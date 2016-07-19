package com.zgh.mvpdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.zgh.mvpdemo.R;
import com.zgh.mvpdemo.base.BaseActivity;
import com.zgh.stylelib.style.StyleHelper;

/**
 * Created by yuelin on 2016/7/18.
 */
public class DetailActivity extends BaseActivity {
    @Override
    public void childOnCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_detial);
        findViewById(R.id.btn_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMode();
            }
        });
    }


}
