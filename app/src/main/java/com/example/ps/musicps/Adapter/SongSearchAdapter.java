package com.example.ps.musicps.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.example.ps.musicps.Commen.Commen;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.PlaySongActivity;
import com.example.ps.musicps.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SongSearchAdapter extends RecyclerView.Adapter<SongSearchAdapter.SongVH> implements
        Filterable {

    List<Song> songList;
    List<Song> songListFiltered = new ArrayList<>();
    Context context;
    onSearchAdpSong onSearchAdpSong;
    String charString;
    boolean isRemoved;

    public SongSearchAdapter(List<Song> songList, Context context, onSearchAdpSong onSearchAdpSong) {
        this.songList = songList;
        this.context = context;
        this.onSearchAdpSong = onSearchAdpSong;
    }


    @NonNull
    @Override
    public SongVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adp_songlist, viewGroup, false);
        return new SongVH(view, onSearchAdpSong);
    }

    @Override
    public void onBindViewHolder(@NonNull SongVH songVH, int i) {

        songVH.bindSong(songListFiltered.get(i),i);

    }

    @Override
    public int getItemCount() {
        return songListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                charString = charSequence.toString();
                if (charString.isEmpty()) {
                    songListFiltered = new ArrayList<>();
                } else {
                    List<Song> filteredList = new ArrayList<>();
                    for (Song row : songList) {
//TODO textView marque
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getSongName().toLowerCase().contains(charString.toLowerCase())
                                || row.getArtistName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    songListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = songListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                songListFiltered = (ArrayList<Song>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class SongVH extends RecyclerView.ViewHolder {

        ImageView songImage;
        ImageView ivMore;
        TextView songName;
        TextView artistName;
        TextView duration;
        PopupMenu popup;


        public SongVH(@NonNull View itemView, final onSearchAdpSong onSearchAdpSong) {
            super(itemView);

            songImage = itemView.findViewById(R.id.iv_songImage_songListAdp);
            songName = itemView.findViewById(R.id.tv_songName_songListAdp);
            artistName = itemView.findViewById(R.id.tv_artistName_songListAdp);
            duration = itemView.findViewById(R.id.tv_songDuration_songListAdp);
            ivMore = itemView.findViewById(R.id.iv_more_songListAdp);
            Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
            popup = new PopupMenu(wrapper, ivMore);
            popup.inflate(R.menu.adp_items_menu);
        }

        void bindSong(final Song song, final int position) {


            ivMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    popup.show();
                }
            });
            // song item option menu click listener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {

                        case R.id.menu_Delete:
                            final File fdelete = new File(song.getTrackFile());

                            if (fdelete.exists()) {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(context);

                                dialog.setTitle("Delete Song")
                                        .setMessage("Do you want to delete this Song?")
                                        .setCancelable(false)
                                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                if (fdelete.delete()) {

                                                    Toast.makeText(context, "song is sucsessfuly deleted", Toast.LENGTH_LONG).show();
                                                    MediaScannerConnection.scanFile(
                                                            context,
                                                            new String[]{fdelete.getAbsolutePath(), null},
                                                            null, null);
                                                    songList.remove(position);
                                                    songListFiltered.remove(position);
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
                                                        onSearchAdpSong.onSongRemoved(song.getId(),true ,songList);
                                                    } else {
                                                        onSearchAdpSong.onSongRemoved(song.getId(),false , songList);
                                                    }
                                                    isRemoved = false;

                                                }
                                            }
                                        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                            }

                            break;
                        case R.id.menu_Rington:
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
                            if (uri.toString().contains("internal")){
                                context.getContentResolver().delete(
                                        uri,
                                        MediaStore.MediaColumns.DATA + "=\""
                                                + songFile.getAbsolutePath() + "\"", null);
                                ringtoneUri = context.getContentResolver().insert(uri,values);
                            }else {
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
            String songNameString = song.getSongName();
            String artistNameString = song.getArtistName();

            String s = artistNameString.toLowerCase();
            int indexSongName = songNameString.toLowerCase().indexOf(charString.toLowerCase());
            int indexArtistName = artistNameString.toLowerCase().indexOf(charString.toLowerCase());

            ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(229, 23, 23));
            if (indexSongName > -1) {
                SpannableStringBuilder sb = new SpannableStringBuilder(songNameString);

                sb.setSpan(fcs, indexSongName, indexSongName + charString.length(), 0);
                songName.setText(sb);
                if (indexArtistName < 0) {
                    artistName.setText(song.getArtistName());
                }
            }
            if (indexArtistName > -1) {
                if (indexSongName < 0) {
                    songName.setText(song.getSongName());
                }
                SpannableStringBuilder sb = new SpannableStringBuilder(artistNameString);

                sb.setSpan(fcs, indexArtistName, indexArtistName + charString.length(), 0);
                artistName.setText(sb);
            }

            duration.setText(song.getDuration());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSearchAdpSong.onSongClicked(song.getId());
                }
            });
        }

    }

    public interface onSearchAdpSong {
        void onSongClicked(int pos);
        void onSongRemoved(int pos, boolean isCurentSong , List<Song> list);
    }
}
