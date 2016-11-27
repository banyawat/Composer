package cpe.com.composer.datamanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PresetDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "COMPOSER";
    private static final int DB_VERSSION = 1;
    public static final String TABLE_NAME = "Preset";
    public static final String COL_TITLE = "Title";

    public PresetDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSSION);
    }

    @Override
    public void onCreate(SQLiteDatabase preDb) {
        preDb.execSQL("CREATE TABLE " + TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TITLE + " TEXT);");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " (" + COL_TITLE + ") VALUES('DUMMY');");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " (" + COL_TITLE + ") VALUES('DUMMY');");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " (" + COL_TITLE + ") VALUES('DUMMY');");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " (" + COL_TITLE + ") VALUES('DUMMY');");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " (" + COL_TITLE + ") VALUES('DUMMY');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase preDb, int i, int i1) {
        preDb.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(preDb);
    }
}
