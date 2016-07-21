package com.zgh.stylelib.style;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.zgh.stylelib.style.util.JSMin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by zhuguohui on 2016/7/17 0017.
 */
public class StyleHelper {
    private static int DEFAULT_STYLE_ID = 0;
    public static Map<String, Integer> attributeTypeMap = new HashMap<>();
    private static Context mContext;
    private static Map<Integer, Style> styleMap = new HashMap<>();


    private static int sCurrentStyleId = 0;
    private static Stack<WeakReference<Activity>> mActivityStack = new Stack<>();
    //默认的typeName
    private static String sDefalutTypeName = "";
    //标记是否设置了默认的type
    private static boolean sHaveSetDefaluType = false;
    //标识为默认type
    private static final String KEY_DEFAULT_TYPE = "default_type";
    //标识为默认style
    private static final String KEY_DEFAULT_STYLE = "default_style";
    //被此type标记的view将不会换肤
    private static final String DISABLE_TYPE_NAME = "type_no";

    static {
        attributeTypeMap.put("background_color", Attribute.TYPE_BACKGROUND_COLOR);
        attributeTypeMap.put("font_color", Attribute.TYPE_FONT_COLOR);
        attributeTypeMap.put("diver_color", Attribute.TYPE_DIVER_COLOR);
        attributeTypeMap.put("url_color", Attribute.TYPE_URL_COLOR);
    }


    private static void setDefalutType(String typeName) {
        sDefalutTypeName = typeName;
        if (!TextUtils.isEmpty(typeName)) {
            sHaveSetDefaluType = true;
        } else {
            sHaveSetDefaluType = false;
        }
    }


    public static void init(Context context, String... stylename) {
        mContext = context;
        for (String name : stylename) {
            Style style = getStyle(context, name);
            styleMap.put(style.getStypeId(), style);
        }
        sCurrentStyleId = getStyle();
    }

    public static void initActivity(Activity activity) {
        mActivityStack.push(new WeakReference<Activity>(activity));
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
            //如果parent被标记为type_no,则child也会被标记
            if (DISABLE_TYPE_NAME.equals(checkTag(child))) {
                child.setTag(DISABLE_TYPE_NAME);
            }

            setColor(child, true);
            setOnHierarchyChangeListener(child);
        }

        @Override
        public void onChildViewRemoved(View parent, View child) {

        }
    };

    private static String checkTag(View child) {
        String tag = "";
        if (child == null) {
            return tag;
        }
        try {
            View parent = null;
            if (child.getParent() instanceof View) {
                parent = (View) child.getParent();
            }
            String parentTag = (String) parent.getTag();
            while (parent != null && !DISABLE_TYPE_NAME.equals(parentTag)) {
                child = parent;
                parent = null;
                if (child.getParent() instanceof View) {
                    parent = (View) child.getParent();
                    parentTag = (String) parent.getTag();
                }
            }
            return parentTag;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tag;
    }


    public static Style getStyle(Context context, String name) {
        int id = context.getResources().getIdentifier(name, "raw", context.getPackageName());
        if (id != 0) {
            Resources resources = context.getResources();
            InputStream in = resources.openRawResource(id);
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try {
                //过滤json文件中的注释
                JSMin jsMin = new JSMin(in, baos);
                jsMin.jsmin();
                byte[] bytes = baos.toByteArray();
                baos.close();
                String jsonStr = new String(bytes);
                System.out.print(jsonStr);
                //解析json
                return AnalzyStyle(jsonStr);
            } catch (Exception e) {
                Log.i("zzz", "读取style失败");
            }
        } else {
            Log.i("zzz", "沒有相应style");
        }
        return null;
    }

    //解析style
    public static Style AnalzyStyle(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        try {
            JSONObject jsonStyle = new JSONObject(jsonStr);
            int style_id = jsonStyle.getInt("style_id");
            Style style = new Style(style_id);
            //判断是否为默认的style
            if (jsonStyle.has(KEY_DEFAULT_STYLE)) {
                if (jsonStyle.getBoolean(KEY_DEFAULT_STYLE)) {
                    DEFAULT_STYLE_ID = style_id;
                }
            }
            JSONArray jsonTypeArray = jsonStyle.getJSONArray("types");
            int length = jsonTypeArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonType = jsonTypeArray.getJSONObject(i);
                //解析type，支持继承功能，通过subtypeName:parentTypeName 实现，只支持单继承。
                String type_name = jsonType.getString("type_name");
                String type_parent_name = "";
                String[] split = type_name.split(":");
                if (split.length == 2) {
                    type_name = split[0];
                    type_parent_name = split[1];
                }
                Type parentType = style.getType(type_parent_name);
                Type type = new Type(type_name, parentType);
                //判断是否是默认的type
                if (jsonType.has(KEY_DEFAULT_TYPE)) {
                    if (jsonType.getBoolean(KEY_DEFAULT_TYPE)) {
                        setDefalutType(type_name);
                    }
                }
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
    public static void changeStyle(int... styleIDs) {
        if (styleIDs == null || styleIDs.length == 0) {
            return;
        }
        int index = 0;
        for (int i = 0; i < styleIDs.length; i++) {
            if (styleIDs[i] == sCurrentStyleId) {
                index = i;
                break;
            }
        }
        index++;
        if (index == styleIDs.length) {
            index = 0;
        }
        sCurrentStyleId = styleIDs[index];
        saveStyle(sCurrentStyleId);
        boolean createAnimator = false;

        for (int i = mActivityStack.size() - 1; i >= 0; i--) {
            Activity activity = mActivityStack.get(i).get();
            if(activity!=null) {
                View view = activity.findViewById(android.R.id.content);
                Log.i("zzz", "activity=" + activity);
                if (!createAnimator) {
                    createAnimator = true;
                    createAnimator(activity);
                }
                setColor(view, true);
            }
        }
    }

    //生成动画,原理：获取当前界面截图，生成一个imageview，添加到decorview中,即覆盖在原来
    //界面的上层，并在一定时间内改变imageview的alpha值，当alpha值为0的时候，将imageview从decorview中移除出去
    private static void createAnimator(Activity activity) {
        if (activity == null) {
            return;
        }
        //获取decorview
        View decorView = activity.getWindow().getDecorView();
        final ImageView imageView = new ImageView(activity);
        int width = activity.getResources().getDisplayMetrics().widthPixels;
        int height = activity.getResources().getDisplayMetrics().heightPixels;
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        //生成截图
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
        int newColor = 0;
        if (view == null) {
            return;
        }
        Object tag = view.getTag(R.id.tag_style);
        if (tag == null && sCurrentStyleId == 0) {
            view.setTag(R.id.tag_style, sCurrentStyleId);
            return;
        }
        if (tag != null) {
            int viewStyle = (int) tag;
            if (viewStyle == sCurrentStyleId) {
                return;
            }
        }
        view.setTag(R.id.tag_style, sCurrentStyleId);
        Object viewTag = view.getTag();
        String typeName = "";
        if (viewTag != null && viewTag instanceof String) {
            typeName = (String) viewTag;
        } else {
            //判断是否设置了默认的type，如果有则使用默认的
            if (sHaveSetDefaluType) {
                typeName = sDefalutTypeName;
            }
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
        //表明此view不需要换肤，直接return
        if (DISABLE_TYPE_NAME.equals(typeName)) {
            return;
        }
        if (view instanceof WebView) {
            setupWebView((WebView) view);
            return;
        }
        //只有在view没有设置背景色或者只使用颜色而不是其他
        if (view.getBackground() == null || (view.getBackground() instanceof ColorDrawable)) {
            newColor = getFSColor(sCurrentStyleId, typeName, Attribute.TYPE_BACKGROUND_COLOR);
            if (newColor != 0) {
                view.setBackgroundColor(newColor);
            }
        }


        if (view instanceof ViewGroup) {

            //只有在listview设置了diver的情况下才换肤
            if (view instanceof ListView && ((ListView) view).getDivider() instanceof ColorDrawable) {
                ListView lv = (ListView) view;
                newColor = getFSColor(sCurrentStyleId, typeName, Attribute.TYPE_DIVER_COLOR);
                if (newColor != 0) {
                    int height = lv.getDividerHeight();
                    lv.setDivider(new ColorDrawable(newColor));
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
                    //如果是ListView则要通过反射将缓存池中的view换肤
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
                newColor = getFSColor(sCurrentStyleId, typeName, Attribute.TYPE_FONT_COLOR);
                if (newColor != 0) {
                    tv.setTextColor(newColor);
                }
            }

        }

    }

    public static void setupWebView(WebView webView) {
        if (webView != null) {
            webView.setBackgroundColor(0);
            Object viewTag = webView.getTag();
            String type_name = "";
            if (viewTag != null && viewTag instanceof String) {
                type_name = (String) viewTag;
            }
            if (TextUtils.isEmpty(type_name)) {
                if (sHaveSetDefaluType) {
                    type_name = sDefalutTypeName;
                }
            }
            if (TextUtils.isEmpty(type_name) || styleMap.get(sCurrentStyleId).getType(type_name) == null) {
                return;
            }
            if (styleMap != null) {
                String backgroudColor = styleMap.get(sCurrentStyleId).getType(type_name).getAttribute(Attribute.TYPE_BACKGROUND_COLOR).getValue();
                String fontColor = styleMap.get(sCurrentStyleId).getType(type_name).getAttribute(Attribute.TYPE_FONT_COLOR).getValue();
                String urlColor = styleMap.get(sCurrentStyleId).getType(type_name).getAttribute(Attribute.TYPE_URL_COLOR).getValue();
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

    private static int getFSColor(int style, String type_name, int attribute_type) {
        int color = 0;
        if (TextUtils.isEmpty(type_name) || styleMap.get(style).getType(type_name) == null) {
            return color;
        }
        if (styleMap != null) {
            try {
                String strColor = styleMap.get(style).getType(type_name).getAttribute(attribute_type).getValue();
                color = Color.parseColor(strColor);
            } catch (NullPointerException e) {
                color = 0;
            }
        }
        return color;
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
        return mySharedPreferences.getInt("style", DEFAULT_STYLE_ID);
    }
}
