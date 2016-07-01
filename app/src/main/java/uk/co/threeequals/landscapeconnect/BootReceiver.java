package uk.co.threeequals.landscapeconnect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Load on device reboot. Starts the Upload Service to check for pending uploads
 */
public class BootReceiver extends BroadcastReceiver {
    private static String TAG = "BootReceiver";

    /**
     * Called whenever the device reboots. Ensures the service is running.
     *
     * @param context Android context for start
     * @param intent  Intent used to fire broadcast
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.i(TAG, "BootReceiver");

            //Start the upload service
            Intent serviceIntent = new Intent(context, LSUploadService.class);
            context.startService(serviceIntent);
        }
    }
}