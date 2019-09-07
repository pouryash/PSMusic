package com.example.ps.musicps.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.crashlytics.android.Crashlytics;
import com.example.ps.musicps.Commen.Commen;
import com.example.ps.musicps.Commen.CustomeAlertDialogClass;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongVH> {

    List<Song> songList;
    Context context;
    onSongAdapter onSongAdapter;
    boolean isRemoved;

    public SongAdapter(List<Song> songList, Context context, onSongAdapter onSongAdapter) {
        this.songList = songList;
        this.context = context;
        this.onSongAdapter = onSongAdapter;
    }

    public SongAdapter() {
    }

    @NonNull
    @Override
    public SongVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adp_songlist, viewGroup, false);
        return new SongVH(view, onSongAdapter);
    }

    @Override
    public void onBindViewHolder(@NonNull SongVH songVH, int i) {

        songVH.bindSong(songList.get(i), i);

    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    class SongVH extends RecyclerView.ViewHolder {

        ImageView songImage;
        ImageView ivMore;
        TextView songName;
        TextView artistName;
        TextView duration;
        PopupMenu popup;
        FirebaseAnalytics firebaseAnalytics;
        View infoDialogLayout;
        TextView infoTitle;
        TextView infoAlbum;
        TextView infoDuration;
        TextView infoSize;
        TextView infoPath;
        TextView infoCancel;
        Dialog infoDialog;


        public SongVH(@NonNull View itemView, final onSongAdapter onSongAdapter) {
            super(itemView);

            songImage = itemView.findViewById(R.id.iv_songImage_songListAdp);
            songName = itemView.findViewById(R.id.tv_songName_songListAdp);
            artistName = itemView.findViewById(R.id.tv_artistName_songListAdp);
            duration = itemView.findViewById(R.id.tv_songDuration_songListAdp);
            ivMore = itemView.findViewById(R.id.iv_more_songListAdp);
            Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
            popup = new PopupMenu(wrapper, ivMore);
            popup.inflate(R.menu.adp_items_menu);
            firebaseAnalytics = FirebaseAnalytics.getInstance(context);
            LayoutInflater aInflater = ((Activity) context).getLayoutInflater();
            infoDialogLayout = aInflater.inflate(R.layout.dialog_song_info, null);
            infoTitle = infoDialogLayout.findViewById(R.id.tv_title_value_SongInfoDialog);
            infoAlbum = infoDialogLayout.findViewById(R.id.tv_album_value_SongInfoDialog);
            infoDuration = infoDialogLayout.findViewById(R.id.tv_duration_value_SongInfoDialog);
            infoSize = infoDialogLayout.findViewById(R.id.tv_size_value_SongInfoDialog);
            infoPath = infoDialogLayout.findViewById(R.id.tv_path_value_SongInfoDialog);
            infoCancel = infoDialogLayout.findViewById(R.id.tv_cancel_SongInfoDialog);
            infoDialog = new Dialog(context,R.style.DialogTheme);
            infoDialog.setTitle("Login");
            infoDialog.setCancelable(true);
            infoDialog.setContentView(SongVH.this.infoDialogLayout);
            infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            infoDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams lp = infoDialog.getWindow().getAttributes();
            lp.dimAmount = 0.7f;
            lp.gravity = Gravity.BOTTOM;


        }

        void bindSong(final Song song, final int position) {

            //song item option menu
            ivMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        popup.show();
                    } catch (Exception e) {
                        Crashlytics.log("show popup menu(SongAdp)");
                    }
                }
            });
            // song item option menu click listener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Bundle bundle = new Bundle();
                    switch (menuItem.getItemId()) {

                        case R.id.menu_Delete:
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "menu item delete list");
                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                            final File fdelete = new File(song.getTrackFile());

                            if (fdelete.exists()) {

                                CustomeAlertDialogClass customeAlertDialog = new
                                        CustomeAlertDialogClass((Activity) context,
                                        "Do you want to delete this Song?",
                                        new CustomeAlertDialogClass.onAlertDialogCliscked() {
                                            @Override
                                            public void onPosetive() {
                                                if (fdelete.delete()) {

                                                    Toast.makeText(context, "song is sucsessfuly deleted", Toast.LENGTH_LONG).show();
                                                    MediaScannerConnection.scanFile(
                                                            context,
                                                            new String[]{fdelete.getAbsolutePath(), null},
                                                            null, null);
                                                    songList.remove(position);
                                                    notifyItemRemoved(position);
                                                    notifyItemRangeRemoved(position, songList.size());
                                                    notifyDataSetChanged();
                                                    isRemoved = true;

                                                } else {
                                                    context.deleteFile(fdelete.getName());
                                                    Toast.makeText(context, "Unable to delete this Song", Toast.LENGTH_LONG).show();
                                                }
                                                if (isRemoved) {
                                                    if (Commen.song.getId() == song.getId()) {
                                                        if (Commen.IS_PLAYING) {
                                                            Commen.mediaPlayer.release();
                                                            Commen.IS_PLAYING = false;
                                                        }
                                                        onSongAdapter.onSongRemoved(song.getId(), true, songList);
                                                    } else {
                                                        onSongAdapter.onSongRemoved(song.getId(), false, songList);
                                                    }
                                                    isRemoved = false;

                                                }
                                            }

                                            @Override
                                            public void onNegetive() {

                                            }
                                        });
                                WindowManager.LayoutParams lp = customeAlertDialog.getWindow().getAttributes();
                                lp.dimAmount = 0.7f;
                                lp.gravity = Gravity.BOTTOM;
                                customeAlertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                                customeAlertDialog.show();
                                customeAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                customeAlertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                                customeAlertDialog.setCanceledOnTouchOutside(false);


                            }

                            break;
                        case R.id.menu_Rington:
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "menu item rington list");
                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                            File songFile = new File(song.getTrackFile());
                            //this is for add song to contentresolver
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.MediaColumns.DATA, songFile.getPath());
                            values.put(MediaStore.MediaColumns.TITLE, song.getSongName());
                            values.put(MediaStore.MediaColumns.SIZE, songFile.getTotalSpace());
                            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
                            values.put(MediaStore.Audio.Media.ARTIST, song.getArtistName());
                            values.put(MediaStore.Audio.Media.DURATION, song.getDuration());
                            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
                            values.put(MediaStore.Audio.Media.IS_ALARM, false);
                            values.put(MediaStore.Audio.Media.IS_MUSIC, true);
                            Uri uri = MediaStore.Audio.Media.getContentUriForPath(songFile.getAbsolutePath());

                            Uri ringtoneUri;
                            if (uri.toString().contains("internal")) {
                                context.getContentResolver().delete(
                                        uri,
                                        MediaStore.MediaColumns.DATA + "=\""
                                                + songFile.getAbsolutePath() + "\"", null);
                                ringtoneUri = context.getContentResolver().insert(uri, values);
                            } else {
                                ringtoneUri = ContentUris.withAppendedId(uri, song.getContentID());
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (Settings.System.canWrite(context)) {
                                    RingtoneManager.setActualDefaultRingtoneUri(
                                            context,
                                            RingtoneManager.TYPE_RINGTONE,
                                            ringtoneUri
                                    );
                                    if (ringtoneUri != null) {
                                        Toast.makeText(context, "This song is sucsessfuly set as rington", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(context, "Something wrong try again later!", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Commen.getInstance().writeSettingEnabled((Activity) context);
                                }

                            } else {
                                RingtoneManager.setActualDefaultRingtoneUri(
                                        context,
                                        RingtoneManager.TYPE_RINGTONE,
                                        ringtoneUri
                                );
                                if (ringtoneUri != null) {
                                    Toast.makeText(context, "This song is sucsessfuly set as rington", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, "Something wrong try again later!", Toast.LENGTH_LONG).show();
                                }
                            }


//                            String[] projection = new String[]{MediaStore.Audio.Media._ID,
//                                    MediaStore.Audio.Media.DATA};
//
//                            Cursor mediaCursor = context.getContentResolver().query(uri,
//                                    projection, MediaStore.MediaColumns.DATA + "=\""
//                                            + songFile.getAbsolutePath() + "\"",
//                                    null, null);
//
//                            if (mediaCursor != null) {
//                                if (mediaCursor.getCount() >= 0 && mediaCursor.moveToFirst()) {
//                                    // Move to first song item
//
//                                    long songId = mediaCursor.getLong(mediaCursor.getColumnIndex(MediaStore.Audio.Media._ID));
//                                    String songUri = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
//                                    Uri songContentUri = MediaStore.Audio.Media.getContentUriForPath(songUri);
//                                    Uri ringtoneUri = ContentUris.withAppendedId(songContentUri, songId);
//
//
//                                }
//
//                                mediaCursor.close();
//                            }

                            break;
                        case R.id.menu_Info:


                            infoDialog.show();
                            infoTitle.setText(song.getSongName());
                            infoAlbum.setText(song.getAlbumName());
                            infoDuration.setText(song.getDuration());
                            songFile = new File(song.getTrackFile());
                            int megaB = 0;
                            int kiloB = 0;
                            if (songFile.length() > (10^4)){
                                megaB = (int)(songFile.length()/1024)/1024;
                                kiloB =(int)((songFile.length()/1024)%1024);

                            }
                            if (kiloB > 0 && megaB > 0){
                                if (kiloB > 9){
                                    infoSize.setText(String.valueOf(megaB).concat("."+Integer.toString(kiloB).substring(0, 2))+" MB");
                                }else {
                                    infoSize.setText(String.valueOf(megaB).concat("."+Integer.toString(kiloB).substring(0, 1))+" MB");
                                }
                            }else if (megaB > 0){
                                if (megaB > 9){
                                    infoSize.setText(Integer.toString(megaB).substring(0, 2).concat(" MB"));
                                }else {
                                    infoSize.setText(Integer.toString(megaB).substring(0, 1).concat(" MB"));
                                }
                            }else if (kiloB > 0){
                                if (kiloB > 9){
                                    infoSize.setText(Integer.toString(kiloB).substring(0, 2).concat(" KB"));
                                }else {
                                    infoSize.setText(Integer.toString(kiloB).substring(0, 1).concat(" KB"));
                                }
                            }
                            infoPath.setText(song.getTrackFile());
                            infoCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    infoDialog.dismiss();
                                }
                            });


                            break;
                    }

                    return false;
                }
            });


            Glide.with(context).asBitmap().load(Uri.parse(song.getSongImageUri()))
                    .apply(new RequestOptions().placeholder(R.drawable.ic_no_album))
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            songImage.setBackgroundColor(Color.parseColor("#d1d9ff"));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(songImage);
            songName.setText(song.getSongName());
            artistName.setText(song.getArtistName());
            duration.setText(song.getDuration());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSongAdapter.onSongClicked(song.getId());
                }
            });
        }

    }

    public interface onSongAdapter {
        void onSongClicked(int pos);

        void onSongRemoved(int pos, boolean isCurent, List<Song> list);
    }
}
