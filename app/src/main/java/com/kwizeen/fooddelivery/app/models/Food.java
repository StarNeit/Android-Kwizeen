package com.kwizeen.fooddelivery.app.models;

/**
 * Created by admin on 12/18/15.
 */
public class Food {
    private int food_id;
    private double food_lat;
    private double food_lon;
    private String food_promise_time;
    private String food_address;
    private String food_regdate;

    private int food_seller_id;
    private String foods_name;
    private double foods_nutlv;
    private double foods_price;
    private String foods_image;
    private int foods_ordernum;

    private int contract_status;

    private Seller sellerInfo;
    private Buyer buyerInfo;

    public Food() {
    }

    public int getFood_contract_status(){return contract_status;}
    public void setFood_contract_status(int contract_status){
        this.contract_status = contract_status;
    }

    public String getFood_regdate() {
        return food_regdate;
    }

    public void setFood_regdate(String food_regdate) {
        this.food_regdate = food_regdate;
    }

    public int getFood_seller_id() {
        return food_seller_id;
    }

    public void setFood_seller_id(int food_seller_id) {
        this.food_seller_id = food_seller_id;
    }

    public String getFood_name() {
        return foods_name;
    }

    public void setFood_name(String foods_name) {
        this.foods_name = foods_name;
    }

    public double getFood_nutlv() {
        return foods_nutlv;
    }

    public void setFood_nutlv(double foods_nutlv) {
        this.foods_nutlv = foods_nutlv;
    }

    public double getFood_price() {
        return foods_price;
    }

    public void setFood_price(double foods_price) {
        this.foods_price = foods_price;
    }

    public void setFood_image(String foods_image) {
        this.foods_image = foods_image;
    }

    public String getFood_image() {
        return foods_image;
    }

    public void setFood_ordernum(int foods_ordernum){
        this.foods_ordernum = foods_ordernum;
    }
    public int getFood_ordernum(){
        return foods_ordernum;
    }

    public int getFood_id() {
        return food_id;
    }

    public void setFood_id(int food_id) {
        this.food_id = food_id;
    }

    public double getFood_lat() {
        return food_lat;
    }

    public void setFood_lat(double food_lat) {
        this.food_lat = food_lat;
    }

    public double getFood_lon() {
        return food_lon;
    }

    public void setFood_lon(double food_lon) {
        this.food_lon = food_lon;
    }

    public String getFood_promise_time() {
        return food_promise_time;
    }

    public void setFood_promise_time(String food_promise_time) {
        this.food_promise_time = food_promise_time;
    }

    public String getFood_address() {
        return food_address;
    }

    public void setFood_address(String food_address) {
        this.food_address = food_address;
    }

    public Seller getSellerInfo() {
        return sellerInfo;
    }

    public void setSellerInfo(Seller sellerInfo) {
        this.sellerInfo = sellerInfo;
    }

    public Buyer getBuyerInfo() {
        return buyerInfo;
    }

    public void setBuyerInfo(Buyer buyerInfo) {
        this.buyerInfo = buyerInfo;
    }
}
