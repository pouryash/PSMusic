package com.example.ps.musicps.Commen;

import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;

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


        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){

          MyApplication.getAudioManager().requestAudioFocus(this,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
        }
    }


    @Override
    public void onAudioFocusChange(int i) {
        //TODO
        Commen.getInstance().FadeOut(2);
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
