package com.example.ps.musicps.Di.PlaySongModel;

import android.app.Application;
import android.content.Context;

import com.example.ps.musicps.MVP.PlaySongMVP;
import com.example.ps.musicps.MVP.PlaySongModel;

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

    @Provides
    PlaySongMVP.ProvidedPlaySongModelOps providePlaySongModel
            (PlaySongMVP.RequiredPlaySongPresenterOps playSongPresenterOps) {

        return new PlaySongModel(playSongPresenterOps);
    }

}
