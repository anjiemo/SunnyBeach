package com.aliyun.player.aliyunplayerbase.bean;

import com.google.gson.annotations.SerializedName;

public class AliyunMps {

    private int code;
    private String message;
    private MpsBean data;

    public class MpsBean {
        @SerializedName("MediaId")
        private String mediaId;
        @SerializedName("AkInfo")
        private AkInfoBean akInfo;
        @SerializedName("HlsUriToken")
        private String hlsUriToken;
        @SerializedName("RegionId")
        private String regionId;
        private String authInfo;

        public class AkInfoBean {
            @SerializedName("SecurityToken")
            private String securityToken;
            @SerializedName("AccessKeyId")
            private String accessKeyId;
            @SerializedName("AccessKeySecret")
            private String accessKeySecret;
            @SerializedName("Expiration")
            private String expiration;

            public String getSecurityToken() {
                return securityToken;
            }

            public void setSecurityToken(String securityToken) {
                this.securityToken = securityToken;
            }

            public String getAccessKeyId() {
                return accessKeyId;
            }

            public void setAccessKeyId(String accessKeyId) {
                this.accessKeyId = accessKeyId;
            }

            public String getAccessKeySecret() {
                return accessKeySecret;
            }

            public void setAccessKeySecret(String accessKeySecret) {
                this.accessKeySecret = accessKeySecret;
            }

            public String getExpiration() {
                return expiration;
            }

            public void setExpiration(String expiration) {
                this.expiration = expiration;
            }
        }

        public String getMediaId() {
            return mediaId;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

        public AkInfoBean getAkInfo() {
            return akInfo;
        }

        public void setAkInfo(AkInfoBean akInfo) {
            this.akInfo = akInfo;
        }

        public String getHlsUriToken() {
            return hlsUriToken;
        }

        public void setHlsUriToken(String hlsUriToken) {
            this.hlsUriToken = hlsUriToken;
        }

        public String getRegionId() {
            return regionId;
        }

        public void setRegionId(String regionId) {
            this.regionId = regionId;
        }

        public String getAuthInfo() {
            return authInfo;
        }

        public void setAuthInfo(String authInfo) {
            this.authInfo = authInfo;
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

    public MpsBean getData() {
        return data;
    }

    public void setData(MpsBean data) {
        this.data = data;
    }
}
