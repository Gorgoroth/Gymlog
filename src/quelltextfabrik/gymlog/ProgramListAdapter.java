package quelltextfabrik.gymlog;

import java.util.ArrayList;

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
import android.widget.TextView;
import android.widget.Toast;

public class ProgramListAdapter extends ArrayAdapter<ArrayList<String>> {

	private ArrayList<ArrayList<String>> items;
	private Context context;
	
	public ProgramListAdapter(Context context, int textViewResourceId, ArrayList<ArrayList<String>> results) {
		super(context, textViewResourceId, 0, results);		
		
		this.context = context;
		this.items = results;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.programlistitem, null);
		}
		
		// --- Retrieve Values -------------------------------------------------------------------
		final ArrayList<String> row = items.get(position);		
		final String itemText =  row.get(0).toString();

		// --- Load row ---------------------------------------------------------------------------
		TextView programView = (TextView) v.findViewById(R.id.programName);
		if(programView != null) {
			// --- Set program name -------------------------------------------
			programView.setText(itemText);		
			
			// --- Set onClick listener ---------------------------------------
			programView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent myIntent = null;	    			
					myIntent = new Intent(context, WorkoutList.class);
					
					// --- Get exercise id ----------------------------------------
	    		    GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getContext());    			
				 	myDbHelper.openDataBase();
					int programId = myDbHelper.retrieveProgramIdFromName(itemText);
	    			myDbHelper.close();

					myIntent.putExtra("mode", ProgramList.myMode);
					myIntent.putExtra("program", programId);
					
					context.startActivity(myIntent);					
				}
			});
		}
						
		if(ProgramList.myMode == 1) {
			// --- Delete Icon ------------------------------------------------------------------------
			// --- Find and show view -----------------------------------------
			ImageView delIcon = (ImageView) v.findViewById(R.id.programDelete);
			delIcon.setVisibility(View.VISIBLE);
			
			// --- onClick listener -------------------------------------------
			delIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d("debug", "Delete " + itemText);
					
					// TODO --- Remove workout ---------
		        	final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
		        	
		        	alert.setMessage("Delete workout " + itemText);
		        	
		        	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		        		public void onClick(DialogInterface dialog, int whichButton) {   
		        			        			
		        			// --- Save to DB ----------------------------------------------------- 
		        		    GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getContext());
		        			
						 	myDbHelper.openDataBaseRW();
					        
		        			myDbHelper.removeProgram(itemText);
		        			myDbHelper.close();
		        			
		        			// --- Refresh view ---------------------------------------------------
		        			items.remove(row);
		        			notifyDataSetChanged();
		        			
		        			Toast.makeText(getContext(), "Deleted program " + itemText, Toast.LENGTH_SHORT).show();
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