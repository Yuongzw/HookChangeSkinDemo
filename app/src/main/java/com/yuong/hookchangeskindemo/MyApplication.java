package com.yuong.hookchangeskindemo;

import android.app.Application;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SkinEngine.getInstance().init(this);
    }
}
