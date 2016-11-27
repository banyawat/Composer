package cpe.com.composer;

import android.content.ClipData;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.puredata.core.PdBase;

import java.io.IOException;
import java.util.ArrayList;

import cpe.com.composer.soundengine.SimplePatch;
import cpe.com.composer.viewmanager.CustomGridViewAdapter;
import cpe.com.composer.viewmanager.RecyclerTouchListener;
import cpe.com.composer.viewmanager.PanelSlotViewAdapter;
import cpe.com.composer.viewmanager.fragmentPagerAdapter;

public class InitialActivity extends AppCompatActivity{
    private GridView controllerGrid;
    private ArrayList<Integer> sampleSet;
    private ArrayList<String> commandSet;
    private TabLayout tabLayout;
    private ViewPager mPager;
    private fragmentPagerAdapter mPagerAdapter;
    private SimplePatch pureData;
    public int activeSlot=66;

    private RecyclerView panelSlotView;
    private PanelSlotViewAdapter mAdapter;

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
    }

    private void initComponent(){
        try {
            if(pureData==null)
                pureData = new SimplePatch(this);
            else
                System.out.println("ERR USS");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Fragment
        mPagerAdapter = new fragmentPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new FingerConfigFragment(), "FINGER");
        mPagerAdapter.addFragment(new ArmConfigFragment(), "ARM");
        mPager.setAdapter(mPagerAdapter);

        //Tab initialization
        tabLayout.setupWithViewPager(mPager);

        //Grid view initialization
        sampleSet = new ArrayList<>();
        commandSet = new ArrayList<>();
        commandSet.add("percussion");
        commandSet.add("bass");
        for(int i =
            0; i<2;i++){
            sampleSet.add(i);
        }

        controllerGrid.setAdapter(new CustomGridViewAdapter(this, sampleSet));
        controllerGrid.setOnItemLongClickListener(new MyTouchListener());
        controllerGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PdBase.sendFloat("playS", 1f);
                PdBase.sendBang(commandSet.get(i));
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

    @Override
    protected void onResume() {
        super.onResume();
        pureData.startAudio();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pureData.stopAudio();
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
