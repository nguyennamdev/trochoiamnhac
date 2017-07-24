package com.wordpress.nguyenvannamdev.trochoiamnhac;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by NAM COI on 3/2/2017.
 */

public class CheckInternet {
    private static CheckInternet instance = new CheckInternet();
    private static Context context;
    private ConnectivityManager connectivityManager;

    public static CheckInternet getInstance(Context ctx) {
        context = ctx.getApplicationContext();
        return instance;
    }



    public boolean isConnected() {
       ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if(connectivityManager  != null){
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null){
                if(networkInfo.getState() == NetworkInfo.State.CONNECTED){
                    return true;
                }
            }
        }
        return false;
    }
}
