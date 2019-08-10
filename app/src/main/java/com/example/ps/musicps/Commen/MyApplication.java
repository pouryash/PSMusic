package com.example.ps.musicps.Commen;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;

public class MyApplication extends Application {

    private static AudioManager am;

    public void onCreate() {
        super.onCreate();
        MyApplication.am = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
    }

    public static AudioManager getAudioManager() {
        return am;
    }

}
