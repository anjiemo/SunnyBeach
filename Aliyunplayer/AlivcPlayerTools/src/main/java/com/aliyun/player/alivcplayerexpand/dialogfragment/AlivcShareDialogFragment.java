package com.aliyun.player.alivcplayerexpand.dialogfragment;

import android.view.View;

import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.svideo.common.base.BaseDialogFragment;

/**
 * 分享 DialogFragment
 */
public class AlivcShareDialogFragment extends BaseDialogFragment {


    public static AlivcShareDialogFragment newInstance() {
        AlivcShareDialogFragment dialogFragment = new AlivcShareDialogFragment();
        return dialogFragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.alivc_video_dialogfragment_share;
    }

    @Override
    protected void bindView(View view) {
        view.findViewById(R.id.alivc_tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.iv_wx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        view.findViewById(R.id.iv_wb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    protected int getDialogAnimationRes() {
        return R.style.Dialog_Animation;
    }
}
