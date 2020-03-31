package com.example.ps.musicps.Di.component;


import com.example.ps.musicps.Di.Scop.ActivityScop;
import com.example.ps.musicps.Di.module.ListActivityModule;
import com.example.ps.musicps.View.ListActivity;

import dagger.Component;

@Component(modules = {ListActivityModule.class})
@ActivityScop
public interface SongListComponent {

    void inject(ListActivity listActivity);

}
