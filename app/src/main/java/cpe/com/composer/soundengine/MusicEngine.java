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
    private ArrayList<TrackCell> Chords = new ArrayList<>();
    private ArrayList<Integer> playingID = new ArrayList<>();
    private ArrayList<Integer> playingChannel = new ArrayList<>();

    private MidiFile midi;
    private int currentMode=0;

    private File output;
    private MediaPlayer player;
    private Activity parentActivity;
    private Context context;
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
            int mode = mCursor.getInt(mCursor.getColumnIndex(PresetDatabase.COL_MODE));


            if(mode==0)
                trackCells.add(new TrackCell(id, title, channel, program, note));
            else if(mode==1){
                Chords.add(new TrackCell(id, title, channel, program, note));
            }
            mCursor.moveToNext();
        }
    }

    public ArrayList<TrackCell> getTrackCells(int mode){
        if(mode==0)
            return trackCells;
        else if(mode==1)
            return Chords;
        else
            return null;
    }

    public void playID(int id) {
        if(currentMode==1){
            TrackCell track = findChordById(id);
            changeKey(Integer.valueOf(track.getNote()));
        }
        else {
            int channel = findChannel(id);
            if (playingID.size() != 0) {
                int currentPosition = player.getCurrentPosition();
                //Someone playing music
                if (playingID.contains(id)) { //user wants to stop this track
                    player.stop();
                    player.release();

                    removeTrack(playingID.indexOf(id));

                    try {
                        midi.writeToFile(output);
                    } catch (IOException e) {
                        System.err.println(e);
                    }
                    if (playingID.size() != 0) {
                        player = MediaPlayer.create(context, Uri.fromFile(output));
                        player.seekTo(currentPosition);
                        player.start();
                    } else {
                        HACK_loopTimer.cancel();
                    }
                } else {
                    //New ID coming
                    player.stop();
                    player.release();
                    if (playingChannel.contains(channel)) {
                        removeTrack(playingChannel.indexOf(channel));
                    }
                    addTrack(id);
                    try {
                        midi.writeToFile(output);
                    } catch (IOException e) {
                        System.err.println(e);
                    }
                    playingID.add(id);
                    playingChannel.add(channel);
                    player = MediaPlayer.create(context, Uri.fromFile(output));
                    player.seekTo(currentPosition);
                    player.start();
                }
            } else {
                //Nobody playing music
                playingID.add(id);
                playingChannel.add(channel);
                addTrack(id);
                try {
                    midi.writeToFile(output);
                } catch (IOException e) {
                    System.err.println(e);
                }
                player = MediaPlayer.create(context, Uri.fromFile(output));
                player.setLooping(true);
                player.start();
                loopHacking();
            }
        }
    }

    private void addTrack(int id){
        MidiTrack track = new MidiTrack();
        TrackCell trackCell = findTrackById(id);
        if (trackCell.getProgram() != -1) {
            track.insertEvent(new ProgramChange(0, trackCell.getChannel(), trackCell.getProgram()));
        }
        try {
            JSONObject jsonObject = new JSONObject(trackCell.getNote());
            JSONArray pitchArray = jsonObject.getJSONArray("note");
            JSONArray ppqArray = jsonObject.getJSONArray("ppq");
            if (!jsonObject.isNull("dur")) {
                JSONArray durArray = jsonObject.getJSONArray("dur");
                for (int i = 0; i < pitchArray.length(); i++) {
                    track.insertNote(trackCell.getChannel(), pitchArray.getInt(i), 100, ppqArray.getInt(i) * PPQ, durArray.getInt(i));
                }
            } else {
                for (int i = 0; i < pitchArray.length(); i++) {
                    track.insertNote(trackCell.getChannel(), pitchArray.getInt(i), 100, ppqArray.getInt(i) * PPQ, NOTE_DURATION);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        midi.addTrack(track);
    }

    public void changeKey(int key){
        int currentPosition = player.getCurrentPosition();
        player.stop();
        player.release();

        //ArrayList<Integer> temp = new ArrayList<>();
        for(int i=playingChannel.size()-1;i>=0;i--){
            if(playingChannel.get(i)!=9){
                Log.d(TAG, "PlayingID: " + playingID.get(i) + ", PlayingChannel: " + playingChannel.get(i));
                midi.removeTrack(i+1);

            }
        }

        for(int i=0;i<playingChannel.size();i++){
            if(playingChannel.get(i)!=9){
                MidiTrack track = new MidiTrack();
                TrackCell trackCell = findTrackById(playingID.get(i));
                if (trackCell.getProgram() != -1) {
                    track.insertEvent(new ProgramChange(0, trackCell.getChannel(), trackCell.getProgram()));
                }
                try {
                    JSONObject jsonObject = new JSONObject(trackCell.getNote());
                    JSONArray pitchArray = jsonObject.getJSONArray("note");
                    JSONArray ppqArray = jsonObject.getJSONArray("ppq");
                    if (!jsonObject.isNull("dur")) {
                        JSONArray durArray = jsonObject.getJSONArray("dur");
                        for (int j = 0; j < pitchArray.length(); j++) {
                            track.insertNote(trackCell.getChannel(), pitchArray.getInt(j)+key, 100, ppqArray.getInt(j) * PPQ, durArray.getInt(j));
                        }
                    } else {
                        for (int j = 0; j < pitchArray.length(); j++) {
                            track.insertNote(trackCell.getChannel(), pitchArray.getInt(j)+key, 100, ppqArray.getInt(j) * PPQ, NOTE_DURATION);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                midi.addTrack(track);
            }
        }

        try {
            midi.writeToFile(output);
        } catch (IOException e) {
            System.err.println(e);
        }
        player = MediaPlayer.create(context, Uri.fromFile(output));
        player.start();
        player.seekTo(currentPosition);
    }

    private void removeTrack(int position){
        midi.removeTrack(position+1);
        playingID.remove(position);
        playingChannel.remove(position);
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

    private int findChannel(int id){
        for(TrackCell track: trackCells)
            if(track.getID()==id)
                return track.getChannel();
        return -1;
    }

    private TrackCell findTrackById(int id){
        for(TrackCell track : trackCells){
            if(track.getID()==id){
                return track;
            }
        }
        return null;
    }

    private TrackCell findChordById(int id){
        for(TrackCell track : Chords){
            if(track.getID()==id){
                return track;
            }
        }
        return null;
    }

    public void checkPlayingArray(){
        for(int i=0;i<playingID.size();i++){
            Log.d(TAG, "playingID: " + playingID.get(i) + ", playingChannel: " + playingChannel.get(i));
        }
    }

    public void setMode(int mode){
        this.currentMode = mode;
    }
}
