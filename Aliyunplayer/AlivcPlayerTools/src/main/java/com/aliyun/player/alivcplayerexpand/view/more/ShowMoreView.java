package com.aliyun.player.alivcplayerexpand.view.more;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aliyun.player.IPlayer;
import com.aliyun.player.alivcplayerexpand.R;


public class ShowMoreView extends LinearLayout implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private Context context;
    private SeekBar seekLight;
    private SeekBar seekVoice;
    private TextView tvDonwload;
    private TextView tvCastScreen;
    private TextView tvBarrage;
    private RadioGroup rgSpeed;
    private AliyunShowMoreValue moreValue;
    private OnDownloadButtonClickListener mOnDownloadButtonClickListener;
    private OnSpeedCheckedChangedListener mOnSpeedCheckedChangedListener;
    private OnLoopCheckedChangedListener mOnLoopCheckedChangedListener;
    private OnLightSeekChangeListener mOnLightSeekChangeListener;
    private OnVoiceSeekChangeListener mOnVoiceSeekChangeListener;
    private OnScreenCastButtonClickListener mOnScreenCastButtonClickListener;
    private OnScaleModeCheckedChangedListener mOnScaleModeCheckedChangedListener;
    private OnBarrageButtonClickListener mOnBarrageButtonClickListener;
    private RadioGroup scaleModelRadioGroup;
    private RadioGroup loopPlayRadioGroup;

    public ShowMoreView(Context context, AliyunShowMoreValue moreValue) {
        super(context);
        this.context = context;
        this.moreValue = moreValue;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.alivc_dialog_more, this, true);
        findAllViews(view);
    }

    private void findAllViews(View view) {
        seekLight = view.findViewById(R.id.seek_light);
        seekVoice = view.findViewById(R.id.seek_voice);
        tvDonwload = view.findViewById(R.id.tv_download);
        tvCastScreen = view.findViewById(R.id.tv_cast_screen);
        tvBarrage = view.findViewById(R.id.tv_barrage);
        rgSpeed = findViewById(R.id.alivc_rg_speed);
        loopPlayRadioGroup = view.findViewById(R.id.alivc_rg_loop);
        scaleModelRadioGroup = view.findViewById(R.id.alivc_rg_scale_model);
        configViews();
        addListener();

    }

    private void configViews() {
        if (moreValue == null) {
            return;
        }
        seekLight.setProgress(moreValue.getScreenBrightness());
        seekVoice.setProgress(moreValue.getVolume());

        int currentRbIndex = 0;
        float curentSpeed = moreValue.getSpeed();
        if (curentSpeed == 0.0f) {
            currentRbIndex = 0;
        } else if (curentSpeed == 0.5f) {
            currentRbIndex = 1;
        } else if (curentSpeed == 1.5f) {
            currentRbIndex = 2;
        } else if (curentSpeed == 2.0f) {
            currentRbIndex = 3;
        }
        rgSpeed.check(rgSpeed.getChildAt(currentRbIndex).getId());

        IPlayer.ScaleMode currentScaleMode = moreValue.getScaleMode();
        int currentScaleModeIndex;
        if (currentScaleMode == IPlayer.ScaleMode.SCALE_ASPECT_FIT) {
            currentScaleModeIndex = 0;
        } else if (currentScaleMode == IPlayer.ScaleMode.SCALE_ASPECT_FILL) {
            currentScaleModeIndex = 1;
        } else if (currentScaleMode == IPlayer.ScaleMode.SCALE_TO_FILL) {
            currentScaleModeIndex = 2;
        } else {
            currentScaleModeIndex = 0;
        }
        scaleModelRadioGroup.check(scaleModelRadioGroup.getChildAt(currentScaleModeIndex).getId());
        loopPlayRadioGroup.check(loopPlayRadioGroup.getChildAt(moreValue.isLoop() ? 0 : 1).getId());
    }


    private void addListener() {
        tvDonwload.setOnClickListener(this);
        tvCastScreen.setOnClickListener(this);
        tvBarrage.setOnClickListener(this);

        rgSpeed.setOnCheckedChangeListener(this);
        loopPlayRadioGroup.setOnCheckedChangeListener(this);
        scaleModelRadioGroup.setOnCheckedChangeListener(this);

        seekLight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mOnLightSeekChangeListener != null) {
                    mOnLightSeekChangeListener.onStart(seekBar);
                }
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mOnLightSeekChangeListener != null) {
                    mOnLightSeekChangeListener.onProgress(seekBar, progress, fromUser);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mOnLightSeekChangeListener != null) {
                    mOnLightSeekChangeListener.onStop(seekBar);
                }
            }
        });

        seekVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mOnVoiceSeekChangeListener != null) {
                    mOnVoiceSeekChangeListener.onStart(seekBar);
                }
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mOnVoiceSeekChangeListener != null) {
                    mOnVoiceSeekChangeListener.onProgress(seekBar, progress, fromUser);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mOnVoiceSeekChangeListener != null) {
                    mOnVoiceSeekChangeListener.onStop(seekBar);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_download) {
            // 下载
            if (mOnDownloadButtonClickListener != null) {
                mOnDownloadButtonClickListener.onDownloadClick();
            }
        } else if (id == R.id.tv_cast_screen) {
            // 投屏
            if (mOnScreenCastButtonClickListener != null) {
                mOnScreenCastButtonClickListener.onScreenCastClick();
            }

        } else if (id == R.id.tv_barrage) {
            // 弹幕
            if (mOnBarrageButtonClickListener != null) {
                mOnBarrageButtonClickListener.onBarrageClick();
            }
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (mOnSpeedCheckedChangedListener != null && group == rgSpeed) {
            mOnSpeedCheckedChangedListener.onSpeedChanged(group, checkedId);
        }
        if (mOnScaleModeCheckedChangedListener != null && group == scaleModelRadioGroup) {
            mOnScaleModeCheckedChangedListener.onScaleModeChanged(group, checkedId);
        }
        if (mOnLoopCheckedChangedListener != null && group == loopPlayRadioGroup) {
            mOnLoopCheckedChangedListener.onLoopChanged(group, checkedId);
        }
    }

    public interface OnDownloadButtonClickListener {
        /**
         * 下载按钮点击
         */
        void onDownloadClick();
    }

    public void setOnDownloadButtonClickListener(OnDownloadButtonClickListener listener) {
        this.mOnDownloadButtonClickListener = listener;
    }

    public interface OnScreenCastButtonClickListener {
        /**
         * 投屏按钮点击
         */
        void onScreenCastClick();
    }

    public void setOnScreenCastButtonClickListener(OnScreenCastButtonClickListener listener) {
        this.mOnScreenCastButtonClickListener = listener;
    }

    public interface OnBarrageButtonClickListener {
        /**
         * 弹幕按钮点击
         */
        void onBarrageClick();
    }

    public void setOnBarrageButtonClickListener(OnBarrageButtonClickListener listener) {
        this.mOnBarrageButtonClickListener = listener;
    }

    public interface OnLoopCheckedChangedListener {
        void onLoopChanged(RadioGroup group, int checkedId);
    }

    public void setOnLoopCheckedChangedListener(OnLoopCheckedChangedListener listener) {
        this.mOnLoopCheckedChangedListener = listener;
    }

    public interface OnSpeedCheckedChangedListener {
        /**
         * 速度切换
         */
        void onSpeedChanged(RadioGroup group, int checkedId);
    }

    public void setOnSpeedCheckedChangedListener(OnSpeedCheckedChangedListener listener) {
        this.mOnSpeedCheckedChangedListener = listener;
    }

    public interface OnScaleModeCheckedChangedListener {
        /**
         * 比例切换
         */
        void onScaleModeChanged(RadioGroup group, int checkedId);
    }

    public void setOnScaleModeCheckedChangedListener(OnScaleModeCheckedChangedListener listener) {
        this.mOnScaleModeCheckedChangedListener = listener;
    }

    /**
     * 亮度调节
     */
    public interface OnLightSeekChangeListener {
        void onStart(SeekBar seekBar);

        void onProgress(SeekBar seekBar, int progress, boolean fromUser);

        void onStop(SeekBar seekBar);
    }

    public void setOnLightSeekChangeListener(OnLightSeekChangeListener listener) {
        this.mOnLightSeekChangeListener = listener;
    }

    /**
     * 音量调节
     */
    public interface OnVoiceSeekChangeListener {
        void onStart(SeekBar seekBar);

        void onProgress(SeekBar seekBar, int progress, boolean fromUser);

        void onStop(SeekBar seekBar);
    }

    public void setOnVoiceSeekChangeListener(OnVoiceSeekChangeListener listener) {
        this.mOnVoiceSeekChangeListener = listener;
    }

    /**
     * 设置音量
     */
    public void setVoiceVolume(float volume) {
        if (seekVoice != null) {
            seekVoice.setProgress((int) (volume * 100));
        }
    }

    /**
     * 设置亮度
     */
    public void setBrightness(int value) {
        if (seekLight != null) {
            seekLight.setProgress(value);
        }
    }
}

