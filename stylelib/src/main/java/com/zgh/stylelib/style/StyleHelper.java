package com.zgh.stylelib.style;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.zgh.stylelib.R;
import com.zgh.stylelib.style.attribute.Attribute;
import com.zgh.stylelib.style.type.Type;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by zhuguohui on 2016/7/17 0017.
 */
public class StyleHelper {
    public static Map<String, Integer> attributeTypeMap = new HashMap<>();
    private static Context mContext;
    private static Map<Integer, Style> styleMap = new HashMap<>();


    private static int currentStyle = 0;
    private static Stack<Activity> mActivityStack = new Stack<>();

    static {
        attributeTypeMap.put("background_color", Attribute.TYPE_BACKGROUD_COLOR);
        attributeTypeMap.put("font_color", Attribute.TYPE_FONT_COLOR);
        attributeTypeMap.put("diver_color", Attribute.TYPE_DIVER_COLOR);
        attributeTypeMap.put("url_color", Attribute.TYPE_URL_COLOR);
    }


    public static void init(Context context, String... stylename) {
        mContext = context;
        currentStyle = getStyle();
        for (String name : stylename) {
            Style style = getStyle(context, name);
            styleMap.put(style.getStypeId(), style);
        }

    }

    public static void initActivity(Activity activity) {
        mActivityStack.push(activity);
        View view = activity.findViewById(android.R.id.content);
        setColor(view, true);
        setOnHierarchyChangeListener(view);
    }

    public static void destroyActivity() {
        mActivityStack.pop();
    }


    private static void setOnHierarchyChangeListener(View view) {
        if (view == null) {
            return;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            viewGroup.setOnHierarchyChangeListener(mOnHierarchyChangeListener);
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                setOnHierarchyChangeListener(viewGroup.getChildAt(i));
            }
        }

    }

    private static ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener = new ViewGroup.OnHierarchyChangeListener() {
        @Override
        public void onChildViewAdded(View parent, View child) {
            setColor(child, true);
            setOnHierarchyChangeListener(child);
        }

        @Override
        public void onChildViewRemoved(View parent, View child) {

        }
    };


    public static Style getStyle(Context context, String name) {
        int id = context.getResources().getIdentifier(name, "raw", context.getPackageName());
        if (id != 0) {
            Resources resources = context.getResources();
            InputStream in = resources.openRawResource(id);
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len = 0;
            try {
                while ((len = in.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                in.close();
                byte[] bytes = baos.toByteArray();
                baos.close();
                String jsonStr = new String(bytes);
                //解析json
                return AnalzyStyle(jsonStr);
            } catch (IOException e) {
                Log.i("zzz", "读取style失败");
            }
        } else {
            Log.i("zzz", "沒有相应style");
        }
        return null;
    }

    public static Style AnalzyStyle(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        try {
            JSONObject jsonStyle = new JSONObject(jsonStr);
            int style_id = jsonStyle.getInt("style_id");
            Style style = new Style(style_id);
            JSONArray jsonTypeArray = jsonStyle.getJSONArray("types");
            int length = jsonTypeArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonType = jsonTypeArray.getJSONObject(i);
                String type_name = jsonType.getString("type_name");
                Type type = new Type(type_name);
                JSONArray jsonAttributeArray = jsonType.getJSONArray("attributes");
                for (int j = 0; j < jsonAttributeArray.length(); j++) {
                    JSONObject json_attribute = jsonAttributeArray.getJSONObject(j);
                    String attributeType = json_attribute.getString("attributeType");
                    final String attributeValue = json_attribute.getString("attributeValue");
                    final int att_type = attributeTypeMap.get(attributeType);
                    Attribute attribut = new Attribute() {
                        @Override
                        public int getType() {
                            return att_type;
                        }

                        @Override
                        public String getValue() {
                            return attributeValue;
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

    //样式切换逻辑

    public static void changeMode() {
        currentStyle -= 1;
        if (currentStyle < 0) {
            currentStyle = 1;
        }
        saveStyle(currentStyle);
        boolean createAnimator = false;

        for (int i = mActivityStack.size() - 1; i >= 0; i--) {
            Activity activity = mActivityStack.get(i);
            View view = activity.findViewById(android.R.id.content);
            Log.i("zzz", "activity=" + activity);
            if (!createAnimator) {
                createAnimator = true;
                createAnimator(activity);
            }
            setColor(view, true);
        }
    }

    private static void createAnimator(Activity activity) {
        if (activity == null) {
            return;
        }
        View decorView = activity.getWindow().getDecorView();
        final ImageView imageView = new ImageView(activity);
        int width = activity.getResources().getDisplayMetrics().widthPixels;
        int height = activity.getResources().getDisplayMetrics().heightPixels;
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        decorView.draw(new Canvas(b));
        imageView.setImageBitmap(b);
        final ViewGroup group = (ViewGroup) decorView;
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        group.addView(imageView);
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alph = (float) animation.getAnimatedValue();
                imageView.setAlpha(alph);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                group.removeView(imageView);
            }
        });
        animator.setDuration(1000);
        animator.start();
    }

    private static void setColor(View view, boolean IsRecursion) {
        if (view == null) {
            return;
        }
        Object tag = view.getTag(R.id.tag_style);
        if (tag == null && currentStyle == 0) {
            view.setTag(R.id.tag_style, currentStyle);
            return;
        }
        if (tag != null) {
            int viewStyle = (int) tag;
            if (viewStyle == currentStyle) {
                return;
            }
        }
        view.setTag(R.id.tag_style, currentStyle);
        Object viewTag = view.getTag();
        String typeName = "";
        if (viewTag != null && viewTag instanceof String) {
            typeName = (String) viewTag;
        }
        boolean needInherit = typeName.startsWith(":");
        if (needInherit) {
            String inheritName = typeName.substring(1);
            if (view instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) view;
                int count = group.getChildCount();
                for (int i = 0; i < count; i++) {
                    View childView = group.getChildAt(i);
                    childView.setTag(inheritName);
                    setColor(childView, IsRecursion);
                }
                return;
            }
        }

        if (view instanceof WebView) {
            setupWebView((WebView) view);
            return;
        }

        Drawable background = view.getBackground();
        if (background instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) background;
            view.setBackgroundColor(getFSColor(colorDrawable.getColor(), currentStyle, typeName, Attribute.TYPE_BACKGROUD_COLOR));
        }
        if (view instanceof ViewGroup) {
            if (view instanceof ListView) {
                ListView lv = (ListView) view;

                Drawable divider = lv.getDivider();
                if (divider instanceof ColorDrawable) {
                    ColorDrawable colorDrawable = (ColorDrawable) divider;
                    int fs = getFSColor(colorDrawable.getColor(), currentStyle, typeName, Attribute.TYPE_DIVER_COLOR);
                    int height = lv.getDividerHeight();
                    lv.setDivider(new ColorDrawable(fs));
                    lv.setDividerHeight(height);
                }
            }

            if (IsRecursion) {
                ViewGroup group = (ViewGroup) view;
                int count = group.getChildCount();
                for (int i = 0; i < count; i++) {
                    setColor(group.getChildAt(i), true);
                }
                if (view instanceof AbsListView) {
                    AbsListView absListView = (AbsListView) view;
                    try {
                        Field field = AbsListView.class.getDeclaredField("mRecycler");
                        field.setAccessible(true);
                        Object o = field.get(absListView);
                        Class<?> cls = Class.forName("android.widget.AbsListView$RecycleBin");
                        Field scrapViews = cls.getDeclaredField("mScrapViews");
                        scrapViews.setAccessible(true);
                        ArrayList<View>[] views = (ArrayList<View>[]) scrapViews.get(o);
                        for (int i = 0; i < views.length; i++) {
                            ArrayList<View> vs = views[i];
                            for (View v : vs) {
                                setColor(v, true);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        } else {
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                tv.setTextColor(getFSColor(tv.getCurrentTextColor(), currentStyle, typeName, Attribute.TYPE_FONT_COLOR));
            }

        }

    }

    public static void setupWebView(WebView webView) {
        if (webView != null) {
            Object viewTag = webView.getTag();
            String type_name = "";
            if (viewTag != null && viewTag instanceof String) {
                type_name = (String) viewTag;
            }
            if (TextUtils.isEmpty(type_name) || styleMap.get(currentStyle).getType(type_name) == null) {
                return;
            }
            if (styleMap != null) {
                String backgroudColor = styleMap.get(currentStyle).getType(type_name).getAttribute(Attribute.TYPE_BACKGROUD_COLOR).getValue();
                String fontColor = styleMap.get(currentStyle).getType(type_name).getAttribute(Attribute.TYPE_FONT_COLOR).getValue();
                String urlColor = styleMap.get(currentStyle).getType(type_name).getAttribute(Attribute.TYPE_URL_COLOR).getValue();
                String js = String.format(jsStyle, backgroudColor, fontColor, urlColor, backgroudColor);
                webView.loadUrl(js);
            }

        }
    }

    private static String jsStyle = "javascript:(function(){\n" +
            "\t\t   document.body.style.backgroundColor=\"%s\";\n" +
            "\t\t    document.body.style.color=\"%s\";\n" +
            "\t\t\tvar as = document.getElementsByTagName(\"a\");\n" +
            "\t\tfor(var i=0;i<as.length;i++){\n" +
            "\t\t\tas[i].style.color = \"%s\";\n" +
            "\t\t}\n" +
            "\t\tvar divs = document.getElementsByTagName(\"div\");\n" +
            "\t\tfor(var i=0;i<divs.length;i++){\n" +
            "\t\t\tdivs[i].style.backgroundColor = \"%s\";\n" +
            "\t\t}\n" +
            "\t\t})()";

    private static int getFSColor(int color, int style, String type_name, int attribute_type) {
        if (TextUtils.isEmpty(type_name) || styleMap.get(style).getType(type_name) == null) {
            return color;
        }
        if (styleMap != null) {
            int integer = Color.parseColor(styleMap.get(style).getType(type_name).getAttribute(attribute_type).getValue());
            return integer;
        }
        int a, r, g, b;
        a = Color.alpha(color);
        r = 255 - Color.red(color);
        g = 255 - Color.green(color);
        b = 255 - Color.blue(color);
        return Color.argb(a, r, g, b);
    }


    private static void saveStyle(int style) {
        SharedPreferences mySharedPreferences = mContext.getSharedPreferences("mode",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt("style", style);
        editor.commit();
    }

    private static int getStyle() {
        SharedPreferences mySharedPreferences = mContext.getSharedPreferences("mode",
                Activity.MODE_PRIVATE);
        return mySharedPreferences.getInt("style", 0);
    }
}
