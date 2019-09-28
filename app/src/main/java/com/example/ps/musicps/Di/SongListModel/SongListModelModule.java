package com.example.ps.musicps.Di.SongListModel;

import com.example.ps.musicps.MVP.PlaySongMVP;
import com.example.ps.musicps.MVP.PlaySongModel;
import com.example.ps.musicps.MVP.SongsListMVP;
import com.example.ps.musicps.MVP.SongsListModel;

import dagger.Module;
import dagger.Provides;


@Module
public class SongListModelModule {


    @Provides
    SongsListMVP.ProvidedModelOps providedSongListModelOps
            (SongsListMVP.RequiredPresenterOps requiredPresenterOps) {

        return new SongsListModel(requiredPresenterOps);
    }


}
