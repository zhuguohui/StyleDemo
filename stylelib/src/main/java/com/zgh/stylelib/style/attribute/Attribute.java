package com.zgh.stylelib.style.attribute;

/**
 * Created by Administrator on 2016/7/17 0017.
 */
public interface Attribute {
    //背景色
    int TYPE_BACKGROUND_COLOR =1;
    //字体颜色
    int TYPE_FONT_COLOR=2;
    //分割线颜色
    int TYPE_DIVER_COLOR=3;
    //url颜色
    int TYPE_URL_COLOR=4;
    int getType();
    String getValue();
}
