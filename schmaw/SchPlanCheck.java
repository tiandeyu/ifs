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

package ifs.schmaw;
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

public class SchPlanCheck extends HzASPPageProviderWf
{

   //-----------------------------------------------------------------------------
   //---------- Static constants ------------------------------------------------
   //-----------------------------------------------------------------------------

   public static boolean DEBUG = Util.isDebugEnabled("ifs.schmaw.SchPlanCheck");

   //-----------------------------------------------------------------------------
   //---------- Header Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   
   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPQuery q;
   private boolean bEmptyTree;
   private String src_str;
   private String iframe_src;
   private String eps_no;
   private String proj_no;
   private String comnd;

   //-----------------------------------------------------------------------------
   //------------------------  Construction  ---------------------------
   //-----------------------------------------------------------------------------

   public  SchPlanCheck (ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      super.run();

      bEmptyTree = false;
      if( mgr.commandBarActivated() ){
         comnd = mgr.readValue("__COMMAND");         
         if  ( "HEAD.Find".equals(comnd) )
            bEmptyTree = true;   // Clear tree when in the find layout
         eval(mgr.commandBarFunction());
      }
      else if(mgr.dataTransfered())
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("CHECK_ID")) )
         okFind();
      else if( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
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
         mgr.showAlert("SCHPLANCHECKNODATA: No data found.");
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

      cmd = trans.addEmptyCommand("HEAD","SCH_PLAN_CHECK_API.New__",headblk);
      cmd.setParameter("REV", "A");
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }

    public void validate() {
    // TODO Auto-generated method stub
    ASPManager mgr = getASPManager();
    ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
    ASPCommand cmd;
    ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();
    ASPCommand cmd1;
    String val = mgr.readValue("VALIDATE");
    String txt = "";
    String EPSNAME = "";
    String RESPONSEORG = "";
    String RESPONSEORGNAME = "";
   
    
    if("ID".equals(val)) {
       cmd = trans.addCustomFunction("GETEPSNAME", 
             "SCH_EPS_API.Get_Eps_Name", "EPS_NAME");
       cmd.addParameter("PROJ_NO,ID,REV");
       
       cmd = trans.addCustomFunction("GETRESPONSEORG", 
             "SCH_EPS_API.Get_Response_Org", "RESPONSE_ORG");
       cmd.addParameter("PROJ_NO,ID,REV");
       trans = mgr.validate(trans);   
       EPSNAME = trans.getValue("GETEPSNAME/DATA/EPS_NAME");
       RESPONSEORG = trans.getValue("GETRESPONSEORG/DATA/RESPONSE_ORG");
       
       
       cmd1 = trans1.addCustomFunction("GETRESPONSEORGNAME", 
             "GENERAL_ZONE_API.Get_Zone_Desc", "RESPONSE_ORG_NAME");
       cmd1.addParameter("RESPONSE_ORG",RESPONSEORG);
       trans1 = mgr.validate(trans1);
       RESPONSEORGNAME = trans1.getValue("GETRESPONSEORGNAME/DATA/RESPONSE_ORG_NAME");
   
       txt = ((mgr.isEmpty(EPSNAME)) ? "" : EPSNAME ) + "^" 
             + ((mgr.isEmpty(RESPONSEORG)) ? "" : RESPONSEORG ) + "^"
             + ((mgr.isEmpty(RESPONSEORGNAME)) ? "" : RESPONSEORGNAME ) + "^";
       
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
      headblk.addField("CHECK_ID").
              setReadOnly().
              setLabel("SCHPLANCHECKCHECKID: Check Id").
              setSize(30);
      
      headblk.addField("PROJ_NO").
              setMandatory().
              setInsertable().
              setDynamicLOV("GENERAL_PROJECT").
              setLabel("SCHPLANCHECKPROJNO: Proj No").
              setSize(30);
      headblk.addField("GENERAL_PROJECT_PROJ_DESC").
              setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC ( :PROJ_NO)").
              setLabel("SCHPLANCHECKGENERALPROJECTPROJDESC: General Project Proj Desc").
              setSize(30).
              setReadOnly();
      mgr.getASPField("PROJ_NO").setValidation("GENERAL_PROJECT_PROJ_DESC");
      
      headblk.addField("ID").
              setInsertable().
//              setWfProperties().
              setDynamicLOV("SCH_PLAN_CHECK_EPS","PROJ_NO").
              setLabel("SCHPLANCHECKID: Eps No").
              setSize(30).
              setCustomValidation("PROJ_NO,ID,REV", "EPS_NAME,RESPONSE_ORG,RESPONSE_ORG_NAME");
      headblk.addField("EPS_NAME").
              setFunction("SCH_EPS_API.Get_Eps_Name ( :PROJ_NO, :ID, :REV)").
              setLabel("SCHPLANCHECKEPSNAME: Eps Name").
              setSize(30).
              setReadOnly();
      headblk.addField("RESPONSE_ORG").
              setFunction("SCH_EPS_API.Get_Response_Org (:PROJ_NO,:ID,:REV)").
              setLabel("SCHPLANCHECKRESPONSEORG: Response Org").
              setSize(30).
              setReadOnly();
      headblk.addField("RESPONSE_ORG_NAME").
              setFunction("GENERAL_ZONE_API.Get_Zone_Desc (SCH_EPS_API.Get_Response_Org (:PROJ_NO,:ID,:REV))").
              setLabel("SCHPLANCHECKRESPONSEORGNAME: Response Org Name").
              setSize(30).
              setReadOnly();
      
      headblk.addField("STATUS").
              setLabel("SCHPLANCHECKSTATUS: Status").
              setSize(30).
              setHidden();
      headblk.addField("STATUS_DESC").
              setFunction("FLOW_STATUS_API.Get_Status_Desc (:STATUS)").
              setLabel("SCHPLANCHECKSTATUSDESC: Status Desc").
              setSize(30).
              setReadOnly();
      
      headblk.addField("REV").
              setMandatory().
              setLabel("SCHPLANCHECKREV: Rev").
              setSize(30).
              setReadOnly();
      
      headblk.addField("CREATE_PERSON").
              setInsertable().
              setDynamicLOV("PERSON_INFO").
              setLabel("SCHPLANCHECKCREATEPERSON: Create Person").
              setSize(30);
      headblk.addField("CREATE_PERSON_NAME").
            setFunction("PERSON_INFO_API.GET_NAME ( :CREATE_PERSON)").
            setLabel("SCHPLANCHECKCREATEPERSONNAME: Create Person Name").
            setSize(30).
            setReadOnly();
      mgr.getASPField("CREATE_PERSON").setValidation("CREATE_PERSON_NAME");
      
      headblk.addField("CREATE_TIME","Date").
              setInsertable().
              setLabel("SCHPLANCHECKCREATETIME: Create Time").
              setSize(30);
      
      headblk.addField("NOTE").
              setInsertable().
              setLabel("SCHPLANCHECKNOTE: Note").
              setSize(120).
              setHeight(5);
      
      headblk.addField("FLOW_TITLE").
      setWfProperties().
      setReadOnly().
      setHidden().
      setFunction("SCH_EPS_API.Get_Eps_Name ( :PROJ_NO, :ID, :REV)").
      setLabel("FLOWTITLE: Flow Title").
      setSize(30);
     
      headblk.setView("SCH_PLAN_CHECK");
      headblk.defineCommand("SCH_PLAN_CHECK_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("SCHPLANCHECKTBLHEAD: Sch Plan Checks");
      headtbl.enableRowSelect();
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      
      headlay.setSimple("GENERAL_PROJECT_PROJ_DESC");
      headlay.setSimple("EPS_NAME");
      headlay.setSimple("RESPONSE_ORG_NAME");
      headlay.setSimple("CREATE_PERSON_NAME");
      headlay.setDataSpan("NOTE", 5);
   }



   public void  adjust()
   {
      // fill function body
      if (( bEmptyTree )|| ( headset.countRows() == 0 ))
      {
         src_str    = "SchPlanCheckTree.page";
         iframe_src = "SchPlanCheckWork.page";
      }
      else        
      {    
         eps_no    = headset.getValue("ID");   
         proj_no = headset.getValue("PROJ_NO");   
         src_str    = "SchPlanCheckTree.page?ID="+eps_no+"&PROJ_NO=" + proj_no ;
         iframe_src = "SchPlanCheckWbsLine.page?ID="+eps_no+"&PROJ_NO=" + proj_no;
      }
      
      ASPManager mgr = getASPManager();
      if("MAIN.NewRow".equals(mgr.readValue("__COMMAND"))){
         mgr.getASPField("CHECK_ID").setHidden();
         headlay.setDataSpan("REV", 5);
      }
      
      //WorkFlow
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
      return "SCHPLANCHECKDESC: Sch Plan Check";
   }


   protected String getTitle()
   {
      return "SCHPLANCHECKTITLE: Sch Plan Check";
   }
   
   
   public String  modifiedClientScript(ASPManager mgr,String sGenerateClientScript)
   {
      sGenerateClientScript = mgr.replace(sGenerateClientScript,"document.location=","window.parent.location=");
      return sGenerateClientScript;
   }


   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>");
      out.append(mgr.generateHeadTag("SCHPLANCHECKTITLE: Sch Plan Check"));
      out.append("</head>\n");
      out.append("<body ");      
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append("<input type=\"hidden\" name=\"CONFIRMB\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"ACTIVITYCOM\" value=\"\">\n");
      out.append("<input type=\"hidden\" name=\"PROGRESSCOM\" value=\"\">\n");
      out.append(mgr.startPresentation("SCHPLANCHECKTITLE: Sch Plan Check"));
      out.append(headlay.show());  
      if (headlay.isSingleLayout() && headset.countRows() > 0)    
      { 
//            out.append("<frameset cols=\"30%,*\">");
//            out.append("<frame name=\"contents\" target=\"ChildMain\" src=\" " );
//            out.append(src_str);  
//            out.append("\"> </frame>\n");      
//            out.append("<frame name=\"ChildMain\" src=\"" );
//            out.append(iframe_src);   
//            out.append("\"> </frame>\n");      
//            out.append("</frameset>\n");
          out.append("<iframe name=\"contents\" target=\"ChildMain\" src=");
          out.append(src_str);
          out.append(" width=\"20%\" scrolling=\"auto\" height = \"100%\" scrolling=\"auto\" marginwidth=\"0\" marginheight=\"0\">\n");
          out.append("</iframe>\n");  
          out.append("<iframe name =\"ChildMain\" src=");
          out.append(iframe_src);        
          out.append(" width=\"78%\" scrolling=\"auto\" height=\"100%\" scrolling=\"auto\" marginwidth=\"0\" marginheight=\"0\">\n");
          out.append("</iframe>\n");                            
      }       
      out.append(modifiedClientScript(mgr,mgr.endPresentation()));
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>\n");
      return out;

   }
   
 //--------------------------  Added in new template  --------------------------
   //--------------  Return blk connected with workflow functions  ---------------
   //-----------------------------------------------------------------------------

   protected ASPBlock getBizWfBlock()
   {
      return headblk;      
   }
}
