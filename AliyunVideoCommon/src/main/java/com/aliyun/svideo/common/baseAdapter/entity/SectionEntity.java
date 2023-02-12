package com.aliyun.svideo.common.baseAdapter.entity;

import java.io.Serializable;

/**
 * 带section布局的列表，需要继承该类，并使用BaseSectionQuickAdapter的二次封装适配器
 * 具体例子可看longvideo
 */
public abstract class SectionEntity<T> implements Serializable {
    public boolean isHeader;
    public T t;
    public String header;

    public SectionEntity(boolean isHeader, String header) {
        this.isHeader = isHeader;
        this.header = header;
        this.t = null;
    }

    public SectionEntity(T t) {
        this.isHeader = false;
        this.header = null;
        this.t = t;
    }

    public void setT(T t) {
        this.t = t;
    }
}
