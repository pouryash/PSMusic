package com.example.ps.musicps.Di.PlaySongModel;

import android.app.Application;
import android.content.Context;

import com.example.ps.musicps.MVP.PlaySongMVP;

import dagger.Module;
import dagger.Provides;

@Module
public class RequiredPlySongPresenterOpsModule {

    private PlaySongMVP.RequiredPlaySongPresenterOps requiredPlaySongPresenterOps;

    public RequiredPlySongPresenterOpsModule(PlaySongMVP.RequiredPlaySongPresenterOps requiredPlaySongPresenterOps) {
        this.requiredPlaySongPresenterOps = requiredPlaySongPresenterOps;
    }

    @Provides
    public PlaySongMVP.RequiredPlaySongPresenterOps provideRequiredPlaySongPresenter() {
        return requiredPlaySongPresenterOps;
    }

}
