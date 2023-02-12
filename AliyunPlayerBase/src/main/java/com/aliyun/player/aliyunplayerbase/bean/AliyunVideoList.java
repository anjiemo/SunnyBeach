package com.aliyun.player.aliyunplayerbase.bean;

import java.util.List;

public class AliyunVideoList {

    private int code;
    private String message;
    private VideoList data;

    public class VideoList {

        private List<VideoListItem> playInfoList;
        private VideoBase videoBase;

        public class VideoListItem {
            private String playURL;
            private String definition;
            private String duration;
            private String format;
            private int encrypt;
            private String specification;
            private String coverURL;
            private String title;

            public String getPlayURL() {
                return playURL;
            }

            public void setPlayURL(String playURL) {
                this.playURL = playURL;
            }

            public String getDefinition() {
                return definition;
            }

            public void setDefinition(String definition) {
                this.definition = definition;
            }

            public String getDuration() {
                return duration;
            }

            public void setDuration(String duration) {
                this.duration = duration;
            }

            public String getFormat() {
                return format;
            }

            public void setFormat(String format) {
                this.format = format;
            }

            public int getEncrypt() {
                return encrypt;
            }

            public void setEncrypt(int encrypt) {
                this.encrypt = encrypt;
            }

            public String getSpecification() {
                return specification;
            }

            public void setSpecification(String specification) {
                this.specification = specification;
            }

            public String getCoverURL() {
                return coverURL;
            }

            public void setCoverURL(String coverURL) {
                this.coverURL = coverURL;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }

        public class VideoBase {
            private String coverURL;
            private String title;
            private String videoId;

            public String getCoverURL() {
                return coverURL;
            }

            public void setCoverURL(String coverURL) {
                this.coverURL = coverURL;
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

        public List<VideoListItem> getPlayInfoList() {
            return playInfoList;
        }

        public void setPlayInfoList(List<VideoListItem> playInfoList) {
            this.playInfoList = playInfoList;
        }

        public VideoBase getVideoBase() {
            return videoBase;
        }

        public void setVideoBase(VideoBase videoBase) {
            this.videoBase = videoBase;
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

    public VideoList getData() {
        return data;
    }

    public void setData(VideoList data) {
        this.data = data;
    }
}
