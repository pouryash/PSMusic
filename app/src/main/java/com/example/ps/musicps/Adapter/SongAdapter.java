package com.example.ps.musicps.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupMenu;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.crashlytics.android.Crashlytics;
import com.example.ps.musicps.Helper.SongsConfigHelper;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.Model.SongInfo;
import com.example.ps.musicps.View.Dialog.CustomeAlertDialogClass;
import com.example.ps.musicps.R;
import com.example.ps.musicps.databinding.SongRowBinding;
import com.example.ps.musicps.viewmodels.SongViewModel;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;


public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongVH> {

    private List<SongViewModel> viewModelList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;
    private onSongAdapter onSongAdapter;
    private SongsConfigHelper songsConfigHelper;

    @Inject
    public SongAdapter(List<SongViewModel> songViewModels, Context context, onSongAdapter onSongAdapter, SongsConfigHelper songsConfigHelper) {
        this.viewModelList = songViewModels;
        this.context = context;
        this.onSongAdapter = onSongAdapter;
        this.songsConfigHelper = songsConfigHelper;
    }

    public SongAdapter() {
    }

    @NonNull
    @Override
    public SongVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(context);
        SongRowBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.song_row, viewGroup, false);
        return new SongVH(binding, onSongAdapter);
    }

    @Override
    public void onBindViewHolder(@NonNull SongVH songVH, int i) {

        songVH.bindSong(viewModelList.get(i), i);

    }

    @Override
    public int getItemCount() {
        return viewModelList.size();
    }

    class SongVH extends RecyclerView.ViewHolder {

        SongRowBinding binding;
        PopupMenu popup;
        FirebaseAnalytics firebaseAnalytics;


        public SongVH(@NonNull SongRowBinding itemView, final onSongAdapter onSongAdapter) {
            super(itemView.getRoot());
            this.binding = itemView;

            Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
            popup = new PopupMenu(wrapper, binding.ivMoreSongListAdp);
            popup.inflate(R.menu.adp_items_menu);
            firebaseAnalytics = FirebaseAnalytics.getInstance(context);

        }

        void bindSong(final SongViewModel songViewModel, final int position) {

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
                    Bundle bundle = new Bundle();
                    switch (menuItem.getItemId()) {

                        case R.id.menu_Delete:
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "menu item delete list");
                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                            CustomeAlertDialogClass customeAlertDialog = new
                                    CustomeAlertDialogClass((Activity) context,
                                    "Do you want to delete this Song?",
                                    new CustomeAlertDialogClass.onAlertDialogCliscked() {
                                        @Override
                                        public void onPosetive() {
                                            if (songsConfigHelper.deleteSong(songViewModel.getTrackFile())) {
                                                viewModelList.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeRemoved(position, viewModelList.size());
                                                notifyDataSetChanged();
                                                onSongAdapter.onSongRemoved(songViewModel.getId(), viewModelList.size());
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
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "menu item rington list");
                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                            songsConfigHelper.setRingtone(songViewModel, (Activity) context);

                            break;
                        case R.id.menu_Info:
                            SongInfo songInfo = new SongInfo();
                            songInfo.setSize(songsConfigHelper.getSize(songViewModel.getTrackFile()));
                            songInfo.setPath(songViewModel.getTrackFile());
                            songInfo.setAlbum(songViewModel.getAlbumName());
                            songInfo.setTitle(songViewModel.getArtistName());
                            songInfo.setDuration(songViewModel.getDuration());

                            onSongAdapter.onMenuInfoClicked(songInfo);

                            break;
                    }

                    return false;
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Song song = new Song();
                    song.setContentID(viewModelList.get(position).getContentID());
                    song.setId(viewModelList.get(position).getId());
                    song.setSongImageUri(viewModelList.get(position).getSongImageUri());
                    song.setDuration(viewModelList.get(position).getDuration());
                    song.setTrackFile(viewModelList.get(position).getTrackFile());
                    song.setArtistName(viewModelList.get(position).getArtistName());
                    song.setAlbumName(viewModelList.get(position).getAlbumName());
                    song.setSongName(viewModelList.get(position).getSongName());
                    onSongAdapter.onSongClicked(song);
                }
            });
        }

    }

    public interface onSongAdapter {
        void onSongClicked(Song song);

        void onSongRemoved(int id, int size);

        void onMenuInfoClicked(SongInfo songInfo);
    }
}
