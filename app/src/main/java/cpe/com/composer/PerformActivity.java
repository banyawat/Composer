package cpe.com.composer;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.media.audiofx.Visualizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import org.puredata.core.PdBase;

import java.util.ArrayList;

import cpe.com.composer.datamanager.ComposerBluetooth;
import cpe.com.composer.datamanager.ComposerJSON;
import cpe.com.composer.datamanager.ComposerMovement;
import cpe.com.composer.datamanager.ComposerParam;
import cpe.com.composer.datamanager.OnComposerBluetoothListener;
import cpe.com.composer.soundengine.ComposerMusicEngine;
import cpe.com.composer.soundengine.OnMusicActionListener;
import cpe.com.composer.viewmanager.ComposerGridViewAdapter;
import cpe.com.composer.viewmanager.ComposerVerticalSeekbar;

public class PerformActivity extends AppCompatActivity {
    private final String PATH = Environment.getExternalStorageDirectory().getPath();
    private static final String TAG = "DEbE";

    private ComposerVerticalSeekbar volumeAdjustBar;
    private GridView activeGridView;
    private ComposerGridViewAdapter activeGridViewAdapter;
    private Button backToInitialButton;

    public float intensity = 0;

    private ProgressBar vuMeterL, vuMeterR;
    private ProgressDialog connectingDialog;
    private ComposerBluetooth btModule;

    private ComposerMusicEngine musicEngine;

    private ArrayList<ComposerMovement> composerMovements;
    private int activeSlot = 0;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perform);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            String json = bundle.getString(ComposerParam.BUNDLE_KEY);
            composerMovements = new ComposerJSON().getComposerArray(json);
        }

        musicEngine = new ComposerMusicEngine(this, PATH);
        musicEngine.loadDatabase();

        initGui();
        initVisualizer();
        initVolumeAdjust();
        initGridView();
        initInitialButton();
        initParameterDebug();
    }

    @Override
    public void onResume(){
        super.onResume();
        initBluetooth();
    }

    public void initGui(){
        volumeAdjustBar = (ComposerVerticalSeekbar) findViewById(R.id.volumeAdjustBar);
        activeGridView = (GridView) findViewById(R.id.activeInstrumentGridView);
        vuMeterL = (ProgressBar) findViewById(R.id.vuMeterViewL);
        vuMeterR = (ProgressBar) findViewById(R.id.vuMeterViewR);
        backToInitialButton = (Button) findViewById(R.id.backToInitialButton);
    }

    private void initInitialButton(){
        backToInitialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initGridView(){
        activeGridViewAdapter = new ComposerGridViewAdapter(this);
        activeGridView.setAdapter(activeGridViewAdapter);
        musicEngine.setOnMusicActionListener(new OnMusicActionListener() {
            @Override
            public void onTrackAdded(final int id, final String title, final int program) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activeGridViewAdapter.addInstrument(id, title, program);
                        activeGridViewAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onTrackDeleted(final int id) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activeGridViewAdapter.removeInstrument(id);
                        activeGridViewAdapter.notifyDataSetChanged();
                    }
                });
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
        new BluetoothConnectionTask().execute();
    }

    private void initParameterDebug(){
        final EditText paramEditText = (EditText) findViewById(R.id.playDebugText);
        Button paramSendButton = (Button) findViewById(R.id.playDebugButton);
        paramSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String param = paramEditText.getText().toString();
                musicEngine.playId(Integer.parseInt(param));
                paramEditText.setText("");
            }
        });

        final EditText recieveEditText = (EditText) findViewById(R.id.recieveDebugText);
        Button receieveSendButton = (Button) findViewById(R.id.receieveDebugButton);
        receieveSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String param = recieveEditText.getText().toString();
                recieveEditText.setText("");
                try {
                    int receieveParam = Integer.parseInt(param);
                    if(receieveParam<5){
                        Log.d(TAG, "Left Finger: " + composerMovements.get(activeSlot).getFingerValue(false, receieveParam));
                    }
                    else if(receieveParam<10){
                        receieveParam -= 5;
                        Log.d(TAG, "Left Finger: " + composerMovements.get(activeSlot).getFingerValue(true, receieveParam));
                    }
                } catch (NumberFormatException e){
                    if(!param.isEmpty()){
                        switch(param){
                            case "a":
                                Log.d(TAG, "previous slot");
                                break;
                            case "b":
                                Log.d(TAG, "next slot");
                                break;
                            default:{
                                int pos = (int) param.charAt(0);
                                pos-=102;   //translate ascii to gesture position
                                Log.d(TAG, "Gesture: " + composerMovements.get(activeSlot).getGesture(pos));
                            }
                                break;
                        }
                    }
                }
            }
        });
    }

    class BluetoothConnectionTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btModule = new ComposerBluetooth();
            connectingDialog = new ProgressDialog(PerformActivity.this);
            connectingDialog.setMessage("Loading products. Please wait...");
            connectingDialog.setIndeterminate(false);
            connectingDialog.setCancelable(false);
            connectingDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            btModule.init();
            return null;
        }

        @Override
        protected void onPostExecute(Void voids){
            connectingDialog.dismiss();
            if(!btModule.checkBTState()){
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 0);
                btModule = new ComposerBluetooth();
                if(!btModule.checkBTState())
                    finish();
            }
            btModule.setOnDataReceiveListener(new OnComposerBluetoothListener() {
                @Override
                public void onDataReceieveListener(String message) {
                    Log.d(TAG, "get: " + message);
                    try {
                        int receieveParam = Integer.parseInt(message);
                        if(receieveParam<5){
                            int id = composerMovements.get(activeSlot).getFingerValue(false, receieveParam);
                            Log.d(TAG, "Left Finger: " + id);
                            musicEngine.playTrackId(id);
                        }
                        else if(receieveParam<10){
                            receieveParam -= 5;
                            int id = composerMovements.get(activeSlot).getFingerValue(true, receieveParam);
                            Log.d(TAG, "Right Finger: " + id);
                            musicEngine.doTranspose(id);
                        }
                    } catch (NumberFormatException e){
                        if(!message.isEmpty()){
                            switch(message){
                                case "a":
                                    Log.d(TAG, "previous slot");
                                    break;
                                case "b":
                                    Log.d(TAG, "next slot");
                                    break;
                                default:{
                                    int pos = (int) message.charAt(0);
                                    pos-=102;   //translate ascii to gesture position
                                    Log.d(TAG, "Gesture: " + composerMovements.get(activeSlot).getGesture(pos));
                                }
                                break;
                            }
                        }
                    }
                }
            });
        }
    }
}
