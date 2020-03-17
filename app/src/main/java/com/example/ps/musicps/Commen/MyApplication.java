package com.example.ps.musicps.Commen;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import com.example.ps.musicps.Di.component.AppComponent;
import com.example.ps.musicps.Di.component.DaggerAppComponent;

public class MyApplication extends Application {

    private AppComponent component;
    private static AudioManager am;
    static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.am = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
        context = getApplicationContext();

        component = DaggerAppComponent.builder().context(context).build();
    }

    public static AudioManager getAudioManager() {
        return am;
    }

    public static Context getAppContext() {
        return context;
    }

    public AppComponent getComponent() {
        return component;
    }
}
