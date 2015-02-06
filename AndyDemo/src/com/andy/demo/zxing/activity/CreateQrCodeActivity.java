package com.andy.demo.zxing.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.andy.demo.R;
import com.andy.demo.activity.BaseActivity;
import com.andy.demo.zxing.encoding.EncodingHandler;
import com.google.zxing.WriterException;

public class CreateQrCodeActivity extends BaseActivity{
    private EditText contentEt;
    private ImageView qrCodeIv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_qr_code_layout);
        initView();
    }
    
    private void initView() {
        contentEt = findView(R.id.create_qr_code_content_et);
        qrCodeIv = findView(R.id.create_qr_code_iv);
        findView(R.id.create_qr_code_btn).setOnClickListener(new OnClickListener(){
            
            @Override
            public void onClick(View v) {
                String content = contentEt.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = EncodingHandler.createQRCode(content, 570);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    qrCodeIv.setImageBitmap(bitmap);
                }else {
                    Toast.makeText(CreateQrCodeActivity.this, "请输入内容", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
