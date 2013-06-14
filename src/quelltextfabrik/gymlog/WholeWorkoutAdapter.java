package quelltextfabrik.gymlog;

import java.util.ArrayList;

import quelltextfabrik.gymlog.control.NumberPicker;
import quelltextfabrik.gymlog.control.WeightPicker;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WholeWorkoutAdapter extends ArrayAdapter<ArrayList<String>> {

	private ArrayList<ArrayList<String>> items;
	private Context context;
		
	public WholeWorkoutAdapter(Context context, int textViewResourceId, ArrayList<ArrayList<String>> dataItems) {
		super(context, textViewResourceId, 0, dataItems);		
		this.context = context;
		this.items = dataItems;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// --- Inflate view -------------------------------------------------------------------------------------------
		View v = convertView;
		final int pos = position;
		
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.wholeworkoutitem, null);
		}
		
		// --- Get data to display ------------------------------------------------------------------------------------
		ArrayList<String> mySet = new ArrayList<String>();				
		mySet = this.items.get(position);
		Log.d("debug", "Loading row " + position + " of " + this.items.size() + " with " + mySet.size() + " items");
		Log.d("debug", "Items: " + mySet.get(0) + ", " + mySet.get(1) + ", " + mySet.get(2));
		

		Button setDone = (Button)v.findViewById(R.id.wholeWorkoutDone);			
		
		if(WholeWorkout.myMode == 2) {
			// --- TRAIN MODE -----------------------------------------------------------------------------------------
			// --- OnClickListener for set done ---------------------------------------------------
			setDone.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(context);
			        myDbHelper = new GymlogDatabaseHelper(context);
			        
					int program = 1;					
					int workout = WholeWorkout.myWorkoutId;					
					int exercise;
					int session = WholeWorkout.mySession;
					
					LinearLayout row = (LinearLayout)arg0.getParent().getParent();					
					
					// --- Retrieve values ---------
					// TODO SERIOUSLY, use somehow ids instead of numbers
				 	myDbHelper.openDataBase();
				 	exercise = myDbHelper.retrieveExerciseIdFromName(
				 		((TextView)
							((LinearLayout) row.getChildAt(1))
							.getChildAt(0))
						.getText().toString()
				 	);
				 	myDbHelper.close();		

					Double weight = ((WeightPicker)((LinearLayout) row.getChildAt(0)).getChildAt(0)).getValue();
					int reps = ((NumberPicker)((LinearLayout) row.getChildAt(0)).getChildAt(2)).getValue();
										
					Toast.makeText(context,
							"You have completed exercise " + exercise
							+ " with " + weight + " kg and "
							+ reps + " reps "
							+ "in session " + session
							+ " at list position " + position
							,
							Toast.LENGTH_SHORT).show();					
			        	 	
				 	
					// --- Save Values to log ---------------------------------
				 	myDbHelper.openDataBaseRW();
				 	myDbHelper.logSet(program, workout, session, exercise, 0, weight, reps);
				 	myDbHelper.close();	
				 	
				 	// --- Update progress bar --------------------------------
				 	WholeWorkout.updateProgress(position);
				 	
				 	// --- Remove finished set from view ----------------------
				 	items.remove(pos);
				 	notifyDataSetChanged();
				}			
			});
		} else {
			// --- SETUP MODE -----------------------------------------------------------------------------------------
			// --- Hide done button, show delete and save -----------------------------------------
			setDone.setVisibility(View.INVISIBLE);
			ImageView remove = (ImageView) v.findViewById(R.id.wholeWorkoutDelete);
			remove.setVisibility(View.VISIBLE);
			Button save = (Button)v.findViewById(R.id.wholeWorkoutSave);
			save.setVisibility(View.VISIBLE);
			
			// --- Save onclick listener ----------------------------------------------------------
			save.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getContext());
					LinearLayout row = (LinearLayout)arg0.getParent().getParent();
								        
					// --- Get weight -------------------------------------------------------------
					WeightPicker weightView = ((WeightPicker) ((LinearLayout) row.getChildAt(0)).getChildAt(0));
					Double weightVal = weightView.getValue();

					// --- Get reps ---------------------------------------------------------------
					NumberPicker repsView = ((NumberPicker) ((LinearLayout) row.getChildAt(0)).getChildAt(2));
					int repsVal = repsView.getValue();
					
					// --- Get exercise id --------------------------------------------------------
				 	myDbHelper.openDataBase();
					int exerciseId = myDbHelper.retrieveExerciseIdFromName(
					 		((TextView)
								((LinearLayout) row.getChildAt(1))
								.getChildAt(0))
							.getText().toString()
					 	);
				 	myDbHelper.close();		
					
					// --- Change in DB -----------------------------------------------------------
				 	// TODO error handling
					myDbHelper.openDataBaseRW();
					myDbHelper.updateSingleExerciseFromWorkoutByRow(WholeWorkout.myWorkoutId, WholeWorkout.myWorkoutType, 0, position, weightVal, repsVal);		        			
	    			myDbHelper.close();
					
					// --- Refresh view ---------------------------------------------------
				 	// TODO cant we put that into the constructor somehow?
				 	ArrayList<String> newRow = new ArrayList<String>();				 	
				 	newRow.add("" + exerciseId);
				 	newRow.add("" + weightVal);
				 	newRow.add("" + repsVal);
				 	
					items.set(position, newRow);					
					notifyDataSetChanged();
					
					Log.d("debug", "Save exercise at " + position + " weight " + weightVal + " reps " + repsVal);
					Toast.makeText(getContext(), "Changes saved", Toast.LENGTH_SHORT).show();
					
				}
			});
			
			// --- Remove onclick listener --------------------------------------------------------
			remove.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Log.d("debug", "Delete exercise at " + position);

					LinearLayout row = (LinearLayout)arg0.getParent().getParent();
					final String itemText = ((TextView) ((LinearLayout) row.getChildAt(1)).getChildAt(0)).getText().toString();
			
		        	final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
		        	
		        	alert.setMessage("Delete exercise " + itemText);
		        	
		        	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		        		public void onClick(DialogInterface dialog, int whichButton) {   
		        			        			
							// --- Delete from DB ------------------------------------------------- 
							GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getContext());
							myDbHelper.openDataBaseRW();
							myDbHelper.removeSingleExerciseFromWorkoutByRow(WholeWorkout.myWorkoutId, WholeWorkout.myWorkoutType, 0, position);		        			
		        			myDbHelper.close();
		        			
							// --- Refresh view ---------------------------------------------------
							items.remove(position);
							notifyDataSetChanged();
							
							Toast.makeText(getContext(), "Deleted workout " + itemText, Toast.LENGTH_SHORT).show();
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
		
		// --- Display data -------------------------------------------------------------------------------------------
		if(mySet != null) {			
			// TODO use same string for less allocation work?
			
			// --- Exercise name ----------------------------------------------
			// TODO use same String for less allocation?
			TextView exerciseNameView = (TextView) v.findViewById(R.id.wholeWorkoutExerciseName);
			if(exerciseNameView != null) {
		        GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getContext());
		        myDbHelper = new GymlogDatabaseHelper(getContext());		        
			 	myDbHelper.openDataBase();
				final String exerciseName = myDbHelper.retrieveExerciseDetail(Integer.parseInt(mySet.get(0).toString()), GymlogDatabaseHelper.KEY_NAME);
		        myDbHelper.close();		        
		        exerciseNameView.setText(exerciseName);
			}
			
			// TODO weight picker needs to change value inside ArrayList
			// --- Weight picker with database value --------------------------
			WeightPicker weightView = (WeightPicker) v.findViewById(R.id.wholeWorkoutWeight);
			if(weightView != null) {
				final String weightText = mySet.get(1).toString();
				weightView.setValue(Double.parseDouble(weightText));
			}
			
			// TODO weight picker needs to change value inside ArrayList
			// --- Weight picker with database value --------------------------
			NumberPicker repsView = (NumberPicker) v.findViewById(R.id.wholeWorkoutReps);
			if(repsView != null) {
				final String repsText = mySet.get(2).toString();
				repsView.setValue(Integer.parseInt(repsText));
			}
		}		
		
		return (v);
	}
}