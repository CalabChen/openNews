package com.example.opennews.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.opennews.R;
import com.example.opennews.db.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PlaybackPopupWindow {

    private final PopupWindow popupWindow;
    private final View contentView;
    private final Context context;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBarMusic;
    private TextView textViewTotalDuration;
    private TextView textViewCurrentDuration;
    private ImageButton buttonPlayPauseMusic;
    private ImageButton buttonRewindMusic;
    private ImageButton buttonForwardMusic;
    private ImageButton buttonPreviousMusic; // 上一首按钮
    private ImageButton buttonNextMusic;     // 下一首按钮
    private String[] songResources = Constants.SONG_RESOURCES;
    private int currentSongIndex = 0;


    public PlaybackPopupWindow(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.fragment_playback_popup, null);

        setupPlaybackControls(contentView);

        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.setAnimationStyle(R.style.PopupAnimation);

    }

    private void setupPlaybackControls(View view) {
        seekBarMusic = view.findViewById(R.id.seekBarMusic);
        buttonPlayPauseMusic = view.findViewById(R.id.buttonPlayPauseMusic);
        buttonRewindMusic = view.findViewById(R.id.buttonRewindMusic);
        buttonForwardMusic = view.findViewById(R.id.buttonForwardMusic);
        buttonPreviousMusic = view.findViewById(R.id.buttonPreviousMusic); // 初始化上一首按钮
        buttonNextMusic = view.findViewById(R.id.buttonNextMusic);         // 初始化下一首按钮
        textViewTotalDuration = view.findViewById(R.id.textViewTotalDuration);
        textViewCurrentDuration = view.findViewById(R.id.textViewCurrentDuration);

        // 设置播放/暂停按钮点击事件
        buttonPlayPauseMusic.setOnClickListener(v -> togglePlayPause());
        // 设置快退按钮点击事件
        buttonRewindMusic.setOnClickListener(v -> rewindMusic());
        // 设置快进按钮点击事件
        buttonForwardMusic.setOnClickListener(v -> forwardMusic());
        // 设置上一首按钮点击事件
        buttonPreviousMusic.setOnClickListener(v -> playPreviousMusic());
        // 设置下一首按钮点击事件
        buttonNextMusic.setOnClickListener(v -> playNextMusic());

        // 设置 SeekBar 的变化监听器
        seekBarMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // 当音乐播放结束时自动播放下一首
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(mp -> playNextMusic());

    }


    private void togglePlayPause() {
        if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
            playMusic();
            buttonPlayPauseMusic.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            pauseMusic();
            buttonPlayPauseMusic.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    private void rewindMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            if (currentPosition > 5000) {
                mediaPlayer.seekTo(currentPosition - 5000); // 快退5秒
            } else {
                mediaPlayer.seekTo(0);
            }
        }
    }

    private void forwardMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.seekTo(currentPosition + 5000); // 快进5秒
        }
    }

    private void playPreviousMusic() {
        if (songResources.length <= 0) return;

        currentSongIndex = (currentSongIndex - 1 + songResources.length) % songResources.length;
        playMusic();
    }

    private void playNextMusic() {
        if (songResources.length <= 0) return;

        currentSongIndex = (currentSongIndex + 1) % songResources.length;
        playMusic();
    }

    private void playMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        try {
            int resId = context.getResources().getIdentifier(songResources[currentSongIndex], "raw", context.getPackageName());
            mediaPlayer.setDataSource(context, Uri.parse("android.resource://" + context.getPackageName() + "/" + resId));
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBarMusic.setMax(mediaPlayer.getDuration());
            updateSeekBar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void updateSeekBar() {
        if (mediaPlayer != null) {
            seekBarMusic.setProgress(mediaPlayer.getCurrentPosition());
            textViewCurrentDuration.setText(formatTime(mediaPlayer.getCurrentPosition()));
            textViewTotalDuration.setText(formatTime(mediaPlayer.getDuration()));

            if (mediaPlayer.isPlaying()) {
                Runnable notification = this::updateSeekBar;
                seekBarMusic.postDelayed(notification, 1000);
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private String formatTime(int duration) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        if (!popupWindow.isShowing()) {
            popupWindow.showAtLocation(parent, gravity, x, y);
        }
    }

    public void dismiss() {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}