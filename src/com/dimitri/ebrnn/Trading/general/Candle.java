package com.dimitri.ebrnn.Trading.general;

public class Candle {

    private double current;
    private double high;
    private double low;

    public Candle(Candle candle){
        this.current = candle.getCurrent();
        this.high = candle.getHigh();
        this.low = candle.getLow();
    }

    public Candle(double current, double high, double low){
        this.current = current;
        this.high = high;
        this.low = low;
    }

    public double getCurrent() {
        return current;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public void setLow(double low) {
        this.low = low;
    }
}
