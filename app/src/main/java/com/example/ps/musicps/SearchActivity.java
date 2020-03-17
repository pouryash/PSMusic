package com.example.ps.musicps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.example.ps.musicps.Adapter.OnSongAdapter;
import com.example.ps.musicps.Adapter.SongSearchAdapter;
import com.example.ps.musicps.Di.component.DaggerSongSearchComponent;
import com.example.ps.musicps.Di.component.SongSearchComponent;
import com.example.ps.musicps.Di.module.SearchActivityModule;
import com.example.ps.musicps.Model.Song;
import com.example.ps.musicps.Model.SongInfo;
import com.example.ps.musicps.databinding.ActivitySearchBinding;
import com.example.ps.musicps.databinding.SongInfoDialogBinding;
import com.example.ps.musicps.viewmodels.SongInfoViewModel;
import com.example.ps.musicps.viewmodels.SongViewModel;
import com.google.firebase.analytics.FirebaseAnalytics;

import javax.inject.Inject;

public class SearchActivity extends AppCompatActivity implements OnSongAdapter {

    ActivitySearchBinding binding;
    FirebaseAnalytics firebaseAnalytics;
    InputMethodManager imgr;
    String searchTerm;
    SongInfoDialogBinding songInfoBinding;
    Dialog infoDialog;
    @Inject
    SongInfoViewModel songInfoViewModel;
    @Inject
    SongViewModel songViewModel;
    SongSearchComponent component;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        songInfoBinding = SongInfoDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setupViews();
    }

    private void setupViews() {
        songViewModel.getSongs();
        imgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        binding.etSearch.requestFocus();
        binding.etSearch.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_search_search_activity, null)
                , null, null, null);
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                songViewModel.getSongSearchAdapter().getFilter().filter(charSequence);
                searchTerm = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.ivBackSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Back Button Search");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                onBackPressed();
                imgr.hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus().getWindowToken(), 0);
            }
        });
    }

    private void init() {
        component = DaggerSongSearchComponent.builder().searchActivityModule(new SearchActivityModule(this)).build();
        component.inject(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        songInfoBinding.setSongInfo(songInfoViewModel);
        binding.setSongViewModel(songViewModel);

        infoDialog = new Dialog(this, R.style.DialogTheme);
        infoDialog.setTitle("Login");
        infoDialog.setCancelable(true);
        infoDialog.setContentView(songInfoBinding.getRoot());
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        infoDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams lp = infoDialog.getWindow().getAttributes();
        lp.dimAmount = 0.7f;
        lp.gravity = Gravity.BOTTOM;
        songInfoBinding.tvCancelSongInfoDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoDialog.dismiss();
            }
        });
    }

    @Override
    public void onSongClicked(Song song) {

    }

    @Override
    public void onSongRemoved(int id, int list) {

    }

    @Override
    public void onMenuInfoClicked(SongInfo songInfo) {
        songInfoViewModel.setSongInfo(songInfo);

        infoDialog.show();
    }
}
