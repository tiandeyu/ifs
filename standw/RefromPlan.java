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

public class RefromPlan extends HzASPPageProviderWf
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.standw.RefromPlan");

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

   private ASPBlock refrom_plan_line_blk;
   private ASPRowSet refrom_plan_line_set;
   private ASPCommandBar refrom_plan_line_bar;
   private ASPTable refrom_plan_line_tbl;
   private ASPBlockLayout refrom_plan_line_lay;


   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  RefromPlan (ASPManager mgr, String page_path)
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
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("REFORM_PLAN_NO")) )
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
         mgr.showAlert("REFROMPLANNODATA: No data found.");
         headset.clear();
      }
      eval( refrom_plan_line_set.syncItemSets() );
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

      cmd = trans.addEmptyCommand("HEAD","REFROM_PLAN_API.New__",headblk);
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

      q = trans.addQuery(refrom_plan_line_blk);
      q.addWhereCondition("PROJ_NO = ? AND REFORM_PLAN_NO = ?");
      q.addParameter("PROJ_NO", headset.getValue("PROJ_NO"));
      q.addParameter("REFORM_PLAN_NO", headset.getValue("REFORM_PLAN_NO"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,refrom_plan_line_blk);
      headset.goTo(headrowno);
   }
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      
      cmd = trans.addEmptyCommand("ITEM1","REFROM_PLAN_LINE_API.New__",refrom_plan_line_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_PROJ_NO", headset.getValue("PROJ_NO"));
      cmd.setParameter("ITEM0_REFORM_PLAN_NO", headset.getValue("REFORM_PLAN_NO"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      refrom_plan_line_set.addRow(data);
   }
   
   public void validate()
   {
       ASPManager mgr = getASPManager();
       ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
       ASPCommand cmd;
       String val = mgr.readValue("VALIDATE"); 
       String txt = "";
       String projDesc = "";
       String projType = "";
       String projTypeDesc = "";
           
       if("PROJ_NO".equals(val)) {
          cmd = trans.addCustomFunction("PROJDESC", 
                "GENERAL_PROJECT_API.GET_PROJ_DESC", "PROJ_NAME");
          cmd.addParameter("PROJ_NO");
          
          cmd = trans.addCustomFunction("PROJTYPE", 
                "GENERAL_PROJECT_API.Get_Project_Type_Id", "PROJECT_TYPE_NO");
          cmd.addParameter("PROJ_NO");         
          
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
      headblk.addField("REFORM_PLAN_NO").
              setMandatory().
              setReadOnly().
              setLabel("REFROMPLANREFORMPLANNO: Reform Plan No").
              setSize(50);
      headblk.addField("PROJ_NO").
              setMandatory().
              setDynamicLOV("GENERAL_PROJECT").
              setCustomValidation("PROJ_NO", "PROJ_NAME,PROJECT_TYPE_NO,PROJECT_TYPE_NAME").
              setInsertable().
              setLabel("REFROMPLANPROJNO: Proj No").
              setSize(20);
      headblk.addField("PROJ_NAME").
              setReadOnly().
              setFunction("GENERAL_PROJECT_API.Get_Proj_Desc(:PROJ_NO)").
              setLabel("REFROMPLANPROJNAME: Proj Name").
              setSize(30);
      headblk.addField("PROJECT_TYPE_NO").
              setInsertable().
              setHidden().
              setLabel("REFROMPLANPROJECTTYPENO: Project Type No").
              setSize(20);
     headblk.addField("PROJECT_TYPE_NAME").
              setReadOnly().
              setFunction("PROJECT_TYPE_API.Get_Project_Type_Name(GENERAL_PROJECT_API.Get_Project_Type_Id ( :PROJ_NO))").
              setLabel("REFROMPLANPROJECTTYPENAME: Project Type Name").
              setSize(30);
//     mgr.getASPField("PROJECT_TYPE_NO").setValidation("PROJECT_TYPE_NAME");
      headblk.addField("GROUP_NO").
              setInsertable().
              setWfProperties().
              setDynamicLOV("PROFESSION_GROUP","PROJECT_TYPE_NO").
              setLabel("REFROMPLANGROUPNO: Group No").
              setSize(20);
      headblk.addField("GROUP_NAME").
              setReadOnly().
              setWfProperties().
              setFunction("PROFESSION_GROUP_API.Get_Group_Desc(:PROJECT_TYPE_NO,:GROUP_NO)").
              setLabel("REFROMPLANGROUPNAME: Group Name").
              setSize(30);
      mgr.getASPField("GROUP_NO").setValidation("GROUP_NAME");
      headblk.addField("REFORM_ORG_NAME").
              setInsertable().
              setDynamicLOV("GENERAL_ORGANIZATION_LOV","PROJ_NO").
              setLabel("REFROMPLANREFORMORGNAME: Reform Org Name").
              setSize(20);
      headblk.addField("REFORM_ORG_NAME_LABEL").
              setReadOnly().
              setLabel("REFROMPLANREFORMORGNAMELABEL: Reform Org Name Label").
              setFunction("GENERAL_ORGANIZATION_API.Get_Org_Desc(:REFORM_ORG_NAME)").
              setSize(20);
      mgr.getASPField("REFORM_ORG_NAME").setValidation("REFORM_ORG_NAME_LABEL");
      headblk.addField("REFORM_CONTENT").
              setInsertable().
              setHeight(3).
              setLabel("REFROMPLANREFORMCONTENT: Reform Content").
              setSize(150);
      headblk.addField("NOTE").
              setInsertable().
              setHeight(3).
              setLabel("REFROMPLANNOTE: Note").
              setSize(150);
      headblk.addField("COMPILE_MAN").
              setInsertable().
              setDynamicLOV("PERSON_INFO_USER").
              setLabel("REFROMPLANCOMPILEMAN: Compile Man").
              setSize(15);
      headblk.addField("COMPILE_MAN_NAME").
              setReadOnly().
              setFunction("PERSON_INFO_API.Get_Name(:COMPILE_MAN)").
              setLabel("REFROMPLANCOMPILEMANNAME: Compile Man Name").
              setSize(20);
      mgr.getASPField("COMPILE_MAN").setValidation("COMPILE_MAN_NAME");
      headblk.addField("APPROVAL_MAN").
              setInsertable().
              setDynamicLOV("PERSON_INFO_USER").
              setLabel("REFROMPLANAPPROVALMAN: Approval Man").
              setSize(15);
      headblk.addField("APPROVAL_MAN_NAME").
              setReadOnly().
              setFunction("PERSON_INFO_API.Get_Name(:APPROVAL_MAN)").
              setLabel("REFROMPLANAPPROVALMANNAME: Approval Man Name").
              setSize(20);
      mgr.getASPField("APPROVAL_MAN").setValidation("APPROVAL_MAN_NAME");
      headblk.setView("REFROM_PLAN");
      headblk.defineCommand("REFROM_PLAN_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("REFROMPLANTBLHEAD: Refrom Plans");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setSimple("PROJ_NAME");
//      headlay.setSimple("PROJECT_TYPE_NAME");
      headlay.setSimple("GROUP_NAME");
      headlay.setSimple("REFORM_ORG_NAME_LABEL");
      headlay.setSimple("COMPILE_MAN_NAME");
      headlay.setSimple("APPROVAL_MAN_NAME");
      headlay.setDataSpan("REFORM_ORG_NAME", 5);
      headlay.setDataSpan("REFORM_CONTENT", 5);
      headlay.setDataSpan("NOTE", 5);

      refrom_plan_line_blk = mgr.newASPBlock("ITEM1");
      refrom_plan_line_blk.addField("ITEM0_OBJID").
                           setHidden().
                           setDbName("OBJID");
      refrom_plan_line_blk.addField("ITEM0_OBJVERSION").
                           setHidden().
                           setDbName("OBJVERSION");
      refrom_plan_line_blk.addField("ITEM0_PROJ_NO").
                           setDbName("PROJ_NO").
                           setMandatory().
                           setHidden().
                           setInsertable().
                           setLabel("REFROMPLANLINEITEM0PROJNO: Proj No").
                           setSize(50);
      refrom_plan_line_blk.addField("ITEM0_REFORM_PLAN_NO").
                           setDbName("REFORM_PLAN_NO").
                           setMandatory().
                           setHidden().
                           setReadOnly().
                           setLabel("REFROMPLANLINEITEM0REFORMPLANNO: Reform Plan No").
                           setSize(50);
      refrom_plan_line_blk.addField("LINE_NO").
                           setMandatory().
                           setReadOnly().
                           setLabel("REFROMPLANLINELINENO: Line No").
                           setSize(50);
      refrom_plan_line_blk.addField("PLAN_START_TIME","Date").
                           setInsertable().
                           setLabel("REFROMPLANLINEPLANSTARTTIME: Plan Start Time").
                           setSize(20);
      refrom_plan_line_blk.addField("PLAN_END_TIME","Date").
                           setInsertable().
                           setLabel("REFROMPLANLINEPLANENDTIME: Plan End Time").
                           setSize(20);
      refrom_plan_line_blk.addField("RESPON_PERSON").
                           setInsertable().
                           setDynamicLOV("PERSON_INFO_USER").
                           setLabel("REFROMPLANLINERESPONPERSON: Respon Person").
                           setSize(20);
      refrom_plan_line_blk.addField("RESPON_PERSON_NAME").
                           setReadOnly().
                           setFunction("PERSON_INFO_API.Get_Name(:RESPON_PERSON)").
                           setLabel("REFROMPLANLINERESPONPERSONNAME: Respon Person Name").
                           setSize(20);
      mgr.getASPField("RESPON_PERSON").setValidation("RESPON_PERSON_NAME");
      refrom_plan_line_blk.addField("REFORM_MEASURE").
                           setInsertable().
                           setHeight(4).
                           setLabel("REFROMPLANLINEREFORMMEASURE: Reform Measure").
                           setSize(150);
      refrom_plan_line_blk.addField("ITEM0_NOTE").
                           setDbName("NOTE").
                           setInsertable().
                           setHeight(4).
                           setLabel("REFROMPLANLINEITEM0NOTE: Note").
                           setSize(150);
      refrom_plan_line_blk.setView("REFROM_PLAN_LINE");
      refrom_plan_line_blk.defineCommand("REFROM_PLAN_LINE_API","New__,Modify__,Remove__");
      refrom_plan_line_blk.setMasterBlock(headblk);
      refrom_plan_line_set = refrom_plan_line_blk.getASPRowSet();
      refrom_plan_line_bar = mgr.newASPCommandBar(refrom_plan_line_blk);
      refrom_plan_line_bar.defineCommand(refrom_plan_line_bar.OKFIND, "okFindITEM1");
      refrom_plan_line_bar.defineCommand(refrom_plan_line_bar.NEWROW, "newRowITEM1");
      refrom_plan_line_tbl = mgr.newASPTable(refrom_plan_line_blk);
      refrom_plan_line_tbl.setTitle("REFROMPLANLINEITEMHEAD1: RefromPlanLine");
      refrom_plan_line_tbl.enableRowSelect();
      refrom_plan_line_tbl.setWrap();
      refrom_plan_line_lay = refrom_plan_line_blk.getASPBlockLayout();
      refrom_plan_line_lay.setDefaultLayoutMode(refrom_plan_line_lay.MULTIROW_LAYOUT);
      refrom_plan_line_lay.setSimple("RESPON_PERSON_NAME");
      refrom_plan_line_lay.setDataSpan("LINE_NO", 5);
      refrom_plan_line_lay.setDataSpan("RESPON_PERSON", 5);
      refrom_plan_line_lay.setDataSpan("REFORM_MEASURE", 5);
      refrom_plan_line_lay.setDataSpan("ITEM0_NOTE", 5);
   
   }



   public void  adjust()
   {
      // fill function body
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
      return "REFROMPLANDESC: Refrom Plan";
   }


   protected String getTitle()
   {
      return "REFROMPLANTITLE: Refrom Plan";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      if (headlay.isVisible())
          appendToHTML(headlay.show());
      else 
      {
         headlay.setLayoutMode(headlay.CUSTOM_LAYOUT);
         appendToHTML(headlay.show());
      }
         
      if (refrom_plan_line_lay.isVisible())
          appendToHTML(refrom_plan_line_lay.show());

   }

   @Override
   protected ASPBlock getBizWfBlock() {
      // TODO Auto-generated method stub
      return headblk;
   }
}
