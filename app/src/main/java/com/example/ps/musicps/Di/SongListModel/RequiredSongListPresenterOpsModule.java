package com.example.ps.musicps.Di.SongListModel;

import com.example.ps.musicps.MVP.SongsListMVP;
import com.example.ps.musicps.MVP.SongsListModel;

import dagger.Module;
import dagger.Provides;

@Module
public class RequiredSongListPresenterOpsModule {

    private SongsListMVP.RequiredPresenterOps requiredPresenterOps;

    public RequiredSongListPresenterOpsModule(SongsListMVP.RequiredPresenterOps requiredPresenterOps) {
        this.requiredPresenterOps = requiredPresenterOps;
    }

    @Provides
    public SongsListMVP.RequiredPresenterOps provideRequiredPresenter() {
        return requiredPresenterOps;
    }

    @Provides
    SongsListMVP.ProvidedModelOps providedSongListModelOps
            (SongsListMVP.RequiredPresenterOps requiredPresenterOps) {

        return new SongsListModel(requiredPresenterOps);
    }


}
