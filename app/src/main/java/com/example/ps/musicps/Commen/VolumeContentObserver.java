package com.example.ps.musicps.Commen;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import com.example.ps.musicps.viewmodels.SongPanelViewModel;

import javax.inject.Inject;

public class VolumeContentObserver extends ContentObserver {

    private int lastVolum;
    private AudioManager audioManager;
    SongPanelViewModel songPanelViewModel;

    @Inject
    public VolumeContentObserver(Context context) {
        super(new Handler());

        context.getApplicationContext().getContentResolver().registerContentObserver(
                android.provider.Settings.System.CONTENT_URI, true,
                this);

        this.audioManager =  (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

            songPanelViewModel.setAudioManagerProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public void setSongPanelViewModel(SongPanelViewModel songPanelViewModel) {
        this.songPanelViewModel = songPanelViewModel;

        songPanelViewModel.setAudioManagerMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        songPanelViewModel.setAudioManagerProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    public void setVolume(int volume){
        this.getAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC,
                volume, 0);
    }

    public int getLastVolum() {
        return lastVolum;
    }

    public void setLastVolum(int lastVolum) {
        this.lastVolum = lastVolum;
    }
}
