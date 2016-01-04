package com.example.michaelwaterworth.testqdownloader;

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
    }
}

