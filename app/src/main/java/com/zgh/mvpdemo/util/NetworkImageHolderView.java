package com.zgh.mvpdemo.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.Glide;

import com.zgh.mvpdemo.R;
import com.zgh.mvpdemo.bean.BannerItem;

/**
 * Created by Sai on 15/8/4.
 * 网络图片加载例子
 */
public class NetworkImageHolderView implements Holder<BannerItem> {
    private ImageView imageView;
    private TextView tv_title;
    @Override
    public View createView(Context context) {
        //你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
        View view=View.inflate(context, R.layout.banner_layout,null);
        imageView = (ImageView) view.findViewById(R.id.img);
        tv_title= (TextView) view.findViewById(R.id.tv_title);
        return view;
    }

    @Override
    public void UpdateUI(Context context, int position, BannerItem data) {
        tv_title.setText(data.getTitle());
        if(context!=null&&context instanceof Activity){
            Activity activity= (Activity) context;
            if(!activity.isFinishing()){
                Glide.with(context).load(data.getImageUrl()).placeholder(R.drawable.default_pic).into(imageView);
            }
        }

    }


}
