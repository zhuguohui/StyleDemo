package com.zgh.stylelib.style;



import com.zgh.stylelib.style.type.Type;

import java.util.HashMap;

/**
 * Created by zhuguohui on 2016/7/17 0017.
 */
public  class Style {
    int mStyleId;
    public Style(int styleId){
     mStyleId=styleId;
    }

    public  int getStypeId(){
        return mStyleId;
    }
    private HashMap<String,Type> typeMap=new HashMap<>();

    public void addType(Type type){
        typeMap.put(type.getName(),type);
    }

    public Type getType(String typeName){
       return typeMap.get(typeName);
    }
}
