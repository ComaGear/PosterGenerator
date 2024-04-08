package com.knx.postergenerator;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.LinkedList;

public class ProductCell<T> extends ListCell<Product> {
    
    
    @Override
    protected void updateItem(Product product, boolean empty) {
        if(product == null || empty) {
            setText("");
            setGraphic(null);
            return;
        }

        ImageView imageView = new ImageView();
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);

        Text titleText = new Text(product.getTitle());
        Text sizeText = new Text(product.getTitle());
        Text priceText = new Text(product.getTitle());

        HBox hBox = new HBox(imageView, titleText, sizeText, priceText);
        hBox.setPadding(new Insets(0, 20, 0, 20));
        setGraphic(hBox);
    }

    public ProductCell() {
        setOnDragDetected(event -> {
            if(getItem() == null) return;

            ObservableList<Product> items = getListView().getItems();

            int index = items.indexOf(getItem());
            
            Dragboard dragBoard = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.put(new DataFormat("index"), Integer.toString(index));
            dragBoard.setContent(clipboardContent);

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

        setOnDragDropped(event -> {
            if(getItem() == null) return;

            ObservableList<Product> items = getListView().getItems();
            LinkedList<Product> linkedList = new LinkedList<Product>(items); 

            Dragboard dragboard = event.getDragboard();
            int index = Integer.parseInt(dragboard.getString());

            Product temp = linkedList.get(index);
            linkedList.remove(index);
            int thisIndex = linkedList.indexOf(this.getItem());
            linkedList.add(thisIndex, temp);
            getListView().getItems().setAll(linkedList);

            event.setDropCompleted(true);
            event.consume();

        });

        setOnDragDone(DragEvent::consume);

    }
}
