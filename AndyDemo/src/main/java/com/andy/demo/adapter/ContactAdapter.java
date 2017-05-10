package com.andy.demo.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.andy.demo.R;
import com.andy.demo.activity.BaseActivity;
import com.andy.demo.analysis.bean.LocalContact;
import com.andy.demo.utils.ImageUtils;
import com.andy.demo.view.pinnedheader.SectionedBaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactAdapter extends SectionedBaseAdapter implements SectionIndexer {
    private BaseActivity mContext;

    private List<String> mGroups;

    private Map<String, List<LocalContact>> mMobileMap;

    /**
     * 搜索关键字字段
     */
    private String searchKey = null;

    Comparator<LocalContact> mobileComparator;

    public ContactAdapter(BaseActivity activity) {
        this.mContext = activity;
        mobileComparator = new Comparator<LocalContact>() {
            @Override
            public int compare(LocalContact lhs, LocalContact rhs) {
                if (lhs.getInitials().compareTo(lhs.getInitials()) < 0) {
                    return -1;
                } else {
                    return 1;
                }
            }

        };
    }

    /**
     * 设置列表数据
     *
     * @param infos 好友列表
     * @param key   搜索关键字字段内容
     */
    public void setContacts(List<LocalContact> infos, String key) {
        if (infos == null) {
            return;
        }
        if (null == mMobileMap) {
            mMobileMap = new HashMap<String, List<LocalContact>>();
        } else {
            mMobileMap.clear();
        }
        searchKey = key;
        // 每一组的联系人
        List<LocalContact> groupMobiles = null;
        String group = "";
        for (LocalContact grantMobileInfo : infos) {
            if (null == grantMobileInfo) {
                continue;
            }
            group = grantMobileInfo.getInitial();
            // 按首字母分组
            if (null == mMobileMap.get(group)) {
                groupMobiles = new ArrayList<LocalContact>();
            } else {
                groupMobiles = mMobileMap.get(group);
            }
            groupMobiles.add(grantMobileInfo);
            mMobileMap.put(group, groupMobiles);
        }
        if (null == mGroups) {
            mGroups = new ArrayList<String>(mMobileMap.keySet());
        } else {
            mGroups.clear();
            mGroups.addAll(mMobileMap.keySet());
        }

        Collections.sort(mGroups, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                if (lhs.compareTo(rhs) < 0) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int section, int position) {
        if (null == mGroups || mGroups.isEmpty() || null == mMobileMap || mMobileMap.isEmpty()) {
            return null;
        }
        return mMobileMap.get(mGroups.get(section)).get(position);
    }

    @Override
    public long getItemId(int section, int position) {
        return position;
    }

    @Override
    public int getSectionCount() {
        return (null == mGroups || mGroups.isEmpty()) ? 0 : mGroups.size();
    }

    @Override
    public int getCountForSection(int section) {
        if (null == mGroups || mGroups.isEmpty() || null == mMobileMap || mMobileMap.isEmpty()) {
            return 0;
        }
        return mMobileMap.get(mGroups.get(section)).size();
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        if (null == mContext) {
            return null;
        }
        ViewHolder holder = null;
        if (null != convertView) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mContext.getLayoutInflater().inflate(R.layout.listitem_contact, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        LocalContact info = (LocalContact) getItem(section, position);
        if (null == info) {
            return null;
        }
        String name = info.getName();
        if (!TextUtils.isEmpty(name)) {
            holder.name.setVisibility(View.VISIBLE);
            setMatchTextColor(holder.name, name, searchKey);
        } else {
            holder.name.setVisibility(View.GONE);
        }
        String moblie = info.getPhoneNumber();
        if (!TextUtils.isEmpty(moblie)) {
            setMatchTextColor(holder.num, moblie, searchKey);
        }
        if (info.isCheck()) {
            holder.cb.setChecked(true);
        } else {
            holder.cb.setChecked(false);
        }
        String contactIdString = info.getContactsIdString();
        if (!TextUtils.isEmpty(contactIdString)) {
            Bitmap imageBitmap = ImageUtils.getContactsImage(mContext, contactIdString);
            if (imageBitmap != null) {
                holder.icon.setImageBitmap(imageBitmap);
            } else {
                holder.icon.setImageResource(R.drawable.user_icon_default);
            }
        } else {
            holder.icon.setImageResource(R.drawable.user_icon_default);
        }
        holder.divider.setVisibility(View.VISIBLE);
        if (position == getCountForSection(section) - 1) {
            holder.divider.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        if (null == mContext) {
            return null;
        }
        if (null == mGroups || mGroups.isEmpty()) {
            return null;
        }
        String group = mGroups.get(section);
        if (TextUtils.isEmpty(group)) {
            group = "";
        }
        SectionViewHolder holder = null;
        if (null != convertView) {
            holder = (SectionViewHolder) convertView.getTag();
        } else {
            convertView = mContext.getLayoutInflater().inflate(R.layout.listitem_contact_header, null);
            holder = new SectionViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder.groupName.setText(group);
        return convertView;
    }

    /**
     * 设置匹配字段的颜色（红色）
     *
     * @param view
     * @param text
     * @param key
     */
    private void setMatchTextColor(TextView view, String text, String key) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (TextUtils.isEmpty(key)) {
            view.setText(text);
        } else {
            int start = text.indexOf(key);
            if (start >= 0) {
                SpannableStringBuilder style = new SpannableStringBuilder(text);
                style.setSpan(new ForegroundColorSpan(Color.RED), start, start + key.length(),
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                view.setText(style);
            } else {
                view.setText(text);
            }
        }
    }

    class ViewHolder {
        ImageView icon;
        TextView name;
        TextView num;
        CheckBox cb;
        View divider;

        ViewHolder(View view) {
            icon = (ImageView) view.findViewById(R.id.iv_listitem_contact);
            name = (TextView) view.findViewById(R.id.tv_listitem_contact_name);
            num = (TextView) view.findViewById(R.id.tv_listitem_contact_number);
            cb = (CheckBox) view.findViewById(R.id.cb_listitem_contact_select);
            divider = view.findViewById(R.id.view_listitem_contact);
        }
    }

    class SectionViewHolder {
        TextView groupName;

        SectionViewHolder(View view) {
            groupName = (TextView) view.findViewById(R.id.tv_listitem_contact_header_name);
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int section) {
        int index = 0;
        for (int i = 0; i < mGroups.size(); i++) {
            if (section == mGroups.get(i).charAt(0)) {
                for (int j = 0; j < i; j++) {
                    index += mMobileMap.get(mGroups.get(j)).size() + 1;
                }
                return index;
            }
        }
        return -1;
    }

}
