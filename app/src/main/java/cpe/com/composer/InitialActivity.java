package cpe.com.composer;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import cpe.com.composer.datamanager.ComposerDatabase;
import cpe.com.composer.datamanager.ComposerJSON;
import cpe.com.composer.datamanager.ComposerMovement;
import cpe.com.composer.datamanager.ComposerParam;
import cpe.com.composer.soundengine.ComposerGesture;
import cpe.com.composer.soundengine.ComposerLeftHand;
import cpe.com.composer.soundengine.ComposerMusicEngine;
import cpe.com.composer.soundengine.ComposerRightHand;
import cpe.com.composer.viewmanager.ComposerGridViewAdapter;
import cpe.com.composer.viewmanager.MovementPagerAdapter;
import cpe.com.composer.viewmanager.PanelViewAdapter;
import cpe.com.composer.viewmanager.PresetViewAdapter;
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
    private ImageButton savePresetButton;
    private RecyclerView panelSlotView;
    private PanelViewAdapter panelViewAdapter;
    private Dialog saveDialog;

    private ComposerMusicEngine musicEngine;

    private int mode=0;

    public ArrayList<ComposerMovement> composerMovements = new ArrayList<>();
    private ArrayList<Integer> idImageType = new ArrayList<>();

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
            Log.d("DEVPER", composerMovements.toString());
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
        savePresetButton = (ImageButton) findViewById(R.id.savePresetButton);
    }

    /**
     * Each event assigned to button
     */
    private void initComponent(){
        if(!isParameterPassed)
            composerMovements.add(new ComposerMovement());

        //music engine initiation;
        musicEngine = new ComposerMusicEngine(this, PATH);
        musicEngine.loadDatabase();

        for(ComposerLeftHand leftHand: musicEngine.getTrackList()){
            if(leftHand.getChannel()==9)
                idImageType.add(ComposerParam.INSTRUMENT_MAP.get(-1));
            else {
                idImageType.add(ComposerParam.INSTRUMENT_MAP.get(leftHand.getProgram()));
            }

        }
        for(ComposerRightHand rightHand: musicEngine.getChordList()){
            idImageType.add(-2);
        }
        for(ComposerGesture gesture: musicEngine.getTempoList()){
            idImageType.add(-3);
        }

        //Fragment
        mPagerAdapter = new MovementPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new FingerSetupFragment(), "FINGER");
        mPagerAdapter.addFragment(new ArmSetupFragment(), "ARM");
        mPager.setAdapter(mPagerAdapter);

        //Tab initialization
        tabLayout.setupWithViewPager(mPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        if (!((FingerSetupFragment)mPagerAdapter.getItem(0)).getSide())
                            setGridViewMode(0);
                        else
                            setGridViewMode(1);
                        break;
                    case 1:
                        setGridViewMode(2);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        adapter = new ComposerGridViewAdapter(this, musicEngine.getTrackList());
        controllerGrid.setAdapter(adapter);
        controllerGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.startDragAndDrop(null, shadowBuilder, view, 0);
                }
                else {
                    view.startDrag(null, shadowBuilder, view, 0);
                }
                activeDraggedId = (int) adapter.getItemId(i);
                return false;
            }
        });
        controllerGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                switch(mode){
                    case 0:
                        musicEngine.playID((int) adapter.getItemId(position));
                        break;
                    case 1:
                        musicEngine.doTranspose((int) adapter.getItemId(position));
                        break;
                    case 2:
                        musicEngine.setBpm((int) adapter.getItemId(position));
                        break;
                    default: break;
                }
            }
        });

        initSaveButton();

        goPerformButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle extras = new Bundle();
                extras.putString(ComposerParam.BUNDLE_KEY, new ComposerJSON(composerMovements).getJSONString());
                startActivity(new Intent(InitialActivity.this, PerformActivity.class).putExtras(extras));
            }
        });
    }

    private void initSaveButton(){
        savePresetButton.setOnClickListener(new View.OnClickListener() {
            private int currentOverwritePos=-1;
            private ArrayList<String> titleArray;
            private RecyclerView dialogPresetView;
            private PresetViewAdapter presetViewAdapter;
            private EditText savedNameEditText;
            private SQLiteDatabase mDb;


            @Override
            public void onClick(View view) {
                titleArray = new ArrayList<>();
                saveDialog = new Dialog(InitialActivity.this);
                saveDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                saveDialog.setContentView(R.layout.save_dialog);
                saveDialog.setCancelable(true);
                dialogPresetView = (RecyclerView) saveDialog.findViewById(R.id.dialogPresetList);
                savedNameEditText = (EditText) saveDialog.findViewById(R.id.presetNameDialogText);

                /**
                 * Load database:
                 */
                final ComposerDatabase mHelper = new ComposerDatabase(InitialActivity.this);
                mDb = mHelper.getWritableDatabase();
                mDb.needUpgrade(0);
                Cursor mCursor = mDb.rawQuery("SELECT _id," + ComposerDatabase.COL_TITLE + " FROM " + ComposerDatabase.PRESET_TABLE, null);
                mCursor.moveToFirst();
                while (!mCursor.isAfterLast()) {
                    titleArray.add(mCursor.getString(mCursor.getColumnIndex(ComposerDatabase.COL_TITLE)));
                    mCursor.moveToNext();
                }

                titleArray.add("+ New preset....");

                /**
                 * Database sector
                 */
                LinearLayoutManager presetLayout = new LinearLayoutManager(InitialActivity.this, LinearLayoutManager.VERTICAL, false);
                dialogPresetView.setLayoutManager(presetLayout);
                presetViewAdapter = new PresetViewAdapter(titleArray);
                currentOverwritePos = titleArray.size()-1;
                presetViewAdapter.setActivePreset(titleArray.size()-1);
                dialogPresetView.setAdapter(presetViewAdapter);
                dialogPresetView.addOnItemTouchListener(new RecyclerTouchListener(saveDialog.getContext(), dialogPresetView, new RecyclerTouchListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(position==titleArray.size()-1){
                            //Save as new preset..
                            savedNameEditText.setText("");
                            savedNameEditText.setEnabled(true);
                        }
                        else {
                            //Overwrite...
                            savedNameEditText.setText(titleArray.get(position));
                            savedNameEditText.setEnabled(false);
                        }
                        currentOverwritePos = position;
                        presetViewAdapter.setActivePreset(position);
                        presetViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {}
                }));

                Button dialogSaveButton = (Button) saveDialog.findViewById(R.id.dialogSaveButton);
                dialogSaveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String jsonInDetails = new ComposerJSON(composerMovements).getJSONString();
                        if(currentOverwritePos==titleArray.size()-1){
                            if(!mDb.isOpen()){
                                mDb.isOpen();
                            }
                            mDb.execSQL("INSERT INTO " + ComposerDatabase.PRESET_TABLE + " VALUES(null, '" + savedNameEditText.getText() + "', '" + jsonInDetails + "');");
                        }
                        else{
                            if(!mDb.isOpen()){
                                mDb.isOpen();
                            }
                            mDb.execSQL("UPDATE " + ComposerDatabase.PRESET_TABLE + " SET " + ComposerDatabase.COL_DETAIL + " = '" + jsonInDetails + "' WHERE _id=" +
                                    (currentOverwritePos+1) + ";");
                        }
                        Toast.makeText(getApplicationContext(), "Preset saved..!", Toast.LENGTH_SHORT).show();
                        mDb.close();
                        mHelper.close();
                        saveDialog.dismiss();
                    }
                });
                Button dialogCancelButton = (Button) saveDialog.findViewById(R.id.dialogCancelButton);
                dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDb.close();
                        mHelper.close();
                        saveDialog.dismiss();
                    }
                });
                saveDialog.show();
            }
        });
    }

    /**
     * Change gridview information between instrument track and melody controller
     * @param mode - to indentify hand side and change it to track or melody
     */
    public void setGridViewMode(int mode){
        switch (mode){
            case 0:
                adapter = new ComposerGridViewAdapter(this, musicEngine.getTrackList());
                break;
            case 1:
                adapter = new ComposerGridViewAdapter(this);
                ArrayList<ComposerRightHand> composerRightHands = musicEngine.getChordList();
                for(ComposerRightHand temp: composerRightHands){
                    adapter.addInstrument(temp.getId(), temp.getTitle(), -2);
                }
                break;
            case 2:
                adapter = new ComposerGridViewAdapter(this);
                ArrayList<ComposerGesture> composerGestures = musicEngine.getTempoList();
                for(ComposerGesture temp: composerGestures){
                    adapter.addInstrument(temp.getId(), temp.getTitle(), -3);
                }
                break;
            default: break;
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
                //((FingerSetupFragment)mPagerAdapter.getItem(0)).setPanel(position);
                //((ArmSetupFragment)mPagerAdapter.getItem(1)).refreshDrawable();
                activeSlotPanel=position;
                if(mode==2){
                    ((ArmSetupFragment)mPagerAdapter.getItem(1)).refreshDrawable();
                }
                else{
                    ((FingerSetupFragment)mPagerAdapter.getItem(0)).setPanel();
                }
                panelViewAdapter.setActiveSlot(position);
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
                    ((FingerSetupFragment)mPagerAdapter.getItem(0)).refreshDrawable();
                    ((ArmSetupFragment)mPagerAdapter.getItem(1)).refreshDrawable();
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
                    activeSlotPanel = panelViewAdapter.removePanel(viewHolder.getAdapterPosition());
                    composerMovements.remove(viewHolder.getAdapterPosition());
                    panelViewAdapter.notifyDataSetChanged();
                    ((FingerSetupFragment)mPagerAdapter.getItem(0)).refreshDrawable();
                    ((ArmSetupFragment)mPagerAdapter.getItem(1)).refreshDrawable();
                }else {
                    panelViewAdapter.notifyItemChanged(0);
                    Toast.makeText(getApplicationContext(), "No panel to remove", Toast.LENGTH_SHORT).show();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(panelSlotView);
    }

    public ComposerMovement getActiveMovement(){
        return composerMovements.get(activeSlotPanel);
    }

    public int getTrackTypeById(int id){
        return idImageType.get(id);
    }

    public int getMode(){
        return mode;
    }
}
