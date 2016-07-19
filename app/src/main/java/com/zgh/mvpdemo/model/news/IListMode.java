package com.zgh.mvpdemo.model.news;

/**
 * Created by zhuguohui on 2016/6/29.
 */
public interface IListMode {
    /**
     *
     * @param useCache 是否使用缓存
     * @param url 数据url
     * @param listener 回调接口
     */
    void LoadData(boolean useCache,String url,DataResultListener listener);
}
