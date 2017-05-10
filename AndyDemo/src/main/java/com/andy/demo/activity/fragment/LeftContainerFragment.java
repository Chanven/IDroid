package com.andy.demo.activity.fragment;

import com.andy.demo.R;
import com.andy.demo.activity.BaseActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LeftContainerFragment extends DrawerChildViewFragment{
	private BaseActivity mContext;
	
	public LeftContainerFragment() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		this.mContext = (BaseActivity)getActivity();
		View contentView = inflater.inflate(R.layout.main_layout_left, null);
		//这里初始化左栏UI
		initView(contentView);
		return contentView;
	}

	@Override
	protected ViewGroup createContainerView(LayoutInflater inflater) {
		return null;
	}
	
	private void initView(View container) {
		
	}

}
