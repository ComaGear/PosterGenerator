package com.knx.postergenerator;

import java.util.List;

import java.io.File;
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
}
