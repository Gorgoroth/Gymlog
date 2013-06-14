package quelltextfabrik.gymlog;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WorkoutListAdapter extends ArrayAdapter<ArrayList<String>> {

	private ArrayList<ArrayList<String>> items;
	private Context context;

	public WorkoutListAdapter(Context context, int textViewResourceId, ArrayList<ArrayList<String>> results) {
		super(context, textViewResourceId, 0, results);		
		
		this.context = context;
		this.items = results;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {	
		View v = convertView;

		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.workoutlistitem, null);
		}
		
		// --- Retrieve Values -------------------------------------------------------------------
		final ArrayList<String> row = items.get(position);
		
		Log.d("debug", items.toString());
		Log.d("debug", row.toString());
		
		if(row != null) {			
			final String itemText =  row.get(0).toString();
			final int type = Integer.parseInt(row.get(1).toString());		
			TextView bTitle = (TextView) v.findViewById(R.id.workoutListTitle);
	
			// --- Load row ---------------------------------------------------------------------------
			bTitle.setText(itemText);		
			
			bTitle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int exerciseId = 0;
					Intent myIntent = null;
					
					// --- Get exercise id ----------------------------------------
	    		    GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getContext());    			
				 	myDbHelper.openDataBase();
					exerciseId = myDbHelper.retrieveWorkoutIdFromName(itemText);
	    			myDbHelper.close();
	    			
	    			// Choose activity to start	
					if(type != 3) {
						// Normal and circle
						myIntent = new Intent(context, ExerciseList.class);	
					} else {
						// Simple List
						myIntent = new Intent(context, WholeWorkout.class);
					}
	
					 // TODO use constants for key in putExtra
					myIntent.putExtra("mode", WorkoutList.myMode); 			
					myIntent.putExtra("type", type);
					myIntent.putExtra("workout", exerciseId);
					context.startActivity(myIntent);
				}
			});
			
			if(WorkoutList.myMode == 1) {
				// --- Delete Icon ------------------------------------------------------------------------
				ImageView delIcon = (ImageView) v.findViewById(R.id.workoutDelete);
				delIcon.setVisibility(View.VISIBLE);
				delIcon.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {					
						// --- Remove workout ---------
			        	final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
			        	
			        	alert.setMessage("Delete workout " + itemText);
			        	
			        	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			        		public void onClick(DialogInterface dialog, int whichButton) {   
			        			        			
			        			// --- Save to DB ----------------------------------------------------- 
			        		    GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getContext());
			        			
							 	myDbHelper.openDataBaseRW();
							 	int workoutId = myDbHelper.retrieveWorkoutIdFromName(itemText);
			        			myDbHelper.removeWorkoutFromProgram(WorkoutList.myProgram, workoutId);
			        			myDbHelper.close();
			        			
			        			// --- Refresh view ---------------------------------------------------
			        			items.remove(row);
			        			notifyDataSetChanged();		        			
			        		}	
			        	});
			        	
			    		alert.setNegativeButton("Cancel",
			    				new DialogInterface.OnClickListener() {
			    					public void onClick(DialogInterface dialog, int whichButton) {
			    						dialog.cancel();
			    					}
			    				});
			    		
			    		alert.show();
					}
				});
			}
		}
		return (v);
	}
}