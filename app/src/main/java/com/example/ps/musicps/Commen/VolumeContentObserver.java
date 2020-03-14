//package com.example.ps.musicps.Commen;
//
//import android.database.ContentObserver;
//import android.media.AudioManager;
//import android.os.Handler;
//
//import com.example.ps.musicps.View.PlaySongActivity;
//
//public class VolumeContentObserver extends ContentObserver {
//
//    public VolumeContentObserver(Handler handler) {
//        super(handler);
//    }
//
//    @Override
//    public boolean deliverSelfNotifications() {
//        return super.deliverSelfNotifications();
//    }
//
//    @Override
//    public void onChange(boolean selfChange) {
//        super.onChange(selfChange);
//        if (PlaySongActivity.audioManager != null) {
//            PlaySongActivity.volumeSeekBar.setProgress(PlaySongActivity.audioManager
//                    .getStreamVolume(AudioManager.STREAM_MUSIC));
//        }
//    }
//}
