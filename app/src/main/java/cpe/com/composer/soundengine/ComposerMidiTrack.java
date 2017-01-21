package cpe.com.composer.soundengine;

import com.leff.midi.MidiTrack;
import com.leff.midi.event.ProgramChange;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ComposerMidiTrack extends MidiTrack {
    private int ID;
    private String title;
    private int channel;
    private boolean isPlaying;

    private static final int DEFAULT_PPQ = 120;
    private static final int DEFAULT_NOTEDUR = 120;

    ComposerMidiTrack(int ID, String title, int channel, int program, String note){
        this.ID = ID;
        this.title = title;
        this.channel = channel;
        this.isPlaying=false;
        if(program!=-1)
            super.insertEvent(new ProgramChange(0, channel, program));
        try {
            JSONObject jsonObject = new JSONObject(note);
            JSONArray pitchArray = jsonObject.getJSONArray("note");
            JSONArray ppqArray = jsonObject.getJSONArray("ppq");
            if (!jsonObject.isNull("dur")) {
                JSONArray durArray = jsonObject.getJSONArray("dur");
                for (int i = 0; i < pitchArray.length(); i++) {
                    super.insertNote(channel, pitchArray.getInt(i), 100, ppqArray.getInt(i) * DEFAULT_PPQ, durArray.getInt(i));
                }
            }
            else{
                for (int i = 0; i < pitchArray.length(); i++) {
                    super.insertNote(channel, pitchArray.getInt(i), 100, ppqArray.getInt(i) * DEFAULT_PPQ, DEFAULT_NOTEDUR);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertNote(int channel, int pitch, int velocity, long tick, long duration) {
        super.insertNote(channel, pitch, velocity, tick, duration);
    }

    public int getChannel(){
        return this.channel;
    }

    public int getID() {
        return ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }

    public void setPlaying(boolean playing) {
        this.isPlaying = playing;
    }
}
