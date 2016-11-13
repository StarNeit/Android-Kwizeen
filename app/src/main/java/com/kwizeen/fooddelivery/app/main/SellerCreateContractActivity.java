package com.kwizeen.fooddelivery.app.main;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import java.util.HashMap;
import java.util.Map;
import android.os.AsyncTask;
import java.io.ByteArrayOutputStream;
import android.util.Base64;

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
import com.kwizeen.fooddelivery.app.models.Food;
import com.kwizeen.fooddelivery.app.HttpUtils.CCHttpHandler;
import com.kwizeen.fooddelivery.app.main.R;
import com.kwizeen.fooddelivery.app.models.Buyer;
import com.kwizeen.fooddelivery.app.utils.CommonFunc;
import com.kwizeen.fooddelivery.app.utils.Global;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.kwizeen.fooddelivery.app.utils.ImageRequest;

public class SellerCreateContractActivity extends AppCompatActivity implements View.OnClickListener{

    private GoogleMap googleMap;
    private MapView mMapView;

    private Marker myMarker, foodMarker, buyerMaker;
    private boolean bFirstMap = true;

    private Context mContext;

    private BroadcastReceiver mLocationChangeReceiver;

    private TextView editAddress, editDate, editTime;
    private EditText editFoodName, editNutLv, editPrice, editImage;

    private String address, strTime;
    private LatLng foodLocation;

    private final int mapZoomValue = 2;

    private int mYear, mMonth, mDay, mHour, mMinute;

    ProgressDialog progressDialog;

    private String imageFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_create_contract);

        mContext = this;

        GregorianCalendar calendar = new GregorianCalendar();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay= calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);

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
        ((ImageView)findViewById(R.id.imgShowFoodLocation)).setOnClickListener(this);


        MarkMyLocationOnGoogleMap(bFirstMap == true ? mapZoomValue : googleMap.getCameraPosition().zoom, bFirstMap);


        ((ImageView)findViewById(R.id.imgShowContractList)).setOnClickListener(this);
        ((Button)findViewById(R.id.btnCreateContract)).setOnClickListener(this);
        editAddress = (TextView)findViewById(R.id.editFoodAddress);

        editDate = (TextView)findViewById(R.id.editFoodPromiseDate);
        editTime = (TextView)findViewById(R.id.editFoodPromiseTime);
        editDate.setOnClickListener(this);
        editTime.setOnClickListener(this);


        editFoodName = (EditText)findViewById(R.id.editFoodName);
        editNutLv = (EditText)findViewById(R.id.editFoodNutLv);
        editPrice = (EditText)findViewById(R.id.editFoodPrice);
        editImage = (EditText)findViewById(R.id.editFoodImage);
        editImage.setOnClickListener(this);
//        address = CommonFunc.GetAddressFromLatLng(mContext,Global.g_Lat, Global.g_Lon);
//        editAddress.setText(address);


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


    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.imgShowMyLocation){
            MarkMyLocationOnGoogleMap(mapZoomValue, true);
        }
        if(v.getId() == R.id.imgShowFoodLocation){
            SetFoodMarker(foodLocation);
        }
        if(v.getId() == R.id.editFoodImage){
            //CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Image Selection", null);
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        }
        if(v.getId() == R.id.editFoodPromiseDate){
            new DatePickerDialog(mContext, dateSetListener, mYear, mMonth, mDay).show();
        }

        if(v.getId() == R.id.editFoodPromiseTime){
            new TimePickerDialog(mContext, timeSetListener, mHour, mMinute, true).show();
        }

        if(v.getId() == R.id.btnCreateContract){
            String foodname = editFoodName.getText().toString().trim();
            if(foodname.equals("")){
                CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Input Food Name", null);
                return;
            }
            String nutlv = editNutLv.getText().toString().trim();
            if(nutlv.equals("")){
                CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Input Nutrition Level", null);
                return;
            }
            String price = editPrice.getText().toString().trim();
            if(price.equals("")){
                CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Input Food Price", null);
                return;
            }
            String address = editAddress.getText().toString().trim();
            if(address.equals("")){
                CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Select Contract Location by Click Map", null);
                return;
            }
            String date = editDate.getText().toString().trim();
            if(date.equals("")){
                CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Select Contract Date", null);
                return;
            }
            String time = editTime.getText().toString().trim();
            if(time.equals("")){
                CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Select Contract Time", null);
                return;
            }
            strTime = date + " " + time + ":00";
            String foodimage = editImage.getText().toString().trim();
            if(foodimage.equals("")){
                CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Input Food Image", null);
                return;
            }

            CreateContract();
        }
        if(v.getId() == R.id.imgShowContractList)
        {
            CommonFunc.NavigateActivity(mContext, MyFoodList.class);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Global.RESULT_SELECT_IMAGE && resultCode == RESULT_OK && data.getData() != null){
            //set the selected image to image variable
            Uri image = data.getData();
            ImageView uploadImage = (ImageView) findViewById(R.id.uploadImage);
            uploadImage.setImageURI(image);

            //get the current timeStamp and strore that in the time Variable
            Long tsLong = System.currentTimeMillis() / 1000;
            String timestamp = tsLong.toString();

            //get image in bitmap format
            Bitmap b_image = ((BitmapDrawable) uploadImage.getDrawable()).getBitmap();
            //execute the async task and upload the image to server
            new Upload(b_image, "IMG_" + timestamp).execute();
            imageFileName = "IMG_" + timestamp;
        }
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            mYear = year;   mMonth = monthOfYear;   mDay = dayOfMonth;
            String strDate = String.format("%d-%s-%s", year, CommonFunc.leadZero(monthOfYear+1), CommonFunc.leadZero(dayOfMonth));
            editDate.setText(strDate);
        }
    };

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // TODO Auto-generated method stub
            mHour = hourOfDay;  mMinute = minute;
            String strTime = String.format("%s:%s", CommonFunc.leadZero(hourOfDay), CommonFunc.leadZero(minute));
            editTime.setText(strTime);
        }
    };

    private void SetFoodMarker(LatLng loc){

        if(loc == null){
            CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Food Contract location is not set", null);
            return;
        }
        foodLocation = loc;
        address = CommonFunc.GetAddressFromLatLng(mContext, loc.latitude, loc.longitude);
        if(address.equals("null")) address = "";
        editAddress.setText(address);
        if(foodMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions().position(loc).title("Contract: " + address);
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

    private void CreateContract(){
        if(progressDialog == null) {
            progressDialog = CommonFunc.createProgressDialog(this);
        }
        progressDialog.show();
        new CCHttpHandler(){

            //Send method
            @Override
            public HttpURLConnection getHttpRequestMethod() {
                String urlParameters = null;
                try {
                    //http://localhost/kwizeen/service/Seller/CreateContract?seller_id=1&lat=123&lon=-43&address=afd&time=dfs&foodname=fdsa&nutlv=23&price=3434&image=fafd
                    urlParameters = "seller_id=" + Global.g_Seller.getSeller_id() + "&lat=" + foodLocation.latitude + "&lon=" + foodLocation.longitude
                            + "&address=" + URLEncoder.encode(address, "UTF-8") + "&time=" + strTime + "&foodname=" + editFoodName.getText().toString().trim()
                            + "&nutlv=" + editNutLv.getText().toString().trim() + "&price=" + editPrice.getText().toString().trim() +"&image=" + editImage.getText().toString().trim() +".JPG";

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                /*urlParameters = "seller_id=" + Global.g_Seller.getSeller_id() + "&lat=" + 0 + "&lon=" + 0
                        + "&address=" + "oust" +"&time=" + strTime + "&foodname=" + editFoodName.getText().toString().trim()
                        + "&nutlv=" + editNutLv.getText().toString().trim() + "&price=" + editPrice.getText().toString().trim() +"&image=" + "";*/
                CommonFunc.AppLog(urlParameters);
                return CCHttpFunc.PostHttpRequestMethod(Global.SELLER_CREATE_CONTRACT_URL, urlParameters);
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
                        CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Post food failed", null);
                        return;
                    }
                    if(resultCode.equals(Global.JSON_RESULT_OK)){

                        int foods_id = jsonResultObj.getInt(Global.FOOD_ID_TAG);

                        if(foods_id > 0) {
                            Food food = new Food();
                            food.setFood_id(foods_id);
                            food.setFood_lat(foodLocation.latitude);
                            food.setFood_lon(foodLocation.longitude);
                            food.setFood_address(address);
                            food.setFood_promise_time(strTime);

                            Buyer buyer = new Buyer();
                            food.setBuyerInfo(buyer);

                            Global.g_Seller.setFoodInfo(food);
                            CommonFunc.NavigateActivity(mContext,MyFoodList.class);
                        }else{
                            CommonFunc.ShowAlertDialog(mContext, "Kwizeen", "Post food failed", null);
                            return;
                        }
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

    private String hashMapToUrl(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }


    //async task to upload image
    private class Upload extends AsyncTask<Void,Void,String>{
        private Bitmap image;
        private String name;
        ProgressDialog loading;

        public Upload(Bitmap image,String name){

            this.image = image;
            this.name = name;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(mContext, "Uploading Image", "Please wait...",true,true);
        }

        @Override
        protected String doInBackground(Void... params) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT);

            HashMap<String,String> detail = new HashMap<>();
            detail.put("name", name);
            detail.put("image", encodeImage);

            try{
                String dataToSend = hashMapToUrl(detail);
                String response = ImageRequest.post(Global.IMAGE_SERVER,dataToSend);
                return response;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            loading.dismiss();
            CommonFunc.ShowAlertDialog(mContext, "Kwizeen", s, null);
            editImage.setText(imageFileName);
        }
    }
}
