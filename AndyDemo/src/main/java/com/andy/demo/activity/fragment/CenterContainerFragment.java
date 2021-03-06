package com.andy.demo.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.andy.demo.R;
import com.andy.demo.activity.BaseActivity;
import com.andy.demo.activity.FragmentWithTabActivity;
import com.andy.demo.activity.LocalFileManagerActivity;
import com.andy.demo.activity.MusicPlayActivity;
import com.andy.demo.activity.MyContactAcitivy;
import com.andy.demo.activity.MyGroupActivity;
import com.andy.demo.activity.PinnedSectionListActivity;
import com.andy.demo.activity.ShareTestActivity;
import com.andy.demo.activity.SimpleViewTestActivity;
import com.andy.demo.activity.SyncTestActivity;
import com.andy.demo.slidingmenu.app.SlidingFragmentActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class CenterContainerFragment extends DrawerChildViewFragment{
	private BaseActivity mContext;
	private SlidingMenu mSlidingMenu;

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
		
		return contentView;
	}
	
	private void initView(View container) {
		container.findViewById(R.id.share_test).setOnClickListener(mOnClickListener);
		container.findViewById(R.id.sync_test).setOnClickListener(mOnClickListener);
		container.findViewById(R.id.simple_view_test).setOnClickListener(mOnClickListener);
		container.findViewById(R.id.fragment_viewpager_tab_btn).setOnClickListener(mOnClickListener);
		container.findViewById(R.id.local_file_manager_btn).setOnClickListener(mOnClickListener);
		container.findViewById(R.id.music_play_btn).setOnClickListener(mOnClickListener);
		container.findViewById(R.id.pinned_expandable_listview_btn).setOnClickListener(mOnClickListener);
		container.findViewById(R.id.pinned_section_listview_btn).setOnClickListener(mOnClickListener);
		container.findViewById(R.id.local_contacts_btn).setOnClickListener(mOnClickListener);
	}
	
	OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.share_test:
			    mContext.startActivity(new Intent(mContext,ShareTestActivity.class));
				break;
			case R.id.sync_test:
				mContext.startActivity(new Intent(mContext,SyncTestActivity.class));
				break;
			case R.id.simple_view_test:
			    mContext.startActivity(new Intent(mContext,SimpleViewTestActivity.class));
			    break;
			case R.id.fragment_viewpager_tab_btn:
			    mContext.startActivity(new Intent(mContext,FragmentWithTabActivity.class));
			    break;
			case R.id.local_file_manager_btn:
			    mContext.startActivity(new Intent(mContext,LocalFileManagerActivity.class));
			    break;
			case R.id.music_play_btn:
			    mContext.startActivity(new Intent(mContext,MusicPlayActivity.class));
			    break;
			case R.id.pinned_expandable_listview_btn:
			    mContext.startActivity(new Intent(mContext,MyGroupActivity.class));
			    break;
			case R.id.pinned_section_listview_btn:
			    mContext.startActivity(new Intent(mContext,PinnedSectionListActivity.class));
			    break;
			case R.id.local_contacts_btn:
				mContext.startActivity(new Intent(mContext,MyContactAcitivy.class));
				break;
			default:
				break;
			}
		}
	};
	
	
}
