package com.example.ps.musicps.Di.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {

   private Context context;

    public ContextModule(Context context) {
        this.context = context;
    }

    @Provides
    Context provideContext(Context context){
        return context;
    }
}
