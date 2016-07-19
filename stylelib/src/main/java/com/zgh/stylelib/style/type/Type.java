package com.zgh.stylelib.style.type;



import com.zgh.stylelib.style.attribute.Attribute;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2016/7/17 0017.
 */
public  class Type {
    Map<Integer,Attribute> mAttributes=new HashMap<>();

    public void addAttribute(Attribute attribute){
        mAttributes.put(attribute.getType(), attribute);
    }

    public Attribute getAttribute(int type){
       return mAttributes.get(type);
    }

    private String mName;
    public Type(String name){
        mName=name;
    }
    public String getName(){
        return  mName;
    }
}
