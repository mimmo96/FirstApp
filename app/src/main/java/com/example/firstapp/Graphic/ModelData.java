package com.example.firstapp.Graphic;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/*
 * Progetto: svilluppo App Android per Tirocinio interno
 *
 * Dipartimento di Informatica Università di Pisa
 *
 * Autore:Domenico Profumo matricola 533695
 * Si dichiara che il programma è in ogni sua parte, opera originale dell'autore
 *
 */

public class ModelData
{
    private String name;
    private LineGraphSeries<DataPoint> series;

    public ModelData() {}

    public ModelData(String name, LineGraphSeries<DataPoint> series) {
        this.name = name;
        this.series = series;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LineGraphSeries<DataPoint> getSeries() {
        return series;
    }

    public void setSeries(LineGraphSeries<DataPoint> series) {
        this.series = series;
    }
}
