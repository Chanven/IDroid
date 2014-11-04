package com.andy.demo.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.andy.demo.R;
import com.andy.demo.base.Constant;
import com.andy.demo.utils.CommonUtils;
import com.andy.demo.utils.ImageUtils;
import com.andy.demo.utils.StoragePathManager;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

public class ShareTestActivity extends BaseActivity{
    BaseActivity mContext;
    
    private IWXAPI wxapi;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_test_layout);
        mContext = this;
        
        initView();
        
        wxapi = WXAPIFactory.createWXAPI(mContext, Constant.WX_APP_ID);
        boolean register = wxapi.registerApp(Constant.WX_APP_ID);
        System.out.println("wx------>" + register);
        
    }
    
    private void initView() {
        this.findViewById(R.id.wx_text_share).setOnClickListener(mOnClickListener);
        this.findViewById(R.id.wx_image_share).setOnClickListener(mOnClickListener);
        this.findViewById(R.id.system_share).setOnClickListener(mOnClickListener);
        this.findViewById(R.id.system_except_share).setOnClickListener(mOnClickListener);
        this.findViewById(R.id.system_target_share).setOnClickListener(mOnClickListener);
    }
    
    OnClickListener mOnClickListener = new OnClickListener(){
        
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.wx_text_share:
                    String share_text = "Share API Test";
                    // 初始化一个WXTextObject对象
                    WXTextObject textObject = new WXTextObject();
                    textObject.text = share_text;
                    
                    // 用WXTextObject对象初始化一个WXMediaMessage对象
                    WXMediaMessage msg = new WXMediaMessage(textObject);
                    msg.mediaObject = textObject;
                    msg.description = share_text;
                    
                    // 构造一个Req
                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = CommonUtils.buildTransaction("text");
                    req.message = msg;
                    req.scene = SendMessageToWX.Req.WXSceneTimeline;
                    
                    // 调用api接口发送数据到微信
                    wxapi.sendReq(req);
                    
                    break;
                case R.id.wx_image_share:
                    //图片分享
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
                    WXImageObject imgObj = new WXImageObject(bitmap);
                    
                    WXMediaMessage smsg = new WXMediaMessage();
                    smsg.mediaObject = imgObj;
                    smsg.thumbData = ImageUtils.bmpToByteArray(bitmap, true);
                    
                    SendMessageToWX.Req  req2 = new SendMessageToWX.Req();
                    req2.transaction = CommonUtils.buildTransaction("image");
                    req2.message = smsg;
                    req2.scene = SendMessageToWX.Req.WXSceneTimeline;
                    
                    wxapi.sendReq(req2);
                    break;
                case R.id.system_share:
                    shareExcept("");
                    break;
                case R.id.system_except_share:
                    shareExcept("bluetooth");
                    break;
                case R.id.system_target_share:
                    targetShare("com.tencent.mm");
                    break;
                default:
                    break;
            }
        }
    };
    
    /**过滤掉某一个分享，可用包名或关键字过滤，如微信com.tecent.mm 蓝牙bluetooth*/
    private void shareExcept(String type) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("image/*");
        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = mContext.getPackageManager().queryIntentActivities(share, 0);
        if (!resInfo.isEmpty()) {
            List<Intent> list = new ArrayList<Intent>();
            for (ResolveInfo info: resInfo) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                ActivityInfo activityInfo = info.activityInfo;
                if (!TextUtils.isEmpty(type) && (activityInfo.packageName.toLowerCase().contains(type) ||
                    activityInfo.name.toLowerCase().contains(type))) {
                    continue;
                }
                intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                intent.putExtra(Intent.EXTRA_TEXT, "your text");
                // 支持微信朋友圈图片+文字分享，important！！！
                intent.putExtra("Kdescription", "your text");
                // Optional, just if you wanna share an image.
                intent.putExtra(Intent.EXTRA_STREAM,
                    Uri.fromFile(new File(StoragePathManager.get().getMainPath() + "test.jpg")));
                 intent.setPackage(info.activityInfo.packageName);
                 intent.setComponent(new ComponentName(activityInfo.packageName, activityInfo.name));
                intent.setClassName(activityInfo.packageName, activityInfo.name);
                list.add(intent);
            }
            Intent chooserIntent = Intent.createChooser(list.remove(0), "Select");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, list.toArray(new Parcelable[] {}));
            startActivity(chooserIntent);
        }
    }
    
    /**指定某一个分享，可用包名或关键字过滤，如微信com.tecent.mm 蓝牙bluetooth*/
    private void targetShare(String type){
        boolean found = false;
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("image/*");
//        share.setType("text/*");
        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = mContext.getPackageManager().queryIntentActivities(share, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info: resInfo) {
                ActivityInfo activityInfo = info.activityInfo;
                if (activityInfo.packageName.toLowerCase().contains(type) ||
                    activityInfo.name.toLowerCase().contains(type)) {
                    share.putExtra(Intent.EXTRA_SUBJECT, "subject");
                    share.putExtra(Intent.EXTRA_TEXT, "your text");
                  //支持微信朋友圈图片+文字分享，important！！！
                    share.putExtra("Kdescription", "your text");
                    // Optional, just if you wanna share an image.
                    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(StoragePathManager.get().getMainPath() + "test.jpg")));
                    share.setPackage(info.activityInfo.packageName);
//                    intent.setComponent(new ComponentName(activityInfo.packageName, activityInfo.name));
//                    intent.setClassName(activityInfo.packageName, activityInfo.name);
                    found = true;
                    break;
                }
            }
            if (!found) {
                return;
            }
            startActivity(Intent.createChooser(share, "Select"));
        }
    }
}
