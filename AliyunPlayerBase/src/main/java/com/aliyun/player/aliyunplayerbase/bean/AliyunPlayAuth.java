package com.aliyun.player.aliyunplayerbase.bean;

public class AliyunPlayAuth {

    private int code;
    private String message;
    private PlayAuthBean data;

    public class PlayAuthBean {
        private String playAuth;
        private VideoMetaBean videoMeta;

        public class VideoMetaBean {
            private String coverURL;
            private String duration;
            private String status;
            private String title;
            private String videoId;

            public String getCoverURL() {
                return coverURL;
            }

            public void setCoverURL(String coverURL) {
                this.coverURL = coverURL;
            }

            public String getDuration() {
                return duration;
            }

            public void setDuration(String duration) {
                this.duration = duration;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
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
        }

        public String getPlayAuth() {
            return playAuth;
        }

        public void setPlayAuth(String playAuth) {
            this.playAuth = playAuth;
        }

        public VideoMetaBean getVideoMeta() {
            return videoMeta;
        }

        public void setVideoMeta(VideoMetaBean videoMeta) {
            this.videoMeta = videoMeta;
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PlayAuthBean getData() {
        return data;
    }

    public void setData(PlayAuthBean data) {
        this.data = data;
    }
}
