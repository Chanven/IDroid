package com.andy.demo.activity;


import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.andy.demo.R;

@SuppressWarnings("deprecation")
public class AddExamActivity extends ActivityGroup {

	protected Button btn_leftTop, btn_rightTop;
	protected TextView tv_head;
	
	private  static LocalActivityManager manager;
	private  RadioGroup radioGroup;
	private  static LinearLayout container;
	public  static Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addexam);
		context=this;
		initHead();
		
		manager=getLocalActivityManager();
		container= (LinearLayout)findViewById(R.id.container);
        radioGroup=(RadioGroup) this.findViewById(R.id.add_tab_group);
        
        container.removeAllViews();
		container.addView(manager.startActivity(
				"PAGE_0",
				new Intent(context, MyExamActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView());
        
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.main_tab_addExam://添加考试
					container.removeAllViews();
					container.addView(manager.startActivity(
							"PAGE_0",
							new Intent(context, MyExamActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
							.getDecorView());
					break;
				case R.id.main_tab_myExam://我的考试
					container.removeAllViews();
					container.addView(manager.startActivity(
							"PAGE_1",
							new Intent(context, MyMessageActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
							.getDecorView());
					break;
				case R.id.main_tab_message://我的通知
					container.removeAllViews();
		            container.addView(manager.startActivity(
		            		"PAGE_2",
		                    new Intent(context, SettingActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
		                    .getDecorView());
					break;
				case R.id.main_tab_testing://测试
					container.removeAllViews();
		            container.addView(manager.startActivity(
		            		"PAGE_3",
		                    new Intent(context, TestingActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
		                    .getDecorView());
					break;
				case R.id.main_tab_settings://设置
					container.removeAllViews();
		            container.addView(manager.startActivity(
		            		"PAGE_4",
		                    new Intent(context, MyExamActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
		                    .getDecorView());
					break;
				default:
					//tabHost.setCurrentTabByTag("我的考试");
					break;
				}
			}
		});
	}
    public static void changeTo(){
    	Animation slideLeftIn = AnimationUtils.loadAnimation(context, R.anim.slide_bottom_in_no_alpha);
    	
    	container.removeAllViews();
        container.addView(manager.startActivity(
        		"PAGE_4",
                new Intent(context, MyExamActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                .getDecorView());
        container.startAnimation(slideLeftIn);
    }
	protected void initHead() {
		btn_leftTop = (Button) findViewById(R.id.mh_left_btn);
		btn_rightTop = (Button) findViewById(R.id.mh_right_btn);
		tv_head = (TextView) findViewById(R.id.mh_title_tv);
		
		btn_leftTop.setVisibility(View.INVISIBLE);
		tv_head.setText("添加考试");
	}
	
   @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

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
