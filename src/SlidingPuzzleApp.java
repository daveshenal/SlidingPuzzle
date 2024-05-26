import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SlidingPuzzleApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sliding Puzzle");

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);

        // Add the main content here (e.g., game board, controls)
        SlidingPuzzlePane puzzlePane = new SlidingPuzzlePane();
        root.setCenter(puzzlePane);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
