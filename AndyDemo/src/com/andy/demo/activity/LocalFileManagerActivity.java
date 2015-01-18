package com.andy.demo.activity;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.andy.demo.R;
import com.andy.demo.view.LocalFileAdapter;

public class LocalFileManagerActivity  extends Activity{
    ListView mListView;
    
    private int scanCode = 1;
	private int makeCode = 2;
    
    private ArrayList<String> items;
    private ArrayList<String> paths;
    private String rootpath = Environment.getExternalStorageDirectory()+"/";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_file_manager_layout);
        initView();
        getFileDir(rootpath);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuItem scan = menu.add(Menu.NONE, scanCode, Menu.NONE, "扫一扫");
    	scan.setIcon(R.drawable.icon);
		MenuItem make = menu.add(Menu.NONE, makeCode, Menu.NONE, "二维码生成");
		setIconVisible(menu, true);
    	return super.onCreateOptionsMenu(menu);
    }
    
    /**利用反射将mOptionalIconsVisible设置为true，图标可见*/
	private void setIconVisible(Menu menu, boolean visible) {
		try {
			Class<?> clazz = Class
					.forName("com.android.internal.view.menu.MenuBuilder");
			Method m = clazz.getDeclaredMethod("setOptionalIconsVisible",
					boolean.class);
			m.setAccessible(true);

			// MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
			m.invoke(menu, visible);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}  
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Toast.makeText(this, "Selected Item: " + item.getTitle(), Toast.LENGTH_SHORT).show();
		return super.onOptionsItemSelected(item);
	}
    
    private void initView() {
        mListView = (ListView) this.findViewById(R.id.local_file_manager_lv);
    }
    
    private void getFileDir(String path){
        items = new ArrayList<String>();
        paths = new ArrayList<String>();
        
        File presentFile = new File(path);
        File[] files = presentFile.listFiles();
        
        /*if (! path.equals(rootpath)) {
            items.add("back to/");
            paths.add(rootpath);
            
            items.add("back previos");
            paths.add(presentFile.getParent());
        }*/
        
        for (File f:files) {
            items.add(f.getName());
            paths.add(f.getPath());
        }
        mListView.setAdapter(new LocalFileAdapter(LocalFileManagerActivity.this,items,paths));
    }

    
}
