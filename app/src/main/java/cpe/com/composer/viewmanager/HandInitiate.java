package cpe.com.composer.viewmanager;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import cpe.com.composer.InitialActivity;
import cpe.com.composer.R;
import cpe.com.composer.datamanager.ComposerJSON;
import cpe.com.composer.datamanager.ComposerMovement;

public class HandInitiate {
    private InitialActivity ancestorActivity;
    private ArrayList<ImageView> fingerViews = new ArrayList<>();
    private ArrayList<Integer> fingerViewsId = new ArrayList<>();
    private ArrayList<ComposerMovement> fingerSlotPanel; // slot Panel
    private boolean SIDE=false; //SIDE; false=left, true=right
    private int activeSlotPanel=0;
    private static final float fingerPositionBiasLeft[][] = {{0.1f,0.45f},{0.3f,0.12f},{0.54f,0.05f},{0.73f,0.1f},{0.89f,0.23f}};
    private static final float fingerPositionBiasRight[][] = {{0.09f,0.25f},{0.25f,0.11f},{0.42f,0.05f},{0.66f,0.14f},{0.88f,0.45f}};

    private final Drawable enterShape;
    private final Drawable normalShape;

    public HandInitiate(ArrayList<ImageView> ImageViewID, InitialActivity ancestorActivity){
        this.ancestorActivity = ancestorActivity;
        this.fingerViews = ImageViewID;
        for(ImageView image: fingerViews){
            fingerViewsId.add(image.getId());
        }

        fingerSlotPanel = new ArrayList<>();
        fingerSlotPanel.add(new ComposerMovement());

        enterShape = ancestorActivity.getResources().getDrawable(R.drawable.ic_music_note);
        normalShape = ancestorActivity.getResources().getDrawable(R.drawable.ic_favorite);

        for(int i=0;i<5;i++) {
            setOnClickListener(i);
        }
        setOnDragListener();
    }

    private void setOnClickListener(final int index){
        fingerViews.get(index).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ancestorActivity.getApplicationContext(), String.valueOf(fingerSlotPanel.get(activeSlotPanel).getFingerValue(SIDE, fingerViewsId.indexOf(view.getId()))), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOnDragListener(){
        for(ImageView imageView: fingerViews){
            imageView.setOnDragListener(new FingerInstrumentDragListener());
        }
    }

    public void setInstrumentID(int keyID, int instrumentID){
        fingerSlotPanel.get(activeSlotPanel).setHandId(SIDE, fingerViewsId.indexOf(keyID), instrumentID);
    }

    public void swapSide(){
        SIDE = !SIDE;
        refreshDrawable();
    }

    public void refreshDrawable(){
        for (int i = 0; i < 5; i++) {
            if(fingerSlotPanel.get(activeSlotPanel).getFingerValue(SIDE, i)==-1)
                fingerViews.get(i).setImageDrawable(normalShape);
            else
                fingerViews.get(i).setImageDrawable(enterShape);
        }

        if(SIDE) {
            for(int i=0;i<5;i++){
                ConstraintLayout.LayoutParams constParams = (ConstraintLayout.LayoutParams) fingerViews.get(i).getLayoutParams();
                constParams.horizontalBias = fingerPositionBiasRight[i][0];
                constParams.verticalBias = fingerPositionBiasRight[i][1];
                fingerViews.get(i).setLayoutParams(constParams);
            }
        }
        else{
            for(int i=0;i<5;i++){
                ConstraintLayout.LayoutParams constParams = (ConstraintLayout.LayoutParams) fingerViews.get(i).getLayoutParams();
                constParams.horizontalBias = fingerPositionBiasLeft[i][0];
                constParams.verticalBias = fingerPositionBiasLeft[i][1];
                fingerViews.get(i).setLayoutParams(constParams);
            }
        }
    }

    public void addSlotPanel(){
        ComposerMovement newSlotFinger = new ComposerMovement();
        fingerSlotPanel.add(newSlotFinger);
        activeSlotPanel=fingerSlotPanel.size()-1;
        SIDE=false;
        refreshDrawable();
    }

    public void setActiveSlotPanel(int position){
        this.activeSlotPanel=position;
        SIDE=false;
        refreshDrawable();
    }

    public String getHandPrefString(){
        return new ComposerJSON(fingerSlotPanel).getJSONString();
    }

    public int getActiveSlotPanel(){
        return this.activeSlotPanel;
    }

    public boolean getSide(){
        return SIDE;
    }

    private class FingerInstrumentDragListener implements View.OnDragListener {
        private boolean inside=false;
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            ImageView imageComponent = (ImageView) v;
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    inside = true;
                    imageComponent.setImageDrawable(enterShape);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    inside = false;
                    if(fingerSlotPanel.get(activeSlotPanel).getFingerValue(SIDE, fingerViewsId.indexOf(v.getId()))==-1) {
                        imageComponent.setImageDrawable(normalShape);
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if(inside) {
                        setInstrumentID(v.getId(), ancestorActivity.activeDraggedId);
                        inside=false;
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    }
}
