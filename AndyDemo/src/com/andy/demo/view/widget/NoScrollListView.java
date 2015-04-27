package com.andy.demo.view.widget;

import android.widget.ListView;

/**
 * 不滚动ScrollListView，可用于ScrollView中的ListView
 * @author Chanven
 *
 */
public class NoScrollListView extends ListView {
    
    public NoScrollListView(android.content.Context context, android.util.AttributeSet attrs){
        super(context, attrs);
    }

    /**
     * 设置不滚动
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
