package com.example.ps.musicps.Helper;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import com.example.ps.musicps.Commen.Commen;
import com.example.ps.musicps.Model.Song;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class MusiPlayerHelper {

    private Timer timer;
    private float volumeOut = 1;
    private float volumeIn = 0;
    private float speed = 0.02f;
    public MediaPlayer mediaPlayer;
    public Song song;

    @Inject
    public MusiPlayerHelper() {
    }

    public Timer getTimer() {
        return timer;
    }

    public void setupMediaPLayer(Context context, Song song, final onMediaPlayerStateChanged onMediaPlayerStateChanged) {
        this.song = song;
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(context, Uri.parse(song.getTrackFile()));
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    onMediaPlayerStateChanged.onMediaPlayerPrepared();
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {

                    onMediaPlayerStateChanged.onMediaPlayerCompletion();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setLooping(true);
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public void FadeOut(final float deltaTime) {
        mediaPlayer.setVolume(volumeOut, volumeOut);
        volumeOut -= speed * deltaTime;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (volumeOut != 1 && !(volumeOut < 0)) {
                    FadeOut(deltaTime);
                } else if (volumeOut == 0 || volumeOut < 0) {
                    mediaPlayer.pause();
                    volumeOut = 1;
                }
            }
        }, 20);

    }

    public void FadeIn(final float deltaTime) {
        mediaPlayer.start();
        mediaPlayer.setVolume(volumeIn, volumeIn);
        volumeIn += speed * deltaTime;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (volumeIn != 1 && !(volumeIn > 1)) {
                    FadeIn(deltaTime);
                } else if (volumeIn == 1 || volumeIn > 1) {
                    volumeIn = 0;
                }
            }
        }, 20);

    }

    public interface onMediaPlayerStateChanged {
        void onMediaPlayerPrepared();

        void onMediaPlayerCompletion();
    }

}
