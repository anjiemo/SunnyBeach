package com.aliyun.vodplayerview.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig;
import com.aliyun.player.alivcplayerexpand.util.RegularPatternUtil;
import com.aliyun.vodplayer.R;

import java.util.Locale;

/**
 * 参数配置界面
 */
public class AliyunPlayerConfigActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 使用此配置，使用默认配置
     */
    private TextView mConfirmTextView, mDefaultConfig;
    /**
     * 是否开启自定义缓存,是否开启SEI,停止是否显示最后帧
     */
    private Switch mEnableCacheSwitch, mEnableSeiSwitch, mEnableClearWhenStop;
    /**
     * 播放器配置 PlayerConfig
     */
    private EditText mMaxDelayTimeEditText, mHightBufferLevelEditText, mFirstStartBufferLevelEditText, mMaxBufferPacketDurationEditText,
            mNetWorkTimeOutEditText, mProbeSizeEditText, mReferrerEditText, mHttpProxyEditText, mRetryCountEditText;

    /**
     * 自定义缓存
     */
    private EditText mMaxDurationEditText, mMaxSizeEditText;
    private ImageView mBackImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_config);

        initView();
        initListener();
        initData();
    }

    private void initView() {
        mBackImageView = findViewById(R.id.iv_back);
        mDefaultConfig = findViewById(R.id.tv_default_config);
        mConfirmTextView = findViewById(R.id.tv_confirm_config);
        mEnableSeiSwitch = findViewById(R.id.switch_enable_sei);
        mEnableCacheSwitch = findViewById(R.id.switch_enable_cache);
        mEnableClearWhenStop = findViewById(R.id.switch_enable_clear_when_stop);

        mReferrerEditText = findViewById(R.id.et_referrer);
        mProbeSizeEditText = findViewById(R.id.et_probe_size);
        mHttpProxyEditText = findViewById(R.id.et_http_proxy);
        mRetryCountEditText = findViewById(R.id.et_retry_count);
        mMaxDelayTimeEditText = findViewById(R.id.et_max_delay_time);
        mNetWorkTimeOutEditText = findViewById(R.id.et_net_work_time_out);
        mHightBufferLevelEditText = findViewById(R.id.et_high_buffer_level);
        mFirstStartBufferLevelEditText = findViewById(R.id.et_first_start_buffer_level);
        mMaxBufferPacketDurationEditText = findViewById(R.id.et_max_buffer_packet_duration);

        mMaxSizeEditText = findViewById(R.id.et_max_size);
        mMaxDurationEditText = findViewById(R.id.et_max_duration);
    }

    private void initListener() {
        mBackImageView.setOnClickListener(this);
        mDefaultConfig.setOnClickListener(this);
        mConfirmTextView.setOnClickListener(this);
    }

    private void initData() {
        mReferrerEditText.setText(GlobalPlayerConfig.PlayConfig.mReferrer);
        mHttpProxyEditText.setText(GlobalPlayerConfig.PlayConfig.mHttpProxy);
        mProbeSizeEditText.setText(String.format(Locale.getDefault(), "%d", GlobalPlayerConfig.PlayConfig.mMaxProbeSize));

        mMaxDelayTimeEditText.setText(String.format(Locale.getDefault(), "%d", GlobalPlayerConfig.PlayConfig.mMaxDelayTime));
        mRetryCountEditText.setText(String.format(Locale.getDefault(), "%d", GlobalPlayerConfig.PlayConfig.mNetworkRetryCount));
        mNetWorkTimeOutEditText.setText(String.format(Locale.getDefault(), "%d", GlobalPlayerConfig.PlayConfig.mNetworkTimeout));
        mHightBufferLevelEditText.setText(String.format(Locale.getDefault(), "%d", GlobalPlayerConfig.PlayConfig.mHighBufferDuration));
        mFirstStartBufferLevelEditText.setText(String.format(Locale.getDefault(), "%d", GlobalPlayerConfig.PlayConfig.mStartBufferDuration));
        mMaxBufferPacketDurationEditText.setText(String.format(Locale.getDefault(), "%d", GlobalPlayerConfig.PlayConfig.mMaxBufferDuration));

        mEnableCacheSwitch.setChecked(GlobalPlayerConfig.PlayCacheConfig.mEnableCache);
        mEnableSeiSwitch.setChecked(GlobalPlayerConfig.PlayConfig.mEnableSei);
        mEnableClearWhenStop.setChecked(GlobalPlayerConfig.PlayConfig.mEnableClearWhenStop);
        mMaxSizeEditText.setText(String.format(Locale.getDefault(), "%d", GlobalPlayerConfig.PlayCacheConfig.mMaxSizeMB));
        mMaxDurationEditText.setText(String.format(Locale.getDefault(), "%d", GlobalPlayerConfig.PlayCacheConfig.mMaxDurationS));

        if (GlobalPlayerConfig.mUrlPath.startsWith("artp")) {
            //artp
            mMaxDelayTimeEditText.setText("100");
        } else if (GlobalPlayerConfig.mUrlPath.startsWith("artc")) {
            //artc
            mMaxDelayTimeEditText.setText("1000");
            mHightBufferLevelEditText.setText("10");
            mFirstStartBufferLevelEditText.setText("10");
        }
    }

    private void restoreData() {
        mReferrerEditText.setText("");
        mHttpProxyEditText.setText("");

        mProbeSizeEditText.setText(String.format(Locale.getDefault(), "%d", GlobalPlayerConfig.PlayConfig.DEFAULT_PROBE_SIZE));
        mMaxDelayTimeEditText.setText(String.format(Locale.getDefault(), "%d", GlobalPlayerConfig.PlayConfig.DEFAULT_MAX_DELAY_TIME));
        mRetryCountEditText.setText(String.format(Locale.getDefault(), "%d", GlobalPlayerConfig.PlayConfig.DEFAULT_NETWORK_RETRY_COUNT));
        mNetWorkTimeOutEditText.setText(String.format(Locale.getDefault(), "%d", GlobalPlayerConfig.PlayConfig.DEFAULT_NETWORK_TIMEOUT));
        mHightBufferLevelEditText.setText(String.format(Locale.getDefault(), "%d", GlobalPlayerConfig.PlayConfig.DEFAULT_HIGH_BUFFER_DURATION));
        mFirstStartBufferLevelEditText.setText(String.format(Locale.getDefault(), "%d", GlobalPlayerConfig.PlayConfig.DEFAULT_START_BUFFER_DURATION));
        mMaxBufferPacketDurationEditText.setText(String.format(Locale.getDefault(), "%d", GlobalPlayerConfig.PlayConfig.DEFAULT_MAX_BUFFER_DURATION));

        mEnableCacheSwitch.setChecked(GlobalPlayerConfig.PlayCacheConfig.DEFAULT_ENABLE_CACHE);
        mEnableSeiSwitch.setChecked(GlobalPlayerConfig.PlayConfig.DEFAULT_ENABLE_SEI);
        mEnableClearWhenStop.setChecked(GlobalPlayerConfig.PlayConfig.DEFAULT_ENABLE_CLEAR_WHEN_STOP);
        mMaxSizeEditText.setText(String.format(Locale.getDefault(), "%d", GlobalPlayerConfig.PlayCacheConfig.DEFAULT_MAX_SIZE_MB));
        mMaxDurationEditText.setText(String.format(Locale.getDefault(), "%d", GlobalPlayerConfig.PlayCacheConfig.DEFAULT_MAX_DURATION_S));
    }

    @Override
    public void onClick(View view) {
        if (view == mConfirmTextView) {
            saveConfig();
            finish();
        } else if (view == mDefaultConfig) {
            restoreData();
        } else if (view == mBackImageView) {
            finish();
        }
    }

    private void saveConfig() {
        GlobalPlayerConfig.PlayConfig.mReferrer = mReferrerEditText.getText().toString();
        GlobalPlayerConfig.PlayConfig.mHttpProxy = mHttpProxyEditText.getText().toString();

        String maxDelayTime = mMaxDelayTimeEditText.getText().toString();
        GlobalPlayerConfig.PlayConfig.mMaxDelayTime = TextUtils.isEmpty(maxDelayTime) ? GlobalPlayerConfig.PlayConfig.DEFAULT_MAX_DELAY_TIME : Integer.valueOf(maxDelayTime);
        String probeSize = mProbeSizeEditText.getText().toString();
        if (!RegularPatternUtil.isNumber(probeSize)) {
            probeSize = "-1";
        }
        GlobalPlayerConfig.PlayConfig.mMaxProbeSize = TextUtils.isEmpty(probeSize) ? 0 : Integer.valueOf(probeSize);
        String netWorkTimeOut = mNetWorkTimeOutEditText.getText().toString();
        GlobalPlayerConfig.PlayConfig.mNetworkTimeout = TextUtils.isEmpty(netWorkTimeOut) ? GlobalPlayerConfig.PlayConfig.DEFAULT_NETWORK_TIMEOUT : Integer.valueOf(netWorkTimeOut);
        String retryCount = mRetryCountEditText.getText().toString();
        GlobalPlayerConfig.PlayConfig.mNetworkRetryCount = TextUtils.isEmpty(retryCount) ? GlobalPlayerConfig.PlayConfig.DEFAULT_NETWORK_RETRY_COUNT : Integer.valueOf(retryCount);
        String maxBufferDuration = mMaxBufferPacketDurationEditText.getText().toString();
        GlobalPlayerConfig.PlayConfig.mMaxBufferDuration = TextUtils.isEmpty(maxBufferDuration) ? GlobalPlayerConfig.PlayConfig.DEFAULT_MAX_BUFFER_DURATION : Integer.valueOf(maxBufferDuration);
        String heightBufferLevel = mHightBufferLevelEditText.getText().toString();
        GlobalPlayerConfig.PlayConfig.mHighBufferDuration = TextUtils.isEmpty(heightBufferLevel) ? GlobalPlayerConfig.PlayConfig.DEFAULT_HIGH_BUFFER_DURATION : Integer.valueOf(heightBufferLevel);
        String firstStartBufferLevel = mFirstStartBufferLevelEditText.getText().toString();
        GlobalPlayerConfig.PlayConfig.mStartBufferDuration = TextUtils.isEmpty(firstStartBufferLevel) ? GlobalPlayerConfig.PlayConfig.DEFAULT_START_BUFFER_DURATION : Integer.valueOf(firstStartBufferLevel);
        GlobalPlayerConfig.PlayConfig.mEnableSei = mEnableSeiSwitch.isChecked();
        GlobalPlayerConfig.PlayConfig.mEnableClearWhenStop = mEnableClearWhenStop.isChecked();

        GlobalPlayerConfig.PlayCacheConfig.mEnableCache = mEnableCacheSwitch.isChecked();
        String maxSizeMB = mMaxSizeEditText.getText().toString();
        GlobalPlayerConfig.PlayCacheConfig.mMaxSizeMB = TextUtils.isEmpty(maxSizeMB) ? GlobalPlayerConfig.PlayCacheConfig.DEFAULT_MAX_SIZE_MB : Integer.valueOf(maxSizeMB);
        String maxDuration = mMaxDurationEditText.getText().toString();
        GlobalPlayerConfig.PlayCacheConfig.mMaxDurationS = TextUtils.isEmpty(maxDuration) ? GlobalPlayerConfig.PlayCacheConfig.DEFAULT_MAX_DURATION_S : Integer.valueOf(maxDuration);
    }
}
