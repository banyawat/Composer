package cpe.com.composer.viewmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cpe.com.composer.R;
import cpe.com.composer.soundengine.ComposerMidi;

public class ComposerGridViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Integer> instrumentID = new ArrayList<>();
    private ArrayList<String> instrumentTitle = new ArrayList<>();

    public ComposerGridViewAdapter(Context context, ArrayList<Integer> InstrumentID, ArrayList<String> instrumentTitle){
        this.context = context;
        this.instrumentID = InstrumentID;
        this.instrumentTitle = instrumentTitle;
    }

    public ComposerGridViewAdapter(Context context, ArrayList<ComposerMidi> tracks){
        this.context = context;
        for(int i=0;i<tracks.size();i++){
            this.instrumentID.add(tracks.get(i).getId());
            this.instrumentTitle.add(tracks.get(i).getTitle());
        }
    }

    public ComposerGridViewAdapter(Context context){
        this.context = context;
    }

    public void addInstrument(int id, String title){
        instrumentID.add(id);
        instrumentTitle.add(title);
    }

    public void setData(ArrayList<ComposerMidi> tracks){
        instrumentID = new ArrayList<>();
        instrumentTitle = new ArrayList<>();
        for(int i=0;i<tracks.size();i++){
            this.instrumentID.add(tracks.get(i).getId());
            this.instrumentTitle.add(tracks.get(i).getTitle());
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
            seqView.setText(String.valueOf(instrumentTitle.get(i)));
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
