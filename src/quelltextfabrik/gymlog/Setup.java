package quelltextfabrik.gymlog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Setup extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);
        
        // --- Program Setup mode ----------
        Button programSetupMode = (Button) findViewById(R.id.yourPrograms);
        programSetupMode.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), ProgramList.class);
		        // TODO use integer constants here
				myIntent.putExtra("mode", 1);
				startActivityForResult(myIntent, 0);
			}
		});
        
        // --- Workoutlist Setup mode ----------
        Button workoutSetupMode = (Button) findViewById(R.id.yourWorkouts);
        workoutSetupMode.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), WorkoutList.class);
		        // TODO use integer constants here
				myIntent.putExtra("mode", 1);
				myIntent.putExtra("program", 0);
				startActivityForResult(myIntent, 0);
			}
		});
        
        // --- Exerciselist Setup mode ----------
        Button exerciseSetupMode = (Button) findViewById(R.id.yourExercises);
        exerciseSetupMode.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), ChooseExercise.class);
		        // TODO use integer constants here
				myIntent.putExtra("mode", 1);
				startActivityForResult(myIntent, 0);
			}
		});
        
        // --- Body Setup mode ----------
        Button bodySetup = (Button) findViewById(R.id.yourBody);
        bodySetup.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), SetupBody.class);
				startActivityForResult(myIntent, 0);
			}
		});
        
        // --- Setup Schedule mode ----------
        Button scheduleSetup = (Button) findViewById(R.id.yourSchedule);
        scheduleSetup.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), SetupSchedule.class);
				startActivityForResult(myIntent, 0);
			}
		});
    }
}