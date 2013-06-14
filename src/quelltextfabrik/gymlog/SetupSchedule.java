package quelltextfabrik.gymlog;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class SetupSchedule extends Activity {

    GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(this);
	ArrayList<String> results;
	int myProgram = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setupschedule);

        // --- Choose workout -----------------------------------------------------------------------------------------
        // --- Load from DB -----------------------------------------------------------------------
    	results = new ArrayList<String>();     	
	 	myDbHelper.openDataBase();
	 	results = myDbHelper.retrieveAllProgramNames();
	 	
	 	if(results != null) {
		 	// --- Set spinner ------------------------------------------------------------------------
	        Spinner spinner = (Spinner) findViewById(R.id.setupScheduleChooseProgram);
	        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, results);
	        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        spinner.setAdapter(spinnerArrayAdapter);
	        
	        // --- Set first program as active -------------------------------------------------------
	        myProgram = myDbHelper.retrieveProgramIdFromName(results.get(0));
	        reloadSchedule();
	        
	        // --- Set call back for spinner ----------------------------------------------------------
	        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
	 	}
        myDbHelper.close();
        
        // --- Back ---------------------------------------------------------------------------------------------------
        Button back = (Button) findViewById(R.id.SetupTrainingScheduleBack);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        
        // --- Continue -----------------------------------------------------------------------------------------------
        Button next = (Button) findViewById(R.id.SetupScheduleSave);
        next.setOnClickListener(new View.OnClickListener() {    		
			@Override
			public void onClick(View v) {
				
				// --- Schedule -------------------------------------------------------------------
				int schedule = 0;				
				int idx = 0;
				
		    	TableRow row = (TableRow) findViewById(R.id.SetupTraingingScheduleRowCheckboxes);
		    	CheckBox checkDay = null;
		    	
	    		for(int day = Gymlog.SCHEDULE_DAY_MO; day <= Gymlog.SCHEDULE_DAY_SU; idx++) {
		        	checkDay = (CheckBox) row.getChildAt(idx);		        	
		        	if(checkDay.isChecked()) { schedule |= day; }	// If day is checked, add to schedule
	    			day <<= 1;
	    		}
				
	    		// --- Schedule type --------------------------------------------------------------
	    		RadioButton chkAlternate = (RadioButton) findViewById(R.id.SetupTrainingScheduleAlternate);
	    		RadioButton chkFixed = (RadioButton) findViewById(R.id.SetupTrainingScheduleFixed);
	    		int type = 0;
	    		
	    		if(chkAlternate.isChecked()) { type = Gymlog.SCHEDULE_TYPE_ALTERNATE;
	    		} else if(chkFixed.isChecked()) { type = Gymlog.SCHEDULE_TYPE_FIXED;	    			
	    		}
	    		
	    		// --- Save all that stuff --------------------------------------------------------
	    	 	myDbHelper.openDataBaseRW();
	    	 	myDbHelper.saveSchedule(myProgram, type, schedule);
	        	myDbHelper.close();
	        	
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
			}
        });
    }
    
    public class MyOnItemSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        	// Retrieve program id and reload schedule
        	String programName = ((TextView)view).getText().toString();
    	 	myDbHelper.openDataBase();
    	 	results = myDbHelper.retrieveAllProgramNames();
        	myProgram = myDbHelper.retrieveProgramIdFromName(programName);
        	myDbHelper.close();
        	reloadSchedule();
        	Toast.makeText(parent.getContext(), "Selected program " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
        }

        public void onNothingSelected(AdapterView<?> parent) {
          // Do nothing.
        }
    }
    
    public void reloadSchedule()
    {
    	Log.d("debug", "Loading schedule for " + myProgram);
    	
	 	myDbHelper.openDataBase();
	 	int type = myDbHelper.retrieveScheduleTypeFromProgram(myProgram);

    	Log.d("debug", "Type is " + type);

		int idx = 0;
    	TableRow row = (TableRow) findViewById(R.id.SetupTraingingScheduleRowCheckboxes);
    	CheckBox checkDay = null;
    	
		RadioButton chkAlternate = (RadioButton) findViewById(R.id.SetupTrainingScheduleAlternate);
		RadioButton chkFixed = (RadioButton) findViewById(R.id.SetupTrainingScheduleFixed);
    	
    	if(type == Gymlog.SCHEDULE_TYPE_ALTERNATE) {
    		// --- Type Alternate ----------------------------------------------------------------- 
    		chkAlternate.setChecked(true);
    		chkFixed.setChecked(false);
    		
    		int schedule = myDbHelper.retrieveScheduleFromProgram(myProgram, type);
    		        	
    		for(int day = Gymlog.SCHEDULE_DAY_MO; day <= Gymlog.SCHEDULE_DAY_SU; idx++) {
	        	checkDay = (CheckBox) row.getChildAt(idx);	        	
    			if((schedule & day) != 0) { checkDay.setChecked(true);
    			} else { checkDay.setChecked(false);    				
    			}    			
    			day <<= 1;
    		}
    		
        	Log.d("debug", "Schedule is " + schedule);
    	} else if (type == Gymlog.SCHEDULE_TYPE_FIXED) {
    		// --- Type fixed ---------------------------------------------------------------------
    		chkAlternate.setChecked(false);
    		chkFixed.setChecked(true);
    		
    		int schedule = myDbHelper.retrieveScheduleFromProgram(myProgram, type);
    		
    		for(int day = Gymlog.SCHEDULE_DAY_MO; day <= Gymlog.SCHEDULE_DAY_SU; idx++) {
	        	checkDay = (CheckBox) row.getChildAt(idx);	        	
    			if((schedule & day) != 0) { checkDay.setChecked(true);
    			} else { checkDay.setChecked(false);    				
    			}    			
    			day <<= 1;
    		}
    	} else {
    		// --- No type yet --------------------------------------------------------------------
    		chkAlternate.setChecked(true);
    		chkFixed.setChecked(false);
    		
    		for(int day = Gymlog.SCHEDULE_DAY_MO; day <= Gymlog.SCHEDULE_DAY_SU; idx++) {
	        	checkDay = (CheckBox) row.getChildAt(idx);	        	
	        	checkDay.setChecked(false);    	
    			day <<= 1;
    		}
    	}
    	
    	myDbHelper.close();
    	
    }
}