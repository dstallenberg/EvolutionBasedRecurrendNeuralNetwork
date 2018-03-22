package com.dimitri.ebrnn.Trading.macd;

import com.dimitri.ebrnn.Trading.TickerDataParser;
import com.dimitri.ebrnn.Trading.general.Arrays;
import com.dimitri.ebrnn.Trading.general.Calc;
import com.dimitri.ebrnn.Trading.general.Candle;
import com.dimitri.ebrnn.Trading.macd.bows.Bow;
import com.dimitri.ebrnn.Trading.macd.bows.BowList;

import java.util.ArrayList;
import java.util.List;

public class MACD {

    private final double shortLength;
    private final double longLength;
    private final double signalLength;

    private final double[] lastPrices;
    private int current;

    private final double[] EMAShort;
    private int currentEMAShort;

    private final double[] EMALong;
    private int currentEMALong;

    private final double[] MACD;
    private int currentMACD;

    private final double[] signal;
    private int currentSignal;

    private final double[] tradeSignal;
    private int currentTradeSignal;
    private double highestTradeSignal;

    private double highest;
    private double lowest;

    private double multiplier;

    private BowList bowList;

    private int currentBowLengthCount;
    private int currentHighestPlace;

    private final double[] movingAverage;
    private int currentMovingAverage;

    private final List<String> lastChoices;
    private String choice;

    private boolean rising;

    private Bow currentBow;

    public MACD(double[] priceArray) {
        this.shortLength = 12;
        this.longLength = 26;
        this.signalLength = 9;

        this.lastChoices = new ArrayList<>();

        this.highest = 0;
        this.lowest = 1000000d;

        this.bowList = new BowList();

        this.current = 0;
        this.lastPrices = new double[1000];
        this.currentEMAShort = 0;
        this.EMAShort = new double[1000];
        this.currentEMALong = 0;
        this.EMALong = new double[1000];
        this.currentMACD = 0;
        this.MACD = new double[1000];
        this.currentSignal = 0;
        this.signal = new double[1000];
        this.currentTradeSignal = 0;
        this.tradeSignal = new double[1000];
        this.currentMovingAverage = 0;
        this.movingAverage = new double[1000];

        double temp = 0;
        for (Double price: priceArray) {
            if (price > temp) {
                temp = price;
            }
        }

        for (int i = 1; i <= 10; i++) {
            temp *= 10;
            if(Calc.floor(temp) != 0){
                multiplier = 1000*Math.pow(10, i);
                break;
            }
        }
        highestTradeSignal = 0.0000000001;

    }

    public MACD(TickerDataParser tickerDataParser) {
        this.shortLength = 12;
        this.longLength = 26;
        this.signalLength = 9;

        this.lastChoices = new ArrayList<>();

        this.highest = 0;
        this.lowest = 1000000d;

        this.bowList = new BowList();

        this.current = 0;
        this.lastPrices = new double[1000];
        this.currentEMAShort = 0;
        this.EMAShort = new double[1000];
        this.currentEMALong = 0;
        this.EMALong = new double[1000];
        this.currentMACD = 0;
        this.MACD = new double[1000];
        this.currentSignal = 0;
        this.signal = new double[1000];
        this.currentTradeSignal = 0;
        this.tradeSignal = new double[1000];
        this.currentMovingAverage = 0;
        this.movingAverage = new double[1000];

        double[] tempArray = tickerDataParser.getArray(1100);
        double temp = 0;
        for (Double price: tempArray) {
            if (price > temp) {
                temp = price;
            }
        }

        for (int i = 1; i <= 10; i++) {
            temp *= 10;
            if(Calc.floor(temp) != 0){
                multiplier = 1000*Math.pow(10, i);
                break;
            }
        }

        for (Double price: tempArray) {
            update(new Candle(price, 0, 0));
        }
    }

    public String update(Candle candle) {
        double currentPrice = candle.getCurrent();
        current = Arrays.rotate(lastPrices, current);

        lastPrices[current] = currentPrice * multiplier;

        if(lastPrices[current] > highest){
            highest = lastPrices[current];
        }

        if(highest < lastPrices[current]){
            highest = lastPrices[current];
        }

        if(lowest > lastPrices[current]){
            lowest = lastPrices[current];
        }

        if (current == shortLength) {
            double averageShortLength = (Arrays.sumLast(lastPrices, (int) shortLength, current) / shortLength);
            EMAShort[0] = averageShortLength;
        } else if (current == longLength) {
            double averageLongLength = (Arrays.sumLast(lastPrices, (int) longLength, current) / longLength);
            EMALong[0] = averageLongLength;
            MACD[0] = EMAShort[currentEMAShort] - EMALong[currentEMALong];
        } else if (current == longLength + signalLength) {
            double averageSignalLength = (Arrays.sumLast(MACD, (int) signalLength, currentMACD+1) / signalLength);
            signal[0] = averageSignalLength;
        }

        if(currentPrice > 50){
            currentMovingAverage = Arrays.rotate(movingAverage, currentMovingAverage);
            movingAverage[currentMovingAverage] = (Arrays.sumLast(lastPrices, 50, current)/50);
        }

        if (current > shortLength) {
            currentEMAShort = Arrays.rotate(EMAShort, currentEMAShort);
            Arrays.calcEMA(current, lastPrices, currentEMAShort, EMAShort, shortLength);
        }

        if (current > longLength) {
            currentEMALong = Arrays.rotate(EMALong, currentEMALong);
            Arrays.calcEMA(current, lastPrices, currentEMALong, EMALong, longLength);

            currentMACD = Arrays.rotate(MACD, currentMACD);
            MACD[currentMACD] = EMAShort[currentEMAShort] - EMALong[currentEMALong];
        }

        if (current > longLength + signalLength) {
            currentSignal = Arrays.rotate(signal, currentSignal);
            Arrays.calcEMA(currentMACD, MACD, currentSignal, signal, signalLength);

            currentTradeSignal = Arrays.rotate(tradeSignal, currentTradeSignal);
            tradeSignal[currentTradeSignal] = MACD[currentMACD]-signal[currentSignal];

            if(Math.abs(tradeSignal[currentTradeSignal]) > highestTradeSignal){
                highestTradeSignal = Math.abs(tradeSignal[currentTradeSignal]);
            }
        }

        calcBow();

        if(current > 60){
            if(currentTradeSignal > 0) {
                return trade();
            }else{
                return "Not ready to trade yet";
            }
        }else{
            return "Not ready to trade yet";
        }
    }

    public String trade() {
        double currentTradeSig = tradeSignal[currentTradeSignal];
        double last = 0;
        for (int i = 1; i < currentTradeSignal; i++) {
            last = tradeSignal[currentTradeSignal - i];
            if (last != 0) {
                break;
            }
        }

        String result = "";

//        if (currentTradeSig > 0 && last < 0) {
//            result = "buy";
//        } else if (currentTradeSig < 0 && last > 0) {
//            result = "sell";
//        } else
//        if (currentTradeSig > 0 && last > 0) {
//            if (currentTradeSig < last) {
//                result = "sell";
//            }
////            else if (currentTradeSig > last) {
////                result = "buy";
////            }
//        } else if (currentTradeSig < 0 && last < 0) {
//            if (currentTradeSig > last) {
//                result = "buy";
//            }
////            else if (currentTradeSig < last) {
////                result = "sell";
////            }
//        }


//        if(currentBowLengthCount < 3){
//            if(tradeSignal[currentTradeSignal] > 0){
//                if(quantity == 0){
//
//                }
//            }else{
//                if(quantity < 0){
//
//                }
//            }
//        }


//        if(currentMovingAverage > 70){
//            fibonacci.setBowList(bowList);
//            fibonacci.update(currentTradeSig, current, lastPrices, currentMovingAverage, movingAverage);
//
//            fibonacciChoice = fibonacci.getFibonacciChoice();
//            targetFibonacci = fibonacci.getTargetFibonacci();
//
//            double sum;
//            if(currentHighestPlace == currentBowLengthCount){
//                sum = heightProbability+lengthProbability+highestPointProbability;
//                sum /= 3;
//            }else{
//                sum = heightProbability+lengthProbability;
//                sum /= 2;
//            }
//
//            if(sum > 40){
//                if(fibonacciChoice != null){
//                    fibonacciAndProbabilityChoice = fibonacciChoice + ", " + String.format("%.3f",sum) + "%";
//                }
//            }else{
//                fibonacciAndProbabilityChoice = "" + String.format("%.3f",sum) + "%";
//            }


//            int lastTrade = 10;
//            String lastTradeString = "";
//            for (int i = (int)(lastChoices.size()-(length.getAverage()*3)); i < lastChoices.size(); i++) {
//                if(i >= 0){
//                    if(lastChoices.get(i).equals("buy") || lastChoices.get(i).equals("sell")){
//                        lastTrade = lastChoices.size()-i;
//                        lastTradeString = lastChoices.get(i);
//                    }
//                }
//            }

//            if(lastTrade < 0.25*length.getAverage()){
//                if(lastTradeString.equals("sell") && result.equals("buy")){
//                    if (fibonacciChoice.equals("sell")) {
//                        result = "Keep BTC";
//                    } else if(fibonacciChoice.equals("HODL")){
//                        result = "buy";
//                    } else{
//                        result = fibonacciChoice;
//                    }
//                }else if(lastTradeString.equals("buy") && result.equals("sell")){
//                    if(fibonacciChoice.equals("Keep BTC")){
//                        result = "sell";
//                    }else if(fibonacciChoice.equals("buy")){
//                        result = "HODL";
//                    }else{
//                        result = fibonacciChoice;
//                    }
//                }
//                Main.getTelegramBot().send("Fibonacci activated choice: " + result);
//            }
//        }

        choice = result;

        lastChoices.add(result);

        return result;
    }

    private void calcBow(){
        bowList = new BowList();

        int count = 0;
        double highest = 0;
        int highestPlace = 0;
        boolean firstBow = true;

        List<Double> heights = new ArrayList<>();

        for (int i = 1; i < tradeSignal.length-(tradeSignal.length-currentTradeSignal); i++) {
            if((tradeSignal[i-1] < 0 && tradeSignal[i] > 0) || (tradeSignal[i-1] > 0 && tradeSignal[i] < 0)){
                if(firstBow){
                    heights = new ArrayList<>();
                    firstBow = false;
                }else{
                    if((tradeSignal[i-1] < 0 && tradeSignal[i] > 0)){
                        bowList.add(new Bow(heights, count, highest, highestPlace, lastPrices[current-(count-highestPlace)], false));
                        rising = true;
                    }else if((tradeSignal[i-1] > 0 && tradeSignal[i] < 0)){
                        bowList.add(new Bow(heights, count, highest, highestPlace, lastPrices[current-(count-highestPlace)], true));
                        rising = false;
                    }
                    heights = new ArrayList<>();
                    heights.add(Math.abs(tradeSignal[i]));

                    highestPlace = 0;
                    highest =  Math.abs(tradeSignal[i]);
                    count = 0;

                }
            }else{
                double current = Math.abs(tradeSignal[i]);
                if (current > highest) {
                    highest = current;
                    highestPlace = count;
                }
                heights.add(current);
                count++;
            }


        }
        currentBowLengthCount = count;
        currentHighestPlace = highestPlace;

        boolean temp = false;
        if(tradeSignal[currentTradeSignal] > 0){
            temp = true;
        }

        currentBow = new Bow(heights, count, highest, highestPlace, lastPrices[current-(count-highestPlace)], temp);
    }

    public double getCurrentTrade(){
        return tradeSignal[currentTradeSignal];
    }

    public double[] getLastPrices() {
        return lastPrices;
    }

    public double[] getTradeSignal() {
        return tradeSignal;
    }

    public double getPrice(int index){
        return lastPrices[index];
    }

    public double getTradeSignal(int index){
        return tradeSignal[index];
    }

    public double getMultiplier() {
        return multiplier;
    }

    public int getCurrent() {
        return current;
    }

    public int getCurrentTradeSignal() {
        return currentTradeSignal;
    }

    public boolean isRising() {
        return rising;
    }

    public void setRising(boolean rising) {
        this.rising = rising;
    }

    public BowList getBowList() {
        return bowList;
    }

    public double getShortLength() {
        return shortLength;
    }

    public double getLongLength() {
        return longLength;
    }

    public double getSignalLength() {
        return signalLength;
    }

    public double[] getEMAShort() {
        return EMAShort;
    }

    public int getCurrentEMAShort() {
        return currentEMAShort;
    }

    public double[] getEMALong() {
        return EMALong;
    }

    public int getCurrentEMALong() {
        return currentEMALong;
    }

    public double[] getMACD() {
        return MACD;
    }

    public int getCurrentMACD() {
        return currentMACD;
    }

    public double[] getSignal() {
        return signal;
    }

    public int getCurrentSignal() {
        return currentSignal;
    }

    public double getHighest() {
        return highest;
    }

    public double getLowest() {
        return lowest;
    }

    public int getCurrentBowLengthCount() {
        return currentBowLengthCount;
    }

    public int getCurrentHighestPlace() {
        return currentHighestPlace;
    }

    public double[] getMovingAverage() {
        return movingAverage;
    }

    public int getCurrentMovingAverage() {
        return currentMovingAverage;
    }

    public List<String> getLastChoices() {
        return lastChoices;
    }

    public String getChoice() {
        return choice;
    }

    public Bow getCurrentBow() {
        return currentBow;
    }
}
