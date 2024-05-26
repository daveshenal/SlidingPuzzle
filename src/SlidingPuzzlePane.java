import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.List;


public class SlidingPuzzlePane extends GridPane {
    char[][] map;

    public SlidingPuzzlePane(char[][] selectedMap) {
        this.map = selectedMap;
        drawMap(selectedMap);
    }

    public void drawMap(char[][] map) {
        this.getChildren().clear();

        this.setAlignment(Pos.CENTER);
        this.setHgap(1);
        this.setVgap(1);

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                char cellValue = map[i][j];
                Rectangle rectangle = new Rectangle(30, 30);
                rectangle.setStroke(Color.GRAY);
                rectangle.setStrokeWidth(1);

                // Set color based on cell value
                if (cellValue == '0') {
                    rectangle.setFill(Color.GRAY);
                } else if (cellValue == 'S') {
                    rectangle.setFill(Color.LIGHTGREEN);
                } else if (cellValue == 'F') {
                    rectangle.setFill(Color.INDIANRED);
                } else {
                    rectangle.setFill(Color.LIGHTBLUE);
                }

                this.add(rectangle, j, i);

                if (cellValue == 'S' || cellValue == 'F') {
                    Text cell = new Text(String.valueOf(cellValue));
                    this.add(cell, j, i);
                    GridPane.setHalignment(cell, javafx.geometry.HPos.CENTER);
                    GridPane.setValignment(cell, javafx.geometry.VPos.CENTER);
                }
            }
        }
    }

    public void printPathSteps(List<IceMapNode> path) {
        for (int i = 1; i < path.size() - 1; i++) {
            IceMapNode start = path.get(i);
            IceMapNode end = path.get(i + 1);

            int rowIncrement = Integer.signum(end.getRow() - start.getRow());
            int colIncrement = Integer.signum(end.getColumn() - start.getColumn());

            int row = start.getRow();
            int col = start.getColumn();
            while (row != end.getRow() || col != end.getColumn()) {
                updateCellColor(row, col, Color.YELLOW);
                if (row != end.getRow()) {
                    row += rowIncrement;
                }
                if (col != end.getColumn()) {
                    col += colIncrement;
                }
            }
        }

        // Update the final position
        IceMapNode lastNode = path.get(path.size() - 1);
        updateCellColor(lastNode.getRow(), lastNode.getColumn(), Color.LIGHTGREEN);
    }

    private void updateCellColor(int row, int col, Color color) {
        for (javafx.scene.Node node : this.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                if (node instanceof Rectangle) {
                    ((Rectangle) node).setFill(color);
                }
            }
        }
    }
}

