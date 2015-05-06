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
*  File        : DocReferenceObjectOvw.java
*  Modified    :
*    29-05-01  Dikalk  Added RMB methods View Document with Ext. App., Copy File To..., Edit Document, Check In Document, Undo Check Out Document
*    04-07-01  Shdilk  Call Id 66637 : Modified editDocument
*    31-07-01  Bakalk  Call Id 67229 : Added a dorp down combo box for 'Status'.
*    05-12-02  Dikalk  Removed obsolete methods doReset() and clone();
*    05-12-02  Dikalk  Added doc_sheet to preDefine() and all relevant methods;
*    05-12-02  Dikalk  Removed method getLoginUser() and other unused variables
*    06-01-03  Dikalk  Removed method getStateVals(). Constants are now retrieved from DocmanConstants
*    01-04-03  BaKalk  Added clone() and doReset() in order to support 3.5.1 web client.
*    02-04-03  Dikalk  Replaced calls to getOCXString() with DocmawUtil.getClientMgrObjectStr()
*    01-08-03  NiSilk  Fixed call 95769., modified methods adjust() and run()
*    11-09-03  ShThlk  Call Id 102913, Added a new translation constant 'DOCMAWDOCREFERENCEOBJECTOVWCANNOTEDIT'
*    10-12-03  BaKalk  Web Alignment(Multirow Actions) done.
*    24-12-03  BaKalk  Web Alignment(Field Order) done.
*    19-02-04  DIKALK  Call 112743. Check to see of rows are selected before performing multirow operations
*    23-02-04  DIKALK  Cleaned up some of the code. Also set the "UpperCase" property for some of
*                      fields in preDefine()
*    23-03-04  DIKALK  SP1-Merge. Bug Id 43378. Running macros when editing document that have already been checked out
*    31-10-05  AMNALK  Merged Bug Id 53112.
*    25-07-06  BAKALK  Bug ID 58216, Fixed Sql Injection.
*    13-10-06  NIJALK  Bug 61028, Increased the length of DOC_SHEET.
*    09-08-07  NaLrlk  XSS Correction.
*    15-11-07  AMNALK  Bug Id 67336, Added new function checkFileOperationEnable() and disabled the file operations on mixed or multiple structure documents.
*    10-04-07  SHTHLK  Bug Id 70286, Replaced addCustomCommand() with addSecureCustomCommand()
*    23-07-10  AMNALK  Bug Id 92069, Modified transferToDocInfo() to transfer all the keys to document info page.
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.docmaw.edm.*;


public class DocReferenceObjectOvw extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocReferenceObjectOvw");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPContext      ctx;
   private ASPBlock        headblk;
   private ASPRowSet       headset;
   private ASPCommandBar   headbar;
   private ASPTable        headtbl;
   private ASPBlockLayout  headlay;
   private ASPBlock        dummyblk;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private boolean bTranferToEDM;
   private ASPQuery q;
   private String searchURL;
   private ASPCommand cmd;
   private ASPBuffer data;
   private String calling_url;
   private ASPBuffer buff;

   private String iscalled;
   private ASPBuffer row;
   private String sFilePath;

   private String sLocalFileName;
   private String curRowNo;


   //===============================================================
   // Construction
   //===============================================================
   public DocReferenceObjectOvw(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      trans           = null;
      q               = null;
      searchURL       = null;
      cmd             = null;
      data            = null;
      calling_url     = null;
      buff            = null;
      iscalled        = null;
      row             = null;
      sFilePath       = null;
      sLocalFileName  = null;
      curRowNo        = null;
      bTranferToEDM   = false;

      super.doReset();
   }


   public ASPPoolElement clone(Object obj) throws FndException
   {
      DocReferenceObjectOvw page = (DocReferenceObjectOvw)(super.clone(obj));

      // Initializing mutable attributes
      page.trans           = null;
      page.q               = null;
      page.searchURL       = null;
      page.cmd             = null;
      page.data            = null;
      page.calling_url     = null;
      page.buff            = null;
      page.iscalled        = null;
      page.row             = null;
      page.sFilePath       = null;
      page.sLocalFileName  = null;
      page.curRowNo        = null;
      page.bTranferToEDM   = false;

      // Cloning immutable attributes
      page.ctx = page.getASPContext();

      page.headblk  = page.getASPBlock(headblk.getName());
      page.headset  = page.headblk.getASPRowSet();
      page.headtbl = page.getASPTable(headtbl.getName());
      page.headbar  = page.headblk.getASPCommandBar();
      page.headlay  = page.headblk.getASPBlockLayout();

      page.dummyblk = page.getASPBlock(dummyblk.getName());


      return page;
   }

   public void cancelFind()
   {
      ASPManager mgr = getASPManager();
      mgr.redirectTo("../Navigator.page?MAINMENU=Y&NEW=Y");
   }



   public void run()
   {
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      ctx = mgr.getASPContext();
      iscalled=ctx.readValue("ISCALLED","0");
      curRowNo = ctx.readValue("CURROWNO","0");

      bTranferToEDM = false;

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
      {
         validate();
      }

      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
         search();
      }

      else if (mgr.dataTransfered())
      {
         okFind();
      }

      else if ((!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))||(!mgr.isEmpty(mgr.getQueryStringValue("LU_NAME"))))
      {
         okFind();
      }

      else if ((!mgr.isEmpty(mgr.getQueryStringValue("KEY_REF"))) && (!mgr.isEmpty(mgr.getQueryStringValue("LOGICAL_UNIT_NAME"))))
      {
         keyRefLuNameTransferred();
      }

      else if ("TRUE".equals(mgr.readValue("REFRESH_ROW")))
      {
         refreshCurrentRow();
      }

      else
      {
         headlay.setLayoutMode(headlay.FIND_LAYOUT);
      }


      adjust();
      ctx.writeValue("ISCALLED",iscalled);
      ctx.writeValue("CURROWNO",curRowNo);
   }


   public void  validate()
   {
      ASPManager mgr = getASPManager();
      mgr.showError("VALIDATE not implemented");
      mgr.endResponse();
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      searchURL = mgr.createSearchURL(headblk);

      q = trans.addQuery(headblk);
      if (mgr.dataTransfered())
         q.addOrCondition( mgr.getTransferedData() );
      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);

      if (headset.countRows() == 0)
         mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWNODATA: No data found."));
   }


   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("HEAD","DOC_ISSUE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }


   public void  keyRefLuNameTransferred()
   {
      ASPManager mgr = getASPManager();

      searchURL = mgr.createSearchURL(headblk);

      trans.clear();
      q = trans.addEmptyQuery(headblk);
      //bug 58216 starts
      q.addWhereCondition("LU_NAME = ? AND KEY_REF = ?");
      q.addParameter("LU_NAME",mgr.readValue("LOGICAL_UNIT_NAME"));
      q.addParameter("KEY_REF",mgr.readValue("KEY_REF"));
      //bug 58216 end
      q.includeMeta("ALL");
      mgr.submit(trans);

      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWNODATAFOUND: No data found."));
         headset.clear();
      }
   }


   public void  search()
   {
      iscalled="1";
      okFind();
   }


   public void  transferToDocConnect()
   {
      int count;
      ASPManager mgr = getASPManager();

      if (iscalled == "1")
      {
         headset.selectRows();
         int currrow = headset.getCurrentRowNo();
         headset.setFilterOn();

         if (headset.countRows() == 0)
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWNODATASELECTED: No lines have been selected."));
            headset.setFilterOff();
            headset.unselectRows();
            headset.goTo(currrow);
         }
         else
         {
            calling_url = mgr.getURL();
            ctx.setGlobal("CALLING_URL",calling_url);
            int selectedrows = headset.countSelectedRows();
            headset.last();
            buff=mgr.newASPBuffer();
            for (count=1; count<=selectedrows; ++count)
            {
               row=buff.addBuffer(Integer.toString(count));
               row.addItem("LU_NAME",headset.getRow().getValue("LU_NAME"));
               row.addItem("KEY_REF",headset.getRow().getValue("KEY_REF"));
               headset.previous();
            }
            mgr.transferDataTo("DocReference.page",buff);
         }
         headset.setFilterOff();
      }
      else
      {
         if (headlay.isMultirowLayout())
            headset.storeSelections();
         else
            headset.selectRow();

         mgr.transferDataTo("DocReference.page",headset.getSelectedRows("LU_NAME,KEY_REF"));
      }
   }



   public void  transferToDocInfo()
   {
      ASPManager mgr = getASPManager();


      if (headlay.isSingleLayout())
      {
         headset.selectRow();
      }
      else
      {
         headset.storeSelections();
      }

      ASPBuffer keys = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV"); // Bug Id 92069

      if (keys.countItems()>0) {
         mgr.transferDataTo("DocIssue.page",keys);
      }
      else
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCTITLEOVWNORECSEL: No records selected!"));
      }


   }


   public void tranferToEdmMacro(String doc_type, String action)
   {
      ASPManager mgr = getASPManager();

      if (headlay.isSingleLayout())
      {
         headset.unselectRows();
         headset.selectRow();
      }
      else
         headset.storeSelections();

      ASPBuffer data = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      sFilePath = DocumentTransferHandler.getDataTransferUrl(mgr, "EdmMacro.page", action, doc_type, data);
      bTranferToEDM = true;
   }



   public void  viewCopy()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout() && headset.selectRows()==0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWNOROWS: No Rows Selected."));
         return;
      }
      //Bug Id 67336, start
      if (CheckFileOperationEnable()) 
      {
	      return;
      }
      //Bug Id 67336, end

      tranferToEdmMacro("VIEW","VIEW");
   }


   public void  viewOriginal()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout() && headset.selectRows()==0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWNOROWS: No Rows Selected."));
         return;
      }
      //Bug Id 67336, start
      if (CheckFileOperationEnable()) 
      {
	      return;
      }
      //Bug Id 67336, end

      tranferToEdmMacro("ORIGINAL","VIEW");
   }


   public void  printDocument()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout() && headset.selectRows()==0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWNOROWS: No Rows Selected."));
         return;
      }
      //Bug Id 67336, start
      if (CheckFileOperationEnable()) 
      {
	      return;
      }
      //Bug Id 67336, end

      tranferToEdmMacro("ORIGINAL","PRINT");
   }


   public void getCopyOfFileToDir()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout() && headset.selectRows()==0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWNOROWS: No Rows Selected."));
         return;
      }
      //Bug Id 67336, start
      if (CheckFileOperationEnable()) 
      {
	      return;
      }
      //Bug Id 67336, end

      tranferToEdmMacro("ORIGINAL","GETCOPYTODIR");
   }



   public void  viewOriginalWithExternalViewer()
   {
       //Bug Id 67336, start
       if (CheckFileOperationEnable()) 
       {
	       return;
       }
       //Bug Id 67336, end

      tranferToEdmMacro("ORIGINAL","VIEWWITHEXTVIEWER");
   }


   public void editDocument() throws FndException
   {
      ASPManager mgr = getASPManager();
      String status;
      String checkOutUser;

      if (headlay.isMultirowLayout() && headset.selectRows()==0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWNOROWS: No Rows Selected."));
         return;
      }

      //Bug Id 67336, start
      if (CheckFileOperationEnable()) 
      {
	      return;
      }
      //Bug Id 67336, end

      if (isEditable())
      {
         trans.clear();
         cmd = trans.addCustomCommand("EDMDOCSTATUS", "EDM_FILE_API.Get_Document_Status");
         cmd.addParameter("DUMMY1");
         cmd.addParameter("DUMMY2");
         cmd.addParameter("DUMMY3");
         cmd.addParameter("DUMMY4");
         cmd.addParameter("DUMMY5");
         cmd.addParameter("DUMMY6");
         cmd.addParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));
         cmd.addParameter("DOC_NO",headset.getRow().getValue("DOC_NO"));
         cmd.addParameter("DOC_SHEET",headset.getRow().getValue("DOC_SHEET"));
         cmd.addParameter("DOC_REV",headset.getRow().getValue("DOC_REV"));
         cmd.addParameter("DOC_TYPE","ORIGINAL");
         trans = mgr.perform(trans);

         status  = trans.getValue("EDMDOCSTATUS/DATA/DUMMY1");
         checkOutUser = trans.getValue("EDMDOCSTATUS/DATA/DUMMY2");

         trans.clear();

         DocmawConstants dm_const = DocmawConstants.getConstantHolder(mgr);

         if ((status.equals(dm_const.edm_file_check_out)) && (!(checkOutUser.equals(mgr.getUserId()))))
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWCHECKEDOUTMSG: The document is checked out by another user."));
            return;
         }
         else
            tranferToEdmMacro("ORIGINAL","CHECKOUT");
      }
      else
         mgr.showAlert("DOCMAWDOCREFERENCEOBJECTOVWCANNOTEDIT: You must have edit access to be able to edit this document.");

   }


   public void  checkInDocument()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout() && headset.selectRows()==0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWNOROWS: No Rows Selected."));
         return;
      }
      //Bug Id 67336, start
      if (CheckFileOperationEnable()) 
      {
	      return;
      }
      //Bug Id 67336, end

      tranferToEdmMacro("ORIGINAL","CHECKIN");
   }


   public void  undoCheckOut()
   {
      tranferToEdmMacro("ORIGINAL","UNDOCHECKOUT");
   }

   //Bug Id 67336, start
   private boolean CheckFileOperationEnable()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
      {
	  headset.storeSelections();
	  headset.setFilterOn();
	  String temp = " ";
	  String structure;
	  if (headset.countSelectedRows() > 1)
	  {
	      for (int k = 0;k < headset.countSelectedRows();k++)
	      {
		   structure = headset.getValue("STRUCTURE");
		   if (" ".equals(temp)) 
		   {
		       temp = structure;
		   }
		   if (!temp.equals(structure)) 
		   {
		       mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWNOFILEOPERATIONONMIXED: File Operations are not allowed on Mixed or multiple structure documents."));
		       headset.setFilterOff();
		       return true;
		   }
		   if ("1".equals(temp) && "1".equals(structure)) 
		   {
		       mgr.showAlert(mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWFILEOPERATIONONMIXED: File Operations are not allowed on Mixed or multiple structure documents."));
		       headset.setFilterOff();
		       return true;
		    }
		   temp = structure;
		   headset.next();
	      }
	   }
	  headset.setFilterOff();
       }
       return false;
   }
   //Bug Id 67336, end

   public boolean  isEditable()
   {
      ASPManager mgr = getASPManager();

      int curr_row;
      if (headlay.isMultirowLayout())
         curr_row = headset.getRowSelected();
      else
         curr_row = headset.getCurrentRowNo();

      headset.goTo(curr_row);

      trans.clear();
      cmd = trans.addCustomFunction("GETEDITACC","DOC_ISSUE_API.Get_Edit_Access_","DUMMY1");
      cmd.addParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));
      cmd.addParameter("DOC_NO",headset.getRow().getValue("DOC_NO"));
      cmd.addParameter("DOC_SHEET",headset.getRow().getValue("DOC_SHEET"));
      cmd.addParameter("DOC_REV",headset.getRow().getValue("DOC_REV"));
      trans = mgr.perform(trans);
      String accessToEdit = trans.getValue("GETEDITACC/DATA/DUMMY1");
      trans.clear();

      if ("TRUE".equals(accessToEdit))
         return true;
      else
         return false;
   }


   public void  refreshCurrentRow()
   {
      if (headlay.isMultirowLayout())
         headset.goTo(Integer.parseInt(curRowNo));
      else
         headset.selectRow();

      //  change the edm status of the file after edit,checkin etc operations
      headset.refreshRow();
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");

      // MDAHSE, 2001-01-17, we do not want to connect
      // a documnent to the connection, right?
      // So we disable the Documents action
      // and user our menu choice instead

      headblk.disableDocMan();

      headblk.addField("OBJID").
      setHidden();

      headblk.addField("OBJVERSION").
              setHidden();

      headblk.addField("SLUDESC").
              setSize(20).
              setMaxLength(2000).
              setLOV("ObjectConnectionLov.page").
              setFunction("OBJECT_CONNECTION_SYS.GET_LOGICAL_UNIT_DESCRIPTION(:LU_NAME)").
              setLabel("DOCMAWDOCREFERENCEOBJECTOVWSLUDESC: Object");

      headblk.addField("SINSTANCEDESC").
              setSize(20).
              setMaxLength(2000).
              setFunction("OBJECT_CONNECTION_SYS.GET_INSTANCE_DESCRIPTION(:LU_NAME,'NULL',:KEY_REF)").
              setLabel("DOCMAWDOCREFERENCEOBJECTOVWSINSTANCEDESC: Object Key");

      headblk.addField("DOC_OBJECT_DESC").
              setSize(20).
              setMaxLength(2000).
              setDefaultNotVisible().
              setLabel("DOCMAWDOCREFERENCEOBJECTOVWDOCOBJECTDESC: Object Desc");

      headblk.addField("DOC_CLASS").
              setSize(20).
              setMaxLength(12).
              setUpperCase().
              setDynamicLOV("DOC_CLASS").
              setLOVProperty("TITLE",mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWDOCCLASS1: List of Document Class")).
              setLabel("DOCMAWDOCREFERENCEOBJECTOVWDOCCLASS: Doc Class");

      headblk.addField("SCLASSNAME").
              setSize(20).
              setMaxLength(2000).
              setDefaultNotVisible().
              setFunction("DOC_CLASS_API.Get_Name(:DOC_CLASS)").
              setLabel("DOCMAWDOCREFERENCEOBJECTOVWSCLASSNAME: Doc Class Desc");
              mgr.getASPField("DOC_CLASS").setValidation("SCLASSNAME");

      headblk.addField("DOC_NO").
              setSize(20).
              setMaxLength(120).
              setUpperCase().
              setDynamicLOV("DOC_TITLE","DOC_CLASS,DOC_NO").
              setLOVProperty("TITLE",mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWDOCNOLIST: List of Document No")).
              setLabel("DOCMAWDOCREFERENCEOBJECTOVWDOCNO: Doc No");

      headblk.addField("DOC_SHEET").
              setSize(20).
              //Bug 61028, Start, Increased the length
              setMaxLength(10).
              //Bug 61028, End
              setUpperCase().
              setDynamicLOV("DOC_ISSUE_LOV1").
              setLOVProperty("TITLE",mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWSHEETNO: List of Doc Sheets")).
              setLabel("SHEETNO: Doc Sheet");

      headblk.addField("DOC_REV").
              setSize(20).
              setMaxLength(6).
              setUpperCase().
              setDynamicLOV("DOC_ISSUE_LOV").
              setLOVProperty("TITLE",mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWDOCREVLIST: List of Revision")).
              setLabel("DOCMAWDOCREFERENCEOBJECTOVWDOCREV: Revision");

      headblk.addField("STITLE").
              setSize(20).
              setMaxLength(2000).
              setDefaultNotVisible().
              setFunction("DOC_TITLE_API.GET_TITLE(:DOC_CLASS,:DOC_NO)").
              setLabel("DOCMAWDOCREFERENCEOBJECTOVWSTITLE: Title");
              mgr.getASPField("DOC_NO").setValidation("STITLE");

      headblk.addField("STATE").
              setSelectBox().
              enumerateValues("Doc_State_API").
              unsetSearchOnDbColumn().
              setFunction("DOC_ISSUE_API.Get_State(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV)").
              setLabel("DOCMAWDOCREFERENCEOBJECTOVWSSTATUS: Status");

      headblk.addField("CATEGORY").
              setSize(20).
              setMaxLength(5).
              setDefaultNotVisible().
              setDynamicLOV("DOC_REFERENCE_CATEGORY").
              setLOVProperty("TITLE",mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWCATEGORYLIST: List of Association Category")).
              setLabel("DOCMAWDOCREFERENCEOBJECTOVWCATEGORY: Association Category");

      headblk.addField("KEEP_LAST_DOC_REV").
              setSize(20).
              setMaxLength(200).
              setSelectBox().
              enumerateValues("Always_Last_Doc_Rev_API").
              unsetSearchOnDbColumn().
              setLabel("DOCMAWDOCREFERENCEOBJECTOVWKEEPLASTDOCREV: Update Revision");

      headblk.addField("COPY_FLAG").
              setSize(20).
              setMaxLength(200).
              setSelectBox().
              enumerateValues("Doc_Reference_Copy_Status_API").
              setLabel("DOCMAWDOCREFERENCEOBJECTOVWCOPYFLAG: Copy Status");

      headblk.addField("SKEEPCODE").
              setHidden().
              setFunction("ALWAYS_LAST_DOC_REV_API.ENCODE(:KEEP_LAST_DOC_REV)");
              mgr.getASPField("KEEP_LAST_DOC_REV").setValidation("SKEEPCODE");

      headblk.addField("NNOOFDOCUMENTS").
              setSize(20).
              setMaxLength(4).
              setDefaultNotVisible().
              setAlignment("RIGHT").
              setFunction("DOC_REFERENCE_OBJECT_API.GET_NUMBER_OF_REFERENCES(:LU_NAME,:KEY_REF)").
              setLabel("DOCMAWDOCREFERENCEOBJECTOVWNNOOFDOCUMENTS: No of Docs Connected to Object");

      headblk.addField("SURVEY_LOCKED_FLAG").
              setSize(20).
              setMaxLength(15).
              setSelectBox().
              enumerateValues("Lock_Document_Survey_API").
              setLabel("DOCMAWDOCREFERENCEOBJECTOVWSURVEYLOCKEDFLAG: Doc Connection Status");

      headblk.addField("LU_NAME").
              setHidden();

      headblk.addField("KEY_REF").
              setHidden();

      headblk.addField("DOC_TYPE").
              setFunction("'ORIGINAL'").
              setHidden();

      headblk.addField("FILE_TYPE").
              setHidden().
              setFunction("EDM_FILE_API.GET_FILE_TYPE(:DOC_CLASS,:DOC_NO,:DOC_SHEET,:DOC_REV,'ORIGINAL')");

      //Bug Id 67336, start
      headblk.addField("STRUCTURE").
	      setHidden().
	      setFunction("DOC_TITLE_API.Get_Structure_(DOC_CLASS,DOC_NO)");
      //Bug Id 67336, end

      headblk.setView("DOC_REFERENCE_OBJECT");
      headblk.defineCommand("DOC_REFERENCE_OBJECT_API","New__,Modify__,Remove__");

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.DUPLICATEROW);
      headbar.disableCommand(headbar.EDITROW);
      headbar.defineCommand(headbar.CANCELFIND,"cancelFind");

      // File Operations
      headbar.addSecureCustomCommand("viewOriginal",mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWVIEVOR: View Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
      headbar.addSecureCustomCommand("viewCopy",mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWVIEWCO: View Copy"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
      headbar.addSecureCustomCommand("viewOriginalWithExternalViewer",mgr.translate("DOCMAWDOCREFERENCEVIEWOREXTVIEWER: View Document with Ext. App"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
      headbar.addSecureCustomCommand("printDocument",mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWPRINTDOC: Print Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
      headbar.addSecureCustomCommand("getCopyOfFileToDir",mgr.translate("DOCMAWDOCISSUECOPYFILETO: Copy File To..."),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
      headbar.addSecureCustomCommand("editDocument",mgr.translate("DOCMAWDOCREFERENCEEDITDOC: Edit Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
      headbar.addSecureCustomCommand("checkInDocument",mgr.translate("DOCMAWDOCREFERENCECHECKINDOC: Check In Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
      headbar.addSecureCustomCommand("undoCheckOut",mgr.translate("DOCMAWDOCREFERENCEUNDOCHECKOUT: Undo Check Out Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286

      headbar.addCustomCommand("transferToDocInfo",mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWDOC: Document Info..."));
      headbar.addCustomCommand("transferToDocConnect",mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWDOB: Connected Documents..."));
      headbar.enableMultirowAction();

      // not supportive for multirow actions
      headbar.removeFromMultirowAction("viewOriginalWithExternalViewer");
      headbar.removeFromMultirowAction("undoCheckOut");

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("DOCMAWDOCREFERENCEOBJECTOVWOVEROBJCONN: Object Connections"));
      headtbl.enableRowSelect();

      headlay = headblk.getASPBlockLayout();
      headlay.setDialogColumns(2);
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

      dummyblk = mgr.newASPBlock("DUMMY");

      dummyblk.addField("DUMMY1");
      dummyblk.addField("DUMMY2");
      dummyblk.addField("DUMMY3");
      dummyblk.addField("DUMMY4");
      dummyblk.addField("DUMMY5");
      dummyblk.addField("DUMMY6");
      dummyblk.addField("LOGUSER");

   }


   public void  adjust()
   {
      if (headset.countRows() == 0)
      {
         headlay.setLayoutMode(headlay.FIND_LAYOUT);
      }
   }


   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      return "DOCMAWDOCREFERENCEOBJECTOVWTITLE: Overview - Object Connections";
   }


   protected String getTitle()
   {
      return "DOCMAWDOCREFERENCEOBJECTOVWTITLE: Overview - Object Connections";
   }


   protected AutoString getContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();
      out.clear();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("DOCMAWDOCREFERENCEOBJECTOVWTITLE: Overview - Object Connections"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append(mgr.startPresentation("DOCMAWDOCREFERENCEOBJECTOVWTITLE: Overview - Object Connections"));

      out.append("<input type=\"hidden\" name=\"REFRESH_ROW\" value=\"FALSE\">\n");
      out.append(headlay.show());

      //
      // CLIENT FUNCTIONS
      //

      appendDirtyJavaScript("var strCategoryIniVal = \"\";\n");
      appendDirtyJavaScript("var strFlag = \"\"\n");

      if (bTranferToEDM)
      {
         appendDirtyJavaScript("   window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(sFilePath));
         appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width=500,height=500,left=100,top=100\");\n");
      }


      appendDirtyJavaScript("function lovCategory(i)\n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript(" strCategoryIniVal = getField_('CATEGORY',i).value;\n");
      appendDirtyJavaScript("   strFlag=\"TRUE\"; \n");
      appendDirtyJavaScript(" openLOVWindow('CATEGORY',i,'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DOC_REFERENCE_CATEGORY&__FIELD=%23%26%21%24%23Association+Category&__TITLE=%23%26%21%24%23Association+Category'\n");
      appendDirtyJavaScript("    ,500,500,'validateCategory');\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function validateCategory(i)\n");
      appendDirtyJavaScript("{   \n");
      appendDirtyJavaScript(" setDirty(); \n");
      appendDirtyJavaScript(" if( !checkCategory(i) ) return;\n");
      appendDirtyJavaScript(" if(strFlag==\"TRUE\")\n");
      appendDirtyJavaScript("   {       \n");
      appendDirtyJavaScript("      strFlag = \"FALSE\";\n");
      appendDirtyJavaScript("      if(strCategoryIniVal.length < 5)\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("          strNewValue = strCategoryIniVal + getField_('CATEGORY',i).value;\n");
      appendDirtyJavaScript("          getField_('CATEGORY',i).value = strNewValue; \n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("      else if(strCategoryIniVal.length==5)\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("          strCategoryIniVal = strCategoryIniVal.substr(0,4);\n");
      appendDirtyJavaScript("          strNewValue = strCategoryIniVal + getField_('CATEGORY',i).value;                    \n");
      appendDirtyJavaScript("          getField_('CATEGORY',i).value = strNewValue;    \n");
      appendDirtyJavaScript("      }    \n");
      appendDirtyJavaScript("   }        \n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function refreshParent()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" document.form.REFRESH_ROW.value=\"TRUE\"\n");
      appendDirtyJavaScript(" submit() \n");
      appendDirtyJavaScript("}\n");

      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }

}
