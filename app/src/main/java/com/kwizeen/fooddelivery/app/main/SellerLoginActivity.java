package com.kwizeen.fooddelivery.app.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kwizeen.fooddelivery.app.main.R;

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


import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class SellerLoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editEmail, editPassword;
    private String eMail, password;
    private LoginCredential mLC;

    private Context mContext;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_login);

        mContext = this;

        editEmail = (EditText)findViewById(R.id.editEmail);
        editPassword = (EditText)findViewById(R.id.editPassword);

        ((Button)findViewById(R.id.btnLogIn)).setOnClickListener(this);
        ((TextView)findViewById(R.id.txtShowSellerSignupActivity)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.txtShowSellerSignupActivity){
            CommonFunc.NavigateActivity(mContext, SellerSignUpActivity.class);
        }
        if(v.getId() == R.id.btnLogIn){
            eMail = editEmail.getText().toString().trim();
            if(eMail.equals("")){
                CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Email is required", null);
                return;
            }
            password = editPassword.getText().toString();
            if(password.equals("")){
                CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Password is required", null);
                return;
            }
            mLC = new LoginCredential(eMail, password);
            SellerLogIn();
        }
    }

    public void SellerLogIn() {
        progressDialog = CommonFunc.createProgressDialog(this);
        progressDialog.show();
        new CCHttpHandler() {

            //Send method
            @Override
            public HttpURLConnection getHttpRequestMethod() {
                // 	http://localhost/kwizeen/service/Seller/Login?email=seller.1@mail.com&password=23fdak23j2di32
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
                if (result.equals("")) {                                                          //If response string is empty, show error message
                    CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Failed. Check Your Internet Connection", null);
                    return;
                }
                try {
                    JSONObject jsonResultObj = new JSONObject(result);                          //Convert json string to json object
                    String resultCode = jsonResultObj.getString(Global.RESULTCODE_TAG);         //get result code from json response
                    if (resultCode.equals(Global.JSON_REULST_ERROR)) {                            //if result code is error code, show error message
                        CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Password is Wrong", null);
                        return;
                    }
                    if (resultCode.equals(Global.JSON_RESULT_OK)) {
                        JSONObject userObj = jsonResultObj.getJSONObject(Global.SELLER_TAG);

                        int seller_id = userObj.getInt(Global.SELLER_ID_TAG);
                        String seller_name = userObj.getString(Global.SELLER_NAME_TAG);
                        String seller_email = userObj.getString(Global.SELLER_EMAIL_TAG);
                        double seller_lat = userObj.getDouble(Global.SELLER_LAT_TAG);
                        double seller_lon = userObj.getDouble(Global.SELLER_LON_TAG);
                        String seller_phone = userObj.getString(Global.SELLER_PHONE_TAG);
                        String seller_address = userObj.getString(Global.SELLER_ADDRESS_TAG);
                        String seller_regdate = userObj.getString(Global.SELLER_REGDATE_TAG);
                        int seller_enabled = userObj.getInt(Global.SELLER_ENABLED_TAG);
                        int seller_delete = userObj.getInt(Global.SELLER_DELETED_TAG);

                        if (seller_enabled != Global.STATUS_ENABLED) {
                            CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Your account has been Suspended", new SimpleAlertDialogListener(mContext, SellerSignUpActivity.class));
                            return;
                        }
                        if (seller_delete != Global.STATUS_NOT_DELETED) {
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
    @Override
    public void onBackPressed(){
        CommonFunc.NavigateActivity(mContext, HomeActivity.class);
    }
}
