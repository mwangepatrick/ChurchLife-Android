package com.acstechnologies.churchlifev2;

import java.util.ArrayList;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class TestActivity extends Activity {

	  @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        setContentView(R.layout.test);
	       
	        
//	        WaitForInternetCallback callback = new WaitForInternetCallback(this) {
//	        	public void onConnectionSuccess() {
//	        		Log.d("test","We have internet!");
//	        	}
//	        		 
//	        	public void onConnectionFailure() {
//	        		Log.d("test","failed to get internet connectivity...");
//	        		finish();
//	        	}
//	        };
//	        		   
//	        try {
//	        	WaitForInternet.setCallback(callback);
//	        } catch (SecurityException e) {
//	        	Log.w("test","Could not check network state.", e);
//	        	callback.onConnectionFailure();				
//	        }

	        		
	        /*
	        ArrayList<CustomListItem> searchResults = GetSearchResults();
	        	        
	        final ListView lv1 = (ListView) findViewById(R.id.listview01);
	        */
	        
	        //lv1.setCacheColorHint(0);
	        
	        //PhoneNumberListAdapter ca = new PhoneNumberListAdapter(this, searchResults);
	        //lv1.setAdapter(ca);
	     
	        
	        
	        
	        
	        
	        //lv1.setAdapter(new CustomBaseAdapter(this, searchResults));
	        
	        
	        
	        
	        
	        /*
	        setContentView(R.layout.individual);
	        	        
	        ViewGroup parent = (ViewGroup) findViewById(R.id.relativeLayout);
	        
	        // result: layout_height=25dip layout_width=25dip 
	        // parent.addView not necessary as this is already done by attachRoot=true
	        // view=root due to parent supplied as hierarchy root and attachRoot=true
	        View vFirst = LayoutInflater.from(getBaseContext()).inflate(R.layout.testwidget, parent, true);
	       // parent.addView(vFirst);

	        View vSecond = LayoutInflater.from(getBaseContext()).inflate(R.layout.testwidget, parent, true);
	       // parent.addView(vSecond);
	        */
	        
	        
	  }
	  
//	  public ArrayList<CustomListItem> GetSearchResults() {
//		  
//		  	ArrayList<CustomListItem> results = new ArrayList<CustomListItem>();
//		     
//		  	CustomListItem sr1 = new CustomListItem("1", "Home Phone", "111.123.2345", null, null);
//		    results.add(sr1);
//	     
//		    sr1 = new CustomListItem("1", "Work Phone", "222.333.4444", null, null);
//		    results.add(sr1);
//		    
//		    sr1 = new CustomListItem("1", "Mobile Phone", "333.567.8910", null, null);
//		    results.add(sr1);
//		    	     
//		    return results;
//	  }
}
