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
 *  File                    : CompanyEmployees.java
 *  Description             :
 *  Notes                   :
 * ----------------------- Wings Merge Start ------------------------------------
 *  Created    : 2006-11-08   Haunlk   Created.
 *  Modify     : 2007-01-17   Haunlk   Modified Some of the Transation Constants.
 *               2007-01-22   Haunlk   B129741 Added the disableDocMan to disable the RMB option "Document".
 *               2007-01-22   Haunlk   B129737 Modified the enumerateValues at HEAD_COUNTRY and DEFAULT_LANGUAGE.
 *               2007-01-22   Haunlk   B129737 Added setInsertable() option for some fields in employeeblk to have the duplicate RMB working.
 *               2007-01-31   Haunlk   Merged Wings Code.
 *               2007-08-02   Thpelk   Call Id 146997, Corrected EMPLOYEE_ID to allow only 11 characters.
 * ----------------------- Wings Merge End --------------------------------------
 * -------------------------------------------------------------------------------
 */


package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class CompanyEmployees extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.CompanyEmployees");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPContext      ctx;
   private ASPLog log;

   private ASPBlock        headblk;
   private ASPRowSet       headset;
   private ASPCommandBar   headbar;
   private ASPTable        headtbl;
   private ASPBlockLayout  headlay;

   private ASPBlock        employeeblk;
   private ASPRowSet       employeeset;
   private ASPCommandBar   employeebar;
   private ASPTable        employeetbl;
   private ASPBlockLayout  employeelay;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPTabContainer tabs;
   private ASPQuery qry;
   private int i;
   private int arraySize;

   private String val;
   private ASPCommand cmd;
   private String name;
   private ASPBuffer buf;
   private ASPQuery q;
   private ASPBuffer data;
   private String activetab;
   private String showAll = "TRUE";
   private final static String TRANSFER_PARAM_NAME = "__TRANSFER";
   private String url = "";
   private boolean showWindow = false;

   //===============================================================
   // Construction
   //===============================================================
   public CompanyEmployees(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run()
   {

      ASPManager mgr = getASPManager();
      ctx   = mgr.getASPContext();
      activetab  = ctx.readValue("ACTIVETAB","1");
      showAll = ctx.readValue("SHOWALL","FALSE");

      if ( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if ( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
         validate();

      adjust();

      activetab  = ctx.readValue("ACTIVETAB","1");
      tabs.saveActiveTab();
      ctx.writeValue("ACTIVETAB",activetab);
      ctx.writeValue("SHOWALL",showAll);
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      qry = trans.addQuery(headblk);
      qry.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getValue("N")));
      headset.clear();
   }


   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q = trans.addQuery(headblk);
      q.includeMeta("ALL");

      mgr.querySubmit(trans,headblk);
      if ( headset.countRows() == 0 )
      {
         mgr.showAlert(mgr.translate("ENTERWCOMPANYEMPLOYEENODATA: No data found."));
         headset.clear();
      }
      mgr.createSearchURL(headblk);
      eval(headset.syncItemSets());
   }

   public void  okFindEMPLOYEE()
   {
      ASPManager mgr = getASPManager();
      int headrowno;

      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q = trans.addQuery(employeeblk);
      q.addWhereCondition("COMPANY = ?");
      q.addParameter("COMPANY", headset.getValue("COMPANY"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,employeeblk);
      headset.goTo(headrowno);

   }
   public void  newRowEMPLOYEE()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      cmd = trans.addEmptyCommand("EMP","COMPANY_EMP_API.New__",employeeblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("COMPANY", headset.getValue("COMPANY"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("EMP/DATA");
      employeeset.addRow(data);
   }

   public void  validate()
   {
      ASPManager mgr = getASPManager();
      String val;
      String txt;
      ASPQuery qry;
      trans.clear();
      val = mgr.readValue("VALIDATE");
   }

   public void adjust()
   {
      ASPManager mgr = getASPManager();

      if ( headset.countRows() == 0 )
      {
         headbar.disableCommand(headbar.BACK);
         headbar.disableCommand(headbar.FORWARD);
         headbar.disableCommand(headbar.BACKWARD);
         headbar.disableCommand(headbar.DELETE);
         headbar.disableCommand(headbar.EDITROW);
         headbar.disableCommand(headbar.DUPLICATEROW);

      }
      if ( "TRUE".equals(showAll) )
      {
         employeebar.disableCommand("allEmp");
      }
      else
      {
         employeebar.disableCommand("validEmp");
      }

      // Remove tab commands from actions
      headbar.removeCustomCommand("activateEmployee");

      if ( employeelay.isNewLayout() )
      {
         mgr.getASPField("PERSON_ID").unsetReadOnly();
         mgr.getASPField("EMPLOYEE_ID").unsetReadOnly();
      }

   }

   public void activateEmployee()
   {
      tabs.setActiveTab(1);
      activetab = "1";
   }

   public void  validEmp()
   {
      ASPManager mgr = getASPManager();
      int headrowno;


      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q = trans.addQuery(employeeblk);
      q.addWhereCondition("COMPANY = ? AND ( EXPIRE_DATE > SYSDATE OR EXPIRE_DATE IS NULL) ");
      q.addParameter("COMPANY", headset.getValue("COMPANY"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,employeeblk);
      headset.goTo(headrowno);
      showAll = "FALSE";
   }

   public void  allEmp()
   {
      showAll = "TRUE";
      okFindEMPLOYEE();
   }

   public void prepareUrl()
   {
      ASPManager mgr = getASPManager();
      String sKeyStr;
      String strUrl = "../enterw/PersonInfo.page";
      char   sep;

      if ( employeelay.isMultirowLayout() )
      {
         employeeset.storeSelections();
         employeeset.setFilterOn();
      }
      else
         employeeset.selectRow();

      sKeyStr = mgr.pack(employeeset.getSelectedRows("PERSON_ID"));
      sep = strUrl.indexOf('?')>0 ? '&' : '?';
      url = strUrl + sep + TRANSFER_PARAM_NAME + "=" + sKeyStr;
      showWindow = true;
   }

   protected void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();
      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
      setHidden();

      headblk.addField("OBJVERSION").
      setHidden();

      headblk.addField("COMPANY").
      setUpperCase().
      setSize(10).
      setLabel("ENTERWCOMPANYEMPLOYEECOMPANY: Identity").
      setReadOnly();

      headblk.addField("PARTY_TYPE_DB").
      setHidden();

      headblk.addField("NAME").
      setLabel("ENTERWCOMPANYEMPLOYEENAME: Name").
      setSize(40);

      headblk.addField("ASSOCIATION_NO").
      setLabel("ENTERWCOMPANYEMPLOYEEASSOCIATION: Association No").
      setSize(20).
      setDynamicLOV("ASSOCIATION_INFO","PARTY_TYPE_DB");

      headblk.addField("CREATED_BY").
      setLabel("ENTERWCOMPANYEMPLOYEECREATEDBY: Created By").
      setReadOnly().
      setSize(10);

      headblk.addField("METHODS").
      setHidden().
      setFunction("''");

      headblk.addField("CREATION_FINISHED").
      setHidden().
      setFunction("Company_Finance_API.Get_Creation_Finished(:COMPANY)");

      //------------ General Tab ------------------------------

      headblk.addField("DEFAULT_LANGUAGE").
      setSize(20).
      setMandatory().
      setLabel("ENTERWCOMPANYEMPLOYEEDEFAULTLANGUAGE: Default Language").
      setSelectBox().
      enumerateValues("ISO_LANGUAGE_API");

      headblk.addField("HEAD_COUNTRY").
      setSize(20).setDbName("COUNTRY").
      setMandatory().
      setLabel("ENTERWCOMPANYEMPLOYEEHEADCOUNTRY: Country").
      setSelectBox().
      enumerateValues("ISO_COUNTRY_API");

      headblk.setView("COMPANY");
      headblk.defineCommand("COMPANY_API","Modify__");
      headblk.disableDocMan();

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(ASPCommandBar.DELETE);
      headbar.defineCommand(headbar.COUNTFIND,"countFind");
      // headbar.disableCommand(headbar.BACK);
      //headbar.disableCommand(headbar.FORWARD);
      //headbar.disableCommand(headbar.BACKWARD);
      headbar.disableCommand(headbar.DELETE);
      headbar.disableCommand(headbar.EDITROW);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.DUPLICATEROW);

      headtbl = mgr.newASPTable( headblk );
      headtbl.setTitle(mgr.translate("ENTERWCOMPANYEMPLOYEECOMPANYEMPTITLE: Company Employees"));

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);

      headlay.setDialogColumns(4);

      //----------------------- Employee block ---------------------

      employeeblk = mgr.newASPBlock("EMPLOYEE");

      employeeblk.addField("EMP_OBJID").
      setDbName("OBJID").
      setHidden();

      employeeblk.addField("EMP_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      employeeblk.addField("EMP_COMPANY").
      setDbName("COMPANY").
      setHidden();

      employeeblk.addField("EMPLOYEE_ID").	   
      setSize(20).
      setMaxLength(11).
      setLabel("ENTERWCOMPANYEMPLOYEESEMPEMPLOYEEID: Employee Id").
      setUpperCase().
      setReadOnly().
      setInsertable();

      employeeblk.addField("PERSON_ID").
      setSize(20).
      setLabel("ENTERWCOMPANYEMPLOYEESEMPPERSONID: Person Id").
      setDynamicLOV("PERSON_INFO").
      setReadOnly().
      setInsertable().
      setUpperCase();

      employeeblk.addField("PERSON_NAME").
      setSize(20).
      setFunction("PERSON_INFO_API.Get_Name(:PERSON_ID)").
      setLabel("ENTERWCOMPANYEMPLOYEESEMPPERSONNAME: Name").
      setInsertable().
      setReadOnly();

      mgr.getASPField("PERSON_ID").setValidation("PERSON_NAME");

      employeeblk.addField("EXPIRE_DATE","Date").
      setSize(20).
      setLabel("ENTERWCOMPANYEMPLOYEESEXPIREDATE: Expire Date");

      employeeblk.setView("COMPANY_EMP");
      employeeblk.defineCommand("COMPANY_EMP_API","New__,Modify__,Remove__");
      employeeblk.setMasterBlock(headblk);

      employeeset = employeeblk.getASPRowSet();

      employeebar = mgr.newASPCommandBar(employeeblk);
      employeebar.defineCommand(employeebar.OKFIND,"okFindEMPLOYEE");
      employeebar.defineCommand(employeebar.NEWROW,"newRowEMPLOYEE");
      employeebar.addCustomCommand("prepareUrl",mgr.translate("ENTERWEOMPANYEMPLOYEESPERSONDETAILS: Details..."));
      employeebar.addCustomCommand("validEmp",mgr.translate("ENTERWEOMPANYEMPLOYEESVALIDEMP: Show Employees Valid Today"));
      employeebar.addCustomCommand("allEmp",mgr.translate("ENTERWEOMPANYEMPLOYEESALLEMP: Show All Employees"));
      employeebar.enableMultirowAction();
      employeebar.forceEnableMultiActionCommand("validEmp");
      employeebar.forceEnableMultiActionCommand("allEmp");

      employeetbl = mgr.newASPTable( employeeblk );
      employeetbl.setTitle(mgr.translate("ENTERWCOMPANYEMPLOYEEEMPLOYEETITLE: Employee"));
      employeetbl.enableRowSelect();
      employeelay = employeeblk.getASPBlockLayout();
      employeelay.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);


      //------ Commands for tabs ----------------------
      headbar.addCustomCommand("activateEmployee",mgr.translate("ENTERWCOMPANYEMPLOYEEENPLOYEE: Employee"));

      // ----------------------------------------------------------------------
      //                         Tabs
      // ----------------------------------------------------------------------

      tabs = mgr.newASPTabContainer();
      tabs.setDirtyFlagEnabled(false);
      tabs.addTab(mgr.translate("COMPANYADDRESSTAB: Employee"),"javascript:commandSet('HEAD.activateEmployee','')");

   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "ENTERWCOMPANYEMPLOYEEDESCRIPTION: Company Employee";
   }

   protected String getTitle()
   {
      return "ENTERWCOMPANYEMPLOYEETITLE: Company Employee";
   }


   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      int activetab = tabs.getActiveTab();

      appendToHTML(headlay.show());

      if ( headset.countRows()>0 )
      {
         if ( headlay.isSingleLayout() )
         {
            if ( !(headlay.isNewLayout() || headlay.isEditLayout()) )
            {
               appendToHTML(tabs.showTabsInit());
               appendToHTML(employeelay.show());
               appendToHTML(tabs.showTabsFinish());
            }
         }
      }
      if ( showWindow )
      {
         appendDirtyJavaScript("    window.open(\"");
         appendDirtyJavaScript(url);
         appendDirtyJavaScript("\",\"ObjDetail\",\"status=yes,resizable=1,scrollbars=yes,left=100,top=100, screenX=150,screenY=100,width=800,height=500\"); \n");
         appendDirtyJavaScript("showWindow \n");
         appendDirtyJavaScript(" = false;   \n");
      }
   }
}
