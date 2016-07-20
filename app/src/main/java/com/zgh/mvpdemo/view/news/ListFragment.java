package com.zgh.mvpdemo.view.news;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.zgh.mvpdemo.DetailActivity;
import com.zgh.mvpdemo.R;
import com.zgh.mvpdemo.WebViewActivity;
import com.zgh.mvpdemo.adapter.NewsAdapter;
import com.zgh.mvpdemo.bean.BannerItem;
import com.zgh.mvpdemo.bean.NewsItem;
import com.zgh.mvpdemo.presenter.news.ListPresenter;
import com.zgh.mvpdemo.retry.LoadingAndRetryManager;
import com.zgh.mvpdemo.retry.OnLoadingAndRetryListener;
import com.zgh.mvpdemo.util.DensityUtil;
import com.zgh.mvpdemo.util.NetworkImageHolderView;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * Created by yuelin on 2016/6/29.
 */
public class ListFragment extends Fragment implements IListView, OnItemClickListener {
    public static final String TAG = "zzz";
    private static final int MSG_LOAD_NEXT_PAGE = 1;
    ListPresenter mPresenter;
    boolean haveInint = false;
    int mPageIndex = 0;
    private View mBaseView;
    private ListView mListView;
    private List<NewsItem> mData = new ArrayList<>();
    private List<BannerItem> mBannerData = new ArrayList<>();
    private NewsAdapter mAdapter;
    LoadingAndRetryManager mLoadingAndRetryManager;
    private BGARefreshLayout mRefreshLayout;
    private boolean IsHaveMorePage = false;
    private TextView tv_bottom_info;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_NEXT_PAGE:
                    mPresenter.LoadNextPage();
                    break;
            }
        }
    };
    private ConvenientBanner mBannerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   Log.i("zzz","onCreate start mPresenter="+mPresenter);
        createView();
        bindData();
        setListener();
        mPresenter = new ListPresenter(this);
        mLoadingAndRetryManager.showLoading();
    //    Log.i("zzz","onCreate end mPresenter="+mPresenter);
        if(getUserVisibleHint()){
            haveInint=true;
            mPresenter.LoadFirstPage(true);
        }
    }


    private void createView() {
        mBaseView = View.inflate(getActivity(), R.layout.fragment_list, null);
        mLoadingAndRetryManager = new LoadingAndRetryManager(mBaseView, new OnLoadingAndRetryListener() {
            @Override
            public void setRetryEvent(View retryView) {
                retryView.findViewById(R.id.id_btn_retry).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.LoadFirstPage(true);
                    }
                });
            }
        });
        mBaseView = mLoadingAndRetryManager.mLoadingAndRetryLayout;
        mListView = (ListView) mBaseView.findViewById(R.id.listview);
        mRefreshLayout = (BGARefreshLayout) mBaseView.findViewById(R.id.rl_listview_refresh);

        //添加footerview
        tv_bottom_info = (TextView) View.inflate(getActivity(), R.layout.view_bottom_retry, null);
        tv_bottom_info.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getActivity(), 45)));
        //要想footerview实现隐藏效果，必须在其外部包裹一层layout,heardview 同理
        LinearLayout footerviewParent = new LinearLayout(getActivity());
        footerviewParent.addView(tv_bottom_info);
        tv_bottom_info.setVisibility(View.GONE);
        mListView.addFooterView(footerviewParent);

        //添加banner
        //添加banner
        mBannerView = new ConvenientBanner(getActivity());
        mBannerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getActivity(), 200)));
        mBannerView.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, mBannerData);
        //设置指示点样式
        mBannerView.setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused});
        //设置指示点对其方式
        mBannerView.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
        mBannerView.setOnItemClickListener(this);
        mBannerView.setVisibility(View.GONE);
        LinearLayout bannerParent = new LinearLayout(getActivity());
        bannerParent.addView(mBannerView);
        bannerParent.setTag("type_no");
        mListView.addHeaderView(bannerParent);
    }

    //绑定数据
    private void bindData() {
        mAdapter = new NewsAdapter(getActivity(), mData);
        mListView.setAdapter(mAdapter);
    }

    private void setListener() {
        mRefreshLayout.setDelegate(new BGARefreshLayout.BGARefreshLayoutDelegate() {
            @Override
            public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
                mPresenter.LoadFirstPage(false);
            }

            @Override
            public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
                if (IsHaveMorePage) {
                    mHandler.sendEmptyMessage(MSG_LOAD_NEXT_PAGE);
                    return true;
                }
                return false;
            }
        });
        BGANormalRefreshViewHolder normalRefreshViewHolder = new BGANormalRefreshViewHolder(getActivity(), true);
        mRefreshLayout.setRefreshViewHolder(normalRefreshViewHolder);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewParent parent = mBaseView.getParent();
        if (parent != null && parent instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) parent;
            group.removeView(mBaseView);
        }
        return mBaseView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
    //    Log.i("zzz","setUserVisibleHint mPresenter="+mPresenter);
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !haveInint&&mPresenter!=null) {
            haveInint = true;
            mPresenter.LoadFirstPage(true);
        }
    }

    @Override
    public void showLoadingFirstPage() {
        mLoadingAndRetryManager.showLoading();
    }

    @Override
    public void showLoadingNextPage() {

    }

    @Override
    public void hideLoadingFirstPage() {
        mLoadingAndRetryManager.showContent();
        mRefreshLayout.endRefreshing();
    }

    @Override
    public void hideLoadingNextPage() {
        mRefreshLayout.endLoadingMore();
    }

    @Override
    public void hideBanner() {
        mBannerView.setVisibility(View.GONE);
    }

    @Override
    public void showRetryFirstPage() {
        mLoadingAndRetryManager.showRetry();
    }

    @Override
    public void showEmpty() {
        mLoadingAndRetryManager.showEmpty();
    }

    @Override
    public void showToast(String info) {
        Toast.makeText(getActivity(), info, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toItemDetail(NewsItem item) {

       startActivity(new Intent(getActivity(),WebViewActivity.class));
    }

    @Override
    public void toBannerDetail(BannerItem item) {
        if (item != null) {
            showToast(item.getTitle());
        }
    }

    @Override
    public void showFirstPageData(List<NewsItem> listData) {
        mPageIndex = 0;
        mData.clear();
        mData.addAll(listData);
        mAdapter.notifyDataSetChanged();
        mRefreshLayout.endRefreshing();
    }

    @Override
    public void showNextPageData(List<NewsItem> listData) {
        //只有在加载成功的时候才更新当前的页数
        mPageIndex++;
        mData.addAll(listData);
        mAdapter.notifyDataSetChanged();
        mRefreshLayout.endLoadingMore();
    }

    @Override
    public void showBanner(List<BannerItem> bannerData) {
        mBannerData.clear();
        mBannerData.addAll(bannerData);
        mBannerView.notifyDataSetChanged();
        mBannerView.setVisibility(View.VISIBLE);
        mBannerView.startTurning(2000);
    }

    private OnBannerClickListener mBannerClickListenr;

    @Override
    public void setOnBannerItemClickListener(OnBannerClickListener listener) {
        mBannerClickListenr = listener;
    }

    @Override
    public void setOnListItemClickListener(final OnNewsItemClickListener listener) {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    NewsItem item = (NewsItem) parent.getAdapter().getItem(position);
                    listener.onNewsItemClick(item);
                }
            }
        });
    }

    @Override
    public boolean haveNextPage(int dataSize) {
        return dataSize == 10;
    }

    @Override
    public void showHaveNextPage() {
        tv_bottom_info.setVisibility(View.GONE);
        IsHaveMorePage = true;

    }

    @Override
    public void showNoMore() {
        IsHaveMorePage = false;
        tv_bottom_info.setVisibility(View.VISIBLE);
        tv_bottom_info.setText("没有更多了");
        tv_bottom_info.setOnClickListener(null);
    }

    @Override
    public void showRetryNextPage() {
        tv_bottom_info.setVisibility(View.VISIBLE);
        tv_bottom_info.setText("加载失败，点击重试");
        tv_bottom_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐藏重试，显示进度条
                tv_bottom_info.setVisibility(View.GONE);
                mRefreshLayout.beginLoadingMore();
            }
        });
    }

    @Override
    public boolean haveContent() {
        return mData.size() > 0;
    }

    @Override
    public String getFirstPageUrl() {
        return "0.html";
    }

    @Override
    public String getNextPageUrl() {
        int tempindex = mPageIndex + 1;
        String result = "other.html";
        switch (tempindex) {
            case 0:
                result = "0.html";
                break;
            case 1:
                result = "1.html";
                break;
        }
        return result;
    }

    @Override
    public void onItemClick(int position) {
        if(mBannerClickListenr!=null){
            if(position>=0&&position<mBannerData.size()){
                mBannerClickListenr.onBannnerClick(mBannerData.get(position));
            }
        }
    }


}
