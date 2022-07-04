package com.aliyun.player.alivcplayerexpand.view.dot;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.svideo.common.base.BaseDialogFragment;

/**
 * 打点信息DialogFragment
 */
public class AlivcDotMsgDialogFragment extends BaseDialogFragment {

    /**
     * DialogFragment展示的x和y点的坐标
     */
    private int mLayoutParamsX, mLayoutParamsY;
    /**
     * 打点详细信息
     */
    private TextView mDotMsgTextView;

    /**
     * 打点实体类
     */
    private DotView mDotView;

    /**
     * 打点信息点击事件监听
     */
    private OnDotViewMsgClickListener mDotViewMsgClickListener;

    public AlivcDotMsgDialogFragment() {
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.alivc_long_video_dialogfragment_dot_msg;
    }

    @Override
    protected void bindView(final View view) {
        FrameLayout mDotViewMsgRootView = view.findViewById(R.id.dot_view_msg_root);
        mDotMsgTextView = view.findViewById(R.id.tv_dot_msg);
        mDotMsgTextView.setText(mDotView.getDotMsg());

        mDotViewMsgRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDotViewMsgClickListener != null) {
                    mDotViewMsgClickListener.onDotViewMsgClick();
                }
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        final Window window = getDialog().getWindow();
        if (window != null) {
            //设置窗体背景色透明
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置宽高
            final WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            //透明度
            layoutParams.dimAmount = getDimAmount();
            //位置
            layoutParams.gravity = Gravity.START | Gravity.BOTTOM;

            final ViewTreeObserver viewTreeObserver = mDotMsgTextView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ViewTreeObserver obs = mDotMsgTextView.getViewTreeObserver();
                    obs.removeGlobalOnLayoutListener(this);
                    int measuredWidth = mDotMsgTextView.getMeasuredWidth();
                    layoutParams.x = mLayoutParamsX - measuredWidth / 2;
                    layoutParams.y = getScreenHeight(getContext()) - mLayoutParamsY;
                    window.setAttributes(layoutParams);

                }
            });
        }
    }

    public void setX(int x) {
        this.mLayoutParamsX = x;
    }

    public void setY(int y) {
        this.mLayoutParamsY = y;
    }

    public void setDotView(DotView dotView) {
        this.mDotView = dotView;
    }

    public DotView getDotView() {
        return mDotView;
    }

    public interface OnDotViewMsgClickListener {
        void onDotViewMsgClick();
    }

    public void setOnDotViewMsgClickListener(OnDotViewMsgClickListener listener) {
        this.mDotViewMsgClickListener = listener;
    }
}
