package cpe.com.composer.viewmanager;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cpe.com.composer.R;

public class PanelSlotViewAdapter extends RecyclerView.Adapter<PanelSlotViewAdapter.MyViewHolder> {
    private int activeSlot=0;
    private ArrayList<String> panelListString = new ArrayList<>();
    private ArrayList<Drawable> panelListDrawable = new ArrayList<>();
    View itemView;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_panel, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        viewHolder.panelSlotName.setText(panelListString.get(position));
        if(position==getItemCount()-1) {
            viewHolder.panelSlotImageView.setImageResource(R.drawable.ic_action_add);
        }
        else {
            if(position==this.activeSlot)
                viewHolder.setActive();
            else
                viewHolder.setInActive();
            viewHolder.panelSlotImageView.setImageResource(R.drawable.ic_settings);
        }
    }

    public void setActiveSlot(int index){
        activeSlot=index;
    }

    @Override
    public int getItemCount() {
        return panelListString.size();
    }

    public void addPanel(String panelName, Drawable panelImage){
        panelListDrawable.add(0, panelImage);
        panelListString.add(0, panelName);
    }

    public void addPanel(String panelName){
        if(getItemCount()!=0)
            panelListString.add(getItemCount()-1, panelName);
        else
            panelListString.add(panelName);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView panelSlotImageView;
        private TextView panelSlotName;
        private LinearLayout linearLayout;

        private MyViewHolder(View v) {
            super(v);
            this.panelSlotImageView = (ImageView) v.findViewById(R.id.panelSlotImageView);
            this.panelSlotName = (TextView) v.findViewById(R.id.panelSlotName);
            this.linearLayout = (LinearLayout) itemView.findViewById(R.id.panelSlotBackground);
        }

        public void setActive(){
            this.linearLayout.setBackgroundColor(itemView.getResources().getColor(R.color.menu_button));
        }

        public void setInActive(){
            this.linearLayout.setBackgroundColor(0x00000000);
        }
    }
}
