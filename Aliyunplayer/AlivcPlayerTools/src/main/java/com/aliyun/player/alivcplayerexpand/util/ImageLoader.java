package com.aliyun.player.alivcplayerexpand.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 * 简单的图片加载器
 */
public class ImageLoader {


    private LoadImgHandler mLoadImgHandler;

    public ImageLoader(ImageView target) {
        mLoadImgHandler = new LoadImgHandler(target);
    }

    public void loadAsync(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = getImageBitmap(url);
                Message msg = mLoadImgHandler.obtainMessage();
                msg.obj = bitmap;
                mLoadImgHandler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 获取bitmap
     */
    private Bitmap getImageBitmap(String url) {
        URL imgUrl = null;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imgUrl
                    .openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        return bitmap;
    }

    /**
     * 异步加载图片
     */
    private static class LoadImgHandler extends Handler {

        private WeakReference<ImageView> imageViewWeakReference;

        LoadImgHandler(ImageView imageView) {
            imageViewWeakReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        public void handleMessage(Message msg) {
            ImageView targetView = imageViewWeakReference.get();

            if (targetView == null) {
                return;
            }

            Bitmap bitmap = (Bitmap) msg.obj;
            targetView.setImageBitmap(bitmap);

            super.handleMessage(msg);
        }
    }
}
