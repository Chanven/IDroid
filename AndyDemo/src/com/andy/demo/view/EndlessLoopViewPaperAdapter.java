package com.andy.demo.view;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.andy.demo.R;
import com.andy.demo.view.widget.ImageIndicator;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class EndlessLoopViewPaperAdapter extends PagerAdapter 
		implements ViewPager.OnPageChangeListener{
    private Context mContext; // 上下文
    /** 物料图片数据源缓存 */
    private List<String> drawablesTemp;
    /** 初始物料图片数据源 */
    private List<String> mList;
    /** 发散后物料图片数据源 */
    private LinkedList<String> mLists;
    /** 数据发散倍数 */
    private final int MaxLen = 10000;
    /** 滑动viewpager */
    private ViewPager mViewPager;
    /** 滑动viewpager指示器 */
    private ImageIndicator mIndicator;
    /** 加倍标志 */
    private boolean mDoubleFlag = false;
    /** 物料滑动定时器 */
    private Runnable viewpagerRunnable;
    /** 物料滑动定时处理器 */
    private Handler handler = new Handler();
    /** 物料定时滑动时间间隙 */
    private static final int TIME = 4000;
    private boolean toRunning = true;
	
	public EndlessLoopViewPaperAdapter(Context context, ViewPager viewPager,
			ImageIndicator indicator, List<String> list) {
		mContext = context;
		mIndicator = indicator;
		mViewPager = viewPager;
		mViewPager.setOnPageChangeListener(this);
		setData(list);
	}

	/** 设置滑动物料数据 */
	private void setData(List<String> list) {
		// 提取图片信息集
		if (drawablesTemp == null) {
			drawablesTemp = new ArrayList<String>();
		} else {
			drawablesTemp.clear();
		}
		if (list != null) {
			mIndicator.setCount(list.size());// 设置指示器总数
			for (int i = 0; i < list.size(); i++) {
				// 获取物料地址
				drawablesTemp.add(list.get(i));
			}
			if (list.size() > 1 && list.size() < 4) {
				// 物料地址加倍
				for (int i = 0; i < list.size(); i++) {
					drawablesTemp.add(list.get(i));
				}
				mDoubleFlag = true;
			}
		}
		// 处理图片信息集
		if (drawablesTemp != null && drawablesTemp.size() == 0) {
			drawablesTemp.add("none");
		}
		mList = drawablesTemp;
		mLists = new LinkedList<String>();
		if (drawablesTemp != null && drawablesTemp.size() > 0) {
			for (String string : drawablesTemp) {
				mLists.add(string);
			}
		}
		if (drawablesTemp.size() > 1) {
			// 开启自动切换图片
			initRunnable();
		}
	}

	/** 设置新的滑动物料数据集 */
	public void setNewData(List<String> list) {
		mDoubleFlag = false;
		setData(list);
		this.notifyDataSetChanged();
	}

	/** 定时切换 */
	protected void initRunnable() {
		setHadSlided(false);
		if (viewpagerRunnable != null) {
			handler.removeCallbacks(viewpagerRunnable);
		}
		viewpagerRunnable = new Runnable() {
			@Override
			public void run() {
				if (toRunning) {
					if (!getHadSlided()) {
						int nowIndex = mViewPager.getCurrentItem();
						int count = mViewPager.getAdapter().getCount();
						// 如果下一张的索引大于最后一张，则切换到第一张
						if (nowIndex + 1 >= count) {
							setCurrentItem();
						} else {
							mViewPager.setCurrentItem(nowIndex + 1);
						}
					}
				}
				handler.postDelayed(viewpagerRunnable, TIME);
			}
		};
		handler.postDelayed(viewpagerRunnable, TIME);
	}

	public void destroyRunnable() {
		if (handler != null && viewpagerRunnable != null) {
			toRunning = false;
			handler.removeCallbacks(viewpagerRunnable);
		}
	}

	public void reStartRunnable() {
		if (handler != null && viewpagerRunnable != null) {
			toRunning = true;
			handler.postDelayed(viewpagerRunnable, TIME);
		}
	}

	/** 设置viewpaper到中间位置 */
	public void setCurrentItem() {
		if (mLists != null) {
			if (mLists.size() > 1) {
				mViewPager.setCurrentItem(mLists.size() * MaxLen / 2, false);
			}
		}
	}

	@Override
	public int getCount() {
		if (mLists != null) {
			if (mLists.size() > 1) {
				return mLists.size() * MaxLen;
			} else {
				return mLists.size();
			}
		}
		return 0;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		String url;
		if (mDoubleFlag) {
			url = mList.get(position % (mLists.size() / 2));
		} else {
			url = mList.get(position % mLists.size());
		}

		View view = LayoutInflater.from(mContext).inflate(
				R.layout.endless_viewpager_item, null);
		ImageView imageView = (ImageView) view
				.findViewById(R.id.viewpager_item_iv);

		if (mList.size() == 1 && mList.get(0) != null
				&& "none".equals(mList.get(0))) {
			// 没有数据时显示
			imageView.setBackgroundResource(R.drawable.icon);
		} else {
			imageView.setBackgroundResource(Integer.valueOf(url));
			if (mDoubleFlag) {
				imageView.setOnClickListener(new ItemOnclickListen(position
						% (mLists.size() / 2)));
			} else {
				imageView.setOnClickListener(new ItemOnclickListen(position
						% mLists.size()));
			}
		}
		container.addView(view, 0);

		return view;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	// 实现ViewPager.OnPageChangeListener接口
	@Override
	public void onPageSelected(int position) {
		int mPosition;
		if (mDoubleFlag) {
			mPosition = position % (mLists.size() / 2);
		} else {
			mPosition = position % mLists.size();
		}
		mIndicator.setSeletion(mPosition);
	}

	private boolean slidedAble = false;

	private boolean hadSlided = false;

	public boolean getHadSlided() {
		return hadSlided;
	}

	public void setHadSlided(boolean slideState) {
		hadSlided = slideState;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		System.out.println("" + positionOffset + slidedAble);
		if (0.01 < positionOffset && positionOffset < 0.99) {
			if (slidedAble) {
				setHadSlided(true);
			}
		} else {
			slidedAble = true;
			setHadSlided(false);
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	private class ItemOnclickListen implements OnClickListener {
		private int mPosition;

		public ItemOnclickListen(int position) {
			mPosition = position;
		}

		@Override
		public void onClick(View v) {
			Toast.makeText(mContext, "" + mPosition, Toast.LENGTH_LONG).show();
		}
	}
	
}