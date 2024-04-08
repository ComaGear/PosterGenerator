package com.knx.postergenerator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;

public class Repository {

    private List<Product> products;
    private File imageFolder;

    public void addProduct(Product product){
        products.add(product);
    }

    public void deleteProduct(Product product){
        products.remove(product);
    }
    
    public List<Product> loadProducts(){
        if(products != null) return products;
        
        this.products = new LinkedList<Product>();

        try {
            Object object = new JSONParser().parse(new FileReader("./PosterReposition.json"));
            JSONObject json = (JSONObject) object;

            JSONArray productList = (JSONArray)json.get("products");
            Iterator<Object> iterator = productList.iterator();
            while(iterator.hasNext()){
                Object next = iterator.next();
                Map<String, String> map = (Map<String, String>) next;

                String title = map.get("title");
                String price = map.get("price");
                String imgLocation = map.get("imgLocation");
                String imgName = map.get("imgName");
                String size = map.get("size");

                Product product = new Product();
                product.setTitle(title);
                product.setPrice(Double.parseDouble(price));
                product.setImgLocation(imgLocation);
                product.setImgName(imgName);
                product.setSize(size);

                products.add(product);
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            File file = new File("./PosterReposition.json");
            try {
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return products;
    }

    public void saveProducts(){
        JSONObject jsonObject = new JSONObject();
        
        JSONArray jsonArray = new JSONArray();
        for(Product product : products){
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("title", product.getTitle());
            hashMap.put("price", product.getPrice().toString());
            hashMap.put("imgLocation", product.getImgLocation());
            hashMap.put("imgName", product.getImgName());
            hashMap.put("size", product.getSizeString());

            jsonArray.add(hashMap);
        }
        jsonObject.put("products", jsonArray);


        try {
            PrintWriter printWriter = new PrintWriter("./PosterReposition.json");
            printWriter.write(jsonObject.toJSONString());
            printWriter.flush();
            printWriter.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void saveImage(Image saveImage, Product product) {
        if(imageFolder == null || !imageFolder.isDirectory() || !imageFolder.exists()) return;
        
        int i = (int) (Math.random() * 1000000);
        String path = imageFolder.getAbsolutePath();
        String name = Integer.toString(i) + ".png";
        try {
            File file = new File(path + "/" + name);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(saveImage, null);
            ImageIO.write(bufferedImage, "png", fileOutputStream);

            fileOutputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        product.setImgLocation(path);
        product.setImgName(name);
    }

    public Repository(){
        File file = new File("./data");
        if(!file.exists()) file.mkdir();
        this.imageFolder = file;
    }
}
