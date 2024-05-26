import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class SlidingPuzzleApp extends Application {

    private char[][] selectedMap;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sliding Puzzle");

        char[][] defaultMap = {
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

        // Right part: SlidingPuzzlePane
        SlidingPuzzlePane puzzlePane = new SlidingPuzzlePane(defaultMap);
        ScrollPane scrollPane = new ScrollPane(puzzlePane);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        // Left part: Map selection and buttons
        VBox leftBox = new VBox(10);
        leftBox.setAlignment(Pos.TOP_LEFT);
        leftBox.setPadding(new Insets(30));

        // Map category selection
        ToggleGroup categoryToggleGroup = new ToggleGroup();
        RadioButton benchmarkRadioButton = new RadioButton("    Benchmark Series");
        benchmarkRadioButton.setUserData("Benchmark Series");
        RadioButton exampleRadioButton = new RadioButton("    Example Puzzles");
        exampleRadioButton.setUserData("Example Puzzles");
        RadioButton customRadioButton = new RadioButton("    Custom Map");
        customRadioButton.setUserData("Custom Map");
        benchmarkRadioButton.setToggleGroup(categoryToggleGroup);
        exampleRadioButton.setToggleGroup(categoryToggleGroup);
        customRadioButton.setToggleGroup(categoryToggleGroup);

        // Map number selection
        ScrollPane mapNumberScrollPane = new ScrollPane();
        VBox mapNumberList = new VBox(10);
        mapNumberList.setPrefWidth(200);

        ToggleGroup mapNumberToggleGroup = new ToggleGroup();

        mapNumberList.setAlignment(Pos.TOP_LEFT);
        mapNumberScrollPane.setContent(mapNumberList);
        mapNumberScrollPane.setFitToWidth(true);
        mapNumberScrollPane.setPrefViewportHeight(450);
        mapNumberScrollPane.setPadding(new Insets(0, 10, 0, 10));

        // Start and reset buttons
        Button loadMapButton = new Button("Get Map");
        Button startButton = new Button("Start");
        Button resetButton = new Button("Reset");
        HBox loadMapButtonBox = new HBox(loadMapButton);
        HBox buttonBox = new HBox(10, startButton, resetButton);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        leftBox.getChildren().addAll(new Label("Select Map Category:"),benchmarkRadioButton, exampleRadioButton, customRadioButton,
                new Label("Select Map Number:"), mapNumberScrollPane,loadMapButtonBox, buttonBox);

        // Add event listeners
        categoryToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                updateMapNumberList(newToggle.getUserData().toString(), mapNumberList, mapNumberToggleGroup);
            }
        });



        // Event handlers for buttons
        loadMapButton.setOnAction(event -> {
            if (categoryToggleGroup.getSelectedToggle() != null) {
                String category = categoryToggleGroup.getSelectedToggle().getUserData().toString();

                if (mapNumberToggleGroup.getSelectedToggle() != null) {
                    try {
                        int selectedMapNumber = (int) mapNumberToggleGroup.getSelectedToggle().getUserData();
                        switch (category) {
                            case "Benchmark Series" -> selectedMap = SlidingPuzzles.selectBenchmarkMap(selectedMapNumber);
                            case "Example Puzzles" -> selectedMap = SlidingPuzzles.selectExampleMap(selectedMapNumber);
                            default -> throw new IllegalStateException("Unexpected value: " + category);
                        }
                        puzzlePane.drawMap(selectedMap);
                    } catch (IOException ex) {
                        ex.printStackTrace(); // Handle exception appropriately
                    }
                } else {
                    displayErrorMessage("Please select a map number.");
                }

            } else {
                // Display an error message if no category is selected
                displayErrorMessage("Please select a map category.");
            }

        });
        startButton.setOnAction(event -> {
            IceMap iceMap = new IceMap(selectedMap);
            List<IceMapNode> path = SlidingPuzzles.findPath(iceMap.getStartNode(), iceMap.getEndNode());
            puzzlePane.printPathSteps(path);
        });
        resetButton.setOnAction(event -> {
            // Reset button action
        });

        // Full layout: BorderPane
        BorderPane root = new BorderPane();
        root.setCenter(scrollPane);
        root.setLeft(leftBox);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    private void updateMapNumberList(String category, VBox mapNumberList, ToggleGroup mapNumberToggleGroup) {
        mapNumberList.getChildren().clear();
        int numberOfMaps = switch (category) {
            case "Benchmark Series" -> 9;
            case "Example Puzzles" -> 25;
            default -> 0;
        };
        for (int i = 1; i <= numberOfMaps; i++) {
            RadioButton radioButton = new RadioButton( category+" Map " + i);
            radioButton.setToggleGroup(mapNumberToggleGroup);
            radioButton.setUserData(i);
            mapNumberList.getChildren().add(radioButton);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void displayErrorMessage(String message) {
        // Display an error message dialog
        Stage errorWindow = new Stage();
        errorWindow.initModality(Modality.APPLICATION_MODAL);
        errorWindow.setTitle("Error");

        Label errorMessage = new Label(message);
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> errorWindow.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(errorMessage, closeButton);
        layout.setAlignment(javafx.geometry.Pos.CENTER);

        Scene scene = new Scene(layout, 200, 100);
        errorWindow.setScene(scene);
        errorWindow.showAndWait();
    }
}
