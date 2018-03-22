package com.dimitri.ebrnn.Trading;

import com.dimitri.ebrnn.Trading.cci.CCI;
import com.dimitri.ebrnn.Trading.fibonacci.Fibonacci;
import com.dimitri.ebrnn.Trading.general.Candle;
import com.dimitri.ebrnn.Trading.general.deviations.StandardDeviation;
import com.dimitri.ebrnn.Trading.macd.MACD;
import com.dimitri.ebrnn.Trading.macd.Probability;
import com.dimitri.ebrnn.neural.Net;

import java.util.ArrayList;
import java.util.Random;

public class TraderNet {

    private final Net net;

    private double currentProfit;
    private ArrayList<Double> profits;
    private int positives;
    private int negatives;
    private int zero;

    private double BTCbeforeTrade;
    private int positivesTrades;
    private int totalTrades;

    private double quantityCoin;
    private double BTC;
    private double startBTC;

    private final Random random;

    public TraderNet(int[] topology){
        net = new Net(topology);
        profits = new ArrayList<>();
        currentProfit = 0;
        quantityCoin = 0;
        BTC = 1;
        startBTC = BTC;
        random = new Random();
        positives = 0;
        negatives = 0;
        zero = 0;

        BTCbeforeTrade = 0;
        positivesTrades = 0;
        totalTrades = 0;
    }

    public TraderNet(Net net){
        this.net = net;
        profits = new ArrayList<>();
        currentProfit = 0;
        quantityCoin = 0;
        BTC = 1;
        startBTC = BTC;
        random = new Random();
        positives = 0;
        negatives = 0;
        zero = 0;

        BTCbeforeTrade = 0;
        positivesTrades = 0;
        totalTrades = 0;
    }

    public void feed(ArrayList<TickerDataParser> array){
        profits = new ArrayList<>();
        currentProfit = 0;
        quantityCoin = 0;
        BTC = 1;
        startBTC = BTC;

        positives = 0;
        negatives = 0;
        zero = 0;

        BTCbeforeTrade = 0;
        positivesTrades = 0;
        totalTrades = 0;

        for (TickerDataParser t: array) {

            currentProfit = 0;
            quantityCoin = 0;
            BTC = 1;
            startBTC = BTC;

            double[] prices = t.getArray(1440);//t.getFullArray();
            double[] lows = t.getLowArray(1440);//t.getFullLowArray();
            double[] highs = t.getHighArray(1440);//t.getFullHighArray();
            CCI cci = new CCI();
            MACD macd = new MACD(prices);
            Fibonacci fibonacci = new Fibonacci();
            for (int i = 0; i < prices.length && i < lows.length && i < highs.length; i++) {
                Candle candle = new Candle(prices[i], highs[i], lows[i]);
                cci.update(candle);
                macd.update(candle);
                fibonacci.update(candle);
                if(i > 200){
                    Probability probabilityLength = new Probability(new StandardDeviation(macd.getBowList().getBowLength()));
                    Probability probabilityHeight = new Probability(new StandardDeviation(macd.getBowList().getBowHighest()));
                    Probability probabilityHighestPlace = new Probability(new StandardDeviation(macd.getBowList().getBowHighestPlace()));
                    probabilityLength.update(macd.getCurrentBow().getLength()*2);
                    probabilityHeight.update(macd.getCurrentBow().getHeight());
                    probabilityHighestPlace.update(macd.getCurrentBow().getHighestPlace());

                    double currentCCI = cci.getCurrentCCI()/500;
                    double currentMACD = Math.abs(macd.getCurrentTrade())/macd.getHighest();
                    double rising = 0;
                    if(macd.getCurrentBow().isPositive()){
                        rising = 1;
                    }
                    double currentFibbonacci = fibonacci.getProjectedProfit();
                    double buy = 0;
                    if(fibonacci.isBuy()){
                        buy = 1;
                    }
                    double bowLengthProb = probabilityLength.getValueProbability();
                    double bowHeightProb = probabilityHeight.getValueProbability();
                    double bowHighestPlaceProb = probabilityHighestPlace.getValueProbability();


                    double[] input = new double[]{currentCCI, currentMACD, rising, currentFibbonacci, buy, bowLengthProb, bowHeightProb, bowHighestPlaceProb};

                    net.feedForward(input);

                    currentProfit = (((BTC+quantityCoin*candle.getCurrent())-startBTC)/startBTC)*100;
                    if(currentProfit < -100){
                        break;
                    }

                    double[] output = net.getOutput();

                    // output[0] means buy
                    // output[1] means nothing
                    // sell when profit

                    if(output[0] > output[1]){
                        if(quantityCoin == 0){
                            buy(candle.getCurrent());
                        }else{
                            // Do nothing
                        }
                    }else{
                        if(quantityCoin > 0){
                            if(currentProfit > 5 || currentProfit < -5){
                                sell(candle.getCurrent());
                            }
                        }else{
                            // Do nothing
                        }
                    }




                    // maybe should be with 3 outputs
                    // output[0] means buy
                    // output[1] means sell
                    // output[2] means do nothing
//                    if(output[0] > output[1]){
//                        if(output[0] > output[2]){
//                            if(quantityCoin == 0){
//                                buy(candle.getCurrent());
//                            }
//                        }
//                    }else{
//                        if(output[1] > output[2]){
//                            if(quantityCoin > 0){
//                                sell(candle.getCurrent());
//                            }
//                        }
//                    }


//                    if(output[0] > 0.5d && output[1] > 0.5d){
//                        if(quantityCoin > 0){
//                            sell(candle.getCurrent());
//                        }else{
//                            buy(candle.getCurrent());
//                        }
//                    }else if(output[0] > 0.5d){
//                        if(quantityCoin > 0){
//                            sell(candle.getCurrent());
//                        }
//                    }else if(output[1] > 0.5d){
//                        if(quantityCoin == 0){
//                            buy(candle.getCurrent());
//                        }
//                    }


                }
            }
            profits.add(currentProfit);
            if(currentProfit > 0){
                positives++;
            }else if(currentProfit < 0){
                negatives++;
            }else{
                zero++;
            }
        }
    }

    public void buy(double price){
        BTCbeforeTrade = BTC;
        double quantity = Math.floor(BTC/price);

        if(quantity == 0){
            quantity = Double.parseDouble(String.format("%.4f", (BTC/price)).replace("," , "."));
        }

        quantityCoin = quantity;

//        BTC = BTC-(quantity*price);
        BTC = BTC-((quantity*price)+quantity*price*0.02d);
    }

    public void sell(double price){
//        BTC = BTC + (price*quantityCoin);
        BTC = BTC + (price*quantityCoin*0.98d);
        quantityCoin = 0;

        double tradeProfit = ((BTC-BTCbeforeTrade)/BTCbeforeTrade)*100;

        if(tradeProfit >= 0){
            positivesTrades++;
        }
        totalTrades++;
    }

    public Net mutate(){
        return net.mutate(5, 1000, 1000);
    }


    public double getCurrentProfit() {
        return currentProfit;
    }

    public double getAverageProfit() {
        double sum = 0;
        for (Double profit : profits) {
            sum += profit;
        }
        return sum/profits.size();
    }

    public Net getNet() {
        return net;
    }

    public double getQuantityCoin() {
        return quantityCoin;
    }

    public double getBTC() {
        return BTC;
    }

    public double getStartBTC() {
        return startBTC;
    }

    public Random getRandom() {
        return random;
    }

    public int getPositives() {
        return positives;
    }

    public int getNegatives() {
        return negatives;
    }

    public int getZero() {
        return zero;
    }

    public int getPositivesTrades() {
        return positivesTrades;
    }

    public int getTotalTrades() {
        return totalTrades;
    }
}
