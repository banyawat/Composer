package cpe.com.composer.datamanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.leff.midi.event.ProgramChange;

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
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(null, 'Funk Drum', 9, -1, '{\"note\":[36,36,36,36,0],\"ppq\":[0,4,8,12,15]}\n');");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(null, 'Electric Bass', 1, " + ProgramChange.MidiProgram.ELECTRIC_BASS_PICK.programNumber() + ", '{\"note\":[39,42,44,45,51,45,44,51,39,39,39,39],\"ppq\":[0,1,2,4,5,6,7,10,11,12,13,14]}\n');");
        preDb.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(null, 'Electirc Guitar', 2, " + ProgramChange.MidiProgram.ELECTRIC_GUITAR_CLEAN.programNumber() + ", '{\"note\":[63,66,70,63,66,70,63,66,70,72,63,66,70,72],\"ppq\":[0,0,0,3,3,3,6,6,6,6,9,9,9,9]}');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase preDb, int i, int i1) {
        preDb.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(preDb);
    }
}
