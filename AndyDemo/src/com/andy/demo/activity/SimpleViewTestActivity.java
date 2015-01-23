package com.andy.demo.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.andy.demo.R;

public class SimpleViewTestActivity  extends BaseActivity{
    ProgressBar mProgressBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_view_test_layout);
        initView();
        initData();
    }
    
    private void initView() {
        mProgressBar = findView(R.id.svt_progress);
        mProgressBar.setProgress(30);
        mProgressBar.setSecondaryProgress(50);
    }
    
    /***
     * 测试browser跳app，在webview中 ↓↓
     * 相应在Manifest中也要配置
     * <p>Intent intent = new Intent(); </p>
     * <p>intent.setAction(Intent.ACTION_VIEW);</p>
     * <p>intent.addCategory(Intent.CATEGORY_BROWSABLE); </p>
     * <p>intent.setData(Uri.parse("andy://?name=andy&id=7"));</p>
     * <p>context.startActivity(intent);</p>
     */
    private void initData() {
        Intent intent = getIntent();
        if (null != intent && Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (null != uri) {
                String name = uri.getQueryParameter("name");
                Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
