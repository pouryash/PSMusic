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
import com.example.ps.musicps.Adapter.OnSongAdapter;
import com.example.ps.musicps.Adapter.SongAdapter;
import com.example.ps.musicps.Adapter.SongSearchAdapter;
import com.example.ps.musicps.BR;
import com.example.ps.musicps.Commen.Commen;
import com.example.ps.musicps.Commen.MyApplication;
import com.example.ps.musicps.Commen.SongSharedPrefrenceManager;
import com.example.ps.musicps.Di.component.DaggerSongViewModelComponent;
import com.example.ps.musicps.Di.component.SongViewModelComponent;
import com.example.ps.musicps.Di.module.SongAdapterModule;
import com.example.ps.musicps.Di.module.SongSearchAdapterModule;
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
    private String filterText;
    private SongAdapter songAdapter;
    private SongSearchAdapter searchAdapter;
    private List<SongViewModel> userViewModelList1 = new ArrayList<>();
    private SongViewModelComponent component;
    private SongSharedPrefrenceManager sharedPrefrenceManager;
    private boolean canClick = true;
    private int faverate;

    @Inject
    public SongViewModel(Context context) {
        setContext(context);
        component = DaggerSongViewModelComponent.builder()
                .songAdapterModule(new SongAdapterModule((OnSongAdapter) context, userViewModelList1, context))
                .build();
        songRepository = component.getSongRepository();
        songAdapter = component.getSongAdapter();
        searchAdapter = component.getSongSearchAdapter();
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
        this.faverate = song.getIsFaverate();
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

    @BindingAdapter({"bind:recyclerBinder", "bind:context", "bind:songAdapter", "bind:viewModelList"})
    public static void getSearchRecyclerBinder(RecyclerView recyclerView, MutableLiveData<List<SongViewModel>> mutableSongViewModelList,
                                               Context context, SongSearchAdapter songSearchAdapter, List<SongViewModel> songViewModels) {


        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(songSearchAdapter);

        SongSearchAdapter finalSongAdapter = songSearchAdapter;
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

    @BindingAdapter({"bind:songName", "bind:filterText"})
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

            } else {
                tv.setText(name);
            }

        }
    }

    public void getSongs() {
        if (songRepository.getSize() > 0) {
            songRepository.getSongs().observe((LifecycleOwner) context, songsViewModel -> {
                if (((MyApplication) context.getApplicationContext()).getState() == MyApplication.LIST_STATE) {
                    List<SongViewModel> songViewModels = new ArrayList<>();
                    for (int i = 0; i < songsViewModel.size(); i++) {
                        songViewModels.add(new SongViewModel(songsViewModel.get(i)));
                    }
                    mutableSongViewModelList.postValue(songViewModels);
                }
            });
        } else updateSongs();
    }

    public void getFaverateSongs() {
        songRepository.getFaverateSongs().observe((LifecycleOwner) context, songsViewModel -> {

            if (((MyApplication) context.getApplicationContext()).getState() == MyApplication.FAVERATE_STATE) {
                List<SongViewModel> songViewModels = new ArrayList<>();
                for (int i = 0; i < songsViewModel.size(); i++) {
                    songViewModels.add(new SongViewModel(songsViewModel.get(i)));
                }
                mutableSongViewModelList.postValue(songViewModels);
            }
        });
    }

    public void updateSongs() {
        songRepository.updateSongs().observe((LifecycleOwner) context, songViewModels -> {
            mutableSongViewModelList.postValue(songViewModels);
        });

    }

    public void updateFaverateSong(int faverate, int id) {
        songRepository.updateFaverateSong(faverate, id);
    }

    @Bindable
    public String getFilterText() {
        return filterText;
    }

    public void setFilterText(String filterText) {
        this.filterText = filterText;
        notifyPropertyChanged(BR.filterText);
    }

    public void getNextSongById() {

        if (canClick && !MyApplication.isExternalSource && ((MyApplication) context.getApplicationContext()).getState()
                == MyApplication.LIST_STATE || !MyApplication.canPlayFaverate) {
            int songId = sharedPrefrenceManager.getSong().getId() + 1;

            if (songId - 1 == mutableSongViewModelList.getValue().get(mutableSongViewModelList.getValue().size() - 1).getId()) {
                songId = mutableSongViewModelList.getValue().get(0).getId();
            }

            Song song = songRepository.getSong(String.valueOf(songId));

            if (song == null) {
                song = songRepository.getMaxSong(String.valueOf(songId));
            }
            songMutableLiveData.postValue(song);

        } else if (MyApplication.isExternalSource) {
            songMutableLiveData.postValue(null);
        } else if (((MyApplication) context.getApplicationContext()).getState()
                == MyApplication.FAVERATE_STATE && MyApplication.canPlayFaverate) {

            int index = Commen.getListItemIndex(mutableSongViewModelList.getValue(), sharedPrefrenceManager.getSong().getId());
            if (index == mutableSongViewModelList.getValue().size() - 1) {
                songMutableLiveData.postValue(mutableSongViewModelList.getValue().get(0).getViewModelSong());
            } else
                songMutableLiveData.postValue(mutableSongViewModelList.getValue().get(index + 1).getViewModelSong());
        }

        setCanClick(true);
    }

    public void getPrevSongById() {

        if (canClick && !MyApplication.isExternalSource && (((MyApplication) context.getApplicationContext()).getState()
                == MyApplication.LIST_STATE || sharedPrefrenceManager.getSong().getIsFaverate() == 0)) {
            int songId = sharedPrefrenceManager.getSong().getId() - 1;

            if (songId + 1 == mutableSongViewModelList.getValue().get(0).getId()) {
                songId = mutableSongViewModelList.getValue().get(mutableSongViewModelList.getValue().size() - 1).getId();
            }

            Song song = songRepository.getSong(String.valueOf(songId));

            if (song == null) {
                song = songRepository.getMinSong(String.valueOf(songId));

            }

            songMutableLiveData.postValue(song);

        } else if (MyApplication.isExternalSource) {
            songMutableLiveData.postValue(null);
        } else if (((MyApplication) context.getApplicationContext()).getState()
                == MyApplication.FAVERATE_STATE) {
            int index = Commen.getListItemIndex(mutableSongViewModelList.getValue(), sharedPrefrenceManager.getSong().getId());
            if (index == 0) {
                songMutableLiveData.postValue(mutableSongViewModelList.getValue().get(mutableSongViewModelList.getValue().size() - 1).getViewModelSong());
            } else
                songMutableLiveData.postValue(mutableSongViewModelList.getValue().get(index - 1).getViewModelSong());
        }

        setCanClick(true);
    }

    public int getFaverate() {
        return faverate;
    }

    public void setFaverate(int faverate) {
        this.faverate = faverate;
    }

    public boolean isSongExist(String path) {
        return songRepository.isSongExist(path);
    }

    public Song getSongByPath(String path) {
        return songRepository.getSongByPath(path);
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

    public Song getViewModelSong() {
        Song song = new Song();
        song.setSongName(songName);
        song.setAlbumName(albumName);
        song.setArtistName(artistName);
        song.setTrackFile(trackFile);
        song.setDuration(duration);
        song.setSongImageUri(songImageUri);
        song.setContentID(contentID);
        song.setIsFaverate(faverate);
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

    public SongSearchAdapter getSongSearchAdapter() {
        return searchAdapter;
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
