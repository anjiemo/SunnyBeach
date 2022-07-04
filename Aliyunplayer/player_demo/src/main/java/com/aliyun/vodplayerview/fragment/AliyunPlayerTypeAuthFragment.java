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
import com.aliyun.player.aliyunplayerbase.bean.AliyunPlayAuth;
import com.aliyun.player.aliyunplayerbase.net.GetAuthInformation;
import com.aliyun.svideo.common.utils.ToastUtils;
import com.aliyun.vodplayer.R;

import java.util.Locale;

/**
 * URL播放方式的Fragment
 */
public class AliyunPlayerTypeAuthFragment extends BaseFragment {

    /**
     * AUTH相关信息
     */
    private EditText mPlayAuthRegionEditText, mPlayAuthVidEditText, mPlayAuthEditText, mPlayAuthPreviewTimeEditText;

    private String mVid, mPlayAuth, mRegion, mPreviewTime;
    /**
     * 刷新
     */
    private TextView mRefreshTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aliyun_auth_player_type, container, false);
        mPlayAuthEditText = view.findViewById(R.id.et_auth);
        mRefreshTextView = view.findViewById(R.id.tv_refresh);
        mPlayAuthVidEditText = view.findViewById(R.id.et_auth_vid);
        mPlayAuthRegionEditText = view.findViewById(R.id.et_auth_region);
        mPlayAuthPreviewTimeEditText = view.findViewById(R.id.et_auth_preview_time);
        initData();
        initListener();
        if (TextUtils.isEmpty(GlobalPlayerConfig.mVid)) {
            defaultPlayInfo();
        }
        return view;
    }

    private void initData() {
        mPlayAuthEditText.setText(GlobalPlayerConfig.mPlayAuth);
        mPlayAuthVidEditText.setText(GlobalPlayerConfig.mVid);
        mPlayAuthRegionEditText.setText(GlobalPlayerConfig.mRegion);
        mPlayAuthPreviewTimeEditText.setText(String.format(Locale.getDefault(), "%d", GlobalPlayerConfig.mPreviewTime));
    }

    private void initListener() {
        mRefreshTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVid = mPlayAuthVidEditText.getText().toString();
                if (TextUtils.isEmpty(mVid)) {
                    ToastUtils.show(getContext(), R.string.alivc_refresh_vid_empty);
                    return;
                }
                getVideoPlayAuthInfoWithVideoId(mVid);
            }
        });
    }

    private void getVideoPlayAuthInfo() {
        GetAuthInformation getAuthInformation = new GetAuthInformation();
        getAuthInformation.getVideoPlayAuthInfo(new GetAuthInformation.OnGetPlayAuthInfoListener() {
            @Override
            public void onGetPlayAuthError(String msg) {
                ToastUtils.show(getContext(), msg);
            }

            @Override
            public void onGetPlayAuthSuccess(AliyunPlayAuth.PlayAuthBean dataBean) {
                if (dataBean != null) {
                    mPlayAuthEditText.setText(dataBean.getPlayAuth());
                    mPlayAuthVidEditText.setText(dataBean.getVideoMeta().getVideoId());
                    mPlayAuthRegionEditText.setText(GlobalPlayerConfig.mRegion);
                }
            }
        });
    }

    private void getVideoPlayAuthInfoWithVideoId(String videoId) {
        GetAuthInformation getAuthInformation = new GetAuthInformation();
        getAuthInformation.getVideoPlayAuthInfoWithVideoId(videoId, new GetAuthInformation.OnGetPlayAuthInfoListener() {
            @Override
            public void onGetPlayAuthError(String msg) {
                if (getContext() != null) {
                    ToastUtils.show(getContext(), msg);
                }

            }

            @Override
            public void onGetPlayAuthSuccess(AliyunPlayAuth.PlayAuthBean dataBean) {
                if (dataBean != null) {
                    mPlayAuthEditText.setText(dataBean.getPlayAuth());
                    mPlayAuthRegionEditText.setText(GlobalPlayerConfig.mRegion);
                }
            }
        });
    }

    private void getInputContent() {
        mVid = mPlayAuthVidEditText.getText().toString();
        mPlayAuth = mPlayAuthEditText.getText().toString();
        mRegion = mPlayAuthRegionEditText.getText().toString();
        mPreviewTime = mPlayAuthPreviewTimeEditText.getText().toString();
    }

    private void setGlobalConfig() {
        getInputContent();
        GlobalPlayerConfig.mVid = mVid;
        GlobalPlayerConfig.mRegion = mRegion;
        GlobalPlayerConfig.mPlayAuth = mPlayAuth;
        GlobalPlayerConfig.mPreviewTime = Integer.valueOf(TextUtils.isEmpty(mPreviewTime) ? "-1" : mPreviewTime);
    }

    @Override
    public void defaultPlayInfo() {
        getVideoPlayAuthInfo();
    }

    @Override
    public void confirmPlayInfo() {
        setGlobalConfig();
        GlobalPlayerConfig.AUTH_TYPE_CHECKED = true;
    }
}
