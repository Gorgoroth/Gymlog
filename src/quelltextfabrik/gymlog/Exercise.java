package quelltextfabrik.gymlog;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Exercise extends ListActivity {
    public static final int CHOOSE_EXERCISE = 1;
    
	private ListView mainListView = null;
	ExerciseAdapter customListAdapter = null;	
	ArrayList<ArrayList<String>> myExercises = new ArrayList<ArrayList<String>>();
	
	
	public static String headline;

	public static int myMode = 0;
	public static int myWorkoutId = 0;
	public static int mySession = 0;
	public static int myProgram = 0;
	
	public static int myExerciseId = 0;
	public static int myCircleId = 0;
	
	public static int myProgress = 0;	
	static ProgressBar mProgress;
	public static int allItemsInExercise = 0;
	
	static ProgressBar viewOverallProgress;
	public static int overAllProgress = 0;
	public static int overAllItems = 0;
	
    GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(this);
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise);
                        
        // --- Get list of workouts -----------------------------------------------------------------------------------
        // Open database
        myDbHelper = new GymlogDatabaseHelper(this);
        myDbHelper.openDataBase();
	 			
		// --- Retrieve the mode we're operating on and workout id ----------------------------------------------------
        Intent startIntent = getIntent();
		myMode = startIntent.getIntExtra(Gymlog.PUT_MODE, -1);
		myProgram = startIntent.getIntExtra(Gymlog.PUT_PROGRAM, -1);
		myWorkoutId = startIntent.getIntExtra(Gymlog.PUT_WORKOUT, -1);
		mySession = startIntent.getIntExtra(Gymlog.PUT_SESSION, -1);

		myExercises = new ArrayList<ArrayList<String>>();
		
		// --- Load data according to workout type --------------------------------------------------------------------
		if(ExerciseList.myWorkoutType == 1) {
			
			myExerciseId = startIntent.getIntExtra("exercise", -1);	// TODO use constant for strings
			headline = myDbHelper.retrieveExerciseDetail(myExerciseId, GymlogDatabaseHelper.KEY_NAME);
			myExercises = myDbHelper.retrieveExerciseFromWorkout(myExerciseId, myWorkoutId);
			

			// --- Auto increment the weight ----------------------------------
			// TODO check weight increase mode
			Double lastWeight = myDbHelper.getLatestSessionWeight(ExerciseList.myProgram, myExerciseId);
			if(lastWeight != -1) {
				Double weightInc = myDbHelper.getExerciseWeightIncrease(myWorkoutId, myExerciseId);
				if(weightInc != -1) {
					Double newWeight = lastWeight + weightInc;					
					ArrayList<String> row = new ArrayList<String>();
					
					for (int i = 0; i < myExercises.size(); i++) {
						row = myExercises.get(i);
						row.set(1, "" + newWeight);
						myExercises.set(i, row);
					}
					
					Log.d("debug", headline + " last time with " + lastWeight + " kg, now " + newWeight + " kg");
				}
			}
			
		} else if(ExerciseList.myWorkoutType == 2) {
			myCircleId = startIntent.getIntExtra("circle", -1);		// TODO use constant for strings
			headline = myDbHelper.retrieveCircleName(myCircleId);
			
			// --- Setup ---------
			if(myMode == 1) {
		        myDbHelper.close();
		        myDbHelper.openDataBaseRW();
				myDbHelper.prepareCircleForSetup(myWorkoutId, myCircleId);
		        myDbHelper.close();
		        myDbHelper.openDataBase();
			}
			
			myExercises = myDbHelper.retrieveCircle(myWorkoutId, myCircleId);
		}
		
				
	 	// --- Set list view with data --------------------------------------------------------------------------------
		this.mainListView = getListView();
		
		mainListView.setCacheColorHint(0);
		
		// --- bind the data with the list ----------------------------------------------------------------------------
		this.customListAdapter = new ExerciseAdapter(this, R.layout.exerciseitem, myExercises);
		mainListView.setAdapter(this.customListAdapter);

		// --- Get workout and exercise names -------------------------------------------------------------------------
		String workoutName = myDbHelper.getWorkoutDetails(myWorkoutId, GymlogDatabaseHelper.KEY_NAME);
		
        myDbHelper.close();

		// --- Find textviews -----------------------------------------------------------------------------------------
        TextView exerciseText =	(TextView) findViewById(R.id.exerciseName);
        TextView modeText = 	(TextView) findViewById(R.id.exerciseMode);

    	exerciseText.setText(headline);
    	
        // TODO use integer constants MODE_SETUP = 1, MODE_TRAIN = 2 etc
		// TODO use string values for setText
        if(myMode == 1) {
        	// --- SETUP MODE -------------------------------------------------------------------------------------------------------------
        	modeText.setText("Add or modify sets");
        	
        	// --- Hide progress bar ----------------------------------------------------------------------------------
        	LinearLayout progress = (LinearLayout) findViewById(R.id.exerciseProgressContainer);
        	progress.setVisibility(View.GONE);
        	
        	// --- Hide display ---------------------------------------------------------------------------------------
        	LinearLayout display = (LinearLayout) findViewById(R.id.exerciseInfo);
        	display.setVisibility(View.GONE);
        	
        	// --- Show add set ---------------------------------------------------------------------------------------
        	LinearLayout AddSetView = (LinearLayout) findViewById(R.id.exerciseAddSet);
        	AddSetView.setVisibility(View.VISIBLE);
        	
        	// --- Get add set button ---------------------------------------------------------------------------------
        	Button addSet = (Button) findViewById(R.id.exerciseAdd);

            // --- Add exercise onClick -------------------------------------------------------------------------------
        	addSet.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	            	if(ExerciseList.myWorkoutType == 1) {
	            		// --- Add set ------------------------------------------------------------
						// --- Save to DB -------------------------------------
	                    GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getBaseContext());
	                    myDbHelper = new GymlogDatabaseHelper(getBaseContext());
	        			myDbHelper.openDataBaseRW();
	        			myDbHelper.addExerciseToWorkout(myWorkoutId, myDbHelper.retrieveExerciseDetail(myExerciseId, GymlogDatabaseHelper.KEY_NAME));
	        			myDbHelper.close();
	        			
						// --- Reload View ----------------------------------------
				        ArrayList<String> row = new ArrayList<String>();
						row.add("0");
						row.add("0");
						row.add("0");					
						row.add("" + myExerciseId);
	   
	        			myExercises.add(row);        			
		    			customListAdapter.notifyDataSetChanged();
	        			
	            	} else if (ExerciseList.myWorkoutType == 2) {
	                	// --- Add exercise to circle -------------------------
						Intent myIntent = new Intent(view.getContext(), ChooseExercise.class);
						startActivityForResult(myIntent, CHOOSE_EXERCISE);
	            	}
	            }
            });
        	
        } else if(myMode == 2) {
        	// --- TRAIN MODE -------------------------------------------------------------------------------------------------------------       	
            TextView workoutText = 	(TextView) findViewById(R.id.exerciseWorkoutName);
            TextView sessionText = 	(TextView) findViewById(R.id.exerciseSession);

            // --- Initialize progress bar ------------------------------------
            mProgress = (ProgressBar) findViewById(R.id.exerciseProgress);
            myProgress = 0;
            viewOverallProgress = (ProgressBar) findViewById(R.id.exerciseOverallProgress);
    		overAllProgress = startIntent.getIntExtra("overallprogress", -1);
    		overAllItems = startIntent.getIntExtra("allitems", -1);
        	allItemsInExercise = myExercises.size();
    		
    		Log.d("debug", "overAllProgress " + overAllProgress + " overAllItems " + overAllItems);
    		Log.d("debug", "myProgress " + myProgress + " allItemsInExercise " + allItemsInExercise);
    		
    		 // ---  
    		myProgress--;		// We just want to get the overall progress, but not influence it
    		overAllProgress--;	// We increment in updateProgress
            updateProgress(0);
    		
            // --- TODO Statistics --------------------------------------------
        	modeText.setText("Exercise!");
        	workoutText.setText(workoutName);
        	sessionText.setText("Session " + mySession);
        	
        } else {        	
        	// --- Undefined mode -----------------------------------------------------------------
        	modeText.setText("Invalid workout mode, please send me hatemail");     
        }
        
        // --- CONTROLS -----------------------------------------------------------------------------------------------------------------------------
        // --- Back -------------------------------------------------------------------------------
        Button back = (Button) findViewById(R.id.Back);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        
        // --- Done -------------------------------------------------------------------------------
        Button done = (Button) findViewById(R.id.Done);
        done.setOnClickListener(new View.OnClickListener() {			
			@Override
            public void onClick(View view) {
                Intent intent = new Intent();
                
                if(myMode == 1) {
                	if(ExerciseList.myWorkoutType == 2) {
		            	AlertDialog alert = makeAlert();
		            	alert.show();
                	}
                }
                
                if(myMode == 2) {
	                intent.putExtra("overallprogress", myProgress);
	                intent.putExtra("exercise", myExerciseId);
	                intent.putExtra("exerciseName", headline);
                }

                if(myMode != 1) {
	                setResult(RESULT_OK, intent);
	                finish();                	
                	Log.d("debug", "Done");
                }
            }
		});
    }
    
    public static void updateProgress(int listPos)
    {
    	float progressFloat = ((float)++myProgress/(float)allItemsInExercise) * 100;    	
    	if(myProgress > allItemsInExercise) { myProgress = 1; }    	
    	int progress = (int) progressFloat;
    	
    	float overallProgressFloat = ((float)++overAllProgress/(float)overAllItems) * 100;  
    	int overallProgress = (int)overallProgressFloat;
    	
        mProgress.setProgress(progress);        
        viewOverallProgress.setProgress(overallProgress);
        
        Log.d("debug", "Items in exercise: " + allItemsInExercise + " Exercise progress items: " + myProgress + " in percent: " + progress);
        Log.d("debug", "Items in workout : " + overAllItems + " Workout progress items : " + overAllProgress + " in percent: " + overallProgress);
    }


	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{     
		super.onActivityResult(requestCode, resultCode, data); 
		switch(requestCode) { 
			case (CHOOSE_EXERCISE) : { 
				if (resultCode == Activity.RESULT_OK) {
			        ArrayList<String> row = new ArrayList<String>();
					String exerciseName = data.getStringExtra("exerciseName");
			        
					// --- Save to DB -----------------------------------------
				    GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(this);	
			        myDbHelper = new GymlogDatabaseHelper(this);
        			myDbHelper.openDataBaseRW();
        			
        			// Delete exercise with exercise ID 0
        			myDbHelper.removeExerciseFromCircle(myWorkoutId, myCircleId, 0);        			
        			myDbHelper.addExerciseToCircle(myWorkoutId, myCircleId, myDbHelper.retrieveExerciseIdFromName(exerciseName));
        			
					// --- Reload View ----------------------------------------
					row.add("" + myCircleId);
					row.add("0");
					row.add("0");					
					row.add("" + myDbHelper.retrieveExerciseIdFromName(exerciseName));
					
        			myDbHelper.close();
   
        			myExercises.add(row);        			
	    			customListAdapter.notifyDataSetChanged();
        			        			
					Toast.makeText(getApplicationContext(), exerciseName, Toast.LENGTH_SHORT).show();
				} 
				break; 
			}			
		} 
	}	

    public AlertDialog makeAlert()
    {
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	final EditText input = new EditText(this);
    	alert.setView(input);

    	alert.setTitle("How often should the circle repeat?");	// TODO Use constants
    	
    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {    			
    			// --- Get them views -------------------------------------------------------------
                int reps = Integer.parseInt(input.getText().toString().trim());

    			myDbHelper.openDataBaseRW();
                myDbHelper.copyExercisesInCircle(myWorkoutId, myCircleId, reps);
    			myDbHelper.close();
    			
        		Toast.makeText(getApplicationContext(), "Circle should repeat ", Toast.LENGTH_SHORT).show(); 

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();  			
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