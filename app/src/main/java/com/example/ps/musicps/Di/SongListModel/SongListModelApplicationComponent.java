package com.example.ps.musicps.Di.SongListModel;


import com.example.ps.musicps.MVP.SongsListPresenter;
import javax.inject.Singleton;
import dagger.Component;

@Component(modules = {RequiredSongListPresenterOpsModule.class})
@Singleton
public interface SongListModelApplicationComponent {

    void inject(SongsListPresenter songsListPresenter);

}
