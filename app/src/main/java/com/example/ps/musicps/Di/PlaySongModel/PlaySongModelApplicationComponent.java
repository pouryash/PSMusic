package com.example.ps.musicps.Di.PlaySongModel;


import com.example.ps.musicps.MVP.PlaySongPresenter;

import javax.inject.Singleton;
import dagger.Component;

@Component(modules = {RequiredPlySongPresenterOpsModule.class})
@Singleton
public interface PlaySongModelApplicationComponent {

    void inject(PlaySongPresenter playSongPresenter);

}
