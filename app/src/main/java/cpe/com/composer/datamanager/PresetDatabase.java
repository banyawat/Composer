package cpe.com.composer.datamanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.leff.midi.event.ProgramChange;

public class PresetDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "COMPOSER";
    private static final int DB_VERSSION = 1;
    public static final String TABLE_NAME = "Preset";
    public static final String COL_TITLE = "Title";
    public static final String COL_CHANNEL = "Channel";
    public static final String COL_PROGRAM = "Program";
    public static final String COL_NOTE = "Note";
    public static final String COL_MODE = "Mode";

    public PresetDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSSION);
    }

    @Override
    public void onCreate(SQLiteDatabase preDb) {
        preDb.execSQL("CREATE TABLE " + TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, '" + COL_TITLE +
                "' TEXT, '" + COL_CHANNEL + "' INT, '" + COL_PROGRAM + "' INT, '" + COL_NOTE + "' TEXT, '" + COL_MODE + "' INT);");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(null, 'Groove Drum', 9, -1, '{\"note\":[53,53,53,53,36,36,38,36,36,36,38,38,38],\"ppq\":[0,4,8,12,0,3,4,6,10,11,12,14,15]}', 0);");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(null, 'Kick', 9, -1, '{\"note\":[36,36,36,36,0],\"ppq\":[0,4,8,12,15]}', 0);");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(null, 'Electric Bass', 1, " + ProgramChange.MidiProgram.ELECTRIC_BASS_PICK.programNumber() + ", " +
                "'{\"note\":[33,35,37,38,44,38,37,44,33,33,33,33],\"ppq\":[0,1,2,4,5,6,7,10,11,12,13,14]}', 0);");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(null, 'ฺBass 2', 1, " + ProgramChange.MidiProgram.ELECTRIC_BASS_PICK.programNumber() + ", " +
                "'{\"note\":[36,36,36,36,36,36,36,36],\"ppq\":[0,2,4,6,8,10,12,14],\"dur\":[220,220,220,220,220,220,220,220]}', 0);");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(null, 'Electirc Guitar', 2, " + ProgramChange.MidiProgram.ELECTRIC_GUITAR_CLEAN.programNumber() + ", '{\"note\":[57,60,64,57,60,64,57,60,64,66,57,60,64,66], " +
                "\"ppq\":[0,0,0,3,3,3,6,6,6,6,9,9,9,9],\"dur\":[220,220,220,50,50,50,220,220,220,220,50,50,50,50]}', 0);");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(null, 'Guitar Major', 2, " + ProgramChange.MidiProgram.ELECTRIC_GUITAR_CLEAN.programNumber() + ", '{\"note\":[48,52,55,48,52,55],\"notemin\":[48,51,55,48,51,55],\"ppq\":[0,2,4,8,10,12],\"dur\":[320,320,320,320,320,320]}', 0);");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(null, 'C', 0, 0, 0, 1);");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(null, 'D', 0, 0, 2, 1);");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(null, 'E', 0, 0, 4, 1);");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(null, 'F', 0, 0, 5, 1);");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(null, 'G', 0, 0, 7, 1);");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(null, 'A', 0, 0, 9, 1);");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(null, 'Am', 0, 1, 9, 1);");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(null, 'B', 0, 0, 11, 1);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase preDb, int i, int i1) {
        preDb.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(preDb);
    }
}
