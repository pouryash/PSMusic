package com.example.ps.musicps.Di.module;

import android.content.Context;

import com.example.ps.musicps.Adapter.SongAdapter;
import com.example.ps.musicps.Adapter.SongSearchAdapter;
import com.example.ps.musicps.viewmodels.SearchSongViewModel;
import com.example.ps.musicps.viewmodels.SongViewModel;

import java.util.List;

import dagger.Module;
import dagger.Provides;

@Module
public class SongSearchAdapterModule {

    private SongSearchAdapter.onSearchAdpSong onSongAdapter;
    private List<SearchSongViewModel> songViewModels;
    private Context context;

    public SongSearchAdapterModule(SongSearchAdapter.onSearchAdpSong onSongAdapter,
                                   List<SearchSongViewModel> songViewModels,
                                   Context context) {
        this.onSongAdapter = onSongAdapter;
        this.songViewModels = songViewModels;
        this.context = context;
    }

    @Provides
    SongSearchAdapter.onSearchAdpSong getOnSongAdapter(){
        return onSongAdapter;
    }

    @Provides
    List<SearchSongViewModel> getSongViewModels(){
        return songViewModels;
    }

    @Provides
    Context getContext(){
        return context;
    }


}
