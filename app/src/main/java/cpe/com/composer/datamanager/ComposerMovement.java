package cpe.com.composer.datamanager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

public class ComposerMovement {
    private Integer[] leftFinger = {-1, -1, -1, -1 ,-1};
    private Integer[] rightFinger = {-1, -1, -1, -1 ,-1};
    private Integer[] gesture = {-1, -1, -1, -1, -1};

    public ComposerMovement(){
        for(int i=0;i<5;i++){
            leftFinger[i]=-1;
            rightFinger[i]=-1;
        }
    }

    public ComposerMovement(JSONArray leftFinger, JSONArray rightFinger, JSONArray gesture){
        for(int i=0;i<5;i++){
            try {
                this.leftFinger[i] = leftFinger.getInt(i);
                this.rightFinger[i] = rightFinger.getInt(i);
                this.gesture[i] = gesture.getInt(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public int getFingerValue(boolean side, int index){
        if(!side)
            return leftFinger[index];
        else
            return rightFinger[index];
    }

    public int getLeftFingerIndexById(int id){
        for(int i=0;i<5;i++){
            if(leftFinger[i]==id)
                return i;
        }
        return -1;
    }

    public ArrayList<Integer> getFingerValue(int movementKey){
        if(movementKey==0){
            return new ArrayList<>(Arrays.asList(leftFinger));
        }
        else if(movementKey==1){
            return new ArrayList<>(Arrays.asList(rightFinger));
        }
        else{
            return new ArrayList<>(Arrays.asList(gesture));
        }
    }

    public void removeFingerValue(boolean side, int index){
        if(!side)
            leftFinger[index]=-1;
        else
            rightFinger[index]=-1;
    }

    public void removeGestureValue(int index){
        gesture[index]=-1;
    }

    public void setHandId(boolean side,int index, int instrumentID){
        if(!side){
            leftFinger[index]=instrumentID;
        }
        else{
            rightFinger[index]=instrumentID;
        }
    }

    public void setGestureId(int index, int instrumentID){
        gesture[index] = instrumentID;
    }

    public int getGesture(int i) {
        return gesture[i];
    }

    public void mirror(){
        for(int i = 0; i < leftFinger.length / 2; i++){
            int temp = leftFinger[i];
            int temp2 = rightFinger[i];
            leftFinger[i] = leftFinger[leftFinger.length - i - 1];
            rightFinger[i] = rightFinger[rightFinger.length - i - 1];
            leftFinger[leftFinger.length - i - 1] = temp;
            rightFinger[rightFinger.length - i - 1] = temp2;
        }
    }
}
