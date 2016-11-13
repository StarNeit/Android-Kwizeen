package com.kwizeen.fooddelivery.app.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.kwizeen.fooddelivery.app.main.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import android.content.Intent;
import android.net.Uri;
import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpFunc;
import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpHandler;
import com.kwizeen.fooddelivery.app.models.Buyer;
import com.kwizeen.fooddelivery.app.models.LoginCredential;
import com.kwizeen.fooddelivery.app.models.Seller;
import com.kwizeen.fooddelivery.app.models.Food;
import com.kwizeen.fooddelivery.app.utils.SimpleAlertDialogListener;
import com.kwizeen.fooddelivery.app.utils.CommonFunc;
import com.kwizeen.fooddelivery.app.utils.Global;
import com.kwizeen.fooddelivery.app.utils.SharedPreferencesTool;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private Context mContext;
    private LoginCredential mLC;

    private int buyerType;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mContext = this;

        /*ImageView imgPostFoodButton = (ImageView)findViewById(R.id.imgPostFoodButton);
        imgPostFoodButton.setOnClickListener(this);

        ImageView imgGetFoodButton = (ImageView)findViewById(R.id.imgGetFoodButton);
        imgGetFoodButton.setOnClickListener(this);*/

        Button imgPostFoodButton = (Button)findViewById(R.id.imgPostFoodButton);
        imgPostFoodButton.setOnClickListener(this);
        Button imgGetFoodButton = (Button)findViewById(R.id.imgGetFoodButton);
        imgGetFoodButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.imgPostFoodButton) {
            SharedPreferencesTool sharedPreferencesTool = new SharedPreferencesTool(mContext);
            mLC = sharedPreferencesTool.GetLoginCredentials(SharedPreferencesTool.SELLER);
            //mLC = new LoginCredential("", "");
            if (!mLC.getEmail().equals(""))
            {
                SellerLogIn();
            } else {
                CommonFunc.NavigateActivity(mContext, SellerSignUpActivity.class);
            }
        }
        if(v.getId() == R.id.imgGetFoodButton) {
            Global.g_buyerType = Global.VOLUNTEER_BUYER;
            SharedPreferencesTool sharedPreferencesTool = new SharedPreferencesTool(mContext);
            mLC = sharedPreferencesTool.GetLoginCredentials(SharedPreferencesTool.BUYER);
            //mLC = new LoginCredential("", "");
            if (!mLC.getEmail().equals("")) {
                BuyerLogin();
            } else {
                CommonFunc.NavigateActivity(mContext, BuyerSignUpActivity.class);
            }
        }
    }

    public void SellerLogIn(){
        progressDialog = CommonFunc.createProgressDialog(this);
        progressDialog.show();
        new CCHttpHandler(){

            //Send method
            @Override
            public HttpURLConnection getHttpRequestMethod() {
                String urlParameters = "email=" + mLC.getEmail() + "&password=" + CommonFunc.GetMD5String(mLC.getPassword());
                CommonFunc.AppLog(urlParameters);
                return CCHttpFunc.PostHttpRequestMethod(Global.SELLER_LOGIN_URL, urlParameters);
            }

            //Receive method
            @Override
            public void onResponse(String result) {
                //close progress dialog
                progressDialog.dismiss();

                CommonFunc.AppLog(result);
                if(result.equals("")){                                                          //If response string is empty, show error message
                    CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Failed. Check Your Internet Connection", null);
                    return;
                }
                try {
                    JSONObject jsonResultObj = new JSONObject(result);                          //Convert json string to json object
                    String resultCode = jsonResultObj.getString(Global.RESULTCODE_TAG);         //get result code from json response
                    if(resultCode.equals(Global.JSON_REULST_ERROR)){                            //if result code is error code, show error message
                        CommonFunc.NavigateActivity(mContext, SellerSignUpActivity.class);
                        return;
                    }
                    if(resultCode.equals(Global.JSON_RESULT_OK)){
                        JSONObject sellerObj = jsonResultObj.getJSONObject(Global.SELLER_TAG);

                        int seller_id = sellerObj.getInt(Global.SELLER_ID_TAG);
                        String seller_name = sellerObj.getString(Global.SELLER_NAME_TAG);
                        String seller_email = sellerObj.getString(Global.SELLER_EMAIL_TAG);
                        double seller_lat = sellerObj.getDouble(Global.SELLER_LAT_TAG);
                        double seller_lon = sellerObj.getDouble(Global.SELLER_LON_TAG);
                        String seller_phone = sellerObj.getString(Global.SELLER_PHONE_TAG);
                        String seller_address = sellerObj.getString(Global.SELLER_ADDRESS_TAG);
                        String seller_regdate = sellerObj.getString(Global.SELLER_REGDATE_TAG);
                        int seller_enabled = sellerObj.getInt(Global.SELLER_ENABLED_TAG);
                        int seller_delete = sellerObj.getInt(Global.SELLER_DELETED_TAG);

                        if(seller_enabled != Global.STATUS_ENABLED){
                            CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Your account has been Suspended", new SimpleAlertDialogListener(mContext, SellerSignUpActivity.class));
                            return;
                        }
                        if(seller_delete != Global.STATUS_NOT_DELETED){
                            CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Your accont has been Deleted", new SimpleAlertDialogListener(mContext, SellerSignUpActivity.class));
                            return;
                        }

                        Global.g_Seller = new Seller();
                        Global.g_Seller.setSeller_id(seller_id);
                        Global.g_Seller.setSeller_name(seller_name);
                        Global.g_Seller.setSeller_email(seller_email);
                        Global.g_Seller.setSeller_lat(seller_lat);
                        Global.g_Seller.setSeller_lon(seller_lon);
                        Global.g_Seller.setSeller_phone(seller_phone);
                        Global.g_Seller.setSeller_address(seller_address);
                        Global.g_Seller.setSeller_regdate(seller_regdate);


                        SharedPreferencesTool sharedPreferencesTool = new SharedPreferencesTool(mContext);
                        sharedPreferencesTool.SaveCredentials(mLC, SharedPreferencesTool.SELLER);


                        Global.g_Seller.setFoodInfo(new Food());
                        Global.g_Seller.getFoodInfo().setBuyerInfo(new Buyer());
                        CommonFunc.NavigateActivity(mContext, SellerCreateContractActivity.class);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void BuyerLogin(){
        progressDialog = CommonFunc.createProgressDialog(this);
        progressDialog.show();
        new CCHttpHandler() {

            //Send method
            @Override
            public HttpURLConnection getHttpRequestMethod() {
                //http://localhost/kwizeen/service/Buyer/login?email=aaa&password=e10adc3949ba59abbe56e057f20f883e
                String urlParameters = "email=" + mLC.getEmail() + "&password=" + CommonFunc.GetMD5String(mLC.getPassword());
                CommonFunc.AppLog(urlParameters);
                return CCHttpFunc.PostHttpRequestMethod(Global.BUYER_LOGIN_URL, urlParameters);
            }

            //Receive method
            @Override
            public void onResponse(String result) {
                //close progress dialog
                progressDialog.dismiss();

                CommonFunc.AppLog(result);
                if (result.equals("")) {                                                          //If response string is empty, show error message
                    CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Failed. Check Your Internet Connection", null);
                    return;
                }
                try {
                    JSONObject jsonResultObj = new JSONObject(result);                          //Convert json string to json object
                    String resultCode = jsonResultObj.getString(Global.RESULTCODE_TAG);         //get result code from json response
                    if (resultCode.equals(Global.JSON_REULST_ERROR)) {                            //if result code is error code, show error message
                        CommonFunc.NavigateActivity(mContext, BuyerLoginActivity.class);
                        return;
                    }
                    if (resultCode.equals(Global.JSON_RESULT_OK)) {
                        JSONObject buyerObj = jsonResultObj.getJSONObject(Global.BUYER_TAG);

                        //"buyer_id":"5","buyer_name":"www","buyer_email":"www","buyer_password":
                        // "4eae35f1b35977a00ebd8086c259d4c9","buyer_phone":"999","buyer_lat":"0",
                        // "buyer_lon":"0","buyer_regdate":"2016-01-17 20:18:13","buyer_enabled":"1"
                        // ,"buyer_deleted":"0"

                        int buyer_id = buyerObj.getInt(Global.BUYER_ID_TAG);
                        String buyer_name = buyerObj.getString(Global.BUYER_NAME_TAG);
                        String buyer_email = buyerObj.getString(Global.BUYER_EMAIL_TAG);
                        String buyer_phone = buyerObj.getString(Global.BUYER_PHONE_TAG);
                        double buyer_lat = buyerObj.getDouble(Global.BUYER_LAT_TAG);
                        double buyer_lon = buyerObj.getDouble(Global.BUYER_LON_TAG);
                        int buyer_enabled = buyerObj.getInt(Global.BUYER_ENABLED_TAG);
                        int buyer_deleted = buyerObj.getInt(Global.BUYER_DELETED_TAG);

                        Global.g_Buyer = new Buyer();
                        Global.g_Buyer.setBuyer_id(buyer_id);
                        Global.g_Buyer.setBuyer_name(buyer_name);
                        Global.g_Buyer.setBuyer_email(buyer_email);
                        Global.g_Buyer.setBuyerPhone(buyer_phone);
                        Global.g_Buyer.setBuyer_lat(buyer_lat);
                        Global.g_Buyer.setBuyer_lon(buyer_lon);

                        if (buyer_enabled != Global.STATUS_ENABLED) {
                            CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Your account has been Suspended", new SimpleAlertDialogListener(mContext, BuyerSignUpActivity.class));
                            return;
                        }
                        if (buyer_deleted != Global.STATUS_NOT_DELETED) {
                            CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Your accont has been Deleted", new SimpleAlertDialogListener(mContext, BuyerSignUpActivity.class));
                            return;
                        }

                        SharedPreferencesTool sharedPreferencesTool = new SharedPreferencesTool(mContext);
                        sharedPreferencesTool.SaveCredentials(mLC, SharedPreferencesTool.BUYER);

                        Global.g_Buyer.setFoodInfo(new Food());
                        Global.g_Buyer.getFoodInfo().setSellerInfo(new Seller());
                        CommonFunc.NavigateActivity(mContext, BuyerPickFoodActivity.class);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    @Override
    public void onBackPressed(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Global.g_Buyer = new Buyer();
                        Global.g_Seller = new Seller();
                        System.exit(1);
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Are you sure to Exit?").setPositiveButton("YES", dialogClickListener)
                .setNegativeButton("NO", dialogClickListener).show();
    }
}