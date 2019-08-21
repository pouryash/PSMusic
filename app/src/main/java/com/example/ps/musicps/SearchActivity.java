package com.example.ps.musicps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.ps.musicps.Adapter.SongSearchAdapter;
import com.example.ps.musicps.Commen.Commen;
import com.example.ps.musicps.Model.Song;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    public static onSearchedItemRemoved onSearchedItemRemoved;
    RecyclerView recyclerView;
    EditText editText;
    SongSearchAdapter adapter;
    ImageView back;
    InputMethodManager imgr;
    List<Song> songList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);


        initViews();
        setupViews();

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SearchActivity.this, SongListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void setupViews() {
        songList = SongListActivity.songList;
        imgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new SongSearchAdapter(songList, this, new SongSearchAdapter.onSearchAdpSong() {
            @Override
            public void onSongClicked(int pos) {
                imgr.hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus().getWindowToken(), 0);
                Intent intent = new Intent(SearchActivity.this, PlaySongActivity.class);
                intent.putExtra("position", pos);
                SearchActivity.this.startActivity(intent);
            }

            @Override
            public void onSongRemoved(int pos,boolean isCurentSong ,List<Song> list) {
                songList = list;
                songList = Commen.notifyListchanged(pos,songList);
                onSearchedItemRemoved.onRemoved(pos,isCurentSong);

            }
        });
        recyclerView.setAdapter(adapter);
        editText.requestFocus();
        editText.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_search_search_activity, null)
                , null, null, null);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, SongListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }

    private void initViews() {

        recyclerView = findViewById(R.id.rl_Search);
        editText = findViewById(R.id.et_Search);
        back = findViewById(R.id.iv_back_Search);

    }

    @Override
    protected void onDestroy() {
        Commen.mediaPlayer.release();
        super.onDestroy();
    }


    public interface onSearchedItemRemoved{
        void onRemoved(int position , boolean isCurentSong);
    }

}
