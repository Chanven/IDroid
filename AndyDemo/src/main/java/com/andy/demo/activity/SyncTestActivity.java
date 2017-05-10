package com.andy.demo.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CancellationException;

/**
 * 测试类Activity
 * 测试AutoCancelServiceFramework框架、DownloadService下载服务、以及http请求，xml解析模块
 *
 * @author ivankuo
 */
public class SyncTestActivity extends BaseActivity {
    //获取SD卡的根目录
    String sdcard = Environment.getExternalStorageDirectory() + "/";
    //文件要保存的位置
    String filepath = sdcard + "ImageDownload/";
    private ImageView imageView;
    private TextView xmlTestTv;
    private TextView jsonTestTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sync_test_layout);
        initView();
        initData();
        new getJdInfoFramework(this.getAutoCancelController()).executeOnExecutor(ApplicationEx.app.getSerialExecutor());
    }

    private void initView() {
        imageView = (ImageView) this.findViewById(R.id.img);
        imageView.setImageResource(R.drawable.icon);
        xmlTestTv = findView(R.id.xml_test_tv);
        jsonTestTv = findView(R.id.json_test_tv);
        findView(R.id.down_test_btn).setOnClickListener(mOnClickListener);
    }

    OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.down_test_btn:
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            GetImage("http://a4.att.hudong.com/20/84/01300001235467137792849329123.jpg");
                        }
                    }).start();
                    break;

                default:
                    break;
            }
        }
    };

    private void initData() {
        autoCancel(new AutoCancelServiceFramework<Void, Bitmap, Bitmap>(this) {
            private ProgressDialog mDialog = null;
            File file = new File(filepath + "test.jpg");

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
                    final MyIdInitResult result = mDownloadService.initMyId(true);
                    if (null != result) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                xmlTestTv.setText(JsonUtils.toJsonString(result));
                            }
                        });

                    }
                } catch (CancellationException e) {
                    e.printStackTrace();
                } catch (XResponseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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

    /**
     * 下载文件
     */
    public void GetImage(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream istream = conn.getInputStream();
            String filename = urlString.substring(urlString.lastIndexOf("/") + 1);

            File directory = new File(filepath);
            if (!directory.exists()) {
                directory.mkdir();
            }
            File newFile = new File(filepath + filename);
            newFile.createNewFile();

            OutputStream output = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = istream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            output.flush();
            output.close();
            istream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //测试http请求，json解析模块
    class getJdInfoFramework extends AutoCancelFramework<Void, Void, KuaidiInfo> {
        AutoCancelController mController;

        public getJdInfoFramework(AutoCancelController autoCancelController) {
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
                jsonTestTv.setText(JsonUtils.toJsonString(result));
            }
        }

    }
}
