package com.example.ps.musicps.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.ps.musicps.R;
import com.example.ps.musicps.databinding.ActivitySplashScreenBinding;

public class SplashScreen extends AppCompatActivity {

    ActivitySplashScreenBinding binding;
    boolean shouldLaunch = true;
    boolean isTimerFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initialize();
        setup();
    }

    @Override
    protected void onStop() {
        super.onStop();
        shouldLaunch = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        shouldLaunch = true;
        if (isTimerFinished) {
            startActivity(new Intent(SplashScreen.this, ListActivity.class));
            SplashScreen.this.finish();
        }
    }

    private void setup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (shouldLaunch) {
                    startActivity(new Intent(SplashScreen.this, ListActivity.class));
                    SplashScreen.this.finish();
                }
                isTimerFinished = true;
            }
        }, 3000);
    }

    private void initialize() {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        binding.ivIconSplash.setAnimation(fadeIn);
    }
}
