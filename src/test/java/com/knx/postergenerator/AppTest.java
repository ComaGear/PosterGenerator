package com.knx.postergenerator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public class AppTest {

    private static final String outputPosition = "C:\\Users\\comag\\Downloads\\";
    
    @Test
    public void renderOutput() throws IOException{
        App app = new App();
        TemplateEngine templateEngine = app.getTemplateEngine();
        
        Context context = new Context();
        context.setVariable("vTest", "vTest");

        String result = templateEngine.process("output", context).trim();

        File file = new File(outputPosition + "output.html");
        file.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
        writer.write(result);

        writer.close();
        fileOutputStream.close();
        
        assertTrue(file.exists());
    }
}
