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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.example.ps.musicps.Commen.Const;
import com.example.ps.musicps.Commen.RuntimePermissionsActivity;
import com.example.ps.musicps.Commen.SongSharedPrefrenceManager;
import com.example.ps.musicps.MVP.SongsListMVP;
import com.example.ps.musicps.MVP.SongsListPresenter;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.Service.SongService;

import java.util.ArrayList;

public class SongListActivity extends RuntimePermissionsActivity implements SongAdapter.onSongClicked,
        SongsListMVP.RequiredSongsListViewOps{

    private static final int READ_EXTERNAL_STORAGE = 1;
    public static SongsListMVP.ProvidedPresenterOps mPresenter = new SongsListPresenter();
    public static boolean isPlaySongActivityEnabled;
    public static ArrayList<Song> songList = new ArrayList<>();
    public static onDataRecived onDataRecived;
    SongAdapter songAdapter;
    RecyclerView recyclerView;
    Toolbar toolbar;
    PlayingSongFragment playingSongFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;



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


            playingSongFragment = new PlayingSongFragment();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.commit();
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
        if (PlaySongActivity.isExternalSource){

                Intent intent = new Intent(this , PlaySongActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                this.finish();

        }
        super.onResume();
    }

    private void setupView() {
        PlayingSongFragment.onGetSong = new PlayingSongFragment.onGetSong() {
            @Override
            public void getSong(int Position) {
                mPresenter.getSong(Position);
            }

            @Override
            public void onPlayingSongClicked(int position) {
                onSongClicked(position);
            }
        };
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        mPresenter.getSongsList();
        setSupportActionBar(toolbar);

    }

    @Override
    public void onBackPressed() {
        //TODO if you want to still play on backpressed
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
//        super.onBackPressed();
    }

    private void initView() {

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
        onDataRecived.onListReciveed(songs);
    }


    @Override
    public void onSongRecived(Song song) {

        onDataRecived.onSongRecived(song);
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
        if(!PlaySongActivity.isExternalSource){
            stopService(new Intent(SongListActivity.this,SongService.class));
            Commen.mediaPlayer.release();
        }
        songList = null;
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public interface onDataRecived {
        void onSongRecived(Song song);

        void onListReciveed(ArrayList<Song> songs);
    }
}

