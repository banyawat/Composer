package cpe.com.composer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.ProgramChange;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.TimeSignature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MidiActivity extends AppCompatActivity {
    private static final int CHANNEL_BASS = 0;
    private static final int CHANNEL_GUITAR = 1;
    private static final int CHANNEL_DRUM = 9;
    private static final int PPQ=120;
    private static final int NOTE_DURATION=120;
    Button playButton;
    Button stopButton;
    Button drumChangeButton;

    MediaPlayer mediaPlayer;
    final static String PATH = Environment.getExternalStorageDirectory().getPath();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_midi);
        writeMidi();


        mediaPlayer = MediaPlayer.create(this, Uri.fromFile(new File(PATH+"/exampleout.mid")));
        mediaPlayer.setLooping(true);
        playButton = (Button) findViewById(R.id.midiPlayButton);
        stopButton = (Button) findViewById(R.id.midiStopButton);
        drumChangeButton = (Button) findViewById(R.id.drumChangeButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.start();
                loopHacking();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
            }
        });

        drumChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drumChange();
            }
        });
    }

    private void loopHacking(){
        long mHackLoopingPreview = 40;
        Timer HACK_loopTimer = new Timer();
        TimerTask HACK_loopTask = new TimerTask() {
            @Override public void run() {
                mediaPlayer.seekTo(0);
            }
        };
        long waitingTime = mediaPlayer.getDuration()-mHackLoopingPreview;
        HACK_loopTimer.schedule(HACK_loopTask, waitingTime, waitingTime);
    }

    private void writeMidi(){
        MidiTrack tempoTrack = new MidiTrack();
        MidiTrack bassTrack = new MidiTrack();
        MidiTrack guitarTrack = new MidiTrack();

    // 2. Add events to the tracks
    // Track 0 is the tempo map
        TimeSignature ts = new TimeSignature();
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);

        Tempo tempo = new Tempo();
        tempo.setBpm(80);

        tempoTrack.insertEvent(ts);
        tempoTrack.insertEvent(tempo);

        //Set Program
        bassTrack.insertEvent(new ProgramChange(0, CHANNEL_BASS, ProgramChange.MidiProgram.ELECTRIC_BASS_PICK.programNumber()));
        bassTrack.insertEvent(new ProgramChange(0, CHANNEL_GUITAR, ProgramChange.MidiProgram.ELECTRIC_GUITAR_CLEAN.programNumber()));

        // Track 1 will have some notes in it

        tempoTrack.insertNote(CHANNEL_DRUM, 53, 100, 0, NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_DRUM, 53, 100, 4*PPQ, NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_DRUM, 53, 100, 8*PPQ, NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_DRUM, 53, 100, 12*PPQ, NOTE_DURATION);

        tempoTrack.insertNote(CHANNEL_DRUM, 36, 100, 0, NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_DRUM, 36, 100, 3*PPQ, NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_DRUM, 38, 100, 4*PPQ, NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_DRUM, 36, 100, 6*PPQ, NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_DRUM, 36, 100, 10*PPQ, NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_DRUM, 36, 100, 11*PPQ, NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_DRUM, 38, 100, 12*PPQ, NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_DRUM, 38, 100, 14*PPQ, NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_DRUM, 38, 100, 15*PPQ, NOTE_DURATION);

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

        guitarTrack.insertNote(CHANNEL_GUITAR, 63, 100, 6*PPQ, NOTE_DURATION+200);
        guitarTrack.insertNote(CHANNEL_GUITAR, 66, 100, 6*PPQ, NOTE_DURATION+200);
        guitarTrack.insertNote(CHANNEL_GUITAR, 70, 100, 6*PPQ, NOTE_DURATION+200);
        guitarTrack.insertNote(CHANNEL_GUITAR, 72, 100, 6*PPQ, NOTE_DURATION+200);



       /* for(int i = 0; i < NOTE_COUNT; i++)
        {
            int channel = 0;
            int pitch = 30 + i;
            int velocity = 100;
            long tick = i * 480;
            long duration = 120;

            //tempoTrack.insertNote(9, 36, velocity, tick, duration);
            noteTrack.insertNote(channel, pitch, velocity, tick, duration);
        }*/

        // 3. Create a MidiFile with the tracks we created
        List<MidiTrack> tracks = new ArrayList<>();
        tracks.add(tempoTrack);
        tracks.add(bassTrack);
        tracks.add(guitarTrack);

        MidiFile midi = new MidiFile(MidiFile.DEFAULT_RESOLUTION, (ArrayList<MidiTrack>) tracks);

        // 4. Write the MIDI data to a file
        Log.d("PATH", PATH);
        File output = new File(PATH+"/exampleout.mid");
        try
        {
            midi.writeToFile(output);
        }
        catch(IOException e) {
            System.err.println(e);
        }
        System.out.print("FILE CREATED : "+output.getPath());
    }

    private void drumChange(){
        MidiTrack tempoTrack = new MidiTrack();
        MidiTrack noteTrack = new MidiTrack();

        // 2. Add events to the tracks
        // Track 0 is the tempo map
        TimeSignature ts = new TimeSignature();
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);

        Tempo tempo = new Tempo();
        tempo.setBpm(90);

        tempoTrack.insertEvent(ts);
        tempoTrack.insertEvent(tempo);

        noteTrack.insertEvent(new ProgramChange(0, CHANNEL_BASS, ProgramChange.MidiProgram.ELECTRIC_BASS_PICK.programNumber()));

        tempoTrack.insertNote(CHANNEL_DRUM, 36, 100, 0*PPQ, NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_DRUM, 36, 100, 2*PPQ, NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_DRUM, 36, 100, 4*PPQ, NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_DRUM, 36, 100, 6*PPQ, NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_DRUM, 36, 100, 8*PPQ, NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_DRUM, 36, 100, 10*PPQ, NOTE_DURATION);

        List<MidiTrack> tracks = new ArrayList<>();
        tracks.add(tempoTrack);
        tracks.add(noteTrack);

        MidiFile midi = new MidiFile(MidiFile.DEFAULT_RESOLUTION, (ArrayList<MidiTrack>) tracks);

        // 4. Write the MIDI data to a file
        Log.d("PATH", PATH);
        File output = new File(PATH+"/exampleout.mid");
        try
        {
            midi.writeToFile(output);
        }
        catch(IOException e) {
            System.err.println(e);
        }
        System.out.print("FILE CREATED : "+output.getPath());

    }
}
