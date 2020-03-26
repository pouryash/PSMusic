package com.example.ps.musicps.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.ps.musicps.Adapter.OnSongAdapter;
import com.example.ps.musicps.Commen.Commen;
import com.example.ps.musicps.Commen.MyApplication;
import com.example.ps.musicps.Commen.SongSharedPrefrenceManager;
import com.example.ps.musicps.Commen.VolumeContentObserver;
import com.example.ps.musicps.Di.component.DaggerMusicServiceComponent;
import com.example.ps.musicps.Di.component.DaggerSongSearchComponent;
import com.example.ps.musicps.Di.component.SongSearchComponent;
import com.example.ps.musicps.Di.module.SearchActivityModule;
import com.example.ps.musicps.Helper.MusiPlayerHelper;
import com.example.ps.musicps.Helper.ServiceConnectionBinder;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.Model.SongInfo;
import com.example.ps.musicps.R;
import com.example.ps.musicps.Service.MusicService;
import com.example.ps.musicps.databinding.ActivitySearchBinding;
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

public class SearchActivity extends AppCompatActivity implements OnSongAdapter, MusiPlayerHelper.onMediaPlayerStateChanged,
MusicService.Callbacks{

    ActivitySearchBinding binding;
    FirebaseAnalytics firebaseAnalytics;
    InputMethodManager imgr;
    String searchTerm;
    SongInfoDialogBinding songInfoBinding;
    Dialog infoDialog;
    @Inject
    SongInfoViewModel songInfoViewModel;
    @Inject
    SongViewModel songViewModel;
    @Inject
    SongPanelViewModel songPanelViewModel;
    @Inject
    VolumeContentObserver volumeContentObserver;
    List<SongViewModel> shuffleList = new ArrayList<>();
    List<SongViewModel> songViewModelList = new ArrayList<>();
    boolean isSetupedPanel = false;
    boolean isSeekbarTouched = false;
    boolean isInLongTouch;
    boolean isSongComplete = false;
    int longTouchPosition;
    int curentShuffleSong;
    boolean iscompleteFromChangeSong;
    Drawable drawableRepeatOne;
    Drawable drawableRepeatAll;
    Drawable drawableShuffle;
    SongSearchComponent component;
    MusiPlayerHelper musiPlayerHelper;
    SongSharedPrefrenceManager sharedPrefrenceManager;
    private Intent serviceIntent;
    private ServiceConnectionBinder serviceConnectionBinder;
    private Observer<Song> songObserver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        songInfoBinding = SongInfoDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setupViews();
    }

    @Override
    protected void onStart() {
        super.onStart();

        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're in day time
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.colorWhite));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }
                }
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're at night!
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.colorBlack));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getWindow().getDecorView().setSystemUiVisibility(0);
                    }
                }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (serviceConnectionBinder.getMusicService() != null)
            serviceConnectionBinder.getMusicService().setUpCallback(SearchActivity.this);

        if (serviceConnectionBinder != null && !serviceConnectionBinder.isServiceConnect && Commen.isServiceRunning(MusicService.class, SearchActivity.this)) {

            serviceIntent = new Intent(SearchActivity.this, MusicService.class);
            startService(serviceIntent);
            bindService(serviceIntent, serviceConnectionBinder.getServiceConnection(), Context.BIND_AUTO_CREATE);
        }

        if (musiPlayerHelper.mediaPlayer != null) {
            onMediaPlayerPrepared();
            if (musiPlayerHelper.mediaPlayer.isPlaying()) {
                binding.panel.ivPlayPauseCollpase.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
                binding.panel.ivPlayPayseExpand.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, null));
                seekBarProgressUpdater();
            }
            setupPanel(sharedPrefrenceManager.getSong());
        }
        if (binding.slidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
            binding.panel.rlSlidePanelTop.setAlpha(0);

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
            if (musiPlayerHelper.getTimer() == null)
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

    private void setupViews() {
        songViewModel.getSongs();
        imgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        binding.etSearch.requestFocus();
        binding.etSearch.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_search_search_activity, null)
                , null, null, null);
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                songViewModel.getSongSearchAdapter().getFilter().filter(charSequence);
                searchTerm = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        serviceConnectionBinder.setOnServiceConnectionChanged(new ServiceConnectionBinder.onServiceConnectionChanged() {
            @Override
            public void onServiceConnected() {
                serviceConnectionBinder.getMusicService().firstTimeSetup();
            }

            @Override
            public void onServiceDisconnected() {

            }
        });

        songViewModel.getMutableSongViewModelList().observe(this, songViewModels -> {

            SearchActivity.this.songViewModelList.clear();
            SearchActivity.this.songViewModelList.addAll(songViewModels);

        });

        binding.ivBackSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Back Button Search");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                onBackPressed();
                try {
                    imgr.hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus().getWindowToken(), 0);
                }catch (Exception e){
                    e.printStackTrace();
                }
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
                        binding.panel.ivRepeatExpand.setImageBitmap(Commen.getBitmapFromVectorDrawable(SearchActivity.this,
                                getResources().getDrawable(R.drawable.ic_repeat_24px)));
                        musiPlayerHelper.mediaPlayer.setLooping(false);
                        sharedPrefrenceManager.setPlayingState("repeatList");
                        Toast.makeText(getApplicationContext(), "Repeat list", Toast.LENGTH_SHORT).show();
                        break;
                    case "repeatList":
                        binding.panel.ivRepeatExpand.setImageBitmap(Commen.getBitmapFromVectorDrawable(SearchActivity.this,
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
                        binding.panel.ivRepeatExpand.setImageBitmap(Commen.getBitmapFromVectorDrawable(SearchActivity.this,
                                getResources().getDrawable(R.drawable.ic_repeat_one_24px)));
                        musiPlayerHelper.mediaPlayer.setLooping(true);
                        sharedPrefrenceManager.setPlayingState("repeatOne");
                        Toast.makeText(getApplicationContext(), "Repeat one", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });

        switch (sharedPrefrenceManager.getPlayingState()) {
            case "repeatOne":
                binding.panel.ivRepeatExpand.setImageDrawable(drawableRepeatOne);
                break;
            case "repeatList":
                binding.panel.ivRepeatExpand.setImageDrawable(drawableRepeatAll);
                break;
            case "shuffle":
                binding.panel.ivRepeatExpand.setImageDrawable(drawableShuffle);
                break;
        }

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
                if (serviceConnectionBinder.getMusicService() != null)
                    serviceConnectionBinder.getMusicService().playBackStateChanged();
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

        binding.panel.ivNextExpand.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                isInLongTouch = true;
                longTouchPosition = 2000;
                longTouchForward();
                iscompleteFromChangeSong = false;
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
                    iscompleteFromChangeSong = true;
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
                iscompleteFromChangeSong = false;
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
                    iscompleteFromChangeSong = true;
                }
                return false;
            }
        });

        songObserver = new Observer<Song>() {
            @Override
            public void onChanged(Song song) {
                onSongClicked(song);
            }
        };

        songViewModel.getSongMutableLiveData().observeForever( songObserver);

        binding.panel.ivPlayPauseCollpase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceConnectionCheck();
                if (musiPlayerHelper.mediaPlayer.isPlaying()) {
                    musiPlayerHelper.FadeOut(2);
                    binding.panel.ivPlayPauseCollpase.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));
                    binding.panel.ivPlayPayseExpand.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, null));
                } else {
                    serviceConnectionCheck();
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
                serviceConnectionCheck();
                if (musiPlayerHelper.mediaPlayer.isPlaying()) {
                    musiPlayerHelper.FadeOut(2);
                    binding.panel.ivPlayPauseCollpase.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));
                    binding.panel.ivPlayPayseExpand.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, null));
                } else {
                    serviceConnectionCheck();
                    musiPlayerHelper.FadeIn(2);
                    musiPlayerHelper.mediaPlayer.start();
                    binding.panel.ivPlayPauseCollpase.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
                    binding.panel.ivPlayPayseExpand.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, null));
                    seekBarProgressUpdater();

                }
            }
        });

        binding.slidingPanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                binding.panel.rlSlidePanelTop.setAlpha(1 - slideOffset);
                binding.panel.ivArrowCollpase.setAlpha(slideOffset);
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

        binding.panel.ivArrowCollpase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            }
        });

        binding.panel.ivPreviousExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a =50;
            }
        });

    }

    private void serviceConnectionCheck() {
        if (!serviceConnectionBinder.isServiceConnect) {
            serviceIntent = new Intent(SearchActivity.this, MusicService.class);
            startService(serviceIntent);
            bindService(serviceIntent, serviceConnectionBinder.getServiceConnection(), Context.BIND_AUTO_CREATE);
        } else {
            serviceConnectionBinder.getMusicService().onPlayPauseClicked();
        }
    }

    private void init() {

        component = DaggerSongSearchComponent.builder().searchActivityModule(new SearchActivityModule(this)).build();
        component.inject(this);
        serviceConnectionBinder = DaggerMusicServiceComponent.builder()
                .Activity(this)
                .LifecycleOwner(this)
                .build()
                .getServiceBinder();

        volumeContentObserver.setSongPanelViewModel(songPanelViewModel);
        binding.panel.setSongPanel(songPanelViewModel);
        binding.panel.setListViewModel(songViewModel);
        sharedPrefrenceManager = ((MyApplication) getApplication()).getComponent().getSharedPrefrence();
        musiPlayerHelper = ((MyApplication) getApplication()).getComponent().getMusicPlayerHelper();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        songInfoBinding.setSongInfo(songInfoViewModel);
        binding.setSongViewModel(songViewModel);

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
        drawableRepeatOne = AppCompatResources.getDrawable(this,
                R.drawable.ic_repeat_one_24px);
        drawableRepeatAll = AppCompatResources.getDrawable(this,
                R.drawable.ic_repeat_24px);
        drawableShuffle = AppCompatResources.getDrawable(this,
                R.drawable.ic_shuffle_24px);
    }

    @Override
    public void onBackPressed() {
        if (binding.slidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            binding.slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serviceConnectionBinder.isServiceConnect && musiPlayerHelper.mediaPlayer != null && !musiPlayerHelper.mediaPlayer.isPlaying()) {
            unbindService(serviceConnectionBinder.getServiceConnection());
            stopService(new Intent(getApplicationContext(), MusicService.class));
        }

        if (volumeContentObserver != null) {
            this.getContentResolver().unregisterContentObserver(volumeContentObserver);
        }

        songViewModel.getSongMutableLiveData().removeObserver(songObserver);

        stopService(serviceIntent);

        songViewModel.getSongMutableLiveData().removeObserver(songObserver);
    }

    @Override
    public void onSongClicked(Song song) {

        if (!serviceConnectionBinder.isServiceConnect) {
            serviceIntent = new Intent(SearchActivity.this, MusicService.class);
            startService(serviceIntent);
            bindService(serviceIntent, serviceConnectionBinder.getServiceConnection(), Context.BIND_AUTO_CREATE);
        } else if (!musiPlayerHelper.mediaPlayer.isPlaying()) {
            serviceConnectionBinder.getMusicService().onPlayPauseClicked();
        }

        //update notification content (reset observer)
        if (!sharedPrefrenceManager.getSharedPrefsSongLiveData(SongSharedPrefrenceManager.KEY_SONG_MODEL).
                getSongLiveData(SongSharedPrefrenceManager.KEY_SONG_MODEL, new Song()).hasObservers()
                && serviceConnectionBinder.getMusicService() != null)
            serviceConnectionBinder.getMusicService().setUpObserver();

        iscompleteFromChangeSong = true;

        try {
            imgr.hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                musiPlayerHelper.setupMediaPLayer(SearchActivity.this, song, SearchActivity.this);
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

        if (size > 0 && id == sharedPrefrenceManager.getSong().getId()) {
            sharedPrefrenceManager.saveSong(songViewModelList.get(1).getViewModelSong());
            setupPanel(songViewModelList.get(1).getViewModelSong());
            musiPlayerHelper.mediaPlayer.release();
            musiPlayerHelper.setupMediaPLayer(SearchActivity.this, songViewModelList.get(1).getViewModelSong(), SearchActivity.this);
            binding.panel.ivPlayPauseCollpase.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));
            binding.panel.ivPlayPayseExpand.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, null));
        }

        if (size == 0) {
            //this is for when delete last song
            binding.slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }
    }

    @Override
    public void onMenuInfoClicked(SongInfo songInfo) {
        songInfoViewModel.setSongInfo(songInfo);

        infoDialog.show();
    }

    @Override
    public void onMediaPlayerPrepared() {
        if (serviceConnectionBinder.getMusicService() != null)
            serviceConnectionBinder.getMusicService().playBackStateChanged();

        songPanelViewModel.setCurrentDuration(Commen.changeDurationFormat(musiPlayerHelper.mediaPlayer.getCurrentPosition()));
        songPanelViewModel.setMaxDuration(musiPlayerHelper.mediaPlayer.getDuration());
        songPanelViewModel.setProgressDuration(musiPlayerHelper.mediaPlayer.getCurrentPosition());
        if (iscompleteFromChangeSong)
            iscompleteFromChangeSong = false;
    }

    @Override
    public void onMediaPlayerCompletion() {
//this is for bug(muti time called onComplete)
        if (!isSongComplete && !iscompleteFromChangeSong) {

            switch (sharedPrefrenceManager.getPlayingState()) {
                case "repeatOne":
                    musiPlayerHelper.mediaPlayer.setLooping(true);
                    musiPlayerHelper.FadeIn(2);
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

                    musiPlayerHelper.setupMediaPLayer(SearchActivity.this, sharedPrefrenceManager.getSong(), SearchActivity.this);

                    musiPlayerHelper.FadeIn(2);
                    seekBarProgressUpdater();

                    setupPanel(sharedPrefrenceManager.getSong());
                    break;
            }

            isSongComplete = true;
        } else
            return;
    }

    @Override
    public void onPlayButtonClicked(boolean isPlaying) {
        binding.panel.ivPlayPauseCollpase.performClick();
    }

    @Override
    public void onNextButtonClicked() {
        binding.panel.ivNextExpand.performClick();
    }

    @Override
    public void onPreviousButtonClicked() {
        binding.panel.ivPreviousExpand.performClick();
    }
}
