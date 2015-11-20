package com.andy.demo.view.widget;

import com.andy.demo.R;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;

/***
 * 自定义字体
 * @author Chanven
 *
 */
public class FontTextView extends TextView{

    public FontTextView(Context context){
        super(context);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public FontTextView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context, attrs);
    }
    
    private void init(Context context, AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FontTextView);
        CharSequence font = typedArray.getText(R.styleable.FontTextView_font);
        if (!TextUtils.isEmpty(font)) {
            Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/" + font + ".ttf");
            this.setTypeface(tf);
        }
        typedArray.recycle();
    }
    
    private long totalNum;
    private long currentNum;
    private FontTextAnimation animation = null;
    private boolean isAnimationRunning = false;
    
    /**
     * 带数字文本及后缀动画显示
     * @param num 数字文本
     * @param isAnimation  是否需要动画
     * @param suffix 需要带的后缀
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public void setText(long num, boolean isAnimation, String suffix) {
        String currentText = getText().toString();
        currentNum = 0;
        if (!TextUtils.isEmpty(currentText)&& isAnimationRunning) {
            try {
                currentNum = Integer.parseInt(currentText);
            } catch (NumberFormatException e) {

            }
        }
        totalNum = num;
        String suffix_ = "";
        if (null != suffix) {
            suffix_ = suffix;
        }
        if (!isAnimation) {
            super.setText(totalNum + suffix_);
        } else {
            if (animation != null && isAnimationRunning) {
                animation.cancel();
            }
            long duration = 1000;
            if (Math.abs(totalNum - currentNum) < 50) {
                duration = duration * Math.abs(totalNum - currentNum) / 50;
            }
            animation = new FontTextAnimation(suffix_);
            animation.setDuration(duration);
            startAnimation(animation);
            
            animation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    isAnimationRunning =true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isAnimationRunning = false;
                }
            });
        }
    }
    
    class FontTextAnimation extends Animation{
        /**后缀*/
        private String suffix = "";
        
        public FontTextAnimation(String suffix){
            if (null != suffix) {
                this.suffix = suffix;
            }
        }
        
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            String text =(long) (currentNum + (totalNum - currentNum)*interpolatedTime) + suffix;
            setText(text);
            super.applyTransformation(interpolatedTime, t);
        }
    }
}
