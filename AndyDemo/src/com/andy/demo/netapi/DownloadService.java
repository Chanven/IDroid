package com.andy.demo.netapi;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CancellationException;

import com.andy.demo.netapi.exception.XResponseException;
import com.andy.demo.netapi.param.BasicServiceParams;

public interface DownloadService extends XService<BasicServiceParams> {
	public interface DownloadObserver {
		public void onConnected(DownloadService service);
		public void onProgress(DownloadService service, long completedBytes, long txRate);
	}
	
	public long download(String url, long bytesOffset, long bytesToDownload, OutputStream outStream, DownloadObserver observer) 
			throws XResponseException, IOException, CancellationException;

	public long getContentLength();

}
