package cpe.com.composer;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

import cpe.com.composer.viewmanager.PresetViewAdapter;
import cpe.com.composer.viewmanager.RecyclerTouchListener;

public class StoreActivity extends AppCompatActivity {
    private RecyclerView presetRecyclerView;
    private PresetViewAdapter presetViewAdapter;
    private ArrayList<String> tempArray = new ArrayList<>();
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset);

        initGui();
        new PresetLoadHttp().execute();
    }

    private void initGui(){
        presetRecyclerView = (RecyclerView) findViewById(R.id.savedPresetRecyclerView);
    }

    private void initPreset(){
        tempArray.add("Test");
        LinearLayoutManager presetLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        presetRecyclerView.setLayoutManager(presetLayout);
        presetViewAdapter = new PresetViewAdapter(tempArray);
        presetRecyclerView.setAdapter(presetViewAdapter);
        presetRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, presetRecyclerView, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getApplicationContext(), "TT", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    class PresetLoadHttp extends AsyncTask<Void, Void, Void>{
        String result;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(StoreActivity.this);
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
                    tempArray.add(array.getJSONObject(i).getString("title"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            initPreset();
        }

        private String readInputStreamToString(HttpURLConnection connection) {
            String result = null;
            StringBuffer sb = new StringBuffer();
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
}
