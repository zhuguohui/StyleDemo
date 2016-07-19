package com.zgh.mvpdemo.view.news;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;

/**
 * Created by yuelin on 2016/7/19.
 */
public class StyleWebView extends WebView {
    public StyleWebView(Context context) {
        this(context,null);
    }
    Canvas myCanvas;
    public StyleWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Bitmap bitmap=Bitmap.createBitmap(100,100, Bitmap.Config.RGB_565);
         myCanvas=new Canvas(bitmap);
    }
    private boolean haveSetStyle=false;

    public void haveSetStyle(){
        haveSetStyle=true;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("zzz","onResume");
    }

    @Override
    protected void onDraw(Canvas canvas) {
     /*  Canvas needCanve=canvas;
        Log.i("zzz","onDraw haveSetStyle="+haveSetStyle);
        if(!haveSetStyle){
            needCanve=myCanvas;

        }*/

        super.onDraw(canvas);
    }

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);
    }
}
