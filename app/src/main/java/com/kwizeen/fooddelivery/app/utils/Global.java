package com.kwizeen.fooddelivery.app.utils;

import android.app.Activity;

import com.kwizeen.fooddelivery.app.models.Buyer;
import com.kwizeen.fooddelivery.app.models.Seller;

/**
 * Created by admin on 12/17/15.
 */
public class Global {

    public static final boolean DEBUG = false;

    public static Seller g_Seller;
    public static Buyer g_Buyer;

    public static int g_buyerType;
    public static double g_Lat;
    public static double g_Lon;

    public static final String JSON_RESULT_OK = "ok";
    public static final String JSON_REULST_ERROR = "fail";
    public static final String JSON_REULST_EMAIL_DUPLICATE= "exist";
    public static final String RESULTCODE_TAG = "resultCode";

    public static final int RESULT_SELECT_IMAGE = 1;
    public static final String IMAGE_SERVER = "http://guglusharma.com/upload.php";

    public static final int STATUS_ENABLED = 1;     //1: Enable 0:Disable
    public static final int STATUS_NOT_DELETED = 0;     //0: Not deleted

    public static final String BROADCASTRECEIVER_FILTER_LOCATIONCHANGE = "com.masterteam.bernietaxi.LOCAITON_CHANGED";

    //public static final String WEBSERVICE_ADDR = "http://79.142.77.32/kwizeen/service/";
    public static final String WEBSERVICE_ADDR = "http://guglusharma.com/kwizeen/service/";

    public static final String SELLER_LOGIN_URL = WEBSERVICE_ADDR + "Seller/Login";
    public static final String SELLER_REGISTER_URL = WEBSERVICE_ADDR + "Seller/RegisterSeller";
    public static final String SELLER_INFO_UPDATE_URL = WEBSERVICE_ADDR + "Seller/UpdateSellerInfo";
    public static final String SELLER_PASSWORD_UPDATE_URL = WEBSERVICE_ADDR + "Seller/UpdateSellerPassword";
    public static final String SELLER_UPDATE_LOCATION_URL = WEBSERVICE_ADDR + "Seller/UpdateSellerLocation";
    public static final String SELLER_CREATE_CONTRACT_URL = WEBSERVICE_ADDR + "Seller/CreateContract";
    public static final String SELLER_GETMYFOODSINFO_URL = WEBSERVICE_ADDR + "Seller/GetMyFoodsInfo";
    public static final String SELLER_REMOVEMYFOODINFO_URL = WEBSERVICE_ADDR + "Seller/RemoveMyFoodInfo";
    public static final String SELLER_GETBUYERLISTBYFOOD_URL = WEBSERVICE_ADDR + "Seller/GetBuyerListByFood";
    public static final String SELLER_COMPLETECONTRACT_URL = WEBSERVICE_ADDR + "Seller/CompleteContract";

    public static final String SELLER_TAG = "seller";
    public static final String SELLER_ID_TAG = "seller_id";
    public static final String SELLER_NAME_TAG = "seller_name";
    public static final String SELLER_EMAIL_TAG = "seller_email";
    public static final String SELLER_LAT_TAG = "seller_lat";
    public static final String SELLER_LON_TAG = "seller_lon";
    public static final String SELLER_PHONE_TAG = "seller_phone";
    public static final String SELLER_ADDRESS_TAG = "seller_address";
    public static final String SELLER_REGDATE_TAG = "seller_regdate";
    public static final String SELLER_ENABLED_TAG = "seller_enabled";
    public static final String SELLER_DELETED_TAG = "seller_deleted";


    public static final String CONTACT_US_URL = WEBSERVICE_ADDR + "Contact";
    public static final String EMAIL_URL = WEBSERVICE_ADDR + "Email";

    public static final String BUYER_LOGIN_URL = WEBSERVICE_ADDR + "Buyer/Login";
    public static final String BUYER_REGISTER_URL = WEBSERVICE_ADDR + "Buyer/RegisterBuyer";
    public static final String BUYER_INFO_UPDATE_URL = WEBSERVICE_ADDR + "Buyer/UpdateBuyerInfo";
    public static final String BUYER_PASSWORD_UPDATE_URL = WEBSERVICE_ADDR + "Buyer/UpdateBuyerPassword";
    public static final String BUYER_UPDATE_LOCATION_URL = WEBSERVICE_ADDR + "Buyer/UpdateBuyerLocation";
    public static final String BUYER_CANCEL_CONTRACT_URL = WEBSERVICE_ADDR + "Buyer/CancelContract";
    public static final String BUYER_ACCEPT_CONTRACT_URL = WEBSERVICE_ADDR + "Buyer/AcceptContract";
    public static final String BUYER_COMPLETE_CONTRACT_URL = WEBSERVICE_ADDR + "Buyer/CompleteContract";
    public static final String BUYER_GET_CONTRACT_LIST_URL = WEBSERVICE_ADDR + "Buyer/GetContractList";
    public static final String BUYER_REMOVE_FOOD_FROM_CART_URL = WEBSERVICE_ADDR + "Buyer/RemoveFoodFromCart";
    public static final String BUYER_GET_MYCARTLIST_URL = WEBSERVICE_ADDR + "Buyer/GetMyCartList";
    public static final String BUYER_ADDFOODTOMYLIST_URL = WEBSERVICE_ADDR + "Buyer/AddFoodToMyList";

    public static final String FOOD_TAG = "food";
    public static final String FOOD_ID_TAG = "foods_id";
    public static final String FOOD_LAT_TAG = "foods_lat";
    public static final String FOOD_LON_TAG = "foods_lon";
    public static final String FOOD_PROMISE_TIME_TAG = "foods_promise_time";
    public static final String FOOD_ADDRESS_TAG = "foods_address";
    //public static final String FOOD_STATUS_TAG = "food_status";   //1:Created 2:Buyer Accept 3:Finishing 4:Finish 5:Paid
    public static final String FOOD_SELLER_ID_TAG = "foods_seller_id";
    public static final String FOOD_REGDATE = "foods_regdate";
    public static final String FOOD_ENABLED_TAG = "foods_enabled";
    public static final String FOOD_DELETED_TAG = "foods_deleted";

    public static final String FOOD_NAME_TAG = "foods_name";
    public static final String FOOD_NATLV_TAG = "foods_nutlv";
    public static final String FOOD_PRICE_TAG = "foods_price";
    public static final String FOOD_IMAGE_TAG = "foods_image";
    public static final String FOOD_ORDERNUM_TAG = "foods_ordernum";



    public static final String BUYER_TAG = "buyer";
    public static final String BUYER_ID_TAG = "buyer_id";
    public static final String BUYER_NAME_TAG = "buyer_name";
    public static final String BUYER_EMAIL_TAG = "buyer_email";
    public static final String BUYER_PHONE_TAG = "buyer_phone";
    public static final String BUYER_LAT_TAG = "buyer_lat";
    public static final String BUYER_LON_TAG = "buyer_lon";
    public static final String BUYER_REGDATE_TAG = "buyer_regdate";
    public static final String BUYER_ENABLED_TAG = "buyer_enabled";
    public static final String BUYER_DELETED_TAG = "buyer_deleted";

    public static final String _TAG = "";

    public static final int PAID_BUYER = 1;
    public static final int VOLUNTEER_BUYER = 0;

    public static final int GETREALBUYER_PERIOD = 30 * 1000;



    public static final int STATUS_FOOD_CREATED = 1;
    public static final int STATUS_FOOD_BUYER_ACCEPTED = 2;
    public static final int STATUS_FOOD_ONGOING = 3;        //Already Finish, but not paid, for only paid buyers
    public static final int STATUS_FOOD_FINISH = 4;         //Completely finish, only for volunteers
    public static final int STATUS_FOOD_PAID = 5;           //Paid, only for paid buyers

    public static final String ANDROID_DEVICE = "android";


/*
    */
}
