package com.aliyun.svideo.common.base;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.aliyun.svideo.common.R;
import com.aliyun.svideo.common.widget.WheelView;

/**
 * 滑动选择对话框
 */
public class AlivcWheelDialogFragment extends BaseDialogFragment implements WheelView.OnValueChangeListener {


    private WheelView mWheelView;
    private TextView mTvLeft, mTvRight;
    public FragmentManager mFragmentManager;

    /**
     * 数据源
     */
    private String[] mDialogWheel;
    /**
     * 左边文字
     */
    private String mDialogLeft;
    /**
     * 右边文字
     */
    private String mDialogRight;
    /**
     * 数据接口回调
     */
    private OnWheelDialogListener mOnWheelDialogListener;
    /**
     * 点击外部是否可以取消
     */
    public boolean mIsCancelableOutside = true;
    /**
     * 弹窗动画
     */
    public int mDialogAnimationRes = 0;

    @Override
    protected int getLayoutRes() {
        return R.layout.alivc_common_dialogfragment_wheelview;
    }

    @Override
    protected void bindView(View view) {
        mTvLeft = (TextView) view.findViewById(R.id.alivc_tv_cancel);
        mTvRight = (TextView) view.findViewById(R.id.alivc_tv_sure);
        mWheelView = (WheelView) view.findViewById(R.id.alivc_wheelView_dialog);

        mTvLeft.setText(mDialogLeft);
        mTvRight.setText(mDialogRight);

        mWheelView.refreshByNewDisplayedValues(mDialogWheel);
        //设置是否可以上下无限滑动
        mWheelView.setWrapSelectorWheel(false);
        mWheelView.setDividerColor(ContextCompat.getColor(getContext(), R.color.alivc_common_bg_white_gray));
        mWheelView.setSelectedTextColor(ContextCompat.getColor(getContext(), R.color.alivc_common_bg_black));
        mWheelView.setNormalTextColor(ContextCompat.getColor(getContext(), R.color.alivc_common_font_gray_333333));
        initEvent();
    }

    protected void initEvent() {
        //左边按钮
        mTvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnWheelDialogListener != null) {
                    mOnWheelDialogListener.onClickLeft(AlivcWheelDialogFragment.this, getWheelValue());
                }
            }
        });

        //右边按钮
        mTvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnWheelDialogListener != null) {
                    mOnWheelDialogListener.onClickRight(AlivcWheelDialogFragment.this, getWheelValue());
                }
            }
        });
    }


    /**
     * 获取当前值
     *
     * @return
     */
    private String getWheelValue() {
        String[] content = mWheelView.getDisplayedValues();
        return content == null ? null : content[mWheelView.getValue() - mWheelView.getMinValue()];
    }

    @Override
    public void onValueChange(WheelView picker, int oldVal, int newVal) {
        String[] content = mWheelView.getDisplayedValues();
        if (content != null && mOnWheelDialogListener != null) {
            mOnWheelDialogListener.onValueChanged(AlivcWheelDialogFragment.this, content[newVal - mWheelView.getMinValue()]);
        }
    }


    @Override
    protected boolean isCancelableOutside() {
        return mIsCancelableOutside;
    }

    @Override
    protected int getDialogAnimationRes() {
        return mDialogAnimationRes;
    }

    public AlivcWheelDialogFragment show() {
        Log.d("Dialog", "show");
        try {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.remove(this);
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            Log.e("Dialog", e.toString());
        }
        return this;
    }

    /**
     * 对外开放的方法
     *
     * @param onWheelDialogListener
     */
    public void setWheelDialogListener(OnWheelDialogListener onWheelDialogListener) {
        this.mOnWheelDialogListener = onWheelDialogListener;
    }

    public static final class Builder {
        private AlivcWheelDialogFragment mDialogFragment = new AlivcWheelDialogFragment();

        public Builder(FragmentManager fragmentManager) {
            mDialogFragment.mFragmentManager = fragmentManager;
        }

        /**
         * 设置数据源
         */
        public Builder setWheelData(String[] data) {
            mDialogFragment.mDialogWheel = data;
            return this;
        }

        /**
         * 设置左边文字
         */
        public Builder cancelString(String cancel) {
            mDialogFragment.mDialogLeft = cancel;
            return this;
        }

        /**
         * 设置右边文字
         */
        public Builder sureString(String sure) {
            mDialogFragment.mDialogRight = sure;
            return this;
        }

        /**
         * 设置回调
         */
        public Builder onWheelDialogListener(OnWheelDialogListener onWheelDialogListener) {
            mDialogFragment.mOnWheelDialogListener = onWheelDialogListener;
            return this;
        }

        /**
         * 设置点击外部是否可以取消
         */
        public Builder isCancelableOutside(boolean outSide) {
            mDialogFragment.mIsCancelableOutside = outSide;
            return this;
        }

        /**
         * 设置动画
         */
        public Builder dialogAnimationRes(int animation) {
            mDialogFragment.mDialogAnimationRes = animation;
            return this;
        }

        /**
         * 真正创建
         */
        public AlivcWheelDialogFragment create() {
            return mDialogFragment;
        }
    }

    public interface OnWheelDialogListener {
        /**
         * 左边按钮单击事件回调
         *
         * @param dialog
         * @param value
         */
        void onClickLeft(DialogFragment dialog, String value);

        /**
         * 右边按钮单击事件回调
         *
         * @param dialog
         * @param value
         */
        void onClickRight(DialogFragment dialog, String value);

        /**
         * 滑动停止时的回调
         *
         * @param dialog
         * @param value
         */
        void onValueChanged(DialogFragment dialog, String value);
    }

}
