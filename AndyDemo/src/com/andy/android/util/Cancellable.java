package com.andy.android.util;

public interface Cancellable {
	void cancel();
	boolean isCancelled();
}
