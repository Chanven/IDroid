package com.andy.demo.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.andy.demo.R;
import com.andy.demo.ui.widget.MyListView;
import com.andy.demo.ui.widget.MyListView.OnRefreshListener;

public class MyExamActivity extends BaseActivity {

	private MyListView myListView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myexam);
		
		initHead();
		mh_title_tv.setText("我的考试");
		
		myListView=(MyListView) findViewById(R.id.myexam_listview);
		myListView.setAdapter(new MyAdatper());
		myListView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {

						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						
						myListView.onRefreshComplete();

					}

				}.execute();
				
			}
		});
		
		myListView.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				MainActivity_old.changeTo(arg2);
				Toast.makeText(getApplicationContext(), arg2+"", Toast.LENGTH_SHORT).show();
			}
		});
		myListView.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), arg2+"", Toast.LENGTH_SHORT).show();
				return true;
			}

			
		});
	}
	
	class MyAdatper extends BaseAdapter{
      
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 10;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View contentView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if(contentView==null)
			{
				contentView=LayoutInflater.from(getApplicationContext()).inflate(R.layout.myexam_item, null);
			}
			
			return contentView;
		}
		
	}
	
	
}
