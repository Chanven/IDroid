package com.andy.demo.activity.fragment;


import com.andy.demo.ui.IDrawerViewGroup;

import android.view.LayoutInflater;
import android.view.ViewGroup;

public abstract class DrawerChildViewFragment extends BaseFragment implements IDrawerViewGroup
{
	protected IDrawerViewGroup drawViewGroup = null;
	private ViewGroup mContainer;
	
	public DrawerChildViewFragment()
	{
	}
	
	public DrawerChildViewFragment(IDrawerViewGroup drawerViewGroup)
	{
		this.drawViewGroup = drawerViewGroup;
	}
	
	public DrawerChildViewFragment(IDrawerViewGroup drawerViewGroup, ViewGroup container)
	{
		this.drawViewGroup = drawerViewGroup;
		this.mContainer = container;
	}
	
	public DrawerChildViewFragment(IDrawerViewGroup drawerViewGroup, LayoutInflater inflater)
	{
		this.drawViewGroup = drawerViewGroup;
		this.mContainer = createContainerView(inflater);
	}

	/**
	 * 取对应的视图View
	 * @return
	 */
	public ViewGroup getContainerView(){
		return mContainer;
	}
	
	protected abstract ViewGroup createContainerView(LayoutInflater inflater);
	
	@Override
	public void openLeftContent(OnDrawerStateChangedListener listener)
	{
		if (drawViewGroup != null) {
			drawViewGroup.openLeftContent(listener);
		}
	}

	@Override
	public void closeLeftContent(OnDrawerStateChangedListener listener)
	{
		if (drawViewGroup != null) {
			drawViewGroup.closeLeftContent(listener);
		}
	}

	@Override
	public void openRightContent(OnDrawerStateChangedListener listener)
	{
		if (drawViewGroup != null) {
			drawViewGroup.openRightContent(listener);
		}
	}

	@Override
	public void closeRightContent(OnDrawerStateChangedListener listener)
	{
		if (drawViewGroup != null) {
			drawViewGroup.closeRightContent(listener);
		}
	}

	@Override
	public boolean isLeftOpen()
	{
		return (drawViewGroup != null) ? drawViewGroup.isLeftOpen() : false;
	}

	@Override
	public boolean isRightOpen()
	{
		return (drawViewGroup != null) ? drawViewGroup.isRightOpen() : false;
	}
}
