package uk.co.threeequals.landscapeconnect;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

/**
 * Created by michaelwaterworth on 10/06/2016. Copyright Michael Waterworth
 */

public class LCLog {
    public static void i(String TAG, String msg){
        Log.i(TAG, msg);
        FirebaseCrash.log("i: " + TAG + " : " +  msg);
    }

    public static void d(String TAG, String msg){
        Log.d(TAG, msg);
        FirebaseCrash.log("d: " + TAG + " : " +  msg);
    }

    public static void e(String TAG, String msg, Exception e){
        Log.e(TAG, msg, e);
        FirebaseCrash.log("e: " + TAG + " : " +  msg);
        FirebaseCrash.report(e);
    }

    public static void e(String TAG, String msg){
        Log.e(TAG, msg);
        FirebaseCrash.log("e: " + TAG + " : " +  msg);
    }
}

