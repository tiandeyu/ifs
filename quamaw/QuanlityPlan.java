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

import java.io.UnsupportedEncodingException;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import ifs.hzwflw.HzASPPageProviderWf;

//-----------------------------------------------------------------------------
//-----------------------------   Class def  ------------------------------
//-----------------------------------------------------------------------------

public class QuanlityPlan extends HzASPPageProviderWf
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.quamaw.QuanlityPlan");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   
   private String src_str;  
   private boolean bEmptyTree;
   private String comnd;
   private String plan_no;
   private String project_no;
   private String standard_info;
   private String is_refresh="F";

   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  QuanlityPlan (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() throws FndException
   {
      super.run();  
      ASPManager mgr = getASPManager();
      bEmptyTree = false;

      if( mgr.commandBarActivated() ){
         comnd = mgr.readValue("__COMMAND");         
         if  ( "HEAD.Find".equals(comnd) )
            bEmptyTree = true; 
         eval(mgr.commandBarFunction());
      }
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();  
      else if( !mgr.isEmpty(mgr.getQueryStringValue("PLAN_NO")) )
         okFind();
     else if ("TRUE".equals(mgr.readValue("REFRESH_PARENT")))
        performHEAD();
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
         mgr.showAlert("QUANLITYPLANNODATA: No data found.");
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
      
      cmd = trans.addEmptyCommand("HEAD","QUANLITY_PLAN_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }


   //-----------------------------------------------------------------------------
   //------------------------  Perform Header and Item functions  ---------------------------
   //-----------------------------------------------------------------------------


   public void  performHEAD( String command)
   {
      int currow;
      
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
     
         currow = headset.getCurrentRowNo();
         if(headlay.isMultirowLayout())
            headset.storeSelections();
         else
            headset.selectRow();
         headset.markSelectedRows( command );
         mgr.submit(trans);
         headset.goTo(currow);
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
      headblk.addField("OBJEVENTS").
              setHidden();
      
      headblk.addField("PROJ_NO").
              setMandatory().
              setDynamicLOV("GENERAL_PROJECT").
              setInsertable().
              setLabel("QUANLITYPLANPROJNO: Proj No").             
              setSize(20);
      headblk.addField("GENERAL_PROJECT_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC ( :PROJ_NO)").
              setLabel("QUANLITYPLANGENERALPROJECTDESC: General Project Desc").
              setReadOnly().
              setSize(25);
      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_DESC");  
      headblk.addField("UNIT").
              setInsertable().
              setMandatory().
              setDynamicLOV("GENERAL_MACH_GROUP").
              setLabel("QUANLITYPLANUNIT: Unit").
              setSize(30);
      headblk.addField("UNIT_DESC").
              setReadOnly().
              setFunction("GENERAL_MACH_GROUP_API.Get_Mach_Grp_Desc (:UNIT)").
              setLabel("QUANLITYPLANUNITDESC: Unit Desc").
              setSize(30);
      mgr.getASPField("UNIT").setValidation("UNIT_DESC");
      headblk.addField("CONTRACT_NO").
              setInsertable().
              setMandatory().
              setDynamicLOV("PROJECT_CONTRACT_LOV","PROJ_NO").
              //setLOVProperty("TREE_PARE_FIELD", "PRE_CONTRACT_NO").
              //setLOVProperty("TREE_DISP_FIELD", "CONTRACT_ID,CONTRACT_DESC").
              setLabel("QUANLITYPLANCONTRACTNO: Contract No").
              setSize(30);
      headblk.addField("CONTRACT_DESC").
              setReadOnly().
              setFunction("PROJECT_CONTRACT_API.Get_Contract_Desc (:PROJ_NO,:CONTRACT_NO)").
              setLabel("QUANLITYPLANCONTRACTDESC: Contract Desc").
              setSize(50);
      mgr.getASPField("CONTRACT_NO").setValidation("CONTRACT_DESC");     
      headblk.addField("SPECIALTY_NO").
              setInsertable().
              setSelectBox().
              enumerateValues("QUA_SPECIALTY_RECEIPT_API").
              setLabel("QUANLITYPLANSPECIALTYNO: Specialty No").
              setSize(30);      
      headblk.addField("PROJECT_CRITERION").
              setInsertable().
              setLabel("QUANLITYPLANPROJECTCRITERION: Project Criterion").
              setSize(30);
      headblk.addField("PROJECT_TYPE_NO").
          setLabel("QUANLITYSTANDARDLINEPROJECTTYPENO: Project Type No").
          setFunction("GENERAL_PROJECT_API.GET_PROJECT_TYPE_ID ( :PROJ_NO)").
          setHidden().
          setSize(20);
      //mgr.getASPField("PROJ_NO").setValidation("PROJECT_TYPE_NO");
      
      headblk.addField("PLAN_NO").
              setHidden().
              setWfProperties().
              setInsertable().
              setLabel("QUANLITYPLANPLANNO: Plan No").
              setSize(50);
      headblk.addField("PLAN_NAME").
              setInsertable().
              setWfProperties().
              setLabel("QUANLITYPLANPLANNAME: Plan Name").
              setSize(50).
              setMaxLength(200);
//      headblk.addField("CREATE_PERSON_ID").
//              setHidden().
//              setReadOnly().
//              setLabel("QUANLITYPLANCREATEPERSONID: Create Person Id").
//              setSize(20);
//      headblk.addField("CREATE_PERSON_NAME").
//              setHidden().
//              setFunction("PERSON_INFO_API.GET_NAME ( :CREATE_PERSON_ID)").
//              setLabel("QUANLITYPLANCREATEPERSONNAME: Create Person Name").              
//              setReadOnly().
//              setSize(30);
//      mgr.getASPField("CREATE_PERSON_ID").setValidation("CREATE_PERSON_NAME");
     
      
      headblk.addField("CONSTRUCT_ORG").
              setInsertable().
              setLabel("QUANLITYPLANCONSTRUCTORG: Construct Org").     
              setSize(30).
              setDynamicLOV("GENERAL_ORGANIZATION");
      headblk.addField("CONSTRUCT_ORG_NAME").
              setFunction("GENERAL_ORGANIZATION_API.Get_Org_Desc(:CONSTRUCT_ORG)").
              setLabel("QUANLITYPLANCONSTRUCTORGGENERALORGANIZATION: Construct Org Name").
              setSize(30).
              setReadOnly();
      mgr.getASPField("CONSTRUCT_ORG").setValidation("CONSTRUCT_ORG_NAME");
      headblk.addField("CREATE_DATE","Date").
              setLabel("QUANLITYPLANCREATEDATE: Create Date").
              setSize(50).
              setReadOnly();
      headblk.addField("STATUS").
              setReadOnly().
              setHidden().
              setLabel("QUANLITYPLANSTATUS: Status").
              setSize(20);
      headblk.addField("STATUS_DESC").
              setFunction("FLOW_STATUS_API.Get_Status_Desc (:STATUS)").
              setLabel("QUANLITYPLANSTATUSDESC: Status Desc").
              setSize(30);
      headblk.setView("QUANLITY_PLAN");
      headblk.defineCommand("QUANLITY_PLAN_API","New__,Modify__,Remove__,FindTree__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("findTree",mgr.translate("QUANLITYPLANSTART: Find Tree Node..."));
      headbar.addCustomCommand("printReport", "QUANLITYPLANREPORT: Print Report...");
      headbar.addCustomCommand("printReport2", "QUANLITYPLANREPORTTWO: Print Report Two...");
      headbar.addCustomCommand("printReportElectrical", "QUANLITYPLANREPORTELECTRICAL: Print Report Electrical...");
      headbar.addCustomCommand("printReportWeld", "QUANLITYPLANREPORTWELD: Print Report Weld...");
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("QUANLITYPLANTBLHEAD: Quanlity Plans");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();       
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setSimple("GENERAL_PROJECT_DESC");
//      headlay.setSimple("CONTRACT_DESC");
//      headlay.setSimple("CREATE_PERSON_NAME");
      headlay.setSimple("CONSTRUCT_ORG_NAME");
      headlay.setSimple("UNIT_DESC");
 
   }

   public void findTree() throws FndException{
      String proj_type;
      ASPManager mgr = getASPManager();
      ASPCommand cmd = mgr.newASPCommand(); 
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      headset.storeSelections();  
      if (headlay.isSingleLayout())
         headset.selectRow();
      ASPBuffer selected_fields=headset.getSelectedRows("PROJECT_TYPE_NO,PROJ_NO,PLAN_NO");
      proj_type = headset.getValue("PROJECT_TYPE_NO");
      plan_no    = headset.getValue("PLAN_NO");
      project_no    = headset.getValue("PROJ_NO");        
      callNewWindow("QuaStandardFindTree.page", selected_fields);
   }
         
   private void callNewWindow(String transfer_page, ASPBuffer buff) throws FndException 
   {
       String id;
       ASPManager mgr = getASPManager();

        id=buff.getBufferAt(0).getValueAt(0);
        ASPContext ctx = mgr.getASPContext();
        ctx.setGlobal("PROJ_NO",project_no);
        ctx.setGlobal("PLAN_NO",plan_no);
        project_no = buff.getBufferAt(0).getValueAt(1);
        plan_no = buff.getBufferAt(0).getValueAt(2);

      String url = transfer_page+"?USERCOMMAND=submitTree&PROJ_NO="+project_no+"&PLAN_NO="+plan_no+"&PROJECT_TYPE_NO="+id;
//      String url = transfer_page+"?PROJ_NO="+project_no+"&PLAN_NO="+plan_no+"&PROJECT_TYPE_NO="+id;
      appendDirtyJavaScript("showNewBrowser_('"+ url + "', 550, 550, 'YES'); \n");
   }
   
   public void  adjust()
   {
      try {
         super.adjust();
      } catch (FndException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      // fill function body
      if (( bEmptyTree )|| ( headset.countRows() == 0 ))
      {
         src_str    = "QuanlityPlanTree.page";
      }
      else
      {
         plan_no    = headset.getValue("PLAN_NO");
         project_no    = headset.getValue("PROJ_NO");    
         src_str    = "QuanlityPlanTree.page?PLAN_NO="+plan_no+"&PROJ_NO="+project_no;
      }     
   }
   
   public void  printReport() throws FndException, UnsupportedEncodingException
   {
    ASPManager mgr = getASPManager();
    ASPConfig cfg = getASPConfig();
    String URL=cfg.getParameter("APPLICATION/RUNQIAN/SERVER_URL");
    if (headlay.isMultirowLayout())
       headset.goTo(headset.getRowSelected());
    if (headset.countRows()>0 )
          {   
             String proj_no = headset.getValue("PROJ_NO");
             String plan_no = headset.getValue("PLAN_NO");
              appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=RPTQuantityPlan.raq&proj_no="+proj_no+"&plan_no="+plan_no
                + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
         }
   }   
   public void  printReport2() throws FndException, UnsupportedEncodingException
   {
    ASPManager mgr = getASPManager();
    ASPConfig cfg = getASPConfig();
    String URL=cfg.getParameter("APPLICATION/RUNQIAN/SERVER_URL");
    if (headlay.isMultirowLayout())
       headset.goTo(headset.getRowSelected());
    if (headset.countRows()>0 )
          {   
             String proj_no = headset.getValue("PROJ_NO");
             String plan_no = headset.getValue("PLAN_NO");
              appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=RPTQuantityPlanLine.raq&proj_no="+proj_no+"&plan_no="+plan_no
                + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
         }
   }   
   public void  printReportElectrical() throws FndException, UnsupportedEncodingException
   {
    ASPManager mgr = getASPManager();
    ASPConfig cfg = getASPConfig();
    String URL=cfg.getParameter("APPLICATION/RUNQIAN/SERVER_URL");
    if (headlay.isMultirowLayout())
       headset.goTo(headset.getRowSelected());
    if (headset.countRows()>0 )
          {   
             String proj_no = headset.getValue("PROJ_NO");
             String plan_no = headset.getValue("PLAN_NO");
              appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=RPTQuantityPlanElectrical.raq&proj_no="+proj_no+"&plan_no="+plan_no
                + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
         }
   }   
   public void  printReportWeld() throws FndException, UnsupportedEncodingException
   {
    ASPManager mgr = getASPManager();
    ASPConfig cfg = getASPConfig();
    String URL=cfg.getParameter("APPLICATION/RUNQIAN/SERVER_URL");
    if (headlay.isMultirowLayout())
       headset.goTo(headset.getRowSelected());
    if (headset.countRows()>0 )
          {   
             String proj_no = headset.getValue("PROJ_NO");
             String plan_no = headset.getValue("PLAN_NO");
              appendDirtyJavaScript("window.open('"+URL+"/showReport.jsp?raq=RPTQuantityPlanWeld.raq&proj_no="+proj_no+"&plan_no="+plan_no
                + "','_blank','height=600, width=780, top=200, left=350, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no');");                                
         }
   }  
   public String  modifiedClientScript(ASPManager mgr,String sGenerateClientScript)
   {
      sGenerateClientScript = mgr.replace(sGenerateClientScript,"document.location=","window.parent.location=");
      return sGenerateClientScript;

   }   

   //-----------------------------------------------------------------------------
   //------------------------  Presentation functions  ---------------------------
   //-----------------------------------------------------------------------------

   protected String getDescription()
   {
      return "QUANLITYPLANDESC: Quanlity Plan";
   }


   protected String getTitle()
   {
      return "QUANLITYPLANTITLE: Quanlity Plan";
   }


   protected AutoString getContents() throws FndException
   {

      printHiddenField("REFRESH_PARENT", "FALSE");
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>");
      out.append(mgr.generateHeadTag("QUANLITYPLANTITLE: Quanlity Plan"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      
      appendDirtyJavaScript("function refreshParent()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" document.form.REFRESH_PARENT.value=\"TRUE\"\n");
      appendDirtyJavaScript(" submit() \n");
      appendDirtyJavaScript("}\n");
      
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append("<input type=\"hidden\" name=\"CONFIRMB\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"ACTIVITYCOM\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"PROGRESSCOM\" value=\"\">\n");
      out.append(mgr.startPresentation("QUANLITYPLANTITLE: Quanlity Plan"));
      out.append(headlay.show());
      if (headlay.isSingleLayout() && headset.countRows() > 0)
      { 
          out.append("<iframe name=\"contents\" target=\"ChildMain\" src=");
          out.append(src_str);
          out.append(" width=\"24%\" scrolling=\"yes\" height = \"100%\" scrolling=\"yes\" marginwidth=\"0\" marginheight=\"0\">\n");
          out.append("</iframe>\n");
          if("T".equals(is_refresh))
             out.append("<iframe name =\"ChildMain\" src=\"QuanlityPlanTree.page\" width=\"74%\" scrolling=\"auto\" height=\"100%\" scrolling=\"auto\" marginwidth=\"0\" marginheight=\"0\">\n");
          else
             out.append("<iframe name =\"ChildMain\" src=\"empty.htm\" width=\"74%\" scrolling=\"auto\" height=\"100%\" scrolling=\"auto\" marginwidth=\"0\" marginheight=\"0\">\n");
          out.append("</iframe>\n");
          
      }
      out.append(modifiedClientScript(mgr,mgr.endPresentation()));
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>\n");

      return out;
      
   }   
   public void  performHEAD()
   {
      int currow;
      is_refresh="T";
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      

      currow = headset.getCurrentRowNo();
      if(headlay.isMultirowLayout())
         headset.storeSelections();
      else
         headset.selectRow();
//      headset.markSelectedRows();
      mgr.submit(trans);
      headset.goTo(currow);
//      refreshActiveTab();
   }
   protected ASPBlock getBizWfBlock()
   {
      return headblk;
   }
}