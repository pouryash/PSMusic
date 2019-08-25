package com.example.ps.musicps.MVP;

import android.content.Context;
import android.widget.Toast;

import com.example.ps.musicps.Model.Song;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SongsListPresenter implements SongsListMVP.ProvidedPresenterOps,
        SongsListMVP.RequiredPresenterOps {

    WeakReference<SongsListMVP.RequiredSongsListViewOps> mView;
    SongsListMVP.ProvidedModelOps mModel ;
    Context con;



    public SongsListPresenter() {

        mModel = new SongsListModel(this);
    }




    @Override
    public void setView(SongsListMVP.RequiredSongsListViewOps view) {
        mView =new WeakReference<>(view);
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
    public Context getAppContext() {
        return mView.get().getAppContext();
    }

    @Override
    public Context getActivityContext() {
        return mView.get().getActivityContext();
    }

    @Override
    public void onSongsRecived(ArrayList<Song> songs) {
        if (songs!= null && songs.size() > 0){
            mView.get().onSongListFinished(songs);
        }else {
            mView.get().showToast(Toast.makeText(getAppContext(),"opss no song found!",Toast.LENGTH_LONG));
        }
    }

    @Override
    public void onSongRecived(Song song) {

        mView.get().onSongRecived(song);
    }

    @Override
    public void onError(String message) {
        mView.get().showToast(Toast.makeText(getAppContext(),message,Toast.LENGTH_LONG));
    }
}
