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
*  File        : PmAction.java 
*  Created     : ASP2JAVA Tool  010215  Created Using the ASP file PmAction.asp
*  Modified    :    
*  JEWILK  010403  Changed file extensions from .asp to .page
*  CHCRLK  010613  Modified overwritten validations.
*  CHCRLK  010619  Changed supplierPerPart() method. 
*  INROLK  010625  Set width for popup "tbl3Pop" and "actionITEM1". call 66209.
*  VAGULK  010711  Modified Standard Job ID validation.(Call ID -66207)
*  BUNILK  010716  Modified PART_NO field validation. 
*  SHCHLK  010801  Corrected the Standard job id LOV & added Parameters, Measurements RMBs to the Criteria tab.
*  VAGULK  010806  Added the RMB Copy PmActions .(Call ID - 77818)
*  BUNILK  010806  Overwrote getContents() method and made some changes in return AutoString of getContents() function so that to call       
*                  checkPartVal() javascript function from onBlur event of PART_NO field. Done some changes in 
*                  validatePartNo() method also. 
*  VAGULK  010808  Call Id - 77818 Modified the js to pass the Pm_No 
*  VAGULK  010813  Changed the unescape() in the js as to pass the description in Netscape (66207)  
*  INROLK  010820  Added field Cost.and chaged validation of Planned Quantity..call id 77918.
*  CHCRLK  010821  Made fields in Customer Information section insertable and Total Budget Cost in Budget non editable.
*  INROLK  010822  Changed PART_NO validation to get cost.
*  ARWILK  010904  Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction(). 
*                  (Caused when cancelNew is performed after saveNew)
*  CHCRLK  010906  Removed alerts in methods okFindITEM0 and okFindITEM0. 
*  SHCHLK  011010  Corrected the RMB ViewGenerationDetails.
*  SHCHLK  011601  Did the security check.
*  VAGULK  020325  Removed the RMB Action - Report in Route WorkOrder 
*  SHAFLK  020502  Bug 29245,Changed RMB "Spare parts in object" so that it works when there are no records in itemset. 
*  SHAFLK  020509  Bug 29948,Changed HTML part and okFind().
*  SHAFLK  020509  Bug 29943,Made changes to refresh in same page after RMB retrn. 
*  BUNILK  020813  Bug 31978, Made changes in saveReturn() method so that to check validity of date entered 
*                  for Start Value if Start Unit is date.   
*  BUNILK  020906  Bug ID 32182 Added a new method isModuleInst() to check availability of 
*                  DOCMAN and ORDER module inside preDefine method.     
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
---------------------Generic WO-------------------------------------------
*  INROLK  021209  Added MCH_CODE_CONTRACT and LOV for MCH_CODE 
*  BUNILK  021213  Merged with 2002-3 SP3
*  BUNILK  030121  Code reviewed.
*  JEJALK  030331  Renamed Cratfs Tab to Operations and Row No to Operation No.
*  CHAMLK  030403  Added new tab Tools and Facilities.
*  CHCRLK  030502  Added CONDITION_CODE & CONDITION_CODE_DESC to Materials tab.
*  CHAMLK  030728  Added Part Ownership, Owner and Owner Name to Materials tab.
*  JEWILK  030926  Removed the commented out toggleStyleDisplay() function.
*  SHTOLK  031013  Call Id 106125, Made 'VENDOR_NO' & 'VENDORNAME' visible and removed field 'VENDORNAME1'. 
*  PAPELK  031016  Call Id 106124.Modified attributes() to display attributes 
*                  of a permit through Action in Separate PM action.
*  CHAMLK  031021  Added Tools/Facilities cost to the Budget tab.
*  CHCRLK  031023  Modified lov properties for field SIGNATURE & commented function lovSignature(i).
*  CHAMLK  031024  Modified functions getCost() and validate() to call method Purchase_Order_Line_Util_API.Get_Base_Price_Supp_Part 
*                  only when a supplier exists for the Purchase Part. 
*  JEWILK  031024  Changed the format mask of PM_NO as "#" in all blocks. Call 107474.
*  CHAMLK  031031  Modified function okFind to call the querySubmit instead of submit.
*  ARWILK  031211  Edge Developments - (Replaced blocks with tabs, Replaced links with RMB's, Removed clone and doReset Methods)
*  VAGULK  031219  Made the field order according to the order in the Centura application(Web Alignment).
*  ARWILK  031229  Edge Developments - (Replaced links with multirow RMB's)
*  ARWILK  040114  Edge Developments - (Enable Multirow RMB actions, Remove uneccessary global variables, Enhance Performance, Clean Javascript Code)
* ----------------------- EDGE - SP1 Merge -----------------------------------
*  BUNILK  040102  Corrected a Spelling error of method name Sales_Part_Exist_On_Pm_Site of Pm_Action_Tool_Facility_API which caused errors when 
*                  it is calling from validations of Tool Facility ID, Sale part no, Department and Site fields of Tools & Facilities tab.
*  SHAFLK  040120  Bug Id 41815,Removed Java dependencies. 
*  VAGULK  040324  Merged with SP1.
*  THWILK  040430  Modified run() method.
*  THWILK  040518  Removed STD_JOB_ID and other related fields,validations and javascript methods which was affected by the removal of STD_JOB_ID.
*  SAPRLK  040531  Added new RMB 'Jobs' to represent the jobs tab for IID AMEC109A, Multiple Standard Jobs on PMs.
*  THWILK  040531  Added JOB_ID lov,PM_REVISION and changed all the relevant places which was affected due to the key changes under 
*                  IID AMEC109A, Multiple Standard Jobs on PMs.
*  ARWILK  040709  Added ORG_CONTRACT to the headblk and itemblk0. (IID AMEC500C: Resource Planning)
*  NIJALK  040727  Added new parameter CONTRACT to method calls to Role_Sales_Part_API.Get_Def_Catalog_No.  
*  ARWILK  040819  Removed "Jobs" RMB and added new tab "Jobs".
*  NIJALK  040830  Call 117471, Modified predefine().
*  NIJALK  040901  Set the field "Organization site" in general tab Read Only.
*  ARWILK  040903  Modified preDefine.(IID AMEC111A: Std Jobs as PM Templates)
*  NIJALK  040914  Added Custom function createNewRevision(). Modified preDefine(). 
*  NIJALK  040920  Added RMB "Jobs and Operations" to maintenance Plan. Modified predefine(), validate(). Added new function jobsAndOperations().
*  NIJALK  040930  Modified preDefine().
*  NIJALK  041004  Renamed signature to 'Executed By' in jobs and operations tabs. modified validations of item2_job_id. 
*                  Added warning to prevent changing already planned signatures.
*  NIJALK  041011  Modified Jobs and Operations(), preDefine(), generateWorkOrder2().
*  ARWILK  041022  LOV Change for Fields STD_JOB_ID,STD_JOB_REVISION. (Spec AMEC111 - Standard Jobs as PM Templates)
*  NIJALK  041103  Modified predefine(), okFind(), validate().
*  NAMELK  041105  Duplicated Translation Tags Changed.
*  NIJALK  041111  Made the field "Employee ID" to visible & read only in Jobs and Operations tabs.
*  NIJALK  041115  Replaced method Standard_Job_API.Get_Work_Description with Standard_Job_API.Get_Definition.
*  NIJALK  041116  Replaced of Inventory_Part_Location_API methods from Inventory_Part_In_Stock_API methods
*  Chanlk  041220  Call Id 120293. Remeoved the method call to CUSTOMER_ORDER_PRICING_API.Get_Sales_Part_Price_Info.
*  NEKOLK  041210  Bug 48478, Modified preDefine() to add MaxLength of REMARK fld in Craft block.
*  HIWELK  041229  Bug 48478, Changed 'Remark' field to a multiline field.
*  Chanlk  040103  Merged Bug 48478.
*  NIJALK  050103  Modified predefine(),validate(),printContents().
*  NIJALK  050112  Removed the block "Fixed Price". Modified predefine(),validate(),printContents(). 
*  NIJALK  050117  Modified LOV for 'spare part site' & changed 'sales part site' to read only in material requisition tab.
*  NIJALK  050119  Modified validation to ROLE_CODE.
*  NIJALK  050121  Modified validation to OWNER.
*  Chanlk  050125  Add field CATALOG_NO to item block 1
*  NIJALK  050203  Modified validate(), preDefine().
*  NIJALK  050207  User allowed sites control is swiched between Site and WO Site.
*  DIAMLK  050215  Modified the method validate() and preDefine().
*  NIJALK  050222  Modified methods createNewRevision(),run().
*  NIJALK  050228  Modified preDefine().
*  NIJALK  050330  Added read only fields JOB_PRG_ID,JOB_PROG_REV to Jobs Tab.
*  NIJALK  050419  Modified generateWorkOrder().
*  NIJALK  050419  Bug 123552: Modified preDefine(),validate(),printContents().
*  NIJALK  050420  Bug 123553: Hide field CURRENCY_CODE.
*  NIJALK  050520  Modified availDetail(), preDefine().
*  THWILK  050525  Added the functionality required under Manage Maintenance Jobs(AMEC114).
*  THWILK  050609  Added the functionality required under Work Order Teams & Qualifications(AMEC114).
*  THWILK  050610  Modified validate() & javascript methods.
*  DiAmlk  050613  Bug IDs:124767,124768,124769 - Modified the methods activatePm,preDefine and run.
*  THWILK  050615  Modified lovItem0OrgCode,lovItem9OrgCode,lovRoleCode,lovItem9RoleCode,lovTeamId,lovItem9TeamId,lovSignnature,
*                  lovItem9Signature javascript methods.
*  SHAFLK  050425  Bug 50830,Used mgr.translateJavaScript() method to get the correctly translated strings in JavaScript functions. 
*  NIJALk  050617  Merged bug id 50830.  
*  THWILK  050622  Modified run(),additionalQualifications1() and additionalQualifications2(). 
*  THWILK  050630  Added the RMB "Predecessors" under Work Order Dependencies(AMEC114). 
*  THWILK  050705  Modified run() and disconnectOperations(). 
*  THWILK  050707  Modified methods run(),additionalQualifications1(),additionalQualifications2(),predecessors1(),
*                  predecessors2() and connectExistingOperations().
*  JAPALK  050708  Call ID 125489,125490. Modified RMB option for itembar2.
*  DiAmlk  050715  Bug ID:125841 - Modified the methods preDefine and newRowITEM0.
*  THWILK  050719  Modified run(). 
*  NIJALK  050722  Bug 125832: Modified preDefine()[itemblk2/Signature & Remarks changed set mandatory].
*                  Modified adjust() to set field 'Generatable' to Read Only if WO is Genearated.
*  NIJALK  050725  Bug 125835: Modified LOV of ITEM5_CONTRACT. Modified LOV Properties for ITEM5_CONTRACT,TOOL_FACILITY_ID to filter retrieving data 
*                  from company site. Modified validation of ITEM5_CONTRACT to fetch data to TOOL_FACILITY_ID,TOOL_FACILITY_TYPE & ITEM5_COST from LOV.
*  NIJALK  050726  Bug 125837: Added "Cost" filed to Operations tab and added necessary validations to it.
*  NIJALK  050726  Bug 125844: Filed "Operation No" in Operation Tab/unset mandatory (to automatically generate operation no).
*  NIJALK  050727  Bug 125835: Modified validation to tools and facilities/tool facility id.
*  NIJALK  050729  Bug 126011: Modified adjust().
*  NEKOLK  050802  AMUT 115: Added new fld GENERATE for itemblk4.
*  NEKOLK  050805  Made changes in jscripts..
*  NIJALK  050808  Bug 126200: Modified Java Script function checkMaintPlan() to return false if not mandatory fields filled. 
*  NIJALK  050808  Bug 126201: Modified ITEM2_SIGNATURE to set Uppercase.
*  NIJALK  050809  Bug 126198: Modified generateWorkOrder(),generateWorkOrder2().
*  NEKOLK  050810  Made changes in newRowItem4().
*  NIJALK  050825  Bug 126563: Modified saveReturn()to refresh itemsets after saving a new record.
*  NIJALK  050826  Bug 126558: Modified preDefine(), adjust() to Set Automatically generated external planning lines read only.
*  AMNILK  051006  Call Id:127600. Modified method preDefine and validate.Add custom validations to ORG_CODE and ORG_CONTRACT.
*  NIJALK  051021  Bug 128082: Modified generateWorkOrder(),generateWorkOrder2().
*  NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile).
*  NIJALK  051205  Bug 129558: Added Expenses,Fixed Price cost to the Budget tab.
*  ERALLK  051208  Bug 54385, Modified the length and the height of 'Alternate Designation' and 'Materials' Fields. 
*  NIJALK  051221  Merged bug 54385.
*  RANFLK  060104  Bug 130520: In the 'Find' mode cannot search a record by Object Id using LOV
*  NIJALK  060111  Changed DATE format to compatible with data formats in PG19.
*  THWILK  060126  Corrected localization errors.
*  RANFLK  060307  call 136529: Modified activematerials()/activeToolAndFacilities()/activePermits()/activePlanning()
*  NIJALK  060315  Call 137412: Modified saveReturn().
*  SULILK  060321  Call 135197: Modified preDefine(),validate(),getCost(),javascript for condition code.
*  SHAFLK  060525  Bug 57598, Modified validation for PART_NO.
* ----------------------------------------------------------------------------
*  AMNILK  060629  Merged with SP1 APP7.
*  AMDILK  060728  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding
*  AMDILK  060807  Merged with Bug Id 58214
*  AMNILK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMNILK  060904  Merged Bug Id: 58216.
*  AMDILK  060918  Eliminated the problem occured when activating a pmaction. Modified activatePm()
*  ILSOLK  070218  Modifed for MTPR904 Hink tasks ReqId 45088.setLovPropery() for Object Id.
*  BUNILK  070504  Implemented "MTIS907 New Service Contract - Services" changes.
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  AMDILK  070405  Merged bug id 64068
*  AMDILK  070607  Call Id 145865: Inserted new service contract information; Contrat name, contract type, 
*                  line description and invoice type
*  CHODLK  070507  Bug 64990, Modified 
*  AMDILK  070626  Merged bug 64990
*  CHANLK  070704  Call 143814: Modified printContents() replace lables with readonly text fields.
*  AMNILK  070719  Eliminated XSS Security Vulnerability.
*  AMDILK  070731  Removed the scroll buttons of the parent when the child tabs are in new or edit modes
*  NIJALK  070730  Bug 66572, Modified preDefine(),activatePm() and printContents().
*  AMDILK  070816  Merged bug 66572
*  SHAFLK  071026  Bug 67948, Modified run(),preDefine(),activatePm(),obsoletePm() and printContents().
*  SHAFLK  071108  Bug 67801, Modified validation for STD_JOB_ID.
*  CHCRLK  071204  Bug 67909, Modified activatePm().
*  CHANLK  080205  Bug 66842,Remove maxLength of PM_NO
*  SHAFLK  080303  Bug 71654, Modified preDefine().
*  ARWILK  080502  Bug 70920, Overode lovLineNo and lovContractId.
*  SHAFLK  081105  Bug 77824, Modified Validate().
*  SHAFLK  080505  Bug 82435, Modified preDefine().
*  NUKULK  090602  Bug 82745, Modified preDefine(). Removed readonly attr in Materials.PART_NO, SPARE_CONTRACT.
*  HARPLK  090708  Bug 84436, Modified preDefine().
*  SHAFLK  090728  Bug 84886, Modified printContents().
*  SHAFLK  090921  Bug 85901, Modified preDefine().
*  SHAFLK  091113  Bug 86851, Modified preDefine() and viewHistWorkOrder().
*  SHAFLK  100210  Bug 88904, Modified printContents().
*  CHANLK  100305  Bug 89283, Change method createNewRevision.
*  NIJALK  100419  Bug 87935, Modified adjust().
*  NIJALK  100420  Bug 85045, Modifed printContents().
*  CHANLK  100305  Bug 88982, Modified okFind
*  NIJALK  100610  Bug 89752, Modified activatePm(), preDefine(), run().
*  CHANLK  100623  Bug 91521, Modified run().
*  SHAFLK  100730  Bug 92157, Modified okFind().
*  SHAFLK  101005  Bug 93366, Modified preDefine().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class PmAction extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.PmAction");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPForm frm;
   private ASPHTMLFormatter fmt;
   private ASPContext ctx;

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   private ASPBlock tempblk;
   private ASPRowSet tempset;

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

   private ASPBlock itemblk3;
   private ASPRowSet itemset3;
   private ASPCommandBar itembar3;
   private ASPTable itemtbl3;
   private ASPBlockLayout itemlay3;

   private ASPBlock itemblk4;
   private ASPRowSet itemset4;
   private ASPCommandBar itembar4;
   private ASPTable itemtbl4;
   private ASPBlockLayout itemlay4;

   private ASPBlock itemblk5;
   private ASPRowSet itemset5;
   private ASPCommandBar itembar5;
   private ASPTable itemtbl5;
   private ASPBlockLayout itemlay5;

   private ASPBlock itemblk6;
   private ASPRowSet itemset6;
   private ASPCommandBar itembar6;
   private ASPTable itemtbl6;
   private ASPBlockLayout itemlay6;

   private ASPBlock itemblk7;
   private ASPRowSet itemset7;
   private ASPCommandBar itembar7;
   private ASPTable itemtbl7;
   private ASPBlockLayout itemlay7;

   private ASPBlock itemblk8;
   private ASPRowSet itemset8;
   private ASPCommandBar itembar8;
   private ASPTable itemtbl8;
   private ASPBlockLayout itemlay8;

   private ASPBlock itemblk9;
   private ASPRowSet itemset9;
   private ASPCommandBar itembar9;
   private ASPTable itemtbl9;
   private ASPBlockLayout itemlay9;

   private ASPField f;
   private ASPBlock headblk1;
   // 031211  ARWILK  Begin  (Replace blocks with tabs)
   private ASPTabContainer tabs;
   // 031211  ARWILK  End  (Replace blocks with tabs)

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private boolean headSearch;
   private boolean qryStrSearch;
   private String qrystr; 
   private ASPTransactionBuffer trans;
   private ASPTransactionBuffer trans1;
   private ASPTransactionBuffer secBuff;
   private ASPQuery qry;
   private ASPCommand cmd;
   private ASPBuffer buf;
   private String strCatDesc;
   private String numListPrice;
   private ASPQuery q;
   private ASPBuffer data;
   private int currrow;
   private String temp;
   private ASPBuffer temp1;
   private int headrowno;
   private String attribute;
   private int beg_pos;
   private String calling_url;
   private ASPBuffer buffer;
   private ASPBuffer buff;
   private ASPBuffer keys;
   private ASPTransactionBuffer testbuff;
   private String wo_num;
   private String date_compl;
   private String pm_type;
   private int n;
   private int i;
   private String perBudg;
   private String matBudg;
   private String toolBudg;
   private String extBudg;
   private String expBudg;
   private String fixBudg;
   private String totBudg;
   private String planPer;
   private String planMat;
   private String planTool;
   private String planExt;
   private String planExp;
   private String planFix;
   private String planTot;
   private String perCostLast;
   private String matCostLast;
   private String toolCostLast;
   private String extCostLast;
   private String expCostLast;
   private String fixCostLast;
   private String totCostLast;
   private String planPerRev;
   private String planToolRev;
   private String planMatRev;
   private String planExtRev;
   private String planExpRev;
   private String planFixRev;
   private String planTotRev;
   private String fldTitlePartNoPurch;
   private String lovTitlePartNoPurch;
   private String warnMsg;
   private int length;  
   private String dateMask;  
   private ASPBuffer row;
   private ASPBuffer sec;
   private String str;  
   private String laymode;
   private ASPBuffer SecBuff;
   private boolean varSec;
   private boolean secConn;
   private boolean secRep;
   private boolean secCopy;   
   private boolean secRev;
   private boolean secGenerate;
   private boolean secSearchObjAdd; 
   private boolean secSpInObj;
   private boolean secSpInDsp;
   private boolean secPrepare;
   private boolean secMeasure;
   private boolean secParam;
   private boolean secSupPart;
   private boolean secInvPart;
   private boolean secAvDet;
   private boolean secCurrQty;
   private boolean secHisWo; 
   private boolean secGenDet;
   private boolean secJobOp;
   private boolean secAttr;
   private boolean secCompetence;
   // 031204  ARWILK  Begin  (Replace links with RMB's)
   private boolean bOpenNewWindow;
   private String urlString;
   private String newWinHandle;
   private int newWinHeight = 600;
   private int newWinWidth = 900;
   // 031204  ARWILK  End  (Replace links with RMB's)
   private boolean checkconn;
   private String headNo;
   String sCompanySites;
   private boolean bWorkOrderGenerated;
   private String sMsgTxt;
   private boolean bSetObsolete;
   private boolean bHasActiveRepl;
   private String sMsgTxt1;

   //===============================================================
   // Construction 
   //===============================================================
   public PmAction(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() 
   {
      headSearch = false;
      qryStrSearch = false;

      ASPManager mgr = getASPManager();

      frm = mgr.getASPForm();
      fmt = mgr.newASPHTMLFormatter();
      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      trans1 = mgr.newASPTransactionBuffer();

      laymode = ctx.readValue("LAYMODE","");

      secConn = ctx.readFlag("SECCONN",false);
      secRep = ctx.readFlag("SECREP",false);
      secCopy = ctx.readFlag("SECCOPY",false);
      secRev = ctx.readFlag("SECREV",false);
      varSec = ctx.readFlag("VARSEC",false);
      secGenerate = ctx.readFlag("SECGENERATE",false);
      secSearchObjAdd = ctx.readFlag("SECSEARCHOBJADD",false); 
      secSpInObj = ctx.readFlag("SECSPINOBJ",false);
      secSpInDsp = ctx.readFlag("SECSPINDSP",false);
      secPrepare = ctx.readFlag("SECPREPARE",false);
      secMeasure = ctx.readFlag("SECMEASURE",false);
      secParam = ctx.readFlag("SECPARAM",false);
      secSupPart = ctx.readFlag("SECSUPPART",false);
      secInvPart = ctx.readFlag("SECINVPART",false);
      secAvDet = ctx.readFlag("SECAVDET",false);   
      secCurrQty = ctx.readFlag("SECCURRQTY",false);
      secHisWo = ctx.readFlag("SECHISWO",false);   
      secGenDet = ctx.readFlag("SECGENDET",false);
      secJobOp = ctx.readFlag("SECJOBOP",false);
      secAttr = ctx.readFlag("SECATTR",false);
      secCompetence = ctx.readFlag("SECCOMP",false);
      qrystr = ctx.readValue("QRYSTR",""); 
      checkconn = ctx.readFlag("CHECKCONNECTION",false);
      if (mgr.commandBarActivated())
      {
         eval(mgr.commandBarFunction());
         if ("ITEM7.SaveReturn".equals(mgr.readValue("__COMMAND")))
            if (itemset2.countRows()>0)
               itemset2.refreshAllRows();
         if ("ITEM9.SaveReturn".equals(mgr.readValue("__COMMAND")))
         {
            okFindITEM0();
            if (itemset9.countRows()>0)
               itemset9.refreshAllRows();
         }
         if ("ITEM0.SaveReturn".equals(mgr.readValue("__COMMAND")))
         {
            if (itemset0.countRows()>0)
               itemset0.refreshAllRows();
         }

      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("REVCRT")))
      {
         int i = 0;
         String new_pm_no = mgr.readValue("NEW_PM_NO");
         String new_revision = mgr.readValue("NEW_REV");

         //Bug 67948. start
         String act_rep = mgr.readValue("ACT_REP");
         if ("TRUE".equals(act_rep))
         {
            mgr.showAlert(mgr.translate("PCMWPMACTCOPYACTIVEREPL: All replacements which had been defined under current revision will be copied to new revision."));
         }
         //Bug 67948. end
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();

         if (headset.countRows()>0)
         {

//        	Bug 91521, Start
         	boolean hasRecords = headset.first();
            while (hasRecords)
            {
               if (headset.getRow().getValue("PM_NO").equals(new_pm_no) && headset.getRow().getValue("PM_REVISION").equals(new_revision))
            	   hasRecords = false;
               else
            	   hasRecords = headset.next();
            }
//        	Bug 91521, End
	    //Bug 89752, Start
            okFindITEM3();
	    //Bug 89752, End
         }
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("OKSPAREPART")))
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();   

         if (headset.countRows() != 1)
         {
            String s_sel_pm = ctx.getGlobal("PMNOGLOBAL");
            int sel_pm = Integer.parseInt(s_sel_pm);
            headset.goTo(sel_pm);   
            okFindITEM0();
            okFindITEM1();
            okFindITEM2();
            okFindITEM3();
            okFindITEM4();
            okFindITEM5();
            okFindITEM6();
            okFindITEM7();
         }
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("PMQUALIFICATION")))
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();   

         if (headset.countRows() != 1)
         {
            headNo = ctx.getGlobal("HEADGLOBAL");
            int headSetNo = Integer.parseInt(headNo);
            headset.goTo(headSetNo);   
            okFindITEM0();
            okFindITEM1();
            okFindITEM2();
            okFindITEM3();
            okFindITEM4();
            okFindITEM5();
            okFindITEM6();
            okFindITEM7();
         }
      }
      else if ("TRUE".equals(mgr.readValue("REFRESHPARENT1")))
         predecessors1();
      else if ("TRUE".equals(mgr.readValue("REFRESHPARENT2")))
         predecessors2();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("PREDECESSORS")))
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();   

         if (headset.countRows() != 1)
         {
            headNo = ctx.getGlobal("HEADGLOBAL");
            int headSetNo = Integer.parseInt(headNo);
            headset.goTo(headSetNo);   
            okFindITEM0();
            okFindITEM1();
            okFindITEM2();
            okFindITEM3();
            okFindITEM4();
            okFindITEM5();
            okFindITEM6();
            okFindITEM7();
         }
      }

      else if (!mgr.isEmpty(mgr.getQueryStringValue("PM_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("PM_REVISION")) && !mgr.isEmpty(mgr.getQueryStringValue("SHOWMAT")))
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();   
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
      {
         qryStrSearch=true;   
         okFind();
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("PM_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("PM_REVISION")))
      {
         qryStrSearch=true;    
         okFind();
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE")))
      {
         qryStrSearch=true;    
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();
      }
      else if (mgr.dataTransfered())
      {
         qryStrSearch=true;  
         okFind();
      }

      securityChk();
      adjust();

      ctx.writeValue("LAYMODE",laymode);

      ctx.writeFlag("SECCONN",secConn);
      ctx.writeFlag("SECREP",secRep);
      ctx.writeFlag("SECCOPY",secCopy);
      ctx.writeFlag("SECREV",secRev);
      ctx.writeFlag("VARSEC",varSec);
      ctx.writeFlag("SECGENERATE",secGenerate);
      ctx.writeFlag("SECSEARCHOBJADD",secSearchObjAdd);
      ctx.writeFlag("SECSPINOBJ",secSpInObj);

      ctx.writeFlag("SECSPINDSP",secSpInDsp);
      ctx.writeFlag("SECPREPARE",secPrepare);
      ctx.writeFlag("SECMEASURE",secMeasure);
      ctx.writeFlag("SECPARAM",secParam);
      ctx.writeFlag("SECSUPPART",secSupPart);
      ctx.writeFlag("SECINVPART",secInvPart);
      ctx.writeFlag("SECAVDET",secAvDet);  
      ctx.writeFlag("SECCURRQTY",secCurrQty);
      ctx.writeFlag("SECHISWO",secHisWo);  
      ctx.writeFlag("SECGENDET",secGenDet);
      ctx.writeFlag("SECJOBOP",secJobOp);
      ctx.writeFlag("SECATTR",secAttr);
      ctx.writeFlag("SECCOMP",secCompetence);
      ctx.writeValue("QRYSTR",qrystr); 
      ctx.writeFlag("CHECKCONNECTION",checkconn);

      // 031211  ARWILK  Begin  (Replace blocks with tabs)
      tabs.saveActiveTab();
      // 031211  ARWILK  End  (Replace blocks with tabs)
   }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
   public boolean checksec( String method,int ref, String[] isSecure) 
   {
      ASPManager mgr = getASPManager();

      isSecure[ref] = "false" ; 
      String splitted[] = split(method,"."); 

      secBuff = mgr.newASPTransactionBuffer();
      secBuff.addSecurityQuery(splitted[0],splitted[1]);

      secBuff = mgr.perform(secBuff);

      if (secBuff.getSecurityInfo().itemExists(method))
      {
         isSecure[ref] = "true";
         return true; 
      }
      else
         return false;   
   }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void validate()
   {
      int ref;
      String val;
      String isSecure[] = new String[15];
      String txt;
      String sOnhand = "ONHAND";
      String sPicking = "PICKING";
      String sF = "F" ;
      String sPallet = "PALLET"  ;
      String sDeep = "DEEP" ;
      String sBuffer = "BUFFER" ;
      String sDelivery = "DELIVERY"  ;
      String sShipment = "SHIPMENT"  ;
      String sManufacturing = "MANUFACTURING" ;
      String sTrue = "TRUE";
      String sFalse = "FALSE";
      String teamDesc;

      ASPManager mgr = getASPManager();

      val = mgr.readValue("VALIDATE");

      //----------------------------------------------------//
      //------ Validate T/F data ---------------------------//
      //----------------------------------------------------//

      if ("TOOL_FACILITY_ID".equals(val))
      {
         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[6];
         String sToolId   = mgr.readValue("TOOL_FACILITY_ID");
         String sToolCont = mgr.readValue("ITEM5_CONTRACT");
         String sToolOrg  = mgr.readValue("ITEM5_ORG_CODE");
         String sToolType = mgr.readValue("TOOL_FACILITY_TYPE");
         String sToolDesc = null;
         String sTypeDesc = null;
         String sQty      = mgr.readValue("ITEM5_QTY");
         String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
         String sNote     = mgr.readValue("ITEM5_NOTE");
         String sToolCost = null;
         String sToolCostCurr = null;
         String sCatalogNo = mgr.readValue("ITEM5_CATALOG_NO");
         String sCatalogContract = mgr.readValue("ITEM5_CATALOG_NO_CONTRACT");
         String sCatalogDesc = mgr.readValue("ITEM5_CATALOG_NO_DESC");
         String sCatalogPrice = mgr.readValue("ITEM5_SALES_PRICE");
         String sCatalogPriceCurr = mgr.readValue("ITEM5_SALES_CURRENCY");
         String sCatalogDiscount  = mgr.readValue("ITEM5_DISCOUNT");
         String sPlannedPriceAmt  = mgr.readValue("ITEM5_PLANNED_PRICE");

         if (sToolId.indexOf("^",0) > 0)
         {
            for (i=0 ; i<3; i++)
            {
               endpos = sToolId.indexOf("^",startpos);
               reqstr = sToolId.substring(startpos,endpos);
               ar[i] = reqstr;
               startpos= endpos+1;
            }

            sToolId = ar[0];
            sToolOrg = ar[1];
            sToolCont = ar[2];
         }

         cmd = trans.addCustomFunction("TFDESC","Tool_Facility_API.Get_Tool_Facility_Description","TOOL_FACILITY_DESC");
         cmd.addParameter("TOOL_FACILITY_ID",sToolId);

         cmd = trans.addCustomFunction("TFTYPE","Tool_Facility_API.Get_Tool_Facility_Type","TOOL_FACILITY_TYPE");
         cmd.addParameter("TOOL_FACILITY_ID",sToolId);

         cmd = trans.addCustomFunction("TFTYPEDESC","Tool_Facility_Type_API.Get_Type_Description","TYPE_DESCRIPTION");
         cmd.addReference("TOOL_FACILITY_TYPE","TFTYPE/DATA");

         cmd = trans.addCustomFunction("TFQTY","Connect_Tools_Facilities_API.Get_Qty","ITEM5_QTY");
         cmd.addParameter("TOOL_FACILITY_ID",sToolId);
         cmd.addParameter("ITEM5_CONTRACT",sToolCont);
         cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);

         cmd = trans.addCustomFunction("TFCOST","Pm_Action_Tool_Facility_API.Get_Cost","ITEM5_COST");
         cmd.addParameter("TOOL_FACILITY_ID",sToolId);
         cmd.addReference("TOOL_FACILITY_TYPE","TFTYPE/DATA");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");

         cmd = trans.addCustomFunction("TFCOSTCURR","Pm_Action_Tool_Facility_API.Get_Cost_Currency","ITEM5_COST_CURRENCY");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");

         cmd = trans.addCustomFunction("TFNOTE","Connect_Tools_Facilities_API.Get_Note","ITEM5_NOTE");
         cmd.addParameter("TOOL_FACILITY_ID",sToolId);
         cmd.addParameter("ITEM5_CONTRACT",sToolCont);
         cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);

         cmd = trans.addCustomFunction("TFSALESPART","Connect_Tools_Facilities_API.Get_Catalog_No","ITEM5_CATALOG_NO");
         cmd.addParameter("TOOL_FACILITY_ID",sToolId);
         cmd.addParameter("ITEM5_CONTRACT",sToolCont);
         cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);

         cmd = trans.addCustomFunction("WOSITE","Pm_Action_API.Get_Contract","ITEM5_WO_CONTRACT");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");

         cmd = trans.addCustomFunction("SPSITE","Pm_Action_Tool_Facility_API.Sales_Part_Exist_On_Pm_Site","ITEM5_SP_SITE");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
         cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");

         cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","ITEM5_CATALOG_NO_DESC");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
         cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");

         cmd = trans.addCustomFunction("TFSPPRICE","Pm_Action_Tool_Facility_API.Calculate_Sales_Price","ITEM5_SALES_PRICE");
         cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");

         cmd = trans.addCustomFunction("TFSPCURR","Pm_Action_Tool_Facility_API.Get_Sales_Currency","ITEM5_SALES_CURRENCY");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");

         cmd = trans.addCustomFunction("TFSPDISCOUNT","Pm_Action_Tool_Facility_API.Get_Discount","ITEM5_DISCOUNT");
         cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");
         cmd.addParameter("ITEM5_QTY",sQty);
         cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

         cmd = trans.addCustomFunction("TFPLANPRAMT","Pm_Action_Tool_Facility_API.Get_Price_Amount","ITEM5_PLANNED_PRICE");
         cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");
         cmd.addParameter("ITEM5_QTY",sQty);
         cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
         cmd.addReference("ITEM5_SALES_PRICE","TFSPPRICE/DATA");
         cmd.addReference("ITEM5_DISCOUNT","TFSPDISCOUNT/DATA");

         trans = mgr.validate(trans);

         sToolDesc = trans.getValue("TFDESC/DATA/TOOL_FACILITY_DESC");
         String sGetToolType = trans.getValue("TFTYPE/DATA/TOOL_FACILITY_TYPE");
         sTypeDesc = trans.getValue("TFTYPEDESC/DATA/TYPE_DESCRIPTION");

         double TfQty = trans.getNumberValue("TFQTY/DATA/QTY");
         String qtyStr = mgr.getASPField("ITEM5_QTY").formatNumber(TfQty);
         if (mgr.isEmpty(sQty))
            sQty = qtyStr;

         double ToolCost = trans.getNumberValue("TFCOST/DATA/COST");
         String costStr = mgr.getASPField("ITEM5_COST").formatNumber(ToolCost);

         sToolCostCurr = trans.getValue("TFCOSTCURR/DATA/COST_CURRENCY");

         String sGetNote = trans.getValue("TFNOTE/DATA/NOTE");

         String sCatalogNum = trans.getValue("TFSALESPART/DATA/CATALOG_NO");

         String sWoSite = trans.getValue("WOSITE/DATA/ITEM5_WO_CONTRACT");
         String sSpSite = trans.getValue("SPSITE/DATA/ITEM5_SP_SITE");

         String sGetCatalogDesc = trans.getValue("TFSPDESC/DATA/ITEM5_CATALOG_NO_DESC");

         double GetCatalogPrice = trans.getNumberValue("TFSPPRICE/DATA/SALES_PRICE");
         String catPriceStr = mgr.getASPField("ITEM5_SALES_PRICE").formatNumber(GetCatalogPrice);

         String sGetCatalogPriceCurr = trans.getValue("TFSPCURR/DATA/PRICE_CURRENCY");

         double GetCatalogDiscount = trans.getNumberValue("TFSPDISCOUNT/DATA/DISCOUNT");
         String catDiscount = mgr.getASPField("ITEM5_DISCOUNT").formatNumber(GetCatalogDiscount);

         double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
         String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

         if (mgr.isEmpty(sToolType))
            sToolType = sGetToolType;

         if (mgr.isEmpty(sNote))
            sNote = sGetNote;

         if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            sPlannedPriceAmt = plannedPriceAmt;

         if ("TRUE".equals(sSpSite))
         {
            if (!mgr.isEmpty(sToolId) && !mgr.isEmpty(sToolCont) && !mgr.isEmpty(sToolOrg) && mgr.isEmpty(sCatalogNo))
            {
               sCatalogNo = sCatalogNum;
               sCatalogDesc = sGetCatalogDesc;
               sCatalogPrice = catPriceStr;
               sCatalogPriceCurr = sGetCatalogPriceCurr;
               sCatalogDiscount = catDiscount;
            }
         }

         txt = (mgr.isEmpty(sToolDesc) ? "" : (sToolDesc))+ "^" + (mgr.isEmpty(sToolType) ? "" : (sToolType))+ "^" + 
               (mgr.isEmpty(sTypeDesc) ? "" : (sTypeDesc))+ "^" + 
               (mgr.isEmpty(costStr) ? "" : (costStr))+ "^" + (mgr.isEmpty(sToolCostCurr) ? "" : (sToolCostCurr))+ "^" + 
               (mgr.isEmpty(sCatalogNo) ? "" : (sCatalogNo))+ "^" + (mgr.isEmpty(sCatalogDesc) ? "" : (sCatalogDesc))+ "^" + 
               (mgr.isEmpty(sCatalogPrice) ? "" : (sCatalogPrice))+ "^" + (mgr.isEmpty(sCatalogPriceCurr) ? "" : (sCatalogPriceCurr))+ "^" + 
               (mgr.isEmpty(sCatalogDiscount) ? "" : (sCatalogDiscount))+ "^" + (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" + 
               (mgr.isEmpty(sNote) ? "" : (sNote))+ "^"+(mgr.isEmpty(sToolCont) ? "" : (sToolCont))+ "^"+(mgr.isEmpty(sToolId) ? "" : (sToolId))+ "^" ;

         mgr.responseWrite(txt);
      }
      else if ("TOOL_FACILITY_TYPE".equals(val))
      {
         String sQty      = mgr.readValue("ITEM5_QTY");
         String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
         String sToolCostAmt = null;

         cmd = trans.addCustomFunction("TFTYPEDESC","Tool_Facility_Type_API.Get_Type_Description","TYPE_DESCRIPTION");
         cmd.addParameter("TOOL_FACILITY_TYPE");

         cmd = trans.addCustomFunction("TFCOST","Pm_Action_Tool_Facility_API.Get_Cost","ITEM5_COST");
         cmd.addParameter("TOOL_FACILITY_ID");
         cmd.addParameter("TOOL_FACILITY_TYPE");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");

         cmd = trans.addCustomFunction("TFCOSTCURR","Pm_Action_Tool_Facility_API.Get_Cost_Currency","ITEM5_COST_CURRENCY");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");

         trans = mgr.validate(trans);

         String sTypeDesc = trans.getValue("TFTYPEDESC/DATA/TYPE_DESCRIPTION");

         double ToolCost = trans.getNumberValue("TFCOST/DATA/COST");
         String costStr = mgr.getASPField("ITEM5_COST").formatNumber(ToolCost);

         String sToolCostCurr = trans.getValue("TFCOSTCURR/DATA/COST_CURRENCY");

         txt = (mgr.isEmpty(sTypeDesc) ? "" : (sTypeDesc))+ "^" + (mgr.isEmpty(costStr) ? "" : (costStr))+ "^" + 
               (mgr.isEmpty(sToolCostCurr) ? "" : (sToolCostCurr))+ "^";

         mgr.responseWrite(txt); 
      }
      else if ("ITEM5_CONTRACT".equals(val))
      {
         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[6];
         String sToolId;
         String sToolDesc;
         String sToolType;
         String sTypeDesc;
         String sToolOrg;
         String sHourCost;
         String sToolCont          = mgr.readValue("ITEM5_CONTRACT");
         String sQty               = mgr.readValue("ITEM5_QTY");
         String sNote              = mgr.readValue("ITEM5_NOTE");
         String sPlannedHour       = mgr.readValue("ITEM5_PLANNED_HOUR");
         String sCatalogNo         = mgr.readValue("ITEM5_CATALOG_NO");
         String sCatalogContract   = mgr.readValue("ITEM5_CATALOG_NO_CONTRACT");
         String sCatalogDesc       = mgr.readValue("ITEM5_CATALOG_NO_DESC");
         String sCatalogPrice      = mgr.readValue("ITEM5_SALES_PRICE");
         String sCatalogPriceCurr  = mgr.readValue("ITEM5_SALES_CURRENCY");
         String sCatalogDiscount   = mgr.readValue("ITEM5_DISCOUNT");
         String sPlannedPriceAmt   = mgr.readValue("ITEM5_PLANNED_PRICE");
         String sWn                = mgr.readValue("ITEM5_PM_NO");

         if (sToolCont.indexOf("^",0) > 0)
         {
            for (i=0 ; i<3; i++)
            {
               endpos = sToolCont.indexOf("^",startpos);
               reqstr = sToolCont.substring(startpos,endpos);
               ar[i] = reqstr;
               startpos= endpos+1;
            }

            sToolId = ar[0];
            sToolOrg = ar[1];
            sToolCont = ar[2];
         }
         else
         {
            sToolId = mgr.readValue("TOOL_FACILITY_ID");
            sToolOrg = mgr.readValue("ITEM5_ORG_CODE");
         }

         trans.clear();

         cmd = trans.addCustomFunction("TFDESC","Tool_Facility_API.Get_Tool_Facility_Description","TOOL_FACILITY_DESC");
         cmd.addParameter("TOOL_FACILITY_ID",sToolId);

         cmd = trans.addCustomFunction("TFTYPE","Tool_Facility_API.Get_Tool_Facility_Type","TOOL_FACILITY_TYPE");
         cmd.addParameter("TOOL_FACILITY_ID",sToolId);

         cmd = trans.addCustomFunction("TFTYPEDESC","Tool_Facility_API.Get_Tool_Facil_Type_Desc","TYPE_DESCRIPTION");
         cmd.addParameter("TOOL_FACILITY_ID",sToolId);

         cmd = trans.addCustomFunction("TFCOST","Pm_Action_Tool_Facility_API.Get_Cost","ITEM5_COST");
         cmd.addParameter("TOOL_FACILITY_ID",sToolId);
         cmd.addReference("TOOL_FACILITY_TYPE","TFTYPE/DATA");
         cmd.addParameter("ITEM5_PM_NO",sWn);
         cmd.addParameter("ITEM5_PM_REVISION",mgr.readValue("ITEM5_PM_REVISION"));

         cmd = trans.addCustomFunction("TFNOTE","Connect_Tools_Facilities_API.Get_Note","ITEM5_NOTE");
         cmd.addParameter("TOOL_FACILITY_ID",sToolId);
         cmd.addParameter("ITEM5_CONTRACT",sToolCont);
         cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);

         cmd = trans.addCustomFunction("TFQTY","Connect_Tools_Facilities_API.Get_Qty","ITEM5_QTY");
         cmd.addParameter("TOOL_FACILITY_ID",sToolId);
         cmd.addParameter("ITEM5_CONTRACT",sToolCont);
         cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);  

         cmd = trans.addCustomFunction("TFSALESPART","Connect_Tools_Facilities_API.Get_Catalog_No","ITEM5_CATALOG_NO");
         cmd.addParameter("TOOL_FACILITY_ID",sToolId);
         cmd.addParameter("ITEM5_CONTRACT",sToolCont);
         cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);

         cmd = trans.addCustomFunction("WOSITE","Pm_Action_API.Get_Contract","ITEM5_WO_CONTRACT");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");

         cmd = trans.addCustomFunction("SPSITE","Pm_Action_Tool_Facility_API.Sales_Part_Exist_On_Pm_Site","ITEM5_SP_SITE");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
         cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");

         cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","ITEM5_CATALOG_NO_DESC");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
         cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");

         cmd = trans.addCustomFunction("TFSPPRICE","Pm_Action_Tool_Facility_API.Calculate_Sales_Price","ITEM5_SALES_PRICE");
         cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");

         cmd = trans.addCustomFunction("TFSPCURR","Pm_Action_Tool_Facility_API.Get_Sales_Currency","ITEM5_SALES_CURRENCY");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");

         cmd = trans.addCustomFunction("TFSPDISCOUNT","Pm_Action_Tool_Facility_API.Get_Discount","ITEM5_DISCOUNT");
         cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");
         cmd.addParameter("ITEM5_QTY",sQty);
         cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

         cmd = trans.addCustomFunction("TFPLANPRAMT","Pm_Action_Tool_Facility_API.Get_Price_Amount","ITEM5_PLANNED_PRICE");
         cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");
         cmd.addParameter("ITEM5_QTY",sQty);
         cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
         cmd.addReference("ITEM5_SALES_PRICE","TFSPPRICE/DATA");
         cmd.addReference("ITEM5_DISCOUNT","TFSPDISCOUNT/DATA");

         trans = mgr.validate(trans);

         String sGetNote = trans.getValue("TFNOTE/DATA/NOTE");

         /*double TfQty = trans.getNumberValue("TFQTY/DATA/QTY");
         String qtyStr = mgr.getASPField("ITEM5_QTY").formatNumber(TfQty);
         if (mgr.isEmpty(sQty))
             sQty    =    qtyStr;*/

         String sCatalogNum = trans.getValue("TFSALESPART/DATA/CATALOG_NO");

         String sWoSite = trans.getValue("WOSITE/DATA/ITEM5_WO_CONTRACT");
         String sSpSite = trans.getValue("SPSITE/DATA/ITEM5_SP_SITE");

         String sGetCatalogDesc = trans.getValue("TFSPDESC/DATA/ITEM5_CATALOG_NO_DESC");

         double GetCatalogPrice = trans.getNumberValue("TFSPPRICE/DATA/SALES_PRICE");
         String catPriceStr = mgr.getASPField("ITEM5_SALES_PRICE").formatNumber(GetCatalogPrice);

         String sGetCatalogPriceCurr = trans.getValue("TFSPCURR/DATA/PRICE_CURRENCY");

         double GetCatalogDiscount = trans.getNumberValue("TFSPDISCOUNT/DATA/DISCOUNT");
         String catDiscount = mgr.getASPField("ITEM5_DISCOUNT").formatNumber(GetCatalogDiscount);

         double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
         String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

         sToolDesc = trans.getValue("TFDESC/DATA/TOOL_FACILITY_DESC");
         sToolType = trans.getValue("TFTYPE/DATA/TOOL_FACILITY_TYPE");
         sTypeDesc = trans.getValue("TFTYPEDESC/DATA/TYPE_DESCRIPTION");
         sHourCost = trans.getValue("TFCOST/DATA/COST");

         if (mgr.isEmpty(sNote))
            sNote = sGetNote;

         if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            sPlannedPriceAmt = plannedPriceAmt;

         if ("TRUE".equals(sSpSite))
         {
            if (!mgr.isEmpty(sToolId) && !mgr.isEmpty(sToolCont) && !mgr.isEmpty(sToolOrg) && mgr.isEmpty(sCatalogNo))
            {
               sCatalogNo = sCatalogNum;
               sCatalogDesc = sGetCatalogDesc;
               sCatalogPrice = catPriceStr;
               sCatalogPriceCurr = sGetCatalogPriceCurr;
               sCatalogDiscount = catDiscount;  
            }
         }
         txt = (mgr.isEmpty(sNote) ? "" : (sNote))+ "^" + 
               (mgr.isEmpty(sCatalogNo) ? "" : (sCatalogNo))+ "^" + 
               (mgr.isEmpty(sCatalogDesc) ? "" : (sCatalogDesc))+ "^" + (mgr.isEmpty(sCatalogPrice) ? "" : (sCatalogPrice))+ "^" + 
               (mgr.isEmpty(sCatalogPriceCurr) ? "" : (sCatalogPriceCurr))+ "^" + (mgr.isEmpty(sCatalogDiscount) ? "" : (sCatalogDiscount))+ "^" + 
               (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^"+
               (mgr.isEmpty(sToolId) ? "" : (sToolId))+ "^"+(mgr.isEmpty(sToolDesc) ? "" : (sToolDesc))+ "^"+
               (mgr.isEmpty(sToolType) ? "" : (sToolType))+ "^"+(mgr.isEmpty(sTypeDesc) ? "" : (sTypeDesc))+ "^"+
               (mgr.isEmpty(sToolCont) ? "" : (sToolCont))+ "^"+(mgr.isEmpty(sHourCost) ? "" : (sHourCost))+ "^";

         mgr.responseWrite(txt);
      }
      else if ("ITEM5_ORG_CODE".equals(val))
      {
         String sToolId   = mgr.readValue("TOOL_FACILITY_ID");
         String sToolCont = mgr.readValue("ITEM5_CONTRACT");
         String sToolOrg  = mgr.readValue("ITEM5_ORG_CODE");
         String sQty      = mgr.readValue("ITEM5_QTY");
         String sNote     = mgr.readValue("ITEM5_NOTE");
         String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
         String sCatalogNo = mgr.readValue("ITEM5_CATALOG_NO");
         String sCatalogContract = mgr.readValue("ITEM5_CATALOG_NO_CONTRACT");
         String sCatalogDesc = mgr.readValue("ITEM5_CATALOG_NO_DESC");
         String sCatalogPrice = mgr.readValue("ITEM5_SALES_PRICE");
         String sCatalogPriceCurr = mgr.readValue("ITEM5_SALES_CURRENCY");
         String sCatalogDiscount  = mgr.readValue("ITEM5_DISCOUNT");
         String sPlannedPriceAmt  = mgr.readValue("ITEM5_PLANNED_PRICE");

         cmd = trans.addCustomFunction("TFNOTE","Connect_Tools_Facilities_API.Get_Note","ITEM5_NOTE");
         cmd.addParameter("TOOL_FACILITY_ID");
         cmd.addParameter("ITEM5_CONTRACT");
         cmd.addParameter("ITEM5_ORG_CODE");

         cmd = trans.addCustomFunction("TFQTY","Connect_Tools_Facilities_API.Get_Qty","ITEM5_QTY");
         cmd.addParameter("TOOL_FACILITY_ID");
         cmd.addParameter("ITEM5_CONTRACT");
         cmd.addParameter("ITEM5_ORG_CODE");  

         cmd = trans.addCustomFunction("TFSALESPART","Connect_Tools_Facilities_API.Get_Catalog_No","ITEM5_CATALOG_NO");
         cmd.addParameter("TOOL_FACILITY_ID");
         cmd.addParameter("ITEM5_CONTRACT");
         cmd.addParameter("ITEM5_ORG_CODE");

         cmd = trans.addCustomFunction("WOSITE","Pm_Action_API.Get_Contract","ITEM5_WO_CONTRACT");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");

         cmd = trans.addCustomFunction("SPSITE","Pm_Action_Tool_Facility_API.Sales_Part_Exist_On_Pm_Site","ITEM5_SP_SITE");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
         cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");

         cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","ITEM5_CATALOG_NO_DESC");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
         cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");

         cmd = trans.addCustomFunction("TFSPPRICE","Pm_Action_Tool_Facility_API.Calculate_Sales_Price","ITEM5_SALES_PRICE");
         cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");

         cmd = trans.addCustomFunction("TFSPCURR","Pm_Action_Tool_Facility_API.Get_Sales_Currency","ITEM5_SALES_CURRENCY");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");

         cmd = trans.addCustomFunction("TFSPDISCOUNT","Pm_Action_Tool_Facility_API.Get_Discount","ITEM5_DISCOUNT");
         cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");
         cmd.addParameter("ITEM5_QTY",sQty);
         cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

         cmd = trans.addCustomFunction("TFPLANPRAMT","Pm_Action_Tool_Facility_API.Get_Price_Amount","ITEM5_PLANNED_PRICE");
         cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");
         cmd.addParameter("ITEM5_QTY",sQty);
         cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
         cmd.addReference("ITEM5_SALES_PRICE","TFSPPRICE/DATA");
         cmd.addReference("ITEM5_DISCOUNT","TFSPDISCOUNT/DATA");


         trans = mgr.validate(trans);

         String sGetNote = trans.getValue("TFNOTE/DATA/NOTE");

         double TfQty = trans.getNumberValue("TFQTY/DATA/QTY");
         String qtyStr = mgr.getASPField("ITEM5_QTY").formatNumber(TfQty);

         if (mgr.isEmpty(sQty))
            sQty = qtyStr;

         String sCatalogNum = trans.getValue("TFSALESPART/DATA/CATALOG_NO");

         String sWoSite = trans.getValue("WOSITE/DATA/ITEM5_WO_CONTRACT");

         String sSpSite = trans.getValue("SPSITE/DATA/ITEM5_SP_SITE");

         String sGetCatalogDesc = trans.getValue("TFSPDESC/DATA/ITEM5_CATALOG_NO_DESC");

         double GetCatalogPrice = trans.getNumberValue("TFSPPRICE/DATA/SALES_PRICE");
         String catPriceStr = mgr.getASPField("ITEM5_SALES_PRICE").formatNumber(GetCatalogPrice);

         String sGetCatalogPriceCurr = trans.getValue("TFSPCURR/DATA/PRICE_CURRENCY");

         double GetCatalogDiscount = trans.getNumberValue("TFSPDISCOUNT/DATA/DISCOUNT");
         String catDiscount = mgr.getASPField("ITEM5_DISCOUNT").formatNumber(GetCatalogDiscount);

         double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
         String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

         if (mgr.isEmpty(sNote))
            sNote = sGetNote;

         if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            sPlannedPriceAmt = plannedPriceAmt;

         if ("TRUE".equals(sSpSite))
         {
            if (!mgr.isEmpty(sToolId) && !mgr.isEmpty(sToolCont) && !mgr.isEmpty(sToolOrg) && mgr.isEmpty(sCatalogNo))
            {
               sCatalogNo = sCatalogNum;
               sCatalogDesc = sGetCatalogDesc;
               sCatalogPrice = catPriceStr;
               sCatalogPriceCurr = sGetCatalogPriceCurr;
               sCatalogDiscount = catDiscount;
            }
         }

         txt = (mgr.isEmpty(sNote) ? "" : (sNote))+ "^" + (mgr.isEmpty(sQty) ? "" : (sQty))+ "^" + 
               (mgr.isEmpty(sCatalogNo) ? "" : (sCatalogNo))+ "^" + 
               (mgr.isEmpty(sCatalogDesc) ? "" : (sCatalogDesc))+ "^" + (mgr.isEmpty(sCatalogPrice) ? "" : (sCatalogPrice))+ "^" + 
               (mgr.isEmpty(sCatalogPriceCurr) ? "" : (sCatalogPriceCurr))+ "^" + (mgr.isEmpty(sCatalogDiscount) ? "" : (sCatalogDiscount))+ "^" + 
               (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" ;

         mgr.responseWrite(txt);
      }
      else if ("ITEM5_QTY".equals(val))
      {
         String sQty      = mgr.readValue("ITEM5_QTY");
         String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
         String sPlannedPriceAmt  = null;

         cmd = trans.addCustomFunction("TFPLANPRAMT","Pm_Action_Tool_Facility_API.Get_Price_Amount","ITEM5_PLANNED_PRICE");
         cmd.addParameter("ITEM5_CATALOG_NO");
         cmd.addParameter("ITEM5_CATALOG_NO_CONTRACT");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");
         cmd.addParameter("ITEM5_QTY",sQty);
         cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
         cmd.addParameter("ITEM5_SALES_PRICE");
         cmd.addParameter("ITEM5_DISCOUNT");

         trans = mgr.validate(trans);

         double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
         String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

         if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            sPlannedPriceAmt = plannedPriceAmt;

         txt = (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^"; 

         mgr.responseWrite(txt);
      }
      else if ("ITEM5_PLANNED_HOUR".equals(val))
      {
         String sQty      = mgr.readValue("ITEM5_QTY");
         String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
         String sPlannedPriceAmt  = null;

         cmd = trans.addCustomFunction("TFPLANPRAMT","Pm_Action_Tool_Facility_API.Get_Price_Amount","ITEM5_PLANNED_PRICE");
         cmd.addParameter("ITEM5_CATALOG_NO");
         cmd.addParameter("ITEM5_CATALOG_NO_CONTRACT");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");
         cmd.addParameter("ITEM5_QTY",sQty);
         cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
         cmd.addParameter("ITEM5_SALES_PRICE");
         cmd.addParameter("ITEM5_DISCOUNT");

         trans = mgr.validate(trans);

         double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
         String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

         if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            sPlannedPriceAmt = plannedPriceAmt;

         txt = (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^"; 

         mgr.responseWrite(txt);  
      }
      else if ("ITEM5_CATALOG_NO".equals(val))
      {
         String sQty      = mgr.readValue("ITEM5_QTY");
         String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
         String sSalesPart = mgr.readValue("ITEM5_CATALOG_NO");
         String sCatalogDesc = null;
         String sCatalogPrice = null;
         String sCatalogPriceCurr = null;
         String sCatalogDiscount  = null;
         String sPlannedPriceAmt  = null;

         cmd = trans.addCustomFunction("WOSITE","Pm_Action_API.Get_Contract","ITEM5_WO_CONTRACT");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");

         cmd = trans.addCustomFunction("SPSITE","Pm_Action_Tool_Facility_API.Sales_Part_Exist_On_Pm_Site","ITEM5_SP_SITE");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
         cmd.addParameter("ITEM5_CATALOG_NO");

         cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","ITEM5_CATALOG_NO_DESC");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
         cmd.addParameter("ITEM5_CATALOG_NO");

         cmd = trans.addCustomFunction("TFSPPRICE","Pm_Action_Tool_Facility_API.Calculate_Sales_Price","ITEM5_SALES_PRICE");
         cmd.addParameter("ITEM5_CATALOG_NO");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");

         cmd = trans.addCustomFunction("TFSPCURR","Pm_Action_Tool_Facility_API.Get_Sales_Currency","ITEM5_SALES_CURRENCY");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");

         cmd = trans.addCustomFunction("TFSPDISCOUNT","Pm_Action_Tool_Facility_API.Get_Discount","ITEM5_DISCOUNT");
         cmd.addParameter("ITEM5_CATALOG_NO");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");
         cmd.addParameter("ITEM5_QTY",sQty);
         cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

         cmd = trans.addCustomFunction("TFPLANPRAMT","Pm_Action_Tool_Facility_API.Get_Price_Amount","ITEM5_PLANNED_PRICE");
         cmd.addParameter("ITEM5_CATALOG_NO");
         cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");
         cmd.addParameter("ITEM5_QTY",sQty);
         cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
         cmd.addReference("ITEM5_SALES_PRICE","TFSPPRICE/DATA");
         cmd.addReference("ITEM5_DISCOUNT","TFSPDISCOUNT/DATA");

         trans = mgr.validate(trans);

         String sWoSite = trans.getValue("WOSITE/DATA/ITEM5_WO_CONTRACT");
         String sSpSite = trans.getValue("SPSITE/DATA/ITEM5_SP_SITE");

         String sGetCatalogDesc = trans.getValue("TFSPDESC/DATA/ITEM5_CATALOG_NO_DESC");

         double GetCatalogPrice = trans.getNumberValue("TFSPPRICE/DATA/SALES_PRICE");
         String catPriceStr = mgr.getASPField("ITEM5_SALES_PRICE").formatNumber(GetCatalogPrice);

         String sGetCatalogPriceCurr = trans.getValue("TFSPCURR/DATA/PRICE_CURRENCY");

         double GetCatalogDiscount = trans.getNumberValue("TFSPDISCOUNT/DATA/DISCOUNT");
         String catDiscount = mgr.getASPField("ITEM5_DISCOUNT").formatNumber(GetCatalogDiscount);

         double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
         String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);


         if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            sPlannedPriceAmt = plannedPriceAmt;

         if ("TRUE".equals(sSpSite))
         {
            sCatalogDesc = sGetCatalogDesc;
            sCatalogPrice = catPriceStr;
            sCatalogPriceCurr = sGetCatalogPriceCurr;
            sCatalogDiscount = catDiscount;
         }

         txt = (mgr.isEmpty(sCatalogDesc) ? "" : (sCatalogDesc))+ "^" + (mgr.isEmpty(sCatalogPrice) ? "" : (sCatalogPrice))+ "^" + 
               (mgr.isEmpty(sCatalogPriceCurr) ? "" : (sCatalogPriceCurr))+ "^" + (mgr.isEmpty(sCatalogDiscount) ? "" : (sCatalogDiscount))+ "^" + 
               (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" ;

         mgr.responseWrite(txt);
      }
      else if ("ITEM5_SALES_PRICE".equals(val))
      {
         String sQty      = mgr.readValue("ITEM5_QTY");
         String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
         String sPlannedPriceAmt  = null;

         cmd = trans.addCustomFunction("TFPLANPRAMT","Pm_Action_Tool_Facility_API.Get_Price_Amount","ITEM5_PLANNED_PRICE");
         cmd.addParameter("ITEM5_CATALOG_NO");
         cmd.addParameter("ITEM5_CATALOG_NO_CONTRACT");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");
         cmd.addParameter("ITEM5_QTY",sQty);
         cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
         cmd.addParameter("ITEM5_SALES_PRICE");
         cmd.addParameter("ITEM5_DISCOUNT");

         trans = mgr.validate(trans);

         double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
         String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

         if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            sPlannedPriceAmt = plannedPriceAmt;

         txt = (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" ;
         mgr.responseWrite(txt);
      }
      else if ("ITEM5_DISCOUNT".equals(val))
      {
         String sQty      = mgr.readValue("ITEM5_QTY");
         String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
         String sPlannedPriceAmt  = null;

         cmd = trans.addCustomFunction("TFPLANPRAMT","Pm_Action_Tool_Facility_API.Get_Price_Amount","ITEM5_PLANNED_PRICE");
         cmd.addParameter("ITEM5_CATALOG_NO");
         cmd.addParameter("ITEM5_CATALOG_NO_CONTRACT");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");
         cmd.addParameter("ITEM5_QTY",sQty);
         cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
         cmd.addParameter("ITEM5_SALES_PRICE");
         cmd.addParameter("ITEM5_DISCOUNT");

         trans = mgr.validate(trans);

         double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
         String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

         if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            sPlannedPriceAmt = plannedPriceAmt;

         txt = (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" ;
         mgr.responseWrite(txt); 
      }

      //----------------------------------------------------//
      //------ End of T/F Validate -------------------------//
      //----------------------------------------------------//

      else if ("HEAD_CONTRACT".equals(val))
      {
         cmd = trans.addCustomFunction("PARAA1", "Site_API.Get_Company", "COMPANY" );
         cmd.addParameter("HEAD_CONTRACT");

         trans = mgr.validate(trans);

         String strCompany = trans.getValue("PARAA1/DATA/COMPANY");
         String Cont = mgr.readValue("HEAD_CONTRACT");
         txt=(mgr.isEmpty(strCompany)? "":strCompany)+ "^"+
             (mgr.isEmpty(Cont)? "":Cont)+ "^";

         mgr.responseWrite( txt );
      }
      else if ("PARAMETER_CODE".equals(val))
      {
         cmd = trans.addCustomFunction("PARA1", "MEASUREMENT_PARAMETER_API.Get_Description", "PARAMETERDESCRIPTION" );
         cmd.addParameter("PARAMETER_CODE");

         cmd = trans.addCustomFunction("PARA2", "MEASUREMENT_PARAMETER_API.Get_Type", "VALUETYPE" );
         cmd.addParameter("PARAMETER_CODE");

         cmd = trans.addCustomFunction("PARA3", "EQUIPMENT_OBJECT_PARAM_API.Get_Unit", "UNITCODE" );
         cmd.addParameter("ITEM3_CONTRACT");
         cmd.addParameter("ITEM3_MCH_CODE");
         cmd.addParameter("ITEM3_TEST_POINT_ID");
         cmd.addParameter("PARAMETER_CODE");

         trans = mgr.validate(trans);

         String paradesc = trans.getValue("PARA1/DATA/PARAMETERDESCRIPTION");
         String type = trans.getValue("PARA2/DATA/VALUETYPE");
         String uncode = trans.getValue("PARA3/DATA/UNITCODE");

         txt = (mgr.isEmpty(paradesc)? "":paradesc)+ "^" + (mgr.isEmpty(type )? "":type )+ "^" + (mgr.isEmpty(uncode )? "":uncode )+ "^" ;
         mgr.responseWrite( txt );
      }
      else if ("SIGNATURE".equals(val))
      {
         cmd = trans.addCustomFunction("SIGNA", "Company_Emp_API.Get_Max_Employee_Id", "SIGNATURE_ID" );
         cmd.addParameter("COMPANY");
         cmd.addParameter("SIGNATURE");

         trans = mgr.validate(trans);

         String strSignDesc = trans.getValue("SIGNA/DATA/SIGNATURE_ID");

         txt=(mgr.isEmpty(strSignDesc)? "":strSignDesc)+ "^";
         mgr.responseWrite( txt );
      }
      else if ("PART_NO".equals(val))
      {
         double salesPaCost;
         String salesPaCostS = mgr.readValue("SALES_PART_COST","");
         String strDesc;
         String sapfpa;
         String strInvFlg;
         String strDim;
         String strTypeDesc;
         String strQtyonHand;
         String strUnit;
         String sDefCondiCode= "";
         String activeInd = "";

         cmd = trans.addCustomFunction("GETCONDCODEUSAGEDB","PART_CATALOG_API.Get_Condition_Code_Usage_Db","COND_CODE_USAGE");
         cmd.addParameter("PART_NO");

         cmd = trans.addCustomFunction("DEFCONDCODE","CONDITION_CODE_API.Get_Default_Condition_Code","CONDITION_CODE");

         cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
         cmd.addParameter("PART_OWNERSHIP",mgr.readValue("PART_OWNERSHIP"));

         trans = mgr.validate(trans);

         String sClientOwnershipDefault = trans.getValue("VALIDOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");
         if ("ALLOW_COND_CODE".equals(trans.getValue("GETCONDCODEUSAGEDB/DATA/COND_CODE_USAGE")))
         {
            sDefCondiCode = trans.getValue("DEFCONDCODE/DATA/CONDITION_CODE");
         }
         trans.clear();

         if (checksec("PURCHASE_PART_API.Get_Description",2,isSecure))
         {
            cmd = trans.addCustomFunction("PPGD1", "PURCHASE_PART_API.Get_Description", "SPAREDESCRIPTION" );
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");
         }
         if (checksec("PURCHASE_PART_API.Get_Inventory_Flag",3,isSecure))
         {
            cmd = trans.addCustomFunction("PPGD2", "PURCHASE_PART_API.Get_Inventory_Flag", "INVENTORYFLAG" );
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");
         }
         if (checksec("Inventory_Part_API.Get_Dim_Quality",4,isSecure))
         {
            cmd = trans.addCustomFunction("PPGD3", "Inventory_Part_API.Get_Dim_Quality", "DIMQUALITY" );
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");
         }
         if (checksec("Inventory_Part_API.Get_Type_Designation",5,isSecure))
         {
            cmd = trans.addCustomFunction("PPGD4", "Inventory_Part_API.Get_Type_Designation", "TYPEDESIGNATION" );
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");
         }
         if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",6,isSecure))
         {
            cmd = trans.addCustomFunction("PPGD5","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
            cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
            cmd.addParameter("PART_NO");
            cmd.addParameter("CONFIG_ID");
            cmd.addParameter("QTY_TYPE",sOnhand);
            cmd.addParameter("EXPIRATION");
            cmd.addParameter("SUPPLY_CONTROL");
            cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
            cmd.addParameter("OWNERSHIP_TYPE2");
            cmd.addParameter("OWNERSHIP_TYPE3");
            cmd.addParameter("OWNERSHIP_TYPE4");
            cmd.addParameter("OWNER");
            cmd.addParameter("OWNER_VENDOR");
            cmd.addParameter("LOCATION_TYPE1",sPicking);
            cmd.addParameter("LOCATION_TYPE2",sF);
            cmd.addParameter("LOCATION_TYPE3",sPallet);
            cmd.addParameter("LOCATION_TYPE4",sDeep);
            cmd.addParameter("LOCATION_TYPE5",sBuffer);
            cmd.addParameter("LOCATION_TYPE6",sDelivery);
            cmd.addParameter("LOCATION_TYPE7",sShipment);
            cmd.addParameter("LOCATION_TYPE8",sManufacturing);
            cmd.addParameter("LOT_BATCH_NO");
            cmd.addParameter("SERIAL_NO");
            cmd.addParameter("ENG_CHG_LEVEL");
            cmd.addParameter("WAIV_DEV_REJ_NO");
            cmd.addParameter("INCLUDE_STANDARD",sTrue);
            cmd.addParameter("INCLUDE_PROJECT",sFalse);
            cmd.addParameter("ACTIVITY_SEQ");
            cmd.addParameter("PROJECT_ID");
            cmd.addParameter("LOCATION_NO");
            cmd.addParameter("ORDER_ISSUE");
            cmd.addParameter("AUTOMAT_RESERV");
            cmd.addParameter("MANUAL_RESERV");
            cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));
         }
         if (checksec("Purchase_Part_Supplier_API.Get_Unit_Meas",7,isSecure))
         {
            cmd = trans.addCustomFunction("PPGD6", "Purchase_Part_Supplier_API.Get_Unit_Meas", "UNITMEAS" );
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");
         }
         if (checksec("Sales_Part_API.Get_Catalog_No_For_Part_No",1,isSecure))
         {
            cmd = trans.addCustomFunction("SPFPART", "Sales_Part_API.Get_Catalog_No_For_Part_No", "ITEM1_CATALOG_NO" );
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");
         }
         if (checksec("INVENTORY_PART_API.Part_Exist",8,isSecure))
         {
            cmd = trans.addCustomFunction("INVENPART","INVENTORY_PART_API.Part_Exist","INVENTORYFLAG"); 
            cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));    
            cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));

            ASPQuery sysd = trans.addQuery("HPSD","select sysdate PSDATE from dual");
         }

         cmd = trans.addCustomFunction("SUUPCODE","INVENTORY_PART_API.Get_Supply_Code ","SUPPLY_CODE");
         cmd.addParameter("ITEM1_CONTRACT");
         cmd.addReference("ITEM1_CATALOG_NO","SPFPART/DATA");

         if ( checksec("Sales_Part_API.Get_Activeind",9,isSecure) )
         {
            cmd = trans.addCustomFunction("GETACT","Sales_Part_API.Get_Activeind","ACTIVEIND");
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addReference("ITEM1_CATALOG_NO","SPFPART/DATA");
         }

         if ( checksec("Active_Sales_Part_API.Encode",10,isSecure) )
         {
            cmd = trans.addCustomFunction("GETACTDB","Active_Sales_Part_API.Encode","ACTIVEIND_DB");
            cmd.addReference("ACTIVEIND","GETACT/DATA");
         }
         trans = mgr.validate(trans);

         ref = 0;

         if (isSecure[2] =="true")
         {
            strDesc = trans.getValue("PPGD1/DATA/SPAREDESCRIPTION");
         }
         else
            strDesc = "";

         if (isSecure[3] =="true")
         {
            strInvFlg = trans.getValue("PPGD2/DATA/INVENTORYFLAG");
         }
         else
            strInvFlg = "";

         if (isSecure[4] =="true")
         {
            strDim = trans.getValue("PPGD3/DATA/DIMQUALITY");
         }
         else
            strDim = "";

         if (isSecure[5] =="true")
         {
            strTypeDesc = trans.getValue("PPGD4/DATA/TYPEDESIGNATION");
         }
         else
            strTypeDesc = "";

         if (isSecure[6] =="true")
         {
            strQtyonHand = trans.getBuffer("PPGD5/DATA").getFieldValue("QTYONHAND");
         }
         else
            strQtyonHand = "";

         if (isSecure[7] =="true")
         {
            strUnit = trans.getValue("PPGD6/DATA/UNITMEAS");
         }
         else
            strUnit = "";

         if (isSecure[1] =="true")
         {
            sapfpa = trans.getValue("SPFPART/DATA/CATALOG_NO");
         }
         else
            sapfpa = "";

         String suppCode = trans.getValue("SUUPCODE/DATA/SUPPLY_CODE");

         if (isSecure[10] =="true")
         {
            activeInd = trans.getValue("GETACTDB/DATA/ACTIVEIND_DB");
         }
         else
            activeInd = "";
         if (isSecure[8] =="true")
         {
            String invenFlag = trans.getValue("INVENPART/DATA/INVENTORYFLAG"); 
            String sysDate = trans.getBuffer("HPSD/DATA").getFieldValue("PSDATE");

            trans.clear();
            if ("1".equals(invenFlag))
            {
               /*cmd = trans.addCustomFunction("SALESPARTCOST","Inventory_Part_API.Get_Inventory_Value_By_Method","SALES_PART_COST"); 
               cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));    
               cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));  */

               cmd = trans.addCustomCommand("SALESPARTCOST","Active_Separate_API.Get_Inventory_Value");
               cmd.addParameter("SALES_PART_COST");
               cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));
               cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
               cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
               cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
               cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));

               trans = mgr.validate(trans);

               salesPaCost =  trans.getNumberValue("SALESPARTCOST/DATA/SALES_PART_COST");
               salesPaCostS = mgr.getASPField("SALES_PART_COST").formatNumber(salesPaCost);
            }
            else
            {
               String nVendor = null;
               cmd = trans.addCustomFunction("PRIMARYSUPP","Purchase_Order_Line_Util_API.Get_Primary_Supplier","PRIMARY_SUPPLIER"); 
               cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));     
               cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));

               trans = mgr.validate(trans);

               nVendor = trans.getValue("PRIMARYSUPP/DATA/PRIMARY_SUPPLIER");
               trans.clear();

               if (!mgr.isEmpty(nVendor))
               {
                  cmd = trans.addCustomCommand("SALESPARTCOST1", "Purchase_Order_Line_Util_API.Get_Base_Price_Supp_Part");
                  cmd.addParameter("BASE_PRICE");
                  cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
                  cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT")); 
                  cmd.addParameter("PRIMARY_SUPPLIER",nVendor);
                  cmd.addParameter("QTY_PLAN",mgr.readValue("QTY_PLAN"));
                  cmd.addParameter("PSDATE",sysDate);

                  trans = mgr.validate(trans);

                  salesPaCost =  trans.getNumberValue("SALESPARTCOST1/DATA/BASE_PRICE");

                  if (isNaN(salesPaCost))
                     salesPaCost=0;
                  salesPaCostS =  mgr.getASPField("SALES_PART_COST").formatNumber(salesPaCost);  
               }
            }
         }

         trans.clear();

         cmd = trans.addCustomFunction("PMSITE","Pm_Action_API.Get_Contract","PM_SITE");
         cmd.addParameter("ITEM1_PM_NO",mgr.readValue("ITEM1_PM_NO"));
         cmd.addParameter("ITEM1_PM_REVISION",mgr.readValue("ITEM1_PM_REVISION"));

         cmd = trans.addCustomFunction("CATEXT","Sales_Part_API.Check_Exist","CAT_EXIST");
         cmd.addReference("PM_SITE","PMSITE/DATA");
         cmd.addParameter("CATALOG_NO",sapfpa);

         trans = mgr.validate(trans);

         double nExist = trans.getNumberValue("CATEXT/DATA/CAT_EXIST");

         if (nExist != 1)
         {
            sapfpa = "";
            suppCode = "";
         }

         trans.clear();

         cmd = trans.addCustomCommand("MATREQLINE","Material_Requis_Line_API.CHECK_PART_NO__");
         cmd.addParameter("SPAREDESCRIPTION",strDesc);
         cmd.addParameter("SUPPLY_CODE",suppCode);
         cmd.addParameter("UNITMEAS",strUnit);
         cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
         cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));

         mgr.validate(trans);

         txt=(mgr.isEmpty(sapfpa )? "":sapfpa )+ "^" + (mgr.isEmpty(strDesc )? "":strDesc )+ "^" + (mgr.isEmpty(strInvFlg )? "":strInvFlg )+ "^" + (mgr.isEmpty(strDim )? "":strDim )+ "^" + (mgr.isEmpty(strTypeDesc )? "":strTypeDesc ) + "^" + (mgr.isEmpty(strQtyonHand )? "":strQtyonHand ) + "^" + (mgr.isEmpty(strUnit )? "":strUnit ) + "^"+(mgr.isEmpty(salesPaCostS) ? "" :salesPaCostS) + "^"+(mgr.isEmpty(sDefCondiCode) ? "" :sDefCondiCode) + "^"+(mgr.isEmpty(sClientOwnershipDefault) ? "" :sClientOwnershipDefault) + "^"+(mgr.isEmpty(activeInd) ? "" :activeInd) + "^"; 

         mgr.responseWrite( txt );
      }
      else if ("CONDITION_CODE".equals(val))
      {
         String qtyOnHand = "";
         double salesPaCost;

         cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
         cmd.addParameter("PART_OWNERSHIP",mgr.readValue("PART_OWNERSHIP"));

         if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",1,isSecure))
         {
            cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
            cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));
            cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
            cmd.addParameter("CONFIG_ID");
            cmd.addParameter("QTY_TYPE",sOnhand);
            cmd.addParameter("EXPIRATION");
            cmd.addParameter("SUPPLY_CONTROL");
            cmd.addReference("PART_OWNERSHIP_DB","VALIDOWNERSHIPDB/DATA");
            cmd.addParameter("OWNERSHIP_TYPE2");
            cmd.addParameter("OWNERSHIP_TYPE3");
            cmd.addParameter("OWNERSHIP_TYPE4");
            cmd.addParameter("OWNER");
            cmd.addParameter("OWNER_VENDOR");
            cmd.addParameter("LOCATION_TYPE1",sPicking);
            cmd.addParameter("LOCATION_TYPE2",sF);
            cmd.addParameter("LOCATION_TYPE3",sPallet);
            cmd.addParameter("LOCATION_TYPE4",sDeep);
            cmd.addParameter("LOCATION_TYPE5",sBuffer);
            cmd.addParameter("LOCATION_TYPE6",sDelivery);
            cmd.addParameter("LOCATION_TYPE7",sShipment);
            cmd.addParameter("LOCATION_TYPE8",sManufacturing);
            cmd.addParameter("LOT_BATCH_NO");
            cmd.addParameter("SERIAL_NO");
            cmd.addParameter("ENG_CHG_LEVEL");
            cmd.addParameter("WAIV_DEV_REJ_NO");
            cmd.addParameter("INCLUDE_STANDARD",sTrue);
            cmd.addParameter("INCLUDE_PROJECT",sFalse);
            cmd.addParameter("ACTIVITY_SEQ");
            cmd.addParameter("PROJECT_ID");
            cmd.addParameter("LOCATION_NO");
            cmd.addParameter("ORDER_ISSUE");
            cmd.addParameter("AUTOMAT_RESERV");
            cmd.addParameter("MANUAL_RESERV");
            cmd.addParameter("CONDITION_CODE");
         }
         cmd = trans.addCustomFunction("CCDESC", "Condition_Code_API.Get_Description", "CONDITION_CODE_DESC");
         cmd.addParameter("CONDITION_CODE"); 

         trans = mgr.validate(trans);
         ref = 0;

         if (isSecure[ref += 1] =="true")
            qtyOnHand = trans.getBuffer("INVONHAND/DATA").getFieldValue("QTYONHAND");
         else
            qtyOnHand = "";

         String ccDesc = trans.getValue("CCDESC/DATA/CONDITION_CODE_DESC");
         String salesPaCostS = mgr.readValue("SALES_PART_COST","");

         trans.clear();

         cmd = trans.addCustomCommand("SALESPARTCOST","Active_Separate_API.Get_Inventory_Value");
         cmd.addParameter("SALES_PART_COST");
         cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));
         cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
         cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
         cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
         cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));

         trans = mgr.validate(trans);

         salesPaCost =  trans.getNumberValue("SALESPARTCOST/DATA/SALES_PART_COST");

         salesPaCostS = mgr.getASPField("SALES_PART_COST").formatNumber(salesPaCost);

         txt = (mgr.isEmpty(ccDesc) ? "" : (ccDesc)) + "^" +
               (mgr.isEmpty(qtyOnHand) ? "": qtyOnHand) + "^" +
               (mgr.isEmpty(salesPaCostS) ? "": salesPaCostS)+ "^" ;  

         mgr.responseWrite(txt);
      }
      else if ("PART_OWNERSHIP".equals(val))
      {
         cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
         cmd.addParameter("PART_OWNERSHIP",mgr.readValue("PART_OWNERSHIP"));
         String qtyOnHand = "";

         if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",1,isSecure))
         {
            cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
            cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
            cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
            cmd.addParameter("CONFIG_ID");
            cmd.addParameter("QTY_TYPE",sOnhand);
            cmd.addParameter("EXPIRATION");
            cmd.addParameter("SUPPLY_CONTROL");
            cmd.addReference("PART_OWNERSHIP_DB","VALIDOWNERSHIPDB/DATA");
            cmd.addParameter("OWNERSHIP_TYPE2");
            cmd.addParameter("OWNERSHIP_TYPE3");
            cmd.addParameter("OWNERSHIP_TYPE4");
            cmd.addParameter("OWNER");
            cmd.addParameter("OWNER_VENDOR");
            cmd.addParameter("LOCATION_TYPE1",sPicking);
            cmd.addParameter("LOCATION_TYPE2",sF);
            cmd.addParameter("LOCATION_TYPE3",sPallet);
            cmd.addParameter("LOCATION_TYPE4",sDeep);
            cmd.addParameter("LOCATION_TYPE5",sBuffer);
            cmd.addParameter("LOCATION_TYPE6",sDelivery);
            cmd.addParameter("LOCATION_TYPE7",sShipment);
            cmd.addParameter("LOCATION_TYPE8",sManufacturing);
            cmd.addParameter("LOT_BATCH_NO");
            cmd.addParameter("SERIAL_NO");
            cmd.addParameter("ENG_CHG_LEVEL");
            cmd.addParameter("WAIV_DEV_REJ_NO");
            cmd.addParameter("INCLUDE_STANDARD",sTrue);
            cmd.addParameter("INCLUDE_PROJECT",sFalse);
            cmd.addParameter("ACTIVITY_SEQ");
            cmd.addParameter("PROJECT_ID");
            cmd.addParameter("LOCATION_NO");
            cmd.addParameter("ORDER_ISSUE");
            cmd.addParameter("AUTOMAT_RESERV");
            cmd.addParameter("MANUAL_RESERV");
            cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));
         }

         trans = mgr.validate(trans);
         ref = 0; 

         if (isSecure[ref += 1] =="true")
            qtyOnHand = trans.getBuffer("INVONHAND/DATA").getFieldValue("QTYONHAND");
         else
            qtyOnHand = "";

         String sOwnershipDb = trans.getValue("VALIDOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");

         txt = (mgr.isEmpty(sOwnershipDb) ? "" : (sOwnershipDb)) + "^" +
               (mgr.isEmpty(qtyOnHand) ? "": qtyOnHand) + "^" ;  
         mgr.responseWrite(txt);
      }
      else if ("OWNER".equals(val))
      {
         cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
         cmd.addParameter("PART_OWNERSHIP");
         String qtyOnHand = "";

         if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",1,isSecure))
         {
            cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
            cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
            cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
            cmd.addParameter("CONFIG_ID");
            cmd.addParameter("QTY_TYPE",sOnhand);
            cmd.addParameter("EXPIRATION");
            cmd.addParameter("SUPPLY_CONTROL");
            cmd.addReference("PART_OWNERSHIP_DB","VALIDOWNERSHIPDB/DATA");
            cmd.addParameter("OWNERSHIP_TYPE2");
            cmd.addParameter("OWNERSHIP_TYPE3");
            cmd.addParameter("OWNERSHIP_TYPE4");
            cmd.addParameter("OWNER");
            cmd.addParameter("OWNER_VENDOR");
            cmd.addParameter("LOCATION_TYPE1",sPicking);
            cmd.addParameter("LOCATION_TYPE2",sF);
            cmd.addParameter("LOCATION_TYPE3",sPallet);
            cmd.addParameter("LOCATION_TYPE4",sDeep);
            cmd.addParameter("LOCATION_TYPE5",sBuffer);
            cmd.addParameter("LOCATION_TYPE6",sDelivery);
            cmd.addParameter("LOCATION_TYPE7",sShipment);
            cmd.addParameter("LOCATION_TYPE8",sManufacturing);
            cmd.addParameter("LOT_BATCH_NO");
            cmd.addParameter("SERIAL_NO");
            cmd.addParameter("ENG_CHG_LEVEL");
            cmd.addParameter("WAIV_DEV_REJ_NO");
            cmd.addParameter("INCLUDE_STANDARD",sTrue);
            cmd.addParameter("INCLUDE_PROJECT",sFalse);
            cmd.addParameter("ACTIVITY_SEQ");
            cmd.addParameter("PROJECT_ID");
            cmd.addParameter("LOCATION_NO");
            cmd.addParameter("ORDER_ISSUE");
            cmd.addParameter("AUTOMAT_RESERV");
            cmd.addParameter("MANUAL_RESERV");
            cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));
         }
         cmd = trans.addCustomFunction("GETOWNERNAME", "CUSTOMER_INFO_API.Get_Name", "OWNER_NAME");
         cmd.addParameter("OWNER");

         trans = mgr.validate(trans);
         ref = 0;

         if (isSecure[ref += 1] =="true")
            qtyOnHand = trans.getBuffer("INVONHAND/DATA").getFieldValue("QTYONHAND");
         else
            qtyOnHand = "";

         String sOwnerName = trans.getValue("GETOWNERNAME/DATA/OWNER_NAME");

         txt = (mgr.isEmpty(sOwnerName) ? "" : (sOwnerName)) + "^" +
               (mgr.isEmpty(qtyOnHand) ? "": qtyOnHand) + "^" ;  
         mgr.responseWrite(txt);
      }
      else if ("TEST_POINT_ID".equals(val))
      {
         cmd = trans.addCustomFunction("TEST1", "EQUIPMENT_OBJECT_TEST_PNT_API.Get_Description", "MSEQOBJECTTESTPOINTDESCRIPTION" );
         cmd.addParameter("HEAD_CONTRACT");
         cmd.addParameter("MCH_CODE");
         cmd.addParameter("TEST_POINT_ID");

         cmd = trans.addCustomFunction("TEST2", "EQUIPMENT_OBJECT_TEST_PNT_API.Get_Location", "MSEQOBJECTTESTPOINTLOCATION" );
         cmd.addParameter("HEAD_CONTRACT");
         cmd.addParameter("MCH_CODE");
         cmd.addParameter("TEST_POINT_ID");

         trans = mgr.validate(trans);

         String descr = trans.getValue("TEST1/DATA/MSEQOBJECTTESTPOINTDESCRIPTION");
         String locat = trans.getValue("TEST2/DATA/MSEQOBJECTTESTPOINTLOCATION");

         txt=(mgr.isEmpty(descr)? "":descr)+ "^" + (mgr.isEmpty(locat)? "":locat)+ "^" ;

         mgr.responseWrite( txt );
      }
      else if ("ITEM0_ORG_CODE".equals(val))
      {
         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[6];

         String decost = "";
         String decat = "";
         String decatdes = "";

         String sOrgCode = "";
         String sOrgContract = "";
         String new_org_code = mgr.readValue("ITEM0_ORG_CODE","");
         String sCatCost;

         if (new_org_code.indexOf("^",0)>0)
         {
            if (!mgr.isEmpty(mgr.readValue("ROLE_CODE")))
            {
               for (i=0 ; i<5; i++)
               {
                  endpos = new_org_code.indexOf("^",startpos);
                  reqstr = new_org_code.substring(startpos,endpos);
                  ar[i] = reqstr;
                  startpos= endpos+1;
               }
               sOrgCode = ar[3];
               sOrgContract = ar[4];
            }
            else
            {
               if (!mgr.isEmpty(mgr.readValue("ITEM0_SIGNATURE")))
               {
                  for (i=0 ; i<4; i++)
                  {
                     endpos = new_org_code.indexOf("^",startpos);
                     reqstr = new_org_code.substring(startpos,endpos);
                     ar[i] = reqstr;
                     startpos= endpos+1;
                  }
                  sOrgCode = ar[2];
                  sOrgContract = ar[3];
               }
               else
               {
                  for (i=0 ; i<2; i++)
                  {
                     endpos = new_org_code.indexOf("^",startpos);
                     reqstr = new_org_code.substring(startpos,endpos);
                     ar[i] = reqstr;
                     startpos= endpos+1;
                  }
                  sOrgCode = ar[0];
                  sOrgContract = ar[1];
               }
            }
         }
         else
         {
            sOrgCode = mgr.readValue("ITEM0_ORG_CODE");
            sOrgContract = mgr.readValue("ITEM0_ORG_CONTRACT"); 
         }

         if (mgr.readValue("ITEM0_CONTRACT").equals(sOrgContract))
         {
            if (checksec("Organization_Sales_Part_API.Get_Def_Catalog_No",1,isSecure))
            {
               cmd = trans.addCustomFunction("DEPTPA", "Organization_Sales_Part_API.Get_Def_Catalog_No", "ITEM0_CATALOG_NO" );
               cmd.addParameter("ITEM0_ORG_CONTRACT",sOrgContract);
               cmd.addParameter("ITEM0_ORG_CODE",sOrgCode);
            }
            if (checksec("Sales_Part_API.Get_Catalog_Desc",2,isSecure))
            {
               cmd = trans.addCustomFunction("DEPTCADES", "Sales_Part_API.Get_Catalog_Desc", "SALESPARTDESCRIPTION" );
               cmd.addParameter("ITEM0_CONTRACT",mgr.readValue("ITEM0_CONTRACT"));
               cmd.addReference("ITEM0_CATALOG_NO","DEPTPA/DATA");
            }
            if (checksec("SALES_PART_API.Get_List_Price",3,isSecure))
            {
               cmd = trans.addCustomFunction("DEPTCO", "SALES_PART_API.Get_List_Price", "LISTPRICE" );
               cmd.addParameter("ITEM0_CONTRACT",mgr.readValue("ITEM0_CONTRACT"));
               cmd.addReference("ITEM0_CATALOG_NO","DEPTPA/DATA");
            }

            trans = mgr.validate(trans);

            ref = 0;

            if (isSecure[3] =="true")
            {
               decost = trans.getValue("DEPTCO/DATA/LISTPRICE");
            }
            else
               decost =  "";

            if (isSecure[1] =="true")
            {
               decat = trans.getValue("DEPTPA/DATA/CATALOG_NO");      
            }
            else
               decat   = null;

            if (isSecure[2] =="true")
            {
               decatdes = trans.getValue("DEPTCADES/DATA/SALESPARTDESCRIPTION");      
            }
            else
               decatdes    = null;
         }

         trans.clear();
         cmd = trans.addCustomFunction("CATCOST","Work_Order_Planning_Util_API.Get_Cost","CATALOG_COST");
         cmd.addParameter("ITEM0_ORG_CODE",sOrgCode);
         cmd.addParameter("ROLE_CODE",mgr.readValue("ROLE_CODE"));
         cmd.addParameter("ITEM0_CONTRACT",mgr.readValue("ITEM0_CONTRACT"));
         cmd.addParameter("ITEM0_CATALOG_NO",decat);
         cmd.addParameter("ITEM0_ORG_CONTRACT",sOrgContract);
         trans = mgr.validate(trans);

         sCatCost = trans.getValue("CATCOST/DATA/CATALOG_COST");

         txt=(mgr.isEmpty(decost)? "":decost)+ "^" + (mgr.isEmpty(decat)? "":decat)+ "^" + (mgr.isEmpty(decatdes)? "":decatdes)+ "^"+ (mgr.isEmpty(sOrgCode)? "":sOrgCode)+ "^"+ (mgr.isEmpty(sOrgContract)? "":sOrgContract)+ "^"+
             (mgr.isEmpty(sCatCost)? "":sCatCost)+ "^"; 

         mgr.responseWrite(txt);      
      }
      else if ("ROLE_CODE".equals(val))
      {
         String rocost;
         String rocode;
         String rocont = "";
         String saldesc;

         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[6];

         String sRoleCode = "";
         String sOrgContract = "";
         String sCatCost;
         String sCatCont = mgr.readValue("ITEM0_CONTRACT","");
         String new_role_code = mgr.readValue("ROLE_CODE","");
         if (new_role_code.indexOf("^",0)>0)
         {
            if (!mgr.isEmpty(mgr.readValue("ITEM0_SIGNATURE")))
            {
               for (i=0 ; i<5; i++)
               {
                  endpos = new_role_code.indexOf("^",startpos);
                  reqstr = new_role_code.substring(startpos,endpos);
                  ar[i] = reqstr;
                  startpos= endpos+1;

               }
               sRoleCode = ar[2];
               sOrgContract = ar[4];
            }
            else
            {
               for (i=0 ; i<2; i++)
               {

                  endpos = new_role_code.indexOf("^",startpos);
                  reqstr = new_role_code.substring(startpos,endpos);
                  ar[i] = reqstr;
                  startpos= endpos+1;
               }
               sRoleCode = ar[0];
               sOrgContract = ar[1];
            }
         }
         else
         {
            sRoleCode = mgr.readValue("ROLE_CODE");
            sOrgContract = mgr.readValue("ITEM0_ORG_CONTRACT"); 
         }

         if (checksec("Role_Sales_Part_API.Get_Def_Catalog_No",1,isSecure))
         {
            cmd = trans.addCustomFunction("SAPANO", "Role_Sales_Part_API.Get_Def_Catalog_No", "ITEM0_CATALOG_NO" );
            cmd.addParameter("ROLE_CODE",sRoleCode);
            cmd.addParameter("ITEM0_CONTRACT",sCatCont);
         }
         if (checksec("Sales_Part_API.Get_Catalog_Desc",2,isSecure))
         {
            cmd = trans.addCustomFunction("SADES", "Sales_Part_API.Get_Catalog_Desc", "SALESPARTDESCRIPTION" );
            cmd.addParameter("ITEM0_CONTRACT",sCatCont);
            cmd.addReference("ITEM0_CATALOG_NO","SAPANO/DATA");
         }
         if (checksec("SALES_PART_API.Get_List_Price",3,isSecure))
         {
            cmd = trans.addCustomFunction("ROCO", "SALES_PART_API.Get_List_Price", "LISTPRICE" );
            cmd.addParameter("ITEM0_CONTRACT",sCatCont);
            cmd.addReference("ITEM0_CATALOG_NO","SAPANO/DATA");
         }

         trans = mgr.validate(trans);

         ref = 0;

         if (isSecure[3] =="true")
         {
            rocost = trans.getValue("ROCO/DATA/LISTPRICE");
         }
         else
            rocost =  "";

         if (isSecure[1] =="true")
         {
            rocode = trans.getValue("SAPANO/DATA/CATALOG_NO");      
         }
         else
            rocode = null;

         if (isSecure[2] =="true")
         {
            saldesc = trans.getValue("SADES/DATA/SALESPARTDESCRIPTION");      
         }
         else
            saldesc = null;

         trans.clear();
         if (mgr.isEmpty(rocode)|| mgr.isEmpty(sRoleCode))
         {
            cmd = trans.addCustomFunction("SAPANO", "Organization_Sales_Part_API.Get_Def_Catalog_No ", "ITEM0_CATALOG_NO");
            cmd.addParameter("ITEM0_CONTRACT");  
            cmd.addParameter("ITEM0_ORG_CODE",mgr.readValue("ITEM0_ORG_CODE"));

            cmd = trans.addCustomFunction("SADES", "Sales_Part_API.Get_Catalog_Desc", "SALESPARTDESCRIPTION");
            cmd.addParameter("ITEM0_CONTRACT");
            cmd.addReference("ITEM0_CATALOG_NO", "SAPANO/DATA");  

            cmd = trans.addCustomFunction("ROCO", "SALES_PART_API.Get_List_Price", "LISTPRICE" );
            cmd.addParameter("ITEM0_CONTRACT",sCatCont);
            cmd.addReference("ITEM0_CATALOG_NO","SAPANO/DATA");

            trans = mgr.validate(trans);

            rocode = trans.getValue("SAPANO/DATA/CATALOG_NO");  
            saldesc = trans.getValue("SADES/DATA/SALESPARTDESCRIPTION");  
            rocost = trans.getValue("ROCO/DATA/LISTPRICE");
         }

         trans.clear();
         cmd = trans.addCustomFunction("CATCOST","Work_Order_Planning_Util_API.Get_Cost","CATALOG_COST");
         cmd.addParameter("ITEM0_ORG_CODE",mgr.readValue("ITEM0_ORG_CODE"));
         cmd.addParameter("ROLE_CODE",sRoleCode);
         cmd.addParameter("ITEM0_CONTRACT",sCatCont);
         cmd.addParameter("ITEM0_CATALOG_NO",rocode);
         cmd.addParameter("ITEM0_ORG_CONTRACT",sOrgContract);
         trans = mgr.validate(trans);

         sCatCost = trans.getValue("CATCOST/DATA/CATALOG_COST");

         txt=(mgr.isEmpty(rocost)? "":rocost)+ "^" + (mgr.isEmpty(rocode)? "":rocode)+ "^" + (mgr.isEmpty(sOrgContract)? "":sOrgContract)+ "^" + (mgr.isEmpty(saldesc)? "":saldesc)+ "^"+ (mgr.isEmpty(sRoleCode)? "":sRoleCode)+ "^"+
             (mgr.isEmpty(sCatCost)? "":sCatCost)+ "^"; 


         mgr.responseWrite(txt);      
      }
      else if ("ITEM0_CATALOG_NO".equals(val))
      {
         String sCatCost;

         if (checksec("SALES_PART_API.Get_Catalog_Desc",1,isSecure))
         {
            cmd = trans.addCustomFunction("CATDESC", "SALES_PART_API.Get_Catalog_Desc", "SALESPARTDESCRIPTION" );
            cmd.addParameter("ITEM0_CONTRACT");
            cmd.addParameter("ITEM0_CATALOG_NO");
         }
         if (checksec("SALES_PART_API.Get_List_Price",2,isSecure))
         {
            cmd = trans.addCustomFunction("LISPRI", "SALES_PART_API.Get_List_Price", "LISTPRICE" );
            cmd.addParameter("ITEM0_CONTRACT");
            cmd.addParameter("ITEM0_CATALOG_NO"); 
         }

         cmd = trans.addCustomFunction("CATCOST","Work_Order_Planning_Util_API.Get_Cost","CATALOG_COST");
         cmd.addParameter("ITEM0_ORG_CODE");
         cmd.addParameter("ROLE_CODE");
         cmd.addParameter("ITEM0_CONTRACT");
         cmd.addParameter("ITEM0_CATALOG_NO");
         cmd.addParameter("ITEM0_ORG_CONTRACT");

         trans = mgr.validate(trans);

         ref = 0;

         if (isSecure[1] =="true")
         {
            strCatDesc = trans.getValue("CATDESC/DATA/SALESPARTDESCRIPTION");
         }
         else
            strCatDesc =  "";

         if (isSecure[2] =="true")
         {
            numListPrice = trans.getValue("LISPRI/DATA/LISTPRICE");      
         }
         else
            numListPrice = null;

         sCatCost = trans.getValue("CATCOST/DATA/CATALOG_COST");

         txt=(mgr.isEmpty(strCatDesc)? "":strCatDesc)+ "^" + (mgr.isEmpty(numListPrice)? "":numListPrice)+ "^"+
             (mgr.isEmpty(sCatCost)? "":sCatCost)+ "^";

         mgr.responseWrite(txt);      
      }
      else if ("ITEM1_CATALOG_NO".equals(val))
      {
         if (checksec("SALES_PART_API.Get_Catalog_Desc",1,isSecure))
         {
            cmd = trans.addCustomFunction("CATDESC1", "SALES_PART_API.Get_Catalog_Desc", "SALESPARTDESCRIPTION1" );
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("ITEM1_CATALOG_NO");
         }
         if (checksec("SALES_PART_API.Get_List_Price",2,isSecure))
         {
            cmd = trans.addCustomFunction("LISPRI1", "SALES_PART_API.Get_List_Price", "ITEM1_LISTPRICE" );
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("ITEM1_CATALOG_NO");    
         }

         trans = mgr.validate(trans);

         ref = 0;

         if (isSecure[ref += 1] =="true")
         {
            strCatDesc = trans.getValue("CATDESC1/DATA/SALESPARTDESCRIPTION1");
         }
         else
            strCatDesc =  "";

         if (isSecure[ref += 1] =="true")
         {
            double numListPricen = trans.getNumberValue("LISPRI1/DATA/ITEM1_LISTPRICE");
            numListPrice =mgr.getASPField("ITEM1_LISTPRICE").formatNumber(numListPricen);
         }
         else
            numListPrice = "";


         txt = (mgr.isEmpty(strCatDesc)? "":strCatDesc)+ "^" + (mgr.isEmpty(numListPrice)? "":numListPrice)+ "^";
         mgr.responseWrite(txt);      
      }
      else if ("MCH_CODE".equals(val))
      {
         String validationAttrAtr1 = "";
         String mchCode = "";
         String mchContract = "";
         String descrip ="";
         String gid = "";
         trans.clear();

         if (mgr.readValue("MCH_CODE").indexOf("~") > -1)
         {
            mchCode = mgr.readValue("MCH_CODE").substring(0,mgr.readValue("MCH_CODE").indexOf("~"));       

            validationAttrAtr1 = mgr.readValue("MCH_CODE").substring(mgr.readValue("MCH_CODE").indexOf("~")+1,mgr.readValue("MCH_CODE").length());

            descrip =  validationAttrAtr1.substring(0,validationAttrAtr1.indexOf("~"));

            mchContract =  validationAttrAtr1.substring(validationAttrAtr1.indexOf("~")+1,validationAttrAtr1.length());                

            cmd = trans.addCustomFunction("DESC","Maintenance_Object_API.Get_Mch_Name","MCH_NAME");
            cmd.addParameter("HEAD_CONTRACT",mchContract);
            cmd.addParameter("MCH_CODE",mchCode);

            cmd = trans.addCustomFunction("DESC1","EQUIPMENT_OBJECT_API.Get_Group_Id","GROUP_ID");
            cmd.addParameter("HEAD_CONTRACT",mchContract);
            cmd.addParameter("MCH_CODE",mchCode);

            trans = mgr.validate(trans);

            descrip =   trans.getValue("DESC/DATA/MCH_NAME");
            gid = trans.getValue("DESC1/DATA/GROUP_ID");

         }
         else
         {
            mchCode = mgr.readValue("MCH_CODE");

            if (mgr.isEmpty(mgr.readValue("HEAD_CONTRACT")))
            {
               cmd = trans.addCustomFunction("MACHCONTRACT","EQUIPMENT_OBJECT_API.Get_Contract","HEAD_CONTRACT");
               cmd.addParameter("MCH_CODE",mchCode);

               cmd = trans.addCustomFunction("DESC","Maintenance_Object_API.Get_Mch_Name","MCH_NAME");
               cmd.addReference("HEAD_CONTRACT","MACHCONTRACT/DATA");
               cmd.addParameter("MCH_CODE",mchCode);

               cmd = trans.addCustomFunction("DESC1","EQUIPMENT_OBJECT_API.Get_Group_Id","GROUP_ID");
               cmd.addReference("HEAD_CONTRACT","MACHCONTRACT/DATA");
               cmd.addParameter("MCH_CODE",mchCode);

               trans = mgr.validate(trans);

               mchContract = trans.getValue("MACHCONTRACT/DATA/CONTRACT");
               descrip =   trans.getValue("DESC/DATA/MCH_NAME");
               gid = trans.getValue("DESC1/DATA/GROUP_ID");
            }
            else
            {
               mchContract = mgr.readValue("HEAD_CONTRACT");

               cmd = trans.addCustomFunction("DESC","Maintenance_Object_API.Get_Mch_Name","MCH_NAME");
               cmd.addParameter("HEAD_CONTRACT",mchContract);
               cmd.addParameter("MCH_CODE",mchCode);

               cmd = trans.addCustomFunction("DESC1","EQUIPMENT_OBJECT_API.Get_Group_Id","GROUP_ID");
               cmd.addParameter("HEAD_CONTRACT",mchContract);
               cmd.addParameter("MCH_CODE",mchCode);

               trans = mgr.validate(trans);

               descrip =   trans.getValue("DESC/DATA/MCH_NAME");
               gid = trans.getValue("DESC1/DATA/GROUP_ID");
            }
         }

         mgr.responseWrite((mgr.isEmpty(mchCode ) ? "" :mchCode ) + "^" +
                           (mgr.isEmpty(descrip ) ? "" :descrip ) + "^" +
                           (mgr.isEmpty(mchContract ) ? "" :mchContract ) + "^" +
                           (mgr.isEmpty(gid) ? "" :gid ));
      }
      else if (("PERS_BUDGET_COST".equals(val)) ||  ("MAT_BUDGET_COST".equals(val)) || ("EXT_BUDGET_COST".equals(val)) ||  ("TF_BUDGET_COST".equals(val)) ||  ("EXP_BUDGET_COST".equals(val)) ||  ("FIX_BUDGET_COST".equals(val)))
      {
         double nPersCost = mgr.isEmpty(mgr.readValue("PERS_BUDGET_COST")) ? 0 : mgr.readNumberValue("PERS_BUDGET_COST");
         double nMatCost = mgr.isEmpty(mgr.readValue("MAT_BUDGET_COST")) ? 0 : mgr.readNumberValue("MAT_BUDGET_COST");
         double nToolCost = mgr.isEmpty(mgr.readValue("TF_BUDGET_COST")) ? 0 : mgr.readNumberValue("TF_BUDGET_COST");
         double nExtCost = mgr.isEmpty(mgr.readValue("EXT_BUDGET_COST")) ? 0 : mgr.readNumberValue("EXT_BUDGET_COST");
         double nExpCost = mgr.isEmpty(mgr.readValue("EXP_BUDGET_COST")) ? 0 : mgr.readNumberValue("EXP_BUDGET_COST");
         double nFixCost = mgr.isEmpty(mgr.readValue("FIX_BUDGET_COST")) ? 0 : mgr.readNumberValue("FIX_BUDGET_COST");

         double nTotCost =toDouble(nPersCost) + toDouble(nMatCost)+toDouble(nExtCost)+ toDouble(nToolCost)+ toDouble(nExpCost)+ toDouble(nFixCost);

         txt=(isNaN(nTotCost)? "" : mgr.formatNumber("TOT_BUDGET_COST",nTotCost)  )+ "^";

         mgr.responseWrite(txt);
      }
      else if ("CUSTOMER_NO".equals(val))
      {
         String sCustName;
         String nIsCreditStopped;

         if (checksec("CUSTOMER_INFO_API.Get_Name",1,isSecure))
         {
            cmd = trans.addCustomFunction("GETCUSTNAME","CUSTOMER_INFO_API.Get_Name","CUSTOMERNAME");
            cmd.addParameter("CUSTOMER_NO");
         }
         if (checksec("CUST_ORD_CUSTOMER_API.Customer_Is_Credit_Stopped",2,isSecure))
         {
            cmd = trans.addCustomFunction("GETCUSTCREDIT","CUST_ORD_CUSTOMER_API.Customer_Is_Credit_Stopped","ISCREDITSTOPPED");
            cmd.addParameter("CUSTOMER_NO");
            cmd.addParameter("COMPANY");
         }

         trans = mgr.validate(trans);

         ref = 1;

         if (isSecure[1] =="true")
            sCustName = trans.getValue("GETCUSTNAME/DATA/CUSTOMERNAME");
         else
            sCustName = "";

         if (isSecure[2] =="true")
            nIsCreditStopped = trans.getValue("GETCUSTCREDIT/DATA/ISCREDITSTOPPED");
         else
            nIsCreditStopped = null;

         txt = (mgr.isEmpty(sCustName) ? "" :sCustName) + "^" + (mgr.isEmpty(nIsCreditStopped) ? "2" :nIsCreditStopped) + "^";
         mgr.responseWrite(txt); 
      }
      else if ("QTY_PLAN".equals(val))
      {
         double salesPaCost;
         String salesPaCostS;

         if (checksec("INVENTORY_PART_API.Part_Exist",1,isSecure))
         {
            cmd = trans.addCustomFunction("INVENPART","INVENTORY_PART_API.Part_Exist","INVENTORYFLAG"); 
            cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));    
            cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));

            ASPQuery sysd = trans.addQuery("HPSD","select sysdate PSDATE from dual");

            trans = mgr.validate(trans);
            String invenFlag = trans.getValue("INVENPART/DATA/INVENTORYFLAG"); 
            String sysDate = trans.getBuffer("HPSD/DATA").getFieldValue("PSDATE");

            trans.clear();

            if ("1".equals(invenFlag))
            {
               /*cmd = trans.addCustomFunction("SALESPARTCOST","Inventory_Part_API.Get_Inventory_Value_By_Method","SALES_PART_COST"); 
               cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));    
               cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));*/

               cmd = trans.addCustomCommand("SALESPARTCOST","Active_Separate_API.Get_Inventory_Value");
               cmd.addParameter("SALES_PART_COST");
               cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));
               cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
               cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
               cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
               cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));

               trans = mgr.perform(trans);

               salesPaCost =  trans.getNumberValue("SALESPARTCOST/DATA/SALES_PART_COST");
               salesPaCostS = mgr.getASPField("SALES_PART_COST").formatNumber(salesPaCost);
            }
            else
            {
               String nVendor = null;

               cmd = trans.addCustomFunction("PRIMARYSUPP","Purchase_Order_Line_Util_API.Get_Primary_Supplier","PRIMARY_SUPPLIER"); 
               cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));     
               cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));

               trans = mgr.validate(trans);
               nVendor = trans.getValue("PRIMARYSUPP/DATA/PRIMARY_SUPPLIER");
               trans.clear();

               if (!mgr.isEmpty(nVendor))
               {
                  cmd = trans.addCustomCommand("SALESPARTCOST1", "Purchase_Order_Line_Util_API.Get_Base_Price_Supp_Part");
                  cmd.addParameter("BASE_PRICE");
                  cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
                  cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT")); 
                  cmd.addParameter("PRIMARY_SUPPLIER",nVendor);
                  cmd.addParameter("QTY_PLAN",mgr.readValue("QTY_PLAN"));
                  cmd.addParameter("PSDATE",sysDate);

                  trans = mgr.validate(trans);

                  salesPaCost =  trans.getNumberValue("SALESPARTCOST1/DATA/BASE_PRICE");

                  if (isNaN(salesPaCost))
                     salesPaCost=0;

                  salesPaCostS =  mgr.getASPField("SALES_PART_COST").formatNumber(salesPaCost);
               }
               else
                  salesPaCostS = "";
            }                   
         }
         else salesPaCostS = "";

         trans.clear();
         String sCurrCode="";
         String sRevenue = mgr.readValue("ITEM1_LISTPRICE","");

         if (checksec("Customer_Agreement_API.Get_Currency_Code",1,isSecure) && checksec("Cust_Ord_Customer_API.Get_Currency_Code",1,isSecure))
         {
            String sAgrCurrencyCode="";
            String sCustCurrencyCode="";

            cmd = trans.addCustomFunction("GETAGRCURRCODE","CUSTOMER_AGREEMENT_API.Get_Currency_Code","CURRENCY_CODE");
            cmd.addParameter("AGREEMENT_ID");

            cmd = trans.addCustomFunction("GETCUSTCURRCODE","CUSTOMER_AGREEMENT_API.Get_Currency_Code","CURRENCY_CODE");
            cmd.addParameter("CUSTOMER_NO");

            trans = mgr.validate(trans);

            sAgrCurrencyCode = trans.getValue("GETAGRCURRCODE/DATA/CURRENCY_CODE"); 
            sCustCurrencyCode = trans.getValue("GETCUSTCURRCODE/DATA/CURRENCY_CODE");

            if (mgr.isEmpty(sAgrCurrencyCode))
               sCurrCode = sCustCurrencyCode;
            else
               sCurrCode = sAgrCurrencyCode;

         }

         trans.clear();

         if (checksec("Customer_Order_Pricing_Api.Get_Valid_Price_List",1,isSecure))
         {
            cmd = trans.addCustomFunction("GETVALIDPRICE","Customer_Order_Pricing_Api.Get_Valid_Price_List","LIST_PRICE");
            cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));
            cmd.addParameter("CATALOG_NO",mgr.readValue("ITEM1_CATALOG_NO"));
            cmd.addParameter("CUSTOMER_NO",mgr.readValue("CUSTOMER_NO"));
            cmd.addParameter("CURRENCY_CODE",sCurrCode);

            cmd = trans.addCustomFunction("GETLISTPRICE","Work_Order_Coding_Utility_API.Get_Price","ITEM1_LISTPRICE");
            cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));
            cmd.addParameter("CATALOG_NO",mgr.readValue("ITEM1_CATALOG_NO"));
            cmd.addParameter("CUSTOMER_NO",mgr.readValue("CUSTOMER_NO"));
            cmd.addParameter("AGREEMENT_ID",mgr.readValue("AGREEMENT_ID"));
            cmd.addReference("LIST_PRICE","GETVALIDPRICE/DATA");
            cmd.addParameter("QTY_PLAN",mgr.readValue("QTY_PLAN"));

            trans = mgr.validate(trans);

            double nRevenue = trans.getNumberValue("GETLISTPRICE/DATA/ITEM1_LISTPRICE"); 
            sRevenue = mgr.getASPField("ITEM1_LISTPRICE").formatNumber(nRevenue);
         }

         txt = (mgr.isEmpty(salesPaCostS) ? "" :salesPaCostS) + "^"+(mgr.isEmpty(sRevenue) ? "" :sRevenue) + "^";

         mgr.responseWrite(txt); 
      }
      else if ("STD_JOB_ID".equals(val))
      {
         String validationAttrAtr1 = "";
         String stdid = "";
         String stdcont = "";
         String stdrev = mgr.readValue("STD_JOB_REVISION");
         String descrip = "";
         String status = "";

         trans.clear();

         validationAttrAtr1 = mgr.readValue("STD_JOB_ID");

         if (mgr.readValue("STD_JOB_ID").indexOf("~") > -1)
         {
            String [] attrarr =  validationAttrAtr1.split("~");

            stdid = attrarr[0];
            stdcont = attrarr[1]; 
            stdrev = attrarr[2]; 
         }
         else
         {
            stdid = mgr.readValue("STD_JOB_ID");
            stdcont = mgr.readValue("STD_JOB_CONTRACT");
            //Bug 67801, start
            trans.clear();

            cmd = trans.addCustomFunction("GETREV", "Standard_Job_API.Get_Active_Revision", "STD_JOB_REVISION");
            cmd.addParameter("STD_JOB_ID", stdid);
            cmd.addParameter("STD_JOB_CONTRACT", stdcont);

            trans = mgr.validate(trans);

            stdrev = trans.getValue("GETREV/DATA/STD_JOB_REVISION");
            //Bug 67801, end

         }

         trans.clear();

         cmd = trans.addCustomFunction("DESC","Separate_Standard_Job_API.Get_Definition","ITEM7_DESCRIPTION");
         cmd.addParameter("STD_JOB_ID",stdid);
         cmd.addParameter("STD_JOB_CONTRACT",stdcont);
         cmd.addParameter("STD_JOB_REVISION",stdrev);

         cmd = trans.addCustomFunction("GETWORKDESC", "Standard_Job_API.Get_Work_Description", "DESCRIPTION");
         cmd.addParameter("STD_JOB_ID",stdid);
         cmd.addParameter("STD_JOB_CONTRACT",stdcont);
         cmd.addParameter("STD_JOB_REVISION",stdrev);

         cmd = trans.addCustomFunction("GETSTATUS","Standard_Job_API.Get_Status","STD_JOB_STATUS");
         cmd.addParameter("STD_JOB_ID",stdid);
         cmd.addParameter("STD_JOB_CONTRACT",stdcont);
         cmd.addParameter("STD_JOB_REVISION",stdrev);
         trans = mgr.validate(trans);

         String definition =   trans.getValue("DESC/DATA/DESCRIPTION");  
         String workDesc =   trans.getValue("GETWORKDESC/DATA/DESCRIPTION");  
         status = trans.getValue("GETSTATUS/DATA/STD_JOB_STATUS");
         if (!mgr.isEmpty(workDesc)) {
                descrip = workDesc;
         }
         else
                descrip = definition;
         txt = (mgr.isEmpty(stdid ) ? "" :stdid ) + "^" +
               (mgr.isEmpty(stdcont ) ? "" :stdcont ) + "^" +
               (mgr.isEmpty(stdrev ) ? "" :stdrev ) + "^" +
               (mgr.isEmpty(descrip ) ? "" :descrip ) + "^"+
               (mgr.isEmpty(status ) ? "" :status ) + "^";

         mgr.responseWrite(txt);
      }
      else if ("STD_JOB_REVISION".equals(val))
      {
         String descrip = "";
         trans.clear();

         cmd = trans.addCustomFunction("DESC","Separate_Standard_Job_API.Get_Definition","ITEM7_DESCRIPTION");
         cmd.addParameter("STD_JOB_ID");
         cmd.addParameter("STD_JOB_CONTRACT");
         cmd.addParameter("STD_JOB_REVISION");

         cmd = trans.addCustomFunction("GETWORKDESC", "Standard_Job_API.Get_Work_Description", "DESCRIPTION");
         cmd.addParameter("STD_JOB_ID");
         cmd.addParameter("STD_JOB_CONTRACT");
         cmd.addParameter("STD_JOB_REVISION");

         cmd = trans.addCustomFunction("GETSTATUS","Standard_Job_API.Get_Status","STD_JOB_STATUS");
         cmd.addParameter("STD_JOB_ID");
         cmd.addParameter("STD_JOB_CONTRACT");
         cmd.addParameter("STD_JOB_REVISION");

         trans = mgr.validate(trans);

         String definition =   trans.getValue("DESC/DATA/DESCRIPTION");  
         String workDesc =   trans.getValue("GETWORKDESC/DATA/DESCRIPTION");  
         String status = trans.getValue("GETSTATUS/DATA/STD_JOB_STATUS");

         if (!mgr.isEmpty(workDesc)) {
                descrip = workDesc;
         }
         else
                descrip = definition;
         txt = (mgr.isEmpty(descrip) ? "" :descrip) + "^"+
               (mgr.isEmpty(status) ? "" :status) + "^";

         mgr.responseWrite(txt);
      }
      else if ("ITEM8_WORK_ORDER_INVOICE_TYPE".equals(val))
      {
         double nSalesPriceAmount = CalcSalesPriceAmount(mgr.readValue("ITEM8_WORK_ORDER_INVOICE_TYPE"),
                                                         mgr.readNumberValue("ITEM8_DISCOUNT"),
                                                         mgr.readNumberValue("ITEM8_SALES_PRICE"),
                                                         mgr.readNumberValue("QUANTITY"),
                                                         mgr.readNumberValue("ITEM8_QTY_TO_INVOICE"));

         txt = isNaN(nSalesPriceAmount)?"0":new Double(nSalesPriceAmount).toString() + "^";

         mgr.responseWrite(txt);
      }
      else if ("QUANTITY".equals(val))
      {
         double nSalesPriceAmount = CalcSalesPriceAmount(mgr.readValue("ITEM8_WORK_ORDER_INVOICE_TYPE"),
                                                         mgr.readNumberValue("ITEM8_DISCOUNT"),
                                                         mgr.readNumberValue("ITEM8_SALES_PRICE"),
                                                         mgr.readNumberValue("QUANTITY"),
                                                         mgr.readNumberValue("ITEM8_QTY_TO_INVOICE"));

         txt = isNaN(nSalesPriceAmount)?"0":new Double(nSalesPriceAmount).toString() + "^";

         mgr.responseWrite(txt);
      }
      else if ("COST".equals(val))
      {
         String sCatalogNo = mgr.readValue("ITEM8_CATALOG_NO");

         double costAmtVal = mgr.readNumberValue("COST");
         if (isNaN(costAmtVal))
            costAmtVal = 0;

         double nQtyVal = mgr.readNumberValue("QUANTITY");
         if (isNaN(nQtyVal))
            nQtyVal = 0;

         trans.clear();
         cmd = trans.addCustomFunction("GETWOCOSTTYPE2","Work_Order_Cost_Type_Api.Get_Client_Value(2)","WORK_ORDER_COST_TYPE");
         cmd = trans.addCustomFunction("GETWOCOSTTYPE3","Work_Order_Cost_Type_Api.Get_Client_Value(3)","WORK_ORDER_COST_TYPE");
         trans = mgr.validate(trans);

         String woCostTypeExternal = trans.getValue("GETWOCOSTTYPE2/DATA/WORK_ORDER_COST_TYPE"); 
         String woCostTypeExpense = trans.getValue("GETWOCOSTTYPE3/DATA/WORK_ORDER_COST_TYPE");

         if (mgr.readValue("WORK_ORDER_COST_TYPE").equals(woCostTypeExternal)||mgr.readValue("WORK_ORDER_COST_TYPE").equals(woCostTypeExpense))
         {
            if ((costAmtVal>= 0||sCatalogNo != null)&&(nQtyVal== 0))
            {
               nQtyVal = 1;
            }
         }

         String strQtyPlan = mgr.getASPField("QUANTITY").formatNumber(nQtyVal);

         double nSalesPriceAmount = CalcSalesPriceAmount(mgr.readValue("ITEM8_WORK_ORDER_INVOICE_TYPE"),
                                                         mgr.readNumberValue("ITEM8_DISCOUNT"),
                                                         mgr.readNumberValue("ITEM8_SALES_PRICE"),
                                                         mgr.readNumberValue("QUANTITY"),
                                                         mgr.readNumberValue("ITEM8_QTY_TO_INVOICE"));

         txt = (isNaN(nSalesPriceAmount)?"0":new Double(nSalesPriceAmount).toString()) + "^"+(mgr.isEmpty(strQtyPlan) ? "" : (strQtyPlan))+"^";

         mgr.responseWrite(txt);
      }
      else if ("ITEM8_QTY_TO_INVOICE".equals(val))
      {
         double nSalesPrice = mgr.readNumberValue("ITEM8_SALES_PRICE");
         double nDiscount = mgr.readNumberValue("ITEM8_DISCOUNT");

         if (!mgr.isEmpty(mgr.readValue("ITEM8_CATALOG_NO")) && !isNaN(mgr.readNumberValue("ITEM8_QTY_TO_INVOICE")))
         {
            double[] priceValues = GetSalesPartInfo(new Double(mgr.readNumberValue("ITEM8_PM_NO")).intValue(),
                                                    mgr.readValue("ITEM8_PM_REVISION"),
                                                    mgr.readValue("ITEM8_CATALOG_CONTRACT"),
                                                    mgr.readValue("ITEM8_CATALOG_NO"),
                                                    mgr.readNumberValue("QUANTITY"),
                                                    mgr.readNumberValue("ITEM8_QTY_TO_INVOICE"),
                                                    mgr.readNumberValue("ITEM8_DISCOUNT"));

            nSalesPrice = priceValues[0];
            nDiscount = priceValues[1];
         }

         double nSalesPriceAmount = CalcSalesPriceAmount(mgr.readValue("ITEM8_WORK_ORDER_INVOICE_TYPE"),
                                                         nDiscount,
                                                         nSalesPrice,
                                                         mgr.readNumberValue("QUANTITY"),
                                                         mgr.readNumberValue("ITEM8_QTY_TO_INVOICE"));

         txt = (isNaN(nSalesPrice)?"0":new Double(nSalesPrice).toString()) + "^" + 
               (isNaN(nDiscount)?"0":new Double(nDiscount).toString()) + "^" +
               (isNaN(nSalesPriceAmount)?"0":new Double(nSalesPriceAmount).toString()) + "^";

         mgr.responseWrite(txt);
      }
      else if ("ITEM8_CATALOG_CONTRACT".equals(val))
      {
         double nSalesPrice = mgr.readNumberValue("ITEM8_SALES_PRICE");
         double nDiscount = mgr.readNumberValue("ITEM8_DISCOUNT");

         if (!mgr.isEmpty(mgr.readValue("ITEM8_CATALOG_NO")) && !isNaN(mgr.readNumberValue("ITEM8_QTY_TO_INVOICE")))
         {
            double[] priceValues = GetSalesPartInfo(new Double(mgr.readNumberValue("ITEM8_PM_NO")).intValue(),
                                                    mgr.readValue("ITEM8_PM_REVISION"),
                                                    mgr.readValue("ITEM8_CATALOG_CONTRACT"),
                                                    mgr.readValue("ITEM8_CATALOG_NO"),
                                                    mgr.readNumberValue("QUANTITY"),
                                                    mgr.readNumberValue("ITEM8_QTY_TO_INVOICE"),
                                                    mgr.readNumberValue("ITEM8_DISCOUNT"));

            nSalesPrice = priceValues[0];
            nDiscount = priceValues[1];
         }

         double nSalesPriceAmount = CalcSalesPriceAmount(mgr.readValue("ITEM8_WORK_ORDER_INVOICE_TYPE"),
                                                         nDiscount,
                                                         nSalesPrice,
                                                         mgr.readNumberValue("QUANTITY"),
                                                         mgr.readNumberValue("ITEM8_QTY_TO_INVOICE"));

         txt = (isNaN(nSalesPrice)?"0":new Double(nSalesPrice).toString()) + "^" + 
               (isNaN(nDiscount)?"0":new Double(nDiscount).toString()) + "^" +
               (isNaN(nSalesPriceAmount)?"0":new Double(nSalesPriceAmount).toString()) + "^";

         mgr.responseWrite(txt);
      }
      else if ("ITEM8_CATALOG_NO".equals(val))
      {
         double nCost = GetCost(mgr.readValue("ITEM8_WORK_ORDER_INVOICE_TYPE"),
                                new Double(mgr.readNumberValue("ITEM8_PM_NO")).intValue(),
                                mgr.readValue("ITEM8_PM_REVISION"),
                                new Double(mgr.readNumberValue("PLAN_LINE_NO")).intValue(),
                                mgr.readValue("ITEM8_CATALOG_CONTRACT"),
                                mgr.readValue("ITEM8_CATALOG_NO"));

         double nSalesPrice = mgr.readNumberValue("ITEM8_SALES_PRICE");
         double nDiscount = mgr.readNumberValue("ITEM8_DISCOUNT");
         double nQuantity = mgr.readNumberValue("QUANTITY");

         trans.clear();
         if (isNaN(nQuantity))
            nQuantity = 0;

         cmd = trans.addCustomFunction("GETWOCOSTTYPE2","Work_Order_Cost_Type_Api.Get_Client_Value(2)","WORK_ORDER_COST_TYPE");
         cmd = trans.addCustomFunction("GETWOCOSTTYPE3","Work_Order_Cost_Type_Api.Get_Client_Value(3)","WORK_ORDER_COST_TYPE");
         trans = mgr.validate(trans);

         String woCostTypeExternal = trans.getValue("GETWOCOSTTYPE2/DATA/WORK_ORDER_COST_TYPE"); 
         String woCostTypeExpense = trans.getValue("GETWOCOSTTYPE3/DATA/WORK_ORDER_COST_TYPE");

         if ((!mgr.isEmpty(mgr.readValue("ITEM8_CATALOG_NO")))&&(mgr.readValue("WORK_ORDER_COST_TYPE").equals(woCostTypeExternal)) || (mgr.readValue("WORK_ORDER_COST_TYPE").equals(woCostTypeExpense)))
         {
            if (nQuantity == 0)
            {
               nQuantity = 1;
            }
         }


         if (!mgr.isEmpty(mgr.readValue("ITEM8_CATALOG_NO")) && !isNaN(mgr.readNumberValue("ITEM8_QTY_TO_INVOICE")))
         {
            double[] priceValues = GetSalesPartInfo(new Double(mgr.readNumberValue("ITEM8_PM_NO")).intValue(),
                                                    mgr.readValue("ITEM8_PM_REVISION"),
                                                    mgr.readValue("ITEM8_CATALOG_CONTRACT"),
                                                    mgr.readValue("ITEM8_CATALOG_NO"),
                                                    nQuantity,
                                                    mgr.readNumberValue("ITEM8_QTY_TO_INVOICE"),
                                                    mgr.readNumberValue("ITEM8_DISCOUNT"));

            nSalesPrice = priceValues[0];
            nDiscount = priceValues[1];
         }

         double nSalesPriceAmount = CalcSalesPriceAmount(mgr.readValue("ITEM8_WORK_ORDER_INVOICE_TYPE"),
                                                         nDiscount,
                                                         nSalesPrice,
                                                         nQuantity,
                                                         mgr.readNumberValue("ITEM8_QTY_TO_INVOICE"));

         trans.clear();

         cmd = trans.addCustomFunction("GETCATDESC","Sales_Part_API.Get_Catalog_Desc","ITEM8_SALES_PART_DESC");
         cmd.addParameter("ITEM8_CATALOG_CONTRACT");
         cmd.addParameter("ITEM8_CATALOG_NO");

         trans = mgr.perform(trans); 

         String sCatalogDesc = trans.getValue("GETCATDESC/DATA/ITEM8_SALES_PART_DESC");
         String sWoQtyPlan = mgr.formatNumber("QUANTITY",nQuantity);

         txt = (isNaN(nCost)?"0":new Double(nCost).toString()) + "^" + 
               (isNaN(nSalesPrice)?"0":new Double(nSalesPrice).toString()) + "^" + 
               (isNaN(nDiscount)?"0":new Double(nDiscount).toString()) + "^" +
               (isNaN(nSalesPriceAmount)?"0":new Double(nSalesPriceAmount).toString()) + "^" +
               (mgr.isEmpty(sCatalogDesc)?"":sCatalogDesc) + "^"+(mgr.isEmpty(sWoQtyPlan)?"":sWoQtyPlan) + "^";

         mgr.responseWrite(txt);
      }
      else if ("ITEM8_SALES_PRICE".equals(val))
      {
         double nSalesPriceAmount = CalcSalesPriceAmount(mgr.readValue("ITEM8_WORK_ORDER_INVOICE_TYPE"),
                                                         mgr.readNumberValue("ITEM8_DISCOUNT"),
                                                         mgr.readNumberValue("ITEM8_SALES_PRICE"),
                                                         mgr.readNumberValue("QUANTITY"),
                                                         mgr.readNumberValue("ITEM8_QTY_TO_INVOICE"));

         txt = isNaN(nSalesPriceAmount)?"0":new Double(nSalesPriceAmount).toString() + "^";

         mgr.responseWrite(txt);
      }
      else if ("ITEM8_DISCOUNT".equals(val))
      {
         double nSalesPriceAmount = CalcSalesPriceAmount(mgr.readValue("ITEM8_WORK_ORDER_INVOICE_TYPE"),
                                                         mgr.readNumberValue("ITEM8_DISCOUNT"),
                                                         mgr.readNumberValue("ITEM8_SALES_PRICE"),
                                                         mgr.readNumberValue("QUANTITY"),
                                                         mgr.readNumberValue("ITEM8_QTY_TO_INVOICE"));

         txt = isNaN(nSalesPriceAmount)?"0":new Double(nSalesPriceAmount).toString() + "^";

         mgr.responseWrite(txt);
      }

      else if ("ITEM7_SIGNATURE".equals(val))
      {

         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[2];
         String emp_id = "";
         String sign = "";

         String new_sign = mgr.readValue("ITEM7_SIGNATURE","");

         if (new_sign.indexOf("^",0)>0)
         {
            for (i=0 ; i<2; i++)
            {
               endpos = new_sign.indexOf("^",startpos);
               reqstr = new_sign.substring(startpos,endpos);
               ar[i] = reqstr;
               startpos= endpos+1;
            }
            sign = ar[0];
            emp_id = ar[1];
         }
         else
         {
            cmd = trans.addCustomFunction("MAINTEMP","Company_Emp_API.Get_Max_Employee_ID","EMPLOYEE_ID");                     
            cmd.addParameter("COMPANY");
            cmd.addParameter("ITEM7_SIGNATURE");
            trans = mgr.perform(trans);

            sign = mgr.readValue("ITEM7_SIGNATURE");
            emp_id = trans.getValue("MAINTEMP/DATA/EMPLOYEE_ID");
         }

         txt = (mgr.isEmpty(emp_id)?"":emp_id) + "^"+(mgr.isEmpty(sign)?"":sign) + "^";
         mgr.responseWrite(txt);
      }
      else if ("MAINT_EMP_SIGN".equals(val))
      {
         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[2];
         String emp_id = "";
         String sign = "";

         String new_sign = mgr.readValue("MAINT_EMP_SIGN","");

         if (new_sign.indexOf("^",0)>0)
         {
            for (i=0 ; i<2; i++)
            {
               endpos = new_sign.indexOf("^",startpos);
               reqstr = new_sign.substring(startpos,endpos);
               ar[i] = reqstr;
               startpos= endpos+1;
            }
            sign = ar[0];
            emp_id = ar[1];
         }
         else
         {
            cmd = trans.addCustomFunction("MAINTEMP","Company_Emp_API.Get_Max_Employee_ID","MAINT_EMPLOYEE");                     
            cmd.addParameter("COMPANY");
            cmd.addParameter("MAINT_EMP_SIGN");
            trans = mgr.perform(trans);

            sign = mgr.readValue("MAINT_EMP_SIGN");
            emp_id = trans.getValue("MAINTEMP/DATA/MAINT_EMPLOYEE");
         }

         txt = (mgr.isEmpty(emp_id)?"":emp_id) + "^"+(mgr.isEmpty(sign)?"":sign) + "^";
         mgr.responseWrite(txt);
      }
      else if ("ITEM0_SIGNATURE".equals(val))
      {
         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[2];
         String emp_id = "";
         String sign = "";
         String catNo = mgr.readValue("ITEM0_CATALOG_NO");;
         String catDesc = mgr.readValue("SALESPARTDESCRIPTION");;
         String listPrice = mgr.readValue("LISTPRICE");;
         String sOrgContract = mgr.readValue("ITEM0_ORG_CONTRACT","");
         String sOrgCode = mgr.readValue("ITEM0_ORG_CODE","");
         String roleCode = mgr.readValue("ROLE_CODE");
         String sCatCont = mgr.readValue("ITEM0_CONTRACT");

         String new_sign = mgr.readValue("ITEM0_SIGNATURE","");
         String sCatCost;

         if (new_sign.indexOf("^",0)>0)
         {
            for (i=0 ; i<2; i++)
            {
               endpos = new_sign.indexOf("^",startpos);
               reqstr = new_sign.substring(startpos,endpos);
               ar[i] = reqstr;
               startpos= endpos+1;
            }
            sign = ar[0];
            emp_id = ar[1];
         }
         else
         {
            cmd = trans.addCustomFunction("MAINTEMP","Company_Emp_API.Get_Max_Employee_ID","ITEM0_EMPLOYEE_ID");                     
            cmd.addParameter("ITEM0_COMPANY");
            cmd.addParameter("ITEM0_SIGNATURE");
            trans = mgr.perform(trans);

            sign = mgr.readValue("ITEM0_SIGNATURE");
            emp_id = trans.getValue("MAINTEMP/DATA/EMPLOYEE_ID");
         }

         trans.clear();

         if (mgr.isEmpty(roleCode))
         {
            cmd = trans.addCustomFunction("DEFROLE","Employee_Role_API.Get_Default_Role","ROLE_CODE");
            cmd.addParameter("ITEM0_COMPANY");
            cmd.addParameter("ITEM0_EMPLOYEE_ID",emp_id); 

            cmd = trans.addCustomFunction("SAPANO", "Role_Sales_Part_API.Get_Def_Catalog_No", "ITEM0_CATALOG_NO" );
            cmd.addReference("ROLE_CODE","DEFROLE/DATA");
            cmd.addParameter("ITEM0_CONTRACT",sCatCont);

            cmd = trans.addCustomFunction("SADES", "Sales_Part_API.Get_Catalog_Desc", "SALESPARTDESCRIPTION" );
            cmd.addParameter("ITEM0_CONTRACT",sCatCont);
            cmd.addReference("ITEM0_CATALOG_NO","SAPANO/DATA");

            cmd = trans.addCustomFunction("ROCO", "SALES_PART_API.Get_List_Price", "LISTPRICE" );
            cmd.addParameter("ITEM0_CONTRACT",sCatCont);
            cmd.addReference("ITEM0_CATALOG_NO","SAPANO/DATA");
         }

         if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
         {
            cmd = trans.addCustomFunction("EMPORGCONT","Employee_API.Get_Org_Contract","ORG_CONTRACT");
            cmd.addParameter("ITEM0_COMPANY");
            cmd.addParameter("ITEM0_EMPLOYEE_ID",emp_id);
         }

         if (mgr.isEmpty(sOrgCode))
         {
            cmd = trans.addCustomFunction("EMPORGCOD","Employee_API.Get_Organization","ITEM0_ORG_CODE");
            cmd.addParameter("ITEM0_COMPANY");
            cmd.addParameter("ITEM0_EMPLOYEE_ID",emp_id);
         }

         cmd = trans.addCustomFunction("CATCOST","Work_Order_Planning_Util_API.Get_Cost","CATALOG_COST");
         if (mgr.isEmpty(sOrgCode))
            cmd.addReference("ITEM0_ORG_CODE","EMPORGCOD/DATA");
         else
            cmd.addParameter("ITEM0_ORG_CODE",sOrgCode);
         if (mgr.isEmpty(roleCode))
            cmd.addReference("ROLE_CODE","DEFROLE/DATA");
         else
            cmd.addParameter("ROLE_CODE",mgr.readValue("ROLE_CODE"));
         cmd.addParameter("ITEM0_CONTRACT",sCatCont);
         if (mgr.isEmpty(roleCode))
            cmd.addReference("ITEM0_CATALOG_NO","SAPANO/DATA");
         else
            cmd.addParameter("ITEM0_CATALOG_NO",catNo);
         if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
            cmd.addReference("ORG_CONTRACT","EMPORGCONT/DATA");
         else
            cmd.addParameter("ITEM0_ORG_CONTRACT",sOrgContract);

         trans = mgr.validate(trans);

         if (mgr.isEmpty(roleCode))
         {
            roleCode = trans.getValue("DEFROLE/DATA/ROLE_CODE");
            catNo = trans.getValue("SAPANO/DATA/CATALOG_NO");
            catDesc = trans.getValue("SADES/DATA/SALESPARTDESCRIPTION");
            listPrice = trans.getValue("ROCO/DATA/LISTPRICE");
         }

         if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
            sOrgContract = trans.getValue("EMPORGCONT/DATA/ORG_CONTRACT");

         if (mgr.isEmpty(sOrgCode))
            sOrgCode = trans.getValue("EMPORGCOD/DATA/ORG_CODE");

         sCatCost = trans.getValue("CATCOST/DATA/CATALOG_COST");

         txt = (mgr.isEmpty(emp_id)?"":emp_id) + "^"+(mgr.isEmpty(roleCode)?"":roleCode) + "^"+
               (mgr.isEmpty(sOrgCode)?"":sOrgCode) + "^"+(mgr.isEmpty(sign)?"":sign) + "^"+(mgr.isEmpty(sOrgContract)?"":sOrgContract) + "^"+
               (mgr.isEmpty(catNo)?"":catNo) + "^"+(mgr.isEmpty(catDesc)?"":catDesc) + "^"+(mgr.isEmpty(listPrice)?"":listPrice) + "^"+
               (mgr.isEmpty(sCatCost)?"":sCatCost) + "^";
         mgr.responseWrite(txt);
      }
      else if ("ITEM0_JOB_ID".equals(val))
      {
         cmd = trans.addCustomFunction("GETEMP","Pm_Action_Job_API.Get_Executed_By","ITEM0_EMPLOYEE_ID");
         cmd.addParameter("ITEM0_PM_NO");
         cmd.addParameter("ITEM0_PM_REVISION");
         cmd.addParameter("ITEM0_JOB_ID");

         cmd = trans.addCustomFunction("GETPERSON","Company_Emp_API.Get_Person_Id","ITEM0_SIGNATURE");
         cmd.addParameter("ITEM0_COMPANY");
         cmd.addReference("ITEM0_EMPLOYEE_ID","GETEMP/DATA");

         cmd = trans.addCustomFunction("DEFROLE","Employee_Role_API.Get_Default_Role","ROLE_CODE");
         cmd.addParameter("ITEM0_COMPANY");
         cmd.addReference("ITEM0_EMPLOYEE_ID","GETEMP/DATA");  

         trans = mgr.validate(trans);

         String emp_id = trans.getValue("GETEMP/DATA/EMPLOYEE_ID");
         String sign = trans.getValue("GETPERSON/DATA/SIGNATURE");
         String role = trans.getValue("DEFROLE/DATA/ROLE_CODE");

         txt = (mgr.isEmpty(emp_id)?"":emp_id) + "^"+(mgr.isEmpty(sign)?"":sign) + "^"+
               (mgr.isEmpty(role)?"":role) + "^";
         mgr.responseWrite(txt);
      }
      else if ("TEAM_ID".equals(val))
      {
         cmd = trans.addCustomFunction("TDESC", "Maint_Team_API.Get_Description", "TEAMDESC" );    
         cmd.addParameter("TEAM_ID");
         cmd.addParameter("TEAM_CONTRACT");
         trans = mgr.validate(trans);   
         teamDesc  = trans.getValue("TDESC/DATA/TEAMDESC");

         txt =  (mgr.isEmpty(teamDesc) ? "" : (teamDesc)) + "^";
         mgr.responseWrite(txt);
      }

      //---------validations for operations in Jobs tab----------------

      else if ("ITEM9_ORG_CODE".equals(val))
      {
         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[6];

         String decost = "";
         String decat = "";
         String decatdes = "";

         String sOrgCode = "";
         String sOrgContract = "";
         String new_org_code = mgr.readValue("ITEM9_ORG_CODE","");

         if (new_org_code.indexOf("^",0)>0)
         {
            if (!mgr.isEmpty(mgr.readValue("ITEM9_ROLE_CODE")))
            {
               for (i=0 ; i<5; i++)
               {
                  endpos = new_org_code.indexOf("^",startpos);
                  reqstr = new_org_code.substring(startpos,endpos);
                  ar[i] = reqstr;
                  startpos= endpos+1;
               }
               sOrgCode = ar[3];
               sOrgContract = ar[4];
            }
            else
            {
               if (!mgr.isEmpty(mgr.readValue("ITEM9_SIGNATURE")))
               {
                  for (i=0 ; i<4; i++)
                  {
                     endpos = new_org_code.indexOf("^",startpos);
                     reqstr = new_org_code.substring(startpos,endpos);
                     ar[i] = reqstr;
                     startpos= endpos+1;
                  }
                  sOrgCode = ar[2];
                  sOrgContract = ar[3];
               }
               else
               {
                  for (i=0 ; i<2; i++)
                  {
                     endpos = new_org_code.indexOf("^",startpos);
                     reqstr = new_org_code.substring(startpos,endpos);
                     ar[i] = reqstr;
                     startpos= endpos+1;
                  }
                  sOrgCode = ar[0];
                  sOrgContract = ar[1];
               }
            }
         }
         else
         {
            sOrgCode = mgr.readValue("ITEM9_ORG_CODE");
            sOrgContract = mgr.readValue("ITEM9_ORG_CONTRACT"); 
         }


         if (mgr.readValue("ITEM9_CONTRACT").equals(sOrgContract))
         {
            if (checksec("Organization_Sales_Part_API.Get_Def_Catalog_No",1,isSecure))
            {
               cmd = trans.addCustomFunction("DEPTPA", "Organization_Sales_Part_API.Get_Def_Catalog_No", "ITEM9_CATALOG_NO" );
               cmd.addParameter("ITEM9_ORG_CONTRACT",sOrgContract);
               cmd.addParameter("ITEM9_ORG_CODE",sOrgCode);
            }
            if (checksec("Sales_Part_API.Get_Catalog_Desc",2,isSecure))
            {
               cmd = trans.addCustomFunction("DEPTCADES", "Sales_Part_API.Get_Catalog_Desc", "ITEM9_SALESPARTDESCRIPTION" );
               cmd.addParameter("ITEM9_CONTRACT",mgr.readValue("ITEM9_CONTRACT"));
               cmd.addReference("ITEM9_CATALOG_NO","DEPTPA/DATA");
            }
            if (checksec("SALES_PART_API.Get_List_Price",3,isSecure))
            {
               cmd = trans.addCustomFunction("DEPTCO", "SALES_PART_API.Get_List_Price", "ITEM9_LISTPRICE" );
               cmd.addParameter("ITEM9_CONTRACT",mgr.readValue("ITEM9_CONTRACT"));
               cmd.addReference("ITEM9_CATALOG_NO","DEPTPA/DATA");
            }

            trans = mgr.validate(trans);

            ref = 0;

            if (isSecure[3] =="true")
            {
               decost = trans.getValue("DEPTCO/DATA/ITEM9_LISTPRICE");
            }
            else
               decost =  "";

            if (isSecure[1] =="true")
            {
               decat = trans.getValue("DEPTPA/DATA/CATALOG_NO");      
            }
            else
               decat   = null;

            if (isSecure[2] =="true")
            {
               decatdes = trans.getValue("DEPTCADES/DATA/ITEM9_SALESPARTDESCRIPTION");      
            }
            else
               decatdes    = null;
         }

         txt=(mgr.isEmpty(decost)? "":decost)+ "^" + (mgr.isEmpty(decat)? "":decat)+ "^" + (mgr.isEmpty(decatdes)? "":decatdes)+ "^"+ (mgr.isEmpty(sOrgCode)? "":sOrgCode)+ "^"+ (mgr.isEmpty(sOrgContract)? "":sOrgContract)+ "^" ; 

         mgr.responseWrite(txt);      
      }
      else if ("ITEM9_ROLE_CODE".equals(val))
      {
         String rocost;
         String rocode;
         String rocont = "";
         String saldesc;

         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[6];

         String sRoleCode = "";
         String sOrgContract = "";
         String sCatCont = mgr.readValue("ITEM9_CONTRACT","");
         String new_role_code = mgr.readValue("ITEM9_ROLE_CODE","");
         if (new_role_code.indexOf("^",0)>0)
         {
            if (!mgr.isEmpty(mgr.readValue("ITEM9_SIGNATURE")))
            {
               for (i=0 ; i<5; i++)
               {
                  endpos = new_role_code.indexOf("^",startpos);
                  reqstr = new_role_code.substring(startpos,endpos);
                  ar[i] = reqstr;
                  startpos= endpos+1;
               }
               sRoleCode = ar[2];
               sOrgContract = ar[4];
            }
            else
            {
               for (i=0 ; i<2; i++)
               {
                  endpos = new_role_code.indexOf("^",startpos);
                  reqstr = new_role_code.substring(startpos,endpos);
                  ar[i] = reqstr;
                  startpos= endpos+1;
               }
               sRoleCode = ar[0];
               sOrgContract = ar[1];

            }
         }
         else
         {
            sRoleCode = mgr.readValue("ITEM9_ROLE_CODE");
            sOrgContract = mgr.readValue("ITEM9_ORG_CONTRACT"); 

         }

         if (checksec("Role_Sales_Part_API.Get_Def_Catalog_No",1,isSecure))
         {
            cmd = trans.addCustomFunction("SAPANO", "Role_Sales_Part_API.Get_Def_Catalog_No", "ITEM9_CATALOG_NO" );
            cmd.addParameter("ITEM9_ROLE_CODE",sRoleCode);
            cmd.addParameter("ITEM9_CONTRACT",sCatCont);
         }
         if (checksec("Sales_Part_API.Get_Catalog_Desc",2,isSecure))
         {
            cmd = trans.addCustomFunction("SADES", "Sales_Part_API.Get_Catalog_Desc", "ITEM9_SALESPARTDESCRIPTION" );
            cmd.addParameter("ITEM9_CONTRACT",sCatCont);
            cmd.addReference("ITEM9_CATALOG_NO","SAPANO/DATA");
         }
         if (checksec("SALES_PART_API.Get_List_Price",3,isSecure))
         {
            cmd = trans.addCustomFunction("ROCO", "SALES_PART_API.Get_List_Price", "ITEM9_LISTPRICE" );
            cmd.addParameter("ITEM9_CONTRACT",sCatCont);
            cmd.addReference("ITEM9_CATALOG_NO","SAPANO/DATA");
         }

         trans = mgr.validate(trans);

         ref = 0;

         if (isSecure[3] =="true")
         {
            rocost = trans.getValue("ROCO/DATA/ITEM9_LISTPRICE");
         }
         else
            rocost =  "";

         if (isSecure[1] =="true")
         {
            rocode = trans.getValue("SAPANO/DATA/CATALOG_NO");      
         }
         else
            rocode = null;

         if (isSecure[2] =="true")
         {
            saldesc = trans.getValue("SADES/DATA/ITEM9_SALESPARTDESCRIPTION");      
         }
         else
            saldesc = null;

         trans.clear();
         if (mgr.isEmpty(rocode)|| mgr.isEmpty(sRoleCode))
         {
            cmd = trans.addCustomFunction("SAPANO", "Organization_Sales_Part_API.Get_Def_Catalog_No ", "ITEM9_CATALOG_NO");
            cmd.addParameter("ITEM9_CONTRACT");  
            cmd.addParameter("ITEM9_ORG_CODE",mgr.readValue("ITEM9_ORG_CODE"));

            cmd = trans.addCustomFunction("SADES", "Sales_Part_API.Get_Catalog_Desc", "ITEM9_SALESPARTDESCRIPTION");
            cmd.addParameter("ITEM9_CONTRACT");
            cmd.addReference("ITEM9_CATALOG_NO", "SAPANO/DATA");  

            cmd = trans.addCustomFunction("ROCO", "SALES_PART_API.Get_List_Price", "ITEM9_LISTPRICE" );
            cmd.addParameter("ITEM9_CONTRACT",sCatCont);
            cmd.addReference("ITEM9_CATALOG_NO","SAPANO/DATA");

            trans = mgr.validate(trans);

            rocode = trans.getValue("SAPANO/DATA/CATALOG_NO");  
            saldesc = trans.getValue("SADES/DATA/ITEM9_SALESPARTDESCRIPTION");  
            rocost = trans.getValue("ROCO/DATA/ITEM9_LISTPRICE");
         }

         txt=(mgr.isEmpty(rocost)? "":rocost)+ "^" + (mgr.isEmpty(rocode)? "":rocode)+ "^" + (mgr.isEmpty(sOrgContract)? "":sOrgContract)+ "^" + (mgr.isEmpty(saldesc)? "":saldesc)+ "^"+ (mgr.isEmpty(sRoleCode)? "":sRoleCode)+ "^"; 

         mgr.responseWrite(txt);      
      }
      else if ("ITEM9_CATALOG_NO".equals(val))
      {
         if (checksec("SALES_PART_API.Get_Catalog_Desc",1,isSecure))
         {
            cmd = trans.addCustomFunction("CATDESC", "SALES_PART_API.Get_Catalog_Desc", "ITEM9_SALESPARTDESCRIPTION" );
            cmd.addParameter("ITEM9_CONTRACT");
            cmd.addParameter("ITEM9_CATALOG_NO");
         }
         if (checksec("SALES_PART_API.Get_List_Price",2,isSecure))
         {
            cmd = trans.addCustomFunction("LISPRI", "SALES_PART_API.Get_List_Price", "ITEM9_LISTPRICE" );
            cmd.addParameter("ITEM9_CONTRACT");
            cmd.addParameter("ITEM9_CATALOG_NO"); 
         }

         trans = mgr.validate(trans);

         ref = 0;

         if (isSecure[1] =="true")
         {
            strCatDesc = trans.getValue("CATDESC/DATA/ITEM9_SALESPARTDESCRIPTION");
         }
         else
            strCatDesc =  "";

         if (isSecure[2] =="true")
         {
            numListPrice = trans.getValue("LISPRI/DATA/ITEM9_LISTPRICE");      
         }
         else
            numListPrice = null;

         txt=(mgr.isEmpty(strCatDesc)? "":strCatDesc)+ "^" + (mgr.isEmpty(numListPrice)? "":numListPrice)+ "^";

         mgr.responseWrite(txt);      
      }
      else if ("ITEM9_SIGNATURE".equals(val))
      {
         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[2];
         String emp_id = "";
         String sign = "";
         String catNo = mgr.readValue("ITEM9_CATALOG_NO");;
         String catDesc = mgr.readValue("ITEM9_SALESPARTDESCRIPTION");;
         String listPrice = mgr.readValue("ITEM9_LISTPRICE");;
         String sOrgContract = mgr.readValue("ITEM9_ORG_CONTRACT","");
         String sOrgCode = mgr.readValue("ITEM9_ORG_CODE","");
         String roleCode = mgr.readValue("ITEM9_ROLE_CODE");
         String sCatCont = mgr.readValue("ITEM9_CONTRACT");

         String new_sign = mgr.readValue("ITEM9_SIGNATURE","");

         if (new_sign.indexOf("^",0)>0)
         {
            for (i=0 ; i<2; i++)
            {
               endpos = new_sign.indexOf("^",startpos);
               reqstr = new_sign.substring(startpos,endpos);
               ar[i] = reqstr;
               startpos= endpos+1;
            }
            sign = ar[0];
            emp_id = ar[1];
         }
         else
         {
            cmd = trans.addCustomFunction("MAINTEMP","Company_Emp_API.Get_Max_Employee_ID","ITEM9_EMPLOYEE_ID");                     
            cmd.addParameter("ITEM9_COMPANY");
            cmd.addParameter("ITEM9_SIGNATURE");
            trans = mgr.perform(trans);

            sign = mgr.readValue("ITEM9_SIGNATURE");
            emp_id = trans.getValue("MAINTEMP/DATA/EMPLOYEE_ID");
         }

         trans.clear();

         if (mgr.isEmpty(roleCode))
         {
            cmd = trans.addCustomFunction("DEFROLE","Employee_Role_API.Get_Default_Role","ITEM9_ROLE_CODE");
            cmd.addParameter("ITEM9_COMPANY");
            cmd.addParameter("ITEM9_EMPLOYEE_ID",emp_id); 

            cmd = trans.addCustomFunction("SAPANO", "Role_Sales_Part_API.Get_Def_Catalog_No", "ITEM9_CATALOG_NO" );
            cmd.addReference("ITEM9_ROLE_CODE","DEFROLE/DATA");
            cmd.addParameter("ITEM9_CONTRACT",sCatCont);

            cmd = trans.addCustomFunction("SADES", "Sales_Part_API.Get_Catalog_Desc", "ITEM9_SALESPARTDESCRIPTION" );
            cmd.addParameter("ITEM9_CONTRACT",sCatCont);
            cmd.addReference("ITEM9_CATALOG_NO","SAPANO/DATA");

            cmd = trans.addCustomFunction("ROCO", "SALES_PART_API.Get_List_Price", "ITEM9_LISTPRICE" );
            cmd.addParameter("ITEM9_CONTRACT",sCatCont);
            cmd.addReference("ITEM9_CATALOG_NO","SAPANO/DATA");
         }

         if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
         {
            cmd = trans.addCustomFunction("EMPORGCONT","Employee_API.Get_Org_Contract","ITEM9_ORG_CONTRACT");
            cmd.addParameter("ITEM9_COMPANY");
            cmd.addParameter("ITEM9_EMPLOYEE_ID",emp_id);
         }

         if (mgr.isEmpty(sOrgCode))
         {
            cmd = trans.addCustomFunction("EMPORGCOD","Employee_API.Get_Organization","ITEM9_ORG_CODE");
            cmd.addParameter("ITEM9_COMPANY");
            cmd.addParameter("ITEM9_EMPLOYEE_ID",emp_id);
         }

         trans = mgr.validate(trans);

         if (mgr.isEmpty(roleCode))
         {
            roleCode = trans.getValue("DEFROLE/DATA/ROLE_CODE");
            catNo = trans.getValue("SAPANO/DATA/CATALOG_NO");
            catDesc = trans.getValue("SADES/DATA/ITEM9_SALESPARTDESCRIPTION");
            listPrice = trans.getValue("ROCO/DATA/ITEM9_LISTPRICE");
         }

         if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
            sOrgContract = trans.getValue("EMPORGCONT/DATA/ORG_CONTRACT");

         if (mgr.isEmpty(sOrgCode))
            sOrgCode = trans.getValue("EMPORGCOD/DATA/ORG_CODE");

         txt = (mgr.isEmpty(emp_id)?"":emp_id) + "^"+(mgr.isEmpty(roleCode)?"":roleCode) + "^"+
               (mgr.isEmpty(sOrgCode)?"":sOrgCode) + "^"+(mgr.isEmpty(sign)?"":sign) + "^"+(mgr.isEmpty(sOrgContract)?"":sOrgContract) + "^"+
               (mgr.isEmpty(catNo)?"":catNo) + "^"+(mgr.isEmpty(catDesc)?"":catDesc) + "^"+(mgr.isEmpty(listPrice)?"":listPrice) + "^";
         mgr.responseWrite(txt);
      }

      else if ("ITEM9_TEAM_ID".equals(val))
      {
         cmd = trans.addCustomFunction("TDESC", "Maint_Team_API.Get_Description", "ITEM9_TEAMDESC" );    
         cmd.addParameter("ITEM9_TEAM_ID");
         cmd.addParameter("ITEM9_TEAM_CONTRACT");
         trans = mgr.validate(trans);   
         teamDesc  = trans.getValue("TDESC/DATA/ITEM9_TEAMDESC");

         txt =  (mgr.isEmpty(teamDesc) ? "" : (teamDesc)) + "^";
         mgr.responseWrite(txt);
      }
      else if ("ORG_CONTRACT".equals(val))
      {

         String sCalendar;  
         String sCalDesc;
         String sComp;

         cmd = trans.addCustomFunction("CALENDER", "Organization_API.Get_Calendar_Id", "CALENDAR_ID" );
         cmd.addParameter("ORG_CONTRACT");
         cmd.addParameter("ORG_CODE");

         cmd = trans.addCustomFunction("CALDESC", "Work_Time_Calendar_API.Get_Description", "CALENDAR_DESC" );
         cmd.addReference("CALENDAR_ID","CALENDER/DATA");

         cmd = trans.addCustomFunction("GETCOMP", "Site_API.Get_Company", "ORG_CODE_COMP" );
         cmd.addParameter("ORG_CONTRACT");

         trans=mgr.validate(trans);
         sCalendar =trans.getValue("CALENDER/DATA/CALENDAR_ID");
         sComp = trans.getValue("GETCOMP/DATA/ORG_CODE_COMP");
         sCalDesc = trans.getValue("CALDESC/DATA/CALENDAR_DESC");

         txt = (mgr.isEmpty (sCalendar) ? "" : (sCalendar)) + "^" + 
               (mgr.isEmpty (sCalDesc) ? "" : sCalDesc) +"^"+
               (mgr.isEmpty (sComp) ? "" : sComp) +"^";  
         mgr.responseWrite(txt);
      }

      else if ("ORG_CODE".equals(val))
      {

         String sCalendar;
         String sCalDesc;

         cmd = trans.addCustomFunction("CALENDER", "Organization_API.Get_Calendar_Id", "CALENDAR_ID" );
         cmd.addParameter("ORG_CONTRACT");
         cmd.addParameter("ORG_CODE");

         cmd = trans.addCustomFunction("CALDESC", "Work_Time_Calendar_API.Get_Description", "CALENDAR_DESC" );
         cmd.addReference("CALENDAR_ID","CALENDER/DATA");

         trans=mgr.validate(trans);
         sCalendar =trans.getValue("CALENDER/DATA/CALENDAR_ID");


         sCalDesc = trans.getValue("CALDESC/DATA/CALENDAR_DESC");

         txt = (mgr.isEmpty (sCalendar) ? "" : sCalendar) + "^" + 
               (mgr.isEmpty (sCalDesc) ? "" : sCalDesc) +"^";  
         mgr.responseWrite(txt);
      }

      else if ("CONTRACT_ID".equals(val))
      {
         String sContractId   = mgr.readValue("CONTRACT_ID");
         String sLineNo       = mgr.readValue("LINE_NO");
         String sContractName = "";
         String sLineDesc     = "";
         String sContractType = "";
         String sInvoiceType  = "";
         String sSrvconCust   = "";
         String sSrvconCustName = "";

         if (sContractId.indexOf("^") > -1)
         {
            String strAttr = sContractId;
            sContractId = strAttr.substring(0, strAttr.indexOf("^"));       
            sLineNo = strAttr.substring(strAttr.indexOf("^") + 1, strAttr.length());
         }

         if (checksec("SC_SERVICE_CONTRACT_API.Get_Contract_Name",1,isSecure))
         {
            cmd = trans.addCustomFunction("GETCONTRACTNAME", "SC_SERVICE_CONTRACT_API.Get_Contract_Name", "CONTRACT_NAME");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }

         if (checksec("PSC_CONTR_PRODUCT_API.Get_Description",1,isSecure))
         {
            cmd = trans.addCustomFunction("GETLINEDESC", "PSC_CONTR_PRODUCT_API.Get_Description", "LINE_DESC");
            cmd.addParameter("CONTRACT_ID", sContractId);
            cmd.addParameter("LINE_NO", sLineNo);
         }

         if (checksec("SC_SERVICE_CONTRACT_API.Get_Contract_Type",1,isSecure))
         {
            cmd = trans.addCustomFunction("GETCONTRACTYPE", "SC_SERVICE_CONTRACT_API.Get_Contract_Type", "CONTRACT_TYPE");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }

         if (checksec("PSC_CONTR_PRODUCT_API.Get_Invoice_Type",1,isSecure))
         {
            cmd = trans.addCustomFunction("GETINVOICETYPE", "PSC_CONTR_PRODUCT_API.Get_Invoice_Type", "INVOICE_TYPE");
            cmd.addParameter("CONTRACT_ID", sContractId);
            cmd.addParameter("LINE_NO", sLineNo );
         }

         if (checksec("SC_SERVICE_CONTRACT_API.Get_Customer_Id",1,isSecure))
         {
            cmd = trans.addCustomFunction("GETSRVCONCUST", "SC_SERVICE_CONTRACT_API.Get_Customer_Id", "CUSTOMER_NO");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }

         if (checksec("CUSTOMER_INFO_API.Get_Name",1,isSecure))
         {
            cmd = trans.addCustomFunction("GETCUSTNAME","CUSTOMER_INFO_API.Get_Name","CUSTOMERNAME");
            cmd.addReference("CUSTOMER_NO","GETSRVCONCUST/DATA");
         }

         trans = mgr.validate(trans);

         sContractName  = trans.getValue("GETCONTRACTNAME/DATA/CONTRACT_NAME");
         sLineDesc      = trans.getValue("GETLINEDESC/DATA/LINE_DESC");
         sContractType  = trans.getValue("GETCONTRACTYPE/DATA/CONTRACT_TYPE");
         sInvoiceType   = trans.getValue("GETINVOICETYPE/DATA/INVOICE_TYPE");
         sSrvconCust    = trans.getValue("GETSRVCONCUST/DATA/CUSTOMER_NO");
         sSrvconCustName= trans.getValue("GETCUSTNAME/DATA/CUSTOMERNAME");

         txt =  (mgr.isEmpty(sContractId) ? "" : (sContractId)) + "^" +(mgr.isEmpty(sContractName)?"":sContractName) + "^" +
                (mgr.isEmpty(sLineNo) ? "" : (sLineNo)) + "^" +(mgr.isEmpty(sLineDesc)?"":sLineDesc) + "^" +
                (mgr.isEmpty(sContractType) ? "" : (sContractType)) + "^" +(mgr.isEmpty(sInvoiceType)?"":sInvoiceType) + "^" +
                (mgr.isEmpty(sSrvconCust)?"":sSrvconCust) + "^" +(mgr.isEmpty(sSrvconCustName)?"":sSrvconCustName) + "^";
         mgr.responseWrite(txt);
      }
      else if ("LINE_NO".equals(val))
      {
         String sContractId   = mgr.readValue("CONTRACT_ID");
         String sLineNo       = mgr.readValue("LINE_NO");
         String sContractName = "";
         String sLineDesc     = "";
         String sContractType = "";
         String sInvoiceType  = "";

         if (sLineNo.indexOf("^") > -1)
         {
            String strAttr = sLineNo;
            sContractId = strAttr.substring(0, strAttr.indexOf("^"));       
            sLineNo =  strAttr.substring(strAttr.indexOf("^") + 1, strAttr.length());                
         }

         if (checksec("SC_SERVICE_CONTRACT_API.Get_Contract_Name",1,isSecure))
         {
            cmd = trans.addCustomFunction("GETCONTRACTNAME", "SC_SERVICE_CONTRACT_API.Get_Contract_Name", "CONTRACT_NAME");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }

         if (checksec("PSC_CONTR_PRODUCT_API.Get_Description",1,isSecure))
         {
            cmd = trans.addCustomFunction("GETLINEDESC", "PSC_CONTR_PRODUCT_API.Get_Description", "LINE_DESC");
            cmd.addParameter("CONTRACT_ID", sContractId);
            cmd.addParameter("LINE_NO", sLineNo);
         }

         if (checksec("SC_SERVICE_CONTRACT_API.Get_Contract_Type",1,isSecure))
         {
            cmd = trans.addCustomFunction("GETCONTRACTYPE", "SC_SERVICE_CONTRACT_API.Get_Contract_Type", "CONTRACT_TYPE");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }

         if (checksec("PSC_CONTR_PRODUCT_API.Get_Invoice_Type",1,isSecure))
         {
            cmd = trans.addCustomFunction("GETINVOICETYPE", "PSC_CONTR_PRODUCT_API.Get_Invoice_Type", "INVOICE_TYPE");
            cmd.addParameter("CONTRACT_ID", sContractId);
            cmd.addParameter("LINE_NO", sLineNo );
         }

         trans = mgr.validate(trans);

         sContractName  = trans.getValue("GETCONTRACTNAME/DATA/CONTRACT_NAME");
         sLineDesc      = trans.getValue("GETLINEDESC/DATA/LINE_DESC");
         sContractType  = trans.getValue("GETCONTRACTYPE/DATA/CONTRACT_TYPE");
         sInvoiceType   = trans.getValue("GETINVOICETYPE/DATA/INVOICE_TYPE");

         txt =  (mgr.isEmpty(sContractId) ? "" : (sContractId)) + "^" +(mgr.isEmpty(sContractName)?"":sContractName) + "^" +
                (mgr.isEmpty(sLineNo) ? "" : (sLineNo)) + "^" +(mgr.isEmpty(sLineDesc)?"":sLineDesc) + "^" +
                (mgr.isEmpty(sContractType) ? "" : (sContractType)) + "^" +(mgr.isEmpty(sInvoiceType)?"":sInvoiceType) + "^" ;

         mgr.responseWrite(txt);
      }


      mgr.endResponse();
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void okFind()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(headblk);
      q.addWhereCondition("PM_TYPE_DB = Pm_Type_API.Get_Db_Value(0)");
      q.addWhereCondition("ORG_CONTRACT IN (Select User_Allowed_Site_API.Authorized(ORG_CONTRACT) from DUAL)");
      q.setOrderByClause("PM_NO,PM_REVISION");
      q.includeMeta("ALL");   

      if (mgr.dataTransfered())
      {
         ASPBuffer retBuffer =  mgr.getTransferedData();
         if (retBuffer.itemExists("PM_NO") /*&& retBuffer.itemExists("CONTRACT")*/)
         {
            String ret_pm_no = retBuffer.getValue("PM_NO");
            String ret_contract = retBuffer.getValue("CONTRACT"); 
            //Bug 58216 Start
            q.addWhereCondition("PM_NO = ?");
            q.addParameter("PM_NO",ret_pm_no);
            q.addWhereCondition("CONTRACT = ?");
            q.addParameter("HEAD_CONTRACT",ret_contract);
            //Bug 58216 End
         }
         else
            q.addOrCondition(mgr.getTransferedData());
      }

      mgr.querySubmit(trans,headblk);

      eval(headset.syncItemSets());

      if (headset.countRows() == 1)
      {
         headSearch =true;
         okFindITEM0();
         okFindITEM1();
         okFindITEM2();
         okFindITEM3();
         okFindITEM4();
         okFindITEM5();
         okFindITEM6();
         okFindITEM7();

         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         headSearch =true;
      }

      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("PCMWPMACTIONNODATAFOUND: No data found."));
         headset.clear();
         headlay.setLayoutMode(headlay.MULTIROW_LAYOUT);    
      }
//    Bug 88982, Start
      else{
          int n = headset.countRows();

          	headset.first();

              for (int i=0; i<=n; ++i)
              {
                  //Bug 92157, start
                  if (!(mgr.isEmpty(headset.getRow().getValue("PM_START_UNIT")))){
                     String startUnit = headset.getRow().getValue("PM_START_UNIT");
                     if (startUnit.equals("Day")){
                       headset.setValue("START_VALUE", headset.getRow().getFieldValue("START_DATE"), false);
                     }
                  }
                  //Bug 92157, end
                  headset.next();
              }
           	headset.first();
      }
//    Bug 88982, End

      qrystr = mgr.createSearchURL(headblk); 
   }

   public void countFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("PM_TYPE_DB = Pm_Type_API.Get_Db_Value(0)");
      q.addWhereCondition("ORG_CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(ORG_CONTRACT) FROM dual)");

      mgr.submit(trans);

      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }

   public void newRow()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomFunction("HED","Pm_Type_API.Get_Client_Value(0)","PM_TYPE");

      cmd = trans.addEmptyCommand("HEAD","PM_ACTION_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      String pmType=trans.getValue("HED/DATA/PM_TYPE");
      data = trans.getBuffer("HEAD/DATA");
      data.setFieldItem("PM_TYPE",pmType);
      headset.addRow(data);
   }

   public boolean saveReturn()
   {
      ASPManager mgr = getASPManager();

      currrow = headset.getCurrentRowNo();
      headset.changeRow();

      int hislay = headlay.getHistoryMode();

      String pmStartUnit = headset.getRow().getValue("PM_START_UNIT");
      String StartValue = headset.getRow().getValue("START_VALUE");

      if (!(mgr.isEmpty(pmStartUnit)))
      {
         String selMon = "select (Pm_Start_Unit_API.Get_Client_Value(2)) CLIENT_START2 from DUAL";
         String selWee = "select (Pm_Start_Unit_API.Get_Client_Value(1)) CLIENT_START1 from DUAL";
         String selDay = "select (Pm_Start_Unit_API.Get_Client_Value(0)) CLIENT_START from DUAL";

         trans1.addQuery("YYM",selMon);
         trans1.addQuery("YYW",selWee);
         trans1.addQuery("YYD",selDay);

         trans1 = mgr.perform(trans1);

         String clientValMon  = trans1.getValue("YYM/DATA/CLIENT_START2");
         String clientValwee  = trans1.getValue("YYW/DATA/CLIENT_START1");
         String clientValDay  = trans1.getValue("YYD/DATA/CLIENT_START");

         trans1.clear();

         if (clientValMon.equals(pmStartUnit))
         {
            temp = headset.getRow().getValue("START_VALUE");
            length = temp.length();

            if (length != 4)
            {
               dateMask = mgr.getFormatMask("Date",true);
               mgr.showAlert(mgr.translate("PCMWPMACTIONINFO1: Invalid Start Value ")+ temp+mgr.translate("PCMWPMACTIONINFO2: . The Format should be  ")+ dateMask+mgr.translate("PCMWPMACTIONINFO3:   for Start Unit 'Day' and YYMM or YYWW for Start Unit 'Month' or 'Week'."));           

               headlay.setLayoutMode( Integer.valueOf(laymode).intValue() );

               return false;
            }
            else
            {
               String nTYvalue = temp.substring(0,2);
               String nTMvalue = temp.substring(2,4);

               String sel2 = "select to_number('"+StartValue+"     ') TEMPSTA from DUAL";
               String sel3 = "select to_number('"+nTYvalue+"') TEMPTY from DUAL";
               String sel4 = "select to_number('"+nTMvalue+"') TEMPTM from DUAL";

               trans1.addQuery("XX2",sel2);
               trans1.addQuery("XX3",sel3);
               trans1.addQuery("XX4",sel4);
               trans1 = mgr.perform(trans1);

               double nTY = trans1.getNumberValue("XX3/DATA/TEMPTY"); 
               double nTM = trans1.getNumberValue("XX4/DATA/TEMPTM");

               if (( length != 4 ) ||  ( nTM < 1 ) ||  ( nTM > 12 ) ||  ( nTY < 0 ))
               {
                  dateMask = mgr.getFormatMask("Date",true);
                  mgr.showAlert(mgr.translate("PCMWPMACTIONINFO1: Invalid Start Value ")+temp+mgr.translate("PCMWPMACTIONINFO4: . The Format should be ")+ dateMask+mgr.translate("PCMWPMACTIONINFO3:   for Start Unit 'Day' and YYMM or YYWW for Start Unit 'Month' or 'Week'."));           

                  headlay.setLayoutMode( Integer.valueOf(laymode).intValue() );

                  return false;
               }
               else
               {
                  trans1.clear();
                  //bug 58216 Start
                  String sel = "select to_date(?,'RRMM') START_DATE from DUAL";
                  q = trans1.addQuery("XX",sel);
                  q.addParameter("START_VALUE",StartValue);
                  //bug 58216 End
                  trans1 = mgr.perform(trans1);

                  String calStatDate  = trans1.getValue("XX/DATA/START_DATE");

                  temp1 = headset.getRow();
                  temp1.setValue("START_DATE",calStatDate);
                  temp1.setValue("START_VALUE",StartValue);
                  headset.setRow(temp1);                                
                  trans1.clear();
                  trans1 = mgr.submit(trans1);
                  headset.goTo(currrow);
                  okFindITEM2Temp();
               }
            }   
         }
         else if (   clientValwee.equals(   pmStartUnit))
         {
            temp = mgr.readValue("START_VALUE");
            length = temp.length();

            cmd = trans1.addCustomFunction("STARTDA","Pm_Calendar_API.Get_Date","START_DATE");
            cmd.addParameter("START_VALUE",StartValue);
            cmd.addParameter("INDEX1","1");

            trans1=mgr.perform(trans1);  
            String startDate = trans1.getValue("STARTDA/DATA/START_DATE");

            if ((mgr.isEmpty(startDate) ))
            {
               dateMask = mgr.getFormatMask("Date",true);
               mgr.showAlert(mgr.translate("PCMWPMACTIONINFO1: Invalid Start Value ")+temp+mgr.translate("PCMWPMACTIONINFO4: . The Format should be ")+ dateMask+mgr.translate("PCMWPMACTIONINFO3:   for Start Unit 'Day' and YYMM or YYWW for Start Unit 'Month' or 'Week'."));

               headlay.setLayoutMode( Integer.valueOf(laymode).intValue() );

               return false;
            }
            else
            {
               temp1 = headset.getRow();
               temp1.setValue("START_DATE",trans1.getValue("STARTDA/DATA/START_DATE"));
               temp1.setValue("START_VALUE",StartValue);
               headset.setRow(temp1);
               trans1.clear();
               trans1 = mgr.submit(trans1);
               headset.goTo(currrow);
               okFindITEM2Temp();
            }
         }
         else if (clientValDay.equals(pmStartUnit))
         {
            dateMask = mgr.getFormatMask("Date",true);

            trans1.clear();

            ASPBuffer buf = mgr.newASPBuffer();
            buf.addFieldItem("START_VALUE_TEMP",StartValue); 

            //Bug 58216 Start 
            String sel = "select to_date(?,'YYYY-MM-DD-hh24.MI.ss') START_DATE from DUAL";
            q = trans1.addQuery("XX",sel);
            q.addParameter("DESCRIPTION",buf.getValue("START_VALUE_TEMP"));
            //Bug 58216 End 
            trans1 = mgr.perform(trans1);

            String calStatDate  = trans1.getValue("XX/DATA/START_DATE");

            temp1 = headset.getRow();
            temp1.setValue("START_DATE",calStatDate);
            temp1.setValue("START_VALUE",StartValue);
            headset.setRow(temp1);

            trans1.clear();
            trans1 = mgr.submit(trans1);
            okFindITEM2Temp();
         }
         else
         {
            trans1.clear();
            trans1 = mgr.submit(trans1);
            okFindITEM2Temp();
         }
      }
      else
      {
         trans.clear();
         mgr.submit(trans);
      }   

      headset.goTo(currrow); 

      if (headset.countRows()>0)
      {
         // Prepare Query String
         if (mgr.isEmpty(qrystr))
            qrystr = mgr.getURL()+"?SEARCH=Y&PM_NO="+headset.getRow().getValue("PM_NO");
         else
         {
            int start = qrystr.indexOf("PM_NO");
            if (start > 0)
            {
               String subStr1 = qrystr.substring(start);
               int end = subStr1.indexOf("&");
               String subStr2;
               if (end > 0)
                  subStr2 = subStr1.substring(0,end);
               else
                  subStr2 = subStr1;
               String subStr3 = subStr2+";"+headset.getRow().getValue("PM_NO");
               qrystr = qrystr.replaceAll(subStr2,subStr3);
            }
            else
               qrystr = qrystr+";PM_NO="+headset.getRow().getValue("PM_NO");
         }
      }

      okFindITEM0();
      okFindITEM1();
      okFindITEM2();
      okFindITEM3();
      okFindITEM4();
      okFindITEM5();
      okFindITEM7();
      return true;
   }

   public void saveNew()
   {

      currrow = headset.getCurrentRowNo();
      trans.clear();

      if (saveReturn())
         newRow();
   }

//-----------------------------------------------------------------------------
//-----------------------  CMDBAR SEARCH-IT FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

   public void okFindITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      currrow = headset.getCurrentRowNo();

      q = trans.addQuery(itemblk0);
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addParameter("PM_NO",headset.getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
      //Bug 58216 End
      q.includeMeta("ALL");

      mgr.submit(trans);

      if (( itemset0.countRows() == 0 ) &&  ! qryStrSearch)
      {
         if ((!("ITEM1.OkFind".equals(mgr.readValue("__COMMAND")))) && !headSearch)
         {
            if (!("ITEM0.OkFind".equals(mgr.readValue("__COMMAND"))))
            {
               itemset1.clear();
            }
         }
      }

      headset.goTo(currrow);  
   }

   public void okFindITEM1()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      currrow = headset.getCurrentRowNo();

      q = trans.addQuery(itemblk1);
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addParameter("PM_NO",headset.getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
      //Bug 58216 End
      q.includeMeta("ALL");

      mgr.submit(trans);

      if (( itemset1.countRows() == 0 ) &&  ! qryStrSearch)
      {
         if ((!("ITEM2.OkFind".equals(mgr.readValue("__COMMAND")))) && !headSearch)
         {
            if (!("ITEM1.OkFind".equals(mgr.readValue("__COMMAND"))))
            {
               itemset1.clear();
            }
         }
      }

      if (itemset1.countRows() > 0)
      {
         getCost();
      }

      headset.goTo(currrow); 
   }

   public void getCost()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      double salesPaCost;

      n = itemset1.countRows();
      itemset1.first();

      for (i=1;i <= n;i++)
      {
         trans.clear();
         cmd = trans.addCustomFunction("INVENPART","INVENTORY_PART_API.Part_Exist","INVENTORYFLAG"); 
         cmd.addParameter("SPARE_CONTRACT",itemset1.getRow().getValue("SPARE_CONTRACT"));    
         cmd.addParameter("PART_NO",itemset1.getRow().getValue("PART_NO"));

         ASPQuery sysd = trans.addQuery("HPSD","select sysdate PSDATE from dual");

         trans = mgr.perform(trans);

         String invenFlag = trans.getValue("INVENPART/DATA/INVENTORYFLAG"); 
         String sysDate = trans.getBuffer("HPSD/DATA").getFieldValue("PSDATE");

         String spareCont = itemset1.getRow().getValue("SPARE_CONTRACT");
         String partNo =itemset1.getRow().getFieldValue("PART_NO");
         String serialNo = itemset1.getRow().getFieldValue("SERIAL_NO");
         String conditionCode = itemset1.getRow().getFieldValue("CONDITION_CODE");
         String configurationId = itemset1.getRow().getFieldValue("CONFIGURATION_ID");

         trans.clear();

         if ("1".equals(invenFlag))
         {
            /*cmd = trans.addCustomFunction("SALESPARTCOST","Inventory_Part_API.Get_Inventory_Value_By_Method","SALES_PART_COST"); 
            cmd.addParameter("SPARE_CONTRACT",itemset1.getRow().getValue("SPARE_CONTRACT"));    
            cmd.addParameter("PART_NO",itemset1.getRow().getValue("PART_NO"));  */

            cmd = trans.addCustomCommand("SALESPARTCOST","Active_Separate_API.Get_Inventory_Value");
            cmd.addParameter("SALES_PART_COST");
            cmd.addParameter("SPARE_CONTRACT",spareCont);
            cmd.addParameter("PART_NO",partNo);
            cmd.addParameter("SERIAL_NO",serialNo);
            cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
            cmd.addParameter("CONDITION_CODE",conditionCode);

            trans = mgr.perform(trans);

            salesPaCost =  trans.getNumberValue("SALESPARTCOST/DATA/SALES_PART_COST");
         }
         else
         {
            String nVendor = null;

            cmd = trans.addCustomFunction("PRIMARYSUPP","Purchase_Order_Line_Util_API.Get_Primary_Supplier","PRIMARY_SUPPLIER"); 
            cmd.addParameter("SPARE_CONTRACT",itemset1.getRow().getValue("SPARE_CONTRACT"));    
            cmd.addParameter("PART_NO",itemset1.getRow().getValue("PART_NO"));

            trans = mgr.perform(trans);
            nVendor = trans.getValue("PRIMARYSUPP/DATA/PRIMARY_SUPPLIER");
            trans.clear();

            if (!mgr.isEmpty(nVendor))
            {
               cmd = trans.addCustomCommand("SALESPARTCOST1", "Purchase_Order_Line_Util_API.Get_Base_Price_Supp_Part");
               cmd.addParameter("BASE_PRICE");
               cmd.addParameter("PART_NO",itemset1.getRow().getValue("PART_NO"));
               cmd.addParameter("SPARE_CONTRACT",itemset1.getRow().getValue("SPARE_CONTRACT"));
               cmd.addParameter("PRIMARY_SUPPLIER",nVendor);
               cmd.addParameter("QTY_PLAN",itemset1.getRow().getValue("QTY_PLAN"));
               cmd.addParameter("PSDATE",sysDate);

               trans = mgr.perform(trans);

               salesPaCost =  trans.getNumberValue("SALESPARTCOST1/DATA/BASE_PRICE");
            }
            else
               salesPaCost = 0;
         }
         row = itemset1.getRow();
         row.setNumberValue("SALES_PART_COST",salesPaCost);
         itemset1.setRow(row);   
         itemset1.next();
      }

      itemset1.first();
   }    

   public void okFindITEM2()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      currrow = headset.getCurrentRowNo();

      q = trans.addQuery(itemblk2);
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addParameter("PM_NO",headset.getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
      //Bug 58216 End
      q.setOrderByClause("GENERATION_DATE,PLANNED_DATE");
      q.includeMeta("ALL");

      mgr.submit(trans);

      if (( itemset2.countRows() == 0 ) &&  ! qryStrSearch)
      {
         if ((!("ITEM3.OkFind".equals(mgr.readValue("__COMMAND")))) && !headSearch)
         {
            if (!("ITEM2.OkFind".equals(mgr.readValue("__COMMAND"))))
            {
               itemset2.clear();
            }
         }
      }

      headset.goTo(currrow);
   }

   public void okFindITEM21()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      currrow = headset.getCurrentRowNo();

      q = trans.addQuery(itemblk2);
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addParameter("PM_NO",headset.getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
      //Bug 58216 End
      q.setOrderByClause("GENERATION_DATE,PLANNED_DATE");
      q.includeMeta("ALL");

      mgr.submit(trans);

      if (( itemset2.countRows() == 0 ) &&  ! qryStrSearch)
      {
         if ((!("ITEM3.OkFind".equals(mgr.readValue("__COMMAND")))) && !headSearch)
         {
            if (!("ITEM2.OkFind".equals(mgr.readValue("__COMMAND"))))
            {
               itemset2.clear();
            }
         }
      }

      headset.goTo(currrow);
   }

   public void okFindITEM2Temp()
   {
      int currow1;
      ASPManager mgr = getASPManager();

      trans.clear();
      currow1 = headset.getCurrentRowNo();

      q = trans.addQuery(itemblk2);
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addParameter("PM_NO",headset.getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
      //Bug 58216 End
      q.setOrderByClause("GENERATION_DATE,PLANNED_DATE");
      q.includeMeta("ALL");

      mgr.submit(trans);

      headset.goTo(currow1);
   }

   public void okFindITEM3()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      currrow = headset.getCurrentRowNo();

      q = trans.addQuery(itemblk3);
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addParameter("PM_NO",headset.getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
      q.addWhereCondition("CONTRACT = ?");
      q.addParameter("HEAD_CONTRACT",headset.getValue("CONTRACT"));
      q.addWhereCondition("MCH_CODE = ?");
      q.addParameter("MCH_CODE",headset.getValue("MCH_CODE"));
      //Bug 58216 End
      q.includeMeta("ALL");

      mgr.submit(trans);

      if (( itemset3.countRows() == 0 ) &&  ! qryStrSearch)
      {
         if ((!("ITEM4.OkFind".equals(mgr.readValue("__COMMAND")))) && !headSearch)
         {
            if (!("ITEM3.OkFind".equals(mgr.readValue("__COMMAND"))))
            {
               itemset3.clear();

            }
         }
      }

      headset.goTo(currrow);
   }

   public void okFindITEM4()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      currrow = headset.getCurrentRowNo();

      q = trans.addQuery(itemblk4);
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addParameter("PM_NO",headset.getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
      //Bug 58216 End
      q.includeMeta("ALL");

      mgr.submit(trans);

      if (( itemset4.countRows() == 0 ) &&  ! qryStrSearch)
      {
         if ((!("ITEM0.OkFind".equals(mgr.readValue("__COMMAND")))) && !headSearch)
         {
            if (!("ITEM4.OkFind".equals(mgr.readValue("__COMMAND"))))
            {
               itemset4.clear();
            }
         }
      }

      headset.goTo(currrow);   
   }

   //==T/F
   public void okFindITEM5()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      currrow = headset.getCurrentRowNo();

      q = trans.addQuery(itemblk5);
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addParameter("PM_NO",headset.getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
      //Bug 58216 End
      q.includeMeta("ALL");

      mgr.submit(trans);

      if (( itemset5.countRows() == 0 ) &&  ! qryStrSearch)
      {
         if ((!("ITEM0.OkFind".equals(mgr.readValue("__COMMAND")))) && !headSearch)
         {
            if (!("ITEM5.OkFind".equals(mgr.readValue("__COMMAND"))))
            {
               itemset5.clear();
            }
         }
      }

      headset.goTo(currrow);  
   }

   public void okFindITEM6()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      currrow = headset.getCurrentRowNo();

      q = trans.addQuery(itemblk6);
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addParameter("PM_NO",headset.getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
      //Bug 58216 End
      q.includeMeta("ALL");

      mgr.submit(trans);

      if (( itemset6.countRows() == 0 ) &&  ! qryStrSearch)
      {
         if ((!("ITEM0.OkFind".equals(mgr.readValue("__COMMAND")))) && !headSearch)
         {
            if (!("ITEM6.OkFind".equals(mgr.readValue("__COMMAND"))))
            {
               itemset6.clear();
            }
         }
      }

      headset.goTo(currrow);
   }

   public void okFindITEM7()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      q = trans.addQuery(itemblk7);
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addParameter("PM_NO",headset.getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
      //Bug 58216 End  

      q.includeMeta("ALL");
      int headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);

      headset.goTo(headrowno);
      if (itemset7 != null && itemset7.countRows() > 0)
         okFindITEM9();

      if (itemset7.countRows() == 0 && "ITEM7.OkFind".equals(mgr.readValue("__COMMAND")))
         itemset7.clear();
   }  

   public void okFindITEM8()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      headrowno = headset.getCurrentRowNo();
      itemset8.clear(); 
      q = trans.addQuery(itemblk8);
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addParameter("PM_NO",headset.getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
      //Bug 58216 End
      q.includeMeta("ALL");

      mgr.submit(trans);

      if (mgr.commandBarActivated())
      {
         if (itemset8.countRows() == 0 && "ITEM8.OkFind".equals(mgr.readValue("__COMMAND")))
            itemset8.clear();
      }

      setPriceAmount8();
      headset.goTo(headrowno);
   }

   public void okFindALLITEM8()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      headrowno = headset.getCurrentRowNo();
      itemset8.clear(); 
      q = trans.addEmptyQuery(itemblk8);
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addParameter("PM_NO",headset.getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
      //Bug 58216 End
      q.includeMeta("ALL");

      mgr.submit(trans);

      if (mgr.commandBarActivated())
      {
         if (itemset8.countRows() == 0 && "ITEM8.OkFind".equals(mgr.readValue("__COMMAND")))
         {
            itemset8.clear();
         }
      }

      setPriceAmount8();
      headset.goTo(headrowno);
   }

   public void okFindITEM9()
   {
      ASPManager mgr = getASPManager();
      if (itemset7 != null && itemset7.countRows() > 0)
      {
         trans.clear();
         int headrowNo = headset.getCurrentRowNo();
         int item7rowNo = itemset7.getCurrentRowNo();
         q = trans.addQuery(itemblk9);
         //Bug 58216 Start
         q.addWhereCondition("PM_NO = ? AND PM_REVISION = ? AND JOB_ID = ?");   
         q.addParameter("PM_NO",headset.getRow().getValue("PM_NO"));
         q.addParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));
         q.addParameter("ITEM0_JOB_ID",itemset7.getRow().getValue("JOB_ID"));
         //Bug 58216 End
         q.includeMeta("ALL");
         mgr.submit(trans);

         if (itemset9.countRows() == 0 && "ITEM9.OkFind".equals(mgr.readValue("__COMMAND")))
         {
            mgr.showAlert(mgr.translate("PCMWPMACTIONNODATA9: No data found."));
            itemset9.clear();
         }

         headset.goTo(headrowNo);
         itemset7.goTo(item7rowNo);
      }
   }

   public void countFindITEM9()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk9);
      q.setSelectList("to_char(count(*)) N");
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ? AND PM_REVISION = ? AND JOB_ID = ?");   
      q.addParameter("PM_NO",headset.getRow().getValue("PM_NO"));
      q.addParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));
      q.addParameter("ITEM0_JOB_ID",itemset7.getRow().getValue("JOB_ID"));
      //Bug 58216 End
      int headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);
      itemlay9.setCountValue(toInt(itemset9.getRow().getValue("N")));
      itemset9.clear();
      itemset9.goTo(headrowno);
   }

   public void connectExistingOperations()
   {
      ASPManager mgr = getASPManager();
      //this is needed when a new STD JOB is created.(createSearchURL is undefined at this instance)
      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      int head_current   = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);
      ctx.setGlobal("HEADGLOBAL", s_head_curr);

      if (itemlay9.isMultirowLayout())
         itemset9.goTo(itemset9.getRowSelected());

      bOpenNewWindow = true;
      urlString = "ConnectPmOperationsDlg.page?"+ "&PM_NO="+ (mgr.isEmpty(headset.getValue("PM_NO"))?"":headset.getValue("PM_NO")) +
                  "&PM_REVISION="+ (mgr.isEmpty(headset.getValue("PM_REVISION"))?"":headset.getValue("PM_REVISION")) +
                  "&JOB_ID="+ (mgr.isEmpty(itemset7.getValue("JOB_ID"))?"":itemset7.getValue("JOB_ID")) +
                  "&QRYSTR=" + mgr.URLEncode(qrystr) + "&FRMNAME=PmAction";
      newWinHandle = "connectExistingOperations"; 
      newWinHeight = 550;
      newWinWidth  = 550;

   }

   public void disconnectOperations()
   {
      ASPManager mgr = getASPManager();
      int count=0;
      int head_current = headset.getCurrentRowNo();
      if (itemlay9.isMultirowLayout())
      {
         itemset9.storeSelections();
         itemset9.setFilterOn();
         count = itemset9.countSelectedRows();
      }
      else
      {
         itemset9.unselectRows();
         count = 1;
      }
      itemset9.first();

      for (int i = 0; i < count; i++)
      {
         buff = itemset9.getRow();
         buff.setValue("JOB_ID","");
         itemset9.setRow(buff);
         itemset9.next();
      }

      trans = mgr.submit(trans);
      headset.goTo(head_current);
      okFindITEM9();
      itemset0.refreshAllRows();

   }


   public void countFindITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk0);
      q.setSelectList("to_char(count(*)) N");
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addParameter("PM_NO",headset.getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
      //Bug 58216 End

      headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);
      itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
      itemset0.clear();
      headset.goTo(headrowno);   
   }

   public void countFindITEM1()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk1);
      q.setSelectList("to_char(count(*)) N");
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addParameter("PM_NO",headset.getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
      //Bug 58216 End

      headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);
      itemlay1.setCountValue(toInt(itemset1.getRow().getValue("N")));
      itemset1.clear();
      headset.goTo(headrowno);
   }

   public void countFindITEM2()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk2);
      q.setSelectList("to_char(count(*)) N");
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addParameter("PM_NO",headset.getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
      //Bug 58216 End

      headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);
      itemlay2.setCountValue(toInt(itemset2.getRow().getValue("N")));
      itemset2.clear();
      headset.goTo(headrowno);
   }

   public void countFindITEM3()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk3);
      q.setSelectList("to_char(count(*)) N");
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addParameter("PM_NO",headset.getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
      q.addWhereCondition("CONTRACT = ?");
      q.addParameter("HEAD_CONTRACT",headset.getRow().getValue("CONTRACT"));
      q.addWhereCondition("MCH_CODE = ?");
      q.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));
      //Bug 58216 End

      headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);
      itemlay3.setCountValue(toInt(itemset3.getRow().getValue("N")));
      itemset3.clear();
      headset.goTo(headrowno);
   }

   public void countFindITEM4()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk4);
      q.setSelectList("to_char(count(*)) N");
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addParameter("PM_NO",headset.getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
      //Bug 58216 End

      headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);
      itemlay4.setCountValue(toInt(itemset4.getRow().getValue("N")));
      itemset4.clear();
      headset.goTo(headrowno);
   }

//T/F==
   public void countFindITEM5()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk5);
      q.setSelectList("to_char(count(*)) N");
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addParameter("PM_NO",headset.getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
      //Bug 58216 End

      headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);
      itemlay5.setCountValue(toInt(itemset5.getRow().getValue("N")));
      itemset5.clear();
      headset.goTo(headrowno);
   }

   public void countFindITEM6()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk6);
      q.setSelectList("to_char(count(*)) N");
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addParameter("PM_NO",headset.getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
      //Bug 58216 End

      headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);
      itemlay6.setCountValue(toInt(itemset6.getRow().getValue("N")));
      itemset6.clear();
      headset.goTo(headrowno);
   }

   public void countFindITEM7()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk7);
      q.setSelectList("to_char(count(*)) N");
      headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);
      itemlay7.setCountValue(toInt(itemset7.getRow().getValue("N")));
      itemset7.clear();
      headset.goTo(headrowno);
   }

   public void countFindITEM8()
   {
      ASPManager mgr = getASPManager();

      currrow = headset.getCurrentRowNo();
      q = trans.addQuery(itemblk8);
      q.setSelectList("to_char(count(*)) N");
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addParameter("PM_NO",headset.getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
      //Bug 58216 End
      mgr.submit(trans);

      itemlay8.setCountValue(toInt(itemset8.getRow().getValue("N")));
      itemset8.clear();
      headset.goTo(currrow);   
   }

   public void newRowITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      cmd = trans.addEmptyCommand("ITEM0","PM_ACTION_ROLE_API.New__",itemblk0);
      cmd.setParameter("PM_NO",headset.getRow().getValue("PM_NO"));
      cmd.setParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));
      cmd.setOption("ACTION","PREPARE");

      cmd = trans.addCustomFunction("CATCOST","Work_Order_Planning_Util_API.Get_Cost","CATALOG_COST");
      cmd.addParameter("ITEM0_ORG_CODE",headset.getRow().getValue("ORG_CODE"));
      cmd.addParameter("ROLE_CODE","");
      cmd.addParameter("ITEM0_CONTRACT",headset.getRow().getValue("ORG_CONTRACT"));
      cmd.addParameter("ITEM0_CATALOG_NO","");
      cmd.addParameter("ITEM0_ORG_CONTRACT",headset.getRow().getValue("ORG_CONTRACT"));

      trans = mgr.perform(trans);

      data = trans.getBuffer("ITEM0/DATA");
      data.setFieldItem("ITEM0_PM_NO",headset.getValue("PM_NO"));
      data.setFieldItem("ITEM0_PM_REVISION",headset.getValue("PM_REVISION"));
      data.setFieldItem("ITEM0_COMPANY",headset.getValue("COMPANY"));
      data.setFieldItem("ITEM0_ORG_CONTRACT",headset.getRow().getValue("ORG_CONTRACT"));
      data.setFieldItem("ITEM0_CONTRACT",headset.getRow().getValue("ORG_CONTRACT"));
      data.setFieldItem("ITEM0_EMPLOYEE_ID",headset.getRow().getValue("MAINT_EMPLOYEE"));
      data.setFieldItem("ITEM0_SIGNATURE",headset.getRow().getValue("MAINT_EMP_SIGN"));
      data.setFieldItem("ITEM0_ORG_CODE",headset.getRow().getValue("ORG_CODE"));
      data.setFieldItem("CATALOG_COST",trans.getValue("CATCOST/DATA/CATALOG_COST"));
      itemset0.addRow(data);
   }

   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM1","PM_ACTION_SPARE_PART_API.New__",itemblk1);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      data.setFieldItem("ITEM1_PM_NO",headset.getValue("PM_NO"));
      data.setFieldItem("ITEM1_PM_REVISION",headset.getValue("PM_REVISION"));
      data.setFieldItem("ITEM1_CONTRACT",headset.getValue("ORG_CONTRACT"));
      itemset1.addRow(data);
   }

   public void newRowITEM2()
   {
      ASPManager mgr = getASPManager();

      String pm_no = headset.getValue("PM_NO");
      String pm_rev = headset.getValue("PM_REVISION");
      String lsAttr;
      int end_pos;

      lsAttr = "PM_NO" + (char)31 + pm_no + (char)30;
      lsAttr = "PM_REVISION" + (char)31 + pm_rev + (char)30;

      cmd = trans.addCustomCommand("ITEM21", "PM_ACTION_CALENDAR_PLAN_API.New__"); 
      cmd.addParameter("INFO");    
      cmd.addParameter("OBJID");
      cmd.addParameter("OBJVERSION");
      cmd.addParameter("ATTR",lsAttr);
      cmd.addParameter("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("ITEM21/DATA");

      attribute = trans.getValue("ITEM21/DATA/ATTR");
      beg_pos = attribute.indexOf("SEQ_NO")+7;
      end_pos = attribute.lastIndexOf((char)30);
      String seq_no = attribute.substring(beg_pos, end_pos);

      data.setFieldItem("ITEM2_PM_NO", pm_no); 
      data.setFieldItem("ITEM2_PM_REVISION", pm_rev); 
      data.setFieldItem("ITEM2_SEQ_NO", seq_no);
      itemset2.addRow(data); 
   }

   public void newRowITEM3()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM3","PM_ACTION_CRITERIA_API.New__",itemblk3);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM3/DATA");
      data.setFieldItem("ITEM3_PM_NO",headset.getValue("PM_NO"));
      data.setFieldItem("ITEM3_PM_REVISION",headset.getValue("PM_REVISION"));
      data.setFieldItem("ITEM3_CONTRACT",headset.getValue("CONTRACT"));
      data.setFieldItem("ITEM3_MCH_CODE",headset.getValue("MCH_CODE"));
      itemset3.addRow(data);
   }

   public void newRowITEM4()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM4","PM_ACTION_PERMIT_API.New__",itemblk4);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM4/DATA");
      data.setFieldItem("ITEM4_PM_NO",headset.getValue("PM_NO"));
      data.setFieldItem("ITEM4_PM_REVISION",headset.getValue("PM_REVISION"));
      data.setFieldItem("ITEM4_GENERATE","TRUE");
      itemset4.addRow(data);
   }

   public void newRowITEM5()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM5","PM_ACTION_TOOL_FACILITY_API.New__",itemblk5);

      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM5_PM_NO", headset.getValue("PM_NO"));
      cmd.setParameter("ITEM5_PM_REVISION", headset.getValue("PM_REVISION"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM5/DATA");

      itemset5.addRow(data);
   }

   public void newRowITEM7()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM7","PM_ACTION_JOB_API.New__",itemblk7);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM7_PM_NO",headset.getRow().getValue("PM_NO"));
      cmd.setParameter("ITEM7_PM_REVISION",headset.getRow().getValue("PM_REVISION"));

      trans = mgr.perform(trans);

      data = trans.getBuffer("ITEM7/DATA");
      data.setFieldItem("ITEM7_SIGNATURE",headset.getRow().getValue("MAINT_EMP_SIGN"));
      itemset7.addRow(data);
   }

   public void newRowITEM8()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM8","PM_ACTION_PLANNING_API.New__",itemblk8);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM8/DATA");
      data.setFieldItem("ITEM8_PM_NO",headset.getValue("PM_NO"));
      data.setFieldItem("ITEM8_PM_REVISION",headset.getValue("PM_REVISION"));
      itemset8.addRow(data);
   }

   public void newRowITEM9()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM9","PM_ACTION_ROLE_API.New__",itemblk9);
      cmd.setParameter("PM_NO",headset.getRow().getValue("PM_NO"));
      cmd.setParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM9/DATA");
      data.setFieldItem("ITEM9_PM_NO",headset.getValue("PM_NO"));
      data.setFieldItem("ITEM0_PM_REVISION",headset.getValue("PM_REVISION"));
      data.setFieldItem("ITEM0_COMPANY",headset.getValue("COMPANY"));
      data.setFieldItem("ITEM0_ORG_CONTRACT",headset.getRow().getValue("CONTRACT"));
      data.setFieldItem("ITEM0_CONTRACT",headset.getRow().getValue("CONTRACT"));
      data.setFieldItem("ITEM0_EMPLOYEE_ID",headset.getRow().getValue("MAINT_EMPLOYEE"));
      data.setFieldItem("ITEM0_SIGNATURE",headset.getRow().getValue("MAINT_EMP_SIGN"));
      data.setFieldItem("ITEM0_ORG_CODE",headset.getRow().getValue("ORG_CODE"));
      data.setFieldItem("ITEM9_JOB_ID",itemset7.getRow().getValue("JOB_ID"));
      itemset9.addRow(data);
   }



   public void duplicateITEM7()
   {
      ASPManager mgr = getASPManager();

      itemset7.goTo(itemset7.getRowSelected());

      ASPBuffer itemRowVals = itemset7.getRow();
      itemRowVals.setValue("JOB_ID","");
      itemset7.addRow(itemRowVals);

      itemlay7.setLayoutMode(itemlay7.NEW_LAYOUT);
   }

   public void deleteITEM7()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      int currrow = headset.getCurrentRowNo();

      if (itemlay7.isMultirowLayout())
         itemset7.goTo(itemset7.getRowSelected());

      itemset7.unselectRows();
      itemset7.selectRow();

      String sPmNo = headset.getRow().getValue("PM_NO");
      String sPmRevision = headset.getRow().getValue("PM_REVISION");
      String sJobNo = itemset7.getRow().getValue("JOB_ID");

      cmd = trans.addCustomCommand("CHECKCONN","Pm_Action_Job_API.Check_Job_Connection");
      cmd.addParameter("ROLES_EXIST");
      cmd.addParameter("MATERIAL_EXIST");
      cmd.addParameter("TOOL_FACILITY_EXIST");
      cmd.addParameter("DOC_EXIST");
      cmd.addParameter("STD_JOB_ID");
      cmd.addParameter("STD_JOB_CONTRACT");
      cmd.addParameter("STD_JOB_REVISION");
      cmd.addParameter("PM_NO", sPmNo);
      cmd.addParameter("PM_REVISION", sPmRevision);
      cmd.addParameter("JOB_ID", sJobNo);

      itemset7.setRemoved();

      trans = mgr.submit(trans);

      String rolesexist = trans.getValue("CHECKCONN/DATA/ROLES_EXIST");
      String matexist = trans.getValue("CHECKCONN/DATA/MATERIAL_EXIST");
      String toolsfacexist = trans.getValue("CHECKCONN/DATA/TOOL_FACILITY_EXIST");
      String docexist = trans.getValue("CHECKCONN/DATA/DOC_EXIST");

      String sStdJobId = trans.getValue("CHECKCONN/DATA/STD_JOB_ID");
      String sStdJobContract = trans.getValue("CHECKCONN/DATA/STD_JOB_CONTRACT");
      String sStdJobRevision = trans.getValue("CHECKCONN/DATA/STD_JOB_REVISION");

      headset.goTo(currrow);

      okFindITEM7();

      headset.goTo(currrow);

      if ("1".equals(rolesexist) || "1".equals(matexist) || "1".equals(toolsfacexist) || "1".equals(docexist))
         mgr.showAlert(mgr.translate("SEPPMREMALLCONN: All Operations, Materials, Tools/Facilities and Documents connected to this PM Action through Standard Job &1 - Site &2 - Revision &3, were removed.", sStdJobId, sStdJobContract, sStdJobRevision));
   }

   public void saveReturnITEM8()
   {
      ASPManager mgr = getASPManager();

      int currHead = headset.getCurrentRowNo();
      itemset8.changeRow();
      mgr.submit(trans);
      itemlay8.setLayoutMode(itemlay8.MULTIROW_LAYOUT);
      headset.goTo(currHead);
      okFindALLITEM8();
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------
//========custom functions for header=========

   public void none()
   {
      ASPManager mgr = getASPManager();

      mgr.showAlert(mgr.translate("PCMWPMACTIONNONE: No RMB method has been selected."));
   }

   public void connections()
   {
      ASPManager mgr = getASPManager();

      // 040114  ARWILK  Begin  (Enable Multirow RMB actions)
      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      if (headlay.isMultirowLayout())
         headset.store();
      else
      {
         headset.unselectRows();
         headset.selectRow();
      }

      bOpenNewWindow = true;
      urlString = createTransferUrl("RMBPmAction.page", headset.getSelectedRows("PM_NO,PM_REVISION"));
      newWinHandle = "RMBPmAction"; 
      // 040114  ARWILK  End  (Enable Multirow RMB actions)
   }

   public void replacements()
   {
      ASPManager mgr = getASPManager();

      // 040114  ARWILK  Begin  (Enable Multirow RMB actions)
      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      if (headlay.isMultirowLayout())
         headset.store();
      else
      {
         headset.unselectRows();
         headset.selectRow();
      }

      bOpenNewWindow = true;
      urlString = createTransferUrl("Replacements.page", headset.getSelectedRows("PM_NO,PM_REVISION"));
      newWinHandle = "Replacements"; 
      // 040114  ARWILK  End  (Enable Multirow RMB actions)
   }

   public void generateWorkOrder()
   {
      ASPManager mgr = getASPManager();
      String attr;
      String sysDate;
      String isGenType;
      String woString;
      boolean moreThanOneWo = false;
      boolean woWrittenOnce = false;
      int count;

      trans.clear();

      cmd = trans.addQuery("HPSD", "select sysdate PSDATE from dual");

      cmd = trans.addCustomFunction("DECO", "Generation_Type_API.Decode('4')", "ISGENTYPE");

      trans = mgr.perform(trans);

      isGenType = trans.getValue("DECO/DATA/ISGENTYPE");
      attr = "GEN_TYPE" + (char)31 + isGenType + (char)30;
      sysDate = trans.getBuffer("HPSD/DATA").getFieldValue("PSDATE");

      trans.clear();

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
         count = headset.countSelectedRows();
      }
      else
      {
         headset.unselectRows();
         count = 1;
      }

      for (int i = 0; i < count; i++)
      {
         cmd = trans.addCustomCommand("HGNID_" + i, "Pm_Generation_API.Create_Gen");
         cmd.addParameter("GEN_ID1", "");
         cmd.addParameter("GNTYPE", isGenType);
         cmd.addParameter("SNULL", attr);

         cmd = trans.addQuery("GETSYS", "select sysdate PSDATE from dual");

         if (mgr.isEmpty(headset.getValue("PLAN_HRS")))
            cmd = trans.addQuery("HPFD_" + i, "select sysdate + 168/24 PFDATE from dual");
         else
         {
            //Bug 58216 Start
            q = trans.addQuery("HPFD_" + i, "select distinct sysdate + PLAN_HRS/24 PFDATE from PM_ACTION where PM_NO = ? and PM_REVISION = ?");
            q.addParameter("PM_NO",headset.getValue("PM_NO"));
            q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
            //Bug 58216 End
         }

         cmd = trans.addCustomCommand("GENE3_" + i, "Pm_Action_API.Generate__");
         cmd.addParameter("WO_NO1", "");
         cmd.addParameter("SNULL", "");
         cmd.addParameter("PM_NO", headset.getValue("PM_NO"));
         cmd.addParameter("PM_REVISION", headset.getValue("PM_REVISION"));
         cmd.addReference("PSDATE", "GETSYS/DATA");
         cmd.addReference("PFDATE", "HPFD_" + i + "/DATA");
         cmd.addParameter("HSEQ_NO", "0");
         cmd.addReference("GEN_ID1", "HGNID_" + i + "/DATA");

         if (headlay.isMultirowLayout())
            headset.next();
      }

      trans = mgr.perform(trans);

      woString = "";

      for (int i = 0; i < count; i++)
      {
         if (!mgr.isEmpty(trans.getValue("GENE3_" + i + "/DATA/WO_NO1")))
         {
            if (!woWrittenOnce)
            {
               woString = trans.getValue("GENE3_" + i + "/DATA/WO_NO1");
               woWrittenOnce = true;
               moreThanOneWo = false;
            }
            else
            {
               woString += "," + trans.getValue("GENE3_" + i + "/DATA/WO_NO1");
               moreThanOneWo = true;
            }

            bWorkOrderGenerated = true;
         }
      }

      if (headlay.isMultirowLayout())
         headset.setFilterOff();

      if (headset.countRows()>0)
         headset.refreshAllRows();
      if (headlay.isSingleLayout())
         okFindITEM2();

      if (moreThanOneWo)
         sMsgTxt = mgr.translate("PCMWPMACTIONWOCREATEDSUCCESSFULLY: &1 Work Orders have been created successfully",Integer.toString(count));
      else
         sMsgTxt = mgr.translate("PCMWPMACTIONWOCREATED: Work Order Created  - &1", woString);
   }

   public void perform(String command) 
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.markSelectedRows(command);
         mgr.submit(trans);

         headset.refreshAllRows();
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
   }

   public void activatePm()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      int count;

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());

      int currrow = headset.getCurrentRowNo();
      headset.markRow("ACTIVE__");

      String sPmNo = headset.getValue("PM_NO");
      String sPmRev = headset.getValue("PM_REVISION");
      
      //Bug 89752, Start
      cmd = trans.addCustomFunction("GETACTIVEREV","Pm_Action_API.Get_Active_Revision","ACTIVE_REV");
      cmd.addParameter("PM_NO", sPmNo);
      //Bug 89752, End

      //Bug 67909, start
      cmd = trans.addCustomFunction("SETOTHRSOBSOLETE","Pm_Action_API.Set_Others_To_Obsolete","NUMDUMMY");
      cmd.addParameter("PM_NO", sPmNo);
      cmd.addParameter("PM_REVISION", sPmRev);

      //Bug 89752, Start
      cmd = trans.addCustomCommand("COPYGENVALUES","Pm_Action_Criteria_API.Copy_Generation_Values");
      cmd.addParameter("PM_NO", sPmNo);
      cmd.addParameter("PM_REVISION", sPmRev);
      cmd.addReference("ACTIVE_REV", "GETACTIVEREV/DATA");
      //Bug 89752, End

      trans = mgr.perform(trans);

      double numRows = trans.getNumberValue("SETOTHRSOBSOLETE/DATA/NUMDUMMY");
      //Bug 67909, end

      if (numRows > 0)
      {
         //Bug 67948. start
         trans.clear();
         cmd = trans.addCustomCommand("REMOBSREP","Pm_Action_Replaced_API.Remove_Replacements");
         cmd.addParameter("PM_NO", sPmNo);
         cmd.addParameter("PM_REVISION", sPmRev);

         trans = mgr.perform(trans);
         //Bug 67948. end
         bSetObsolete = true;

         //Bug 67909, start
         sMsgTxt = mgr.translate("PCMWPMACTOTHETSTOOBS: All other revisions for PM No &1 will be set to \"Obsolete\".", sPmNo); 
         //Bug 67909, end
      }

      trans.clear();
      mgr.submit(trans);

      headset.refreshAllRows();
      headset.goTo(currrow);
      if (itemset2.countRows()>0)
         itemset2.refreshAllRows();
      //Bug 89752, Start
      if (itemset3.countRows()>0)
         itemset3.refreshAllRows();
      //Bug 89752, End
   }

   public void obsoletePm()
   {
      ASPManager mgr = getASPManager();
      //Bug 67948. start
      String sPmNo = headset.getValue("PM_NO"); 
      String sPmRev = headset.getValue("PM_REVISION"); 
      //Bug 67948. end


      perform("OBSOLETE__");

      //Bug 67948. start
      trans.clear();
      cmd = trans.addCustomCommand("REMCURROBSREP","Pm_Action_Replaced_API.Remove_Curr_Replacements");
      cmd.addParameter("PM_NO", sPmNo);
      cmd.addParameter("PM_REVISION", sPmRev);

      trans = mgr.perform(trans);
      //Bug 67948. end
   }

   public void schonobjaddr()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      calling_url=mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      // 040114  ARWILK  Begin  (Remove uneccessary global variables)
      bOpenNewWindow = true;
      urlString = "../equipw/EquipmentObjectAddress1.page?MCH_CODE=" + mgr.URLEncode(headset.getValue("MCH_CODE")) +
                  "&CONTRACT=" + mgr.URLEncode(headset.getValue("CONTRACT"));

      newWinHandle = "schonobjaddr"; 
      // 040114  ARWILK  End  (Remove uneccessary global variables)
   }

   public void copyPmActions()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      // 040114  ARWILK  Begin  (Remove uneccessary global variables)
      bOpenNewWindow = true;
      urlString = "CopyPmActions.page?MCH_CODE=" + mgr.URLEncode(headset.getValue("MCH_CODE")) +
                  "&CONTRACT=" + mgr.URLEncode(headset.getValue("CONTRACT")) + 
                  "&PM_NO=" + mgr.URLEncode(headset.getValue("PM_NO"))+
                  "&PM_REVISION=" + mgr.URLEncode(headset.getValue("PM_REVISION"));

      newWinHandle = "copyPmActions"; 
      // 040114  ARWILK  End  (Remove uneccessary global variables)
   }

   public void createNewRevision()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      bOpenNewWindow = true;

      //Bug 89283, Start
      if (qrystr.length()<= qrystr.indexOf("SEARCH=Y")+8){
         qrystr = qrystr+"&PM_NO="+headset.getValueAt(0, "PM_NO");
         for (int i=1; i<headset.countRows();i++) {
             qrystr = qrystr+";"+headset.getValueAt(i, "PM_NO");
		}
      }
      //Bug 89283, End
      	
      urlString = "CreatePmRevisionDlg.page?PM_NO=" + mgr.URLEncode(headset.getValue("PM_NO")) +
                  "&PM_REVISION=" + mgr.URLEncode(headset.getValue("PM_REVISION"))+
                  "&QRYSTR=" + mgr.URLEncode(qrystr);

      newWinHandle = "CreatePmRevisionDlg"; 
   }

//========custom functions for Materials=========

   // 031211  ARWILK  Begin  (Repalce links with RMB's)
   public void sparePartObject()
   {
      ASPManager mgr = getASPManager();

      bOpenNewWindow = true;

      urlString = "MaintenanceObject.page?MCH_CODE=" + mgr.URLEncode(headset.getValue("MCH_CODE")) +
                  "&CONTRACT=" + mgr.URLEncode(headset.getValue("CONTRACT")) +
                  "&PM_NO=" + mgr.URLEncode(headset.getValue("PM_NO")) +
                  "&PM_REVISION=" + mgr.URLEncode(headset.getValue("PM_REVISION")) +
                  "&QRYSTR=" + mgr.URLEncode(qrystr) +
                  "&FRMNAME=PmAction";

      newWinHandle = "sparePartObject";
   }
   // 031211  ARWILK  End  (Repalce links with RMB's)

   public void sparePartsInDetPart()
   {
      ASPManager mgr = getASPManager();

      // 040114  ARWILK  Begin  (Enable Multirow RMB actions)
      int count = 0;
      ASPBuffer transferBuffer;
      ASPBuffer rowBuff;

      bOpenNewWindow = true;

      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      if (itemlay1.isMultirowLayout())
      {
         itemset1.storeSelections();
         itemset1.setFilterOn();
         count = itemset1.countSelectedRows();
      }
      else
      {
         itemset1.unselectRows();
         count = 1;
      }

      transferBuffer = mgr.newASPBuffer();

      for (int i = 0; i < count; i++)
      {
         rowBuff = mgr.newASPBuffer();

         if (i == 0)
            rowBuff.addItem("SPARE_ID", itemset1.getValue("PART_NO"));
         else
            rowBuff.addItem(null, itemset1.getValue("PART_NO"));

         transferBuffer.addBuffer("DATA", rowBuff);

         if (itemlay1.isMultirowLayout())
            itemset1.next();
      }

      if (itemlay1.isMultirowLayout())
         itemset1.setFilterOff();

      urlString = createTransferUrl("../equipw/EquipmentSpareStructure2.page", transferBuffer);
      newWinHandle = "EquipmentSpareStructure2"; 
      // 040114  ARWILK  End  (Enable Multirow RMB actions)
   }

   public void currQuntity()
   {
      ASPManager mgr = getASPManager();

      // 040114  ARWILK  Begin  (Enable Multirow RMB actions)
      int count = 0;
      ASPBuffer transferBuffer;
      ASPBuffer rowBuff;

      bOpenNewWindow = true;

      if (itemlay1.isMultirowLayout())
      {
         itemset1.storeSelections();
         itemset1.setFilterOn();
         count = itemset1.countSelectedRows();
      }
      else
      {
         itemset1.unselectRows();
         count = 1;
      }

      transferBuffer = mgr.newASPBuffer();

      for (int i = 0; i < count; i++)
      {
         rowBuff = mgr.newASPBuffer();

         if (i == 0)
         {
            rowBuff.addItem("PART_NO", itemset1.getValue("PART_NO"));
            rowBuff.addItem("CONTRACT", itemset1.getValue("SPARE_CONTRACT"));
         }
         else
         {
            rowBuff.addItem(         null,          itemset1.getValue(         "PART_NO"));
            rowBuff.addItem(null, itemset1.getValue("SPARE_CONTRACT"));
         }

         transferBuffer.addBuffer("DATA", rowBuff);

         if (itemlay1.isMultirowLayout())
            itemset1.next();
      }

      if (itemlay1.isMultirowLayout())
         itemset1.setFilterOff();

      urlString = createTransferUrl("../invenw/InventoryPartInventoryPartCurrentOnHand.page", transferBuffer);
      newWinHandle = "InventoryPartInventoryPartCurrentOnHand"; 
      // 040114  ARWILK  End  (Enable Multirow RMB actions)
   }

   public void availDetail()
   {
      ASPManager mgr = getASPManager();

      // 040114  ARWILK  Begin  (Enable Multirow RMB actions)
      int count = 0;
      ASPBuffer transferBuffer;
      ASPBuffer rowBuff;

      bOpenNewWindow = true;

      if (itemlay1.isMultirowLayout())
      {
         itemset1.storeSelections();
         itemset1.setFilterOn();
         count = itemset1.countSelectedRows();
      }
      else
      {
         itemset1.unselectRows();
         count = 1;
      }

      transferBuffer = mgr.newASPBuffer();

      for (int i = 0; i < count; i++)
      {
         rowBuff = mgr.newASPBuffer();

         if (i == 0)
         {
            rowBuff.addItem("PART_NO", itemset1.getValue("PART_NO"));
            rowBuff.addItem("CONTRACT", itemset1.getValue("SPARE_CONTRACT"));
            rowBuff.addItem("PROJECT_ID", "*");
            rowBuff.addItem("CONFIGURATION_ID", "*");
         }
         else
         {
            rowBuff.addItem(null, itemset1.getValue("PART_NO"));
            rowBuff.addItem(null, itemset1.getValue("SPARE_CONTRACT"));
            rowBuff.addItem(null, "*");
            rowBuff.addItem(null, "*");
         }

         transferBuffer.addBuffer("DATA", rowBuff);

         if (itemlay1.isMultirowLayout())
            itemset1.next();
      }

      if (itemlay1.isMultirowLayout())
         itemset1.setFilterOff();

      urlString = createTransferUrl("../invenw/InventoryPartAvailabilityPlanningQry.page", transferBuffer);
      newWinHandle = "InventoryPartAvailabilityPlanningQry"; 
      // 040114  ARWILK  End  (Enable Multirow RMB actions)
   }

   public void inventoryPart()
   {
      ASPManager mgr = getASPManager();

      // 040114  ARWILK  Begin  (Enable Multirow RMB actions)
      int count = 0;
      ASPBuffer transferBuffer;
      ASPBuffer rowBuff;

      bOpenNewWindow = true;

      if (itemlay1.isMultirowLayout())
      {
         itemset1.storeSelections();
         itemset1.setFilterOn();
         count = itemset1.countSelectedRows();
      }
      else
      {
         itemset1.unselectRows();
         count = 1;
      }

      transferBuffer = mgr.newASPBuffer();

      for (int i = 0; i < count; i++)
      {
         rowBuff = mgr.newASPBuffer();

         if (i == 0)
         {
            rowBuff.addItem("PART_NO", itemset1.getValue("PART_NO"));
            rowBuff.addItem("CONTRACT", itemset1.getValue("SPARE_CONTRACT"));
         }
         else
         {
            rowBuff.addItem(null, itemset1.getValue("PART_NO"));
            rowBuff.addItem(null, itemset1.getValue("SPARE_CONTRACT"));
         }

         transferBuffer.addBuffer("DATA", rowBuff);

         if (itemlay1.isMultirowLayout())
            itemset1.next();
      }

      if (itemlay1.isMultirowLayout())
         itemset1.setFilterOff();

      urlString = createTransferUrl("../invenw/InventoryPart.page", transferBuffer);
      newWinHandle = "InventoryPart"; 
      // 040114  ARWILK  End  (Enable Multirow RMB actions)
   }

   public void supplierPerPart()
   {
      ASPManager mgr = getASPManager();

      // 040114  ARWILK  Begin  (Enable Multirow RMB actions)
      int count = 0;
      ASPBuffer transferBuffer;
      ASPBuffer rowBuff;

      bOpenNewWindow = true;

      if (itemlay1.isMultirowLayout())
      {
         itemset1.storeSelections();
         itemset1.setFilterOn();
         count = itemset1.countSelectedRows();
      }
      else
      {
         itemset1.unselectRows();
         count = 1;
      }

      transferBuffer = mgr.newASPBuffer();

      for (int i = 0; i < count; i++)
      {
         rowBuff = mgr.newASPBuffer();

         if (i == 0)
         {
            rowBuff.addItem("PART_NO", itemset1.getValue("PART_NO"));
            rowBuff.addItem("CONTRACT", itemset1.getValue("SPARE_CONTRACT"));
         }
         else
         {
            rowBuff.addItem(null, itemset1.getValue("PART_NO"));
            rowBuff.addItem(null, itemset1.getValue("SPARE_CONTRACT"));
         }

         transferBuffer.addBuffer("DATA", rowBuff);

         if (itemlay1.isMultirowLayout())
            itemset1.next();
      }

      if (itemlay1.isMultirowLayout())
         itemset1.setFilterOff();

      urlString = createTransferUrl("../purchw/PurchasePartSupplier.page", transferBuffer);
      newWinHandle = "PurchasePartSupplier"; 
      // 040114  ARWILK  End  (Enable Multirow RMB actions)
   }

//========custom functions for Maintenance Plan=========

   public void generateWorkOrder2()
   {
      ASPManager mgr = getASPManager();

      String strgeneratable;
      String sysDate;
      String isGenType;
      String attr;
      String woString;
      boolean moreThanOneWo = false;
      boolean woWrittenOnce = false;
      int count;

      cmd = trans.addQuery("GBLE","select Pm_Generateable_API.Decode('Y') GENABLE1 from dual");
      cmd = trans.addQuery("HPSD","select sysdate PSDATE from dual");
      cmd = trans.addCustomFunction("DECO", "Generation_Type_API.Decode('4')", "ISGENTYPE");

      trans = mgr.perform(trans);

      strgeneratable = trans.getValue("GBLE/DATA/GENABLE1");
      sysDate = trans.getBuffer("HPSD/DATA").getFieldValue("PSDATE");
      isGenType = trans.getValue("DECO/DATA/ISGENTYPE");
      attr = "GEN_TYPE" + (char)31 + isGenType + (char)30;

      trans.clear();

      if (itemlay2.isMultirowLayout())
      {
         itemset2.storeSelections();
         itemset2.setFilterOn();
         count = itemset2.countSelectedRows();
      }
      else
      {
         itemset2.unselectRows();
         count = 1;
      }

      for (int i = 0; i < count; i++)
      {
         if (strgeneratable.equals(itemset2.getRow().getValue("PM_GENERATEABLE"))
             && mgr.isEmpty(itemset2.getRow().getValue("WO_NO")))
         {
            cmd = trans.addCustomCommand("GNID_" + i, "Pm_Generation_API.Create_Gen");
            cmd.addParameter("GEN_ID1","");
            cmd.addParameter("GNTYPE",isGenType);
            cmd.addParameter("SNULL",attr);

            //Bug 58216 Start
            q = trans.addQuery("GETPSDATE","select to_date( ? ,'YYYY-MM-DD-hh24.mi.ss') PSDATE from dual");
            q.addParameter("DESCRIPTION",itemset2.getRow().getValue("PLANNED_DATE"));
            //Bug 58216 End

            if (mgr.isEmpty(headset.getValue("PLAN_HRS")))
               cmd = trans.addQuery("PFD_" + i,"select sysdate + 168/24 PFDATE from dual");
            else
            {
               //Bug 58216 Start
               q = trans.addQuery("PFD_" + i,"select distinct sysdate + PLAN_HRS/24 PFDATE from PM_ACTION where PM_NO = ? and PM_REVISION = ?"); 
               q.addParameter("PM_NO",headset.getValue("PM_NO"));
               q.addParameter("PM_REVISION",headset.getValue("PM_REVISION"));
               //Bug 58216 End
            } 

            cmd = trans.addCustomCommand("GENE4_" + i, "Pm_Action_API.Generate__");
            cmd.addParameter("WO_NO1","");
            cmd.addParameter("SNULL","");
            cmd.addParameter("PM_NO", itemset2.getRow().getValue("PM_NO"));
            cmd.addParameter("PM_REVISION", itemset2.getRow().getValue("PM_REVISION"));
            cmd.addReference("PSDATE","GETPSDATE/DATA");
            cmd.addReference("PFDATE", "PFD_" + i + "/DATA");
            cmd.addParameter("ITEM2_SEQ_NO", itemset2.getRow().getValue("SEQ_NO"));
            cmd.addReference("GEN_ID1","GNID_" + i + "/DATA");

            if (itemlay2.isMultirowLayout())
               itemset2.next();
         }
      }

      trans = mgr.perform(trans);
      woString = "";

      for (int i = 0; i < count; i++)
      {
         if (!mgr.isEmpty(trans.getValue("GENE4_" + i + "/DATA/WO_NO1")))
         {
            if (!woWrittenOnce)
            {
               woString = trans.getValue("GENE4_" + i + "/DATA/WO_NO1");
               woWrittenOnce = true;
               moreThanOneWo = false;
            }
            else
            {
               woString += "," + trans.getValue("GENE4_" + i + "/DATA/WO_NO1");
               moreThanOneWo = true;
            }

            bWorkOrderGenerated = true;
         }
      }

      if (itemlay2.isMultirowLayout())
         itemset2.setFilterOff();

      okFindITEM2();

      if (moreThanOneWo)
         sMsgTxt = mgr.translate("PCMWPMACTIONWOCREATEDSUCCESSFULLY: &1 Work Orders have been created successfully",Integer.toString(count));
      else
         sMsgTxt = mgr.translate("PCMWPMACTIONWOCREATED: Work Order Created  - &1", woString);
   }

   public void prepWorkOrder()
   {
      ASPManager mgr = getASPManager();

      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      // 040114  ARWILK  Begin  (Enable Multirow RMB actions)
      if (itemlay2.isMultirowLayout())
         itemset2.store();
      else
      {
         itemset2.unselectRows();
         itemset2.selectRow();
      }

      bOpenNewWindow = true;
      urlString = createTransferUrl("ActiveSeparate2.page", itemset2.getSelectedRows("ITEM2_WO_NO"));
      newWinHandle = "prepareWo"; 
      // 040114  ARWILK  End  (Enable Multirow RMB actions)
   }

   public boolean checkValidReportInRoute()
   {
      ASPManager mgr = getASPManager();

      if (itemlay2.isMultirowLayout())
         itemset2.goTo(itemset2.getRowSelected());
      else
         itemset2.selectRow();

      wo_num = itemset2.getRow().getFieldValue("ITEM2_WO_NO");             
      date_compl = itemset2.getRow().getFieldValue("COMPLETION_DATE");
      pm_type = itemset2.getRow().getFieldValue("ITEM2_PM_TYPE");

      if ((! mgr.isEmpty(wo_num) ) &&  ( mgr.isEmpty(date_compl) ) &&  ( "ActiveRound".equals(pm_type) ))
         return true;
      else
         return false;
   }

   public void viewHistWorkOrder()
   {
      ASPManager mgr = getASPManager();

      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      // 040114  ARWILK  Begin  (Enable Multirow RMB actions)
      if (itemlay2.isMultirowLayout())
         itemset2.store();
      else
      {
         itemset2.unselectRows();
         itemset2.selectRow();
      }

      bOpenNewWindow = true;
      urlString = createTransferUrl("HistoricalSeparateRMB.page", itemset2.getSelectedRows("ITEM2_WO_NO"));
      newWinHandle = "prepareWo"; 
      // 040114  ARWILK  End  (Enable Multirow RMB actions)
   }

   public void viewGenDetails()
   {
      ASPManager mgr = getASPManager();

      // 040114  ARWILK  Begin  (Enable Multirow RMB actions)
      int count = 0;
      ASPBuffer transferBuffer;
      ASPBuffer rowBuff;

      bOpenNewWindow = true;

      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      if (itemlay2.isMultirowLayout())
         itemset2.goTo(itemset2.getRowSelected());
      else
         itemset2.selectRow();

      transferBuffer = mgr.newASPBuffer();

      cmd = trans.addCustomFunction("GENTYPE", "PM_GENERATION_API.Get_Generation_Type", "ITEM2_GEN_TYPE");
      cmd.addParameter("ITEM2_GEN_ID", itemset2.getRow().getValue("GEN_ID"));

      trans = mgr.perform(trans);

      rowBuff = mgr.newASPBuffer();

      rowBuff.addItem("WO_NO",              itemset2.getRow().getValue("WO_NO"));
      rowBuff.addItem("PM_NO",              headset.getRow().getValue("PM_NO"));
      rowBuff.addItem("PM_REVISION",        headset.getRow().getValue("PM_REVISION"));
      rowBuff.addItem("MCHCODE",            itemset2.getRow().getValue("ITEM2_MCH_CODE"));
      rowBuff.addItem("DESCRIPTION",        itemset2.getRow().getValue("ITEM2_MCH_DESC"));
      rowBuff.addItem("SITE",               itemset2.getRow().getValue("ITEM2_SITE"));
      rowBuff.addItem("PLANNED_DATE",       itemset2.getRow().getValue("PLANNED_DATE"));
      rowBuff.addItem("PLANNED_WEEK",       itemset2.getRow().getValue("PLANNED_WEEK"));
      rowBuff.addItem("GEN_ID",             itemset2.getRow().getValue("GEN_ID"));  
      rowBuff.addItem("GENTYPE",            trans.getValue("GENTYPE/DATA/ITEM2_GEN_TYPE"));
      rowBuff.addItem("REPLACED_BY_PM_NO",  itemset2.getRow().getValue("REPLACED_BY_PM_NO"));
      rowBuff.addItem("REPLACED_BY_PM_REVISION",itemset2.getRow().getValue("REPLACED_BY_PM_REVISION"));
      rowBuff.addItem("REPLACED_BY_SEQ_NO", itemset2.getRow().getValue("REPLACED_BY_SEQ_NO"));
      rowBuff.addItem("EVENT",              itemset2.getRow().getValue("EVENT"));
      rowBuff.addItem("EVENT_SEQ",          itemset2.getRow().getValue("EVENT_SEQ"));
      rowBuff.addItem("TESTPOINT",          itemset2.getRow().getValue("TESTPOINT"));
      rowBuff.addItem("PARAMETER",          itemset2.getRow().getValue("PARAMETER"));
      rowBuff.addItem("PM_NO",              itemset2.getRow().getValue("VALUE"));

      transferBuffer.addBuffer("DATA", rowBuff);

      urlString = createTransferUrl("../pcmw/ViewGenerationDlg.page", transferBuffer);
      newWinHandle = "ViewGenerationDlg"; 
      // 040114  ARWILK  End  (Enable Multirow RMB actions)
   }


   public void jobsAndOperations()
   {

      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      if (itemlay2.isMultirowLayout())
         itemset2.goTo(itemset2.getRowSelected());
      else
         itemset2.selectRow();

      bOpenNewWindow = true;
      urlString = "JobsOperationsDlg.page?PM_NO=" + mgr.URLEncode(headset.getValue("PM_NO")) +
                  "&PM_REVISION=" + mgr.URLEncode(headset.getValue("PM_REVISION"))+
                  "&SEQ_NO=" + mgr.URLEncode(itemset2.getValue("SEQ_NO"))+ 
                  "&PLANNED_DATE=" + mgr.URLEncode(itemset2.getValue("PLANNED_DATE"))+
                  "&WO_NO=" + mgr.URLEncode(itemset2.getValue("WO_NO"));

      newWinHandle = "JobsOperationsDlg"; 
   }

//========custom functions for Criteria=========        

   public void parameters()
   {
      ASPManager mgr = getASPManager();

      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      // 040114  ARWILK  Begin  (Remove uneccessary global variables)
      bOpenNewWindow = true;
      urlString = "../equipw/RMBEquipmentObject.page?CONTRACT=" + mgr.URLEncode(headset.getValue("CONTRACT")) +
                  "&MCH_CODE="             +             mgr.URLEncode(            headset.getValue(            "MCH_CODE"));

      newWinHandle = "parameters"; 
      // 040114  ARWILK  End  (Remove uneccessary global variables)
   }

   public void measurements()
   {
      ASPManager mgr = getASPManager();

      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      // 040114  ARWILK  Begin  (Remove uneccessary global variables)
      bOpenNewWindow = true;
      urlString = "../equipw/RMBMaintenanceObject.page?CONTRACT=" + mgr.URLEncode(headset.getValue("CONTRACT")) +
                  "&MCH_CODE=" + mgr.URLEncode(headset.getValue("MCH_CODE"));

      newWinHandle = "measurements"; 
      // 040114  ARWILK  End  (Remove uneccessary global variables)
   }

//========custom functions for Permit=========

   public void attributes()
   {
      ASPManager mgr = getASPManager();

      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      // 040114  ARWILK  Begin  (Enable Multirow RMB actions)
      if (itemlay4.isMultirowLayout())
         itemset4.store();
      else
      {
         itemset4.unselectRows();
         itemset4.selectRow();
      }

      bOpenNewWindow = true;
      urlString = createTransferUrl("PermitTypeRMB.page", itemset4.getSelectedRows("PERMIT_TYPE_ID"));
      newWinHandle = "attributes"; 
      // 040114  ARWILK  End  (Enable Multirow RMB actions)
   }

//-----------------------------------------------------------------------------
//-------------------------  OVERVIEWMODE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void getBudgetValues()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      cmd = trans.addCustomCommand("GETPLAN1", "PM_Action_Spare_Part_API.Get_Plan_Inv_Part_Cost");
      cmd.addParameter("PLAN_MAT_COST","");
      cmd.addParameter("ITEM6_PM_NO",itemset6.getValue("PM_NO"));
      cmd.addParameter("ITEM6_PM_REVISION",itemset6.getValue("PM_REVISION"));

      cmd = trans.addCustomCommand("GETPLAN2", "PM_Action_Spare_Part_API.Get_Plan_Ext_Cost");
      cmd.addParameter("PLAN_EXT_COST","");
      cmd.addParameter("ITEM6_PM_NO",itemset6.getValue("PM_NO"));
      cmd.addParameter("ITEM6_PM_REVISION",itemset6.getValue("PM_REVISION"));

      cmd = trans.addCustomCommand("GETPLAN3", "PM_Action_Role_API.Get_Plan_Pers_Revenue__");
      cmd.addParameter("PLAN_PERS_REV","");
      cmd.addParameter("ITEM6_PM_NO",itemset6.getValue("PM_NO"));
      cmd.addParameter("ITEM6_PM_REVISION",itemset6.getValue("PM_REVISION"));


      cmd = trans.addCustomCommand("GETPLAN4", "PM_Action_Spare_Part_API.Get_Plan_Mat_Revenue");
      cmd.addParameter("PLAN_MAT_REV","");
      cmd.addParameter("ITEM6_PM_NO",itemset6.getValue("PM_NO"));
      cmd.addParameter("ITEM6_PM_REVISION",itemset6.getValue("PM_REVISION"));

      cmd = trans.addCustomCommand("GETPLAN5", "PM_Action_Spare_Part_API.Get_Plan_Ext_Revenue");
      cmd.addParameter("PLAN_EXT_REV","");
      cmd.addParameter("ITEM6_PM_NO",itemset6.getValue("PM_NO"));
      cmd.addParameter("ITEM6_PM_REVISION",itemset6.getValue("PM_REVISION"));

      cmd = trans.addCustomFunction("GETPLAN6", "PM_ACTION_TOOL_FACILITY_API.Get_Total_Revenue","PLAN_TOOL_REV");
      cmd.addParameter("ITEM6_PM_NO",itemset6.getValue("PM_NO"));
      cmd.addParameter("ITEM6_PM_REVISION",itemset6.getValue("PM_REVISION"));

      cmd = trans.addCustomFunction("GETPLAN7", "PM_ACTION_TOOL_FACILITY_API.Get_Total_Cost","PLAN_TOOL_COST");
      cmd.addParameter("ITEM6_PM_NO",itemset6.getValue("PM_NO"));
      cmd.addParameter("ITEM6_PM_REVISION",itemset6.getValue("PM_REVISION"));

      // External Cost
      cmd = trans.addCustomFunction("GETPLAN8", "Pm_Action_Plan_Utility_API.Get_Planned_Cost","PLAN_EXP_COST");
      cmd.addParameter("ITEM6_PM_NO",itemset6.getValue("PM_NO"));
      cmd.addParameter("ITEM6_PM_REVISION",itemset6.getValue("PM_REVISION"));
      cmd.addParameter("WORK_ORDER_COST_TYPE","X");

      // External Revenue
      cmd = trans.addCustomFunction("GETPLAN9", "Pm_Action_Plan_Utility_API.Get_Planned_Revenue","PLAN_EXP_REV");
      cmd.addParameter("ITEM6_PM_NO",itemset6.getValue("PM_NO"));
      cmd.addParameter("ITEM6_PM_REVISION",itemset6.getValue("PM_REVISION"));
      cmd.addParameter("WORK_ORDER_COST_TYPE","X");

      // Fixed Price Cost
      cmd = trans.addCustomFunction("GETPLAN10", "Pm_Action_Plan_Utility_API.Get_Planned_Cost","PLAN_FIX_COST");
      cmd.addParameter("ITEM6_PM_NO",itemset6.getValue("PM_NO"));
      cmd.addParameter("ITEM6_PM_REVISION",itemset6.getValue("PM_REVISION"));
      cmd.addParameter("WORK_ORDER_COST_TYPE","F");

      // Fixed Price Revenue
      cmd = trans.addCustomFunction("GETPLAN11", "Pm_Action_Plan_Utility_API.Get_Planned_Revenue","PLAN_FIX_REV");
      cmd.addParameter("ITEM6_PM_NO",itemset6.getValue("PM_NO"));
      cmd.addParameter("ITEM6_PM_REVISION",itemset6.getValue("PM_REVISION"));
      cmd.addParameter("WORK_ORDER_COST_TYPE","F");

      trans=mgr.perform(trans);

      String planMatCost = trans.getValue("GETPLAN1/DATA/PLAN_MAT_COST");
      String planExtCost = trans.getValue("GETPLAN2/DATA/PLAN_EXT_COST");
      String planPersRev = trans.getValue("GETPLAN3/DATA/PLAN_PERS_REV");
      String planToolsRev= trans.getValue("GETPLAN6/DATA/PLAN_TOOL_REV");
      String planToolsCost = trans.getValue("GETPLAN7/DATA/PLAN_TOOL_COST");
      String planExpCost = trans.getValue("GETPLAN8/DATA/PLAN_EXP_COST");
      String planFixCost = trans.getValue("GETPLAN10/DATA/PLAN_FIX_COST");
      planMatRev = trans.getValue("GETPLAN4/DATA/PLAN_MAT_REV");
      planExtRev = trans.getValue("GETPLAN5/DATA/PLAN_EXT_REV");
      planExpRev = trans.getValue("GETPLAN9/DATA/PLAN_EXP_REV");
      planFixRev = trans.getValue("GETPLAN11/DATA/PLAN_FIX_REV");

      row = itemset6.getRow();

      String planPersCost = row.getValue("PLAN_PERS_COST");
      double totPersCost = toDouble(planPersCost)+toDouble(planMatCost)+toDouble(planExtCost)+toDouble(planToolsCost)+toDouble(planExpCost)+toDouble(planFixCost);

      double totPersRev = toDouble(planPersRev)+toDouble(planMatRev)+toDouble(planExtRev)+toDouble(planToolsRev)+toDouble(planExpRev)+toDouble(planFixRev);

      row.setValue("PLAN_MAT_COST",planMatCost);
      row.setValue("PLAN_EXT_COST",planExtCost);
      row.setValue("PLAN_TOOL_COST",planToolsCost);
      row.setValue("PLAN_EXP_COST",planExpCost);
      row.setValue("PLAN_FIX_COST",planFixCost);

      row.setValue("PLAN_PERS_REV",planPersRev);
      row.setValue("PLAN_MAT_REV",planMatRev);
      row.setValue("PLAN_EXT_REV",planExtRev);
      row.setValue("PLAN_TOOL_REV",planToolsRev);
      row.setValue("PLAN_EXP_REV",planExpRev);
      row.setValue("PLAN_FIX_REV",planFixRev);

      row.setNumberValue("TOT_PLAN_COST",totPersCost);
      row.setNumberValue("TOT_PLAN_REV",totPersRev);
      itemset6.setRow(row);
   }

   // 040114  ARWILK  Begin  (Enable Multirow RMB actions)
   private String createTransferUrl(String url, ASPBuffer object)
   {
      ASPManager mgr = getASPManager();

      try
      {
         String pkg = mgr.pack(object,1900 - url.length());
         char sep = url.indexOf('?') > 0 ? '&' : '?';
         urlString = url + sep + "__TRANSFER=" + pkg ;
         return urlString;
      }
      catch (Throwable any)
      {
         return null;
      }
   }
   // 040114  ARWILK  End  (Enable Multirow RMB actions)

   private boolean checkforConnections(int currrowItem)
   {
      ASPManager mgr = getASPManager();

      itemset7.goTo(currrowItem);

      cmd = trans.addCustomCommand("CHECKCONN","Pm_Action_Job_API.Check_Job_Connection");
      cmd.addParameter("ROLES_EXIST","0");
      cmd.addParameter("MATERIAL_EXIST","0");
      cmd.addParameter("TOOL_FACILITY_EXIST","0");
      cmd.addParameter("DOC_EXIST","0");
      cmd.addParameter("STD_JOB_ID",itemset7.getRow().getValue("STD_JOB_ID"));
      cmd.addParameter("STD_JOB_CONTRACT",itemset7.getRow().getValue("STD_JOB_CONTRACT"));
      cmd.addParameter("STD_JOB_REVISION",itemset7.getRow().getValue("STD_JOB_REVISION"));
      cmd.addParameter("PM_NO",headset.getRow().getValue("PM_NO"));
      cmd.addParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));
      cmd.addParameter("JOB_ID",itemset7.getRow().getValue("JOB_ID"));

      trans = mgr.perform(trans);

      String rolesexist = trans.getValue("CHECKCONN/DATA/ROLES_EXIST");
      String matexist = trans.getValue("CHECKCONN/DATA/MATERIAL_EXIST");
      String toolsfacexist = trans.getValue("CHECKCONN/DATA/TOOL_FACILITY_EXIST");
      String docexist = trans.getValue("CHECKCONN/DATA/DOC_EXIST");

      if ("1".equals(rolesexist) || "1".equals(matexist) || "1".equals(toolsfacexist) || "1".equals(docexist))
      {
         return true;
      }
      else
         return false;
   }

   public void setPriceAmount8()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addCustomFunction("GETINVOICETYPE0","Work_Order_Invoice_Type_API.Get_Client_Value(0)","INVOICETYPE");
      cmd = trans.addCustomFunction("GETINVOICETYPE1","Work_Order_Invoice_Type_API.Get_Client_Value(1)","INVOICETYPE");
      cmd = trans.addCustomFunction("GETINVOICETYPE2","Work_Order_Invoice_Type_API.Get_Client_Value(2)","INVOICETYPE");
      cmd = trans.addCustomFunction("GETINVOICETYPE3","Work_Order_Invoice_Type_API.Get_Client_Value(3)","INVOICETYPE");

      trans = mgr.perform(trans);

      String sInvoiceTypeFixedLine  = trans.getValue("GETINVOICETYPE0/DATA/INVOICETYPE");
      String sInvoiceTypeAsReported  = trans.getValue("GETINVOICETYPE1/DATA/INVOICETYPE");
      String sInvoiceTypeMinQty  = trans.getValue("GETINVOICETYPE2/DATA/INVOICETYPE");
      String sInvoiceTypeMaxQty  = trans.getValue("GETINVOICETYPE3/DATA/INVOICETYPE");

      trans.clear();

      ASPTransactionBuffer testbuff = mgr.newASPTransactionBuffer();
      testbuff.addSecurityQuery("SALES_PART");
      mgr.perform(testbuff);
      ASPBuffer sec = testbuff.getSecurityInfo();

      if (sec.itemExists("SALES_PART"))
      {
         trans.clear();
         int n = itemset8.countRows();

         itemset8.first();
         for (int i = 1;i <= n;i++)
         {
            double nPriceAmt = 0;
            String salesPartNo = itemset8.getRow().getValue("CATALOG_NO");

            if (!mgr.isEmpty(salesPartNo))
            {
               //Get the right price
               double nSalesPrice = itemset8.getRow().getNumberValue("SALES_PRICE");
               if (isNaN(nSalesPrice))
                  nSalesPrice = 0;

               if (nSalesPrice == 0)
               {
                  double nSalesPartListPrice = itemset8.getRow().getNumberValue("SALES_PRICE");
                  if (isNaN(nSalesPartListPrice))
                     nSalesPartListPrice = 0;

                  nSalesPrice = nSalesPartListPrice;
               }

               //Calculate on right price
               String invoiceType = itemset8.getRow().getValue("WORK_ORDER_INVOICE_TYPE");

               double nDiscount = itemset8.getRow().getNumberValue("DISCOUNT");
               if (isNaN(nDiscount))
                  nDiscount = 0;

               double nQtyToInvoice = itemset8.getRow().getNumberValue("QTY_TO_INVOICE");
               if (isNaN(nQtyToInvoice))
                  nQtyToInvoice = 0;

               double nPlannedQty = itemset8.getRow().getNumberValue("QUANTITY");
               if (isNaN(nPlannedQty))
                  nPlannedQty = 0;

               if (invoiceType.equals(sInvoiceTypeFixedLine))
                  nPriceAmt = nSalesPrice * nQtyToInvoice;
               else if (invoiceType.equals(sInvoiceTypeAsReported))
                  nPriceAmt = nSalesPrice * nPlannedQty;
               else if (invoiceType.equals(sInvoiceTypeMinQty))
               {
                  if (nPlannedQty > nQtyToInvoice)
                     nPriceAmt = nSalesPrice * nPlannedQty;
                  else
                     nPriceAmt = nSalesPrice * nQtyToInvoice;
               }
               else if (invoiceType.equals(sInvoiceTypeMaxQty))
               {
                  if (nPlannedQty < nQtyToInvoice)
                     nPriceAmt = nSalesPrice * nPlannedQty;
                  else
                     nPriceAmt = nSalesPrice * nQtyToInvoice;
               }

               if (nDiscount != 0)
                  nPriceAmt = nPriceAmt - ( nDiscount / 100 * nPriceAmt );


               if (isNaN(nPriceAmt))
                  nPriceAmt = 0;

               ASPBuffer r = itemset8.getRow();
               r.setNumberValue("ITEM8_PRICE_AMOUNT",nPriceAmt);
               itemset8.setRow(r);
            }
            itemset8.next();
         }
      }
   }

   private double[] GetSalesPartInfo(int nPmNo,String sPmRevision,String sCatalogContract,String sCatalogNo,double nQuantity,double nQtyToInvoice,double nDiscountToUse)
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      trans.addSecurityQuery("Company_Finance_API","Get_Currency_Code");
      trans.addSecurityQuery("Customer_Order_Pricing_API","Get_Valid_Price_List");
      trans.addSecurityQuery("Customer_Order_Pricing_API","Get_Sales_Part_Price_Info");

      trans = mgr.perform(trans);

      ASPBuffer secBuff = trans.getSecurityInfo();

      double nQtyToUse;
      double nSalesPrice = 0;
      double nDiscount = 0;

      if (isNaN(nQtyToInvoice))
         nQtyToUse = nQuantity;
      else
         nQtyToUse = nQtyToInvoice;

      if (secBuff.itemExists("Company_Finance_API.Get_Currency_Code") && secBuff.itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List"))
      {
         trans.clear();

         cmd = trans.addCustomFunction("GETCUSTNO","Pm_Action_API.Get_Customer_No","STRDUMMY");
         cmd.addParameter("ITEM8_PM_NO",new Integer(nPmNo).toString());
         cmd.addParameter("ITEM8_PM_REVISION",sPmRevision);

         cmd = trans.addCustomFunction("GETAGRID","Pm_Action_API.Get_Agreement_Id","STRDUMMY");
         cmd.addParameter("ITEM8_PM_NO",new Integer(nPmNo).toString());
         cmd.addParameter("ITEM8_PM_REVISION",sPmRevision);

         cmd = trans.addCustomFunction("GETORGCONT","Pm_Action_API.Get_Org_Contract","STRDUMMY");
         cmd.addParameter("ITEM8_PM_NO",new Integer(nPmNo).toString());
         cmd.addParameter("ITEM8_PM_REVISION",sPmRevision);

         cmd = trans.addCustomFunction("GETCOMP","Site_API.Get_Company","STRDUMMY");
         cmd.addReference("STRDUMMY","GETORGCONT/DATA");

         cmd = trans.addCustomFunction("GETCURRCODE","Company_Finance_API.Get_Currency_Code","STRDUMMY");
         cmd.addReference("STRDUMMY","GETCOMP/DATA");

         trans = mgr.perform(trans); 

         String sCustomerNo = trans.getValue("GETCUSTNO/DATA/STRDUMMY");
         String sAgreementId = trans.getValue("GETAGRID/DATA/STRDUMMY");
         String sCurrencyCode = trans.getValue("GETCURRCODE/DATA/STRDUMMY");

         sCustomerNo = mgr.isEmpty(sCustomerNo)?"":sCustomerNo;
         sAgreementId = mgr.isEmpty(sAgreementId)?"":sAgreementId;
         sCurrencyCode = mgr.isEmpty(sCurrencyCode)?"":sCurrencyCode;

         trans.clear();

         cmd = trans.addQuery("GETSYSDATE","SELECT SYSDATE DATEDUMMY FROM DUAL");

         cmd = trans.addCustomCommand("FETCHWOSALPRICE","Work_Order_Coding_API.Get_Price_Info");
         cmd.addParameter("DUMMY_BASE_UNIT_PRICE","");
         cmd.addParameter("DUMMY_SALE_UNIT_PRICE","");
         cmd.addParameter("DUMMY_DISCOUNT","");
         cmd.addParameter("DUMMY_CURRENCY_RATE","");
         cmd.addParameter("DUMMY_PRICE_SOURCE","");
         cmd.addParameter("DUMMY_PRICE_SOURCE_ID","");
         cmd.addParameter("ITEM8_CATALOG_CONTRACT",sCatalogContract);
         cmd.addParameter("ITEM8_CATALOG_NO",sCatalogNo);
         cmd.addParameter("STRDUMMY",sCustomerNo);
         cmd.addParameter("STRDUMMY",sAgreementId);
         cmd.addParameter("STRDUMMY","");
         cmd.addParameter("QUANTITY",new Double(nQtyToUse).toString());
         cmd.addParameter("STRDUMMY","");
         cmd.addReference("DATEDUMMY","GETSYSDATE/DATA");
         cmd.addParameter("STRDUMMY","");

         trans = mgr.perform(trans);

         nSalesPrice = trans.getNumberValue("FETCHWOSALPRICE/DATA/DUMMY_BASE_UNIT_PRICE");
         nDiscount = 0;

         if (!isNaN(nDiscount) && nDiscount != 0)
            nDiscountToUse = nDiscount;
      }

      double[] priceVals = new double[2];
      priceVals[0] = isNaN(nSalesPrice)?0:nSalesPrice;
      priceVals[1] = isNaN(nDiscountToUse)?0:nDiscountToUse;

      return priceVals;
   }

   private double GetCost(String sTranslatedWorkOrderCostType,int nPmNo,String sPmRevision,int nPlanLineNo,String sCatalogContract,String sCatalogNo)
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      cmd = trans.addCustomFunction("GETCOSTTYPE","Work_Order_Cost_Type_API.Encode","STRDUMMY");
      cmd.addParameter("WORK_ORDER_COST_TYPE",sTranslatedWorkOrderCostType);

      trans = mgr.perform(trans); 

      String sWorkOrderCostType = trans.getValue("GETCOSTTYPE/DATA/STRDUMMY");

      trans.clear();

      if ("T".equals(sWorkOrderCostType))
      {
         cmd = trans.addCustomFunction("GETCOSTVAL","Pm_Action_Tool_Facility_API.Get_Tool_Cost","COST");
         cmd.addParameter("ITEM8_PM_NO",new Integer(nPmNo).toString());
         cmd.addParameter("ITEM8_PM_REVISION",sPmRevision);
         cmd.addParameter("PLAN_LINE_NO",new Integer(nPlanLineNo).toString());
      }
      else
      {
         cmd = trans.addCustomFunction("GETCOSTVAL","Pm_Action_Planning_API.Get_Cost_By_Planning","COST");
         cmd.addParameter("ITEM8_PM_NO",new Integer(nPmNo).toString());
         cmd.addParameter("ITEM8_PM_REVISION",sPmRevision);
         cmd.addParameter("PLAN_LINE_NO",new Integer(nPlanLineNo).toString());
         cmd.addParameter("ITEM8_CATALOG_CONTRACT",sCatalogContract);
         cmd.addParameter("ITEM8_CATALOG_NO",sCatalogNo);
      }

      trans = mgr.perform(trans);

      double nCost = trans.getNumberValue("GETCOSTVAL/DATA/COST");

      return nCost;
   }

   private double CalcSalesPriceAmount(String sTranslatedWorkOrderInvoiceType,double nDiscount,double nSalesPrice,double nQuantity,double nQtyToInvoice)
   {
      ASPManager mgr = getASPManager();

      double nSalesPriceAmount = 0;
      double nQtyToUse;

      trans.clear();

      cmd = trans.addCustomFunction("GETWOINVTYPE","Work_Order_Invoice_Type_API.Encode","INVOICETYPE");
      cmd.addParameter("ITEM8_WORK_ORDER_INVOICE_TYPE",sTranslatedWorkOrderInvoiceType);

      trans = mgr.perform(trans);

      String sWorkOrderInvoiceType = trans.getValue("GETWOINVTYPE/DATA/INVOICETYPE");

      if ("AR".equals(sWorkOrderInvoiceType))
         nQtyToUse = nQuantity;

      else if ("MINQ".equals(sWorkOrderInvoiceType))
      {
         if (nQuantity > nQtyToInvoice)
            nQtyToUse = nQuantity;
         else
            nQtyToUse = nQtyToInvoice;
      }
      else if ("MAXQ".equals(sWorkOrderInvoiceType))
      {
         if (nQuantity < nQtyToInvoice)
            nQtyToUse = nQuantity;
         else
            nQtyToUse = nQtyToInvoice;
      }
      else if ("FL".equals(sWorkOrderInvoiceType))
         nQtyToUse = nQtyToInvoice;
      else
         nQtyToUse = nQtyToInvoice;


      if (isNaN(nDiscount))
         nSalesPriceAmount = nSalesPrice * nQtyToUse;
      else
      {
         nSalesPriceAmount = nSalesPrice * nQtyToUse;
         nSalesPriceAmount -= nDiscount / 100 * nSalesPriceAmount;
      }

      return nSalesPriceAmount;
   }

   public void additionalQualifications1()
   {
      ASPManager mgr = getASPManager();

      if (itemlay0.isMultirowLayout())
         itemset0.goTo(itemset0.getRowSelected());
      //this is needed when a new STD JOB is created.(createSearchURL is undefined at this instance)
      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      int head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);
      ctx.setGlobal("HEADGLOBAL", s_head_curr);

      bOpenNewWindow = true;
      urlString = "PMQualificationsDlg.page?RES_TYPE=QUALIFICATIONS"+
                  "&PM_NO="+ mgr.URLEncode(mgr.isEmpty(itemset0.getValue("PM_NO"))?"":itemset0.getValue("PM_NO")) +
                  "&PM_REVISION="+ mgr.URLEncode(mgr.isEmpty(itemset0.getValue("PM_REVISION"))?"":itemset0.getValue("PM_REVISION")) +
                  "&ROW_NO="+ mgr.URLEncode(mgr.isEmpty(itemset0.getValue("ROW_NO"))?"":itemset0.getValue("ROW_NO")) +
                  "&QRYSTR=" + mgr.URLEncode(qrystr) +
                  "&FRMNAME=PmAction";
      newWinHandle = "additionalQualifications1"; 
   }

   public void additionalQualifications2()
   {
      ASPManager mgr = getASPManager();

      if (itemlay9.isMultirowLayout())
         itemset9.goTo(itemset9.getRowSelected());
      //this is needed when a new STD JOB is created.(createSearchURL is undefined at this instance)
      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      int head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);
      ctx.setGlobal("HEADGLOBAL", s_head_curr);

      bOpenNewWindow = true;
      urlString = "PMQualificationsDlg.page?RES_TYPE=QUALIFICATIONS"+
                  "&PM_NO="+ mgr.URLEncode(mgr.isEmpty(itemset9.getValue("PM_NO"))?"":itemset9.getValue("PM_NO")) +
                  "&PM_REVISION="+ mgr.URLEncode(mgr.isEmpty(itemset9.getValue("PM_REVISION"))?"":itemset9.getValue("PM_REVISION")) +
                  "&ROW_NO="+ mgr.URLEncode (mgr.isEmpty(itemset9.getValue("ROW_NO"))?"":itemset9.getValue("ROW_NO")) +
                  "&QRYSTR=" + mgr.URLEncode(qrystr) +
                  "&FRMNAME=PmAction";

      newWinHandle = "additionalQualifications2"; 
   }

   public void predecessors1()
   {
      ASPManager mgr = getASPManager();
      int count;
      ASPBuffer data_buff;
      ASPBuffer rowBuff;
      //this is needed when a new STD JOB is created.(createSearchURL is undefined at this instance)
      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      ctx.setGlobal("KEY_NO1", itemset0.getValue("PM_NO"));
      ctx.setGlobal("KEY_NO2", itemset0.getValue("PM_REVISION"));
      ctx.setGlobal("KEY_NO3", "0");
      ctx.setGlobal("STR_LU", "PmActionRole");
      int head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);
      ctx.setGlobal("HEADGLOBAL", s_head_curr);


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

      data_buff = mgr.newASPBuffer();

      for (int i = 0; i < count; i++)
      {
         rowBuff = mgr.newASPBuffer();

         if (i == 0)
         {
            rowBuff.addItem("ROW_NO", itemset0.getValue("ROW_NO"));
            rowBuff.addItem("ROW_NO", itemset0.getValue("ROW_NO"));
         }
         else
         {
            rowBuff.addItem(null, itemset0.getValue("ROW_NO"));
            rowBuff.addItem(null, itemset0.getValue("ROW_NO"));
         }

         data_buff.addBuffer("DATA", rowBuff);

         if (itemlay0.isMultirowLayout())
            itemset0.next();
      }

      if (itemlay0.isMultirowLayout())
         itemset0.setFilterOff();

      bOpenNewWindow = true;
      if ("TRUE".equals(mgr.readValue("REFRESHPARENT1")))
         urlString= createTransferUrl("ConnectPredecessorsDlg.page?&BLOCK=ITEM0", data_buff);
      else
         urlString= createTransferUrl("ConnectPredecessorsDlg.page?&QRYSTR=" + mgr.URLEncode(qrystr) +
                                      "&FRMNAME=PmAction", data_buff);
      newWinHandle = "predecessors1"; 
      newWinHeight = 550;
      newWinWidth  = 650;

   }

   public void predecessors2()
   {
      ASPManager mgr = getASPManager();
      int count;
      ASPBuffer data_buff;
      ASPBuffer rowBuff;
      //this is needed when a new STD JOB is created.(createSearchURL is undefined at this instance)
      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      ctx.setGlobal("KEY_NO1", itemset9.getValue("PM_NO"));
      ctx.setGlobal("KEY_NO2", itemset9.getValue("PM_REVISION"));
      ctx.setGlobal("KEY_NO3", "0");
      ctx.setGlobal("STR_LU", "PmActionRole");
      int head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);
      ctx.setGlobal("HEADGLOBAL", s_head_curr);


      if (itemlay9.isMultirowLayout())
      {
         itemset9.storeSelections();
         itemset9.setFilterOn();
         count = itemset9.countSelectedRows();
      }
      else
      {
         itemset9.unselectRows();
         count = 1;
      }

      data_buff = mgr.newASPBuffer();

      for (int i = 0; i < count; i++)
      {
         rowBuff = mgr.newASPBuffer();

         if (i == 0)
         {
            rowBuff.addItem("ROW_NO", itemset9.getValue("ROW_NO"));
            rowBuff.addItem("ROW_NO", itemset9.getValue("ROW_NO"));
         }
         else
         {
            rowBuff.addItem(null, itemset9.getValue("ROW_NO"));
            rowBuff.addItem(null, itemset9.getValue("ROW_NO"));
         }

         data_buff.addBuffer("DATA", rowBuff);

         if (itemlay9.isMultirowLayout())
            itemset9.next();
      }

      if (itemlay9.isMultirowLayout())
         itemset9.setFilterOff();

      bOpenNewWindow = true;
      if ("TRUE".equals(mgr.readValue("REFRESHPARENT2")))
         urlString= createTransferUrl("ConnectPredecessorsDlg.page?&BLOCK=ITEM9", data_buff);
      else
         urlString= createTransferUrl("ConnectPredecessorsDlg.page?&QRYSTR=" + mgr.URLEncode(qrystr) +
                                      "&FRMNAME=PmAction", data_buff);
      newWinHandle = "predecessors2"; 
      newWinHeight = 550;
      newWinWidth  = 650;

   }


   public void preDefine()
   {
      ASPManager mgr = getASPManager();

      //mgr.beginASPEvent();

      tempblk = mgr.newASPBlock("TEMP");

      tempblk.setView("Dual");
      tempset = tempblk.getASPRowSet();

      headblk = mgr.newASPBlock("HEAD");

      f = headblk.addField("OBJID");
      f.setHidden();

      f = headblk.addField("OBJVERSION");
      f.setHidden();

      f = headblk.addField("ATTR");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("INFO");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("ACTION");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("PM_NO","Number","#");
      f.setSize(20);
      f.setHilite();
      f.setLabel("PCMWPMACTIONPMNO: PM No");
      f.setReadOnly();

      f = headblk.addField("PM_REVISION");
      f.setSize(20);
      f.setHilite();
      f.setLabel("PCMWPMACTIONPMREV: PM Revision");
      f.setMaxLength(6);   
      f.setInsertable();
      f.setReadOnly();

      f = headblk.addField("HEAD_CONTRACT");
      f.setSize(20);
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
      f.setCustomValidation("HEAD_CONTRACT","COMPANY,MCH_CODE_CONTRACT");
      f.setMandatory();
      f.setHilite();
      f.setLabel("PCMWPMACTIONCONTRACT: Site");
      f.setUpperCase();
      f.setDbName("CONTRACT");
      f.setMaxLength(5);

      f = headblk.addField("MCH_CODE");
      f.setSize(16);
      f.setMandatory();
      f.setHilite();
      f.setCustomValidation("HEAD_CONTRACT,MCH_CODE","MCH_CODE,MCH_NAME,MCH_CODE_CONTRACT,GROUP_ID");
      f.setLabel("PCMWPMACTIONMCHCODEID: Object ID");
      f.setUpperCase();
      f.setMaxLength(100);
      f.setHyperlink("WorkOrderQuotationRedirect.page","MCH_CODE,HEAD_CONTRACT","NEWWIN");

      f = headblk.addField("MCH_CODE_CONTRACT");
      f.setHidden();

      f = headblk.addField("MCH_NAME");
      f.setSize(20);
      f.setLabel("PCMWPMACTIONMCHNAME: Description");
      f.setFunction("Equipment_Object_API.Get_Mch_Name(:MCH_CODE_CONTRACT,:MCH_CODE)");
      f.setReadOnly();
      f.setHilite();
      f.setMaxLength(45);

      f = headblk.addField("ACTION_CODE_ID");
      f.setSize(16);
      f.setDynamicLOV("MAINTENANCE_ACTION",600,445);
      f.setMandatory();
      f.setHilite();
      f.setLabel("PCMWPMACTIONACID: Action");
      f.setUpperCase();
      f.setMaxLength(10);

      f = headblk.addField("ACTION_DESCR1");
      f.setSize(20);
      f.setHilite();
      f.setDefaultNotVisible();
      f.setFunction("Maintenance_Action_API.Get_Description(:ACTION_CODE_ID)");
      mgr.getASPField("ACTION_CODE_ID").setValidation("ACTION_DESCR1");
      f.setReadOnly();

      f = headblk.addField("STATE");
      f.setSize(20);
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONSTATE: State");

      f = headblk.addField("OBJSTATE");
      f.setHidden();

      f = headblk.addField("OLD_REVISION");
      f.setFunction("Pm_Action_API.Get_Old_Revision(:PM_NO,:PM_REVISION)");
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONOLDREV: Old Revision");

      f = headblk.addField("COMPANY");
      f.setSize(6);
      f.setMandatory();
      f.setHidden();

      //=========== WO Specific Info ==================

      f = headblk.addField("ORG_CONTRACT");
      f.setSize(10);
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
      f.setMandatory();
      f.setLabel("PCMWPMACTIONWOCONT: WO Site");
      f.setUpperCase();
      f.setInsertable();
      f.setReadOnly();
      f.setMaxLength(5);
      f.setCustomValidation("ORG_CONTRACT,ORG_CODE","CALENDAR_ID,CALENDAR_DESC,ORG_CODE_COMP");

      f = headblk.addField("ORG_CODE");
      f.setSize(16);
      f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ORG_CONTRACT CONTRACT",600,445);
      f.setMandatory();
      f.setHilite();
      f.setLabel("PCMWPMACTIONORGCODE: Maintenance Organization");
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONORCO: List of Maintenance Organization"));
      f.setUpperCase();
      f.setMaxLength(8);
      f.setCustomValidation("ORG_CONTRACT,ORG_CODE","CALENDAR_ID,CALENDAR_DESC"); 


      f = headblk.addField("ORG_CODE_COMP");
      f.setFunction("Site_API.Get_Company(:ORG_CONTRACT)");
      f.setHidden();

      f = headblk.addField("OP_STATUS_ID");
      f.setSize(20);
      f.setHilite();
      f.setDynamicLOV("OPERATIONAL_STATUS",600,445);
      f.setLabel("PCMWPMACTIONOPERSTATUS: Operational Status");
      f.setUpperCase();

      f = headblk.addField("PRIORITY_ID");
      f.setSize(20);
      f.setDynamicLOV("MAINTENANCE_PRIORITY",600,445);
      f.setLabel("PCMWPMACTIONPRID: Priority");
      f.setUpperCase();
      f.setMaxLength(1);
      f.setDefaultNotVisible();

      f = headblk.addField("MAINT_EMP_SIGN");
      f.setSize(20);
      f.setLOV("../mscomw/MaintEmployeeLov.page","ORG_CODE_COMP COMPANY",600,450);
      f.setLabel("PCMWPMACTIONEXECUTEDBY: Executed By");
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONSIGN: List of Signature"));
      f.setUpperCase();
      f.setMaxLength(20);
      f.setDefaultNotVisible();
      f.setCustomValidation("MAINT_EMP_SIGN,COMPANY","MAINT_EMPLOYEE,MAINT_EMP_SIGN");

      f = headblk.addField("MAINT_EMPLOYEE");
      f.setLabel("PCMWPMACTIONSIGID: Signature Id");
      f.setHidden();

      f = headblk.addField("PLAN_HRS","Number");
      f.setSize(20);
      f.setLabel("PCMWPMACTIONPLANHRS: Execution Time");
      f.setDefaultNotVisible();

      f = headblk.addField("WORK_TYPE_ID");
      f.setSize(20);
      f.setDynamicLOV("WORK_TYPE",600,445);
      f.setLabel("PCMWPMACTIONWTID: Work Type");
      f.setUpperCase();
      f.setMaxLength(20);
      f.setDefaultNotVisible();

      f = headblk.addField("SIGNATURE");
      f.setSize(20);
      f.setDynamicLOV("EMPLOYEE_LOV","ORG_CODE_COMP COMPANY",600,445);
      f.setLabel("PCMWPMACTIONPLNBY: Planned By");
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONLSTOFPLANNER: List of Planner"));
      f.setUpperCase();
      f.setMaxLength(20);
      f.setCustomValidation("SIGNATURE,COMPANY","SIGNATURE_ID");
      f.setDefaultNotVisible();

      //================= PM Info =========================

      f = headblk.addField("LAST_CHANGED","Date");
      f.setSize(20);
      f.setLabel("PCMWPMACTIONLCHANGED: Last Modified");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("LATEST_PM","Date");
      f.setSize(16);
      f.setLabel("PCMWPMACTIONLATEPM: Last Performed");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("OVERDUE_PERCENT","Number","#");
      f.setSize(20);
      f.setLabel("PCMWPMACTIONOVRDUEPCNT: Overdue Percent (%)");
      f.setReadOnly();
      f.setFunction("PM_Action_API.Overdue_Percent(:PM_NO,:PM_REVISION)");
      f.setMaxLength(4);
      f.setDefaultNotVisible();

      f = headblk.addField("PM_PERFORMED_DATE_BASED");
      f.setSize(20);
      f.setLabel("PCMWPMACTIONPMPDBASED: Performed Date Based");
      f.setSelectBox();
      f.enumerateValues("PM_PERFORMED_DATE_BASED_API");
      f.unsetSearchOnDbColumn();
      f.setMaxLength(20);
      f.setDefaultNotVisible();

      //=========== Maintenance Plan =====================

      f = headblk.addField("START_VALUE");
      f.setLabel("PCMWPMACTIONSTVALUE: Start Value");
      f.setMaxLength(11);
      f.setSize(12);
      f.setDefaultNotVisible();

      f = headblk.addField("PM_START_UNIT");
      f.setSize(12);
      f.setLabel("PCMWPMACTIONPMSUNIT: Start Unit");
      f.setSelectBox();
      f.enumerateValues("PM_START_UNIT_API");
      f.unsetSearchOnDbColumn();
      f.setDefaultNotVisible();

      f = headblk.addField("START_VALUE_TEMP","Date");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("INTERVAL");
      f.setSize(12);
      f.setLabel("PCMWPMACTIONINTERVAL: Interval");
      f.setMaxLength(3);
      f.setDefaultNotVisible();

      f = headblk.addField("PM_INTERVAL_UNIT");
      f.setSize(15);
      f.setLabel("PCMWPMACTIONPMINTUNIT: Interval Unit");
      f.setSelectBox();
      f.enumerateValues("PM_INTERVAL_UNIT_API");
      f.unsetSearchOnDbColumn();
      f.setDefaultNotVisible();

      f = headblk.addField("PM_GENERATEABLE");
      f.setSize(16);
      f.setLabel("PCMWPMACTIONPMGENE: Generatable");
      f.setSelectBox();
      f.enumerateValues("PM_GENERATEABLE_API");
      f.unsetSearchOnDbColumn();
      f.setDefaultNotVisible();

      //=========Event==========
      f = headblk.addField("CALL_CODE");
      f.setSize(10);
      f.setDynamicLOV("MAINTENANCE_EVENT",600,445);
      f.setLabel("PCMWPMACTIONCALLCODE: Event");
      f.setUpperCase();
      f.setMaxLength(10);
      f.setDefaultNotVisible();

      f = headblk.addField("START_CALL","Number");
      f.setSize(10);
      f.setLabel("PCMWPMACTIONGFSTCALL: Start");
      f.setMaxLength(2);
      f.setDefaultNotVisible();

      f = headblk.addField("CALL_INTERVAL","Number");
      f.setSize(10);
      f.setLabel("PCMWPMACTIONGFCALLINT: Interval");
      f.setMaxLength(2);
      f.setDefaultNotVisible();

      //========= Valid ===================
      f = headblk.addField("VALID_FROM","Date");
      f.setDefaultNotVisible();
      f.setLabel("PCMWPMACTIONVALIDFROM: Valid From");

      f = headblk.addField("VALID_TO","Date");
      f.setDefaultNotVisible();
      f.setLabel("PCMWPMACTIONVALIDTO: Valid To");

      f = headblk.addField("CALENDAR_ID");
      f.setUpperCase();
      f.setLabel("PCMWPMACTIONCALENDAR: Calendar");
      f.setSize(15);
      f.setDefaultNotVisible();
      f.setMaxLength(10);
      f.setDynamicLOV("WORK_TIME_CALENDAR",600,445);
      f.setLOVProperty("WHERE","OBJSTATE = 'Generated'");

      f = headblk.addField("CALENDAR_DESC");
      f.setDefaultNotVisible();
      f.setFunction("Work_Time_Calendar_API.Get_Description(:CALENDAR_ID)");
      f.setSize(20);
      f.setReadOnly();
      mgr.getASPField("CALENDAR_ID").setValidation("CALENDAR_DESC");

      //=========PM Has==========

      f = headblk.addField("OBSOLETE_JOBS");
      f.setSize(10);
      f.setLabel("PCMWPMACTIONOBSOLETEJOBS: Obsolete Jobs");
      f.setFunction("Pm_Action_Job_API.Check_Obsolete_Jobs(:PM_NO,:PM_REVISION)"); 
      f.setCheckBox("FALSE,TRUE");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("CONNECTIONS");
      f.setSize(10);
      f.setLabel("PCMWPMACTIONCONNECTIONS: Connections");
      f.setFunction("Pm_Action_Connection_API.Connections(:PM_NO,:PM_REVISION)"); 
      f.setCheckBox("FALSE,TRUE");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("CRITERIA");
      f.setSize(11);
      f.setLabel("PCMWPMACTIONCRITERIA: Criteria");
      f.setFunction("Pm_Action_Criteria_API.Has_Criteria(:PM_NO,:PM_REVISION)"); 
      f.setCheckBox("FALSE,TRUE");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("ADJUSTMENT");
      f.setSize(11);
      f.setLabel("PCMWPMACTIONADJUSTMENT: Adjustments");
      f.setFunction("Pm_Action_Calendar_Plan_API.Is_Adjusted(:PM_NO,:PM_REVISION)");
      f.setCheckBox("FALSE,TRUE");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("LU_NAME");
      f.setHidden();  
      f.setFunction("'PmAction'");

      f = headblk.addField("KEY_REF");
      f.setHidden();
      f.setFunction("CONCAT('PM_NO=',CONCAT(PM_NO,CONCAT('^PM_REVISION=',CONCAT(PM_REVISION,'^'))))");

      f = headblk.addField("DOCUMENTS");
      f.setSize(11);
      f.setLabel("PCMWPMACTIONDOCUMENTS: Documents");

      if (mgr.isModuleInstalled("DOCMAN"))
         f.setFunction("substr(Doc_Reference_Object_API.Exist_Obj_Reference('PmAction',CONCAT('PM_NO=',CONCAT(PM_NO,CONCAT('^PM_REVISION=',CONCAT(PM_REVISION,'^'))))),1,5)");
      else
         f.setFunction("''");
      f.setCheckBox("FALSE,TRUE");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("PM_NO_KEY_VALUE");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("PM_NO_KEY_VALUE1");
      f.setHidden();
      f.setFunction("PM_NO_KEY_VALUE");

      f = headblk.addField("REPLACEMENTS");
      f.setSize(11);
      f.setLabel("PCMWPMACTIONREPLACEMENT: Replacements");
      f.setFunction("Pm_Action_Replaced_API.Replacements(:PM_NO,:PM_REVISION)");
      f.setCheckBox("FALSE,TRUE");
      f.setReadOnly();
      f.setDefaultNotVisible();

      //=============== Prepare ==================

      f = headblk.addField("TEST_POINT_ID");
      f.setSize(12);
      f.setDynamicLOV("EQUIPMENT_OBJECT_TEST_PNT","HEAD_CONTRACT CONTRACT,MCH_CODE",600,445);
      f.setLabel("PCMWPMACTIONTPID: Testpoint");
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONTEPOID: List of Testpoint"));
      f.setCustomValidation("TEST_POINT_ID,MCH_CODE,HEAD_CONTRACT","MSEQOBJECTTESTPOINTDESCRIPTION,MSEQOBJECTTESTPOINTLOCATION");
      f.setUpperCase();
      f.setMaxLength(6);
      f.setDefaultNotVisible();

      f = headblk.addField("MSEQOBJECTTESTPOINTDESCRIPTION");
      f.setSize(22);
      f.setFunction("EQUIPMENT_OBJECT_TEST_PNT_API.Get_Description(:HEAD_CONTRACT,:MCH_CODE,:TEST_POINT_ID)");
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONTESTDESC: Testpoint Description");
      f.setDefaultNotVisible();

      f = headblk.addField("MSEQOBJECTTESTPOINTLOCATION");
      f.setSize(23);
      f.setLabel("PCMWPMACTIONTESTPNTLOCATION: Location");
      f.setFunction("EQUIPMENT_OBJECT_TEST_PNT_API.Get_Location(:HEAD_CONTRACT,:MCH_CODE,:TEST_POINT_ID)");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("DESCRIPTION");
      f.setSize(70);
      f.setLabel("PCMWPMACTIONDESCRIPTION: Work Description");
      f.setDefaultNotVisible();
      f.setHeight(3);

      f = headblk.addField("VENDOR_NO");
      f.setSize(12);
      f.setDynamicLOV("SUPPLIER_INFO",600,445);
      f.setLabel("PCMWPMACTIONVENDNO: Contractor");
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONVENO: List of Contractor"));
      f.setUpperCase();
      f.setDefaultNotVisible();

      f = headblk.addField("VENDORNAME");
      f.setSize(50);
      f.setLabel("PCMWPMACTIONVENDNAME: Contractor Name");
      f.setFunction("SUPPLIER_INFO_API.Get_Name(:VENDOR_NO)");
      mgr.getASPField("VENDOR_NO").setValidation("VENDORNAME");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("MATERIALS");
      f.setSize(70);
      f.setLabel("PCMWPMACTIONMATERIALS: Material");
      f.setMaxLength(2000);
      f.setDefaultNotVisible();

      f = headblk.addField("TESTNUMBER");
      f.setSize(20);
      f.setLabel("PCMWPMACTIONTESTNUMBER: Test Number");
      f.setMaxLength(15);
      f.setDefaultNotVisible();

      f = headblk.addField("ALTERNATE_DESIGNATION");
      f.setSize(70);
      f.setLabel("PCMWPMACTIONALTDESIG: Alternate Designation");
      f.setMaxLength(2000);
      f.setDefaultNotVisible();

      f = headblk.addField("CURRENTPOSITION");
      f.setSize(71);
      f.setLabel("PCMWPMACTIONCURRPOS: Latest Transaction");
      f.setFunction("PART_SERIAL_CATALOG_API.Get_Alt_Latest_Transaction(:HEAD_CONTRACT,:MCH_CODE)");
      f.setReadOnly();
      f.setDefaultNotVisible();

      //================== Customer ===============

      f = headblk.addField("CUSTOMER_NO");  
      f.setSize(20);
      f.setDynamicLOV("CUSTOMER_INFO",600,445);
      f.setCustomValidation("CUSTOMER_NO,COMPANY","CUSTOMERNAME,ISCREDITSTOPPED");
      f.setLabel("PCMWPMACTIONCUSTOMERNO: Customer");
      f.setUpperCase();
      f.setMaxLength(20);
      f.setDefaultNotVisible();

      f = headblk.addField("CUSTOMERNAME");
      f.setFunction("CUSTOMER_INFO_API.Get_Name(:CUSTOMER_NO)");
      f.setSize(40);
      f.setLabel("PCMWPMACTIONCUSTOMERNAME: Customer Name");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("CONTRACT_ID");                     
      f.setLOV("PscContrProductLov.page","HEAD_CONTRACT,CUSTOMER_NO,MCH_CODE");
      f.setCustomValidation("CONTRACT_ID,LINE_NO","CONTRACT_ID,CONTRACT_NAME,LINE_NO,LINE_DESC,CONTRACT_TYPE,INVOICE_TYPE,CUSTOMER_NO,CUSTOMERNAME");
      f.setUpperCase();
      f.setMaxLength(15);
      f.setDefaultNotVisible();
      f.setLabel("PCMWPMACTIONCONTRACTID: Contract ID");
      f.setSize(15);

      f = headblk.addField("CONTRACT_NAME");                     
      f.setDefaultNotVisible();
      //Bug 84436, Start
      if (mgr.isModuleInstalled("SRVCON"))
         f.setFunction("SC_SERVICE_CONTRACT_API.Get_Contract_Name(:CONTRACT_ID)");
      else
            f.setFunction("''");
      //Bug 84436, End 
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONCONTRACTNAME: Contract Name");
      f.setSize(15);

      f = headblk.addField("LINE_NO");
      f.setLOV("PscContrProductLov.page","HEAD_CONTRACT,CUSTOMER_NO,MCH_CODE,CONTRACT_ID");
      f.setCustomValidation("CONTRACT_ID,LINE_NO","CONTRACT_ID,CONTRACT_NAME,LINE_NO,LINE_DESC,CONTRACT_TYPE,INVOICE_TYPE");
      f.setDefaultNotVisible();
      f.setLabel("PCMWPMACTIONLINENO: Line No");
      f.setSize(10); 

      f = headblk.addField("LINE_DESC");                     
      f.setDefaultNotVisible();
      //Bug 84436, Start
      if (mgr.isModuleInstalled("PCMSCI"))
         f.setFunction("PSC_CONTR_PRODUCT_API.Get_Description(:CONTRACT_ID,:LINE_NO)");
       else
            f.setFunction("''");   
      //Bug 84436, End
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONLINEDESC: Description");
      f.setSize(15);

      f = headblk.addField("CONTRACT_TYPE");                     
      f.setDefaultNotVisible();
       //Bug 84436, Start
      if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("SC_SERVICE_CONTRACT_API.Get_Contract_Type(:CONTRACT_ID)");
      else
            f.setFunction("''");
      //Bug 84436, End 
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONCONTRACTTYPE: Contract Type");
      f.setSize(15);

      f = headblk.addField("INVOICE_TYPE");                     
      f.setDefaultNotVisible();
       //Bug 84436, Start
      if (mgr.isModuleInstalled("PCMSCI"))
            f.setFunction("PSC_CONTR_PRODUCT_API.Get_Invoice_Type(:CONTRACT_ID,:LINE_NO)");
      else
            f.setFunction("''");
      //Bug 84436, End 
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONINVOICETYPE: Invoice Type");
      f.setSize(15);

      f = headblk.addField("AGREEMENT_ID"); 
      f.setSize(10);
      f.setDynamicLOV("CUSTOMER_AGREEMENT",600,445);
      f.setLabel("PCMWPMACTIONAGREEMENTID: Agreement");
      f.setUpperCase();
      f.setHidden();
      f.setMaxLength(20);
      f.setDefaultNotVisible();

      f = headblk.addField("CURRENCY_CODE");
      f.setDynamicLOV("ISO_CURRENCY",600,445);
      f.setSize(10);
      f.setLabel("PCMWPMACTIONCURRENCYCODE: Currency");
      f.setMaxLength(3);
      f.setHidden();

      //Bug 82435, start
      f = headblk.addField("REASON");                     
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONREASON: Created Reason");
      f.setSize(15);

      f = headblk.addField("DONE_BY");                     
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONDONEBY: Created By");
      f.setSize(15);

      f = headblk.addField("REV_CRE_DATE", "Datetime");                     
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONREVCREDATE: Created Date");
      f.setSize(15);
      //Bug 82435, end

      f = headblk.addField("START_DATE","Date");
      f.setHidden();

      f = headblk.addField("INDEX1");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("ITEM2_TEMP");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("ITEM3_TEMP");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("CLIENT_START");
      f.setFunction("Pm_Start_Unit_API.Get_Client_Value(0)");
      f.setHidden();

      f = headblk.addField("CLIENT_START1");
      f.setFunction("Pm_Start_Unit_API.Get_Client_Value(1)");
      f.setHidden();

      f = headblk.addField("CLIENT_START2");
      f.setFunction("Pm_Start_Unit_API.Get_Client_Value(2)");
      f.setHidden();

      f = headblk.addField("ISCREDITSTOPPED");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("GROUP_ID");
      f.setSize(8);
      f.setLabel("PCMWPMACTIONGROUPID: Group");
      f.setFunction("EQUIPMENT_OBJECT_API.Get_Group_Id(:HEAD_CONTRACT,:MCH_CODE)");
      f.setReadOnly();
      f.setHidden();

      f = headblk.addField("DOCUMENT");
      if (mgr.isModuleInstalled("DOCMAN"))
         f.setFunction("substr(Doc_Reference_Object_API.Exist_Obj_Reference('PmAction',CONCAT('PM_NO=',CONCAT(PM_NO,CONCAT('PM_REVISION=',CONCAT(PM_REVISION,chr(31)))))),1,5)");
      else
         f.setFunction("''");
      f.setUpperCase();
      f.setLabel("PCMWPMACTIONDOCUMENT: Has Documents");
      f.setReadOnly();
      f.setCheckBox("FALSE,TRUE");
      f.setSize(18);
      f.setHidden();

      f = headblk.addField("PM_TYPE");
      f.setSize(11);
      f.setMandatory();
      f.setHidden();

      f = headblk.addField("SIGNATURE_ID");
      f.setSize(6);
      f.setHidden();
      f.setUpperCase();   

      f = headblk.addField("DEFAULT_CONTRACT");
      f.setHidden();  
      f.setFunction("''");

      f = headblk.addField("ISGENTYPE");
      f.setHidden();  
      f.setFunction("''");

      // 040114  ARWILK  Begin  (Enable Multirow RMB actions)
      f = headblk.addField("GENERATE");
      f.setHidden();  
      f.setFunction("Pm_Action_API.Is_Generateable__(:PM_NO,:PM_REVISION)");

      f = headblk.addField("CONECTED");
      f.setHidden();  
      f.setFunction("Pm_Action_Connection_API.Is_Connected(:PM_NO,:PM_REVISION)");
      // 040114  ARWILK  End  (Enable Multirow RMB actions)

      f = headblk.addField("PM_IS_VALID"); 
      f.setHidden();  
      f.setFunction("Pm_Action_API.Is_Valid_Pm(:PM_NO,:PM_REVISION)");

      f = headblk.addField("OLD_EMP"); 
      f.setHidden();  
      f.setFunction("Pm_Action_API.Get_Maint_Employee(:PM_NO,:PM_REVISION)");

      f = headblk.addField("JOB_OPER_PLAN_EXIST"); 
      f.setHidden();  
      f.setFunction("Pm_Action_Calendar_Job_API.Job_Oper_Plan_Exist(:PM_NO,:PM_REVISION)");

      f = headblk.addField("SIGN_EXIST"); 
      f.setHidden();  
      f.setFunction("Pm_Action_Calendar_Job_API.Signature_Exist(:PM_NO,:PM_REVISION,:MAINT_EMPLOYEE)");

      f = headblk.addField("PM_PLAN_OPERATION"); 
      f.setHidden();  
      f.setFunction("Maintenance_Configuration_API.Get_Pm_Plan_Operation");

      f = headblk.addField("PM_CAN_BE_MODIFIED"); 
      f.setHidden();  
      f.setFunction("Pm_Action_API.Check_New_Row(:PM_NO,:PM_REVISION)");

      f = headblk.addField("CAN_REPLACE"); 
      f.setHidden();  
      f.setFunction("Pm_Action_API.Can_Add_Replacements(:PM_NO,:PM_REVISION)");

      //Bug 89752, Start
      f = headblk.addField("ACTIVE_REV"); 
      f.setHidden();  
      f.setFunction("''");
      //Bug 89752, End

      headblk.setView("PM_ACTION");
      headblk.defineCommand("PM_ACTION_API","New__,Modify__,Remove__,ACTIVE__,OBSOLETE__");
      headblk.disableDocMan();
      headset = headblk.getASPRowSet();

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("PCMWPMACTIONHD: Separate PM Action"));
      headtbl.setWrap();
      // 040114  ARWILK  Begin  (Enable Multirow RMB actions)
      headtbl.enableRowSelect();
      // 040114  ARWILK  End  (Enable Multirow RMB actions)

      headbar = mgr.newASPCommandBar(headblk);

      headbar.defineCommand(headbar.SAVERETURN ,"saveReturn","checkMaintPlan()");
      headbar.defineCommand(headbar.SAVENEW ,"saveNew","checkHeadFields(-1)");

      headbar.addCustomCommand("createNewRevision",mgr.translate("PCMWPMACTIONCRENEWREVISION: Create New Revision..."));
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand(            "connections",mgr.translate(            "PCMWPMACTIONCDNH1: Connections..."));
      headbar.addCustomCommand("replacements",mgr.translate("PCMWPMACTIONREPL3: Replacements..."));
      headbar.addCustomCommand("generateWorkOrder",mgr.translate("PCMWPMACTIONCDNH2: Generate Work Order"));
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("copyPmActions",mgr.translate("PCMWPMACTIONREPL4: Copy..."));
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("activatePm",mgr.translate("PCMWPMACTIONACTIVE: Active"));
      headbar.addCustomCommand("obsoletePm",mgr.translate("PCMWPMACTIONOBSOLETE: Obsolete"));
      if (mgr.isPresentationObjectInstalled("equipw/EquipmentObjectAddress1.page"))
      {
         headbar.addCustomCommandSeparator();
         headbar.addCustomCommand("schonobjaddr",mgr.translate("PCMWPMACTIONSERONOBJADD: Search on Object Address..."));
      }

      // 040114  ARWILK  Begin  (Enable Multirow RMB actions)
      headbar.addCommandValidConditions("connections",          "REPLACEMENTS",   "Enable",   "FALSE");
      headbar.addCommandValidConditions("replacements",         "CONNECTIONS",    "Enable",   "FALSE");
      //Bug 67948, Start
      headbar.appendCommandValidConditions("replacements", "OBJSTATE",    "Disable", "Obsolete");
      //Bug 67948, End

      headbar.addCommandValidConditions("generateWorkOrder",    "GENERATE",       "Enable",  "TRUE");
      headbar.appendCommandValidConditions("generateWorkOrder", "CONECTED",       "Enable",  "FALSE");
      headbar.appendCommandValidConditions("generateWorkOrder", "OBJSTATE",       "Enable", "Active");
      headbar.appendCommandValidConditions("generateWorkOrder", "PM_IS_VALID",    "Disable", "FALSE");

      headbar.addCommandValidConditions("activatePm",  "OBJSTATE", "Enable", "Preliminary");
      headbar.addCommandValidConditions("obsoletePm","OBJSTATE", "Enable", "Active");

      headbar.enableMultirowAction();
      headbar.removeFromMultirowAction("copyPmActions");
      headbar.removeFromMultirowAction("activatePm");
      if (mgr.isPresentationObjectInstalled("equipw/EquipmentObjectAddress1.page"))
         headbar.removeFromMultirowAction("schonobjaddr");
      // 040114  ARWILK  End  (Enable Multirow RMB actions)

      // 031211  ARWILK  Begin  (Replace blocks with tabs)
      headbar.addCustomCommand("activateBudget","");
      headbar.addCustomCommand("activateJobs","");
      headbar.addCustomCommand("activatePlanning","");
      headbar.addCustomCommand("activateOperations","");
      headbar.addCustomCommand("activateMaterials","");
      headbar.addCustomCommand("activateToolsAndFacilities","");
      headbar.addCustomCommand("activateMaintenancePlan","");
      headbar.addCustomCommand("activateCriteria","");
      headbar.addCustomCommand("activatePermits","");
      // 031211  ARWILK  End  (Replace blocks with tabs)

      headbar.addCustomCommandGroup("PMSTATUS", mgr.translate("PCMWPMACTIONPMSTATUS: PM Action Status"));
      headbar.setCustomCommandGroup("activatePm", "PMSTATUS");
      headbar.setCustomCommandGroup("obsoletePm", "PMSTATUS");

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

      headlay.defineGroup("","PM_NO,PM_REVISION,HEAD_CONTRACT,MCH_CODE,MCH_NAME,ACTION_CODE_ID,ACTION_DESCR1,STATE,OLD_REVISION",false,true);   
      headlay.defineGroup(mgr.translate("PCMWPMACTIONGRPWOSPECINFO: WO Specific Info"),"ORG_CONTRACT,ORG_CODE,OP_STATUS_ID,PRIORITY_ID,MAINT_EMP_SIGN,MAINT_EMPLOYEE,PLAN_HRS,WORK_TYPE_ID,SIGNATURE",true,true);   
      headlay.defineGroup(mgr.translate("PCMWPMACTIONGRPMINFO: PM Info"),"LAST_CHANGED,LATEST_PM,OVERDUE_PERCENT,PM_PERFORMED_DATE_BASED",true,true);
      headlay.defineGroup(mgr.translate("PCMWPMACTIONGRPMAINTPLAN: Maintenance Plan"),"START_VALUE,PM_START_UNIT,INTERVAL,PM_INTERVAL_UNIT,PM_GENERATEABLE",true,true);   
      headlay.defineGroup(mgr.translate("PCMWPMACTIONGRPEVENT: Event"),"CALL_CODE,START_CALL,CALL_INTERVAL",true,true);
      headlay.defineGroup(mgr.translate("PCMWPMACTIONGRPVALID: Valid"),"VALID_FROM,VALID_TO,CALENDAR_ID,CALENDAR_DESC",true,true);
      headlay.defineGroup(mgr.translate("PCMWPMACTIONGRPPMHAS: PM Has"),"OBSOLETE_JOBS,CONNECTIONS,CRITERIA,ADJUSTMENT,DOCUMENTS,REPLACEMENTS",true,true);   
      headlay.defineGroup(mgr.translate("PCMWPMACTIONGRPPREPARE: Prepare"),"TEST_POINT_ID,MSEQOBJECTTESTPOINTDESCRIPTION,MSEQOBJECTTESTPOINTLOCATION,DESCRIPTION,VENDOR_NO,VENDORNAME,MATERIALS,TESTNUMBER,ALTERNATE_DESIGNATION,CURRENTPOSITION",true,false);   
      headlay.defineGroup(mgr.translate("PCMWPMACTIONGRPREVINFO: Revision Info"),"REASON,DONE_BY,REV_CRE_DATE",true,false);   
      headlay.defineGroup(mgr.translate("PCMWPMACTIONGRPCUSTOMER: Customer Information"),"CUSTOMER_NO,CUSTOMERNAME,CONTRACT_ID,CONTRACT_NAME,LINE_NO,LINE_DESC,CONTRACT_TYPE,INVOICE_TYPE,CURRENCY_CODE",true,false);   

      headlay.setSimple("MCH_NAME");
      headlay.setSimple("ACTION_DESCR1");
      headlay.setSimple("VENDORNAME");
      headlay.setSimple("MSEQOBJECTTESTPOINTDESCRIPTION");
      headlay.setSimple("MSEQOBJECTTESTPOINTLOCATION");
      headlay.setSimple("CALENDAR_DESC");
      headlay.setSimple("CUSTOMERNAME");
      headlay.setSimple("CONTRACT_NAME");
      headlay.setSimple("LINE_DESC");

      headlay.setFieldSpan("DESCRIPTION",1,3);
      headlay.setFieldSpan("PM_GENERATEABLE",1,3);
      headlay.setFieldSpan("VENDOR_NO",1,3);
      headlay.setFieldSpan("TEST_POINT_ID",1,3);
      headlay.setFieldSpan("MATERIALS",1,3);
      headlay.setFieldSpan("CURRENTPOSITION",1,3);
      headlay.setFieldSpan("ALTERNATE_DESIGNATION",1,3);

      // 031211  ARWILK  Begin  (Replace blocks with tabs)
      tabs = mgr.newASPTabContainer();
      tabs.addTab(mgr.translate("PCMWPMACTIONBUDGETTAB: Budget"),  "javascript:commandSet('HEAD.activateBudget', '')");
      tabs.addTab(mgr.translate("PCMWPMACTIONJOBSTAB: Jobs"),  "javascript:commandSet('HEAD.activateJobs', '')");
      tabs.addTab(mgr.translate("PCMWPMACTIONPLANNINGTAB: Planning"),  "javascript:commandSet('HEAD.activatePlanning', '')");
      tabs.addTab(mgr.translate("PCMWPMACTIONOPERATIONSTAB: Operations"),  "javascript:commandSet('HEAD.activateOperations', '')");
      tabs.addTab(mgr.translate("PCMWPMACTIONMATERIALSTAB: Materials"),  "javascript:commandSet('HEAD.activateMaterials', '')");
      tabs.addTab(mgr.translate("PCMWPMACTIONTOOLFACTAB: Tools and Facilities"),  "javascript:commandSet('HEAD.activateToolsAndFacilities', '')");
      tabs.addTab(mgr.translate("PCMWPMACTIONMAINTPLTAB: Maintenance Plan"),  "javascript:commandSet('HEAD.activateMaintenancePlan', '')");
      tabs.addTab(mgr.translate("PCMWPMACTIONCRITERIATAB: Criteria"),  "javascript:commandSet('HEAD.activateCriteria', '')");
      tabs.addTab(mgr.translate("PCMWPMACTIONPERMITSTAB: Permits"),  "javascript:commandSet('HEAD.activatePermits', '')");
      // 031211  ARWILK  End  (Replace blocks with tabs)

      //=======================================================================================
      // Craft
      //=======================================================================================

      itemblk0 = mgr.newASPBlock("ITEM0");

      f = itemblk0 .addField("ITEM0_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk0 .addField("ITEM0_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk0.addField("ITEM0_PM_NO","Number","#");
      f.setSize(8);
      f.setMandatory();
      f.setHidden();
      f.setDbName("PM_NO");

      f = itemblk0.addField("ITEM0_PM_REVISION");
      f.setSize(8);
      f.setMandatory();
      f.setHidden();
      f.setDbName("PM_REVISION");

      f = itemblk0.addField("ROW_NO","Number","#");
      f.setSize(8);
      f.setLabel("PCMWPMACTIONROWNO: Operation No");
      f.setReadOnly();
      f.setInsertable();

      f = itemblk0.addField("ITEM0_DESCRIPTION");
      f.setSize(25);
      f.setLabel("PCMWPMACTIONITEM0DESCRIPTION: Description");
      f.setDbName("DESCRIPTION");
      f.setMaxLength(60);

      f = itemblk0.addField("ITEM0_COMPANY");
      f.setDbName("COMPANY");
      f.setHidden();

      f = itemblk0.addField("ITEM0_SIGNATURE");
      f.setDbName("SIGNATURE");
      f.setSize(20);
      f.setLOV("../mscomw/MaintEmployeeLov.page","ITEM0_COMPANY COMPANY",600,450);
      f.setCustomValidation("ITEM0_COMPANY,ITEM0_SIGNATURE,ITEM0_ORG_CONTRACT,ROLE_CODE,ITEM0_ORG_CODE,ITEM0_CONTRACT,ITEM0_CATALOG_NO,SALESPARTDESCRIPTION,LISTPRICE","ITEM0_EMPLOYEE_ID,ROLE_CODE,ITEM0_ORG_CODE,ITEM0_SIGNATURE,ITEM0_ORG_CONTRACT,ITEM0_CATALOG_NO,SALESPARTDESCRIPTION,LISTPRICE,CATALOG_COST");
      f.setLOVProperty("TITLE","PCMWPMACTIONLOVTITLE: List of Employee");
      f.setLabel("PCMWPMACTIONEXECUTEDBY: Executed By");
      f.setUpperCase();
      f.setMaxLength(20);

      f = itemblk0.addField("ITEM0_EMPLOYEE_ID");
      f.setSize(25);
      f.setUpperCase();
      f.setLabel("PCMWPMACTIONITEM0EMPID: Employee Id");
      f.setDbName("EMPLOYEE_ID");
      f.setReadOnly();

      f = itemblk0.addField("ITEM0_JOB_ID","Number","#");
      f.setDbName("JOB_ID");
      f.setDynamicLOV("PM_ACTION_JOB","PM_NO,PM_REVISION",600,445);
      f.setLabel("PCMWPMACTIONITEM5JOBID0: Job ID");
      f.setSize(8);
      f.setCustomValidation("ITEM0_COMPANY,ITEM0_PM_NO,ITEM0_PM_REVISION,ITEM0_JOB_ID","ITEM0_EMPLOYEE_ID,ITEM0_SIGNATURE,ROLE_CODE,ITEM0_ORG_CODE");
      f.setInsertable();

      f = itemblk0.addField("ITEM0_ORG_CODE");
      f.setSize(12);
      f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ITEM0_ORG_CONTRACT CONTRACT",600,445);
      f.setLabel("PCMWPMACTIONITORGCODE: Maintenance Organization");
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONORCO134: List of Maintenance Organization"));
      f.setCustomValidation("ITEM0_ORG_CODE,ITEM0_ORG_CONTRACT,ITEM0_CATALOG_NO,ITEM0_CONTRACT,ITEM0_SIGNATURE,ROLE_CODE,ITEM0_CONTRACT","LISTPRICE,ITEM0_CATALOG_NO,SALESPARTDESCRIPTION,ITEM0_ORG_CODE,ITEM0_ORG_CONTRACT,CATALOG_COST");
      f.setUpperCase();
      f.setDbName("ORG_CODE");
      f.setMaxLength(8);

      f = itemblk0.addField("ITEM0_ORG_CONTRACT");
      f.setSize(8);
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
      f.setLabel("PCMWPMACTIONITORGCONT: Maint. Org. Site");
      f.setUpperCase();
      f.setDbName("ORG_CONTRACT");
      f.setMaxLength(5);

      f = itemblk0.addField("ROLE_CODE");
      f.setSize(8);
      f.setDynamicLOV("ROLE_TO_SITE_LOV","ITEM0_CONTRACT CONTRACT",600,445);
      f.setLabel("PCMWPMACTIONITROLECODE: Craft");
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONROCO: List of Craft"));
      f.setCustomValidation("ROLE_CODE,ITEM0_CATALOG_NO,ITEM0_CONTRACT,ITEM0_SIGNATURE,ITEM0_ORG_CONTRACT,ITEM0_ORG_CODE","LISTPRICE,ITEM0_CATALOG_NO,ITEM0_ORG_CONTRACT,SALESPARTDESCRIPTION,ROLE_CODE,CATALOG_COST");
      f.setUpperCase();
      f.setMaxLength(10);

      f = itemblk0.addField("PLAN_MEN","Number");
      f.setSize(13);
      f.setLabel("PCMWPMACTIONPLANMEN: Planned Men");

      f = itemblk0.addField("ITEM0_PLAN_HRS","Number");
      f.setSize(13);
      f.setLabel("PCMWPMACTIONITPLANHRS: Planned Hours");
      f.setDbName("PLAN_HRS");

      f = itemblk0.addField("TEAM_CONTRACT");
      f.setSize(7);
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
      f.setLabel("PCMWPMACTIONITEM0CONT: Team Site");
      f.setMaxLength(5);
      f.setInsertable();
      f.setUpperCase();

      f = itemblk0.addField("TEAM_ID");
      f.setCustomValidation("TEAM_ID,TEAM_CONTRACT","TEAMDESC");
      f.setSize(13);
      f.setDynamicLOV("MAINT_TEAM","TEAM_CONTRACT",600,450);
      f.setMaxLength(20);
      f.setInsertable();
      f.setLabel("PCMWPMACTIONITEM0TID: Team ID");
      f.setUpperCase();

      f = itemblk0.addField("TEAMDESC");
      f.setFunction("Maint_Team_API.Get_Description(:TEAM_ID,:TEAM_CONTRACT)");
      f.setSize(40);
      f.setMaxLength(200);
      f.setLabel("PCMWPMACTIONITEM0DESC: Description");
      f.setReadOnly();

      f = itemblk0.addField("REMARK");
      f.setSize(40);
      f.setLabel("PCMWPMACTIONREMARK: Remark");
      f.setDefaultNotVisible();
      f.setMaxLength(2000);
      f.setHeight(4);


      f = itemblk0.addField("ITEM0_CONTRACT");
      f.setSize(17);
      f.setLabel("PCMWPMACTIONCONTNEW: Sales Part Site");
      f.setDbName("CONTRACT");
      f.setMaxLength(5);
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONROCOL: List of Sales Part Site"));
      f.setUpperCase();
      f.setReadOnly();

      f = itemblk0.addField("ITEM0_CATALOG_NO");
      f.setSize(12);
      f.setLabel("PCMWPMACTIONITEM0CATALOGNO: Sales Part");
      f.setDbName("CATALOG_NO");
      f.setMaxLength(25);
      f.setDynamicLOV("SALES_PART","ITEM0_CONTRACT CONTRACT",600,445);
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONROCXCX: List of Sales Part"));
      f.setUpperCase();
      f.setCustomValidation("ITEM0_CATALOG_NO,ITEM0_CONTRACT,ROLE_CODE,ITEM0_ORG_CONTRACT,ITEM0_ORG_CODE","SALESPARTDESCRIPTION,LISTPRICE,CATALOG_COST");

      f = itemblk0.addField("SALESPARTDESCRIPTION");
      f.setSize(25);
      f.setLabel("PCMWPMACTIONSALESPART1: Sales Part Description ");
      if (mgr.isModuleInstalled("ORDER"))
         f.setFunction("SALES_PART_API.Get_Catalog_Desc(:ITEM0_CONTRACT,:ITEM0_CATALOG_NO)");
      else
         f.setFunction("''");
      f.setReadOnly();

      f = itemblk0.addField("CATALOG_COST");
      f.setReadOnly();
      f.setFunction("Work_Order_Planning_Util_API.Get_Cost(:ITEM0_ORG_CODE,:ROLE_CODE,:ITEM0_CONTRACT,:ITEM0_CATALOG_NO,:ITEM0_ORG_CONTRACT)");
      f.setLabel("PCMWPMACTIONCATALOGCOST: Cost ");

      f = itemblk0.addField("LISTPRICE");
      f.setSize(11);
      f.setLabel("PCMWPMACTIONLISTPRICE: List Price");
      if (mgr.isModuleInstalled("ORDER"))
         f.setFunction("SALES_PART_API.Get_List_Price(:ITEM0_CONTRACT,:ITEM0_CATALOG_NO)");
      else
         f.setFunction("''");
      f.setReadOnly();

      f=itemblk0.addField("PREDECESSOR");
      f.setSize(22);
      f.setLabel("PCMWACTIVESEPARATE2PRED: Predecessors");
      f.setLOV("ConnectPredecessorsDlg.page",600,450);
      f.setFunction("Pm_Role_Dependencies_API.Get_Predecessors(:ITEM0_PM_NO,:ITEM0_PM_REVISION,:ROW_NO)");

      f = itemblk0.addField("TOOLS");
      f.setSize(8);
      f.setLabel("PCMWPMACTIONTOOLS: Tools");
      f.setMaxLength(10); 

      itemblk0.addField("ITEM0_CBADDQUALIFICATION").
      setLabel("PCMWPMACTIONITEM0CBADDQUAL0: Additional Qualifications").
      setFunction("Pm_Action_Role_API.Check_Qualifications_Exist(:ITEM0_PM_NO,:ITEM0_PM_REVISION,:ROW_NO)").
      setCheckBox("0,1").
      setReadOnly().
      setQueryable();

      itemblk0 .setView("PM_ACTION_ROLE");
      itemblk0.defineCommand("PM_ACTION_ROLE_API","New__,Modify__,Remove__");
      itemblk0.setMasterBlock(headblk);

      itemset0 = itemblk0.getASPRowSet();

      itembar0= mgr.newASPCommandBar(itemblk0 );
      itembar0.defineCommand(itembar0.SAVERETURN,null,"checkItem0Fields(-1)");
      itembar0.defineCommand(itembar0.SAVENEW,null,"checkItem0Fields(-1)");

      itemtbl0 = mgr.newASPTable(itemblk0 );
      itemtbl0.setWrap();
      itemtbl0.enableRowSelect();
      itembar0.enableMultirowAction();

      itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");
      itembar0.enableCommand(itembar0.FIND);

      itembar0.addCustomCommand("additionalQualifications1",mgr.translate("PCMWPMACTIONADDQUALIFICATIONS1: Additional Qualifications..."));
      itembar0.addCustomCommand("predecessors1",mgr.translate("PCMWPMACTIONPREDECESSORS1: Predecessors..."));
      itembar0.removeFromMultirowAction("additionalQualifications1");

      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDialogColumns(2);
      itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);


      //========================================================================================
      // Materials
      //==============================================================================

      itemblk1= mgr.newASPBlock("ITEM1");

      f = itemblk1.addField("ITEM1_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk1.addField("ITEM1_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk1.addField("PART_NO");
      f.setSize(14);
      f.setDynamicLOV("INVENTORY_PART_WO_LOV","SPARE_CONTRACT CONTRACT",600,445);
      f.setMandatory();
      f.setCustomValidation("PART_NO,SPARE_CONTRACT,ITEM1_CONTRACT,SALES_PART_COST,QTY_PLAN,OWNER,CONDITION_CODE,PART_OWNERSHIP,ITEM1_PM_NO,ITEM1_PM_REVISION","ITEM1_CATALOG_NO,SPAREDESCRIPTION,INVENTORYFLAG,DIMQUALITY,TYPEDESIGNATION,QTYONHAND,UNITMEAS,SALES_PART_COST,ACTIVEIND_DB");
      f.setLabel("PCMWPMACTIONPARTNO: Part No");
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONPANO: List of Part No"));
      f.setUpperCase();
      f.setMaxLength(25);
      f.setInsertable();

      f = itemblk1.addField("SPAREDESCRIPTION");
      f.setSize(25);
      f.setLabel("PCMWPMACTIONSPAREDESC: Part Description");
      f.setFunction("PURCHASE_PART_API.Get_Description(:SPARE_CONTRACT,:PART_NO)");
      f.setReadOnly();

      f = itemblk1.addField("SPARE_CONTRACT");
      f.setSize(7);
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
      f.setMandatory();
      f.setLabel("PCMWPMACTIONSPARECON: Site");   
      f.setUpperCase();
      f.setMaxLength(5);
      f.setInsertable();

      f = itemblk1.addField("CONDITION_CODE");
      f.setSize(10);
      f.setUpperCase();
      f.setMaxLength(10);
      f.setDynamicLOV("CONDITION_CODE");
      f.setLabel("PCMWPMACTIONCONDITIONCODE: Condition Code");
      f.setCustomValidation("CONDITION_CODE,SPARE_CONTRACT,PART_NO,PART_OWNERSHIP,QTY_PLAN","CONDITION_CODE_DESC,QTYONHAND,SALES_PART_COST");

      f = itemblk1.addField("CONDITION_CODE_DESC");
      f.setSize(20);
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONCONDITIONCODEDESC: Condition Code Description");
      f.setFunction("CONDITION_CODE_API.Get_Description(:CONDITION_CODE)");

      f = itemblk1.addField("PART_OWNERSHIP");
      f.setSize(20);
      f.setInsertable();
      f.setSelectBox();
      f.enumerateValues("PART_OWNERSHIP_API");
      f.setLabel("PCMWPMACTIONPARTOWNERSHIP: Ownership");
      f.setCustomValidation("PART_OWNERSHIP,SPARE_CONTRACT,PART_NO","PART_OWNERSHIP_DB,QTYONHAND");

      f = itemblk1.addField("PART_OWNERSHIP_DB");
      f.setHidden();

      f = itemblk1.addField("OWNER");
      f.setSize(18);
      f.setMaxLength(20);
      f.setInsertable();
      f.setLabel("PCMWPMACTIONPARTOWNER: Owner");
      f.setCustomValidation("OWNER,PART_OWNERSHIP_DB,SPARE_CONTRACT,PART_NO,PART_OWNERSHIP","OWNER_NAME,QTYONHAND");
      f.setDynamicLOV("CUSTOMER_INFO");

      f = itemblk1.addField("OWNER_NAME");
      f.setSize(20);
      f.setMaxLength(100);
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONPARTOWNERNAME: Owner Name");
      f.setFunction("CUSTOMER_INFO_API.Get_Name(:OWNER)");

      f = itemblk1.addField("INVENTORYFLAG");
      f.setSize(15);
      f.setLabel("PCMWPMACTIONINVFLAG: Inventory Part");
      f.setFunction("PURCHASE_PART_API.Get_Inventory_Flag(:SPARE_CONTRACT,:PART_NO)");
      f.setReadOnly();

      f = itemblk1.addField("DIMQUALITY");
      f.setSize(17);
      f.setLabel("PCMWPMACTIONDIMQUALITY: Dimension/ Quality");
      f.setFunction("Inventory_Part_API.Get_Dim_Quality(:SPARE_CONTRACT,:PART_NO)");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk1.addField("TYPEDESIGNATION");
      f.setSize(16);
      f.setLabel("PCMWPMACTIONTYPEDESIG: Type Designation");
      f.setFunction(      "Inventory_Part_API.Get_Type_Designation(:SPARE_CONTRACT,:PART_NO)");
      f.setReadOnly();

      f = itemblk1.addField("QTYONHAND","Number");
      f.setSize(16);
      f.setLabel("PCMWPMACTIONQTYONHAND: Quantity on Hand");
      f.setFunction("Inventory_Part_In_Stock_API.Get_Inventory_Quantity(:SPARE_CONTRACT,:PART_NO,NULL,'ONHAND',NULL,NULL,:PART_OWNERSHIP_DB,NULL,NULL,NULL,:OWNER,NULL,'PICKING','F','PALLET','DEEP','BUFFER','DELIVERY','SHIPMENT','MANUFACTURING',NULL,NULL,NULL,NULL,'TRUE','FALSE',NULL,NULL,NULL,NULL,NULL,NULL,:CONDITION_CODE)");
      f.setReadOnly();

      f = itemblk1.addField("UNITMEAS");
      f.setSize(8);
      f.setLabel("PCMWPMACTIONUNITMEAS: Unit");
      f.setFunction("Purchase_Part_Supplier_API.Get_Unit_Meas(:SPARE_CONTRACT,:PART_NO)");
      f.setReadOnly();

      f = itemblk1.addField("QTY_PLAN","Number");
      f.setSize(16);
      f.setMandatory();
      f.setLabel("PCMWPMACTIONQTYPLAN: Planned Quantity");
      f.setCustomValidation("QTY_PLAN,SPARE_CONTRACT,PART_NO,SALES_PART_COST,ITEM1_LISTPRICE,CUSTOMER_NO,AGREEMENT_ID,ITEM1_CATALOG_NO,SERIAL_NO,CONFIGURATION_ID,CONDITION_CODE","SALES_PART_COST,ITEM1_LISTPRICE");

      f = itemblk1 .addField("ITEM1_CONTRACT");
      f.setSize(18);
      f.setLabel("PCMWPMACTIONCONTNEW1: Sales Part Site");
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
      f.setDbName("CONTRACT");
      f.setMaxLength(5);
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONROCO12: List of Sales Part Site"));
      f.setUpperCase();
      f.setDefaultNotVisible();
      f.setReadOnly();

      f= itemblk1 .addField("ITEM1_CATALOG_NO");
      f.setSize(12);
      f.setLabel("PCMWPMACTIONDESC1: Sales Part");
      f.setDbName("CATALOG_NO");
      f.setMaxLength(25);
      f.setDynamicLOV("SALES_PART","ITEM1_CONTRACT CONTRACT",600,445);
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONROCO1: List of Sales Part"));
      f.setUpperCase();
      f.setCustomValidation("ITEM1_CATALOG_NO,ITEM1_CONTRACT","SALESPARTDESCRIPTION1,ITEM1_LISTPRICE");
      f.setDefaultNotVisible();

      f = itemblk1.addField("SALESPARTDESCRIPTION1");
      f.setSize(25);
      f.setLabel("PCMWPMACTIONSALESPART11: Sales Part Description ");
      if (mgr.isModuleInstalled("ORDER"))
         f.setFunction("SALES_PART_API.Get_Catalog_Desc(:ITEM1_CONTRACT,:ITEM1_CATALOG_NO)");
      else
         f.setFunction("''");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk1.addField("ITEM1_JOB_ID","Number","#");
      f.setDbName("JOB_ID");
      f.setDynamicLOV("PM_ACTION_JOB","PM_NO,PM_REVISION",600,445);
      f.setLabel("PCMWPMACTIONITEM5JOBID1: Job ID");
      f.setSize(8);
      f.setInsertable();

      f = itemblk1.addField("SALES_PART_COST","Number");
      f.setSize(13);
      f.setLabel("PCMWPMACTIONITEM1SALESPARTCOST: Cost");
      f.setFunction("''");
      f.setReadOnly();

      f = itemblk1.addField("ITEM1_LISTPRICE","Money");
      f.setSize(18);
      f.setLabel("PCMWPMACTIONITEM1LISTPRICE: Sales Price");
      if (mgr.isModuleInstalled("ORDER"))
         f.setFunction("SALES_PART_API.Get_List_Price(:ITEM1_CONTRACT,:ITEM1_CATALOG_NO)");
      else
         f.setFunction("''");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk1.addField("ITEM1_PM_NO","Number","#");
      f.setSize(8);
      f.setMandatory();
      f.setHidden();
      f.setDbName("PM_NO");

      f = itemblk1.addField("ITEM1_PM_REVISION");
      f.setSize(8);
      f.setMandatory();
      f.setHidden();
      f.setDbName("PM_REVISION");

      f = itemblk1.addField("PM_SPARE_SEQ","Number");
      f.setSize(8);
      f.setHidden();

      f = itemblk1.addField("SUPPLY_CODE");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("PRIMARY_SUPPLIER");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("BASE_PRICE");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("LIST_PRICE");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("CONFIG_ID");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("COND_CODE_USAGE");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("QTY_TYPE");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("EXPIRATION");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("SUPPLY_CONTROL");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("OWNERSHIP_TYPE2");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("OWNERSHIP_TYPE3");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("OWNERSHIP_TYPE4");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("OWNER_VENDOR");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("LOCATION_TYPE1");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("LOCATION_TYPE2");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("LOCATION_TYPE3");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("LOCATION_TYPE4");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("LOCATION_TYPE5");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("LOCATION_TYPE6");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("LOCATION_TYPE7");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("LOCATION_TYPE8");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("LOT_BATCH_NO");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("SERIAL_NO");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("ENG_CHG_LEVEL");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("WAIV_DEV_REJ_NO");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("INCLUDE_STANDARD");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("INCLUDE_PROJECT");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("ACTIVITY_SEQ");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("PROJECT_ID");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("LOCATION_NO");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("ORDER_ISSUE");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("AUTOMAT_RESERV");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("MANUAL_RESERV");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("CATALOG_NO");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("CAT_EXIST","Number");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("PM_SITE");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("CONFIGURATION_ID");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("ACTIVEIND");
      f.setHidden();
      f.setFunction("''");

      f = itemblk1.addField("ACTIVEIND_DB");
      f.setHidden();
      f.setFunction("''");

      itemblk1.setView("PM_ACTION_SPARE_PART");
      itemblk1.defineCommand("PM_ACTION_SPARE_PART_API","New__,Modify__,Remove__");
      itemblk1.setMasterBlock(headblk);

      itemset1 = itemblk1.getASPRowSet();

      itembar1 = mgr.newASPCommandBar(itemblk1);
      itembar1.defineCommand(itembar1.SAVERETURN,null,"checkItem1Fields(-1)");
      itembar1.defineCommand(itembar1.SAVENEW,null,"checkItem1Fields(-1)");

      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setWrap();
      // 040114  ARWILK  Begin  (Enable Multirow RMB actions)
      itemtbl1.enableRowSelect();
      // 040114  ARWILK  End  (Enable Multirow RMB actions)

      itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
      itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
      itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1");
      itembar1.enableCommand(itembar1.FIND);

      // 031211  ARWILK  Begin  (Repalce links with RMB's)
      itembar1.addCustomCommand("sparePartObject",mgr.translate("PCMWPMACTIONSPAREPARTOBJ: Spare Parts in Object..."));
      itembar1.addCustomCommandSeparator();
      // 031211  ARWILK  End  (Replace links with RMB's)
      if (mgr.isPresentationObjectInstalled("equipw/EquipmentSpareStructure2.page"))
         itembar1.addCustomCommand("sparePartsInDetPart",mgr.translate("PCMWPMACTIONCDN11: Spare Parts in Detached Part List..."));
      if (mgr.isPresentationObjectInstalled("invenw/InventoryPartInventoryPartCurrentOnHand.page"))
         itembar1.addCustomCommand("currQuntity",mgr.translate("PCMWPMACTIONCDN2: Current Quantity on Hand..."));
      if (mgr.isPresentationObjectInstalled("invenw/InventoryPartAvailabilityPlanningQry.page"))
         itembar1.addCustomCommand("availDetail",mgr.translate("PCMWPMACTIONCDN3: Query - Inventory Part Availability Planning..."));
      if (mgr.isPresentationObjectInstalled("invenw/InventoryPart.page"))
         itembar1.addCustomCommand("inventoryPart",mgr.translate("PCMWPMACTIONCDN4: Inventory Part..."));
      if (mgr.isPresentationObjectInstalled("purchw/PurchasePartSupplier.page"))
         itembar1.addCustomCommand("supplierPerPart",mgr.translate("PCMWPMACTIONCDN5: Supplier per Part..."));

      // 031229  ARWILK  Begin  (Links with multirow RMB's)
      itembar1.enableMultirowAction();
      // 031229  ARWILK  End  (Links with multirow RMB's)

      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDialogColumns(2);
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);

      mgr.getASPPage().getASPPopup("tbl3Pop").setWidth(200);
      mgr.getASPPage().getASPPopup("actionITEM1").setWidth(200);

      //================================================================
      // Tools and Facilities
      //================================================================

      itemblk5 = mgr.newASPBlock("ITEM5");

      f = itemblk5.addField("ITEM5_OBJID");
      f.setDbName("OBJID");
      f.setHidden();

      f = itemblk5.addField("ITEM5_OBJVERSION");
      f.setDbName("OBJVERSION");
      f.setHidden();

      f = itemblk5.addField("ITEM5_PM_NO","Number","#");
      f.setDbName("PM_NO");
      f.setLabel("PCMWPMACTIONITEM5PMNO: Pm No");
      f.setSize(8);
      f.setMandatory();
      f.setInsertable();
      f.setReadOnly();
      f.setHidden();

      f = itemblk5.addField("ITEM5_PM_REVISION");
      f.setDbName("PM_REVISION");
      f.setLabel("PCMWPMACTIONITEM5PMREV: Pm Revision");
      f.setSize(8);
      f.setInsertable();
      f.setMandatory();
      f.setReadOnly();
      f.setHidden();

      f = itemblk5.addField("ITEM5_ROW_NO","Number");
      f.setDbName("ROW_NO");
      f.setLabel("PCMWPMACTIONITEM5ROWNO: Row No");
      f.setSize(8);
      f.setReadOnly();

      f = itemblk5.addField("ITEM5_CONTRACT");
      f.setDbName("CONTRACT");
      f.setLabel("PCMWPMACTIONITEM5CONTRACT: Site");
      f.setLOV("../mscomw/ConnectToolsFacilitiesSiteLov.page","TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM5_ORG_CODE ORG_CODE,ITEM5_CONTRACT CONTRACT",600,445);
      f.setCustomValidation("ITEM5_CONTRACT,ITEM5_ORG_CODE,TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM5_QTY,ITEM5_PLANNED_HOUR,ITEM5_PM_NO,ITEM5_PM_REVISION,ITEM5_NOTE,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_CONTRACT,ITEM5_CATALOG_NO_DESC,ITEM5_SALES_PRICE,ITEM5_SALES_CURRENCY,ITEM5_DISCOUNT,ITEM5_PLANNED_PRICE","ITEM5_NOTE,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_DESC,ITEM5_SALES_PRICE,ITEM5_SALES_CURRENCY,ITEM5_DISCOUNT,ITEM5_PLANNED_PRICE,TOOL_FACILITY_ID,TOOL_FACILITY_DESC,TOOL_FACILITY_TYPE,TYPE_DESCRIPTION,ITEM5_CONTRACT,ITEM5_COST");
      f.setSize(8);
      f.setMaxLength(5);
      f.setInsertable();
      f.setUpperCase();

      f = itemblk5.addField("TOOL_FACILITY_ID");
      f.setLabel("PCMWPMACTIONITEM5TFID: Tool/Facility ID");
      f.setLOV("../mscomw/ConnectToolsFacilitiesSiteLov.page","ITEM5_CONTRACT CONTRACT,ITEM5_ORG_CODE ORG_CODE,TOOL_FACILITY_TYPE",600,445);
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONTFID: List of Tool/Facility ID"));
      f.setCustomValidation("TOOL_FACILITY_ID,ITEM5_CONTRACT,ITEM5_ORG_CODE,ITEM5_PM_NO,ITEM5_PM_REVISION,ITEM5_QTY,ITEM5_PLANNED_HOUR,ITEM5_NOTE,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_CONTRACT,ITEM5_CATALOG_NO_DESC,ITEM5_SALES_PRICE,ITEM5_SALES_CURRENCY,ITEM5_DISCOUNT,ITEM5_PLANNED_PRICE","TOOL_FACILITY_DESC,TOOL_FACILITY_TYPE,TYPE_DESCRIPTION,ITEM5_COST,ITEM5_COST_CURRENCY,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_DESC,ITEM5_SALES_PRICE,ITEM5_SALES_CURRENCY,ITEM5_DISCOUNT,ITEM5_PLANNED_PRICE,ITEM5_NOTE,ITEM5_CONTRACT,TOOL_FACILITY_ID");
      f.setSize(20);
      f.setMaxLength(40);
      f.setInsertable();
      f.setUpperCase();

      f = itemblk5.addField("TOOL_FACILITY_DESC");
      f.setLabel("PCMWPMACTIONITEM5TFIDDESC: Tool/Facility Description");
      f.setFunction("Tool_Facility_API.Get_Tool_Facility_Description(:TOOL_FACILITY_ID)");
      f.setSize(30);
      f.setMaxLength(200);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk5.addField("TOOL_FACILITY_TYPE");
      f.setLabel("PCMWPMACTIONITEM5TFTYPE: Tool/Facility Type");
      f.setDynamicLOV("TOOL_FACILITY_TYPE",600,445);
      f.setCustomValidation("TOOL_FACILITY_TYPE,TOOL_FACILITY_ID,ITEM5_PM_NO,ITEM5_PM_REVISION,ITEM5_QTY,ITEM5_PLANNED_HOUR","TYPE_DESCRIPTION,ITEM5_COST,ITEM5_COST_CURRENCY");
      f.setSize(20);
      f.setMaxLength(40);
      f.setInsertable();
      f.setUpperCase();
      f.setMandatory();

      f = itemblk5.addField("TYPE_DESCRIPTION");
      f.setLabel("PCMWPMACTIONITEM5TFTYPEDESC: Type Description");
      f.setFunction("Tool_Facility_Type_API.Get_Type_Description(:TOOL_FACILITY_TYPE)");
      f.setSize(30);
      f.setMaxLength(200);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk5.addField("ITEM5_ORG_CODE");
      f.setDbName("ORG_CODE");
      f.setLabel("PCMWPMACTIONITEM5ORGCODE: Maintenance Organization");
      f.setDynamicLOV("CONNECT_TOOLS_FACILITIES_ORG","TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM5_CONTRACT CONTRACT",600,445);
      f.setCustomValidation("ITEM5_ORG_CODE,ITEM5_CONTRACT,TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM5_QTY,ITEM5_PLANNED_HOUR,ITEM5_PM_NO,ITEM5_PM_REVISION,ITEM5_NOTE,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_CONTRACT,ITEM5_CATALOG_NO_DESC,ITEM5_SALES_PRICE,ITEM5_SALES_CURRENCY,ITEM5_DISCOUNT,ITEM5_PLANNED_PRICE","ITEM5_NOTE,ITEM5_QTY,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_DESC,ITEM5_SALES_PRICE,ITEM5_SALES_CURRENCY,ITEM5_DISCOUNT,ITEM5_PLANNED_PRICE");
      f.setSize(12);
      f.setMaxLength(8);
      f.setInsertable();
      f.setUpperCase();

      f = itemblk5.addField("ITEM5_QTY", "Number");
      f.setDbName("QTY");
      f.setLabel("PCMWPMACTIONITEM5QTY: Quantity");
      f.setCustomValidation("ITEM5_QTY,ITEM5_PLANNED_HOUR,TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM5_PM_NO,ITEM5_PM_REVISION,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_CONTRACT,ITEM5_SALES_PRICE,ITEM5_DISCOUNT","ITEM5_PLANNED_PRICE");
      f.setSize(10);
      f.setInsertable();

      f = itemblk5.addField("ITEM5_PLANNED_HOUR", "Number");
      f.setDbName("PLANNED_HRS");
      f.setLabel("PCMWPMACTIONITEM5PLNHRS: Planned Hours");
      f.setCustomValidation("ITEM5_PLANNED_HOUR,ITEM5_QTY,TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM5_PM_NO,ITEM5_PM_REVISION,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_CONTRACT,ITEM5_SALES_PRICE,ITEM5_DISCOUNT","ITEM5_PLANNED_PRICE");
      f.setSize(10);
      f.setInsertable();

      f = itemblk5.addField("ITEM5_CRAFT_LINE_NO", "Number");
      f.setDbName("OPERATION_NO");
      f.setLabel("PCMWPMACTIONITEM5CRAFTLINENO: Operation No");
      f.setDynamicLOV("PM_ACTION_ROLE","ITEM5_PM_NO PM_NO,ITEM5_PM_REVISION PM_REVISION",600,445);
      f.setSize(8);
      f.setInsertable();
      f.setDefaultNotVisible();

      f = itemblk5.addField("ITEM5_COST", "Money");
      f.setDbName("COST");
      f.setLabel("PCMWPMACTIONITEM5COST: Cost");
      f.setSize(12);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk5.addField("ITEM5_COST_CURRENCY");
      f.setDbName("COST_CURRENCY");
      f.setLabel("PCMWPMACTIONITEM5COSTCURR: Cost Currency");
      f.setSize(10);
      f.setMaxLength(3);
      f.setReadOnly();
      f.setHidden();

      f = itemblk5.addField("ITEM5_CATALOG_NO_CONTRACT");
      f.setDbName("CATALOG_NO_CONTRACT");
      f.setLabel("PCMWPMACTIONITEM5CATALOGCONTRACT: Sales Part Site");
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
      f.setSize(8);
      f.setMaxLength(5);
      f.setReadOnly();
      if (!mgr.isModuleInstalled("ORDER"))
         f.setHidden();


      f = itemblk5.addField("ITEM5_CATALOG_NO");
      f.setDbName("CATALOG_NO");
      f.setLabel("PCMWPMACTIONITEM5CATALOGNO: Sales Part");
      f.setDynamicLOV("SALES_PART_SERVICE_LOV", "ITEM5_CATALOG_NO_CONTRACT CONTRACT",600,445);
      f.setCustomValidation("ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_CONTRACT,ITEM5_PM_NO,ITEM5_PM_REVISION,ITEM5_QTY,ITEM5_PLANNED_HOUR","ITEM5_CATALOG_NO_DESC,ITEM5_SALES_PRICE,ITEM5_SALES_CURRENCY,ITEM5_DISCOUNT,ITEM5_PLANNED_PRICE");
      f.setSize(20);
      f.setMaxLength(25);
      f.setInsertable();
      if (!mgr.isModuleInstalled("ORDER"))
         f.setHidden();

      f = itemblk5.addField("ITEM5_CATALOG_NO_DESC");
      f.setLabel("PCMWPMACTIONITEM5CATALOGDESC: Sales Part Description");
      f.setFunction("''");
      f.setSize(30);
      f.setMaxLength(35);
      f.setReadOnly();
      f.setDefaultNotVisible();
      if (!mgr.isModuleInstalled("ORDER"))
         f.setHidden();
      else
         f.setFunction("Connect_Tools_Facilities_API.Get_Sales_Part_Desc(:ITEM5_CATALOG_NO_CONTRACT,:ITEM5_CATALOG_NO)");   

      f = itemblk5.addField("ITEM5_SALES_PRICE", "Money");
      f.setDbName("SALES_PRICE");
      f.setLabel("PCMWPMACTIONITEM5SALESPRICE: Sales Price");
      f.setCustomValidation("ITEM5_SALES_PRICE,ITEM5_CATALOG_NO_CONTRACT,ITEM5_CATALOG_NO,ITEM5_PM_NO,ITEM5_PM_REVISION,ITEM5_QTY,ITEM5_PLANNED_HOUR,ITEM5_DISCOUNT","ITEM5_PLANNED_PRICE");
      f.setSize(12);
      f.setInsertable();
      f.setDefaultNotVisible();
      if (!mgr.isModuleInstalled("ORDER"))
         f.setHidden();

      f = itemblk5.addField("ITEM5_JOB_ID","Number","#");
      f.setDbName("JOB_ID");
      f.setDynamicLOV("PM_ACTION_JOB","PM_NO,PM_REVISION",600,445);
      f.setLabel("PCMWPMACTIONITEM5JOBID5: Job ID");
      f.setSize(8);
      f.setInsertable();

      f = itemblk5.addField("ITEM5_SALES_CURRENCY");
      f.setDbName("PRICE_CURRENCY");
      f.setLabel("PCMWPMACTIONITEM5SALESCURR: Sales Currency");
      f.setSize(10);
      f.setMaxLength(3);
      f.setReadOnly();
      f.setHidden();
      f.setDefaultNotVisible();
      if (!mgr.isModuleInstalled("ORDER"))
         f.setHidden();

      f = itemblk5.addField("ITEM5_DISCOUNT", "Number");
      f.setDbName("DISCOUNT");
      f.setLabel("PCMWPMACTIONITEM5DISCOUNT: Discount");
      f.setCustomValidation("ITEM5_DISCOUNT,ITEM5_SALES_PRICE,ITEM5_CATALOG_NO_CONTRACT,ITEM5_CATALOG_NO,ITEM5_PM_NO,ITEM5_PM_REVISION,ITEM5_QTY,ITEM5_PLANNED_HOUR","ITEM5_PLANNED_PRICE");
      f.setSize(8);
      f.setInsertable();
      f.setDefaultNotVisible();
      if (!mgr.isModuleInstalled("ORDER"))
         f.setHidden();

      f = itemblk5.addField("ITEM5_PLANNED_PRICE", "Money");
      f.setLabel("PCMWPMACTIONITEM5PLANNEDPRICE: Planned Price Amount");
      f.setFunction("''");
      f.setSize(12);
      f.setReadOnly();
      f.setDefaultNotVisible();
      if (!mgr.isModuleInstalled("ORDER"))
         f.setHidden();
      else
         f.setFunction("Pm_Action_Tool_Facility_API.Get_Price_Amount(:ITEM5_CATALOG_NO,:ITEM5_CATALOG_NO_CONTRACT,:ITEM5_PM_NO,:ITEM5_PM_REVISION,:ITEM5_QTY,:ITEM5_PLANNED_HOUR,:ITEM5_SALES_PRICE,:ITEM5_DISCOUNT)");

      f = itemblk5.addField("ITEM5_NOTE");
      f.setDbName("NOTE");
      f.setLabel("PCMWPMACTIONITEM5NOTE: Note");
      f.setSize(20);
      f.setHeight(4);
      f.setMaxLength(2000);
      f.setInsertable();
      f.setDefaultNotVisible();

      f = itemblk5.addField("ITEM5_WO_CONTRACT");
      f.setFunction("''");
      f.setHidden();

      f = itemblk5.addField("ITEM5_SP_SITE");
      f.setFunction("''");
      f.setHidden();

      itemblk5.setView("PM_ACTION_TOOL_FACILITY");
      itemblk5.defineCommand("PM_ACTION_TOOL_FACILITY_API","New__,Modify__,Remove__");
      itemset5 = itemblk5.getASPRowSet();

      itemblk5.setMasterBlock(headblk);

      itembar5 = mgr.newASPCommandBar(itemblk5);
      itembar5.enableCommand(itembar5.FIND);
      itembar5.defineCommand(itembar5.NEWROW,"newRowITEM5");
      itembar5.defineCommand(itembar5.COUNTFIND,"countFindITEM5");
      itembar5.defineCommand(itembar5.OKFIND,"okFindITEM5");

      itemtbl5 = mgr.newASPTable(itemblk5);
      itemtbl5.setWrap();

      itemlay5 = itemblk5.getASPBlockLayout();
      itemlay5.setDefaultLayoutMode(itemlay5.MULTIROW_LAYOUT);
      itemlay5.setDialogColumns(3);  


      //================================================================
      // Criteria
      //==================================================================

      itemblk3 = mgr.newASPBlock("ITEM3");

      f = itemblk3.addField("ITEM3_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk3.addField("ITEM3_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk3.addField("ITEM3_PM_NO","Number","#");
      f.setSize(8);
      f.setMandatory();
      f.setHidden();
      f.setLabel("PCMWPMACTIONIT3PMNO: PM no");
      f.setDbName("PM_NO");

      f = itemblk3.addField("ITEM3_PM_REVISION");
      f.setSize(8);
      f.setMandatory();
      f.setHidden();
      f.setLabel("PCMWPMACTIONIT3PMREV: PM Revision");
      f.setDbName("PM_REVISION");


      f = itemblk3.addField("ITEM3_CONTRACT");
      f.setSize(8);
      f.setMandatory();
      f.setHidden();
      f.setUpperCase();
      f.setDbName("CONTRACT");

      f = itemblk3.addField("ITEM3_MCH_CODE");
      f.setSize(8);
      f.setMandatory();
      f.setHidden();
      f.setDbName("MCH_CODE");

      f = itemblk3.addField("ITEM3_TEST_POINT_ID");
      f.setSize(8);
      f.setDynamicLOV("EQUIPMENT_OBJECT_TEST_PNT","HEAD_CONTRACT CONTRACT,MCH_CODE",600,445);
      f.setMandatory();
      f.setLabel("PCMWPMACTIONTSTPID: Testpoint");
      f.setUpperCase();
      f.setDbName("TEST_POINT_ID");
      f.setMaxLength(6);

      f = itemblk3.addField("PARAMETER_CODE");
      f.setSize(9);
      f.setDynamicLOV("EQUIPMENT_OBJECT_PARAM","ITEM3_CONTRACT CONTRACT,ITEM3_MCH_CODE MCH_CODE,ITEM3_TEST_POINT_ID TEST_POINT_ID",600,445);
      f.setMandatory();
      f.setCustomValidation("PARAMETER_CODE,ITEM3_MCH_CODE,ITEM3_CONTRACT,ITEM3_TEST_POINT_ID","PARAMETERDESCRIPTION,VALUETYPE,UNITCODE");
      f.setLabel("PCMWPMACTIONPARACODE: Parameter");
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONPARCO: List of Parameter"));
      f.setUpperCase();
      f.setMaxLength(5);

      f = itemblk3.addField("PARAMETERDESCRIPTION");
      f.setSize(25);
      f.setLabel("PCMWPMACTIONPARADESC: Parameter Description");
      f.setFunction("MEASUREMENT_PARAMETER_API.Get_Description(:PARAMETER_CODE)");
      f.setReadOnly();

      f = itemblk3.addField("VALUETYPE");
      f.setSize(8);
      f.setLabel("PCMWPMACTIONVALUETYPE: Type");
      f.setFunction("MEASUREMENT_PARAMETER_API.Get_Type(:PARAMETER_CODE)");
      f.setReadOnly();

      f = itemblk3.addField("UNITCODE");
      f.setSize(5);
      f.setLabel("PCMWPMACTIONUNITCODE: Unit");
      f.setFunction("EQUIPMENT_OBJECT_PARAM_API.Get_Unit(:ITEM3_CONTRACT,:ITEM3_MCH_CODE,:ITEM3_TEST_POINT_ID,:PARAMETER_CODE)");
      f.setReadOnly();

      f = itemblk3.addField("MIN_VALUE","Number");
      f.setSize(10);
      f.setLabel("PCMWPMACTIONMINVALUE: Min Value");

      f = itemblk3.addField("MAX_VALUE","Number");
      f.setSize(10);
      f.setLabel("PCMWPMACTIONMAXVALUE: Max Value");

      f = itemblk3.addField("ACC_START_VALUE","Number");
      f.setSize(11);
      f.setLabel("PCMWPMACTIONASVALUE: Start Value");

      f = itemblk3.addField("ACC_INTERVAL","Number");
      f.setSize(8);
      f.setLabel("PCMWPMACTIONACCINT: Interval");

      f = itemblk3.addField("LAST_VALUE","Number");
      f.setSize(9);
      f.setLabel("PCMWPMACTIONLASTVALUE: Last Value");
      f.setReadOnly();

      f = itemblk3.addField("PMGENVALUE","Number");
      f.setSize(10);
      f.setLabel("PCMWPMACTIONPMGENVALUE: Generation Value");
      f.setReadOnly();

      f = itemblk3.addField("CRITERIA_SEQ","Number");
      f.setSize(8);
      f.setHidden();
      f.setLabel("PCMWPMACTIONCRISEQ: Criteria Seq");

      itemblk3.setView("PM_ACTION_CRITERIA");
      // 040114  ARWILK  Begin  (Enhance Performance)
      itemblk3.defineCommand("PM_ACTION_CRITERIA_API","New__,Remove__");
      // 040114  ARWILK  End  (Enhance Performance)
      itemblk3.setMasterBlock(headblk);

      itemset3 = itemblk3.getASPRowSet();

      itembar3 = mgr.newASPCommandBar(itemblk3);

      itemtbl3 = mgr.newASPTable(itemblk3);
      itemtbl3.setWrap();

      itembar3.defineCommand(itembar3.SAVERETURN,null,"checkItem3Fields(-1)");
      itembar3.defineCommand(itembar3.SAVENEW,null,"checkItem3Fields(-1)");    

      itembar3.defineCommand(itembar3.OKFIND,"okFindITEM3");
      itembar3.defineCommand(itembar3.COUNTFIND,"countFindITEM3");
      itembar3.defineCommand(itembar3.NEWROW,"newRowITEM3");
      itembar3.enableCommand(itembar3.FIND);   

      itembar3.addCustomCommand("parameters",mgr.translate("PCMWPMACTIONROUNDCRI1: Parameters..."));
      itembar3.addCustomCommand("measurements",mgr.translate("PCMWPMACTIONROUNDCRI2: Measurements..."));

      // 040114  ARWILK  Begin  (Enable Multirow RMB actions)
      itembar3.enableMultirowAction();
      // 040114  ARWILK  End  (Enable Multirow RMB actions)

      // 040114  ARWILK  Begin  (Enhance Performance)
      //itembar3.disableCommand(itembar3.EDIT);  
      // 040114  ARWILK  End  (Enhance Performance)

      itemlay3 = itemblk3.getASPBlockLayout();
      itemlay3.setDialogColumns(2);
      itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);

      //===============================================================
      // Maintenance Plan
      //===============================================================

      itemblk2 = mgr.newASPBlock("ITEM2");

      f = itemblk2.addField("ITEM2_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk2.addField("ITEM2_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk2.addField("ITEM2_COMPANY");
      f.setSize(8);
      f.setHidden();
      f.setDbName("COMPANY");

      f = itemblk2.addField("PLANNED_DATE","Datetime");
      f.setSize(15);
      f.setMandatory();
      f.setLabel("PCMWPMACTIONPLNDATE: Planned Date");

      f = itemblk2.addField("PLANNED_WEEK");
      f.setSize(15);
      f.setLabel("PCMWPMACTIONGENWEEK: Planned Week");
      f.setReadOnly();

      f = itemblk2.addField("GENERATION_DATE","Date");
      f.setSize(17);
      f.setLabel("PCMWPMACTIONGENDATE: Generation Date");
      f.setReadOnly();

      f = itemblk2.addField("COMPLETION_DATE","Date");
      f.setSize(17);
      f.setLabel("PCMWPMACTIONCMPLDATE: Completion Date");
      f.setReadOnly();

      f = itemblk2.addField("REPLACED_BY_PM_NO");
      f.setSize(19);
      f.setLabel("PCMWPMACTIONRBPMNO: Replace By Pm No");
      f.setReadOnly();
      f.setHidden();

      f = itemblk2.addField("REPLACED_BY_PM_REVISION");
      f.setSize(20);
      f.setLabel("PCMWPMACTIONRBPMRev: Replace By Pm Revision");
      f.setReadOnly();
      f.setHidden();

      f = itemblk2.addField("REPLACED_BY_SEQ_NO");
      f.setSize(26);
      f.setLabel("PCMWPMACTIONRBSEQNO: Replace By Pm Sequence No");
      f.setReadOnly();
      f.setHidden();

      f = itemblk2.addField("PM_ADJUSTED_DATE");
      f.setSize(12);
      f.setLabel("PCMWPMACTIONPMADJDATE: Adjusted");
      f.setSelectBox();
      f.enumerateValues("PM_ADJUSTED_DATE_API");
      f.unsetSearchOnDbColumn();

      f = itemblk2.addField("ITEM2_PM_GENERATEABLE");
      f.setSize(12);
      f.setLabel("PCMWPMACTIONPMGENABLE: Generatable");
      f.setSelectBox();
      f.enumerateValues("PM_GENERATEABLE_API");
      f.unsetSearchOnDbColumn();
      f.setDbName("PM_GENERATEABLE");

      f = itemblk2.addField("ITEM2_WO_NO","Number","#");
      f.setSize(12);
      f.setLabel("PCMWPMACTIONWONUM: WO Number");
      f.setReadOnly();
      f.setDbName("WO_NO");

      f = itemblk2.addField("ITEM2_STATUS");
      f.setSize(12);
      f.setLabel("PCMWPMACTIONSTTUS: Status ");
      f.setFunction("WORK_ORDER_API.Get_Wo_Status_Id_Cl(:ITEM2_WO_NO)");
      f.setReadOnly();

      f = itemblk2.addField("ITEM2_NOTE");
      f.setSize(12);
      f.setLabel("PCMWPMACTIONINSNOTE: Inspection Note ");
      f.setFunction("WORK_ORDER_API.Get_Note(:ITEM2_WO_NO)");
      f.setReadOnly();
      f.setInsertable();

      f = itemblk2.addField("ITEM2_GEN_ID");
      f.setSize(12);
      f.setLabel("PCMWPMACTIONGNID: Gen ID");
      f.setDbName("GEN_ID");
      f.setDynamicLOV("PM_GENERATION",600,445);
      f.setHidden();

      f = itemblk2.addField("ITEM2_GEN_TYPE");
      f.setSize(8);
      f.setHidden();
      f.setFunction("PM_GENERATION_API.Get_Generation_Type(:ITEM2_GEN_ID)");

      f = itemblk2.addField("ITEM2_ATTR");
      f.setSize(8);
      f.setHidden();
      f.setFunction("PM_GENERATION_API.Get_Attr(:ITEM2_GEN_ID)");

      f = itemblk2.addField("ITEM2_EVENT");   
      f.setSize(12);
      f.setLabel("PCMWPMACTIONEVNT: Event");
      f.setReadOnly();
      f.setDbName("EVENT");
      f.setHidden();

      f = itemblk2.addField("ITEM2_EVENT_SEQ");
      f.setSize(12);
      f.setLabel("PCMWPMACTIONEVNTSEQ: Event Seq");
      f.setReadOnly();
      f.setDbName("EVENT_SEQ");
      f.setHidden();

      f = itemblk2.addField("TESTPOINT");
      f.setSize(12);
      f.setLabel("PCMWPMACTIONTSTPNT: Testpoint");
      f.setReadOnly();
      f.setHidden();

      f = itemblk2.addField("PARAMETER");
      f.setSize(12);
      f.setLabel("PCMWPMACTIONPRMTR: Parameter");
      f.setReadOnly();
      f.setHidden();

      f = itemblk2.addField("VALUE");
      f.setSize(12);
      f.setLabel("PCMWPMACTIONVLUE: Value");
      f.setReadOnly();
      f.setHidden();

      f = itemblk2.addField("ITEM2_EMPLOYEE_ID");
      f.setSize(12);
      f.setLabel("PCMWPMACTIONEMPLEEID: Employee ID");
      f.setDbName("EMPLOYEE_ID");
      f.setHidden();

      f = itemblk2.addField("ITEM2_SIGNATURE");
      f.setSize(12);
      f.setDbName("SIGNATURE");
      f.setMandatory();
      f.setLabel("PCMWPMACTIONSIGNATRE: Signature");
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONSIGNATRELIST: List of Signature"));
      f.setDynamicLOV("EMPLOYEE_LOV","ITEM2_COMPANY COMPANY",600,445);
      f.setDefaultNotVisible();
      f.setUpperCase();

      f = itemblk2.addField("ITEM2_REMARK");
      f.setDbName("REMARK");
      f.setMandatory();
      f.setSize(30);
      f.setLabel("PCMWPMACTIONREMRK: Remark");
      f.setDefaultNotVisible();

      f = itemblk2.addField("ITEM2_PM_NO","Number","#");
      f.setSize(8);
      f.setHidden();
      f.setDbName("PM_NO");

      f = itemblk2.addField("ITEM2_PM_REVISION");
      f.setSize(8);
      f.setHidden();
      f.setDbName("PM_REVISION");

      f = itemblk2.addField("ITEM2_SEQ_NO","Number");   
      f.setSize(8);
      f.setHidden();
      f.setDbName("SEQ_NO");

      f = itemblk2.addField("ITEM2_MCH_CODE");
      f.setSize(8);
      f.setHidden();
      f.setFunction("PM_ACTION_CALENDAR_PLAN_API.Get_Mch_Code(:ITEM2_PM_NO,:ITEM2_PM_REVISION)");

      f = itemblk2.addField("ITEM2_MCH_DESC");
      f.setSize(8);
      f.setHidden();
      f.setFunction("PM_ACTION_CALENDAR_PLAN_API.Get_Mch_Desc(:ITEM2_PM_NO,:ITEM2_PM_REVISION)");

      f = itemblk2.addField("ITEM2_SITE");
      f.setSize(8);
      f.setHidden();
      f.setFunction("PM_ACTION_CALENDAR_PLAN_API.Get_Site(:ITEM2_PM_NO,:ITEM2_PM_REVISION)");

      f = itemblk2.addField("ITEM2_PM_TYPE");
      f.setSize(20);
      f.setHidden();
      f.setFunction("PM_ACTION_CALENDAR_PLAN_API.Get_PM_TYPE(:ITEM2_PM_NO,:ITEM2_PM_REVISION)");

      // 040114  ARWILK  Begin  (Enable Multirow RMB actions)
      f = itemblk2.addField("ITEM2_GENERATE");
      f.setHidden();  
      f.setFunction("Pm_Action_API.Is_Generateable__(:ITEM2_PM_NO,:ITEM2_PM_REVISION)");

      f = itemblk2.addField("ITEM2_CONECTED");
      f.setHidden();  
      f.setFunction("Pm_Action_Connection_API.Is_Connected(:ITEM2_PM_NO,:ITEM2_PM_REVISION)");

      f = itemblk2.addField("ITEM2_OBJSTATE");
      f.setHidden();  
      f.setFunction("Pm_Action_API.Get_Pm_State(:ITEM2_PM_NO,:ITEM2_PM_REVISION)");

      f = itemblk2.addField("ITEM2_PM_IS_VALID"); 
      f.setHidden();  
      f.setFunction("Pm_Action_API.Is_Valid_Pm(:ITEM2_PM_NO,:ITEM2_PM_REVISION)");

      f = itemblk2.addField("ITEM2_IS_GENERATABLE");
      f.setHidden();  
      f.setFunction("Pm_Action_Calendar_Plan_API.Get_Pm_Generateable(:ITEM2_PM_NO,:ITEM2_PM_REVISION,:ITEM2_SEQ_NO)");

      f = itemblk2.addField("ITEM2_HAS_WO");
      f.setHidden();  
      f.setFunction("Pm_Action_Calendar_Plan_API.Has_Wo_No(:ITEM2_PM_NO,:ITEM2_PM_REVISION,:ITEM2_SEQ_NO)");

      f = itemblk2.addField("ITEM2_HAS_COMPLETION_DATE");
      f.setHidden();  
      f.setFunction("Pm_Action_Calendar_Plan_API.Has_Completion_Date(:ITEM2_PM_NO,:ITEM2_PM_REVISION,:ITEM2_SEQ_NO)");
      // 040114  ARWILK  End  (Enable Multirow RMB actions)

      f = itemblk2.addField("JOBS_EXIST"); 
      f.setHidden();  
      f.setFunction("Pm_Action_Calendar_Job_API.Jobs_Exist(:ITEM2_PM_NO,:ITEM2_PM_REVISION,:ITEM2_SEQ_NO)");

      f = itemblk2.addField("ITEM2_PM_PLAN_OPERATION"); 
      f.setHidden();  
      f.setFunction("Maintenance_Configuration_API.Get_Pm_Plan_Operation");

      itemblk2.setView("PM_ACTION_CALENDAR_PLAN");
      itemblk2.defineCommand("PM_ACTION_CALENDAR_PLAN_API","New__,Modify__,Remove__");
      itemblk2.setMasterBlock(headblk);

      itemset2 = itemblk2.getASPRowSet();
      itembar2= mgr.newASPCommandBar(itemblk2);

      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.setWrap();
      // 040114  ARWILK  Begin  (Enable Multirow RMB actions)
      itemtbl2.enableRowSelect();
      // 040114  ARWILK  End  (Enable Multirow RMB actions)

      itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");
      itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
      itembar2.defineCommand(itembar2.NEWROW,"newRowITEM2");
      itembar2.enableCommand(itembar2.FIND);

      itembar2.defineCommand(itembar2.SAVERETURN ,null,"checkItem2Fields(-1)");
      itembar2.defineCommand(itembar2.SAVENEW ,null,"checkItem2Fields(-1)");

      itembar2.addCustomCommand("generateWorkOrder2",mgr.translate("PCMWPMACTIONCCND1: Generate Work Order"));
      itembar2.addCustomCommand("prepWorkOrder",mgr.translate("PCMWPMACTIONCCND2: Prepare Work Order..."));
      itembar2.addCustomCommand("viewHistWorkOrder",mgr.translate("PCMWPMACTIONCCND4: View Historical Work Order..."));
      itembar2.addCustomCommand("viewGenDetails",mgr.translate("PCMWPMACTIONCCND5: View Generation Details..."));
      itembar2.addCustomCommand("jobsAndOperations",mgr.translate("PCMWPMACTIONCCND6: Jobs and Operations..."));

      // 040114  ARWILK  Begin  (Enable Multirow RMB actions)
      itembar2.addCommandValidConditions("generateWorkOrder2",    "ITEM2_GENERATE",  "Enable",  "TRUE");
      itembar2.appendCommandValidConditions("generateWorkOrder2", "ITEM2_CONECTED",  "Disable", "TRUE");
      itembar2.appendCommandValidConditions("generateWorkOrder2", "ITEM2_IS_GENERATABLE",  "Enable", "Y");
      itembar2.appendCommandValidConditions("generateWorkOrder2",    "ITEM2_HAS_WO",     "Disable", "TRUE");
      itembar2.appendCommandValidConditions("generateWorkOrder2", "ITEM2_OBJSTATE",       "Enable", "Active");
      itembar2.appendCommandValidConditions("generateWorkOrder2", "ITEM2_PM_IS_VALID",    "Disable", "FALSE");

      itembar2.addCommandValidConditions("prepWorkOrder",         "ITEM2_HAS_WO",     "Enable", "TRUE");
      itembar2.appendCommandValidConditions("prepWorkOrder",      "ITEM2_HAS_COMPLETION_DATE",  "FALSE");
      itembar2.appendCommandValidConditions("prepWorkOrder",      "ITEM2_PM_TYPE",    "ActiveSeparate");

      itembar2.addCommandValidConditions("viewHistWorkOrder",     "ITEM2_HAS_WO",     "Enable", "TRUE");
      itembar2.appendCommandValidConditions("viewHistWorkOrder",     "ITEM2_HAS_COMPLETION_DATE",     "TRUE");

      itembar2.addCommandValidConditions("viewGenDetails",        "WO_NO",            "Disable", "null");

      itembar2.enableMultirowAction();
      itembar2.removeFromMultirowAction("viewGenDetails");
      // 040114  ARWILK  End  (Enable Multirow RMB actions)
      itembar2.addCommandValidConditions("jobsAndOperations",  "ITEM2_PM_PLAN_OPERATION",  "Disable", "FALSE");
      itembar2.appendCommandValidConditions("jobsAndOperations",  "JOBS_EXIST",      "Disable", "FALSE");
      itembar2.removeFromMultirowAction("jobsAndOperations");

      itemlay2 = itemblk2.getASPBlockLayout();
      itemlay2.setDialogColumns(2);
      itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);

      //================================================================
      // Permit
      //================================================================ 

      itemblk4 = mgr.newASPBlock("ITEM4");

      f = itemblk4.addField("ITEM4_OBJID");
      f.setHidden();
      f.setDbName("OBJID");       

      f = itemblk4.addField("ITEM4_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk4.addField("ITEM4_PM_NO","Number","#");
      f.setSize(8);
      f.setMandatory();
      f.setHidden();
      f.setLabel("PCMWPMACTIONIT4PMNO: PM No");
      f.setDbName("PM_NO");

      f = itemblk4.addField("ITEM4_PM_REVISION");
      f.setSize(8);
      f.setMandatory();
      f.setHidden();
      f.setDbName("PM_REVISION");

      f = itemblk4.addField("PERMIT_TYPE_ID");
      f.setSize(10);
      f.setDynamicLOV("PERMIT_TYPE",600,445);
      f.setMandatory();
      f.setLabel("PCMWPMACTIONPERTYPEID: Permit Type");
      f.setUpperCase();
      f.setMaxLength(4);
      f.setReadOnly();
      f.setInsertable();

      f = itemblk4.addField("PERMITTYPEDESCRIPTION");
      f.setSize(51);
      f.setLabel("PCMWPMACTIONPERTYPEDESC: Description");
      f.setFunction("PERMIT_TYPE_API.Get_Description(:PERMIT_TYPE_ID)");
      mgr.getASPField("PERMIT_TYPE_ID").setValidation("PERMITTYPEDESCRIPTION");
      f.setReadOnly();

      f = itemblk4.addField("DELIMITATION_ID","Number");
      f.setSize(16);
      f.setDynamicLOV("CONN_DELIMITATION",600,445);
      f.setLabel("PCMWPMACTIONITEM4ISOLATION: Isolation ID");

      f = itemblk4.addField("ITEM4_GENERATE");
      f.setDbName("GENERATE");
      f.setSize(10);
      f.setLabel("PCMWPERMITENERATE: Generate");
      f.setCheckBox("FALSE,TRUE");

      itemblk4.setView("PM_ACTION_PERMIT");
      // 040114  ARWILK  Begin  (Enhance Performance)
      itemblk4.defineCommand("PM_ACTION_PERMIT_API","New__,Modify__,Remove__");
      // 040114  ARWILK  End  (Enhance Performance)
      itemblk4.setMasterBlock(headblk);

      itemset4 = itemblk4.getASPRowSet();

      itembar4 = mgr.newASPCommandBar(itemblk4);

      itemtbl4 = mgr.newASPTable(itemblk4);
      itemtbl4.setWrap();
      // 040114  ARWILK  Begin  (Enable Multirow RMB actions)
      itemtbl4.enableRowSelect();
      // 040114  ARWILK  End  (Enable Multirow RMB actions)

      itembar4.defineCommand(itembar4.SAVERETURN,null,"checkItem4Fields(-1)");
      itembar4.defineCommand(itembar4.SAVENEW,null,"checkItem4Fields(-1)");   
      itembar4.defineCommand(itembar4.OKFIND,"okFindITEM4");
      itembar4.defineCommand(itembar4.COUNTFIND,"countFindITEM4");
      itembar4.defineCommand(itembar4.NEWROW,"newRowITEM4");
      itembar4.enableCommand(itembar4.FIND);

      itembar4.addCustomCommand("attributes",mgr.translate("PCMWPMACTIONCDN111: Attributes..."));

      // 040114  ARWILK  Begin  (Enable Multirow RMB actions)
      itembar4.enableMultirowAction();
      // 040114  ARWILK  End  (Enable Multirow RMB actions)

      // 040114  ARWILK  Begin  (Enhance Performance)
      // itembar4.disableCommand(itembar4.EDIT);
      // 040114  ARWILK  End  (Enhance Performance)

      itemlay4 = itemblk4.getASPBlockLayout();
      itemlay4.setDialogColumns(2);
      itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT);

      //================================================================
      // Budget
      //================================================================= 

      itemblk6 = mgr.newASPBlock("ITEM6");

      f = itemblk6.addField("ITEM6_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk6.addField("ITEM6_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk6.addField("ITEM6_PM_NO");   
      f.setHidden();
      f.setDbName("PM_NO");

      f = itemblk6.addField("ITEM6_PM_REVISION");
      f.setSize(8);
      f.setHidden();
      f.setDbName("PM_REVISION");

      f = itemblk6.addField("PERS_BUDGET_COST","Money");   
      f.setSize(14);
      f.setCustomValidation("PERS_BUDGET_COST,MAT_BUDGET_COST,EXT_BUDGET_COST,TF_BUDGET_COST,EXP_BUDGET_COST,FIX_BUDGET_COST", "TOT_BUDGET_COST");
      f.setDefaultNotVisible();

      f = itemblk6.addField("MAT_BUDGET_COST","Money");   
      f.setSize(14);
      f.setCustomValidation("PERS_BUDGET_COST,MAT_BUDGET_COST,EXT_BUDGET_COST,TF_BUDGET_COST,EXP_BUDGET_COST,FIX_BUDGET_COST", "TOT_BUDGET_COST");
      f.setDefaultNotVisible();

      f = itemblk6.addField("TF_BUDGET_COST","Money");   
      f.setSize(14);
      f.setCustomValidation("PERS_BUDGET_COST,MAT_BUDGET_COST,EXT_BUDGET_COST,TF_BUDGET_COST,EXP_BUDGET_COST,FIX_BUDGET_COST", "TOT_BUDGET_COST");
      f.setDefaultNotVisible();

      f = itemblk6.addField("EXT_BUDGET_COST","Money");   
      f.setSize(14);
      f.setCustomValidation("PERS_BUDGET_COST,MAT_BUDGET_COST,EXT_BUDGET_COST,TF_BUDGET_COST,EXP_BUDGET_COST,FIX_BUDGET_COST", "TOT_BUDGET_COST");
      f.setDefaultNotVisible();

      f = itemblk6.addField("EXP_BUDGET_COST","Money");   
      f.setSize(14);
      f.setCustomValidation("PERS_BUDGET_COST,MAT_BUDGET_COST,EXT_BUDGET_COST,TF_BUDGET_COST,EXP_BUDGET_COST,FIX_BUDGET_COST", "TOT_BUDGET_COST");
      f.setDefaultNotVisible();

      f = itemblk6.addField("FIX_BUDGET_COST","Money");   
      f.setSize(14);
      f.setCustomValidation("PERS_BUDGET_COST,MAT_BUDGET_COST,EXT_BUDGET_COST,TF_BUDGET_COST,EXP_BUDGET_COST,FIX_BUDGET_COST", "TOT_BUDGET_COST");
      f.setDefaultNotVisible();

      f = itemblk6.addField("TOT_BUDGET_COST","Money");   
      f.setFunction("nvl(PERS_BUDGET_COST,0)+nvl(MAT_BUDGET_COST,0)+nvl(EXT_BUDGET_COST,0)+nvl(TF_BUDGET_COST,0)+nvl(EXP_BUDGET_COST,0)+nvl(FIX_BUDGET_COST,0)");
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk6.addField("PLAN_PERS_COST","Money"); 
      f.setFunction("PM_Action_Role_API.Calc_Plan_Personnel_Cost__(:ITEM6_PM_NO,:ITEM6_PM_REVISION,0)");  
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk6.addField("PLAN_MAT_COST","Money");   
      f.setFunction("''");
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk6.addField("PLAN_TOOL_COST","Money");   
      f.setFunction("''");
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk6.addField("PLAN_EXT_COST","Money");   
      f.setFunction("''");
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk6.addField("PLAN_EXP_COST","Money");   
      f.setFunction("''");
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk6.addField("PLAN_FIX_COST","Money");   
      f.setFunction("''");
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk6.addField("TOT_PLAN_COST","Money");   
      f.setFunction("''");
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk6.addField("PERS_COST_LAST_WO","Money");   
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk6.addField("MAT_COST_LAST_WO","Money");   
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk6.addField("TF_COST_LAST_WO","Money");   
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk6.addField("EXT_COST_LAST_WO","Money");   
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk6.addField("EXP_COST_LAST_WO","Money");   
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk6.addField("FIX_COST_LAST_WO","Money");   
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk6.addField("TOT_COST_LAST_WO","Money");   
      f.setFunction("nvl(PERS_COST_LAST_WO,0)+nvl(MAT_COST_LAST_WO,0)+nvl(EXT_COST_LAST_WO,0)+nvl(EXP_COST_LAST_WO,0)+nvl(FIX_COST_LAST_WO,0)");
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk6.addField("PLAN_PERS_REV","Money"); 
      f.setFunction("''");
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk6.addField("PLAN_MAT_REV","Money");   
      f.setFunction("''");
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk6.addField("PLAN_TOOL_REV","Money");   
      f.setFunction("''");
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk6.addField("PLAN_EXT_REV","Money");   
      f.setFunction("''");
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk6.addField("PLAN_EXP_REV","Money");   
      f.setFunction("''");
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk6.addField("PLAN_FIX_REV","Money");   
      f.setFunction("''");
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk6.addField("TOT_PLAN_REV","Money"); 
      f.setFunction("''");
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      itemblk6.setView("PM_ACTION_BUDGET");
      itemblk6.defineCommand("PM_ACTION_BUDGET_API","Modify__");
      itemblk6.setMasterBlock(headblk);

      itemset6 = itemblk6.getASPRowSet();
      itembar6 = mgr.newASPCommandBar(itemblk6);
      itemtbl6 = mgr.newASPTable(itemblk6);
      itemtbl6.setWrap();

      itembar6.defineCommand(itembar6.OKFIND,"okFindITEM6");
      itembar6.defineCommand(itembar6.COUNTFIND,"countFindITEM6");
      itembar6.enableCommand(itembar6.EDIT);

      itemlay6 = itemblk6.getASPBlockLayout();
      itemlay6.setDialogColumns(1);
      itemlay6.setDefaultLayoutMode(itemlay6.SINGLE_LAYOUT);

      // -------------------------------------------------------------------------------------------------------
      // -------------------------------------------------------------------------------------------------------
      // -------------------------------------------------------------------------------------------------------

      itemblk7 = mgr.newASPBlock("ITEM7");

      itemblk7.addField("ITEM7_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk7.addField("ITEM7_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      itemblk7.addField("ITEM7_PM_NO","Number").
      setDbName("PM_NO").
      setMandatory().
      setHidden();

      itemblk7.addField("ITEM7_PM_REVISION").
      setDbName("PM_REVISION").
      setMandatory().
      setHidden();

      itemblk7.addField("JOB_ID").
      setSize(20).
      setReadOnly().
      setInsertable().
      setLabel("PCMWPMACTIONITEM7JOB_ID: Job ID").
      setMaxLength(20);

      f = itemblk7.addField("STD_JOB_ID");
      f.setSize(20);
      f.setLOV("SeparateStandardJobLov.page",600,445);
      f.setLabel("PCMWPMACTIONITEM7STDJOB_ID: Standard Job ID");
      f.setCustomValidation("STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION","STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION,ITEM7_DESCRIPTION,STD_JOB_STATUS");
      f.setMaxLength(12);
      f.setUpperCase();

      itemblk7.addField("STD_JOB_CONTRACT").
      setSize(20).
      setReadOnly().
      setLabel("PCMWPMACTIONITEM7STDJOB_CONTRACT: Site").
      setMaxLength(5).
      setUpperCase();

      f = itemblk7.addField("STD_JOB_REVISION");
      f.setSize(20);
      f.setDynamicLOV("SEPARATE_STANDARD_JOB_LOV","STD_JOB_ID,STD_JOB_CONTRACT CONTRACT",600,445);
      f.setLabel("PCMWPMACTIONITEM7STDJOB_REV: Revision");
      f.setCustomValidation("STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION","ITEM7_DESCRIPTION,STD_JOB_STATUS");
      f.setMaxLength(6); 
      f.setUpperCase();

      itemblk7.addField("ITEM7_DESCRIPTION").
      setSize(70).
      setDbName("DESCRIPTION").
      setMandatory().
      setLabel("PCMWPMACTIONITEM7STDDESC: Description").
      setMaxLength(20);

      itemblk7.addField("QTY","Number").
      setSize(12).
      setLabel("PCMWPMACTIONITEM7QTY: Quantity");

      itemblk7.addField("ITEM7_PLAN_HRS","Number").
      setFunction("Pm_Action_Job_API.Get_Total_Plan_Hours(:ITEM7_PM_NO,:ITEM7_PM_REVISION,:JOB_ID)").
      setReadOnly().
      setLabel("PCMWPMACTIONITEM7PHRS: Planned Hours");

      itemblk7.addField("ITEM7_COMPANY").
      setDbName("COMPANY").
      setHidden(); 

      itemblk7.addField("STD_JOB_STATUS").
      setLabel("PCMWPMACTROUITEM6STDJOBSTATUS: Std Job Status").
      setFunction("Standard_Job_API.Get_Status(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)").
      setReadOnly();

      itemblk7.addField("JOB_PROG_ID").
      setLabel("PCMWPMACTROUITEM6JOBPROGID: Job Program ID").
      setQueryable().
      setSize(20).
      setReadOnly();

      itemblk7.addField("JOB_PROG_REV").
      setLabel("PCMWPMACTROUITEM6JOBPROGREV: Job Program Revision").
      setQueryable().
      setSize(20).
      setReadOnly();

      itemblk7.addField("ITEM7_SIGNATURE").
      setSize(20).
      setDbName("SIGNATURE").
      setLabel("PCMWPMACTIONEXECUTEDBY: Executed By").
      setLOV("../mscomw/MaintEmployeeLov.page","ITEM7_COMPANY COMPANY",600,450).
      setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONSIGN: List of Signature")).
      setMaxLength(20).
      setCustomValidation("ITEM7_SIGNATURE,ITEM7_COMPANY","EMPLOYEE_ID,ITEM7_SIGNATURE").
      setUpperCase();

      itemblk7.addField("EMPLOYEE_ID").
      setSize(40).
      setLabel("PCMWPMACTIONITEM7_SIGN_ID: Employee ID").
      setReadOnly();    

      itemblk7.addField("ROLES_EXIST","Number").
      setFunction("''").
      setHidden();

      itemblk7.addField("MATERIAL_EXIST","Number").
      setFunction("''").
      setHidden();

      itemblk7.addField("TOOL_FACILITY_EXIST","Number").
      setFunction("''").
      setHidden();

      itemblk7.addField("DOC_EXIST","Number").
      setFunction("''").
      setHidden();

      itemblk7.addField("ITEM7_TEMP").
      setFunction("''").
      setHidden();  

      itemblk7.addField("ITEM7_PM_PLAN_OPERATION"). 
      setHidden().  
      setFunction("Maintenance_Configuration_API.Get_Pm_Plan_Operation");

      itemblk7.addField("ITEM7_OLD_EMP"). 
      setHidden().  
      setFunction("Pm_Action_Job_API.Get_Employee_Id(:ITEM7_PM_NO,:ITEM7_PM_REVISION,:JOB_ID)");

      itemblk7.addField("ITEM7_JOB_EXIST"). 
      setHidden().  
      setFunction("Pm_Action_Calendar_Job_API.Job_Plan_Exist(:ITEM7_PM_NO,:ITEM7_PM_REVISION,:JOB_ID)");

      itemblk7.setView("PM_ACTION_JOB");
      itemblk7.defineCommand("PM_ACTION_JOB_API","New__,Modify__,Remove__");
      itemblk7.setMasterBlock(headblk);
      itemset7 = itemblk7.getASPRowSet();

      itembar7 = mgr.newASPCommandBar(itemblk7);
      itembar7.defineCommand(itembar7.OKFIND,"okFindITEM7");
      itembar7.defineCommand(itembar7.COUNTFIND,"countFindITEM7");
      itembar7.defineCommand(itembar7.NEWROW,"newRowITEM7");
      itembar7.defineCommand(itembar7.DUPLICATEROW,"duplicateITEM7");
      itembar7.defineCommand(itembar7.DELETE,"deleteITEM7");

      itembar7.defineCommand(itembar7.SAVENEW,null,"checkItem7Fields(-1)");
      itembar7.defineCommand(itembar7.SAVERETURN,null,"checkJobSignature();checkConnections()");   
      itembar7.enableCommand(itembar7.FIND);

      itemtbl7 = mgr.newASPTable(itemblk7);
      itemtbl7.setTitle(mgr.translate("PCMWPMACTIONITEM7ITM: Jobs"));
      itemtbl7.setWrap();

      itemlay7 = itemblk7.getASPBlockLayout();
      itemlay7.setDefaultLayoutMode(itemlay7.MULTIROW_LAYOUT);

      //-------------------------------------------------------
      //   Block refers to PLANNING tab
      //-------------------------------------------------------

      itemblk8 = mgr.newASPBlock("ITEM8");

      f = itemblk8.addField("ITEM8_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk8.addField("ITEM8_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk8.addField("ITEM8_PM_NO");
      f.setHidden();
      f.setDbName("PM_NO");   

      f = itemblk8.addField("ITEM8_PM_REVISION");
      f.setHidden();
      f.setDbName("PM_REVISION");

      f = itemblk8.addField("PLAN_LINE_NO","Number");
      f.setSize(10);
      f.setLabel("PCMWSEPPMACTIONPLANLINENO: Plan Line No");
      f.setReadOnly();
      f.setInsertable();

      f = itemblk8.addField("WORK_ORDER_COST_TYPE");
      f.setSize(22);
      f.setMandatory();
      f.enumerateValues("WORK_ORDER_COST_TYPE_API");
      f.setLabel("PCMWSEPPMACTIONWOORCOTY: Work Order Cost Type");
      f.setSelectBox();
      f.unsetSearchOnDbColumn();

      f = itemblk8.addField("ITEM8_WORK_ORDER_INVOICE_TYPE");
      f.setDbName("WORK_ORDER_INVOICE_TYPE");
      f.setLabel("PCMWSEPPMACTIONWOORINTY: Work Order Invoice Type");
      f.setSize(25);
      f.setSelectBox();
      f.enumerateValues("WORK_ORDER_INVOICE_TYPE_API");
      f.setMandatory();
      f.unsetSearchOnDbColumn();
      f.setCustomValidation("ITEM8_WORK_ORDER_INVOICE_TYPE,ITEM8_DISCOUNT,ITEM8_SALES_PRICE,QUANTITY,ITEM8_QTY_TO_INVOICE","ITEM8_PRICE_AMOUNT");

      f = itemblk8.addField("QUANTITY","Number");
      f.setSize(18);
      f.setLabel("PCMWSEPPMACTIONPLAQTY: Planned Quantity");
      f.setCustomValidation("ITEM8_WORK_ORDER_INVOICE_TYPE,ITEM8_DISCOUNT,ITEM8_SALES_PRICE,QUANTITY,ITEM8_QTY_TO_INVOICE","ITEM8_PRICE_AMOUNT");

      f = itemblk8.addField("COST","Number");
      f.setSize(15);
      f.setLabel("PCMWSEPPMACTIONPLACO: Planned Cost"); 
      f.setCustomValidation("ITEM8_WORK_ORDER_INVOICE_TYPE,ITEM8_DISCOUNT,ITEM8_SALES_PRICE,QUANTITY,ITEM8_QTY_TO_INVOICE,WORK_ORDER_COST_TYPE,ITEM8_CATALOG_NO","ITEM8_PRICE_AMOUNT,QUANTITY");

      f = itemblk8.addField("ITEM8_QTY_TO_INVOICE","Number");
      f.setDbName("QTY_TO_INVOICE");
      f.setCustomValidation("ITEM8_WORK_ORDER_INVOICE_TYPE,ITEM8_DISCOUNT,ITEM8_SALES_PRICE,QUANTITY,ITEM8_QTY_TO_INVOICE,ITEM8_CATALOG_CONTRACT,ITEM8_CATALOG_NO,ITEM8_DISCOUNT,ITEM8_PM_NO,ITEM8_PM_REVISION","ITEM8_SALES_PRICE,ITEM8_DISCOUNT,ITEM8_PRICE_AMOUNT");
      f.setSize(18);
      f.setLabel("PCMWSEPPMACTIONQTYTINV: Qty To Invoice");

      f = itemblk8.addField("ITEM8_CATALOG_CONTRACT");
      f.setSize(20);
      f.setDbName("CATALOG_CONTRACT");
      f.setLabel("PCMWSEPPMACTIONCATCON: Sales Part Site");
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
      f.setCustomValidation("ITEM8_WORK_ORDER_INVOICE_TYPE,ITEM8_DISCOUNT,ITEM8_SALES_PRICE,QUANTITY,ITEM8_QTY_TO_INVOICE,ITEM8_CATALOG_CONTRACT,ITEM8_CATALOG_NO,ITEM8_DISCOUNT,ITEM8_PM_NO,ITEM8_PM_REVISION","ITEM8_SALES_PRICE,ITEM8_DISCOUNT,ITEM8_PRICE_AMOUNT");
      f.setLOVProperty("TITLE",mgr.translate("PCMWSEPPMACTIONLOVTITLE5: List of Sales Part Site"));

      f = itemblk8.addField("ITEM8_CATALOG_NO");
      f.setSize(15);
      f.setLabel("PCMWSEPPMACTIONCATNO: Sales Part");
      f.setDbName("CATALOG_NO");
      f.setDynamicLOV("SALES_PART_SERVICE_LOV","ITEM8_CATALOG_CONTRACT CONTRACT",600,450); 
      f.setUpperCase();
      f.setCustomValidation("ITEM8_WORK_ORDER_INVOICE_TYPE,ITEM8_DISCOUNT,ITEM8_SALES_PRICE,QUANTITY,ITEM8_QTY_TO_INVOICE,ITEM8_CATALOG_CONTRACT,ITEM8_CATALOG_NO,ITEM8_DISCOUNT,ITEM8_PM_NO,ITEM8_PM_REVISION,PLAN_LINE_NO,WORK_ORDER_COST_TYPE","COST,ITEM8_SALES_PRICE,ITEM8_DISCOUNT,ITEM8_PRICE_AMOUNT,ITEM8_SALES_PART_DESC,QUANTITY");

      f = itemblk8.addField("ITEM8_SALES_PART_DESC");
      f.setFunction("Sales_Part_API.Get_Catalog_Desc(:ITEM8_CATALOG_CONTRACT,:ITEM8_CATALOG_NO)");
      f.setSize(25);
      f.setReadOnly();
      f.setLabel("PCMWSEPPMACTIONSALESPARTDESC: Sales Part Description");

      f = itemblk8.addField("ITEM8_SALES_PRICE","Number");
      f.setDbName("SALES_PRICE");
      f.setSize(18);
      f.setLabel("PCMWSEPPMACTIONSAPR: Sales Price/Unit");
      f.setCustomValidation("ITEM8_WORK_ORDER_INVOICE_TYPE,ITEM8_DISCOUNT,ITEM8_SALES_PRICE,QUANTITY,ITEM8_QTY_TO_INVOICE","ITEM8_PRICE_AMOUNT");

      f = itemblk8.addField("ITEM8_PRICE_AMOUNT","Money");
      f.setLabel("PCMWSEPPMACTIONITEM8PRAMU: Sales Price/Amount");
      f.setReadOnly(); 
      f.setFunction("''");

      f = itemblk8.addField("ITEM8_DISCOUNT");
      f.setDbName("DISCOUNT");
      f.setSize(10);
      f.setLabel("PCMWSEPPMACTIONDISC: Discount");
      f.setCustomValidation("ITEM8_WORK_ORDER_INVOICE_TYPE,ITEM8_DISCOUNT,ITEM8_SALES_PRICE,QUANTITY,ITEM8_QTY_TO_INVOICE","ITEM8_PRICE_AMOUNT");

      f = itemblk8.addField("ITEM8_JOB_ID","Number");
      f.setDbName("JOB_ID");
      f.setDynamicLOV("PM_ACTION_JOB","ITEM8_PM_NO PM_NO,ITEM8_PM_REVISION PM_REVISION");
      f.setLabel("PCMWSEPPMACTIONITEM8JOBID: Job ID"); 

      f = itemblk8.addField("INVOICETYPE");
      f.setFunction("''");
      f.setHidden();

      f = itemblk8.addField("STRDUMMY");
      f.setFunction("''");
      f.setHidden();

      f = itemblk8.addField("DUMMY_SALE_UNIT_PRICE","Money");
      f.setFunction("0");
      f.setHidden();

      f = itemblk8.addField("DUMMY_BASE_UNIT_PRICE","Money");
      f.setFunction("0");
      f.setHidden();

      f = itemblk8.addField("DUMMY_CURRENCY_RATE","Money");
      f.setFunction("0");
      f.setHidden();

      f = itemblk8.addField("DUMMY_DISCOUNT","Money");
      f.setFunction("0");
      f.setHidden();

      f = itemblk8.addField("DUMMY_PRICE_SOURCE");
      f.setFunction("''");
      f.setHidden();

      f = itemblk8.addField("DUMMY_PRICE_SOURCE_ID");
      f.setFunction("''");
      f.setHidden();

      f = itemblk8.addField("DATEDUMMY","Datetime");
      f.setFunction("''");
      f.setHidden();

      f = itemblk8.addField("NUMDUMMY","Number");
      f.setFunction("0");
      f.setHidden();

      f = itemblk8.addField("IS_AUTO_LINE","Number");
      f.setFunction("Pm_Action_Planning_API.Is_Auto_External_Line(:ITEM8_PM_NO,:ITEM8_PM_REVISION,:PLAN_LINE_NO)");
      f.setHidden();

      itemblk8.setView("PM_ACTION_PLANNING");
      itemblk8.defineCommand("PM_ACTION_PLANNING_API","New__,Modify__,Remove__");
      itemblk8.setMasterBlock(headblk);

      itemset8 = itemblk8.getASPRowSet();

      itembar8 = mgr.newASPCommandBar(itemblk8);

      itembar8.enableCommand(itembar8.FIND);
      itembar8.defineCommand(itembar8.NEWROW,"newRowITEM8");
      itembar8.defineCommand(itembar8.COUNTFIND,"countFindITEM8");
      itembar8.defineCommand(itembar8.OKFIND,"okFindITEM8");

      itembar8.defineCommand(itembar8.SAVERETURN,"saveReturnITEM8","checkItem8Fields(-1)");       
      itembar8.defineCommand(itembar8.SAVENEW,"saveNewITEM8","checkItem8Fields(-1)");

      itemtbl8 = mgr.newASPTable(itemblk8);
      itemtbl8.setWrap();
      itemlay8 = itemblk8.getASPBlockLayout();
      itemlay8.setDefaultLayoutMode(itemlay8.MULTIROW_LAYOUT);


      //-------------------operations block under jobs tab---------------

      itemblk9 = mgr.newASPBlock("ITEM9");

      f = itemblk9.addField("ITEM9_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk9.addField("ITEM9_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk9.addField("ITEM9_PM_NO","Number","#");
      f.setSize(8);
      f.setMandatory();
      f.setHidden();
      f.setDbName("PM_NO");

      f = itemblk9.addField("ITEM9_PM_REVISION");
      f.setSize(8);
      f.setMandatory();
      f.setHidden();
      f.setDbName("PM_REVISION");

      f = itemblk9.addField("ITEM9_ROW_NO","Number","#");
      f.setDbName("ROW_NO");
      f.setSize(8);
      f.setMandatory();
      f.setLabel("PCMWPMACTIONITEM9ROWNO: Operation No");
      f.setReadOnly();
      f.setInsertable();

      f = itemblk9.addField("ITEM9_DESCRIPTION");
      f.setSize(25);
      f.setLabel("PCMWPMACTIONITEM9DESCRIPTION: Description");
      f.setDbName("DESCRIPTION");
      f.setMaxLength(60);

      f = itemblk9.addField("ITEM9_COMPANY");
      f.setDbName("COMPANY");
      f.setHidden();

      f = itemblk9.addField("ITEM9_SIGNATURE");
      f.setDbName("SIGNATURE");
      f.setSize(20);
      f.setLOV("../mscomw/MaintEmployeeLov.page","ITEM9_COMPANY COMPANY",600,450);
      f.setCustomValidation("ITEM9_COMPANY,ITEM9_SIGNATURE,ITEM9_ORG_CONTRACT,ITEM9_ROLE_CODE,ITEM9_ORG_CODE,ITEM9_CONTRACT,ITEM9_CATALOG_NO,ITEM9_SALESPARTDESCRIPTION,ITEM9_LISTPRICE","ITEM9_EMPLOYEE_ID,ITEM9_ROLE_CODE,ITEM9_ORG_CODE,ITEM9_SIGNATURE,ITEM9_ORG_CONTRACT,ITEM9_CATALOG_NO,ITEM9_SALESPARTDESCRIPTION,ITEM9_LISTPRICE");
      f.setLOVProperty("TITLE","PCMWPMACTIONLOVITEM9TITLE: List of Employee");
      f.setLabel("PCMWPMACTIONITEM9SIGNATURE: Executed By");
      f.setUpperCase();
      f.setMaxLength(20);

      f = itemblk9.addField("ITEM9_EMPLOYEE_ID");
      f.setSize(25);
      f.setUpperCase();
      f.setLabel("PCMWPMACTIONITEM9EMPID: Employee Id");
      f.setDbName("EMPLOYEE_ID");
      f.setReadOnly();

      f = itemblk9.addField("ITEM9_JOB_ID","Number","#");
      f.setDbName("JOB_ID");
      f.setSize(8);
      f.setHidden();
      f.setInsertable();

      f = itemblk9.addField("ITEM9_ORG_CODE");
      f.setSize(12);
      f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ITEM9_ORG_CONTRACT CONTRACT",600,445);
      f.setLabel("PCMWPMACTIONITEM9ORGCODE: Maintenance Organization");
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONORGCOD: List of Maintenance Organization"));
      f.setCustomValidation("ITEM9_ORG_CODE,ITEM9_ORG_CONTRACT,ITEM9_CATALOG_NO,ITEM9_CONTRACT,ITEM9_SIGNATURE,ITEM9_ROLE_CODE,ITEM9_CONTRACT","ITEM9_LISTPRICE,ITEM9_CATALOG_NO,ITEM9_SALESPARTDESCRIPTION,ITEM9_ORG_CODE,ITEM9_ORG_CONTRACT");
      f.setUpperCase();
      f.setDbName("ORG_CODE");
      f.setMaxLength(8);

      f = itemblk9.addField("ITEM9_ORG_CONTRACT");
      f.setSize(8);
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
      f.setLabel("PCMWPMACTIONITEM9ORGCONT: Organization Site");
      f.setUpperCase();
      f.setDbName("ORG_CONTRACT");
      f.setMaxLength(5);

      f = itemblk9.addField("ITEM9_ROLE_CODE");
      f.setDbName("ROLE_CODE");
      f.setSize(8);
      f.setDynamicLOV("ROLE_TO_SITE_LOV","ITEM9_CONTRACT CONTRACT",600,445);
      f.setLabel("PCMWPMACTIONITEM9ROLECODE: Craft");
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONROLECOD: List of Craft"));
      f.setCustomValidation("ITEM9_ROLE_CODE,ITEM9_CATALOG_NO,ITEM9_CONTRACT,ITEM9_SIGNATURE,ITEM9_ORG_CONTRACT,ITEM9_ORG_CODE","ITEM9_LISTPRICE,ITEM9_CATALOG_NO,ITEM9_ORG_CONTRACT,ITEM9_SALESPARTDESCRIPTION,ITEM9_ROLE_CODE");
      f.setUpperCase();
      f.setMaxLength(10);

      f = itemblk9.addField("ITEM9_PLAN_MEN","Number");
      f.setDbName("PLAN_MEN");
      f.setSize(13);
      f.setLabel("PCMWPMACTIONITEM9PLANMEN: Planned Men");

      f = itemblk9.addField("ITEM9_PLAN_HRS","Number");
      f.setSize(13);
      f.setLabel("PCMWPMACTIONITEM9PLANHRS: Planned Hours");
      f.setDbName("PLAN_HRS");

      f = itemblk9.addField("ITEM9_TEAM_CONTRACT");
      f.setSize(7);
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
      f.setLabel("PCMWPMACTIONITEM9CONT: Team Site");
      f.setMaxLength(5);
      f.setDbName("TEAM_CONTRACT");
      f.setUpperCase();

      f = itemblk9.addField("ITEM9_TEAM_ID");
      f.setCustomValidation("ITEM9_TEAM_ID,ITEM9_TEAM_CONTRACT","ITEM9_TEAMDESC");
      f.setSize(13);
      f.setDynamicLOV("MAINT_TEAM","ITEM9_TEAM_CONTRACT TEAM_CONTRACT",600,450);
      f.setDbName("TEAM_ID");
      f.setMaxLength(20);
      f.setInsertable();
      f.setLabel("PCMWPMACTIONITEM9TID: Team ID");
      f.setUpperCase();

      f = itemblk9.addField("ITEM9_TEAMDESC");
      f.setFunction("Maint_Team_API.Get_Description(:ITEM9_TEAM_ID,:ITEM9_TEAM_CONTRACT)");
      f.setSize(40);
      f.setMaxLength(200);
      f.setLabel("PCMWPMACTIONITEM9DESC: Description");
      f.setReadOnly();

      f = itemblk9.addField("ITEM9_REMARK");
      f.setDbName("REMARK");
      f.setSize(40);
      f.setLabel("PCMWPMACTIONITEM9REMARK: Remark");
      f.setDefaultNotVisible();
      f.setMaxLength(2000);
      f.setHeight(4);

      f = itemblk9.addField("ITEM9_CONTRACT");
      f.setSize(17);
      f.setLabel("PCMWPMACTIONITEM9SALESPARTCONT: Sales Part Site");
      f.setDbName("CONTRACT");
      f.setMaxLength(5);
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONSPARTSITE: List of Sales Part Site"));
      f.setUpperCase();
      f.setReadOnly();

      f = itemblk9.addField("ITEM9_CATALOG_NO");
      f.setSize(12);
      f.setLabel("PCMWPMACTIONITEM9CATALOGNO: Sales Part");
      f.setDbName("CATALOG_NO");
      f.setMaxLength(25);
      f.setDynamicLOV("SALES_PART","ITEM9_CONTRACT CONTRACT",600,445);
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONLISTSPART: List of Sales Part"));
      f.setUpperCase();
      f.setCustomValidation("ITEM9_CATALOG_NO,ITEM9_CONTRACT","ITEM9_SALESPARTDESCRIPTION,ITEM9_LISTPRICE");

      f = itemblk9.addField("ITEM9_SALESPARTDESCRIPTION");
      f.setSize(25);
      f.setLabel("PCMWPMACTIONITEM9SALESPART: Sales Part Description ");
      if (mgr.isModuleInstalled("ORDER"))
         f.setFunction("SALES_PART_API.Get_Catalog_Desc(:ITEM9_CONTRACT,:ITEM9_CATALOG_NO)");
      else
         f.setFunction("''");
      f.setReadOnly();

      f = itemblk9.addField("ITEM9_LISTPRICE");
      f.setSize(11);
      f.setLabel("PCMWPMACTIONITEM9LISTPRICE: List Price");
      if (mgr.isModuleInstalled("ORDER"))
         f.setFunction("SALES_PART_API.Get_List_Price(:ITEM9_CONTRACT,:ITEM9_CATALOG_NO)");
      else
         f.setFunction("''");
      f.setReadOnly();

      itemblk9.addField("ITEM9_PREDECESSOR").
      setSize(22).
      setLabel("PCMWPMACTIONITEM9PRED: Predecessors").
      setLOV("ConnectPredecessorsDlg.page",600,450).
      setFunction("Pm_Role_Dependencies_API.Get_Predecessors(:ITEM9_PM_NO,:ITEM9_PM_REVISION,:ITEM9_ROW_NO)");

      f = itemblk9.addField("ITEM9_TOOLS");
      f.setDbName("TOOLS");
      f.setSize(8);
      f.setLabel("PCMWPMACTIONITEM9TOOLS: Tools");
      f.setMaxLength(10); 

      itemblk9.addField("ITEM9_CBADDQUALIFICATION").
      setLabel("PCMWPMACTIONITEM9CBADDQUAL1: Additional Qualifications").
      setFunction("Pm_Action_Role_API.Check_Qualifications_Exist(:ITEM9_PM_NO,:ITEM9_PM_REVISION,:ITEM9_ROW_NO)").
      setCheckBox("0,1").
      setReadOnly().
      setQueryable();


      itemblk9.setView("PM_ACTION_ROLE");
      itemblk9.defineCommand("PM_ACTION_ROLE_API","New__,Modify__,Remove__");
      itemblk9.setMasterBlock(itemblk7);

      itemset9 = itemblk9.getASPRowSet();

      itembar9= mgr.newASPCommandBar(itemblk9 );
      itembar9.defineCommand(itembar9.SAVERETURN,null,"checkItem9Fields(-1)");
      itembar9.defineCommand(itembar9.SAVENEW,null,"checkItem9Fields(-1)");
      itembar9.enableMultirowAction();

      itembar9.addCustomCommand("connectExistingOperations",mgr.translate("PCMWPMACTIONCONEXISTINGOPER: Connect Existing Operation..."));
      itembar9.addCustomCommand("disconnectOperations",mgr.translate("PCMWPMACTIONDISCONEXISTINGOPER: Disconnect Operation..."));
      itembar9.addCustomCommand("additionalQualifications2",mgr.translate("PCMWPMACTIONADDQUALIFICATIONS1: Additional Qualifications..."));
      itembar9.addCustomCommand("predecessors2",mgr.translate("PCMWPMACTIONPREDECESSORS2: Predecessors..."));
      itembar9.forceEnableMultiActionCommand("connectExistingOperations");
      itembar9.removeFromMultirowAction("additionalQualifications2");

      itemtbl9 = mgr.newASPTable(itemblk9 );
      itemtbl9.setWrap();
      itemtbl9.enableRowSelect();

      itembar9.defineCommand(itembar9.OKFIND,"okFindITEM9");
      itembar9.defineCommand(itembar9.COUNTFIND,"countFindITEM9");
      itembar9.defineCommand(itembar9.NEWROW,"newRowITEM9");
      itembar9.enableCommand(itembar9.FIND);
      itemtbl9.enableRowSelect();

      itemlay9 = itemblk9.getASPBlockLayout();
      itemlay9.setDialogColumns(2);
      itemlay9.setDefaultLayoutMode(itemlay9.MULTIROW_LAYOUT);


      //================================================================= 

      headblk1 = mgr.newASPBlock("HEAD1");
      headblk1.addField("WO_NO1");
      headblk1.addField("DNULL");
      headblk1.addField("SNULL");
      headblk1.addField("GEN_ID1");
      headblk1.addField("GNTYPE");
      headblk1.addField("PSDATE");
      headblk1.addField("PFDATE");
      headblk1.addField("HSEQ_NO","Number");   

      //mgr.endASPEvent();
   }



   public void securityChk()
   {
      ASPManager mgr = getASPManager();

      if (!varSec)
      {
         trans.clear();

         trans.addSecurityQuery("PERMIT_TYPE,EQUIPMENT_OBJECT_ADDRESS,MAINTENANCE_OBJECT,EQUIPMENT_SPARE_STRUC_DISTINCT,INVENTORY_PART_CONFIG,INVENTORY_PART,PURCHASE_PART_SUPPLIER,EQUIPMENT_OBJECT,ACTIVE_SEPARATE,ACTIVE_ROUND,HISTORICAL_SEPARATE,COMPETENCY_GROUP_LOV1");
         trans.addSecurityQuery("Pm_Action_API","Generate__");
         trans.addPresentationObjectQuery("PCMW/RMBPmAction.page,PCMW/Replacements.page,PCMW/PermitTypeRMB.page,PCMW/CopyPmActions.page,PCMW/CreatePmRevisionDlg.page,PCMW/JobsOperationsDlg.page,EQUIPW/EquipmentObjectAddress1.page,PCMW/MaintenanceObject.page,EQUIPW/EquipmentSpareStructure2.page,INVENW/InventoryPartInventoryPartCurrentOnHand.page,INVENW/InventoryPartAvailabilityPlanningQry.page,INVENW/InventoryPart.page,PURCHW/PurchasePartSupplier.page,EQUIPW/RMBEquipmentObject.page,EQUIPW/RMBMaintenanceObject.page,PCMW/ActiveSeparate2.page,PCMW/ActiveRound.page,PCMW/HistoricalSeparateRMB.page,PCMW/ViewGenerationDlg.page");

         trans = mgr.perform(trans);

         SecBuff = trans.getSecurityInfo();

         if (SecBuff.namedItemExists("PCMW/RMBPmAction.page"))
            secConn = true;
         if (SecBuff.namedItemExists("PCMW/Replacements.page"))
            secRep = true;
         if (SecBuff.namedItemExists("PCMW/CopyPmActions.page"))
            secCopy = true;
         if (SecBuff.namedItemExists("PCMW/CreatePmRevisionDlg.page"))
            secRev = true;
         if (SecBuff.itemExists("EQUIPMENT_OBJECT_ADDRESS") && SecBuff.namedItemExists("EQUIPW/EquipmentObjectAddress1.page"))
            secSearchObjAdd = true;
         if (SecBuff.itemExists("MAINTENANCE_OBJECT") && SecBuff.namedItemExists("PCMW/MaintenanceObject.page"))
            secSpInObj = true;
         if (SecBuff.itemExists("EQUIPMENT_SPARE_STRUC_DISTINCT") && SecBuff.namedItemExists("EQUIPW/EquipmentSpareStructure2.page"))
            secSpInDsp = true;
         if (SecBuff.itemExists("INVENTORY_PART") && SecBuff.namedItemExists("INVENW/InventoryPartInventoryPartCurrentOnHand.page"))
            secCurrQty = true;
         if (SecBuff.itemExists("INVENTORY_PART_CONFIG") && SecBuff.namedItemExists("INVENW/InventoryPartAvailabilityPlanningQry.page"))
            secAvDet = true;
         if (SecBuff.itemExists("INVENTORY_PART") && SecBuff.namedItemExists("INVENW/InventoryPart.page"))
            secInvPart = true;
         if (SecBuff.itemExists("PURCHASE_PART_SUPPLIER") && SecBuff.namedItemExists("PURCHW/PurchasePartSupplier.page"))
            secSupPart = true;
         if (SecBuff.itemExists("EQUIPMENT_OBJECT") && SecBuff.namedItemExists("EQUIPW/RMBEquipmentObject.page"))
            secParam = true;
         if (SecBuff.itemExists("MAINTENANCE_OBJECT") && SecBuff.namedItemExists("EQUIPW/RMBEquipmentObject.page"))
            secMeasure = true;
         if (SecBuff.itemExists("ACTIVE_SEPARATE") && SecBuff.namedItemExists("PCMW/ActiveSeparate2.page"))
            secPrepare = true;
         if (SecBuff.itemExists("HISTORICAL_SEPARATE") && SecBuff.namedItemExists("PCMW/HistoricalSeparateRMB.page"))
            secHisWo = true;
         if (SecBuff.itemExists("PERMIT_TYPE") && SecBuff.namedItemExists("PCMW/PermitTypeRMB.page"))
            secAttr = true;
         if (SecBuff.namedItemExists("PCMW/ViewGenerationDlg.page"))
            secGenDet = true;
         if (SecBuff.namedItemExists("PCMW/JobsOperationsDlg.page"))
            secJobOp = true;
         if (SecBuff.itemExists("Pm_Action_API.Generate__"))
            secGenerate = true;
         if (SecBuff.itemExists("COMPETENCY_GROUP_LOV1"))
            secCompetence = true;


         varSec = true;
      }
   }

   // 031211  ARWILK  Begin  (Replace blocks with tabs)
   public void activateBudget()
   {
      tabs.setActiveTab(1);
   }

   public void activateJobs()
   {
      tabs.setActiveTab(2);
   }

   public void activatePlanning()
   {
      tabs.setActiveTab(3);
      okFindITEM8();
   }

   public void activateOperations()
   {
      tabs.setActiveTab(4);
      okFindITEM0();
   }

   public void activateMaterials()
   {
      tabs.setActiveTab(5);
      okFindITEM1();
   }

   public void activateToolsAndFacilities()
   {
      tabs.setActiveTab(6);
      okFindITEM5();
   }

   public void activateMaintenancePlan()
   {
      tabs.setActiveTab(7);
   }

   public void activateCriteria()
   {
      tabs.setActiveTab(8);
   }

   public void activatePermits()
   {
      tabs.setActiveTab(9);
      okFindITEM4();
   }
   // 031211  ARWILK  End  (Replace blocks with tabs)

   public void adjust()
   {
      ASPManager mgr = getASPManager();

      // 031211  ARWILK  Begin  (Replace blocks with tabs)
      headbar.removeCustomCommand("activateBudget");
      headbar.removeCustomCommand("activateJobs");
      headbar.removeCustomCommand("activatePlanning");
      headbar.removeCustomCommand("activateOperations");
      headbar.removeCustomCommand("activateMaterials");
      headbar.removeCustomCommand("activateToolsAndFacilities");
      headbar.removeCustomCommand("activateMaintenancePlan");
      headbar.removeCustomCommand("activateCriteria");
      headbar.removeCustomCommand("activatePermits");
      // 031211  ARWILK  End  (Replace blocks with tabs)

      if (mgr.isPresentationObjectInstalled("docmaw/DocReference.page"))
      {
         mgr.getASPField("DOCUMENTS").setHyperlink("../docmaw/DocReference.page","LU_NAME,KEY_REF","NEWWIN");
         mgr.getASPField("DOCUMENT").setHyperlink("../docmaw/DocReference.page","LU_NAME,KEY_REF","NEWWIN");
      }
      if (mgr.isPresentationObjectInstalled("invenw/inventoryPart.page"))
         mgr.getASPField("PART_NO").setHyperlink("../invenw/inventoryPart.page","PART_NO","NEWWIN");

      mgr.createSearchURL(headblk);

      if (tabs.getActiveTab() == 1)
      {
         if (itemset6.countRows() == 0)
         {
            perBudg = "";
            matBudg = "";
            toolBudg= "";
            extBudg = "";
            expBudg = "";
            fixBudg = "";
            totBudg = "";

            planPer = "";
            planMat = "";
            planTool= "";
            planExt = "";
            planExp = "";
            planFix = "";
            planTot = "";

            perCostLast = "";
            matCostLast = "";
            toolCostLast= "";
            extCostLast = "";
            expCostLast = "";
            fixCostLast = "";
            totCostLast = "";

            planPerRev = "";
            planMatRev = "";
            planToolRev= "";
            planExtRev = "";
            planExpRev = "";
            planFixRev = "";
            planTotRev = "";

         }
         else
         {
            getBudgetValues();

            double perBudgNF = itemset6.getNumberValue("PERS_BUDGET_COST");
            double matBudgNF = itemset6.getNumberValue("MAT_BUDGET_COST");
            double toolBudgNF= itemset6.getNumberValue("TF_BUDGET_COST");
            double extBudgNF = itemset6.getNumberValue("EXT_BUDGET_COST");
            double expBudgNF = itemset6.getNumberValue("EXP_BUDGET_COST");
            double fixBudgNF = itemset6.getNumberValue("FIX_BUDGET_COST");
            double totBudgNF = itemset6.getNumberValue("TOT_BUDGET_COST");

            perBudg = mgr.formatNumber("PERS_BUDGET_COST",perBudgNF);
            matBudg = mgr.formatNumber("MAT_BUDGET_COST",matBudgNF);
            toolBudg= mgr.formatNumber("TF_BUDGET_COST",toolBudgNF);
            extBudg = mgr.formatNumber("EXT_BUDGET_COST",extBudgNF);
            expBudg = mgr.formatNumber("EXP_BUDGET_COST",expBudgNF);
            fixBudg = mgr.formatNumber("FIX_BUDGET_COST",fixBudgNF);
            totBudg = mgr.formatNumber("TOT_BUDGET_COST",totBudgNF);

            double planPerNF = itemset6.getNumberValue("PLAN_PERS_COST");
            double planMatNF = itemset6.getNumberValue("PLAN_MAT_COST");
            double planToolNF= itemset6.getNumberValue("PLAN_TOOL_COST");
            double planExtNF = itemset6.getNumberValue("PLAN_EXT_COST");
            double planExpNF = itemset6.getNumberValue("PLAN_EXP_COST");
            double planFixNF = itemset6.getNumberValue("PLAN_FIX_COST");
            double planTotNF = itemset6.getNumberValue("TOT_PLAN_COST");

            planPer = mgr.formatNumber("PLAN_PERS_COST",planPerNF);
            planMat = mgr.formatNumber("PLAN_MAT_COST",planMatNF);
            planTool= mgr.formatNumber("PLAN_TOOL_COST",planToolNF);
            planExt = mgr.formatNumber("PLAN_EXT_COST",planExtNF);
            planExp = mgr.formatNumber("PLAN_EXP_COST",planExpNF);
            planFix = mgr.formatNumber("PLAN_FIX_COST",planFixNF);
            planTot = mgr.formatNumber("TOT_PLAN_COST",planTotNF);

            double perCostLastNF = itemset6.getNumberValue("PERS_COST_LAST_WO");
            double matCostLastNF = itemset6.getNumberValue("MAT_COST_LAST_WO");
            double toolCostLastNF= itemset6.getNumberValue("TF_COST_LAST_WO");
            double extCostLastNF = itemset6.getNumberValue("EXT_COST_LAST_WO");
            double expCostLastNF = itemset6.getNumberValue("EXP_COST_LAST_WO");
            double fixCostLastNF = itemset6.getNumberValue("FIX_COST_LAST_WO");
            double totCostLastNF = itemset6.getNumberValue("TOT_COST_LAST_WO");

            perCostLast = mgr.formatNumber("PERS_COST_LAST_WO",perCostLastNF);
            matCostLast = mgr.formatNumber("MAT_COST_LAST_WO",matCostLastNF);
            toolCostLast= mgr.formatNumber("TF_COST_LAST_WO",toolCostLastNF);
            extCostLast = mgr.formatNumber("EXT_COST_LAST_WO",extCostLastNF);
            expCostLast = mgr.formatNumber("EXP_COST_LAST_WO",expCostLastNF);
            fixCostLast = mgr.formatNumber("FIX_COST_LAST_WO",fixCostLastNF);
            totCostLast = mgr.formatNumber("TOT_COST_LAST_WO",totCostLastNF);

            double planPerRevNF = itemset6.getNumberValue("PLAN_PERS_REV");
            double planMatRevNF = itemset6.getNumberValue("PLAN_MAT_REV");
            double planToolRevNF= itemset6.getNumberValue("PLAN_TOOL_REV");
            double planExtRevNF = itemset6.getNumberValue("PLAN_EXT_REV");
            double planExpRevNF = itemset6.getNumberValue("PLAN_EXP_REV");
            double planFixRevNF = itemset6.getNumberValue("PLAN_FIX_REV");
            double planTotRevNF = itemset6.getNumberValue("TOT_PLAN_REV");

            planPerRev = mgr.formatNumber("PLAN_PERS_REV",planPerRevNF);
            planMatRev = mgr.formatNumber("PLAN_MAT_REV",planMatRevNF);
            planToolRev= mgr.formatNumber("PLAN_TOOL_REV",planToolRevNF);
            planExtRev = mgr.formatNumber("PLAN_EXT_REV",planExtRevNF);
            planExpRev = mgr.formatNumber("PLAN_EXP_REV",planExpRevNF);
            planFixRev = mgr.formatNumber("PLAN_FIX_REV",planFixRevNF);
            planTotRev = mgr.formatNumber("TOT_PLAN_REV",planTotRevNF);
         }
      }

      fldTitlePartNoPurch = mgr.translate("PCMWPMACTIONPARTNOFLD: Part+No");
      lovTitlePartNoPurch = mgr.translate("PCMWPMACTIONPARTNOLOV: List+of+Part+No");

      warnMsg = mgr.translate("PCMWPMACTIONCREDBLOCK: Customer is Credit blocked");

      laymode = String.valueOf( headlay.getLayoutMode() );

      if (!secConn)
         headbar.removeCustomCommand("connections");
      if (!secRep)
         headbar.removeCustomCommand("replacements");
      if (!secCopy)
         headbar.removeCustomCommand("copyPmActions");
      if (!secRev)
         headbar.removeCustomCommand("createNewRevision");
      if (!secGenerate)
      {
         headbar.removeCustomCommand("generateWorkOrder");
         itembar2.removeCustomCommand("generateWorkOrder2");
      }
      if (mgr.isPresentationObjectInstalled("equipw/EquipmentObjectAddress1.page"))
      {
         if (!secSearchObjAdd)
            headbar.removeCustomCommand("schonobjaddr");
      }

      if (mgr.isPresentationObjectInstalled("equipw/EquipmentSpareStructure2.page"))
      {
         if (!secSpInDsp)
            itembar1.removeCustomCommand("sparePartsInDetPart");
      }
      if (mgr.isPresentationObjectInstalled("invenw/InventoryPartInventoryPartCurrentOnHand.page"))
      {
         if (!secCurrQty)
            itembar1.removeCustomCommand("currQuntity");
      }
      if (mgr.isPresentationObjectInstalled("invenw/InventoryPartAvailabilityPlanningQry.page"))
      {
         if (!secAvDet)
            itembar1.removeCustomCommand("availDetail");
      }
      if (mgr.isPresentationObjectInstalled("invenw/InventoryPart.page"))
      {
         if (!secInvPart)
            itembar1.removeCustomCommand("inventoryPart");
      }
      if (mgr.isPresentationObjectInstalled("purchw/PurchasePartSupplier.page"))
      {
         if (!secSupPart)
            itembar1.removeCustomCommand("supplierPerPart");
      }
      if (!secParam)
         itembar3.removeCustomCommand("parameters");
      if (!secMeasure)
         itembar3.removeCustomCommand("measurements");
      if (!secPrepare)
         itembar2.removeCustomCommand("prepWorkOrder");
      if (!secHisWo)
         itembar2.removeCustomCommand("viewHistWorkOrder");
      if (!secGenDet)
         itembar2.removeCustomCommand("viewGenDetails");
      if (!secJobOp)
         itembar2.removeCustomCommand("jobsAndOperations");
      if (!secAttr)
         itembar4.removeCustomCommand("attributes");
      if (!secCompetence)
      {
         itembar0.removeCustomCommand("additionalQualifications1");
         itembar9.removeCustomCommand("additionalQualifications2");
      }

      if (headset.countRows() > 0)
      {
         if (itemlay7.isEditLayout())
         {
            int currrowItem = itemset7.getCurrentRowNo();

            if (checkforConnections(currrowItem))
               checkconn = true;
            else
               checkconn = false;
         }

         // Set Automatically generated external planning lines read only.
         if (itemlay8.isEditLayout())
         {
            if ("1".equals(itemset8.getRow().getFieldValue("IS_AUTO_LINE")))
               mgr.getASPField("COST").setReadOnly();
         }

         if (headlay.isSingleLayout())
         {
            if ("TRUE".equals(headset.getValue("PM_CAN_BE_MODIFIED")))
            {
               itembar0.enableCommand(itembar0.NEWROW);
               itembar1.enableCommand(itembar1.NEWROW);
               itembar3.enableCommand(itembar3.NEWROW);
               itembar4.enableCommand(itembar4.NEWROW);
               itembar5.enableCommand(itembar5.NEWROW);
               itembar6.enableCommand(itembar6.NEWROW);
               itembar7.enableCommand(itembar7.NEWROW);
               itembar8.enableCommand(itembar8.NEWROW);

               itembar0.enableCommand(itembar0.DUPLICATEROW);
               itembar1.enableCommand(itembar1.DUPLICATEROW);
               itembar3.enableCommand(itembar3.DUPLICATEROW);
               itembar4.enableCommand(itembar4.DUPLICATEROW);
               itembar5.enableCommand(itembar5.DUPLICATEROW);
               itembar6.enableCommand(itembar6.DUPLICATEROW);
               itembar7.enableCommand(itembar7.DUPLICATEROW);
               itembar8.enableCommand(itembar8.DUPLICATEROW);

               itembar0.enableCommand(itembar0.DELETE);
               itembar1.enableCommand(itembar1.DELETE);
               itembar3.enableCommand(itembar3.DELETE);
               itembar4.enableCommand(itembar4.DELETE);
               itembar5.enableCommand(itembar5.DELETE);
               itembar6.enableCommand(itembar6.DELETE);
               itembar7.enableCommand(itembar7.DELETE);
               itembar8.enableCommand(itembar8.DELETE);

               itembar0.enableCommand(itembar0.EDITROW);
               itembar1.enableCommand(itembar1.EDITROW);
               itembar3.enableCommand(itembar3.EDITROW);
               itembar4.enableCommand(itembar4.EDITROW);
               itembar5.enableCommand(itembar5.EDITROW);
               itembar6.enableCommand(itembar6.EDITROW);
               itembar7.enableCommand(itembar7.EDITROW);
               itembar8.enableCommand(itembar8.EDITROW);
            }
            else
            {
               itembar0.disableCommand(itembar0.NEWROW);
               itembar1.disableCommand(itembar1.NEWROW);
               itembar3.disableCommand(itembar3.NEWROW);
               itembar4.disableCommand(itembar4.NEWROW);
               itembar5.disableCommand(itembar5.NEWROW);
               itembar6.disableCommand(itembar6.NEWROW);
               itembar7.disableCommand(itembar7.NEWROW);
               itembar8.disableCommand(itembar8.NEWROW);

               itembar0.disableCommand(itembar0.DUPLICATEROW);
               itembar1.disableCommand(itembar1.DUPLICATEROW);
               itembar3.disableCommand(itembar3.DUPLICATEROW);
               itembar4.disableCommand(itembar4.DUPLICATEROW);
               itembar5.disableCommand(itembar5.DUPLICATEROW);
               itembar6.disableCommand(itembar6.DUPLICATEROW);
               itembar7.disableCommand(itembar7.DUPLICATEROW);
               itembar8.disableCommand(itembar8.DUPLICATEROW);

               itembar0.disableCommand(itembar0.DELETE);
               itembar1.disableCommand(itembar1.DELETE);
               itembar3.disableCommand(itembar3.DELETE);
               itembar4.disableCommand(itembar4.DELETE);
               itembar5.disableCommand(itembar5.DELETE);
               itembar6.disableCommand(itembar6.DELETE);
               itembar7.disableCommand(itembar7.DELETE);
               itembar8.disableCommand(itembar8.DELETE);

               itembar0.disableCommand(itembar0.EDITROW);
               itembar1.disableCommand(itembar1.EDITROW);
               itembar3.disableCommand(itembar3.EDITROW);
               itembar4.disableCommand(itembar4.EDITROW);
               itembar5.disableCommand(itembar5.EDITROW);
               itembar6.disableCommand(itembar6.EDITROW);
               itembar7.disableCommand(itembar7.EDITROW);
               itembar8.disableCommand(itembar8.EDITROW);
            }
         }

         if (itemlay9.isNewLayout()  && (itemset7.countRows() > 0))
            mgr.getASPField("ITEM9_PREDECESSOR").setReadOnly();

         if (itemlay0.isNewLayout())
            mgr.getASPField("PREDECESSOR").setReadOnly();

      }

      if (headset.countRows()>0 && itemlay7.isVisible())
      {
         String sWhereStrForITEM7 = "CONTRACT = '" + headset.getValue("ORG_CONTRACT") + "'";

         if (itemlay7.isFindLayout())
         {
            mgr.getASPField("STD_JOB_ID").setLOV("StandardJobLov1.page", 600, 450);
            sWhereStrForITEM7 = sWhereStrForITEM7 + " AND STANDARD_JOB_TYPE_DB = '1'";
         }

         mgr.getASPField("STD_JOB_ID").setLOVProperty("WHERE", sWhereStrForITEM7);
      }

      if (itemlay1.isVisible())
      {
         mgr.getASPField("SPARE_CONTRACT").setLOVProperty("WHERE","Site_API.Get_Company(CONTRACT) = Site_API.Get_Company('"+headset.getRow().getValue("ORG_CONTRACT")+"')");
      }

      // Field 'Generatable' can not be changed to 'No', if WO is generated.
      if (itemlay2.isVisible() && itemlay2.isEditLayout())
      {
         if (!mgr.isEmpty(itemset2.getValue("WO_NO")))
            mgr.getASPField("ITEM2_PM_GENERATEABLE").setReadOnly();
      }

      if (itemlay5.isVisible())
      {
         mgr.getASPField("ITEM5_CONTRACT").setLOVProperty("WHERE","Site_API.Get_Company(CONTRACT) = Site_API.Get_Company('"+headset.getRow().getValue("CONTRACT")+"')");
         mgr.getASPField("TOOL_FACILITY_ID").setLOVProperty("WHERE","Site_API.Get_Company(CONTRACT) = Site_API.Get_Company('"+headset.getRow().getValue("CONTRACT")+"')");
      }

      if (headlay.isFindLayout())
      {
         mgr.getASPField("MCH_CODE").setLOV("MaintenanceObjectLov.page","HEAD_CONTRACT CONTRACT",600,450);
      }
      else
      {
	 //Bug 87935, Start, Modified the code to get correct pres objects
         mgr.getASPField("MCH_CODE").setLOV("MaintenanceObjectLovLov.page","HEAD_CONTRACT CONTRACT",600,450);
         mgr.getASPField("MCH_CODE").setLOVProperty("WHERE","(OBJ_LEVEL IS NULL OR (OBJ_LEVEL IS NOT NULL AND Equipment_Object_Level_API.Get_Create_Pm(OBJ_LEVEL) = 'TRUE'))");
	 //Bug 87935, End
      }

      if ( itemlay0.isNewLayout() || itemlay0.isEditLayout() || itemlay1.isNewLayout() || itemlay1.isEditLayout() ||
           itemlay2.isNewLayout() || itemlay2.isEditLayout() || itemlay3.isNewLayout() || itemlay3.isEditLayout() ||
           itemlay4.isNewLayout() || itemlay4.isEditLayout() || itemlay5.isNewLayout() || itemlay5.isEditLayout() ||
           itemlay6.isNewLayout() || itemlay6.isEditLayout() || itemlay7.isNewLayout() || itemlay7.isEditLayout() ||
           itemlay8.isNewLayout() || itemlay8.isEditLayout() )
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

//-----------------------------------------------------------------------------
//------------------------ DOCUMENTATION FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "PCMWPMACTIONMASTERTITLE: Separate PM Action";
   }

   protected String getTitle()
   {
      return "PCMWPMACTIONMASTERTITLE: Separate PM Action";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      printHiddenField("HIDDENPARTNO","");
      printHiddenField("ONCEGIVENERROR","FALSE");
      printHiddenField("REFRESHPARENT1", "FALSE");
      printHiddenField("REFRESHPARENT2", "FALSE");

      if (headlay.isSingleLayout() && headset.countRows() > 0)
      {

         if (itemlay9.isEditLayout() && (itemset9.countRows() > 0))
         {
            appendDirtyJavaScript("   f.ITEM9_PREDECESSOR.disabled = true;\n");

         }
         if (itemlay0.isEditLayout() && (itemset0.countRows() > 0))
         {
            appendDirtyJavaScript("   f.PREDECESSOR.disabled = true;\n");

         }
      }

      // 031211  ARWILK  Begin  (Replace blocks with tabs)
      appendToHTML(headlay.show());

      if (headlay.isSingleLayout() && (headset.countRows() > 0))
      {
         appendToHTML(tabs.showTabsInit());

         if (tabs.getActiveTab() == 1)
         {
            appendToHTML(itembar6.showBar());

            if (!itemlay6.isEditLayout())
            {
               appendToHTML("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">");
               appendToHTML("<tr>");
               appendToHTML("<td>");
               appendToHTML("<table cellspacing=0 cellpadding=0 border=0 width=100%><tr><td>&nbsp;&nbsp;</td><td width=100%>");
               appendToHTML("<table id=\"cntITEM6\" cellpadding=0 width=0 border=\"0\" class=\"BlockLayoutTable\">\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80 align=\"middle\">");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONBUDGCOST: Budget Cost"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80 align=\"middle\">");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONPLANCOST: Planned Cost"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80 align=\"middle\">");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONACTCOST: Actual Cost Last WO"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=100 align=\"middle\">");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONPLANREV: Planned Revenue"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>	\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=0>");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONPERSONAL: Personal"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(perBudg));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(planPer));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(perCostLast));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(planPerRev));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>				\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=0>");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONMATERIAL: Material"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(matBudg));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(planMat));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(matCostLast));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(planMatRev));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>				\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=0>");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONTOOL: Tools/Facilities"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(toolBudg));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(planTool));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(toolCostLast));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(planToolRev));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>				\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=0>");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONEXTERNAL: External"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(extBudg));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(planExt));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(extCostLast));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(planExtRev));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>				\n");
               appendToHTML("		<td width=0>");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONEXPENSES: Expenses"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(expBudg));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(planExp));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(expCostLast));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(planExpRev));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>				\n");
               appendToHTML("		<td width=0>");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONFIXEDPRICE: Fixed Price"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(fixBudg));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(planFix));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(fixCostLast));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(planFixRev));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>				\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=0>");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONTOTAL: Total"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(totBudg));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(planTot));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(totCostLast));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(planTotRev));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>				\n");
               appendToHTML("	<tr>\n");
               appendToHTML("	</tr>	\n");
               appendToHTML("</table>\n");
               appendToHTML("</td>	\n");
               appendToHTML("</tr>	\n");
               appendToHTML("</table>\n");
               appendToHTML("</td>	\n");
               appendToHTML("</tr>	\n");
               appendToHTML("</table>\n");
            }
            else
            {
               appendToHTML(itemblk6.generateHiddenFields());   //XSS_Safe AMNILK 20070718
               appendToHTML("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">");
               appendToHTML("<tr>");
               appendToHTML("<td>");
               appendToHTML("<table cellspacing=0 cellpadding=0 border=0 width=100%><tr><td>&nbsp;&nbsp;</td><td width=100%>");
               appendToHTML("&nbsp;<table id=\"cntITEM6\" cellpadding=2 width=0 border=\"0\" class=\"BlockLayoutTable\">\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80 align=\"middle\">");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONBUDGCOST: Budget Cost"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80 align=\"middle\">");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONPLANCOST: Planned Cost"));
               appendToHTML(                                       "		</td>\n");
               appendToHTML("		<td width=80 align=\"middle\">");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONACTCOST: Actual Cost Last WO"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=100 align=\"middle\">");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONPLANREV: Planned Revenue"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>	\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=0>");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONPERSONAL: Personal"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawTextField("PERS_BUDGET_COST",perBudg,"OnChange=validatePersBudgetCost(0)",18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("PLAN_PERS_COST", planPer, "style=\"text-align: right\"", 18));
               appendToHTML(               "		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("PERS_COST_LAST_WO", perCostLast, "style=\"text-align: right\"", 18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("PLAN_PERS_REV", planPerRev, "style=\"text-align: right\"", 18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>	\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=0>");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONMATERIAL: Material"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawTextField("MAT_BUDGET_COST",matBudg,"OnChange=validateMatBudgetCost(0)",18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("PLAN_MAT_COST", planMat, "style=\"text-align: right\"", 18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("MAT_COST_LAST_WO", matCostLast, "style=\"text-align: right\"", 18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("PLAN_MAT_REV", planMatRev, "style=\"text-align: right\"", 18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>				\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=0>");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONTOOL: Tools/Facilities"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawTextField("TF_BUDGET_COST",toolBudg,"OnChange=validateTfBudgetCost(0)",18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("PLAN_TOOL_COST", planTool, "style=\"text-align: right\"", 18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("TF_COST_LAST_WO", toolCostLast, "style=\"text-align: right\"", 18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("PLAN_TOOL_REV", planToolRev, "style=\"text-align: right\"", 18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>				\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=0>");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONEXTERNAL: External"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawTextField("EXT_BUDGET_COST",extBudg,"OnChange=validateExtBudgetCost(0)",18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("PLAN_EXT_COST", planExt, "style=\"text-align: right\"", 18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("EXT_COST_LAST_WO", extCostLast, "style=\"text-align: right\"", 18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("PLAN_EXT_REV", planExtRev, "style=\"text-align: right\"", 18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>				\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=0>");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONEXPENSES: Expenses"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawTextField("EXP_BUDGET_COST",expBudg,"OnChange=validateExpBudgetCost(0)",18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("PLAN_EXP_COST", planExp, "style=\"text-align: right\"", 18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("EXP_COST_LAST_WO", expCostLast, "style=\"text-align: right\"", 18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("PLAN_EXP_REV", planExpRev, "style=\"text-align: right\"", 18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>				\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=0>");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONFIXEDPRICE: Fixed Price"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawTextField("FIX_BUDGET_COST",fixBudg,"OnChange=validateFixBudgetCost(0)",18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("PLAN_FIX_COST", planFix, "style=\"text-align: right\"", 18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("FIX_COST_LAST_WO", fixCostLast, "style=\"text-align: right\"", 18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("PLAN_FIX_REV", planFixRev, "style=\"text-align: right\"", 18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>				\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=0>");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONTOTAL: Total"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("TOT_BUDGET_COST",totBudg,"",18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("TOT_PLAN_COST", planTot, "style=\"text-align: right\"", 18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("TOT_COST_LAST_WO", totCostLast, "style=\"text-align: right\"", 18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("TOT_PLAN_REV", planTotRev, "style=\"text-align: right\"", 18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>				\n");
               appendToHTML("	<tr>\n");
               appendToHTML("	</tr>	\n");
               appendToHTML("</table>	\n");
               appendToHTML("</td>	\n");
               appendToHTML("</tr>	\n");
               appendToHTML("</table>\n");
               appendToHTML("</td>	\n");
               appendToHTML("</tr>	\n");
               appendToHTML("</table>\n");
            }
         }
         else if (tabs.getActiveTab() == 2)
         {
            appendToHTML(itemlay7.show());
            if (itemlay7.isSingleLayout() && (itemset7.countRows() > 0))
               appendToHTML(itemlay9.show());

            if (itemlay7.isEditLayout())
            {
               appendDirtyJavaScript("var sStdJobPrev = document.form.STD_JOB_ID.value;\n");
               appendDirtyJavaScript("var sStdJobRevPrev = document.form.STD_JOB_REVISION.value;\n");
               appendDirtyJavaScript("var sStdJobSitePrev = document.form.STD_JOB_CONTRACT.value;\n");
            }


            appendDirtyJavaScript("function checkConnections()\n");
            appendDirtyJavaScript("{\n");
            if (itemlay7.isEditLayout())
            {
               if (checkconn)
               {
                  appendDirtyJavaScript(" var sStdJob = document.form.STD_JOB_ID.value; \n");
                  appendDirtyJavaScript(" var sStdJobRev = document.form.STD_JOB_REVISION.value; \n");
                  appendDirtyJavaScript(" if ((sStdJobPrev != sStdJob) || (sStdJobRevPrev!=sStdJobRev)) \n");
                  appendDirtyJavaScript(" { \n");
                  appendDirtyJavaScript("    alert(\"");
                  appendDirtyJavaScript(mgr.translateJavaScript("PMACTCONNDISCONNWARNING1: All Operations, Materials, Tools/Facilities and Documents connected to this PM Action through Standard Job "));
                  appendDirtyJavaScript(" : \" + sStdJobPrev \n+ \"");
                  appendDirtyJavaScript(mgr.translateJavaScript("PMACTCONNDISCONNWARNING2:  - Site "));
                  appendDirtyJavaScript(" : \" + sStdJobSitePrev \n+ \"");
                  appendDirtyJavaScript(mgr.translateJavaScript("PMACTCONNDISCONNWARNING3:  - Revision "));
                  appendDirtyJavaScript(" : \" + sStdJobRevPrev \n+ \"");
                  appendDirtyJavaScript(mgr.translateJavaScript("PMACTCONNDISCONNWARNING4: , will be removed. "));
                  appendDirtyJavaScript(" \" ); \n");
                  appendDirtyJavaScript(" } \n");
               }
            }
            appendDirtyJavaScript("return true;\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function validateStdJobId(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if( getRowStatus_('ITEM7',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkStdJobId(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('STD_JOB_ID',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('STD_JOB_CONTRACT',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('STD_JOB_ID',i)=='' )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		getField_('STD_JOB_ID',i).value = '';\n");
            appendDirtyJavaScript("		getField_('STD_JOB_REVISION',i).value = '';\n");        
            appendDirtyJavaScript("		return;\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript(" window.status='");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTPLWAITVAL: Please wait for validation"));
            appendDirtyJavaScript("';\n");
            appendDirtyJavaScript("	 r = __connect(\n");
            appendDirtyJavaScript("		'../pcmw/PmAction.page?VALIDATE=STD_JOB_ID'\n");
            appendDirtyJavaScript("		+ '&STD_JOB_ID=' + URLClientEncode(getValue_('STD_JOB_ID',i))\n");
            appendDirtyJavaScript("		+ '&STD_JOB_CONTRACT=' + URLClientEncode(getValue_('STD_JOB_CONTRACT',i))\n");
            appendDirtyJavaScript("		+ '&STD_JOB_REVISION=' + URLClientEncode(getValue_('STD_JOB_REVISION',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	window.status='';\n\n");    
            appendDirtyJavaScript("	if( checkStatus_(r,'STD_JOB_ID',i,'");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTIONITEM7STDJOB_ID: Standard Job ID"));
            appendDirtyJavaScript("') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('STD_JOB_ID',i,0);\n");
            appendDirtyJavaScript("		assignValue_('STD_JOB_CONTRACT',i,1);\n");
            appendDirtyJavaScript("		assignValue_('STD_JOB_REVISION',i,2);\n");
            appendDirtyJavaScript("		assignValue_('ITEM7_DESCRIPTION',i,3);\n");
            appendDirtyJavaScript("		assignValue_('STD_JOB_STATUS',i,4);\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n\n");

            appendDirtyJavaScript("function validateStdJobRevision(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if( getRowStatus_('ITEM7',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkStdJobRevision(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('STD_JOB_ID',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('STD_JOB_CONTRACT',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('STD_JOB_REVISION',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('STD_JOB_REVISION',i)=='' )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		return;\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript(" window.status='");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTPLWAITVAL: Please wait for validation"));
            appendDirtyJavaScript("';\n");
            appendDirtyJavaScript("	 r = __connect(\n");
            appendDirtyJavaScript("		'../pcmw/PmAction.page?VALIDATE=STD_JOB_REVISION'\n");
            appendDirtyJavaScript("		+ '&STD_JOB_ID=' + URLClientEncode(getValue_('STD_JOB_ID',i))\n");
            appendDirtyJavaScript("		+ '&STD_JOB_CONTRACT=' + URLClientEncode(getValue_('STD_JOB_CONTRACT',i))\n");
            appendDirtyJavaScript("		+ '&STD_JOB_REVISION=' + URLClientEncode(getValue_('STD_JOB_REVISION',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	window.status='';\n\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'STD_JOB_REVISION',i,'");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTIONITEM7STDJOB_REV: Revision"));
            appendDirtyJavaScript("') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('ITEM7_DESCRIPTION',i,0);\n");
            appendDirtyJavaScript("		assignValue_('STD_JOB_STATUS',i,1);\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n\n");
         }
         else if (tabs.getActiveTab() == 3)
            appendToHTML(itemlay8.show());
         else if (tabs.getActiveTab() == 4)
            appendToHTML(itemlay0.show());
         else if (tabs.getActiveTab() == 5)
            appendToHTML(itemlay1.show());
         else if (tabs.getActiveTab() == 6)
            appendToHTML(itemlay5.show());
         else if (tabs.getActiveTab() == 7)
            appendToHTML(itemlay2.show());
         else if (tabs.getActiveTab() == 8)
            appendToHTML(itemlay3.show());
         else if (tabs.getActiveTab() == 9)
            appendToHTML(itemlay4.show());
      }
      // 031211  ARWILK  End  (Replace blocks with tabs)

      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

      appendDirtyJavaScript("window.name = \"PmAction\";\n");  

      appendDirtyJavaScript("function LovPartNoPurch(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	lovUrl = '");
      appendDirtyJavaScript(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT"));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("	openLOVWindow('PART_NO',i,\n");
      appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PURCHASE_PART_LOV&__FIELD=");
      appendDirtyJavaScript(fldTitlePartNoPurch);
      appendDirtyJavaScript("&__TITLE=");
      appendDirtyJavaScript(lovTitlePartNoPurch);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))\n");
      appendDirtyJavaScript("		,600,445,'validatePartNo');\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function hyperlinkContractor()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  sHypeVenderNo = document.form.VENDOR_NO.value;\n");
      appendDirtyJavaScript("  if (sHypeVenderNo == '')\n");
      appendDirtyJavaScript("     window.open(\"../purchw/SupplierInfoSupplier.page?VENDOR_NO=\"+URLClientEncode(sHypeVenderNo),\"StdWin\",\"scrollbars,status,resizable=yes,height=445,width=600\");\n");
      appendDirtyJavaScript("  else\n");
      appendDirtyJavaScript("     window.open(\"../purchw/SupplierInfoSupplier.page?VENDOR_NO=\"+URLClientEncode(sHypeVenderNo),\"StdWin\",\"scrollbars,status,resizable=yes,height=445,width=600\");\n");
      appendDirtyJavaScript("}\n");    

      appendDirtyJavaScript("\n\nvar sComp = '';\n");      

      appendDirtyJavaScript("function validateContract(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkContract(i) ) return;\n");
      appendDirtyJavaScript(" window.status='");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTPLWAITVAL: Please wait for validation"));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=DEFAULT_CONTRACT'\n");
      appendDirtyJavaScript("		+ '&DEFAULT_CONTRACT=' + URLClientEncode(getValue_('DEFAULT_CONTRACT',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript(" window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'DEFAULT_CONTRACT',i,'");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTDEFCONT: Site"));
      appendDirtyJavaScript("') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('COMPANY',i,0);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");   

      appendDirtyJavaScript("function validateCustomerNo(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkCustomerNo(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('CUSTOMER_NO',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('CUSTOMERNAME',i).value = '';\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript(" window.status='");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTPLWAITVAL: Please wait for validation"));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=CUSTOMER_NO'\n");
      appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript(" window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'CUSTOMER_NO',i,'");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTCUSTNO: Customer"));
      appendDirtyJavaScript("') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('CUSTOMERNAME',i,0);\n");
      appendDirtyJavaScript("		custCredStop = __getValidateValue(1);\n");
      appendDirtyJavaScript("		if (custCredStop == 1)\n");
      appendDirtyJavaScript("			alert('");
      appendDirtyJavaScript(mgr.translateJavaScript(warnMsg));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validatePartNo(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("f.HIDDENPARTNO.value = \"\";\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkPartNo(i) ) return;\n");
      appendDirtyJavaScript(" window.status='");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTPLWAITVAL: Please wait for validation"));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=PART_NO'\n");
      appendDirtyJavaScript("		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
      appendDirtyJavaScript("		+ '&SPARE_CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&SALES_PART_COST=' + URLClientEncode(getValue_('SALES_PART_COST',i))\n");
      appendDirtyJavaScript("		+ '&QTY_PLAN=' + URLClientEncode(getValue_('QTY_PLAN',i))\n");
      appendDirtyJavaScript("		+ '&OWNER=' + URLClientEncode(getValue_('OWNER',i))\n");
      appendDirtyJavaScript("		+ '&CONDITION_CODE=' + URLClientEncode(getValue_('CONDITION_CODE',i))\n");
      appendDirtyJavaScript("		+ '&PART_OWNERSHIP=' + URLClientEncode(getValue_('PART_OWNERSHIP',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_PM_NO=' + URLClientEncode(getValue_('ITEM1_PM_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_PM_REVISION=' + URLClientEncode(getValue_('ITEM1_PM_REVISION',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'PART_NO',i,'");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTPARTNO: Part No"));
      appendDirtyJavaScript("') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('ITEM1_CATALOG_NO',i,0);\n");
      appendDirtyJavaScript("		assignValue_('SPAREDESCRIPTION',i,1);\n");
      appendDirtyJavaScript("		assignValue_('INVENTORYFLAG',i,2);\n");
      appendDirtyJavaScript("		assignValue_('DIMQUALITY',i,3);\n");
      appendDirtyJavaScript("		assignValue_('TYPEDESIGNATION',i,4);\n");
      appendDirtyJavaScript("		assignValue_('QTYONHAND',i,5);\n");
      appendDirtyJavaScript("		assignValue_('UNITMEAS',i,6);\n");
      appendDirtyJavaScript("		assignValue_('SALES_PART_COST',i,7);\n");
      appendDirtyJavaScript("		assignValue_('CONDITION_CODE',i,8);\n");
      appendDirtyJavaScript("		assignValue_('PART_OWNERSHIP',i,9);\n");
      appendDirtyJavaScript("		assignValue_('ACTIVEIND_DB',i,10);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("else{\n");
      appendDirtyJavaScript("f.HIDDENPARTNO.value = f.PART_NO.value;\n");
      appendDirtyJavaScript("f.ONCEGIVENERROR.value = \"TRUE\";\n");
      appendDirtyJavaScript("getField_('PART_NO',i).value = '';\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("   if (f.ACTIVEIND_DB.value == 'N') \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWPMACTIONINVSALESPART: All sale parts connected to the part are inactive."));
      appendDirtyJavaScript("          ');\n");
      appendDirtyJavaScript("      f.ITEM1_CATALOG_NO.value = ''; \n");
      appendDirtyJavaScript("      f.SPAREDESCRIPTION.value = ''; \n");   
      appendDirtyJavaScript("      f.SALES_PART_COST.value = ''; \n");   
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("        validateItem1CatalogNo(i);\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function checkPartVal()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" if(f.ONCEGIVENERROR.value == \"TRUE\")");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("if(f.HIDDENPARTNO.value == f.PART_NO.value.toUpperCase())");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("        validatePartNo(-1);\n");  
      appendDirtyJavaScript("}\n"); 
      appendDirtyJavaScript("}\n");   
      appendDirtyJavaScript("}\n");   

//T/F Validation cont            
      appendDirtyJavaScript("function validateItem5Qty(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM5',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkItem5Qty(i) ) return;\n");
      appendDirtyJavaScript(" window.status='");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTPLWAITVAL: Please wait for validation"));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM5_QTY'\n");
      appendDirtyJavaScript("		+ '&ITEM5_QTY=' + URLClientEncode(getValue_('ITEM5_QTY',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM5_PLANNED_HOUR',i))\n");
      appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
      appendDirtyJavaScript("		+ '&TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_PM_NO=' + URLClientEncode(getValue_('ITEM5_PM_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_PM_REVISION=' + URLClientEncode(getValue_('ITEM5_PM_REVISION',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_CATALOG_NO=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_SALES_PRICE=' + URLClientEncode(getValue_('ITEM5_SALES_PRICE',i))\n");
      appendDirtyJavaScript("                + '&ITEM5_DISCOUNT=' + URLClientEncode(getValue_('ITEM5_DISCOUNT',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'ITEM5_QTY',i,'");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTITEM5QTY: Quantity"));
      appendDirtyJavaScript("') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_PLANNED_PRICE',i,0);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateItem5PlannedHour(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("if( getRowStatus_('ITEM5',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkItem5PlannedHour(i) ) return;\n");
      appendDirtyJavaScript(" window.status='");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTPLWAITVAL: Please wait for validation"));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM5_PLANNED_HOUR'\n");
      appendDirtyJavaScript("		+ '&ITEM5_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM5_PLANNED_HOUR',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_QTY=' + URLClientEncode(getValue_('ITEM5_QTY',i))\n");
      appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
      appendDirtyJavaScript("		+ '&TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_PM_NO=' + URLClientEncode(getValue_('ITEM5_PM_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_PM_REVISION=' + URLClientEncode(getValue_('ITEM5_PM_REVISION',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_CATALOG_NO=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_SALES_PRICE=' + URLClientEncode(getValue_('ITEM5_SALES_PRICE',i))\n");
      appendDirtyJavaScript("                + '&ITEM5_DISCOUNT=' + URLClientEncode(getValue_('ITEM5_DISCOUNT',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'ITEM5_PLANNED_HOUR',i,'");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTITEM5PLNHRS: Planned Hours"));
      appendDirtyJavaScript("') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_PLANNED_PRICE',i,0);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateItem5Discount(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("        if( getRowStatus_('ITEM5',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkItem5Discount(i) ) return;\n");
      appendDirtyJavaScript(" window.status='");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTPLWAITVAL: Please wait for validation"));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM5_DISCOUNT'\n");
      appendDirtyJavaScript("		+ '&ITEM5_DISCOUNT=' + URLClientEncode(getValue_('ITEM5_DISCOUNT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_SALES_PRICE=' + URLClientEncode(getValue_('ITEM5_SALES_PRICE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_CATALOG_NO=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_PM_NO=' + URLClientEncode(getValue_('ITEM5_PM_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_PM_REVISION=' + URLClientEncode(getValue_('ITEM5_PM_REVISION',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_QTY=' + URLClientEncode(getValue_('ITEM5_QTY',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM5_PLANNED_HOUR',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_SALES_PRICE=' + URLClientEncode(getValue_('ITEM5_SALES_PRICE',i))\n");
      appendDirtyJavaScript("                + '&ITEM5_DISCOUNT=' + URLClientEncode(getValue_('ITEM5_DISCOUNT',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'ITEM5_DISCOUNT',i,'");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTDISCOUNT: Discount"));
      appendDirtyJavaScript("') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_PLANNED_PRICE',i,0);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateToolFacilityId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("if( getRowStatus_('ITEM5',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkToolFacilityId(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('TOOL_FACILITY_ID',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('TOOL_FACILITY_DESC',i).value = '';\n");
      appendDirtyJavaScript("		validateToolFacilityType(i);\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript(" window.status='");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTPLWAITVAL: Please wait for validation"));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=TOOL_FACILITY_ID'\n");
      appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_ORG_CODE=' + URLClientEncode(getValue_('ITEM5_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_PM_NO=' + URLClientEncode(getValue_('ITEM5_PM_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_PM_REVISION=' + URLClientEncode(getValue_('ITEM5_PM_REVISION',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_QTY=' + URLClientEncode(getValue_('ITEM5_QTY',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM5_PLANNED_HOUR',i))\n");
      appendDirtyJavaScript("                + '&ITEM5_NOTE=' + URLClientEncode(getValue_('ITEM5_NOTE',i))\n");
      appendDirtyJavaScript("                + '&ITEM5_CATALOG_NO=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO',i))\n");
      appendDirtyJavaScript("                + '&ITEM5_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_CONTRACT',i))\n");
      appendDirtyJavaScript("                + '&ITEM5_CATALOG_NO_DESC=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_DESC',i))\n");
      appendDirtyJavaScript("                + '&ITEM5_SALES_PRICE=' + URLClientEncode(getValue_('ITEM5_SALES_PRICE',i))\n");
      appendDirtyJavaScript("                + '&ITEM5_SALES_CURRENCY=' + URLClientEncode(getValue_('ITEM5_SALES_CURRENCY',i))\n");
      appendDirtyJavaScript("                + '&ITEM5_DISCOUNT=' + URLClientEncode(getValue_('ITEM5_DISCOUNT',i))\n");
      appendDirtyJavaScript("                + '&ITEM5_PLANNED_PRICE=' + URLClientEncode(getValue_('ITEM5_PLANNED_PRICE',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'TOOL_FACILITY_ID',i,'");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTTOOLFACID: Tool/Facility ID"));
      appendDirtyJavaScript("') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_DESC',i,0);\n");
      appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_TYPE',i,1);\n");
      appendDirtyJavaScript("		assignValue_('TYPE_DESCRIPTION',i,2);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_COST',i,3);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_COST_CURRENCY',i,4);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO',i,5);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO_DESC',i,6);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_SALES_PRICE',i,7);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_SALES_CURRENCY',i,8);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_DISCOUNT',i,9);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_PLANNED_PRICE',i,10);\n"); 
      appendDirtyJavaScript("		assignValue_('ITEM5_NOTE',i,11);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_CONTRACT',i,12);\n");
      appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_ID',i,13);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateItem5Contract(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM5',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkItem5Contract(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('ITEM5_CONTRACT',i)!='' )\n");
      appendDirtyJavaScript("        {\n");
      appendDirtyJavaScript(" window.status='");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTPLWAITVAL: Please wait for validation"));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM5_CONTRACT'\n");
      appendDirtyJavaScript("		+ '&ITEM5_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_ORG_CODE=' + URLClientEncode(getValue_('ITEM5_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
      appendDirtyJavaScript("		+ '&TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_QTY=' + URLClientEncode(getValue_('ITEM5_QTY',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM5_PLANNED_HOUR',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_PM_NO=' + URLClientEncode(getValue_('ITEM5_PM_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_PM_REVISION=' + URLClientEncode(getValue_('ITEM5_PM_REVISION',i))\n");
      appendDirtyJavaScript("                + '&ITEM5_NOTE=' + URLClientEncode(getValue_('ITEM5_NOTE',i))\n");
      appendDirtyJavaScript("                + '&ITEM5_CATALOG_NO=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO',i))\n");
      appendDirtyJavaScript("                + '&ITEM5_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_CONTRACT',i))\n");
      appendDirtyJavaScript("                + '&ITEM5_CATALOG_NO_DESC=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_DESC',i))\n");
      appendDirtyJavaScript("                + '&ITEM5_SALES_PRICE=' + URLClientEncode(getValue_('ITEM5_SALES_PRICE',i))\n");
      appendDirtyJavaScript("                + '&ITEM5_SALES_CURRENCY=' + URLClientEncode(getValue_('ITEM5_SALES_CURRENCY',i))\n");
      appendDirtyJavaScript("                + '&ITEM5_DISCOUNT=' + URLClientEncode(getValue_('ITEM5_DISCOUNT',i))\n");
      appendDirtyJavaScript("                + '&ITEM5_PLANNED_PRICE=' + URLClientEncode(getValue_('ITEM5_PLANNED_PRICE',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'ITEM5_CONTRACT',i,'");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTITEM5CONT: Site"));
      appendDirtyJavaScript("') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_NOTE',i,0);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO',i,1);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO_DESC',i,2);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_SALES_PRICE',i,3);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_SALES_CURRENCY',i,4);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_DISCOUNT',i,5);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_PLANNED_PRICE',i,6);\n");
      appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_ID',i,7);\n");
      appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_DESC',i,8);\n");
      appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_TYPE',i,9);\n");
      appendDirtyJavaScript("		assignValue_('TYPE_DESCRIPTION',i,10);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_CONTRACT',i,11);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_COST',i,12);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateItem5OrgCode(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("if( getRowStatus_('ITEM5',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkItem5OrgCode(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('ITEM5_ORG_CODE',i)!='' )\n");
      appendDirtyJavaScript("{\n");                
      appendDirtyJavaScript(" window.status='");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTPLWAITVAL: Please wait for validation"));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM5_ORG_CODE'\n");
      appendDirtyJavaScript("		+ '&ITEM5_ORG_CODE=' + URLClientEncode(getValue_('ITEM5_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
      appendDirtyJavaScript("		+ '&TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_QTY=' + URLClientEncode(getValue_('ITEM5_QTY',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM5_PLANNED_HOUR',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_PM_NO=' + URLClientEncode(getValue_('ITEM5_PM_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM5_PM_REVISION=' + URLClientEncode(getValue_('ITEM5_PM_REVISION',i))\n");
      appendDirtyJavaScript("         + '&ITEM5_NOTE=' + URLClientEncode(getValue_('ITEM5_NOTE',i))\n");
      appendDirtyJavaScript("         + '&ITEM5_CATALOG_NO=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO',i))\n");
      appendDirtyJavaScript("         + '&ITEM5_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_CONTRACT',i))\n");
      appendDirtyJavaScript("         + '&ITEM5_CATALOG_NO_DESC=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_DESC',i))\n");
      appendDirtyJavaScript("         + '&ITEM5_SALES_PRICE=' + URLClientEncode(getValue_('ITEM5_SALES_PRICE',i))\n");
      appendDirtyJavaScript("         + '&ITEM5_SALES_CURRENCY=' + URLClientEncode(getValue_('ITEM5_SALES_CURRENCY',i))\n");
      appendDirtyJavaScript("         + '&ITEM5_DISCOUNT=' + URLClientEncode(getValue_('ITEM5_DISCOUNT',i))\n");
      appendDirtyJavaScript("         + '&ITEM5_PLANNED_PRICE=' + URLClientEncode(getValue_('ITEM5_PLANNED_PRICE',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'ITEM5_ORG_CODE',i,'");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTITEM5ORGCODE: Maintenance Organization"));
      appendDirtyJavaScript("') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_NOTE',i,0);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_QTY',i,1);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO',i,2);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO_DESC',i,3);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_SALES_PRICE',i,4);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_SALES_CURRENCY',i,5);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_DISCOUNT',i,6);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_PLANNED_PRICE',i,7);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("}\n"); 

      appendDirtyJavaScript("function validatePartOwnership(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkPartOwnership(i) ) return;\n");
      appendDirtyJavaScript(" if( getValue_('PART_OWNERSHIP',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript(" window.status='");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTPLWAITVAL: Please wait for validation"));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=PART_OWNERSHIP'\n");
      appendDirtyJavaScript("		+ '&PART_OWNERSHIP=' + SelectURLClientEncode('PART_OWNERSHIP',i)+ '&SPARE_CONTRACT=' + SelectURLClientEncode('SPARE_CONTRACT',i)\n");
      appendDirtyJavaScript("         + '&PART_NO=' + SelectURLClientEncode('PART_NO',i)\n");
      appendDirtyJavaScript("         + '&CONDITION_CODE=' + SelectURLClientEncode('CONDITION_CODE',i));\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'PART_OWNERSHIP',i,'");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTPARTOWN: Ownership"));
      appendDirtyJavaScript("') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('PART_OWNERSHIP_DB',i,0);\n");
      appendDirtyJavaScript("		assignValue_('QTYONHAND',i,1);\n");
      appendDirtyJavaScript("		if (f.PART_OWNERSHIP_DB.value == 'CONSIGNMENT'){\n");
      appendDirtyJavaScript("		alert('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTIONINVOWNER1: Ownership type Consignment is not allowed in Materials for Preventive Maintenance Actions."));
      appendDirtyJavaScript("'); \n");
      appendDirtyJavaScript("      f.PART_OWNERSHIP.value = ''; \n");
      appendDirtyJavaScript("      f.PART_OWNERSHIP_DB.value = ''; \n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("		if (f.PART_OWNERSHIP_DB.value == 'SUPPLIER LOANED'){\n");
      appendDirtyJavaScript("		alert('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTIONINVOWNER2: Ownership type Supplier Loaned is not allowed in Materials for Preventive Maintenance Actions."));
      appendDirtyJavaScript("'); \n");
      appendDirtyJavaScript("      f.PART_OWNERSHIP.value = ''; \n");
      appendDirtyJavaScript("      f.PART_OWNERSHIP_DB.value = ''; \n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovOwner(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	  if(params) param = params;\n");
      appendDirtyJavaScript("	  else param = '';\n");
      appendDirtyJavaScript("	  var enable_multichoice =(true && ITEM1_IN_FIND_MODE);\n");
      appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'CUSTOMER OWNED') \n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("	  var key_value = (getValue_('OWNER',i).indexOf('%') !=-1)? getValue_('OWNER',i):'';\n");
      appendDirtyJavaScript("	  openLOVWindow('OWNER',i,\n");
      appendDirtyJavaScript("'");
      appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CUSTOMER_INFO&__FIELD=Owner&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('OWNER',i))\n");
      appendDirtyJavaScript("+ '&OWNER=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript(",550,500,'validateOwner');\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateOwner(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("      setDirty();\n");
      appendDirtyJavaScript("   if( !checkOwner(i) ) return;\n");
      appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'CUSTOMER OWNED') \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("      if( getValue_('OWNER',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("      if( getValue_('PART_OWNERSHIP_DB',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("      if( getValue_('OWNER',i)=='' )\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         getField_('OWNER_NAME',i).value = '';\n");
      appendDirtyJavaScript("         return;\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript(" window.status='");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTPLWAITVAL: Please wait for validation"));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("      r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=OWNER'\n");
      appendDirtyJavaScript("                    + '&OWNER=' + URLClientEncode(getValue_('OWNER',i))\n");
      appendDirtyJavaScript("                    + '&PART_OWNERSHIP_DB=' + URLClientEncode(getValue_('PART_OWNERSHIP_DB',i))+ '&SPARE_CONTRACT=' + SelectURLClientEncode('SPARE_CONTRACT',i)\n");
      appendDirtyJavaScript("                    + '&CONDITION_CODE=' + SelectURLClientEncode('CONDITION_CODE',i)\n");
      appendDirtyJavaScript("                    + '&PART_NO=' + SelectURLClientEncode('PART_NO',i)\n");
      appendDirtyJavaScript("                    + '&PART_OWNERSHIP=' + SelectURLClientEncode('PART_OWNERSHIP',i)\n");
      appendDirtyJavaScript("                   );\n");
      appendDirtyJavaScript("      window.status='';\n");
      appendDirtyJavaScript("      if( checkStatus_(r,'OWNER',i,'");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTOWNER: Owner"));
      appendDirtyJavaScript("') )\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         assignValue_('OWNER_NAME',i,0);\n");
      appendDirtyJavaScript("         assignValue_('QTYONHAND',i,1);\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'COMPANY OWNED' && f.OWNER.value != '') \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWPMACTIONINVOWNER11: Owner should not be specified for Company Owned Stock."));
      appendDirtyJavaScript("          ');\n");
      appendDirtyJavaScript("      f.OWNER.value = ''; \n");
      appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'CONSIGNMENT' && f.OWNER.value != '') \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWPMACTIONINVOWNER12: Owner should not be specified for Consignment Stock."));
      appendDirtyJavaScript("          ');\n");
      appendDirtyJavaScript("      f.OWNER.value = ''; \n");
      appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'SUPPLIER LOANED' && f.OWNER.value != '') \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWPMACTIONINVOWNER13: Owner should not be specified for Supplier Loaned Stock."));
      appendDirtyJavaScript("          ');\n");
      appendDirtyJavaScript("      f.OWNER.value = ''; \n");
      appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == '' && f.OWNER.value != '') \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWPMACTIONINVOWNER14: Owner should not be specified when there is no Ownership type."));
      appendDirtyJavaScript("          ');\n");
      appendDirtyJavaScript("      f.OWNER.value = ''; \n");
      appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateConditionCode(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("      setDirty();\n");
      appendDirtyJavaScript("   if( !checkConditionCode(i) ) return;\n");
      appendDirtyJavaScript(" window.status='");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTPLWAITVAL: Please wait for validation"));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("      r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=CONDITION_CODE'\n");
      appendDirtyJavaScript("                    + '&CONDITION_CODE=' + URLClientEncode(getValue_('CONDITION_CODE',i))\n");
      appendDirtyJavaScript("                    + '&SPARE_CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))+ '&PART_NO=' + SelectURLClientEncode('PART_NO',i)\n");
      appendDirtyJavaScript("                    + '&PART_OWNERSHIP=' + SelectURLClientEncode('PART_OWNERSHIP',i)\n");
      appendDirtyJavaScript("                   );\n");
      appendDirtyJavaScript("      window.status='';\n");
      appendDirtyJavaScript("      if( checkStatus_(r,'CONDITION_CODE',i,'");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTCONDITIONCODE: Condition Code"));
      appendDirtyJavaScript("') )\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         assignValue_('CONDITION_CODE_DESC',i,0);\n");
      appendDirtyJavaScript("         assignValue_('QTYONHAND',i,1);\n");
      appendDirtyJavaScript("         assignValue_('SALES_PART_COST',i,2);\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateWorkOrderCostType(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	  if( getRowStatus_('ITEM8',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	     setDirty();\n");
      appendDirtyJavaScript("	  if( !checkWorkOrderCostType(i) ) return;\n");
      if (itemlay8.isEditLayout() || itemlay8.isNewLayout())
      {
         appendDirtyJavaScript("   getField_('ITEM8_WORK_ORDER_INVOICE_TYPE',i).selectedIndex = 2;\n");
         appendDirtyJavaScript("   f.ITEM8_WORK_ORDER_INVOICE_TYPE.disabled = false;\n");
         appendDirtyJavaScript("   f.COST.disabled = false;\n");
      }
      appendDirtyJavaScript("   if( getField_('WORK_ORDER_COST_TYPE',i).selectedIndex == 1 )\n");
      appendDirtyJavaScript("	  {\n");
      appendDirtyJavaScript("      window.alert('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTPLANREGOFPER: Registration of Personnel costs is not allowed here. Use Operations section."));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("      getField_('WORK_ORDER_COST_TYPE',i).value = '';\n");
      appendDirtyJavaScript("      return;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   if( getField_('WORK_ORDER_COST_TYPE',i).selectedIndex == 2 )\n");
      appendDirtyJavaScript("	  {\n");
      appendDirtyJavaScript("      window.alert('");
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWPMACTPLANREGOFMAT: Registration of Material costs is not allowed here. Use Materials section."));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("      getField_('WORK_ORDER_COST_TYPE',i).value = '';\n");
      appendDirtyJavaScript("      return;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   if( getField_('WORK_ORDER_COST_TYPE',i).selectedIndex == 3 )\n");
      appendDirtyJavaScript("      getField_('ITEM8_WORK_ORDER_INVOICE_TYPE',i).selectedIndex = 2;\n");
      appendDirtyJavaScript("   if( getField_('WORK_ORDER_COST_TYPE',i).selectedIndex == 4 )\n");
      appendDirtyJavaScript("      getField_('ITEM8_WORK_ORDER_INVOICE_TYPE',i).selectedIndex = 2;\n");
      appendDirtyJavaScript("   if( getField_('WORK_ORDER_COST_TYPE',i).selectedIndex == 5 )\n");
      appendDirtyJavaScript("	  {\n");
      appendDirtyJavaScript("      getField_('ITEM8_WORK_ORDER_INVOICE_TYPE',i).selectedIndex = 1;\n");
      if (itemlay8.isEditLayout() || itemlay8.isNewLayout())
      {
         appendDirtyJavaScript("      f.ITEM8_WORK_ORDER_INVOICE_TYPE.disabled = true;\n"); 
         appendDirtyJavaScript("      f.COST.disabled = true;\n");
      }
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   if( getField_('WORK_ORDER_COST_TYPE',i).selectedIndex == 6 )\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      window.alert('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTPLANREGOFTOOLFAC: Registration of Tools/Facilities costs is not allowed here. Use Tools and Facilities section."));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("      getField_('WORK_ORDER_COST_TYPE',i).value = '';\n");
      appendDirtyJavaScript("      return;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function checkMaintPlan()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    result = true;\n");
      appendDirtyJavaScript("    if (!checkHeadFields(-1))\n");
      appendDirtyJavaScript("        return;\n");
      appendDirtyJavaScript("        if (\n");
      appendDirtyJavaScript("        (((f.MAINT_EMPLOYEE.value != '') && (f.OLD_EMP.value != '')) && (f.MAINT_EMPLOYEE.value != f.OLD_EMP.value)) ||\n");
      appendDirtyJavaScript("        ((f.MAINT_EMPLOYEE.value == '') && (f.OLD_EMP.value != '')) || ((f.MAINT_EMPLOYEE.value != '') && (f.OLD_EMP.value == ''))\n");
      appendDirtyJavaScript("        )\n");
      appendDirtyJavaScript("        {\n");
      appendDirtyJavaScript("           if (f.PM_PLAN_OPERATION.value == 'TRUE')\n");
      appendDirtyJavaScript("           {\n");
      appendDirtyJavaScript("               if ((f.JOB_OPER_PLAN_EXIST.value == 'TRUE') && (f.SIGN_EXIST.value == 'TRUE'))\n");
      appendDirtyJavaScript("               {\n");
      appendDirtyJavaScript("                   if (confirm('"+mgr.translateJavaScript("PMACTIONOVERWRITESIGN: The Maintenance Plan was adjusted. Do you want to continue?")+"'))\n");        
      appendDirtyJavaScript("                      result = true;\n");
      appendDirtyJavaScript("                   else \n");
      appendDirtyJavaScript("                      result = false; \n");
      appendDirtyJavaScript("                }\n");
      appendDirtyJavaScript("            }\n");
      appendDirtyJavaScript("         }\n");
      appendDirtyJavaScript("     return result;\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function checkJobSignature()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    result = true;\n");
      appendDirtyJavaScript("    checkItem7Fields(-1);\n");
      appendDirtyJavaScript("       if (\n");
      appendDirtyJavaScript("       (((f.EMPLOYEE_ID.value != '') && (f.ITEM7_OLD_EMP.value != '')) && (f.EMPLOYEE_ID.value != f.ITEM7_OLD_EMP.value)) ||\n");
      appendDirtyJavaScript("       ((f.EMPLOYEE_ID.value == '') && (f.ITEM7_OLD_EMP.value != '')) || ((f.EMPLOYEE_ID.value != '') && (f.ITEM7_OLD_EMP.value == ''))\n");
      appendDirtyJavaScript("       )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("            if (f.ITEM7_PM_PLAN_OPERATION.value == 'TRUE')\n");
      appendDirtyJavaScript("            {\n");
      appendDirtyJavaScript("                if (f.ITEM7_JOB_EXIST.value == 'TRUE') \n");
      appendDirtyJavaScript("                {\n");
      appendDirtyJavaScript("                    if (confirm('"+mgr.translateJavaScript("PMACTIONOVERWRITESIGN: The Maintenance Plan was adjusted. Do you want to continue?")+"'))\n");        
      appendDirtyJavaScript("                        result = true;\n");
      appendDirtyJavaScript("                    else \n");
      appendDirtyJavaScript("                        result = false;\n");
      appendDirtyJavaScript("                }\n");
      appendDirtyJavaScript("            }\n");
      appendDirtyJavaScript("        }\n");
      appendDirtyJavaScript("    return result;\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovItem0Signature(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript(" if (document.form.ROLE_CODE.value == '')\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       if(document.form.ITEM0_ORG_CODE.value != '' && document.form.ITEM0_ORG_CONTRACT.value != '' && document.form.ITEM0_COMPANY.value != '')\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM0_COMPANY.value) + \"' AND CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+\"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM0_ORG_CODE.value) +\"' \";\n"); 
      appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM0_COMPANY.value) + \"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+ \"'AND MAINT_ORG= '\" +URLClientEncode(document.form.ITEM0_ORG_CODE.value)+\"' \";\n");
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else if ((document.form.ITEM0_ORG_CODE.value != '') && (document.form.ITEM0_ORG_CONTRACT.value == '') && (document.form.ITEM0_COMPANY.value != ''))\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM0_COMPANY.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM0_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM0_COMPANY.value) + \"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM0_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else if(document.form.ITEM0_ORG_CODE.value = '' && document.form.ITEM0_ORG_CONTRACT.value != '' && document.form.ITEM0_COMPANY.value != '')\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM0_COMPANY.value) + \"' AND CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM0_COMPANY.value) + \"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+\"' \";\n");
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else if(document.form.ITEM0_ORG_CODE.value == '' && document.form.ITEM0_ORG_CONTRACT.value == '' && document.form.ITEM0_COMPANY.value != '')\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM0_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM0_COMPANY.value) + \"' \";\n");
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("		 whereCond1 = \" COMPANY IS NOT NULL \";\n"); 
      appendDirtyJavaScript("		 whereCond2 = \" COMPANY IS NOT NULL \";\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("    if((document.form.ITEM0_ORG_CODE.value != '') && (document.form.ITEM0_ORG_CONTRACT.value != '') && (document.form.ITEM0_COMPANY.value != ''))\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+\"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM0_ORG_CODE.value)+\"' AND ROLE_CODE='\" +URLClientEncode(document.form.ROLE_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript("		 whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+\"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM0_ORG_CODE.value)+\"' \";\n");
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else if(document.form.ITEM0_ORG_CODE.value == '' && document.form.ITEM0_ORG_CONTRACT.value == '' && document.form.ITEM0_COMPANY.value != '')\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ROLE_CODE.value) + \"' \";\n"); 
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" COMPANY IS NOT NULL AND ROLE_CODE = '\" + URLClientEncode(document.form.ROLE_CODE.value) + \"' \";\n");  
      appendDirtyJavaScript("		 whereCond2 = \" COMPANY IS NOT NULL \";\n"); 
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" if(document.form.TEAM_ID.value != '')\n");                           
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("    if(document.form.ROLE_CODE.value == '')\n");
      appendDirtyJavaScript("	   whereCond1 += \" AND EMPLOYEE_ID IN (SELECT MEMBER_EMP_NO FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID='\" + URLClientEncode(document.form.TEAM_ID.value) +\"' AND CONTRACT = '\"+ document.form.TEAM_CONTRACT.value+\"' AND \" +whereCond2 +\") \";\n"); 
      appendDirtyJavaScript("       else\n");
      appendDirtyJavaScript("	   whereCond1 += \" AND (EMP_NO,1)IN (SELECT MEMBER_EMP_NO,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\" + URLClientEncode(document.form.ROLE_CODE.value) + \"',MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID='\" + URLClientEncode(document.form.TEAM_ID.value) +\"' AND CONTRACT = '\"+ document.form.TEAM_CONTRACT.value+\"' AND \" +whereCond2 +\") \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM0_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM0_SIGNATURE',i).indexOf('%') !=-1)? getValue_('ITEM0_SIGNATURE',i):'';\n");
      appendDirtyJavaScript("	if( getValue_('ROLE_CODE',i)=='' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("              openLOVWindow('ITEM0_SIGNATURE',i,\n");
      appendDirtyJavaScript("                  '../mscomw/MaintEmployeeLov.page?__FIELD=Executed+By&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM0_SIGNATURE',i))\n"); 
      appendDirtyJavaScript("                  + '&PERSON_ID=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM0_ORG_CODE',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM0_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&CONTRACT=' + URLClientEncode(getValue_('ITEM0_ORG_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateItem0Signature');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("       {\n");        
      appendDirtyJavaScript("              openLOVWindow('ITEM0_SIGNATURE',i,\n");
      appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Executed+By&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM0_SIGNATURE',i))\n"); 
      appendDirtyJavaScript("                  + '&SIGNATURE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM0_ORG_CODE',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM0_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM0_ORG_CONTRACT',i))\n");       
      appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateItem0Signature');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateItem0Signature(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkItem0Signature(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('ITEM0_SIGNATURE',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('ITEM0_EMPLOYEE_ID',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ROLE_CODE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM0_ORG_CODE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM0_SIGNATURE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM0_ORG_CONTRACT',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM0_CATALOG_NO',i).value = '';\n");
      appendDirtyJavaScript("		getField_('SALESPARTDESCRIPTION',i).value = '';\n");
      appendDirtyJavaScript("		getField_('LISTPRICE',i).value = '';\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM0_SIGNATURE'\n");
      appendDirtyJavaScript("		+ '&ITEM0_COMPANY=' + URLClientEncode(getValue_('ITEM0_COMPANY',i))\n");
      appendDirtyJavaScript("		+ '&ITEM0_SIGNATURE=' + URLClientEncode(getValue_('ITEM0_SIGNATURE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM0_ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM0_ORG_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM0_ORG_CODE=' + URLClientEncode(getValue_('ITEM0_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM0_CONTRACT=' + URLClientEncode(getValue_('ITEM0_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM0_CATALOG_NO=' + URLClientEncode(getValue_('ITEM0_CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&SALESPARTDESCRIPTION=' + URLClientEncode(getValue_('SALESPARTDESCRIPTION',i))\n");
      appendDirtyJavaScript("		+ '&LISTPRICE=' + URLClientEncode(getValue_('LISTPRICE',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'ITEM0_SIGNATURE',i,'Executed By') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('ITEM0_EMPLOYEE_ID',i,0);\n");
      appendDirtyJavaScript("		assignValue_('ROLE_CODE',i,1);\n");
      appendDirtyJavaScript("		assignValue_('ITEM0_ORG_CODE',i,2);\n");
      appendDirtyJavaScript("		assignValue_('ITEM0_SIGNATURE',i,3);\n");
      appendDirtyJavaScript("		assignValue_('ITEM0_ORG_CONTRACT',i,4);\n");
      appendDirtyJavaScript("		assignValue_('ITEM0_CATALOG_NO',i,5);\n");
      appendDirtyJavaScript("		assignValue_('SALESPARTDESCRIPTION',i,6);\n");
      appendDirtyJavaScript("		assignValue_('LISTPRICE',i,7);\n");
      appendDirtyJavaScript("		assignValue_('CATALOG_COST',i,8);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovItem0OrgCode(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript(" if (document.form.ROLE_CODE.value != '') \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if((document.form.ITEM0_EMPLOYEE_ID.value == '') && (document.form.ITEM0_ORG_CONTRACT.value == ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ROLE_CODE.value) + \"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM0_EMPLOYEE_ID.value == '') && (document.form.ITEM0_ORG_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ROLE_CODE.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM0_EMPLOYEE_ID.value != '') && (document.form.ITEM0_ORG_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ROLE_CODE.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+\"' AND EMP_NO = '\" +URLClientEncode(document.form.ITEM0_EMPLOYEE_ID.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+\"' AND MEMBER_EMP_NO = '\" +URLClientEncode(document.form.ITEM0_EMPLOYEE_ID.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else if ((document.form.ROLE_CODE.value == '') && (document.form.ITEM0_EMPLOYEE_ID.value != '')) \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if (document.form.ITEM0_ORG_CONTRACT.value == '') \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMPLOYEE_ID = '\" +URLClientEncode(document.form.ITEM0_EMPLOYEE_ID.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" +URLClientEncode(document.form.ITEM0_EMPLOYEE_ID.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMPLOYEE_ID = '\" +URLClientEncode(document.form.ITEM0_EMPLOYEE_ID.value)+\"' AND CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" +URLClientEncode(document.form.ITEM0_EMPLOYEE_ID.value)+\"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+\"'\";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else if ((document.form.ROLE_CODE.value == '') && (document.form.ITEM0_EMPLOYEE_ID.value == '')) \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if (document.form.ITEM0_ORG_CONTRACT.value != '') \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if (document.form.ITEM0_ORG_CONTRACT.value != '') \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if (document.form.TEAM_ID.value != '')\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript(" if(whereCond1 != '' )\n");
      appendDirtyJavaScript("	           whereCond1 += \" AND \";\n");
      appendDirtyJavaScript(" if (document.form.ROLE_CODE.value == '')\n");    
      appendDirtyJavaScript("	   whereCond1 += \" ORG_CODE IN (SELECT MAINT_ORG FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.TEAM_ID.value+\"' AND CONTRACT = '\"+ document.form.TEAM_CONTRACT.value+\"' \";\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("	   whereCond1 += \" (ORG_CODE,EMP_NO,1) IN (SELECT MAINT_ORG,MEMBER_EMP_NO,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\"+ document.form.ROLE_CODE.value+\"',MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.TEAM_ID.value+\"' AND CONTRACT='\" + URLClientEncode(document.form.TEAM_CONTRACT.value) +\"' \";\n"); 
      appendDirtyJavaScript(" if(whereCond2 != '' )\n");
      appendDirtyJavaScript("	           whereCond1 += \" AND \" +whereCond2 +\" \";\n"); 
      appendDirtyJavaScript("	whereCond1 += \")\";\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM0_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM0_ORG_CODE',i).indexOf('%') !=-1)? getValue_('ITEM0_ORG_CODE',i):'';\n");
      appendDirtyJavaScript("	if( getValue_('ROLE_CODE',i)=='' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("	            if( getValue_('ITEM0_EMPLOYEE_ID',i)=='' )\n");
      appendDirtyJavaScript("             {\n");
      appendDirtyJavaScript("                  openLOVWindow('ITEM0_ORG_CODE',i,\n");
      appendDirtyJavaScript("                     '../mscomw/OrgCodeAllowedSiteLov.page?__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM0_ORG_CODE',i))\n"); 
      appendDirtyJavaScript("                      + '&ORG_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM0_COMPANY',i))\n");
      appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM0_ORG_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	             ,550,500,'validateItem0OrgCode');\n");
      appendDirtyJavaScript("             }\n");
      appendDirtyJavaScript("	            else\n");
      appendDirtyJavaScript("             {\n");
      appendDirtyJavaScript("                 openLOVWindow('ITEM0_ORG_CODE',i,\n");
      appendDirtyJavaScript("                     '../mscomw/MaintEmployeeLov.page?&__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                     + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM0_ORG_CODE',i))\n"); 
      appendDirtyJavaScript("                     + '&ORG_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                     + '&EMPLOYEE_ID=' + URLClientEncode(getValue_('ITEM0_EMPLOYEE_ID',i))\n");
      appendDirtyJavaScript("                     + '&COMPANY=' + URLClientEncode(getValue_('ITEM0_COMPANY',i))\n");
      appendDirtyJavaScript("                     + '&CONTRACT=' + URLClientEncode(getValue_('ITEM0_ORG_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	            ,550,500,'validateItem0OrgCode');\n");
      appendDirtyJavaScript("             }\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("       {\n");        
      appendDirtyJavaScript("              openLOVWindow('ITEM0_ORG_CODE',i,\n");
      appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM0_ORG_CODE',i))\n"); 
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('ITEM0_EMPLOYEE_ID',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM0_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM0_ORG_CONTRACT',i))\n");       
      appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateItem0OrgCode');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovItem0OrgContract(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript(" if (document.form.ITEM0_COMPANY.value != '') \n");
      appendDirtyJavaScript("	      whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.ITEM0_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM0_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM0_ORG_CONTRACT',i).indexOf('%') !=-1)? getValue_('ITEM0_ORG_CONTRACT',i):'';\n");
      appendDirtyJavaScript(" openLOVWindow('ITEM0_ORG_CONTRACT',i,\n");
      appendDirtyJavaScript("'");
      appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=USER_ALLOWED_SITE_LOV&__FIELD=Organization+Site&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM0_ORG_CONTRACT',i))\n");
      appendDirtyJavaScript("+ '&ITEM0_ORG_CONTRACT=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript(",550,500,'validateItem0OrgContract');\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovRoleCode(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript(" if ((document.form.ITEM0_EMPLOYEE_ID.value != '') && (document.form.ITEM0_COMPANY.value != '')) \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if((document.form.ITEM0_ORG_CODE.value == '') && (document.form.ITEM0_ORG_CONTRACT.value == ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.ITEM0_EMPLOYEE_ID.value) + \"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.ITEM0_EMPLOYEE_ID.value) + \"' \";\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM0_ORG_CODE.value != '') && (document.form.ITEM0_ORG_CONTRACT.value == ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.ITEM0_EMPLOYEE_ID.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM0_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.ITEM0_EMPLOYEE_ID.value) + \"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM0_ORG_CODE.value)+\"' \";\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM0_ORG_CODE.value == '') && (document.form.ITEM0_ORG_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.ITEM0_EMPLOYEE_ID.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.ITEM0_EMPLOYEE_ID.value) + \"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM0_ORG_CODE.value != '') && (document.form.ITEM0_ORG_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.ITEM0_EMPLOYEE_ID.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM0_ORG_CODE.value)+ \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.ITEM0_EMPLOYEE_ID.value) + \"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM0_ORG_CODE.value)+\"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+\"' \";\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("	   whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.ITEM0_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript("	   whereCond2 = \" Site_API.Get_Company(MAINT_ORG_CONTRACT) = '\" + URLClientEncode(document.form.ITEM0_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if (document.form.ITEM0_ORG_CONTRACT.value != '')\n");
      appendDirtyJavaScript("		whereCond1 += \" AND CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript(" else \n");
      appendDirtyJavaScript("		whereCond1 += \" AND CONTRACT = '\" +URLClientEncode(document.form.ORG_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript(" if (document.form.TEAM_ID.value != '')\n");
      appendDirtyJavaScript("	   whereCond1 += \" AND 1 IN (SELECT Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,ROLE_CODE,MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.TEAM_ID.value+\"' AND CONTRACT = '\"+ document.form.TEAM_CONTRACT.value+\"' AND \" +whereCond2 +\")\";\n"); 
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM0_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ROLE_CODE',i).indexOf('%') !=-1)? getValue_('ROLE_CODE',i):'';\n");
      appendDirtyJavaScript("	if( getValue_('ITEM0_EMPLOYEE_ID',i)=='' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("                  openLOVWindow('ROLE_CODE',i,\n");
      appendDirtyJavaScript("                     '../mscomw/RoleToSiteLov.page?__FIELD=Craft+ID&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n"); 
      appendDirtyJavaScript("                      + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM0_COMPANY',i))\n");
      appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM0_ORG_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	             ,550,500,'validateRoleCode');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("       {\n");        
      appendDirtyJavaScript("              openLOVWindow('ROLE_CODE',i,\n");
      appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Craft+ID&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n"); 
      appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM0_ORG_CODE',i))\n");
      appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('ITEM0_EMPLOYEE_ID',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM0_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM0_ORG_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateRoleCode');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function validateRoleCode(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkRoleCode(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('ROLE_CODE',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('LISTPRICE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM0_CATALOG_NO',i).value = '';\n");
      appendDirtyJavaScript("		getField_('SALESPARTDESCRIPTION',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ROLE_CODE',i).value = '';\n");
      appendDirtyJavaScript("		validateItem0OrgCode(i);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ROLE_CODE'\n");
      appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM0_CATALOG_NO=' + URLClientEncode(getValue_('ITEM0_CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM0_CONTRACT=' + URLClientEncode(getValue_('ITEM0_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM0_SIGNATURE=' + URLClientEncode(getValue_('ITEM0_SIGNATURE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM0_ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM0_ORG_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM0_ORG_CODE=' + URLClientEncode(getValue_('ITEM0_ORG_CODE',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'ROLE_CODE',i,'Craft') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('LISTPRICE',i,0);\n");
      appendDirtyJavaScript("		assignValue_('ITEM0_CATALOG_NO',i,1);\n");
      appendDirtyJavaScript("		assignValue_('ITEM0_ORG_CONTRACT',i,2);\n");
      appendDirtyJavaScript("		assignValue_('SALESPARTDESCRIPTION',i,3);\n");
      appendDirtyJavaScript("		assignValue_('ROLE_CODE',i,4);\n");
      appendDirtyJavaScript("		assignValue_('CATALOG_COST',i,5);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovTeamId(i,params)"); 
      appendDirtyJavaScript("{"); 
      appendDirtyJavaScript("	if(params) param = params;\n"); 
      appendDirtyJavaScript("	else param = '';\n"); 
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript(" if(document.form.TEAM_CONTRACT.value != '')\n");
      appendDirtyJavaScript("		whereCond1 = \"CONTRACT = '\" +URLClientEncode(document.form.TEAM_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("		whereCond1 = \"CONTRACT IS NOT NULL \";\n"); 
      appendDirtyJavaScript(" if(document.form.ITEM0_COMPANY.value != '')\n");
      appendDirtyJavaScript(" if( whereCond1=='')\n");
      appendDirtyJavaScript("		whereCond1 = \"COMPANY = '\" +URLClientEncode(document.form.ITEM0_COMPANY.value)+\"' \";\n"); 
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("		whereCond1 += \" AND COMPANY = '\" +URLClientEncode(document.form.ITEM0_COMPANY.value)+\"' \";\n"); 
      appendDirtyJavaScript(" if( whereCond1 !='')\n");
      appendDirtyJavaScript("	           whereCond1 += \" AND \";\n"); 
      appendDirtyJavaScript("	whereCond1 += \" to_date(to_char(sysdate,\'YYYYMMDD\'),\'YYYYMMDD\') BETWEEN nvl(VALID_FROM,to_date(\'00010101\',\'YYYYMMDD\')) AND nvl(VALID_TO,to_date(\'99991231\',\'YYYYMMDD\')) \";\n"); 
      appendDirtyJavaScript(" if(document.form.ITEM0_EMPLOYEE_ID.value != '')\n");
      appendDirtyJavaScript("		whereCond2 = \"MEMBER_EMP_NO = '\" +URLClientEncode(document.form.ITEM0_EMPLOYEE_ID.value)+\"' \";\n"); 
      appendDirtyJavaScript(" if(document.form.ITEM0_ORG_CODE.value != '')\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript(" if( whereCond2=='')\n");
      appendDirtyJavaScript("		whereCond2 = \"MAINT_ORG = '\" +URLClientEncode(document.form.ITEM0_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript(" else \n");
      appendDirtyJavaScript("		whereCond2+= \" AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM0_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if(document.form.ITEM0_ORG_CONTRACT.value != '')\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript(" if(whereCond2=='' )\n");
      appendDirtyJavaScript("		whereCond2 = \"MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript(" else \n");
      appendDirtyJavaScript("		whereCond2 += \" AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM0_ORG_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if(whereCond2 !='' )\n");
      appendDirtyJavaScript("     {\n");
      appendDirtyJavaScript("        if(whereCond1 !='' )\n");
      appendDirtyJavaScript("	           whereCond1 += \" AND \";\n"); 
      appendDirtyJavaScript("        if(document.form.ROLE_CODE.value == '' )\n");
      appendDirtyJavaScript("	           whereCond1 += \" TEAM_ID IN (SELECT TEAM_ID FROM MAINT_TEAM_MEMBER_SERV WHERE \" +whereCond2 +\")\";\n"); 
      appendDirtyJavaScript("        else \n");
      appendDirtyJavaScript("	           whereCond1 += \" (TEAM_ID,1) IN (SELECT TEAM_ID,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\"+ document.form.ROLE_CODE.value+\"' ,MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE \" +whereCond2 +\")\"		;\n"); 
      appendDirtyJavaScript("     }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM0_IN_FIND_MODE);\n"); 
      appendDirtyJavaScript("	var key_value = (getValue_('TEAM_ID',i).indexOf('%') !=-1)?getValue_('TEAM_ID',i):'';\n"); 
      appendDirtyJavaScript("	openLOVWindow('TEAM_ID',i,\n"); 
      appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=MAINT_TEAM&__FIELD=Team+Id&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''"); 
      appendDirtyJavaScript("		+ '&__KEY_VALUE=' + URLClientEncode(getValue_('TEAM_ID',i))"); 
      appendDirtyJavaScript("		+ '&TEAM_ID=' + URLClientEncode(key_value)"); 
      appendDirtyJavaScript("		,550,500,'validateTeamId');\n"); 
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovTeamContract(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript(" if (document.form.ITEM0_COMPANY.value != '') \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("	      whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.ITEM0_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM0_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('TEAM_CONTRACT',i).indexOf('%') !=-1)? getValue_('TEAM_CONTRACT',i):'';\n");
      appendDirtyJavaScript("                  openLOVWindow('TEAM_CONTRACT',i,\n");
      appendDirtyJavaScript("'");
      appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=USER_ALLOWED_SITE_LOV&__FIELD=Team+Site&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('TEAM_CONTRACT',i))\n");
      appendDirtyJavaScript("+ '&TEAM_CONTRACT=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript(",550,500,'validateTeamContract');\n");
      appendDirtyJavaScript("}\n");


      //-----validations for new operations under jobs tab.
      //NOTE : The changes done to the javascript methods in the operation tab
      //       should also be done to the below methods as well.

      appendDirtyJavaScript("function lovItem9Signature(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript(" if (document.form.ITEM9_ROLE_CODE.value == '')\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       if((document.form.ITEM9_ORG_CODE.value != '') && (document.form.ITEM9_ORG_CONTRACT.value != '') && (document.form.ITEM9_COMPANY.value != ''))\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM9_COMPANY.value) + \"' AND CONTRACT = '\" +URLClientEncode(document.form.ITEM9_ORG_CONTRACT.value)+\"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value) +\"' \";\n"); 
      appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM9_COMPANY.value) + \"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_ORG_CONTRACT.value)+ \"'AND MAINT_ORG= '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+\"' \";\n");
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else if ((document.form.ITEM9_ORG_CODE.value != '') && (document.form.ITEM9_ORG_CONTRACT.value == '') && (document.form.ITEM9_COMPANY.value != ''))\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM9_COMPANY.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM9_COMPANY.value) + \"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else if((document.form.ITEM9_ORG_CODE.value = '') && (document.form.ITEM9_ORG_CONTRACT.value != '') && (document.form.ITEM9_COMPANY.value != ''))\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM9_COMPANY.value) + \"' AND CONTRACT = '\" +URLClientEncode(document.form.ITEM9_ORG_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM9_COMPANY.value) + \"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_ORG_CONTRACT.value)+\"' \";\n");
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else if((document.form.ITEM9_ORG_CODE.value == '') && (document.form.ITEM9_ORG_CONTRACT.value == '') && (document.form.ITEM9_COMPANY.value != ''))\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM9_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM9_COMPANY.value) + \"' \";\n");
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("		 whereCond1 = \" COMPANY IS NOT NULL \";\n"); 
      appendDirtyJavaScript("		 whereCond2 = \" COMPANY IS NOT NULL \";\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("    if((document.form.ITEM9_ORG_CODE.value != '') && (document.form.ITEM9_ORG_CONTRACT.value != '') && (document.form.ITEM9_COMPANY.value != ''))\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_ORG_CONTRACT.value)+\"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+\"' AND ROLE_CODE='\" +URLClientEncode(document.form.ITEM9_ROLE_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript("		 whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_ORG_CONTRACT.value)+\"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+\"' AND ROLE='\" +URLClientEncode(document.form.ITEM9_ROLE_CODE.value)+\"' \";\n");
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else if(document.form.ITEM9_ORG_CODE.value == '' && document.form.ITEM9_ORG_CONTRACT.value == '' && document.form.ITEM9_COMPANY.value != '')\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ITEM9_ROLE_CODE.value) + \"' \";\n"); 
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" COMPANY IS NOT NULL AND ROLE_CODE = '\" + URLClientEncode(document.form.ITEM9_ROLE_CODE.value) + \"' \";\n");  
      appendDirtyJavaScript("		 whereCond2 = \" COMPANY IS NOT NULL AND ROLE = '\" + URLClientEncode(document.form.ITEM9_ROLE_CODE.value) + \"' \";\n"); 
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" if(document.form.ITEM9_TEAM_ID.value != '')\n");                           
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("    if(document.form.ROLE_CODE.value == '')\n");
      appendDirtyJavaScript("	   whereCond1 += \" AND EMPLOYEE_ID IN (SELECT MEMBER_EMP_NO FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID='\" + URLClientEncode(document.form.ITEM9_TEAM_ID.value) +\"' AND CONTRACT = '\"+ document.form.ITEM9_TEAM_CONTRACT.value+\"' AND \" +whereCond2 +\") \";\n"); 
      appendDirtyJavaScript("       else\n");
      appendDirtyJavaScript("	   whereCond1 += \" AND (EMP_NO,1)IN (SELECT MEMBER_EMP_NO,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\" + URLClientEncode(document.form.ITEM9_ROLE_CODE.value) + \"',MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID='\" + URLClientEncode(document.form.ITEM9_TEAM_ID.value) +\"' AND CONTRACT = '\"+ document.form.ITEM9_TEAM_CONTRACT.value+\"' AND \" +whereCond2 +\") \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM9_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM9_SIGNATURE',i).indexOf('%') !=-1)? getValue_('ITEM9_SIGNATURE',i):'';\n");
      appendDirtyJavaScript("	if( getValue_('ITEM9_ROLE_CODE',i)=='' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("              openLOVWindow('ITEM9_SIGNATURE',i,\n");
      appendDirtyJavaScript("                  '../mscomw/MaintEmployeeLov.page?__FIELD=Executed+By&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM9_SIGNATURE',i))\n"); 
      appendDirtyJavaScript("                  + '&PERSON_ID=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM9_ORG_CODE',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM9_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&CONTRACT=' + URLClientEncode(getValue_('ITEM9_ORG_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateItem9Signature');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("       {\n");        
      appendDirtyJavaScript("              openLOVWindow('ITEM9_SIGNATURE',i,\n");
      appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Executed+By&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM9_SIGNATURE',i))\n"); 
      appendDirtyJavaScript("                  + '&SIGNATURE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM9_ORG_CODE',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM9_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM9_ORG_CONTRACT',i))\n");       
      appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(getValue_('ITEM9_ROLE_CODE',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateItem9Signature');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateItem9Signature(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM9',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkItem9Signature(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('ITEM9_SIGNATURE',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('ITEM9_EMPLOYEE_ID',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM9_ROLE_CODE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM9_ORG_CODE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM9_SIGNATURE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM9_ORG_CONTRACT',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM9_CATALOG_NO',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM9_SALESPARTDESCRIPTION',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM9_LISTPRICE',i).value = '';\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM9_SIGNATURE'\n");
      appendDirtyJavaScript("		+ '&ITEM9_COMPANY=' + URLClientEncode(getValue_('ITEM9_COMPANY',i))\n");
      appendDirtyJavaScript("		+ '&ITEM9_SIGNATURE=' + URLClientEncode(getValue_('ITEM9_SIGNATURE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM9_ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM9_ORG_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM9_ROLE_CODE=' + URLClientEncode(getValue_('ITEM9_ROLE_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM9_ORG_CODE=' + URLClientEncode(getValue_('ITEM9_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM9_CONTRACT=' + URLClientEncode(getValue_('ITEM9_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM9_CATALOG_NO=' + URLClientEncode(getValue_('ITEM9_CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM9_SALESPARTDESCRIPTION=' + URLClientEncode(getValue_('ITEM9_SALESPARTDESCRIPTION',i))\n");
      appendDirtyJavaScript("		+ '&ITEM9_LISTPRICE=' + URLClientEncode(getValue_('ITEM9_LISTPRICE',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'ITEM9_SIGNATURE',i,'Executed By') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('ITEM9_EMPLOYEE_ID',i,0);\n");
      appendDirtyJavaScript("		assignValue_('ITEM9_ROLE_CODE',i,1);\n");
      appendDirtyJavaScript("		assignValue_('ITEM9_ORG_CODE',i,2);\n");
      appendDirtyJavaScript("		assignValue_('ITEM9_SIGNATURE',i,3);\n");
      appendDirtyJavaScript("		assignValue_('ITEM9_ORG_CONTRACT',i,4);\n");
      appendDirtyJavaScript("		assignValue_('ITEM9_CATALOG_NO',i,5);\n");
      appendDirtyJavaScript("		assignValue_('ITEM9_SALESPARTDESCRIPTION',i,6);\n");
      appendDirtyJavaScript("		assignValue_('ITEM9_LISTPRICE',i,7);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function lovItem9OrgCode(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript(" if (document.form.ITEM9_ROLE_CODE.value != '') \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if((document.form.ITEM9_EMPLOYEE_ID.value == '') && (document.form.ITEM9_ORG_CONTRACT.value == ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ITEM9_ROLE_CODE.value) + \"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM9_EMPLOYEE_ID.value == '') && (document.form.ITEM9_ORG_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ITEM9_ROLE_CODE.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_ORG_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_ORG_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM9_EMPLOYEE_ID.value != '') && (document.form.ITEM9_ORG_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ITEM9_ROLE_CODE.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_ORG_CONTRACT.value)+\"' AND EMP_NO = '\" +URLClientEncode(document.form.ITEM9_EMPLOYEE_ID.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_ORG_CONTRACT.value)+\"' AND MEMBER_EMP_NO = '\" +URLClientEncode(document.form.ITEM9_EMPLOYEE_ID.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else if ((document.form.ITEM9_ROLE_CODE.value == '') && (document.form.ITEM9_EMPLOYEE_ID.value != '')) \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if (document.form.ITEM9_ORG_CONTRACT.value == '') \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMPLOYEE_ID = '\" +URLClientEncode(document.form.ITEM9_EMPLOYEE_ID.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" +URLClientEncode(document.form.ITEM9_EMPLOYEE_ID.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMPLOYEE_ID = '\" +URLClientEncode(document.form.ITEM9_EMPLOYEE_ID.value)+\"' AND CONTRACT = '\" +URLClientEncode(document.form.ITEM9_ORG_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" +URLClientEncode(document.form.ITEM9_EMPLOYEE_ID.value)+\"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_ORG_CONTRACT.value)+\"'\";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else if ((document.form.ITEM9_ROLE_CODE.value == '') && (document.form.ITEM9_EMPLOYEE_ID.value == '')) \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if (document.form.ITEM9_ORG_CONTRACT.value != '') \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" CONTRACT = '\" +URLClientEncode(document.form.ITEM9_ORG_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_ORG_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if (document.form.ITEM9_ORG_CONTRACT.value != '') \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" CONTRACT = '\" +URLClientEncode(document.form.ITEM9_ORG_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_ORG_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if (document.form.ITEM9_TEAM_ID.value != '')\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript(" if(whereCond1 != '' )\n");
      appendDirtyJavaScript("	           whereCond1 += \" AND \";\n"); 
      appendDirtyJavaScript(" if (document.form.ITEM9_ROLE_CODE.value == '')\n");    
      appendDirtyJavaScript("	   whereCond1 += \" ORG_CODE IN (SELECT MAINT_ORG FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.ITEM9_TEAM_ID.value+\"' AND CONTRACT = '\"+ document.form.ITEM9_TEAM_CONTRACT.value+\"' \";\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("	   whereCond1 += \" (ORG_CODE,EMP_NO,1) IN (SELECT MAINT_ORG,MEMBER_EMP_NO,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\"+ document.form.ITEM9_ROLE_CODE.value+\"',MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.ITEM9_TEAM_ID.value+\"' AND CONTRACT='\" + URLClientEncode(document.form.ITEM9_TEAM_CONTRACT.value) +\"' \";\n"); 
      appendDirtyJavaScript(" if(whereCond2 != '' )\n");
      appendDirtyJavaScript("	           whereCond1 += \" AND \" +whereCond2 +\" \";\n"); 
      appendDirtyJavaScript("	whereCond1 += \")\";\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM9_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM9_ORG_CODE',i).indexOf('%') !=-1)? getValue_('ITEM9_ORG_CODE',i):'';\n");
      appendDirtyJavaScript("	if( getValue_('ITEM9_ROLE_CODE',i)=='' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("	            if( getValue_('ITEM9_EMPLOYEE_ID',i)=='' )\n");
      appendDirtyJavaScript("             {\n");
      appendDirtyJavaScript("                  openLOVWindow('ITEM9_ORG_CODE',i,\n");
      appendDirtyJavaScript("                     '../mscomw/OrgCodeAllowedSiteLov.page?__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM9_ORG_CODE',i))\n"); 
      appendDirtyJavaScript("                      + '&ORG_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM9_COMPANY',i))\n");
      appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM9_ORG_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	             ,550,500,'validateItem9OrgCode');\n");
      appendDirtyJavaScript("             }\n");
      appendDirtyJavaScript("	            else\n");
      appendDirtyJavaScript("             {\n");
      appendDirtyJavaScript("                 openLOVWindow('ITEM9_ORG_CODE',i,\n");
      appendDirtyJavaScript("                     '../mscomw/MaintEmployeeLov.page?&__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                     + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM9_ORG_CODE',i))\n"); 
      appendDirtyJavaScript("                     + '&ORG_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                     + '&EMPLOYEE_ID=' + URLClientEncode(getValue_('ITEM9_EMPLOYEE_ID',i))\n");
      appendDirtyJavaScript("                     + '&COMPANY=' + URLClientEncode(getValue_('ITEM9_COMPANY',i))\n");
      appendDirtyJavaScript("                     + '&CONTRACT=' + URLClientEncode(getValue_('ITEM9_ORG_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	            ,550,500,'validateItem9OrgCode');\n");
      appendDirtyJavaScript("             }\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("       {\n");        
      appendDirtyJavaScript("              openLOVWindow('ITEM9_ORG_CODE',i,\n");
      appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM9_ORG_CODE',i))\n"); 
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('ITEM9_EMPLOYEE_ID',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM9_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM9_ORG_CONTRACT',i))\n");       
      appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(getValue_('ITEM9_ROLE_CODE',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateItem9OrgCode');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovItem9OrgContract(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript(" if (document.form.ITEM9_COMPANY.value != '') \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("	      whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.ITEM9_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM9_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM9_ORG_CONTRACT',i).indexOf('%') !=-1)? getValue_('ITEM9_ORG_CONTRACT',i):'';\n");
      appendDirtyJavaScript("                  openLOVWindow('ITEM9_ORG_CONTRACT',i,\n");
      appendDirtyJavaScript("'");
      appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=USER_ALLOWED_SITE_LOV&__FIELD=Organization+Site&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM9_ORG_CONTRACT',i))\n");
      appendDirtyJavaScript("+ '&ITEM9_ORG_CONTRACT=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript(",550,500,'validateItem9OrgContract');\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function lovItem9RoleCode(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript(" if ((document.form.ITEM9_EMPLOYEE_ID.value != '') && (document.form.ITEM9_COMPANY.value != '')) \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if((document.form.ITEM9_ORG_CODE.value == '') && (document.form.ITEM9_ORG_CONTRACT.value == ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.ITEM9_EMPLOYEE_ID.value) + \"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.ITEM9_EMPLOYEE_ID.value) + \"' \";\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM9_ORG_CODE.value != '') && (document.form.ITEM9_ORG_CONTRACT.value == ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.ITEM9_EMPLOYEE_ID.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.ITEM9_EMPLOYEE_ID.value) + \"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+\"' \";\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM9_ORG_CODE.value == '') && (document.form.ITEM9_ORG_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.ITEM9_EMPLOYEE_ID.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_ORG_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.ITEM9_EMPLOYEE_ID.value) + \"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_ORG_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM9_ORG_CODE.value != '') && (document.form.ITEM9_ORG_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.ITEM9_EMPLOYEE_ID.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+ \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_ORG_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.ITEM9_EMPLOYEE_ID.value) + \"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+\"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_ORG_CONTRACT.value)+\"' \";\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("	   whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.ITEM9_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript("	   whereCond2 = \" Site_API.Get_Company(MAINT_ORG_CONTRACT) = '\" + URLClientEncode(document.form.ITEM9_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if (document.form.ITEM9_TEAM_ID.value != '')\n");
      appendDirtyJavaScript("	   whereCond1 += \" AND 1 IN (SELECT Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,ROLE_CODE,MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.ITEM9_TEAM_ID.value+\"' AND CONTRACT = '\"+ document.form.ITEM9_TEAM_CONTRACT.value+\"' AND \" +whereCond2 +\")\";\n"); 
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM9_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM9_ROLE_CODE',i).indexOf('%') !=-1)? getValue_('ITEM9_ROLE_CODE',i):'';\n");
      appendDirtyJavaScript("	if( getValue_('ITEM9_EMPLOYEE_ID',i)=='' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("                  openLOVWindow('ITEM9_ROLE_CODE',i,\n");
      appendDirtyJavaScript("                     '../mscomw/RoleToSiteLov.page?__FIELD=Craft+ID&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM9_ROLE_CODE',i))\n"); 
      appendDirtyJavaScript("                      + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM9_COMPANY',i))\n");
      appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM9_ORG_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	             ,550,500,'validateItem9RoleCode');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("       {\n");        
      appendDirtyJavaScript("              openLOVWindow('ITEM9_ROLE_CODE',i,\n");
      appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Craft+ID&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM9_ROLE_CODE',i))\n"); 
      appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM9_ORG_CODE',i))\n");
      appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('ITEM9_EMPLOYEE_ID',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM9_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM9_ORG_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateItem9RoleCode');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function validateItem9RoleCode(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM9',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkItem9RoleCode(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('ITEM9_ROLE_CODE',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('ITEM9_LISTPRICE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM9_CATALOG_NO',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM9_SALESPARTDESCRIPTION',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM9_ROLE_CODE',i).value = '';\n");
      appendDirtyJavaScript("		validateItem9OrgCode(i);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM9_ROLE_CODE'\n");
      appendDirtyJavaScript("		+ '&ITEM9_ROLE_CODE=' + URLClientEncode(getValue_('ITEM9_ROLE_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM9_CATALOG_NO=' + URLClientEncode(getValue_('ITEM9_CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM9_CONTRACT=' + URLClientEncode(getValue_('ITEM9_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM9_SIGNATURE=' + URLClientEncode(getValue_('ITEM9_SIGNATURE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM9_ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM9_ORG_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM9_ORG_CODE=' + URLClientEncode(getValue_('ITEM9_ORG_CODE',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'ITEM9_ROLE_CODE',i,'Craft') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('ITEM9_LISTPRICE',i,0);\n");
      appendDirtyJavaScript("		assignValue_('ITEM9_CATALOG_NO',i,1);\n");
      appendDirtyJavaScript("		assignValue_('ITEM9_ORG_CONTRACT',i,2);\n");
      appendDirtyJavaScript("		assignValue_('ITEM9_SALESPARTDESCRIPTION',i,3);\n");
      appendDirtyJavaScript("		assignValue_('ITEM9_ROLE_CODE',i,4);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovItem9TeamId(i,params)"); 
      appendDirtyJavaScript("{"); 
      appendDirtyJavaScript("	if(params) param = params;\n"); 
      appendDirtyJavaScript("	else param = '';\n"); 
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript(" if(document.form.ITEM9_TEAM_CONTRACT.value != '')\n");
      appendDirtyJavaScript("		whereCond1 = \"CONTRACT = '\" +URLClientEncode(document.form.ITEM9_TEAM_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("		whereCond1 = \"CONTRACT IS NOT NULL \";\n"); 
      appendDirtyJavaScript(" if(document.form.ITEM9_COMPANY.value != '')\n");
      appendDirtyJavaScript(" if( whereCond1=='')\n");
      appendDirtyJavaScript("		whereCond1 = \"COMPANY = '\" +URLClientEncode(document.form.ITEM9_COMPANY.value)+\"' \";\n"); 
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("		whereCond1 += \" AND COMPANY = '\" +URLClientEncode(document.form.ITEM9_COMPANY.value)+\"' \";\n"); 
      appendDirtyJavaScript(" if( whereCond1 !='')\n");
      appendDirtyJavaScript("	           whereCond1 += \" AND \";\n"); 
      appendDirtyJavaScript("	whereCond1 += \" to_date(to_char(sysdate,\'YYYYMMDD\'),\'YYYYMMDD\') BETWEEN nvl(VALID_FROM,to_date(\'00010101\',\'YYYYMMDD\')) AND nvl(VALID_TO,to_date(\'99991231\',\'YYYYMMDD\')) \";\n"); 
      appendDirtyJavaScript(" if(document.form.ITEM9_EMPLOYEE_ID.value != '')\n");
      appendDirtyJavaScript("		whereCond2 = \"MEMBER_EMP_NO = '\" +URLClientEncode(document.form.ITEM9_EMPLOYEE_ID.value)+\"' \";\n"); 
      appendDirtyJavaScript(" if(document.form.ITEM9_ORG_CODE.value != '')\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript(" if( whereCond2=='')\n");
      appendDirtyJavaScript("		whereCond2 = \"MAINT_ORG = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript(" else \n");
      appendDirtyJavaScript("		whereCond2+= \" AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if(document.form.ITEM9_ORG_CONTRACT.value != '')\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript(" if(whereCond2=='' )\n");
      appendDirtyJavaScript("		whereCond2 = \"MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_ORG_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript(" else \n");
      appendDirtyJavaScript("		whereCond2 += \" AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_ORG_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if(whereCond2 !='' )\n");
      appendDirtyJavaScript("     {\n");
      appendDirtyJavaScript("        if(whereCond1 !='' )\n");
      appendDirtyJavaScript("	           whereCond1 += \" AND \";\n"); 
      appendDirtyJavaScript("        if(document.form.ITEM9_ROLE_CODE.value == '' )\n");
      appendDirtyJavaScript("	           whereCond1 += \" TEAM_ID IN (SELECT TEAM_ID FROM MAINT_TEAM_MEMBER_SERV WHERE \" +whereCond2 +\")\";\n"); 
      appendDirtyJavaScript("        else \n");
      appendDirtyJavaScript("	           whereCond1 += \" (TEAM_ID,1) IN (SELECT TEAM_ID,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\"+ document.form.ITEM9_ROLE_CODE.value+\"',MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE \" +whereCond2 +\")\"		;\n"); 
      appendDirtyJavaScript("     }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM9_IN_FIND_MODE);\n"); 
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM9_TEAM_ID',i).indexOf('%') !=-1)?getValue_('ITEM9_TEAM_ID',i):'';\n"); 
      appendDirtyJavaScript("	openLOVWindow('ITEM9_TEAM_ID',i,\n"); 
      appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=MAINT_TEAM&__FIELD=Team+Id&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''"); 
      appendDirtyJavaScript("		+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM9_TEAM_ID',i))"); 
      appendDirtyJavaScript("		+ '&ITEM9_TEAM_ID=' + URLClientEncode(key_value)"); 
      appendDirtyJavaScript("		,550,500,'validateItem9TeamId');\n"); 
      appendDirtyJavaScript("}\n"); 

      appendDirtyJavaScript("function lovItem9TeamContract(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript(" if (document.form.ITEM9_COMPANY.value != '') \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("	      whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.ITEM9_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM9_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM9_TEAM_CONTRACT',i).indexOf('%') !=-1)? getValue_('ITEM9_TEAM_CONTRACT',i):'';\n");
      appendDirtyJavaScript("                  openLOVWindow('ITEM9_TEAM_CONTRACT',i,\n");
      appendDirtyJavaScript("'");
      appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=USER_ALLOWED_SITE_LOV&__FIELD=Team+Site&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM9_TEAM_CONTRACT',i))\n");
      appendDirtyJavaScript("+ '&ITEM9_TEAM_CONTRACT=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript(",550,500,'validateItem9TeamContract');\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovPredecessor(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   document.form.REFRESHPARENT1.value = \"TRUE\";\n");
      appendDirtyJavaScript("   submit();\n");  
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovItem9Predecessor(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   document.form.REFRESHPARENT2.value = \"TRUE\";\n");
      appendDirtyJavaScript("   submit();\n");  
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function setPredValues(sPredList,sblock)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("if (sPredList!='-1') \n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("     if (sblock =='ITEM0') \n");
      appendDirtyJavaScript("        getField_('PREDECESSOR', -1).value = sPredList;\n");
      appendDirtyJavaScript("     else \n");
      appendDirtyJavaScript("        getField_('ITEM9_PREDECESSOR', -1).value = sPredList;\n");
      appendDirtyJavaScript("  }\n");
      appendDirtyJavaScript("else\n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("     if (sblock =='ITEM0') \n");
      appendDirtyJavaScript("       getField_('PREDECESSOR', -1).value = '';\n");
      appendDirtyJavaScript("     else\n");
      appendDirtyJavaScript("       getField_('ITEM9_PREDECESSOR', -1).value = '';\n");
      appendDirtyJavaScript("  }\n"); 
      appendDirtyJavaScript("}\n"); 


      appendDirtyJavaScript("function lovDelimitationId(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript(" if (document.form.PERMIT_TYPE_ID.value != '') \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("	      whereCond1 = \" ISOLATION_TYPE IN (SELECT isolation_type FROM conn_permit_isolation u WHERE u.permit_type_id = '\" + URLClientEncode(document.form.PERMIT_TYPE_ID.value) + \"')\";\n"); 
      appendDirtyJavaScript("    }\n"); 

      appendDirtyJavaScript("if(params) param = params;\n");
      appendDirtyJavaScript("else param = '';\n");
      appendDirtyJavaScript("var enable_multichoice =(true && ITEM5_IN_FIND_MODE);\n");
      appendDirtyJavaScript("var key_value = (getValue_('DELIMITATION_ID',i).indexOf('%') !=-1)? getValue_('DELIMITATION_ID',i):'';\n");
      appendDirtyJavaScript("openLOVWindow('DELIMITATION_ID',i,\n");
      appendDirtyJavaScript("	APP_ROOT + 'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CONN_DELIMITATION&__FIELD=Isolation+ID&__INIT=1'+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+'&__MASK=%23%23%23%2C%23%23%23%2C%23%23%23%2C%23%230.%23%23'\n");
      appendDirtyJavaScript("	+ '&__KEY_VALUE=' + URLClientEncode(getValue_('DELIMITATION_ID',i))\n");
      appendDirtyJavaScript("	+ '&DELIMITATION_ID=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("	,600,445,'validateDelimitationId');\n");
      appendDirtyJavaScript("}\n");  

      //(-/+) Bug 70920, Modified where condition to consider category objects (Start).
      appendDirtyJavaScript("function lovLineNo(i,params)\n{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('CONTRACT_ID',i).indexOf('%') !=-1)? getValue_('CONTRACT_ID',i):'';\n");
      appendDirtyJavaScript(" 	if(getValue_('CONNECTION_TYPE_DB',i) != 'VIM')\n");
      appendDirtyJavaScript(" 	{\n");
      appendDirtyJavaScript("		whereCond1 = '';\n"); 
      appendDirtyJavaScript("	      	whereCond1 = \" OBJSTATE = 'Active' \";\n"); 
      appendDirtyJavaScript(" 	if (document.form.HEAD_CONTRACT.value != '') \n");
      appendDirtyJavaScript("	      		whereCond1 += \" AND CONTRACT = '\" + URLClientEncode(document.form.HEAD_CONTRACT.value) + \"' \";\n"); 
      appendDirtyJavaScript(" 	if (document.form.CUSTOMER_NO.value != '') \n");
      appendDirtyJavaScript("	      		whereCond1 += \" AND (CUSTOMER_NO = '\" + URLClientEncode(document.form.CUSTOMER_NO.value)+\"' OR Sc_Contract_Customer_API.Check_Exist(CONTRACT_ID, ( '\" + URLClientEncode(document.form.CUSTOMER_NO.value)+ \"')) = 'TRUE' ) \" ;\n"); 
      appendDirtyJavaScript(" 	if (document.form.WORK_TYPE_ID.value != '') \n");
      appendDirtyJavaScript("	      		whereCond1 += \" AND WORK_TYPE_ID = '\" + URLClientEncode(document.form.WORK_TYPE_ID.value) + \"' \";\n"); 
      appendDirtyJavaScript(" 	if (document.form.CONTRACT_ID.value != '') \n");
      appendDirtyJavaScript("	      		whereCond1 += \" AND CONTRACT_ID = '\" + URLClientEncode(document.form.CONTRACT_ID.value) + \"' \";\n"); 
      if (mgr.isModuleInstalled("PCMSCI"))
      {
         appendDirtyJavaScript(" 	if (document.form.MCH_CODE.value != '' && document.form.HEAD_CONTRACT.value != '') \n");
         appendDirtyJavaScript("	      		whereCond1 += \" AND (((CONTRACT_ID,LINE_NO) IN (SELECT contract_id, line_no FROM PSC_SRV_LINE_OBJECTS WHERE mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' AND mch_contract = '\" + URLClientEncode(document.form.HEAD_CONTRACT.value) + \"')) OR (mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' AND mch_contract = '\" + URLClientEncode(document.form.HEAD_CONTRACT.value) + \"') OR CONNECTION_TYPE_DB = 'CATEGORY')\";\n");
         // Filtering by From Date
         //appendDirtyJavaScript(" 	if (document.form.VALID_FROM.value != '') \n");
         //appendDirtyJavaScript("	      		whereCond1 += \" AND Psc_Contr_Product_Api.Get_Date_From(CONTRACT_ID,LINE_NO) <= to_date(to_char(to_date('\" + URLClientEncode(document.form.VALID_FROM.value) + \"','" + mgr.getASPField("VALID_FROM").getMask() + "'),\'YYYYMMDD\'),\'YYYYMMDD\') \";\n"); 
         //appendDirtyJavaScript(" 	else \n");
         appendDirtyJavaScript("	      		whereCond1 += \" AND Psc_Contr_Product_Api.Get_Date_From(CONTRACT_ID,LINE_NO) <= sysdate \";\n"); 
      }
      appendDirtyJavaScript("		openLOVWindow('CONTRACT_ID',i,\n");
      appendDirtyJavaScript("		'PscContrProductLov.page' + '?__VIEW=DUMMY&__INIT=1&__LOV=Y&__WHERE=' +whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("							,550,500,'validateContractId');\n");
      appendDirtyJavaScript(" 	}\n");
      appendDirtyJavaScript("	else\n");
      appendDirtyJavaScript("  window.open('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript("ServiceContractLineSearchDlg.page?"));
      appendDirtyJavaScript("' + '&CUSTOMER_NO='+ URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("+ '&WO_NO=' + URLClientEncode(getValue_('WO_NO',i))\n");
      appendDirtyJavaScript("+ '&CONTRACT=' + URLClientEncode(getValue_('HEAD_CONTRACT',i))\n"); 
      appendDirtyJavaScript("+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
      appendDirtyJavaScript("+ '&WORK_TYPE_ID=' + URLClientEncode(getValue_('WORK_TYPE_ID',i))\n");
      appendDirtyJavaScript(",'serviceContract','scrollbars,resizable,status=yes,width=770,height=500');\n");
      appendDirtyJavaScript("}\n");
      //(-/+) Bug 70920, Modified where condition to consider category objects (Finish).

      //(-/+) Bug 70920, Modified where condition to consider category objects (Start).
      appendDirtyJavaScript("function lovContractId(i,params)\n{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('CONTRACT_ID',i).indexOf('%') !=-1)? getValue_('CONTRACT_ID',i):'';\n");
      appendDirtyJavaScript(" 	if(getValue_('CONNECTION_TYPE_DB',i) != 'VIM')\n");
      appendDirtyJavaScript(" 	{\n");
      appendDirtyJavaScript("		whereCond1 = '';\n"); 
      appendDirtyJavaScript("	      	whereCond1 = \" OBJSTATE = 'Active' \";\n"); 
      appendDirtyJavaScript(" 	if (document.form.HEAD_CONTRACT.value != '') \n");
      appendDirtyJavaScript("	      		whereCond1 += \" AND CONTRACT = '\" + URLClientEncode(document.form.HEAD_CONTRACT.value) + \"' \";\n"); 
      appendDirtyJavaScript(" 	if (document.form.CUSTOMER_NO.value != '') \n");
      appendDirtyJavaScript("	      		whereCond1 += \" AND (CUSTOMER_NO = '\" + URLClientEncode(document.form.CUSTOMER_NO.value)+\"' OR Sc_Contract_Customer_API.Check_Exist(CONTRACT_ID, ( '\" + URLClientEncode(document.form.CUSTOMER_NO.value)+ \"')) = 'TRUE' ) \" ;\n"); 
      appendDirtyJavaScript(" 	if (document.form.WORK_TYPE_ID.value != '') \n");
      appendDirtyJavaScript("	      		whereCond1 += \" AND WORK_TYPE_ID = '\" + URLClientEncode(document.form.WORK_TYPE_ID.value) + \"' \";\n"); 
      if (mgr.isModuleInstalled("PCMSCI"))
      {
         appendDirtyJavaScript(" 	if (document.form.MCH_CODE.value != '' && document.form.HEAD_CONTRACT.value != '') \n");
         appendDirtyJavaScript("	      		whereCond1 += \" AND (((CONTRACT_ID,LINE_NO) IN (SELECT contract_id, line_no FROM PSC_SRV_LINE_OBJECTS WHERE mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' AND mch_contract = '\" + URLClientEncode(document.form.HEAD_CONTRACT.value) + \"')) OR (mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' AND mch_contract = '\" + URLClientEncode(document.form.HEAD_CONTRACT.value) + \"') OR CONNECTION_TYPE_DB = 'CATEGORY')\";\n");         
         // Filtering by From Date
         //appendDirtyJavaScript(" 	if (document.form.VALID_FROM.value != '') \n");
         //appendDirtyJavaScript("	      		whereCond1 += \" AND Psc_Contr_Product_Api.Get_Date_From(CONTRACT_ID,LINE_NO) <= to_date(to_char(to_date('\" + URLClientEncode(document.form.VALID_FROM.value) + \"','" + mgr.getASPField("VALID_FROM").getMask() + "'),\'YYYYMMDD\'),\'YYYYMMDD\') \";\n"); 
         //appendDirtyJavaScript(" 	else \n");
         appendDirtyJavaScript("	      		whereCond1 += \" AND Psc_Contr_Product_Api.Get_Date_From(CONTRACT_ID,LINE_NO) <= sysdate \";\n"); 
      }
      appendDirtyJavaScript("		openLOVWindow('CONTRACT_ID',i,\n");
      appendDirtyJavaScript("		'PscContrProductLov.page' + '?__VIEW=DUMMY&__INIT=1&__LOV=Y&__WHERE=' +whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("							,550,500,'validateContractId');\n");
      appendDirtyJavaScript(" 	}\n");
      appendDirtyJavaScript("	else\n");
      appendDirtyJavaScript("  window.open('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript("ServiceContractLineSearchDlg.page?"));
      appendDirtyJavaScript("' + '&CUSTOMER_NO='+ URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("+ '&WO_NO=' + URLClientEncode(getValue_('WO_NO',i))\n");
      appendDirtyJavaScript("+ '&CONTRACT=' + URLClientEncode(getValue_('HEAD_CONTRACT',i))\n"); 
      appendDirtyJavaScript("+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
      appendDirtyJavaScript("+ '&WORK_TYPE_ID=' + URLClientEncode(getValue_('WORK_TYPE_ID',i))\n");
      appendDirtyJavaScript(",'serviceContract','scrollbars,resizable,status=yes,width=770,height=500');\n");
      appendDirtyJavaScript("}\n");
      //(-/+) Bug 70920, Modified where condition to consider category objects (Finish).

      //Bug 85045, Start, Set Maint Org Site to mandatory if Maint Org, Executed By or Craft has values
      appendDirtyJavaScript("function checkItem0Fields()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("      if ((f.ITEM0_ORG_CODE.value != '' || f.ITEM0_SIGNATURE.value != '' || f.ROLE_CODE.value != '') && f.ITEM0_ORG_CONTRACT.value == '')  \n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWPMACTIONORGMASITEMAN: The field [Maint. Org. Site] must have a value"));
      appendDirtyJavaScript("          ');\n");
      appendDirtyJavaScript("   return false;\n"); 
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("  return checkItem0Description(0);\n");
      appendDirtyJavaScript("}\n");
      //Bug 85045, End


      //-----------------------------------------------------------------
      //------------- 031211  ARWILK  Begin  (Replace links with RMB's)
      if (bOpenNewWindow)
      {
         appendDirtyJavaScript("  window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));  //XSS_Safe AMNILK 20070718
         appendDirtyJavaScript("\", \"");
         appendDirtyJavaScript(newWinHandle);
         appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=");
         appendDirtyJavaScript(newWinWidth);
         appendDirtyJavaScript(",height=");
         appendDirtyJavaScript(newWinHeight);
         appendDirtyJavaScript("\"); \n");  

      }
      // 031211  ARWILK  End  (Replace links with RMB's)

      if (bWorkOrderGenerated)
      {
         appendDirtyJavaScript("alert('" + sMsgTxt + "'); \n");
         //clears the current command to avoid generating a new wo when refreshed is pressed.
         appendDirtyJavaScript(" commandSet('','');\n");                   
      }

      if (bSetObsolete)
      {
         appendDirtyJavaScript("alert('" + sMsgTxt + "'); \n");
         appendDirtyJavaScript(" commandSet('','');\n");                   
      }

   }

}
