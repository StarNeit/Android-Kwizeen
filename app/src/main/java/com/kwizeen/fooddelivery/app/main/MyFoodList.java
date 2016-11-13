package com.kwizeen.fooddelivery.app.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpFunc;
import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpHandler;
import com.kwizeen.fooddelivery.app.adapter.FoodMyListAdapter;
import com.kwizeen.fooddelivery.app.models.Buyer;
import com.kwizeen.fooddelivery.app.models.Seller;
import com.kwizeen.fooddelivery.app.models.Food;
import com.kwizeen.fooddelivery.app.utils.CommonFunc;
import com.kwizeen.fooddelivery.app.utils.Global;
import com.kwizeen.fooddelivery.app.main.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class MyFoodList extends AppCompatActivity {

    private List<Food> foodList = new ArrayList<>();
    private FoodMyListAdapter adapter;
    private ListView listFood;

    ProgressDialog progressDialog;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_contract_list);

        mContext = this;

        listFood = (ListView)findViewById(R.id.listContract);
        adapter = new FoodMyListAdapter(this, foodList);
        listFood.setAdapter(adapter);

        GetContractList();

        ((ImageView)findViewById(R.id.imgAddNewFood)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Global.g_Seller.setFoodInfo(new Food());
                //Global.g_Seller.getFoodInfo().setBuyerInfo(new Buyer());
                CommonFunc.NavigateActivity(mContext, SellerCreateContractActivity.class);
            }
        });
    }

    private void GetContractList(){
        progressDialog = CommonFunc.createProgressDialog(this);
        progressDialog.show();
        new CCHttpHandler() {

            //Send method
            @Override
            public HttpURLConnection getHttpRequestMethod() {
                //http://localhost/kwizeen/service/Seller/GetMyfoodsInfo?my_seller_id=1
                String urlParameters = "my_seller_id=" + Global.g_Seller.getSeller_id();
                CommonFunc.AppLog(urlParameters);
                return CCHttpFunc.PostHttpRequestMethod(Global.SELLER_GETMYFOODSINFO_URL, urlParameters);
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
                try {
                    JSONObject jsonResultObj = new JSONObject(result);                          //Convert json string to json object
                    String resultCode = jsonResultObj.getString(Global.RESULTCODE_TAG);         //get result code from json response
                    if (resultCode.equals(Global.JSON_REULST_ERROR)) {                            //if result code is error code, show error message
                        foodList.clear();
                        CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "No Dish Information", null);
                        return;
                    }
                    if (resultCode.equals(Global.JSON_RESULT_OK))
                    {
                        foodList.clear();

                        JSONArray foodArr = jsonResultObj.getJSONArray(Global.FOOD_TAG);
                        for (int i = 0; i < foodArr.length(); i++)
                        {
                            JSONObject foodObj = foodArr.getJSONObject(i);

                            int foods_id = foodObj.getInt(Global.FOOD_ID_TAG);
                            double foods_lat = foodObj.optDouble(Global.FOOD_LAT_TAG, 0);
                            double foods_lon = foodObj.optDouble(Global.FOOD_LON_TAG, 0);
                            String foods_promise_time = foodObj.optString(Global.FOOD_PROMISE_TIME_TAG, "");
                            String foods_address = foodObj.optString(Global.FOOD_ADDRESS_TAG, "");
                            String foods_regdate = foodObj.optString(Global.FOOD_REGDATE, "");

                            String foods_name = foodObj.optString(Global.FOOD_NAME_TAG, "");
                            double foods_nutlv = foodObj.optDouble(Global.FOOD_NATLV_TAG, 0);
                            double foods_price = foodObj.optDouble(Global.FOOD_PRICE_TAG,0);
                            String foods_image = foodObj.optString(Global.FOOD_IMAGE_TAG,"");
                            int foods_ordernum = foodObj.getInt(Global.FOOD_ORDERNUM_TAG);

                            Food food = new Food();
                            food.setFood_id(foods_id);
                            food.setFood_lat(foods_lat);
                            food.setFood_lon(foods_lon);
                            food.setFood_promise_time(foods_promise_time);
                            food.setFood_address(foods_address);
                            food.setFood_regdate(foods_regdate);
                            food.setFood_name(foods_name);
                            food.setFood_nutlv(foods_nutlv);
                            food.setFood_price(foods_price);
                            food.setFood_image(foods_image);
                            food.setFood_ordernum(foods_ordernum);

                            food.setSellerInfo(new Seller());
                            foodList.add(food);
                        }
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
    @Override
    public void onBackPressed(){
        CommonFunc.NavigateActivity(mContext, SellerCreateContractActivity.class);
    }
}
