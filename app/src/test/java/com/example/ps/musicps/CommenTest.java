package com.example.ps.musicps;

import android.os.Environment;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;

public class CommenTest {


    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void test(){
        parseFilePath("storage/s4sd5/download/ppp.mp3");
    }

    private void parseFilePath(String path){
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
}
