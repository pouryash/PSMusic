package com.example.ps.musicps.Commen;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.ps.musicps.Di.component.AppComponent;
import com.example.ps.musicps.Di.component.DaggerAppComponent;

public class MyApplication extends Application {

    public static boolean canPlayFaverate;
    public static int LIST_STATE = 0;
    public static int FAVERATE_STATE = 1;
    int state;
    private AppComponent component;
    private static AudioManager am;
    static Context context;
    public static boolean isExternalSource = false;

    public void onCreate() {
        super.onCreate();
        MyApplication.am = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
        context = getApplicationContext();

        component = DaggerAppComponent.builder().context(context).build();

        AppCompatDelegate.setDefaultNightMode(component.getSharedPrefrence().getNightMode());

        canPlayFaverate = component.getSharedPrefrence().getSong().getIsFaverate() == 1;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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
