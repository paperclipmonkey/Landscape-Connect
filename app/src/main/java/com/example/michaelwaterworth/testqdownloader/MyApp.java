package com.example.michaelwaterworth.testqdownloader;

import android.content.Intent;

import com.alexbbb.uploadservice.UploadService;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

/**
 * Created by michaelwaterworth on 04/01/2016. Copyright Michael Waterworth
 */
public class MyApp extends com.activeandroid.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new FontAwesomeModule());

        // setup the broadcast action namespace string which will
        // be used to notify upload status.
        // Gradle automatically generates proper variable as below.
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;

        //Start the upload service...
        Intent serviceIntent = new Intent(this, LSUploadService.class);
        getApplicationContext().startService(serviceIntent);
    }
}

