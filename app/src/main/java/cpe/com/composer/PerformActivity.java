package cpe.com.composer;

import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import org.puredata.core.PdBase;

import java.util.ArrayList;

import cpe.com.composer.datamanager.ComposerBluetooth;
import cpe.com.composer.datamanager.ComposerJSON;
import cpe.com.composer.datamanager.ComposerParam;
import cpe.com.composer.soundengine.ComposerMusicEngine;
import cpe.com.composer.viewmanager.ComposerGridViewAdapter;
import cpe.com.composer.viewmanager.ComposerVerticalSeekbar;

public class PerformActivity extends AppCompatActivity {
    private String PATH = Environment.getExternalStorageDirectory().getPath();
    private ComposerVerticalSeekbar volumeAdjustBar;
    private GridView activeGridView;
    private Button backToInitialButton;

    private ArrayList<Integer> sampleSet;
    private ArrayList<String> instrumentTitle;
    private Visualizer audioOutput = null;
    public float intensity = 0;

    private ProgressBar vuMeterL, vuMeterR;
    private ComposerBluetooth btModule;

    ComposerMusicEngine musicEngine;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perform);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            String json = bundle.getString(ComposerParam.BUNDLE_KEY);
            Log.d("DEVDEV", json);
            new ComposerJSON().getComposerArray(json);
        }

        musicEngine = new ComposerMusicEngine(this, PATH);

        initGui();
        initBluetooth();
        initComponent();
    }

    public void initGui(){
        volumeAdjustBar = (ComposerVerticalSeekbar) findViewById(R.id.volumeAdjustBar);
        activeGridView = (GridView) findViewById(R.id.activeInstrumentGridView);
        vuMeterL = (ProgressBar) findViewById(R.id.vuMeterViewL);
        vuMeterR = (ProgressBar) findViewById(R.id.vuMeterViewR);
        backToInitialButton = (Button) findViewById(R.id.backToInitialButton);
    }

    @Override
    protected void onResume() {
        super.onResume();
        btModule.onResumeActivity();

    }

    @Override
    protected void onPause() {
        super.onPause();
        btModule.onPauseParent();
    }

    private void initComponent(){
        sampleSet = new ArrayList<>();
        instrumentTitle = new ArrayList<>();

        for(int i=0;i<2;i++){
            sampleSet.add(i);
        }

        backToInitialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        activeGridView.setAdapter(new ComposerGridViewAdapter(this, sampleSet, instrumentTitle));
        activeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {  }
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
        btModule = new ComposerBluetooth();
    }
}
