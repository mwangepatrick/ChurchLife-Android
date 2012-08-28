package com.acstech.churchlife;

import java.util.ArrayList;

import com.acstech.churchlife.R;

import android.content.Context;
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
public class CommentSummaryListItemAdapter extends BaseAdapter {
	
	private ArrayList<CommentSummaryListItem> _items;
	private Context _context;

	// Constructor
	public CommentSummaryListItemAdapter(Context context, ArrayList<CommentSummaryListItem> items) {			
		_context = context;
		_items = items;				
	}
	
	public Context getContext()			{  return _context;				}
	public int getCount()				{  return _items.size();	 	}
	public Object getItem(int position) {  return _items.get(position); }
	public long getItemId(int position) {  return position; 			}
	
	public void refill(ArrayList<CommentSummaryListItem> newItems) {
		_items = newItems;
	    notifyDataSetChanged();
	}

	
	/**
	 * Object representation of the layout
	 * 
	 * @author softwarearchitect
	 *
	 */
	static class ListViewHolder {		 
		String id = "";
		ImageView colorImageView;		
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
		CommentSummaryListItem currentItem = _items.get(position);			
			
		if (convertView == null) {
			
			 LayoutInflater inflater = LayoutInflater.from(_context);
			 holder = new ListViewHolder();
			 
			 if (currentItem.isTitleOnlyItem())
			 {
				 convertView = inflater.inflate(R.layout.listitem_default, null);	 
				 holder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);				 				
			 }
			 else
			 {
				 convertView = inflater.inflate(R.layout.listitem_commentsummary, null);	 

				 holder.colorImageView = (ImageView) convertView.findViewById(R.id.colorImageView);
				 holder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
				 holder.descriptionTextView = (TextView) convertView.findViewById(R.id.descriptionTextView);					 							
			 }
			 			  			 						 			 	
			 convertView.setTag(holder);
		}
		else {
			 holder = (ListViewHolder) convertView.getTag();
		}
			
		//----------------------------------------------------
		// set control values to this item's property values
		//----------------------------------------------------
		if (currentItem.isTitleOnlyItem() == false)
		{				
			holder.id = currentItem.getId();
			holder.titleTextView.setText(currentItem.getTitle());
			holder.descriptionTextView.setText(currentItem.getDescription());
					
			if (currentItem.getColor().length() > 0)
			{
				//holder.colorImageView.getBackground().setColorFilter(Color.parseColor("#00ff00"), Mode.DARKEN);
				//holder.colorImageView.setBackgroundColor(Color.parseColor("#" + currentItem.getColor()));
				//holder.colorImageView.setBackgroundColor(Color.GREEN);
				holder.colorImageView.setImageBitmap(ImageHelper.getBackground("#" + currentItem.getColor()));
			}
		}
		else
		{
			// title only item
			holder.id = currentItem.getId();
			holder.titleTextView.setText(currentItem.getTitle());			
		}
		
		return convertView;				
	}

	
}
