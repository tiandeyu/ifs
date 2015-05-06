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
*  File        : DocDistributionListOvw.java 
*  Modified    :
*  ASP2JAVA Tool  2001-03-18  - Created Using the ASP file DocDistributionListOvw.asp
*
*  16/05/2001  : Shdilk - Call Id 64971 added new RMB, View Document with External Viewer
*  28/05/2001  : Prsalk - Call Id 65237, changed .asp to .page
*  29/05/2001  : Dikalk - Added RMB methods View Document with Ext. App., Copy File To..., 
*  03/02/2003  : Prsalk - Added Sheet Number (doc_sheet)
*  27/08/2003  : InoSlk - Call ID 100731: Modified doReset() and clone().
*  16/09/2003  : Thwilk - Call ID 103379 : Modified field lengths in predefine().
*  16/12/2003  : Bakalk - Web Alignment done.
*  22/12/2003  : Bakalk - Did some modifictations regarding multirow action.
*  02/01/2004  : Bakalk - Web alignment (field Order) done.
*  28/04/2005  : Karalk - Call 123515 Query No field removed.
*  31/05/20006 : Dikalk - Fixed Bug 57779, Removed method startup(), removed context parameter APPOWNER,
*                         removed use of appowner and replaced it with the new Docman Administrator.
*                         Also removed the clone() and doReset() methods.
*  13/10/2006  : Nijalk - Bug 61028, Increased the length of field DOC_SHEET.
*  12/06/2007  : Bakalk - Call Id: 144929, Modified confirmDistribution() and preDefine(), added new methods to remove records from rowset.
*  20070807    : ILSOLK   Eliminated SQLInjection.
*  15/08/2007  : ASSALK - Merged Bug 65659, Added search_url.
*  25/09/2007  : DINHLK - Changed the title.
*  15/04/2008  : SHTHLK - Bug Id 70286, Replaced addCustomCommand() with addSecureCustomCommand()
*  31/07/2008  : NIJALK - Bug Id 75803, Modified preDefine().
*  12/07/2010  : AMNALK - Bug Id 91182, Modified getContents() to display the term usage on the check box label DOCMAWDOCDISTRIBUTIONLISTOVWIGNORE.
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DocDistributionListOvw extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocDistributionListOvw");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPHTMLFormatter fmt;
   private ASPContext ctx;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   private ASPBlock dummyblk;
   private ASPField f;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPCommand cmd;
   private ASPQuery q;

   private boolean exedist;
   private boolean duprecord;
   private boolean newrecord;
   private boolean bTranferToEDM;
   private boolean allow;

   private String sSuspended;
   private String sPending;
   private String sSqlSelect;
   private String doc_class;
   private String doc_no;
   private String doc_sheet;
   private String doc_rev;
   private String txt;
   private String sFilePath;

   private int currow;
   private int i;
   private String   search_url;

   //===============================================================
   // Construction 
   //===============================================================
   public DocDistributionListOvw(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      fmt = mgr.newASPHTMLFormatter();
      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer(); 

      sSuspended = ctx.readValue("SSUSPENDED", "");
      sPending = ctx.readValue("SPENDING", "");

      exedist=false;
      duprecord = false;
      newrecord = false;
      bTranferToEDM = false;
      allow=false; 


      if ((mgr.isEmpty(sSuspended))||(mgr.isEmpty(sPending)))
         getStateVals();

      if (mgr.commandBarActivated())
      {
         if ("HEAD.DuplicateRow".equals(mgr.readValue("__COMMAND")))
            duprecord=true;

         if ("HEAD.NewRow".equals(mgr.readValue("__COMMAND")) ||   "HEAD.SaveNew".equals(mgr.readValue("__COMMAND")))
            newrecord=true;
         eval(mgr.commandBarFunction());
      }
      else if (mgr.dataTransfered())
         okFind();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else if (mgr.buttonPressed("OK"))
         confirmDistribution();
      else if (mgr.buttonPressed("CANCEL"))
         backToForm();

      adjust();

      // Use to keep client values of each State.
      ctx.writeValue("SSUSPENDED", sSuspended);
      ctx.writeValue("SPENDING", sPending);  
   }

   // Bug 57779, Start
   private boolean isUserDocmanAdministrator()
   {
      ASPManager mgr = getASPManager();
      ASPCommand cmd = trans.addCustomFunction("DOCMAN_ADMIN", "Docman_Security_Util_API.Check_Docman_Administrator", "OUT_STR1");
      trans = mgr.perform(trans);
      return "TRUE".equals(trans.getValue("DOCMAN_ADMIN/DATA/OUT_STR1"));
   }
   // Bug 57779, End


   public void perform( String command) 
   {
      ASPManager mgr = getASPManager();

      headset.storeSelections();
      headset.markSelectedRows(command);
      mgr.submit(trans);
   }

   //-----------------------------------------------------------------------------
   //-------------------------  VALIDATE FUNCTIONS  ---------------------------
   //-----------------------------------------------------------------------------

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      String val = mgr.readValue("VALIDATE");

      if ("DOC_NO".equals(val))
      {
         cmd = trans.addCustomFunction( "GETTITLE", "DOC_TITLE_API.Get_Title", "DOC_TITLE" );
         cmd.addParameter("DOC_NO",mgr.readValue("DOC_NO"));
         cmd.addParameter("DOC_CLASS",mgr.readValue("DOC_CLASS"));       

         trans = mgr.validate(trans);

         String title = trans.getValue("GETTITLE/DATA/DOC_TITLE");

         txt = (mgr.isEmpty(title) ? "" : title + "^") ; 

         mgr.responseWrite( txt );           
      }
      mgr.endResponse(); 
   }


   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("HEAD","DOC_DIST_LIST_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      ASPBuffer data = trans.getBuffer("HEAD/DATA");
      if (duprecord)
      {
         data.setFieldItem("DOC_CLASS_DES",headset.getRow().getValue("DOC_CLASS_DES"));
         data.setFieldItem("DOC_TITLE",headset.getRow().getValue("DOC_TITLE"));
         data.setFieldItem("RECEIVER_NAME",headset.getRow().getValue("RECEIVER_NAME"));
         data.setFieldItem("SENDER_PERSON_NAME",headset.getRow().getValue("SENDER_PERSON_NAME"));      
      }
      headset.addRow(data);

      trans.clear();
      cmd = trans.addCustomFunction( "SENDERNAME", "DOC_DIST_PERSON_API.Get_Name", "SENDER_PERSON_NAME" );
      cmd.addParameter("SENDER_PERSON",headset.getRow().getValue("SENDER_PERSON"));       

      trans = mgr.validate(trans);

      String sendername = trans.getValue("SENDERNAME/DATA/SENDER_PERSON_NAME");

      ASPBuffer buff = headset.getRow();
      buff.setValue("SENDER_PERSON_NAME",sendername);     
      headset.setRow(buff);     
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
      search_url = mgr.createSearchURL(headblk);

      q = trans.addQuery(headblk);   
      q.includeMeta("ALL");   
      mgr.querySubmit(trans,headblk);

      if (headset.countRows() == 0)
      {
         mgr.showAlert("DOCMAWDOCDISTRIBUTIONLISTOVWNODATA: No data found.");
         headset.clear();
      }
   }

   //-----------------------------------------------------------------------------
   //------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
   //-----------------------------------------------------------------------------
   // Use to get states which are shown in the client.

   public void  getStateVals()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      txt = "select DOC_DIST_LIST_API.Finite_State_Decode__('Suspended') SSUSPEND,";   
      txt += "DOC_DIST_LIST_API.Finite_State_Decode__('Pending') SPEND from DUAL";

      q = trans.addQuery("GETCLIENTSTATE",txt);
      trans = mgr.perform(trans);

      sSuspended  = trans.getValue("GETCLIENTSTATE/DATA/SSUSPEND");     
      sPending = trans.getValue("GETCLIENTSTATE/DATA/SPEND");

      trans.clear();
   }


   public void  activate()
   {
      ASPManager mgr = getASPManager();
      int noOfselectedRecords = 1;

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections(); 
         headset.setFilterOn();
         noOfselectedRecords = headset.countSelectedRows();
         
      }
      else
      {
         headset.selectRow();
      }
         
      

      int count = 0;
      
      
      boolean bNotAllSuspended = false;

      for (count=0;count < noOfselectedRecords;count++)
      {
         if (!sSuspended.equals(headset.getRow().getValue("STATE")))
        {
            bNotAllSuspended = true;
            break;
        }
        if (headlay.isMultirowLayout()) {
           headset.next();
        }
      }

      if (headlay.isMultirowLayout()) {
         headset.first();
      }
      

      if (!bNotAllSuspended)// all suspended : bakalk
      {
         headset.markSelectedRows("ACTIVATE__");  
         if (headlay.isSingleLayout()) {
            currow=headset.getCurrentRowNo();
         }
         
         mgr.submit(trans);
         if (headlay.isSingleLayout()) {
            headset.goTo(currow);
         }
         trans.clear();

         if (headlay.isMultirowLayout()) {
            headset.setFilterOff();
         }
         

      }
      else
      {
         if (noOfselectedRecords > 1)
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCDISTRIBUTIONLISTOVWNOTACTWITHNO: This operation is valid only for Suspended records.Record no &1 in selected rows has not been suspended",(++count+"")));
         }
         else
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCDISTRIBUTIONLISTOVWNOTACT: This operation is valid only for Suspended records."));
         }
      }
         

      
      headset.setFilterOff(); 
   }


   public void  suspend()
   {
      ASPManager mgr = getASPManager();
      int noOfSelectedRows=1;

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections(); 
         headset.setFilterOn();
         noOfSelectedRows=headset.countRows();
      }
      else
      {
         headset.selectRow(); 
      }
      
      
      boolean bAllPending = true;
      
      int count = 0;

      for (count=0;count<noOfSelectedRows;count++)
      {
         if (!sPending.equals(headset.getRow().getValue("STATE")))
         {
            bAllPending = false;
            break;
         }
         if (headlay.isMultirowLayout()) {
            headset.next();
         }
      }

      if (bAllPending)
      {
         headset.markSelectedRows("SUSPEND__");  
         if (headlay.isSingleLayout()) {
            currow=headset.getCurrentRowNo();
         }
         
         mgr.submit(trans);
         if (headlay.isSingleLayout()) {
            headset.goTo(currow);
         }
         
         trans.clear();
         headset.unselectRows();// if we show an error msg we have to show selected rows as well. : bakalk
      }
      else
      {
         if (noOfSelectedRows>1)
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCDISTRIBUTIONLISTOVWNOTSUS: This operation is valid only for Pending records. Record no &1 in selected rows is not in Pending state.",(++count+"")));
         }
         else
         {
            mgr.showAlert(mgr.translate("DOCMAWDOCDISTRIBUTIONLISTOVWNOTSUS2: This operation is valid only for Pending records."));
         }
      }
         

      if (headlay.isMultirowLayout()) {
      }
      headset.setFilterOff(); 
   }


   public void  executeDistribution()
   {

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections(); 
      }
      else
      {
         headset.selectRow(); 
      }
         

      exedist=true;
   }


   public void  confirmDistribution()
   {
      ASPManager mgr = getASPManager();
      int noOfRowsSelected = 1;

      String ignore=mgr.readValue("CHECKED_VALUE");

      if ("true".equals(ignore))
         ignore="1";
      else
         ignore="0";

      if (headlay.isMultirowLayout()) {
          headset.setFilterOn();
          noOfRowsSelected = headset.countRows();
      }
     
      trans.clear();
      for (int k=0;k< noOfRowsSelected;k++)
      {
         cmd = trans.addCustomCommand("EXEDIST"+k,"DOC_DIST_ENGINE_API.Execute_Distribution");              
         cmd.addParameter("RCODE");
         cmd.addParameter("DISTRIBUTION_NO",headset.getRow().getValue("DISTRIBUTION_NO"));
         cmd.addParameter("LINE_NO",headset.getRow().getValue("LINE_NO"));
         cmd.addParameter("IGNORE_DISTRIBUTED",ignore); 

         

         if (headlay.isMultirowLayout()) {
             headset.next();
         }
        
      }
      int curr_row =0;

      if (headlay.isSingleLayout()) {
         curr_row = headset.getCurrentRowNo();
      }

      
      //mgr.submit(trans);//w.a. let us try change the row definition too.
      mgr.perform(trans);

      

      

      
      if (headlay.isMultirowLayout()) {
         headset.setFilterOff();
         //headset.unselectRows();
      }
      else{
         headset.goTo(curr_row);
      }

       deleteDistFromRecordSet();  // ONLY WHEN DOC_DIST_TYPE_DB = 2
      

      //okFind();   //a big mistake :)
     
       
   }

   public void deleteDistFromRecordSet()
   {
     //remove the row from the buffer
     ASPBuffer buf = headset.getRows();
     if (headlay.isMultirowLayout())
	  {
		  // headset.storeSelections();
         headset.first();
         int deletedRows = 0;
         for (int k=0;k<headset.countRows();k++) {
            if (headset.isRowSelected()) { //isRowSelected
               if ("2".equals(headset.getRow().getValue("DOC_DIST_TYPE_DB"))) {
                  buf.removeItemAt(k-deletedRows);
                  deletedRows++;
               }
            }
            headset.next();
         }
	  }
	  else
     {
        headset.selectRow();
        int current_row = headset.getCurrentRowNo();
        if ("2".equals(headset.getRow().getValue("DOC_DIST_TYPE_DB"))) {
           buf.removeItemAt(current_row);
        }
        
        
     }
     setBufferAsRows(headset, buf);
   }

   private void setBufferAsRows(ASPRowSet rowset, ASPBuffer rows)
   {
      rowset.clear();
      for (int x = 0; x < rows.countItems(); x++)
         rowset.addRow(rows.getBufferAt(x));
   }

   // File operations

   public void tranferToEdmMacro( String doc_type,String action) //TODO: Parameter types are not recognized and set them to String. Declare type[s] for (doc_type,action)
   {
      int temp;
      String fndUser,viewAccess;
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections(); 
         headset.setFilterOn();
      }
      else
         headset.selectRow(); 

      cmd = trans.addCustomFunction( "FILESTATE", "EDM_FILE_API.GET_DOCUMENT_STATE", "FILE_STATE" );
      cmd.addParameter("DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));
      cmd.addParameter("DOC_NO",headset.getRow().getValue("DOC_NO"));
      cmd.addParameter("DOC_SHEET",headset.getRow().getValue("DOC_SHEET"));
      cmd.addParameter("DOC_REV",headset.getRow().getValue("DOC_REV")); 
      cmd.addParameter("DOC_TYPE","ORIGINAL");   

      trans = mgr.validate(trans);

      String filestate = trans.getValue("FILESTATE/DATA/FILE_STATE");   

      fndUser = getASPInfoServices().getFndUser();

      trans.clear();
      sSqlSelect = "SELECT ACCESS_CONTROL,USER_CREATED,FILE_TYPE From DOC_ISSUE_REFERENCE WHERE DOC_CLASS= ? AND DOC_NO= ? AND DOC_SHEET= ? AND DOC_REV= ? ";      
      q = trans.addQuery("HEAD",sSqlSelect);
      q.addParameter("DOC_CLASS" ,headset.getRow().getValue("DOC_CLASS"));
      q.addParameter("DOC_NO" ,headset.getRow().getValue("DOC_NO"));
      q.addParameter("DOC_SHEET" ,headset.getRow().getValue("DOC_SHEET"));
      q.addParameter("DOC_REV" ,headset.getRow().getValue("DOC_REV"));
      q.includeMeta("ALL");

      trans = mgr.perform(trans);

      String access = trans.getValue("HEAD/DATA/ACCESS_CONTROL");
      String creator = trans.getValue("HEAD/DATA/USER_CREATED");
      String file_type = trans.getValue("HEAD/DATA/FILE_TYPE");    

      trans.clear(); 

      if ("Approval Group".equals(access))
      {
         sSqlSelect = "SELECT GROUP_ID,PERSON_ID,VIEW_ACCESS From DOCUMENT_ISSUE_ACCESS WHERE DOC_CLASS= ? AND DOC_NO= ?  AND DOC_SHEET= ? AND DOC_REV= ? ";              
         q = trans.addQuery("HEAD",sSqlSelect);
	 q.addParameter("DOC_CLASS" ,headset.getRow().getValue("DOC_CLASS"));
	 q.addParameter("DOC_NO" ,headset.getRow().getValue("DOC_NO"));
	 q.addParameter("DOC_SHEET" ,headset.getRow().getValue("DOC_SHEET"));
	 q.addParameter("DOC_REV" ,headset.getRow().getValue("DOC_REV"));
         q.includeMeta("ALL");

         trans = mgr.perform(trans);      
         trans.clear();          

         ASPBuffer buf = trans.getBuffer("HEAD");     

         for (i=0; i<buf.countItems(); i++)
         {
            if ("DATA".equals(buf.getNameAt(i)))
            {
               ASPBuffer tempBuf=buf.getBufferAt(i);
               String person=tempBuf.getValue("PERSON_ID");            
               viewAccess = tempBuf.getValue("VIEW_ACCESS");

               if (( fndUser.equals(person) ) &&  ( viewAccess.equals("1") ))
                  allow=true;
            }
         }      

         for (i=0; i<buf.countItems(); i++)
         {
            if ("DATA".equals(buf.getNameAt(i)))
            {
               ASPBuffer tempBuf1=buf.getBufferAt(i);            
               String group = tempBuf1.getValue("GROUP_ID");
               viewAccess = tempBuf1.getValue("VIEW_ACCESS");

               if (viewAccess.equals("1"))
               {
                  sSqlSelect = "SELECT PERSON_ID From DOCUMENT_GROUP_MEMBERS WHERE GROUP_ID= ? ";      
                  q = trans.addQuery("HEAD",sSqlSelect);
		  q.addParameter("GROUP_ID" ,group);
                  q.includeMeta("ALL");

                  trans = mgr.perform(trans);            
                  trans.clear();

                  ASPBuffer buf1=trans.getBuffer("HEAD");
                  for (i=0; i<buf1.countItems(); i++)
                  {
                     if ("DATA".equals(buf1.getNameAt(i)))
                     {
                        ASPBuffer tempBuf2=buf1.getBufferAt(i);            
                        String groupPerson=tempBuf2.getValue("PERSON_ID");
                        if (fndUser.equals(groupPerson))
                           allow=true;
                     }
                  }
               }
            }
         }     
      }

      // Bug 57779, Start
      boolean docman_admin = isUserDocmanAdministrator();
      // Bug 57779, End
      if (mgr.isEmpty(filestate))
      {
         mgr.showAlert("DOCMAWDOCDISTRIBUTIONLISTOVWCANTVIEW: Only checked in files can be viewed or printed.");
      }
      else if ((!docman_admin && !(creator.equals(fndUser)) && "User".equals(access)) || (!docman_admin && "Approval Group".equals(access) && !allow)) // Bug 57779
      {
         mgr.showAlert("DOCMAWDOCDISTRIBUTIONLISTOVWNOTDOCINFO: This operation cannot be completed for the selected record.");
      }
      else
      {
         // retrieve keys from table
         doc_class = headset.getValue("DOC_CLASS");
         doc_no = headset.getValue("DOC_NO");
         doc_sheet = headset.getValue("DOC_SHEET");
         doc_rev = headset.getValue("DOC_REV");      

         bTranferToEDM = true;
         sFilePath  = "EdmMacro.page?DOC_CLASS="+mgr.URLEncode(doc_class);
         sFilePath += "&DOC_NO="+mgr.URLEncode(doc_no);
         sFilePath += "&DOC_SHEET="+mgr.URLEncode(doc_sheet);
         sFilePath += "&DOC_REV="+mgr.URLEncode(doc_rev);
         sFilePath += "&DOC_TYPE="+mgr.URLEncode(doc_type);
         sFilePath += "&FILE_TYPE="+mgr.URLEncode(file_type);
         sFilePath += "&PROCESS_DB="+mgr.URLEncode(action);
      }

      headset.unselectRows();
      headset.setFilterOff();
   }


   public void  viewOriginal()
   {
      tranferToEdmMacro("ORIGINAL","VIEW");
   }

   public void  viewOriginalWithExternalViewer()
   {
      tranferToEdmMacro("ORIGINAL","VIEWWITHEXTVIEWER");
   }


   public void  printDocument()
   {
      tranferToEdmMacro("ORIGINAL","PRINT");
   }


   public void getCopyOfFileToDir()
   {
      tranferToEdmMacro("ORIGINAL","GETCOPYTODIR");
   }


   public void  transferToDocInfo()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections(); 
         
      }
      else
      {
         headset.selectRow();
      }
         
      ASPBuffer keys = headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");
      mgr.transferDataTo("DocIssue.page",keys);

   }


   public void  backToForm()
   {
      headset.setFilterOff();
      exedist=false;
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      getASPInfoServices().addFields();

      headblk = mgr.newASPBlock("HEAD");
      headbar=mgr.newASPCommandBar(headblk);

      f = headblk.addField("OBJID");
      f.setHidden();

      f = headblk.addField("OBJVERSION");
      f.setHidden();   

      f = headblk.addField("DISTRIBUTION_NO","Number");
      f.setSize(20);
      f.setLabel("DOCMAWDOCDISTRIBUTIONLISTOVWDISTNO: Distribution No");   
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();   
      

      f = headblk.addField("LINE_NO","Number");
      f.setSize(20);   
      f.setLabel("DOCMAWDOCDISTRIBUTIONLISTOVWLINENO: Line No");   
      f.setMaxLength(0);
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();   

      f = headblk.addField("DOC_CLASS");
      f.setSize(20);
      f.setMandatory();   
      f.setLabel("DOCMAWDOCDISTRIBUTIONLISTOVWDOCCLASS: Doc Class");
      f.setUpperCase();
      f.setMaxLength(12);
      f.setDynamicLOV("DOC_CLASS"); 
      f.setLOVProperty("TITLE","Document Class");

      f = headblk.addField("DOC_CLASS_DES");
      f.setSize(20);    
      f.setLabel("DOCMAWDOCDISTRIBUTIONLISTOVWDOCCLASSDES: Doc Class Description");   
      f.setMaxLength(2000);   
      f.setFunction("DOC_CLASS_API.Get_Name(:DOC_CLASS)");
      mgr.getASPField("DOC_CLASS").setValidation("DOC_CLASS_DES");
      f.setReadOnly();
      // w.a DISTRIBUTION_NO,LINE_NO,DOC_CLASS,DOC_CLASS_DES

      f = headblk.addField("DOC_NO");
      f.setSize(20);   
      f.setLabel("DOCMAWDOCDISTRIBUTIONLISTOVWDOCNO: Doc No");
      f.setUpperCase();
      f.setMaxLength(120);
      f.setMandatory(); 
      f.setDynamicLOV("DOC_TITLE","DOC_CLASS");
      f.setLOVProperty("TITLE","Document No");

      f = headblk.addField("DOC_TITLE");
      f.setSize(20);   
      f.setLabel("DOCMAWDOCDISTRIBUTIONLISTOVWDOCTITLE: Title");
      f.setReadOnly();
      f.setMaxLength(2000);
      f.setFunction("DOC_TITLE_API.Get_Title(:DOC_CLASS,:DOC_NO)");
      mgr.getASPField("DOC_NO").setValidation("DOC_TITLE");        

      f = headblk.addField("DOC_SHEET");
      f.setSize(20);   
      f.setLabel("DOCMAWDOCDISTRIBUTIONLISTOVWDOCSHEET: Doc Sheet");
      f.setUpperCase();
      //Bug 61028, Start, Increased the length
      f.setMaxLength(10); 
      //Bug 61028, End
      f.setMandatory();   
      f.setDynamicLOV("DOC_ISSUE_LOV1","DOC_CLASS,DOC_NO");
      f.setLOVProperty("TITLE",mgr.translate("DOCMAWDOCDISTRIBUTIONLISTOVWDOCSHEET: Doc Sheet"));

      f = headblk.addField("DOC_REV");
      f.setSize(20);   
      f.setLabel("DOCMAWDOCDISTRIBUTIONLISTOVWDOCREV: Revision");
      f.setUpperCase();
      f.setMaxLength(6); 
      f.setMandatory();   
      f.setDynamicLOV("DOC_ISSUE","DOC_CLASS,DOC_NO");
      f.setLOVProperty("TITLE","Revision");   

      f = headblk.addField("STATE");
      f.setSize(20);   
      f.setLabel("DOCMAWDOCDISTRIBUTIONLISTOVWSTATUS: Status");
      f.setReadOnly();
      f.setMaxLength(2000);

      f = headblk.addField("RECEIVER_PERSON");
      f.setSize(20);   
      f.setLabel("DOCMAWDOCDISTRIBUTIONLISTOVWRECEIVER: Receiver");
      f.setMandatory();
      f.setMaxLength(100); 
      f.setUpperCase();    
      f.setDynamicLOV("DOC_DIST_PERSON");
      f.setLOVProperty("TITLE","Receiver");

      f = headblk.addField("RECEIVER_NAME");
      f.setSize(20);   
      f.setLabel("DOCMAWDOCDISTRIBUTIONLISTOVWRECEIVERNAME: Name of Receiver");
      f.setReadOnly();
      f.setMaxLength(2000);
      f.setFunction("DOC_DIST_PERSON_API.Get_Name(:RECEIVER_PERSON)");
      mgr.getASPField("RECEIVER_PERSON").setValidation("RECEIVER_NAME");     

      f = headblk.addField("DATE_ORDERED","Date");
      f.setSize(20);   
      f.setLabel("DOCMAWDOCDISTRIBUTIONLISTOVWDATEORDERED: Date Ordered");   
      f.setMaxLength(0);
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable(); 

      f = headblk.addField("NUMBER_OF_COPIES","Number");
      f.setSize(20);   
      f.setLabel("DOCMAWDOCDISTRIBUTIONLISTOVWNOFCOPIES: Number of Copies");   
      f.setMaxLength(0);
      f.setMandatory();

      f = headblk.addField("DOC_DIST_TYPE");
      f.setSize(20);  
      //Bug 75083, Start
      f.setLabel("DOCMAWDOCDISTRIBUTIONLISTOVWTYPE: Document Distribution Type");   
      //Bug 75083, End
      f.setMaxLength(20);
      f.setMandatory(); 
      f.setSelectBox();  
      f.enumerateValues("DOC_DIST_TYPE_API");

      f = headblk.addField("DOC_DIST_TYPE_DB");
      f.setFunction("DOC_DIST_TYPE_API.Encode(:DOC_DIST_TYPE)");
      //Bug 75083, Start
      f.setHidden();
      //Bug 75083, End

      f = headblk.addField("DOC_DIST_METHOD");
      f.setSize(20);   
      f.setLabel("DOCMAWDOCDISTRIBUTIONLISTOVWDISTMETHOD: Distribution Method");   
      f.setMaxLength(20);
      f.setMandatory();
      f.setSelectBox();  
      f.enumerateValues("DOC_DIST_METHOD_API");   

      f = headblk.addField("DOC_DIST_TRIGGER_COND");
      f.setSize(20);   
      f.setLabel("DOCMAWDOCDISTRIBUTIONLISTOVWTRIGGER: Triggering Condition");   
      f.setMaxLength(20);
      f.setMandatory();
      f.setSelectBox();  
      f.enumerateValues("DOC_DIST_TRIGGER_COND_API");

      f = headblk.addField("DOC_DIST_PRIORITY");
      f.setSize(20);   
      f.setLabel("DOCMAWDOCDISTRIBUTIONLISTOVWPRIORITY: Priority");   
      f.setMaxLength(20);
      f.setMandatory();
      f.setSelectBox(); 
      f.enumerateValues("DOC_DIST_PRIORITY_API");   

      f = headblk.addField("SENDER_PERSON");
      f.setSize(20);   
      f.setLabel("DOCMAWDOCDISTRIBUTIONLISTOVWSENDER: Sender");   
      f.setMaxLength(100);   
      f.setDynamicLOV("DOC_DIST_PERSON");
      f.setLOVProperty("TITLE","Sender");
      f.setMandatory();

      f = headblk.addField("SENDER_PERSON_NAME");
      f.setSize(20);   
      f.setLabel("DOCMAWDOCDISTRIBUTIONLISTOVWSENDERNAME: Name");   
      f.setMaxLength(2000);   
      f.setReadOnly();
      f.setFunction("DOC_DIST_PERSON_API.Get_Name(:SENDER_PERSON)");
      mgr.getASPField("SENDER_PERSON").setValidation("SENDER_PERSON_NAME");

         

      f = headblk.addField("DISTRIBUTION_NOTE");
      f.setSize(20);   
      f.setLabel("DOCMAWDOCDISTRIBUTIONLISTOVWDISTRIBUTIONNOTE: Distribution note");
      f.setMaxLength(2000); 

      f = headblk.addField("DATE_VALID_FROM","Date");
      f.setSize(20);   
      f.setLabel("DOCMAWDOCDISTRIBUTIONLISTOVWDATEVALIDFROM: Valid from");   
      f.setMaxLength(0);  
      f.setMandatory(); 

      f = headblk.addField("DATE_VALID_TO","Date");
      f.setSize(20);   
      f.setLabel("DOCMAWDOCDISTRIBUTIONLISTOVWDATEVALIDTO: Valid To");   
      f.setMaxLength(0);  

      headtbl=mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("DOCMAWDOCDISTRIBUTIONLISTOVWDOCDIST: Overview Document Distribution"));
      headtbl.enableRowSelect();//w.a.

      headtbl.unsetEditable();    
      headlay = headblk.getASPBlockLayout();
      headlay.setDialogColumns(2);
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT); 
      
      headlay.setFieldOrder("DISTRIBUTION_NO,LINE_NO,DOC_CLASS,DOC_CLASS_DES,DOC_NO,DOC_SHEET,DOC_REV,DOC_TITLE,STATE,RECEIVER_PERSON,RECEIVER_NAME,DATE_ORDERED,NUMBER_OF_COPIES,DOC_DIST_TYPE,DOC_DIST_METHOD,DOC_DIST_TRIGGER_COND,DOC_DIST_PRIORITY,SENDER_PERSON,SENDER_PERSON_NAME,DISTRIBUTION_NOTE,DATE_VALID_FROM,DATE_VALID_TO");

      headbar.addSecureCustomCommand("activate",mgr.translate("DOCMAWDOCDISTRIBUTIONLISTOVWACTIVATE: Activate"),"DOC_DIST_LIST_API.ACTIVATE__"); //Bug Id 70286
      headbar.addSecureCustomCommand("suspend",mgr.translate("DOCMAWDOCDISTRIBUTIONLISTOVWSUSPEND: Suspend"),"DOC_DIST_LIST_API.SUSPEND__"); //Bug Id 70286
      headbar.addSecureCustomCommand("executeDistribution",mgr.translate("DOCMAWDOCDISTRIBUTIONLISTOVWEXECUTE: Send Distribution"),"DOC_DIST_ENGINE_API.Execute_Distribution");   //Bug Id 70286
      headbar.addSecureCustomCommand("viewOriginal",mgr.translate("DOCMAWDOCDISTRIBUTIONLISTOVWVIEVOR: View Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access");   //Bug Id 70286
      headbar.addSecureCustomCommand("viewOriginalWithExternalViewer",mgr.translate("DOCMAWDOCDISTRIBUTIONLISTOVWVIEVOREXTVIEWER: View Document with Ext. App."),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
      headbar.addSecureCustomCommand("printDocument",mgr.translate("DOCMAWDOCDISTRIBUTIONLISTOVWPRINTDOC: Print Document"),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
      headbar.addSecureCustomCommand("getCopyOfFileToDir",mgr.translate("DOCMAWDOCISSUECOPYFILETO: Copy File To..."),"DOCUMENT_ISSUE_ACCESS_API.Get_Document_Access"); //Bug Id 70286
      headbar.addCustomCommand("transferToDocInfo",mgr.translate("DOCMAWDOCDISTRIBUTIONLISTOVWDOCINFO: Document info..."));    

      //w.a.
      headbar.enableMultirowAction();
      //file operations here not supportive for multirow selections
      headbar.removeFromMultirowAction("viewOriginal");
      headbar.removeFromMultirowAction("viewOriginalWithExternalViewer");
      headbar.removeFromMultirowAction("printDocument");
      headbar.removeFromMultirowAction("getCopyOfFileToDir");
      //headbar.removeFromMultirowAction("viewOriginalWithExternalViewer");

      headblk.setView("DOC_DIST_LIST");
      headblk.defineCommand("DOC_DIST_LIST_API","New__,Modify__,Remove__,SUSPEND__,ACTIVATE__");

      headset = headblk.getASPRowSet();

      //---------------------------------------------------------------------

      dummyblk = mgr.newASPBlock("DUMMY");

      f = dummyblk.addField("RCODE");
      f = dummyblk.addField("IGNORE_DISTRIBUTED"); 
      f = dummyblk.addField("FILE_STATE");
      f = dummyblk.addField("DOC_TYPE");
      // Bug 57779, Start
      f = dummyblk.addField("OUT_STR1");
      f = dummyblk.addField("IN_STR1");
      // Bug 57779, End

   }


   public void  adjust()
   {

      if (duprecord)
      {
         headset.setValue("DISTRIBUTION_NO","0"); 
         headset.setValue("LINE_NO","1");     
      }

      if (headlay.isMultirowLayout() || headlay.isSingleLayout())
      {
         headbar.addCommandValidConditions("activate","STATE","Disable","Pending");
         headbar.addCommandValidConditions("suspend","STATE","Disable","Suspended");       
      }

      if (headset.countRows()==0)
      {
         headbar.disableMultirowAction();
      }
      else
      {
         headbar.enableMultirowAction();
      }
   }


   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      return "DOCMAWDOCDISTRIBUTIONLISTOVWTITLE: Overview - Document Distribution";
   }

   protected String getTitle()
   {
      return "DOCMAWDOCDISTRIBUTIONLISTOVWTITLEOVERVIEW: Overview - Document Distribution";
   }

   protected AutoString getContents() throws FndException
   { 
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("DOCMAWDOCDISTRIBUTIONLISTOVWTITLEOVERVIEW: Overview - Document Distribution"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append("<input type=\"hidden\" name=\"CHECKED_VALUE\" value=\"\">\n");
      if (newrecord || duprecord)
         out.append(mgr.startPresentation("DOCMAWDOCDISTRIBUTIONLISTOVWTITLE: Overview - Document Distribution"));
      else
         out.append(mgr.startPresentation("DOCMAWDOCDISTRIBUTIONLISTOVWTITLEOVERVIEW: Overview - Document Distribution"));
      if (exedist)
      {
         out.append("       <p>Do you want to start destributing the highlighted distribution?</p>\n");
         out.append("       <p>\n");
         out.append(fmt.drawCheckbox("CHECK_IGNORE","",false,""));
         out.append(fmt.drawWriteLabel("DOCMAWDOCDISTRIBUTIONLISTOVWIGNORE: Resend already distributed documents")); // Bug Id 91182
         out.append(" </p>       \n");
         out.append("  <table>           \n");
         out.append("    <tr>\n");
         out.append("      <td>");
         out.append(fmt.drawSubmit("OK",mgr.translate("DOCMAWDOCDISTRIBUTIONLISTOVWDISTOK: Ok     "),"onClick='getCheckBoxValue()'"));
         out.append("&nbsp;\n");
         out.append(fmt.drawSubmit("CANCEL",mgr.translate("DOCMAWDOCDISTRIBUTIONLISTOVWDISTCANCEL: Cancel"),""));
         out.append("      </td>\n");
         out.append("    </tr>\n");
         out.append("  </table>\n");
      }
      if (headlay.isVisible() && !exedist)
      {
         out.append(headlay.show());
      }
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("function getCheckBoxValue()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  document.form.CHECKED_VALUE.value=document.form.CHECK_IGNORE.checked;  \n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("// Tranfer to EdmMacro.page file\n");
      if (bTranferToEDM)
      {
         appendDirtyJavaScript("   window.open(\"");
         appendDirtyJavaScript(sFilePath);
         appendDirtyJavaScript("\",\"anotherWindow\",\"status,resizable,scrollbars,width=500,height=500,left=100,top=100\");\n");      
      }
      appendDirtyJavaScript("function refreshParent()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("}\n");

      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }



}
