package quelltextfabrik.gymlog;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class WholeWorkout extends ListActivity {
    public static final int CHOOSE_EXERCISE = 1;
    public static final String EXERCISE = "exerciseName";
	private ListView mainListView = null;
	WholeWorkoutAdapter customListAdapter = null;
	public static int myMode = 0;
	public static int myWorkoutId = 0;
	public static int mySession = 0;
	public static int allItems = 0;
	public static int myWorkoutType = 0;
	ArrayList<ArrayList<String>> myWholeWorkout;
	public static int myProgress = 0;
	
	static ProgressBar mProgress;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        requestWindowFeature(Window.FEATURE_PROGRESS);        
        setContentView(R.layout.wholeworkout);
        
        // --- Initialize progress bar ------------------------------------------------------------
        mProgress = (ProgressBar) findViewById(R.id.wholeWorkoutProgress);
        myProgress = 0;
             	 			
		// --- Retrieve the mode we're operating on and workout id --------------------------------
        // TODO use integer constants MODE_SETUP = 1, MODE_TRAIN = 2 etc
        Intent startIntent = getIntent();
		myMode = startIntent.getIntExtra("mode", 0);			// TODO use constant for strings
		myWorkoutId = startIntent.getIntExtra("workout", -1);	// TODO use constant for strings
    	// --- Setup mode ---------------------------------------------------------------------
		myWorkoutType = startIntent.getIntExtra("type", -1);	// TODO use constant for strings
		
	 	// --- Get workout list -------------------------------------------------------------------
		myWholeWorkout = new ArrayList<ArrayList<String>>();
        GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(this);
        myDbHelper = new GymlogDatabaseHelper(this);        
        myDbHelper.openDataBase();
		myWholeWorkout = myDbHelper.retrieveWholeWorkout(myWorkoutId);
		
		// Generate session if mode == MODE_TRAIN
		if(myMode == 2) {
			mySession = myDbHelper.sessionGenerate();
			allItems = myWholeWorkout.size();
		}

		// --- Bind data to listview --------------------------------------------------------------
		this.mainListView = getListView();
		
		mainListView.setCacheColorHint(0);
		this.customListAdapter = new WholeWorkoutAdapter(this, R.layout.wholeworkoutitem, myWholeWorkout);
		customListAdapter.setNotifyOnChange(true);
		mainListView.setItemsCanFocus(true);
		mainListView.setAdapter(this.customListAdapter);
		
		
		// --- OnClickListener for set done ----------
		/*
		Button setDone = (Button)mainListView.findViewById(R.id.wholeWorkoutDone);			
		setDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			    ViewHolder deleteHolder = (ViewHolder) arg0.getTag();
				myListener(arg0);
			}
		});
		mainListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		        Log.d("debug", view.toString());				
			}
		});*/

		// --- Fill title bar with info -------------------------------------------------------------------------------
		String workoutName = myDbHelper.getWorkoutDetails(myWorkoutId, GymlogDatabaseHelper.KEY_NAME);
        myDbHelper.close();

        TextView modeText = 	(TextView) findViewById(R.id.wholeWorkoutMode);
        TextView workoutText = 	(TextView) findViewById(R.id.wholeWorkoutWorkoutName);
        TextView sessionText = 	(TextView) findViewById(R.id.wholeWorkoutSession);
        
        // --- Mode specific controls ---------------------------------------------------------------------------------
        if(myMode == 1) {
        	modeText.setText("Add or modify sets");
        	LinearLayout ll = (LinearLayout)findViewById(R.id.wholeWorkoutProgramInfo);        	
        	ll.setVisibility(View.GONE);
        	Button add = (Button)findViewById(R.id.wholeWorkoutAdd);
        	add.setVisibility(View.VISIBLE);

        	// TODO click handler to edit workout
        	
        	// TODO click handler to add exercise            
            // --- Add exercise onClick ----------------------------------------
            add.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
					Intent myIntent = new Intent(view.getContext(), ChooseExercise.class);
					startActivityForResult(myIntent, CHOOSE_EXERCISE);
	            }
	        });
        } else if(myMode == 2) {
        	// --- Trainmode ----------------------------------------------------------------------
        	// TODO click handler to start exercise
        	modeText.setText("Exercise!");
        	workoutText.setText(workoutName); 
        	sessionText.setText("Session " + mySession);
        } else {        	
        	// --- Undefined mode -----------------------------------------------------------------
        	modeText.setText("Invalid workout mode, please send me hatemail");     
        }
        
        // --- CONTROLS -----------------------------------------------------------------------------------------------
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
				// TODO cool down activity
				Intent myIntent = new Intent(v.getContext(), Gymlog.class);
				startActivityForResult(myIntent, 0);
			}
		});
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
        			ArrayList<String> row = new ArrayList<String>();
        			row.add("" + myWorkoutId);
        			row.add("" + 0);
        			row.add("" + 0);
        			
        			myWholeWorkout.add(row);
        			customListAdapter.notifyDataSetChanged();
        			
					Toast.makeText(getApplicationContext(), myExercise, Toast.LENGTH_SHORT).show();
				}
				break; 
			}
		} 
	}
    
    public static void updateProgress(int listPos)
    {
    	float progressFloat = ((float)++myProgress/(float)allItems) * 100;
    	
    	if(myProgress > allItems) { myProgress = 1; }
    	
    	int progress = (int) progressFloat;
    	
        mProgress.setProgress(progress);
        
        Log.d("debug", "listPos " + listPos + " itemProgress " + listPos + " allItems " + allItems + " progress " + progress);
    }
    
    
	
	/*
	public void myListener(View arg0)
	{
		GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getBaseContext());
        myDbHelper = new GymlogDatabaseHelper(getBaseContext());
        
		// TODO implement program
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
	 	
		String set = ((TextView)
						((LinearLayout)
							((LinearLayout) row.getChildAt(1))
							.getChildAt(1))
						.getChildAt(1))
					.getText().toString();
		int setNr = Integer.parseInt(set);			

		int weight = ((NumberPicker)((LinearLayout) row.getChildAt(0)).getChildAt(0)).getValue();
		int reps = ((NumberPicker)((LinearLayout) row.getChildAt(0)).getChildAt(2)).getValue();
		
		//updateProgress(position);
		
		Toast.makeText(getBaseContext(),
				"You have completed exercise " + exercise
				+ " Set " + setNr + " with "
				+ weight + "kg and "
				+ reps + " reps "
				+ "in session " + session
				//+ " position " + position
				,
				Toast.LENGTH_SHORT).show();					
        
	 	myDbHelper.openDataBaseRW();	 	
	 	
		// --- Save Values to log ----------
	 	myDbHelper.logSet(program, workout, session, exercise, setNr, weight, reps);				 	
	 	myDbHelper.close();	
	 	
	 	row.setVisibility(View.INVISIBLE);
	}*/
}