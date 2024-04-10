package com.knx.postergenerator;

import java.util.LinkedList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Dragboard;
import javafx.util.Callback;

public class ProductCellFactory implements Callback<ListView<Product>, ListCell<Product>> {

    private Repository repository;

    @Override
    public ListCell<Product> call(ListView<Product> param) {

        ProductCell<Product> productCell = new ProductCell<Product>();

        productCell.setOnDragDropped(event -> {
            if(productCell.getItem() == null) return;

            ObservableList<Product> items = productCell.getListView().getItems();
            LinkedList<Product> linkedList = new LinkedList<Product>(items); 

            Dragboard dragboard = event.getDragboard();
            int index = Integer.parseInt(dragboard.getString());

            List<Product> products = repository.loadProducts();
            Product temp = linkedList.get(index);
            linkedList.remove(index);
            products.remove(index);
            int thisIndex = items.indexOf(productCell.getItem());
            linkedList.add(thisIndex, temp);
            products.add(thisIndex, temp);
            productCell.getListView().getItems().setAll(linkedList);

            event.setDropCompleted(true);
            event.consume();

        });

        MenuItem menuItem = new MenuItem("delete");
        menuItem.setOnAction(event ->{
            // TODO
        });
        ContextMenu contextMenu = new ContextMenu(menuItem);
        productCell.setContextMenu(contextMenu);

        return productCell;
    }

    public ProductCellFactory(Repository repository){
        this.repository = repository;
    }

}
