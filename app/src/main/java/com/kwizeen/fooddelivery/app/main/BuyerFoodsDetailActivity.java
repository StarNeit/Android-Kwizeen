package com.kwizeen.fooddelivery.app.main;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.app.ProgressDialog;
import android.content.Context;

import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpFunc;
import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpHandler;
import com.kwizeen.fooddelivery.app.utils.CommonFunc;
import com.kwizeen.fooddelivery.app.utils.Global;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import java.net.URL;

public class BuyerFoodsDetailActivity extends AppCompatActivity {

    private TextView foodName, foodPrice, foodnutlv, foodaddress, foodtime;
    private Context mContext;
    private Button butAddToCart;
    ProgressDialog progressDialog;
    ImageView imageView;
    String imageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_foods_detail);

        mContext = this;

        foodName = (TextView)findViewById(R.id.txtFoodName);
        foodPrice = (TextView)findViewById(R.id.txtFoodPrice);
        foodnutlv = (TextView)findViewById(R.id.txtFoodNutLv);
        foodaddress = (TextView)findViewById(R.id.txtFoodAddress);
        foodtime = (TextView)findViewById(R.id.txtFoodTime);

        foodName.setText(Global.g_Buyer.getFoodInfo().getFood_name());
        foodPrice.setText(Global.g_Buyer.getFoodInfo().getFood_price() + "");
        foodnutlv.setText(Global.g_Buyer.getFoodInfo().getFood_nutlv() + "");
        foodaddress.setText(Global.g_Buyer.getFoodInfo().getFood_address());
        foodtime.setText(Global.g_Buyer.getFoodInfo().getFood_promise_time());

        imageView = (ImageView)findViewById(R.id.imgFood);
        imageName = Global.g_Buyer.getFoodInfo().getFood_image();
        new DownloadImageTask().execute();
        //Global.g_Buyer.getFoodInfo().getFood_id(), Global.g_Buyer.getFoodInfo().getFood_image() , Global.g_Buyer.getFoodInfo().getFood_lat(), Global.g_Buyer.getFoodInfo().getFood_lon()
    }

    public void doAddToCart(View view)
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (progressDialog == null) {
                            progressDialog = CommonFunc.createProgressDialog(mContext);
                        }
                        progressDialog.show();

                        new CCHttpHandler() {
                            //Send method
                            @Override
                            public HttpURLConnection getHttpRequestMethod() {
                                //http://localhost/kwizeen/service/buyer/addfoodtomylist?foods_id=1&buyer_id=3
                                String urlParameters = "buyer_id=" + Global.g_Buyer.getBuyer_id() + "&foods_id=" + Global.g_Buyer.getFoodInfo().getFood_id();
                                CommonFunc.AppLog(urlParameters);
                                return CCHttpFunc.PostHttpRequestMethod(Global.BUYER_ADDFOODTOMYLIST_URL, urlParameters);
                            }

                            //Receive method
                            @Override
                            public void onResponse(String result) {
                                //close progress dialog
                                progressDialog.dismiss();
                                CommonFunc.AppLog(result);

                                if (result.equals("")) {
                                    CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Error, Check your Internet", null);//If response string is empty, show error message
                                    return;
                                }
                                try {
                                    JSONObject jsonResultObj = new JSONObject(result);                          //Convert json string to json object
                                    String resultCode = jsonResultObj.getString(Global.RESULTCODE_TAG);         //get result code from json response
                                    if (resultCode.equals(Global.JSON_REULST_ERROR)) {                            //if result code is error code, show error message
                                        CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "This Food Doesn' Exist any more", null);
                                        return;
                                    }
                                    if (resultCode.equals(Global.JSON_REULST_EMAIL_DUPLICATE)) {
                                        CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Already Added to Cart", null);
                                        return;
                                    }
                                    if (resultCode.equals(Global.JSON_RESULT_OK)) {
                                        /*Global.g_Seller.setFoodInfo(new Food());
                                        Global.g_Seller.getFoodInfo().setBuyerInfo(new Buyer());*/
                                        CommonFunc.NavigateActivity(mContext, BuyerMyCartActivity.class);

                                        //"contract_id":"15","buyer_id":"5","foods_id":"25","contract_status":"2","foods_lat":"13.866143794866916",
                                        // "foods_lon":"17.7426677942276","foods_promise_time":"2016-01-18 17:09:00","foods_address":"Batha-Ouest ",
                                        // "foods_name":"fdasfdsa","foods_nutlv":"32432","foods_price":"432432","foods_image":"","foods_seller_id":"7"
                                        // ,"foods_premium":"0","foods_regdate":"2016-01-18 17:11:11","foods_enabled":"1","foods_deleted":"0","foods_ordernum":"0"
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.execute();
                        dialog.dismiss();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Are you sure to Add to Cart?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    class DownloadImageTask extends AsyncTask<Void, Integer, Long> {

        Bitmap bitmap = null;
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(BuyerFoodsDetailActivity.this, "Downloading Image", "Please wait...",true,true);
        }

        protected Long doInBackground(Void... urls) {
            Long a = new Long(10);

            try {
                URL url = new URL("http://guglusharma.com/kwizeen/images/" + imageName);
                bitmap = BitmapFactory.decodeStream(url.openConnection()
                        .getInputStream());
            } catch (Exception E) {
                //Log.e("a","error");
                //CommonFunc.ShowAlertDialog(this, "Kwizeen", "Image Load Failed", null);
            }
            return a;
        }

        protected void onPostExecute(Long result) {
            loading.dismiss();
            if(bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void onBackPressed(){
        CommonFunc.NavigateActivity(mContext, BuyerPickFoodActivity.class);
    }
}
