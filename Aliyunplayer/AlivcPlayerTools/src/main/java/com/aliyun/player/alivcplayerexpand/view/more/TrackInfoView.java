package com.aliyun.player.alivcplayerexpand.view.more;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;

import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.player.alivcplayerexpand.view.quality.QualityItem;
import com.aliyun.player.nativeclass.TrackInfo;

import java.util.List;
import java.util.Locale;

public class TrackInfoView extends LinearLayout implements RadioGroup.OnCheckedChangeListener {

    private Context mContext;
    private RadioGroup mTrackInfoRadioGroup;

    private OnAudioChangedListener mOnAudioChangedListener;
    private OnBitrateChangedListener mOnBitrateChangedListener;
    private OnSubtitleChangedListener mOnSubtitleChangedListener;
    private OnDefinitionChangedListrener mOnDefinitionChangedListener;

    public TrackInfoView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public TrackInfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public TrackInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.alivc_dialog_trackinfo, this, true);
        findAllViews(view);
        initListener();
    }

    private void findAllViews(View view) {
        mTrackInfoRadioGroup = view.findViewById(R.id.radio_group_track_info);
    }

    private void initListener() {
        mTrackInfoRadioGroup.setOnCheckedChangeListener(this);
    }


    /**
     * 设置数据
     */
    public void setTrackInfoLists(List<TrackInfo> trackInfoLists) {
        if (mTrackInfoRadioGroup != null && trackInfoLists != null) {
            for (int i = 0; i < trackInfoLists.size(); i++) {
                TrackInfo trackInfo = trackInfoLists.get(i);
                RadioButton radioButton = createRadioButton();
                radioButton.setTag(trackInfo);
                if (trackInfo.getType() == TrackInfo.Type.TYPE_AUDIO) {
                    radioButton.setText(trackInfo.getAudioLang());
                } else if (trackInfo.getType() == TrackInfo.Type.TYPE_SUBTITLE) {
                    //增加取消字幕选项
//                    if(mTrackInfoRadioGroup.getChildCount() == 0){
//                        RadioButton mSubtitleRadioButton = createRadioButton();
//                        String cancelSubtitle = getContext().getString(R.string.alivc_player_cancel_subtitle);
//                        mSubtitleRadioButton.setTag(cancelSubtitle);
//                        mSubtitleRadioButton.setText(cancelSubtitle);
//                        mTrackInfoRadioGroup.addView(mSubtitleRadioButton);
//                    }
                    radioButton.setText(trackInfo.getSubtitleLang());
                } else if (trackInfo.getType() == TrackInfo.Type.TYPE_VOD) {
                    QualityItem item = QualityItem.getItem(getContext(), trackInfo.getVodDefinition(), false);
                    radioButton.setText(item.getName());
                } else {
                    if (i == 0) {
                        //自动码率
                        radioButton.setText("自动码率");
                        radioButton.setId(R.id.auto_bitrate);
                    } else {
                        radioButton.setText(String.format(Locale.getDefault(), "%d", trackInfo.getVideoBitrate()));
                    }
                }
                mTrackInfoRadioGroup.addView(radioButton);
            }

        }
    }

    /**
     * 创建RadioButton
     */
    private RadioButton createRadioButton() {
        final RadioButton radioButton = new RadioButton(mContext);
        radioButton.setPadding(50, 50, 50, 50);
        radioButton.setGravity(Gravity.CENTER);
        radioButton.setButtonDrawable(null);
        radioButton.setTextColor(getResources().getColorStateList(R.color.radio_track_info_color_selector));
        return radioButton;
    }

    /**
     * 设置当前选中的TrackInfo
     */
    public void setCurrentTrackInfo(TrackInfo trackInfo) {
        if (mTrackInfoRadioGroup != null && mTrackInfoRadioGroup.getChildCount() > 0) {
            int childCount = mTrackInfoRadioGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                RadioButton radioButtaon = (RadioButton) mTrackInfoRadioGroup.getChildAt(i);
                if (radioButtaon != null) {
                    TrackInfo radioButtonTag = (TrackInfo) radioButtaon.getTag();
                    if (trackInfo != null && radioButtonTag != null && trackInfo.getIndex() == radioButtonTag.getIndex()) {
                        if (trackInfo.getType() == TrackInfo.Type.TYPE_VIDEO && i == 0) {
                            //排除自动码率选项
                            continue;
                        } else {
                            mTrackInfoRadioGroup.check(radioButtaon.getId());
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        RadioButton mRadioButton = radioGroup.findViewById(checkedId);
        Object radioButtonTag = mRadioButton.getTag();
        if (radioButtonTag instanceof TrackInfo) {
            TrackInfo mTrackInfo = (TrackInfo) mRadioButton.getTag();
            if (mTrackInfo != null) {
                TrackInfo.Type type = mTrackInfo.getType();
                if (type == TrackInfo.Type.TYPE_AUDIO) {
                    if (mOnAudioChangedListener != null) {
                        mOnAudioChangedListener.onAudioChanged(mTrackInfo);
                    }
                } else if (type == TrackInfo.Type.TYPE_SUBTITLE) {
                    if (mOnSubtitleChangedListener != null) {
                        mOnSubtitleChangedListener.onSubtitleChanged(mTrackInfo);
                    }
                } else if (type == TrackInfo.Type.TYPE_VIDEO) {
                    if (mOnBitrateChangedListener != null) {
                        mOnBitrateChangedListener.onBitrateChanged(mTrackInfo, checkedId);
                    }
                } else {
                    if (mOnDefinitionChangedListener != null) {
                        mOnDefinitionChangedListener.onDefinitionChanged(mTrackInfo);
                    }
                }
            } else {
                if (mOnBitrateChangedListener != null) {
                    mOnBitrateChangedListener.onBitrateChanged(mTrackInfo, checkedId);
                }
            }
        } else if (radioButtonTag instanceof String) {
            if (mOnSubtitleChangedListener != null) {
                mOnSubtitleChangedListener.onSubtitleCancel();
            }
        }

    }

    public interface OnAudioChangedListener {
        /**
         * 音轨切换
         */
        void onAudioChanged(TrackInfo selectTrackInfo);
    }

    public void setOnAudioChangedListener(OnAudioChangedListener listener) {
        this.mOnAudioChangedListener = listener;
    }

    public interface OnBitrateChangedListener {
        /**
         * 码率切换
         */
        void onBitrateChanged(TrackInfo selectTrackInfo, int checkedId);
    }

    public void setOnBitrateChangedListener(OnBitrateChangedListener listener) {
        this.mOnBitrateChangedListener = listener;
    }

    public interface OnSubtitleChangedListener {
        /**
         * 字幕切换
         */
        void onSubtitleChanged(TrackInfo selectTrackInfo);

        void onSubtitleCancel();
    }

    public void setOnSubtitleChangedListener(OnSubtitleChangedListener listener) {
        this.mOnSubtitleChangedListener = listener;
    }

    public interface OnDefinitionChangedListrener {

        /**
         * 清晰度切换
         */
        void onDefinitionChanged(TrackInfo selectTrackInfo);
    }

    public void setOnDefinitionChangedListener(OnDefinitionChangedListrener listener) {
        this.mOnDefinitionChangedListener = listener;
    }

}
