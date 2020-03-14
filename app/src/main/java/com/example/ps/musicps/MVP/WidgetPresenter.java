package com.example.ps.musicps.MVP;

import android.content.Context;

import com.example.ps.musicps.Model.Song;

import java.util.ArrayList;

public class WidgetPresenter implements WidgetSongsListMVP.ProvidedPresenterOps,
        WidgetSongsListMVP.RequiredPresenterOps {

    WidgetSongsListMVP.RequiredSongsListViewOps mView;
    WidgetSongsListMVP.ProvidedModelOps mModel ;



    public WidgetPresenter(Context context , WidgetSongsListMVP.RequiredSongsListViewOps view) {

        mModel = new WidgetSongsListModel(this,context);
        mView = view;

    }


    @Override
    public void getSongsList() {
        mModel.getSongs();
    }

    @Override
    public void getSong(int pos) {
        mModel.getSong(pos);
    }


    @Override
    public void onSongsRecived(ArrayList<Song> songs) {
        if (songs!= null && songs.size() > 0){
            mView.onSongListFinished(songs);
        }
    }

    @Override
    public void onSongRecived(Song song) {

        mView.onSongRecived(song);
    }

    @Override
    public void onError(String message) {

    }
}
