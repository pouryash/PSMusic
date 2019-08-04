package com.example.ps.musicps;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.ps.musicps.Adapter.SongSearchAdapter;

public class SearchActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText editText;
    SongSearchAdapter adapter;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);





        initViews();
        setupViews();

    }

    private void setupViews() {

        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new SongSearchAdapter(SongListActivity.songList, this, new SongSearchAdapter.onSongClicked() {
            @Override
            public void onSongClicked(int pos) {

            }
        });
        recyclerView.setAdapter(adapter);
        editText.setCompoundDrawablesWithIntrinsicBounds( ResourcesCompat.getDrawable(getResources(),R.drawable.ic_search_search_activity,null)
                ,null,null,null);
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
                SearchActivity.super.onBackPressed();
            }
        });
    }

    private void initViews() {

        recyclerView = findViewById(R.id.rl_Search);
        editText = findViewById(R.id.et_Search);
        back = findViewById(R.id.iv_back_Search);

    }
}
