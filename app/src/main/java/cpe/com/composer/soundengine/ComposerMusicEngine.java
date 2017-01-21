package cpe.com.composer.soundengine;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.TimeSignature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import cpe.com.composer.datamanager.PresetDatabase;

public class ComposerMusicEngine {
    private static final int PPQ=120;
    private static final int NOTE_DURATION=120;

    private static final String TAG="DevDebugger-Tew";

    private MidiFile midi;
    private int currentTransposeKey=0;

    private ArrayList<ComposerMidiTrack> trackList = new ArrayList<>();
    private ArrayList<ChordCell> chordList = new ArrayList<>();
    private ArrayList<Integer> playingID = new ArrayList<>();

    private File output;
    private MediaPlayer player;
    private Activity parentActivity;
    private Context context;
    private static Timer HACK_loopTimer;

    public ComposerMusicEngine(Activity parentActivity, String path){
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
        PresetDatabase mHelper = new PresetDatabase(context);
        SQLiteDatabase mDb = mHelper.getWritableDatabase();
        Cursor mCursor = mDb.rawQuery("SELECT * FROM " + PresetDatabase.TABLE_NAME, null);

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
                trackList.add(new ComposerMidiTrack(id, title, channel, program, note));
            else if(mode==1)
                chordList.add(new ChordCell(id, title, Integer.valueOf(note)));

            mCursor.moveToNext();
        }
    }

    public ArrayList<ComposerMidiTrack> getTrackList(){
        return trackList;
    }

    public ArrayList<ChordCell> getChordList(){
        return chordList;
    }

    public void playID(int id) {
        new PlayTrackTask(id).execute();
        /*if(playingID.size()!=0){ //playing music
            if(getTrackById(id).isPlaying()){
                removeTrack(id);
                if(playingID.size()==0) {
                    HACK_loopTimer.cancel();
                }
                else{
                    int currentPosition = player.getCurrentPosition();
                    player.stop();
                    player.release();
                    try { midi.writeToFile(output); } catch (IOException e) {System.err.println(e);}
                    player = MediaPlayer.create(context, Uri.fromFile(output));
                    player.seekTo(currentPosition);
                    player.start();
                }
            }
            else {
                int duplicateId = isDuplicateChannel(getTrackById(id).getChannel());
                Log.d(TAG, "duplicate id: " + duplicateId);
                if(duplicateId!=-1){
                    removeTrack(duplicateId);
                }
                addTrack(id);
                int currentPosition = player.getCurrentPosition();
                player.stop();
                player.release();
                try { midi.writeToFile(output); } catch (IOException e) {System.err.println(e);}
                player = MediaPlayer.create(context, Uri.fromFile(output));
                player.seekTo(currentPosition);
                player.start();
            }
        }
        else{ //First time play
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
        }*/
    }

    public void doTranspose(int id){
        new TransposeKeyTask(id).execute();
        /*int transposeKey = getPitchKeyById(id).getKey();
        if(playingID.size()!=0){

            for(int i=playingID.size()-1;i>=0;i--){
                if(getTrackById(playingID.get(i)).getChannel()!=9) {
                    Log.d(TAG, "remove track " + i);
                    midi.removeTrack(i+1);
                }
            }

            for(int i=0;i<playingID.size();i++){
                ComposerMidiTrack track = getTrackById(playingID.get(i));
                if(track.getChannel()!=9) {
                    Iterator<MidiEvent> it = track.getEvents().iterator();
                    while (it.hasNext()){
                        MidiEvent e = it.next();
                        if(e instanceof NoteOn){
                            ((NoteOn) e).setNoteValue(((NoteOn) e).getNoteValue()+transposeKey);
                        }
                    }
                    midi.addTrack(track);
                }
            }

            int currentPosition = player.getCurrentPosition();
            player.stop();
            player.release();
            try { midi.writeToFile(output); } catch (IOException e) {System.err.println(e);}
            player = MediaPlayer.create(context, Uri.fromFile(output));
            player.setLooping(true);
            player.seekTo(currentPosition);
            player.start();
            loopHacking();
        }*/
    }

    private class TransposeKeyTask extends AsyncTask<Void, Void, Void>{
        private int id;
        TransposeKeyTask(int id){
            this.id = id;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int transposeKey = getPitchKeyById(id).getKey();
            for(int i=playingID.size()-1;i>=0;i--){
                if(getTrackById(playingID.get(i)).getChannel()!=9) {
                    Log.d(TAG, "remove track " + i);
                    midi.removeTrack(i+1);
                }
            }

            for(int i=0;i<playingID.size();i++){
                ComposerMidiTrack track = getTrackById(playingID.get(i));
                if(track.getChannel()!=9) {
                    Iterator<MidiEvent> it = track.getEvents().iterator();
                    while (it.hasNext()){
                        MidiEvent e = it.next();
                        if(e instanceof NoteOn){
                            ((NoteOn) e).setNoteValue(((NoteOn) e).getNoteValue()+transposeKey);
                        }
                    }
                    midi.addTrack(track);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            int currentPosition = player.getCurrentPosition();
            //HACK_loopTimer.cancel();
            //player.stop();
            //player.release();
            try { midi.writeToFile(output); } catch (IOException e) {System.err.println(e);}
            //player = MediaPlayer.create(context, Uri.fromFile(output));
            //player.setLooping(true);
            //player.seekTo(currentPosition);
            //player.start();
            //loopHacking();
        }
    }

    private class PlayTrackTask extends  AsyncTask<Void, Void, Void>{
        private int id;
        private boolean first=true;
        private boolean end=false;

        PlayTrackTask(int id){
            this.id = id;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(playingID.size()!=0){ //add or remove track on playing
                first=false;
                if(getTrackById(id).isPlaying()){
                    removeTrack(id);
                    if(playingID.size()==0) {
                        end=true;
                    }
                }
                else {
                    int duplicateId = isDuplicateChannel(getTrackById(id).getChannel());
                    Log.d(TAG, "duplicate id: " + duplicateId);
                    if(duplicateId!=-1){ //replace track
                        removeTrack(duplicateId);
                    }
                    addTrack(id);
                }
            }
            else{ //First time play
                addTrack(id);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(first){
                try { midi.writeToFile(output); } catch (IOException e) {System.err.println(e);}
                player = MediaPlayer.create(context, Uri.fromFile(output));
                player.setLooping(true);
                player.start();
                loopHacking();
            }
            else{
                int currentPosition = player.getCurrentPosition();
                player.stop();
                player.release();
                try {midi.writeToFile(output);} catch (IOException e) {System.err.println(e);}
                if(!end) {
                    player = MediaPlayer.create(context, Uri.fromFile(output));
                    player.seekTo(currentPosition);
                    player.start();
                }
                else
                    HACK_loopTimer.cancel();
            }
        }
    }

    @Nullable
    private ComposerMidiTrack getTrackById(int id){
        for(ComposerMidiTrack track: trackList){
            if(track.getID()==id)
                return track;
        }
        return null;
    }

    @Nullable
    private ChordCell getPitchKeyById(int id){
        for(ChordCell chordCell: chordList){
            if(chordCell.getId()==id)
                return chordCell;
        }
        return null;
    }

    private int getTrackIndexById(int id){
        for(int i=0;i<trackList.size();i++){
            if(trackList.get(i).getID()==id)
                return i;
        }
        return -1;
    }

    private int isDuplicateChannel(int channel){
        for(int i=0;i<trackList.size();i++){
            if(trackList.get(i).isPlaying())
                if(trackList.get(i).getChannel()==channel)
                    return trackList.get(i).getID();
        }
        return -1;
    }

    private void addTrack(int id){
        midi.addTrack(getTrackById(id));
        trackList.get(getTrackIndexById(id)).setPlaying(true);
        playingID.add(id);
    }

    private void removeTrack(int id){
        midi.removeTrack(playingID.indexOf(id)+1);
        playingID.remove(Integer.valueOf(id));
        trackList.get(getTrackIndexById(id)).setPlaying(false);
    }

    private void loopHacking(){
        long mHackLoopingPreview = 30;
        HACK_loopTimer = new Timer();
        TimerTask HACK_loopTask = new TimerTask() {
            @Override public void run() {
                parentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(playingID.size()!=0)
                            player.seekTo(0);
                    }
                });
            }
        };
        long waitingTime = player.getDuration()-mHackLoopingPreview;
        HACK_loopTimer.schedule(HACK_loopTask, waitingTime, waitingTime);
    }

    public void checkPlayingArray(){
        Log.d(TAG, "CURRENT TRANSPOSE: " + currentTransposeKey);
        for(int x: playingID)
            Log.d(TAG, "Playing id: " + x);
    }
}
