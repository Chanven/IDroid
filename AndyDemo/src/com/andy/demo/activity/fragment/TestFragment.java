package com.andy.demo.activity.fragment;

import com.andy.demo.R;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TestFragment extends BaseFragment{
    private Context mContext;
    private String mText;
    
    private View mRootView;
    private TextView mTextView;
    
    public static final String VALUE_KEY = "value";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null != mRootView) {
            // 缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，
            // 要不然会发生这个rootview已经有parent的错误。
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (null != parent) {
                parent.removeView(mRootView);
            }
        }else {
            mRootView = inflater.inflate(R.layout.test_fragment_layout, null);
            getData();
            initView();
            initData();
        }
        return mRootView;
    }
    
    private void getData() {
        Bundle bundle = getArguments();
        if (null != bundle) {
            mText = bundle.getString(VALUE_KEY);
        }
    }
    
    private void initView() {
        mTextView = (TextView) mRootView.findViewById(R.id.test_fragment_tv);
    }
    
    private void initData() {
        if (!TextUtils.isEmpty(mText)) {
            mTextView.setText(mText);
        }
    }
}
