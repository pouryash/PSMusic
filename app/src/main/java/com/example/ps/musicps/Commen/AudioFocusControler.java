package com.example.ps.musicps.Commen;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import com.example.ps.musicps.Helper.MusiPlayerHelper;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class AudioFocusControler implements AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "AUDIOFOCUS";
    public static onAudioFocusChange onAudioFocusChange;
    public AudioFocusRequest focusRequest;


    @Inject
    public AudioFocusControler(@Named("context") Context context) {

    }

    public void RequestAudio() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            AudioAttributes playbackAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(playbackAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(this)
                    .build();

            MyApplication.getAudioManager().requestAudioFocus(focusRequest);
        }


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {

            MyApplication.getAudioManager().requestAudioFocus(this,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    public void abandonFocus() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            MyApplication.getAudioManager().abandonAudioFocusRequest(focusRequest);
            return;
        }
        MyApplication.getAudioManager().abandonAudioFocus(AudioFocusControler.this);
    }

    @Override
    public void onAudioFocusChange(int i) {

        switch (i) {
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.i(TAG, "AUDIOFOCUS_GAIN");
                break;
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                Log.e(TAG, "AUDIOFOCUS_LOSS");
                break;
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                Log.i(TAG, "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK");
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                onAudioFocusChange.onFocusLoss();
                abandonFocus();
                Log.e(TAG, "AUDIOFOCUS_LOSS");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                onAudioFocusChange.onFocusLoss();
                Log.e(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.e(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                break;
            case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                Log.e(TAG, "AUDIOFOCUS_REQUEST_FAILED");
                break;
        }


    }


    public interface onAudioFocusChange {
        void onFocusLoss();
    }
}
