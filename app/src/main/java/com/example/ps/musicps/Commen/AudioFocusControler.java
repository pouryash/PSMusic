package com.example.ps.musicps.Commen;

import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;

public class AudioFocusControler implements AudioManager.OnAudioFocusChangeListener {

    public onAudioFocusChangeService onAudioFocusChangeService;
    public onAudioFocusChangePlay onAudioFocusChangePlay;
    private static AudioFocusControler instance;
    AudioFocusRequest  focusRequest;


    private AudioFocusControler(){

    }

    static {
        instance = new AudioFocusControler();
    }

    public static AudioFocusControler getInstance(){
        return instance;
    }


    public void initAudio(){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            AudioAttributes playbackAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
                    .setAudioAttributes(playbackAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(this)
                    .build();

            MyApplication.getAudioManager().requestAudioFocus(focusRequest);
        }
    }


    @Override
    public void onAudioFocusChange(int i) {
        Commen.mediaPlayer.pause();
        if (onAudioFocusChangeService != null){
            onAudioFocusChangeService.onServiceFocusChange();
        }
        if (onAudioFocusChangePlay != null){
            onAudioFocusChangePlay.onPlaySongFocusChange();
        }

    }


    public interface onAudioFocusChangeService {
        void onServiceFocusChange();
    }
    public interface onAudioFocusChangePlay{
        void onPlaySongFocusChange();
    }
}
