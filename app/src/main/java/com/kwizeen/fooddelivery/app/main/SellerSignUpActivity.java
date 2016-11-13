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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

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

public class SellerSignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private String fullName, eMail, password, phoneNumber, address;
    private EditText editFullName, editEmail, editPassword, editVerifyPassword, editPhoneNumber, editAddress;

    private Context mContext;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_sign_up);

        mContext = this;

        editFullName = (EditText)findViewById(R.id.editFullName);
        editEmail = (EditText)findViewById(R.id.editEmail);
        editPassword = (EditText)findViewById(R.id.editPassword);
        editVerifyPassword = (EditText)findViewById(R.id.editVerifyPassword);
        editPhoneNumber = (EditText)findViewById(R.id.editPhoneNumber);
        editAddress = (EditText)findViewById(R.id.editAddress);

        ((Button)findViewById(R.id.btnSignUp)).setOnClickListener(this);
        ((TextView)findViewById(R.id.txtShowSellerLoginActivity)).setOnClickListener(this);
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
            if(phoneNumber.equals("")){
                CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "PhoneNumber is required", null);
                return;
            }
            address = editAddress.getText().toString().trim();
            if(address.equals("")){
                CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Address is required", null);
                return;
            }

            SellerSignUp();
        }
        if(v.getId() == R.id.txtShowSellerLoginActivity){
            CommonFunc.NavigateActivity(mContext, SellerLoginActivity.class);
        }
    }

    private void SellerSignUp(){
        progressDialog = CommonFunc.createProgressDialog(this);
        progressDialog.show();
        new CCHttpHandler(){

            //Send method
            @Override
            public HttpURLConnection getHttpRequestMethod() {
                // 	http://localhost/kwizeen/service/Seller/RegisterSeller?email=seller.1@mail.com&password=123&password=23fdak23j2di32&phone=232323&address=awjdj&name=aaasda
                String urlParameters = "name=" + fullName + "&email=" + eMail.toLowerCase() + "&password=" + CommonFunc.GetMD5String(password)
                        + "&phone=" + phoneNumber + "&address=" + address;
                CommonFunc.AppLog(urlParameters);
                return CCHttpFunc.PostHttpRequestMethod(Global.SELLER_REGISTER_URL, urlParameters);
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

                        Food food = new Food();
                        Buyer buyer = new Buyer();
                        food.setBuyerInfo(buyer);
                        Global.g_Seller.setFoodInfo(food);

                        SharedPreferencesTool sharedPreferencesTool = new SharedPreferencesTool(mContext);
                        LoginCredential lc = new LoginCredential(eMail, password);
                        sharedPreferencesTool.SaveCredentials(lc, SharedPreferencesTool.SELLER);

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
