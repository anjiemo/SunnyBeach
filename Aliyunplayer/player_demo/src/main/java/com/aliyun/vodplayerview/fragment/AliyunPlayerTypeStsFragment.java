package com.aliyun.vodplayerview.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig;
import com.aliyun.player.aliyunplayerbase.bean.AliyunSts;
import com.aliyun.player.aliyunplayerbase.net.GetAuthInformation;
import com.aliyun.svideo.common.utils.ToastUtils;
import com.aliyun.vodplayer.R;

import java.util.Locale;

/**
 * URL播放方式的Fragment
 */
public class AliyunPlayerTypeStsFragment extends BaseFragment {

    /**
     * STS相关信息
     */
    private EditText mStsPreviewTimeEditText, mStsVidEditText, mStsRegionEditText, mStsAccessKeyIdEditText,
            mStsSecurityTokenEditText, mStsAccessKeySecretEditText;

    private String mVid, mRegion, mPreviewTime, mAccessKeyId, mSecurityToken, mAccessKeySecret;
    /**
     * 刷新
     */
    private TextView mRefreshTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aliyun_sts_player_type, container, false);
        mStsVidEditText = view.findViewById(R.id.et_sts_vid);
        mRefreshTextView = view.findViewById(R.id.tv_refresh);
        mStsRegionEditText = view.findViewById(R.id.et_sts_region);
        mStsPreviewTimeEditText = view.findViewById(R.id.et_preview_time);
        mStsAccessKeyIdEditText = view.findViewById(R.id.et_sts_access_key_id);
        mStsSecurityTokenEditText = view.findViewById(R.id.et_sts_security_token);
        mStsAccessKeySecretEditText = view.findViewById(R.id.et_sts_access_key_secret);
        initData();
        initListener();
        if (TextUtils.isEmpty(GlobalPlayerConfig.mVid)) {
            defaultPlayInfo();
        }
        return view;
    }

    private void getVideoPlayStsInfo() {
        GetAuthInformation getAuthInformation = new GetAuthInformation();
        getAuthInformation.getVideoPlayStsInfo(new GetAuthInformation.OnGetStsInfoListener() {
            @Override
            public void onGetStsError(String errorMsg) {
                Context context = getContext();
                if (context == null) {
                    return;
                }
                ToastUtils.show(context, errorMsg);
            }

            @Override
            public void onGetStsSuccess(AliyunSts.StsBean dataBean) {
                if (dataBean != null) {
                    mStsVidEditText.setText(dataBean.getVideoId());
                    mStsRegionEditText.setText(GlobalPlayerConfig.mRegion);
                    mStsAccessKeyIdEditText.setText(dataBean.getAccessKeyId());
                    mStsSecurityTokenEditText.setText(dataBean.getSecurityToken());
                    mStsAccessKeySecretEditText.setText(dataBean.getAccessKeySecret());
                }
            }
        });
    }

    private void getVideoPlayStsInfoWithVideoId(String videoId) {
        GetAuthInformation getAuthInformation = new GetAuthInformation();
        getAuthInformation.getVideoPlayStsInfoWithVideoId(videoId, new GetAuthInformation.OnGetStsInfoListener() {
            @Override
            public void onGetStsError(String errorMsg) {
                if (getContext() != null) {
                    ToastUtils.show(getContext(), errorMsg);
                }
            }

            @Override
            public void onGetStsSuccess(AliyunSts.StsBean dataBean) {
                if (dataBean != null) {
                    mStsRegionEditText.setText(GlobalPlayerConfig.mRegion);
                    mStsAccessKeyIdEditText.setText(dataBean.getAccessKeyId());
                    mStsSecurityTokenEditText.setText(dataBean.getSecurityToken());
                    mStsAccessKeySecretEditText.setText(dataBean.getAccessKeySecret());
                }
            }
        });
    }


    private void initData() {
        mStsVidEditText.setText(GlobalPlayerConfig.mVid);
        mStsRegionEditText.setText(GlobalPlayerConfig.mRegion);
        mStsAccessKeyIdEditText.setText(GlobalPlayerConfig.mStsAccessKeyId);
        mStsPreviewTimeEditText.setText(String.format(Locale.getDefault(), "%d", GlobalPlayerConfig.mPreviewTime));
        mStsSecurityTokenEditText.setText(GlobalPlayerConfig.mStsSecurityToken);
        mStsAccessKeySecretEditText.setText(GlobalPlayerConfig.mStsAccessKeySecret);
    }

    private void initListener() {
        mRefreshTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVid = mStsVidEditText.getText().toString();
                if (TextUtils.isEmpty(mVid)) {
                    ToastUtils.show(getContext(), R.string.alivc_refresh_vid_empty);
                    return;
                }
                getVideoPlayStsInfoWithVideoId(mVid);

            }
        });
    }

    private void getInputContent() {
        mVid = mStsVidEditText.getText().toString();
        mRegion = mStsRegionEditText.getText().toString();
        mPreviewTime = mStsPreviewTimeEditText.getText().toString();
        mAccessKeyId = mStsAccessKeyIdEditText.getText().toString();
        mSecurityToken = mStsSecurityTokenEditText.getText().toString();
        mAccessKeySecret = mStsAccessKeySecretEditText.getText().toString();
    }

    private void setGlobaConfig() {
        getInputContent();

        GlobalPlayerConfig.mVid = mVid;
        GlobalPlayerConfig.mRegion = mRegion;
        GlobalPlayerConfig.mPreviewTime = Integer.valueOf(TextUtils.isEmpty(mPreviewTime) ? "-1" : mPreviewTime);
        GlobalPlayerConfig.mStsAccessKeyId = mAccessKeyId;
        GlobalPlayerConfig.mStsSecurityToken = mSecurityToken;
        GlobalPlayerConfig.mStsAccessKeySecret = mAccessKeySecret;
    }

    @Override
    public void defaultPlayInfo() {
        getVideoPlayStsInfo();
    }

    @Override
    public void confirmPlayInfo() {
        setGlobaConfig();
        GlobalPlayerConfig.STS_TYPE_CHECKED = true;
    }
}
