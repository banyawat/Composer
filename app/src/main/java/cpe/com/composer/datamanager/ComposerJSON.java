package cpe.com.composer.datamanager;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ComposerJSON {
    private JSONObject presetObj = new JSONObject();

    public ComposerJSON(){

    }

    /**
     * get finger value 0 -> left finger
     * 1 -> right finger
     * else -> gesture
     */
    public ComposerJSON(ArrayList<ComposerMovement> mainData){
        for(int i=0;i<mainData.size();i++){
            JSONObject tempArr1 = new JSONObject();
            for(int j=0;j< ComposerParam.INPUT_NUM;j++){
                JSONArray tempArr2 = new JSONArray(mainData.get(i).getFingerValue(j));
                try {
                    tempArr1.put(String.valueOf(j), tempArr2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                presetObj.put(String.valueOf(i), tempArr1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Log.d("Translator", presetObj.toString());
    }

    public String getJSONString(){
        return presetObj.toString();
    }

    public void getComposerArray(String jsonString){
        ArrayList<ComposerMovement> composerMovements = new ArrayList<>();
        try {
            JSONObject jsonO1 = new JSONObject(jsonString);
            Iterator<?> keys = jsonO1.keys();
            while(keys.hasNext()){
                String key = (String)keys.next();
                if(jsonO1.get(key) instanceof JSONObject){//enter preset's level
                    JSONObject jsonO2 = new JSONObject(jsonO1.getString(key));
                    JSONArray leftFinger = new JSONArray(jsonO2.getString("0"));
                    JSONArray rightFinger = new JSONArray(jsonO2.getString("1"));
                    JSONArray gesture = new JSONArray(jsonO2.getString("2"));

                    composerMovements.add(new ComposerMovement(leftFinger, rightFinger, gesture));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("ENCODED", String.valueOf(composerMovements.get(0).getLeftFinger(0)));
    }
}
