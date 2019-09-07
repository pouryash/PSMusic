package com.example.ps.musicps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import android.support.v4.media.session.MediaSessionCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.ps.musicps.Commen.Commen;
import com.example.ps.musicps.Commen.SongSharedPrefrenceManager;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.Service.SongService;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.ArrayList;


public class PlayingSongFragment extends Fragment implements Commen.onMediaPlayerStateChanged,
        SongService.onNotificationServiceStateChangedList, SongListActivity.onDataRecived {

    public static SongSharedPrefrenceManager songSharedPrefrenceManager;
    public static onPlayingSongCompletion onPlayingSongCompletion;
    public static onSongListActivityStateChanged onSongListActivityStateChanged;
    public static onGetSong onGetSong;
    public static boolean isFragmentRootClicked;
    private View view;
    private ImageView playingSongImage;
    private TextView playingSongName;
    private TextView playingSongArtist;
    private ImageView playPauseButtonPlayingSong;
    private RelativeLayout playingSongRoot;
    private boolean shouldMediaPlayerStart;
    FirebaseAnalytics firebaseAnalytics;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songSharedPrefrenceManager = new SongSharedPrefrenceManager(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_playing_song, container, false);

        initViews();
        setupViews();

        return view;
    }

    private void setupViews() {

        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        SongListActivity.onDataRecived = this;
        PlaySongActivity.onPlaySongActivityCompletion = new PlaySongActivity.onPlaySongActivityCompletion() {
            @Override
            public void onCompletion() {
                playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));
                onSongListActivityStateChanged.onSongListPlaypauseMediaComplete();
            }
        };

        playingSongRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "playingSongRoot Fragment");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                if (SongListActivity.isPlaySongActivityEnabled) {
                    Intent intent = new Intent(getContext(), PlaySongActivity.class);
                    if (!SongListActivity.shouldStartNewInstance) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                    }
                    intent.putExtra("position", Commen.song.getId());
                    startActivity(intent);
                } else {
                    isFragmentRootClicked = true;
                    onGetSong.onPlayingSongClicked(Commen.song.getId());
                }

            }
        });
        playPauseButtonPlayingSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "PlayPause Fragment");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                if (!Commen.isServiceRunning(SongService.class, getContext())) {
                    getActivity().startService(new Intent(getContext(), SongService.class));

                }
                SongService.onNotificationServiceStateChangedList = PlayingSongFragment.this;

                if (Commen.IS_PLAYING) {
                    if (Commen.mediaPlayer != null) {
                        try {
                            Commen.getInstance().FadeOut(2);
                            playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));
                            if (onSongListActivityStateChanged != null) {
                                onSongListActivityStateChanged.onSongListPlaypauseClicked(false);
                            }
                        } catch (Exception e) {
                            Commen.getInstance().setupMediaPLayer(getContext(), Commen.song, PlayingSongFragment.this);
                            Commen.getInstance().FadeOut(2);
                            playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));
                            if (onSongListActivityStateChanged != null) {
                                onSongListActivityStateChanged.onSongListPlaypauseClicked(false);
                            }
                        }
                    } else {
                        Commen.getInstance().setupMediaPLayer(getContext(), Commen.song, PlayingSongFragment.this);
                    }

                } else {
                    if (Commen.mediaPlayer != null) {
                        try {
                            Commen.getInstance().FadeIn(2);
                            playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
                            if (onSongListActivityStateChanged != null) {
                                onSongListActivityStateChanged.onSongListPlaypauseClicked(true);
                            }
                        } catch (Exception e) {
                            if (Commen.song == null) {
                                Commen.song = SongListActivity.songList.get(songSharedPrefrenceManager.getSong().getId());
                            }
                            Commen.getInstance().setupMediaPLayer(getContext(), Commen.song, PlayingSongFragment.this);
                            Commen.getInstance().FadeIn(2);
                            playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
                            if (onSongListActivityStateChanged != null) {
                                onSongListActivityStateChanged.onSongListPlaypauseClicked(true);
                            }
                        }
                    } else {
                        Commen.getInstance().setupMediaPLayer(getContext(), Commen.song, PlayingSongFragment.this);
                    }
                }

            }
        });
    }


    private void initViews() {

        playPauseButtonPlayingSong = view.findViewById(R.id.iv_playPause_playingSong_fragment);
        playingSongRoot = view.findViewById(R.id.rl_playingSong_fragment);
        playingSongImage = view.findViewById(R.id.iv_songImage_playingSong_fragment);
        playingSongName = view.findViewById(R.id.tv_songName_playingSong_fragment);
        playingSongArtist = view.findViewById(R.id.tv_artistName_fragment);
        MediaSessionCompat _mediaSession;
        MediaSessionCompat.Token _mediaSessionToken;

    }

    public Palette createPaletteAsync(Bitmap bitmap) {
        final Palette[] u = new Palette[0];
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {
                u[0] = p;
            }
        });

        return u[0];
    }

    public void setupPlayingSong(Song song, MediaPlayer mediaPlayer) {

//
//        Palette p = Palette.from(resource).generate();
//        Palette.Swatch vibrantSwatch = p.getDominantSwatch();
//        playingSongRoot.setBackgroundColor(vibrantSwatch != null ? vibrantSwatch.getRgb() : 0);
        Glide.with(this).asBitmap().load(Uri.parse(song.getSongImageUri()))
                .apply(new RequestOptions().placeholder(R.drawable.ic_no_album))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        playingSongImage.setBackgroundColor(Color.parseColor("#d1d9ff"));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                        return false;
                    }
                })
                .into(playingSongImage);
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
                } else {
                    playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));
                }
            } catch (IllegalStateException e) {
                playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));
            }
        } else {
            playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));
        }
        playingSongArtist.setText(song.getArtistName());
        playingSongName.setText(song.getSongName());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && playingSongName != null) {
            if (Commen.song != null) {

                setupPlayingSong(Commen.song, Commen.mediaPlayer);

            } else if (playingSongName.getText().equals("") || Commen.song == null) {
                File file = new File(songSharedPrefrenceManager.getSong().getTrackFile());
                if (file.exists()) {

                    Commen.song = songSharedPrefrenceManager.getSong();
                    Commen.getInstance().setupMediaPLayer(getContext(), Commen.song, this);
                }

                SongListActivity.isPlaySongActivityEnabled = false;
            }
        }
        if (Commen.IS_PLAYING) {
            playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
        } else {
            playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));

        }
        SongService.onNotificationServiceStateChangedList = this;
        super.onResume();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onDestroy() {
        if (Commen.isServiceRunning(SongService.class, getContext())) {
            getContext().stopService(new Intent(getActivity(), SongService.class));
        }
        if (!PlaySongActivity.isExternalSource) {
            if (Commen.mediaPlayer != null){
                Commen.mediaPlayer.release();
            }
        }
        if (songSharedPrefrenceManager != null) {
            songSharedPrefrenceManager = null;
        }
        super.onDestroy();
    }

    @Override
    public void onMediaPlayerPrepared() {
        if (shouldMediaPlayerStart) {
            Commen.getInstance().FadeIn(2);
        }
        setupPlayingSong(Commen.song, Commen.mediaPlayer);
    }

    @Override
    public void onMediaPlayerCompletion() {
        playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));
        if (onPlayingSongCompletion != null) {
            onPlayingSongCompletion.onCompletion();

        }
        if (onSongListActivityStateChanged != null) {
            onSongListActivityStateChanged.onSongListPlaypauseMediaComplete();
        }
    }

    @Override
    public void onPlayButtonClickedList(boolean isPlaying) {
        if (isPlaying) {
            playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
        } else {
            playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));

        }
    }

    @Override
    public void onNextButtonClickedList() {
        if (SongListActivity.songList.size() - 1 == Commen.song.getId()) {
            if (PlaySongActivity.song == null) {
                onGetSong.getSong(0);
            } else {
                setupPlayingSong(Commen.song, Commen.mediaPlayer);
            }
        } else {
            if (PlaySongActivity.song == null) {
                onGetSong.getSong(Commen.song.getId() + 1);
            } else {
                setupPlayingSong(Commen.song, Commen.mediaPlayer);
            }
        }
        playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));

    }

    @Override
    public void onPrevioudButtonClickedList() {
        if (Commen.song.getId() == 0) {
            if (PlaySongActivity.song == null) {
                onGetSong.getSong(SongListActivity.songList.size() - 1);
            } else {
                setupPlayingSong(PlaySongActivity.song, Commen.mediaPlayer);
            }
        } else {
            if (PlaySongActivity.song == null) {
                onGetSong.getSong(Commen.song.getId() - 1);
            } else {
                setupPlayingSong(PlaySongActivity.song, Commen.mediaPlayer);
            }
        }
        playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
    }

    @Override
    public void onSongRecived(Song song, boolean shouldPlay) {

        if (shouldPlay) {
            shouldMediaPlayerStart = true;
        }
        Commen.song = song;
        if (Commen.mediaPlayer != null) {
            if (onSongListActivityStateChanged != null) {
                onSongListActivityStateChanged.onSongListRemovedSong();
            }
        }
        if (Commen.mediaPlayer != null) {
            Commen.mediaPlayer.release();
        }
        Commen.getInstance().setupMediaPLayer(getContext(), Commen.song, this);

    }

    @Override
    public void onListReciveed(ArrayList<Song> songs) {
        File file = new File(songSharedPrefrenceManager.getSong().getTrackFile());
        if (!file.exists()) {
            Commen.song = songs.get(0);
            Commen.getInstance().setupMediaPLayer(getContext(), Commen.song, this);
        }
        if (songSharedPrefrenceManager.getFirstIn()) {
            songSharedPrefrenceManager.setFirstIn(false);
            Commen.song = songs.get(0);
            Commen.getInstance().setupMediaPLayer(getContext(), Commen.song, this);
            setupPlayingSong(Commen.song, Commen.mediaPlayer);
            SongListActivity.isPlaySongActivityEnabled = false;
        }
    }

    public interface onPlayingSongCompletion {
        void onCompletion();
    }

    public interface onSongListActivityStateChanged {
        void onSongListPlaypauseClicked(boolean isPlaying);

        void onSongListRemovedSong();

        void onSongListPlaypauseMediaComplete();

    }

    public interface onGetSong {
        void getSong(int Position);

        void onPlayingSongClicked(int position);
    }
}
