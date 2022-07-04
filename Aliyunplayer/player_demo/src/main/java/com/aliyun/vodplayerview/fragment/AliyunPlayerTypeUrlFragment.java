package com.aliyun.vodplayerview.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig;
import com.aliyun.player.aliyunplayerbase.bean.AliyunVideoList;
import com.aliyun.player.aliyunplayerbase.net.GetAuthInformation;
import com.aliyun.svideo.common.utils.ToastUtils;
import com.aliyun.vodplayer.R;

import java.util.List;

/**
 * URL播放方式的Fragment
 */
public class AliyunPlayerTypeUrlFragment extends BaseFragment {

    private static final int REQ_CODE_PERMISSION = 0x1111;

    private EditText mUrlEditText;
    private String mUrlPath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aliyun_url_player_type, container, false);
        mUrlEditText = view.findViewById(R.id.et_url);
        initData();
        initListener();
        if (TextUtils.isEmpty(GlobalPlayerConfig.mUrlPath)) {
            defaultPlayInfo();
        }
        return view;
    }

    private void getPlayUrlInfo() {
        GetAuthInformation getAuthInformation = new GetAuthInformation();
        getAuthInformation.getVideoPlayUrlInfo(new GetAuthInformation.OnGetUrlInfoListener() {
            @Override
            public void onGetUrlError(String msg) {
                if (getContext() != null) {
                    ToastUtils.show(getContext(), msg);
                }
            }

            @Override
            public void onGetUrlSuccess(AliyunVideoList.VideoList dataBean) {
                if (dataBean != null) {
                    List<AliyunVideoList.VideoList.VideoListItem> playInfoList = dataBean.getPlayInfoList();
                    if (playInfoList != null && playInfoList.size() > 0) {
                        AliyunVideoList.VideoList.VideoListItem videoListItem = playInfoList.get(0);
                        mUrlEditText.setText(videoListItem.getPlayURL());
                        mUrlPath = videoListItem.getPlayURL();
                    }
                }
            }
        });
    }

    private void initData() {
        mUrlEditText.setText(GlobalPlayerConfig.mUrlPath);
    }

    private void initListener() {

    }

    @Override
    public void defaultPlayInfo() {
        getPlayUrlInfo();
    }

    @Override
    public void confirmPlayInfo() {
        mUrlPath = mUrlEditText.getText().toString();
        GlobalPlayerConfig.mUrlPath = mUrlPath;
        GlobalPlayerConfig.URL_TYPE_CHECKED = true;
    }
}
