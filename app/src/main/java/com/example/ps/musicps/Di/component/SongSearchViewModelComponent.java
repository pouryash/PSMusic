package com.example.ps.musicps.Di.component;

import com.example.ps.musicps.Adapter.SongAdapter;
import com.example.ps.musicps.Adapter.SongSearchAdapter;
import com.example.ps.musicps.Di.Scop.ActivityScop;
import com.example.ps.musicps.Di.module.SongAdapterModule;
import com.example.ps.musicps.Di.module.SongSearchAdapterModule;
import com.example.ps.musicps.Repository.SongRepository;

import dagger.Component;

@Component(modules = {SongSearchAdapterModule.class})
@ActivityScop
public interface SongSearchViewModelComponent {

    SongRepository getSongRepository();

    SongSearchAdapter getSongSearchAdapter();


}
