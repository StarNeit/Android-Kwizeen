package com.kwizeen.fooddelivery.app.HttpUtils;

import java.net.HttpURLConnection;

/***
 * Abstract class of HttpHandler
 */
public abstract class CCHttpHandler {

    public abstract HttpURLConnection getHttpRequestMethod();

    public abstract void onResponse(String result);

    public void execute(){
        new CCJsonAsyncTask(this).execute();
    } 
}