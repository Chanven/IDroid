package com.andy.demo.netapi;

import com.andy.demo.netapi.param.BasicServiceParams;

public interface XService<ServParam extends BasicServiceParams> {
	public void getParams(ServParam outParams);
	public void commitParams(ServParam params);
	public void resetParams(ServParam params);
	public void abortService();
	public boolean isAborted();
}
