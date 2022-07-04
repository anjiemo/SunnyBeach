package com.aliyun.svideo.common.bottomnavigationbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aliyun.svideo.common.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义底部导航栏
 */

public class BottomNavigationBar extends LinearLayout implements View.OnClickListener {


    private IBnbItemSelectListener bnbItemSelectListener;
    private IBnbItemDoubleClickListener bnbItemDoubleClickListener;
    private List<BottomNavigationEntity> entities = new ArrayList<>();


    //这里是-1主要是为了第一次比较
    private int mCurrentPosition = -1;
    //选中的color
    private int mTextSelectedColor;
    //未选中的color
    private int mTextUnSelectedColor;
    //单个布局
    private int mItemLayout;
    //是否需要缩放动画
    private boolean isAnim;
    //缩放的比例
    private float scaleRatio;

    private static final String DEFAULT_SELECTED_COLOR = "#000000";
    private static final String DEFAULT_UNSELECTED_COLOR = "#999999";

    public BottomNavigationBar(Context context) {
        this(context, null);
    }

    public BottomNavigationBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomNavigationBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);
        init(context, attrs);
    }

    public void setEntities(List<BottomNavigationEntity> list) {
        entities.clear();
        entities.addAll(list);
        addItems();
    }

    /**
     * 初始化
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BottomNavigationBar);
        mTextSelectedColor = array.getColor(R.styleable.BottomNavigationBar_bnb_selectedColor, Color.parseColor(DEFAULT_SELECTED_COLOR));
        mTextUnSelectedColor = array.getColor(R.styleable.BottomNavigationBar_bnb_unSelectedColor, Color.parseColor(DEFAULT_UNSELECTED_COLOR));
        isAnim = array.getBoolean(R.styleable.BottomNavigationBar_bnb_anim, false);
        scaleRatio = array.getFloat(R.styleable.BottomNavigationBar_bnb_scale_ratio, 1.1f);
        mItemLayout = array.getResourceId(R.styleable.BottomNavigationBar_bnb_layoutId, -1);
        array.recycle();
    }

    /**
     * 添加item
     */
    private void addItems() {
        if (entities.isEmpty()) {
            return;
        }
        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        BottomNavigationItemView item;
        for (int i = 0; i < entities.size(); i++) {
            BottomNavigationEntity entity = entities.get(i);
            item = new BottomNavigationItemView(getContext());
            item.setLayoutId(mItemLayout);
            item.setAnim(isAnim);
            item.setScaleRatio(scaleRatio);
            item.setBottomNavigationEntity(entity);
            item.setTextSelectedColor(mTextSelectedColor);
            item.setTextUnSelectedColor(mTextUnSelectedColor);
            item.setTag(i);
            addView(item, params);
            item.setOnClickListener(this);
            item.setDefaultState();
        }
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        if (position == mCurrentPosition && bnbItemDoubleClickListener != null) {
            bnbItemDoubleClickListener.onBnbItemDoubleClick(position);
            return;
        }
        if (position != mCurrentPosition) {
            setCurrentPosition(position);
        }
    }


    public void setBnbItemSelectListener(IBnbItemSelectListener listener) {
        this.bnbItemSelectListener = listener;
    }

    public void setBnbItemDoubleClickListener(IBnbItemDoubleClickListener listener) {
        this.bnbItemDoubleClickListener = listener;
    }

    /**
     * 设置当前选中位置
     *
     * @param position 当前选中的item位置索引
     */
    public void setCurrentPosition(int position) {
        int count = getChildCount();
        if (count == 0 || position > count) {
            return;
        }
        if (position == mCurrentPosition) {
            return;
        }
        BottomNavigationItemView lastItem = (BottomNavigationItemView) getChildAt(mCurrentPosition);
        BottomNavigationItemView currentItem = (BottomNavigationItemView) getChildAt(position);
        if (lastItem != null) {
            lastItem.setSelected(false);
        }
        if (currentItem != null) {
            currentItem.setSelected(true);
        }
        mCurrentPosition = position;
        if (bnbItemSelectListener != null) {
            bnbItemSelectListener.onBnbItemSelect(position);
        }
    }

    /**
     * 刷新的目的是：
     * 1.更新budge
     * 2.后续添加功能
     *
     * @param index 刷新index
     */
    public void refreshItem(int index) {
        if (index < 0) {
            return;
        }
        if (index >= getChildCount()) {
            return;
        }
        BottomNavigationItemView itemView = (BottomNavigationItemView) getChildAt(index);
        itemView.refresh();
    }

    /**
     * 设置是否开启切换动画
     *
     * @param anim
     */
    public void setAnim(boolean anim) {
        isAnim = anim;
    }

    public interface IBnbItemSelectListener {
        void onBnbItemSelect(int position);
    }

    public interface IBnbItemDoubleClickListener {
        void onBnbItemDoubleClick(int position);
    }


}
