package com.example.ps.musicps.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.R;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongVH> {

    List<Song> songList;
    Context context;
    onSongClicked onSongClicked;

    public SongAdapter(List<Song> songList, Context context, onSongClicked onSongClicked) {
        this.songList = songList;
        this.context = context;
        this.onSongClicked = onSongClicked;
    }

    public SongAdapter() {
    }

    @NonNull
    @Override
    public SongVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adp_songlist,viewGroup,false);
        return new SongVH(view, onSongClicked);
    }

    @Override
    public void onBindViewHolder(@NonNull SongVH songVH, int i) {

        songVH.bindSong(songList.get(i));

    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    class SongVH extends RecyclerView.ViewHolder{

        ImageView songImage;
        TextView songName;
        TextView artistName;
        TextView duration;


        public SongVH(@NonNull View itemView , final onSongClicked onSongClicked) {
            super(itemView);

            songImage = itemView.findViewById(R.id.iv_songImage_songListAdp);
            songName = itemView.findViewById(R.id.tv_songName_songListAdp);
            artistName = itemView.findViewById(R.id.tv_artistName_songListAdp);
            duration = itemView.findViewById(R.id.tv_songDuration_songListAdp);
        }

        void bindSong(final Song song){

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
            songName.setText(song.getSongName());
            artistName.setText(song.getArtistName());
            duration.setText(song.getDuration());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSongClicked.onSongClicked(song.getId());
                }
            });
        }

    }
    public interface onSongClicked{
        void onSongClicked(int pos);
    }
}
