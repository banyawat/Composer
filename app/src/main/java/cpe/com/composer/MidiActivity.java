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
import java.util.Timer;
import java.util.TimerTask;

public class MidiActivity extends AppCompatActivity {
    private static final int CHANNEL_BASS = 0;
    private static final int CHANNEL_GUITAR = 1;
    private static final int CHANNEL_DRUM = 9;
    private static final int PPQ=120;
    private static final int NOTE_DURATION=120;
    Button drumPlayButton;
    Button stopButton;
    Button bassPlayButton;
    Button guitarPlayButton;
    Button changeKeyButton;
    Button drum2PlayButton;
    Button checkArrayButton;

    MidiFile midi;

    MediaPlayer mediaPlayer;
    final static String PATH = Environment.getExternalStorageDirectory().getPath();

    MidiTrack tempoTrack = new MidiTrack();
    MidiTrack drumTrack1 = new MidiTrack();
    MidiTrack drumTrack2 = new MidiTrack();
    MidiTrack bassTrack = new MidiTrack();
    MidiTrack guitarTrack = new MidiTrack();

    ArrayList<MidiTrack> tracks = new ArrayList<>();
    ArrayList<Integer> playingID = new ArrayList<>();

    /**
     *  Virtual MIDI ID
     *  0 = Drum 1 (Groove)
     *  1 = Drum 2 (Kick only)
     *  2 = Bass
     *  3 = Guitar
     *  4 = Key
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_midi);
        noteLoader();
        writeMidiFirstState();

        mediaPlayer = MediaPlayer.create(this, Uri.fromFile(new File(PATH+"/exampleout.mid")));

        drumPlayButton = (Button) findViewById(R.id.drumPlayButton);
        drum2PlayButton = (Button) findViewById(R.id.drum2PlayButton);
        stopButton = (Button) findViewById(R.id.midiStopButton);
        bassPlayButton = (Button) findViewById(R.id.bassPlayButton);
        guitarPlayButton = (Button) findViewById(R.id.guitarPlayButton);
        changeKeyButton = (Button) findViewById(R.id.keyChange);
        checkArrayButton = (Button) findViewById(R.id.checkArray);

        mediaPlayer.setLooping(true);
        drumPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playingID.add(0);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        //MidiFile midi = new MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks);
                        midi.addTrack(drumTrack1);

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

                        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.fromFile(new File(PATH+"/exampleout.mid")));
                        mediaPlayer.seekTo(currentPosition);
                        mediaPlayer.start();
                        loopHacking();
                    }
                }).run();
            }
        });

        drum2PlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        playingID.add(1);
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        mediaPlayer.stop();
                        mediaPlayer.release();

                        midi.removeTrack(midi.getTrackCount()-1);
                        midi.addTrack(drumTrack2);

                        File output = new File(PATH+"/exampleout.mid");
                        try
                        {
                            midi.writeToFile(output);
                        }
                        catch(IOException e) {
                            System.err.println(e);
                        }
                        System.out.print("FILE CREATED : "+output.getPath());

                        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.fromFile(new File(PATH+"/exampleout.mid")));
                        mediaPlayer.seekTo(currentPosition);
                        mediaPlayer.start();
                    }
                }).start();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
            }
        });

        bassPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            playingID.add(2);
                            int currentPosition = mediaPlayer.getCurrentPosition();
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            //MidiFile midi = new MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks);
                            midi.addTrack(bassTrack, 0);

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

                            mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.fromFile(new File(PATH+"/exampleout.mid")));
                            mediaPlayer.seekTo(currentPosition);
                            mediaPlayer.start();
                        }
                    }).run();
                }
            }
        });

        guitarPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            playingID.add(3);
                            int currentPosition = mediaPlayer.getCurrentPosition();
                            mediaPlayer.stop();
                            mediaPlayer.release();

                            tracks.add(guitarTrack);

                            midi.addTrack(guitarTrack, 0);

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

                            mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.fromFile(new File(PATH+"/exampleout.mid")));
                            mediaPlayer.seekTo(currentPosition);
                            mediaPlayer.start();
                        }
                    }).run();
                }
            }
        });

        changeKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            playingID.add(4);
                            int currentPosition = mediaPlayer.getCurrentPosition();
                            mediaPlayer.stop();
                            mediaPlayer.release();

                            midi.removeTrack(0);
                            midi.removeTrack(0);

                            bassTrack = new MidiTrack();
                            guitarTrack = new MidiTrack();

                            bassTrack.insertEvent(new ProgramChange(0, CHANNEL_BASS, ProgramChange.MidiProgram.ELECTRIC_BASS_PICK.programNumber()));
                            guitarTrack.insertEvent(new ProgramChange(0, CHANNEL_GUITAR, ProgramChange.MidiProgram.ELECTRIC_GUITAR_CLEAN.programNumber()));

                            bassTrack.insertNote(CHANNEL_BASS, 42, 100, 0, NOTE_DURATION);
                            bassTrack.insertNote(CHANNEL_BASS, 45, 100, 1 * PPQ, NOTE_DURATION);
                            bassTrack.insertNote(CHANNEL_BASS, 47, 100, 2 * PPQ, NOTE_DURATION);
                            bassTrack.insertNote(CHANNEL_BASS, 48, 100, 4 * PPQ, NOTE_DURATION);
                            bassTrack.insertNote(CHANNEL_BASS, 54, 100, 5 * PPQ, NOTE_DURATION);
                            bassTrack.insertNote(CHANNEL_BASS, 48, 100, 6 * PPQ, NOTE_DURATION);
                            bassTrack.insertNote(CHANNEL_BASS, 47, 100, 7 * PPQ, NOTE_DURATION);
                            bassTrack.insertNote(CHANNEL_BASS, 54, 100, 10 * PPQ, NOTE_DURATION);
                            bassTrack.insertNote(CHANNEL_BASS, 42, 100, 11 * PPQ, NOTE_DURATION);
                            bassTrack.insertNote(CHANNEL_BASS, 42, 100, 12 * PPQ, NOTE_DURATION);
                            bassTrack.insertNote(CHANNEL_BASS, 42, 100, 13 * PPQ, NOTE_DURATION);
                            bassTrack.insertNote(CHANNEL_BASS, 42, 100, 14 * PPQ, NOTE_DURATION);

                            guitarTrack.insertNote(CHANNEL_GUITAR, 66, 100, 0, NOTE_DURATION + 200);
                            guitarTrack.insertNote(CHANNEL_GUITAR, 69, 100, 0, NOTE_DURATION + 200);
                            guitarTrack.insertNote(CHANNEL_GUITAR, 73, 100, 0, NOTE_DURATION + 200);

                            guitarTrack.insertNote(CHANNEL_GUITAR, 66, 100, 3 * PPQ, 50);
                            guitarTrack.insertNote(CHANNEL_GUITAR, 69, 100, 3 * PPQ, 50);
                            guitarTrack.insertNote(CHANNEL_GUITAR, 73, 100, 3 * PPQ, 50);

                            guitarTrack.insertNote(CHANNEL_GUITAR, 66, 100, 6 * PPQ, NOTE_DURATION + 200);
                            guitarTrack.insertNote(CHANNEL_GUITAR, 69, 100, 6 * PPQ, NOTE_DURATION + 200);
                            guitarTrack.insertNote(CHANNEL_GUITAR, 73, 100, 6 * PPQ, NOTE_DURATION + 200);
                            guitarTrack.insertNote(CHANNEL_GUITAR, 75, 100, 6 * PPQ, NOTE_DURATION + 200);

                            guitarTrack.insertNote(CHANNEL_GUITAR, 66, 100, 9 * PPQ, 50);
                            guitarTrack.insertNote(CHANNEL_GUITAR, 69, 100, 9 * PPQ, 50);
                            guitarTrack.insertNote(CHANNEL_GUITAR, 73, 100, 9 * PPQ, 50);
                            guitarTrack.insertNote(CHANNEL_GUITAR, 75, 100, 9 * PPQ, 50);

                            midi.addTrack(bassTrack);
                            midi.addTrack(guitarTrack);

                            File output = new File(PATH + "/exampleout.mid");
                            try {
                                midi.writeToFile(output);
                            } catch (IOException e) {
                                System.err.println(e);
                            }
                            System.out.print("FILE CREATED : " + output.getPath());

                            mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.fromFile(new File(PATH + "/exampleout.mid")));
                            mediaPlayer.seekTo(currentPosition);
                            mediaPlayer.start();
                        }
                    }).start();
                }
            }
        });

        checkArrayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int id : playingID){
                    Log.d("PLAYINGID", String.valueOf(id));
                }
            }
        });
    }

    private void loopHacking(){
        long mHackLoopingPreview = 40;
        Timer HACK_loopTimer = new Timer();
        TimerTask HACK_loopTask = new TimerTask() {
            @Override public void run() {
                if(mediaPlayer.isPlaying()) {
                    MidiActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mediaPlayer.seekTo(0);
                        }
                    });
                }
                else
                    mediaPlayer.seekTo(0);
            }
        };
        long waitingTime = mediaPlayer.getDuration()-mHackLoopingPreview;
        HACK_loopTimer.schedule(HACK_loopTask, waitingTime, waitingTime);
    }

    private void noteLoader(){
        //DRUM
        TimeSignature ts = new TimeSignature();
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);

        Tempo tempo = new Tempo();
        tempo.setBpm(120);

        tempoTrack.insertEvent(ts);
        tempoTrack.insertEvent(tempo);

        drumTrack2.insertNote(CHANNEL_DRUM, 36, 100, 0, NOTE_DURATION);
        drumTrack2.insertNote(CHANNEL_DRUM, 36, 100, 4*PPQ, NOTE_DURATION);
        drumTrack2.insertNote(CHANNEL_DRUM, 36, 100, 8*PPQ, NOTE_DURATION);
        drumTrack2.insertNote(CHANNEL_DRUM, 36, 100, 12*PPQ, NOTE_DURATION);
        drumTrack2.insertNote(CHANNEL_DRUM, 0, 100, 15*PPQ, NOTE_DURATION);


        drumTrack1.insertNote(CHANNEL_DRUM, 53, 100, 0, NOTE_DURATION);
        drumTrack1.insertNote(CHANNEL_DRUM, 53, 100, 4*PPQ, NOTE_DURATION);
        drumTrack1.insertNote(CHANNEL_DRUM, 53, 100, 8*PPQ, NOTE_DURATION);
        drumTrack1.insertNote(CHANNEL_DRUM, 53, 100, 12*PPQ, NOTE_DURATION);

        drumTrack1.insertNote(CHANNEL_DRUM, 36, 100, 0, NOTE_DURATION);
        drumTrack1.insertNote(CHANNEL_DRUM, 36, 100, 3*PPQ, NOTE_DURATION);
        drumTrack1.insertNote(CHANNEL_DRUM, 38, 100, 4*PPQ, NOTE_DURATION);
        drumTrack1.insertNote(CHANNEL_DRUM, 36, 100, 6*PPQ, NOTE_DURATION);
        drumTrack1.insertNote(CHANNEL_DRUM, 36, 100, 10*PPQ, NOTE_DURATION);
        drumTrack1.insertNote(CHANNEL_DRUM, 36, 100, 11*PPQ, NOTE_DURATION);
        drumTrack1.insertNote(CHANNEL_DRUM, 38, 100, 12*PPQ, NOTE_DURATION);
        drumTrack1.insertNote(CHANNEL_DRUM, 38, 100, 14*PPQ, NOTE_DURATION);
        drumTrack1.insertNote(CHANNEL_DRUM, 38, 100, 15*PPQ, NOTE_DURATION);

        //Set Program
        bassTrack.insertEvent(new ProgramChange(0, CHANNEL_BASS, ProgramChange.MidiProgram.ELECTRIC_BASS_PICK.programNumber()));
        guitarTrack.insertEvent(new ProgramChange(0, CHANNEL_GUITAR, ProgramChange.MidiProgram.ELECTRIC_GUITAR_CLEAN.programNumber()));

        // Track 1 will have some notes in it

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
    }

    private void writeMidiFirstState(){
        // 3. Create a MidiFile with the tracks we created
        //tracks.add(tempoTrack);
        midi = new MidiFile(MidiFile.DEFAULT_RESOLUTION);
        midi.addTrack(tempoTrack);
        //MidiFile midi = new MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks);

        // 4. Write the MIDI data to a file
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
