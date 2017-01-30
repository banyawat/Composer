package cpe.com.composer.viewmanager;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cpe.com.composer.R;

public class PresetViewAdapter extends RecyclerView.Adapter<PresetViewAdapter.MyViewHolder> {
    private int activePreset=0;
    private ArrayList<String> presetTitle = new ArrayList<>();

    public PresetViewAdapter(ArrayList<String> presetTitle){
        this.presetTitle = presetTitle;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_preset_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.titleTextView.setText(presetTitle.get(position));
        if(position==this.activePreset)
            holder.setActive();
        else
            holder.setInActive();
    }

    public void setActivePreset(int i){
        activePreset=i;
    }

    @Override
    public int getItemCount() {
        return presetTitle.size();
    }

    public void addPreset(String title){
        presetTitle.add(title);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private LinearLayout linearLayout;

        private MyViewHolder(View v) {
            super(v);
            this.titleTextView = (TextView) v.findViewById(R.id.presetTitleText);
            this.linearLayout = (LinearLayout) v.findViewById(R.id.presetViewBackground);
            this.linearLayout.setBackgroundColor(v.getResources().getColor(R.color.menu_button));
        }

        public void setActive(){
            this.linearLayout.setBackgroundColor(super.itemView.getResources().getColor(R.color.menu_button));
        }

        public void setInActive(){
            this.linearLayout.setBackgroundColor(0x00000000);
        }
    }
}
