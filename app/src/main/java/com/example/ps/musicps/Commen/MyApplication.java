package com.example.ps.musicps.Commen;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;

public class MyApplication extends Application {

    private static AudioManager am;
    static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.am = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
        context = getApplicationContext();
    }

    public static AudioManager getAudioManager() {
        return am;
    }

    public static Context getAppContext(){
       return context;
    }

}
