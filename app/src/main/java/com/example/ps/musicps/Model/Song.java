package com.example.ps.musicps.Model;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by poorya on 8/8/2018.
 */

@Entity(tableName = "tbl_song")
public class Song implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "songName")
    private String songName;
    @ColumnInfo(name = "artistName")
    private String artistName;
    @ColumnInfo(name = "trackFile")
    private String trackFile;
    @ColumnInfo(name = "albumName")
    private String albumName;
    @ColumnInfo(name = "duration")
    private String duration;
    @ColumnInfo(name = "songImageUri")
    private String songImageUri;
    @ColumnInfo(name = "ContentId")
    private int ContentID;
    @ColumnInfo(name = "isFaverate")
    private int isFaverate;

    public int getContentID() {
        return ContentID;
    }

    public void setContentID(int contentID) {
        ContentID = contentID;
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public int getIsFaverate() {
        return isFaverate;
    }

    public void setIsFaverate(int isFaverate) {
        this.isFaverate = isFaverate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSongImageUri() {
        return songImageUri;
    }

    public void setSongImageUri(String songImageUri) {
        this.songImageUri = songImageUri;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTrackFile() {
        return trackFile;
    }

    public void setTrackFile(String trackFile) {
        this.trackFile = trackFile;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.songName);
        dest.writeString(this.artistName);
        dest.writeString(this.trackFile);
        dest.writeString(this.albumName);
        dest.writeString(this.duration);
        dest.writeString(this.songImageUri);
    }

    public Song() {
    }

    protected Song(Parcel in) {
        this.songName = in.readString();
        this.songImageUri = in.readString();
        this.artistName = in.readString();
        this.trackFile = in.readString();
        this.albumName = in.readString();
        this.duration = in.readString();
    }
}
