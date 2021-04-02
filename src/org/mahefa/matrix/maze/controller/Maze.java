package org.mahefa.matrix.maze.controller;

import org.mahefa.data_structure.Heap;
import org.mahefa.matrix.maze.obj.Cell;
import org.mahefa.matrix.maze.obj.Coordinates;
import org.mahefa.matrix.maze.obj.Wall;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Maze extends JPanel {
    private int row;
    private int col;
    private Cell[][] maze;
    private boolean[][] visited;
    private Heap openSet;
    private Heap closedSet;

    private boolean showPosition;

    private BufferedImage bufferedImage;
    private Timer animationTimer ;

    private int xAxisMargin = 0;

    public static final int CANVAS_WIDTH = 800;
    public static final int CANVAS_HEIGHT = 800;

    private static final int Y_AXIS_MARGIN = 35;

    private static final String POINTER_COLOR = "#F1C40F";
    private static final String VISITED_COLOR = "#F0F3F4";
    private static final String UNVISITED_COLOR = "#424949";
    private static final String PATH_COLOR = "#5DADE2";
    private static final String ERROR_COLOR = "#A93226";

    private static final String NO_SOLUTION_MESSAGE = "No solution";

    private static int MATRIX_BUILDER_DELAY = 1;
    private static int MAZE_GENERATOR_DELAY = 1;
    private static int SHOWING_PATH_DELAY = 25;

    public Maze(int row, int col) {
        this.row = row;
        this.col = col;
        this.maze = new Cell[row][col];
        this.visited = new boolean[row][col];

        this.xAxisMargin = (CANVAS_WIDTH % row) / 2;
        this.showPosition = row < 30 && col < 30;

        Draw.xAxisMargin = xAxisMargin;
        Draw.yAxisMargin = Y_AXIS_MARGIN;

        setBackground(Color.WHITE);
        JButton drawButton = new JButton("Draw");
        drawButton.addActionListener(e -> action(MATRIX_BUILDER_DELAY, new MatrixBuilder()));
        add(drawButton);

        JButton generateMazeButton = new JButton("Generate Maze");
        generateMazeButton.addActionListener(e -> action(MAZE_GENERATOR_DELAY, new MazeGenerator()));
        add(generateMazeButton);

        JButton solveMazeButton = new JButton("Solve");
        solveMazeButton.addActionListener(e -> solve(this.getCell(0, 0), this.getCell(row - 1, col - 1)));
        add(solveMazeButton);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> showGUI());
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        this.bufferedImage = this.getBufferedImage();
        graphics.drawImage(this.bufferedImage, 0,0, this);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT);
    }

    private static void showGUI() {
        Maze maze = new Maze(50, 50);

        JFrame jFrame = new JFrame("Maze generator + solver");
        Container container = jFrame.getContentPane();
        container.setLayout(new BorderLayout());

        container.add(maze, BorderLayout.CENTER);
        jFrame.pack();
        jFrame.setResizable(true);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
    }

    private class MatrixBuilder implements ActionListener {
        int dimX = (CANVAS_WIDTH - xAxisMargin) / row;
        int dimY = (CANVAS_HEIGHT - Y_AXIS_MARGIN) / col;

        int r = 0;
        int c = 0;
        int scaleX = 0;
        int scaleY = 0;

        @Override
        public void actionPerformed(ActionEvent e) {
            Graphics2D graphics2D = bufferedImage.createGraphics();

            Cell cell =  new Cell(r, c);
            cell.setNeighbors(getNeighbors(r, c, false));

            // Add drawing position
            Coordinates coordinates = cell.getCoordinates();
            coordinates.setX(scaleX);
            coordinates.setY(scaleY);
            coordinates.setWidth(dimX);
            coordinates.setHeight(dimY);

            cell.setCoordinates(coordinates);

            maze[r][c] = cell;

            // Set array as unvisited yet
            visited[r][c]= false;

            Draw.adCell(graphics2D, cell, UNVISITED_COLOR);
            graphics2D.dispose();

            c++;
            scaleX += dimX;

            if(c == col) {
                scaleY += dimY;
                scaleX = 0;
                r++;
                c = 0;

                if(r == row) {
                    ((Timer) e.getSource()).stop();
                }
            }

            repaint();
        }
    }

    /**
     * Aldous-Broder algorithm
     */
    private class MazeGenerator implements ActionListener {
        int counter = row * col;
        Cell currentCell = null;
        Cell neighbor = null;

        @Override
        public void actionPerformed(ActionEvent e) {
            Graphics2D graphics2D = bufferedImage.createGraphics();

            if(currentCell == null) {
                // Pick random cell as the current cell
                currentCell = getRandomCell();
                final Coordinates currentCoordinates = currentCell.getCoordinates();

                // Mark as visited
                visited[currentCoordinates.getRow()][currentCoordinates.getColumn()] = true;
                counter--;

                Draw.adCell(graphics2D, currentCell, POINTER_COLOR);
            } else {
                if(visited[currentCell.getCoordinates().getRow()][currentCell.getCoordinates().getColumn()]) {
                    Draw.adCell(graphics2D, currentCell, VISITED_COLOR);

                    if(neighbor != null) {
                        Draw.adCell(graphics2D, neighbor, VISITED_COLOR);

                        // Mark neighbor as the current cell
                        currentCell = neighbor;
                    }

                    // While there are unvisited cell
                    if(counter == 0) {
                        ((Timer) e.getSource()).stop();
                        repaint();
                        return;
                    }
                }

                // Pick random neighbor
                neighbor = getRandomNeighbor(currentCell);
                final Coordinates neighborCoordinates = neighbor.getCoordinates();

                Draw.adCell(graphics2D, neighbor, POINTER_COLOR);

                // If the chosen neighbour has not been visited
                if (!visited[neighborCoordinates.getRow()][neighborCoordinates.getColumn()]) {
                    // Remove wall between the current cell and the neighbor
                    final Coordinates currentCellCoordinates = currentCell.getCoordinates();

                    Wall neighborWall = neighbor.getWall();
                    Wall currentCellWall = currentCell.getWall();

                    if (neighborCoordinates.getRow() + 1 == currentCellCoordinates.getRow()) {
                        neighborWall.setBottom(false);
                        currentCellWall.setTop(false);
                    } else if (neighborCoordinates.getRow() - 1 == currentCellCoordinates.getRow()) {
                        neighborWall.setTop(false);
                        currentCellWall.setBottom(false);
                    } else if (neighborCoordinates.getColumn() + 1 == currentCellCoordinates.getColumn()) {
                        neighborWall.setRight(false);
                        currentCellWall.setLeft(false);
                    } else if (neighborCoordinates.getColumn() - 1 == currentCellCoordinates.getColumn()) {
                        neighborWall.setLeft(false);
                        currentCellWall.setRight(false);
                    }

                    // Mark as visited
                    visited[neighborCoordinates.getRow()][neighborCoordinates.getColumn()] = true;
                    counter--;
                }
            }

            graphics2D.dispose();
            repaint();
        }
    }

    private class Path implements ActionListener {
        private Cell currentCell;

        public Path(Cell cell) {
            this.currentCell = cell;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Graphics2D graphics2D = bufferedImage.createGraphics();

            Draw.drawPath(graphics2D, currentCell, PATH_COLOR, showPosition);

            currentCell = currentCell.getPrecedingPath();

            if(currentCell.getPrecedingPath() == null) {
                ((Timer) e.getSource()).stop();
            }

            graphics2D.dispose();
            repaint();
        }
    }

    /**
     * A* algorithm using min-heap queue
     *
     * @param start
     * @param end
     */
    private void solve(Cell start, Cell end) {
        if(start != null && end != null) {
            Graphics2D graphics2D = bufferedImage.createGraphics();

            // Init open set
            this.openSet = new Heap(false);
            this.closedSet = new Heap(false);

            // Add start in the open set
            this.openSet.insert(start);

            // While the open set is not empty
            try {
                while(!this.openSet.isEmpty()) {
                    // Get cell having the lowest f score value
                    Cell currentCell = this.openSet.get();

                    // The end has been reached
                    if(currentCell == end) {
                        this.action(SHOWING_PATH_DELAY, new Path(currentCell));
                        return;
                    }

                    this.openSet.remove(currentCell);
                    this.closedSet.insert(currentCell);

                    // Get neighbors
                    final Coordinates[] neighbors = currentCell.getNeighbors();

                    for(int i = 0; i < neighbors.length; i++) {
                        Cell neighbor = getCell(neighbors[i].getRow(), neighbors[i].getColumn());

                        if(!this.closedSet.include(neighbor) && isNotWall(currentCell, neighbor)) {
                            final int tentative_gScore = currentCell.getG() + 1;

                            if(this.openSet.include(neighbor)) {
                                if(tentative_gScore < neighbor.getG()) {
                                    neighbor.setG(tentative_gScore);
                                }
                            } else {
                                neighbor.setG(tentative_gScore);
                                this.openSet.insert(neighbor);
                            }

                            neighbor.setH(heuristic(neighbor, end));
                            neighbor.setF(neighbor.getG() + neighbor.getH());
                            neighbor.setPrecedingPath(currentCell);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            Font font = new Font("TimesRoman", Font.BOLD, 40);

            Draw.addCenteredText(graphics2D, NO_SOLUTION_MESSAGE, 0, 0, CANVAS_WIDTH - Y_AXIS_MARGIN, CANVAS_HEIGHT - xAxisMargin, font, ERROR_COLOR);

            graphics2D.dispose();
            repaint();
        }
    }

    private Cell getRandomCell() {
        final int randomCellRowPos = this.random(row);
        final int randomCellColPos = this.random(col);

        return this.getCell(randomCellRowPos, randomCellColPos);
    }

    private Cell getRandomNeighbor(Cell cell) {
        final Coordinates[] neighbors = cell.getNeighbors();
        final int max = neighbors.length;
        final int randomIndex = this.random(max);
        final Coordinates coordinates = neighbors[(randomIndex < neighbors.length) ? randomIndex : randomIndex - 1];

        return this.getCell(coordinates.getRow(), coordinates.getColumn());
    }

    private int random(final int max) {
        Random rand = new Random();

        return rand.nextInt(max);
    }

    private Coordinates[] getNeighbors(int row, int col, boolean withDiagonal) {
        List<Coordinates> neighbors = new ArrayList<>();

        if((row + 1) < this.row) {
            neighbors.add(new Coordinates(row + 1, col));
        }

        if((row - 1) >= 0) {
            neighbors.add(new Coordinates(row - 1, col));
        }

        if((col + 1) < this.col) {
            neighbors.add(new Coordinates(row, col + 1));
        }

        if((col - 1) >= 0) {
            neighbors.add(new Coordinates(row, col - 1));
        }

        // Diagonal
        if(withDiagonal) {
            if((row + 1) < this.row && (col - 1) >= 0) {
                neighbors.add(new Coordinates(row + 1, col - 1));
            }

            if((row + 1) < this.row && (col + 1) < this.col) {
                neighbors.add(new Coordinates(row + 1, col + 1));
            }

            if((row - 1) >= 0 && (col + 1) < this.col) {
                neighbors.add(new Coordinates(row - 1, col + 1));
            }

            if((row - 1) >= 0 && (col - 1) >= 0) {
                neighbors.add(new Coordinates(row - 1, col - 1));
            }
        }

        return neighbors.toArray(new Coordinates[0]);
    }

    public void action(int delay, ActionListener actionListener) {
        if (this.animationTimer != null && this.animationTimer.isRunning()) {
            this.animationTimer.stop();
        }

        this.bufferedImage = this.getBufferedImage();
        this.animationTimer = new Timer(delay, actionListener);
        this.animationTimer.start();
    }

    private Cell getCell(final int row, final int column) {
        return this.maze[row][column];
    }

    private int heuristic(Cell current, Cell end) {
        final Coordinates currentCoordinates = current.getCoordinates();
        final Coordinates endCoordinates = end.getCoordinates();

        return Math.abs(currentCoordinates.getRow() - endCoordinates.getRow()) + Math.abs(currentCoordinates.getColumn() - endCoordinates.getColumn());
    }

    private boolean isNotWall(Cell current, Cell neighbor) {
        final Coordinates currentCoordinates = current.getCoordinates();
        final Coordinates neighborCoordinates = neighbor.getCoordinates();

        return (neighborCoordinates.getRow() - 1 == currentCoordinates.getRow() && !neighbor.getWall().isTop())
                || (neighborCoordinates.getColumn() - 1 == currentCoordinates.getColumn() && !neighbor.getWall().isLeft())
                || (neighborCoordinates.getRow() + 1 == currentCoordinates.getRow() && !neighbor.getWall().isBottom())
                || (neighborCoordinates.getColumn() + 1 == currentCoordinates.getColumn() && !neighbor.getWall().isRight());
    }

    private BufferedImage getBufferedImage() {
        if(this.bufferedImage == null) {
            this.bufferedImage = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        }

        return this.bufferedImage;
    }
}