package com.example.ps.musicps.viewmodels;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
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
import com.example.ps.musicps.Di.component.DaggerSongListComponent;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.R;
import com.example.ps.musicps.Repository.SongRepository;

import java.util.ArrayList;
import java.util.List;

public class SongViewModel extends BaseObservable {

    private MutableLiveData<List<SongViewModel>> mutableSongViewModelList = new MutableLiveData<>();
    private SongRepository songRepository;
    private int id;
    private String songName;
    private String artistName;
    private String trackFile;
    private String albumName;
    private String duration;
    private String songImageUri;
    private int ContentID;
    private Context context;
    private SongAdapter songAdapter;

    public SongViewModel(Context context) {
        this.context = context;
        songRepository = DaggerSongListComponent.builder().build().getSongRepository();
    }

    public SongViewModel(Song song) {
        this.id = song.getId();
        this.songName = song.getSongName();
        this.artistName = song.getArtistName();
        this.trackFile = song.getTrackFile();
        this.albumName = song.getAlbumName();
        this.duration = song.getDuration();
        this.songImageUri = song.getSongImageUri();
        ContentID = song.getContentID();
    }

    @BindingAdapter({"bind:recyclerBinder", "bind:context", "bind:songAdapter"})
    public static void getRecyclerBinder(RecyclerView recyclerView, MutableLiveData<List<SongViewModel>> mutableSongViewModelList,
                                         Context context, SongAdapter songAdapter) {

        List<SongViewModel> userViewModelList1 = new ArrayList<>();

        songAdapter = new SongAdapter(userViewModelList1, context, (SongAdapter.onSongAdapter) context);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(songAdapter);

        SongAdapter finalSongAdapter = songAdapter;
        mutableSongViewModelList.observe((LifecycleOwner) context, songViewModellist -> {
            userViewModelList1.clear();
            userViewModelList1.addAll(songViewModellist);
            finalSongAdapter.notifyDataSetChanged();
        });

    }

    @BindingAdapter({"bind:imgaeUri", "bind:context"})
    public static void loadImage(ImageView iv, String uri, Context context) {
        if (uri != null){
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
           if (songsViewModel.size() > 0){
               List<SongViewModel> songViewModels = new ArrayList<>();
               for (int i = 0; i < songsViewModel.size(); i++) {
                   songViewModels.add(new SongViewModel(songsViewModel.get(i)));
               }
               mutableSongViewModelList.postValue(songViewModels);
           }else
               updateSongs();
        });
    }

    public void updateSongs() {
        songRepository.updateSongs().observe((LifecycleOwner)context, songViewModels -> {
            mutableSongViewModelList.postValue(songViewModels);
        });

    }

    public void deleteSongById(int id){
        songRepository.deleteSong(id);
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
        return ContentID;
    }

    public Context getContext() {
        return context;
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
        ContentID = contentID;
    }
}
