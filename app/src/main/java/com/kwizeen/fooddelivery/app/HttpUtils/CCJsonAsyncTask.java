package com.kwizeen.fooddelivery.app.HttpUtils;


import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by admin on 10/24/15.
 *  http://developer.android.com/reference/android/os/AsyncTask.html
 */
public class CCJsonAsyncTask extends AsyncTask<String, Void, String>{

    private CCHttpHandler httpHandler;

    public CCJsonAsyncTask(CCHttpHandler httpHandler){
        this.httpHandler = httpHandler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String...params) {

        String responseJsonStr = "";

        HttpURLConnection httpURLConnection = httpHandler.getHttpRequestMethod();   //Send http request
        try {
            int responseCode = httpURLConnection.getResponseCode();                 //get responseCode from server
            //Read stream to buffer
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            //Change buffer to String
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            responseJsonStr = response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseJsonStr;
    }

    //Callback method of when receive response.
    @Override
    protected void onPostExecute(String result) {
        httpHandler.onResponse(result.trim());
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

}
