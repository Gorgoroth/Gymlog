package quelltextfabrik.gymlog;

import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Gymlog extends Activity {	
	protected static final int SCHEDULE_TYPE_ALTERNATE = 1;
	protected static final int SCHEDULE_TYPE_FIXED = 2;
	
	protected static final int SCHEDULE_DAY_MO = 1;	// 0b00000001
	protected static final int SCHEDULE_DAY_TU = 2;	// 0b00000010
	protected static final int SCHEDULE_DAY_WE = 4;	// 0b00000100
	protected static final int SCHEDULE_DAY_TH = 8;	// 0b00001000
	protected static final int SCHEDULE_DAY_FR = 16;	// 0b00010000
	protected static final int SCHEDULE_DAY_SA = 32;	// 0b00100000
	protected static final int SCHEDULE_DAY_SU = 64;	// 0b01000000
	
	protected static final int SCHEDULE_DAYS[] = {
		0,					// Dummy because SUNDAY is 1
		SCHEDULE_DAY_SU,	// Week starts on sunday but we want it to start on monday
		SCHEDULE_DAY_MO,
		SCHEDULE_DAY_TU,
		SCHEDULE_DAY_WE,
		SCHEDULE_DAY_TH,
		SCHEDULE_DAY_FR,
		SCHEDULE_DAY_SA
	};

	protected static final int MODE_SETUP = 1;
	protected static final int MODE_TRAIN = 2;
	
	protected static final String PUT_MODE = "mode";
	protected static final String PUT_WORKOUT = "workout";
	protected static final String PUT_TYPE = "type";
	protected static final String PUT_PROGRAM = "program";
	protected static final String PUT_EXERCISE = "exercise";
	protected static final String PUT_CIRCLE = "circle";
	protected static final String PUT_SESSION = "session";
	
	protected static final int START_NONE = 0;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Open database
        GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(this);
        myDbHelper = new GymlogDatabaseHelper(this);
        try { 
	        myDbHelper.createDataBase();
	 	} catch (IOException ioe) {	 
	 		throw new Error("Unable to create database");
	 	}
	 	
	 	try {	 		 
	 		myDbHelper.openDataBase();	 
	 	} catch(SQLException sqle) {
	 		throw sqle;	 
	 	}
	 	
	 	myDbHelper.close();
        
        // --- Train --------------------------------------------------------------------------------------------------
        Button train = (Button) findViewById(R.id.TrainButton);
        train.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				
				// TODO check schedule
				
				// --- Get current day of week ----------------------------------------------------
				Calendar rightNow = Calendar.getInstance();
				int day = rightNow.get(Calendar.DAY_OF_WEEK);
				
				// TODO getActiveWorkoutOnSchedule
		        GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getBaseContext());
		        myDbHelper = new GymlogDatabaseHelper(getBaseContext());
		 		myDbHelper.openDataBase();
		 		int myWorkout = myDbHelper.getActiveWorkoutOnSchedule(SCHEDULE_DAYS[day]);
		 		myDbHelper.close();	
				
		 		if(myWorkout != -1) {
					Log.d("debug", "Fuck yes! Let's pump some iron! Workout " + myWorkout);

			 		myDbHelper.openDataBase();
			 		int workoutType = Integer.parseInt(myDbHelper.getWorkoutDetails(myWorkout, GymlogDatabaseHelper.KEY_TYPE));			 		
			 		int program = myDbHelper.getActiveProgramOnSchedule(SCHEDULE_DAYS[day]);
			 		myDbHelper.close();
			 		
					Intent myIntent = new Intent(v.getContext(), ExerciseList.class);
					myIntent.putExtra(PUT_MODE, MODE_TRAIN);					
					myIntent.putExtra(PUT_WORKOUT, myWorkout);
					myIntent.putExtra(PUT_TYPE, workoutType);
					myIntent.putExtra(PUT_PROGRAM, program);
					
					startActivityForResult(myIntent, START_NONE);			
		 		} else {
					Log.d("debug", "No active Workout :( Load program list");
					Intent myIntent = new Intent(v.getContext(), ProgramList.class);
					myIntent.putExtra(PUT_MODE, MODE_TRAIN);
					startActivityForResult(myIntent, START_NONE);
		 		}
				
				/*
				Log.d("debug", "Day of the week " + day + " which is our binary " + SCHEDULE_DAYS[day]);
				Intent myIntent = new Intent(v.getContext(), ProgramList.class);
				myIntent.putExtra(PUT_MODE, MODE_TRAIN);
				startActivityForResult(myIntent, START_NONE);
				*/
			}
		});
        
        // --- Analyzing ----------------------------------------------------------------------------------------------
        Button analyze = (Button) findViewById(R.id.AnalyzeButton);
        analyze.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), SessionList.class);
				startActivityForResult(myIntent, START_NONE);
			}
		});
        
        // --- Benchmark ----------------------------------------------------------------------------------------------
        Button benchmark = (Button) findViewById(R.id.BenchmarkButton);
        benchmark.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), Benchmark.class);
				startActivityForResult(myIntent, START_NONE);
			}
		});
                       
        // --- Setup --------------------------------------------------------------------------------------------------
        Button setup = (Button) findViewById(R.id.SetupButton);
        setup.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), Setup.class);
				startActivityForResult(myIntent, START_NONE);
			}
		});
    }
}