package com.zgh.mvpdemo;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zgh.mvpdemo.base.BaseActivity;
import com.zgh.mvpdemo.view.news.StyleWebView;
import com.zgh.stylelib.style.StyleHelper;

import java.lang.reflect.Method;

public class WebViewActivity extends BaseActivity {
    WebView webview;
    @Override
    public void childOnCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_web_view);
        findViewById(R.id.btn_baidu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StyleHelper.changeStyle(0,1);
            }
        });
        findViewById(R.id.btn_wangyi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StyleHelper.changeStyle(0,2);
            }
        });
        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

        });
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                StyleHelper.setupWebView(view);
            }
        });
        webview.setBackgroundColor(0);
        webview.loadUrl("http://wap.163.com/index.jsp");
    }


    String strBackgroudColor = "#252F3B";
    String strFontColor = "#8E9EB5";
    String strUrlColor = "#2C4D7D";

    String strDayBackgroudColor = "#FFFFFF";
    String strDayFontColor = "#000000";
    String strDayUrlColor = "#FF0000";

    private void change() {
        changeMode();
    }


}
