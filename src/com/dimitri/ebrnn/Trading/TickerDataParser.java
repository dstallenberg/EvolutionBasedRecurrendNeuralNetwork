package com.dimitri.ebrnn.Trading;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class TickerDataParser {

    private String pair;
    private String interval;

    private JSONArray data;

    private String lastGivenTimestamp;

    private String dataString;

    public TickerDataParser(String dataString) throws IOException {
        this.dataString = dataString;
        JSONObject getTicker = new JSONObject(dataString);
        if(getTicker.getBoolean("success")){
            this.data = getTicker.getJSONArray("result");
        }else{
            throw new IOException("No success");
        }
    }

    public int getSize(){
        return data.length();
    }

    public JSONObject getObject(int index){
        return data.getJSONObject(index);
    }

    public double getLastPrice(int index){
        lastGivenTimestamp = data.getJSONObject(index).getString("T");
        return data.getJSONObject(index).getDouble("C");
    }

    public double getLastHigh(int index){
        return data.getJSONObject(index).getDouble("H");
    }

    public double getLastLow(int index){
        return data.getJSONObject(index).getDouble("L");
    }

    public double getLastOpen(int index){ return data.getJSONObject(index).getDouble("O");}

    public double[] getArray(int lenght){
        int start = getSize()-lenght;
        if(lenght > getSize()){
            lenght = getSize();
            start = 0;
        }

        double[] prices = new double[lenght];

        for (int i = 0; i < prices.length; i++) {
            prices[i] = getLastPrice(start+i);
        }
        return prices;
    }

    public double[] getHighArray(int lenght){
        int start = getSize()-lenght;
        if(lenght > getSize()){
            lenght = getSize();
            start = 0;
        }

        double[] prices = new double[lenght];

        for (int i = 0; i < prices.length; i++) {
            prices[i] = getLastHigh(start+i);
        }
        return prices;
    }

    public double[] getLowArray(int lenght){
        int start = getSize()-lenght;
        if(lenght > getSize()){
            lenght = getSize();
            start = 0;
        }

        double[] prices = new double[lenght];

        for (int i = 0; i < prices.length; i++) {
            prices[i] = getLastLow(start+i);
        }
        return prices;
    }

    public double[] getOpenArray(int lenght){
        int start = getSize()-lenght;
        if(lenght > getSize()){
            lenght = getSize();
            start = 0;
        }

        double[] prices = new double[lenght];

        for (int i = 0; i < prices.length; i++) {
            prices[i] = getLastOpen(start+i);
        }
        return prices;
    }

    public double[] getFullArray(){
        double[] result = new double[getSize()];
        for (int i = 0; i < getSize(); i++) {
            result[i] = getLastPrice(i);
        }
        return result;
    }

    public double[] getFullHighArray(){
        double[] result = new double[getSize()];
        for (int i = 0; i < getSize(); i++) {
            result[i] = getLastHigh(i);
        }
        return result;
    }

    public double[] getFullLowArray(){
        double[] result = new double[getSize()];
        for (int i = 0; i < getSize(); i++) {
            result[i] = getLastLow(i);
        }
        return result;
    }

    public double[] getFullOpenArray(){
        double[] result = new double[getSize()];
        for (int i = 0; i < getSize(); i++) {
            result[i] = getLastOpen(i);
        }
        return result;
    }

    public String getDataString() {
        return dataString;
    }

    public String getPair() {
        return pair;
    }

    public String getInterval() {
        return interval;
    }

    public JSONArray getData() {
        return data;
    }

    public String getLastGivenTimestamp() {
        return lastGivenTimestamp;
    }
}
