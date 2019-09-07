package com.example.ps.musicps.Commen;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.ps.musicps.Model.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Commen {

    public static boolean IS_PLAYING;
    private static final int WRITE_SETTINGS_REQUEST = 10;
    private static Commen instance;
    public static MediaPlayer mediaPlayer;
    public static Song song;
    float volumeOut = 1;
    float volumeIn = 0;
    float speed = 0.02f;


    private Commen() {

    }

    static {
        instance = new Commen();
    }

    public static Commen getInstance() {
        return instance;
    }


    public void setupMediaPLayer(Context context, Song song, final onMediaPlayerStateChanged onMediaPlayerStateChanged) {
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

    public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void FadeOut(final float deltaTime) {
        IS_PLAYING = false;
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
            IS_PLAYING = true;
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

    public static List<Song> notifyListchanged(int pos, List<Song> songs) {
        List<Song> songList = new ArrayList<>();

        for (Song song : songs) {
            if (song.getId() >= pos + 1) {
                int id = song.getId();
                song.setId(id - 1);
                songList.add(song);
            } else {
                songList.add(song);
            }
        }
        return songList;
    }

    public void writeSettingEnabled(Activity context) {

        if (!Settings.System.canWrite(context)) {
            writeSettingAlertMessage(context);

        }
    }

    public void writeSettingAlertMessage(final Activity context) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("We need write Setting permision, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        context.startActivityForResult(intent, WRITE_SETTINGS_REQUEST);
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public interface onMediaPlayerStateChanged {
        void onMediaPlayerPrepared();

        void onMediaPlayerCompletion();
    }
}
