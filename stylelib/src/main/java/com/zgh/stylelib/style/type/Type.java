package com.zgh.stylelib.style.type;


import com.zgh.stylelib.style.attribute.Attribute;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2016/7/17 0017.
 */
public class Type {
    Map<Integer, Attribute> mAttributes = new HashMap<>();
    Type mParentType;

    public void addAttribute(Attribute attribute) {
        mAttributes.put(attribute.getType(), attribute);
    }

    public Attribute getAttribute(int type) {
        Attribute attribute = mAttributes.get(type);
        if (attribute == null) {
            attribute = mParentType.getAttribute(type);
        }
        return attribute;
    }

    private String mName;

    public Type(String name, Type ParentType) {
        mName = name;
        mParentType = ParentType;
    }

    public String getName() {
        return mName;
    }
}
