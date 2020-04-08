package com.example.ps.musicps.Di.component;


import com.example.ps.musicps.Di.Scop.ActivityScop;
import com.example.ps.musicps.Di.module.ListActivityModule;
import com.example.ps.musicps.Di.module.MainActivityModule;
import com.example.ps.musicps.View.ListActivity;
import com.example.ps.musicps.View.MainActivity;

import dagger.Component;

@Component(modules = {MainActivityModule.class})
@ActivityScop
public interface MainActivityComponent {

    void inject(MainActivity mainActivity);

}
