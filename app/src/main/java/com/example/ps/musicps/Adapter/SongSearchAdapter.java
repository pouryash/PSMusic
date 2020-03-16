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
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.crashlytics.android.Crashlytics;
import com.example.ps.musicps.Commen.Commen;
import com.example.ps.musicps.Helper.SongsConfigHelper;
import com.example.ps.musicps.Model.SongInfo;
import com.example.ps.musicps.View.Dialog.CustomeAlertDialogClass;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.R;
import com.example.ps.musicps.databinding.SongRowBinding;
import com.example.ps.musicps.databinding.SongSearchRowBinding;
import com.example.ps.musicps.viewmodels.SearchSongViewModel;
import com.example.ps.musicps.viewmodels.SongViewModel;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SongSearchAdapter extends RecyclerView.Adapter<SongSearchAdapter.SongVH> implements
        Filterable {

    private List<SearchSongViewModel> songList;
    private List<SearchSongViewModel> songListFiltered = new ArrayList<>();
    private Context context;
    private onSearchAdpSong onSearchAdpSong;
    private String charString;
    private LayoutInflater layoutInflater;
    private SongsConfigHelper songsConfigHelper;

    @Inject
    public SongSearchAdapter(List<SearchSongViewModel> songList, Context context, onSearchAdpSong onSearchAdpSong, SongsConfigHelper songsConfigHelper) {
        this.songList = songList;
        this.context = context;
        this.onSearchAdpSong = onSearchAdpSong;
        this.songsConfigHelper = songsConfigHelper;
    }


    @NonNull
    @Override
    public SongVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        SongSearchRowBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.song_search_row, viewGroup, false);
        return new SongVH(binding, onSearchAdpSong);
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
                    List<SearchSongViewModel> filteredList = new ArrayList<>();
                    for ( SearchSongViewModel row : songList) {
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
                songListFiltered = (ArrayList<SearchSongViewModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class SongVH extends RecyclerView.ViewHolder {

        SongSearchRowBinding binding;
        PopupMenu popup;



        public SongVH(@NonNull SongSearchRowBinding itemView, final onSearchAdpSong onSearchAdpSong) {
            super(itemView.getRoot());
            this.binding = itemView;

            Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
            popup = new PopupMenu(wrapper, binding.ivMoreSongListAdp);
            popup.inflate(R.menu.adp_items_menu);
        }

        void bindSong(final SearchSongViewModel songViewModel, final int position) {

            binding.setSong(songViewModel);
            binding.executePendingBindings();

            //song item option menu
            binding.ivMoreSongListAdp.setOnClickListener(new View.OnClickListener() {
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

                    switch (menuItem.getItemId()) {

                        case R.id.menu_Delete:

                            CustomeAlertDialogClass customeAlertDialog = new
                                    CustomeAlertDialogClass((Activity) context,
                                    "Do you want to delete this Song?",
                                    new CustomeAlertDialogClass.onAlertDialogCliscked() {
                                        @Override
                                        public void onPosetive() {
                                            if (songsConfigHelper.deleteSong(songViewModel.getTrackFile())) {
                                                songListFiltered.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeRemoved(position, songListFiltered.size());
                                                notifyDataSetChanged();
                                                onSearchAdpSong.onSongRemoved(songViewModel.getId(), songListFiltered.size());
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


                            break;
                        case R.id.menu_Rington:

                            songsConfigHelper.setRingtone(songViewModel.getViewModelSong(), (Activity) context);

                            break;
                        case R.id.menu_Info:
                            SongInfo songInfo = new SongInfo();
                            songInfo.setSize(songsConfigHelper.getSize(songViewModel.getTrackFile()));
                            songInfo.setPath(songViewModel.getTrackFile());
                            songInfo.setAlbum(songViewModel.getAlbumName());
                            songInfo.setTitle(songViewModel.getArtistName());
                            songInfo.setDuration(songViewModel.getDuration());

                            onSearchAdpSong.onMenuInfoClicked(songInfo);

                            break;
                    }

                    return false;
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Song song = new Song();
                    song.setContentID(songListFiltered.get(position).getContentID());
                    song.setId(songListFiltered.get(position).getId());
                    song.setSongImageUri(songListFiltered.get(position).getSongImageUri());
                    song.setDuration(songListFiltered.get(position).getDuration());
                    song.setTrackFile(songListFiltered.get(position).getTrackFile());
                    song.setArtistName(songListFiltered.get(position).getArtistName());
                    song.setAlbumName(songListFiltered.get(position).getAlbumName());
                    song.setSongName(songListFiltered.get(position).getSongName());
                    onSearchAdpSong.onSongClicked(song);
                }
            });


            songViewModel.setFilterText(charString);

        }


    }

    public interface onSearchAdpSong {
        void onSongClicked(Song song);
        void onSongRemoved(int id, int list);
        void onMenuInfoClicked(SongInfo songInfo);
    }
}
