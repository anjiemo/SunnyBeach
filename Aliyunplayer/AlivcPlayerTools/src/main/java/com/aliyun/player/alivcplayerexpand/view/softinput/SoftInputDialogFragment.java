package com.aliyun.player.alivcplayerexpand.view.softinput;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.svideo.common.base.BaseDialogFragment;

public class SoftInputDialogFragment extends BaseDialogFragment {


    private EditText mEditText;
    private TextView mTextView;
    private OnBarrageSendClickListener mOnBarrageSendClickListener;

    public static SoftInputDialogFragment newInstance() {
        SoftInputDialogFragment dialogFragment = new SoftInputDialogFragment();
        return dialogFragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.alivc_softinput_send_danmaku;
    }

    @Override
    protected void bindView(View view) {

        mTextView = view.findViewById(R.id.alivc_tv_input_send);
        mEditText = view.findViewById(R.id.alivc_et_input_danmu);

        initView();
    }

    private void initView() {


        mEditText.findFocus();
        mEditText.setFocusable(true);
        mEditText.setFocusableInTouchMode(true);
        mEditText.requestFocus();
        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        //flagNoExtractUi
        mEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        mEditText.setSingleLine(true);

        showSoftInput(mEditText);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int mTextMaxlenght = 0;
                Editable editable = mEditText.getText();
                String str = editable.toString().trim();
                //得到最初字段的长度大小，用于光标位置的判断
                int selEndIndex = Selection.getSelectionEnd(editable);
                // 取出每个字符进行判断，如果是字母数字和标点符号则为一个字符加1，
                //如果是汉字则为两个字符
                for (int i = 0; i < str.length(); i++) {
                    char charAt = str.charAt(i);
                    //32-122包含了空格，大小写字母，数字和一些常用的符号，
                    //如果在这个范围内则算一个字符，
                    //如果不在这个范围比如是汉字的话就是两个字符
                    if (charAt >= 32 && charAt <= 122) {
                        mTextMaxlenght++;
                    } else {
                        mTextMaxlenght += 2;
                    }
                    // 当最大字符大于40时，进行字段的截取，并进行提示字段的大小
                    if (mTextMaxlenght > 40) {
                        // 截取最大的字段
                        String newStr = str.substring(0, i);
                        mEditText.setText(newStr);
                        // 得到新字段的长度值
                        editable = mEditText.getText();
                        int newLen = editable.length();
                        if (selEndIndex > newLen) {
                            selEndIndex = editable.length();
                        }
                        // 设置新光标所在的位置
                        Selection.setSelection(editable, selEndIndex);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBarrageSendClickListener != null) {
                    //发送弹幕
                    String danmu = mEditText.getText().toString();
                    mOnBarrageSendClickListener.onBarrageSendClick(danmu);
                }
            }
        });
    }

    /**
     * 发送弹幕监听
     */
    public interface OnBarrageSendClickListener {
        /**
         * 弹幕发送按钮点击
         */
        void onBarrageSendClick(String danmu);
    }

    public void setOnBarrageSendClickListener(OnBarrageSendClickListener listener) {
        this.mOnBarrageSendClickListener = listener;
    }

    /**
     * 显示软键盘
     */
    private void showSoftInput(EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, 0);
    }
}
