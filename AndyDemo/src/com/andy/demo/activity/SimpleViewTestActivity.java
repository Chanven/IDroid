package com.andy.demo.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.andy.demo.R;
import com.andy.demo.adapter.EndlessLoopViewPaperAdapter;
import com.andy.demo.view.widget.ImageIndicator;

public class SimpleViewTestActivity  extends BaseActivity{
    DrawerLayout drawerLayout;
    ProgressBar mProgressBar;
    ListView drawerListView;
    Button drawerToggleBtn;
    
    ImageIndicator mIndicator;
    ViewPager mViewPager;
    EndlessLoopViewPaperAdapter mAdapter;
    List<String> mImageList;
    
    String[] drawerListStrings;
    
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
        
        mIndicator = findView(R.id.image_indicator);
        mViewPager = findView(R.id.enless_image_viewpager);
        
        drawerToggleBtn = findView(R.id.svt_drawer_toggle_btn);
        drawerToggleBtn.setOnClickListener(mOnClickListener);
        drawerListStrings = this.getResources().getStringArray(R.array.drawer_list_array);
        drawerLayout = findView(R.id.drawerlayout_dlyt);
        //设置drawerLayout外背景色
        drawerLayout.setScrimColor(this.getResources().getColor(R.color.transparent));
        drawerListView = findView(R.id.drawer_lv);
        drawerListView.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, drawerListStrings));
        drawerListView.setOnItemClickListener(mOnItemClickListener);
    }
    
    OnClickListener mOnClickListener = new OnClickListener(){

        @Override
        public void onClick(View v) {
            if (!drawerLayout.isDrawerOpen(drawerListView)) {
                drawerLayout.openDrawer(drawerListView);
            }
        }
    };
    
    OnItemClickListener mOnItemClickListener = new OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            drawerListView.setItemChecked(position, true);
            drawerLayout.closeDrawer(drawerListView);
            Toast.makeText(SimpleViewTestActivity.this, drawerListStrings[position], Toast.LENGTH_SHORT).show();
        }
    };
    
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
    	//接收browser跳转intent
        Intent intent = getIntent();
        if (null != intent && Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (null != uri) {
                String name = uri.getQueryParameter("name");
                Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
            }
        }
        
        mImageList = new ArrayList<String>();
        mImageList.add(String.valueOf(R.drawable.bigbang));
        mImageList.add(String.valueOf(R.drawable.hannibal));
        
        mIndicator.setCount(mImageList.size());
        mAdapter = new EndlessLoopViewPaperAdapter(this, mViewPager, mIndicator, mImageList);
        mViewPager.setAdapter(mAdapter);
        mAdapter.setCurrentItem();
    }
}
