package com.andy.demo.activity;

import com.andy.demo.R;
import com.andy.demo.adapter.MyGroupAdapter;
import com.andy.demo.view.widget.PinnedExpandableListView;

import android.os.Bundle;

public class MyGroupActivity extends BaseActivity{
    private PinnedExpandableListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_group_layout);
        initView();
    }
    
    private void initView() {
        mListView = findView(R.id.my_group_lv);
        mListView.setHeaderView(getLayoutInflater().inflate(R.layout.my_group_list_group_view, null));
        mListView.setGroupIndicator(null);
        MyGroupAdapter mAdapter = new MyGroupAdapter(this, mListView);
        mListView.setAdapter(mAdapter);
        mListView.expandGroup(0);
        mAdapter.onHeadViewClick(0, 1);
    }
}
