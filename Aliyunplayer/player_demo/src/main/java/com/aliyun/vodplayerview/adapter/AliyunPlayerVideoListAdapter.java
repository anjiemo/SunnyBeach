package com.aliyun.vodplayerview.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.player.alivcplayerexpand.playlist.AlivcVideoInfo;
import com.aliyun.svideo.common.utils.image.ImageLoaderImpl;
import com.aliyun.vodplayer.R;

import java.util.ArrayList;

public class AliyunPlayerVideoListAdapter extends RecyclerView.Adapter<AliyunPlayerVideoListAdapter.PlayerVideoListViewHolder> {

    private ArrayList<AlivcVideoInfo.DataBean.VideoListBean> mPlayInfoList;

    private Context mContext;

    public OnItemClickListener mOnItemClickListener;

    public AliyunPlayerVideoListAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public PlayerVideoListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.alivc_player_video_list_item, parent, false);
        return new PlayerVideoListViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final PlayerVideoListViewHolder holder, int position) {
        AlivcVideoInfo.DataBean.VideoListBean videoListBean = mPlayInfoList.get(position);
        holder.mVideoTitleTextView.setText(videoListBean.getTitle());
        String coverUrlPath = videoListBean.getCoverUrl();
        ImageView mThumb = holder.mVideoCoverImageView;

        if (mContext != null) {
            if (mContext instanceof Activity) {
                Activity activity = (Activity) mContext;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (!activity.isFinishing() || !activity.isDestroyed()) {
                        loadPicture(coverUrlPath, mThumb);
                    }
                } else {
                    if (!activity.isFinishing()) {
                        loadPicture(coverUrlPath, mThumb);
                    }
                }
            }
        }

        holder.mPlayListItemRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.getAdapterPosition());
                }
            }
        });
    }

    private void loadPicture(String coverPath, ImageView mThumb) {
        new ImageLoaderImpl().loadImage(mContext, coverPath).into(mThumb);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return mPlayInfoList == null ? 0 : mPlayInfoList.size();
    }

    public void setData(ArrayList<AlivcVideoInfo.DataBean.VideoListBean> mPlayInfoList) {
        this.mPlayInfoList = mPlayInfoList;
    }

    public class PlayerVideoListViewHolder extends RecyclerView.ViewHolder {

        private TextView mVideoTitleTextView;
        private ImageView mVideoCoverImageView;
        private TextView mVideoDescriptionTextView;
        private LinearLayout mPlayListItemRoot;

        public PlayerVideoListViewHolder(View itemView) {
            super(itemView);
            mVideoCoverImageView = itemView.findViewById(R.id.iv_cover);
            mVideoTitleTextView = itemView.findViewById(R.id.tv_video_title);
            mPlayListItemRoot = itemView.findViewById(R.id.ll_play_list_item_root);
            mVideoDescriptionTextView = itemView.findViewById(R.id.tv_video_description);

        }
    }


}
