package samanasoft.android.framework;

import java.io.IOException;
import samanasoft.android.framework.DbSetting.OpenDataHelper;
import android.content.Context;

public class DbConfiguration {
	public static String DATABASE_NAME = "OttimoParamedic.db";
	public static String DATABASE_PATH = "";
	public static int DATABASE_VERSION = 1;
	public static void initDB(Context context, boolean isCreateDb){
		//DATABASE_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        if(android.os.Build.VERSION.SDK_INT >= 4.2)
            DATABASE_PATH = context.getApplicationInfo().dataDir + "/databases/";
        else
            DATABASE_PATH = "/data/data/" + context.getPackageName() + "/databases/";
		
		if(isCreateDb){
			OpenDataHelper myDbHelper = new OpenDataHelper(context);		 
			try {		 
				myDbHelper.createDataBase();		 
			} catch (IOException ioe) {		 
				throw new Error("Unable to create database");
			}
		}
	}
}
