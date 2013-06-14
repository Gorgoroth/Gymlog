package quelltextfabrik.gymlog;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class LoadLog extends ListActivity {
    public static final String KEY_NAME = "name";
	private ListView mainListView = null;
	LoadLogAdapter customListAdapter = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadlog);
             

        Intent startIntent = getIntent();
		int sessionId = startIntent.getIntExtra(Gymlog.PUT_SESSION, 0);
        
        // --- Get list of workouts ----------
        // Open database
        GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(this);
        myDbHelper = new GymlogDatabaseHelper(this);
        
	 	myDbHelper.openDataBase();	 
	 							 
	 	// --- Get workout list ----------
		ArrayList<ArrayList<String>> myLog = new ArrayList<ArrayList<String>>();
	 	myLog = myDbHelper.logLoad(sessionId);
        myDbHelper.close();
        
		//Toast.makeText(getBaseContext(), myLog.toString(), Toast.LENGTH_SHORT).show();

		this.mainListView = getListView();
		
		mainListView.setCacheColorHint(0);
		
		// --- bind the data with the list -------
		this.customListAdapter = new LoadLogAdapter(this, R.layout.exerciseitem, myLog);
		mainListView.setAdapter(this.customListAdapter);
		
        
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
}