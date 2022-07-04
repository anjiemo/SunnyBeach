package com.aliyun.player.alivcplayerexpand.view.download;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.player.alivcplayerexpand.listener.QualityValue;
import com.aliyun.player.alivcplayerexpand.util.DensityUtil;
import com.aliyun.player.alivcplayerexpand.util.FixedToastUtils;
import com.aliyun.player.alivcplayerexpand.util.ImageLoader;
import com.aliyun.player.alivcplayerexpand.util.WrapCheckGroup;
import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadMediaInfo;
import com.aliyun.player.aliyunplayerbase.util.AliyunScreenMode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 */

public class AddDownloadView extends LinearLayout {

    private AliyunScreenMode screenMode;
    private ImageView ivVideoCover;
    private TextView tvAddDownloadViewTitle;
    private TextView tvAddDownloadViewSize;
    private Button downloadBtn;
    private WrapCheckGroup wrapCheckGroup;

    private Map<String, String> qualityList = new HashMap<>();
    private RadioGroup rgQualityList;
    private AliyunDownloadMediaInfo downLoadTag;

    public AddDownloadView(Context context) {
        super(context);
        init();
    }

    public AddDownloadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AddDownloadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public AddDownloadView(Context context, AliyunScreenMode screenMode) {
        super(context);
        this.screenMode = screenMode;
        init();
    }

    private void init() {
        if (screenMode == AliyunScreenMode.Small) {
            LayoutInflater.from(getContext()).inflate(R.layout.view_add_download, this, true);
            findAllViews();
        } else {
            LayoutInflater.from(getContext()).inflate(R.layout.view_add_download_horizontal, this, true);
            findAllViewsByFullScreen();
        }
        qualityList.put(QualityValue.QUALITY_FLUENT,
                getContext().getString(R.string.alivc_fd_definition));
        qualityList.put(QualityValue.QUALITY_LOW,
                getContext().getString(R.string.alivc_ld_definition));
        qualityList.put(QualityValue.QUALITY_STAND,
                getContext().getString(R.string.alivc_sd_definition));
        qualityList.put(QualityValue.QUALITY_HIGH,
                getContext().getString(R.string.alivc_hd_definition));
        qualityList.put(QualityValue.QUALITY_2K, getContext().getString(R.string.alivc_k2_definition));
        qualityList.put(QualityValue.QUALITY_4K, getContext().getString(R.string.alivc_k4_definition));
        qualityList.put(QualityValue.QUALITY_ORIGINAL,
                getContext().getString(R.string.alivc_od_definition));
        qualityList.put(QualityValue.QUALITY_SQ,
                getContext().getString(R.string.alivc_sq_definition));
        qualityList.put(QualityValue.QUALITY_HQ,
                getContext().getString(R.string.alivc_hq_definition));
    }

    private void findAllViewsByFullScreen() {
        ivVideoCover = findViewById(R.id.iv_video_cover);
        tvAddDownloadViewTitle = findViewById(R.id.tv_add_download_view_title);
        tvAddDownloadViewSize = findViewById(R.id.tv_add_download_view_size);

        rgQualityList = findViewById(R.id.rg_quality_list);

        // close  dialog
        findViewById(R.id.iv_download_dialog_close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onViewClickListener != null) {
                    onViewClickListener.onCancel();
                }
            }
        });

        // start download
        findViewById(R.id.alivc_download_start).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onViewClickListener != null) {
                    onViewClickListener.onDownload(downLoadTag);
                }
            }
        });

        // native video
        findViewById(R.id.alivc_current_download).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onShowVideoListLisener != null) {
                    onShowVideoListLisener.onShowVideo();
                }
            }
        });
    }

    public void onPrepared(List<AliyunDownloadMediaInfo> infos) {
        //第二步 ； 准备结束
        showAllDownloadItems(infos);
    }

    private void showAllDownloadItems(List<AliyunDownloadMediaInfo> list) {
        if (list == null || list.isEmpty()) {
            FixedToastUtils.show(getContext().getApplicationContext(), R.string.no_download_right);
            return;
        }

        Log.d("demo", "list size = " + list.size());
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT, 1f);
        layoutParams.setMargins(0, 0, DensityUtil.px2dip(getContext(), 16), 0);
        int infoSize = list.size();
        Iterator<AliyunDownloadMediaInfo> iterator = list.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            AliyunDownloadMediaInfo info = iterator.next();
            String encript = info.isEncripted() == 1 ? getContext().getString(R.string.encrypted)
                    : getContext().getString(R.string.encrypted_no);
            RadioButton item = (RadioButton) LayoutInflater.from(getContext()).inflate(R.layout.view_item_quality,
                    new FrameLayout(getContext()), false);

            item.setLayoutParams(layoutParams);
            item.setText(qualityList.get(info.getQuality()));
            item.setTag(info);
            ////设置id，供自动化测试用
            int id = R.id.custom_id_min + i;
            item.setId(id);
            rgQualityList.addView(item);
            i++;
        }

        if (rgQualityList.getChildCount() > 0) {
            int checkId = rgQualityList.getChildAt(0).getId();
            rgQualityList.check(checkId);
            downLoadTag = (AliyunDownloadMediaInfo) rgQualityList.findViewById(checkId).getTag();
        }
        rgQualityList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rbChecked = findViewById(checkedId);
                if (rbChecked == null) {
                    FixedToastUtils.show(getContext().getApplicationContext(), R.string.choose_a_definition_to_download);
                    return;
                }

                downLoadTag = (AliyunDownloadMediaInfo) rbChecked.getTag();
                tvAddDownloadViewSize.setText(formatSizeDecimal(downLoadTag.getSize()));
            }
        });

        new ImageLoader(ivVideoCover).loadAsync(list.get(0).getCoverUrl());
        tvAddDownloadViewTitle.setText(list.get(0).getTitle());
        tvAddDownloadViewSize.setText(formatSizeDecimal(list.get(0).getSize()));

    }

    private String formatSize(long size) {
        int kb = (int) (size / 1024f);
        if (kb < 1024) {
            return kb + "KB";
        }

        int mb = (int) (kb / 1024f);
        return mb + "MB";
    }

    /**
     * 视频大小格式化,这里由于要和IOS同步,所以先四舍五入保留两位小数,再四舍五入保留一位小数
     */
    private String formatSizeDecimal(long size) {
        float kb = (size / 1024 * 1.0f);
        BigDecimal bigDecimal = new BigDecimal(kb);

        if (kb < 1024) {
            return String.format("%.1f", bigDecimal.setScale(2, RoundingMode.HALF_UP)) + "KB";
        }

        float mb = (kb / 1024 * 1.0f);
        BigDecimal decimal = new BigDecimal(mb);
        return String.format("%.1f", decimal.setScale(2, RoundingMode.HALF_UP)) + "MB";
    }

    private void findAllViews() {
        ivVideoCover = (ImageView) findViewById(R.id.iv_video_cover);
        tvAddDownloadViewTitle = (TextView) findViewById(R.id.tv_add_download_view_title);
        tvAddDownloadViewSize = (TextView) findViewById(R.id.tv_add_download_view_size);
        downloadBtn = (Button) findViewById(R.id.download);

        //wrapCheckGroup = (WrapCheckGroup) findViewById(R.id.quality_list);
        rgQualityList = findViewById(R.id.rg_quality_list);
        rgQualityList.removeAllViews();
        //wrapCheckGroup.removeCheckBox();

        downloadBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadItem();
            }
        });

        findViewById(R.id.iv_download_dialog_close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onViewClickListener != null) {
                    onViewClickListener.onCancel();
                }
            }
        });
    }

    private void downloadItem() {
        if (onViewClickListener != null) {
            onViewClickListener.onDownload(downLoadTag);
        }
    }

    private OnViewClickListener onViewClickListener;

    public void setOnViewClickListener(OnViewClickListener l) {
        onViewClickListener = l;
    }

    public interface OnViewClickListener {
        void onCancel();

        void onDownload(AliyunDownloadMediaInfo info);
    }

    private OnShowNativeVideoBtnClickListener onShowVideoListLisener;

    public void setOnShowVideoListLisener(
            OnShowNativeVideoBtnClickListener listener) {
        this.onShowVideoListLisener = listener;
    }

    /**
     * 横屏状态下, 点击查看离线视频按钮回调接口
     */
    public interface OnShowNativeVideoBtnClickListener {
        void onShowVideo();
    }
}
