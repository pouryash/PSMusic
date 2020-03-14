package com.example.ps.musicps.Di.module;

import com.example.ps.musicps.MVP.PlaySongMVP;
import com.example.ps.musicps.MVP.PlaySongModel;

import dagger.Module;
import dagger.Provides;

@Module
public class PlaySongModule {

    private PlaySongMVP.RequiredPlaySongPresenterOps requiredPlaySongPresenterOps;

    public PlaySongModule(PlaySongMVP.RequiredPlaySongPresenterOps requiredPlaySongPresenterOps) {
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
