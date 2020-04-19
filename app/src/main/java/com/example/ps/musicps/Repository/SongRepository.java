package com.example.ps.musicps.Repository;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.crashlytics.android.Crashlytics;
import com.example.ps.musicps.Commen.MyApplication;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.viewmodels.SongViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class SongRepository {

    private DbRepository dbRepository;

    @Inject
    public SongRepository() {
        dbRepository = ((MyApplication) MyApplication.getAppContext()).getComponent().getDbRepo();
    }

    public MutableLiveData<List<SongViewModel>> updateSongs() {

        List<SongViewModel> songList = new ArrayList<>();
        MutableLiveData<List<SongViewModel>> mutableLiveData = new MutableLiveData<>();

        ObservableOnSubscribe observableOnSubscribe = new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {

                ContentResolver cr = MyApplication.getAppContext().getContentResolver();
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
                            try {
                                int m = Integer.parseInt(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                                int seconds = (int) m / 1000;
                                int minutes = (int) seconds / 60;
                                seconds %= 60;
                                String s = String.format(Locale.ENGLISH, "%02d", minutes) +
                                        ":" + String.format(Locale.ENGLISH, "%02d", seconds);
                                song.setDuration(s);
                            } catch (Exception e) {
                                Crashlytics.log(e.getMessage() + "  song task 59");
                                song.setDuration("0");
                            }
                            try {
                                song.setContentID(Integer.parseInt(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media._ID))));
                            } catch (Exception e) {
                                Crashlytics.log(e.getMessage() + "  song task 64");
                            }

                            song.setArtistName(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                            song.setAlbumName(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                            int albumId = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                            song.setSongImageUri(ContentUris.withAppendedId(albumArtUri, cur.getLong(albumId)).toString());
                            if (!dbRepository.isSongExist(song.getTrackFile())) {
                                dbRepository.insertSong(song);
                            }
                            song.setId(dbRepository.getSongByPath(song.getTrackFile()).getId());
                            emitter.onNext(song);

                        }
                    }
                    cur.close();
                    emitter.onComplete();
                }

            }
        };

        Observable observable = Observable.create(observableOnSubscribe);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        songList.add(new SongViewModel((Song) o));
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        mutableLiveData.postValue(songList);
                    }
                });

        return mutableLiveData;
    }

    public LiveData<List<Song>> getSongs() {
        List<Song> list = new ArrayList<>();
        MutableLiveData<List<Song>> modelMutableLiveData = new MutableLiveData<>();

        dbRepository.getSongs().observeForever(songs -> {
            list.clear();
            Observable.fromArray(songs)
                    .flatMapIterable(songs1 -> songs1)
                    .map(song -> {
                        File songFile = new File(song.getTrackFile());
                            if (songFile.exists()){
                                return song;
                            }else {
                                dbRepository.deleteSong(song);
                                return new Song();
                            }

                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Song>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Song song) {
                            list.add(song);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            modelMutableLiveData.postValue(list);
                        }
                    });

        });

        return modelMutableLiveData;

    }

    public LiveData<List<Song>> getFaverateSongs() {
        List<Song> list = new ArrayList<>();
        MutableLiveData<List<Song>> modelMutableLiveData = new MutableLiveData<>();

        dbRepository.getFaverateSongs().observeForever(songs -> {
            list.clear();
            Observable.fromArray(songs)
                    .flatMapIterable(songs1 -> songs1)
                    .map(song -> {
                        File songFile = new File(song.getTrackFile());
                        if (songFile.exists()){
                            return song;
                        }else {
                            dbRepository.deleteSong(song);
                            return new Song();
                        }

                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Song>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Song song) {
                            list.add(song);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            modelMutableLiveData.postValue(list);
                        }
                    });

        });

        return modelMutableLiveData;

    }

    public int updateFaverateSong(int faverate, int id){
       return dbRepository.updateFaverateSong(faverate, id);
    }

    public void deleteSong(int id) {
        dbRepository.deleteById(id);
    }

    public Song getSong(String id) {
        return dbRepository.getSong(id);
    }

    public Song getSongByPath(String path) {
        return dbRepository.getSongByPath(path);
    }

    public Song getMinSong(String id) {
        return dbRepository.getMinSong(id);
    }

    public Song getMaxSong(String id) {
        return dbRepository.getMaxSong(id);
    }

    public boolean isSongExist(String path) {
        return dbRepository.isSongExist(path);
    }

    public int getSize(){
        return dbRepository.getSize();
    }
}
