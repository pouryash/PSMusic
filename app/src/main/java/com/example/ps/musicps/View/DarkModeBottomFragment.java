package com.example.ps.musicps.View;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.ps.musicps.Commen.MyApplication;
import com.example.ps.musicps.Commen.SongSharedPrefrenceManager;
import com.example.ps.musicps.databinding.DarkModeFragmentBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class DarkModeBottomFragment extends BottomSheetDialogFragment {

    DarkModeFragmentBinding binding;
    private View view;
    SongSharedPrefrenceManager sharedPrefrenceManager;

    public DarkModeBottomFragment() {

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        binding = DarkModeFragmentBinding.inflate(getActivity().getLayoutInflater());
        view = binding.getRoot();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));



        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        setupView();

        if (behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    switch (newState) {
                        case BottomSheetBehavior.STATE_HIDDEN: {
                            dismiss();
                            break;
                        }
                        case BottomSheetBehavior.STATE_DRAGGING:
                        case BottomSheetBehavior.STATE_SETTLING:
                        case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        case BottomSheetBehavior.STATE_EXPANDED:
                        case BottomSheetBehavior.STATE_COLLAPSED:
                        default:
                            break;
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        }
    }

    private void setCurentMode(){
        switch (AppCompatDelegate.getDefaultNightMode()){
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                binding.tickDefaultSetting.setVisibility(View.VISIBLE);
                binding.tickOnSetting.setVisibility(View.INVISIBLE);
                binding.tickOffSetting.setVisibility(View.INVISIBLE);
                sharedPrefrenceManager.setNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                binding.tickOnSetting.setVisibility(View.VISIBLE);
                binding.tickDefaultSetting.setVisibility(View.INVISIBLE);
                binding.tickOffSetting.setVisibility(View.INVISIBLE);
                sharedPrefrenceManager.setNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                binding.tickOffSetting.setVisibility(View.VISIBLE);
                binding.tickOnSetting.setVisibility(View.INVISIBLE);
                binding.tickDefaultSetting.setVisibility(View.INVISIBLE);
                sharedPrefrenceManager.setNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
        }
    }

    private void setupView() {
        sharedPrefrenceManager = ((MyApplication)getActivity().getApplication()).getComponent().getSharedPrefrence();

        setCurentMode();

        binding.frDarkModeDefault.setOnClickListener(view1 -> {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            setCurentMode();
            dismiss();
        });
        binding.frDarkModeOn.setOnClickListener(view1 -> {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
            setCurentMode();
            dismiss();
        });
        binding.frDarkModeOff.setOnClickListener(view1 -> {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
            setCurentMode();
            dismiss();
        });

    }

}
