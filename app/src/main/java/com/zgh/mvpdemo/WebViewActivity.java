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

    static boolean night = false;
    String css = "javascript: (function() {/n" + " /n" + " css = document.createElement('link');/n" + " css.id = 'xxx_browser_2014';/n" + " css.rel = 'stylesheet';/n" + " css.href = 'data:text/css,html,body,applet,object,h1,h2,h3,h4,h5,h6,blockquote,pre,abbr,acronym,address,big,cite,code,del,dfn,em,font,img,ins,kbd,q,p,s,samp,small,strike,strong,sub,sup,tt,var,b,u,i,center,dl,dt,dd,ol,ul,li,fieldset,form,label,legend,table,caption,tbody,tfoot,thead,th,td{background:rgba(0,0,0,0) !important;color:#fff !important;border-color:#A0A0A0 !important;}div,input,button,textarea,select,option,optgroup{background-color:#000 !important;color:#fff !important;border-color:#A0A0A0 !important;}a,a *{color:#ffffff !important; text-decoration:none !important;font-weight:bold !important;background-color:rgba(0,0,0,0) !important;}a:active,a:hover,a:active *,a:hover *{color:#1F72D0 !important;background-color:rgba(0,0,0,0) !important;}p,span{font color:#FF0000 !important;color:#ffffff !important;background-color:rgba(0,0,0,0) !important;}html{-webkit-filter: contrast(50%);}';/n" + " document.getElementsByTagName('head')[0].appendChild(css);/n" + " /n" + "})();";

    @Override
    public void childOnCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_web_view);
        findViewById(R.id.btn_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change();
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
