package com.example.ps.musicps.Commen;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.ps.musicps.Model.Song;

import java.io.IOException;


public class Commen {

    private static Commen instance;
    public static MediaPlayer mediaPlayer;
    public static Song song;
    float volumeOut = 1;
    float volumeIn = 0;
    float speed = 0.02f;


    private Commen(){

    }

    static {
        instance = new Commen();
    }

    public static Commen getInstance(){
        return instance;
    }



    public void setupMediaPLayer(Context context,Song song, final onMediaPlayerStateChanged onMediaPlayerStateChanged) {
        Commen.song = song;
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

    public static Bitmap getBitmapFromVectorDrawable(Context context, Drawable drawableId) {
        Drawable drawable = drawableId;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static boolean isServiceRunning(Class<?> serviceClass , Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void FadeOut(final float deltaTime)
    {
        mediaPlayer.setVolume(volumeOut, volumeOut);
        volumeOut -= speed* deltaTime;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (volumeOut != 1 && !(volumeOut < 0)){
                    FadeOut(deltaTime);
                }else if (volumeOut == 0 || volumeOut < 0) {
                    mediaPlayer.pause();
                    volumeOut = 1;
                }
            }
        },20);
    }

    public void FadeIn(final float deltaTime)
    {
        mediaPlayer.start();
        mediaPlayer.setVolume(volumeIn, volumeIn);
        volumeIn += speed* deltaTime;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (volumeIn != 1 && !(volumeIn > 1)){
                    FadeIn(deltaTime);
                }else if (volumeIn == 1 || volumeIn > 1) {
                    volumeIn = 0;
                }
            }
        },20);
    }

    public interface onMediaPlayerStateChanged{
        void onMediaPlayerPrepared();
        void onMediaPlayerCompletion();
    }
}
