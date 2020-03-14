package com.example.ps.musicps.Di.module;

import android.content.Context;

import com.example.ps.musicps.viewmodels.SongViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class ListActivityModule {

    private Context context;

    public ListActivityModule(Context context) {
        this.context = context;
    }

    @Provides
    Context getContext(){
        return context;
    }



}
