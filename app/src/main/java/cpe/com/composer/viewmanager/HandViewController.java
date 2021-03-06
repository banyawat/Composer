package cpe.com.composer.viewmanager;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cpe.com.composer.R;

public class HandViewController extends Fragment {
    private static final float verticalBiasLeft[] = {0.41f, 0.13f, 0.06f, 0.1f, 0.22f};
    private static final float horizontalBiasLeft[] = {0.17f, 0.32f, 0.48f, 0.61f, 0.72f};

    private static final float verticalBiasRight[] = {0.2f, 0.1f, 0.07f, 0.12f, 0.42f};
    private static final float horizontalBiasRight[] = {0.16f, 0.27f, 0.4f, 0.57f, 0.71f};

    private Drawable leftHandImage;
    private Drawable rightHandImage;
    private Drawable oirignalImage;

    private boolean Side = false;
    private ImageView handView;
    private List<ImageView> handController = new ArrayList<>();
    private List<TextView> handTextView = new ArrayList<>();

    private OnImageLoadedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hand_layout, container, false);
        handView = (ImageView) view.findViewById(R.id.handView);
        handController.add((ImageView) view.findViewById(R.id.controllerImage01));
        handController.add((ImageView) view.findViewById(R.id.controllerImage02));
        handController.add((ImageView) view.findViewById(R.id.controllerImage03));
        handController.add((ImageView) view.findViewById(R.id.controllerImage04));
        handController.add((ImageView) view.findViewById(R.id.controllerImage05));

        handTextView.add((TextView) view.findViewById(R.id.controllerText01));
        handTextView.add((TextView) view.findViewById(R.id.controllerText02));
        handTextView.add((TextView) view.findViewById(R.id.controllerText03));
        handTextView.add((TextView) view.findViewById(R.id.controllerText04));
        handTextView.add((TextView) view.findViewById(R.id.controllerText05));

        leftHandImage = ContextCompat.getDrawable(getActivity(), R.drawable.left_hand);
        rightHandImage = ContextCompat.getDrawable(getActivity(), R.drawable.right_hand);
        oirignalImage = ContextCompat.getDrawable(getActivity(), R.drawable.ic_panorama_fish_eye);

        Bundle bundle = this.getArguments();
        if(bundle!=null){
            Side = bundle.getBoolean("bool");
        }

        view.post(new Runnable() {
            @Override
            public void run() {
                resize();
                if(listener!=null)
                    listener.OnImageLoadedListener();
            }
        });

        return view;
    }

    public void setOnImageLoadListener(OnImageLoadedListener listener){
        this.listener = listener;
    }

    public void setSide(boolean Side){
        this.Side = Side;
    }

    public void setDrawable(int pos, Drawable img){
        if(img!=null)
            handController.get(pos).setImageDrawable(img);
        else
            handController.get(pos).setImageDrawable(oirignalImage);
    }

    public void setActive(int i, boolean active){
        if(active)
            handController.get(i).setBackgroundColor(Color.GRAY);
        else
            handController.get(i).setBackgroundColor(0x00000000);
    }

    public void clearActive(){
        for(ImageView component: handController){
            component.setBackgroundColor(0x00000000);
        }
    }

    public void setDrawableAndText(int pos, Drawable img, String txt){
        handTextView.get(pos).setText(txt);
        if(img!=null)
            handController.get(pos).setImageDrawable(img);
        else
            handController.get(pos).setImageDrawable(oirignalImage);
    }


    private void resize() {
        int height = handView.getHeight();
        int width = handView.getWidth();
        if(!Side) {
            for (int i = 0; i < 5; i++) {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) handController.get(i).getLayoutParams();

                layoutParams.leftMargin = ((int) (width * horizontalBiasLeft[i]));
                layoutParams.topMargin = ((int) (height * verticalBiasLeft[i]));

                layoutParams.width = (int) (width * 0.1);
                layoutParams.height = (int) (height * 0.1);

                handController.get(i).setLayoutParams(layoutParams);
                handView.setImageDrawable(leftHandImage);
            }
        }
        else{
            for (int i = 0; i < 5; i++) {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) handController.get(i).getLayoutParams();

                layoutParams.leftMargin = ((int) (width * horizontalBiasRight[i]));
                layoutParams.topMargin = ((int) (height * verticalBiasRight[i]));

                layoutParams.width = (int) (width * 0.1);
                layoutParams.height = (int) (height * 0.1);

                handController.get(i).setLayoutParams(layoutParams);
                handView.setImageDrawable(rightHandImage);
            }
        }
    }
}
