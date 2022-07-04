package com.aliyun.player.alivcplayerexpand.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

public class LongVideoBean implements Serializable {

    /*
        title	        string		视频标题
        videoId	        string		视频id
        description	    string		视频描述
        duration	    string		视频时长（秒）
        coverUrl	    string		视频封面URL
        status	        string		视频状态
        firstFrameUrl	string		首帧地址
        size	        string		视频源文件大小（字节）。
        tags	        string		视频标签.多个用逗号分隔。
        tvId	        string		电视剧id。
        tvName	        string		电视剧名称。
        dot	            string		打点信息。
        sort	        string		序号。
        creationTime	string		创建时间
        transcodeStatus	string		转码状态。
        snapshotStatus	string		截图状态
        censorStatus	string		审核状态
        isRecommend	    string		是否推荐（是：true，否：false）
        isHomePage	    string		是否首页（是：true，否：false）
        isVip	        string		是否vip（是：true，否：false）
        watchDuration   String      观看时长 (毫秒)
        watchPercent    int         观看历史百分比
        saveUrl    String         本地视频地址

     */
    private String title;
    private String videoId;
    private String description;
    private String duration;
    private String coverUrl;
    private String status;
    private String firstFrameUrl;
    private String size;
    private String tags;
    private String tvId;
    private String tvName;
    private List<DotBean> dotList;
    private String sort;
    private String total;
    private String creationTime;
    private String transcodeStatus;
    private String snapshotStatus;
    private String censorStatus;
    private String isRecommend;
    private String isHomePage;
    private boolean isVip;
    private boolean downloading;
    private boolean downloaded;
    private boolean selected;
    private String watchDuration;
    private int watchPercent;
    private String saveUrl;
    private String tvCoverUrl;

    public String getSaveUrl() {
        return saveUrl;
    }

    public void setSaveUrl(String saveUrl) {
        this.saveUrl = saveUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFirstFrameUrl() {
        return firstFrameUrl;
    }

    public void setFirstFrameUrl(String firstFrameUrl) {
        this.firstFrameUrl = firstFrameUrl;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTvId() {
        return tvId;
    }

    public void setTvId(String tvId) {
        this.tvId = tvId;
    }

    public String getTvName() {
        return tvName;
    }

    public void setTvName(String tvName) {
        this.tvName = tvName;
    }

    public List<DotBean> getDot() {
        return dotList;
    }

    public void setDot(List<DotBean> dot) {
        this.dotList = dot;
    }

    public String getSort() {
        if (sort == null) {
            sort = "1";
        }
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getTranscodeStatus() {
        return transcodeStatus;
    }

    public void setTranscodeStatus(String transcodeStatus) {
        this.transcodeStatus = transcodeStatus;
    }

    public String getSnapshotStatus() {
        return snapshotStatus;
    }

    public void setSnapshotStatus(String snapshotStatus) {
        this.snapshotStatus = snapshotStatus;
    }

    public String getCensorStatus() {
        return censorStatus;
    }

    public void setCensorStatus(String censorStatus) {
        this.censorStatus = censorStatus;
    }

    public String getIsRecommend() {
        return isRecommend;
    }

    public void setIsRecommend(String isRecommend) {
        this.isRecommend = isRecommend;
    }

    public String getIsHomePage() {
        return isHomePage;
    }

    public void setIsHomePage(String isHomePage) {
        this.isHomePage = isHomePage;
    }

    public boolean getIsVip() {
        return isVip;
    }

    public void setIsVip(boolean isVip) {
        this.isVip = isVip;
    }

    public boolean isDownloading() {
        return downloading;
    }

    public void setDownloading(boolean downloading) {
        this.downloading = downloading;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getWatchDuration() {
        return watchDuration;
    }

    public void setWatchDuration(String watchDuration) {
        this.watchDuration = watchDuration;
    }

    public int getWatchPercent() {
        return watchPercent;
    }

    public void setWatchPercent(int watchPercent) {
        this.watchPercent = watchPercent;
    }

    public String getTvCoverUrl() {
        return tvCoverUrl;
    }

    public void setTvCoverUrl(String tvCoverUrl) {
        this.tvCoverUrl = tvCoverUrl;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public boolean equals(Object obj) {
        if (TextUtils.isEmpty(this.getVideoId())) {
            return super.equals(obj);
        } else {
            LongVideoBean bean = (LongVideoBean) obj;
            return this.videoId.equals(bean.videoId);
        }

    }
}
