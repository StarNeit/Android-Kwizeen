package com.kwizeen.fooddelivery.app.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

/*import com.masterteam.bernietaxi.app.main.PassengerSettingsActivity;
import com.masterteam.bernietaxi.app.main.PassengerSignUpActivity;
import com.masterteam.bernietaxi.app.main.R;*/
import com.kwizeen.fooddelivery.app.main.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;


/**
 * Created by admin on 10/24/15.
 */
public class CommonFunc {

    public static final String LOG_TAG = "bernietaxi_tag";

    public static String leadZero(int k){
        if(k < 10) return "0" + k;
        else return String.valueOf(k);
    }

    /***
     * Return simple progress dialog
     * @param mContext: context of current activity
     * @return Handle of ProgressDialog
     */
    public static ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_simple_progress_dialog);
        return dialog;
    }

    /***
     * return progressdialog have title and content.
     * @param title
     * @param message
     * @param mContext
     * @return
     */
    public static ProgressDialog createProgressDialog(String title, String message, Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        return dialog;
    }

    public static void ShowToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void AppLog(String log){
        if(Global.DEBUG)
            Log.d(LOG_TAG, log);
    }

    public static void ShowAlertDialog(Context context, int  titleId, int messageId, int iconId){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, R.style.MyInfoAppCompatAlertDialogStyle);
        builder.setTitle(context.getString(titleId))
                .setMessage(context.getString(messageId))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(iconId)
                .show();
    }

    public static void ShowAlertDialog(Context context, String title, String message, DialogInterface.OnClickListener listener){
        if(listener == null){
            listener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("OK", listener);
        alert.show();
    }

    public static String GetMD5String(String password){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(password.getBytes());

        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static void NavigateActivity(Context context, Class<?> cls){
        Intent intent = new Intent(context, cls);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        ((Activity)context).finish();
    }

    public static void NavigateActivityWithOutFinish(Context context, Class<?> cls){
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    public static String GetAddressFromLatLng(Context context, double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(context, Locale.US);
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                for(int i = 0; i < address.getMaxAddressLineIndex(); i++){
                    result.append(address.getAddressLine(i)).append(" ");
                }
//                result.append(address.getLocality()).append("\n");
//                result.append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        return result.toString();
    }
}
