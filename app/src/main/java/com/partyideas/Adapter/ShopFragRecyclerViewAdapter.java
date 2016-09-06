package com.partyideas.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.partyideas.R;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by xeonyan on 6/9/2016.
 */
public class ShopFragRecyclerViewAdapter extends RecyclerView.Adapter<ShopFragRecyclerViewAdapter.ViewHolder> {

    ArrayList<ShopObject> data = new ArrayList<>();
    private ShopRecyclerViewOnClickListener listener;
    public ShopFragRecyclerViewAdapter(ArrayList<ShopObject> data ){
        this.data = data;
    }
    public void setOnClickListener(ShopRecyclerViewOnClickListener listener){
        this.listener = listener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_recycler_card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ShopObject obj = data.get(position);
        try {
            holder.titleView.setText(obj.name);
            holder.priceView.setText("$"+obj.price);
            holder.slotView.setText(obj.stock ? obj.quantity+" remaining" : "OUT OF STOCK");
            String imgsrc = obj.imgsrc.replaceFirst("^(https://)|^(http://)?(www\\.)?", "");
            Ion.with(holder.gameImg)
                    .placeholder(R.mipmap.ic_picture)
                    .load("http://images.weserv.nl/?url="+imgsrc+"&w=240&h=240&t=square&a=center");
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(obj);

                }
            });
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView titleView;
        public TextView priceView;
        public TextView slotView;
        public LinearLayout root;
        public ImageView gameImg;

        public ViewHolder(View v) {
            super(v);
            titleView = (TextView) v.findViewById(R.id.gameTitle);
            priceView = (TextView)v.findViewById(R.id.gamePrice);
            slotView = (TextView)v.findViewById(R.id.gameStock);
            root = (LinearLayout) v.findViewById(R.id.root);
            gameImg = (ImageView) v.findViewById(R.id.gameImg);
        }
    }
}
