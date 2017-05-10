package com.andy.demo.activity;

import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.andy.android.util.AutoCancelController;
import com.andy.android.util.Cancellable;
import com.andy.android.util.DLog;
import com.andy.demo.ApplicationEx;
import com.andy.demo.base.Constant;
import com.andy.demo.view.UniLoadingDialog;

public class BaseActivity extends FragmentActivity {

	protected Button mh_left_btn, mh_right_btn;
	protected TextView mh_title_tv;
	
	private AutoCancelController mAutoCancelController;
	
	private UniLoadingDialog mLoadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 放入活动管理器
		ApplicationEx application = (ApplicationEx) this.getApplication();
		application.getActivityManager().pushActivity(this);
		initScreenDeminsion();
		DLog.d(getClass().getSimpleName(), "onCreate()");
	}
	
	/**
	 * 等同于findViewById()
	 * @param id
	 * @return view
	 */
    @SuppressWarnings("unchecked")
    protected <T extends View>T findView(int id) {
        return (T) super.findViewById(id);
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
		if (null != mAutoCancelController) {
			mAutoCancelController.clean();
		}
		ApplicationEx application = (ApplicationEx) getApplication();
		application.getActivityManager().popActivity(this);
		DLog.d(getClass().getSimpleName(), "OnDestroy()");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		return super.onKeyDown(keyCode, event);
		}
	
	public void autoCancel(Cancellable task) {
		mAutoCancelController.add(task);
	}

	public void removeAutoCancel(Cancellable task) {
		mAutoCancelController.remove(task);
	}

	public AutoCancelController getAutoCancelController() {
		if (null == mAutoCancelController) {
			mAutoCancelController = new AutoCancelController();
		}
		return mAutoCancelController;
	}
	
	/**显示加载框*/
	protected void showLoadingDialog(){
		if (null == mLoadingDialog) {
			mLoadingDialog = new UniLoadingDialog(this);
		}
		mLoadingDialog.show();
	}
	
	/**隐藏加载框*/
	protected void dismissLoadingDialog(){
		if (null != mLoadingDialog && mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
	}
	
	protected void toastMsgLong(int resId) {
		toastMsgLong(getString(resId));
	}
	
	protected void toastMsgShort(int resId) {
		toastMsgShort(getString(resId));
	}
	
	protected void toastMsgLong(String msg) {
		show(msg, Toast.LENGTH_LONG);
	}
	protected void toastMsgShort(String msg) {
		show(msg, Toast.LENGTH_SHORT);
	}
	
	protected void show(final String msg, final int duration) {
		if (TextUtils.isEmpty(msg)) {
			return;
		}
		if (Looper.myLooper() != Looper.getMainLooper()) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(BaseActivity.this, msg, duration).show();
				}
			});
		}else {
			Toast.makeText(BaseActivity.this, msg, duration).show();
		}
	}
	
}
