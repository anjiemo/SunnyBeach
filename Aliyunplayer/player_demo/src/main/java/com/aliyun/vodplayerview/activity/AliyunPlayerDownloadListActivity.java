package com.aliyun.vodplayerview.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig;
import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadInfoListener;
import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadManager;
import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadMediaInfo;
import com.aliyun.player.alivcplayerexpand.util.download.StorageUtil;
import com.aliyun.player.aliyunplayerbase.bean.AliyunPlayAuth;
import com.aliyun.player.aliyunplayerbase.bean.AliyunSts;
import com.aliyun.player.aliyunplayerbase.net.GetAuthInformation;
import com.aliyun.player.aliyunplayerbase.util.Formatter;
import com.aliyun.player.aliyunplayerbase.util.NetWatchdog;
import com.aliyun.player.bean.ErrorCode;
import com.aliyun.player.source.VidAuth;
import com.aliyun.player.source.VidSts;
import com.aliyun.svideo.common.utils.ToastUtils;
import com.aliyun.vodplayer.R;
import com.aliyun.vodplayerview.adapter.AliyunPlayerDownloadListAdapter;
import com.aliyun.vodplayerview.global.Global;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AliyunPlayerDownloadListActivity extends AppCompatActivity implements View.OnClickListener, AliyunDownloadInfoListener {

    public static final int DOWNLOAD_ACTIVITY_FOR_REQUEST_CODE = 0x0001;

    private static final String TAG = "AliyunPlayerDownloadLis";

    private AliyunDownloadManager mAliyunDownloadManager;

    /**
     * 底部显示缓存父布局
     */
    private FrameLayout mFlCacheBottom;
    /**
     * 底部显示状态父布局
     */
    private LinearLayout mLlStatusBottom;
    /**
     * 缓存列表RecyclerView
     */
    private RecyclerView mCacheVideoRecyclerView;
    /**
     * 缓存列表Adapter
     */
    private AliyunPlayerDownloadListAdapter mAliyunPlayerDownloadListAdapter;
    /**
     * 缓存大小
     */
    private TextView mCacheSizeTextView;
    /**
     * 缓存大小progress
     */
    private ProgressBar mCacheProgressBar;
    /**
     * 全选
     */
    private TextView mSelectedAllTextView;
    /**
     * 删除
     */
    private TextView mDeleteTextView;

    /**
     * 是否是可以编辑状态
     */
    private boolean mIsEditing = false;
    /**
     * 是否全选
     */
    private boolean mIsAllSelected = true;
    /**
     * 当前网络状态
     * true ,wifi,
     * false 4g
     */
    private boolean mIsNetWorkconnect = true;
    /**
     * 网络状态监听
     */
    private NetWatchdog mNetWatchdog;
    /**
     * 连网断网监听
     */
    private NetConnectedListener mNetConnectedListener = null;
    private TextView mTvRight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_download_list);
        mAliyunDownloadManager = AliyunDownloadManager.getInstance(getApplicationContext());
        mAliyunDownloadManager.addDownloadInfoListener(this);
        initView();
        initTitle();
        initRecyclerView();
        initNetWatchdog();
        initListener();
    }


    /**
     * setting common title
     */
    private void initTitle() {
        TextView tvTitle = findViewById(R.id.alivc_base_tv_middle_title);
        FrameLayout mFlLeftBack = findViewById(R.id.alivc_base_fl_left_back);
        mTvRight = findViewById(R.id.alivc_base_tv_right_edit);
        mTvRight.setOnClickListener(this);
        mFlLeftBack.setOnClickListener(this);
        tvTitle.setText(getResources().getString(R.string.alivc_player_cache_video_title));
        mTvRight.setText(getResources().getString(R.string.alivc_player_cache_video_edit));

    }

    private void initView() {
        mFlCacheBottom = findViewById(R.id.alivc_fl_cache_bottom);
        mLlStatusBottom = findViewById(R.id.alivc_fl_edit_bottom);
        mDeleteTextView = findViewById(R.id.alivc_tv_delete);
        mSelectedAllTextView = findViewById(R.id.alivc_tv_all_selected);
        mCacheSizeTextView = findViewById(R.id.alivc_tv_cache_size);
        mCacheProgressBar = findViewById(R.id.alivc_progress_bar_healthy);

        mCacheVideoRecyclerView = findViewById(R.id.alivc_cache_video_recyclerView);
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        mCacheVideoRecyclerView.setLayoutManager(linearLayoutManager);
        mAliyunPlayerDownloadListAdapter = new AliyunPlayerDownloadListAdapter(this);
        mCacheVideoRecyclerView.setAdapter(mAliyunPlayerDownloadListAdapter);
    }

    private void initData() {
        if (mAliyunPlayerDownloadListAdapter != null) {
            if (Global.mDownloadMediaLists == null) {
                Global.mDownloadMediaLists = new ArrayList<>();
            }
            mAliyunPlayerDownloadListAdapter.setData(Global.mDownloadMediaLists);
        }
        calculationCache();
        calculationTotal();
        changeEditType(false);
    }

    private void initNetWatchdog() {
        mNetWatchdog = new NetWatchdog(this);
        mNetWatchdog.setNetChangeListener(new MyNetChangeListener(this));
        mNetWatchdog.setNetConnectedListener(new MyNetConnectedListener(this));
    }

    private void initListener() {
        mSelectedAllTextView.setOnClickListener(this);
        mDeleteTextView.setOnClickListener(this);

        mAliyunPlayerDownloadListAdapter.setOnItemClickListener(new AliyunPlayerDownloadListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AliyunPlayerDownloadListAdapter adapter, View view, int position) {
                AliyunDownloadMediaInfo mediaInfo = adapter.getDatas().get(position);
                if (mIsEditing) {
                    //设置选中状态
                    mediaInfo.setSelected(!mediaInfo.isSelected());

                    //如果有单个未选中的话，则取消全选状态
                    for (int i = 0; i < adapter.getDatas().size(); i++) {
                        if (!adapter.getDatas().get(i).isSelected()) {
                            mIsAllSelected = false;
                            mSelectedAllTextView.setText(getResources().getString(R.string.alivc_player_download_video_all_selected));
                        }
                    }

                    adapter.notifyItemChanged(position, 1);
                    calculationTotal();
                }
            }
        });

        mAliyunPlayerDownloadListAdapter.setOnItemChildClickListener(new AliyunPlayerDownloadListAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(AliyunPlayerDownloadListAdapter adapter, View view, int position) {
                AliyunDownloadMediaInfo mediaInfo = adapter.getDatas().get(position);
                if (!mIsEditing) {
                    if (view.getId() == R.id.alivc_iv_preview) {
                        AliyunPlayerSkinActivity.startAliyunPlayerSkinActivityWithLocalVideo(AliyunPlayerDownloadListActivity.this, mediaInfo.getSavePath());
//                        //如果是未观看的视频,点击后设置已观看
//                        if (mediaInfo.getWatched() == 0) {
//                            mediaInfo.setWatched(1);
//                            mAliyunDownloadManager.updateDb(mediaInfo);
//                        }
                    } else if (view.getId() == R.id.alivc_fl_font) {

                        if (!mIsNetWorkconnect) {
                            Toast.makeText(AliyunPlayerDownloadListActivity.this, getResources().getString(R.string.alivc_player_doawload_operator), Toast.LENGTH_SHORT).show();
                        }
                        if (mediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Prepare || mediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Stop || mediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Error || mediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Wait) {
                            mAliyunDownloadManager.startDownload(mediaInfo);
                        } else {
                            mAliyunDownloadManager.pauseDownload(mediaInfo);
                        }
                    }

                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        //编辑 取消
        if (view.getId() == R.id.alivc_base_tv_right_edit) {
            if (Global.mDownloadMediaLists == null || Global.mDownloadMediaLists.isEmpty()) {
                return;
            }
            if (mIsEditing) {
                mIsEditing = false;
                mTvRight.setText(getResources().getString(R.string.alivc_player_cache_video_edit));
            } else {
                mIsEditing = true;
                mTvRight.setText(getResources().getString(R.string.alivc_common_cancel));
            }
            changeEditType(mIsEditing);
            //全选
        } else if (view.getId() == R.id.alivc_tv_all_selected) {
            allSelectedStatus();
            allSelectedOrUnSelected();
            //删除
        } else if (view.getId() == R.id.alivc_tv_delete) {
            deleteItem();
        } else if (view.getId() == R.id.alivc_base_fl_left_back) {
            finish();
        }
    }

    /**
     * 修改编辑状态
     */
    private void changeEditType(boolean mIsEditing) {
        statusAndCacheVisibility(mIsEditing);
        mAliyunPlayerDownloadListAdapter.setEditing(mIsEditing);
    }

    /**
     * 判断是否有网络的监听
     */
    public interface NetConnectedListener {
        /**
         * 网络已连接
         */
        void onReNetConnected(boolean isReconnect);

        /**
         * 网络未连接
         */
        void onNetUnConnected();
    }

    /**
     * 断网/连网监听
     */
    private class MyNetConnectedListener implements NetWatchdog.NetConnectedListener {
        public MyNetConnectedListener(AliyunPlayerDownloadListActivity alivcCacheVideoActivity) {
        }

        @Override
        public void onReNetConnected(boolean isReconnect) {
            if (mNetConnectedListener != null) {
                mNetConnectedListener.onReNetConnected(isReconnect);
            }
        }

        @Override
        public void onNetUnConnected() {
            if (mNetConnectedListener != null) {
                mNetConnectedListener.onNetUnConnected();
            }
        }
    }

    /**
     * 删除数据
     */
    private void deleteItem() {
        for (int i = 0; i < Global.mDownloadMediaLists.size(); i++) {
            if (Global.mDownloadMediaLists.get(i).isSelected()) {
                mAliyunDownloadManager.deleteFile(Global.mDownloadMediaLists.get(i));
            }
        }
        if (Global.mDownloadMediaLists.size() == 0) {
            changeEditType(false);
            mIsEditing = false;
        }
    }

    /**
     * 底部布局的显示隐藏
     */
    private void statusAndCacheVisibility(boolean isEditing) {
        mLlStatusBottom.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        mFlCacheBottom.setVisibility(isEditing ? View.GONE : View.VISIBLE);
    }

    /**
     * 全选反选文本,状态
     */
    private void allSelectedStatus() {
        if (mIsAllSelected) {
            mIsAllSelected = false;
            mSelectedAllTextView.setText(getResources().getString(R.string.alivc_player_download_video_all_selected));

        } else {
            mIsAllSelected = true;
            mSelectedAllTextView.setText(getResources().getString(R.string.alivc_player_download_video_un_all_selected));
        }
    }

    /**
     * 全选还是反选
     */
    private void allSelectedOrUnSelected() {
        ArrayList<AliyunDownloadMediaInfo> downloadMediaInfos = (ArrayList<AliyunDownloadMediaInfo>) mAliyunPlayerDownloadListAdapter.getDatas();

        for (int i = 0; i < downloadMediaInfos.size(); i++) {
            if (mIsAllSelected) {
                downloadMediaInfos.get(i).setSelected(true);
            } else {
                downloadMediaInfos.get(i).setSelected(false);
            }
        }
        mAliyunPlayerDownloadListAdapter.notifyDataSetChanged();
        calculationTotal();
    }

    /**
     * 计算选中的数量
     */
    private void calculationTotal() {
        int totalNumber = 0;
        if (mIsEditing) {
            for (int i = 0; i < mAliyunPlayerDownloadListAdapter.getDatas().size(); i++) {
                //如果是选中状态则计算数量
                if (mAliyunPlayerDownloadListAdapter.getDatas().get(i).isSelected()) {
                    totalNumber += mAliyunPlayerDownloadListAdapter.getDatas().get(i).getNumber();
                }
            }
            //恢复编辑前的状态
            if (totalNumber == 0) {
                mDeleteTextView.setTextColor(ContextCompat.getColor(AliyunPlayerDownloadListActivity.this, R.color.alivc_common_font_gray_333333));
                mDeleteTextView.setText(getResources().getString(R.string.alivc_player_download_video_delete));
            } else {
                mDeleteTextView.setTextColor(ContextCompat.getColor(AliyunPlayerDownloadListActivity.this, R.color.alivc_common_bg_red_darker));
                StringBuilder builder = new StringBuilder();
                builder.append(getResources().getString(R.string.alivc_player_download_video_delete))
                        .append("(")
                        .append(totalNumber)
                        .append(")");
                mDeleteTextView.setText(builder);
            }
        }
    }

    /**
     * 删除数据刷新界面
     */
    private void reFreshData(AliyunDownloadMediaInfo mediaInfo) {
        Global.mDownloadMediaLists.remove(mediaInfo);
        mAliyunPlayerDownloadListAdapter.deleteData(mediaInfo);
        //  如果列表为空，那么重置为编辑，并且不可点击，隐藏底部导航栏
        if (Global.mDownloadMediaLists.isEmpty()) {
            mIsEditing = false;
            mTvRight.setText(getResources().getString(R.string.alivc_player_cache_video_edit));
            statusAndCacheVisibility(mIsEditing);
        }
        calculationTotal();
        calculationCache();
    }

    /**
     * 计算缓存和可用空间
     */
    private void calculationCache() {
        long cacheSize = 0;

        ArrayList<AliyunDownloadMediaInfo> aliyunDownloadMediaInfos = (ArrayList<AliyunDownloadMediaInfo>) mAliyunPlayerDownloadListAdapter.getDatas();
        for (AliyunDownloadMediaInfo mediaInfo : aliyunDownloadMediaInfos) {
            cacheSize += mediaInfo.getSize();

        }
        //缓存大小和占用空间大小
        String intentUseStorage = Formatter.getFileSizeDescription(cacheSize);
        long size = StorageUtil.getAvailableExternalMemorySize();
        mCacheSizeTextView.setText(String.format(getResources().getString(R.string.alivc_player_video_cache_storage_tips), intentUseStorage, Formatter.getFileSizeDescription(size * 1024L)));
        //占据总容量的百分比
        int newSize = (int) ((cacheSize / 1024.0 / size) * 100);

        mCacheProgressBar.setProgress(newSize);
    }

    /**
     * 网络监听
     */
    private static class MyNetChangeListener implements NetWatchdog.NetChangeListener {

        private WeakReference<AliyunPlayerDownloadListActivity> viewWeakReference;

        public MyNetChangeListener(AliyunPlayerDownloadListActivity cacheVideoActivity) {
            viewWeakReference = new WeakReference<>(cacheVideoActivity);
        }

        @Override
        public void onWifiTo4G() {
            AliyunPlayerDownloadListActivity downloadListActivity = viewWeakReference.get();
            if (downloadListActivity != null) {
                downloadListActivity.onWifiTo4G();
            }
        }

        @Override
        public void on4GToWifi() {
            AliyunPlayerDownloadListActivity downloadListActivity = viewWeakReference.get();
            if (downloadListActivity != null) {
                downloadListActivity.on4GToWifi();
            }
        }

        @Override
        public void onNetDisconnected() {
            AliyunPlayerDownloadListActivity downloadListActivity = viewWeakReference.get();
            if (downloadListActivity != null) {
                downloadListActivity.onNetDisconnected();
            }
        }
    }

    /**
     * 切换到4g，不可下载，点击提示，可运营商下载或连接wifi
     * 如果允许4G下载，并且有权限，并且已经prepared成功，需要暂停之前的下载
     */
    private void onWifiTo4G() {
        mIsNetWorkconnect = false;
        Toast.makeText(this, getResources().getString(R.string.alivc_player_doawload_operator), Toast.LENGTH_SHORT).show();
        return;
    }

    /**
     * 切换到wifi，自动下载
     */
    private void on4GToWifi() {
        //如果已经显示错误了，那么就不用显示网络变化的提示了。
        mIsNetWorkconnect = true;
    }

    private void onNetDisconnected() {
        Toast.makeText(this, getResources().getString(R.string.alivc_net_disable), Toast.LENGTH_SHORT).show();
        //网络断开。
        // NOTE： 由于安卓这块网络切换的时候，有时候也会先报断开。所以这个回调是不准确的。
    }


    @Override
    protected void onStart() {
        super.onStart();
        initData();
        if (mNetWatchdog != null) {
            mNetWatchdog.startWatch();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAliyunPlayerDownloadListAdapter != null) {
            mAliyunPlayerDownloadListAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNetWatchdog.stopWatch();
        if (mAliyunDownloadManager != null) {
            mAliyunDownloadManager.removeDownloadInfoListener(this);
        }
    }

    /**
     * -------------------------------------- 下载相关的监听 --------------------------------------
     */
    @Override
    public void onPrepared(List<AliyunDownloadMediaInfo> infos) {
    }

    @Override
    public void onAdd(AliyunDownloadMediaInfo info) {

    }

    @Override
    public void onStart(AliyunDownloadMediaInfo info) {
        if (info == null) {
            return;
        }
        //开始下载状态更新
        mAliyunPlayerDownloadListAdapter.updateData(info);
    }

    @Override
    public void onProgress(AliyunDownloadMediaInfo info, int percent) {
        if (info == null) {
            return;
        }
        //更新对应数据的进度条
        mAliyunPlayerDownloadListAdapter.updateData(info);
    }

    @Override
    public void onStop(AliyunDownloadMediaInfo info) {
        if (info == null) {
            return;
        }
        mAliyunPlayerDownloadListAdapter.updateData(info);
    }

    @Override
    public void onCompletion(AliyunDownloadMediaInfo info) {
        //下载完成
        if (Global.mDownloadMediaLists != null && Global.mDownloadMediaLists.contains(info)) {
            AliyunDownloadMediaInfo aliyunDownloadMediaInfo = Global.mDownloadMediaLists.get(Global.mDownloadMediaLists.indexOf(info));
            aliyunDownloadMediaInfo.setSavePath(info.getSavePath());
        }
        mAliyunPlayerDownloadListAdapter.updateData(info);
    }

    @Override
    public void onError(AliyunDownloadMediaInfo info, ErrorCode code, String msg, String requestId) {
        if (info == null || code == null) {
            return;
        }
        if (code.getValue() == ErrorCode.ERROR_SERVER_POP_TOKEN_EXPIRED.getValue()
                || code.getValue() == ErrorCode.ERROR_SERVER_VOD_INVALIDAUTHINFO_EXPIRETIME.getValue()) {
            //鉴权过期
            refresh(info);
        } else {
            mAliyunPlayerDownloadListAdapter.updateData(info);
        }
        Log.e(TAG, "onError: " + info.getTitle() + "__" + msg);

    }

    @Override
    public void onWait(AliyunDownloadMediaInfo outMediaInfo) {

    }

    @Override
    public void onDelete(AliyunDownloadMediaInfo info) {
        reFreshData(info);
    }

    @Override
    public void onDeleteAll() {

    }

    @Override
    public void onFileProgress(AliyunDownloadMediaInfo info) {

    }

    /**
     * -------------------------------------- 下载相关的监听 --------------------------------------
     */

    private void refresh(final AliyunDownloadMediaInfo info) {
        if (info.getVidType() == AliyunDownloadManager.VID_STS) {
            GetAuthInformation getAuthInformation = new GetAuthInformation();
            getAuthInformation.getVideoPlayStsInfo(new GetAuthInformation.OnGetStsInfoListener() {
                @Override
                public void onGetStsError(String errorMsg) {
                    ToastUtils.show(AliyunPlayerDownloadListActivity.this, errorMsg);
                    mAliyunPlayerDownloadListAdapter.updateData(info);
                }

                @Override
                public void onGetStsSuccess(AliyunSts.StsBean dataBean) {
                    if (dataBean != null) {
                        VidSts vidSts = new VidSts();
                        vidSts.setVid(info.getVid());
                        vidSts.setAccessKeyId(dataBean.getAccessKeyId());
                        vidSts.setAccessKeySecret(dataBean.getAccessKeySecret());
                        vidSts.setSecurityToken(dataBean.getSecurityToken());
                        info.setVidSts(vidSts);
                        mAliyunDownloadManager.setmVidSts(vidSts);
                        mAliyunDownloadManager.prepareDownloadByQuality(info, AliyunDownloadManager.INTENT_STATE_START);
                    }
                }
            });
        } else if (info.getVidType() == AliyunDownloadManager.VID_AUTH) {
            GetAuthInformation getAuthInformation = new GetAuthInformation();
            getAuthInformation.getVideoPlayAuthInfo(new GetAuthInformation.OnGetPlayAuthInfoListener() {
                @Override
                public void onGetPlayAuthError(String msg) {
                    ToastUtils.show(AliyunPlayerDownloadListActivity.this, msg);
                    mAliyunPlayerDownloadListAdapter.updateData(info);
                }

                @Override
                public void onGetPlayAuthSuccess(AliyunPlayAuth.PlayAuthBean dataBean) {
                    if (dataBean != null) {
                        GlobalPlayerConfig.mPlayAuth = dataBean.getPlayAuth();

                        VidAuth vidAuth = new VidAuth();
                        vidAuth.setVid(info.getVid());
                        vidAuth.setPlayAuth(dataBean.getPlayAuth());
                        info.setVidAuth(vidAuth);
                        mAliyunDownloadManager.setmVidAuth(vidAuth);
                        mAliyunDownloadManager.prepareDownloadByQuality(info, AliyunDownloadManager.INTENT_STATE_START);
                    }
                }
            });
        }
    }
}
