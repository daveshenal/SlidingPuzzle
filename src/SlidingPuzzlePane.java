import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;


public class SlidingPuzzlePane extends GridPane {
    private char[][] map;
    private String selectedCellType = "Start";
    private int startRow,startCol,endRow,endCol;
    private Boolean startCellAdded = false;
    private Boolean endCellAdded = false;

    public SlidingPuzzlePane(char[][] selectedMap) {
        this.map = selectedMap;
        drawMap();
    }

    public void setMap(char[][] map) {
        this.map = map;
    }

    public void drawMap() {
        this.getChildren().clear();

        this.setAlignment(Pos.CENTER);
        this.setHgap(1);
        this.setVgap(1);

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                char cellValue = map[i][j];
                ImageView imageView = new ImageView();
                imageView.setFitWidth(30);
                imageView.setFitHeight(30);

                switch (cellValue) {
                    case '0' -> imageView.setImage(loadImage("images/cave-painting.png"));
                    case 'S' -> imageView.setImage(loadImage("images/igloo.png"));
                    case 'F' -> imageView.setImage(loadImage("images/castle-tower2.png"));
                    default -> imageView.setImage(loadImage("images/ice2.png"));
                }

                this.add(imageView, j, i);
            }
        }
    }

    private Image loadImage(String path) {
        try {
            FileInputStream inputStream = new FileInputStream(new File(path));
            return new Image(inputStream);
        } catch (FileNotFoundException e) {
            System.err.println("Failed to load image at path: " + path);
            return null;
        }
    }

    public void printPathSteps() {
        IceMap iceMap = new IceMap(map);
        List<IceMapNode> path = SlidingPuzzles.findPath(iceMap.getStartNode(), iceMap.getEndNode());

        if (path.size() == 1) {
            SlidingPuzzleApp.displayErrorMessage("No Path found!");
            System.out.println("No Path found!");
            return;
        }

        Thread pathPrintingThread = new Thread(() -> {
            for (int i = 0; i < path.size() - 1; i++) {
                IceMapNode start = path.get(i);
                IceMapNode end = path.get(i + 1);

                int rowIncrement = Integer.signum(end.getRow() - start.getRow());
                int colIncrement = Integer.signum(end.getColumn() - start.getColumn());

                int row, col;
                if (i == 0) {
                    row = start.getRow() + rowIncrement;
                    col = start.getColumn() + colIncrement;
                } else {
                    row = start.getRow();
                    col = start.getColumn();
                }
                int prevRow = row;
                int prevCol = col;
                while (row != end.getRow() || col != end.getColumn()) {
                    updateCellImage(prevRow, prevCol, "images/ice5.png");
                    updateCellImage(row, col, "images/cold.png");
                    if (row != end.getRow()) {
                        prevRow = row;
                        row += rowIncrement;
                    }
                    if (col != end.getColumn()) {
                        prevCol = col;
                        col += colIncrement;
                    }
                    try {
                        Thread.sleep(100); // delay time in milliseconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                updateCellImage(prevRow, prevCol, "images/ice5.png");
            }

            // Update the final position
            IceMapNode lastNode = path.get(path.size() - 1);
            updateCellImage(lastNode.getRow(), lastNode.getColumn(), "images/castle-tower.png");
        });

        pathPrintingThread.start();
    }

    private void updateCellImage(int row, int col, String imagePath) {
        for (javafx.scene.Node node : this.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                if (node instanceof ImageView) {
                    ((ImageView) node).setImage(loadImage(imagePath));
                }
            }
        }
    }

    public void drawCustomMap(int rows, int column) {
        this.map =  new char[rows][column];
        this.getChildren().clear();

        this.setAlignment(Pos.CENTER);
        this.setHgap(1);
        this.setVgap(1);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < column; j++) {
                this.map[i][j] = '.';
                Rectangle rectangle = new Rectangle(30, 30);
                rectangle.setStroke(Color.GRAY);
                rectangle.setStrokeWidth(1);

                rectangle.setFill(Color.LIGHTBLUE);

                // Add event handling for mouse click
                int finalI = i;
                int finalJ = j;
                rectangle.setOnMouseClicked(event -> handleGridClick(rectangle, finalI, finalJ));

                this.add(rectangle, j, i);
            }
        }
    }

    public void setSelectedCellType(String cellType) {
        this.selectedCellType = cellType;
    }

    // Method to handle grid click event
    private void handleGridClick(Rectangle rectangle, int row, int col) {
        char oldCellValue = map[row][col];
        char newCellValue = selectedCellValue();

        if (newCellValue == '0') {
            if(oldCellValue == newCellValue){
                map[row][col] = '.';
                rectangle.setFill(Color.LIGHTBLUE);
            }
            else {
                map[row][col] = '0';
                rectangle.setFill(Color.GRAY);
            }

        } else if (newCellValue == 'S' || newCellValue == 'F') {
            if(oldCellValue != newCellValue){
                if(newCellValue == 'S'){
                    if(!startCellAdded){
                        startCellAdded = true;
                    }
                    else {
                        map[startRow][startCol] = '.';
                        getRectangleByRowAndColumn(startRow,startCol).setFill(Color.LIGHTBLUE);

                    }
                    startRow = row;
                    startCol = col;
                    map[row][col] = 'S';
                    rectangle.setFill(Color.LIGHTGREEN);
                }
                else {
                    if(!endCellAdded){
                        endCellAdded = true;
                    }
                    else {
                        map[endRow][endCol] = '.';
                        getRectangleByRowAndColumn(endRow,endCol).setFill(Color.LIGHTBLUE);
                    }
                    endRow = row;
                    endCol = col;
                    map[row][col] = 'F';
                    rectangle.setFill(Color.INDIANRED);

                }
            }
        }

    }

    private char selectedCellValue() {
        switch (selectedCellType) {
            case "Start" -> {return 'S';}
            case "Finish" -> {return 'F';}
            case "Rock" -> {return '0';}
        }
        return '.';
    }

    public Rectangle getRectangleByRowAndColumn(int row, int column) {
        for (Node node : this.getChildren()) {
            if (GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) == row &&
                    GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) == column)
                if(node instanceof Rectangle) {
                    return (Rectangle) node;
                }
        }
        return null;
    }

    public Boolean getStartCellAdded() {
        return startCellAdded;
    }

    public Boolean getEndCellAdded() {
        return endCellAdded;
    }
}

