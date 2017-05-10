package com.andy.demo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.andy.demo.R;
import com.andy.demo.adapter.LocalFileAdapter;
import com.andy.demo.zxing.activity.CaptureActivity;
import com.andy.demo.zxing.activity.CreateQrCodeActivity;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 本地文件管理
 */
public class LocalFileManagerActivity extends Activity {
    ListView mListView;

    private static final int SCAN_CODE = 1;
    private static final int CREATE_CODE = 2;

    private String rootpath = Environment.getExternalStorageDirectory() + "/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_file_manager_layout);
        initView();
        getFileDir(rootpath);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem scan = menu.add(Menu.NONE, SCAN_CODE, Menu.NONE, "扫一扫");
        scan.setIcon(R.drawable.icon);
        MenuItem create = menu.add(Menu.NONE, CREATE_CODE, Menu.NONE, "二维码生成");
        setIconVisible(menu, true);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 利用反射将mOptionalIconsVisible设置为true，图标可见
     */
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
        switch (item.getItemId()) {
            case SCAN_CODE:
                LocalFileManagerActivity.this.startActivity(new Intent(LocalFileManagerActivity.this,
                        CaptureActivity.class));
                break;
            case CREATE_CODE:
                LocalFileManagerActivity.this.startActivity(new Intent(LocalFileManagerActivity.this,
                        CreateQrCodeActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mListView = (ListView) this.findViewById(R.id.local_file_manager_lv);
    }

    private void getFileDir(String path) {
        File presentFile = new File(path);
        List<File> files = new ArrayList<File>();
        
        /*if (! path.equals(rootpath)) {
            items.add("back to/");
            paths.add(rootpath);
            
            items.add("back previos");
            paths.add(presentFile.getParent());
        }*/

        for (File f : presentFile.listFiles()) {
            files.add(f);
        }
        mListView.setAdapter(new LocalFileAdapter(this, files));
    }


}
