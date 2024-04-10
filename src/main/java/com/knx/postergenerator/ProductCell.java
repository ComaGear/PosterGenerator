package com.knx.postergenerator;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.LinkedList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ProductCell<T> extends ListCell<Product> {
    
    
    @Override
    protected void updateItem(Product product, boolean empty) {
        if(product == null || empty) {
            setText("");
            setGraphic(null);
            return;
        }
        this.setItem(product);

        final int fitSize = 50;
        ImageView imageView = new ImageView();
        VBox imageVBox = new VBox(imageView);
        imageVBox.setPrefHeight(fitSize);
        imageVBox.setPrefWidth(fitSize);
        String path = product.getImgLocation() + "/" + product.getImgName();
        Image image = null;
        try {
            image = new Image(new FileInputStream(new File(path)));
        } catch (FileNotFoundException e) {
            new Alert(AlertType.ERROR, e.getStackTrace().toString(), ButtonType.CLOSE).show();
        }
        imageView.setImage(image);
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

        Font font = new Font(18);
        Text titleText = new Text(product.getTitle());
        Text sizeText = new Text(product.getSizeString());
        Text priceText = new Text(product.getPrice().toString());
        titleText.setFont(font);
        sizeText.setFont(font);
        priceText.setFont(font);
        titleText.setWrappingWidth(200);
        priceText.setWrappingWidth(80);
        sizeText.setWrappingWidth(150);

        HBox hBox = new HBox(imageVBox, titleText, priceText, sizeText);
        hBox.setPadding(new Insets(2, 5, 2, 5));
        hBox.setMargin(titleText, new Insets(8, 8, 8, 8));
        hBox.setMargin(sizeText, new Insets(8, 8, 8, 8));
        hBox.setMargin(priceText, new Insets(8, 8, 8, 8));
        setGraphic(hBox);
    }

    public ProductCell() {
        setOnDragDetected(event -> {
            
            if(getItem() == null) return;

            ObservableList<Product> items = getListView().getItems();

            int index = items.indexOf(getItem());
            
            Dragboard dragBoard = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent clipboardContent = new ClipboardContent();
            // clipboardContent.put(new DataFormat("index"), Integer.toString(index));
            clipboardContent.putString(Integer.toString(index));
            dragBoard.setContent(clipboardContent);

            event.consume();
        });

        setOnDragOver(event -> {
            if (event.getGestureSource() != this &&
                    event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }

                event.consume();
        });

        setOnDragEntered(event -> {
            if(event.getGestureSource() != this && event.getDragboard().hasString()) {
                setOpacity(0.3);
            }
        });

        setOnDragExited(event -> {
            if(event.getGestureSource() != this && event.getDragboard().hasString()) {
                setOpacity(1.0);
            }
        });

        setOnDragDone(DragEvent::consume);

    }
}
