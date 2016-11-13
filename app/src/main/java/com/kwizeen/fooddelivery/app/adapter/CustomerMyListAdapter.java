package com.kwizeen.fooddelivery.app.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpFunc;
import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpHandler;
import com.kwizeen.fooddelivery.app.main.HomeActivity;
import com.kwizeen.fooddelivery.app.main.R;
import com.kwizeen.fooddelivery.app.main.SellerCreateContractActivity;
import com.kwizeen.fooddelivery.app.main.SellerFinishContractActivity;
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
public class CustomerMyListAdapter extends BaseAdapter {

    List<Buyer> buyerList;
    Context context;
    private static LayoutInflater inflater=null;
    ProgressDialog progressDialog;

    public CustomerMyListAdapter(Activity activity, List<Buyer> buyerList) {
        // TODO Auto-generated constructor stub
        this.context = activity;
        this.buyerList = buyerList;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return buyerList.size();
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
        View rowView = inflater.inflate(R.layout.customer_list_item, null);

        final Buyer buyer = buyerList.get(position);

        TextView txtBuyerName = (TextView) rowView.findViewById(R.id.txtBuyerName);
        TextView txtBuyerPhone = (TextView) rowView.findViewById(R.id.txtBuyerPhone);

        TextView btnConfirm = (TextView) rowView.findViewById(R.id.txtConfirmButton);

        txtBuyerName.setText(buyer.getBuyer_name());
        txtBuyerPhone.setText(buyer.getBuyerPhone());

        btnConfirm.setOnClickListener(new View.OnClickListener() {
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
                                        //http://localhost/kwizeen/service/Seller/completecontract?buyer_id=2&foods_id=2
                                        String urlParameters = "buyer_id=" + buyerList.get(position).getBuyer_id() + "&foods_id=" + buyerList.get(position).getFoodInfo().getFood_id();
                                        CommonFunc.AppLog(urlParameters);
                                        return CCHttpFunc.PostHttpRequestMethod(Global.SELLER_COMPLETECONTRACT_URL, urlParameters);
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
                                                CommonFunc.ShowAlertDialog(context, "Kwizeen", "Buyer cancelled this food.", null);
                                                return;
                                            }
                                            if(resultCode.equals(Global.JSON_RESULT_OK)){
                                                //Global.g_Seller.setFoodInfo(new Food());
                                                //Global.g_Seller.getFoodInfo().setBuyerInfo(new Buyer());
                                                CommonFunc.NavigateActivity(context, SellerFinishContractActivity.class);
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
                builder.setMessage("Are you sure to complete this contract?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
        return rowView;
    }
}
