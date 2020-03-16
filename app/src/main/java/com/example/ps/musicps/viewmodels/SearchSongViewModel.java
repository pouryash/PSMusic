package com.example.ps.musicps.viewmodels;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.example.ps.musicps.Adapter.SongSearchAdapter;
import com.example.ps.musicps.BR;
import com.example.ps.musicps.Di.component.DaggerSongSearchViewModelComponent;
import com.example.ps.musicps.Di.component.SongSearchViewModelComponent;
import com.example.ps.musicps.Di.module.SongAdapterModule;
import com.example.ps.musicps.Di.module.SongSearchAdapterModule;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.R;
import com.example.ps.musicps.Repository.SongRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SearchSongViewModel extends BaseObservable {

    private MutableLiveData<List<SearchSongViewModel>> mutableSongViewModelList = new MutableLiveData<>();
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
    private String filterText;
    private SongSearchAdapter songAdapter;
    private SongSearchViewModelComponent component;
    private List<SearchSongViewModel> searchViewModelList1 = new ArrayList<>();

    @Inject
    public SearchSongViewModel(Context context) {
        setContext(context);
        component = DaggerSongSearchViewModelComponent.builder()
                .songSearchAdapterModule(new SongSearchAdapterModule((SongSearchAdapter.onSearchAdpSong) context, searchViewModelList1, context))
                .build();
        songRepository = component.getSongRepository();
        songAdapter = component.getSongSearchAdapter();
    }

    public SearchSongViewModel(Song song) {
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
    public static void getRecyclerBinder(RecyclerView recyclerView, MutableLiveData<List<SearchSongViewModel>> mutableSongViewModelList,
                                         Context context, SongSearchAdapter songAdapter, List<SearchSongViewModel> songViewModels) {


        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(songAdapter);

        SongSearchAdapter finalSongAdapter = songAdapter;
        mutableSongViewModelList.observe((LifecycleOwner) context, songViewModellist -> {
            songViewModels.clear();
            songViewModels.addAll(songViewModellist);
            finalSongAdapter.notifyDataSetChanged();
        });

    }

    @BindingAdapter({"bind:Uri", "bind:context"})
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

    @BindingAdapter({"bind:songName","bind:filterText"})
    public static void loadSearchName(TextView tv, String name, String filterText) {
        if (filterText == null)
            filterText = "";
        if (name != null) {

            ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(229, 23, 23));

            int indexSongName = name.toLowerCase().indexOf(filterText.toLowerCase());

            if (indexSongName > -1) {
                SpannableStringBuilder sb = new SpannableStringBuilder(name);

                sb.setSpan(fcs, indexSongName, indexSongName + filterText.length(), 0);
                tv.setText(sb);

            }else {
                tv.setText(name);
            }

        }
    }

    public void getSongs() {
        songRepository.getSongs().observe((LifecycleOwner) context, songsViewModel -> {

                List<SearchSongViewModel> songViewModels = new ArrayList<>();
                for (int i = 0; i < songsViewModel.size(); i++) {
                    songViewModels.add(new SearchSongViewModel(new SongViewModel(songsViewModel.get(i)).getViewModelSong()));
                }
                mutableSongViewModelList.postValue(songViewModels);
        });
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

    public MutableLiveData<List<SearchSongViewModel>> getMutableSongViewModelList() {
        return mutableSongViewModelList;
    }

    public MutableLiveData<Song> getSongMutableLiveData() {
        return songMutableLiveData;
    }


    @Bindable
    public SongSearchAdapter getSongAdapter() {
        return songAdapter;
    }

    public void setSongAdapter(SongSearchAdapter songAdapter) {
        this.songAdapter = songAdapter;
        notifyPropertyChanged(BR.songAdapter);
    }

    @Bindable
    public List<SearchSongViewModel> getSearchViewModelList1() {
        return searchViewModelList1;
    }

    public void setSearchViewModelList1(List<SearchSongViewModel> searchViewModelList1) {
        this.searchViewModelList1 = searchViewModelList1;
        notifyPropertyChanged(BR.userViewModelList1);
    }

    @Bindable
    public String getFilterText() {
        return filterText;
    }

    public void setFilterText(String filterText) {
        this.filterText = filterText;
        notifyPropertyChanged(BR.filterText);
    }

    @Bindable
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
        notifyPropertyChanged(BR.songName);
    }

    @Bindable
    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
        notifyPropertyChanged(BR.artistName);
    }

    @Bindable
    public String getTrackFile() {
        return trackFile;
    }

    public void setTrackFile(String trackFile) {
        this.trackFile = trackFile;
        notifyPropertyChanged(BR.trackFile);
    }

    @Bindable
    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
        notifyPropertyChanged(BR.albumName);
    }

    @Bindable
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
        notifyPropertyChanged(BR.duration);
    }

    @Bindable
    public String getSongImageUri() {
        return songImageUri;
    }

    public void setSongImageUri(String songImageUri) {
        this.songImageUri = songImageUri;
        notifyPropertyChanged(BR.songImageUri);
    }

    @Bindable
    public int getContentID() {
        return contentID;
    }

    public void setContentID(int contentID) {
        this.contentID = contentID;
        notifyPropertyChanged(BR.contentID);
    }

    @Bindable
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
        notifyPropertyChanged(BR.context);
    }
}
