package com.aliyun.svideo.common.bottomnavigationbar;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aliyun.svideo.common.R;


/**
 * BottomNavigationItemView
 */
class BottomNavigationItemView extends LinearLayout {
    private BottomNavigationEntity mBottomNavigationEntity;
    private int mTextSelectedColor;
    private int mTextUnSelectedColor;

    private ImageView mItemIcon;
    private TextView mItemText;
    private TextView mItemBadge;

    private boolean isAnim;
    private float scaleRatio;
    private int mLayoutId;

    private static final float SCALE_MAX = 1.1f;

    protected BottomNavigationItemView(Context context) {
        this(context, null);
    }

    protected BottomNavigationItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    protected BottomNavigationItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
    }

    protected void setScaleRatio(float scaleRatio) {
        this.scaleRatio = Math.abs(scaleRatio);
    }

    /*unused*/
    public float getScaleRatio() {
        return scaleRatio;
    }

    protected void setAnim(boolean anim) {
        isAnim = anim;
    }


    /**
     * //set item layout
     *
     * @param layoutId 布局ID
     */
    protected void setLayoutId(int layoutId) {
        mLayoutId = layoutId;
        LayoutInflater.from(getContext()).inflate(mLayoutId, this, true);
        mItemIcon = findViewById(R.id.bnb_item_icon);
        mItemText = findViewById(R.id.bnb_item_text);
        mItemBadge = findViewById(R.id.bnb_item_badge);
    }

    /*unused*/
    public boolean isAnim() {
        return isAnim;
    }

    /**
     * 设置
     */
    protected void setBottomNavigationEntity(BottomNavigationEntity bottomNavigationEntity) {
        mBottomNavigationEntity = bottomNavigationEntity;
        setDefaultState();
    }

    protected void setTextSelectedColor(int textSelectedColor) {
        this.mTextSelectedColor = textSelectedColor;
    }

    protected void setTextUnSelectedColor(int textUnSelectedColor) {
        this.mTextUnSelectedColor = textUnSelectedColor;
    }


    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        rendingItemText(selected);
        rendingItemIcon(selected);
        if (isAnim) {
            if (selected) {
                scale(1f, scaleRatio > SCALE_MAX ? scaleRatio : SCALE_MAX);
            } else {
                scale(scaleRatio > SCALE_MAX ? scaleRatio : SCALE_MAX, 1f);
            }
        }
    }

    /**
     * 设置为初始状态
     * 默认未选中
     */
    protected void setDefaultState() {
        rendingItemText(false);
        rendingItemIcon(false);
        rendingItemBadge();
    }

    /**
     * 目前刷新只是刷新badge
     */
    protected void refresh() {
        rendingItemBadge();
    }

    /**
     * rendind ICON only
     */
    private void rendingItemText(boolean select) {
        if (mItemText == null || mBottomNavigationEntity == null) {
            return;
        }
        String text = mBottomNavigationEntity.getText();
        if (TextUtils.isEmpty(text)) {
            mItemText.setVisibility(GONE);
        } else {
            mItemText.setText(text);
            mItemText.setVisibility(View.VISIBLE);
            if (select) {
                mItemText.setTextColor(mTextSelectedColor);
            } else {
                mItemText.setTextColor(mTextUnSelectedColor);
            }
        }
    }

    private void rendingItemIcon(boolean select) {
        if (mItemIcon == null || mBottomNavigationEntity == null) {
            return;
        }
        if (select) {
            mItemIcon.setImageResource(mBottomNavigationEntity.getSelectedIcon());
        } else {
            mItemIcon.setImageResource(mBottomNavigationEntity.getUnSelectIcon());
        }
    }

    @SuppressLint("SetTextI18n")
    private void rendingItemBadge() {
        if (mItemBadge == null || mBottomNavigationEntity == null) {
            return;
        }
        int num = mBottomNavigationEntity.getBadgeNum();
        if (num > 0) {
            if (num < 99) {
                mItemBadge.setText(String.valueOf(num));
            } else {
                mItemBadge.setText("99+");
            }
            mItemBadge.setVisibility(VISIBLE);
        } else {
            mItemBadge.setVisibility(INVISIBLE);
        }
    }


    private ValueAnimator valueAnimator;

    private void scale(float from, float to) {
        valueAnimator = ValueAnimator.ofFloat(from, to);
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                setScaleX(value);
                setScaleY(value);
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (null != valueAnimator) {
            valueAnimator.cancel();
        }
        super.onDetachedFromWindow();
    }
}
