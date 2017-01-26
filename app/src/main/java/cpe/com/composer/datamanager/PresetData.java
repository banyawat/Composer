package cpe.com.composer.datamanager;

import java.util.ArrayList;

public class PresetData {
    ArrayList<ComposerMovementArray> presetList = new ArrayList<>();
    public PresetData(){

    }

    public int getCount(){
        return presetList.size();
    }

    public void add(ComposerMovementArray composerMovementArray){
        presetList.add(composerMovementArray);
    }

    public ComposerMovementArray get(int index){
        return presetList.get(index);
    }
}
