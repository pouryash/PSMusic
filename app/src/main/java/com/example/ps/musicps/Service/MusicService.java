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
import android.media.MediaMetadata;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.ps.musicps.Commen.AudioFocusControler;
import com.example.ps.musicps.Commen.Commen;
import com.example.ps.musicps.Commen.MyApplication;
import com.example.ps.musicps.Commen.SongSharedPrefrenceManager;
import com.example.ps.musicps.Helper.MusiPlayerHelper;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.R;
import com.example.ps.musicps.View.SearchActivity;


public class MusicService extends Service implements MusiPlayerHelper.onMediaPlayerStateChanged,
        MusiPlayerHelper.onMediaplayerChange {

    private static final String ACTION_PLAY = "com.example.ps.musicps.PLAY_MUSIC";
    private static final String ACTION_NEXT = "com.example.ps.musicps.NEXT_MUSIC";
    private static final String ACTION_PREVIOUS = "com.example.ps.musicps.PREVIOUS_MUSIC";
    private static final String ACTION_CONTENT = "com.example.ps.musicps.ACTION_CONTENT";
    private final static String CHANNEL_DESCRIPTION_SONG = "channel_description_Song";
    private final static String NOTIFICATION_CHANNEL = "channel_name";
    private final static int NOTIFICATION_ID = 101;

    public boolean isBind = false;
    private Bitmap musicAlbum;
    private final IBinder mBinder = new ServiceBinder();
    public static Callbacks callbacks;
    private NotificationCompat.Builder notification;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Action[] playPauseAction;
    MusiPlayerHelper musiPlayerHelper;
    SongSharedPrefrenceManager sharedPrefrenceManager;
    private Observer<Song> songObserver;
    PlaybackStateCompat.Builder mStateBuilder;
    MediaSessionCompat mediaSession;
    MediaMetadataCompat.Builder metadataBuilder;
    Intent intentContent;
    PendingIntent contentPendingIntent;


    @Override
    public void onCreate() {
        super.onCreate();
        musiPlayerHelper = ((MyApplication) getApplication()).getComponent().getMusicPlayerHelper();
        sharedPrefrenceManager = ((MyApplication) getApplication()).getComponent().getSharedPrefrence();
        musiPlayerHelper.setOnMediaplayerChange(MusicService.this);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        isBind = true;
        return mBinder;
    }


    public class ServiceBinder extends Binder {
        public MusicService getMusicService() {
            return MusicService.this;
        }
    }

    public void registerClient(Activity activity, LifecycleOwner lifecycleOwner) {
        setUpCallback((Callbacks) activity);

        setUpObserver();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isBind = false;
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    public void setUpCallback(Callbacks callback) {
        this.callbacks = callback;
//        if (callbacks.getClass().equals(SearchActivity.class)) {
//            intentContent = new Intent(this, SearchActivity.class);
//            intentContent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        } else {
//            intentContent = new Intent(this, ListActivity.class);
//            intentContent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        }
//        contentPendingIntent = PendingIntent.getService(this, 0, intentContent, PendingIntent.FLAG_UPDATE_CURRENT);
//        notification.setContentIntent(contentPendingIntent);
//        notificationManager.notify(NOTIFICATION_ID, notification.build());
    }

    public void setUpObserver() {

        songObserver = new Observer<Song>() {
            @Override
            public void onChanged(Song song) {
//                onContentChanged();
            }
        };

        sharedPrefrenceManager.getSharedPrefsSongLiveData(SongSharedPrefrenceManager.KEY_SONG_MODEL).
                getSongLiveData(SongSharedPrefrenceManager.KEY_SONG_MODEL, new Song()).
                observeForever(songObserver);
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
            startForeground(NOTIFICATION_ID, notification.build());


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
//        notificationManager.notify(NOTIFICATION_ID, notification.build());
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
                    if (!sharedPrefrenceManager.getSharedPrefsSongLiveData(SongSharedPrefrenceManager.KEY_SONG_MODEL).
                            getSongLiveData(SongSharedPrefrenceManager.KEY_SONG_MODEL, new Song()).hasObservers())
                        setUpObserver();

                    if (callbacks != null) {
                        callbacks.onNextButtonClicked();
                    }
                    if (!musiPlayerHelper.mediaPlayer.isPlaying())
                        onPlayPauseClicked();

                    break;
                case ACTION_PREVIOUS:
                    if (!sharedPrefrenceManager.getSharedPrefsSongLiveData(SongSharedPrefrenceManager.KEY_SONG_MODEL).
                            getSongLiveData(SongSharedPrefrenceManager.KEY_SONG_MODEL, new Song()).hasObservers())
                        setUpObserver();

                    if (callbacks != null) {
                        callbacks.onPreviousButtonClicked();
                    }
                    if (!musiPlayerHelper.mediaPlayer.isPlaying())
                        onPlayPauseClicked();

                    notificationManager.notify(NOTIFICATION_ID, notification.build());
                    break;

                default:

                    Intent playIntent = new Intent(this, MusicService.class);
                    playIntent.setAction(ACTION_PLAY);
                    PendingIntent playPendingIntent = PendingIntent.getService(this, 0, playIntent, 0);

                    Intent forwardIntent = new Intent(this, MusicService.class);
                    forwardIntent.setAction(ACTION_NEXT);
                    PendingIntent nextPendingIntent = PendingIntent.getService(this, 0, forwardIntent, 0);

                    Intent rewindIntent = new Intent(this, MusicService.class);
                    rewindIntent.setAction(ACTION_PREVIOUS);
                    PendingIntent previousPendingIntent = PendingIntent.getService(this, 0, rewindIntent, 0);

                    intentContent = new Intent(this, ListActivity.class);
//                    intentContent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    contentPendingIntent = PendingIntent.getService(this, 0, intentContent, PendingIntent.FLAG_ONE_SHOT);


                    notificationManager = NotificationManagerCompat.from(getApplicationContext());

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, CHANNEL_DESCRIPTION_SONG, NotificationManager.IMPORTANCE_LOW);
                        channel.setShowBadge(false);
                        notificationManager.createNotificationChannel(channel);
                    }


                    mediaSession = new MediaSessionCompat(this, "Service");
                    mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);


                    setUpMediaSession();

                    mStateBuilder = new PlaybackStateCompat.Builder()
                            .setState(PlaybackStateCompat.STATE_PLAYING, 1, 1.0f)
                            .setBufferedPosition(musiPlayerHelper.mediaPlayer.getCurrentPosition())
                            .setActions(
                                    PlaybackStateCompat.ACTION_PLAY |
                                            PlaybackStateCompat.ACTION_PAUSE |
                                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                            PlaybackStateCompat.ACTION_SEEK_TO |
                                            PlaybackStateCompat.ACTION_PLAY_PAUSE);


                    mediaSession.setPlaybackState(mStateBuilder.build());

                    mediaSession.setCallback(new MySessionCallback());

                    playPauseAction = new NotificationCompat.Action[]{
                            new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", playPendingIntent),
                            new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", playPendingIntent),
                    };


                    notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
                            .setSmallIcon(R.drawable.icon1)
                            .addAction(android.R.drawable.ic_media_previous, "Previous", previousPendingIntent)
                            .addAction(playPauseAction[0])
                            .addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent)
                            .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                    .setShowActionsInCompactView(0, 1, 2)
                                    .setMediaSession(mediaSession.getSessionToken()))
                            .setContentTitle(sharedPrefrenceManager.getSong().getSongName())
                            .setContentText(sharedPrefrenceManager.getSong().getAlbumName())
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setOngoing(false)
                            .setShowWhen(false)
                            .setAutoCancel(true)
                            .setContentIntent(contentPendingIntent)
                            .setLargeIcon(musicAlbum);

                    onContentChanged();
                    playBackStateChanged();

                    startForeground(NOTIFICATION_ID, notification.build());

                    break;
            }

            notificationManager.notify(NOTIFICATION_ID, notification.build());

        } else {
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    public void setUpMediaSession() {

        metadataBuilder = new MediaMetadataCompat.Builder();

//        ComponentName receiver = new ComponentName(getPackageName(), RemoteReciver.class.getName());


//        mediaSession = new MediaSessionCompat(getApplicationContext(), "StreamService", receiver, null);
//        final Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
//        mediaButtonIntent.setComponent(receiver);
//        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
//        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 99 /*request code*/,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        mediaSession.setSessionActivity(pi);


        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, musiPlayerHelper.mediaPlayer.getDuration());
//        metadataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, musicAlbum)
//                .putBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON, musicAlbum);

//        metadataBuilder = new MediaMetadataCompat.Builder()
//                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Test Artist")
//                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "Test Album")
//                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Test Track Name")
//                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
//                        BitmapFactory.decodeResource(getResources(), R.drawable.icon1))
//                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, musiPlayerHelper.mediaPlayer.getDuration());

        mediaSession.setMetadata(metadataBuilder.build());

        mediaSession.setActive(true);
    }


    public void playBackStateChanged() {


        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, musiPlayerHelper.mediaPlayer.getDuration());


        if (musiPlayerHelper.mediaPlayer.isPlaying()) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    musiPlayerHelper.mediaPlayer.getCurrentPosition(), 1f);
        } else {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    musiPlayerHelper.mediaPlayer.getCurrentPosition(), 1f);
        }
        mediaSession.setMetadata(metadataBuilder.build());
        mediaSession.setPlaybackState(mStateBuilder.build());
    }

    private class MySessionCallback extends MediaSessionCompat.Callback {

        @Override
        public void onSeekTo(long pos) {
            musiPlayerHelper.mediaPlayer.seekTo((int) pos);
            playBackStateChanged();
        }

    }

    @Override
    public void onDestroy() {

        callbacks = null;

        mediaSession.release();

        sharedPrefrenceManager
                .getSharedPrefsSongLiveData(SongSharedPrefrenceManager.KEY_SONG_MODEL)
                .removeObserver(songObserver);
    }

    public void removeNotification() {
        stopForeground(false);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Override
    public void onMediaPlayerPrepared() {
        mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                musiPlayerHelper.mediaPlayer.getCurrentPosition(), 1f);
    }

    @Override
    public void onMediaPlayerCompletion() {
        notificationManager.notify(NOTIFICATION_ID, notification.build());
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onMediaPlayerChange() {
        playBackStateChanged();
    }

    public interface Callbacks {
        void onPlayButtonClicked(boolean isPlaying);

        void onNextButtonClicked();

        void onPreviousButtonClicked();
    }
}
