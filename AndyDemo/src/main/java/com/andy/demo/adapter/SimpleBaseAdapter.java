package com.andy.demo.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseAdapter基类
 *
 * @param <T>
 * @author guoch
 */
public abstract class SimpleBaseAdapter<T> extends BaseAdapter {
    protected List<T> mData;
    protected Context mContext;

    public SimpleBaseAdapter(Context context, List<T> data) {
        this.mContext = context;
        this.mData = data == null ? new ArrayList<T>() : data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < mData.size()) {
            return mData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 该方法需要子类实现，需要返回item布局的resource layout id
     *
     * @return id
     */
    public abstract int getItemResource();

    /**
     * 使用该getItemView()方法替换原来的getView()方法，需要子类实现
     *
     * @param position
     * @param convertView
     * @param viewHolder
     * @return
     */
    public abstract View getItemView(int position, View convertView, ViewHolder holder);

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = View.inflate(mContext, getItemResource(), null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return getItemView(position, convertView, holder);
    }

    public void addAll(List<T> elem) {
        mData.addAll(elem);
        notifyDataSetChanged();
    }

    public void add(T elem) {
        mData.add(elem);
        notifyDataSetChanged();
    }

    public void remove(T elem) {
        mData.remove(elem);
        notifyDataSetChanged();
    }

    public void remove(int index) {
        mData.remove(index);
        notifyDataSetChanged();
    }

    public void replaceAll(List<T> elem) {
        mData.clear();
        mData.addAll(elem);
        notifyDataSetChanged();
    }

    public class ViewHolder {
        private SparseArray<View> viewHolder = new SparseArray<View>();
        private View convertView;

        public ViewHolder(View view) {
            this.convertView = view;
        }

        @SuppressWarnings("unchecked")
        public <T extends View> T getChildView(int resId) {
            View childView = viewHolder.get(resId);
            if (null == childView) {
                childView = convertView.findViewById(resId);
                viewHolder.put(resId, childView);
            }
            return (T) childView;
        }
    }

}
