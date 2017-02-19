package cpe.com.composer;

import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import cpe.com.composer.datamanager.ComposerBluetooth;
import cpe.com.composer.datamanager.ComposerJSON;
import cpe.com.composer.datamanager.ComposerMovement;
import cpe.com.composer.datamanager.ComposerParam;
import cpe.com.composer.datamanager.OnComposerBluetoothListener;
import cpe.com.composer.soundengine.ComposerMusicEngine;
import cpe.com.composer.soundengine.OnChangeBpmListener;
import cpe.com.composer.soundengine.OnMusicActionListener;
import cpe.com.composer.viewmanager.ComposerGridViewAdapter;
import cpe.com.composer.viewmanager.ComposerVerticalSeekbar;
import cpe.com.composer.viewmanager.PanelViewAdapter;

public class PerformActivity extends AppCompatActivity {
    private final String PATH = Environment.getExternalStorageDirectory().getPath();
    private static final String TAG = "DEbE";

    private GridView activeGridView;
    private ComposerGridViewAdapter activeGridViewAdapter;

    private RecyclerView panelSlotView;
    private PanelViewAdapter panelViewAdapter;

    private ComposerVerticalSeekbar volumeAdjustBar;
    private TextView bpmTextView;
    private Button backToInitialButton;
    private Button clearTrackButton;

    private float intensity = 0;

    private ProgressBar vuMeterL, vuMeterR;

    private ComposerBluetooth btModule;
    private ComposerMusicEngine musicEngine;

    private ArrayList<ComposerMovement> composerMovements;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perform);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            String json = bundle.getString(ComposerParam.BUNDLE_KEY);
            composerMovements = new ComposerJSON().getComposerArray(json);
            for(ComposerMovement composerMovement: composerMovements){
                composerMovement.mirror();
            }
        }

        musicEngine = new ComposerMusicEngine(this, PATH);
        musicEngine.loadDatabase();

        initGui();
        initVisualizer();
        initVolumeAdjust();
        initBpmIndicator();
        initGridView();
        initInitialButton();
        initClearTracksButton();
        initPanelSlot();
        initBluetooth();
    }

    private void initGui(){
        bpmTextView = (TextView) findViewById(R.id.bpmTextView);
        volumeAdjustBar = (ComposerVerticalSeekbar) findViewById(R.id.volumeAdjustBar);
        activeGridView = (GridView) findViewById(R.id.activeInstrumentGridView);
        vuMeterL = (ProgressBar) findViewById(R.id.vuMeterViewL);
        vuMeterR = (ProgressBar) findViewById(R.id.vuMeterViewR);
        backToInitialButton = (Button) findViewById(R.id.backToInitialButton);
        clearTrackButton = (Button) findViewById(R.id.clearTrackButton);
        panelSlotView = (RecyclerView) findViewById(R.id.peformPanelSlot);
    }

    private void initBpmIndicator(){
        musicEngine.onChangeBpmListener(new OnChangeBpmListener() {
            @Override
            public void OnChangeBpmListener(float bpm) {
                bpmTextView.setText("BPM: " + bpm);
            }
        });
    }

    private void initInitialButton(){
        backToInitialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicEngine.clearTracks();
                finish();
            }
        });
    }

    private void initGridView(){
        activeGridViewAdapter = new ComposerGridViewAdapter(this);
        activeGridView.setAdapter(activeGridViewAdapter);
        musicEngine.setOnMusicActionListener(new OnMusicActionListener() {
            @Override
            public void onTrackAdded(int id, String title, int program) {
                activeGridViewAdapter.addInstrument(id, title, program);
                activeGridView.setAdapter(activeGridViewAdapter);
            }

            @Override
            public void onTrackDeleted(int id) {
                activeGridViewAdapter.removeInstrument(id);
                activeGridView.setAdapter(activeGridViewAdapter);
            }

            @Override
            public void onTrackReplaced(int removeId, int id, String title, int program) {
                activeGridViewAdapter.removeInstrument(removeId);
                activeGridViewAdapter.addInstrument(id, title, program);
                activeGridView.setAdapter(activeGridViewAdapter);
            }
        });
    }

    private void initVisualizer(){
        int rate = Visualizer.getMaxCaptureRate();
        Visualizer audioOutput = new Visualizer(0);
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
    }

    private void initVolumeAdjust(){
        volumeAdjustBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int amount, boolean b) {
                float vol = (float) (1 - (Math.log(100 - amount) / Math.log(100)));
                musicEngine.setVolume(vol);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initClearTracksButton(){
        clearTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicEngine.clearTracks();
                activeGridViewAdapter.clearInstrument();
                activeGridView.setAdapter(activeGridViewAdapter);
            }
        });
    }

    private void initPanelSlot(){
        LinearLayoutManager panelSlotLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        panelSlotView.setLayoutManager(panelSlotLayout);
        panelViewAdapter = new PanelViewAdapter();
        for(int i=0;i<composerMovements.size();i++)
            panelViewAdapter.addPanel();
        panelSlotView.setAdapter(panelViewAdapter);
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

    private void initBluetooth(){
        btModule = new ComposerBluetooth();
        btModule.setOnDataReceievedListener(new OnComposerBluetoothListener() {
            @Override
            public void onDataReceieveListener(String message) {
                try {
                    int receieveParam = Integer.parseInt(message);
                    if(receieveParam<5){
                        int id = composerMovements.get(panelViewAdapter.getActiveSlot()).getFingerValue(false, receieveParam);
                        Log.d(TAG, "Left Finger: " + id);
                        if(id!=-1)
                            musicEngine.playTrackId(id);
                    }
                    else if(receieveParam<10){
                        receieveParam -= 5;
                        int id = composerMovements.get(panelViewAdapter.getActiveSlot()).getFingerValue(true, receieveParam);
                        Log.d(TAG, "Right Finger: " + id);
                        if(id!=-1)
                            musicEngine.doTranspose(id);
                    }
                } catch (NumberFormatException e){
                    if(!message.isEmpty()&&(message.charAt(0)>='a'&&message.charAt(0)<='z')){
                        switch(message){
                            case "a":
                                Log.d(TAG, "previous slot");
                                scrollPanelSot(-1);
                                break;
                            case "b":
                                Log.d(TAG, "next slot");
                                scrollPanelSot(1);
                                break;
                            default:{
                                Log.d(TAG, "Message: " + message);
                                int pos = (int) message.charAt(0);
                                pos-=102;   //translate ascii to gesture position
                                Log.d(TAG, "POS: " + pos);
                                int id = composerMovements.get(panelViewAdapter.getActiveSlot()).getGesture(pos);
                                Log.d(TAG, "Gesture: " + id);
                                if(id!=-1)
                                    musicEngine.setBpm(id);
                            }
                            break;
                        }
                    }
                }
            }
        });
    }

    private void scrollPanelSot(int dx){
        panelViewAdapter.scrollPanel(dx);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                panelViewAdapter.notifyDataSetChanged();
            }
        });
        Log.d(TAG, "" + panelViewAdapter.getActiveSlot());
    }
}
