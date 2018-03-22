package com.dimitri.ebrnn.Trading.general.deviations;

public class MeanAbsoluteDeviation {

    private final double[] data;
    private int size;

    private double sum;
    private double average;

    private double meanAbsoluteDeviation;

    private int sampleSize;

    public MeanAbsoluteDeviation(int sampleSize, double[] data, int current){
        this.size = data.length;

        if(current > sampleSize){
            this.data = new double[sampleSize];
            for (int i = current-sampleSize, j = 0; i < current; i++, j++) {
                this.data[j] = data[i];
            }
            size = this.data.length;
        }else{
            this.data = data;
        }
        calcMeanAbsoluteDeviation();
    }

    private void calcMeanAbsoluteDeviation(){
        calcAverage();
        double sum = 0;
        for (int i = 0; i < size; i++) {
            sum += Math.abs(data[i]-average);
        }
        meanAbsoluteDeviation = sum/size;
    }

    private void calcAverage(){
        sum();
        average = sum/size;
    }

    private void sum(){
        sum = 0;
        for (int i = 0; i < size; i++) {
            sum += data[i];
        }
    }

    public double[] getData() {
        return data;
    }

    public int getSize() {
        return size;
    }

    public double getSum() {
        return sum;
    }

    public double getAverage() {
        return average;
    }

    public double getMeanAbsoluteDeviation() {
        return meanAbsoluteDeviation;
    }
}
