package com.andy.demo.activity;

import java.io.File;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Environment;
import android.widget.ListView;

import com.andy.demo.R;
import com.andy.demo.view.LocalFileAdapter;

public class LocalFileManagerActivity  extends BaseActivity{
    ListView mListView;
    
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
    
    private void initView() {
        mListView = findView(R.id.local_file_manager_lv);
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
