package com.zgh.mvpdemo.base;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.zgh.stylelib.style.StyleHelper;

/**
 * Created by yuelin on 2016/7/18.
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        childOnCreate(savedInstanceState);
        StyleHelper.initActivity(this);

    }


    public abstract   void childOnCreate(Bundle savedInstanceState);

    public void changeMode(){
        StyleHelper.changeMode();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        StyleHelper.destroyActivity();
    }
}
