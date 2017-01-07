package cpe.com.composer;

import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;

import java.util.ArrayList;

import cpe.com.composer.datamanager.BluetoothModule;
import cpe.com.composer.viewmanager.CustomGridViewAdapter;
import cpe.com.composer.viewmanager.VerticalSeekBar;

public class PerformActivity extends AppCompatActivity {
    private VerticalSeekBar volumeAdjustBar;
    private GridView activeGridView;
    private ArrayList<Integer> sampleSet;
    private ArrayList<String> instrumentTitle;
    private ArrayList<String> commandSet;
    private Visualizer audioOutput = null;
    public float intensity = 0;

    private ProgressBar vuMeterL, vuMeterR;
    private BluetoothModule btModule;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perform);
        initGui();
        initBluetooth();
        initComponent();
    }

    public void initGui(){
        volumeAdjustBar = (VerticalSeekBar) findViewById(R.id.volumeAdjustBar);
        activeGridView = (GridView) findViewById(R.id.activeInstrumentGridView);
        vuMeterL = (ProgressBar) findViewById(R.id.vuMeterViewL);
        vuMeterR = (ProgressBar) findViewById(R.id.vuMeterViewR);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PdBase.startAudio();
        btModule.onResumeActivity();

    }

    @Override
    protected void onPause() {
        super.onPause();
        PdAudio.stopAudio();
        btModule.onPauseParent();
    }

    private void initComponent(){
        PdBase.sendFloat("playS", 1f);
        sampleSet = new ArrayList<>();
        commandSet = new ArrayList<>();
        instrumentTitle = new ArrayList<>();
        commandSet.add("percussion");
        commandSet.add("bass");
        for(int i =
            0; i<2;i++){
            sampleSet.add(i);
        }
        activeGridView.setAdapter(new CustomGridViewAdapter(this, sampleSet, instrumentTitle));
        activeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PdBase.sendFloat("playS", 1f);
                PdBase.sendBang(commandSet.get(i));
                Toast.makeText(getApplicationContext(), "PLAY", Toast.LENGTH_SHORT).show();
            }
        });

        int rate = Visualizer.getMaxCaptureRate();
        audioOutput = new Visualizer(0);
        audioOutput.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int i) {
                intensity = ((float) bytes[0] + 128f) / 256;
                vuMeterL.setProgress((int) (intensity*100));
                vuMeterR.setProgress((int) (intensity*100));
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int i) {

            }
        }, rate, true, false);

        Log.d("rate", String.valueOf(Visualizer.getMaxCaptureRate()));
        audioOutput.setEnabled(true);

        volumeAdjustBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                PdBase.sendFloat("changeVolume", (float)i/120);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void initBluetooth(){
        btModule = new BluetoothModule();
    }
}
