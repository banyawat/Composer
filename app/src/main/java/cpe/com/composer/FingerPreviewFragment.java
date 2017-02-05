package cpe.com.composer;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;

public class FingerPreviewFragment extends Fragment {
    private PresetActivity parentActivity;
    private View thisView;
    private ImageView handImageView;
    private CircleButton swapSideButton;

    private ArrayList<ImageView> fingerViews = new ArrayList<>();
    private boolean SIDE=false; //SIDE; false=left, true=right
    private static final float fingerPositionBiasLeft[][] = {{0.1f,0.45f},{0.3f,0.12f},{0.54f,0.05f},{0.73f,0.1f},{0.89f,0.23f}};
    private static final float fingerPositionBiasRight[][] = {{0.09f,0.25f},{0.25f,0.11f},{0.42f,0.05f},{0.66f,0.14f},{0.88f,0.45f}};

    private Drawable normalShape;
    private CircleButton removeHideButton;

    public FingerPreviewFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_finger_set, container, false);

        parentActivity = (PresetActivity) getActivity();
        normalShape = getActivity().getResources().getDrawable(R.drawable.ic_panorama_fish_eye);

        initGui();
        initComponent();
        return thisView;
    }

    private void initGui(){
        fingerViews = new ArrayList<>();
        fingerViews.add((ImageView)thisView.findViewById(R.id.controllerImage1));
        fingerViews.add((ImageView)thisView.findViewById(R.id.controllerImage2));
        fingerViews.add((ImageView)thisView.findViewById(R.id.controllerImage3));
        fingerViews.add((ImageView)thisView.findViewById(R.id.controllerImage4));
        fingerViews.add((ImageView)thisView.findViewById(R.id.controllerImage5));
        swapSideButton = (CircleButton) thisView.findViewById(R.id.swapSideButton1);
        handImageView = (ImageView) thisView.findViewById(R.id.leftHandImageView);
        removeHideButton = (CircleButton) thisView.findViewById(R.id.fingerSlotRemove);
    }

    private void initComponent(){
        refreshDrawable();
        swapSideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swapSide();
                if(!SIDE){
                    handImageView.setImageResource(R.drawable.left_hand);
                    swapSideButton.setImageResource(R.drawable.ic_keyboard_arrow_left);
                }
                else {
                    handImageView.setImageResource(R.drawable.right_hand);
                    swapSideButton.setImageResource(R.drawable.ic_keyboard_arrow_right);
                }
            }
        });
        removeHideButton.setVisibility(View.INVISIBLE);
        for(ImageView imageView: fingerViews){
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }
    }

    private void swapSide(){
        SIDE = !SIDE;
        refreshDrawable();
    }

    public void refreshDrawable(){
        for (int i = 0; i < 5; i++) {
            int id = parentActivity.getActivePreset().get(parentActivity.activeSlotPanel).getFingerValue(SIDE, i);
            if(id == -1)
                fingerViews.get(i).setImageDrawable(normalShape);
            else{
                fingerViews.get(i).setImageDrawable(ContextCompat.getDrawable(parentActivity.getApplicationContext(), parentActivity.getImageByTypeId(id)));
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

    public void setActiveSlotPanel(){
        handImageView.setImageResource(R.drawable.left_hand);
        swapSideButton.setImageResource(R.drawable.ic_keyboard_arrow_left);
        SIDE=false;
        refreshDrawable();
    }
}
