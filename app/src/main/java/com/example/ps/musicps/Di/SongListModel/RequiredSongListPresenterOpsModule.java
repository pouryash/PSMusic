package com.example.ps.musicps.Di.SongListModel;

import com.example.ps.musicps.MVP.SongsListMVP;
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

}
