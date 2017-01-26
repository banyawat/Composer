package cpe.com.composer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;
import cpe.com.composer.viewmanager.HandInitiate;

public class FingerSetupFragment extends Fragment {
    private InitialActivity parentActivity;
    private View thisView;
    private HandInitiate myHand;
    private ImageView handImageView;
    private CircleButton swapSideButton;

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
        parentActivity = (InitialActivity)getActivity();
        handImageView = (ImageView) thisView.findViewById(R.id.leftHandImageView);
        myHand = new HandInitiate(viewList, parentActivity);
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
                }
                else {
                    handImageView.setImageResource(R.drawable.right_hand);
                    swapSideButton.setImageResource(R.drawable.ic_keyboard_arrow_right);
                }
                ((InitialActivity)getActivity()).swapGrid(myHand.getSide());
            }
        });
    }

    public void addPanel(){
        swapSideButton.setImageResource(R.drawable.ic_keyboard_arrow_left);
        handImageView.setImageResource(R.drawable.left_hand);
        myHand.addSlotPanel();
        myHand.refreshDrawable();
    }

    public void setPanel(int position){
        swapSideButton.setImageResource(R.drawable.ic_keyboard_arrow_left);
        handImageView.setImageResource(R.drawable.left_hand);
        myHand.setActiveSlotPanel(position);
        myHand.refreshDrawable();
    }

    public String getDataHandPref(){
        return myHand.getHandPrefString();
    }
}
