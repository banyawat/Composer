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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import cpe.com.composer.datamanager.ComposerJSON;
import cpe.com.composer.datamanager.ComposerMovement;
import cpe.com.composer.datamanager.ComposerParam;
import cpe.com.composer.soundengine.ChordCell;
import cpe.com.composer.soundengine.ComposerMusicEngine;
import cpe.com.composer.viewmanager.ComposerGridViewAdapter;
import cpe.com.composer.viewmanager.MovementPagerAdapter;
import cpe.com.composer.viewmanager.PanelViewAdapter;
import cpe.com.composer.viewmanager.RecyclerTouchListener;

public class InitialActivity extends AppCompatActivity{
    public int activeDraggedId =-1; //active dragged instrument id
    public int activeSlotPanel = 0;
    public boolean isParameterPassed=false;

    private GridView controllerGrid;
    private ComposerGridViewAdapter adapter;

    private TabLayout tabLayout;
    private ViewPager mPager;
    private MovementPagerAdapter mPagerAdapter;
    private ImageButton goPerformButton;
    private ImageButton addSlotButton;
    private RecyclerView panelSlotView;
    private PanelViewAdapter panelViewAdapter;

    private ComposerMusicEngine musicEngine;

    private boolean mode=false;

    public ArrayList<ComposerMovement> composerMovements = new ArrayList<>();


    final static String PATH = Environment.getExternalStorageDirectory().getPath();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        initGui();

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            isParameterPassed=true;
            composerMovements = new ComposerJSON().getComposerArray(extras.getString(ComposerParam.BUNDLE_KEY));
            Log.d("DEVPER", extras.getString(ComposerParam.BUNDLE_KEY));
            Log.d("DEVPER", "SIZE: " + composerMovements.size());
        }

        initComponent();
        initPanelSlot();
    }

    private void initGui(){
        controllerGrid = (GridView) findViewById(R.id.controllerGrid);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout1);
        mPager = (ViewPager) findViewById(R.id.viewpager);
        panelSlotView = (RecyclerView) findViewById(R.id.panelSlot);
        goPerformButton = (ImageButton) findViewById(R.id.goPerformButton);
        addSlotButton = (ImageButton) findViewById(R.id.addPanelButton);
    }

    /**
     * Each event assigned to button
     */
    private void initComponent(){
        if(!isParameterPassed)
            composerMovements.add(new ComposerMovement());
        //Fragment
        mPagerAdapter = new MovementPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new FingerSetupFragment(), "FINGER");
        mPagerAdapter.addFragment(new ArmSetupFragment(), "ARM");
        mPager.setAdapter(mPagerAdapter);

        //Tab initialization
        tabLayout.setupWithViewPager(mPager);

        //music engine initiation;
        musicEngine = new ComposerMusicEngine(this, PATH);
        musicEngine.loadDatabase();

        adapter = new ComposerGridViewAdapter(this, musicEngine.getTrackList());
        controllerGrid.setAdapter(adapter);
        controllerGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, View.DRAG_FLAG_GLOBAL);
                activeDraggedId = (int) adapter.getItemId(i);
                return false;
            }
        });
        controllerGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                if(!mode)
                    musicEngine.playID((int) adapter.getItemId(position));
                else
                    musicEngine.doTranspose((int) adapter.getItemId(position));
            }
        });
        controllerGrid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(null, shadowBuilder, view, 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        goPerformButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle extras = new Bundle();
                extras.putString(ComposerParam.BUNDLE_KEY, new ComposerJSON(composerMovements).getJSONString());
                startActivity(new Intent(InitialActivity.this, PerformActivity.class).putExtras(extras));
            }
        });
    }

    /**
     * Change gridview information between instrument track and melody controller
     * @param mode - to indentify hand side and change it to track or melody
     */
    public void swapHandSide(boolean mode){
        if (!mode) {
            adapter = new ComposerGridViewAdapter(this, musicEngine.getTrackList());
        }
        else {
            adapter = new ComposerGridViewAdapter(this);
            ArrayList<ChordCell> chordCells = musicEngine.getChordList();
            for(ChordCell temp: chordCells){
                adapter.addInstrument(temp.getId(), temp.getTitle());
            }
        }
        controllerGrid.setAdapter(adapter);
        this.mode = mode;
    }

    /**
     * preset slot initiaing
     */
    private void initPanelSlot(){
        LinearLayoutManager panelSlotLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        panelSlotView.setLayoutManager(panelSlotLayout);
        panelViewAdapter = new PanelViewAdapter();

        if(isParameterPassed) {
            for (int i = 0; i < composerMovements.size(); i++) {
                panelViewAdapter.addPanel();
            }
        }
        else
            panelViewAdapter.addPanel();

        panelSlotView.setAdapter(panelViewAdapter);
        panelSlotView.addOnItemTouchListener(new RecyclerTouchListener(this, panelSlotView, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ((FingerSetupFragment)mPagerAdapter.getItem(0)).setPanel(position);
                ((ArmSetupFragment)mPagerAdapter.getItem(1)).refreshDrawable();
                panelViewAdapter.setActiveSlot(position);
                activeSlotPanel=position;
                panelViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

        addSlotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newposition = panelViewAdapter.getItemCount();
                if(newposition<ComposerParam.MAX_PANELSLOT) {
                    panelViewAdapter.addPanel();
                    ((FingerSetupFragment) mPagerAdapter.getItem(0)).addPanel(); // First Fragment
                    ((ArmSetupFragment) mPagerAdapter.getItem(1)).addPanel();
                    panelViewAdapter.setActiveSlot(newposition);
                    activeSlotPanel = newposition;
                    panelViewAdapter.notifyDataSetChanged();
                }
                else
                    Toast.makeText(getApplicationContext(), "Panel full", Toast.LENGTH_SHORT).show();
            }
        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP | ItemTouchHelper.DOWN ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if(panelViewAdapter.getItemCount()>1){
                    activeSlotPanel = panelViewAdapter.removePanel(swipeDir);
                    panelViewAdapter.notifyDataSetChanged();
                }else {
                    panelViewAdapter.notifyItemChanged(0);
                    Toast.makeText(getApplicationContext(), "No panel to remove", Toast.LENGTH_SHORT).show();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(panelSlotView);
    }
}
