package com.acstechnologies.churchlifev2;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Extends the BaseAdapter so that a ListView can display a Events
 *  
 * @author softwarearchitect
 *
 */
public class EventListItemAdapter extends BaseAdapter {
	
	private ArrayList<EventListItem> _items;
	private Context _context;

	// Constructor
	public EventListItemAdapter(Context context, ArrayList<EventListItem> items) {			
		_context = context;
		_items = items;				
	}
	
	public Context getContext()			{  return _context;				}
	public int getCount()				{  return _items.size();	 	}
	public Object getItem(int position) {  return _items.get(position); }
	public long getItemId(int position) {  return position; 			}

	
	public void refill(ArrayList<EventListItem> newItems) {
		_items = newItems;
	    notifyDataSetChanged();
	}

	
	/**
	 * Object representation of the listitem_event layout
	 * 
	 * @author softwarearchitect
	 *
	 */
	static class ListViewHolder {		 
		String id = "";
		TextView titleTextView;
		TextView timeTextView;
		TextView dateTextView;
	}

	
	/**
	 * Builds and returns the custom view for this list. 
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ListViewHolder holder = null;
		EventListItem currentItem = _items.get(position);			
		
//		if(currentItem.getTitle().startsWith("-")){		
//			LayoutInflater inflater = LayoutInflater.from(_context);
//			View divider = inflater.inflate(R.layout.listitem_header, null);
//		    return divider;
//		}
//		else {
			
			
		if (convertView == null) {
			
			 LayoutInflater inflater = LayoutInflater.from(_context);
			 convertView = inflater.inflate(R.layout.listitem_event, null); 
			 						 
			 holder = new ListViewHolder();
			 holder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
			 holder.timeTextView = (TextView) convertView.findViewById(R.id.timeTextView);
			 holder.dateTextView = (TextView) convertView.findViewById(R.id.dateTextView);				 				 				 				
			 			 						
			 convertView.setTag(holder);
		}
		else {
			 holder = (ListViewHolder) convertView.getTag();
		}
		
		//zzz add header if different
		holder.id = currentItem.getId();
		holder.titleTextView.setText(currentItem.getTitle());			
		holder.timeTextView.setText(currentItem.getTimeText());			
		holder.dateTextView.setText(currentItem.getDateText());
		
		
		return convertView;	
		//}
		
	}

}
