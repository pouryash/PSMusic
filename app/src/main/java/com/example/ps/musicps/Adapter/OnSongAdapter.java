package com.example.ps.musicps.Adapter;

import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.Model.SongInfo;

public interface OnSongAdapter {

        void onSongClicked(Song song);

        void onSongRemoved(int id, int size);

        void onMenuInfoClicked(SongInfo songInfo);
}
