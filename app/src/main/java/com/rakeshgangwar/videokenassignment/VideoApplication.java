package com.rakeshgangwar.videokenassignment;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by dell on 5/26/2017.
 */

public class VideoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(getApplicationContext());
    }
}
