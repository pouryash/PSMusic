package com.example.ps.musicps.Commen;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.ps.musicps.Helper.MusiPlayerHelper;


public class OnAppCleared extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ClearFromRecentService", "Service Destroyed");
//        stopService(new Intent(MyApplication.context, SongService.class));
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearFromRecentService", "END");
        MusiPlayerHelper musiPlayerHelper = ((MyApplication)getApplication()).getComponent().getMusicPlayerHelper();
        SongSharedPrefrenceManager songSharedPrefrenceManager = ((MyApplication)getApplication()).getComponent().getSharedPrefrence();

        songSharedPrefrenceManager.setPlayingState("repeatOne");

        if (musiPlayerHelper.getTimer() != null) {
            musiPlayerHelper.getTimer().purge();
            musiPlayerHelper.getTimer().cancel();
            musiPlayerHelper.setTimer(null);
        }
        if (musiPlayerHelper.mediaPlayer != null) {
            musiPlayerHelper.mediaPlayer.release();
            musiPlayerHelper.mediaPlayer = null;
        }
        MyApplication.isExternalSource = false;
        stopSelf();
    }
}
