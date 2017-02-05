package cpe.com.composer.viewmanager;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import cpe.com.composer.InitialActivity;
import cpe.com.composer.R;

public class ComposerFingerSetupTool {
    private InitialActivity ancestorActivity;
    private ArrayList<ImageView> fingerViews = new ArrayList<>();
    private ArrayList<Integer> fingerViewsId = new ArrayList<>();
    private boolean SIDE=false; //SIDE; false=left, true=right
    private static final float fingerPositionBiasLeft[][] = {{0.1f,0.45f},{0.3f,0.12f},{0.54f,0.05f},{0.73f,0.1f},{0.89f,0.23f}};
    private static final float fingerPositionBiasRight[][] = {{0.09f,0.25f},{0.25f,0.11f},{0.42f,0.05f},{0.66f,0.14f},{0.88f,0.45f}};

    private final Drawable normalShape;

    private int draggedPosition=-1;

    public ComposerFingerSetupTool(ArrayList<ImageView> ImageViewIds, InitialActivity ancestorActivity){
        this.ancestorActivity = ancestorActivity;
        this.fingerViews = ImageViewIds;
        normalShape = ContextCompat.getDrawable(ancestorActivity.getApplicationContext(), R.drawable.ic_panorama_fish_eye);

        if(ancestorActivity.isParameterPassed){
            for (ImageView image : fingerViews) {
                fingerViewsId.add(image.getId());
            }
            refreshDrawable();
        }else {
            for (ImageView image : fingerViews) {
                fingerViewsId.add(image.getId());
            }
        }

        for(int i=0;i<5;i++) {
            setViewActionListener(i);
        }
        setOnDragListener();
    }

    private void setViewActionListener(int index){
        fingerViews.get(index).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ancestorActivity.getApplicationContext(), String.valueOf(ancestorActivity.getActiveMovement().getFingerValue(SIDE, fingerViewsId.indexOf(view.getId()))), Toast.LENGTH_SHORT).show();
            }
        });
        fingerViews.get(index).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                draggedPosition = fingerViewsId.indexOf(view.getId());
                if(ancestorActivity.getActiveMovement().getFingerValue(SIDE, draggedPosition)!=-1) {
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        view.startDragAndDrop(null, shadowBuilder, view, 0);
                    }
                    else {
                        view.startDrag(null, shadowBuilder, view, 0);
                    }
                }
                return false;
            }
        });
    }

    public void removeDraggedId(){
        if(draggedPosition!=-1)
            ancestorActivity.getActiveMovement().removeFingerValue(SIDE, draggedPosition);
        refreshDrawable();
    }

    private void setOnDragListener(){
        for(ImageView imageView: fingerViews){
            imageView.setOnDragListener(new FingerInstrumentDragListener());
        }
    }

    public void setInstrumentID(int keyID, int instrumentID){
        ancestorActivity.getActiveMovement().setHandId(SIDE, fingerViewsId.indexOf(keyID), instrumentID);
    }

    public void swapSide(){
        SIDE = !SIDE;
    }

    public void refreshDrawable(){
        for (int i = 0; i < 5; i++) {
            int id = ancestorActivity.getActiveMovement().getFingerValue(SIDE, i);
            if(id==-1)
                fingerViews.get(i).setImageDrawable(normalShape);
            else {
                fingerViews.get(i).setImageDrawable(ContextCompat.getDrawable(ancestorActivity.getApplicationContext(), ancestorActivity.getTrackTypeById(id))); //asdasdas
            }
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
        ancestorActivity.activeSlotPanel= ancestorActivity.composerMovements.size()-1;
        SIDE=false;
        refreshDrawable();
    }

    public void setActiveSlotPanel(){
        SIDE=false;
    }

    public void resetDraggedPosition(){
        draggedPosition=-1;
    }

    public boolean getSide(){
        return SIDE;
    }

    private class FingerInstrumentDragListener implements View.OnDragListener {
        private Drawable droppedDrawable;
        private boolean inside=false;
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public boolean onDrag(View v, DragEvent event) {
            Log.d("EVENT", ancestorActivity.getMode() + ", " + draggedPosition);
            if(draggedPosition==-1&&ancestorActivity.getMode()!=2) {
                int action = event.getAction();
                ImageView imageComponent = (ImageView) v;
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        View draggedView = (View) event.getLocalState();
                        droppedDrawable = ((ImageView) draggedView.findViewById(R.id.musicNoteImageView)).getDrawable();
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        inside = true;
                        imageComponent.setImageDrawable(droppedDrawable);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        int id = ancestorActivity.getActiveMovement().getFingerValue(SIDE, fingerViewsId.indexOf(v.getId()));
                        inside = false;
                        if (id == -1) {
                            imageComponent.setImageDrawable(normalShape);
                        }
                        else{
                            imageComponent.setImageDrawable(ContextCompat.getDrawable(ancestorActivity, ancestorActivity.getTrackTypeById(id)));
                        }
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        if (inside) {
                            setInstrumentID(v.getId(), ancestorActivity.activeDraggedId);
                            inside = false;
                        }
                        break;
                    default:
                        break;
                }
            }
            return true;
        }
    }
}
