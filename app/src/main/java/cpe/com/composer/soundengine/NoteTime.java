package cpe.com.composer.soundengine;

import com.leff.midi.MidiTrack;

import java.util.ArrayList;

public class NoteTime {
    ArrayList<Integer> pitch;
    ArrayList<Long> tick;

    public NoteTime(){
        pitch = new ArrayList<>();
        tick = new ArrayList<>();
    }

    public void addNote(int pitch, long tick){
        this.pitch.add(pitch);
        this.tick.add(tick);
    }

    public MidiTrack processTrack(MidiTrack track, int channel, int velocity, int duration){
        for(int i=0;i<pitch.size();i++){
            track.insertNote(channel, pitch.get(i), velocity, tick.get(i), duration);
        }
        return track;
    }
}
