package com.example.ps.musicps;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;

import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ps.musicps.Commen.Commen;
import com.example.ps.musicps.Commen.VolumeContentObserver;
import com.example.ps.musicps.MVP.PlaySongMVP;
import com.example.ps.musicps.MVP.PlaySongPresenter;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.Service.SongService;

import java.io.ByteArrayOutputStream;
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
    static boolean isExternalSource;
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
    Drawable drawableCurent;
    Drawable drawableRepeatOne;
    Drawable drawableRepeatAll;
    Drawable drawableShuffle;
    int positions;
    Uri singleSongPathUri;
    File filePath;
    Bitmap songBitmapAlbum;
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
                if ((Commen.mediaPlayer == null) || !Commen.mediaPlayer.isPlaying()) {
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
            if (getRealPathFromURI(this, singleSongPathUri) == null){
                path = getIntent().getData().getPath();
            }else {
                path = getRealPathFromURI(this, singleSongPathUri);
            }
            metaRetriver.setDataSource(path);
            filePath = new File(path);


            byte[] art;
            Uri albumUri = Uri.EMPTY;
            art = metaRetriver.getEmbeddedPicture();
            if (art != null) {

                songBitmapAlbum = BitmapFactory
                        .decodeByteArray(art, 0, art.length);

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
        }catch (Exception e){
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void setupViews() {
        mVolumeContentObserver = new VolumeContentObserver(new Handler());
        this.getApplicationContext().getContentResolver().registerContentObserver(
                android.provider.Settings.System.CONTENT_URI, true,
                mVolumeContentObserver);
        volumeSeekBar.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeSeekBar.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if (Uri.parse(song.getSongImageUri()) == Uri.EMPTY) {
            Glide.with(this).asBitmap().load(songBitmapAlbum)
                    .apply(new RequestOptions().placeholder(R.mipmap.ic_launcher))
                    .into(songImage);
        } else {
            Glide.with(this).asBitmap().load(Uri.parse(song.getSongImageUri()))
                    .apply(new RequestOptions().placeholder(R.mipmap.ic_launcher))
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
                if (Commen.mediaPlayer.isPlaying()) {
                    Commen.mediaPlayer.pause();
                    playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, null));
                } else {
                    Commen.mediaPlayer.start();
                    seekBarProgressUpdater();
                    playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, null));
                }
                onPlaySongActivityStateChanged.onPlaySongPlaypauseClicked();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isExternalSource) {
                    if (Commen.song.getId() == SongListActivity.songList.size() - 1) {
                        mPresenter.getSong(0);
                    } else {
                        mPresenter.getSong(Commen.song.getId() + 1);
                    }
                } else {
                    setupMediaPLayer();
                }
                onPlaySongActivityStateChanged.onPlaySongNextClicked();
            }
        });

//        nextButton.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                Commen.mediaPlayer.seekTo(Commen.mediaPlayer.getCurrentPosition()+20000);
//
//                seekBar.setProgress(Commen.mediaPlayer.getCurrentPosition());
//                timePassed.setText(changeDurationFormat(Commen.mediaPlayer.getCurrentPosition()));
//                return false;
//            }
//        });
        //TODO fix on long touch

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isExternalSource) {
                    if (Commen.song.getId() == 0) {
                        mPresenter.getSong(SongListActivity.songList.size() - 1);
                    } else {
                        mPresenter.getSong(Commen.song.getId() - 1);
                    }
                } else {
                    setupMediaPLayer();
                }
                onPlaySongActivityStateChanged.onPlaySongPreviousClicked();
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
        seekBarProgressUpdater();
        if (!isExternalSource) {
            onPlaySongActivityStateChanged.onPlaySongPlaypauseClicked();
        }
        playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, null));
    }

    private void seekBarProgressUpdater() {

        if (Commen.mediaPlayer.isPlaying()) {
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
    protected void onResume() {
      if (Commen.song == null && !PlaySongActivity.isExternalSource && SongListActivity.songList == null){
          this.finish();
      }else {
          if (isExternalSource){
                  backButton.setEnabled(false);
                  backButton.setImageAlpha(75);
                  repeatButton.setEnabled(false);
                  repeatButton.setImageAlpha(75);
          }
          int pos = getIntent().getIntExtra("position", 0);

          if (!isExternalSource && Commen.song != null) {
              if (Commen.mediaPlayer != null && (Commen.song.getId() == pos)) {
                  if (Commen.mediaPlayer.isPlaying()) {

                      playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, null));
                  } else {
                      playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, null));
                  }
              }
          }
          if (!Commen.isServiceRunning(SongService.class, this)) {
              if (Commen.song== null){
                  Commen.getInstance().setupMediaPLayer(PlaySongActivity.this, song, this);
              }
              startService(new Intent(PlaySongActivity.this, SongService.class));
          }

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

        if (song.getTrackFile() != null) {

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
        song = null;
        isExternalSource = false;
        Commen.mediaPlayer.release();
        Commen.song = null;
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

            moveTaskToBack (true);

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
            onPlaySongActivityStateChanged.onPlaySongNextClicked();
        }

    }

    @Override
    public void onPlayButtonClickedPlaySong() {
        if (Commen.mediaPlayer.isPlaying()) {
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
        void onPlaySongPlaypauseClicked();

        void onPlaySongNextClicked();

        void onPlaySongPreviousClicked();

        void onPlaySongMediaComplete();
    }
}
