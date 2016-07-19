package com.zgh.mvpdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zgh.mvpdemo.R;
import com.zgh.mvpdemo.bean.NewsItem;

import java.util.List;

/**
 * Created by Administrator on 2016/7/17 0017.
 */
public class NewsAdapter extends BaseAdapter {
    List<NewsItem> mData;
    Context mContext;
    LayoutInflater inflater;
   public NewsAdapter(Context context,List<NewsItem> data) {
       mData=data;
       mContext=context;
       inflater=LayoutInflater.from(context);
   }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=inflater.inflate(R.layout.item_news,parent,false);
            holder.tv_title= (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        holder.tv_title.setText(mData.get(position).getTitle());
        return convertView;
    }

    public static class ViewHolder{
        TextView tv_title;
    }


}
