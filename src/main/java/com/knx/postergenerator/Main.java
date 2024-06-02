package com.knx.postergenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
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

        stage.setOnCloseRequest(event -> {
            repository.saveProducts();
            repository.clearCacheImage();
            stage.close();
        });
    }

    private Scene setupScene() throws FileNotFoundException {

        InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream("images/undefined.jpg");
        Image image = new Image(systemResourceAsStream);
        ImageView imageView = new ImageView();
        VBox imageVBox = new VBox(imageView);
        final int fitSize = 200;
        imageVBox.setPrefWidth(fitSize);
        imageVBox.setPrefHeight(fitSize);
        int height = 0;
        int width = 0;
        double radio = image.widthProperty().doubleValue() / image.heightProperty().doubleValue();
        // smaller than 1 was protrait, more than 1 was landspace.
        if (radio < 1) {
            height = fitSize;
            width = (int) (fitSize * radio);
            imageVBox.setPadding(new Insets(0, (fitSize - width)/2, 0, (fitSize - width)/2));
        } else if(radio > 1) {
            height = (int) (fitSize / radio);
            width = fitSize;
            imageVBox.setPadding(new Insets((fitSize - height)/2, 0, (fitSize - height)/2, 0));
        } else {
            height = fitSize;
            width = fitSize;
            imageVBox.setPadding(new Insets(0));
        }
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        imageView.setImage(image);

        imageView.setOnDragOver((DragEvent event) -> {
            if (event.getGestureSource() != imageView
                    && (event.getDragboard().hasImage() || event.getDragboard().hasFiles())) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        imageView.setOnDragDropped((DragEvent event) -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasImage() || dragboard.hasFiles()) {
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(dragboard.getFiles().get(0));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (fileInputStream == null) {
                    event.consume();
                    return;
                }
                Image newImage = new Image(fileInputStream);
                imageView.setImage(newImage);
                int newHeight = 0;
                int newWidth = 0;
                double newRadio = newImage.widthProperty().doubleValue() / newImage.heightProperty().doubleValue();
                if (newRadio < 1) {
                    newHeight = fitSize;
                    newWidth = (int) (fitSize * newRadio);
                    imageVBox.setPadding(new Insets(0, (fitSize - newWidth)/2, 0, (fitSize - newWidth)/2));
                } else if(newRadio > 1) {
                    newHeight = (int) (fitSize / newRadio);
                    newWidth = fitSize;
                    imageVBox.setPadding(new Insets((fitSize - newHeight)/2, 0, (fitSize - newHeight)/2, 0));
                } else {
                    newHeight = fitSize;
                    newWidth = fitSize;
                    imageVBox.setPadding(new Insets(0));
                }
                imageView.setFitHeight(newHeight);
                imageView.setFitWidth(newWidth);
                
                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });


        MenuButton sizeMenuButton = new MenuButton();
        sizeMenuButton.setText("square");
        sizeMenuButton.setPrefWidth(300);
        MenuItem squareMenuItem = new MenuItem("square");
        MenuItem landspaceMediumMenuItem = new MenuItem("landspaceMedium");
        MenuItem landspaceLageMenuItem = new MenuItem("landspaceLage");
        MenuItem portraitMenuItem = new MenuItem("portrait");
        EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MenuItem menuItem = (MenuItem) event.getSource();
                sizeMenuButton.setText(menuItem.getText());
            }
        };
        squareMenuItem.setOnAction(eventHandler);
        landspaceMediumMenuItem.setOnAction(eventHandler);
        landspaceLageMenuItem.setOnAction(eventHandler);
        portraitMenuItem.setOnAction(eventHandler);
        sizeMenuButton.getItems().setAll(squareMenuItem, landspaceMediumMenuItem, landspaceLageMenuItem,
                portraitMenuItem);

        TextField titleField = new TextField();
        titleField.setPrefWidth(300);
        TextField priceField = new TextField();
        priceField.setPrefWidth(300);
        Text titleText = new Text("Title :");
        Text sizeText = new Text("Size :");
        Text priceText = new Text("Price :");
        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*"))
                newValue = newValue.replaceAll("[^\\d.]", "");
            priceField.setText(newValue);
        });

        Button saveButton = new Button("Save");
        saveButton.setPadding(new Insets(20, 100, 20, 100));
        saveButton.setPrefWidth(300);
        saveButton.setOnAction(event -> {
            Image saveImage = imageView.getImage();
            Product product = new Product();
            product.setTitle(titleField.getText());
            product.setPrice(Double.parseDouble(priceField.getText()));
            product.setSize(sizeMenuButton.getText());

            repository.saveImage(saveImage, product);
            repository.addProduct(product);
            listView.getItems().add(product);
            // listView.setItems(FXCollections.observableArrayList(repository.loadProducts()));
            // listView.getItems().setAll(repository.loadProducts());
        });

        VBox fieldPanel = new VBox(titleText, titleField, sizeText, sizeMenuButton, priceText, priceField, saveButton);
        VBox.setMargin(saveButton, new Insets(10, 0, 0, 0));
        fieldPanel.setPadding(new Insets(10));

        HBox productEditPanel = new HBox(imageVBox, fieldPanel);
        productEditPanel.setPadding(new Insets(20));

        repository = new Repository();
        listView = new ListView<Product>();
        listView.setCellFactory(new ProductCellFactory(repository));
        listView.getItems().addAll(repository.loadProducts());

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(App.getProperty(App.DEFAULT_INITIAL_DIRECTORY)));
        Button processButton = new Button("process");
        processButton.setPrefWidth(500);
        processButton.setPadding(new Insets(5, 0, 5, 0));
        processButton.setOnAction(event -> {
            directoryChooser.setTitle("select where to save the output");
            File folder = directoryChooser.showDialog(stage);
            App.setProperty(App.DEFAULT_INITIAL_DIRECTORY, folder.getAbsolutePath());
            if(!folder.exists()) return;

            App app = new App();
            try {
                app.saveResult(folder.getAbsolutePath(), app.render(repository.loadProducts()));
            } catch (IOException e) {
                e.printStackTrace();
            }

        });


        VBox vBox = new VBox(setupMenuBar(), productEditPanel, listView, processButton);
        vBox.setAlignment(Pos.CENTER);
        VBox.setMargin(processButton, new Insets(5));

        return new Scene(vBox);
    }

    private MenuBar setupMenuBar(){
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("worspace");
        menuBar.getMenus().add(menu);

        MenuItem loadWorkspaceMenuButton = new MenuItem("load workspace");
        MenuItem createWorkspaceMenuButton = new MenuItem("create workspace");
        menu.getItems().add(loadWorkspaceMenuButton);
        menu.getItems().add(createWorkspaceMenuButton);

        loadWorkspaceMenuButton.setOnAction(e ->{
            WorkspaceChooser workspaceChooser = new WorkspaceChooser(repository.getAllWorkspace());
            workspaceChooser.getStage().showAndWait();
            String selectedWorkspaceString = workspaceChooser.getSelectedWorkspaceString();
            repository.setCurrentWorkspace(selectedWorkspaceString);
            listView.getItems().clear();
            listView.getItems().setAll(repository.loadProducts());
        });

        createWorkspaceMenuButton.setOnAction(e -> {
            WorkspaceCreator workspaceCreator = new WorkspaceCreator();
            workspaceCreator.getStage().showAndWait();
            String createName = workspaceCreator.getCreateName();
            repository.createWorkspace(createName);
            listView.getItems().clear();
            listView.getItems().setAll(repository.loadProducts());
        });


        return menuBar;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
