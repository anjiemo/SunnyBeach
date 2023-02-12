package com.aliyun.player.aliyunplayerbase.bean;

/**
 * 用户信息 javaBean
 */
public class AliyunUserInfo {

    private int code;
    private String message;

    private UserDataBean data;

    public static class UserDataBean {
        private String id;
        private String userId;
        private String nickName;
        private String avatarUrl;
        private String gmtCreate;
        private String gmtModified;
        private String token;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getGmtCreate() {
            return gmtCreate;
        }

        public void setGmtCreate(String gmtCreate) {
            this.gmtCreate = gmtCreate;
        }

        public String getGmtModified() {
            return gmtModified;
        }

        public void setGmtModified(String gmtModified) {
            this.gmtModified = gmtModified;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        @Override
        public String toString() {
            return "LittleUserInfo{" +
                    "id='" + id + '\'' +
                    ", userId='" + userId + '\'' +
                    ", nickName='" + nickName + '\'' +
                    ", avatarUrl='" + avatarUrl + '\'' +
                    ", gmtCreate='" + gmtCreate + '\'' +
                    ", gmtModified='" + gmtModified + '\'' +
                    ", token='" + token + '\'' +
                    '}';
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

    public UserDataBean getData() {
        return data;
    }

    public void setData(UserDataBean data) {
        this.data = data;
    }
}
