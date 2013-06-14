package quelltextfabrik.gymlog;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseExercise extends ListActivity {
    public static final String EXERCISE = "exerciseName";
	private ListView mainListView = null;
	ArrayList<String> results;
	ArrayAdapter<String> customListAdapter;
    GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(this);
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	results = new ArrayList<String>(); 
    		 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chooseexercise);
        
        // --- Open database --------------------------------------------------------------------------------
        GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(this);
        myDbHelper = new GymlogDatabaseHelper(this);
        
        myDbHelper.openDataBase();
	 	results = myDbHelper.retrieveAllExercises();
        myDbHelper.close();
	 	
	 	// --- List contents --------------------------------------------------------------------------------
		this.mainListView = getListView();
		Log.d("debug", "manListView " + mainListView.toString());
		
		mainListView.setCacheColorHint(0);
		customListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, results);				
		mainListView.setAdapter(customListAdapter);
		mainListView.setTextFilterEnabled(true);
		
		// --- On click listener for returning exercise name ------------------------------------------------
		mainListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(EXERCISE, ((TextView) view).getText());
                setResult(RESULT_OK, intent);
                finish();
			}
		});
		
		TextView addButton = (TextView) findViewById(R.id.chooseExerciseAdd);
		addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	final AlertDialog alert =  makeAlert();
            	alert.show();
        		Log.d("debug", "Click!");
            }
        });
    }
    
    public AlertDialog makeAlert()
    {
    	final AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	final EditText input = new EditText(this);
    	alert.setView(input);

    	alert.setTitle("Enter new exercise");
    	//alert.setView(layout);
    	
    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    			
    			// --- Get them views ---------------------------------------------------------
                String name = input.getText().toString().trim();
    			
    			// --- Save to DB -------------------------------------------------------------
    			myDbHelper.openDataBaseRW();
    			myDbHelper.addNewExercise(name);
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