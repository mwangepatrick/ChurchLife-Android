package com.acstechnologies.churchlifev2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.acstechnologies.churchlifev2.R;
import com.acstechnologies.churchlifev2.webservice.RESTClient;
import com.acstechnologies.churchlifev2.webservice.RESTClient.RequestMethod;

public class PersonListActivity extends ListActivity {
	
    static final String[] COUNTRIES = new String[] {
        "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra",
        "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina",
        "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan"
      };    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        String[] countries = getResources().getStringArray(R.array.countries_array);

        setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, countries));

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view,
              int position, long id) {
            // When clicked, show a toast with the TextView text
            Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
                Toast.LENGTH_SHORT).show();
          }
        });
        */
        
                    
        //get data from webservice to populate the listview
        String[] names = getNames4();
        
        setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, names));
        
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view,
              int position, long id) {
            // When clicked, show a toast with the TextView text
            Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
                Toast.LENGTH_SHORT).show();
          }
        });
       
    }
    
    public String[] getNames() 
    {
    	String[] names = new String[] { "one", "two" };
    	    	
    	DefaultHttpClient httpClient = new DefaultHttpClient();
    	
    	 //HttpGet request = new HttpGet(SERVICE_URI + "/GetVehicle/" + plateSpinner.getSelectedItem());
    	 HttpPost post = new HttpPost("http://secure.accessacs.com/link/v1/json/account/signinwithusername");    	    	     	    
    	 //post.setHeader("Accept", "application/json");
    	 post.setHeader("Content-type", "application/x-www-form-urlencoded");
    	 //post.setHeader("Content-type", "application/json");
    	 
    	 //HttpEntity myEntity = new StringEntity(message);
    	 //post.setEntity(myEntity);
    	 
    	 /*
         HttpParams params = new BasicHttpParams();
         params.setParameter("sitenumber", "106217");
         params.setParameter("username", "admin");
         params.setParameter("password", "password");
         
         post.setParams(params);
    	 */
         
         List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
         
         nameValuePairs.add(new BasicNameValuePair("sitenumber", "106217"));
         nameValuePairs.add(new BasicNameValuePair("username", "admin"));
         nameValuePairs.add(new BasicNameValuePair("password", "password"));
                                        
        
         try {
        	 UrlEncodedFormEntity ent = new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8);   
			 //post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        	 post.setEntity(ent);
			
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}        
         
        HttpResponse response = null;
		try {
			response = httpClient.execute(post);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  
         HttpEntity responseEntity = response.getEntity();
         
         
         String responseString = null;
		try {
			responseString = EntityUtils.toString(response.getEntity());
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
         

         
       try {			
			JSONObject returnedObjects = new JSONObject(responseString);			
			JSONArray tokenArray = returnedObjects.getJSONArray("data");
			
			JSONObject firstToken = tokenArray.getJSONObject(0);
			
			Log.d("this is the token", firstToken.getString("Token"));
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
    
    	return names;    	
    }
    
    public String[] getNames2()
    {
    	RESTClient client = new RESTClient("http://secure.accessacs.com/link/v1/json/account/signinwithusername");
    	
    	client.AddParam("sitenumber", "106217");
    	client.AddParam("username", "admin");
    	client.AddParam("password", "password");    	
    	 
    	try {
    	    client.Execute(RequestMethod.POST);
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}
    	 
    	String response = client.getResponse();
    	//return response;
    	
    	
    	String[] names = new String[] { "kit", "kat" };
    	return names;  
    	
    }
    
    // this is the logic to validate an account (login) after the user inputs their
    //  sitenumber, username, password. A login page should validate the values
    //  and then hang on to them for future calls
    //
    // zzz - how to store off (like a cookie) the name/password entered 
    // zzz - do we allow a 'remember me' checkbox?
    //
    public void validateAccount()
    {
    	RESTClient client = new RESTClient("http://labs.acstechnologies.com/api/account/validate");
    	
    	client.AddParam("sitenumber", "106217");
    	client.AddParam("username", "admin");
    	client.AddParam("password", "password");    	
    	 
    	try {
    	    client.Execute(RequestMethod.POST);
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}
    	 
    	String response = client.getResponse();
    	//return response;
     
    }
    
    // actually return a list of individuals
    public String[] getNames4()
    {
    	RESTClient client = new RESTClient("http://labs.acstechnologies.com/api/106217/individuals");
    	    
    	client.AddParam("searchText", "smith");
    	client.AddParam("firstResult", "0");
    	client.AddParam("maxResult", "25");    	
    	
    	String auth = client.getB64Auth("admin","password");    	
    	client.AddHeader("Authorization", auth); //"Basic YWRtaW46cGFzc3dvcmQ=");
    	
    	
    	try {
    	    client.Execute(RequestMethod.GET);
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}
    	 
    	String response = client.getResponse();
    	//return response;
    	
    	// Present the individuals returned to the user
    	String[] names = null;
		try {
			JSONObject returnedObject = new JSONObject(response);
			JSONObject dataObject = returnedObject.getJSONObject("Data");
			JSONArray objectArray = dataObject.getJSONArray("Data");
		
			names =  new String[objectArray.length()];
			
			for (int i = 0; i < objectArray.length(); i++) {
			    JSONObject row = objectArray.getJSONObject(i);			    
			    names[i] = row.getString("FirstName") + " " + row.getString("LastName");			  
			}
							
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
    	    	   
    	//String[] names = new String[] { "four", "five" };
    	return names;     
    }
    
    
    
    
}