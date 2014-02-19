package com.andy.demo.activity.fragment;

import java.io.ByteArrayOutputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.andy.android.util.DLog;
import com.andy.demo.R;
import com.andy.demo.activity.BaseActivity;
import com.andy.demo.activity.SyncTestActivity;
import com.andy.demo.base.Constant;
import com.andy.demo.slidingmenu.app.SlidingFragmentActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

public class CenterContainerFragment extends DrawerChildViewFragment{
	private BaseActivity mContext;
	private SlidingMenu mSlidingMenu;
	
	private IWXAPI wxapi;

	public CenterContainerFragment(){
		
	}
	
	@Override
	protected ViewGroup createContainerView(LayoutInflater inflater) {
		return null;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mSlidingMenu = ((SlidingFragmentActivity)getActivity()).getSlidingMenu();
		mContext = (BaseActivity)getActivity();
		
		View contentView = inflater.inflate(R.layout.main_layout_center, null);
		//初始化中间页UI
		initView(contentView);
		
		wxapi = WXAPIFactory.createWXAPI(mContext, Constant.WX_APP_ID,false);
		boolean register = wxapi.registerApp(Constant.WX_APP_ID);
		System.out.println("wx------>" + register);
		
		return contentView;
	}
	
	private void initView(View container) {
		container.findViewById(R.id.wx_share).setOnClickListener(mOnClickListener);
		container.findViewById(R.id.jc_share).setOnClickListener(mOnClickListener);
		container.findViewById(R.id.sync_test).setOnClickListener(mOnClickListener);
	}
	
	OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.wx_share:
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
				req.transaction = buildTransaction("text");
				req.message = msg;
				req.scene = SendMessageToWX.Req.WXSceneTimeline;
				
				// 调用api接口发送数据到微信
				wxapi.sendReq(req);
				
				break;
			case R.id.jc_share:
				Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
				WXImageObject imgObj = new WXImageObject(bitmap);
				
				WXMediaMessage smsg = new WXMediaMessage();
				smsg.mediaObject = imgObj;
				smsg.thumbData = bmpToByteArray(bitmap, true);
				
				SendMessageToWX.Req  req2 = new SendMessageToWX.Req();
				req2.transaction = buildTransaction("image");
				req2.message = smsg;
				req2.scene = SendMessageToWX.Req.WXSceneTimeline;
				
				wxapi.sendReq(req2);
				
			case R.id.sync_test:
				mContext.startActivity(new Intent(mContext,SyncTestActivity.class));
				break;
			default:
				break;
			}
		}
	};
	
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
}
