package cpe.com.composer.datamanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.leff.midi.event.ProgramChange;

public class ComposerDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "COMPOSER";
    private static final int DB_VERSSION = 1;
    public static final String TRACK_TABLE = "Track";
    public static final String COL_TITLE = "Title";
    public static final String COL_CHANNEL = "Channel";
    public static final String COL_PROGRAM = "Program";
    public static final String COL_NOTE = "Note";
    public static final String COL_MODE = "Mode";
    public static final String PRESET_TABLE = "Preset";
    public static final String COL_DETAIL = "Detail";

    public ComposerDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSSION);
    }

    @Override
    public void onCreate(SQLiteDatabase preDb) {
        preDb.execSQL("CREATE TABLE " + TRACK_TABLE + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, '" + COL_TITLE +
                "' TEXT, '" + COL_CHANNEL + "' INT, '" + COL_PROGRAM + "' INT, '" + COL_NOTE + "' TEXT, '" + COL_MODE + "' INT);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, '70''s Drum', 9, -1, '{\"note\":[53,53,53,53,36,36,38,36,36,36,38,38,38],\"ppq\":[0,4,8,12,0,3,4,6,10,11,12,14,15]}', 0);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, '70''s Drum 2', 9, -1, '{\"note\":[36,42,40,42,36,42,36,42,40,40,40],\"ppq\":[0,0,4,4,6,8,10,12,12,14,15]}', 0);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, '80''s Disco Drum', 9, -1, '{\"note\":[36,53,38,53,36,53,38,53,38],\"ppq\":[0,2,4,6,8,10,12,14,15]}', 0);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'Rhythm Beat', 9, -1, '{\"note\":[36,42,42,36],\"ppq\":[0,4,12,14]}', 0);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'Kick Beat', 9, -1, '{\"note\":[36,36,36,36,0],\"ppq\":[0,4,8,12,15]}', 0);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'Basic Bass', 1, " + ProgramChange.MidiProgram.ELECTRIC_BASS_PICK.programNumber() + ", " +
                "'{\"note\":[36,36,36],\"ppq\":[0,6,8]}', 0);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'ฺBasic Bass 2', 1, " + ProgramChange.MidiProgram.ELECTRIC_BASS_PICK.programNumber() + ", " +
                "'{\"note\":[36,36,36,36,36,36,36,36],\"ppq\":[0,2,4,6,8,10,12,14],\"dur\":[220,220,220,220,220,220,220,220]}', 0);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'FLY Bass', 1, " + ProgramChange.MidiProgram.ELECTRIC_BASS_PICK.programNumber() + ", " +
                "'{\"note\":[36,36,36],\"ppq\":[0,6,8]}', 0);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'Heaven Tone Guitar', 2, " + ProgramChange.MidiProgram.ELECTRIC_GUITAR_CLEAN.programNumber() +
                ", '{\"note\":[48,55,48,50,55,48,53,48,52,50,52], \"notemin\":[48,55,48,51,55,48,50,48,55,50,51]," +
                "\"ppq\":[0,0,2,4,4,6,8,10,10,12,14], \"dur\":[220,220,220,220,220,220,220,220,220,220,220]}', 0);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'Basic Guitar', 2, " + ProgramChange.MidiProgram.ELECTRIC_GUITAR_CLEAN.programNumber() + ", '{\"note\":[48,52,55,48,52,55],\"notemin\":[48,51,55,48,51,55],\"ppq\":[0,2,4,8,10,12],\"dur\":[320,320,320,320,320,320]}', 0);");

        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'C', 0, 0, 0, 1);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'C#', 0, 0, 1, 1);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'D', 0, 0, 2, 1);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'Eb', 0, 0, 3, 1);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'E', 0, 0, 4, 1);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'F', 0, 0, 5, 1);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'F#', 0, 0, 6, 1);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'G', 0, 0, 7, 1);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'G#', 0, 0, 8, 1);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'A', 0, 0, 9, 1);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'Bb', 0, 0, 10, 1);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'B', 0, 0, 11, 1);");

        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'Cm', 0, 1, 0, 1);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'C#m', 0, 1, 1, 1);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'Dm', 0, 1, 2, 1);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'Ebm', 0, 1, 3, 1);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'Em', 0, 1, 4, 1);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'Fm', 0, 1, 5, 1);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'F#m', 0, 1, 6, 1);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'Gm', 0, 1, 7, 1);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'G#m', 0, 1, 8, 1);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'Am', 0, 1, 9, 1);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'Bbm', 0, 1, 10, 1);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'Bm', 0, 1, 11, 1);");

        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, 'C Am F G', 0, 0, '{\"note\":[0,9,5,7],\"minor\":[0,1,0,0]}', 2);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, '80', 0, 0, 80, 3);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, '90', 0, 0, 90, 3);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, '100', 0, 0, 100, 3);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, '120', 0, 0, 120, 3);");
        preDb.execSQL("INSERT INTO " + TRACK_TABLE + " VALUES(null, '140', 0, 0, 140, 3);");

        preDb.execSQL("CREATE TABLE " + PRESET_TABLE + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, '" + COL_TITLE +
                "' TEXT, '" + COL_DETAIL + "' TEXT);");
        preDb.execSQL("INSERT INTO " + PRESET_TABLE + " VALUES(null, '80s Rock n Roll', '{\"0\":{\"2\":[-1,-1,-1,-1,-1],\"1\":[-1,-1,-1,-1,-1],\"0\":[-1,-1,-1,-1,-1]}}');");
        preDb.execSQL("INSERT INTO " + PRESET_TABLE + " VALUES(null, 'Nickelback Style', '{\"0\":{\"0\":[-1,1,-1,-1,4],\"1\":[-1,-1,13,-1,-1],\"2\":[17,-1,16,-1,-1]},\"1\":{\"0\":[5,-1,-1,4,-1],\"1\":[-1,-1,-1,-1,-1],\"2\":[18,-1,-1,-1,-1]}}');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase preDb, int i, int i1) {
        preDb.execSQL("DROP TABLE IF EXISTS " + TRACK_TABLE);
        onCreate(preDb);
    }
}
