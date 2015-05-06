package ifs.docmaw.reportupload;

import ifs.docmaw.DocmawUtil;
import ifs.docmaw.edm.DocmawFtp;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.base.IfsException;
import ifs.fnd.base.SystemException;
import ifs.fnd.record.FndSqlType;
import ifs.fnd.record.FndSqlValue;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;
import ifs.fnd.sf.j2ee.FndJ2eeConnectionManager;
import ifs.fnd.sf.storage.FndConnection;
import ifs.fnd.sf.storage.FndStatement;
import ifs.fnd.util.Str;

import java.io.File;
import java.util.StringTokenizer;

/**
 * 
 * @author luqingwei
 *
 */
public class DocumentCheckIn {
   
   public static boolean DEBUG = Util.isDebugEnabled(DocumentCheckIn.class.getName());
   
   public static void checkInDocument(
         String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev,
         String server_file_name,
         String original_file_name) throws FndException, IfsException {
      try {
         //
         String tempPath = getTmpPath();
         if(false == new File(getTmpPath().concat( server_file_name)).exists()){
            throw new FndException("DOCUMENTCHECKINFILENOTFOUND: File not found!");
         }
         
         String file_no = getNextFileNo(doc_class, doc_no, doc_sheet, doc_rev, "ORIGINAL");
         String file_ext = DocmawUtil.getFileExtention(server_file_name);
         String file_type = getFileType(file_ext.toUpperCase());
         String repository_file_name = getRemoteFileName(doc_class, doc_no, doc_sheet, doc_rev, file_ext, file_no);
         String edm_rep_info = getRepositoryInfo(doc_class, doc_no, doc_sheet, doc_rev, file_type);
         
         String[] checkInFileRet = checkInFile(doc_class, doc_no, doc_sheet, doc_rev, original_file_name, file_type, file_no);
         setFileState(doc_class, doc_no, doc_sheet, doc_rev, file_type, file_no, "StartCheckOut");
         
         // Put all files to the repository...
         String[] local_file_names = new String[1];
         String[] repository_file_names = new String[1];
         local_file_names[0] = server_file_name;
         repository_file_names[0] = repository_file_name;
         
         try
         {
            putFilesToRepository(addKeys(edm_rep_info, doc_class, doc_no, doc_sheet, doc_rev), local_file_names, repository_file_names);
         }
         catch (FndException fnd)
         {
            boolean new_file = "TRUE".equals(checkInFileRet[2]);
            if (new_file)
            {
               removeInvalidReference(doc_class, doc_no, doc_sheet, doc_rev, checkInFileRet[0], file_no);
            }
            else
            {
               setFileState(doc_class, doc_no, doc_sheet, doc_rev, checkInFileRet[0], file_no, "FinishCheckOut");
            }
            throw fnd;
         }
         
         if (DEBUG)
            Util.debug( DocumentCheckIn.class.getName() + ": Files checked in to repository sucessfully..");
         if (DEBUG)
            Util.debug( DocumentCheckIn.class.getName() + ": Changing edm state..");
         
         setFileState(doc_class, doc_no, doc_sheet, doc_rev, file_type, file_no, "FinishCheckIn");
         
      } catch (SystemException e) {
         e.printStackTrace();
      } catch (IfsException e) {
         e.printStackTrace();
      }finally{
         //NO-OP
      }
   }
   public static String getNextFileNo(
         String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev,
         String doc_type) throws FndException, IfsException{
      String nextFileNo = null;
      FndJ2eeConnectionManager fndJ2eeConnectionManager = new FndJ2eeConnectionManager();
      FndConnection conn = fndJ2eeConnectionManager.getPlsqlConnection("IFSAPP","zh");
      FndStatement stmt=null;
      String sqlnNextFileNo = "BEGIN ? := IFSAPP.EDM_FILE_API.GET_NEXT_FILE_NO(?,?,?,?,?); END;";
      try{
         stmt=conn.createStatement();
         stmt.prepareCall(sqlnNextFileNo);
         
         stmt.defineOutParameter("NEXT_FILE_NO", FndSqlType.STRING);
         FndSqlValue  docClass=new FndSqlValue("DOC_CLASS",doc_class,false);
         stmt.defineInParameter(docClass);
         FndSqlValue  docNo=new FndSqlValue("DOC_NO",doc_no,false);
         stmt.defineInParameter(docNo);
         FndSqlValue  docSheet=new FndSqlValue("DOC_SHEET",doc_sheet,false);
         stmt.defineInParameter(docSheet);
         FndSqlValue  docRev=new FndSqlValue("DOC_REV",doc_rev,false);
         stmt.defineInParameter(docRev);
         FndSqlValue  docType=new FndSqlValue("OBJID",doc_type,false);
         stmt.defineInParameter(docType);
         stmt.execute();
         nextFileNo = "" + stmt.getBigDecimal(1).toBigInteger().intValue();
      }finally{
         try {
            stmt.close();
         } catch (SystemException e) {
            e.printStackTrace();
         }
      }
      return nextFileNo;		   
   }
   
   
   // Get file type with file extention.
   private static String getFileType(String file_ext) throws FndException, IfsException
   {
      String fileType = null;
      FndJ2eeConnectionManager fndJ2eeConnectionManager = new FndJ2eeConnectionManager();
      FndConnection conn = fndJ2eeConnectionManager.getPlsqlConnection("IFSAPP","zh");
      FndStatement stmt=null;
      String sqlGetFileType ="BEGIN ? := IFSAPP.Edm_Application_API.Get_File_Type(?); END;";
      try{
         stmt=conn.createStatement();
         stmt.prepareCall(sqlGetFileType);
         
         stmt.defineOutParameter("NEXT_FILE_NO", FndSqlType.STRING);
         FndSqlValue  fileExt=new FndSqlValue("FILE_EXT",file_ext,false);
         stmt.defineInParameter(fileExt);
         stmt.execute();
         fileType = stmt.getString(1);
      }finally{
         try {
            stmt.close();
         } catch (SystemException e) {
            e.printStackTrace();
         }
      }
      return fileType;		   
   }
   
   private static String getRemoteFileName(
         String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev,
         String file_ext,
         String file_no) throws FndException, IfsException
         {
      String remoteFileName = null;
      FndJ2eeConnectionManager fndJ2eeConnectionManager = new FndJ2eeConnectionManager();
      FndConnection conn = fndJ2eeConnectionManager.getPlsqlConnection("IFSAPP","zh");
      FndStatement stmt=null;
      String sqlGetRemoteFileName ="BEGIN ? := IFSAPP.Edm_File_Api.Generate_Remote_File_Name(?,?,?,?,?,?); END;";
      try{
         stmt=conn.createStatement();
         stmt.prepareCall(sqlGetRemoteFileName);
         
         stmt.defineOutParameter("REMOTE_FILE_NAME", FndSqlType.STRING);
         FndSqlValue  docClass=new FndSqlValue("DOC_CLASS",doc_class,false);
         stmt.defineInParameter(docClass);
         FndSqlValue  docNo=new FndSqlValue("DOC_NO",doc_no,false);
         stmt.defineInParameter(docNo);
         FndSqlValue  docSheet=new FndSqlValue("DOC_SHEET",doc_sheet,false);
         stmt.defineInParameter(docSheet);
         FndSqlValue  docRev=new FndSqlValue("DOC_REV",doc_rev,false);
         stmt.defineInParameter(docRev);
         FndSqlValue  fileExt=new FndSqlValue("FILE_EXT",file_ext,false);
         stmt.defineInParameter(fileExt);
         FndSqlValue  fileNo=new FndSqlValue("FILE_NO",file_no,false);
         stmt.defineInParameter(fileNo);
         stmt.execute();
         remoteFileName = stmt.getString(1);
      }finally{
         try {
            stmt.close();
         } catch (SystemException e) {
            e.printStackTrace();
         }
      }
      return remoteFileName;		   
         }
   
   private static String getRepositoryInfo(
         String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev,
         String file_type) throws FndException, IfsException
         {
      String repoInfo = null;
      FndJ2eeConnectionManager fndJ2eeConnectionManager = new FndJ2eeConnectionManager();
      FndConnection conn = fndJ2eeConnectionManager.getPlsqlConnection("IFSAPP","zh");
      FndStatement stmt=null;
      String sqlGetEdmRepositoryInfo ="BEGIN ? := IFSAPP.Edm_File_Api.Get_Edm_Repository_Info2(?,?,?,?,?); END;";
      try{
         stmt=conn.createStatement();
         stmt.prepareCall(sqlGetEdmRepositoryInfo);
         
         stmt.defineOutParameter("EDM_REPOSITORY_INFO", FndSqlType.STRING);
         FndSqlValue  docClass=new FndSqlValue("DOC_CLASS",doc_class,false);
         stmt.defineInParameter(docClass);
         FndSqlValue  docNo=new FndSqlValue("DOC_NO",doc_no,false);
         stmt.defineInParameter(docNo);
         FndSqlValue  docSheet=new FndSqlValue("DOC_SHEET",doc_sheet,false);
         stmt.defineInParameter(docSheet);
         FndSqlValue  docRev=new FndSqlValue("DOC_REV",doc_rev,false);
         stmt.defineInParameter(docRev);
         FndSqlValue  fileType=new FndSqlValue("FILE_TYPE",file_type,false);
         stmt.defineInParameter(fileType);
         stmt.execute();
         repoInfo = stmt.getString(1);
      }finally{
         try {
            stmt.close();
         } catch (SystemException e) {
            e.printStackTrace();
         }
      }
      return repoInfo;		   
         }
   
   private static String getRepositoryInfo3(
         String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev,
         String file_no,
         String file_name) throws FndException, IfsException {
      String repoInfo = null;
      FndJ2eeConnectionManager fndJ2eeConnectionManager = new FndJ2eeConnectionManager();
      FndConnection conn = fndJ2eeConnectionManager.getPlsqlConnection("IFSAPP","zh");
      FndStatement stmt=null;
      String sqlGetEdmRepositoryInfo ="BEGIN ? := IFSAPP.Edm_File_Api.Get_Edm_Repository_Info3(?,?,?,?,?,?); END;";
      try{
         stmt=conn.createStatement();
         stmt.prepareCall(sqlGetEdmRepositoryInfo);
         
         stmt.defineOutParameter("EDM_REPOSITORY_INFO", FndSqlType.STRING);
         FndSqlValue  docClass=new FndSqlValue("DOC_CLASS",doc_class,false);
         stmt.defineInParameter(docClass);
         FndSqlValue  docNo=new FndSqlValue("DOC_NO",doc_no,false);
         stmt.defineInParameter(docNo);
         FndSqlValue  docSheet=new FndSqlValue("DOC_SHEET",doc_sheet,false);
         stmt.defineInParameter(docSheet);
         FndSqlValue  docRev=new FndSqlValue("DOC_REV",doc_rev,false);
         stmt.defineInParameter(docRev);
         FndSqlValue  fileNo=new FndSqlValue("FILE_TYPE",file_no,false);
         stmt.defineInParameter(fileNo);
         FndSqlValue  fileName=new FndSqlValue("FILE_TYPE",file_name,false);
         stmt.defineInParameter(fileName);
         stmt.execute();
         repoInfo = stmt.getString(1);
      }finally{
         try {
            stmt.close();
         } catch (SystemException e) {
            e.printStackTrace();
         }
      }
      return repoInfo;		   
   }
   
   
   
   private static String addKeys(String info,
         String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev)
   {
      StringBuffer strBuff = new StringBuffer(info);
      strBuff.append("DOC_CLASS="+doc_class+"^");
      strBuff.append("DOC_NO="+doc_no+"^");
      strBuff.append("DOC_SHEET="+doc_sheet+"^");
      strBuff.append("DOC_REV="+doc_rev+"^");
      return strBuff.toString();
   }
   
   public static String[] checkInFile(
         String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev,
         String original_file_name,
         String file_type,
         String file_no)throws FndException, IfsException{
      String[] out = new String[3];
      FndJ2eeConnectionManager fndJ2eeConnectionManager = new FndJ2eeConnectionManager();
      FndConnection conn = fndJ2eeConnectionManager.getPlsqlConnection("IFSAPP","zh");
      FndStatement stmt=null;
      String sqlCheckInFile ="BEGIN IFSAPP.Edm_File_API.Check_In_File_(?,?,?,?,?,?,?,?,?,?,?); END;";
      try{
         stmt=conn.createStatement();
         stmt.prepareCall(sqlCheckInFile);
         
         stmt.defineOutParameter("OUT_1", FndSqlType.STRING);
         stmt.defineOutParameter("OUT_2", FndSqlType.STRING);
         stmt.defineOutParameter("OUT_3", FndSqlType.STRING);
         FndSqlValue  docClass=new FndSqlValue("DOC_CLASS",doc_class,false);
         stmt.defineInParameter(docClass);
         FndSqlValue  docNo=new FndSqlValue("DOC_NO",doc_no,false);
         stmt.defineInParameter(docNo);
         FndSqlValue  docSheet=new FndSqlValue("DOC_SHEET",doc_sheet,false);
         stmt.defineInParameter(docSheet);
         FndSqlValue  docRev=new FndSqlValue("DOC_REV",doc_rev,false);
         stmt.defineInParameter(docRev);
         FndSqlValue  originalFileName=new FndSqlValue("ORIGINAL_FILE_NAME",original_file_name,false);
         stmt.defineInParameter(originalFileName);
         FndSqlValue  fileType=new FndSqlValue("FILE_TYPE",file_type,false);
         stmt.defineInParameter(fileType);
         FndSqlValue  checkCurrentState=new FndSqlValue("CHECK_CURRENT_STATE","FALSE",false);
         stmt.defineInParameter(checkCurrentState);
         FndSqlValue  fileNo=new FndSqlValue("FILE_NO",file_no,false);
         stmt.defineInParameter(fileNo);
         stmt.execute();
         out[0] = stmt.getString(1);
         out[1] = stmt.getString(2);
         out[2] = stmt.getString(3);
      }finally{
         try {
            stmt.close();
         } catch (SystemException e) {
            e.printStackTrace();
         }
      }
      return out;
   }
   
   private static void setFileState(
         String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev,
         String file_type,
         String file_no,
         String file_state)throws FndException, IfsException{
      FndJ2eeConnectionManager fndJ2eeConnectionManager = new FndJ2eeConnectionManager();
      FndConnection conn = fndJ2eeConnectionManager.getPlsqlConnection("IFSAPP","zh");
      FndStatement stmt=null;
      String sqlCheckInFile ="BEGIN IFSAPP.Edm_File_API.Set_File_State(?,?,?,?,?,?,?,?); END;";
      try{
         stmt=conn.createStatement();
         stmt.prepareCall(sqlCheckInFile);
         
         FndSqlValue  docClass=new FndSqlValue("DOC_CLASS",doc_class,false);
         stmt.defineInParameter(docClass);
         FndSqlValue  docNo=new FndSqlValue("DOC_NO",doc_no,false);
         stmt.defineInParameter(docNo);
         FndSqlValue  docSheet=new FndSqlValue("DOC_SHEET",doc_sheet,false);
         stmt.defineInParameter(docSheet);
         FndSqlValue  docRev=new FndSqlValue("DOC_REV",doc_rev,false);
         stmt.defineInParameter(docRev);
         FndSqlValue  fileType=new FndSqlValue("FILE_TYPE",file_type,false);
         stmt.defineInParameter(fileType);
         FndSqlValue  fileNo=new FndSqlValue("FILE_NO",file_no,false);
         stmt.defineInParameter(fileNo);
         FndSqlValue  checkCurrentState=new FndSqlValue("FILE_STATE",file_state,false);
         stmt.defineInParameter(checkCurrentState);
         FndSqlValue  fileName=new FndSqlValue("FILE_NAME","",false);
         stmt.defineInParameter(fileName);
         stmt.execute();
      }finally{
         try {
            stmt.close();
         } catch (SystemException e) {
            e.printStackTrace();
         }
      }
   }
   
   private static void removeInvalidReference(
         String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev,
         String file_type,
         String file_no)throws FndException, IfsException {
      FndJ2eeConnectionManager fndJ2eeConnectionManager = new FndJ2eeConnectionManager();
      FndConnection conn = fndJ2eeConnectionManager.getPlsqlConnection("IFSAPP","zh");
      FndStatement stmt=null;
      String sqlCheckInFile ="BEGIN IFSAPP.Edm_File_API.Remove_Invalid_Reference_(?,?,?,?,?,?); END;";
      try{
         stmt=conn.createStatement();
         stmt.prepareCall(sqlCheckInFile);
         
         FndSqlValue  docClass=new FndSqlValue("DOC_CLASS",doc_class,false);
         stmt.defineInParameter(docClass);
         FndSqlValue  docNo=new FndSqlValue("DOC_NO",doc_no,false);
         stmt.defineInParameter(docNo);
         FndSqlValue  docSheet=new FndSqlValue("DOC_SHEET",doc_sheet,false);
         stmt.defineInParameter(docSheet);
         FndSqlValue  docRev=new FndSqlValue("DOC_REV",doc_rev,false);
         stmt.defineInParameter(docRev);
         FndSqlValue  fileType=new FndSqlValue("FILE_TYPE",file_type,false);
         stmt.defineInParameter(fileType);
         FndSqlValue  fileNo=new FndSqlValue("FILE_NO",file_no,false);
         stmt.defineInParameter(fileNo);
         stmt.execute();
      }finally{
         try {
            stmt.close();
         } catch (SystemException e) {
            e.printStackTrace();
         }
      }
   }
   
   
   
   
   /**
    *Use this method to put the files into respective repository for
    *each file. Use only if files are in different repositories.
    * @throws IfsException 
    */
   protected static void putFilesToRepository(String rep_info, String local_file_names[], String repository_file_names[]) throws FndException, IfsException
   {
      String rep_type;
      String rep_address;
      String rep_user;
      String rep_password;
      String rep_port;
      String local_file;
      String tmpPath = "";
      
      try
      {
         tmpPath = getTmpPath();
      }
      catch(ifs.fnd.service.FndException e)
      {
      }
      
      String doc_class = getStringAttribute(rep_info, "DOC_CLASS");
      String doc_no    = getStringAttribute(rep_info, "DOC_NO");
      String doc_sheet = getStringAttribute(rep_info, "DOC_SHEET");
      String doc_rev   = getStringAttribute(rep_info, "DOC_REV");      
      
      int no_files = local_file_names.length;
      String edm_rep_info;
      
      for (int i = 0; i < no_files; i++) {
         edm_rep_info = getRepositoryInfo3(doc_class, doc_no, doc_sheet, doc_rev, "1", repository_file_names[i]);
         rep_type = getStringAttribute(edm_rep_info, "LOCATION_TYPE");
         if ("2".equals(rep_type))
         {
            local_file = tmpPath + local_file_names[i];
            
            rep_address  = getStringAttribute(edm_rep_info, "LOCATION_ADDRESS");
            rep_user     = getStringAttribute(edm_rep_info, "LOCATION_USER");
            rep_password = decryptFtpPassword(getStringAttribute(edm_rep_info, "LOCATION_PASSWORD"));
            rep_port     = getStringAttribute(edm_rep_info, "LOCATION_PORT");
            
            if (DEBUG) Util.debug("XXXXXXXXXXXXXXX____FTP_REP___XXXXXXXXXXXXXXXXXX");
            putSingleFileToFtpRepository(rep_address, rep_port, rep_user, rep_password, local_file, repository_file_names[i]);
         }
      }
      
   }
   private static void putSingleFileToFtpRepository(String ftp_address,
         String ftp_port,
         String ftp_user,
         String ftp_password,
         String local_file,
         String ftp_file_name) throws FndException, IfsException {
      if (DEBUG) Util.debug(": putSingleFileToFtpRepository() }");
      DocmawFtp ftp_server = new DocmawFtp();
      int port_no = Str.isEmpty(ftp_port) ? 21 : Integer.parseInt(ftp_port);
      String tmpPath = getTmpPath();
      String local_file_ = local_file.startsWith(tmpPath) ? local_file : tmpPath + local_file;
      
      if (ftp_server.login(ftp_address, ftp_user, ftp_password, port_no))
      {
         ftp_server.putBinaryFile(local_file_, ftp_file_name);
	         File tmp_file = new File(local_file_);
	         if (!tmp_file.delete())
	         {
	            throw new FndException("The file &1 could not be deleted.");
	         }
         ftp_server.logoff();
      }
      else
      {
         throw new FndException("DOCMAWDOCSRVFTPLOGINFAILED3: Login to FTP server &1 on port: &2 with login name &3 failed.", ftp_address, Integer.toString(port_no), ftp_user);
      }
   }
   
   public static String getDefaultValue(String lu_name, String entry_code) throws IfsException{
      String defaultValue = null;
      FndJ2eeConnectionManager fndJ2eeConnectionManager = new FndJ2eeConnectionManager();
      FndConnection conn = fndJ2eeConnectionManager.getPlsqlConnection("IFSAPP","zh");
      FndStatement stmt=null;
      String sqlGetFileType ="BEGIN ? := IFSAPP.Docman_Default_API.Get_Default_Value_(?,?); END;";
      try{
         stmt=conn.createStatement();
         stmt.prepareCall(sqlGetFileType);
         stmt.defineOutParameter("DEFAULT_VALUE", FndSqlType.STRING);
         FndSqlValue luName=new FndSqlValue("LU_NAME",lu_name,false);
         stmt.defineInParameter(luName);
         FndSqlValue entryCode=new FndSqlValue("ENTRY_CODE",entry_code,false);
         stmt.defineInParameter(entryCode);
         stmt.execute();
         defaultValue = stmt.getString(1);
      }finally{
         try {
            stmt.close();
         } catch (SystemException e) {
            e.printStackTrace();
         }
      }
      return defaultValue;		   
   }
   
   /**
    * This returns the temporary path ending with a system dependent path separator.
    * @throws IfsException 
    */
   protected static String getTmpPath() throws FndException, IfsException
   {
      String      path = null;
      boolean     is_absolute;
      String defaultPath = getDefaultValue("DocIssue", "SYSCFG_SHARED_PATH_FNDWEB");
      if (defaultPath == null)
      {
         throw new FndException("DEFAULTSYSCFGPATHFNDWEBNOTDEFINED: The path for default value SYSCFG_SHARED_PATH_FNDWEB is not defined. You can define this in Docman - Basic Data - Default Values.");
      }
      if (!(new File(defaultPath)).exists())
      {
         throw new FndException("DEFAULTSYSCFGPATHFNDWEBNOTEXISTS: The path set for default value SYSCFG_SHARED_PATH_FNDWEB does not exist. You can change this in Docman - Basic Data - Default Values.");
      }
      if ( ! defaultPath.endsWith( File.separator )  ) {
         defaultPath += File.separator;
      }
      is_absolute = new File(defaultPath).isAbsolute();
      if(is_absolute)
      {
         path = defaultPath;
         if (DEBUG) Util.debug("DocSrv.getTmpPath - path 1 = " + path);
      }
      else
      {
         throw new FndException("TmpPath should be absolute path.");
      }
      // Make sure it ends with a path separator
      path = path.charAt(path.length() - 1) == File.separatorChar ? path : path + File.separator;
      // Check that path exist
      File tmp_dir = new File(path);
      if (DEBUG) Util.debug("DocSrv.getTmpPath - path 3 = " + path);
      if (!tmp_dir.exists())
      {
         createServerPath(path);
      }
      if (DEBUG) Util.debug("DocSrv.getTmpPath - path 4 = " + path);
      return path;
   }
   // Create folder on extended server
   private static void createServerPath(String path) throws FndException
   {
      File server_path = new File(path);
      
      if (!server_path.exists())
      {
         try
         {
            server_path.mkdirs();// Attempt to create the temporary path..
         }
         catch(Exception e)
         {
            throw new FndException("DOCMAWDOCSRVTMPPATHNOTEXIST: The directory specified by the parameter DOCMAW/DOCUMENT_TEMP_PATH could not be created on the server. The problem may be due to insufficient privileges. Contact your system adminstrator.");
         }
      }
   }
   
   private static String getStringAttribute(String attr_string, String attr_name)
   {
      StringTokenizer st = new StringTokenizer(attr_string, "^");
      attr_name += "=";
      while (st.hasMoreTokens())
      {
         String str = st.nextToken();
         if (str.startsWith(attr_name))
         {
            return str.substring(attr_name.length());
         }
      }
      return "";
   }
   
   private static String decryptFtpPassword(String crypt_pwd)
   {
      if (DEBUG) Util.debug(DocumentCheckIn.class+": decryptFtpPassword() {");
      StringBuffer uncrypt_pwd = new StringBuffer();
      for (int i = crypt_pwd.length()-1; i>=0; i--)
      {
         char ch = crypt_pwd.charAt(i);
         switch (ch)
         {
         case 1: ch = 94;
         break;
         case 2: ch = 61;
         break;
         }
         if ((crypt_pwd.length() - i + 1) % 2 == 0)
         {
            ch = (char) (ch + (crypt_pwd.length() - i));
         }
         else
         {
            ch = (char) (ch - (crypt_pwd.length() - i));
         }
         uncrypt_pwd.append(ch);
      }
      if (DEBUG) Util.debug(DocumentCheckIn.class+":   decrypted FTP password: " + uncrypt_pwd.toString());
      if (DEBUG) Util.debug(DocumentCheckIn.class+":   decryptFtpPassword() {");
      return uncrypt_pwd.toString();
   }
}
