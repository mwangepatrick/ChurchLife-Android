package com.acstech.churchlife;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.R;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class TestActivity extends Activity {

	  @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        setContentView(R.layout.test);
	       	
	        ListView lv1 = (ListView) findViewById(R.id.ListView01); 
	        lv1.setAdapter(getAdapter2());  
	        
	        lv1.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {                  
                	TextView idView = (TextView)view.findViewById(R.id.idView);
                	
                	String s = "123";
                		
                	//EventListItem item = (EventListItem)parent.getAdapter().getItem(position);                 	                
                	String x = s;            	                 	
                }
              });      
  
	        
	        
	        
	        
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
	  
	  
	  public SeparatedListAdapter getAdapter1() {
		
		  
	        ArrayList<EventListItem> itemList = new ArrayList<EventListItem>();
	      
			for (int i = 0; i < 5; i++) {
				/*
				itemList.add(new EventListItem(Integer.toString(i),
												"Test Event", 
												new Date()));
												*/	  
			}
			
			EventListItemAdapter eventsAdapter = new EventListItemAdapter(TestActivity.this, itemList);
			
	            
			//zzz try simpleadapter...but what do we get onclick?  does position map?
					
			//lv1.setAdapter(new EventListItemAdapter(EventListActivity.this, _itemList));
	        
	        // create our list and custom adapter  
			SeparatedListAdapter adapter = new SeparatedListAdapter(this);  
			adapter.addSection("Real Events", eventsAdapter);
			
			//zzz2
			  
	        ArrayList<EventListItem> itemList2 = new ArrayList<EventListItem>();
	       
			for (int i = 0; i < 5; i++) {				
				/*itemList2.add(new EventListItem(Integer.toString(i),
												"Blah Event", 
												new Date()));
												*/	  
			}
			
			EventListItemAdapter eventsAdapter2 = new EventListItemAdapter(TestActivity.this, itemList2);
			adapter.addSection("Events Blah", eventsAdapter2);
			
			//zzz2
			
	        //adapter.addSection("Array test", new ArrayAdapter<String>(this, R.layout.list_item, new String[] { "First item", "Item two" }));  
	        //adapter.addSection("Security", new SimpleAdapter(this, security, R.layout.list_complex, new String[] { ITEM_TITLE, ITEM_CAPTION }, new int[] { R.id.list_complex_title, R.id.list_complex_caption }));
	        
			
			/* works
	        List<Map<String,?>> events = new LinkedList<Map<String,?>>();	        
	        events.add(createEventItem("My First Event", "9:00", "Aug 21"));
	        events.add(createEventItem("Event Numero Dos", "12:00", "Aug 25"));
	        events.add(createEventItem("Yet Again Event", "4:00", "Sep 21"));	  	        
	       // adapter.addSection("Events", new SimpleAdapter(this, events, R.layout.listitem_event, new String[] { EVENT_ITEM_TITLE, EVENT_ITEM_TIME,EVENT_ITEM_DATE  }, new int[] { R.id.titleTextView, R.id.timeTextView, R.id.dateTextView }));
	        * //SimpleAdapter sa = new SimpleAdapter(this, events, R.layout.listitem_event, new String[] { EVENT_ITEM_TITLE, EVENT_ITEM_TIME,EVENT_ITEM_DATE  }, new int[] { R.id.titleTextView, R.id.timeTextView, R.id.dateTextView }));
			ad
			
          */
	        		
			return adapter;
	  }
	  
	  //
	  public SeparatedListAdapter getAdapter2() {
			
		  //zzz try simpleadapter...but what do we get onclick?  does position map?
			
	        // create our list and custom adapter  
			SeparatedListAdapter adapter = new SeparatedListAdapter(this);  
					
			//adapter.addSection("Security", new SimpleAdapter(this, security, R.layout.list_complex, new String[] { ITEM_TITLE, ITEM_CAPTION }, new int[] { R.id.list_complex_title, R.id.list_complex_caption }));
	        					
	        List<Map<String,String>> events = new LinkedList<Map<String,String>>();	        
	        events.add(createEventItem("a", "My First Event", "9:00", "Aug 21"));
	        events.add(createEventItem("b", "Event Numero Dos", "12:00", "Aug 25"));
	        events.add(createEventItem("c", "Yet Again Event", "4:00", "Aug 30"));	  	        
	        adapter.addSection("August 2011", new SimpleAdapter(this, events, R.layout.listitem_event, new String[] { EVENT_ITEM_ID, EVENT_ITEM_TITLE, EVENT_ITEM_TIME  }, new int[] { R.id.idView, R.id.titleTextView, R.id.timeTextView }));
	        

	        List<Map<String,String>> events2 = new LinkedList<Map<String,String>>();	        
	        events2.add(createEventItem("d", "My Meeting", "9:00", "Dec 21"));
	        events2.add(createEventItem("e", "Meetings of the Minds", "12:00", "Dec 25"));
	        events2.add(createEventItem("f", "Battle of Cowpens", "10:00", "Dec 31"));	  	        
	        adapter.addSection("December 2011", new SimpleAdapter(this, events2, R.layout.listitem_event, new String[] { EVENT_ITEM_ID, EVENT_ITEM_TITLE, EVENT_ITEM_TIME  }, new int[] { R.id.idView, R.id.titleTextView, R.id.timeTextView }));
	        
	        
	        
	        
	        //SimpleAdapter sa = new SimpleAdapter(this, events, R.layout.listitem_event, new String[] { EVENT_ITEM_TITLE, EVENT_ITEM_TIME,EVENT_ITEM_DATE  }, new int[] { R.id.titleTextView, R.id.timeTextView, R.id.dateTextView }));
	        		
			return adapter;
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
	  
	  
	       public final static String ITEM_TITLE = "title";  
	       public final static String ITEM_CAPTION = "caption";  
	     
	       public Map<String,?> createItem(String title, String caption) {  
	           Map<String,String> item = new HashMap<String,String>();  
	           item.put(ITEM_TITLE, title);  
	           item.put(ITEM_CAPTION, caption);  
	           return item;  
	       }  
	  

	       public final static String EVENT_ITEM_ID = "id";  
	       public final static String EVENT_ITEM_TITLE = "title";  
	       public final static String EVENT_ITEM_TIME = "time";  
	       public final static String EVENT_ITEM_DATE = "date"; 
	       
	       public Map<String, String> createEventItem(String id, String title, String timeText, String dateText) {  
	           Map<String,String> item = new HashMap<String,String>();
	           item.put(EVENT_ITEM_ID, id); 
	           item.put(EVENT_ITEM_TITLE, title);  
	           item.put(EVENT_ITEM_TIME, timeText);
	           item.put(EVENT_ITEM_DATE, dateText);  	           
	           return item;  
	       }  
	       
	  
	  
}
