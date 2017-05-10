package com.andy.demo.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.andy.demo.R;

/**
 * 统一加载框
 *
 * @author Chanven
 * @date 2015-10-20
 */
public class UniLoadingDialog extends Dialog {
    private Context mContext;
    private LayoutInflater mInflater;
    private TextView mLoadingTv;

    public UniLoadingDialog(Context context) {
        super(context, R.style.uni_loading_dialog_style);
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        init();
    }

    private void init() {
        View view = mInflater.inflate(R.layout.uni_loading_dialog_layout, null);
        mLoadingTv = (TextView) view.findViewById(R.id.uni_loading_dialog_msg_tv);
        setContentView(view);
        setCanceledOnTouchOutside(false);
    }

    public void setMessage(String msg) {
        mLoadingTv.setText(msg);
    }

}
