package quelltextfabrik.gymlog;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Benchmark extends Activity {
	
	private TextView benchmarkDate;	// For birth date picker
    private Button pickDate;
    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;

    static final int DATE_DIALOG_ID = 0;
    
    public static final int CHOOSE_EXERCISE_1 = 1;
    public static final int CHOOSE_EXERCISE_2 = 2;
    public static final int CHOOSE_EXERCISE_3 = 3;
    public static final int CHOOSE_EXERCISE_4 = 4;
    
    /*
    private int exerciseId1;
    private int exerciseId2;
    private int exerciseId3;
    private int exerciseId4;
    */

	Button benchmark1Button;
	Button benchmark2Button;
	Button benchmark3Button;
	Button benchmark4Button;
	EditText benchmark1Value;
	EditText benchmark2Value;
	EditText benchmark3Value;
	EditText benchmark4Value;
    
    private String exerciseString1;
    private String exerciseString2;
    private String exerciseString3;
    private String exerciseString4;
    
    public static final String EXERCISE = "exerciseName";
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.benchmark);
        
        benchmark1Button = (Button) findViewById(R.id.BenchmarkExercise1);
        benchmark2Button = (Button) findViewById(R.id.BenchmarkExercise2);
        benchmark3Button = (Button) findViewById(R.id.BenchmarkExercise3);
        benchmark4Button = (Button) findViewById(R.id.BenchmarkExercise4);
        
        benchmark1Value = (EditText) findViewById(R.id.BenchmarkValue1);
        benchmark2Value = (EditText) findViewById(R.id.BenchmarkValue2);
        benchmark3Value = (EditText) findViewById(R.id.BenchmarkValue3);
        benchmark4Value = (EditText) findViewById(R.id.BenchmarkValue4);
                        
        // --- Load last benchmark values or default ----------
        GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getBaseContext());
        myDbHelper = new GymlogDatabaseHelper(getBaseContext());
        
	 	try { myDbHelper.openDataBaseRW();	 
	 	} catch(SQLException sqle) { throw sqle; }
	 	
        ArrayList<String> lastBenchmark = myDbHelper.getLatestBenchmark();
        
        if(lastBenchmark.size() != 0) {
			Toast.makeText(getApplicationContext(), lastBenchmark.toString(), Toast.LENGTH_SHORT).show();
		    
			// Load weight
			((EditText)findViewById(R.id.WeightValue)).setText(lastBenchmark.get(2));
			
			// Load fat
			((EditText)findViewById(R.id.BodyFatValue)).setText(lastBenchmark.get(3));
			
			// Load exercises
		    benchmark1Button.setText(myDbHelper.retrieveExerciseDetail(Integer.parseInt(lastBenchmark.get(4)), GymlogDatabaseHelper.KEY_NAME));
		    benchmark1Value.setText(lastBenchmark.get(5));
		    
		    benchmark2Button.setText(myDbHelper.retrieveExerciseDetail(Integer.parseInt(lastBenchmark.get(6)), GymlogDatabaseHelper.KEY_NAME));
		    benchmark2Value.setText(lastBenchmark.get(7));
		    
		    benchmark3Button.setText(myDbHelper.retrieveExerciseDetail(Integer.parseInt(lastBenchmark.get(8)), GymlogDatabaseHelper.KEY_NAME));
		    benchmark3Value.setText(lastBenchmark.get(9));
		    
		    benchmark4Button.setText(myDbHelper.retrieveExerciseDetail(Integer.parseInt(lastBenchmark.get(10)), GymlogDatabaseHelper.KEY_NAME));		    
		    benchmark4Value.setText(lastBenchmark.get(11));
        }
        
        // --- Date controls -----------------------------------------------------------------------------------------------------------
        // TODO Load todays date
        long dateTimestamp = System.currentTimeMillis();
        
        benchmarkDate = (TextView) findViewById(R.id.BenchmarkDateValue);
        pickDate = (Button) findViewById(R.id.BenchmarkDatePicker);
        
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateTimestamp);        
        mYear = cal.get(Calendar.YEAR);
        mDay = cal.get(Calendar.DAY_OF_MONTH);
        mMonth = cal.get(Calendar.MONTH);
                
        updateDisplay();	// Updates the TextView
        
        // add a click listener to the button
        pickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        
        // --- Exercises onClick bindings ----------------------------------------------------------------------------------------------        
        // --- Choose exercise 1 ----------
        benchmark1Button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), ChooseExercise.class);
				startActivityForResult(myIntent, CHOOSE_EXERCISE_1);
			}
        });
        
        // --- Choose exercise 2 ----------
        benchmark2Button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), ChooseExercise.class);
				startActivityForResult(myIntent, CHOOSE_EXERCISE_2);
			}
        });
        
        // --- Choose exercise 3 ----------
        benchmark3Button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), ChooseExercise.class);
				startActivityForResult(myIntent, CHOOSE_EXERCISE_3);
			}
        });
        
        // --- Choose exercise 4 ----------
        benchmark4Button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), ChooseExercise.class);
				startActivityForResult(myIntent, CHOOSE_EXERCISE_4);
			}
        });
        
        // --- Controls --------------------------------------------------------------------------------------------------------------------
        // --- Back ----------
        Button back = (Button) findViewById(R.id.BenchmarkBack);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        // --- Continue ----------
        Button done = (Button) findViewById(R.id.BenchmarkDone);
        done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	ArrayList<String> myBenchmark = new ArrayList<String>();
            	String val;
            	Integer id;

            	// TODO --- Retrieve date ----------
		        Calendar cal = Calendar.getInstance();
		        cal.set(mYear, mMonth, mDay); 
		        Long saveStamp = cal.getTimeInMillis();
		        myBenchmark.add("" + saveStamp);
		        
            	// --- Retrieve body values
		        // Weight
		        val = ((EditText)findViewById(R.id.WeightValue)).getText().toString();
		        myBenchmark.add(val);
		        
		        // BodyFat
		        val = ((EditText)findViewById(R.id.BodyFatValue)).getText().toString();
		        myBenchmark.add(val);
		        
		        // --- Retrieve exercises 
		        GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getBaseContext());
		        myDbHelper = new GymlogDatabaseHelper(getBaseContext());
		        
			 	try { myDbHelper.openDataBaseRW();	 
			 	} catch(SQLException sqle) { throw sqle; }
			 	
		        // 1
		        val = ((Button)findViewById(R.id.BenchmarkExercise1)).getText().toString();
		        id = myDbHelper.retrieveExerciseIdFromName(val);
		        myBenchmark.add(id.toString());
		        val = ((EditText)findViewById(R.id.BenchmarkValue1)).getText().toString();
		        myBenchmark.add(val);
		        
		        // 2
		        val = ((Button)findViewById(R.id.BenchmarkExercise2)).getText().toString();
		        id = myDbHelper.retrieveExerciseIdFromName(val);
		        myBenchmark.add(id.toString());
		        val = ((EditText)findViewById(R.id.BenchmarkValue2)).getText().toString();
		        myBenchmark.add(val);
		        
		        // 3
		        val = ((Button)findViewById(R.id.BenchmarkExercise3)).getText().toString();
		        id = myDbHelper.retrieveExerciseIdFromName(val);
		        myBenchmark.add(id.toString());
		        val = ((EditText)findViewById(R.id.BenchmarkValue3)).getText().toString();
		        myBenchmark.add(val);
		        
		        // 4
		        val = ((Button)findViewById(R.id.BenchmarkExercise4)).getText().toString();
		        id = myDbHelper.retrieveExerciseIdFromName(val);
		        myBenchmark.add(id.toString());
		        val = ((EditText)findViewById(R.id.BenchmarkValue4)).getText().toString();
		        myBenchmark.add(val);
		        
                // --- Save values ----------			 	
		        myDbHelper.newBenchmark(myBenchmark);
		        myDbHelper.close();		        
				Toast.makeText(getApplicationContext(), myBenchmark.toString(), Toast.LENGTH_LONG).show();
		        
		        // --- TODO post to facebook and twitter ----------
            	
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
    // --- Date related -------------------------------------------------------------------------------------------------------------------------------
    private void updateDisplay() {
    	benchmarkDate.setText(
            new StringBuilder()                    
            		.append(mDay).append(".")
                    .append(mMonth+1).append(".")	// Month is 0 based so add 1
                    .append(mYear));
    				// TODO figure this "Month is 0 based so add 1" stuff out so we can use real timestamps
    }
    
    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
            };
            
	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
		    case DATE_DIALOG_ID:
		        return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
	    }
	    return null;
	}
	
	// --- Results from choose exercise -------------------------------------------------------------------------------------------------------------
    @Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{     
		super.onActivityResult(requestCode, resultCode, data); 
		switch(requestCode) { 
			case (CHOOSE_EXERCISE_1) : { 
				if (resultCode == Activity.RESULT_OK) { 
					exerciseString1 = data.getStringExtra(EXERCISE);					
					benchmark1Button.setText(exerciseString1);					
					Toast.makeText(getApplicationContext(), exerciseString1, Toast.LENGTH_SHORT).show();
				} 
				break; 
			} 
			case (CHOOSE_EXERCISE_2) : { 
				if (resultCode == Activity.RESULT_OK) { 
					exerciseString2 = data.getStringExtra(EXERCISE);					
					benchmark2Button.setText(exerciseString2);
					
					Toast.makeText(getApplicationContext(), exerciseString2, Toast.LENGTH_SHORT).show();
				} 
				break; 
			} 
			case (CHOOSE_EXERCISE_3) : { 
				if (resultCode == Activity.RESULT_OK) { 
					exerciseString3 = data.getStringExtra(EXERCISE);					
					benchmark3Button.setText(exerciseString3);					
					Toast.makeText(getApplicationContext(), exerciseString3, Toast.LENGTH_SHORT).show();
				} 
				break; 
			}
			case (CHOOSE_EXERCISE_4) : { 
				if (resultCode == Activity.RESULT_OK) { 
					exerciseString4 = data.getStringExtra(EXERCISE);
					benchmark4Button.setText(exerciseString4);					
					Toast.makeText(getApplicationContext(), exerciseString4, Toast.LENGTH_SHORT).show();
				} 
				break; 
			} 
		} 
	}
}