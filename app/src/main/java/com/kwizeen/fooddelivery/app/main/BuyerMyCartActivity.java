package com.kwizeen.fooddelivery.app.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpFunc;
import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpHandler;
import com.kwizeen.fooddelivery.app.adapter.MyCartListAdapter;
import com.kwizeen.fooddelivery.app.models.Buyer;
import com.kwizeen.fooddelivery.app.models.Food;
import com.kwizeen.fooddelivery.app.models.Seller;
import com.kwizeen.fooddelivery.app.utils.CommonFunc;
import com.kwizeen.fooddelivery.app.utils.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class BuyerMyCartActivity extends AppCompatActivity {

    private List<Food> foodList = new ArrayList<>();
    private MyCartListAdapter adapter;
    private ListView listFood;

    ProgressDialog progressDialog;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_my_cart);

        mContext = this;

        listFood = (ListView)findViewById(R.id.listContract);
        adapter = new MyCartListAdapter(this, foodList);
        listFood.setAdapter(adapter);

        GetMyCartList();
    }

    private void GetMyCartList()
    {

        if(progressDialog == null) {
            progressDialog = CommonFunc.createProgressDialog(mContext);
        }
        progressDialog.show();

        new CCHttpHandler(){
            //Send method
            @Override
            public HttpURLConnection getHttpRequestMethod() {
                //http://localhost/kwizeen/service/buyer/getmycartlist?buyer_id=2
                String urlParameters;
                urlParameters = "buyer_id="+Global.g_Buyer.getBuyer_id();
                return CCHttpFunc.PostHttpRequestMethod(Global.BUYER_GET_MYCARTLIST_URL, urlParameters);
            }

            //Receive method
            @Override
            public void onResponse(String result) {
                //close progress dialog
                progressDialog.dismiss();

                CommonFunc.AppLog(result);
                if (result.equals("")) {                                                          //If response string is empty, show error message
                    CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Error. Check Your Internet Connection", null);
                    return;
                }
                try
                {
                    JSONObject jsonResultObj = new JSONObject(result);                          //Convert json string to json object
                    String resultCode = jsonResultObj.getString(Global.RESULTCODE_TAG);         //get result code from json response
                    if (resultCode.equals(Global.JSON_REULST_ERROR)) {                            //if result code is error code, show error message
                        foodList.clear();
                        CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "No Cart Information", null);
                        //CommonFunc.NavigateActivity(mContext, BuyerPickFoodActivity.class);
                        return;
                    }
                    if (resultCode.equals(Global.JSON_RESULT_OK))
                    {
                        foodList.clear();

                        JSONArray foodArr = jsonResultObj.getJSONArray(Global.FOOD_TAG);
                        for (int i = 0; i < foodArr.length(); i++)
                        {
                            JSONObject foodObj = foodArr.getJSONObject(i);
                            //"contract_id":"15","buyer_id":"5","foods_id":"25","contract_status":"2","foods_lat":"13.866143794866916","foods_lon":"17.7426677942276",
                            // "foods_promise_time":"2016-01-18 17:09:00","foods_address":"Batha-Ouest ","foods_name":"fdasfdsa","foods_nutlv":"32432","foods_price":"432432",
                            // "foods_image":"","foods_seller_id":"7","foods_premium":"0","foods_regdate":"2016-01-18 17:11:11","foods_enabled":"1",
                            // "foods_deleted":"0","foods_ordernum":"0"

                            int foods_id = 0;
                            try{
                                foods_id = foodObj.getInt(Global.FOOD_ID_TAG);
                            }catch (JSONException e){}
                            double foods_lat = foodObj.optDouble(Global.FOOD_LAT_TAG, 0);
                            double foods_lon = foodObj.optDouble(Global.FOOD_LON_TAG, 0);
                            String foods_promise_time = foodObj.optString(Global.FOOD_PROMISE_TIME_TAG, "");
                            String foods_address = foodObj.optString(Global.FOOD_ADDRESS_TAG, "");
                            String foods_name = foodObj.optString(Global.FOOD_NAME_TAG, "");
                            double foods_nutlv = foodObj.optDouble(Global.FOOD_NATLV_TAG,0);
                            double foods_price = foodObj.optDouble(Global.FOOD_PRICE_TAG, 0);
                            int contract_status = 0;
                            try{
                                contract_status = foodObj.getInt("contract_status");
                            }catch (JSONException e){}

                            Food food = new Food();
                            food.setFood_id(foods_id);
                            food.setFood_lat(foods_lat);
                            food.setFood_lon(foods_lon);
                            food.setFood_promise_time(foods_promise_time);
                            food.setFood_address(foods_address);
                            food.setFood_name(foods_name);
                            food.setFood_nutlv(foods_nutlv);
                            food.setFood_price(foods_price);
                            food.setFood_contract_status(contract_status);

                            foodList.add(food);
                        }
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    @Override
    public void onBackPressed(){
        CommonFunc.NavigateActivity(mContext, BuyerPickFoodActivity.class);
    }
}
