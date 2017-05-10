package com.andy.demo.view.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.andy.demo.R;
import com.andy.demo.utils.ScreenUtils;

public class ContactSideBar extends View {
    private Context mContext;
    private boolean mSimple = false;
    private char[] pinyin;
    private char[] pinyinSimple;
    private char[] chars;
    private SectionIndexer sectionIndexter = null;
    private ListView list;
    private TextView mDialogText;
    Bitmap bitMap;
    private Paint paint;
    private float m_nItemHeight;

    private int curIndex = -1;

    public static int headerCount = 0;

    public ContactSideBar(Context context) {
        super(context);
        init(context);
    }

    public ContactSideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setFocusable(true);
        setFocusableInTouchMode(true);

        mContext = context;
        pinyin = new char[]{'#', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
                'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        pinyinSimple = new char[]{'#', 'A', 'I', 'J', 'R', 'S', 'Z'};
    }

    public ContactSideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void setListView(ListView _list) {
        if (null == _list) {
            return;
        }
        list = _list;
        SetSectionIndexter(_list);
    }

    public void SetSectionIndexter(ListView listView) {
        if (listView.getHeaderViewsCount() > 0 || listView.getFooterViewsCount() > 0) {
            HeaderViewListAdapter ha = (HeaderViewListAdapter) listView.getAdapter();
            sectionIndexter = (SectionIndexer) ha.getWrappedAdapter();
        } else {
            sectionIndexter = (SectionIndexer) listView.getAdapter();
        }
    }

    public void setTextView(TextView mDialogText) {
        this.mDialogText = mDialogText;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean searchIndex = false;
        if (mSimple) {
            chars = pinyinSimple;
        } else {
            chars = pinyin;
        }
        int i = (int) event.getY();
        if (i < ScreenUtils.dip2px(mContext, 16)) {
            searchIndex = true;
            i = 0;
        } else {
            i = i - ScreenUtils.dip2px(mContext, /* 16 */0);
        }
        int idx = (int) (i / m_nItemHeight);
        if (idx >= chars.length) {
            idx = chars.length - 1;
        } else if (idx < 0) {
            idx = 0;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            super.setPressed(true);
            if (searchIndex) {
                list.setSelection(0);
                mDialogText.setVisibility(View.INVISIBLE);
                curIndex = -1;
//				invalidate();
            } else {
                curIndex = idx;
//				invalidate();
                mDialogText.setText("" + chars[idx]);
                mDialogText.setVisibility(View.VISIBLE);
                if (sectionIndexter == null) {
                    SetSectionIndexter(list);
                }
                int position = sectionIndexter.getPositionForSection(chars[idx]);
                if (position == -1) {
                    return true;
                }
                list.setSelection(position + headerCount);
            }
        } else {
            super.setPressed(false);
            mDialogText.setVisibility(View.INVISIBLE);
            curIndex = -1;
//			invalidate();
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitMap == null) {
            bitMap = BitmapFactory.decodeResource(getResources(), R.drawable.contact_charlist_btn);
        }
        int marginTopAndBottom = 15;
        int sideBarHeight = getHeight();
        int searchBtnHeight = /* AndroidUtil.dip2px(mContext, 22) */0; // 搜索图标高度
        int sideBar = sideBarHeight - searchBtnHeight - marginTopAndBottom * 2;
        // 获取被点击时背景图片的宽度和高度
        int bitMapWid = bitMap.getWidth();
        int bitMapHei = bitMap.getHeight();
        // 平均每个字母所在区域的高度
        m_nItemHeight = sideBar * 1.0f / pinyin.length;
        // 用于计算背景图片出现时的垂直位置
        float bitMapTop = 0;
        // 设置画笔
        if (paint == null) {
            paint = new Paint();
            // paint.setColor(0xff595c61);
            paint.setColor(getResources().getColor(R.color.txt_gray));
            paint.setTextSize(getResources().getDimension(R.dimen.contact_side_letter_size));
            paint.setAntiAlias(true);
            paint.setTextAlign(Paint.Align.CENTER);
        }
        // 用于计算背景图片和字母水平的绘制位置
        float widthCenter = getWidth() * 1.0f / 2;
        float bitWidthCenter = (getWidth() - bitMapWid) * 1.0f / 2;
        // 计算文字高度的对象
        FontMetrics fm = paint.getFontMetrics();
        // 计算字体高度
        float fontHeight = fm.bottom - fm.top;
        if (m_nItemHeight < fontHeight) {
            mSimple = true;
            chars = pinyinSimple;
            m_nItemHeight = sideBar * 1.0f / pinyinSimple.length;
        } else {
            mSimple = false;
            chars = pinyin;
        }
        // Bitmap searchBit = BitmapFactory.decodeResource(getResources(),
        // R.drawable.ticket_present_contact_search_icon);
        // canvas.drawBitmap(searchBit, bitWidthCenter,
        // (searchBtnHeight-searchBit.getHeight())/2, paint);
        for (int i = 0; i < chars.length; i++) {
            if (curIndex == i) {/*
                if (m_nItemHeight > bitMapHei) {
					bitMapTop = (m_nItemHeight - bitMapHei) / 2;
				} else {
					bitMapTop = 0;
				}
				canvas.drawBitmap(bitMap, bitWidthCenter, i * m_nItemHeight + bitMapTop + searchBtnHeight,
						paint);
			*/
            }
            // 计算绘制字母的y坐标
            float textBaseY = marginTopAndBottom + (i + 1) * m_nItemHeight - (m_nItemHeight - fontHeight) * 0.5f - fm.bottom;
            canvas.drawText(String.valueOf(chars[i]), widthCenter, textBaseY + searchBtnHeight, paint);
        }
    }

}
