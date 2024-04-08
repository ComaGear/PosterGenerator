package com.knx.postergenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage stage;
    private Repository repository;
    private ListView<Product> listView;

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        this.stage = new Stage();

        stage.setScene(setupScene());
        stage.setTitle("Poster Generator");
        stage.show();
    }

    private Scene setupScene() throws FileNotFoundException {
        
        InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream("images/undefined.jpg");
        Image image = new Image(systemResourceAsStream);
        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        imageView.setImage(image);

        imageView.setOnDragOver((DragEvent event) ->{
            if(event.getGestureSource() != imageView && (event.getDragboard().hasImage() || event.getDragboard().hasFiles())){
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        imageView.setOnDragDropped((DragEvent event) ->{
            Dragboard dragboard = event.getDragboard();
            if(dragboard.hasImage() || dragboard.hasFiles()){
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(dragboard.getFiles().get(0));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if(fileInputStream == null ) {
                    event.consume();
                    return;
                }
                imageView.setImage(new Image(fileInputStream));
                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });

        TextField titleField = new TextField();
        titleField.setPrefWidth(300);
        TextField subtitleField = new TextField();
        subtitleField.setPrefWidth(300);
        TextField priceField = new TextField();
        priceField.setPrefWidth(300);
        Text titleText = new Text("Title :");
        Text subtitleText = new Text("Subtitle :");
        Text priceText = new Text("Price :");
        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("\\d*"))
                newValue = newValue.replaceAll("[^\\d.]", "");
                priceField.setText(newValue);
        });

        Button saveButton = new Button("Save");
        saveButton.setPadding(new Insets(20, 100, 20 , 100));
        saveButton.setPrefWidth(300);
        saveButton.setOnAction(event -> {
            Image saveImage = imageView.getImage();
            Product product = new Product();

            repository.saveImage(saveImage, product);
            repository.addProduct(product);
            listView.getItems().clear();
            listView.getItems().setAll(repository.loadProducts());
        });

        VBox fieldPanel = new VBox(titleText, titleField, subtitleText, subtitleField, priceText, priceField, saveButton);
        fieldPanel.setMargin(saveButton, new Insets(10, 0, 0, 0));
        fieldPanel.setPadding(new Insets(10));

        HBox productEditPanel = new HBox(imageView, fieldPanel);
        productEditPanel.setPadding(new Insets(20));
        

        repository = new Repository();
        listView = new ListView<Product>();
        listView.setCellFactory(new ProductCellFactory());
        listView.getItems().addAll(repository.loadProducts());

        
        VBox vBox = new VBox(productEditPanel, listView);

        return new Scene(vBox);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
    
}
