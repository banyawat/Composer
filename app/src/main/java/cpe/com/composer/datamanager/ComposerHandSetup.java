package cpe.com.composer.datamanager;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class ComposerHandSetup {
    private HashMap<Integer, Integer> gestureInstruId = new HashMap<>();
    private HashMap<Integer, Integer> leftFingerInstruId = new HashMap<>();
    private HashMap<Integer, Integer> rightFingerInstruId = new HashMap<>();

    public void init(ArrayList<ImageView> idList){
        for(int i=0;i<5;i++) {
            leftFingerInstruId.put(idList.get(i).getId(), -1);
            rightFingerInstruId.put(idList.get(i).getId(), -1);
        }
    }

    public void setHandID(boolean side,int keyID, int instrumentID){
        if(!side){
            leftFingerInstruId.remove(keyID);
            leftFingerInstruId.put(keyID, instrumentID);
        }
        else{
            rightFingerInstruId.remove(keyID);
            rightFingerInstruId.put(keyID, instrumentID);
        }
    }

    public int getFingerValue(boolean side, int keyID){
        if(!side)
            return leftFingerInstruId.get(keyID);
        else
            return rightFingerInstruId.get(keyID);
    }

    public ArrayList<Integer> getFingerValue(int movement){
        if(movement == 0)
            return new ArrayList<>(leftFingerInstruId.values());
        else if(movement == 1)
            return new ArrayList<>(rightFingerInstruId.values());
        else
            return new ArrayList<>(gestureInstruId.values());

    }
}
