package com.dimitri.ebrnn.gfx;

import com.dimitri.ebrnn.neural.Net;

import java.awt.*;

public abstract class GraphicsObject {

    private final Net net;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public GraphicsObject(Net net, int x, int y, int width, int height){
        this.net = net;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void update();

    public abstract void render(Graphics graphics);

    public Net getNet() {
        return net;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
