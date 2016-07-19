package com.zgh.mvpdemo.model.news;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.zgh.mvpdemo.app.MyApp;
import com.zgh.mvpdemo.bean.BannerItem;
import com.zgh.mvpdemo.bean.NewsItem;
import com.zgh.mvpdemo.util.NetStateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by yuelin on 2016/6/29.
 */
public class ListMode implements IListMode {
    private static final int MSG_LAOD_DATA = 1;
    private static List<NewsItem> pageOne = new ArrayList<>();
    private static List<NewsItem> pageOneCache = new ArrayList<>();
    private static List<BannerItem> bannerData = new ArrayList<>();
    private static List<NewsItem> pageTwo = new ArrayList<>();
    private static String[] imageurls = new String[]{"http://dl.bizhi.sogou.com/images/2012/09/30/44928.jpg", "http://pic1.desk.chinaz.com/file/10.03.10/5/rrgaos56.jpg", "http://www.deskcar.com/desktop/star/world/20081017165318/27.jpg"};
    private Random mRandom=new Random();
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LAOD_DATA:
                    laodDataFinish();
                    break;
            }
        }
    };

    //初始化数据
    static {
        for (int i = 1; i <= 10; i++) {
            NewsItem item = new NewsItem("新闻页1 缓存-" + i, "www.baidu.com");
            pageOneCache.add(item);
        }

        for (int i = 1; i <= 10; i++) {
            NewsItem item = new NewsItem("新闻页1-" + i, "www.baidu.com");
            pageOne.add(item);
        }

        for (int i = 1; i <= 9; i++) {
            NewsItem item = new NewsItem("新闻页2-" + i, "www.baidu.com");
            pageTwo.add(item);
        }

        for (int i = 0; i < 3; i++) {
            BannerItem bannerItem = new BannerItem("banner-" + i, "www.baidu.com", imageurls[i]);
            bannerData.add(bannerItem);
        }


    }

    List<NewsItem> items = null;
    List<NewsItem> itemCache = null;
    List<BannerItem> bannerItems = null;
    DataResultListener listener = null;
    boolean useCache = false;

    @Override
    public void LoadData(boolean useCache, String url, DataResultListener listener) {
        items = null;
        bannerItems = null;
        itemCache = null;
        this.useCache = useCache;
        this.listener = listener;
        if (url.equals("0.html")) {
            items = pageOne;
            //随机显示banner
            boolean show = mRandom.nextBoolean();
            bannerItems = show?bannerData:null;
            if (useCache) {
                itemCache = pageOneCache;
            }

        } else if (url.equals("1.html")) {
            items = pageTwo;
        }
        //如果使用缓存则优先加载缓存
        if (useCache) {
            listener.onSuccess(itemCache, bannerItems);
        }

        if (!NetStateUtil.isNetworkAvailable(MyApp.app)) {
            listener.onError("网络不给力");
        } else {
            mHandler.sendEmptyMessageDelayed(MSG_LAOD_DATA, 2000);
        }

    }

    private void laodDataFinish() {
        if (listener != null) {
            //在不使用缓存的时候直接更新
            if (!useCache) {
                listener.onSuccess(items, bannerItems);
            } else {
                //如果使用缓存，则将最新的数据与缓存数据比较，如果不同，则更新缓存，更新界面,如果相同则不更新界面
                if(!itemCache.equals(items)){
                    //更新缓存
                    listener.onSuccess(items,bannerItems);
                }
            }
        }
    }
}
