package com.dimitri.ebrnn.neural.layers;

import com.dimitri.ebrnn.neural.Net;
import com.dimitri.ebrnn.neural.cells.Cell;
import com.dimitri.ebrnn.neural.cells.lstm.LSTMCell;

public class LSTMLayer extends Layer{

    private LSTMCell[] cell;

    public LSTMLayer(Net net, int layerIndex, int cellAmount) {
        super(net, layerIndex, cellAmount);
        cell = new LSTMCell[cellAmount];
        for (int i = 0; i < cell.length; i++) {
            cell[i] = new LSTMCell(this, i, net.getLayer(layerIndex-1).getCell().length);
        }
    }

    public LSTMLayer(Net net, int layerIndex, double[][][] weights) {
        super(net, layerIndex, weights.length);
        cell = new LSTMCell[weights.length];
        for (int i = 0; i < cell.length; i++) {
            cell[i] = new LSTMCell(this, i, net.getLayer(layerIndex-1).getCell().length, weights[i]);
        }
    }


    @Override
    public LSTMCell[] getCell() {
        return cell;
    }

    @Override
    public Cell getCell(int index) {
        return cell[index];
    }
}
