package com.example.ps.musicps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ps.musicps.Commen.Commen;
import com.example.ps.musicps.Commen.SongSharedPrefrenceManager;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.Service.SongService;

import java.util.ArrayList;


public class PlayingSongFragment extends Fragment implements Commen.onMediaPlayerStateChanged,
        SongService.onNotificationServiceStateChangedList, SongListActivity.onDataRecived {

    public static onPlayingSongCompletion onPlayingSongCompletion;
    public static onSongListActivityStateChanged onSongListActivityStateChanged;
    public static onGetSong onGetSong;
    private View view;
    private ImageView playingSongImage;
    private TextView playingSongName;
    private TextView playingSongArtist;
    private ImageView playPauseButtonPlayingSong;
    private RelativeLayout playingSongRoot;
    private boolean shouldMediaPlayerStart;
    private SongSharedPrefrenceManager songSharedPrefrenceManager;


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

                if (SongListActivity.isPlaySongActivityEnabled) {
                    Intent intent = new Intent(getContext(), PlaySongActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } else {
                    onGetSong.onPlayingSongClicked(Commen.song.getId());
                }

            }
        });
        playPauseButtonPlayingSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Commen.isServiceRunning(SongService.class, getContext())) {
                    getActivity().startService(new Intent(getContext(), SongService.class));
                }
                SongService.onNotificationServiceStateChangedList = PlayingSongFragment.this;

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

    private void initViews() {

        playPauseButtonPlayingSong = view.findViewById(R.id.iv_playPause_playingSong_fragment);
        playingSongRoot = view.findViewById(R.id.rl_playingSong_fragment);
        playingSongImage = view.findViewById(R.id.iv_songImage_playingSong_fragment);
        playingSongName = view.findViewById(R.id.tv_songName_playingSong_fragment);
        playingSongArtist = view.findViewById(R.id.tv_artistName_fragment);
    }

    private void setupPlayingSong(Song song, MediaPlayer mediaPlayer) {

        Glide.with(this).asBitmap().load(Uri.parse(song.getSongImageUri()))
                .apply(new RequestOptions().placeholder(R.drawable.no_image))
                .into(playingSongImage);
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
                } else {
                    playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));
                }
            }catch (IllegalStateException e){
                Toast.makeText(getContext(),e.getCause()+"1111",Toast.LENGTH_LONG).show();
            }
        } else {
            playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));
        }
        playingSongArtist.setText(song.getArtistName());
        playingSongName.setText(song.getSongName());
    }

    @Override
    public void onStop() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            songSharedPrefrenceManager.saveSong(PlaySongActivity.song);
        }
        super.onStop();
    }

    @Override
    public void onResume() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && playingSongName != null) {
            if (PlaySongActivity.song != null || Commen.song != null) {

                if (PlaySongActivity.song != null) {
                    setupPlayingSong(PlaySongActivity.song, Commen.mediaPlayer);
                } else {
                    SongListActivity.onDataRecived = this;
                    setupPlayingSong(Commen.song, Commen.mediaPlayer);
                }

            } else if (playingSongName.getText().equals("")) {
                Commen.song = songSharedPrefrenceManager.getSong();
                Commen.getInstance().setupMediaPLayer(getContext(), Commen.song, this);

                SongListActivity.isPlaySongActivityEnabled = false;
            }
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
        Commen.mediaPlayer.release();
        super.onDestroy();
    }

    @Override
    public void onMediaPlayerPrepared() {
        if (shouldMediaPlayerStart) {
            Commen.mediaPlayer.start();
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
    public void onPlayButtonClickedList() {
        if (Commen.mediaPlayer.isPlaying()) {
            playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
        } else {
            playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_24px, null));

        }
    }

    @Override
    public void onNextButtonClickedList() {
        if (PlaySongActivity.song == null) {
            onGetSong.getSong(Commen.song.getId() + 1);
            playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
        } else {
            setupPlayingSong(PlaySongActivity.song, Commen.mediaPlayer);
            playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
        }
    }

    @Override
    public void onPrevioudButtonClickedList() {
        if (PlaySongActivity.song == null) {
            onGetSong.getSong(Commen.song.getId() - 1);
            playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
        } else {
            setupPlayingSong(PlaySongActivity.song, Commen.mediaPlayer);
            playPauseButtonPlayingSong.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause_24px, null));
        }
    }

    @Override
    public void onSongRecived(Song song) {
        Commen.song = song;
        Commen.mediaPlayer.release();
        Commen.getInstance().setupMediaPLayer(getContext(), Commen.song, this);
        shouldMediaPlayerStart = true;
    }

    @Override
    public void onListReciveed(ArrayList<Song> songs) {
        if (!songSharedPrefrenceManager.getFirstIn()) {
            songSharedPrefrenceManager.setFirstIn();
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
        void onSongListPlaypauseClicked();

        void onSongListPlaypauseMediaComplete();

    }

    public interface onGetSong {
        void getSong(int Position);

        void onPlayingSongClicked(int position);
    }
}
