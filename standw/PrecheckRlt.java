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

package ifs.standw;
//-----------------------------------------------------------------------------
//-----------------------------   Import def  ------------------------------
//-----------------------------------------------------------------------------

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.hzwflw.HzASPPageProviderWf;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class PrecheckRlt extends HzASPPageProviderWf
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.standw.PrecheckRlt");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   //-----------------------------------------------------------------------------
   //---------- Item Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock precheck_rlt_line_blk;
   private ASPRowSet precheck_rlt_line_set;
   private ASPCommandBar precheck_rlt_line_bar;
   private ASPTable precheck_rlt_line_tbl;
   private ASPBlockLayout precheck_rlt_line_lay;

   private String Id;
   private String  proj_type;
   
   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  PrecheckRlt (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      super.run();
      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if("TRUE".equals(mgr.readValue("REFRESH_PARENT")))
      {  headset.refreshRow();
         okFindITEM1();
      }
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("ID")) )
         okFind();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
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
         mgr.showAlert("PRECHECKRLTNODATA: No data found.");
         headset.clear();
      }
      eval( precheck_rlt_line_set.syncItemSets() );
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

      cmd = trans.addEmptyCommand("HEAD","PRECHECK_RLT_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }


   //-----------------------------------------------------------------------------
   //------------------------  Item block cmd bar functions  ---------------------------
   //-----------------------------------------------------------------------------


   public void okFindITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;

      q = trans.addQuery(precheck_rlt_line_blk);
      q.addWhereCondition("ID = ?");
      q.addParameter("ID", headset.getValue("ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,precheck_rlt_line_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","PRECHECK_RLT_LINE_API.New__",precheck_rlt_line_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_ID", headset.getValue("ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      precheck_rlt_line_set.addRow(data);
   }
   
   public void validate()
   {
       ASPManager mgr = getASPManager();
       ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
       ASPCommand cmd;
       String val = mgr.readValue("VALIDATE"); 
       String txt = "";
       String contractDesc = "";
       String contructOrg = "";
       String contructOrgDesc = "";
       String projDesc = "";
       String projType = "";
       String projTypeDesc = "";

       if ("CONTRACT_NO".equals(val)) {   
          cmd = trans.addCustomFunction("CONTRACTDESC", 
                "PROJECT_CONTRACT_API.Get_Contract_Desc", "CONTRACT_DESC");
          cmd.addParameter("PRE_EXAM_NO,CONTRACT_NO");
          
          cmd = trans.addCustomFunction("CONSTRUCTORG", 
                "PROJECT_CONTRACT_API.Get_Secend_Side", "CONSTRUCT_ORG");
          cmd.addParameter("PRE_EXAM_NO,CONTRACT_NO");         
          
          trans = mgr.validate(trans);
          contractDesc = trans.getValue("CONTRACTDESC/DATA/CONTRACT_DESC");
          contructOrg = trans.getValue("CONSTRUCTORG/DATA/CONSTRUCT_ORG");
          
          trans.clear();
          cmd.clear();
          cmd = trans.addCustomFunction("CONSTRUCTORGDESC", 
                "GENERAL_ZONE_API.Get_Zone_Desc", "CONSTRUCT_ORG_DESC");
          cmd.addParameter("CONSTRUCT_ORG",contructOrg);
   
          trans = mgr.validate(trans);
          contructOrgDesc = trans.getValue("CONSTRUCTORGDESC/DATA/CONSTRUCT_ORG_DESC");
          
          txt = ((mgr.isEmpty(contractDesc)) ? "" : contractDesc) + "^" + ((mgr.isEmpty(contructOrg)) ? "" : contructOrg) + "^";
          txt = txt + ((mgr.isEmpty(contructOrgDesc)) ? "" : contructOrgDesc) + "^";
          mgr.responseWrite(txt);
       }
       if("PRE_EXAM_NO".equals(val)) {
          cmd = trans.addCustomFunction("PROJDESC", 
                "GENERAL_PROJECT_API.GET_PROJ_DESC", "PROJ_NAME");
          cmd.addParameter("PRE_EXAM_NO");
          
          cmd = trans.addCustomFunction("PROJTYPE", 
                "GENERAL_PROJECT_API.Get_Project_Type_Id", "PROJECT_TYPE_NO");
          cmd.addParameter("PRE_EXAM_NO");         
          
          trans = mgr.validate(trans);
          projDesc = trans.getValue("PROJDESC/DATA/PROJ_NAME");
          projType = trans.getValue("PROJTYPE/DATA/PROJECT_TYPE_NO");
          
          trans.clear();
          cmd.clear();         
          cmd = trans.addCustomFunction("PROJTYPEDESC", "PROJECT_TYPE_API.Get_Project_Type_Name", "PROJECT_TYPE_NAME");          
          cmd.addParameter("PROJECT_TYPE_NO",projType);
          
          trans = mgr.validate(trans);         
          projTypeDesc = trans.getValue("PROJTYPEDESC/DATA/PROJECT_TYPE_NAME");
          
          txt = ((mgr.isEmpty(projDesc)) ? "" : projDesc) + "^" + ((mgr.isEmpty(projType)) ? "" : projType) + "^";
          txt = txt + ((mgr.isEmpty(projTypeDesc)) ? "" : projTypeDesc) + "^";
          mgr.responseWrite(txt);
       }
       mgr.endResponse();
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
      headblk.addField("ID").
              setMandatory().
              setInsertable().
              setHidden().
              setLabel("PRECHECKRLTID: Id").
              setSize(50);
      headblk.addField("PRE_EXAM_NO").
              setInsertable().
              setReadOnly().
              setDynamicLOV("GENERAL_PROJECT").
              setCustomValidation("PRE_EXAM_NO", "PROJ_NAME,PROJECT_TYPE_NO,PROJECT_TYPE_NAME").
              setLabel("PRECHECKRLTPREEXAMNO: Pre Exam No").
              setSize(20);
      headblk.addField("PROJ_NAME").
              setReadOnly().
              setFunction("GENERAL_PROJECT_API.Get_Proj_Desc(:PRE_EXAM_NO)").
              setLabel("PRECHECKRLTPREEXAMNAME: Pre Exam Name").
              setSize(30);
//      mgr.getASPField("PRE_EXAM_NO").setValidation("PROJ_NAME");
      headblk.addField("PROJECT_TYPE_NO").
              setInsertable().
              setHidden().
              setLabel("PRECHECKRLTPROJECTTYPENO: Project Type No").
              setSize(20);
      headblk.addField("PROJECT_TYPE_NAME").
              setReadOnly().
              setFunction("PROJECT_TYPE_API.Get_Project_Type_Name(GENERAL_PROJECT_API.Get_Project_Type_Id ( :PRE_EXAM_NO))").
              setLabel("PRECHECKRLTPROJECTTYPENAME: Project Type Name").
              setSize(30);
//      mgr.getASPField("PROJECT_TYPE_NO").setValidation("PROJECT_TYPE_NAME");
      headblk.addField("PRE_EXAM_NAME").
              setInsertable().
              setHidden().
              setLabel("PRECHECKRLTPREEXAMNAME: Pre Exam Name").
              setSize(50);
      headblk.addField("CONTRACT_NO").
              setInsertable().
              setDynamicLOV("PROJECT_CONTRACT","PRE_EXAM_NO").
              setLabel("PRECHECKRLTCONTRACTNO: Contract No").
              setCustomValidation("PRE_EXAM_NO,CONTRACT_NO", "CONTRACT_DESC,CONSTRUCT_ORG,CONSTRUCT_ORG_DESC").
              setSize(20);
      headblk.addField("CONTRACT_DESC").
              setReadOnly().
              setFunction("PROJECT_CONTRACT_API.Get_Contract_Desc (:PRE_EXAM_NO,:CONTRACT_NO)").
              setLabel("PRECHECKRLTCONTRACTNAME: Contract Name").
              setSize(30);
      headblk.addField("ORG_NO").
              setInsertable().
              setHidden().
              setLabel("PRECHECKRLTORGNO: Org No").
              setSize(20);
      headblk.addField("CONSTRUCT_ORG").
              setReadOnly().
              setFunction("PROJECT_CONTRACT_API.Get_Secend_Side (:PRE_EXAM_NO,:CONTRACT_NO)").
              setLabel("PRECHECKRLTCONSTRUCTORG: Construct Org").
              setSize(20);
      headblk.addField("CONSTRUCT_ORG_DESC").
              setReadOnly().
              setFunction("GENERAL_ZONE_API.Get_Zone_Desc (PROJECT_CONTRACT_API.Get_Secend_Side (:PRE_EXAM_NO,:CONTRACT_NO))").
              setLabel("PRECHECKRLTCONSTRUCTORGDESC: Construct Org Desc").
              setSize(30);
      headblk.addField("STD_SCORE","Number").
              setInsertable().
              setLabel("PRECHECKRLTSTDSCORE: Std Score").
              setSize(20);
      headblk.addField("ACTUAL_SCORE","Number").
              setInsertable().
              setLabel("PRECHECKRLTACTUALSCORE: Actual Score").
              setSize(20);
      headblk.addField("CHECK_STYLE_NO").
              setInsertable().
              setWfProperties().
              setDynamicLOV("CHECK_STYLE","PROJECT_TYPE_NO").
              setLabel("PRECHECKRLTCHECKSTYLENO: Check Style No").
              setSize(20);
      headblk.addField("CHECK_STYLE_NAME").  
              setReadOnly().
              setWfProperties().
              setFunction("CHECK_STYLE_API.Get_Check_Style_Name(:PROJECT_TYPE_NO,:CHECK_STYLE_NO)").
              setLabel("ETSTARTRESREGPERSONNAME: Check Style Name").        
              setSize(30);
      mgr.getASPField("CHECK_STYLE_NO").setValidation("CHECK_STYLE_NAME");
      headblk.addField("STD_NO").
              setInsertable().
              setDynamicLOV("ASSESS_STD_CLASSIFY","PROJECT_TYPE_NO").
              setLabel("PRECHECKRLTSTDNO: Std No").
              setSize(20);
      headblk.addField("STD_NAME").  
              setReadOnly().
              setFunction("ASSESS_STD_CLASSIFY_API.Get_Std_Desc(:PROJECT_TYPE_NO,:STD_NO)").
              setLabel("PRECHECKRLTSTDNAME: Std Name").        
              setSize(30);
      mgr.getASPField("STD_NO").setValidation("STD_NAME");
      headblk.addField("REG_PERSON").
              setInsertable().
              setDynamicLOV("PERSON_INFO_USER").
              setLabel("PRECHECKRLTREGPERSON: Reg Person").
              setSize(20);
      headblk.addField("REG_PERSON_NAME").
              setReadOnly().
              setFunction("PERSON_INFO_API.Get_Name(:REG_PERSON)").
              setLabel("PRECHECKRLTREGPERSONNAME: Reg Person Name").
              setSize(20);
      mgr.getASPField("REG_PERSON").setValidation("REG_PERSON_NAME");
      headblk.addField("CREATE_TIME","Date").
              setInsertable().
              setLabel("PRECHECKRLTCREATETIME: Create Time").
              setSize(20);
      headblk.addField("NOTE").
              setInsertable().
              setHeight(5).
              setLabel("PRECHECKRLTNOTE: Note").
              setSize(150);
      headblk.setView("PRECHECK_RLT");
      headblk.defineCommand("PRECHECK_RLT_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("PRECHECKRLTTBLHEAD: Precheck Rlts");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setSimple("PROJ_NAME");
//      headlay.setSimple("PROJECT_TYPE_NAME");
      headlay.setSimple("CONTRACT_DESC");
      headlay.setSimple("CONSTRUCT_ORG_DESC");      
      headlay.setSimple("REG_PERSON_NAME");
      headlay.setSimple("CHECK_STYLE_NAME");
      headlay.setSimple("STD_NAME");
      headlay.setDataSpan("NOTE", 5);

      headbar.addCustomCommand("findPreStdTemp",mgr.translate("FINDPRESTDTEMP: Find Std Temp..."));

      precheck_rlt_line_blk = mgr.newASPBlock("ITEM1");
      precheck_rlt_line_blk.addField("ITEM0_OBJID").
                            setHidden().
                            setDbName("OBJID");
      precheck_rlt_line_blk.addField("ITEM0_OBJVERSION").
                            setHidden().
                            setDbName("OBJVERSION");
      precheck_rlt_line_blk.addField("ITEM0_ID").
                            setDbName("ID").
                            setHidden().
                            setMandatory().
                            setInsertable().
                            setLabel("PRECHECKRLTLINEITEM0ID: Id").
                            setSize(50);
      precheck_rlt_line_blk.addField("LINE_NO").
                            setMandatory().
                            setReadOnly().
                            setLabel("PRECHECKRLTLINELINENO: Line No").
                            setSize(50);
      precheck_rlt_line_blk.addField("ASSESS_CONTENT").
                            setInsertable().
                            setHeight(3).
                            setLabel("PRECHECKRLTLINEASSESSCONTENT: Assess Content").
                            setSize(50);
      precheck_rlt_line_blk.addField("RANGE").
                            setInsertable().
                            setHeight(3).
                            setLabel("PRECHECKRLTLINERANGE: Range").
                            setSize(50);
      precheck_rlt_line_blk.addField("ORG").
                            setInsertable().
                            setLabel("PRECHECKRLTLINEORG: Org").
                            setSize(50);
      precheck_rlt_line_blk.addField("PRE_STD_SCORE","Number").
                            setInsertable().
                            setLabel("PRECHECKRLTLINEPRESTDSCORE: Pre Std Score").
                            setSize(50);
      precheck_rlt_line_blk.addField("DEDUCTION","Number").
                            setInsertable().
                            setLabel("PRECHECKRLTLINEDEDUCTION: Deduction").
                            setSize(50);
      precheck_rlt_line_blk.addField("ACTUAL_COMPLETE","Number").
                            setInsertable().
                            setLabel("PRECHECKRLTLINEACTUALCOMPLETE: Actual Complete").
                            setSize(50);
      precheck_rlt_line_blk.addField("ACTUAL_DEDUCTION","Number").
                            setInsertable().
                            setLabel("PRECHECKRLTLINEACTUALDEDUCTION: Actual Deduction").
                            setSize(50);
      precheck_rlt_line_blk.addField("ITEM0_NOTE").
                            setDbName("NOTE").
                            setHeight(5).
                            setInsertable().
                            setLabel("PRECHECKRLTLINEITEM0NOTE: Note").
                            setSize(140);
      precheck_rlt_line_blk.setView("PRECHECK_RLT_LINE");
      precheck_rlt_line_blk.defineCommand("PRECHECK_RLT_LINE_API","New__,Modify__,Remove__");
      precheck_rlt_line_blk.setMasterBlock(headblk);
      precheck_rlt_line_set = precheck_rlt_line_blk.getASPRowSet();
      precheck_rlt_line_bar = mgr.newASPCommandBar(precheck_rlt_line_blk);
      precheck_rlt_line_bar.defineCommand(precheck_rlt_line_bar.OKFIND, "okFindITEM1");
      precheck_rlt_line_bar.defineCommand(precheck_rlt_line_bar.NEWROW, "newRowITEM1");
      precheck_rlt_line_tbl = mgr.newASPTable(precheck_rlt_line_blk);
      precheck_rlt_line_tbl.setTitle("PRECHECKRLTLINEITEMHEAD1: PrecheckRltLine");
      precheck_rlt_line_tbl.enableRowSelect();
      precheck_rlt_line_tbl.setWrap();
      precheck_rlt_line_lay = precheck_rlt_line_blk.getASPBlockLayout();
      precheck_rlt_line_lay.setDefaultLayoutMode(precheck_rlt_line_lay.MULTIROW_LAYOUT);
      precheck_rlt_line_lay.setDataSpan("LINE_NO",5);
      precheck_rlt_line_lay.setDataSpan("ACTUAL_DEDUCTION",5);
      precheck_rlt_line_lay.setDataSpan("ITEM0_NOTE", 5);

   }
   
   public void findPreStdTemp() throws FndException{

      ASPManager mgr = getASPManager();
      ASPCommand cmd = mgr.newASPCommand(); 
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      headset.storeSelections();  
      if (headlay.isSingleLayout())
         headset.selectRow();
      ASPBuffer selected_fields=headset.getSelectedRows("ID,PROJECT_TYPE_NO");
      proj_type = headset.getValue("PROJECT_TYPE_NO");
      Id = headset.getValue("ID");
        
      callNewWindow("ReachStdTemp.page", selected_fields);
   }
         
   private void callNewWindow(String transfer_page, ASPBuffer buff) throws FndException 
   {
       String id;
       ASPManager mgr = getASPManager();

        ASPContext ctx = mgr.getASPContext();
        ctx.setGlobal("PROJECT_TYPE_NO",proj_type);
        ctx.setGlobal("ID",Id);
        id=buff.getBufferAt(0).getValueAt(0);
        proj_type = buff.getBufferAt(0).getValueAt(1);

      String url = transfer_page+"?PROJECT_TYPE_NO="+proj_type+"&ID="+id+"&TYPE=2";
      appendDirtyJavaScript("showNewBrowser_('"+ url + "', 550, 550, 'YES'); \n");
   }



   public void  adjust()
   {
      // fill function body
      precheck_rlt_line_bar.removeCustomCommand(precheck_rlt_line_bar.NEWROW);
      try {
         super.adjust();
      } catch (FndException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "PRECHECKRLTDESC: Precheck Rlt";
   }


   protected String getTitle()
   {
      return "PRECHECKRLTTITLE: Precheck Rlt";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      printHiddenField("REFRESH_PARENT", "FALSE");
      if (headlay.isVisible())
          appendToHTML(headlay.show());
      else
      {
         headlay.setLayoutMode(headlay.CUSTOM_LAYOUT);
         appendToHTML(headlay.show());
      }
      
      if ((headlay.isSingleLayout() || headlay.isCustomLayout())
            && headset.countRows() > 0) {
      if (precheck_rlt_line_lay.isVisible())
          appendToHTML(precheck_rlt_line_lay.show());
      }
      
      appendDirtyJavaScript("function refreshParent()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" document.form.REFRESH_PARENT.value=\"TRUE\"\n");
      appendDirtyJavaScript(" submit() \n");
      appendDirtyJavaScript("}\n");
   }

   @Override
   protected ASPBlock getBizWfBlock() {
      // TODO Auto-generated method stub
      return headblk;
   }

}
