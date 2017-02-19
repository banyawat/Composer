package cpe.com.composer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import cpe.com.composer.datamanager.ComposerBluetooth;

public class BluetoothActivity extends AppCompatActivity {
    private ComposerBluetooth btModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        Log.d("Debug", "Started");
        initBluetooth();
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
    }
}
