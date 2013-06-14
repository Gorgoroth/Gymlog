package quelltextfabrik.gymlog;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProgramList extends ListActivity {
	private ListView mainListView = null;
	ProgramListAdapter customListAdapter = null;
    GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(this);
	static int myMode = 0;
	ArrayList<ArrayList<String>> results;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workoutlist);
        
            
        // --- Get list of workouts -----------------------------------------------------------------------------------
        // Open database
    	results = new ArrayList<ArrayList<String>>();     	
	 	myDbHelper.openDataBase();	 
	 	
	 	// --- Get workout list ----------
	 	results = myDbHelper.retrieveAllPrograms();
		this.mainListView = getListView();		
		mainListView.setCacheColorHint(0);
		
		// --- Bind the data with the list -------
		this.customListAdapter = new ProgramListAdapter(ProgramList.this, R.layout.programlistitem, results);
		mainListView.setAdapter(this.customListAdapter);

		
		// --- Retrieve the mode we're operating on -------------------------------------------------------------------
        // TODO use integer constants MODE_SETUP = 1, MODE_TRAIN = 2 etc
		// TODO use string values for setText
        Intent startIntent = getIntent();
		myMode = startIntent.getIntExtra("mode", 0);        
		
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
	        		alert.show();
	            }
	        });           
                                    
            // --- Set Title ----------------------------------------------------------------------
        	modeText.setText("Add or modify programs");
        	
        } else if(myMode == 2) {
        	// --- Trainmode --------------------------------------------------------------------------------
        	modeText.setText("Choose program");
        	
        } else {        	
        	// --- Undefined mode ---------------------------------------------------------------------------
        	modeText.setText("Invalid mode, please send me hatemail");     
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
    	final EditText input = new EditText(this);
    	alert.setTitle("Enter new program");
    	alert.setView(input);
    	
    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    			
    			// --- Get them views -------------------------------------------------------------
                String name = input.getText().toString().trim();
                ArrayList<String> row = new ArrayList<String>();
    			
    			// --- Save to DB -----------------------------------------------------------------
    			myDbHelper.openDataBaseRW();
    			myDbHelper.addNewProgram(name);
    			myDbHelper.close();
    			
    			// --- Refresh view ---------------------------------------------------------------
    			row.add(name);
    			results.add(row);
    			customListAdapter.notifyDataSetChanged();
    			
        		Toast.makeText(getApplicationContext(), "New program " + name, Toast.LENGTH_SHORT).show();        			
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