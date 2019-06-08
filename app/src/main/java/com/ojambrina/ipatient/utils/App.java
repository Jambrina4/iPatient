package com.ojambrina.ipatient.utils;

import android.app.Application;

public class App extends Application {

    private static final String FATAL_NO_INSTANCE = "Fatal Error: No App instance found";

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        //Fabric.with(this, new Crashlytics());
        instance = this;
    }

    public static synchronized App getInstance() {
        if (instance == null) {
            throw new IllegalArgumentException(FATAL_NO_INSTANCE);
        }
        return instance;
    }

}
