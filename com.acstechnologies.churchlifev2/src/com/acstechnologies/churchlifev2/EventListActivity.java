package com.acstechnologies.churchlifev2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionHelper;
import com.acstechnologies.churchlifev2.webservice.EventResponse;
import com.acstechnologies.churchlifev2.webservice.WebServiceHandler;

import android.os.Bundle;

public class EventListActivity extends OptionsActivity {
	
	EventResponse _wsEvents;					// results of the web service call
	AppPreferences _appPrefs;  	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try
        {   
        	_appPrefs = new AppPreferences(getApplicationContext());
        	
        	setContentView(R.layout.eventlist);
        	  
        	bindControls();						// Set state variables to their form controls
        	 
            
            /*
            ArrayList<Individual> searchResults = GetSearchResults2();
            final ListView lv1 = (ListView) findViewById(R.id.ListView01);
            lv1.setAdapter(new CustomBaseAdapter(this, searchResults));
            */
            
//            lv1.setOnItemClickListener(new OnItemClickListener() {        
//             public void onItemClick(AdapterView<?> a, View v, int position, long id) {
//              Object o = lv1.getItemAtPosition(position);
//              Individual fullObject = (Individual)o;
//              Toast.makeText(getApplicationContext(), "You have chosen: " + " " + fullObject.getName(), Toast.LENGTH_LONG).show();
//             } 
//            });
        	
        }           
        catch (Exception e) {
        	ExceptionHelper.notifyUsers(e, EventListActivity.this);
        	ExceptionHelper.notifyNonUsers(e);
        }          
    }
    
    /**
     *  Links state variables to their respective form controls
     */
    private void bindControls(){
    	//lv1 = (ListView)this.findViewById(R.id.ListView01);
    }
    
    //zzz remove ParseException - acccept text input from controls?  zzz 
    public String[] getSearchResults(String searchText) throws AppException, ParseException
    {    	
    	GlobalState gs = (GlobalState) getApplication();
    	    	
    	//zzz testing event list
    	SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy");    
    	
    	WebServiceHandler wh1 = new WebServiceHandler(_appPrefs.getWebServiceUrl());
    	Date start = dateFormatter.parse("07-01-2011");
    	Date stop = dateFormatter.parse("2011-09-01");
    	
    	EventResponse er = wh1.getEvents(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), start, stop, 0, 50);
    	
    	//zzz temp - 
    	return new String[] {};
    }
    
    
    
}