package com.example.sunsai.mymusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import com.example.sunsai.mymusicplayer.PlayerActivity;

public class MusicService extends Service {

  public static final String CMD_MUSICSERVICE_PLAY =
      "com.spsl.sample.musicplayer.CMD_MUSICSERVICE_PLAY";
  public static final String CMD_MUSICSERVICE_PAUSE =
      "com.spsl.sample.musicplayer.CMD_MUSICSERVICE_PAUSE";
  public static final String CMD_MUSICSERVICE_STOP =
      "com.spsl.sample.musicplayer.CMD_MUSICSERVICE_STOP";
  public static final String CMD_MUSICSERVICE_NEXT =
      "com.spsl.sample.musicplayer.CMD_MUSICSERVICE_NEXT";
  public static final String CMD_MUSICSERVICE_PREV =
      "com.spsl.sample.musicplayer.CMD_MUSICSERVICE_PREV";
  public static final String CMD_MUSICSERVICE_RESUM =
      "com.spsl.sample.musicplayer.CMD_MUSICSERVICE_RESUM";
public static final String CMD_MUSICSERVICE_SEEKTO =
      "com.spsl.sample.musicplayer.CMD_MUSICSERVICE_SEEKTO111";

  private LocalBroadcastManager localBroadcastManager;

  enum state {
    NO_START, PLAYING, IS_PAUSE, STOP
  }

  private state currentState;

  private MediaPlayer mediaPlayer;

  public static final String TAG = "MusicService";
  public MusicService() {
  }

  @Override public IBinder onBind(Intent intent) {
    // TODO: Return the communication channel to the service.
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();
    localBroadcastManager = LocalBroadcastManager.getInstance(this);
    mediaPlayer = new MediaPlayer();
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    if (null != intent) {
      if (TextUtils.equals(CMD_MUSICSERVICE_PLAY, intent.getAction())) {
        play(intent);
      } else if (TextUtils.equals(CMD_MUSICSERVICE_PAUSE, intent.getAction())) {
        pause(intent);
      } else if (TextUtils.equals(CMD_MUSICSERVICE_STOP, intent.getAction())) {
        stop(intent);
      } else if (TextUtils.equals(CMD_MUSICSERVICE_NEXT, intent.getAction())) {
        next(intent);
      } else if (TextUtils.equals(CMD_MUSICSERVICE_PREV, intent.getAction())) {
        prev(intent);
      } else if (TextUtils.equals(CMD_MUSICSERVICE_RESUM, intent.getAction())) {
        resum(intent);
      } else if (TextUtils.equals(CMD_MUSICSERVICE_SEEKTO, intent.getAction())) {
        seekTo(intent);
      }
    }

    return super.onStartCommand(intent, flags, startId);
  }

  private void seekTo(Intent intent) {
    Log.d(TAG, "seekTo");
    if (null != mediaPlayer) {
      int seekTo = intent.getIntExtra("SEEK_TO", -1);
      if (seekTo > -1) {
        mediaPlayer.seekTo(seekTo);
      }
    }
  }

  public void play(Intent intent) {
    Log.i(TAG, "play");

    String songUrl = intent.getStringExtra("url");

    if (currentState != state.PLAYING && !TextUtils.isEmpty(songUrl)) {
      try {
        mediaPlayer.setDataSource(songUrl);
        mediaPlayer.prepare();
        mediaPlayer.start();
        currentState = state.PLAYING;
        handler.sendEmptyMessage(0);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void pause(Intent intent) {
    currentState = state.IS_PAUSE;
    mediaPlayer.pause();
  }

  private void resum(Intent intent) {
    if (currentState == state.IS_PAUSE) {
      currentState = state.PLAYING;
      mediaPlayer.start();
    }
  }

  public void stop(Intent intent) {

  }

  public void next(Intent intent) {
    
  }

  public void prev(Intent intent) {

  }

  public void sendBroadCast() {
    Log.i(TAG, "sendBroadCast");
    Intent intent = new Intent();
    intent.setAction(PlayerActivity.BROADCAST_MUSICPLAYER);
    intent.putExtra("CurrentPosition",mediaPlayer.getCurrentPosition());
    intent.putExtra("Duration",mediaPlayer.getDuration());
    localBroadcastManager.sendBroadcast(intent);
  }

  private Handler handler = new Handler() {
    @Override public void handleMessage(Message msg) {
      super.handleMessage(msg);

      Log.i(TAG, "handleMessage");

      sendBroadCast();
      if (currentState == state.PLAYING) {
        sendEmptyMessageDelayed(0, 1000);
      }
    }
  };

}
