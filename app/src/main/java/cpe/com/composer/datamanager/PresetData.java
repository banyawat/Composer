package cpe.com.composer.datamanager;

import java.util.ArrayList;

public class PresetData {
    ArrayList<FingerData> presetList = new ArrayList<>();
    public PresetData(){

    }

    public int getCount(){
        return presetList.size();
    }

    public void add(FingerData fingerData){
        presetList.add(fingerData);
    }

    public FingerData get(int index){
        return presetList.get(index);
    }
}
