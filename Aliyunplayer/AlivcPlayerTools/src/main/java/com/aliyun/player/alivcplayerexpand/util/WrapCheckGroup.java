package com.aliyun.player.alivcplayerexpand.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lifujun on 2017/6/22.
 */

public class WrapCheckGroup extends WordWrapView {

    List<CheckBox> childs = new LinkedList<CheckBox>();

    public WrapCheckGroup(Context context) {
        super(context);
    }

    public WrapCheckGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapCheckGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addCheckBox(CheckBox checkBox) {
        checkBox.setChecked(false);
        checkBox.setOnClickListener(clickListener);
        childs.add(checkBox);
        addView(checkBox);
    }

    public View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean isChecked = ((CheckBox) v).isChecked();
            clearAllCheck();
            ((CheckBox) v).setChecked(isChecked);
        }
    };

    private void clearAllCheck() {
        for (CheckBox checkBox : childs) {
            if (checkBox.isChecked()) {
                checkBox.setChecked(false);
            }
        }
    }

    public CheckBox getSelectedBox() {
        for (CheckBox checkBox : childs) {
            if (checkBox.isChecked()) {
                return checkBox;
            }
        }
        return null;
    }

    public void removeCheckBox() {
        childs.clear();
        removeAllViews();
    }
}
