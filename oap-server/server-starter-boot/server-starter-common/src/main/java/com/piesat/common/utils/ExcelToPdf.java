package com.piesat.common.utils;

import com.aspose.cells.License;
import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;

import javax.servlet.ServletOutputStream;
import java.io.*;

/**
 * @ClassName : ExcelToPdf
 * @Description :
 * @Author : zzj
 * @Date: 2020-11-11 09:41
 */
public class ExcelToPdf {
    public static boolean getLicense() {
        boolean result = false;
        try {
            InputStream is = Test.class.getClassLoader().getResourceAsStream("xlsxlicense.xml"); //  license.xml应放在..\WebRoot\WEB-INF\classes路径下
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void excel2pdf(org.apache.poi.ss.usermodel.Workbook workbook,OutputStream outputStream) {

        try {
            if (!getLicense()) {          // 验证License 若不验证则转化出的pdf文档会有水印产生
                return;
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] barray = bos.toByteArray();
            InputStream is = new ByteArrayInputStream(barray);
            Workbook wb = new Workbook(is);// 原始excel路径
            PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
            pdfSaveOptions.setOnePagePerSheet(true);
            wb.save(outputStream, pdfSaveOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

