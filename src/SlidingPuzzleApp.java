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


public class SlidingPuzzleApp extends Application {

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
                {'.', '.', '0', '0','.', '.','.', '.', '.', '.'},
                {'.', '0', '.', '.','0', '0','.', '.', '.', '.'},
                {'.', '.', '.', 'F','.', '.','.', '.', '.', '.'},
                {'.', '0', '.', '.','0', '.','0', '.', '.', '.'},
                {'.', '0', '.', '.','.', '.','.', '0', '.', '.'},
                {'.', '.', '.', '.','.', '.','.', '0', '.', '.'},
                {'0', '.', '.', '.','.', '.','0', '.', '.', '.'},
                {'.', '.', '0', '.','.', '.','.', '.', '.', '.'},
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
        Label selectOrCreate = new Label("Select Map Number:");
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
        resetButton.setVisible(false);
        HBox loadMapButtonBox = new HBox(loadMapButton);
        HBox buttonBox = new HBox(10, startButton, resetButton);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        leftBox.getChildren().addAll(new Label("Select Map Category:"),benchmarkRadioButton, exampleRadioButton, customRadioButton,
                selectOrCreate, mapNumberScrollPane,loadMapButtonBox, buttonBox);

        // Add event listeners
        categoryToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                if(newToggle.getUserData().toString().equals("Custom Map")) {
                    selectOrCreate.setText("Select Custom Map Properties:");
                    customMapEditor(mapNumberList, puzzlePane, startButton);
                    startButton.setVisible(false);
                    loadMapButton.setVisible(false);
                    puzzlePane.drawCustomMap(10,10);
                }
                else{
                    selectOrCreate.setText("Select Map Number:");
                    startButton.setVisible(true);
                    loadMapButton.setVisible(true);
                    updateMapNumberList(newToggle.getUserData().toString(), mapNumberList, mapNumberToggleGroup);
                }
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
                            case "Benchmark Series" -> puzzlePane.setMap(SlidingPuzzles.selectBenchmarkMap(selectedMapNumber));
                            case "Example Puzzles" -> puzzlePane.setMap(SlidingPuzzles.selectExampleMap(selectedMapNumber));
                            default -> throw new IllegalStateException("Unexpected value: " + category);
                        }
                        puzzlePane.drawMap();
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
            puzzlePane.printPathSteps();
            resetButton.setVisible(true);
        });
        resetButton.setOnAction(event -> {
            puzzlePane.drawMap();
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

    private void customMapEditor(VBox mapNumberList, SlidingPuzzlePane puzzlePane, Button startButton) {
        mapNumberList.getChildren().clear();
        Label rowsLabel = new Label("Rows:");
        Spinner<Integer> rowsSpinner = new Spinner<>(3, 20, 10);
        rowsSpinner.setEditable(true);

        Label colsLabel = new Label("Columns:");
        Spinner<Integer> colsSpinner = new Spinner<>(3, 20, 10);
        colsSpinner.setEditable(true);

        Button createMapGridBtn = new Button("Create Map Grid");

        createMapGridBtn.setOnAction(event -> {
            puzzlePane.drawCustomMap(rowsSpinner.getValue(),colsSpinner.getValue());
        });

        // Cell type selection
        ToggleGroup categoryToggleGroup = new ToggleGroup();
        RadioButton startRadioButton = new RadioButton("  Start");
        startRadioButton.setUserData("Start");
        RadioButton finishRadioButton = new RadioButton("  Finish");
        finishRadioButton.setUserData("Finish");
        RadioButton rockRadioButton = new RadioButton("  Rock");
        rockRadioButton.setUserData("Rock");

        startRadioButton.setToggleGroup(categoryToggleGroup);
        finishRadioButton.setToggleGroup(categoryToggleGroup);
        rockRadioButton.setToggleGroup(categoryToggleGroup);

        startRadioButton.setSelected(true);

        categoryToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                String selectedType = newToggle.getUserData().toString();
                puzzlePane.setSelectedCellType(selectedType);
            }
        });

        Button finalizeMapBtn = new Button("Done");

        finalizeMapBtn.setOnAction(event -> {
            if(!puzzlePane.getStartCellAdded()){
                displayErrorMessage("Please select the starting point.");
            }
            else if(!puzzlePane.getEndCellAdded()){
                displayErrorMessage("Please select a map finishing point.");
            }
            else {
                puzzlePane.drawMap();
                startButton.setVisible(true);
            }
        });

        mapNumberList.getChildren().addAll(rowsLabel,rowsSpinner,colsLabel,colsSpinner,createMapGridBtn,
                new Label("Select Cell Type:"),startRadioButton,finishRadioButton,rockRadioButton,
                finalizeMapBtn);



    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void displayErrorMessage(String message) {
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
