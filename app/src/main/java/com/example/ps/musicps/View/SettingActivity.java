package com.example.ps.musicps.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;

import com.example.ps.musicps.Commen.Commen;
import com.example.ps.musicps.Commen.MyApplication;
import com.example.ps.musicps.Helper.DialogHelper;
import com.example.ps.musicps.R;
import com.example.ps.musicps.View.Dialog.CustomeAlertDialogClass;
import com.example.ps.musicps.databinding.ActivitySettingBinding;

public class SettingActivity extends AppCompatActivity {

    ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUp();
    }

    @Override
    protected void onStart() {
        super.onStart();

        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.colorWhite));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }
                }
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.colorBlack));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getWindow().getDecorView().setSystemUiVisibility(0);
                    }
                }
        }
    }

    private void setUp() {

        binding.arowBackSetting.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.linClearCacheSetting.setOnClickListener(view -> {
            DialogHelper.alertDialog(SettingActivity.this, 0, "are you sure to clear cache?", new CustomeAlertDialogClass.onAlertDialogCliscked() {
                @Override
                public void onPosetive() {
                    Commen.deleteCache(SettingActivity.this);
                }

                @Override
                public void onNegetive() {

                }
            });
        });
        binding.linDarkModeSetting.setOnClickListener(view -> {
            DarkModeBottomFragment darkModeBottomFragment = new DarkModeBottomFragment();
            darkModeBottomFragment.show(getSupportFragmentManager(), "");
        });
    }
}
