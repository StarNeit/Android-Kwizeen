package com.kwizeen.fooddelivery.app.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpFunc;
import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpHandler;
import com.kwizeen.fooddelivery.app.main.BuyerMyCartActivity;
import com.kwizeen.fooddelivery.app.main.BuyerPickFoodActivity;
import com.kwizeen.fooddelivery.app.main.HomeActivity;
import com.kwizeen.fooddelivery.app.main.MyCustomerList;
import com.kwizeen.fooddelivery.app.main.R;
import com.kwizeen.fooddelivery.app.main.SellerCreateContractActivity;
import com.kwizeen.fooddelivery.app.models.Buyer;
import com.kwizeen.fooddelivery.app.models.Food;
import com.kwizeen.fooddelivery.app.utils.CommonFunc;
import com.kwizeen.fooddelivery.app.utils.Global;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.List;

//import com.kwizeen.fooddelivery.app.main.DriverContractDetailActivity;
//import com.kwizeen.fooddelivery.app.main.DriverContractShowActivity;

/**
 * Created by admin on 10/26/15.
 */
public class MyCartListAdapter extends BaseAdapter {

    List<Food> foodList;
    Context context;
    private static LayoutInflater inflater=null;
    ProgressDialog progressDialog;

    public MyCartListAdapter(Activity activity, List<Food> foodList) {
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

        View rowView = inflater.inflate(R.layout.buyer_cart_item, null);

        TextView txtFoodName = (TextView) rowView.findViewById(R.id.txtFoodName);
        TextView txtFoodAddress = (TextView) rowView.findViewById(R.id.txtFoodAddress);
        TextView txtFoodTime = (TextView) rowView.findViewById(R.id.txtFoodTime);
        TextView txtFoodPrice = (TextView) rowView.findViewById(R.id.txtFoodPrice);
        TextView txtFoodNutLv = (TextView) rowView.findViewById(R.id.txtFoodNutLv);

        TextView btnAccept = (TextView) rowView.findViewById(R.id.txtAcceptButton);
        TextView btnRemove = (TextView) rowView.findViewById(R.id.txtRemoveButton);
        TextView btnCompleteContract = (TextView) rowView.findViewById(R.id.txtCompleteContract);
        TextView btnCancelContract = (TextView) rowView.findViewById(R.id.txtCancelContract);

        txtFoodName.setText(food.getFood_name());
        txtFoodAddress.setText(food.getFood_address());
        txtFoodTime.setText(food.getFood_promise_time());
        txtFoodPrice.setText(food.getFood_price() + "");
        txtFoodNutLv.setText(food.getFood_nutlv() + "");

        if (food.getFood_contract_status() == 2)
        {
            rowView.setBackgroundResource(R.color.main_bg_color);
            btnAccept.setVisibility(View.GONE);
            btnRemove.setVisibility(View.GONE);
            //btnCompleteContract.setVisibility(View.VISIBLE);
            btnCancelContract.setVisibility(View.VISIBLE);
        }else if (food.getFood_contract_status() == 1)
        {
            rowView.setBackgroundResource(R.color.sub_bg_color);
            btnAccept.setVisibility(View.VISIBLE);
            btnRemove.setVisibility(View.VISIBLE);
            //btnCompleteContract.setVisibility(View.GONE);
            btnCancelContract.setVisibility(View.GONE);
        }

        btnAccept.setOnClickListener(new View.OnClickListener() {
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
                                        //http://localhost/kwizeen/service/buyer/acceptcontract?foods_id=2&buyer_id=1
                                        String urlParameters = "foods_id=" + foodList.get(position).getFood_id() + "&buyer_id=" + Global.g_Buyer.getBuyer_id();
                                        CommonFunc.AppLog(urlParameters);
                                        return CCHttpFunc.PostHttpRequestMethod(Global.BUYER_ACCEPT_CONTRACT_URL, urlParameters);
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
                                                CommonFunc.ShowAlertDialog(context, "Kwizeen", "Error, This Food doesn't exist", null);
                                                return;
                                            }
                                            if(resultCode.equals(Global.JSON_RESULT_OK)){
                                                CommonFunc.NavigateActivity(context, BuyerMyCartActivity.class);
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
                builder.setMessage("Are you sure to Accept?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
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
                                        //http://localhost/kwizeen/service/buyer/removefoodfromcart?buyer_id=1&foods_id=1
                                        String urlParameters = "foods_id=" + foodList.get(position).getFood_id() + "&buyer_id=" + Global.g_Buyer.getBuyer_id();
                                        CommonFunc.AppLog(urlParameters);
                                        return CCHttpFunc.PostHttpRequestMethod(Global.BUYER_REMOVE_FOOD_FROM_CART_URL, urlParameters);
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
                                                CommonFunc.ShowAlertDialog(context, "Kwizeen", "Error, Check your Internet", null);
                                                return;
                                            }
                                            if(resultCode.equals(Global.JSON_RESULT_OK)){
                                                CommonFunc.NavigateActivity(context, BuyerMyCartActivity.class);
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
                builder.setMessage("Are you sure to Remove from Cart?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        btnCompleteContract.setOnClickListener(new View.OnClickListener() {
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
                                        //http://localhost/kwizeen/service/buyer/completecontract?buyer_id=2&foods_id=2
                                        String urlParameters = "foods_id=" + foodList.get(position).getFood_id() + "&buyer_id=" + Global.g_Buyer.getBuyer_id();
                                        CommonFunc.AppLog(urlParameters);
                                        return CCHttpFunc.PostHttpRequestMethod(Global.BUYER_COMPLETE_CONTRACT_URL, urlParameters);
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
                                                CommonFunc.ShowAlertDialog(context, "Kwizeen", "Seller didn't Complete this Food Contract yet", null);
                                                return;
                                            }
                                            if(resultCode.equals(Global.JSON_RESULT_OK)){
                                                CommonFunc.NavigateActivity(context, BuyerMyCartActivity.class);
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
                builder.setMessage("Are you sure to Complete this Food Contract?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        btnCancelContract.setOnClickListener(new View.OnClickListener() {
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
                                        //http://localhost/kwizeen/service/buyer/cancelcontract?buyer_id=2&foods_id=1
                                        String urlParameters = "foods_id=" + foodList.get(position).getFood_id() + "&buyer_id=" + Global.g_Buyer.getBuyer_id();
                                        CommonFunc.AppLog(urlParameters);
                                        return CCHttpFunc.PostHttpRequestMethod(Global.BUYER_CANCEL_CONTRACT_URL, urlParameters);
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
                                                CommonFunc.ShowAlertDialog(context, "Kwizeen", "Error, Check your Internet", null);
                                                return;
                                            }
                                            if(resultCode.equals(Global.JSON_RESULT_OK)){
                                                CommonFunc.NavigateActivity(context, BuyerMyCartActivity.class);
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
                builder.setMessage("Are you sure to Cancel this Food Contract?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
        return rowView;
    }
}
