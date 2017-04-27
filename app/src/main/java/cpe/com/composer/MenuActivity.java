package cpe.com.composer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import cpe.com.composer.datamanager.ComposerParam;

public class MenuActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    Button startButton, presetButton, storeButton;
    Bundle extras = new Bundle();

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
    }

    private void initAction(){
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, SetupActivity.class));
            }
        });
        presetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extras.putInt(ComposerParam.MENU_BUNDLE_KEY, 0);
                startActivity(new Intent(MenuActivity.this, PreviewActivity.class).putExtras(extras));
            }
        });
        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkConnected()) {
                    extras.putInt(ComposerParam.MENU_BUNDLE_KEY, 1);
                    startActivity(new Intent(MenuActivity.this, PreviewActivity.class).putExtras(extras));
                }
                else
                    Toast.makeText(getApplicationContext(), "No network connection, Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
