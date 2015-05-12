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
 * File         : FileUpload.java
 * Description  :
 * Notes        :
 * ----------------------------------------------------------------------------
 * Modified     :
 *    Chandana D: 2003-Mar-17 - Created
 *    Chandana D  2003-Jul-25 - Added function writeXMLToDisk
 *    Suneth M    2007-Nov-29 - Bug 67140, Changed updateBlobData() and deleteBlob(). 
 *                              Added overloaded method writeBlobData().
 * ----------------------------------------------------------------------------
 * New Comments:
 * 29092009 sumelk Bug 85807 - Added overloaded methods writeBlobData() and updateBlobData() to
 *                             send the upload file type as a parameter.
 * 03022009 rahelk Bug 78975 - LONGROW to BLOB, coordinated necessary changes in WEB
 * ----------------------------------------------------------------------------
 */

 package ifs.fnd.asp;
 import ifs.fnd.asp.*;
 import ifs.fnd.service.*;
 import java.util.*;
 import java.io.*;
 import java.text.*;
 
public class FileUpload
{

   private ASPPage page;

   //public constructor
   public FileUpload(ASPPage page)
   {  
      this.page = page;   
   }

   /**
   * Writes blob data to the Database & returns the new Blob ID.
   * @param sbuf Data as an array of bytes.
   * @param fielname Name of the file.
   * @param fielpath File path.
   * @return New Blob ID for the saved blob.
   */       
   public String writeBlobData(byte[] sbuf, String filename, String filepath)
   {
       return writeBlobData("", sbuf, filename, filepath);
   }
   
   /**
   * Writes blob data to the Database & returns the new Blob ID.
   * @param sbuf Data as an array of bytes.
   * @param fielname Name of the file.
   * @param fielpath File path.
   * @param filetype Type of the file. Valid types are PICTURE and UNKNOWN.
   * @return New Blob ID for the saved blob.
   */       
   public String writeBlobData(byte[] sbuf, String filename, String filepath, String filetype)
   {
       return writeBlobData("", sbuf, filename, filepath, filetype);
   }

   /**
   * Writes blob data to the Database.
   * @param blob_id Blob ID of the record (when updating the existing record).   
   * @param sbuf Data as an array of bytes.
   * @param filename Name of the file.
   * @param filepath File path.
   * @return Blob ID of the saved blob.
   */       
   public String writeBlobData(String blob_id, byte[] sbuf, String filename, String filepath)
   {
       return writeBlobData(blob_id, sbuf, filename, filepath,"UNKNOWN");
   }    

   /**
   * Writes blob data to the Database.
   * @param blob_id Blob ID of the record (when updating the existing record).   
   * @param sbuf Data as an array of bytes.
   * @param filename Name of the file.
   * @param filepath File path.
   * @param filetype Type of the file. Valid types are PICTURE and UNKNOWN.
   * @return Blob ID of the saved blob.
   */       
   public String writeBlobData(String blob_id, byte[] sbuf, String filename, String filepath, String filetype)
   {
      ASPManager mgr = page.getASPManager();
      try
      {
         ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
         ASPCommand cmd = trans.addCustomCommand("REF","binary_object_api.create_or_replace");
         if (mgr.isEmpty(blob_id))
            cmd.addParameter("BLOB_ID","N","OUT","0");
         else
            cmd.addParameter("BLOB_ID","N","IN",blob_id);
         
         cmd.addParameter("DISPLAY_TEXT","S","IN",filename);
         cmd.addParameter("FILE_NAME","S","IN",filename);
         cmd.addParameter("FILE_PATH","S","IN",filepath);
         cmd.addParameter("EXTERNAL_STORAGE","S","IN","FALSE");
         cmd.addParameter("LENGTH","N","IN",sbuf.length+"");
         cmd.addParameter("BINARY_OBJECT_TYPE","S","IN",filetype);
         cmd.addParameter("APPLICATION_DATA","S","IN",null); 

         trans = mgr.perform(trans);
         
         if (mgr.isEmpty(blob_id))
            blob_id = trans.getValue("REF/DATA/BLOB_ID");

         trans.clear();
         
         /*cmd = trans.addCustomCommand("INSERT","insert into &AO.binary_object_data_block (BLOB_ID, SEQ, DATA, LENGTH, APPLICATION_DATA) values (?, ?, ?, ?, ?)");
         cmd.setItem("CATEGORY","Sql");
         cmd.addParameter("BLOB_ID","N","IN",blob_id);
         cmd.addParameter("SEQ","N","IN","1");
         cmd.addParameter("DATA","R","IN",Util.toBase64Text(sbuf));
         cmd.addParameter("LENGTH","N","IN",sbuf.length+"");
         cmd.addParameter("APPLICATION_DATA","S","IN",null);          */

         String picture_type = "";
         if ("PICTURE".equals(filetype))
            picture_type = getPictureType(filename);   

         //overloaded method for web
         cmd = trans.addCustomCommand("BODBNEW","BINARY_OBJECT_DATA_BLOCK_API.New__");
         cmd.addParameter("OBJVERSION","S","OUT",null);
         cmd.addParameter("OBJID","S","OUT",null);
         cmd.addParameter("BLOB_ID","N","IN",blob_id);
         cmd.addParameter("APPLICATION_DATA","S","IN",picture_type);          
         cmd.addParameter("DATA","R","IN",Util.toBase64Text(sbuf));

         mgr.perform(trans);

         return blob_id;
      } catch (Exception e)
      {
         page.error(e);   
         return null; 
      }
   }    

   /** 
    * Returns information relevant to a given Blob ID from the database.
    * Returned Hashtable has the keys: FILE_NAME, FILE_PATH, FILE_SIZE. 
    * @param blob_id Blob ID of the record from which data shoud be retrieved.
    * @return Blob information in a Hashtable. 
    */ 
   public Hashtable getBlobInfo(String blob_id)
   {
      ASPManager mgr = page.getASPManager();
      ASPTransactionBuffer  trans = mgr.newASPTransactionBuffer();
      /*ASPCommand cmd_select = trans.addCustomCommand("SELECT","Query");
      cmd_select.setItem("CATEGORY","Sql");
      cmd_select.setItem("SELECT","select * from binary_object where BLOB_ID = ?");
      cmd_select.addParameter("BLOB_ID","N","IN",blob_id);*/
      
      ASPCommand cmd = trans.addCustomCommand("BOINFO","BINARY_OBJECT_API.Get_Object_Info");
      cmd.addParameter("OBJID","S","OUT",null);
      cmd.addParameter("OBJVERSION","S","OUT",null);
      cmd.addParameter("DISPLAY_TEXT","S","OUT",null);
      cmd.addParameter("FILE_NAME","S","OUT",null);
      cmd.addParameter("FILE_PATH","S","OUT",null);
      cmd.addParameter("EXTERNAL_STORAGE","S","OUT",null);
      cmd.addParameter("APPLICATION_DATA","S","OUT",null);
      cmd.addParameter("LENGTH","S","OUT",null);
      cmd.addParameter("LOB_OBJID","S","OUT",null);
      cmd.addParameter("BLOB_ID","N","IN",blob_id);

      trans = mgr.perform(trans);         

      String file_name = trans.getValue("BOINFO/DATA/FILE_NAME");         
      String file_path = trans.getValue("BOINFO/DATA/FILE_PATH");         
      String file_length = trans.getValue("BOINFO/DATA/LENGTH");   

      Hashtable blob_info = new Hashtable();
      blob_info.put("FILE_NAME",file_name);
      blob_info.put("FILE_PATH",file_path);
      blob_info.put("FILE_LENGTH",file_length);

      return blob_info;
   }    

   /** 
    *Returns data relevant to a given Blob ID from the database.
    * @param blob_id Blob ID of the record from which data shoud be retrieved.
    * @throws IOException.
    * @return Data as a byte array. 
    */ 
   public byte[] getBlobData(String blob_id)
   {
      ASPManager mgr = page.getASPManager();
      try
      {
         byte data[] = null; 
         ASPTransactionBuffer  trans = mgr.newASPTransactionBuffer();
         ASPCommand cmd_select = trans.addCustomCommand("SELECT","Query");
         cmd_select.setItem("CATEGORY","Sql");
         cmd_select.setItem("SELECT","select * from binary_object_data_block where BLOB_ID = ?");
         cmd_select.addParameter("BLOB_ID","N","IN",blob_id);
         trans = mgr.perform(trans);
         if (trans.getBuffer("SELECT/DATA")!=null)
            data = Util.fromBase64Text(trans.getBuffer("SELECT/DATA").getValue("DATA"));
         return data;           
      } catch (IOException e)
      {
         page.error(e);   
         return  null;
      }
   }  

   /**
    * Updates an existing blob data.
    * @param blob_id Blob ID of the record to be updated.
    * @param sbuf Data as a byte array.
    */       
   public void updateBlobData(String blob_id, byte[] sbuf)
   {
      updateBlobData(blob_id, sbuf, null, null)    ;
   }

   /**
    * Updates an existing blob data.
    * @param blob_id Blob ID of the record to be updated.
    * @param sbuf Data as a byte array.
    * @param filename Name of the file.
    * @param filepath File path.
    */        
   public void updateBlobData(String blob_id, byte[] sbuf, String filename, String filepath)
   {
      writeBlobData(blob_id, sbuf, filename, filepath);
   }      

   /**
    * Updates an existing blob data.
    * @param blob_id Blob ID of the record to be updated.
    * @param sbuf Data as a byte array.
    * @param filename Name of the file.
    * @param filepath File path.
    * @param filetype Type of the file. Valid types are PICTURE and UNKNOWN.
    */        
   public void updateBlobData(String blob_id, byte[] sbuf, String filename, String filepath, String filetype)
   {
      writeBlobData(blob_id, sbuf, filename, filepath, filetype);
   }      

   /**
    * Deletes a blob from the database.
    * @ param blob_id Blob ID of the record which shoud be removed.
    */  
   public void deleteBlob(String blob_id)
   {
      ASPManager mgr = page.getASPManager(); 
      ASPTransactionBuffer  trans = mgr.newASPTransactionBuffer();     
      ASPCommand cmd_delete = trans.addCustomCommand("DELETE_INFO","BINARY_OBJECT_API.Delete");
      cmd_delete.addParameter("BLOB_ID", "N", "IN", blob_id);
      mgr.perform(trans);
   }  

   /**
    * Writes a file to the disk.
    * @param sbuf Data to be written as an array of bytes.
    * @param filepath Detination path where the file should be written.
    * @param filename Name of the file to be written.
    */       
   public void writeToDisk(byte[] sbuf, String filepath, String filename)throws Exception 
   {
      String f = "";
      File file = new File(filepath);
      file.mkdirs();
      if (filepath.endsWith(File.separator))
         f = filepath+filename;
      else
         f =  filepath+File.separatorChar+filename; 
      file = new File(f);
      if (file.exists())
         file.renameTo(new File(filepath.endsWith(File.separator)?
                                filepath+"TMP_"+timeStamp()+"_"+filename:
                                filepath+File.separatorChar+"TMP_"+timeStamp()+"_"+filename));
      BufferedOutputStream outfile = new BufferedOutputStream(new FileOutputStream(file), 2048);
      outfile.write(sbuf);
      outfile.close();
   }
   
   /**
    * Writes a XML file to the disk.
    * @param sbuf Data to be written as an array of bytes.
    * @param filepath Detination path where the file should be written.
    */       
   public String writeXMLToDisk(byte[] sbuf, String filepath)throws Exception 
   {
      String f = "";
      String filename = timeStamp()+"_profile.xml";
      
      if (filepath == null || filepath =="")
         filepath = page.getASPConfig().getParameter("ADMIN/FILE_UPLOAD/TARGET","../server/uploaded_files");
      
      File file = new File(filepath);
      file.mkdirs();
      if (filepath.endsWith(File.separator))
         f = filepath+filename;
      else
         f =  filepath+File.separatorChar+filename; 
      file = new File(f);
 
      BufferedOutputStream outfile = new BufferedOutputStream(new FileOutputStream(file), 2048);
      outfile.write(sbuf);
      outfile.close();
      return filename;
   }   

   private String timeStamp(){
      SimpleDateFormat formatter = new SimpleDateFormat("yyMMddhhmmss");
      Date currentTime = new Date();
      String timeStamp = formatter.format(currentTime);
      return timeStamp;  
   } 

   private String getPictureType(String file_name)
   {
       String file_ext = file_name.substring(file_name.indexOf(".")+1,file_name.length());
       file_ext = file_ext.toUpperCase();
       
       if ("BMP".equals(file_ext))
          return "1";
       else if ("ICO".equals(file_ext))
          return "2";
       else if ("WMF".equals(file_ext))
          return "3"; 
       else if (("TIFF".equals(file_ext)) || ("TIF".equals(file_ext)))
          return "4"; 
       else if ("PCX".equals(file_ext))
          return "5"; 
       else if ("GIF".equals(file_ext))
          return "6"; 
       else if (("JPEG".equals(file_ext)) || ("JPG".equals(file_ext)) || ("JPE".equals(file_ext)))
          return "7"; 
       else return "0"; 
   }
}
