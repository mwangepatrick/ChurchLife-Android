package com.acstech.churchlife.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
 
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/*
 * This URL connection 'trusts' all - no SSL checking
 */
public class TrustingURLConnection {

	/**
	* @param args
	* @return
	* @throws IOException
	* @throws NoSuchAlgorithmException
	* @throws KeyManagementException
	* @throws URISyntaxException
	*/
	public static InputStream getContent(final String[] args) throws IOException, NoSuchAlgorithmException,	KeyManagementException {
	 
		// Create a trust manager that does not validate certificate chains
		final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				@Override
				public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
				}
	 
				@Override
				public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
				}
	 
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
		}
	};
	 
		// Install the all-trusting trust manager
		final SSLContext sslContext = SSLContext.getInstance("SSL");
		
		sslContext.init(null, trustAllCerts, null);
		
		// Create an ssl socket factory with our all-trusting manager
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				return true;
			}
		});
		
		// be authentic
		Authenticator.setDefault(new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(args[0], args[1].toCharArray());
			}
		});
	 
		// All set up, we can get a resource through https now:
		final URL url = new URL(args[2]);
	 
		URLConnection connection = url.openConnection();
	 
		return (InputStream) connection.getContent();
	}
}
