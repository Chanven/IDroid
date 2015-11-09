package com.andy.demo.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andy.android.util.AutoCancelController;
import com.andy.android.util.AutoCancelFramework;
import com.andy.demo.ApplicationEx;
import com.andy.demo.R;
import com.andy.demo.adapter.ContactAdapter;
import com.andy.demo.analysis.bean.LocalContact;
import com.andy.demo.utils.CommonUtils;
import com.andy.demo.utils.PinYin;
import com.andy.demo.utils.ScreenUtils;
import com.andy.demo.view.pinnedheader.PinnedHeaderListView;
import com.andy.demo.view.pinnedheader.PinnedHeaderListView.OnItemClickListener;
import com.andy.demo.view.widget.ContactSideBar;

/**
 * 本地联系人
 * @author Chanven
 * @date 2015-10-6
 */
public class MyContactAcitivy extends BaseActivity{
	private PinnedHeaderListView mListView;
	private Button mSelectBtn;
	private LinearLayout mSelectLyt;
	private TextView mSelectNumTv;
	private ContactSideBar mSideBar;
	private TextView mCurrentCharTv;
	
	private ContactAdapter mAdapter;
	private List<LocalContact> mLocalContactInfos;
	
	private int mSelectNum;
	private boolean isAll;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_contact);
		initView();
		initData();
	}
	
	private void initView() {
		mListView = findView(R.id.lv_contact);
		mSelectBtn = findView(R.id.btn_contact_select);
		mSelectLyt = findView(R.id.lyt_contact_select);
		mSelectNumTv = findView(R.id.tv_contact_selected_num);
		
		mSelectBtn.setOnClickListener(mOnClickListener);
		
		mSideBar = findView(R.id.sidebar_contact);
		initCurrentChar();
		mSideBar.setTextView(mCurrentCharTv);
	}
	
	private void initData() {
		mAdapter = new ContactAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(mOnItemClickListener);
		mSideBar.setListView(mListView);
		new getLocalContactFramework(getAutoCancelController()).executeOnExecutor(ApplicationEx.app
				.getMainExecutor());
	}
	
	OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_contact_select:
				checkAll();
				break;
			default:
				break;
			}
		}
	};
	
	protected void initCurrentChar() {
		WindowManager windowManager;
		mCurrentCharTv = (TextView) LayoutInflater.from(this).inflate(R.layout.contact_current_char, null);
		mCurrentCharTv.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				ScreenUtils.dip2px(this, 80),
				ScreenUtils.dip2px(this, 80),
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		try {
			windowManager.addView(mCurrentCharTv, lp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		
		@Override
		public void onSectionClick(AdapterView<?> adapterView, View view, int section, long id) {
			
		}
		
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int section, int position, long id) {
			if (null != mAdapter) {
				LocalContact contact = (LocalContact) mAdapter.getItem(section, position);
				if (null != contact) {
					if (contact.isCheck()) {
						mSelectNum --;
					}else {
						mSelectNum ++;
					}
					contact.setCheck(!contact.isCheck());
				}
				mAdapter.notifyDataSetChanged();
				if (mSelectNum > 0) {
					mSelectLyt.setVisibility(View.VISIBLE);
					mSelectNumTv.setText(mSelectNum + " items selected");
					if (mSelectNum == mAdapter.getCount()) {
						mSelectBtn.setText("UnSelect All");
					}else {
						mSelectBtn.setText("Select All");
					}
				}else {
					mSelectLyt.setVisibility(View.GONE);
				}
			}
		}
	};
	
	/**全选/反选*/
	private void checkAll() {
		isAll = !isAll;
		int size = mLocalContactInfos.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				LocalContact contact = mLocalContactInfos.get(i);
				contact.setCheck(isAll);
			}
		}
		mAdapter.notifyDataSetChanged();
		if (isAll) {
			mSelectNum = size;
			mSelectBtn.setText("UnSelect All");
		}else {
			mSelectNum = 0;
			mSelectBtn.setText("Select All");
		}
		mSelectNumTv.setText(mSelectNum + " items selected");
	}
	
	void setAdapter(){
		if (null == mAdapter) {
			mAdapter = new ContactAdapter(this);
		}
		mAdapter.setContacts(mLocalContactInfos, "");
	}
	
	/**get the selected contact list*/
	private List<LocalContact> getSelectedContacts() {
		List<LocalContact> result = new ArrayList<LocalContact>();
		for (int i = 0; i < mLocalContactInfos.size(); i++) {
			LocalContact contact = mLocalContactInfos.get(i);
			if (contact.isCheck()) {
				result.add(contact);
			}
		}
		return result;
	}
	
	class getLocalContactFramework extends AutoCancelFramework<Void, Void, List<LocalContact>>{

		public getLocalContactFramework(AutoCancelController autoCancelController) {
			super(autoCancelController);
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showLoadingDialog();
		}

		@Override
		protected List<LocalContact> doInBackground(Void... params) {
			return getAllContacts();
		}
		
		@Override
		protected void onPostExecute(List<LocalContact> result) {
			super.onPostExecute(result);
			dismissLoadingDialog();
			if (null != result && result.size() > 0) {
				mLocalContactInfos = result;
				setAdapter();
			}
		}
	}
	
	/**
	 * 获取手机通讯录中联系人信息列表
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<LocalContact> getAllContacts(){
		 List<LocalContact> result = null;
		 if (!CommonUtils.havePermission(this, "android.permission.READ_CONTACTS")) {
			Toast.makeText(this, "读取联系人权限被禁用，请重新开启读取联系人权限", Toast.LENGTH_SHORT).show();
			return result;
		}
		 ContentResolver resolver = getContentResolver();
		 Uri uri = Phone.CONTENT_URI;
		 Cursor cursor = null;
		 try {
			 cursor = resolver.query(uri, null, null, null, Phone.DISPLAY_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 if (null != cursor) {
			result = new ArrayList<LocalContact>();
			if (cursor.getColumnCount() <= 0) {
				return result;
			}
			Set set = new HashSet();
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				String phone = cursor.getString(cursor.getColumnIndex(Phone.DATA1));
				String narmolPhone = CommonUtils.normalizationPhoneNum(phone);
				LocalContact contactInfo = null;
				if (CommonUtils.matchMobilNo(narmolPhone) && set.add(narmolPhone)) {
					contactInfo = new LocalContact();
					String name = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
					String contact_id = cursor.getString(cursor.getColumnIndex(Phone.CONTACT_ID));
					String pinyin = PinYin.getPinYin(name);
					String initials = PinYin.getPinYinInitials(name);
					contactInfo.setName(name);
					contactInfo.setPhoneNumber(narmolPhone);
					contactInfo.setContactsIdString(contact_id);
					contactInfo.setPinyin(pinyin);
					contactInfo.setInitials(initials);
					String initial = pinyin;
					if (TextUtils.isEmpty(initial)) {
						initial = "";
					}else {
						initial = pinyin.substring(0, 1).toUpperCase();
					}
					if (!CommonUtils.isLetter(initial)) {
						initial = "#";
					}
					contactInfo.setInitial(initial);
					result.add(contactInfo);
					contactInfo = null;
				}
				cursor.moveToNext();
			}
			cursor.close();
			set = null;
			Collections.sort(result, new Comparator<LocalContact>() {

				@Override
				public int compare(LocalContact lhs, LocalContact rhs) {
					return lhs.getInitials().compareTo(rhs.getInitials());
				}
			});
		}
		 return result;
	}
}
