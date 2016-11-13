package com.kwizeen.fooddelivery.app.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kwizeen.fooddelivery.app.main.R;
import com.kwizeen.fooddelivery.app.utils.CommonFunc;

public class SellerFinishContractActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_contract_finish);

        ((Button)findViewById(R.id.btnBackToHome)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonFunc.NavigateActivity(SellerFinishContractActivity.this, HomeActivity.class);
            }
        });

        /*((ImageView)findViewById(R.id.imgShowSettings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonFunc.NavigateActivityWithOutFinish(SellerFinishContractActivity.this, PassengerSettingsActivity.class);
            }
        });*/
    }
    @Override
    public void onBackPressed(){
        CommonFunc.NavigateActivity(this, HomeActivity.class);
    }
}
