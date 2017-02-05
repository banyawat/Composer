package cpe.com.composer;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;

public class SetupFingerFragment extends Fragment {
    private SetupActivity parentActivity;
    private View thisView;
    private ImageView handImageView;
    private CircleButton swapSideButton;
    private CircleButton fingerSlotRemoveButton;

    private static final float fingerPositionBiasLeft[][] = {{0.1f,0.45f},{0.3f,0.12f},{0.54f,0.05f},{0.73f,0.1f},{0.89f,0.23f}};
    private static final float fingerPositionBiasRight[][] = {{0.09f,0.25f},{0.25f,0.11f},{0.42f,0.05f},{0.66f,0.14f},{0.88f,0.45f}};
    private ArrayList<ImageView> fingerViews = new ArrayList<>();
    private ArrayList<Integer> fingerViewsId = new ArrayList<>();
    private Drawable normalShape;

    private int draggedToRemovePos =-1;

    public SetupFingerFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_finger_set, container, false);
        normalShape = ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.ic_panorama_fish_eye);
        parentActivity = (SetupActivity) getActivity();

        initGui();
        initComponent();
        return thisView;
    }

    private void initGui(){
        fingerViews.add((ImageView)thisView.findViewById(R.id.controllerImage1));
        fingerViews.add((ImageView)thisView.findViewById(R.id.controllerImage2));
        fingerViews.add((ImageView)thisView.findViewById(R.id.controllerImage3));
        fingerViews.add((ImageView)thisView.findViewById(R.id.controllerImage4));
        fingerViews.add((ImageView)thisView.findViewById(R.id.controllerImage5));
        for(ImageView imageView: fingerViews){
            fingerViewsId.add(imageView.getId());
        }
        swapSideButton = (CircleButton) thisView.findViewById(R.id.swapSideButton1);
        handImageView = (ImageView) thisView.findViewById(R.id.leftHandImageView);
        fingerSlotRemoveButton = (CircleButton) thisView.findViewById(R.id.fingerSlotRemove);
    }

    private void initComponent(){
        if(parentActivity.isParameterPassed){
            refreshDrawable();
        }
        swapSideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentActivity.swapSide();
                if(!parentActivity.getSide()) //Get hand side from object
                {
                    handImageView.setImageResource(R.drawable.left_hand);
                    swapSideButton.setImageResource(R.drawable.ic_keyboard_arrow_left);
                    ((SetupActivity)getActivity()).setGridViewMode(0);
                }
                else {
                    handImageView.setImageResource(R.drawable.right_hand);
                    swapSideButton.setImageResource(R.drawable.ic_keyboard_arrow_right);
                    ((SetupActivity)getActivity()).setGridViewMode(1);
                }
                refreshDrawable();
            }
        });
        fingerSlotRemoveButton.setOnDragListener(new FingerSlotRemoveListener());
        for(ImageView imageView: fingerViews){
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("click", "on id " + parentActivity.getActiveMovement().getFingerValue(parentActivity.getSide(), fingerViewsId.indexOf(view.getId())));
                }
            });
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    draggedToRemovePos = fingerViewsId.indexOf(view.getId());
                    if(parentActivity.getActiveMovement().getFingerValue(parentActivity.getSide(), draggedToRemovePos)!=-1) {
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
            imageView.setOnDragListener(new FingerInstrumentDragListener());
        }
    }

    public void addPanel(){
        swapSideButton.setImageResource(R.drawable.ic_keyboard_arrow_left);
        handImageView.setImageResource(R.drawable.left_hand);
        parentActivity.setMode(0);
        refreshDrawable();
    }

    public void setPanel(){
        swapSideButton.setImageResource(R.drawable.ic_keyboard_arrow_left);
        handImageView.setImageResource(R.drawable.left_hand);
        parentActivity.setMode(0);
        refreshDrawable();
    }

    public void refreshDrawable(){
        for (int i = 0; i < 5; i++) {
            int id = parentActivity.getActiveMovement().getFingerValue(parentActivity.getSide(), i);
            if(id==-1)
                fingerViews.get(i).setImageDrawable(normalShape);
            else {
                fingerViews.get(i).setImageDrawable(ContextCompat.getDrawable(parentActivity.getApplicationContext(), parentActivity.getTrackTypeById(id))); //asdasdas
            }
        }

        if(parentActivity.getSide()) {
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

    private void removeDraggedId(){
        if(draggedToRemovePos!=-1){
            parentActivity.getActiveMovement().removeFingerValue(parentActivity.getSide(), draggedToRemovePos);
        }
        refreshDrawable();
    }

    public void setInstrumentID(int keyID, int instrumentID){
        parentActivity.getActiveMovement().setHandId(parentActivity.getSide(), fingerViewsId.indexOf(keyID), instrumentID);
    }

    private class FingerInstrumentDragListener implements View.OnDragListener {
        private Drawable droppedDrawable;
        private boolean inside=false;
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public boolean onDrag(View v, DragEvent event) {
            if(draggedToRemovePos ==-1&&parentActivity.getMode()!=2) {
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
                        int id = parentActivity.getActiveMovement().getFingerValue(parentActivity.getSide(), fingerViewsId.indexOf(v.getId()));
                        inside = false;
                        if (id == -1) {
                            imageComponent.setImageDrawable(normalShape);
                        }
                        else{
                            imageComponent.setImageDrawable(ContextCompat.getDrawable(parentActivity, parentActivity.getTrackTypeById(id)));
                        }
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        if (inside) {
                            setInstrumentID(v.getId(), parentActivity.activeDraggedId);
                            inside = false;
                        }
                        draggedToRemovePos=-1;
                        break;
                    default:
                        break;
                }
            }
            return true;
        }
    }

    private class FingerSlotRemoveListener implements View.OnDragListener {
        private boolean inside = false;
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
                    draggedToRemovePos =-1;
                    v.setPressed(false);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if (inside) {
                        removeDraggedId();
                        inside = false;
                    }
                    draggedToRemovePos =-1;
                    v.setPressed(false);
                    break;
                default:
                    break;
            }
            return true;
        }
    }
}
