package com.andy.demo.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andy.demo.R;
import com.andy.demo.activity.BaseActivity;

public class RightContainerFragment extends DrawerChildViewFragment{
	private BaseActivity mContext;
	
	public RightContainerFragment() {
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
		View contentView = inflater.inflate(R.layout.main_layout_right, null);
		//这里初始化右栏UI
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
