package com.andy.demo.netapi.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class FlowAccumulatorOutputStream extends FilterOutputStream {

	public FlowAccumulatorOutputStream(OutputStream out) {
		super(out);
	}

	/* (non-Javadoc)
	 * @see java.io.FilterOutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] buffer, int offset, int length) throws IOException {
		super.write(buffer, offset, length);
	}

	/* (non-Javadoc)
	 * @see java.io.FilterOutputStream#write(byte[])
	 */
	@Override
	public void write(byte[] buffer) throws IOException {
		super.write(buffer);
	}

	/* (non-Javadoc)
	 * @see java.io.FilterOutputStream#write(int)
	 */
	@Override
	public void write(int oneByte) throws IOException {
		super.write(oneByte);
	}
}
