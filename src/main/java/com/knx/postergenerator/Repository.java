package com.knx.postergenerator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
public class Repository {

    private static final String DATA = "./data";
    private static final String IMAGES = "./images";
    private static final String DEFAULT_WORKSPACE = "default";
    private List<Product> products;
    private File imageFolder;
    private String currentWorkspace;
    private File dataFolder;

    public void setCurrentWorkspace(String name){
        this.currentWorkspace = name;
        this.products = null;
        loadProducts();
    }

    public String getCurrentWorkspace(){
        return this.currentWorkspace;
    }

    public List<String> getAllWorkspace(){
        ArrayList<String> filesNames = new ArrayList<String>();
        File[] listFiles = dataFolder.listFiles();
        for(int i = 0; i < listFiles.length; i++){
            String name = listFiles[i].getName();
            name = name.substring(0, name.indexOf("."));
            filesNames.add(name);
        }
        return filesNames;
    }

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
            String FilePathName = dataFolder.getAbsolutePath() + "/" +  currentWorkspace + ".json";
            // String FilePathName = "./data/" + currentWorkspace + ".json";
            Object object = new JSONParser().parse(new FileReader(FilePathName));
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
            if(currentWorkspace == null) setCurrentWorkspace(DEFAULT_WORKSPACE);
        
            String FilePathName = dataFolder.getAbsolutePath() + "/" + currentWorkspace + ".json";
            String imagesPathName = imageFolder.getAbsolutePath() + "/" + currentWorkspace;
            File file = new File(FilePathName);
            File imageFolder = new File(imagesPathName);
            try {
                file.createNewFile();
                imageFolder.mkdir();
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
            String FilePathName = dataFolder.getAbsolutePath() + "/" + currentWorkspace + ".json";
            PrintWriter printWriter = new PrintWriter(FilePathName);
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
        String path = imageFolder.getAbsolutePath() + "/" + currentWorkspace;
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
        File imagesFile = new File(IMAGES);
        File dataFile = new File(DATA);
        if(!imagesFile.exists()) imagesFile.mkdir();
        if(!dataFile.exists()) dataFile.mkdir();
        this.imageFolder = imagesFile;
        this.dataFolder = dataFile;
    }

    public void clearCacheImage() {
        String FilePathName = imageFolder.getAbsolutePath() + "/" + currentWorkspace;
        File folder = new File(FilePathName);
        if(!folder.isDirectory()) return;

        ArrayList<String> containedFileNames = new ArrayList<String>(products.size());
        for(Product product : products){
            containedFileNames.add(product.getImgName());
        }
        
        ArrayList<File> toDelele = new ArrayList<File>();
        File[] listFiles = folder.listFiles();
        for(int i = 0; i < listFiles.length; i++){
            if(!containedFileNames.contains(listFiles[i].getName())) {
                toDelele.add(listFiles[i]);
            }
        }

        for(File file : toDelele){
            file.delete();
        }
    }

    public void createWorkspace(String createName) {
        setCurrentWorkspace(createName);
        loadProducts();
    }
}
