package com.example.ps.musicps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;

import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.ps.musicps.Commen.AudioFocusControler;
import com.example.ps.musicps.Commen.Commen;
import com.example.ps.musicps.Commen.VolumeContentObserver;
import com.example.ps.musicps.MVP.PlaySongMVP;
import com.example.ps.musicps.MVP.PlaySongPresenter;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.Service.SongService;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class PlaySongActivity extends AppCompatActivity implements PlaySongMVP.RequiredPlaySongViewOps,
        Commen.onMediaPlayerStateChanged, SongService.onNotificationServiceStateChangedPlay {

    public static Timer timer;
    private final Handler handler = new Handler();
    public static Song song;
    public static onPlaySongActivityCompletion onPlaySongActivityCompletion;
    public static onPlaySongActivityStateChanged onPlaySongActivityStateChanged;
    public static ArrayList<Song> shuffleList = new ArrayList<>();
    public static int curentShuffleSong;
    public static AudioManager audioManager;
    public static SeekBar volumeSeekBar;
    public static boolean isExternalSource;
    VolumeContentObserver mVolumeContentObserver;
    ImageView songImage;
    ImageView playPauseButton;
    ImageView nextButton;
    ImageView previousButton;
    TextView songName;
    TextView artistName;
    TextView duration;
    TextView timePassed;
    SeekBar seekBar;
    ImageView backButton;
    ImageView repeatButton;
    ImageView forwardButton;
    ImageView imageVolume;
    Drawable drawableCurent;
    Drawable drawableRepeatOne;
    Drawable drawableRepeatAll;
    Drawable drawableShuffle;
    int positions;
    byte[] art;
    Uri singleSongPathUri = Uri.EMPTY;
    File filePath;
    Bitmap songBitmapAlbum;
    boolean isInLongTouch;
    boolean canClicked = true;
    int longTouchPosition;
    PlaySongMVP.ProvidedPlaySongPresenterOps mPresenter = new PlaySongPresenter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);


        initViews();
        getExtrnalSong();
        mPresenter.setView(this);
        if (!isExternalSource) {
            mPresenter.getSong(positions);
        } else {
            try {
                if ((Commen.mediaPlayer == null) || !Commen.IS_PLAYING) {
                    setupMediaPLayer();
                } else {
                    setupViews();
                }
            } catch (Exception e) {

            }
        }


    }

    private void getExtrnalSong() {
        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            isExternalSource = true;

            singleSongPathUri = getIntent().getData();

            MediaMetadataRetriever metaRetriver;
            metaRetriver = new MediaMetadataRetriever();

            String path;
            if (getRealPathFromURI(this, singleSongPathUri) == null) {
                path = getIntent().getData().getPath();
            } else {
                path = getRealPathFromURI(this, singleSongPathUri);
            }
            metaRetriver.setDataSource(path);
            filePath = new File(path);


            art = metaRetriver.getEmbeddedPicture();

            Uri albumUri = Uri.EMPTY;
            art = metaRetriver.getEmbeddedPicture();
            if (art != null) {

                songBitmapAlbum = BitmapFactory
                        .decodeByteArray(art, 0, metaRetriver.getEmbeddedPicture().length);

                String paths = MediaStore.Images.Media.insertImage
                        (this.getContentResolver(), songBitmapAlbum, "Title", null);
                if (paths != null) {
                    albumUri = Uri.parse(paths);
                }
            }

            song = new Song();
            song.setSongName(filePath.getName());
            song.setArtistName(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            Integer duration = Integer.parseInt(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            song.setDuration(changeDurationFormat(duration));
            song.setAlbumName(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            song.setSongImageUri(albumUri.toString());
            song.setTrackFile(path);
            song.setId(0);


            backButton.setEnabled(false);
            backButton.setImageAlpha(75);
            repeatButton.setEnabled(false);
            repeatButton.setImageAlpha(75);
        }
    }


    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;

        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndex(proj[0]);
            String path = cursor.getString(column_index);
            return path;
        } catch (Exception e) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void longTouchForward() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isInLongTouch) {
                    if (longTouchPosition + Commen.mediaPlayer.getCurrentPosition() >= Commen.mediaPlayer.getDuration()) {
                        if (!isExternalSource) {
                            if (Commen.song.getId() == SongListActivity.songList.size() - 1) {
                                mPresenter.getSong(0);
                            } else {
                                mPresenter.getSong(Commen.song.getId() + 1);
                            }
                        } else {
                            setupMediaPLayer();
                        }
                        if (onPlaySongActivityStateChanged != null) {

                            onPlaySongActivityStateChanged.onPlaySongNextClicked();

                        }
                    } else {
                        Commen.mediaPlayer.seekTo(Commen.mediaPlayer.getCurrentPosition() + longTouchPosition);
                        seekBar.setProgress(Commen.mediaPlayer.getCurrentPosition());
                        timePassed.setText(changeDurationFormat(Commen.mediaPlayer.getCurrentPosition()));
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

    public void longTouchBackward() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isInLongTouch) {
                    if (Commen.mediaPlayer.getCurrentPosition() - longTouchPosition <= 0) {
                        if (!isExternalSource) {
                            if (Commen.song.getId() == 0) {
                                mPresenter.getSong(SongListActivity.songList.size() - 1);
                            } else {
                                mPresenter.getSong(Commen.song.getId() - 1);
                            }
                        } else {
                            setupMediaPLayer();
                        }
                        if (onPlaySongActivityStateChanged != null) {

                            onPlaySongActivityStateChanged.onPlaySongPreviousClicked();

                        }
                    } else {
                        Commen.mediaPlayer.seekTo(Commen.mediaPlayer.getCurrentPosition() - longTouchPosition);
                        seekBar.setProgress(Commen.mediaPlayer.getCurrentPosition());
                        timePassed.setText(changeDurationFormat(Commen.mediaPlayer.getCurrentPosition()));
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

    private void setupViews() {

        AudioFocusControler.getInstance().onAudioFocusChangePlay = new AudioFocusControler.onAudioFocusChangePlay() {
            @Override
            public void onPlaySongFocusChange() {
                playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, null));
            }
        };
        AudioFocusControler.getInstance().initAudio();
        mVolumeContentObserver = new VolumeContentObserver(new Handler());
        this.getApplicationContext().getContentResolver().registerContentObserver(
                android.provider.Settings.System.CONTENT_URI, true,
                mVolumeContentObserver);
        volumeSeekBar.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeSeekBar.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if (Uri.parse(song.getSongImageUri()).toString() == "") {
            Glide.with(this).asBitmap().load(art)
                    .apply(new RequestOptions().placeholder(R.drawable.ic_no_album_128))
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            songImage.setBackgroundColor(Color.parseColor("#d1d9ff"));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(songImage);
        } else {
            Glide.with(this).asBitmap().load(Uri.parse(song.getSongImageUri()))
                    .apply(new RequestOptions().placeholder(R.drawable.ic_no_album_128))
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            songImage.setBackgroundColor(Color.parseColor("#d1d9ff"));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(songImage);
        }

        songName.setText(song.getSongName());
        artistName.setText(song.getArtistName());
        duration.setText(song.getDuration());
        seekBar.setMax(Commen.mediaPlayer.getDuration());
        timePassed.setText(changeDurationFormat(0));
        PlayingSongFragment.onPlayingSongCompletion = new PlayingSongFragment.onPlayingSongCompletion() {
            @Override
            public void onCompletion() {
                if (drawableCurent.getConstantState() == drawableRepeatAll.getConstantState()) {
                    if ((SongListActivity.songList.size() - 1) == Commen.song.getId()) {
                        playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, null));
                        onPlaySongActivityStateChanged.onPlaySongMediaComplete();
                    } else {
                        nextButton.performClick();
                    }
                } else if (drawableCurent.getConstantState() == drawableShuffle.getConstantState()) {
                    PlaySongActivity.song = shuffleList.get(curentShuffleSong);
                    if (curentShuffleSong != shuffleList.size() - 1) {
                        curentShuffleSong++;
                    } else {
                        Collections.shuffle(shuffleList);
                        curentShuffleSong = 0;
                    }
                    setupMediaPLayer();
                    if (drawableCurent.getConstantState() == drawableRepeatAll.getConstantState()) {
                        Commen.mediaPlayer.setLooping(false);
                    } else if (drawableCurent.getConstantState() == drawableShuffle.getConstantState()) {
                        Commen.mediaPlayer.setLooping(false);
                    }
                    onPlaySongActivityStateChanged.onPlaySongNextClicked();
                }
//                playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, null));
//                onPlaySongActivityStateChanged.onPlaySongMediaComplete();
            }
        };
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaySongActivity.this, SongListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if (fromUser) {
                    Commen.mediaPlayer.seekTo(i);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                timePassed.setText(changeDurationFormat(Commen.mediaPlayer.getCurrentPosition()));
            }
        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Commen.IS_PLAYING) {

                    Commen.getInstance().FadeOut(2);

                    if (onPlaySongActivityStateChanged != null) {

                        onPlaySongActivityStateChanged.onPlaySongPlaypauseClicked(false);

                    }

                    playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, null));
                } else {
//                    Commen.mediaPlayer.setVolume(1,1);
                    Commen.getInstance().FadeIn(2);
                    AudioFocusControler.getInstance().initAudio();
                    seekBarProgressUpdater();
                    playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, null));
                    if (onPlaySongActivityStateChanged != null) {

                        onPlaySongActivityStateChanged.onPlaySongPlaypauseClicked(true);

                    }
                }

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (canClicked) {
                    if (!isExternalSource) {
                        if (Commen.song.getId() == SongListActivity.songList.size() - 1) {
                            mPresenter.getSong(0);
                        } else {
                            mPresenter.getSong(Commen.song.getId() + 1);
                        }
                    } else {
                        setupMediaPLayer();
                    }
                    if (onPlaySongActivityStateChanged != null) {

                        onPlaySongActivityStateChanged.onPlaySongNextClicked();

                    }
                }
                canClicked = true;
            }
        });
        nextButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                isInLongTouch = true;
                longTouchPosition = 2000;
                longTouchForward();
                return false;
            }
        });
        nextButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (isInLongTouch) {
                        canClicked = false;
                        isInLongTouch = false;
                    }
                }
                return false;
            }
        });

        previousButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                isInLongTouch = true;
                longTouchPosition = 2000;
                longTouchBackward();
                return false;
            }
        });
        previousButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (isInLongTouch) {
                        canClicked = false;
                        isInLongTouch = false;
                    }
                }
                return false;
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (canClicked) {
                    if (!isExternalSource) {
                        if (Commen.song.getId() == 0) {
                            mPresenter.getSong(SongListActivity.songList.size() - 1);
                        } else {
                            mPresenter.getSong(Commen.song.getId() - 1);
                        }
                    } else {
                        setupMediaPLayer();
                    }
                    if (onPlaySongActivityStateChanged != null) {

                        onPlaySongActivityStateChanged.onPlaySongPreviousClicked();

                    }
                }
                canClicked = true;
            }
        });

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commen.mediaPlayer.seekTo(Commen.mediaPlayer.getCurrentPosition() + 30000);

                seekBar.setProgress(Commen.mediaPlayer.getCurrentPosition());
                timePassed.setText(changeDurationFormat(Commen.mediaPlayer.getCurrentPosition()));

            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (drawableCurent.getConstantState() == drawableRepeatOne.getConstantState()) {
                    repeatButton.setImageBitmap(Commen.getBitmapFromVectorDrawable(PlaySongActivity.this,
                            getResources().getDrawable(R.drawable.ic_repeat_24px)));
                    Commen.mediaPlayer.setLooping(false);
                    drawableCurent = drawableRepeatAll;
                } else if (drawableCurent.getConstantState() == drawableRepeatAll.getConstantState()) {
                    repeatButton.setImageBitmap(Commen.getBitmapFromVectorDrawable(PlaySongActivity.this,
                            getResources().getDrawable(R.drawable.ic_shuffle_24px)));
                    if (shuffleList.size() == 0) {
                        shuffleList = SongListActivity.songList;
                    }
                    Collections.shuffle(shuffleList);
                    Commen.mediaPlayer.setLooping(false);
                    drawableCurent = drawableShuffle;
                } else if (drawableCurent.getConstantState() == drawableShuffle.getConstantState()) {
                    repeatButton.setImageBitmap(Commen.getBitmapFromVectorDrawable(PlaySongActivity.this,
                            getResources().getDrawable(R.drawable.ic_repeat_one_24px)));
                    Commen.mediaPlayer.setLooping(true);
                    drawableCurent = drawableRepeatOne;
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

            Commen.mediaPlayer.start();
            Commen.IS_PLAYING = true;
            seekBarProgressUpdater();
            if (!isExternalSource && onPlaySongActivityStateChanged != null) {

                onPlaySongActivityStateChanged.onPlaySongPlaypauseClicked(true);
            }
            playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, null));

    }

    private void seekBarProgressUpdater() {

        if (Commen.IS_PLAYING) {
//            Runnable notification = new Runnable() {
//                public void run() {
//                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
//                    seekBarProgressUpdater();
//                }
//            };
//            handler.postDelayed(notification, 1000);
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                seekBar.setProgress(Commen.mediaPlayer.getCurrentPosition());
                                timePassed.setText(changeDurationFormat(Commen.mediaPlayer.getCurrentPosition()));
                            } catch (Exception e) {
                                Log.e("seekbar timer", "error: " + e);
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        seekBar.setProgress(Commen.mediaPlayer.getCurrentPosition());
//                                        timePassed.setText(changeDurationFormat(Commen.mediaPlayer.getCurrentPosition()));
//                                    }
//                                }, 2000);
                            }
                        }
                    });
                }
            }, 0, 1000);
        }


    }

    private String changeDurationFormat(long duration) {
        int seconds = (int) (duration / 1000);
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format(Locale.ENGLISH, "%02d", minutes) + ":" + String.format(Locale.ENGLISH, "%02d", seconds);
    }

    private void initViews() {

        volumeSeekBar = findViewById(R.id.sb_Sound_PlaySong);
        imageVolume = findViewById(R.id.iv_SoundSeekbar_PlaySong);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeSeekBar = findViewById(R.id.sb_Sound_PlaySong);
        drawableRepeatOne = AppCompatResources.getDrawable(PlaySongActivity.this,
                R.drawable.ic_repeat_one_24px);
        drawableRepeatAll = AppCompatResources.getDrawable(PlaySongActivity.this,
                R.drawable.ic_repeat_24px);
        drawableShuffle = AppCompatResources.getDrawable(PlaySongActivity.this,
                R.drawable.ic_shuffle_24px);
        drawableCurent = drawableRepeatOne;
        repeatButton = findViewById(R.id.iv_Repeat_PlaySong);
        forwardButton = findViewById(R.id.iv_forward_PlaySong);
        if (getIntent().getBooleanExtra("notification", false)) {
            positions = Commen.song.getId();
        } else {
            positions = getIntent().getIntExtra("position", 0);
        }
        songImage = findViewById(R.id.iv_songImage_playSong);
        playPauseButton = findViewById(R.id.iv_playPayse_playSong);
        nextButton = findViewById(R.id.iv_next_playSong);
        previousButton = findViewById(R.id.iv_previous_playSong);
        songName = findViewById(R.id.tv_songName_songListAdp);
        artistName = findViewById(R.id.tv_songArtist_playSong);
        duration = findViewById(R.id.tv_duration_playSong);
        timePassed = findViewById(R.id.tv_timePassed_playSong);
        seekBar = findViewById(R.id.seekbar_playSong);
        backButton = findViewById(R.id.iv_back_playSong);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {

            if (volumeSeekBar.getVisibility() == View.VISIBLE) {
                volumeSeekBar.setVisibility(View.GONE);
                imageVolume.setVisibility(View.GONE);
            } else {
                volumeSeekBar.setVisibility(View.VISIBLE);
                imageVolume.setVisibility(View.VISIBLE);
            }

        }

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);

    }

    @Override
    protected void onResume() {

        String newIntentUri = "";
        if (getIntent().getData() != null) {
            newIntentUri = getIntent().getData().toString();
        }
        if (!newIntentUri.equals("")) {
            getExtrnalSong();
            setupMediaPLayer();
            if (onPlaySongActivityStateChanged != null) {
                onPlaySongActivityStateChanged.onPlaySongNextClicked();
            }
        }

        if (Commen.song == null && !PlaySongActivity.isExternalSource) {
            this.finish();
        } else {
            // disable some feature on singleSong
            if (isExternalSource) {
                backButton.setEnabled(false);
                backButton.setImageAlpha(75);
                repeatButton.setEnabled(false);
                repeatButton.setImageAlpha(75);
            }
            int pos = getIntent().getIntExtra("position", 0);

            if (!isExternalSource && Commen.song != null) {
                if (Commen.mediaPlayer != null && (Commen.song.getId() == pos)) {
                    try {
                        if (!Commen.song.getSongName().equals(songName.getText().toString())){
                            setupViews();
                        }
                        if (Commen.IS_PLAYING) {
                            playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, null));
                        } else {
                            playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, null));
                        }
                    } catch (Exception e) {
                        //this try catch is for when delete curent playing file and then play first song
                        if (Commen.mediaPlayer == null){
                            setupMediaPLayer();
                        }
                    }
                }
            }
            if (!Commen.isServiceRunning(SongService.class, this)) {
                if (Commen.song == null) {
                    Commen.getInstance().setupMediaPLayer(PlaySongActivity.this, song, this);
                }
                startService(new Intent(PlaySongActivity.this, SongService.class));
            }
//when new singleExternal select change data

            SongService.onNotificationServiceStateChangedPlay = this;
        }
        super.onResume();
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
    public void onSongRecived(Song song) {
        PlaySongActivity.song = song;
        if (song.getId() == Commen.song.getId()) {
            setupViews();
        } else {
            setupMediaPLayer();
            if (drawableCurent.getConstantState() == drawableRepeatAll.getConstantState()) {
                Commen.mediaPlayer.setLooping(false);
            } else if (drawableCurent.getConstantState() == drawableShuffle.getConstantState()) {
                Commen.mediaPlayer.setLooping(false);
            }
            if (onPlaySongActivityStateChanged != null) {
                onPlaySongActivityStateChanged.onPlaySongNextClicked();
            }
        }
    }

    private void setupMediaPLayer() {

        if (Commen.song != null) {

            if (song == null){
                song = Commen.song;
            }

            if (Commen.mediaPlayer != null) {
                Commen.mediaPlayer.release();
            }
//            timer.purge();
//            timer.cancel();
            Commen.getInstance().setupMediaPLayer(PlaySongActivity.this, song, this);

        } else {
            Toast.makeText(PlaySongActivity.this, "Cant Find Music!!!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(PlaySongActivity.this, SongService.class));
        Commen.mediaPlayer.release();
        song = null;
        timer.purge();
        timer.cancel();
        PlaySongActivity.this.getContentResolver().unregisterContentObserver(mVolumeContentObserver);
        this.finish();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        if (isExternalSource) {

            moveTaskToBack(true);

        } else {
            intent = new Intent(PlaySongActivity.this, SongListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }

    }

    @Override
    public void onMediaPlayerPrepared() {
        setupViews();
    }

    //TODO use one Commen.getinstance.setupMediaplayer
    @Override
    public void onMediaPlayerCompletion() {

        if (drawableCurent.getConstantState() == drawableRepeatAll.getConstantState()) {
            if ((SongListActivity.songList.size() - 1) == Commen.song.getId()) {
                playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, null));
                onPlaySongActivityStateChanged.onPlaySongMediaComplete();
            } else {
                nextButton.performClick();
            }
        } else if (drawableCurent.getConstantState() == drawableShuffle.getConstantState()) {
            PlaySongActivity.song = shuffleList.get(curentShuffleSong);
            if (curentShuffleSong != shuffleList.size() - 1) {
                curentShuffleSong++;
            } else {
                Collections.shuffle(shuffleList);
                curentShuffleSong = 0;
            }
            setupMediaPLayer();
            if (drawableCurent.getConstantState() == drawableRepeatAll.getConstantState()) {
                Commen.mediaPlayer.setLooping(false);
            } else if (drawableCurent.getConstantState() == drawableShuffle.getConstantState()) {
                Commen.mediaPlayer.setLooping(false);
            }
            if (onPlaySongActivityStateChanged != null) {

                onPlaySongActivityStateChanged.onPlaySongNextClicked();

            }
        }

    }

    @Override
    public void onPlayButtonClickedPlaySong(boolean isPlaying) {
        if (isPlaying) {
            seekBarProgressUpdater();
            playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, null));
        } else {
            playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, null));

        }
    }

    @Override
    public void onNextButtonClickedPlaySong() {
        if (!isExternalSource) {
            if (Commen.song.getId() == SongListActivity.songList.size() - 1) {
                mPresenter.getSong(0);
            } else {
                mPresenter.getSong(Commen.song.getId() + 1);
            }
        } else {
            setupMediaPLayer();
        }
    }

    @Override
    public void onPrevioudButtonClickedPlaySong() {
        if (!isExternalSource) {
            if (Commen.song.getId() == 0) {
                mPresenter.getSong(SongListActivity.songList.size() - 1);
            } else {
                mPresenter.getSong(Commen.song.getId() - 1);
            }
        } else {
            setupMediaPLayer();
        }
    }

    public interface onPlaySongActivityCompletion {
        void onCompletion();
    }

    public interface onPlaySongActivityStateChanged {
        void onPlaySongPlaypauseClicked(boolean isPlaying);

        void onPlaySongNextClicked();

        void onPlaySongPreviousClicked();

        void onPlaySongMediaComplete();
    }
}
