package com.example.firstapp.Graphic;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapp.R;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
{

    private List<ModelData> data;
    private Context context;

    public RecyclerViewAdapter(List<ModelData> data, Context context) {
        this.data = data;
        this.context = context;
    }

    // facciamo l'inflate (gonfiaggio) lo riportiamo sul ViewHolder -> grazie al quale andrà a richiamare i vari componenti
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.graphic_layout_row, parent, false);
        return new ViewHolder(v);
    }

    //impostare gli oggetti presi dalla lista popolata da classi "model"
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ModelData user = data.get(position);
        holder.text.setText(user.getName());
        holder.series=user.getSeries();
        GraphView graph= holder.graph;
        final SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy HH:mm");
        graph.addSeries(holder.series);
        //setto la data come valore da mostrare
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
            public String formatLabel(double value,boolean isValueX){
                   if(isValueX){
                       return sdf.format(new Date((long) value));
                   }
                    return super.formatLabel(value,isValueX);
                }
        });
        graph.getGridLabelRenderer().setNumHorizontalLabels(2);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(true);
        graph.getGridLabelRenderer().setHumanRounding(false);
        graph.getViewport().setScalable(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);

        graph.getViewport().setScrollableY(false);
        //azione da fare quando tocco sul cardview
        holder.touch_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   Toast.makeText(context, /*message*/"Position: " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //restituire la dimensione della lista
    @Override
    public int getItemCount() {
        return data.size();
    }

    //definiamo il ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView text;
        private LineGraphSeries<DataPoint> series;
        private GraphView graph;
        private RelativeLayout touch_layout;

        public ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.titleText);
            graph = itemView.findViewById(R.id.graph);
            touch_layout = itemView.findViewById(R.id.touch_layout);
        }
    }
}

