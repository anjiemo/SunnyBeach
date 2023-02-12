package com.aliyun.player.alivcplayerexpand.util.download;

import com.aliyun.player.bean.ErrorCode;

import java.util.List;

public interface AliyunDownloadInfoListener {

    public void onPrepared(/*String vid, String quality*/List<AliyunDownloadMediaInfo> infos);

    public void onAdd(AliyunDownloadMediaInfo info);

    public void onStart(/*String vid, String quality*/AliyunDownloadMediaInfo info);

    public void onProgress(/*String vid, String quality*/AliyunDownloadMediaInfo info, int percent);

    public void onStop(/*String vid, String quality*/AliyunDownloadMediaInfo info);

    public void onCompletion(/*String vid, String quality*/AliyunDownloadMediaInfo info);

    public void onError(/*String vid, String quality*/AliyunDownloadMediaInfo info, ErrorCode code, String msg, String requestId);

    public void onWait(AliyunDownloadMediaInfo outMediaInfo);

    public void onDelete(AliyunDownloadMediaInfo info);

    public void onDeleteAll();

    public void onFileProgress(AliyunDownloadMediaInfo info);

//    public void onM3u8IndexUpdate(AliyunDownloadMediaInfo outMediaInfo , int index);
}
