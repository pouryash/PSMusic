package com.example.ps.musicps.Commen;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;

import com.example.ps.musicps.Model.Song;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class SongSharedPrefrenceManager {

    private static final String SONG_SHARED_PREF_NAME = "song_shared_pref";
    private static final String FIRST_IN_SHARED_PREF_NAME = "first_in_shared_pref";
    private static final String KEY_FIRST_IN = "first_in";
    public static final String KEY_SONG_MODEL = "song_model";
    private static final String KEY_CURENT_PLAYING_STATE = "curentPlayingState";
    private SharedPreferences songSharedPreferences;
    private SharedPreferences firstInSharedPreferences;
    private SharedPreferenceSongLiveData sharedPreferenceSongLiveData;



    @Inject
    public SongSharedPrefrenceManager(@Named("context") Context context) {

        songSharedPreferences = context.getSharedPreferences(SONG_SHARED_PREF_NAME, context.MODE_PRIVATE);
        firstInSharedPreferences = context.getSharedPreferences(FIRST_IN_SHARED_PREF_NAME, context.MODE_PRIVATE);

        if (getPlayingState().equals("")){
            setPlayingState("repeatOne");
        }
    }

    public SharedPreferenceSongLiveData getSharedPrefsSongLiveData(String key) {
        if (sharedPreferenceSongLiveData == null)
            sharedPreferenceSongLiveData = new SharedPreferenceSongLiveData(songSharedPreferences, key, new Song());
        return sharedPreferenceSongLiveData;
    }


    public void saveSong(Song song) {
        if (song != null) {
            SharedPreferences.Editor editor = songSharedPreferences.edit();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", song.getId());
                jsonObject.put("time", song.getDuration());
                jsonObject.put("songName", song.getSongName());
                jsonObject.put("imgUrl", song.getSongImageUri());
                jsonObject.put("albumName", song.getAlbumName());
                jsonObject.put("artist", song.getArtistName());
                jsonObject.put("path", song.getTrackFile());
                jsonObject.put("contentId", song.getContentID());
                editor.putString(KEY_SONG_MODEL, jsonObject.toString());
                editor.apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPlayingState(String state){
        SharedPreferences.Editor editor = songSharedPreferences.edit();
        editor.putString(KEY_CURENT_PLAYING_STATE, state);
        editor.apply();
    }

    public String getPlayingState(){
        return songSharedPreferences.getString(KEY_CURENT_PLAYING_STATE, "");
    }

    public Song getSong() {
        Song model = new Song();
        try {
            JSONObject jsonObject = new JSONObject(songSharedPreferences.getString(KEY_SONG_MODEL, ""));
            model.setId(Integer.parseInt(jsonObject.getString("id")));
            model.setDuration(jsonObject.getString("time"));
            model.setSongName(jsonObject.getString("songName"));
            model.setAlbumName(jsonObject.getString("albumName"));
            model.setTrackFile(jsonObject.getString("path"));
            model.setArtistName(jsonObject.getString("artist"));
            model.setSongImageUri(jsonObject.getString("imgUrl"));
            model.setContentID(Integer.parseInt(jsonObject.getString("contentId")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return model;
    }

    public void setFirstIn(boolean firstIn) {
        SharedPreferences.Editor editor = firstInSharedPreferences.edit();
        editor.putBoolean(KEY_FIRST_IN, firstIn);
        editor.apply();
    }

    public boolean getFirstIn() {

        return firstInSharedPreferences.getBoolean(KEY_FIRST_IN, true);
    }

    public abstract class SharedPreferenceLiveData<T> extends LiveData<T>{
        SharedPreferences sharedPrefs;
        String key;
        public T defValue;


        public SharedPreferenceLiveData(SharedPreferences sharedPrefs, String key, T defValue) {
            this.sharedPrefs = sharedPrefs;
            this.key = key;
            this.defValue = defValue;
        }

        SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s){
                if (SharedPreferenceLiveData.this.key.equals(s))
                    setValue(getValueFromPreferences(s, defValue));
            }
        };

        abstract T getValueFromPreferences(String key, T defValue);

//        @Override
//        protected void onActive() {
//            super.onActive();
//            isActive = true;
//            setValue(getValueFromPreferences(key, defValue));
//            sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
//        }
//
//        @Override
//        protected void onInactive() {
////            sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
//            isActive = false;
//            super.onInactive();
//        }

        public SharedPreferenceLiveData<Song> getSongLiveData(String key, Song defaultValue) {
            return new SharedPreferenceSongLiveData(sharedPrefs,key, defaultValue);
        }

    }

    public class SharedPreferenceSongLiveData extends SharedPreferenceLiveData<Song>{

        public SharedPreferenceSongLiveData(SharedPreferences prefs, String key, Song song) {
            super(prefs, key, song);
            sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        }

        @Override
        Song getValueFromPreferences(String key, Song song) {

            return getSong();
        }

    }

}
