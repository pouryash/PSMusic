package com.example.ps.musicps.viewmodels;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.ps.musicps.BR;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.Model.SongInfo;
import com.example.ps.musicps.R;

public class SongPanelViewModel extends BaseObservable {

    private int id;
    private String songName;
    private String albumName;
    private String duration;
    private String currentDuration;
    private String path;
    private String imageUri;
    private Context context;
    private int maxDuration;
    private int progressDuration;

    public SongPanelViewModel(Song song) {
        this.songName = song.getSongName();
        this.albumName = song.getAlbumName();
        this.duration = song.getDuration();
        this.path = song.getTrackFile();
        this.imageUri = song.getSongImageUri();
    }


    @BindingAdapter({"imgaeUri", "context"})
    public static void loadImage(ImageView iv, String uri, Context context) {
        Glide.with(iv.getContext()).asBitmap().load(Uri.parse(uri))
                .apply(new RequestOptions().placeholder(R.drawable.ic_no_album))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        iv.setBackgroundColor(Color.parseColor("#d1d9ff"));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(iv);
    }

    public SongPanelViewModel(Context context) {
        this.context = context;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Bindable
    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
        notifyPropertyChanged(BR.songName);
    }

    @Bindable
    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
        notifyPropertyChanged(BR.imageUri);
    }

    @Bindable
    public int getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
        notifyPropertyChanged(BR.maxDuration);
    }

    @Bindable
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
        notifyPropertyChanged(BR.context);
    }

    @Bindable
    public int getProgressDuration() {
        return progressDuration;
    }

    public void setProgressDuration(int progressDuration) {
        this.progressDuration = progressDuration;
        notifyPropertyChanged(BR.progressDuration);
    }

    @Bindable
    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
        notifyPropertyChanged(BR.albumName);
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
    public String getCurrentDuration() {
        return currentDuration;
    }

    public void setCurrentDuration(String currentDuration) {
        this.currentDuration = currentDuration;
        notifyPropertyChanged(BR.currentDuration);
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
