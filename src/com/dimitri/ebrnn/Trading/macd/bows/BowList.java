package com.dimitri.ebrnn.Trading.macd.bows;

import java.util.ArrayList;
import java.util.List;

public class BowList {

    private final List<Bow> bowList;

    private final List<Double> bowLength;
    private final List<Double> bowHighest;
    private final List<Double> bowHighestPlace;

    private final List<Double> bowHighestPrice;

    private final List<Boolean> bowPositive;

    public BowList(){
        this.bowList = new ArrayList<>();
        this.bowLength = new ArrayList<>();
        this.bowHighest = new ArrayList<>();
        this.bowHighestPlace = new ArrayList<>();
        this.bowHighestPrice = new ArrayList<>();
        this.bowPositive = new ArrayList<>();
    }

    public void add(Bow bow){
        bowList.add(bow);
        bowLength.add((double) bow.getLength());
        bowHighest.add(bow.getHeight());
        bowHighestPlace.add((double) bow.getHighestPlace());
        bowHighestPrice.add(bow.getPriceHighestPoint());
        bowPositive.add(bow.isPositive());
    }

    public Bow getBow(int index){
        return bowList.get(index);
    }

    public Bow getCurrentBow(){
        return bowList.get(bowList.size()-1);
    }

    public List<Bow> getBowList() {
        return bowList;
    }

    public List<Double> getBowLength() {
        return bowLength;
    }

    public List<Double> getBowHighest() {
        return bowHighest;
    }

    public List<Double> getBowHighestPlace() {
        return bowHighestPlace;
    }

    public List<Double> getBowHighestPrice() {
        return bowHighestPrice;
    }

    public List<Boolean> getBowPositive() {
        return bowPositive;
    }
}
