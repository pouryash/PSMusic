package com.example.ps.musicps.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioFocusRequest;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import android.widget.RemoteViews;

import com.example.ps.musicps.Commen.AudioFocusControler;
import com.example.ps.musicps.Commen.Commen;
import com.example.ps.musicps.PlaySongActivity;
import com.example.ps.musicps.PlayingSongFragment;
import com.example.ps.musicps.R;

public class SongService extends Service implements Commen.onMediaPlayerStateChanged{


    private static final String ACTION_PLAY = "com.example.ps.musicps.PLAY_MUSIC";
    private static final String ACTION_NEXT = "com.example.ps.musicps.NEXT_MUSIC";
    private static final String ACTION_PREVIOUS = "com.example.ps.musicps.PREVIOUS_MUSIC";
    private final static String CHANNEL_DISCIPTION_SONG = "channel_discription_Song";
    private final static String NOTIFICATION_CHANNEL = "channel_name";
    private final static int NOTIFICATION_ID = 101;
    public static onNotificationServiceStateChangedList onNotificationServiceStateChangedList;
    public static onNotificationServiceStateChangedPlay onNotificationServiceStateChangedPlay;
    RemoteViews view;
    RemoteViews bigView;
    NotificationCompat.Builder notification;
    NotificationManager notificationManager;
    AudioFocusRequest  focusRequest;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        AudioFocusControler.getInstance().onAudioFocusChangeService = new AudioFocusControler.onAudioFocusChangeService() {
            @Override
            public void onServiceFocusChange() {
                    view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
                    bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
                    notificationManager.notify(NOTIFICATION_ID, notification.build());
                    if (onNotificationServiceStateChangedList != null) {
                        onNotificationServiceStateChangedList.onPlayButtonClickedList();
                    }

                    if (onNotificationServiceStateChangedPlay != null) {
                        onNotificationServiceStateChangedPlay.onPlayButtonClickedPlaySong();
                    }
                }
        };
        AudioFocusControler.getInstance().initAudio();


        PlaySongActivity.onPlaySongActivityStateChanged = new PlaySongActivity.onPlaySongActivityStateChanged() {
            @Override
            public void onPlaySongPlaypauseClicked() {
                if (Commen.mediaPlayer.isPlaying()) {
                    view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
                    bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        MyApplication.getAudioManager().requestAudioFocus(focusRequest);
//                    }
                } else {
                    view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
                    bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        MyApplication.getAudioManager().abandonAudioFocusRequest(focusRequest);
//                    }
                }
                notificationManager.notify(NOTIFICATION_ID, notification.build());
            }

            @Override
            public void onPlaySongNextClicked() {
                view.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
                bigView.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
                bigView.setTextViewText(R.id.tv_ArtistName_notification, Commen.song.getArtistName());
                notificationManager.notify(NOTIFICATION_ID, notification.build());
            }

            @Override
            public void onPlaySongPreviousClicked() {
                view.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
                bigView.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
                bigView.setTextViewText(R.id.tv_ArtistName_notification, Commen.song.getArtistName());
                notificationManager.notify(NOTIFICATION_ID, notification.build());
            }

            @Override
            public void onPlaySongMediaComplete() {
                view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
                bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
                notificationManager.notify(NOTIFICATION_ID, notification.build());
            }
        };
        PlayingSongFragment.onSongListActivityStateChanged = new PlayingSongFragment.onSongListActivityStateChanged() {
            @Override
            public void onSongListPlaypauseClicked() {
                if (Commen.mediaPlayer.isPlaying()) {
                    view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
                    bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
                } else {
                    view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
                    bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
                }
                notificationManager.notify(NOTIFICATION_ID, notification.build());
            }

            @Override
            public void onSongListPlaypauseMediaComplete() {
                view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
                bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
                notificationManager.notify(NOTIFICATION_ID, notification.build());
            }
        };
        if (intent.getAction() == null) {
            intent.setAction("");
        }
        switch (intent.getAction()) {
            case ACTION_PLAY:
                if (Commen.mediaPlayer.isPlaying()) {
                    Commen.mediaPlayer.pause();
                    view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
                    bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
                } else {
                    Commen.mediaPlayer.start();
                    view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
                    bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
                }
                if (onNotificationServiceStateChangedList != null) {
                    onNotificationServiceStateChangedList.onPlayButtonClickedList();
                }

                if (onNotificationServiceStateChangedPlay != null) {
                    onNotificationServiceStateChangedPlay.onPlayButtonClickedPlaySong();
                }
                notificationManager.notify(NOTIFICATION_ID, notification.build());
                break;
            case ACTION_NEXT:
                if (onNotificationServiceStateChangedPlay != null) {
                    onNotificationServiceStateChangedPlay.onNextButtonClickedPlaySong();
                }
                if (onNotificationServiceStateChangedList != null) {
                    onNotificationServiceStateChangedList.onNextButtonClickedList();
                }
                view.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
                bigView.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
                bigView.setTextViewText(R.id.tv_ArtistName_notification, Commen.song.getArtistName());
                view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
                bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
                notificationManager.notify(NOTIFICATION_ID, notification.build());
                break;
            case ACTION_PREVIOUS:
                if (onNotificationServiceStateChangedPlay != null) {
                    onNotificationServiceStateChangedPlay.onPrevioudButtonClickedPlaySong();
                }
                if (onNotificationServiceStateChangedList != null) {
                    onNotificationServiceStateChangedList.onPrevioudButtonClickedList();
                }
                view.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
                bigView.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
                bigView.setTextViewText(R.id.tv_ArtistName_notification, Commen.song.getArtistName());
                view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
                bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
                notificationManager.notify(NOTIFICATION_ID, notification.build());
                break;
            default:
                Intent showMusicPlayerActivityIntent = new Intent(this, PlaySongActivity.class);
                showMusicPlayerActivityIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                showMusicPlayerActivityIntent.putExtra("notification", true);

                Intent playIntent = new Intent(this, SongService.class);
                playIntent.setAction(ACTION_PLAY);
                PendingIntent playPendingIntent = PendingIntent.getService(this, 0, playIntent, 0);

                Intent forwardIntent = new Intent(this, SongService.class);
                forwardIntent.setAction(ACTION_NEXT);
                PendingIntent nextPendingIntent = PendingIntent.getService(this, 0, forwardIntent, 0);

                Intent rewindIntent = new Intent(this, SongService.class);
                rewindIntent.setAction(ACTION_PREVIOUS);
                PendingIntent previousPendingIntent = PendingIntent.getService(this, 0, rewindIntent, 0);

                notificationManager = (NotificationManager) this.getSystemService(Service.NOTIFICATION_SERVICE);


                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, CHANNEL_DISCIPTION_SONG, NotificationManager.IMPORTANCE_LOW);
                    channel.setShowBadge(false);
                    notificationManager.createNotificationChannel(channel);
                }

                this.view = new RemoteViews(getPackageName(), R.layout.layout_notification);
                this.bigView = new RemoteViews(getPackageName(), R.layout.layout_notification_expanded);
                view.setOnClickPendingIntent(R.id.iv_playPayse_notification, playPendingIntent);
                view.setOnClickPendingIntent(R.id.iv_next_notification, nextPendingIntent);
                view.setOnClickPendingIntent(R.id.iv_previous_notification, previousPendingIntent);
                if (Commen.song != null) {
                    view.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
                    view.setTextViewText(R.id.tv_appName_notification, "PSMusic");
                    view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
                }

                bigView.setOnClickPendingIntent(R.id.iv_playPayse_notification, playPendingIntent);
                bigView.setOnClickPendingIntent(R.id.iv_next_notification, nextPendingIntent);
                bigView.setOnClickPendingIntent(R.id.iv_previous_notification, previousPendingIntent);
                if (Commen.song != null) {
                    bigView.setTextViewText(R.id.tv_SongName_notification, Commen.song.getSongName());
                    bigView.setTextViewText(R.id.tv_appName_notification, "PSMusic");
                    bigView.setTextViewText(R.id.tv_ArtistName_notification, Commen.song.getArtistName());
                    bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_pause_24px);
                }


                notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL);
                notification.setContent(view).
                        setCustomBigContentView(bigView)
                        .setSmallIcon(R.drawable.icon1)
                        .setContentText(Commen.song.getSongName())
                        .setOngoing(true)
                        .setColor(getResources().getColor(R.color.colorPrimary))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(PendingIntent.getActivity(this, 0, showMusicPlayerActivityIntent, 0));
                notificationManager.notify(NOTIFICATION_ID, notification.build());
                startForeground(NOTIFICATION_ID, notification.build());

                break;
        }
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Commen.mediaPlayer.release();
        onNotificationServiceStateChangedList = null;
        onNotificationServiceStateChangedPlay = null;
        if (PlaySongActivity.timer != null) {
            PlaySongActivity.timer.purge();
            PlaySongActivity.timer.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void onMediaPlayerPrepared() {

    }

    @Override
    public void onMediaPlayerCompletion() {
        view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
        bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
        notificationManager.notify(NOTIFICATION_ID, notification.build());
        stopForeground(true);
        stopSelf();
    }

//    @Override
//    public void onAudioFocusChangeService(int i) {
//
//                if (Commen.mediaPlayer.isPlaying()) {
//                    Commen.mediaPlayer.pause();
//                    view.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
//                    bigView.setImageViewResource(R.id.iv_playPayse_notification, R.drawable.ic_play_24px);
//                    notificationManager.notify(NOTIFICATION_ID, notification.build());
//                    if (onNotificationServiceStateChangedList != null) {
//                        onNotificationServiceStateChangedList.onPlayButtonClickedList();
//                    }
//
//                    if (onNotificationServiceStateChangedPlay != null) {
//                        onNotificationServiceStateChangedPlay.onPlayButtonClickedPlaySong();
//                    }
//                }
//
//
//    }


    public interface onNotificationServiceStateChangedPlay {
        void onPlayButtonClickedPlaySong();

        void onNextButtonClickedPlaySong();

        void onPrevioudButtonClickedPlaySong();
    }

    public interface onNotificationServiceStateChangedList {
        void onPlayButtonClickedList();

        void onNextButtonClickedList();

        void onPrevioudButtonClickedList();
    }

}
