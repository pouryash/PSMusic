package com.example.ps.musicps.Di.module;

import android.content.Context;

import com.example.ps.musicps.Adapter.OnSongAdapter;
import com.example.ps.musicps.Adapter.SongAdapter;
import com.example.ps.musicps.Helper.SongsConfigHelper;
import com.example.ps.musicps.viewmodels.SongViewModel;

import java.util.List;

import dagger.Module;
import dagger.Provides;

@Module
public class SongAdapterModule {

    private OnSongAdapter onSongAdapter;
    private List<SongViewModel> songViewModels;
    private Context context;

    public SongAdapterModule(OnSongAdapter onSongAdapter,
                             List<SongViewModel> songViewModels,
                             Context context) {
        this.onSongAdapter = onSongAdapter;
        this.songViewModels = songViewModels;
        this.context = context;
    }

    @Provides
    OnSongAdapter getOnSongAdapter(){
        return onSongAdapter;
    }

    @Provides
    List<SongViewModel> getSongViewModels(){
        return songViewModels;
    }

    @Provides
    Context getContext(){
        return context;
    }


}
