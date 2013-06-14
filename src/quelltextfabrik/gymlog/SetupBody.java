package quelltextfabrik.gymlog;

import java.util.Calendar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class SetupBody extends Activity {
    public static final String PREFS_NAME = "GymlogPrefsFile";
	
	private TextView userBirth;	// For birth date picker
    private Button mPickDate;
    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;

    static final int DATE_DIALOG_ID = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setupbody);
        
        // --- Get preferences --------------------------------------------------        
        // --- Capture view elements ----------
        final EditText userHeight = (EditText) findViewById(R.id.HeightValue);
        
        // Load settings -----------
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);        
        String prefBirthDate = settings.getString("userBirthdate", "0");        
        String prefHeight = settings.getString("userHeight", "0");
        
        userBirth = (TextView) findViewById(R.id.BirthdateValue);
        mPickDate = (Button) findViewById(R.id.SetupBirthdatePicker);
        
        // --- Get date ----------
        long birthdateTimestamp;
        if(prefBirthDate != "0") {
        	birthdateTimestamp = Long.decode(prefBirthDate);
        } else {
        	birthdateTimestamp = Long.decode("542644055000");
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(birthdateTimestamp);        
        mYear = cal.get(Calendar.YEAR);
        mDay = cal.get(Calendar.DAY_OF_MONTH);
        mMonth = cal.get(Calendar.MONTH);
                
        updateDisplay();	// Updates the TextView
        
        // Display height
        if(prefHeight != "0") {
        	userHeight.setText(prefHeight);
        } else {
        	userHeight.setText("170");
        }

        // --- Date picker ----------
        // add a click listener to the button
        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        
        // --- Save button ----------
        Button save = (Button) findViewById(R.id.SetupSave);
        save.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);	// All objects are from android.context.Context
				SharedPreferences.Editor editor = settings.edit();					// We need an Editor object to make preference changes.
				
		        Calendar cal = Calendar.getInstance();
		        cal.set(mYear, mMonth, mDay); 
		        Long saveStamp = cal.getTimeInMillis();
				
				editor.putString("userHeight", userHeight.getText().toString());
				editor.putString("userBirthdate", saveStamp.toString());		
				editor.commit();	// Commit the edits!				
			}	
        }); 
        
        // --- Back button ----------
        Button back = (Button) findViewById(R.id.SetupBack);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        // --- Done button ----------
        Button done = (Button) findViewById(R.id.SetupDone);
        done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
    
    // --- Updates birthday display ----------
    private void updateDisplay() {
    	userBirth.setText(
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
    /*
    @Override
    protected void onStop(){
    	super.onStop();
    	
    	
    	
    	
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);// All objects are from android.context.Context
		SharedPreferences.Editor editor = settings.edit();	// We need an Editor object to make preference changes.
		  
		editor.putString("userHeight", mSilentMode);		
	
		editor.commit();	// Commit the edits!
		
    }*/
}