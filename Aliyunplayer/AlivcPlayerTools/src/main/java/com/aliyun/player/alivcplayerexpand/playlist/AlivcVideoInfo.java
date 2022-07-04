package com.aliyun.player.alivcplayerexpand.playlist;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Mulberry create on 2018/5/17.
 */

public class AlivcVideoInfo {

    private String result;
    private String requestId;
    private String message;
    private String code;
    private DataBean data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {

        private int total;
        private List<VideoListBean> videoList;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<VideoListBean> getVideoList() {
            return videoList;
        }

        public void setVideoList(List<VideoListBean> videoList) {
            this.videoList = videoList;
        }

        public static class VideoListBean {

            @SerializedName("creationTime")
            private String creationTime;
            @SerializedName("coverUrl")
            private String coverUrl;
            @SerializedName("status")
            private String status;
            @SerializedName("videoId")
            private String videoId;
            @SerializedName("duration")
            private String duration;
            @SerializedName("createTime")
            private String createTime;
            @SerializedName("Snapshots")
            private Snapshots snapshots;
            @SerializedName("modifyTime")
            private String modifyTime;
            @SerializedName("title")
            private String title;
            @SerializedName("size")
            private int size;
            @SerializedName("description")
            private String description;
            @SerializedName("cateName")
            private String cateName;
            @SerializedName("cateId")
            private int cateId;

            public String getVideoId() {
                return videoId;
            }

            public void setVideoId(String videoId) {
                this.videoId = videoId;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public String getDuration() {
                return duration;
            }

            public void setDuration(String duration) {
                this.duration = duration;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String getModifyTime() {
                return modifyTime;
            }

            public void setModifyTime(String modifyTime) {
                this.modifyTime = modifyTime;
            }


            public String getCreationTime() {
                return creationTime;
            }

            public void setCreationTime(String creationTime) {
                this.creationTime = creationTime;
            }

            public String getCoverUrl() {
                return coverUrl;
            }

            public void setCoverUrl(String coverUrl) {
                this.coverUrl = coverUrl;
            }

            public int getCateId() {
                return cateId;
            }

            public void setCateId(int cateId) {
                this.cateId = cateId;
            }

            public Object getCateName() {
                return cateName;
            }

            public void setCateName(String cateName) {
                this.cateName = cateName;
            }
        }

        private class Snapshots {
            private String[] snapshot;

            public String[] getSnapshot() {
                return snapshot;
            }

            public void setSnapshot(String[] snapshot) {
                this.snapshot = snapshot;
            }

            @Override
            public String toString() {
                return "ClassPojo [Snapshot = " + snapshot + "]";
            }
        }
    }
}
