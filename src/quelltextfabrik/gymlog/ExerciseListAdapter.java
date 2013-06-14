package quelltextfabrik.gymlog;

import java.util.ArrayList;

import android.app.Activity;
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

public class ExerciseListAdapter extends ArrayAdapter<String> implements OnClickListener{

	private ArrayList<String> items;
	private Context context;

	public ExerciseListAdapter(Context context, int textViewResourceId, ArrayList<String> dataItems) {
		super(context, textViewResourceId, dataItems);
		
		this.context = context;
		this.items = dataItems;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		View v = convertView;
		String entry = items.get(position);
		
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.exerciselistitem, null);
		}
		
		final String itemText =  this.items.get(position).toString();
		TextView bTitle = (TextView) v.findViewById(R.id.exerciseListTitle);
		
		// --- Load exercise ----------------------------------------------------------
		bTitle.setText(itemText);		
		bTitle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// --- Put id into intent ---------------------------
				Intent myIntent = new Intent(context, Exercise.class);
				
				myIntent.putExtra(Gymlog.PUT_MODE, ExerciseList.myMode);
				myIntent.putExtra(Gymlog.PUT_WORKOUT, ExerciseList.myWorkoutId);
				myIntent.putExtra(Gymlog.PUT_PROGRAM, ExerciseList.myProgram);

    		    GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(context);
    		    
			 	myDbHelper.openDataBase();			 	
			 	int id = -1;
			 	// --- Pass on circle or exercise id -------------------------------
    			if(ExerciseList.myWorkoutType == 1) {
    				id = myDbHelper.retrieveExerciseIdFromName(itemText);		
    				myIntent.putExtra(Gymlog.PUT_EXERCISE, id);
    			} else if(ExerciseList.myWorkoutType == 2) {
    				id = myDbHelper.retrieveCircleIdFromName(itemText);		
    				myIntent.putExtra("circle", id);
    			}
    			
    			myDbHelper.close();
				
				if(ExerciseList.myMode == 2) {
					myIntent.putExtra("session", ExerciseList.mySession);
					myIntent.putExtra("overallprogress", ExerciseList.myOverallProgress);
					myIntent.putExtra("allitems", ExerciseList.allItems);
				}

				Log.d("debug", "" + "Clicked circle/exercise " + id + " from workout nr " + ExerciseList.myWorkoutId + " type " + ExerciseList.myWorkoutType + " in mode " + ExerciseList.myMode);
				
				((Activity) context).startActivityForResult(myIntent, ExerciseList.EXERCISE_TRAIN);
				
			}
		});
		
		if(ExerciseList.myMode == 1) {
			// --- Delete Icon ------------------------------------------------------------------------
			ImageView delIcon = (ImageView) v.findViewById(R.id.exerciseListDelete);
			delIcon.setVisibility(View.VISIBLE);
			delIcon.setOnClickListener(this);
			delIcon.setTag(entry);			
		}
		
		
		return (v);
	}
	
    @Override
    public void onClick(View view) {
    	// TODO make this prettier
        final String entry = (String) view.getTag();
        
        Log.d("debug", "Remove " + entry);
		
    	final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
    	

		if(ExerciseList.myWorkoutType == 1) {
	    	alert.setMessage("Remove Exercise " + entry + " from this workout?");		
		} else if(ExerciseList.myWorkoutType == 2) {
	    	alert.setMessage("Remove circle " + entry + " from this workout?");		
		}
		
    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    		
    		public void onClick(DialogInterface dialog, int whichButton) {    			
    			// --- Remove from db ---
    		    GymlogDatabaseHelper myDbHelper = new GymlogDatabaseHelper(getContext());    			
			 	myDbHelper.openDataBaseRW();

				if(ExerciseList.myWorkoutType == 1) {
					myDbHelper.removeExerciseFromWorkout(ExerciseList.myWorkoutId, entry);
				} else if(ExerciseList.myWorkoutType == 2) {
					myDbHelper.removeCircle(ExerciseList.myWorkoutId, entry);
				}
    			myDbHelper.close();
    			
    			// --- Remove from view ---
    	        items.remove(entry);
    	        notifyDataSetChanged();
    			
    			Toast.makeText(getContext(), "Deleted workout " + entry, Toast.LENGTH_SHORT).show();
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
	
}