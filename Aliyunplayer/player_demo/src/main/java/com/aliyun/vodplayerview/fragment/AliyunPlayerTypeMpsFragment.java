package com.aliyun.vodplayerview.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig;
import com.aliyun.player.aliyunplayerbase.bean.AliyunMps;
import com.aliyun.player.aliyunplayerbase.net.GetAuthInformation;
import com.aliyun.svideo.common.utils.ToastUtils;
import com.aliyun.vodplayer.R;

import java.util.Locale;

/**
 * URL播放方式的Fragment
 */
public class AliyunPlayerTypeMpsFragment extends BaseFragment {

    /**
     * MPS相关信息
     */
    private EditText mMpsVidEditText, mMpsRegionEditText, mMpsPlayDomainEditText, mMpsMtsHlsTokenEditText, mMpsPreviewTimeEditText,
            mMpsAccessKeyIdEditText, mMpsSecurityTokenEditText, mMpsAccessKeySecretEditText, mMpsAuthInfoEditText;

    private String mVid, mRegion, mPlayDomain, mMtsHlsToken, mPreviewTime, mAccessKeyId, mSecurityToken, mAccessKeySecret, mAuthInfo;
    /**
     * 刷新
     */
    private TextView mRefreshTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aliyun_mps_player_type, container, false);
        mMpsVidEditText = view.findViewById(R.id.et_mps_vid);
        mRefreshTextView = view.findViewById(R.id.tv_refresh);
        mMpsRegionEditText = view.findViewById(R.id.et_mps_region);
        mMpsAuthInfoEditText = view.findViewById(R.id.et_mps_auth_info);
        mMpsPlayDomainEditText = view.findViewById(R.id.et_mps_play_domain);
        mMpsMtsHlsTokenEditText = view.findViewById(R.id.et_mps_mts_hls_token);
        mMpsPreviewTimeEditText = view.findViewById(R.id.et_preview_time);
        mMpsAccessKeyIdEditText = view.findViewById(R.id.et_mps_access_key_id);
        mMpsSecurityTokenEditText = view.findViewById(R.id.et_mps_security_token);
        mMpsAccessKeySecretEditText = view.findViewById(R.id.et_mps_access_key_secret);
        initData();
        initListener();
        if (TextUtils.isEmpty(GlobalPlayerConfig.mVid)) {
            defaultPlayInfo();
        }
        return view;
    }

    private void getVideoPlayMpsInfo() {
        GetAuthInformation getAuthInformation = new GetAuthInformation();
        getAuthInformation.getVideoPlayMpsInfo(new GetAuthInformation.OnGetMpsInfoListener() {
            @Override
            public void onGetMpsError(String msg) {
                if (getContext() != null) {
                    ToastUtils.show(getContext(), msg);
                }
            }

            @Override
            public void onGetMpsSuccess(AliyunMps.MpsBean dataBean) {
                if (dataBean != null) {
                    mMpsVidEditText.setText(dataBean.getMediaId());
                    mMpsRegionEditText.setText(dataBean.getRegionId());
                    mMpsAuthInfoEditText.setText(dataBean.getAuthInfo());
                    mMpsMtsHlsTokenEditText.setText(dataBean.getHlsUriToken());
                    mMpsAccessKeyIdEditText.setText(dataBean.getAkInfo().getAccessKeyId());
                    mMpsSecurityTokenEditText.setText(dataBean.getAkInfo().getSecurityToken());
                    mMpsAccessKeySecretEditText.setText(dataBean.getAkInfo().getAccessKeySecret());
                }
            }
        });
    }

    private void getVideoPlayMpsInfoWithVideoId(String videoId) {
        GetAuthInformation getAuthInformation = new GetAuthInformation();
        getAuthInformation.getVideoPlayMpsInfoWithVideoId(videoId, new GetAuthInformation.OnGetMpsInfoListener() {
            @Override
            public void onGetMpsError(String msg) {
                ToastUtils.show(getContext(), msg);
            }

            @Override
            public void onGetMpsSuccess(AliyunMps.MpsBean dataBean) {
                if (dataBean != null) {
                    mMpsRegionEditText.setText(dataBean.getRegionId());
                    mMpsAuthInfoEditText.setText(dataBean.getAuthInfo());
                    mMpsMtsHlsTokenEditText.setText(dataBean.getHlsUriToken());
                    mMpsAccessKeyIdEditText.setText(dataBean.getAkInfo().getAccessKeyId());
                    mMpsSecurityTokenEditText.setText(dataBean.getAkInfo().getSecurityToken());
                    mMpsAccessKeySecretEditText.setText(dataBean.getAkInfo().getAccessKeySecret());
                }
            }
        });
    }

    private void initData() {
        mMpsVidEditText.setText(GlobalPlayerConfig.mVid);
        mMpsRegionEditText.setText(GlobalPlayerConfig.mMpsRegion);
        mMpsAuthInfoEditText.setText(GlobalPlayerConfig.mMpsAuthInfo);
        mMpsMtsHlsTokenEditText.setText(GlobalPlayerConfig.mMpsHlsUriToken);
        mMpsAccessKeyIdEditText.setText(GlobalPlayerConfig.mMpsAccessKeyId);
        mMpsSecurityTokenEditText.setText(GlobalPlayerConfig.mMpsSecurityToken);
        mMpsAccessKeySecretEditText.setText(GlobalPlayerConfig.mMpsAccessKeySecret);
        mMpsPreviewTimeEditText.setText(String.format(Locale.getDefault(), "%d", GlobalPlayerConfig.mPreviewTime));
    }

    private void initListener() {
        mRefreshTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVid = mMpsVidEditText.getText().toString();
                if (TextUtils.isEmpty(mVid)) {
                    ToastUtils.show(getContext(), R.string.alivc_refresh_vid_empty);
                    return;
                }
                getVideoPlayMpsInfoWithVideoId(mVid);
            }
        });
    }

    private void getInputContent() {
        mVid = mMpsVidEditText.getText().toString();
        mRegion = mMpsRegionEditText.getText().toString();
        mAuthInfo = mMpsAuthInfoEditText.getText().toString();
        mPlayDomain = mMpsPlayDomainEditText.getText().toString();
        mMtsHlsToken = mMpsMtsHlsTokenEditText.getText().toString();
        mPreviewTime = mMpsPreviewTimeEditText.getText().toString();
        mAccessKeyId = mMpsAccessKeyIdEditText.getText().toString();
        mSecurityToken = mMpsSecurityTokenEditText.getText().toString();
        mAccessKeySecret = mMpsAccessKeySecretEditText.getText().toString();

    }

    private void setGlobalConfig() {
        getInputContent();

        GlobalPlayerConfig.mVid = mVid;
        GlobalPlayerConfig.mMpsRegion = mRegion;
        GlobalPlayerConfig.mMpsAuthInfo = mAuthInfo;
        GlobalPlayerConfig.mMpsHlsUriToken = mMtsHlsToken;
        GlobalPlayerConfig.mMpsAccessKeyId = mAccessKeyId;
        GlobalPlayerConfig.mMpsSecurityToken = mSecurityToken;
        GlobalPlayerConfig.mMpsAccessKeySecret = mAccessKeySecret;
        GlobalPlayerConfig.mPreviewTime = Integer.valueOf(TextUtils.isEmpty(mPreviewTime) ? "-1" : mPreviewTime);
    }

    @Override
    public void defaultPlayInfo() {
        getVideoPlayMpsInfo();
    }

    @Override
    public void confirmPlayInfo() {
        setGlobalConfig();
        GlobalPlayerConfig.MPS_TYPE_CHECKED = true;
    }
}
