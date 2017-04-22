package cpe.com.composer;

import android.graphics.drawable.Drawable;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import cpe.com.composer.datamanager.ComposerBluetooth;
import cpe.com.composer.datamanager.ComposerJSON;
import cpe.com.composer.datamanager.ComposerMovement;
import cpe.com.composer.datamanager.ComposerParam;
import cpe.com.composer.datamanager.OnComposerBluetoothListener;
import cpe.com.composer.soundengine.ComposerGesture;
import cpe.com.composer.soundengine.ComposerLeftHand;
import cpe.com.composer.soundengine.ComposerMusicEngine;
import cpe.com.composer.soundengine.ComposerRightHand;
import cpe.com.composer.soundengine.OnMusicActionListener;
import cpe.com.composer.viewmanager.ArmViewController;
import cpe.com.composer.viewmanager.ComposerVerticalSeekbar;
import cpe.com.composer.viewmanager.HandViewController;
import cpe.com.composer.viewmanager.OnImageLoadedListener;
import cpe.com.composer.viewmanager.PanelViewAdapter;

public class PerformActivity extends AppCompatActivity {
    private final String PATH = Environment.getExternalStorageDirectory().getPath();
    private static final String TAG = "DEbE";

    private HandViewController leftHandView;
    private HandViewController rightHandView;
    private ArmViewController gestureView;

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
    private ArrayList<Integer> idImageType = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perform);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            String json = bundle.getString(ComposerParam.BUNDLE_KEY);
            composerMovements = new ComposerJSON().getComposerArray(json);
            /*for(ComposerMovement composerMovement: composerMovements){
                //composerMovement.flip();
            }*/
        }

        musicEngine = new ComposerMusicEngine(this, PATH);
        musicEngine.loadDatabase();

        //Load image database
        for(ComposerLeftHand leftHand: musicEngine.getTrackList()){
            if(leftHand.getChannel()==9)
                idImageType.add(ComposerParam.INSTRUMENT_MAP.get(-1));
            else {
                idImageType.add(ComposerParam.INSTRUMENT_MAP.get(leftHand.getProgram()));
            }

        }
        for(ComposerRightHand ignored : musicEngine.getChordList()){
            idImageType.add(ComposerParam.INSTRUMENT_MAP.get(-2));
        }
        for(ComposerGesture ignored : musicEngine.getTempoList()){
            idImageType.add(ComposerParam.INSTRUMENT_MAP.get(-3));
        }

        initGui();
        initFragment();
        initVisualizer();
        initVolumeAdjust();
        initInitialButton();
        initClearTracksButton();
        initPanelSlot();
        initEvent();
        //initBluetooth();

        //DEBUGGER BUTTON
        /*testButton.setOnClickListener(new View.OnClickListener() {
            int count=0;
            @Override
            public void onClick(View view) {
                switch (count){
                    case 0:
                        musicEngine.playId(composerMovements.get(0).getFingerValue(false, 0));
                        break;
                    case 1:
                        musicEngine.playId(composerMovements.get(0).getFingerValue(false, 3));
                        break;
                    case 2:
                        musicEngine.doTranspose(composerMovements.get(0).getFingerValue(true, 1));
                        break;
                    case 3:
                        musicEngine.playId(composerMovements.get(0).getFingerValue(false, 1));
                        break;
                    case 4:
                        musicEngine.doTranspose(composerMovements.get(0).getFingerValue(true, 0));
                        break;
                    default: break;
                }
                count++;
            }
        });

        testButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(panelViewAdapter.getActiveSlot()==0)
                    scrollPanelSot(1);
                else
                    scrollPanelSot(-1);
            }
        });*/
    }

    private void initEvent(){
        musicEngine.setOnMusicActionListener(new OnMusicActionListener() {
            @Override
            public void onTrackAdded(int id, String title, int program) {
                ComposerMovement movement = composerMovements.get(panelViewAdapter.getActiveSlot());
                for(int i=0;i<5;i++){
                    if(movement.getFingerValue(false, i)==id){
                        leftHandView.setActive(i, true);
                        break;
                    }
                }
            }

            @Override
            public void onTrackDeleted(int id) {
                ComposerMovement movement = composerMovements.get(panelViewAdapter.getActiveSlot());
                for(int i=0;i<5;i++){
                    if(movement.getFingerValue(false, i)==id){
                        leftHandView.setActive(i, false);
                        break;
                    }
                }
            }

            @Override
            public void onTrackReplaced(int removeId, int id, String title, int program) {
                ComposerMovement movement = composerMovements.get(panelViewAdapter.getActiveSlot());
                for(int i=0;i<5;i++){
                    if(movement.getFingerValue(false, i)==removeId)
                       leftHandView.setActive(i, false);
                    if(movement.getFingerValue(false, i)==id)
                        leftHandView.setActive(i, true);

                }
            }

            @Override
            public void onTranspose(int id) {
                ComposerMovement movement = composerMovements.get(panelViewAdapter.getActiveSlot());
                rightHandView.clearActive();
                for(int i=0;i<5;i++) {
                    if(movement.getFingerValue(true, i)==id) {
                        rightHandView.setActive(i, true);
                        break;
                    }
                }
            }

            @Override
            public void onBpm(float bpm) {
                bpmTextView.setText("BPM: " + bpm);
            }
        });

        rightHandView.setOnImageLoadListener(new OnImageLoadedListener() {
            @Override
            public void OnImageLoadedListener() {
                refreshDrawable();
            }
        });
    }

    private void initGui(){
        bpmTextView = (TextView) findViewById(R.id.bpmTextView);
        volumeAdjustBar = (ComposerVerticalSeekbar) findViewById(R.id.volumeAdjustBar);
        vuMeterL = (ProgressBar) findViewById(R.id.vuMeterViewL);
        vuMeterR = (ProgressBar) findViewById(R.id.vuMeterViewR);
        backToInitialButton = (Button) findViewById(R.id.backToInitialButton);
        clearTrackButton = (Button) findViewById(R.id.clearTrackButton);
        panelSlotView = (RecyclerView) findViewById(R.id.peformPanelSlot);
    }

    private void initFragment(){
        leftHandView = new HandViewController();
        rightHandView = new HandViewController();
        gestureView = new ArmViewController();

        Bundle args = new Bundle();
        args.putBoolean("bool", true);
        rightHandView.setArguments(args);

        FragmentManager manager = getSupportFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.performLeftHandView, leftHandView);
        transaction.commit();

        FragmentTransaction transaction2 = manager.beginTransaction();
        transaction2.replace(R.id.performRightHandView, rightHandView);
        transaction2.commit();

        FragmentTransaction transaction3 = manager.beginTransaction();
        transaction3.replace(R.id.gestureView, gestureView);
        transaction3.commit();
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
                leftHandView.clearActive();
                rightHandView.clearActive();
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
        //btModule.onResumeActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //btModule.onPauseParent();
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
        refreshDrawable();
    }

    private void refreshDrawable(){
        ComposerMovement movement = composerMovements.get(panelViewAdapter.getActiveSlot());
        for(int i=0;i<5;i++){
            int leftId = movement.getFingerValue(false, i);
            if(leftId!=-1) {
                String Title = musicEngine.getTitleById(leftId);
                Drawable image = ContextCompat.getDrawable(getApplicationContext(), idImageType.get(leftId - 1));
                leftHandView.setDrawableAndText(i, image, Title);
                if(musicEngine.isTrackPlaying(leftId))
                    leftHandView.setActive(i, true);
                else
                    leftHandView.setActive(i, false);
            }
            else{
                leftHandView.setDrawableAndText(i, null, "");
                leftHandView.setActive(i , false);
            }

            int rightId = movement.getFingerValue(true, i);
            if(rightId!=-1){
                String Title = musicEngine.getTitleById(rightId);
                rightHandView.setDrawableAndText(i, ContextCompat.getDrawable(getApplicationContext(), ComposerParam.INSTRUMENT_MAP.get(-2)), Title);
                if(musicEngine.isKeyPlaying(rightId)){
                    rightHandView.setActive(i, true);
                }
                else
                    rightHandView.setActive(i, false);
            }
            else{
                rightHandView.setDrawableAndText(i, null, "");
                rightHandView.setActive(i, false);
            }

            int gestureId = movement.getGesture(i);
            if(gestureId!=-1) {
                String Title = musicEngine.getTitleById(movement.getGesture(i));
                gestureView.setDrawableAndText(i, ContextCompat.getDrawable(getApplicationContext(), ComposerParam.INSTRUMENT_MAP.get(-3)), Title);
                Log.d(TAG, "Result: " + musicEngine.isTempoPlaying(gestureId));
                if(musicEngine.isTempoPlaying(gestureId)) {
                    gestureView.setActive(i, true);
                }
                else
                    gestureView.setActive(i, false);
            }
            else {
                gestureView.setDrawableAndText(i, null, "");
                gestureView.setActive(i, false);
            }
        }
    }

}
