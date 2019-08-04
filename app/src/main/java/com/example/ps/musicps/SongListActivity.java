package com.example.ps.musicps;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ps.musicps.Adapter.SongAdapter;
import com.example.ps.musicps.Commen.Commen;
import com.example.ps.musicps.Commen.RuntimePermissionsActivity;
import com.example.ps.musicps.Commen.SongSharedPrefrenceManager;
import com.example.ps.musicps.MVP.SongsListMVP;
import com.example.ps.musicps.MVP.SongsListPresenter;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.Service.SongService;

import java.util.ArrayList;

public class SongListActivity extends RuntimePermissionsActivity implements SongAdapter.onSongClicked,
        SongsListMVP.RequiredSongsListViewOps, Commen.onMediaPlayerStateChanged,
        SongService.onNotificationServiceStateChangedList {

    private static final int READ_EXTERNAL_STORAGE = 1;
    public static onPlayingSongCompletion onPlayingSongCompletion;
    public static onSongListActivityStateChanged onSongListActivityStateChanged;
    public static SongsListMVP.ProvidedPresenterOps mPresenter = new SongsListPresenter();
    public static ArrayList<Song> songList = new ArrayList<>();
    SongAdapter songAdapter;
    RecyclerView recyclerView;
    Toolbar toolbar;
    ImageView playingSongImage;
    TextView playingSongName;
    TextView playingSongArtist;
    ImageView playPauseButtonPlayingSong;
    RelativeLayout playingSongRoot;
    SongSharedPrefrenceManager songSharedPrefrenceManager;
    Song curentSong;
    boolean shouldMediaPlayerStart;
    boolean isPlaySongActivityEnabled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            mPresenter.setView(this);
            initView();
            setupView();
        } else {
            SongListActivity.super.requestAppPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                    , READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        mPresenter.setView(this);
        initView();
        setupView();
    }

    @Override
    public void onPermissionsDeny(int requestCode) {
        Toast.makeText(this, "We Need Permission To Run!!", Toast.LENGTH_LONG).show();
        this.finish();
    }

    @Override
    protected void onResume() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && playingSongName != null) {
            if (PlaySongActivity.song != null) {
                setupPlayingSong(PlaySongActivity.song, Commen.mediaPlayer);
            } else if (playingSongName.getText().equals("")) {
                curentSong = songSharedPrefrenceManager.getSong();
                Commen.getInstance().setupMediaPLayer(SongListActivity.this, curentSong, this);

                isPlaySongActivityEnabled = false;
            }
        }
        SongService.onNotificationServiceStateChangedList = SongListActivity.this;
        super.onResume();
    }

    private void setupPlayingSong(Song song, MediaPlayer mediaPlayer) {

        Glide.with(this).asBitmap().load(Uri.parse(song.getSongImageUri()))
                .apply(new RequestOptions().placeholder(R.drawable.no_image))
                .into(playingSongImage);
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
            } else {
                playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));

            }
        } else {
            playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));
        }
        playingSongArtist.setText(song.getArtistName());
        playingSongName.setText(song.getSongName());

    }

    private void setupView() {
        PlaySongActivity.onPlaySongActivityCompletion = new PlaySongActivity.onPlaySongActivityCompletion() {
            @Override
            public void onCompletion() {
                playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));
                onSongListActivityStateChanged.onSongListPlaypauseMediaComplete();
            }
        };
        songSharedPrefrenceManager = new SongSharedPrefrenceManager(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        mPresenter.getSongsList();
        setSupportActionBar(toolbar);
        playingSongRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isPlaySongActivityEnabled) {
                    Intent intent = new Intent(SongListActivity.this, PlaySongActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } else {
                    onSongClicked(curentSong.getId());
                }

            }
        });
        playPauseButtonPlayingSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Commen.isServiceRunning(SongService.class, SongListActivity.this)) {
                    startService(new Intent(SongListActivity.this, SongService.class));
                }
                SongService.onNotificationServiceStateChangedList = SongListActivity.this;
                if (Commen.mediaPlayer.isPlaying()) {
                    Commen.mediaPlayer.pause();
                    playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));
                } else {
                    Commen.mediaPlayer.start();
                    playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
                }
                if (onSongListActivityStateChanged != null) {
                    onSongListActivityStateChanged.onSongListPlaypauseClicked();
                }
            }
        });

    }

    private void initView() {

        playPauseButtonPlayingSong = findViewById(R.id.iv_playPause_playingSong_SongList);
        playingSongRoot = findViewById(R.id.rl_playingSong_songList);
        playingSongImage = findViewById(R.id.iv_songImage_playingSong_songList);
        playingSongName = findViewById(R.id.tv_songName_playingSong_songList);
        playingSongArtist = findViewById(R.id.tv_artistName_playingSong);
        recyclerView = findViewById(R.id.rv_songList);
        toolbar = findViewById(R.id.toolbar_songList);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.song_list_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_search:
                startActivity(new Intent(SongListActivity.this,SearchActivity.class));
                break;
        }
        return true;
    }

    @Override
    public Context getAppContext() {
        return getApplication();
    }

    @Override
    public Activity getActivityContext() {
        return this;
    }

    @Override
    public void showToast(Toast toast) {
        toast.show();
    }

    @Override
    public void onSongListFinished(ArrayList<Song> songs) {
        songList = songs;
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        songAdapter = new SongAdapter(songs, this, this);
        recyclerView.setAdapter(songAdapter);
        if (!songSharedPrefrenceManager.getFirstIn()) {
            songSharedPrefrenceManager.setFirstIn();
            curentSong = songList.get(0);
            Commen.getInstance().setupMediaPLayer(SongListActivity.this, curentSong, this);
            setupPlayingSong(curentSong, null);
            isPlaySongActivityEnabled = false;
        }
    }

    @Override
    public void onSongRecived(Song song) {
        curentSong = song;
        Commen.mediaPlayer.release();
        Commen.getInstance().setupMediaPLayer(SongListActivity.this, curentSong, this);
        shouldMediaPlayerStart = true;
    }

    @Override
    public void onSongClicked(int pos) {

        Intent intent = new Intent(SongListActivity.this, PlaySongActivity.class);
        if (PlaySongActivity.song != null) {
            if (PlaySongActivity.song.getId() == pos && Commen.mediaPlayer != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            } else {
                if (PlaySongActivity.timer != null) {
                    PlaySongActivity.timer.purge();
                    PlaySongActivity.timer.cancel();
                    Commen.mediaPlayer.release();
                }
            }
        }
        intent.putExtra("position", pos);
        startActivity(intent);
        isPlaySongActivityEnabled = true;

    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(SongListActivity.this,SongService.class));
        Commen.mediaPlayer.release();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            songSharedPrefrenceManager.saveSong(PlaySongActivity.song);
        }
        super.onStop();
    }

    @Override
    public void onMediaPlayerPrepared() {
        if (shouldMediaPlayerStart){
            Commen.mediaPlayer.start();
        }
        setupPlayingSong(curentSong, Commen.mediaPlayer);
    }

    @Override
    public void onMediaPlayerCompletion() {
        playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));
        if (onPlayingSongCompletion!= null){
            onPlayingSongCompletion.onCompletion();

        }
        if (onSongListActivityStateChanged != null){
            onSongListActivityStateChanged.onSongListPlaypauseMediaComplete();
        }
    }

    @Override
    public void onPlayButtonClickedList() {
        if (Commen.mediaPlayer.isPlaying()) {
            playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
        } else {
            playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));

        }
    }

    @Override
    public void onNextButtonClickedList() {
        if (PlaySongActivity.song == null){
            mPresenter.getSong(Commen.song.getId() +1);
        }else {
            setupPlayingSong(PlaySongActivity.song,Commen.mediaPlayer);
        }
    }

    @Override
    public void onPrevioudButtonClickedList() {
        if (PlaySongActivity.song == null){
            mPresenter.getSong(Commen.song.getId() -1);
        }else {
            setupPlayingSong(PlaySongActivity.song,Commen.mediaPlayer);
        }
    }

    public interface onPlayingSongCompletion {
        void onCompletion();
    }

    public interface onSongListActivityStateChanged {
        void onSongListPlaypauseClicked();
        void onSongListPlaypauseMediaComplete();

    }
}

