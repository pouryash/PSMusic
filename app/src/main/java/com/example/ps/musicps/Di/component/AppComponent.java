package com.example.ps.musicps.Di.component;

import android.content.Context;

import com.example.ps.musicps.Commen.SongSharedPrefrenceManager;
import com.example.ps.musicps.Helper.MusiPlayerHelper;
import com.example.ps.musicps.Repository.DbRepository;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Component
@Singleton
public interface AppComponent {

    MusiPlayerHelper getMusicPlayerHelper();

    SongSharedPrefrenceManager getSharedPrefrence();

    DbRepository getDbRepo();

    @Component.Builder
    interface Builder{

        @BindsInstance
        Builder context(@Named("context")Context context);

        AppComponent build();

    }

}
