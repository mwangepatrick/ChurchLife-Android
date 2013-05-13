package com.acstech.churchlife;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DialogDatePickerFragment extends DialogFragment {
	 
private OnDateSetListener mDateSetListener;
 
	private Calendar _calendar;
	
	public DialogDatePickerFragment() {
		// nothing to see here, move along
	}
 
	public DialogDatePickerFragment(Bundle b, OnDateSetListener callback) {
		
		// If an initial date was passed in, set the widget to
		//  that date.  Date format is expected to be MM/dd/yyyy
		if (b != null) {
			String dateString = b.getString("date");
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
			try {
				sdf.parse(dateString);
				_calendar = sdf.getCalendar();
			} catch (ParseException e) {
				//sink - no errors
			}
		}
		mDateSetListener = (OnDateSetListener) callback;
	}
 
	public Dialog onCreateDialog(Bundle savedInstanceState) {		
		if (_calendar == null) {
			_calendar = Calendar.getInstance();
		}
		return new DatePickerDialog(getActivity(), mDateSetListener, _calendar.get(Calendar.YEAR), _calendar.get(Calendar.MONTH), _calendar.get(Calendar.DAY_OF_MONTH));
	}
}
