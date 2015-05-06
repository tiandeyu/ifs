/*
 *                 IFS Research & Development
 *
 *  This program is protected by copyright law and by international
 *  conventions. All licensing, renting, lending or copying (including
 *  for private use), and all other use of the program, which is not
 *  expressively permitted by IFS Research & Development (IFS), is a
 *  violation of the rights of IFS. Such violations will be reported to the
 *  appropriate authorities.
 *
 *  VIOLATIONS OF ANY COPYRIGHT IS PUNISHABLE BY LAW AND CAN LEAD
 *  TO UP TO TWO YEARS OF IMPRISONMENT AND LIABILITY TO PAY DAMAGES.
 * ----------------------------------------------------------------------------
 *  File                        : HttpFileUploadDownload.java
 *  Description                 : recieve http request from ActiveX and handle download/upload files from/to web server.
 *  Notes                       :
 *  Other Programs Called       : Inherits from DocSrv in order to get temp path.
 *  ---------------------------------------------------------------------------
 *  Modified    :
 *    2006-05-28  BAKALK  First version.
 *    2006-05-30  BAKALK  Bug 58326.
 *    2006-06-04  BAKALK  Bug 58326., Added some error handlings.
 *    2007-04-04  CHSELK  Merged bug 62256.
 *                2007-02-19  THWILK  Bug 62256, Modified methods writeFile and readFile.
 *    2007-11-30  AMNALK  Bug 65997, Added new method deleteTempFile() to delete temp files copied when downloading.
 *    2008-07-07  AMNALK  Bug 72460, Modifed run() and added new method deleteAllTempFiles().
 *    2009-11-12  AMNALK  Bug 85070, Modified readFile() to support very large files.
 *
 */
 
package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.service.*;
import java.io.*;
import javax.servlet.http.*;

public class HttpFileUploadDownload extends DocSrv
{
   //
   // Static constants
   //

   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.HttpFileUploadDownload");
   
   String  docFileName;
   String  fileType;    
   
   //
   // Construction
   //
   public HttpFileUploadDownload(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }
   
   
   public void run()
   {
      ASPManager      mgr  = getASPManager();
      if ("upload".equals( getRequestHeader("action")))
         writeFile();
      else if ("download".equals( getRequestHeader("action")))
         readFile();
      else if ("getfilesize".equals( getRequestHeader("action")))
         getFileSize();
      //Bug Id 65997, Start
      else if ("deletetempfile".equals( getRequestHeader("action")))
         deleteTempFile();
      //Bug Id 65997, End       
      //Bug Id 72460, Start
      else if ("deletealltempfiles".equals( getRequestHeader("action")))
         deleteAllTempFiles();
      //Bug Id 72460, End
   }
   
   
   public void  preDefine() throws ifs.fnd.service.FndException
   {
      ASPManager mgr = getASPManager();
      super.preDefine();
   }
   
   /*
     Reading a chunk of binaries out of the the file: bakalk
    **/
   private void readFile()
   {
      ASPManager mgr = getASPManager();
      
      if (mgr instanceof DocmawManager)
      {
         mgr.removePageId();	 // Bug ID 62256
         HttpServletResponse theResponse = ((DocmawManager)mgr).getPublicAspResponse();
         String fileNamePassed = this.getRequestHeader("filename");
         try
         {
            String tempPath = getTmpPath();
            
            if (illegalCharsContained(fileNamePassed))
            {
               return;
            }
            String fullLocalFileName = tempPath + fileNamePassed;
            int    chunkSize =  (new Integer(this.getRequestHeader("chunksize"))).intValue();
            long   readFrom  =  (new Long(this.getRequestHeader("readfrom"))).longValue(); //Bug Id 85070
            
            FileInputStream in = new FileInputStream(fullLocalFileName);
            
            //discard some binaries downloaded already
            in.skip(readFrom);
            
            // If the above worked, it is safe to continue
            
            //HttpServletResponse theResponse = ((DocmawManager)mgr).getPublicAspResponse();
            
            OutputStream out = theResponse.getOutputStream();
            
            theResponse.setContentType("application/octet-stream");
            
            int buf_size = 65536;
            byte[] buf = new byte[buf_size];
            int count = 0;
            
            //slice the chunk 
            int noOfSlices =  chunkSize / buf_size;
            int lastSliceSize = chunkSize % buf_size;
            
            if (lastSliceSize > 0)
            {
               noOfSlices++;
            }
            
            // Read until we get no more data
            try
            {
               for (int k = 0; k<noOfSlices; k++)
               {
                  if (k==noOfSlices-1 && lastSliceSize > 0) { //last slice 
                     buf_size = lastSliceSize;
                     buf = new byte[buf_size];
                  }
                  
                  if ((count = in.read(buf, 0, buf_size)) != -1) {
                     out.write(buf, 0, count);
                     out.flush();
                  }
                  else
                     break;
               }
            }
            finally
            {
               in.close();
               out.close();
            } 
         }
         catch(Exception e)
         {
            theResponse.addHeader("errormessage",mgr.translate("DOCMAWHTTPFILEUPLOADDOWNLOADREADFILEFAILED: Reading binary data from &1  failed:" ,fileNamePassed) );
         }
         finally
         {
            // Modified by Terry 20121023
            // Original:
            // mgr.endResponse();
            // Modified end
         }
      }
   }
   
   
   private void writeFile()
   {
      ASPManager      mgr = getASPManager();
      String  contentType;
      String  fullLocalFileName;
      byte [] requestBytes;
      
      contentType = mgr.getRequestContentType();
      
      if (!mgr.isEmpty(contentType)&& contentType.toLowerCase().startsWith("multipart/form-data"))
      {
         mgr.removePageId(); // Bug ID 62256
         HttpServletResponse theResponse = ((DocmawManager)mgr).getPublicAspResponse();
         String fileNamePassed = this.getRequestHeader("filename");
         try
         {
            // Fetch the whole request bytes
            requestBytes = getRequestBytes(mgr);
            
            String tempPath = getTmpPath();
            
            if (illegalCharsContained(fileNamePassed))
            {
               throw new java.io.IOException();
            }
            fullLocalFileName = tempPath + fileNamePassed;
            // Parse request and write file to disk
            writeFileFromHttpPostRequest(requestBytes,fullLocalFileName);
            
         } catch(Exception e){
            theResponse.addHeader("errormessage",mgr.translate("DOCMAWHTTPFILEUPLOADDOWNLOADWRITEFILEFAILED: Writing binary data to &1 failed:",fileNamePassed));
         }//try
         finally
         { 
            mgr.endResponse();
         }
      }//if
      
      //mgr.endResponse();     
   }// method
   
   
   private void getFileSize()
   {
      ASPManager mgr = getASPManager();
      String contentType = mgr.getRequestContentType();
      
      if (!mgr.isEmpty(contentType)&& contentType.toLowerCase().startsWith("multipart/form-data"))
      {  
         HttpServletResponse theResponse = ((DocmawManager)mgr).getPublicAspResponse();
         String fileNamePassed = this.getRequestHeader("filename");
         try
         {
            String tempPath = getTmpPath();
            
            long fileSize = -1; 
            
            String fullLocalFileName = tempPath + fileNamePassed;
            
            File file = new File(fullLocalFileName);
            
            if (!illegalCharsContained(fileNamePassed) && file.isFile()) {// otherwise -1 returned as filsize
               fileSize = file.length();
            }
            
            if (fileSize>=0) {
               theResponse.addHeader("filesize",""+fileSize);
            }
            else
               theResponse.addHeader("errormessage",mgr.translate("DOCMAWHTTPFILEUPLOADDOWNLOADNOTREALFILE: Filename is not allowed or file does not exist:")+" "+fileNamePassed);
            
         }
         catch(Exception e)
         {
            theResponse.addHeader("errormessage",mgr.translate("DOCMAWHTTPFILEUPLOADDOWNLOADGETFILESIZEFAILED: Could not access file:")+" "+fileNamePassed + ". "+ e.getMessage());
         }//try
         finally
         {
            mgr.endResponse();
         }
      }//
   }
   
   private synchronized void writeFileFromHttpPostRequest(byte[]  requestBytes, String fileName) throws IOException
   {
      FileOutputStream foStream;
      
      foStream = new FileOutputStream(fileName,true);
      foStream.write(requestBytes);
      foStream.close();
   }
   
   
   private byte[] getRequestBytes (ASPManager  mgr) throws IOException
   {
      if(DEBUG) debug(this+": getRequestBytes() {");
      
      int offset = 0, size;
      
      int currPercent = 0, lastPercent = 0;
      
      int contentLen   = 0;
      
      byte[] requestBytes;
      
      InputStream inputStream;
      
      contentLen  = mgr.getRequestLength();
      
      // Reserve a byte array to place all the file-bytes in
      requestBytes = new byte [contentLen];
      
      if(DEBUG) debug("contentLen = " + contentLen);
      
      // Get InpuStream to read bytes from
      inputStream = mgr.getRequestBodyAsInputStream() ;
      
      try
      {
         // Loop for getting the whole POSTed request into our byte array
         while (offset < contentLen)
         {
            size = inputStream.read (requestBytes, offset, contentLen - offset);
            if (size == -1)
               throw new IOException ("Unexpected error while reading POST request.");
            offset = offset + size;
            
            if(DEBUG)
            {
               currPercent = (100 * offset) / contentLen;
               if (currPercent != lastPercent &&  ((currPercent % 10) == 0))
                  debug(this+":   Read " + currPercent + " %");
               lastPercent = currPercent;
            }
         }
      }
      catch (IOException io)
      {
         inputStream.close();
         throw io;
      }
      
      if(DEBUG) debug("");
      
      if(DEBUG) debug(this+": getRequestBytes() }");
      
      return requestBytes;
   }
   
   public void debug(String str)
   {
      // System.out.println(str);
      if (DEBUG) super.debug(str);
   }
   
   String getRequestHeader(String headerName)
   {
      ASPManager mgr = getASPManager();
      String retStr = "";
      if (mgr instanceof DocmawManager ) {
         HttpServletRequest theRequest =((DocmawManager)mgr).getPublicAspRequest();
         retStr = theRequest.getHeader(headerName);
      }
      
      return retStr;
   }
   
   
   
   /* private String getFileLocation(){ throws ifs.fnd.service.FndException
      String tempPath = "";
      ASPManager mgr = getASPManager();
      tempPath = getTmpPath();
      if (!mgr.isEmpty(tempPath) && (!( tempPath.endsWith("/") || tempPath.endsWith("\\")))) {
         tempPath += tempPath.indexOf("/")!= -1?"/":"\\";
      }
      return tempPath;
   }*/
   
   
   private boolean illegalCharsContained(String fileName)
   {
      char[] illegalChars = {'\\','/',':','*','?','<','>','|','"','^'};
      int counter = 0;
      for (int m=0;m<illegalChars.length;m++) {
         if (fileName.indexOf(""+illegalChars[m]) != -1) {
            counter++;
            break;
         }
         
      }
      return counter>0 ? true : false;
   }
   
   //Bug Id 65997, Start
   private void deleteTempFile()
   {
      String FileName = this.getRequestHeader("filename");
      ASPManager mgr = getASPManager();
      
      if (mgr instanceof DocmawManager)
      {
         mgr.removePageId();  
         HttpServletResponse theResponse = ((DocmawManager)mgr).getPublicAspResponse();       
         
         try{
            String tempPath = getTmpPath();
            String fullLocalFileName = tempPath + FileName;
            
            File f = new File(fullLocalFileName);
            
            boolean success = f.delete();
            
         }
         
         catch(Exception e)
         {
            theResponse.addHeader("errormessage",mgr.translate("DOCMAWHTTPFILEUPLOADDOWNLOADDELETEFILEFAILED: Deletion  failed:" ,FileName) );
            
         }
         finally
         {
            mgr.endResponse();
         }
         
      }
      
   }
   //Bug Id 65997, End
   
   //Bug Id 72460, Start
   private void deleteAllTempFiles()
   {
      String FileName = this.getRequestHeader("filename");
      ASPManager mgr = getASPManager();
      
      String filename = FileName.substring(0,FileName.lastIndexOf('.'));
      
      if (mgr instanceof DocmawManager)
      {
         mgr.removePageId();  
         HttpServletResponse theResponse = ((DocmawManager)mgr).getPublicAspResponse();       
         
         try{
            String tempPath = getTmpPath();
            String fullLocalFileName = tempPath + FileName;
            
            File folder = new File(tempPath);
            File[] listOfFiles = folder.listFiles();
            
            for (int i = 0 ; i < listOfFiles.length; i++) 
            {
               if (listOfFiles[i].isFile()) 
               {
                  if(DEBUG) debug(this+ " :listOfFiles[i] " + listOfFiles[i]);
                  
                  if (listOfFiles[i].getName().startsWith(filename))
                  {
                     if(DEBUG) debug(this+ "Delete : " + listOfFiles[i]);
                     listOfFiles[i].delete();
                  }
               }
            }
         }
         
         catch(Exception e)
         {
            theResponse.addHeader("errormessage",mgr.translate("DOCMAWHTTPFILEUPLOADDOWNLOADDELETEFILEFAILED: Deletion  failed:" ,FileName) );
            
         }
         finally
         {
            mgr.endResponse();
         }
         
      }
      
   }
   //Bug Id 72460, End
}//end of class

