package com.aliyun.player.alivcplayerexpand.view.more;

import com.aliyun.player.IPlayer;

public class AliyunShowMoreValue {
    private int volume;
    private int screenBrightness;
    private float speed;
    private boolean loop;
    private IPlayer.ScaleMode scaleMode;

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public IPlayer.ScaleMode getScaleMode() {
        return scaleMode;
    }

    public void setScaleMode(IPlayer.ScaleMode scaleMode) {
        this.scaleMode = scaleMode;
    }

    public int getScreenBrightness() {
        return screenBrightness;
    }

    public void setScreenBrightness(int screenBrightness) {
        this.screenBrightness = screenBrightness;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean isLoop() {
        return loop;
    }
}
