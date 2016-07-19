package com.zgh.mvpdemo.view.news;

import android.content.DialogInterface;

import com.zgh.mvpdemo.bean.BannerItem;
import com.zgh.mvpdemo.bean.NewsItem;

import java.util.List;

/**
 * Created by yuelin on 2016/6/29.
 */
public interface IListView {
    /*******加载首页相关*******/

    //显示加载首页的加载效果
    void showLoadingFirstPage();

    //隐藏加载首页动画
    void hideLoadingFirstPage();

    //首页加载失败的时候调用
    void showRetryFirstPage();

    //首页为空的时候调用
    void showEmpty();

    //获取到首页数据的时候调用
    void showFirstPageData(List<NewsItem> listData);


    /***********加载下一页相关*************/

    //显示加载下一页
    void showLoadingNextPage();

    //隐藏加载下一页
    void hideLoadingNextPage();

    //获取到下一页数据是调用
    void showNextPageData(List<NewsItem> listData);

    //显示还有下一页
    void showHaveNextPage();

    //显示重试加载下一页
    void showRetryNextPage();

    //没有更多了
    void showNoMore();

    /**************Banner相关*********************/

    //隐藏banner在没有banner数据的时候调用
    void hideBanner();

    //显示banner数据
    void showBanner(List<BannerItem> bannerData);


    /*****************获取数据***************************/

    //判断是否还有下一页
    boolean haveNextPage(int dataSize);

    //是否已经有显示的内容了，用于判断在没有网络的时候是否显示重试界面，如果有内容则显示内容，否则显示重试。
    boolean haveContent();

    //获取首页的url地址
    String getFirstPageUrl();

    //下一页的url地址
    String getNextPageUrl();

    /**************点击事件****************************/

    interface OnBannerClickListener {
        void onBannnerClick(BannerItem item);
    }

    interface OnNewsItemClickListener {
        void onNewsItemClick(NewsItem item);
    }

    void setOnBannerItemClickListener(OnBannerClickListener listener);

    void setOnListItemClickListener(OnNewsItemClickListener listener);

    void toItemDetail(NewsItem item);

    void toBannerDetail(BannerItem item);


    /****************通知*******************************/

    void showToast(String info);

}
