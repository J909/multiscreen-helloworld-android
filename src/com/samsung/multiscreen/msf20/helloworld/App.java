package com.samsung.multiscreen.msf20.helloworld;

import android.app.Application;

/**
 * @author plin
 *
 * The Hello World app.
 * 
 */
public class App extends Application {
    public static final String TAG = App.class.getName();

    private static App instance;
    private static Config config;

    private MSHelloWorldWebApplication msHelloWorld;
    public static App getInstance() {
        return instance;
    }

    public App() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        config = Config.newInstance(this);

        msHelloWorld = MSHelloWorldWebApplication.getInstance(this);
    }

    public Config getConfig() {
        return config;
    }

    public MSHelloWorldWebApplication getHelloWorldWebApplication() {
        return msHelloWorld;
    }
}
