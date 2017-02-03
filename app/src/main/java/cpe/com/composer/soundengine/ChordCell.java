package cpe.com.composer.soundengine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChordCell {
    private int id;
    private String title;
    private ArrayList<Integer> key = new ArrayList<>();
    private ArrayList<Integer> minor = new ArrayList<>();

    public ChordCell(int id, String title, String keyAndMinor){
        this.id = id;
        this.title = title;
        try {
            JSONObject jsonTemp1 = new JSONObject(keyAndMinor);
            JSONArray jsonArr1 = jsonTemp1.getJSONArray("note");
            JSONArray jsonArr2 = jsonTemp1.getJSONArray("minor");
            for(int i=0;i<jsonArr1.length();i++){
                key.add(jsonArr1.getInt(i));
                minor.add(jsonArr2.getInt(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ChordCell(int id, String title, int key, int minor){
        this.id = id;
        this.title = title;
        this.key.add(key);
        this.minor.add(minor);
    }

    public int getKey(int position) {
        return this.key.get(position);
    }

    public int getKey(){
        return this.key.get(0);
    }

    public void addKey(int key) {
        this.key.add(key);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getMinor(int position) {
        return minor.get(position);
    }

    public int getMinor(){
        if(this.minor.size()==1)
            return this.minor.get(0);
        else if(this.minor.size()>1)
            return 2;
        else
            return 0;
    }

    public int getMinorSize(){
        return this.minor.size();
    }

    public void addMinor(int minor) {
        this.minor.add(minor);
    }
}
