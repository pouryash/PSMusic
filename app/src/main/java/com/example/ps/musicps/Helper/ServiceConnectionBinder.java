package com.example.ps.musicps.Helper;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import androidx.lifecycle.LifecycleOwner;
import com.example.ps.musicps.Service.MusicService;

import javax.inject.Inject;
import javax.inject.Named;


public class ServiceConnectionBinder {

    private ServiceConnection serviceConnection;
    private MusicService musicService;
    private onServiceConnectionChanged onServiceConnectionChanged;
    public boolean isServiceConnect = false;

    @Inject
    public ServiceConnectionBinder(@Named("activity") Activity activity,@Named("lifecycleOwner") LifecycleOwner lifecycleOwner) {

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                isServiceConnect = true;
                MusicService.ServiceBinder binder = (MusicService.ServiceBinder) iBinder;
                musicService = binder.getMusicService();
                musicService.registerClient(activity, lifecycleOwner);
                onServiceConnectionChanged.onServiceConnected();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                isServiceConnect = false;
                onServiceConnectionChanged.onServiceDisconnected();
            }
        };

    }

    public void setOnServiceConnectionChanged(onServiceConnectionChanged onServiceConnectionChanged) {

        this.onServiceConnectionChanged = onServiceConnectionChanged;
    }

    public ServiceConnection getServiceConnection() {

        return serviceConnection;

    }

    public MusicService getMusicService() {
        return musicService;
    }

    public interface onServiceConnectionChanged {
        void onServiceConnected();

        void onServiceDisconnected();
    }
}
