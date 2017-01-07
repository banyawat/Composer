package cpe.com.composer;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import cpe.com.composer.datamanager.PresetDatabase;
import cpe.com.composer.viewmanager.CustomGridViewAdapter;
import cpe.com.composer.viewmanager.PanelSlotViewAdapter;
import cpe.com.composer.viewmanager.RecyclerTouchListener;
import cpe.com.composer.viewmanager.fragmentPagerAdapter;

public class InitialActivity extends AppCompatActivity{
    private GridView controllerGrid;
    private ArrayList<Integer> sampleSet;
    private ArrayList<String> commandSet;
    private TabLayout tabLayout;
    private ViewPager mPager;
    private fragmentPagerAdapter mPagerAdapter;
    public int activeSlot=66;
    private ImageButton goPerformButton;

    private SQLiteDatabase mDb;
    private PresetDatabase mHelper;
    private Cursor mCursor;

    private RecyclerView panelSlotView;
    private PanelSlotViewAdapter mAdapter;

    private ArrayList<Integer> instrumentID = new ArrayList<>();
    private ArrayList<String> instrumentTitle = new ArrayList<>();
    private ArrayList<String> noteArray = new ArrayList<>();

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
    }

    private void initComponent(){
        //Fragment
        mPagerAdapter = new fragmentPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new FingerConfigFragment(), "FINGER");
        mPagerAdapter.addFragment(new ArmConfigFragment(), "ARM");
        mPager.setAdapter(mPagerAdapter);

        //Tab initialization
        tabLayout.setupWithViewPager(mPager);

        initDatabase();

        controllerGrid.setAdapter(new CustomGridViewAdapter(this, instrumentID, instrumentTitle));
        controllerGrid.setOnItemLongClickListener(new MyTouchListener());
        controllerGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), noteArray.get(i), Toast.LENGTH_SHORT).show();
            }
        });

        goPerformButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InitialActivity.this, PerformActivity.class));
            }
        });
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
                    ((FingerConfigFragment)mPagerAdapter.getItem(0)).addPanel(); // First Fragment
                    mAdapter.setActiveSlot(position);
                    mAdapter.notifyDataSetChanged();
                }
                else {
                    ((FingerConfigFragment)mPagerAdapter.getItem(0)).setPanel(position);
                    mAdapter.setActiveSlot(position);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    private void initDatabase(){
        mHelper = new PresetDatabase(this);
        mDb = mHelper.getWritableDatabase();
        mCursor = mDb.rawQuery("SELECT " + PresetDatabase.COL_TITLE + "," + PresetDatabase.COL_NOTE + " FROM " + PresetDatabase.TABLE_NAME, null);
        Toast.makeText(this, "Database Download Complete", Toast.LENGTH_SHORT).show();

        mCursor.moveToFirst();
        while ( !mCursor.isAfterLast() ){
            instrumentTitle.add(mCursor.getString(mCursor.getColumnIndex(PresetDatabase.COL_TITLE)));
            noteArray.add(mCursor.getString(mCursor.getColumnIndex(PresetDatabase.COL_NOTE)));
            instrumentID.add(mCursor.getInt(mCursor.getColumnIndex(PresetDatabase.COL_NOTE)));
            mCursor.moveToNext();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private final class MyTouchListener implements  AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, view, View.DRAG_FLAG_GLOBAL);
            activeSlot = i;
            return false;
        }
    }
}
