package cpe.com.composer;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;
import cpe.com.composer.viewmanager.ComposerFingerSetupTool;

public class FingerSetupFragment extends Fragment {
    private View thisView;
    private ComposerFingerSetupTool myHand;
    private ImageView handImageView;
    private CircleButton swapSideButton;
    private CircleButton fingerSlotRemoveButton;

    public FingerSetupFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_finger_set, container, false);

        initGui();
        initComponent();
        return thisView;
    }

    private void initGui(){
        ArrayList<ImageView> viewList = new ArrayList<>();
        viewList.add((ImageView)thisView.findViewById(R.id.controllerImage1));
        viewList.add((ImageView)thisView.findViewById(R.id.controllerImage2));
        viewList.add((ImageView)thisView.findViewById(R.id.controllerImage3));
        viewList.add((ImageView)thisView.findViewById(R.id.controllerImage4));
        viewList.add((ImageView)thisView.findViewById(R.id.controllerImage5));
        swapSideButton = (CircleButton) thisView.findViewById(R.id.swapSideButton1);
        handImageView = (ImageView) thisView.findViewById(R.id.leftHandImageView);
        fingerSlotRemoveButton = (CircleButton) thisView.findViewById(R.id.fingerSlotRemove);
        myHand = new ComposerFingerSetupTool(viewList, (InitialActivity) getActivity());
    }

    private void initComponent(){
        swapSideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myHand.swapSide();
                if(!myHand.getSide()) //Get hand side from object
                {
                    handImageView.setImageResource(R.drawable.left_hand);
                    swapSideButton.setImageResource(R.drawable.ic_keyboard_arrow_left);
                    ((InitialActivity)getActivity()).setGridViewMode(0);
                }
                else {
                    handImageView.setImageResource(R.drawable.right_hand);
                    swapSideButton.setImageResource(R.drawable.ic_keyboard_arrow_right);
                    ((InitialActivity)getActivity()).setGridViewMode(1);
                }
                myHand.refreshDrawable();
            }
        });
        fingerSlotRemoveButton.setOnDragListener(new FingerSlotRemoveListener());
    }

    public boolean getSide(){
        return myHand.getSide();
    }

    public void addPanel(){
        swapSideButton.setImageResource(R.drawable.ic_keyboard_arrow_left);
        handImageView.setImageResource(R.drawable.left_hand);
        myHand.addSlotPanel();
        myHand.refreshDrawable();
    }

    public void setPanel(){
        swapSideButton.setImageResource(R.drawable.ic_keyboard_arrow_left);
        handImageView.setImageResource(R.drawable.left_hand);
        myHand.setActiveSlotPanel();
        myHand.refreshDrawable();
    }

    public void refreshDrawable(){
        myHand.refreshDrawable();
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
                    myHand.resetDraggedPosition();
                    v.setPressed(false);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if (inside) {
                        myHand.removeDraggedId();
                        inside = false;
                        myHand.resetDraggedPosition();
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
