package com.andy.demo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.andy.android.util.DLog;
import com.andy.demo.R;
import com.andy.demo.base.Constant;

public class BaseActivity extends FragmentActivity {

	protected Button mh_left_btn, mh_right_btn;
	protected TextView mh_title_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 放入活动管理器
		ApplicationEx application = (ApplicationEx) this.getApplication();
		application.getActivityManager().pushActivity(this);
		initScreenDeminsion();
		DLog.d(getClass().getSimpleName(), "onCreate()");
	}
	
	protected void initHead() {
		mh_left_btn = (Button) findViewById(R.id.mh_left_btn);
		mh_right_btn = (Button) findViewById(R.id.mh_right_btn);
		mh_title_tv = (TextView) findViewById(R.id.mh_title_tv);
	}
	
	// 获取和设置屏幕分辨率
	private void initScreenDeminsion() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		Constant.m_screenW = dm.widthPixels;
		Constant.m_screenH = dm.heightPixels;
		Constant.DENSITY = dm.density;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();

		// 按返回的退出，自动从堆栈中退出
		 ApplicationEx application = (ApplicationEx) getApplication();
		 application.getActivityManager().popActivity(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		DLog.d(getClass().getSimpleName(), "OnResume()");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		DLog.d(getClass().getSimpleName(), "OnPause()");
		if (isFinishing()) {
			
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		ApplicationEx application = (ApplicationEx) getApplication();
		application.getActivityManager().popActivity(this);
		DLog.d(getClass().getSimpleName(), "OnDestroy()");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getParent());
			builder.setMessage("你确定退出吗？")
					.setCancelable(false)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									finish();
									System.exit(0);
								}
							})
					.setNegativeButton("返回",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
			return true;
		}

		return super.onKeyDown(keyCode, event);
		}
}
