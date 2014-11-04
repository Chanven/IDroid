package com.andy.demo.netapi.util;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.andy.android.util.DLog;
import com.andy.demo.netapi.ConstantConfig;

public final class FlowAccumulatorInputStream extends FilterInputStream {
	
	private static final String RESPONSE_RECV_TAG = "__<<";
	private ByteArrayOutputStream mTraceBuffer;
	private boolean mbDumpContent = true;
	
	//public String getLog

	public FlowAccumulatorInputStream(InputStream in) {
		super(in);
		if(mbDumpContent && ConstantConfig.DEBUG) {
			mTraceBuffer = new ByteArrayOutputStream(1024);
		}
	}
	
	public FlowAccumulatorInputStream(InputStream in, boolean bDumpContent) {
		super(in);
		mbDumpContent = bDumpContent;
		if(mbDumpContent && ConstantConfig.DEBUG) {
			mTraceBuffer = new ByteArrayOutputStream(1024);
		}
	}
	

	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#read()
	 */
	@Override
	public int read() throws IOException {
		int num = super.read();
		if(num >= 0) {
			if(mbDumpContent && mTraceBuffer != null) {
				mTraceBuffer.write(num);
			}
		} else {
			dump();
		}
		return num;
	}

	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] buffer, int offset, int count) throws IOException {
		int num = super.read(buffer, offset, count);
		if(num > 0) {
			if(mbDumpContent && mTraceBuffer != null) {
				mTraceBuffer.write(buffer, 0, num);
			}
		} else {
			dump();
		}
		return num;
	}

	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#read(byte[])
	 */
	@Override
	public int read(byte[] buffer) throws IOException {
		return read(buffer, 0 ,buffer.length);
	}

	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#skip(long)
	 */
	@Override
	public long skip(long byteCount) throws IOException {
		long num = super.skip(byteCount);
		return num;
	}
	
	void dump() {
		if(mbDumpContent && mTraceBuffer != null) {
			try {
				DLog.d(RESPONSE_RECV_TAG, mTraceBuffer.toString("UTF-8"));
				DLog.write2File("http response content", mTraceBuffer.toString("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mTraceBuffer = null;
		}
	}
}
