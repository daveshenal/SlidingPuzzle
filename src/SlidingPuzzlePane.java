import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class SlidingPuzzlePane extends GridPane {

    private char[][] map = {
            {'.', '.', '.', '.','0', '.','.', '.', '.', '.'},
            {'.', '.', '.', '.','.', '.','.', '.', '.', '.'},
            {'0', '.', '.', '.','.', '.','.', '.', '.', '.'},
            {'.', '.', '.', '.','.', 'S','0', '.', '.', '.'},
            {'.', '.', '.', '.','.', '0','.', '.', '.', '.'},
            {'.', '.', '.', '.','.', '.','.', '.', '.', '.'},
            {'.', '.', '0', '.','.', '.','.', '.', '.', '.'},
            {'.', '.', '.', '.','0', '0','.', '.', '.', '.'},
            {'.', '.', '.', 'F','.', '.','.', '.', '.', '.'},
            {'.', '0', '.', '.','0', '.','0', '.', '.', '.'},
            {'.', '0', '.', '.','.', '.','.', '.', '.', '.'},
            {'.', '.', '.', '.','.', '.','.', '0', '.', '.'},
            {'0', '.', '.', '.','.', '.','.', '.', '.', '.'},
            {'.', '.', '.', '.','.', '.','.', '.', '.', '.'},
            {'.', '.', '0', '.','.', '.','.', '.', '.', '.'},
    };

    public SlidingPuzzlePane() {
        drawMap();
    }

    private void drawMap() {
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
                    rectangle.setFill(Color.BLACK);
                } else if (cellValue == 'S' || cellValue == 'F') {
                    rectangle.setFill(Color.LIGHTGREEN);
                } else {
                    rectangle.setFill(Color.WHITE);
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
}

