package com.example.GreenApp.Channel;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.GreenApp.AppDatabase;
import com.example.firstapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/*
 * Progetto: svilluppo App Android per Tirocinio interno
 *
 * Dipartimento di Informatica Università di Pisa
 *
 * Autore:Domenico Profumo matricola 533695
 * Si dichiara che il programma è in ogni sua parte, opera originale dell'autore
 *
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyVieworder> {
    private static int pos=0;
    private List<Channel> channel;
    private Context context;
    private AppDatabase db;

    /**
     * metodo costruttore
     * @param channel: channel da inserire
     * @param context: contesto
     * @param db: database utilizzato
     */
    public RecyclerViewAdapter(List<Channel> channel, Context context,AppDatabase db) {
        this.context=context;
        this.channel=channel;
        this.db=db;
    }

    /**
     * faccio l'inflate (gonfiaggio) lo riportiamo sul ViewHolder -> grazie al quale andrà a richiamare i vari componenti
     */
    @Override
    public MyVieworder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row,parent,false);
        return new MyVieworder(v);
    }

    /**
     * imposta gli oggetti presi dalla lista popolata da classi "model"
     */
    @Override
    public void onBindViewHolder(final MyVieworder holder, final int position) {
        final Channel chan=channel.get(position);
        holder.testo.setText("Channel: " + chan.getLett_id());
        if(channel.get(position).getNotification()) holder.notifiche.setText("NOTIFICHE ON");
        else holder.notifiche.setText("NOTIFICHE OFF");
        pos=ChannelActivity.getposition();
        if(pos==position) ChannelActivity.sendPrefer(holder.star,context,pos);

        holder.bottone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChannelActivity.sendObjcet(v,context,position);
            }
        });
        //funzione eseguita guando premo sul pulsante "stella"
        holder.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChannelActivity.sendPrefer(v,context,position);

            }
        });

    }

    /**
     *
     * @return numero di elementi presenti
     */
    @Override
    public int getItemCount() {
        return channel.size();
    }

    /**
     *  definiamo il ViewHolder (si occuperà della gestione dei singoli view)
     */
    public static class MyVieworder extends RecyclerView.ViewHolder{

        private TextView testo;
        private TextView notifiche;
        private FloatingActionButton bottone;
        private RelativeLayout touch_layout;
        private ImageButton star;

        /**
         * metodo costruttore
         * @param itemView puntatore al View
         */
        public MyVieworder(View itemView) {
            super(itemView);
            testo=itemView.findViewById(R.id.titleText);
            bottone=itemView.findViewById(R.id.Button2);
            star=itemView.findViewById(R.id.favorite);
            touch_layout=itemView.findViewById(R.id.touch_layout);
            notifiche=itemView.findViewById(R.id.textNotification);
        }
    }
}