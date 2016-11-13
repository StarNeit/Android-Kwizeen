package com.kwizeen.fooddelivery.app.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kwizeen.fooddelivery.app.main.MyCustomerList;
import com.kwizeen.fooddelivery.app.main.MyFoodList;
import com.kwizeen.fooddelivery.app.main.SellerCreateContractActivity;
import com.kwizeen.fooddelivery.app.models.Seller;
import com.kwizeen.fooddelivery.app.models.Food;
import com.kwizeen.fooddelivery.app.main.R;
import com.kwizeen.fooddelivery.app.utils.Global;

import com.kwizeen.fooddelivery.app.models.Buyer;

import java.util.List;

import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpFunc;
import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpHandler;
//import com.kwizeen.fooddelivery.app.main.DriverContractDetailActivity;
//import com.kwizeen.fooddelivery.app.main.DriverContractShowActivity;
import com.kwizeen.fooddelivery.app.utils.CommonFunc;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by admin on 10/26/15.
 */
public class FoodMyListAdapter extends BaseAdapter {

    List<Food> foodList;
    Context context;
    private static LayoutInflater inflater=null;
    ProgressDialog progressDialog;

    public FoodMyListAdapter(Activity activity, List<Food> foodList) {
        // TODO Auto-generated constructor stub
        this.context = activity;
        this.foodList = foodList;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return foodList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Food food = foodList.get(position);

        View rowView = inflater.inflate(R.layout.contract_mylist_item, null);//contract_mylist_item
        TextView txtFoodName = (TextView) rowView.findViewById(R.id.txtFoodName);
        TextView txtFoodAddress = (TextView) rowView.findViewById(R.id.txtFoodAddress);
        TextView txtFoodTime = (TextView) rowView.findViewById(R.id.txtFoodTime);
        TextView txtFoodPrice = (TextView) rowView.findViewById(R.id.txtFoodPrice);
        TextView txtFoodOrderNum = (TextView) rowView.findViewById(R.id.txtOrderNum);

        TextView btnStatus = (TextView) rowView.findViewById(R.id.txtStatusButton);
        TextView btnRemove = (TextView) rowView.findViewById(R.id.txtRemoveButton);

        txtFoodName.setText(food.getFood_name());
        txtFoodAddress.setText(food.getFood_address());
        txtFoodTime.setText(food.getFood_promise_time());
        txtFoodPrice.setText(food.getFood_price() + "");
        txtFoodOrderNum.setText(food.getFood_ordernum() + "");


        if (food.getFood_ordernum()>0){
            rowView.setBackgroundResource(R.color.main_bg_color);
            btnRemove.setVisibility(View.GONE);
            btnStatus.setVisibility(View.VISIBLE);
            txtFoodOrderNum.setTextColor(Color.RED);
            txtFoodOrderNum.setTextSize(18);
        }else{
            rowView.setBackgroundResource(R.color.sub_bg_color);
            btnRemove.setVisibility(View.VISIBLE);
            btnStatus.setVisibility(View.GONE);
            txtFoodOrderNum.setTextColor(Color.WHITE);
        }

        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(context, DriverContractDetailActivity.class);
                intent.putExtra("pName", foodList.get(position).getPassengerInfo().getUser_name());
                intent.putExtra("pPhone", foodList.get(position).getPassengerInfo().getUser_phone());
                intent.putExtra("pLat", foodList.get(position).getPassengerInfo().getUser_lat());
                intent.putExtra("pLon", foodList.get(position).getPassengerInfo().getUser_lon());
                intent.putExtra("pId", foodList.get(position).getPassengerInfo().getUser_id());
                intent.putExtra("tAddr", foodList.get(position).getTrip_address());
                intent.putExtra("tTime", foodList.get(position).getTrip_promise_time());
                intent.putExtra("tLat", foodList.get(position).getTrip_lat());
                intent.putExtra("tLon", foodList.get(position).getTrip_lon());
                intent.putExtra("tId", foodList.get(position).getTrip_id());
                context.startActivity(intent);*/
                Global.g_Seller.setFoodInfo(new Food());
                Global.g_Seller.getFoodInfo().setBuyerInfo(new Buyer());
                Global.g_Seller.getFoodInfo().setFood_id(foodList.get(position).getFood_id());
                CommonFunc.NavigateActivity(context, MyCustomerList.class);
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if(progressDialog == null) {
                                    progressDialog = CommonFunc.createProgressDialog(context);
                                }
                                progressDialog.show();

                                new CCHttpHandler(){
                                    //Send method
                                    @Override
                                    public HttpURLConnection getHttpRequestMethod() {
                                        //http://localhost/kwizeen/service/Seller/RemoveMyFoodInfo?my_food_id=2
                                        String urlParameters = "my_food_id=" + foodList.get(position).getFood_id();
                                        CommonFunc.AppLog(urlParameters);
                                        return CCHttpFunc.PostHttpRequestMethod(Global.SELLER_REMOVEMYFOODINFO_URL, urlParameters);
                                    }

                                    //Receive method
                                    @Override
                                    public void onResponse(String result) {
                                        //close progress dialog
                                        progressDialog.dismiss();
                                        CommonFunc.AppLog(result);

                                        if(result.equals("")){
                                            CommonFunc.ShowAlertDialog(context, "Kwizeen", "Error, Check your Internet", null);//If response string is empty, show error message
                                            return;
                                        }
                                        try {
                                            JSONObject jsonResultObj = new JSONObject(result);                          //Convert json string to json object
                                            String resultCode = jsonResultObj.getString(Global.RESULTCODE_TAG);         //get result code from json response
                                            if(resultCode.equals(Global.JSON_REULST_ERROR)){                            //if result code is error code, show error message
                                                CommonFunc.ShowAlertDialog(context, "Kwizeen", "Buyer Or Seller accepted this food.Please finish the contract first.", null);
                                                return;
                                            }
                                            if(resultCode.equals(Global.JSON_RESULT_OK)){
                                                //Global.g_Seller.setFoodInfo(new Food());
                                                //Global.g_Seller.getFoodInfo().setBuyerInfo(new Buyer());
                                                CommonFunc.NavigateActivity(context, MyFoodList.class);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure to Remove?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        return rowView;
    }


}

