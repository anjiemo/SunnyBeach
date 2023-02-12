package com.aliyun.player.aliyunlistplayer;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.player.aliyunlistplayer.view.AliyunListPlayerView;
import com.aliyun.player.aliyunplayerbase.bean.AliyunSts;
import com.aliyun.player.aliyunplayerbase.bean.AliyunVideoListBean;
import com.aliyun.player.aliyunplayerbase.net.GetVideoInfomation;
import com.aliyun.player.aliyunplayerbase.net.ServiceCommon;
import com.aliyun.player.aliyunplayerbase.util.NetWatchdog;
import com.aliyun.player.source.StsInfo;
import com.aliyun.svideo.common.okhttp.AlivcOkHttpClient;
import com.aliyun.svideo.common.utils.ToastUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;

import okhttp3.Request;

public class AliyunListPlayerActivity extends AppCompatActivity {

    private AliyunListPlayerView mListPlayerView;
    private NetWatchdog mNetWatchDog;
    private String mUserToken;
    private boolean mIsLoadMore = false;
    private int mLastVideoId = -1;
    private ImageView mBackImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aliyun_list_player);
        mUserToken = getIntent().getStringExtra("list_player_user_token");

        initView();
        initSts(true);
        initListener();
    }

    private void initView() {
        mListPlayerView = findViewById(R.id.list_player_view);
        mBackImageView = findViewById(R.id.iv_back);
    }

    private void refreshListDatas() {
        initSts(false);
        getDatas(mLastVideoId);
    }

    private void getDatas(int id) {
        GetVideoInfomation getVideoInfomation = new GetVideoInfomation();
        getVideoInfomation.getListPlayerVideoInfos(this, "1", mUserToken, id, new GetVideoInfomation.OnGetListPlayerVideoInfosListener() {
            private SparseArray<String> mSparseArray;

            @Override
            public void onGetSuccess(Request request, String result) {
                Gson gson = new Gson();
                AliyunVideoListBean aliyunVideoListBean = gson.fromJson(result, AliyunVideoListBean.class);
                if (aliyunVideoListBean != null && aliyunVideoListBean.getCode() == ServiceCommon.RESPONSE_SUCCESS) {
                    AliyunVideoListBean.VideoDataBean data = aliyunVideoListBean.getData();
                    int total = data.getTotal();
                    List<AliyunVideoListBean.VideoDataBean.VideoListBean> videoList = data.getVideoList();

                    if (mListPlayerView != null && videoList != null) {
                        if (!mIsLoadMore) {
                            mSparseArray = new SparseArray<>();
                            mListPlayerView.setData(videoList);
                        } else {
                            mSparseArray = mListPlayerView.getCorrelationTable();
                            mListPlayerView.addMoreData(videoList);
                        }
                        //遍历资源,添加到列表播放器当中
                        int size = mSparseArray.size();
                        for (int i = 0; i < videoList.size(); i++) {
                            if (i == videoList.size() - 1) {
                                mLastVideoId = videoList.get(i).getId();
                            }
                            String randomUUID = UUID.randomUUID().toString();
                            mListPlayerView.addVid(videoList.get(i).getVideoId(), randomUUID);
                            mSparseArray.put(size + i, randomUUID);
                        }
                        mListPlayerView.setCorrelationTable(mSparseArray);
                    }
                }

                if (mListPlayerView != null) {
                    mListPlayerView.hideRefresh();
                }
            }

            @Override
            public void onGetError(Request request, IOException e) {
                ToastUtils.show(AliyunListPlayerActivity.this, e.getMessage());
                if (mListPlayerView != null) {
                    mListPlayerView.hideRefresh();
                }
            }
        });
    }

    private void initData() {
        String listPlayerDatasJson = getIntent().getStringExtra("list_player_datas_json");
        if (!TextUtils.isEmpty(listPlayerDatasJson)) {
            Gson gson = new Gson();
            AliyunVideoListBean aliyunVideoListBean = gson.fromJson(listPlayerDatasJson, AliyunVideoListBean.class);
            if (aliyunVideoListBean != null && aliyunVideoListBean.getCode() == ServiceCommon.RESPONSE_SUCCESS) {
                AliyunVideoListBean.VideoDataBean data = aliyunVideoListBean.getData();
                int total = data.getTotal();
                List<AliyunVideoListBean.VideoDataBean.VideoListBean> videoList = data.getVideoList();

                if (mListPlayerView != null && videoList != null) {
                    SparseArray<String> mSparseArray = new SparseArray<>();
                    //遍历资源,添加到列表播放器当中
                    for (int i = 0; i < videoList.size(); i++) {
                        if (i == videoList.size() - 1) {
                            mLastVideoId = videoList.get(i).getId();
                        }
                        String randomUUID = UUID.randomUUID().toString();
                        mListPlayerView.addVid(videoList.get(i).getVideoId(), randomUUID);
                        mSparseArray.put(i, randomUUID);
                    }
                    mListPlayerView.setData(videoList);
                    mListPlayerView.setCorrelationTable(mSparseArray);
                }

            }
        }
    }

    private void initSts(final boolean needLoadData) {
        AlivcOkHttpClient.getInstance().get(ServiceCommon.GET_VIDEO_PLAY_STS, new AlivcOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.show(AliyunListPlayerActivity.this, e.getMessage());
            }

            @Override
            public void onSuccess(Request request, String result) {
                Gson gson = new Gson();
                AliyunSts aliyunSts = gson.fromJson(result, AliyunSts.class);
                if (aliyunSts != null && aliyunSts.getCode() == ServiceCommon.RESPONSE_SUCCESS) {
                    if (mListPlayerView != null) {
                        AliyunSts.StsBean data = aliyunSts.getData();
                        StsInfo stsInfo = new StsInfo();
                        stsInfo.setAccessKeyId(data.getAccessKeyId());
                        stsInfo.setSecurityToken(data.getSecurityToken());
                        stsInfo.setAccessKeySecret(data.getAccessKeySecret());
                        mListPlayerView.setStsInfo(stsInfo);
                        if (needLoadData) {
                            initData();
                        }
                    }

                }
            }
        });
    }

    private void initListener() {
        mNetWatchDog = new NetWatchdog(this);
        mNetWatchDog.setNetChangeListener(new MyNetChangeListener(this));
        mNetWatchDog.setNetConnectedListener(new MyNetConnectedListener(this));

        mListPlayerView.setOnRefreshDataListener(new MyOnRefreshListener(this));
        mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mListPlayerView != null) {
            mListPlayerView.setOnBackground(false);
        }
        if (mNetWatchDog != null) {
            mNetWatchDog.startWatch();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mListPlayerView != null) {
            mListPlayerView.setOnBackground(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mListPlayerView != null) {
            mListPlayerView.setOnBackground(true);
        }
        if (mNetWatchDog != null) {
            mNetWatchDog.stopWatch();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListPlayerView != null) {
            mListPlayerView.destroy();
        }
    }

    /**
     * 刷新数据监听
     */
    private static class MyOnRefreshListener implements AliyunListPlayerView.OnRefreshDataListener {

        private WeakReference<AliyunListPlayerActivity> weakReference;

        public MyOnRefreshListener(AliyunListPlayerActivity aliyunListPlayerActivity) {
            weakReference = new WeakReference<>(aliyunListPlayerActivity);
        }

        @Override
        public void onRefresh() {
            AliyunListPlayerActivity aliyunListPlayerActivity = weakReference.get();
            if (aliyunListPlayerActivity != null) {
                aliyunListPlayerActivity.onRefresh();
            }
        }

        @Override
        public void onLoadMore() {
            AliyunListPlayerActivity aliyunListPlayerActivity = weakReference.get();
            if (aliyunListPlayerActivity != null) {
                aliyunListPlayerActivity.onLoadMore();
            }
        }
    }

    private void onRefresh() {
        mIsLoadMore = false;
        mLastVideoId = -1;
        refreshListDatas();
    }

    private void onLoadMore() {
        mIsLoadMore = true;
        getDatas(mLastVideoId);
    }


    private static class MyNetChangeListener implements NetWatchdog.NetChangeListener {

        private WeakReference<AliyunListPlayerActivity> weakReference;

        public MyNetChangeListener(AliyunListPlayerActivity aliyunListPlayerActivity) {
            weakReference = new WeakReference<>(aliyunListPlayerActivity);
        }

        @Override
        public void onWifiTo4G() {
            AliyunListPlayerActivity aliyunListPlayerActivity = weakReference.get();
            if (aliyunListPlayerActivity != null) {
                aliyunListPlayerActivity.onWifiTo4G();
            }
        }

        @Override
        public void on4GToWifi() {
            AliyunListPlayerActivity aliyunListPlayerActivity = weakReference.get();
            if (aliyunListPlayerActivity != null) {
                aliyunListPlayerActivity.on4GToWifi();
            }
        }

        @Override
        public void onNetDisconnected() {
            AliyunListPlayerActivity aliyunListPlayerActivity = weakReference.get();
            if (aliyunListPlayerActivity != null) {
                aliyunListPlayerActivity.onNetDisconnected();
            }
        }
    }

    private void onNetDisconnected() {
    }

    private void on4GToWifi() {
    }

    private void onWifiTo4G() {
        ToastUtils.show(this, getString(R.string.alivc_operator_play));
    }

    private static class MyNetConnectedListener implements NetWatchdog.NetConnectedListener {

        private final WeakReference<AliyunListPlayerActivity> weakReference;

        public MyNetConnectedListener(AliyunListPlayerActivity aliyunLivePlayerView) {
            weakReference = new WeakReference<>(aliyunLivePlayerView);
        }

        @Override
        public void onReNetConnected(boolean isReconnect) {
            AliyunListPlayerActivity aliyunListPlayerActivity = weakReference.get();
            if (aliyunListPlayerActivity != null) {
                aliyunListPlayerActivity.onReNetConnected(isReconnect);
            }

        }

        @Override
        public void onNetUnConnected() {
            AliyunListPlayerActivity aliyunListPlayerActivity = weakReference.get();
            if (aliyunListPlayerActivity != null) {
                aliyunListPlayerActivity.onNetUnConnected();
            }

        }
    }

    private void onNetUnConnected() {
        ToastUtils.show(this, getString(R.string.alivc_player_net_unconnect));
    }

    private void onReNetConnected(boolean isReconnect) {
    }
}
