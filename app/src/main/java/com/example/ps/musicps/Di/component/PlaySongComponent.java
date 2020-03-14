package com.example.ps.musicps.Di.component;


import com.example.ps.musicps.Di.module.PlaySongModule;
import com.example.ps.musicps.Di.Scop.ActivityScop;

import dagger.Component;

@Component(dependencies = AppComponent.class, modules = {PlaySongModule.class})
@ActivityScop
public interface PlaySongComponent {


}
