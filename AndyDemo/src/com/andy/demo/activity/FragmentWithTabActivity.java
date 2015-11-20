package com.andy.demo.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.andy.demo.R;
import com.andy.demo.activity.fragment.TestFragment;

public class FragmentWithTabActivity extends FragmentActivity{
    RadioGroup tabRadioGroup;
    ViewPager mViewPager;
    FragmentAdapter mAdapter;
    List<Fragment> mFragments;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_with_tab_layout);
        initView();
        initData();
    }
    
    private void initView() {
        tabRadioGroup = (RadioGroup) findViewById(R.id.fragment_tab_rg);
        tabRadioGroup.check(R.id.top_tab1_rbtn);
        mViewPager = (ViewPager) findViewById(R.id.fragment_tab_vp);
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);
        
        tabRadioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }
    
    private void initData() {
        mFragments = new ArrayList<Fragment>();
        
        TestFragment testFragment = new TestFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TestFragment.VALUE_KEY, "tab 1");
        testFragment.setArguments(bundle);
        mFragments.add(testFragment);
        
        testFragment = new TestFragment();
        bundle = new Bundle();
        bundle.putString(TestFragment.VALUE_KEY, "tab 2");
        testFragment.setArguments(bundle);
        mFragments.add(testFragment);
        
        testFragment = new TestFragment();
        bundle = new Bundle();
        bundle.putString(TestFragment.VALUE_KEY, "tab 3");
        testFragment.setArguments(bundle);
        mFragments.add(testFragment);
        
        mAdapter = new FragmentAdapter(this.getSupportFragmentManager(), mFragments);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mAdapter);
    }
    
    OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener(){
        
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.top_tab1_rbtn:
                    mViewPager.setCurrentItem(0);
                    break;
                case R.id.top_tab2_rbtn:
                    //无滑动动画
                    mViewPager.setCurrentItem(1,false);
                    break;
                case R.id.top_tab3_rbtn:
                    mViewPager.setCurrentItem(2);
                    break;

                default:
                    break;
            }
        }
    };
    
    OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener(){
        
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    tabRadioGroup.check(R.id.top_tab1_rbtn);
                    break;
                case 1:
                    tabRadioGroup.check(R.id.top_tab2_rbtn);
                    break;
                case 2:
                    tabRadioGroup.check(R.id.top_tab3_rbtn);
                    break;
                default:
                    break;
            }
        }
        
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            
        }
        
        @Override
        public void onPageScrollStateChanged(int arg0) {
            
        }
    };
    
    
    class FragmentAdapter extends FragmentPagerAdapter{
        private List<Fragment> mFragments;

        public FragmentAdapter(FragmentManager fm,List<Fragment> fragments){
            super(fm);
            this.mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            if (null != mFragments && position < mFragments.size() && null != mFragments.get(position)) {
                return mFragments.get(position);
            }
            return null;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
        
    }
}
