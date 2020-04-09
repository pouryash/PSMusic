package com.example.ps.musicps.Commen;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.viewmodels.SongViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Commen {

    private static final int WRITE_SETTINGS_REQUEST = 10;
    private static Commen instance;

    private Commen() {
    }


    public static Commen getInstance() {
        return instance;
    }


    public static Bitmap getBitmapFromVectorDrawable(Context context, Drawable drawableId) {
        Drawable drawable = drawableId;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Uri getImageByteUri(byte[] bytes, int length, Context context) {
        Uri albumUri = Uri.EMPTY;
        if (bytes != null) {

            Bitmap songBitmapAlbum = BitmapFactory
                    .decodeByteArray(bytes, 0, length);

            String paths = MediaStore.Images.Media.insertImage
                    (context.getContentResolver(), songBitmapAlbum, "Title", null);
            if (paths != null) {
                albumUri = Uri.parse(paths);
            }
        }
        return albumUri;
    }

    public static int getListItemIndex(List<SongViewModel> modelList, int id) {
        for (int i = 0; i < modelList.size(); i++) {
            if (modelList.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;

        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndex(proj[0]);
            String path = cursor.getString(column_index);
            return path;
        } catch (Exception e) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static Bitmap decodeUriToBitmap(Context mContext, Uri sendUri) {
        Bitmap getBitmap = null;
        try {
            InputStream image_stream;
            try {
                image_stream = mContext.getContentResolver().openInputStream(sendUri);
                getBitmap = BitmapFactory.decodeStream(image_stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getBitmap;
    }

    static {
        instance = new Commen();
    }

    public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String changeDurationFormat(long duration) {
        int seconds = (int) (duration / 1000);
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format(Locale.ENGLISH, "%02d", minutes) + ":" + String.format(Locale.ENGLISH, "%02d", seconds);
    }

    public static List<Song> notifyListchanged(int pos, List<Song> songs) {
        List<Song> songList = new ArrayList<>();

        for (Song song : songs) {
            if (song.getId() >= pos + 1) {
                int id = song.getId();
                song.setId(id - 1);
                songList.add(song);
            } else {
                songList.add(song);
            }
        }
        return songList;
    }

    public void writeSettingEnabled(Activity context) {

        if (!Settings.System.canWrite(context)) {
            writeSettingAlertMessage(context);

        }
    }

    public void writeSettingAlertMessage(final Activity context) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("We need write Setting permision, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        context.startActivityForResult(intent, WRITE_SETTINGS_REQUEST);
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            }
        });
        alert.show();
    }

}
