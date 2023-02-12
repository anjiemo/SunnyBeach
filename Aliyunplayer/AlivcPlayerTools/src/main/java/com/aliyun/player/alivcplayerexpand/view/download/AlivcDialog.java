package com.aliyun.player.alivcplayerexpand.view.download;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.player.alivcplayerexpand.R;


/**
 * 离线视频tab, 编辑 --> 删除dialog
 *
 * @author Mulberry
 * create on 2018/4/19.
 */

public class AlivcDialog extends Dialog {

    //确定按钮
    private Button btnConfirm;
    //取消按钮
    private Button btnCancel;
    //消息标题文本
    private ImageView ivDialogIcon;
    //消息提示文本
    private TextView tvMessage;
    //从外界设置的title文本
    private int dialogIcon;
    //从外界设置的消息文本
    private String messageStr;
    //确定文本和取消文本的显示内容
    private String confirmStr, cancelStr;
    //取消按钮被点击了的监听器
    private onCancelOnclickListener onCancelOnclickListener;
    //确定按钮被点击了的监听器
    private onConfirmClickListener onConfirmClickListener;


    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param str
     * @param onCancelOnclickListener
     */
    public void setOnCancelOnclickListener(String str, onCancelOnclickListener onCancelOnclickListener) {
        if (str != null) {
            cancelStr = str;
        }
        this.onCancelOnclickListener = onCancelOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param str
     * @param onYesOnclickListener
     */
    public void setOnConfirmclickListener(String str, onConfirmClickListener onYesOnclickListener) {
        if (str != null) {
            confirmStr = str;
        }
        this.onConfirmClickListener = onYesOnclickListener;
    }


    public AlivcDialog(@NonNull Context context) {
        super(context);
    }

    public AlivcDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public AlivcDialog(@NonNull Context context, boolean cancelable,
                       @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alivc_dialog_delete);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();

    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onConfirmClickListener != null) {
                    onConfirmClickListener.onConfirm();
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCancelOnclickListener != null) {
                    onCancelOnclickListener.onCancel();
                }
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {
        //如果用户自定了title和message
        ivDialogIcon.setBackgroundResource(dialogIcon);
        if (messageStr != null) {
            tvMessage.setText(messageStr);
        }
        //如果设置按钮的文字
        if (confirmStr != null) {
            btnConfirm.setText(confirmStr);
        }
        if (cancelStr != null) {
            btnCancel.setText(cancelStr);
        }
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        btnConfirm = (Button) findViewById(R.id.yes);
        btnCancel = (Button) findViewById(R.id.no);
        ivDialogIcon = (ImageView) findViewById(R.id.iv_dialog_icon);
        tvMessage = (TextView) findViewById(R.id.tv_message);
    }

    /**
     * 从外界Activity为Dialog设置标题
     *
     * @param icon
     */
    public void setDialogIcon(int icon) {
        dialogIcon = icon;
    }

    /**
     * 从外界Activity为Dialog设置dialog的message
     *
     * @param message
     */
    public void setMessage(String message) {
        messageStr = message;
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onConfirmClickListener {
        void onConfirm();
    }

    public interface onCancelOnclickListener {
        void onCancel();
    }
}
