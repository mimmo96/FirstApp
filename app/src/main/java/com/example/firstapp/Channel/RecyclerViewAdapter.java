package com.example.firstapp.Channel;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.firstapp.AppDatabase;
import com.example.firstapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyVieworder> {
    private static int pos=0;
    private List<Channel> channel;
    private Context context;
    private AppDatabase db;

    public RecyclerViewAdapter(List<Channel> channel, Context context,AppDatabase db) {
        this.context=context;
        this.channel=channel;
        this.db=db;
    }

    @Override
    public MyVieworder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row,parent,false);
        return new MyVieworder(v);
    }

    @Override
    public void onBindViewHolder(final MyVieworder holder, final int position) {
        final Channel chan=channel.get(position);
        holder.testo.setText("Channel: " + chan.getId());
        pos=ChannelActivity.getposition();
        if(pos==position) ChannelActivity.sendPrefer(holder.star,context,pos);

        holder.bottone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChannelActivity.sendObjcet(v,context,position);
            }
        });
        holder.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChannelActivity.sendPrefer(v,context,position);

            }
        });

    }

    @Override
    public int getItemCount() {
        return channel.size();
    }

    public static class MyVieworder extends RecyclerView.ViewHolder{

        private TextView testo;
        private FloatingActionButton bottone;
        private RelativeLayout touch_layout;
        private ImageButton star;

        public MyVieworder(View itemView) {
            super(itemView);
            testo=itemView.findViewById(R.id.titleText);
            bottone=itemView.findViewById(R.id.Button2);
            star=itemView.findViewById(R.id.favorite);
            touch_layout=itemView.findViewById(R.id.touch_layout);

        }
    }

}
