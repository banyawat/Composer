package cpe.com.composer;


import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import cpe.com.composer.datamanager.ComposerMovement;

public class ArmSetupFragment extends Fragment {
    private View thisView;
    private boolean SIDE = false;

    private ArrayList<ImageView> viewList = new ArrayList<>();
    private ArrayList<Integer> viewIdList = new ArrayList<>();
    private InitialActivity parentActivity;

    private Drawable enterShape;
    private Drawable normalShape;

    public ArmSetupFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_arm_config, container, false);

        parentActivity = (InitialActivity) getActivity();
        enterShape = getActivity().getResources().getDrawable(R.drawable.ic_music_note);
        normalShape = getActivity().getResources().getDrawable(R.drawable.ic_favorite);


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
    }

    private void initComponent(){
        for(ImageView imageView: viewList){
            viewIdList.add(imageView.getId());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), String.valueOf(parentActivity.composerMovements.get(parentActivity.activeSlotPanel).getGesture(viewIdList.indexOf(view.getId()))), Toast.LENGTH_SHORT).show();
                }
            });
            imageView.setOnDragListener(new OnGestureDragListener());
        }
    }

    public void setInstrumentID(int keyID, int instrumentID){
        parentActivity.composerMovements.get(parentActivity.activeSlotPanel).setGestureId(viewIdList.indexOf(keyID), instrumentID);
    }

    public void refreshDrawable(){
        for(int i=0;i<5;i++){
            if(parentActivity.composerMovements.get(parentActivity.activeSlotPanel).getGesture(i)==-1){
                viewList.get(i).setImageDrawable(normalShape);
            }
            else{
                viewList.get(i).setImageDrawable(enterShape);
            }
        }
    }

    public void addPanel(){
        parentActivity.composerMovements.add(new ComposerMovement());
        refreshDrawable();
    }

    private class OnGestureDragListener implements View.OnDragListener {
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
                    if(parentActivity.composerMovements.get(parentActivity.activeSlotPanel).getFingerValue(SIDE, viewIdList.indexOf(v.getId()))==-1) {
                        imageComponent.setImageDrawable(normalShape);
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if(inside) {
                        setInstrumentID(v.getId(), ((InitialActivity)getActivity()).activeDraggedId);
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
