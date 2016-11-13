package com.kwizeen.fooddelivery.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.kwizeen.fooddelivery.app.models.Buyer;
import com.kwizeen.fooddelivery.app.models.LoginCredential;
import com.kwizeen.fooddelivery.app.models.Seller;

/**
 * Created by admin on 12/17/15.
 */
public class SharedPreferencesTool {

    private Context context;

    private static final String PREFERENCES_IDENTIFIER = "BernieTaxi";

    public static final int SELLER = 1;
    public static final int BUYER = 2;

    private static final String SELLER_EMAIL_IDENTIFIER = "p_email";
    private static final String SELLER_PASSWORD_IDENTIFIER = "p_password";

    private static final String BUYER_EMAIL_IDENTIFIER = "d_email";
    private static final String BUYER_PASSWORD_IDENTIFIER = "d_password";

    public SharedPreferencesTool(Context context) {
        this.context = context;
    }

    public LoginCredential GetLoginCredentials(int loginType){
        LoginCredential lc = new LoginCredential();

        SharedPreferences bernieMapSharePreference = context.getSharedPreferences(PREFERENCES_IDENTIFIER, context.MODE_PRIVATE);
        String email = "", password = "";

        if(loginType == SELLER) {
            email = bernieMapSharePreference.getString(SELLER_EMAIL_IDENTIFIER, "");
            password = bernieMapSharePreference.getString(SELLER_PASSWORD_IDENTIFIER, "");
        }else if(loginType == BUYER){
            email = bernieMapSharePreference.getString(BUYER_EMAIL_IDENTIFIER, "");
            password = bernieMapSharePreference.getString(BUYER_PASSWORD_IDENTIFIER, "");
        }
        lc.setEmail(email);
        lc.setPassword(password);
        return lc;
    }

    public void SaveCredentials(LoginCredential lc, int loginType){
        if(lc == null) lc = new LoginCredential();

        SharedPreferences bernieMapSharePreference = context.getSharedPreferences(PREFERENCES_IDENTIFIER, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = bernieMapSharePreference.edit();

        if(loginType == SELLER){
            editor.putString(SELLER_EMAIL_IDENTIFIER, lc.getEmail());
            editor.putString(SELLER_PASSWORD_IDENTIFIER, lc.getPassword());
        }else if(loginType == BUYER){
            editor.putString(BUYER_EMAIL_IDENTIFIER, lc.getEmail());
            editor.putString(BUYER_PASSWORD_IDENTIFIER, lc.getPassword());
        }

        editor.commit();
    }

    public void EmptyCredentials(int loginType){
        SharedPreferences bernieMapSharePreference = context.getSharedPreferences(PREFERENCES_IDENTIFIER, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = bernieMapSharePreference.edit();

        if(loginType == SELLER){
            editor.putString(SELLER_EMAIL_IDENTIFIER, "");
            editor.putString(SELLER_PASSWORD_IDENTIFIER, "");
        }else if(loginType == BUYER){
            editor.putString(BUYER_EMAIL_IDENTIFIER, "");
            editor.putString(BUYER_PASSWORD_IDENTIFIER, "");
        }
        editor.commit();
    }
}
