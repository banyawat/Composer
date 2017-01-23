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
    private boolean isDurationAvailable =false;
    private boolean isMinorAvailable =false;

    private JSONArray pitchMajorArray;
    private JSONArray pitchMinorArray;
    private JSONArray ppqArray;
    private JSONArray durArray;

    private ProgramChange programChange;

    private static final int DEFAULT_PPQ = 120;
    private static final int DEFAULT_NOTEDUR = 120;

    ComposerMidiTrack(int ID, String title, int channel, int program, String note){
        this.ID = ID;
        this.title = title;
        this.channel = channel;
        this.isPlaying=false;
        if(program!=-1) {
            programChange = new ProgramChange(0, channel, program);
            insertEvent(programChange);
        }
        try {
            JSONObject jsonObject = new JSONObject(note);
            pitchMajorArray = jsonObject.getJSONArray("note");
            ppqArray = jsonObject.getJSONArray("ppq");

            isDurationAvailable = !jsonObject.isNull("dur");
            isMinorAvailable = !jsonObject.isNull("notemin");


            if(isMinorAvailable &&program!=-1){
                pitchMinorArray = jsonObject.getJSONArray(("notemin"));
            }
            if (isDurationAvailable) {
                durArray = jsonObject.getJSONArray("dur");
                for (int i = 0; i < pitchMajorArray.length(); i++) {
                    insertNote(channel, pitchMajorArray.getInt(i), 100, ppqArray.getInt(i) * DEFAULT_PPQ, durArray.getInt(i));
                }
            }
            else{
                for (int i = 0; i < pitchMajorArray.length(); i++) {
                    insertNote(channel, pitchMajorArray.getInt(i), 100, ppqArray.getInt(i) * DEFAULT_PPQ, DEFAULT_NOTEDUR);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isMinorAvailable(){
        return isMinorAvailable;
    }

    public MidiTrack getNewMinorTrack() throws JSONException {
        MidiTrack track = new MidiTrack();
        track.insertEvent(programChange);
        if (isDurationAvailable) {
            for (int i = 0; i < pitchMinorArray.length(); i++) {
                track.insertNote(channel, pitchMinorArray.getInt(i), 100, ppqArray.getInt(i) * DEFAULT_PPQ, durArray.getInt(i));
            }
        }
        else{
            for (int i = 0; i < pitchMinorArray.length(); i++) {
                track.insertNote(channel, pitchMinorArray.getInt(i), 100, ppqArray.getInt(i) * DEFAULT_PPQ, DEFAULT_NOTEDUR);
            }
        }
        return track;

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
