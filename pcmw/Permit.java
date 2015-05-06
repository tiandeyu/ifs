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
 *  File        : Permit.java
 *  Created     : ASP2JAVA Tool  010307
 *  Modified    :
 *  JEWILK  010403  Changed file extensions from .asp to .page
 *  CHCRLK  010613  Modified overwritten validations.
 *  CHCRLK  010613  Removed unnecessary overwritten lov functions.
 *  CHCRLK  010806  Modified method printPermit().
 *  CHCRLK  010810  Modified method deleteRow().
 *  CHCRLK  010831  Modified method deleteRow(), again!.
 *  ARWILK  010904  Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction().
 *                  (Caused when cancelNew is performed after saveNew)
 *  CHCRLK  011009  Made some fields defaultNotVisible.69972
 *  INROLK  011010  added mgr.createSearchURL(headblk) to add to add link. call id 70397
 *  SAPRLK  011012  Added function checkObjAvaileble() and modified function adjust() to perform the security
 *                  check on the form.
 *  SAPRLK  011017  Modified functions adjust() and perform().Call Id : 70450
 *  INROLK  011019  Modified javaScript of LOV. call id 70449.
 *  VAGULK  011020  Corrected the problem with the Attributes Action(71129)
 *  BUNILK  020906  Bug ID 32182 Added a new method isModuleInst() to check availability of
 *                  DOCMAN module inside preDefine method.
 *  SHAFLK  021108  Bug Id 34064,Changed method printPermit.
 *  GACOLK  021204  Set Max Length of MCH_CODE to 100
 *  BUNILK  021211  Merged with 2002-3 SP3
 *  CHAMLK  031023  Modified function adjust() to display state actions correctly after page load.
 *  THWILK  031023  Call ID 109044, Modified the link to the LOV and added format mask to DELIMITATION_ORDER_NO.
 *  SAPRLK  031017  Web Alignment:replacing blocks with tabs, removing methods clone() and doReset() and removing block titles of the tabs.
 *  VAGULK  040103  Made field order according to the order in the Centura application(Web Alignment).
 *  SHAFLK  040120  Bug Id 41815,Removed Java dependencies.
 *  SAPRLK  040325  Merged with SP1.
 *  SAPRLK  040415  Web Alignment. Added multirow action, simplify code for RMBs, changed condition code in validate method,
 *                  Removed Unnecessary global variables.
 *  ARWILK  040722  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning)
 *  ARWILK  041111  Replaced getContents with printContents.
 *  NEKOLK  050608  AMUT 115: Isolation and permits. Added ITEM2. and RMB methods
 *  NEKOLk  050615  AMUT115:Modified Adjust.
 *  SHAFLK  050617  Bug 50830, Used mgr.translateJavaScript() method to get the correctly translated strings
 *  NIJALK  050617  Merged bug 50830.
 *  NEKOLK  050801  AMUT 115:Made changes.Added action permit data .
 *  NEKOLK  050803  AMUT 115: made cahnges to popup warning message.
 *  NEKOLK  050811  B 126182.Rename the RMB cancel to Cancelled
 *  NEKOLK  050816  B 126365. removed action prepareIsolationOrder From Multirow Action.
 *  NEKOLK  050825  B 126387.Modified CheckValidityOfPermit() and CheckValidityOfPermit2()
 *  NIJALK  050907  B126415: Added saveReturn().
 *  NEKOLK  050920  B127038 changed view of itemblk0 to "WO_ORDER_PERMIT".
 *  NIJALK  051007  B127673: Modified okFind().
 *  NIJALK  051007  B127696: Modified preDefine().
 *  NIJALK  051011  B127744: Modified function perform().
 *  NIJALK  051012  B127816: Added function modifyQrystr(). Modified createIsolationOrder(), createFromIsolationTemplate(),run().
 *  AMNILK  051012  Call Id: 127810.Modified perform() method.
 *  NIJALK  051024  Bug 127837: Modified preDefine().
 *  THWILK  060126  Corrected localization errors.
 *  NEKOLK  060315  Call 137294- modified fld ITEM2_DELIMITATION_ORDER_NO.
 *  NEKOLK  060315  Call 137371- Modified Adjust().
 *  RANFLK  060316  Call 137141 - Add Lov to status in Find mode
 *  NIJALK  060322  Renamed 'Work Master' to 'Executed By'.
 * ----------------------------------------------------------------------------
 *  CHODLK  060508  Bug 54569,Added bew RMB action reportInRoundWorkOrder. Modifie
 *  AMNILK  060629  Merged with SP1 APP7.
 *  AMDILK  060728  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding
 *  AMNILK  060807  Merged Bug ID: 58214.
*   AMNILK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*   AMNILK  060904  Merged Bug Id: 58216.
*   ASSALK  070424  Corrected APP_PATH tag to COOKIE_PATH.
*   AMNILK  070719  Eliminated XSS Security Vulnerability.
*   AMDILK  070731  Removed the scroll buttons of the parent when the child tab(Isoation) is in new or edit modes 
* ----------------------------------------------------------------------------
 */

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;


public class Permit extends ASPPageProvider
{
   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.Permit");
   
   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPContext ctx;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPCommandBar statebar;
   private ASPTable headtbl;
   private ASPTable statetbl;
   private ASPBlockLayout headlay;
   private ASPBlock stateblk;
   private ASPRowSet stateset;
   private ASPBlock itemblk0;
   private ASPRowSet itemset0;
   private ASPCommandBar itembar0;
   private ASPTable itemtbl0;
   private ASPBlockLayout itemlay0;
   private ASPBlock itemblk1;
   private ASPRowSet itemset1;
   private ASPCommandBar itembar1;
   private ASPTable itemtbl1;
   private ASPBlockLayout itemlay1;
   
   private ASPBlock itemblk2;
   private ASPRowSet itemset2;
   private ASPCommandBar itembar2;
   private ASPTable itemtbl2;
   private ASPBlockLayout itemlay2;
   private ASPField f;
   
   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private boolean prepareHead;
   private boolean removeHead;
   private boolean chkpermittype;
   private String url;
   private String scripts;
   private int currrow;
   private ASPQuery q;
   private String val;
   private ASPCommand cmd;
   private String clientvalue2;
   private String connectiontype;
   private String n;
   private ASPBuffer data;
   private String objid;
   private int headrowno;
   private ASPBuffer temp;
   private int currow;
   private String calling_url;
   private String head_command;
   private String fldTitlePreparedBy;
   private String fldTitleApprovedBy;
   private String fldTitleInquiredBy;
   private String fldTitleWorkLeaderSign;
   private String fldTitleDelimitationOrderNo;
   private String lovTitlePreparedBy;
   private String lovTitleApprovedBy;
   private String lovTitleInquiredBy;
   private String lovTitleWorkLeaderSign;
   private String lovTitleDelimitationOrderNo;
   private ASPManager mgr;
   private String txt;
   private String qrystr;
   
   private boolean actEna1;
   private boolean actEna2;
   private boolean actEna3;
   private boolean actEna4;
   private boolean actEna5;
   
   private boolean again;
   private String lay;
   
   private ASPTabContainer tabs;
   
   //Web Alignment - simplify code for RMBs
   private boolean bOpenNewWindow;
   private String urlString;
   private String newWinHandle;
   
   private String validto;
   private String valid;
   
   
   //===============================================================
   // Construction
   //===============================================================
   public Permit(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }
   
   public void run()
   {
      mgr = getASPManager();
      
      
      trans = mgr.newASPTransactionBuffer();
      ctx = mgr.getASPContext();
      
      prepareHead = ctx.readFlag("PREPAREHEAD",false);
      removeHead = ctx.readFlag("REMOVEHEAD",false);
      chkpermittype = ctx.readFlag("CHKPERMITTYPE",false);
      url = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");
      scripts = mgr.getConfigParameter("APPLICATION/LOCATION/SCRIPTS");
      
      actEna1 = ctx.readFlag("ACTENA1",false);
      actEna2 = ctx.readFlag("ACTENA2",false);
      actEna3 = ctx.readFlag("ACTENA3",false);
      actEna4 = ctx.readFlag("ACTENA4",false);

      actEna5 = ctx.readFlag("ACTENA5",false);

      again  = ctx.readFlag("AGAIN",false);
      lay = ctx.readValue("LAY",null);
      qrystr = ctx.readValue("QRYSTR","");
      
      validto =  ctx.readValue("VALIDTO","FALSE");
      valid =  ctx.readValue("VALID","FALSE");
      
      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (mgr.dataTransfered())
         okFind();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("ISOCREATED")) && !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
         okFind();
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         headset.goTo(Integer.parseInt(ctx.findGlobal("CURROW","0")));
         okFindITEM2();
         ctx.removeGlobal("CURROW");
         activateIsoalationOrdersTab();
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("PERMIT_SEQ")))
         okFind();
      else if ("VALIDOK".equals(mgr.readValue("BUTTONVAL")))
      {
         valid="FALSE";
         saveReturnItem2temp();
         okFindITEM2();
      }
      else if ("VALIDCANCEL".equals(mgr.readValue("BUTTONVAL")))
      {
         headset.refreshRow();
         okFindITEM2();
      }
      else if ("VALIDTOOK".equals(mgr.readValue("BUTTONVAL")))
      {
         validto="FALSE";
         saveReturnItem2temp();
         okFindITEM2();
      }
      else if ("VALIDTOCAN".equals(mgr.readValue("BUTTONVAL")))
      {
         headset.refreshRow();
         okFindITEM2();
      }
      
      checkObjAvaileble();
      
      adjust();
      
      tabs.saveActiveTab();
      
      ctx.writeFlag("CHKPERMITTYPE",chkpermittype);
      ctx.writeFlag("PREPAREHEAD",prepareHead);
      ctx.writeFlag("REMOVEHEAD",removeHead);
      
      ctx.writeFlag("ACTENA1",actEna1);
      ctx.writeFlag("ACTENA2",actEna2);
      ctx.writeFlag("ACTENA3",actEna3);
      ctx.writeFlag("ACTENA4",actEna4);

      ctx.writeFlag("ACTENA5",actEna5);

      ctx.writeFlag("AGAIN",again);
      ctx.writeValue("LAY",lay);
      ctx.writeValue("QRYSTR",qrystr);
   }
   
//-----------------------------------------------------------------------------
//-----------------------------  PERFORM FUNCTION  ----------------------------
//-----------------------------------------------------------------------------
   
   public void perform( String command)
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      //Web Alignment - Multirow Action
      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.markSelectedRows(command);
         mgr.submit(trans);
      }
      else
      {
         headset.unselectRows();
         headset.markRow(command);
         int currrow = headset.getCurrentRowNo();
         mgr.submit(trans);
         headset.goTo(currrow);
         headset.refreshRow();
      }
      
      trans.clear();
      if (headlay.isMultirowLayout() && headset.countRows()>0)
         headset.refreshAllRows();
      else if (headlay.isSingleLayout())
         okFindITEM1();
      
      trans.clear();
   }
   
//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------
   
   public void validate()
   {
      ASPManager mgr = getASPManager();
      
      val = mgr.readValue("VALIDATE");
      
      
      if ("CONTRACT".equals(val))
      {
         cmd = trans.addCustomFunction("GETCOMP", "Site_API.Get_Company", "COMPANY" );
         cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
         
         trans = mgr.validate(trans);
         
         String comp = trans.getValue("GETCOMP/DATA/COMPANY");
         txt = (mgr.isEmpty(comp) ? "" :comp)  + "^";
         
      }
      
      
      else if ("PREPARED_BY".equals(val))
      {
         cmd = trans.addCustomFunction("PREID", "Company_Emp_API.Get_Max_Employee_Id", "PREPARED_BY_ID");
         cmd.addParameter("COMPANY");
         cmd.addParameter("PREPARED_BY");
         
         trans = mgr.validate(trans);
         String preid = trans.getValue("PREID/DATA/PREPARED_BY_ID");
         
         txt = (mgr.isEmpty(preid) ? "" : (preid));
         mgr.responseWrite(txt);
      }
      
      
      else if ("INQUIRE".equals(val))
      {
         cmd = trans.addCustomFunction("INQID", "Company_Emp_API.Get_Max_Employee_Id", "INQUIRE_ID");
         cmd.addParameter("COMPANY");
         cmd.addParameter("INQUIRE");
         
         trans = mgr.validate(trans);
         String inqid = trans.getValue("INQID/DATA/INQUIRE_ID");
         
         txt = (mgr.isEmpty(inqid) ? "" : (inqid));
         mgr.responseWrite(txt);
      }
      
      
      else if ("WORK_LEADER_SIGN".equals(val))
      {
         cmd = trans.addCustomFunction("WRKID", "Company_Emp_API.Get_Max_Employee_Id", "WORK_LEADER_SIGN_ID");
         cmd.addParameter("COMPANY");
         cmd.addParameter("WORK_LEADER_SIGN");
         
         trans = mgr.validate(trans);
         String wrkid = trans.getValue("WRKID/DATA/WORK_LEADER_SIGN_ID");
         
         txt = (mgr.isEmpty(wrkid) ? "" : (wrkid));
         mgr.responseWrite(txt);
      }
      
      else if ("APPROVED_BY".equals(val))
      {
         cmd = trans.addCustomFunction("APPID", "Company_Emp_API.Get_Max_Employee_Id", "APPROVED_BY_ID");
         cmd.addParameter("COMPANY");
         cmd.addParameter("APPROVED_BY");
         
         trans = mgr.validate(trans);
         String appid = trans.getValue("APPID/DATA/APPROVED_BY_ID");
         
         txt = (mgr.isEmpty(appid) ? "" : (appid)) ;
         mgr.responseWrite(txt);
      }
      
      else if ("RELEASED_BY".equals(val))
      {
         cmd = trans.addCustomFunction("RELBY", "Company_Emp_API.Get_Max_Employee_Id", "RELEASED_BY_ID");
         cmd.addParameter("COMPANY");
         cmd.addParameter("RELEASED_BY");
         
         trans = mgr.validate(trans);
         String relbyid = trans.getValue("RELBY/DATA/RELEASED_BY_ID");
         
         txt = (mgr.isEmpty(relbyid) ? "" : (relbyid)) ;
         mgr.responseWrite(txt);
      }
      
      else if ("RETURNED_BY".equals(val))
      {
         cmd = trans.addCustomFunction("RETBY", "Company_Emp_API.Get_Max_Employee_Id", "RETURNED_BY_ID");
         cmd.addParameter("COMPANY");
         cmd.addParameter("RETURNED_BY");
         
         trans = mgr.validate(trans);
         String retbyid = trans.getValue("RETBY/DATA/RETURNED_BY_ID");
         
         txt = (mgr.isEmpty(retbyid) ? "" : (retbyid)) ;
         mgr.responseWrite(txt);
      }
      
      
      else if ("PERMIT_TYPE_ID".equals(val))
      {
         cmd = trans.addCustomFunction("CLIENTVALUE", "Connection_Type_API.Decode","CLIENTVAL2");
         cmd.addParameter("DBVALUE","2");
         
         cmd = trans.addCustomFunction("CONTYPE", "Permit_Type_API.Get_Connection_Type","CONNECTTYPE");
         cmd.addParameter("PERMIT_TYPE_ID",mgr.readValue("PERMIT_TYPE_ID"));
         
         trans=mgr.validate(trans);
         
         clientvalue2 = trans.getValue("CLIENTVALUE/DATA/CLIENTVAL2");
         connectiontype = trans.getValue("CONTYPE/DATA/CONNECTTYPE");
         
         txt = (mgr.isEmpty(connectiontype) ? "" : (connectiontype)) + "^" + (mgr.isEmpty(clientvalue2) ? "" : (clientvalue2)) + "^";
         
         mgr.responseWrite(txt);
      }
      
      if ( "ITEM2_DELIMITATION_ORDER_NO".equals(val) )
      {
         cmd = trans.addCustomFunction("ISOTYPE","DELIMITATION_ORDER_API.Get_Isolation_Type","ISOLATION_TYPE");
         cmd.addParameter("ITEM2_DELIMITATION_ORDER_NO");
         
         cmd = trans.addCustomFunction("ISODES","DELIMITATION_ORDER_API.Get_Description","ITEM2_DESCRIPTION");
         cmd.addParameter("ITEM2_DELIMITATION_ORDER_NO");
         
         
         trans = mgr.validate(trans);
         
         String strIsoDes = trans.getValue("ISODES/DATA/ITEM2_DESCRIPTION");
         String strIsoType = trans.getValue("ISOTYPE/DATA/ISOLATION_TYPE");
         
         txt = (mgr.isEmpty(strIsoDes ) ? "" :strIsoDes ) +"^"+ (mgr.isEmpty(strIsoType) ? "" :strIsoType) + "^";
         
         mgr.responseWrite(txt);
         
         
      }
      
      
      
      mgr.endResponse();
   }
   
/////----------------------------------------------------------------------------
/////------------------------ GCreated Functions --------------------------------
//-----------------------------------------------------------------------------
   
   public void okFind()
   {
      ASPManager mgr = getASPManager();
      
      trans.clear();
      q = trans.addQuery(headblk);
      q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
      if (mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());
      q.setOrderByClause("PERMIT_SEQ");
      q.includeMeta("ALL");
      
      q = trans.addQuery(stateblk);
      q.addMasterConnection("HEAD","PERMIT_SEQ","STATE_PERMIT_SEQ");
      q.includeMeta("ALL");
      
      mgr.querySubmit(trans,headblk);
      
      if (headset.countRows() == 1)
      {
         okFindITEM0();
         okFindITEM1();
         okFindITEM2();
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
      }
      
      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("PCMWPERMITNODATA: No data found."));
         headset.clear();
      }
      qrystr = mgr.createSearchURL(headblk);
   }
   
   
   public void countFind()
   {
      ASPManager mgr = getASPManager();
      
      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
      mgr.submit(trans);
      n = headset.getRow().getValue("N");
      headlay.setCountValue(toInt(n));
      headset.clear();
   }
   
   
   public void newRow()
   {
      ASPManager mgr = getASPManager();
      
      trans.clear();
      prepareHead = true;
      cmd = trans.addEmptyCommand("HEAD","PERMIT_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }
   
   public void deleteRow()
   {
      ASPManager mgr = getASPManager();
      
      headset.store();
      if (headlay.isMultirowLayout())
      {
         headset.setSelectedRowsRemoved();
         headset.unselectRows();
      }
      else
         headset.setRemoved();
      
      mgr.submit(trans);
      headbar.browseUpdate(1);
   }
   
   
/////----------------------------------------------------------------------------
/////-------------------------- ITEM Functions ---------------------------------
//-----------------------------------------------------------------------------
   
   
   public void okFindITEM0()
   {
      ASPManager mgr = getASPManager();
      
      trans.clear();
      headrowno = headset.getCurrentRowNo();
      q = trans.addQuery(itemblk0);
      //Bug 58216 Start
      q.addWhereCondition("PERMIT_SEQ = ?");
      q.addParameter("PERMIT_SEQ",headset.getRow().getValue("PERMIT_SEQ"));
      //bug 58216 End
      q.includeMeta("ALL");
      
      mgr.submit(trans);
      if (itemset0.countRows() == 0 &&  ("ITEM0.OkFind".equals(mgr.readValue("__COMMAND"))))
      {
         mgr.showAlert(mgr.translate("PCMWPERMITNODATA: No data found."));
         itemset0.clear();
      }
      headset.goTo(headrowno);
   }
   
   
   public void okFindITEM1()
   {
      ASPManager mgr = getASPManager();
      
      trans.clear();
      headrowno = headset.getCurrentRowNo();
      q = trans.addQuery(itemblk1);
      //Bug 58216 Start
      q.addWhereCondition("PERMIT_SEQ = ?");
      q.addParameter("PERMIT_SEQ",headset.getRow().getValue("PERMIT_SEQ"));
      //Bug 58216 End
      q.includeMeta("ALL");
      
      mgr.submit(trans);
      if (itemset1.countRows() == 0 &&  ("ITEM1.OkFind".equals(mgr.readValue("__COMMAND"))))
      {
         mgr.showAlert(mgr.translate("PCMWPERMITNODATA: No data found."));
         itemset1.clear();
      }
      headset.goTo(headrowno);
   }
   
   public void saveReturn()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      
      headset.changeRow();
      int currrow = headset.getCurrentRowNo();
      mgr.submit(trans);
      headset.refreshAllRows();
      headset.goTo(currrow);
      headlay.setLayoutMode(headlay.getHistoryMode());
      
      qrystr = modifyQrystr(qrystr);
      
   }
   
   public void  saveReturnItem2()
   {
      ASPManager mgr = getASPManager();
      itemset2.changeRow();
      
      validto = "FALSE";
      valid = "FALSE";
      if (!CheckValidityOfPermit())
      {
         trans.clear();
         ctx.setCookie( "my_cookie", "TRUE" );
         validto  = "TRUE";
      }
      else if (!CheckValidityOfPermit2())
      {
         trans.clear();
         ctx.setCookie( "my_cookie", "TRUE" );
         valid  = "TRUE";
      }
      
      else
      {
         saveReturnItem2temp();
         
      }
   }
   
   public void  saveReturnItem2temp()
   {
      
      ASPManager mgr = getASPManager();
      
      trans.clear();
      int currHead = headset.getCurrentRowNo();
      int currrowItem2 = itemset2.getCurrentRowNo();
      
      mgr.submit(trans);
      itemlay2.setLayoutMode(itemlay2.SINGLE_LAYOUT);
      itemset2.goTo(currrowItem2);
      headset.goTo(currHead);
   }
   
   public String modifyQrystr(String qtystrOld)
   {
      String qrystrNew = "";
      String permitSeq = "";
      
      if (headset.countRows()>0)
         permitSeq =  headset.getRow().getValue("PERMIT_SEQ");
      
      if (!mgr.isEmpty(qtystrOld))
      {
         int index = qtystrOld.indexOf("PERMIT_SEQ");
         if (index != -1)
            qrystrNew =  qtystrOld.substring(0,index+11) + permitSeq + ";" + qtystrOld.substring(index+11);
         else
            qrystrNew = mgr.getURL()+"?SEARCH=Y&PERMIT_SEQ="+permitSeq;
      }
      else
         qrystrNew = mgr.getURL()+"?SEARCH=Y&PERMIT_SEQ="+permitSeq;
      
      return  qrystrNew;
   }
   
   public boolean CheckValidityOfPermit()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer buff = mgr.newASPBuffer();
      buff.addFieldItem("VALID_FROM_DATE",mgr.readValue("VALID_FROM_DATE"));
      
      
      trans.clear();
      
      cmd = trans.addCustomFunction("PLANFINISHEST","Delimitation_Order_API.Get_Plan_Finish_Establishment","PLAN_FINSH_EST");
      cmd.addParameter("DELIMITATION_ORDER_NO",mgr.readValue("ITEM2_DELIMITATION_ORDER_NO"));
      
      cmd = trans.addCustomFunction("PLANFINISHREEST","Delimitation_Order_API.Get_Plan_Start_Reestablishment","PLAN_FINSH_REEST");
      cmd.addParameter("DELIMITATION_ORDER_NO",mgr.readValue("ITEM2_DELIMITATION_ORDER_NO"));
      
      
      trans = mgr.validate(trans);
      
      Date planFinishEst = trans.getBuffer("PLANFINISHEST/DATA").getFieldDateValue("PLAN_FINSH_EST");
      String splanFinishEst = trans.getBuffer("PLANFINISHEST/DATA").getFieldValue("PLAN_FINSH_EST");
      
      Date planFinishReest = trans.getBuffer("PLANFINISHREEST/DATA").getFieldDateValue("PLAN_FINSH_REEST");
      String splanFinishReest = trans.getBuffer("PLANFINISHREEST/DATA").getFieldValue("PLAN_FINSH_REEST");
      
      Date validFromDate = buff.getFieldDateValue("VALID_FROM_DATE");
      String svalidFromDate = buff.getFieldValue("VALID_FROM_DATE");
      
      if (!mgr.isEmpty(splanFinishEst) && !mgr.isEmpty(svalidFromDate))
      {
         
         boolean isBefore3 = validFromDate.before(planFinishEst);
         if (isBefore3)
            return false;
         
         else if (!mgr.isEmpty(splanFinishReest) && !mgr.isEmpty(svalidFromDate))
         {
            boolean isBefore2 = planFinishReest.before(validFromDate);
            if (isBefore2)
               return false;
         }
         
      }
      else if (!mgr.isEmpty(splanFinishReest) && !mgr.isEmpty(svalidFromDate))
      {
         boolean isBefore2 = planFinishReest.before(validFromDate);
         if (isBefore2)
            return false;
      }
      else
         return true;
      
      return true;
   }
   
   public boolean CheckValidityOfPermit2()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer buff = mgr.newASPBuffer();
      buff.addFieldItem("VALID_TO_DATE",mgr.readValue("VALID_TO_DATE"));
      
      
      trans.clear();
      
      cmd = trans.addCustomFunction("PLANFINISHEST","Delimitation_Order_API.Get_Plan_Finish_Establishment","PLAN_FINSH_EST");
      cmd.addParameter("DELIMITATION_ORDER_NO",mgr.readValue("ITEM2_DELIMITATION_ORDER_NO"));
      
      cmd = trans.addCustomFunction("PLANFINISHREEST","Delimitation_Order_API.Get_Plan_Start_Reestablishment","PLAN_FINSH_REEST");
      cmd.addParameter("DELIMITATION_ORDER_NO",mgr.readValue("ITEM2_DELIMITATION_ORDER_NO"));
      
      
      trans = mgr.validate(trans);
      
      Date planFinishEst = trans.getBuffer("PLANFINISHEST/DATA").getFieldDateValue("PLAN_FINSH_EST");
      String splanFinishEst = trans.getBuffer("PLANFINISHEST/DATA").getFieldValue("PLAN_FINSH_EST");
      
      Date planFinishReest = trans.getBuffer("PLANFINISHREEST/DATA").getFieldDateValue("PLAN_FINSH_REEST");
      String splanFinishReest = trans.getBuffer("PLANFINISHREEST/DATA").getFieldValue("PLAN_FINSH_REEST");
      
      Date validToDate = buff.getFieldDateValue("VALID_TO_DATE");
      String svalidToDate = buff.getFieldValue("VALID_TO_DATE");
      
      if (!mgr.isEmpty(splanFinishEst) && !mgr.isEmpty(svalidToDate))
      {
         boolean isBefore1 = validToDate.before(planFinishEst);
         if (isBefore1)
            return false;
         else if (!mgr.isEmpty(splanFinishReest) && !mgr.isEmpty(svalidToDate))
         {
            boolean isBefore4 = planFinishReest.before(validToDate);
            if (isBefore4)
               return false;
         }
         
      }
      else if (!mgr.isEmpty(splanFinishReest) && !mgr.isEmpty(svalidToDate))
      {
         boolean isBefore4 = planFinishReest.before(validToDate);
         if (isBefore4)
            return false;
      }
      else
         return true;
      
      return true;
   }
   
   
   public void okFindITEM2()
   {
      ASPManager mgr = getASPManager();
      
      trans.clear();
      headrowno = headset.getCurrentRowNo();
      q = trans.addQuery(itemblk2);
      //bug 58216 Start
      q.addWhereCondition("PERMIT_SEQ = ?");
      q.addParameter("PERMIT_SEQ",headset.getRow().getValue("PERMIT_SEQ"));
      //Bug 58216 End
      q.includeMeta("ALL");
      
      mgr.submit(trans);
      if (itemset2.countRows() == 0 &&  ("ITEM2.OkFind".equals(mgr.readValue("__COMMAND"))))
      {
         mgr.showAlert(mgr.translate("PCMWPERMITNODATA: No data found."));
         itemset2.clear();
      }
      headset.goTo(headrowno);
   }
   
   
   public void countFindITEM0()
   {
      ASPManager mgr = getASPManager();
      
      headrowno = headset.getCurrentRowNo();
      q = trans.addQuery(itemblk0);
      q.setSelectList("to_char(count(*)) N");
      //bug 58216 Start
      q.addWhereCondition("PERMIT_SEQ = ?");
      q.addParameter("PERMIT_SEQ",headset.getRow().getValue("PERMIT_SEQ"));
      //Bug 58216 End
      mgr.submit(trans);
      n = itemset0.getRow().getValue("N");
      itemlay0.setCountValue(toInt(n));
      itemset0.clear();
      headset.goTo(headrowno);
   }
   
   
   public void countFindITEM1()
   {
      ASPManager mgr = getASPManager();
      
      headrowno = headset.getCurrentRowNo();
      q = trans.addQuery(itemblk1);
      q.setSelectList("to_char(count(*)) N");
      //bug 58216 Start
      q.addWhereCondition("PERMIT_SEQ = ?");
      q.addParameter("PERMIT_SEQ",headset.getRow().getValue("PERMIT_SEQ"));
      //Bug 58216 End
      mgr.submit(trans);
      n = itemset1.getRow().getValue("N");
      itemlay1.setCountValue(toInt(n));
      itemset1.clear();
      headset.goTo(headrowno);
   }
   
   public void countFindITEM2()
   
   {
      ASPManager mgr = getASPManager();
      
      headrowno = headset.getCurrentRowNo();
      q = trans.addQuery(itemblk2);
      q.setSelectList("to_char(count(*)) N");
      //bug 58216 Start
      q.addWhereCondition("PERMIT_SEQ = ?");
      q.addParameter("PERMIT_SEQ",headset.getRow().getValue("PERMIT_SEQ"));
      //Bug 58216 End
      mgr.submit(trans);
      n = itemset2.getRow().getValue("N");
      itemlay2.setCountValue(toInt(n));
      itemset2.clear();
      headset.goTo(headrowno);
   }
   
   public void newRowITEM2()
   {
      ASPManager mgr = getASPManager();
      
      trans.clear();
      cmd = trans.addEmptyCommand("ITEM2","ISOLATION_ORDER_PERMIT_API.New__",itemblk2);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      
      data = trans.getBuffer("ITEM2/DATA");
      data.setFieldItem("ITEM2_PERMIT_SEQ",headset.getRow().getValue("PERMIT_SEQ"));
      data.setFieldItem("ITEM2_PERMIT_TYPE_ID",headset.getRow().getValue("PERMIT_TYPE_ID"));
      itemset2.addRow(data);
   }
   
   
   
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
   
   public void checkPermitType()
   {
      ASPManager mgr = getASPManager();
      
      cmd = trans.addCustomFunction("CLIENTVALUE", "Connection_Type_API.Decode","CLIENTVAL2");
      cmd.addParameter("DBVALUE","2");
      
      cmd = trans.addCustomFunction("CONTYPE", "Permit_Type_API.Get_Connection_Type","CONNECTTYPE");
      cmd.addParameter("PERMIT_TYPE_ID",headset.getRow().getValue("PERMIT_TYPE_ID"));
      
      trans=mgr.perform(trans);
      
      clientvalue2 = trans.getValue("CLIENTVALUE/DATA/CLIENTVAL2");
      connectiontype = trans.getValue("CONTYPE/DATA/CONNECTTYPE");
      
      if (connectiontype.equals(clientvalue2))
      {
         temp = headset.getRow();
         temp.setValue("CONNECTTYPE",connectiontype);
         headset.setRow(temp);
         chkpermittype = true;
      }
      else
         chkpermittype = false;
   }
    /* public void setPermitSeq()
 {
     ASPManager mgr = getASPManager();
     
             temp = itemset2.getRow();
         temp.setValue("ITEM2_PERMI_SEQ",headset.getRow().getValue("PERMIT_SEQ"));
         itemset2.setRow(temp);
 }   */
   
   
//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------
   
   public void startPreparation()
   {
      
      //Web Alignment - enable multirow action
      trans.clear();
      perform("START_PREPARATION__");
   }
   
   
   public void prepareCus()
   {
      
      //Web Alignment - enable multirow action
      trans.clear();
      perform("PREPARE__");
   }
   
   
   public void revise()
   {
      //Web Alignment - enable multirow action
      trans.clear();
      perform("REVISE__");
   }
   
   
   public void approvePreparation()
   {
      //Web Alignment - enable multirow action
      trans.clear();
      perform("APPROVE_PREPARATION__");
   }
   
   
   public void release()
   {
      //Web Alignment - enable multirow action
      trans.clear();
      perform("RELEASE__");
   }
   
   
   public void changeDlo()
   {
      //Web Alignment - enable multirow action
      trans.clear();
      perform("CHANGE_DLO__");
   }
   
   
   public void returnPermit()
   {
      //Web Alignment - enable multirow action
      trans.clear();
      perform("RETURN__");
   }
   
   
   public void cancelPermit()
   {
      //Web Alignment - enable multirow action
      trans.clear();
      perform("Canceled__");
   }
   
   
   public void historical()
   {
      //Web Alignment - enable multirow action
      trans.clear();
      perform("Complete__");
   }
   
   public void none()
   {
      ASPManager mgr = getASPManager();
      
      mgr.showAlert(mgr.translate("PCMWPERMITNONE: No RMB method has been selected."));
      
      if (!chkpermittype)
      {
         mgr.getASPField("DELIMITATION_ORDER_NO").setReadOnly();
         eval(headblk.generateAssignments("DELIMITATION_ORDER_NO",headset.getRow()));
      }
   }
   
   public void obtainState()
   {
      trans.clear();
      q = trans.addEmptyQuery(stateblk);
      //bug 58216 Start
      q.addWhereCondition("PERMIT_SEQ = ?");
      q.addParameter("PERMIT_SEQ",headset.getRow().getValue("PERMIT_SEQ"));
      //Bug 58216 End
      q.includeMeta("ALL");
      mgr.submit(trans);
      
   }
   
   public void printPermit()
   {
      ASPManager mgr = getASPManager();
      
      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      
      String attr1 =  "REPORT_ID" + (char)31 + "PERMIT_REP" + (char)30;
      String attr2 =  "PERMIT_SEQ_LIST" + (char)31 + headset.getRow().getValue("PERMIT_SEQ") + (char)30;
      String attr3 =  "";
      String attr4 =  "";
      
      cmd = trans.addCustomCommand( "PRNT", "Archive_API.New_Client_Report");
      
      cmd.addParameter("ATTR0");
      cmd.addParameter("ATTR1",attr1);
      cmd.addParameter("ATTR2",attr2);
      cmd.addParameter("ATTR3",attr3);
      cmd.addParameter("ATTR4",attr4);
      
      trans = mgr.perform(trans);
      
      String result_key =  trans.getValue("PRNT/DATA/ATTR0");
      
      ASPBuffer print = mgr.newASPBuffer();
      ASPBuffer printBuff=print.addBuffer("DATA");
      printBuff.addItem("RESULT_KEY",result_key);
      
      callPrintDlg(print,true);
   }
   
   
   public void attributes()
   {
      ASPManager mgr = getASPManager();
      
      calling_url  = mgr.getURL();
      ctx.setGlobal("CALLING_URL",calling_url);
      
      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      
      String primary_key = headset.getRow().getValue("PERMIT_SEQ");
      ctx.setGlobal("FORM_NAME","Permit.page");
      ctx.setGlobal("PRIMARY_KEY",primary_key);
      //sPermitTypeId = headset.getRow().getValue("PERMIT_TYPE_ID");
      
      checkPermitType();
      if (!chkpermittype)
      {
         //Web Alignment - simplify code for RMBs
         //rmbAttriFlag = "true";
         bOpenNewWindow = true;
         urlString = "PermitTypeRMB.page?PERMIT_TYPE_ID="+mgr.URLEncode(headset.getRow().getValue("PERMIT_TYPE_ID"));
         newWinHandle = "frmPermitType";
         //
      }
      else
         mgr.showAlert(mgr.translate("PCMWPERMITCANNOTATTR: Cannot perform action Attributes."));
   }
   
   
   public void prepareDlo()
   {
      ASPManager mgr = getASPManager();
      
      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();
      
      //Web Alignment - simplify code for RMBs
      bOpenNewWindow = true;
      urlString = "DelimitationOrderTab.page?DELIMITATION_ORDER_NO="+mgr.URLEncode(headset.getRow().getValue("DELIMITATION_ORDER_NO"));
      newWinHandle = "frmPrepDlo";
      //
   }
   
   public void permitData()
   {
      ASPManager mgr = getASPManager();
      
      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();
      
      bOpenNewWindow = true;
      urlString = "../appsrw/TechnicalObjectReference.page?LU_NAME=" + mgr.URLEncode(headset.getRow().getValue("LU_NAME")) +
              "&KEY_REF=" + mgr.URLEncode(headset.getRow().getValue("KEY_REF"));
      
      
      newWinHandle = "frmTecObject";
      
   }
   
   
//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM-IT FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------
   
   private String createTransferUrl(String url, ASPBuffer object)
   {
      ASPManager mgr = getASPManager();
      
      try
      {
         String pkg = mgr.pack(object, 1900 - url.length());
         char sep = url.indexOf('?') > 0 ? '&' : '?';
         urlString = url + sep + "__TRANSFER=" + pkg ;
         return urlString;
      }
      catch (Throwable any)
      {
         return null;
      }
   }
   
   public void prepareWorkOrder()
   {
      ASPManager mgr = getASPManager();
      
      //Web Alignment - Multirow Action
      int count = 0;
      ASPBuffer transferBuffer;
      ASPBuffer rowBuff;
      
      bOpenNewWindow = true;
      
      if (itemlay0.isMultirowLayout())
      {
         itemset0.storeSelections();
         itemset0.setFilterOn();
         count = itemset0.countSelectedRows();
      }
      else
      {
         itemset0.unselectRows();
         count = 1;
      }
      
      transferBuffer = mgr.newASPBuffer();
      
      for (int i = 0; i < count; i++)
      {
         rowBuff = mgr.newASPBuffer();
         
         if (i == 0)
         {
            rowBuff.addItem("WO_NO", itemset0.getValue("WO_NO"));
         }
         else
         {
            rowBuff.addItem(null, itemset0.getValue("WO_NO"));
         }
         
         transferBuffer.addBuffer("DATA", rowBuff);
         
         if (itemlay0.isMultirowLayout())
            itemset0.next();
      }
      
      if (itemlay0.isMultirowLayout())
         itemset0.setFilterOff();
      
      urlString = createTransferUrl("ActiveSeparate2.page", transferBuffer);
      newWinHandle = "frmPrepWO";
      //
      
      
      
   }
   
   public void reportInRoundWorkOrder()
   {
      ASPManager mgr = getASPManager();
      int count = 0;
      ASPBuffer transferBuffer;
      ASPBuffer rowBuff;
      
      bOpenNewWindow = true;
      
      if (itemlay0.isMultirowLayout())
      {
         itemset0.storeSelections();
         itemset0.setFilterOn();
         count = itemset0.countSelectedRows();
      }
      else
      {
         itemset0.unselectRows();
         count = 1;
      }
      
      transferBuffer = mgr.newASPBuffer();
      
      for (int i = 0; i < count; i++)
      {
         rowBuff = mgr.newASPBuffer();
         
         if (i == 0)
         {
            rowBuff.addItem("WO_NO", itemset0.getValue("WO_NO"));
         }
         else
         {
            rowBuff.addItem(null, itemset0.getValue("WO_NO"));
         }
         
         transferBuffer.addBuffer("DATA", rowBuff);
         
         if (itemlay0.isMultirowLayout())
            itemset0.next();
      }
      
      if (itemlay0.isMultirowLayout())
         itemset0.setFilterOff();

      urlString = createTransferUrl("ActiveRound.page", transferBuffer);
      newWinHandle = "frmActRound";
      
   }

   public boolean isModuleInst(String module_)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();
      ASPCommand cmd = mgr.newASPCommand();
      String modVersion;
      
      cmd = trans1.addCustomFunction("MODVERS","module_api.get_version","MODULENAME");
      cmd.addParameter("MODULENAME",module_);
      
      trans1 = mgr.performConfig(trans1);
      modVersion = trans1.getValue("MODVERS/DATA/MODULENAME");
      
      if (!mgr.isEmpty(modVersion))
         return true;
      else
         return false;
   }
   
//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------
   public void createFromIsolationTemplate()
   {
      ASPManager mgr = getASPManager();
      int currow = 0;
      
      if (itemlay2.isMultirowLayout())
         itemset2.goTo(itemset2.getRowSelected());
      
      if (headlay.isSingleLayout() && headset.countRows()>0)
         currow = headset.getCurrentRowNo();
      
      ctx.setGlobal("CURROW",String.valueOf(currow));
      
      bOpenNewWindow = true;
      
      urlString = "ModuleIsoDlg.page?PERMIT_SEQ=" + mgr.URLEncode(headset.getRow().getValue("PERMIT_SEQ")) +
              "&PERMIT_TYPE_ID=" + mgr.URLEncode(headset.getRow().getValue("PERMIT_TYPE_ID"))+
              "&QRYSTR=" + mgr.URLEncode(qrystr);
      
      newWinHandle = "createDeliTemp";
   }
   
   public void createIsolationOrder()
   {
      ASPManager mgr = getASPManager();
      int currow = 0;
      
      if (itemlay2.isMultirowLayout())
         itemset2.goTo(itemset2.getRowSelected());
      
      if (headlay.isSingleLayout() && headset.countRows()>0)
         currow = headset.getCurrentRowNo();
      
      ctx.setGlobal("CURROW",String.valueOf(currow));
      
      bOpenNewWindow = true;
      
      urlString = "ModuleIsoTypDlg.page?PERMIT_SEQ=" + mgr.URLEncode(headset.getRow().getValue("PERMIT_SEQ")) +
              "&PERMIT_TYPE_ID=" + mgr.URLEncode(headset.getRow().getValue("PERMIT_TYPE_ID"))+
              "&QRYSTR=" + mgr.URLEncode(qrystr);
      
      newWinHandle = "createDeliIsoTypTemp";
   }
   
   public void prepareIsolationOrder()
   {
      ASPManager mgr = getASPManager();
      
      if (itemlay2.isMultirowLayout())
         itemset2.goTo(itemset2.getRowSelected());
      
      bOpenNewWindow = true;
      
      urlString = "DelimitationOrderTab.page?DELIMITATION_ORDER_NO=" + mgr.URLEncode(itemset2.getRow().getValue("DELIMITATION_ORDER_NO"));
      
      newWinHandle = "frmDelimitationOrder";
   }
   
   
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
   
   
   
   
   public void preDefine()
   {
      ASPManager mgr = getASPManager();
      
      headblk = mgr.newASPBlock("HEAD");
      
      f = headblk.addField("OBJID");
      f.setHidden();
      
      f = headblk.addField("OBJVERSION");
      f.setHidden();
      
      f = headblk.addField("OBJSTATE");
      f.setHidden();
      
      f = headblk.addField("MODULENAME");
      f.setHidden();
      f.setFunction("''");
      
      f = headblk.addField("PERMIT_SEQ");
      f.setSize(10);
      f.setLabel("PCMWPERMITPERMITSEQ: Permit Seq");
      f.setHilite();
      f.setReadOnly();
      
      f = headblk.addField("PERMIT_TYPE_ID");
      f.setSize(6);
      f.setDynamicLOV("PERMIT_TYPE",600,450);
      f.setMandatory();
      f.setCustomValidation("PERMIT_TYPE_ID,DBVALUE","CONNECTTYPE,CLIENTVAL2");
      f.setLabel("PCMWPERMITTYPEID: Permit Type");
      f.setHilite();
      f.setUpperCase();
      f.setReadOnly();
      f.setInsertable();
      f.setMaxLength(4);
      
      f = headblk.addField("CONTRACT");
      f.setSize(10);
      f.setReadOnly();
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
      f.setCustomValidation("CONTRACT","COMPANY");
      f.setLabel("PCMWPERMITCONTRACT: Site");
      f.setHilite();
      f.setUpperCase();
      f.setInsertable();
      f.setMaxLength(5);
      
      f = headblk.addField("COMPANY");
      f.setSize(6);
      f.setMandatory();
      f.setHidden();
      f.setUpperCase();
      
      f = headblk.addField("STATE");
      f.setSize(25);
      f.setLOV("PermitLov.page",600,450);
      f.setLabel("PCMWPERMITSTATE: Status");
      f.setHilite();
      f.setReadOnly();
      f.setMaxLength(253);
      
      f = headblk.addField("DESCRIPTION");
      f.setSize(40);
      f.setLabel("PCMWPERMITDESC: Permit Description");
      f.setHilite();
      f.setMaxLength(40);
      
      f = headblk.addField("VALID_FROM_DATE","Datetime");
      f.setSize(25);
      f.setLabel("PCMWPERMITFROMDATE: Valid From Date");
      
      f = headblk.addField("VALID_TO_DATE","Datetime");
      f.setSize(25);
      f.setLabel("PCMWPERMITTODATE: Valid To Date");
      
      f = headblk.addField("WORK_LEADER_SIGN");
      f.setSize(25);
      f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,450);
      f.setLOVProperty("TITLE","PCMWPERMITLOVWL: List of Work Leader Signature");
      f.setCustomValidation("COMPANY,WORK_LEADER_SIGN","WORK_LEADER_SIGN_ID");
      f.setLabel("PCMWPERMITWORKSIGN: Work Leader Signature");
      f.setUpperCase();
      f.setMaxLength(20);
      
      f = headblk.addField("PREPARED_BY");
      f.setSize(25);
      f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,450);
      f.setLOVProperty("TITLE","PCMWPERMITLOVTITLE3: List of Prepared by");
      f.setCustomValidation("COMPANY,PREPARED_BY","PREPARED_BY_ID");
      f.setLabel("PCMWPERMITPREBY: Prepared By");
      f.setUpperCase();
      f.setMaxLength(20);
      
      f = headblk.addField("PREPARED_BY_ID");
      f.setSize(15);
      f.setHidden();
      f.setUpperCase();
      f.setMaxLength(11);
      
      f = headblk.addField("INQUIRE");
      f.setSize(25);
      f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,450);
      f.setLOVProperty("TITLE","PCMWPERMITLOVTITLE2: List of Inquired by");
      f.setCustomValidation("COMPANY,INQUIRE","INQUIRE_ID");
      f.setLabel("PCMWPERMITINQUIRE: Inquired By");
      f.setDefaultNotVisible();
      f.setUpperCase();
      f.setMaxLength(20);
      
      f = headblk.addField("INQUIRE_ID");
      f.setSize(15);
      f.setHidden();
      f.setUpperCase();
      f.setMaxLength(11);
      
      f = headblk.addField("WORK_LEADER_SIGN_ID");
      f.setSize(15);
      f.setHidden();
      f.setUpperCase();
      f.setMaxLength(11);
      
      f = headblk.addField("APPROVED_BY");
      f.setSize(25);
      f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,450);
      f.setLOVProperty("TITLE","PCMWPERMITLOVTITLE1: List of Approved by");
      f.setCustomValidation("COMPANY,APPROVED_BY","APPROVED_BY_ID");
      f.setLabel("PCMWPERMITAPPBY: Approved By");
      f.setDefaultNotVisible();
      f.setUpperCase();
      f.setMaxLength(20);
      
      f = headblk.addField("RELEASED_BY");
      f.setSize(25);
      f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,450);
      f.setLOVProperty("TITLE","PCMWPERMITLISTRELEASEDBY: List of Released by");
      f.setCustomValidation("COMPANY,RELEASED_BY","RELEASED_BY_ID");
      f.setLabel("PCMWPERMITRELEASEBY: Released By");
      f.setDefaultNotVisible();
      f.setUpperCase();
      f.setMaxLength(20);
      
      f = headblk.addField("RELEASED_BY_ID");
      f.setSize(15);
      f.setHidden();
      
      f = headblk.addField("RETURNED_BY");
      f.setSize(25);
      f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,450);
      f.setLOVProperty("TITLE","PCMWPERMITLISTRETURNEDBY: List of Returned by");
      f.setCustomValidation("COMPANY,RETURNED_BY","RETURNED_BY_ID");
      f.setLabel("PCMWPERMITRETURNEDBY: Returned By");
      f.setDefaultNotVisible();
      f.setUpperCase();
      f.setMaxLength(20);
      
      f = headblk.addField("RETURNED_BY_ID");
      f.setSize(15);
      f.setHidden();
      
      f = headblk.addField("DELIMITATION_ORDER_NO","Number","#");
      f.setSize(9);
      f.setDynamicLOV("DELIMITATION_ORDER",600,450);
      f.setLOVProperty("TITLE","WRKCLRNOTITLE: List of Isolation Order No");
      f.setDefaultNotVisible();
      f.setLabel("PCMWPERMITDELORDNO: Isolation Order No");
      f.setMaxLength(8);
      
      f = headblk.addField("DELIMITATIONORDEROBJSTATE");
      f.setSize(25);
      f.setReadOnly();
      f.setLabel("PCMWPERMITDELOBJSTATE: Isolation Order Status");
      f.setFunction("DELIMITATION_ORDER_API.Get_Decoded_State(:DELIMITATION_ORDER_NO)");
      mgr.getASPField("DELIMITATION_ORDER_NO").setValidation("DELIMITATIONORDEROBJSTATE");
      f.setDefaultNotVisible();
      f.setMaxLength(253);
      
      f = headblk.addField("NOTE");
      f.setSize(45);
      f.setLabel("PCMWPERMITNOTE: Note");
      f.setDefaultNotVisible();
      f.setMaxLength(2000);
      
      //---------- Added fields for hyperlink (Documents)-----------------
      
      f = headblk.addField("LU_NAME");
      f.setHidden();
      f.setFunction("'Permit'");
      
      f = headblk.addField("KEY_REF");
      f.setHidden();
      f.setFunction("CONCAT('PERMIT_SEQ=',CONCAT(PERMIT_SEQ,'^'))");
      
      f = headblk.addField("DOCUMENT");
      if (isModuleInst("DOCMAN"))
         f.setFunction("substr(Doc_Reference_Object_API.Exist_Obj_Reference('Permit',CONCAT('PERMIT_SEQ=',CONCAT(PERMIT_SEQ,'^'))),1, 5)");
      else
         f.setFunction("''");
      f.setUpperCase();
      f.setLabel("PCMWPERMITDOCUMENT: Has Documents");
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setCheckBox("FALSE,TRUE");
      f.setSize(18);
      
      //----------------End of the added part-------------------------
      
      //----------------End of the added part-------------------------
      
      f = headblk.addField("APPROVED_BY_ID");
      f.setSize(15);
      f.setHidden();
      f.setUpperCase();
      f.setMaxLength(11);
      
      f = headblk.addField("CLIENTVAL2");
      f.setFunction("''");
      f.setHidden();
      
      f = headblk.addField("CONNECTTYPE");
      f.setFunction("''");
      f.setHidden();
      
      f = headblk.addField("DBVALUE");
      f.setFunction("''");
      f.setHidden();
      
      f = headblk.addField("ATTR0");
      f.setFunction("''");
      f.setHidden();
      
      f = headblk.addField("ATTR1");
      f.setFunction("''");
      f.setHidden();
      
      f = headblk.addField("ATTR2");
      f.setFunction("''");
      f.setHidden();
      
      f = headblk.addField("ATTR3");
      f.setFunction("''");
      f.setHidden();
      
      f = headblk.addField("ATTR4");
      f.setFunction("''");
      f.setHidden();
      
      
      headblk.setView("PERMIT");
      headblk.defineCommand("PERMIT_API","New__,Modify__,Remove__,START_PREPARATION__,PREPARE__,REVISE__,APPROVE_PREPARATION__,RELEASE__,CHANGE_DLO__,RETURN__,Canceled__,Complete__");
      headblk.disableDocMan();
      headset = headblk.getASPRowSet();
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("PCMWPERMITHD: Permit"));
      headtbl.setWrap();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.defineCommand(headbar.SAVERETURN,"saveReturn");
      headbar.defineCommand(headbar.DELETE ,"deleteRow");
      
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      
      //Web Alignment - Adding Tabs
      tabs = mgr.newASPTabContainer();
      tabs.setTabSpace(5);
      tabs.setTabWidth(100);
      
      tabs.addTab("Work Orders","javascript:commandSet('HEAD.activateWorkOrdersTab','')");
      tabs.addTab("Isolation Orders","javascript:commandSet('HEAD.activateIsoalationOrdersTab','')");
      tabs.addTab("Permit History","javascript:commandSet('HEAD.activatePermitHistoryTab','')");
      
      headbar.addCustomCommand("activateWorkOrdersTab","Work Orders");
      headbar.addCustomCommand("activateIsoalationOrdersTab","Isolation Orders");
      headbar.addCustomCommand("activatePermitHistoryTab","Permit History");
      //
      
      //Web Alignment - enable multirow action
      headtbl.enableRowSelect();
      //
      
      headbar.addCustomCommand("permitData",mgr.translate("PCMWPERMITDATA: Permit Data..."));
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("printPermit",mgr.translate("PCMWPERMITPRINT: Print..."));
      headbar.addCustomCommand("prepareDlo",mgr.translate("PCMWPERMITPREPAREDLO: Prepare Delimitation Order..."));
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("attributes",mgr.translate("PCMWPERMITATTRIBUTES: Attributes..."));
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("startPreparation",mgr.translate("PCMWPERMITSTARTPREPARATION: Start Preparation"));
      headbar.addCustomCommand("prepareCus",mgr.translate("PCMWPERMITPREPARECUS: Prepare"));
      headbar.addCustomCommand("revise",mgr.translate("PCMWPERMITREVISE: Revise"));
      headbar.addCustomCommand("approvePreparation",mgr.translate("PCMWPERMITAPPROVEPREPARATION: Approve Preparation"));
      headbar.addCustomCommand("release",mgr.translate("PCMWPERMITRELEASE: Release"));
      headbar.addCustomCommand("changeDlo",mgr.translate("PCMWPERMITCHANGEDLO: Change Delimitation Order"));
      headbar.addCustomCommand("returnPermit",mgr.translate("PCMWPERMITRETURNPERMIT: Return Permit"));
      headbar.addCustomCommand("cancelPermit",mgr.translate("PCMWPERMITCANCEL: Cancelled"));
      headbar.addCustomCommand("historical",mgr.translate("PCMWPERMITHITORICAL: Historical"));
      
      
      headbar.addCustomCommandGroup("ISSTATUS", mgr.translate("PCMWPERMITSTUS: Permit Status"));
      headbar.setCustomCommandGroup("startPreparation", "ISSTATUS");
      headbar.setCustomCommandGroup("prepareCus", "ISSTATUS");
      headbar.setCustomCommandGroup("revise", "ISSTATUS");
      headbar.setCustomCommandGroup("approvePreparation", "ISSTATUS");
      headbar.setCustomCommandGroup("release", "ISSTATUS");
      headbar.setCustomCommandGroup("changeDlo", "ISSTATUS");
      headbar.setCustomCommandGroup("returnPermit", "ISSTATUS");
      headbar.setCustomCommandGroup("cancelPermit", "ISSTATUS");
      headbar.setCustomCommandGroup("historical", "ISSTATUS");
      
      //Web Alignment - enable multirow action
      headbar.enableMultirowAction();
      headbar.removeFromMultirowAction("attributes");
      headbar.removeFromMultirowAction("prepareDlo");
      
      headbar.addCommandValidConditions("prepareDlo","DELIMITATION_ORDER_NO","Disable","null");
      
      headbar.addCommandValidConditions("startPreparation",   "OBJSTATE",  "Enable",  "New");
      headbar.addCommandValidConditions("prepareCus",  "OBJSTATE",  "Enable",  "New;UnderPreparation");
      headbar.addCommandValidConditions("revise",   "OBJSTATE",  "Enable",  "PreparationApproved;Prepared");
      headbar.addCommandValidConditions("approvePreparation",   "OBJSTATE",  "Enable",  "Prepared");
      headbar.addCommandValidConditions("release",    "OBJSTATE",  "Enable",  "IsoChanged;PreparationApproved");
      headbar.addCommandValidConditions("changeDlo",   "OBJSTATE",  "Enable",  "Released");
      headbar.addCommandValidConditions("returnPermit",   "OBJSTATE",  "Enable",  "Released");
      headbar.addCommandValidConditions("cancelPermit","OBJSTATE","Enable","New;UnderPreparation;PreparationApproved;Prepared");
      headbar.addCommandValidConditions("historical","OBJSTATE","Enable","Returned");
      //
      head_command = headbar.getSelectedCustomCommand();
      headlay.defineGroup("","PERMIT_SEQ,PERMIT_TYPE_ID,DESCRIPTION,STATE,CONTRACT",false,true);
      headlay.defineGroup(mgr.translate("PCMWPERMITPREPTAB: Prepare"),"NOTE,DOCUMENT,VALID_FROM_DATE,VALID_TO_DATE,PREPARED_BY,INQUIRE,WORK_LEADER_SIGN,APPROVED_BY,RELEASED_BY,RETURNED_BY",true,true);
      
      stateblk = mgr.newASPBlock("STATE");
      
      f = stateblk.addField("STOBJID");
      f.setHidden();
      f.setDbName("OBJID");
      
      f = stateblk.addField("STOBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
      
      f = stateblk.addField("OBJEVENTS");
      f.setHidden();
      
      f = stateblk.addField("STATE_PERMIT_SEQ");
      f.setMandatory();
      f.setHidden();
      f.setDbName("PERMIT_SEQ");
      
      
      stateblk.setView("PERMIT");
      stateblk.defineCommand("PERMIT_API","New__,Modify__,Remove__,START_PREPARATION__,PREPARE__,REVISE__,APPROVE_PREPARATION__,RELEASE__,CHANGE_DLO__,RETURN__");
      stateblk.setMasterBlock(headblk);
      stateset = stateblk.getASPRowSet();
      statetbl = mgr.newASPTable(stateblk);
      statebar = mgr.newASPCommandBar(stateblk);
      
      
      ////////////////////////////////////////////////////////
      ////////////////Work Orders Tab Items///////////////////
      ////////////////////////////////////////////////////////
      
      itemblk0 = mgr.newASPBlock("ITEM0");
      
      f = itemblk0.addField("ITEM0_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
      
      f = itemblk0.addField("ITEM0_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
      
      f = itemblk0.addField("ITEM0_PERMIT_SEQ","Number");
      f.setSize(11);
      f.setDynamicLOV("PERMIT",600,450);
      f.setMandatory();
      f.setHidden();
      f.setLabel("PCMWPERMITIT0PERSEQ: Permit Seq");
      f.setDbName("PERMIT_SEQ");
      
      f = itemblk0.addField("WO_NO","Number","#");
      f.setSize(10);
      f.setDynamicLOV("ACTIVE_WORK_ORDER",600,450);
      f.setMandatory();
      f.setLabel("PCMWPERMITWONO: WO No");
      f.setMaxLength(8);
      
      f = itemblk0.addField("WOSTATUS");
      f.setSize(25);
      f.setLabel("PCMWPERMITWOSTATUS: Status");
      f.setFunction("ACTIVE_WORK_ORDER_API.Get_State(:WO_NO)"); 
      f.setMaxLength(253);
      
      f = itemblk0.addField("WORKMASTERSIGN");
      f.setSize(25);
      f.setLabel("PCMWPERMITWORKMASTERSIGN: Executed By");
      f.setFunction("substr(ACTIVE_WORK_ORDER_API.GET_WORK_MASTER_SIGN(WO_NO),1,20)"); 
      f.setMaxLength(20);
      
      f = itemblk0.addField("WORKLEADERSIGN");
      f.setSize(25);
      f.setLabel("PCMWPERMITWORKLEADERSIGN: Work Leader");
      f.setFunction("substr(ACTIVE_WORK_ORDER_API.GET_WORK_LEADER_SIGN(WO_NO),1,20)"); 
      f.setMaxLength(20);
      
      f = itemblk0.addField("ORGCODE");
      f.setSize(10);
      f.setLabel("PCMWPERMITORGCODE: Maintenance Organization");
      f.setFunction("Substr(ACTIVE_WORK_ORDER_API.GET_ORG_CODE(WO_NO),1,10)"); 
      f.setMaxLength(8);
      
      f = itemblk0.addField("PLANSDATE","Datetime");
      f.setSize(25);
      f.setLabel("PCMWPERMITPLANSDATE: Planned Start");
      f.setFunction("ACTIVE_WORK_ORDER_API.Get_Plan_S_Date(:WO_NO)"); 
      
      f = itemblk0.addField("PLANFDATE","Datetime");
      f.setSize(25);
      f.setLabel("PCMWPERMITPLANFDATE: Planned Completion");
      f.setFunction("ACTIVE_WORK_ORDER_API.Get_Plan_F_Date(:WO_NO)"); 
      
      f = itemblk0.addField("MCH_CODE");
      f.setSize(25);
      f.setLabel("PCMWPERMITMCHCODE: Object ID");
      f.setFunction("Substr(SEPARATE_WORK_ORDER_API.GET_MCH_CODE(WO_NO),1,40)");
      f.setMaxLength(100);
      
      f = itemblk0.addField("MCHNAME");
      f.setSize(35);
      f.setLabel("PCMWPERMITMCHNAME: Object Description");
      f.setFunction("substr(SEPARATE_WORK_ORDER_API.GET_OBJ_DESCRIPTION(WO_NO),1,45)");
      mgr.getASPField("MCH_CODE").setValidation("MCHNAME");
      
      f = itemblk0.addField("OBJECTSTATE");
      f.setSize(25);
      f.setHidden();
      f.setLabel("PCMWPERMITOBJECTSTATE: Object State");
      f.setFunction("substr(Active_Work_Order_API.Get_Obj_State(WO_NO),1,30)");
      
      f = itemblk0.addField("ROUNDDEF");
      f.setSize(11);
      f.setHidden();
      f.setFunction("ACTIVE_WORK_ORDER_API.Get_Rounddef_Id(:WO_NO)");
      
      itemblk0.setView("WO_ORDER_PERMIT");
      itemblk0.defineCommand("WORK_ORDER_PERMIT_API","New__,Modify__,Remove__");
      itemblk0.setMasterBlock(headblk);
      //itemblk0.setTitle(mgr.translate("PCMWPERMITITM0TITLE: Work Orders"));
      itemset0 = itemblk0.getASPRowSet();
      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle(mgr.translate("PCMWPERMITITM0: Work Orders"));
      itemtbl0.setWrap();
      itembar0 = mgr.newASPCommandBar(itemblk0);
      
      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
      
      itembar0.disableCommand(itembar0.EDITROW);
      itembar0.disableCommand(itembar0.DELETE);
      itembar0.disableCommand(itembar0.DUPLICATEROW);
      itembar0.disableCommand(itembar0.NEWROW);
      itembar0.enableCommand(itembar0.FIND);
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
      
      //Web Alignment - Multirow Action
      itemtbl0.enableRowSelect();
      //
      
      itembar0.addCustomCommand("prepareWorkOrder", mgr.translate("PCMWPERMITPREPAREWORKORDER: Prepare Work Order..."));
      
      //Web Alignment - Multirow Action
      itembar0.enableMultirowAction();
      itembar0.addCommandValidConditions("prepareWorkOrder", "OBJECTSTATE", "Disable", "null");
      //
      
      itembar0.addCustomCommand("reportInRoundWorkOrder", mgr.translate("PCMWPERMITROUTEWORKORDER: Report In Round Work Order..."));
      itembar0.addCommandValidConditions("prepareWorkOrder","ROUNDDEF","Enable",null);
      itembar0.addCommandValidConditions("reportInRoundWorkOrder","ROUNDDEF","Disable",null);
      
      ////////////////////////////////////////////////////////
      ///////////////Permit History Tab Items/////////////////
      ////////////////////////////////////////////////////////
      
      
      
      itemblk1 = mgr.newASPBlock("ITEM1");
      
      f = itemblk1.addField("ITEM1_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
      
      f = itemblk1.addField("ITEM1_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
      
      f = itemblk1.addField("ITEM1_PERMIT_SEQ","Number");
      f.setHidden();
      f.setDbName("PERMIT_SEQ");
      
      f = itemblk1.addField("CREATE_DATE","Datetime");
      f.setSize(25);
      f.setLabel("PCMWPERMITCRDATE: Create Date");
      
      f = itemblk1.addField("PERMIT_STATUS");
      f.setSize(35);
      f.setLabel("PCMWPERMITPERSTATUS: Permit Status");
      f.setMaxLength(30);
      
      f = itemblk1.addField("ITEM1_VALID_FROM_DATE","Datetime");
      f.setSize(25);
      f.setLabel("PCMWPERMITIT1FDATE: Valid From Date");
      f.setDbName("VALID_FROM_DATE");
      
      f = itemblk1.addField("ITEM1_VALID_TO_DATE","Datetime");
      f.setSize(25);
      f.setLabel("PCMWPERMITIT1TDATE: Valid To Date");
      f.setDbName("VALID_TO_DATE");
      
      f = itemblk1.addField("USER_NAME");
      f.setSize(35);
      f.setLabel("PCMWPERMITUSERNAME: User Name");
      f.setUpperCase();
      f.setMaxLength(30);
      
      f = itemblk1.addField("ITEM1_NOTE");
      f.setHidden();
      f.setDbName("NOTE");
      
      itemblk1.setView("PERMIT_HISTORY");
      itemblk1.defineCommand("PERMIT_HISTORY_API","New__,Modify__,Remove__");
      itemblk1.setMasterBlock(headblk);
      itemset1 = itemblk1.getASPRowSet();
      itembar1 = mgr.newASPCommandBar(itemblk1);
      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);
      
      itembar1.disableCommand(itembar1.EDITROW);
      itembar1.disableCommand(itembar1.DELETE);
      itembar1.disableCommand(itembar1.DUPLICATEROW);
      itembar1.disableCommand(itembar1.NEWROW);
      itembar1.enableCommand(itembar1.FIND);
      itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
      itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
      
      
      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setTitle(mgr.translate("PCMWPERMITITM1: Permit History"));
      itemtbl1.setWrap();
      itembar1.removeCommandGroup(itembar1.CMD_GROUP_CUSTOM);
      
      //------------------------------------------------------//
      //-----------------Isolation order         -------------//
      //------------------------------------------------------//
      
      
      itemblk2 = mgr.newASPBlock("ITEM2");
      
      f = itemblk2.addField("ITEM2_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
      
      f = itemblk2.addField("ITEM2_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
      
      
      f = itemblk2.addField("ITEM2_PERMIT_SEQ","Number");
      f.setHidden();
      f.setDbName("PERMIT_SEQ");
      
      f = itemblk2.addField("ITEM2_PERMIT_TYPE_ID");
      f.setHidden();
      f.setDbName("PERMIT_TYPE_ID");
      
      f = itemblk2.addField("ITEM2_DELIMITATION_ORDER_NO","Number","#");
      f.setDbName("DELIMITATION_ORDER_NO");
      f.setLabel("ITEM2DELIMITATIONORDERNO: Isolation Order No");
      f.setDynamicLOV("CONN_DELIMITATION_ORDER",600,450);
      f.setCustomValidation("ITEM2_DELIMITATION_ORDER_NO","ITEM2_DESCRIPTION,ISOLATION_TYPE");
      f.setSize(9);
      f.setMaxLength(8);
      
      f = itemblk2.addField("ISOLATION_TYPE");
      f.setSize(35);
      f.setLabel("PCMWPERMITISOLATIONTYPE: Isolation Type");
      f.setMaxLength(100);
      f.setReadOnly();
      
      f = itemblk2.addField("ITEM2_DESCRIPTION");
      f.setSize(35);
      f.setLabel("ITEM2DESCRIPTION: Description");
      f.setMaxLength(2000);
      f.setFunction("DELIMITATION_ORDER_API.Get_Description(:ITEM2_DELIMITATION_ORDER_NO)");
      f.setReadOnly();
      
      f = itemblk2.addField("ITEM2_STATUS");
      f.setSize(35);
      f.setLabel("ITEM2STATUS: Status");
      f.setFunction("DELIMITATION_ORDER_API.Get_Objstate(:ITEM2_DELIMITATION_ORDER_NO)");
      f.setReadOnly();
      
      f = itemblk2.addField("PLAN_FINSH_EST","Datetime");
      f.setFunction("Delimitation_Order_API.Get_Plan_Finish_Establishment(:ITEM2_DELIMITATION_ORDER_NO)");
      f.setHidden();
      
      
      f = itemblk2.addField("PLAN_FINSH_REEST","Datetime");
      f.setFunction("Delimitation_Order_API.Get_Plan_Start_Reestablishment(:ITEM2_DELIMITATION_ORDER_NO)");
      f.setHidden();
      
      itemblk2.setView("ISOLATION_ORDER_PERMIT");
      itemblk2.defineCommand("ISOLATION_ORDER_PERMIT_API","New__,Modify__,Remove__");
      itemblk2.setMasterBlock(headblk);
      itemset2 = itemblk2.getASPRowSet();
      itembar2 = mgr.newASPCommandBar(itemblk2);
      
      itembar2.enableMultirowAction();
      
      itemlay2 = itemblk2.getASPBlockLayout();
      itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);
      
      itembar2.enableCommand(itembar2.FIND);
      itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
      itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");
      itembar2.defineCommand(itembar2.NEWROW,   "newRowITEM2");
      itembar2.defineCommand(itembar2.SAVERETURN,"saveReturnItem2");
      itembar2.defineCommand(itembar2.SAVENEW,"saveReturnItem2");
      
      
      itembar2.addCustomCommand("createIsolationOrder",mgr.translate("PCMWCREATEISOORDER: Create Isolation Order...."));
      itembar2.addCustomCommand("createFromIsolationTemplate",mgr.translate("PCMWPERMITCREATEFROMISOTEMPLATE: Create From Isolation Template..."));
      itembar2.addCustomCommand("prepareIsolationOrder",mgr.translate("PCMWPERMITPREPAREISOLATIONORDER: Prepare Isolation Order...."));
      
      itembar2.forceEnableMultiActionCommand("createIsolationOrder");
      itembar2.forceEnableMultiActionCommand("createFromIsolationTemplate");
      itembar2.removeFromMultirowAction("prepareIsolationOrder");
      
      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.setTitle(mgr.translate("PCMWPERMITITM2: Isolation Orders"));
      itemtbl2.setWrap();
   }
   
   //Web Alignment - Adding Tabs
   public void activateWorkOrdersTab()
   {
      tabs.setActiveTab(1);
   }
   public void activateIsoalationOrdersTab()
   {
      tabs.setActiveTab(2);
   }
   public void activatePermitHistoryTab()
   {
      tabs.setActiveTab(3);
   }
   
   
   //
   
   
   public void checkObjAvaileble()
   {
      
      if (!again)
      {
         
         ASPManager mgr = getASPManager();
         
         ASPBuffer availObj;
         trans.clear();
         
         trans.addSecurityQuery("PERMIT_TYPE_ATTRIBUTE,PERMIT,DELIMITATION_ORDER,ACTIVE_SEPARATE,ACTIVE_ROUND");
         trans.addPresentationObjectQuery("PCMW/PermitTypeRMB.page,PCMW/DelimitationOrderTab.page,PCMW/ActiveSeparate2.page,PCMW/ActiveRound.page");

         trans = mgr.perform(trans);
         
         availObj = trans.getSecurityInfo();
         
         if (availObj.itemExists("PERMIT_TYPE_ATTRIBUTE")  && availObj.namedItemExists("PCMW/PermitTypeRMB.page"))
            actEna1 = true;
         
         if (availObj.itemExists("PERMIT"))
            actEna2 = true;
         
         if (availObj.itemExists("DELIMITATION_ORDER")  && availObj.namedItemExists("PCMW/DelimitationOrderTab.page"))
            actEna3 = true;
         
         if (availObj.itemExists("ACTIVE_SEPARATE") &&  availObj.namedItemExists("PCMW/ActiveSeparate2.page"))
            actEna4 = true;
         
         if ( availObj.itemExists("ACTIVE_ROUND") &&  availObj.namedItemExists("PCMW/ActiveRound.page") )
            actEna5 = true;

         again = true;
         
      }
   }
   
   public void adjust()
   {
      
      ASPManager mgr = getASPManager();
      
      headbar.removeCustomCommand("activateWorkOrdersTab");
      headbar.removeCustomCommand("activatePermitHistoryTab");
      headbar.removeCustomCommand("activateIsoalationOrdersTab");
     

      
      if (headlay.isNewLayout())
         lay = "new";
      else
         lay= null;
      
      if (!actEna1)
         headbar.removeCustomCommand("attributes");
      
      if (!actEna2)
         headbar.removeCustomCommand("printPermit");
      
      if (!actEna3)
         headbar.removeCustomCommand("prepareDlo");
      
      if (!actEna4)
         itembar0.removeCustomCommand("prepareWorkOrder");
      
       if (!actEna5)
         itembar0.removeCustomCommand("reportInRoundWorkOrder");
      
      if (mgr.isPresentationObjectInstalled("docmaw/DocReference.page"))
         mgr.getASPField("DOCUMENT").setHyperlink("../docmaw/DocReference.page","LU_NAME,KEY_REF","NEWWIN");
      if (headlay.isEditLayout())
      {
         checkPermitType();
         if (!chkpermittype)
         {
            mgr.getASPField("DELIMITATION_ORDER_NO").setReadOnly();
            eval(headblk.generateAssignments("DELIMITATION_ORDER_NO",headset.getRow()));
         }
      }
      
      if (headlay.isMultirowLayout())
      {
         headbar.removeCustomCommand("printPermit");
      }
      else
      {
         if (headset.countRows() > 0)
         {
            String value_dlo = headset.getRow().getValue("DELIMITATION_ORDER_NO");
            if (mgr.isEmpty(value_dlo))
               headbar.removeCustomCommand("prepareDlo");
         }
         //mgr.getASPField("DOCUMENT").setHidden();
      }
      
      if ((headlay.isSingleLayout()) && (headset.countRows() > 0))
      {
         objid = headset.getRow().getValue("OBJID");
         currrow = headset.getCurrentRowNo();
         //okFindState();
         headset.goTo(currrow);
      }
      
      
      fldTitlePreparedBy = mgr.translate("PCMWPERMITPREPEREDBYFLD: Prepared+by");
      fldTitleApprovedBy = mgr.translate("PCMWPERMITAPPROVEDBYFLD: Approved+by");
      fldTitleInquiredBy = mgr.translate("PCMWPERMITINQUIREDBYFLD: Inquired+by");
      fldTitleWorkLeaderSign = mgr.translate("PCMWPERMITWORKLEADERSIGNFLD: Work+Leader+Signature");
      fldTitleDelimitationOrderNo = mgr.translate("PCMWPERMITDELIMITORDFLD: Delimitation+Order+No");
      
      
      lovTitlePreparedBy = mgr.translate("PCMWPERMITPREPEREDBYLOV: List+of+Prepared+by");
      lovTitleApprovedBy = mgr.translate("PCMWPERMITAPPROVEDBYLOV: List+of+Approved+by");
      lovTitleInquiredBy = mgr.translate("PCMWPERMITREPORTEDBYLOV: List+of+Inquired+by");
      lovTitleWorkLeaderSign = mgr.translate("PCMWPERMITWORKLEADERSIGNLOV: List+of+Work+Leader+Signature");
      lovTitleDelimitationOrderNo = mgr.translate("PCMWPERMITDELIMITORDLOV: List+of+Isolation+Order+No");
      
      if (itemlay2.isVisible())
      {
         String permitTyp = headset.getRow().getValue("PERMIT_TYPE_ID");
         String sWhereStrForITEM2 = "ISOLATION_TYPE IN (SELECT isolation_type FROM conn_permit_isolation  u WHERE u.permit_type_id ='" + permitTyp + "')";
         mgr.getASPField("ITEM2_DELIMITATION_ORDER_NO").setLOVProperty("WHERE", sWhereStrForITEM2);
      }
      if (headlay.isFindLayout())
      {
         mgr.getASPField("PREPARED_BY").setDynamicLOV("EMPLOYEE_LOV",600,445);
         mgr.getASPField("PREPARED_BY").setLOVProperty("WHERE","COMPANY IS NOT NULL");
         
         mgr.getASPField("WORK_LEADER_SIGN").setDynamicLOV("EMPLOYEE_LOV",600,445);
         mgr.getASPField("WORK_LEADER_SIGN").setLOVProperty("WHERE","COMPANY IS NOT NULL");
         
         mgr.getASPField("RELEASED_BY").setDynamicLOV("EMPLOYEE_LOV",600,445);
         mgr.getASPField("RELEASED_BY").setLOVProperty("WHERE","COMPANY IS NOT NULL");
         
         mgr.getASPField("INQUIRE").setDynamicLOV("EMPLOYEE_LOV",600,445);
         mgr.getASPField("INQUIRE").setLOVProperty("WHERE","COMPANY IS NOT NULL");
         
         mgr.getASPField("APPROVED_BY").setDynamicLOV("EMPLOYEE_LOV",600,445);
         mgr.getASPField("APPROVED_BY").setLOVProperty("WHERE","COMPANY IS NOT NULL");
         
         mgr.getASPField("RETURNED_BY").setDynamicLOV("EMPLOYEE_LOV",600,445);
         mgr.getASPField("RETURNED_BY").setLOVProperty("WHERE","COMPANY IS NOT NULL");
         
      }
      
      if ( itemlay2.isNewLayout() || itemlay2.isEditLayout() )
      {
          headbar.disableCommand(headbar.DELETE);
          headbar.disableCommand(headbar.NEWROW);
          headbar.disableCommand(headbar.EDITROW);
          headbar.disableCommand(headbar.DELETE);
          headbar.disableCommand(headbar.DUPLICATEROW);
          headbar.disableCommand(headbar.FIND);
          headbar.disableCommand(headbar.BACKWARD);
          headbar.disableCommand(headbar.FORWARD);
      }
   }
   
   
   
   
   
//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "PCMWPERMITPERMIT: Permit";
   }
   
   protected String getTitle()
   {
      return "PCMWPERMITPERMIT: Permit";
   }
   
   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      
      printHiddenField("BUTTONVAL", "");
      appendToHTML(headlay.show());
      
      if (headlay.isSingleLayout())
         appendToHTML(tabs.showTabsInit());
      
      if (itemlay0.isVisible() && tabs.getActiveTab() == 1)
         appendToHTML(itemlay0.show());
      if (itemlay2.isVisible() && tabs.getActiveTab() == 2)
         appendToHTML(itemlay2.show());
      if (itemlay1.isVisible() && tabs.getActiveTab() == 3)
         appendToHTML(itemlay1.show());
      
      
      appendDirtyJavaScript("function validatePermitTypeId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("   setDirty();\n");
      appendDirtyJavaScript("   if( !checkPermitTypeId(i) ) return;\n");
      appendDirtyJavaScript("   window.status='Please wait for validation';\n");
      appendDirtyJavaScript("   r = __connect(\n");
      appendDirtyJavaScript("       '");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=PERMIT_TYPE_ID'\n");
      appendDirtyJavaScript("       + '&PERMIT_TYPE_ID=' + URLClientEncode(getValue_('PERMIT_TYPE_ID'))\n");
      appendDirtyJavaScript("       + '&DBVALUE=' + URLClientEncode(getValue_('DBVALUE'))\n");
      appendDirtyJavaScript("       );\n");
      appendDirtyJavaScript("   window.status='';\n");
      appendDirtyJavaScript("   if( checkStatus_(r,'PERMIT_TYPE_ID',i,'Permit Type') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('CONNECTTYPE',i,0);\n");
      appendDirtyJavaScript("		assignValue_('CLIENTVAL2',i,1);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("   if (document.form.CONNECTTYPE.value == document.form.CLIENTVAL2.value)\n");
      appendDirtyJavaScript("      f.DELIMITATION_ORDER_NO.readOnly = false;\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("      f.DELIMITATION_ORDER_NO.readOnly = true;\n");
      appendDirtyJavaScript("}\n");
      
      appendDirtyJavaScript("function lovDelimitationOrderNo(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   laymode = '");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(lay));	//XSS_Safe AMNILK 20070718
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("   if (document.form.PERMIT_TYPE_ID.value == \"\")\n");
      appendDirtyJavaScript("       openLOVWindow('DELIMITATION_ORDER_NO',i,\n");
      appendDirtyJavaScript("       '");
      appendDirtyJavaScript(url);
      appendDirtyJavaScript("'+'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DELIMITATION_ORDER&__FIELD=");
      appendDirtyJavaScript(fldTitleDelimitationOrderNo);
      appendDirtyJavaScript("&__TITLE=");
      appendDirtyJavaScript(lovTitleDelimitationOrderNo);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("       ,600,450,'validateDelimitationOrderNo');\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("        if ((document.form.CONNECTTYPE.value == document.form.CLIENTVAL2.value) ||(laymode != \"new\"))\n");
      appendDirtyJavaScript("           openLOVWindow('DELIMITATION_ORDER_NO',i,\n");
      appendDirtyJavaScript("           '");
      appendDirtyJavaScript(url);
      appendDirtyJavaScript("'+'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=DELIMITATION_ORDER&__FIELD=");
      appendDirtyJavaScript(fldTitleDelimitationOrderNo);
      appendDirtyJavaScript("&__TITLE=");
      appendDirtyJavaScript(lovTitleDelimitationOrderNo);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("           ,600,450,'validateDelimitationOrderNo');\n");
      appendDirtyJavaScript("        else            \n");
      appendDirtyJavaScript("           window.alert('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPERMITNOLOVFAS: No Lov Facility."));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("}\n");
      
      
      appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(validto));	//XSS_Safe AMNILK 20070718
      appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"my_cookie\")==\"TRUE\")\n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript("      if (confirm(\"");
      appendDirtyJavaScript(mgr.translate("PCMWVALIDFROMDATE: Valid from date must be greater than plan finish establishment time and less than plan start reestablishment time is due to the connected isolation order.Still want to save this Record?"));
      appendDirtyJavaScript("\")) {\n");
      appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"VALIDTOOK\";\n");
      appendDirtyJavaScript("		writeCookie(\"my_cookie\", \"FALSE\", '', COOKIE_PATH); \n");
      appendDirtyJavaScript("		f.submit();\n");
      appendDirtyJavaScript("     } \n");
      
      appendDirtyJavaScript(" else");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"VALIDTOCAN\";\n");
      appendDirtyJavaScript("		f.submit();\n");
      appendDirtyJavaScript("     } \n");
      
      appendDirtyJavaScript("} \n");
      
      
      appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(valid));		//XSS_Safe AMNILK 20070718
      appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"my_cookie\")==\"TRUE\")\n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript("      if (confirm(\"");
      appendDirtyJavaScript(mgr.translate("PCMWAVALIDTODATE: Valid to date must be greater than plan finish establishment time and less than plan start reestablishment time is due to the connected isolation order.Still want to save this Record?"));
      appendDirtyJavaScript("\")) {\n");
      appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"VALIDOK\";\n");
      appendDirtyJavaScript("		writeCookie(\"my_cookie\", \"FALSE\", '', COOKIE_PATH); \n");
      appendDirtyJavaScript("		f.submit();\n");
      appendDirtyJavaScript("     } \n");
      
      appendDirtyJavaScript(" else");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"VALIDCANCEL\";\n");
      appendDirtyJavaScript("		f.submit();\n");
      appendDirtyJavaScript("     } \n");
      
      appendDirtyJavaScript("} \n");
      
      appendDirtyJavaScript("window.name = \"frmPermit\";\n");
      
      if (bOpenNewWindow)
      {
         appendDirtyJavaScript("  window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));      //XSS_Safe AMNILK 20070718
         appendDirtyJavaScript("\", \"");
         appendDirtyJavaScript(newWinHandle);
         appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");
      }
      
   }
}
