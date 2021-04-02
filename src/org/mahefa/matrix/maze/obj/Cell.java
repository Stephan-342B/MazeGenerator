package org.mahefa.matrix.maze.obj;

import java.util.Arrays;
import java.util.Objects;

public class Cell {
    private Coordinates coordinates;
    private Coordinates[] neighbors;
    private Wall wall = new Wall();
    private Cell precedingPath = null;

    private int f = 0;
    private int g = 0;
    private int h = 0;

    public Cell(int row, int col) {
        this.coordinates = new Coordinates(row, col);
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Coordinates[] getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(Coordinates[] neighbors) {
        this.neighbors = neighbors;
    }

    public Wall getWall() {
        return wall;
    }

    public void setWall(Wall wall) {
        this.wall = wall;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public Cell getPrecedingPath() {
        return precedingPath;
    }

    public void setPrecedingPath(Cell precedingPath) {
        this.precedingPath = precedingPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;

        Cell cell = (Cell) o;

        return getF() == cell.getF() &&
                getG() == cell.getG() &&
                getH() == cell.getH() &&
                Objects.equals(getCoordinates(), cell.getCoordinates()) &&
                Arrays.equals(getNeighbors(), cell.getNeighbors()) &&
                Objects.equals(getWall(), cell.getWall()) &&
                Objects.equals(getPrecedingPath(), cell.getPrecedingPath());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getCoordinates(), getWall(), getPrecedingPath(), getF(), getG(), getH());
        result = 31 * result + Arrays.hashCode(getNeighbors());

        return result;
    }
}
