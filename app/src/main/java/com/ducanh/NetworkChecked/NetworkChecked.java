package com.ducanh.NetworkChecked;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkChecked {
    Context context;
    public NetworkChecked(Context context) {
        this.context = context;
    }

    public boolean isNetworkConnected() {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo () != null && cm.getActiveNetworkInfo ().isConnected ();
    }

}
