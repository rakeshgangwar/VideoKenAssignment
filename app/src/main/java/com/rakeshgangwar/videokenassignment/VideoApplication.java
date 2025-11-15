package com.rakeshgangwar.videokenassignment;

import android.app.Application;

import io.realm.Realm;

/**
 * VideoApplication is the custom Application class for the VideoKen Assignment app.
 * This class is instantiated before any other component and is used for application-level
 * initialization tasks.
 *
 * Primary responsibility:
 * - Initialize the Realm database framework on app startup
 *
 * This class is declared in AndroidManifest.xml using the android:name attribute
 * in the <application> tag.
 *
 * @author Rakesh Gangwar
 * @version 1.0
 */
public class VideoApplication extends Application {

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects have been created.
     *
     * This method initializes the Realm database with the application context,
     * which is required before any Realm instance can be created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Realm database framework
        Realm.init(getApplicationContext());
    }
}
