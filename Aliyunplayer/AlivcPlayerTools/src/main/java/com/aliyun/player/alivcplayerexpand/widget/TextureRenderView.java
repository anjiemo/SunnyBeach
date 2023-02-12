package com.aliyun.player.alivcplayerexpand.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

public class TextureRenderView extends TextureView implements IRenderView, TextureView.SurfaceTextureListener {

    private IRenderCallback mRenderCallback;
    private Surface mSurface;

    public TextureRenderView(Context context) {
        super(context);
        init(context);
    }

    public TextureRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TextureRenderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        Log.e("AliLivePlayerView", "init: TextureRenderView");
        setSurfaceTextureListener(this);
    }

    @Override
    public void addRenderCallback(IRenderCallback renderCallback) {
        this.mRenderCallback = renderCallback;
    }

    @Override
    public View getView() {
        return this;
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        mSurface = new Surface(surfaceTexture);
        if (mRenderCallback != null) {
            mRenderCallback.onSurfaceCreate(mSurface);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        if (mRenderCallback != null) {
            mRenderCallback.onSurfaceChanged(width, height);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        mSurface.release();
        if (mRenderCallback != null) {
            mRenderCallback.onSurfaceDestroyed();
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
