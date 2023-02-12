package com.aliyun.vodplayerview.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadMediaInfo;
import com.aliyun.player.aliyunplayerbase.util.Formatter;
import com.aliyun.svideo.common.utils.image.ImageLoaderImpl;
import com.aliyun.svideo.common.utils.image.ImageLoaderOptions;
import com.aliyun.vodplayer.R;

import java.util.ArrayList;
import java.util.List;

public class AliyunPlayerDownloadListAdapter extends RecyclerView.Adapter<AliyunPlayerDownloadListAdapter.PlayerDownloadListViewHolder> {

    /**
     * 是否是可以编辑状态
     */
    private boolean mIsEditing = false;

    private List<AliyunDownloadMediaInfo> mMediaInfos = new ArrayList<>();

    private Context mContext;

    public AliyunPlayerDownloadListAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public PlayerDownloadListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(parent.getContext()).inflate(R.layout.alivc_player_download_cache_list_item, parent, false);
        return new PlayerDownloadListViewHolder(inflater);
    }

    @Override
    public void onBindViewHolder(final PlayerDownloadListViewHolder holder, int position) {
        /*item点击*/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(AliyunPlayerDownloadListAdapter.this, holder.itemView, holder.getAdapterPosition());
                }
            }
        });
        holder.ivPreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemChildClickListener != null) {
                    mOnItemChildClickListener.onItemChildClick(AliyunPlayerDownloadListAdapter.this, v, holder.getAdapterPosition());
                }

            }
        });
        holder.mFlFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemChildClickListener != null) {
                    mOnItemChildClickListener.onItemChildClick(AliyunPlayerDownloadListAdapter.this, v, holder.getAdapterPosition());
                }
            }
        });

        final AliyunDownloadMediaInfo downloadMediaInfo = mMediaInfos.get(position);
        //如果是100的话，就是完成状态
        if (downloadMediaInfo.getProgress() == 100) {
            downloadMediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Complete);
        }
        holder.tvSize.setText(Formatter.getFileSizeDescription(downloadMediaInfo.getSize()));
        String coverUrl = downloadMediaInfo.getCoverUrl();

        new ImageLoaderImpl().loadImage(mContext, coverUrl, new ImageLoaderOptions.Builder()
                .crossFade()
                .error(R.mipmap.ic_launcher)
                .build()).into(holder.ivPreView);

        switch (downloadMediaInfo.getStatus().ordinal()) {
            case 1:
                //prepare
                holder.progressBar.setVisibility(View.VISIBLE);
//                holder.tvStatus.setVisibility(View.GONE);
                holder.tvSize.setVisibility(View.GONE);
                holder.progressBar.setProgress(downloadMediaInfo.getProgress());
                holder.ivDownload.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.alivc_player_download_video));
                holder.ivPreView.setVisibility(View.GONE);
                holder.ivDownload.setVisibility(View.VISIBLE);
                holder.cacheLinearLayout.setVisibility(View.VISIBLE);
                holder.tvTitle.setVisibility(View.INVISIBLE);
                holder.mTvDownload.setText(mContext.getResources().getString(R.string.alivc_player_cache_wait));

                break;
            case 2:
                //wait
                holder.progressBar.setVisibility(View.VISIBLE);
//                holder.tvStatus.setVisibility(View.GONE);
                holder.tvSize.setVisibility(View.GONE);
                holder.progressBar.setProgress(downloadMediaInfo.getProgress());
                holder.ivDownload.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.alivc_player_download_video));
                holder.ivPreView.setVisibility(View.GONE);
                holder.ivDownload.setVisibility(View.VISIBLE);
                holder.cacheLinearLayout.setVisibility(View.VISIBLE);
                holder.tvTitle.setVisibility(View.INVISIBLE);
                holder.mTvDownload.setText(mContext.getResources().getString(R.string.alivc_player_cache_wait));
                break;
            case 3:
                //start
                holder.progressBar.setVisibility(View.VISIBLE);
//                holder.tvStatus.setVisibility(View.GONE);
                holder.tvSize.setVisibility(View.GONE);
                holder.progressBar.setProgress(downloadMediaInfo.getProgress());
                holder.ivDownload.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.alivc_player_download_video));
                holder.ivPreView.setVisibility(View.GONE);
                holder.ivDownload.setVisibility(View.VISIBLE);
                holder.cacheLinearLayout.setVisibility(View.VISIBLE);
                holder.tvTitle.setVisibility(View.INVISIBLE);
                holder.mTvDownload.setText(mContext.getResources().getString(R.string.alivc_player_cache_ing));

                break;
            case 4:
                //stop
                holder.progressBar.setVisibility(View.VISIBLE);
//                holder.tvStatus.setVisibility(View.GONE);
                holder.tvSize.setVisibility(View.GONE);
                holder.progressBar.setProgress(downloadMediaInfo.getProgress());
                holder.ivDownload.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.alivc_player_download_video));
                holder.ivPreView.setVisibility(View.GONE);
                holder.ivDownload.setVisibility(View.VISIBLE);
                holder.cacheLinearLayout.setVisibility(View.VISIBLE);
                holder.tvTitle.setVisibility(View.INVISIBLE);
                holder.mTvDownload.setText(R.string.alivc_player_cache_stop);

                break;
            case 5:
                //complete
                holder.progressBar.setVisibility(View.GONE);
//                holder.tvStatus.setVisibility(View.VISIBLE);
                holder.tvSize.setVisibility(View.VISIBLE);
                holder.ivPreView.setVisibility(View.VISIBLE);
                holder.ivDownload.setVisibility(View.GONE);
                holder.cacheLinearLayout.setVisibility(View.GONE);
                holder.tvTitle.setVisibility(View.VISIBLE);
                break;
            case 6:
                //error
                //100的时候已经下载完成了
                holder.progressBar.setVisibility(View.VISIBLE);
//                holder.tvStatus.setVisibility(View.GONE);
                holder.ivPreView.setVisibility(View.GONE);
                holder.ivDownload.setVisibility(View.VISIBLE);
                holder.cacheLinearLayout.setVisibility(View.VISIBLE);
                holder.tvTitle.setVisibility(View.INVISIBLE);
                holder.tvSubTitle.setVisibility(View.VISIBLE);
                holder.tvSize.setVisibility(View.GONE);
                holder.mTvDownload.setText(mContext.getResources().getString(R.string.alivc_player_cache_fail));


                break;
            case 8:
                //file
                break;
            default:
                break;
        }

        if (TextUtils.isEmpty(downloadMediaInfo.getTvId())) {
            //单集
            holder.tvSize.setText(Formatter.getFileSizeDescription(downloadMediaInfo.getSize()));
            holder.tvTitle.setText(downloadMediaInfo.getTitle());
            holder.tvSubTitle.setText(downloadMediaInfo.getTitle());
            //如果等于0，那么就是未观看
            if (downloadMediaInfo.getWatched() == 0) {
//                holder.tvStatus.setText(mContext.getResources().getString(R.string.alivc_longvideo_no_watch));
            } else {
//                holder.tvStatus.setText(mContext.getResources().getString(R.string.alivc_longvideo_is_watch));
            }

            new ImageLoaderImpl().loadImage(mContext, downloadMediaInfo.getCoverUrl(), new ImageLoaderOptions.Builder()
                    .crossFade()
                    .error(R.mipmap.ic_launcher)
                    .build()).into(holder.ivPreView);

        } else {
            //系列
            holder.progressBar.setVisibility(View.GONE);
//            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.ivPreView.setVisibility(View.VISIBLE);
            holder.ivDownload.setVisibility(View.GONE);
            holder.cacheLinearLayout.setVisibility(View.GONE);
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.tvSize.setText(Formatter.getFileSizeDescription(downloadMediaInfo.getSize()));
            if (downloadMediaInfo.getWatchNumber() == 0) {
//                holder.tvStatus.setText(String.format(mContext.getResources().getString(R.string.alivc_longvideo_video_cache_item_number_watch), String.valueOf(downloadMediaInfo.getNumber()), "0"));

            } else {
//                holder.tvStatus.setText(String.format(mContext.getResources().getString(R.string.alivc_longvideo_video_cache_item_number_watch), String.valueOf(downloadMediaInfo.getNumber()), String.valueOf(downloadMediaInfo.getWatchNumber())));
            }
            String tvName = downloadMediaInfo.getTvName();
            if (TextUtils.isEmpty(tvName)) {
                tvName = downloadMediaInfo.getTitle();
            }
            holder.tvTitle.setText(tvName);
            new ImageLoaderImpl().loadImage(mContext, downloadMediaInfo.getTvCoverUrl(), new ImageLoaderOptions.Builder()
                    .crossFade()
                    .error(R.mipmap.ic_launcher)
                    .build()).into(holder.ivPreView);
        }

        //     }
        //如果是编辑状态，显示按钮选项
        if (mIsEditing) {
            holder.ivSelector.setVisibility(View.VISIBLE);
        } else {
            holder.ivSelector.setVisibility(View.GONE);
        }
        if (downloadMediaInfo.isSelected()) {
            holder.ivSelector.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.alivc_player_cachevideo_selected));
        } else {
            holder.ivSelector.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.alivc_player_cachevideo_un_selected));

        }
    }

    @Override
    public int getItemCount() {
        return mMediaInfos == null ? 0 : mMediaInfos.size();
    }

    public void updateData(final AliyunDownloadMediaInfo mediaInfo) {
        int index = mMediaInfos.indexOf(mediaInfo);
        if (index >= 0 && index < mMediaInfos.size()) {
            /*如果状态已经是100的话，则赋值为完成状态*/
            if (mediaInfo.getProgress() == 100) {
                mMediaInfos.get(index).setStatus(AliyunDownloadMediaInfo.Status.Complete);
            } else {
                mMediaInfos.get(index).setStatus(mediaInfo.getStatus());
            }
            mMediaInfos.get(index).setProgress(mediaInfo.getProgress());
            notifyItemChanged(index, mediaInfo);
        }
    }

    public void deleteData(AliyunDownloadMediaInfo info) {
        int index = mMediaInfos.indexOf(info);
        if (index >= 0 && index < mMediaInfos.size()) {
            mMediaInfos.remove(index);
            notifyDataSetChanged();
        }
    }

    public void setData(List<AliyunDownloadMediaInfo> mediaInfo) {
        if (mMediaInfos != null) {
            this.mMediaInfos.clear();
            this.mMediaInfos.addAll(mediaInfo);
        }
        notifyDataSetChanged();
    }

    public List<AliyunDownloadMediaInfo> getDatas() {
        return mMediaInfos;
    }

    public void setEditing(boolean isEditing) {
        mIsEditing = isEditing;
        notifyDataSetChanged();
    }


    public static class PlayerDownloadListViewHolder extends RecyclerView.ViewHolder {

        //已看，未看状态
//        public TextView tvStatus;
        //数据大小
        public TextView tvSize;
        //标题
        public TextView tvTitle;
        //下载进度条
        public ProgressBar progressBar;
        //预览图
        public ImageView ivPreView;
        //选中与否
        public ImageView ivSelector;
        //下载图标
        public ImageView ivDownload;
        //正在缓存父布局
        public LinearLayout cacheLinearLayout;
        //副标题
        public TextView tvSubTitle;
        //
        public FrameLayout mFlFont;
        //下载状态
        public TextView mTvDownload;

        public PlayerDownloadListViewHolder(View itemView) {
            super(itemView);
            //已看，未看状态
//            tvStatus = itemView.findViewById(R.id.alivc_tv_status);
            //数据大小
            tvSize = itemView.findViewById(R.id.alivc_tv_size);
            //标题
            tvTitle = itemView.findViewById(R.id.alivc_tv_title);
            //下载进度条
            progressBar = itemView.findViewById(R.id.alivc_progress_bar_healthy);
            //预览图
            ivPreView = itemView.findViewById(R.id.alivc_iv_preview);
            //选中与否
            ivSelector = itemView.findViewById(R.id.alivc_iv_selector);
            //下载图标
            ivDownload = itemView.findViewById(R.id.alivc_iv_download);
            //正在缓存父布局
            cacheLinearLayout = itemView.findViewById(R.id.alivc_ll_cache);
            //副标题
            tvSubTitle = itemView.findViewById(R.id.alivc_tv_sub_title);
            mFlFont = itemView.findViewById(R.id.alivc_fl_font);
            //
            mTvDownload = itemView.findViewById(R.id.alivc_iv_loading);
        }
    }

    /**
     * 点击事件
     */
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {

        /**
         * Callback method to be invoked when an item in this RecyclerView has
         * been clicked.
         */
        void onItemClick(AliyunPlayerDownloadListAdapter adapter, View view, int position);
    }

    public void setOnItemChildClickListener(OnItemChildClickListener listener) {
        mOnItemChildClickListener = listener;
    }

    private OnItemChildClickListener mOnItemChildClickListener;

    public interface OnItemChildClickListener {
        /**
         * callback method to be invoked when an itemchild in this view has been click
         */
        void onItemChildClick(AliyunPlayerDownloadListAdapter adapter, View view, int position);
    }
}
