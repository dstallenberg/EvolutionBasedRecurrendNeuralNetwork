package com.dimitri.ebrnn.Trading.cci;

import com.dimitri.ebrnn.Trading.TickerDataParser;
import com.dimitri.ebrnn.Trading.general.Arrays;
import com.dimitri.ebrnn.Trading.general.Candle;
import com.dimitri.ebrnn.Trading.general.deviations.MeanAbsoluteDeviation;

public class CCI {

    private MeanAbsoluteDeviation meanAbsoluteDeviation;
    private double[] prices;
    private double[] highs;
    private double[] lows;

    private int current;

    private double[] typicalPrice;
    private int currentTypicalPrice;

    private double[] SMA;
    private int currentSMA;

    private double[] CCI;
    private int currentCCI;

    private int SMAStepSize;
    private int tradeInterval;

    private double high;
    private double low;
    private double currentPrice;

    private String choice;

    public CCI(){
        this.current = 0;
        this.prices = new double[1000];
        this.highs = new double[1000];
        this.lows = new double[1000];

        this.currentSMA = 0;
        this.SMA = new double[1000];
        this.currentCCI = 0;
        this.CCI = new double[1000];
        this.currentTypicalPrice = 0;
        this.typicalPrice = new double[1000];

        this.SMAStepSize = 30;
        this.tradeInterval = 100;
    }

    public CCI(TickerDataParser tickerDataParser){
        this.current = 0;
        this.prices = new double[1000];
        this.highs = new double[1000];
        this.lows = new double[1000];

        this.currentSMA = 0;
        this.SMA = new double[1000];
        this.currentCCI = 0;
        this.CCI = new double[1000];
        this.currentTypicalPrice = 0;
        this.typicalPrice = new double[1000];

        this.SMAStepSize = 30;
        this.tradeInterval = 100;

        double[] tempArray = tickerDataParser.getArray(1100);
        double[] tempLowArray = tickerDataParser.getLowArray(1100);
        double[] tempHighArray = tickerDataParser.getHighArray(1100);
        for (int i = 0; i < tempArray.length; i++) {
            update(new Candle(tempArray[i], tempHighArray[i], tempLowArray[i]));
        }
    }

    public String update(Candle candle){
        Arrays.rotate(highs, current);
        Arrays.rotate(lows, current);
        current = Arrays.rotate(prices, current);

        this.currentPrice = candle.getCurrent();
        this.low = candle.getLow();
        this.high = candle.getHigh();

        currentTypicalPrice = Arrays.rotate(typicalPrice, currentTypicalPrice);
        typicalPrice[currentTypicalPrice] = (high+low+currentPrice)/3d;

        meanAbsoluteDeviation = new MeanAbsoluteDeviation(SMAStepSize, typicalPrice, currentTypicalPrice);

        choice = "Do nothing";

        if(currentTypicalPrice > SMAStepSize) {
            currentSMA = Arrays.rotate(SMA, currentSMA);
            SMA[currentSMA] = Arrays.sumLast(typicalPrice, SMAStepSize, currentTypicalPrice)/SMAStepSize;

            currentCCI = Arrays.rotate(CCI, currentCCI);
            CCI[currentCCI] = (1d/0.015d)*((typicalPrice[currentTypicalPrice]-SMA[currentSMA])/meanAbsoluteDeviation.getMeanAbsoluteDeviation());

            if(CCI[currentCCI] > tradeInterval){
                choice = "sell";
            }else if(CCI[currentCCI] < -tradeInterval){
                choice = "buy";
            }
        }

        return choice;
    }

    public double getLastCCI(){
        return CCI[currentCCI];
    }

    public MeanAbsoluteDeviation getMeanAbsoluteDeviation() {
        return meanAbsoluteDeviation;
    }

    public double[] getPrices() {
        return prices;
    }

    public int getCurrent() {
        return current;
    }

    public double[] getTypicalPrice() {
        return typicalPrice;
    }

    public int getCurrentTypicalPrice() {
        return currentTypicalPrice;
    }

    public double[] getSMA() {
        return SMA;
    }

    public int getCurrentSMA() {
        return currentSMA;
    }

    public double[] getCCI() {
        return CCI;
    }

    public double getCCI(int index){
        return CCI[index];
    }

    public int getCurrentCCI() {
        return currentCCI;
    }

    public int getSMAStepSize() {
        return SMAStepSize;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public String getChoice() {
        return choice;
    }
}
