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
import cpe.com.composer.datamanager.FingerData;

public class HandSlotController {
    private InitialActivity ancestorActivity;
    private ArrayList<ImageView> fingerViews = new ArrayList<>();
    private ArrayList<FingerData> fingerSlotPanel; // slot Panel
    private boolean SIDE=false; //SIDE; false=left, true=right
    private int activeSlotPanel=0;
    private static final float biasLeft[][] = {{0.1f,0.45f},{0.3f,0.12f},{0.54f,0.05f},{0.73f,0.1f},{0.89f,0.23f}};
    private static final float biasRight[][] = {{0.09f,0.25f},{0.25f,0.11f},{0.42f,0.05f},{0.66f,0.14f},{0.88f,0.45f}};

    private final Drawable enterShape;
    private final Drawable normalShape;

    public HandSlotController(ArrayList<ImageView> ImageViewID, InitialActivity ancestorActivity){
        this.ancestorActivity = ancestorActivity;
        this.fingerViews = ImageViewID;

        fingerSlotPanel = new ArrayList<>();
        fingerSlotPanel.add(new FingerData());

        enterShape = ancestorActivity.getResources().getDrawable(R.drawable.ic_music_note);
        normalShape = ancestorActivity.getResources().getDrawable(R.drawable.ic_favorite);
        fingerSlotPanel.get(0).init(fingerViews);

        for(int i=0;i<5;i++) {
            setOnClickListener(i);
        }
        setOnDragListener();
    }

    private void setOnClickListener(final int index){
        fingerViews.get(index).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ancestorActivity.getApplicationContext(), String.valueOf(fingerSlotPanel.get(activeSlotPanel).getFingerValue(SIDE, view.getId())), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOnDragListener(){
        for(ImageView imageView: fingerViews){
            imageView.setOnDragListener(new FingerInstrumentDragListener());
        }
    }

    public void setInstrumentID(int keyID, int instrumentID){
        fingerSlotPanel.get(activeSlotPanel).setHandID(SIDE, keyID, instrumentID);
    }

    public void swapSide(){
        SIDE = !SIDE;
        refreshDrawable();
    }

    public void refreshDrawable(){
        for (int i = 0; i < 5; i++) {
            if(fingerSlotPanel.get(activeSlotPanel).getFingerValue(SIDE, fingerViews.get(i).getId())==-1)
                fingerViews.get(i).setImageDrawable(normalShape);
            else
                fingerViews.get(i).setImageDrawable(enterShape);
        }

        if(SIDE) {
            for(int i=0;i<5;i++){
                ConstraintLayout.LayoutParams constParams = (ConstraintLayout.LayoutParams) fingerViews.get(i).getLayoutParams();
                constParams.horizontalBias = biasRight[i][0];
                constParams.verticalBias = biasRight[i][1];
                fingerViews.get(i).setLayoutParams(constParams);
            }
        }
        else{
            for(int i=0;i<5;i++){
                ConstraintLayout.LayoutParams constParams = (ConstraintLayout.LayoutParams) fingerViews.get(i).getLayoutParams();
                constParams.horizontalBias = biasLeft[i][0];
                constParams.verticalBias = biasLeft[i][1];
                fingerViews.get(i).setLayoutParams(constParams);
            }
        }
    }

    public void addSlotPanel(){
        FingerData newSlFing = new FingerData();
        newSlFing.init(fingerViews);
        fingerSlotPanel.add(newSlFing);
        activeSlotPanel=fingerSlotPanel.size()-1;
        SIDE=false;
        refreshDrawable();
    }

    public void setActiveSlotPanel(int position){
        this.activeSlotPanel=position;
        SIDE=false;
        refreshDrawable();
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
                    if(fingerSlotPanel.get(activeSlotPanel).getFingerValue(SIDE, v.getId())==-1) {
                        imageComponent.setImageDrawable(normalShape);
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if(inside) {
                        setInstrumentID(v.getId(), ancestorActivity.activeSlot);
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
