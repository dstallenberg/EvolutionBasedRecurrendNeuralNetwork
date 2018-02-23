package com.dimitri.ebrnn.neural.layers;

import com.dimitri.ebrnn.neural.Net;
import com.dimitri.ebrnn.neural.cells.Cell;

public abstract class Layer {

    private Net net;
    private int layerIndex;
    private int cellAmount;

    public Layer(Net net, int layerIndex, int cellAmount){
        this.net = net;
        this.layerIndex = layerIndex;
        this.cellAmount = cellAmount;
    }

    public abstract Cell[] getCell();
    public abstract Cell getCell(int index);

    public Net getNet() {
        return net;
    }

    public int getLayerIndex() {
        return layerIndex;
    }
}
