package com.aliyun.svideo.common.baseAdapter;


import com.aliyun.svideo.common.R;

/**
 * 配合BaseQuickAdapter的加载更多布局，如需修改请自定义
 */
public final class SimpleLoadMoreView extends LoadMoreView {

    @Override
    public int getLayoutId() {
        return R.layout.alivc_quick_view_load_more;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.load_more_load_end_view;
    }
}
