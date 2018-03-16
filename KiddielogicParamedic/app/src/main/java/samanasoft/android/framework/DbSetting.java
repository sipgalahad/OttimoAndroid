package samanasoft.android.framework;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DbSetting {
	protected Context context;
	
	protected OpenDataHelper openDataHelp;
	protected SQLiteDatabase db;
	public DbSetting(Context context){
		this.context = context;		
	}
	public DbSetting open() throws SQLException {
		openDataHelp = new OpenDataHelper(context);
		db = openDataHelp.getWritableDatabase();
		return this;
	}

	public void close() {
		openDataHelp.close();
	}
	
	public static class OpenDataHelper extends SQLiteOpenHelper{
		private SQLiteDatabase myDataBase;		 
		private final Context myContext;
		 
		/**
		  * Constructor
		  * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
		  * @param context
		  */
		public OpenDataHelper(Context context) {
			super(context, DbConfiguration.DATABASE_NAME, null, DbConfiguration.DATABASE_VERSION);
			this.myContext = context;
		}
		 
		/**
		  * Creates a empty database on the system and rewrites it with your own database.
		  * */
		public void createDataBase() throws IOException{		 
			//boolean dbExist = checkDataBase();
			//if(!dbExist){
				this.getReadableDatabase();
				try {
					copyDataBase();
				} catch (IOException e) {
					throw new Error("Error copying database");
				} finally{
					this.close();
				}
			//}
		}
		 
		/**
		  * Check if the database already exist to avoid re-copying the file each time you open the application.
		  * @return true if it exists, false if it doesn't
		  */
		private boolean checkDataBase(){		 
			SQLiteDatabase checkDB = null;		 
			try{
				String myPath = DbConfiguration.DATABASE_PATH + DbConfiguration.DATABASE_NAME;
				checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READONLY);		 
			}catch(SQLiteException e){
				//e.printStackTrace();
            }
			if(checkDB != null){		 
				checkDB.close();		 
			}		 
			return checkDB != null ? true : false;
		}
		 
		/**
		  * Copies your database from your local assets-folder to the just created empty database in the
		  * system folder, from where it can be accessed and handled.
		  * This is done by transfering bytestream.
		  * */
		private void copyDataBase() throws IOException{		
			InputStream myInput = myContext.getAssets().open(DbConfiguration.DATABASE_NAME);		
			String outFileName = DbConfiguration.DATABASE_PATH + DbConfiguration.DATABASE_NAME;
			 
			OutputStream myOutput = new FileOutputStream(outFileName);
			 
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer))>0){
				myOutput.write(buffer, 0, length);
			}
			 
			//Close the streams
			myOutput.flush();
			myOutput.close();
			myInput.close();		 
		}
		 
		public void openDataBase() throws SQLException{		 
			//Open the database
			String myPath = DbConfiguration.DATABASE_PATH + DbConfiguration.DATABASE_NAME;
			myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READONLY);		 
		}
		 
		@Override
		public synchronized void close() {		 
			if(myDataBase != null)
				myDataBase.close();	 
			super.close();
		}
		 
		@Override
		public void onCreate(SQLiteDatabase db) {
			/*db.execSQL("CREATE TABLE Patient (MRN INTEGER PRIMARY KEY, FirstName VARCHAR(20), MiddleName VARCHAR(20), LastName VARCHAR(20)," +
						 "IsDeleted CHAR(1), CreatedDate VARCHAR(20))");
	
	
			db.execSQL("INSERT INTO Patient (FirstName, MiddleName, LastName, IsDeleted, CreatedDate) VALUES('Sidney','','Bristow', '0', '2012-01-05 12:03:10')");
			db.execSQL("INSERT INTO Patient (FirstName, MiddleName, LastName, IsDeleted, CreatedDate) VALUES('Simon','','James', '0', '2012-08-30 12:03:10')");
			db.execSQL("INSERT INTO Patient (FirstName, MiddleName, LastName, IsDeleted, CreatedDate) VALUES('Gregory','','Fuller', '0', '2012-05-10 12:03:10')");
			*/
		}
		 
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {		 
		}
		 
	}
}
