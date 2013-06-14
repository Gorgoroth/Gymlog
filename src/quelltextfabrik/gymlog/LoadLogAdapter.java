package quelltextfabrik.gymlog;

import java.util.ArrayList;
import java.util.Calendar;

import quelltextfabrik.gymlog.control.NumberPicker;
import android.content.Context;
import android.database.SQLException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LoadLogAdapter extends ArrayAdapter<ArrayList<String>> {

	private ArrayList<ArrayList<String>> items;
	private Context context;
	
	TextView setNrView;
	NumberPicker weightView; 
	NumberPicker repsView; 

	public LoadLogAdapter(Context context, int textViewResourceId, ArrayList<ArrayList<String>> dataItems) {
		super(context, textViewResourceId, 0, dataItems);		
		this.context = context;
		this.items = dataItems;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		View v = convertView;

		
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.loadlogitem, null);
		}
		
		Log.d("debug", "Position " + position);
		
		ArrayList<String> mySet = new ArrayList<String>();				
		mySet = this.items.get(position);
		
		
		
		if(mySet != null) {			
			for(int i = 0; i < mySet.size(); i++) {
				TextView temp = (TextView) ((ViewGroup) v).getChildAt(i);
				if(temp != null) {
					String value = mySet.get(i);
					
					// Date
					if(i == 1) {
			        	long timestamp = Long.decode(value);
			        	int mYear, mDay, mMonth, mHour, mMinutes;
			        	
			            Calendar cal = Calendar.getInstance();
			            cal.setTimeInMillis(timestamp);        
			            
			            mYear = cal.get(Calendar.YEAR);
			            mDay = cal.get(Calendar.DAY_OF_MONTH);
			            mMonth = cal.get(Calendar.MONTH);
			            mHour = cal.get(Calendar.HOUR_OF_DAY);
			            mMinutes = cal.get(Calendar.MINUTE);
			            
			            value = new StringBuilder()                    
		            		.append(mDay).append(".")
		                    .append(mMonth+1).append(".")	// Month is 0 based so add 1
		                    .append(mYear).append(" ")
		                    .append(mHour).append(":")
		                    .append(mMinutes)
		                    .toString();
					}
					
					// Workout name
					if(i == 3) {
				        GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getContext());
				        myDbHelper = new GymlogDatabaseHelper(getContext());
				        
					 	try { myDbHelper.openDataBase();	 
					 	} catch(SQLException sqle) { throw sqle; }
					 	
						value = myDbHelper.getWorkoutDetails(Integer.parseInt(value), GymlogDatabaseHelper.KEY_NAME);
				        myDbHelper.close();
					}
					
					// Workout name
					if(i == 4) {
						value = new StringBuilder("Session: " + value).toString();
					}
					
					// Exercise name
					if(i == 5) {
				        GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getContext());
				        myDbHelper = new GymlogDatabaseHelper(getContext());
				        
					 	try { myDbHelper.openDataBase();	 
					 	} catch(SQLException sqle) { throw sqle; }
					 	

						value = myDbHelper.retrieveExerciseDetail(Integer.parseInt(value), GymlogDatabaseHelper.KEY_NAME);
				        myDbHelper.close();
					}
					
					if(i == 6) {
						value = new StringBuilder("Set: " + value).toString();
					}
					
					if(i == 7) {
						value = new StringBuilder("Weight: " + value + " kg").toString();
					}
					
					if(i == 8) {
						value = new StringBuilder("Reps: " + value).toString();
					}
					
					temp.setText(value);
				} else {
					Log.d("debug", "No view in " + v.toString());
				}
			}
		}
		
		//Toast.makeText(getContext(), mySet.toString(), Toast.LENGTH_SHORT).show();
		
		return (v);
	}

}