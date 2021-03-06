package cpe.com.composer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import cpe.com.composer.datamanager.ComposerDatabase;
import cpe.com.composer.datamanager.ComposerJSON;
import cpe.com.composer.datamanager.ComposerMovement;
import cpe.com.composer.datamanager.ComposerParam;
import cpe.com.composer.viewmanager.MovementPagerAdapter;
import cpe.com.composer.viewmanager.PanelViewAdapter;
import cpe.com.composer.viewmanager.PresetViewAdapter;
import cpe.com.composer.viewmanager.RecyclerTouchListener;

public class PreviewActivity extends AppCompatActivity {
    private ImageButton presetSubmitButton;
    private RecyclerView presetRecyclerView;
    private RecyclerView panelSlotView;
    private ProgressDialog pDialog;
    private TabLayout tabLayout;
    private ViewPager mPager;

    private PresetViewAdapter presetViewAdapter;

    private MovementPagerAdapter movementPagerAdapter;
    private PanelViewAdapter panelViewAdapter;

    private ArrayList<ArrayList<ComposerMovement>> presetList = new ArrayList<>();
    public int activeSlotPanel=0;
    public int activePreset=0;

    private ArrayList<String> titleArray = new ArrayList<>();
    public ArrayList<Integer> idImageType = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset);

        initGui();
        initDatabase();
        presetSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle extras = new Bundle();
                extras.putString(ComposerParam.BUNDLE_KEY, new ComposerJSON(presetList.get(activePreset)).getJSONString());
                startActivity(new Intent(PreviewActivity.this, SetupActivity.class).putExtras(extras));
            }
        });
    }

    private void initGui(){
        presetRecyclerView = (RecyclerView) findViewById(R.id.savedPresetRecyclerView);
        tabLayout = (TabLayout) findViewById(R.id.presetTabLayout);
        mPager = (ViewPager) findViewById(R.id.presetViewPager);
        panelSlotView = (RecyclerView) findViewById(R.id.peformPanelSlot);
        presetSubmitButton = (ImageButton) findViewById(R.id.presetSubmitButton);
    }

    private void initDatabase(){
        ComposerDatabase mHelper = new ComposerDatabase(this);
        SQLiteDatabase mDb = mHelper.getWritableDatabase();
        Bundle extras = getIntent().getExtras();
        Cursor mCursor;
        int selectDatabase = extras.getInt(ComposerParam.MENU_BUNDLE_KEY);
        if(selectDatabase==0) {
            mDb.needUpgrade(0);
            mCursor = mDb.rawQuery("SELECT " + ComposerDatabase.COL_TITLE + "," + ComposerDatabase.COL_DETAIL + " FROM " + ComposerDatabase.PRESET_TABLE, null);
            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                presetList.add(new ComposerJSON().getComposerArray(mCursor.getString(mCursor.getColumnIndex(ComposerDatabase.COL_DETAIL))));
                titleArray.add(mCursor.getString(mCursor.getColumnIndex(ComposerDatabase.COL_TITLE)));
                mCursor.moveToNext();
            }
            mCursor.close();
            initPanelSlot();
            initPresetList();
            initPresetPreviewZone();
        }
        else{
            new PresetLoadHttp().execute();
        }
        mCursor = mDb.rawQuery("SELECT "+ ComposerDatabase.COL_CHANNEL + "," + ComposerDatabase.COL_PROGRAM + "," + ComposerDatabase.COL_MODE + " FROM " + ComposerDatabase.TRACK_TABLE, null);
        mCursor.moveToFirst();
        while (!mCursor.isAfterLast()) {
            int channel = mCursor.getInt(mCursor.getColumnIndex(ComposerDatabase.COL_CHANNEL));
            if(channel==9){
                idImageType.add(ComposerParam.INSTRUMENT_MAP.get(-1));
            }
            else{
                int mode = mCursor.getInt(mCursor.getColumnIndex(ComposerDatabase.COL_MODE));
                if(mode==0){
                    int program = mCursor.getInt(mCursor.getColumnIndex(ComposerDatabase.COL_PROGRAM));
                    idImageType.add(ComposerParam.INSTRUMENT_MAP.get(program));
                }
                else if(mode==1||mode==2){
                    idImageType.add(ComposerParam.INSTRUMENT_MAP.get(-2));
                }
                else if(mode==3){
                    idImageType.add(ComposerParam.INSTRUMENT_MAP.get(-3));
                }
            }
            mCursor.moveToNext();
        }
        mDb.close();
        mHelper.close();
    }

    public int getImageByTypeId(int id){
        return idImageType.get(id-1);
    }

    private void initPresetList(){
        LinearLayoutManager presetLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        presetRecyclerView.setLayoutManager(presetLayout);
        presetViewAdapter = new PresetViewAdapter(titleArray);
        presetRecyclerView.setAdapter(presetViewAdapter);
        presetRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, presetRecyclerView, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                updateActivePreset(position);
                Log.d("DEVEV", new ComposerJSON(presetList.get(position)).getJSONString());
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    private void initPresetPreviewZone(){
        movementPagerAdapter = new MovementPagerAdapter(getSupportFragmentManager());
        movementPagerAdapter.addFragment(new PreviewFingerFragment(), "Finger");
        movementPagerAdapter.addFragment(new PreviewArmFragment(), "Gesture");
        mPager.setAdapter(movementPagerAdapter);
        tabLayout.setupWithViewPager(mPager);
    }

    private void initPanelSlot(){
        LinearLayoutManager panelSlotLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        panelSlotView.setLayoutManager(panelSlotLayout);
        panelViewAdapter = new PanelViewAdapter();
        for(int i=0;i<presetList.get(activePreset).size();i++)
            panelViewAdapter.addPanel();
        panelSlotView.setAdapter(panelViewAdapter);
        panelSlotView.addOnItemTouchListener(new RecyclerTouchListener(this, panelSlotView, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                activeSlotPanel=position;
                Log.d("DETAIL", "Current preset: " + activePreset + ", Current Slot: " + activeSlotPanel);
                ((PreviewFingerFragment)movementPagerAdapter.getItem(0)).refreshDrawable();
                ((PreviewArmFragment)movementPagerAdapter.getItem(1)).refreshDrawable();
                panelViewAdapter.setActiveSlot(position);
                panelViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

    }

    public ArrayList<ComposerMovement> getActivePreset(){
        return presetList.get(activePreset);
    }

    private void updateActivePreset(int position){
        activePreset=position;
        activeSlotPanel=0;
        ((PreviewFingerFragment)movementPagerAdapter.getItem(0)).setActiveSlotPanel();
        ((PreviewArmFragment)movementPagerAdapter.getItem(1)).refreshDrawable();
        ArrayList<String> temp = new ArrayList<>();
        for(int i=0;i<presetList.get(activePreset).size();i++) {
            temp.add("0");
        }
        panelViewAdapter.setActiveSlot(activeSlotPanel);
        panelViewAdapter.notifyDataSetChanged();
        panelViewAdapter.setPanel(temp);
        presetViewAdapter.setActivePreset(position);
        presetViewAdapter.notifyDataSetChanged();
        movementPagerAdapter.notifyDataSetChanged();
    }

    class PresetLoadHttp extends AsyncTask<Void, Void, Void> {
        String result;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(PreviewActivity.this);
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("http://composer.pe.hu/getdat.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                result = readInputStreamToString(connection);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voids){
            pDialog.dismiss();
            try {
                JSONArray array = new JSONArray(result);
                for(int i=0;i<array.length();i++){
                    titleArray.add(array.getJSONObject(i).getString(ComposerDatabase.COL_TITLE));
                    presetList.add(new ComposerJSON().getComposerArray(array.getJSONObject(i).getString(ComposerDatabase.COL_DETAIL)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            initPresetList();
            initPresetPreviewZone();
            initPanelSlot();
        }

        private String readInputStreamToString(HttpURLConnection connection) {
            String result = null;
            StringBuilder sb = new StringBuilder();
            InputStream is = null;

            try {
                is = new BufferedInputStream(connection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                result = sb.toString();
            }
            catch (Exception e) {
                Log.i("TAG", "Error reading InputStream");
                result = null;
            }
            finally {
                if (is != null) {
                    try {
                        is.close();
                    }
                    catch (IOException e) {
                        Log.i("TAG", "Error closing InputStream");
                    }
                }
            }
            return result;
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
}
