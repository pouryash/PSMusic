package com.example.ps.musicps.viewmodels;

import android.content.Context;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.ps.musicps.BR;
import com.example.ps.musicps.Model.SongInfo;

public class SongInfoViewModel extends BaseObservable {

    private String title;
    private String Album;
    private String duration;
    private String size;
    private String path;
    private Context context;

    public SongInfoViewModel(SongInfo songInfo) {
        this.title = songInfo.getTitle();
        this.Album = songInfo.getAlbum();
        this.duration = songInfo.getDuration();
        this.size = songInfo.getSize();
        this.path = songInfo.getPath();
    }

    public void setSongInfo(SongInfo songInfo) {
        setTitle(songInfo.getTitle());
        setAlbum(songInfo.getAlbum());
        setDuration(songInfo.getDuration());
        setSize(songInfo.getSize());
        setPath(songInfo.getPath());
    }

    public SongInfoViewModel(Context context) {
        this.context = context;
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public String getAlbum() {
        return Album;
    }

    public void setAlbum(String album) {
        Album = album;
        notifyPropertyChanged(BR.album);
    }

    @Bindable
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
        notifyPropertyChanged(BR.duration);
    }

    @Bindable
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
        notifyPropertyChanged(BR.size);
    }

    @Bindable
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        notifyPropertyChanged(BR.path);
    }
}
