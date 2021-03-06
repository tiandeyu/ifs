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
* File                          :
* Description                   :
* Notes                         :
* Other Programs Called :
* ----------------------------------------------------------------------------
* Modified    : Automatically generated by IFS/Design
* ----------------------------------------------------------------------------
*/

//-----------------------------------------------------------------------------
//-----------------------------   Package def  ------------------------------
//-----------------------------------------------------------------------------

package ifs.quamaw;
//-----------------------------------------------------------------------------
//-----------------------------   Import def  ------------------------------
//-----------------------------------------------------------------------------

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
      
//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class QuaSupervision extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.quamaw.QuaSupervision");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   
   private ASPBlock Conference_doc_reference_object_blk;
   private ASPRowSet Conference_doc_reference_object_set;
   private ASPCommandBar Conference_doc_reference_object_bar;  
   private ASPTable Conference_doc_reference_object_tbl;
   private ASPBlockLayout Conference_doc_reference_object_lay;

   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  QuaSupervision (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {
      ASPManager mgr = getASPManager();

      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("ID")) )
         okFind();
      else
         okFind();
      adjust();
   }
   //-----------------------------------------------------------------------------
   //------------------------  Command Bar functions  ---------------------------
   //-----------------------------------------------------------------------------

   public void okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      mgr.createSearchURL(headblk);
      q = trans.addQuery(headblk);
      q.includeMeta("ALL");
      if(mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      mgr.querySubmit(trans,headblk);
      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("QUASUPERVISIONNODATA: No data found.");
         headset.clear();
      }
   }



   public void countFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getValue("N")));
      headset.clear();
   }



   public void newRow()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPBuffer data;
      ASPCommand cmd;

      cmd = trans.addEmptyCommand("HEAD","QUA_SUPERVISION_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }

   public void okFindITEM12()
   {
      ASPManager mgr = getASPManager();
      if(headset.countRows() == 0){
         return;
      }
      String luName = headblk.getLUName();//
      String view = headblk.getView();//
      String objid  = headset.getValue("OBJID");
      if(!"".equals(headset.getValue("PROJ_NO"))&&headset.getValue("PROJ_NO") != null){     
         ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
         ASPQuery q;
         ASPCommand  cmd = trans.addCustomCommand("KEYREF", " client_sys.get_key_reference");
         cmd.addParameter("KEYREF", "S", "OUT", null);
         cmd.addParameter("BIZ_LU", "S", "IN", luName);
         cmd.addParameter("BIZ_OBJID", "S", "IN", objid);
         trans = mgr.validate(trans);
         String keyReference = trans.getValue("KEYREF/DATA/KEYREF");
         trans.clear();
         q = trans.addQuery(Conference_doc_reference_object_blk);
         q.addWhereCondition("LU_NAME = ?");
         q.addWhereCondition("KEY_REF = ?");
//         q.addWhereCondition("DOC_CLASS = ?");
         q.addParameter("ITEM2_LU_NAME", luName);
         q.addParameter("ITEM2_KEY_REF", keyReference);
//         q.addParameter("ITEM2_DOC_CLASS", "HYJY");  
         q.includeMeta("ALL");  
         int headrowno = headset.getCurrentRowNo();
         mgr.querySubmit(trans,Conference_doc_reference_object_blk);
         headset.goTo(headrowno);  
      }
   }
   //-----------------------------------------------------------------------------
   //------------------------  Predefines Head ---------------------------
   //-----------------------------------------------------------------------------

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("MAIN");
      headblk.addField("OBJID").
              setHidden();
      headblk.addField("OBJVERSION").
              setHidden();
      headblk.addField("PROJ_NO").
              setMandatory().
              setInsertable().
              setDefaultNotVisible().
              setLabel("QUASUPERVISIONPROJNO: Proj No").
              setDynamicLOV("GENERAL_PROJECT",600,445).
              setSize(35);
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC ( :PROJ_NO)").
              setLabel("HSEACCIDENTMEASUREGENERALPROJECTPROJDESC: General Project Proj Desc").
              setReadOnly().
              setSize(50);
      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");
      headblk.addField("ORG_NO").
              setInsertable().
              setDefaultNotVisible().
              setDynamicLOV("GENERAL_ZONE", "PROJ_NO").
              setLabel("QUASUPERVISIONORGNO: Org No").
              setSize(35);
      headblk.addField("ORG_DESC").
         setFunction("GENERAL_ZONE_API.GET_ZONE_DESC ( :ORG_NO)").
         setLabel("QUASUPERVISIONORGDESC: Org No Desc").
         setReadOnly().
         setSize(50);
      mgr.getASPField("ORG_NO").setValidation("ORG_DESC");
      headblk.addField("ID").
              setHidden().
              setInsertable().
              setLabel("QUASUPERVISIONID: Id").
              setSize(50);
      headblk.addField("FILE_NAME").
              setInsertable().
              setLabel("QUASUPERVISIONFILENAME: File Name").
              setSize(50);
              //setMaxLength(200);
      headblk.addField("NOTE").
              setInsertable().
              setDefaultNotVisible().
              setLabel("QUASUPERVISIONNOTE: Note").
              setSize(150).
              setMaxLength(200).
              setHeight(3);
      headblk.setView("QUA_SUPERVISION");
      headblk.defineCommand("QUA_SUPERVISION_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("QUASUPERVISIONTBLHEAD: Qua Supervisions");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setDataSpan("FILE_NAME", 5);
      headlay.setDataSpan("PROJ_NO", 5);
      headlay.setDataSpan("ORG_NO", 5);
      headlay.setSimple("ORG_DESC");
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC");
     
      
      Conference_doc_reference_object_blk = mgr.newASPBlock("ITEM3");
      Conference_doc_reference_object_blk.addField("ITEM2_VIEW_FILE").
                                          setFunction("''").
                                          setReadOnly().
                                          unsetQueryable().
                                          setLabel("DOCSENDTRANSREFERENCEBLKVIEWFILE: View File").
                                          setHyperlink("../docmaw/EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL", "ITEM2_DOC_CLASS DOC_CLASS,ITEM2_DOC_NO DOC_NO,ITEM2_DOC_SHEET DOC_SHEET,ITEM2_DOC_REV DOC_REV", "NEWWIN").
                                          setAsImageField();
      Conference_doc_reference_object_blk.addField("ITEM2_DOC_CLASS").setDbName("DOC_CLASS").setLabel("DOCSENDTRANSREFERENCEBLKDOCCLASS: Doc Class");
      Conference_doc_reference_object_blk.addField("ITEM2_DOC_NO").setDbName("DOC_NO").setLabel("DOCSENDTRANSREFERENCEBLKDOCNO: Doc No").setHidden();
      Conference_doc_reference_object_blk.addField("ITEM2_DOC_SHEET").setDbName("DOC_SHEET").setLabel("DOCSENDTRANSREFERENCEBLKDOCSHEET: Doc sheet").setHidden();
      Conference_doc_reference_object_blk.addField("ITEM2_DOC_REV").setDbName("DOC_REV").setLabel("DOCSENDTRANSREFERENCEBLKDOCREV: Doc Rev").setHidden();
      Conference_doc_reference_object_blk.addField("ITEM2_LU_NAME").setDbName("LU_NAME").setLabel("DOCSENDTRANSREFERENCEBLKLUNAME: Lu Name").setHidden();
      Conference_doc_reference_object_blk.addField("ITEM2_KEY_REF").setDbName("KEY_REF").setLabel("DOCSENDTRANSREFERENCEBLKKEYREF: Key Ref").setHidden();
      Conference_doc_reference_object_blk.addField("ITEM2_KEY_VALUE").setDbName("KEY_VALUE").setLabel("DOCSENDTRANSREFERENCEBLKKEYVALUE: Key Value").setHidden();
      Conference_doc_reference_object_blk.addField("ITEM2_REV_TITLE").setDbName("DOC_TITLE").setLabel("DOCSENDTRANSREFERENCEBLKREVTITLE: Rev Title").setFieldHyperlink("../docmaw/DocIssue.page", "ITEM2_PAGE_URL","ITEM2_DOC_CLASS DOC_CLASS,ITEM2_DOC_NO DOC_NO,ITEM2_DOC_SHEET DOC_SHEET,ITEM2_DOC_REV DOC_REV");
      Conference_doc_reference_object_blk.addField("ITEM2_DOC_CODE").setDbName("DOC_CODE").setLabel("DOCSENDTRANSREFERENCEBLKDOCCODE: Doc Code");
      Conference_doc_reference_object_blk.addField("ITEM2_SUB_CLASS").setDbName("SUB_CLASS").setLabel("DOCSENDTRANSREFERENCEBLKSUBCLASS: Sub Class");
      Conference_doc_reference_object_blk.addField("ITEM2_PAGE_URL").setFunction("nvl(DOC_CLASS_API.Get_Page_Url(:ITEM2_DOC_CLASS), DOC_SUB_CLASS_API.Get_Page_Url(:ITEM2_DOC_CLASS,:ITEM2_SUB_CLASS))").setHidden();
      Conference_doc_reference_object_blk.addField("ITEM2_IS_ELE_DOC").
                                          setFunction("EDM_FILE_API.Have_Edm_File(:ITEM2_DOC_CLASS,:ITEM2_DOC_NO,:ITEM2_DOC_SHEET,:ITEM2_DOC_REV)").
                                          setHidden().
                                          setLabel("DOCSENDTRANSREFERENCEBLKISELEDOC: Is Ele Doc").
                                          setSize(5);    
      
      Conference_doc_reference_object_blk.setView("DOC_REFERENCE_OBJECT");
      Conference_doc_reference_object_blk.setMasterBlock(headblk);
      Conference_doc_reference_object_set = Conference_doc_reference_object_blk.getASPRowSet();
      Conference_doc_reference_object_bar = mgr.newASPCommandBar(Conference_doc_reference_object_blk);
      Conference_doc_reference_object_bar.defineCommand(Conference_doc_reference_object_bar.OKFIND, "okFindITEM12");
      Conference_doc_reference_object_tbl = mgr.newASPTable(Conference_doc_reference_object_blk);
      Conference_doc_reference_object_tbl.enableRowSelect();
      Conference_doc_reference_object_tbl.setWrap();
      Conference_doc_reference_object_lay = Conference_doc_reference_object_blk.getASPBlockLayout();
      Conference_doc_reference_object_lay.setDefaultLayoutMode(Conference_doc_reference_object_lay.MULTIROW_LAYOUT);
     

   }



   public void  adjust()
   {
      // fill function body
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "QUASUPERVISIONDESC: Quality Supervision";
   }


   protected String getTitle()
   {
      return "QUASUPERVISIONTITLE: Quality Supervision";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if (headlay.isVisible())
          appendToHTML(headlay.show());
      if (  (headlay.isSingleLayout() || headlay.isCustomLayout())) {
         appendToHTML(Conference_doc_reference_object_lay.show());
      }
   }
}
