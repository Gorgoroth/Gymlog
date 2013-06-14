package quelltextfabrik.gymlog;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChooseAdapter extends ArrayAdapter<ArrayList<String>> {

	private ArrayList<ArrayList<String>> items;
	private Context context;

	public ChooseAdapter(Context context, int textViewResourceId, ArrayList<ArrayList<String>> results) {
		super(context, textViewResourceId, 0, results);		
		
		this.context = context;
		this.items = results;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {	
		View v = convertView;

		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.chooseitem, null);
		}
		
		// --- Retrieve Values -------------------------------------------------------------------
		final ArrayList<String> row = items.get(position);
		
		if(row != null) {		
			final String itemText =  row.get(0).toString();
			final int type = Integer.parseInt(row.get(1).toString());		
			TextView bTitle = (TextView) v.findViewById(R.id.name);
	
			// --- Load row ---------------------------------------------------------------------------
			bTitle.setText(itemText);		
			
			bTitle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int workoutId = -1;
					
					// --- Get exercise id ----------------------------------------
	    		    GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getContext());    			
				 	myDbHelper.openDataBase();
				 	workoutId = myDbHelper.retrieveWorkoutIdFromName(itemText);
	    			myDbHelper.close();    			
	
	                Intent myIntent = new Intent();
					myIntent.putExtra("type", type);
					myIntent.putExtra("workout", workoutId);
					myIntent.putExtra("workoutName", itemText);
					
	                ((Activity) context).setResult(Activity.RESULT_OK, myIntent);
	                ((Activity) context).finish();
				}
			});
			
			// --- Delete Icon ------------------------------------------------------------------------
			ImageView delIcon = (ImageView) v.findViewById(R.id.delete);
			delIcon.setVisibility(View.VISIBLE);
			delIcon.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {					
						// --- Remove workout ---------
			        	final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
			        	
			        	alert.setMessage("Delete workout " + itemText);
			        	
			        	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			        		public void onClick(DialogInterface dialog, int whichButton) {   
			        			        			
			        			// --- Save to DB ----------------------------------------------------- 
			        		    GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getContext());
			        			
							 	myDbHelper.openDataBaseRW();
						        
			        			myDbHelper.removeWorkout(itemText);
			        			myDbHelper.close();
			        			
			        			// --- Refresh view ---------------------------------------------------
			        			items.remove(row);
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
		}
		
		return (v);
	}
}