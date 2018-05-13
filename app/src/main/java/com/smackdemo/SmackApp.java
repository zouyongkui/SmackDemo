package com.smackdemo;

import android.app.Application;

public class SmackApp extends Application {
    private static SmackApp smackApp;

    @Override
    public void onCreate() {
        super.onCreate();
        smackApp = this;
    }

    public static SmackApp getInstance() {
        return smackApp;
    }
}
