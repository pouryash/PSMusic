package com.example.ps.musicps.MVP;

import android.content.Context;
import android.widget.Toast;

import com.example.ps.musicps.Model.Song;
import java.lang.ref.WeakReference;
import javax.inject.Inject;

public class PlaySongPresenter implements PlaySongMVP.ProvidedPlaySongPresenterOps,
        PlaySongMVP.RequiredPlaySongPresenterOps {

    WeakReference<PlaySongMVP.RequiredPlaySongViewOps> mView;
    @Inject
    PlaySongMVP.ProvidedPlaySongModelOps mModel;


    public PlaySongPresenter() {

//        mModel = new PlaySongModel(this);

    }


    @Override
    public void setView(PlaySongMVP.RequiredPlaySongViewOps view) {
        mView = new WeakReference<>(view);
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
    public void onSongRecived(Song song) {
        mView.get().onSongRecived(song);
    }

    @Override
    public void onError(String message) {
        mView.get().showToast(Toast.makeText(getAppContext(), message, Toast.LENGTH_LONG));
    }

    @Override
    public void getSong(int pos) {
        mModel.getSong(pos);
    }

}
