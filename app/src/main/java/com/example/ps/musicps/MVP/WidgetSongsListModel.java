package com.example.ps.musicps.MVP;

import android.content.Context;

import com.example.ps.musicps.Commen.SongTask;
import com.example.ps.musicps.Model.Song;

import java.util.ArrayList;


public class WidgetSongsListModel implements WidgetSongsListMVP.ProvidedModelOps, SongTask.onSongListFinished {


    private WidgetSongsListMVP.RequiredPresenterOps mPresenter;
    ArrayList<Song> songsList;
    Context context;


    public WidgetSongsListModel(WidgetSongsListMVP.RequiredPresenterOps presenter, Context context) {
        this.mPresenter = presenter;
        this.context = context;
    }


    @Override
    public ArrayList<Song> getSongs() {
        SongTask songTask = new SongTask(context, this);
        songTask.execute();
        return null;
    }

    @Override
    public void getSong(int pos) {

            if (pos > songsList.size() - 1 || pos < 0) {
                int a;
                if (pos > songsList.size() - 1) {
                    mPresenter.onError("last");
                    getSong(0);
                } else if (pos < 0) {
                    mPresenter.onError("first");
                    getSong(songsList.size()-1);
                }
            } else {
                mPresenter.onSongRecived(songsList.get(pos));
            }
    }


    @Override
    public void onSongListFinished(ArrayList<Song> songs) {
        songsList = songs;
        mPresenter.onSongsRecived(songs);
    }
}
