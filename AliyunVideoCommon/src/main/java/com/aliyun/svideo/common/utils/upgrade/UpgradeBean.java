package com.aliyun.svideo.common.utils.upgrade;

/**
 * @author cross_ly
 * @date 2018/11/07
 * <p>描述:
 */
public class UpgradeBean {

    /**
     * versionName : 1.3.8
     * versionCode : 138
     * describe : 更新了数据统计,自动升级
     * url : http://text-ly.oss-cn-beijing.aliyuncs.com/app-allmodule.apk?Expires=1541576479&OSSAccessKeyId=TMP
     * .AQGbFq_0YKY8ujwVF1zuCAgdYQQj7NPYsrHXEoHvcebX3oALXLUvv0J9sqwZAAAwLAIUbOiVPlKDsEA3lUkoM48YnMxrVSECFDiOezccZ1Grn83cLApjuf3unE4p&Signature=eH7E0lslnqq9JTVr15Bu8gFfyjM%3D
     */

    private String versionName;//版本名称
    private int versionCode;//版本号
    private String describe;//描述
    private String url;//apk url

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
