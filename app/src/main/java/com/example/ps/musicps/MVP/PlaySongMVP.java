package com.example.ps.musicps.MVP;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.example.ps.musicps.Model.Song;

import dagger.Module;
import dagger.Provides;


public interface PlaySongMVP {

    interface RequiredPlaySongViewOps {

        Context getAppContext();

        Activity getActivityContext();

        void showToast(Toast toast);

        void onSongRecived(Song song);

    }


    interface ProvidedPlaySongPresenterOps {

        void setView(RequiredPlaySongViewOps view);

        void getSong(int pos);


    }

    interface RequiredPlaySongPresenterOps {

        Context getAppContext();

        Context getActivityContext();

        void onSongRecived(Song song);

        void onError(String message);

    }

    interface ProvidedPlaySongModelOps {

        void getSong(int pos);

    }

}
