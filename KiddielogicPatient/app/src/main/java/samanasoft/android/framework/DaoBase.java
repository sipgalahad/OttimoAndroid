package samanasoft.android.framework;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

public class DaoBase extends DbSetting{
	public DaoBase(Context context) {
		super(context);
	}
	
	public int executeNonQuery(String query){
		this.open();
		db.execSQL(query);
		SQLiteStatement stmt = db.compileStatement("SELECT CHANGES()");
		int affectedRows = (int)stmt.simpleQueryForLong();
		this.close();
		return affectedRows;
	}
	
	public Cursor getDataRow(String query){
		this.open();
		Cursor c = db.rawQuery(query, null);
		c.getCount();
		this.close();
		return c;
	}
	
	public Cursor getDataReader(String query){
		this.open();
		Cursor c = db.rawQuery(query, null);		
		c.getCount();
		this.close();
		return c;
	}
}
