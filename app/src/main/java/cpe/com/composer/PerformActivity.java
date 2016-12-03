package cpe.com.composer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.puredata.core.PdBase;

import java.util.ArrayList;

import cpe.com.composer.soundengine.SimplePatch;
import cpe.com.composer.viewmanager.CustomGridViewAdapter;
import cpe.com.composer.viewmanager.VerticalSeekBar;

public class PerformActivity extends AppCompatActivity {
    private VerticalSeekBar verticalSeekBar;
    private GridView activeGridView;
    private ArrayList<Integer> sampleSet;
    private ArrayList<String> commandSet;
    private SimplePatch purePatch; //least

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perform);
        initGui();
        initComponent();
    }

    public void initGui(){
        verticalSeekBar = (VerticalSeekBar) findViewById(R.id.volumeAdjustBar);
        activeGridView = (GridView) findViewById(R.id.activeInstrumentGridView);
    }

    private void initComponent(){
        sampleSet = new ArrayList<>();
        commandSet = new ArrayList<>();
        commandSet.add("percussion");
        commandSet.add("bass");
        for(int i =
            0; i<2;i++){
            sampleSet.add(i);
        }
        activeGridView.setAdapter(new CustomGridViewAdapter(this, sampleSet));
        activeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PdBase.sendFloat("playS", 1f);
                PdBase.sendBang(commandSet.get(i));
            }
        });
    }
}
