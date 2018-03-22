package com.dimitri.ebrnn.Trading.macd;

import com.dimitri.ebrnn.Trading.general.deviations.StandardDeviation;

public class Probability {

    private final StandardDeviation standardDeviation;
    private double currentValue;
    private double valueProbability;

    public Probability(StandardDeviation standardDeviation){
        this.standardDeviation = standardDeviation;
        this.currentValue = 0;
        this.valueProbability = 0;
    }

    public void update(double value){
        currentValue = value;
        valueProbability = standardDeviation.calcCumulativeProbabilityFast(value);
        if(valueProbability > 100){
            valueProbability = 100;
        }
    }

    public StandardDeviation getStandardDeviation() {
        return standardDeviation;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public double getValueProbability() {
        return valueProbability;
    }

}
