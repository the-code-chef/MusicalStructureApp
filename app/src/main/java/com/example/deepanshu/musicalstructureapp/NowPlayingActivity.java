package com.example.deepanshu.musicalstructureapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class NowPlayingActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener,
        SeekBar.OnSeekBarChangeListener {

    private ImageButton btnPlay;
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private SeekBar songProgressBar;
    private TextView songTitleLabel;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    // Media Player
    private MediaPlayer mp;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    private Utilities utils;
    private static final int SEEK_FORWARD = 5000; // 5000 milliseconds
    private static final int SEEK_BACKWARD = 5000; // 5000 milliseconds
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<>();

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            // Displaying Total Duration time
            songTotalDurationLabel.setText(
                    String.valueOf(utils.milliSecondsToTimer(totalDuration))
            );
            // Displaying time completed playing
            songCurrentDurationLabel.setText(
                    String.valueOf(utils.milliSecondsToTimer(currentDuration))
            );

            // Updating progress bar
            int progress = utils.getProgressPercentage(currentDuration, totalDuration);
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        // All player buttons
        btnPlay = (ImageButton) findViewById(R.id.play_ibtn);
        Button btnHome = (Button) findViewById(R.id.home);
        ImageButton btnForward = (ImageButton) findViewById(R.id.forward_ibtn);
        ImageButton btnBackward = (ImageButton) findViewById(R.id.backward_ibtn);
        ImageButton btnNext = (ImageButton) findViewById(R.id.next_ibtn);
        ImageButton btnPrevious = (ImageButton) findViewById(R.id.previous_ibtn);
        ImageButton btnPlaylist = (ImageButton) findViewById(R.id.playlist_ibtn);
        btnRepeat = (ImageButton) findViewById(R.id.repeat_ibtn);
        btnShuffle = (ImageButton) findViewById(R.id.shuffle_ibtn);
        songProgressBar = (SeekBar) findViewById(R.id.progressbar_sb);
        songTitleLabel = (TextView) findViewById(R.id.song_title_tv);
        songCurrentDurationLabel = (TextView) findViewById(R.id.current_duration_tv);
        songTotalDurationLabel = (TextView) findViewById(R.id.total_duration_tv);

        // Mediaplayer
        mp = new MediaPlayer();
        SongsHelper songsHelper = new SongsHelper();
        utils = new Utilities();

        // Listeners
        songProgressBar.setOnSeekBarChangeListener(this); // Important
        mp.setOnCompletionListener(this); // Important

        // Getting all songs list
        songsList = songsHelper.getPlayList();

        /*
          Play button click event
          plays a song and changes button to pause image
          pauses a song and changes button to play image
          */
        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (mp.isPlaying()) {
                    if (mp != null) {
                        mp.pause();
                        // Changing button image to play button
                        btnPlay.setImageResource(R.drawable.btn_play);
                    }
                } else {
                    // Resume song
                    if (mp != null) {
                        mp.start();
                        // Changing button image to pause button
                        btnPlay.setImageResource(R.drawable.btn_pause);
                    }
                }

            }
        });

        /*
          Forward button click event
          Forwards song specified seconds
          */
        btnForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if (currentPosition + SEEK_FORWARD <= mp.getDuration()) {
                    // forward song
                    mp.seekTo(currentPosition + SEEK_FORWARD);
                } else {
                    // forward to end position
                    mp.seekTo(mp.getDuration());
                }
            }
        });

        /*
          Backward button click event
          Backward song to specified seconds
          */
        btnBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if (currentPosition - SEEK_BACKWARD >= 0) {
                    // forward song
                    mp.seekTo(currentPosition - SEEK_BACKWARD);
                } else {
                    // backward to starting position
                    mp.seekTo(0);
                }

            }
        });

        /*
          Next button click event
          Plays next song by taking currentSongIndex + 1
          */
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check if next song is there or not
                if (currentSongIndex < (songsList.size() - 1)) {
                    playSong(currentSongIndex + 1);
                    currentSongIndex = currentSongIndex + 1;
                } else {
                    // play first song
                    playSong(0);
                    currentSongIndex = 0;
                }

            }
        });

        /*
          Back button click event
          Plays previous song by currentSongIndex - 1
          */
        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (currentSongIndex > 0) {
                    playSong(currentSongIndex - 1);
                    currentSongIndex = currentSongIndex - 1;
                } else {
                    // play last song
                    playSong(songsList.size() - 1);
                    currentSongIndex = songsList.size() - 1;
                }

            }
        });

        /*
          Button Click event for Repeat button
          Enables repeat flag to true
          */
        btnRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isRepeat) {
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT)
                            .show();
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                } else {
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT)
                            .show();
                    // make shuffle to false
                    isShuffle = false;
                    btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }
            }
        });

        /*
          Button Click event for Shuffle button
          Enables shuffle flag to true
          */
        btnShuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isShuffle) {
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), R.string.shuffle_off, Toast.LENGTH_SHORT)
                            .show();
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                } else {
                    // make repeat to true
                    isShuffle = true;
                    Toast.makeText(getApplicationContext(), R.string.shuffle_on, Toast.LENGTH_SHORT)
                            .show();
                    // make shuffle to false
                    isRepeat = false;
                    btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                }
            }
        });

        /*
          Button Click event for Play list click event
          Launches list activity which displays list of songs
          */
        btnPlaylist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), PlayListActivity.class);
                startActivityForResult(i, 100);
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    /**
     * Receiving song index from playlist view
     * and play the song
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            currentSongIndex = data.getExtras().getInt(getString(R.string.song_index));
            // play selected song
            playSong(currentSongIndex);
        }

    }

    /**
     * Function to play a song
     *
     * @param songIndex - index of song
     */
    public void playSong(int songIndex) {

        if (songsList.size() > 0) {
            // Play song
            try {
                mp.reset();
                mp.setDataSource(songsList.get(songIndex).get("songPath"));
                mp.prepare();
                mp.start();
                // Displaying Song title
                String songTitle = songsList.get(songIndex).get("songTitle");
                songTitleLabel.setText(songTitle);

                // Changing Button Image to pause image
                btnPlay.setImageResource(R.drawable.btn_pause);

                // set Progress bar values
                songProgressBar.setProgress(0);
                songProgressBar.setMax(100);

                // Updating progress bar
                updateProgressBar();
            } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(NowPlayingActivity.this, getString(R.string.no_songs), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Update timer on seekbar
     */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     *
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    /**
     * On Song Playing completed
     * if repeat is ON play same song again
     * if shuffle is ON play random song
     */
    @Override
    public void onCompletion(MediaPlayer arg0) {

        // check for repeat is ON or OFF
        if (isRepeat) {
            // repeat is on play same song again
            playSong(currentSongIndex);
        } else if (isShuffle) {
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songsList.size() - 1) + 1);
            playSong(currentSongIndex);
        } else {
            // no repeat or shuffle ON - play next song
            if (currentSongIndex < (songsList.size() - 1)) {
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            } else {
                // play first song
                playSong(0);
                currentSongIndex = 0;
            }
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle(R.string.quit)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NowPlayingActivity.this.finish();

                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mp.release();
    }
}
