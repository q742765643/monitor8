package com.piesat.starter;

import lombok.SneakyThrows;
import org.apache.skywalking.oap.server.library.util.ResourceUtils;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class LocalClassLoader {

    @SneakyThrows
    public static void load(String[] args)  {
        if(args.length==0){
            return;
        }
        String path="";
        for(int i=0;i<args.length;i++){
            String value=args[i];
            if(value.indexOf("spring.config.location")!=-1){
                String[] values=value.split("=");
                path=values[1].trim();
            }
        }
        if("".equals(path)){
            return;
        }
        File file = new File(path);
        URLClassLoader classloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Method add = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
        add.setAccessible(true);
        add.invoke(classloader, new Object[]{file.toURI().toURL()});
    }

}
