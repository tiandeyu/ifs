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
*  File        : DocSubjectOvw.java
*  Modified    :
*  Converted   : BaKa - 2001-03-01 ASP2JAVA Tool
*                     - Created Using the ASP file DocSubjectOvw.asp
*
*    25-05-01  Shdilk - Call Id 65466
*    25-05-01  Shdilk - Call Id 65494
*    05-12-03  Bakalk - (Multirow Action) Web Alignment done.
*    24-12-03  Bakalk - (Field Order) Web Alignment done.
*    01-10-04  Dikalk - Merged Bug Id 46944
*    11-10-04  Dikalk - Merged Bug Id 46009
*    01-11-04  SUKMLK - Fixed Bug with null folders being passed to docreference (call 119128)
*    08-08-07  ASSALK - Merged Bug 64804, Modified saveReturn().
*    20-08-07  ASSALK - Merged Bug 64508, Added LOV to SUBJECT_NO.
*    03-03-08   VIRALK   Bug Id 69651 Replaced view PERSON_INFO with PERSON_INFO_LOV.
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DocSubjectOvw extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocSubjectOvw");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPContext ctx;
   private ASPLog log;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   private ASPBlock itemblk;
   private ASPRowSet itemset;
   private ASPCommandBar itembar;
   private ASPTable itemtbl;
   private ASPBlockLayout itemlay;
   private ASPField f;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private String val;
   private int curr_row;
   private String luname;
   private String subno;
   private String subrev;
   private String subver;
   private ASPBuffer buff;
   private ASPQuery q;
   private String searchURL;
   private ASPCommand cmd;
   private ASPBuffer data;
   private int currow;
   private String folder_;
   private String Revision_;
   private String Variant_;
   private String subjectName;
   private String luname_;


   private String keyRef;
   private ASPBuffer row;
   private String attr;
   private int new_row_number;

   private boolean debug_enabled = true;
   //===============================================================
   // Construction
   //===============================================================
   public DocSubjectOvw(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      val   = null;
      curr_row   = 0;
      luname   = null;
      subno   = null;
      subrev   = null;
      subver   = null;
      buff   = null;
      q   = null;
      searchURL   = null;
      cmd   = null;
      data   = null;
      currow   = 0;
      folder_   = null;
      Revision_   = null;
      Variant_   = null;
      subjectName   = null;
      luname_   = null;



      //If any of these variables are mutable, reset them here.
      keyRef   = null;
      row   = null;
      attr   = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      DocSubjectOvw page = (DocSubjectOvw)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.val   = null;
      page.curr_row   = 0;
      page.luname   = null;
      page.subno   = null;
      page.subrev   = null;
      page.subver   = null;
      page.buff   = null;
      page.q   = null;
      page.searchURL   = null;
      page.cmd   = null;
      page.data   = null;
      page.currow   = 0;
      page.folder_   = null;
      page.Revision_   = null;
      page.Variant_   = null;
      page.subjectName   = null;
      page.luname_   = null;

      // Cloning immutable attributes
      page.ctx = page.getASPContext();
      page.log = page.getASPLog();
      page.headblk = page.getASPBlock(headblk.getName());
      page.headset = page.headblk.getASPRowSet();
      page.headbar = page.headblk.getASPCommandBar();
      page.headtbl = page.getASPTable(headtbl.getName());
      page.headlay = page.headblk.getASPBlockLayout();
      page.itemblk = page.getASPBlock(itemblk.getName());
      page.itemset = page.itemblk.getASPRowSet();
      page.itembar = page.itemblk.getASPCommandBar();
      page.itemtbl = page.getASPTable(itemtbl.getName());
      page.itemlay = page.itemblk.getASPBlockLayout();
      page.f = page.getASPField(f.getName());




      page.keyRef   = null;
      page.row   = null;
      page.attr   = null;

      return page;
   }

   public void run()
   {
      ASPManager mgr = getASPManager();


      trans = mgr.newASPTransactionBuffer();
      ctx = mgr.getASPContext();

      log = mgr.getASPLog();     
      

      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
         validate();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else if(mgr.dataTransfered())
         okFind();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SUBJECT_NO")) )
         okFind();

      adjust();
      
   }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      val = mgr.readValue("VALIDATE");
      mgr.showError("VALIDATE not implemented");
      mgr.endResponse();
   }

//-----------------------------------------------------------------------------
//-----------------------  CMDBAR CUSTOM FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

   public void  rmbConnectedDocs() //modified to support multirow actions: bakalk
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
      //headset.setFilterOn();

       /*
       curr_row = headset.getRowSelected();
       else
          curr_row = headset.getCurrentRowNo();

       headset.goTo(curr_row);

      luname   = "DocSubject";
      subno    = headset.getRow().getValue("SUBJECT_NO");
      subrev   = headset.getRow().getValue("SUBJECT_REV");
      subver   = headsheadset.storeSelections();et.getRow().getValue("SUBJECT_VER");


      keyRef   = "SUBJECT_NO="+subno+"^"+"SUBJECT_REV="+subrev+"^"+"SUBJECT_VER="+subver+"^";
       mgr.showAlert(keyRef);

       */



       /*
       row.addItem("LU_NAME",mgr.URLEncode(luname));
       row.addItem("KEY_REF",keyRef);
       */
       mgr.transferDataTo("DocReference.page",headset.getSelectedRows("LU_NAME,KEY_REF"));
   }

//-----------------------------------------------------------------------------
//----------------------------  CMDBAR FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

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

      headtbl.clearQueryRow();
      q = trans.addQuery(headblk);
      if(mgr.dataTransfered())
         q.addOrCondition( mgr.getTransferedData() );
      q.setOrderByClause("SUBJECT_NO,SUBJECT_REV,SUBJECT_VER");
      q.includeMeta("ALL");

      mgr.querySubmit(trans,headblk);

      if ( headset.countRows() == 0 )
         mgr.showAlert(mgr.translate("DOCMAWDOCSUBJECTOVWNODATA: No data found."));
   }

   public void  debug( String text,boolean popup)
   {
      ASPManager mgr = getASPManager();

      if (debug_enabled)
      {
         log.debug(text);

         // If popup argument is true, display message
         // in an alert box also
         if (popup)
         {
            mgr.showAlert(text);
         }
      }
   }

   public void saveReturn()
   {       
       ASPManager mgr = getASPManager();

       if ("New__".equals(headset.getRowStatus()))
       {           
           trans.clear();         
           int newRow = headset.getCurrentRowNo();	   
           headset.changeRow();          
           mgr.submit(trans); 
           headset.goTo(newRow);	                       

           ASPBuffer buf = headset.getRow();
           buf.setValue("KEY_REF", generateKeyRef());
           buf.setValue("LU_NAME","DocSubject");
           headset.setRow(buf);
       }
       else
       {
           trans.clear();         
           int newRow = headset.getCurrentRowNo();	   
           headset.changeRow();          
           mgr.submit(trans); 
           headset.goTo(newRow);
       }             
   }
      

   public void  newRow()
   {
      ASPManager mgr = getASPManager();     
      trans.clear();
      cmd = trans.addEmptyCommand("NEWROW", "DOC_SUBJECT_API.New__", headblk);
      cmd.setOption("ACTION", "PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("NEWROW/DATA");
      headset.addRow(data);
   }

   public void saveNew()
   {
      ASPManager mgr = getASPManager();
      trans.clear();      

      headset.changeRow();
      int rowno = headset.getCurrentRowNo();
      mgr.submit(trans);
      headset.goTo(rowno);

      ASPBuffer buf = headset.getRow();
      buf.setValue("KEY_REF", generateKeyRef());
      buf.setValue("LU_NAME","DocSubject");
      headset.setRow(buf);
      
      newRow();
   }



   public String generateKeyRef()
   {
       String key_ref_temp;             
       key_ref_temp = "SUBJECT_NO=" + headset.getValue("SUBJECT_NO") + "^" + "SUBJECT_REV=" + headset.getValue("SUBJECT_REV") + "^" + "SUBJECT_VER=" + headset.getValue("SUBJECT_VER") + "^";     
       return key_ref_temp;
   }  


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();


      headblk = mgr.newASPBlock("HEAD");

      // MDAHSE, 2001-01-18
      headblk.disableDocMan();

      f = headblk.addField("OBJID").
          setHidden();

      f = headblk.addField("OBJVERSION").
          setHidden();

      f = headblk.addField("SUBJECT_NO").
          setSize(20).
          setMaxLength(24).
          setMandatory().
          setLOV("DocSubjectLov.page").
          setLabel("DOCMAWDOCSUBJECTOVWSUBJECT_NO: Folder").
          setUpperCase().
          setReadOnly().
          setInsertable();

      f = headblk.addField("SUBJECT_REV").
          setSize(20).
          setMaxLength(10).
          setMandatory().
          setLabel("DOCMAWDOCSUBJECTOVWSUBJECT_REV: Revision").
          setUpperCase().
          setReadOnly().
          setInsertable();

      f = headblk.addField("SUBJECT_VER").
          setSize(20).
          setMaxLength(10).
          setMandatory().
          setLabel("DOCMAWDOCSUBJECTOVWSUBJECT_VER: Variant").
          setUpperCase().
          setReadOnly().
          setInsertable();

      f = headblk.addField("KEY_REF").   //w.a.
          setHidden().
          setFunction("'SUBJECT_NO='||SUBJECT_NO||'^'||'SUBJECT_REV='||SUBJECT_REV||'^'||'SUBJECT_VER='||SUBJECT_VER||'^'");

      f = headblk.addField("LU_NAME").  //w.a.
          setHidden().
          setFunction("'DocSubject'");


      f = headblk.addField("SUBJECT_NAME").
          setSize(20).
          setMaxLength(50).
          setLabel("DOCMAWDOCSUBJECTOVWSUBJECT_NAME: Description");

      f = headblk.addField("SUBJECT_MODE").
          setSize(25).
          setMaxLength(20).
          setSelectBox().
          enumerateValues("DOC_SUBJECT_MODE_API").
          setLabel("DOCMAWDOCSUBJECTOVWSUBJECT_MODE: Status");

      f = headblk.addField("SUBJECT_RESP_DEPT").
          setSize(20).
          setMaxLength(4).
          setLabel("DOCMAWDOCSUBJECTOVWSUBJECT_RESP_DEPT: Responsible Department").
          setUpperCase();

      f = headblk.addField("SUBJECT_RESP_SIGN").
          setSize(20).
          setMaxLength(20).
          setDynamicLOV ("PERSON_INFO_LOV").
          setLabel("DOCMAWDOCSUBJECTOVWSUBJECT_RESP_SIGN: Responsible Person").
          setUpperCase();

      f = headblk.addField("SUBJECT_RESP_DT","Date").
          setSize(20).
          setLabel("DOCMAWDOCSUBJECTOVWSUBJECT_RESP_DT: Responsible Date");

      f = headblk.addField("USER_CREATED").
          setSize(20).
          setMaxLength(30).
          setDynamicLOV("PERSON_INFO_USER").
          setLabel("DOCMAWDOCSUBJECTOVWUSER_CREATED: Created By").
          setUpperCase().
          setReadOnly();

      f = headblk.addField("DT_CRE","Date").
          setSize(20).
          setDefaultNotVisible().
          setLabel("DOCMAWDOCSUBJECTOVWDT_CRE: Date Created").
          setReadOnly();

      f = headblk.addField("USER_SIGN").
          setSize(20).
          setMaxLength(30).
          setDynamicLOV("PERSON_INFO_USER").
          setLabel("DOCMAWDOCSUBJECTOVWUSER_SIGN: Modified By").
          setUpperCase().
          setReadOnly();

      f = headblk.addField("DT_CHG","Date", "yyyy-MM-dd").
          setSize(20).
          setDefaultNotVisible().
          setLabel("DOCMAWDOCSUBJECTOVWDT_CHG: Date Modified").
          setReadOnly();

      f = headblk.addField("SUBJECT_INFO").
          setSize(20).
          setMaxLength(255).
          setDefaultNotVisible().
          setLabel("DOCMAWDOCSUBJECTOVWSUBJECT_INFO: Note");

      f = headblk.addField("PUBLISH").
          setSize(8).
          setCheckBox("0,1").
          setLabel("DOCMAWDOCSUBJECTOVWPUBLISH: Publish");

      headblk.setView("DOC_SUBJECT");
      headblk.defineCommand("DOC_SUBJECT_API","New__,Modify__,Remove__");

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);

      // MDAHSE, 2001-01-18
      // Renamed menu choice to "Documents"
      headbar.addCustomCommand("rmbConnectedDocs",mgr.translate("DOCMAWDOCSUBJECTOVWCONNDOCS: Documents"));
      headbar.enableMultirowAction();//w.a.     

      headbar.defineCommand(ASPCommandBar.SAVERETURN, "saveReturn");
      headbar.defineCommand(ASPCommandBar.SAVENEW, "saveNew");
      headbar.defineCommand(ASPCommandBar.NEWROW, "newRow");


      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("DOCMAWDOCSUBJECTOVWOVERDOCFOL: Document Folders"));
      headtbl.enableRowSelect();  //w.a.

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setFieldOrder("OBJID,OBJVERSION,SUBJECT_NO,SUBJECT_REV,SUBJECT_VER,KEY_REF,LU_NAME,SUBJECT_NAME,SUBJECT_MODE,SUBJECT_RESP_DEPT,SUBJECT_RESP_SIGN,SUBJECT_RESP_DT,,SUBJECT_INFO,PUBLISH,USER_CREATED,DT_CRE,USER_SIGN,DT_CHG");

      itemblk = mgr.newASPBlock("ITEM");

      // MDAHSE, 2001-01-18
      itemblk.disableDocMan();

      f = itemblk.addField("ITEM_INFO");
      f.setHidden();
      f.setFunction("''");

      f = itemblk.addField("ITEM_OBJID");//ITEM_INFO
      f.setHidden();

      f = itemblk.addField("ITEM_OBJVERSION");
      f.setHidden();

      f = itemblk.addField("ITEM_LU_NAME");
      f.setMandatory();
      f.setMaxLength(8);
      f.setReadOnly();
      f.setDbName("LU_NAME");
      f.setHidden();

      f = itemblk.addField("ITEM_KEY_REF");
      f.setMandatory();
      f.setMaxLength(600);
      f.setReadOnly();
      f.setDbName("KEY_REF");
      f.setHidden();

      f = itemblk.addField("ATTR");
      f.setHidden();
      f.setFunction("''");

      f = itemblk.addField("ACTION");
      f.setHidden();
      f.setFunction("''");

      itemblk.setView("DOC_REFERENCE");
      itemblk.defineCommand("DOC_REFERENCE_API","New__,Modify__,Remove__");      

      itemblk.setMasterBlock(headblk);
      itemset = itemblk.getASPRowSet();

   }


   public void  adjust()
   {

   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "DOCMAWDOCSUBJECTOVWTITLE: Overview - Document Folders";
   }

   protected String getTitle()
   {
      return "DOCMAWDOCSUBJECTOVWTITLE: Overview - Document Folders";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      appendToHTML(headlay.show());
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
   }

}
