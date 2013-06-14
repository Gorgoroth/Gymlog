package quelltextfabrik.gymlog;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class WorkoutList extends ListActivity {
	protected static final int CHOOSE_WORKOUT = 1;
	private ListView mainListView = null;
	WorkoutListAdapter customListAdapter = null;
    GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(this);
	static int myMode = 0;
	static int myProgram = 0;
	ArrayList<ArrayList<String>> results;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workoutlist);

		// --- Retrieve the mode we're operating on -------------------------------------------------------------------
        // TODO use integer constants MODE_SETUP = 1, MODE_TRAIN = 2 etc
		// TODO use string values for setText
        Intent startIntent = getIntent();
		myMode = startIntent.getIntExtra("mode", 0);
		myProgram = startIntent.getIntExtra("program", 0);
            
        // --- Get list of workouts -----------------------------------------------------------------------------------
        // Open database
    	results = new ArrayList<ArrayList<String>>(); 
    	
	 	myDbHelper.openDataBase();
	 	
	 	// --- Get workout list ----------
	 	results = myDbHelper.retrieveWorkoutsFromProgram(myProgram);

		this.mainListView = getListView();		
		mainListView.setCacheColorHint(0);
		// --- Bind the data with the list -------
		this.customListAdapter = new WorkoutListAdapter(WorkoutList.this, R.layout.workoutlistitem, results);
		mainListView.setAdapter(this.customListAdapter);

		
        TextView modeText = (TextView) findViewById(R.id.workoutlistMode);
        
        if(myMode == 1) {
        	// --- Setup mode -------------------------------------------------------------------------------        	
        	// --- Add Workout --------------------------------------------------------------------
        	final AlertDialog alert =  makeAlert();
        	
        	// --- Show add button --------------------------------------------
            Button addButton = (Button) findViewById(R.id.workoutListAdd);
            addButton.setVisibility(View.VISIBLE);
            
            // --- Add workout onClick ----------------------------------------
            addButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	            	if(myProgram != 0) {
	    				Intent myIntent = new Intent(view.getContext(), ChooseWorkout.class);
	    				startActivityForResult(myIntent, CHOOSE_WORKOUT);
	            	} else {
	            		alert.show();
	            	}
	        		
	            }
	        });           
                                    
            // --- Set Title ----------------------------------------------------------------------
        	modeText.setText("Add or modify workouts");
        	
        } else if(myMode == 2) {
        	// --- Trainmode --------------------------------------------------------------------------------
        	modeText.setText("Choose workout");
        	
        } else {        	
        	// --- Undefined mode ---------------------------------------------------------------------------
        	modeText.setText("Invalid workout mode, please send me hatemail");     
        }
		
        myDbHelper.close();
        
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
				Intent myIntent = new Intent(v.getContext(), Gymlog.class);
				startActivityForResult(myIntent, 0);
			}
		});
    }
    
    public AlertDialog makeAlert()
    {
    	final AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	Context mContext = getApplicationContext();
    	LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
    	final View layout = inflater.inflate(R.layout.newworkoutdialog, (ViewGroup) findViewById(R.id.newWorkoutDialogRoot));
    	
    	alert.setTitle("Enter new workout");
    	alert.setView(layout);
    	
    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    			int type = 0;
    			
    			// --- Get them views ---------------------------------------------------------
    			String name = ((TextView)layout.findViewById(R.id.newWorkoutName)).getText().toString();
    			RadioButton setBased = (RadioButton)layout.findViewById(R.id.newWorkoutSetbased);
    			RadioButton circle = (RadioButton)layout.findViewById(R.id.newWorkoutCircle);
    			RadioButton list = (RadioButton)layout.findViewById(R.id.newWorkoutList);
    			
    			// --- Choose type according to checked button --------------------------------
    			if(setBased.isChecked()) { 		type = 1;
    			} else if (circle.isChecked()){ type = 2;
    			} else if (list.isChecked()) { 	type = 3; }
    			
    			// --- Save to DB -------------------------------------------------------------
    			myDbHelper.openDataBaseRW();
    			myDbHelper.addNewWorkout(name, type);
    			myDbHelper.close();    			
    			
    			// --- Refresh view ---------------------------------------------------------------
    			ArrayList<String> row = new ArrayList<String>();
    			row.add(name);
    			row.add("" + type);
    			
    			results.add(row);
    			customListAdapter.notifyDataSetChanged();     			
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
    
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{     
		super.onActivityResult(requestCode, resultCode, data); 
		switch(requestCode) { 
			case (CHOOSE_WORKOUT) : { 
				if (resultCode == Activity.RESULT_OK) { 		
			        // --- Retrieve data --------------------------------------
					int workoutId = data.getIntExtra("workout", -1);
					int workoutType = data.getIntExtra("type", -1);
					String workoutName = data.getStringExtra("workoutName");
					
					// --- Save to DB -----------------------------------------		
				    GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(this);	
			        myDbHelper = new GymlogDatabaseHelper(this);
        			myDbHelper.openDataBaseRW();
        			myDbHelper.addWorkoutToProgram(myProgram, workoutId);
        			myDbHelper.close();
        			
					// --- Reload View ----------------------------------------
        			ArrayList<String> row = new ArrayList<String>();
        			row.add(workoutName);
        			row.add("" + workoutType);
					results.add(row);
	    			customListAdapter.notifyDataSetChanged();
        			        			
					Toast.makeText(getApplicationContext(), workoutName, Toast.LENGTH_SHORT).show();
				} 
				break; 
			}
		} 
	}
}