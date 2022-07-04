package com.aliyun.player.aliyunplayerbase.bean;

import java.util.List;

public class AliyunVideoListBean {
    /**
     * 视频审核状态，审核中
     */
    public static final String STATUS_CENSOR_ON = "onCensor";
    /**
     * 视频审核状态，待审核
     */
    public static final String STATUS_CENSOR_WAIT = "check";
    /**
     * 视频审核状态，审核通过
     */
    public static final String STATUS_CENSOR_SUCCESS = "success";
    /**
     * 视频审核状态，审核不通过
     */
    public static final String STATUS_CENSOR_FAIL = "fail";

    private String message;
    private int code;
    private VideoDataBean data;

    public static class VideoDataBean {
        private int total;
        private List<VideoListBean> videoList;

        public static class VideoListBean {
            /**
             * id
             */
            protected String id = "";
            private String videoId;
            private String title;
            private String status;
            private String coverUrl;
            private String censorStatus;
            private String firstFrameUrl;

            public VideoSourceType getSourceType() {
                if (STATUS_CENSOR_SUCCESS.equals(censorStatus)) {
                    return VideoSourceType.TYPE_STS;
                } else {
                    return VideoSourceType.TYPE_ERROR_NOT_SHOW;
                }
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getCensorStatus() {
                return censorStatus;
            }

            public void setCensorStatus(String censorStatus) {
                this.censorStatus = censorStatus;
            }

            public String getFirstFrameUrl() {
                return firstFrameUrl;
            }

            public void setFirstFrameUrl(String firstFrameUrl) {
                this.firstFrameUrl = firstFrameUrl;
            }

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

            public String getCoverUrl() {
                return coverUrl;
            }

            public void setCoverUrl(String coverUrl) {
                this.coverUrl = coverUrl;
            }

            public int getId() {
                int i = 0;
                try {
                    i = Integer.parseInt(id);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                return i;

            }

            public void setId(int id) {
                this.id = id + "";
            }

        }

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
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public VideoDataBean getData() {
        return data;
    }

    public void setData(VideoDataBean data) {
        this.data = data;
    }
}
