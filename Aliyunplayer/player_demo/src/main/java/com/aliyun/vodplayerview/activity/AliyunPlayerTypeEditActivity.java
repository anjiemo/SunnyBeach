package com.aliyun.vodplayerview.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig;
import com.aliyun.vodplayer.R;
import com.aliyun.vodplayerview.fragment.AliyunPlayerTypeAuthFragment;
import com.aliyun.vodplayerview.fragment.AliyunPlayerTypeMpsFragment;
import com.aliyun.vodplayerview.fragment.AliyunPlayerTypeStsFragment;
import com.aliyun.vodplayerview.fragment.AliyunPlayerTypeUrlFragment;
import com.aliyun.vodplayerview.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class AliyunPlayerTypeEditActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 播放方式
     */
    private RadioButton mPlayTypeUrlRadioButton, mPlayTypeStsRadioButton, mPlayTypeMpsRadioButton, mPlayTypeAuthRadioButton;
    /**
     * 播放方式ListView
     */
    private List<RadioButton> mPlayTypeRadioButtonList = new ArrayList<>();

    /**
     * 当前Fragment
     */
    private BaseFragment mCurrentFragment;

    /**
     * 播放方式Fragment
     */
    private BaseFragment mAliyunPlayerTypeUrlFragment, mAliyunPlayerTypeStsFragment, mAliyunPlayerTypeMpsFragment, mAliyunPlayerTypeAuthFragment, mAliyunPlayerTypeLiveStsFragment;
    /**
     * 默认配置
     */
    private TextView mNormalTextView;
    /**
     * 使用此配置
     */
    private TextView mConfirmConfigTextView;
    /**
     * 返回
     */
    private ImageView mBackImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aliyun_player_type_edit);
        initView();
        initFragment();
        initData();
        initListener();
    }

    private void initView() {
        mBackImageView = findViewById(R.id.iv_back);
        mNormalTextView = findViewById(R.id.tv_normal_config);
        mConfirmConfigTextView = findViewById(R.id.tv_confirm_config);

        //播放方式
        mPlayTypeUrlRadioButton = findViewById(R.id.radio_btn_play_type_url);
        mPlayTypeStsRadioButton = findViewById(R.id.radio_btn_play_type_sts);
        mPlayTypeMpsRadioButton = findViewById(R.id.radio_btn_play_type_mps);
        mPlayTypeAuthRadioButton = findViewById(R.id.radio_btn_play_type_auth);
        mPlayTypeRadioButtonList.add(mPlayTypeUrlRadioButton);
        mPlayTypeRadioButtonList.add(mPlayTypeStsRadioButton);
        mPlayTypeRadioButtonList.add(mPlayTypeMpsRadioButton);
        mPlayTypeRadioButtonList.add(mPlayTypeAuthRadioButton);
    }

    private void initData() {
        //初始化选中播放方式
        if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.STS) {
            //sts播放方式
            selectedPlayType(mPlayTypeStsRadioButton);
            switchFragment(mAliyunPlayerTypeStsFragment);
        } else if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.MPS) {
            //mps播放方式
            selectedPlayType(mPlayTypeMpsRadioButton);
            switchFragment(mAliyunPlayerTypeMpsFragment);
        } else if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.AUTH) {
            //auth播放方式
            selectedPlayType(mPlayTypeAuthRadioButton);
            switchFragment(mAliyunPlayerTypeAuthFragment);
        } else {
            //如果是default或者url播放方式，选中url播放方式
            selectedPlayType(mPlayTypeUrlRadioButton);
            switchFragment(mAliyunPlayerTypeUrlFragment);
        }
    }

    private void initFragment() {
        mAliyunPlayerTypeUrlFragment = new AliyunPlayerTypeUrlFragment();
        mAliyunPlayerTypeStsFragment = new AliyunPlayerTypeStsFragment();
        mAliyunPlayerTypeMpsFragment = new AliyunPlayerTypeMpsFragment();
        mAliyunPlayerTypeAuthFragment = new AliyunPlayerTypeAuthFragment();
    }

    private void initListener() {
        mBackImageView.setOnClickListener(this);
        mNormalTextView.setOnClickListener(this);
        mConfirmConfigTextView.setOnClickListener(this);

        mPlayTypeUrlRadioButton.setOnClickListener(this);
        mPlayTypeStsRadioButton.setOnClickListener(this);
        mPlayTypeMpsRadioButton.setOnClickListener(this);
        mPlayTypeAuthRadioButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mBackImageView) {
            finish();
        } else if (view == mPlayTypeUrlRadioButton) {
            //url播放方式
            GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.URL;
            selectedPlayType(mPlayTypeUrlRadioButton);
            switchFragment(mAliyunPlayerTypeUrlFragment);
        } else if (view == mPlayTypeStsRadioButton) {
            //sts播放方式
            GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.STS;
            selectedPlayType(mPlayTypeStsRadioButton);
            switchFragment(mAliyunPlayerTypeStsFragment);
        } else if (view == mPlayTypeMpsRadioButton) {
            //mps播放方式
            GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.MPS;
            selectedPlayType(mPlayTypeMpsRadioButton);
            switchFragment(mAliyunPlayerTypeMpsFragment);
        } else if (view == mPlayTypeAuthRadioButton) {
            //auth播放方式
            GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.AUTH;
            selectedPlayType(mPlayTypeAuthRadioButton);
            switchFragment(mAliyunPlayerTypeAuthFragment);
        } else if (view == mConfirmConfigTextView) {
            //使用此配置
            judgeCurrentPlayType();
            if (mCurrentFragment != null) {
                mCurrentFragment.confirmPlayInfo();
            }
            setResult(Activity.RESULT_OK);
            finish();
        } else if (view == mNormalTextView) {
            //使用默认配置
            if (mCurrentFragment != null) {
                mCurrentFragment.defaultPlayInfo();
            }
        }
    }

    private void judgeCurrentPlayType() {
        if (mPlayTypeUrlRadioButton.isChecked()) {
            GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.URL;
        } else if (mPlayTypeStsRadioButton.isChecked()) {
            GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.STS;
        } else if (mPlayTypeAuthRadioButton.isChecked()) {
            GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.AUTH;
        } else if (mPlayTypeMpsRadioButton.isChecked()) {
            GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.MPS;
        } else {
            GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.URL;
        }
    }

    /**
     * 选择播放方式
     */
    private void selectedPlayType(RadioButton selectedRadioButton) {
        if (mPlayTypeRadioButtonList != null && selectedRadioButton != null) {
            for (RadioButton radioButton : mPlayTypeRadioButtonList) {
                radioButton.setChecked(radioButton == selectedRadioButton);
            }
        }
    }

    /**
     * 根据不同的播放方式切换Fragment
     */
    private void switchFragment(BaseFragment targetFragment) {

        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            if (mCurrentFragment != null) {
                mFragmentTransaction.hide(mCurrentFragment);
            }
            mFragmentTransaction.add(R.id.framelayout_input_content, targetFragment);
        } else {
            mFragmentTransaction.hide(mCurrentFragment).show(targetFragment);
        }
        //更改当前的fragment所指向的值
        mCurrentFragment = targetFragment;
        mFragmentTransaction.commit();

    }
}
