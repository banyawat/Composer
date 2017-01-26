package cpe.com.composer.datamanager;

import java.util.ArrayList;

public class PresetData {
    ArrayList<ComposerHandSetup> presetList = new ArrayList<>();
    public PresetData(){

    }

    public int getCount(){
        return presetList.size();
    }

    public void add(ComposerHandSetup composerMovementArray){
        presetList.add(composerMovementArray);
    }

    public ComposerHandSetup get(int index){
        return presetList.get(index);
    }
}
