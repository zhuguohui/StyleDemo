package com.example.administrator.stylechangelib.style.attribute;

/**
 * Created by Administrator on 2016/7/17 0017.
 */
public interface Attribute {
    int TYPE_BACKGROUD_COLOR=1;
    int TYPE_FONT_COLOR=2;
    int TYPE_DIVER_COLOR=3;
    int getType();
    Object getValue();
}
