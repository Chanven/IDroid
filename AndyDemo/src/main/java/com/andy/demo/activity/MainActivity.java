package com.andy.demo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.andy.demo.R;
import com.andy.demo.activity.fragment.CenterContainerFragment;
import com.andy.demo.activity.fragment.LeftContainerFragment;
import com.andy.demo.activity.fragment.RightContainerFragment;
import com.andy.demo.slidingmenu.app.SlidingFragmentActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.CanvasTransformer;

public class MainActivity extends SlidingFragmentActivity {

    LeftContainerFragment mLeftFragment;
    RightContainerFragment mRightFragment;
    CenterContainerFragment mCenterFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragment(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initFragment(Bundle savedInstanceState) {
        //触摸模式（左右、是否全屏或者边框）、边距、淡入淡出参数等
        SlidingMenu sm = getSlidingMenu();
        sm.setMode(SlidingMenu.LEFT_RIGHT);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setFadeDegree(0.35f);
        sm.setTouchmodeMarginThreshold(10);
        sm.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN);

        //设置above动画部分，可取消
        CanvasTransformer transformer = new CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                float scale = (float) (1 - percentOpen * 0.15);
                float px = 0.0f;
                if (getSlidingMenu().getOpenedWidth() > 0) {
                    px = canvas.getWidth();
                } else if (getSlidingMenu().getOpenedWidth() < 0) {
                    px = 0;
                }
                canvas.scale(scale, scale, px, canvas.getHeight() / 2);
            }
        };
        sm.setAboveCanvasTransformer(transformer);

        //设置中间部分
        setContentView(R.layout.center_frame);
        if (savedInstanceState == null) {
            FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
            mCenterFragment = new CenterContainerFragment();
            t.replace(R.id.center_frame, mCenterFragment);
            t.commit();
        } else {
            mCenterFragment = (CenterContainerFragment) this.getSupportFragmentManager().findFragmentById(R.id
                    .center_frame);
        }

        // 设置左边部分
        setBehindContentView(R.layout.left_frame);
        sm.setShadowDrawable(R.drawable.shadow_left);
        if (savedInstanceState == null) {
            FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
            mLeftFragment = new LeftContainerFragment();
            t.replace(R.id.left_frame, mLeftFragment);
            t.commit();
        } else {
            mLeftFragment = (LeftContainerFragment) this.getSupportFragmentManager().findFragmentById(R.id.left_frame);
        }

        //设置右边部分
        sm.setSecondaryMenu(R.layout.right_frame);
        sm.setSecondaryShadowDrawable(R.drawable.shadow_right);
        if (savedInstanceState == null) {
            FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
            mRightFragment = new RightContainerFragment();
            t.replace(R.id.right_frame, mRightFragment);
            t.commit();
        } else {
            mRightFragment = (RightContainerFragment) this.getSupportFragmentManager().findFragmentById(R.id
					.right_frame);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getSlidingMenu().isMenuShowing()) {
                getSlidingMenu().showContent();
                return true;
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示");
                builder.setMessage("你确定退出吗？")
                        .setCancelable(false)
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        finish();
                                        System.exit(0);
                                    }
                                })
                        .setNegativeButton("返回",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
//			return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
