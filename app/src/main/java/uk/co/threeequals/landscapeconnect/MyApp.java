package uk.co.threeequals.landscapeconnect;

import android.content.Intent;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import net.gotev.uploadservice.UploadService;

/**
 * Base class for the App which initialises all of the libraries
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

