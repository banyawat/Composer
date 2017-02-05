package cpe.com.composer;


import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;
import cpe.com.composer.datamanager.ComposerMovement;

public class ArmSetupFragment extends Fragment {
    private View thisView;
    private boolean SIDE = false;

    private ArrayList<ImageView> viewList = new ArrayList<>();
    private ArrayList<Integer> viewIdList = new ArrayList<>();
    private InitialActivity parentActivity;

    private Drawable normalShape;
    private CircleButton removeGestureSlotButton;

    private int draggedPosition=-1;

    public ArmSetupFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_arm_config, container, false);

        parentActivity = (InitialActivity) getActivity();
        normalShape = ContextCompat.getDrawable(parentActivity, R.drawable.ic_panorama_fish_eye);


        initGui();
        initComponent();

        return thisView;
    }

    private void initGui(){
        viewList.add((ImageView)thisView.findViewById(R.id.gestureSlot01));
        viewList.add((ImageView)thisView.findViewById(R.id.gestureSlot02));
        viewList.add((ImageView)thisView.findViewById(R.id.gestureSlot03));
        viewList.add((ImageView)thisView.findViewById(R.id.gestureSlot04));
        viewList.add((ImageView)thisView.findViewById(R.id.gestureSlot05));
        removeGestureSlotButton = (CircleButton) thisView.findViewById(R.id.gestureSlotRemoveButton);
    }

    private void initComponent(){
        for(ImageView imageView : viewList){
            viewIdList.add(imageView.getId());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), String.valueOf(parentActivity.getActiveMovement().getGesture(viewIdList.indexOf(view.getId()))), Toast.LENGTH_SHORT).show();
                }
            });
            imageView.setOnDragListener(new OnGestureDragListener());
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    draggedPosition = viewIdList.indexOf(view.getId());
                    if(draggedPosition!=-1)
                    if(parentActivity.getActiveMovement().getGesture(draggedPosition)!=-1) {
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
        removeGestureSlotButton.setOnDragListener(new OnRemoveGestureDragListener());
        if(parentActivity.isParameterPassed){
            refreshDrawable();
        }
    }

    public void setInstrumentID(int keyID, int instrumentID){
        parentActivity.getActiveMovement().setGestureId(viewIdList.indexOf(keyID), instrumentID);
    }

    public void refreshDrawable(){
        for(int i=0;i<5;i++){
            int id = parentActivity.getActiveMovement().getGesture(i);
            Log.d("ID", "ID: " + id);
            if(id==-1){
                viewList.get(i).setImageDrawable(normalShape);
            }
            else{
                viewList.get(i).setImageDrawable(ContextCompat.getDrawable(parentActivity.getApplicationContext(), parentActivity.getTrackTypeById(id)));
            }
        }
    }

    public void addPanel(){
        parentActivity.composerMovements.add(new ComposerMovement());
        refreshDrawable();
    }

    private class OnGestureDragListener implements View.OnDragListener {
        private Drawable droppedDrawable;
        private boolean inside=false;
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public boolean onDrag(View v, DragEvent event) {
            Log.d("MODE2", "asdasd");
            if(draggedPosition==-1&&parentActivity.getMode()==2){
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
                        int id = parentActivity.getActiveMovement().getFingerValue(SIDE, viewIdList.indexOf(v.getId()));
                        inside = false;
                        if (id == -1) {
                            imageComponent.setImageDrawable(normalShape);
                        } else {
                            imageComponent.setImageDrawable(ContextCompat.getDrawable(parentActivity, parentActivity.getTrackTypeById(id)));
                        }
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        if (inside) {
                            setInstrumentID(v.getId(), parentActivity.activeDraggedId);
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

    private class OnRemoveGestureDragListener implements View.OnDragListener {
        private boolean inside=false;
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    inside = true;
                    v.setPressed(true);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    inside = false;
                    v.setPressed(false);
                    draggedPosition=-1;
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if(inside) {
                        if(draggedPosition!=-1)
                            parentActivity.getActiveMovement().removeGestureValue(draggedPosition);
                        refreshDrawable();
                        inside=false;
                        draggedPosition=-1;
                    }
                    v.setPressed(false);
                    break;
                default:
                    break;
            }
            return true;
        }
    }

}
