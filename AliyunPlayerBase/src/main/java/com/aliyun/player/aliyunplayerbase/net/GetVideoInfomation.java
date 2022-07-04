package com.aliyun.player.aliyunplayerbase.net;

import android.content.Context;

import com.aliyun.player.aliyunplayerbase.bean.AliyunUserInfo;
import com.aliyun.svideo.common.okhttp.AlivcOkHttpClient;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Request;

/**
 * 获取播放视频信息
 */
public class GetVideoInfomation {

    public void getRandomUser(final OnGetRandomUserListener listener) {
        AlivcOkHttpClient.getInstance().get(ServiceCommon.GET_RANDOM_USER, new AlivcOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                if (listener != null) {
                    listener.onGetError(request, e);
                }
            }

            @Override
            public void onSuccess(Request request, String result) {
                Gson gson = new Gson();
                AliyunUserInfo aliyunUserInfo = gson.fromJson(result, AliyunUserInfo.class);
                if (aliyunUserInfo != null && listener != null) {
                    listener.onGetSuccess(aliyunUserInfo);
                }
            }
        });
    }

    public interface OnGetRandomUserListener {
        void onGetSuccess(AliyunUserInfo aliyunUserInfo);

        void onGetError(Request request, IOException e);
    }

    public void getListPlayerVideoInfos(Context context, String pageIndex, String userToken, int id, final OnGetListPlayerVideoInfosListener listener) {
        HashMap<String, String> hashMap = new HashMap<>();
        String mPackageName = context.getPackageName();
        hashMap.put(ServiceCommon.RequestKey.FORM_KEY_PACKAGE_NAME, mPackageName);
        hashMap.put(ServiceCommon.RequestKey.FORM_KEY_PAGE_INDEX, pageIndex);
        hashMap.put(ServiceCommon.RequestKey.FORM_KEY_PAGE_SIZE, ServiceCommon.DEFAULT_PAGE_SIZE + "");
        hashMap.put(ServiceCommon.RequestKey.FORM_KEY_TOKEN, userToken);
        if (id > 0) {
            hashMap.put(ServiceCommon.RequestKey.FORM_KEY_ID, id + "");
        }
        AlivcOkHttpClient.getInstance().get(ServiceCommon.GET_VIDEO_LIST_INFO, hashMap, new AlivcOkHttpClient.HttpCallBack() {

            @Override
            public void onError(okhttp3.Request request, IOException e) {
                if (listener != null) {
                    listener.onGetError(request, e);
                }
            }

            @Override
            public void onSuccess(okhttp3.Request request, String result) {
                if (listener != null) {
                    listener.onGetSuccess(request, result);
                }
            }
        });
    }

    public interface OnGetListPlayerVideoInfosListener {
        void onGetSuccess(okhttp3.Request request, String result);

        void onGetError(okhttp3.Request request, IOException e);
    }
}
