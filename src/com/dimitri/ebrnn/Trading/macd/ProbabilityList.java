package com.dimitri.ebrnn.Trading.macd;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ProbabilityList {

    private ArrayList<Probability> probabilities;
    private HashMap<Probability, Point> renderPlaces;

    public ProbabilityList(){
        probabilities = new ArrayList<>();
        renderPlaces = new HashMap<>();
    }

    public void add(Probability probability, Point point){
        probabilities.add(probability);
        renderPlaces.put(probability, point);
    }

    public void update(String name, double value){
        for (Probability p : probabilities) {
            if(p.getName().equals(name)){
                p.update(value);
            }
        }
    }

    public void render(Graphics g){
        for (Probability p: probabilities) {
            int x = (int)renderPlaces.get(p).getX();
            int y = (int)renderPlaces.get(p).getY();
            g.translate(x, y);
            p.render(g);
            g.translate(-x, -y);
        }
    }

    public ArrayList<Probability> getProbabilities() {
        return probabilities;
    }
}
