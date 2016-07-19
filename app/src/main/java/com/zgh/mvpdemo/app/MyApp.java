package com.zgh.mvpdemo.app;

import android.app.Application;
import android.util.Log;

import com.zgh.mvpdemo.R;
import com.zgh.mvpdemo.retry.LoadingAndRetryManager;
import com.zgh.stylelib.style.StyleHelper;

import me.drakeet.library.CrashWoodpecker;


/**
 * Created by yuelin on 2016/6/29.
 */
public class MyApp extends Application {
    public static MyApp app;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashWoodpecker.init(this);
        StyleHelper.init(this, "baidu", "day");
        app = this;
        //设置多个页面共享加载和重试页面
        LoadingAndRetryManager.BASE_RETRY_LAYOUT_ID = R.layout.base_retry;
        LoadingAndRetryManager.BASE_LOADING_LAYOUT_ID = R.layout.base_loading;
        LoadingAndRetryManager.BASE_EMPTY_LAYOUT_ID = R.layout.base_empty;
    }
}
