package cpe.com.composer.soundengine;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.ProgramChange;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.TimeSignature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cpe.com.composer.datamanager.PresetDatabase;

public class MusicEngine {
    private static final int CHANNEL_BASS = 1;
    private static final int CHANNEL_GUITAR = 2;
    private static final int CHANNEL_DRUM = 9;
    private static final int PPQ=120;
    private static final int NOTE_DURATION=120;

    private static final String TAG="DevDebugger-Tew";

    private SQLiteDatabase mDb;
    private PresetDatabase mHelper;
    private Cursor mCursor;

    private ArrayList<TrackCell> trackCells = new ArrayList<>();
    private ArrayList<Integer> playingID = new ArrayList<>();

    MidiFile midi;
    MidiTrack drumTrack1 = new MidiTrack();
    MidiTrack drumTrack2 = new MidiTrack();
    MidiTrack bassTrack = new MidiTrack();
    MidiTrack guitarTrack = new MidiTrack();

    File output;
    MediaPlayer player;
    Activity parentActivity;
    Context context;
    private static Timer HACK_loopTimer;

    public MusicEngine(Activity parentActivity, String path){
        this.parentActivity = parentActivity;
        context = parentActivity.getApplicationContext();
        midi = new MidiFile(MidiFile.DEFAULT_RESOLUTION);

        //Tempo setting
        TimeSignature ts = new TimeSignature();
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);
        Tempo tempo = new Tempo();
        tempo.setBpm(120);

        MidiTrack tempoTrack = new MidiTrack();
        tempoTrack.insertNote(0, 0, 100, 15*PPQ, NOTE_DURATION);

        tempoTrack.insertEvent(ts);
        tempoTrack.insertEvent(tempo);
        noteLoader();

        midi.addTrack(tempoTrack);
        output = new File(path+"/exampleout.mid");
        try
        {
            midi.writeToFile(output);
        }
        catch(IOException e) {
            Log.e(TAG, e.toString());
        }
        Log.d(TAG, "File created");
        player = MediaPlayer.create(context, Uri.fromFile(output));
    }

    public void loadDatabase(){
        mHelper = new PresetDatabase(context);
        mDb = mHelper.getWritableDatabase();
        mCursor = mDb.rawQuery("SELECT * FROM " + PresetDatabase.TABLE_NAME, null);

        mCursor.moveToFirst();
        while ( !mCursor.isAfterLast() ){
            int id = mCursor.getInt(mCursor.getColumnIndex("_id"));
            String title = mCursor.getString(mCursor.getColumnIndex(PresetDatabase.COL_TITLE));
            int channel = mCursor.getInt(mCursor.getColumnIndex(PresetDatabase.COL_CHANNEL));
            int program = mCursor.getInt(mCursor.getColumnIndex(PresetDatabase.COL_PROGRAM));
            String note = mCursor.getString(mCursor.getColumnIndex(PresetDatabase.COL_NOTE));

            trackCells.add(new TrackCell(id, title, channel, program, note));
            mCursor.moveToNext();
        }
    }

    private void noteLoader(){
        drumTrack2.insertNote(CHANNEL_DRUM, 36, 100, 0, NOTE_DURATION);
        drumTrack2.insertNote(CHANNEL_DRUM, 36, 100, 4*PPQ, NOTE_DURATION);
        drumTrack2.insertNote(CHANNEL_DRUM, 36, 100, 8*PPQ, NOTE_DURATION);
        drumTrack2.insertNote(CHANNEL_DRUM, 36, 100, 12*PPQ, NOTE_DURATION);
        drumTrack2.insertNote(CHANNEL_DRUM, 0, 100, 15*PPQ, NOTE_DURATION);


        drumTrack1.insertNote(CHANNEL_DRUM, 53, 100, 0, NOTE_DURATION);
        drumTrack1.insertNote(CHANNEL_DRUM, 53, 100, 4*PPQ, NOTE_DURATION);
        drumTrack1.insertNote(CHANNEL_DRUM, 53, 100, 8*PPQ, NOTE_DURATION);
        drumTrack1.insertNote(CHANNEL_DRUM, 53, 100, 12*PPQ, NOTE_DURATION);

        drumTrack1.insertNote(CHANNEL_DRUM, 36, 100, 0, NOTE_DURATION);
        drumTrack1.insertNote(CHANNEL_DRUM, 36, 100, 3*PPQ, NOTE_DURATION);
        drumTrack1.insertNote(CHANNEL_DRUM, 38, 100, 4*PPQ, NOTE_DURATION);
        drumTrack1.insertNote(CHANNEL_DRUM, 36, 100, 6*PPQ, NOTE_DURATION);
        drumTrack1.insertNote(CHANNEL_DRUM, 36, 100, 10*PPQ, NOTE_DURATION);
        drumTrack1.insertNote(CHANNEL_DRUM, 36, 100, 11*PPQ, NOTE_DURATION);
        drumTrack1.insertNote(CHANNEL_DRUM, 38, 100, 12*PPQ, NOTE_DURATION);
        drumTrack1.insertNote(CHANNEL_DRUM, 38, 100, 14*PPQ, NOTE_DURATION);
        drumTrack1.insertNote(CHANNEL_DRUM, 38, 100, 15*PPQ, NOTE_DURATION);

        //Set Program
        bassTrack.insertEvent(new ProgramChange(0, CHANNEL_BASS, ProgramChange.MidiProgram.ELECTRIC_BASS_PICK.programNumber()));
        guitarTrack.insertEvent(new ProgramChange(0, CHANNEL_GUITAR, ProgramChange.MidiProgram.ELECTRIC_GUITAR_CLEAN.programNumber()));

        // Track 1 will have some notes in it

        bassTrack.insertNote(CHANNEL_BASS, 39, 100, 0, NOTE_DURATION);
        bassTrack.insertNote(CHANNEL_BASS, 42, 100, 1*PPQ, NOTE_DURATION);
        bassTrack.insertNote(CHANNEL_BASS, 44, 100, 2*PPQ, NOTE_DURATION);
        bassTrack.insertNote(CHANNEL_BASS, 45, 100, 4*PPQ, NOTE_DURATION);
        bassTrack.insertNote(CHANNEL_BASS, 51, 100, 5*PPQ, NOTE_DURATION);
        bassTrack.insertNote(CHANNEL_BASS, 45, 100, 6*PPQ, NOTE_DURATION);
        bassTrack.insertNote(CHANNEL_BASS, 44, 100, 7*PPQ, NOTE_DURATION);
        bassTrack.insertNote(CHANNEL_BASS, 51, 100, 10*PPQ, NOTE_DURATION);
        bassTrack.insertNote(CHANNEL_BASS, 39, 100, 11*PPQ, NOTE_DURATION);
        bassTrack.insertNote(CHANNEL_BASS, 39, 100, 12*PPQ, NOTE_DURATION);
        bassTrack.insertNote(CHANNEL_BASS, 39, 100, 13*PPQ, NOTE_DURATION);
        bassTrack.insertNote(CHANNEL_BASS, 39, 100, 14*PPQ, NOTE_DURATION);

        guitarTrack.insertNote(CHANNEL_GUITAR, 63, 100, 0, NOTE_DURATION+200);
        guitarTrack.insertNote(CHANNEL_GUITAR, 66, 100, 0, NOTE_DURATION+200);
        guitarTrack.insertNote(CHANNEL_GUITAR, 70, 100, 0, NOTE_DURATION+200);

        guitarTrack.insertNote(CHANNEL_GUITAR, 63, 100, 3*PPQ, 50);
        guitarTrack.insertNote(CHANNEL_GUITAR, 66, 100, 3*PPQ, 50);
        guitarTrack.insertNote(CHANNEL_GUITAR, 70, 100, 3*PPQ, 50);

        guitarTrack.insertNote(CHANNEL_GUITAR, 63, 100, 6*PPQ, NOTE_DURATION+200);
        guitarTrack.insertNote(CHANNEL_GUITAR, 66, 100, 6*PPQ, NOTE_DURATION+200);
        guitarTrack.insertNote(CHANNEL_GUITAR, 70, 100, 6*PPQ, NOTE_DURATION+200);
        guitarTrack.insertNote(CHANNEL_GUITAR, 72, 100, 6*PPQ, NOTE_DURATION+200);

        guitarTrack.insertNote(CHANNEL_GUITAR, 63, 100, 9*PPQ, 50);
        guitarTrack.insertNote(CHANNEL_GUITAR, 66, 100, 9*PPQ, 50);
        guitarTrack.insertNote(CHANNEL_GUITAR, 70, 100, 9*PPQ, 50);
        guitarTrack.insertNote(CHANNEL_GUITAR, 72, 100, 9*PPQ, 50);
    }

    public void playID(int id) {
        if (playingID.size() != 0) {
            Log.d(TAG, "Someone has been played");
            if (playingID.contains(id)) {
                Log.d(TAG, "It have your ID already, remove");
                int currentPosition = player.getCurrentPosition();
                player.stop();
                player.release();

                midi.removeTrack(playingID.indexOf(id)+1);
                Log.d(TAG, "ID " + playingID.indexOf(id) + " has been removed");
                Log.d(TAG, "Removing track " +playingID.indexOf(id) + " ...");
                playingID.remove(new Integer(id));

                try {
                    midi.writeToFile(output);
                } catch (IOException e) {
                    System.err.println(e);
                }
                if(playingID.size()!=0) {
                    player = MediaPlayer.create(context, Uri.fromFile(output));
                    player.seekTo(currentPosition);
                    player.start();
                }
                else{
                    HACK_loopTimer.cancel();
                }
            } else {
                Log.d(TAG, "New ID coming");
                if (player.isPlaying()) {
                    int currentPosition = player.getCurrentPosition();
                    player.stop();
                    player.release();

                    midi.addTrack(guitarTrack);
                    try {
                        midi.writeToFile(output);
                    } catch (IOException e) {
                        System.err.println(e);
                    }

                    player = MediaPlayer.create(context, Uri.fromFile(output));
                    player.seekTo(currentPosition);
                    player.start();
                } else {
                    Log.d(TAG, "Play on stop");
                    midi.addTrack(guitarTrack);
                    try {
                        midi.writeToFile(output);
                    } catch (IOException e) {
                        System.err.println(e);
                    }
                    player = MediaPlayer.create(context, Uri.fromFile(output));
                    player.start();
                }
                playingID.add(id);
            }
        }
        else{
            Log.d(TAG, "Nobody played, First play");
            midi.addTrack(drumTrack1);
            try {
                midi.writeToFile(output);
            } catch (IOException e) {
                System.err.println(e);
            }
            player = MediaPlayer.create(context, Uri.fromFile(output));
            player.setLooping(true);
            player.start();
            playingID.add(id);
            loopHacking();
        }
    }

    private void loopHacking(){
        long mHackLoopingPreview = 30;
        HACK_loopTimer = new Timer();
        TimerTask HACK_loopTask = new TimerTask() {
            @Override public void run() {
                parentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        player.seekTo(0);
                    }
                });
            }
        };
        long waitingTime = player.getDuration()-mHackLoopingPreview;
        HACK_loopTimer.schedule(HACK_loopTask, waitingTime, waitingTime);
    }

    public void checkPlayingArray(){
        for(int sId: playingID){
            Log.d(TAG, "playingID: " + sId);
        }
    }
}
