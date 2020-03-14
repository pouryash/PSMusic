//package com.example.ps.musicps.View;
//
//import android.app.PendingIntent;
//import android.appwidget.AppWidgetManager;
//import android.appwidget.AppWidgetProvider;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.ColorFilter;
//import android.graphics.LightingColorFilter;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.widget.RemoteViews;
//import com.example.ps.musicps.Commen.Commen;
//import com.example.ps.musicps.Commen.SongSharedPrefrenceManager;
//import com.example.ps.musicps.Helper.MusiPlayerHelper;
//import com.example.ps.musicps.MVP.WidgetPresenter;
//import com.example.ps.musicps.MVP.WidgetSongsListMVP;
//import com.example.ps.musicps.Model.Song;
//import com.example.ps.musicps.R;
//import com.example.ps.musicps.Service.SongService;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
///**
// * Implementation of App Widget functionality.
// */
//public class PSMusicWidget extends AppWidgetProvider implements WidgetSongsListMVP.RequiredSongsListViewOps,
//        MusiPlayerHelper.onMediaPlayerStateChanged,
////        PlayingSongFragment.onSongListActivityStateCHangedWidget,
//        SongService.onNotificationServiceStateChangedWidget, PlaySongActivity.onSongPlaySongStateChangedWidget,
//        SongListActivity.onSongClickedWidget {
//
//
//    public static onWidgetStatusChanged onWidgetStatusChangedService;
//    public static onWidgetStatusChanged onWidgetStatusChangedPlaySong;
//    public static WidgetSongsListMVP.ProvidedPresenterOps mPresenter;
//    public static boolean SHOULD_SERVICE_START = false;
//    private static final String PLAYPAUSE_CLICK = "playpause";
//    private static final String NEXT_CLICK = "nexttrack";
//    private static final String PREVEOUS_CLICK = "preveoustrack";
//    private static ArrayList<Song> songList = new ArrayList<>();
//    private static SongSharedPrefrenceManager songSharedPrefrenceManager;
//    private Context context;
//    private static boolean isDefaultSize = true;
//
//
//    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
//                         int appWidgetId) {
//        if (mPresenter == null) {
//            songSharedPrefrenceManager = new SongSharedPrefrenceManager(context);
//            mPresenter = new WidgetPresenter(context, this);
//            Commen.getInstance().setupMediaPLayer(context, songSharedPrefrenceManager.getSong()
//                    , PSMusicWidget.this);
//        }
//
//        mPresenter.getSongsList();
//
//
//        // Construct the RemoteViews object
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.psmusic_widget);
//        views.setTextViewText(R.id.tv_service_trackName, Commen.song.getSongName());
//        try {
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(Commen.song.getSongImageUri()));
//            Bitmap resizedbitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight() / 2);
//            views.setImageViewBitmap(R.id.iv_service_album, resizedbitmap1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        views.setOnClickPendingIntent(R.id.ib_service_play, getPendingSelfIntent(context, PLAYPAUSE_CLICK));
//        views.setOnClickPendingIntent(R.id.ib_service_next, getPendingSelfIntent(context, NEXT_CLICK));
//        views.setOnClickPendingIntent(R.id.ib_service_prev, getPendingSelfIntent(context, PREVEOUS_CLICK));
//
//        // Instruct the widget manager to update the widget
//        appWidgetManager.updateAppWidget(appWidgetId, views);
//    }
//
//    @Override
//    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//        // There may be multiple widgets active, so update all of them
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }
//    }
//
//    @Override
//    public void onEnabled(Context context) {
//        // Enter relevant functionality for when the first widget is created
//        songSharedPrefrenceManager = new SongSharedPrefrenceManager(context);
//        mPresenter = new WidgetPresenter(context, this);
//        //TODO 111111111
//        Commen.getInstance().setupMediaPLayer(context, songSharedPrefrenceManager.getSong()
//                , PSMusicWidget.this);
//        if (PlayingSongFragment.onSongListActivityStateCHangedWidget == null) {
//            PlayingSongFragment.onSongListActivityStateCHangedWidget = this;
//        }
//        if (SongService.onNotificationServiceStateChangedWidget == null) {
//            SongService.onNotificationServiceStateChangedWidget = this;
//        }
//        if (PlaySongActivity.onSongPlaySongStateChangedWidget == null) {
//            PlaySongActivity.onSongPlaySongStateChangedWidget = this;
//        }
//        if (SongListActivity.onSongClickedWidget == null) {
//            SongListActivity.onSongClickedWidget = this;
//        }
//
//    }
//
//    @Override
//    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
//        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
//        int width = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
//        int height = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
//        if (height > 40) {
//            isDefaultSize = false;
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.psmusic_widget);
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(Commen.song.getSongImageUri()));
//                views.setImageViewBitmap(R.id.iv_service_album, bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            AppWidgetManager.getInstance(context).updateAppWidget(
//                    new ComponentName(context, PSMusicWidget.class), views);
//        }
//        if (height < 100) {
//            isDefaultSize = true;
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.psmusic_widget);
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(Commen.song.getSongImageUri()));
//                Bitmap resizedbitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight() / 2);
//                views.setImageViewBitmap(R.id.iv_service_album, resizedbitmap1);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            AppWidgetManager.getInstance(context).updateAppWidget(
//                    new ComponentName(context, PSMusicWidget.class), views);
//
//        }
//    }
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//        super.onReceive(context, intent);
//        if (mPresenter == null) {
//            songSharedPrefrenceManager = new SongSharedPrefrenceManager(context);
//            mPresenter = new WidgetPresenter(context, this);
//            Commen.getInstance().setupMediaPLayer(context, songSharedPrefrenceManager.getSong()
//                    , PSMusicWidget.this);
//        }
//        if (SongListActivity.onSongClickedWidget == null) {
//            SongListActivity.onSongClickedWidget = this;
//        }
//        if (PlaySongActivity.onSongPlaySongStateChangedWidget == null) {
//            PlaySongActivity.onSongPlaySongStateChangedWidget = this;
//        }
//        if (SongService.onNotificationServiceStateChangedWidget == null) {
//            SongService.onNotificationServiceStateChangedWidget = this;
//        }
//        if (PlayingSongFragment.onSongListActivityStateCHangedWidget == null) {
//            PlayingSongFragment.onSongListActivityStateCHangedWidget = this;
//        }
//        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.psmusic_widget);
//        this.context = context;
//
//        if (mPresenter != null && !(songList.size() > 0)) {
//            mPresenter.getSongsList();
//        }
//
//
//        switch (intent.getAction()) {
//
//            case PLAYPAUSE_CLICK:
//
//                if (!Commen.IS_PLAYING) {
//                    if (!Commen.isServiceRunning(SongService.class, context)) {
//                        SHOULD_SERVICE_START = true;
//                        context.startService(new Intent(context, SongService.class));
//                    }
//                    if (Commen.song == null) {
//                        Commen.getInstance().setupMediaPLayer(context, songSharedPrefrenceManager.getSong()
//                                , PSMusicWidget.this);
//                    }
//                    try {
//                        Commen.getInstance().FadeIn(2);
//                    } catch (Exception e) {
//                        Commen.getInstance().setupMediaPLayer(context, Commen.song, new Commen.onMediaPlayerStateChanged() {
//                            @Override
//                            public void onMediaPlayerPrepared() {
//                                views.setImageViewResource(R.id.ib_service_play, R.drawable.ic_pause_white_24px);
//
//                            }
//
//                            @Override
//                            public void onMediaPlayerCompletion() {
//                                views.setImageViewResource(R.id.ib_service_play, R.drawable.ic_play_white_24px);
//                            }
//                        });
//                    }
//                    views.setImageViewResource(R.id.ib_service_play, R.drawable.ic_pause_white_24px);
//                    AppWidgetManager.getInstance(context).updateAppWidget(
//                            new ComponentName(context, PSMusicWidget.class), views);
//                    Commen.IS_PLAYING = true;
//                } else {
//                    Commen.getInstance().FadeOut(2);
//                    views.setImageViewResource(R.id.ib_service_play, R.drawable.ic_play_white_24px);
//                    AppWidgetManager.getInstance(context).updateAppWidget(
//                            new ComponentName(context, PSMusicWidget.class), views);
//
//                }
//                if (onWidgetStatusChangedService != null) {
//                    onWidgetStatusChangedService.onPlayPauseClicked();
//                }
//                if (onWidgetStatusChangedPlaySong != null) {
//                    onWidgetStatusChangedPlaySong.onPlayPauseClicked();
//                }
//                break;
//            case NEXT_CLICK:
////                mPresenter.getSong(Commen.song.getId() + 1);
//                onSongRecived(songList.get(Commen.song.getId()+1));
//                break;
//            case PREVEOUS_CLICK:
////                mPresenter.getSong(Commen.song.getId() - 1);
//                onSongRecived(songList.get(Commen.song.getId()-1));
//
//            default:
//                break;
//        }
//
//    }
//
//    @Override
//    public void onSongListFinished(ArrayList<Song> songs) {
//        this.songList = songs;
//    }
//
//    @Override
//    public void onSongRecived(Song song) {
//        if (Commen.mediaPlayer != null) {
//            Commen.mediaPlayer.release();
//        }
//        Commen.getInstance().setupMediaPLayer(context, song, this);
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.psmusic_widget);
//        views.setTextViewText(R.id.tv_service_trackName, song.getSongName());
//        if (isDefaultSize) {
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(Commen.song.getSongImageUri()));
//                Bitmap resizedbitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight() / 2);
//                views.setImageViewBitmap(R.id.iv_service_album, resizedbitmap1);
//            } catch (IOException e) {
//                e.printStackTrace();
//                views.setImageViewResource(R.id.iv_service_album, R.color.colorNoSong);
//            }
//        } else {
//            try {
//
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(Commen.song.getSongImageUri()));
//                views.setImageViewBitmap(R.id.iv_service_album, bitmap);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                views.setImageViewResource(R.id.iv_service_album, R.color.colorNoSong);
//            }
//
//        }
//        AppWidgetManager.getInstance(context).updateAppWidget(
//                new ComponentName(context, PSMusicWidget.class), views);
//        if (Commen.IS_PLAYING) {
//            Commen.getInstance().FadeIn(2);
//        }
//        if (onWidgetStatusChangedService != null) {
//            onWidgetStatusChangedService.onSongChanged();
//        }
//        if (onWidgetStatusChangedPlaySong != null) {
//            onWidgetStatusChangedPlaySong.onSongChanged();
//        }
//    }
//
//    private PendingIntent getPendingSelfIntent(Context context, String action) {
//        Intent intent = new Intent(context, getClass());
//        intent.setAction(action);
//        return PendingIntent.getBroadcast(context, 0, intent, 0);
//    }
//
//    @Override
//    public void onMediaPlayerPrepared() {
//
//    }
//
//    @Override
//    public void onMediaPlayerCompletion() {
//
//    }
//
//
//    @Override
//    public void onPlaypauseClicked() {
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.psmusic_widget);
//        if (Commen.IS_PLAYING) {
//            views.setImageViewResource(R.id.ib_service_play, R.drawable.ic_pause_white_24px);
//        } else {
//            views.setImageViewResource(R.id.ib_service_play, R.drawable.ic_play_white_24px);
//        }
//        AppWidgetManager.getInstance(context).updateAppWidget(
//                new ComponentName(context, PSMusicWidget.class), views);
//
//    }
//
//    private Bitmap darkenBitMap(Bitmap bm) {
//
//        Canvas canvas = new Canvas(bm);
//        Paint p = new Paint(Color.RED);
//        //ColorFilter filter = new LightingColorFilter(0xFFFFFFFF , 0x00222222); // lighten
//        ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
//        p.setColorFilter(filter);
//        canvas.drawBitmap(bm, new Matrix(), p);
//
//        return bm;
//    }
//
//    @Override
//    public void onPlayPauseClicked() {
//        onPlaypauseClicked();
//    }
//
//    @Override
//    public void onSongChanged() {
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.psmusic_widget);
//        views.setTextViewText(R.id.tv_service_trackName, Commen.song.getSongName());
//        try {
//            if (isDefaultSize) {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(Commen.song.getSongImageUri()));
//                Bitmap resizedbitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight() / 2);
//                views.setImageViewBitmap(R.id.iv_service_album, resizedbitmap1);
//            } else {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(Commen.song.getSongImageUri()));
//                views.setImageViewBitmap(R.id.iv_service_album, bitmap);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        views.setImageViewResource(R.id.ib_service_play, R.drawable.ic_pause_white_24px);
//        AppWidgetManager.getInstance(context).updateAppWidget(
//                new ComponentName(context, PSMusicWidget.class), views);
//    }
//
//    @Override
//    public void onSongClicked(Song song) {
//        onSongRecived(song);
//    }
//
//    public interface onWidgetStatusChanged {
//
//        void onPlayPauseClicked();
//
//        void onSongChanged();
//    }
//
//}
//
