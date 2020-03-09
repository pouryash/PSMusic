package com.example.ps.musicps.MVP;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.example.ps.musicps.Model.Song;

import java.util.ArrayList;


public interface WidgetSongsListMVP {

    interface RequiredSongsListViewOps {


        void onSongListFinished(ArrayList<Song> songs);

        void onSongRecived(Song song);

    }


    interface ProvidedPresenterOps {

        void getSongsList();

        void getSong(int pos);

    }

    interface RequiredPresenterOps {


        void onSongsRecived(ArrayList<Song> songs);

        void onSongRecived(Song song);

        void onError(String message);

    }

    interface ProvidedModelOps {

        ArrayList<Song> getSongs();

        void getSong(int pos);
    }

}
