package com.example.deepanshu.musicalstructureapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class PlayListActivity extends AppCompatActivity {
    // Songs list

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setAdapter(new MusicAdapter(new SongsHelper().getPlayList()));

        // listening to single listitem click
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // Starting new intent
                Intent in = new Intent(getApplicationContext(), MainActivity.class);
                // Sending songIndex to PlayerActivity
                in.putExtra(getString(R.string.song_index), position);
                setResult(100, in);
                // Closing PlayListView
                finish();
            }
        }));
    }
}
