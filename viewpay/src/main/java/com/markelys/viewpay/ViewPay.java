package com.markelys.viewpay;



import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;

import static com.markelys.viewpay.ViewPayConstants.VP_AD_SELECTOR_PATH;
import static com.markelys.viewpay.ViewPayConstants.VP_CHECK_VIDEO_PATH;
import static com.markelys.viewpay.ViewPayConstants.VP_CONFIG_URL;


/**
 * Created by Herbert TOMBO on 24/01/2018.
 */

public class ViewPay {

    static protected ViewPayDataManager data;

    static private Context ctx;
    static final int IS_CHECK_VIDEO = 1;
    static final int IS_ADS = 2;

    public static ViewPay init(Context context,String accountID){
        return new ViewPay(context, accountID);
    }

    private ViewPay(Context context,String accountID){

        ctx = context;

        data = ViewPayDataManager.getInstance();

        data.setAppContext(ctx);

        ViewPayLocationManager viewpayLocationManager = new ViewPayLocationManager(context);

        data.setAccountID(accountID);

        //get device language
        String lang = Locale.getDefault().getDisplayLanguage();

        //get device type
        boolean isTablet = context.getResources().getBoolean(R.bool.is_tablet);
        data.setTablet(isTablet);

        //get android device_id
        String android_id = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
        data.setHostID(android_id);

        getConfigFromJson(VP_CONFIG_URL,accountID);

    }

    public static void setUserGender(String gender){
        data.setGenre(gender);
    }

    public static void setUserAge(int age){
        data.setUserAge(age);
    }

    public static void setCountry(String country){
        data.setCountry(country);
    }

    public static void setLanguage(String lang){
        data.setLanguage(lang);
    }

    public static void setPostalCode(String code){
        data.setPostalCode(code);
    }

    public static void setCategorie(String categorie){
        data.setCategorie(categorie);
    }

    public static void presentAd() {
        Intent viewpayIntent = new Intent (ctx, ViewPayActivity.class);
        Bundle dataBundle = new Bundle();
        dataBundle.putString("url", fullUrl(IS_ADS));
        viewpayIntent.putExtras(dataBundle);
        ctx.startActivity(viewpayIntent);

    }

    public static void checkVideo(){
        checkVideoWS(fullUrl(IS_CHECK_VIDEO));
    }

    private static String fullUrl(int type){
        String param="";

        if(!TextUtils.isEmpty(data.getHostID()))
            param=param+"?hostId="+data.getHostID();

        if(!TextUtils.isEmpty(data.getAccountID()))
            param=param+"&id="+data.getAccountID();

        if(type != IS_CHECK_VIDEO){
            param=param+"&price=0.5&mobile&noInterface";

            if(!TextUtils.isEmpty(data.getCvID()))
                param = param +"&cvid="+data.getCvID();
        }

        if(data.isTablet()) {
            param = param + "&typeDevice=2";
        }else{
            param = param + "&typeDevice=1";
        }

        param = param+"&mobileOS=2&me";

        if(data.getUserAge()>0)
            param = param+"&a="+data.getUserAge();

        if(!TextUtils.isEmpty(data.getGenre()))
            param = param + "&s="+data.getGenre();

        if(!TextUtils.isEmpty(data.getLanguage()))
            param = param + "&language="+ data.getLanguage();

        if(!TextUtils.isEmpty(data.getCountry()))
            param = param +"&country="+data.getCountry();

        if(!TextUtils.isEmpty(data.getLatitude()))
            param = param +"&gps_x="+data.getLatitude();

        if(!TextUtils.isEmpty(data.getLongitude()))
            param = param +"&gps_y="+data.getLongitude();

        if(!TextUtils.isEmpty(data.getPostalCode()))
            param = param +"&postcode="+data.getPostalCode();

        if(!TextUtils.isEmpty(data.getCategorie()))
            param = param +"&c="+data.getCategorie();

        String _fullUrl="";
        if(type != IS_CHECK_VIDEO){
            _fullUrl = data.getServerUrl()+VP_AD_SELECTOR_PATH+param;
            Log.d("adselector url : ",_fullUrl);
        }else{
            _fullUrl = data.getServerUrl()+VP_CHECK_VIDEO_PATH+param;
            Log.d("check video url : ",_fullUrl);
        }
        return _fullUrl;
    }

    private static boolean getConfigFromJson(String _url,String accountId) {
        ViewPayEventsListener vp = (ViewPayEventsListener)ctx;

        if (Integer.parseInt(Build.VERSION.SDK)
                < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        HttpURLConnection urlConnection = null;
        try {
            URL urlToRequest = new URL(_url);
            urlConnection = (HttpURLConnection)urlToRequest.openConnection();
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String result = new Scanner(in).useDelimiter("\\A").next();
            Log.d("WS URL", _url);
            if (result != null) {
                result = result.replace("\n","");
                result = result.replace("\r","");
                Log.d("WS OUTPUT", result);

                try {
                    JSONObject jsonObj = new JSONObject(result);

                    String prod ="";
                    if(jsonObj.has("prod")){
                        prod = jsonObj.getString("prod");
                        if(!prod.contains("http:")){
                            prod = "http://"+prod;
                        }
                        data.setServerUrl(prod);
                    }

                    String preprod="";
                    if(jsonObj.has("preprod")){
                        preprod = jsonObj.getString("preprod");
                        if(!preprod.contains("http:")){
                            preprod = "http://"+preprod;
                        }
                    }

                    JSONArray editorPrepro = new JSONArray();
                    if(jsonObj.has("editorPrepro")){
                        editorPrepro = jsonObj.getJSONArray("editorPrepro");
                        for (int i = 0; i < editorPrepro.length(); i++) {
                            if(accountId.equals(editorPrepro.get(i))){
                                data.setServerUrl(preprod);
                                return true;
                            }
                        }
                    }
                } catch (JSONException e) {
                    return false;
                }
            } else {
                return false;
            }

        } catch (MalformedURLException e) {
            return false;
        } catch (SocketTimeoutException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return false;
    }

    private static void checkVideoWS(String _url) {
        ViewPayEventsListener vp = (ViewPayEventsListener)ctx;

        if (Integer.parseInt(Build.VERSION.SDK)
                < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        HttpURLConnection urlConnection = null;
        try {
            URL urlToRequest = new URL(_url);
            urlConnection = (HttpURLConnection)urlToRequest.openConnection();
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String result = new Scanner(in).useDelimiter("\\A").next();

            if (result != null) {
                result = result.replace("\n","");
                result = result.replace("\r","");
                Log.d("WS OUTPUT", result);

                try {
                    JSONObject jsonObj = new JSONObject(result);

                    int nbVideo = 0;
                    if(jsonObj.has("nbVideo")){
                        nbVideo = jsonObj.getInt("nbVideo");
                    }

                    String freeCamp ="";
                    if(jsonObj.has("freeCamp")){
                        freeCamp = jsonObj.getString("freeCamp");
                    }

                    String cvid="";
                    if(jsonObj.has("cvid")){
                        cvid = jsonObj.getString("cvid");
                        data.setCvID(cvid);
                    }

                    String accessMessage="";
                    if(jsonObj.has("labelValidate")){
                        accessMessage = jsonObj.getString("labelValidate");
                        data.setAccessMessage(accessMessage);
                    }

                    int activeAdex = 0;
                    if(jsonObj.has("activeAdex")){
                        activeAdex = jsonObj.getInt("activeAdex");
                    }

                    if(nbVideo>0 || freeCamp.toLowerCase().equals("ok")){
                        vp.checkVideoSuccesVP();
                    }else{
                        vp.checkVideoErrorVP();
                    }

                } catch (JSONException e) {
                    vp.checkVideoErrorVP();
                }
            } else {
                vp.checkVideoErrorVP();
            }

        } catch (MalformedURLException e) {
            vp.checkVideoErrorVP();
        } catch (SocketTimeoutException e) {
            vp.checkVideoErrorVP();
        } catch (IOException e) {
            vp.checkVideoErrorVP();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

}
