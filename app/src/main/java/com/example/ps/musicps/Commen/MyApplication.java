package com.example.ps.musicps.Commen;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.ps.musicps.Di.component.AppComponent;
import com.example.ps.musicps.Di.component.DaggerAppComponent;

public class MyApplication extends Application {

    private AppComponent component;
    private static AudioManager am;
    static Context context;
    public static boolean isExternalSource = false;

    public void onCreate() {
        super.onCreate();
        MyApplication.am = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
        context = getApplicationContext();

        component = DaggerAppComponent.builder().context(context).build();

        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
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
