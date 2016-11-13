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
import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpHandler;
import com.kwizeen.fooddelivery.app.models.Buyer;
import com.kwizeen.fooddelivery.app.models.LoginCredential;
import com.kwizeen.fooddelivery.app.models.Seller;
import com.kwizeen.fooddelivery.app.models.Food;
import com.kwizeen.fooddelivery.app.utils.CommonFunc;
import com.kwizeen.fooddelivery.app.utils.Global;
import com.kwizeen.fooddelivery.app.utils.SharedPreferencesTool;
import com.kwizeen.fooddelivery.app.utils.SimpleAlertDialogListener;
import com.kwizeen.fooddelivery.app.main.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class BuyerLoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editEmail, editPassword;
    private String eMail, password;
    private LoginCredential mLC;

    private Context mContext;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);

        mContext = this;

        editEmail = (EditText)findViewById(R.id.editEmail);
        editPassword = (EditText)findViewById(R.id.editPassword);

        ((Button)findViewById(R.id.btnLogIn)).setOnClickListener(this);
        ((TextView)findViewById(R.id.txtShowDriverSignupActivity)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
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
            BuyerLogin();
        }
        if(v.getId() == R.id.txtShowDriverSignupActivity){
            CommonFunc.NavigateActivity(mContext, BuyerSignUpActivity.class);
        }
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
                        CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Password is Wrong", null);
                        return;
                    }
                    if (resultCode.equals(Global.JSON_RESULT_OK)) {
                        JSONObject driverObj = jsonResultObj.getJSONObject(Global.BUYER_TAG);

                        int seller_id = driverObj.getInt(Global.BUYER_ID_TAG);
                        String seller_name = driverObj.getString(Global.BUYER_NAME_TAG);
                        String seller_email = driverObj.getString(Global.BUYER_EMAIL_TAG);
                        String seller_phone = driverObj.getString(Global.BUYER_PHONE_TAG);
                        double seller_lat = driverObj.getDouble(Global.BUYER_LAT_TAG);
                        double seller_lon = driverObj.getDouble(Global.BUYER_LON_TAG);
                        int seller_enabled = driverObj.getInt(Global.BUYER_ENABLED_TAG);
                        int seller_deleted = driverObj.getInt(Global.BUYER_DELETED_TAG);

                        Global.g_Buyer = new Buyer();
                        Global.g_Buyer.setBuyer_id(seller_id);
                        Global.g_Buyer.setBuyer_name(seller_name);
                        Global.g_Buyer.setBuyer_email(seller_email);
                        Global.g_Buyer.setBuyerPhone(seller_phone);
                        Global.g_Buyer.setBuyer_lat(seller_lat);
                        Global.g_Buyer.setBuyer_lon(seller_lon);

                        if (seller_enabled != Global.STATUS_ENABLED) {
                            CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Your account has been Suspended", new SimpleAlertDialogListener(mContext, BuyerSignUpActivity.class));
                            return;
                        }
                        if (seller_deleted != Global.STATUS_NOT_DELETED) {
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
        CommonFunc.NavigateActivity(mContext, HomeActivity.class);
    }
}
