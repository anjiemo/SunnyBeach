package com.aliyun.player.aliyunplayerbase.view.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * 刷新Refresh
 */
public class AlivcSwipeRefreshLayout extends SwipeRefreshLayout {
    private float startY;
    private float startX;
    /**
     * 记录是否向下拖拽的标记
     */
    private boolean mIsDragger;

    public AlivcSwipeRefreshLayout(Context context) {
        super(context);
    }

    public AlivcSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        if (!canChildScrollUp()) {
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // 记录手指按下的位置
                    startY = ev.getY();
                    startX = ev.getX();
                    // 初始化标记
                    mIsDragger = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 获取当前手指位置
                    float endY = ev.getY();

                    float distanceY = endY - startY;
                    Log.e("test", "distanceY" + distanceY);
                    if (distanceY > 0) {
                        mIsDragger = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // 初始化标记
                    mIsDragger = false;
                    break;
                default:
                    break;
            }
            return mIsDragger;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean onTouchEvent = super.onTouchEvent(ev);
        Log.e("test", "onTouchEvent" + onTouchEvent);
        return onTouchEvent;
    }
}
