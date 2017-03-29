package com.example.deepanshu.musicalstructureapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by deepanshu on 30/3/17.
 */

class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {
    private List<HashMap<String, String>> songsList;

    MusicAdapter(List<HashMap<String, String>> songsListData) {
        this.songsList = songsListData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HashMap<String, String> songsMap = songsList.get(position);
        holder.mTextView.setText(songsMap.get("songTitle"));
        holder.mTextView.append(songsMap.get("songPath"));
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        MyViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.songTitle);
        }
    }
}
