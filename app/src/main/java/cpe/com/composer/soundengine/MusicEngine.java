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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cpe.com.composer.datamanager.PresetDatabase;

public class MusicEngine {
    private static final int PPQ=120;
    private static final int NOTE_DURATION=120;

    private static final String TAG="DevDebugger-Tew";

    private SQLiteDatabase mDb;
    private PresetDatabase mHelper;
    private Cursor mCursor;

    private ArrayList<TrackCell> trackCells = new ArrayList<>();
    private ArrayList<Integer> playingID = new ArrayList<>();
    private ArrayList<Integer> playingChannel = new ArrayList<>();

    MidiFile midi;

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
        Log.d(TAG, "Loading database");
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

    public int getIdFromUI(int position){
        return trackCells.get(position).getID();
    }

    public ArrayList<TrackCell> getTrackCells(){
        return trackCells;
    }

    public void playID(int id) {
        final int channel = trackCells.get(id-1).getChannel();
        Log.d(TAG, "GET ID: " + id);
        if (playingID.size() != 0) { //On playing
            Log.d(TAG, "Someone has been played");
            if (playingID.contains(id)) {
                Log.d(TAG, "User click on exist ID, remove it");
                int currentPosition = player.getCurrentPosition();
                player.stop();
                player.release();

                midi.removeTrack(playingID.indexOf(id)+1);
                playingID.remove(new Integer(id));
                playingChannel.remove(new Integer(channel));

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
                int currentPosition = player.getCurrentPosition();
                player.stop();
                player.release();

                if(playingChannel.contains(channel)){ //Check if channel was running already
                    Log.d(TAG, "Duplicate channel");
                    int indexToRemove = playingChannel.indexOf(channel);
                    midi.removeTrack(indexToRemove+1);
                    midi.addTrack(getTrackFromId(id));
                    playingID.remove(indexToRemove);
                    playingChannel.remove(indexToRemove);
                } else {
                    Log.d(TAG, "New Channel");
                    midi.addTrack(getTrackFromId(id));
                }

                try {
                    midi.writeToFile(output);
                } catch (IOException e) {
                    System.err.println(e);
                }

                player = MediaPlayer.create(context, Uri.fromFile(output));
                player.seekTo(currentPosition);
                player.start();

                playingID.add(id);
                playingChannel.add(trackCells.get(id-1).getChannel());
            }
        }
        else{ //First play
            Log.d(TAG, "Nobody played, First play");
            midi.addTrack(getTrackFromId(id));
            try {
                midi.writeToFile(output);
            } catch (IOException e) {
                System.err.println(e);
            }
            player = MediaPlayer.create(context, Uri.fromFile(output));
            player.setLooping(true);
            player.start();
            playingID.add(id);
            playingChannel.add(trackCells.get(id-1).getChannel());
            loopHacking();
        }
        if(playingID.size()!=0&&(!player.isPlaying())) {
            player.release();
            HACK_loopTimer.cancel();
            player = MediaPlayer.create(context, Uri.fromFile(output));
            player.start();
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

    private MidiTrack getTrackFromId(int id){
        MidiTrack track = new MidiTrack();
        int program = trackCells.get(id-1).getProgram();
        if(program!=-1){
            track.insertEvent(new ProgramChange(0, trackCells.get(id-1).getChannel(), trackCells.get(id-1).getProgram()));
        }
        try {
            JSONObject jsonObject = new JSONObject(trackCells.get(id-1).getNote());
            JSONArray pitchArray = jsonObject.getJSONArray("note");
            JSONArray ppqArray = jsonObject.getJSONArray("ppq");
            for(int i=0;i<pitchArray.length();i++){
                track.insertNote(trackCells.get(id-1).getChannel(), pitchArray.getInt(i), 100, ppqArray.getInt(i)*PPQ, NOTE_DURATION);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return track;
    }

    public void checkPlayingArray(){
        for(int i=0;i<playingID.size();i++){
            Log.d(TAG, "playingID: " + playingID.get(i) + ", playingChannel: " + playingChannel.get(i));
        }
    }
}
