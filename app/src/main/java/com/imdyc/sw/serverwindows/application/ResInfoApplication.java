package com.imdyc.sw.serverwindows.application;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by 邓远超 on 2018/5/11.
 */
public class ResInfoApplication extends Application{
    public static RequestQueue requestQueue;

    @Override
    public void onCreate(){
        super.onCreate();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    public static  RequestQueue getHttpQueues(){
        return  requestQueue;
    }
}
