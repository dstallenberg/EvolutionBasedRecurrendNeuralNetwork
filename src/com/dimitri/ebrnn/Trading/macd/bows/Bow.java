package com.dimitri.ebrnn.Trading.macd.bows;

import java.util.List;

public class Bow {

    private final List<Double> heights;

    private final int length;
    private final double height;
    private final int highestPlace;

    private final double priceHighestPoint;

    private final boolean positive;

    public Bow(List<Double> heights, int length, double height, int highestPlace, double priceHighestPoint, boolean positive){
        this.length = length;
        this.height = height;
        this.highestPlace = highestPlace;
        this.priceHighestPoint = priceHighestPoint;
        this.positive = positive;
        this.heights = heights;
    }


    public int getLength() {
        return length;
    }

    public double getHeight() {
        return height;
    }

    public int getHighestPlace() {
        return highestPlace;
    }

    public double getPriceHighestPoint() {
        return priceHighestPoint;
    }

    public boolean isPositive() {
        return positive;
    }

    public List<Double> getHeights() {
        return heights;
    }
}
