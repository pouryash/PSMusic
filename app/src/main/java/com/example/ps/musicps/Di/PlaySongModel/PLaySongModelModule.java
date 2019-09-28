package com.example.ps.musicps.Di.PlaySongModel;

import android.content.Context;
import com.example.ps.musicps.MVP.PlaySongMVP;
import com.example.ps.musicps.MVP.PlaySongModel;
import com.example.ps.musicps.MVP.PlaySongPresenter;

import dagger.Module;
import dagger.Provides;


@Module
public class PLaySongModelModule {


    @Provides
    PlaySongMVP.ProvidedPlaySongModelOps providePlaySongModel
            (PlaySongMVP.RequiredPlaySongPresenterOps playSongPresenterOps) {

        return new PlaySongModel(playSongPresenterOps);
    }


}
