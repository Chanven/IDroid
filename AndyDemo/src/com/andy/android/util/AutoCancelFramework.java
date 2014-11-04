package com.andy.android.util;


public abstract class AutoCancelFramework<Params, Progress, Result> extends
		AsyncFramework<Params, Progress, Result> {
	
	public AutoCancelFramework(AutoCancelController autoCancelController) {
		super();
		mAutoCancelController = autoCancelController;
	}

	@Override
	protected void finish(Result result) {
		if(mAutoCancelController != null) {
			mAutoCancelController.remove(this);
			mAutoCancelController = null;
		}
		super.finish(result);
	}
	protected AutoCancelController mAutoCancelController;
}
