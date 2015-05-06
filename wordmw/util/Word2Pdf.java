package ifs.wordmw.util;
import java.io.File;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class Word2Pdf {
 
static final int wdDoNotSaveChanges = 0;// 不保存待定的更改。
static final int wdFormatPDF = 17;// PDF 格式



public static String transfer(String filePath){
	
	 String filename = filePath;
    String toFilename = filename + ".pdf";
    System.out.println("启动Word");
    long start = System.currentTimeMillis();
    ActiveXComponent app = null;
    try {
        app = new ActiveXComponent("Word.Application");
        app.setProperty("Visible", true);

        Dispatch docs = app.getProperty("Documents").toDispatch();
        System.out.println("打开文档" + filename);
        Dispatch doc = Dispatch.call(docs,//
                "Open", //
                filename,// FileName
                false,// ConfirmConversions
                true // ReadOnly
                ).toDispatch();

        
        System.out.println("转换文档到PDF" + toFilename);
        File tofile = new File(toFilename);
        if (tofile.exists()) {
            tofile.delete();
        }
        Dispatch.call(doc,//
                "SaveAs", //
                toFilename, // FileName
                wdFormatPDF);
        
        
//        Dispatch.invoke(doc, 
//        		"SaveAs", 
//        		Dispatch.Method, 
//        		new Object[] {toFilename,new Variant(17)}, 
//        		new int[1]);

        Dispatch.call(doc, "Close", false);
        long end = System.currentTimeMillis();
        System.out.println("转换完成..用时：" + (end - start) + "ms.");
    } catch (Exception e) {
        System.out.println("========Error:文档转换失败：" + e.getMessage());
    } finally {
        if (app != null)
            app.invoke("Quit", wdDoNotSaveChanges);
    }
    return toFilename;
}

public static void main(String[] args) {

    String filename = "D:\\3.doc";
    
    
    
    transfer(filename);
//    String toFilename = filename + ".pdf";
//    System.out.println("启动Word");
//    long start = System.currentTimeMillis();
//    ActiveXComponent app = null;
//    try {
//        app = new ActiveXComponent("Word.Application");
//        app.setProperty("Visible", true);
//
//        Dispatch docs = app.getProperty("Documents").toDispatch();
//        System.out.println("打开文档" + filename);
//        Dispatch doc = Dispatch.call(docs,//
//                "Open", //
//                filename,// FileName
//                false,// ConfirmConversions
//                true // ReadOnly
//                ).toDispatch();
//
//        System.out.println("转换文档到PDF" + toFilename);
//        File tofile = new File(toFilename);
//        if (tofile.exists()) {
//            tofile.delete();
//        }
//        Dispatch.call(doc,//
//                "SaveAs", //
//                toFilename, // FileName
//                wdFormatPDF);
//
//        Dispatch.call(doc, "Close", false);
//        long end = System.currentTimeMillis();
//        System.out.println("转换完成..用时：" + (end - start) + "ms.");
//    } catch (Exception e) {
//        System.out.println("========Error:文档转换失败：" + e.getMessage());
//    } finally {
//        if (app != null)
//            app.invoke("Quit", wdDoNotSaveChanges);
//    }
}
}