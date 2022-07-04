package com.aliyun.player.alivcplayerexpand.util.database;

import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadMediaInfo;

import java.util.List;

public interface LoadDbTvListDatasListenerr {

    /**
     * 查询对应tvid电视剧列表
     */
    void onLoadTvListSuccess(List<AliyunDownloadMediaInfo> aliyunDownloadMediaInfos);
}
