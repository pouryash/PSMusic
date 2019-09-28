package com.example.ps.musicps.View;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ps.musicps.Adapter.SongAdapter;
import com.example.ps.musicps.Commen.Commen;
import com.example.ps.musicps.Commen.CustomeDialogClass;
import com.example.ps.musicps.Commen.RuntimePermissionsActivity;
import com.example.ps.musicps.MVP.SongsListMVP;
import com.example.ps.musicps.MVP.SongsListPresenter;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.R;
import com.example.ps.musicps.Service.SongService;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;




public class SongListActivity extends RuntimePermissionsActivity implements SongAdapter.onSongAdapter,
        SongsListMVP.RequiredSongsListViewOps {

    private static final int READ_EXTERNAL_STORAGE = 1;
    public static boolean shouldStartNewInstance = false;
    public static SongsListMVP.ProvidedPresenterOps mPresenter = new SongsListPresenter();
    public static boolean isPlaySongActivityEnabled;
    public static ArrayList<Song> songList = new ArrayList<>();
    public static onDataRecived onDataRecived;
    SongAdapter songAdapter;
    RecyclerView recyclerView;
    Toolbar toolbar;
    ImageView noItems;
    View fView;
    PlayingSongFragment playingSongFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    CustomeDialogClass customeDialog;
    FirebaseAnalytics firebaseAnalytics;


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
            SongListActivity.super.requestAppPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}
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


        if (PlaySongActivity.isExternalSource) {

            Intent intent = new Intent(this, PlaySongActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            this.finish();

        }
        super.onResume();
    }

    private void setupView() {
        //when song removed from search activity
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        SearchActivity.onSearchedItemRemoved = new SearchActivity.onSearchedItemRemoved() {
            @Override
            public void onRemoved(int position, boolean isCurentSong, List<Song> list) {
                songAdapter.notifyItemRemoved(position);
                songAdapter.notifyItemRangeRemoved(position, songList.size() - 1);
                songAdapter.notifyDataSetChanged();
                if (isCurentSong) {
                    if (songList.size() > 0) {
                        onDataRecived.onSongRecived(songList.get(0), false);
                        //when curent song deleted and then open first song by PlayingSongFragment
                        PlaySongActivity.song = songList.get(0);
                    } else {
                        fView.setVisibility(View.GONE);
                        noItems.setVisibility(View.VISIBLE);
                    }
                }
            }
        };
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

        fView = findViewById(R.id.fr_PlayingSong);
        noItems = findViewById(R.id.iv_No_Items);
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
        Bundle bundle = new Bundle();
        switch (item.getItemId()) {
            case R.id.menu_search:
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "menu item search");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                if (fView.getVisibility() != View.VISIBLE) {
                    Toast.makeText(this, "There is no song on your phone. Try again later!",
                            Toast.LENGTH_LONG).show();

                } else {
                    startActivity(new Intent(SongListActivity.this, SearchActivity.class));
                }
                break;
            case R.id.menu_scan:
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "menu item scan");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                if (songList.size() == 0 && Commen.mediaPlayer != null) {
                    shouldStartNewInstance = true;
                    Commen.mediaPlayer.release();
                    Commen.mediaPlayer = null;
                }
                customeDialog = new CustomeDialogClass(this);
                WindowManager.LayoutParams lp = customeDialog.getWindow().getAttributes();
                lp.dimAmount = 0.7f;
                lp.gravity = Gravity.BOTTOM;
                customeDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                customeDialog.show();
                customeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                customeDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                customeDialog.setCanceledOnTouchOutside(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (customeDialog.isShowing()) {
                            mPresenter.getSongsList();
                        }
                    }
                }, 1500);


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
        String displayedText = ((TextView) ((LinearLayout) toast.getView()).getChildAt(0)).getText().toString();
        if (displayedText.equals("opss no song found!")) {
            if (customeDialog != null && customeDialog.isShowing()) {
                customeDialog.dismiss();
            }
            fView.setVisibility(View.GONE);
            noItems.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSongListFinished(ArrayList<Song> songs) {

        if (customeDialog != null && customeDialog.isShowing()) {
            if (songList.size() == 0) {
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fr_PlayingSong, new PlayingSongFragment());
                fragmentTransaction.commit();
                onDataRecived.onSongRecived(songs.get(0), false);
                customeDialog.dismiss();
            }
            customeDialog.dismiss();
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            songAdapter = new SongAdapter(songs, this, this);
            recyclerView.setAdapter(songAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            if (noItems != null && fView != null) {
                fView.setVisibility(View.VISIBLE);
                noItems.setVisibility(View.GONE);
            }
            Toast.makeText(getActivityContext(), "Scan for Song is Completed", Toast.LENGTH_LONG).show();
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            songAdapter = new SongAdapter(songs, this, this);
            recyclerView.setAdapter(songAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            onDataRecived.onListReciveed(songs);
        }
        songList = songs;

    }


    @Override
    public void onSongRecived(Song song) {

        onDataRecived.onSongRecived(song, true);
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
                }
                Commen.mediaPlayer.release();
                Commen.IS_PLAYING = false;
            }
        }else {
//           if (!PlayingSongFragment.isFragmentRootClicked){
//               Commen.mediaPlayer.release();
//               Commen.IS_PLAYING = false;
//               // this is for when start song from fragment and clicked root(problem is restart mediaplayer)
//               PlayingSongFragment.isFragmentRootClicked = false;
//           }
        }
        intent.putExtra("position", pos);
        File file = new File(songList.get(pos).getTrackFile());
        if (file.exists()) {
            startActivity(intent);

            //analetics
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, songList.get(pos).getSongName());
            bundle.putString("ITEM_DESCRIPTION", songList.get(pos).getArtistName());
            bundle.putString("ITEM_LOCATION", songList.get(pos).getArtistName());
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
        } else {
            Toast.makeText(getActivityContext(), "this file not exists!", Toast.LENGTH_LONG).show();

//            MediaScannerConnection.scanFile(
//                    this,
//                    new String[]{songList.get(pos).getTrackFile(), null},
//                    null, null);
//            songList.remove(pos);
//            songAdapter.notifyItemRemoved(pos);
//            songAdapter.notifyItemRangeRemoved(pos,songAdapter.getItemCount());
//            songAdapter.notifyDataSetChanged();
        }
        isPlaySongActivityEnabled = true;

    }

    @Override
    public void onSongRemoved(int pos, boolean isCurent, List<Song> list) {

//this is for when delete song in serach and then delete in list again(update fragment data)
        if (SearchActivity.isIntentFromSearch) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fr_PlayingSong, new PlayingSongFragment());
            fragmentTransaction.commit();
            SearchActivity.isIntentFromSearch = false;
        }
//this is caled when song removed and check if is curent playing song removed
        songAdapter.notifyDataSetChanged();
        songList = (ArrayList<Song>) Commen.notifyListchanged(pos, songList);
        if (isCurent) {
            if (list.size() > 0) {
                onDataRecived.onSongRecived(songList.get(0), false);
                //when curent song deleted and then open first song by PlayingSongFragment
                PlaySongActivity.song = songList.get(0);
            } else if (list.size() == 0) {
                //this is for when delete last song
                fView.setVisibility(View.GONE);
                noItems.setVisibility(View.VISIBLE);
            }
        }
        //this is for when delete first Song multiple(wiich is in curent song fragment)
        if (list.size() < songList.size()) {
            if (list.size() > 0) {
                onDataRecived.onSongRecived(list.get(0), false);
                PlaySongActivity.song = list.get(0);
            }
        }
        if (list.size() == 0) {
            //this is for when delete last song
            fView.setVisibility(View.GONE);
            noItems.setVisibility(View.VISIBLE);
        }
        songList = (ArrayList<Song>) list;
    }

    @Override
    protected void onDestroy() {
        if (Commen.IS_PLAYING) {
            Commen.IS_PLAYING = false;
        }
        if (!PlaySongActivity.isExternalSource) {
            stopService(new Intent(SongListActivity.this, SongService.class));
            if (Commen.mediaPlayer != null){
                Commen.mediaPlayer.release();
            }
        }
        songList = null;
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }





    public interface onDataRecived {
        void onSongRecived(Song song, boolean shouldPlay);

        void onListReciveed(ArrayList<Song> songs);
    }
}

