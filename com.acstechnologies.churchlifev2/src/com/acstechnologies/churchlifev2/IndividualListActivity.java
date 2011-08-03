package com.acstechnologies.churchlifev2;

import java.util.ArrayList;

import com.acstechnologies.churchlifev2.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class IndividualListActivity extends OptionsActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_individual);
       
        ArrayList<Individual> searchResults = GetSearchResults();
       
        final ListView lv1 = (ListView) findViewById(R.id.ListView01);
        lv1.setAdapter(new CustomBaseAdapter(this, searchResults));
       
        lv1.setOnItemClickListener(new OnItemClickListener() {        
         public void onItemClick(AdapterView<?> a, View v, int position, long id) {
          Object o = lv1.getItemAtPosition(position);
          Individual fullObject = (Individual)o;
          Toast.makeText(getApplicationContext(), "You have chosen: " + " " + fullObject.getName(), Toast.LENGTH_LONG).show();
         } 
        });
        
        Button button1;
        button1=(Button)this.findViewById(R.id.button1);
        button1.setOnClickListener(new OnClickListener() {		
		public void onClick(View v) {		
						
			Intent settingsActivity = new Intent(getBaseContext(), LoginActivity.class);
			//shortcut to bundle
			settingsActivity.putExtra("logout", true);
			
			/*
			Bundle bundle = settingsActivity.getExtras();
			
			//Bundle bundle = new Bundle();
			bundle.putBoolean("logout", true);
			settingsActivity.putExtras(bundle);
			*/			
			startActivity(settingsActivity);
		 }		
		});
        
        
        
        
        
    }
   

	
    private ArrayList<Individual> GetSearchResults(){
    
    	ArrayList<Individual> results = new ArrayList<Individual>();
	     
	    Individual sr1 = new Individual();
	    sr1.setName("John Smith");
	    sr1.setCityState("Dallas, TX");
	    sr1.setPhone("214-555-1234");
	    results.add(sr1);
     
	    sr1 = new Individual();
	    sr1.setName("Jane Doe");
	    sr1.setCityState("Atlanta, GA");
	    sr1.setPhone("469-555-2587");
	    results.add(sr1);
	     
	    sr1 = new Individual();
	    sr1.setName("Steve Young");
	    sr1.setCityState("Miami, FL");
	    sr1.setPhone("305-555-7895");
	    results.add(sr1);
	     
	    sr1 = new Individual();
	    sr1.setName("Fred Jones");
	    sr1.setCityState("Las Vegas, NV");
	    sr1.setPhone("612-555-8214");
	    results.add(sr1);
     
	    return results;
    }
}    