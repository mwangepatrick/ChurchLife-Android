package com.acstechnologies.churchlifev2;

import java.util.ArrayList;

import com.acstechnologies.churchlifev2.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class CustomBaseAdapter extends BaseAdapter {
	 private static ArrayList<Individual> searchArrayList;
	 
	 private LayoutInflater mInflater;

	 public CustomBaseAdapter(Context context, ArrayList<Individual> results) {
	  searchArrayList = results;
	  mInflater = LayoutInflater.from(context);
	 }

	 public int getCount() {
	  return searchArrayList.size();
	 }

	 public Object getItem(int position) {
	  return searchArrayList.get(position);
	 }

	 public long getItemId(int position) {
	  return position;
	 }

	 public View getView(int position, View convertView, ViewGroup parent) {
	  ViewHolder holder;
	  if (convertView == null) {
	   convertView = mInflater.inflate(R.layout.individual, null);
	   holder = new ViewHolder();
	   holder.txtName = (TextView) convertView.findViewById(R.id.name);
	   holder.txtCityState = (TextView) convertView.findViewById(R.id.cityState);
	   holder.txtPhone = (TextView) convertView.findViewById(R.id.phone);

	   convertView.setTag(holder);
	  } else {
	   holder = (ViewHolder) convertView.getTag();
	  }
	  
	  holder.txtName.setText(searchArrayList.get(position).getName());
	  holder.txtCityState.setText(searchArrayList.get(position).getCityState());
	  holder.txtPhone.setText(searchArrayList.get(position).getPhone());

	  return convertView;
	 }

	 static class ViewHolder {
	  TextView txtName;
	  TextView txtCityState;
	  TextView txtPhone;
	 }
}