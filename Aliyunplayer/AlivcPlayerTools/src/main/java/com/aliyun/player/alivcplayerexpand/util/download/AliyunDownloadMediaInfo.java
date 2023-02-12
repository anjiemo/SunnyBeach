package com.aliyun.player.alivcplayerexpand.util.download;

import android.os.Parcel;
import android.text.TextUtils;

import com.aliyun.player.bean.ErrorCode;
import com.aliyun.player.nativeclass.TrackInfo;
import com.aliyun.player.source.VidAuth;
import com.aliyun.player.source.VidSts;
import com.aliyun.utils.JsonUtil;
import com.cicada.player.utils.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 下载信息封装类
 *
 * @author hanyu
 */
public class AliyunDownloadMediaInfo {

    private static final String TAG = AliyunDownloadMediaInfo.class.getSimpleName();
    private String mVid;
    private String mQuality;
    private int mProgress = 0;
    private String mSavePath = null;
    private String mTitle;
    private String mCoverUrl;
    private long mDuration;
    private AliyunDownloadMediaInfo.Status mStatus;
    private long mSize;
    private String mFormat;
    private int mDownloadIndex = 0;
    private int isEncripted = 0;
    private TrackInfo mTrackInfo;
    private VidSts mVidSts;
    private ErrorCode errorCode;
    private String errorMsg;
    private int mFileHandleProgress = 0;
    private int mQualityIndex;
    private String mTvId;
    private boolean isSelected;
    private int number = 1;
    private int watchNumber = 0;
    private String mTvName;
    private String mTvCoverUrl;
    private int mWatched;
    private VidAuth vidAuth;
    private int vidType;

    public VidAuth getVidAuth() {
        return vidAuth;
    }

    public void setVidAuth(VidAuth vidAuth) {
        this.vidAuth = vidAuth;
    }

    public int getVidType() {
        return vidType;
    }

    public void setVidType(int vidType) {
        this.vidType = vidType;
    }

    public int getWatched() {
        return mWatched;
    }

    public void setWatched(int mWatched) {
        this.mWatched = mWatched;
    }

    public String getTvCoverUrl() {
        return mTvCoverUrl;
    }

    public void setTvCoverUrl(String mTvCoverUrl) {
        this.mTvCoverUrl = mTvCoverUrl;
    }

    public int getWatchNumber() {
        return watchNumber;
    }

    public void setWatchNumber(int watchNumber) {
        this.watchNumber = watchNumber;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    protected AliyunDownloadMediaInfo(Parcel in) {
        mVid = in.readString();
        mQuality = in.readString();
        mProgress = in.readInt();
        mSavePath = in.readString();
        mTitle = in.readString();
        mCoverUrl = in.readString();
        mDuration = in.readLong();
        mSize = in.readLong();
        mFormat = in.readString();
        mDownloadIndex = in.readInt();
        isEncripted = in.readInt();
        errorMsg = in.readString();
        mFileHandleProgress = in.readInt();
        mQualityIndex = in.readInt();
        mTvId = in.readString();
        isSelected = in.readByte() != 0;
    }

    public String getTvName() {
        return mTvName;
    }

    public void setTvName(String mTvName) {
        this.mTvName = mTvName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getTvId() {
        return mTvId;
    }

    public void setTvId(String mTvId) {
        this.mTvId = mTvId;
    }

    public AliyunDownloadMediaInfo() {
    }

    public int getQualityIndex() {
        return mQualityIndex;
    }

    public void setQualityIndex(int mQualityIndex) {
        this.mQualityIndex = mQualityIndex;
    }

    public int getmFileHandleProgress() {
        return mFileHandleProgress;
    }

    public void setmFileHandleProgress(int mFileHandleProgress) {
        this.mFileHandleProgress = mFileHandleProgress;
    }

    public int getDownloadIndex() {
        return this.mDownloadIndex;
    }

    public void setDownloadIndex(int mDownloadIndex) {
        this.mDownloadIndex = mDownloadIndex;
    }

    public String getVid() {
        return this.mVid;
    }

    public void setVid(String mVid) {
        this.mVid = mVid;
    }

    public String getQuality() {
        return this.mQuality;
    }

    public void setQuality(String mQuality) {
        this.mQuality = mQuality;
    }

    public int getProgress() {
        return this.mProgress;
    }

    public void setProgress(int mProgress) {
        this.mProgress = mProgress;
    }

    public String getSavePath() {
        return this.mSavePath;
    }

    public void setSavePath(String mSavePath) {
        this.mSavePath = mSavePath;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getCoverUrl() {
        return this.mCoverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.mCoverUrl = coverUrl;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public void setDuration(long duration) {
        this.mDuration = duration;
    }

    public AliyunDownloadMediaInfo.Status getStatus() {
        return this.mStatus;
    }

    public void setStatus(AliyunDownloadMediaInfo.Status mStatus) {
        this.mStatus = mStatus;
    }

    public long getSize() {
        return this.mSize;
    }

    public String getSizeStr() {
        int kbSize = (int) ((float) this.mSize / 1024.0F);
        return kbSize < 1024 ? kbSize + "KB" : (float) kbSize / 1024.0F + "MB";
    }

    public void setSize(long mSize) {
        this.mSize = mSize;
    }

    public String getFormat() {
        return this.mFormat;
    }

    public void setFormat(String mFormat) {
        this.mFormat = mFormat;
    }

    public int isEncripted() {
        return this.isEncripted;
    }

    public void setEncripted(int encripted) {
        this.isEncripted = encripted;
    }

    public TrackInfo getTrackInfo() {
        return mTrackInfo;
    }

    public void setTrackInfo(TrackInfo mTrackInfo) {
        this.mTrackInfo = mTrackInfo;
    }

    public VidSts getVidSts() {
        return mVidSts;
    }

    public void setVidSts(VidSts mVidSts) {
        this.mVidSts = mVidSts;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public static String getJsonFromInfos(List<AliyunDownloadMediaInfo> infos) {
        JSONArray jsonArray = new JSONArray();
        if (infos != null && !infos.isEmpty()) {
            Iterator var2 = infos.iterator();

            while (var2.hasNext()) {
                AliyunDownloadMediaInfo info = (AliyunDownloadMediaInfo) var2.next();
                JSONObject infoJsonobject = formatInfoToJsonobj(info);
                if (infoJsonobject != null) {
                    jsonArray.put(infoJsonobject);
                }
            }

            return jsonArray.toString();
        } else {
            return jsonArray.toString();
        }
    }

    private static JSONObject formatInfoToJsonobj(AliyunDownloadMediaInfo info) {
        if (info == null) {
            return null;
        } else {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("vid", info.getVid());
                jsonObject.put("quality", info.getQuality());
                jsonObject.put("format", info.getFormat());
                jsonObject.put("coverUrl", info.getCoverUrl());
                jsonObject.put("duration", info.getDuration());
                jsonObject.put("title", info.getTitle());
                jsonObject.put("savePath", info.getSavePath());
                jsonObject.put("status", info.getStatus());
                jsonObject.put("size", info.getSize());
                jsonObject.put("progress", info.getProgress());
                jsonObject.put("dIndex", info.getDownloadIndex());
                jsonObject.put("encript", info.isEncripted());
                return jsonObject;
            } catch (JSONException var3) {
                Logger.e(TAG, "e : " + var3.getMessage());
                return null;
            }
        }
    }

    public static List<AliyunDownloadMediaInfo> getInfosFromJson(String infoContent) {
        if (TextUtils.isEmpty(infoContent)) {
            return null;
        } else {
            JSONArray jsonArray = null;

            try {
                jsonArray = new JSONArray(infoContent);
            } catch (JSONException var8) {
                Logger.d(TAG, " e..." + var8);
            }

            if (jsonArray == null) {
                return null;
            } else {
                List<AliyunDownloadMediaInfo> infos = new ArrayList();
                int size = jsonArray.length();

                for (int i = 0; i < size; ++i) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        AliyunDownloadMediaInfo info = getInfoFromJson(jsonObject);
                        infos.add(info);
                    } catch (JSONException var7) {
                        Logger.d(TAG, " e..." + var7);
                    }
                }

                return infos;
            }
        }
    }

    private static AliyunDownloadMediaInfo getInfoFromJson(JSONObject jsonObject) {
        AliyunDownloadMediaInfo info = new AliyunDownloadMediaInfo();
        info.setVid(JsonUtil.getString(jsonObject, new String[]{"vid"}));
        info.setTitle(JsonUtil.getString(jsonObject, new String[]{"title"}));
        info.setQuality(JsonUtil.getString(jsonObject, new String[]{"quality"}));
        info.setFormat(JsonUtil.getString(jsonObject, new String[]{"format"}));
        info.setCoverUrl(JsonUtil.getString(jsonObject, new String[]{"coverUrl"}));
        info.setDuration((long) JsonUtil.getInt(jsonObject, new String[]{"duration"}));
        info.setSavePath(JsonUtil.getString(jsonObject, new String[]{"savePath"}));
        info.setStatus(AliyunDownloadMediaInfo.Status.valueOf(JsonUtil.getString(jsonObject, new String[]{"status"})));
        info.setSize((long) JsonUtil.getInt(jsonObject, new String[]{"size"}));
        info.setProgress(JsonUtil.getInt(jsonObject, new String[]{"progress"}));
        info.setDownloadIndex(JsonUtil.getInt(jsonObject, new String[]{"dIndex"}));
        info.setEncripted(JsonUtil.getInt(jsonObject, new String[]{"encript"}));
        return info;
    }

    public static enum Status {
        /**
         * 空闲状态
         */
        Idle,
        /**
         * 准备状态
         */
        Prepare,
        /**
         * 等待状态
         */
        Wait,
        /**
         * 开始状态
         */
        Start,
        /**
         * 暂停状态
         */
        Stop,
        /**
         * 完成状态
         */
        Complete,
        /**
         * 错误状态
         */
        Error,
        /**
         * 删除状态
         */
        Delete,
        /**
         * 文件处理状态
         */
        File;

        private Status() {
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || TextUtils.isEmpty(this.mVid) || TextUtils.isEmpty(this.mQuality)) {
            return false;
        }
        AliyunDownloadMediaInfo that = (AliyunDownloadMediaInfo) o;
        if (TextUtils.isEmpty(this.mTvId) && TextUtils.isEmpty(that.mTvId)) {
            return this.mVid.equals(that.mVid) && this.mQuality.equals(that.mQuality);
        } else {
            return this.mVid.equals(that.mVid);
        }

    }

    @Override
    public int hashCode() {
        Object[] hashObject = new Object[2];
        hashObject[0] = mVid;
        hashObject[1] = mQuality;
        return Arrays.hashCode(hashObject);
    }
}
