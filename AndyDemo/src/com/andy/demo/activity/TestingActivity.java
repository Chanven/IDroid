package com.andy.demo.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.andy.demo.R;

public class TestingActivity extends BaseActivity {

	private Button button;
	private TextView textView1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		View contentView = LayoutInflater.from(this.getParent()).inflate(R.layout.testing, null);
		setContentView(contentView);
		
		button=(Button) findViewById(R.id.button1);
		textView1=(TextView) findViewById(R.id.textView1);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				textView1.setText("测试页面");
				AddExamActivity.changeTo();
				
			}
		});
		
	}
}
