package com.aliyun.player.alivcplayerexpand.view.more;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aliyun.player.alivcplayerexpand.R;

/**
 * 弹幕设置View
 */
public class DanmakuSettingView extends LinearLayout {

    /**
     * 透明度默认值
     */
    public static final int ALPHA_PROGRESS_DEFAULT = 0;
    /**
     * 速率默认值
     */
    public static final int SPEED_PROGRESS_DEFAULT = 30;
    /**
     * 默认显示区域
     */
    public static final int REGION_PROGRESS_DEFAULT = 0;

    private View view;
    /**
     * 透明度
     */
    private SeekBar mAlphaSeek;
    private TextView mAlphaValueTextView;
    private SeekBar.OnSeekBarChangeListener mOnAlphaSeekBarChangeListener;
    private int mAlphProgress;
    /**
     * 区域
     */
    private SeekBar mRegionSeek;
    private TextView mRegionValueTextView;
    private SeekBar.OnSeekBarChangeListener mOnRegionSeekBarChangeListener;
    private int mRegionProgress;
    /**
     * 速率
     */
    private SeekBar mSpeedSeek;
    private TextView mSpeedValueTextView;
    private SeekBar.OnSeekBarChangeListener mOnSpeedSeekBarChangeListener;
    private int mSpeedProgress;
    /**
     * 恢复默认
     */
    private TextView mDefaultTextView;
    private OnDefaultClickListener mOnDefaultClickListener;

    public DanmakuSettingView(Context context) {
        super(context);
        init(context);
    }

    public DanmakuSettingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DanmakuSettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.alivc_dialog_danmaku_setting, this, true);

        initView();
        initListener();
        initDefault();
    }

    private void initView() {
        mAlphaSeek = view.findViewById(R.id.seek_alpha);
        mSpeedSeek = view.findViewById(R.id.seek_speed);
        mRegionSeek = view.findViewById(R.id.seek_region);

        mDefaultTextView = view.findViewById(R.id.tv_default);

        mAlphaValueTextView = view.findViewById(R.id.tv_alpha_value);
        mSpeedValueTextView = view.findViewById(R.id.tv_speed_value);
        mRegionValueTextView = view.findViewById(R.id.tv_region_value);
    }

    private void initListener() {
        mAlphaSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mAlphProgress = progress;
                if (mOnAlphaSeekBarChangeListener != null) {
                    mOnAlphaSeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
                }
                if (mAlphaValueTextView != null) {
                    mAlphaValueTextView.setText(progress + "%");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mOnAlphaSeekBarChangeListener != null) {
                    mOnAlphaSeekBarChangeListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mOnAlphaSeekBarChangeListener != null) {
                    mOnAlphaSeekBarChangeListener.onStopTrackingTouch(seekBar);
                }
            }
        });

        mSpeedSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSpeedProgress = progress;
                if (mOnSpeedSeekBarChangeListener != null) {
                    mOnSpeedSeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
                }
                if (mSpeedValueTextView != null) {
                    mSpeedValueTextView.setText(progress + "%");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mOnSpeedSeekBarChangeListener != null) {
                    mOnSpeedSeekBarChangeListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mOnSpeedSeekBarChangeListener != null) {
                    mOnSpeedSeekBarChangeListener.onStopTrackingTouch(seekBar);
                }
            }
        });

        mRegionSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRegionProgress = progress;
                if (mOnRegionSeekBarChangeListener != null) {
                    mOnRegionSeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
                }
                if (mRegionValueTextView != null) {
                    switch (progress) {
                        case 0:
                            mRegionValueTextView.setText(getResources().getString(R.string.alivc_danmaku_position_quarter));
                            break;
                        case 1:
                            mRegionValueTextView.setText(getResources().getString(R.string.alivc_danmaku_position_half));
                            break;
                        case 2:
                            mRegionValueTextView.setText(getResources().getString(R.string.alivc_danmaku_position_Three_fourths));
                            break;
                        case 3:
                            mRegionValueTextView.setText(getResources().getString(R.string.alivc_danmaku_position_unlimit));
                            break;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mOnRegionSeekBarChangeListener != null) {
                    mOnRegionSeekBarChangeListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mOnRegionSeekBarChangeListener != null) {
                    mOnRegionSeekBarChangeListener.onStopTrackingTouch(seekBar);
                }
            }
        });

        mDefaultTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlphaSeek.setProgress(ALPHA_PROGRESS_DEFAULT);
                mSpeedSeek.setProgress(SPEED_PROGRESS_DEFAULT);
                mRegionSeek.setProgress(REGION_PROGRESS_DEFAULT);
                if (mOnDefaultClickListener != null) {
                    mOnDefaultClickListener.onDefaultClick();
                }
            }
        });
    }

    private void initDefault() {
        mAlphaSeek.setProgress(ALPHA_PROGRESS_DEFAULT);
        mSpeedSeek.setProgress(SPEED_PROGRESS_DEFAULT);
        mRegionSeek.setProgress(REGION_PROGRESS_DEFAULT);
    }

    public void setAlphaProgress(int progress) {
        mAlphProgress = progress;
        if (mAlphaSeek != null) {
            mAlphaSeek.setProgress(progress);
        }
    }

    public void setSpeedProgress(int progress) {
        mSpeedProgress = progress;
        if (mSpeedSeek != null) {
            mSpeedSeek.setProgress(progress);
        }
    }

    public void setRegionProgress(int progress) {
        mRegionProgress = progress;
        if (mRegionSeek != null) {
            mRegionSeek.setProgress(progress);
        }
    }

    public void setOnAlphaSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener) {
        this.mOnAlphaSeekBarChangeListener = listener;
    }

    public void setOnRegionSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener) {
        this.mOnRegionSeekBarChangeListener = listener;
    }

    public void setOnSpeedSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener) {
        this.mOnSpeedSeekBarChangeListener = listener;
    }

    public interface OnDefaultClickListener {
        void onDefaultClick();
    }

    public void setOnDefaultListener(OnDefaultClickListener listener) {
        this.mOnDefaultClickListener = listener;
    }
}
