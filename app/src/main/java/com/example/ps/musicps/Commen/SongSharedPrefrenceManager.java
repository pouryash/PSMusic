package com.example.ps.musicps.Commen;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ps.musicps.Model.Song;

public class SongSharedPrefrenceManager {

    private static final String SONG_SHARED_PREF_NAME = "song_shared_pref";
    private static final String FIRST_IN_SHARED_PREF_NAME = "first_in_shared_pref";

    private static final String KEY_FIRST_IN = "first_in";

    private static final String KEY_SONG_NAME = "song_name";
    private static final String KEY_SONG_ARTIST_NAME = "song_artist_name";
    private static final String KEY_ALBUM_NAME = "album_name";
    private static final String KEY_SONG_TRACK_FILE = "song_track_file";
    private static final String KEY_SONG_DURATION = "song_duration";
    private static final String KEY_SONG_IMAGE_URI = "song_image_uri";
    private static final String KEY_SONG_ID = "song_id";

    private SharedPreferences songSharedPreferences;
    private SharedPreferences firstInSharedPreferences;




    public SongSharedPrefrenceManager(Context context) {

        songSharedPreferences = context.getSharedPreferences(SONG_SHARED_PREF_NAME, context.MODE_PRIVATE);
        firstInSharedPreferences = context.getSharedPreferences(FIRST_IN_SHARED_PREF_NAME, context.MODE_PRIVATE);
    }


    public void saveSong(Song song) {
        if (song != null) {
            SharedPreferences.Editor editor = songSharedPreferences.edit();
            editor.putInt(KEY_SONG_ID, song.getId());
            editor.putString(KEY_SONG_NAME, song.getSongName());
            editor.putString(KEY_SONG_ARTIST_NAME, song.getArtistName());
            editor.putString(KEY_ALBUM_NAME, song.getAlbumName());
            editor.putString(KEY_SONG_TRACK_FILE, song.getTrackFile());
            editor.putString(KEY_SONG_DURATION, song.getDuration());
            editor.putString(KEY_SONG_IMAGE_URI, song.getSongImageUri());
            editor.apply();
        }
    }


    public Song getSong() {
        Song song = new Song();
        song.setSongName(songSharedPreferences.getString(KEY_SONG_NAME, ""));
        song.setArtistName(songSharedPreferences.getString(KEY_SONG_ARTIST_NAME, ""));
        song.setAlbumName(songSharedPreferences.getString(KEY_ALBUM_NAME, ""));
        song.setTrackFile(songSharedPreferences.getString(KEY_SONG_TRACK_FILE, ""));
        song.setDuration(songSharedPreferences.getString(KEY_SONG_DURATION, ""));
        song.setSongImageUri(songSharedPreferences.getString(KEY_SONG_IMAGE_URI, ""));
        song.setId(songSharedPreferences.getInt(KEY_SONG_ID, 0));
        return song;
    }

    public void setFirstIn() {
        SharedPreferences.Editor editor = firstInSharedPreferences.edit();
        editor.putBoolean(KEY_FIRST_IN, true);
        editor.apply();
    }

    public boolean getFirstIn() {

        return firstInSharedPreferences.getBoolean(KEY_FIRST_IN, false);
    }

}
