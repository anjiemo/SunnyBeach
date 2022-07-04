package com.aliyun.svideo.common.base;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.svideo.common.R;
import com.aliyun.svideo.common.baseAdapter.BaseQuickAdapter;
import com.aliyun.svideo.common.baseAdapter.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * 列表选择器
 **/
public class AlivcListSelectorDialogFragment extends BaseDialogFragment {

    public static final String TAG = AlivcListSelectorDialogFragment.class.getSimpleName();

    public FragmentManager mFragmentManager;
    public int mWidth;
    public int mHeight;
    public float mDimAmount = 0.2f;
    public int mGravity = Gravity.BOTTOM;
    public String mTag = TAG;
    /**
     * 数据源
     */
    public List<String> mLists;
    /**
     * 点击外部是否可以取消
     */
    public boolean mIsCancelableOutside = true;
    /**
     * 弹窗动画
     */
    public int mDialogAnimationRes = 0;
    /**
     * 列表适配器
     */
    private SelectorQuickAdapter mSelectorQuickAdapter;

    /**
     * 选中的颜色
     */
    private int mItemSelectedColor;
    /**
     * 未选中的颜色
     */
    private int mItemUnSelectedColor;

    /**
     * 设置选择的位置,如果不设置adapter默认选择第一个
     */
    public void setPosition(String position) {
        mPosition = position;
    }

    public String mPosition;
    public TextView mTvCancel;

    public DialogInterface.OnKeyListener mKeyListener;

    private OnListItemSelectedListener mOnListItemSelectedListener;
    private DialogInterface.OnDismissListener onDismissListener;


    public void setOnListSelectedListener(OnListItemSelectedListener onListItemSelectedListener) {
        mOnListItemSelectedListener = onListItemSelectedListener;
    }

    /**
     * 监听选择的位置信息
     */
    public interface OnListItemSelectedListener {
        void onClick(String position);
    }

    /**
     * 弹窗消失时回调方法
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }


    @Override
    protected int getLayoutRes() {
        return R.layout.alivc_common_dialogfragment_selector;
    }

    @Override
    protected void bindView(View view) {
        //控件点击事件处理
        RecyclerView recyclerView = view.findViewById(R.id.alivc_common_dialog_recyclerview);
        mTvCancel = view.findViewById(R.id.alivc_tv_cancel);
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mSelectorQuickAdapter = new SelectorQuickAdapter(R.layout.alivc_common_dialog_rv_selector_item, null);
        recyclerView.setAdapter(mSelectorQuickAdapter);
        //自定义分割线
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), OrientationHelper.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.alivc_common_rv_divider_gray_vertical));
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        initData();
    }

    private void initData() {
        if (mLists != null) {
            mSelectorQuickAdapter.setNewData(mLists);
            mSelectorQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    if (mOnListItemSelectedListener != null) {
                        mOnListItemSelectedListener.onClick(adapter.getData().get(position).toString());
                        dismiss();
                    }
                }
            });
        }
        if (mPosition != null) {
            mSelectorQuickAdapter.setSelectedPosition(mPosition);
        }
    }


    public AlivcListSelectorDialogFragment show() {
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
     * 使用Builder模式实现
     */
    public static class Builder {
        private AlivcListSelectorDialogFragment params = new AlivcListSelectorDialogFragment();

        public Builder(FragmentManager fragmentManager) {
            params.mFragmentManager = fragmentManager;
        }

        /**
         * 设置弹窗宽度(单位:像素)
         *
         * @param widthPx
         * @return
         */
        public Builder setWidth(int widthPx) {
            params.mWidth = widthPx;
            return this;
        }

        /**
         * 设置弹窗高度(px)
         *
         * @param heightPx
         * @return
         */
        public Builder setHeight(int heightPx) {
            params.mHeight = heightPx;
            return this;
        }

        /**
         * 设置弹窗宽度是屏幕宽度的比例 0 -1
         */
        public Builder setScreenWidthAspect(Context context, float widthAspect) {
            params.mWidth = (int) (getScreenWidth(context) * widthAspect);
            return this;
        }

        /**
         * 设置弹窗高度是屏幕高度的比例 0 -1
         */
        public Builder setScreenHeightAspect(Context context, float heightAspect) {
            params.mHeight = (int) (getScreenHeight(context) * heightAspect);
            return this;
        }

        /**
         * 设置弹窗在屏幕中显示的位置
         *
         * @param gravity
         * @return
         */
        public Builder setGravity(int gravity) {
            params.mGravity = gravity;
            return this;
        }

        /**
         * 设置弹窗在弹窗区域外是否可以取消
         *
         * @param cancel
         * @return
         */
        public Builder setCancelableOutside(boolean cancel) {
            params.mIsCancelableOutside = cancel;
            return this;
        }

        /**
         * 弹窗dismiss时监听回调方法
         *
         * @param dismissListener
         * @return
         */
        public Builder setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
            params.onDismissListener = dismissListener;
            return this;
        }


        /**
         * 设置弹窗背景透明度(0-1f)
         *
         * @param dim
         * @return
         */
        public Builder setDimAmount(float dim) {
            params.mDimAmount = dim;
            return this;
        }

        public Builder setTag(String tag) {
            params.mTag = tag;
            return this;
        }

        /**
         * 弹窗控件点击回调
         *
         * @param listener
         * @return
         */
        public Builder setOnListItemSelectedListener(OnListItemSelectedListener listener) {
            params.mOnListItemSelectedListener = listener;
            return this;
        }

        /**
         * 设置弹窗动画
         *
         * @param animationRes
         * @return
         */
        public Builder setDialogAnimationRes(int animationRes) {
            params.mDialogAnimationRes = animationRes;
            return this;
        }

        /**
         * 列表数据源
         */
        public Builder setNewData(ArrayList<String> lists) {
            params.mLists = lists;
            return this;
        }

        /**
         * 监听弹窗后，返回键点击事件
         */
        public Builder setOnKeyListener(DialogInterface.OnKeyListener keyListener) {
            params.mKeyListener = keyListener;
            return this;
        }

        /**
         * 列表选中的颜色
         */
        public Builder setItemColor(int selectedColor) {
            params.mItemSelectedColor = selectedColor;
            return this;
        }

        /**
         * 列表未选中的颜色
         */
        public Builder setUnItemColor(int selectedUnColor) {
            params.mItemUnSelectedColor = selectedUnColor;
            return this;
        }

        /**
         * 真正创建SimpleDialogFragment对象实例
         *
         * @return
         */
        public AlivcListSelectorDialogFragment create() {
            return params;
        }
    }

    @Override
    protected DialogInterface.OnKeyListener getOnKeyListener() {
        return mKeyListener;
    }

    @Override
    public int getGravity() {
        return mGravity;
    }

    @Override
    public int getDialogHeight() {
        return mHeight;
    }

    @Override
    public int getDialogWidth() {
        return mWidth;
    }

    @Override
    public float getDimAmount() {
        return mDimAmount;
    }

    @Override
    public String getFragmentTag() {
        return mTag;
    }

    @Override
    protected boolean isCancelableOutside() {
        return mIsCancelableOutside;
    }

    @Override
    protected int getDialogAnimationRes() {
        return mDialogAnimationRes;
    }


    /**
     * 列表适配器
     */
    public class SelectorQuickAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
        private String mPosition;

        public void setSelectedPosition(String position) {
            mPosition = position;
        }

        public SelectorQuickAdapter(int layoutResId, @Nullable List<String> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            TextView tvName = helper.getView(R.id.alivc_tv_name);
            tvName.setText(item);
            if (mPosition != null && mPosition.equals(item)) {
                tvName.setTextColor(mItemSelectedColor);
            } else {
                tvName.setTextColor(mItemUnSelectedColor);

            }
        }
    }
}
