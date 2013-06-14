package quelltextfabrik.gymlog;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class SessionList extends ListActivity {
	private ListView mainListView = null;
	SessionListAdapter customListAdapter = null;	
	ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exerciselist);
        		
		// --- Display data ---------------------------------------------------------------------------------
        results = new ArrayList<ArrayList<String>>();
        GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(this);
        myDbHelper = new GymlogDatabaseHelper(this);  	
	 	myDbHelper.openDataBase();
	 	results = myDbHelper.retrieveAllSessions();
		myDbHelper.close();
		
        this.mainListView = getListView();		
		mainListView.setCacheColorHint(0);
		this.customListAdapter = new SessionListAdapter(SessionList.this, R.layout.sessionlistitem, results);
		mainListView.setAdapter(this.customListAdapter);
		
			
		// --- CONTROLS --------------------------------------------------------------------------------------
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
}