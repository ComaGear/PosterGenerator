package com.knx.postergenerator;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WorkspaceCreator {
    private Stage stage;
    private String createName;

    public String getCreateName() {
        return createName;
    }

    public Stage getStage() {
        return stage;
    }

    public WorkspaceCreator(){
        this.stage = new Stage();
        stage.setTitle("input a new workspace name");
        stage.setScene(setupScene());
    }

    private Scene setupScene() {

        TextField nameTextField = new TextField();
        nameTextField.setPrefWidth(200);

        Button createButton = new Button("create");
        Button cancelButton = new Button("cancel");
        
        createButton.setOnAction(e -> {
            this.createName = nameTextField.getText();
            stage.close();
        });
        cancelButton.setOnAction(e -> {
            stage.close();
        });

        HBox buttonPanel = new HBox(createButton, cancelButton);
        HBox.setMargin(createButton, new Insets(5, 10, 5, 5));
        HBox.setMargin(cancelButton, new Insets(5, 5, 5, 10));
        
        VBox box = new VBox(nameTextField, buttonPanel);
        VBox.setMargin(nameTextField, new Insets(20, 5, 20, 5));
        return new Scene(box);
    }
}
