package com.silencedut.concurrentstudy;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by SilenceDut on 16/7/23.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);

    }
}
