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
*  File        : PmActionRound.java 
*  Created     : ASP2JAVA Tool  010222  JEWILK
*  Modified    :
*  CHCRLK  010531  Changed the saveNew() method and added cancelNew().
*  CHCRLK  010613  Modified overwritten validation.
*  CHCRLK  010620  Made Work Description a multiline field and modified 
*                  Standard Job ID validation.
*  NUPELK  010704  Removed RMB Generate WO
*  VAGULK  010704  Changed the parameters in the Inspection Note in the Maintenance Plan(66289)
*  BUNILK  010717  Modified PART_NO field validation. 
*  BUNILK  010806  Made some changes in return AutoString of getContents() function so that to call       
*                  checkPartVal() javascript function from onBlur event of PART_NO field. Done some changes in 
*                  validatePartNo() method also. 
*  NUPELK  010809  Removed contract parameter from the call to Hist. WO form.
*  ARWILK  010813  Changed validation for PART_NO & Changed the view for funstion LOV from Purchase Part.
*  ARWILK  010814  Changed so that Historical Round is opened in a new window.
*  SHCHLK  010815  Modified the supplierPerPart RMB.
*  VAGULK  010816  Added the RMB "Copy Pm Actions..." (Call ID - 77818)
*  SHCHLK  010820  Modified the validations of materials tab.
*  VAGULK  010821  Modified the function validateStdJobId (Call Id 66207) 
*  VAGULK  010828  Removed the pmactionok from run() ctx.writevalue (77818)
*  ARWILK  010904  Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction(). 
*                  (Caused when cancelNew is performed after saveNew)
*  SHCHLK  011010  Added new RMB viewGenerationDetails to the maintenance plan tab.
*  CHCRLK  011017  Performed security check for actions and hyperlinks.
*  SHCHLK  011019  Corrected the okFindITEM4() function. (Call Id - 70869) 
*  SHAFLK  020509  Bug 29948,Added hyperlink for the operation "Spare Parts in Object" under the header level of Material section. 
*  SHAFLK  020509  Bug 29943,Made changes to refresh in same page after RMB retrn. 
*  BUNILK  020906  Bug ID 32182 Added a new method isModuleInst() to check availability of 
*                  DOCMAN and ORDER module inside preDefine method.    
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
------------------Generic WO-------------------------------------------
*  INROLK  021210  Added MCH_CODE_CONTRACT and LOV for MCH_CODE 
*  BUNILK  021213  Merged with 2002-3 SP3
*  BUNILK  030121  Codes reviewed.
*  CHCRLK  030502  Added CONDITION_CODE & CONDITION_CODE_DESC to Materials tab.
*  CHAMLK  030728  Added Part Ownership, Owner and Owner Name to Materials tab.
*  CHAMLK  031018  Modified function Save() in order to allow the deletion of the Maintenance Plan where there is no vale for 
*                  Start Unit and Interval Unit.
*  CHCRLK  031023  Modified lov properties for field SIGNATURE & commented function lovSignature(i). [Call ID 107493]
*  ARWILK  031215  Edge Developments - (Replaced blocks with tabs, Replaced links with RMB's, Removed clone and doReset Methods)
*  VAGULK  031222  Made the field order according to the order in the Centura application(Web Alignment).
*  ARWILK  031229  Edge Developments - (Replaced links with multirow RMB's)
*  VAGULK  040105  Made the field order according to the order in the Centura application(Web Alignment).
*  ARWILK  040120  Edge Developments - (Enable Multirow RMB actions, Remove uneccessary global variables, Enhance Performance, Clean Javascript Code)
* ----------------------- EDGE - SP1 Merge -----------------------------------
*  SHAFLK  040120  Bug Id 41815,Removed Java dependencies.  
*  VAGULK  040324  Merged with SP1.
*  THWILK  040518  Removed STD_JOB_ID and other related fields,validations and javascript methods which was affected by the removal of STD_JOB_ID.
*  SAPRLK  040531  Added new RMB 'Jobs' to represent the jobs tab for IID AMEC109A, Multiple Standard Jobs on PMs.
*  THWILK  040531  Added JOB_ID lov,PM_REVISION and changed all the relevant places which was affected due to the key changes
*                  under IID AMEC109A, Multiple Standard Jobs on PMs.
*  ARWILK  040708  Added ORG_CONTRACT to the headblk. (IID AMEC500C: Resource Planning)
*  ARWILK  040819  Removed "Jobs" RMB and added new tab "Jobs".
*  NIJALK  040830  Call 117471, Modified preDefine().
*  SHAFLK  040722  Bug 43249, Modified validations of PART_NO, CONDITION_CODE, PART_OWNERSHIP and OWNER and added some fields to predefine and modified HTML part. 
*  NIJALK  040902  Merged bug 43249.
*  ARWILK  040903  Modified preDefine.(IID AMEC111A: Std Jobs as PM Templates)
*  NIJALK  040914  Added function createNewRevision(). Modified preDefine().
*  NIJALK  040929  Added parameter project_id to method calls to Inventory_Part_In_Stock_API.Get_Inventory_Quantity().Modified preDefine().
*  NIJALK  041005  Renamed field signatute to 'Executed By' in jobs tab. Modified validations for SIGN and employee_id.
*                  Added new fields MAINT_EMPLOYEE, MAINT_EMP_SIGN to headblk. Removed field SIGNATURE in headblk.
*  ARWILK  041022  LOV Change for Fields STD_JOB_ID,STD_JOB_REVISION. (Spec AMEC111 - Standard Jobs as PM Templates)
*  NIJALK  041025  Modified preDefine(), Validate().
*  NIJALK  041103  Added check box 'OBSOLETE_JOBS' to headblk and modified okFind().Added field Std Job Status to Jobs tab.
*                  Modified validate().
*  NAMELK  041108  Non Standard Translation Tags Changed. 
*  ARWILK  041111  Replaced getContents with printContents.
*  NIJALk  041112  Made the field "Employee ID" to visible and read only in jobs tab.
*  NIJALK  041115  Replaced method Standard_Job_API.Get_Work_Description with Standard_Job_API.Get_Definition.
*  NIJALK  041202  Added new parameters to method calls to Inventory_Part_In_Stock_API.Get_Inventory_Quantity.
*  NIJALK  050117  Modified LOV for 'spare part site' & changed 'sales part site' to read only in material requisition tab.
*  NIJALK  050203  Modified validate(), preDefine().
*  NIJALK  050207  User allowed sites control is swiched between Site and WO Site.
*  NIJALK  050222  Modified createNewRevision(),run().
*  NIJALK  050228  Field PLANNED_WEEK set visible in default.
*  NIJALK  050330  Added read only fields JOB_PRG_ID,JOB_PROG_REV to Jobs Tab.
*  NIJALK  050520  Modified availDetail(), preDefine().
*  SHAFLK  050425  Bug 50830, Used mgr.translateJavaScript() method to get the correctly translated strings in JavaScript functions. 
*  NIJALK  050617  Merged bug 50830. 
*  NIJALK  050808  Bug 126201: Modified ITEM1_SIGNATURE to set Uppercase, and Modified preDefine().
*  NIJALK  050825  Bug 126563: Modified saveReturn() to refresh itemsets after saving a new record.
*  AMNILK  051006  Call Id:127600. Modified method preDefine and validate.Add custom validations to ORG_CODE and ORG_CONTRACT.
*  AMNILK  051012  Call Id:127773. Modified the field format of ITEM1_WO_NO to String.
*  NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile).
*  ERALLK  051208  Bug 54385, Modified the length and the height of 'Alternate Designation' and 'Materials' Fields. 
*  NIJALK  051221  Merged bug 54385.
*  NIJALK  060111  Changed DATE format to compatible with data formats in PG19.
*  THWILK  060310  Call 136823,Modified predefine().
*  SULILK  060321  Call 135197: Modified preDefine(),validate(),getCost(),getSalesPartCost().
*  SHAFLK  060525  Bug 57598, Modified validation for PART_NO.
*  AMNILK  060629  Merged with SP1 APP7.
*  AMDILK  060728  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding
*  AMDILK  060807  Merged with Bug Id 58214	
*  AMNILK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMNILK  060904  Merged Bug Id: 58216.
*  ILSOLK  070218  Modifed for MTPR904 Hink tasks ReqId 45088.setLovPropery() for Object Id.
*  BUNILK  070504  Implemented "MTIS907 New Service Contract - Services" changes.
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  AMDILK  070405  Merged bug id 64068
*  AMDILK  070607  Call Id 145865: Inserted new information for the customer info tab; Contrat name, contract type, 
*                  line description and invoice type
*  AMNILK  070725  Eliminated XSS Security Vulnerability.
*  AMDILK  070731  Removed the scroll buttons of the parent when the child tabs are in new or edit modes
*  AMDILK  070803  Removed the scroll buttons of the parent when the child tabs are in new or edit modes 02
*  NIJALK  070730  Bug 66572, Modified preDefine(),activatePm() and printContents().
*  AMDILK  070816  Merged bug 66572
*  SHAFLK  071108  Bug 67801, Modified validation for STD_JOB_ID.
*  CHANLK  071201  Bug 67819, Change okFind to enable next button.
*  CHCRLK  071204  Bug 67909, Modified activatePm().
*  SHAFLK  080303  Bug 71664, Modified copyPmActions().
*  SHAFLK  080303  Bug 71654, Modified preDefine().
*  ARWILK  080502  Bug 70920, Overode lovLineNo and lovContractId.
*  SHAFLK  081105  Bug 77824, Modified Validate().
*  SHAFLK  090306  Bug 81166, Modified preDefine().
*  SHAFLK  080505  Bug 82435, Modified preDefine().
*  NUKULK  090602  Bug 82745, Modified preDefine(). Removed readonly attr in Materials.PART_NO, SPARE_CONTRACT.
*  HARPLK  090708   Bug 84436, Modified preDefine().
*  SHAFLK  090921  Bug 85901, Modified preDefine().
*  SHAFLK  100210  Bug 88904, Modified printContents().
*  CHANLK  100305  Bug 89283, Change method createNewRevision.
*  NIJALK  100419  Bug 87935, Modified adjust().
*  CHANLK  100305  Bug 88982, Modified okFind
*  NIJALK  100601  Bug 89752, Modified activatePm(), preDefine(), run().
*  CHANLK  100623  Bug 91521, Modified run().
*  SHAFLK  101005  Bug 93366, Modified preDefine().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.lang.reflect.*;
import java.util.*;

public class PmActionRound extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.PmActionRound");

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

   private ASPField f;
   private ASPBlock headblk1;
   private ASPBlock b;

   // 031215  ARWILK  Begin  (Replace blocks with tabs)
   private ASPTabContainer tabs;
   // 031215  ARWILK  End  (Replace blocks with tabs)

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private boolean headSearch;
   private ASPTransactionBuffer trans;
   private boolean flagpm;
   private String qrystr;
   private String pmNo;
   private String pmRev;
   private ASPTransactionBuffer secBuff;
   private ASPQuery qry;
   private ASPCommand cmd;
   private ASPBuffer data;
   private ASPQuery q;
   private int currrow;
   private int headrowno;
   private String calling_url;
   private ASPBuffer buffer;
   private ASPBuffer keys;
   private ASPTransactionBuffer testbuff;
   private String date_compl;
   private String wo_num;
   private ASPBuffer r;
   private String planMatCost;
   private String planExtCost;
   private String planPersRev;
   private String planMatRev;
   private String planExtRev;
   private String perBudg;
   private String matBudg;
   private String extBudg;
   private String totBudg;
   private String planPer;
   private String planMat;
   private String planExt;
   private String planTot;
   private String perCostLast;
   private String matCostLast;
   private String extCostLast;
   private String totCostLast;
   private String planPerRev;
   private String planTotRev;
   private String fldTitleItem3TestPointId;
   private String fldTitleParameterCode;
   private String fldTitleReportInBy;
   private String fldTitleItem1Signature;
   private String lovTitleItem3TestPointId;
   private String lovTitleParameterCode;
   private String lovTitleReportInBy;
   private String lovTitleItem1Signature;
   private String isSecure[];  
   private ASPBuffer row;
   private ASPBuffer sec;  
   //Variables for security checking of actions
   private boolean secCheck;                 
   private boolean ctxReplacements;          
   private boolean ctxCopyPmActions; 
   private boolean ctxCreatePmRev;
   private boolean ctxSparePartsinObject;    
   private boolean ctxSpareParts;            
   private boolean ctxCurrQuntity;           
   private boolean ctxAvailDetail;           
   private boolean ctxInventoryPart;         
   private boolean ctxSupplierPerPart;       
   private boolean ctxReportInRouteWO;       
   private boolean ctxViewHistoricalWO;      
   private boolean ctxViewGenerationDetails; 
   private boolean ctxAttributes;            
   private boolean ctxParameters;            
   private boolean ctxMeasurements;
   private boolean ctxHypeHasDoc;
   private boolean ctxHypePart;
   // 031215  ARWILK  Begin  (Links with RMB's)
   private boolean bOpenNewWindow;
   private String urlString;
   private String newWinHandle;
   // 031215  ARWILK  End  (Links with RMB's)
   private boolean checkconn;
   private boolean bSetObsolete;
   private boolean bHasActiveRepl;
   private String sMsgTxt;
   private String sMsgTxt1;

   //===============================================================
   // Construction 
   //===============================================================
   public PmActionRound(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() 
   {
      isSecure = new String[14] ;
      headSearch = true;

      ASPManager mgr = getASPManager();

      frm = mgr.getASPForm();
      fmt = mgr.newASPHTMLFormatter();   
      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();

      flagpm = ctx.readFlag("FLAGPM",false);
      qrystr = ctx.readValue("QRYSTR","");
      pmNo= ctx.readValue("PM_NO","");
      pmRev= ctx.readValue("PM_REVISION","");
      //Variables for security checking of actions
      secCheck = ctx.readFlag("SECCHECK",secCheck);                                
      ctxReplacements = ctx.readFlag("CTXREPLACEMENTS",ctxReplacements);
      ctxCopyPmActions = ctx.readFlag("CTXCOPYPMACTIONS",ctxCopyPmActions); 
      ctxCreatePmRev = ctx.readFlag("CTXCREATENEWREV",ctxCreatePmRev);
      ctxSparePartsinObject = ctx.readFlag("CTXSPAREPARTSINOBJECT",ctxSparePartsinObject);
      ctxSpareParts = ctx.readFlag("CTXSPAREPARTS",ctxSpareParts);
      ctxCurrQuntity = ctx.readFlag("CTXCURRQUNTITY",ctxCurrQuntity);
      ctxAvailDetail = ctx.readFlag("CTXAVAILDETAIL",ctxAvailDetail);
      ctxInventoryPart = ctx.readFlag("CTXINVENTORYPART",ctxInventoryPart);
      ctxSupplierPerPart = ctx.readFlag("CTXSUPPLIERPERPART",ctxSupplierPerPart);
      ctxReportInRouteWO = ctx.readFlag("CTXREPORTINROUTEWO",ctxReportInRouteWO);
      ctxViewHistoricalWO = ctx.readFlag("CTXVIEWHISTORICALWO",ctxViewHistoricalWO);
      ctxViewGenerationDetails = ctx.readFlag("CTXVIEWGENERATIONDETAILS",ctxViewGenerationDetails);
      ctxAttributes = ctx.readFlag("CTXATTRIBUTES",ctxAttributes);
      ctxParameters = ctx.readFlag("CTXPARAMETERS",ctxParameters);
      ctxMeasurements = ctx.readFlag("CTXMEASUREMENTS",ctxMeasurements);
      ctxHypeHasDoc = ctx.readFlag("CTXHYPEHASDOC",ctxHypeHasDoc);
      ctxHypePart = ctx.readFlag("CTXHYPEPART",ctxHypePart);
      checkconn = ctx.readFlag("CHECKCONNECTION",false);

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("REVCRT")))
      {
         int i = 0;
         String new_pm_no = mgr.readValue("NEW_PM_NO");
         String new_revision = mgr.readValue("NEW_REV");

         //Bug 67948, start
         String act_rep = mgr.readValue("ACT_REP");
         if ("TRUE".equals(act_rep))
         {
            mgr.showAlert(mgr.translate("PCMWPMACTRNDCOPYACTIVEREPL: All replacements which had been defined under current revision will be copied to new revision."));
         }
         //Bug 67948, end
         flagpm = false;

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
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
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
         }
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("PM_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("PM_REVISION")) && !mgr.isEmpty(mgr.getQueryStringValue("SHOWMAT")))
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();   

      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
         flagpm=true;
         pmNo = mgr.readValue("PM_NO");
         pmRev= mgr.readValue("PM_REVISION");
         okFind();    
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
         okFind();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("PM_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("PM_REVISION")))
         okFind();
      else if (mgr.dataTransfered())
         okFind();

      adjust();

      ctx.writeValue("QRYSTR",qrystr);
      ctx.writeValue("PM_NO",pmNo);
      ctx.writeValue("PM_REVISION",pmRev);
      //Variables for security checking of actions
      ctx.writeFlag("SECCHECK",secCheck);                                
      ctx.writeFlag("CTXREPLACEMENTS",ctxReplacements);                  
      ctx.writeFlag("CTXCOPYPMACTIONS",ctxCopyPmActions); 
      ctx.writeFlag("CTXCREATENEWREV",ctxCreatePmRev); 
      ctx.writeFlag("CTXSPAREPARTSINOBJECT",ctxSparePartsinObject);      
      ctx.writeFlag("CTXSPAREPARTS",ctxSpareParts);                      
      ctx.writeFlag("CTXCURRQUNTITY",ctxCurrQuntity);                    
      ctx.writeFlag("CTXAVAILDETAIL",ctxAvailDetail);                    
      ctx.writeFlag("CTXINVENTORYPART",ctxInventoryPart);                
      ctx.writeFlag("CTXSUPPLIERPERPART",ctxSupplierPerPart);            
      ctx.writeFlag("CTXREPORTINROUTEWO",ctxReportInRouteWO);            
      ctx.writeFlag("CTXVIEWHISTORICALWO",ctxViewHistoricalWO);          
      ctx.writeFlag("CTXVIEWGENERATIONDETAILS",ctxViewGenerationDetails);
      ctx.writeFlag("CTXATTRIBUTES",ctxAttributes);                      
      ctx.writeFlag("CTXPARAMETERS",ctxParameters);                      
      ctx.writeFlag("CTXMEASUREMENTS",ctxMeasurements);
      ctx.writeFlag("CTXHYPEHASDOC",ctxHypeHasDoc);
      ctx.writeFlag("CTXHYPEPART",ctxHypePart);
      ctx.writeFlag("CHECKCONNECTION",checkconn);

      // 031215  ARWILK  Begin  (Replace blocks with tabs)
      tabs.saveActiveTab();
      // 031215  ARWILK  End  (Replace blocks with tabs)
   }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
// This function is used to check security status for methods which
// are in modules Order,Purch and Invent .This function is called before
// the required method is performed.  -NUPE,21-07-2000

   public boolean checksec(String method, int ref) 
   {
      String splitted[];
      ASPManager mgr = getASPManager();

      splitted = null;

      isSecure[ref] = "false"; 
      splitted = split(method, "."); 

      secBuff = mgr.newASPTransactionBuffer();
      secBuff.addSecurityQuery(splitted[0], splitted[1]);

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
      ASPManager mgr = getASPManager();
      String val = mgr.readValue("VALIDATE");
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

      cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
      cmd.addParameter("PART_OWNERSHIP",mgr.readValue("PART_OWNERSHIP"));
      trans = mgr.validate(trans);
      String sClientOwnershipDefault = trans.getValue("VALIDOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");
      trans.clear();

      if ("HEAD_CONTRACT".equals(val))
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
      else if ("MCH_CODE".equals(val))
      {
         String validationAttrAtr1 = "";
         String mchCode = "";
         String mchContract = "";
         String descrip ="";
         trans.clear();

         if (mgr.readValue("MCH_CODE").indexOf("~") > -1)
         {
            mchCode = mgr.readValue("MCH_CODE").substring(0,mgr.readValue("MCH_CODE").indexOf("~"));       

            validationAttrAtr1 = mgr.readValue("MCH_CODE").substring(mgr.readValue("MCH_CODE").indexOf("~")+1,mgr.readValue("MCH_CODE").length());

            descrip =  validationAttrAtr1.substring(0,validationAttrAtr1.indexOf("~"));

            mchContract =  validationAttrAtr1.substring(validationAttrAtr1.indexOf("~")+1,validationAttrAtr1.length());                

            cmd = trans.addCustomFunction("DESC","Maintenance_Object_API.Get_Mch_Name","MCH_NAME1");
            cmd.addParameter("HEAD_CONTRACT", mchContract);
            cmd.addParameter("MCH_CODE", mchCode);

            trans = mgr.validate(trans);

            descrip =   trans.getValue("DESC/DATA/MCH_NAME1");

         }
         else
         {
            mchCode = mgr.readValue("MCH_CODE");

            if (mgr.isEmpty(mgr.readValue("HEAD_CONTRACT")))
            {
               cmd = trans.addCustomFunction("MACHCONTRACT","EQUIPMENT_OBJECT_API.Get_Contract","HEAD_CONTRACT");
               cmd.addParameter("MCH_CODE", mchCode);

               cmd = trans.addCustomFunction("DESC","Maintenance_Object_API.Get_Mch_Name","MCH_NAME1");
               cmd.addReference("HEAD_CONTRACT","MACHCONTRACT/DATA");
               cmd.addParameter("MCH_CODE", mchCode);

               trans = mgr.validate(trans);

               mchContract = trans.getValue("MACHCONTRACT/DATA/CONTRACT");
               descrip =   trans.getValue("DESC/DATA/MCH_NAME1");

            }
            else
            {
               mchContract = mgr.readValue("HEAD_CONTRACT");

               cmd = trans.addCustomFunction("DESC","Maintenance_Object_API.Get_Mch_Name","MCH_NAME1");
               cmd.addParameter("HEAD_CONTRACT", mchContract);
               cmd.addParameter("MCH_CODE", mchCode);

               trans = mgr.validate(trans);

               descrip =   trans.getValue("DESC/DATA/MCH_NAME1");
            }


         }

         txt = (mgr.isEmpty(mchCode ) ? "" :mchCode ) + "^" +
               (mgr.isEmpty(descrip ) ? "" :descrip ) + "^" +
               (mgr.isEmpty(mchContract ) ? "" :mchContract ) + "^" +
               (mgr.isEmpty(mchContract ) ? "" :mchContract ) + "^";

         mgr.responseWrite(txt);
      }
      else if ("TEST_POINT_ID".equals(val))
      {
         trans.clear();
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
      else if ("PART_NO".equals(val))
      {
         trans.clear();

         String sAgrId = mgr.readValue("AGREEMENT_ID");    
         String sCusNo = mgr.readValue("CUSTOMER_NO");
         String sCont = mgr.readValue("SPARE_CONTRACT");
         double nValListPrice = 0;

         String nQty = mgr.readValue("QTY_PLAN");
         double qty = mgr.readNumberValue("QTY_PLAN");
         if (isNaN(qty))
            nQty = "0";

         String stListPrice = "";
         String sDefCondiCode= "";
         String activeInd = "";

         cmd = trans.addCustomFunction("GETCONDCODEUSAGEDB","PART_CATALOG_API.Get_Condition_Code_Usage_Db","COND_CODE_USAGE");
         cmd.addParameter("PART_NO");

         cmd = trans.addCustomFunction("DEFCONDCODE","CONDITION_CODE_API.Get_Default_Condition_Code","CONDITION_CODE");

         trans = mgr.validate(trans);
         if ("ALLOW_COND_CODE".equals(trans.getValue("GETCONDCODEUSAGEDB/DATA/COND_CODE_USAGE")))
         {
            sDefCondiCode = trans.getValue("DEFCONDCODE/DATA/CONDITION_CODE");
         }
         trans.clear();

         if (checksec("Inventory_Part_API.Get_Description",1))
         {
            cmd = trans.addCustomFunction("PPGD1", "Inventory_Part_API.Get_Description", "SPAREDESCRIPTION" );
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");
         }
         if (checksec("PURCHASE_PART_API.Get_Inventory_Flag",2))
         {
            cmd = trans.addCustomFunction("PPGD2", "PURCHASE_PART_API.Get_Inventory_Flag", "INVENTORYFLAG" );
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");
         }
         if (checksec("Inventory_Part_API.Get_Dim_Quality",3))
         {
            cmd = trans.addCustomFunction("PPGD3", "Inventory_Part_API.Get_Dim_Quality", "DIMQUALITY" );
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");
         }
         if (checksec("Inventory_Part_API.Get_Type_Designation",4))
         {
            cmd = trans.addCustomFunction("PPGD4", "Inventory_Part_API.Get_Type_Designation", "TYPEDESIGNATION" );
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");
         }
         if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",5))
         {
            cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
            cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
            cmd.addParameter("PART_NO");
            cmd.addParameter("CONFIGURATION");
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
            //cmd.addParameter("CONDITION_CODE",sDefCondiCode);
            cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));  
         }
         if (checksec("Purchase_Part_Supplier_API.Get_Unit_Meas",6))
         {
            cmd = trans.addCustomFunction("PPGD6", "Purchase_Part_Supplier_API.Get_Unit_Meas", "UNITMEAS" );
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");
         }
         if (checksec("Sales_Part_API.Get_Catalog_No_For_Part_No",7))
         {
            cmd = trans.addCustomFunction("PPGD7", "Sales_Part_API.Get_Catalog_No_For_Part_No", "CATALOG_NO" );
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");
            cmd = trans.addCustomFunction("GETACT","Sales_Part_API.Get_Activeind","ACTIVEIND");
            cmd.addParameter("ITEM0_CONTRACT");
            cmd.addReference("CATALOG_NO","PPGD7/DATA");      
            cmd = trans.addCustomFunction("GETACTDB","Active_Sales_Part_API.Encode","ACTIVEIND_DB");
            cmd.addReference("ACTIVEIND","GETACT/DATA");
         }

         if (checksec("SALES_PART_API.Get_Catalog_Desc",8))
         {
            cmd = trans.addCustomFunction("CATDESC", "SALES_PART_API.Get_Catalog_Desc", "SALESPARTDESCRIPTION" );
            cmd.addParameter("ITEM0_CONTRACT");
            cmd.addReference("CATALOG_NO","PPGD7/DATA");      
         }

         if (checksec("SALES_PART_API.Get_List_Price",9))
         {
            cmd = trans.addCustomFunction("LISPRI", "SALES_PART_API.Get_List_Price", "LISTPRICE" );
            cmd.addParameter("ITEM0_CONTRACT");
            cmd.addReference("CATALOG_NO","PPGD7/DATA");
         }

         if (checksec("Purchase_Part_API.Get_Description",10))
         {
            cmd = trans.addCustomFunction("PURPRI", "Purchase_Part_API.Get_Description", "SPAREDESCRIPTION" );
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");
         }

         if (checksec("INVENTORY_PART_API.Part_Exist",11))
         {
            cmd = trans.addCustomFunction("CHKEX", "INVENTORY_PART_API.Part_Exist", "CHCKEXIST" );
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");
         }

         if (checksec("INVENTORY_PART_API.Get_Supply_Code",12))
         {
            cmd = trans.addCustomFunction("SUUPCODE","INVENTORY_PART_API.Get_Supply_Code","SUPPLY_CODE");
            cmd.addParameter("ITEM0_CONTRACT");
            cmd.addReference("CATALOG_NO","PPGD7/DATA");
         }

         trans = mgr.validate(trans);
         ref = 0;
         String strDesc;
         String strInvFlg;
         String strDim;
         String strTypeDesc;
         String strQtyonHand;
         String strUnit;
         String strSlsPartNo;
         String strCatDesc;
         String numListPrice;

         if (isSecure[ref += 1] =="true")
            strDesc = trans.getValue("PPGD1/DATA/SPAREDESCRIPTION");
         else
            strDesc = "";

         if (isSecure[ref += 1] =="true")
            strInvFlg = trans.getValue("PPGD2/DATA/INVENTORYFLAG");
         else
            strInvFlg = "";

         if (isSecure[ref += 1] =="true")
            strDim = trans.getValue("PPGD3/DATA/DIMQUALITY");
         else
            strDim = "";

         if (isSecure[ref += 1] =="true")
            strTypeDesc = trans.getValue("PPGD4/DATA/TYPEDESIGNATION");
         else
            strTypeDesc = "";

         if (isSecure[ref += 1] =="true")
            strQtyonHand = trans.getBuffer("INVONHAND/DATA").getFieldValue("QTYONHAND");
         else
            strQtyonHand = "";

         if (isSecure[ref += 1] =="true")
            strUnit = trans.getValue("PPGD6/DATA/UNITMEAS");
         else
            strUnit = "";

         if (isSecure[ref += 1] =="true")
         {
            strSlsPartNo = trans.getValue("PPGD7/DATA/CATALOG_NO");
            activeInd = trans.getValue("GETACTDB/DATA/ACTIVEIND_DB");
         }
         else
            strSlsPartNo = "";

         if (isSecure[ref += 1] =="true")
            strCatDesc = trans.getValue("CATDESC/DATA/SALESPARTDESCRIPTION");
         else
            strCatDesc = "";

         if (isSecure[ref += 1] =="true")
            numListPrice = trans.getValue("LISPRI/DATA/LISTPRICE");
         else
            numListPrice = "";

         if ((isSecure[ref += 1] =="true")&&(mgr.isEmpty(strDesc)))
            strDesc = trans.getValue("PURPRI/DATA/SPAREDESCRIPTION");
         else if (mgr.isEmpty(strDesc))
            strDesc = "";

         int nInventoryFlag;

         if (isSecure[ref += 1] =="true")
            nInventoryFlag = toInt(trans.getNumberValue("CHKEX/DATA/CHCKEXIST"));
         else
            nInventoryFlag  = 0;         

         String suppCode;

         if (isSecure[ref += 1] =="true")
            suppCode = trans.getValue("SUUPCODE/DATA/SUPPLY_CODE");
         else
            suppCode    = "";

         trans.clear();

         double sCostVal = 0;
         String sSuppID = "";

         if (nInventoryFlag == 1)
         {
            if (checksec("Active_Separate_API.Get_Inventory_Value",1))
            {
               /*cmd = trans.addCustomFunction("COSTBUFFONE","Inventory_Part_API.Get_Inventory_Value_By_Method","COST");
               cmd.addParameter("SPARE_CONTRACT");
               cmd.addParameter("PART_NO");   */

               cmd = trans.addCustomCommand("COSTBUFFONE","Active_Separate_API.Get_Inventory_Value");
               cmd.addParameter("COST");
               cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));
               cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
               cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
               cmd.addParameter("CONFIGURATION_IDS",mgr.isEmpty(mgr.readValue("CONFIGURATION_IDS"))?"*":mgr.readValue("CONFIGURATION_IDS"));
               cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));
            }

            trans = mgr.validate(trans);
            ref = 0;

            if (isSecure[ref += 1] =="true")
               sCostVal = trans.getNumberValue("COSTBUFFONE/DATA/COST");
            else
               sCostVal = 0;             
         }
         else
         {
            if (checksec("Purchase_Order_Line_Util_API.Get_Primary_Supplier",1))
            {
               cmd = trans.addCustomFunction("SUPPCODD_ID","Purchase_Order_Line_Util_API.Get_Primary_Supplier","SUPPLIER_ID");
               cmd.addParameter("SPARE_CONTRACT");
               cmd.addParameter("PART_NO");
            }

            trans = mgr.validate(trans);
            ref = 0;

            if (isSecure[ref += 1] =="true")
               sSuppID = trans.getValue("SUPPCODD_ID/DATA/SUPPLIER_ID");
            else
               sSuppID  = "";
         }

         trans.clear();

         if (!mgr.isEmpty(sSuppID))
         {
            if (checksec("Purchase_Order_Line_Util_API.Get_Base_Price_Supp_Part",1))
            {
               cmd = trans.addQuery("SYSDATEBUFF","select SYSDATE F_SYSDATE from dual"); 

               cmd = trans.addCustomCommand("SCOSTVAL","Purchase_Order_Line_Util_API.Get_Base_Price_Supp_Part");
               cmd.addParameter("COST");
               cmd.addParameter("PART_NO");
               cmd.addParameter("SPARE_CONTRACT");
               cmd.addParameter("SUPPLIER_ID", sSuppID);
               cmd.addParameter("QTY_PLAN");
               cmd.addReference("F_SYSDATE","SYSDATEBUFF/DATA");          
            }

            trans = mgr.validate(trans);
            ref = 0;

            if (isSecure[ref += 1] =="true")
               sCostVal = trans.getNumberValue("SCOSTVAL/DATA/COST");
         }

         ref = 0; 
         trans.clear();

         cmd = trans.addCustomFunction("PMSITE","Pm_Action_API.Get_Contract","PM_SITE");
         cmd.addParameter("ITEM0_PM_NO",mgr.readValue("ITEM0_PM_NO"));
         cmd.addParameter("ITEM0_PM_REVISION",mgr.readValue("ITEM0_PM_REVISION"));

         cmd = trans.addCustomFunction("CATEXT","Sales_Part_API.Check_Exist","CAT_EXIST");
         cmd.addReference("PM_SITE","PMSITE/DATA");
         cmd.addParameter("CATALOG_NO",strSlsPartNo);

         trans = mgr.validate(trans);

         double nExist = trans.getNumberValue("CATEXT/DATA/CAT_EXIST");

         if (nExist != 1)
         {
            strSlsPartNo = "";
            strCatDesc = "";
            numListPrice = "";
            suppCode = "";
         }

         trans.clear();
         cmd = trans.addCustomCommand("MATREQLINE","Material_Requis_Line_API.CHECK_PART_NO__");
         cmd.addParameter("SPAREDESCRIPTION", strDesc);
         cmd.addParameter("SUPPLY_CODE", suppCode);
         cmd.addParameter("UNITMEAS", strUnit);
         cmd.addParameter("PART_NO", mgr.readValue("PART_NO"));
         cmd.addParameter("SPARE_CONTRACT", mgr.readValue("SPARE_CONTRACT"));

         if (mgr.isEmpty(strDesc))
         {
            if (checksec("PM_ACTION_SPARE_PART_API.Description_For_Part",1))
            {
               cmd = trans.addCustomCommand("SECPARTDESC","PM_ACTION_SPARE_PART_API.Description_For_Part");
               cmd.addParameter("PART_NO");
               cmd.addParameter("SPARE_CONTRACT");
               cmd.addParameter("SPAREDESCRIPTION");
            }

         }

         mgr.perform(trans);

         if (mgr.isEmpty(strDesc))
         {
            ref = 0; 
            if (isSecure[ref += 1] =="true")
               strDesc = trans.getValue("SECPARTDESC/DATA/SPAREDESCRIPTION");
         }

         if (!mgr.isEmpty(sAgrId))
         {
            nValListPrice = getRevenue(sAgrId,sCusNo,nQty,sCont,strSlsPartNo);
            stListPrice = mgr.getASPField("LISTPRICE").formatNumber(nValListPrice);
         }

         String strCostVal = mgr.getASPField("COST").formatNumber(sCostVal);
         txt =   (mgr.isEmpty(strDesc) ? "" : (strDesc))+ "^" + 
                 (mgr.isEmpty(strInvFlg) ? "" : (strInvFlg))+ "^" + 
                 (mgr.isEmpty(strDim) ? "" : (strDim))+ "^" + 
                 (mgr.isEmpty(strTypeDesc) ? "" : (strTypeDesc))+ "^" + 
                 (mgr.isEmpty(strQtyonHand) ? "" : (strQtyonHand))+ "^" + 
                 (mgr.isEmpty(strUnit) ? "" : (strUnit))+ "^" + 
                 (mgr.isEmpty(strSlsPartNo) ? "" : (strSlsPartNo))+ "^" + 
                 (mgr.isEmpty(strCatDesc) ? "" : (strCatDesc))+ "^" + 
                 (mgr.isEmpty(stListPrice) ? "" : (stListPrice))+ "^" + 
                 (mgr.isEmpty(strCostVal) ? "" : (strCostVal))+ "^"+ 
                 (mgr.isEmpty(sDefCondiCode) ? "": sDefCondiCode) + "^"+
                 (mgr.isEmpty(activeInd) ? "": activeInd) + "^";

         mgr.responseWrite( txt );
      }
      else if ("CONDITION_CODE".equals(val))
      {
         String qtyOnHand = "";
         double salesPaCost;

         if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",1))
         {
            cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
            cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
            cmd.addParameter("PART_NO");
            cmd.addParameter("CONFIGURATION");
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
         String salesPaCostS = mgr.readValue("COST","");

         trans.clear();

         cmd = trans.addCustomCommand("SALESPARTCOST","Active_Separate_API.Get_Inventory_Value");
         cmd.addParameter("COST");
         cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));
         cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
         cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
         cmd.addParameter("CONFIGURATION_IDS",mgr.isEmpty(mgr.readValue("CONFIGURATION_IDS"))?"*":mgr.readValue("CONFIGURATION_IDS"));
         cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));

         trans = mgr.validate(trans);

         salesPaCost =  trans.getNumberValue("SALESPARTCOST/DATA/COST");

         salesPaCostS = mgr.getASPField("COST").formatNumber(salesPaCost);

         txt = (mgr.isEmpty(ccDesc) ? "" : (ccDesc)) + "^" +
               (mgr.isEmpty(qtyOnHand) ? "": qtyOnHand) + "^" +
               (mgr.isEmpty(salesPaCostS) ? "": salesPaCostS)+ "^" ;  

         mgr.responseWrite(txt);
      }
      else if ("PART_OWNERSHIP".equals(val))
      {
         cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
         cmd.addParameter("PART_OWNERSHIP");
         String qtyOnHand = "";

         if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",1))
         {
            cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
            cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
            cmd.addParameter("PART_NO");
            cmd.addParameter("CONFIGURATION");
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
         String qtyOnHand = "";
         if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",1))
         {
            cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
            cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
            cmd.addParameter("PART_NO");
            cmd.addParameter("CONFIGURATION");
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
            cmd.addParameter("CONDITION_CODE");
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
      else if ("PARAMETER_CODE".equals(val))
      {
         trans.clear();
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
         txt=(mgr.isEmpty(paradesc)? "":paradesc)+ "^" + (mgr.isEmpty(type )? "":type )+ "^" + (mgr.isEmpty(uncode )? "":uncode )+ "^" ;

         mgr.responseWrite(txt);
      }
      else if ("CATALOG_NO".equals(val))
      {
         trans.clear();

         if (checksec("SALES_PART_API.Get_Catalog_Desc",1))
         {
            cmd = trans.addCustomFunction("CATDESC", "SALES_PART_API.Get_Catalog_Desc", "SALESPARTDESCRIPTION" );
            cmd.addParameter("ITEM0_CONTRACT");
            cmd.addParameter("CATALOG_NO");      
         }
         if (checksec("SALES_PART_API.Get_List_Price",2))
         {
            cmd = trans.addCustomFunction("LISPRI", "SALES_PART_API.Get_List_Price", "LISTPRICE" );
            cmd.addParameter("ITEM0_CONTRACT");
            cmd.addParameter("CATALOG_NO");
         }

         trans = mgr.validate(trans);
         ref = 0;
         String strCatDesc;
         String numListPrice;

         if (isSecure[ref += 1] =="true")
            strCatDesc = trans.getValue("CATDESC/DATA/SALESPARTDESCRIPTION");
         else
            strCatDesc = "";

         if (isSecure[ref += 1] =="true")
            numListPrice = trans.getValue("LISPRI/DATA/LISTPRICE");
         else
            numListPrice = "";

         String sAgrId = mgr.readValue("AGREEMENT_ID");    
         String sCusNo = mgr.readValue("CUSTOMER_NO");
         String sCont = mgr.readValue("SPARE_CONTRACT");
         String strSlsPartNo = mgr.readValue("CATALOG_NO");
         double nValListPrice = 0;

         String nQty = mgr.readValue("QTY_PLAN");
         double qty = mgr.readNumberValue("QTY_PLAN");
         if (isNaN(qty))
            nQty = "0";

         String stListPrice = "";

         if (!mgr.isEmpty(sAgrId))
         {
            nValListPrice = getRevenue(sAgrId,sCusNo,nQty,sCont,strSlsPartNo);
            numListPrice = mgr.getASPField("LISTPRICE").formatNumber(nValListPrice);
         }

         txt=(mgr.isEmpty(strCatDesc)? "":strCatDesc)+ "^" + (mgr.isEmpty(numListPrice)? "":numListPrice)+ "^";
         mgr.responseWrite(txt);      
      }
      else if (("PERS_BUDGET_COST".equals(val)) ||  ("MAT_BUDGET_COST".equals(val)) || ("EXT_BUDGET_COST".equals(val)))
      {
         trans.clear();
         double nPersCost;
         double nMatCost;
         double nExtCost;
         double nTotCost;
         if (!mgr.isEmpty("PERS_BUDGET_COST"))
            nPersCost = mgr.readNumberValue("PERS_BUDGET_COST");
         else
            nPersCost = 0; 
         if (!mgr.isEmpty("MAT_BUDGET_COST"))
            nMatCost = mgr.readNumberValue("MAT_BUDGET_COST");
         else
            nMatCost    = 0;  
         if (!mgr.isEmpty("EXT_BUDGET_COST"))
            nExtCost = mgr.readNumberValue("EXT_BUDGET_COST");
         else
            nExtCost    = 0;  

         nTotCost = nPersCost + nMatCost + nExtCost;
         txt= mgr.formatNumber("TOT_BUDGET_COST",nTotCost) + "^";

         mgr.responseWrite(txt);
      }
      else if ("PLAN_MAT_COST".equals(val))
      {
         trans.clear();
         cmd = trans.addCustomCommand("TOTBUDG", "PM_Action_Spare_Part_API.Get_Plan_Inv_Part_Cost");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");

         trans = mgr.validate(trans);
         double totbudget = trans.getNumberValue("TOTBUDG/DATA/TOT_BUDGET_COST");
         txt= mgr.formatNumber("TOT_BUDGET_COST", totbudget) + "^";

         mgr.responseWrite(txt);
      }
      else if ("PLAN_EXT_COST".equals(val))
      {
         trans.clear();
         cmd = trans.addCustomCommand("TOTINVVAL", "PM_Action_Spare_Part_API.Get_Plan_Ext_Cost");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");

         trans = mgr.validate(trans);
         double totinven = trans.getNumberValue("TOTINVVAL/DATA/PLAN_EXT_COST");
         txt=mgr.formatNumber("PLAN_EXT_COST", totinven) + "^";

         mgr.responseWrite(txt);
      }
      else if ("PLAN_PERS_REV".equals(val))
      {
         trans.clear();
         cmd = trans.addCustomCommand("PLANPERREV", "PM_Action_ROLE_API.Get_Plan_Pers_Revenue__");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");

         trans = mgr.validate(trans);
         double planpersrev = trans.getNumberValue("PLANPERREV/DATA/PLAN_PERS_REV");
         txt= mgr.formatNumber("PLAN_PERS_REV", planpersrev) + "^";

         mgr.responseWrite(txt);
      }
      else if ("PLAN_MAT_REV".equals(val))
      {
         trans.clear();
         cmd = trans.addCustomCommand("PLANMATREV", "PM_Action_Spare_Part_API.Get_Plan_Mat_Revenue");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");

         trans = mgr.validate(trans);
         double planmatrev = trans.getNumberValue("PLANMATREV/DATA/PLAN_MAT_REV");
         txt=mgr.formatNumber("PLAN_MAT_REV", planmatrev) + "^";

         mgr.responseWrite(txt);
      }
      else if ("PLAN_EXT_REV".equals(val))
      {
         trans.clear();
         cmd = trans.addCustomCommand("PLANEXTREV", "PM_Action_Spare_Part_API.Get_Plan_Ext_Revenue");
         cmd.addParameter("ITEM5_PM_NO");
         cmd.addParameter("ITEM5_PM_REVISION");

         trans = mgr.validate(trans);
         double planextrev = trans.getNumberValue("PLANEXTREV/DATA/PLAN_EXT_REV");
         txt=mgr.formatNumber("PLAN_EXT_REV", planextrev) + "^";

         mgr.responseWrite(txt);
      }
      else if ("QTY_PLAN".equals(val))
      {
         String sAgrId = mgr.readValue("AGREEMENT_ID");    
         String sCusNo = mgr.readValue("CUSTOMER_NO");
         String sCont = mgr.readValue("SPARE_CONTRACT");
         String strSlsPartNo = mgr.readValue("CATALOG_NO");
         String sPartNo = mgr.readValue("PART_NO");
         double nValListPrice = 0;
         double nCost = 0;

         String nQty = mgr.readValue("QTY_PLAN");
         double qty = mgr.readNumberValue("QTY_PLAN");
         if (isNaN(qty))
            nQty = "0";

         String strListPrice = "";
         String strCost = "";

         nCost = getCost(sCont,sPartNo,nQty);
         strCost = mgr.getASPField("COST").formatNumber(nCost);

         if (!mgr.isEmpty(sAgrId))
         {
            nValListPrice = getRevenue(sAgrId,sCusNo,nQty,sCont,strSlsPartNo);
            strListPrice = mgr.getASPField("LISTPRICE").formatNumber(nValListPrice);
         }
         txt=(mgr.isEmpty(strCost)? "":strCost)+ "^" + 
             (mgr.isEmpty(strListPrice )? "":strListPrice )+ "^" ;

         mgr.responseWrite(txt);
      }
      else if ("SPARE_CONTRACT".equals(val))
      {
         trans.clear();

         String sAgrId = mgr.readValue("AGREEMENT_ID");    
         String sCusNo = mgr.readValue("CUSTOMER_NO");
         String sCont = mgr.readValue("SPARE_CONTRACT");
         String strSlsPartNo = mgr.readValue("CATALOG_NO");
         double nValListPrice = 0;

         String nQty = mgr.readValue("QTY_PLAN");
         double qty = mgr.readNumberValue("QTY_PLAN");
         if (isNaN(qty))
            nQty = "0";

         String strListPrice = "";

         if (!mgr.isEmpty(sAgrId))
         {
            nValListPrice = getRevenue(sAgrId,sCusNo,nQty,sCont,strSlsPartNo);
            strListPrice = mgr.getASPField("LISTPRICE").formatNumber(nValListPrice);
         }

         txt = (mgr.isEmpty(strListPrice) ? "" : (strListPrice))+ "^" ;
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

         cmd = trans.addCustomFunction("DESC","Standard_Job_API.Get_Definition","ITEM6_DESCRIPTION");
         cmd.addParameter("STD_JOB_ID",stdid);
         cmd.addParameter("STD_JOB_CONTRACT",stdcont);
         cmd.addParameter("STD_JOB_REVISION",stdrev);

         cmd = trans.addCustomFunction("WORKDESC","Standard_Job_API.Get_Work_description","ITEM6_DESCRIPTION");
         cmd.addParameter("STD_JOB_ID",stdid);
         cmd.addParameter("STD_JOB_CONTRACT",stdcont);
         cmd.addParameter("STD_JOB_REVISION",stdrev);

         cmd = trans.addCustomFunction("GETSTATUS","Standard_Job_API.Get_Status","STD_JOB_STATUS");
         cmd.addParameter("STD_JOB_ID",stdid);
         cmd.addParameter("STD_JOB_CONTRACT",stdcont);
         cmd.addParameter("STD_JOB_REVISION",stdrev);
         trans = mgr.validate(trans);

         String definition =   trans.getValue("DESC/DATA/DESCRIPTION");  
         String workDesc =   trans.getValue("WORKDESC/DATA/DESCRIPTION");  
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

         cmd = trans.addCustomFunction("DESC","Standard_Job_API.Get_Definition","ITEM6_DESCRIPTION");
         cmd.addParameter("STD_JOB_ID");
         cmd.addParameter("STD_JOB_CONTRACT");
         cmd.addParameter("STD_JOB_REVISION");

         cmd = trans.addCustomFunction("WORKDESC","Standard_Job_API.Get_Work_description","ITEM6_DESCRIPTION");
         cmd.addParameter("STD_JOB_ID");
         cmd.addParameter("STD_JOB_CONTRACT");
         cmd.addParameter("STD_JOB_REVISION");

         cmd = trans.addCustomFunction("GETSTATUS","Standard_Job_API.Get_Status","STD_JOB_STATUS");
         cmd.addParameter("STD_JOB_ID");
         cmd.addParameter("STD_JOB_CONTRACT");
         cmd.addParameter("STD_JOB_REVISION");

         trans = mgr.validate(trans);

         String definition =   trans.getValue("DESC/DATA/DESCRIPTION");  
         String workDesc =   trans.getValue("WORKDESC/DATA/DESCRIPTION");  
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
      else if ("SIGN".equals(val))
      {

         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[2];
         String emp_id = "";
         String sign = "";

         String new_sign = mgr.readValue("SIGN","");

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
            cmd.addParameter("ITEM6_COMPANY");
            cmd.addParameter("SIGN");

            trans = mgr.perform(trans);

            sign = mgr.readValue("SIGN");
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

         txt = (mgr.isEmpty (sCalendar) ? "" : sCalendar) + "^" + 
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

         if (checksec("SC_SERVICE_CONTRACT_API.Get_Contract_Name",1))
         {
            cmd = trans.addCustomFunction("GETCONTRACTNAME", "SC_SERVICE_CONTRACT_API.Get_Contract_Name", "CONTRACT_NAME");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }

         if (checksec("PSC_CONTR_PRODUCT_API.Get_Description",1))
         {
            cmd = trans.addCustomFunction("GETLINEDESC", "PSC_CONTR_PRODUCT_API.Get_Description", "LINE_DESC");
            cmd.addParameter("CONTRACT_ID", sContractId);
            cmd.addParameter("LINE_NO", sLineNo);
         }

         if (checksec("SC_SERVICE_CONTRACT_API.Get_Contract_Type",1))
         {
            cmd = trans.addCustomFunction("GETCONTRACTYPE", "SC_SERVICE_CONTRACT_API.Get_Contract_Type", "CONTRACT_TYPE");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }

         if (checksec("PSC_CONTR_PRODUCT_API.Get_Invoice_Type",1))
         {
            cmd = trans.addCustomFunction("GETINVOICETYPE", "PSC_CONTR_PRODUCT_API.Get_Invoice_Type", "INVOICE_TYPE");
            cmd.addParameter("CONTRACT_ID", sContractId);
            cmd.addParameter("LINE_NO", sLineNo );
         }

         if (checksec("SC_SERVICE_CONTRACT_API.Get_Customer_Id",1))
         {
            cmd = trans.addCustomFunction("GETSRVCONCUST", "SC_SERVICE_CONTRACT_API.Get_Customer_Id", "CUSTOMER_NO");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }

         if (checksec("CUSTOMER_INFO_API.Get_Name",1))
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
                (mgr.isEmpty(sSrvconCust)?"":sSrvconCust) + "^" + (mgr.isEmpty(sSrvconCustName)?"":sSrvconCustName) + "^";

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

         if (checksec("SC_SERVICE_CONTRACT_API.Get_Contract_Name",1))
         {
            cmd = trans.addCustomFunction("GETCONTRACTNAME", "SC_SERVICE_CONTRACT_API.Get_Contract_Name", "CONTRACT_NAME");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }

         if (checksec("PSC_CONTR_PRODUCT_API.Get_Description",1))
         {
            cmd = trans.addCustomFunction("GETLINEDESC", "PSC_CONTR_PRODUCT_API.Get_Description", "LINE_DESC");
            cmd.addParameter("CONTRACT_ID", sContractId);
            cmd.addParameter("LINE_NO", sLineNo);
         }

         if (checksec("SC_SERVICE_CONTRACT_API.Get_Contract_Type",1))
         {
            cmd = trans.addCustomFunction("GETCONTRACTYPE", "SC_SERVICE_CONTRACT_API.Get_Contract_Type", "CONTRACT_TYPE");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }

         if (checksec("PSC_CONTR_PRODUCT_API.Get_Invoice_Type",1))
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

   public double getRevenue(String sAgrId, String sCusNo, String nQty, String sCont, String sCatNo)
   {
      ASPManager mgr = getASPManager();
      int ref = 0;
      String valCurrCode = null;
      String sPriceList = null;
      double sRevenue = 0;
      String sAgrCurrCost = "";
      String sCustCurrCode = "";

      trans.clear();

      if ((checksec("'Customer_Agreement_API.Get_Currency_Code",1)) && (checksec("Cust_Ord_Customer_API.Get_Currency_Code",2)))
      {
         cmd = trans.addCustomFunction("AGRCOST", "Customer_Agreement_API.Get_Currency_Code", "AGRCURRCODE" );
         cmd.addParameter("AGREEMENT_ID");
         cmd = trans.addCustomFunction("COUSTCOST", "Cust_Ord_Customer_API.Get_Currency_Code", "CUSTCURRCODE" );
         cmd.addParameter("CUSTOMER_NO");
      }

      trans = mgr.perform(trans);

      if (isSecure[ref += 1] =="true")
         sAgrCurrCost = trans.getValue("AGRCOST/DATA/AGRCURRCODE");
      else
         sAgrCurrCost = ""; 

      if (isSecure[ref += 1] =="true")
         sCustCurrCode = trans.getValue("COUSTCOST/DATA/CUSTCURRCODE");
      else
         sCustCurrCode = ""; 

      if (mgr.isEmpty(sAgrCurrCost))
         valCurrCode = sCustCurrCode;
      else
         valCurrCode = sAgrCurrCost;

      trans.clear();

      if (checksec("'Customer_Order_Pricing_Api.Get_Valid_Price_List",3))
      {
         cmd = trans.addCustomFunction("CURRCOST", "Customer_Order_Pricing_Api.Get_Valid_Price_List", "PRICELIST" );
         cmd.addParameter("SPARE_CONTRACT", sCont);
         cmd.addParameter("CATALOG_NO", sCatNo);   
         cmd.addParameter("CUSTOMER_NO", sCusNo);
         cmd.addParameter("CURRENCY_CODE", valCurrCode);
      }

      trans = mgr.perform(trans);

      if (isSecure[ref += 1] =="true")
         sPriceList = trans.getValue("CURRCOST/DATA/PRICELIST");

      trans.clear();

      cmd = trans.addCustomFunction("REVENUE", "Work_Order_Coding_Utility_API.Get_Price", "PREVENUE" );
      cmd.addParameter("SPARE_CONTRACT", sCont);
      cmd.addParameter("CATALOG_NO", sCatNo);   
      cmd.addParameter("CUSTOMER_NO", sCusNo);
      cmd.addParameter("AGREEMENT_ID", sAgrId);
      cmd.addParameter("PRICELIST", sPriceList);
      cmd.addParameter("QTY_PLAN", nQty);

      trans = mgr.perform(trans);
      sRevenue = trans.getNumberValue("REVENUE/DATA/PREVENUE");

      return sRevenue;
   }


// ************************ Function getCost() ******************************

   public double getCost(String sCont, String sPartNo, String nQty)
   {
      trans.clear();
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer testbuff = mgr.newASPTransactionBuffer();
      testbuff.addSecurityQuery("INVENTORY_PART");
      mgr.perform(testbuff);
      ASPBuffer sec = testbuff.getSecurityInfo();

      double nInvPart = 0;
      double nCost = 0;
      double nInstall = 0; 
      double nPrimSup = 0;
      double nBasePrice = 0;
      String sObject = "";
      String sMethod = "";
      String strPrimSup = "";

      String serialNo = mgr.readValue("SERIAL_NO");
      String conditionCode = mgr.readValue("CONDITION_CODE"); 
      String configurationId = mgr.readValue("CONFIGURATION_IDS");

      if (sec.itemExists("INVENTORY_PART"))
      {
         cmd = trans.addCustomFunction("INPARTEX", "INVENTORY_PART_API.Part_Exist", "INPART" );
         cmd.addParameter("SPARE_CONTRACT", sCont);
         cmd.addParameter("PART_NO", sPartNo);   

         trans = mgr.perform(trans);
         nInvPart = trans.getNumberValue("INPARTEX/DATA/INPART");
      }

      trans.clear();


      if (nInvPart == 1)
      {
         if (sec.itemExists("INVENTORY_PART"))
         {
            /*cmd = trans.addCustomFunction("INPARTME", "INVENTORY_PART_API.Get_Inventory_Value_By_Method", "INPAMEVAL" );
            cmd.addParameter("SPARE_CONTRACT", sCont);
            cmd.addParameter("PART_NO", sPartNo); */

            cmd = trans.addCustomCommand("INPARTME","Active_Separate_API.Get_Inventory_Value");
            cmd.addParameter("INPAMEVAL");
            cmd.addParameter("SPARE_CONTRACT",sCont);
            cmd.addParameter("PART_NO",sPartNo);
            cmd.addParameter("SERIAL_NO",serialNo);
            cmd.addParameter("CONFIGURATION_IDS",mgr.isEmpty(mgr.readValue("CONFIGURATION_IDS"))?"*":mgr.readValue("CONFIGURATION_IDS"));
            cmd.addParameter("CONDITION_CODE",conditionCode);

            trans = mgr.perform(trans);
            nCost = trans.getNumberValue("INPARTME/DATA/INPAMEVAL");
         }
      }
      else
      {
         if (sec.itemExists("PURCHASE_PART"))
         {
            sObject = "Purchase_Order_Line_Util_Api";
            sMethod =  "Get_Primary_Supplier";

            cmd = trans.addCustomFunction("ISINSTALL", "Transaction_SYS.Method_Is_Installed", "ISINST" );
            cmd.addParameter("PACKAGE_NAME", sObject);
            cmd.addParameter("METHOD_NAME", sMethod);   

            trans = mgr.perform(trans);                                                  
            nInstall = trans.getNumberValue("ISINSTALL/DATA/ISINST");
         }
         trans.clear();
         if (nInstall == 1)
         {
            cmd = trans.addCustomFunction("GETSUP", "Purchase_Order_Line_Util_API.Get_Primary_Supplier", "PRIMSUP" );
            cmd.addParameter("SPARE_CONTRACT", sCont);
            cmd.addParameter("PART_NO", sPartNo);   

            trans = mgr.perform(trans);
            strPrimSup = trans.getValue("GETSUP/DATA/PRIMSUP");
         }
         trans.clear();
         if (!mgr.isEmpty(strPrimSup))
         {
            cmd = trans.addQuery("SYSD","SELECT sysdate PRICE_DATE FROM DUAL");

            cmd = trans.addCustomFunction("BAPRI", "Purchase_Order_Line_Util_API.Get_Base_Price_Supp_Part", "BASEPRI" );
            cmd.addParameter("BASE_PRICE");   
            cmd.addParameter("PART_NO", sPartNo);   
            cmd.addParameter("SPARE_CONTRACT", sCont);
            cmd.addParameter("VENDOER_NO", strPrimSup);         
            cmd.addParameter("QTY_PLAN", nQty);   
            cmd.addReference("PRICE_DATE","SYSD/DATA");   

            trans = mgr.perform(trans);
            nCost = trans.getNumberValue("BAPRI/DATA/BASEPRI");
         }
      }

      return nCost;
   }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

   public void newRow()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addCustomFunction("HED","Pm_Type_API.Get_Client_Value(1)", "PM_TYPE");

      cmd = trans.addEmptyCommand("HEAD", "PM_ACTION_API.New__", headblk);
      cmd.setOption("ACTION", "PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("HEAD/DATA");

      data.setFieldItem("PM_TYPE", trans.getValue("HED/DATA/PM_TYPE"));
      headset.addRow(data);
   }

//-----------------------------------------------------------------------------
//----------------------  CMDBAR EDIT-IT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

   public void newRowITEM0()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("ITEM0","PM_ACTION_SPARE_PART_API.New__",itemblk0);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM0/DATA");
      data.setFieldItem("ITEM0_PM_NO",headset.getRow().getValue("PM_NO"));
      data.setFieldItem("ITEM0_PM_REVISION",headset.getRow().getValue("PM_REVISION"));
      data.setFieldItem("ITEM0_CONTRACT",headset.getRow().getValue("ORG_CONTRACT"));
      itemset0.addRow(data);
   }

   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("ITEM1","PM_ACTION_CALENDAR_PLAN_API.New__",itemblk1);
      cmd.setOption("ACTION","PREPARE");
      cmd.addParameter("ITEM1_PM_NO", headset.getRow().getValue("PM_NO"));
      cmd.addParameter("ITEM1_PM_REVISION", headset.getRow().getValue("PM_REVISION"));

      trans = mgr.perform(trans);

      data = trans.getBuffer("ITEM1/DATA");
      itemset1.addRow(data);
   }

   public void newRowITEM2()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("ITEM2","PM_ACTION_PERMIT_API.New__",itemblk2);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM2/DATA");
      data.setFieldItem("ITEM2_PM_NO",headset.getRow().getValue("PM_NO"));
      data.setFieldItem("ITEM2_PM_REVISION",headset.getRow().getValue("PM_REVISION"));
      itemset2.addRow(data);
   }

   public void newRowITEM3()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("ITEM3","PM_ACTION_CRITERIA_API.New__",itemblk3);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM3/DATA");
      data.setFieldItem("ITEM3_PM_NO",headset.getRow().getValue("PM_NO"));
      data.setFieldItem("ITEM3_PM_REVISION",headset.getRow().getValue("PM_REVISION"));
      data.setFieldItem("ITEM3_CONTRACT",headset.getRow().getValue("CONTRACT"));
      data.setFieldItem("ITEM3_MCH_CODE",headset.getRow().getValue("MCH_CODE"));
      itemset3.addRow(data);
   }

   public void newRowITEM6()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM6","PM_ACTION_JOB_API.New__",itemblk6);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM6_PM_NO",headset.getRow().getValue("PM_NO"));
      cmd.setParameter("ITEM6_PM_REVISION",headset.getRow().getValue("PM_REVISION"));

      trans = mgr.perform(trans);

      data = trans.getBuffer("ITEM6/DATA");
      itemset6.addRow(data);
   }

   public void duplicateITEM6()
   {
      ASPManager mgr = getASPManager();

      itemset6.goTo(itemset6.getRowSelected());

      ASPBuffer itemRowVals = itemset6.getRow();
      itemRowVals.setValue("JOB_ID","");
      itemset6.addRow(itemRowVals);

      itemlay6.setLayoutMode(itemlay6.NEW_LAYOUT);
   }

   public void deleteITEM6()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      int currrow = headset.getCurrentRowNo();

      if (itemlay6.isMultirowLayout())
         itemset6.goTo(itemset6.getRowSelected());

      itemset6.unselectRows();
      itemset6.selectRow();

      String sPmNo = headset.getRow().getValue("PM_NO");
      String sPmRevision = headset.getRow().getValue("PM_REVISION");
      String sJobNo = itemset6.getRow().getValue("JOB_ID");

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

      itemset6.setRemoved();

      trans = mgr.submit(trans);

      String rolesexist = trans.getValue("CHECKCONN/DATA/ROLES_EXIST");
      String matexist = trans.getValue("CHECKCONN/DATA/MATERIAL_EXIST");
      String toolsfacexist = trans.getValue("CHECKCONN/DATA/TOOL_FACILITY_EXIST");
      String docexist = trans.getValue("CHECKCONN/DATA/DOC_EXIST");

      String sStdJobId = trans.getValue("CHECKCONN/DATA/STD_JOB_ID");
      String sStdJobContract = trans.getValue("CHECKCONN/DATA/STD_JOB_CONTRACT");
      String sStdJobRevision = trans.getValue("CHECKCONN/DATA/STD_JOB_REVISION");

      headset.goTo(currrow);

      okFindITEM6();

      headset.goTo(currrow);

      if ("1".equals(matexist) || "1".equals(docexist))
         mgr.showAlert(mgr.translate("ROUTPMREMALLCONN: All Materials and Documents connected to this PM Action through Standard Job &1 - Site &2 - Revision &3, were removed.", sStdJobId, sStdJobContract, sStdJobRevision));
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void countFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.addWhereCondition("PM_TYPE_DB = Pm_Type_API.Get_Db_Value(1)");
      q.addWhereCondition(" ORG_CONTRACT =User_Allowed_Site_API.Authorized ( ORG_CONTRACT )");
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }

   public void okFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.addWhereCondition("PM_TYPE_DB = Pm_Type_API.Get_Db_Value(1)");
      q.addWhereCondition("ORG_CONTRACT = User_Allowed_Site_API.Authorized ( ORG_CONTRACT )");
      if (flagpm)
      {
         //Bug 58216 Start
         q.addWhereCondition("PM_NO= ?");
         q.addWhereCondition("PM_REVISION= ?");
         q.addParameter("PM_NO",pmNo);
         q.addParameter("PM_REVISION",pmRev);
         //Bug 58216 End
      }

      q.setOrderByClause("PM_NO,PM_REVISION");
      q.includeMeta("ALL");

      if (mgr.dataTransfered())
      {
         ASPBuffer retBuffer =  mgr.getTransferedData();
         if (retBuffer.itemExists("PM_NO") && retBuffer.itemExists("CONTRACT"))
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

      //Bug 67819, Start
      mgr.querySubmit(trans,headblk);
      //Bug 67819, End


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

         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         headSearch =true;
      }
      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("PCMWPMACTIONROUNDNODATAFOUND: No data found."));
         headset.clear();
      }
//    Bug 88982, Start
      else{
          int n = headset.countRows();

          	headset.first();

              for (int i=0; i<=n; ++i)
              {
              	String startUnit = headset.getRow().getValue("PM_START_UNIT");
              	if (startUnit.equals("Day")){
              	  headset.setValue("START_VALUE", headset.getRow().getFieldValue("START_DATE"), false);
              	}
                headset.next();
              }
           	headset.first();
      }
//    Bug 88982, End

      qrystr = mgr.createSearchURL(headblk); 
      trans.clear();
   }

   public void save()
   {
      ASPManager mgr = getASPManager();
      //Bug 58216 Start
      q = trans.addQuery(headblk);
      //Bug 58216 End

      currrow = headset.getCurrentRowNo();
      headset.changeRow();

      String pmStartUnit = headset.getRow().getValue("PM_START_UNIT");
      String startValue = headset.getRow().getValue("START_VALUE");
      String pmIntervalunit = headset.getRow().getValue("PM_INTERVAL_UNIT");

      if (!(mgr.isEmpty(pmStartUnit)))
      {
         ASPTransactionBuffer mytrans = mgr.newASPTransactionBuffer();
         mytrans.addQuery("YYM","select (Pm_Start_Unit_API.Get_Client_Value(2)) CLIENT_START2 from DUAL");
         mytrans.addQuery("YYW","select (Pm_Start_Unit_API.Get_Client_Value(1)) CLIENT_START1 from DUAL");
         mytrans.addQuery("YYD","select (Pm_Start_Unit_API.Get_Client_Value(0)) CLIENT_START from DUAL");
         mytrans = mgr.perform(mytrans);

         String clientValMon  = mytrans.getValue("YYM/DATA/CLIENT_START2");
         String clientValwee  = mytrans.getValue("YYW/DATA/CLIENT_START1");
         String clientValDay  = mytrans.getValue("YYD/DATA/CLIENT_START");

         mytrans.clear();

         ASPTransactionBuffer startUnitTrans = mgr.newASPTransactionBuffer();
         if (clientValMon.equals(pmStartUnit))
         {
            String tempSV = headset.getRow().getValue("START_VALUE");
            int lengthSV = tempSV.length();

            String nTYvalue = tempSV.substring(0,2);
            String nTMvalue = tempSV.substring(2,4);

            //Bug 58216 Start
            q = startUnitTrans.addQuery("XX3","select to_number(?) TEMPTY from DUAL");
            q.addParameter("START_VALUE",nTYvalue);
            q = startUnitTrans.addQuery("XX4","select to_number(?) TEMPTM from DUAL");
            q.addParameter("START_VALUE",nTMvalue);
            //Bug 58216 End
            startUnitTrans = mgr.perform(startUnitTrans);

            int nTY = (int)startUnitTrans.getNumberValue("XX3/DATA/TEMPTY"); 
            int nTM = (int)startUnitTrans.getNumberValue("XX4/DATA/TEMPTM");

            if ((lengthSV != 4) || (nTM <1) || (nTM>12) || (nTY<0))
            {
               String dateMask = mgr.getFormatMask("Date",true);
               mgr.showAlert(mgr.translate("PMACTIONROUNDINVLDSV: Invalid Start Value ")+tempSV+mgr.translate("PMACTIONROUNDFORMAT: . The Format should be ")+ dateMask+mgr.translate("PMACTIONROUNDSTARTUNIT:   for Start Unit 'Day' and YYMM or YYWW for Start Unit 'Month' or 'Week'."));           
            }
            else
            {
               startUnitTrans.clear();
               //Bug 58216 Start
               q = startUnitTrans.addQuery("XX","select to_date( ?,'RRMM') START_DATE from DUAL");
               q.addParameter("START_VALUE",startValue);
               //Bug 58216 End
               startUnitTrans = mgr.perform(startUnitTrans);

               String calStatDate  = startUnitTrans.getValue("XX/DATA/START_DATE");

               ASPBuffer temp1 = headset.getRow();
               temp1.setValue("START_DATE",calStatDate);
               temp1.setValue("START_VALUE",startValue);
               headset.setRow(temp1);

               startUnitTrans.clear();
               startUnitTrans = mgr.submit(startUnitTrans);
               headset.goTo(currrow);
            }
         }
         else if (clientValwee.equals(pmStartUnit))
         {
            String tempSV = mgr.readValue("START_VALUE");

            cmd = startUnitTrans.addCustomFunction("STARTDA","Pm_Calendar_API.Get_Date","START_DATE");
            cmd.addParameter("START_VALUE", startValue);
            cmd.addParameter("INDEX1", "1");

            startUnitTrans=mgr.perform(startUnitTrans);  
            String startDate = startUnitTrans.getValue("STARTDA/DATA/START_DATE");

            if ((mgr.isEmpty(startDate)))
            {
               String dateMask = mgr.getFormatMask("Date",true);
               mgr.showAlert(mgr.translate("PMACTIONROUNDINVLDSV: Invalid Start Value ")+tempSV+mgr.translate("PMACTIONROUNDFORMAT: . The Format should be ")+ dateMask+mgr.translate("PMACTIONROUNDSTARTUNIT:   for Start Unit 'Day' and YYMM or YYWW for Start Unit 'Month' or 'Week'."));
            }
            else
            {
               ASPBuffer temp2 = headset.getRow();
               temp2.setValue("START_DATE",startUnitTrans.getValue("STARTDA/DATA/START_DATE"));
               temp2.setValue("START_VALUE",startValue);
               headset.setRow(temp2);

               startUnitTrans.clear();
               startUnitTrans = mgr.submit(startUnitTrans);
               headset.goTo(currrow);
            }
         }
         else if (clientValDay.equals(pmStartUnit))
         {
            String dateMask = mgr.getFormatMask("Date",true);

            ASPBuffer buf = mgr.newASPBuffer();
            buf.addFieldItem("START_VALUE_TEMP",startValue);

            //Bug 58216 Start
            q = startUnitTrans.addQuery("XX","select ? START_DATE from DUAL");
            q.addParameter("START_VALUE",buf.getValue("START_VALUE_TEMP"));
            //Bug 58216 End
            startUnitTrans = mgr.perform(startUnitTrans);

            String calStatDate  = startUnitTrans.getValue("XX/DATA/START_DATE");

            if ((mgr.isEmpty(calStatDate)))
               mgr.showAlert(mgr.translate("PMACTIONROUNDINVLDSV: Invalid Start Value ")+startValue+mgr.translate("PMACTIONROUNDFORMAT: . The Format should be ")+ dateMask+mgr.translate("PMACTIONROUNDSTARTUNIT:   for Start Unit 'Day' and YYMM or YYWW for Start Unit 'Month' or 'Week'."));
            else
            {
               ASPBuffer temp3 = headset.getRow();
               temp3.setValue("START_DATE",calStatDate);
               temp3.setValue("START_VALUE",startValue);
               headset.setRow(temp3);

               startUnitTrans.clear();
               startUnitTrans = mgr.submit(startUnitTrans);
               headset.goTo(currrow);
            }
         }
         else
         {
            startUnitTrans.clear();
            startUnitTrans = mgr.submit(startUnitTrans);
         }   
      }
      else
      {
         ASPTransactionBuffer mytrans = mgr.newASPTransactionBuffer();
         mytrans.clear();
         mytrans.addQuery("YYN","select (Pm_Start_Unit_API.Get_Client_Value(3)) CLIENT_START3 from DUAL");
         mytrans = mgr.perform(mytrans);
         String clientValNone  = mytrans.getValue("YYN/DATA/CLIENT_START3");

         ASPTransactionBuffer startUnitTrans = mgr.newASPTransactionBuffer();

         ASPBuffer temp4 = headset.getRow();
         temp4.setValue("START_VALUE","");
         temp4.setValue("PM_START_UNIT",clientValNone);
         headset.setRow(temp4);

         startUnitTrans.clear();
         startUnitTrans = mgr.submit(startUnitTrans);
         headset.goTo(currrow);
      }

      if (mgr.isEmpty(pmIntervalunit))
      {
         ASPTransactionBuffer mytrans = mgr.newASPTransactionBuffer();
         mytrans.clear();
         mytrans.addQuery("INVNONE","select (Pm_Interval_Unit_API.Get_Client_Value(3)) CLIENT_INTERVAL3 from DUAL");
         mytrans = mgr.perform(mytrans);
         String IntervalValNone  = mytrans.getValue("INVNONE/DATA/CLIENT_INTERVAL3");

         ASPTransactionBuffer intervalUnitTrans = mgr.newASPTransactionBuffer();

         ASPBuffer temp5 = headset.getRow();
         temp5.setValue("INTERVAL","");
         temp5.setValue("PM_INTERVAL_UNIT",IntervalValNone);
         headset.setRow(temp5);

         intervalUnitTrans.clear();
         intervalUnitTrans = mgr.submit(intervalUnitTrans);
         headset.goTo(currrow);
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
   }

   public void saveReturn()
   {
      save();
      okFindITEM1();
      okFindITEM2();
      okFindITEM3();
      okFindITEM4();
      okFindITEM0();
      okFindITEM6();
   }

   public void saveNew()
   {
      save();
      trans.clear();
      newRow();
   }

   public void cancelNew()
   {
      boolean moreThanOne = false; 
      int laymod = headlay.getHistoryMode();
      currrow = headset.getCurrentRowNo();
      if (headset.countRows() > 1)
      {
         moreThanOne = true;

      }
      headset.clear();
      if (moreThanOne)
      {
         okFind();
      }
      headset.goTo(currrow);
      headlay.setLayoutMode(laymod);
   }

   public void saveReturnItem0()
   {
      ASPManager mgr = getASPManager();
      int currHead = headset.getCurrentRowNo();
      int currrowItem0 = itemset0.getCurrentRowNo();
      itemset0.changeRow();
      mgr.submit(trans);
      okFindITEM5();
      itemset0.goTo(currrowItem0);       
      headset.goTo(currHead);
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
      q.addParameter("PM_NO",headset.getRow().getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));
      //Bug 58216 End
      q.includeMeta("ALL");
      mgr.submit(trans);

      if (itemset0.countRows() == 0)
      {
         if ((!("ITEM1.OkFind".equals(mgr.readValue("__COMMAND")))) && !headSearch)
         {
            mgr.showAlert(mgr.translate("PCMWPMACTIONROUNDNODATAFOUND: No data found."));
            itemset0.clear();
         }
      }

      headset.goTo(currrow);              
      getSalesPartCost();
      headset.goTo(currrow);
   }

   public void okFindITEM1()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      headrowno = headset.getCurrentRowNo();

      itemset1.clear();
      q = trans.addQuery(itemblk1);
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addParameter("PM_NO",headset.getRow().getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));
      //Bug 58216 End
      q.includeMeta("ALL");

      mgr.submit(trans);

      if (mgr.commandBarActivated())
      {
         if (itemset1.countRows() == 0 && "ITEM1.OkFind".equals(mgr.readValue("__COMMAND")))
         {
            mgr.showAlert(mgr.translate("PCMWPMACTIONROUNDNODATA: No data found."));
            itemset1.clear();
         }
      }

      headset.goTo(headrowno);
   }

   public void okFindITEM2()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      headrowno = headset.getCurrentRowNo();

      itemset2.clear();
      q = trans.addQuery(itemblk2);
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addParameter("PM_NO",headset.getRow().getValue("PM_NO"));
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));
      //Bug 58216 End
      q.includeMeta("ALL");

      mgr.submit(trans);

      if (mgr.commandBarActivated())
      {
         if (itemset2.countRows() == 0 && "ITEM2.OkFind".equals(mgr.readValue("__COMMAND")))
         {
            mgr.showAlert(mgr.translate("PCMWPMACTIONROUNDNODATA: No data found."));
            itemset2.clear();
         }
      }

      headset.goTo(headrowno);
   }

   public void okFindITEM3()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      headrowno = headset.getCurrentRowNo();

      itemset3.clear();
      q = trans.addQuery(itemblk3);
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addWhereCondition("PM_REVISION = ?");
      q.addWhereCondition("CONTRACT = ?");
      q.addWhereCondition("MCH_CODE = ?");
      q.addParameter("PM_NO",headset.getRow().getValue("PM_NO"));
      q.addParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));
      q.addParameter("HEAD_CONTRACT",headset.getRow().getValue("CONTRACT"));
      q.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));
      //Bug 58216 End
      q.includeMeta("ALL");

      mgr.submit(trans);

      if (mgr.commandBarActivated())
      {
         if (itemset3.countRows() == 0 && "ITEM3.OkFind".equals(mgr.readValue("__COMMAND")))
         {
            mgr.showAlert(mgr.translate("PCMWPMACTIONROUNDNODATA: No data found."));
            itemset3.clear();
         }
      }

      headset.goTo(headrowno);
   }

   public void okFindITEM4()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      headrowno = headset.getCurrentRowNo();

      itemset4.clear();
      //Bug 58216 Start
      q = trans.addQuery("ITEM4","select H_R.WO_NO, A_R_A.PM_ORDER_NO, A_R_A.CONTRACT, H_R.COMPANY, A_R_A.ROUND_REPORT_IN_STATUS, H_R.REPORT_IN_BY, H_R.REPORT_IN_BY_ID, H_R.PLAN_S_DATE, H_R.REAL_S_DATE, H_R.PLAN_F_DATE, H_R.REAL_F_DATE, H_R.PM_NO,H_R.PM_REVISION from HISTORICAL_ROUND H_R, ACTIVE_ROUND_ACTION A_R_A where H_R.WO_NO=A_R_A.WO_NO and A_R_A.PM_NO= ? and A_R_A.PM_REVISION= ?");
      q.addParameter("PM_NO",headset.getRow().getValue("PM_NO"));
      q.addParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));
      //Bug 58216 End
      q.includeMeta("ALL");

      mgr.submit(trans);

      if (mgr.commandBarActivated())
      {
         if (itemset4.countRows() == 0 && "ITEM4.OkFind".equals(mgr.readValue("__COMMAND")))
         {
            mgr.showAlert(mgr.translate("PCMWPMACTIONROUNDNODATA: No data found."));
            itemset4.clear();
         }
      }

      headset.goTo(headrowno);
   }

   public void okFindITEM5()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      headrowno = headset.getCurrentRowNo();

      itemset5.clear();
      q = trans.addQuery(itemblk5);
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_NO",headset.getRow().getValue("PM_NO"));
      q.addParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));
      //Bug 58216 End
      q.includeMeta("ALL");

      mgr.submit(trans);

      headset.goTo(headrowno);

      if (itemset5.countRows()>0)
         setBudgetTotals();

      if (mgr.commandBarActivated())
      {
         if (itemset5.countRows() == 0 && "ITEM5.OkFind".equals(mgr.readValue("__COMMAND")))
         {
            mgr.showAlert(mgr.translate("PCMWPMACTIONROUNDNODATA: No data found."));
            itemset5.clear();
         }
      }

      headset.goTo(headrowno);
   }

   public void okFindITEM6()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      q = trans.addQuery(itemblk6);
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_NO",headset.getRow().getValue("PM_NO"));
      q.addParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));
      //Bug 58216 End  

      q.includeMeta("ALL");
      int headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);
      headset.goTo(headrowno);

      if (itemset6.countRows() == 0 && "ITEM6.OkFind".equals(mgr.readValue("__COMMAND")))
         itemset6.clear();
   }  

   public void countFindITEM0()
   {
      ASPManager mgr = getASPManager();

      currrow = headset.getCurrentRowNo();
      q = trans.addQuery(itemblk0);
      q.setSelectList("to_char(count(*)) N");
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_NO",headset.getRow().getValue("PM_NO"));
      q.addParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));
      //Bug 58216 End
      mgr.submit(trans);
      itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
      itemset0.clear();
      headset.goTo(currrow);   
   }

   public void countFindITEM1()
   {
      ASPManager mgr = getASPManager();

      currrow = headset.getCurrentRowNo();
      q = trans.addQuery(itemblk1);
      q.setSelectList("to_char(count(*)) N");
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_NO",headset.getRow().getValue("PM_NO"));
      q.addParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));
      //Bug 58216 End
      mgr.submit(trans);
      itemlay1.setCountValue(toInt(itemset1.getRow().getValue("N")));
      itemset1.clear();
      headset.goTo(currrow);   
   }

   public void countFindITEM2()
   {
      ASPManager mgr = getASPManager();

      currrow = headset.getCurrentRowNo();
      q = trans.addQuery(itemblk2);
      q.setSelectList("to_char(count(*)) N");
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_NO",headset.getRow().getValue("PM_NO"));
      q.addParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));
      //Bug 58216 End
      mgr.submit(trans);
      itemlay2.setCountValue(toInt(itemset2.getRow().getValue("N")));
      itemset2.clear();
      headset.goTo(currrow);   
   }

   public void countFindITEM3()
   {
      ASPManager mgr = getASPManager();

      currrow = headset.getCurrentRowNo();
      q = trans.addQuery(itemblk3);
      q.setSelectList("to_char(count(*)) N");
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_NO",headset.getRow().getValue("PM_NO"));
      q.addParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));
      //Bug 58216 End
      mgr.submit(trans);
      itemlay3.setCountValue(toInt(itemset3.getRow().getValue("N")));
      itemset3.clear();
      headset.goTo(currrow);   
   }

   public void countFindITEM5()
   {
      ASPManager mgr = getASPManager();

      currrow = headset.getCurrentRowNo();
      q = trans.addQuery(itemblk5);
      q.setSelectList("to_char(count(*)) N");
      //Bug 58216 Start
      q.addWhereCondition("PM_NO = ?");
      q.addWhereCondition("PM_REVISION = ?");
      q.addParameter("PM_NO",headset.getRow().getValue("PM_NO"));
      q.addParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));
      //Bug 58216 End
      mgr.submit(trans);
      itemlay5.setCountValue(toInt(itemset5.getRow().getValue("N")));
      itemset5.clear();
      headset.goTo(currrow);   
   }

   public void countFindITEM6()
   {
      ASPManager mgr = getASPManager();

      currrow = headset.getCurrentRowNo();
      q = trans.addQuery(itemblk6);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay6.setCountValue(toInt(itemset6.getRow().getValue("N")));
      itemset6.clear();
      headset.goTo(currrow);   
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void copyPmActions()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      // 040120  ARWILK  Begin  (Remove uneccessary global variables)
      bOpenNewWindow = true;
      //Bug 71664, start
      urlString = "CopyPmActions.page?MCH_CODE=" + mgr.URLEncode(headset.getValue("MCH_CODE")) +
                  "&CONTRACT=" + mgr.URLEncode(headset.getValue("CONTRACT")) +
                  "&PM_NO=" + mgr.URLEncode(headset.getValue("PM_NO"))+
                  "&PM_REVISION=" + mgr.URLEncode(headset.getValue("PM_REVISION"))+
                  "&ROUNDDEF_ID=" + mgr.URLEncode(headset.getValue("ROUNDDEF_ID")) ;
      //Bug 71664, end

      newWinHandle = "copyPmActions";
      // 040120  ARWILK  End  (Remove uneccessary global variables)
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
         bSetObsolete = true;

         //Bug 67948, start
         trans.clear();
         cmd = trans.addCustomCommand("REMOBSREP","Pm_Action_Replaced_API.Remove_Replacements");
         cmd.addParameter("PM_NO", sPmNo);
         cmd.addParameter("PM_REVISION", sPmRev);
         trans = mgr.perform(trans);
         //Bug 67948, end

         //Bug 67909, start
         sMsgTxt = mgr.translate("PCMWPMACTRNDOTHERSTOOBS: All other revisions for PM No &1 will be set to \"Obsolete\".", sPmNo); 
         //Bug 67909, end
      }

      trans.clear();
      mgr.submit(trans);

      headset.refreshAllRows();
      headset.goTo(currrow);
      //Bug 89752, Start
      if (itemset3.countRows()>0)
         itemset3.refreshAllRows();
      //Bug 89752, End
   }

   public void obsoletePm()
   {
      ASPManager mgr = getASPManager();
      //Bug 67948, start
      String sPmNo = headset.getValue("PM_NO"); 
      String sPmRev = headset.getValue("PM_REVISION"); 
      //Bug 67948, end

      perform("OBSOLETE__");

      //Bug 67948, start
      trans.clear();
      cmd = trans.addCustomCommand("REMCURROBSREP","Pm_Action_Replaced_API.Remove_Curr_Replacements");
      cmd.addParameter("PM_NO", sPmNo);
      cmd.addParameter("PM_REVISION", sPmRev);

      trans = mgr.perform(trans);
      //Bug 67948, end
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

   public void replacements()
   {
      ASPManager mgr = getASPManager();

      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      // 040120  ARWILK  Begin  (Remove uneccessary global variables)
      if (headlay.isMultirowLayout())
         headset.store();
      else
      {
         headset.unselectRows();
         headset.selectRow();
      }

      bOpenNewWindow = true;
      urlString = createTransferUrl("Replacements.page", headset.getSelectedRows("PM_NO,PM_REVISION"));
      newWinHandle = "replacements"; 
      // 040120  ARWILK  End  (Remove uneccessary global variables)
   }

   // 031215  ARWILK  Begin  (Links with RMB's)
   public void sparePartsinObject()
   {
      ASPManager mgr = getASPManager();

      bOpenNewWindow = true;

      urlString = "MaintenanceObject.page?MCH_CODE=" + mgr.URLEncode(headset.getValue("MCH_CODE")) +
                  "&CONTRACT=" + mgr.URLEncode(headset.getValue("CONTRACT")) +
                  "&PM_NO=" + mgr.URLEncode(headset.getValue("PM_NO")) +
                  "&PM_REVISION=" + mgr.URLEncode(headset.getValue("PM_REVISION")) +
                  "&QRYSTR=" + mgr.URLEncode(qrystr) +
                  "&FRMNAME=" + mgr.URLEncode("PmActionRound");

      newWinHandle = "SpareInObj"; 
   }
   // 031215  ARWILK  End  (Links with RMB's)

   public void spareParts()
   {
      ASPManager mgr = getASPManager();

      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      // 040120  ARWILK  Begin  (Remove uneccessary global variables)
      int count = 0;
      ASPBuffer transferBuffer;
      ASPBuffer rowBuff;

      bOpenNewWindow = true;

      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

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
            rowBuff.addItem("SPARE_ID", itemset0.getValue("PART_NO"));
         else
            rowBuff.addItem(null, itemset0.getValue("PART_NO"));

         transferBuffer.addBuffer("DATA", rowBuff);

         if (itemlay0.isMultirowLayout())
            itemset0.next();
      }

      if (itemlay0.isMultirowLayout())
         itemset0.setFilterOff();

      urlString = createTransferUrl("../equipw/EquipmentSpareStructure2.page", transferBuffer);
      newWinHandle = "sparePartsInDetached"; 
      // 040120  ARWILK  End  (Remove uneccessary global variables)
   }

   public void currQuntity()
   {
      // 040120  ARWILK  Begin  (Remove uneccessary global variables)
      ASPManager mgr = getASPManager();

      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

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
            rowBuff.addItem("PART_NO", itemset0.getValue("PART_NO"));
            rowBuff.addItem("CONTRACT", itemset0.getValue("SPARE_CONTRACT"));
         }
         else
         {
            rowBuff.addItem(null, itemset0.getValue("PART_NO"));
            rowBuff.addItem(null, itemset0.getValue("SPARE_CONTRACT"));
         }

         transferBuffer.addBuffer("DATA", rowBuff);

         if (itemlay0.isMultirowLayout())
            itemset0.next();
      }

      if (itemlay0.isMultirowLayout())
         itemset0.setFilterOff();

      urlString = createTransferUrl("../invenw/InventoryPartInventoryPartCurrentOnHand.page", transferBuffer);
      newWinHandle = "currQuntity"; 
      // 040120  ARWILK  End  (Remove uneccessary global variables)
   }

   public void availDetail()
   {
      // 040120  ARWILK  Begin  (Remove uneccessary global variables)
      ASPManager mgr = getASPManager();

      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

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
            rowBuff.addItem("PART_NO", itemset0.getValue("PART_NO"));
            rowBuff.addItem("CONTRACT", itemset0.getValue("SPARE_CONTRACT"));
            rowBuff.addItem("PROJECT_ID",  "*");
            rowBuff.addItem("CONFIGURATION_ID", "*");
         }
         else
         {
            rowBuff.addItem(null, itemset0.getValue("PART_NO"));
            rowBuff.addItem(null, itemset0.getValue("SPARE_CONTRACT"));
            rowBuff.addItem(null,  "*");
            rowBuff.addItem(null, "*");
         }

         transferBuffer.addBuffer("DATA", rowBuff);

         if (itemlay0.isMultirowLayout())
            itemset0.next();
      }

      if (itemlay0.isMultirowLayout())
         itemset0.setFilterOff();

      urlString = createTransferUrl("../invenw/InventoryPartAvailabilityPlanningQry.page", transferBuffer);
      newWinHandle = "availDetail"; 
      // 040120  ARWILK  End  (Remove uneccessary global variables)
   }

   public void inventoryPart()
   {
      // 040120  ARWILK  Begin  (Remove uneccessary global variables)
      ASPManager mgr = getASPManager();

      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

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
            rowBuff.addItem("PART_NO", itemset0.getValue("PART_NO"));
            rowBuff.addItem("CONTRACT", itemset0.getValue("SPARE_CONTRACT"));
         }
         else
         {
            rowBuff.addItem(null, itemset0.getValue("PART_NO"));
            rowBuff.addItem(null, itemset0.getValue("SPARE_CONTRACT"));
         }

         transferBuffer.addBuffer("DATA", rowBuff);

         if (itemlay0.isMultirowLayout())
            itemset0.next();
      }

      if (itemlay0.isMultirowLayout())
         itemset0.setFilterOff();

      urlString = createTransferUrl("../invenw/InventoryPart.page", transferBuffer);
      newWinHandle = "inventoryPart"; 
      // 040120  ARWILK  End  (Remove uneccessary global variables)
   }

   public void supplierPerPart()
   {
      ASPManager mgr = getASPManager();

      // 040120  ARWILK  Begin  (Remove uneccessary global variables)
      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

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
            rowBuff.addItem("PART_NO", itemset0.getValue("PART_NO"));
            rowBuff.addItem("CONTRACT", itemset0.getValue("SPARE_CONTRACT"));
         }
         else
         {
            rowBuff.addItem(null, itemset0.getValue("PART_NO"));
            rowBuff.addItem(null, itemset0.getValue("SPARE_CONTRACT"));
         }

         transferBuffer.addBuffer("DATA", rowBuff);

         if (itemlay0.isMultirowLayout())
            itemset0.next();
      }

      if (itemlay0.isMultirowLayout())
         itemset0.setFilterOff();

      urlString = createTransferUrl("../purchw/PurchasePartSupplier.page", transferBuffer);
      newWinHandle = "supplierPerPart"; 
      // 040120  ARWILK  End  (Remove uneccessary global variables)
   }

   public void parameters()
   {
      ASPManager mgr = getASPManager();

      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      // 040120  ARWILK  Begin  (Remove uneccessary global variables)
      bOpenNewWindow = true;
      urlString = "../equipw/RMBEquipmentObject.page?CONTRACT=" + mgr.URLEncode(headset.getValue("CONTRACT")) +
                  "&MCH_CODE=" + mgr.URLEncode(headset.getValue("MCH_CODE"));

      newWinHandle = "parameters"; 
      // 040120  ARWILK  End  (Remove uneccessary global variables)
   }

   public void measurements()
   {
      ASPManager mgr = getASPManager();

      calling_url=mgr.getURL();
      ctx.setGlobal("CALLING_URL",calling_url);

      // 040120  ARWILK  Begin  (Remove uneccessary global variables)
      bOpenNewWindow = true;
      urlString = "../equipw/RMBMaintenanceObject.page?CONTRACT=" + mgr.URLEncode(headset.getValue("CONTRACT")) +
                  "&MCH_CODE=" + mgr.URLEncode(headset.getValue("MCH_CODE"));

      newWinHandle = "measurements"; 
      // 040120  ARWILK  End  (Remove uneccessary global variables)
   }

   public void attributes()
   {
      ASPManager mgr = getASPManager();

      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      // 040120  ARWILK  Begin  (Remove uneccessary global variables)
      if (itemlay2.isMultirowLayout())
         itemset2.store();
      else
      {
         itemset2.unselectRows();
         itemset2.selectRow();
      }

      bOpenNewWindow = true;
      urlString = createTransferUrl("PermitTypeRMB.page", itemset2.getSelectedRows("PERMIT_TYPE_ID"));
      newWinHandle = "attributes"; 
      // 040120  ARWILK  End  (Remove uneccessary global variables)
   }

   public boolean isGeneratable()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomFunction("GENE2", "Pm_Action_API.Is_Generateable__", "GENERATE2" );
      cmd.addParameter("ITEM1_PM_NO", itemset1.getRow().getFieldValue("ITEM1_PM_NO"));
      cmd.addParameter("ITEM1_PM_REVISION", itemset1.getRow().getFieldValue("ITEM1_PM_REVISION"));
      trans = mgr.perform(trans);

      String strGenerate= trans.getValue("GENE2/DATA/GENERATE2");

      if ("TRUE".equals(strGenerate))
         return(true);
      else
         return(false);
   }

   public boolean isConnection()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomFunction("CONT2", "Pm_Action_Connection_API.Is_Connected", "CONECTED2" );
      cmd.addParameter("ITEM1_PM_NO", itemset1.getRow().getFieldValue("ITEM1_PM_NO"));
      cmd.addParameter("ITEM1_PM_REVISION", itemset1.getRow().getFieldValue("ITEM1_PM_REVISION"));
      trans = mgr.perform(trans);

      String strConected= trans.getValue("CONT2/DATA/CONECTED2");

      if ("TRUE".equals(strConected))
         return(true);
      else
         return(false);
   }

   public void reportInRouteWO()
   {
      ASPManager mgr = getASPManager();

      calling_url=mgr.getURL();
      ctx.setGlobal("CALLING_URL",calling_url);

      // 040120  ARWILK  Begin  (Remove uneccessary global variables)
      if (itemlay1.isMultirowLayout())
         itemset1.store();
      else
      {
         itemset1.unselectRows();
         itemset1.selectRow();
      }

      bOpenNewWindow = true;
      urlString = createTransferUrl("ActiveRound.page", itemset1.getSelectedRows("WO_NO"));
      newWinHandle = "reportInRouteWO"; 
      // 040120  ARWILK  End  (Remove uneccessary global variables)
   }


   public void viewHistoricalWO()
   {
      ASPManager mgr = getASPManager();

      calling_url=mgr.getURL();
      ctx.setGlobal("CALLING_URL",calling_url);

      // 040120  ARWILK  Begin  (Remove uneccessary global variables)
      if (itemlay1.isMultirowLayout())
         itemset1.store();
      else
      {
         itemset1.unselectRows();
         itemset1.selectRow();
      }

      bOpenNewWindow = true;
      urlString = createTransferUrl("HistoricalRound.page", itemset1.getSelectedRows("WO_NO"));
      newWinHandle = "viewHistoricalWO"; 
      // 040120  ARWILK  End  (Remove uneccessary global variables)
   }

   public void viewGenerationDetails()
   {
      ASPManager mgr = getASPManager();

      calling_url=mgr.getURL();
      ctx.setGlobal("CALLING_URL",calling_url);

      // 040120  ARWILK  Begin  (Enable Multirow RMB actions)
      if (itemlay1.isMultirowLayout())
         itemset1.goTo(itemset1.getRowSelected());
      else
         itemset1.selectRow();

      cmd = trans.addCustomFunction("GENTYPE", "PM_GENERATION_API.Get_Generation_Type", "ITEM1_GEN_TYPE" );
      cmd.addParameter("GEN_ID", itemset1.getValue("GEN_ID"));

      trans = mgr.perform(trans);

      String strGenType= trans.getValue("GENTYPE/DATA/ITEM1_GEN_TYPE");

      buffer = mgr.newASPBuffer();
      row = buffer.addBuffer("0");
      row.addItem("WO_NO", itemset1.getValue("WO_NO"));
      row.addItem("PM_NO", headset.getValue("PM_NO"));
      row.addItem("PM_REVISION", headset.getValue("PM_REVISION"));
      row.addItem("MCHCODE", itemset1.getValue("ITEM1_MCH_CODE"));
      row.addItem("DESCRIPTION", itemset1.getValue("ITEM1_MCH_DESC"));
      row.addItem("SITE", itemset1.getValue("ITEM1_SITE"));
      row.addItem("PLANNED_DATE", itemset1.getValue("PLANNED_DATE"));
      row.addItem("PLANNED_WEEK", itemset1.getValue("PLANNED_WEEK"));
      row.addItem("GEN_ID", itemset1.getValue("GEN_ID"));  
      row.addItem("GENTYPE", strGenType);
      row.addItem("REPLACED_BY_PM_NO", itemset1.getValue("REPLACED_BY_PM_NO"));
      row.addItem("REPLACED_BY_PM_REVISION", itemset1.getValue("REPLACED_BY_PM_REVISION"));
      row.addItem("REPLACED_BY_SEQ_NO", itemset1.getValue("REPLACED_BY_SEQ_NO"));
      row.addItem("EVENT", itemset1.getValue("EVENT"));
      row.addItem("EVENT_SEQ", itemset1.getValue("EVENT_SEQ"));
      row.addItem("TESTPOINT", itemset1.getValue("TESTPOINT"));
      row.addItem("PARAMETER", itemset1.getValue("PARAMETER"));
      row.addItem("PM_NO", itemset1.getValue("VALUE"));

      bOpenNewWindow = true;
      urlString = createTransferUrl("ViewGenerationDlg.page", buffer);
      newWinHandle = "ViewGenerationDlg"; 
      // 040120  ARWILK  End  (Enable Multirow RMB actions)
   }

//-----------------------------------------------------------------------------
//-------------------------  CHECK BOX FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void setCheckBoxValues()
   {
      ASPManager mgr = getASPManager();

      eval(headblk.generateAssignments("PM_NO,PM_REVISION",headset.getRow()));

      cmd = trans.addCustomFunction("ADT","Pm_Action_Calendar_Plan_API.Is_Adjusted","ADJUSTMENTS");
      cmd.addParameter("PM_NO", mgr.getASPField("PM_NO").getValue() );
      cmd.addParameter("PM_REVISION", mgr.getASPField("PM_REVISION").getValue() );

      cmd = trans.addCustomFunction("CRI","Pm_Action_Criteria_API.Has_Criteria","CRITERIA");
      cmd.addParameter("PM_NO", mgr.getASPField("PM_NO").getValue() );
      cmd.addParameter("PM_REVISION", mgr.getASPField("PM_REVISION").getValue() );

      String pno = (String)mgr.getASPField("PM_NO").getValue()+ (char)31;
      pno = (String)mgr.getASPField("PM_REVISION").getValue()+ (char)31;

      cmd = trans.addCustomFunction("DOCREF","DOC_REFERENCE_OBJECT_API.EXIST_OBJ_REFERENCE","CBHASDOCUMENTS");
      cmd.addParameter("LUNAME", "PmAction");
      cmd.addParameter("KEY_REF", pno );

      trans = mgr.perform(trans);

      trans.clear(); 

      String strAdjust = trans.getValue("ADT/DATA/ADJUSTMENTS");
      String strCriteria = trans.getValue("CRI/DATA/CRITERIA");
      String strHasDoc = trans.getValue("DOCREF/DATA/CBHASDOCUMENTS");

      if ("TRUE".equals(strAdjust))
      {
         r = headset.getRow();
         r.setValue("ADJUSTMENTS","TRUE"); 
         headset.setRow(r);
      }

      if ("TRUE".equals(strCriteria))
      {
         r = headset.getRow();
         r.setValue("CRITERIA","TRUE" ); 
         headset.setRow(r);
      }

      if ("TRUE".equals(strHasDoc))
      {
         r = headset.getRow();
         r.setValue("CBHASDOCUMENTS","TRUE" ); 
         headset.setRow(r);
      }

   }

//-----------------------------------------------------------------------------
//----------------------  SET BUDGET TOTALS FUNCTION  -------------------------
//-----------------------------------------------------------------------------

   public void setBudgetTotals()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      row = itemset5.getRow();

      cmd = trans.addCustomCommand("PMATCT","PM_Action_Spare_Part_API.Get_Plan_Inv_Part_Cost");
      cmd.addParameter("PLAN_MAT_COST");
      cmd.addParameter("ITEM5_PM_NO", headset.getRow().getValue("PM_NO"));
      cmd.addParameter("ITEM5_PM_REVISION", headset.getRow().getValue("PM_REVISION"));

      cmd = trans.addCustomCommand("PEXTCT","PM_Action_Spare_Part_API.Get_Plan_Ext_Cost");
      cmd.addParameter("PLAN_EXT_COST");
      cmd.addParameter("ITEM5_PM_NO", headset.getRow().getValue("PM_NO"));   
      cmd.addParameter("ITEM5_PM_REVISION", headset.getRow().getValue("PM_REVISION"));   

      cmd = trans.addCustomCommand("PPERREV","PM_Action_ROLE_API.Get_Plan_Pers_Revenue__");
      cmd.addParameter("PLAN_PERS_REV");
      cmd.addParameter("ITEM5_PM_NO", headset.getRow().getValue("PM_NO"));   
      cmd.addParameter("ITEM5_PM_REVISION", headset.getRow().getValue("PM_REVISION"));   

      cmd = trans.addCustomCommand("PMATREV","PM_Action_Spare_Part_API.Get_Plan_Mat_Revenue");
      cmd.addParameter("PLAN_MAT_REV");   
      cmd.addParameter("ITEM5_PM_NO", headset.getRow().getValue("PM_NO"));   
      cmd.addParameter("ITEM5_PM_REVISION", headset.getRow().getValue("PM_REVISION"));   

      cmd = trans.addCustomCommand("PEXTREV","PM_Action_Spare_Part_API.Get_Plan_Ext_Revenue");
      cmd.addParameter("PLAN_EXT_REV");   
      cmd.addParameter("ITEM5_PM_NO", headset.getRow().getValue("PM_NO"));
      cmd.addParameter("ITEM5_PM_REVISION", headset.getRow().getValue("PM_REVISION"));

      trans = mgr.perform(trans);

      double planPersCost = row.getNumberValue("PLAN_PERS_COST");
      planMatCost = trans.getValue("PMATCT/DATA/PLAN_MAT_COST");
      planExtCost = trans.getValue("PEXTCT/DATA/PLAN_EXT_COST");
      double totPersCost = planPersCost + toDouble(planMatCost) + toDouble(planExtCost);

      planPersRev = trans.getValue("PPERREV/DATA/PLAN_PERS_REV");
      planMatRev = trans.getValue("PMATREV/DATA/PLAN_MAT_REV");
      planExtRev = trans.getValue("PEXTREV/DATA/PLAN_EXT_REV");
      double totPersRev = toDouble(planPersRev) + toDouble(planMatRev) + toDouble(planExtRev);

      row.setNumberValue("PLAN_PERS_COST", planPersCost);
      row.setNumberValue("PLAN_MAT_COST", toDouble(planMatCost));
      row.setNumberValue("PLAN_EXT_COST", toDouble(planExtCost));
      row.setNumberValue("PLAN_PERS_REV", toDouble(planPersRev));
      row.setNumberValue("PLAN_MAT_REV", toDouble(planMatRev));
      row.setNumberValue("PLAN_EXT_REV", toDouble(planExtRev));
      row.setNumberValue("TOT_PLAN_COST", totPersCost);
      row.setNumberValue("TOT_PLAN_REV", totPersRev);
      itemset5.setRow(row);
   }

   private boolean checkforConnections(int currrowItem)
   {
      ASPManager mgr = getASPManager();

      itemset6.goTo(currrowItem);

      cmd = trans.addCustomCommand("CHECKCONN","Pm_Action_Job_API.Check_Job_Connection");
      cmd.addParameter("ROLES_EXIST","0");
      cmd.addParameter("MATERIAL_EXIST","0");
      cmd.addParameter("TOOL_FACILITY_EXIST","0");
      cmd.addParameter("DOC_EXIST","0");
      cmd.addParameter("STD_JOB_ID",itemset6.getRow().getValue("STD_JOB_ID"));
      cmd.addParameter("STD_JOB_CONTRACT",itemset6.getRow().getValue("STD_JOB_CONTRACT"));
      cmd.addParameter("STD_JOB_REVISION",itemset6.getRow().getValue("STD_JOB_REVISION"));
      cmd.addParameter("PM_NO",headset.getRow().getValue("PM_NO"));
      cmd.addParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));
      cmd.addParameter("JOB_ID",itemset6.getRow().getValue("JOB_ID"));

      trans = mgr.perform(trans);

      String rolesexist = trans.getValue("CHECKCONN/DATA/ROLES_EXIST");
      String matexist = trans.getValue("CHECKCONN/DATA/MATERIAL_EXIST");
      String toolsfacexist = trans.getValue("CHECKCONN/DATA/TOOL_FACILITY_EXIST");
      String docexist = trans.getValue("CHECKCONN/DATA/DOC_EXIST");

      if ("1".equals(matexist) || "1".equals(docexist))
      {
         return true;
      }
      else
         return false;
   }

   public void preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");

      f = headblk.addField("OBJID");
      f.setHidden();

      f = headblk.addField("OBJVERSION");
      f.setHidden();

      f = headblk.addField("PM_NO");
      f.setSize(8);
      f.setHilite();
      f.setLabel("PCMWPMACTIONROUNDPMNO: PM No");
      f.setReadOnly();

      f = headblk.addField("PM_REVISION");
      f.setSize(8);
      f.setHilite();
      f.setLabel("PCMWPMACTIONROUNDPMREV: PM Revision");
      f.setInsertable();
      f.setReadOnly();

      f = headblk.addField("HEAD_CONTRACT");
      f.setSize(8);
      f.setHilite();
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONROUNDCONTRACTLOV: List of Site"));
      f.setLabel("PCMWPMACTIONROUNDCONTRACT: Site");
      f.setUpperCase();
      f.setDbName("CONTRACT");
      f.setMaxLength(5);
      f.setCustomValidation("HEAD_CONTRACT","COMPANY,MCH_CODE_CONTRACT");

      f = headblk.addField("MCH_CODE");
      f.setSize(25);
      //f.setLOV("MaintenanceObjectLov1.page","HEAD_CONTRACT CONTRACT",600,450);
      f.setMandatory();
      f.setHilite();
      f.setLabel("PCMWPMACTIONROUNDMCHCODE: Object ID");
      f.setUpperCase();
      f.setMaxLength(100);
      f.setCustomValidation("HEAD_CONTRACT,MCH_CODE","MCH_CODE,MCH_NAME1,HEAD_CONTRACT,MCH_CODE_CONTRACT");

      f = headblk.addField("MCH_CODE_CONTRACT");
      f.setHidden();

      f = headblk.addField("MCH_NAME1");
      f.setSize(25);
      f.setHilite();
      f.setLabel("PCMWPMACTIONROUNDMCHNAME: Object Description");
      f.setFunction("Equipment_Object_API.Get_Mch_Name(:MCH_CODE_CONTRACT,:MCH_CODE)");
      f.setReadOnly();

      f = headblk.addField("ACTION_CODE_ID");
      f.setSize(8);
      f.setDynamicLOV("MAINTENANCE_ACTION",600,450);
      f.setMandatory();
      f.setLabel("PCMWPMACTIONROUNDACTIONCODID: Action");
      f.setHilite();
      f.setUpperCase();
      f.setMaxLength(10);

      f = headblk.addField("ACTION_DESCR1");
      f.setSize(25);
      f.setFunction("Maintenance_Action_API.Get_Description(:ACTION_CODE_ID)");
      mgr.getASPField("ACTION_CODE_ID").setValidation("ACTION_DESCR1");
      f.setHilite();
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("STATE");
      f.setSize(20);
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONRNDSTATE: State");

      f = headblk.addField("OBJSTATE");
      f.setHidden();

      f = headblk.addField("OLD_REVISION");
      f.setFunction("Pm_Action_API.Get_Old_Revision(:PM_NO,:PM_REVISION)");
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONRNDOLDREV: Old Revision");

      f = headblk.addField("COMPANY");
      f.setSize(6);
      f.setHidden();
      f.setUpperCase();

      //================= WO Specific Info =======================

      f = headblk.addField("ORG_CONTRACT");
      f.setSize(10);
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
      f.setMandatory();
      f.setLabel("PCMWPMACTIONROUNDWOCONTR: WO Site");
      f.setUpperCase();
      f.setMaxLength(5);
      f.setInsertable();
      f.setReadOnly();
      f.setCustomValidation("ORG_CONTRACT,ORG_CODE","CALENDAR_ID,CALENDAR_DESC,ORG_CODE_COMP");


      f = headblk.addField("ORG_CODE");
      f.setSize(15);
      f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ORG_CONTRACT CONTRACT",600,450);
      f.setMandatory();
      f.setHilite();
      f.setLabel("PCMWPMACTIONROUNDMORGCODE: Maintenance Organization");
      f.setUpperCase();
      f.setMaxLength(8);
      f.setCustomValidation("ORG_CONTRACT,ORG_CODE","CALENDAR_ID,CALENDAR_DESC");

      f = headblk.addField("ORG_CODE_COMP");
      f.setFunction("Site_API.Get_Company(:ORG_CONTRACT)");
      f.setHidden();

      f = headblk.addField("OP_STATUS_ID");
      f.setSize(9);
      f.setHilite();
      f.setDynamicLOV("OPERATIONAL_STATUS",600,450);
      f.setLabel("PCMWPMACTIONROUNDOPSTATUSID: Operational Status");
      f.setUpperCase();
      f.setMaxLength(3);

      f = headblk.addField("PRIORITY_ID");
      f.setSize(9);
      f.setDynamicLOV("MAINTENANCE_PRIORITY",600,450);
      f.setLabel("PCMWPMACTIONROUNDPRIORITYID: Priority");
      f.setUpperCase();
      f.setMaxLength(1);

      f = headblk.addField("MAINT_EMP_SIGN");
      f.setSize(20);
      f.setLOV("../mscomw/MaintEmployeeLov.page","ORG_CODE_COMP COMPANY",600,450);
      f.setLabel("PCMWPMACTIONROUNDEXECUTEDBY: Executed By");
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONROUNDSIGN: List of Signature"));
      f.setUpperCase();
      f.setMaxLength(20);
      f.setDefaultNotVisible();
      f.setCustomValidation("MAINT_EMP_SIGN,COMPANY","MAINT_EMPLOYEE,MAINT_EMP_SIGN");

      f = headblk.addField("MAINT_EMPLOYEE");
      f.setLabel("PCMWPMACTIONROUNDSIG: Executed By Id");
      f.setHidden();

      f = headblk.addField("WORK_TYPE_ID");
      f.setSize(9);
      f.setDynamicLOV("WORK_TYPE",600,450);
      f.setLabel("PCMWPMACTIONROUNDWORKTYPEID: Work Type");
      f.setUpperCase();
      f.setMaxLength(20);
      f.setDefaultNotVisible();


      //=============== PM Info ========================

      f = headblk.addField("LAST_CHANGED","Date");
      f.setSize(15);
      f.setLabel("PCMWPMACTIONROUNDLASTCHANGED: Last Modified");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("LATEST_PM","Date");
      f.setSize(15);
      f.setLabel("PCMWPMACTIONROUNDLATESTPM: Last Performed");
      f.setDefaultNotVisible();

      f = headblk.addField("PM_PERFORMED_DATE_BASED");
      f.setSize(22);
      f.setLabel("PCMWPMACTIONROUNDPMPERFORMEDDATASED: Performed Date Based");
      f.setSelectBox();
      f.enumerateValues("PM_PERFORMED_DATE_BASED_API");
      f.unsetSearchOnDbColumn();
      f.setDefaultNotVisible();

      f = headblk.addField("OVERDUE_PERCENT","Number","#");
      f.setSize(9);
      f.setLabel("PCMWPMACTIONROUNDOVRDUEPCNT: Overdue Percent (%)");
      f.setReadOnly();
      f.setMaxLength(4);
      f.setFunction("PM_Action_API.Overdue_Percent(:PM_NO,:PM_REVISION)");

      //================ Routes ==============================

      f = headblk.addField("ROUNDDEF_ID");
      f.setSize(9);
      f.setHilite();
      f.setDynamicLOV("PM_ROUND_DEFINITION",600,450);
      f.setLabel("PCMWPMACTIONROUNDROUNDDEFID: Route ID");
      f.setUpperCase();
      f.setMaxLength(8);
      f.setMandatory();   
      f.setDefaultNotVisible();

      f = headblk.addField("PM_ORDER_NO","Number");
      f.setSize(9);
      f.setHilite();
      f.setLabel("PCMWPMACTIONROUNDPMORDERNO: Order No");
      f.setDefaultNotVisible();

      f = headblk.addField("ROLE_CODE");
      f.setSize(9);
      f.setDynamicLOV("ROLE_TO_SITE_LOV","HEAD_CONTRACT CONTRACT",600,450);
      f.setLabel("PCMWPMACTIONROUNDROLECODE: Role");
      f.setUpperCase();
      f.setMaxLength(10);
      f.setDefaultNotVisible();

      f = headblk.addField("PLAN_MEN","Number");
      f.setSize(9);
      f.setLabel("PCMWPMACTIONROUNDPLANMEN: Plan Men");
      f.setDefaultNotVisible();

      f = headblk.addField("PLAN_HRS","Number");
      f.setSize(9);
      f.setLabel("PCMWPMACTIONROUNDPLANHRS: Plan Hours");
      f.setDefaultNotVisible();

      f = headblk.addField("PM_GENERATEABLE");
      f.setSize(12);
      f.setLabel("PCMWPMACTIONROUNDPMGENERATEABLE: Generateable");
      f.setSelectBox();
      f.enumerateValues("PM_GENERATEABLE_API");
      f.unsetSearchOnDbColumn();
      f.setDefaultNotVisible();

      //================== Calendar =====================

      f = headblk.addField("START_VALUE");
      f.setSize(9);
      f.setLabel("PCMWPMACTIONROUNDSTARVALUE: Start Value");
      f.setUpperCase();
      f.setMaxLength(11);
      f.setDefaultNotVisible();

      f = headblk.addField("PM_START_UNIT");
      f.setSize(12);
      f.setLabel("PCMWPMACTIONROUNDPMSTARTUNIT: Start Unit");
      f.setSelectBox();
      f.enumerateValues("PM_START_UNIT_API");
      f.unsetSearchOnDbColumn();   
      f.setDefaultNotVisible();

      f = headblk.addField("INTERVAL");
      f.setSize(9);
      f.setLabel("PCMWPMACTIONROUNDINTERVAL: Interval");
      f.setMaxLength(3);
      f.setDefaultNotVisible();

      f = headblk.addField("PM_INTERVAL_UNIT");
      f.setSize(12);
      f.setLabel("PCMWPMACTIONROUNDPMINTERVALUNIT: Interval Unit");
      f.setSelectBox();
      f.enumerateValues("PM_INTERVAL_UNIT_API");
      f.unsetSearchOnDbColumn();
      f.setDefaultNotVisible();

      //=================== Event ==========================

      f = headblk.addField("CALL_CODE");
      f.setSize(9);
      f.setDynamicLOV("MAINTENANCE_EVENT",600,450);
      f.setLabel("PCMWPMACTIONROUNDCALLCODE: Event");
      f.setUpperCase();
      f.setMaxLength(10);
      f.setDefaultNotVisible();

      f = headblk.addField("START_CALL","Number");
      f.setSize(9);
      f.setLabel("PCMWPMACTIONROUNDSTARTCALLN: Start");
      f.setDefaultNotVisible();

      f = headblk.addField("CALL_INTERVAL","Number");
      f.setSize(9);
      f.setLabel("PCMWPMACTIONROUNDCALLINTERVALN: Interval");
      f.setDefaultNotVisible();

      //==================== PM Has ===========================

      f = headblk.addField("OBSOLETE_JOBS");
      f.setSize(10);
      f.setLabel("PCMWPMACTIONOBSOLETEJOBS: Obsolete Jobs");
      f.setFunction("Pm_Action_Job_API.Check_Obsolete_Jobs(:PM_NO,:PM_REVISION)"); 
      f.setCheckBox("FALSE,TRUE");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("CRITERIA");
      f.setSize(9);
      f.setLabel("PCMWPMACTIONROUNDCRITERIA: Criteria");
      f.setFunction("Pm_Action_Criteria_API.Has_Criteria(:PM_NO,:PM_REVISION)"); 
      f.setCheckBox("FALSE,TRUE");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("ADJUSTMENTS");
      f.setSize(9);
      f.setLabel("PCMWPMACTIONROUNDADJUSTMENTS: Adjustments");
      f.setFunction("Pm_Action_Calendar_Plan_API.Is_Adjusted(:PM_NO,:PM_REVISION)");
      f.setCheckBox("FALSE,TRUE");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("CBHASDOCUMENTS");
      f.setSize(10);
      f.setLabel("PCMWPMACTIONROUNDCBHASDOCUMENT: Documents");
      if (mgr.isModuleInstalled("DOCMAN"))
         f.setFunction("substr(Doc_Reference_Object_API.Exist_Obj_Reference('PmAction',CONCAT('PM_NO=',CONCAT(PM_NO,CONCAT('^PM_REVISION=',CONCAT(PM_REVISION,'^'))))),1,5)");
      else
         f.setFunction("''");
      f.setCheckBox("FALSE,TRUE");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("REPLACEMENTS");
      f.setSize(11);
      f.setLabel("PCMWPMACTIONROUNDREPLACEMENT: Replacements");
      f.setFunction("Pm_Action_Replaced_API.Replacements(:PM_NO,:PM_REVISION)");
      f.setCheckBox("FALSE,TRUE");
      f.setReadOnly(  );
      f.setDefaultNotVisible();

      f = headblk.addField("KEY_REF");
      f.setHidden();
      f.setFunction("CONCAT('PM_NO=',CONCAT(PM_NO,CONCAT('^PM_REVISION=',CONCAT(PM_REVISION,'^'))))");

      f = headblk.addField("CBHASDOCUMENT");
      f.setSize(10);
      f.setLabel("PCMWPMACTIONROUNDCBHASDOCUMENT1: Documents");
      if (mgr.isModuleInstalled("DOCMAN"))
         f.setFunction("DOC_REFERENCE_OBJECT_API.EXIST_OBJ_REFERENCE('PmAction',CONCAT('PM_NO_KEY_VALUE=',CONCAT(PM_NO_KEY_VALUE,CONCAT('^PM_REVISION=',CONCAT(PM_REVISION,'^')))))");
      else
         f.setFunction("''");
      f.setHidden();

      //======================== Valid ====================

      f = headblk.addField("VALID_FROM","Date");
      f.setDefaultNotVisible();
      f.setLabel("PCMWPMACTIONRNDVALIDFROM: Valid From");

      f = headblk.addField("VALID_TO","Date");
      f.setDefaultNotVisible();
      f.setLabel("PCMWPMACTIONRNDVALIDTO: Valid To");

      f = headblk.addField("CALENDAR_ID");
      f.setUpperCase();
      f.setLabel("PCMWPMACTIONRNDCALENDAR: Calendar");
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

      //======================== Perapre =======================

      f = headblk.addField("TEST_POINT_ID");
      f.setSize(9);
      f.setDynamicLOV("EQUIPMENT_OBJECT_TEST_PNT","HEAD_CONTRACT,MCH_CODE",600,450);
      f.setLabel("PCMWPMACTIONROUNDTESTPOINTID: Testpoint");
      f.setUpperCase();
      f.setCustomValidation("TEST_POINT_ID,MCH_CODE,HEAD_CONTRACT","MSEQOBJECTTESTPOINTDESCRIPTION,MSEQOBJECTTESTPOINTLOCATION");
      f.setMaxLength(6);
      f.setDefaultNotVisible();

      f = headblk.addField("MSEQOBJECTTESTPOINTDESCRIPTION");
      f.setSize(  21);
      f.setFunction("EQUIPMENT_OBJECT_TEST_PNT_API.Get_Description(:HEAD_CONTRACT,:MCH_CODE,:TEST_POINT_ID)");
      f.setReadOnly();
      f.setSize(30);
      f.setDefaultNotVisible();

      f = headblk.addField("MSEQOBJECTTESTPOINTLOCATION");
      f.setSize(20);
      f.setFunction("EQUIPMENT_OBJECT_TEST_PNT_API.Get_Location(:HEAD_CONTRACT,:MCH_CODE,:TEST_POINT_ID)");
      f.setReadOnly();
      f.setDefaultNotVisible();  

      f = headblk.addField("DESCRIPTION");
      f.setSize(52);
      f.setHeight(3);
      f.setLabel("PCMWPMACTIONROUNDDESCRIPTION: Work Description");
      f.setDefaultNotVisible();
      f.setMaxLength(1950);

      f = headblk.addField("VENDOR_NO");
      f.setSize(15);
      f.setDynamicLOV("SUPPLIER_INFO",600,450);
      f.setLabel("PCMWPMACTIONROUNDVENDORNO: Contractor");
      f.setDefaultNotVisible();
      f.setUpperCase();

      f = headblk.addField("VENDORNAME");
      f.setSize(35);                
      f.setFunction("SUPPLIER_INFO_API.Get_Name(:VENDOR_NO)");
      mgr.getASPField("VENDOR_NO").setValidation("VENDORNAME");
      f.setReadOnly();
      f.setMaxLength(10);
      f.setDefaultNotVisible();

      f = headblk.addField("MATERIALS");
      f.setSize(52);
      f.setLabel("PCMWPMACTIONROUNDMATERIALS: Material");
      f.setMaxLength(2000);
      f.setDefaultNotVisible();

      f = headblk.addField("TESTNUMBER");
      f.setSize(12);
      f.setLabel("PCMWPMACTIONROUNDTESTNUMBER: Test Number");
      f.setDefaultNotVisible();
      f.setMaxLength(15);

      f = headblk.addField("ALTERNATE_DESIGNATION");
      f.setSize(52);
      f.setLabel("PCMWPMACTIONROUNDALTERNATESIGNATION: Alternate Designation");
      f.setMaxLength(2000);
      f.setDefaultNotVisible();

      f = headblk.addField("CURRENTPOSITION");
      f.setSize(52);
      f.setLabel("PCMWPMACTIONROUNDCURRENTPOSITION: Latest Transation");
      f.setFunction("PART_SERIAL_CATALOG_API.Get_Alt_Latest_Transaction(:HEAD_CONTRACT,:MCH_CODE)");
      f.setDefaultNotVisible();

      //================== Customer ===============

      f = headblk.addField("CUSTOMER_NO");  
      f.setSize(20);
      f.setDynamicLOV("CUSTOMER_INFO",600,445);
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
      f.setDefaultNotVisible();
      f.setMaxLength(15);
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

      f = headblk.addField("LINE_NO","Number");
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

      //Bug 82435, start
      f = headblk.addField("REASON");                     
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setLabel("CMWPMACTIONROUNDREASON: Created Reason");
      f.setSize(15);

      f = headblk.addField("DONE_BY");                     
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setLabel("CMWPMACTIONROUNDDONEBY: Created By");
      f.setSize(15);

      f = headblk.addField("REV_CRE_DATE", "Datetime");                     
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setLabel("CMWPMACTIONROUNDREVCREDATE: Created Date");
      f.setSize(15);
      //Bug 82435, end

      f = headblk.addField("AGREEMENT_ID");
      f.setSize(10);
      f.setDynamicLOV("CUSTOMER_AGREEMENT",600,450);
      f.setLabel("PCMWPMACTIONROUNDAGREEMENTID: Agreement ID");
      f.setUpperCase();
      f.setHidden();
      f.setMaxLength(20);
      f.setDefaultNotVisible();


      //============================================================

      f = headblk.addField("GROUP_ID");
      f.setSize(10);
      f.setLabel("PCMWPMACTIONROUNDGROUPID: Group ID");
      f.setFunction("EQUIPMENT_OBJECT_API.Get_Group_Id(:HEAD_CONTRACT,:MCH_CODE)");
      f.setReadOnly();
      f.setHidden();

      f = headblk.addField("START_DATE","Date");
      f.setHidden();

      f = headblk.addField("INDEX1");
      f.setFunction("''");
      f.setHidden();    

      f = headblk.addField("PM_TYPE");
      f.setSize(11);
      f.setMandatory();
      f.setHidden();

      f = headblk.addField("LU_NAME");
      f.setHidden();
      f.setFunction("'PmAction'");

      f = headblk.addField("PMNOKEYVALUE");
      f.setFunction("PM_NO_KEY_VALUE"); 
      f.setReadOnly();
      f.setHidden();

      f = headblk.addField("CLIENT_START");
      f.setHidden();
      f.setFunction("''");                     

      f = headblk.addField("CLIENT_START1");
      f.setHidden();
      f.setFunction("''");    

      f = headblk.addField("CLIENT_START2");
      f.setHidden();
      f.setFunction("''");   

      f = headblk.addField("CLIENT_START3");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("CLIENT_INTERVAL3");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("TEMPSTA");
      f.setHidden();
      f.setFunction("''");                     

      f = headblk.addField("TEMPTY");
      f.setHidden();
      f.setFunction("''");    

      f = headblk.addField("TEMPTM");
      f.setHidden();
      f.setFunction("''");   

      f = headblk.addField("NUMDUMMY","Number");
      f.setFunction("0");
      f.setHidden();

      f = headblk.addField("PM_CAN_BE_MODIFIED"); 
      f.setHidden();  
      f.setFunction("Pm_Action_API.Check_New_Row(:PM_NO,:PM_REVISION)");

      f = headblk.addField("INCLUDE_STANDARD"); 
      f.setHidden();  
      f.setFunction("''");

      f = headblk.addField("INCLUDE_PROJECT"); 
      f.setHidden();  
      f.setFunction("''");

      f = headblk.addField("START_VALUE_TEMP","Date");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("CAN_REPLACE"); 
      f.setHidden();  
      f.setFunction("Pm_Action_API.Can_Add_Replacements(:PM_NO,:PM_REVISION)");

      f = headblk.addField("STRDUMMY");
      f.setFunction("''");  
      f.setHidden(); 

      //Bug 89752, Start
      f = headblk.addField("ACTIVE_REV"); 
      f.setHidden();  
      f.setFunction("''");
      //Bug 89752, End

      headblk.setView("PM_ACTION");
      headblk.defineCommand("PM_ACTION_API","New__,Modify__,Remove__,ACTIVE__,OBSOLETE__");
      headblk.disableDocMan();

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("PCMWPMACTIONROUNDHD: Route PM Action"));
      headtbl.setWrap();   
      // 040120  ARWILK  Begin  (Enable Multirow RMB actions)
      headtbl.enableRowSelect();
      // 040120  ARWILK  End  (Enable Multirow RMB actions)

      headbar = mgr.newASPCommandBar(headblk); 

      headbar.addCustomCommand("createNewRevision",mgr.translate("PCMWPMACTIONROUNDCRENEWREVISION: Create New Revision..."));
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("replacements", mgr.translate("PCMWPMACTIONROUNDREPLA: Replacements..."));
      headbar.addCustomCommand("copyPmActions", mgr.translate("PCMWPMACTIONREPL4: Copy..."));
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("activatePm",mgr.translate("PCMWPMACRNDACTIVE: Active"));
      headbar.addCustomCommand("obsoletePm",mgr.translate("PCMWPMACRNDOBSOLETE: Obsolete"));

      //Bug 67948, start
      headbar.addCommandValidConditions("replacements", "OBJSTATE",    "Disable", "Obsolete");
      //Bug 67948, end

      // 031215  ARWILK  Begin  (Replace blocks with tabs)
      headbar.addCustomCommand("activateBudget", "");
      headbar.addCustomCommand("activateJobs", "");
      headbar.addCustomCommand("activateMaterials", "");
      headbar.addCustomCommand("activateMaintenancePlan", "");
      headbar.addCustomCommand("activatePermits", "");
      headbar.addCustomCommand("activateCriteria", "");
      headbar.addCustomCommand("activateHistory", "");
      // 031215  ARWILK  End  (Replace blocks with tabs)

      headbar.defineCommand(headbar.SAVERETURN, "saveReturn", "checkHeadFields(-1)");
      headbar.defineCommand(headbar.SAVENEW, "saveNew", "checkHeadFields(-1)");
      headbar.defineCommand(headbar.CANCELNEW, "cancelNew", "");

      headbar.addCommandValidConditions("activatePm",  "OBJSTATE", "Enable", "Preliminary");
      headbar.addCommandValidConditions("obsoletePm","OBJSTATE", "Enable", "Active");

      // 040120  ARWILK  Begin  (Enable Multirow RMB actions)
      headbar.enableMultirowAction();
      headbar.removeFromMultirowAction("copyPmActions");
      headbar.removeFromMultirowAction("activatePm");
      // 040120  ARWILK  End  (Enable Multirow RMB actions)

      headbar.addCustomCommandGroup("PMSTATUS", mgr.translate("PCMWPMACRNDPMSTATUS: PM Action Status"));
      headbar.setCustomCommandGroup("activatePm", "PMSTATUS");
      headbar.setCustomCommandGroup("obsoletePm", "PMSTATUS");

      headset = headblk.getASPRowSet();
      headset.syncItemSets();   

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

      headlay.defineGroup("", "PM_NO,PM_REVISION,HEAD_CONTRACT,MCH_CODE,MCH_NAME1,ACTION_CODE_ID,ACTION_DESCR1,STATE,OLD_REVISION",false,true);   
      headlay.defineGroup(mgr.translate("PCMWPMACTIONROUNDWOSPEINF: WO Specific Info"), "ORG_CONTRACT,ORG_CODE,OP_STATUS_ID,PRIORITY_ID,MAINT_EMP_SIGN,WORK_TYPE_ID",true,true);
      headlay.defineGroup(mgr.translate("PCMWPMACTIONROUNDPMINFO: PM Info"), "LAST_CHANGED,LATEST_PM,PM_PERFORMED_DATE_BASED,OVERDUE_PERCENT",true,true);
      headlay.defineGroup(mgr.translate("PCMWPMACTIONROUNDROUTES: Routes"), "ROUNDDEF_ID,PM_ORDER_NO,ROLE_CODE,PLAN_MEN,PLAN_HRS,PM_GENERATEABLE",true,true);
      headlay.defineGroup(mgr.translate("PCMWPMACTIONROUNDCALENDAR: Calendar"), "START_VALUE,PM_START_UNIT,INTERVAL,PM_INTERVAL_UNIT",true,true);
      headlay.defineGroup(mgr.translate("PCMWPMACTIONROUNDEVENT: Event"), "CALL_CODE,START_CALL,CALL_INTERVAL",true,true);
      headlay.defineGroup(mgr.translate("PCMWPMACTIONROUNDPMHAS: PM Has"), "OBSOLETE_JOBS,CRITERIA,ADJUSTMENTS,CBHASDOCUMENTS,REPLACEMENTS",true,true);
      headlay.defineGroup(mgr.translate("PCMWPMACTIONROUNDVALID: Valid"), "VALID_FROM,VALID_TO,CALENDAR_ID,CALENDAR_DESC",true,true);
      headlay.defineGroup(mgr.translate("PCMWPMACTIONROUNDPREPARE: Prepare"), "TEST_POINT_ID,MSEQOBJECTTESTPOINTDESCRIPTION,MSEQOBJECTTESTPOINTLOCATION,DESCRIPTION,VENDOR_NO,VENDORNAME,MATERIALS,TESTNUMBER,ALTERNATE_DESIGNATION,CURRENTPOSITION",true,false);
      headlay.defineGroup(mgr.translate("PCMWPMACTIONROUNDREVINFO: Revision Info"),"REASON,DONE_BY,REV_CRE_DATE",true,false);   
      headlay.defineGroup(mgr.translate("PCMWPMACTIONGRPCUSTOMER: Customer Information"),"CUSTOMER_NO,CUSTOMERNAME,CONTRACT_ID,CONTRACT_NAME,LINE_NO,LINE_DESC,CONTRACT_TYPE,INVOICE_TYPE,SIGNATURE",true,false);   

      headlay.setSimple("ACTION_DESCR1");
      headlay.setSimple("MCH_NAME1");
      headlay.setSimple("MSEQOBJECTTESTPOINTDESCRIPTION");
      headlay.setSimple("MSEQOBJECTTESTPOINTLOCATION");
      headlay.setSimple("VENDORNAME");
      headlay.setSimple("CALENDAR_DESC");
      headlay.setSimple("CONTRACT_NAME");
      headlay.setSimple("LINE_DESC");

      // 031215  ARWILK  Begin  (Replace blocks with tabs)
      tabs = mgr.newASPTabContainer();
      tabs.addTab(mgr.translate("PCMWPMACTIONROUNDBUDGETTAB: Budget"), "javascript:commandSet('HEAD.activateBudget','')");
      tabs.addTab(mgr.translate("PCMWPMACTIONROUNDJOBSTAB: Jobs"), "javascript:commandSet('HEAD.activateJobs','')");
      tabs.addTab(mgr.translate("PCMWPMACTIONROUNDMATERIALSTAB: Materials"), "javascript:commandSet('HEAD.activateMaterials','')");
      tabs.addTab(mgr.translate("PCMWPMACTIONROUNDMAINTPLNTAB: Maintenance Plan"), "javascript:commandSet('HEAD.activateMaintenancePlan','')");
      tabs.addTab(mgr.translate("PCMWPMACTIONROUNDPERMITSTAB: Permits"), "javascript:commandSet('HEAD.activatePermits','')");
      tabs.addTab(mgr.translate("PCMWPMACTIONROUNDCRITERIATAB: Criteria"), "javascript:commandSet('HEAD.activateCriteria','')");
      tabs.addTab(mgr.translate("PCMWPMACTIONROUNDHISTORYTAB: History"), "javascript:commandSet('HEAD.activateHistory','')");
      // 031215  ARWILK  End  (Replace blocks with tabs)

      //================================ Materials Tab ===========================================================

      itemblk0 = mgr.newASPBlock("ITEM0");

      f = itemblk0.addField("ITEM0_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk0.addField("ITEM0_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk0.addField("PART_NO");
      f.setSize(18);
      f.setLabel("PCMWPMACTIONROUNDPARTNO: Part No");
      f.setDynamicLOV("INVENTORY_PART_WO_LOV","SPARE_CONTRACT CONTRACT ",600,450);
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONROUNDPANO: List of Part No"));
      f.setCustomValidation("PART_NO,SPARE_CONTRACT,ITEM0_CONTRACT,QTY_PLAN,AGREEMENT_ID,CUSTOMER_NO,CATALOG_NO,OWNER,CONDITION_CODE,PART_OWNERSHIP,ITEM0_PM_NO,ITEM0_PM_REVISION","SPAREDESCRIPTION,INVENTORYFLAG,DIMQUALITY,TYPEDESIGNATION,QTYONHAND,UNITMEAS,CATALOG_NO,SALESPARTDESCRIPTION,LISTPRICE,COST,CONDITION_CODE,ACTIVEIND_DB"); 
      f.setUpperCase();
      f.setMaxLength(25);
      f.setMandatory();
      f.setInsertable();

      f = itemblk0.addField("SPAREDESCRIPTION");
      f.setSize(22);
      f.setLabel("PCMWPMACTIONROUNDSPAREDESCRIPTION: Part Description");
      f.setFunction("INVENTORY_PART_API.Get_Description(:SPARE_CONTRACT,:PART_NO)");
      f.setReadOnly();

      f = itemblk0.addField("SPARE_CONTRACT");
      f.setSize(9);
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONROUNDSPARECONTRACTLOV: List of Site"));   
      f.setMandatory();
      f.setLabel("PCMWPMACTIONROUNDSPARECONTRACT: Site");
      f.setUpperCase();
      f.setMaxLength(5);
      f.setInsertable();
      f.setCustomValidation("QTY_PLAN,SPARE_CONTRACT,AGREEMENT_ID,CUSTOMER_NO,CATALOG_NO","LISTPRICE");

      f = itemblk0.addField("CONDITION_CODE");
      f.setSize(10);
      f.setUpperCase();
      f.setMaxLength(10);
      f.setDynamicLOV("CONDITION_CODE");
      f.setLabel("PCMWPMACTIONROUNDCONDITIONCODE: Condition Code");
      f.setCustomValidation("CONDITION_CODE,PART_NO,SPARE_CONTRACT,OWNER,PART_OWNERSHIP,QTY_PLAN","CONDITION_CODE_DESC,QTYONHAND,COST");

      f = itemblk0.addField("CONDITION_CODE_DESC");
      f.setSize(20);
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONROUNDCONDITIONCODEDESC: Condition Code Description");
      f.setFunction("CONDITION_CODE_API.Get_Description(:CONDITION_CODE)");

      f = itemblk0.addField("PART_OWNERSHIP");
      f.setSize(20);
      f.setInsertable();
      f.setSelectBox();
      f.enumerateValues("PART_OWNERSHIP_API");
      f.setLabel("PCMWPMACTIONROUNDPARTOWNERSHIP: Ownership");
      f.setCustomValidation("PART_OWNERSHIP,PART_NO,SPARE_CONTRACT,OWNER,CONDITION_CODE","PART_OWNERSHIP_DB,QTYONHAND");

      f = itemblk0.addField("PART_OWNERSHIP_DB");
      f.setHidden();

      f = itemblk0.addField("OWNER");
      f.setSize(18);
      f.setMaxLength(20);
      f.setInsertable();
      f.setLabel("PCMWPMACTIONROUNDPARTOWNER: Owner");
      f.setCustomValidation("OWNER,PART_OWNERSHIP_DB,CONDITION_CODE,PART_NO,SPARE_CONTRACT,PART_OWNERSHIP","OWNER_NAME,QTYONHAND");
      f.setCustomValidation("OWNER,PART_OWNERSHIP_DB","OWNER_NAME");
      f.setDynamicLOV("CUSTOMER_INFO");

      f = itemblk0.addField("OWNER_NAME");
      f.setSize(20);
      f.setMaxLength(100);
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONROUNDOWNERNAME: Owner Name");
      f.setFunction("CUSTOMER_INFO_API.Get_Name(:OWNER)");

      f = itemblk0.addField("INVENTORYFLAG");
      f.setSize(14);
      f.setLabel("PCMWPMACTIONROUNDINVENTORYFLAG: Inventory Part");
      f.setFunction("PURCHASE_PART_API.Get_Inventory_Flag(:SPARE_CONTRACT,:PART_NO)");
      f.setReadOnly();

      f = itemblk0.addField("DIMQUALITY");
      f.setSize(18);
      f.setLabel("PCMWPMACTIONROUNDDIMQUALITY: Dimension/ Quality");
      f.setFunction("Inventory_Part_API.Get_Dim_Quality(:SPARE_CONTRACT,:PART_NO)");
      f.setReadOnly();

      f = itemblk0.addField("TYPEDESIGNATION");
      f.setSize(18);
      f.setLabel("PCMWPMACTIONROUNDTYPEDESIGNATION: Type Designation");
      f.setFunction("Inventory_Part_API.Get_Type_Designation(:SPARE_CONTRACT,:PART_NO)");
      f.setReadOnly();

      f = itemblk0.addField("QTYONHAND","Number");
      f.setSize(15);
      f.setLabel("PCMWPMACTIONROUNDQTYONHAND: Quantity on Hand");
      f.setFunction("Inventory_Part_In_Stock_API.Get_Inventory_Quantity(:SPARE_CONTRACT,:PART_NO,NULL,'ONHAND',NULL,NULL,:PART_OWNERSHIP_DB,NULL,NULL,NULL,:OWNER,NULL,'PICKING','F','PALLET','DEEP','BUFFER','DELIVERY','SHIPMENT','MANUFACTURING',NULL,NULL,NULL,NULL,'TRUE','FALSE',NULL,NULL,NULL,NULL,NULL,NULL,:CONDITION_CODE)");
      f.setReadOnly();

      f = itemblk0.addField("UNITMEAS");
      f.setSize(11);
      f.setLabel("PCMWPMACTIONROUNDUNITMEAS: Unit");
      f.setFunction("Purchase_Part_Supplier_API.Get_Unit_Meas(:SPARE_CONTRACT,:PART_NO)");
      f.setReadOnly();

      f = itemblk0.addField("QTY_PLAN","Number");
      f.setSize(15);
      f.setMandatory();
      f.setLabel("PCMWPMACTIONROUNDQTYPLAN: Planned Quantity");
      f.setCustomValidation("QTY_PLAN,SPARE_CONTRACT,AGREEMENT_ID,PART_NO,CUSTOMER_NO,CATALOG_NO,SERIAL_NO,CONFIGURATION_IDS,CONDITION_CODE","COST,LISTPRICE");

      f = itemblk0.addField("ITEM0_PM_NO","Number");
      f.setSize(11);
      f.setDynamicLOV("PM_ACTION",600,450);
      f.setMandatory();
      f.setHidden();
      f.setLabel("PCMWPMACTIONROUNDITEM0PMNO: PM no");
      f.setDbName("PM_NO");

      f = itemblk0.addField("ITEM0_PM_REVISION");
      f.setSize(11);
      f.setMandatory();
      f.setHidden();
      f.setLabel("PCMWPMACTIONROUNDITEM0PMREV: PM Revision");
      f.setDbName("PM_REVISION");

      f = itemblk0 .addField("ITEM0_CONTRACT");
      f.setSize(17);
      f.setLabel("PCMWPMACTIONROUNDCONTNEW: Sales Part Site");
      f.setDbName("CONTRACT");
      f.setMaxLength(5);
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONROUNDCONTNEWLOV: List of Sales Part Site"));
      f.setUpperCase();
      f.setReadOnly();
      f.setDefaultNotVisible();

      f= itemblk0 .addField("CATALOG_NO");
      f.setDynamicLOV("SALES_PART","ITEM0_CONTRACT CONTRACT",600,450);
      f.setCustomValidation("QTY_PLAN,SPARE_CONTRACT,AGREEMENT_ID,ITEM0_CONTRACT,CUSTOMER_NO,CATALOG_NO","SALESPARTDESCRIPTION,LISTPRICE");
      f.setLabel("PCMWPMACTIONROUNDDESC: Sales Part");
      f.setSize(12);
      f.setMaxLength(25);
      f.setUpperCase();
      f.setDefaultNotVisible();

      f = itemblk0.addField("SALESPARTDESCRIPTION");
      f.setSize(25);
      f.setLabel("PCMWPMACTIONROUNDSALESPART1: Sales Part Description ");
      if (mgr.isModuleInstalled("ORDER"))
         f.setFunction("SALES_PART_API.Get_Catalog_Desc(:ITEM0_CONTRACT,:CATALOG_NO)");
      else
         f.setFunction("''");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk0.addField("COST","Number"); 
      f.setSize(11);
      f.setLabel("PCMWPMACTIONROUNDCOST: Cost");
      f.setFunction("''");
      f.setReadOnly();

      f = itemblk0.addField("LISTPRICE","Number"); 
      f.setSize(11);
      f.setLabel("PCMWPMACTIONROUNDLISTPRICE: Sales Price");
      f.setDbName("REVENUE");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk0.addField("ITEM0_JOB_ID","Number","#");
      f.setDbName("JOB_ID");
      f.setDynamicLOV("PM_ACTION_JOB","PM_NO,PM_REVISION",600,445);
      f.setLabel("PCMWPMACTIONROUNDJOBID0: Job ID");
      f.setSize(8);
      f.setInsertable();

      f = itemblk0.addField("PM_SPARE_SEQ","Number"); 
      f.setSize(11);
      f.setHidden();
      f.setLabel("PCMWPMACTIONROUNDPMSPARESEQ: Spare Seq");

      f = itemblk0.addField("ISINSTALLED");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("NCOST","Number");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("SSUPPLIER");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("TODAY","Date");
      f.setFunction("SYSDATE");
      f.setHidden();

      f = itemblk0.addField("PACKAGENAME");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("METHODNAME");
      f.setFunction("''");
      f.setHidden(); 

      f = itemblk0.addField("PURCHPART");
      f.setFunction("''");
      f.setHidden(); 

      f = itemblk0.addField("SUPPLY_CODE");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("CHCKEXIST");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("SUPPLIER_ID");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("F_SYSDATE","Date");
      f.setFunction("''");
      f.setHidden();     

      f = itemblk0.addField("AGRCURRCODE");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("CUSTCURRCODE");        
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("CURRENCY_CODE");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("PRICELIST");   
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("PREVENUE");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("INPART");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("INPAMEVAL");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("ISINST");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("PRIMSUP");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("BASEPRI"); 
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("BASE_PRICE");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("VENDOER_NO");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("PRICE_DATE");
      f.setFunction("''");
      f.setHidden();

      // Bug 43249, start
      f = itemblk0.addField("LOCATION_NO");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("LOT_BATCH_NO");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("SERIAL_NO");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("ENG_CHG_LEVEL");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("WAIV_DEV_REJ_NO");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("COND_CODE_USAGE");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("CONFIGURATION");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("QTY_TYPE");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("EXPIRATION");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("SUPPLY_CONTROL");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("OWNERSHIP_TYPE1");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("OWNERSHIP_TYPE2");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("OWNERSHIP_TYPE3");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("OWNERSHIP_TYPE4");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("OWNER_VENDOR");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("LOCATION_TYPE1");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("LOCATION_TYPE2");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("LOCATION_TYPE3");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("LOCATION_TYPE4");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("LOCATION_TYPE5");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("LOCATION_TYPE6");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("LOCATION_TYPE7");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("LOCATION_TYPE8");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("ORDER_ISSUE");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("AUTOMAT_RESERV");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("MANUAL_RESERV");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("ACTIVITY_SEQ");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("PROJECT_ID");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("CAT_EXIST","Number");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("PM_SITE");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("CONFIGURATION_IDS");
      f.setFunction("''");
      f.setHidden();

      f = itemblk0.addField("ACTIVEIND");
      f.setHidden();
      f.setFunction("''");

      f = itemblk0.addField("ACTIVEIND_DB");
      f.setHidden();
      f.setFunction("''");

      itemblk0.setView("PM_ACTION_SPARE_PART");
      itemblk0.defineCommand("PM_ACTION_SPARE_PART_API","New__,Modify__,Remove__");
      itemset0 = itemblk0.getASPRowSet();
      itemblk0.setMasterBlock(headblk);

      itembar0 = mgr.newASPCommandBar(itemblk0);

      itembar0.enableCommand(itembar0.FIND);

      itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0"); 
      itembar0.defineCommand(itembar0.SAVERETURN,"saveReturnItem0","checkItem0Fields(-1)");
      itembar0.defineCommand(itembar0.SAVENEW,null,"checkItem0Fields(-1)");
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");

      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setWrap();
      // 040120  ARWILK  Begin  (Enable Multirow RMB actions)
      itemtbl0.enableRowSelect();
      // 040120  ARWILK  End  (Enable Multirow RMB actions)

      // 031215  ARWILK  Begin  (Links with RMB's)
      itembar0.addCustomCommand("sparePartsinObject",mgr.translate("PCMWPMACTIONROUNDSPAREPARTOBJ: Spare Parts in Object..."));
      // 031215  ARWILK  End  (Links with RMB's)
      itembar0.addCustomCommandSeparator();
      itembar0.addCustomCommand("spareParts",mgr.translate("PCMWPMACTIONROUNDCDN1: Spare Parts in Detached Part List..."));
      if (mgr.isPresentationObjectInstalled("invenw/InventoryPartInventoryPartCurrentOnHand.page"))
         itembar0.addCustomCommand("currQuntity",mgr.translate("PCMWPMACTIONROUNDCDN2: Current Quantity on Hand..."));
      if (mgr.isPresentationObjectInstalled("invenw/InventoryPartAvailabilityPlanningQry.page"))
         itembar0.addCustomCommand("availDetail",mgr.translate("PCMWPMACTIONROUNDCDN3: Query - Inventory Part Availability Planning..."));
      if (mgr.isPresentationObjectInstalled("invenw/InventoryPart.page"))
         itembar0.addCustomCommand("inventoryPart",mgr.translate("PCMWPMACTIONROUNDCDN4: Inventory Part..."));
      if (mgr.isPresentationObjectInstalled("purchw/PurchasePartSupplier.page"))
         itembar0.addCustomCommand("supplierPerPart",mgr.translate("PCMWPMACTIONROUNDCDN5: Supplier per Part..."));

      // 031229  ARWILK  Begin  (Links with multirow RMB's)
      itembar0.enableMultirowAction();
      // 031229  ARWILK  End  (Links with multirow RMB's)

      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);   

      //============================== Maintenance Plan Tab ======================================================

      itemblk1 = mgr.newASPBlock("ITEM1");

      f = itemblk1.addField("ITEM1_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk1.addField("ITEM1_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk1.addField("PLANNED_DATE","Date");
      f.setSize(12);
      f.setLabel("PCMWPMACTIONROUNDPLADATE: Planned Date");

      f = itemblk1.addField("PLANNED_WEEK");
      f.setSize(13);
      f.setLabel("PCMWPMACTIONROUNDPLAWEEK: Planned Week");
      f.setReadOnly();

      f = itemblk1.addField("GENERATION_DATE","Date");
      f.setSize(16);
      f.setLabel("PCMWPMACTIONROUNDGENDATE: Generation Date");
      f.setReadOnly();

      f = itemblk1.addField("COMPLETION_DATE","Date");
      f.setSize(16);
      f.setLabel("PCMWPMACTIONROUNDCOMDATE: Completion Date");
      f.setReadOnly();

      f = itemblk1.addField("PM_ADJUSTED_DATE");
      f.setSize(13);
      f.setLabel("PCMWPMACTIONROUNDPMADJUSTEDDATE: Adjusted");
      f.setSelectBox();
      f.enumerateValues("PM_ADJUSTED_DATE_API");
      f.unsetSearchOnDbColumn();

      f = itemblk1.addField("ITEM1_PM_NO","Number");
      f.setSize(11);
      f.setDynamicLOV("PM_ACTION",600,450);
      f.setMandatory();
      f.setHidden();
      f.setDbName("PM_NO");


      f = itemblk1.addField("ITEM1_PM_REVISION");
      f.setSize(11);
      f.setMandatory();
      f.setHidden();
      f.setDbName("PM_REVISION");

      f = itemblk1.addField("ITEM1_PM_GENERATEABLE");
      f.setSize(13);
      f.setLabel("PCMWPMACTIONROUNDPMGENERATABLE: Generatable");
      f.setSelectBox();
      f.enumerateValues("PM_GENERATEABLE_API");
      f.unsetSearchOnDbColumn();
      f.setDbName("PM_GENERATEABLE");


      f = itemblk1.addField("ITEM1_WO_NO","Number","#");
      f.setSize(13);
      f.setLabel("PCMWPMACTIONROUNDWNO: WO Number");
      f.setReadOnly();
      f.setDbName("WO_NO");

      f = itemblk1.addField("STATUS");
      f.setSize(13);
      f.setLabel("PCMWPMACTIONROUNDSTAT: Status");
      f.setReadOnly();
      f.setFunction("WORK_ORDER_API.Get_Wo_Status_Id(:ITEM1_WO_NO)");

      f = itemblk1.addField("NOTE");
      f.setSize(15);
      f.setLabel("PCMWPMACTIONROUNDNOTE: Inspection Note");
      f.setReadOnly();
      f.setFunction("WORK_ORDER_API.Get_Note(:ITEM1_WO_NO,:ITEM1_PM_NO,:ITEM1_PM_REVISION)");

      f = itemblk1.addField("ITEM1_SIGNATURE");
      f.setSize(13);
      f.setUpperCase();
      f.setLabel("PCMWPMACTIONROUNDSIGNA: Signature");
      f.setDbName("SIGNATURE");
      f.setDynamicLOV("EMPLOYEE_LOV","ITEM1_COMPANY COMPANY",600,450); 

      f = itemblk1.addField("REMARK");
      f.setSize(13);
      f.setLabel("PCMWPMACTIONROUNDREMAR: Remark");

      f = itemblk1.addField("ITEM1_MCH_CODE");
      f.setSize(13);
      f.setHidden();
      f.setFunction("PM_ACTION_CALENDAR_PLAN_API.Get_Mch_Code(:ITEM1_PM_NO,:ITEM1_PM_REVISION)");

      f = itemblk1.addField("ITEM1_MCH_DESC");
      f.setSize(13);
      f.setHidden();
      f.setFunction("PM_ACTION_CALENDAR_PLAN_API.Get_Mch_Desc(:ITEM1_PM_NO,:ITEM1_PM_REVISION)");

      f = itemblk1.addField("ITEM1_SITE");
      f.setSize(13);
      f.setHidden();
      f.setFunction("PM_ACTION_CALENDAR_PLAN_API.Get_Site(:ITEM1_PM_NO,:ITEM1_PM_REVISION)");

      f = itemblk1.addField("GEN_ID","Number");
      f.setSize(13);
      f.setHidden();

      f = itemblk1.addField("ITEM1_GEN_TYPE");
      f.setSize(8);
      f.setHidden();
      f.setFunction("PM_GENERATION_API.Get_Generation_Type(:GEN_ID)");

      f = itemblk1.addField("REPLACED_BY_PM_NO","Number");
      f.setSize(13);
      f.setHidden();

      f = itemblk1.addField("REPLACED_BY_PM_REVISION");
      f.setSize(13);
      f.setHidden();

      f = itemblk1.addField("REPLACED_BY_SEQ_NO","Number");
      f.setSize(13);
      f.setHidden();

      f = itemblk1.addField("EVENT");
      f.setSize(13);
      f.setHidden();

      f = itemblk1.addField("EVENT_SEQ","Number");
      f.setSize(13);
      f.setHidden();

      f = itemblk1.addField("TESTPOINT");
      f.setSize(13);
      f.setHidden();

      f = itemblk1.addField("ITEM1_PARAMETER");
      f.setSize(13);
      f.setHidden();
      f.setDbName("PARAMETER");

      f = itemblk1.addField("VALUE","Number");
      f.setSize(13);
      f.setHidden();

      f = itemblk1.addField("ITEM1_COMPANY");
      f.setSize(13);
      f.setHidden();
      f.setDbName("COMPANY");

      f = itemblk1.addField("ITEM1_PM_TYPE");
      f.setSize(13);
      f.setHidden();
      f.setFunction("PM_ACTION_CALENDAR_PLAN_API.Get_PM_TYPE(:ITEM1_PM_NO,:ITEM1_PM_REVISION)");

      f = itemblk1.addField("ITEM1_SEQ_NO","Number");  
      f.setSize(8);
      f.setHidden();
      f.setDbName("SEQ_NO");

      f = itemblk1.addField("ITEM1_HAS_WO");
      f.setHidden();  
      f.setFunction("Pm_Action_Calendar_Plan_API.Has_Wo_No(:ITEM1_PM_NO,:ITEM1_PM_REVISION,:ITEM1_SEQ_NO)");

      f = itemblk1.addField("ITEM1_HAS_COMPLETION_DATE");
      f.setHidden();  
      f.setFunction("Pm_Action_Calendar_Plan_API.Has_Completion_Date(:ITEM1_PM_NO,:ITEM1_PM_REVISION,:ITEM1_SEQ_NO)");

      f = itemblk1.addField("ITEM1_STATUS");
      f.setSize(13);
      f.setHidden();
      f.setFunction("WORK_ORDER_API.Get_Wo_Status_Id_Cl(:ITEM1_WO_NO)");

      itemblk1.setView("PM_ACTION_CALENDAR_PLAN");
      itemblk1.defineCommand("PM_ACTION_CALENDAR_PLAN_API","New__,Modify__,Remove__");
      itemset1 = itemblk1.getASPRowSet();
      itemblk1.setMasterBlock(headblk);

      itembar1 = mgr.newASPCommandBar(itemblk1);

      itembar1.enableCommand(itembar1.FIND);

      itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");         
      itembar1.defineCommand(itembar1.SAVERETURN,null,"checkItem1Fields(-1)");   
      itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
      itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1");
      itembar1.defineCommand(itembar1.SAVENEW,null,"checkItem1Fields(-1)");

      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setWrap();
      // 040120  ARWILK  Begin  (Enable Multirow RMB actions)
      itemtbl1.enableRowSelect();
      // 040120  ARWILK  End  (Enable Multirow RMB actions)

      itembar1.addCustomCommand("reportInRouteWO",mgr.translate("PCMWPMACTIONROUNDREPINROUWO: Report in Route Work Order..."));   
      itembar1.addCustomCommand("viewHistoricalWO",mgr.translate("PCMWPMACTIONROUNDVIHISWO: View Historical Work Order..."));
      itembar1.addCustomCommand("viewGenerationDetails",mgr.translate("PCMWPMACTIONCCND5: View Generation Details..."));

      // 040120  ARWILK  Begin  (Enable Multirow RMB actions)
      itembar1.addCommandValidConditions("reportInRouteWO",      "ITEM1_HAS_WO",               "Enable",      "TRUE");
      itembar1.appendCommandValidConditions("reportInRouteWO",   "ITEM1_PM_TYPE",              "ActiveRound");
      itembar1.appendCommandValidConditions("reportInRouteWO",   "ITEM1_HAS_COMPLETION_DATE",  "FALSE");

      itembar1.addCommandValidConditions("viewHistoricalWO",      "ITEM1_HAS_WO",              "Enable",      "TRUE");
      itembar1.appendCommandValidConditions("viewHistoricalWO",   "ITEM1_HAS_COMPLETION_DATE","TRUE");
      itembar1.appendCommandValidConditions("viewHistoricalWO",   "ITEM1_STATUS","Finished");

      itembar1.addCommandValidConditions("viewGenerationDetails", "WO_NO",                     "Disable",     "null");

      itembar1.enableMultirowAction();
      itembar1.removeFromMultirowAction("viewGenerationDetails");
      // 040120  ARWILK  End  (Enable Multirow RMB actions)

      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);   

      //============================== Permits Tab ===============================================================

      itemblk2 = mgr.newASPBlock("ITEM2");

      f = itemblk2.addField("ITEM2_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk2.addField("ITEM2_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk2.addField("ITEM2_PM_NO","Number");
      f.setSize(11);
      f.setMandatory();
      f.setHidden();
      f.setDbName("PM_NO");

      f = itemblk2.addField("ITEM2_PM_REVISION");
      f.setSize(11);
      f.setMandatory();
      f.setHidden();
      f.setDbName("PM_REVISION");

      f = itemblk2.addField("PERMIT_TYPE_ID");
      f.setSize(13);
      f.setDynamicLOV("PERMIT_TYPE_NONE",600,450);
      f.setMandatory();
      f.setLabel("PCMWPMACTIONROUNDPERMITTYPID: Permit Type");
      f.setUpperCase();
      f.setReadOnly();
      f.setInsertable();
      f.setMaxLength(4);

      f = itemblk2.addField("PERMITTYPEDESCRIPTION");
      f.setSize(65);
      f.setLabel("PCMWPMACTIONROUNDPERMITTYPEDESCRIPTION: Description");
      f.setFunction("PERMIT_TYPE_API.Get_Description(:PERMIT_TYPE_ID)");
      mgr.getASPField("PERMIT_TYPE_ID").setValidation("PERMITTYPEDESCRIPTION");
      f.setReadOnly();

      itemblk2.setView("PM_ACTION_PERMIT");
      // 040120  ARWILK  Begin  (Enhance Performance)
      itemblk2.defineCommand("PM_ACTION_PERMIT_API","New__,Remove__");
      // 040120  ARWILK  End  (Enhance Performance)
      itemset2 = itemblk2.getASPRowSet();
      itemblk2.setMasterBlock(headblk);

      itembar2 = mgr.newASPCommandBar(itemblk2);

      itembar2.enableCommand(itembar2.FIND);

      itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");   
      itembar2.defineCommand(itembar2.SAVERETURN,null,"checkItem2Fields(-1)");   
      itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");   
      itembar2.defineCommand(itembar2.NEWROW,"newRowITEM2");   
      itembar2.defineCommand(itembar2.SAVENEW,null,"checkItem2Fields(-1)"); 

      itembar2.addCustomCommand("attributes",mgr.translate("PCMWPMACTIONROUNDCDN111: Attributes..."));

      // 040120  ARWILK  Begin  (Enable Multirow RMB actions)
      itembar2.enableMultirowAction();
      // 040120  ARWILK  End  (Enable Multirow RMB actions)

      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.setWrap();
      // 040120  ARWILK  Begin  (Enable Multirow RMB actions)
      itemtbl2.enableRowSelect();
      // 040120  ARWILK  End  (Enable Multirow RMB actions)

      itemlay2 = itemblk2.getASPBlockLayout();
      itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);   

      //============================== Criteria ==================================================================

      itemblk3 = mgr.newASPBlock("ITEM3");

      f = itemblk3.addField("ITEM3_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk3.addField("ITEM3_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk3.addField("ITEM3_PM_NO","Number");
      f.setSize(11);
      f.setMandatory();
      f.setHidden();
      f.setDbName("PM_NO");

      f = itemblk3.addField("ITEM3_PM_REVISION");
      f.setSize(11);
      f.setMandatory();
      f.setHidden();
      f.setDbName("PM_REVISION");

      f = itemblk3.addField("ITEM3_CONTRACT");
      f.setSize(11);
      f.setMandatory();
      f.setHidden();
      f.setDbName("CONTRACT");
      f.setUpperCase();

      f = itemblk3.addField("ITEM3_MCH_CODE");
      f.setSize(11);
      f.setMandatory();
      f.setHidden();
      f.setDbName("MCH_CODE");
      f.setUpperCase();

      f = itemblk3.addField("ITEM3_TEST_POINT_ID");
      f.setSize(11);
      f.setDynamicLOV("EQUIPMENT_OBJECT_TEST_PNT","ITEM3_CONTRACT HEAD_CONTRACT, ITEM3_MCH_CODE MCH_CODE",600,450);  
      f.setMandatory();
      f.setLabel("PCMWPMACTIONROUNDITEM3TESTPOINID: Testpoint");
      f.setDbName("TEST_POINT_ID");
      f.setUpperCase();
      f.setMaxLength(6);

      f = itemblk3.addField("PARAMETER_CODE");
      f.setSize(9);
      f.setDynamicLOV("EQUIPMENT_OBJECT_PARAM","ITEM3_CONTRACT CONTRACT,ITEM3_MCH_CODE MCH_CODE,ITEM3_TEST_POINT_ID TEST_POINT_ID",600,450);
      f.setMandatory();
      f.setLabel("PCMWPMACTIONROUNDPARAMETERCODE: Parameter");
      f.setCustomValidation("PARAMETER_CODE,ITEM3_MCH_CODE,ITEM3_CONTRACT,ITEM3_TEST_POINT_ID","PARAMETERDESCRIPTION,VALUETYPE,UNITCODE");
      f.setUpperCase();
      f.setMaxLength(5);

      f = itemblk3.addField("PARAMETERDESCRIPTION");
      f.setSize(25);
      f.setLabel("PCMWPMACTIONROUNDPARAMETERDESCRIPTION: Parameter Description");
      f.setFunction("MEASUREMENT_PARAMETER_API.Get_Description(:PARAMETER_CODE)");
      f.setReadOnly();

      f = itemblk3.addField("VALUETYPE");
      f.setSize(11);
      f.setLabel("PCMWPMACTIONROUNDVALUETYPE: Type");
      f.setFunction("MEASUREMENT_PARAMETER_API.Get_Type(:PARAMETER_CODE)");
      f.setReadOnly();

      f = itemblk3.addField("UNITCODE");
      f.setSize(6);
      f.setLabel("PCMWPMACTIONROUNDUNITCODE: Unit");
      f.setFunction("EQUIPMENT_OBJECT_PARAM_API.Get_Unit(:ITEM3_CONTRACT,:MCH_CODE,:TEST_POINT_ID,:PARAMETER_CODE)");
      f.setReadOnly();

      f = itemblk3.addField("MIN_VALUE","Number");
      f.setSize(10);
      f.setLabel("PCMWPMACTIONROUNDMINVALUE: Min Value");

      f = itemblk3.addField("MAX_VALUE","Number");
      f.setSize(10);
      f.setLabel("PCMWPMACTIONROUNDMAXVALUE: Max Value");

      f = itemblk3.addField("ACC_START_VALUE","Number");
      f.setSize(10);
      f.setLabel("PCMWPMACTIONROUNDACCSTARTVALUE: Start Value");

      f = itemblk3.addField("ACC_INTERVAL","Number");
      f.setSize(9);
      f.setLabel("PCMWPMACTIONROUNDACCINTERVAL: Interval");

      f = itemblk3.addField("LAST_VALUE","Number");
      f.setSize(10);
      f.setLabel("PCMWPMACTIONROUNDLASTVALUE: Last Value");
      f.setReadOnly();

      f = itemblk3.addField("PMGENVALUE","Number");
      f.setSize(12);
      f.setLabel("PCMWPMACTIONROUNDPMGENVALUE: Generation");
      f.setReadOnly();

      f = itemblk3.addField("CRITERIA_SEQ","Number");
      f.setSize(11);
      f.setHidden();
      f.setLabel("PCMWPMACTIONROUNDCRITERIASEQ: Criteria Seq");

      itemblk3.setView("PM_ACTION_CRITERIA");
      itemblk3.defineCommand("PM_ACTION_CRITERIA_API","New__,Modify__,Remove__");
      itemset3 = itemblk3.getASPRowSet();
      itemblk3.setMasterBlock(headblk);

      itembar3 = mgr.newASPCommandBar(itemblk3);

      itembar3.enableCommand(itembar3.FIND);

      itembar3.defineCommand(itembar3.OKFIND,"okFindITEM3"); 
      itembar3.defineCommand(itembar3.SAVERETURN,null,"checkItem3Fields(-1)");  
      itembar3.defineCommand(itembar3.COUNTFIND,"countFindITEM3");
      itembar3.defineCommand(itembar3.NEWROW,"newRowITEM3");    
      itembar3.defineCommand(itembar3.SAVENEW,null,"checkItem3Fields(-1)");

      itembar3.addCustomCommand("parameters",mgr.translate("PCMWPMACTIONROUNDCRI1: Parameters..."));
      itembar3.addCustomCommand("measurements",mgr.translate("PCMWPMACTIONROUNDCRI2: Measurements..."));

      // 040120  ARWILK  Begin  (Enable Multirow RMB actions)
      itembar3.enableMultirowAction();
      // 040120  ARWILK  End  (Enable Multirow RMB actions)

      itemtbl3 = mgr.newASPTable(itemblk3);
      itemtbl3.setWrap();

      itemlay3 = itemblk3.getASPBlockLayout();
      itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);      

      //================================================================
      // History
      //================================================================= 

      itemblk4 = mgr.newASPBlock("ITEM4");

      f = itemblk4.addField("WO_NO","Number","#");
      f.setSize(14);
      f.setMandatory();
      f.setLabel("PCMWPMACTIONROUNDWONO: WO No");

      f = itemblk4.addField("ITEM4_PM_ORDER_NO","Number");
      f.setSize(11);
      f.setLabel("PCMWPMACTIONROUNDITEMPMORDERNO: Order");
      f.setDbName("PM_ORDER_NO");

      f = itemblk4.addField("ITEM4_CONTRACT");
      f.setSize(11);
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
      f.setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONROUNDITEM4CONTRACTLOV: List of Site"));   
      f.setLabel("PCMWPMACTIONROUNDITEM4CONTRACT: Site");
      f.setUpperCase();
      f.setDbName("CONTRACT");

      f = itemblk4.addField("ITEM4_COMPANY");
      f.setSize(11);
      f.setLabel("PCMWPMACTIONROUNDCOMPANY: Company");
      f.setUpperCase();
      f.setDbName("COMPANY");

      f = itemblk4.addField("ITEM4_ROUND_REPORT_IN_STATUS");
      f.setSize(11);
      f.setLabel("PCMWPMACTIONROUNDROUNREPORINSTATUS: Status");
      f.setDbName("ROUND_REPORT_IN_STATUS");

      f = itemblk4.addField("REPORT_IN_BY");
      f.setSize(14);
      f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,450);
      f.setLabel("PCMWPMACTIONROUNDREPONBY: Reported In by");
      f.setUpperCase();

      f = itemblk4.addField("REPORT_IN_BY_ID");
      f.setSize(12);
      f.setHidden();
      f.setLabel("PCMWPMACTIONROUNDREPORTINBYD: Reported In by ID");
      f.setUpperCase();

      f = itemblk4.addField("PLAN_S_DATE","Datetime");
      f.setSize(17);
      f.setLabel("PCMWPMACTIONROUNDPLANSDATE: Planned Start");

      f = itemblk4.addField("REAL_S_DATE","Datetime");
      f.setSize(16);
      f.setLabel("PCMWPMACTIONROUNDREALSDATE: Actual Start");

      f = itemblk4.addField("PLAN_F_DATE","Datetime");
      f.setSize(18);
      f.setLabel("PCMWPMACTIONROUNDPLANFDATE: Planned Completion");

      f = itemblk4.addField("REAL_F_DATE","Datetime");
      f.setSize(16);
      f.setLabel("PCMWPMACTIONROUNDREALFDATE: Actual Completion");

      f = itemblk4.addField("ITEM4_PM_NO","Number");
      f.setSize(11);
      f.setHidden();
      f.setDbName("PM_NO");

      f = itemblk4.addField("ITEM4_PM_REVISION");
      f.setSize(11);
      f.setHidden();
      f.setDbName("PM_REVISION");

      itemblk4.setView("DUAL");
      // 040120  ARWILK  Begin  (Enhance Performance)
      itemblk4.defineCommand("HISTORICAL_ROUND_API", "");
      // 040120  ARWILK  End  (Enhance Performance)
      itemset4 = itemblk4.getASPRowSet();
      itemblk4.setMasterBlock(headblk);

      itembar4 = mgr.newASPCommandBar(itemblk4);

      itembar4.enableCommand(itembar4.FIND);   

      itembar4.defineCommand(itembar4.OKFIND,"okFindITEM4");   
      itembar4.defineCommand(itembar4.COUNTFIND,"countFindITEM4");  

      itemtbl4 = mgr.newASPTable(itemblk4);
      itemtbl4.setWrap();

      itemlay4 = itemblk4.getASPBlockLayout();
      itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT);   

      //================================================================
      // Budget
      //================================================================= 

      itemblk5 = mgr.newASPBlock("ITEM5");

      f = itemblk5.addField("ITEM5_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk5.addField("ITEM5_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk5.addField("ITEM5_PM_NO");   
      f.setHidden();
      f.setDbName("PM_NO");   

      f = itemblk5.addField("ITEM5_PM_REVISION");   
      f.setHidden();
      f.setDbName("PM_REVISION");

      f = itemblk5.addField("PERS_BUDGET_COST","Money");   
      f.setSize(14);
      f.setCustomValidation("PERS_BUDGET_COST,MAT_BUDGET_COST,EXT_BUDGET_COST", "TOT_BUDGET_COST");
      f.setLabel("PCMWPMACTIONROUNDPERBUDCO1: Personnel");

      f = itemblk5.addField("MAT_BUDGET_COST","Money");   
      f.setSize(14);
      f.setCustomValidation("PERS_BUDGET_COST,MAT_BUDGET_COST,EXT_BUDGET_COST", "TOT_BUDGET_COST");
      f.setLabel("PCMWPMACTIONROUNDMATBUDCO1: Material");

      f = itemblk5.addField("EXT_BUDGET_COST","Money");   
      f.setSize(14);
      f.setCustomValidation("PERS_BUDGET_COST,MAT_BUDGET_COST,EXT_BUDGET_COST", "TOT_BUDGET_COST");
      f.setLabel("PCMWPMACTIONROUNDEXTBUDCO1: External");

      f = itemblk5.addField("TOT_BUDGET_COST","Money");   
      f.setFunction("nvl(PERS_BUDGET_COST,0)+nvl(MAT_BUDGET_COST,0)+nvl(EXT_BUDGET_COST,0)");
      f.setSize(14);
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONROUNDTOTAL1: Total");

      f = itemblk5.addField("PLAN_PERS_COST","Money"); 
      f.setFunction("PM_Action_API.Get_Plan_Pers_Cost(PM_NO,PM_REVISION)");  
      f.setSize(14);
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONROUNDPERBUDCO2: Personnel");

      f = itemblk5.addField("PLAN_MAT_COST","Money");   
      f.setCustomValidation("ITEM5_PM_NO,ITEM5_PM_REVISION", "TOT_BUDGET_COST");
      f.setSize(14);
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONROUNDMATBUDCO2: Material");   
      f.setFunction("''");

      f = itemblk5.addField("PLAN_EXT_COST","Money");   
      f.setCustomValidation("ITEM5_PM_NO,ITEM5_PM_REVISION", "PLAN_EXT_COST");
      f.setSize(14);
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONROUNDEXTBUDCO2: External");   
      f.setFunction("''");

      f = itemblk5.addField("TOT_PLAN_COST","Money");   
      f.setFunction("''");
      f.setSize(14);
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONROUNDTOTAL2: Total");   

      f = itemblk5.addField("TOTAL_INV_VALUE","Money");   
      f.setFunction("''");
      f.setSize(14);
      f.setHidden();

      f = itemblk5.addField("PERS_COST_LAST_WO","Money");   
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk5.addField("MAT_COST_LAST_WO","Money");   
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk5.addField("EXT_COST_LAST_WO","Money");   
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk5.addField("TOT_COST_LAST_WO","Money");   
      f.setFunction("nvl(PERS_COST_LAST_WO,0)+nvl(MAT_COST_LAST_WO,0)+nvl(EXT_COST_LAST_WO,0)");
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk5.addField("PLAN_PERS_REV","Money"); 
      f.setCustomValidation("ITEM5_PM_NO,ITEM5_PM_REVISION", "PLAN_PERS_REV");
      f.setSize(14);
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONROUNDPLANPERREV: Personnel");   
      f.setFunction("''");

      f = itemblk5.addField("PLAN_MAT_REV","Money");   
      f.setCustomValidation("ITEM5_PM_NO,ITEM5_PM_REVISION", "PLAN_MAT_REV");
      f.setSize(14);
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONROUNDMATBUDCO3: Material");      
      f.setFunction("''");

      f = itemblk5.addField("PLAN_EXT_REV","Money");   
      f.setCustomValidation("ITEM5_PM_NO,ITEM5_PM_REVISION", "PLAN_EXT_REV");
      f.setSize(14);
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONROUNDEXTBUDCO3: External");      
      f.setFunction("''");

      f = itemblk5.addField("TOT_PLAN_REV","Money"); 
      f.setFunction("''");
      f.setSize(14);
      f.setReadOnly();
      f.setLabel("PCMWPMACTIONROUNDTOTAL3: Total");   

      itemblk5.setView("PM_ACTION_BUDGET");
      // 040120  ARWILK  Begin  (Enhance Performance)
      itemblk5.defineCommand("PM_ACTION_BUDGET_API","Modify__");
      // 040120  ARWILK  End  (Enhance Performance)
      itemset5 = itemblk5.getASPRowSet();
      itemblk5.setMasterBlock(headblk);

      itembar5 = mgr.newASPCommandBar(itemblk5);

      itembar5.defineCommand(itembar5.OKFIND,"okFindITEM5");   
      itembar5.defineCommand(itembar5.COUNTFIND,"countFindITEM5");   

      itemtbl5 = mgr.newASPTable(itemblk5);
      itemtbl5.setWrap();
      itemlay5 = itemblk5.getASPBlockLayout();
      itemlay5.setDefaultLayoutMode(itemlay5.SINGLE_LAYOUT);

      // -------------------------------------------------------------------------------------------------
      // -------------------------------------------------------------------------------------------------
      // -------------------------------------------------------------------------------------------------

      itemblk6 = mgr.newASPBlock("ITEM6");

      itemblk6.addField("ITEM6_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk6.addField("ITEM6_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      itemblk6.addField("ITEM6_PM_NO","Number").
      setDbName("PM_NO").
      setMandatory().
      setHidden();

      itemblk6.addField("ITEM6_PM_REVISION").
      setDbName("PM_REVISION").
      setMandatory().
      setHidden();

      itemblk6.addField("JOB_ID").
      setSize(20).
      setReadOnly().
      setInsertable().
      setLabel("PCMWPMACTROUITEM6JOB_ID: Job ID").
      setMaxLength(20);

      f = itemblk6.addField("STD_JOB_ID");
      f.setSize(20);
      f.setLOV("RoundStandardJobLov.page", 600, 445);
      f.setLabel("PCMWPMACTROUITEM6STDJOB_ID: Standard Job ID");
      f.setCustomValidation("STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION","STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION,ITEM6_DESCRIPTION,STD_JOB_STATUS");
      f.setMaxLength(12);
      f.setUpperCase();

      itemblk6.addField("STD_JOB_CONTRACT").
      setSize(20).
      setReadOnly().
      setLabel("PCMWPMACTROUITEM6STDJOB_CONTRACT: Site").
      setMaxLength(5).
      setUpperCase();

      f = itemblk6.addField("STD_JOB_REVISION");
      f.setSize(20);
      f.setDynamicLOV("ROUND_STANDARD_JOB_LOV", "STD_JOB_ID,STD_JOB_CONTRACT CONTRACT", 600, 445);
      f.setLabel("PCMWPMACTROUITEM6STDJOB_REV: Revision");
      f.setCustomValidation("STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION","ITEM6_DESCRIPTION,STD_JOB_STATUS");
      f.setMaxLength(6);   
      f.setUpperCase();

      itemblk6.addField("ITEM6_DESCRIPTION").
      setSize(70).
      setDbName("DESCRIPTION").
      setMandatory().
      setLabel("PCMWPMACTROUITEM6STDDESC: Description").
      setMaxLength(20);

      itemblk6.addField("QTY","Number").
      setSize(12).
      setLabel("PCMWPMACTROUITEM6QTY: Quantity");

      itemblk6.addField("ITEM6_COMPANY").
      setDbName("COMPANY").
      setHidden(); 

      itemblk6.addField("STD_JOB_STATUS").
      setLabel("PCMWPMACTROUITEM6STDJOBSTATUS: Std Job Status").
      setFunction("Standard_Job_API.Get_Status(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)").
      setReadOnly();

      itemblk6.addField("JOB_PROG_ID").
      setLabel("PCMWPMACTROUITEM6JOBPROGID: Job Program ID").
      setQueryable().
      setSize(20).
      setReadOnly();

      itemblk6.addField("JOB_PROG_REV").
      setLabel("PCMWPMACTROUITEM6JOBPROGREV: Job Program Revision").
      setQueryable().
      setSize(20).
      setReadOnly();

      itemblk6.addField("SIGN").
      setSize(40).
      setDbName("SIGNATURE").
      setLabel("PCMWPMACTROUITEM6_SIGN: Executed By").
      setLOV("../mscomw/MaintEmployeeLov.page","ITEM6_COMPANY COMPANY",600,450).
      setLOVProperty("TITLE",mgr.translate("PCMWPMACTIONROUNDSIGN: List of Signature")).
      setCustomValidation("SIGN,ITEM6_COMPANY","EMPLOYEE_ID,SIGN").
      setUpperCase().
      setMaxLength(20);   

      itemblk6.addField("EMPLOYEE_ID").
      setSize(40).
      setLabel("PCMWPMACTROUITEM6_SIGN_ID: Employee ID").    
      setMaxLength(20).
      setReadOnly();

      itemblk6.addField("ROLES_EXIST","Number").
      setFunction("''").
      setHidden();

      itemblk6.addField("MATERIAL_EXIST","Number").
      setFunction("''").
      setHidden();

      itemblk6.addField("TOOL_FACILITY_EXIST","Number").
      setFunction("''").
      setHidden();

      itemblk6.addField("DOC_EXIST","Number").
      setFunction("''").
      setHidden();

      itemblk6.addField("ITEM6_SIGNATURE").
      setFunction("''").
      setHidden(); 

      itemblk6.addField("ITEM6_TEMP").
      setFunction("''").
      setHidden();        

      itemblk6.addField("HAS_CONNECTIONS").
      setFunction("''").
      setCustomValidation("JOB_ID,PM_NO,PM_REVISION","HAS_CONNECTIONS").
      setHidden(); 

      itemblk6.setView("PM_ACTION_JOB");
      itemblk6.defineCommand("PM_ACTION_JOB_API","New__,Modify__,Remove__");
      itemblk6.setMasterBlock(headblk);
      itemset6 = itemblk6.getASPRowSet();

      itembar6 = mgr.newASPCommandBar(itemblk6);
      itembar6.defineCommand(itembar6.OKFIND,"okFindITEM6");
      itembar6.defineCommand(itembar6.COUNTFIND,"countFindITEM6");
      itembar6.defineCommand(itembar6.NEWROW,"newRowITEM6");
      itembar6.defineCommand(itembar6.DUPLICATEROW,"duplicateITEM6");
      itembar6.defineCommand(itembar6.DELETE,"deleteITEM6");   

      itembar6.defineCommand(itembar6.SAVENEW,null,"checkItem6Fields(-1)");
      itembar6.defineCommand(itembar6.SAVERETURN,null,"checkItem6Fields(-1);checkConnections()");   
      itembar6.enableCommand(itembar6.FIND);

      itemtbl6 = mgr.newASPTable(itemblk6);
      itemtbl6.setTitle(mgr.translate("PCMWPMACTROUITEM6ITM: Jobs"));
      itemtbl6.setWrap();

      itemlay6 = itemblk6.getASPBlockLayout();
      itemlay6.setDefaultLayoutMode(itemlay6.MULTIROW_LAYOUT);

      // -------------------------------------------------------------------------------------------------
      // -------------------------------------------------------------------------------------------------
      // -------------------------------------------------------------------------------------------------

      headblk1 = mgr.newASPBlock("HEAD1");
      headblk1.addField("WO_NO1");
      headblk1.addField("SNULL");
      headblk1.addField("GEN_ID1");
      headblk1.addField("PSDATE");
      headblk1.addField("PFDATE");
      headblk1.addField("GNTYPE");
      headblk1.addField("CONECTED2");
      headblk1.addField("GENERATE2");
   }

   public void getSalesPartCost()
   {
      ASPManager mgr = getASPManager();

      boolean invenPart = false;
      boolean purchPart = false;

      if (headset.countRows() > 0 && itemset0.countRows() > 0)
      {
         trans.clear();

         secBuff = mgr.newASPTransactionBuffer();

         secBuff.addSecurityQuery("INVENTORY_PART,PURCHASE_PART");

         secBuff = mgr.perform(secBuff);

         if (secBuff.getSecurityInfo().itemExists("INVENTORY_PART"))
            invenPart = true;
         if (secBuff.getSecurityInfo().itemExists("PURCHASE_PART"))
            purchPart = true;

         trans.clear();

         trans.addSecurityQuery("PURCHASE_ORDER_LINE_UTIL_API","Get_Primary_Supplier"); 
         ASPBuffer secBuff = trans.getSecurityInfo(); 
         double isInstalled = secBuff.itemExists("PURCHASE_PART_SUPPLIER_API.Get_Primary_Supplier_No")?1:0;

         itemset0.first();

         int n = itemset0.countRows(); 

         double salesPartCost;
         double     costVar;

         for (int i = 1;i <= n; i++)
         {
            String sparePart = itemset0.getRow().getValue("SPARE_CONTRACT");
            String partNo = itemset0.getRow().getValue("PART_NO");
            String serialNo = itemset0.getRow().getFieldValue("SERIAL_NO");
            String conditionCode = itemset0.getRow().getFieldValue("CONDITION_CODE");
            String configurationId = itemset0.getRow().getFieldValue("CONFIGURATION_IDS");
            salesPartCost = 0;
            costVar = 0;

            if (!mgr.isEmpty(partNo))
            {
               if (invenPart)
               {
                  trans.clear();

                  /*cmd = trans.addCustomFunction("INVENVALBYM","Inventory_Part_API.Get_Inventory_Value_By_Method","NCOST");
                  cmd.addParameter("SPARE_CONTRACT", sparePart);
                  cmd.addParameter("PART_NO", partNo); */

                  cmd = trans.addCustomCommand("INVENVALBYM","Active_Separate_API.Get_Inventory_Value");
                  cmd.addParameter("NCOST");
                  cmd.addParameter("SPARE_CONTRACT",sparePart);
                  cmd.addParameter("PART_NO",partNo);
                  cmd.addParameter("SERIAL_NO",serialNo);
                  cmd.addParameter("CONFIGURATION_IDS",mgr.isEmpty(mgr.readValue("CONFIGURATION_IDS"))?"*":mgr.readValue("CONFIGURATION_IDS"));
                  cmd.addParameter("CONDITION_CODE",conditionCode);

                  trans = mgr.perform(trans);    

                  salesPartCost = trans.getNumberValue("INVENVALBYM/DATA/NCOST");
               }
               else
               {
                  if (purchPart)
                  {
                     if (isInstalled==1)
                     {
                        trans.clear();

                        cmd = trans.addCustomFunction("PRIMSUPP","Purchase_Order_Line_Util_API.Get_Primary_Supplier","SSUPPLIER");
                        cmd.addParameter("SPARE_CONTRACT", sparePart);
                        cmd.addParameter("PART_NO", partNo);

                        trans = mgr.perform(trans);

                        String sSupplier = trans.getValue("PRIMSUPP/DATA/SSUPPLIER");

                        if (!mgr.isEmpty(sSupplier))
                        {
                           trans.clear();
                           cmd = trans.addCustomCommand("GTBASEPRICE","Purchase_Order_Line_Util_API.Get_Base_Price_Supp_Part");
                           cmd.addParameter("NCOST", "");
                           cmd.addParameter("PART_NO", partNo);
                           cmd.addParameter("SPARE_CONTRACT", sparePart);
                           cmd.addParameter("SSUPPLIER", sSupplier);
                           cmd.addParameter("QTY_PLAN", itemset0.getRow().getValue("QTY_PLAN"));
                           cmd.addParameter("TODAY", itemset0.getRow().getValue("TODAY"));

                           trans = mgr.perform(trans);

                           salesPartCost = trans.getNumberValue("GTBASEPRICE/DATA/NCOST");
                        }
                     }
                  }
               }

               double planQty = itemset0.getRow().getNumberValue("QTY_PLAN");

               if (isNaN(planQty))
                  planQty = 0;

               costVar = costVar + salesPartCost*planQty;

               r = itemset0.getRow();
               r.setNumberValue("COST", salesPartCost);

               itemset0.setRow(r);
               itemset0.next();
            }
         }
         itemset0.first();
      }
   }

   // 040120  ARWILK  Begin  (Enable Multirow RMB actions)
   private String createTransferUrl(String url, ASPBuffer object)
   {
      ASPManager mgr = getASPManager();

      try
      {
         String pkg = mgr.pack(object,1900 - url.length());
         char sep = url.indexOf('?')>0 ? '&' : '?';
         urlString = url + sep + "__TRANSFER=" + pkg ;
         return urlString;
      }
      catch (Throwable any)
      {
         return null;
      }
   }
   // 040120  ARWILK  End  (Enable Multirow RMB actions)

   // 031215  ARWILK  Begin  (Replace blocks with tabs)
   public void activateBudget()
   {
      tabs.setActiveTab(1);
   }

   public void activateJobs()
   {
      tabs.setActiveTab(2);
   }

   public void activateMaterials()
   {
      tabs.setActiveTab(3);
   }

   public void activateMaintenancePlan()
   {
      tabs.setActiveTab(4);
   }

   public void activatePermits()
   {
      tabs.setActiveTab(5);
   }

   public void activateCriteria()
   {
      tabs.setActiveTab(6);
   }

   public void activateHistory()
   {
      tabs.setActiveTab(7);
   }
   // 031215  ARWILK  End  (Replace blocks with tabs)

   public void adjust()
   {
      ASPManager mgr = getASPManager();

      // 031215  ARWILK  Begin  (Replace blocks with tabs)
      headbar.removeCustomCommand("activateBudget");
      headbar.removeCustomCommand("activateJobs");
      headbar.removeCustomCommand("activateMaterials");
      headbar.removeCustomCommand("activateMaintenancePlan");
      headbar.removeCustomCommand("activatePermits");
      headbar.removeCustomCommand("activateCriteria");
      headbar.removeCustomCommand("activateHistory");
      // 031215  ARWILK  End  (Replace blocks with tabs)

      if (itemlay4.isFindLayout())
         itembar4.disableCommand(itembar4.COUNTFIND);

      if (itemlay5.isVisible())
      {
         if (itemset5.countRows() == 0)
         {
            perBudg = "";
            matBudg = "";
            extBudg = "";
            totBudg = "";

            planPer = "";
            planMat = "";
            planExt = "";
            planTot = "";

            perCostLast = "";
            matCostLast = "";
            extCostLast = "";
            totCostLast = "";

            planPerRev = "";
            planMatRev = "";
            planExtRev = "";
            planTotRev = "";
         }
         else
         {
            double perBudgNF = itemset5.getNumberValue("PERS_BUDGET_COST");
            double matBudgNF = itemset5.getNumberValue("MAT_BUDGET_COST");
            double extBudgNF = itemset5.getNumberValue("EXT_BUDGET_COST");
            double totBudgNF = itemset5.getNumberValue("TOT_BUDGET_COST");

            perBudg = mgr.formatNumber("PERS_BUDGET_COST",(isNaN(perBudgNF)? 0:perBudgNF));
            matBudg = mgr.formatNumber("MAT_BUDGET_COST",(isNaN(matBudgNF)? 0:matBudgNF));
            extBudg = mgr.formatNumber("EXT_BUDGET_COST",(isNaN(extBudgNF)? 0:extBudgNF));
            totBudg = mgr.formatNumber("TOT_BUDGET_COST",(isNaN(totBudgNF)? 0:totBudgNF));

            double planPerNF = itemset5.getNumberValue("PLAN_PERS_COST");
            double planMatNF = itemset5.getNumberValue("PLAN_MAT_COST");
            double planExtNF = itemset5.getNumberValue("PLAN_EXT_COST");
            double planTotNF = itemset5.getNumberValue("TOT_PLAN_COST");

            planPer = mgr.formatNumber("PLAN_PERS_COST",(isNaN(planPerNF)? 0:planPerNF));
            planMat = mgr.formatNumber("PLAN_MAT_COST",(isNaN(planMatNF)? 0:planMatNF));
            planExt = mgr.formatNumber("PLAN_EXT_COST",(isNaN(planExtNF)? 0:planExtNF));
            planTot = mgr.formatNumber("TOT_PLAN_COST",(isNaN(planTotNF)? 0:planTotNF));

            double perCostLastNF = itemset5.getNumberValue("PERS_COST_LAST_WO");
            double matCostLastNF = itemset5.getNumberValue("MAT_COST_LAST_WO");
            double extCostLastNF = itemset5.getNumberValue("EXT_COST_LAST_WO");
            double totCostLastNF = itemset5.getNumberValue("TOT_COST_LAST_WO");

            perCostLast = mgr.formatNumber("PERS_COST_LAST_WO",(isNaN(perCostLastNF)? 0:perCostLastNF));
            matCostLast = mgr.formatNumber("MAT_COST_LAST_WO",(isNaN(matCostLastNF)? 0:matCostLastNF));
            extCostLast = mgr.formatNumber("EXT_COST_LAST_WO",(isNaN(extCostLastNF)? 0:extCostLastNF));
            totCostLast = mgr.formatNumber("TOT_COST_LAST_WO",(isNaN(totCostLastNF)? 0:totCostLastNF));

            double planPerRevNF = itemset5.getNumberValue("PLAN_PERS_REV");
            double planMatRevNF = itemset5.getNumberValue("PLAN_MAT_REV");
            double planExtRevNF = itemset5.getNumberValue("PLAN_EXT_REV");
            double planTotRevNF = itemset5.getNumberValue("TOT_PLAN_REV");

            planPerRev = mgr.formatNumber("PLAN_PERS_REV",(isNaN(planPerRevNF)? 0:planPerRevNF));
            planMatRev = mgr.formatNumber("PLAN_MAT_REV",(isNaN(planMatRevNF)? 0:planMatRevNF));
            planExtRev = mgr.formatNumber("PLAN_EXT_REV",(isNaN(planExtRevNF)? 0:planExtRevNF));
            planTotRev = mgr.formatNumber("TOT_PLAN_REV",(isNaN(planTotRevNF)? 0:planTotRevNF));
         }
      }

      fldTitleItem3TestPointId = mgr.translate("PCMWPMACTIONROUNDTESTPNTFLD: Testpoint");
      fldTitleParameterCode = mgr.translate("PCMWPMACTIONROUNDPARAMCODEFLD: Parameter");
      fldTitleReportInBy = mgr.translate("PCMWPMACTIONROUNDREPORINBYFLD: Reported+In+by");
      fldTitleItem1Signature = mgr.translate("PCMWPMACTIONROUNDITEM1SIGFLD: Signature");

      lovTitleItem3TestPointId = mgr.translate("PCMWPMACTIONROUNDTESTPNTLOV: List+of+Testpoint");
      lovTitleParameterCode = mgr.translate("PCMWPMACTIONROUNDPARAMCODELOV: List+of+Parameter");
      lovTitleReportInBy = mgr.translate("PCMWPMACTIONROUNDREPORINBYLOV: List+of+Reported+In+by");
      lovTitleItem1Signature = mgr.translate("PCMWPMACTIONROUNDITEM1SIGLOV: List+of+Signature");

      if (!secCheck)
      {
         actionSecurityCheck();                                                                           
         secCheck = true;                                                                                 
      }

      if (mgr.isPresentationObjectInstalled("docmaw/DocReference.page"))
      {
         if (!ctxHypeHasDoc)
            mgr.getASPField("CBHASDOCUMENTS").setHyperlink("../docmaw/DocReference.page","LU_NAME,KEY_REF","NEWWIN");
      }
      if (mgr.isPresentationObjectInstalled("invenw/inventoryPart.page"))
      {
         if (!ctxHypePart)
            mgr.getASPField("PART_NO").setHyperlink("../invenw/inventoryPart.page","PART_NO","NEWWIN");
      }

      if (headlay.isSingleLayout() && headset.countRows() > 0)
      {
         if ("TRUE".equals(headset.getValue("PM_CAN_BE_MODIFIED")))
         {
            itembar0.enableCommand(itembar0.NEWROW);
            //itembar1.enableCommand(itembar1.NEWROW);
            itembar2.enableCommand(itembar2.NEWROW);
            itembar3.enableCommand(itembar3.NEWROW);
            itembar4.enableCommand(itembar4.NEWROW);
            itembar5.enableCommand(itembar5.NEWROW);
            itembar6.enableCommand(itembar6.NEWROW);

            itembar0.enableCommand(itembar0.DUPLICATEROW);
            //itembar1.enableCommand(itembar1.DUPLICATEROW);
            itembar2.enableCommand(itembar2.DUPLICATEROW);
            itembar3.enableCommand(itembar3.DUPLICATEROW);
            itembar4.enableCommand(itembar4.DUPLICATEROW);
            itembar5.enableCommand(itembar5.DUPLICATEROW);
            itembar6.enableCommand(itembar6.DUPLICATEROW);

            itembar0.enableCommand(itembar0.DELETE);
            //itembar1.enableCommand(itembar1.DELETE);
            itembar2.enableCommand(itembar2.DELETE);
            itembar3.enableCommand(itembar3.DELETE);
            itembar4.enableCommand(itembar4.DELETE);
            itembar5.enableCommand(itembar5.DELETE);
            itembar6.enableCommand(itembar6.DELETE);

            itembar0.enableCommand(itembar0.EDITROW);
            //itembar1.enableCommand(itembar1.EDITROW);
            itembar2.enableCommand(itembar2.EDITROW);
            itembar3.enableCommand(itembar3.EDITROW);
            itembar4.enableCommand(itembar4.EDITROW);
            itembar5.enableCommand(itembar5.EDITROW);
            itembar6.enableCommand(itembar6.EDITROW);
         }
         else
         {
            itembar0.disableCommand(itembar0.NEWROW);
            //itembar1.disableCommand(itembar1.NEWROW);
            itembar2.disableCommand(itembar2.NEWROW);
            itembar3.disableCommand(itembar3.NEWROW);
            itembar4.disableCommand(itembar4.NEWROW);
            itembar5.disableCommand(itembar5.NEWROW);
            itembar6.disableCommand(itembar6.NEWROW);

            itembar0.disableCommand(itembar0.DUPLICATEROW);
            //itembar1.disableCommand(itembar1.DUPLICATEROW);
            itembar2.disableCommand(itembar2.DUPLICATEROW);
            itembar3.disableCommand(itembar3.DUPLICATEROW);
            itembar4.disableCommand(itembar4.DUPLICATEROW);
            itembar5.disableCommand(itembar5.DUPLICATEROW);
            itembar6.disableCommand(itembar6.DUPLICATEROW);

            itembar0.disableCommand(itembar0.DELETE);
            //itembar1.disableCommand(itembar1.DELETE);
            itembar2.disableCommand(itembar2.DELETE);
            itembar3.disableCommand(itembar3.DELETE);
            itembar4.disableCommand(itembar4.DELETE);
            itembar5.disableCommand(itembar5.DELETE);
            itembar6.disableCommand(itembar6.DELETE);

            itembar0.disableCommand(itembar0.EDITROW);
            //itembar1.disableCommand(itembar1.EDITROW);
            itembar2.disableCommand(itembar2.EDITROW);
            itembar3.disableCommand(itembar3.EDITROW);
            itembar4.disableCommand(itembar4.EDITROW);
            itembar5.disableCommand(itembar5.EDITROW);
            itembar6.disableCommand(itembar6.EDITROW);
         }
      }

      disableCommands();    

      if (itemset6.countRows()>0 && itemlay6.isVisible())
      {
         String sWhereStrForITEM6 = "CONTRACT = '" + headset.getValue("CONTRACT") + "'";

         if (itemlay6.isFindLayout())
         {
            mgr.getASPField("STD_JOB_ID").setLOV("StandardJobLov1.page", 600, 450);
            sWhereStrForITEM6 = sWhereStrForITEM6 + " AND STANDARD_JOB_TYPE_DB = '2'";
         }

         mgr.getASPField("STD_JOB_ID").setLOVProperty("WHERE", sWhereStrForITEM6);
      }

      if (itemset0.countRows()>0 && itemlay0.isVisible())
      {
         mgr.getASPField("SPARE_CONTRACT").setLOVProperty("WHERE","Site_API.Get_Company(CONTRACT) = Site_API.Get_Company('"+headset.getRow().getValue("ORG_CONTRACT")+"')");
      }

      if (headlay.isFindLayout())
      {
         mgr.getASPField("MCH_CODE").setLOV("MaintenanceObjectLov.page","HEAD_CONTRACT CONTRACT",600,450);
      }
      else
      {
	 //Bug 87935, Start, Modified the code to get correct pres objects
         mgr.getASPField("MCH_CODE").setLOV("MaintenanceObjectLov1.page","HEAD_CONTRACT CONTRACT",600,450);
         mgr.getASPField("MCH_CODE").setLOVProperty("WHERE","(OBJ_LEVEL IS NULL OR (OBJ_LEVEL IS NOT NULL AND Equipment_Object_Level_API.Get_Create_Pm(OBJ_LEVEL) = 'TRUE'))");
	 //Bug 87935, End
      }

      if ( itemlay0.isNewLayout() || itemlay0.isEditLayout() || itemlay1.isNewLayout() || itemlay1.isEditLayout() || 
           itemlay2.isNewLayout() || itemlay2.isEditLayout() || itemlay3.isNewLayout() || itemlay3.isEditLayout() ||
           itemlay5.isEditLayout()|| itemlay6.isNewLayout()  || itemlay6.isEditLayout() )
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

   public void actionSecurityCheck()                                                                                                                                                                                               
   {
      ASPManager mgr = getASPManager();                                                                                                                                                                                           

      trans = mgr.newASPTransactionBuffer();                                                                                                                                                                                      
      trans.addSecurityQuery("PM_ACTION,MAINTENANCE_OBJECT,EQUIPMENT_SPARE_STRUC_DISTINCT,INVENTORY_PART_CONFIG,INVENTORY_PART,PURCHASE_PART_SUPPLIER,ACTIVE_ROUND,HISTORICAL_ROUND,PERMIT_TYPE,EQUIPMENT_OBJECT,MAINTENANCE_OBJECT");
      trans.addSecurityQuery("PURCHASE_PART_SUPPLIER_API","Get_Primary_Supplier_No");                                                                                                                                                        
      trans.addPresentationObjectQuery("PCMW/Replacements.page,PCMW/CopyPmActions.page,PCMW/CreatePmRevisionDlg.page,PCMW/MaintenanceObject.page,PCMW/RMBEquipmentSpareStructure.page,INVENW/InventoryPartInventoryPartCurrentOnHand.page,INVENW/InventoryPartAvailabilityPlanningQry.page,INVENW/InventoryPart.page,PURCHW/PurchasePartSupplier.page,PCMW/ActiveRound.page,PCMW/HistoricalRound.page,PCMW/ViewGenerationDlg.page,PCMW/PermitTypeRMB.page,EQUIPW/RMBEquipmentObject.page,EQUIPW/RMBMaintenanceObject.page,DOCMAW/DocReference.page");

      trans = mgr.perform(trans);                                                                                                                                                                                                 
      ASPBuffer secBuff = trans.getSecurityInfo();                                                                                                                                                                                

      if (secBuff.itemExists("PM_ACTION") && secBuff.namedItemExists("PCMW/Replacements.page"))
         ctxReplacements = true;
      if (secBuff.namedItemExists("PCMW/CopyPmActions.page"))
         ctxCopyPmActions = true;
      if (secBuff.namedItemExists("PCMW/CreatePmRevisionDlg.page"))
         ctxCreatePmRev = true;
      if (secBuff.itemExists("MAINTENANCE_OBJECT") && secBuff.namedItemExists("PCMW/MaintenanceObject.page"))
         ctxSparePartsinObject = true;
      if (secBuff.itemExists("EQUIPMENT_SPARE_STRUC_DISTINCT") && secBuff.namedItemExists("PCMW/RMBEquipmentSpareStructure.page"))
         ctxSpareParts = true;
      if (secBuff.itemExists("INVENTORY_PART") && secBuff.namedItemExists("INVENW/InventoryPartInventoryPartCurrentOnHand.page"))
         ctxCurrQuntity = true;
      if (secBuff.itemExists("INVENTORY_PART_CONFIG") && secBuff.namedItemExists("INVENW/InventoryPartAvailabilityPlanningQry.page"))
         ctxAvailDetail = true;
      if (secBuff.itemExists("INVENTORY_PART") && secBuff.namedItemExists("INVENW/InventoryPart.page"))
         ctxInventoryPart = true;
      if (secBuff.itemExists("PURCHASE_PART_SUPPLIER") && secBuff.namedItemExists("PURCHW/PurchasePartSupplier.page") && secBuff.itemExists("PURCHASE_PART_SUPPLIER_API.Get_Primary_Supplier_No"))
         ctxSupplierPerPart = true;
      if (secBuff.itemExists("ACTIVE_ROUND") && secBuff.namedItemExists("PCMW/ActiveRound.page"))
         ctxReportInRouteWO = true;
      if (secBuff.itemExists("HISTORICAL_ROUND") && secBuff.namedItemExists("PCMW/HistoricalRound.page"))
         ctxViewHistoricalWO = true;
      if (secBuff.namedItemExists("PCMW/ViewGenerationDlg.page"))
         ctxViewGenerationDetails = true;
      if (secBuff.itemExists("PERMIT_TYPE") && secBuff.namedItemExists("PCMW/PermitTypeRMB.page"))
         ctxAttributes = true;
      if (secBuff.itemExists("EQUIPMENT_OBJECT") && secBuff.namedItemExists("EQUIPW/RMBEquipmentObject.page"))
         ctxParameters = true;
      if (secBuff.itemExists("MAINTENANCE_OBJECT") && secBuff.namedItemExists("EQUIPW/RMBMaintenanceObject.page"))
         ctxMeasurements = true;
      if (secBuff.namedItemExists("DOCMAW/DocReference.page"))
         ctxHypeHasDoc = true;
      if (secBuff.namedItemExists("INVENW/InventoryPart.page"))
         ctxHypePart = true;
   }                                                                                                                                                                                                                               

   public void disableCommands()                                                                                                                                                                                                   
   {
      ASPManager mgr = getASPManager();                                                                                                                                                                                           

      if (!ctxReplacements)
         headbar.disableCustomCommand("replacements");
      if (!ctxCopyPmActions)
         headbar.disableCustomCommand("copyPmActions");
      if (!ctxCreatePmRev)
         headbar.disableCustomCommand("createNewRevision");
      if (!ctxSpareParts)
         itembar0.disableCustomCommand("spareParts");

      if (!ctxReportInRouteWO)
      {
         itembar1.disableCustomCommand("reportInRouteWO");
      }
      if (!ctxViewHistoricalWO)
         itembar1.disableCustomCommand("viewHistoricalWO");
      if (!ctxViewGenerationDetails)
         itembar1.disableCustomCommand("viewGenerationDetails");

      if (!ctxAttributes)
         itembar2.disableCustomCommand("attributes");

      if (!ctxParameters)
         itembar3.disableCustomCommand("parameters");
      if (!ctxMeasurements)
         itembar3.disableCustomCommand("measurements");

      if (mgr.isPresentationObjectInstalled("invenw/InventoryPartInventoryPartCurrentOnHand.page"))
      {
         if (!ctxCurrQuntity)
            itembar0.disableCustomCommand("currQuntity");
      }
      if (mgr.isPresentationObjectInstalled("invenw/InventoryPartAvailabilityPlanningQry.page"))
      {
         if (!ctxAvailDetail)
            itembar0.disableCustomCommand("availDetail");
      }
      if (mgr.isPresentationObjectInstalled("invenw/InventoryPart.page"))
      {
         if (!ctxInventoryPart)
            itembar0.disableCustomCommand("inventoryPart");
      }
      if (mgr.isPresentationObjectInstalled("purchw/PurchasePartSupplier.page"))
      {
         if (!ctxSupplierPerPart)
            itembar0.disableCustomCommand("supplierPerPart");
      }

      // 031215  ARWILK  Begin  (Links with RMB's)
      if (!ctxSparePartsinObject)
         itembar0.disableCustomCommand("sparePartsinObject");
      // 031215  ARWILK  End  (Links with RMB's)

      if (headset.countRows()>0)
      {
         if (itemlay6.isEditLayout())
         {
            int currrowItem = itemset6.getCurrentRowNo();

            if (checkforConnections(currrowItem))
               checkconn = true;
            else
               checkconn = false;
         }
      }
   }

//===============================================================
//                         HTML
//===============================================================

   protected String getDescription()
   {
      return "PCMWPMACTIONROUNDTITLE: Route PM Action";
   }

   protected String getTitle()
   {
      return "PCMWPMACTIONROUNDTITLE: Route PM Action";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      printHiddenField("HIDDENPARTNO","");
      printHiddenField("ONCEGIVENERROR","FALSE");
      printHiddenField("GROUP_ID","");

      appendToHTML(headlay.show());

      if (headlay.isSingleLayout() && (headset.countRows() > 0))
      {
         appendToHTML(tabs.showTabsInit());

         if (tabs.getActiveTab() == 1)
         {
            appendToHTML(itembar5.showBar());

            if (!itemlay5.isEditLayout())
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
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONROUNDBUDGCOST: Budget Cost"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80 align=\"middle\">");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONROUNDPLANCOST: Planned Cost"));
               appendToHTML("		</td>\n");
               appendToHTML("		<!-- <td width=80 align=\"middle\">");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONROUNDACTCOST: Actual Cost"));
               appendToHTML("		</td> -->\n");
               appendToHTML("		<td width=100 align=\"middle\">");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONROUNDPLANREV: Planned Revenue"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>	\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=0>");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONROUNDPERSONAL: Personal"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(perBudg));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(planPer));
               appendToHTML("		</td>\n");
               appendToHTML("		<!-- <td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(perCostLast));
               appendToHTML("		</td> -->\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(planPerRev));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>				\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=0>");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONROUNDMATERIAL: Material"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(matBudg));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(planMat));
               appendToHTML("		</td>\n");
               appendToHTML("		<!-- <td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(matCostLast));
               appendToHTML("		</td> -->\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(planMatRev));
               appendToHTML("		</td> \n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>				\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=0>");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONROUNDEXTERNAL: External"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(extBudg));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(planExt));
               appendToHTML("		</td>\n");
               appendToHTML("		<!-- <td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(extCostLast));
               appendToHTML("		</td> -->\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(planExtRev));
               appendToHTML("		</td> \n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>				\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=0>");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONROUNDTOTAL: Total"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(totBudg));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(planTot));
               appendToHTML("		</td>\n");
               appendToHTML("		<!-- <td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadValue(totCostLast));
               appendToHTML("		</td> -->\n");
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
               appendToHTML(itemblk5.generateHiddenFields()); //XSS_Safe AMNILK 20070725
               appendToHTML("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">");
               appendToHTML("<tr>");
               appendToHTML("<td>");
               appendToHTML("<table cellspacing=0 cellpadding=0 border=0 width=100%><tr><td>&nbsp;&nbsp;</td><td width=100%>");
               appendToHTML("<table id=\"cntITEM6\" cellpadding=2 width=0 border=\"0\" class=\"BlockLayoutTable\">\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80 align=\"middle\">");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONROUNDBUDGCOST: Budget Cost"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80 align=\"middle\">");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONROUNDPLANCOST: Planned Cost"));
               appendToHTML("		</td>\n");
               appendToHTML("		<!--<td width=80 align=\"middle\">");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONROUNDACTCOST: Actual Cost"));
               appendToHTML("		</td>-->\n");
               appendToHTML("		<td width=100 align=\"middle\">");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONROUNDPLANREV: Planned Revenue"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>	\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=0>");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONROUNDPERSONAL: Personal"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"left\">");
               appendToHTML(fmt.drawTextField("PERS_BUDGET_COST",perBudg,"OnChange=validatePersBudgetCost(0)",15));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("PLAN_PERS_COST",planPer,"ReadOnly",18));
               appendToHTML("		</td>\n");
               appendToHTML("		<!--<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("PERS_COST_LAST_WO",perCostLast,"ReadOnly",18));
               appendToHTML("		</td>-->\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("PLAN_PERS_REV",planPerRev,"ReadOnly",18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>	\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=0>");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONROUNDMATERIAL: Material"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"left\">");
               appendToHTML(fmt.drawTextField("MAT_BUDGET_COST",matBudg,"OnChange=validateMatBudgetCost(0)",15));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("PLAN_MAT_COST",planMat,"ReadOnly",18));
               appendToHTML("		</td>\n");
               appendToHTML("		<!--<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("MAT_COST_LAST_WO",matCostLast,"ReadOnly",18));
               appendToHTML("		</td>-->\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("PLAN_MAT_REV",planMatRev,"ReadOnly",18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>				\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=0>");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONROUNDEXTERNAL: External"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"left\">");
               appendToHTML(fmt.drawTextField("EXT_BUDGET_COST",extBudg,"OnChange=validateExtBudgetCost(0)",15));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("PLAN_EXT_COST",planExt,"ReadOnly",18));
               appendToHTML("		</td>\n");
               appendToHTML("		<!--<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("EXT_COST_LAST_WO",extCostLast,"ReadOnly",18));
               appendToHTML("		</td>-->\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("PLAN_EXT_REV",planExtRev,"ReadOnly",18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=80>\n");
               appendToHTML("		</td>\n");
               appendToHTML("	</tr>				\n");
               appendToHTML("	<tr>\n");
               appendToHTML("		<td width=0>");
               appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONROUNDTOTAL: Total"));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("TOT_BUDGET_COST",totBudg,"",18));
               appendToHTML("		</td>\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("PLAN_TOT_COST",planTot,"ReadOnly",18));
               appendToHTML("		</td>\n");
               appendToHTML("		<!--<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("TOT_COST_LAST_WO",totCostLast,"ReadOnly",18));
               appendToHTML("		</td>-->\n");
               appendToHTML("		<td width=0 align=\"right\">");
               appendToHTML(fmt.drawReadOnlyTextField("PLAN_TOT_REV",planTotRev,"ReadOnly",18));
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
            appendToHTML(itemlay6.show());

            if (itemlay6.isEditLayout())
            {
               appendDirtyJavaScript("var sStdJobPrev = document.form.STD_JOB_ID.value;\n");
               appendDirtyJavaScript("var sStdJobRevPrev = document.form.STD_JOB_REVISION.value;\n");
               appendDirtyJavaScript("var sStdJobSitePrev = document.form.STD_JOB_CONTRACT.value;\n");
            }

            appendDirtyJavaScript("function checkConnections()\n");
            appendDirtyJavaScript("{\n");

            if (itemlay6.isEditLayout())
            {
               if (checkconn)
               {
                  appendDirtyJavaScript(" var sStdJob1 = document.form.STD_JOB_ID.value; \n");
                  appendDirtyJavaScript(" if (sStdJobPrev != sStdJob1) \n");
                  appendDirtyJavaScript(" { \n");
                  appendDirtyJavaScript("    alert(\"");
                  appendDirtyJavaScript(mgr.translateJavaScript("PMACTCONNDISCONNWARNINGRND1: All Materials and Documents connected to this PM Action through Standard Job "));
                  appendDirtyJavaScript(" : \" + sStdJobPrev \n+ \"");
                  appendDirtyJavaScript(mgr.translateJavaScript("PMACTCONNDISCONNWARNINGRND2:  - Site "));
                  appendDirtyJavaScript(" : \" + sStdJobSitePrev \n+ \"");
                  appendDirtyJavaScript(mgr.translateJavaScript("PMACTCONNDISCONNWARNINGRND3:  - Revision "));
                  appendDirtyJavaScript(" : \" + sStdJobRevPrev \n+ \"");
                  appendDirtyJavaScript(mgr.translateJavaScript("PMACTCONNDISCONNWARNINGRND4: , will be removed. "));
                  appendDirtyJavaScript(" \" ); \n");
                  appendDirtyJavaScript(" } \n");
               }
            }
            appendDirtyJavaScript("return true;\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function validateStdJobId(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
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
            appendDirtyJavaScript("	window.status='"); 
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTRNDPLVAL: Please wait for validation"));
            appendDirtyJavaScript("';\n");
            appendDirtyJavaScript("	 r = __connect(\n");
            appendDirtyJavaScript("		'../pcmw/PmActionRound.page?VALIDATE=STD_JOB_ID'\n");
            appendDirtyJavaScript("		+ '&STD_JOB_ID=' + URLClientEncode(getValue_('STD_JOB_ID',i))\n");
            appendDirtyJavaScript("		+ '&STD_JOB_CONTRACT=' + URLClientEncode(getValue_('STD_JOB_CONTRACT',i))\n");
            appendDirtyJavaScript("		+ '&STD_JOB_REVISION=' + URLClientEncode(getValue_('STD_JOB_REVISION',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	window.status='';\n\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'STD_JOB_ID',i,'"); 
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTROUITEM6STDJOB_ID: Standard Job ID")); 
            appendDirtyJavaScript("') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('STD_JOB_ID',i,0);\n");
            appendDirtyJavaScript("		assignValue_('STD_JOB_CONTRACT',i,1);\n");
            appendDirtyJavaScript("		assignValue_('STD_JOB_REVISION',i,2);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_DESCRIPTION',i,3);\n");
            appendDirtyJavaScript("		assignValue_('STD_JOB_STATUS',i,4);\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function validateStdJobRevision(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkStdJobRevision(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('STD_JOB_ID',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('STD_JOB_CONTRACT',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('STD_JOB_REVISION',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('STD_JOB_REVISION',i)=='' )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		return;\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	window.status='"); 
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTRNDPLVAL: Please wait for validation"));
            appendDirtyJavaScript("';\n");
            appendDirtyJavaScript("	 r = __connect(\n");
            appendDirtyJavaScript("		'../pcmw/PmActionRound.page?VALIDATE=STD_JOB_REVISION'\n");
            appendDirtyJavaScript("		+ '&STD_JOB_ID=' + URLClientEncode(getValue_('STD_JOB_ID',i))\n");
            appendDirtyJavaScript("		+ '&STD_JOB_CONTRACT=' + URLClientEncode(getValue_('STD_JOB_CONTRACT',i))\n");
            appendDirtyJavaScript("	        + '&STD_JOB_REVISION=' + URLClientEncode(getValue_('STD_JOB_REVISION',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	window.status='';\n\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'STD_JOB_REVISION',i,'"); 
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTROUITEM6STDJOB_REV: Revision"));
            appendDirtyJavaScript("') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_DESCRIPTION',i,0);\n");
            appendDirtyJavaScript("		assignValue_('STD_JOB_STATUS',i,1);\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");
         }
         else if (tabs.getActiveTab() == 3)
            appendToHTML(itemlay0.show());
         else if (tabs.getActiveTab() == 4)
            appendToHTML(itemlay1.show());
         else if (tabs.getActiveTab() == 5)
            appendToHTML(itemlay2.show());
         else if (tabs.getActiveTab() == 6)
            appendToHTML(itemlay3.show());
         else if (tabs.getActiveTab() == 7)
            appendToHTML(itemlay4.show());
      }

      appendDirtyJavaScript("function hypeAgreement()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  sHypeLinkAgreementId = document.form.AGREEMENT_ID.value;\n");
      appendDirtyJavaScript("  if (sHypeLinkAgreementId == '')\n");
      appendDirtyJavaScript("     window.open(\"../orderw/CustomerAgreement.page\",\"hypeRouteWin\",\"scrollbars,status,resizable=yes,height=445,width=600\");\n");
      appendDirtyJavaScript("  else\n");
      appendDirtyJavaScript("     window.open(\"../orderw/CustomerAgreement.page?AGREEMENT_ID=\"+URLClientEncode(sHypeLinkAgreementId),\"hypeTestPntWin\",\"scrollbars,status,resizable=yes,height=445,width=600\");\n");
      appendDirtyJavaScript("}\n"); 

      appendDirtyJavaScript("function HypDoc()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   sPMNoVar = document.form.PM_NO.value;\n");
      appendDirtyJavaScript("   sPMRevVar = document.form.PM_REVISION.value;\n");
      appendDirtyJavaScript("   if (sPMNoVar =='' && sPMRevVar=='')\n");
      appendDirtyJavaScript("      window.open(\"../docmaw/DocReference.page\",\"hypeWindow\",\"scrollbars,resizable,status=yes,width=600,height=445\");\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("      window.open(\"../docmaw/DocReference.page?LU_NAME=PmAction&KEY_REF=PM_NO=\"+URLClientEncode(sPMNoVar)+\"^\"+\"PM_REVISION=\"+URLClientEncode(sPMRevVar)+\"^\",\"hypeWindow\",\"scrollbars,resizable,status=yes,width=600,height=445\");  \n");
      appendDirtyJavaScript("}\n"); 

      appendDirtyJavaScript("function lovItem3TestPointId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    if( getRowStatus_('ITEM3',i)=='QueryMode__')\n");
      appendDirtyJavaScript("   	openLOVWindow('ITEM3_TEST_POINT_ID',i, \n");
      appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EQUIPMENT_OBJECT_TEST_PNT&__FIELD=");
      appendDirtyJavaScript(fldTitleItem3TestPointId);
      appendDirtyJavaScript("&__TITLE=");
      appendDirtyJavaScript(lovTitleItem3TestPointId);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("		,600,450,'validateItem3TestPointId');\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("	   openLOVWindow('ITEM3_TEST_POINT_ID',i,\n");
      appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EQUIPMENT_OBJECT_TEST_PNT&__FIELD=");
      appendDirtyJavaScript(fldTitleItem3TestPointId);
      appendDirtyJavaScript("&__TITLE=");
      appendDirtyJavaScript(lovTitleItem3TestPointId);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("		+ '&HEAD_CONTRACT=' + URLClientEncode(getValue_('ITEM3_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&MCH_CODE=' + URLClientEncode(getValue_('ITEM3_MCH_CODE',i))\n");
      appendDirtyJavaScript("		,600,450,'validateItem3TestPointId');\n");
      appendDirtyJavaScript("}\n"); 

      appendDirtyJavaScript("function lovParameterCode(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    if( getRowStatus_('ITEM3',i)=='QueryMode__')\n");
      appendDirtyJavaScript("   	openLOVWindow('PARAMETER_CODE',i,\n");
      appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EQUIPMENT_OBJECT_PARAM&__FIELD=");
      appendDirtyJavaScript(fldTitleParameterCode);
      appendDirtyJavaScript("&__TITLE=");
      appendDirtyJavaScript(lovTitleParameterCode);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("		,600,450,'validateParameterCode');\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("	   openLOVWindow('PARAMETER_CODE',i,	 \n");
      appendDirtyJavaScript("	 	'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EQUIPMENT_OBJECT_PARAM&__FIELD=");
      appendDirtyJavaScript(fldTitleParameterCode);
      appendDirtyJavaScript("&__TITLE=");
      appendDirtyJavaScript(lovTitleParameterCode);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('ITEM3_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&MCH_CODE=' + URLClientEncode(getValue_('ITEM3_MCH_CODE',i))\n");
      appendDirtyJavaScript("		+ '&TEST_POINT_ID=' + URLClientEncode(getValue_('ITEM3_TEST_POINT_ID',i))\n");
      appendDirtyJavaScript("		,600,450,'validateParameterCode');	\n");
      appendDirtyJavaScript("}\n"); 

      appendDirtyJavaScript("function lovReportInBy(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    if( getRowStatus_('ITEM4',i)=='QueryMode__')\n");
      appendDirtyJavaScript("   	openLOVWindow('REPORT_IN_BY',i,\n");
      appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
      appendDirtyJavaScript(fldTitleReportInBy);
      appendDirtyJavaScript("&__TITLE=");
      appendDirtyJavaScript(lovTitleReportInBy);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("		,600,450,'validateReportInBy');\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("	 	openLOVWindow('REPORT_IN_BY',i,\n");
      appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
      appendDirtyJavaScript(fldTitleReportInBy);
      appendDirtyJavaScript("&__TITLE=");
      appendDirtyJavaScript(lovTitleReportInBy);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
      appendDirtyJavaScript("		,600,450,'validateReportInBy');	\n");
      appendDirtyJavaScript("}\n"); 

      appendDirtyJavaScript("function lovItem1Signature(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    if( getRowStatus_('ITEM1',i)=='QueryMode__')\n");
      appendDirtyJavaScript("   	openLOVWindow('ITEM1_SIGNATURE',i,\n");
      appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
      appendDirtyJavaScript(fldTitleItem1Signature);
      appendDirtyJavaScript("&__TITLE=");
      appendDirtyJavaScript(lovTitleItem1Signature);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("		,600,450,'validateItem1Signature');\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("	   openLOVWindow('ITEM1_SIGNATURE',i,\n");
      appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
      appendDirtyJavaScript(fldTitleItem1Signature);
      appendDirtyJavaScript("&__TITLE=");
      appendDirtyJavaScript(lovTitleItem1Signature);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
      appendDirtyJavaScript("		,600,450,'validateItem1Signature');\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("window.name = \"PmActionRound\";\n");  

      appendDirtyJavaScript("function LovPartNoPurch(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("        lovUrl = '");
      appendDirtyJavaScript(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT"));
      appendDirtyJavaScript("'; \n");
      appendDirtyJavaScript("	openLOVWindow('PART_NO',i,\n");
      appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PURCHASE_PART_LOV&__FIELD=Part+No&__TITLE=List+of+Part+No'\n");
      appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))\n");
      appendDirtyJavaScript("		,600,450,'validatePartNo');\n");
      appendDirtyJavaScript("}\n"); 

      appendDirtyJavaScript("function validatePartNo(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("f.HIDDENPARTNO.value = \"\";\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkPartNo(i) ) return;\n");
      appendDirtyJavaScript("	window.status='");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTRNDPLVAL: Please wait for validation"));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=PART_NO'\n");
      appendDirtyJavaScript("		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
      appendDirtyJavaScript("		+ '&SPARE_CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM0_CONTRACT=' + URLClientEncode(getValue_('ITEM0_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&QTY_PLAN=' + URLClientEncode(getValue_('QTY_PLAN',i))\n");      
      appendDirtyJavaScript("		+ '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");      
      appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n"); 
      appendDirtyJavaScript("		+ '&OWNER=' + URLClientEncode(getValue_('OWNER',i))\n");
      appendDirtyJavaScript("		+ '&CONDITION_CODE=' + URLClientEncode(getValue_('CONDITION_CODE',i))\n");      
      appendDirtyJavaScript("		+ '&PART_OWNERSHIP=' + URLClientEncode(getValue_('PART_OWNERSHIP',i))\n");      
      appendDirtyJavaScript("		+ '&ITEM0_PM_NO=' + URLClientEncode(getValue_('ITEM0_PM_NO',i))\n");      
      appendDirtyJavaScript("		+ '&ITEM0_PM_REVISION=' + URLClientEncode(getValue_('ITEM0_PM_REVISION',i))\n");      
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'PART_NO',i,'");
      appendDirtyJavaScript(mgr.translateJavaScript("PMACTRNDPARTNO: Part No"));
      appendDirtyJavaScript("') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('SPAREDESCRIPTION',i,0);\n");            
      appendDirtyJavaScript("		assignValue_('INVENTORYFLAG',i,1);\n");
      appendDirtyJavaScript("		assignValue_('DIMQUALITY',i,2);\n");
      appendDirtyJavaScript("		assignValue_('TYPEDESIGNATION',i,3);\n");
      appendDirtyJavaScript("		assignValue_('QTYONHAND',i,4);\n");
      appendDirtyJavaScript("		assignValue_('UNITMEAS',i,5);\n");
      appendDirtyJavaScript("		assignValue_('CATALOG_NO',i,6);\n");
      appendDirtyJavaScript("		assignValue_('SALESPARTDESCRIPTION',i,7);\n");
      appendDirtyJavaScript("		assignValue_('LISTPRICE',i,8);\n");
      appendDirtyJavaScript("		assignValue_('COST',i,9);\n");
      appendDirtyJavaScript("		assignValue_('CONDITION_CODE',i,10);\n"); 
      appendDirtyJavaScript("		assignValue_('ACTIVEIND_DB',i,11);\n"); 
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("else{\n");
      appendDirtyJavaScript("f.HIDDENPARTNO.value = f.PART_NO.value;\n");
      appendDirtyJavaScript("f.ONCEGIVENERROR.value = \"TRUE\";\n");
      appendDirtyJavaScript("getField_('PART_NO',i).value = '';\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("   if (f.ACTIVEIND_DB.value == 'N') \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWPMACTIONROUNDINVSALESPART: All sale parts connected to the part are inactive."));
      appendDirtyJavaScript("          ');\n");
      appendDirtyJavaScript("      f.CATALOG_NO.value = ''; \n");
      appendDirtyJavaScript("      f.SALESPARTDESCRIPTION.value = ''; \n");   
      appendDirtyJavaScript("      f.LISTPRICE.value = ''; \n");   
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("}\n");

      /*appendDirtyJavaScript("function checkPartVal()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" if(f.ONCEGIVENERROR.value == \"TRUE\")");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("if(f.HIDDENPARTNO.value == f.PART_NO.value.toUpperCase())");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("        validatePartNo(-1);\n");  
      appendDirtyJavaScript("}\n"); 
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("}\n");*/

      appendDirtyJavaScript("function validatePartOwnership(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkPartOwnership(i) ) return;\n");
      appendDirtyJavaScript(" if( getValue_('PART_OWNERSHIP',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("	window.status='"); 
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTRNDPLVAL: Please wait for validation"));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=PART_OWNERSHIP'\n");
      appendDirtyJavaScript("		+ '&PART_OWNERSHIP=' + SelectURLClientEncode('PART_OWNERSHIP',i)		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
      appendDirtyJavaScript("		+ '&SPARE_CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&OWNER=' + URLClientEncode(getValue_('OWNER',i))\n");
      appendDirtyJavaScript("		+ '&CONDITION_CODE=' + URLClientEncode(getValue_('CONDITION_CODE',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'PART_OWNERSHIP',i,'"); 
      appendDirtyJavaScript(mgr.translateJavaScript("PMACTRNDPARTOWN: Ownership"));
      appendDirtyJavaScript("') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('PART_OWNERSHIP_DB',i,0);\n");
      appendDirtyJavaScript("		assignValue_('QTYONHAND',i,1);\n");
      appendDirtyJavaScript("		if (f.PART_OWNERSHIP_DB.value == 'CONSIGNMENT'){\n");
      appendDirtyJavaScript("		alert('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTIONROUNDINVOWNER1: Ownership type Consignment is not allowed in Materials for Preventive Maintenance Actions."));
      appendDirtyJavaScript("'); \n");
      appendDirtyJavaScript("      f.PART_OWNERSHIP.value = ''; \n");
      appendDirtyJavaScript("      f.PART_OWNERSHIP_DB.value = ''; \n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("		if (f.PART_OWNERSHIP_DB.value == 'SUPPLIER LOANED'){\n");
      appendDirtyJavaScript("		alert('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTIONROUNDINVOWNER2: Ownership type Supplier Loaned is not allowed in Materials for Preventive Maintenance Actions."));
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
      appendDirtyJavaScript("   if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("      setDirty();\n");
      appendDirtyJavaScript("   if( !checkOwner(i) ) return;\n");
      appendDirtyJavaScript("   if (f.PART_OWNERSHIP.value == 'Customer Owned') \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("      if( getValue_('OWNER',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("      if( getValue_('PART_OWNERSHIP_DB',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("      if( getValue_('OWNER',i)=='' )\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         getField_('OWNER_NAME',i).value = '';\n");
      appendDirtyJavaScript("         return;\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("	window.status='");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWPMACTRNDPLVAL: Please wait for validation"));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("      r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=OWNER'\n");
      appendDirtyJavaScript("                    + '&OWNER=' + URLClientEncode(getValue_('OWNER',i))\n");
      appendDirtyJavaScript("                    + '&PART_OWNERSHIP_DB=' + URLClientEncode(getValue_('PART_OWNERSHIP_DB',i))\n");
      appendDirtyJavaScript("		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
      appendDirtyJavaScript("		+ '&SPARE_CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&CONDITION_CODE=' + URLClientEncode(getValue_('CONDITION_CODE',i))\n");
      appendDirtyJavaScript("                   );\n");
      appendDirtyJavaScript("      window.status='';\n");
      appendDirtyJavaScript("      if( checkStatus_(r,'OWNER',i,'");
      appendDirtyJavaScript(mgr.translateJavaScript("PMACTRNDOWNER: Owner"));
      appendDirtyJavaScript("') )\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         assignValue_('OWNER_NAME',i,0);\n");
      appendDirtyJavaScript("		assignValue_('QTYONHAND',i,1);\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("   if (f.PART_OWNERSHIP.value == 'Company Owned' && f.OWNER.value != '') \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWPMACTIONROUNDINVOWNER11: Owner should not be specified for Company Owned Stock."));
      appendDirtyJavaScript("          ');\n");
      appendDirtyJavaScript("      f.OWNER.value = ''; \n");
      appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("   if (f.PART_OWNERSHIP.value == 'Consignment' && f.OWNER.value != '') \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWPMACTIONROUNDINVOWNER12: Owner should not be specified for Consignment Stock."));
      appendDirtyJavaScript("          ');\n");
      appendDirtyJavaScript("      f.OWNER.value = ''; \n");
      appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("   if (f.PART_OWNERSHIP.value == 'Supplier Loaned' && f.OWNER.value != '') \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWPMACTIONROUNDINVOWNER13: Owner should not be specified for Supplier Loaned Stock."));
      appendDirtyJavaScript("          ');\n");
      appendDirtyJavaScript("      f.OWNER.value = ''; \n");
      appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("   if (f.PART_OWNERSHIP.value == '' && f.OWNER.value != '') \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWPMACTIONROUNDINVOWNER14: Owner should not be specified when there is no Ownership type."));
      appendDirtyJavaScript("          ');\n");
      appendDirtyJavaScript("      f.OWNER.value = ''; \n");
      appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
      appendDirtyJavaScript("   } \n");
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

      /*String outStr = out.toString();
      int beg_pos = outStr.indexOf("validatePartNo(-1)");

      if (beg_pos > 0)
      {
          String begin_part = outStr.substring(0,beg_pos);
          String first_part = outStr.substring(beg_pos,beg_pos+19);
          String middle_part = "OnBlur=checkPartVal()";
          String last_part = outStr.substring(beg_pos+19);
          String outStrNew = begin_part + first_part + middle_part + last_part;
          out.clear();
          out.append(outStrNew);
      }*/

      // 040120  ARWILK  Begin  (Enable Multirow RMB actions)
      if (bOpenNewWindow)
      {
         appendDirtyJavaScript("  window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));  //XSS_Safe AMNILK 20070725
         appendDirtyJavaScript("\", \"");
         appendDirtyJavaScript(newWinHandle);
         appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
      }
      // 040120  ARWILK  End  (Enable Multirow RMB actions)

      if (bSetObsolete)
      {
         appendDirtyJavaScript("alert('" + sMsgTxt + "'); \n");
         appendDirtyJavaScript(" commandSet('','');\n");                   
      }

   }

   /*private String toSqlFormatMask(String formatMask)
   {
      ASPManager mgr;
      return formatMask.replace("HH", "HH24").replace("hh", "HH12").replace("mm", "MI").replace("a", "AM").replace("p", "PM");
   }*/
}
