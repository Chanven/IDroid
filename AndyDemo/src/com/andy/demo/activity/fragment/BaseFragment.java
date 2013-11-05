package com.andy.demo.activity.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andy.android.util.DLog;

public class BaseFragment extends Fragment {

	public BaseFragment() {
		super();
	}

	@Override
	public void onAttach(Activity activity) {
		DLog.i(this.getClass().getSimpleName(), "onAttach");
		super.onAttach(activity);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		DLog.i(this.getClass().getSimpleName(), "onCreate");
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		DLog.i(this.getClass().getSimpleName(), "onCreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		DLog.i(this.getClass().getSimpleName(), "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		DLog.i(this.getClass().getSimpleName(), "onResume");
		super.onResume();
	}
	
	@Override
	public void onStart() {
		DLog.i(this.getClass().getSimpleName(), "onStart");
		super.onStart();
	}
	
	@Override
	public void onPause() {
		DLog.i(this.getClass().getSimpleName(), "onPause");
		super.onPause();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		DLog.i(this.getClass().getSimpleName(), "onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onStop() {
		DLog.i(this.getClass().getSimpleName(), "onStop");
		super.onStop();
	}
	
	@Override
	public void onDestroyView() {
		DLog.i(this.getClass().getSimpleName(), "onDestroyView");
		super.onDestroyView();
	}
	
	@Override
	public void onDestroy() {
		DLog.i(this.getClass().getSimpleName(), "onDestroy");
		super.onDestroy();
	}
	
	@Override
	public void onDetach() {
		DLog.i(this.getClass().getSimpleName(), "onDetach");
		super.onDetach();
	}
	
}
