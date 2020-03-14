package com.example.ps.musicps.Helper;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.example.ps.musicps.Commen.Commen;
import com.example.ps.musicps.Commen.MyApplication;
import com.example.ps.musicps.Di.Scop.ActivityScop;
import com.example.ps.musicps.Model.SongInfo;
import com.example.ps.musicps.viewmodels.SongViewModel;

import java.io.File;

import javax.inject.Inject;

@ActivityScop
public class SongsConfigHelper {


    @Inject
    public SongsConfigHelper() {
    }

    public boolean deleteSong(String path) {
        File file = new File(path);
        if (file.delete()) {

            Toast.makeText(MyApplication.getAppContext(), "song is sucsessfuly deleted", Toast.LENGTH_LONG).show();
            MediaScannerConnection.scanFile(
                    MyApplication.getAppContext(),
                    new String[]{file.getAbsolutePath(), null},
                    null, null);

            return true;
        } else {
            MyApplication.getAppContext().deleteFile(file.getName());
            Toast.makeText(MyApplication.getAppContext(), "Unable to delete this Song", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public String getSize(String path) {

        String size = "";

        File songFile = new File(path);
        int megaB = 0;
        int kiloB = 0;
        if (songFile.length() > (10 ^ 4)) {
            megaB = (int) (songFile.length() / 1024) / 1024;
            kiloB = (int) ((songFile.length() / 1024) % 1024);

        }
        if (kiloB > 0 && megaB > 0) {
            if (kiloB > 9) {
                size =  String.valueOf(megaB).concat("." + Integer.toString(kiloB).substring(0, 2)) + " MB";
            } else {
                size =  String.valueOf(megaB).concat("." + Integer.toString(kiloB).substring(0, 1)) + " MB";
            }
        } else if (megaB > 0) {
            if (megaB > 9) {
                size =  Integer.toString(megaB).substring(0, 2).concat(" MB");
            } else {
                size =  Integer.toString(megaB).substring(0, 1).concat(" MB");
            }
        } else if (kiloB > 0) {
            if (kiloB > 9) {
                size =  Integer.toString(kiloB).substring(0, 2).concat(" KB");
            } else {
               size = Integer.toString(kiloB).substring(0, 1).concat(" KB");
            }
        }
        return size;
    }

    public void setRingtone(SongViewModel songViewModel, Activity activity){

        File songFile = new File(songViewModel.getTrackFile());
        //this is for add song to contentresolver
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, songFile.getPath());
        values.put(MediaStore.MediaColumns.TITLE, songViewModel.getSongName());
        values.put(MediaStore.MediaColumns.SIZE, songFile.getTotalSpace());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.ARTIST, songViewModel.getArtistName());
        values.put(MediaStore.Audio.Media.DURATION, songViewModel.getDuration());
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, true);
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(songFile.getAbsolutePath());

        Uri ringtoneUri;
        if (uri.toString().contains("internal")) {
            MyApplication.getAppContext().getContentResolver().delete(
                    uri,
                    MediaStore.MediaColumns.DATA + "=\""
                            + songFile.getAbsolutePath() + "\"", null);
            ringtoneUri = MyApplication.getAppContext().getContentResolver().insert(uri, values);
        } else {
            ringtoneUri = ContentUris.withAppendedId(uri, songViewModel.getContentID());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(MyApplication.getAppContext())) {
                RingtoneManager.setActualDefaultRingtoneUri(
                        MyApplication.getAppContext(),
                        RingtoneManager.TYPE_RINGTONE,
                        ringtoneUri
                );
                if (ringtoneUri != null) {
                    Toast.makeText(MyApplication.getAppContext(), "This song is sucsessfuly set as rington", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MyApplication.getAppContext(), "Something wrong try again later!", Toast.LENGTH_LONG).show();
                }
            } else {
                Commen.getInstance().writeSettingEnabled(activity);
            }

        } else {
            RingtoneManager.setActualDefaultRingtoneUri(
                    MyApplication.getAppContext(),
                    RingtoneManager.TYPE_RINGTONE,
                    ringtoneUri
            );
            if (ringtoneUri != null) {
                Toast.makeText(MyApplication.getAppContext(), "This song is sucsessfuly set as rington", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MyApplication.getAppContext(), "Something wrong try again later!", Toast.LENGTH_LONG).show();
            }
        }

    }

}
