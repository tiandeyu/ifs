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
*  File        : PersonalInfoOvw.java 
*  Modified    :
*    ASP2JAVA Tool  2001-01-22  - Created Using the ASP file PersonalInfoOvw.asp
*    Disali K. : 2001-02-09 - Made the file compilable after converting to Java
*   KuPelk     : 2004-08-17   Added multirow Select.
*   Jakalk     : 2005-09-07 - Code Cleanup, Removed doReset and clone methods.  
*   Thpelk     : 2007-08-02   Call Id 146997, Corrected PERSON_ID to allow only 20 characters.
*   Kanslk     : 2007-11-30   Bug 65132, Modified adjust() so that OBJVERSION of the row gets updated everytime a new row is inserted.
*   Kanslk     : 2008-07-11 - Bug 73244, Modified details() so that query get corrected.
*   Hiralk     : 2008-09-29 - Bug 76755, Added  saveReturn(). Modified preDefine() and setDynamicRmb()
*   Fashlk     : 2008-10-28 - Bug 77886, Changed View used to "PERSON_INFO_ALL"
* ----------------------------------------------------------------------------
*/


package ifs.enterw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.util.Str;
import ifs.fnd.*;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PersonalInfoOvw extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.enterw.PersonalInfoOvw");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   
   
   //-----------------------------------------------------------------------------
   //---------- Item Instances created on page creation --------
   //-----------------------------------------------------------------------------

   private ASPBlock general_org_pos_person_blk;
   private ASPRowSet general_org_pos_person_set;
   private ASPCommandBar general_org_pos_person_bar;
   private ASPTable general_org_pos_person_tbl;
   private ASPBlockLayout general_org_pos_person_lay;

   private ASPBlock person_zone_blk;
   private ASPRowSet person_zone_set;
   private ASPCommandBar person_zone_bar;
   private ASPTable person_zone_tbl;
   private ASPBlockLayout person_zone_lay;

   private ASPBlock person_project_blk;
   private ASPRowSet person_project_set;
   private ASPCommandBar person_project_bar;
   private ASPTable person_project_tbl;
   private ASPBlockLayout person_project_lay;
   
   private ASPTabContainer tabs;
   
   private String custom_query;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   // Bug 76755, Begin 
   private String company;
   // Bug 76755, End

   //===============================================================
   // Construction 
   //===============================================================
   public PersonalInfoOvw(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }


   public void run() 
   {
      ASPManager mgr = getASPManager();
      // Bug 76755, Begin
      ASPContext ctx = mgr.getASPContext();
      company = ctx.getGlobal("COMPANY");
      // Bug 76755, End

      trans = mgr.newASPTransactionBuffer();

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (mgr.buttonPressed("CUSTOMQUERY"))
      {
         ASPQuery qry = trans.addQuery(headblk);
         String qry_where = qry.getWhereCondition();
         ASPBuffer qry_data_binding = qry.getDataBinding();
         if (qry_data_binding != null)
         {
            for (int i = 0; i < qry_data_binding.countItems(); i++)
               // qry_where.replaceFirst("?", qry_data_binding.getValueAt(i));
               qry_where = Str.replaceFirst(Str.replaceFirst(qry_where, "?", "'?'"), "?", qry_data_binding.getValueAt(i));
            custom_query = qry_where;
         }
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("PERSON_ID")))
         okFind();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else 
         okFind();   
      adjust();
      tabs.saveActiveTab();
   }
   
   public String replaceFirst(String str, String substr, String with)
   {
      if (str == null || substr == null)
         return null;
      if (str.length() == 0 || substr.length() == 0)
         return str;

      StringBuffer buf = null;
      int i = 0, j;

      j = str.indexOf(substr, i);
      if (j < 0)
         return str;
      else {
         if (with == null)
            return null;
         buf = new StringBuffer(10 + str.length());
         buf.append(str.substring(i, j));
         buf.append(with);
         buf.append(str.substring(j + substr.length()));
         return buf.toString();
      }
   }
   
   //-----------------------------------------------------------------------------
   //-----------------------------  VALIDATE FUNCTION  ---------------------------
   //-----------------------------------------------------------------------------
   public void  validate()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      String val = mgr.readValue("VALIDATE");
      
      String txt = "";
      
      if ("ZONE_NO".equals(val))
      {
         String zone_no = mgr.readValue("ZONE_NO");
         ASPCommand cmd = trans.addCustomFunction("GETZONEDESC", "General_Zone_API.Get_Zone_Desc", "ZONE_DESC");
         cmd.addParameter("ZONE_NO", zone_no);
         cmd = trans.addCustomFunction("GETPROJNO", "General_Zone_API.Get_Proj_No", "ZONE_PROJ_NO");
         cmd.addParameter("ZONE_NO", zone_no);
         cmd = trans.addCustomFunction("GETPROJDESC", "General_Zone_API.Get_Proj_Desc", "ZONE_PROJ_DESC");
         cmd.addParameter("ZONE_NO", zone_no);
         
         trans = mgr.validate(trans);
         
         String zone_desc = trans.getValue("GETZONEDESC/DATA/ZONE_DESC");
         String zone_proj_no = trans.getValue("GETPROJNO/DATA/ZONE_PROJ_NO");
         String zone_proj_desc = trans.getValue("GETPROJDESC/DATA/ZONE_PROJ_DESC");
         
         txt = (mgr.isEmpty(zone_desc) ? "" : zone_desc) + "^" +
         (mgr.isEmpty(zone_proj_no) ? "" : zone_proj_no) + "^" +
         (mgr.isEmpty(zone_proj_desc) ? "" : zone_proj_desc) + "^";
         mgr.responseWrite(txt);
      }
      else if ("VALID_FROM".equals(val))
      {
         String valid_from = mgr.readValue("VALID_FROM");
         Date valid_from_date = strToDate(valid_from);
         valid_from_date.setDate(valid_from_date.getDate() + 1);
         mgr.responseWrite(dateToStr(valid_from_date) + "^");
      }
      mgr.endResponse();
   }

   public Date strToDate(String strDate)
   {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
      ParsePosition pos = new ParsePosition(0);
      Date strtodate = formatter.parse(strDate, pos);
      return strtodate;
   }
   
   public String dateToStr(Date date)
   {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
      return formatter.format(date);
   }
    

//=============================================================================
//  Command Bar Edit Group functions
//=============================================================================

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      ASPQuery q = null;
      q = trans.addQuery(headblk);
      q.setOrderByClause("PERSON_ID");
      q.includeMeta("ALL");

      mgr.querySubmit(trans,headblk);

      if (  headset.countRows() == 0 )
      {
         mgr.showAlert("ENTERWPERSONALINFOOVWNODATA: No data found.");
         headset.clear();
      }
      okFindITEM1();
      okFindITEM2();
      okFindITEM3();
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      ASPQuery q = null;
      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getValue("N")));
      headset.clear();
   }


   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      ASPCommand cmd = null;
      ASPBuffer data = null;
      cmd = trans.addEmptyCommand("MAIN","Person_Info_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("MAIN/DATA");
      headset.addRow(data);
   }

   // 76755, Begin
   public void saveReturn()
   {   
      ASPManager mgr = getASPManager();
      ASPCommand cmd = null;
      boolean newrecord = false;
      
      headset.changeRow();
      if ("New__".equals(headset.getRowStatus()))
      {
          newrecord = true;   
      }
      mgr.submit(trans);
      trans.clear();

      if (newrecord) 
      {
          cmd = trans.addCustomCommand("PERSONCOMPANY", "PERSON_COMPANY_ACCESS_API.New");
          cmd.addParameter("COMPANY", company);
          cmd.addParameter("PERSON_ID", mgr.readValue("PERSON_ID"));
          trans = mgr.perform(trans);
          trans.clear();
      }
   }
   // 76755, End
   
   //-----------------------------------------------------------------------------
   //------------------------  Item block cmd bar functions  ---------------------------
   //-----------------------------------------------------------------------------

   public void okFindITEM1()
   {
      ASPManager mgr = getASPManager();
      if (headset.countRows() == 0)
         return;
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;

      q = trans.addQuery(general_org_pos_person_blk);
      q.addWhereCondition("PERSON_ID = ?");
      q.addParameter("PERSON_ID", headset.getValue("PERSON_ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,general_org_pos_person_blk);
      headset.goTo(headrowno);
   }
   
   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;
      
      cmd = trans.addEmptyCommand("ITEM1","GENERAL_ORG_POS_PERSON_API.New__",general_org_pos_person_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM0_PERSON_ID", headset.getValue("PERSON_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      general_org_pos_person_set.addRow(data);
   }
   
   public void okFindITEM2()
   {
      ASPManager mgr = getASPManager();
      
      if (headset.countRows() == 0)
         return;
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;

      q = trans.addQuery(person_zone_blk);
      q.addWhereCondition("PERSON_ID = ?");
      q.addParameter("PERSON_ID", headset.getValue("PERSON_ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,person_zone_blk);
      headset.goTo(headrowno);
   }
   
   public void newRowITEM2()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      cmd = trans.addEmptyCommand("ITEM2","PERSON_ZONE_API.New__",person_zone_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM1_PERSON_ID", headset.getValue("PERSON_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM2/DATA");
      person_zone_set.addRow(data);
   }
   
   public void okFindITEM3()
   {
      ASPManager mgr = getASPManager();
      
      if (headset.countRows() == 0)
         return;
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;

      q = trans.addQuery(person_project_blk);
      q.addWhereCondition("PERSON_ID = ?");
      q.addParameter("PERSON_ID", headset.getValue("PERSON_ID"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,person_project_blk);
      headset.goTo(headrowno);
   }
   
   public void newRowITEM3()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;
      
      cmd = trans.addEmptyCommand("ITEM3","PERSON_PROJECT_API.New__",person_project_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM2_PERSON_ID", headset.getValue("PERSON_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM3/DATA");
      person_project_set.addRow(data);
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  details()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer buff = null;
      ASPBuffer row = null;

      if (headlay. isMultirowLayout())
      {
         headset.store();
         mgr.transferDataTo("PersonInfo.page",
                            headset.getSelectedRows("PERSON_ID"));
      }
      else
      {
         buff = mgr.newASPBuffer();
         row = buff.addBuffer("1");

         //Bug 73244, Begin
         row.addItem("PERSON_ID",headset.getValue("PERSON_ID"));
         //Bug 73244, End
         mgr.transferDataTo("PersonInfo.page",buff);
      } 

   }

//-----------------------------------------------------------------------------
//----------------------------  DYNAMIC RMB FUNCTION  ------------------------------
//-----------------------------------------------------------------------------

   public void  setDynamicRmb()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer sec = null;
      trans.clear();
      // Bug 76755, Begin, Changed PERSON_INFO_ADDRESS to PERSON_INFO_ADDRESS1 
      trans.addSecurityQuery("PERSON_INFO,PERSON_INFO_ADDRESS1,PERSON_INFO_ADDRESS_TYPE,PERSON_INFO_COMM_METHOD");
      // Bug 76755, End
      trans = mgr.perform(trans);

      sec = trans.getSecurityInfo();
      
      // Bug 76755, Begin, Changed PERSON_INFO_ADDRESS to PERSON_INFO_ADDRESS1 
      if ((!sec.itemExists("PERSON_INFO")) || (!sec.itemExists("PERSON_INFO_ADDRESS1"))  || (!sec.itemExists("PERSON_INFO_ADDRESS_TYPE")) || (!sec.itemExists("PERSON_INFO_COMM_METHOD")))
      {
         headbar.removeCustomCommand("details"); 
         headbar.removeCommandGroup(headbar.CMD_GROUP_CUSTOM);
      }
      // Bug 76755, End
   }
   
//   public void createZone() throws FndException 
//   {
//      ASPManager mgr = getASPManager();
//      headset.store();
//      
//      String __parent_params = mgr.URLEncode("PERSON_ID@" + headset.getValue("PERSON_ID") + "^");
//      String root_path = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");
//      String page_url = root_path + "common/scripts/DynamicSel.page?__DYNAMIC_LOV_VIEW=GENERAL_ZONE"
//                        + "&__FIELD=" + mgr.URLEncode(mgr.translate("ENTERWPIOGENZONE: General Zone"))
//                        + "&__PARENT_PARAMS=" + __parent_params
//                        + "&__FIELD_PARAMS=ZONE_NO"
//                        + "&__TARGET_FIELDS=ZONE_NO"
//                        + "&__TARGET_PKG=PERSON_ZONE_API"
//                        + "&__ORDER_BY=ZONE_NO";
//                        // + "&__WHERE=" + mgr.URLEncode("ZONE_NO LIKE '%CNI23%'");
//      callNewWindow(page_url);
//   }
   
//   public void createProj() throws FndException 
//   {
//      ASPManager mgr = getASPManager();
//      headset.store();
//      
//      String __parent_params = mgr.URLEncode("PERSON_ID" + String.valueOf((char)31) + headset.getValue("PERSON_ID") + String.valueOf((char)30));
//      String root_path = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");
//      String page_url = root_path + "common/scripts/DynamicSel.page?__DYNAMIC_LOV_VIEW=GENERAL_PROJECT"
//                        + "&__FIELD=" + mgr.URLEncode(mgr.translate("ENTERWPIOGENPROJ: General Project"))
//                        + "&__PARENT_PARAMS=" + __parent_params
//                        + "&__FIELD_PARAMS=PROJ_NO"
//                        + "&__ORDER_BY=PROJ_NO"
//                        + "&__MULTICHOICE=true";
//      callNewWindow(page_url);
//   }
   
   private void callNewWindow(String page) throws FndException 
   {
      appendDirtyJavaScript("showNewBrowser_('"+ page + "', 550, 550, 'YES'); \n");
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("MAIN");

      headblk.addField("OBJID").
         setHidden();

      headblk.addField("OBJVERSION").
         setHidden();

      headblk.addField("PROTECTED").
         setHidden();

      headblk.addField("PARTY_TYPE").
         setHidden();

      headblk.addField("DEFAULT_DOMAIN").
         setHidden();

      headblk.addField("PERSON_ID").
         setLabel("ENTERWPERSONALINFOOVWPERSONID: Identity").
         setMandatory().
         setReadOnly().
         setInsertable().
         setUpperCase().
         setSize(15).
         setMaxLength(20);

      headblk.addField("NAME").
         setLabel("ENTERWPERSONALINFOOVWNAME: Name").
         setMandatory().
         setSize(25);

      headblk.addField("DEFAULT_LANGUAGE").
         setLabel("ENTERWPERSONALINFOOVWDEFAULTLANGUAGE: Default Language").
         enumerateValues("ISO_LANGUAGE_API").
         unsetSearchOnDbColumn(). 
         setSelectBox().
         setSize(15);

      headblk.addField("COUNTRY").
         setLabel("ENTERWPERSONALINFOOVWCOUNTRY: Country").
         enumerateValues("ISO_COUNTRY_API").
         unsetSearchOnDbColumn().
         setSelectBox().
         setSize(12);

      headblk.addField("CREATION_DATE","Date").
         setLabel("ENTERWPERSONALINFOOVWCREATIONDATE: Creation Date").
         setMandatory().
         setReadOnly().
         setSize(15);

      headblk.addField("USER_ID").
         setLabel("ENTERWPERSONALINFOOVWUSERID: User Id.").
         setDynamicLOV("PERSON_INFO_FREE_USER",650,450).
         setUpperCase().
         setSize(25);

      // Bug 76755, Begin
      headblk.addField("COMPANY").
         setHidden().
         setFunction("' '");
      // Bug 76755, End

      // Bug 77886, Begin
      headblk.setView("PERSON_INFO_ALL");
      // Bug 77886, End
      headblk.defineCommand("PERSON_INFO_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.addCustomCommand("details", mgr.translate("ENTERWPERSONALINFOOVWDTAIL: Details..."));
      headbar.enableMultirowAction();
      // Bug 76755, Begin
      headbar.defineCommand(headbar.SAVERETURN,"saveReturn");
      headbar.addCustomCommand("CreateZone", mgr.translate("ENTERWPERSONALINFOOVWCREATEZONE: Create Zone..."), "../common/images/toolbar/" + mgr.getLanguageCode() + "/connectZone.gif", true);
      headbar.setCmdProperty("CreateZone", headbar.CMD_PRO_VIEW, "GENERAL_ZONE_LOV");
//      headbar.setCmdProperty("CreateZone", "FIELD", mgr.translate("DOCMAWDOCISSUELOV: General Zone List"));
      headbar.setCmdProperty("CreateZone", headbar.CMD_PRO_PARPA, "PERSON_ID");
      headbar.setCmdProperty("CreateZone", headbar.CMD_PRO_FIEPA, "ZONE_NO,'FALSE','FALSE','Create_Zone'");
      headbar.setCmdProperty("CreateZone", headbar.CMD_PRO_TARG_FIE, "ZONE_NO,DEF_CTL,NEW_CTL,NOTE");
      headbar.setCmdProperty("CreateZone", headbar.CMD_PRO_TARG_PKG, "PERSON_ZONE_API");
      
      headbar.setCmdProperty("CreateZone", "ADD_WHERE_TAR_VIEW", "PERSON_ZONE");
      headbar.setCmdProperty("CreateZone", "ADD_WHERE_TAR_KEYS", "PERSON_ID");
//      headbar.setCmdProperty("CreateZone", headbar.CMD_PRO_TARG_PKG, "PERSON_ZONE_API");
//      headbar.setCmdProperty("CreateZone", headbar.CMD_PRO_TARG_PKG, "PERSON_ZONE_API");
//      headbar.setCmdProperty("CreateZone", headbar.CMD_PRO_TARG_PKG, "PERSON_ZONE_API");
      
      
      headbar.addCustomCommand("CreateProj", mgr.translate("ENTERWPERSONALINFOOVWCREATEPROJ: Create Proj..."), "../common/images/toolbar/" + mgr.getLanguageCode() + "/connectProj.gif", true);
      headbar.setCmdProperty("CreateProj", headbar.CMD_PRO_VIEW, "GENERAL_PROJECT");
      headbar.setCmdProperty("CreateProj", headbar.CMD_PRO_PARPA, "PERSON_ID");
      headbar.setCmdProperty("CreateProj", headbar.CMD_PRO_FIEPA, "PROJ_NO,'FALSE','FALSE'");
      headbar.setCmdProperty("CreateProj", headbar.CMD_PRO_TARG_FIE, "PROJ_NO,DEF_CTL,NEW_CTL");
      headbar.setCmdProperty("CreateProj", headbar.CMD_PRO_TARG_PKG, "PERSON_PROJECT_API");
    
      
//      headbar.addCustomCommandGroup("CREATE", mgr.translate("ENTERWPERSONALINFOOVWCREATE: Create Operation"));
//      headbar.setCustomCommandGroup("CreateZone", "CREATE");
//      headbar.setCustomCommandGroup("CreateProj", "CREATE");
      
      // Bug 76755, End
      
      // Tab commands
      headbar.addCustomCommand("activateGopp", "OrgPos");
      headbar.addCustomCommand("activateZone", "Zone");
      headbar.addCustomCommand("activateProj", "Proj");

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("ENTERWPERSONALINFOOVWPERSONOVW: Overview Persons");
      headtbl.enableRowSelect();

      headlay = headblk.getASPBlockLayout();
      headlay.setFieldOrder("PERSON_ID");
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
      
      //
      // General Organization Position person
      //
      
      general_org_pos_person_blk = mgr.newASPBlock("ITEM1");
      general_org_pos_person_blk.addField("ITEM0_OBJID").
      setHidden().
      setDbName("OBJID");
      
      general_org_pos_person_blk.addField("ITEM0_OBJVERSION").
      setHidden().
      setDbName("OBJVERSION");
      
      general_org_pos_person_blk.addField("ITEM0_PERSON_ID").
      setDbName("PERSON_ID").
      setMandatory().
      setInsertable().
      setHidden().
      setLabel("GENERALORGPOSPERSONITEM0PERSONID: Person Id").
      setSize(20);
      
      // line 1
      general_org_pos_person_blk.addField("ORG_NO").
      setMandatory().
      setInsertable().
      setDynamicLOV("GENERAL_ORGANIZATION").
      setLOVProperty("WHERE", "ORG_TYPE_DB = 'DEP'").
      setLOVProperty("ORDER_BY", "ORG_SEQ").
      setLabel("GENERALORGPOSPERSONORGNO: Org No").
      setSize(20);
      
      general_org_pos_person_blk.addField("ORG_DESC").
      setFunction("GENERAL_ORGANIZATION_API.GET_ORG_DESC(:ORG_NO)").
      setReadOnly().
      setLabel("GENERALORGPOSPERSONORGDESC: Org Desc").
      setSize(20);
      mgr.getASPField("ORG_NO").setValidation("ORG_DESC");
      
      general_org_pos_person_blk.addField("POS_NO").
      setMandatory().
      setInsertable().
      setDynamicLOV("GENERAL_ORG_POS", "ORG_NO").
      setLOVProperty("ORDER_BY", "POS_SEQ").
      setLabel("GENERALORGPOSPERSONPOSNO: Pos No").
      setSize(20);
      
      general_org_pos_person_blk.addField("POS_DESC").
      setFunction("GENERAL_POSITION_API.GET_POS_DESC(:POS_NO)").
      setReadOnly().
      setLabel("GENERALORGPOSPERSONPOSDESC: Pos Desc").
      setSize(20);
      mgr.getASPField("POS_NO").setValidation("POS_DESC");
      
      // line 2
      general_org_pos_person_blk.addField("VALID_FROM","Date").
      setInsertable().
      setLabel("GENERALORGPOSPERSONVALIDFROM: Valid From").
      setCustomValidation("VALID_FROM", "VALID_DUE").
      setSize(10);
      
      general_org_pos_person_blk.addField("VALID_DUE","Date").
      setInsertable().
      setLabel("GENERALORGPOSPERSONVALIDDUE: Valid Due").
      setSize(10);

      // line 3
      general_org_pos_person_blk.addField("MAIN_POS").
      setInsertable().
      setCheckBox("FALSE,TRUE").
      setLabel("GENERALORGPOSPERSONMAINPOS: Main Pos").
      setSize(5);
      
      general_org_pos_person_blk.addField("PERSON_SEQ", "Number").
      setInsertable().
      setLabel("GENERALORGPOSPERSONPERSONSEQ: Person Seq").
      setSize(10);
      
      // line 4
      general_org_pos_person_blk.addField("NOTE").
      setInsertable().
      setLabel("GENERALORGPOSPERSONNOTE: Note").
      setHeight(3).
      setSize(50);
      
      general_org_pos_person_blk.setView("GENERAL_ORG_POS_PERSON");
      general_org_pos_person_blk.defineCommand("GENERAL_ORG_POS_PERSON_API", "New__,Modify__,Remove__");
      general_org_pos_person_blk.setMasterBlock(headblk);
      general_org_pos_person_set = general_org_pos_person_blk.getASPRowSet();
      general_org_pos_person_bar = mgr.newASPCommandBar(general_org_pos_person_blk);
      general_org_pos_person_bar.defineCommand(general_org_pos_person_bar.OKFIND, "okFindITEM1");
      general_org_pos_person_bar.defineCommand(general_org_pos_person_bar.NEWROW, "newRowITEM1");
      general_org_pos_person_tbl = mgr.newASPTable(general_org_pos_person_blk);
      general_org_pos_person_tbl.setTitle("GENERALORGPOSPERSONITEMHEAD1: GeneralOrgPosPerson");
      general_org_pos_person_tbl.enableRowSelect();
      general_org_pos_person_tbl.setWrap();
      general_org_pos_person_lay = general_org_pos_person_blk.getASPBlockLayout();
      general_org_pos_person_lay.setDefaultLayoutMode(general_org_pos_person_lay.MULTIROW_LAYOUT);
      general_org_pos_person_lay.setSimple("ORG_DESC");
      general_org_pos_person_lay.setSimple("POS_DESC");
      
      //
      // Person Zone
      //
      
      person_zone_blk = mgr.newASPBlock("ITEM2");
      person_zone_blk.addField("ITEM1_OBJID").
      setHidden().
      setDbName("OBJID");
      
      person_zone_blk.addField("ITEM1_OBJVERSION").
      setHidden().
      setDbName("OBJVERSION");
      
      person_zone_blk.addField("ITEM1_PERSON_ID").
      setDbName("PERSON_ID").
      setMandatory().
      setInsertable().
      setHidden().
      setLabel("PERSONZONEITEM1PERSONID: Person Id").
      setSize(20);
      
      // line 1
      person_zone_blk.addField("ZONE_NO").
      setMandatory().
      setInsertable().
      setDynamicLOV("GENERAL_ZONE_LOV").
      setCustomValidation("ZONE_NO", "ZONE_DESC,ZONE_PROJ_NO,ZONE_PROJ_DESC").
      setLabel("PERSONZONEZONENO: Zone No").
      setSize(20);
      
      person_zone_blk.addField("ZONE_DESC").
      setFunction("General_Zone_API.Get_Zone_Desc(:ZONE_NO)").
      setReadOnly().
      setLabel("PERSONZONEZONEDESC: Zone Desc").
      setSize(20);
      // mgr.getASPField("ZONE_NO").setValidation("ZONE_DESC");
      
      person_zone_blk.addField("ZONE_PROJ_NO").
      setReadOnly().
      setFunction("General_Zone_API.Get_Proj_No(:ZONE_NO)").
      setLabel("PERSONZONEPROJNO: Proj No").
      setSize(20);
      
      person_zone_blk.addField("ZONE_PROJ_DESC").
      setFunction("General_Zone_API.Get_Proj_Desc(:ZONE_NO)").
      setReadOnly().
      setLabel("PERSONZONEPROJDESC: Proj Desc").
      setSize(20);
      
      // line 2
      person_zone_blk.addField("DEF_CTL").
      setInsertable().
      setCheckBox("FALSE,TRUE").
      setLabel("PERSONZONEDEFCTL: Def Ctl").
      setSize(5);
      
      person_zone_blk.addField("NEW_CTL").
      setInsertable().
      setCheckBox("FALSE,TRUE").
      setLabel("PERSONZONENEWCTL: New Ctl").
      setSize(5);
      
      // line 3
      person_zone_blk.addField("ITEM1_NOTE").
      setDbName("NOTE").
      setInsertable().
      setLabel("PERSONZONEITEM1NOTE: Note").
      setHeight(3).
      setSize(50);
      
      person_zone_blk.setView("PERSON_ZONE");
      person_zone_blk.defineCommand("PERSON_ZONE_API","New__,Modify__,Remove__");
      person_zone_blk.setMasterBlock(headblk);
      person_zone_set = person_zone_blk.getASPRowSet();
      person_zone_bar = mgr.newASPCommandBar(person_zone_blk);
      person_zone_bar.defineCommand(person_zone_bar.OKFIND, "okFindITEM2");
      person_zone_bar.defineCommand(person_zone_bar.NEWROW, "newRowITEM2");
      person_zone_tbl = mgr.newASPTable(person_zone_blk);
      person_zone_tbl.setTitle("PERSONZONEITEMHEAD2: PersonZone");
      person_zone_tbl.enableRowSelect();
      person_zone_tbl.setWrap();
      person_zone_lay = person_zone_blk.getASPBlockLayout();
      person_zone_lay.setDefaultLayoutMode(person_zone_lay.MULTIROW_LAYOUT);
      person_zone_lay.setSimple("ZONE_DESC");
      person_zone_lay.setSimple("ZONE_PROJ_DESC");
      
      //
      // Person Project
      //

      person_project_blk = mgr.newASPBlock("ITEM3");
      person_project_blk.addField("ITEM2_OBJID").
      setHidden().
      setDbName("OBJID");
      
      person_project_blk.addField("ITEM2_OBJVERSION").
      setHidden().
      setDbName("OBJVERSION");
      
      person_project_blk.addField("ITEM2_PERSON_ID").
      setDbName("PERSON_ID").
      setMandatory().
      setInsertable().
      setHidden().
      setLabel("PERSONPROJECTITEM2PERSONID: Person Id").
      setSize(20);
      
      // line 1
      person_project_blk.addField("PROJ_NO").
      setMandatory().
      setInsertable().
      setDynamicLOV("GENERAL_PROJECT").
      setLabel("PERSONPROJECTPROJNO: Proj No").
      setSize(20);
      
      person_project_blk.addField("PROJ_DESC").
      setFunction("GENERAL_PROJECT_API.GET_PROJ_DESC(:PROJ_NO)").
      setReadOnly().
      setLabel("PERSONPROJECTPROJDESC: Proj Desc").
      setSize(20);
      mgr.getASPField("PROJ_NO").setValidation("PROJ_DESC");
      
      person_project_blk.addField("ITEM2_DEF_CTL").
      setDbName("DEF_CTL").
      setInsertable().
      setCheckBox("FALSE,TRUE").
      setLabel("PERSONPROJECTITEM2DEFCTL: Def Ctl").
      setSize(5);
      
      // line 2
      person_project_blk.addField("ITEM2_NEW_CTL").
      setDbName("NEW_CTL").
      setInsertable().
      setCheckBox("FALSE,TRUE").
      setLabel("PERSONPROJECTITEM2NEWCTL: New Ctl").
      setSize(5);
      
      person_project_blk.addField("ITEM2_NOTE").
      setDbName("NOTE").
      setInsertable().
      setLabel("PERSONPROJECTITEM2NOTE: Note").
      setHeight(3).
      setSize(50);
      
      person_project_blk.setView("PERSON_PROJECT");
      person_project_blk.defineCommand("PERSON_PROJECT_API", "New__,Modify__,Remove__");
      person_project_blk.setMasterBlock(headblk);
      person_project_set = person_project_blk.getASPRowSet();
      person_project_bar = mgr.newASPCommandBar(person_project_blk);
      person_project_bar.defineCommand(person_project_bar.OKFIND, "okFindITEM3");
      person_project_bar.defineCommand(person_project_bar.NEWROW, "newRowITEM3");
      person_project_tbl = mgr.newASPTable(person_project_blk);
      person_project_tbl.setTitle("PERSONPROJECTITEMHEAD3: PersonProject");
      person_project_tbl.enableRowSelect();
      person_project_tbl.setWrap();
      person_project_lay = person_project_blk.getASPBlockLayout();
      person_project_lay.setDefaultLayoutMode(person_project_lay.MULTIROW_LAYOUT);
      
      tabs = mgr.newASPTabContainer();
      tabs.addTab(mgr.translate("ENTERWPERSONINFOOVWGOPP: Org and Pos"), "javascript:commandSet('MAIN.activateGopp','')");
      tabs.addTab(mgr.translate("ENTERWPERSONINFOOVWPZ: Zone"), "javascript:commandSet('MAIN.activateZone','')");
      tabs.addTab(mgr.translate("ENTERWPERSONINFOOVWPP: Project"), "javascript:commandSet('MAIN.activateProj','')");
      
      tabs.setContainerWidth(700);
      tabs.setLeftTabSpace(1);
      tabs.setContainerSpace(5);
      tabs.setTabWidth(100);
      
   }


   public void  adjust()
   {
      //Bug 65132, Begin
      int rowcount  = headset.countRows();
      int iteration = 51;
      //Bug 65132, End
      setDynamicRmb();
      if (rowcount == 0)
      {
         headbar.disableCommand(headbar.FORWARD);
         headbar.disableCommand(headbar.BACKWARD);
         headbar.disableCommand(headbar.DELETE);
         headbar.disableCommand(headbar.DUPLICATEROW);
         headbar.disableCommand(headbar.EDITROW);
         headbar.disableCommand(headbar.BACK);
         headbar.removeCustomCommand("details");

      }

      //Bug 65132, Begin
      if (headlay.isMultirowLayout())
      {
          if(rowcount>0 && rowcount<=50)
          {
              headset.refreshAllRows();
          }
          else if(rowcount > 50 )
          {
              for(int i = iteration;i <= rowcount;i++)
              {
                  int version = toInt(headset.getValueAt((i-1),"OBJVERSION"));
                  if(version==1)
                     headset.setValueAt((i-1),"OBJVERSION","2");
              }
              iteration++;
          }
      }
      //Bug 65132, End

      headbar.removeCustomCommand("activateGopp");
      headbar.removeCustomCommand("activateZone");
      headbar.removeCustomCommand("activateProj");
   }
   
   public void activateGopp()
   {
      tabs.setActiveTab(1);
      okFindITEM1();
   }
   
   public void activateZone()
   {
      tabs.setActiveTab(2);
      okFindITEM2();
   }
   
   public void activateProj()
   {
      tabs.setActiveTab(3);
      okFindITEM3();
   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "ENTERWPERSONALINFOOVWTITLE: Overview Persons";
   }

   protected String getTitle()
   {
      return "ENTERWPERSONALINFOOVWTITLE: Overview Persons";
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
      
      if ((headlay.isSingleLayout() || headlay.isCustomLayout()) && headset.countRows() > 0)
      {
         appendToHTML(tabs.showTabsInit());
         if (tabs.getActiveTab() == 1)
            appendToHTML(general_org_pos_person_lay.show());
         else if (tabs.getActiveTab() == 2)
            appendToHTML(person_zone_lay.show());
         else if (tabs.getActiveTab() == 3)
            appendToHTML(person_project_lay.show());
      }
   }
}
