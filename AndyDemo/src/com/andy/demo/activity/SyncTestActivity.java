package com.andy.demo.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CancellationException;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.andy.demo.R;
import com.andy.demo.analysis.bean.MyIdInitResult;
import com.andy.demo.netapi.exception.XResponseException;
import com.andy.demo.utils.AutoCancelServiceFramework;
import com.andy.demo.utils.StoragePathManager;

/**
 * 测试类Activity
 * 测试AutoCancelServiceFramework框架、DownloadService下载服务、以及http请求，xml解析模块
 * @author ivankuo
 *
 */
public class SyncTestActivity extends BaseActivity{
	private ImageView imageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sync_test_layout);
		initView();
		initData();
	}
	
	private void initView(){
		imageView = (ImageView) this.findViewById(R.id.img); 
		imageView.setImageResource(R.drawable.icon);
	}
	
	private void initData() {
		autoCancel(new AutoCancelServiceFramework<Void, Bitmap, Bitmap>(this){
			private ProgressDialog mDialog = null;
			File file = new File(StoragePathManager.get().getMainPath() + "test.jpg");
			@Override
			protected void onPreExecute() {
				if (mDialog == null) {
					mDialog = new ProgressDialog(SyncTestActivity.this);
					mDialog.setMessage("loading...");
				}
				mDialog.show();
			}
			
			@Override
			protected Bitmap doInBackground(Void... params) {
				Bitmap bitmap = null;
				try {
					createDownlaodService();
					FileOutputStream out = new FileOutputStream(file);
					mDownloadService.download("http://images.csdn.net/20140214/QQ%E6%88%AA%E5%9B%BE20140214115548.jpg", 
							0, 0, out, null);
					bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
					
					//测试http请求，xml解析模块
					MyIdInitResult result = mDownloadService.initMyId(true);
					String url = result.qrCodeUrl;
					
				} catch (CancellationException e) {
					e.printStackTrace();
				} catch (XResponseException e) {
					e.printStackTrace();
				}catch (IOException e) {
					e.printStackTrace();
				}
				return bitmap;
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				if (result != null) {
					mDialog.dismiss();
					imageView.setImageBitmap(result);
				}
			}
			
		}.executeOnExecutor(this.getSerialExecutor()));
	}
}
