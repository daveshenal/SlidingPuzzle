import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class MapSelectionDialog {

    private char[][] selectedMap;

    public char[][] getSelectedMap() {
        return selectedMap;
    }

    public void display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Select Map");

        // Create radio buttons for map categories
        ToggleGroup categoryToggleGroup = new ToggleGroup();
        RadioButton benchmarkRadioButton = new RadioButton("Benchmark Series");
        benchmarkRadioButton.setUserData("Benchmark Series"); // Set userData
        RadioButton exampleRadioButton = new RadioButton("Example Puzzles");
        exampleRadioButton.setUserData("Example Puzzles"); // Set userData
        RadioButton customRadioButton = new RadioButton("Custom Map");
        customRadioButton.setUserData("Custom Map"); // Set userData
        benchmarkRadioButton.setToggleGroup(categoryToggleGroup);
        exampleRadioButton.setToggleGroup(categoryToggleGroup);
        customRadioButton.setToggleGroup(categoryToggleGroup);

        // Create a button to confirm category selection
        Button confirmCategoryButton = new Button("Confirm Category");
        confirmCategoryButton.setOnAction(e -> {
            if (categoryToggleGroup.getSelectedToggle() != null) {
                selectMapNumber(window, categoryToggleGroup.getSelectedToggle().getUserData().toString());
            } else {
                // Display an error message if no category is selected
                displayErrorMessage("Please select a map category.");
            }
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(
                new Label("Select a map category:"),
                benchmarkRadioButton,
                exampleRadioButton,
                customRadioButton,
                confirmCategoryButton
        );
        layout.setPadding(new Insets(10));
        layout.setAlignment(javafx.geometry.Pos.CENTER);

        Scene scene = new Scene(layout, 300, 200);
        window.setScene(scene);
        window.showAndWait();
    }

    private void selectMapNumber(Stage window, String category) {
        Stage mapNumberWindow = new Stage();
        mapNumberWindow.initModality(Modality.APPLICATION_MODAL);
        mapNumberWindow.setTitle("Select Map Number");

        VBox layout = new VBox(10);
        Label label = new Label("Select a map number:");
        ToggleGroup mapNumberToggleGroup = new ToggleGroup();

        int numberOfMaps = switch (category) {
            case "Benchmark Series" -> 9;
            case "Example Puzzles" -> 25;
            default -> 0;
        };

        VBox radioButtonsBox = new VBox(10);
        radioButtonsBox.setPadding(new Insets(10));

        for (int i = 1; i <= numberOfMaps; i++) {
            RadioButton radioButton = new RadioButton("Map " + i);
            radioButton.setUserData(i);
            radioButton.setToggleGroup(mapNumberToggleGroup);
            radioButtonsBox.getChildren().add(radioButton);
        }

        ScrollPane scrollPane = new ScrollPane(radioButtonsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(200);

        Button confirmMapNumberButton = new Button("Confirm Map Number");
        confirmMapNumberButton.setOnAction(e -> {
            if (mapNumberToggleGroup.getSelectedToggle() != null) {
                try {
                    int selectedMapNumber = (int) mapNumberToggleGroup.getSelectedToggle().getUserData();
                    switch (category) {
                        case "Benchmark Series" -> selectedMap = SlidingPuzzles.selectBenchmarkMap(selectedMapNumber);
                        case "Example Puzzles" -> selectedMap = SlidingPuzzles.selectExampleMap(selectedMapNumber);
                        default -> throw new IllegalStateException("Unexpected value: " + category);
                    }
                    mapNumberWindow.close();
                    window.close();
                } catch (IOException ex) {
                    ex.printStackTrace(); // Handle exception appropriately
                }
            } else {
                displayErrorMessage("Please select a map number.");
            }
        });
        layout.getChildren().addAll(label, scrollPane, confirmMapNumberButton);

        layout.setPadding(new Insets(10));
        layout.setAlignment(javafx.geometry.Pos.CENTER);

        Scene scene = new Scene(layout, 300, 200);
        mapNumberWindow.setScene(scene);
        mapNumberWindow.showAndWait();
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


