package com.andy.demo.view;

import java.io.File;
import java.util.List;

import com.andy.demo.R;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LocalFileAdapter extends SimpleBaseAdapter<File>{

    public LocalFileAdapter(Context context, List<File> data){
        super(context, data);
    }

    @Override
    public int getItemResource() {
        return R.layout.local_file_list_item;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        TextView name = holder.getChildView(R.id.local_file_list_item_name);
        ImageView icon = holder.getChildView(R.id.local_file_list_item_icon);
        File file = (File) getItem(position);
        if (file.isDirectory()) {
            icon.setImageResource(R.drawable.icon_folder);
        }else {
            icon.setImageResource(R.drawable.icon_file);
        }
        name.setText(file.getName());
        return convertView;
    }

}
