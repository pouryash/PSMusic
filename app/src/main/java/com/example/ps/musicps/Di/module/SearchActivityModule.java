package com.example.ps.musicps.Di.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class SearchActivityModule {

    private Context context;

    public SearchActivityModule(Context context) {
        this.context = context;
    }

    @Provides
    Context getContext(){
        return context;
    }



}
