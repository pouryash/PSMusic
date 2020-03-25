package com.example.ps.musicps.Di.component;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;

import com.example.ps.musicps.Helper.ServiceConnectionBinder;

import javax.inject.Named;

import dagger.BindsInstance;
import dagger.Component;

@Component
public interface MusicServiceComponent {

    ServiceConnectionBinder getServiceBinder();

    @Component.Builder
    interface Builder{

        @BindsInstance
        MusicServiceComponent.Builder Activity(@Named("activity") Activity activity);

        @BindsInstance
        MusicServiceComponent.Builder LifecycleOwner(@Named("lifecycleOwner") LifecycleOwner lifecycleOwner);

        MusicServiceComponent build();

    }
}
