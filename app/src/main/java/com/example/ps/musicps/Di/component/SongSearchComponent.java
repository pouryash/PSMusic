package com.example.ps.musicps.Di.component;


import com.example.ps.musicps.Di.Scop.ActivityScop;
import com.example.ps.musicps.Di.module.SearchActivityModule;
import com.example.ps.musicps.SearchActivity;
import com.example.ps.musicps.View.ListActivity;

import dagger.Component;

@Component(modules = {SearchActivityModule.class})
@ActivityScop
public interface SongSearchComponent {


    void inject(SearchActivity searchActivity);

}
