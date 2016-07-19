package com.zgh.mvpdemo.model.news;

import com.zgh.mvpdemo.bean.BannerItem;
import com.zgh.mvpdemo.bean.NewsItem;

import java.util.List;

/**
 * Created by yuelin on 2016/6/29.
 */
public interface DataResultListener {
    void onSuccess(List<NewsItem> newsData,List<BannerItem> bannerData);
    void onError(String error);
}
