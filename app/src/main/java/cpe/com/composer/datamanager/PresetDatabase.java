package cpe.com.composer.datamanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PresetDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "COMPOSER";
    private static final int DB_VERSSION = 1;
    public static final String TABLE_NAME = "Preset";
    public static final String

            COL_TITLE = "Title";
    public static final String COL_CHANNEL = "Channel";
    public static final String COL_PROGRAM = "Program";
    public static final String COL_NOTE = "Note";

    public PresetDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSSION);
    }

    @Override
    public void onCreate(SQLiteDatabase preDb) {
        preDb.execSQL("CREATE TABLE " + TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, '" + COL_TITLE +
                "' TEXT, '" + COL_CHANNEL + "' INT, '" + COL_PROGRAM + "' INT, '" + COL_NOTE + "' TEXT);");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(null, '8Soul Drum', 9, -1, '{\"note\":[53,53,53,53,36,36,38,36,36,36,38,38,38],\"ppq\":[0,4,8,12,0,3,4,6,10,11,12,14,15]}\n');");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(null, 'Funk Drum', 9, -1, '{\"note\":[53,53,53,53,36,36,38,36,36,36,38,38,38],\"ppq\":[0,4,8,12,0,3,4,6,10,11,12,14,15]}\n');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase preDb, int i, int i1) {
        preDb.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(preDb);
    }
}
