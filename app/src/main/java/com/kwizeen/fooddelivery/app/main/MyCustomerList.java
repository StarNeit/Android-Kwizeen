package com.kwizeen.fooddelivery.app.main;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;


import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpFunc;
import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpHandler;
import com.kwizeen.fooddelivery.app.adapter.CustomerMyListAdapter;
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


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kwizeen.fooddelivery.app.utils.SimpleAlertDialogListener;

public class MyCustomerList extends AppCompatActivity{

    private GoogleMap googleMap;
    private MapView mMapView;

    private Marker myMarker, foodMarker, buyerMaker;
    private boolean bFirstMap = true;

    private BroadcastReceiver mLocationChangeReceiver;

    private List<Buyer> buyerList = new ArrayList<>();
    private CustomerMyListAdapter adapter;
    private ListView listBuyer;

    ProgressDialog progressDialog;

    private Context mContext;

    private final int mapZoomValue = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_customer_list);

        mContext = this;

        listBuyer = (ListView) findViewById(R.id.listCustomerContract);
        adapter = new CustomerMyListAdapter(this, buyerList);
        listBuyer.setAdapter(adapter);

        GetBuyerList();


        mMapView = (MapView)findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }


        googleMap = mMapView.getMap();
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                SetFoodMarker(latLng);
            }
        });


        MarkMyLocationOnGoogleMap(bFirstMap == true ? mapZoomValue : googleMap.getCameraPosition().zoom, bFirstMap);
    }

    private void SetFoodMarker(LatLng loc){

        if(loc == null){
            CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Contract location is not set", null);
            return;
        }
        //foodLocation = loc;
        //address = CommonFunc.GetAddressFromLatLng(mContext, loc.latitude, loc.longitude);
        //if(address.equals("null")) address = "";
        //editAddress.setText(address);
        if(foodMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions().position(loc).title("Contract: ");
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            foodMarker = googleMap.addMarker(markerOptions);
        }else{
            foodMarker.setPosition(loc);
        }
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(loc).zoom(mapZoomValue).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    public void MarkMyLocationOnGoogleMap(float mapZoomValue, boolean animateCameraToMyLocation){
        LatLng myLoc = new LatLng(Global.g_Lat, Global.g_Lon);
        // create marker
        if(myMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions().position(myLoc).title("Me: " + CommonFunc.GetAddressFromLatLng(mContext, Global.g_Lat, Global.g_Lon));
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_RED));
            myMarker = googleMap.addMarker(markerOptions);
        }else{
            myMarker.setPosition(myLoc);
        }

        if(animateCameraToMyLocation) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(myLoc).zoom(mapZoomValue).build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }
        bFirstMap = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        bFirstMap = true;
        mMapView.onResume();
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mLocationChangeReceiver, new IntentFilter(Global.BROADCASTRECEIVER_FILTER_LOCATIONCHANGE));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mLocationChangeReceiver);
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void GetBuyerList()
    {
        if(progressDialog == null) {
            progressDialog = CommonFunc.createProgressDialog(mContext);
        }
        progressDialog.show();

        new CCHttpHandler(){
            //Send method
            @Override
            public HttpURLConnection getHttpRequestMethod() {
                //http://localhost/kwizeen/service/Seller/GetBuyerListByFood?foods_id=2
                String urlParameters = "foods_id=" + Global.g_Seller.getFoodInfo().getFood_id();
                CommonFunc.AppLog(urlParameters);
                return CCHttpFunc.PostHttpRequestMethod(Global.SELLER_GETBUYERLISTBYFOOD_URL, urlParameters);
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
                        buyerList.clear();
                        //CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "No Customer Information", new SimpleAlertDialogListener(mContext, BuyerPickFoodActivity.class));
                        CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "No Customer Information", null);
                        return;
                    }
                    if (resultCode.equals(Global.JSON_RESULT_OK))
                    {
                        buyerList.clear();

                        JSONArray buyerArr = jsonResultObj.getJSONArray(Global.BUYER_TAG);
                        for (int i = 0; i < buyerArr.length(); i++)
                        {
                            JSONObject buyerObj = buyerArr.getJSONObject(i);
                            //"contract_id":"4","buyer_id":"1","foods_id":"2","contract_status":"1","buyer_name":"Smith11","buyer_email":"jacksmith91620@gmail.com","buyer_password":
                            // "b2ca678b4c936f905fb82f2733f5297f","buyer_phone":"12343","buyer_lat":"65.9691916666667","buyer_lon":"-18.532175","buyer_regdate":"2015-12-19 20:28:48",
                            // "buyer_enabled":"1","buyer_deleted":"0"

                            int buyer_id = buyerObj.getInt(Global.BUYER_ID_TAG);
                            String buyer_name = buyerObj.optString(Global.BUYER_NAME_TAG,"");
                            String buyer_email = buyerObj.optString(Global.BUYER_EMAIL_TAG,"");
                            String buyer_phone = buyerObj.optString(Global.BUYER_PHONE_TAG,"");
                            double buyer_lat = buyerObj.optDouble(Global.BUYER_LAT_TAG, 0);
                            double buyer_lon = buyerObj.optDouble(Global.BUYER_LON_TAG, 0);
                            String buyer_regdate = buyerObj.optString(Global.BUYER_REGDATE_TAG, "");

                            Buyer buyer = new Buyer();
                            buyer.setBuyer_id(buyer_id);
                            buyer.setBuyer_name(buyer_name);
                            buyer.setBuyer_email(buyer_email);
                            buyer.setBuyerPhone(buyer_phone);
                            buyer.setBuyer_lat(buyer_lat);
                            buyer.setBuyer_lon(buyer_lon);
                            buyer.setBuyer_regdate(buyer_regdate);

                            Food food = new Food();
                            food.setFood_id(Global.g_Seller.getFoodInfo().getFood_id());
                            buyer.setFoodInfo(food);
                            buyerList.add(buyer);
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
        CommonFunc.NavigateActivity(mContext, MyFoodList.class);
    }
}
