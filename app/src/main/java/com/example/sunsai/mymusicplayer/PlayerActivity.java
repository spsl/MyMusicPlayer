package com.example.sunsai.mymusicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.sunsai.mymusicplayer.service.MusicService;
import com.orhanobut.logger.Logger;

/**
 * Created by sunsai on 2016/2/3.
 */
public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

  private TextView line1;
  private TextView line2;
  private TextView line3;
  private TextView startText;
  private TextView endText;
  private SeekBar seekBar;
  private Button prev;
  private Button playOrpause;
  private Button next;

  public static final String BROADCAST_MUSICPLAYER =
      "com.spsl.sample.musicplayer.BROADCAST_MUSICPLAYER";
  private boolean isPlaying = false;

  private boolean isPause = false;

  private static final String TAG = "PlayerActivity";

  private BroadcastReceiver broadcastReceiver;
  private LocalBroadcastManager localBroadcastManager;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_player);
    initView();
    registerOnClick();
    registerBroadcast();
  }

  private void registerBroadcast() {
    localBroadcastManager = LocalBroadcastManager.getInstance(this);
    broadcastReceiver = new MusicReceiver();
    IntentFilter inf = new IntentFilter();
    inf.addAction(BROADCAST_MUSICPLAYER);
    localBroadcastManager.registerReceiver(broadcastReceiver, inf);
  }

  @Override public void onStop() {
    super.onStop();
    Logger.d("onStop");
    localBroadcastManager.unregisterReceiver(broadcastReceiver);
  }

  private void initView() {
    line1 = (TextView) findViewById(R.id.line1);
    line2 = (TextView) findViewById(R.id.line2);
    line3 = (TextView) findViewById(R.id.line3);
    startText = (TextView) findViewById(R.id.startText);
    endText = (TextView) findViewById(R.id.endText);
    seekBar = (SeekBar) findViewById(R.id.seekBar);
    prev = (Button) findViewById(R.id.prev);
    playOrpause = (Button) findViewById(R.id.play_pause);
    next = (Button) findViewById(R.id.next);
  }

  public void registerOnClick() {
    prev.setOnClickListener(this);
    playOrpause.setOnClickListener(this);
    next.setOnClickListener(this);

    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override public void onStopTrackingTouch(SeekBar seek) {
        Log.d(TAG, "onStopTrackingTouch");
        seekTo(seek.getProgress());
      }
    });
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.prev:
        Log.d(TAG, "prev");
        break;
      case R.id.play_pause:
        if (!isPlaying) {
          isPlaying = true;
          play();
        } else if (isPause) {
          resume();
        } else {
          pause();
        }
        break;
      case R.id.next:
        break;
    }
  }

  private void resume() {
    isPause = false;
    playOrpause.setText(getString(R.string.pause));
    Intent intent = new Intent(this, MusicService.class);
    intent.setAction(MusicService.CMD_MUSICSERVICE_RESUM);
    startService(intent);
  }

  private void pause() {
    isPause = true;
    playOrpause.setText(getString(R.string.play));
    Intent intent = new Intent(this, MusicService.class);
    intent.setAction(MusicService.CMD_MUSICSERVICE_PAUSE);
    startService(intent);
  }

  private void play() {
    Log.d("", "player play");
    playOrpause.setText(getString(R.string.pause));
    String url =
        "http://yinyueshiting.baidu.com/data2/music/242383503/242383503.mp3?xcode=42b17cb7c88fb43ab6f28ebc1a3b5868";
    Intent intent = new Intent(this, MusicService.class);
    intent.setAction(MusicService.CMD_MUSICSERVICE_PLAY);
    intent.putExtra("url", url);
    startService(intent);
  }

  private void seekTo(int position) {
    Log.d(TAG, "seekTo");
    Intent intent = new Intent(this, MusicService.class);
    intent.setAction(MusicService.CMD_MUSICSERVICE_SEEKTO);
    intent.putExtra("SEEK_TO", position);
    startService(intent);
  }

  public void updateSeekBar(int position) {
    seekBar.setProgress(position);
  }

  public void updateCurrTime(String time) {
    startText.setText(time);
  }

  public class MusicReceiver extends BroadcastReceiver {

    @Override public void onReceive(Context context, Intent intent) {
      int posi = intent.getIntExtra("CurrentPosition", 0);
      int duration = intent.getIntExtra("Duration", 0);
      seekBar.setMax(duration);
      seekBar.setProgress(posi);
      startText.setText(getStrTime(posi));
      endText.setText(getStrTime(duration));
    }

    private String getStrTime(int mis) {

      int m = mis / 1000 / 60;
      int s = mis / 1000 % 60;
      String time = "";

      if (m < 10) {
        time = "0" + m;
      } else {
        time = "" + m;
      }
      time += ":";

      if (s < 10) {
        time = time + "0" + s;
      } else {
        time = time + s;
      }

      return time;
    }
  }
}
