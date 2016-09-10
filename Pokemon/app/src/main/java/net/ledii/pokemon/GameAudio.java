package net.ledii.pokemon;

import android.content.Context;
import android.media.MediaPlayer;

public class GameAudio {
    public enum Music {
        WALKING, BATTLE
    }
    private MediaPlayer musicPlayer;
    private Context context;

    GameAudio(Context context) {
        this.context = context;
    }

    public void setMusic(Music track) {
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.release();
            musicPlayer = null;
        }
        if (track != null) {
            musicPlayer = MediaPlayer.create(context, getResource(track));
            musicPlayer.setLooping(true);
        }
    }

    private int getResource(Music track) {
        int result = 0;
        switch (track) {
            case WALKING: { result = R.raw.pokemon_walking; break; }
            case BATTLE: { result = R.raw.pokemon_battle; break; }
        }
        return result;
    }

    public void pause() {
        if (musicPlayer != null) {
            musicPlayer.pause();
        }
    }

    public void play() {
        if (musicPlayer != null) {
            musicPlayer.start();
            float volume = 0.0f;
            musicPlayer.setVolume(volume, volume);
        }
    }

    public void stop() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }
}