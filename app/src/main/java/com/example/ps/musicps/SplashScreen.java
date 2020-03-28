package com.example.ps.musicps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.ps.musicps.View.ListActivity;
import com.example.ps.musicps.databinding.ActivitySplashScreenBinding;

public class SplashScreen extends AppCompatActivity {

    ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initialize();
        setup();
    }

    private void setup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, ListActivity.class));

            }
        },3000);
    }

    private void initialize() {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        binding.ivIconSplash.setAnimation(fadeIn);
    }
}
