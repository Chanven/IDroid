package com.andy.demo.view;

import java.io.File;
import java.util.List;

import com.andy.demo.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LocalFileAdapter  extends BaseAdapter{
    
    private List<String> items;
    private List<String> paths;
    private LayoutInflater inflater;
    private Bitmap folderIcon;
    private Bitmap fileIcon;
    
    public LocalFileAdapter(Context context,List<String> items,List<String> paths){
        inflater = LayoutInflater.from(context);
        this.items = items;
        this.paths = paths;
        folderIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_folder);
        fileIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_file);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.local_file_list_item, null);
            holder = new ViewHolder();
            holder.name=(TextView)convertView.findViewById(R.id.local_file_list_item_name);
            holder.icon=(ImageView)convertView.findViewById(R.id.local_file_list_item_icon);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        File f = new File(paths.get(position));
        if(f.isDirectory()){
            holder.icon.setImageBitmap(folderIcon);
        }else {
                holder.icon.setImageBitmap(fileIcon);
            }
        holder.name.setText(items.get(position));
        return convertView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    private  class ViewHolder{
        TextView name;
        ImageView icon;
    }


}
