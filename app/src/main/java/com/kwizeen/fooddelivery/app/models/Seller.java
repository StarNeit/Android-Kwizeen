package com.kwizeen.fooddelivery.app.models;


/**
 * Created by admin on 12/18/15.
 */
public class Seller {
    private int seller_id;
    private String seller_name;
    private String seller_email;
    private double seller_lat;
    private double seller_lon;
    private String seller_phone;
    private String seller_address;
    private String seller_regdate;

    private Food foodInfo;

    public Seller() {
    }

    public Seller(int seller_id, String seller_name, String seller_email, long seller_lat, long seller_lon, String seller_phone,
                  String seller_address, Food foodInfo) {
        this.seller_id = seller_id;
        this.seller_name = seller_name;
        this.seller_email = seller_email;
        this.seller_lat = seller_lat;
        this.seller_lon = seller_lon;
        this.seller_phone = seller_phone;
        this.seller_address = seller_address;
        this.foodInfo = foodInfo;
    }

    public int getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(int seller_id) {
        this.seller_id = seller_id;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public String getSeller_email() {
        return seller_email;
    }

    public void setSeller_email(String seller_email) {
        this.seller_email = seller_email;
    }

    public double getSeller_lat() {
        return seller_lat;
    }

    public void setSeller_lat(double seller_lat) {
        this.seller_lat = seller_lat;
    }

    public double getSeller_lon() {
        return seller_lon;
    }

    public void setSeller_lon(double seller_lon) {
        this.seller_lon = seller_lon;
    }

    public String getSeller_phone() {
        return seller_phone;
    }

    public void setSeller_phone(String seller_phone) {
        this.seller_phone = seller_phone;
    }

    public String getSeller_address() {
        return seller_address;
    }

    public void setSeller_address(String seller_address) {
        this.seller_address = seller_address;
    }

    public Food getFoodInfo() {
        return foodInfo;
    }

    public void setFoodInfo(Food foodInfo) {
        this.foodInfo = foodInfo;
    }

    public String getSeller_regdate() {
        return seller_regdate;
    }

    public void setSeller_regdate(String seller_regdate) {
        this.seller_regdate = seller_regdate;
    }
}
