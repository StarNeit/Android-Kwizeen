package com.kwizeen.fooddelivery.app.main;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpFunc;
import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpHandler;
import com.kwizeen.fooddelivery.app.adapter.ContractListAdapter;
import com.kwizeen.fooddelivery.app.adapter.FoodMyListAdapter;
import com.kwizeen.fooddelivery.app.models.Buyer;
import com.kwizeen.fooddelivery.app.models.Seller;
import com.kwizeen.fooddelivery.app.models.Food;
import com.kwizeen.fooddelivery.app.utils.CommonFunc;
import com.kwizeen.fooddelivery.app.utils.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class BuyerPickFoodActivity extends AppCompatActivity implements View.OnClickListener {

    private GoogleMap googleMap;
    private MapView mMapView;

    private Marker myMarker;
    private boolean bFirstMap = true;
    private BroadcastReceiver mLocationChangeReceiver;
    private final int mapZoomValue = 2;

    private List<Seller> sellerList = new ArrayList<>();
    private ContractListAdapter adapter;
    private ListView listSeller;

    ProgressDialog progressDialog;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_pick_food);

        mContext = this;

        listSeller = (ListView)findViewById(R.id.listContract);
        adapter = new ContractListAdapter(this, sellerList);
        listSeller.setAdapter(adapter);

        GetSellerList();

        ((ImageView)findViewById(R.id.imgSearchFood)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ImageView) findViewById(R.id.imgSearchFood)).setVisibility(View.VISIBLE);
            }
        });
        ((ImageView)findViewById(R.id.imgMyFavFoodList)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonFunc.NavigateActivity(mContext, BuyerMyCartActivity.class);
            }
        });



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

        mLocationChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if(action.equals(Global.BROADCASTRECEIVER_FILTER_LOCATIONCHANGE)) {
                    MarkMyLocationOnGoogleMap(bFirstMap == true ? mapZoomValue : googleMap.getCameraPosition().zoom, bFirstMap);
                }
            }
        };

        ((ImageView)findViewById(R.id.imgShowMyLocation)).setOnClickListener(this);
        MarkMyLocationOnGoogleMap(bFirstMap == true ? mapZoomValue : googleMap.getCameraPosition().zoom, bFirstMap);



    }
/*
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.setMyLocationEnabled(true);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {


                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                    @Override
                    public void onMyLocationChange(Location arg0) {
                        // TODO Auto-generated method stub

                        mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                    }
                });

            }
        }
    }
*/
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.imgShowMyLocation) {
            MarkMyLocationOnGoogleMap(mapZoomValue, true);
        }
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

    private void showFoodContractPosition(double foods_lat,double foods_lon)
    {
        LatLng loc = new LatLng(foods_lat,foods_lon);

        String address = CommonFunc.GetAddressFromLatLng(mContext, loc.latitude, loc.longitude);
        MarkerOptions markerOptions = new MarkerOptions().position(loc).title("Seller:"+address);
        markerOptions.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        googleMap.addMarker(markerOptions);
    }

    private void GetSellerList()
    {
        progressDialog = CommonFunc.createProgressDialog(this);
        progressDialog.show();

        new CCHttpHandler(){
            //Send method
            @Override
            public HttpURLConnection getHttpRequestMethod() {
                //http://localhost/kwizeen/service/buyer/getcontractlist
                return CCHttpFunc.PostHttpRequestMethod(Global.BUYER_GET_CONTRACT_LIST_URL,"");
            }

            //Receive method
            @Override
            public void onResponse(String result) {
                //close progress dialog
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
                        sellerList.clear();
                        CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "No Dish Information", null);
                        progressDialog.dismiss();
                        return;
                    }
                    if (resultCode.equals(Global.JSON_RESULT_OK))
                    {
                        sellerList.clear();

                        JSONArray sellerArr = jsonResultObj.getJSONArray(Global.FOOD_TAG);
                        for (int i = 0; i < sellerArr.length(); i++)
                        {
                            JSONObject sellerObj = sellerArr.getJSONObject(i);
                            //"foods_id":"1","foods_lat":"45.95115947722279","foods_lon":"45.8149690926075","foods_promise_time":"2016-01-16 20:32:00",
                            // "foods_address":"Kalmykia Russia ","foods_name":"aaaaa","foods_nutlv":"0","foods_price":"0","foods_image":"aaa","foods_seller_id":"2",
                            // "foods_premium":"0","foods_regdate":"2016-01-16 21:02:17","foods_enabled":"1","foods_deleted":"0","foods_ordernum":"2","seller_id":"2",
                            // "seller_name":"qqq","seller_email":"qqq","seller_password":"b2ca678b4c936f905fb82f2733f5297f","seller_lat":"0","seller_lon":"0",
                            // "seller_phone":"777","seller_address":"qqq","seller_regdate":"2016-01-16 20:28:56","seller_enabled":"1","seller_deleted":"0"

                            String seller_name = sellerObj.optString(Global.SELLER_NAME_TAG, "");
                            int seller_id = 0;
                            try{
                                seller_id = sellerObj.getInt(Global.SELLER_ID_TAG);
                            }catch (JSONException e){}
                            String seller_address = sellerObj.optString(Global.SELLER_ADDRESS_TAG, "");
//                          double seller_lat = sellerObj.optDouble(Global.SELLER_LAT_TAG, 0);
//                          double seller_lon = sellerObj.optDouble(Global.SELLER_LON_TAG, 0);
                            String seller_phone = sellerObj.optString(Global.SELLER_PHONE_TAG, "");

                            int foods_id = 0;
                            try{
                                foods_id = sellerObj.getInt(Global.FOOD_ID_TAG);
                            }catch (JSONException e){}
                            double foods_lat = sellerObj.optDouble(Global.FOOD_LAT_TAG, 0);
                            double foods_lon = sellerObj.optDouble(Global.FOOD_LON_TAG, 0);
                            showFoodContractPosition(foods_lat,foods_lon);
                            String foods_promise_time = sellerObj.optString(Global.FOOD_PROMISE_TIME_TAG, "");
                            String foods_address = sellerObj.optString(Global.FOOD_ADDRESS_TAG, "");
                            String foods_name = sellerObj.optString(Global.FOOD_NAME_TAG, "");
                            double foods_nutlv = sellerObj.optDouble(Global.FOOD_NATLV_TAG,0);
                            double foods_price = sellerObj.optDouble(Global.FOOD_PRICE_TAG, 0);
                            String foods_image = sellerObj.optString(Global.FOOD_IMAGE_TAG,"");


                            Food food = new Food();
                            food.setFood_id(foods_id);
                            food.setFood_lat(foods_lat);
                            food.setFood_lon(foods_lon);
                            food.setFood_promise_time(foods_promise_time);
                            food.setFood_address(foods_address);
                            food.setFood_name(foods_name);
                            food.setFood_nutlv(foods_nutlv);
                            food.setFood_price(foods_price);
                            food.setFood_image(foods_image);
                            food.setBuyerInfo(new Buyer());


                            Seller seller = new Seller();

                            seller.setFoodInfo(food);
                            seller.setSeller_id(seller_id);
                            seller.setSeller_name(seller_name);
                            seller.setSeller_address(seller_address);
                            //seller.setSeller_lat(seller_lat);
                            //seller.setSeller_lon(seller_lon);
                            seller.setSeller_phone(seller_phone);

                            sellerList.add(seller);
                        }
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }.execute();
    }
    @Override
    public void onBackPressed(){
        CommonFunc.NavigateActivity(mContext, HomeActivity.class);
    }
}
