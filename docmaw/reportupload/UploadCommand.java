package ifs.docmaw.reportupload;

import ifs.fnd.service.Util;

public class UploadCommand implements Command {
   public static boolean DEBUG = Util.isDebugEnabled(UploadCommand.class.getName());
   
   private String docClass = null;
   private String docNo = null;
   private String docSheet = null;
   private String docRev = null;
   private String serverFileName = null;
   private String originalFileName = null;
   
   private long commandCreatedTime = 0;
   
   public UploadCommand(String docClass, String docNo, String docSheet, String docRev, String serverFileName, String originalFileName){
      this.docClass = docClass;
      this.docNo = docNo;
      this.docSheet = docSheet;
      this.docRev = docRev;
      this.serverFileName = serverFileName;
      this.originalFileName = originalFileName;
      commandCreatedTime = System.currentTimeMillis();
   }

   public boolean execute() {
      try {
         ifs.docmaw.reportupload.DocumentCheckIn.checkInDocument(docClass, docNo, docSheet, docRev, serverFileName, originalFileName);
         return true;
      } catch (Throwable th) {
         th.printStackTrace();
         if(DEBUG){
            //NOOP
         }
         return false;
      } 
   }

}
