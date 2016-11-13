package com.kwizeen.fooddelivery.app.models;

/**
 * Created by admin on 12/18/15.
 */
public class Buyer {

    private int buyer_id;
    private String buyer_name;
    private String buyer_email;
    private String buyerPhone;
    private double buyer_lat;
    private double buyer_lon;
    private String buyer_regdate;

    private Food foodInfo;

    public Buyer() {
    }

    public Buyer(int buyer_id, String buyer_name, String buyer_email, String buyerPhone, String buyer_license_card, String buyer_paypal, long buyer_lat, long buyer_lon,
                 String buyer_regdate, Food foodInfo) {
        this.buyer_id = buyer_id;
        this.buyer_name = buyer_name;
        this.buyer_email = buyer_email;
        this.buyerPhone = buyerPhone;
        this.buyer_lat = buyer_lat;
        this.buyer_lon = buyer_lon;
        this.buyer_regdate = buyer_regdate;
        this.foodInfo = foodInfo;
    }

    public int getBuyer_id() {
        return buyer_id;
    }

    public void setBuyer_id(int buyer_id) {
        this.buyer_id = buyer_id;
    }

    public String getBuyer_name() {
        return buyer_name;
    }

    public void setBuyer_name(String buyer_name) {
        this.buyer_name = buyer_name;
    }

    public String getBuyer_email() {
        return buyer_email;
    }

    public void setBuyer_email(String buyer_email) {
        this.buyer_email = buyer_email;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public double getBuyer_lat() {
        return buyer_lat;
    }

    public void setBuyer_lat(double buyer_lat) {
        this.buyer_lat = buyer_lat;
    }

    public double getBuyer_lon() {
        return buyer_lon;
    }

    public void setBuyer_lon(double buyer_lon) {
        this.buyer_lon = buyer_lon;
    }

    public String getBuyer_regdate() {
        return buyer_regdate;
    }

    public void setBuyer_regdate(String buyer_regdate) {
        this.buyer_regdate = buyer_regdate;
    }

    public Food getFoodInfo() {
        return foodInfo;
    }

    public void setFoodInfo(Food foodInfo) {
        this.foodInfo = foodInfo;
    }
}
