package com.kwizeen.fooddelivery.app.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kwizeen.fooddelivery.app.utils.LocationFinder;
import com.kwizeen.fooddelivery.app.main.R;
import com.kwizeen.fooddelivery.app.models.Buyer;
import com.kwizeen.fooddelivery.app.models.Seller;
import com.kwizeen.fooddelivery.app.utils.CommonFunc;
import com.kwizeen.fooddelivery.app.utils.Global;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Global.g_Seller = new Seller();
        Global.g_Buyer = new Buyer();

        Intent locationServiceInent = new Intent(this, LocationFinder.class);
        startService(locationServiceInent);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                CommonFunc.NavigateActivity(SplashActivity.this, HomeActivity.class);
            }
        }).start();
    }

}