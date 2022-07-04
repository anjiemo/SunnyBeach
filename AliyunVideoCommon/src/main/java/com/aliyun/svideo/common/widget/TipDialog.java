package com.aliyun.svideo.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.aliyun.svideo.common.R;
import com.aliyun.svideo.common.utils.DensityUtils;


public class TipDialog extends Dialog {
    public TipDialog(Context context) {
        this(context, R.style.TipDialog);
    }

    public TipDialog(Context context, int themeResId) {
        super(context, themeResId);
        setCanceledOnTouchOutside(false);
    }

    public static class Builder {
        private Context mContext;
        public static final int TYPE_DEFAILD = 0;
        public static final int TYPE_MESSAGE_ONLY = 1;
        public static final int TYPE_IMG_ONLY = 5;
        public static final int TYPE_LOADING = 2;
        public static final int TYPE_SUCCESS = 3;
        public static final int TYPE_FAIL = 4;

        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        private int iconId;
        private String message = "";
        private int type = TYPE_DEFAILD;

        public TipDialog create() {
            TipDialog dialog = new TipDialog(mContext);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.alivc_common_dialog_tip);
            ViewGroup contentWrap = (ViewGroup) dialog.findViewById(R.id.contentWrap);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) contentWrap.getLayoutParams();
            switch (type) {
                case TYPE_DEFAILD:
                    params.width = DensityUtils.dip2px(mContext, 300);
                    params.height = DensityUtils.dip2px(mContext, 180);
                    contentWrap.setLayoutParams(params);
                    if (iconId != 0) {
                        addImageView(contentWrap, iconId);
                    }
                    if (!TextUtils.isEmpty(message)) {
                        addTextView(contentWrap);
                    }
                    break;
                case TYPE_MESSAGE_ONLY:
                    contentWrap.setBackgroundResource(R.drawable.alivc_dialog_bg_translucent);
                    params.width = DensityUtils.dip2px(mContext, 150);
                    params.height = DensityUtils.dip2px(mContext, 90);
                    contentWrap.setLayoutParams(params);
                    addTextView(contentWrap);
                    break;
                case TYPE_IMG_ONLY:
                    contentWrap.setBackgroundResource(R.drawable.alivc_dialog_bg_translucent);
                    params.width = DensityUtils.dip2px(mContext, 150);
                    params.height = DensityUtils.dip2px(mContext, 90);
                    contentWrap.setLayoutParams(params);
                    addImageView(contentWrap, iconId);
                    break;
                case TYPE_LOADING:
                    contentWrap.setBackgroundResource(R.drawable.alivc_dialog_bg_translucent);
                    params.width = DensityUtils.dip2px(mContext, 150);
                    params.height = DensityUtils.dip2px(mContext, 90);
                    contentWrap.setLayoutParams(params);
                    addLoadingView(contentWrap);
                    if (TextUtils.isEmpty(message)) {
                        addTextView(contentWrap, mContext.getResources().getString(R.string.alivc_common_loading));
                    } else {
                        addTextView(contentWrap);
                    }
                    break;
                case TYPE_SUCCESS:
                    params.width = DensityUtils.dip2px(mContext, 300);
                    params.height = DensityUtils.dip2px(mContext, 180);
                    contentWrap.setLayoutParams(params);
                    addImageView(contentWrap, R.mipmap.icon_delete_tips);
                    if (TextUtils.isEmpty(message)) {
                        addTextView(contentWrap, mContext.getResources().getString(R.string.alivc_common_operate_success));
                    } else {
                        addTextView(contentWrap);
                    }
                    break;
                case TYPE_FAIL:
                    params.width = DensityUtils.dip2px(mContext, 300);
                    params.height = DensityUtils.dip2px(mContext, 180);
                    contentWrap.setLayoutParams(params);
                    addImageView(contentWrap, R.mipmap.icon_delete_tips);
                    if (TextUtils.isEmpty(message)) {
                        addTextView(contentWrap, mContext.getResources().getString(R.string.alivc_common_operate_success));
                    } else {
                        addTextView(contentWrap);
                    }
                    break;
                default:
                    params.width = DensityUtils.dip2px(mContext, 300);
                    params.height = DensityUtils.dip2px(mContext, 180);
                    contentWrap.setLayoutParams(params);
                    if (iconId != 0) {
                        addImageView(contentWrap, iconId);
                    }
                    if (!TextUtils.isEmpty(message)) {
                        addTextView(contentWrap);
                    }
                    break;
            }

            return dialog;
        }

        private void addTextView(ViewGroup rootView) {
            addTextView(rootView, message);
        }

        private void addTextView(ViewGroup rootView, String tip) {
            TextView tipView = new TextView(mContext);
            LinearLayout.LayoutParams tipViewLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            tipViewLP.topMargin = DensityUtils.dip2px(mContext, 12);
            tipView.setLayoutParams(tipViewLP);
            tipView.setEllipsize(TextUtils.TruncateAt.END);
            tipView.setGravity(Gravity.CENTER);
            tipView.setMaxLines(2);
            tipView.setTextColor(ContextCompat.getColor(mContext, R.color.alivc_common_white));
            tipView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tipView.setText(tip);

            rootView.addView(tipView);

        }

        private void addImageView(ViewGroup rootView, int iconId) {
            ImageView imageView = new ImageView(mContext);
            LinearLayout.LayoutParams imageViewLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(imageViewLP);
            imageView.setImageDrawable(ContextCompat.getDrawable(mContext, iconId));

            rootView.addView(imageView);

        }

        private void addLoadingView(ViewGroup rootView) {
            QMUILoadingView loadingView = new QMUILoadingView(mContext);
            loadingView.setColor(Color.WHITE);
            loadingView.setSize(DensityUtils.dip2px(mContext, 32));
            LinearLayout.LayoutParams loadingViewLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            loadingView.setLayoutParams(loadingViewLP);
            rootView.addView(loadingView);
        }

        public Builder setIconId(int iconId) {
            this.iconId = iconId;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setType(int type) {
            this.type = type;
            return this;
        }
    }

}
