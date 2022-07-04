package com.aliyun.player.alivcplayerexpand.util.database;

import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadMediaInfo;

import java.util.List;

/**
 * 数据库加载数据监听
 *
 * @author hanyu
 */
public interface LoadDbDatasListener {

    /**
     * 数据加载成功
     */
    public void onLoadSuccess(List<AliyunDownloadMediaInfo> dataList);
}
