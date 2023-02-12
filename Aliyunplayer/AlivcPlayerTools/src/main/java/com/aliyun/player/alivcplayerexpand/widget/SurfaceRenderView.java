package com.aliyun.player.alivcplayerexpand.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class SurfaceRenderView extends SurfaceView implements IRenderView, SurfaceHolder.Callback {

    private IRenderCallback mRenderCallback;

    public SurfaceRenderView(Context context) {
        super(context);
        init(context);
    }

    public SurfaceRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SurfaceRenderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        Log.e("AliLivePlayerView", "init: SurfaceRenderView");
        getHolder().addCallback(this);
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
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (mRenderCallback != null) {
            mRenderCallback.onSurfaceCreate(surfaceHolder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        if (mRenderCallback != null) {
            mRenderCallback.onSurfaceChanged(width, height);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Surface surface = surfaceHolder.getSurface();
        if (surface != null) {
            surface.release();
        }
        if (mRenderCallback != null) {
            mRenderCallback.onSurfaceDestroyed();
        }
    }
}
