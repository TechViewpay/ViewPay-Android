package com.markelys.viewpaytest;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.markelys.viewpay.ViewPay;
import com.markelys.viewpay.ViewPayEventsListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements ViewPayEventsListener {
    Button showads;
    Button checkVideo;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();

        ViewPay.init(MainActivity.this, "b23d3f0235ae89e4");
        showads = findViewById(R.id.showAds);
        checkVideo = findViewById(R.id.checkVideo);
        result = findViewById(R.id.result);
        checkVideo.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                ViewPay.checkVideo();
            }
        });
        showads.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                ViewPay.presentAd();
            }
        });
    }

    @Override
    public void checkVideoSuccesVP() {
        showads.setVisibility(View.VISIBLE);
        result.setText("checkVideoSuccesVP");
    }

    @Override
    public void checkVideoErrorVP() {
        result.setText("checkVideoErrorVP");
    }

    @Override
    public void errorVP() {
        result.setText("errorVP");
    }

    @Override
    public void closeAdsVP() {
        result.setText("closeAdsVP");
    }

    @Override
    public void completeAdsVP() {
        result.setText("completeAdsVP");
    }

    String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            int result;
            List<String> listPermissionsNeeded = new ArrayList<>();
            for (String p : permissions) {
                result = ContextCompat.checkSelfPermission(this, p);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p);
                }
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
                return false;
            }
            return true;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
