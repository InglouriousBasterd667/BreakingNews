package com.example.awfulman.breakingnews;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class PlayService extends Service {
    AudioPlayBinder binder = new AudioPlayBinder();
    MediaPlayer mediaPlayer = new MediaPlayer();

    public PlayService() {
    }

    public void play() {
        mediaPlayer.start();
    }

    public void pauseOrResume() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else mediaPlayer.start();
    }

    public void stop() {
        mediaPlayer.stop();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }



    class AudioPlayBinder extends Binder {
        PlayService getService() {
            return PlayService.this;
        }
    }


}
