package uk.co.threeequals.landscapeconnect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Load on device network changes. Starts the upload manager if has internet
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    private static String TAG = "NetworkChangeReceiver";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if(isOnline(context)){
            //Start the upload service
            Log.i(TAG, "NetworkChangeReceiver");
            Intent serviceIntent = new Intent(context, LSUploadService.class);
            context.startService(serviceIntent);
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }
}