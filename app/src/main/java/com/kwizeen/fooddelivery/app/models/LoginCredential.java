package com.kwizeen.fooddelivery.app.models;


/**
 * Created by admin on 12/17/15.
 */
public class LoginCredential {

    private String email;
    private String password;

    public LoginCredential() {
        email = ""; password = "";
    }

    public LoginCredential(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
