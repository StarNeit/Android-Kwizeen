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
import android.content.Intent;
import android.net.Uri;

import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpFunc;
import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpHandler;
import com.kwizeen.fooddelivery.app.main.BuyerFoodsDetailActivity;
import com.kwizeen.fooddelivery.app.main.BuyerMyCartActivity;
import com.kwizeen.fooddelivery.app.main.R;
import com.kwizeen.fooddelivery.app.main.SellerFinishContractActivity;
import com.kwizeen.fooddelivery.app.models.Buyer;
import com.kwizeen.fooddelivery.app.models.Seller;
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
public class ContractListAdapter extends BaseAdapter {

    List<Seller> sellerList;
    Context context;
    private static LayoutInflater inflater=null;
    ProgressDialog progressDialog;

    public ContractListAdapter(Activity activity, List<Seller> sellerList) {
        // TODO Auto-generated constructor stub
        this.context = activity;
        this.sellerList = sellerList;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return sellerList.size();
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
        View rowView = inflater.inflate(R.layout.contract_list_item, null);

        final Seller seller = sellerList.get(position);

        TextView txtFoodName = (TextView) rowView.findViewById(R.id.txtFoodName);
        TextView txtSellerPhone = (TextView) rowView.findViewById(R.id.txtSellerPhone);
        TextView txtSellerAddress = (TextView) rowView.findViewById(R.id.txtSellerAddress);
        TextView txtSellerTime = (TextView) rowView.findViewById(R.id.SellerPromiseTime);

        TextView btnAddToCart = (TextView) rowView.findViewById(R.id.txtCartButton);
        TextView btnDetails = (TextView) rowView.findViewById(R.id.txtDetailsButton);

        txtFoodName.setText(seller.getFoodInfo().getFood_name());
        txtSellerPhone.setText(seller.getSeller_phone());
        txtSellerAddress.setText(seller.getFoodInfo().getFood_address());
        txtSellerTime.setText(seller.getFoodInfo().getFood_promise_time());

        txtSellerPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + seller.getSeller_phone()));
                    context.startActivity(intent);
                }catch (Exception e){

                }
            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if (progressDialog == null) {
                                    progressDialog = CommonFunc.createProgressDialog(context);
                                }
                                progressDialog.show();

                                new CCHttpHandler() {
                                    //Send method
                                    @Override
                                    public HttpURLConnection getHttpRequestMethod() {
                                        //http://localhost/kwizeen/service/buyer/addfoodtomylist?foods_id=1&buyer_id=3
                                        String urlParameters = "buyer_id=" + Global.g_Buyer.getBuyer_id() + "&foods_id=" + seller.getFoodInfo().getFood_id();
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
                                            CommonFunc.ShowAlertDialog(context, "Kwizeen", "Error, Check your Internet", null);//If response string is empty, show error message
                                            return;
                                        }
                                        try {
                                            JSONObject jsonResultObj = new JSONObject(result);                          //Convert json string to json object
                                            String resultCode = jsonResultObj.getString(Global.RESULTCODE_TAG);         //get result code from json response
                                            if (resultCode.equals(Global.JSON_REULST_ERROR)) {                            //if result code is error code, show error message
                                                CommonFunc.ShowAlertDialog(context, "Kwizeen", "This Food Doesn' Exist any more", null);
                                                return;
                                            }
                                            if (resultCode.equals(Global.JSON_REULST_EMAIL_DUPLICATE)) {
                                                CommonFunc.ShowAlertDialog(context, "Kwizeen", "Already Added to Cart", null);
                                                return;
                                            }
                                            if (resultCode.equals(Global.JSON_RESULT_OK)) {
                                                /*Global.g_Seller.setFoodInfo(new Food());
                                                Global.g_Seller.getFoodInfo().setBuyerInfo(new Buyer());*/
                                                CommonFunc.NavigateActivity(context, BuyerMyCartActivity.class);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure to Add to Cart?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Global.g_Buyer.getFoodInfo().setFood_id(seller.getFoodInfo().getFood_id());
                Global.g_Buyer.getFoodInfo().setFood_name(seller.getFoodInfo().getFood_name());
                Global.g_Buyer.getFoodInfo().setFood_address(seller.getFoodInfo().getFood_address());
                Global.g_Buyer.getFoodInfo().setFood_promise_time(seller.getFoodInfo().getFood_promise_time());
                Global.g_Buyer.getFoodInfo().setFood_image(seller.getFoodInfo().getFood_image());
                Global.g_Buyer.getFoodInfo().setFood_lat(seller.getFoodInfo().getFood_lat());
                Global.g_Buyer.getFoodInfo().setFood_lon(seller.getFoodInfo().getFood_lon());
                Global.g_Buyer.getFoodInfo().setFood_nutlv(seller.getFoodInfo().getFood_nutlv());
                Global.g_Buyer.getFoodInfo().setFood_price(seller.getFoodInfo().getFood_price());

                CommonFunc.NavigateActivity(context, BuyerFoodsDetailActivity.class);
            }
        });
        return rowView;
    }
}
