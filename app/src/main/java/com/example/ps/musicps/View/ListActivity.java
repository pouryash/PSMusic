package com.example.ps.musicps.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.ps.musicps.Adapter.SongAdapter;
import com.example.ps.musicps.Commen.Commen;
import com.example.ps.musicps.Commen.MyApplication;
import com.example.ps.musicps.Commen.OnAppCleared;
import com.example.ps.musicps.Commen.RuntimePermissionsActivity;
import com.example.ps.musicps.Commen.SongSharedPrefrenceManager;
import com.example.ps.musicps.Commen.VolumeContentObserver;
import com.example.ps.musicps.Di.component.DaggerSongListComponent;
import com.example.ps.musicps.Di.component.SongListComponent;
import com.example.ps.musicps.Di.module.ListActivityModule;
import com.example.ps.musicps.Di.module.SongAdapterModule;
import com.example.ps.musicps.Helper.MusiPlayerHelper;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.Model.SongInfo;
import com.example.ps.musicps.R;
import com.example.ps.musicps.Repository.SongRepository;
import com.example.ps.musicps.View.Dialog.CustomeDialogClass;
import com.example.ps.musicps.databinding.ActivityListBinding;
import com.example.ps.musicps.databinding.PlayingSongPanelBinding;
import com.example.ps.musicps.databinding.SongInfoDialogBinding;
import com.example.ps.musicps.viewmodels.SongInfoViewModel;
import com.example.ps.musicps.viewmodels.SongPanelViewModel;
import com.example.ps.musicps.viewmodels.SongViewModel;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class ListActivity extends RuntimePermissionsActivity implements SongAdapter.onSongAdapter,
        MusiPlayerHelper.onMediaPlayerStateChanged {

    private static final int READ_EXTERNAL_STORAGE = 1;
    ActivityListBinding binding;
    MusiPlayerHelper musiPlayerHelper;
    SongSharedPrefrenceManager sharedPrefrenceManager;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    CustomeDialogClass customeDialog;
    FirebaseAnalytics firebaseAnalytics;
    @Inject
    SongViewModel songViewModel;
    @Inject
    SongInfoViewModel songInfoViewModel;
    SongInfoDialogBinding songInfoBinding;
    @Inject
    SongPanelViewModel songPanelViewModel;
    @Inject
    VolumeContentObserver volumeContentObserver;
    Dialog infoDialog;
    List<SongViewModel> songViewModelList = new ArrayList<>();
    SongListComponent component;
    boolean isSetupedPanel = false;
    boolean isSeekbarTouched = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        component = DaggerSongListComponent.builder()
                .listActivityModule(new ListActivityModule(this))
                .build();
        component.inject(this);

        binding.setSong(songViewModel);
        songInfoBinding = SongInfoDialogBinding.inflate(getLayoutInflater());
        startService(new Intent(getBaseContext(), OnAppCleared.class));

        ListActivity.super.requestAppPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}
                , READ_EXTERNAL_STORAGE);

    }

    @Override
    public void onPermissionsGranted(int requestCode) {

        initialize();
        setupView();
        songViewModel.getSongs();
    }

    @Override
    public void onPermissionsDeny(int requestCode) {
        Toast.makeText(this, "We Need Permission To Run!!", Toast.LENGTH_LONG).show();
        this.finish();
    }

    @Override
    protected void onResume() {


//        if (PlaySongActivity.isExternalSource) {
//
//            Intent intent = new Intent(this, PlaySongActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//            startActivity(intent);
//            this.finish();
//            return;
//
//        }
        super.onResume();
    }

    private void setupPanel(Song song) {

        if (song.getId() == songPanelViewModel.getId())
            return;

        songPanelViewModel.setId(song.getId());
        songPanelViewModel.setSongName(song.getSongName());
        songPanelViewModel.setImageUri(song.getSongImageUri());
        songPanelViewModel.setAlbumName(song.getAlbumName());
        songPanelViewModel.setDuration(song.getDuration());
        songPanelViewModel.setPath(song.getTrackFile());


        isSetupedPanel = true;

        binding.slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);


        if (musiPlayerHelper.mediaPlayer != null && !musiPlayerHelper.mediaPlayer.isPlaying()) {
            binding.panel.ivPlayPauseCollpase.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));
            binding.panel.ivPlayPayseExpand.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, null));

        } else {
            binding.panel.ivPlayPauseCollpase.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
            binding.panel.ivPlayPayseExpand.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, null));
            if (musiPlayerHelper.getTimer() == null) {
                seekBarProgressUpdater();
            }
        }
    }

    private void setupView() {
        //when song removed from search activity
        SearchActivity.onSearchedItemRemoved = new SearchActivity.onSearchedItemRemoved() {
            @Override
            public void onRemoved(int position, boolean isCurentSong, List<Song> list) {
//                songAdapter.notifyItemRemoved(position);
//                songAdapter.notifyItemRangeRemoved(position, songViewModelList.size() - 1);
//                songAdapter.notifyDataSetChanged();
//                if (isCurentSong) {
//                    if (songViewModelList.size() > 0) {
////                        onDataRecived.onSongRecived(songList.get(0), false);
//                        //when curent song deleted and then open first song by PlayingSongFragment
////                        PlaySongActivity.song = songList.get(0);
//                    } else {
//                        binding.ivNoItems.setVisibility(View.VISIBLE);
//                    }
//                }
            }
        };

        songViewModel.getMutableSongViewModelList().observe(this, songViewModels -> {
            if (sharedPrefrenceManager.getSong().getSongName().equals("") && songViewModels.size() > 0) {
                Song song = new Song();
                song.setSongName(songViewModels.get(0).getSongName());
                song.setAlbumName(songViewModels.get(0).getAlbumName());
                song.setArtistName(songViewModels.get(0).getArtistName());
                song.setTrackFile(songViewModels.get(0).getTrackFile());
                song.setDuration(songViewModels.get(0).getDuration());
                song.setSongImageUri(songViewModels.get(0).getSongImageUri());
                song.setId(songViewModels.get(0).getId());
                song.setContentID(songViewModels.get(0).getContentID());
                sharedPrefrenceManager.saveSong(song);
            }
            ListActivity.this.songViewModelList.clear();
            ListActivity.this.songViewModelList.addAll(songViewModels);
            if (customeDialog != null && customeDialog.isShowing()) {
                customeDialog.dismiss();
                if (songViewModels.size() > 0) {
                    Toast.makeText(this, "scan for song is successfuly complete", Toast.LENGTH_LONG).show();
                    if (binding.ivNoItems.getVisibility() == View.VISIBLE) {
                        binding.ivNoItems.setVisibility(View.GONE);
                    }
                }
            }
            if (!isSetupedPanel) {
                musiPlayerHelper.setupMediaPLayer(this, sharedPrefrenceManager.getSong(), ListActivity.this);
                setupPanel(sharedPrefrenceManager.getSong());
            }
            if (songViewModels.size() == 0) {
                Toast.makeText(this, "No songs found, try again later", Toast.LENGTH_LONG).show();
                if (binding.ivNoItems.getVisibility() != View.VISIBLE) {
                    binding.ivNoItems.setVisibility(View.VISIBLE);
                }

            }
        });

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setSupportActionBar(binding.toolbarSongList);

        binding.slidingPanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                binding.panel.rlSlidePanelTop.setAlpha(1 - slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState
                    previousState, SlidingUpPanelLayout.PanelState newState) {
                switch (newState) {
                    case EXPANDED:
                        binding.panel.rlSlidePanelTop.setClickable(false);
                        break;
                    case COLLAPSED:
                        binding.panel.rlSlidePanelTop.setClickable(true);
                        break;
                    case HIDDEN:
                        break;
                    case DRAGGING:
                        break;
                    case ANCHORED:
                        break;

                }
            }
        });

        binding.panel.sbSoundPlaySong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                volumeContentObserver.setVolume(i);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "seekbar volume");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!songPanelViewModel.isSoundOn())
                    songPanelViewModel.setSoundOn(true);
            }
        });

        binding.panel.ivPlayPauseCollpase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musiPlayerHelper.mediaPlayer.isPlaying()) {
                    musiPlayerHelper.FadeOut(2);
                    binding.panel.ivPlayPauseCollpase.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));
                    binding.panel.ivPlayPayseExpand.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, null));
                } else {
                    musiPlayerHelper.FadeIn(2);
                    musiPlayerHelper.mediaPlayer.start();
                    binding.panel.ivPlayPauseCollpase.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
                    binding.panel.ivPlayPayseExpand.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, null));
                    if (musiPlayerHelper.getTimer() == null) {
                        seekBarProgressUpdater();
                    }
                }
            }
        });

        binding.panel.ivPlayPayseExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musiPlayerHelper.mediaPlayer.isPlaying()) {
                    musiPlayerHelper.FadeOut(2);
                    binding.panel.ivPlayPauseCollpase.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));
                    binding.panel.ivPlayPayseExpand.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, null));
                } else {
                    musiPlayerHelper.FadeIn(2);
                    musiPlayerHelper.mediaPlayer.start();
                    binding.panel.ivPlayPauseCollpase.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
                    binding.panel.ivPlayPayseExpand.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, null));
                    if (musiPlayerHelper.getTimer() == null) {
                        seekBarProgressUpdater();
                    }
                }
            }
        });

        binding.panel.ivForwardExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Forward Button 30s");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                musiPlayerHelper.mediaPlayer.seekTo(musiPlayerHelper.mediaPlayer.getCurrentPosition() + 30000);

                songPanelViewModel.setProgressDuration(musiPlayerHelper.mediaPlayer.getCurrentPosition());
                songPanelViewModel.setCurrentDuration(Commen.changeDurationFormat(musiPlayerHelper.mediaPlayer.getCurrentPosition()));

            }
        });

        binding.panel.seekbarExpand.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
//                if (fromUser)
//                    musiPlayerHelper.mediaPlayer.seekTo(i);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekbarTouched = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekbarTouched = false;
                musiPlayerHelper.mediaPlayer.seekTo(seekBar.getProgress());
                songPanelViewModel.setProgressDuration(musiPlayerHelper.mediaPlayer.getCurrentPosition());
                songPanelViewModel.setCurrentDuration(Commen.changeDurationFormat(musiPlayerHelper.mediaPlayer.getCurrentPosition()));

            }
        });

        binding.panel.ivSoundSeekbarPlaySong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (songPanelViewModel.isSoundOn()) {
                    volumeContentObserver.setLastVolum(volumeContentObserver.getAudioManager().getStreamVolume(AudioManager.STREAM_MUSIC));
                    songPanelViewModel.setSoundOn(false);
                    volumeContentObserver.setVolume(0);
                } else {
                    songPanelViewModel.setSoundOn(true);
                    volumeContentObserver.setVolume(volumeContentObserver.getLastVolum());
                }
            }
        });

    }

    private void seekBarProgressUpdater() {

        if (musiPlayerHelper.mediaPlayer != null && musiPlayerHelper.mediaPlayer.isPlaying()) {

            musiPlayerHelper.setTimer(new Timer());
            musiPlayerHelper.getTimer().schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (!isSeekbarTouched) {
                                    songPanelViewModel.setProgressDuration(musiPlayerHelper.mediaPlayer.getCurrentPosition());
                                    songPanelViewModel.setCurrentDuration(Commen.changeDurationFormat(musiPlayerHelper.mediaPlayer.getCurrentPosition()));
                                }
                            } catch (Exception e) {

                            }
                        }
                    });
                }
            }, 0, 1000);
        }

    }

    @Override
    public void onBackPressed() {
        if (binding.slidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            binding.slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            //TODO if you want to still play on backpressed
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
//        super.onBackPressed();
        }

    }

    private void initialize() {

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        volumeContentObserver.setSongPanelViewModel(songPanelViewModel);
        binding.panel.setSong(songPanelViewModel);
        songInfoBinding.setSongInfo(songInfoViewModel);
        infoDialog = new Dialog(this, R.style.DialogTheme);
        infoDialog.setTitle("Login");
        infoDialog.setCancelable(true);
        infoDialog.setContentView(songInfoBinding.getRoot());
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        infoDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams lp = infoDialog.getWindow().getAttributes();
        lp.dimAmount = 0.7f;
        lp.gravity = Gravity.BOTTOM;
        songInfoBinding.tvCancelSongInfoDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoDialog.dismiss();
            }
        });
        sharedPrefrenceManager = ((MyApplication) getApplication()).getComponent().getSharedPrefrence();
        musiPlayerHelper = ((MyApplication) getApplication()).getComponent().getMusicPlayerHelper();

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

                break;
            case R.id.menu_scan:
                if (customeDialog == null || !customeDialog.isShowing()) {
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "menu item scan");
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                    if (songViewModelList.size() == 0 && musiPlayerHelper.mediaPlayer != null) {
                        musiPlayerHelper.mediaPlayer.release();
                        musiPlayerHelper.mediaPlayer = null;
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
                                songViewModel.updateSongs();
                            }
                        }
                    }, 1500);

                }
                break;

        }
        return true;
    }


    @Override
    public void onSongClicked(Song song) {


        if (song.getId() == sharedPrefrenceManager.getSong().getId() && musiPlayerHelper.mediaPlayer != null) {

            binding.slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            if (!musiPlayerHelper.mediaPlayer.isPlaying()) {
                musiPlayerHelper.FadeIn(2);
                binding.panel.ivPlayPauseCollpase.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
                binding.panel.ivPlayPayseExpand.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, null));
            }

        } else {
            if (musiPlayerHelper.mediaPlayer != null){
                musiPlayerHelper.mediaPlayer.release();
            }
            musiPlayerHelper.setupMediaPLayer(ListActivity.this, song, ListActivity.this);
            if (musiPlayerHelper.mediaPlayer == null) {
                    musiPlayerHelper.FadeIn(2);
            }else if (song.getId() != sharedPrefrenceManager.getSong().getId()){
                sharedPrefrenceManager.saveSong(song);
                setupPanel(song);
                binding.slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                musiPlayerHelper.FadeIn(2);
                binding.panel.ivPlayPauseCollpase.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
                binding.panel.ivPlayPayseExpand.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, null));
            }
        }


//TODO
        //analetics
//            Bundle bundle = new Bundle();
//            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, songList.get(pos).getSongName());
//            bundle.putString("ITEM_DESCRIPTION", songList.get(pos).getArtistName());
//            bundle.putString("ITEM_LOCATION", songList.get(pos).getArtistName());
//            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
//        } else {
//            Toast.makeText(getActivityContext(), "this file not exists!", Toast.LENGTH_LONG).show();

//            MediaScannerConnection.scanFile(
//                    this,
//                    new String[]{songList.get(pos).getTrackFile(), null},
//                    null, null);
//            songList.remove(pos);
//            songAdapter.notifyItemRemoved(pos);
//            songAdapter.notifyItemRangeRemoved(pos,songAdapter.getItemCount());
//            songAdapter.notifyDataSetChanged();
    }


    @Override
    public void onSongRemoved(int id, int size) {

        songViewModel.deleteSongById(id);

//this is for when delete song in serach and then delete in list again(update fragment data)
        if (SearchActivity.isIntentFromSearch) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.fr_PlayingSong, new PlayingSongFragment());
            fragmentTransaction.commit();
            SearchActivity.isIntentFromSearch = false;
        }

//        if (isCurent) {
//            if (list.size() > 0) {
////                onDataRecived.onSongRecived(songList.get(0), false);
//                //when curent song deleted and then open first song by PlayingSongFragment
////                PlaySongActivity.song = songList.get(0);
//            } else if (size == 0) {
//                //this is for when delete last song
//                binding.ivNoItems.setVisibility(View.VISIBLE);
//            }
//        }
//        //this is for when delete first Song multiple(wiich is in curent song fragment)
//        if (list.size() < songList.size()) {
//            if (list.size() > 0) {
////                onDataRecived.onSongRecived(list.get(0), false);
////                PlaySongActivity.song = list.get(0);
//            }
//        }
        if (size == 0) {
            //this is for when delete last song
            binding.ivNoItems.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMenuInfoClicked(SongInfo songInfo) {

        songInfoViewModel.setSongInfo(songInfo);

        infoDialog.show();
    }

    @Override
    protected void onDestroy() {
        if (musiPlayerHelper.getTimer() != null) {
            musiPlayerHelper.getTimer().purge();
            musiPlayerHelper.getTimer().cancel();
        }
        if (musiPlayerHelper.mediaPlayer != null) {
            musiPlayerHelper.mediaPlayer.release();
        }

        if (volumeContentObserver != null) {
            this.getContentResolver().unregisterContentObserver(volumeContentObserver);
        }

        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onMediaPlayerPrepared() {
        songPanelViewModel.setCurrentDuration(Commen.changeDurationFormat(musiPlayerHelper.mediaPlayer.getCurrentPosition()));
        songPanelViewModel.setMaxDuration(musiPlayerHelper.mediaPlayer.getDuration());
        songPanelViewModel.setProgressDuration(musiPlayerHelper.mediaPlayer.getCurrentPosition());
    }

    @Override
    public void onMediaPlayerCompletion() {

    }

    public SongListComponent getComponent() {
        return component;
    }
}
