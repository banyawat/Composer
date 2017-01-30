package cpe.com.composer;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class ArmPreviewFragment extends Fragment {
    private View thisView;

    private ArrayList<ImageView> viewList = new ArrayList<>();
    private ArrayList<Integer> viewIdList = new ArrayList<>();
    private PresetActivity parentActivity;

    private Drawable enterShape;
    private Drawable normalShape;

    public ArmPreviewFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_arm_config, container, false);

        parentActivity = (PresetActivity) getActivity();
        enterShape = getActivity().getResources().getDrawable(R.drawable.ic_music_note);
        normalShape = getActivity().getResources().getDrawable(R.drawable.ic_panorama_fish_eye);


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
                    Toast.makeText(getContext(), String.valueOf(parentActivity.getActivePreset().get(parentActivity.activeSlotPanel).getGesture(viewIdList.indexOf(view.getId()))), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void setInstrumentID(int keyID, int instrumentID){
        parentActivity.getActivePreset().get(parentActivity.activeSlotPanel).setGestureId(viewIdList.indexOf(keyID), instrumentID);
    }

    public void refreshDrawable(){
        for(int i=0;i<5;i++){
            if(parentActivity.getActivePreset().get(parentActivity.activeSlotPanel).getGesture(i)==-1)
                viewList.get(i).setImageDrawable(normalShape);
            else
                viewList.get(i).setImageDrawable(enterShape);
        }
    }
}
