package com.example.deepanshu.musicalstructureapp;

import android.os.Environment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

class SongsHelper {
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<>();

    // Constructor
    SongsHelper() {

    }

    /**
     * Function to read all mp3 files from sdcard
     * and store the details in ArrayList
     */
    ArrayList<HashMap<String, String>> getPlayList() {
        String MEDIA_PATH = Environment.getExternalStorageDirectory().getPath() + "/Download/";
        File home = new File(MEDIA_PATH);

        if (home.listFiles(new FileExtensionFilter()).length > 0) {
            HashMap<String, String> song;
            for (File file : home.listFiles(new FileExtensionFilter())) {
                song = new HashMap<>();
                song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
                song.put("songPath", file.getPath());

                // Adding each song to SongList
                songsList.add(song);
            }
        }
        // return songs list array
        return songsList;
    }

    /**
     * Class to filter files which are having .mp3 extension
     */
    private class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
}
