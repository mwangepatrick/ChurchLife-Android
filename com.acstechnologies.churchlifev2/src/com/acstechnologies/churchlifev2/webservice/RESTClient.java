package com.acstechnologies.churchlifev2.webservice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo;

import android.util.Base64;

public class RESTClient {

		public enum RequestMethod
		{
		GET,
		POST
		}
		
		private ArrayList <NameValuePair> params;
		private ArrayList <NameValuePair> headers;
		 
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
	        HttpClient client = new DefaultHttpClient();
	 
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
	            
	            throw AppException.AppExceptionFactory(e,
						   ExceptionInfo.TYPE.UNEXPECTED,
						   ExceptionInfo.SEVERITY.CRITICAL, 
						   "100",           												    
						   "RESTClient.executeRequest",
						   e.toString()); 
	        } 
	        catch (HttpHostConnectException e) {
	        	 throw AppException.AppExceptionFactory(e,
						   ExceptionInfo.TYPE.APPLICATION,
						   ExceptionInfo.SEVERITY.CRITICAL, 
						   "100",           												    
						   "RESTClient.executeRequest",
						   "There was a problem connecting to the data service."); 
	        }
	        catch (IOException e) {
	            client.getConnectionManager().shutdown();
	            
	            throw AppException.AppExceptionFactory(e,
						   ExceptionInfo.TYPE.UNEXPECTED,
						   ExceptionInfo.SEVERITY.CRITICAL, 
						   "100",           												    
						   "RESTClient.executeRequest",
						   e.toString()); 
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
}
