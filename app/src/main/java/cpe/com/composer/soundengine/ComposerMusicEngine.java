package cpe.com.composer.soundengine;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
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

import cpe.com.composer.datamanager.ComposerDatabase;

public class ComposerMusicEngine {
    private static final int PPQ=120;
    private static final int NOTE_DURATION=120;
    private static final String FILE_NAME = "cache01.mid";
    private static final String FILE_NAME_2 = "cache02.mid";
    private static final String TAG="DevDebugger-Tew";

    private MidiFile midi;
    private int currentTransposeKey=0;

    private ArrayList<ComposerMidi> trackList = new ArrayList<>();
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
        output = new File(path+"/"+FILE_NAME);
        try
        {
            midi.writeToFile(output);
        }
        catch(IOException e) {
            Log.e(TAG, e.toString());
        }
        player = MediaPlayer.create(context, Uri.fromFile(output));
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void loadDatabase(){
        ComposerDatabase mHelper = new ComposerDatabase(context);
        SQLiteDatabase mDb = mHelper.getWritableDatabase();
        Cursor mCursor = mDb.rawQuery("SELECT * FROM " + ComposerDatabase.TRACK_TABLE, null);

        mCursor.moveToFirst();
        while ( !mCursor.isAfterLast() ){
            int id = mCursor.getInt(mCursor.getColumnIndex("_id"));
            String title = mCursor.getString(mCursor.getColumnIndex(ComposerDatabase.COL_TITLE));
            int channel = mCursor.getInt(mCursor.getColumnIndex(ComposerDatabase.COL_CHANNEL));
            int program = mCursor.getInt(mCursor.getColumnIndex(ComposerDatabase.COL_PROGRAM));
            String note = mCursor.getString(mCursor.getColumnIndex(ComposerDatabase.COL_NOTE));
            int mode = mCursor.getInt(mCursor.getColumnIndex(ComposerDatabase.COL_MODE));

            if(mode==0) {
                trackList.add(new ComposerMidi(id, title, channel, program, note));
            }
            else if(mode==1) {
                chordList.add(new ChordCell(id, title, Integer.valueOf(note), program));    //program = isMinor
            }
            else if(mode==2){
                chordList.add(new ChordCell(id, title, note));
            }
            else if(mode==3){
                chordList.add(new ChordCell(id, title, note));
            }

            mCursor.moveToNext();
        }
    }

    public void playID(int id) {
        new PlayTrackTask(id).execute();
    }
    public void doTranspose(int id){
        final ChordCell tempChordCell = getPitchKeyById(id);
        if(tempChordCell.getMinorSize()>1) {
            Log.d(TAG, "chord set");
            player.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                int keyIndex=0;
                @Override
                public void onSeekComplete(MediaPlayer mediaPlayer) {
                    new TransposeKeyTask(tempChordCell.getKey(keyIndex), tempChordCell.getMinor(keyIndex)).execute();
                    if(keyIndex<tempChordCell.getMinorSize()-1)
                        keyIndex++;
                    else
                        keyIndex=0;
                    Log.d(TAG, "KEY index = " + keyIndex);
                }
            });
        }
        else
            new TransposeKeyTask(tempChordCell.getKey(), tempChordCell.getMinor()).execute();
    }

    private class TransposeKeyTask extends AsyncTask<Void, Void, Void>{
        private int transposeKey;
        private int minor;

        TransposeKeyTask(int transposeKey, int minor){
            this.minor = minor;
            this.transposeKey = transposeKey;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            currentTransposeKey = transposeKey;
            Log.d(TAG, "Press on transpose key: " + transposeKey);

            //transpose track note by note
            switch (minor){
                case 0:
                    Log.d(TAG, "Major");
                    for(int i=1;i<midi.getTrackCount();i++){
                        Iterator<MidiEvent> it = midi.getTrack(i).getEvents().iterator();
                        MidiEvent e = it.next();
                        if(!(e instanceof NoteOn)){
                            midi.setTrack(i, getTransposeTrackById(minor, playingID.get(i-1)));
                        }
                    }
                    break;
                case 1:
                    Log.d(TAG, "Minor");
                    for(int i=1;i<midi.getTrackCount();i++){
                        Iterator<MidiEvent> it = midi.getTrack(i).getEvents().iterator();
                        MidiEvent e = it.next();
                        if(!(e instanceof NoteOn)){
                            midi.setTrack(i, getTransposeTrackById(minor, playingID.get(i-1)));
                        }
                    }
                    break;
                default: break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            /*int currentPosition = player.getCurrentPosition();
            player.stop();
            player.reset();
            player.release();*/
            try { midi.writeToFile(output); } catch (IOException e) {System.err.println(e);}
            /*player = MediaPlayer.create(context, Uri.fromFile(output));
            player.seekTo(currentPosition);
            player.start();*/

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
                if(isTrackPlaying(id)){ //remove existing track
                    removeTrack(id);
                    if(playingID.size()==0) {
                        end=true;
                    }
                }
                else { //New Track addded just check existing channel
                    int dupIndex = findDuplicateChannelId(getTrackChannelById(id));
                    if(dupIndex!=-1) {
                        removeTrack(dupIndex);
                        Log.d(TAG, "Duplicate: " + dupIndex);
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
                player.reset();
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

    public ArrayList<ComposerMidi> getTrackList(){ return this.trackList; }

    public ArrayList<ChordCell> getChordList(){
        return chordList;
    }

    private boolean isTrackPlaying(int id){
        for(ComposerMidi track: trackList){
            if(track.getId()==id)
                return track.isPlaying();
        }
        return false;
    }

    private int getTrackChannelById(int id){
        for(ComposerMidi track: trackList){
            if(track.getId()==id){
                return track.getChannel();
            }
        }
        return -1;
    }

    @Nullable
    private MidiTrack getTrackById(int id){
        for(ComposerMidi track: trackList){
            if(track.getId()==id)
                return track.getMidiTrack(0, currentTransposeKey);
        }
        return null;
    }

    @Nullable
    private MidiTrack getTransposeTrackById(int minor, int id){
        for(ComposerMidi track: trackList){
            if(track.getId()==id) {
                if((!track.isMinorAvailable())&&minor==1)
                    return track.getMidiTrack(0, currentTransposeKey);
                return track.getMidiTrack(minor, currentTransposeKey);
            }
        }
        return null;
    }

    /**
     *
     * @param id
     * @return Chord pattern use to tranpose key or change to minor chord/notes
     */
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
            if(trackList.get(i).getId()==id)
                return i;
        }
        return -1;
    }

    private int findDuplicateChannelId(int channel){
        for(ComposerMidi track: trackList){
            if(track.isPlaying())
                if(track.getChannel()==channel)
                    return track.getId();
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
