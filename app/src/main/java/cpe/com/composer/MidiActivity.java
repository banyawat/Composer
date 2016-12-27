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
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.TimeSignature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MidiActivity extends AppCompatActivity {
    private static final int CHANNEL_PERC=9;
    private static final int PPQ=240;
    private static final int NOTE_DURATION=120;
    Button playButton;
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
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    mediaPlayer.start();
            }
        });
    }

    private void writeMidi(){
        MidiTrack tempoTrack = new MidiTrack();
        MidiTrack noteTrack = new MidiTrack();

    // 2. Add events to the tracks
    // Track 0 is the tempo map
        TimeSignature ts = new TimeSignature();
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);

        Tempo tempo = new Tempo();
        tempo.setBpm(120);

        tempoTrack.insertEvent(ts);
        tempoTrack.insertEvent(tempo);

        // Track 1 will have some notes in it
        final int NOTE_COUNT = 16;

        tempoTrack.insertNote(CHANNEL_PERC, 36, 100, 0, NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_PERC, 36, 100, (long)(1.5*PPQ), NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_PERC, 38, 100, 2*PPQ, NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_PERC, 36, 100, 3*PPQ, NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_PERC, 36, 100, 5*PPQ, NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_PERC, 36, 100, (long)(5.5*PPQ), NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_PERC, 38, 100, 6*PPQ, NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_PERC, 38, 100, 7*PPQ, NOTE_DURATION);
        tempoTrack.insertNote(CHANNEL_PERC, 38, 100, (long)(7.5*PPQ), NOTE_DURATION);

        tempoTrack.insertNote(CHANNEL_PERC, 53, 100, 0, NOTE_DURATION);

        //tempoTrack.insertNote(CHANNEL_PERC, 53, 100, 6*PPQ, NOTE_DURATION);

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
