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

public class ExerciseList extends ListActivity {
    public static final int CHOOSE_EXERCISE = 1;
    public static final int EXERCISE_TRAIN = 2;
    public static final String EXERCISE = "exerciseName";
    
	private ListView mainListView = null;
	ExerciseListAdapter customListAdapter = null;
	ArrayList<String> results = new ArrayList<String>();
    GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(this);
	
	static int myMode = 0;
	static int myWorkoutId = 0;
	static int myWorkoutType = 0;
	static int myProgram = 0;
	static int mySession = 0;
	
	public static int myOverallProgress = 0;	
	static ProgressBar viewOverallProgress;
	public static int allItems = 0;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exerciselist);

		// --- Retrieve the mode we're operating on and workout id ----------
        Intent startIntent = getIntent();
		myMode = startIntent.getIntExtra(Gymlog.PUT_MODE, -1);
		myWorkoutId = startIntent.getIntExtra(Gymlog.PUT_WORKOUT, -1);
		myWorkoutType = startIntent.getIntExtra(Gymlog.PUT_TYPE, -1);
		myProgram = startIntent.getIntExtra(Gymlog.PUT_PROGRAM, -1);
		
        // --- Get list of workouts ----------
        // Open database
        myDbHelper = new GymlogDatabaseHelper(this);
        myDbHelper.openDataBase();		
		
	 	// --- Get workout list -----------------------------------------------------------------------------
		String workoutName;
	 	workoutName = myDbHelper.getWorkoutDetails(myWorkoutId, GymlogDatabaseHelper.KEY_NAME);
        myDbHelper.close();

		this.mainListView = getListView();
		
		mainListView.setCacheColorHint(0);
		
		// --- Display data ---------------------------------------------------------------------------------		
		//refreshView(); 
    	myDbHelper.openDataBase();

    	// TODO retrieve according to type
    	if(myWorkoutType == 1) {
    		results = myDbHelper.retrieveWorkout(myWorkoutId);
    	} else if (myWorkoutType == 2) {
    		results = myDbHelper.retrieveCircles(myWorkoutId);
    	}
    	else {
    		Log.d("debug", "Nix");
    	}
		customListAdapter = new ExerciseListAdapter(ExerciseList.this, R.layout.exerciselistitem, results);
		mainListView.setAdapter(customListAdapter);
		
        myDbHelper.close();
		
		// --- Display stuff ---------
		// TODO find meaningful description
        TextView modeText = (TextView) findViewById(R.id.exerciselistMode);
        TextView workoutText = (TextView) findViewById(R.id.exerciselistWorkoutname);
        
        if(myMode == 1) {        	
        	// --- Setup mode -----------------------------------------------------------------------------------------        	
        	// --- Add Exercise or circle ---------------------------------------------------------
        	// --- Show add button --------------------------------------------
            Button addButton = (Button) findViewById(R.id.exerciseListAdd);
            addButton.setVisibility(View.VISIBLE);
            
            // --- Add exercise onClick ----------------------------------------
            addButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	            	if(myWorkoutType == 1) {
						Intent myIntent = new Intent(view.getContext(), ChooseExercise.class);
						startActivityForResult(myIntent, CHOOSE_EXERCISE);
	            	} else if (myWorkoutType == 2) {
	                	final AlertDialog alert =  makeAlert();
	                	alert.show();
	            	}
	            }
	        });
            
        	LinearLayout progress = (LinearLayout) findViewById(R.id.exerciseProgressContainer);
        	progress.setVisibility(View.GONE);
                                    
            // --- Set Title ----------------------------------------------------------------------
        	modeText.setText("Add or modify workouts");    	
        } else if(myMode == 2) {
        	// --- Trainmode ----------------------------------------------------------------------
        	myDbHelper.openDataBase();
        	
    		// --- Count all items in workout -----------------------------------------------------
    		allItems = myDbHelper.retrieveAllExerciseFromWorkout(myWorkoutId).size();	// TODO do this without retrieving the whole workout    		
    		Log.d("debug", "All items " + allItems + " current progress " + myOverallProgress);
    		
    		// --- Generate session id-------------------------------------------------------------
    		mySession = myDbHelper.sessionGenerate();
    		
        	myDbHelper.close();

            // --- Initialize progress bar ------------------------------------
    		viewOverallProgress = (ProgressBar) findViewById(R.id.exerciseListOverallProgress);
    		myOverallProgress = 0;
    		
    		updateProgress(0);
    		
    		// --- TODO Stats -------------------------------------------------
        	modeText.setText("Choose your exercise");
        	workoutText.setText(workoutName);        	
        } else {        	
        	// --- Undefined mode ---------------------------------------------
        	modeText.setText("Invalid workout mode, please send me hatemail");     
        }        
        
        // --- Back ----------
        Button back = (Button) findViewById(R.id.Back);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        
        // --- Done ----------
        // TODO Save all
        Button done = (Button) findViewById(R.id.Done);
        done.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), Gymlog.class);
				startActivityForResult(myIntent, 0);
			}
		});
    }    
    
    void refreshView()
    {
	    GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(this);	
        myDbHelper = new GymlogDatabaseHelper(this);
    	ArrayList<String> results = new ArrayList<String>(); 
    	myDbHelper.openDataBase();

	 	results = myDbHelper.retrieveWorkout(myWorkoutId);
		customListAdapter = new ExerciseListAdapter(ExerciseList.this, R.layout.exerciselistitem, results);
		mainListView.setAdapter(customListAdapter);
		
        myDbHelper.close();
    }
    
    public static void updateProgress(int listPos)
    {
    	float progressFloat = ((float)myOverallProgress/(float)allItems) * 100;    	
    	if(myOverallProgress > allItems) { myOverallProgress = 0; }    	
    	int progress = (int) progressFloat;    	
        viewOverallProgress.setProgress(progress);
        
        Log.d("debug", "listPos " + listPos + " itemProgress " + listPos + " allItems " + allItems + " progress " + progress);
    }
    
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{     
		super.onActivityResult(requestCode, resultCode, data); 
		switch(requestCode) { 
			case (CHOOSE_EXERCISE) : { 
				if (resultCode == Activity.RESULT_OK) { 				
				    GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(this);	
			        myDbHelper = new GymlogDatabaseHelper(this);
			        
					// --- Save to DB -----------------------------------------
					String myExercise = data.getStringExtra(EXERCISE);
        			myDbHelper.openDataBaseRW();
        			myDbHelper.addExerciseToWorkout(myWorkoutId, myExercise);
        			myDbHelper.close();
        			
					// --- Reload View ----------------------------------------
					results.add(myExercise);
	    			customListAdapter.notifyDataSetChanged();
        			        			
					Toast.makeText(getApplicationContext(), myExercise, Toast.LENGTH_SHORT).show();
				} 
				break; 
			}
			case (EXERCISE_TRAIN) : {
				if (resultCode == Activity.RESULT_OK) {  
					if(myMode == 2) {
						int exerciseProgress = data.getIntExtra("overallprogress", -1);
						String exercise = data.getStringExtra("exerciseName");
						if(exerciseProgress != -1) {
							myOverallProgress += exerciseProgress;
							updateProgress(0);
						} // TODO error handling
		      			
						// --- Reload View ----------------------------------------
						results.remove(exercise);
		    			customListAdapter.notifyDataSetChanged();
						
						Toast.makeText(getApplicationContext(), "Checked items " + myOverallProgress, Toast.LENGTH_SHORT).show();
					}
				} 
				break;
			}
		} 
	}

    
    public AlertDialog makeAlert()
    {
    	final AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	final EditText input = new EditText(this);
    	alert.setView(input);

    	alert.setTitle("Enter new circle name");	// TODO Use constants
    	
    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {    			
    			// --- Get them views -------------------------------------------------------------
                String name = input.getText().toString().trim();
    			
    			// --- Save to DB -----------------------------------------------------------------
    			myDbHelper.openDataBaseRW();
    			myDbHelper.addNewCircle(myWorkoutId, name);
    			myDbHelper.close();    			
    			
    			// --- Refresh view ---------------------------------------------------------------
    			results.add(name);
    			customListAdapter.notifyDataSetChanged();
    			
        		Toast.makeText(getApplicationContext(), "New exercise " + name, Toast.LENGTH_SHORT).show();        			
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