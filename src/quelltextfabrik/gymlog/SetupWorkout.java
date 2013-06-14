package quelltextfabrik.gymlog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SetupWorkout extends Activity {

    public static final int CHOOSE_EXERCISE = 1;
    public static final String EXERCISE = "exerciseName";
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setupworkout);
        
        // TODO get workout ID
        // TODO load exercises
        // TODO setup mode
        // TODO train mode
        
        /*
        Button squat = (Button) findViewById(R.id.SetupWorkoutSquat);
        squat.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), ChooseWorkout.class);
				startActivityForResult(myIntent, CHOOSE_EXERCISE);
			}
        });*/       
       
        
        // --- Back ----------
        Button back = (Button) findViewById(R.id.SetupWorkoutBack);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        
        // --- Done ----------
        // TODO Save all
        Button done = (Button) findViewById(R.id.SetupWorkoutDone);
        done.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), WorkoutList.class);
				startActivityForResult(myIntent, 0);
			}
		});
    }
    //onActivityResult
    //
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{     
		super.onActivityResult(requestCode, resultCode, data); 
		switch(requestCode) { 
			case (CHOOSE_EXERCISE) : { 
				if (resultCode == Activity.RESULT_OK) { 
					String myExercise = data.getStringExtra(EXERCISE);
					Toast.makeText(getApplicationContext(), myExercise, Toast.LENGTH_SHORT).show();
				} 
				break; 
			} 
		} 
	}
}