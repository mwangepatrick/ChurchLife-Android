package com.acstech.churchlife.webservice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

import android.net.http.AndroidHttpClient;
import android.util.Base64;

public class RESTClient {

		public enum RequestMethod
		{
		GET,
		POST,
		PUT
		}
		
		private ArrayList <NameValuePair> params;
		private ArrayList <NameValuePair> headers;
		private String postEntity;
		
		private String url;		 
		private int responseCode;
		private String message;	 
		private String response;
	 
		public String getResponse() {
			return response;
		}
		 
	    public String getErrorMessage() {
	        return message;
	    }
	 
	    public int getResponseCode() {
	        return responseCode;
	    }
	 
	    //CTOR
	    public RESTClient(String url)
	    {
	        this.url = url;
	        params = new ArrayList<NameValuePair>();
	        headers = new ArrayList<NameValuePair>();
	    }
	 
	    public void AddParam(String name, String value)
	    {
	        params.add(new BasicNameValuePair(name, value));
	    }
	 
	    public void AddHeader(String name, String value)
	    {
	        headers.add(new BasicNameValuePair(name, value));
	    }

	    public void AddPostEntity(String entity)
	    {
	    	postEntity = entity;
	    }
	    
	    public void Execute(RequestMethod method) throws AppException
	    {
	    	try
	    	{
		        switch(method) {
		            case GET:
		            {
		                //add parameters
		                String combinedParams = "";
		                if(!params.isEmpty()){
		                    combinedParams += "?";
		                    for(NameValuePair p : params)
		                    {
		                        String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(),"UTF-8");
		                        if(combinedParams.length() > 1)
		                        {
		                            combinedParams  +=  "&" + paramString;
		                        }
		                        else
		                        {
		                            combinedParams += paramString;
		                        }
		                    }
		                }
		 
		                HttpGet request = new HttpGet(url + combinedParams);
		 
		                //add headers
		                for(NameValuePair h : headers)
		                {
		                    request.addHeader(h.getName(), h.getValue());
		                }
		 
		                executeRequest(request, url);
		                break;
		            }
		            case PUT:
		            	HttpPut putRequest = new HttpPut(url);
		       		 
		                //add headers
		                for(NameValuePair h : headers)
		                {
		                    putRequest.addHeader(h.getName(), h.getValue());
		                }
		 
		                if(!params.isEmpty()){
		                    putRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		                }
		 
		                if (postEntity != null && postEntity.length() > 0) {
		                	StringEntity entity = new StringEntity(postEntity, HTTP.UTF_8);
		                	entity.setContentType("application/json");			                
		                	putRequest.setEntity(new StringEntity(postEntity));	
		                }
		                		                
		                executeRequest(putRequest, url);
		                break;
		                
		            case POST:
		            {
		                HttpPost request = new HttpPost(url);
		 
		                //add headers
		                for(NameValuePair h : headers)
		                {
		                    request.addHeader(h.getName(), h.getValue());
		                }
		 
		                if(!params.isEmpty()){
		                    request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		                }
		 
		                if (postEntity != null && postEntity.length() > 0) {
		                	StringEntity entity = new StringEntity(postEntity, HTTP.UTF_8);
		                	entity.setContentType("application/json");			                
		                	request.setEntity(new StringEntity(postEntity));	
		                }
		                		                
		                executeRequest(request, url);
		                break;
		            }
		        }
	    	}
	    	catch (UnsupportedEncodingException e){
	    		throw AppException.AppExceptionFactory(e,
						   ExceptionInfo.TYPE.UNEXPECTED,
						   ExceptionInfo.SEVERITY.CRITICAL, 
						   "100",           												    
						   "RESTClient.Execute",
						   e.toString());
	    	}	    	
	    }
	 
	   
	    private void executeRequest(HttpUriRequest request, String url) throws AppException
	    {
	    	HttpClient client = new DefaultHttpClient();			/* 2.3.3 */	   
	    	//DefaultHttpClient client = createHttpClient();			/* 2.2 */	
	    	
	        HttpResponse httpResponse;
	 
	        try {
	           
	        	
	        	httpResponse = client.execute(request);  
	        	
	            responseCode = httpResponse.getStatusLine().getStatusCode();
	            message = httpResponse.getStatusLine().getReasonPhrase();
	 
	            HttpEntity entity = httpResponse.getEntity();
	 
	            if (entity != null) {
	 
	                //InputStream instream = entity.getContent();
	                //response = convertStreamToString(instream);
	                response = EntityUtils.toString(entity);
	                
	                // Closing the input stream will trigger connection release
	                //instream.close();
	            }
	 
	        } catch (ClientProtocolException e)  {
	        	client.getConnectionManager().shutdown();
	        		        	
	        	if (e.getCause() instanceof MalformedChallengeException) {
	        		// invalid credentials (username/password/sitenumber)
	        		throw AppException.AppExceptionFactory(e,
							   ExceptionInfo.TYPE.UNAUTHORIZED,
							   ExceptionInfo.SEVERITY.CRITICAL, 
							   "100",           												    
							   "RESTClient.executeRequest",
							   e.getCause().getMessage());
	        	}
	        	else
	        	{	        		           
		            throw AppException.AppExceptionFactory(e,
							   ExceptionInfo.TYPE.NOCONNECTION,
							   ExceptionInfo.SEVERITY.CRITICAL, 
							   "100",           												    
							   "RESTClient.executeRequest",
							   e.getMessage()); 
	        	}
	        } 
	        catch (IOException e) {
	            client.getConnectionManager().shutdown();
	            
	            throw AppException.AppExceptionFactory(e,
						   ExceptionInfo.TYPE.NOCONNECTION,
						   ExceptionInfo.SEVERITY.CRITICAL, 
						   "100",           												    
						   "RESTClient.executeRequest",
						   e.getMessage()); 
	        }
	    }
	    
	    
	    // Helper method for encoding basic authentication
	    public String getB64Auth (String login, String pass) {
	    	   String source=login+":"+pass;
	    	   String ret="Basic "+Base64.encodeToString(source.getBytes(),Base64.URL_SAFE|Base64.NO_WRAP);
	    	   return ret;
	    }

	    //This functionality has been replaced by EntityUtils.toString(entity);  
	    //line 145
	    // TODO:  Remove after testing
	    /*
	    private static String convertStreamToString(InputStream is) {
	 
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        StringBuilder sb = new StringBuilder();
	 
	        String line = null;
	        try {
	            while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                is.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        return sb.toString();
	    }
	    */
	    
	    /**
	     * 
	     * Uses the EasySSLSocketFactory to validate certificates
	     * 
	     * This was not necessary for 2.3.3 and up.  See line 151 for original code.
	     * 
	     * @return
	     */
	    public DefaultHttpClient createHttpClient() {
	    	
            SchemeRegistry schemeRegistry = new SchemeRegistry();            
            schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));	// http scheme            
            schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));			// https scheme

            HttpParams params = new BasicHttpParams();
            params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
            params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
            params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
              
            ProtocolVersion pv = new ProtocolVersion("HTTP", 1, 1);
            HttpProtocolParams.setVersion(params, pv);
            
            ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
            DefaultHttpClient client = new DefaultHttpClient(cm, params);
                            
            return client;
	    }


}
