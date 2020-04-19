package com.example.ps.musicps;

import android.os.Environment;

import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.viewmodels.SongViewModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.File;
import java.util.List;

public class CommenTest {

    @Mock
    List<SongViewModel> songViewModels;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(songViewModels.size()).thenReturn(2);
        for (int i = 0; i < 2 ; i++) {
            Song song = new Song();
            song.setId(i);
            Mockito.when(songViewModels.get(i)).thenReturn(new SongViewModel(song));
        }
    }

    @Test
    public void test() {
        parseFilePath("storage/s4sd5/download/ppp.mp3");
        getListItemIndex(songViewModels, 1);
    }

    private void parseFilePath(String path) {
        String root = "storage/emulated/0/";
        String[] splitPath = path.split("/");
        for (String s : splitPath) {
            File file = new File(root + s);
            if (file.exists() || s.equals("s4sd5")) {
                path = root + path.substring(path.indexOf(s));
                int a = 0;
            }
        }
    }

    private int getListItemIndex(List<SongViewModel> modelList, int id) {
        for (int i = 0; i < modelList.size(); i++) {
            if (modelList.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }
}
