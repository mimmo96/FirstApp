package com.example.firstapp.Graphic;


import android.content.Context;
import android.util.Log;
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
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        Log.d("Graphic/Recyclerview", user.getName());
        final LineGraphSeries<DataPoint> series=holder.series;
        final GraphView graph= holder.graph;
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(5);
        final SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy HH:mm");
        graph.addSeries(holder.series);


        //setto la data come valore da mostrare
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
            public String formatLabel(double value,boolean isValueX){
                   if(isValueX){
                       return sdf.format(new Date((long) value));
                   }
                    else return super.formatLabel(value,isValueX);
                }
        });

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Date dat=new Date((long) dataPoint.getX());
                Toast.makeText(context,"x: " + sdf.format(dat) +"\ny: "+dataPoint.getY(),Toast.LENGTH_SHORT).show();
            }
        });
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        Date xas=new Date((long) graph.getViewport().getMinX(true));
        Date yas=new Date((long) graph.getViewport().getMaxX(true));
        holder.start.setText(sdf.format(xas));
        holder.end.setText(sdf.format(yas));
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getViewport().setOnXAxisBoundsChangedListener(new Viewport.OnXAxisBoundsChangedListener() {
            @Override
            public void onXAxisBoundsChanged(double minX, double maxX, Reason reason) {
                graph.getGridLabelRenderer().invalidate(false,false);
                Date xas=new Date((long) minX);
                Date yas=new Date((long) maxX);
                holder.start.setText(sdf.format(xas));
                holder.end.setText(sdf.format(yas));
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
        private TextView start;
        private TextView end;

        public ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.titleText);
            graph = itemView.findViewById(R.id.graph);
            touch_layout = itemView.findViewById(R.id.touch_layout);
            start=itemView.findViewById(R.id.textViewstart);
            end=itemView.findViewById(R.id.textViewend);
        }
    }
}

