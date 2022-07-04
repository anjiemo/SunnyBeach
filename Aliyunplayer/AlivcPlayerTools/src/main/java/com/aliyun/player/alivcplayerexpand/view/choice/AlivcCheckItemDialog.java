package com.aliyun.player.alivcplayerexpand.view.choice;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.player.aliyunplayerbase.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mulberry
 * create on 2018/5/15.
 */

public class AlivcCheckItemDialog extends Dialog {

    private static final String TAG = AlivcCheckItemDialog.class.getName();

    private final static int ANIMATION_DURATION = 200;
    private View mContentView;
    private boolean mIsAnimating = false;

    public AlivcCheckItemDialog(@NonNull Context context) {
        super(context, R.style.BottomCheckDialog);
    }

    public AlivcCheckItemDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setPadding(0, 0, 0, 0);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM | Gravity.CENTER;

        int screenWidth = ScreenUtils.getWidth(getContext());
        int screenHeight = ScreenUtils.getHeight(getContext());
        params.width = screenWidth < screenHeight ? screenWidth : screenHeight;
        getWindow().setAttributes(params);
        setCanceledOnTouchOutside(true);
    }

    @Override
    public void setContentView(int layoutResID) {
        mContentView = LayoutInflater.from(getContext()).inflate(layoutResID, null);
        super.setContentView(layoutResID);
    }

    @Override
    public void setContentView(@NonNull View view, @Nullable LayoutParams params) {
        mContentView = view;
        super.setContentView(view, params);
    }

    public View getContentView() {
        return mContentView;
    }

    @Override
    public void setContentView(View mContentView) {
        this.mContentView = mContentView;
    }

    /**
     * ChoiceItemBottomDialog从下往上升起的动画动画
     */
    private void animateUp() {
        if (mContentView != null) {
            return;
        }
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setInterpolator(new DecelerateInterpolator());
        animationSet.setDuration(ANIMATION_DURATION);
        animationSet.setFillAfter(true);
        mContentView.startAnimation(animationSet);
    }

    /**
     * ChoiceItemBottomDialog从下往上升起的动画动画
     */
    private void animateDown() {
        if (mContentView == null) {
            return;
        }
        TranslateAnimation translate = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f
        );
        AlphaAnimation alpha = new AlphaAnimation(1, 0);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(translate);
        set.addAnimation(alpha);
        set.setInterpolator(new DecelerateInterpolator());
        set.setDuration(ANIMATION_DURATION);
        set.setFillAfter(true);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsAnimating = false;
                mContentView.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            AlivcCheckItemDialog.super.dismiss();
                        } catch (Exception e) {
                            Log.w(TAG, "dismiss error\n" + Log.getStackTraceString(e));
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mContentView.startAnimation(set);
    }

    @Override
    public void show() {
        super.show();
        animateUp();
        if (onChoiceItemListener != null) {
            onChoiceItemListener.onShow();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mIsAnimating) {
            return;
        }
        animateDown();
    }


    OnChoiceItemListener onChoiceItemListener;

    public void setOnChoiceItemListener(
            OnChoiceItemListener onChoiceButtomItemListener) {
        this.onChoiceItemListener = onChoiceButtomItemListener;
    }

    public interface OnChoiceItemListener {
        /**
         * 显示
         */
        void onShow();
    }

    /**
     * 生成底部选择{@link AlivcCheckItemDialog}对话框
     */
    public static class BottomListCheckBuilder {
        private Context mContext;
        private AlivcCheckItemDialog alivcCheckItemDialog;
        private List<BottomCheckListItemData> mItems;
        private RecyclerView recyclerView;
        private int mCheckedIndex;

        private OnCheckItemClickListener onCheckItemClickListener;
        private OnDismissListener onBottomDialogDismissListener;

        public BottomListCheckBuilder(Context context) {
            mContext = context;
            mItems = new ArrayList<>();
        }

        /**
         * 设置要被选择的item的下标
         *
         * @param mCheckedIndex
         * @return
         */
        public BottomListCheckBuilder setCheckedIndex(int mCheckedIndex) {
            this.mCheckedIndex = mCheckedIndex;
            return this;
        }

        /**
         * @param typeAndTag Item 的文字内容，同时会把内容设置为 tag。
         */
        public BottomListCheckBuilder addItem(String typeAndTag, String value) {
            mItems.add(new BottomCheckListItemData(typeAndTag, value, typeAndTag));
            return this;
        }

        /**
         * 设置item点击事件
         *
         * @param onCheckItemClickListener
         * @return
         */
        public BottomListCheckBuilder setOnCheckItemClickListener(
                OnCheckItemClickListener onCheckItemClickListener) {
            this.onCheckItemClickListener = onCheckItemClickListener;
            return this;
        }

        /**
         * dialog dismiss添加回调
         *
         * @param onBottomDialogDismissListener
         * @return
         */
        public BottomListCheckBuilder setOnBottomDialogDismissListener(
                OnDismissListener onBottomDialogDismissListener) {
            this.onBottomDialogDismissListener = onBottomDialogDismissListener;
            return this;
        }

        /**
         * 构建一个AlivcCheckItemDialog
         *
         * @return
         */
        public AlivcCheckItemDialog build() {
            alivcCheckItemDialog = new AlivcCheckItemDialog(mContext);
            View contentView = buildViews();
            alivcCheckItemDialog.setContentView(contentView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            if (onBottomDialogDismissListener != null) {
                alivcCheckItemDialog.setOnDismissListener(onBottomDialogDismissListener);
            }
            return alivcCheckItemDialog;
        }

        private View buildViews() {
            View wrapperView = View.inflate(mContext, getContentViewLayoutId(), null);
            TextView tvCloseBottomCheck = (TextView) wrapperView.findViewById(R.id.tv_close_bottom_check);
            RecyclerView mContainerView = (RecyclerView) wrapperView.findViewById(R.id.check_list_view);

            tvCloseBottomCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (alivcCheckItemDialog != null) {
                        alivcCheckItemDialog.dismiss();
                    }
                }
            });
            //if (needToScroll()) {
            mContainerView.getLayoutParams().height = getListMaxHeight();
            alivcCheckItemDialog.setOnChoiceItemListener(new OnChoiceItemListener() {
                @Override
                public void onShow() {
                    //onshow do Something
                }
            });
            //}

            mContainerView.setLayoutManager(new LinearLayoutManager(mContext));
            CheckListAdapter mAdapter = new CheckListAdapter();
            mContainerView.setAdapter(mAdapter);
            return wrapperView;
        }

        /**
         * 注意:这里只考虑List的高度,如果有title或者headerView,不计入考虑中
         */
        protected int getListMaxHeight() {
            return (int) (ScreenUtils.getHeight(mContext) * 0.5);
        }

        protected int getContentViewLayoutId() {
            return R.layout.alivc_check_list_view_layout;
        }

        public interface OnCheckItemClickListener {
            void onClick(AlivcCheckItemDialog dialog, View itemView, int position, String tag);
        }

        private static class BottomCheckListItemData {
            String type;
            String value;
            String tag;

            public BottomCheckListItemData(String type, String value, String tag) {
                this.type = type;
                this.value = value;
                this.tag = tag;
            }
        }

        public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.ViewHolder> {

            public class ViewHolder extends RecyclerView.ViewHolder {
                LinearLayout bottomDialogListItem;
                TextView type;
                TextView value;

                public ViewHolder(View itemView) {
                    super(itemView);
                    bottomDialogListItem = (LinearLayout) itemView.findViewById(R.id.bottom_dialog_list_item);
                    type = (TextView) itemView.findViewById(R.id.bottom_dialog_list_item_type);
                    value = (TextView) itemView.findViewById(R.id.bottom_dialog_list_item_value);
                }
            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                CheckListAdapter.ViewHolder viewHolder = new CheckListAdapter.ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.alivc_check_list_item, parent, false));
                return viewHolder;
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, final int position) {
                holder.type.setText(mItems.get(position).type);
                holder.value.setText(mItems.get(position).value);

                holder.bottomDialogListItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCheckItemClickListener.onClick(alivcCheckItemDialog, v, position, mItems.get(position).tag);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return mItems.size();
            }

            @Override
            public int getItemViewType(int position) {
                return super.getItemViewType(position);
            }

        }


    }


}
