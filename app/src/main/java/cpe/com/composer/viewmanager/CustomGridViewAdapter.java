package cpe.com.composer.viewmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cpe.com.composer.R;

public class CustomGridViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Integer> fxSequence = new ArrayList<>();

    public CustomGridViewAdapter(Context context, ArrayList<Integer> fxSequence){
        this.context = context;
        this.fxSequence = fxSequence;
    }

    @Override
    public int getCount() {
        return fxSequence.size();
    }

    @Override
    public Object getItem(int i) {
        return fxSequence.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;

        if(view == null){
            gridView = new View(context);
            gridView = inflater.inflate(R.layout.row_fx_grid, null);

            TextView seqView = (TextView) gridView.findViewById(R.id.fxNameText);
            seqView.setText(String.valueOf(fxSequence.get(i)));
        }
        else {
            gridView = (View) view;
        }

        return gridView;
    }
}
