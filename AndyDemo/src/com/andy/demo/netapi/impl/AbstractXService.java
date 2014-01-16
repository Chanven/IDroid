package com.andy.demo.netapi.impl;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.concurrent.CancellationException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import com.andy.android.util.DLog;
import com.andy.demo.netapi.Session;
import com.andy.demo.netapi.XService;
import com.andy.demo.netapi.exception.XResponseException;
import com.andy.demo.netapi.param.BasicServiceParams;
import com.andy.demo.netapi.request.RestfulRequest;
import com.andy.demo.netapi.util.HelperUtil;

/**
 * @author ivankuo
 *
 */
public abstract class AbstractXService<ServParam extends BasicServiceParams> implements
		XService<ServParam> {
	
	AbstractXService() {
		reset();
	}

	@Override
	public void getParams(ServParam outParams) {
		mParams.applyTo(outParams);
	}

	@Override
	public void commitParams(ServParam params) {
		params.applyTo(mParams);
		applyServiceParams(params);
	}

	@Override
	public void resetParams(ServParam params) {
		mParams.clear();
		params.applyTo(mParams);
		applyServiceParams(mParams);
	}

	@Override
	public void abortService() {
		synchronized (this) {
			mbAborted = true;
			if(mCurRequest != null) {
				DLog.d(this.getClass().getSimpleName(), "Need to cancel current request:" + mCurRequest.toString());
				mCurRequest.cancel();
				mCurRequest = null;
			}
			if(mHttpClient != null) {
				DLog.d(this.getClass().getSimpleName(), "Shutdown connection!");
				mHttpClient.getConnectionManager().shutdown();
				// Don't set mHttpClient to null since it may be
				// still used in another thread.
			}
			if(mCurSocketFactory != null) {
				mCurSocketFactory.close();
				mCurSocketFactory = null;
			}
			if(mCurSSLSocketFactory != null) {
				mCurSSLSocketFactory.close();
				mCurSSLSocketFactory = null;
			}
		}
	}

	@Override
	public boolean isAborted() {
		return mbAborted;
	}
	
	protected void applyServiceParams(ServParam params) {
		HelperUtil.applyServiceParams(params, mHttpClient);
	}

	protected void reset() {
		if(mbAborted || mHttpClient == null) {
			HttpParams params = null;
//			HttpClient client;
			if(mHttpClient != null) {
				//client = mHttpClient;
				params = mHttpClient.getParams().copy();
			} else {
				//client = new DefaultHttpClient();
			}
			//HttpParams params = client.getParams();
			abortService();
			try {
				mCurSocketFactory = new ManagedSocketFactory(false);
				mCurSSLSocketFactory = new ManagedSocketFactory(true);
			} catch (KeyStoreException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (CertificateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (KeyManagementException e) {
				e.printStackTrace();
			} catch (UnrecoverableKeyException e) {
				e.printStackTrace();
			}
	        
			
			if(params != null) {
				mHttpClient = new DefaultHttpClient(params); //(new SingleClientConnManager(params, schreg), params);
			} else {
				mHttpClient = new DefaultHttpClient();
			}
			SchemeRegistry schreg = mHttpClient.getConnectionManager().getSchemeRegistry();
			if(mCurSocketFactory != null) {
				schreg.register(new Scheme("http", mCurSocketFactory.getFactory(), 80));
			}
			if(mCurSSLSocketFactory != null) {
				schreg.register(new Scheme("https", mCurSSLSocketFactory.getFactory(), 443));
			}
			mbAborted = false;
		}
	}
	
	protected <T> T send(RestfulRequest<T> request, Session session) 
			throws CancellationException, ClientProtocolException, XResponseException, IOException {
		try {
			synchronized (this) {
				if(mbAborted) {
					throw new CancellationException();
				}
				else if(mCurRequest != null) {
					throw new IllegalStateException("Another request is still executing!");
				}
				request.setHttpClient(mHttpClient);
				mCurRequest = request;
			}
			return request.send(session);
		} 
		catch(CancellationException e) {
			throw e;
		} 
		catch(XResponseException e) {
			if(mbAborted) {
				throw new CancellationException();
			} else {
				throw e;
			}
		}
		catch(SocketTimeoutException e) {
			if(mbAborted) {
				throw new CancellationException();
			} else {
				throw e;
			}
		}
		catch(IOException e) {
			HttpClient httpClient = mHttpClient;
			synchronized (this) {
				mHttpClient = null;
				reset();
				DLog.w(this.getClass().getSimpleName(), "Connection need reset!");
			}
			if(mbAborted) {
				throw new CancellationException();
			} else {
				httpClient.getConnectionManager().shutdown();
				throw e;
			}	
		}
		finally {
			synchronized (this) {
				mCurRequest = null;
			}
		}
	}
	
	protected ServParam mParams;
	protected boolean mbAborted;
	protected HttpClient mHttpClient;
	protected RestfulRequest<?> mCurRequest;
	protected ManagedSocketFactory mCurSocketFactory;
	protected ManagedSocketFactory mCurSSLSocketFactory;
}

class ManagedSocketFactory {
	private HashSet<WeakReference<Socket>> mSocketList = new HashSet<WeakReference<Socket>>(16);
	private boolean mbClosed = false;
	SocketFactory mInternalFactory = null;
	
	public ManagedSocketFactory(boolean bSecure)
			throws NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
		if(bSecure) {
			KeyStore trustStore = getKeyStore();
			ManagedSSLSocketFactory factory = new ManagedSSLSocketFactory(trustStore);
			factory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			mInternalFactory = factory;
		} else {
			mInternalFactory = new ManagedPlainSocketFactory();
		}

	}
	
	
	
	public SocketFactory getFactory() {
		return mInternalFactory;
	}
	
	public synchronized void close() {
		mbClosed = true;
		DLog.d(getClass().getSimpleName(), "Close sockets! total:" + mSocketList.size());
		Socket socket = null;
		for(WeakReference<Socket> socketRef : mSocketList) {
			socket = socketRef.get();
			if(socket == null) {
				continue;
			}
			try {
				socket.shutdownInput();
				DLog.d(getClass().getSimpleName(), "Shut down input for socket:" + socket.toString());
			} catch(Exception e) {
				//DLog.d(getClass().getSimpleName(), "Already shut down input for socket:" + socket.toString());
			}
			try {
				socket.shutdownOutput();
				DLog.d(getClass().getSimpleName(), "Shut down output for socket:" + socket.toString());
			} catch(Exception e) {
				//DLog.d(getClass().getSimpleName(), "Already shut down output for socket:" + socket.toString());
			}
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mSocketList.clear();
		DLog.d(getClass().getSimpleName(), "All sockets closed!");
		mInternalFactory = null;
	}
	
	protected synchronized void addSocket(Socket socket) throws IOException {
		if(socket != null) {
			if(mbClosed) {
				socket.close();
			} else {
				mSocketList.add(new WeakReference<Socket>(socket));
			}
		}
	}
	
	private static synchronized KeyStore getKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore trustStore;
		trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		trustStore.load(null, null);
		return trustStore;
	}
	
	// http连接工厂类
	class ManagedPlainSocketFactory implements SocketFactory {
		PlainSocketFactory mFactory = new PlainSocketFactory();
		
		@Override
		public Socket connectSocket(Socket sock, String host, int port,
				InetAddress localAddress, int localPort, HttpParams params)
				throws IOException, UnknownHostException,
				ConnectTimeoutException {		
			DLog.d(getClass().getSimpleName(), "connectSocket:" + host + ":" + port);
			if(mbClosed) {
				throw new CancellationException();
			}
			Socket socket = mFactory.connectSocket(sock, host, port, localAddress, localPort, params);
			if(socket != null) {
				DLog.d(getClass().getSimpleName(), "Connected with socket:" + socket.toString());
				addSocket(socket);
			}
			return socket;
		}

		@Override
		public Socket createSocket() throws IOException {
			DLog.d(getClass().getSimpleName(), "createSocket:");
			Socket socket = mFactory.createSocket();
			if(socket != null) {
				DLog.d(getClass().getSimpleName(), "Created a socket:" + socket.toString());
				addSocket(socket);
			}
			return socket;
		}

		@Override
		public boolean isSecure(Socket sock) throws IllegalArgumentException {
			return mFactory.isSecure(sock);
		}
	}
	
	// https连接的工厂类。自动忽略服务器认证
	class ManagedSSLSocketFactory extends SSLSocketFactory {
		private SSLContext mSSLContext = null;
		public ManagedSSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
			super(truststore);
			TrustManager tm = new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

			};

			mSSLContext = SSLContext.getInstance("TLS");
			mSSLContext.init(null, new TrustManager[] { tm }, null);
		}
		
		@Override
		public Socket connectSocket(Socket sock, String host, int port,
				InetAddress localAddress, int localPort, HttpParams params)
				throws IOException, UnknownHostException,
				ConnectTimeoutException {
			
			DLog.d(getClass().getSimpleName(), "connectSocket:" + host + ":" + port);
			if(mbClosed) {
				throw new CancellationException();
			}
			Socket socket = super.connectSocket(sock, host, port, localAddress, localPort, params);
			if(socket != null) {
				DLog.d(getClass().getSimpleName(), "Connected with socket:" + socket.toString());
				addSocket(socket);
			}
			return socket;
		}

		@Override
		public Socket createSocket() throws IOException {
			DLog.d(getClass().getSimpleName(), "createSocket:");
			Socket socket = mSSLContext.getSocketFactory().createSocket();
			if(socket != null) {
				DLog.d(getClass().getSimpleName(), "Created a socket:" + socket.toString());
				addSocket(socket);
			}
			return socket;
		}
		
		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			DLog.d(getClass().getSimpleName(), "createSocket host:" + host);
			Socket newSocket = mSSLContext.getSocketFactory().createSocket(socket, host, port, autoClose);
			if(newSocket != null) {
				DLog.d(getClass().getSimpleName(), "Created a socket:" + newSocket.toString());
				addSocket(newSocket);
			}
			return newSocket;
		}
		
	}
	
	
	
	
}