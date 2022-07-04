package com.aliyun.vodplayerview.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.player.IPlayer;
import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig;
import com.aliyun.player.alivcplayerexpand.util.Common;
import com.aliyun.player.alivcplayerexpand.util.database.DatabaseManager;
import com.aliyun.player.alivcplayerexpand.util.database.LoadDbDatasListener;
import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadManager;
import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadMediaInfo;
import com.aliyun.player.aliyunplayerbase.activity.BaseActivity;
import com.aliyun.player.aliyunplayerbase.bean.AliyunMps;
import com.aliyun.player.aliyunplayerbase.bean.AliyunPlayAuth;
import com.aliyun.player.aliyunplayerbase.bean.AliyunSts;
import com.aliyun.player.aliyunplayerbase.bean.AliyunVideoList;
import com.aliyun.player.aliyunplayerbase.net.GetAuthInformation;
import com.aliyun.private_service.PrivateService;
import com.aliyun.svideo.common.utils.FileUtils;
import com.aliyun.svideo.common.utils.ToastUtils;
import com.aliyun.vodplayer.R;
import com.aliyun.vodplayerview.global.Global;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AliyunPlayerSettingActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 播放器播放方式 -- 博播放方式编辑界面 requestCode
     */
    private static final int PLAY_TYPE_EDIT_ACTIVITY_REQUEST = 0x0001;
    /**
     * 开始播放
     */
    private TextView mStartPlayTextView;
    /**
     * 其他设置
     */
    private RadioGroup mDecodeRadioGroup, mMirrorRadioGroup, mAutoSwithRadioGroup, mSeekModuleRadioGroup, mEnableBackgroundRadioGroup;
    private ImageView mPlayConfigSettingImageView;

    /**
     * 播放方式
     */
    private RadioButton mPlayTypeUrlRadioButton, mPlayTypeStsRadioButton, mPlayTypeMpsRadioButton, mPlayTypeAuthRadioButton, mPlayTypeDefaultRadioButton;
    /**
     * 镜像模式
     */
    private RadioButton mPlayTypeRotate0RadioButton, mPlayTypeRotate90RadioButton, mPlayTypeRotate180RadioButton, mPlayTypeRotate270RadioButton;
    /**
     * 编辑
     */
    private TextView mPlayTypeEditTextView;
    /**
     * 播放方式ListView
     */
    private List<RadioButton> mPlayTypeRadioButtonList = new ArrayList<>();
    /**
     * 加载中ProgressBar
     */
    private ProgressBar mLoadingProgressBar;
    /**
     * 返回
     */
    private ImageView mBackImageView;
    /**
     * 起播码率
     */
    private RadioButton mMutiRate400RadioButton, mMutiRate900RadioButton, mMutiRate1500RadioButton, mMutiRate3000RadioButton, mMutiRate3500RadioButton, mMutiRate6000RadioButton;
    /**
     * 起播码率 List
     */
    private List<RadioButton> mPlayMutiRateRadioButtonList = new ArrayList<>();
    private Common commenUtils;
    /**
     * 下载帮助类
     */
    private AliyunDownloadManager mAliyunDownloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        copyAssets();
        setContentView(R.layout.activity_aliyun_player_setting);

        initDownloadInfo();
        initView();
        initListener();
        initCacheDir();
        initDataBase();
        initGlobalConfig();
    }

    /**
     * 初始化配置
     */
    private void initGlobalConfig() {
        GlobalPlayerConfig.mEnableHardDecodeType = true;
        GlobalPlayerConfig.PlayConfig.mAutoSwitchOpen = false;
        GlobalPlayerConfig.PlayConfig.mEnablePlayBackground = false;
        GlobalPlayerConfig.PlayConfig.mEnableAccurateSeekModule = false;
        GlobalPlayerConfig.mRotateMode = IPlayer.RotateMode.ROTATE_0;
        GlobalPlayerConfig.mMirrorMode = IPlayer.MirrorMode.MIRROR_MODE_NONE;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (commenUtils != null) {
            commenUtils.onDestroy();
            commenUtils = null;
        }
        if (Global.mDownloadMediaLists != null && Global.mDownloadMediaLists.size() > 0) {
            Global.mDownloadMediaLists.clear();
        }

    }

    private void copyAssets() {
        final String encryptPath = FileUtils.getDir(getApplicationContext()) + GlobalPlayerConfig.ENCRYPT_DIR_PATH;
        commenUtils = Common.getInstance(getApplicationContext()).copyAssetsToSD("encrypt", encryptPath);
        commenUtils.setFileOperateCallback(

                new Common.FileOperateCallback() {

                    @Override
                    public void onSuccess() {
                        PrivateService.initService(getApplicationContext(), encryptPath + "encryptedApp.dat");
                    }

                    @Override
                    public void onFailed(String error) {
                        Toast.makeText(AliyunPlayerSettingActivity.this, "encrypt copy error : " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 初始化下载相关信息
     */
    private void initDownloadInfo() {
        Global.mDownloadMediaLists = new ArrayList<>();
        DatabaseManager.getInstance().createDataBase(this);
        mAliyunDownloadManager = AliyunDownloadManager.getInstance(getApplicationContext());
        mAliyunDownloadManager.setDownloadDir(FileUtils.getDir(getApplicationContext()) + GlobalPlayerConfig.DOWNLOAD_DIR_PATH);
    }

    private void initCacheDir() {
        //设置边播边缓存路径
        File externalFilesDir = this.getExternalFilesDir(null);
        if (externalFilesDir != null) {
            if (!externalFilesDir.exists()) {
                externalFilesDir.mkdirs();
                GlobalPlayerConfig.PlayCacheConfig.mDir = externalFilesDir.getAbsolutePath();
            }
        }
    }

    private void initDataBase() {
        mAliyunDownloadManager.findDatasByDb(new LoadDbDatasListener() {
            @Override
            public void onLoadSuccess(List<AliyunDownloadMediaInfo> dataList) {
                Global.mDownloadMediaLists.addAll(dataList);
            }
        });
    }

    private void initView() {
        mBackImageView = findViewById(R.id.iv_back);
        mStartPlayTextView = findViewById(R.id.tv_start_play);
        mLoadingProgressBar = findViewById(R.id.loading_progress);
        mPlayConfigSettingImageView = findViewById(R.id.iv_config_setting);

        mDecodeRadioGroup = findViewById(R.id.radio_group_decode);
        mMirrorRadioGroup = findViewById(R.id.radio_group_mirror);
        mAutoSwithRadioGroup = findViewById(R.id.radio_group_auto_switch);
        mSeekModuleRadioGroup = findViewById(R.id.radio_group_seek_module);
        mEnableBackgroundRadioGroup = findViewById(R.id.radio_group_enable_background);
        mPlayTypeEditTextView = findViewById(R.id.tv_play_type_edit);

        //播放方式
        mPlayTypeUrlRadioButton = findViewById(R.id.radio_btn_play_type_url);
        mPlayTypeStsRadioButton = findViewById(R.id.radio_btn_play_type_sts);
        mPlayTypeMpsRadioButton = findViewById(R.id.radio_btn_play_type_mps);
        mPlayTypeAuthRadioButton = findViewById(R.id.radio_btn_play_type_auth);
        mPlayTypeDefaultRadioButton = findViewById(R.id.radio_btn_play_type_default);
        mPlayTypeRadioButtonList.add(mPlayTypeUrlRadioButton);
        mPlayTypeRadioButtonList.add(mPlayTypeStsRadioButton);
        mPlayTypeRadioButtonList.add(mPlayTypeMpsRadioButton);
        mPlayTypeRadioButtonList.add(mPlayTypeAuthRadioButton);
        mPlayTypeRadioButtonList.add(mPlayTypeDefaultRadioButton);

        //起播码率
        mMutiRate400RadioButton = findViewById(R.id.radio_rate_400);
        mMutiRate900RadioButton = findViewById(R.id.radio_rate_900);
        mMutiRate1500RadioButton = findViewById(R.id.radio_rate_1500);
        mMutiRate3000RadioButton = findViewById(R.id.radio_rate_3000);
        mMutiRate3500RadioButton = findViewById(R.id.radio_rate_3500);
        mMutiRate6000RadioButton = findViewById(R.id.radio_rate_6000);
        mPlayMutiRateRadioButtonList.add(mMutiRate400RadioButton);
        mPlayMutiRateRadioButtonList.add(mMutiRate900RadioButton);
        mPlayMutiRateRadioButtonList.add(mMutiRate1500RadioButton);
        mPlayMutiRateRadioButtonList.add(mMutiRate3000RadioButton);
        mPlayMutiRateRadioButtonList.add(mMutiRate3500RadioButton);
        mPlayMutiRateRadioButtonList.add(mMutiRate6000RadioButton);

        //镜像模式
        mPlayTypeRotate0RadioButton = findViewById(R.id.radio_btn_rotate_0);
        mPlayTypeRotate90RadioButton = findViewById(R.id.radio_btn_rotate_90);
        mPlayTypeRotate180RadioButton = findViewById(R.id.radio_btn_rotate_180);
        mPlayTypeRotate270RadioButton = findViewById(R.id.radio_btn_rotate_270);
    }

    private void initListener() {
        mBackImageView.setOnClickListener(this);
        mStartPlayTextView.setOnClickListener(this);
        mPlayTypeEditTextView.setOnClickListener(this);
        mPlayConfigSettingImageView.setOnClickListener(this);

        //播放方式
        mPlayTypeUrlRadioButton.setOnClickListener(this);
        mPlayTypeStsRadioButton.setOnClickListener(this);
        mPlayTypeMpsRadioButton.setOnClickListener(this);
        mPlayTypeAuthRadioButton.setOnClickListener(this);
        mPlayTypeDefaultRadioButton.setOnClickListener(this);

        mMutiRate400RadioButton.setOnClickListener(this);
        mMutiRate900RadioButton.setOnClickListener(this);
        mMutiRate1500RadioButton.setOnClickListener(this);
        mMutiRate3000RadioButton.setOnClickListener(this);
        mMutiRate3500RadioButton.setOnClickListener(this);
        mMutiRate6000RadioButton.setOnClickListener(this);

        //镜像模式
        mPlayTypeRotate0RadioButton.setOnClickListener(this);
        mPlayTypeRotate90RadioButton.setOnClickListener(this);
        mPlayTypeRotate180RadioButton.setOnClickListener(this);
        mPlayTypeRotate270RadioButton.setOnClickListener(this);

        //解码方式
        mDecodeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                GlobalPlayerConfig.mEnableHardDecodeType = checkedId != R.id.radio_btn_decode_soft;
            }
        });

        //镜像模式
        mMirrorRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radio_btn_mirror_none) {
                    GlobalPlayerConfig.mMirrorMode = IPlayer.MirrorMode.MIRROR_MODE_NONE;
                } else if (checkedId == R.id.radio_btn_mirror_vertical) {
                    GlobalPlayerConfig.mMirrorMode = IPlayer.MirrorMode.MIRROR_MODE_VERTICAL;
                } else if (checkedId == R.id.radio_btn_mirror_horizontal) {
                    GlobalPlayerConfig.mMirrorMode = IPlayer.MirrorMode.MIRROR_MODE_HORIZONTAL;
                } else {
                    GlobalPlayerConfig.mMirrorMode = IPlayer.MirrorMode.MIRROR_MODE_NONE;
                }
            }
        });

        //auto自动开关
        mAutoSwithRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radio_btn_auto_open) {
                    GlobalPlayerConfig.PlayConfig.mAutoSwitchOpen = true;
                } else {
                    GlobalPlayerConfig.PlayConfig.mAutoSwitchOpen = false;
                }
            }
        });

        //seek模式
        mSeekModuleRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radio_btn_seek_accurate) {
                    GlobalPlayerConfig.PlayConfig.mEnableAccurateSeekModule = true;
                } else {
                    GlobalPlayerConfig.PlayConfig.mEnableAccurateSeekModule = false;
                }
            }
        });

        //是否允许后台播放
        mEnableBackgroundRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radio_btn_background_open) {
                    GlobalPlayerConfig.PlayConfig.mEnablePlayBackground = true;
                } else {
                    GlobalPlayerConfig.PlayConfig.mEnablePlayBackground = false;
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view == mBackImageView) {
            finish();
        } else if (view == mStartPlayTextView) {
            //开始播放
            getCurrentPlayType();
            checkedIsNeedNormalData();
        } else if (view == mPlayTypeEditTextView) {
            //编辑
            getCurrentPlayType();
            Intent intent = new Intent(this, AliyunPlayerTypeEditActivity.class);
            startActivityForResult(intent, PLAY_TYPE_EDIT_ACTIVITY_REQUEST);
        } else if (view == mPlayConfigSettingImageView) {
            Intent intent = new Intent(this, AliyunPlayerConfigActivity.class);
            startActivity(intent);
        } else if (view == mPlayTypeUrlRadioButton) {
            //url播放方式
            GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.URL;
            selectedPlayType();
        } else if (view == mPlayTypeStsRadioButton) {
            //sts播放方式
            GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.STS;
            selectedPlayType();
        } else if (view == mPlayTypeMpsRadioButton) {
            //mps播放方式
            GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.MPS;
            selectedPlayType();
        } else if (view == mPlayTypeAuthRadioButton) {
            //auth播放方式
            GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.AUTH;
            selectedPlayType();
        } else if (view == mPlayTypeDefaultRadioButton) {
            //默认播放方式
            GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.DEFAULT;
            selectedPlayType();
        } else if (view == mMutiRate400RadioButton) {
            //起播码率400
            GlobalPlayerConfig.mCurrentMutiRate = GlobalPlayerConfig.MUTIRATE.RATE_400;
            selectMutiRate();
        } else if (view == mMutiRate900RadioButton) {
            //起播码率900
            GlobalPlayerConfig.mCurrentMutiRate = GlobalPlayerConfig.MUTIRATE.RATE_900;
            selectMutiRate();
        } else if (view == mMutiRate1500RadioButton) {
            //起播码率1500
            GlobalPlayerConfig.mCurrentMutiRate = GlobalPlayerConfig.MUTIRATE.RATE_1500;
            selectMutiRate();
        } else if (view == mMutiRate3000RadioButton) {
            //起播码率3000
            GlobalPlayerConfig.mCurrentMutiRate = GlobalPlayerConfig.MUTIRATE.RATE_3000;
            selectMutiRate();
        } else if (view == mMutiRate3500RadioButton) {
            //起播码率3500
            GlobalPlayerConfig.mCurrentMutiRate = GlobalPlayerConfig.MUTIRATE.RATE_3500;
            selectMutiRate();
        } else if (view == mMutiRate6000RadioButton) {
            //起播码率6000
            GlobalPlayerConfig.mCurrentMutiRate = GlobalPlayerConfig.MUTIRATE.RATE_6000;
            selectMutiRate();
        } else if (view == mPlayTypeRotate0RadioButton) {
            //旋转0
            GlobalPlayerConfig.mRotateMode = IPlayer.RotateMode.ROTATE_0;
            mPlayTypeRotate0RadioButton.setChecked(true);
            mPlayTypeRotate90RadioButton.setChecked(false);
            mPlayTypeRotate180RadioButton.setChecked(false);
            mPlayTypeRotate270RadioButton.setChecked(false);
        } else if (view == mPlayTypeRotate90RadioButton) {
            //旋转90
            GlobalPlayerConfig.mRotateMode = IPlayer.RotateMode.ROTATE_90;
            mPlayTypeRotate90RadioButton.setChecked(true);
            mPlayTypeRotate0RadioButton.setChecked(false);
            mPlayTypeRotate180RadioButton.setChecked(false);
            mPlayTypeRotate270RadioButton.setChecked(false);
        } else if (view == mPlayTypeRotate180RadioButton) {
            //旋转180
            GlobalPlayerConfig.mRotateMode = IPlayer.RotateMode.ROTATE_180;
            mPlayTypeRotate180RadioButton.setChecked(true);
            mPlayTypeRotate0RadioButton.setChecked(false);
            mPlayTypeRotate90RadioButton.setChecked(false);
            mPlayTypeRotate270RadioButton.setChecked(false);
        } else if (view == mPlayTypeRotate270RadioButton) {
            //旋转270
            GlobalPlayerConfig.mRotateMode = IPlayer.RotateMode.ROTATE_270;
            mPlayTypeRotate270RadioButton.setChecked(true);
            mPlayTypeRotate0RadioButton.setChecked(false);
            mPlayTypeRotate90RadioButton.setChecked(false);
            mPlayTypeRotate180RadioButton.setChecked(false);
        }
    }

    /**
     * 选择播放方式
     */
    private void selectedPlayType() {
        RadioButton selectedRadioButton;
        if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.STS) {
            selectedRadioButton = mPlayTypeStsRadioButton;
        } else if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.AUTH) {
            selectedRadioButton = mPlayTypeAuthRadioButton;
        } else if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.MPS) {
            selectedRadioButton = mPlayTypeMpsRadioButton;
        } else if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.URL) {
            selectedRadioButton = mPlayTypeUrlRadioButton;
        } else {
            selectedRadioButton = mPlayTypeDefaultRadioButton;
        }

        if (mPlayTypeRadioButtonList != null && selectedRadioButton != null) {
            for (RadioButton radioButton : mPlayTypeRadioButtonList) {
                radioButton.setChecked(radioButton == selectedRadioButton);
            }
        }
    }

    /**
     * 获取当前选中的播放方式
     */
    private void getCurrentPlayType() {
        if (mPlayTypeStsRadioButton.isChecked()) {
            GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.STS;
        } else if (mPlayTypeAuthRadioButton.isChecked()) {
            GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.AUTH;
        } else if (mPlayTypeMpsRadioButton.isChecked()) {
            GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.MPS;
        } else if (mPlayTypeDefaultRadioButton.isChecked()) {
            GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.DEFAULT;
        } else {
            GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.URL;
        }
    }

    /**
     * 选中起播码率
     */
    private void selectMutiRate() {
        RadioButton selectedRadioButton;
        if (GlobalPlayerConfig.mCurrentMutiRate == GlobalPlayerConfig.MUTIRATE.RATE_400) {
            selectedRadioButton = mMutiRate400RadioButton;
        } else if (GlobalPlayerConfig.mCurrentMutiRate == GlobalPlayerConfig.MUTIRATE.RATE_900) {
            selectedRadioButton = mMutiRate900RadioButton;
        } else if (GlobalPlayerConfig.mCurrentMutiRate == GlobalPlayerConfig.MUTIRATE.RATE_1500) {
            selectedRadioButton = mMutiRate1500RadioButton;
        } else if (GlobalPlayerConfig.mCurrentMutiRate == GlobalPlayerConfig.MUTIRATE.RATE_3000) {
            selectedRadioButton = mMutiRate3000RadioButton;
        } else if (GlobalPlayerConfig.mCurrentMutiRate == GlobalPlayerConfig.MUTIRATE.RATE_3500) {
            selectedRadioButton = mMutiRate3500RadioButton;
        } else {
            //6000
            selectedRadioButton = mMutiRate6000RadioButton;
        }

        if (mPlayMutiRateRadioButtonList != null && selectedRadioButton != null) {
            for (RadioButton radioButton : mPlayMutiRateRadioButtonList) {
                radioButton.setChecked(radioButton == selectedRadioButton);
            }
        }
    }

    /**
     * 检查是否需要默认源
     */
    private void checkedIsNeedNormalData() {
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        GetAuthInformation getAuthInformation = new GetAuthInformation();
        if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.URL && !GlobalPlayerConfig.URL_TYPE_CHECKED) {

            getAuthInformation.getVideoPlayUrlInfo(new GetAuthInformation.OnGetUrlInfoListener() {
                @Override
                public void onGetUrlError(String msg) {
                    mLoadingProgressBar.setVisibility(View.GONE);
                    ToastUtils.show(AliyunPlayerSettingActivity.this, msg);
                }

                @Override
                public void onGetUrlSuccess(AliyunVideoList.VideoList dataBean) {
                    mLoadingProgressBar.setVisibility(View.GONE);
                    if (dataBean != null) {
                        List<AliyunVideoList.VideoList.VideoListItem> playInfoList = dataBean.getPlayInfoList();
                        if (playInfoList != null && playInfoList.size() > 0) {
                            AliyunVideoList.VideoList.VideoListItem videoListItem = playInfoList.get(0);
                            GlobalPlayerConfig.mUrlPath = videoListItem.getPlayURL();
                            startPlay();
                        }
                    }
                }
            });

        } else if ((GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.STS && !GlobalPlayerConfig.STS_TYPE_CHECKED)) {
            getAuthInformation.getVideoPlayStsInfo(new GetAuthInformation.OnGetStsInfoListener() {
                @Override
                public void onGetStsError(String msg) {
                    mLoadingProgressBar.setVisibility(View.GONE);
                    ToastUtils.show(AliyunPlayerSettingActivity.this, msg);
                }

                @Override
                public void onGetStsSuccess(AliyunSts.StsBean dataBean) {
                    mLoadingProgressBar.setVisibility(View.GONE);
                    if (dataBean != null) {
                        GlobalPlayerConfig.mVid = dataBean.getVideoId();
                        GlobalPlayerConfig.mStsAccessKeyId = dataBean.getAccessKeyId();
                        GlobalPlayerConfig.mStsSecurityToken = dataBean.getSecurityToken();
                        GlobalPlayerConfig.mStsAccessKeySecret = dataBean.getAccessKeySecret();
                        startPlay();
                    }
                }
            });

        } else if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.AUTH && !GlobalPlayerConfig.AUTH_TYPE_CHECKED) {

            getAuthInformation.getVideoPlayAuthInfo(new GetAuthInformation.OnGetPlayAuthInfoListener() {

                @Override
                public void onGetPlayAuthError(String msg) {
                    mLoadingProgressBar.setVisibility(View.GONE);
                    ToastUtils.show(AliyunPlayerSettingActivity.this, msg);
                }

                @Override
                public void onGetPlayAuthSuccess(AliyunPlayAuth.PlayAuthBean dataBean) {
                    mLoadingProgressBar.setVisibility(View.GONE);
                    if (dataBean != null) {
                        GlobalPlayerConfig.mVid = dataBean.getVideoMeta().getVideoId();
                        GlobalPlayerConfig.mPlayAuth = dataBean.getPlayAuth();
                        startPlay();
                    }
                }
            });

        } else if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.MPS && !GlobalPlayerConfig.MPS_TYPE_CHECKED) {

            getAuthInformation.getVideoPlayMpsInfo(new GetAuthInformation.OnGetMpsInfoListener() {
                @Override
                public void onGetMpsError(String msg) {
                    mLoadingProgressBar.setVisibility(View.GONE);
                    ToastUtils.show(AliyunPlayerSettingActivity.this, msg);
                }

                @Override
                public void onGetMpsSuccess(AliyunMps.MpsBean dataBean) {
                    mLoadingProgressBar.setVisibility(View.GONE);
                    if (dataBean != null) {
                        GlobalPlayerConfig.mVid = dataBean.getMediaId();
                        GlobalPlayerConfig.mMpsRegion = dataBean.getRegionId();
                        GlobalPlayerConfig.mMpsAuthInfo = dataBean.getAuthInfo();
                        GlobalPlayerConfig.mMpsHlsUriToken = dataBean.getHlsUriToken();
                        GlobalPlayerConfig.mMpsAccessKeyId = dataBean.getAkInfo().getAccessKeyId();
                        GlobalPlayerConfig.mMpsSecurityToken = dataBean.getAkInfo().getSecurityToken();
                        GlobalPlayerConfig.mMpsAccessKeySecret = dataBean.getAkInfo().getAccessKeySecret();
                        startPlay();
                    }
                }
            });

        } else if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.LIVE_STS && !GlobalPlayerConfig.LIVE_STS_TYPE_CHECKED) {
            getAuthInformation.getVideoPlayStsInfo(new GetAuthInformation.OnGetStsInfoListener() {
                @Override
                public void onGetStsError(String msg) {
                    mLoadingProgressBar.setVisibility(View.GONE);
                    ToastUtils.show(AliyunPlayerSettingActivity.this, msg);
                }

                @Override
                public void onGetStsSuccess(AliyunSts.StsBean dataBean) {
                    mLoadingProgressBar.setVisibility(View.GONE);
                    if (dataBean != null) {
                        GlobalPlayerConfig.mLiveStsAccessKeyId = dataBean.getAccessKeyId();
                        GlobalPlayerConfig.mLiveStsSecurityToken = dataBean.getSecurityToken();
                        GlobalPlayerConfig.mLiveStsAccessKeySecret = dataBean.getAccessKeySecret();
                        startPlay();
                    }
                }
            });
        } else if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.DEFAULT) {
            getAuthInformation.getVideoPlayStsInfo(new GetAuthInformation.OnGetStsInfoListener() {
                @Override
                public void onGetStsError(String msg) {
                    mLoadingProgressBar.setVisibility(View.GONE);
                    ToastUtils.show(AliyunPlayerSettingActivity.this, msg);
                }

                @Override
                public void onGetStsSuccess(AliyunSts.StsBean dataBean) {
                    mLoadingProgressBar.setVisibility(View.GONE);
                    if (dataBean != null) {
                        GlobalPlayerConfig.mVid = "";
                        GlobalPlayerConfig.mStsAccessKeyId = dataBean.getAccessKeyId();
                        GlobalPlayerConfig.mStsSecurityToken = dataBean.getSecurityToken();
                        GlobalPlayerConfig.mStsAccessKeySecret = dataBean.getAccessKeySecret();
                        startPlay();
                    }
                }
            });
        } else {
            mLoadingProgressBar.setVisibility(View.GONE);
            startPlay();
        }
    }


    /**
     * 开启播放界面
     */
    private void startPlay() {
        Intent intent = new Intent(this, AliyunPlayerSkinActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLAY_TYPE_EDIT_ACTIVITY_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedPlayType();
        }
    }
}
