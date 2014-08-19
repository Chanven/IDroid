package com.andy.demo.jasonnet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;

import com.andy.demo.analysis.bean.MyIdInitResult;
import com.andy.demo.jasonnet.data.KuaidiInfo;
import com.andy.demo.netapi.ConstantConfig;
import com.andy.demo.netapi.exception.XResponseException;
/**
 * 接口实现
 * @author ivankuo
 *
 */
public class XBusinessAgent extends XApiAbstractAgent{
	private final int startConnectTimes = 0;
	private String server_action_url = "";
	private String mUriAction = "";
	private Map<String, String> mMap = null;
	
	public  XBusinessAgent() {
		server_action_url = ConstantConfig.PLATFORM_SERVER_HOST;
	}
	
	public MyIdInitResult getMyIdInit() throws CancellationException, IOException, XResponseException{
		prepare();
		mUriAction = "myid/init.action";
		mMap = new HashMap<String, String>();
		mMap.put("qrCodelogin", "1");
		MyIdInitResult result = doPost(mMap, server_action_url, mUriAction, startConnectTimes, MyIdInitResult.class);
		return result;
	}
	
	public KuaidiInfo getJdInfo() throws CancellationException, IOException, XResponseException{
		prepare();
		server_action_url = "http://www.kuaidi100.com/query";
		mMap = new HashMap<String, String>();
		mMap.put("type", "quanfengkuaidi");
		mMap.put("postid", "330012187993");
		KuaidiInfo result = doPost(mMap, server_action_url, mUriAction, startConnectTimes, KuaidiInfo.class);
		return result;
	}
	
}
