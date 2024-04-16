package com.knx.postergenerator;

import java.util.List;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class App 
{
    private static final String POSTER_GENERATOR_PROPERTIES = "./PosterGenerator.properties";
    public static final String DEFAULT_INITIAL_DIRECTORY = "defaultInitialDirectory";
    private TemplateEngine templateEngine;

    public String render(List<Product> products){
        getTemplateEngine();

        Context context = new Context();
        context.setVariable("products", products);
        
        String result = this.templateEngine.process("output", context);
        return result;
    }

    public boolean saveResult(String location, String content) throws IOException {
        File file = new File(location);
        if(!file.isDirectory()) throw new FileNotFoundException("provided location must be folder");

        File output = new File(location + "/Poster Output.html");
        output.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(output);
        OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
        writer.write(content);

        writer.close();
        fileOutputStream.close();

        return true;
    }

    public TemplateEngine getTemplateEngine(){
        if(templateEngine != null) return templateEngine;

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver(Thread.currentThread().getContextClassLoader());
        templateResolver.setPrefix("thymeleaf/template/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        
        this.templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.setMessageResolver(new StandardMessageResolver());
        
        return templateEngine;
    }
    
    public static String getProperty(String propertyName){
        Properties properties = new Properties();

        try {
            File file = new File(POSTER_GENERATOR_PROPERTIES);
            if(!file.exists()) createPropertiesFile();
            FileInputStream fileInputStream = new FileInputStream(file);
            properties.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties.getProperty(propertyName);
    }

    public static void setProperty(String propertyName, String value){
        Properties properties = new Properties();

        try {
            FileInputStream fileInputStream = new FileInputStream(new File(POSTER_GENERATOR_PROPERTIES));
            properties.load(fileInputStream);
            fileInputStream.close();
            
            FileOutputStream fileOutputStream = new FileOutputStream(new File(POSTER_GENERATOR_PROPERTIES));
            properties.setProperty(propertyName, value);
            properties.store(fileOutputStream, value);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createPropertiesFile(){
        File propertiesFile = new File(POSTER_GENERATOR_PROPERTIES);
        Properties properties = null;
        if(!propertiesFile.exists()){
            try {
                propertiesFile.createNewFile();
                properties = new Properties();
                properties.setProperty(DEFAULT_INITIAL_DIRECTORY, "C:/");

                FileOutputStream fileOutputStream = new FileOutputStream(propertiesFile);
                properties.store(fileOutputStream, null);
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
