package com.andy.demo.activity;

import com.andy.demo.R;

import android.os.Bundle;
import android.widget.ProgressBar;

public class SimpleViewTestActivity  extends BaseActivity{
    ProgressBar mProgressBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_view_test_layout);
        initView();
    }
    
    private void initView() {
        mProgressBar = findView(R.id.svt_progress);
        mProgressBar.setProgress(30);
        mProgressBar.setSecondaryProgress(50);
    }
}
