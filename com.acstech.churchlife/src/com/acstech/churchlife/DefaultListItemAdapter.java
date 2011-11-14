package com.acstech.churchlife;

import java.util.ArrayList;

import com.acstech.churchlife.R;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Extends the BaseAdapter so that a ListView can display a list items
 *  
 * @author softwarearchitect
 *
 */
public class DefaultListItemAdapter extends BaseAdapter {
	
	private ArrayList<DefaultListItem> _items;
	private Context _context;

	// Constructor
	public DefaultListItemAdapter(Context context, ArrayList<DefaultListItem> items) {			
		_context = context;
		_items = items;				
	}
	
	public Context getContext()			{  return _context;				}
	public int getCount()				{  return _items.size();	 	}
	public Object getItem(int position) {  return _items.get(position); }
	public long getItemId(int position) {  return position; 			}

	
	public void refill(ArrayList<DefaultListItem> newItems) {
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
		TextView descriptionTextView;		
	}

	
	/**
	 * Builds and returns the custom view for this list.
	 *  
	 * TODO:  Future, change this to show different xml layouts (passed in by consumer)
	 * TODO:  Ability to hide title on xml layout if it does not exist in item
	 * 
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ListViewHolder holder = null;
		DefaultListItem currentItem = _items.get(position);			
			
		if (convertView == null) {
			
			 LayoutInflater inflater = LayoutInflater.from(_context);
			 convertView = inflater.inflate(R.layout.listitem_withtitle, null); 
			 						 
			 holder = new ListViewHolder();
			 holder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
			 holder.descriptionTextView = (TextView) convertView.findViewById(R.id.descriptionTextView);
				 						
			 convertView.setTag(holder);
		}
		else {
			 holder = (ListViewHolder) convertView.getTag();
		}
			
		// set control values to this item's property values		
		holder.id = currentItem.getId();
		holder.titleTextView.setText(currentItem.getTitle());
		
		if (currentItem.getContainsHtml()) {
			holder.descriptionTextView.setMovementMethod(LinkMovementMethod.getInstance());
			holder.descriptionTextView.setText(Html.fromHtml(currentItem.getDescription()));  // reformat for &amp; &lt; etc.
			Linkify.addLinks(holder.descriptionTextView, Linkify.ALL);
		}
		else {
			holder.descriptionTextView.setText(currentItem.getDescription()); 
		}
		
		return convertView;				
	}

}
