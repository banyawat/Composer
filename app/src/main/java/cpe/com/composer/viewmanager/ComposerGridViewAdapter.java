package cpe.com.composer.viewmanager;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cpe.com.composer.R;
import cpe.com.composer.datamanager.ComposerParam;
import cpe.com.composer.soundengine.ComposerLeftHand;

public class ComposerGridViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Integer> instrumentID = new ArrayList<>();
    private ArrayList<String> instrumentTitle = new ArrayList<>();
    private ArrayList<Integer> instrumentImageId = new ArrayList<>();

    public ComposerGridViewAdapter(Context context, ArrayList<Integer> InstrumentID, ArrayList<String> instrumentTitle){
        this.context = context;
        this.instrumentID = InstrumentID;
        this.instrumentTitle = instrumentTitle;
    }

    public ComposerGridViewAdapter(Context context, ArrayList<ComposerLeftHand> tracks){
        this.context = context;
        for(int i=0;i<tracks.size();i++){
            this.instrumentID.add(tracks.get(i).getId());
            this.instrumentTitle.add(tracks.get(i).getTitle());
            if(tracks.get(i).getChannel()==9)
                this.instrumentImageId.add(-1);
            else{
                this.instrumentImageId.add(tracks.get(i).getProgram());
            }
        }
    }

    public ComposerGridViewAdapter(Context context){
        this.context = context;
    }

    public void addInstrument(int id, String title, int program){
        this.instrumentID.add(id);
        this.instrumentTitle.add(title);
        this.instrumentImageId.add(program);
    }

    public void setData(ArrayList<ComposerLeftHand> tracks){
        instrumentID = new ArrayList<>();
        instrumentTitle = new ArrayList<>();
        for(int i=0;i<tracks.size();i++){
            this.instrumentID.add(tracks.get(i).getId());
            this.instrumentTitle.add(tracks.get(i).getTitle());
            if(tracks.get(i).getChannel()==9)
                this.instrumentImageId.add(-1);
            else{
                this.instrumentImageId.add(tracks.get(i).getProgram());
            }
        }
    }

    @Override
    public int getCount() {
        return instrumentTitle.size();
    }

    @Override
    public Object getItem(int i) {
        return instrumentTitle.get(i);
    }

    @Override
    public long getItemId(int i) {
        return instrumentID.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;

        if(view == null){
            gridView = new View(context);
            gridView = inflater.inflate(R.layout.row_fx_grid, null);
            TextView seqView = (TextView) gridView.findViewById(R.id.fxNameText);
            ImageView imageView = (ImageView) gridView.findViewById(R.id.musicNoteImageView);
            seqView.setText(String.valueOf(instrumentTitle.get(i)));
            imageView.setImageDrawable(ContextCompat.getDrawable(context, ComposerParam.INSTRUMENT_MAP.get(instrumentImageId.get(i))));
        }
        else {
            gridView = view;
        }
        gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return false;
            }
        });

        return gridView;
    }
}
