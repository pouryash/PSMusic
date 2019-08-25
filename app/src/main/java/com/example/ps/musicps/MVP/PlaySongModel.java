package com.example.ps.musicps.MVP;

import com.example.ps.musicps.Commen.SongTask;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.SongListActivity;

import java.util.ArrayList;


public class PlaySongModel implements PlaySongMVP.ProvidedPlaySongModelOps, SongTask.onSongListFinished {


    private PlaySongMVP.RequiredPlaySongPresenterOps mPresenter;
    ArrayList<Song> songsList;
    int position;


    public PlaySongModel(PlaySongMVP.RequiredPlaySongPresenterOps presenter) {
        this.mPresenter = presenter;
    }


    @Override
    public void getSong(int pos) {
        if (songsList!=null && songsList.size() > SongListActivity.songList.size()){
            songsList = SongListActivity.songList;
        }
        position = pos;
        if (songsList == null) {
            SongTask songTask = new SongTask(mPresenter.getActivityContext(), this);
            songTask.execute();
        } else {
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

    }

    @Override
    public void onSongListFinished(ArrayList<Song> songs) {
        songsList = songs;
        mPresenter.onSongRecived(songsList.get(position));
    }
}
