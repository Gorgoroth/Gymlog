package quelltextfabrik.gymlog;

import java.util.ArrayList;

import quelltextfabrik.gymlog.control.NumberPicker;
import quelltextfabrik.gymlog.control.NumberPicker.OnRepChangedListener;
import quelltextfabrik.gymlog.control.WeightPicker;
import quelltextfabrik.gymlog.control.WeightPicker.OnChangedListener;
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

public class ExerciseAdapter extends ArrayAdapter<ArrayList<String>> {

	private ArrayList<ArrayList<String>> items;
	private Context context;
	
	TextView setNrView;
	WeightPicker weightView; 
	NumberPicker repsView; 
	TextView exName;

	public ExerciseAdapter(Context context, int textViewResourceId, ArrayList<ArrayList<String>> dataItems) {
		super(context, textViewResourceId, 0, dataItems);		
		this.context = context;
		this.items = dataItems;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// --- Inflate view -------------------------------------------------------------------------------------------
		View v = convertView;		
		final int pos = position;
				
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.exerciseitem, null);
		}
		
		// --- Get data to display ------------------------------------------------------------------------------------
		ArrayList<String> mySet = new ArrayList<String>();				
		mySet = this.items.get(position);
		Log.d("debug", "Loading row " + position + " of " + this.items.size() + " with " + mySet.size() + " items");
		
		// --- Display data -------------------------------------------------------------------------------------------
		if(mySet != null) {	
			
				// --- Number of set ----------------------------------------------
				setNrView = (TextView) v.findViewById(R.id.exerciseSetNr);
				if(setNrView != null) {
					final String setNrText = mySet.get(0).toString();				
					setNrView.setText(setNrText);
				}

				// --- Weight picker with database value --------------------------
				weightView = (WeightPicker) v.findViewById(R.id.exerciseWeight);
				if(weightView != null) {
					// --- Set value ----------------------
					final String weightText = mySet.get(1).toString();
					weightView.setValue(Double.parseDouble(weightText));

					// --- Set on change listener ---------
					weightView.setOnChangeListener(new OnChangedListener() {
							public void onChanged(WeightPicker picker, Double oldVal, Double newVal) {								
								ArrayList<String> myRow = items.get(pos);
				        	    myRow.set(1, "" + newVal);
				        	    items.set(pos, myRow);	
				        	    
				        	    // --- If type == 1, change all subsequent here to
				        	    if(ExerciseList.myWorkoutType == 1) { // TODO use constant
				        	    	for(int i = (pos+1); i < items.size(); i++) {
				        	    		myRow = items.get(i);
						        	    myRow.set(1, "" + newVal);
						        	    items.set(i, myRow);	 
				        	    	}
				        	    }				        	    
				        	    notifyDataSetChanged();	
							}
						
					});
				}
				
				// --- Reps picker with database value ----------------------------
				repsView = (NumberPicker) v.findViewById(R.id.exerciseReps);
				if(repsView != null) {
					final String repsText = mySet.get(2).toString();
					repsView.setValue(Integer.parseInt(repsText));
					
					// --- On change listener -------------
					repsView.setOnRepChangeListener(new OnRepChangedListener() {
						public void onChanged(NumberPicker picker, int newVal) {							
							ArrayList<String> myRow = items.get(pos);
			        	    myRow.set(2, "" + newVal);
			        	    items.set(pos, myRow);	    
			        	    notifyDataSetChanged();	
						}
					});
				}
				
				// --- Circle needs exercise name ---------------------------------
				exName = (TextView) v.findViewById(R.id.exerciseName);
				if(exName != null) {
					String name = "";
	    		    GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getContext());    			
				 	myDbHelper.openDataBaseRW();		        
					if(ExerciseList.myWorkoutType == 2) {
						name = myDbHelper.retrieveExerciseDetail(Integer.parseInt(mySet.get(3)), GymlogDatabaseHelper.KEY_NAME);
					} else {
						name = Exercise.headline;
					}
	    			myDbHelper.close();	    			
	    			exName.setText(name);
    			}
				
				Button setDone = (Button)v.findViewById(R.id.exerciseSetDone);	
				
				if(Exercise.myMode == Gymlog.MODE_SETUP) {
					// --- SETUP MODE ---------------------------------------------------------------------------------------------------------					
					// --- Hide Done button ---------------------------------------
					setDone.setVisibility(View.GONE);
					
					// --- Show Controls ------------------------------------------
					LinearLayout editView = (LinearLayout) v.findViewById(R.id.exerciseSetupControls);
					editView.setVisibility(View.VISIBLE);
					
					// --- Delete Icon --------------------------------------------
					ImageView delIcon = (ImageView) v.findViewById(R.id.exerciseDelete);
					delIcon.setOnClickListener(new View.OnClickListener() {
			            public void onClick(View view) {
			            	final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
			            	final View v = view;
			            	
			            	alert.setTitle("Are you sure you want to delete this?");
			            	
			            	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			            		public void onClick(DialogInterface dialog, int whichButton) {
			            			
									LinearLayout row = (LinearLayout)v.getParent().getParent().getParent();
									GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getContext());    			
								 	myDbHelper.openDataBaseRW();
									
									int exerciseId = myDbHelper.retrieveExerciseIdFromName(
									 		((TextView) ((LinearLayout) row.getChildAt(6))
												.getChildAt(0))
											.getText().toString()
									 	);
									int circleId = Integer.parseInt(((TextView)row.getChildAt(1)).getText().toString());

					    			// --- Remove from db ---------------------
									if(ExerciseList.myWorkoutType == 2) {
										myDbHelper.removeExerciseFromCircle(ExerciseList.myWorkoutId, circleId, exerciseId);
									} else if (ExerciseList.myWorkoutType == 1) {
										myDbHelper.removeSingleExerciseFromWorkoutByRow(ExerciseList.myWorkoutId, ExerciseList.myWorkoutType, exerciseId, pos);
									}					    			
					    			
					    			myDbHelper.close();
					    								    			
					    			// --- Remove from view ---------------------------
					    	        items.remove(pos);
					    	        notifyDataSetChanged();
					    	        
					    			Toast.makeText(getContext(), "Deleted exercise " + exerciseId + " from circle " + circleId, Toast.LENGTH_SHORT).show();
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
					
					// --- Save changes -------------------------------------------
					Button save = (Button) v.findViewById(R.id.exerciseSaveSet);
					save.setOnClickListener(new View.OnClickListener() {
			            public void onClick(View view) {					        
							GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getContext());
							LinearLayout row = (LinearLayout)view.getParent().getParent().getParent();
							
							// --- Get weight -------------------------------------------------------------
							WeightPicker weightView = ((WeightPicker)row.getChildAt(2));
							double weightVal = weightView.getValue();

							// --- Get reps ---------------------------------------------------------------
							NumberPicker repsView = ((NumberPicker)row.getChildAt(4));
							int repsVal = repsView.getValue();
							
							// --- Get exercise id --------------------------------------------------------
						 	myDbHelper.openDataBase();
							int exerciseId = myDbHelper.retrieveExerciseIdFromName(
							 		((TextView) ((LinearLayout) row.getChildAt(6))
										.getChildAt(0))
									.getText().toString()
							 	);
						 	myDbHelper.close();		
							
							// --- Change in DB -----------------------------------------------------------
						 	// TODO error handling
							myDbHelper.openDataBaseRW();
							myDbHelper.updateSingleExerciseFromWorkoutByRow(ExerciseList.myWorkoutId, ExerciseList.myWorkoutType, exerciseId, pos, weightVal, repsVal);							
			    			myDbHelper.close();
							
							// --- Refresh view ---------------------------------------------------
						 	// TODO cant we put that into the constructor somehow?
						 	ArrayList<String> newRow = new ArrayList<String>();		
						 	newRow.add("0");
						 	newRow.add("" + weightVal);
						 	newRow.add("" + repsVal);
						 	
							if(ExerciseList.myWorkoutType == 2) {
								newRow.add("" + exerciseId);
							}
						 	
							items.set(pos, newRow);					
							notifyDataSetChanged();
							
							Log.d("debug", "Save exercise " + 
							 		((TextView) ((LinearLayout) row.getChildAt(6))
											.getChildAt(0))
										.getText().toString() + " at " + pos + " weight " + weightVal + " reps " + repsVal);
							Toast.makeText(getContext(), "Changes saved", Toast.LENGTH_SHORT).show();
			            }
					});					
				}
	
				if(Exercise.myMode == Gymlog.MODE_TRAIN) {
					// --- TRAIN MODE ---------------------------------------------------------------------------------------------------------
					// --- OnClickListener for set done ----------		
					setDone.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// TODO implement program
							int program = Exercise.myProgram;					
							int workout = Exercise.myWorkoutId;					
							int exercise = Exercise.myExerciseId;			
							int session = Exercise.mySession;
							int setNr, repVal;
							Double weightVal;
							
							LinearLayout row = (LinearLayout)arg0.getParent().getParent();
							
							// --- Retrieve values ---------
							// TODO use ids instead of numbers
							String set = (String) ((TextView) row.getChildAt(1)).getText();
							setNr = Integer.parseInt(set);						
							
							WeightPicker weight = (WeightPicker) row.getChildAt(2);
							weightVal = weight.getValue();
							
							NumberPicker reps = (NumberPicker) row.getChildAt(4);
							repVal = reps.getValue();
		
					        GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getContext());
					        myDbHelper = new GymlogDatabaseHelper(getContext());
					        			        
						 	myDbHelper.openDataBaseRW();
							
							// --- Save Values to log ----------
						 	myDbHelper.logSet(program, workout, session, exercise, setNr, weightVal, repVal);				 	
						 	myDbHelper.close();
						 	
						 	// --- Remove finished set from View ---------
						 	items.remove(pos);
						 	notifyDataSetChanged();
		
						 	// --- Update progress bar --------------------------------
						 	Exercise.updateProgress(pos);
						 	
							Toast.makeText(getContext(),
									"You have completed exercise " + exercise
									+ " Set " + set + " with "
									+ weightVal + "kg and "
									+ repVal + " reps "
									+ "in session " + Exercise.mySession,
									Toast.LENGTH_SHORT).show();					
						}
					});			
				}

			if((ExerciseList.myWorkoutType == 2) && (Integer.parseInt(mySet.get(3)) == 0)) {
				// If exercise id is 0, remove from view for now
		        items.remove(pos);
		        notifyDataSetChanged();
			}	
		} 
		
		return (v);
	}
    
    public AlertDialog makeAlert()
    {        
    	final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

    	alert.setTitle("Are you sure you want to delete this?");
    	
    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {   			
    		}
    	});
    	
		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
    	
    	return alert.create();
    }
}