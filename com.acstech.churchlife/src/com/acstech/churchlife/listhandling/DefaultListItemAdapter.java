package com.acstech.churchlife.listhandling;

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
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Extends the BaseAdapter so that a ListView can display a list items
 *  
 * @author softwarearchitect
 *
 */
public class DefaultListItemAdapter extends BaseAdapter {
	
	private int _layoutResourceId = R.layout.listitem_default;
	private ArrayList<DefaultListItem> _items;
	private Context _context;

	// Constructor
	public DefaultListItemAdapter(Context context, ArrayList<DefaultListItem> items) {			
		_context = context;
		_items = items;				
	}

	public DefaultListItemAdapter(Context context, ArrayList<DefaultListItem> items, int layoutResourceId) {			
		_context = context;
		_items = items;	
		_layoutResourceId = layoutResourceId;
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
		ImageView iconImageView;
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
			 convertView = inflater.inflate(_layoutResourceId, null);
			 
			 holder = new ListViewHolder();
			 holder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
			 holder.descriptionTextView = (TextView) convertView.findViewById(R.id.descriptionTextView);
			 holder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);
			 convertView.setTag(holder);
		}
		else {
			 holder = (ListViewHolder) convertView.getTag();
		}

		//------------------------------------------------------------------------------
		// Set control values to this item's property values
		//
		// Keep in mind that not all layouts implement all of the holder's controls!
		//  ex.  Some layouts don't have a "description" textview or an icon
		//------------------------------------------------------------------------------
		holder.id = currentItem.getId();
			
		// title
		if (holder.titleTextView != null) {
			holder.titleTextView.setText(currentItem.getTitle());
		}
		
		// description
		if (holder.descriptionTextView != null)	{
			if (currentItem.getContainsHtml()) {
				holder.descriptionTextView.setMovementMethod(LinkMovementMethod.getInstance());
				holder.descriptionTextView.setText(Html.fromHtml(currentItem.getDescription()));  // reformat for &amp; &lt; etc.
				Linkify.addLinks(holder.descriptionTextView, Linkify.ALL);
			}
			else {
				holder.descriptionTextView.setText(currentItem.getDescription()); 
			}
		}
		
		// icon
		if (holder.iconImageView != null){						
			holder.iconImageView.setImageResource(currentItem.getIconResourceId());
		}
		
		return convertView;				
	}
}
