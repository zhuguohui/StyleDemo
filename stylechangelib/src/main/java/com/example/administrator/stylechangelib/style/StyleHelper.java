package com.example.administrator.stylechangelib.style;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.zgh.mvpdemo.style.attribute.Attribute;
import com.zgh.mvpdemo.style.type.Type;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/17 0017.
 */
public class StyleHelper {
    public static Map<String,Integer> attributeTypeMap=new HashMap<>();
    static  {
        attributeTypeMap.put("background_color",Attribute.TYPE_BACKGROUD_COLOR);
        attributeTypeMap.put("font_color",Attribute.TYPE_FONT_COLOR);
        attributeTypeMap.put("diver_color",Attribute.TYPE_DIVER_COLOR);
    }

    public static Style getStyle(Context context,String name){
        int id = context.getResources().getIdentifier(name, "raw", context.getPackageName());
        if(id!=0){
            Resources resources = context.getResources();
            InputStream in = resources.openRawResource(id);
            byte[] buffer=new byte[1024];
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            int len=0;
            try {
                while((len=in.read(buffer))!=-1){
                    baos.write(buffer,0,len);
                }
                in.close();
                byte[] bytes = baos.toByteArray();
                baos.close();
                String jsonStr=new String(bytes);
                //解析json
              return   AnalzyStyle(jsonStr);
            } catch (IOException e) {
                Log.i("zzz", "读取style失败");
            }
        }else{
            Log.i("zzz","沒有相应style");
        }
        return null;
    }

    public static Style AnalzyStyle(String jsonStr){
        if(TextUtils.isEmpty(jsonStr)){
            return null;
        }
        try {
            JSONObject jsonStyle=new JSONObject(jsonStr);
            int style_id = jsonStyle.getInt("style_id");
            Style style=new Style(style_id);
            JSONArray jsonTypeArray=jsonStyle.getJSONArray("types");
            int length = jsonTypeArray.length();
            for(int i=0;i<length;i++){
                JSONObject jsonType = jsonTypeArray.getJSONObject(i);
                String type_name= jsonType.getString("type_name");
                Type type=new Type(type_name);
                JSONArray jsonAttributeArray=jsonType.getJSONArray("attributes");
                for(int j=0;j<jsonAttributeArray.length();j++){
                   JSONObject json_attribute=  jsonAttributeArray.getJSONObject(j);
                   String attributeType=json_attribute.getString("attributeType");
                    final String attributeValue=json_attribute.getString("attributeValue");
                    final int att_type=attributeTypeMap.get(attributeType);
                    Attribute attribut=new Attribute() {
                        @Override
                        public int getType() {
                            return att_type;
                        }
                        @Override
                        public Integer getValue() {
                            return Color.parseColor(attributeValue);
                        }
                    };
                    type.addAttribute(attribut);
                }
                style.addType(type);
            }
            return style;
        } catch (JSONException e) {
            Log.i("zzz", "解析style失败");
        }

        return null;

    }
}
