package com.example.ps.musicps.Di.component;


import android.content.Context;

import com.example.ps.musicps.Adapter.SongAdapter;
import com.example.ps.musicps.Di.Scop.ActivityScop;
import com.example.ps.musicps.Di.module.ListActivityModule;
import com.example.ps.musicps.Di.module.SongAdapterModule;
import com.example.ps.musicps.Helper.SongsConfigHelper;
import com.example.ps.musicps.Model.SongInfo;
import com.example.ps.musicps.Repository.SongRepository;
import com.example.ps.musicps.View.ListActivity;
import com.example.ps.musicps.viewmodels.SongViewModel;

import java.util.List;

import javax.inject.Named;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = {ListActivityModule.class})
@ActivityScop
public interface SongListComponent {


    void inject(ListActivity listActivity);

}
