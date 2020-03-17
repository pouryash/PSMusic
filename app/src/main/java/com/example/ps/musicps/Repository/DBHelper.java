package com.example.ps.musicps.Repository;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.ps.musicps.Model.Song;

@Database(entities = {Song.class},
        version = 1, exportSchema = false)
abstract public class DBHelper extends RoomDatabase {

        private static DBHelper INSTANCE;

        public static DBHelper getAppDatabase(Context context) {
            if (INSTANCE == null) {
                INSTANCE =
                        Room.databaseBuilder(context.getApplicationContext(), DBHelper.class, "PSMusicDB")
                                .allowMainThreadQueries()
                                .build();
            }
            return INSTANCE;
        }

        public static void destroyInstance() {
            INSTANCE = null;
        }

        public abstract SongDao userDao();
}
