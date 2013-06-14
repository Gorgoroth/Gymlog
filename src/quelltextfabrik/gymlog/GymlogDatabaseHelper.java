package quelltextfabrik.gymlog;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.inputmethodservice.Keyboard.Row;
import android.util.Log;

public class GymlogDatabaseHelper extends SQLiteOpenHelper
{
    public static final String KEY_ROWID = "_id";
    public final static String KEY_NAME = "name";
    public static final String KEY_TABLE = "tableName";
    public static final String KEY_EXERCISEID = "exerciseId";
    public static final String KEY_SETNR = "setNr"; 
    public static final String KEY_SESSION = "session";
    public static final String KEY_WEIGHT = "weight";
    public static final String KEY_REPS = "reps";
    public static final String KEY_TYPE = "type";
    public static final String KEY_WEIGHTINCREASEMODE = "weightIncreaseMode";
    public static final String KEY_WEIGHTINCREASE = "weightIncrease";
    public static final String KEY_WORKOUT = "workout";
    public static final String KEY_PROGRAM = "program";
    public static final String KEY_EXERCISE = "exercise";
    public static final String KEY_SCHEDULE = "schedule";
    public static final String KEY_SCHEDULETYPE = "scheduleType";
    public static final String KEY_TIMESTAMP = "timestamp";
    
    private static String DB_PATH = "/data/data/quelltextfabrik.gymlog/databases/";	//The Android's default system path of your application database.
    private static final String DEFAULT_DB_NAME = "gymlogDefault.db";
    private static final String DB_NAME = "gymlog";
    
    private static final String DB_EXERCISES_TABLE ="exercises";
    private static final String DB_WORKOUTS_TABLE ="workouts";
    private static final String DB_USERLOG_TABLE ="userLog";
    private static final String DB_BENCHMARK_TABLE ="userBenchmark";
    private static final String DB_CIRCLES_TABLE ="circles";
    private static final String DB_PROGRAMS_TABLE = "programs";

    private GymlogDatabaseHelper myDBHelper;
    private SQLiteDatabase myDataBase; 
    private final Context myContext;
    
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public GymlogDatabaseHelper(Context context)
    { 
    	super(context, DB_NAME, null, 1);
        this.myContext = context;
    }
    
    
    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException
    { 
    	boolean dbExist = checkDataBase(); 
    	if(dbExist){
    		//do nothing - database already exist
    	} else {
        	this.getReadableDatabase(); // By calling this method and empty database will be created into the default system path 
        	try {						// of your application so we are gonna be able to overwrite that database with our database. 
    			copyDataBase(); 
    		} catch (IOException e) { 
        		throw new Error("Error copying database"); 
        	}
    	} 
    }
    
    
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase()
    { 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME ;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY); 
    	} catch(SQLiteException e) { 
    	}
 
    	if(checkDB != null){ checkDB.close(); }
 
    	return checkDB != null ? true : false;    	
    }
    
    
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transferring byte stream.
     * */
    private void copyDataBase() throws IOException
    {
    	InputStream myInput = myContext.getAssets().open(DEFAULT_DB_NAME);	// Open default local db as the input stream     	
    	String outFileName = DB_PATH + DB_NAME;								// Path to the just created empty db   	
    	OutputStream myOutput = new FileOutputStream(outFileName);			// Open the empty db as the output stream
 
    	//transfer bytes from the input file to the output file
    	byte[] buffer = new byte[1024];
    	int length;
    	
    	while((length = myInput.read(buffer)) > 0) {
    		myOutput.write(buffer, 0, length);
    	}

		//throw new Error("database doesn't exist yet");
    	
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close(); 
    }    
    
    public void openDataBase() throws SQLException
    { 
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY); 
    }
    
    public GymlogDatabaseHelper openDataBaseRW() throws SQLException
    { 
    	/*
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE); 
    	myDataBase.getWritableDatabase();*/

        myDBHelper = new GymlogDatabaseHelper(myContext);
        myDataBase = myDBHelper.getWritableDatabase();
        return this;
    }
        
    @Override
	public synchronized void close()
    { 
	    if(myDataBase != null) { myDataBase.close(); } 
	    super.close(); 
	}
     
	@Override
	public void onCreate(SQLiteDatabase db)
	{ 
	}
	 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{ 
	}
 	
	// Add your public helper methods to access and get content from the database.
	// TODO You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
	// to you to create adapters for your views.
	
	// TODO ensure that database is open, throw error otherwise
	// TODO make code nice, cut out redundancy

	// --- BENCHMARKS -------------------------------------------------------------------------------------------------------------------------------------
	public long newBenchmark(ArrayList<String> values)
	{
		long result;
		
		ContentValues myValues = new ContentValues();
		
		myValues.put("timestamp",	Long.parseLong(values.get(0)));
		myValues.put("weight",		Double.parseDouble(values.get(1)));
		myValues.put("bodyfat",		Long.parseLong(values.get(2)));
		myValues.put("exercise1",	Integer.parseInt(values.get(3)));
		myValues.put("weight1",		Double.parseDouble(values.get(4)));
		myValues.put("exercise2",	Integer.parseInt(values.get(5)));
		myValues.put("weight2",		Double.parseDouble(values.get(6)));
		myValues.put("exercise3",	Integer.parseInt(values.get(7)));
		myValues.put("weight3",		Double.parseDouble(values.get(8)));
		myValues.put("exercise4",	Integer.parseInt(values.get(9)));
		myValues.put("weight4",		Double.parseDouble(values.get(10)));		
		
		result = myDataBase.insertOrThrow(DB_BENCHMARK_TABLE,
				null,
				myValues);

		return result;
	}
	
	public ArrayList<String> getLatestBenchmark()
	{
		ArrayList<String> values = new ArrayList<String>();
	
	     Cursor c = myDataBase.query(
	        		DB_BENCHMARK_TABLE,
	        		null,	// Select all columns
	        		null,	// Select all
	        		null,
	        		null,
	        		null,
	        		KEY_ROWID + " DESC"	// Latest first
	        		);
	        
	        if(c != null) {
				if(c.moveToFirst()) {
					for (int i = 0; i < c.getColumnCount(); i++) {
						values.add(c.getString(i));
						Log.d("debug", "getLatestBenchmark() - column " + i + " value " + c.getString(i));
					}
					Log.d("debug", "getLatestBenchmark() - All values loaded");
				} else {					
					Log.d("debug", "Error - getLatestBenchmark() - moveToFirst()");
				}
			} else {				
				Log.d("debug", "Error - getLatestBenchmark() - cursor null");
			}

	    c.close();
		return values;
	}
	
	// --- LOGGING ----------------------------------------------------------------------------------------------------------------------------------------
	public long logSet(int program, int workout, int session, int exercise, int setNr, Double weightVal, int repVal)
	{
		long result;
		
		ContentValues myValues = new ContentValues();
		
		myValues.put("timestamp",	System.currentTimeMillis());
		myValues.put("program",		program);
		myValues.put("workout",		workout);
		myValues.put("session",		session);
		myValues.put("exercise",	exercise);				
		myValues.put("setNr",		setNr);
		myValues.put("weight",		weightVal);
		myValues.put("reps",		repVal);
		
		result = myDataBase.insertOrThrow(DB_USERLOG_TABLE,
				null,
				myValues);
		
		Log.d("debug", "logSet - set logged with result " + result + " and values(" + System.currentTimeMillis() + ", " + program  + ", "+ workout  + ", "+ exercise + ", " + setNr + ", " + weightVal + ", " + repVal + ", " + ")");

		return result;
	}
	
	public ArrayList<ArrayList<String>> logLoad(int sessionId)
	{
        ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
        
        Cursor c = myDataBase.query(
        		DB_USERLOG_TABLE,
        		null,	// Select all columns
        		"session='" + sessionId + "'",	// Load all rows with session Id
        		null,
        		null,
        		null,
        		KEY_ROWID + " DESC",
        		null);
		
        if(c != null) {
			if(c.moveToFirst()) {
				do {
					ArrayList<String> row = new ArrayList<String>();
					int idx = 0;
					
					idx = c.getColumnIndex(KEY_ROWID);					
					if(idx != -1) {
						int val = c.getInt(idx);
						row.add("" + val);						
					}

					idx = c.getColumnIndex(KEY_TIMESTAMP);					
					if(idx != -1) {
						long val = c.getLong(idx);
						row.add("" + val);						
					}
					
					idx = c.getColumnIndex(KEY_PROGRAM);					
					if(idx != -1) {
						int val = c.getInt(idx);
						row.add("" + val);						
					}
					
					idx = c.getColumnIndex(KEY_WORKOUT);					
					if(idx != -1) {
						int val = c.getInt(idx);
						row.add("" + val);						
					}
					
					idx = c.getColumnIndex(KEY_SESSION);					
					if(idx != -1) {
						int val = c.getInt(idx);
						row.add("" + val);						
					}
					
					idx = c.getColumnIndex(KEY_EXERCISE);					
					if(idx != -1) {
						int val = c.getInt(idx);
						row.add("" + val);						
					}
					
					idx = c.getColumnIndex(KEY_SETNR);					
					if(idx != -1) {
						int val = c.getInt(idx);
						row.add("" + val);						
					}
					
					idx = c.getColumnIndex(KEY_WEIGHT);					
					if(idx != -1) {
						Double val = c.getDouble(idx);
						row.add("" + val);						
					}
					
					idx = c.getColumnIndex(KEY_REPS);					
					if(idx != -1) {
						int val = c.getInt(idx);
						row.add("" + val);						
					}
					
					
					results.add(row);
				} while(c.moveToNext());
			} else {
				
				Log.d("debug", "Error - logLoadAll()- moveToFirst()");
			}
		} else {
			
			Log.d("debug", "Error - logLoadAll() - cursor null");
		}
        c.close();
    	return results;
	}
	
	public ArrayList<ArrayList<String>> logLoadAll()
	{
        ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
        
        Cursor c = myDataBase.query(
        		DB_USERLOG_TABLE,
        		null,	// Select all columns
        		null,	// Load all rows
        		null,
        		null,
        		null,
        		KEY_ROWID + " DESC",
        		null);
		
        if(c != null) {
			if(c.moveToFirst()) {
				do {
					ArrayList<String> row = new ArrayList<String>();
					int idx = 0;
					
					// --- TODO - srsly, use constants
					// id
					idx = c.getColumnIndex("_id");					
					if(idx != -1) {
						int val = c.getInt(idx);
						row.add("" + val);						
					}

					// timestamp
					idx = c.getColumnIndex("timestamp");					
					if(idx != -1) {
						long val = c.getLong(idx);
						row.add("" + val);						
					}
					
					// program
					idx = c.getColumnIndex("program");					
					if(idx != -1) {
						int val = c.getInt(idx);
						row.add("" + val);						
					}
					
					// workout
					idx = c.getColumnIndex("workout");					
					if(idx != -1) {
						int val = c.getInt(idx);
						row.add("" + val);						
					}
					
					// session
					idx = c.getColumnIndex("session");					
					if(idx != -1) {
						int val = c.getInt(idx);
						row.add("" + val);						
					}
					
					// exercise
					idx = c.getColumnIndex("exercise");					
					if(idx != -1) {
						int val = c.getInt(idx);
						row.add("" + val);						
					}
					
					// setNr
					idx = c.getColumnIndex("setNr");					
					if(idx != -1) {
						int val = c.getInt(idx);
						row.add("" + val);						
					}
					
					// weight
					idx = c.getColumnIndex("weight");					
					if(idx != -1) {
						Double val = c.getDouble(idx);
						row.add("" + val);						
					}
					
					// reps
					idx = c.getColumnIndex("reps");					
					if(idx != -1) {
						int val = c.getInt(idx);
						row.add("" + val);						
					}					
					
					results.add(row);
				} while(c.moveToNext());
			} else {
				
				Log.d("debug", "Error - logLoadAll()- moveToFirst()");
			}
		} else {
			
			Log.d("debug", "Error - logLoadAll() - cursor null");
		}
        c.close();
    	return results;
	}
	
	// --- SESSION ----------------------------------------------------------------------------------------------------------------------------------------
	public int sessionGenerate()
	{
		int sessionId = -1;
		
		sessionId = getLatestSessionNr() + 1;
        
		return sessionId;
	}
	
	public int getLatestSessionNr()
	{
		int result = 0;
		
		Cursor c = myDataBase.query(
        		DB_USERLOG_TABLE,
        		new String[] {
        			KEY_SESSION	// Select all columns
        		},
        		KEY_ROWID + " IN (SELECT MAX(" + KEY_ROWID + ") FROM " + DB_USERLOG_TABLE + " GROUP BY " + KEY_SESSION + ")",	// select highest session ID
        		null,
        		null,
        		null,
        		KEY_ROWID + " DESC",
        		"1");
		
        if(c != null) {
			if(c.moveToFirst()) {
				int idx = c.getColumnIndex(KEY_SESSION);
				if(idx != -1) {
					result = c.getInt(idx);
				} else {
					Log.d("debug", "Error - getLatestSessionNr - getColumnIdx");
				}
			} else {
				Log.d("debug", "Error - getLatestSessionNr - moveToFirst");
			}
        } else {
			Log.d("debug", "Error - getLatestSessionNr - query failed");        	
        }
		c.close();
		
		return result;
	}
	
	public long deleteSession(int sessionId)
	{
		long error = -1;
		
		error = myDataBase.delete(DB_USERLOG_TABLE, KEY_SESSION + "='" + sessionId + "'", null);
		
		Log.d("debug", "deleting id " + sessionId + " with result " + error);
		
		return error;
	}
	
	public ArrayList<ArrayList<String>> retrieveAllSessions()
	{
		ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
        
        Cursor c = myDataBase.query(
        		DB_USERLOG_TABLE,
        		new String[] {
        			KEY_SESSION,
        			KEY_TIMESTAMP,
        			KEY_PROGRAM,
        			KEY_WORKOUT
        		},
        		KEY_ROWID + " IN (SELECT MAX(" + KEY_ROWID + ") FROM " + DB_USERLOG_TABLE + " GROUP BY " + KEY_SESSION + ")",
        		null,
        		null,
        		null,
        		KEY_SESSION + " DESC"
        		);
		
        if(c != null) {
			if(c.moveToFirst()) {
				ArrayList<String> row = new ArrayList<String>();
				int idx = -1;
				int val = -1;
				do {
					row = new ArrayList<String>();
					val = -1;
					idx = c.getColumnIndex(KEY_SESSION);
					if(idx != -1) {
						val = c.getInt(idx);
						row.add("" + val);
					}

					idx = c.getColumnIndex(KEY_TIMESTAMP);
					if(idx != -1) {
						long date = c.getLong(idx);
						row.add("" + date);
					}
					idx = c.getColumnIndex(KEY_PROGRAM);
					if(idx != -1) {
						val = c.getInt(idx);
						row.add("" + val);
					}
					idx = c.getColumnIndex(KEY_WORKOUT);
					if(idx != -1) {
						val = c.getInt(idx);
						row.add("" + val);
					}
					
					results.add(row);
				} while (c.moveToNext());
			} else {
				Log.d("debug", "Error - retrieveAllSessions - moveToFirst - No sessions");
			}
        } else {
			Log.d("debug", "Error - retrieveAllSessions - query failed");        	
        }
        
        return results;	
	}
	
	public int getLastWorkoutFromSession(int programId) 
	{
		int result = -1;
		int lastSession = getLatestSessionForProgram(programId);
		
	    Cursor c = myDataBase.query(
			DB_USERLOG_TABLE,
			new String[] {
				"MAX(" + KEY_WORKOUT + ")"
			},
			KEY_PROGRAM + "='" + programId + "' AND " + KEY_SESSION + "='" + lastSession + "'",
			null,
			null,
			null,
			null);   
		
		if(c != null) {
			if(c.moveToFirst()) {
				int idx = -1;
				idx = c.getColumnIndex("MAX(" + KEY_WORKOUT + ")");
				if(idx != -1) { 	
					result = c.getInt(idx);
				} else {
					Log.d("debug", "Error getColumnIndex");
				}
			}else {
				Log.d("debug", "Error moveToFirst");
			}
		}else {
			Log.d("debug", "Error null");
		}

		c.close();
		return result;
	}
	
	public Double getLatestSessionWeight(int programId, int exId) 
	{
		Double result = -1.0;
		int lastSession = getLatestSessionForProgramAndExercise(programId, exId);
		
	    Cursor c = myDataBase.query(
			DB_USERLOG_TABLE,
			new String[] {
				"MAX(" + KEY_WEIGHT + ")"
			},
			KEY_PROGRAM + "='" + programId + "' AND " + KEY_EXERCISE + "='" + exId + "'" + " AND " + KEY_SESSION + "='" + lastSession + "'",
			null,
			null,
			null,
			null);   
		
		if(c != null) {
			if(c.moveToFirst()) {
				int idx = -1;
				idx = c.getColumnIndex("MAX(" + KEY_WEIGHT + ")");
				if(idx != -1) { 	
					result = c.getDouble(idx);
				} else {
					Log.d("debug", "Error getColumnIndex");
				}
			}else {
				Log.d("debug", "Error moveToFirst");
			}
		}else {
			Log.d("debug", "Error null");
		}

		c.close();
		return result;
	}
	
	int getLatestSessionForProgram(int programId) 
	{
		int result = -1;		
		
		Cursor c = myDataBase.query(
			DB_USERLOG_TABLE,
			new String[] {
				"MAX(" + KEY_SESSION + ")"
			},
			KEY_PROGRAM + "='" + programId + "'",
			null,
			null,
			null,
			null);
        
		if(c != null) {
			if(c.moveToFirst()) {
				int idx = -1;
				idx = c.getColumnIndex("MAX(" + KEY_SESSION + ")");
				if(idx != -1) { 	
					result = c.getInt(idx);
				} else {
					Log.d("debug", "Error getColumnIndex");
				}
			}else {
				Log.d("debug", "Error moveToFirst");
			}
		}else {
			Log.d("debug", "Error null");
		}
		
		c.close();
		return result;
	}
	
	int getLatestSessionForProgramAndExercise(int programId, int exId) 
	{
		int result = -1;		
		
		Cursor c = myDataBase.query(
			DB_USERLOG_TABLE,
			new String[] {
				"MAX(" + KEY_SESSION + ")"
			},
			KEY_PROGRAM + "='" + programId + "' AND " + KEY_EXERCISE + "='" + exId + "'",
			null,
			null,
			null,
			null);
        
		if(c != null) {
			if(c.moveToFirst()) {
				int idx = -1;
				idx = c.getColumnIndex("MAX(" + KEY_SESSION + ")");
				if(idx != -1) { 	
					result = c.getInt(idx);
				} else {
					Log.d("debug", "Error getColumnIndex");
				}
			}else {
				Log.d("debug", "Error moveToFirst");
			}
		}else {
			Log.d("debug", "Error null");
		}
		
		c.close();
		return result;
	}
	
	// --- EXERCISES --------------------------------------------------------------------------------------------------------------------------------------
	public ArrayList<String> retrieveAllExercises()
	{ 
        ArrayList<String> results = new ArrayList<String>();
		Cursor c = myDataBase.rawQuery("SELECT * FROM " + DB_EXERCISES_TABLE, null);

		if(c != null ) {		
			if(c.moveToFirst()) {
				do {
					String name = c.getString(c.getColumnIndex(KEY_NAME));
					results.add(name);
				} while (c.moveToNext());
			} 
		}		
        c.close();
    	return results;
	}

	public int retrieveExerciseIdFromName(String name)
	{ 
        int result = -1;
        Cursor c = myDataBase.query(
        		DB_EXERCISES_TABLE, 
        		new String[] {
        			KEY_ROWID
        		},
        		KEY_NAME + "='" + name + "'",
        		null,
        		null,
        		null,
        		null,
        		null);
        
        if(c != null) {
			if(c.moveToFirst()) {
				int idx = c.getColumnIndex(KEY_ROWID);
				if(idx != -1) {
					result = c.getInt(idx);
					Log.d("debug", "" + result);
				} else {
					Log.d("debug", "Error - retrieveExerciseIdFromName - getColumnIdx");
				}
			} else {
				Log.d("debug", "Error - retrieveExerciseIdFromName - moveToFirst");
			}
		} else {
			Log.d("debug", "Error - retrieveExerciseIdFromName - cursor null");
		}
		
        c.close();
    	return result;
	}

	public String retrieveExerciseDetail(int id, String col)
	{ 
        String result = new String();
        Cursor c = myDataBase.query(
        		DB_EXERCISES_TABLE, 
        		new String[] {
        			col
        		},
        		KEY_ROWID + "=" + id,
        		null,
        		null,
        		null,
        		null,
        		null);
        
        if(c != null) {
			if(c.moveToFirst()) {
				int idx = c.getColumnIndex(col);
				if(idx != -1) {
					result = c.getString(idx);
					Log.d("debug", "" + result);
				} else {
					Log.d("debug", "Error - retrieveExerciseDetails - getColumnIdx");
				}
			} else {
				Log.d("debug", "Error - retrieveExerciseDetails - moveToFirst");
			}
		} else {
			Log.d("debug", "Error - retrieveExerciseDetails - cursor null");
		}

        c.close();
    	return result;
	}

	public ArrayList<String> retrieveExercise(int id)
	{
        ArrayList<String> results = new ArrayList<String>();

        String workoutTable = this.getWorkoutDetails(id, KEY_TABLE);	// Get table name from id
		        
        Cursor c = myDataBase.query(
        		workoutTable,
        		null,	// Select all columns
        		null,	// Select all rows
        		null,
        		null,
        		null,
        		null,
        		null);

		Log.d("debug", "Now displaying workout");
        if(c != null) {
			if(c.moveToFirst()) {
				do {
					int idx = c.getColumnIndex("exerciseId");
					if(idx != -1) { 	
						int exId = c.getInt(1);	// TODO constant
						String exName = this.retrieveExerciseDetail(exId, KEY_NAME);
						Log.d("debug", "Now retrieving exercise Nr: " + exId);
						results.add(exName);
					} else {
						Log.d("debug", "Error - retrieveWorkout - getColumnIndex()");
					}
				} while(c.moveToNext());
			} else {				
				Log.d("debug", "Error - retrieveWorkout- moveToFirst()");
			}
		} else {			
			Log.d("debug", "Error - retrieveWorkout - cursor null");
		}
        c.close();
    	return results;
	}
		
	public long addNewExercise(String exerciseName) 
	{
		long error = 0;
				
		Log.d("debug", "Creating exercise " + exerciseName);

		// TODO check for duplicates
		// --- Insert into workouts table ---------------------------------------------------------
		ContentValues myValues = new ContentValues();		
		myValues.put(KEY_NAME, exerciseName);
		
		error = myDataBase.insertOrThrow(DB_EXERCISES_TABLE, null, myValues);
		
		return error;
  	}
	
	public int getExerciseIdFromWorkoutAndListPosition(int workoutId, int exerciseListPos)
	{
		int result = -1;
		
        String workoutTable = this.getWorkoutDetails(workoutId, KEY_TABLE);	// Get table name from id
        
        Cursor c = myDataBase.query(
        		workoutTable,
        		new String[] {
        				KEY_EXERCISEID	// Select exerciseId
        		},
        		KEY_ROWID + " IN (SELECT MAX(" + KEY_ROWID + ") FROM " + workoutTable + " GROUP BY " + KEY_EXERCISEID + ")",
        		null,
        		null,
        		null,
        		KEY_EXERCISEID + " ASC",
        		null);
		
        if(c != null) {
			if(c.move(exerciseListPos)) {
				int idx = c.getColumnIndex("exerciseId");
				if(idx != -1) { 	
					int exId = c.getInt(idx);
					Log.d("debug", "Now retrieving exercise Nr: " + exId);
					result = exId;
				} else {
					Log.d("debug", "Error - getExerciseIdFromWorkoutAndListPosition(" + workoutId + ", " + exerciseListPos + ") - getColumnIndex()");
				}
			} else {				
				Log.d("debug", "Error - getExerciseIdFromWorkoutAndListPosition(" + workoutId + ", " + exerciseListPos + ")- moveToFirst()");
			}
		} else {			
			Log.d("debug", "Error - getExerciseIdFromWorkoutAndListPosition(" + workoutId + ", " + exerciseListPos + ") - cursor null");
		}
        c.close();
    	return result;
	}
	
	public Double getExerciseWeightIncrease(int workoutId, int exerciseId)
	{
		Double result = -1.0;
		
        String workoutTable = this.getWorkoutDetails(workoutId, KEY_TABLE);	// Get table name from id
		
	    Cursor c = myDataBase.query(
	    	workoutTable,
			new String[] {
				"MAX(" + KEY_WEIGHTINCREASE + ")"
			},
			KEY_EXERCISEID + "='" + exerciseId + "'",
			null,
			null,
			null,
			null);   
		
		if(c != null) {
			if(c.moveToFirst()) {
				int idx = -1;
				idx = c.getColumnIndex("MAX(" + KEY_WEIGHTINCREASE + ")");
				if(idx != -1) { 	
					result = c.getDouble(idx);
				} else {
					Log.d("debug", "Error getColumnIndex");
				}
			}else {
				Log.d("debug", "Error moveToFirst");
			}
		}else {
			Log.d("debug", "Error null");
		}

		c.close();
		return result;
	}
	
	// --- PROGRAMS ----------------------------------------------------------------------------------------------------------------------------------------
	public ArrayList<ArrayList<String>> retrieveAllPrograms()
	{ 
        ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
		Cursor c = myDataBase.rawQuery("SELECT * FROM " + DB_PROGRAMS_TABLE, null);

		if(c != null ) {		
			if(c.moveToFirst()) {
				do {
					ArrayList<String> row = new ArrayList<String>();
					String name = c.getString(c.getColumnIndex(KEY_NAME));
					row.add(name);
					results.add(row);
				} while (c.moveToNext());
			} 
		}		
        c.close();
    	return results;
	}
	
	public ArrayList<String> retrieveAllProgramNames()
	{ 
		ArrayList<String> results = new ArrayList<String>();
		Cursor c = myDataBase.rawQuery("SELECT * FROM " + DB_PROGRAMS_TABLE, null);

		if(c != null ) {		
			if(c.moveToFirst()) {
				do {
					String name = c.getString(c.getColumnIndex(KEY_NAME));
					results.add(name);
				} while (c.moveToNext());
			} 
		}		
        c.close();
    	return results;
	}
	
	public ArrayList<Integer> retrieveAllProgramIds()
	{ 
		ArrayList<Integer> results = new ArrayList<Integer>();
		
		Cursor c = myDataBase.rawQuery("SELECT " + KEY_ROWID + " FROM " + DB_PROGRAMS_TABLE, null);

		if(c != null ) {		
			if(c.moveToFirst()) {
				do {
					Integer name = c.getInt(c.getColumnIndex(KEY_ROWID));
					results.add(name);
				} while (c.moveToNext());
			} 
		}		
        c.close();
    	return results;
	}
	
	public String getProgramDetail(int id, String col)
	{ 
        String result = new String();
        
        Cursor c = myDataBase.query(
        		DB_PROGRAMS_TABLE, 
        		new String[] {
        			col
        		},
        		KEY_ROWID + "=" + id,
        		null,
        		null,
        		null,
        		null,
        		null);
        
        if(c != null) {
			if(c.moveToFirst()) {
				int idx = c.getColumnIndex(col);
				if(idx != -1) {
					result = c.getString(idx);					
				} else {
					Log.d("debug", "Error - getProgramDetail(" + id + ") - getColumnIdx");
				}
			} else {
				Log.d("debug", "Error - getProgramDetail(" + id + ") - moveToFirst");
			}
		} else {
			Log.d("debug", "Error - getProgramDetail(" + id + ") - cursor null");
		}

        c.close();
    	return result;
	}
	
	public int retrieveProgramIdFromName(String name)
	{ 
        int result = -1;
        
        Cursor c = myDataBase.query(
        		DB_PROGRAMS_TABLE, 
        		new String[] {
        			KEY_ROWID
        		},
        		KEY_NAME + "='" + name + "'",	//" + name + "
        		null,
        		null,
        		null,
        		null,
        		null);
        
        if(c != null) {
			if(c.moveToFirst()) {
				int idx = c.getColumnIndex(KEY_ROWID);
				if(idx != -1) {
					result = c.getInt(idx);
				} else {
					Log.d("debug", "Error - retrieveProgramIdFromName - getColumnIdx");
				}
			} else {
				Log.d("debug", "Error - retrieveProgramIdFromName - moveToFirst");
			}
		} else {
			Log.d("debug", "Error - retrieveProgramIdFromName - cursor null");
		}
		
        c.close();
    	return result;
	}
	
	public long removeProgram(String programName) 
	{
		long error = -1;
		
		// --- Retrieve id ------------------------------------------------------------------------
		int programId = this.retrieveProgramIdFromName(programName);
		
		// --- Delete workout table ---------------------------------------------------------------
		String programTableName = getProgramDetail(programId, KEY_TABLE);
		myDataBase.execSQL("DROP TABLE " + programTableName + ";");
				
		// --- Delete workout entry in workouts ---------------------------------------------------
		myDataBase.delete(DB_PROGRAMS_TABLE, KEY_ROWID + "='" + programId + "'", null);
		
		Log.d("debug", "removeProgram " + programName + " with id " + programId + " and table name " + programTableName);
		
		return error;
	}
	
	public long addNewProgram(String programName) 
	{
		long error = 0;
		String programTableName = programName;
		
		// --- Create table name ------------------------------------------------------------------
		// Lower case without special chars
		programTableName = programTableName.replaceAll("[^a-zA-Z0-9]", "");
		programTableName = programTableName.toLowerCase();
		
		Log.d("debug", "Creating workout " + programName + " with table name " + programTableName);
		
		// --- TODO check for duplicates

		// --- Insert into workouts table ---------------------------------------------------------
		ContentValues myValues = new ContentValues();		
		myValues.put(KEY_NAME,		programName);
		myValues.put(KEY_TABLE,	programTableName);
		
		error = myDataBase.insertOrThrow(DB_PROGRAMS_TABLE, null, myValues);
		
		// --- Create table for workout -----------------------------------------------------------
		if(error != -1) {			
			myDataBase.execSQL(
			"CREATE TABLE " + programTableName + " (" +
					"_id INTEGER PRIMARY KEY autoincrement," +
					"workout NUMERIC," +
					"scheduleType NUMERIC," +
					"schedule NUMERIC);"
			);
		}
		
		return error;
  	}
	
	public long addWorkoutToProgram(int programId, int workoutId)
	{
		long error = 0;

		// --- Retrieve workout table ------------------------------------------------------------------------
        String programTable = this.getProgramDetail(programId, KEY_TABLE);	// Get table name from id
        
        // TODO check for duplicates
		
		// --- Insert into workouts table ---------------------------------------------------------
		ContentValues myValues = new ContentValues();		
		myValues.put(KEY_WORKOUT, workoutId);
		
		error = myDataBase.insertOrThrow(programTable, null, myValues);
		
		return error;
	}
	
	public long removeWorkoutFromProgram(int programId, int workoutId)
	{
		long error = -1;

		// --- Retrieve workout table ------------------------------------------------------------------------
        String programTable = this.getProgramDetail(programId, KEY_TABLE);
		
		// --- Delete workout entry in workouts ---------------------------------------------------
		error = myDataBase.delete(
				programTable,
				KEY_WORKOUT + "='" + workoutId + "'",
				null);
		
		Log.d("debug", "Remove workout with id " + workoutId + " from " + programTable);		
		
		return error;
		
	}
	
	public int retrieveScheduleTypeFromProgram(int programId)
	{
		int result = -1;

		String programTable = getProgramDetail(programId, KEY_TABLE);

        Cursor c = myDataBase.query(
        		programTable, 
        		new String[] {
        			KEY_SCHEDULETYPE
        		},
        		null,
        		null,
        		null,
        		null,
        		null,
        		null);
        
        if(c != null) {
			if(c.moveToFirst()) {
				int idx = c.getColumnIndex(KEY_SCHEDULETYPE);
				if(idx != -1) {
					result = c.getInt(idx);
				} else {
					Log.d("debug", "Error - retrieveScheduleTypeFromProgram " + programId + " - getColumnIdx");
				}
			} else {
				Log.d("debug", "Error - retrieveScheduleTypeFromProgram " + programId + " - moveToFirst");
			}
		} else {
			Log.d("debug", "Error - retrieveScheduleTypeFromProgram " + programId + " - cursor null");
		}
		
        c.close();
		return result;
	}
	
	public int retrieveScheduleFromProgram(int programId, int schedType)
	{
		int result = -1;
		
		String programTable = getProgramDetail(programId, KEY_TABLE);

		Cursor c = null;
		
		//if(schedType == Gymlog.SCHEDULE_TYPE_ALTERNATE) {
		c = myDataBase.query(programTable, new String[] { KEY_SCHEDULE }, null, null, null, null, null, null);
		        
        if(c != null) {
			if(c.moveToFirst()) {
				int idx = c.getColumnIndex(KEY_SCHEDULE);
				if(idx != -1) { 
					result = c.getInt(idx);
				} else {
					Log.d("debug", "Error - retrieveScheduleFromProgram " + programId + " - getColumnIdx");
				}
			} else {
				Log.d("debug", "Error - retrieveScheduleFromProgram " + programId + " - moveToFirst");
			}
		} else {
			Log.d("debug", "Error - retrieveScheduleFromProgram " + programId + " - cursor null");
		}
		
		return result;
	}
	
	public void saveSchedule(int programId, int type, int schedule)
	{
		String programTable = getProgramDetail(programId, KEY_TABLE);
		
		ContentValues myValues = new ContentValues();	
		myValues.put(KEY_SCHEDULETYPE,	type);	
		myValues.put(KEY_SCHEDULE,		schedule);
		
		myDataBase.update(programTable, myValues, null, null);
	}
	
	public int getActiveWorkoutOnSchedule(int day)
	{
		int result = -1;

		// --- Get active program for today -------------------------------------------------------
		int activeProgram = getActiveProgramOnSchedule(day);
		
		if(activeProgram != -1) {
			// --- Get last workout for program -------------------------------------------------------
			int lastWorkout = getLastWorkoutFromSession(activeProgram);
	
			// --- Get todays workout -----------------------------------------------------------------
			if((lastWorkout != 0) && (lastWorkout != -1)) {
				result = getNextWorkoutFromProgram(activeProgram, lastWorkout);
			} else { // No workout has been done yet, load first
				result = getFirstWorkoutFromProgram(activeProgram);
			}
			
			Log.d("debug", "Last workout ID " + lastWorkout);
		}
		
		return result;		
	}
	
	public int getNextWorkoutFromProgram(int programId, int lastWorkout)
	{
		int results = -1;
		
		ArrayList<ArrayList<String>> workouts = retrieveWorkoutsFromProgram(programId);
		
		for (int i = 0; i < workouts.size(); i++) {
			ArrayList<String> row = workouts.get(i);
			int thisWorkout = Integer.parseInt(row.get(4));
			
			if(thisWorkout == lastWorkout) {
				int idxOfNxtWorkout = i + 1;
				
				if(idxOfNxtWorkout == workouts.size()) {
					idxOfNxtWorkout = 0;	// If idx is out of bounds, load first again	
				}
				row = workouts.get(idxOfNxtWorkout);
				results = Integer.parseInt(row.get(4));
				break;
			}
			
			//Log.d("debug", "" + i + " of " + workouts.size() + " workoutId " + thisWorkout + " looking for " + lastWorkout);
		}
		
		
		return results;	
	}
	
	public int getFirstWorkoutFromProgram(int programId)
	{
		int results = -1;

		String programTable = getProgramDetail(programId, KEY_TABLE);

		Cursor c = null;
		
		c = myDataBase.query(programTable, new String[] { KEY_WORKOUT }, null, null, null, null, KEY_ROWID + " ASC");
		        
        if(c != null) {
			if(c.moveToFirst()) {
				int idx = c.getColumnIndex(KEY_WORKOUT);
				if(idx != -1) { 
					results = c.getInt(idx);
				} else {
					Log.d("debug", "Error - retrieveScheduleFromProgram " + programId + " - getColumnIdx");
				}
			} else {
				Log.d("debug", "Error - retrieveScheduleFromProgram " + programId + " - moveToFirst");
			}
		} else {
			Log.d("debug", "Error - retrieveScheduleFromProgram " + programId + " - cursor null");
		}
		
		return results;	
	}
	
	public int getActiveProgramOnSchedule(int day)
	{
		int results = -1;
		ArrayList<Integer> programIds = retrieveAllProgramIds();
		
		for(int i = 0; i < programIds.size(); i++) {
			int type = retrieveScheduleTypeFromProgram(programIds.get(i));
			
			if(type != -1) {
				int schedule = retrieveScheduleFromProgram(programIds.get(i), type);				
				if(schedule != -1) {					
					if((schedule & day) != 0) {
						results = programIds.get(i);
						break;
					}	
				}					
			}					
		}		
		return results;		
	}
	
	// --- WORKOUTS ----------------------------------------------------------------------------------------------------------------------------------------
	public ArrayList<ArrayList<String>> retrieveWorkoutsFromProgram(int program)
	{ 
        ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
        
        if(program != 0) {
        	String programTable = getProgramDetail(program, KEY_TABLE);
        	Log.d("debug", "table " + programTable);
        	
	        Cursor c = myDataBase.query(
	        		programTable, 
	        		new String[] {
	        			KEY_WORKOUT,
	        			KEY_SCHEDULETYPE,
	        			KEY_SCHEDULE
	        		},
	        		null,
	        		null,
	        		null,
	        		null,
	        		null,
	        		null);
			
			if(c != null ) {		
				if(c.moveToFirst()) {
					int idx = -1;
					int workoutId = -1;
					
					do {
						int val = 0;
						ArrayList<String> row = new ArrayList<String>();
						String name = null;
						String type = null;
						
						idx = c.getColumnIndex(KEY_WORKOUT);						
						if(idx != -1) {
							workoutId = c.getInt(idx);							
							name = getWorkoutDetails(workoutId, KEY_NAME);
							if(name != null) { row.add(name); }
							type = getWorkoutDetails(workoutId, KEY_TYPE);
							if(type != null) { row.add(type); }
						}	
						idx = c.getColumnIndex(KEY_SCHEDULETYPE);
						if(idx != -1) {
							val = c.getInt(idx);
							row.add("" + val);
						}						
						
						idx = c.getColumnIndex(KEY_SCHEDULE);
						if(idx != -1) {
							val = c.getInt(idx);
							row.add("" + val);
						}
						
						if((name != null) && (type != null)) {
							row.add("" + workoutId);
							results.add(row);
						}
					} while (c.moveToNext());
				} 
			}		
	        c.close();
        } else {
        	results = retrieveAllWorkouts();
        }
    	return results;
	}
	
	public ArrayList<ArrayList<String>> retrieveAllWorkouts()
	{ 
        ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
		Cursor c = myDataBase.rawQuery("SELECT * FROM " + DB_WORKOUTS_TABLE, null);

		if(c != null ) {		
			if(c.moveToFirst()) {
				do {
					ArrayList<String> row = new ArrayList<String>();
					String name = c.getString(c.getColumnIndex(KEY_NAME));
					String type = c.getString(c.getColumnIndex(KEY_TYPE));
					row.add(name);
					row.add(type);
					results.add(row);
				} while (c.moveToNext());
			} 
		}		
        c.close();
    	return results;
	}

	public String getWorkoutDetails(int id, String col)
	{ 
        String result = null;
        
        Cursor c = myDataBase.query(
        		DB_WORKOUTS_TABLE, 
        		new String[] {
        			col
        		},
        		KEY_ROWID + "=" + id,
        		null,
        		null,
        		null,
        		null,
        		null);
        
        if(c != null) {
			if(c.moveToFirst()) {
				int idx = c.getColumnIndex(col);
				if(idx != -1) {
					result = c.getString(idx);					
				} else {
					Log.d("debug", "Error - retrieveWorkoutDetails(" + id + ") - getColumnIdx");
				}
			} else {
				Log.d("debug", "Error - retrieveWorkoutDetails(" + id + ") - moveToFirst");
			}
		} else {
			Log.d("debug", "Error - retrieveWorkoutDetails(" + id + ") - cursor null");
		}

        c.close();
    	return result;
	}

	public ArrayList<String> retrieveWorkout(int id)
	{
        ArrayList<String> results = new ArrayList<String>();
        
        String workoutTable = this.getWorkoutDetails(id, KEY_TABLE);	// Get table name from id
        
        Cursor c = myDataBase.query(
        		workoutTable,
        		null,	// Select all columns
        		KEY_ROWID + " IN (SELECT MAX(" + KEY_ROWID + ") FROM " + workoutTable + " GROUP BY " + KEY_EXERCISEID + ")",	// Select only different exercises
        		null,
        		null,
        		null,
        		KEY_EXERCISEID + " ASC",
        		null);
        
        if(c != null) {
			if(c.moveToFirst()) {
				do {
					int idx = c.getColumnIndex(KEY_EXERCISEID);
					if(idx != -1) { 	
						int exId = c.getInt(idx);
						String exName = this.retrieveExerciseDetail(exId, KEY_NAME);
						results.add(exName);
					} else {
						Log.d("debug", "Error - retrieveWorkout(" + id + ") - getColumnIndex()");
					}
				} while(c.moveToNext());
			} else {				
				Log.d("debug", "Error - retrieveWorkout(" + id + ") - moveToFirst()");
			}
		} else {			
			Log.d("debug", "Error - retrieveWorkout(" + id + ") - cursor null");
		}
        c.close();
    	return results;
	}
			
	public ArrayList<ArrayList<String>> retrieveWholeWorkout(int workoutId)
	{
    	return retrievWorkout(workoutId, 1);	// TODO constants;
	}
	
	public ArrayList<ArrayList<String>> retrieveAllExerciseFromWorkout(int workoutId)
	{       
    	return retrievWorkout(workoutId, 2);	// TODO constants;
	}
	
	public ArrayList<ArrayList<String>> retrievWorkout(int workoutId, int type)
	{
        ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
        
        String workoutTable = this.getWorkoutDetails(workoutId, KEY_TABLE);	// Get table name from id
		        
        Cursor c = myDataBase.query(
        		workoutTable,
        		null,	// Select all columns
        		null,	// Select all rows
        		null,
        		null,
        		null,
        		KEY_ROWID + " ASC",
        		null);
		        
        if(c != null) {
			if(c.moveToFirst()) {
				do {
					ArrayList<String> row = new ArrayList<String>();

					int val = -1;
					int idx = -1;
					
					if(type == 1) { // TODO DEFINES
						// Get exercise ID
						idx = c.getColumnIndex(KEY_EXERCISEID);
						if(idx != -1) { 	
							val = c.getInt(idx);
							row.add("" + val);
						} else { Log.d("debug", "Error - retrieveWholeWorkout(" + workoutId + ") - getColumnIndex(KEY_EXERCISEID)");
						}			
					}
					
					if(type == 2) {
						// Get setNr
						idx = c.getColumnIndex("setNr");
						if(idx != -1) { 	
							int setNr = c.getInt(idx);
							row.add(0, "" + setNr);
						} else { Log.d("debug", "Error - retrieveAllExerciseFromWorkout(" + workoutId + ") - getColumnIndex()");
						}
					}
					
					// Get weight
					idx = c.getColumnIndex(KEY_WEIGHT);
					if(idx != -1) { 	
						Double weight = c.getDouble(idx);
						row.add("" + weight);
					} else { Log.d("debug", "Error - retrieveWholeWorkout(" + workoutId + ") - getColumnIndex(KEY_WEIGHT)");
					}
					
					// Get repetitions
					idx = c.getColumnIndex(KEY_REPS);
					if(idx != -1) { 	
						val = c.getInt(idx);
						row.add("" + val);
					} else { Log.d("debug", "Error - retrieveWholeWorkout(" + workoutId + ") - getColumnIndex(KEY_REPS)");
					}
					results.add(row);
				} while(c.moveToNext());
			} else {				
				Log.d("debug", "Error - retrieveWholeWorkout(" + workoutId + ")- moveToFirst()");
			}
		} else {			
			Log.d("debug", "Error - retrieveWholeWorkout(" + workoutId + ") - cursor null");
		}
        c.close();
        return results;
	}
	
	public ArrayList<ArrayList<String>> retrieveExerciseFromWorkout(int exerciseId, int workoutId)
	{
        ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
        
        String workoutTable = this.getWorkoutDetails(workoutId, KEY_TABLE);	// Get table name from id
        
        Cursor c = myDataBase.query(
        		workoutTable,
        		null,	// Select all columns
        		KEY_EXERCISEID + "=" + exerciseId,	// Select all rows
        		null,
        		null,
        		null,
        		KEY_SETNR + " ASC",
        		null);
		
        if(c != null) {
			if(c.moveToFirst()) {
				do {
					ArrayList<String> row = new ArrayList<String>();
					
					int idx = c.getColumnIndex("setNr");
					if(idx != -1) { 	
						int setNr = c.getInt(idx);
						row.add("" + setNr);
					} else { Log.d("debug", "Error - retrieveWorkout(" + exerciseId + ", " + workoutId + ") - getColumnIndex()");
					}
					idx = c.getColumnIndex("weight");
					if(idx != -1) { 	
						Double weight = c.getDouble(idx);
						row.add("" + weight);
					} else { Log.d("debug", "Error - retrieveWorkout(" + exerciseId + ", " + workoutId + ") - getColumnIndex()");
					}
					idx = c.getColumnIndex("reps");
					if(idx != -1) { 	
						int reps = c.getInt(idx);
						row.add("" + reps);
					} else { Log.d("debug", "Error - retrieveWorkout(" + exerciseId + ", " + workoutId + ") - getColumnIndex()");
					}
					results.add(row);
				} while(c.moveToNext());
			} else {				
				Log.d("debug", "Error - retrieveWorkout(" + exerciseId + ", " + workoutId + ")- moveToFirst()");
			}
		} else {			
			Log.d("debug", "Error - retrieveWorkout(" + exerciseId + ", " + workoutId + ") - cursor null");
		}
        c.close();
    	return results;
	}
		
	public long addNewWorkout(String workoutName, int type) 
	{
		long error = 0;
		String workoutTableName = workoutName;
		
		// --- Create table name ------------------------------------------------------------------
		// Lower case without special chars
		workoutTableName = workoutTableName.replaceAll("[^a-zA-Z0-9]", "");
		workoutTableName = workoutTableName.toLowerCase();
		
		Log.d("debug", "Creating workout " + workoutName + " with table name " + workoutTableName);
		
		// --- TODO check for duplicates

		// --- Insert into workouts table ---------------------------------------------------------
		ContentValues myValues = new ContentValues();		
		myValues.put("name",		workoutName);	
		myValues.put("type",		type);
		myValues.put("tableName",	workoutTableName);
		
		error = myDataBase.insertOrThrow(DB_WORKOUTS_TABLE, null, myValues);
		
		// --- Create table for workout -----------------------------------------------------------
		if(error != -1) {			
			myDataBase.execSQL(
			"CREATE TABLE " + workoutTableName + " (" +
					"_id INTEGER PRIMARY KEY autoincrement," +
					"exerciseId NUMERIC," +
					"setNr NUMERIC," +
					"weight NUMERIC," +
					"reps NUMERIC," +
					"weightIncreaseMode NUMCERIC," +
					"weightIncrease NUMBERIC);"
			);
		}
		
		return error;
  	}
	
	public long removeWorkout(String workoutName) 
	{
		long error = 0;
		
		// --- Retrieve id ------------------------------------------------------------------------
		int workoutId = this.retrieveWorkoutIdFromName(workoutName);

		int type = this.getWorkoutType(workoutId);
		
		// --- If circle workout is removed, remove circles, too
		if(type == 2) { removeAllCirclesFromWorkout(workoutId); }
		
		// --- Delete workout table ---------------------------------------------------------------
		String workoutTableName = getWorkoutDetails(workoutId, KEY_TABLE);
		myDataBase.execSQL("DROP TABLE " + workoutTableName + ";");
				
		// --- Delete workout entry in workouts ---------------------------------------------------
		myDataBase.delete(
				DB_WORKOUTS_TABLE,
				KEY_ROWID + "='" + workoutId + "'",
				null);
		
		Log.d("debug", "removeWorkout " + workoutName + " with id " + workoutId + " and table name " + workoutTableName);
		
		return error;
	}
	
	int getWorkoutType(int workoutId)   
	{
		int error = -1;
		
		Log.d("debug", "getWorkoutType");
        
        Cursor c = myDataBase.query(
        		DB_WORKOUTS_TABLE,
        		new String[] {
        				KEY_TYPE	// Select all columns
        		},
        		KEY_ROWID + "='" + workoutId + "'",
        		null,
        		null,
        		null,
        		KEY_ROWID + " ASC",
        		null);
		        
        if(c != null) {
			if(c.moveToFirst()) {
				int idx = c.getColumnIndex(KEY_TYPE);
				if(idx != -1) { 	
					error = c.getInt(idx);	
				}
			}
        }
		
		return error;
	}
	
	public int retrieveWorkoutIdFromName(String name)
	{ 
        int result = -1;
        
		Log.d("debug", "retrieve ID for " + name);
        
        Cursor c = myDataBase.query(
        		DB_WORKOUTS_TABLE, 
        		new String[] {
        			KEY_ROWID
        		},
        		KEY_NAME + "='" + name + "'",	//" + name + "
        		null,
        		null,
        		null,
        		null,
        		null);
        
        if(c != null) {
			if(c.moveToFirst()) {
				int idx = c.getColumnIndex(KEY_ROWID);
				if(idx != -1) {
					result = c.getInt(idx);
				} else {
					Log.d("debug", "Error - retrieveWorkoutIdFromName - getColumnIdx");
				}
			} else {
				Log.d("debug", "Error - retrieveWorkoutIdFromName - moveToFirst");
			}
		} else {
			Log.d("debug", "Error - retrieveWorkoutIdFromName - cursor null");
		}
		
        c.close();
    	return result;
	}
	
	public long addExerciseToWorkout(int workoutId, String exerciseName) 
	{
		long error = 0;

		// --- Retrieve workout table ------------------------------------------------------------------------
        String workoutTable = this.getWorkoutDetails(workoutId, KEY_TABLE);	// Get table name from id
        
        // --- Retrieve exercise id ---------------------------------------------------------------
        int exerciseId = retrieveExerciseIdFromName(exerciseName);
		
		Log.d("debug", "Add exercise " + exerciseName + " to " + workoutTable);
		
		// --- Insert into workouts table ---------------------------------------------------------
		ContentValues myValues = new ContentValues();		
		myValues.put(KEY_EXERCISEID, exerciseId);
		
		error = myDataBase.insertOrThrow(workoutTable, null, myValues);
		
		return error;
  	}
	
	public long updateSingleExerciseFromWorkoutByRow(int workoutId, int type, int exerciseId, int rowNr, double weight, int reps)
	{
		long error = 0;
		
		// --- Retrieve row id to update -------------------------------------------------------------------- 
		int rowId = getRowIdFromListPositionInWorkout(workoutId, type, exerciseId, rowNr);

		// --- Retrieve workout table -----------------------------------------------------------------------
        String workoutTable = this.getWorkoutDetails(workoutId, KEY_TABLE);
        
        // --- Put values -----------------------------------------------------------------------------------
		ContentValues myValues = new ContentValues();		
		myValues.put("weight",		weight);	
		myValues.put("reps",		reps);
        
        // --- Update row -----------------------------------------------------------------------------------
        myDataBase.update(
        	workoutTable,
        	myValues,
        	"_id='" + rowId + "'",
        	null
        );
        
        Log.d("debug", "Inserted into "  + workoutTable + " at row id " + rowId + " which was at pos " + rowNr + " values " + weight + ", " + reps + " with result " + error);
        
		return error;
	}
	
	public int getRowIdFromListPositionInWorkout(int workoutId, int type, int exerciseId, int listPos)
	{
		int result = -1;
		String where = null;
		
		// --- Retrieve workout table ------------------------------------------------------------------------
        String workoutTable = this.getWorkoutDetails(workoutId, KEY_TABLE);	// Get table name from id
        
        // If type
        if(type == 1) { where = KEY_EXERCISEID + "='" + exerciseId + "'"; }
        
		// --- Delete workout entry in workouts ---------------------------------------------------   
		Cursor c = myDataBase.query(
				workoutTable,
				new String[] {
						KEY_ROWID
				},
				where,	// Select all
				null,
				null,
				null,
				null
		);
		
		int ctr = 0;
		
        if(c != null) {
        	int[] allIds = new int[1000];
        	
			if(c.moveToFirst()) {
				do {
					int idx = c.getColumnIndex(KEY_ROWID);
					if(idx != -1) {
						allIds[ctr] = c.getInt(idx);
						ctr++;
			        } else {
						Log.d("debug", "Error - removeSingleExerciseFromWorkoutByRow - getColumnIdx");
					}
				} while(c.moveToNext());
				result = allIds[listPos];
				
		        Log.d("debug", "Found row id " + allIds[listPos] + " which was at pos " + listPos + " from " + workoutTable);
				
			} else {
				Log.d("debug", "Error - removeSingleExerciseFromWorkoutByRow - moveToFirst");
			}
		} else {
			Log.d("debug", "Error - removeSingleExerciseFromWorkoutByRow - cursor null");
		}   
        c.close();
		return result;
	}
	
	// use setNr to delete row
	// setNr should indicate the circle or exercise circle (5x 5x Squat)
	public long removeSingleExerciseFromWorkoutByRow(int workoutId, int type, int exerciseId, int rowNr) 
	{
		long rowId = 0;
		int error = 0;

		// --- Retrieve workout table ------------------------------------------------------------------------
        String workoutTable = this.getWorkoutDetails(workoutId, KEY_TABLE);
        		
		// --- Get row id from position ----------------------------------------------------------------------   
		rowId = getRowIdFromListPositionInWorkout(workoutId, type, exerciseId, rowNr);
		
		if(rowId != -1) {
			error = myDataBase.delete(
						workoutTable,
						"ROWID='" + rowId + "'",
						null);
	        Log.d("debug", "Delete row " + rowId + " which was at pos " + rowNr + " from " + workoutTable + " with result " + error);
		} else {
			Log.d("debug", "Error - removeSingleExerciseFromWorkoutByRow - cursor null");
		}        		
		
		return error;
  	}
	
	// TODO this is more of a "delete circle"
	// use setNr to delete row
	// setNr should indicate the circle or exercise circle (5x 5x Squat)
	public long removeExerciseFromWorkout(int workoutId, String exerciseName) 
	{
		long error = 0;

		// --- Retrieve workout table ------------------------------------------------------------------------
        String workoutTable = this.getWorkoutDetails(workoutId, KEY_TABLE);	// Get table name from id
        
        // --- Retrieve exercise id ---------------------------------------------------------------
        int exerciseId = retrieveExerciseIdFromName(exerciseName);
		
		// --- Delete workout entry in workouts ---------------------------------------------------
		error = myDataBase.delete(
				workoutTable,
				KEY_EXERCISEID + "='" + exerciseId + "'",
				null);
		
		Log.d("debug", "Remove exercise " + exerciseName + " with id " + exerciseId + " from " + workoutTable);		
		
		return error;
  	}
	
	// --- CIRCLES ----------------------------------------------------------------------------------------------------------------------------------
	public ArrayList<String> retrieveCircles(int id)
	{
        ArrayList<String> results = new ArrayList<String>();
		
		// --- Retrieve workout table -------------------------------------------------------------
        String workoutTable = this.getWorkoutDetails(id, KEY_TABLE);
		        
        Cursor c = myDataBase.query(
        		workoutTable,
        		null,
        		KEY_ROWID + " IN (SELECT MAX(" + KEY_ROWID + ") FROM " + workoutTable + " GROUP BY " + KEY_SETNR + ")",
        		null, null, null, null, null);
        if(c != null) {
			if(c.moveToFirst()) {
				do {
					int idx = c.getColumnIndex(KEY_SETNR);
					if(idx != -1) { 	
						int exId = c.getInt(idx);
						String exName = this.retrieveCircleName(exId);
						results.add(exName);
					} else {
						Log.d("debug", "Error - retrieveCircles - getColumnIndex()");
					}
				} while(c.moveToNext());
			} else {				
				Log.d("debug", "Error - retrieveCircles - moveToFirst()");
			}
		} else {			
			Log.d("debug", "Error - retrieveCircles - cursor null");
		}
        
        c.close();
    	return results;
	}
	
	public String retrieveCircleName(int id)
	{
        String result = new String();
        Cursor c = myDataBase.query(
        		DB_CIRCLES_TABLE, 
        		new String[] {
        			KEY_NAME
        		},
        		KEY_ROWID + "=" + id,
        		null,
        		null,
        		null,
        		null,
        		null);
        
        if(c != null) {
			if(c.moveToFirst()) {
				int idx = c.getColumnIndex(KEY_NAME);
				if(idx != -1) {
					result = c.getString(idx);
					//Log.d("debug", "retrieveCircleDetail: " + result);
				} else {
					Log.d("debug", "Error - retrieveCircleDetail " + id + " - getColumnIdx");
				}
			} else {
				Log.d("debug", "Error - retrieveCircleDetail " + id + " - moveToFirst");
			}
		} else {
			Log.d("debug", "Error - retrieveCircleDetail " + id + " - cursor null");
		}

        c.close();
    	return result;
	}
	
	public void addNewCircle(int workoutId, String circleName)
	{
		long id = -1;
		
		// --- Add to circle table and retrieve id ------------------------------------------------
		ContentValues myValues = new ContentValues();		
		myValues.put(KEY_NAME, circleName);			
        
        id = myDataBase.insertOrThrow(
        	DB_CIRCLES_TABLE,
        	null,
        	myValues
        );
		
        // TODO check for Duplicates
        
		// --- Retrieve workout table ------------------------------------------------------------------------
        String workoutTable = this.getWorkoutDetails(workoutId, KEY_TABLE);	
		myValues = new ContentValues();
		myValues.put(KEY_SETNR, id);
		myValues.put(KEY_EXERCISEID, 0);
		
        myDataBase.insertOrThrow(
        		workoutTable,
            	null,
            	myValues
            );
        
		//Log.d("debug", "Add circle with name " + circleName + ", it now has id " + id + " and has been added to " + workoutTable);
	}
	
	public long removeCircle(int workoutId, String name) 
	{
		long error = 0;

		// --- Retrieve workout table ------------------------------------------------------------------------
        String workoutTable = this.getWorkoutDetails(workoutId, KEY_TABLE);	// Get table name from id
        
        // --- Retrieve exercise id ---------------------------------------------------------------
        int circleId = retrieveCircleIdFromName(name);
		
		// --- Delete workout entry in workouts ---------------------------------------------------
		error = myDataBase.delete(
				DB_CIRCLES_TABLE,
				KEY_ROWID + "='" + circleId + "'",
				null);
		
		error = myDataBase.delete(
				workoutTable,
				KEY_SETNR + "='" + circleId + "'",
				null);
		
		//Log.d("debug", "Remove circle " + name + " with id " + circleId + " from " + workoutTable);		
		
		return error;
  	}
	
	public long removeAllCirclesFromWorkout(int workoutId)
	{
		long error = -1;
		
		String workoutTable = this.getWorkoutDetails(workoutId, KEY_TABLE);	// Get table name from id
		//Log.d("debug", "removeAllCirclesFromWorkout");
		        
        Cursor c = myDataBase.query(
        		workoutTable,
        		new String[] {
        				KEY_SETNR	// Select all columns
        		},
        		KEY_ROWID + " IN (SELECT MAX(" + KEY_ROWID + ") FROM " + workoutTable + " GROUP BY " + KEY_SETNR + ")",
        		null,
        		null,
        		null,
        		KEY_ROWID + " ASC",
        		null);
		        
        if(c != null) {
			if(c.moveToFirst()) {
				int idx = -1;
				
				// Get setNr
				idx = c.getColumnIndex(KEY_SETNR);
				if(idx != -1) { 	
					int setNr = c.getInt(idx);					

					error = myDataBase.delete(
							DB_CIRCLES_TABLE,
							KEY_ROWID + "='" + setNr + "'",
							null);
					
					//Log.d("debug", "Delete circle from circle table " + setNr);
				} else { Log.d("debug", "Error - loadSingleExerciseFromCircle(" + workoutId + ") - getColumnIndex()");
				}					
			} else {				
				Log.d("debug", "Error - loadSingleExerciseFromCircle(" + workoutId + ")- moveToFirst()");
			}
		} else {			
			Log.d("debug", "Error - loadSingleExerciseFromCircle(" + workoutId + ") - cursor null");
		}
        c.close();
		
		return error;
	}
		
	public int retrieveCircleIdFromName(String name)
	{ 
        int result = -1;
        
		//Log.d("debug", "retrieve ID for " + name);
        
        Cursor c = myDataBase.query(
        		DB_CIRCLES_TABLE, 
        		new String[] {
        			KEY_ROWID
        		},
        		KEY_NAME + "='" + name + "'",	//" + name + "
        		null,
        		null,
        		null,
        		null,
        		null);
        
        if(c != null) {
			if(c.moveToFirst()) {
				int idx = c.getColumnIndex(KEY_ROWID);
				if(idx != -1) {
					result = c.getInt(idx);
					//Log.d("debug", "" + result);
				} else {
					Log.d("debug", "Error - retrieveCircleIdFromName - getColumnIdx");
				}
			} else {
				Log.d("debug", "Error - retrieveCircleIdFromName - moveToFirst");
			}
		} else {
			Log.d("debug", "Error - retrieveCircleIdFromName - cursor null");
		}
		
        c.close();
    	return result;
	}

	public long addExerciseToCircle(int workoutId, int circleNr, int exerciseId)
	{
		long error = 0;

		// --- Retrieve workout table ------------------------------------------------------------------------
        String workoutTable = this.getWorkoutDetails(workoutId, KEY_TABLE);	// Get table name from id
        				
		// --- Insert into workouts table ---------------------------------------------------------
		ContentValues myValues = new ContentValues();		
		myValues.put(KEY_SETNR, circleNr);	
		myValues.put(KEY_EXERCISEID, exerciseId);	
		
		error = myDataBase.insertOrThrow(workoutTable, null, myValues);
		
		//Log.d("debug", "Add exercise " + exerciseId + " to circle " + circleNr + " in " + workoutTable + " with result " + error);
		
		return error;
	}

	public ArrayList<ArrayList<String>> retrieveCircle(int workoutId, int circleId)
	{
        ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
        
        String workoutTable = this.getWorkoutDetails(workoutId, KEY_TABLE);	// Get table name from id
		        
        Cursor c = myDataBase.query(
        		workoutTable,
        		null,	// Select all columns
        		KEY_SETNR + "='" + circleId + "'",	// Select only current circle
        		null,
        		null,
        		null,
        		KEY_ROWID + " ASC",
        		null);
		        
        if(c != null) {
			if(c.moveToFirst()) {
				do {
					ArrayList<String> row = new ArrayList<String>();

					int val = -1;
					int idx = -1;
					
					// Get setNr
					idx = c.getColumnIndex("setNr");
					if(idx != -1) { 	
						int setNr = c.getInt(idx);
						row.add(0, "" + setNr);
					} else { Log.d("debug", "Error - retrieveAllExerciseFromWorkout(" + workoutId + ") - getColumnIndex()");
					}
					
					// Get weight
					idx = c.getColumnIndex(KEY_WEIGHT);
					if(idx != -1) { 	
						Double weight = c.getDouble(idx);
						row.add("" + weight);
					} else { Log.d("debug", "Error - retrievCircle(" + workoutId + "," + circleId + ") - getColumnIndex(KEY_WEIGHT)");
					}
					
					// Get repetitions
					idx = c.getColumnIndex(KEY_REPS);
					if(idx != -1) { 	
						val = c.getInt(idx);
						row.add("" + val);
					} else { Log.d("debug", "Error - retrievCircle(" + workoutId + "," + circleId + ") - getColumnIndex(KEY_REPS)");
					}
						
					// Get exercise ID
					idx = c.getColumnIndex(KEY_EXERCISEID);
					if(idx != -1) { 	
						val = c.getInt(idx);
						row.add("" + val);
					} else { Log.d("debug", "Error - retrievCircle(" + workoutId + "," + circleId + ") - getColumnIndex(KEY_EXERCISEID)");
					}
					
					results.add(row);
				} while(c.moveToNext());
			} else {
				Log.d("debug", "Error - retrievCircle(" + workoutId + "," + circleId + ")- moveToFirst()");
			}
		} else {			
			Log.d("debug", "Error - retrieveWholeWorkout(" + workoutId + "," + circleId + ") - cursor null");
		}
        c.close();
        return results;
	}
	
	public long removeExerciseFromCircle(int workoutId, int circleId, int exerciseId) 
	{
		long error = 0;

		// --- Retrieve workout table ------------------------------------------------------------------------
        String workoutTable = this.getWorkoutDetails(workoutId, KEY_TABLE);	// Get table name from id
        		
		// --- Delete workout entry in workouts ---------------------------------------------------
		error = myDataBase.delete(
				workoutTable,
				"(" + KEY_EXERCISEID + "='" + exerciseId + "' AND " + KEY_SETNR + "='" + circleId + "')",
				null);
		
		//Log.d("debug", "Remove exercise " + exerciseId + " from " + workoutTable + " from circle " + circleId + " with result " + error);		
		
		return error;
  	}

	/*****************************************************
	 * Deletes all duplicate exercise entries for circle *
	 *****************************************************/
	public long prepareCircleForSetup(int workoutId, int circleId)
	{
		long error = -1;

        String workoutTable = this.getWorkoutDetails(workoutId, KEY_TABLE);	// Get table name from id
		Log.d("debug", "Now retrieving exercises from " + workoutTable + " from circle " + circleId);
		
		// --- Delete workout entry in workouts ---------------------------------------------------
		error = myDataBase.delete(
				workoutTable,
				// SELECT * FROM zirkeltraining WHERE (_id  NOT IN (SELECT MAX(_id) FROM zirkeltraining GROUP BY exerciseId)) AND setNr=4
        		"(" + KEY_ROWID + " NOT IN (SELECT MAX(" + KEY_ROWID + ") FROM " + workoutTable + " GROUP BY " + KEY_EXERCISEID + ")) AND " + KEY_SETNR + "='" + circleId + "'",
				null);
		
		return error;
	}
	
	public long copyExercisesInCircle(int workoutId, int circleId, int circleRepetetions)
	{
		long error = -1;

        String workoutTable = this.getWorkoutDetails(workoutId, KEY_TABLE);

		//Log.d("debug", "Now retrieving exercise to copy:");
		ArrayList<ArrayList<String>> exValues = loadDifferentExerciseFromCircle(workoutId, circleId);
		
		//Log.d("debug", "Now copying circle:");		
		for(int i = 0; i < (circleRepetetions-1); i++) {	// Repeat the following circleRep times			
			//Log.d("debug", "Circle rep nr " + i + " of " + (circleRepetetions-1));			
			for(int j = 0; j < exValues.size(); j++) {		// Insert different exercises after each other
				//Log.d("debug", "Exercise in circle Nr " + j + " of " + exValues.size());
				
				// --- Insert into workouts table ---------------------------------------------------------
				ContentValues myValues = new ContentValues();		
				
				myValues.put(KEY_SETNR,					exValues.get(j).get(0));	
				myValues.put(KEY_EXERCISEID,			exValues.get(j).get(1));	
				myValues.put(KEY_WEIGHT,				exValues.get(j).get(2));	
				myValues.put(KEY_REPS,					exValues.get(j).get(3));	
				myValues.put(KEY_WEIGHTINCREASEMODE,	exValues.get(j).get(4));	
				myValues.put(KEY_WEIGHTINCREASE,		exValues.get(j).get(5));
				
				error = myDataBase.insertOrThrow(workoutTable, null, myValues);
				
				//Log.d("debug", "Inserting exercise " + exValues.get(j).toString() + " #" + (i+1) + " with result " + error);
				//Log.d("debug", "Value of myValues " + myValues.toString());
				//Log.d("debug", retrieveCircle(workoutId, circleId).toString());
			}
		}
				
		return error;
	}
	
	ArrayList<ArrayList<String>> loadDifferentExerciseFromCircle(int workoutId, int circleId)
	{
		ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
		
        String workoutTable = this.getWorkoutDetails(workoutId, KEY_TABLE);	// Get table name from id
		//Log.d("debug", "Now retrieving exercises from " + workoutTable + " from circle " + circleId);
		        
        Cursor c = myDataBase.query(
        		workoutTable,
        		null,	// Select all columns
        		KEY_ROWID + " IN (SELECT MAX(" + KEY_ROWID + ") FROM " + workoutTable + " GROUP BY " + KEY_EXERCISEID + ")",
        		null,
        		null,
        		null,
        		KEY_ROWID + " ASC",
        		null);
		        
        if(c != null) {
			if(c.moveToFirst()) {
				do {
					int val = -1;
					int idx = -1;
					ArrayList<String> row = new ArrayList<String>();
					
					// Get setNr
					idx = c.getColumnIndex(KEY_SETNR);
					if(idx != -1) { 	
						val = c.getInt(idx);
						row.add("" + val);
						//Log.d("debug", "Got set id " + val);
					} else { Log.d("debug", "Error - loadSingleExerciseFromCircle(" + workoutId + ") - getColumnIndex()");
					}
						
					// Get exercise ID
					idx = c.getColumnIndex(KEY_EXERCISEID);
					if(idx != -1) { 	
						val = c.getInt(idx);
						row.add("" + val);
						//Log.d("debug", "Got exercise id " + val);
					} else { Log.d("debug", "Error - loadSingleExerciseFromCircle(" + workoutId + "," + circleId + ") - getColumnIndex(KEY_EXERCISEID)");
					}
					
					// Get weight
					idx = c.getColumnIndex(KEY_WEIGHT);
					if(idx != -1) { 	
						Double weight = c.getDouble(idx);
						row.add("" + weight);
						//Log.d("debug", "Got weight " + weight);
					} else { Log.d("debug", "Error - loadSingleExerciseFromCircle(" + workoutId + "," + circleId + ") - getColumnIndex(KEY_WEIGHT)");
					}
					
					// Get repetitions
					idx = c.getColumnIndex(KEY_REPS);
					if(idx != -1) { 	
						val = c.getInt(idx);
						row.add("" + val);
						//Log.d("debug", "Got reps " + val);
					} else { Log.d("debug", "Error - loadSingleExerciseFromCircle(" + workoutId + "," + circleId + ") - getColumnIndex(KEY_REPS)");
					}
						
					// Get exercise ID
					idx = c.getColumnIndex(KEY_WEIGHTINCREASEMODE);
					if(idx != -1) { 	
						val = c.getInt(idx);
						row.add("" + val);
						//Log.d("debug", "Got weight increase mode " + val);
					} else { Log.d("debug", "Error - loadSingleExerciseFromCircle(" + workoutId + "," + circleId + ") - getColumnIndex(KEY_EXERCISEID)");
					}
						
					// Get exercise ID
					idx = c.getColumnIndex(KEY_WEIGHTINCREASE);
					if(idx != -1) { 	
						Double wMode = c.getDouble(idx);
						row.add("" + wMode);
						//Log.d("debug", "Got weight increase " + wMode);
					} else { Log.d("debug", "Error - loadSingleExerciseFromCircle(" + workoutId + "," + circleId + ") - getColumnIndex(KEY_EXERCISEID)");
					}
					
					results.add(row);
				} while(c.moveToNext());
			} else {
				Log.d("debug", "Error - loadSingleExerciseFromCircle(" + workoutId + "," + circleId + ")- moveToFirst()");
			}
		} else {
			Log.d("debug", "Error - loadSingleExerciseFromCircle(" + workoutId + "," + circleId + ") - cursor null");
		}
        c.close();
		
		return results;
	}

}