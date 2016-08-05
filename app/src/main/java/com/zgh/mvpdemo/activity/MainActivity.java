package com.zgh.mvpdemo.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.zgh.mvpdemo.R;
import com.zgh.mvpdemo.base.BaseActivity;
import com.zgh.mvpdemo.view.news.ListFragment;
import com.zgh.stylelib.style.StyleHelper;

public class MainActivity extends BaseActivity {
    private static final String TAG = "zgh";
    TabLayout tabLayout;
    ViewPager viewPager;
    String[] titles = new String[]{"头条", "科技", "军事"};


    @Override
    public void childOnCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_baidu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StyleHelper.changeStyle(0,1);
            }
        });
        findViewById(R.id.btn_wangyi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StyleHelper.changeStyle(0,2);
            }
        });
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return new ListFragment();
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        });
        tabLayout.setupWithViewPager(viewPager);

    }


}
