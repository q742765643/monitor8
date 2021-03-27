package com.htht;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CodeTotal {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        String path="D:\\gitcangku\\monitor8\\apm-webapp";
        List<File> list=total(path);
        System.out.println("文件数量："+list.size());

        //统计代码行数
        Integer row=0;

        for (File file : list) {
            System.out.println(file.getName());
            FileReader fr=new FileReader(file);//创建文件输入流
            BufferedReader in=new BufferedReader(fr);//包装文件输入流，可整行读取
            String line="";
            while((line=in.readLine()) != null) {
                row++;
            }
        }

        System.out.println("代码行数："+row);
    }

    public static List<File> total(String path){
        List<File> files=new ArrayList<File>();
        File file=new File(path);
        File []files2=file.listFiles();
        for (File file3 : files2) {
            if(file3.isFile()){
                files.add(file3);
            }else {
                files.addAll(files.size(), total(file3.getPath()));
            }
        }
        return files;
    }

}

