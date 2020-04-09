package com.example.firstapp.Graphic;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
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
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
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
    private PointsGraphSeries<DataPoint> last;

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
        last=user.getPoint();
        final LineGraphSeries<DataPoint> series=holder.series;
        final GraphView graph= holder.graph;
        series.setDrawDataPoints(true);
        series.setDrawBackground(true);
        series.setBackgroundColor(Color.LTGRAY);
        series.setDataPointsRadius(8);
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

                /*
               if(last!=null) last.clearReference(graph);
               DataPoint[] data=new DataPoint[1];
               data[0]=new DataPoint(dataPoint.getX(), dataPoint.getY());
               dataPoint.
                LineGraphSeries<DataPoint> serie1=new LineGraphSeries<>(data);
                serie1.setColor(Color.BLUE);


                graph.addSeries(serie1);
                //graph.removeSeries(last);
*/
            }
        });
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        Date xas=new Date((long) graph.getViewport().getMinX(true));
        Date yas=new Date((long) graph.getViewport().getMaxX(true));
        holder.start.setText(sdf.format(xas));
        holder.end.setText(sdf.format(yas));
        Double max=Math.round(series.getHighestValueY()*100.0)/100.0;
        Double min=Math.round(series.getLowestValueY()*100.0)/100.0;
        holder.max.setText(String.valueOf(max));
        holder.min.setText(String.valueOf(min));
        holder.avg.setText(String.valueOf(user.getMedia()));
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getViewport().setOnXAxisBoundsChangedListener(new Viewport.OnXAxisBoundsChangedListener() {
            @Override
            public void onXAxisBoundsChanged(double minX, double maxX, Reason reason) {
                graph.getGridLabelRenderer().invalidate(false,false);
                Date xas=new Date((long) minX);
                Date yas=new Date((long) maxX);
                holder.start.setText(sdf.format(xas));
                holder.end.setText(sdf.format(yas));

                //faccio la scanzione di tutti i punti compresi tra minX e maxX per individuare minimo,massimo e media
                Iterator<DataPoint> massimo=series.getValues(minX,maxX);
                Double val=massimo.next().getY();
                Double min=val;
                Double max=val;
                Double med=0.0;
                int conta=1;
                Double somma=0.0;
                while (massimo.hasNext()){
                    conta++;
                    val=massimo.next().getY();
                    if(val>max) max=val;
                    if(val<min) min=val;
                    somma=somma+val;
                }
                med=Math.round((somma/conta)*100.0)/100.0;
                max=Math.round(max*100.0)/100.0;
                min=Math.round(min*100.0)/100.0;
                holder.max.setText(String.valueOf(max));
                holder.min.setText(String.valueOf(min));
                holder.avg.setText(String.valueOf(med));
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
        private TextView min;
        private TextView max;
        private TextView avg;
        private TextView end;

        public ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.titleText);
            graph = itemView.findViewById(R.id.graph);
            touch_layout = itemView.findViewById(R.id.touch_layout);
            start=itemView.findViewById(R.id.textViewstart);
            end=itemView.findViewById(R.id.textViewend);
            min=itemView.findViewById(R.id.textViewMin);
            max=itemView.findViewById(R.id.textViewMax);
            avg=itemView.findViewById(R.id.textViewMed);
        }
    }
}

