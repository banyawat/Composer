package cpe.com.composer.soundengine;

import android.util.Log;

import com.leff.midi.MidiTrack;
import com.leff.midi.event.ProgramChange;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *  Object store information for 1 track. either major note and minor
 *  Instrument information would be stored in this opponent
 */
public class ComposerLeftHand {
    private static final int DEFAULT_PPQ = 120;
    private static final int DEFAULT_NOTEDUR = 120;

    private boolean isPlaying=false;

    private int id;
    private String title;
    private int channel;
    private int program;
    private JSONArray majorPitches = new JSONArray();
    private JSONArray minorPitches = new JSONArray();
    private JSONArray ticks = new JSONArray();
    private JSONArray duration = new JSONArray();


    ComposerLeftHand(int id, String title, int channel, int program, String jsonNote){
        this.id = id;
        this.title = title;
        this.channel = channel;
        this.program = program;
        try {
            JSONObject jsonObject = new JSONObject(jsonNote);
            majorPitches = jsonObject.getJSONArray("note");
            ticks = jsonObject.getJSONArray("ppq");
            if(program!=-1&&!jsonObject.isNull("notemin")){
                minorPitches = jsonObject.getJSONArray("notemin");
            }

            if(!jsonObject.isNull("dur")){
                duration = jsonObject.getJSONArray("dur");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    MidiTrack getMidiTrack(int minor, int transpose){
        MidiTrack track = new MidiTrack();

        if(program!=-1){
            track.insertEvent(new ProgramChange(0, channel, program));
        }
        else{
            transpose=0;
        }

        if(minor==0){
            if(duration.length()!=0)
                for(int i=0;i<majorPitches.length();i++){
                    try {
                        Log.d("HIDI", "Tick: " + ticks.getDouble(i));
                        track.insertNote(channel, majorPitches.getInt(i)+transpose, 100, (long) (ticks.getDouble(i)* DEFAULT_PPQ), duration.getInt(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            else
                for(int i=0;i<majorPitches.length();i++){
                    try {
                        track.insertNote(channel, majorPitches.getInt(i)+transpose, 100, (long) (ticks.getDouble(i)* DEFAULT_PPQ), DEFAULT_NOTEDUR);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        }
        else if(minor==1){
            if(duration.length()!=0)
                for(int i=0;i<minorPitches.length();i++){
                    try {
                        track.insertNote(channel, minorPitches.getInt(i)+transpose, 100, (long) (ticks.getDouble(i)* DEFAULT_PPQ), duration.getInt(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            else
                for(int i=0;i<minorPitches.length();i++){
                    try {
                        track.insertNote(channel, minorPitches.getInt(i)+transpose, 100, (long) (ticks.getDouble(i)* DEFAULT_PPQ), DEFAULT_NOTEDUR);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        }
        return track;
    }

    boolean isMinorAvailable(){
        return minorPitches.length() != 0;
    }

    public String getTitle(){ return this.title; }

    public int getId(){ return this.id; }

    public int getChannel(){
        return this.channel;
    }

    public int getProgram(){
        return this.program;
    }

    boolean isPlaying() {
        return this.isPlaying;
    }

    void setPlaying(boolean playing) {
        this.isPlaying = playing;
    }


}
