package cpe.com.composer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import cpe.com.composer.datamanager.PresetDatabase;
import cpe.com.composer.viewmanager.PresetViewAdapter;
import cpe.com.composer.viewmanager.RecyclerTouchListener;

public class PresetActivity extends AppCompatActivity {
    private RecyclerView presetRecyclerView;
    private PresetViewAdapter presetViewAdapter;
    private SQLiteDatabase mDb;
    private PresetDatabase mHelper;
    private Cursor mCursor;

    private ArrayList<String> TitleArray = new ArrayList<>();
    private ArrayList<String> NoteArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset);
        initGui();
        initDatabase();
        initPreset();
    }

    private void initGui(){
        presetRecyclerView = (RecyclerView) findViewById(R.id.savedPresetRecyclerView);
    }

    private void initDatabase(){
        mHelper = new PresetDatabase(this);
        mDb = mHelper.getWritableDatabase();
        mDb.needUpgrade(0);
        mCursor = mDb.rawQuery("SELECT " + PresetDatabase.COL_TITLE + "," + PresetDatabase.COL_NOTE + " FROM " + PresetDatabase.TABLE_NAME, null);
        Toast.makeText(this, "Database Download Complete", Toast.LENGTH_SHORT).show();

        mCursor.moveToFirst();
        while ( !mCursor.isAfterLast() ){
            TitleArray.add(mCursor.getString(mCursor.getColumnIndex(PresetDatabase.COL_TITLE)));
            NoteArray.add(mCursor.getString(mCursor.getColumnIndex(PresetDatabase.COL_NOTE)));
            mCursor.moveToNext();
        }
    }

    private void initPreset(){
        LinearLayoutManager presetLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        presetRecyclerView.setLayoutManager(presetLayout);
        presetViewAdapter = new PresetViewAdapter(TitleArray);
        presetRecyclerView.setAdapter(presetViewAdapter);
        presetRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, presetRecyclerView, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getApplicationContext(), NoteArray.get(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    public void onPause(){
        super.onPause();
        mHelper.close();
        mDb.close();
    }
}
