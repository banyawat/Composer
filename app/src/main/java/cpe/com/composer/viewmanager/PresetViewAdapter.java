package cpe.com.composer.viewmanager;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cpe.com.composer.R;

public class PresetViewAdapter extends RecyclerView.Adapter<PresetViewAdapter.MyViewHolder> {
    ArrayList<String> presetList = new ArrayList<>();

    public PresetViewAdapter(ArrayList<String> presetList){
        this.presetList = presetList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_preset_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.titleTextView.setText(presetList.get(position));
    }

    @Override
    public int getItemCount() {
        return presetList.size();
    }

    public void addPreset(String title){
        presetList.add(title);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;

        private MyViewHolder(View v) {
            super(v);
            this.titleTextView = (TextView) v.findViewById(R.id.presetTitleText);
        }
    }
}
