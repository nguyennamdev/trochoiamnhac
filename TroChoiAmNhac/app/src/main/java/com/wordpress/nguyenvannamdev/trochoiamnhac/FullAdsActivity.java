package com.wordpress.nguyenvannamdev.trochoiamnhac;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class FullAdsActivity extends AppCompatActivity {

    private static final String TAG = "FullAdsActivity";
    private InterstitialAd interstitialAd;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_ads);
        MobileAds.initialize(this, "ca-app-pub-8486025614150503~8511203671");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tải......");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.ad_unit_full));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.i(TAG, "onAdLoaded: ");
                progressDialog.dismiss();
                interstitialAd.show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();

                Log.i(TAG, "onAdOpened: ");
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                Log.i(TAG, "onAdLeftApplication: ");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.i(TAG, "onAdFailedToLoad: ");
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.i(TAG, "onAdClosed: ");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("hint_return", 1);
                startActivity(intent);
                finish();
                Toast.makeText(getApplicationContext(), "Bạn đã được thêm 1 lần gợi ý", Toast.LENGTH_LONG).show();
            }
        });

        adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
    }
}
