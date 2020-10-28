package com.piesat.common.utils;

import com.aspose.words.FontSettings;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;

import java.io.*;
import java.net.URL;

/**
 * @author cwh
 * @date 2020年 04月26日 15:34:30
 */
public class Doc2PDF {

    public static void main(String[] args) {
        String sourcePath = "D:\\项目\\[模板].docx";
        String targetPath = "D:\\项目\\[模板].pdf";
        doc2pdf(sourcePath, targetPath);
    }

    /**
     * word文档转pdf
     *
     * @param wordPath word路径
     * @param pdfPath  pdf路径
     */
    public static void doc2pdf(String wordPath, String pdfPath) {
        // 验证License 若不验证则转化出的pdf文档会有水印产生

        if (!getLicense()) {
            return;
        }
        FileOutputStream os = null;
        try {
            // 新建的PDF文件路径
            File file = new File(pdfPath);
            os = new FileOutputStream(file);
            // 要转换的word文档的路径
//            FontSettings.getDefaultInstance().setFontsFolder("font", false);
            com.aspose.words.Document doc =
                    new com.aspose.words.Document(wordPath);
            //设置字体
//            FontSettings fontSettings = new FontSettings();
//            fontSettings.setFontsFolder("/usr/share/fonts", true);
            // 全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
            doc.save(os, SaveFormat.PDF);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 注册word2pdf转换工具
     *
     * @return
     */
    private static boolean getLicense() {
        boolean result = false;
        try {
            // 获取并配置license.xml
            InputStream is = com.aspose.words.Document.class
                    .getResourceAsStream("/com.aspose.words.lic_2999.xml");
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
