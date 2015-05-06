package ifs.wordmw;


import ifs.docmaw.DocmawUtil;
import ifs.docmaw.edm.DocmawFtp;
import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPLog;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.buffer.Buffer;
import ifs.fnd.service.FndException;
import ifs.fnd.service.FndRuntimeException;
import ifs.fnd.service.Util;
import ifs.fnd.util.Str;
import ifs.hzwflw.HzASPPageProviderWf;
import ifs.wordmw.util.GetWordFromOracle;
import ifs.wordmw.util.Word2Pdf;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

public abstract class GoldGridProvider extends HzASPPageProviderWf
{
   protected boolean wordOperate = false;
   
   protected boolean moreWordOperate = false;
   
   protected boolean signReadOnly = false;
   
   protected String blockName;
  
   protected String goldgridRecordFieldName;//  not DbName , fieldName
   
   protected String signFieldName;
   
   protected String goldgridTemplateFieldName;
   
   protected String recordId = "";
   
   protected String templateId = "";
   
   private String bookMarksList = "";
   
   private ASPBlock ggpblk;
   
   private ASPTransactionBuffer trans;
   private ASPCommand cmd;
   
   String usbkeySn = "";
   String usbkeyPassword = "";
   
   
//   protected String docClass = null;
//   protected String docNo = null;
//   protected String docSheet = null;
//   protected String docRev = null;
//   protected String originalFileName = null;
//   protected String server_file_name = null;
   
   
   public GoldGridProvider(ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }
   
   public void run()  throws FndException
   
   {
      
      super.run();
      ASPManager mgr = getASPManager();
      
      usbkeySn = mgr.readValue("USBKEYSN","");
      trans = mgr.newASPTransactionBuffer();
      
      if(!mgr.isEmpty(mgr.readValue("_CURRTEMPLATEID")) && "1".equals(mgr.readValue("_CURRTEMPLATECOUNT")))
      {
         templateId=mgr.readValue("_CURRTEMPLATEID");
      }
      initParameter();
   }
   
   public void preDefine()
   {
      ASPManager mgr = getASPManager();

      ggpblk = mgr.newASPBlock("GGPBLK");

      ggpblk.addField("IN_1");
      ggpblk.addField("IN_STR1");
      ggpblk.addField("STR_OUT");
      ggpblk.addField("INOUT_STR1");
      ggpblk.addField("IN_NUM","Number");
      ggpblk.addField("OUT_1");
      ggpblk.addField("OUT_2");
      ggpblk.addField("OUT_3");
      ggpblk.addField("OUT_4");
      ggpblk.addField("OUT_5");
   }
   
   
   public  void adjust() throws FndException
   {
      super.adjust();
      if(needOpenMoreWord()){
         openMoreWord();
      }
   }
   
   protected abstract boolean goldGridActivated();
   
   private boolean needOpenMoreWord()
   {
      wordOperate = false;
      ASPManager mgr = getASPManager();
      ASPBlock blk=mgr.getASPPage().getASPBlock(blockName);
      ASPBlockLayout lay=blk.getASPBlockLayout();
      if (lay.isSingleLayout() || lay.isEditLayout())
      {
         
         if(blk.getASPRowSet().countRows() > 0 ){
            recordId=blk.getASPRowSet().getClientValue(goldgridRecordFieldName);
         }
         if(false == mgr.isEmpty(recordId) && goldGridActivated() )
            wordOperate = true;
      }
      return wordOperate;
   }
   
   public void openMoreWord()
   {
      ASPManager mgr = getASPManager();
      ASPBlock blk=mgr.getASPPage().getASPBlock(blockName);
      ASPBlockLayout lay=blk.getASPBlockLayout();
      if (lay.isMultirowLayout())
         blk.getASPRowSet().goTo(blk.getASPRowSet().getRowSelected());
      if(blk.getASPRowSet().countRows()==0)
      {
         return;
      }
      recordId=blk.getASPRowSet().getClientValue(goldgridRecordFieldName);
      if(mgr.isEmpty(templateId))
      {
         templateId=blk.getASPRowSet().getClientValue(goldgridTemplateFieldName);
      }
      
      //设置只读标记,
      setSignRead();
      
      if(!mgr.isEmpty(recordId))
      {
         //如果业务表里已经关联recordId,说明该业务数据已经存在文档实例
         if(!signReadOnly)
            setLayoutMode(lay,lay.EDIT_LAYOUT);
         else
            setLayoutMode(lay,lay.SINGLE_LAYOUT);
      }
      else if(!mgr.isEmpty(templateId))
      {
         //如果业务表里没有关联recordId,并且模板编号不为空，说明已经选定了一个word模板
         recordId=createRecordId();
         setLayoutMode(lay,lay.EDIT_LAYOUT);
         blk.getASPRowSet().setValue(goldgridRecordFieldName, recordId);
         blk.getASPRowSet().setValue(goldgridTemplateFieldName, templateId);
      }
      else
      {
         //如果业务表里没有关联recordId，并且模板编号为空。则直接从数据库中查找该页面是否存在模板.
         templateId=getTemplateId();
         if(!mgr.isEmpty(templateId))
         {
            //如果当前页面只存在一个模板，则取出该模板。
            recordId=createRecordId();
            setLayoutMode(lay,lay.EDIT_LAYOUT);
            blk.getASPRowSet().setValue(goldgridRecordFieldName, recordId);
            blk.getASPRowSet().setValue(goldgridTemplateFieldName, templateId);
         }
         else
         {
            //如果存在多个，或则在没有。弹出一个提示框
            setLayoutMode(lay,lay.SINGLE_LAYOUT);
            moreWordOperate=true;
         }
      }
      
      if(!mgr.isEmpty(templateId)||!mgr.isEmpty(recordId))
      {
         //需要从数据库中取出该模板的标签
         wordOperate=true;
      }
   }
   
   public String[] convertDocToPdf() throws FndException{
	      ASPManager mgr = getASPManager();
	      ASPBlock blk = mgr.getASPPage().getASPBlock(blockName);
	      ASPBlockLayout lay = blk.getASPBlockLayout();
	      blk.getASPRowSet().store();
	      if (lay.isMultirowLayout())
	         blk.getASPRowSet().goTo(blk.getASPRowSet().getRowSelected());
//	      if (blk.getASPRowSet().getSelectedRows().countItems() != 1)
//	      {
//	         mgr.showAlert("WORDMWGGPSELONEREC: You must select one record.");
//	         return null;
//	      }
	      
	      recordId = blk.getASPRowSet().getClientValue(goldgridRecordFieldName);
	      if (!mgr.isEmpty(recordId))
	      {
	         String tempLocation = getTmpPath();
	         GetWordFromOracle wordGetter = new GetWordFromOracle(recordId, tempLocation);
	         String wordFilePath = wordGetter.getFile();
	         String pdfFilePath = Word2Pdf.transfer(wordFilePath);
	         return new String[]{wordFilePath,pdfFilePath};
	      }
	      else
	      {
	         mgr.showAlert("WORDMWGGPNOWORDFILE: You need to associate a word document.");
	      }
	   return null;
   }
   
   public void saveAsPdf() throws FndException{
      throw new FndRuntimeException("This method is not intend to be called.");
   }
   
   
   
   
   public void saveAsPdf1(String docClass, String docNo, String docSheet, String docRev, String originalFileName) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPBlock blk = mgr.getASPPage().getASPBlock(blockName);
//      System.out.println(System.getProperty("java.library.path"));
      ASPBlockLayout lay = blk.getASPBlockLayout();
      blk.getASPRowSet().store();
      if (lay.isMultirowLayout())
         blk.getASPRowSet().goTo(blk.getASPRowSet().getRowSelected());
      if (blk.getASPRowSet().getSelectedRows().countItems() != 1)
      {
         mgr.showAlert("WORDMWGGPSELONEREC: You must select one record.");
         return;
      }
      
      recordId = blk.getASPRowSet().getClientValue(goldgridRecordFieldName);
      if (!mgr.isEmpty(recordId))
      {
         String tempLocation = getTmpPath();
         
         GetWordFromOracle wordGetter = new GetWordFromOracle(recordId, tempLocation);
         String wordFilePath = wordGetter.getFile();
         String pdfFilePath = Word2Pdf.transfer(wordFilePath);
         
         String server_file_name = DocmawUtil.getFileName(pdfFilePath);
//         String original_file_name = "测试WORD自动转换PDF.PDF";
         checkInDocument(docClass, docNo, docSheet, docRev, server_file_name, originalFileName);
//         checkInDocument("PRJSDL", "1000123", "1", "A1", server_file_name, originalFileName);
         deleteFile(wordFilePath);
         
         // mgr.showAlert("conver to pdf successfully! location:" + pdfFilePath);
      }
      else
      {
         mgr.showAlert("WORDMWGGPNOWORDFILE: You need to associate a word document.");
      }
   }
   
   
   /**
    * 该方法是为了避免重复设置layoutMode，
    * 因为如果在编辑状态下在设置一遍状态为编辑状态的话，取消编辑或保存的时候，
    * 会变回原来的layoutMode，仍然是编辑状态。
    * @param lay
    * @param layOutMode
    */
   private void setLayoutMode(ASPBlockLayout lay, int layOutMode)
   {
      if(lay.getLayoutMode() != layOutMode)
      {
         lay.setLayoutMode(layOutMode);
      }
   }
   
   private String getTemplateId()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomCommand("GETTEMPLATEID", "Business_Template_API.Get_Templateid_By_Page");
      cmd.addParameter("PAGE_URL","S","IN", mgr.getURL());
      cmd.addParameter("TEMPLATEID","S","OUT", "");
      trans = mgr.perform(trans);
      String templateId_ = trans.getValue("GETTEMPLATEID/DATA/TEMPLATEID");
      return templateId_;
   }
   
   private String getBookMarksList(String _templateId)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd = trans.addCustomCommand("GETBOOKMARKLIST", "Gd_Template_Bookmarks_API.Get_BookmarkList");
      cmd.addParameter("RECORDID", "S", "IN", _templateId);
      cmd.addParameter("BOOKMARK_LIST", "S", "OUT", "");
      trans = mgr.perform(trans);
      String bookmark_list = trans.getValue("GETBOOKMARKLIST/DATA/BOOKMARK_LIST");
      if(mgr.isEmpty(bookmark_list))
      {
         bookmark_list="";
      }
      return bookmark_list;
   }
   
   protected void addGoldGridCmd(ASPCommandBar bar)
   {
      bar.addCustomCommand("openMoreWord", "WORDMWGGPBAROPENTEMP: Open Word Template...");
      bar.removeFromMultirowAction("openMoreWord");
   }
   
   private void setSignRead()
   {
      // 设置只读标识：1.当只读控制字段的值为true;2.当工作流所在的处理页面不允许时，已办页面，送阅页面等
      ASPManager mgr = getASPManager();
      ASPBlock blk=mgr.getASPPage().getASPBlock(blockName);
      String singFieldValue = blk.getASPRowSet().getClientValue(signFieldName);
      if("TRUE".equals(singFieldValue))
      {
         signReadOnly=true;
      }
      
      ASPBlockLayout lay=blk.getASPBlockLayout();
      if (lay.isSingleLayout()){
         signReadOnly = true;
      }else if(lay.isEditLayout()){
         signReadOnly = false;
      }
      //TODO add workflow logic control.
   }
   
   /**
    * 设置初始话参数
    */
   protected  abstract void initParameter();
   
   
   /**
    * 产生文档唯一编号
    * @return
    */
   protected String createRecordId()
   {
      java.util.Date dt=new java.util.Date();
      long lg=dt.getTime();
      Long ld=new Long(lg);
      return ld.toString();
   }
   
   // Added by Terry 20130320
   public void checkInDocument(
         String doc_class,
         String doc_no,
         String doc_sheet,
         String doc_rev,
         String server_file_name,
         String original_file_name) throws FndException
   {
      if (DEBUG) Util.debug(this + ": checkInDocument() {");

      ASPManager mgr = getASPManager();

      String doc_type = "ORIGINAL";
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      trans.clear();
      cmd = trans.addCustomFunction("GETNEXTFILENO",  "Edm_File_API.Get_Next_File_No", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      
      trans = perform(trans);

      String file_no = trans.getValue("GETNEXTFILENO/DATA/OUT_1");
      
      String file_ext = DocmawUtil.getFileExtention(server_file_name);
      String file_type = getFileType(file_ext.toUpperCase());

      trans.clear();
      cmd = trans.addCustomFunction("GETREMOTEFILENAME", "Edm_File_Api.Generate_Remote_File_Name", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", file_ext);
      cmd.addParameter("IN_NUM",  file_no);

      cmd = trans.addCustomFunction("EDMREPINFO", "Edm_File_Api.Get_Edm_Repository_Info2", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);

      trans = perform(trans);

      String repository_file_name = trans.getValue("GETREMOTEFILENAME/DATA/OUT_1");;
      String edm_rep_info = trans.getValue("EDMREPINFO/DATA/OUT_1");

      if (DEBUG)
         Util.debug(this + ": Creating file references...");

      trans.clear();
      
      cmd = trans.addCustomCommand("CHECKINNEW", "Edm_File_API.Check_In_File_");
      cmd.addParameter("OUT_1");
      cmd.addParameter("OUT_2");
      cmd.addParameter("OUT_3"); // Bug 59605, indicates if a new reference was created
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", original_file_name);
      cmd.addParameter("IN_STR1", file_type);
      cmd.addParameter("IN_STR1", "FALSE");
      cmd.addParameter("IN_NUM",  file_no);
      cmd = trans.addCustomCommand("SETSTATECHECKOUT", "Edm_File_API.Set_File_State");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      cmd.addParameter("IN_NUM",  file_no);
      cmd.addParameter("IN_STR1", "StartCheckOut");
      cmd.addParameter("IN_STR1", null);
      
      trans = perform(trans);

      // Put all files to the repository...
      // bug 58326, starts....
      String[] local_file_names = new String[1];
      String[] repository_file_names = new String[1];
      local_file_names[0] = server_file_name;
      repository_file_names[0] = repository_file_name;
      
      //
      // Bug 59605, Start
      // Check in the files to the repository. If an exception occurs,
      // role-back the state changes from Operation In Progress
      //
      try
      {
         putFilesToRepository(addKeys(edm_rep_info, doc_class, doc_no, doc_sheet, doc_rev), local_file_names, repository_file_names);
      }
      catch (FndException fnd)
      {
         ASPTransactionBuffer trans2 = mgr.newASPTransactionBuffer();

         boolean new_file = "TRUE".equals(trans.getValue("CHECKINNEW/DATA/OUT_3"));
         
         if (new_file)
         {
            cmd = trans2.addCustomCommand("DELETEFILEREF", "Edm_File_API.Remove_Invalid_Reference_");
            cmd.addParameter("IN_STR1", doc_class);
            cmd.addParameter("IN_STR1", doc_no);
            cmd.addParameter("IN_STR1", doc_sheet);
            cmd.addParameter("IN_STR1", doc_rev);
            cmd.addParameter("IN_STR1", trans.getValue("CHECKINNEW/DATA/OUT_1"));
            cmd.addParameter("IN_NUM",  file_no);
         }
         else
         {
            cmd = trans2.addCustomCommand("SETSTATECHECKOUT", "Edm_File_API.Set_File_State");
            cmd.addParameter("IN_STR1", doc_class);
            cmd.addParameter("IN_STR1", doc_no);
            cmd.addParameter("IN_STR1", doc_sheet);
            cmd.addParameter("IN_STR1", doc_rev);
            cmd.addParameter("IN_STR1", trans.getValue("CHECKINNEW/DATA/OUT_1"));
            cmd.addParameter("IN_NUM",  file_no);
            cmd.addParameter("IN_STR1", "FinishCheckOut");
            cmd.addParameter("IN_STR1", null);
         }
         perform(trans2);

         // re-raise exception..
         throw fnd;
      }

      if (DEBUG)
         Util.debug(this + ": Files checked in to repository sucessfully..");
      if (DEBUG)
         Util.debug(this + ": Changing edm state..");

      trans.clear();
      
      cmd = trans.addCustomCommand("SETSTATECHECKIN", "Edm_File_API.Set_File_State");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      cmd.addParameter("IN_NUM",  file_no);
      cmd.addParameter("IN_STR1", "FinishCheckIn");
      cmd.addParameter("IN_STR1", null);
      
      trans = perform(trans);
      
   }
   
   public String getEdmInformation(String doc_class, String doc_no, String doc_sheet, String doc_rev, String doc_type) throws FndException
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("EDMINFO", "Edm_File_API.Get_Edm_Information", "OUT_1");
      cmd.addParameter("IN_STR1", doc_class);
      cmd.addParameter("IN_STR1", doc_no);
      cmd.addParameter("IN_STR1", doc_sheet);
      cmd.addParameter("IN_STR1", doc_rev);
      cmd.addParameter("IN_STR1", doc_type);
      trans = perform(trans);
      
      return trans.getValue("EDMINFO/DATA/OUT_1");
   }
   
   // Get file type with file extention.
   public String getFileType(String file_ext) throws FndException
   {
      ASPManager mgr = getASPManager();
      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("GETFILETYPE", "Edm_Application_API.Get_File_Type", "OUT_1");
      cmd.addParameter("IN_STR1", file_ext);
      trans = perform(trans);
      return trans.getValue("GETFILETYPE/DATA/OUT_1");
   }
   
   private String addKeys(String info,
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
   
   /**
    *Use this method to put the files into respective repository for
    *each file. Use only if files are in different repositories.
    */
   protected void putFilesToRepository(String rep_info, String local_file_names[], String repository_file_names[]) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer edm_rep_info_trans = mgr.newASPTransactionBuffer();
      
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
      
      edm_rep_info_trans.clear();
      for (int i = 0;i<no_files;i++)
      {
         cmd = edm_rep_info_trans.addCustomFunction("EDMREPINFO_"+i, "Edm_File_Api.Get_Edm_Repository_Info3", "OUT_1");
         cmd.addParameter("IN_STR1", doc_class);
         cmd.addParameter("IN_STR1", doc_no);
         cmd.addParameter("IN_STR1", doc_sheet);
         cmd.addParameter("IN_STR1", doc_rev);
         cmd.addParameter("IN_NUM",  "1");
         cmd.addParameter("IN_STR1", repository_file_names[i]);         
      }
      
      edm_rep_info_trans = perform(edm_rep_info_trans);
      
      for (int i = 0;i<no_files;i++)
      {         
         edm_rep_info = edm_rep_info_trans.getValue("EDMREPINFO_" + i + "/DATA/OUT_1");
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
   
   protected void putSingleFileToFtpRepository(String ftp_address,
         String ftp_port,
         String ftp_user,
         String ftp_password,
         String local_file,
         String ftp_file_name) throws FndException
   {
      if (DEBUG) Util.debug(this+": putSingleFileToFtpRepository() }");
      
      ASPManager mgr = getASPManager();
      DocmawFtp ftp_server = new DocmawFtp();
      
      int port_no = Str.isEmpty(ftp_port) ? 21 : Integer.parseInt(ftp_port);
      
      String tp = getTmpPath();
      String local_file_ = local_file.startsWith(tp) ? local_file : tp + local_file;
      
      if (ftp_server.login(ftp_address, ftp_user, ftp_password, port_no))
      {
         ftp_server.putBinaryFile(local_file_, ftp_file_name);
//         deleteFile(local_file_);
         ftp_server.logoff();
      }
      else
      {
         throw new FndException(mgr.translate("DOCMAWDOCSRVFTPLOGINFAILED3: Login to FTP server &1 on port: &2 with login name &3 failed.", ftp_address, Integer.toString(port_no), ftp_user));
      }
   }
   
   /**
    * This returns the temporary path ending with a system dependent path separator.
    */
   protected String getTmpPath() throws FndException
   {
      String      path = null;
      File        tmpDir;
      boolean     is_absolute;
      
      ASPManager mgr = getASPManager();
      
      //bug 58326
      trans = mgr.newASPTransactionBuffer();
      cmd = trans.addCustomFunction("DEFAULTSYSCFGPATHFNDWEB", "Docman_Default_API.Get_Default_Value_", "OUT_1");
      cmd.addParameter("IN_STR1","DocIssue");
      cmd.addParameter("IN_STR1","SYSCFG_SHARED_PATH_FNDWEB");
      trans = mgr.perform(trans);
      
      String defaultPath = trans.getValue("DEFAULTSYSCFGPATHFNDWEB/DATA/OUT_1");
      
      
      //Bug Id 57006, Start 
      if (defaultPath == null)
      {
         //Bug 60903, Start, Modified the exception
         throw new FndException(mgr.translate("DEFAULTSYSCFGPATHFNDWEBNOTDEFINED: The path for default value SYSCFG_SHARED_PATH_FNDWEB is not defined. You can define this in Docman - Basic Data - Default Values."));
         //Bug 60903, End
      }
      if (!(new File(defaultPath)).exists())
      {
         //Bug 60903, Start, Modified the exception
         throw new FndException(mgr.translate("DEFAULTSYSCFGPATHFNDWEBNOTEXISTS: The path set for default value SYSCFG_SHARED_PATH_FNDWEB does not exist. You can change this in Docman - Basic Data - Default Values."));
         //Bug 60903, End
      }
      if ( ! defaultPath.endsWith( File.separator )  ) {
         defaultPath += File.separator;
      }
      //Bug Id 57006, End
      
      is_absolute = new File(defaultPath).isAbsolute();
      
      if(is_absolute)
      {
         /* For work with the existing hard coded path in docmawconfig.xml */
         path = defaultPath;
         if (DEBUG) Util.debug("DocSrv.getTmpPath - path 1 = " + path);
      }
      else
      {
         try
         {
            path = new File(mgr.getPhyPath(defaultPath)).getCanonicalPath();
            if (DEBUG) Util.debug("DocSrv.getTmpPath - path 2 = " + path);
         }
         catch(IOException e)
         {
            if (DEBUG) Util.debug("DocSrv.getTmpPath error: " + Str.getStackTrace(e));
            error(e);
         }
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
   
   /**
    * Deletes the specified local file
    */
   protected void deleteFile(String tmp_file_location) throws FndException
   {
      ASPManager mgr = getASPManager();
      tmp_file_location = handleBackSlash(tmp_file_location);
      File tmp_file = new File(tmp_file_location);
      if (!tmp_file.delete())
      {
         throw new FndException(mgr.translate("DOCMAWDOCSRVFILENOTDEL: The file &1 could not be deleted.", tmp_file_location));
      }
   }
   
   private String handleBackSlash(String input)
   {
      String output = "";
      for (int k=0;k<input.length();k++)
         if("\\".equals(input.charAt(k)+""))
            output += "\\\\";
         else
            output += input.charAt(k);
      return output;
   }
   
   protected String getStringAttribute(String attr_string, String attr_name)
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
   
   // Create folder on extended server
   private void createServerPath(String path) throws FndException
   {
      ASPManager mgr = getASPManager();
      
      File server_path = new File(path);
      
      if (!server_path.exists())
      {
         try
         {
            // Attempt to create the temp path..
            server_path.mkdirs();
         }
         catch(Exception e)
         {
            throw new FndException(mgr.translate("DOCMAWDOCSRVTMPPATHNOTEXIST: The directory specified by the parameter DOCMAW/DOCUMENT_TEMP_PATH could not be created on the server. The problem may be due to insufficient privileges. Contact your system adminstrator."));
         }
      }
   }
   
   public ASPTransactionBuffer perform(ASPTransactionBuffer trans) throws FndException
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer response = null;
      
      try
      {
         response = mgr.performEx(trans);
      }
      catch(ASPLog.ExtendedAbortException e)
      {
         Buffer info = e.getExtendedInfo();
         String error_message = info.getItem("ERROR_MESSAGE").toString();
         error_message = error_message.substring(error_message.indexOf("=") + 1);
         throw new FndException(error_message);
      }
      
      return response;
   }
   
   protected String decryptFtpPassword(String crypt_pwd)
   {
      if (DEBUG) Util.debug(this+": decryptFtpPassword() {");
      
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
      
      if (DEBUG) Util.debug(this+":   decrypted FTP password: " + uncrypt_pwd.toString());
      if (DEBUG) Util.debug(this+":   decryptFtpPassword() {");
      
      return uncrypt_pwd.toString();
   }
   // Added end
   
   private String firstToUpper(String str)
   {
      if (!"".equals(str) && !(null == str))
      {
         int len = str.length();
         String tempFirst = str.substring(0, 1).toUpperCase();
         String tempLast = str.substring(1, len).toLowerCase();
         str = tempFirst + tempLast;
      }
      return str;
   }
   
   public String getUsbKeyPassword(String usbKey){
	   if(Str.isEmpty(usbKey)){
		   return "";
	   }
	   ASPManager mgr = getASPManager();
	   ASPCommand  cmd = trans.addCustomFunction("PASSWORD", "USBKEY_PASSWORD_API.GET_PASSWORD","IN_1");
	   cmd.addParameter("IN_1", "S", "IN", usbKey); 
	   trans = mgr.validate(trans);
	   String password = trans.getValue("PASSWORD/DATA/IN_1");
	   
	   try {
		return ifs.wordmw.UsbkeyPassword.decodeString(password);
	} catch (IOException e) {
		return "";
	}
   }
   
   protected void printContents() throws FndException
   {
      super.printContents();
      ASPManager mgr = getASPManager();
      appendDirtyJavaScript("function setCurrTemplateId(tempId){\n");
      appendDirtyJavaScript("    document.getElementById('_CURRTEMPLATEID').value=tempId;\n");
      appendDirtyJavaScript("    document.getElementById('_CURRTEMPLATECOUNT').value=1;\n");
      appendDirtyJavaScript("    commandSet('"+blockName+".openMoreWord', '');\n");
      appendDirtyJavaScript("}\n");
      
      printHiddenField("USBKEYSN", "");
      
      if(wordOperate && (false == signReadOnly))
      {
         appendDirtyJavaScript("function addEventElement(){\n");
         appendDirtyJavaScript("    SetBookmarks(this.name.toUpperCase(),this.value);\n");
         appendDirtyJavaScript("}\n");
         appendDirtyJavaScript("function addEventOnblur()\n");
         appendDirtyJavaScript("{    \n");
         appendDirtyJavaScript("    for(var i = 0; i < f.elements.length; i++)\n");
         appendDirtyJavaScript("    {\n");
         appendDirtyJavaScript("      if((f.elements[i].type=='text') || (f.elements[i].type == 'textarea'))\n");
         appendDirtyJavaScript("      {\n");
         appendDirtyJavaScript("         f.elements[i].onblur=addEventElement;\n");
         appendDirtyJavaScript("      }\n");
         appendDirtyJavaScript("    }\n");
         appendDirtyJavaScript("}\n");
         appendDirtyJavaScript("addEventOnblur();\n");
         
         appendDirtyJavaScript("function saveAll(){\n");
         appendDirtyJavaScript("    SaveDocument();\n");
         appendDirtyJavaScript("    SyncWordDataToForm();\n");
         appendDirtyJavaScript("    commandSet('"+blockName+".SaveReturn', 'check"+firstToUpper(blockName)+"Fields(i)');;\n");
         appendDirtyJavaScript("}\n");
         
         appendDirtyJavaScript("var a_href=document.getElementsByName(\""+blockName+"_SaveReturn\")[0].parentNode;\n");
         
         appendDirtyJavaScript("if('A'==a_href.tagName.toUpperCase()){\n");
         appendDirtyJavaScript("  a_href.href=\"javascript:saveAll();\";\n");
         appendDirtyJavaScript("}\n");
      }
      if(moreWordOperate){
         appendDirtyJavaScript("showNewBrowser_('/b2e/secured/wordmw/TemplateSelecter.page?currUrl="+mgr.getURL()+"', width=700, height=400, 'YES');\n");
      }
      
   }
   
   
   protected void printGoldGrid()  throws FndException{
      ASPManager mgr = getASPManager();
      String WebUrl="http://"+mgr.getApplicationDomain()+"/b2e/WordServer.do";
      
      appendToHTML("<input  id=\"_CURRTEMPLATEID\" type=\"hidden\" name=\"_CURRTEMPLATEID\" value=\""+(templateId==null?"":templateId)+"\">\n");
      appendToHTML("<input  id=\"_CURRTEMPLATECOUNT\" type=\"hidden\" name=\"_CURRTEMPLATECOUNT\" value=\"0\">\n");
      appendToHTML("<input  id=\"COMMAND\" type=\"hidden\" name=\"COMMAND\" value=\"\">\n");
      
      if(wordOperate)
      {
         String menu1 = mgr.translate("WORDMWGGPWORDMENUOPEN: Open Local File...");
         String menu2 = mgr.translate("WORDMWGGPWORDMENUSAVELOCALFILE: Save Local File...");
         String menu3 = mgr.translate("WORDMWGGPWORDMENUSIGN: Sign...");
         String menu4 = mgr.translate("WORDMWGGPWORDMENUBOOKMARK: Bookmark Setting...");
         String menu5 = mgr.translate("WORDMWGGPWORDMENUPRINT: Print Document...");
         String menu6 = mgr.translate("WORDMWGGPWORDMENUTEMPSAVE: Temporary Saving");
         String menu7 = mgr.translate("WORDMWGGPWORDMENUSAVE: Save And Return");
         String menu8 = mgr.translate("WORDMWGGPWORDMENUFULLSCREEN: Full Screen");
         String menus = menu1 + ","   + menu2 + ",-," +
         menu3 + ","   + menu4 + ","   + 
         menu5 + ",-," + menu6 + ","   + 
         menu7 + ","   + menu8;
         appendToHTML("<script id=\"iWebOffice_Word\"  src=\"/b2e/unsecured/common/scripts/iWebOfficeWord.js\" language=\"javascript\"  " 
               + "iWebOffice_RecordID=\"" + (recordId == null ? "" : recordId) + "\" "
               + "iWebOffice_Template=\"" + (templateId == null ? "" : templateId) + "\" "
               + "iWebOffice_FileName=\"" + (recordId == null ? "" : (recordId + ".doc")) + "\" "
               + "iWebOffice_EditType=\"" + (signReadOnly ? "0,0" : "1,1") + "\" "
               + "iWebOffice_BlockName=\"" + (blockName == null ? "" : blockName) + "\" "
               + "iWebOffice_WebUrl=\"" + (WebUrl == null ? "" : WebUrl) + "\" "
               + "iWebOffice_SignReadOnly=\"" + signReadOnly + "\" "
               + "iWebOffice_FndUser=\"" + mgr.getFndUser() + "\" "
               + "iWebOffice_ButtonName=\"返回_BEGIN\" "
               + "iWebOffice_ButtonMessage=\"是否保存？\" "
               + "iWebOffice_MenusName=\"" + menus + "\"></script>\n");
        
         
         appendToHTML("<table width=\"100%\" height=\"100%\"><tr><td>");
//         appendToHTML("<script src=\"/b2e/unsecured/common/scripts/iWebOffice2006.js\"></script>");
         appendToHTML("<script src=\"/b2e/unsecured/common/scripts/iWebOffice2009.js\"></script>");
         appendToHTML("</td></tr></table>");
         
         
         appendToHTML("<script src=\"/b2e/unsecured/common/scripts/iSignatureAPI.js\"></script>");
         appendToHTML("<object id=Mana classid=\"clsid:E5C44C76-610A-4C1F-9083-6CE933E3DC1D\" width=0 height=0 align=center hspace=0 vspace=0></object> \n" );
         printHiddenField("iSignature_Password", getUsbKeyPassword(usbkeySn));//TODO LQW
         
         appendDirtyJavaScript("userDefindOnload();");
         
      }
      
      ////////////auto signature begin
//      appendToHTML("<select name=Sign value=\"\" ></select>\n");//style=\"display:none\"
//      appendToHTML(" <input type=button onclick=\"autoSignIDATest();\" value=autoSignIDATest />\n");
//      appendToHTML(" <input type=button onclick=\"TestBatch();\" value=TestBatch />\n");
//      
//      appendDirtyJavaScript("//----------建立签章列表-------------------------\n");
//      appendDirtyJavaScript("function WebFlashSign(){\n");
//      appendDirtyJavaScript("   keysn = webform.Mana.WebGetKeySN();\n");
//      appendDirtyJavaScript("   webform.Sign.options.length=0;\n");
//      appendDirtyJavaScript("   webform.SignatureAPI.BeginLoadSignature();\n");
//      appendDirtyJavaScript("   var SignName = webform.SignatureAPI.SignatureNames;\n");
//      appendDirtyJavaScript("   var SName = SignName.split(\";\");\n");
//      appendDirtyJavaScript("   for(i=0; i<SName.length-1; i++){\n");
//      appendDirtyJavaScript("      var varItem = new Option(SName[i], SName[i]);\n");
//      appendDirtyJavaScript("      webform.Sign.options.add(varItem);\n");
//      appendDirtyJavaScript("   }\n");
//      appendDirtyJavaScript("   webform.SignatureAPI.EndLoadSignature();\n");
//      appendDirtyJavaScript("}\n");
//      appendDirtyJavaScript("\n");
//      appendDirtyJavaScript("\n");
//      appendDirtyJavaScript("\n");
//      appendDirtyJavaScript("//---------盖章之前设置活动文档对象-------\n");
//      appendDirtyJavaScript("function setActiveDocument(){\n");
//      appendDirtyJavaScript("   webform.SignatureAPI.ActiveDocument=webform.WebOffice.WebObject;//设置word对象\n");
//      appendDirtyJavaScript("}\n");
//      appendDirtyJavaScript("\n");
//      appendDirtyJavaScript("\n");
//      appendDirtyJavaScript("//--------API批量盖章--word---------------\n");
//      appendDirtyJavaScript("function TestBatch(){\n");
//      appendDirtyJavaScript("   webform = form;///------\n");
//      appendDirtyJavaScript("   var mRec = '1392364884672';//webform.RecordID.value;//获得要盖章的文档编号\n");
//      appendDirtyJavaScript("   //alert(mRec);\n");
//      appendDirtyJavaScript("   var arr2 = mRec.split(\",\");\n");
//      appendDirtyJavaScript("   WebFlashSign();\n");
//      appendDirtyJavaScript("   if(webform.Sign.selectedIndex != -1){\n");
//      appendDirtyJavaScript("      if(webform.Mana.VerifyPin(webform.Password.value)){\n");
//      appendDirtyJavaScript("         alert(\"密码不正确。\");\n");
//      appendDirtyJavaScript("         return;\n");
//      appendDirtyJavaScript("      }\n");
//      appendDirtyJavaScript("      if(keysn != webform.Mana.WebGetKeySN()){\n");
//      appendDirtyJavaScript("    	  alert(\"密钥盘已经更换，请重新选择签章\");\n");
//      appendDirtyJavaScript("    	  WebFlashSign();\n");
//      appendDirtyJavaScript("    	  return;\n");
//      appendDirtyJavaScript("      }\n");
//      appendDirtyJavaScript("      \n");
//      appendDirtyJavaScript("      keysn = webform.Mana.WebGetKeySN();//获得密钥盘信息\n");
//      appendDirtyJavaScript("      \n");
//      appendDirtyJavaScript("      webform.SignatureAPI.BeginLoadSignature();\n");
//      appendDirtyJavaScript("      var j = arr2.length;\n");
//      appendDirtyJavaScript("      for(j=0; j<arr2.length; j++){\n");
////      appendDirtyJavaScript("    	  webform.WebOffice.RecordID = arr2[j];\n");
////      appendDirtyJavaScript("    	  webform.WebOffice.fileName = arr2[j] + \".doc\";\n");
////      appendDirtyJavaScript("    	  webform.WebOffice.WebOpen();  //使用iWebOffice控件打开文档\n");
//      appendDirtyJavaScript("    	  setActiveDocument();\n");
//      appendDirtyJavaScript("    	  webform.WebOffice.WebObject.Application.Selection.GoTo(-1,0,0,\"POS_IDA_SIGANATURE\");\n");
//      appendDirtyJavaScript("    	  webform.SignatureAPI.CreateGroupSignature(!j, webform.Sign.selectedIndex, webform.Password.value);\n");
////      appendDirtyJavaScript("    	  SaveDocument();\n");
//      appendDirtyJavaScript("    	  webform.SignatureAPI.ReleaseActiveDocument();\n");
//      appendDirtyJavaScript("      }\n");
//      appendDirtyJavaScript("      webform.SignatureAPI.EndLoadSignature();\n");
//      appendDirtyJavaScript("   }else{\n");
//      appendDirtyJavaScript("	   alert(\"选章出错！\");\n");
//      appendDirtyJavaScript("   }\n");
//      appendDirtyJavaScript("   \n");
//      appendDirtyJavaScript("}\n");
//      
      ////////////auto signature end.
   }
}
