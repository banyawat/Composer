package cpe.com.composer;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

import java.util.ArrayList;

import cpe.com.composer.soundengine.ChordCell;
import cpe.com.composer.soundengine.ComposerMusicEngine;
import cpe.com.composer.viewmanager.CustomGridViewAdapter;
import cpe.com.composer.viewmanager.PanelSlotViewAdapter;
import cpe.com.composer.viewmanager.RecyclerTouchListener;
import cpe.com.composer.viewmanager.fragmentPagerAdapter;

public class InitialActivity extends AppCompatActivity{
    public int activeDraggedId =-1; //active dragged instrument id

    private GridView controllerGrid;
    private CustomGridViewAdapter adapter;

    private TabLayout tabLayout;
    private ViewPager mPager;
    private fragmentPagerAdapter mPagerAdapter;
    private ImageButton goPerformButton;
    private Button checkArrButton;
    private RecyclerView panelSlotView;
    private PanelSlotViewAdapter mAdapter;

    private ComposerMusicEngine musicEngine;

    private boolean mode=false;


    final static String PATH = Environment.getExternalStorageDirectory().getPath();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        initGui();
        initComponent();
        initPanelSlot();
    }

    private void initGui(){
        controllerGrid = (GridView) findViewById(R.id.controllerGrid);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout1);
        mPager = (ViewPager) findViewById(R.id.viewpager);
        panelSlotView = (RecyclerView) findViewById(R.id.panelSlot);
        goPerformButton = (ImageButton) findViewById(R.id.goPerformButton);
        checkArrButton = (Button) findViewById(R.id.button);
    }

    private void initComponent(){
        //Fragment
        mPagerAdapter = new fragmentPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new FingerSetupFragment(), "FINGER");
        mPagerAdapter.addFragment(new ArmSetupFragment(), "ARM");
        mPager.setAdapter(mPagerAdapter);

        //Tab initialization
        tabLayout.setupWithViewPager(mPager);

        //initDatabase();
        musicEngine = new ComposerMusicEngine(this, PATH);
        musicEngine.loadDatabase();

        adapter = new CustomGridViewAdapter(this, musicEngine.getTrackList());
        controllerGrid.setAdapter(adapter);
        controllerGrid.setOnItemLongClickListener(new TouchListener());
        controllerGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                if(!mode)
                    musicEngine.playID((int) adapter.getItemId(position));
                else
                    musicEngine.doTranspose((int) adapter.getItemId(position));
            }
        });

        checkArrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicEngine.checkPlayingArray();
            }
        });

        goPerformButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InitialActivity.this, PerformActivity.class));
            }
        });
    }

    public void swapGrid(boolean mode){
        if (!mode) {
            adapter = new CustomGridViewAdapter(this, musicEngine.getTrackList());
        }
        else {
            adapter = new CustomGridViewAdapter(this);
            ArrayList<ChordCell> chordCells = musicEngine.getChordList();
            for(ChordCell temp: chordCells){
                adapter.addInstrument(temp.getId(), temp.getTitle());
            }
        }
        controllerGrid.setAdapter(adapter);
        this.mode = mode;
    }

    private void initPanelSlot(){
        LinearLayoutManager panelSlotLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        panelSlotView.setLayoutManager(panelSlotLayout);
        mAdapter = new PanelSlotViewAdapter();
        mAdapter.addPanel("ADD");
        mAdapter.addPanel(String.valueOf(mAdapter.getItemCount()));
        panelSlotView.setAdapter(mAdapter);
        panelSlotView.addOnItemTouchListener(new RecyclerTouchListener(this, panelSlotView, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position==mAdapter.getItemCount()-1){
                    mAdapter.addPanel(String.valueOf(mAdapter.getItemCount()));
                    ((FingerSetupFragment)mPagerAdapter.getItem(0)).addPanel(); // First Fragment
                    mAdapter.setActiveSlot(position);
                    mAdapter.notifyDataSetChanged();
                }
                else {
                    ((FingerSetupFragment)mPagerAdapter.getItem(0)).setPanel(position);
                    mAdapter.setActiveSlot(position);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    private final class TouchListener implements  AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, view, View.DRAG_FLAG_GLOBAL);
            activeDraggedId = (int) adapter.getItemId(i);
            return false;
        }
    }
}
