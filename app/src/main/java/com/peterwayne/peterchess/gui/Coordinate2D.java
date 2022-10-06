package com.peterwayne.peterchess.gui;

public class Coordinate2D {
    protected int x;
    protected int y;

    public Coordinate2D() {
    }

    public Coordinate2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Coordinate2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
