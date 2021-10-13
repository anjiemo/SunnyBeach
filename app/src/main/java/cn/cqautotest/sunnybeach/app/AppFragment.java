package cn.cqautotest.sunnybeach.app;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.KeyboardUtils;
import com.hjq.base.BaseFragment;
import com.hjq.http.listener.OnHttpListener;

import cn.cqautotest.sunnybeach.action.Init;
import cn.cqautotest.sunnybeach.action.ToastAction;
import cn.cqautotest.sunnybeach.http.model.HttpData;
import okhttp3.Call;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 业务 Fragment 基类
 */
public abstract class AppFragment<A extends AppActivity> extends BaseFragment<A>
        implements Init, ToastAction, OnHttpListener<Object> {

    @CallSuper
    @Override
    protected void onFragmentResume(boolean first) {
        super.onFragmentResume(first);
    }

    private ViewBinding mViewBinding = null;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initEvent();
        initObserver();
    }

    @Override
    protected void initView() {
        mViewBinding = onBindingView();
    }

    protected ViewBinding onBindingView() {
        return null;
    }

    @Override
    public void initEvent() {

    }

    @Override
    public void initObserver() {

    }

    /**
     * 当前加载对话框是否在显示中
     */
    public boolean isShowDialog() {
        A activity = getAttachActivity();
        if (activity != null) {
            return activity.isShowDialog();
        }

        return false;
    }

    /**
     * 显示加载对话框
     */
    public void showDialog() {
        A activity = getAttachActivity();
        if (activity != null) {
            activity.showDialog();
        }
    }

    /**
     * 隐藏加载对话框
     */
    public void hideDialog() {
        A activity = getAttachActivity();
        if (activity != null) {
            activity.hideDialog();
        }
    }

    /**
     * {@link OnHttpListener}
     */

    @Override
    public void onStart(Call call) {
        showDialog();
    }

    @Override
    public void onSucceed(Object result) {
        if (result instanceof HttpData) {
            toast(((HttpData<?>) result).getMessage());
        }
    }

    @Override
    public void onFail(Exception e) {
        toast(e.getMessage());
    }

    @Override
    public void onEnd(Call call) {
        hideDialog();
    }

    @Override
    public void hideKeyboard(View view) {
        KeyboardUtils.hideSoftInput(view);
    }
}