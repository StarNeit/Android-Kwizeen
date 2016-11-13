package com.kwizeen.fooddelivery.app.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpFunc;
import com.kwizeen.fooddelivery.app.models.LoginCredential;
import com.kwizeen.fooddelivery.app.models.Food;
import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpHandler;
import com.kwizeen.fooddelivery.app.main.R;
import com.kwizeen.fooddelivery.app.models.Buyer;
import com.kwizeen.fooddelivery.app.models.Seller;
import com.kwizeen.fooddelivery.app.utils.CommonFunc;
import com.kwizeen.fooddelivery.app.utils.Global;
import com.kwizeen.fooddelivery.app.utils.SharedPreferencesTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class BuyerSignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private String fullName, eMail, password, phoneNumber, driverLicenseCard, paypal;
    private EditText editFullName, editEmail, editPassword, editVerifyPassword, editPhoneNumber;

    private Context mContext;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_sign_up);

        mContext = this;

        editFullName = (EditText)findViewById(R.id.editFullName);
        editEmail = (EditText)findViewById(R.id.editEmail);
        editPassword = (EditText)findViewById(R.id.editPassword);
        editVerifyPassword = (EditText)findViewById(R.id.editVerifyPassword);
        editPhoneNumber = (EditText)findViewById(R.id.editPhoneNumber);

        ((Button)findViewById(R.id.btnSignUp)).setOnClickListener(this);
        ((TextView)findViewById(R.id.txtShowBuyerLoginActivity)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnSignUp){
            fullName = editFullName.getText().toString().trim();
            if(fullName.equals("")) {
                CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Name is required", null);
                return;
            }
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
            String verifyPassword = editVerifyPassword.getText().toString();
            if(verifyPassword.equals("")){
                CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Password is required", null);
                return;
            }
            if(!password.equals(verifyPassword)){
                CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Password doesn't match", null);
                return;
            }
            phoneNumber = editPhoneNumber.getText().toString().trim();
            if(phoneNumber.equals("")) {
                CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "PhoneNumber is required", null);
                return;
            }

            BuyerSignUp();
        }
        if(v.getId() == R.id.txtShowBuyerLoginActivity){
            CommonFunc.NavigateActivity(mContext, BuyerLoginActivity.class);
        }
    }

    private void BuyerSignUp(){
        progressDialog = CommonFunc.createProgressDialog(this);
        progressDialog.show();
        new CCHttpHandler(){

            //Send method
            @Override
            public HttpURLConnection getHttpRequestMethod() {
                //http://localhost/kwizeen/service/Buyer/RegisterBuyer?name=aba&email=aba&password=e10adc3949ba59abbe56e057f20f883e&phone=aaa
                String urlParameters = "name=" + fullName + "&email=" + eMail.toLowerCase() + "&password=" + CommonFunc.GetMD5String(password)
                        + "&phone=" + phoneNumber;
                CommonFunc.AppLog(urlParameters);
                return CCHttpFunc.PostHttpRequestMethod(Global.BUYER_REGISTER_URL, urlParameters);
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
                        CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Register Failed", null);
                        return;
                    }else if(resultCode.equals(Global.JSON_REULST_EMAIL_DUPLICATE)){
                        CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Your E-mail Address Already Exist", null);
                        return;
                    }
                    if(resultCode.equals(Global.JSON_RESULT_OK)){
                        JSONObject driverObj = jsonResultObj.getJSONObject(Global.BUYER_TAG);

                        int buyer_id = driverObj.getInt(Global.BUYER_ID_TAG);
                        String buyer_name = driverObj.getString(Global.BUYER_NAME_TAG);
                        String buyer_email = driverObj.getString(Global.BUYER_EMAIL_TAG);
                        String buyer_phone = driverObj.getString(Global.BUYER_PHONE_TAG);
                        double buyer_lat = driverObj.getDouble(Global.BUYER_LAT_TAG);
                        double buyer_lon = driverObj.getDouble(Global.BUYER_LON_TAG);
                        int buyer_enabled = driverObj.getInt(Global.BUYER_ENABLED_TAG);
                        int buyer_deleted = driverObj.getInt(Global.BUYER_DELETED_TAG);

                        Global.g_Buyer = new Buyer();
                        Global.g_Buyer.setBuyer_id(buyer_id);
                        Global.g_Buyer.setBuyer_name(buyer_name);
                        Global.g_Buyer.setBuyer_email(buyer_email);
                        Global.g_Buyer.setBuyerPhone(buyer_phone);
                        Global.g_Buyer.setBuyer_lat(buyer_lat);
                        Global.g_Buyer.setBuyer_lon(buyer_lon);

                        Food food = new Food();
                        Seller seller = new Seller();
                        food.setSellerInfo(seller);
                        Global.g_Buyer.setFoodInfo(food);

                        SharedPreferencesTool sharedPreferencesTool = new SharedPreferencesTool(mContext);
                        LoginCredential lc = new LoginCredential(eMail, password);
                        sharedPreferencesTool.SaveCredentials(lc, SharedPreferencesTool.BUYER);

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
        CommonFunc.NavigateActivity(mContext, HomeActivity.class);
    }
}
