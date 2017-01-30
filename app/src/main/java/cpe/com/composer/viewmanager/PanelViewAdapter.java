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

public class PanelViewAdapter extends RecyclerView.Adapter<PanelViewAdapter.MyViewHolder> {
    private int activeSlot=0;
    private ArrayList<String> panelListString = new ArrayList<>();
    private ArrayList<Drawable> panelListDrawable = new ArrayList<>();

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_panel, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        viewHolder.panelSlotName.setText(String.valueOf(position+1));
        if(position==this.activeSlot)
            viewHolder.setActive();
        else
            viewHolder.setInActive();
        viewHolder.panelSlotImageView.setImageResource(R.drawable.ic_settings);
    }

    public void setPanel(ArrayList<String> panelListString){
        this.panelListString = panelListString;
    }

    public void setActiveSlot(int index){
        activeSlot=index;
    }

    @Override
    public int getItemCount() {
        return panelListString.size();
    }

    public int removePanel(int i){
        panelListString.remove(i);
        setActiveSlot(panelListString.size()-1);
        return panelListString.size()-1;
    }

    public void addPanel(){
            panelListString.add(String.valueOf(panelListString.size()+1));
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView panelSlotImageView;
        private TextView panelSlotName;
        private LinearLayout linearLayout;

        private MyViewHolder(View v) {
            super(v);
            this.panelSlotImageView = (ImageView) v.findViewById(R.id.panelSlotImageView);
            this.panelSlotName = (TextView) v.findViewById(R.id.panelSlotName);
            this.linearLayout = (LinearLayout) v.findViewById(R.id.panelSlotBackground);
        }

        public void setActive(){
            this.linearLayout.setBackgroundColor(super.itemView.getResources().getColor(R.color.menu_button));
        }

        public void setInActive(){
            this.linearLayout.setBackgroundColor(0x00000000);
        }
    }
}
