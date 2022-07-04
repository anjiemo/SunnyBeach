package com.aliyun.player.alivcplayerexpand.view.download;

import static com.aliyun.player.alivcplayerexpand.view.download.DownloadSection.DOWNLOADED_TAG;
import static com.aliyun.player.alivcplayerexpand.view.download.DownloadSection.DOWNLOADING_TAG;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.player.alivcplayerexpand.util.FixedToastUtils;
import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadMediaInfo;
import com.aliyun.player.alivcplayerexpand.view.sectionlist.SectionedRecyclerViewAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 离线下载的界面 该界面中包含下载列表, 列表的item编辑(全选, 删除),  Empty空数据显示等操作
 *
 * @author Mulberry create on 2018/4/12.
 */
public class DownloadView extends FrameLayout implements OnClickListener, CompoundButton.OnCheckedChangeListener {
    private SectionedRecyclerViewAdapter sectionAdapter;
    private RecyclerView downloadListView;
    private LinearLayout downloadEmptyView;
    private ImageView ivDownloadDelete;
    private ImageView ivCloseEdit;
    private RelativeLayout rlDownloadManagerContent;
    private RelativeLayout rlDownloadManagerEdit;
    private RelativeLayout rlDownloadManagerEditDefault;

    private ArrayList<AlivcDownloadMediaInfo> alivcDownloadMediaInfos;
    private ArrayList<AlivcDownloadMediaInfo> alivcDownloadingMediaInfos;
    private WeakReference<Context> context;
    private CheckBox cbAllDownloadCheck;
    private LinearLayoutManager linearLayoutManager;
    private DownloadSection section;
    /**
     * 离线视频列表
     * 点击item,根据item 的状态改变全选的选中/未选中状态
     */
    private boolean itemFollowCheckBox = true;
    /**
     * 用于判断当前是否处于编辑状态
     */
    private boolean isEditeState = false;

    public boolean isEditeState() {
        return isEditeState;
    }

    public void setEditeState(boolean editeState) {
        isEditeState = editeState;
    }

    public DownloadView(@NonNull Context context) {
        super(context);
        this.context = new WeakReference<Context>(context);

        initView();
    }

    public DownloadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = new WeakReference<Context>(context);
        initView();
    }

    public DownloadView(@NonNull Context context, @Nullable AttributeSet attrs,
                        int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = new WeakReference<Context>(context);
        initView();
    }

    private void initView() {
        LayoutInflater.from(this.context.get()).inflate(R.layout.alivc_download_view_layout, this, true);
        downloadEmptyView = (LinearLayout) findViewById(R.id.alivc_layout_empty_view);
        downloadListView = (RecyclerView) findViewById(R.id.download_list_view);
        linearLayoutManager = new LinearLayoutManager(getContext());
        downloadListView.setLayoutManager(linearLayoutManager);

        rlDownloadManagerContent = (RelativeLayout) findViewById(R.id.rl_download_manager_content);
        rlDownloadManagerEdit = (RelativeLayout) findViewById(R.id.rl_download_manager_edit);
        rlDownloadManagerEditDefault = (RelativeLayout) findViewById(R.id.rl_download_manager_edit_default);
        ivDownloadDelete = (ImageView) findViewById(R.id.iv_download_delete);
        ivCloseEdit = (ImageView) findViewById(R.id.iv_close_edit);

        cbAllDownloadCheck = findViewById(R.id.checkbox_all_select);

        cbAllDownloadCheck.setOnCheckedChangeListener(this);

        ivDownloadDelete.setOnClickListener(this);
        rlDownloadManagerEditDefault.setOnClickListener(this);
        ivCloseEdit.setOnClickListener(this);
        sectionAdapter = new SectionedRecyclerViewAdapter();
        alivcDownloadMediaInfos = new ArrayList<>();
        alivcDownloadingMediaInfos = new ArrayList<>();
        downloadListView.setItemAnimator(null);
        downloadListView.setAdapter(sectionAdapter);

        sectionAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                if (isEditeState()) {
                    boolean selectAllChecked = cbAllDownloadCheck.isChecked();
                    for (AlivcDownloadMediaInfo info : alivcDownloadMediaInfos) {
                        boolean checkedState = info.isCheckedState();
                        if (checkedState) {
                            continue;
                        } else {
                            if (selectAllChecked) {
                                itemFollowCheckBox = false;
                                cbAllDownloadCheck.setChecked(false);
                            }
                            return;
                        }
                    }

                    for (AlivcDownloadMediaInfo info : alivcDownloadingMediaInfos) {
                        boolean checkedState = info.isCheckedState();
                        if (checkedState) {
                            continue;
                        } else {
                            if (selectAllChecked) {
                                itemFollowCheckBox = false;
                                cbAllDownloadCheck.setChecked(false);
                            }
                            return;
                        }
                    }

                    cbAllDownloadCheck.setChecked(true);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.rl_download_manager_edit_default) {
            changeDownloadEditState(true);
            isEditeState = true;
            cbAllDownloadCheck.setChecked(false);
        } else if (i == R.id.iv_download_delete) {
            ArrayList<AlivcDownloadMediaInfo> deleteDownloadMediaInfos = new ArrayList<>();
            for (Iterator<AlivcDownloadMediaInfo> it = alivcDownloadingMediaInfos.iterator(); it.hasNext(); ) {
                AlivcDownloadMediaInfo val = it.next();
                if (val.isCheckedState()) {

                    deleteDownloadMediaInfos.add(val);
                }
            }

            for (Iterator<AlivcDownloadMediaInfo> it = alivcDownloadMediaInfos.iterator(); it.hasNext(); ) {
                AlivcDownloadMediaInfo val = it.next();
                if (val.isCheckedState()) {
                    deleteDownloadMediaInfos.add(val);
                }
            }
            if (onDownloadViewListener != null) {
                if (deleteDownloadMediaInfos.size() <= 0) {
                    Context context = this.context.get();
                    if (context != null) {
                        FixedToastUtils.show(context, getResources().getString(R.string.alivc_not_choose_video));
                    }
                } else {
                    onDownloadViewListener.onDeleteDownloadInfo(deleteDownloadMediaInfos);
                }
            }

        } else if (i == R.id.iv_close_edit) {
            setEditeState(false);
            changeDownloadEditState(false);
            isEditeState = false;
        }
    }

    public void deleteDownloadInfo() {
        changeDownloadEditState(false);
        isEditeState = false;

        for (Iterator<AlivcDownloadMediaInfo> it = alivcDownloadingMediaInfos.iterator(); it.hasNext(); ) {
            AlivcDownloadMediaInfo val = it.next();
            if (val.isCheckedState()) {
                it.remove();
            }
        }
        for (Iterator<AlivcDownloadMediaInfo> it = alivcDownloadMediaInfos.iterator(); it.hasNext(); ) {
            AlivcDownloadMediaInfo val = it.next();
            if (val.isCheckedState()) {
                it.remove();
            }
        }
        if (alivcDownloadingMediaInfos.size() <= 0) {
            sectionAdapter.removeSection(DOWNLOADING_TAG);
        }

        if (alivcDownloadMediaInfos.size() <= 0) {
            sectionAdapter.removeSection(DOWNLOADED_TAG);
        }

        sectionAdapter.notifyDataSetChanged();
        showDownloadContentView();
    }

    /**
     * 删除单个视频
     */
    public void deleteDownloadInfo(AliyunDownloadMediaInfo downloadMediaInfo) {
        changeDownloadEditState(false);
        isEditeState = false;

        for (Iterator<AlivcDownloadMediaInfo> it = alivcDownloadingMediaInfos.iterator(); it.hasNext(); ) {
            AlivcDownloadMediaInfo val = it.next();
            AliyunDownloadMediaInfo mediaInfo = val.getAliyunDownloadMediaInfo();
            if (mediaInfo.getQuality().equals(downloadMediaInfo.getQuality()) &&
                    mediaInfo.getFormat().equals(downloadMediaInfo.getFormat()) &&
                    mediaInfo.getVid().equals(downloadMediaInfo.getVid())) {
                it.remove();
            }
        }
        for (Iterator<AlivcDownloadMediaInfo> it = alivcDownloadMediaInfos.iterator(); it.hasNext(); ) {
            AlivcDownloadMediaInfo val = it.next();
            AliyunDownloadMediaInfo mediaInfo = val.getAliyunDownloadMediaInfo();
            if (mediaInfo.getQuality().equals(downloadMediaInfo.getQuality()) &&
                    mediaInfo.getFormat().equals(downloadMediaInfo.getFormat()) &&
                    mediaInfo.getVid().equals(downloadMediaInfo.getVid())) {
                it.remove();
            }
        }
        if (alivcDownloadingMediaInfos.size() <= 0) {
            sectionAdapter.removeSection(DOWNLOADING_TAG);
        }

        if (alivcDownloadMediaInfos.size() <= 0) {
            sectionAdapter.removeSection(DOWNLOADED_TAG);
        }

        sectionAdapter.notifyDataSetChanged();
        showDownloadContentView();
    }

    /**
     * 添加现在所有的
     *
     * @param alldownloadMediaInfos
     */
    public void addAllDownloadMediaInfo(List<AliyunDownloadMediaInfo> alldownloadMediaInfos) {
        if (alldownloadMediaInfos == null) {
            return;
        }

        // TODO: 2018/4/18 这不是一个正确的做法，做排序没有多大意义 业务需要永远保证正在下载的section在前
        //Collections.sort(alldownloadMediaInfos, new Comparator<AliyunDownloadMediaInfo>() {
        //    @Override
        //    public int compare(AliyunDownloadMediaInfo o1, AliyunDownloadMediaInfo o2) {
        //        if (o1.getStatus() == Status.Complete) {
        //            return 1;
        //        } else if (o2.getStatus() == Status.Complete){
        //            return 1;
        //        } else {
        //            return 0;
        //        }
        //    }
        //});

        for (AliyunDownloadMediaInfo downloadMediaInfo : alldownloadMediaInfos) {
            String tag = downloadMediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Complete ? DOWNLOADED_TAG : DOWNLOADING_TAG;
            String title = downloadMediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Complete ? getResources().getString(
                    R.string.already_downloaded) : getResources().getString(R.string.download_caching);
            if (downloadMediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Complete
                    || (downloadMediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Start && downloadMediaInfo.getProgress() == 100)) {
                AlivcDownloadMediaInfo alivcDownloadMediaInfo = new AlivcDownloadMediaInfo();
                alivcDownloadMediaInfo.setAliyunDownloadMediaInfo(downloadMediaInfo);
                alivcDownloadMediaInfos.add(0, alivcDownloadMediaInfo);
                addSection(tag, title, alivcDownloadMediaInfos);
            } else {
                AlivcDownloadMediaInfo alivcDownloadMediaInfo = new AlivcDownloadMediaInfo();
                alivcDownloadMediaInfo.setAliyunDownloadMediaInfo(downloadMediaInfo);
                alivcDownloadingMediaInfos.add(0, alivcDownloadMediaInfo);
                addSection(tag, title, alivcDownloadingMediaInfos);
            }
        }

        sectionAdapter.notifyDataSetChanged();

        showDownloadContentView();

    }

    /**
     * 添加一个item
     *
     * @param downloadMedia 要添加的item
     */
    public void addDownloadMediaInfo(AliyunDownloadMediaInfo downloadMedia) {
        if (hasAdded(downloadMedia)) {
            return;
        }
        String tag = downloadMedia.getStatus() == AliyunDownloadMediaInfo.Status.Complete ? DOWNLOADED_TAG : DOWNLOADING_TAG;
        String title = downloadMedia.getStatus() == AliyunDownloadMediaInfo.Status.Complete ? getResources().getString(
                R.string.already_downloaded) : getResources().getString(R.string.download_caching);

        if (downloadMedia.getStatus() == AliyunDownloadMediaInfo.Status.Complete) {
            AlivcDownloadMediaInfo alivcDownloadMediaInfo = new AlivcDownloadMediaInfo();
            alivcDownloadMediaInfo.setEditState(isEditeState);
            alivcDownloadMediaInfo.setAliyunDownloadMediaInfo(downloadMedia);
            alivcDownloadMediaInfos.add(0, alivcDownloadMediaInfo);
            addSection(tag, title, alivcDownloadMediaInfos);
        } else {
            AlivcDownloadMediaInfo alivcDownloadMediaInfo = new AlivcDownloadMediaInfo();
            alivcDownloadMediaInfo.setEditState(isEditeState);
            alivcDownloadMediaInfo.setAliyunDownloadMediaInfo(downloadMedia);
            alivcDownloadingMediaInfos.add(0, alivcDownloadMediaInfo);
            addSection(tag, title, alivcDownloadingMediaInfos);
        }

        showDownloadContentView();
        sectionAdapter.notifyDataSetChanged();

    }

    private void addSection(String tag, String title, final ArrayList<AlivcDownloadMediaInfo> alivcDownloadMediaInfos) {
        if (sectionAdapter.getSection(tag) == null) {
            section = new DownloadSection(this.context.get(), tag, title, alivcDownloadMediaInfos);
            section.setOnSectionItemClickListener(new DownloadSection.OnSectionItemClickListener() {
                @Override
                public void onItemClick(int posion, String tag) {
                    int positionInSection = sectionAdapter.getPositionInSection(posion);
                    if (tag.equals(DOWNLOADING_TAG)) {
                        AlivcDownloadMediaInfo alivcDownloadMediaInfo = alivcDownloadingMediaInfos.get(positionInSection);
                        if (alivcDownloadMediaInfo.isEditState()) {
                            alivcDownloadMediaInfo.setCheckedState(!alivcDownloadMediaInfo.isCheckedState());
                            sectionAdapter.notifyItemChangedInSection(tag, positionInSection);
                            return;
                        }
                    } else if (tag.equals(DOWNLOADED_TAG)) {
                        AlivcDownloadMediaInfo alivcDownloadMediaInfo = alivcDownloadMediaInfos.get(positionInSection);
                        if (alivcDownloadMediaInfo.isEditState()) {
                            alivcDownloadMediaInfo.setCheckedState(!alivcDownloadMediaInfo.isCheckedState());
                            sectionAdapter.notifyItemChangedInSection(tag, positionInSection);
                            return;
                        }
                    }

                    if (tag.equals(DOWNLOADING_TAG)) {
                        AliyunDownloadMediaInfo aliyunDownloadMediaInfo = alivcDownloadingMediaInfos.get(positionInSection).getAliyunDownloadMediaInfo();
                        if (aliyunDownloadMediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Start) {
                            if (onDownloadViewListener != null) {
                                //aliyunDownloadMediaInfo.setStatus(Status.Stop);
                                onDownloadViewListener.onStop(aliyunDownloadMediaInfo);
                                sectionAdapter.notifyItemChangedInSection(tag, positionInSection);
                            }
                        } else if (aliyunDownloadMediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Stop) {
                            if (aliyunDownloadMediaInfo.getStatus() != AliyunDownloadMediaInfo.Status.Complete) {
                                //aliyunDownloadMediaInfo.setStatus(Status.Start);
                                onDownloadViewListener.onStart(aliyunDownloadMediaInfo);
                                sectionAdapter.notifyItemChangedInSection(tag, positionInSection);
                            }
                        }
                    }

                    if (tag.equals(DOWNLOADED_TAG)) {
                        // 点击播放下载完成的视频
                        if (onDownloadItemClickListener != null) {
                            onDownloadItemClickListener.onDownloadedItemClick(positionInSection);
                        }
                    }

                    if (tag.equals(DOWNLOADING_TAG)) {
                        // 点击下载中的视频
                        if (onDownloadItemClickListener != null) {
                            onDownloadItemClickListener.onDownloadingItemClick(alivcDownloadMediaInfos, positionInSection);
                        }
                    }
                }
            });

            sectionAdapter.addSection(tag, section);
        }
    }

    private OnDownloadItemClickListener onDownloadItemClickListener;

    public void setOnDownloadedItemClickListener(OnDownloadItemClickListener listener) {
        this.onDownloadItemClickListener = listener;
    }

    /**
     * 下载完成的视频item点击事件
     */
    public interface OnDownloadItemClickListener {
        void onDownloadedItemClick(int positin);

        void onDownloadingItemClick(ArrayList<AlivcDownloadMediaInfo> alivcDownloadMediaInfos, int position);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.checkbox_all_select) {
            if (!itemFollowCheckBox) {
                itemFollowCheckBox = true;
                return;
            }
            for (AlivcDownloadMediaInfo info : alivcDownloadMediaInfos) {
                if (info != null) {
                    info.setCheckedState(isChecked);
                }
            }

            for (AlivcDownloadMediaInfo info : alivcDownloadingMediaInfos) {
                if (info != null) {
                    info.setCheckedState(isChecked);
                }
            }
            sectionAdapter.notifyDataSetChanged();
        }
    }

    private OnNotifyItemCheckedListener onItemCheckAllListener;

    public void setOnItemCheckAllListener(OnNotifyItemCheckedListener listener) {
        this.onItemCheckAllListener = listener;
    }

    public interface OnNotifyItemCheckedListener {
        void onItemCheck(boolean isChecked);
    }

    public void removeDownloadingMeiaInfo(AliyunDownloadMediaInfo downloadingMedia) {
        for (AlivcDownloadMediaInfo info : alivcDownloadingMediaInfos) {
            AliyunDownloadMediaInfo mInfo = info.getAliyunDownloadMediaInfo();
            if (info.getAliyunDownloadMediaInfo().getVid().equals(downloadingMedia.getVid())
                    && info.getAliyunDownloadMediaInfo().getFormat().equals(downloadingMedia.getFormat())
                    && info.getAliyunDownloadMediaInfo().getQuality().equals(downloadingMedia.getQuality())) {
                alivcDownloadingMediaInfos.remove(info);
                break;
            }
        }

        if (alivcDownloadingMediaInfos.size() <= 0) {
            sectionAdapter.removeSection(DOWNLOADING_TAG);
        }
        sectionAdapter.notifyDataSetChanged();
    }

    /**
     * 判断是否已经存在
     *
     * @param info 下载媒体信息
     */
    public boolean hasAdded(AliyunDownloadMediaInfo info) {
        for (AlivcDownloadMediaInfo downloadMediaInfo : alivcDownloadingMediaInfos) {
            if (info.getFormat().equals(downloadMediaInfo.getAliyunDownloadMediaInfo().getFormat()) &&
                    info.getQuality().equals(downloadMediaInfo.getAliyunDownloadMediaInfo().getQuality()) &&
                    info.getVid().equals(downloadMediaInfo.getAliyunDownloadMediaInfo().getVid()) &&
                    info.isEncripted() == downloadMediaInfo.getAliyunDownloadMediaInfo().isEncripted()) {
                Context context = this.context.get();
                if (context != null) {
                    FixedToastUtils.show(context, context.getResources().getString(R.string.alivc_video_downloading_tips));
                }
                return true;
            }
        }
        for (AlivcDownloadMediaInfo alivcDownloadMediaInfo : alivcDownloadMediaInfos) {
            if (info.getFormat().equals(alivcDownloadMediaInfo.getAliyunDownloadMediaInfo().getFormat()) &&
                    info.getQuality().equals(alivcDownloadMediaInfo.getAliyunDownloadMediaInfo().getQuality()) &&
                    info.getVid().equals(alivcDownloadMediaInfo.getAliyunDownloadMediaInfo().getVid()) &&
                    info.isEncripted() == alivcDownloadMediaInfo.getAliyunDownloadMediaInfo().isEncripted()) {
                Context context = this.context.get();
                if (context != null) {
                    FixedToastUtils.show(context, context.getResources().getString(R.string.alivc_video_download_finish_tips));
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 更新item的值
     *
     * @param aliyunDownloadMediaInfo
     */
    public void updateInfo(AliyunDownloadMediaInfo aliyunDownloadMediaInfo) {
        AlivcDownloadMediaInfo tmpInfo = null;
        for (AlivcDownloadMediaInfo info : alivcDownloadingMediaInfos) {
            if (info.getAliyunDownloadMediaInfo().getVid().equals(aliyunDownloadMediaInfo.getVid()) &&
                    info.getAliyunDownloadMediaInfo().getQuality().equals(aliyunDownloadMediaInfo.getQuality()) &&
                    info.getAliyunDownloadMediaInfo().getFormat().equals(aliyunDownloadMediaInfo.getFormat())) {
                tmpInfo = info;
                break;
            }
        }

        if (tmpInfo != null) {
            //tmpInfo.getAliyunDownloadMediaInfo().setSavePath(aliyunDownloadMediaInfo.getSavePath());
            tmpInfo.getAliyunDownloadMediaInfo().setProgress(aliyunDownloadMediaInfo.getProgress());
            tmpInfo.getAliyunDownloadMediaInfo().setStatus(aliyunDownloadMediaInfo.getStatus());
        }

//        int sectionPosition = sectionAdapter.getSectionPosition(section);
//        sectionAdapter.notifyItemChanged(sectionPosition);
        sectionAdapter.notifyDataSetChanged();
    }

    public void updateProgress() {

    }

    /**
     * 切换为编辑状态
     */
    public void changeDownloadEditState(boolean isEdit) {
        for (AlivcDownloadMediaInfo info : alivcDownloadMediaInfos) {
            if (info != null) {
                info.setEditState(isEdit);
            }
        }

        for (AlivcDownloadMediaInfo info : alivcDownloadingMediaInfos) {
            if (info != null) {
                info.setEditState(isEdit);
            }
        }

        rlDownloadManagerEdit.setVisibility(isEdit ? View.VISIBLE : View.GONE);
        rlDownloadManagerEditDefault.setVisibility(isEdit ? View.GONE : View.VISIBLE);
        sectionAdapter.notifyDataSetChanged();
        //showDownloadContentView();
    }

    /**
     * 下载完成更新
     *
     * @param aliyunDownloadMediaInfo
     */
    public void updateInfoByComplete(AliyunDownloadMediaInfo aliyunDownloadMediaInfo) {
        if (aliyunDownloadMediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Complete) {
            removeDownloadingMeiaInfo(aliyunDownloadMediaInfo);
            addDownloadMediaInfo(aliyunDownloadMediaInfo);
        }
        showDownloadContentView();
        sectionAdapter.notifyDataSetChanged();

    }

    /**
     * 下载出错
     *
     * @param info
     */
    public void updateInfoByError(AliyunDownloadMediaInfo info) {
        if (info != null && info.getStatus() == AliyunDownloadMediaInfo.Status.Error) {
            sectionAdapter.notifyDataSetChanged();
            showDownloadContentView();
        }

    }

    /**
     * 根据是否有数据判断是否显示downloadEmptyView
     */
    public void showDownloadContentView() {
        if (alivcDownloadMediaInfos.size() > 0 || alivcDownloadingMediaInfos.size() > 0) {

            downloadEmptyView.setVisibility(View.GONE);
            rlDownloadManagerContent.setVisibility(View.VISIBLE);
//            rlDownloadManagerEdit.setVisibility(VISIBLE);
//            rlDownloadManagerEditDefault.setVisibility(VISIBLE);
        } else if (alivcDownloadMediaInfos.size() <= 0 || alivcDownloadingMediaInfos.size() <= 0) {

            downloadEmptyView.setVisibility(View.VISIBLE);
            rlDownloadManagerContent.setVisibility(View.GONE);
//            rlDownloadManagerEdit.setVisibility(GONE);
//            rlDownloadManagerEditDefault.setVisibility(GONE);

        }

        if (isEditeState()) {
            rlDownloadManagerEdit.setVisibility(VISIBLE);
            rlDownloadManagerEditDefault.setVisibility(GONE);
        } else {
            rlDownloadManagerEdit.setVisibility(GONE);
            rlDownloadManagerEditDefault.setVisibility(VISIBLE);
        }
    }

    private OnDownloadViewListener onDownloadViewListener;

    public void setOnDownloadViewListener(
            OnDownloadViewListener onDownloadViewListener) {
        this.onDownloadViewListener = onDownloadViewListener;
    }

    public interface OnDownloadViewListener {
        void onStop(AliyunDownloadMediaInfo downloadMediaInfo);

        void onStart(AliyunDownloadMediaInfo downloadMediaInfo);

        void onDeleteDownloadInfo(ArrayList<AlivcDownloadMediaInfo> alivcDownloadMediaInfos);
    }


    /**
     * 获取所有下载 MediaInfo 信息
     *
     * @return
     */
    public ArrayList<AlivcDownloadMediaInfo> getAllDownloadMediaInfo() {
        if (alivcDownloadMediaInfos == null) {
            alivcDownloadMediaInfos = new ArrayList<>();
        }
        return alivcDownloadMediaInfos;
    }

    /**
     * 获取所有下载 MediaInfo 信息
     */
    public ArrayList<AlivcDownloadMediaInfo> getDownloadMediaInfo() {
        ArrayList<AlivcDownloadMediaInfo> allDownloadMediaInfo = new ArrayList<>();
        if (alivcDownloadMediaInfos == null) {
            alivcDownloadMediaInfos = new ArrayList<>();
        }
        if (alivcDownloadingMediaInfos == null) {
            alivcDownloadMediaInfos = new ArrayList<>();
        }
        allDownloadMediaInfo.addAll(alivcDownloadMediaInfos);
        allDownloadMediaInfo.addAll(alivcDownloadingMediaInfos);
        return allDownloadMediaInfo;
    }

}
