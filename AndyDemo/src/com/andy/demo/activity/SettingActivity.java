package com.andy.demo.activity;

import android.os.Bundle;

import com.andy.demo.R;

public class SettingActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		
		initHead();
		mh_title_tv.setText("设置");
	}
	
	
}
