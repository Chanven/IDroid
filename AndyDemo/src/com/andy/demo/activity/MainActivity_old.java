package com.andy.demo.activity;



import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TextView;

import com.andy.demo.R;

@SuppressWarnings("deprecation")
public class MainActivity_old extends TabActivity {
    /** Called when the activity is first created. */
	private static TabHost tabHost;
	private static Context context;
	private TextView main_tab_new_message;
	private static RadioButton main_tab_myExam;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_old);
        context=this;
        main_tab_new_message=(TextView) findViewById(R.id.main_tab_new_message);
        main_tab_new_message.setVisibility(View.VISIBLE);
        main_tab_new_message.setText("10");
        
        main_tab_myExam=(RadioButton) findViewById(R.id.main_tab_myExam);
        
        tabHost=this.getTabHost();
        TabHost.TabSpec spec;
        Intent intent;
       
        intent=new Intent().setClass(this, AddExamActivity.class);
        spec=tabHost.newTabSpec("添加考试").setIndicator("添加考试").setContent(intent);
        tabHost.addTab(spec);
        
        intent=new Intent().setClass(this,MyExamActivity.class);
        spec=tabHost.newTabSpec("我的考试").setIndicator("我的考试").setContent(intent);
        tabHost.addTab(spec);
        
        intent=new Intent().setClass(this, MyMessageActivity.class);
        spec=tabHost.newTabSpec("我的通知").setIndicator("我的通知").setContent(intent);
        tabHost.addTab(spec);
        
     
        intent=new Intent().setClass(this, SettingActivity.class);
        spec=tabHost.newTabSpec("设置").setIndicator("设置").setContent(intent);
        tabHost.addTab(spec);
        tabHost.setCurrentTab(1);
        
        RadioGroup radioGroup=(RadioGroup) this.findViewById(R.id.main_tab_group);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.main_tab_addExam://添加考试
					tabHost.setCurrentTabByTag("添加考试");
					break;
				case R.id.main_tab_myExam://我的考试
					tabHost.setCurrentTabByTag("我的考试");
					break;
				case R.id.main_tab_message://我的通知
					tabHost.setCurrentTabByTag("我的通知");
					break;
				case R.id.main_tab_settings://设置
					tabHost.setCurrentTabByTag("设置");
					break;
				default:
					//tabHost.setCurrentTabByTag("我的考试");
					break;
				}
			}
		});
    }
    
    public static void changeTo(int id){
    	
    	tabHost.setCurrentTabByTag("我的考试");
    	Animation slideLeftIn = AnimationUtils.loadAnimation(context, R.anim.slide_left_in);
    	tabHost.getCurrentView().startAnimation(slideLeftIn);
    	main_tab_myExam.setChecked(true);
    }
   
}