package cpe.com.composer.datamanager;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class ComposerMovementArray {
    /*private JSONObject leftFingerAssignId = new JSONObject();
    private JSONObject rightFingerAssignId = new JSONObject();*/

    private HashMap<Integer, Integer> fingerLAssignedID = new HashMap<>();
    private HashMap<Integer, Integer> fingerRAssignedID = new HashMap<>();

    public void init(ArrayList<ImageView> idList){
        for(int i=0;i<5;i++) {
            fingerLAssignedID.put(idList.get(i).getId(), -1);
            fingerRAssignedID.put(idList.get(i).getId(), -1);
        }
    }

    public void setHandID(boolean side,int keyID, int instrumentID){
        if(!side){
            fingerLAssignedID.remove(keyID);
            fingerLAssignedID.put(keyID, instrumentID);
        }
        else{
            fingerRAssignedID.remove(keyID);
            fingerRAssignedID.put(keyID, instrumentID);
        }
    }

    public int getFingerValue(boolean side, int keyID){
        if(!side)
            return fingerLAssignedID.get(keyID);
        else
            return fingerRAssignedID.get(keyID);
    }
}
