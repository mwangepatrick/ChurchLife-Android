package com.acstech.churchlife;

import java.util.ArrayList;

import com.acstech.churchlife.R;

import android.content.Context;
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
public class CommentListItemAdapter extends BaseAdapter {
	
	private ArrayList<CommentListItem> _items;
	private Context _context;

	// Constructor
	public CommentListItemAdapter(Context context, ArrayList<CommentListItem> items) {			
		_context = context;
		_items = items;				
	}
	
	public Context getContext()			{  return _context;				}
	public int getCount()				{  return _items.size();	 	}
	public Object getItem(int position) {  return _items.get(position); }
	public long getItemId(int position) {  return position; 			}
	
	public void refill(ArrayList<CommentListItem> newItems) {
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
		TextView commentDateTextView;
		TextView commentTextView;		
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
		CommentListItem currentItem = _items.get(position);			
			
		if (convertView == null) {
			
			 LayoutInflater inflater = LayoutInflater.from(_context);
			 holder = new ListViewHolder();
			 
			 if (currentItem.isTitleOnlyItem())
			 {
				 convertView = inflater.inflate(R.layout.listitem_default, null);	 
				 holder.commentTextView = (TextView) convertView.findViewById(R.id.titleTextView);				 				
			 }
			 else
			 {
				 convertView = inflater.inflate(R.layout.listitem_comment, null);
				 holder.commentDateTextView = (TextView) convertView.findViewById(R.id.commentDateTextView);
				 holder.commentTextView = (TextView) convertView.findViewById(R.id.commentTextView);
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
			holder.commentDateTextView.setText(currentItem.getCommentDate());
			holder.commentTextView.setText(currentItem.getCommentBody());							
		}
		else
		{
			// title only item
			holder.id = currentItem.getId();
			holder.commentTextView.setText(currentItem.getCommentBody());			
		}
		
		return convertView;				
	}

}
