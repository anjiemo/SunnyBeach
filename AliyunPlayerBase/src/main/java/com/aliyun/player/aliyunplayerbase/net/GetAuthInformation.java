package com.aliyun.player.aliyunplayerbase.net;

import com.aliyun.player.aliyunplayerbase.bean.AliyunMps;
import com.aliyun.player.aliyunplayerbase.bean.AliyunPlayAuth;
import com.aliyun.player.aliyunplayerbase.bean.AliyunSts;
import com.aliyun.player.aliyunplayerbase.bean.AliyunVideoList;
import com.aliyun.svideo.common.okhttp.AlivcOkHttpClient;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Request;

/**
 * 获取鉴权信息
 */
public class GetAuthInformation {

    /**
     * 临时获取sts信息的接口，后期删除
     *
     * @param listener 获取STS信息监听
     */
    public void getVideoPlayLiveStsInfo(final OnGetStsInfoListener listener) {
        AlivcOkHttpClient.getInstance().get(ServiceCommon.GET_LIVE_PLAY_STS, new AlivcOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                if (listener != null) {
                    listener.onGetStsError(e.getMessage());
                }
            }

            @Override
            public void onSuccess(Request request, String result) {
                Gson gson = new Gson();
                AliyunSts aliyunSts = gson.fromJson(result, AliyunSts.class);
                if (aliyunSts != null && aliyunSts.getCode() == ServiceCommon.RESPONSE_SUCCESS) {
                    AliyunSts.StsBean data = aliyunSts.getData();
                    if (listener != null) {
                        listener.onGetStsSuccess(data);
                    }
                }
            }
        });
    }

    /**
     * 获取sts信息
     *
     * @param listener 获取STS信息监听
     */
    public void getVideoPlayStsInfo(final OnGetStsInfoListener listener) {
        AlivcOkHttpClient.getInstance().get(ServiceCommon.GET_VIDEO_PLAY_STS, new AlivcOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                if (listener != null) {
                    listener.onGetStsError(e.getMessage());
                }
            }

            @Override
            public void onSuccess(Request request, String result) {
                Gson gson = new Gson();
                AliyunSts aliyunSts = gson.fromJson(result, AliyunSts.class);
                if (aliyunSts != null && aliyunSts.getCode() == ServiceCommon.RESPONSE_SUCCESS) {
                    AliyunSts.StsBean data = aliyunSts.getData();
                    if (listener != null) {
                        listener.onGetStsSuccess(data);
                    }
                }
            }
        });
    }

    /**
     * 获取sts信息
     *
     * @param videoId  vid
     * @param listener 获取STS信息监听
     */
    public void getVideoPlayStsInfoWithVideoId(String videoId, final OnGetStsInfoListener listener) {
        HashMap<String, String> mHashMap = new HashMap<>();
        mHashMap.put("videoId", videoId);
        AlivcOkHttpClient.getInstance().get(ServiceCommon.GET_VIDEO_PLAY_STS, mHashMap, new AlivcOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                if (listener != null) {
                    listener.onGetStsError(e.getMessage());
                }
            }

            @Override
            public void onSuccess(Request request, String result) {
                Gson gson = new Gson();
                AliyunSts aliyunSts = gson.fromJson(result, AliyunSts.class);
                if (aliyunSts != null && aliyunSts.getCode() == ServiceCommon.RESPONSE_SUCCESS) {
                    AliyunSts.StsBean data = aliyunSts.getData();
                    if (listener != null) {
                        listener.onGetStsSuccess(data);
                    }
                }
            }
        });
    }

    /**
     * 获取sts监听
     */
    public interface OnGetStsInfoListener {

        void onGetStsError(String errorMsg);

        void onGetStsSuccess(AliyunSts.StsBean dataBean);
    }

    /**
     * 获取url信息
     *
     * @param listener 获取URL信息监听
     */
    public void getVideoPlayUrlInfo(final OnGetUrlInfoListener listener) {
        AlivcOkHttpClient.getInstance().get(ServiceCommon.GET_VIDEO_PLAY_INFO, new AlivcOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                if (listener != null) {
                    listener.onGetUrlError(e.getMessage());
                }
            }

            @Override
            public void onSuccess(Request request, String result) {
                Gson gson = new Gson();
                AliyunVideoList aliyunVideoList = gson.fromJson(result, AliyunVideoList.class);
                if (aliyunVideoList != null && aliyunVideoList.getCode() == ServiceCommon.RESPONSE_SUCCESS) {
                    AliyunVideoList.VideoList data = aliyunVideoList.getData();
                    if (listener != null) {
                        listener.onGetUrlSuccess(data);
                    }
                }
            }
        });
    }

    /**
     * 获取URL监听
     */
    public interface OnGetUrlInfoListener {

        void onGetUrlError(String msg);

        void onGetUrlSuccess(AliyunVideoList.VideoList dataBean);
    }

    /**
     * 获取MPS信息
     *
     * @param listener 获取MPS信息监听
     */
    public void getVideoPlayMpsInfo(final OnGetMpsInfoListener listener) {
        AlivcOkHttpClient.getInstance().get(ServiceCommon.GET_VIDEO_PLAY_MPS, new AlivcOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                if (listener != null) {
                    listener.onGetMpsError(e.getMessage());
                }
            }

            @Override
            public void onSuccess(Request request, String result) {
                Gson gson = new Gson();
                AliyunMps aliyunMps = gson.fromJson(result, AliyunMps.class);
                if (aliyunMps != null && aliyunMps.getCode() == ServiceCommon.RESPONSE_SUCCESS) {
                    AliyunMps.MpsBean mpsBean = aliyunMps.getData();
                    if (listener != null) {
                        listener.onGetMpsSuccess(mpsBean);
                    }
                }
            }
        });
    }

    /**
     * 获取MPS信息
     *
     * @param videoId  vid
     * @param listener 获取mps监听
     */
    public void getVideoPlayMpsInfoWithVideoId(String videoId, final OnGetMpsInfoListener listener) {
        HashMap<String, String> mHashMap = new HashMap<>();
        mHashMap.put("videoId", videoId);
        AlivcOkHttpClient.getInstance().get(ServiceCommon.GET_VIDEO_PLAY_MPS, mHashMap, new AlivcOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                if (listener != null) {
                    listener.onGetMpsError(e.getMessage());
                }
            }

            @Override
            public void onSuccess(Request request, String result) {
                Gson gson = new Gson();
                AliyunMps aliyunMps = gson.fromJson(result, AliyunMps.class);
                if (aliyunMps != null && aliyunMps.getCode() == ServiceCommon.RESPONSE_SUCCESS) {
                    AliyunMps.MpsBean mpsBean = aliyunMps.getData();
                    if (listener != null) {
                        listener.onGetMpsSuccess(mpsBean);
                    }
                }
            }
        });
    }

    /**
     * 获取MPS监听
     */
    public interface OnGetMpsInfoListener {

        void onGetMpsError(String msg);

        void onGetMpsSuccess(AliyunMps.MpsBean dataBean);
    }

    /**
     * 获取PlayAuth信息
     *
     * @param listener 获取PlayAuth监听
     */
    public void getVideoPlayAuthInfo(final OnGetPlayAuthInfoListener listener) {
        AlivcOkHttpClient.getInstance().get(ServiceCommon.GET_VIDEO_PLAY_AUTH, new AlivcOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                if (listener != null) {
                    listener.onGetPlayAuthError(e.getMessage());
                }
            }

            @Override
            public void onSuccess(Request request, String result) {
                Gson gson = new Gson();
                AliyunPlayAuth aliyunPlayAuth = gson.fromJson(result, AliyunPlayAuth.class);
                if (aliyunPlayAuth != null && aliyunPlayAuth.getCode() == ServiceCommon.RESPONSE_SUCCESS) {
                    AliyunPlayAuth.PlayAuthBean data = aliyunPlayAuth.getData();
                    if (listener != null) {
                        listener.onGetPlayAuthSuccess(data);
                    }
                }
            }
        });
    }

    /**
     * 根据vid获取PlayAuth信息
     *
     * @param videoId  vid
     * @param listener 获取PlayAuth监听
     */
    public void getVideoPlayAuthInfoWithVideoId(String videoId, final OnGetPlayAuthInfoListener listener) {
        HashMap<String, String> mHashMap = new HashMap<>();
        mHashMap.put("videoId", videoId);
        AlivcOkHttpClient.getInstance().get(ServiceCommon.GET_VIDEO_PLAY_AUTH, mHashMap, new AlivcOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                if (listener != null) {
                    listener.onGetPlayAuthError(e.getMessage());
                }
            }

            @Override
            public void onSuccess(Request request, String result) {
                Gson gson = new Gson();
                AliyunPlayAuth aliyunPlayAuth = gson.fromJson(result, AliyunPlayAuth.class);
                if (aliyunPlayAuth != null && aliyunPlayAuth.getCode() == ServiceCommon.RESPONSE_SUCCESS) {
                    AliyunPlayAuth.PlayAuthBean data = aliyunPlayAuth.getData();
                    if (listener != null) {
                        listener.onGetPlayAuthSuccess(data);
                    }
                }
            }
        });
    }

    /**
     * 获取PlayAuth监听
     */
    public interface OnGetPlayAuthInfoListener {

        void onGetPlayAuthError(String msg);

        void onGetPlayAuthSuccess(AliyunPlayAuth.PlayAuthBean dataBean);
    }
}
