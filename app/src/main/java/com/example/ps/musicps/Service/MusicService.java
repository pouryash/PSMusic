package com.example.ps.musicps.Service;

import android.app.Activity;
import android.app.ListActivity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.ps.musicps.Commen.Commen;
import com.example.ps.musicps.Commen.MyApplication;
import com.example.ps.musicps.Commen.SongSharedPrefrenceManager;
import com.example.ps.musicps.Helper.MusiPlayerHelper;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.R;


public class MusicService extends Service implements MusiPlayerHelper.onMediaPlayerStateChanged {

    private static final String ACTION_PLAY = "com.example.ps.musicps.PLAY_MUSIC";
    private static final String ACTION_NEXT = "com.example.ps.musicps.NEXT_MUSIC";
    private static final String ACTION_PREVIOUS = "com.example.ps.musicps.PREVIOUS_MUSIC";
    private final static String CHANNEL_DESCRIPTION_SONG = "channel_description_Song";
    private final static String NOTIFICATION_CHANNEL = "channel_name";
    private final static int NOTIFICATION_ID = 101;

    private Bitmap musicAlbum;
    private final IBinder mBinder = new ServiceBinder();
    public static Callbacks callbacks;
    private NotificationCompat.Builder notification;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Action[] playPauseAction;
    MusiPlayerHelper musiPlayerHelper;
    SongSharedPrefrenceManager sharedPrefrenceManager;

    @Override
    public void onCreate() {
        super.onCreate();
        musiPlayerHelper = ((MyApplication) getApplication()).getComponent().getMusicPlayerHelper();
        sharedPrefrenceManager = ((MyApplication) getApplication()).getComponent().getSharedPrefrence();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ServiceBinder extends Binder {
        public MusicService getMusicService() {
            return MusicService.this;
        }
    }

    public void registerClient(Activity activity, LifecycleOwner lifecycleOwner) {
        this.callbacks = (Callbacks) activity;

        sharedPrefrenceManager.getSharedPrefsSongLiveData(SongSharedPrefrenceManager.KEY_SONG_MODEL).
                getSongLiveData(SongSharedPrefrenceManager.KEY_SONG_MODEL, new Song()).
                observeForever(song -> onContentChanged());
    }

    public void onPlayPauseClicked() {
        if (musiPlayerHelper.mediaPlayer.isPlaying()) {

            notification.mActions.set(1, playPauseAction[0]);
            stopForeground(false);


        } else {

            notification.mActions.set(1, playPauseAction[1]);


            startForeground(NOTIFICATION_ID, notification.build());
        }
        notificationManager.notify(NOTIFICATION_ID, notification.build());
    }

    public void firstTimeSetup() {
        if (musiPlayerHelper.mediaPlayer.isPlaying()) {

            notification.mActions.set(1, playPauseAction[1]);
            stopForeground(false);


        } else {

            notification.mActions.set(1, playPauseAction[0]);


            startForeground(NOTIFICATION_ID, notification.build());
        }
        notificationManager.notify(NOTIFICATION_ID, notification.build());
    }

    private void onContentChanged() {

        musicAlbum = Commen.decodeUriToBitmap(getApplicationContext(), Uri.parse(sharedPrefrenceManager.getSong().getSongImageUri()));
        if (musicAlbum == null)
            musicAlbum = BitmapFactory.decodeResource(getResources(), R.drawable.icon1);
        notification.setLargeIcon(musicAlbum);
        notification.setContentTitle(sharedPrefrenceManager.getSong().getSongName());
        notification.setContentText(sharedPrefrenceManager.getSong().getAlbumName());
        notificationManager.notify(NOTIFICATION_ID, notification.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (intent.getAction() == null) {
            intent.setAction("");
        }

        if (intent.getAction() != null) {

            switch (intent.getAction()) {
                case ACTION_PLAY:

                    if (musiPlayerHelper.mediaPlayer.isPlaying()) {

                        notification.mActions.set(1, playPauseAction[0]);
                        stopForeground(false);

                        if (callbacks != null) {
                            callbacks.onPlayButtonClicked(false);
                        }

                    } else {

                        notification.mActions.set(1, playPauseAction[1]);

                        if (callbacks != null) {
                            callbacks.onPlayButtonClicked(true);
                        }

                        startForeground(NOTIFICATION_ID, notification.build());
                    }

                    break;
                case ACTION_NEXT:

                    if (callbacks != null) {
                        callbacks.onNextButtonClicked();
                    }
                    if (!musiPlayerHelper.mediaPlayer.isPlaying())
                        onPlayPauseClicked();

                    break;
                case ACTION_PREVIOUS:

                    if (callbacks != null) {
                        callbacks.onPreviousButtonClicked();
                    }
                    if (!musiPlayerHelper.mediaPlayer.isPlaying())
                        onPlayPauseClicked();

                    notificationManager.notify(NOTIFICATION_ID, notification.build());
                    break;

                default:
                    Intent showMusicPlayerActivityIntent = new Intent(this, ListActivity.class);
                    showMusicPlayerActivityIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    showMusicPlayerActivityIntent.putExtra("notification", true);

                    Intent playIntent = new Intent(this, MusicService.class);
                    playIntent.setAction(ACTION_PLAY);
                    PendingIntent playPendingIntent = PendingIntent.getService(this, 0, playIntent, 0);

                    Intent forwardIntent = new Intent(this, MusicService.class);
                    forwardIntent.setAction(ACTION_NEXT);
                    PendingIntent nextPendingIntent = PendingIntent.getService(this, 0, forwardIntent, 0);

                    Intent rewindIntent = new Intent(this, MusicService.class);
                    rewindIntent.setAction(ACTION_PREVIOUS);
                    PendingIntent previousPendingIntent = PendingIntent.getService(this, 0, rewindIntent, 0);

                    notificationManager = NotificationManagerCompat.from(getApplicationContext());

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, CHANNEL_DESCRIPTION_SONG, NotificationManager.IMPORTANCE_LOW);
                        channel.setShowBadge(false);
                        notificationManager.createNotificationChannel(channel);
                    }


                    musicAlbum = Commen.decodeUriToBitmap(getApplicationContext(), Uri.parse(sharedPrefrenceManager.getSong().getSongImageUri()));

                    MediaSessionCompat mediaSession = new MediaSessionCompat(this, "Service");
                    mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

                    playPauseAction = new NotificationCompat.Action[]{
                            new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", playPendingIntent),
                            new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", playPendingIntent),
                    };

                    notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setSmallIcon(R.drawable.icon1)
                            .addAction(android.R.drawable.ic_media_previous, "Previous", previousPendingIntent)
                            .addAction(playPauseAction[0])
                            .addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent)
                            .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                    .setShowActionsInCompactView(0, 1, 2)
                                    .setMediaSession(mediaSession.getSessionToken()))
                            .setContentTitle(sharedPrefrenceManager.getSong().getSongName())
                            .setContentText(sharedPrefrenceManager.getSong().getAlbumName())
                            .setOngoing(false)
                            .setLargeIcon(musicAlbum);

                    startForeground(NOTIFICATION_ID, notification.build());

                    break;
            }

            notificationManager.notify(NOTIFICATION_ID, notification.build());

        } else {
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {

        callbacks = null;
    }

    @Override
    public void onMediaPlayerPrepared() {

    }

    @Override
    public void onMediaPlayerCompletion() {
        notificationManager.notify(NOTIFICATION_ID, notification.build());
        stopForeground(true);
        stopSelf();
    }


    public interface Callbacks {
        void onPlayButtonClicked(boolean isPlaying);

        void onNextButtonClicked();

        void onPreviousButtonClicked();
    }
}
