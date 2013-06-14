package quelltextfabrik.gymlog;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

public class ChooseWorkout extends ListActivity {
    public static final String EXERCISE = "exerciseName";
	private ListView mainListView = null;
	ArrayList<ArrayList<String>> results;
	ChooseAdapter customListAdapter;
    GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(this);
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	results = new ArrayList<ArrayList<String>>(); 
    		 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chooseworkout);
        
        // --- Open database --------------------------------------------------------------------------------
        GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(this);
        myDbHelper = new GymlogDatabaseHelper(this);
        
        myDbHelper.openDataBase();
	 	results = myDbHelper.retrieveAllWorkouts();
        myDbHelper.close();
	 	
	 	// --- List contents --------------------------------------------------------------------------------
		this.mainListView = getListView();
		
		mainListView.setCacheColorHint(0);
		customListAdapter = new ChooseAdapter(this, R.layout.chooseitem, results);				
		mainListView.setAdapter(customListAdapter);
		mainListView.setTextFilterEnabled(true);
		
		// --- On click listener for returning exercise name ------------------------------------------------
		mainListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("program", ((TextView) view).getText());
                setResult(RESULT_OK, intent);
                finish();
			}
		});
		
		TextView addButton = (TextView) findViewById(R.id.chooseWorkoutAdd);
		addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	final AlertDialog alert =  makeAlert();
            	alert.show();
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
    
}