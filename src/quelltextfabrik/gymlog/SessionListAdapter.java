package quelltextfabrik.gymlog;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SessionListAdapter extends ArrayAdapter<ArrayList<String>> {

	private ArrayList<ArrayList<String>> items;
	private Context context;

	public SessionListAdapter(Context context, int textViewResourceId, ArrayList<ArrayList<String>> dataItems) {
		super(context, textViewResourceId, dataItems);
		
		this.context = context;
		this.items = dataItems;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		View v = convertView;
		final int pos = position;
		
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.sessionlistitem, null);
		}
		
		Log.d("debug", items.toString());
		
		// --- Session Id ---------------------------------------------------------------------------------------------
		final String sessionId =  items.get(position).get(0).toString();
		TextView sessionIdView = (TextView) v.findViewById(R.id.sessionId);
		sessionIdView.setText(sessionId);
		
		// --- Date ---------------------------------------------------------------------------------------------------
		long timestamp = Long.decode(items.get(position).get(1).toString());
    	int mYear, mDay, mMonth, mHour, mMinutes;
    	
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);        
        
        mYear = cal.get(Calendar.YEAR);
        mDay = cal.get(Calendar.DAY_OF_MONTH);
        mMonth = cal.get(Calendar.MONTH);
        /*
        mHour = cal.get(Calendar.HOUR_OF_DAY);
        mMinutes = cal.get(Calendar.MINUTE);
        */
        String date = new StringBuilder()
    		.append(mDay).append(".")
            .append(mMonth+1).append(".")	// Month is 0 based so add 1
            .append(mYear)
            /*.append(" ")
            .append(mHour).append(":")
            .append(mMinutes)*/
            .toString();
		TextView sessionDateView = (TextView) v.findViewById(R.id.sessionDate);
		sessionDateView.setText(date);

		// --- Program and workout ------------------------------------------------------------------------------------
		int programId =  Integer.parseInt(items.get(position).get(2).toString());
		int workoutId =  Integer.parseInt(items.get(position).get(3).toString());
		
		// --- Retrieve names
        GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getContext());
        myDbHelper = new GymlogDatabaseHelper(getContext());  	
	 	myDbHelper.openDataBase();
	 	String program = myDbHelper.getProgramDetail(programId, GymlogDatabaseHelper.KEY_NAME);
	 	String workout = myDbHelper.getWorkoutDetails(workoutId, GymlogDatabaseHelper.KEY_NAME);
		myDbHelper.close();
		
		TextView programView = (TextView) v.findViewById(R.id.sessionProgram);
		programView.setText(program);	
		
		TextView workoutView = (TextView) v.findViewById(R.id.sessionWorkout);
		workoutView.setText(workout);	
		
        
		// --- onClick on Row load log --------------------------------------------------------------------------------
		LinearLayout listRow = (LinearLayout) v.findViewById(R.id.sessionListRow);
		listRow.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(context, LoadLog.class);		        
		        String sessionId = ((TextView)((LinearLayout)v).getChildAt(0)).getText().toString();		        
		        myIntent.putExtra(Gymlog.PUT_SESSION, Integer.parseInt(sessionId));
		        context.startActivity(myIntent);
			}
		});
		 
		// --- Delete Icon --------------------------------------------------------------------------------------------
		ImageView delIcon = (ImageView) v.findViewById(R.id.Delete);
		delIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {					
				// --- Remove session ---------
	        	final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
	        	
	        	alert.setMessage("Delete " + sessionId);
	        	
	        	final int session = Integer.parseInt(sessionId);
	        	
	        	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int whichButton) {   
	        			        			
	        			// --- Save to DB -----------------------------------------------------
	        		    GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getContext());
	        			
					 	myDbHelper.openDataBaseRW();
					 	myDbHelper.deleteSession(session);
	        			myDbHelper.close();
	        			
	        			//Log.d("debug", "deleting id " + sessionId + " at position " + pos);
	        			
	        			// --- Refresh view ---------------------------------------------------
	        			items.remove(pos);
	        			notifyDataSetChanged();		        			
	        		}	
	        	});
	        	
	    		alert.setNegativeButton("Cancel",
	    				new DialogInterface.OnClickListener() {
	    					public void onClick(DialogInterface dialog, int whichButton) {
	    						dialog.cancel();
	    					}
	    				});
	    		
	    		alert.show();
			}
		});
	        
		
		return (v);
	}
}