package com.markelys.viewpay;

/**
 * Created by Herbert TOMBO on 02/02/2018.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import static com.markelys.viewpay.ViewPayConstants.ERROR;
import static com.markelys.viewpay.ViewPayConstants.HIDE_BTN_CLOSE;
import static com.markelys.viewpay.ViewPayConstants.SHOW_BTN_CLOSE;
import static com.markelys.viewpay.ViewPayConstants.SUCCES;

public class  ViewPayActivity extends Activity {

    protected ViewPayDataManager data;

    private LinearLayout closeContainer;
    private LinearLayout popup;
    private Button viewpage;
    private Button btnAbandonner;
    private Button btnContinuer;

    boolean isCompleted =false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpay_layout);
        data = ViewPayDataManager.getInstance();
        WebView viewpayView = findViewById(R.id.content);

        viewpayView.setWebChromeClient(new WebChromeClient());
        viewpayView.getSettings().setJavaScriptEnabled(true);
        viewpayView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        viewpayView.clearCache(true);
        WebChromeClientCustomPoster chromeClient = new WebChromeClientCustomPoster();
        viewpayView.setWebChromeClient(chromeClient);
        viewpayView.addJavascriptInterface(new ViewpayWebInterface(), "Android");
        viewpayView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
                                        String failingUrl) {
                // Handle the error
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            viewpayView.loadUrl(extras.getString("url"));
        }



        popup= findViewById(R.id.popup);

        closeContainer= findViewById(R.id.closeContainer);
        closeContainer.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {
                //showConfirmationPopup();
                popup.setVisibility(View.VISIBLE);
            }
        });

        viewpage = findViewById(R.id.viewpage);
        viewpage.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {
                ViewPayEventsListener view = (ViewPayEventsListener)data.getAppContext();
                view.completeAdsVP();
                finish();
            }
        });

        btnAbandonner = findViewById(R.id.btnAbandonner);
        btnAbandonner.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.setVisibility(View.GONE);
                isCompleted=false;
                ViewPayEventsListener vp = (ViewPayEventsListener)data.getAppContext();
                vp.closeAdsVP();
                finish();
            }
        });

        btnContinuer = findViewById(R.id.btnContinuer);
        btnContinuer.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public void onBackPressed() {
        if(!isCompleted){
            popup.setVisibility(View.VISIBLE);
        }else{
            ViewPayEventsListener view = (ViewPayEventsListener)data.getAppContext();
            view.completeAdsVP();
            finish();
        }

    }

    private class ViewpayWebInterface {

        final Handler _handler = new Handler();
        String message="";

        ViewpayWebInterface() {
        }

        @JavascriptInterface
        public void sendMessageVP(String msg) {
            message = msg;
            _handler.post(_onUpdateUi);
        }

        @JavascriptInterface
        public void openOnBrowser(final String url) {

            Runnable onOpenBrowser= new Runnable() {
                public void run() {
                    ViewPayEventsListener vp = (ViewPayEventsListener)data.getAppContext();
                    vp.completeAdsVP();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    ViewPayActivity.this.startActivity(browserIntent);
                    finish();
                }
            };
            _handler.post(onOpenBrowser);
        }

        final Runnable _onUpdateUi= new Runnable() {
            public void run() {
                updateUI();
            }
        };

        private void updateUI() {
            ViewPayEventsListener vp = (ViewPayEventsListener)data.getAppContext();

            if(message.toLowerCase().equals(HIDE_BTN_CLOSE)){
                closeContainer.setVisibility(View.GONE);
            }else if(message.toLowerCase().equals(SHOW_BTN_CLOSE)){
                closeContainer.setVisibility(View.VISIBLE);
            }else if(message.toLowerCase().equals(SUCCES)){
                if(!TextUtils.isEmpty(data.getAccessMessage()))
                    viewpage.setText(data.getAccessMessage());
                viewpage.setVisibility(View.VISIBLE);
                vp.completeAdsVP();
                isCompleted = true;
            }else if(message.toLowerCase().equals(ERROR)){
                vp.errorVP();
                finish();
            }
        }
    }

    private class WebChromeClientCustomPoster extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public Bitmap getDefaultVideoPoster() {
            return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        }

        @Override
        public View getVideoLoadingProgressView() {
            return super.getVideoLoadingProgressView();

        }
    }
}
