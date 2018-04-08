package com.example.administrator.mylibrary;

import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import itbour.onetouchshow.base.ABaseActivity;
import itbour.onetouchshow.utils.UIUtils;

public class MainActivity extends ABaseActivity {


    @Override
    protected void initPresenter() {

    }

    @Override
    protected void initData() {

    }


    @Override
    protected void initTitle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.System.canWrite(this);
        }
        setTopLeftButton(R.mipmap.return_icon).
                setTitleTextColor(UIUtils.getColor(R.color.white)).
                setBackgroundColor(UIUtils.getColor(R.color.black));
        getToolBarView().getLeftimageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        setTitleText("测试");
    }


    @Override
    protected void initView() {

    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {

    }

    @Override
    public int getFragmentContentId() {
        return 0;
    }
}
