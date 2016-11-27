package cpe.com.composer;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {
    Button startButton, presetButton, storeButton, settingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initGui();
        initAction();
    }

    private void initGui(){
        startButton = (Button) findViewById(R.id.start_button);
        presetButton = (Button) findViewById(R.id.saved_preset_button);
        storeButton = (Button) findViewById(R.id.store_button);
        settingButton = (Button) findViewById(R.id.setting_button);
    }

    private void initAction(){
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, InitialActivity.class));
            }
        });
        presetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, PresetActivity.class));
            }
        });
        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkConnected())
                    startActivity(new Intent(MenuActivity.this, StoreActivity.class));
                else
                    Toast.makeText(getApplicationContext(), "No network connection, Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, SettingsActivity.class));
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
