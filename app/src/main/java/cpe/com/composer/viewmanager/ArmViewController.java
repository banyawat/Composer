package cpe.com.composer.viewmanager;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

public class ArmViewController extends Fragment {
    private List<ImageView> gestureSlotView = new ArrayList<>();
    private List<TextView> gestureTextView = new ArrayList<>();

    private Drawable originalImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.arm_layout, container, false);

        gestureSlotView.add((ImageView) view.findViewById(R.id.gestureSlot01));
        gestureSlotView.add((ImageView) view.findViewById(R.id.gestureSlot02));
        gestureSlotView.add((ImageView) view.findViewById(R.id.gestureSlot03));
        gestureSlotView.add((ImageView) view.findViewById(R.id.gestureSlot04));
        gestureSlotView.add((ImageView) view.findViewById(R.id.gestureSlot05));

        gestureTextView.add((TextView) view.findViewById(R.id.gestureTextView01));
        gestureTextView.add((TextView) view.findViewById(R.id.gestureTextView02));
        gestureTextView.add((TextView) view.findViewById(R.id.gestureTextView03));
        gestureTextView.add((TextView) view.findViewById(R.id.gestureTextView04));
        gestureTextView.add((TextView) view.findViewById(R.id.gestureTextView05));

        originalImage = ContextCompat.getDrawable(getActivity(), R.drawable.ic_panorama_fish_eye);

        return view;
    }

    public void setDrawable(int pos, Drawable img){
        if(img!=null)
            gestureSlotView.get(pos).setImageDrawable(img);
        else
            gestureSlotView.get(pos).setImageDrawable(originalImage);
    }

    public void setDrawableAndText(int pos, Drawable img, String txt){
        gestureTextView.get(pos).setText(txt);
        if(img!=null)
            gestureSlotView.get(pos).setImageDrawable(img);
        else
            gestureSlotView.get(pos).setImageDrawable(originalImage);
    }

    public void setActive(int i, boolean active){
        if(active)
            gestureSlotView.get(i).setBackgroundColor(Color.GRAY);
        else
            gestureSlotView.get(i).setBackgroundColor(0x00000000);
    }

    public void clearActive(){
        for(ImageView component: gestureSlotView){
            component.setBackgroundColor(0x00000000);
        }
    }
}
