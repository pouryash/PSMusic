package com.example.ps.musicps.Repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.ps.musicps.Model.Song;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

public class DbRepository {

    private SongDao songDao;
    private Context context;

    @Inject
    public DbRepository(@Named("context") Context context) {
        this.context = context;

        songDao = DBHelper.getAppDatabase(context).userDao();
    }

    public LiveData<List<Song>> getSongs() {

        try {
            return new GetSongsAsyncTask().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isSongExist(String path){
        if (songDao.getSong(path) != null){
            return true;
        }else {
            return false;
        }
    }

    public Song getSong(String id){
        return songDao.getSongById(Integer.parseInt(id));
    }

    public Song getMinSong(String id){
        return songDao.getSongByIdMin(Integer.parseInt(id));
    }

    public Song getMaxSong(String id){
        return songDao.getSongByIdMax(Integer.parseInt(id));
    }

    public void deleteById(final int id) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                songDao.deleteById(id);
            }
        });
    }

    public void deleteSong(final Song song) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                songDao.deleteSong(song);
            }
        });
    }

    public void insertSong(Song song) {
        songDao.insertSong(song);
    }

    public void updateSong(String location, int id) {
        songDao.updateSong(location, id);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSongsAsyncTask extends AsyncTask<Void, Void, LiveData<List<Song>>> {
        @Override
        protected LiveData<List<Song>> doInBackground(Void... voids) {
            return songDao.getSongs();
        }
    }

}
