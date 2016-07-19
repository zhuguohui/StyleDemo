package com.zgh.mvpdemo.presenter.news;

import com.zgh.mvpdemo.bean.BannerItem;
import com.zgh.mvpdemo.bean.NewsItem;
import com.zgh.mvpdemo.model.news.DataResultListener;
import com.zgh.mvpdemo.model.news.IListMode;
import com.zgh.mvpdemo.model.news.ListMode;
import com.zgh.mvpdemo.view.news.IListView;

import java.util.List;

/**
 * Created by yuelin on 2016/6/29.
 */
public class ListPresenter implements IListView.OnBannerClickListener, IListView.OnNewsItemClickListener {
    IListView listView;
    IListMode listMode;

    public ListPresenter(IListView listView) {
        this.listView = listView;

        listMode = new ListMode();
        //设置点击事件
        listView.setOnBannerItemClickListener(this);
        listView.setOnListItemClickListener(this);
    }

    public void LoadFirstPage(boolean useCache) {
        //在没有内容的时候才显示进度条，在下拉刷新的时候不需要
        if(!listView.haveContent()) {
            listView.showLoadingFirstPage();
        }
        listMode.LoadData(useCache,listView.getFirstPageUrl(), new DataResultListener() {
            @Override
            public void onSuccess(List<NewsItem> newsData, List<BannerItem> bannerData) {
                listView.hideLoadingFirstPage();
                //如果banner数据不为空才显示banner，否则隐藏
                if (bannerData != null && bannerData.size() > 0) {
                    listView.showBanner(bannerData);
                } else {
                    listView.hideBanner();
                }
                //根据list是否有数据设置显示样式
                if (newsData != null && newsData.size() > 0) {
                    listView.showFirstPageData(newsData);
                    //根据item的数量判断是否有下一页
                    if (listView.haveNextPage(newsData.size())) {
                        listView.showHaveNextPage();
                    } else {
                        listView.showNoMore();
                    }
                } else {
                    listView.showEmpty();
                }

            }

            @Override
            public void onError(String error) {
                listView.hideLoadingFirstPage();
                //在没有内容的时候才显示重试，否则只提示，还是显示原来的缓存内容
                if (!listView.haveContent()) {
                    listView.showRetryFirstPage();
                }
                listView.showToast(error);
            }

        });
    }

    public void LoadNextPage() {
        //显示加载下一页
        listView.showLoadingNextPage();
        listMode.LoadData(false,listView.getNextPageUrl(), new DataResultListener() {
            @Override
            public void onSuccess(List<NewsItem> newsData, List<BannerItem> bannerData) {
                //在加载下一页的时候不需要判断banner的数据

                if (newsData != null && newsData.size() > 0) {
                    listView.showNextPageData(newsData);
                    //如果没有下一页，则显示没有更多了
                    if (!listView.haveNextPage(newsData.size())) {
                        listView.showNoMore();
                    }else{
                        listView.showHaveNextPage();
                    }
                } else {
                    listView.showToast("没有更多了");
                    listView.showNoMore();
                }
                listView.hideLoadingFirstPage();
            }

            @Override
            public void onError(String error) {
                listView.hideLoadingNextPage();
                //在没有内容的时候才显示重试，否则只提示，还是显示原来的缓存内容
                if (!listView.haveContent()) {
                    listView.showRetryFirstPage();
                }else{
                    listView.showRetryNextPage();
                }
                listView.showToast(error);
            }

        });
    }

    @Override
    public void onBannnerClick(BannerItem item) {
        listView.toBannerDetail(item);
    }

    @Override
    public void onNewsItemClick(NewsItem item) {
        listView.toItemDetail(item);
    }
}
