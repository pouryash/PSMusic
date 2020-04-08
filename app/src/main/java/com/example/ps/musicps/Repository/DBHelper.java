package com.example.ps.musicps.Repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.ps.musicps.Model.Song;

@Database(entities = {Song.class},
        version = 2, exportSchema = false)
abstract public class DBHelper extends RoomDatabase {

        private static DBHelper INSTANCE;

        public static DBHelper getAppDatabase(Context context) {
            if (INSTANCE == null) {
                INSTANCE =
                        Room.databaseBuilder(context.getApplicationContext(), DBHelper.class, "PSMusicDB")
                                .allowMainThreadQueries()
                                .addMigrations(MIGRATION_1_2)
                                .build();
            }
            return INSTANCE;
        }

        private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {

                database.execSQL("Alter Table 'tbl_song' ADD Column 'isFaverate' INTEGER Not Null Default 0 ");

            }
        };

        public static void destroyInstance() {
            INSTANCE = null;
        }

        public abstract SongDao userDao();
}
