package com.aliyun.svideo.common.baseAdapter.utils;

import android.util.SparseArray;

import com.aliyun.svideo.common.baseAdapter.provider.BaseItemProvider;

public class ProviderDelegate {

    private SparseArray<BaseItemProvider> mItemProviders = new SparseArray<>();

    public void registerProvider(BaseItemProvider provider) {
        if (provider == null) {
            throw new ItemProviderException("ItemProvider can not be null");
        }

        int viewType = provider.viewType();

        if (mItemProviders.get(viewType) == null) {
            mItemProviders.put(viewType, provider);
        }
    }

    public SparseArray<BaseItemProvider> getItemProviders() {
        return mItemProviders;
    }

}
