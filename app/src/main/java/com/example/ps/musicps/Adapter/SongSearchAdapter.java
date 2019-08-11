package com.example.ps.musicps.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.R;

import java.util.ArrayList;
import java.util.List;

public class SongSearchAdapter extends RecyclerView.Adapter<SongSearchAdapter.SongVH> implements
        Filterable {

    List<Song> songList;
    List<Song> songListFiltered = new ArrayList<>();
    Context context;
    onSongClicked onSongClicked;
    String charString;

    public SongSearchAdapter(List<Song> songList, Context context, onSongClicked onSongClicked) {
        this.songList = songList;
        this.context = context;
        this.onSongClicked = onSongClicked;
    }


    @NonNull
    @Override
    public SongVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adp_songlist, viewGroup, false);
        return new SongVH(view, onSongClicked);
    }

    @Override
    public void onBindViewHolder(@NonNull SongVH songVH, int i) {

        songVH.bindSong(songListFiltered.get(i));

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
        TextView songName;
        TextView artistName;
        TextView duration;


        public SongVH(@NonNull View itemView, final onSongClicked onSongClicked) {
            super(itemView);

            songImage = itemView.findViewById(R.id.iv_songImage_songListAdp);
            songName = itemView.findViewById(R.id.tv_songName_songListAdp);
            artistName = itemView.findViewById(R.id.tv_artistName_songListAdp);
            duration = itemView.findViewById(R.id.tv_songDuration_songListAdp);
        }

        void bindSong(final Song song) {

            //TODO set placeholder by requestoption
            Glide.with(context).asBitmap().load(Uri.parse(song.getSongImageUri()))
                    .apply(new RequestOptions().placeholder(R.drawable.ic_no_album).fitCenter())
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
                if (indexArtistName < 0){
                    artistName.setText(song.getArtistName());
                }
            }
            if (indexArtistName > -1) {
                if (indexSongName < 0){
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
                    onSongClicked.onSongClicked(song.getId());
                }
            });
        }

    }

    public interface onSongClicked {
        void onSongClicked(int pos);
    }
}
