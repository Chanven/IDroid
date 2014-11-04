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
import android.widget.TextView;

import com.andy.android.util.AutoCancelController;
import com.andy.android.util.AutoCancelFramework;
import com.andy.demo.ApplicationEx;
import com.andy.demo.R;
import com.andy.demo.analysis.bean.MyIdInitResult;
import com.andy.demo.jsonnet.XBusinessAgent;
import com.andy.demo.jsonnet.data.KuaidiInfo;
import com.andy.demo.netapi.AutoCancelServiceFramework;
import com.andy.demo.netapi.exception.XResponseException;
import com.andy.demo.utils.JsonUtils;
import com.andy.demo.utils.StoragePathManager;

/**
 * 测试类Activity
 * 测试AutoCancelServiceFramework框架、DownloadService下载服务、以及http请求，xml解析模块
 * @author ivankuo
 *
 */
public class SyncTestActivity extends BaseActivity{
	private ImageView imageView;
	private TextView testTv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sync_test_layout);
		initView();
		initData();
		new getJdInfoFramework(this.getAutoCancelController()).executeOnExecutor(ApplicationEx.app.getSerialExecutor());
	}
	
	private void initView(){
		imageView = (ImageView) this.findViewById(R.id.img); 
		imageView.setImageResource(R.drawable.icon);
		testTv = (TextView) this.findViewById(R.id.test_tv);
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
				if (mDialog != null && mDialog.isShowing()) {
					mDialog.dismiss();
				}
				
				if (result != null) {
					imageView.setImageBitmap(result);
				}
			}
			
		}.executeOnExecutor(ApplicationEx.app.getSerialExecutor()));
	}
	
	//测试http请求，json解析模块
	class getJdInfoFramework extends AutoCancelFramework<Void, Void, KuaidiInfo>{
	    AutoCancelController mController;

        public getJdInfoFramework(AutoCancelController autoCancelController){
            super(autoCancelController);
            if (null != autoCancelController) {
                this.mController = autoCancelController;
                mController.add(this);
            }
        }

        @Override
        protected KuaidiInfo doInBackground(Void... params) {
            XBusinessAgent agent = new XBusinessAgent();
            KuaidiInfo info = null;
            try {
                info = agent.getJdInfo();
            } catch (CancellationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XResponseException e) {
                e.printStackTrace();
            }
            return info;
        }
        
        @Override
        protected void onPostExecute(KuaidiInfo result) {
            super.onPostExecute(result);
            if (null != mController) {
                mController.remove(this);
            }
            if (null != result) {
                testTv.setText(JsonUtils.toJsonString(result));
            }
        }
	    
	}
}
