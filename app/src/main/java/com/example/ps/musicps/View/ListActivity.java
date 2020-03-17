package com.example.ps.musicps.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.ps.musicps.Adapter.OnSongAdapter;
import com.example.ps.musicps.Commen.Commen;
import com.example.ps.musicps.Commen.MyApplication;
import com.example.ps.musicps.Commen.OnAppCleared;
import com.example.ps.musicps.Commen.RuntimePermissionsActivity;
import com.example.ps.musicps.Commen.SongSharedPrefrenceManager;
import com.example.ps.musicps.Commen.VolumeContentObserver;
import com.example.ps.musicps.Di.component.DaggerSongListComponent;
import com.example.ps.musicps.Di.component.SongListComponent;
import com.example.ps.musicps.Di.module.ListActivityModule;
import com.example.ps.musicps.Helper.MusiPlayerHelper;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.Model.SongInfo;
import com.example.ps.musicps.R;
import com.example.ps.musicps.View.Dialog.CustomeDialogClass;
import com.example.ps.musicps.databinding.ActivityListBinding;
import com.example.ps.musicps.databinding.SongInfoDialogBinding;
import com.example.ps.musicps.viewmodels.SongInfoViewModel;
import com.example.ps.musicps.viewmodels.SongPanelViewModel;
import com.example.ps.musicps.viewmodels.SongViewModel;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class ListActivity extends RuntimePermissionsActivity implements OnSongAdapter,
        MusiPlayerHelper.onMediaPlayerStateChanged {

    private static final int READ_EXTERNAL_STORAGE = 1;
    ActivityListBinding binding;
    MusiPlayerHelper musiPlayerHelper;
    SongSharedPrefrenceManager sharedPrefrenceManager;
    CustomeDialogClass customeDialog;
    FirebaseAnalytics firebaseAnalytics;
    List<SongViewModel> shuffleList = new ArrayList<>();
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
    boolean isInLongTouch;
    boolean isSongComplete = false;
    int longTouchPosition;
    int curentShuffleSong;
    Drawable drawableRepeatOne;
    Drawable drawableRepeatAll;
    Drawable drawableShuffle;


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

        if (musiPlayerHelper.mediaPlayer != null) {
            if (musiPlayerHelper.mediaPlayer.isPlaying()) {
                binding.panel.ivPlayPauseCollpase.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
                binding.panel.ivPlayPayseExpand.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, null));
                seekBarProgressUpdater();
            } else {
                binding.panel.ivPlayPauseCollpase.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));
                binding.panel.ivPlayPayseExpand.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, null));
            }
            if (songPanelViewModel.getId() != sharedPrefrenceManager.getSong().getId())
                setupPanel(sharedPrefrenceManager.getSong());
        }

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


        if (musiPlayerHelper.mediaPlayer != null && !musiPlayerHelper.mediaPlayer.isPlaying()) {
            binding.panel.ivPlayPauseCollpase.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));
            binding.panel.ivPlayPayseExpand.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, null));

        } else {
            binding.panel.ivPlayPauseCollpase.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
            binding.panel.ivPlayPayseExpand.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, null));
            seekBarProgressUpdater();
        }
    }

    private void setupView() {
        //when song removed from search activity
//        SearchActivity.onSearchedItemRemoved = new SearchActivity.onSearchedItemRemoved() {
//            @Override
//            public void onRemoved(int position, boolean isCurentSong, List<Song> list) {
////                songAdapter.notifyItemRemoved(position);
////                songAdapter.notifyItemRangeRemoved(position, songViewModelList.size() - 1);
////                songAdapter.notifyDataSetChanged();
////                if (isCurentSong) {
////                    if (songViewModelList.size() > 0) {
//////                        onDataRecived.onSongRecived(songList.get(0), false);
////                        //when curent song deleted and then open first song by PlayingSongFragment
//////                        PlaySongActivity.song = songList.get(0);
////                    } else {
////                        binding.ivNoItems.setVisibility(View.VISIBLE);
////                    }
////                }
//            }
//        };

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
                    binding.slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
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
                    seekBarProgressUpdater();
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
                    seekBarProgressUpdater();

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
                if (i > seekBar.getMax() / 2) {
                    isSongComplete = false;
                }
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

        songViewModel.getSongMutableLiveData().observe(this, this::onSongClicked);

        binding.panel.ivNextExpand.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                isInLongTouch = true;
                longTouchPosition = 2000;
                longTouchForward();
                return false;
            }
        });

        binding.panel.ivNextExpand.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (isInLongTouch) {
                        isInLongTouch = false;
                        songViewModel.setCanClick(false);
                    }
                }
                return false;
            }
        });

        binding.panel.ivPreviousExpand.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                isInLongTouch = true;
                longTouchPosition = 2000;
                longTouchBackward();
                return false;
            }
        });

        binding.panel.ivPreviousExpand.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (isInLongTouch) {
                        songViewModel.setCanClick(false);
                        isInLongTouch = false;
                    }
                }
                return false;
            }
        });

        binding.panel.ivRepeatExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Repeat Button ");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                switch (sharedPrefrenceManager.getPlayingState()) {
                    case "repeatOne":
                        binding.panel.ivRepeatExpand.setImageBitmap(Commen.getBitmapFromVectorDrawable(ListActivity.this,
                                getResources().getDrawable(R.drawable.ic_repeat_24px)));
                        musiPlayerHelper.mediaPlayer.setLooping(false);
                        sharedPrefrenceManager.setPlayingState("repeatList");
                        Toast.makeText(getApplicationContext(), "Repeat list", Toast.LENGTH_SHORT).show();
                        break;
                    case "repeatList":
                        binding.panel.ivRepeatExpand.setImageBitmap(Commen.getBitmapFromVectorDrawable(ListActivity.this,
                                getResources().getDrawable(R.drawable.ic_shuffle_24px)));
                        if (shuffleList.size() == 0) {
                            shuffleList = songViewModelList;
                        }
                        Collections.shuffle(shuffleList);
                        musiPlayerHelper.mediaPlayer.setLooping(false);
                        sharedPrefrenceManager.setPlayingState("shuffle");
                        Toast.makeText(getApplicationContext(), "Shuffle", Toast.LENGTH_SHORT).show();
                        break;
                    case "shuffle":
                        binding.panel.ivRepeatExpand.setImageBitmap(Commen.getBitmapFromVectorDrawable(ListActivity.this,
                                getResources().getDrawable(R.drawable.ic_repeat_one_24px)));
                        musiPlayerHelper.mediaPlayer.setLooping(true);
                        sharedPrefrenceManager.setPlayingState("repeatOne");
                        Toast.makeText(getApplicationContext(), "Repeat one", Toast.LENGTH_SHORT).show();
                        break;
                }


//                if (!bmpCurrent.equals(bmpReplayList)) {
//                    repeatButton.setImageDrawable(drawable);
//                    Toast.makeText(getApplicationContext(), "Play All Song In List", Toast.LENGTH_SHORT).show();
//                    Commen.mediaPlayer.setLooping(false);
//                } else {
//                    Commen.mediaPlayer.setLooping(true);
//                    repeatButton.setImageResource(R.drawable.ic_repeat_one_24px);
//                    Toast.makeText(getApplicationContext(), "Replay Current Song", Toast.LENGTH_SHORT).show();
//                }
            }
        });

    }

    public void longTouchBackward() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isInLongTouch) {
                    if (musiPlayerHelper.mediaPlayer.getCurrentPosition() - longTouchPosition <= 0) {
                        if (musiPlayerHelper.mediaPlayer.isLooping()) {
                            musiPlayerHelper.mediaPlayer.seekTo(0);
                        } else {
//                            if (!isExternalSource) {
                            binding.panel.ivPlayPayseExpand.performClick();
//                            } else {
//                                setupMediaPLayer();
//                            }

                        }
                    } else {
                        musiPlayerHelper.mediaPlayer.seekTo(musiPlayerHelper.mediaPlayer.getCurrentPosition() - longTouchPosition);
                        songPanelViewModel.setProgressDuration(musiPlayerHelper.mediaPlayer.getCurrentPosition());
                        songPanelViewModel.setCurrentDuration(Commen.changeDurationFormat(musiPlayerHelper.mediaPlayer.getCurrentPosition()));
                    }

                    if (longTouchPosition >= 20000) {
                        longTouchPosition = 20000;
                    } else {
                        longTouchPosition = longTouchPosition + 2000;
                    }
                    if (isInLongTouch) {
                        longTouchBackward();
                    }
                }
            }
        }, 500);
    }

    public void longTouchForward() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isInLongTouch) {
                    if (longTouchPosition + musiPlayerHelper.mediaPlayer.getCurrentPosition() >= musiPlayerHelper.mediaPlayer.getDuration()) {
                        if (musiPlayerHelper.mediaPlayer.isLooping()) {
                            musiPlayerHelper.mediaPlayer.seekTo(0);
                        } else {
                            //TODO
//                            if (!isExternalSource) {
//                                    mPresenter.getSong(Commen.song.getId() + 1);
                            binding.panel.ivNextExpand.performClick();
//                            } else {
//                                setupMediaPLayer();
//                            }

                        }
                    } else {
                        musiPlayerHelper.mediaPlayer.seekTo(musiPlayerHelper.mediaPlayer.getCurrentPosition() + longTouchPosition);
                        songPanelViewModel.setProgressDuration(musiPlayerHelper.mediaPlayer.getCurrentPosition());
                        songPanelViewModel.setCurrentDuration(Commen.changeDurationFormat(musiPlayerHelper.mediaPlayer.getCurrentPosition()));
                    }

                    if (longTouchPosition >= 20000) {
                        longTouchPosition = 20000;
                    } else {
                        longTouchPosition = longTouchPosition + 2000;
                    }
                    if (isInLongTouch) {
                        longTouchForward();
                    }
                }
            }
        }, 500);


    }


    private void seekBarProgressUpdater() {

        if (musiPlayerHelper.mediaPlayer != null) {

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
        binding.panel.setSongPanel(songPanelViewModel);
        binding.panel.setListViewModel(songViewModel);
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

        drawableRepeatOne = AppCompatResources.getDrawable(this,
                R.drawable.ic_repeat_one_24px);
        drawableRepeatAll = AppCompatResources.getDrawable(this,
                R.drawable.ic_repeat_24px);
        drawableShuffle = AppCompatResources.getDrawable(this,
                R.drawable.ic_shuffle_24px);

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
                if (songViewModelList.size() != 0)
                    startActivity(new Intent(ListActivity.this, SearchActivity.class));
                else
                    Toast.makeText(getApplicationContext(), "there is no song to search!!", Toast.LENGTH_LONG).show();

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
                seekBarProgressUpdater();
            }

        } else {
            if (musiPlayerHelper.mediaPlayer != null) {
                musiPlayerHelper.mediaPlayer.release();
            }

            if (song.getId() != sharedPrefrenceManager.getSong().getId()) {
                musiPlayerHelper.setupMediaPLayer(ListActivity.this, song, ListActivity.this);
                sharedPrefrenceManager.saveSong(song);
                setupPanel(song);
                if (binding.slidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.EXPANDED)
                    binding.slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                musiPlayerHelper.FadeIn(2);
                seekBarProgressUpdater();
                binding.panel.ivPlayPauseCollpase.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
                binding.panel.ivPlayPayseExpand.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, null));
            }
        }
    }


    @Override
    public void onSongRemoved(int id, int size) {

        songViewModel.deleteSongById(id);

        //this is for when delete first Song multiple(wiich is in curent song)
        if (size > 0 && id == sharedPrefrenceManager.getSong().getId()) {
            sharedPrefrenceManager.saveSong(songViewModelList.get(1).getViewModelSong());
            setupPanel(songViewModelList.get(1).getViewModelSong());
            musiPlayerHelper.mediaPlayer.release();
            musiPlayerHelper.setupMediaPLayer(ListActivity.this, songViewModelList.get(1).getViewModelSong(), ListActivity.this);
            binding.panel.ivPlayPauseCollpase.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));
            binding.panel.ivPlayPayseExpand.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, null));
        }

        if (size == 0) {
            //this is for when delete last song
            binding.ivNoItems.setVisibility(View.VISIBLE);
            binding.slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
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
        sharedPrefrenceManager.setPlayingState("repeatOne");
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

//this is for bug(muti time called onComplete)
        if (!isSongComplete) {

            switch (sharedPrefrenceManager.getPlayingState()) {
                case "repeatOne":
                    break;
                case "repeatList":
                    if (!songViewModel.isCanClick())
                        binding.panel.ivNextExpand.performClick();

                    binding.panel.ivNextExpand.performClick();
                    break;
                case "shuffle":
                    sharedPrefrenceManager.saveSong(shuffleList.get(curentShuffleSong).getViewModelSong());
                    if (curentShuffleSong != shuffleList.size() - 1) {
                        curentShuffleSong++;
                    } else {
                        Collections.shuffle(shuffleList);
                        curentShuffleSong = 0;
                    }

                    musiPlayerHelper.setupMediaPLayer(ListActivity.this, sharedPrefrenceManager.getSong(), ListActivity.this);

                    musiPlayerHelper.FadeIn(2);
                    seekBarProgressUpdater();
                    setupPanel(sharedPrefrenceManager.getSong());
                    break;
            }

            isSongComplete = true;
        } else
            return;


    }

    public SongListComponent getComponent() {
        return component;
    }
}
