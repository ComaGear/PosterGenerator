package com.knx.postergenerator;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

public class WorkspaceChooser {
    
    private Stage stage;
    private List<String> workspacesNameList;
    private String selectedWorkspaceString;
    
    public Stage getStage() {
        return stage;
    }

    public String getSelectedWorkspaceString(){
        return selectedWorkspaceString;
    }

    public WorkspaceChooser(List<String> workspacesNameList){
        this.stage = new Stage();
        this.workspacesNameList = workspacesNameList;

        stage.setTitle("wordspace select");
        stage.setScene(setupScene());
        
    }

    private Scene setupScene() {
        ListView<String> listView = new ListView<String>();
        listView.getItems().setAll(workspacesNameList);
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        Button selectButton = new Button("select");
        Button cancelButton = new Button("cancel");
        
        selectButton.setOnAction(e -> {
            String selectedItem = listView.getSelectionModel().getSelectedItem();
            this.selectedWorkspaceString = selectedItem;
            stage.close();
        });
        cancelButton.setOnAction(e -> {
            stage.close();
        });

        HBox buttonPanel = new HBox(selectButton, cancelButton);
        HBox.setMargin(selectButton, new Insets(5, 10, 5, 5));
        HBox.setMargin(cancelButton, new Insets(5, 5, 5, 10));
        
        VBox box = new VBox(listView, buttonPanel);
        return new Scene(box);
    }
}
