package com.aliyun.player.alivcplayerexpand.view.quality;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.player.alivcplayerexpand.listener.QualityValue;
import com.aliyun.player.alivcplayerexpand.theme.ITheme;
import com.aliyun.player.alivcplayerexpand.theme.Theme;
import com.aliyun.player.nativeclass.TrackInfo;

import java.util.LinkedList;
import java.util.List;

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 清晰度列表view。用于显示不同的清晰度列表。
 * 在{@link com.aliyun.player.alivcplayerexpand.widget.AliyunVodPlayerView}中使用。
 */
public class QualityView extends FrameLayout implements ITheme {

    //显示清晰度的列表
    private ListView mListView;
    private BaseAdapter mAdapter;
    //adapter的数据源
    private List<TrackInfo> mQualityItems;
    //当前播放的清晰度，高亮显示
    private String currentQuality;
    //清晰度项的点击事件
    private OnQualityClickListener mOnQualityClickListener;
    //是否是mts源
    private boolean isMtsSource = false;
    //默认的主题色
    private int themeColorResId = R.color.alivc_blue;


    public QualityView(@NonNull Context context) {
        super(context);
        init();
    }


    public QualityView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QualityView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //初始化布局
        LayoutInflater.from(getContext()).inflate(R.layout.alivc_view_quality, this, true);
        mListView = (ListView) findViewById(R.id.quality_view);

        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //不显示滚动条，保证全部被显示
        mListView.setVerticalScrollBarEnabled(false);
        mListView.setHorizontalScrollBarEnabled(false);

        mAdapter = new QualityAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击之后就隐藏
                hide();
                //回调监听
                if (mOnQualityClickListener != null && mQualityItems != null) {
                    mOnQualityClickListener.onQualityClick(mQualityItems.get(position));
                }
            }
        });

        hide();
    }

    @Override
    public void setTheme(Theme theme) {
        //更新主题
        if (theme == Theme.Blue) {
            themeColorResId = R.color.alivc_blue;
        } else if (theme == Theme.Green) {
            themeColorResId = R.color.alivc_green;
        } else if (theme == Theme.Orange) {
            themeColorResId = R.color.alivc_orange;
        } else if (theme == Theme.Red) {
            themeColorResId = R.color.alivc_red;
        } else {
            themeColorResId = R.color.alivc_blue;
        }

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置清晰度点击监听
     *
     * @param l 点击监听
     */
    public void setOnQualityClickListener(OnQualityClickListener l) {
        mOnQualityClickListener = l;
    }

    /**
     * 设置清晰度
     *
     * @param qualities      所有支持的清晰度
     * @param currentQuality 当前的清晰度
     */
    public void setQuality(List<TrackInfo> qualities, String currentQuality) {
        //排序之后显示出来
        mQualityItems = sortQuality(qualities);
        this.currentQuality = currentQuality;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置是否是MTS的源，因为清晰度的文字显示与其他的不一样
     *
     * @param isMts 是否是MTS的源
     */
    public void setIsMtsSource(boolean isMts) {
        isMtsSource = isMts;
    }


    /**
     * SQ，HQ，FD，LD，SD，HD，2K，4K，OD
     */
    private List<TrackInfo> sortQuality(List<TrackInfo> qualities) {

        //MTS的源不需要排序
        if (isMtsSource) {
            return qualities;
        }

        TrackInfo ld = null, sd = null, hd = null, fd = null, k2 = null, k4 = null, od = null, sq = null, hq = null;
        for (TrackInfo quality : qualities) {
            if (QualityValue.QUALITY_FLUENT.equals(quality.getVodDefinition())) {
//                fd = QualityValue.QUALITY_FLUENT;
                fd = quality;
            } else if (QualityValue.QUALITY_LOW.equals(quality.getVodDefinition())) {
//                ld = QualityValue.QUALITY_LOW;
                ld = quality;
            } else if (QualityValue.QUALITY_STAND.equals(quality.getVodDefinition())) {
//                sd = QualityValue.QUALITY_STAND;
                sd = quality;
            } else if (QualityValue.QUALITY_HIGH.equals(quality.getVodDefinition())) {
//                hd = QualityValue.QUALITY_HIGH;
                hd = quality;
            } else if (QualityValue.QUALITY_2K.equals(quality.getVodDefinition())) {
//                k2 = QualityValue.QUALITY_2K;
                k2 = quality;
            } else if (QualityValue.QUALITY_4K.equals(quality.getVodDefinition())) {
//                k4 = QualityValue.QUALITY_4K;
                k4 = quality;
            } else if (QualityValue.QUALITY_ORIGINAL.equals(quality.getVodDefinition())) {
//                od = QualityValue.QUALITY_ORIGINAL;
                od = quality;
            } else if (QualityValue.QUALITY_SQ.equals(quality.getVodDefinition())) {
                sq = quality;
            } else if (QualityValue.QUALITY_HQ.equals(quality.getVodDefinition())) {
                hq = quality;
            }
        }

        //清晰度按照fd,ld,sd,hd,2k,4k,od排序
        List<TrackInfo> sortedQuality = new LinkedList<>();
//        if (!TextUtils.isEmpty(fd)) {
//            sortedQuality.add(fd);
//        }
//
//        if (!TextUtils.isEmpty(ld)) {
//            sortedQuality.add(ld);
//        }
//        if (!TextUtils.isEmpty(sd)) {
//            sortedQuality.add(sd);
//        }
//        if (!TextUtils.isEmpty(hd)) {
//            sortedQuality.add(hd);
//        }
//
//        if (!TextUtils.isEmpty(k2)) {
//            sortedQuality.add(k2);
//        }
//        if (!TextUtils.isEmpty(k4)) {
//            sortedQuality.add(k4);
//        }
//        if (!TextUtils.isEmpty(od)) {
//            sortedQuality.add(od);
//        }
        if (sq != null) {
            sortedQuality.add(sq);
        }

        if (hq != null) {
            sortedQuality.add(hq);
        }

        if (fd != null) {
            sortedQuality.add(fd);
        }

        if (ld != null) {
            sortedQuality.add(ld);
        }
        if (sd != null) {
            sortedQuality.add(sd);
        }
        if (hd != null) {
            sortedQuality.add(hd);
        }

        if (k2 != null) {
            sortedQuality.add(k2);
        }
        if (k4 != null) {
            sortedQuality.add(k4);
        }
        if (od != null) {
            sortedQuality.add(od);
        }


        return sortedQuality;
    }

    /**
     * 在某个控件的上方显示
     *
     * @param anchor 控件
     */
    public void showAtTop(View anchor) {

        LayoutParams listViewParam = (LayoutParams) mListView.getLayoutParams();
        listViewParam.width = anchor.getWidth();
        listViewParam.height = getResources().getDimensionPixelSize(R.dimen.alivc_player_rate_item_height) * mQualityItems.size();
        int[] location = new int[2];
        anchor.getLocationInWindow(location);
        listViewParam.leftMargin = location[0];
        listViewParam.topMargin = getHeight() - listViewParam.height - anchor.getHeight() - 20;
        mListView.setLayoutParams(listViewParam);

        mListView.setVisibility(VISIBLE);

    }

    /**
     * 隐藏
     */
    public void hide() {
        if (mListView != null && mListView.getVisibility() == VISIBLE) {
            mListView.setVisibility(GONE);
        }
    }

    /**
     * 触摸之后就隐藏
     *
     * @param event 事件
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mListView.getVisibility() == VISIBLE) {
            hide();
            return true;
        }

        return super.onTouchEvent(event);
    }

    public interface OnQualityClickListener {
        /**
         * 清晰度点击事件
         *
         * @param qualityTrackInfo 点中的清晰度
         */
        void onQualityClick(TrackInfo qualityTrackInfo);
    }

    /**
     * 清晰度列表的适配器
     */
    private class QualityAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mQualityItems != null) {
                return mQualityItems.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return mQualityItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.ratetype_item, null);
            if (mQualityItems != null) {
                TrackInfo trackInfo = mQualityItems.get(position);
                String quality = trackInfo.getVodDefinition();
                view.setText(QualityItem.getItem(getContext(), quality, isMtsSource).getName());
                //默认白色，当前清晰度为主题色。
                if (quality.equals(currentQuality)) {
                    view.setTextColor(ContextCompat.getColor(getContext(), themeColorResId));
                } else {
                    view.setTextColor(ContextCompat.getColor(getContext(), R.color.alivc_common_font_white_light));
                }
            }
            return view;
        }
    }
}
