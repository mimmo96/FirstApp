package com.example.GreenApp.Channel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/*
 * Progetto: svilluppo App Android per Tirocinio interno
 *
 * Dipartimento di Informatica Università di Pisa
 *
 * Autore:Domenico Profumo matricola 533695
 * Si dichiara che il programma è in ogni sua parte, opera originale dell'autore
 *
 */

/**
 * entity Channel contenente tutti i metodi che serviranno al database per recuperare/memorizzare i dati
 */
@Entity
public class Channel {
    //dichiaro tutti i parametri utilizzati
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "id_key")
    private String lett_id;
    private String scritt_id;

    @ColumnInfo(name = "read_key")
    private String lett_read_key;
    private String scritt_read_key;
    private String write_key;

    @ColumnInfo(name = "position_key")
    private int position=0;
    private boolean notification=false;
    private String filed1=null;private String filed2=null;private String filed3=null;
    private String filed4=null;private String filed5=null;private String filed6=null;
    private String filed7=null;private String filed8=null;
    private Double tempMin=null;private Double tempMax=null;
    private Double umidMin=null;private Double umidMax=null;
    private Double condMin=null;private Double condMax=null;
    private Double phMin=null;private Double phMax=null;
    private Double irraMin=null;private Double irraMax=null;
    private Double pesMin=null;private Double pesMax=null;
    private int tempomax=0;
    private String imagetemp=null;
    private String imageumid=null;
    private String imageph=null;
    private String imagecond=null;
    private String imageirra=null;
    private String imagepeso=null;
    private Double evapotraspirazione=null;
    private Double IrrigationDuration=null;
    private Double FlussoAcqua=null;
    private Double Leachingfactor =null;
    private int Numirra =0;
    private int lastimevalues=0;
    //ultimo valore dato server in tempo
    private Double minutes=null;

    /**
     * metodo costruttore
     * @param lett_id: id associato al channel di lettura
     * @param scritt_id:  id associato al channel di scrittura
     * @param lett_read_key: api lettura associato al channel di lettura
     * @param scritt_read_key: api scrittura associato al channel di lettura
     * @param write_key: api scrittura associato al channel di scrittura
     */
    public Channel(String lett_id,String scritt_id, String lett_read_key,String scritt_read_key,String write_key) {
        this.lett_id = lett_id;
        this.scritt_id=scritt_id;
        this.lett_read_key=lett_read_key;
        this.scritt_read_key=scritt_read_key;
        this.write_key=write_key;
    }

    /**
     *
     * @return il tempo massimo settato (notifiche)
     */
    public int getTempomax() {
        return tempomax;
    }

    /**
     *
     * @return il tempo minimo settato (notifiche)
     */
    public void setTempomax(int tempomax) {
        this.tempomax = tempomax;
    }

    /**
     *
     * @return se le notifiche sono settate ritorna true altrimenti false
     */
    public boolean getNotification() {
        return notification;
    }

    /**
     *
     * @param notification: booleano (true per settare le notifiche, false per eliminarle)
     */
    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    /**
     * recuperare il field1
     * @return nome associato al filed1
     */
    public String getFiled1() {
        return filed1;
    }

    /**impostare il field1
     * @param filed1 nome associato al filed1
     */
    public void setFiled1(String filed1) {
        this.filed1 = filed1;
    }

    /**
     * recuperare il field2
     * @return nome associato al filed2
     */
    public String getFiled2() {
        return filed2;
    }
    /**impostare il field2
     * @param filed2 nome associato al filed2
     */
    public void setFiled2(String filed2) {
        this.filed2 = filed2;
    }
    /**
     * recuperare il field3
     * @return nome associato al filed3
     */
    public String getFiled3() {
        return filed3;
    }
    /**impostare il field3
     * @param filed3 nome associato al filed3
     */
    public void setFiled3(String filed3) {
        this.filed3 = filed3;
    }

    /**
     * recuperare il field4
     * @return nome associato al filed4
     */
    public String getFiled4() {
        return filed4;
    }
    /**impostare il field4
     * @param filed4 nome associato al filed4
     */
    public void setFiled4(String filed4) {
        this.filed4 = filed4;
    }

    /**
     * recuperare il field5
     * @return nome associato al filed5
     */
    public String getFiled5() {
        return filed5;
    }
    /**impostare il field5
     * @param fild5 nome associato al filed5
     */
    public void setFiled5(String fild5) {
        this.filed5 = fild5;
    }
    /**
     * recuperare il field6
     * @return nome associato al filed6
     */
    public String getFiled6() {
        return filed6;
    }
    /**impostare il filed6
     * @param filed6 nome associato al filed6
     */
    public void setFiled6(String filed6) {
        this.filed6 = filed6;
    }
    /**
     * recuperare il field7
     * @return nome associato al filed7
     */
    public String getFiled7() {
        return filed7;
    }
    /**impostare il filed7
     * @param filed7 nome associato al filed7
     */
    public void setFiled7(String filed7) {
        this.filed7 = filed7;
    }
    /**
     * recuperare il field8
     * @return nome associato al filed8
     */
    public String getFiled8() {
        return filed8;
    }
    /**
     * impostare il filed8
     * @param filed8 nome associato al filed8
     */
    public void setFiled8(String filed8) {
        this.filed8 = filed8;
    }

    /**
     *
     * @return id associato al channel
     */
    public int getUid() {
        return uid;
    }

    /**
     *  imposta uid channel (per il database)
     * @param uid:uid del channel
     */
    public void setUid(int uid) {
        this.uid = uid;
    }

    /**
     *
     * @return chiave di scrittura
     */
    public String getWrite_key() {
        return write_key;
    }

    /**
     *
     * @return id chiave lettura
     */
    public String getLett_id() {
        return lett_id;
    }

    /**
     *  imposta id chiave di lettura
     * @param lett_id: id chiave di lettura
     */
    public void setLett_id(String lett_id) { this.lett_id = lett_id; }

    /**
     *
     * @return id chiave di scrittura
     */
    public String getScritt_id() {
        return scritt_id;
    }

    /**
     * imposta id chiave di scrittura
     * @param scritt_id: valore id chiave di scrittura
     */
    public void setScritt_id(String scritt_id) {
        this.scritt_id = scritt_id;
    }

    /**
     *
     * @return chiave di lettura
     */
    public String getLett_read_key() { return lett_read_key; }

    /**
     *imposta id chiave di lettura
     * @param lett_read_key:valore imposta id chiave di lettura
     */
    public void setLett_read_key(String lett_read_key) {
        this.lett_read_key = lett_read_key;
    }

    /**
     *
     * @return chiave di scrittura
     */
    public String getScritt_read_key() {
        return scritt_read_key;
    }

    /**
     *
     * @param scritt_read_key api lettura chiave di scrittura
     */
    public void setScritt_read_key(String scritt_read_key) { this.scritt_read_key = scritt_read_key; }

    /**
     *
     * @param write_key:valore chiave di scrittura
     */
    public void setWrite_key(String write_key) {
        this.write_key = write_key;
    }

    /**
     *
     * @return posizione associata
     */
    public int getPosition() {
        return position;
    }

    /**
     *
     * @param pos imposta la posizione
     */
    public void setPosition(int pos) {
        this.position = pos;
    }

    /**
     *
     * @return temperatura minima (notifiche)
     */
    public Double getTempMin() {
        return tempMin;
    }

    /**
     * imposta temperatura minima
     * @param tempMin:valore cifra decimale della temperatura minima
     */
    public void setTempMin(Double tempMin) {
        this.tempMin = tempMin;
    }
    /**
     *
     * @return temperatura minima (notifiche)
     */
    public Double getTempMax() {
        return tempMax;
    }

    /**
     * imposta il valore di temperatura massima
     * @param tempMax: valore temperatura massima
     */
    public void setTempMax(Double tempMax) {
        this.tempMax = tempMax;
    }
    /**
     *
     * @return umidità minima (notifiche)
     */
    public Double getUmidMin() {
        return umidMin;
    }

    /**
     * imposta il valore dell'umidità minima
     * @param umidMin:valore umidità minima
     */
    public void setUmidMin(Double umidMin) {
        this.umidMin = umidMin;
    }

    /**
     *
     * @return umidità massimo (notifiche)
     */
    public Double getUmidMax() {
        return umidMax;
    }

    /**
     * imposta il valore del'umidità massima
     * @param umidMax:valore umidità massima
     */
    public void setUmidMax(Double umidMax) {
        this.umidMax = umidMax;
    }

    /**
     *
     * @return conducibilità minima (notifiche)
     */
    public Double getCondMin() {
        return condMin;
    }

    /**
     * imposta il valore della conducibilità minima
     * @param condMin: valore conducibilità minima
     */
    public void setCondMin(Double condMin) {
        this.condMin = condMin;
    }
    /**
     *
     * @return conducibilità massimo (notifiche)
     */
    public Double getCondMax() {
        return condMax;
    }

    /**
     * imposta il valore della conducibilità massima
     * @param condMax: valore conducibilità massima
     */
    public void setCondMax(Double condMax) {
        this.condMax = condMax;
    }
    /**
     *
     * @return ph minima (notifiche)
     */
    public Double getPhMin() {
        return phMin;
    }

    /**
     * imposta il valore del ph minimo
     * @param phMin: valore ph minimo
     */
    public void setPhMin(Double phMin) {
        this.phMin = phMin;
    }
    /**
     *
     * @return ph massimo (notifiche)
     */
    public Double getPhMax() {
        return phMax;
    }

    public void setPhMax(Double phMax) {
        this.phMax = phMax;
    }
    /**
     *
     * @return irradianza minima (notifiche)
     */
    public Double getIrraMin() {
        return irraMin;
    }

    /**
     * imposta il valore dell'irradianza
     * @param irraMin: valore irradianza minimo
     */
    public void setIrraMin(Double irraMin) {
        this.irraMin = irraMin;
    }
    /**
     *
     * @return irradianza massimo (notifiche)
     */
    public Double getIrraMax() {
        return irraMax;
    }

    /**
     * imposta il valore dell'irradianza
     * @param irraMax:valore irradianza massimo
     */
    public void setIrraMax(Double irraMax) {
        this.irraMax = irraMax;
    }
    /**
     *
     * @return peso minima (notifiche)
     */
    public Double getPesMin() {
        return pesMin;
    }

    /**
     * imposta il valore del peso
     * @param pesMin:valore peso minimo
     */
    public void setPesMin(Double pesMin) {
        this.pesMin = pesMin;
    }
    /**
     *
     * @return evapotraspirazione massimo (notifiche)
     */
    public Double getPesMax() { return pesMax; }

    /**
     * imposta il valore dell'evapotraspirazione
     * @param pesMax: valore massimo evapotraspirazione
     */
    public void setPesMax(Double pesMax) {
        this.pesMax = pesMax;
    }

    /**
     *
     * @return nome del field associato al pulsante temperatura (nel caso di scelta manuale)
     */
    public String getImagetemp() {
        return imagetemp;
    }

    /**
     * imposta il nuovo field da visualizzare
     * @param imagetemp:nome del field associato al pulsante temperatura
     */
    public void setImagetemp(String imagetemp) {
        this.imagetemp = imagetemp;
    }
    /**
     *
     * @return nome del field associato al pulsante umidità (nel caso di scelta manuale)
     */
    public String getImageumid() {
        return imageumid;
    }
    /**
     * imposta il nuovo field da visualizzare
     * @param imageumid:nome del field associato al pulsante umidità
     */
    public void setImageumid(String imageumid) { this.imageumid = imageumid; }

    /**
     *
     * @return nome del field associato al pulsante ph (nel caso di scelta manuale)
     */
    public String getImageph() {
        return imageph;
    }
    /**
     * imposta il nuovo field da visualizzare
     * @param imageph:nome del field associato al pulsante ph
     */
    public void setImageph(String imageph) {
        this.imageph = imageph;
    }

    /**
     *
     * @return nome del field associato al pulsante conducibilità (nel caso di scelta manuale)
     */
    public String getImagecond() {
        return imagecond;
    }
    /**
     * imposta il nuovo field da visualizzare
     * @param imagecond:nome del field associato al pulsante conducibilità
     */
    public void setImagecond(String imagecond) {
        this.imagecond = imagecond;
    }
    /**
     *
     * @return nome del field associato al pulsante irradianza (nel caso di scelta manuale)
     */
    public String getImageirra() {
        return imageirra;
    }
    /**
     * imposta il nuovo field da visualizzare
     * @param imageirra:nome del field associato al pulsante irradianza
     */
    public void setImageirra(String imageirra) {
        this.imageirra = imageirra;
    }
    /**
     *
     * @return nome del field associato al pulsante evapotraspirazione (nel caso di scelta manuale)
     */
    public String getImagepeso() {
        return imagepeso;
    }
    /**
     * imposta il nuovo field da visualizzare
     * @param imagepeso:nome del field associato al pulsante evapotraspirazione
     */
    public void setImagepeso(String imagepeso) {
        this.imagepeso = imagepeso;
    }

    /**
     *
     * @return restituisce la presunta durata delll'irrigazione
     */
    public Double getIrrigationDuration() { return IrrigationDuration; }

    /**
     *
     * @param irrigationDuration: durata irrigazione (in minuti)
     */
    public void setIrrigationDuration(Double irrigationDuration) { IrrigationDuration = irrigationDuration; }

    /**
     *
     * @return il flusso dell'acqua
     */
    public Double getFlussoAcqua() { return FlussoAcqua; }

    /**
     * imposta flusso dell'acqua
     * @param flussoAcqua: memorizza il valore del flusso acqua
     */
    public void setFlussoAcqua(Double flussoAcqua) { FlussoAcqua = flussoAcqua; }

    /**
     *
     * @return valore del leachingfactor
     */
    public Double getLeachingfactor() { return Leachingfactor; }

    /**
     *
     * @param leachingfactor memorizza valore del leachingfactor
     */
    public void setLeachingfactor(Double leachingfactor) { Leachingfactor = leachingfactor; }

    /**
     *
     * @return numero irrigazioni al giorno
     */
    public int getNumirra() { return Numirra; }

    /**
     *
     * @param numirra: memorizza valore del numero irrigazioni al giorno
     */
    public void setNumirra(int numirra) { Numirra = numirra; }

    /**
     *
     * @return il valore in minuto dall'ultimo dato inserito
     */
    public int getLastimevalues() {
        return lastimevalues;
    }

    /**
     *
     * @param Lastimevalues: memorizza valore del leachingfactor
     */
    public void setLastimevalues(int Lastimevalues) {
       lastimevalues = Lastimevalues;
    }

    /**
     *
     * @return valore dell'evapotraspirazione
     */
    public Double getEvapotraspirazione() {
        return evapotraspirazione;
    }

    /**
     *
     * @param evapotraspirazione: memorizza valore dell'evapotraspirazione
     */
    public void setEvapotraspirazione(Double evapotraspirazione) { this.evapotraspirazione = evapotraspirazione; }

    /**
     *
     * @return numero di minuti
     */
    public Double getMinutes() {
        return minutes;
    }

    /**
     *
     * @param minutes numero di minuti
     */
    public void setMinutes(Double minutes) {
        this.minutes = minutes;
    }
}
