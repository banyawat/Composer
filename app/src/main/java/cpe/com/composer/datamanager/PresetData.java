package cpe.com.composer.datamanager;

import java.util.ArrayList;

public class PresetData {
    ArrayList<ComposerMovement> presetList = new ArrayList<>();
    public PresetData(){

    }

    public int getCount(){
        return presetList.size();
    }

    public void add(ComposerMovement composerMovementArray){
        presetList.add(composerMovementArray);
    }

    public ComposerMovement get(int index){
        return presetList.get(index);
    }
}
