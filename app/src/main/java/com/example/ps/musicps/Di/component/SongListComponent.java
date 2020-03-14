package com.example.ps.musicps.Di.component;


import com.example.ps.musicps.Di.Scop.ActivityScop;
import com.example.ps.musicps.Di.module.ContextModule;
import com.example.ps.musicps.Helper.SongsConfigHelper;
import com.example.ps.musicps.MVP.SongsListPresenter;
import com.example.ps.musicps.Repository.SongRepository;

import dagger.Component;

@Component(modules = ContextModule.class)
@ActivityScop
public interface SongListComponent {

    SongRepository getSongRepository();

    SongsConfigHelper getSongConfigHelper();

}
