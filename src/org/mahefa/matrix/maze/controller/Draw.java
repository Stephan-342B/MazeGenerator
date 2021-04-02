package org.mahefa.matrix.maze.controller;

import org.mahefa.matrix.maze.obj.Cell;
import org.mahefa.matrix.maze.obj.Coordinates;
import org.mahefa.matrix.maze.obj.Wall;

import javax.swing.*;
import java.awt.*;

public abstract class Draw extends JPanel {

    public static int xAxisMargin;
    public  static int yAxisMargin;

    public static void adCell(Graphics2D graphics2D, final Cell cell, final String background) {
        if(cell != null) {
            final Coordinates coordinates = cell.getCoordinates();
            final int x = coordinates.getX() + xAxisMargin;
            final int y = coordinates.getY() + yAxisMargin;
            final int width = coordinates.getWidth();
            final int height = coordinates.getHeight();

            graphics2D.setColor(Color.decode(background));
            graphics2D.fillRect(x , y , width, height);

            // Remove wall
            addWall(graphics2D, cell);
        }
    }

    public static void addWall(Graphics2D graphics2D, Cell cell) {
        final Coordinates coordinates = cell.getCoordinates();
        final Wall wall = cell.getWall();
        final int x = coordinates.getX() + xAxisMargin;
        final int y = coordinates.getY() + yAxisMargin;
        final int width = coordinates.getWidth();
        final int height = coordinates.getHeight();
        final int xWidth = x + width;
        final int yHeight = y + height;

        graphics2D.setColor(Color.black);

        // Top
        if(wall.isTop()) {
            graphics2D.drawLine(x, y, xWidth, y);
        }

        // Left
        if(wall.isLeft()) {
            graphics2D.drawLine(x, y, x, yHeight);
        }

        // Bottom
        if(wall.isBottom()) {
            graphics2D.drawLine(x, yHeight, xWidth, yHeight);
        }

        // Right
        if(wall.isRight()) {
            graphics2D.drawLine(xWidth, y, xWidth, yHeight);
        }
    }

    public static void drawPath(Graphics2D graphics2D, final Cell cell, final String background, final boolean showPosition) {
        if(cell != null) {
            final Cell previous = cell.getPrecedingPath();
            final Coordinates coordinates = cell.getCoordinates();
            final int width = coordinates.getWidth();
            final int height = coordinates.getHeight();

            int x = coordinates.getX() + xAxisMargin;
            int y = coordinates.getY() + yAxisMargin;

            final BasicStroke stroke = new BasicStroke(5f);

            graphics2D.setColor(Color.decode(background));
            graphics2D.setStroke(stroke);

            if(!isVerticalAlign(cell, previous)) {
                x += (coordinates.getColumn() + 1 == previous.getCoordinates().getColumn()) ? width : 0;
                y += width / 2;
            } else {
                x += height / 2;
                y += (coordinates.getRow() + 1 == previous.getCoordinates().getRow()) ? height : 0;
            }

            if(showPosition) {
                graphics2D.drawString("[" + coordinates.getRow()+ ", " + coordinates.getColumn() + "]", x, y);
            }

            graphics2D.drawLine(x, y, x, y);

            /*System.out.println("Path [" + cell.getCoordinates().getRow() + ", " + cell.getCoordinates().getColumn() + "] wall ["
                    + cell.getWall().isTop() + ", " + cell.getWall().isLeft() + ", "
                    + cell.getWall().isBottom() + ", " + cell.getWall().isRight() + "]");*/
        }
    }

    public static void addCenteredText(Graphics2D graphics2D, final String text, int x, int y, final int width, final int height, final Font font, final String color) {
        FontMetrics metrics = graphics2D.getFontMetrics(font);

        // Determine the X coordinate for the text
        x += (width - metrics.stringWidth(text)) / 2;

        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        y += ((height + xAxisMargin - metrics.getHeight()) / 2) + metrics.getAscent();

        addText(graphics2D, text, x, y, font, color);
    }

    public static void addText(Graphics2D graphics2D, final String text, final int x, final int y, final Font font, final String color) {
        graphics2D.setColor(Color.decode(color));

        // Set the font
        if(font != null) {
            graphics2D.setFont(font);
        }

        graphics2D.drawString(text, x, y);
    }

    private static boolean isVerticalAlign(Cell current, Cell previous) {
        final Coordinates currentCoordinates = current.getCoordinates();
        final Coordinates previousCoordinates = previous.getCoordinates();

        return (currentCoordinates.getRow() != previousCoordinates.getRow())
                && (currentCoordinates.getColumn() == previousCoordinates.getColumn());
    }
}
