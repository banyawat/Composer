package cpe.com.composer.datamanager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ComposerMovement {
    private int[] leftFinger = new int[5];
    private int[] rightFinger = new int[5];
    private ArrayList<Integer> gesture = new ArrayList<>();

    public ComposerMovement(JSONArray leftFinger, JSONArray rightFinger, JSONArray gesture){
        for(int i=0;i<5;i++){
            try {
                this.leftFinger[i] = leftFinger.getInt(i);
                this.rightFinger[i] = rightFinger.getInt(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        for(int i=0;i<gesture.length();i++){
            try {
                this.gesture.add(gesture.getInt(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public int getLeftFinger(int i){
        return leftFinger[i];
    }

    public int getRightFinger(int i){
        return rightFinger[i];
    }

    public ArrayList<Integer> getGesture() {
        return gesture;
    }
}
