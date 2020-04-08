package com.example.ps.musicps.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.ps.musicps.Commen.MyApplication;
import com.example.ps.musicps.Commen.SongSharedPrefrenceManager;
import com.example.ps.musicps.Di.component.DaggerMainActivityComponent;
import com.example.ps.musicps.Di.component.DaggerMusicServiceComponent;
import com.example.ps.musicps.Di.component.DaggerSongListComponent;
import com.example.ps.musicps.Di.component.MainActivityComponent;
import com.example.ps.musicps.Di.module.ListActivityModule;
import com.example.ps.musicps.Di.module.MainActivityModule;
import com.example.ps.musicps.Helper.MusiPlayerHelper;
import com.example.ps.musicps.Helper.ServiceConnectionBinder;
import com.example.ps.musicps.R;
import com.example.ps.musicps.Service.MusicService;
import com.example.ps.musicps.databinding.ActivityMainBinding;
import com.example.ps.musicps.viewmodels.SongViewModel;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    MusiPlayerHelper musiPlayerHelper;
    SongSharedPrefrenceManager sharedPrefrenceManager;
    MainActivityComponent component;
    @Inject
    SongViewModel songViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        component = DaggerMainActivityComponent
                .builder()
                .mainActivityModule( new MainActivityModule(this))
                .build();

        init();
        setUp();
    }

    private void init() {
        sharedPrefrenceManager = ((MyApplication) getApplicationContext()).getComponent().getSharedPrefrence();
        musiPlayerHelper = ((MyApplication) getApplicationContext()).getComponent().getMusicPlayerHelper();
    }

    private void setUp() {

        setUpMainImage(sharedPrefrenceManager.getSong().getSongImageUri());

        binding.cvAllSongMain.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            intent.putExtra("isListClicked", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        binding.cvFaverateMain.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            intent.putExtra("isFaverateClicked", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

    }

    void setUpMainImage(String uri) {
        if (uri == null)
            uri = "";

        Glide.with(binding.ivSongMain.getContext()).asBitmap().load(Uri.parse(uri))
                .apply(new RequestOptions().placeholder(R.drawable.ic_no_album_128))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        binding.ivSongMain.setPadding(200, 256, 200, 256);
                        binding.ivSongMain.setBackgroundColor(Color.parseColor("#d1d9ff"));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        binding.ivSongMain.setPadding(0, 0, 0, 0);
                        return false;
                    }
                })
                .into(binding.ivSongMain);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setUpMainImage(sharedPrefrenceManager.getSong().getSongImageUri());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorWhite));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
