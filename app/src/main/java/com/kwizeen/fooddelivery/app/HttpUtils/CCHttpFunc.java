package com.kwizeen.fooddelivery.app.HttpUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by admin on 12/17/15.
 */
public class CCHttpFunc {

    private final static String USER_AGENT = "Mozilla/5.0";

    public static HttpURLConnection GetHttpRequestMethod(String url){

        HttpURLConnection httpURLConnection = null;
        try {
            URL obj = new URL(url);
            httpURLConnection = (HttpURLConnection)obj.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(10000);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  httpURLConnection;
    }

    public static HttpURLConnection PostHttpRequestMethod(String url, String urlParameters){

        HttpURLConnection httpURLConnection = null;
        try {
            URL obj = new URL(url);
            httpURLConnection = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);
            httpURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            // Send post request
            httpURLConnection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return httpURLConnection;
    }
}
