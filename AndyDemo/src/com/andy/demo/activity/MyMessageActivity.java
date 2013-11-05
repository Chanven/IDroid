package com.andy.demo.activity;

import com.andy.demo.R;

import android.os.Bundle;
import android.view.View;

public class MyMessageActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mymessage);
		
		initHead();
		mh_left_btn.setVisibility(View.INVISIBLE);
		mh_title_tv.setText("我的通知");
	}
	

}
