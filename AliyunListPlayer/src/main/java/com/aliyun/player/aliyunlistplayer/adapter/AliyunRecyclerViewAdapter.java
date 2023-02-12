package com.aliyun.player.aliyunlistplayer.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.player.aliyunlistplayer.R;
import com.aliyun.player.aliyunplayerbase.bean.AliyunVideoListBean;
import com.aliyun.svideo.common.utils.image.ImageLoaderImpl;
import com.aliyun.svideo.common.utils.image.ImageLoaderOptions;
import com.aliyun.svideo.common.utils.image.ImageLoaderRequestListener;

import java.util.List;

public class AliyunRecyclerViewAdapter extends RecyclerView.Adapter<AliyunRecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private Point mScreenPoint = new Point();
    private List<AliyunVideoListBean.VideoDataBean.VideoListBean> mVideoListBeanItems;

    public AliyunRecyclerViewAdapter(Context context) {
        this.mContext = context;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mScreenPoint.x = displayMetrics.widthPixels;
        mScreenPoint.y = displayMetrics.heightPixels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_list_player_recyclerview_item, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AliyunVideoListBean.VideoDataBean.VideoListBean videoListBean = mVideoListBeanItems.get(position);
        String coverUrlPath;
        if (TextUtils.isEmpty(videoListBean.getFirstFrameUrl())) {
            coverUrlPath = videoListBean.getFirstFrameUrl();
        } else {
            coverUrlPath = videoListBean.getCoverUrl();
        }
        ImageView mThumb = holder.getCoverView();

        if (mContext != null) {
            if (mContext instanceof Activity) {
                Activity activity = (Activity) mContext;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (!activity.isFinishing() || !activity.isDestroyed()) {
                        loadPicture(holder, coverUrlPath, mThumb);
                    }
                } else {
                    if (!activity.isFinishing()) {
                        loadPicture(holder, coverUrlPath, mThumb);
                    }
                }
            }
        }
    }

    private void loadPicture(final MyViewHolder holder, String coverPath, final ImageView iv) {
        new ImageLoaderImpl().loadImage(mContext, coverPath, new ImageLoaderOptions.Builder()
                .asBitmap()
                .placeholder(android.R.color.black)
                .thumbnail(0.1f)
                .build()
        ).listener(new ImageLoaderRequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(String exception, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, boolean isFirstResource) {
                float aspectRatio = (float) resource.getWidth() / resource.getHeight();
                float screenRatio = mScreenPoint.x / (float) mScreenPoint.y;
                if (aspectRatio <= (9f / 16f + 0.01) && aspectRatio >= (9f / 16f - 0.01) //考虑到float值不精确的原因取一个范围值 视频比例 = 9/16
                        && (screenRatio < 9f / 16f - 0.01) //屏幕宽高比例小于9/16(长手机)
                ) {
                    float height = holder.getContainerView().getHeight();
                    float width = height * resource.getWidth() / resource.getHeight();
                    ViewGroup.LayoutParams layoutParams = iv.getLayoutParams();
                    layoutParams.width = (int) width;
                    layoutParams.height = (int) height;
                    iv.setLayoutParams(layoutParams);

                } else {
                    //获取屏幕宽度
                    float screenWith = mScreenPoint.x;
                    ViewGroup.LayoutParams layoutParams = iv.getLayoutParams();
                    //获取imageview的高度
                    float height = screenWith * resource.getHeight() / resource.getWidth();
                    layoutParams.width = (int) screenWith;
                    layoutParams.height = (int) height;
                    iv.setLayoutParams(layoutParams);
                }
                return false;
            }
        }).into(iv);
    }

    @Override
    public int getItemCount() {
        return mVideoListBeanItems == null ? 0 : mVideoListBeanItems.size();
    }

    public void setData(List<AliyunVideoListBean.VideoDataBean.VideoListBean> videoListBeanItems) {
        this.mVideoListBeanItems = videoListBeanItems;
    }

    public void addMoreData(List<AliyunVideoListBean.VideoDataBean.VideoListBean> videoListBeanItems) {
        this.mVideoListBeanItems.addAll(videoListBeanItems);
        notifyItemRangeInserted(this.mVideoListBeanItems.size() - videoListBeanItems.size(), videoListBeanItems.size());
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView mThumb;
        private ViewGroup mRootView;
        private FrameLayout mPlayerViewRoot;

        public MyViewHolder(View itemView) {
            super(itemView);
            mThumb = itemView.findViewById(R.id.img_thumb);
            mPlayerViewRoot = itemView.findViewById(R.id.list_player_root);
            mRootView = itemView.findViewById(R.id.root_view);
        }

        public ImageView getCoverView() {
            return mThumb;
        }

        public ViewGroup getContainerView() {
            return mRootView;
        }
    }
}
