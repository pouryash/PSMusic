package com.example.ps.musicps.Di.component;


import com.example.ps.musicps.Di.Scop.ActivityScop;
import com.example.ps.musicps.Di.module.SearchActivityModule;
import com.example.ps.musicps.View.SearchActivity;

import dagger.Component;

@Component(modules = {SearchActivityModule.class})
@ActivityScop
public interface SongSearchComponent {


    void inject(SearchActivity searchActivity);

}
