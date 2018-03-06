package com.dimitri.ebrnn.Trading.fibonacci;

import com.dimitri.ebrnn.Trading.TickerDataParser;
import com.dimitri.ebrnn.Trading.general.Candle;

import java.util.ArrayList;

public class Fibonacci {

    private double resistance;
    private double support;

    private int count;

    private ArrayList<Candle> candles;
    private int current;

    private double low;
    private int lowPoint;
    private double high;
    private int highPoint;

    private double buyTarget;
    private double sellTarget;

    private int minCount;
    private int maxCount;
    private double interestPercentage;

    private double buy, sell;

    public Fibonacci(){
        candles = new ArrayList<>();
        buyTarget = 0;
        sellTarget = 0;
        highPoint = 0;
        lowPoint = 0;
        high = 0;
        low = 100000;
        current = 0;
        count = 0;

        minCount = 6;
        maxCount = 60;
        interestPercentage = 1;
        buy = 0.618d;
        sell = 0.5d; //0.382d;
    }

    //Testing only constructor
    public Fibonacci(int minCount, int maxCount, double interestPercentage, double buy, double sell){
        candles = new ArrayList<>();
        buyTarget = 0;
        sellTarget = 0;
        highPoint = 0;
        lowPoint = 0;
        high = 0;
        low = 100000;
        current = 0;
        count = 0;
        this.buy = buy;
        this.sell = sell;

        this.minCount = minCount;
        this.maxCount = maxCount;
        this.interestPercentage = interestPercentage;
    }

    public Fibonacci(TickerDataParser tickerDataParser){
        candles = new ArrayList<>();
        buyTarget = 0;
        sellTarget = 0;
        highPoint = 0;
        lowPoint = 0;
        high = 0;
        low = 0;
        current = 0;
        count = 0;

        minCount = 12;
        maxCount = 36;
        interestPercentage = 4;

        buy = 0.618d;
        sell = 0.5d; //0.382d;

        double[] tempArray = tickerDataParser.getArray(1100);
        double[] tempLowArray = tickerDataParser.getLowArray(1100);
        double[] tempHighArray = tickerDataParser.getHighArray(1100);
        for (int i = 0; i < tempArray.length; i++) {
            String p = update(new Candle(tempArray[i], tempHighArray[i], tempLowArray[i]));
        }
    }

    public String update(Candle candle){
        candles.add(candle);
        current++;

        if(count >= minCount){
            for (int i = candles.size()-count; i < candles.size(); i++) {
                if(candles.get(i).getLow() < low){
                    low = candles.get(i).getLow();
                    lowPoint = i;
                }
                if(candles.get(i).getHigh() > high){
                    high = candles.get(i).getHigh();
                    highPoint = i;
                }
            }

            if(((high-low)/low)*100 > interestPercentage){
                if(highPoint > lowPoint){
                    if(highPoint+1 < current){
                        buyTarget = high-((high-low)*buy);//0.618d);
                        sellTarget = 0;
                    }
                }else if(highPoint < lowPoint){
                    if(lowPoint+1 < current){
                        sellTarget = low+((high-low)*sell);//0.382d);
                        buyTarget = 0;
                    }
                }
            }

        }

        if(count >= maxCount){
            count = 0;
            high = 0;
            low = 100000;
        }
        count++;

        if(sellTarget != 0){
            return "sell " + sellTarget;
        }else if(buyTarget != 0){
            return "buy " + buyTarget;
        }else{
            return "nothing";
        }

    }

    public double getProjectedProfit(){
        double results = 0;
        double buyTarget = getBuyTarget();
        double sellTarget = getSellTarget();

        if(sellTarget != 0){
            results = (sellTarget-candles.get(current).getCurrent())/candles.get(current).getCurrent();
        }else if(buyTarget != 0){
            results = (buyTarget-candles.get(current).getCurrent())/candles.get(current).getCurrent();
        }

        return results;
    }

    public boolean isBuy(){
        return (buyTarget != 0);
    }

    public double getResistance() {
        return resistance;
    }

    public double getSupport() {
        return support;
    }

    public int getCount() {
        return count;
    }

    public ArrayList<Candle> getCandles() {
        return candles;
    }

    public int getCurrent() {
        return current;
    }

    public double getLow() {
        return low;
    }

    public int getLowPoint() {
        return lowPoint;
    }

    public double getHigh() {
        return high;
    }

    public int getHighPoint() {
        return highPoint;
    }

    public double getBuyTarget() {
        return buyTarget;
    }

    public double getSellTarget() {
        return sellTarget;
    }

    public int getMinCount() {
        return minCount;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public double getInterestPercentage() {
        return interestPercentage;
    }

    public double getBuy() {
        return buy;
    }

    public double getSell() {
        return sell;
    }
}
