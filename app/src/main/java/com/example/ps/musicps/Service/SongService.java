//package com.example.ps.musicps.Service;
//
//import android.Manifest;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.appwidget.AppWidgetManager;
//import android.content.ComponentName;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.IBinder;
//
//import androidx.annotation.Nullable;
//import androidx.core.app.NotificationCompat;
//import androidx.core.content.ContextCompat;
//
//import android.support.v4.media.session.MediaSessionCompat;
//import android.widget.RemoteViews;
//import android.widget.Toast;
//
//import com.example.ps.musicps.Commen.AudioFocusControler;
//import com.example.ps.musicps.Commen.Commen;
//import com.example.ps.musicps.Commen.MyApplication;
//import com.example.ps.musicps.Commen.SongSharedPrefrenceManager;
//import com.example.ps.musicps.Helper.MusiPlayerHelper;
//import com.example.ps.musicps.View.PSMusicWidget;
//import com.example.ps.musicps.View.PlaySongActivity;
//import com.example.ps.musicps.R;
//import com.example.ps.musicps.View.SongListActivity;
//
//public class SongService  {
//
//
//    private static final String ACTION_PLAY = "com.example.ps.musicps.PLAY_MUSIC";
//    private static final String ACTION_NEXT = "com.example.ps.musicps.NEXT_MUSIC";
//    private static final String ACTION_PREVIOUS = "com.example.ps.musicps.PREVIOUS_MUSIC";
//    private static final String ACTION_CLOSE = "com.example.ps.musicps.CLOSE";
//    private final static String CHANNEL_DISCIPTION_SONG = "channel_discription_Song";
//    private final static String NOTIFICATION_CHANNEL = "channel_name";
//    private final static int NOTIFICATION_ID = 101;
//    public static onNotificationServiceStateChangedWidget onNotificationServiceStateChangedWidget;
//    public static onNotificationServiceStateChangedList onNotificationServiceStateChangedList;
//    public static onNotificationServiceStateChangedPlay onNotificationServiceStateChangedPlay;
//    RemoteViews view;
//    RemoteViews bigView;
//    NotificationCompat.Builder notification;
//    NotificationManager notificationManager;
//    private MediaSessionCompat mediaSession;
//
////    @Override
////    public void onCreate() {
////        super.onCreate();
////
////    }
////
////    @Nullable
////    @Override
////    public IBinder onBind(Intent intent) {
////        return null;
////    }
////
////    @Override
////    public int onStartCommand(Intent intent, int flags, int startId) {
////
////        PSMusicWidget.onWidgetStatusChangedService = new PSMusicWidget.onWidgetStatusChanged() {
////            @Override
////            public void onPlayPauseClicked() {
////                if (view != null && bigView != null) {
////                    if (Commen.IS_PLAYING) {
////                        view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
////                        bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
////                    } else {
////                        view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
////                        bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
////                    }
////                    notificationManager.notify(NOTIFICATION_ID, notification.build());
////                }
////            }
////
////            @Override
////            public void onSongChanged() {
////                if (Commen.song != null) {
////                    view.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
////                    bigView.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
////                    bigView.setTextViewText(R.id.tv_ArtistName_notification, Commen.song.getArtistName());
////                    notificationManager.notify(NOTIFICATION_ID, notification.build());
////                }
////            }
////        };
////
////
////        AudioFocusControler.getInstance().onAudioFocusChangeService = new AudioFocusControler.onAudioFocusChangeService() {
////            @Override
////            public void onServiceFocusChange() {
////                Commen.IS_PLAYING = false;
////                view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
////                bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
////                notificationManager.notify(NOTIFICATION_ID, notification.build());
////                if (onNotificationServiceStateChangedList != null) {
////                    onNotificationServiceStateChangedList.onPlayButtonClickedList(false);
////                }
////
////                if (onNotificationServiceStateChangedPlay != null) {
////                    onNotificationServiceStateChangedPlay.onPlayButtonClickedPlaySong(false);
////                }
////                if (onNotificationServiceStateChangedWidget != null) {
////                    onNotificationServiceStateChangedWidget.onPlayPauseClicked();
////                }
////            }
////        };
////        AudioFocusControler.getInstance().initAudio();
////
////
////        PlaySongActivity.onPlaySongActivityStateChanged = new PlaySongActivity.onPlaySongActivityStateChanged() {
////            @Override
////            public void onPlaySongPlaypauseClicked(boolean isPlaying) {
////                if (isPlaying) {
////                    view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
////                    bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
//////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//////                        MyApplication.getAudioManager().requestAudioFocus(focusRequest);
//////                    }
////                } else {
////                    view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
////                    bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
//////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//////                        MyApplication.getAudioManager().abandonAudioFocusRequest(focusRequest);
//////                    }
////                }
////                notificationManager.notify(NOTIFICATION_ID, notification.build());
////            }
////
////            @Override
////            public void onPlaySongNextClicked() {
////                if (Commen.song != null) {
////                    view.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
////                    bigView.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
////                    bigView.setTextViewText(R.id.tv_ArtistName_notification, Commen.song.getArtistName());
////                    notificationManager.notify(NOTIFICATION_ID, notification.build());
////                }
////            }
////
////            @Override
////            public void onPlaySongPreviousClicked() {
////                if (Commen.song != null) {
////                    view.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
////                    bigView.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
////                    bigView.setTextViewText(R.id.tv_ArtistName_notification, Commen.song.getArtistName());
////                    notificationManager.notify(NOTIFICATION_ID, notification.build());
////                }
////            }
////
////            @Override
////            public void onPlaySongMediaComplete() {
////                view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
////                bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
////                notificationManager.notify(NOTIFICATION_ID, notification.build());
////            }
////        };
////
////        PlayingSongFragment.onSongListActivityStateChanged = new PlayingSongFragment.onSongListActivityStateChanged() {
////            @Override
////            public void onSongListPlaypauseClicked(boolean isPlaying) {
////                if (view != null && bigView != null) {
////                    if (isPlaying) {
////                        view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
////                        bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
////                    } else {
////                        view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
////                        bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
////                    }
////                    notificationManager.notify(NOTIFICATION_ID, notification.build());
////                }
////            }
////
////            @Override
////            public void onSongListRemovedSong() {
////                if (!Commen.IS_PLAYING && (view != null && bigView != null)) {
////                    view.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
////                    bigView.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
////                    bigView.setTextViewText(R.id.tv_ArtistName_notification, Commen.song.getArtistName());
////                    view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
////                    bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
////                    notificationManager.notify(NOTIFICATION_ID, notification.build());
////                }
////            }
////
////            @Override
////            public void onSongListPlaypauseMediaComplete() {
////                if (!Commen.IS_PLAYING) {
////
////                }
////            }
////        };
////        if (intent.getAction() == null) {
////            intent.setAction("");
////        }
////
////        if (intent.getAction() != null && (PlaySongActivity.isExternalSource || (SongListActivity.songList != null && SongListActivity.songList.size() > 0) || PSMusicWidget.SHOULD_SERVICE_START)) {
////
////            switch (intent.getAction()) {
////                case ACTION_PLAY:
////                    if (Commen.IS_PLAYING) {
////                        Commen.getInstance().FadeOut(2);
////                        view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
////                        bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
////                        if (onNotificationServiceStateChangedList != null) {
////                            onNotificationServiceStateChangedList.onPlayButtonClickedList(false);
////                        }
////
////                        if (onNotificationServiceStateChangedPlay != null) {
////                            onNotificationServiceStateChangedPlay.onPlayButtonClickedPlaySong(false);
////                        }
////                    } else {
////                        Commen.getInstance().FadeIn(2);
////                        view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
////                        bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
////                        if (onNotificationServiceStateChangedList != null) {
////                            onNotificationServiceStateChangedList.onPlayButtonClickedList(true);
////                        }
////
////                        if (onNotificationServiceStateChangedPlay != null) {
////                            onNotificationServiceStateChangedPlay.onPlayButtonClickedPlaySong(true);
////                        }
////                    }
////                    notificationManager.notify(NOTIFICATION_ID, notification.build());
////                    if (onNotificationServiceStateChangedWidget != null) {
////                        onNotificationServiceStateChangedWidget.onPlayPauseClicked();
////                    }
////                    break;
////                case ACTION_NEXT:
////                    if (onNotificationServiceStateChangedPlay != null) {
////                        onNotificationServiceStateChangedPlay.onNextButtonClickedPlaySong();
////                    }
////                    if (onNotificationServiceStateChangedList != null) {
////                        onNotificationServiceStateChangedList.onNextButtonClickedList();
////                    }
////                    view.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
////                    bigView.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
////                    bigView.setTextViewText(R.id.tv_ArtistName_notification, Commen.song.getArtistName());
////                    view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
////                    bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
////                    notificationManager.notify(NOTIFICATION_ID, notification.build());
////                    if (onNotificationServiceStateChangedWidget != null) {
////                        onNotificationServiceStateChangedWidget.onSongChanged();
////                    }
////                    break;
////                case ACTION_PREVIOUS:
////                    if (onNotificationServiceStateChangedPlay != null) {
////                        onNotificationServiceStateChangedPlay.onPrevioudButtonClickedPlaySong();
////                    }
////                    if (onNotificationServiceStateChangedList != null) {
////                        onNotificationServiceStateChangedList.onPrevioudButtonClickedList();
////                    }
////                    view.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
////                    bigView.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
////                    bigView.setTextViewText(R.id.tv_ArtistName_notification, Commen.song.getArtistName());
////                    view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
////                    bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
////                    notificationManager.notify(NOTIFICATION_ID, notification.build());
////                    if (onNotificationServiceStateChangedWidget != null) {
////                        onNotificationServiceStateChangedWidget.onSongChanged();
////                    }
////                    break;
////                case ACTION_CLOSE:
////                    stopSelf();
////                    Commen.IS_PLAYING = false;
////                    if (onNotificationServiceStateChangedWidget != null) {
////                        onNotificationServiceStateChangedWidget.onPlayPauseClicked();
////                    }
////                    if (onNotificationServiceStateChangedList != null) {
////                        onNotificationServiceStateChangedList.onCloseButtonClickedList();
////                    }
////                    if (onNotificationServiceStateChangedPlay != null) {
////                        onNotificationServiceStateChangedPlay.onCloseButtonClickedPlaySong();
////                    }
////                    break;
////                default:
////                    Intent showMusicPlayerActivityIntent = new Intent(this, PlaySongActivity.class);
////                    showMusicPlayerActivityIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
////                    showMusicPlayerActivityIntent.putExtra("notification", true);
////
////                    Intent playIntent = new Intent(this, SongService.class);
////                    playIntent.setAction(ACTION_PLAY);
////                    PendingIntent playPendingIntent = PendingIntent.getService(this, 0, playIntent, 0);
////
////                    Intent forwardIntent = new Intent(this, SongService.class);
////                    forwardIntent.setAction(ACTION_NEXT);
////                    PendingIntent nextPendingIntent = PendingIntent.getService(this, 0, forwardIntent, 0);
////
////                    Intent rewindIntent = new Intent(this, SongService.class);
////                    rewindIntent.setAction(ACTION_PREVIOUS);
////                    PendingIntent previousPendingIntent = PendingIntent.getService(this, 0, rewindIntent, 0);
////
////                    Intent closeIntent = new Intent(this, SongService.class);
////                    closeIntent.setAction(ACTION_CLOSE);
////                    PendingIntent closePendingIntent = PendingIntent.getService(this, 0, closeIntent, 0);
////
////                    notificationManager = (NotificationManager) this.getSystemService(Service.NOTIFICATION_SERVICE);
////
////
////                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
////                        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, CHANNEL_DISCIPTION_SONG, NotificationManager.IMPORTANCE_LOW);
////                        channel.setShowBadge(false);
////                        notificationManager.createNotificationChannel(channel);
////                    }
////
////                    this.view = new RemoteViews(getPackageName(), R.layout.layout_notification);
////                    this.bigView = new RemoteViews(getPackageName(), R.layout.layout_notification_expanded);
////                    view.setOnClickPendingIntent(R.id.iv_playPayse_notification, playPendingIntent);
////                    view.setOnClickPendingIntent(R.id.iv_next_notification, nextPendingIntent);
////                    view.setOnClickPendingIntent(R.id.iv_previous_notification, previousPendingIntent);
////                    view.setOnClickPendingIntent(R.id.iv_close, closePendingIntent);
////                    if (Commen.song != null) {
////                        view.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
////                        view.setTextViewText(R.id.tv_appName_notification, "PSMusic");
////                        view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
////                        view.setImageViewResource(R.id.iv_close, R.drawable.ic_close_black_24dp);
////                    }
////
////                    bigView.setOnClickPendingIntent(R.id.iv_playPayse_notification, playPendingIntent);
////                    bigView.setOnClickPendingIntent(R.id.iv_next_notification, nextPendingIntent);
////                    bigView.setOnClickPendingIntent(R.id.iv_previous_notification, previousPendingIntent);
////                    bigView.setOnClickPendingIntent(R.id.iv_close, closePendingIntent);
////                    if (Commen.song != null) {
////                        bigView.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
////                        bigView.setTextViewText(R.id.tv_appName_notification, "PSMusic");
////                        bigView.setTextViewText(R.id.tv_ArtistName_notification, Commen.song.getArtistName());
////                        bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
////                        bigView.setImageViewResource(R.id.iv_close, R.drawable.ic_close_black_24dp);
////                    }
////
////
////                    notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL);
////                    notification.setContent(view)
////                            .setCustomBigContentView(bigView)
////                            .setSmallIcon(R.drawable.icon1)
////                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
////                            .setContentText(Commen.song.getSongName())
////                            .setColor(getResources().getColor(R.color.colorPrimary))
////                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
////                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
////                            .setContentIntent(PendingIntent.getActivity(this, 0, showMusicPlayerActivityIntent, 0));
////
////                    notificationManager.notify(NOTIFICATION_ID, notification.build());
////                    startForeground(NOTIFICATION_ID, notification.build());
////
////                    break;
////            }
////
////        } else {
////            stopSelf();
////            Toast.makeText(getApplicationContext(), "This song is not exists!", Toast.LENGTH_LONG).show();
////        }
////
////        return START_STICKY;
////    }
////
////    @Override
////    public void onDestroy() {
////        if (onNotificationServiceStateChangedWidget != null) {
////            onNotificationServiceStateChangedWidget.onPlayPauseClicked();
////        }
////        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
////                == PackageManager.PERMISSION_GRANTED) {
////            if (!PlaySongActivity.isExternalSource) {
////                try {
////                    MyApplication.songSharedPrefrence.saveSong(Commen.song);
////                } catch (Exception e) {
////
////                    MyApplication.songSharedPrefrence = new SongSharedPrefrenceManager(getApplication());
////                    MyApplication.songSharedPrefrence.saveSong(Commen.song);
////                }
////            }
////            PlaySongActivity.isExternalSource = false;
////        }
////        onNotificationServiceStateChangedList = null;
////        onNotificationServiceStateChangedPlay = null;
////        if (PlaySongActivity.timer != null) {
////            PlaySongActivity.timer.purge();
////            PlaySongActivity.timer.cancel();
////        }
////    }
////
////    @Override
////    public void onMediaPlayerPrepared() {
////
////    }
////
////    @Override
////    public void onMediaPlayerCompletion() {
////        view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
////        bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
////        notificationManager.notify(NOTIFICATION_ID, notification.build());
////        stopForeground(true);
////        stopSelf();
////    }
//
//
////    @Override
////    public void onAudioFocusChangeService(int i) {
////
////                if (Commen.mediaPlayer.isPlaying()) {
////                    Commen.mediaPlayer.pause();
////                    view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
////                    bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
////                    notificationManager.notify(NOTIFICATION_ID, notification.build());
////                    if (onNotificationServiceStateChangedList != null) {
////                        onNotificationServiceStateChangedList.onPlayButtonClickedList();
////                    }
////
////                    if (onNotificationServiceStateChangedPlay != null) {
////                        onNotificationServiceStateChangedPlay.onPlayButtonClickedPlaySong();
////                    }
////                }
////
////
////    }
//
//
//    //////////////////Lock screen///////////////////
//
////    private void setupMediaSession() {
////        ComponentName receiver = new ComponentName(getPackageName(), SongService.class.getName());
////        mediaSession = new MediaSessionCompat(this, "StreamService", receiver, null);
////        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
////                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
////        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
////                .setState(PlaybackStateCompat.STATE_PAUSED, 0, 0)
////                .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE)
////                .build());
////        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
////                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Test Artist")
////                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "Test Album")
////                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Test Track Name")
////                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 10000)
////                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
////                        BitmapFactory.decodeResource(getResources(), R.drawable.ic_repeat_one_24px))
////                //.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, "Test Artist")
////                .build());
////
////
////        mediaSession.setActive(true);
////
////        IntentFilter filter = new IntentFilter();
////        filter.addAction(ACTION_NEXT);
////        filter.addAction(ACTION_PLAY);
////       this.registerReceiver( new RemoteReceiver(), filter);
////    }
//
//
//    // i use isPlaying param becausse fadin and fadeout , stop and atart mediaplayer in period of time
//    public interface onNotificationServiceStateChangedPlay {
//        void onPlayButtonClickedPlaySong(boolean isPlaying);
//
//        void onNextButtonClickedPlaySong();
//
//        void onPrevioudButtonClickedPlaySong();
//
//        void onCloseButtonClickedPlaySong();
//
//    }
//
//    public interface onNotificationServiceStateChangedList {
//        void onPlayButtonClickedList(boolean isPlaying);
//
//        void onNextButtonClickedList();
//
//        void onPrevioudButtonClickedList();
//
//        void onCloseButtonClickedList();
//    }
//
//    public interface onNotificationServiceStateChangedWidget {
//        void onPlayPauseClicked();
//
//        void onSongChanged();
//    }
//
//}
