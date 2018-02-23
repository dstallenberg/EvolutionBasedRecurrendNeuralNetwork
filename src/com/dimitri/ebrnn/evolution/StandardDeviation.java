package com.dimitri.ebrnn.evolution;

import ai.brain.calculations.general.Calc;

import java.util.List;

public class StandardDeviation {

 //   private final double Pi = 3.141592653d;
    private final double Euler = 2.71828182846d;
    private final double sqrt2Pi = 2.50662827463d;
    private final double sqrtPi = 1.77245385d;
    private final double div2sqrtPi = 2/sqrtPi;
    private final double sqrt2 = 1.41421356237d;

    private final List<Double> dataSet;
    private final double totalSum;
    private final double size;

    private final double average;

    private final double standardDeviation;
    private final double standardError;

    public StandardDeviation(List<Double> dataSet){
        this.dataSet = dataSet;
        this.size = dataSet.size();
        this.totalSum = calcSum();
        this.average = calcAverage();
        this.standardDeviation = calcStandardDeviation();
        this.standardError = calcStandardError();
    }

    private double calcSum(){
        double sum = 0;
        for (int i = 0; i < size; i++) {
            sum += dataSet.get(i);
        }
        return sum;
    }

    private double calcAverage(){
        return (totalSum/size);
    }

    private double calcStandardDeviation(){
        double sum = 0;
        for (int i = 0; i < size; i++) {
            double delta = (dataSet.get(i)) - average;
            sum += (delta*delta);
        }
        return Math.sqrt(sum/size);
    }

    private double calcStandardError(){
        return (standardDeviation/Math.sqrt(size));
    }

    public double calcProbability(double current){
        double exp = (-((current-average)*(current-average)))/(2*standardDeviation*standardDeviation);

        double temp = (standardDeviation*sqrt2Pi);
        temp = 1/temp;
        temp = temp * Math.pow(Euler, exp);

        return temp;
    }

    public double calcCumulativeProbability(double current){
        if(current > average + standardDeviation*2.5){
            current = average + standardDeviation*2.5;
        }else if(current < average - standardDeviation*2.5){
            current = average - standardDeviation*2.5;
        }
        double result;

        double correction = 0.5d;
        double factor = 1/(standardDeviation*sqrt2Pi);
        double sum = current-average;
        double temp = 1;
        for (int i = 1; i < 30; i++) {
            temp *= -1;

            sum += temp*((Math.pow((current-average), 2*i+1))/((2*i+1)*Math.pow(standardDeviation, 2*i)*Math.pow(2, i)* factorial(i)));
        }
        result = (sum * factor) + correction;
        return result;
    }

    public double calcCumulativeProbability(double current, double spread){
        double save = current;
        current += spread;

        if(current > average + standardDeviation*2.5){
            current = average + standardDeviation*2.5;
        }else if(current < average - standardDeviation*2.5){
            current = average - standardDeviation*2.5;
        }

        double result;

        double correction = 0.5d;
        double factor = 1/(standardDeviation*sqrt2Pi);
        double sum = current-average;
        double temp = 1;
        for (int i = 1; i < 30; i++) {
            temp *= -1;

            sum += temp*((Math.pow((current-average), 2*i+1))/((2*i+1)*Math.pow(standardDeviation, 2*i)*Math.pow(2, i)*factorial(i)));
        }
        result = (sum * factor) + correction;

        current = save - spread;

        if(current > average + standardDeviation*2.5){
            current = average + standardDeviation*2.5;
        }else if(current < average - standardDeviation*2.5){
            current = average - standardDeviation*2.5;
        }

        correction = 0.5d;
        factor = 1/(standardDeviation*sqrt2Pi);
        sum = current-average;
        temp = 1;
        for (int i = 1; i < 30; i++) {
            temp *= -1;

            sum += temp*((Math.pow((current-average), 2*i+1))/((2*i+1)*Math.pow(standardDeviation, 2*i)*Math.pow(2, i)*factorial(i)));
        }
        result -= ((sum * factor) + correction);

        return result;
    }

    public double calcCumulativeProbabilityFast(double current){
        double x = ((current-average)/(standardDeviation*sqrt2));

        double sum = 0;
        for (int i = 0; i < 30; i++) {
            double counter = Math.pow(-1, i)*Math.pow(x, 2*i+1);
            double denom = factorial(i)*(2*i+1);
            sum += (counter/denom);
        }

        return ((sum * div2sqrtPi)*0.5);
    }

    public double factorial(int val) {
        int result = 1;
        for (int i = 1; i <= val; i++) {
            result *= i;
        }
        return result;
    }

    public double getAverage() {
        return average;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public double getStandardError() {
        return standardError;
    }
}
