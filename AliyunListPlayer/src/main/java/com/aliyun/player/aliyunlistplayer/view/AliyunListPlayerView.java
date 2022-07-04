package com.aliyun.player.aliyunlistplayer.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aliyun.player.AliListPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.aliyunlistplayer.R;
import com.aliyun.player.aliyunlistplayer.adapter.AliyunRecyclerViewAdapter;
import com.aliyun.player.aliyunlistplayer.adapter.PagerLayoutManager;
import com.aliyun.player.aliyunlistplayer.listener.OnViewPagerListener;
import com.aliyun.player.aliyunplayerbase.bean.AliyunVideoListBean;
import com.aliyun.player.aliyunplayerbase.bean.VideoSourceType;
import com.aliyun.player.aliyunplayerbase.net.ServiceCommon;
import com.aliyun.player.aliyunplayerbase.view.refresh.AlivcSwipeRefreshLayout;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.source.StsInfo;
import com.aliyun.svideo.common.utils.ToastUtils;

import java.util.Iterator;
import java.util.List;


public class AliyunListPlayerView extends FrameLayout {

    private View mListPlayerContainer;
    /**
     * play -- pause  icon
     */
    private ImageView mPlayIconImageView;
    private TextureView mListPlayerTextureView;
    private RecyclerViewEmptySupport mListPlayerRecyclerView;
    private AliyunRecyclerViewAdapter mRecyclerViewAdapter;
    private AliListPlayer mAliListPlayer;

    private StsInfo mStsInfo;
    private PagerLayoutManager mPagerLayoutManager;
    /**
     * 预加载位置, 默认离底部还有5条数据时请求下一页视频列表
     */
    private static final int DEFAULT_PRELOAD_NUMBER = 5;
    /**
     * 手势监听器
     */
    private GestureDetector mGestureDetector;
    /**
     * 播放资源UUID和index关联表
     */
    private SparseArray<String> mSparseArray;
    /**
     * 当前选中位置
     */
    private int mCurrentPosition;
    /**
     * 正常滑动，上一个被暂停的位置
     */
    private int mLastStopPosition = -1;
    /**
     * 数据是否到达最后一页
     */
    private boolean isEnd;
    /**
     * 是否在后台
     */
    private boolean mIsOnBackground;
    /**
     * 是否是暂停
     */
    private boolean mIsPause;
    /**
     * 刷新View
     */
    private AlivcSwipeRefreshLayout mRefreshView;
    /**
     * 是否正在刷新
     */
    private boolean mIsLoadingData;

    /**
     * 刷新数据listener
     */
    private OnRefreshDataListener onRefreshDataListener;
    private TextView mRefreshTextView;
    private List<AliyunVideoListBean.VideoDataBean.VideoListBean> mVideoListBean;

    public AliyunListPlayerView(Context context) {
        super(context);
        initVideoView();
    }

    public AliyunListPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVideoView();
    }

    public AliyunListPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView();
    }

    private void initVideoView() {
        //初始化列表播放器
        initListPlayer();
        //初始化播放界面和暂停/播放按钮
        initListPlayerView();
        //初始化PagerLayoutManager
        initPagerLayoutManager();
        //初始化滑动RecyclerView
        initRecyclerView();
    }

    /**
     * 初始化列表播放器
     */
    private void initListPlayer() {
        mAliListPlayer = AliPlayerFactory.createAliListPlayer(getContext());
        mAliListPlayer.setLoop(true);
        PlayerConfig config = mAliListPlayer.getConfig();
        config.mClearFrameWhenStop = true;
        mAliListPlayer.setConfig(config);
        mAliListPlayer.setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                if (!mIsPause && !mIsOnBackground) {
                    mAliListPlayer.start();
                }
            }
        });


        mAliListPlayer.setOnRenderingStartListener(new IPlayer.OnRenderingStartListener() {
            @Override
            public void onRenderingStart() {
                if (mListPlayerRecyclerView != null) {
                    AliyunRecyclerViewAdapter.MyViewHolder mViewHolder = (AliyunRecyclerViewAdapter.MyViewHolder) mListPlayerRecyclerView.findViewHolderForLayoutPosition(mCurrentPosition);
                    if (mViewHolder != null) {
                        mViewHolder.getCoverView().setVisibility(View.GONE);
                    }
                }
            }
        });

        mAliListPlayer.setOnInfoListener(new IPlayer.OnInfoListener() {
            @Override
            public void onInfo(InfoBean infoBean) {
            }
        });

        mAliListPlayer.setOnErrorListener(new IPlayer.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {
                ToastUtils.show(getContext(), errorInfo.getCode() + " --- " + errorInfo.getMsg());
            }
        });
    }

    /**
     * 初始化播放界面
     */
    private void initListPlayerView() {
        mListPlayerContainer = View.inflate(getContext(), R.layout.layout_list_player_view, null);

        mPlayIconImageView = mListPlayerContainer.findViewById(R.id.iv_play_icon);
        mListPlayerTextureView = mListPlayerContainer.findViewById(R.id.list_player_textureview);

        //TextureView
        mListPlayerTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                Surface mSurface = new Surface(surfaceTexture);
                if (mAliListPlayer != null) {
                    mAliListPlayer.setSurface(mSurface);
                    mAliListPlayer.redraw();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                if (mAliListPlayer != null) {
                    mAliListPlayer.redraw();
                }
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });

        //手势监听器
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                onPauseClick();
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });

        //播放列表界面的touch事件由手势监听器处理
        mListPlayerContainer.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return mGestureDetector.onTouchEvent(motionEvent);
            }
        });

    }

    /**
     * 初始化滑动RecyclerView
     */
    private void initRecyclerView() {
        View mListPlayerRecyclerViewRoot = LayoutInflater.from(getContext()).inflate(R.layout.layout_list_player_recyclerview, this, true);
        mListPlayerRecyclerView = mListPlayerRecyclerViewRoot.findViewById(R.id.list_player_recyclerview);
        mRefreshView = mListPlayerRecyclerViewRoot.findViewById(R.id.refresh_view);
        mRefreshTextView = mListPlayerRecyclerViewRoot.findViewById(R.id.tv_refresh);

        mRefreshView.setColorSchemeColors(Color.YELLOW, Color.GREEN, Color.BLUE, Color.RED);
        mRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (onRefreshDataListener != null) {
                    mIsLoadingData = true;
                    onRefreshDataListener.onRefresh();
                }
            }
        });

        mRefreshTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRefreshView != null) {
                    mRefreshView.setRefreshing(true);
                }
                if (onRefreshDataListener != null) {
                    mIsLoadingData = true;
                    onRefreshDataListener.onRefresh();
                }
            }
        });

        mListPlayerRecyclerView.setHasFixedSize(true);
        mListPlayerRecyclerView.setLayoutManager(mPagerLayoutManager);
        mListPlayerRecyclerView.setEmptyView(mListPlayerRecyclerViewRoot.findViewById(R.id.rl_empty_view));

        mRecyclerViewAdapter = new AliyunRecyclerViewAdapter(getContext());
        mListPlayerRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

    private void initPagerLayoutManager() {
        if (mPagerLayoutManager == null) {
            mPagerLayoutManager = new PagerLayoutManager(getContext());
            mPagerLayoutManager.setItemPrefetchEnabled(true);
        }

        if (mPagerLayoutManager.viewPagerListenerIsNull()) {
            mPagerLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {

                @Override
                public void onInitComplete() {
                    int position = mPagerLayoutManager.findFirstVisibleItemPosition();
                    if (position != -1) {
                        mCurrentPosition = position;
                    }
                    int itemCount = mRecyclerViewAdapter.getItemCount();
                    if (itemCount - position < DEFAULT_PRELOAD_NUMBER && !mIsLoadingData && !isEnd) {
                        // 正在加载中, 防止网络太慢或其他情况造成重复请求列表
                        mIsLoadingData = true;
                        loadMore();
                    }
                    startPlay(mCurrentPosition);
                    mLastStopPosition = -1;
                }

                @Override
                public void onPageRelease(boolean isNext, int position, View view) {
                    if (mCurrentPosition == position) {
                        mLastStopPosition = position;
                        stopPlay();
                        AliyunRecyclerViewAdapter.MyViewHolder holder = (AliyunRecyclerViewAdapter.MyViewHolder) mListPlayerRecyclerView.findViewHolderForLayoutPosition(position);
                        if (holder != null) {
                            holder.getCoverView().setVisibility(VISIBLE);
                        }
                    }

                }

                @Override
                public void onPageSelected(int position, boolean bottom, View view) {
                    //重新选中视频不播放，如果该位置被stop过则会重新播放视频
                    if (mCurrentPosition == position && mLastStopPosition != position) {
                        return;
                    }

                    int itemCount = mRecyclerViewAdapter.getItemCount();

                    if (itemCount == position + 1 && isEnd) {
                        Toast.makeText(getContext(), R.string.alivc_player_tip_last_video, Toast.LENGTH_SHORT).show();
                    }
                    if (itemCount - position < DEFAULT_PRELOAD_NUMBER && !mIsLoadingData && !isEnd) {
                        // 正在加载中, 防止网络太慢或其他情况造成重复请求列表
                        mIsLoadingData = true;
                        loadMore();
                    }
                    //开始播放选中视频
                    startPlay(position);
                    mCurrentPosition = position;
                }
            });
        }
    }

    /**
     * 加载更多
     */
    private void loadMore() {
        if (onRefreshDataListener != null) {
            onRefreshDataListener.onLoadMore();
        }
    }

    /**
     * 播放视频
     */
    private void startPlay(int position) {
        if (position < 0 || position > mVideoListBean.size()) {
            return;
        }
        //恢复界面状态
        mIsPause = false;
        mPlayIconImageView.setVisibility(View.GONE);

        AliyunRecyclerViewAdapter.MyViewHolder mViewHolder = (AliyunRecyclerViewAdapter.MyViewHolder) mListPlayerRecyclerView.findViewHolderForLayoutPosition(position);
        ViewParent parent = mListPlayerContainer.getParent();

        if (parent instanceof FrameLayout) {
            ((ViewGroup) parent).removeView(mListPlayerContainer);
        }
        if (mViewHolder != null) {
            mViewHolder.getContainerView().addView(mListPlayerContainer, 0);
        }

        //防止退出后台之后，再次调用start方法，导致视频播放
        if (!mIsOnBackground) {
            mAliListPlayer.moveTo(mSparseArray.get(position), mStsInfo);
        }
    }

    /**
     * 视频暂停/恢复的时候使用，
     */
    public void onPauseClick() {
        if (mIsPause) {
            resumePlay();
        } else {
            pausePlay();
        }
    }

    /**
     * 设置StsInfo
     */
    public void setStsInfo(StsInfo stsInfo) {
        this.mStsInfo = stsInfo;
    }

    /**
     * 开始播放
     */
    public void moveTo(String uuid) {
        if (mAliListPlayer != null) {
            mAliListPlayer.moveTo(uuid);
        }
    }

    public void moveTo(String uuid, StsInfo stsInfo) {
        if (mAliListPlayer != null) {
            mAliListPlayer.moveTo(uuid, stsInfo);
        }
    }

    /**
     * 添加视频
     */
    public void addVid(String videoId, String randomUUID) {
        if (mAliListPlayer != null) {
            mAliListPlayer.addVid(videoId, randomUUID);
        }
    }

    /**
     * 设置播放源
     */
    public void setData(List<AliyunVideoListBean.VideoDataBean.VideoListBean> videoListBeanItems) {
        clearNotShowVideo(videoListBeanItems);
        isEnd = false;
        mIsLoadingData = false;
        if (mRefreshView != null && mRefreshView.isRefreshing()) {
            mRefreshView.setRefreshing(false);
        }
        if (mRecyclerViewAdapter != null) {
            mRecyclerViewAdapter.setData(videoListBeanItems);
            mRecyclerViewAdapter.notifyDataSetChanged();
            this.mVideoListBean = videoListBeanItems;
        }
    }

    /**
     * 加载更多数据
     */
    public void addMoreData(List<AliyunVideoListBean.VideoDataBean.VideoListBean> videoListBeanItems) {
        if (videoListBeanItems == null || videoListBeanItems.size() < ServiceCommon.DEFAULT_PAGE_SIZE) {
            isEnd = true;
            return;
        } else {
            isEnd = false;
        }
        clearNotShowVideo(videoListBeanItems);
        mIsLoadingData = false;

        if (mRecyclerViewAdapter != null) {
            mRecyclerViewAdapter.addMoreData(videoListBeanItems);
        }
        hideRefresh();
    }

    /**
     * 清除不允许显示的视频
     */
    private void clearNotShowVideo(List<AliyunVideoListBean.VideoDataBean.VideoListBean> list) {
        Iterator<AliyunVideoListBean.VideoDataBean.VideoListBean> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().getSourceType() == VideoSourceType.TYPE_ERROR_NOT_SHOW) {
                it.remove();
            }
        }
    }

    public void showRefresh() {
        if (mRefreshView != null) {
            mRefreshView.setRefreshing(true);
        }
    }

    public void hideRefresh() {
        if (mRefreshView != null) {
            mRefreshView.setRefreshing(false);
        }
    }

    /**
     * 设置关联表
     */
    public void setCorrelationTable(SparseArray<String> sparseArray) {
        this.mSparseArray = sparseArray;
    }

    /**
     * 获取关联表
     */
    public SparseArray<String> getCorrelationTable() {
        return this.mSparseArray;
    }

    /**
     * 停止视频播放
     */
    private void stopPlay() {
        ViewParent parent = mListPlayerContainer.getParent();
        if (parent instanceof FrameLayout) {
            ((FrameLayout) parent).removeView(mListPlayerContainer);
        }
        mAliListPlayer.stop();
        mAliListPlayer.setSurface(null);
    }

    /**
     * activity不可见或者播放页面不可见时调用该方法
     */
    public void setOnBackground(boolean isOnBackground) {
        this.mIsOnBackground = isOnBackground;
        if (isOnBackground) {
            pausePlay();
        } else {
            resumePlay();
        }
    }

    /**
     * 暂停播放
     */
    private void pausePlay() {
        mIsPause = true;
        mPlayIconImageView.setVisibility(View.VISIBLE);
        mAliListPlayer.pause();
    }

    /**
     * 恢复播放
     */
    private void resumePlay() {
        mIsPause = false;
        mPlayIconImageView.setVisibility(View.GONE);
        mAliListPlayer.start();
    }

    /**
     * 销毁
     */
    public void destroy() {
        if (mAliListPlayer != null) {
            mAliListPlayer.stop();
            mAliListPlayer.release();
        }
    }

    /**
     * 刷新数据
     */
    public interface OnRefreshDataListener {
        /**
         * 下拉刷新
         */
        void onRefresh();

        /**
         * 上拉加载
         */
        void onLoadMore();
    }

    public void setOnRefreshDataListener(OnRefreshDataListener onRefreshDataListener) {
        this.onRefreshDataListener = onRefreshDataListener;
    }
}
