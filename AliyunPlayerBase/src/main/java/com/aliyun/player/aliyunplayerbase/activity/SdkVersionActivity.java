package com.aliyun.player.aliyunplayerbase.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.aliyunplayerbase.R;

/**
 * 显示sdk的版本信息
 * 短视频的三个版本都会使用
 */
public class SdkVersionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdk_version);
        showVersionInfo();
    }

    @SuppressLint("SetTextI18n")
    private void showVersionInfo() {
        ((TextView) findViewById(R.id.tv_version)).setText("VERSION :" + AliPlayerFactory.getSdkVersion());
    }
}
