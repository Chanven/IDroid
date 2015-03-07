package com.andy.demo.utils;

import java.io.ByteArrayOutputStream;

import com.andy.demo.base.Constant;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.view.View;
import android.view.View.MeasureSpec;

public class ImageUtils {

	public static byte[] bmpToByteArray(final Bitmap bmp,
			final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/** view转bitmap */
	public Bitmap convertViewToBitmap(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(Constant.m_screenW,
				MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0,
				MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}
}
