package com.example.ps.musicps.MVP;

import com.example.ps.musicps.Commen.SongTask;
import com.example.ps.musicps.Model.Song;

import java.util.ArrayList;


public class SongsListModel implements SongsListMVP.ProvidedModelOps , SongTask.onSongListFinished {


    private SongsListMVP.RequiredPresenterOps mPresenter;
    private ArrayList<Song> songsList;


    public SongsListModel(SongsListMVP.RequiredPresenterOps presenter) {
        this.mPresenter = presenter;
    }


    @Override
    public ArrayList<Song> getSongs() {
        SongTask songTask = new SongTask(mPresenter.getActivityContext(),this);
        songTask.execute();
        return null;
    }

    @Override
    public void getSong(int pos) {

        if (pos > songsList.size()-1 || pos < 0){
            int a;
            if (pos > songsList.size()-1){
                mPresenter.onError("this is the last song!!");
            }else if (pos < 0){
                a = songsList.size();
                mPresenter.onError("this is the first song!!");
            }
        }else {
            mPresenter.onSongRecived(songsList.get(pos));
        }
    }


    @Override
    public void onSongListFinished(ArrayList<Song> songs) {
        songsList = songs;
        mPresenter.onSongsRecived(songs);
    }
}
