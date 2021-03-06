package com.example.ps.musicps.Repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ps.musicps.Model.Song;

import java.util.List;

@Dao
public interface SongDao {

    @Query("SELECT * FROM tbl_song")
    LiveData<List<Song>> getSongs();

    @Query("SELECT * FROM tbl_song WHERE trackFile LIKE :path")
    Song getSong(String path);

    @Query("SELECT * FROM tbl_song WHERE id = (select max(id) from tbl_song where id < :id)")
    Song getSongByIdMin(int id);

    @Query("SELECT * FROM tbl_song WHERE id = (select min(id) from tbl_song where id > :id)")
    Song getSongByIdMax(int id);

    @Query("SELECT * FROM tbl_song WHERE id = :id")
    Song getSongById(int id);

    @Query("SELECT COUNT(*) FROM tbl_song")
    int getSize();

    @Query("SELECT * FROM tbl_song WHERE trackFile = :path")
    Song getSongByPath(String path);

    @Query("SELECT * FROM tbl_song WHERE isFaverate Like 1")
    LiveData<List<Song>> getFaverateSong();

    @Insert
    void insertSong(Song song);

    @Delete
    void deleteSong(Song song);

    @Query("Delete From tbl_song WHERE id LIKE:id")
    void deleteById(int id);

    @Query("UPDATE tbl_song SET trackFile =:location WHERE id LIKE :id")
    void updateSongLocation(String location, int id);

    @Query("UPDATE tbl_song SET isFaverate =:faverate WHERE id LIKE :id")
    int updateFaverateSong(int faverate, int id);

    @Update
    void updateFaverate(Song... song);
}
