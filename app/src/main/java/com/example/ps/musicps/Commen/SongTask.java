package com.example.ps.musicps.Commen;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.example.ps.musicps.Model.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.sql.StatementEvent;

public class SongTask extends AsyncTask<Void, Void, Void> {


    private Context context;
    private onSongListFinished onSongListFinished;
    private ArrayList<Song> songList;

    public SongTask(Context context, SongTask.onSongListFinished onSongListFinished) {
        this.context = context;
        this.onSongListFinished = onSongListFinished;

    }

    @Override
    protected Void doInBackground(Void... voids) {


        ContentResolver cr = context.getContentResolver();
        songList = new ArrayList<>();
        final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cur = cr.query(uri, null, selection, null, sortOrder);
        int count = 0;

        if (cur != null) {
            count = cur.getCount();

            if (count > 0) {
                while (cur.moveToNext()) {
                    Song song = new Song();
                    song.setTrackFile(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)));
                    song.setSongName(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                    int m = Integer.parseInt(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                    song.setContentID(Integer.parseInt(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media._ID))));
//                        int hours = (int) ((m / (1000 * 60 * 60)) % 24);
//                        int minutes = (int) (m % (1000 * 60 * 60) / (1000 * 60));
//                        int seconds = (int) ((m % (1000 * 60 * 60)) % (1000 * 60) / 1000);
                    int seconds = (int) m / 1000;
                    int minutes = (int) seconds / 60;
                    seconds %= 60;
                    String s = String.format(Locale.ENGLISH, "%02d", minutes) +
                            ":" + String.format(Locale.ENGLISH, "%02d", seconds);
                    song.setDuration(s);
                    song.setArtistName(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                    song.setAlbumName(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                    int albumId = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                    song.setSongImageUri(ContentUris.withAppendedId(albumArtUri, cur.getLong(albumId)).toString());
                    song.setId(cur.getPosition());
                    songList.add(song);

                }
            }
            cur.close();
        }

//       getLocalSongList(Environment.getExternalStorageDirectory().getPath());
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        onSongListFinished.onSongListFinished(songList);

    }

    public List<Song> getLocalSongList(String rootPath) {

        List<Song> list = new ArrayList<>();
        try {
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles(); //here you will get NPE if directory doesn't contains  any file,handle it like this.
            for (File file : files) {
                if (file.isDirectory()) {
                    if (getLocalSongList(file.getAbsolutePath()) != null) {
                        list.addAll(getLocalSongList(file.getAbsolutePath()));
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(".mp3")) {

                    MediaMetadataRetriever metaRetriver;
                    metaRetriver = new MediaMetadataRetriever();
                    metaRetriver.setDataSource(file.getAbsolutePath());
                    byte[] art;
                    art = metaRetriver.getEmbeddedPicture();

                    Song song = new Song();


                    song.setTrackFile(file.getPath());
                    song.setSongName(file.getName());
                    int m = Integer.parseInt(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                    int seconds = (int) m / 1000;
                    int minutes = (int) seconds / 60;
                    seconds %= 60;
                    String s = String.format(Locale.ENGLISH, "%02d", minutes) +
                            ":" + String.format(Locale.ENGLISH, "%02d", seconds);
                    song.setDuration(s);
                    song.setArtistName(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                    song.setAlbumName(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
                    song.setId(list.size());


                    list.add(song);
                }
            }
            return list;
        } catch (Exception e) {
            Log.e("1", "getLocalSongList: " + e);
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    public interface onSongListFinished {
        void onSongListFinished(ArrayList<Song> songs);
    }
}
