package com.example.ps.musicps.viewmodels;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.ps.musicps.Adapter.SongAdapter;
import com.example.ps.musicps.BR;
import com.example.ps.musicps.Commen.MyApplication;
import com.example.ps.musicps.Commen.SongSharedPrefrenceManager;
import com.example.ps.musicps.Di.component.DaggerSongViewModelComponent;
import com.example.ps.musicps.Di.component.SongViewModelComponent;
import com.example.ps.musicps.Di.module.SongAdapterModule;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.R;
import com.example.ps.musicps.Repository.SongRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SongViewModel extends BaseObservable {

    private MutableLiveData<List<SongViewModel>> mutableSongViewModelList = new MutableLiveData<>();
    private MutableLiveData<Song> songMutableLiveData = new MutableLiveData<>();
    private SongRepository songRepository;
    private int id;
    private String songName;
    private String artistName;
    private String trackFile;
    private String albumName;
    private String duration;
    private String songImageUri;
    private int contentID;
    private Context context;
    private SongAdapter songAdapter;
    private List<SongViewModel> userViewModelList1 = new ArrayList<>();
    private SongViewModelComponent component;
    private SongSharedPrefrenceManager sharedPrefrenceManager;
    private boolean canClick = true;

    @Inject
    public SongViewModel(Context context) {
        setContext(context);
        component = DaggerSongViewModelComponent.builder()
                .songAdapterModule(new SongAdapterModule((SongAdapter.onSongAdapter) context, userViewModelList1, context))
                .build();
        songRepository = component.getSongRepository();
        songAdapter = component.getSongAdapter();
        sharedPrefrenceManager = ((MyApplication) context.getApplicationContext()).getComponent().getSharedPrefrence();
    }

    public SongViewModel(Song song) {
        this.id = song.getId();
        this.songName = song.getSongName();
        this.artistName = song.getArtistName();
        this.trackFile = song.getTrackFile();
        this.albumName = song.getAlbumName();
        this.duration = song.getDuration();
        this.songImageUri = song.getSongImageUri();
        contentID = song.getContentID();
    }

    @BindingAdapter({"bind:recyclerBinder", "bind:context", "bind:songAdapter", "bind:viewModelList"})
    public static void getRecyclerBinder(RecyclerView recyclerView, MutableLiveData<List<SongViewModel>> mutableSongViewModelList,
                                         Context context, SongAdapter songAdapter, List<SongViewModel> songViewModels) {


        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(songAdapter);

        SongAdapter finalSongAdapter = songAdapter;
        mutableSongViewModelList.observe((LifecycleOwner) context, songViewModellist -> {
            songViewModels.clear();
            songViewModels.addAll(songViewModellist);
            finalSongAdapter.notifyDataSetChanged();
        });

    }

    @BindingAdapter({"bind:imgaeUri", "bind:context"})
    public static void loadImage(ImageView iv, String uri, Context context) {
        if (uri != null) {
            Glide.with(iv.getContext()).asBitmap().load(Uri.parse(uri))
                    .apply(new RequestOptions().placeholder(R.drawable.ic_no_album))
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            iv.setBackgroundColor(Color.parseColor("#d1d9ff"));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(iv);
        }
    }

    public void getSongs() {
        songRepository.getSongs().observe((LifecycleOwner) context, songsViewModel -> {
            //TODO startservice
            if (songsViewModel.size() > 0) {
                List<SongViewModel> songViewModels = new ArrayList<>();
                for (int i = 0; i < songsViewModel.size(); i++) {
                    songViewModels.add(new SongViewModel(songsViewModel.get(i)));
                }
                mutableSongViewModelList.postValue(songViewModels);
            } else
                updateSongs();
        });
    }

    public void updateSongs() {
        songRepository.updateSongs().observe((LifecycleOwner) context, songViewModels -> {
            mutableSongViewModelList.postValue(songViewModels);
        });

    }

    public void getNextSongById() {

        if (canClick){
            int songId = sharedPrefrenceManager.getSong().getId() + 1;

            if (songId - 1 == mutableSongViewModelList.getValue().get(mutableSongViewModelList.getValue().size() - 1).getId()) {
                songId = mutableSongViewModelList.getValue().get(0).getId();
            }

            Song song = songRepository.getSong(String.valueOf(songId));

            if (song == null) {
                song = songRepository.getMaxSong(String.valueOf(songId));
            }
            songMutableLiveData.postValue(song);

        }

        setCanClick(true);
    }

    public void getPrevSongById() {

        if (canClick){
            int songId = sharedPrefrenceManager.getSong().getId() - 1;

            if (songId + 1 == mutableSongViewModelList.getValue().get(0).getId()) {
                songId = mutableSongViewModelList.getValue().get(mutableSongViewModelList.getValue().size() - 1).getId();
            }

            Song song = songRepository.getSong(String.valueOf(songId));

            if (song == null) {
                song = songRepository.getMinSong(String.valueOf(songId));

            }

            songMutableLiveData.postValue(song);

        }
        setCanClick(true);
    }

    public boolean isCanClick() {
        return canClick;
    }

    public void setCanClick(boolean canClick) {
        this.canClick = canClick;
    }

    public MutableLiveData<Song> getSongMutableLiveData() {
        return songMutableLiveData;
    }

    public void deleteSongById(int id) {
        songRepository.deleteSong(id);
    }

    @Bindable
    public List<SongViewModel> getUserViewModelList1() {
        return userViewModelList1;
    }

    public void setUserViewModelList1(List<SongViewModel> userViewModelList1) {
        this.userViewModelList1 = userViewModelList1;
        notifyPropertyChanged(BR.userViewModelList1);
    }

    public Song getViewModelSong(){
        Song song = new Song();
        song.setSongName(songName);
        song.setAlbumName(albumName);
        song.setArtistName(artistName);
        song.setTrackFile(trackFile);
        song.setDuration(duration);
        song.setSongImageUri(songImageUri);
        song.setContentID(contentID);
        song.setId(id);

        return song;
    }

    public int getId() {
        return id;
    }

    public String getSongName() {
        return songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getTrackFile() {
        return trackFile;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getDuration() {
        return duration;
    }

    public SongAdapter getSongAdapter() {
        return songAdapter;
    }

    public String getSongImageUri() {
        return songImageUri;
    }

    public int getContentID() {
        return contentID;
    }

    @Bindable
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
        notifyPropertyChanged(BR.context);
    }

    public MutableLiveData<List<SongViewModel>> getMutableSongViewModelList() {
        return mutableSongViewModelList;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setTrackFile(String trackFile) {
        this.trackFile = trackFile;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setSongImageUri(String songImageUri) {
        this.songImageUri = songImageUri;
    }

    public void setContentID(int contentID) {
        this.contentID = contentID;
    }
}
