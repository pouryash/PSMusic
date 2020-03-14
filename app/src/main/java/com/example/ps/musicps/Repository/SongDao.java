package com.example.ps.musicps.Repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ps.musicps.Model.Song;

import java.util.List;

@Dao
public interface SongDao {

    @Query("SELECT * FROM tbl_song")
    LiveData<List<Song>> getSongs();

    @Query("SELECT * FROM tbl_song WHERE trackFile LIKE :path")
    Song getSong(String path);

    @Insert
    void insertSong(Song song);

    @Delete
    void deleteSong(Song song);

    @Query("Delete From tbl_song WHERE id LIKE:id")
    void deleteById(int id);

    @Query("UPDATE tbl_song SET trackFile =:location WHERE id LIKE :id")
    void updateSong(String location, int id);
}
