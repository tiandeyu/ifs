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

package ifs.conmaw;
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

public class ConProjConstructionManContract extends ASPPageProvider
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.conmaw.ConProjConstructionManContract");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

//   private ASPBlock headblk;
//   private ASPRowSet headset;
//   private ASPCommandBar headbar;
//   private ASPTable headtbl;
//   private ASPBlockLayout headlay;
   private boolean bRefreshTree;
   private String comnd;
   private ASPContext ctx; 
   private ASPTabContainer tabs;

   //-----------------------------------------------------------------------------
   //---------- Item Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   private ASPBlock Pending_doc_reference_object_blk;
   private ASPRowSet Pending_doc_reference_object_set;
   private ASPCommandBar Pending_doc_reference_object_bar;
   private ASPTable Pending_doc_reference_object_tbl;
   private ASPBlockLayout Pending_doc_reference_object_lay;
   
   private ASPBlock Conference_doc_reference_object_blk;
   private ASPRowSet Conference_doc_reference_object_set;
   private ASPCommandBar Conference_doc_reference_object_bar;  
   private ASPTable Conference_doc_reference_object_tbl;
   private ASPBlockLayout Conference_doc_reference_object_lay; 
   



   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  ConProjConstructionManContract (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {      ASPManager mgr = getASPManager();
   ctx = mgr.getASPContext(); 
   String UNIT =  mgr.getQueryStringValue("UNIT");
   if(!(mgr.isEmpty(UNIT))) ctx.setGlobal("UNIT", UNIT);

   if( mgr.commandBarActivated() )
      eval(mgr.commandBarFunction());
   else if(mgr.dataTransfered())
      okFind();
   else if( !mgr.isEmpty(mgr.getQueryStringValue("PROJ_NO"))&&!mgr.isEmpty(mgr.getQueryStringValue("CONTRACT_ID")) )
      okFind();
   else
      okFind();    
   adjust();
   

   tabs.saveActiveTab(); 
   }


   //-----------------------------------------------------------------------------
   //------------------------  Item block cmd bar functions  ---------------------------
   //-----------------------------------------------------------------------------


   public void okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      String projNo;
      String planNo;
      String subProjNo;

      if(headset.countRows() == 0){      
             projNo = mgr.getQueryStringValue("PROJ_NO");
             planNo = mgr.getQueryStringValue("CONTRACT_ID");
      }else{
            projNo = headset.getValueAt(0, "PROJ_NO");
            planNo = headset.getValueAt(0, "CONTRACT_ID");
            }
         q = trans.addEmptyQuery(headblk);
         q.addWhereCondition("PROJ_NO = ? AND CONTRACT_ID = ? ");
         q.addParameter("PROJ_NO", projNo);
         q.addParameter("CONTRACT_ID", planNo);
         q.includeMeta("ALL");
         mgr.submit(trans);   
//         okFindITEM1();
         if (  headset.countRows() == 0 )
         {
            mgr.showAlert("QUANLITYPLANLINENODATA: No data found.");
            headset.clear();
         }else  okFindITEM11();
      
   }

   public void okFindITEM2()
   {
      ASPManager mgr = getASPManager();
      if(headset.countRows() == 0){
         return;
      }
         ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
         ASPQuery q;
         q = trans.addQuery(Pending_doc_reference_object_blk);
//         q.addWhereCondition("LU_NAME = ?");
//         q.addWhereCondition("KEY_REF LIKE ?");
//         q.addParameter("ITEM2_LU_NAME", "");
//         q.addParameter("ITEM2_KEY_REF", "PLAN_NO="+headset.getValue("PLAN_NO")+"^PROJ_NO="+headset.getValue("PROJ_NO")+"^SUB_PROJ_NO="+headset.getValue("SUB_PROJ_NO")+"%^");
         q.addWhereCondition("(LU_NAME = 'DocContractForView' AND KEY_REF='CONTRACT_ID="+headset.getValue("CONTRACT_ID")+"^PROJ_NO="+headset.getValue("PROJ_NO")+"^') OR (" +
         		"LU_NAME = 'QuanlityPlanLine' AND substr(key_ref,0,instr(key_ref,'SUB_PROJ_NO')-1) IN " +
         		"(select 'PLAN_NO='||b.plan_no||'^PROJ_NO='||b.proj_no||'^' FROM quanlity_plan b " +
         		"where b.proj_no='"+headset.getValue("PROJ_NO")+"' and b.unit='"+ctx.findGlobal("UNIT")+"' and b.CONTRACT_NO='"+headset.getValue("CONTRACT_ID")+"'))");
         q.addOrderByClause("KEY_REF");
         q.includeMeta("ALL");
         mgr.querySubmit(trans,Pending_doc_reference_object_blk);
         if (  Pending_doc_reference_object_set.countRows() == 0 )
         {
            mgr.showAlert("QUANLITYPLANLINENODATA: No data found.");
            Pending_doc_reference_object_set.clear();
         }
   }
   public void okFindITEM11()
   {
      ASPManager mgr = getASPManager();
      if(headset.countRows() == 0){
         return;
      }
      String luName = headblk.getLUName();//
      String view = headblk.getView();//
      String objid  = headset.getValue("OBJID");
//      if(!mgr.isEmpty(headset.getValue("PROJ_NO"))){  
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
         q.addParameter("ITEM11_LU_NAME", luName);
         q.addParameter("ITEM11_KEY_REF", keyReference);
         q.includeMeta("ALL");
         int headrowno = headset.getCurrentRowNo();
         mgr.querySubmit(trans,Conference_doc_reference_object_blk);
         headset.goTo(headrowno);
//      }
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
      headblk.addField("OBJSTATE").
              setHidden();
      headblk.addField("PROJ_NO").
              setMandatory().
              setWfProperties(). 
              setInsertable().
              setDynamicLOV("GENERAL_PROJECT").
              setLabel("PROJECTCONTRACTPROJNO: Proj No").
              setSize(20);  
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setReadOnly().
              setWfProperties(). 
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC (:PROJ_NO)").
              setLabel("PROJECTCONTRACTGENERALPROJECTPROJDESC: General Project Proj Desc").
              setSize(20);
      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");
      headblk.addField("CONTRACT_ID"). 
              setWfProperties(). 
              setUpperCase().  
              setInsertable().  
              setLabel("PROJECTCONTRACTCONTRACTID: Contract Id").
              setSize(20);   
     headblk.addField("CONTRACT_DESC").
             setMandatory().
             setWfProperties(). 
             setInsertable().
             setLabel("PROJECTCONTRACTCONTRACTDESC: Contract Desc").
             setSize(120);    
     
//      headblk.addField("CONTRACT_TYPE").
//              enumerateValues("Contract_Type_API").
//              setSelectBox().
//              setDefaultNotVisible().
//              setInsertable().
//              setHidden(). 
//              setLabel("PROJECTCONTRACTCONTRACTTYPE: Contract Type").
//              setSize(20);
//      headblk.addField("BACK_FILE").
//              setInsertable().
//              setDefaultNotVisible().
//              setLabel("PROJECTCONTRACTBACKFILE: Back File").
//              setSize(5).
//              setCheckBox("F,T");
      headblk.setView("DOC_CONTRACT_FOR_VIEW");
      headblk.defineCommand("PROJECT_CONTRACT_API","");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      //headbar.disableCommand(headbar.FIND);
      //headbar.disableCommand(headbar.OKFIND);
      headbar.disableCommand(headbar.BACK);
      headbar.disableCommand(headbar.DUPLICATEROW);
      headbar.disableCommand(headbar.FORWARD);
      headbar.disableCommand(headbar.BACKWARD);
      headbar.disableCommand(headbar.SAVENEW); 
      headbar.disableCommand(headbar.NEWROW);
      headbar.addCustomCommand("activateITEM1",mgr.translate("DOCCONTRACTFORVIEW: Doc Contract For View"));
      headbar.addCustomCommand("activateITEM2",mgr.translate("QUAPLANLINEDOCLIST: Qua Plan Line Sub Doc List"));
      
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("DOCCONTRACTFORVIEWTBLHEAD: Doc Contract For View");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
      headlay.setDialogColumns(2);
      headlay.setSimple  ("GENERAL_PROJECT_PROJ_DESC");  
      headlay.setSimple  ("CONTRACT_DESC");  

      Pending_doc_reference_object_blk = mgr.newASPBlock("ITEM2");
      Pending_doc_reference_object_blk.addField("ITEM2_VIEW_FILE").
                                       setFunction("''").
                                       setReadOnly().  
                                       unsetQueryable().
                                       setLabel("DOCSENDTRANSREFERENCEBLKVIEWFILE: View File").
                                       setHyperlink("../docmaw/EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL", "ITEM2_DOC_CLASS DOC_CLASS,ITEM2_DOC_NO DOC_NO,ITEM2_DOC_SHEET DOC_SHEET,ITEM2_DOC_REV DOC_REV", "NEWWIN").
                                       setAsImageField();
      Pending_doc_reference_object_blk.addField("ITEM2_DOC_CLASS").setDbName("DOC_CLASS").setLabel("DOCSENDTRANSREFERENCEBLKDOCCLASS: Doc Class");
      Pending_doc_reference_object_blk.addField("ITEM2_DOC_NO").setDbName("DOC_NO").setLabel("DOCSENDTRANSREFERENCEBLKDOCNO: Doc No").setHidden();
      Pending_doc_reference_object_blk.addField("ITEM2_DOC_SHEET").setDbName("DOC_SHEET").setLabel("DOCSENDTRANSREFERENCEBLKDOCSHEET: Doc sheet").setHidden();
      Pending_doc_reference_object_blk.addField("ITEM2_DOC_REV").setDbName("DOC_REV").setLabel("DOCSENDTRANSREFERENCEBLKDOCREV: Doc Rev").setHidden();
      Pending_doc_reference_object_blk.addField("ITEM2_LU_NAME").setDbName("LU_NAME").setLabel("DOCSENDTRANSREFERENCEBLKLUNAME: Lu Name").setHidden();
      Pending_doc_reference_object_blk.addField("ITEM2_KEY_REF").setDbName("KEY_REF").setLabel("DOCSENDTRANSREFERENCEBLKKEYREF: Key Ref").setHidden();
      Pending_doc_reference_object_blk.addField("ITEM2_KEY_VALUE").setDbName("KEY_VALUE").setLabel("DOCSENDTRANSREFERENCEBLKKEYVALUE: Key Value").setHidden();
      Pending_doc_reference_object_blk.addField("ITEM2_REV_TITLE").setDbName("DOC_TITLE").setLabel("DOCSENDTRANSREFERENCEBLKREVTITLE: Rev Title").setFieldHyperlink("../docmaw/DocIssue.page", "ITEM2_PAGE_URL","ITEM2_DOC_CLASS DOC_CLASS,ITEM2_DOC_NO DOC_NO,ITEM2_DOC_SHEET DOC_SHEET,ITEM2_DOC_REV DOC_REV");
      Pending_doc_reference_object_blk.addField("ITEM2_DOC_CODE").setDbName("DOC_CODE").setLabel("DOCSENDTRANSREFERENCEBLKDOCCODE: Doc Code");
      Pending_doc_reference_object_blk.addField("ITEM2_SUB_CLASS").setDbName("SUB_CLASS").setLabel("DOCSENDTRANSREFERENCEBLKSUBCLASS: Sub Class");
      Pending_doc_reference_object_blk.addField("ITEM2_PAGE_URL").setFunction("nvl(DOC_CLASS_API.Get_Page_Url(:ITEM2_DOC_CLASS), DOC_SUB_CLASS_API.Get_Page_Url(:ITEM2_DOC_CLASS,:ITEM2_SUB_CLASS))").setHidden();
      Pending_doc_reference_object_blk.addField("ITEM2_IS_ELE_DOC").
                                       setFunction("EDM_FILE_API.Have_Edm_File(:ITEM2_DOC_CLASS,:ITEM2_DOC_NO,:ITEM2_DOC_SHEET,:ITEM2_DOC_REV)").
                                       setHidden().
                                       setLabel("DOCSENDTRANSREFERENCEBLKISELEDOC: Is Ele Doc").
                                       setSize(5);
      
      Pending_doc_reference_object_blk.setView("DOC_REFERENCE_OBJECT");
//      Pending_doc_reference_object_blk.setMasterBlock(headblk);
      Pending_doc_reference_object_set = Pending_doc_reference_object_blk.getASPRowSet();
      Pending_doc_reference_object_bar = mgr.newASPCommandBar(Pending_doc_reference_object_blk);
      Pending_doc_reference_object_bar.defineCommand(Pending_doc_reference_object_bar.OKFIND, "okFindITEM2");
      Pending_doc_reference_object_tbl = mgr.newASPTable(Pending_doc_reference_object_blk);
      Pending_doc_reference_object_tbl.enableRowSelect();
      Pending_doc_reference_object_tbl.setWrap();
      Pending_doc_reference_object_lay = Pending_doc_reference_object_blk.getASPBlockLayout();
      Pending_doc_reference_object_lay.setDefaultLayoutMode(Pending_doc_reference_object_lay.MULTIROW_LAYOUT);
    
      Conference_doc_reference_object_blk = mgr.newASPBlock("ITEM11");
      Conference_doc_reference_object_blk.addField("ITEM11_VIEW_FILE").
                                          setFunction("''").
                                          setReadOnly().
                                          unsetQueryable().
                                          setLabel("DOCSENDTRANSREFERENCEBLKVIEWFILE: View File").
                                          setHyperlink("../docmaw/EdmMacro.page?PROCESS_DB=VIEW&DOC_TYPE=ORIGINAL", "ITEM11_DOC_CLASS DOC_CLASS,ITEM11_DOC_NO DOC_NO,ITEM11_DOC_SHEET DOC_SHEET,ITEM11_DOC_REV DOC_REV", "NEWWIN").
                                          setAsImageField();
      Conference_doc_reference_object_blk.addField("ITEM11_DOC_CLASS").setDbName("DOC_CLASS").setLabel("DOCSENDTRANSREFERENCEBLKDOCCLASS: Doc Class");
      Conference_doc_reference_object_blk.addField("ITEM11_DOC_NO").setDbName("DOC_NO").setLabel("DOCSENDTRANSREFERENCEBLKDOCNO: Doc No").setHidden();
      Conference_doc_reference_object_blk.addField("ITEM11_DOC_SHEET").setDbName("DOC_SHEET").setLabel("DOCSENDTRANSREFERENCEBLKDOCSHEET: Doc sheet").setHidden();
      Conference_doc_reference_object_blk.addField("ITEM11_DOC_REV").setDbName("DOC_REV").setLabel("DOCSENDTRANSREFERENCEBLKDOCREV: Doc Rev").setHidden();
      Conference_doc_reference_object_blk.addField("ITEM11_LU_NAME").setDbName("LU_NAME").setLabel("DOCSENDTRANSREFERENCEBLKLUNAME: Lu Name").setHidden();
      Conference_doc_reference_object_blk.addField("ITEM11_KEY_REF").setDbName("KEY_REF").setLabel("DOCSENDTRANSREFERENCEBLKKEYREF: Key Ref").setHidden();
      Conference_doc_reference_object_blk.addField("ITEM11_KEY_VALUE").setDbName("KEY_VALUE").setLabel("DOCSENDTRANSREFERENCEBLKKEYVALUE: Key Value").setHidden();
      Conference_doc_reference_object_blk.addField("ITEM11_REV_TITLE").setDbName("DOC_TITLE").setLabel("DOCSENDTRANSREFERENCEBLKREVTITLE: Rev Title").setFieldHyperlink("../docmaw/DocIssue.page", "ITEM11_PAGE_URL","ITEM11_DOC_CLASS DOC_CLASS,ITEM11_DOC_NO DOC_NO,ITEM11_DOC_SHEET DOC_SHEET,ITEM11_DOC_REV DOC_REV");
      Conference_doc_reference_object_blk.addField("ITEM11_DOC_CODE").setDbName("DOC_CODE").setLabel("DOCSENDTRANSREFERENCEBLKDOCCODE: Doc Code");
      Conference_doc_reference_object_blk.addField("ITEM11_SUB_CLASS").setDbName("SUB_CLASS").setLabel("DOCSENDTRANSREFERENCEBLKSUBCLASS: Sub Class");
      Conference_doc_reference_object_blk.addField("ITEM11_PAGE_URL").setFunction("nvl(DOC_CLASS_API.Get_Page_Url(:ITEM11_DOC_CLASS), DOC_SUB_CLASS_API.Get_Page_Url(:ITEM11_DOC_CLASS,:ITEM11_SUB_CLASS))").setHidden();
      Conference_doc_reference_object_blk.addField("ITEM11_IS_ELE_DOC").
                                          setFunction("EDM_FILE_API.Have_Edm_File(:ITEM11_DOC_CLASS,:ITEM11_DOC_NO,:ITEM11_DOC_SHEET,:ITEM11_DOC_REV)").
                                          setHidden().
                                          setLabel("DOCSENDTRANSREFERENCEBLKISELEDOC: Is Ele Doc").
                                          setSize(5);    
      
      Conference_doc_reference_object_blk.setView("DOC_REFERENCE_OBJECT");
      Conference_doc_reference_object_blk.setMasterBlock(headblk);
      Conference_doc_reference_object_set = Conference_doc_reference_object_blk.getASPRowSet();
      Conference_doc_reference_object_bar = mgr.newASPCommandBar(Conference_doc_reference_object_blk);
      Conference_doc_reference_object_bar.defineCommand(Conference_doc_reference_object_bar.OKFIND, "okFindITEM11");
      Conference_doc_reference_object_tbl = mgr.newASPTable(Conference_doc_reference_object_blk);
      Conference_doc_reference_object_tbl.enableRowSelect();
      Conference_doc_reference_object_tbl.setWrap();
      Conference_doc_reference_object_lay = Conference_doc_reference_object_blk.getASPBlockLayout();
      Conference_doc_reference_object_lay.setDefaultLayoutMode(Conference_doc_reference_object_lay.MULTIROW_LAYOUT);

      tabs = newASPTabContainer("TAB1");      
      tabs.addTab("DOCCONTRACTFORVIEW: Doc Contract For View","javascript:commandSet('MAIN.activateITEM1','')");
      tabs.addTab("QUAPLANLINEDOCLIST: Qua Plan Line Sub Doc List","javascript:commandSet('MAIN.activateITEM2','')");
      tabs.setContainerSpace(0);
      tabs.setLeftTabSpace(0);
      tabs.setTabWidth(68);
      
      headbar.removeCustomCommand("activateITEM1");
      headbar.removeCustomCommand("activateITEM2");

     
   }


   public void  adjust()
   {
      // fill function body
   }

   protected String getImageFieldTag(ASPField imageField, ASPRowSet rowset, int rowNum) throws FndException
   {
      ASPManager mgr = getASPManager();
      String imgSrc = mgr.getASPConfig().getImagesLocation();
      if (rowset.countRows() > 0)
      {
         if(("ITEM11_VIEW_FILE").equals(imageField.getName()))
         {
            if ("TRUE".equals(rowset.getValueAt(rowNum, "ITEM11_IS_ELE_DOC")))
            {
               imgSrc += "folder.gif";
               return "<img src=\""+imgSrc+"\" height=\"16\" width=\"16\" border=\"0\">";
            }
            else
            {
               return "";
            }
        }else if ("ITEM2_VIEW_FILE".equals(imageField.getName()))
        {
            if ("TRUE".equals(rowset.getValueAt(rowNum, "ITEM2_IS_ELE_DOC")))
            {
               imgSrc += "folder.gif";
               return "<img src=\""+imgSrc+"\" height=\"16\" width=\"16\" border=\"0\">";
            }    
            else
            {
               return "";
            } 
        }    
    }
     return "" ;        
  }
   
   

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return null;
   }


   protected String getTitle()
   {
      return null;
   }

   public String tabsInit()
   {
      return(tabs.showTabsInit());
   }

   public String tabsFinish()
   {
      return(tabs.showTabsFinish());
   } 

   public void activateITEM1() {
       tabs.setActiveTab(1);  
       okFind(); 
   }
   public void activateITEM2() {
       tabs.setActiveTab(2); 
       okFindITEM2();
   }      

   protected AutoString getContents() throws FndException{
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();
      out.clear();  
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag(""));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append("\n");

      appendToHTML(tabsInit());

      if (tabs.getActiveTab() == 1){
         if (headlay.isVisible())
            appendToHTML(headlay.show());
         else{
            headlay.setLayoutMode(headlay.CUSTOM_LAYOUT);
            appendToHTML(headlay.show());
         }
         if (Conference_doc_reference_object_lay.isVisible())
            appendToHTML(Conference_doc_reference_object_lay.show());

      }else if (tabs.getActiveTab() == 2)
         appendToHTML(Pending_doc_reference_object_lay.show());
                  
      appendToHTML(tabsFinish());       
 
      appendDirtyJavaScript("function refreshTree()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   parent_url = this.parent.location.href;\n");
      appendDirtyJavaScript("  url_str = this.parent.frames[\"contents\"].location.href;\n");  
      appendDirtyJavaScript("        this.parent.frames[\"contents\"].location.href = url_str;\n");
      appendDirtyJavaScript("}\n");    
     
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
 }

}
