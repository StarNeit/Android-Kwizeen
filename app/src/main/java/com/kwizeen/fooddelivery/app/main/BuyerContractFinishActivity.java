package com.kwizeen.fooddelivery.app.main;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kwizeen.fooddelivery.app.models.LoginCredential;
import com.kwizeen.fooddelivery.app.utils.CommonFunc;

public class BuyerContractFinishActivity extends AppCompatActivity  implements View.OnClickListener{
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_contract_finish);
        mContext = this;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnBack){
            CommonFunc.NavigateActivity(mContext, BuyerPickFoodActivity.class);
        }
    }

    @Override
    public void onBackPressed(){
        CommonFunc.NavigateActivity(mContext, BuyerPickFoodActivity.class);
    }
}
