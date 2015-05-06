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
*  File        : ActiveSeparate3.java 
*  ASP2JAVA Tool  010213  Created Using the ASP file ActiveSeparate3.asp
*  Modified    : 
*  CHDELK  010419  Added "Customer..." and Customer Agreement..." RMBs.
*  INROLK  010423  Corrected RMB printProposal. Call Id 77446.
*  CHCRLK  010517  Modified validation for MCH_CODE and added validation for ORG_CODE. 
*  SHFELK  010518  Modified validation for CUSTOMER_NO - Call 64973
*  CHCRLK  010522  Added name of lov for Object Id and Code Review..
*  INROLK  010522  Changed validations for Call ID 65254.
*  INROLK  010523  Changed 'ServiceRequest' for Call ID 77619.
*  CHCRLK  010524  Removed javascript function lovmchcode().
*  INROLK  010604  Changed okFindTrans() and gRow()for Call ID 65362.
*  CHCRLK  010612  Modified ovewritten validations.
*  INROLK  010613  Added Back Button to Customer Order.
*  INROLK  010711  Changed the view of lov of object id  and changed the framename call id 77781.
*  CHCRLK  010807  Modified method printProposal().
*  INROLK  010809  Changed validation of Execution time. call id 77835.
*  INROLK  010814  Changed validaton of MCH_CODE. call id 77835
*  ARWILK  010904  Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction(). 
*                  (Caused when cancelNew is performed after saveNew)
*  INROLK  010927  Added Security Check to RMBs.
*  INROLK  011019  Added Decode to Warranty Description. call id 70691.
*  ARWILK  020327  Added URLClientEncode to all the validateFunctions.
*  SHAFLK  020726  Bug Id 31771 Added security check for MOBMGR_WORK_ORDER_API.Check_Exist & Work_Center_Int_API.
*  SHAFLK  021108  Bug Id 34064,Changed method printProposal.
*  ---------------------Generic WO-------------------------------------------
*  INROLK  021128  Added MCH_CODE_CONTRACT and CONNECTION_TYPE.  
*  ----------------------------------------------------------------------------
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  BUNILK  021213  Merged with 2002-3 SP3
*  SHAFLK  030102  Modified validation of AGREEMENT_ID,WORK_TYPE_ID,MCH_CODE and HTML part of vlidations.
*  NIMHLK  030113  Added a new action to call condition code dialog according to specification W110 - Condition Codes.
*  BUNILK  030121  Codes reviewed.
*  YAWILK  030129  Call Id 93402 modified sMchContract to get the Object Site and modified transferWrite().
*  CHCRLK  030602  Added Connection Type and modified related functionality.
*  CHCRLK  030611  Added action "Create MRO Object Receive Order" to the header.
*  CHCRLK  030724  Added call to get valid Agreement for VIM serial in the validation of Object Id. 
*  CHAMLK  030829  Disabled action 'Create New Serial Object' if there is no customer specified in the Service Request.
*                  Passes Customer as Owner and 'Customer Owned'as Ownership to dialog CopySerialDlg.
*  CHCRLK  030903  Added method setCheckboxForVim() to fetch checkbox values for VIM type work orders. 
*  CHCRLK  030906  Made a minor modification to method setCheckboxForVim(). 
*  CHCRLK  030916  Corrected method setCheckboxForVim().
*  CHCRLK  030922  Added action "Agreement Search" to the headbar. 
*  JEWILK  030924  Removed the commented out toggleStyleDisplay() function.
*  JEWILK  030930  Removed some parts in validation of PLAN_F_DATE. Call 101350.
*  ARWILK  031001  (Bug#104731) Corrected validation errors. (Check method comments)
*  ARWILK  031003  (Bug#104743) Modified functions preDefine and validate. (Check method comments)
*  THWILK  031018  Call ID 104789 Added an extra parameter(#)to the deffinition of the field WO_NO.
*  CHCRLK  031021  Modified AGREEMENT_ID validation. [Call 105199]
*  CHCRLK  031022  Modified method saveReturn(). [Call ID 105217] 
*  CHAMLK  031022  Modified function preDefine() to remove setUppderCase() in CONNECTION_TYPE.
*  JEWILK  031023  Removed setMandatory() of field CONNECTION_TYPE and modified saveReturn() and saveNew() to refresh row after saving. Call 106205
*  JEWILK  031025  Modified method checkMROObjRO() to check the conditions according to centura. Call 100823.
*  THWILK  031028  Call ID 109505, Modified Validate method to prevent sPlanStartDate1 getting "null" when Object Id is selected.
*  CHCRLK  031031  Modified validations of fields AGREEMENT_ID, WORK_TYPE_ID, CUSTOMER_NO and MCH_CODE. [Call 105199]
*  CHCRLK  031111  Modified method okFind() to add querySubmit instead of submit. Added a sub menu for state changing methods and modified adjust(). [Calls 110574 & 110563]
*  ARWILK  031216  Edge Developments - (Replaced blocks with tabs, Replaced links with RMB's, Moved RMB's into Groups, Removed clone and doReset Methods)
*  VAGULK  040108  Made the field order according to the order in the Centura application(Web Alignment).
*  DIMALK  040120  Replaced the calls to package Active_Separate1_API with Active_Separate_API
*  SAPRLK  040203  Web Alignment - Added multirow action to master form and the tabs, simplifying code for RMBs, remove unnecessary method calls for hidden fields,
*                  removed unnecessary global variables.  
*  ARWILK  040310  Bug#112970 - Removed tab COInfomation and added it's fields to the headblk.
*  ARWILK  040312  Bug#113080 - Added conditions for Serial Object Creation.
*  SAPRLK  040316  Call Id 113079 Corrected. Changed parameters passed to methods "setMinimizeImage".
*                  and added to conditional code in methods "findAndEditObject" and "findAndEditCustomer".   
*  SHAFLK  040119  Bug Id 41815,Removed Java dependencies. 
*  SHAFLK  040219  Bug Id 42419,Modified validation of field MCH_CODE,
*  SHAFLK  040301  Bug Id 42419,Modified validation of fields AGREEMENT_ID, WORK_TYPE_ID, CUSTOMER_NO and MCH_CODE, and predefine(),
*  SAPRLK  040323  Merge with SP1.
*  THWILK  040604  Removed PM_NO under IID AMEC109A, Multiple Standard Jobs on PMs.
*  ARWILK  040609  Removed the field STD_JOB_ID (For IID AMEC109A).
*  THWILK  040615  Removed block BUDGET and implemented it as a tab.Also added jobs functionalty as a tab into the form.(For IID AMEC109A).
*  ARWILK  040712  Modified calls to ORGANIZATION_API. (IID AMEC500C: Resource Planning) 
*  ARWILK  040820  Changed LOV's of STD_JOB_ID and STD_JOB_REVISION.
*  ARWILK  041001  LCS Merge:46394
*  NIJALK  041014  Modified function validateMchCode.
*  ARWILK  041022  LOV Change for Fields STD_JOB_ID,STD_JOB_REVISION. (Spec AMEC111 - Standard Jobs as PM Templates)
*  NAMELK 041109   Non Standard Translation Tags Corrected.
*  Chanlk  041231  Modified function PrintContents.
*  Chanlk  050207  Modified function Validate(work type).
*  Chanlk  050208  Modified function setCheckboxForVim
*  NIJALK  050217  Modified function agrmntSearch().
*  DIAMLK  050228  Replaced the field Pre_Posting_Id by Pre_Accounting_Id.(IID AMEC113 - Mandatory Pre-posting)
*  DIAMLK  050310  Bug ID:122509 - Modified the method okFindTrans().
*  NEKOLK  050107  Bug 48180, Modified run(), okFindEdit(),agrmntSearch(), setCheckboxForVim() .
*  DIAMLK  050321  Modified the method run().
*  NIJALK  050505  Bug 123698: Set a warning msg when changing state to "Prepared" if WO causes a project exception.
*  NIJALK  050525  Bug 124289: Modified LOV property for AGREEMENT_ID.
*  SHAFLK  050425  Bug 50830, Used mgr.translateJavaScript() method to get the correctly translated strings in JavaScript functions.
*  NIJALk  050617  Merged bug 50830. 
*  NIJALK  050810  Bug 126224, Modified newRow().
*  NIJALK  050824  Bug 126554, Modified preDefine().
*  AMNILK  050927  Bug 127369, Modified Ajust().
*  SHAFLK  050919  Bug 52880, Modified preDefine() and replaced all places calling Vim_Serial_API.Fetch_Object_Details.
*  NIJALK  051004  Merged bug 52880.
*  NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile).
*  NEKOLK  051125  Bug 54415, Changed server method Get_Contract to Get_Def_Contract .
*  NIJALK  051220  Merged bug 54415.
*  NIJALK  060110  Changed DATE format to compatible with data formats in PG19.
*  THWILK  060203  Call 132922, Modified Predefine() and Adjust().
*  SHAFLK  060206  Call 132969, Modified run() and okFindEdit().
*  NIJALK  060206  Call 132938: Modified customforobj(),objectForCustomer(),findAndEditCustomer(),findAndEditObject(),run().
*  NIJALK  060206  Call 132960: Modified cancelEdit().
*  SHAFLK  060207  Call 132977: Introduced some check boxes. 
*  NIJALK  060216  Call 134266: Modified validation to AGREEMENT_ID. Modified findAndEditCustomer(),findAndEditObject().
*  NIJALK  060217  Call 134472: Modified setCheckBoxValue().
*  NIJALK  060217  Call 134492: Renamed filed "Description" to "Work Description".
*  SULILK  060217  Call 134271: Modified itemblk2 STD_JOB_ID and SIGNATURE fields in prdefine().Modified validate() method also. 
*  NIJALK  060220  Call 134475: Added methods deleteRow2(),deleteRowITEM2(). Modified preDefine(),validate(),run(),printContents().
*  NIJALK  060221  Added parameter QTY to method call to Work_Order_Job_API.Disconnect_Std_Job.
*  NEKOLK  060221  Call 134809-Modified saveReturnITEM1.
*  NIJALK  060221  Call 134481: Cleared the itemset2 at begining of the okFind().
*  SULILK  060222  Call 135120: Modified setCheckBoxValue()
*  ASSALK  060223  Call 135136. Modified deleteRowITEM2() & added saveReturnItem2().
*  NIJALK  060224  Call 135521: Modified findAndEditCustomer(),findAndEditObject().
*  NEKOLK  060227  Call 134923,Modified okFindItem() agrmntSearch() and modified run(),.
*  ASSALK  060302  Call 134682. Modified saveNew() & saveReturn(). 
*  THWILK  060307  Call 136334, Modified predefine(). 
*  THWILK  060307  Call 136518, Modified predefine(). 
*  NIJALK  060322  Renamed 'Work Master' to 'Executed By'.
* ----------------------------------------------------------------------------
*  CHODLK  060510  Modified methods run(),transferWrite(),preDefine().
*  CHODLK  060524  Bug 56390-2, Modified method transferWrite(). 
*  AMNILK  060629  Merged with SP1 APP7.
*  JEWILK  060803  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged Bug Id:58214.
*  ILSOLK  060817  Set the MaxLength of Address1 as 100.
*  JEWILK  060816  Bug 58216, Eliminated SQL Injection security vulnerability.
*  NAMELK  060906  Merged Bug 58216.
*  NAMELK  060913  REQUEST ID 45081: Addresses for Customer RMB added.
*  AMDILK  060918  Removed the Button "Send SMS"
*  AMNILK  061206  Modifies the javaScript function lovMchCode(), adjust() and preDefine().
*  SHAFLK  060731  Bug 59613, Modified changeConditionCode(). 
*  ILSOLK  070216  Modifed for MTPR904 Hink tasks ReqId 45088.setLovProperty() for Object Id.
*  BUNILK  070504  Implemented "MTIS907 New Service Contract - Services" changes.
*  AMDILK  070404  Eliminate script errors
*  ARWILK  070410  MTIS907: Replaced calls to SERIAL_PART_AGR_DETAILS_API with Psc_Contr_Product_API. Also added hidden field LINE_NO to header.
*  ARWILK  070410  MTIS907: Removed references to SerialPartAgreementDlg.
*  AMDILK  070430  Call Id 143039: Modified Validate() to fetch the customer id and coordinator id from contract id
*  AMNILK  070502  Call Id: 142850. Modified Validate(). Removed Cust_Agreement_Object_API & Cust_Agreement_Peoduct_API.
*  AMNILK  070504  Call Id: 142849. Modified the method getSettingTime().
*  AMDILK  070504  Modified method validate().
*  AMDILK  070504  Call Id 142863: fetched the value for field "CBHASAGREEMENT" by calling Psc_Contr_Product_Api.Has_Contract_For_Object
*  AMDILK  070514  Call Id 144397: Modified run()
*  AMDILK  070614  call Id 144417: Modified run(), saveReturn(), customerforobj()
*  AMDILK  070614  call Id 144425: Modified Lov validations.
*  AMDILK  070607  Call Id 145865: Inserted new service contract information; Contrat name, contract type, 
*                  line description and invoice type
*  ILSOLK  070706  Eliminated XSS. 
*  AMDILK  070712  Fetched the agreement informations from the given contrat id
*  NIJALK  070525  Bug 64744, Modified run(), saveReturn(), preDefine() and printContents(). Added updatePostings().
*  AMDILK  070716  Merged bug 64744
*  NIJALK  070420  Bug 64572, Modified preDefine().
*  ILSOLK  070718  Merged Bug ID 64572.
*  ILSOLK  070730  Eliminated LocalizationErrors.
*  AMDILK  070731  Removed the scroll buttons of the parent when the child tabs are in new or edit modes
*  AMDILK  070905  Call Id 148272: Modified javascript function validatePlanFDate() to validate the Planned start
*                  date planned end date
*  ASSALK  070920  Call ID 148854. Modified printContents().
*  AMNILK  070920  Call Id: 206321. Modified findAndEditCustomer().
*  CHANLK  071130  Bug 69047 Add contract auto fetch functionality.
* ----------------------------------------------------------------------------
* -----------------------APP7.5 SP1-------------------------------------
*  ILSOLK  071214  Bug Id 68773, Eliminated XSS.
*  CHANLK  071217  Bug 68362 Change method findAndEditObject().
*  NIJALK  071220  Bug 69819, Removed function calls to Customer_Agreement_API.Get_Description. Modified parameter 'ref' in
*                  method calls to checksec() accordingly.
*  CHANLK  080104  Bug 68363, Change method findAndEditObject().
*  CHANLK  080121  Bug 68947, Added RMB Service Contract Line Search.
* -----------------------------------------------------------------------
*  AMNILK  080125  Bug Id 70012, Modified printContents().
*  SHAFLK  080227  Bug 71738, Modified method createQuot().
*  CHANLK  080304  Bug 72007, Modified valid conditions for RMB's.
*  ARWILK  071130  Bug 66406, Added CONN_PM_NO, CONN_PM_REVISION, CONN_PM_JOB_ID.
*  AMNILK  080317  Bug Id 70921, Added new methods calculateDatesForSrvcon(),getDatesForSrvcon().
*                  Modified FindAndEditCustomer() and FindAndEditObject().
*  AMNILK  080430  Bug Id 70156.Modified printContents().
*  ARWILK  080502  Bug 70920, Overrode lovLineNo and lovContractId.
*  SHAFLK  090305  Bug 80959  Modified preDefine() and added reinit(). 
*  HARPLK  090708  Bug 84436, Modified preDefine(),findAndEditObject().
*  SHAFLK  100210  Bug 88904  Modified printContents(). 
*  SHAFLK  100211  Bug 88922  Modified printContents(), calculateDatesForSrvcon(), getDatesForSrvconValidate(), preDefine() and validate(). 
*  NIJALK  100419  Bug 87935  Modified adjust().
*  ILSOLK  100916  Bug 93061, Modified saveReturn().
* -----------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.ASPBlock;
import ifs.fnd.asp.ASPBlockLayout;
import ifs.fnd.asp.ASPBuffer;
import ifs.fnd.asp.ASPCommand;
import ifs.fnd.asp.ASPCommandBar;
import ifs.fnd.asp.ASPContext;
import ifs.fnd.asp.ASPField;
import ifs.fnd.asp.ASPForm;
import ifs.fnd.asp.ASPHTMLFormatter;
import ifs.fnd.asp.ASPManager;
import ifs.fnd.asp.ASPPageProvider;
import ifs.fnd.asp.ASPQuery;
import ifs.fnd.asp.ASPRowSet;
import ifs.fnd.asp.ASPTabContainer;
import ifs.fnd.asp.ASPTable;
import ifs.fnd.asp.ASPTransactionBuffer;
import ifs.fnd.service.FndException;
import ifs.fnd.service.Util;

import java.util.Date;
import java.text.DateFormat;

public class ActiveSeparate3 extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeparate3");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPForm frm;
   private ASPHTMLFormatter fmt;
   private ASPContext ctx;

   private ASPBlock eventblk;
   private ASPRowSet eventset;

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

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

   private ASPBlock tempblk;
   private ASPField f;
   private ASPBlock inact;
   private ASPBlock ref1;
   private ASPBlock blkPost;

   private ASPField fCOUNTRY;
   private ASPField fADDRESS1;
   private ASPField fADDRESS2;
   private ASPField fZIP_CODE;
   private ASPField fCITY;
   private ASPField fCOUNTRY_CODE;
   private ASPField fSTATE;   
   private ASPField fCOUNTY;

   //Web Alignment - simplify code for EMBs
   private boolean bOpenNewWindow;
   private String urlString;
   private String newWinHandle;
   //
   private ASPTabContainer tabs;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================

   private String sAddressID;
   private int sReturnedCommand;
   private String fmtdBuff;
   private String transferred;
   private String trToMob;
   private ASPTransactionBuffer trans;
   private ASPTransactionBuffer trans1;
   private ASPTransactionBuffer trans12;

   private String cbWarrantyVar;
   private String dateMask;
   private String companyvar;
   private String trnsfr;
   private String newman;
   private int curentrow;
   private boolean comBarAct;
   private boolean newWarr;
   private String sWoNo;
   private String caseId;
   private String caseFlag;
   private String custFlag;
   private String transferurl;
   private String keys;
   private String performRMB;
   private boolean isFind;
   private String comp;
   private String sDbFaultReport;
   private String qrystr;
   private String rWarrType;
   private String rRowN;
   private String rRawId;
   private String referenceNo;
   private String caseCustId;
   private String caseContact;
   private String casePhoneNo;
   private String rWarrDesc;
   private ASPBuffer buff;
   private String sWorkTID;
   private ASPTransactionBuffer secBuff;
   private ASPCommand cmd;
   private String sDefContract;
   private String reportById;
   private String custno;
   private String clientvalue;
   private String frepagree;
   private String frepagreeclient;
   private String cbhasagreement;
   private String agrredesc;
   private String custdesc;
   private String custwarrenty;
   private String sAgreementId1;
   private int nSettingTime1;
   private String sCustomerNum1;
   private String sRegistDate;
   private String sRequiredStartDate1;
   private String lsFaultReportAgreement;
   private String lsClientFaultReportAgreement;
   //private String sStandardJob;
   private String sAuthoCode;
   //private String sWorkDesc;
   private String sOrgDesc;
   private String sSysDate1;
   private String sSysDate;
   private String sPlanStartDate1;
   private String sPlanEndDate1; 
   private String sRequiredEndDate1;  
   private int placeOfHoursStart;
   private int placeOfHoursFinish;
   //private String stdjobid;
   private String orgcodedescription;
   private String worktypedescription;
   private int settingtime;
   private String agreemid;
   private String authcode;
   private String stdjob;
   //private String workdescrlo;
   private double exttime;
   private String cbWarrantyOnObject;
   private String cbhasactivewo;
   private String addId;
   private String address1;
   private String address2;
   private String address3;
   private String address4;
   private String address5;
   private String address6;
   private String agreemid1;
   private String authcode1;
//	 Bug 68362, Start        
   private String sConnectionTypeDB;
   private String sConnectionType;
//	 Bug 68362, End        
   //private String stdjob1;
   private String orgcode1;
   //private String workdesc;
   private String machname;
   private String orgcodedescription1;
   private ASPBuffer buf;
   private String noSecurity;
   private String flg;
   private String finish;
   private String discDesc;
   private String symDesc;
   private String custOrdType;
   private ASPBuffer set;
   private ASPQuery q;
   private String sContract1;
   private String sWorkTID1;
   private String sMchCodeDesc1;
   private String sWorkTypeDesc1;
   private String sAgreementID1;
   private String sStdJobID1;
   private double sPlanHrs1;
   //private String sWorkDesrLo1;
   private String sAuthorizeCode1;
   private String sPlanCompleteDate1;
   private String sOrgCode1;
   private String sErrorSymptom1;
   private String sSymptomDescription1;
   private int nWarrantyRowNo1;
   private String sAddressID1;
   private String s1Address1;
   private String s2Address1;
   private String s3Address1;
   private String s4Address1;
   private String s5Address1;
   private String s6Address1;
//	 Bug 68362, Start        
   private String sConnectionTypeDB1;
   private String sConnectionType1;
//	 Bug 68362, End        
   private String sCbHasAgreement1;
   private String sCbHasActiveWorkOrder1;
   private ASPBuffer temp;
   private String sMchCodeDesc;
   private String sWorkTypeDesc;
   private String sContractSave;
   private String sAddressIdSave;
   private String sIsHasAgreements;
   private String sIsHasActiveWorkOrder;
   private String sIsFaultReportAgreement;
   private String sIsClientFaultReportAgreement;
   private String sIsStdJobID;
   private int sIsExecutionTime;
   private String sIsAuthorizeCode;
   //private String sIsStdJobDesc;
   private String sIsSymptCodeDesc;
   private int sCustomerCreditStoped;
   private String signId;
   private int headrowno;
   private int n;
   private int i;
   private double budgRev;
   private double budgCost;
   private double planRev;
   private double planCost;
   private double totBudgCost;
   private double totBudgRev;
   private double totBudgMargin;
   private double totplannCost;
   private double totPlannRev;
   private double totplannMargin;
   private double totActCost;
   private double totActRev;
   private double totActMargin;
   private double budCost;
   private double budRev;
   private double budMargine;
   private double plannCost;
   private double plannRev;
   private ASPBuffer data;
   private int headCurrrow;
   private String woNo;
   private String workOrderCostType;
   private String repid;
   private String custName;
   private String hCustOrdertype;
   private String itemCustOrderType;
   private ASPBuffer r;
   private int currrow;
   private int currrowItem;
   private ASPBuffer row;
   private String newObjEvents;
   private String current_url;
   private String calling_url;
   private int enabl10;
   private String objstate;
   private String contract;
   private String wo_no;
   private int enabl0;
   private int enabl1;
   private int enabl2;
   private int enabl3;
   private int enabl4;
   private int enabl5;
   private int enabl6;
   private int enabl7;
   private int enabl8;
   private int enabl9;
   private ASPBuffer buffer;
   private String sState;
   private String nQuotationId;
   private String attr3;
   private String attr4;
   private String result_key;
   private String fldTitleReportedBy;
   private String lovTitleReportedBy;
   private String head_date;
   private boolean edited;
   private boolean editedCust;
   public  String isSecure[];
   private String sWONo; 
   private String sObjectID;  
   private String sRegDatePassed;  
   private String sContract;
   private String sMchContract;
   private String sMchCode;  
   private String sRow;  
   private String sWarr;  
   private String sMchNameDes;  
   private String sWorkTypeID; 
   private String sCustomerNum; 
   private String sAgreementID; 
   private String sDescription; 
   private int nSettingTime; 
   private int nExecutionTime; 
   private String sOrgCode;  
   private String sStdJobID;  
   private String sErrorSymptom;  
   private int nWarrantyRowNo;  
   private String objState;  
   private int item1CurrRow;  
   private String sObjectID1;  
   private String sMchCodeSave;  
   private String sSettingTime; 
   private String sSettingTime1; 
   private double actCost;  
   private double actRev; 
   private double actMargin;  
   private double budgMargin; 
   private double planMargin; 
   private String enabled_events;  
   private String attr1;  
   private String attr2;  
   private String ret_or_no;
   private String ret_cu_no;
   private String ret_cu_type;
   private String ret_curr_code;
   private String ret_au_code;
   private String trans_;
   private String sentWoNo;
   private String sentAgrmntId;
   private String sentWorkTypeId;
   private String sentWorkShop;
   private ASPBuffer ret_data_buffer;
   private String lout;
   private String hasPlanning;
   private String sContractId;
   private String sLineNo;

   //============ Variables for Security check for RMBs ===============

   private boolean again;
   private boolean objectForCustomer_;
   private boolean customforobj_;
   private boolean transToMob_;
   private boolean newFuncObject_;
   private boolean newSerialObject_;
   private boolean grapicalObject_;
   private boolean prepareWork_;
   private boolean reportIn_;
   private boolean preposting_;
   private boolean printProposal_;
   private boolean custInfo_;
   private boolean custAgreement_;
   private boolean actWoOrd_;
   private boolean deptResources_;
   private boolean createQuot_;
   private boolean prepWoQuot_;
   private boolean custWarr_;
   //Bug 68947, Start   
   private boolean srvCon_;
   //Bug 68947, End   
   private boolean mroCreateRO_;
   private boolean copy_;

   private boolean vimObject;
   private boolean vimAgree;
   private boolean cusAgree;
   private boolean bException;

   private String sTestPointId;
   private String pmDescription;
   private String insNote;
   private String sPmNo;
   private boolean bPpChanged;
//  Bug 68363, Start
   private boolean bFECust;
//  Bug 68363, End
   //Bug Id 70012,Start
   private boolean bPcmsciExist;
   // Bug Id 70012,End

   //Bug Id 70921, Start
   private boolean bSrvconDateChange;
   private String sSkipDateOverwrite;
   private boolean bShowWarning;
   //Bug Id 70921, End

   //===============================================================
   // Construction 
   //===============================================================
   public ActiveSeparate3(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() 
   {
      isSecure =  null;
      cbWarrantyVar =  "";
      sWONo =  "";
      sObjectID =  "";
      sRegDatePassed =  "";
      sContract =  "";
      sMchContract =  "";
      sMchCode =  "";
      sRow =  "";
      sWarr =  "";
      sMchNameDes =  "";
      sWorkTypeID =  "";
      sCustomerNum =  "";
      sAgreementID =  "";
      sDescription =  "";
      nSettingTime =  0;
      sOrgCode =  "";
      sStdJobID =  "";
      sErrorSymptom =  "";
      nWarrantyRowNo =  0;
      sAddressID =  "";
      sReturnedCommand =  0;
      fmtdBuff =  "";
      transferred = "";
      objState = "";
      trToMob = "";
      hasPlanning = "";

      //Bug Id 70921, Start
      bSrvconDateChange = true;
      //Bug Id 70921, End

      ASPManager mgr = getASPManager();

      frm = mgr.getASPForm();
      fmt = mgr.newASPHTMLFormatter();
      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      trans1 = mgr.newASPTransactionBuffer();
      trans12 = mgr.newASPTransactionBuffer();

      dateMask =  ctx.readValue("DATEMASK","");
      companyvar =  ctx.readValue("COMPANYVAR",""); 
      trnsfr = ctx.readValue("TRANSFR","");
      newman = ctx.readValue("NEWMAN","");
      curentrow = (int)ctx.readNumber("CURRENTROW",0);
      comBarAct = ctx.readFlag("COMBARACT",false);
      edited = ctx.readFlag("EDITED",false);
      editedCust = ctx.readFlag("EDITEDCUST",false);
      newWarr = ctx.readFlag("NEWWARR",false);
      sWoNo = ctx.readValue("SWONO","");
      caseId = ctx.readValue("CASEID","");
      caseFlag = ctx.readValue("CASEFLAG","");
      custFlag = ctx.readValue("CUSTFLAG","");
      ret_or_no = ctx.readValue("RETORNO","");
      ret_cu_no = ctx.readValue("RETCUNO","");
      ret_cu_type = ctx.readValue("RETCUTYPE","");
      ret_curr_code = ctx.readValue("RETCURRCODE","");
      ret_au_code = ctx.readValue("RETAUCODE","");

      dateMask = mgr.getFormatMask("Datetime",true);
      transferurl = ctx.readValue("TRNURL","");

      keys = ctx.readValue("KEYS","");
      performRMB  = ctx.readValue("PERFRMB","");

      isFind = ctx.readFlag("ISFIND",false);
      comp = ctx.readValue("COMP");
      sDbFaultReport = ctx.readValue("CTXFLTREP","");

      sWONo = ctx.readValue("SWONOPAS", sWONo);
      sObjectID = ctx.readValue("SOBJECIDS", sObjectID);
      sRegDatePassed = ctx.readValue("SREGDATPAS", sRegDatePassed);
      sContract = ctx.readValue("SCONNNT", sContract);
      sMchContract = ctx.readValue("SMCHCONNNT", sMchContract);
      sMchCode = ctx.readValue("CTXMCHCODE", sMchCode);
      sRow = ctx.readValue("SROW", sRow);
      sWarr = ctx.readValue("SWARR", sWarr);
      sMchNameDes = ctx.readValue("CTXMCHNAMEDESCRIPTION", sMchNameDes);
      sWorkTypeID = ctx.readValue("SWRKTIEID", sWorkTypeID);
      sCustomerNum = ctx.readValue("SCUSTNUMB", sCustomerNum);
      sAgreementID = ctx.readValue("SAGGRIDM", sAgreementID);
      sDescription = ctx.readValue("SDESC", sDescription);
      nSettingTime = ctx.readNumber("NSETTTIM", nSettingTime);
      sOrgCode = ctx.readValue("SORGCOED", sOrgCode);
      sStdJobID = ctx.readValue("SSDTOJBDI", sStdJobID);
      sErrorSymptom = ctx.readValue("SERRRSYMPT", sErrorSymptom);
      nWarrantyRowNo =ctx.readNumber("NWARRNUMB", nWarrantyRowNo);
      sAddressID = ctx.readValue("SIDOFADDREESS", sAddressID);
      qrystr = ctx.readValue("QRYSTR", "");
      rWarrType = ctx.readValue("RWARRTYPE", "");
      rRowN = ctx.readValue("RROWN", "");
      rRawId = ctx.readValue("RRAWID", "");
      transferred = ctx.readValue("TRANSFERRED", "");
      objState = ctx.readValue("OBJESTATE", "");
      sReturnedCommand = ctx.readNumber("SRETNCOMM", sReturnedCommand);
      item1CurrRow =ctx.readNumber("ITEM1CURRROW", 0);
      sTestPointId = ctx.readValue("CTXTESTPOINTID","");
      pmDescription= ctx.readValue("PMDESC","");
      insNote = ctx.readValue("INSPNOTE","");
      sPmNo = ctx.readValue("PMNO","");
      //Bug Id 70012, Start
      bPcmsciExist = ctx.readFlag("PCMSCIEXIST",false);
      //Bug Id 70012, End    

      //Bug Id 70921, Start
      bShowWarning = ctx.readFlag("SHOWSRVCONWARNING",true);
      sSkipDateOverwrite = ctx.findGlobal("SKIPDATEOVERWITE");

      //Bug Id 70921, End

      //=============== Variables for Security check for RMBs =================

      again = ctx.readFlag("AGAIN", again);
      objectForCustomer_= ctx.readFlag("OBJECTFORCUSTOMER", false);
      customforobj_= ctx.readFlag("CUSTOMFOROBJ", false);
      transToMob_= ctx.readFlag("TRANSTOMOB", false);
      newFuncObject_ =  ctx.readFlag("NEWFUNCOBJECT", false);
      newSerialObject_= ctx.readFlag("NEWSERIALOBJECT", false);
      grapicalObject_= ctx.readFlag("GRAPHICALOBJECT", false);
      prepareWork_= ctx.readFlag("PREPAREWORK", false);
      reportIn_ = ctx.readFlag("REPORTIN", false);
      preposting_= ctx.readFlag("PREPOSTING", false);
      printProposal_= ctx.readFlag("PRINTPROPOSAL", false);
      custInfo_= ctx.readFlag("CUSTINFO", false);
      custAgreement_= ctx.readFlag("CUSTAGREEMENT", false);
      actWoOrd_= ctx.readFlag("ACTWOORD", false);
      deptResources_ = ctx.readFlag("DEPTRESOURCES", false);
      createQuot_ = ctx.readFlag("CREATEQUOT", false);
      prepWoQuot_= ctx.readFlag("PREPWOQUOT", false);
      custWarr_= ctx.readFlag("CUSTWARR", false);
      //Bug 68947, Start   
      srvCon_= ctx.readFlag("SRVCON", false);
      //Bug 68947, End   
      mroCreateRO_ = ctx.readFlag("MROCREATERO", false);
      copy_= ctx.readFlag("COPY", false);

      if (!mgr.commandBarActivated() && mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         ctx.setGlobal("CALL_URL","NOTCUSTORDER");

      if (mgr.commandBarActivated())
      {
         comBarAct = true;

         clearItem1();
         eval(mgr.commandBarFunction());
         if ("HEAD.SaveReturn".equals(mgr.readValue("__COMMAND")))
            headset.refreshRow();

      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("CASE_ID")))
      {
         caseFlag = "TRUE";

         caseId = mgr.readValue("CASE_ID");
         referenceNo = mgr.readValue("REFERENCE_NO");
         caseCustId = mgr.readValue("CUSTOMER_NO");
         caseContact = mgr.readValue("CONTACT");
         casePhoneNo = mgr.readValue("PHONE_NO");

         newRowForCC();
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALUEOF_MCHCOD")))
      {
         sObjectID = mgr.readValue("VALUEOF_MCHCOD");
         sMchContract = mgr.readValue("VALUEOF_CONTRACT");
         sWorkTID = mgr.readValue("VALUEOF_WORKTYPEID");
         sCustomerNum = mgr.readValue("VALUEOF_CUSTOMERNO");
         sAgreementID = mgr.readValue("VALUEOF_AGREEMENTID");
         sContractId = mgr.readValue("VALUEOF_CONTRACTID");
         sLineNo     = mgr.readValue("VALUEOF_LINENO");
         nSettingTime = (int)mgr.readNumberValue("VALUEOF_SETTINGTIME");
         sOrgCode = mgr.readValue("VALUEOF_ORGCODE");
         sStdJobID = mgr.readValue("VALUEOF_STAJOBID");
         sErrorSymptom = mgr.readValue("VALUEOF_ERRSYMPT");
         nWarrantyRowNo = (int)mgr.readNumberValue("VALUEOF_WARRANTYNO");
         sAddressID = mgr.readValue("VALUEOF_ADDRESSID");
         qrystr = ctx.findGlobal("CALLING_URL","");

         if (!mgr.isEmpty(mgr.getQueryStringValue("CANCELVAL_WONO")))
         {
            sWONo = mgr.readValue("CANCELVAL_WONO");
            findAndEditObject();
         }
         else if (!mgr.isEmpty(mgr.getQueryStringValue("CUSTAGREEVAL_WONO")))
         {
            sWONo = mgr.readValue("CUSTAGREEVAL_WONO");
            sReturnedCommand = 1;
            findAndEditObject();
         }
         else if (!mgr.isEmpty(mgr.getQueryStringValue("SELWARRANTYVAL_WONO")))
         {
            sWONo = mgr.readValue("SELWARRANTYVAL_WONO");
            sReturnedCommand = 2;
            findAndEditObject();
         }

      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALUEOF_OBJECTID")))
      {
         sObjectID = mgr.readValue("VALUEOF_OBJECTID");
         sContract = mgr.readValue("VALUEOF_CONTRACT");
         sWorkTID = mgr.readValue("VALUEOF_WORKTYPEID");
         sCustomerNum = mgr.readValue("VALUEOF_CUSTOMERNO");
         sAgreementID = mgr.readValue("VALUEOF_AGREEMENTID");
         nSettingTime = (int)mgr.readNumberValue("VALUEOF_SETTINGTIME");
         sOrgCode = mgr.readValue("VALUEOF_ORGCODE");
         sStdJobID = mgr.readValue("VALUEOF_STAJOBID");
         sErrorSymptom = mgr.readValue("VALUEOF_ERRSYMPT");
         sContractId = mgr.readValue("VALUEOF_CONTRACTID");
         sLineNo     = mgr.readValue("VALUEOF_LINENO");
         nWarrantyRowNo = (int)mgr.readNumberValue("VALUEOF_WARRANTYNO");
         qrystr = ctx.findGlobal("CALLING_URL","");

         if (!mgr.isEmpty(mgr.getQueryStringValue("CANCELVAL_WONO")))
         {
            sWONo = mgr.readValue("CANCELVAL_WONO");
            findAndEditCustomer();
         }
         else if (!mgr.isEmpty(mgr.getQueryStringValue("CUSTAGREEVAL_WONO")))
         {
            sWONo = mgr.readValue("CUSTAGREEVAL_WONO");
            sReturnedCommand = 1;
            findAndEditCustomer();
         }
         else if (!mgr.isEmpty(mgr.getQueryStringValue("SELWARRANTYVAL_WONO")))
         {
            sWONo = mgr.readValue("SELWARRANTYVAL_WONO");
            sReturnedCommand = 2;
            findAndEditCustomer();
         }

      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("NEW_AGRMNT_ID")))
      {
         sentAgrmntId = mgr.readValue("NEW_AGRMNT_ID","");
         sentWoNo = mgr.readValue("SENT_WO_NO","");
         sentWorkTypeId = mgr.readValue("WORKTYPEID","");
         sentWorkShop  = mgr.readValue("WORKSHOPCODE","");
         qrystr = ctx.findGlobal("CALLING_URL","");

         okFindEdit();        
         edited = true;

      }

      else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
      {
         okFind();

         if (!mgr.isEmpty(mgr.getQueryStringValue("RWARRTYPEENT")))
         {
            if (headset.countRows() > 0)
            {
               rWarrType = mgr.readValue("RWARRTYPEENT");
               rWarrDesc = mgr.URLDecode(mgr.readValue("RWARRDESC"));
               rRowN = mgr.readValue("RROWNENT");
               rRawId = mgr.readValue("RWARRIDENT");  

               buff = headset.getRow();
               buff.setValue("CUST_WARR_TYPE_DUMMY", rWarrType);
               buff.setValue("WARR_DESC_DUMMY", rWarrDesc);

               headset.setRow(buff);

               headlay.setLayoutMode(headlay.EDIT_LAYOUT);   

               newWarr = true;   
            }
         }
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE")))
      {
         sContract = mgr.readValue("CONTRACT");
         sMchCode = mgr.readValue("MCH_CODE");
         sMchNameDes = mgr.readValue("MCH_CODE_DESCRIPTION");
         sMchContract = mgr.readValue("MCH_CODE_CONTRACT");
         sTestPointId = mgr.readValue("TEST_POINT_ID");
         sPmNo = mgr.readValue("PM_NO");
         pmDescription = mgr.readValue("PM_DESCR");
         insNote = mgr.readValue("NOTE");
         transferWrite(sContract, sMchCode, sMchNameDes, sMchContract,sTestPointId,sPmNo,pmDescription,insNote);   
         headlay.setLayoutMode(headlay.NEW_LAYOUT);   
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
         okFind();
         startup();
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("FAULT_REP_FLAG")))
      {
         okFind();  
         startup();
      }
      /*else if (mgr.buttonPressed("SENDSMS"))
          trnsfr = "TRUE";*/
      else if (mgr.dataTransfered())
      {
         startup();
         okFindTrans();
      }
      else if (mgr.buttonPressed("BACK"))
         back();
      else if ("AAAA".equals(mgr.readValue("HASPLANNING")))
         deleteRow2("YES");
      else if ("BBBB".equals(mgr.readValue("HASPLANNING")))
         deleteRow2("NO");
      else if ("TRUE".equals(mgr.readValue("PRPOSTCHANGED")))
         updatePostings();
      //Bug 68947, Start   
      else if (!mgr.isEmpty(mgr.readValue("TEMPCONTRACTID")))
         updateContract();
      //Bug 68947, End   
      else
         startup();

      if (!again)
      {
         checkObjAvaileble();
         again = true;
      }

      adjust();

      ctx.writeValue("DATEMASK", dateMask);
      ctx.writeValue("COMPANYVAR", companyvar);
      ctx.writeNumber("CURRENTROW", curentrow);
      ctx.writeFlag("EDITED", edited);
      ctx.writeFlag("EDITEDCUST", editedCust);
      ctx.writeFlag("NEWWARR", newWarr);
      ctx.writeValue("CTXFLTREP", sDbFaultReport);
      ctx.writeValue("SWONOPAS", sWONo);
      ctx.writeValue("SOBJECIDS", sObjectID);
      ctx.writeValue("SREGDATPAS", sRegDatePassed);
      ctx.writeValue("SCONNNT", sContract);
      ctx.writeValue("SMCHCONNNT", sMchContract);
      ctx.writeValue("SWRKTIEID", sWorkTypeID);
      ctx.writeValue("SCUSTNUMB", sCustomerNum);
      ctx.writeValue("SAGGRIDM", sAgreementID);
      ctx.writeValue("SDESC", sDescription);
      ctx.writeNumber("NSETTTIM", nSettingTime);
      ctx.writeValue("SORGCOED", sOrgCode);
      ctx.writeValue("SSDTOJBDI", sStdJobID);
      ctx.writeValue("SERRRSYMPT", sErrorSymptom);
      ctx.writeNumber("NWARRNUMB", nWarrantyRowNo);
      ctx.writeValue("SIDOFADDREESS", sAddressID);
      ctx.writeNumber("SRETNCOMM", sReturnedCommand);
      ctx.writeValue("QRYSTR", qrystr);
      ctx.writeValue("RWARRTYPE", rWarrType);
      ctx.writeValue("RROWN", rRowN);
      ctx.writeValue("RRAWID", rRawId);
      ctx.writeValue("SROW", sRow);
      ctx.writeValue("SWARR", sWarr);
      ctx.writeValue("TRANSFERRED", transferred);
      ctx.writeValue("OBJESTATE", objState);

      ctx.writeNumber("ITEM1CURRROW", item1CurrRow);
      ctx.writeValue("CASEID", caseId);
      ctx.writeValue("CASEFLAG", caseFlag);

      ctx.writeValue("RETORNO", ret_or_no);
      ctx.writeValue("RETCUNO", ret_cu_no);
      ctx.writeValue("RETCUTYPE", ret_cu_type);
      ctx.writeValue("RETCURRCODE", ret_curr_code);
      ctx.writeValue("RETAUCODE", ret_au_code);

      ctx.writeValue("CTXTESTPOINTID",sTestPointId);
      ctx.writeValue("PMDESC",pmDescription);
      ctx.writeValue("INSPNOTE",insNote);
      ctx.writeValue("PMNO",sPmNo);

      // =============== Variables for Security check for RMBs ============

      ctx.writeFlag("AGAIN", again);
      ctx.writeFlag("OBJECTFORCUSTOMER", objectForCustomer_);
      ctx.writeFlag("CUSTOMFOROBJ", customforobj_);
      ctx.writeFlag("TRANSTOMOB", transToMob_); 
      ctx.writeFlag("NEWFUNCOBJECT", newFuncObject_);
      ctx.writeFlag("NEWSERIALOBJECT", newSerialObject_);
      ctx.writeFlag("GRAPHICALOBJECT", grapicalObject_);
      ctx.writeFlag("PREPAREWORK", prepareWork_);
      ctx.writeFlag("REPORTIN", reportIn_);
      ctx.writeFlag("PREPOSTING", preposting_);
      ctx.writeFlag("PRINTPROPOSAL", printProposal_);
      ctx.writeFlag("CUSTINFO", custInfo_);
      ctx.writeFlag("CUSTAGREEMENT", custAgreement_);
      ctx.writeFlag("ACTWOORD", actWoOrd_);
      ctx.writeFlag("DEPTRESOURCES", deptResources_);
      ctx.writeFlag("CREATEQUOT", createQuot_);
      ctx.writeFlag("PREPWOQUOT", prepWoQuot_);
      ctx.writeFlag("CUSTWARR", custWarr_);
      //Bug 68947, Start   
      ctx.writeFlag("SRVCON", srvCon_);
      //Bug 68947, End   
      ctx.writeFlag("MROCREATERO", mroCreateRO_);
      ctx.writeFlag("COPY", copy_);
      //Bug Id 70012, Start
      ctx.writeFlag("PCMSCIEXIST", bPcmsciExist);
      //Bug Id 70012, End

      //Bug Id 70921, Start
      ctx.writeFlag("SHOWSRVCONWARNING",bShowWarning);
      ctx.setGlobal("SKIPDATEOVERWITE",sSkipDateOverwrite);

      //Bug Id 70921, End


      tabs.saveActiveTab();
   }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
// This function is used to check security status for methods which
// are in modules Order,Purch and Invent .This function is called before
// the required method is performed.  -NUPE,21-07-2000
// Todo Parameter types are not recognized and set them to String. Declare type[s] for (method,ref)

   public boolean  checksec( String method,int ref,String[]isSecure1)
   {
      ASPManager mgr = getASPManager();

      isSecure1[ref] = "false" ;
      String splitted[] = split(method,".");

      String first = splitted[0].toString();
      String Second = splitted[1].toString();

      secBuff = mgr.newASPTransactionBuffer();
      secBuff.addSecurityQuery(first,Second);
      secBuff = mgr.perform(secBuff);

      if (secBuff.getSecurityInfo().itemExists(method))
      {
         isSecure1[ref] = "true";
         return true; 
      }
      else
         return false;
   }

   public void startup()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      cmd = trans.addCustomFunction("DEFCO","User_Default_API.Get_Contract","DEFCONTRACT");

      cmd = trans.addCustomFunction("COMPA","Site_API.Get_Company","COMPANY");
      cmd.addReference("DEFCONTRACT", "DEFCO/DATA");

      cmd = trans.addCustomFunction("FLTREP","ACTIVE_SEPARATE_API.Finite_State_Decode__","DBFAULTREPORT");     
      cmd.addParameter("FAULTREPORT","FAULTREPORT");

      trans = mgr.submit(trans);

      sDefContract = trans.getValue("DEFCO/DATA/DEFCONTRACT");
      companyvar = trans.getValue("COMPA/DATA/COMPANY");
      sDbFaultReport = trans.getValue("FLTREP/DATA/DBFAULTREPORT");

      trans.clear();
   }

   public String convertToString(int max)
   {
      if (max ==0)
         return("'" + ret_data_buffer.getBufferAt(max).getValueAt(0) + "'");
      else
         return("'" + ret_data_buffer.getBufferAt(max).getValueAt(0) + "'," + convertToString(max-1));
   }

//-------------------------------------------------
//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void validate()
   {
      int ref;
      String sExecutionTime="";
      String compani=null;
      isSecure = new String[7];
      String val=null;
      String txt="";

      ASPManager mgr = getASPManager();

      val = mgr.readValue("VALIDATE");

      if ("CONTRACT".equals(val))
      {
         cmd = trans.addCustomFunction("COMP","Site_API.Get_Company","COMPANY");
         cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));

         trans = mgr.validate(trans);

         compani = trans.getValue("COMP/DATA/COMPANY");

         txt = (mgr.isEmpty(compani) ? "": (compani))+ "^";
         mgr.responseWrite(txt);
      }
      else if ("REPORTED_BY".equals(val))
      {
         cmd = trans.addCustomFunction("REPBYID","Company_Emp_API.Get_Max_Employee_Id","REPORTED_BY_ID");
         cmd.addParameter("COMPANY");
         cmd.addParameter("REPORTED_BY");

         trans = mgr.validate(trans);

         reportById = trans.getValue("REPBYID/DATA/REPORTED_BY_ID");

         txt = (mgr.isEmpty(reportById) ? "" : (reportById))+ "^";
         mgr.responseWrite(txt);
      }
      else if ("CUST_ADD_ID".equals(val))
      {
         String sCustomerNum1 = mgr.readValue("CUSTOMER_NO","");
         String sAddressId = mgr.readValue("CUST_ADD_ID");

         cmd = trans.addCustomFunction("ADD1","Customer_Info_Address_API.Get_Address1","ADDRESS1");
         cmd.addParameter("CUSTOMER_NO",sCustomerNum1);
         cmd.addParameter("CUST_ADD_ID",sAddressId);


         cmd = trans.addCustomFunction("ADD2","Customer_Info_Address_API.Get_Address2","ADDRESS2");
         cmd.addParameter("CUSTOMER_NO",sCustomerNum1);
         cmd.addParameter("CUST_ADD_ID",sAddressId);

         cmd = trans.addCustomFunction("ADD3","Customer_Info_Address_API.Get_Zip_Code","ADDRESS3");
         cmd.addParameter("CUSTOMER_NO",sCustomerNum1);
         cmd.addParameter("CUST_ADD_ID",sAddressId);


         cmd = trans.addCustomFunction("ADD4","Customer_Info_Address_API.Get_City","ADDRESS4");
         cmd.addParameter("CUSTOMER_NO",sCustomerNum1);
         cmd.addParameter("CUST_ADD_ID",sAddressId);


         cmd = trans.addCustomFunction("ADD5","Customer_Info_Address_API.Get_County","ADDRESS5");
         cmd.addParameter("CUSTOMER_NO",sCustomerNum1);
         cmd.addParameter("CUST_ADD_ID",sAddressId);


         cmd = trans.addCustomFunction("ADD6","Customer_Info_Address_API.Get_State","ADDRESS6");
         cmd.addParameter("CUSTOMER_NO",sCustomerNum1);
         cmd.addParameter("CUST_ADD_ID",sAddressId);


         trans = mgr.validate(trans);

         address1 = trans.getValue("ADD1/DATA/ADDRESS1");
         address2 = trans.getValue("ADD2/DATA/ADDRESS2");
         address3 = trans.getValue("ADD3/DATA/ADDRESS3");
         address4 = trans.getValue("ADD4/DATA/ADDRESS4");
         address5 = trans.getValue("ADD5/DATA/ADDRESS5");
         address6 = trans.getValue("ADD6/DATA/ADDRESS6");

         txt = (mgr.isEmpty(address1) ? "" : (address1))+ "^"+(mgr.isEmpty(address2) ? "" : (address2))+ "^"+(mgr.isEmpty(address3) ? "" : (address3))+ "^"+(mgr.isEmpty(address4) ? "" : (address4))+ "^"+(mgr.isEmpty(address5) ? "" : (address5))+ "^"+(mgr.isEmpty(address6) ? "" : (address6))+ "^";
         mgr.responseWrite(txt);
      }
      else if ("CUSTOMER_NO".equals(val))
      {
         String connTypeDb = mgr.readValue("CONNECTION_TYPE_DB");
         String sTempOrgCode = mgr.readValue("ORG_CODE");         
         String org_code;
         String orgcodedescription;

         String partno = "";
         String serialno = "";
         //Bug 68947, Start   
         sContractId = mgr.readValue("CONTRACT_ID");
         sLineNo = mgr.readValue("LINE_NO");

         if ("VIM".equals(connTypeDb))
         {
            cmd = trans.addCustomCommand("VIMOBJDETS","Active_Separate_API.Get_Vim_Part_Serial");                                
            cmd.addParameter("PART_NO");
            cmd.addParameter("SERIAL_NO");                                
            cmd.addParameter("WO_NO", mgr.readValue("WO_NO"));
            //Bug 68947, End   

            trans = mgr.validate(trans);

            partno = trans.getValue("VIMOBJDETS/DATA/PART_NO");
            serialno = trans.getValue("VIMOBJDETS/DATA/SERIAL_NO");
            trans.clear();

            if ( checksec("VEHICLE_SERIAL_API.Get_Vehicle_By_Id",1,isSecure) )
            {
               if (mgr.isEmpty(partno))
               {
                  cmd = trans.addCustomCommand("GETPARTSERIAL1","VIM_SERIAL_API.Fetch_Object_Details");
                  cmd.addParameter("PART_NO");
                  cmd.addParameter("SERIAL_NO");
                  cmd.addParameter("MCH_CODE");

                  trans = mgr.validate(trans);

                  partno = trans.getValue("GETPARTSERIAL1/DATA/PART_NO");
                  serialno = trans.getValue("GETPARTSERIAL1/DATA/SERIAL_NO");
                  trans.clear();
               }
            }
         }

//      Bug 69047, Start
         else if (("EQUIPMENT".equals(connTypeDb)) || ("CATEGORY".equals(connTypeDb)))
         {
            //Bug Id 70921, Removed the condition added by Bug Id by 68363 
            trans.clear();
            trans.addSecurityQuery("Psc_Contr_Product_API", "Get_Valid_Service_Line");
            trans = mgr.perform(trans);
            if (trans.getSecurityInfo().itemExists("Psc_Contr_Product_API.Get_Valid_Service_Line"))
            {
               trans.clear();
               cmd = trans.addCustomCommand("SEARCHAGRMNT", "Psc_Contr_Product_API.Get_Valid_Service_Line");
               cmd.addParameter("IS_VALID");
               cmd.addParameter("CONTRACT_ID",sContractId); //Bug Id 70921
               cmd.addParameter("LINE_NO",sLineNo); //Bug Id 70921
               cmd.addParameter("ORG_CODE");
               cmd.addParameter("SETTINGTIME");
               cmd.addParameter("AUTHORIZE_CODE");
               cmd.addParameter("CUSTOMER_NO", mgr.readValue("CUSTOMER_NO"));
               cmd.addParameter("CONTRACT", mgr.readValue("CONTRACT"));
               cmd.addParameter("MCH_CODE", mgr.readValue("MCH_CODE"));
               cmd.addParameter("MCH_CODE_CONTRACT", mgr.readValue("MCH_CODE_CONTRACT"));
               cmd.addParameter("WORK_TYPE_ID", mgr.readValue("WORK_TYPE_ID"));
               cmd.addParameter("CURRENCY_CODE", mgr.readValue("CURRENCY_CODE", ""));
               cmd.addParameter("REG_DATE", mgr.readValue("REG_DATE"));
               cmd.addParameter("PLAN_F_DATE", mgr.readValue("PLAN_F_DATE"));
               trans = mgr.validate(trans);
               //Bug Id 70921, Start
               if (("FALSE").equals(trans.getValue("SEARCHAGRMNT/DATA/IS_VALID")) )
               {
                  sContractId = trans.getValue("SEARCHAGRMNT/DATA/CONTRACT_ID");
                  sLineNo = trans.getValue("SEARCHAGRMNT/DATA/LINE_NO");
               }
               //Bug Id 70921, End
            }
            trans.clear();
         }
// Bug 69047, End

         if (checksec("Cust_Ord_Customer_API.Customer_Is_Credit_Stopped",1,isSecure))
         {
            cmd = trans.addCustomFunction("CUSTNO","Cust_Ord_Customer_API.Customer_Is_Credit_Stopped","CUST_NO");
            cmd.addParameter("CUSTOMER_NO");
            cmd.addParameter("COMPANY");
         }

         if (checksec("Customer_Credit_Block_API.Get_Client_Value",2,isSecure))
            cmd = trans.addCustomFunction("CLIENTVAL","Customer_Credit_Block_API.Get_Client_Value(0)","CLIENT_VALUE");

         cmd = trans.addCustomFunction("FREPAGREECLIENT","Fault_Report_Agreement_API.Get_Client_Value(0)","FAULT_REP_AGREE_CLIENT");

         if (checksec("CUSTOMER_INFO_API.Get_Name",5,isSecure))
         {
            cmd = trans.addCustomFunction("CUSTDESC","CUSTOMER_INFO_API.Get_Name","CUSTOMERDESCRIPTION");
            cmd.addParameter("CUSTOMER_NO");
         }

         if ("VIM".equals(connTypeDb))
         {
            if (checksec("Psc_Contr_Product_API.Get_Top_Part_No",3,isSecure) && checksec("Psc_Contr_Product_API.Get_Top_Part_Rev",4,isSecure))
            {
               cmd = trans.addCustomFunction("VIMAGRPARTNODETS","Psc_Contr_Product_API.Get_Top_Part_No", "AGRMNT_PART_NO");                                
               cmd.addParameter("CONTRACT_ID");
               cmd.addParameter("LINE_NO");

               cmd = trans.addCustomFunction("VIMAGRPARTREVDETS","Psc_Contr_Product_API.Get_Top_Part_Rev", "PART_REV");                                
               cmd.addParameter("CONTRACT_ID");
               cmd.addParameter("LINE_NO");

               cmd = trans.addCustomFunction("FREPAGREE","Psc_Contr_Product_API.Get_Fault_Report_Agreement","FAULT_REP_AGREE");
               cmd.addParameter("CONTRACT_ID");
               cmd.addParameter("LINE_NO");             

               if (mgr.isEmpty(sTempOrgCode))
               {
                  cmd = trans.addCustomFunction("ORGCODE","Psc_Contr_Product_API.Get_Work_Shop_Code","ORG_CODE");
                  cmd.addParameter("CONTRACT_ID");
                  cmd.addParameter("LINE_NO");   

                  cmd = trans.addCustomFunction("ORGDESC","Organization_API.Get_Description","ORGCODEDESCRIPTION");
                  cmd.addParameter("CONTRACT");
                  cmd.addReference("ORG_CODE","ORGCODE/DATA");
               }
               else
               {
                  cmd = trans.addCustomFunction("ORGDESC","Organization_API.Get_Description","ORGCODEDESCRIPTION");
                  cmd.addParameter("CONTRACT");
                  cmd.addParameter("ORG_CODE",sTempOrgCode);
               } 
            }
            if (!mgr.isEmpty(partno) && checksec("Psc_Contr_Product_API.Search_For_Agreement",4,isSecure))
            {
               cmd = trans.addCustomCommand("ISHASAGREE","Psc_Contr_Product_API.Search_For_Agreement");
               cmd.addParameter("CBHASAGREEMENT");
               cmd.addParameter("CONTRACT_ID");
               cmd.addParameter("LINE_NO");
               cmd.addParameter("PART_REV","VIMAGRPARTREVDETS/DATA");
               cmd.addParameter("AGRMNT_PART_NO","VIMAGRPARTNODETS/DATA");
               cmd.addParameter("WO_NO");
               cmd.addParameter("PART_NO",partno);
               cmd.addParameter("SERIAL_NO",serialno);
               cmd.addParameter("CUSTOMER_NO", mgr.readValue("CUSTOMER_NO"));
               cmd.addParameter("WORK_TYPE_ID", mgr.readValue("WORK_TYPE_ID"));
               cmd.addParameter("CONTRACT", mgr.readValue("CONTRACT")); 
            }
         }
         else
         {
            if (checksec("Psc_Contr_Product_API.Get_Fault_Report_Agreement",3,isSecure))
            {
               cmd = trans.addCustomFunction("FREPAGREE","Psc_Contr_Product_API.Get_Fault_Report_Agreement","FAULT_REP_AGREE");
               cmd.addParameter("CONTRACT_ID");
               cmd.addParameter("LINE_NO");             

               if (mgr.isEmpty(sTempOrgCode))
               {
                  if (checksec("Psc_Contr_Product_API.Get_Maint_Org",4,isSecure))
                  {
                     cmd = trans.addCustomFunction("ORGCODE","Psc_Contr_Product_API.Get_Maint_Org","ORG_CODE");
                     cmd.addParameter("CONTRACT_ID");
                     cmd.addParameter("LINE_NO");
                  }

                  cmd = trans.addCustomFunction("ORGDESC","Organization_API.Get_Description","ORGCODEDESCRIPTION");
                  cmd.addParameter("CONTRACT");
                  cmd.addReference("ORG_CODE","ORGCODE/DATA");     
               }
               else
               {
                  cmd = trans.addCustomFunction("ORGDESC","Organization_API.Get_Description","ORGCODEDESCRIPTION");
                  cmd.addParameter("CONTRACT");
                  cmd.addParameter("ORG_CODE",sTempOrgCode);     
               }               
            }

            if (checksec("Psc_Contr_Product_API.Has_Contract_For_Object",4,isSecure))
            {
               cmd = trans.addCustomFunction("ISHASAGREE","Psc_Contr_Product_API.Has_Contract_For_Object","CBHASAGREEMENT");
               cmd.addParameter("CONNECTION_TYPE_DB");
               cmd.addParameter("MCH_CODE");
               cmd.addParameter("MCH_CODE_CONTRACT");
               cmd.addParameter("CUSTOMER_NO");
            }
         }

         trans = mgr.validate(trans);

         ref = 0;

         if (isSecure[ref += 1] =="true")
            custno = trans.getValue("CUSTNO/DATA/CUST_NO");
         else
            custno = "";

         if (isSecure[ref += 1] =="true")
            clientvalue = trans.getValue("CLIENTVAL/DATA/CLIENT_VALUE");
         else
            clientvalue = "";

         if (isSecure[ref += 1] =="true")
         {
            frepagree = trans.getValue("FREPAGREE/DATA/FAULT_REP_AGREE");
            orgcodedescription = trans.getValue("ORGDESC/DATA/ORGCODEDESCRIPTION");            

            if (mgr.isEmpty(sTempOrgCode))
               org_code = trans.getValue("ORGCODE/DATA/ORG_CODE");
            else
               org_code    = sTempOrgCode;      
         }
         else
         {
            frepagree = "";
            orgcodedescription = trans.getValue("ORGDESC/DATA/ORGCODEDESCRIPTION");                        
            if (mgr.isEmpty(sTempOrgCode))
            {
               org_code = "";
               orgcodedescription = "";
            }
            else
               org_code    = sTempOrgCode;      
         }   

         frepagreeclient = trans.getValue("FREPAGREECLIENT/DATA/FAULT_REP_AGREE_CLIENT");
         if (isSecure[ref += 1] =="true")
         {
            cbhasagreement = trans.getValue("ISHASAGREE/DATA/CBHASAGREEMENT");
            //Bug Id 70921, Removed Code here
         }
         else
            cbhasagreement  = "";

         if (isSecure[ref += 1] =="true")
            custdesc = trans.getValue("CUSTDESC/DATA/CUSTOMERDESCRIPTION");
         else
            custdesc    = ""; 
//      Bug 69047, Start
         txt = (mgr.isEmpty(custno)? "" : custno) +"^"
               +(mgr.isEmpty(clientvalue)? "" : clientvalue) +"^"
               +(mgr.isEmpty(frepagree)? "" : frepagree) +"^"
               +(mgr.isEmpty(frepagreeclient)? "" : frepagreeclient) +"^"
               +(mgr.isEmpty(cbhasagreement) ? "": (cbhasagreement))+ "^"
               +(mgr.isEmpty(custdesc) ? "" : (custdesc))+"^"
               +(mgr.isEmpty(org_code) ? "": (org_code))+ "^"
               +(mgr.isEmpty(orgcodedescription) ? "" : (orgcodedescription))+"^"
               +(mgr.isEmpty(sContractId) ? "" : sContractId)+ "^"
               +(mgr.isEmpty(sLineNo) ? "" : sLineNo)+ "^";

//      Bug 69047, End
         mgr.responseWrite(txt);
      }
      else if ("AGREEMENT_ID".equals(val))
      {
         sAgreementId1 = mgr.readValue("AGREEMENT_ID");
         nSettingTime1 = (int)mgr.readNumberValue("SETTINGTIME");
         sCustomerNum1 = mgr.readValue("CUSTOMER_NO");
         sRegistDate   = mgr.readValue("REG_DATE");      
         sRequiredStartDate1 = mgr.readValue("REQUIRED_START_DATE");
         sPlanStartDate1 = mgr.readValue("PLAN_S_DATE");  
         sPlanEndDate1 = mgr.readValue("PLAN_F_DATE"); 
         sRequiredEndDate1 = mgr.readValue("REQUIRED_END_DATE");  
         String connTypeDB = mgr.readValue("CONNECTION_TYPE_DB");
         String orgCode    = mgr.readValue("ORG_CODE");
         String strDisplaySettingTime1 = "";

         String partno = "";
         String serialno = "";

         if ("VIM".equals(connTypeDB))
         {
            cmd = trans.addCustomCommand("VIMOBJDETS","Active_Separate_API.Get_Vim_Part_Serial");                                
            cmd.addParameter("PART_NO");
            cmd.addParameter("SERIAL_NO");                                
            cmd.addParameter("WO_NO");  

            trans = mgr.validate(trans);

            partno = trans.getValue("VIMOBJDETS/DATA/PART_NO");
            serialno = trans.getValue("VIMOBJDETS/DATA/SERIAL_NO");
            trans.clear();

            if ( checksec("VEHICLE_SERIAL_API.Get_Vehicle_By_Id",1,isSecure) )
            {

               if (mgr.isEmpty(partno))
               {
                  cmd = trans.addCustomCommand("GETPARTSERIAL1","VEHICLE_SERIAL_API.Get_Vehicle_By_Id");
                  cmd.addParameter("PART_NO");
                  cmd.addParameter("SERIAL_NO");
                  cmd.addParameter("MCH_CODE");

                  trans = mgr.validate(trans);

                  partno = trans.getValue("GETPARTSERIAL1/DATA/PART_NO");
                  serialno = trans.getValue("GETPARTSERIAL1/DATA/SERIAL_NO");
                  trans.clear();
               }
            }
         }
         if ("VIM".equals(connTypeDB))
         {
            if (checksec("Psc_Contr_Product_API.Get_Top_Part_No",2,isSecure) && checksec("Psc_Contr_Product_API.Get_Top_Part_Rev",3,isSecure))
            {
               cmd = trans.addCustomFunction("VIMAGRPARTNODETS","Psc_Contr_Product_API.Get_Top_Part_No","AGRMNT_PART_NO");                                
               cmd.addParameter("CONTRACT_ID");
               cmd.addParameter("LINE_NO");

               cmd = trans.addCustomFunction("VIMAGRPARTREVDETS","Psc_Contr_Product_API.Get_Top_Part_Rev","PART_REV");                                
               cmd.addParameter("CONTRACT_ID");
               cmd.addParameter("LINE_NO");

               cmd = trans.addCustomFunction("FAULTREPAGREE","Psc_Contr_Product_API.Get_Fault_Report_Agreement","FAULT_REP_AGREE");
               cmd.addParameter("CONTRACT_ID");
               cmd.addParameter("LINE_NO");

               if (mgr.isEmpty(orgCode))
               {
                  cmd = trans.addCustomFunction("ORGCODE","Psc_Contr_Product_API.Get_Work_Shop_Code","ORG_CODE");
                  cmd.addParameter("CONTRACT_ID");
                  cmd.addParameter("LINE_NO");

                  cmd = trans.addCustomFunction("ORGDESC","Organization_API.Get_Description","ORGCODEDESCRIPTION");
                  cmd.addParameter("CONTRACT");
                  cmd.addReference("ORG_CODE","ORGCODE/DATA");                                                            
               }
               else
               {
                  cmd = trans.addCustomFunction("ORGDESC","Organization_API.Get_Description","ORGCODEDESCRIPTION");
                  cmd.addParameter("CONTRACT");
                  cmd.addParameter("ORG_CODE");                                                            
               }

               if (checksec("Psc_Contr_Product_API.Get_Setting_Time",4,isSecure))
               {
                  cmd = trans.addCustomFunction("SETTIME1","Psc_Contr_Product_API.Get_Setting_Time","SETTINGTIME");
                  cmd.addParameter("CONTRACT_ID");
                  cmd.addParameter("LINE_NO"); 
               }
            }

            cmd = trans.addCustomFunction("FREPAGREECLIENT","Fault_Report_Agreement_API.Get_Client_Value(0)","FAULT_REP_AGREE_CLIENT");

            cmd = trans.addCustomFunction("AUTHOCODE","Customer_Agreement_API.Get_Authorize_Code","AUTHORIZE_CODE");
            cmd.addParameter("AGREEMENT_ID");

            trans = mgr.validate(trans);


            if (checksec("Psc_Contr_Product_API.Get_Top_Part_No",2,isSecure) && checksec("Psc_Contr_Product_API.Get_Top_Part_Rev",3,isSecure))
            {
               lsFaultReportAgreement = trans.getValue("FAULTREPAGREE/DATA/FAULT_REP_AGREE");

               if (mgr.isEmpty(orgCode))
                  sOrgCode = trans.getValue("ORGCODE/DATA/ORG_CODE");
               else
                  sOrgCode    = orgCode;

               sOrgDesc = trans.getValue("ORGDESC/DATA/ORGCODEDESCRIPTION");                           

               nSettingTime1 = (int)trans.getNumberValue("SETTIME1/DATA/SETTINGTIME");
            }

            lsClientFaultReportAgreement = trans.getValue("FREPAGREECLIENT/DATA/FAULT_REP_AGREE_CLIENT");
            sAuthoCode  = trans.getValue("AUTHOCODE/DATA/AUTHORIZE_CODE");


            /* if (isNaN(nSettingTime1))
             nSettingTime1 = 0;*/

            nSettingTime = nSettingTime1;

            trans.clear();      

            if (checksec("Maintenance_Site_Utility_API.Get_Site_Date",1,isSecure))
            {
               cmd = trans.addCustomFunction("SYDATES","Maintenance_Site_Utility_API.Get_Site_Date","VALSYDATE");
               cmd.addParameter("CONTRACT");
            }
            trans = mgr.validate(trans);
            ref = 0;

            if (isSecure[ref += 1] =="true")
            {
               sSysDate1 = trans.getValue("SYDATES/DATA/VALSYDATE");
               sSysDate  = trans.getBuffer("SYDATES/DATA").getFieldValue("VALSYDATE");
            }
            else
            {
               sSysDate    = "";
               sSysDate1 = "";
            }

            if (nSettingTime == 0)
            {

               if (!mgr.isEmpty(sCustomerNum1))
               {
                  trans.clear();      

                  if (checksec("Maintenance_Site_Utility_API.Get_Site_Date",1,isSecure))
                  {
                     cmd = trans.addCustomFunction("SYDATES","Maintenance_Site_Utility_API.Get_Site_Date","VALSYDATE");
                     cmd.addParameter("CONTRACT");
                  }
                  trans = mgr.validate(trans);
                  ref = 0;

                  if (isSecure[ref += 1] =="true")
                  {
                     sSysDate1 = trans.getValue("SYDATES/DATA/VALSYDATE");
                     sSysDate  = trans.getBuffer("SYDATES/DATA").getFieldValue("VALSYDATE");
                  }
                  else
                  {
                     sSysDate    = "";
                     sSysDate1 = "";
                  }
                  sPlanStartDate1 = sSysDate;
               }
               else
               {
                  if (mgr.isEmpty(sPlanStartDate1))
                     sPlanStartDate1 = "";
                  if (mgr.isEmpty(sRequiredStartDate1))
                     sRequiredStartDate1 = "";
               }
            }
            else
            {
               sPlanStartDate1 = sRegistDate;
               sRequiredStartDate1 = addNumToDate(sPlanStartDate1,(double)nSettingTime);

               if (mgr.isEmpty(sRequiredStartDate1))
               {
                  if (!mgr.isEmpty(sSysDate))
                     sRequiredStartDate1 =  addNumToDate(sSysDate,(double)nSettingTime);
               }
            }

            sPlanEndDate1 =  addNumToDate(sPlanStartDate1,(double)(nSettingTime+nExecutionTime));
            sRequiredEndDate1 = addNumToDate(sRequiredStartDate1,(double)nExecutionTime);

            if (!isNaN(nSettingTime1))
               strDisplaySettingTime1 = mgr.getASPField("SETTINGTIME").formatNumber(nSettingTime1);

            txt = (mgr.isEmpty(sOrgCode) ? mgr.readValue("ORG_CODE",""): sOrgCode)+ "^"+
                  //(mgr.isEmpty(sStandardJob)? mgr.readValue("STD_JOB_ID",""): sStandardJob) +"^"+
                  (mgr.isEmpty(sOrgDesc) ? mgr.readValue("ORGCODEDESCRIPTION",""): sOrgDesc)+"^"+
                  (mgr.isEmpty (sExecutionTime)? "":sExecutionTime)+ "^"+
                  //(mgr.isEmpty(sWorkDesc) ? "": sWorkDesc)+ "^"+
                  (mgr.isEmpty(sAuthoCode) ? "": sAuthoCode)+ "^"+
                  (mgr.isEmpty(lsFaultReportAgreement ) ? "": lsFaultReportAgreement )+ "^"+
                  (mgr.isEmpty(lsClientFaultReportAgreement) ? "": lsClientFaultReportAgreement)+ "^"+
                  ((sPlanStartDate1 == "") ? "":sPlanStartDate1)+"^"+
                  (mgr.isEmpty(sRequiredStartDate1) ? "":sRequiredStartDate1)+"^"+
                  (mgr.isEmpty(strDisplaySettingTime1) ? "":strDisplaySettingTime1)+"^"+
                  (mgr.isEmpty(sPlanEndDate1) ? "":sPlanEndDate1)+"^"+
                  (mgr.isEmpty(sRequiredEndDate1) ? "":sRequiredEndDate1)+"^";
         }
         else
         {
            if (checksec("Psc_Contr_Product_API.Get_Fault_Report_Agreement",4,isSecure))
            {
               cmd = trans.addCustomFunction("FAULTREPAGREE","Psc_Contr_Product_API.Get_Fault_Report_Agreement","FAULT_REP_AGREE");
               cmd.addParameter("CONTRACT_ID");
               cmd.addParameter("LINE_NO");             
            }

            cmd = trans.addCustomFunction("FREPAGREECLIENT","Fault_Report_Agreement_API.Get_Client_Value(0)","FAULT_REP_AGREE_CLIENT");

            if (checksec("Psc_Contr_Product_API.Get_Max_Resolution_Time",4,isSecure))
            {
               cmd = trans.addCustomFunction("EXETIME","Psc_Contr_Product_API.Get_Max_Resolution_Time","PLAN_HRS");
               cmd.addParameter("CONTRACT_ID");
               cmd.addParameter("LINE_NO");             
            }

            cmd = trans.addCustomFunction("AUTHOCODE","Customer_Agreement_API.Get_Authorize_Code","AUTHORIZE_CODE");
            cmd.addParameter("AGREEMENT_ID");

            if (mgr.isEmpty(orgCode))
            {
               if (checksec("Psc_Contr_Product_API.Get_Fault_Report_Agreement",4,isSecure))
               {
                  cmd = trans.addCustomFunction("ORGCODE","Psc_Contr_Product_API.Get_Maint_Org","ORG_CODE");
                  cmd.addParameter("CONTRACT_ID");
                  cmd.addParameter("LINE_NO");
               }

               cmd = trans.addCustomFunction("ORGDESC","Organization_API.Get_Description","ORGCODEDESCRIPTION");
               cmd.addParameter("CONTRACT");
               cmd.addReference("ORG_CODE","ORGCODE/DATA");
            }
            else
            {
               cmd = trans.addCustomFunction("ORGDESC","Organization_API.Get_Description","ORGCODEDESCRIPTION");
               cmd.addParameter("CONTRACT");
               cmd.addParameter("ORG_CODE");
            }   

            if (checksec("Psc_Contr_Product_API.Get_Setting_Time",4,isSecure))
            {
               cmd = trans.addCustomFunction("SETTIME1","Psc_Contr_Product_API.Get_Setting_Time","SETTINGTIME");
               cmd.addParameter("CONTRACT_ID");
               cmd.addParameter("LINE_NO");             
            }
            trans = mgr.validate(trans);

            lsFaultReportAgreement =  trans.getValue("FAULTREPAGREE/DATA/FAULT_REP_AGREE");
            lsClientFaultReportAgreement = trans.getValue("FREPAGREECLIENT/DATA/FAULT_REP_AGREE_CLIENT");

            sAuthoCode  = trans.getValue("AUTHOCODE/DATA/AUTHORIZE_CODE");


            if (mgr.isEmpty(orgCode))
               sOrgCode = trans.getValue("ORGCODE/DATA/ORG_CODE");
            else
               sOrgCode    = orgCode;   
            sOrgDesc = trans.getValue("ORGDESC/DATA/ORGCODEDESCRIPTION");
            // sSettingTime = trans.getBuffer("SETTIME1/DATA").getFieldValue("SETTINGTIME");
            nSettingTime1 = (int)trans.getNumberValue("SETTIME1/DATA/SETTINGTIME");

            /*if (isNaN(nSettingTime1))
                nSettingTime1 = 0;*/


            nSettingTime = nSettingTime1;

            trans.clear();      

            if (checksec("Maintenance_Site_Utility_API.Get_Site_Date",1,isSecure))
            {
               cmd = trans.addCustomFunction("SYDATES","Maintenance_Site_Utility_API.Get_Site_Date","VALSYDATE");
               cmd.addParameter("CONTRACT");
            }
            trans = mgr.validate(trans);
            ref = 0;

            if (isSecure[ref += 1] =="true")
            {
               sSysDate1 = trans.getValue("SYDATES/DATA/VALSYDATE");
               sSysDate  = trans.getBuffer("SYDATES/DATA").getFieldValue("VALSYDATE");
            }
            else
            {
               sSysDate1 = "";
               sSysDate    = "";
            }

            if (nSettingTime == 0)
            {

               if (!mgr.isEmpty(sCustomerNum1))
               {
                  trans.clear();      

                  if (checksec("Maintenance_Site_Utility_API.Get_Site_Date",1,isSecure))
                  {
                     cmd = trans.addCustomFunction("SYDATES","Maintenance_Site_Utility_API.Get_Site_Date","VALSYDATE");
                     cmd.addParameter("CONTRACT");
                  }
                  trans = mgr.validate(trans);
                  ref = 0;

                  if (isSecure[ref += 1] =="true")
                  {
                     sSysDate1 = trans.getValue("SYDATES/DATA/VALSYDATE");
                     sSysDate  = trans.getBuffer("SYDATES/DATA").getFieldValue("VALSYDATE");
                  }
                  else
                  {
                     sSysDate1 = "";
                     sSysDate    = "";
                  }
                  sPlanStartDate1 = sSysDate;

               }
               else
               {
                  if (mgr.isEmpty(sPlanStartDate1))
                     sPlanStartDate1 = "";
                  if (mgr.isEmpty(sRequiredStartDate1))
                     sRequiredStartDate1 = "";
               }
            }
            else
            {

               sPlanStartDate1 = sRegistDate;
               sRequiredStartDate1 = addNumToDate(sPlanStartDate1,(double)nSettingTime);

               if (mgr.isEmpty(sRequiredStartDate1))
               {
                  if (!mgr.isEmpty(sSysDate))
                  {
                     sRequiredStartDate1 =  addNumToDate(sSysDate,(double)nSettingTime);
                  }
               }
            }

            sPlanEndDate1 =  addNumToDate(sPlanStartDate1,(double)(nSettingTime+nExecutionTime));
            sRequiredEndDate1 = addNumToDate(sRequiredStartDate1,(double)nExecutionTime);

            if (!isNaN(nSettingTime1))
               strDisplaySettingTime1 = mgr.getASPField("SETTINGTIME").formatNumber(nSettingTime1);

            txt = (mgr.isEmpty(sOrgCode) ? mgr.readValue("ORG_CODE",""): sOrgCode)+ "^"+
                  //(mgr.isEmpty(sStandardJob)? mgr.readValue("STD_JOB_ID",""): sStandardJob) +"^"+
                  (mgr.isEmpty(sOrgDesc) ? mgr.readValue("ORGCODEDESCRIPTION",""): sOrgDesc)+"^"+
                  (mgr.isEmpty (sExecutionTime)? "":sExecutionTime)+ "^"+
                  //(mgr.isEmpty(sWorkDesc) ? "": sWorkDesc)+ "^"+
                  (mgr.isEmpty(sAuthoCode) ? "": sAuthoCode)+ "^"+
                  (mgr.isEmpty(lsFaultReportAgreement ) ? "": lsFaultReportAgreement )+ "^"+
                  (mgr.isEmpty(lsClientFaultReportAgreement) ? "": lsClientFaultReportAgreement)+ "^"+
                  ((sPlanStartDate1 == "") ? "":sPlanStartDate1)+"^"+
                  (mgr.isEmpty(sRequiredStartDate1) ? "":sRequiredStartDate1)+"^"+
                  (mgr.isEmpty(strDisplaySettingTime1) ? "":strDisplaySettingTime1)+"^"+
                  (mgr.isEmpty(sPlanEndDate1) ? "":sPlanEndDate1)+"^"+
                  (mgr.isEmpty(sRequiredEndDate1) ? "":sRequiredEndDate1)+"^";
         }

         mgr.responseWrite(txt);
      }
      else if ("WORK_TYPE_ID".equals(val))
      {
         nSettingTime1 = (int)mgr.readNumberValue("SETTINGTIME");
         sCustomerNum1 = mgr.readValue("CUSTOMER_NO","");
         sRegistDate   = mgr.readValue("REG_DATE",""); 
         sRequiredStartDate1 = mgr.readValue("REQUIRED_START_DATE","");
         sPlanStartDate1 = mgr.readValue("PLAN_S_DATE");         
         String connTypeDB = mgr.readValue("CONNECTION_TYPE_DB");
         String sTempOrgCode = mgr.readValue("ORG_CODE");
         String org_code = "";
         String partno = "";
         String serialno = "";

         if ("VIM".equals(connTypeDB))
         {
            cmd = trans.addCustomCommand("VIMOBJDETS","Active_Separate_API.Get_Vim_Part_Serial");                                
            cmd.addParameter("PART_NO");
            cmd.addParameter("SERIAL_NO");                                
            cmd.addParameter("WO_NO");

            trans = mgr.validate(trans);

            partno = trans.getValue("VIMOBJDETS/DATA/PART_NO");
            serialno = trans.getValue("VIMOBJDETS/DATA/SERIAL_NO");
            trans.clear();

            if ( checksec("VEHICLE_SERIAL_API.Get_Vehicle_By_Id",1,isSecure) )
            {

               if (mgr.isEmpty(partno))
               {

                  cmd = trans.addCustomCommand("GETPARTSERIAL1","VIM_SERIAL_API.Fetch_Object_Details");
                  cmd.addParameter("PART_NO");
                  cmd.addParameter("SERIAL_NO");
                  cmd.addParameter("MCH_CODE");

                  trans = mgr.validate(trans);

                  partno = trans.getValue("GETPARTSERIAL1/DATA/PART_NO");
                  serialno = trans.getValue("GETPARTSERIAL1/DATA/SERIAL_NO");
                  trans.clear();
               }
            }

            //  Bug 68947, Start
            if (!mgr.isEmpty(partno) && checksec("Psc_Contr_Product_API.Search_For_Agreement",4,isSecure))
            {
               trans.clear();
               cmd = trans.addCustomCommand("ISHASAGREE","Psc_Contr_Product_API.Search_For_Agreement");
               cmd.addParameter("CBHASAGREEMENT");
               cmd.addParameter("CONTRACT_ID");
               cmd.addParameter("LINE_NO");
               cmd.addParameter("PART_REV","VIMAGRPARTREVDETS/DATA");
               cmd.addParameter("AGRMNT_PART_NO","VIMAGRPARTNODETS/DATA");
               cmd.addParameter("WO_NO");
               cmd.addParameter("PART_NO",partno);
               cmd.addParameter("SERIAL_NO",serialno);
               cmd.addParameter("CUSTOMER_NO", mgr.readValue("CUSTOMER_NO"));
               cmd.addParameter("WORK_TYPE_ID", mgr.readValue("WORK_TYPE_ID"));
               cmd.addParameter("CONTRACT", mgr.readValue("CONTRACT")); 
               trans = mgr.validate(trans);
               sContractId = trans.getValue("ISHASAGREE/DATA/CONTRACT_ID");
               sLineNo = trans.getValue("ISHASAGREE/DATA/LINE_NO");
               trans.clear();
            }
            //  Bug 68947, End
         }

//      Bug 69047, Start
         else if (("EQUIPMENT".equals(connTypeDB))||("CATEGORY".equals(connTypeDB)))
         {
            trans.clear();
            trans.addSecurityQuery("Psc_Contr_Product_API","Get_Valid_Service_Line");
            trans = mgr.perform(trans);
            if (trans.getSecurityInfo().itemExists("Psc_Contr_Product_API.Get_Valid_Service_Line"))
            {
               trans.clear();
               cmd = trans.addCustomCommand("SEARCHAGRMNT","Psc_Contr_Product_API.Get_Valid_Service_Line");
               cmd.addParameter("IS_VALID"); //Bug Id 70921
               cmd.addParameter("CONTRACT_ID");
               cmd.addParameter("LINE_NO");
               cmd.addParameter("ORG_CODE");
               cmd.addParameter("SETTINGTIME");
               cmd.addParameter("AUTHORIZE_CODE");
               cmd.addParameter("CUSTOMER_NO",mgr.readValue("CUSTOMER_NO"));
               cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
               cmd.addParameter("MCH_CODE",mgr.readValue("MCH_CODE"));
               cmd.addParameter("MCH_CODE_CONTRACT", mgr.readValue("MCH_CODE_CONTRACT"));
               cmd.addParameter("WORK_TYPE_ID",mgr.readValue("WORK_TYPE_ID"));
               cmd.addParameter("CURRENCY_CODE",mgr.readValue("CURRENCY_CODE",""));
               cmd.addParameter("REG_DATE",mgr.readValue("REG_DATE"));
               cmd.addParameter("PLAN_F_DATE",mgr.readValue("PLAN_F_DATE"));
               trans = mgr.validate(trans);
               sContractId = trans.getValue("SEARCHAGRMNT/DATA/CONTRACT_ID");
               sLineNo = trans.getValue("SEARCHAGRMNT/DATA/LINE_NO");
            }
            trans.clear();
         }
//      Bug 69047, End

         cmd = trans.addCustomFunction("FREPAGREECLIENT","Fault_Report_Agreement_API.Get_Client_Value(0)","FAULT_REP_AGREE_CLIENT");

         if ("VIM".equals(connTypeDB))
         {
            cmd = trans.addCustomFunction("WORKRYPE","WORK_TYPE_API.Get_Description","WORKTYPEDESCRIPTION");
            cmd.addParameter("WORK_TYPE_ID");

            if (checksec("Psc_Contr_Product_API.Get_Top_Part_No",1,isSecure) && checksec("Psc_Contr_Product_API.Get_Top_Part_Rev",2,isSecure))
            {
               cmd = trans.addCustomFunction("VIMAGRPARTNODETS","Psc_Contr_Product_API.Get_Top_Part_No","AGRMNT_PART_NO");                                
               cmd.addParameter("CONTRACT_ID");
               cmd.addParameter("LINE_NO");

               cmd = trans.addCustomFunction("VIMAGRPARTREVDETS","Psc_Contr_Product_API.Get_Top_Part_Rev","PART_REV");                                
               cmd.addParameter("CONTRACT_ID");
               cmd.addParameter("LINE_NO");

               cmd = trans.addCustomFunction("FREPAGREE","Psc_Contr_Product_API.Get_Fault_Report_Agreement","FAULT_REP_AGREE");
               cmd.addParameter("CONTRACT_ID");
               cmd.addParameter("LINE_NO");    

               // Was here

               if (mgr.isEmpty(sTempOrgCode))
               {
                  cmd = trans.addCustomFunction("ORGCODE","Psc_Contr_Product_API.Get_Work_Shop_Code","ORG_CODE");
                  cmd.addParameter("CONTRACT_ID");
                  cmd.addParameter("LINE_NO");    

                  cmd = trans.addCustomFunction("ORGDESC","Organization_API.Get_Description","ORGCODEDESCRIPTION");
                  cmd.addParameter("CONTRACT");
                  cmd.addReference("ORG_CODE","ORGCODE/DATA");
               }
               else
               {
                  cmd = trans.addCustomFunction("ORGDESC","Organization_API.Get_Description","ORGCODEDESCRIPTION");
                  cmd.addParameter("CONTRACT");
                  cmd.addParameter("ORG_CODE",sOrgCode);
               } 
            }

            trans = mgr.validate(trans);
            ref = 0;

            frepagreeclient = trans.getValue("FREPAGREECLIENT/DATA/FAULT_REP_AGREE_CLIENT");

            if (isSecure[ref += 1] =="true" && isSecure[ref += 1] =="true")
            {
               frepagree = trans.getValue("FREPAGREE/DATA/FAULT_REP_AGREE");               
               //stdjobid = trans.getValue("STDJOBID/DATA/STD_JOB_ID");    

               if (mgr.isEmpty(sTempOrgCode))
                  org_code = trans.getValue("ORGCODE/DATA/ORG_CODE");
               else
                  org_code    = sTempOrgCode;   

               orgcodedescription = trans.getValue("ORGDESC/DATA/ORGCODEDESCRIPTION");
            }
            else
            {
               frepagree = "";
               //stdjobid = "";
               org_code = "";              
               orgcodedescription = "";
               sExecutionTime = "";
            }   

            worktypedescription = trans.getValue("WORKRYPE/DATA/WORKTYPEDESCRIPTION");
            nSettingTime = nSettingTime1;
         }
         else
         {
            if (checksec("Psc_Contr_Product_API.Get_Fault_Report_Agreement",1,isSecure))
            {
               cmd = trans.addCustomFunction("FREPAGREE","Psc_Contr_Product_API.Get_Fault_Report_Agreement","FAULT_REP_AGREE");
               cmd.addParameter("CONTRACT_ID");
               cmd.addParameter("LINE_NO");             
            }

            if (mgr.isEmpty(sTempOrgCode))
            {
               if (checksec("Psc_Contr_Product_API.Get_Maint_Org",2,isSecure))
               {
                  cmd = trans.addCustomFunction("ORGCODE","Psc_Contr_Product_API.Get_Maint_Org","ORG_CODE");
                  cmd.addParameter("CONTRACT_ID");
                  cmd.addParameter("LINE_NO");
               }
               cmd = trans.addCustomFunction("ORGDESC","Organization_API.Get_Description","ORGCODEDESCRIPTION");
               cmd.addParameter("CONTRACT");
               cmd.addReference("ORG_CODE","ORGCODE/DATA");
            }
            else
            {
               cmd = trans.addCustomFunction("ORGDESC","Organization_API.Get_Description","ORGCODEDESCRIPTION");
               cmd.addParameter("CONTRACT");
               cmd.addParameter("ORG_CODE",sTempOrgCode);
            }   

            cmd = trans.addCustomFunction("WORKRYPE","WORK_TYPE_API.Get_Description","WORKTYPEDESCRIPTION");
            cmd.addParameter("WORK_TYPE_ID");

            //Bug Id 70921, Removed Code here.

            trans = mgr.validate(trans);
            ref = 0;

            frepagreeclient = trans.getValue("FREPAGREECLIENT/DATA/FAULT_REP_AGREE_CLIENT");

            if (isSecure[ref += 1] =="true")
               frepagree = trans.getValue("FREPAGREE/DATA/FAULT_REP_AGREE");
            else
               frepagree = "";

            /*if (isSecure[ref += 1] =="true")
                stdjobid = trans.getValue("STDJOBID/DATA/STD_JOB_ID");
            else
                stdjobid = "";*/

            if (mgr.isEmpty(sTempOrgCode))
            {
               if (isSecure[ref += 1] =="true")
                  org_code = trans.getValue("ORGCODE/DATA/ORG_CODE");
               else
                  org_code    = "";
            }
            else
            {
               org_code    = sTempOrgCode;
               //Bug 69819, Start
               ref += 1;
               //Bug 69819, End
            }

            orgcodedescription = trans.getValue("ORGDESC/DATA/ORGCODEDESCRIPTION");
            worktypedescription = trans.getValue("WORKRYPE/DATA/WORKTYPEDESCRIPTION");

            //Bug Id 70921, Removed Code here.

            nSettingTime = nSettingTime1;
         }   

         trans.clear();      


         if (checksec("Maintenance_Site_Utility_API.Get_Site_Date",1,isSecure))
         {
            cmd = trans.addCustomFunction("SYDATES","Maintenance_Site_Utility_API.Get_Site_Date","VALSYDATE");
            cmd.addParameter("CONTRACT");
         }

         trans = mgr.validate(trans);
         ref = 0;


         if (isSecure[ref += 1] == "true")
         {
            sSysDate1 = trans.getValue("SYDATES/DATA/VALSYDATE");
            sSysDate = trans.getBuffer("SYDATES/DATA").getFieldValue("VALSYDATE");
         }
         else
         {
            sSysDate    = "";
            sSysDate1 = "";
         }

         if (nSettingTime1 == 0)
         {
            nSettingTime = 0;
         }
         //Bug Id 70921, Removed Code here
         //String stdjob_ = mgr.readValue("STD_JOB_ID","");
         String orgcode_ = mgr.readValue("ORG_CODE","");
         String orgdesc_ = mgr.readValue("ORGCODEDESCRIPTION","");
//      Bug 69047, Start
         txt =   (mgr.isEmpty(frepagreeclient) ? "" : (frepagreeclient))+ "^"+
                 (mgr.isEmpty(frepagree) ? "" : (frepagree))+ "^"+
                 //(mgr.isEmpty(stdjobid) ? stdjob_ : (stdjobid))+"^"+
                 (mgr.isEmpty(org_code) ? orgcode_ : (org_code))+ "^"+
                 (mgr.isEmpty(orgcodedescription) ? orgdesc_ : (orgcodedescription))+"^"+
                 (mgr.isEmpty(worktypedescription) ? "" : (worktypedescription))+ "^"+
                 //Bug Id 70921, Removed PlanSDate and RequiredSDate
                 (mgr.isEmpty(sContractId) ? "" : sContractId)+ "^"+
                 (mgr.isEmpty(sLineNo) ? "" : sLineNo)+ "^";
//      Bug 69047, End

         mgr.responseWrite(txt);
      }
      else if ("MCH_CODE".equals(val))
      {
         agreemid = (mgr.readValue("AGREEMENT_ID",""));
         String connTypeDB = (mgr.readValue("CONNECTION_TYPE_DB",""));
         String org_code = (mgr.readValue("ORG_CODE",""));
         String sTempOrgCode = (mgr.readValue("ORG_CODE",""));
         orgcodedescription = mgr.readValue("ORGCODEDESCRIPTION","");
         authcode = mgr.readValue("AUTHORIZE_CODE","");
         sCustomerNum1 = mgr.readValue("CUSTOMER_NO","");
         sRegistDate   = mgr.readValue("REG_DATE","");
         sRequiredStartDate1 = mgr.readValue("REQUIRED_START_DATE","");
         sPlanStartDate1 = mgr.readValue("PLAN_S_DATE","");
         String sPlanEndDate1 = mgr.readValue("PLAN_F_DATE","");
         String sRequiredEndDate1 = mgr.readValue("REQUIRED_END_DATE","");
         sPlanStartDate1 = mgr.readValue("PLAN_S_DATE");  

         if (mgr.isEmpty(mgr.readValue("SETTINGTIME")))
            nSettingTime1 = 0;
         else
            nSettingTime1 = (int)mgr.readNumberValue("SETTINGTIME");

         String validationAttrAtr = "";
         String validationAttrAtr1 = "";
         String mchCode = "";
         String mchContract = "";
         String partno = "";
         String serialno = "";
         String cbSupWar = "";
         String cbCustWar = "";
         double nExecutionTime =0.0;
         machname ="";
         trans.clear();

         if (("EQUIPMENT".equals(connTypeDB))||("CATEGORY".equals(connTypeDB)))
         {
            if (mgr.readValue("MCH_CODE").indexOf("~") > -1)
            {
               mchCode = mgr.readValue("MCH_CODE").substring(0,mgr.readValue("MCH_CODE").indexOf("~"));       
               validationAttrAtr1 = mgr.readValue("MCH_CODE").substring(mgr.readValue("MCH_CODE").indexOf("~")+1,mgr.readValue("MCH_CODE").length());
               machname =  validationAttrAtr1.substring(0,validationAttrAtr1.indexOf("~"));
               mchContract =  validationAttrAtr1.substring(validationAttrAtr1.indexOf("~")+1,validationAttrAtr1.length());                
            }
            else
            {
               mchCode = mgr.readValue("MCH_CODE");

               trans.clear();
               if (mgr.isEmpty(mgr.readValue("MCH_CODE_CONTRACT")))
               {
                  cmd = trans.addCustomFunction("MACHCONTRACT","EQUIPMENT_OBJECT_API.Get_Def_Contract","MCH_CODE_CONTRACT");
                  cmd.addParameter("MCH_CODE");

                  trans = mgr.validate(trans);

                  mchContract = trans.getValue("MACHCONTRACT/DATA/MCH_CODE_CONTRACT");
                  trans.clear();
               }
               else
                  mchContract = mgr.readValue("MCH_CODE_CONTRACT");
            }
            cmd = trans.addCustomFunction("GETSUPWAR","Object_Supplier_Warranty_API.Has_Warranty","CBWARRANTYONOBJECT");
            cmd.addParameter("MCH_CODE_CONTRACT",mchContract);
            cmd.addParameter("MCH_CODE",mchCode);
            cmd.addParameter("REG_DATE");

            cmd = trans.addCustomFunction("GETCUSTWAR","Object_Cust_Warranty_API.Has_Warranty","CBHASCUSTWARRANTYONOBJ");
            cmd.addParameter("MCH_CODE_CONTRACT",mchContract);
            cmd.addParameter("MCH_CODE",mchCode);
            cmd.addParameter("REG_DATE");

            trans = mgr.validate(trans);
            cbSupWar = trans.getValue("GETSUPWAR/DATA/CBWARRANTYONOBJECT");
            cbCustWar = trans.getValue("GETCUSTWAR/DATA/CBHASCUSTWARRANTYONOBJ");
            trans.clear();

//      Bug 69047, Start
            trans.addSecurityQuery("Psc_Contr_Product_API","Get_Valid_Service_Line");
            trans = mgr.perform(trans);
            if (trans.getSecurityInfo().itemExists("Psc_Contr_Product_API.Get_Valid_Service_Line"))
            {
               trans.clear();
               cmd = trans.addCustomCommand("SEARCHAGRMNT","Psc_Contr_Product_API.Get_Valid_Service_Line");
               cmd.addParameter("IS_VALID"); //Bug Id 70921
               cmd.addParameter("CONTRACT_ID");
               cmd.addParameter("LINE_NO");
               cmd.addParameter("ORG_CODE");
               cmd.addParameter("SETTINGTIME");
               cmd.addParameter("AUTHORIZE_CODE");
               cmd.addParameter("CUSTOMER_NO",mgr.readValue("CUSTOMER_NO"));
               cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
               cmd.addParameter("MCH_CODE",mchCode);
               cmd.addParameter("MCH_CODE_CONTRACT", mchContract);
               cmd.addParameter("WORK_TYPE_ID",mgr.readValue("WORK_TYPE_ID"));
               cmd.addParameter("CURRENCY_CODE",mgr.readValue("CURRENCY_CODE",""));
               cmd.addParameter("REG_DATE",mgr.readValue("REG_DATE"));
               cmd.addParameter("PLAN_F_DATE",mgr.readValue("PLAN_F_DATE"));
               trans = mgr.validate(trans);
               //Bug Id 70921, Start
               if (("FALSE").equals(trans.getValue("SEARCHAGRMNT/DATA/IS_VALID")) )
               {
                  sContractId = trans.getValue("SEARCHAGRMNT/DATA/CONTRACT_ID");
                  sLineNo = trans.getValue("SEARCHAGRMNT/DATA/LINE_NO");
               }
               //Bug Id 70921, End
            }
            trans.clear();
//      Bug 69047, End
         }
         else if ("VIM".equals(connTypeDB))
         {
            if (mgr.readValue("MCH_CODE").indexOf("~") > -1)
            {
               mchCode = mgr.readValue("MCH_CODE").substring(0,mgr.readValue("MCH_CODE").indexOf("~"));       
               validationAttrAtr1 = mgr.readValue("MCH_CODE").substring(mgr.readValue("MCH_CODE").indexOf("~")+1,mgr.readValue("MCH_CODE").length());
               partno =  validationAttrAtr1.substring(0,validationAttrAtr1.indexOf("~"));
               serialno =  validationAttrAtr1.substring(validationAttrAtr1.indexOf("~")+1,validationAttrAtr1.length());                
            }
            else
            {
               mchCode = mgr.readValue("MCH_CODE");
               cmd = trans.addCustomCommand("GETPARTSERIAL","Active_Separate_API.Get_Vim_Part_Serial");
               cmd.addParameter("PART_NO");
               cmd.addParameter("SERIAL_NO");
               cmd.addParameter("WO_NO");

               trans = mgr.validate(trans);

               partno = trans.getValue("GETPARTSERIAL/DATA/PART_NO");
               serialno = trans.getValue("GETPARTSERIAL/DATA/SERIAL_NO");
               trans.clear();

               if (mgr.isEmpty(partno))
               {

                  if (checksec("VEHICLE_SERIAL_API.Get_Vehicle_By_Id",1,isSecure))
                  {
                     cmd = trans.addCustomCommand("GETPARTSERIAL1","VEHICLE_SERIAL_API.Get_Vehicle_By_Id");
                     cmd.addParameter("PART_NO");
                     cmd.addParameter("SERIAL_NO");
                     cmd.addParameter("MCH_CODE");

                     trans = mgr.validate(trans);

                     partno = trans.getValue("GETPARTSERIAL1/DATA/PART_NO");
                     serialno = trans.getValue("GETPARTSERIAL1/DATA/SERIAL_NO");
                     trans.clear();
                  }
               }
            }

            cmd = trans.addCustomFunction("GETSUPWAR","Part_Serial_Catalog_API.Get_Sup_Warranty_Id","CBWARRANTYONOBJECT");
            cmd.addParameter("PART_NO",partno);
            cmd.addParameter("SERIAL_NO",serialno);

            cmd = trans.addCustomFunction("GETCUSTWAR","Part_Serial_Catalog_API.Get_Cust_Warranty_Id","CBHASCUSTWARRANTYONOBJ");
            cmd.addParameter("PART_NO",partno);
            cmd.addParameter("SERIAL_NO",serialno);

            trans = mgr.validate(trans);
            cbSupWar = trans.getValue("GETSUPWAR/DATA/CBWARRANTYONOBJECT");
            cbCustWar = trans.getValue("GETCUSTWAR/DATA/CBHASCUSTWARRANTYONOBJ");
            trans.clear();

            if (checksec("Vehicle_Serial_Part_API.Get_Vehicle_Id_As_Obj_Id",1,isSecure))
            {
               cmd = trans.addCustomFunction("VEHICLEIDAS", "Vehicle_Serial_Part_API.Get_Vehicle_Id_As_Obj_Id","VEHICLE");
               cmd.addParameter("PART_NO", partno);
               trans = mgr.validate(trans);

               String vehAsMchCode = trans.getValue("VEHICLEIDAS/DATA/VEHICLE");
               trans.clear();

               if ("TRUE".equals(vehAsMchCode) && checksec("Vehicle_Serial_API.Get_Vehicle_Id",1,isSecure))
               {
                  cmd = trans.addCustomFunction("VEHICLEID", "Vehicle_Serial_API.Get_Vehicle_Id", "VEHICLEID");
                  cmd.addParameter("PART_NO", partno);
                  cmd.addParameter("SERIAL_NO", serialno);

                  trans = mgr.validate(trans);
                  mchCode = trans.getValue("VEHICLEID/DATA/VEHICLEID");
                  trans.clear();
               }
            }
         }

         if (!mgr.isEmpty(agreemid))
         {
            settingtime = 0;
            sExecutionTime="";

            if (checksec("Psc_Contr_Product_API.Get_Setting_Time",1,isSecure) && checksec("Psc_Contr_Product_API.Get_Max_Resolution_Time",1,isSecure) )
            {
               cmd = trans.addCustomFunction("SETTIME","Psc_Contr_Product_API.Get_Setting_Time","SETTINGTIME");
               cmd.addParameter("CONTRACT_ID",mgr.readValue("CONTRACT_ID"));
               cmd.addParameter("LINE_NO",mgr.readValue("LINE_NO"));

               cmd = trans.addCustomFunction("EXTIME","Psc_Contr_Product_API.Get_Max_Resolution_Time","PLAN_HRS");
               cmd.addParameter("CONTRACT_ID",mgr.readValue("CONTRACT_ID"));
               cmd.addParameter("LINE_NO",mgr.readValue("LINE_NO"));
            }
         }

         if ("VIM".equals(connTypeDB))
         {
            cmd = trans.addCustomFunction("MACHNAME","Part_Catalog_API.Get_Description","MCH_CODE_DESCRIPTION");
            cmd.addParameter("PART_NO",partno);
            if (!mgr.isEmpty(partno) && checksec("Psc_Contr_Product_API.Search_For_Agreement",1,isSecure) )
            {
               cmd = trans.addCustomCommand("SEARCHAGRMNT","Psc_Contr_Product_API.Search_For_Agreement");
               cmd.addParameter("CBHASAGREEMENT");
               cmd.addParameter("CONTRACT_ID");
               cmd.addParameter("LINE_NO");
               cmd.addParameter("PART_REV");
               cmd.addParameter("AGRMNT_PART_NO");
               cmd.addParameter("WO_NO");
               cmd.addParameter("PART_NO",partno);
               cmd.addParameter("SERIAL_NO",serialno);
               cmd.addParameter("CUSTOMER_NO",mgr.readValue("CUSTOMER_NO"));
               cmd.addParameter("WORK_TYPE_ID");
               cmd.addParameter("CONTRACT"); 
            }
         }
         else
         {
            cmd = trans.addCustomFunction("MACHNAME","MAINTENANCE_OBJECT_API.Get_Mch_Name","MCH_CODE_DESCRIPTION");
            cmd.addParameter("MCH_CODE_CONTRACT",mchContract);
            cmd.addParameter("MCH_CODE",mchCode);

            if (checksec("Psc_Contr_Product_API.Get_Fault_Report_Agreement",4,isSecure))
            {
               cmd = trans.addCustomFunction("ISHASAGREE","Psc_Contr_Product_API.Has_Contract_For_Object","CBHASAGREEMENT");
               cmd.addParameter("CONNECTION_TYPE_DB");
               cmd.addParameter("MCH_CODE");
               cmd.addParameter("MCH_CODE_CONTRACT");
               cmd.addParameter("CUSTOMER_NO");
            }
            /*cmd = trans.addCustomFunction("AGREEID","Cust_Agreement_Object_API.Get_Valid_Agreement","AGREEMENT_ID");                
            cmd.addParameter("CUSTOMER_NO");
                      cmd.addParameter("MCH_CODE_CONTRACT",mchContract);
                      cmd.addParameter("MCH_CODE",mchCode);
                      cmd.addParameter("WORK_TYPE_ID");
                      cmd.addParameter("REG_DATE"); */
         }         

         cmd = trans.addCustomFunction("HASWO","Object_Supplier_Warranty_API.Has_Warranty","CBWARRANTYONOBJECT");
         cmd.addParameter("MCH_CODE_CONTRACT",mchContract);
         cmd.addParameter("MCH_CODE",mchCode);
         cmd.addParameter("REG_DATE");

         cmd = trans.addCustomFunction("ISHASACTWO","Active_Work_Order_API.Obj_Has_Wo","CBHASACTIVEWO");
         cmd.addParameter("MCH_CODE_CONTRACT",mchContract);
         cmd.addParameter("MCH_CODE",mchCode);

         cmd = trans.addCustomFunction("ADDID","Equipment_Object_Address_API.Get_Default_Address_Id","ADDRESS_ID");
         cmd.addParameter("MCH_CODE_CONTRACT",mchContract);
         cmd.addParameter("MCH_CODE",mchCode);

         cmd = trans.addCustomFunction("ADD1","Equipment_Object_Address_API.Get_Address1","ADDRESS1");
         cmd.addParameter("MCH_CODE_CONTRACT",mchContract);
         cmd.addParameter("MCH_CODE",mchCode);
         cmd.addReference("ADDRESS_ID","ADDID/DATA");

         cmd = trans.addCustomFunction("ADD2","Equipment_Object_Address_API.Get_Address2","ADDRESS2");
         cmd.addParameter("MCH_CODE_CONTRACT",mchContract);
         cmd.addParameter("MCH_CODE",mchCode);
         cmd.addReference("ADDRESS_ID","ADDID/DATA");

         cmd = trans.addCustomFunction("ADD3","Equipment_Object_Address_API.Get_Address3","ADDRESS3");
         cmd.addParameter("MCH_CODE_CONTRACT",mchContract);
         cmd.addParameter("MCH_CODE",mchCode);
         cmd.addReference("ADDRESS_ID","ADDID/DATA");

         cmd = trans.addCustomFunction("ADD4","Equipment_Object_Address_API.Get_Address4","ADDRESS4");
         cmd.addParameter("MCH_CODE_CONTRACT",mchContract);
         cmd.addParameter("MCH_CODE",mchCode);
         cmd.addReference("ADDRESS_ID","ADDID/DATA");

         cmd = trans.addCustomFunction("ADD5","Equipment_Object_Address_API.Get_Address5","ADDRESS5");
         cmd.addParameter("MCH_CODE_CONTRACT",mchContract);
         cmd.addParameter("MCH_CODE",mchCode);
         cmd.addReference("ADDRESS_ID","ADDID/DATA");

         cmd = trans.addCustomFunction("ADD6","Equipment_Object_Address_API.Get_Address6","ADDRESS6");
         cmd.addParameter("MCH_CODE_CONTRACT",mchContract);
         cmd.addParameter("MCH_CODE",mchCode);
         cmd.addReference("ADDRESS_ID","ADDID/DATA");     

         trans = mgr.validate(trans);

         machname = trans.getValue("MACHNAME/DATA/MCH_CODE_DESCRIPTION");
         cbWarrantyOnObject  = trans.getValue("HASWO/DATA/CBWARRANTYONOBJECT");
         cbhasactivewo = trans.getValue("ISHASACTWO/DATA/CBHASACTIVEWO");
         addId = trans.getValue("ADDID/DATA/ADDRESS_ID");
         address1 = trans.getValue("ADD1/DATA/ADDRESS1");
         address2 = trans.getValue("ADD2/DATA/ADDRESS2");
         address3 = trans.getValue("ADD3/DATA/ADDRESS3");
         address4 = trans.getValue("ADD4/DATA/ADDRESS4");
         address5 = trans.getValue("ADD5/DATA/ADDRESS5");
         address6 = trans.getValue("ADD6/DATA/ADDRESS6");

         if ("VIM".equals(connTypeDB))
         {
            if ( "true".equals(isSecure[1]) )
            {
               cbhasagreement = trans.getValue("SEARCHAGRMNT/DATA/CBHASAGREEMENT");             
               agreemid1 = trans.getValue("SEARCHAGRMNT/DATA/AGREEMENT_ID");
               sContractId = trans.getValue("SEARCHAGRMNT/DATA/CONTRACT_ID"); 
               sLineNo = trans.getValue("SEARCHAGRMNT/DATA/LINE_NO"); 
            }
         }
         else
         {
            cbhasagreement = trans.getValue("ISHASAGREE/DATA/CBHASAGREEMENT");             
            agreemid1 = trans.getValue("AGREEID/DATA/AGREEMENT_ID"); 
         }

         ref = 0;

         if (( isSecure[ref += 1] =="true" ) && !mgr.isEmpty(agreemid))
         {
            settingtime = (int)trans.getNumberValue("SETTIME/DATA/SETTINGTIME");
            sExecutionTime = trans.getBuffer("EXTIME/DATA").getFieldValue("PLAN_HRS");
         }

         if (mgr.isEmpty(agreemid1) && !mgr.isEmpty(agreemid))
            agreemid1 = agreemid;

         if (!mgr.isEmpty(agreemid1))
         {
            trans.clear();

            cmd = trans.addCustomFunction("AUTHCODE","Customer_Agreement_API.Get_Authorize_Code","AUTHORIZE_CODE");
            cmd.addParameter("AGREEMENT_ID",agreemid1);     

            if ("VIM".equals(connTypeDB))
            {
               if (checksec("Psc_Contr_Product_API.Get_Top_Part_No",2,isSecure) && checksec("Psc_Contr_Product_API.Get_Top_Part_Rev",3,isSecure))
               {
                  cmd = trans.addCustomFunction("VIMAGRPARTNODETS","Psc_Contr_Product_API.Get_Top_Part_No","AGRMNT_PART_NO");                                
                  cmd.addParameter("CONTRACT_ID");
                  cmd.addParameter("LINE_NO");

                  cmd = trans.addCustomFunction("VIMAGRPARTREVDETS","Psc_Contr_Product_API.Get_Top_Part_Rev","PART_REV");                                
                  cmd.addParameter("CONTRACT_ID");
                  cmd.addParameter("LINE_NO");              
               }

               if (mgr.isEmpty(sTempOrgCode))
               {
                  if (checksec("Psc_Contr_Product_API.Get_Work_Shop_Code",3,isSecure))
                  {
                     cmd = trans.addCustomFunction("ORGCODE","Psc_Contr_Product_API.Get_Work_Shop_Code","ORG_CODE");
                     cmd.addParameter("CONTRACT_ID");
                     cmd.addParameter("LINE_NO");

                     cmd = trans.addCustomFunction("ORGDESC","Organization_API.Get_Description","ORGCODEDESCRIPTION");
                     cmd.addParameter("CONTRACT");
                     cmd.addReference("ORG_CODE","ORGCODE/DATA");
                  }
               }
               else
               {
                  cmd = trans.addCustomFunction("ORGDESC","Organization_API.Get_Description","ORGCODEDESCRIPTION");
                  cmd.addParameter("CONTRACT");
                  cmd.addParameter("ORG_CODE",sTempOrgCode);
               } 

               if ( checksec("Psc_Contr_Product_API.Get_Setting_Time",4,isSecure) )
               {
                  cmd = trans.addCustomFunction("SETTIME1","Psc_Contr_Product_API.Get_Setting_Time","SETTINGTIME");
                  cmd.addParameter("CONTRACT_ID");
                  cmd.addParameter("LINE_NO");
               }
            }
            else
            {
               if (mgr.isEmpty(sTempOrgCode))
               {
                  if (checksec("Psc_Contr_Product_API.Get_Maint_Org",4,isSecure))
                  {
                     cmd = trans.addCustomFunction("ORGCODE","Psc_Contr_Product_API.Get_Maint_Org","ORG_CODE");
                     cmd.addParameter("CONTRACT_ID");
                     cmd.addParameter("LINE_NO");
                  }
                  cmd = trans.addCustomFunction("ORGDESC","Organization_API.Get_Description","ORGCODEDESCRIPTION");
                  cmd.addParameter("CONTRACT");
                  cmd.addReference("ORG_CODE","ORGCODE/DATA");     
               }
               else
               {
                  cmd = trans.addCustomFunction("ORGDESC","Organization_API.Get_Description","ORGCODEDESCRIPTION");
                  cmd.addParameter("CONTRACT");
                  cmd.addParameter("ORG_CODE",sTempOrgCode);     
               }

               if (checksec("Psc_Contr_Product_API.Get_Setting_Time",4,isSecure))
               {
                  cmd = trans.addCustomFunction("SETTIME1","Psc_Contr_Product_API.Get_Setting_Time","SETTINGTIME");
                  cmd.addParameter("CONTRACT_ID",mgr.readValue("CONTRACT_ID"));
                  cmd.addParameter("LINE_NO",mgr.readValue("LINE_NO"));
               }
            }

            trans = mgr.validate(trans);

            authcode1 = trans.getValue("AUTHCODE/DATA/AUTHORIZE_CODE");     

            if (mgr.isEmpty(sTempOrgCode))
               orgcode1 = trans.getValue("ORGCODE/DATA/ORG_CODE");
            else
               orgcode1    = sTempOrgCode;

            if ( "true".equals(isSecure[4]) )
               settingtime = (int)trans.getNumberValue("SETTIME1/DATA/SETTINGTIME");

            orgcodedescription1 = trans.getValue("ORGDESC/DATA/ORGCODEDESCRIPTION");

            if (!mgr.isEmpty(orgcodedescription1))
               orgcodedescription = orgcodedescription1;

            nSettingTime = settingtime;

            trans.clear();      

            if (checksec("Maintenance_Site_Utility_API.Get_Site_Date",1,isSecure))
            {
               cmd = trans.addCustomFunction("SYDATES","Maintenance_Site_Utility_API.Get_Site_Date","VALSYDATE");
               cmd.addParameter("CONTRACT");
            }

            trans = mgr.validate(trans);

            ref = 0;

            if (isSecure[ref += 1] =="true")
            {
               sSysDate1 = trans.getValue("SYDATES/DATA/VALSYDATE");
               sSysDate  = trans.getBuffer("SYDATES/DATA").getFieldValue("VALSYDATE");
            }
            else
               sSysDate    = "";

            if (settingtime == 0)
               nSettingTime = 0;

            if (nSettingTime == 0)
            {
               if (!mgr.isEmpty(sCustomerNum1))
               {
                  trans.clear(); 

                  if (! mgr.isEmpty(mgr.readValue("REG_DATE")))
                  {
                     sPlanStartDate1 = mgr.readValue("REG_DATE");
                     sRequiredStartDate1 = mgr.readValue("REG_DATE");
                     if (!mgr.isEmpty(sExecutionTime))
                     {
                        sPlanEndDate1 = addNumToDate(sPlanStartDate1,nExecutionTime);
                        sRequiredEndDate1 = addNumToDate(sRequiredStartDate1,nExecutionTime);
                     }
                  }
                  else
                  {
                     if (checksec("Maintenance_Site_Utility_API.Get_Site_Date",1,isSecure))
                     {
                        cmd = trans.addCustomFunction("SYDATES","Maintenance_Site_Utility_API.Get_Site_Date","VALSYDATE");
                        cmd.addParameter("CONTRACT");
                     }

                     trans = mgr.validate(trans);

                     ref = 0;
                     if (isSecure[ref += 1] =="true")
                     {
                        sSysDate1 = trans.getValue("SYDATES/DATA/VALSYDATE");
                        sSysDate  = trans.getBuffer("SYDATES/DATA").getFieldValue("VALSYDATE");                      
                     }
                     else
                        sSysDate    = "";

                     sPlanStartDate1 = sSysDate;
                     sRequiredStartDate1 = sSysDate;

                     if ((!mgr.isEmpty(sSysDate)) && (!mgr.isEmpty(sExecutionTime)))
                     {
                        sPlanEndDate1 = addNumToDate(sPlanStartDate1,nExecutionTime);
                        sRequiredEndDate1 = addNumToDate(sRequiredStartDate1,nExecutionTime);
                     }
                  }  
               }
               else
               {
                  if (mgr.isEmpty(sPlanStartDate1))
                     sPlanStartDate1 = "";
                  if (mgr.isEmpty(sRequiredStartDate1))
                     sRequiredStartDate1 = "";
               }
            }
            else
            {
               sPlanStartDate1 = addNumToDate(sRegistDate,(double)nSettingTime);
               sRequiredStartDate1 = sPlanStartDate1;

               if (mgr.isEmpty(sRequiredStartDate1))
               {
                  if (!mgr.isEmpty(sSysDate))
                     sRequiredStartDate1= addNumToDate(sSysDate,(double)nSettingTime);
               }
               if (!mgr.isEmpty(sExecutionTime))
               {
                  sPlanEndDate1 = addNumToDate(sPlanStartDate1,nExecutionTime);
                  sRequiredEndDate1 = addNumToDate(sRequiredStartDate1,nExecutionTime);
               }
            } 
         }

         if (mgr.isEmpty (stdjob))   stdjob="";
         if (mgr.isEmpty (org_code)) org_code="";
         if (mgr.isEmpty (authcode)) authcode="";
         if (mgr.isEmpty (agreemid)) agreemid="";
         if (mgr.isEmpty (agreemid1)) agreemid1="";
         if (mgr.isEmpty (authcode1)) authcode1="";
         if (mgr.isEmpty (orgcode1)) orgcode1="";
//      Bug 69047, Start
         //Bug Id 70921, Start
         txt = ((settingtime==0)? 0 : (settingtime))+"^"+
               ("TRUE".equals(cbWarrantyOnObject)?1:0)+"^"+
               ("TRUE".equals(cbhasagreement) ? 1 : 0)+ "^"+
               ("TRUE".equals(cbhasactivewo)?1:0)+"^"+
               (mgr.isEmpty(addId) ? "" : (addId))+ "^"+
               (mgr.isEmpty(address1) ? "" : (address1))+ "^"+
               (mgr.isEmpty(address2) ? "" : (address2))+"^"+
               (mgr.isEmpty(address3) ? "" : (address3))+ "^"+
               (mgr.isEmpty(address4) ? "" : (address4))+"^"+
               (mgr.isEmpty(address5) ? "" : (address5))+ "^"+
               (mgr.isEmpty(address6) ? "" : (address6))+"^"+
               (!mgr.isEmpty(agreemid1) ? agreemid1 : agreemid)+ "^"+
               (!mgr.isEmpty(authcode1) ? authcode1: authcode)+ "^"+
               (!mgr.isEmpty(orgcode1) ? orgcode1: org_code)+ "^"+
               (mgr.isEmpty(orgcodedescription) ? "" : (orgcodedescription))+"^"+
               (mgr.isEmpty(machname) ? "" : (machname))+"^"+
               ((mchCode == "") ? "":mchCode)+"^"+
               (mgr.isEmpty(mchContract) ? "":mchContract)+"^"+
               ((partno == "") ? "":partno)+"^"+
               ((serialno == "") ? "":serialno)+"^" +
               ("TRUE".equals(cbCustWar)?1:0)+"^"+
               ("TRUE".equals(cbSupWar)?1:0)+"^"+
               (mgr.isEmpty(sContractId) ? "" : sContractId)+ "^"+
               (mgr.isEmpty(sLineNo) ? "" : sLineNo)+ "^";
         //Bug Id 70921, End
//      Bug 69047, End

         mgr.responseWrite(txt);
      }
      else if ("CONNECTION_TYPE".equals(val))
      {
         cmd = trans.addCustomFunction("CONNTYPEDB","MAINT_CONNECTION_TYPE_API.Encode","CONNECTION_TYPE_DB");
         cmd.addParameter("CONNECTION_TYPE");

         trans = mgr.validate(trans);

         String conn_db = trans.getValue("CONNTYPEDB/DATA/CONNECTION_TYPE_DB");

         txt = (mgr.isEmpty(conn_db) ? "" : (conn_db))+ "^";

         mgr.responseWrite(txt);
      }
      else if ("REG_DATE".equals(val))
      {
         buf = mgr.newASPBuffer();
         buf.addFieldItem("REG_DATE",mgr.readValue("REG_DATE"));
         mgr.responseWrite(buf.getFieldValue("REG_DATE") +"^" );
      }
      else if ("REQUIRED_START_DATE".equals(val))
      {
         buf = mgr.newASPBuffer();
         buf.addFieldItem("REQUIRED_START_DATE",mgr.readValue("REQUIRED_START_DATE"));
         mgr.responseWrite(buf.getFieldValue("REQUIRED_START_DATE") +"^" );
      }
      else if ("REQUIRED_END_DATE".equals(val))
      {
         buf = mgr.newASPBuffer();
         buf.addFieldItem("REQUIRED_END_DATE",mgr.readValue("REQUIRED_END_DATE"));
         mgr.responseWrite(buf.getFieldValue("REQUIRED_END_DATE") +"^" );
      }
      else if ("PLAN_S_DATE".equals(val) || "PLAN_F_DATE".equals(val))
      {
         String sal="";
         String sal1="";

         if ("PLAN_S_DATE".equals(val))
         {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("PLAN_S_DATE",mgr.readValue("PLAN_S_DATE")); 
         }

         if ("PLAN_F_DATE".equals(val))
         {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("PLAN_F_DATE",mgr.readValue("PLAN_F_DATE"));
         }

         dateMask = mgr.getFormatMask("Datetime",true);

         if ("PLAN_S_DATE".equals(val))
         {
            if (!mgr.isEmpty(mgr.readValue("PLAN_S_DATE")))
            {
               if (!mgr.isEmpty(mgr.readValue("PLAN_HRS")))
               {
                  sal = addNumToDate(mgr.readValue("PLAN_S_DATE"),mgr.readNumberValue("PLAN_HRS"));
                  flg = "True";                                                                                                                
               }
               else
                  flg   = "";                                                                                                                    
            }
         }

         if ("True".equals(flg))
         {
            if (!("PLAN_F_DATE".equals(val)))
               finish = sal;
         }

         if ("PLAN_S_DATE".equals(val))
         {
            if (!mgr.isEmpty(mgr.readValue("PLAN_S_DATE")))
            {
               if (!mgr.isEmpty(mgr.readValue("PLAN_HRS")))
                  txt = finish +"^";
               else
                  txt   = mgr.readValue("PLAN_S_DATE") +"^";
            }
            if (mgr.isEmpty(mgr.readValue("PLAN_F_DATE")) && mgr.isEmpty(mgr.readValue("PLAN_HRS")))
               txt = ""+"^";
         }
         else if ("PLAN_F_DATE".equals(val))
         {
            if (!mgr.isEmpty(mgr.readValue("PLAN_F_DATE")))
            {
               if (!mgr.isEmpty(mgr.readValue("PLAN_S_DATE")))
                  txt = mgr.readValue("PLAN_F_DATE") +"^"+ mgr.readValue("PLAN_S_DATE") +"^"+ head_date +"^";
               else
               {
                  if (!mgr.isEmpty(mgr.readValue("PLAN_HRS")))
                     txt = mgr.readValue("PLAN_F_DATE") +"^"+ sal1 +"^"+ " " +"^";
                  else
                     txt   = mgr.readValue("PLAN_F_DATE") +"^"+ mgr.readValue("PLAN_F_DATE") +"^"+ " " +"^";
               }                                                                                                                                  
            }
            else
               txt   = mgr.readValue("PLAN_F_DATE") +"^"+ mgr.readValue("PLAN_S_DATE") +"^"+ " " +"^";
         }

         mgr.responseWrite(txt);
      }
      else if ("PLAN_HRS".equals(val))
      {
         String sal="";
         String sal1="";

         //Bug Id 70921, Start
         String sPlanHrs = mgr.readValue("PLAN_HRS","");
         String sContractId = mgr.readValue("CONTRACT_ID","");
         String sLineNo = mgr.readValue("LINE_NO","");
         String sOrgCode = mgr.readValue("ORG_CODE","");
         String sContract = mgr.readValue("CONTRACT","");
         String sPlanSDate = mgr.readValue("PLAN_S_DATE","");
         String sReqSDate = mgr.readValue("REQUIRED_START_DATE","");
         String sPlanFDate =  mgr.readValue("PLAN_F_DATE","");
         String sReqEndDate =  mgr.readValue("REQUIRED_END_DATE","");

         ASPBuffer newBuff = mgr.newASPBuffer();

         newBuff.addFieldItem("PLAN_HRS",sPlanHrs);
         newBuff.addFieldItem("CONTRACT_ID",sContractId);
         newBuff.addFieldItem("LINE_NO",sLineNo);
         newBuff.addFieldItem("ORG_CODE",sOrgCode);
         newBuff.addFieldItem("CONTRACT",sContract);
         newBuff.addFieldItem("PLAN_S_DATE",sPlanSDate);
         newBuff.addFieldItem("REQUIRED_START_DATE",sReqSDate);
         newBuff.addFieldItem("PLAN_F_DATE",sPlanFDate);
         newBuff.addFieldItem("REQUIRED_END_DATE",sReqEndDate);

         //Bug Id 70921, End

         secBuff = mgr.newASPTransactionBuffer();
         secBuff.addSecurityQuery("CUSTOMER_AGREEMENT");

         secBuff = mgr.perform(secBuff);

         if (!secBuff.getSecurityInfo().itemExists("CUSTOMER_AGREEMENT"))
            noSecurity = "True";
         else
            noSecurity = "False";

         //Bug Id 70921, Start
         boolean a = mgr.isEmpty(mgr.readValue("CUSTOMER_NO")) && mgr.isEmpty(mgr.readValue("PLAN_F_DATE"));
         boolean b = !mgr.isEmpty(mgr.readValue("CUSTOMER_NO"));

         if (a || b)
            sal = calEndDatesAccPlanHrs(newBuff);
         else
            sal   = mgr.readValue("PLAN_F_DATE","")+"^"+mgr.readValue("REQUIRED_END_DATE","")+"^";

         if ("True".equals(noSecurity))
            txt = mgr.readValue("PLAN_F_DATE","") +"^"+mgr.readValue("REQUIRED_END_DATE","") +"^";
         else
            txt = sal; 
         //Bug Id 70921, End

         mgr.responseWrite(txt);
      }
      else if ("ERR_DISCOVER_CODE".equals(val))
      {
         cmd = trans.addCustomFunction("DISCDESC","WORK_ORDER_DISC_CODE_API.Get_Description","DISCOVERDESCRIPTION");
         cmd.addParameter("ERR_DISCOVER_CODE",mgr.readValue("ERR_DISCOVER_CODE"));

         trans = mgr.validate(trans); 

         discDesc = trans.getValue("DISCDESC/DATA/DISCOVERDESCRIPTION");

         txt = (mgr.isEmpty(discDesc) ? "" : (discDesc))+ "^";
         mgr.responseWrite(txt); 
      }
      else if ("ERR_SYMPTOM".equals(val))
      {
         cmd = trans.addCustomFunction("SYMDESC","WORK_ORDER_SYMPT_CODE_API.Get_Description","SYMPTOMDESCRIPTION");
         cmd.addParameter("ERR_SYMPTOM",mgr.readValue("ERR_SYMPTOM"));

         trans = mgr.validate(trans); 

         symDesc = trans.getValue("SYMDESC/DATA/SYMPTOMDESCRIPTION");

         txt = (mgr.isEmpty(symDesc) ? "" : (symDesc))+ "^";
         mgr.responseWrite(txt); 
      }
      else if ("CUST_ORDER_TYPE".equals(val))
      {
         cmd = trans.addCustomFunction("CUSTORDTYPE","CUST_ORDER_TYPE_API.Get_Description","CUSTORDERTYPEDESCRIPTION");
         cmd.addParameter("CUST_ORDER_TYPE ");

         trans = mgr.validate(trans); 

         custOrdType = trans.getValue("CUSTORDTYPE/DATA/CUSTORDERTYPEDESCRIPTION");
         txt = (mgr.isEmpty(custOrdType) ? "" : (custOrdType))+ "^";
         mgr.responseWrite(txt); 
      }
      else if ("BUDGET_COST".equals(val))
      {
         buf = mgr.newASPBuffer();
         buf.addFieldItem("BUDGET_COST",mgr.readValue("BUDGET_COST"));
         mgr.responseWrite(buf.getFieldValue("BUDGET_COST") +"^" );
      }
      else if ("BUDGET_REVENUE".equals(val))
      {
         buf = mgr.newASPBuffer();
         buf.addFieldItem("BUDGET_REVENUE",mgr.readValue("BUDGET_REVENUE"));
         mgr.responseWrite(buf.getFieldValue("BUDGET_REVENUE") +"^" );
      }
      else if ("ORG_CODE".equals(val))
      {
         cmd = trans.addCustomFunction("ORGCODEDESC","Organization_Api.Get_Description","ORGCODEDESCRIPTION");
         cmd.addParameter("CONTRACT");
         cmd.addParameter("ORG_CODE");

         trans = mgr.validate(trans);

         String orgcodedesc = trans.getValue("ORGCODEDESC/DATA/ORGCODEDESCRIPTION");

         txt = (mgr.isEmpty(orgcodedesc) ? "" : (orgcodedesc))+ "^";
         mgr.responseWrite(txt);
      }
      else if ("WORK_LEADER_SIGN".equals(val))
      {
         cmd = trans.addCustomFunction("WORKLEADERSIGNID","Company_Emp_API.Get_Max_Employee_Id","WORK_LEADER_SIGN_ID");
         cmd.addParameter("COMPANY");
         cmd.addParameter("WORK_LEADER_SIGN");       

         trans = mgr.validate(trans);

         String sWorkLeadSgnID = trans.getValue("WORKLEADERSIGNID/DATA/WORK_LEADER_SIGN_ID");

         txt = (mgr.isEmpty(sWorkLeadSgnID) ? "" : (sWorkLeadSgnID))+ "^";
         mgr.responseWrite(txt);
      }
      else if ("WORK_MASTER_SIGN".equals(val))
      {
         cmd = trans.addCustomFunction("WORKMASTERSIGNID","Company_Emp_API.Get_Max_Employee_Id","WORK_MASTER_SIGN_ID");
         cmd.addParameter("COMPANY");
         cmd.addParameter("WORK_MASTER_SIGN");       

         trans = mgr.validate(trans);

         String sWorkMasSgnID = trans.getValue("WORKMASTERSIGNID/DATA/WORK_MASTER_SIGN_ID");

         txt = (mgr.isEmpty(sWorkMasSgnID) ? "" : (sWorkMasSgnID))+ "^";
         mgr.responseWrite(txt);
      }

      else if ("STD_JOB_ID".equals(val))
      {
         String valueStr = mgr.readValue("STD_JOB_ID");
         String stdJobId  = "";
         String stdJobContract = mgr.readValue("STD_JOB_CONTRACT");
         String stdJobRev = "";

         if (valueStr.indexOf("~") > -1)
         {
            String[] fieldValues = valueStr.split("~");

            stdJobId = fieldValues[0];
            stdJobContract = fieldValues[1];
            stdJobRev = fieldValues[2];
         }
         else
         {
            stdJobId = valueStr;
            stdJobRev = mgr.readValue("STD_JOB_REVISION");
         }

         trans.clear();

         cmd = trans.addCustomFunction("GETWORKDESC", "Standard_Job_API.Get_Work_Description", "DESCRIPTION");
         cmd.addParameter("STD_JOB_ID", stdJobId);
         cmd.addParameter("STD_JOB_CONTRACT", stdJobContract);
         cmd.addParameter("STD_JOB_REVISION", stdJobRev);

         cmd = trans.addCustomFunction("GETDESC", "Standard_Job_API.Get_Definition", "DESCRIPTION");
         cmd.addParameter("STD_JOB_ID", stdJobId);
         cmd.addParameter("STD_JOB_CONTRACT", stdJobContract);
         cmd.addParameter("STD_JOB_REVISION", stdJobRev);

         trans = mgr.validate(trans);

         String desc = trans.getValue("GETDESC/DATA/DESCRIPTION");
         String workDesc = trans.getValue("GETWORKDESC/DATA/DESCRIPTION");

         String addDesc;

         if (mgr.isEmpty(workDesc))
         {
            addDesc = desc;
         }
         else
            addDesc = workDesc;


         txt = (mgr.isEmpty(stdJobId)?"":stdJobId) + "^" + 
               (mgr.isEmpty(stdJobContract)?"":stdJobContract) + "^" +
               (mgr.isEmpty(stdJobRev)?"":stdJobRev) + "^" + 
               (mgr.isEmpty(addDesc)?"":addDesc) + "^";

         mgr.responseWrite(txt);
      }
      else if ("STD_JOB_REVISION".equals(val))
      {
         trans.clear();

         cmd = trans.addCustomFunction("GETWORKDESC", "Standard_Job_API.Get_Work_Description", "DESCRIPTION");
         cmd.addParameter("STD_JOB_ID", mgr.readValue("STD_JOB_ID"));
         cmd.addParameter("STD_JOB_CONTRACT", mgr.readValue("STD_JOB_CONTRACT"));
         cmd.addParameter("STD_JOB_REVISION", mgr.readValue("STD_JOB_REVISION"));

         cmd = trans.addCustomFunction("GETDESC", "Standard_Job_API.Get_Definition", "DESCRIPTION");
         cmd.addParameter("STD_JOB_ID", mgr.readValue("STD_JOB_ID"));
         cmd.addParameter("STD_JOB_CONTRACT", mgr.readValue("STD_JOB_CONTRACT"));
         cmd.addParameter("STD_JOB_REVISION", mgr.readValue("STD_JOB_REVISION"));

         trans = mgr.validate(trans);

         String desc = trans.getValue("GETDESC/DATA/DESCRIPTION");
         String workDesc = trans.getValue("GETWORKDESC/DATA/DESCRIPTION");

         String addDesc;

         if (mgr.isEmpty(workDesc))
         {
            addDesc = desc;
         }
         else
            addDesc = workDesc;   

         //String desc = trans.getValue("GETDESC/DATA/DESCRIPTION");

         txt = (mgr.isEmpty(addDesc)?"":addDesc) + "^";

         mgr.responseWrite(txt);
      }
      else if ("STD_JOB_FLAG".equals(val))
      {
         int nJobExist = 0;

         String sStdJobExist = "";
         int nRoleExist = 0;
         int nMatExist = 0;
         int nToolExist = 0;
         int nPlanningExist = 0;
         int nDocExist = 0;
         String sStdJobId = "";
         String sStdJobContract = "";
         String sStdJobRevision = "";

         double nQty = 0;
         String sIsSeparate = "";

         String sAgreementId = "";

         trans.clear();

         cmd = trans.addCustomFunction("GETJOBEXIST", "Work_Order_Job_API.Check_Exist", "N_JOB_EXIST");
         cmd.addParameter("ITEM2_WO_NO");
         cmd.addParameter("JOB_ID"); 

         cmd = trans.addCustomFunction("GETAGR", "Active_Separate_API.Get_Agreement_Id", "S_AGREEMENT_ID");
         cmd.addParameter("ITEM2_WO_NO");

         trans = mgr.validate(trans);

         nJobExist = new Double(trans.getNumberValue("GETJOBEXIST/DATA/N_JOB_EXIST")).intValue();
         sAgreementId = trans.getValue("GETAGR/DATA/S_AGREEMENT_ID");

         if (nJobExist == 1)
         {
            trans.clear();

            cmd = trans.addCustomFunction("GETSTDJOBEXIST", "Work_Order_Job_API.Std_Job_Exist", "S_STD_JOB_EXIST");
            cmd.addParameter("ITEM2_WO_NO");
            cmd.addParameter("JOB_ID");         

            cmd = trans.addCustomCommand("GETDEFVALS", "Work_Order_Job_API.Check_Job_Connection");
            cmd.addParameter("N_ROLE_EXIST","0");
            cmd.addParameter("N_MAT_EXIST","0");
            cmd.addParameter("N_TOOL_EXIST","0");
            cmd.addParameter("N_PLANNING_EXIST","0");
            cmd.addParameter("N_DOC_EXIST","0");
            cmd.addParameter("S_STD_JOB_ID");
            cmd.addParameter("S_STD_JOB_CONTRACT");
            cmd.addParameter("S_STD_JOB_REVISION");
            cmd.addParameter("ITEM2_WO_NO");
            cmd.addParameter("JOB_ID"); 

            cmd = trans.addCustomFunction("GETQTY", "Work_Order_Job_API.Get_Qty", "N_QTY");
            cmd.addParameter("ITEM2_WO_NO");
            cmd.addParameter("JOB_ID"); 

            /*cmd = trans.addCustomFunction("GETISSEP", "Active_Separate_API.Is_Separate", "S_IS_SEPARATE");
            cmd.addParameter("ITEM6_WO_NO");*/

            trans = mgr.validate(trans);

            sStdJobExist = trans.getValue("GETSTDJOBEXIST/DATA/S_STD_JOB_EXIST");

            nRoleExist = new Double(trans.getNumberValue("GETDEFVALS/DATA/N_ROLE_EXIST")).intValue();
            nMatExist = new Double(trans.getNumberValue("GETDEFVALS/DATA/N_MAT_EXIST")).intValue();
            nToolExist = new Double(trans.getNumberValue("GETDEFVALS/DATA/N_TOOL_EXIST")).intValue();
            nPlanningExist = new Double(trans.getNumberValue("GETDEFVALS/DATA/N_PLANNING_EXIST")).intValue();
            nDocExist = new Double(trans.getNumberValue("GETDEFVALS/DATA/N_DOC_EXIST")).intValue();
            sStdJobId = trans.getValue("GETDEFVALS/DATA/S_STD_JOB_ID");
            sStdJobContract = trans.getValue("GETDEFVALS/DATA/S_STD_JOB_CONTRACT");
            sStdJobRevision = trans.getValue("GETDEFVALS/DATA/S_STD_JOB_REVISION");

            nQty = trans.getNumberValue("GETQTY/DATA/N_QTY");
         }

         String sQty = mgr.formatNumber("N_QTY",nQty);

         txt = nJobExist + "^" +
               (mgr.isEmpty(sStdJobExist)?"":sStdJobExist) + "^" +
               nRoleExist + "^" +
               nMatExist + "^" +
               nToolExist + "^" +
               nPlanningExist + "^" +
               nDocExist + "^" +
               (mgr.isEmpty(sStdJobId)?"":sStdJobId) + "^" +
               (mgr.isEmpty(sStdJobContract)?"":sStdJobContract) + "^" +
               (mgr.isEmpty(sStdJobRevision)?"":sStdJobRevision) + "^" +
               sQty + "^" +
               (mgr.isEmpty(sAgreementId)?"":sAgreementId) + "^";

         mgr.responseWrite(txt);
      }
      else if ("DATE_FROM".equals(val))
      {
         buf = mgr.newASPBuffer();
         buf.addFieldItem("DATE_FROM",mgr.readValue("DATE_FROM",""));      
         mgr.responseWrite(mgr.readValue("DATE_FROM",""));
      }
      else if ("DATE_TO".equals(val))
      {
         buf = mgr.newASPBuffer();
         buf.addFieldItem("DATE_TO",mgr.readValue("DATE_TO",""));    
         mgr.responseWrite(mgr.readValue("DATE_TO",""));
      }
      else if ("CONTRACT_ID".equals(val))
      {
         String sContractId = mgr.readValue("CONTRACT_ID");
         String sLineNo = mgr.readValue("LINE_NO");
         //Bug Id 70921, Start
         String sWoNo = mgr.readValue("WO_NO");
         String sPlanStartDate = mgr.readValue("PLAN_S_DATE");
         String sPlanHrs = mgr.readValue("PLAN_HRS");
         String sRequiredStartDate =  mgr.readValue("REQUIRED_START_DATE");
         String sPlanCompleteDate = mgr.readValue("PLAN_F_DATE");
         String sRequiredEndDate = mgr.readValue("REQUIRED_END_DATE");
         String sPriorityId = mgr.readValue("PRIORITY_ID");
         String sRegDate = mgr.readValue("REG_DATE");

         //Bug Id 70921, End

         String sCustNo = null;
         String sCoordinator = null;
         String sContractName = "";
         String sLineDesc = "";
         String sContractType = "";
         String sInvoiceType = "";
         String sAgreementId = "";
         String sAgreementDesc = "";
         //Bug Id 70921, Start
         String connTypeDb = mgr.readValue("CONNECTION_TYPE_DB");
         String sWrkTypeId = "";
         String sWrkTypeDesc = "";
         String sOrgCode = "";
         String sOrgCodeDesc = "";
         String sSavedSrvcon = "";
         String sReturnStr = "^^^^^^^^";
         //Bug Id 70921, End

         if (sContractId.indexOf("^") > -1)
         {
            String strAttr = sContractId;
            sContractId = strAttr.substring(0, strAttr.indexOf("^"));
            sLineNo = strAttr.substring(strAttr.indexOf("^") + 1, strAttr.length());
         }

         if (checksec("Sc_Service_Contract_API.Get_Customer_Id", 1, isSecure))
         {
            cmd = trans.addCustomFunction("GETCUSTOMERNO", "Sc_Service_Contract_API.Get_Customer_Id", "CUSTOMER_NO");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }

         if (checksec("Sc_Service_Contract_API.Get_Authorize_Code", 1, isSecure))
         {
            cmd = trans.addCustomFunction("GETCOORDINATOR", "Sc_Service_Contract_API.Get_Authorize_Code", "AUTHORIZE_CODE");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }

         if (checksec("SC_SERVICE_CONTRACT_API.Get_Contract_Name", 1, isSecure))
         {
            cmd = trans.addCustomFunction("GETCONTRACTNAME", "SC_SERVICE_CONTRACT_API.Get_Contract_Name", "CONTRACT_NAME");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }

         if (checksec("PSC_CONTR_PRODUCT_API.Get_Description", 1, isSecure))
         {
            cmd = trans.addCustomFunction("GETLINEDESC", "PSC_CONTR_PRODUCT_API.Get_Description", "LINE_DESC");
            cmd.addParameter("CONTRACT_ID", sContractId);
            cmd.addParameter("LINE_NO", sLineNo);
         }

         if (checksec("SC_SERVICE_CONTRACT_API.Get_Contract_Type", 1, isSecure))
         {
            cmd = trans.addCustomFunction("GETCONTRACTYPE", "SC_SERVICE_CONTRACT_API.Get_Contract_Type", "CONTRACT_TYPE");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }

         if (checksec("PSC_CONTR_PRODUCT_API.Get_Invoice_Type", 1, isSecure))
         {
            cmd = trans.addCustomFunction("GETINVOICETYPE", "PSC_CONTR_PRODUCT_API.Get_Invoice_Type", "INVOICE_TYPE");
            cmd.addParameter("CONTRACT_ID", sContractId);
            cmd.addParameter("LINE_NO", sLineNo);
         }

         //Bug Id 70921, Start
         if (checksec("PSC_CONTR_PRODUCT_API.Get_Work_Type_Id", 1, isSecure))
         {
            cmd = trans.addCustomFunction("GETWORKTYPE", "PSC_CONTR_PRODUCT_API.Get_Work_Type_Id", "WORK_TYPE_ID");
            cmd.addParameter("CONTRACT_ID", sContractId);
            cmd.addParameter("LINE_NO", sLineNo);
         }

         if (checksec("WORK_TYPE_API.Get_Description", 1, isSecure))
         {
            cmd = trans.addCustomFunction("GETWORKTYPEDESC", "WORK_TYPE_API.Get_Description", "WORKTYPEDESCRIPTION");
            cmd.addParameter("WORK_TYPE_ID");
         }

         if ("VIM".equals(connTypeDb))
         {
            if (checksec("PSC_CONTR_PRODUCT_API.Get_Work_Shop_Code", 1, isSecure))
            {
               cmd = trans.addCustomFunction("GETORGCODE", "PSC_CONTR_PRODUCT_API.Get_Work_Shop_Code", "ORG_CODE");
               cmd.addParameter("CONTRACT_ID", sContractId);
               cmd.addParameter("LINE_NO", sLineNo);
            }
         }
         else if (checksec("PSC_CONTR_PRODUCT_API.Get_Maint_Org", 1, isSecure))
         {
            cmd = trans.addCustomFunction("GETORGCODE", "PSC_CONTR_PRODUCT_API.Get_Maint_Org", "ORG_CODE");
            cmd.addParameter("CONTRACT_ID", sContractId);
            cmd.addParameter("LINE_NO", sLineNo);
         }


         if (checksec("ORGANIZATION_API.Get_Description", 1, isSecure))
         {
            cmd = trans.addCustomFunction("ORGCODEDESC", "ORGANIZATION_API.Get_Description", "ORGCODEDESCRIPTION");
            cmd.addParameter("CONTRACT");
            cmd.addParameter("ORG_CODE");
         }

         if (checksec("ACTIVE_SEPARATE_API.Get_Plan_S_Date", 1, isSecure))
         {
            cmd = trans.addCustomFunction("PLAN_S_DATE", "ACTIVE_SEPARATE_API.Get_Plan_S_Date", "PLAN_S_DATE");
            cmd.addParameter("WO_NO", sWoNo);
         }
         if (checksec("ACTIVE_SEPARATE_API.Get_Plan_F_Date", 1, isSecure))
         {
            cmd = trans.addCustomFunction("PLAN_F_DATE", "ACTIVE_SEPARATE_API.Get_Plan_F_Date", "PLAN_F_DATE");
            cmd.addParameter("WO_NO", sWoNo);
         }
         if (checksec("ACTIVE_SEPARATE_API.Get_Required_S_Date", 1, isSecure))
         {
            cmd = trans.addCustomFunction("REQUIRED_START_DATE", "ACTIVE_SEPARATE_API.Get_Required_S_Date", "REQUIRED_START_DATE");
            cmd.addParameter("WO_NO", sWoNo);
         }
         if (checksec("ACTIVE_SEPARATE_API.Get_Required_End_Date", 1, isSecure))
         {
            cmd = trans.addCustomFunction("REQUIRED_END_DATE", "ACTIVE_SEPARATE_API.Get_Required_End_Date", "REQUIRED_END_DATE");
            cmd.addParameter("WO_NO", sWoNo);
         }
         if (checksec("ACTIVE_SEPARATE_API.Get_Plan_Hrs", 1, isSecure))
         {
            cmd = trans.addCustomFunction("PLAN_HRS", "ACTIVE_SEPARATE_API.Get_Plan_Hrs", "PLAN_HRS");
            cmd.addParameter("WO_NO", sWoNo);
         }
         if (checksec("ACTIVE_SEPARATE_API.Get_Priority_Id", 1, isSecure))
         {
            cmd = trans.addCustomFunction("PRIORITY_ID", "ACTIVE_SEPARATE_API.Get_Priority_Id", "PRIORITY_ID");
            cmd.addParameter("WO_NO", sWoNo);
         }
         if (checksec("SC_SERVICE_CONTRACT_API.Exist", 1, isSecure))
         {

            cmd = trans.addCustomCommand("EXIST_LINE", "SC_SERVICE_CONTRACT_API.Exist");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }

         if (checksec("ACTIVE_SEPARATE_API.Get_Contract_Id", 1, isSecure))
         {
            cmd = trans.addCustomFunction("WOSAVEDSRVCON", "ACTIVE_SEPARATE_API.Get_Contract_Id","DUMMY_CONTRACT_ID");
            cmd.addParameter("WO_NO", sWoNo);
         }
         //Bug Id 70921, End

         if (checksec("SC_CONTRACT_AGREEMENT_API.Get_First_Agreement", 1, isSecure))
         {
            cmd = trans.addCustomCommand("SCCONTRACTAGREEMENT", "SC_CONTRACT_AGREEMENT_API.Get_First_Agreement");
            cmd.addParameter("AGREEMENT_ID");
            cmd.addParameter("AGREEMENT_DESC");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }

         trans = mgr.validate(trans);

         sCustNo = trans.getValue("GETCUSTOMERNO/DATA/CUSTOMER_NO");
         sCoordinator = trans.getValue("GETCOORDINATOR/DATA/AUTHORIZE_CODE");
         sContractName = trans.getValue("GETCONTRACTNAME/DATA/CONTRACT_NAME");
         sLineDesc = trans.getValue("GETLINEDESC/DATA/LINE_DESC");
         sContractType = trans.getValue("GETCONTRACTYPE/DATA/CONTRACT_TYPE");
         sInvoiceType = trans.getValue("GETINVOICETYPE/DATA/INVOICE_TYPE");
         sAgreementId = trans.getBuffer("SCCONTRACTAGREEMENT/DATA").getValue("AGREEMENT_ID");
         sAgreementDesc = trans.getBuffer("SCCONTRACTAGREEMENT/DATA").getValue("AGREEMENT_DESC");
         //Bug Id 70921, Start
         sWrkTypeId = trans.getBuffer("GETWORKTYPE/DATA").getValue("WORK_TYPE_ID");
         sWrkTypeDesc = trans.getBuffer("GETWORKTYPEDESC/DATA").getValue("WORKTYPEDESCRIPTION");
         sOrgCode = trans.getBuffer("GETORGCODE/DATA").getValue("ORG_CODE");
         sOrgCodeDesc = trans.getBuffer("ORGCODEDESC/DATA").getValue("ORGCODEDESCRIPTION");
         sSavedSrvcon = trans.getBuffer("WOSAVEDSRVCON/DATA").getValue("DUMMY_CONTRACT_ID");

         buf = mgr.newASPBuffer();
         buf.clear();

         buf.addFieldItem("PLAN_HRS",sPlanHrs);
         buf.addFieldItem("PLAN_S_DATE",sPlanStartDate);
         buf.addFieldItem("REQUIRED_START_DATE",sRequiredStartDate); 
         buf.addFieldItem("PLAN_F_DATE",sPlanCompleteDate);
         buf.addFieldItem("REQUIRED_END_DATE",sRequiredEndDate);
         buf.addFieldItem("CONTRACT_ID",sContractId);
         buf.addFieldItem("LINE_NO",sLineNo);
         buf.addFieldItem("WO_NO",sWoNo);
         buf.addFieldItem("PRIORITY_ID",sPriorityId);
         buf.addFieldItem("REG_DATE",sRegDate);

         buf.addFieldItem("PRIORITY_ID",mgr.readValue("PRIORITY_ID"));

         sReturnStr = (mgr.isEmpty(sPlanStartDate) ? "" : sPlanStartDate) + "^" + (mgr.isEmpty(sRequiredStartDate) ? "" : sRequiredStartDate) + "^" + 
                      (mgr.isEmpty(sPlanCompleteDate) ? "" : sPlanCompleteDate) + "^" + (mgr.isEmpty(sRequiredEndDate) ? "" : sRequiredEndDate) + "^" + 
                      (mgr.isEmpty(sPlanHrs) ? "" : sPlanHrs) + "^" +
                      (mgr.isEmpty(sSavedSrvcon) ? "" : sSavedSrvcon) +"^" + 
                      (mgr.isEmpty(sPlanCompleteDate) ? "" : sPlanCompleteDate) + "^" + 
                      (mgr.isEmpty(sRequiredEndDate) ? "" : sRequiredEndDate) + "^" ; 

         if ( !(("TRUE").equals(sSkipDateOverwrite)) && !mgr.isEmpty(sContractId) && !mgr.isEmpty(sLineNo))
         {

            sReturnStr = getDatesForSrvconValidate(buf);
         }
         sSkipDateOverwrite = "FALSE";
         ctx.setGlobal("SKIPDATEOVERWITE",sSkipDateOverwrite);

         txt = (mgr.isEmpty(sContractId) ? "" : (sContractId)) + "^" + (mgr.isEmpty(sLineNo) ? "" : (sLineNo)) + "^" + (mgr.isEmpty(sContractName) ? "" : sContractName) + "^" + (mgr.isEmpty(sLineDesc) ? "" : sLineDesc) + "^"
               + (mgr.isEmpty(sContractType) ? "" : (sContractType)) + "^" + (mgr.isEmpty(sInvoiceType) ? "" : sInvoiceType) + "^" + (mgr.isEmpty(sCustNo) ? "" : sCustNo) + "^" + (mgr.isEmpty(sCoordinator) ? "" : sCoordinator) + "^"
               + (mgr.isEmpty(sAgreementId) ? "" : sAgreementId) + "^" + (mgr.isEmpty(sAgreementDesc) ? "" : sAgreementDesc) + "^"
               + (mgr.isEmpty(sWrkTypeId) ? "" : sWrkTypeId) + "^" + (mgr.isEmpty(sWrkTypeDesc) ? "" : sWrkTypeDesc) + "^"
               + (mgr.isEmpty(sOrgCode) ? "" : sOrgCode) + "^" + (mgr.isEmpty(sOrgCodeDesc) ? "" : sOrgCodeDesc) + "^"+sReturnStr;

         sSkipDateOverwrite = "FALSE";
         //Bug Id 70921, End

         mgr.responseWrite(txt);
      }

      else if ("LINE_NO".equals(val))
      {
         String sContractId   = mgr.readValue("CONTRACT_ID");
         String sLineNo       = mgr.readValue("LINE_NO");
         //Bug Id 70921, Start
         String sWoNo = mgr.readValue("WO_NO");
         String sPlanStartDate = mgr.readValue("PLAN_S_DATE");
         String sPlanHrs = mgr.readValue("PLAN_HRS");
         String sRequiredStartDate =  mgr.readValue("REQUIRED_START_DATE");
         String sPlanCompleteDate = mgr.readValue("PLAN_F_DATE");
         String sRequiredEndDate = mgr.readValue("REQUIRED_END_DATE");
         String sPriorityId = mgr.readValue("PRIORITY_ID");
         String sRegDate = mgr.readValue("REG_DATE");

         //Bug Id 70921, End
         String sContractName = "";
         String sLineDesc     = "";
         String sContractType = "";
         String sInvoiceType  = "";
         String sAgreementId  = "";
         String sAgreementDesc= "";
         //Bug Id 70921, Start
         String connTypeDb = mgr.readValue("CONNECTION_TYPE_DB");
         String sWrkTypeId = "";
         String sWrkTypeDesc = "";
         String sOrgCode = "";
         String sOrgCodeDesc = "";
         String sMchCode = "";
         String sMchCodeDesc = "";
         String sSavedSrvcon = "";
         String sReturnStr = "^^^^^^^^";
         //Bug Id 70921, End

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

         if (checksec("SC_CONTRACT_AGREEMENT_API.Get_First_Agreement",1,isSecure))
         {
            cmd = trans.addCustomCommand("SCCONTRACTAGREEMENT","SC_CONTRACT_AGREEMENT_API.Get_First_Agreement");
            cmd.addParameter("AGREEMENT_ID");
            cmd.addParameter("AGREEMENT_DESC");
            cmd.addParameter("CONTRACT_ID",sContractId);
         }
         //Bug Id 70921, Start
         if (checksec("PSC_CONTR_PRODUCT_API.Get_Work_Type_Id", 1, isSecure))
         {
            cmd = trans.addCustomFunction("GETWORKTYPE", "PSC_CONTR_PRODUCT_API.Get_Work_Type_Id", "WORK_TYPE_ID");
            cmd.addParameter("CONTRACT_ID", sContractId);
            cmd.addParameter("LINE_NO", sLineNo);
         }

         if (checksec("WORK_TYPE_API.Get_Description", 1, isSecure))
         {
            cmd = trans.addCustomFunction("GETWORKTYPEDESC", "WORK_TYPE_API.Get_Description", "WORKTYPEDESCRIPTION");
            cmd.addParameter("WORK_TYPE_ID");
         }

         if ("VIM".equals(connTypeDb))
         {
            if (checksec("PSC_CONTR_PRODUCT_API.Get_Work_Shop_Code", 1, isSecure))
            {
               cmd = trans.addCustomFunction("GETORGCODE", "PSC_CONTR_PRODUCT_API.Get_Work_Shop_Code", "ORG_CODE");
               cmd.addParameter("CONTRACT_ID", sContractId);
               cmd.addParameter("LINE_NO", sLineNo);
            }
         }
         else if (checksec("PSC_CONTR_PRODUCT_API.Get_Maint_Org", 1, isSecure))
         {
            cmd = trans.addCustomFunction("GETORGCODE", "PSC_CONTR_PRODUCT_API.Get_Maint_Org", "ORG_CODE");
            cmd.addParameter("CONTRACT_ID", sContractId);
            cmd.addParameter("LINE_NO", sLineNo);
         }


         if (checksec("ORGANIZATION_API.Get_Description", 1, isSecure))
         {
            cmd = trans.addCustomFunction("ORGCODEDESC", "ORGANIZATION_API.Get_Description", "ORGCODEDESCRIPTION");
            cmd.addParameter("CONTRACT");
            cmd.addParameter("ORG_CODE");
         }

         if (checksec("ACTIVE_SEPARATE_API.Get_Plan_S_Date", 1, isSecure))
         {
            cmd = trans.addCustomFunction("PLAN_S_DATE", "ACTIVE_SEPARATE_API.Get_Plan_S_Date", "PLAN_S_DATE");
            cmd.addParameter("WO_NO", sWoNo);
         }
         if (checksec("ACTIVE_SEPARATE_API.Get_Plan_F_Date", 1, isSecure))
         {
            cmd = trans.addCustomFunction("PLAN_F_DATE", "ACTIVE_SEPARATE_API.Get_Plan_F_Date", "PLAN_F_DATE");
            cmd.addParameter("WO_NO", sWoNo);
         }
         if (checksec("ACTIVE_SEPARATE_API.Get_Required_S_Date", 1, isSecure))
         {
            cmd = trans.addCustomFunction("REQUIRED_START_DATE", "ACTIVE_SEPARATE_API.Get_Required_S_Date", "REQUIRED_START_DATE");
            cmd.addParameter("WO_NO", sWoNo);
         }
         if (checksec("ACTIVE_SEPARATE_API.Get_Required_End_Date", 1, isSecure))
         {
            cmd = trans.addCustomFunction("REQUIRED_END_DATE", "ACTIVE_SEPARATE_API.Get_Required_End_Date", "REQUIRED_END_DATE");
            cmd.addParameter("WO_NO", sWoNo);
         }
         if (checksec("ACTIVE_SEPARATE_API.Get_Plan_Hrs", 1, isSecure))
         {
            cmd = trans.addCustomFunction("PLAN_HRS", "ACTIVE_SEPARATE_API.Get_Plan_Hrs", "PLAN_HRS");
            cmd.addParameter("WO_NO", sWoNo);
         }
         if (checksec("ACTIVE_SEPARATE_API.Get_Priority_Id", 1, isSecure))
         {
            cmd = trans.addCustomFunction("PRIORITY_ID", "ACTIVE_SEPARATE_API.Get_Priority_Id", "PRIORITY_ID");
            cmd.addParameter("WO_NO", sWoNo);
         }
         if (checksec("PSC_CONTR_PRODUCT_API.Exist", 1, isSecure))
         {
            cmd = trans.addCustomCommand("EXIST_LINE", "PSC_CONTR_PRODUCT_API.Exist");
            cmd.addParameter("CONTRACT_ID", sContractId);
            cmd.addParameter("LINE_NO", sLineNo);
         }
         if (checksec("ACTIVE_SEPARATE_API.Get_Contract_Id", 1, isSecure))
         {
            cmd = trans.addCustomFunction("WOSAVEDSRVCON", "ACTIVE_SEPARATE_API.Get_Contract_Id","DUMMY_CONTRACT_ID");
            cmd.addParameter("WO_NO", sWoNo);
         }

         //Bug Id 70921, End


         trans = mgr.validate(trans);

         sContractName  = trans.getValue("GETCONTRACTNAME/DATA/CONTRACT_NAME");
         sLineDesc      = trans.getValue("GETLINEDESC/DATA/LINE_DESC");
         sContractType  = trans.getValue("GETCONTRACTYPE/DATA/CONTRACT_TYPE");
         sInvoiceType   = trans.getValue("GETINVOICETYPE/DATA/INVOICE_TYPE");
         sAgreementId   = trans.getBuffer("SCCONTRACTAGREEMENT/DATA").getValue("AGREEMENT_ID");
         sAgreementDesc = trans.getBuffer("SCCONTRACTAGREEMENT/DATA").getValue("AGREEMENT_DESC");
         //Bug Id 70921, Start
         sWrkTypeId = trans.getBuffer("GETWORKTYPE/DATA").getValue("WORK_TYPE_ID");
         sWrkTypeDesc = trans.getBuffer("GETWORKTYPEDESC/DATA").getValue("WORKTYPEDESCRIPTION");
         sOrgCode = trans.getBuffer("GETORGCODE/DATA").getValue("ORG_CODE");
         sOrgCodeDesc = trans.getBuffer("ORGCODEDESC/DATA").getValue("ORGCODEDESCRIPTION");
         sSavedSrvcon = trans.getBuffer("WOSAVEDSRVCON/DATA").getValue("DUMMY_CONTRACT_ID");

         buf = mgr.newASPBuffer();
         buf.clear();

         buf.addFieldItem("PLAN_HRS",sPlanHrs);
         buf.addFieldItem("PLAN_S_DATE",sPlanStartDate);
         buf.addFieldItem("REQUIRED_START_DATE",sRequiredStartDate); 
         buf.addFieldItem("PLAN_F_DATE",sPlanCompleteDate);
         buf.addFieldItem("REQUIRED_END_DATE",sRequiredEndDate);
         buf.addFieldItem("CONTRACT_ID",sContractId);
         buf.addFieldItem("LINE_NO",sLineNo);
         buf.addFieldItem("WO_NO",sWoNo);
         buf.addFieldItem("PRIORITY_ID",sPriorityId);
         buf.addFieldItem("REG_DATE",sRegDate);

         sReturnStr = (mgr.isEmpty(sPlanStartDate) ? "" : sPlanStartDate) + "^" + (mgr.isEmpty(sRequiredStartDate) ? "" : sRequiredStartDate) + "^" + 
                      (mgr.isEmpty(sPlanCompleteDate) ? "" : sPlanCompleteDate) + "^" + (mgr.isEmpty(sRequiredEndDate) ? "" : sRequiredEndDate) + "^" + 
                      (mgr.isEmpty(sPlanHrs) ? "" : sPlanHrs) + "^" +
                      (mgr.isEmpty(sSavedSrvcon) ? "" : sSavedSrvcon) +"^" + 
                      (mgr.isEmpty(sPlanCompleteDate) ? "" : sPlanCompleteDate) + "^" + 
                      (mgr.isEmpty(sRequiredEndDate) ? "" : sRequiredEndDate) + "^" ; 

         if (!(("TRUE").equals(sSkipDateOverwrite)) && !mgr.isEmpty(sContractId) && !mgr.isEmpty(sLineNo))
         {
            sReturnStr = getDatesForSrvconValidate(buf);
         }
         sSkipDateOverwrite = "FALSE";
         ctx.setGlobal("SKIPDATEOVERWITE",sSkipDateOverwrite);

         txt =  (mgr.isEmpty(sContractId) ? "" : (sContractId)) + "^" + (mgr.isEmpty(sLineNo) ? "" : (sLineNo)) + "^" +
                (mgr.isEmpty(sContractName)?"":sContractName) + "^" + (mgr.isEmpty(sLineDesc)?"":sLineDesc) + "^" +
                (mgr.isEmpty(sContractType) ? "" : (sContractType)) + "^" +(mgr.isEmpty(sInvoiceType)?"":sInvoiceType) + "^" +
                (mgr.isEmpty(sAgreementId)?"":sAgreementId)+ "^" + (mgr.isEmpty(sAgreementDesc)?"":sAgreementDesc)+ "^" + 
                (mgr.isEmpty(sWrkTypeId) ? "" : sWrkTypeId) + "^" + (mgr.isEmpty(sWrkTypeDesc) ? "" : sWrkTypeDesc) + "^" +
                (mgr.isEmpty(sOrgCode) ? "" : sOrgCode) + "^" + (mgr.isEmpty(sOrgCodeDesc) ? "" : sOrgCodeDesc) + "^" +sReturnStr;

         mgr.responseWrite(txt);

         //Bug Id 70921, End
      }

      // 031003  ARWILK  Begin (Bug#104743)
      /*else if ("STD_JOB_ID".equals(val))
      {
          String sWorkDescLo;
          String sPlanHrs;
          String sPlanSDate = mgr.readValue("PLAN_S_DATE");
          String sCutomerNo = mgr.readValue("CUSTOMER_NO");
          String sPlanFDate = mgr.readValue("PLAN_F_DATE");

          cmd = trans.addCustomFunction("GETSTDDESC","Standard_Job_API.Get_Work_Description","WORK_DESCR_LO");
          cmd.addParameter("STD_JOB_ID",mgr.readValue("STD_JOB_ID"));

          cmd = trans.addCustomFunction("GETPLANHRS","Standard_Job_Role_Utility_API.Calculate_Execution_Time","PLAN_HRS");
          cmd.addParameter("STD_JOB_ID",mgr.readValue("STD_JOB_ID"));

          trans = mgr.validate(trans); 

          sWorkDescLo = trans.getValue("GETSTDDESC/DATA/WORK_DESCR_LO");
          sPlanHrs = trans.getValue("GETPLANHRS/DATA/PLAN_HRS");

          if ((!mgr.isEmpty(sPlanSDate)) 
              && (!mgr.isEmpty(sPlanHrs))
              && ((mgr.isEmpty(sCutomerNo)
                   && mgr.isEmpty(sPlanFDate))
                  || (!mgr.isEmpty(sCutomerNo))))
          {
              sPlanFDate = addNumToDate(sPlanSDate, new Double(sPlanHrs).doubleValue());
          }

          txt = (mgr.isEmpty(sWorkDescLo) ? "" : (sWorkDescLo))+ "^"
                + (mgr.isEmpty(sPlanHrs) ? "" : (sPlanHrs))+ "^"
                + (mgr.isEmpty(sPlanFDate) ? "" : (sPlanFDate))+ "^";

          mgr.responseWrite(txt); 
      }*/
      // 031003  ARWILK  End (Bug#104743)
      //(+) Bug 66406, Start
      else if ("CONN_PM_NO".equals(val))
      {
         String pmno = "";
         String pmrev = "";

         if (mgr.readValue("CONN_PM_NO").indexOf("~") > -1)
         {
            String [] attrarr =  mgr.readValue("CONN_PM_NO").split("~");

            pmno  = attrarr[0];
            pmrev = attrarr[1]; 
         }
         else
         {
            pmno = mgr.readValue("CONN_PM_NO");
            pmrev = mgr.readValue("CONN_PM_REVISION");
         }

         txt = (mgr.isEmpty(pmno)? "":pmno)+ "^" + (mgr.isEmpty(pmrev)? "":pmrev)+ "^";
         mgr.responseWrite( txt );
      }
      //(+) Bug 66406, End

      //Bug Id 70921, Start

      else if ("PRIORITY_ID".equals(val))
      {

         String sContractId = mgr.readValue("CONTRACT_ID");
         String sLineNo = mgr.readValue("LINE_NO");
         String sPlanStartDate = mgr.readValue("PLAN_S_DATE");
         String sRequiredStartDate =  mgr.readValue("REQUIRED_START_DATE");
         String sPriorityId = mgr.readValue("PRIORITY_ID");

         String sPlanCompleteDate = "";
         String sRequiredEndDate = "";
         String sPlanHrs = "";
         String sPriorityDesc = "";

         txt = "";

         trans.clear();

         if (checksec("MAINTENANCE_PRIORITY_API.Exist", 1, isSecure))
         {

            cmd = trans.addCustomCommand("EXIST_LINE", "MAINTENANCE_PRIORITY_API.Exist");
            cmd.addParameter("PRIORITY_ID", sPriorityId);
         }

         if (checksec("ACTIVE_SEPARATE_API.Get_Dates_For_Priority", 1, isSecure))
         {
            cmd = trans.addCustomCommand("PRIORITYDATES", "ACTIVE_SEPARATE_API.Get_Dates_For_Priority");
            cmd.addParameter("PLAN_F_DATE");
            cmd.addParameter("REQUIRED_END_DATE");
            cmd.addParameter("PLAN_HRS");
            cmd.addParameter("PLAN_S_DATE",sPlanStartDate); 
            cmd.addParameter("REQUIRED_START_DATE",sRequiredStartDate);
            cmd.addParameter("CONTRACT_ID", sContractId);
            cmd.addParameter("LINE_NO", sLineNo);
            cmd.addParameter("PRIORITY_ID", sPriorityId);
         }

         if (checksec("MAINTENANCE_PRIORITY_API.Get_Description", 1, isSecure))
         {
            cmd = trans.addCustomFunction("PRIORITYDESCRIPTION", "MAINTENANCE_PRIORITY_API.Get_Description","PRIORITYDESCRIPTION");
            cmd.addParameter("PRIORITY_ID",sPriorityId);
         }
         trans = mgr.validate(trans);

         sPlanCompleteDate = trans.getBuffer("PRIORITYDATES/DATA").getFieldValue("PLAN_F_DATE");
         sRequiredEndDate = trans.getBuffer("PRIORITYDATES/DATA").getFieldValue("REQUIRED_END_DATE");
         sPlanHrs = trans.getBuffer("PRIORITYDATES/DATA").getValue("PLAN_HRS");

         sPriorityDesc = trans.getBuffer("PRIORITYDESCRIPTION/DATA").getValue("PRIORITYDESCRIPTION");

         txt = (mgr.isEmpty(sPlanHrs) ? "" : sPlanHrs) + "^" +
               (mgr.isEmpty(sPlanCompleteDate) ? "" : sPlanCompleteDate) + "^" + 
               (mgr.isEmpty(sRequiredEndDate) ? "" : sRequiredEndDate) + "^" +
               (mgr.isEmpty(sPriorityDesc) ? "" : sPriorityDesc) + "^" ;

         mgr.responseWrite(txt);
      }
      //Bug Id 70921, End
      mgr.endResponse();
   }

   public String addNumToDate(String dt,double num)
   {
      ASPManager mgr = getASPManager();
      buf = mgr.newASPBuffer();
      buf.addFieldItem("PLAN_S_DATE",dt);      

      trans12.clear();
      q = trans12.addQuery("DATEAFTER","select ? + ? DUMMY_PLAN_F_DATE from DUAL");
      q.addParameter("PLAN_S_DATE",buf.getFieldValue("PLAN_S_DATE"));
      q.addParameter("PLAN_HRS",num/24+"");
      q.includeMeta("ALL");
      trans12 = mgr.perform(trans12);
      String output = trans12.getBuffer("DATEAFTER/DATA").getFieldValue("DUMMY_PLAN_F_DATE");
      trans12.clear();

      return output;
   }

   public String addNumToDate1(String dt,double num)
   {
      ASPManager mgr = getASPManager();
      buf = mgr.newASPBuffer();
      buf.addFieldItem("PLAN_S_DATE",dt);      

      trans12.clear();
      q = trans12.addQuery("DATEAFTER","select ? + ? DUMMY_PLAN_F_DATE from DUAL");
      q.addParameter("PLAN_S_DATE",buf.getFieldValue("PLAN_S_DATE"));
      q.addParameter("PLAN_HRS",num/24+"");
      q.includeMeta("ALL");
      trans12 = mgr.perform(trans12);
      String output = trans12.getValue("DATEAFTER/DATA/DUMMY_PLAN_F_DATE");
      trans12.clear();

      return output;
   }

   public String redNumFromDate(String dt,double num)
   {
      ASPManager mgr = getASPManager();
      buf = mgr.newASPBuffer();
      buf.addFieldItem("PLAN_S_DATE",dt);      

      trans12.clear();
      q = trans12.addQuery("DATEAFTER","select ? - ? PLAN_F_DATE from DUAL");
      q.addParameter("PLAN_S_DATE",buf.getFieldValue("PLAN_S_DATE"));
      q.addParameter("PLAN_HRS",num/24+"");
      q.includeMeta("ALL");
      trans12 = mgr.perform(trans12);
      String output = trans12.getBuffer("DATEAFTER/DATA").getFieldValue("PLAN_F_DATE");
      trans12.clear();

      return output;
   }

   public void getSettingTime()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      isSecure = new String[7];

//    Bug 68363, Start
      if (checksec("Psc_Contr_Product_API.Get_Setting_Time", 4, isSecure))
      {
         cmd = trans.addCustomFunction("SETTIME", "Psc_Contr_Product_API.Get_Setting_Time", "SETTINGTIME");
         cmd.addParameter("CONTRACT_ID", headset.getRow().getFieldValue("CONTRACT_ID"));
         cmd.addParameter("LINE_NO", headset.getRow().getFieldValue("LINE_NO"));
      }
//    Bug 68363, End
      trans = mgr.perform(trans);
      settingtime = (int) trans.getNumberValue("SETTIME/DATA/SETTINGTIME");
      set = headset.getRow();

      set.setNumberValue("SETTINGTIME", settingtime);

      headset.setRow(set);

   }

// -----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

   public void findAndEditObject()
   {
      int ref;
      ASPManager mgr = getASPManager();
      isSecure = new String[7];

      trans.clear();
      q = trans.addQuery(headblk);
      q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
      q.addWhereCondition("OBJSTATE = 'FAULTREPORT' OR OBJSTATE = 'WORKREQUEST' OR OBJSTATE = 'OBSERVED' OR OBJSTATE = 'UNDERPREPARATION' OR OBJSTATE = 'PREPARED'");
      q.includeMeta("ALL");

      q = trans.addEmptyQuery(eventblk);
      q.addMasterConnection("HEAD","WO_NO","EVNTBLK_WO_NO");
      q.addMasterConnection("HEAD","CONTRACT","EVNTBLK_CONTRACT");
      q.includeMeta("ALL");

      mgr.submit(trans);

      headset.goTo(Integer.parseInt(ctx.getGlobal("HEADCURRROW")));

      trans.clear();   

      sCustomerNum1 = headset.getRow().getValue("CUSTOMER_NO");
      sContract1 = headset.getRow().getValue("CONTRACT");
      sWorkTID1 = headset.getRow().getValue("WORK_TYPE_ID");
      sMchCodeDesc1 = headset.getRow().getValue("MCH_CODE_DESCRIPTION");
      sWorkTypeDesc1 = headset.getRow().getValue("WORKTYPEDESCRIPTION");     
      sAgreementID1 = headset.getRow().getValue("AGREEMENT_ID");
      nSettingTime1 = (int)headset.getRow().getNumberValue("SETTINGTIME");

      if (isNaN(nSettingTime1))
         nSettingTime1=0;

      //sStdJobID1 = headset.getRow().getValue("STD_JOB_ID");     
      sPlanHrs1 = headset.getRow().getNumberValue("PLAN_HRS");

      if (isNaN(sPlanHrs1))
         sPlanHrs1 = 0;

      //sWorkDesrLo1 = headset.getRow().getValue("WORK_DESCR_LO");
      sAuthorizeCode1 = headset.getRow().getValue("AUTHORIZE_CODE");     
      sPlanStartDate1 = headset.getRow().getFieldValue("PLAN_S_DATE");
      sPlanCompleteDate1 = headset.getRow().getFieldValue("PLAN_F_DATE");
      sOrgCode1 = headset.getRow().getValue("ORG_CODE");     
      sRequiredStartDate1 = headset.getRow().getFieldValue("REQUIRED_START_DATE");
      sErrorSymptom1 = headset.getRow().getValue("ERR_SYMPTOM");
      sSymptomDescription1 = headset.getRow().getValue("SYMPTOMDESCRIPTION");  
      nWarrantyRowNo1 = (int)headset.getRow().getNumberValue("WARRANTY_ROW_NO"); 
      sAddressID1 = headset.getRow().getValue("ADDRESS_ID"); 
      s1Address1 = headset.getRow().getValue("ADDRESS1"); 
      s2Address1 = headset.getRow().getValue("ADDRESS2"); 
      s3Address1 = headset.getRow().getValue("ADDRESS3"); 
      s4Address1 = headset.getRow().getValue("ADDRESS4"); 
      s5Address1 = headset.getRow().getValue("ADDRESS5"); 
      s6Address1 = headset.getRow().getValue("ADDRESS6"); 
      sCbHasAgreement1 = headset.getRow().getValue("CBHASAGREEMENT"); 
      sCbHasActiveWorkOrder1 = headset.getRow().getValue("CBHASACTIVEWO");

      //Bug Id 70921, Start
      sWoNo = headset.getRow().getValue("WO_NO");
      String sPriorityId   = headset.getRow().getValue("PRIORITY_ID");
      String sDummyReqStartDate = headset.getRow().getFieldValue("REQUIRED_START_DATE");
      String sDummyReqEndDate = headset.getRow().getFieldValue("REQUIRED_END_DATE");
      String sRegDate = headset.getRow().getFieldValue("REG_DATE");
      String sSavedSrvcon = "";
      //Bug Id 70921, End

      sContract1 = headset.getRow().getValue("CONTRACT");

      sObjectID1 = sObjectID;
      sWorkTID1 = sWorkTID;
//		  Bug 68362, Start        
      sConnectionType1 = headset.getRow().getValue("CONNECTION_TYPE");
      sConnectionTypeDB1 = headset.getRow().getValue("CONNECTION_TYPE_DB");
//		  Bug 68362, End        

      if (checksec("Maintenance_Object_API.Get_Mch_Name",1,isSecure))
      {
         cmd = trans.addCustomFunction("SMCHCODEION","Maintenance_Object_API.Get_Mch_Name","MCH_CODE_DESCRIPTION");
         cmd.addParameter("CONTRACT",sMchContract);
         cmd.addParameter("MCH_CODE",sObjectID1);
      }

      if (checksec("WORK_TYPE_API.Get_Description",2,isSecure))
      {
         cmd = trans.addCustomFunction("WORKTYPTION","WORK_TYPE_API.Get_Description","WORKTYPEDESCRIPTION");
         cmd.addParameter("WORK_TYPE_ID",sWorkTID1);
      }

      trans = mgr.perform(trans); 

      ref = 0;

      if (isSecure[ref += 1] =="true")
      {
         sMchCodeDesc = trans.getValue("SMCHCODEION/DATA/MCH_CODE_DESCRIPTION");
      }
      else
         sMchCodeDesc = "";

      if (isSecure[ref += 1] =="true")
      {
         sWorkTypeDesc = trans.getValue("WORKTYPTION/DATA/WORKTYPEDESCRIPTION");
      }
      else
         sWorkTypeDesc = ""; 


      sMchCodeDesc1 = sMchCodeDesc;
      sWorkTypeDesc1 = sWorkTypeDesc;
      sAddressID1 = sAddressID;   
      sAddressIdSave = sAddressID1;

      trans.clear();

      if (!mgr.isEmpty(sAddressIdSave))
      {

         if (checksec("Equipment_Object_Address_API.Get_Address1",1,isSecure))
         {
            cmd = trans.addCustomFunction("SSADS1","Equipment_Object_Address_API.Get_Address1","ADDRESS1");
            cmd.addParameter("CONTRACT",sMchContract);
            cmd.addParameter("MCH_CODE",sObjectID1);
            cmd.addParameter("ADDRESS_ID",sAddressIdSave);
         }

         if (checksec("Equipment_Object_Address_API.Get_Address2",2,isSecure))
         {
            cmd = trans.addCustomFunction("SSADS2","Equipment_Object_Address_API.Get_Address2","ADDRESS2");
            cmd.addParameter("CONTRACT",sMchContract);
            cmd.addParameter("MCH_CODE",sObjectID1);
            cmd.addParameter("ADDRESS_ID",sAddressIdSave);
         }

         if (checksec("Equipment_Object_Address_API.Get_Address3",3,isSecure))
         {
            cmd = trans.addCustomFunction("SSADS3","Equipment_Object_Address_API.Get_Address3","ADDRESS3");
            cmd.addParameter("CONTRACT",sMchContract);
            cmd.addParameter("MCH_CODE",sObjectID1);
            cmd.addParameter("ADDRESS_ID",sAddressIdSave);
         }

         if (checksec("Equipment_Object_Address_API.Get_Address4",4,isSecure))
         {
            cmd = trans.addCustomFunction("SSADS4","Equipment_Object_Address_API.Get_Address4","ADDRESS4");
            cmd.addParameter("CONTRACT",sMchContract);
            cmd.addParameter("MCH_CODE",sObjectID1);
            cmd.addParameter("ADDRESS_ID",sAddressIdSave);
         }

         if (checksec("Equipment_Object_Address_API.Get_Address5",5,isSecure))
         {
            cmd = trans.addCustomFunction("SSADS5","Equipment_Object_Address_API.Get_Address5","ADDRESS5");
            cmd.addParameter("CONTRACT",sMchContract);
            cmd.addParameter("MCH_CODE",sObjectID1);
            cmd.addParameter("ADDRESS_ID",sAddressIdSave);
         }

         if (checksec("Equipment_Object_Address_API.Get_Address6",6,isSecure))
         {
            cmd = trans.addCustomFunction("SSADS6","Equipment_Object_Address_API.Get_Address6","ADDRESS6");
            cmd.addParameter("CONTRACT",sMchContract);
            cmd.addParameter("MCH_CODE",sObjectID1);
            cmd.addParameter("ADDRESS_ID",sAddressIdSave);
         }

         trans = mgr.perform(trans);

         ref = 0;

         if (isSecure[ref += 1] =="true")
         {
            s1Address1 = trans.getValue("SSADS1/DATA/ADDRESS1");
         }
         else
            s1Address1 = "";  

         if (isSecure[ref += 1] =="true")
         {
            s2Address1 = trans.getValue("SSADS2/DATA/ADDRESS2");
         }
         else
            s2Address1 = "";                         

         if (isSecure[ref += 1] =="true")
         {
            s3Address1 = trans.getValue("SSADS3/DATA/ADDRESS3");
         }
         else
            s3Address1 = "";  

         if (isSecure[ref += 1] =="true")
         {
            s4Address1 = trans.getValue("SSADS4/DATA/ADDRESS4");
         }
         else
            s4Address1 = "";  

         if (isSecure[ref += 1] =="true")
         {
            s5Address1 = trans.getValue("SSADS5/DATA/ADDRESS5");
         }
         else
            s5Address1 = "";  

         if (isSecure[ref += 1] =="true")
         {
            s6Address1 = trans.getValue("SSADS6/DATA/ADDRESS6");
         }
         else
            s6Address1 = "";  
      }
      else
      {
         s1Address1 = "";
         s2Address1 = "";
         s3Address1 = "";
         s4Address1 = "";
         s5Address1 = "";
         s6Address1 = "";
      }

      trans.clear();

//		  Bug 68362, Start        
      cmd = trans.addCustomFunction("GETISCATOBJ","Equipment_Object_Api.Get_Is_Category_Object","ISCATOBJ");
      cmd.addParameter("CONTRACT",sMchContract);
      cmd.addParameter("MCH_CODE",sObjectID1);

      trans = mgr.perform(trans);

      String isCat = trans.getValue("GETISCATOBJ/DATA/ISCATOBJ");
      if ("TRUE".equals(isCat))
      {
         trans.clear();
         cmd = trans.addCustomFunction("CONNTYPEDB","Maint_Connection_Type_API.Get_Db_Value","CONNECTION_TYPE_DB");
         cmd.addParameter("CONNECTION_TYPE_DB", "3");

         cmd = trans.addCustomFunction("CONNTYPE","Maint_Connection_Type_API.Decode","CONNECTION_TYPE");
         cmd.addReference("CONNECTION_TYPE_DB","CONNTYPEDB/DATA");

         trans = mgr.perform(trans);

         sConnectionTypeDB = trans.getValue("CONNTYPEDB/DATA/CONNECTION_TYPE_DB");
         sConnectionType = trans.getValue("CONNTYPE/DATA/CONNECTION_TYPE");
      }
      else if ("CATEGORY".equals(sConnectionTypeDB1))
      {
         trans.clear();
         cmd = trans.addCustomFunction("CONNTYPEDB","Maint_Connection_Type_API.Get_Db_Value","CONNECTION_TYPE_DB");
         cmd.addParameter("CONNECTION_TYPE_DB", "0");

         cmd = trans.addCustomFunction("CONNTYPE","Maint_Connection_Type_API.Decode","CONNECTION_TYPE");
         cmd.addReference("CONNECTION_TYPE_DB","CONNTYPEDB/DATA");

         trans = mgr.perform(trans);

         sConnectionTypeDB = trans.getValue("CONNTYPEDB/DATA/CONNECTION_TYPE_DB");
         sConnectionType = trans.getValue("CONNTYPE/DATA/CONNECTION_TYPE");
      }
      else
      {
         sConnectionTypeDB = sConnectionTypeDB1;            
         sConnectionType = sConnectionType1;
      }

      trans.clear();
//		  Bug 68362, End        
      if (checksec("Psc_Contr_Product_API.Has_Contract_For_Object",1,isSecure))
      {
         cmd = trans.addCustomFunction("ISHASAGR","Psc_Contr_Product_API.Has_Contract_For_Object","ISHASAGREEMENT");
         cmd.addParameter("CONNECTION_TYPE_DB");
         cmd.addParameter("MCH_CODE");
         cmd.addParameter("MCH_CODE_CONTRACT");
         cmd.addParameter("CUSTOMER_NO");
      }

      trans = mgr.perform(trans);

      ref = 0;

      if (isSecure[ref += 1] =="true")
      {
         sIsHasAgreements = trans.getValue("ISHASAGR/DATA/ISHASAGREEMENT");
      }
      else
         sIsHasAgreements = "";  

      if (!mgr.isEmpty(sIsHasAgreements))
         sCbHasAgreement1 = "TRUE";
      else
         sCbHasAgreement1 = "FALSE";

      sContract = sContract1;
      sObjectID = sObjectID1; 
      sCustomerNum = headset.getValue("CUSTOMER_NO");
      sIsHasActiveWorkOrder = "";

      trans.clear();

      if (checksec("Active_Work_Order_API.Obj_Has_Wo",1,isSecure))
      {
         cmd = trans.addCustomFunction("ISACTWOR","Active_Work_Order_API.Obj_Has_Wo","ISHASACTIVEWORKORDER");
         cmd.addParameter("CONTRACT",sMchContract);
         cmd.addParameter("MCH_CODE",sObjectID);
      }

      trans = mgr.perform(trans);

      ref = 0;

      if (isSecure[ref += 1] =="true")
      {
         sIsHasActiveWorkOrder = trans.getValue("ISACTWOR/DATA/ISHASACTIVEWORKORDER");
      }
      else
         sIsHasActiveWorkOrder = ""; 

      if (!("FALSE".equals(sIsHasActiveWorkOrder)))
         sCbHasActiveWorkOrder1 = "TRUE";
      else
         sCbHasActiveWorkOrder1 = "FALSE";

      if (!mgr.isEmpty(sAgreementID))
      {
         sAgreementID1 = sAgreementID;

         trans.clear();

         if (checksec("Psc_Contr_Product_API.Get_Fault_Report_Agreement",1,isSecure))
         {
            cmd = trans.addCustomFunction("ISFUALAGREE","Psc_Contr_Product_API.Get_Fault_Report_Agreement","ISFAULTREPAGREE");
            cmd.addParameter("CONTRACT_ID",mgr.readValue("CONTRACT_ID"));
            cmd.addParameter("LINE_NO",mgr.readValue("LINE_NO"));
         }

         trans = mgr.perform(trans);

         ref = 0;

         if (isSecure[ref += 1] =="true")
         {
            sIsFaultReportAgreement = trans.getValue("ISFUALAGREE/DATA/ISFAULTREPAGREE");
         }
         else
            sIsFaultReportAgreement = "";  

         trans.clear();

         if (checksec("Fault_Report_Agreement_API.Get_Client_Value",1,isSecure))
         {
            cmd = trans.addCustomFunction("CLIAGREEREP","Fault_Report_Agreement_API.Get_Client_Value(0)","ISCLIENTFAUREPAGREE");
         }

         trans = mgr.perform(trans);

         ref = 0;

         if (isSecure[ref += 1] =="true")
         {
            sIsClientFaultReportAgreement = trans.getValue("CLIAGREEREP/DATA/ISCLIENTFAUREPAGREE");
         }
         else
            sIsClientFaultReportAgreement   = "";

         if (sIsClientFaultReportAgreement.equals(sIsFaultReportAgreement))
         {
            sAgreementID = "";
         }
      }

      trans.clear();

      if (mgr.isEmpty(sAgreementID))
      {
         sOrgCode = "";
         nSettingTime = 0;
         sAgreementID1 = sAgreementID;
         sSettingTime = nSettingTime+"";
         nSettingTime1 = nSettingTime;

         if (mgr.isEmpty(headset.getRow().getValue("WARRANTY_ROW_NO")))
         {
            sStdJobID1 = "";
         }

         sPlanHrs1 = 0;
         //sWorkDesrLo1 = "";
         sAuthorizeCode1 = "";

         sPlanStartDate1 = headset.getRow().getFieldValue("REG_DATE");
         sPlanCompleteDate1 = sPlanStartDate1;
         sPlanCompleteDate1 = "";
         sRequiredEndDate1 = "";
      }
      else
      {
         sAgreementID1 = sAgreementID;  

         if (!mgr.isEmpty(sOrgCode))
         {
            sOrgCode1 = sOrgCode;
         }

         sSettingTime1 = nSettingTime+"";
         nSettingTime1 = nSettingTime;  

         trans.clear();

         if (checksec("Psc_Contr_Product_API.Get_Std_Job_Id",1,isSecure))
         {
            cmd = trans.addCustomFunction("ISSTDJ","Psc_Contr_Product_API.Get_Std_Job_Id","ISSTDJOBID");
            cmd.addParameter("CONTRACT_ID",mgr.readValue("CONTRACT_ID"));
            cmd.addParameter("LINE_NO",mgr.readValue("LINE_NO"));
         }

         if (checksec("Psc_Contr_Product_API.Get_Max_Resolution_Time",2,isSecure))
         {
            cmd = trans.addCustomFunction("ISEXUTTI","Psc_Contr_Product_API.Get_Max_Resolution_Time","ISEXECUTETIME");
            cmd.addParameter("CONTRACT_ID",mgr.readValue("CONTRACT_ID"));
            cmd.addParameter("LINE_NO",mgr.readValue("LINE_NO"));
         }

         if (checksec("Customer_Agreement_API.Get_Authorize_Code",3,isSecure))
         {
            cmd = trans.addCustomFunction("ISAUTHCO","Customer_Agreement_API.Get_Authorize_Code","ISAUTHRIZECODE");
            cmd.addParameter("AGREEMENT_ID",sAgreementID1);
         }

         trans = mgr.perform(trans);

         ref = 0;

         if (isSecure[ref += 1] =="true")
         {
            sIsStdJobID = trans.getValue("ISSTDJ/DATA/ISSTDJOBID");
         }
         else
            sIsStdJobID = "";

         if (isSecure[ref += 1] =="true")
         {
            sIsExecutionTime = (int)trans.getNumberValue("ISEXUTTI/DATA/ISEXECUTETIME");
         }
         else
            sIsExecutionTime = 0;

         if (isSecure[ref += 1] =="true")
         {
            sIsAuthorizeCode = trans.getValue("ISAUTHCO/DATA/ISAUTHRIZECODE");
         }
         else
            sIsAuthorizeCode = "";

         if (!mgr.isEmpty(sIsStdJobID))
         {
            sStdJobID1 = sIsStdJobID;
         }

         sPlanHrs1 = toDouble(sIsExecutionTime);
         sAuthorizeCode1 = sIsAuthorizeCode;

         trans.clear();

         trans = mgr.perform(trans);

         ref = 0;

         nSettingTime = nSettingTime1;

         trans.clear();      

         if (checksec("Maintenance_Site_Utility_API.Get_Site_Date",1,isSecure))
         {
            cmd = trans.addCustomFunction("SYDATES","Maintenance_Site_Utility_API.Get_Site_Date","VALSYDATE");
            cmd.addParameter("CONTRACT",sContract1);
         }

         trans = mgr.perform(trans);

         ref = 0;

         if (isSecure[ref += 1] =="true")
         {
            sSysDate  = trans.getBuffer("SYDATES/DATA").getFieldValue("VALSYDATE");
         }
         else
            sSysDate    = "";

         if (isNaN(nSettingTime1))
            nSettingTime1 = 0;

         if (nSettingTime1 == 0)
         {
            nSettingTime = 0;
         }

         //Bug Id 70921, Removed code here
      } 

      trans.clear();

      if (sReturnedCommand == 1)
      {
         if (!mgr.isEmpty(sErrorSymptom) && !(sErrorSymptom.equals("null")))
         {
            sErrorSymptom1 = sErrorSymptom;
         }

         nWarrantyRowNo1 = nWarrantyRowNo;

      }

      if (sReturnedCommand == 2)
      {
         if (!mgr.isEmpty(sStdJobID))
         {
            sStdJobID1 = sStdJobID;
         }

         if (!mgr.isEmpty(sErrorSymptom) && !(sErrorSymptom.equals("null")))
         {
            sErrorSymptom1 = sErrorSymptom;
         }

         nWarrantyRowNo1 = nWarrantyRowNo;

         trans.clear();

         if (checksec("WORK_ORDER_SYMPT_CODE_API.Get_Description",1,isSecure))
         {
            cmd = trans.addCustomFunction("ISSYMDES","WORK_ORDER_SYMPT_CODE_API.Get_Description","ISSYMPTDESC");
            cmd.addParameter("ERR_SYMPTOM",sErrorSymptom1);
         }

         trans = mgr.perform(trans);      

         ref = 0;



         if (isSecure[ref += 1] =="true")
         {
            sIsSymptCodeDesc = trans.getValue("ISSYMDES/DATA/ISSYMPTDESC");
         }
         else
            sIsSymptCodeDesc = "";

         sSymptomDescription1 = sIsSymptCodeDesc;

      }

      trans.clear(); 

      headlay.setLayoutMode(headlay.EDIT_LAYOUT);
      trans.clear();

      buf = mgr.newASPBuffer();

      buf.addFieldItem("PLAN_S_DATE",sPlanStartDate1);
      buf.addFieldItem("REQUIRED_START_DATE",sRequiredStartDate1);
      buf.addFieldItem("PLAN_F_DATE",sPlanCompleteDate1);
      buf.addFieldItem("REQUIRED_END_DATE",sRequiredEndDate1);
      //Bug Id 70921, Start
      buf.addFieldItem("CONTRACT_ID",sContractId);
      buf.addFieldItem("LINE_NO",sLineNo);
      buf.addFieldItem("WO_NO",sWoNo);
      buf.addFieldItem("PRIORITY_ID",sPriorityId);
      buf.addFieldItem("REG_DATE",sRegDate);
      
      //Bug Id 70921, End


      sPlanStartDate1 =buf.getValue("PLAN_S_DATE");
      sRequiredStartDate1 =  buf.getValue("REQUIRED_START_DATE");
      sPlanCompleteDate1 = buf.getValue("PLAN_F_DATE");
      sRequiredEndDate1 = buf.getValue("REQUIRED_END_DATE");

      if ("null".equals(sCustomerNum1))
         sCustomerNum1 = "";
      if ("null".equals(sWorkTID1))
         sWorkTID1 = "";
      if ("null".equals(sAgreementID1))
         sAgreementID1 = "";
      if ("null".equals(sObjectID))
         sObjectID = "";
      if ("null".equals(sMchContract))
         sMchContract = "";

      //Bug Id 70921, Start
      if (!mgr.isEmpty(sContractId))
      {

         buf = calculateDatesForSrvcon(buf);
          //Bug Id 84436, Start
        if (checksec("SC_SERVICE_CONTRACT_API.Get_Authorize_Code",1,isSecure))
         {
         cmd = trans.addCustomFunction("SCCONTRACTAUTH", "SC_SERVICE_CONTRACT_API.Get_Authorize_Code", "AUTHORIZE_CODE");
         cmd.addParameter("CONTRACT_ID", sContractId);
         }
         
         if (checksec("PSC_CONTR_PRODUCT_API.Get_Setting_Time",1,isSecure))
         {
         cmd = trans.addCustomFunction("PSCCONTRSETTINGTIME", "PSC_CONTR_PRODUCT_API.Get_Setting_Time", "SETTINGTIME");
         cmd.addParameter("CONTRACT_ID", sContractId);
         cmd.addParameter("LINE_NO", sLineNo);
         }
         //Bug 84436, End
         trans = mgr.perform(trans);

         sAuthorizeCode1 = trans.getBuffer("SCCONTRACTAUTH/DATA").getValue("AUTHORIZE_CODE");
         nSettingTime1 = (int)trans.getBuffer("PSCCONTRSETTINGTIME/DATA").getNumberValue("SETTINGTIME");
         trans.clear();

         sPlanHrs1 = buf.getNumberValue("PLAN_HRS");
         sPlanStartDate1 = buf.getValue("PLAN_S_DATE");
         sRequiredStartDate1 =  buf.getValue("REQUIRED_START_DATE");
         sPlanCompleteDate1 = buf.getValue("PLAN_F_DATE");
         sRequiredEndDate1 = buf.getValue("REQUIRED_END_DATE");
         sPriorityId = buf.getValue("PRIORITY_ID");
         sSavedSrvcon = buf.getValue("DUMMY_CONTRACT_ID");
      }

      if (mgr.isEmpty(sSavedSrvcon) && !mgr.isEmpty(sContractId) && !mgr.isEmpty(sLineNo) && (!mgr.isEmpty(sDummyReqStartDate) || !mgr.isEmpty(sDummyReqEndDate)) && bShowWarning)
      {
         mgr.showAlert(mgr.translate("SRVCONDATEOVERWRITE1: Existing Date values in fields Required Start or Latest Completion will be replaced with the dates from Service Contract Id &1 Line No &2.",sContractId,sLineNo));
         bShowWarning = false;
      }

      //Bug Id 70921, End



      row = headset.getRow();

      row.setValue("CUSTOMER_NO",((sCustomerNum1 == "") ? "":sCustomerNum1));
      row.setValue("WORK_TYPE_ID",((sWorkTID1 == "") ? "":sWorkTID1));
      row.setValue("WORKTYPEDESCRIPTION",((sWorkTypeDesc1 == "''") ? "":sWorkTypeDesc1));     
      row.setValue("AGREEMENT_ID",((sAgreementID1 == "") ? "":sAgreementID1));
      row.setNumberValue("SETTINGTIME",((nSettingTime1 == 0) ? 0:nSettingTime1));
      //row.setValue("STD_JOB_ID",((sStdJobID1 == "") ? "":sStdJobID1));     
      row.setNumberValue("PLAN_HRS",((sPlanHrs1 == 0) ? 0:sPlanHrs1));
      //row.setValue("WORK_DESCR_LO",((sWorkDesrLo1 == "") ? "":sWorkDesrLo1));
      row.setValue("AUTHORIZE_CODE",((sAuthorizeCode1 == "") ? "":sAuthorizeCode1));     
      row.setValue("PLAN_S_DATE",((sPlanStartDate1 == "") ? "":sPlanStartDate1));
      row.setValue("PLAN_F_DATE",((sPlanCompleteDate1 == "") ? "":sPlanCompleteDate1));
      row.setValue("ORG_CODE",((sOrgCode1 == "") ? "":sOrgCode1));     
      row.setValue("REQUIRED_START_DATE",((sRequiredStartDate1 == "") ? "":sRequiredStartDate1));
      row.setValue("REQUIRED_END_DATE",((sRequiredEndDate1 == "") ? "":sRequiredEndDate1));
      row.setValue("ERR_SYMPTOM",((sErrorSymptom1 == "") ? "":sErrorSymptom1));
      row.setValue("SYMPTOMDESCRIPTION",((sSymptomDescription1 == "") ? "":sSymptomDescription1));     
      row.setNumberValue("WARRANTY_ROW_NO",((nWarrantyRowNo1 == 0) ? 0:nWarrantyRowNo1));
      row.setValue("MCH_CODE",sObjectID);
      row.setValue("MCH_CODE_CONTRACT",sMchContract);
      row.setValue("MCH_CODE_DESCRIPTION",((sMchCodeDesc1 == "") ? "":sMchCodeDesc1));        
      row.setValue("ADDRESS_ID",((sAddressID1 == "") ? "":sAddressID1));        
      row.setValue("ADDRESS1",((s1Address1 == "") ? "":s1Address1));        
      row.setValue("ADDRESS2",((s2Address1 == "") ? "":s2Address1));        
      row.setValue("ADDRESS3",((s3Address1 == "") ? "":s3Address1));        
      row.setValue("ADDRESS4",((s4Address1 == "") ? "":s4Address1));        
      row.setValue("ADDRESS5",((s5Address1 == "") ? "":s5Address1));        
      row.setValue("ADDRESS6",((s6Address1 == "") ? "":s6Address1));        
      row.setValue("CBHASAGREEMENT",((sCbHasAgreement1 == "") ? "":sCbHasAgreement1));        
      row.setValue("CBHASACTIVEWO",((sCbHasActiveWorkOrder1 == "") ? "":sCbHasActiveWorkOrder1));  
      row.setValue("CONTRACT_ID",((sContractId == "") ? "":sContractId)); 
      row.setValue("LINE_NO",((sLineNo == "") ? "":sLineNo)); 
      row.setValue("AGREEMENT_ID",((sAgreementID == "") ? "":sAgreementID)); 
//		  Bug 68362, Start        
      row.setValue("CONNECTION_TYPE_DB",((sConnectionTypeDB == "") ? "":sConnectionTypeDB)); 
      row.setValue("CONNECTION_TYPE",((sConnectionType == "") ? "":sConnectionType)); 
//		  Bug 68362, End   
      //Bug Id 70921, Start
      row.setValue("PRIORITY_ID",((sPriorityId == "") ? "":sPriorityId)); 
      row.setValue("DUMMY_REQ_START_DATE",((sRequiredStartDate1 == "") ? "":sRequiredStartDate1));
      row.setValue("DUMMY_REQ_END_DATE",((sRequiredEndDate1 == "") ? "":sRequiredEndDate1));

      //Bug Id 70921, End

      headset.setRow(row);
      trans.clear(); 

      edited = true;
      editedCust = false;
      custFlag ="TRUE";

      //Bug Id 70921, Start
      sSkipDateOverwrite = "TRUE";
      ctx.setGlobal("SKIPDATEOVERWITE",sSkipDateOverwrite);
      //Bug Id 70921, End
   }

   public void findAndEditCustomer()
   {
      int ref;
      isSecure = new String[7];
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(headblk);
      q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
      q.addWhereCondition("OBJSTATE = 'FAULTREPORT' OR OBJSTATE = 'WORKREQUEST' OR OBJSTATE = 'OBSERVED' OR OBJSTATE = 'UNDERPREPARATION' OR OBJSTATE = 'PREPARED'");
      q.includeMeta("ALL");

      q = trans.addEmptyQuery(eventblk);
      q.addMasterConnection("HEAD","WO_NO","EVNTBLK_WO_NO");
      q.addMasterConnection("HEAD","CONTRACT","EVNTBLK_CONTRACT");
      q.includeMeta("ALL");

      mgr.submit(trans);

      headset.goTo(Integer.parseInt(ctx.getGlobal("HEADCURRROW")));

      trans.clear();  

      sCustomerNum1 = sCustomerNum;
      sContract1 = headset.getRow().getValue("CONTRACT");
      sWorkTID1 = sWorkTID;
      sObjectID1 = headset.getRow().getValue("MCH_CODE");
      sMchContract = headset.getRow().getValue("MCH_CODE_CONTRACT");
      sWorkTypeDesc1 = headset.getRow().getValue("WORKTYPEDESCRIPTION");     
      sAgreementID1 = headset.getRow().getValue("AGREEMENT_ID");
      String sAgreementID1desc = headset.getRow().getValue("CUSTOMERAGREEMENTDESCRIPTION");
      nSettingTime1 = (int)headset.getRow().getNumberValue("SETTINGTIME");
      //sStdJobID1 = headset.getRow().getValue("STD_JOB_ID");     
      sPlanHrs1 = headset.getRow().getNumberValue("PLAN_HRS");
      //sWorkDesrLo1 = headset.getRow().getValue("WORK_DESCR_LO");
      sAuthorizeCode1 = headset.getRow().getValue("AUTHORIZE_CODE");     
      sPlanStartDate1 = headset.getRow().getFieldValue("PLAN_S_DATE");
      sPlanCompleteDate1 = headset.getRow().getFieldValue("PLAN_F_DATE");
      sOrgCode1 = headset.getRow().getValue("ORG_CODE");     
      sRequiredStartDate1 = headset.getRow().getFieldValue("REQUIRED_START_DATE");
      sErrorSymptom1 = headset.getRow().getValue("ERR_SYMPTOM");
      sSymptomDescription1 = headset.getRow().getValue("SYMPTOMDESCRIPTION");  
      nWarrantyRowNo1 = (int)headset.getRow().getNumberValue("WARRANTY_ROW_NO");
      //Bug Id 70921, Start
      sWoNo = headset.getRow().getValue("WO_NO");
      String sSavedSrvcon = "";
      String sDummyReqStartDate = headset.getRow().getFieldValue("REQUIRED_START_DATE");
      String sDummyReqEndDate = headset.getRow().getFieldValue("REQUIRED_END_DATE");
      String sRegDate = headset.getRow().getFieldValue("REG_DATE");
      //Bug Id 70921, End

      String sPriorityId   = headset.getRow().getValue("PRIORITY_ID");

      if (checksec("Psc_Contr_Product_API.Get_Fault_Report_Agreement",1,isSecure))
      {
         cmd = trans.addCustomFunction("WRKTYPDESC","WORK_TYPE_API.Get_Description","WORKTYPEDESCRIPTION");
         cmd.addParameter("WORK_TYPE_ID",sWorkTID);
      }

      trans = mgr.perform(trans);

      ref = 0;

      if (isSecure[ref += 1] =="true")
      {
         sWorkTypeDesc = trans.getValue("WRKTYPDESC/DATA/WORKTYPEDESCRIPTION");
      }
      else
         sWorkTypeDesc = "";   

      sCustomerNum1 = sCustomerNum;
      sWorkTID1 = sWorkTID;
      sWorkTypeDesc1 = sWorkTypeDesc;

      trans.clear();

//      Bug 68363, Removed validation for Agreement id and added validation for contract id 
      if (sReturnedCommand == 1)
      {
         if (!mgr.isEmpty(sErrorSymptom) && !(sErrorSymptom.equals("null")))
         {
            sErrorSymptom1 = sErrorSymptom;
         }

         nWarrantyRowNo1 = nWarrantyRowNo;

      }

      if (sReturnedCommand == 2)
      {
         if (!mgr.isEmpty(sStdJobID))
         {
            sStdJobID1 = sStdJobID;
         }

         if (!mgr.isEmpty(sErrorSymptom) && !(sErrorSymptom.equals("null")))
         {
            sErrorSymptom1 = sErrorSymptom;
         }

         nWarrantyRowNo1 = nWarrantyRowNo;

         trans.clear();

         if (checksec("WORK_ORDER_SYMPT_CODE_API.Get_Description",1,isSecure))
         {
            cmd = trans.addCustomFunction("ISSYMDES","WORK_ORDER_SYMPT_CODE_API.Get_Description","ISSYMPTDESC");
            cmd.addParameter("ERR_SYMPTOM",sErrorSymptom1);
         }

         trans = mgr.perform(trans);      

         ref = 0;

         if (isSecure[ref += 1] =="true")
         {
            sIsSymptCodeDesc = trans.getValue("ISSYMDES/DATA/ISSYMPTDESC");
         }
         else
            sIsSymptCodeDesc = "";

         sSymptomDescription1 = sIsSymptCodeDesc;

      }

      if (checksec("Cust_Ord_Customer_API.Customer_Is_Credit_Stopped",1,isSecure))
      {

         sCustomerCreditStoped = 2;

         trans.clear();

         cmd = trans.addCustomFunction("ISCUSSTO","Cust_Ord_Customer_API.Customer_Is_Credit_Stopped","ISCUSTCREDIRSTOP");
         cmd.addParameter("CUSTOMER_NO",sCustomerNum1);
         cmd.addParameter("COMPANY");
      }

      trans = mgr.perform(trans);

      ref = 0;

      if (isSecure[ref += 1] =="true")
      {
         sCustomerCreditStoped = (int)trans.getNumberValue("ISCUSSTO/DATA/ISCUSTCREDIRSTOP");
      }
      else
         sCustomerCreditStoped = 0;

      sIsHasAgreements = "";

      trans.clear();   

      headlay.setLayoutMode(headlay.EDIT_LAYOUT);
      trans.clear();

      buf = mgr.newASPBuffer();

      buf.addFieldItem("PLAN_S_DATE",sPlanStartDate1);
      buf.addFieldItem("REQUIRED_START_DATE",sRequiredStartDate1);
      buf.addFieldItem("PLAN_F_DATE",sPlanCompleteDate1);
      buf.addFieldItem("REQUIRED_END_DATE",sRequiredEndDate1);
      //Bug Id 70921, Start
      buf.addFieldItem("CONTRACT_ID",sContractId);
      buf.addFieldItem("LINE_NO",sLineNo);
      buf.addFieldItem("WO_NO",sWoNo);
      buf.addFieldItem("PRIORITY_ID",sPriorityId);
      buf.addFieldItem("REG_DATE",sRegDate);
      //Bug Id 70921, End

      sPlanStartDate1 =buf.getValue("PLAN_S_DATE");
      sRequiredStartDate1 =  buf.getValue("REQUIRED_START_DATE");
      sPlanCompleteDate1 = buf.getValue("PLAN_F_DATE");
      sRequiredEndDate1 = buf.getValue("REQUIRED_END_DATE");

//        Bug 68363, Start
      if (mgr.isEmpty (sAgreementID1))
         sAgreementID1desc ="";
      if ("null".equals(sCustomerNum1))
         sCustomerNum1 = "";
      else
         bFECust = true;
      if ("null".equals(sWorkTID1))
         sWorkTID1 = "";
      if ("null".equals(sAgreementID1))
         sAgreementID1 = "";

      //Bug Id 70921, Start
      if (!mgr.isEmpty(sContractId))
      {

         buf = calculateDatesForSrvcon(buf);
         //Bug 84436, Start
          if (checksec("SC_SERVICE_CONTRACT_API.Get_Authorize_Code",1,isSecure))
         {
         cmd = trans.addCustomFunction("SCCONTRACTAUTH", "SC_SERVICE_CONTRACT_API.Get_Authorize_Code", "AUTHORIZE_CODE");
         cmd.addParameter("CONTRACT_ID", sContractId);
          }
          if (checksec("PSC_CONTR_PRODUCT_API.Get_Setting_Time",1,isSecure))
         {
         cmd = trans.addCustomFunction("PSCCONTRSETTINGTIME", "PSC_CONTR_PRODUCT_API.Get_Setting_Time", "SETTINGTIME");
         cmd.addParameter("CONTRACT_ID", sContractId);
         cmd.addParameter("LINE_NO", sLineNo);
          }
         //Bug 84436, End

         trans = mgr.perform(trans);

         sAuthorizeCode1 = trans.getBuffer("SCCONTRACTAUTH/DATA").getValue("AUTHORIZE_CODE");
         nSettingTime1 = (int)trans.getBuffer("PSCCONTRSETTINGTIME/DATA").getNumberValue("SETTINGTIME");
         trans.clear();

         sPlanHrs1 = buf.getNumberValue("PLAN_HRS");
         sPlanStartDate1 = buf.getValue("PLAN_S_DATE");
         sRequiredStartDate1 =  buf.getValue("REQUIRED_START_DATE");
         sPlanCompleteDate1 = buf.getValue("PLAN_F_DATE");
         sRequiredEndDate1 = buf.getValue("REQUIRED_END_DATE");
         sPriorityId = buf.getValue("PRIORITY_ID");
         sSavedSrvcon = buf.getValue("DUMMY_CONTRACT_ID");
      }

      if (mgr.isEmpty(sSavedSrvcon) && !mgr.isEmpty(sContractId) && !mgr.isEmpty(sLineNo) && (!mgr.isEmpty(sDummyReqStartDate) || !mgr.isEmpty(sDummyReqEndDate)) && bShowWarning)
      {
         mgr.showAlert(mgr.translate("SRVCONDATEOVERWRITE1: Existing Date values in fields Required Start or Latest Completion will be replaced with the dates from Service Contract Id &1 Line No &2.",sContractId,sLineNo));
         bShowWarning = false;
      }
      //Bug Id 70921, End


      row = headset.getRow();  

      row.setValue("CUSTOMER_NO",((sCustomerNum1 == "") ? "":sCustomerNum1));
      row.setValue("WORK_TYPE_ID",((sWorkTID1 == "") ? "":sWorkTID1));
      row.setValue("WORKTYPEDESCRIPTION",((sWorkTypeDesc1 == "''") ? "":sWorkTypeDesc1));     
      row.setValue("AGREEMENT_ID",((sAgreementID1 == "") ? "":sAgreementID1));
      row.setValue("CUSTOMERAGREEMENTDESCRIPTION",((sAgreementID1desc == "") ? "":sAgreementID1desc));
      row.setNumberValue("SETTINGTIME",((nSettingTime1 == 0) ? 0:nSettingTime1));
      //row.setValue("STD_JOB_ID",((sStdJobID1 == "") ? "":sStdJobID1));     
      row.setNumberValue("PLAN_HRS",((sPlanHrs1 == 0) ? 0:sPlanHrs1));
      //row.setValue("WORK_DESCR_LO",((sWorkDesrLo1 == "") ? "":sWorkDesrLo1));
      row.setValue("AUTHORIZE_CODE",((sAuthorizeCode1 == "") ? "":sAuthorizeCode1)); 

      row.setValue("PLAN_S_DATE",((sPlanStartDate1 == "") ? "":sPlanStartDate1));
      row.setValue("PLAN_F_DATE",((sPlanCompleteDate1 == "") ? "":sPlanCompleteDate1));
      row.setValue("ORG_CODE",((sOrgCode1 == "") ? "":sOrgCode1));     
      row.setValue("REQUIRED_START_DATE",((sRequiredStartDate1 == "") ? "":sRequiredStartDate1));
      row.setValue("REQUIRED_END_DATE",((sRequiredEndDate1 == "") ? "":sRequiredEndDate1));
      row.setValue("ERR_SYMPTOM",((sErrorSymptom1 == "") ? "":sErrorSymptom1));
      row.setValue("SYMPTOMDESCRIPTION",((sSymptomDescription1 == "") ? "":sSymptomDescription1));     
      row.setNumberValue("WARRANTY_ROW_NO",((nWarrantyRowNo1 == 0) ? 0:nWarrantyRowNo1));    
      row.setValue("CONTRACT_ID",((sContractId == "") ? "":sContractId)); 
      row.setValue("LINE_NO",((sLineNo == "") ? "":sLineNo));

      //Bug Id 70921, Start
      row.setValue("DUMMY_REQ_START_DATE",((sRequiredStartDate1 == "") ? "":sRequiredStartDate1));
      row.setValue("DUMMY_REQ_END_DATE",((sRequiredEndDate1 == "") ? "":sRequiredEndDate1));
      //Bug Id 70921, End

      //  Bug 68363, End


      headset.setRow(row);

      trans.clear();

      if (checksec("Psc_Contr_Product_API.Has_Contract_For_Object",1,isSecure))
      {
         cmd = trans.addCustomFunction("ISHASAGR","Psc_Contr_Product_API.Has_Contract_For_Object","ISHASAGREEMENT");
         cmd.addParameter("CONNECTION_TYPE_DB");
         cmd.addParameter("MCH_CODE");
         cmd.addParameter("MCH_CODE_CONTRACT");
         cmd.addParameter("CUSTOMER_NO");
      }

      trans = mgr.perform(trans);

      ref = 0;

      if (isSecure[ref += 1] =="true")
      {
         sIsHasAgreements = trans.getValue("ISHASAGR/DATA/ISHASAGREEMENT");
      }
      else
         sIsHasAgreements = "";  

      row = headset.getRow();

      if (!mgr.isEmpty(sIsHasAgreements))
         row.setValue("CBHASAGREEMENT","1");
      else
         row.setValue("CBHASAGREEMENT","0");
      headset.setRow(row);

      trans.clear();   

      edited = false;
      editedCust = true;
      custFlag ="TRUE";

      //Bug Id 70921, Start
      sSkipDateOverwrite = "TRUE";
      ctx.setGlobal("SKIPDATEOVERWITE",sSkipDateOverwrite);
      //Bug Id 70921, End

   }

   public void clearAllItem()
   {
      headset.clear();
   }

//=============================================================================
//  Command Bar Edit Group functions
//=============================================================================

   public void okFind()
   {
      ASPManager mgr = getASPManager();

      //Refresh the itemset2 before querying to avoid unneccessary error messages.
      itemset2.clear();

      trans.clear();
      q = trans.addQuery(headblk);
      q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
      q.addWhereCondition("OBJSTATE = 'FAULTREPORT' OR OBJSTATE = 'WORKREQUEST' OR OBJSTATE = 'OBSERVED' OR OBJSTATE = 'UNDERPREPARATION' OR OBJSTATE = 'PREPARED'");

      if (mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());

      if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
      {
         q.addWhereCondition("WO_NO = ?");
         q.addParameter("WO_NO",mgr.readValue("WO_NO"));
      }
      q.includeMeta("ALL");

      mgr.querySubmit(trans,headblk);

      trans.clear();      
      q = trans.addEmptyQuery(eventblk);
      q.addMasterConnection("HEAD","WO_NO","EVNTBLK_WO_NO");
      q.addMasterConnection("HEAD","CONTRACT","EVNTBLK_CONTRACT");
      q.includeMeta("ALL");
      mgr.submit(trans);

      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE3NODATA: No data found."));
         headset.clear();
         itemset1.clear();
      }

      if (headset.countRows() == 1)
      {
         okFindITEM1();
         okFindITEM2();
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
      }

      if (headset.countRows()==1)
      {
         setCheckBoxValue();

         if ("1".equals(headset.getRow().getValue("EXCEPTION_EXISTS")))
            bException = true;
         else
            bException = false;
      }

      qrystr = mgr.createSearchURL(headblk);
   }

   public void okFindTrans()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer retBuffer;
      ASPBuffer retBufferCustOrder;
      ASPBuffer retBufferCustNo;
      ASPBuffer retBufferCustType;
      ASPBuffer retBufferCurrencyCode;
      String  curr_row_exists = "FALSE";

      trans.clear();
      q = trans.addQuery(headblk);
      q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
      q.addWhereCondition("OBJSTATE = 'FAULTREPORT' OR OBJSTATE = 'WORKREQUEST' OR OBJSTATE = 'OBSERVED' OR OBJSTATE = 'UNDERPREPARATION' OR OBJSTATE = 'PREPARED'");

      if (mgr.dataTransfered())
      {
         retBuffer =  mgr.getTransferedData();
         if (retBuffer.countItems()>3)
         {
            retBufferCustOrder = retBuffer.getBufferAt(3);
            retBufferCustNo = retBuffer.getBufferAt(0);
            retBufferCustType = retBuffer.getBufferAt(1);
            retBufferCurrencyCode = retBuffer.getBufferAt(2);

            if (retBufferCustNo.itemExists("CUSTOMER_NO"))
            {
               ret_cu_no = retBufferCustNo.getValue("CUSTOMER_NO");
            }
            if (retBufferCustType.itemExists("CUST_ORDER_TYPE"))
            {
               ret_cu_type = retBufferCustType.getValue("CUST_ORDER_TYPE");
            }
            if (retBufferCurrencyCode.itemExists("CURRENCY_CODE"))
            {
               ret_curr_code = retBufferCurrencyCode.getValue("CURRENCY_CODE");
            }

            if (retBufferCustOrder.itemExists("CUST_ORDER_NO"))
            {
               ctx.setGlobal("CALL_URL","CUSTORDER");
               ret_or_no = retBufferCustOrder.getValue("CUST_ORDER_NO");
               q.addWhereCondition("CUST_ORDER_NO = ?");
               q.addParameter("CUST_ORDER_NO",ret_or_no);
            }
            else
               q.addOrCondition(mgr.getTransferedData());
         }
         else if (retBuffer.itemExists("CURR_ROW"))
         {
            curr_row_exists = "TRUE";
            ret_data_buffer = retBuffer.getBuffer("ROWS");
            currrow = Integer.parseInt(retBuffer.getValue("CURR_ROW"));
            lout = retBuffer.getValue("LAYOUT");

            //String in_string = convertToString(ret_data_buffer.countItems()-1);
            //q.addWhereCondition("WO_NO IN (" + in_string + ")");
            q.addOrCondition(ret_data_buffer);
         }
         else
            q.addOrCondition(mgr.getTransferedData());
      }

      q.includeMeta("ALL");

      q = trans.addEmptyQuery(eventblk);
      q.addMasterConnection("HEAD","WO_NO","EVNTBLK_WO_NO");
      q.addMasterConnection("HEAD","CONTRACT","EVNTBLK_CONTRACT");
      q.includeMeta("ALL");

      mgr.submit(trans);

      if ("TRUE".equals(curr_row_exists))
      {
         headset.goTo(currrow);
         if ("1".equals(lout))
            headlay.setLayoutMode(headlay.MULTIROW_LAYOUT);
      }

      if (headset.countRows() == 1)
      {
         okFindITEM1();
         okFindITEM2();
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
      }

      if (headset.countRows() == 0)
      {
         trans.clear();
         trans_="TRUE";
         newRow();
      }

      qrystr = mgr.createSearchURL(headblk);
   }

   public void okFindEdit()
   {
      int ref;
      isSecure = new String[7];
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(headblk);
      q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
      q.addWhereCondition("OBJSTATE = 'FAULTREPORT' OR OBJSTATE = 'WORKREQUEST' OR OBJSTATE = 'OBSERVED' OR OBJSTATE = 'UNDERPREPARATION' OR OBJSTATE = 'PREPARED'");
      q.includeMeta("ALL");

      q = trans.addEmptyQuery(eventblk);
      q.addMasterConnection("HEAD","WO_NO","EVNTBLK_WO_NO");
      q.addMasterConnection("HEAD","CONTRACT","EVNTBLK_CONTRACT");
      q.includeMeta("ALL");

      mgr.submit(trans);

      headset.goTo(Integer.parseInt(ctx.getGlobal("HEADCURRROW")));

      trans.clear();         

      ASPBuffer cutAgreeObj;
      trans.clear();
      trans.addSecurityQuery("CUSTOMER_AGREEMENT_API","Get_Authorize_Code");
      trans = mgr.perform(trans);

      cutAgreeObj = trans.getSecurityInfo();
      trans.clear();

      if ( cutAgreeObj.itemExists("CUSTOMER_AGREEMENT_API.Get_Authorize_Code"))
         cusAgree = true;

      if (cusAgree )
      {
         cmd = trans.addCustomFunction("AUTHOCODE","Customer_Agreement_API.Get_Authorize_Code","AUTHORIZE_CODE");
         cmd.addParameter("AGREEMENT_ID",sentAgrmntId);
      }

      trans = mgr.perform(trans);

      if ( cusAgree)
      {
         sAuthoCode  = trans.getValue("AUTHOCODE/DATA/AUTHORIZE_CODE");
      }
      else
      {
         sAuthoCode = "";
      }

      trans.clear();
      headlay.setLayoutMode(headlay.EDIT_LAYOUT);
      ASPBuffer row = headset.getRow();
      row.setValue("AGREEMENT_ID", sentAgrmntId);
      row.setValue("AUTHORIZE_CODE",sAuthoCode);
      if (!mgr.isEmpty(sentWorkTypeId) && !(sentWorkTypeId.equals(headset.getRow().getValue("WORK_TYPE_ID"))))
      {

         row.setValue("WORK_TYPE_ID",sentWorkTypeId);
      }

      if (!mgr.isEmpty(sentWorkShop) && mgr.isEmpty(headset.getRow().getValue("ORG_CODE")))
      {

         row.setValue("ORG_CODE",sentWorkShop);
      }
      headset.setRow(row);
   }   

   public void back()
   {
      ASPManager mgr = getASPManager();
      mgr.redirectTo("../orderw/CustomerOrder.page?ORDER_NO="+mgr.URLEncode(ret_or_no));
   }

   public void okFindCase()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer woBuff = mgr.newASPBuffer();

      String woList = "";

      if (!mgr.isEmpty(caseId))
      {
         trans.clear();

         cmd = trans.addCustomFunction("CCWOLIST","CC_WORK_ORDER_API.Get_Work_Order_List","WOLIST");
         cmd.addParameter("CASE_ID", caseId);

         trans = mgr.perform(trans);

         woList = trans.getValue("CCWOLIST/DATA/WOLIST");
         while (woList.indexOf(31) >-1)
            woList = woList.replace((char)31,',');


         if (!mgr.isEmpty(woList))
            woList += ","+headset.getValue("WO_NO");
         else
            woList = headset.getValue("WO_NO");

         String wo[] = split(woList,",");
         for (int i = 0; i < wo.length; i++)
            woBuff.addItem("WO_NO",wo[i]);

         trans.clear();
         q = trans.addEmptyQuery(headblk);
         q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
         q.addWhereCondition("OBJSTATE = 'FAULTREPORT' OR OBJSTATE = 'WORKREQUEST' OR OBJSTATE = 'OBSERVED' OR OBJSTATE = 'UNDERPREPARATION' OR OBJSTATE = 'PREPARED'");
         q.addOrCondition(woBuff);
         q.setOrderByClause("WO_NO");
         q.includeMeta("ALL");

         mgr.submit(trans);

         if (headset.countRows() == 1)
         {
            okFindITEM1();
            okFindITEM2();
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         }
      }
   }

   public void okFindITEM1()
   {
      ASPManager mgr = getASPManager();

      if (headset.countRows()>0)
      {
         headrowno = headset.getCurrentRowNo();
         trans.clear();
         q = trans.addEmptyQuery(itemblk1);
         q.addWhereCondition("WO_NO = ?");
         q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
         q.includeMeta("ALL");

         mgr.submit(trans);
         trans.clear();
         fillRows();
         headset.goTo(headrowno);
      }
   }

   public void fillRows()
   {
      ASPManager mgr = getASPManager();

      if (headset.countRows() > 0)
      {
         trans.clear();
         if (itemset1.countRows()>0)
         {
            n = itemset1.countRows();
            itemset1.first();
            for (i=0; i<=n; ++i)
            {

               cmd = trans.addCustomFunction( "ACTCOST"+i, "Work_Order_Budget_API.Get_Actual_Cost","NACTUALCOST");
               cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
               cmd.addParameter("WORK_ORDER_COST_TYPE",itemset1.getRow().getValue("WORK_ORDER_COST_TYPE"));

               cmd = trans.addCustomCommand( "ACTREV"+i, "Work_Order_Budget_API.Actual_Revenue");
               cmd.addParameter("NACTUALREVENUE",itemset1.getRow().getValue("NACTUALREVENUE"));
               cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
               cmd.addParameter("WORK_ORDER_COST_TYPE",itemset1.getRow().getValue("WORK_ORDER_COST_TYPE"));

               itemset1.next();
            }
            trans = mgr.perform(trans);
            itemset1.first();

            for (i=0; i<=n; ++i)
            {
               budgRev = itemset1.getRow().getNumberValue("BUDGET_REVENUE");
               budgCost = itemset1.getRow().getNumberValue("BUDGET_COST");
               planRev = itemset1.getRow().getNumberValue("NPLANNEDREVENUE");
               planCost = itemset1.getRow().getNumberValue("NPLANNEDCOST");

               actCost = trans.getNumberValue("ACTCOST"+i+"/DATA/NACTUALCOST");
               actRev = trans.getNumberValue("ACTREV"+i+"/DATA/NACTUALREVENUE");

               actMargin =  toDouble(actRev) - toDouble(actCost);
               if (isNaN(budgCost))
                  budgMargin = toDouble(budgRev) - 0;
               else if (isNaN(budgRev))
                  budgMargin = 0 - toDouble(budgCost);
               else
                  budgMargin = toDouble(budgRev) - toDouble(budgCost);

               planMargin = toDouble(planRev) - toDouble(planCost);

               buf = itemset1.getRow();

               buf.setNumberValue("NACTUALCOST",actCost);
               buf.setNumberValue("NACTUALREVENUE",actRev);
               buf.setNumberValue("NACTUALMARGIN",actMargin);
               buf.setNumberValue("NBUDGETMARGIN",budgMargin);
               buf.setNumberValue("NPLANNEDMARGIN",planMargin);

               itemset1.setRow(buf);

               itemset1.next();
            }  
            itemset1.first();
         }
      }
   }

   public void deleteRowITEM2()
   {
      ASPManager mgr = getASPManager();

      int nJobExist = 0;
      int nRoleExist = 0;
      int nMatExist = 0;
      int nToolExist = 0;
      int nPlanningExist = 0;
      int nDocExist = 0;
      String sStdJobId = "";
      String sStdJobContract = "";
      String sStdJobRevision = "";
      String sStdJobExist = "";
      ctx.removeGlobal("HEADROWGLOBAL");
      ctx.removeGlobal("CURRROWGLOBAL");

      int currHead = headset.getCurrentRowNo();   
      ctx.setGlobal("HEADROWGLOBAL", String.valueOf(currHead));

      if (itemlay2.isMultirowLayout())
         itemset2.goTo(itemset2.getRowSelected());
      itemset2.selectRow();

      int currrow = itemset2.getCurrentRowNo();
      ctx.setGlobal("CURRROWGLOBAL", String.valueOf(currrow));

      trans.clear();

      cmd = trans.addCustomFunction("GETJOBEXIST", "Work_Order_Job_API.Check_Exist", "N_JOB_EXIST");
      cmd.addParameter("ITEM2_WO_NO",itemset2.getRow().getValue("WO_NO"));
      cmd.addParameter("JOB_ID",itemset2.getRow().getValue("JOB_ID"));

      cmd = trans.addCustomCommand("GETDEFVALS", "Work_Order_Job_API.Check_Job_Connection");
      cmd.addParameter("N_ROLE_EXIST","0");
      cmd.addParameter("N_MAT_EXIST","0");
      cmd.addParameter("N_TOOL_EXIST","0");
      cmd.addParameter("N_PLANNING_EXIST","0");
      cmd.addParameter("N_DOC_EXIST","0");
      cmd.addParameter("S_STD_JOB_ID");
      cmd.addParameter("S_STD_JOB_CONTRACT");
      cmd.addParameter("S_STD_JOB_REVISION");
      cmd.addParameter("ITEM2_WO_NO",itemset2.getRow().getValue("WO_NO"));
      cmd.addParameter("JOB_ID",itemset2.getRow().getValue("JOB_ID"));

      trans = mgr.perform(trans);

      nJobExist = new Double(trans.getNumberValue("GETJOBEXIST/DATA/N_JOB_EXIST")).intValue();
      nRoleExist = new Double(trans.getNumberValue("GETDEFVALS/DATA/N_ROLE_EXIST")).intValue();
      nMatExist = new Double(trans.getNumberValue("GETDEFVALS/DATA/N_MAT_EXIST")).intValue();
      nToolExist = new Double(trans.getNumberValue("GETDEFVALS/DATA/N_TOOL_EXIST")).intValue();
      nPlanningExist = new Double(trans.getNumberValue("GETDEFVALS/DATA/N_PLANNING_EXIST")).intValue();
      nDocExist = new Double(trans.getNumberValue("GETDEFVALS/DATA/N_DOC_EXIST")).intValue();
      sStdJobId = trans.getValue("GETDEFVALS/DATA/S_STD_JOB_ID");
      sStdJobContract = trans.getValue("GETDEFVALS/DATA/S_STD_JOB_CONTRACT");
      sStdJobRevision = trans.getValue("GETDEFVALS/DATA/S_STD_JOB_REVISION");

      hasPlanning = "FALSE";

      if ((nJobExist == 1) && (!mgr.isEmpty(sStdJobId)))
      {
         if (nRoleExist == 1 || nMatExist == 1 || nToolExist == 1 || nPlanningExist == 1 || nDocExist == 1)
         {
            hasPlanning = "TRUE";
            ctx.setCookie( "PageID_Has_Planing", "TRUE" );
         }
      }
      if (!"TRUE".equals(hasPlanning))
         deleteRow2("NO");
   }

   public void deleteRow2(String removeConn)
   {
      ASPManager mgr = getASPManager();

      int currHead = Integer.parseInt(ctx.findGlobal("HEADROWGLOBAL",""));
      headset.goTo(currHead);   
      int curr_row = Integer.parseInt(ctx.findGlobal("CURRROWGLOBAL",""));
      itemset2.goTo(curr_row);   

      itemset2.selectRow();

      trans.clear();

      if ("YES".equals(removeConn))
      {
         //Disconnect std job
         cmd = trans.addCustomCommand("REMOVECONN","Work_Order_Job_API.Disconnect_Std_Job");
         cmd.addParameter("ITEM2_WO_NO",itemset2.getRow().getValue("WO_NO"));
         cmd.addParameter("JOB_ID",itemset2.getRow().getValue("JOB_ID"));
         cmd.addParameter("STD_JOB_ID",itemset2.getRow().getValue("STD_JOB_ID"));
         cmd.addParameter("STD_JOB_CONTRACT",itemset2.getRow().getValue("STD_JOB_CONTRACT"));
         cmd.addParameter("STD_JOB_REVISION",itemset2.getRow().getValue("STD_JOB_REVISION")); 
         cmd.addParameter("ITEM2_QTY",itemset2.getRow().getValue("QTY")); 
      }

      cmd = trans.addCustomCommand("REMOVEJOBLINE","Work_Order_Job_API.Remove_Job_Line");
      cmd.addParameter("ITEM2_WO_NO",itemset2.getRow().getValue("WO_NO"));
      cmd.addParameter("JOB_ID",itemset2.getRow().getValue("JOB_ID")); 

      trans = mgr.perform(trans);

      trans.clear();
      clearItem1();
      okFindITEM2();
      okFindITEM1();
   }

   public void sumColumns()
   {
      ASPManager mgr = getASPManager();

      n = itemset1.countRows();
      itemset1.first();
      totBudgCost = 0;
      totBudgRev = 0;
      totBudgMargin = 0;
      totplannCost = 0;
      totPlannRev = 0;
      totplannMargin = 0;
      totActCost = 0;
      totActRev = 0;
      totActMargin = 0;

      for (i=1; i<=n; i++)
      {
         budCost = itemset1.getRow().getNumberValue("BUDGET_COST");
         budRev = itemset1.getRow().getNumberValue("BUDGET_REVENUE");
         budMargine = itemset1.getRow().getNumberValue("NBUDGETMARGIN");
         plannCost = itemset1.getRow().getNumberValue("NPLANNEDCOST");
         plannRev =  itemset1.getRow().getNumberValue("NPLANNEDREVENUE");
         planMargin = itemset1.getRow().getNumberValue("NPLANNEDMARGIN");
         actCost = itemset1.getRow().getNumberValue("NACTUALCOST");
         actRev = itemset1.getRow().getNumberValue("NACTUALREVENUE");
         actMargin = itemset1.getRow().getNumberValue("NACTUALMARGIN");

         if (isNaN(budCost))
            totBudgCost = toDouble(totBudgCost) +0;
         else
            totBudgCost = toDouble(totBudgCost) + toDouble(budCost);
         if (isNaN(budRev))
            totBudgRev =  toDouble(totBudgRev) + 0;
         else
            totBudgRev =  toDouble(totBudgRev) + toDouble(budRev);
         if (isNaN(budMargine))
            totBudgMargin = toDouble(totBudgMargin) + 0;
         else
            totBudgMargin = toDouble(totBudgMargin) + toDouble(budMargine);
         if (isNaN(plannCost))
            totplannCost =  toDouble(totplannCost) + 0;
         else
            totplannCost =  toDouble(totplannCost) + toDouble(plannCost);
         if (isNaN(plannRev))
            totPlannRev = toDouble(totPlannRev) + 0;
         else
            totPlannRev = toDouble(totPlannRev) + toDouble(plannRev);
         if (isNaN(planMargin))
            totplannMargin = toDouble(totplannMargin) + 0;
         else
            totplannMargin  = toDouble(totplannMargin) + toDouble(planMargin);
         if (isNaN(actCost))
            totActCost =  toDouble(totActCost) + 0;
         else
            totActCost =  toDouble(totActCost) + toDouble(actCost);
         if (isNaN(actRev))
            totActRev =  toDouble(totActRev) + 0;
         else
            totActRev =  toDouble(totActRev) + toDouble(actRev);
         if (isNaN(actMargin))
            totActMargin =  toDouble(totActMargin) + 0;
         else
            totActMargin =  toDouble(totActMargin) + toDouble(actMargin);

         itemset1.next();
      }

      data = trans.getBuffer("ITEM1/DATA");
      itemset1.addRow(data);
      buf = itemset1.getRow();
      buf.setValue("WORK_ORDER_COST_TYPE",mgr.translate("PCMWACTIVESEPARATE3SUMMARY: Summary"));  
      buf.setNumberValue("BUDGET_COST",totBudgCost);
      buf.setNumberValue("BUDGET_REVENUE",totBudgRev);
      buf.setNumberValue("NBUDGETMARGIN",totBudgMargin);
      buf.setNumberValue("NPLANNEDCOST",totplannCost);
      buf.setNumberValue("NPLANNEDREVENUE",totPlannRev);
      buf.setNumberValue("NPLANNEDMARGIN",totplannMargin);
      buf.setNumberValue("NACTUALCOST",totActCost);
      buf.setNumberValue("NACTUALREVENUE",totActRev);
      buf.setNumberValue("NACTUALMARGIN",totActMargin);

      itemset1.setRow(buf);
      itemset1.first();
   }

   public void viewDetailsITEM1()
   {
      curentrow = itemset1.getRowSelected();
      item1CurrRow = curentrow;

      sumColumns();
      itemset1.storeSelections();
      itemset1.goTo(curentrow);
      itemlay1.setLayoutMode(itemlay1.SINGLE_LAYOUT);
   }

   public void saveReturnITEM1()
   {
      ASPManager mgr = getASPManager();

      headCurrrow = headset.getCurrentRowNo();
      trans.clear();

      itemset1.goTo(curentrow);

      woNo = mgr.readValue("ITEM1_WO_NO");
      workOrderCostType = mgr.readValue("WORK_ORDER_COST_TYPE");
      budgCost = mgr.readNumberValue("BUDGET_COST");
      budgRev = mgr.readNumberValue("BUDGET_REVENUE");
      budgMargin = mgr.readNumberValue("NBUDGETMARGIN");
      planCost = mgr.readNumberValue("NPLANNEDCOST");
      planRev = mgr.readNumberValue("NPLANNEDREVENUE");
      planMargin = mgr.readNumberValue("NPLANNEDMARGIN");
      actCost = mgr.readNumberValue("NACTUALCOST");
      actRev = mgr.readNumberValue("NACTUALREVENUE");
      actMargin = mgr.readNumberValue("NACTUALMARGIN");

      buff = itemset1.getRow();

      //buff.setValue("WO_NO",woNo);
      buff.setValue("WORK_ORDER_COST_TYPE",workOrderCostType);
      buff.setNumberValue("BUDGET_COST",budgCost);
      buff.setNumberValue("BUDGET_REVENUE",budgRev);
      buff.setNumberValue("NBUDGETMARGIN",budgMargin);
      buff.setNumberValue("NPLANNEDCOST",planCost);
      buff.setNumberValue("NPLANNEDREVENUE",planRev);
      buff.setNumberValue("NPLANNEDMARGIN",planMargin);
      buff.setNumberValue("NACTUALCOST",actCost);
      buff.setNumberValue("NACTUALREVENUE",actRev);
      buff.setNumberValue("NACTUALMARGIN",actMargin);

      itemset1.setRow(buff);
      mgr.submit(trans);
      headset.goTo(headCurrrow);
      okFindITEM1();
      sumColumns();
      itemlay1.setLayoutMode(itemlay1.getHistoryMode());
      headset.goTo(headCurrrow);

      if (itemlay1.isSingleLayout())
         itemset1.goTo(curentrow);
   }

   public void saveReturnItem2() 
   {
      ASPManager mgr = getASPManager(); 

      trans.clear();

      itemset2.changeRow();
      int currrowHead = headset.getCurrentRowNo();
      int currrowItem2 = itemset2.getCurrentRowNo();

      mgr.submit(trans);

      itemset2.goTo(currrowItem2);
      headset.goTo(currrowHead);        

      okFindITEM1();        
   }

   public void editItem1()
   {
      ASPManager mgr = getASPManager();

      if (itemlay1.isMultirowLayout())
         curentrow = itemset1.getRowSelected();
      else
         curentrow = item1CurrRow;


      if (curentrow==5)
         mgr.showAlert("PCMWACTIVESEPARATE3CANNOT: Can not perform on selected line.");
      else
      {
         item1CurrRow = curentrow;

         if (itemset1.countRows() == 5)
            sumColumns();

         itemset1.goTo(curentrow);
         itemlay1.setLayoutMode(itemlay1.EDIT_LAYOUT);
      }
   }

   public void clearItem1()
   {

      if (itemset1.countRows() > 0)
      {
         itemset1.last();
         itemset1.clearRow();
         itemset1.first();
      }
   }

   public void forwardITEM1()
   {

      if (item1CurrRow<5)
         item1CurrRow++;
   }

   public void backwardITEM1()
   {

      if (item1CurrRow>0)
         item1CurrRow--;
   }

   public void okFindITEM2()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      int headrowno = headset.getCurrentRowNo();

      q = trans.addQuery(itemblk2);
      q.addWhereCondition("WO_NO = ? AND STD_JOB_CONTRACT = ?");
      q.addParameter("WO_NO",headset.getValue("WO_NO"));
      q.addParameter("CONTRACT",headset.getValue("CONTRACT"));

      q.includeMeta("ALL");
      mgr.querySubmit(trans, itemblk2);

      headset.goTo(headrowno);

   }

   public void countFindITEM2()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      int headrowno = headset.getCurrentRowNo();

      q = trans.addQuery(itemblk2);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("WO_NO = ? AND STD_JOB_CONTRACT = ?");
      q.addParameter("WO_NO",headset.getValue("WO_NO"));
      q.addParameter("CONTRACT",headset.getValue("CONTRACT"));
      mgr.submit(trans);

      headset.goTo(headrowno);

      itemlay2.setCountValue(toInt(itemset2.getRow().getValue("N")));
      itemset2.clear();
   }

   public void newRowITEM2()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      cmd = trans.addEmptyCommand("ITEM2", "WORK_ORDER_JOB_API.New__", itemblk2);
      cmd.setParameter("ITEM2_WO_NO", headset.getValue("WO_NO"));

      cmd.setOption("ACTION", "PREPARE");

      cmd = trans.addCustomFunction("GETSDATE", "Active_Separate_API.Get_Plan_S_Date", "ITEM2_DATE_FROM");
      cmd.addParameter("ITEM2_WO_NO", headset.getValue("WO_NO"));

      cmd = trans.addCustomFunction("GETFDATE", "Active_Separate_API.Get_Plan_F_Date", "ITEM2_DATE_TO");
      cmd.addParameter("ITEM2_WO_NO", headset.getValue("WO_NO"));

      trans = mgr.perform(trans);

      data = trans.getBuffer("ITEM2/DATA");


      Date dPlanSDate = trans.getBuffer("GETSDATE/DATA").getFieldDateValue("ITEM2_DATE_FROM");
      Date dPlanFDate = trans.getBuffer("GETFDATE/DATA").getFieldDateValue("ITEM2_DATE_TO");

      data.setFieldDateItem("ITEM2_DATE_FROM", dPlanSDate);
      data.setFieldDateItem("ITEM2_DATE_TO", dPlanFDate);
      itemset2.addRow(data);
   }



   public void countFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
      q.addWhereCondition("OBJSTATE = 'FAULTREPORT' OR OBJSTATE = 'WORKREQUEST' OR OBJSTATE = 'OBSERVED' OR OBJSTATE = 'UNDERPREPARATION' OR OBJSTATE = 'PREPARED'");
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }

   public void newRow()
   {
      ASPManager mgr = getASPManager();

      String call_url = ctx.getGlobal("CALL_URL");
      String sDelDate;

      cmd = trans.addEmptyCommand("HEAD","ACTIVE_SEPARATE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");

      cmd = trans.addCustomFunction("FNDUSER","Fnd_Session_API.Get_Fnd_User","FND_USER");

      cmd = trans.addCustomFunction("REPBY","Person_Info_API.Get_Id_For_User","REPORTED_BY");
      cmd.addReference("FND_USER","FNDUSER/DATA");

      cmd = trans.addCustomFunction("REPBYID","Company_Emp_API.Get_Max_Employee_Id","REPORTED_BY_ID");
      cmd.addReference("COMPANY","HEAD/DATA");
      cmd.addReference("REPORTED_BY","REPBY/DATA");     

      cmd = trans.addCustomFunction("CONNTYPEDB","MAINT_CONNECTION_TYPE_API.Encode","CONNECTION_TYPE_DB");
      cmd.addReference("CONNECTION_TYPE","HEAD/DATA");

      if ("CUSTORDER".equals(call_url))
      {
         cmd = trans.addCustomFunction("AUTHCODE","Customer_Order_API.Get_Authorize_code","AUTHORIZE_CODE");
         cmd.addParameter("CUST_ORDER_NO",ret_or_no);

         cmd = trans.addCustomFunction("DELIVERYDATE","Customer_Order_API.Get_Wanted_Delivery_Date","DELIVERYDATE");
         cmd.addParameter("CUST_ORDER_NO",ret_or_no);
      }

      trans = mgr.perform(trans);

      signId = trans.getValue("REPBY/DATA/REPORTED_BY");
      repid = trans.getValue("REPBYID/DATA/REPORTED_BY_ID");
      String conTypeDB = trans.getValue("CONNTYPEDB/DATA/CONNECTION_TYPE_DB"); 

      data = trans.getBuffer("HEAD/DATA");

      data.setFieldItem("REPORTED_BY",signId);
      data.setFieldItem("REPORTED_BY_ID",repid);
      data.setFieldItem("CONNECTION_TYPE_DB",conTypeDB);
      data.setNumberValue("FAULT_REP_FLAG",1);

      if ("CUSTORDER".equals(call_url))
      {
         ret_au_code = trans.getValue("AUTHCODE/DATA/AUTHORIZE_CODE");
         sDelDate = trans.getBuffer("DELIVERYDATE/DATA").getFieldValue("DELIVERYDATE");
         data.setFieldItem("CUST_ORDER_NO",ret_or_no);
         data.setFieldItem("CUSTOMER_NO",ret_cu_no);
         data.setFieldItem("CUST_ORDER_TYPE",ret_cu_type);
         data.setFieldItem("CURRENCY_CODE",ret_curr_code);
         data.setFieldItem("AUTHORIZE_CODE",ret_au_code);
         data.setFieldItem("CONNECTION_TYPE_DB","EQUIPMENT");
         data.setFieldItem("DELIVERYDATE",sDelDate);
      }
      headset.addRow(data);

      if ("TRUE".equals(trans_))
      {
         headlay.setLayoutMode(headlay.NEW_LAYOUT);
         trans_ ="";
      }

      newman = "TRUE";
   }

   public void newRowForCC()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("HEAD","ACTIVE_SEPARATE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");

      cmd = trans.addCustomFunction("GETCUSTNAME","CUSTOMER_INFO_API.Get_Name","CUSTOMERDESCRIPTION");
      cmd.addParameter("CUSTOMER_NO",caseCustId);

      cmd = trans.addCustomFunction("FNDUSER","Fnd_Session_API.Get_Fnd_User","FND_USER");

      cmd = trans.addCustomFunction("REPBY","Person_Info_API.Get_Id_For_User","REPORTED_BY");
      cmd.addReference("FND_USER","FNDUSER/DATA");

      cmd = trans.addCustomFunction("REPBYID","Company_Emp_API.Get_Max_Employee_Id","REPORTED_BY_ID");
      cmd.addReference("COMPANY","HEAD/DATA");
      cmd.addReference("REPORTED_BY","REPBY/DATA");

      cmd = trans.addCustomFunction("CONNTYPEDB","MAINT_CONNECTION_TYPE_API.Encode","CONNECTION_TYPE_DB");
      cmd.addReference("CONNECTION_TYPE","HEAD/DATA");

      trans = mgr.perform(trans);

      custName = trans.getValue("GETCUSTNAME/DATA/CUSTOMERDESCRIPTION");
      signId = trans.getValue("REPBY/DATA/REPORTED_BY");
      repid = trans.getValue("REPBYID/DATA/REPORTED_BY_ID");      
      String conTypeDB = trans.getValue("CONNTYPEDB/DATA/CONNECTION_TYPE_DB"); 

      data = trans.getBuffer("HEAD/DATA");
      data.setFieldItem("REFERENCE_NO",referenceNo);
      data.setFieldItem("CUSTOMER_NO",caseCustId);  
      data.setFieldItem("CONTACT",caseContact);  
      data.setFieldItem("PHONE_NO",casePhoneNo);        
      data.setFieldItem("CUSTOMERDESCRIPTION",custName);
      data.setFieldItem("REPORTED_BY",signId);
      data.setFieldItem("REPORTED_BY_ID",repid);
      data.setNumberValue("FAULT_REP_FLAG",1);
      data.setFieldItem("CONNECTION_TYPE_DB",conTypeDB);

      headset.addRow(data);

      headlay.setLayoutMode(headlay.NEW_LAYOUT);
      newman = "TRUE";
   }
   public void transferWrite( String sContract,String sMchCode,String sMchNameDes,String sMchCodeCont,String sTestPointId,String sPmNo,String pmDescription,String insNote) 
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("HEAD","ACTIVE_SEPARATE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");

      trans.clear();
      cmd = trans.addCustomFunction("FNDUSER","Fnd_Session_API.Get_Fnd_User","FND_USER");

      cmd = trans.addCustomFunction("REPBY","Person_Info_API.Get_Id_For_User","REPORTED_BY");
      cmd.addReference("FND_USER","FNDUSER/DATA");

      cmd = trans.addCustomFunction("REPBYID","Company_Emp_API.Get_Max_Employee_Id","REPORTED_BY_ID");
      cmd.addReference("COMPANY","HEAD/DATA");
      cmd.addReference("REPORTED_BY","REPBY/DATA");

      trans = mgr.perform(trans);

      signId = trans.getValue("REPBY/DATA/REPORTED_BY");
      repid = trans.getValue("REPBYID/DATA/REPORTED_BY_ID");

      data.setValue("REPORTED_BY",signId);
      data.setValue("REPORTED_BY_ID",repid);
      data.setNumberValue("FAULT_REP_FLAG",1);   
      data.setValue("CONTRACT",sContract);
      data.setValue("MCH_CODE",sMchCode);
      data.setValue("MCH_CODE_CONTRACT",sMchCodeCont);
      data.setValue("TEST_POINT_ID",sTestPointId);
      data.setValue("PM_NO",sPmNo);
      data.setValue("PM_DESCR",pmDescription);
      data.setValue("NOTE",insNote);
      data.setValue("CONNECTION_TYPE_DB","EQUIPMENT");
      headset.addRow(data);
   }


   public void saveReturn()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();
      String sPpExist = "";

      headset.changeRow();
      hCustOrdertype = headset.getRow().getValue("CUST_ORDER_TYPE");

      //Bug 93061,Start added mch_code_contract as a param
      trans1.clear();
      cmd = trans1.addCustomFunction("PPEXISTS","ACTIVE_SEPARATE_API.Exist_Pre_Postings","PP_EXISTS");
      cmd.addParameter("WO_NO", headset.getValue("WO_NO"));
      cmd.addParameter("MCH_CODE_CONTRACT", headset.getValue("MCH_CODE_CONTRACT"));
      cmd.addParameter("MCH_CODE", headset.getValue("MCH_CODE"));
      trans1 = mgr.perform(trans1);
      sPpExist = trans1.getValue("PPEXISTS/DATA/PP_EXISTS");
      //Bug 93061,End

      if (newWarr)
      {
         temp = headset.getRow();
         temp.setValue("OBJ_CUST_WARRANTY", rRowN);
         temp.setValue("CUST_WARRANTY", rRawId);
         temp.setValue("CUST_WARR_TYPE", rWarrType);

         headset.setRow(temp);

         newWarr = false;   
      }

      if (mgr.isEmpty(headset.getValue("WO_NO")))
      {
         r = headset.getRow();
         r.setValue("STATE","");
         r.setValue("QUOTATION_ID","");
         headset.setRow(r);
      }
      currrow = headset.getCurrentRowNo();

//		  Bug 68362, Start        
      if (edited)
         headset.setEdited("CUSTOMER_NO,WORK_TYPE_ID,AGREEMENT_ID,PLAN_HRS,AUTHORIZE_CODE,ORG_CODE,ERR_SYMPTOM,WARRANTY_ROW_NO,MCH_CODE,MCH_CODE_DESCRIPTION,MCH_CODE_CONTRACT,ADDRESS_ID,ADDRESS1,ADDRESS2,ADDRESS3,ADDRESS4,ADDRESS5,ADDRESS6,PLAN_S_DATE,PLAN_F_DATE,REQUIRED_START_DATE,CUST_ADD_ID,CONTRACT_ID,LINE_NO,CONNECTION_TYPE_DB,CONNECTION_TYPE");

      if (editedCust)
//         Bug 68363, Start
         headset.setEdited("CUSTOMER_NO,WORK_TYPE_ID,AGREEMENT_ID,PLAN_HRS,AUTHORIZE_CODE,ORG_CODE,ERR_SYMPTOM,WARRANTY_ROW_NO,PLAN_S_DATE,PLAN_F_DATE,REQUIRED_START_DATE,CONTRACT_ID,LINE_NO,REQUIRED_END_DATE");
//      	  Bug 68363, End
//		  Bug 68362, End   


      mgr.submit(trans);

      headset.goTo(currrow);
      headset.refreshRow();

      if ("TRUE".equals(caseFlag))
      {
         trans.clear();

         cmd = trans.addCustomCommand("CCWOLIST","CC_WORK_ORDER_API.Create_New_Work_Order");
         cmd.addParameter("OBJID");
         cmd.addParameter("OBJVERSION");
         cmd.addParameter("WORK_ORDER_OID");
         cmd.addParameter("CASE_ID", caseId);
         cmd.addParameter("WO_NO", headset.getValue("WO_NO"));

         trans = mgr.perform(trans);

         okFindCase();
      }

      r = headset.getRow();
      r.setValue("DUP_OBJEVENTS",r.getValue("OBJEVENTS"));
      headset.setRow(r);

      headlay.setLayoutMode(headlay.getHistoryMode());    

      okFindITEM1();
      okFindITEM2();

      if (mgr.isEmpty(qrystr))
         qrystr = mgr.getURL()+"?SEARCH=Y&WO_NO="+headset.getValue("WO_NO");
      else
         qrystr = qrystr+mgr.URLEncode(";"+headset.getValue("WO_NO"));

      if ("REMOVED".equals(sPpExist))
         mgr.showAlert("PCMWACTIVESEPARATE3PPREMOVED: Existing pre-posting values are not updated");
      else if ("CHANGED".equals(sPpExist))
         bPpChanged = true;
   }

   public void cancelEdit()
   {
      headlay.setLayoutMode(headlay.getHistoryMode());    
      headset.refreshRow();
      headtbl.clearQueryRow();
   }

   public void saveNew()
   {
      ASPManager mgr = getASPManager();

      headset.changeRow();

      r = headset.getRow();
      r.setValue("STATE","");
      r.setValue("QUOTATION_ID","");
      headset.setRow(r);

      currrow = headset.getCurrentRowNo();
      mgr.submit(trans);
      headset.goTo(currrow);
      headset.refreshRow();
      trans.clear();

      r = headset.getRow();
      r.setValue("DUP_OBJEVENTS",r.getValue("OBJEVENTS"));
      headset.setRow(r);

      newRow();
   }


   public void updatePostings()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomCommand("UPDATEPP","ACTIVE_SEPARATE_API.Update_Mch_Pre_Postings");
      cmd.addParameter("WO_NO", headset.getValue("WO_NO"));
      trans = mgr.perform(trans);
   }

   //Bug 68947, Start   
   public void updateContract()
   {
      ASPManager mgr = getASPManager();
      if (!headlay.isEditLayout())
         headlay.setLayoutMode(headlay.EDIT_LAYOUT);
      r = headset.getRow();
      r.setValue("CONTRACT_ID",mgr.readValue("TEMPCONTRACTID"));
      r.setValue("LINE_NO",mgr.readValue("TEMPLINENO"));
      headset.setRow(r);
      bFECust = true;
   }
   //Bug 68947, End   

   public void searchEvent()
   {
      ASPManager mgr = getASPManager();

      trans1.clear();
      q = trans1.addQuery(eventblk);
      q.addWhereCondition("OBJID = ?");
      q.addParameter("OBJID",headset.getValue("OBJID"));
      q.includeMeta("ALL");

      currrow = headset.getCurrentRowNo();
      mgr.submit(trans1);
      headset.goTo(currrow);
      trans1.clear();   
   }

   public void setNewObjEvent( int currrow) 
   {

      headset.goTo(currrow);
      searchEvent();
      newObjEvents = eventset.getValue("OBJEVENTS");

      r = headset.getRow();
      r.setValue("DUP_OBJEVENTS",newObjEvents);
      headset.setRow(r);
   }

//-----------------------------------------------------------------------------
//-----------------------------  PERFORM FUNCTION  ----------------------------
//-----------------------------------------------------------------------------
   public void perform(String command) 
   {
      ASPManager mgr = getASPManager();

      //Web Alignment - Enable multirow action
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
         setNewObjEvent(currrow);
      }   
   }

   public void reinit()
   {
      ASPManager mgr = getASPManager();
      perform("RE_INIT__");
   }

   public void observed()
   {
      ASPManager mgr = getASPManager();
      //Web Alignment - Enable multirow action
      perform("CONFIRM__");
   }


   public void underPrep()
   {
      ASPManager mgr = getASPManager();
      //Web Alignment - Enable multirow action
      perform("TO_PREPARE__");
   }


   public void prepared()
   {
      ASPManager mgr = getASPManager();
      //Web Alignment - Enable multirow action
      perform("PREPARE__");
   }


   public void released()
   {
      ASPManager mgr = getASPManager();
      //Web Alignment - Enable multirow action
      int count = 0;
      String eventVal = "";

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

      trans.clear();

      for (int i = 0; i < count; i ++)
      {
         eventVal = headset.getRow().getValue("DUP_OBJEVENTS");

         if (eventVal.indexOf("Release") != -1)
            headset.markRow("RELEASE__");
         else if (eventVal.indexOf("Replan") != -1)
            headset.markRow("REPLAN__");

         if (headlay.isMultirowLayout())
            headset.next();
      }

      mgr.submit(trans);

      if (headlay.isMultirowLayout()){
          headset.refreshAllRows();
          headset.setFilterOff();
      }
      else
      {
        headset.goTo(currrow);
        headset.refreshRow();
      }
   }


   public void started()
   {
      ASPManager mgr = getASPManager();
      //Web Alignment - Enable multirow action

      int count = 0;
      String eventVal = "";

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

      trans.clear();

      for (int i = 0; i < count; i ++)
      {
         eventVal = headset.getRow().getValue("DUP_OBJEVENTS");

         if (eventVal.indexOf("Restart") != -1)
            headset.markRow("RESTART__");
         else if (eventVal.indexOf("StartOrder") != -1)
            headset.markRow("START_ORDER__");

         if (headlay.isMultirowLayout())
            headset.next();
      }

      mgr.submit(trans);

      if (headlay.isMultirowLayout()){
          headset.refreshAllRows();
          headset.setFilterOff();
      }
      else
      {
        headset.goTo(currrow);
        headset.refreshRow();
      }
   }


   public void workDone()
   {
      ASPManager mgr = getASPManager();
      //Web Alignment - Enable multirow action
      perform("WORK__");
   }


   public void reported()
   {
      ASPManager mgr = getASPManager();
      //Web Alignment - Enable multirow action
      perform("REPORT__");
   }


   public void finished()
   {
      ASPManager mgr = getASPManager();
      //Web Alignment - Enable multirow action
      perform("FINISH__");
   }


   public void cancelled()
   {
      ASPManager mgr = getASPManager();
      //Web Alignment - Enable multirow action
      perform("CANCEL__");
   }

//-----------------------------------------------------------------------------
//-------------------------  CUSTOM FUNCTIONS  --------------------------------
//-----------------------------------------------------------------------------

   public void setCheckBoxValue()
   {
      ASPManager mgr = getASPManager();
      String cbmobile_var = null;

      double warranty_row_no = headset.getRow().getNumberValue("OBJ_CUST_WARRANTY");  
      if (isNaN(warranty_row_no))
         warranty_row_no = 0;

      if (warranty_row_no == 0)
         cbWarrantyVar = "FALSE";
      else
         cbWarrantyVar = "TRUE";

      //ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer();
      // 107820
      //secBuff.addSecurityQuery("Mobmgr_Work_Order_API","Check_Exist");

      //secBuff = mgr.perform(secBuff);

      trans.clear();

      cmd = trans.addCustomFunction("GETSUPWAR","Object_Supplier_Warranty_API.Has_Warranty","CBWARRANTYONOBJECT");
      cmd.addParameter("MCH_CODE_CONTRACT");
      cmd.addParameter("MCH_CODE");
      cmd.addParameter("REG_DATE");

      cmd = trans.addCustomFunction("GETCUSTWAR","Object_Cust_Warranty_API.Has_Warranty","CBHASCUSTWARRANTYONOBJ");
      cmd.addParameter("MCH_CODE_CONTRACT",headset.getRow().getValue("MCH_CODE_CONTRACT"));
      cmd.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));
      cmd.addParameter("REG_DATE");
      //cmd.addParameter("MCH_CODE_CONTRACT");
      //cmd.addParameter("MCH_CODE");
      //cmd.addParameter("REG_DATE");

      trans = mgr.perform(trans);
      String cbSupWar = trans.getValue("GETSUPWAR/DATA/CBWARRANTYONOBJECT");
      String cbCustWar = trans.getValue("GETCUSTWAR/DATA/CBHASCUSTWARRANTYONOBJ");

      row = headset.getRow();
      row.setValue("CBWARRWO",cbWarrantyVar);

      if ("TRUE".equals(cbCustWar))
         row.setValue("CBHASCUSTWARRANTYONOBJ","TRUE");
      else
         row.setValue("CBHASCUSTWARRANTYONOBJ","FALSE");

      if ("TRUE".equals(cbSupWar))
         row.setValue("CBWARRANTYONOBJECT","TRUE");
      else
         row.setValue("CBWARRANTYONOBJECT","FALSE");

      // 107820
      //row.setValue("CBTRANSFEREDTOMOB",cbmobile_var);

      headset.setRow(row);
   }

   public void objectForCustomer()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();      

      if (headset.countRows()>0)
      {
         //Web Alignment - simplify code for RMBs
         bOpenNewWindow = true;
         urlString = "../equipw/WorkOrderCustomer.page?REG_DAT_PASS="+mgr.URLEncode(headset.getRow().getFieldValue("REG_DATE")) + 
                     "&WO_NO1="+mgr.URLEncode(headset.getRow().getValue("WO_NO")) + "&OBJECT_ID1="+mgr.URLEncode(headset.getRow().getValue("MCH_CODE")) + 
                     "&CONTRACT1="+mgr.URLEncode(headset.getRow().getValue("MCH_CODE_CONTRACT"))+"&WORK_TYPE_ID1=" + mgr.URLEncode(headset.getRow().getValue("WORK_TYPE_ID")) + 
                     "&CUSTOMER_NO1="+mgr.URLEncode(headset.getRow().getValue("CUSTOMER_NO"))+"&ADDRESS_ID1="+mgr.URLEncode(headset.getRow().getValue("ADDRESS_ID")) + 
                     "&AGREEMENT_ID1="+mgr.URLEncode(headset.getRow().getValue("AGREEMENT_ID"))+"&SETTING_TIME1="+nSettingTime + 
                     "&ORG_CODE1="+mgr.URLEncode(headset.getRow().getValue("ORG_CODE"))+"&ERR_SYMPT1="+mgr.URLEncode(headset.getRow().getValue("ERR_SYMPTOM")) + 
                     "&WARRANTY_NO1="+(int)headset.getRow().getNumberValue("WARRANTY_ROW_NO")+"&FRM_NAME=ServiceRequest" + 
                     "&WOCONTRACT=" + mgr.URLEncode(headset.getRow().getValue("CONTRACT")) ;
         newWinHandle = "ObjectForCustom";
         //
      }

      nSettingTime = 0;
      sStdJobID = "";
      sErrorSymptom = "";

      current_url = qrystr;
      ctx.setGlobal("CALLING_URL",current_url); 
      ctx.setGlobal("HEADCURRROW",String.valueOf(headset.getCurrentRowNo()));
   }

   public void customforobj()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();      

      if (headset.countRows()>0)
      {
         //Web Alignment - simplify code for RMBs
         bOpenNewWindow = true;
         urlString = "../equipw/MaintenanceObject4.page?REG_DAT_PASS="+mgr.URLEncode(headset.getRow().getFieldValue("REG_DATE"))+
                     "&WO_NO1="+mgr.URLEncode(headset.getRow().getValue("WO_NO"))+
                     "&OBJECT_ID1="+mgr.URLEncode(headset.getRow().getValue("MCH_CODE"))+
                     "&CONTRACT1="+mgr.URLEncode(headset.getRow().getValue("MCH_CODE_CONTRACT"))+
                     "&WORK_TYPE_ID1="+mgr.URLEncode(headset.getRow().getValue("WORK_TYPE_ID"))+
                     "&CUSTOMER_NO1="+mgr.URLEncode(headset.getRow().getValue("CUSTOMER_NO"))+
                     "&AGREEMENT_ID1="+mgr.URLEncode(headset.getRow().getValue("AGREEMENT_ID"))+
                     "&SETTING_TIME1="+nSettingTime+"&ORG_CODE1="+mgr.URLEncode(headset.getRow().getValue("ORG_CODE"))+
                     "&ERR_SYMPT1="+mgr.URLEncode(headset.getRow().getValue("ERR_SYMPTOM"))+
                     "&WARRANTY_NO1="+(int)headset.getRow().getNumberValue("WARRANTY_ROW_NO")+
                     "&WOCONTRACT=" + mgr.URLEncode(headset.getRow().getValue("CONTRACT")) + 
                     "&FRM_NAME=ServiceRequest";
         newWinHandle = "CustomerforObject";
         //
      }

      nSettingTime = 0;
      sStdJobID = "";
      sErrorSymptom = "";

      current_url = qrystr;
      ctx.setGlobal("CALLING_URL",current_url);
      ctx.setGlobal("HEADCURRROW",String.valueOf(headset.getCurrentRowNo()));
   }

   public void grapicalObject()
   {
      ASPManager mgr = getASPManager();
      String enabledGO = "FALSE";

      if (headlay.isMultirowLayout())
      {
         headset.goTo(headset.getRowSelected());           
         if ("EQUIPMENT".equals(headset.getRow().getValue("CONNECTION_TYPE")))
            enabledGO = "TRUE";
         else
            enabledGO = "FALSE";   
      }
      else
         enabledGO = "TRUE";

      if ("TRUE".equals(enabledGO))
      {
         //Web Alignment - simplify code for RMBs
         bOpenNewWindow = true;
         urlString = "../equipw/NavigatorFrameSet.page?MCH_CODE="+mgr.URLEncode(headset.getRow().getValue("MCH_CODE"))+"&CONTRACT="+mgr.URLEncode(headset.getRow().getValue("MCH_CODE_CONTRACT"))+"&MCH_NAME="+mgr.URLEncode(headset.getRow().getValue("MCH_CODE_DESCRIPTION"))+"&FRM_NAME=ServiceRequest";
         newWinHandle = "grapicalSt";
         //

         current_url = mgr.getURL();
         ctx.setGlobal("CALLING_URL",current_url);    
      }
      else
         mgr.showAlert("PCMWACTIVESEPARATE3CANNOTVIM: Can not perform for VIM connection types.");
   }

   public void custWarr()
   {
      ASPManager mgr = getASPManager();
      String enabledCW = "FALSE";

      if (headlay.isMultirowLayout())
      {
         headset.goTo(headset.getRowSelected());           
         if ("EQUIPMENT".equals(headset.getRow().getValue("CONNECTION_TYPE")))
            enabledCW = "TRUE";
         else
            enabledCW = "FALSE";   
      }
      else
         enabledCW = "TRUE";

      if ("TRUE".equals(enabledCW))
      {
         currrow = headset.getCurrentRowNo();
         headset.goTo(currrow);
         //Web Alignment - simplify code for RMBs
         bOpenNewWindow = true;
         urlString = "WarrantyTypeDlg.page?MCH_CODE="+mgr.URLEncode(headset.getRow().getValue("MCH_CODE"))+"&CONTRACT="+mgr.URLEncode(headset.getRow().getValue("MCH_CODE_CONTRACT"))+"&ROW_NO="+mgr.URLEncode(headset.getRow().getValue("OBJ_CUST_WARRANTY"))+"&WARRANTY_ID="+mgr.URLEncode(headset.getRow().getValue("CUST_WARRANTY"))+"&WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"))+"&FRMNAME=ServiceRequest"+"&QRYSTR="+mgr.URLEncode(qrystr);
         newWinHandle = "custwarr";
         //
      }
      else
         mgr.showAlert("PCMWACTIVESEPARATE3CANNOTVIM: Can not perform for VIM connection types.");
   }      

   public void preposting()
   {
      ASPManager mgr = getASPManager();

      calling_url=mgr.getURL();
      ctx.setGlobal("CALLING_URL",calling_url);

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();      

      enabl10 =0;
      objstate = headset.getRow().getValue("OBJSTATE");
      contract = headset.getRow().getValue("CONTRACT");
      wo_no = headset.getRow().getValue("WO_NO");
      lout = (headlay.isMultirowLayout()?"1":"0");

      GetEnabledFields();
      makePrePostBuffer();

   }

   public void copy()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();      

      calling_url=mgr.getURL();
      ctx.setGlobal("CALLING_URL",calling_url);

      //Web Alignment - simplify code for RMBs
      bOpenNewWindow = true;
      urlString = "CopyWorkOrderDlg.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) + "&FAULT_REP_FLAG=" + mgr.URLEncode(headset.getRow().getValue("FAULT_REP_FLAG"));
      newWinHandle = "copyWind";
      //
   }

   public void changeConditionCode()
   {
      ASPManager mgr = getASPManager();

      ASPCommand cmd;
      ASPBuffer buff,row;

      String sPartNo = "";
      String sSerialNo = "";
      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      trans.clear();
      //Bug 59613, start
      cmd = trans.addCustomFunction("GETPARTNO","EQUIPMENT_SERIAL_API.GET_PART_NO","PART_NO");
      cmd.addParameter("CONTRACT",headset.getValue("CONTRACT"));
      cmd.addParameter("MCH_CODE",headset.getValue("MCH_CODE"));

      cmd = trans.addCustomFunction("GETSERIALNO","EQUIPMENT_SERIAL_API.GET_SERIAL_NO","SERIAL_NO");
      cmd.addParameter("CONTRACT",headset.getValue("CONTRACT"));
      cmd.addParameter("MCH_CODE",headset.getValue("MCH_CODE"));
      trans = mgr.perform(trans);

      sPartNo = trans.getValue("GETPARTNO/DATA/PART_NO");
      sSerialNo = trans.getValue("GETSERIALNO/DATA/SERIAL_NO");

      trans.clear();
      if (mgr.isEmpty(sPartNo))
      {
         cmd = trans.addCustomCommand("GETPARTSER","Active_Separate_API.Get_Vim_Part_Serial");
         cmd.addParameter("PART_NO");
         cmd.addParameter("SERIAL_NO");
         cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
         trans = mgr.perform(trans);

         sPartNo = trans.getValue("GETPARTSER/DATA/PART_NO");
         sSerialNo = trans.getValue("GETPARTSER/DATA/SERIAL_NO");

      }

      trans.clear();

      cmd = trans.addCustomFunction("GETCONDCODEUSAGEDB","PART_CATALOG_API.Get_Condition_Code_Usage_Db","COND_CODE_USAGE");
      cmd.addParameter("PART_NO", sPartNo);

      trans = mgr.perform(trans);

      if ("ALLOW_COND_CODE".equals(trans.getValue("GETCONDCODEUSAGEDB/DATA/COND_CODE_USAGE")))
      {
         buff = mgr.newASPBuffer();
         row = buff.addBuffer("0");
         row.addItem("PART_NO",sPartNo);
         row.addItem("SERIAL_NO",sSerialNo);

         bOpenNewWindow = true;
         urlString = "../partcw/ChangeConditionCodeDlg.page?" + mgr.getTransferedQueryString(buff);
         newWinHandle = "chgCond"; 

      }
      else
      {
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE3CANNOTPERCHACON: Cannot change the condition code."));
      }
      //Bug 59613, end
   }

   public void mroObjReceiveO()
   {
      ASPManager mgr = getASPManager();        
      ASPCommand cmd;
      ASPBuffer buff,row;
      boolean enabled;

      if (headlay.isMultirowLayout())
      {
         headset.goTo(headset.getRowSelected());
         if (checkMROObjRO())
            enabled = true;
         else
            enabled  = false;
      }
      else
         enabled  = true;

      if (enabled)
      {
         trans.clear();

         cmd = trans.addCustomCommand("CREMRORO","ACTIVE_SEPARATE_API.Create_Mro_Ro");
         cmd.addParameter("RECEIVE_ORDER_NO");
         cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

         trans = mgr.perform(trans);

         String roNo = trans.getValue("CREMRORO/DATA/RECEIVE_ORDER_NO");

         if (mgr.isEmpty(roNo))
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE3RONOTCRE: Receive Order was not Created."));
         else
         {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE3ROCRE: Receive Order " +roNo+ "  has been created."));

            trans.clear();
            cmd = trans.addCustomFunction("GETREQSDATE","ACTIVE_SEPARATE_API.Get_Required_Start_Date","REQUIRED_START_DATE");
            cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

            trans = mgr.perform(trans);

            String reqSdate =  trans.getValue("GETREQSDATE/DATA/REQUIRED_START_DATE");
            if (mgr.isEmpty(reqSdate))
               mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE3INVREQSTNOTSP: Required Start is not specified for Work Order " +headset.getValue("WO_NO")+ ". Update corresponding Planned Receipt Date for Receive Order " +roNo+ "."));
         }
      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTPERMRO: Cannot perform Create MRO Object Receive Order on this record."));        
   }

   public void customerAddress()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());

      urlString = "CustomerAddresses.page?CUSTOMER_ID="+mgr.URLEncode(headset.getValue("CUSTOMER_ID"));;
      newWinHandle = "CustomerAddresses";
      bOpenNewWindow = true;
      headlay.setLayoutMode(headlay.EDIT_LAYOUT);
   }


//--------------------------------------------------
//----------------------------- get the enabled fields to be  passed to prepost.page-----------------------------------

   public void GetEnabledFields()
   {
      ASPManager mgr = getASPManager();
      String code_a = null;
      String code_b = null;
      String code_c = null;
      String code_d = null;
      String code_e = null;
      String code_f = null;
      String code_g = null;
      String code_h = null;
      String code_i = null;
      String code_j = null;

      cmd = trans.addCustomCommand( "POSTI", "Pre_Accounting_API.Execute_Accounting");  
      cmd.addParameter("CODE_A");
      cmd.addParameter("CODE_B");
      cmd.addParameter("CODE_C");
      cmd.addParameter("CODE_D");
      cmd.addParameter("CODE_E");
      cmd.addParameter("CODE_F");
      cmd.addParameter("CODE_G");
      cmd.addParameter("CODE_H");
      cmd.addParameter("CODE_I");
      cmd.addParameter("CODE_J");
      cmd.addParameter("STR_CODE","T50"); 
      cmd.addParameter("CONTROL_TYPE"); 
      cmd.addParameter("COMPANY", headset.getRow().getValue("COMPANY"));

      trans = mgr.perform(trans);

      code_a = trans.getValue("POSTI/DATA/CODE_A");
      code_b = trans.getValue("POSTI/DATA/CODE_B");
      code_c = trans.getValue("POSTI/DATA/CODE_C");
      code_d = trans.getValue("POSTI/DATA/CODE_D");
      code_e = trans.getValue("POSTI/DATA/CODE_E");
      code_f = trans.getValue("POSTI/DATA/CODE_F");
      code_g = trans.getValue("POSTI/DATA/CODE_G");
      code_h = trans.getValue("POSTI/DATA/CODE_H");
      code_i = trans.getValue("POSTI/DATA/CODE_I");
      code_j = trans.getValue("POSTI/DATA/CODE_J");

      if (!("0".equals(code_a)))  enabl0 = 1;
      else enabl0 = 0;
      if ("0".equals(code_b))  enabl1 = 0;
      else enabl1 = 1;
      if ("0".equals(code_c))  enabl2 = 0;
      else enabl2 = 1;
      if ("0".equals(code_d))  enabl3 = 0;
      else enabl3 = 1;
      if ("0".equals(code_e))  enabl4 = 0;
      else enabl4 = 1;
      if ("0".equals(code_f))  enabl5 = 0;
      else enabl5 = 1;
      if ("0".equals(code_g))  enabl6 = 0;
      else enabl6 = 1;
      if ("0".equals(code_h))  enabl7 = 0;
      else enabl7 = 1;
      if ("0".equals(code_i))  enabl8 = 0;
      else enabl8 = 1;
      if ("0".equals(code_j))  enabl9 = 0;
      else enabl9 = 1; 
   }

   //Making the buffer to be passed to prepost.page
   public void  makePrePostBuffer()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer prepost_buffer = mgr.newASPBuffer();
      ASPBuffer data = prepost_buffer.addBuffer("dataBuffer");
      data.addItem("CONTRACT",headset.getValue("CONTRACT"));
      data.addItem("PRE_ACCOUNTING_ID",headset.getRow().getValue("PRE_ACCOUNTING_ID"));
      data.addItem("ENABL0",Integer.toString(enabl0));
      data.addItem("ENABL1",Integer.toString(enabl1));
      data.addItem("ENABL2",Integer.toString(enabl2));
      data.addItem("ENABL3",Integer.toString(enabl3));
      data.addItem("ENABL4",Integer.toString(enabl4));
      data.addItem("ENABL5",Integer.toString(enabl5));
      data.addItem("ENABL6",Integer.toString(enabl6));
      data.addItem("ENABL7",Integer.toString(enabl7));
      data.addItem("ENABL8",Integer.toString(enabl8));
      data.addItem("ENABL9",Integer.toString(enabl9));
      data.addItem("ENABL10",Integer.toString(enabl10));

      ASPBuffer return_buffer = prepost_buffer.addBuffer("return_buffer");
      ASPBuffer ret = return_buffer.addBuffer("ROWS");
      ret.parse(headset.getRows("WO_NO").format());
      return_buffer.addItem("CURR_ROW",Integer.toString(headset.getCurrentRowNo()));
      return_buffer.addItem("LAYOUT",lout);

      mgr.transferDataTo("../mpccow/PreAccountingDlg.page",prepost_buffer);
   }

   // Web Alignment - Enable Multirow RMB actions
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
   //

   public void reportIn()
   {
      // Web Alignment - Enable Multirow RMB actions

      ASPManager mgr = getASPManager();

      int count = 0;
      ASPBuffer transferBuffer;
      ASPBuffer rowBuff;

      bOpenNewWindow = true;

      calling_url=mgr.getURL();
      ctx.setGlobal("CALLING_URL",calling_url);

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

      transferBuffer = mgr.newASPBuffer();

      for (int i = 0; i < count; i++)
      {
         rowBuff = mgr.newASPBuffer();

         if (i == 0)
         {
            rowBuff.addItem("WO_NO", headset.getValue("WO_NO"));
         }
         else
         {
            rowBuff.addItem(null, headset.getValue("WO_NO"));
         }

         transferBuffer.addBuffer("DATA", rowBuff);

         if (headlay.isMultirowLayout())
            headset.next();
      }

      if (headlay.isMultirowLayout())
         headset.setFilterOff();

      urlString = createTransferUrl("ActiveSeperateReportInWorkOrderSM.page", transferBuffer);

      newWinHandle = "ReportIn"; 
   }

   // Web Alignment - Enable Multirow RMB actions
   public void prepareWork()
   {

      ASPManager mgr = getASPManager();

      int count = 0;
      ASPBuffer transferBuffer;
      ASPBuffer rowBuff;

      bOpenNewWindow = true;

      calling_url=mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

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

      transferBuffer = mgr.newASPBuffer();

      for (int i = 0; i < count; i++)
      {
         rowBuff = mgr.newASPBuffer();

         if (i == 0)
         {
            rowBuff.addItem("WO_NO", headset.getValue("WO_NO"));
         }
         else
         {
            rowBuff.addItem(null, headset.getValue("WO_NO"));
         }

         transferBuffer.addBuffer("DATA", rowBuff);

         if (headlay.isMultirowLayout())
            headset.next();
      }

      if (headlay.isMultirowLayout())
         headset.setFilterOff();

      urlString = createTransferUrl("ActiveSeparate2ServiceManagement.page", transferBuffer);

      newWinHandle = "PrepareWO"; 
   }
   //

   public void agreement()
   {
      ASPManager mgr = getASPManager();
      if (headlay.isMultirowLayout())
      {
         headset.goTo(headset.getRowSelected());
         headset.storeSelections();
      }
      transferurl = "CustomerAgreement.page?AGREEMENT_ID="+mgr.URLEncode(headset.getRow().getValue("AGREEMENT_ID"));
   }

   public void createURL( String frm)
   {

      keys = buffer.format();
      buffer.clear();
      performRMB = frm;
   }

   public void custInfo()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
      {
         headset.goTo(headset.getRowSelected());
         headset.storeSelections();
      }

      current_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", current_url);
      buffer = mgr.newASPBuffer();
      row = buffer.addBuffer("0");
      row.addItem("CUSTOMER_ID", headset.getRow().getValue("CUSTOMER_ID"));

      createURL("../enterw/CustomerInfo");
   }

   public void custAgreement()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
      {
         headset.goTo(headset.getRowSelected());
         headset.storeSelections();
      }

      current_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", current_url);
      buffer = mgr.newASPBuffer();
      row = buffer.addBuffer("0");
      row.addItem("AGREEMENT_ID", headset.getRow().getValue("AGREEMENT_ID"));

      createURL("../orderw/CustomerAgreement");
   }


   //Bug 68947, Start   
   public void srvConLineSearch()
   {

      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
      {
         headset.goTo(headset.getRowSelected());
         headset.storeSelections();
      }

      current_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", current_url);
      buffer = mgr.newASPBuffer();
      row = buffer.addBuffer("0");
      row.addItem("CUSTOMER_NO", headset.getRow().getValue("CUSTOMER_NO"));
      row.addItem("WO_NO", headset.getRow().getValue("WO_NO"));
      row.addItem("CONTRACT", headset.getRow().getValue("CONTRACT"));
      row.addItem("MCH_CODE", headset.getRow().getValue("MCH_CODE"));
      row.addItem("WORK_TYPE_ID", headset.getRow().getValue("WORK_TYPE_ID"));

      createURL("ServiceContractLineSearchDlg");

   }
   //Bug 68947, End   

   public void actWoOrd()
   {
      ASPManager mgr = getASPManager();

      String enabledAW = "FALSE";

      if (headlay.isMultirowLayout())
      {
         headset.goTo(headset.getRowSelected());   

         if ("EQUIPMENT".equals(headset.getRow().getValue("CONNECTION_TYPE")))
            enabledAW = "TRUE";
         else
            enabledAW = "FALSE";   
      }
      else
         enabledAW = "TRUE";

      if ("TRUE".equals(enabledAW))
      {
         current_url = mgr.getURL();
         ctx.setGlobal("CALLING_URL",current_url);
         buffer = mgr.newASPBuffer();
         row = buffer.addBuffer("0");
         row.addItem("CONTRACT",headset.getRow().getValue("MCH_CODE_CONTRACT"));
         row.addItem("MCH_CODE",headset.getRow().getValue("MCH_CODE"));

         createURL("../equipw/MaintenanceObject2");
      }

      else
         mgr.showAlert("PCMWACTIVESEPARATE3CANNOTVIM: Can not perform for VIM connection types.");
   }

   public void deptResources()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
      {
         headset.goTo(headset.getRowSelected());
         headset.storeSelections();
      }

      current_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", current_url);
      buffer = mgr.newASPBuffer();
      row = buffer.addBuffer("0");
      row.addItem("ORG_CONTRACT", headset.getRow().getValue("CONTRACT"));
      row.addItem("ORG_CODE", headset.getRow().getValue("ORG_CODE"));

      createURL("DepartmentResources");
   }

   public void freeTimeSearch()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());

      bOpenNewWindow = true;
      urlString = "FreeTimeSearchEngine.page?CONTRACT=" + (mgr.isEmpty(headset.getValue("CONTRACT"))?"":headset.getValue("CONTRACT")) + 
                  "&ORG_CODE="+ (mgr.isEmpty(headset.getValue("ORG_CODE"))?"":headset.getValue("ORG_CODE")) +
                  "&WO_NO="+ (mgr.isEmpty(headset.getValue("WO_NO"))?"":headset.getValue("WO_NO")) +
                  "&PLAN_S_DATE="+ (mgr.isEmpty(headset.getRow().getFieldValue("PLAN_S_DATE"))?"":headset.getRow().getFieldValue("PLAN_S_DATE")) +
                  "&PLAN_F_DATE="+ (mgr.isEmpty(headset.getRow().getFieldValue("PLAN_F_DATE"))?"":headset.getRow().getFieldValue("PLAN_F_DATE")) +
                  "&PLAN_HRS="+ (mgr.isEmpty(headset.getValue("PLAN_HRS"))?"":headset.getValue("PLAN_HRS"));
      newWinHandle = "freeTimeSearch"; 
   }

   public void createQuot()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());

      //Bug 71738, start
      trans.clear();

      cmd = trans.addCustomFunction("FLTREP","ACTIVE_SEPARATE_API.Finite_State_Decode__","DBFAULTREPORT");     
      cmd.addParameter("FAULTREPORT","FAULTREPORT");
      trans = mgr.submit(trans);
      sDbFaultReport = trans.getValue("FLTREP/DATA/DBFAULTREPORT");
      trans.clear();
      //Bug 71738, end

      sState = headset.getValue("STATE");
      nQuotationId = headset.getValue("QUOTATION_ID");

      if ((sDbFaultReport.equals(sState)) &&  (mgr.isEmpty(nQuotationId)))
      {
         current_url = mgr.getURL();
         ctx.setGlobal("CALLING_URL", current_url);

         buffer = mgr.newASPBuffer();
         row = buffer.addBuffer("0");
         row.addItem("WO_NO", headset.getValue("WO_NO"));

         row = buffer.addBuffer("1");
         row.addItem("ERR_DESCR", headset.getValue("ERR_DESCR"));

         mgr.transferDataTo("ActiveSeparateDlg2.page", buffer);
      }
      else
         mgr.showAlert("PCMWACTIVESEPARATE3CANNOT: Can not perform on selected line.");
   }

   public void prepWoQuot()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());

      nQuotationId = headset.getValue("QUOTATION_ID");

      if (!mgr.isEmpty(nQuotationId))
      {
         buffer = mgr.newASPBuffer();
         row = buffer.addBuffer("0");
         row.addItem("QUOTATION_ID",headset.getValue("QUOTATION_ID"));
         fmtdBuff = buffer.format();

         //Web Alignment - simplify code for RMBs
         bOpenNewWindow = true;
         urlString = "WorkOrderQuotation.page?__TRANSFER="+mgr.URLEncode(fmtdBuff);
         newWinHandle = "WorkOrderQuotation";
         //
      }
      else
         mgr.showAlert("PCMWACTIVESEPARATE3CANNOT: Can not perform on selected line.");
   }

//------------Sets the check boxes in general tab------------------------------

   public void printProposal()
   {
      ASPManager mgr = getASPManager();

      //Web Alignment - Enable Multirow Action
      int count = 0;
      ASPBuffer print;
      ASPBuffer printBuff;
      String attr1;
      String attr2;
      String attr3;
      String attr4;

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

      for (int i = 0;i < count;i++)
      {
         attr1 = "REPORT_ID" + (char)31 + "PRINT_WORK_ORDER_SE_MA_REP" + (char)30;
         attr2 = "WO_NO_LIST" + (char)31 + headset.getValue("WO_NO") + (char)30;
         attr3 =  "";
         attr4 =  "";

         cmd = trans.addCustomCommand( "PRINTQUOT" + i, "Archive_API.New_Client_Report");
         cmd.addParameter("ATTR0");                       
         cmd.addParameter("ATTR1", attr1);       
         cmd.addParameter("ATTR2", attr2);              
         cmd.addParameter("ATTR3", attr3);      
         cmd.addParameter("ATTR4", attr4);  

         if (headlay.isMultirowLayout())
            headset.next();
      }

      trans = mgr.perform(trans);

      if (headlay.isMultirowLayout())
         headset.setFilterOff();

      print = mgr.newASPBuffer();

      for (int i = 0;i < count;i++)
      {
         printBuff = print.addBuffer("DATA");
         //printBuff = print.addBuffer("DATA" + i);
         printBuff.addItem("RESULT_KEY", trans.getValue("PRINTQUOT" + i + "/DATA/ATTR0"));
      }

      callPrintDlg(print,true);
      //
   }

   public void newSerialObject()
   {
      ASPManager mgr = getASPManager();
      String sOwnership = "";
      String sOwner = "";

      // Set values for Ownership and Owner
      if (headlay.isMultirowLayout() || headlay.isSingleLayout())
      {
         headset.goTo(headset.getRowSelected()); 
         trans.clear();

         String sIndex = "2";
         cmd = trans.addCustomFunction("GETCUSTOWNED","PART_OWNERSHIP_API.Get_Client_Value","PART_OWNERSHIP");
         cmd.addParameter("CLIENT_VAL", sIndex);

         trans = mgr.perform(trans); 

         sOwnership = trans.getValue("GETCUSTOWNED/DATA/PART_OWNERSHIP");
         sOwner = headset.getValue("CUSTOMER_NO");
      }
      if (headlay.isMultirowLayout())
      {
         headset.goTo(headset.getRowSelected());    

         if ("VIM".equals(headset.getRow().getValue("CONNECTION_TYPE")))
            mgr.showAlert("PCMWACTIVESEPARATE3CANNOTVIM: Can not perform for VIM connection types.");
         else
         {
            //Web Alignment - simplify code for RMBs
            bOpenNewWindow = true;
            urlString = "../equipw/CopySerialDlg.page?PART_OWNERSHIP="+mgr.URLEncode(sOwnership)+"&OWNER="+mgr.URLEncode(sOwner);
            newWinHandle = "newSerialObject";
            //
         }
      }
      else
      {
         //Web Alignment - simplify code for RMBs
         bOpenNewWindow = true;
         urlString = "../equipw/CopySerialDlg.page?PART_OWNERSHIP="+mgr.URLEncode(sOwnership)+"&OWNER="+mgr.URLEncode(sOwner);
         newWinHandle = "newSerialObject";
         //
      }
   }

   public void newFuncObject()
   {
      ASPManager mgr = getASPManager();
      if (headlay.isMultirowLayout())
      {
         headset.goTo(headset.getRowSelected());           
         if ("VIM".equals(headset.getRow().getValue("CONNECTION_TYPE")))
            mgr.showAlert("PCMWACTIVESEPARATE3CANNOTVIM: Can not perform for VIM connection types.");
         else
         {
            //Web Alignment - simplify code for RMBs
            bOpenNewWindow = true;
            urlString = "../equipw/CopyFunctionalDlg.page";
            newWinHandle = "newFunctionalObject";
            //
         }
      }
      else
      {
         //Web Alignment - simplify code for RMBs
         bOpenNewWindow = true;
         urlString = "../equipw/CopyFunctionalDlg.page";
         newWinHandle = "newFunctionalObject";
         //
      }
   }

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTION  ------------------------------
//-----------------------------------------------------------------------------

   /*public void sendsms()
   {
       ASPManager mgr = getASPManager();

       mgr.redirectTo("../common/scripts/SendSMSMessage.page");
   }*/

   public void preDefine()
   {
      ASPManager mgr = getASPManager();

      //---------- block used for setcheckbox() funtion --------

      tempblk = mgr.newASPBlock("TEMP");
      f = tempblk.addField("LUNAME");

      //-----------------------------------------------------------------------
      //----------------- block used for RMB status funtions  -----------------
      //-----------------------------------------------------------------------

      inact = mgr.newASPBlock("RMBLK3");
      inact.addField("INFO");
      inact.addField("ATTR");
      inact.addField("ACTION");

      //---------- block used for getsettingtime() function --------

      headblk = mgr.newASPBlock("HEAD");

      f = headblk.addField("OBJID");
      f.setHidden();

      f = headblk.addField("OBJVERSION");
      f.setHidden();

      f = headblk.addField("OBJSTATE");
      f.setHidden();

      f = headblk.addField("OBJEVENTS"); 
      f.setHidden();

      f = headblk.addField("DUP_OBJEVENTS");
      f.setHidden();
      f.setFunction("OBJEVENTS");

      f = headblk.addField("WO_NO","Number","#");
      f.setSize(13); 
      f.setLabel("PCMWACTIVESEPARATE3WO_NO: WO No");
      f.setReadOnly();

      f = headblk.addField("CONTRACT");
      f.setSize(5);
      f.setLOV("../Mpccow/UserAllowedSiteLovLov.page",600,450);
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
      f.setLabel("PCMWACTIVESEPARATE3WOCONTRACT: WO Site");
      f.setCustomValidation("CONTRACT","COMPANY");
      f.setUpperCase();
      f.setInsertable();
      f.setReadOnly();
      f.setMaxLength(5);

      f = headblk.addField("WO_NO_LIST");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("REG_DATE","Datetime");
      f.setSize(25);
      f.setMandatory();
      f.setLabel("PCMWACTIVESEPARATE3REG_DATE: Date");
      f.setReadOnly(); 
      f.setInsertable();
      f.setDefaultNotVisible();
      f.setMaxLength(100);
      f.setCustomValidation("WO_NO,REG_DATE","TEMP_DATE");   

      f = headblk.addField("REPORTED_BY");
      f.setSize(13);
      f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445);
      f.setLOVProperty("TITLE", mgr.translate("PCMWACTIVESEPARATE3REPBY: List of Reported By"));
      f.setMandatory();
      f.setLabel("PCMWACTIVESEPARATE3REPORTEDBY: Reported by");
      f.setDefaultNotVisible();
      f.setUpperCase();
      f.setCustomValidation("REPORTED_BY,COMPANY","REPORTED_BY_ID");

      f = headblk.addField("STATE"); 
      f.setSize(25);
      f.setLabel("PCMWACTIVESEPARATE3STATE: Status");
      f.setLOV("ActiveSeparateLov1.page",600,450);
      f.setReadOnly();

      //------------Fields in Group1 ---- General-----------------------




      f = headblk.addField("CONTACT");
      f.setSize(29);
      f.setLabel("PCMWACTIVESEPARATE3CONTACT: Contact");
      f.setMaxLength(30);
      f.setDefaultNotVisible();

      f = headblk.addField("PHONE_NO");
      f.setSize(18);
      f.setLabel("PCMWACTIVESEPARATE3PHONE_NO: Phone No");
      f.setDefaultNotVisible();
      f.setMaxLength(20);

      f = headblk.addField("ERR_DESCR");
      f.setSize(40);
      f.setMandatory();
      f.setLabel("PCMWACTIVESEPARATE3ERR_DESCR: Short Description");
      f.setMaxLength(60);
      f.setDefaultNotVisible();

      //Bug 69819, Start, Modified validation
      f = headblk.addField("WORK_TYPE_ID");
      f.setDynamicLOV("WORK_TYPE",600,445);
//      Bug 69047, Start
      //Bug Id 70921, Removed PLAN_HRS,REQUIRED_START_DATE,PLAN_S_DATE
      f.setCustomValidation("AGREEMENT_ID,CONTRACT,MCH_CODE,WORK_TYPE_ID,ORG_CODE,ORGCODEDESCRIPTION,SETTINGTIME,REG_DATE,CUSTOMER_NO,MCH_CODE_CONTRACT,CURRENCY_CODE","FAULT_REP_AGREE_CLIENT,FAULT_REP_AGREE,ORG_CODE,ORGCODEDESCRIPTION,WORKTYPEDESCRIPTION,CONTRACT_ID,LINE_NO");

//      Bug 69047, End
      f.setLabel("PCMWACTIVESEPARATE3WORK_TYPE_ID: Work Type");
      f.setUpperCase();
      f.setSize(18);
      f.setDefaultNotVisible();
      f.setMaxLength(20);
      //Bug 69819, End

      f = headblk.addField("WORKTYPEDESCRIPTION");
      f.setSize(30);
      f.setFunction("WORK_TYPE_API.Get_Description(:WORK_TYPE_ID)");
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE3WORK_TYPE_DESC: Work Type Description");
      f.setDefaultNotVisible();

      f = headblk.addField("REFERENCE_NO");
      f.setSize(28);
      f.setLabel("PCMWACTIVESEPARATE3REFERENCE_NO: Reference No");
      f.setDefaultNotVisible();
      f.setMaxLength(25);

      f = headblk.addField("WARRANTY_ROW_NO");
      f.setSize(12);
      f.setLabel("PCMWACTIVESEPARATE3WARRANTY: Warranty No");
      f.setUpperCase(); 
      f.setMaxLength(20);
      f.setHidden();

      f = headblk.addField("CUSTOMERDESCRIPTION");
      f.setSize(30);
      f.setLabel("PCMWACTIVESEPARATE3CUSTOMERDESC: Customer Name");
      f.setFunction("CUSTOMER_INFO_API.Get_Name(:CUSTOMER_NO)");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("CUST_NO");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("CLIENT_VAL");
      f.setHidden();
      f.setFunction("''"); 

      //Bug 69819, Start, Modified function call
      f = headblk.addField("CUSTOMERAGREEMENTDESCRIPTION");
      f.setSize(30);
      f.setFunction("''");
      f.setHidden();
      f.setLabel("PCMWACTIVESEPARATE3AGREEMENTIDDES: Agreement ID");
      f.setDefaultNotVisible();
      //Bug 69819, End

      f = headblk.addField("FAULT_REP_AGREE");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("FAULT_REP_AGREE_CLIENT");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("CONNECTION_TYPE");
      f.setSize(20);
      f.setSelectBox();
      f.setReadOnly();
      f.setInsertable();
      f.setCustomValidation("CONNECTION_TYPE","CONNECTION_TYPE_DB");
      f.enumerateValues("MAINT_CONNECTION_TYPE_API");
      f.setLabel("PCMWACTIVESEPARATE3CONNECTIONTYPE: Connection Type");

      f = headblk.addField("CONNECTION_TYPE_DB");
      f.setHidden();

      f = headblk.addField("CONTRACT_ID");             
      f.setLOV("PscContrProductLov.page","CONTRACT,CUSTOMER_NO,MCH_CODE");
      // Bug Id 70012, removed Where property of LOV
      // Bug Id 70921, added work type and orgcode for contract_id validation
      f.setCustomValidation("CONTRACT_ID,LINE_NO,WO_NO,PLAN_S_DATE,PLAN_F_DATE,REQUIRED_START_DATE,REQUIRED_END_DATE,PLAN_HRS,PRIORITY_ID,REG_DATE","CONTRACT_ID,LINE_NO,CONTRACT_NAME,LINE_DESC,CONTRACT_TYPE,INVOICE_TYPE,CUSTOMER_NO,AUTHORIZE_CODE,AGREEMENT_ID,AGREEMENT_DESC,WORK_TYPE_ID,WORKTYPEDESCRIPTION,ORG_CODE,ORGCODEDESCRIPTION,PLAN_S_DATE,REQUIRED_START_DATE,PLAN_F_DATE,REQUIRED_END_DATE,PLAN_HRS,DUMMY_CONTRACT_ID,DUMMY_REQ_START_DATE,DUMMY_REQ_END_DATE");
      f.setUpperCase();
      f.setDefaultNotVisible();
      f.setMaxLength(15);
      f.setLabel("PCMWACTIVESEPARATE3CONTRACTID: Contract ID");
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
      f.setLabel("PCMWACTIVESEPARATE3CONTRACTNAME: Contract Name");
      f.setSize(15);

      f = headblk.addField("LINE_NO");
      f.setLOV("PscContrProductLov.page","CONTRACT,CUSTOMER_NO,MCH_CODE,CONTRACT_ID");
      // Bug Id 70921, added work type and orgcode for contract_id validation
      f.setCustomValidation("CONTRACT_ID,LINE_NO,WO_NO,PLAN_S_DATE,PLAN_F_DATE,REQUIRED_START_DATE,REQUIRED_END_DATE,PLAN_HRS,PRIORITY_ID,REG_DATE","CONTRACT_ID,LINE_NO,CONTRACT_NAME,LINE_DESC,CONTRACT_TYPE,INVOICE_TYPE,AGREEMENT_ID,AGREEMENT_DESC,WORK_TYPE_ID,WORKTYPEDESCRIPTION,ORG_CODE,ORGCODEDESCRIPTION,PLAN_S_DATE,REQUIRED_START_DATE,PLAN_F_DATE,REQUIRED_END_DATE,PLAN_HRS,DUMMY_CONTRACT_ID,DUMMY_REQ_START_DATE,DUMMY_REQ_END_DATE");
      f.setDefaultNotVisible();
      f.setLabel("PCMWACTIVESEPARATE3LINENO: Line No");
      f.setSize(10); 

      //Bug 84436, Start 
      f = headblk.addField("LINE_DESC");                     
      f.setDefaultNotVisible();
      if (mgr.isModuleInstalled("PCMSCI"))
         f.setFunction("PSC_CONTR_PRODUCT_API.Get_Description(:CONTRACT_ID,:LINE_NO)");
      else
         f.setFunction("''");
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE3LINEDESC: Description");
      f.setSize(15);
      //Bug 84436, End 

      f = headblk.addField("CONTRACT_TYPE");                     
      f.setDefaultNotVisible();
      //Bug 84436, Start  
      if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("SC_SERVICE_CONTRACT_API.Get_Contract_Type(:CONTRACT_ID)");
       else
            f.setFunction("''");
      //Bug 84436, End  
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE3CONTRACTTYPE: Contract Type");
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
      f.setLabel("PCMWACTIVESEPARATE3TYPE: Invoice Type");
      f.setSize(15);

      f = headblk.addField("AGREEMENT_ID");
      f.setSize(12);
      f.setHidden();
      f.setLabel("PCMWACTIVESEPARATE3AGREEMENTID: Agreement ID");
      f.setCustomValidation("AGREEMENT_ID,CONTRACT,MCH_CODE,WORK_TYPE_ID,ORG_CODE,ORGCODEDESCRIPTION,SETTINGTIME,REG_DATE,CUSTOMER_NO,REQUIRED_START_DATE,MCH_CODE_CONTRACT,PLAN_S_DATE,CONNECTION_TYPE_DB","ORG_CODE,ORGCODEDESCRIPTION,PLAN_HRS,AUTHORIZE_CODE,FAULT_REP_AGREE,FAULT_REP_AGREE_CLIENT,PLAN_S_DATE,REQUIRED_START_DATE,SETTINGTIME,PLAN_F_DATE,REQUIRED_END_DATE"); 
      f.setUpperCase();
      f.setDefaultNotVisible();
      f.setMaxLength(10);  

      f = headblk.addField("AGREEMENT_DESC");
      f.setHidden();
      f.setFunction("''");

      //Bug 69819, Start, Modified validation
      f = headblk.addField("MCH_CODE");
      f.setSize(13);
//      Bug 69047, Start
      //Bug Id 70921, Removed PLAN_S_DATE,REQUIRED_START_DATE,PLAN_F_DATE,REQUIRED_END_DATE,PLAN_HRS
      f.setCustomValidation("AGREEMENT_ID,CONNECTION_TYPE_DB,CONTRACT,MCH_CODE,WORK_TYPE_ID,ORG_CODE,ORGCODEDESCRIPTION,AUTHORIZE_CODE,REG_DATE,CUSTOMER_NO,SETTINGTIME,MCH_CODE_CONTRACT,CURRENCY_CODE","SETTINGTIME,CBWARRANTYONOBJECT,CBHASAGREEMENT,CBHASACTIVEWO,ADDRESS_ID,ADDRESS1,ADDRESS2,ADDRESS3,ADDRESS4,ADDRESS5,ADDRESS6,AGREEMENT_ID,AUTHORIZE_CODE,ORG_CODE,ORGCODEDESCRIPTION,MCH_CODE_DESCRIPTION,MCH_CODE,MCH_CODE_CONTRACT,PART_NO,SERIAL_NO,CBHASCUSTWARRANTYONOBJ,CBHASWARRANTYONOBJ,CONTRACT_ID,LINE_NO");
//      Bug 69047, End
      f.setLabel("PCMWACTIVESEPARATE3MCHCODE: Object ID");
      f.setUpperCase();
      f.setMaxLength(100);
      //f.setHyperlink("../equipw/EquipmentAllObjectLightRedirect.page","MCH_CODE,MCH_CODE_CONTRACT,WO_NO","NEWWIN");
      //Bug 69819, End

      f = headblk.addField("MCH_CODE_DESCRIPTION");
      f.setSize(44);
      f.setLabel("PCMWACTIVESEPARATE3MCHCODEDESCRIPTION: Object Description");
      f.setDefaultNotVisible();
      f.setReadOnly(); 

      f = headblk.addField("MCH_CODE_CONTRACT");
      f.setSize(5);
      f.setLOV("../Mpccow/UserAllowedSiteLovLov.page",600,450);
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
      f.setLabel("PCMWACTIVESEPARATE3CONTRACT: Site");
      f.setUpperCase();
      f.setMaxLength(5);

      f = headblk.addField("CBHASACTIVEWO"); 
      f.setSize(12);
      f.setLabel("PCMWACTIVESEPARATE3CBHASACTIVEWO: Has Active WO");
      f.setFunction("Active_Work_Order_API.Obj_Has_Wo(:MCH_CODE_CONTRACT,:MCH_CODE)");
      f.setCheckBox("FALSE,TRUE");
      f.setDefaultNotVisible();

      f = headblk.addField("CBHASAGREEMENT");
      f.setSize(2);
      f.setLabel("PCMWACTIVESEPARATE3CBHASSERVICECONTRACT: Has Service Contract");
      //Bug 84436, Start 
      if (mgr.isModuleInstalled("PCMSCI"))
         f.setFunction("Psc_Contr_Product_Api.Has_Contract_For_Object(:CONNECTION_TYPE_DB,:MCH_CODE,:MCH_CODE_CONTRACT,:CUSTOMER_NO)");
      else
         f.setFunction("''");
      //Bug 84436, End
      f.setCheckBox("FALSE,TRUE");
      f.setDefaultNotVisible();

      fADDRESS1 = headblk.addField("ADDRESS1").
                  setSize(22).
                  setDynamicLOV("EQUIPMENT_OBJECT_ADDR1_LOV","MCH_CODE_CONTRACT, MCH_CODE",600,445).
                  setLabel("PCMWACTIVESEPARATE3ADDRESS1: Address 1"). 
                  setDefaultNotVisible().
                  setMaxLength(100);

      fADDRESS2 = headblk.addField("ADDRESS2").
                  setSize(22).
                  setDynamicLOV("EQUIPMENT_OBJECT_ADDR2_LOV","MCH_CODE_CONTRACT, MCH_CODE",600,445).
                  setLabel("PCMWACTIVESEPARATE3ADDRESS2: Address 2"). 
                  setDefaultNotVisible().
                  setMaxLength(35);

      fZIP_CODE = headblk.addField("ADDRESS3").
                  setSize(22).
                  setDynamicLOV("EQUIPMENT_OBJECT_ADDR3_LOV","MCH_CODE_CONTRACT, MCH_CODE",600,445).
                  setLabel("PCMWACTIVESEPARATE3ADDRESS3: Address 3").
                  setDefaultNotVisible().
                  setMaxLength(35);

      fCITY = headblk.addField("ADDRESS4").
              setSize(22).
              setDynamicLOV("EQUIPMENT_OBJECT_ADDR4_LOV","MCH_CODE_CONTRACT, MCH_CODE",600,445).        
              setLabel("PCMWACTIVESEPARATE3ADDRESS4: Address 4").
              setDefaultNotVisible().
              setMaxLength(35);

      fSTATE = headblk.addField("ADDRESS5").
               setSize(22).
               setDynamicLOV("EQUIPMENT_OBJECT_ADDR5_LOV","MCH_CODE_CONTRACT, MCH_CODE",600,445).        
               setLabel("PCMWACTIVESEPARATE3ADDRESS5: Address 5"). 
               setDefaultNotVisible().
               setMaxLength(35);

      fCOUNTY = headblk.addField("ADDRESS6").
                setSize(22).
                setDynamicLOV("EQUIPMENT_OBJECT_ADDR6_LOV","MCH_CODE_CONTRACT, MCH_CODE",600,445).       
                setLabel("PCMWACTIVESEPARATE3ADDRESS6: Address 6"). 
                setDefaultNotVisible().
                setMaxLength(35);

      fCOUNTRY_CODE = headblk.addField("TEMP1").
                      setSize(22).
                      setFunction("CUSTOMER_INFO_API.Get_Country_Code(:CUSTOMER_NO)").
                      setDefaultNotVisible().
                      setLabel("PCMWACTIVESEPARATE3CONTRYODE: Country Code"). 
                      setHidden().
                      setMaxLength(35);

      fCOUNTRY = headblk.addField("TEMP2").
                 setSize(22).
                 setDynamicLOV("EQUIPMENT_OBJECT_ADDR6_LOV","MCH_CODE_CONTRACT, MCH_CODE",600,445).   
                 setFunction("ADDRESS6").
                 setLabel("PCMWACTIVESEPARATE3ADDRESS6: Address 6"). 
                 setDefaultNotVisible().
                 setHidden().
                 setMaxLength(35);


      f = headblk.addField("ADDRESS_ID");
      f.setFunction("Equipment_Object_Address_API.Get_Default_Address_Id(:MCH_CODE_CONTRACT,:MCH_CODE)");
      f.setHidden();

      f = headblk.addField("CUST_ADD_ID");
      f.setCustomValidation("CUSTOMER_NO,CUST_ADD_ID","ADDRESS1,ADDRESS2,ADDRESS3,ADDRESS4,ADDRESS5,ADDRESS6");
      f.setLabel("PCMWACTIVESEPARATE3CUSTADDIDD: Cust Address Id");
      f.setValidateFunction("setCustomerAddress");
      f.setHidden();

      f = headblk.addField("NAME");
      f.setFunction("PERSON_INFO_API.Get_Name(:REPORTED_BY)");
      f.setReadOnly();
      f.setHidden();

      f = headblk.addField("ORG_CODE");
      f.setSize(15);
      f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,445);
      f.setMandatory();
      f.setLabel("PCMWACTIVESEPARATE3ORG_CODE: Maintenance Organization"); 
      f.setUpperCase();
      f.setCustomValidation("CONTRACT,ORG_CODE","ORGCODEDESCRIPTION");
      f.setMaxLength(8);

      f = headblk.addField("ORGCODEDESCRIPTION");
      f.setSize(24);
      f.setFunction("Organization_API.Get_Description(:CONTRACT,:ORG_CODE)");
      f.setLabel("PCMWACTIVESEPARATE3ORG_CODEDESC: Description"); 
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("SETTINGTIME","Number");
      f.setSize(10);
      f.setLabel("PCMWACTIVESEPARATE3SETTINGTIME: Setting time");
      f.setFunction("''");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("PRIORITY_ID");                                        
      f.setSize(8);                                                                  
      f.setDynamicLOV("MAINTENANCE_PRIORITY",600,450);                               
      //Bug Id 70921, Added validations to PriorityId
      f.setCustomValidation("PRIORITY_ID,PLAN_S_DATE,REQUIRED_START_DATE,CONTRACT_ID,LINE_NO","PLAN_HRS,PLAN_F_DATE,REQUIRED_END_DATE,PRIORITYDESCRIPTION");
      f.setLabel("PCMWACTIVESEPARATE3PRIORITY_ID: Priority");                                           
      f.setUpperCase();                                                              
      f.setMaxLength(1);                                                             
      f.setDefaultNotVisible();                                                      

      f = headblk.addField("PRIORITYDESCRIPTION");                                   
      f.setSize(24);                                                                 
      f.setFunction("Maintenance_Priority_API.Get_Description(:PRIORITY_ID)"); 
      mgr.getASPField("PRIORITY_ID").setValidation("PRIORITYDESCRIPTION");    
      f.setReadOnly();                                                               
      f.setDefaultNotVisible();                                                      

      // Tab Planning shedule is added----------

      f = headblk.addField("REQUIRED_START_DATE","Datetime");
      f.setSize(30);
      f.setLabel("PCMWACTIVESEPARATE3REQSDATE: Required Start");
      f.setCustomValidation("WO_NO,REQUIRED_START_DATE","TEMP_DATE"); 

      f = headblk.addField("REQUIRED_END_DATE","Datetime");
      f.setSize(30);
      f.setLabel("PCMWACTIVESEPARATE3REQEDATE: Latest Completion");
      f.setCustomValidation("WO_NO,REQUIRED_END_DATE","TEMP_DATE");   

      f = headblk.addField("PLAN_S_DATE","Datetime");
      f.setSize(30);
      f.setLabel("PCMWACTIVESEPARATE3PLANSDATE: Start");
      f.setCustomValidation("PLAN_S_DATE,PLAN_F_DATE,PLAN_HRS,CONTRACT,MCH_CODE,HEAD_TEMP","PLAN_F_DATE");
      f.setDefaultNotVisible();

      f = headblk.addField("PLAN_F_DATE","Datetime");
      f.setSize(30);
      f.setLabel("PCMWACTIVESEPARATE3PLANFDATE: Completion");
      f.setCustomValidation("PLAN_S_DATE,PLAN_F_DATE,PLAN_HRS,CONTRACT,MCH_CODE,HEAD_TEMP","PLAN_F_DATE,PLAN_S_DATE,HEAD_TEMP");
      f.setDefaultNotVisible();

      f = headblk.addField("DUMMY_PLAN_F_DATE","Datetime");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("PLAN_HRS","Number");
      f.setSize(30);
      f.setLabel("PCMWACTIVESEPARATE3PLANHRS: Execution Time");
      f.setCustomValidation("PLAN_S_DATE,PLAN_F_DATE,PLAN_HRS,REQUIRED_START_DATE,REQUIRED_END_DATE,CUSTOMER_NO,ORG_CODE,CONTRACT,CONTRACT_ID,LINE_NO","PLAN_F_DATE,REQUIRED_END_DATE");
      f.setDefaultNotVisible();

      f = headblk.addField("CUSTOMER_ID");
      f.setHidden();
      f.setFunction("CUSTOMER_NO");

      //Bug 69819, Start, Modified validation
      f = headblk.addField("CUSTOMER_NO");
      f.setSize(12);
      f.setDynamicLOV("CUSTOMER_INFO",600,445);
//      Bug 69047, Start
//      Bug 68363, Start
      //Bug 68947, Start   
      f.setCustomValidation("AGREEMENT_ID,CONTRACT,MCH_CODE,WORK_TYPE_ID,CUSTOMER_NO,MCH_CODE_CONTRACT,CONNECTION_TYPE_DB,CURRENCY_CODE,CONTRACT_ID,LINE_NO,WO_NO","CUST_NO,CLIENT_VALUE,FAULT_REP_AGREE,FAULT_REP_AGREE_CLIENT,CBHASAGREEMENT,CUSTOMERDESCRIPTION,ORG_CODE,ORGCODEDESCRIPTION,CONTRACT_ID,LINE_NO");
//      Bug 68363, End
//      Bug 69047, End
      //Bug 68947, End   
      f.setLabel("PCMWACTIVESEPARATE3CUSTOMERNO: Customer No");
      f.setUpperCase(); 
      f.setMaxLength(20);
      //Bug 69819, End

      f = headblk.addField("PART_OWNERSHIP");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("OWNER");
      f.setHidden();
      f.setFunction("''");

      // 031001  ARWILK  Begin (Bug#104731)
      f = headblk.addField("CLIENT_VALUE");
      // 031001  ARWILK  End (Bug#104731)
      f.setHidden();
      f.setFunction("''");

      // -------------------------------------

      // Tab more information about fault is added---------

      f = headblk.addField("ERR_DISCOVER_CODE");
      f.setSize(4);
      f.setMaxLength(3);
      f.setDynamicLOV("WORK_ORDER_DISC_CODE",600,450);
      f.setLabel("PCMWACTIVESEPARATE3ERRDISCOVERCODE: Discovery Code");
      f.setUpperCase();
      f.setCustomValidation("ERR_DISCOVER_CODE","DISCOVERDESCRIPTION");   
      f.setDefaultNotVisible();

      f = headblk.addField("DISCOVERDESCRIPTION");
      f.setSize(32);
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE3DISCDESC: Discovery Description");   
      f.setFunction("WORK_ORDER_DISC_CODE_API.Get_Description(:ERR_DISCOVER_CODE)");
      f.setDefaultNotVisible();

      f = headblk.addField("ERR_SYMPTOM");
      f.setSize(6);
      f.setDynamicLOV("WORK_ORDER_SYMPT_CODE",600,450);
      f.setLabel("PCMWACTIVESEPARATE3ERRSYMPTOM: Symptom");
      f.setMaxLength(3);
      f.setUpperCase();
      f.setCustomValidation("ERR_SYMPTOM","SYMPTOMDESCRIPTION");
      f.setDefaultNotVisible();

      f = headblk.addField("SYMPTOMDESCRIPTION");
      f.setSize(34);
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE3SYMPTDESC: Symptom Description");   
      f.setFunction("WORK_ORDER_SYMPT_CODE_API.Get_Description(:ERR_SYMPTOM)");
      f.setDefaultNotVisible();

      f = headblk.addField("ERR_DESCR_LO");
      f.setSize(49);
      f.setHeight(3);
      f.setMaxLength(2000);
      f.setLabel("PCMWACTIVESEPARATE3ERRDESCRLO: Fault Descr");
      f.setDefaultNotVisible();

      //-----------------------------------------------
      f = headblk.addField("TEMP_DATE");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("HEAD_TEMP");
      f.setHidden();
      f.setFunction("''");

      ///----------------------------not shown in the table-----------------------
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

      //----------To get company-----------------

      f = headblk.addField("DEFCONTRACT");
      f.setFunction("''");
      f.setHidden();

      //----------To get DB Status of FAULTREPORT-----------------

      f = headblk.addField("FAULTREPORT");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("DBFAULTREPORT");
      f.setFunction("''");
      f.setHidden();

      //-----------------------------------------
      f = headblk.addField("QUOTATION_ID");
      f.setHidden();

      f = headblk.addField("REPORTED_BY_ID");
      f.setSize(6);
      f.setHidden();
      f.setUpperCase();

      f = headblk.addField("CBWARRANTYONOBJECT");
      f.setLabel("PCMWACTIVESEPARATE3CBWARRANTYONOBJECT: Warranty on Object"); 
      f.setFunction("''");
      f.setReadOnly();
      f.setHidden();   

      f = headblk.addField("PRE_ACCOUNTING_ID","Number");
      f.setHidden();
      f.setFunction("ACTIVE_SEPARATE_API.Get_Pre_Accounting_Id(:WO_NO)");

      f = headblk.addField("MODULE");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("CODE_C");
      f.setHidden();

      f = headblk.addField("CODE_D");
      f.setHidden();

      f = headblk.addField("CODE_E");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("CODE_F");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("CODE_G");
      f.setHidden();

      f = headblk.addField("CODE_H");
      f.setHidden();

      f = headblk.addField("CODE_I");
      f.setHidden();

      f = headblk.addField("CODE_J");
      f.setHidden();

      f = headblk.addField("COMPANY");                                            
      f.setSize(6);                                                               
      f.setHidden();                                                              

      // 031003  ARWILK  Begin (Bug#104743)
      f = headblk.addField("WORK_DESCR_LO");                                      
      f.setSize(50);     
      f.setMaxLength(2000);
      f.setLabel("PCMWACTIVESEPARATE3WORK_DESCR_LO: Work Description");       
      f.setHeight(5);
      //f.setHidden();                                                              
      // 031003  ARWILK  End (Bug#104743)

      f = headblk.addField("OP_STATUS_ID");                                        
      f.setSize(8);                                                               
      f.setLOV("OperationalStatusLov.page",600,450);                              
      f.setLabel("PCMWACTIVESEPARATE3OPSTATUSID: Op Status");                                       
      f.setUpperCase();                                                           
      f.setHidden();                                                              

      f = headblk.addField("OPSTATUSIDDESCR");                                    
      f.setSize(27);                                                               
      f.setFunction("OPERATIONAL_STATUS_API.Get_Description(:OP_STATUS_ID)");     
      mgr.getASPField("OP_STATUS_ID").setValidation("OPSTATUSIDDESCR");           
      f.setReadOnly();                                                            
      f.setHidden();                                                              

      f = headblk.addField("CALL_CODE");
      f.setSize(8);
      f.setLOV("MaintenanceEventLov.page",600,450);
      f.setLabel("PCMWACTIVESEPARATE3CALLCODE: Event");
      f.setUpperCase();
      f.setHidden();

      f = headblk.addField("CALLCODEDESCRIPTION");
      f.setSize(27);
      f.setFunction("MAINTENANCE_EVENT_API.Get_Description(:CALL_CODE)");
      mgr.getASPField("CALL_CODE").setValidation("CALLCODEDESCRIPTION");
      f.setReadOnly();
      f.setHidden();

      f = headblk.addField("VENDOR_NO");            
      f.setSize(8);                                     
      f.setLOV("SupplierInfoLov.page",600,450);
      f.setLabel("PCMWACTIVESEPARATE3VENDORNO: Contractor");               
      f.setUpperCase();                                 
      f.setHidden();                                    

      f = headblk.addField("VENDORNAME");
      f.setSize(27);
      f.setFunction("Supplier_Info_API.Get_Name(:VENDOR_NO)");
      mgr.getASPField("VENDOR_NO").setValidation("VENDORNAME");
      f.setReadOnly();
      f.setHidden();

      f = headblk.addField("PREPARED_BY");
      f.setSize(11);
      f.setLOV("EmployeeLovLov.page","COMPANY",600,450);
      f.setLabel("PCMWACTIVESEPARATE3PREPAREDBY: Prepared by");
      f.setUpperCase();
      f.setHidden();

      f = headblk.addField("PREPARED_BY_ID");
      f.setHidden();
      f.setUpperCase();
      f = headblk.addField("PM_NO","Number");
      f.setSize(8);
      f.setLOV("PmActionLov.page",600,450);
      f.setLabel("PCMWACTIVESEPARATE3PMNO: PM No");
      f.setReadOnly();
      f.setHidden();

      f = headblk.addField("ACTION_CODE_ID");
      f.setSize(35);
      f.setLOV("MaintenanceActionLov.page",600,450);
      f.setLabel("PCMWACTIVESEPARATE3ACTIONCODEID: Action");
      f.setUpperCase();
      f.setReadOnly();
      f.setHidden();

      f = headblk.addField("PM_DESCR");             
      f.setSize(35);                                     
      f.setLabel("PCMWACTIVESEPARATE3PMDESCR: PM Description");    
      f.setReadOnly();                                   
      f.setHidden();                                     

      f = headblk.addField("FAULT_REP_FLAG","Number");
      f.setHidden();

      f = headblk.addField("WO_KEY_VALUE");
      f.setUpperCase();
      f.setHidden();
      f.setFunction("WO_KEY_VALUE");

      f = headblk.addField("NORGANIZATIONORG_COST","Number");
      f.setLabel("PCMWACTIVESEPARATE3NORGANIZATIONORG_COST: Org_Cost");
      f.setFunction("ORGANIZATION_API.Get_Org_Cost(:CONTRACT,:ORG_CODE)");
      f.setHidden();

      f = headblk.addField("FND_USER");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("WORK_LEADER_SIGN");
      f.setUpperCase();
      f.setLabel("PCMWACTIVESEPARATE3WORKLEADER: Work Leader");
      f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445);
      f.setCustomValidation("WORK_LEADER_SIGN,COMPANY","WORK_LEADER_SIGN_ID");
      f.setDefaultNotVisible();

      f = headblk.addField("WORK_LEADER_SIGN_ID");
      f.setUpperCase();  
      f.setHidden();
      f.setMaxLength(11); 

      f = headblk.addField("WORK_MASTER_SIGN");
      f.setUpperCase();
      f.setLabel("PCMWACTIVESEPARATE3EXECBY: Executed By");
      f.setCustomValidation("WORK_MASTER_SIGN,COMPANY","WORK_MASTER_SIGN_ID");
      f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445);
      f.setDefaultNotVisible();

      f = headblk.addField("WORK_MASTER_SIGN_ID");
      f.setUpperCase();  
      f.setHidden();
      f.setMaxLength(11); 

      f = headblk.addField("AUTHORIZE_CODE");
      f.setUpperCase();
      f.setLabel("PCMWACTIVESEPARATE3AUTHORIZECODE: Coordinator");
      f.setDynamicLOV("ORDER_COORDINATOR_LOV",600,445);
      f.setDefaultNotVisible();

      f = headblk.addField("CBHASWARRANTYONOBJ");
      f.setCheckBox("FALSE,TRUE");
      f.setFunction("substr(Object_Supplier_Warranty_API.Has_Warranty(:MCH_CODE_CONTRACT,:MCH_CODE,:REG_DATE),1,5)");
      f.setLabel("PCMWACTIVESEPARATE3SUPPWARR: Supplier Warranty");
      f.setDefaultNotVisible();

      f = headblk.addField("CBHASCUSTWARRANTYONOBJ");
      f.setCheckBox("FALSE,TRUE");
      f.setFunction("substr(Object_Cust_Warranty_API.Has_Warranty(:MCH_CODE_CONTRACT,:MCH_CODE,:REG_DATE),1,5)");
      //f.setLabel("PCMWACTIVESEPARATE3CBHASCUSTWARRANTYONOBJ: Customer Warranty");
      f.setLabel("PCMWACTIVESEPARATE3CUSTWARR: Customer Warranty");
      f.setDefaultNotVisible();

      f = headblk.addField("REPAIR_FLAG");
      f.setCheckBox("FALSE,TRUE");
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE3REPAIRWO: Repair Work Order");
      f.setDefaultNotVisible();

      /* 107820
      f = headblk.addField("CBTRANSFEREDTOMOB");
      f.setCheckBox("FALSE,TRUE");
      f.setFunction("''");
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE3TRANSFEREDTOMOB: Transferred to Mobile");
      f.setDefaultNotVisible();
      */

      f = headblk.addField("FIXED_PRICE_DB");
      f.setCheckBox("1,2");
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE3FIXEDPRICEDB: Fixed Price");
      f.setDefaultNotVisible();

      f = headblk.addField("CBMULCUST");
      f.setFunction("Work_Order_Coding_Utility_API.Multiple_Customer_Exist(WO_NO)");
      f.setLabel("PCMWACTIVESEPARATE3CBMULCUST: Multiple Customer Exist");
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setCheckBox("FALSE,TRUE");

      f = headblk.addField("CBWARRWO");
      f.setFunction("substr(Active_Separate_API.Has_Cust_Warr_Type(:WO_NO),1,5)");
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCBWARRWO: Warranty Work Order");
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setCheckBox("FALSE,TRUE");

      f = headblk.addField("CBCURRPOSITION");
      f.setFunction("PART_SERIAL_CATALOG_API.Get_Alt_Latest_Transaction(:MCH_CODE_CONTRACT,:MCH_CODE)");
      f.setReadOnly();
      f.setSize(44);
      f.setLabel("PCMWACTIVESEPARATE3CBLATESTTRANS: Latest Transaction");
      f.setDefaultNotVisible();

      //*****************************************

      // 040309  ARWILK  Begin (Bug#112970)
      f = headblk.addField("CUST_ORDER_TYPE");
      f.setSize(11);
      f.setDynamicLOV("CUST_ORDER_TYPE",600,445);     
      f.setLabel("PCMWACTIVESEPARATE3CUST_ORDER_TYPE: Cust Order Type");
      f.setCustomValidation("CUST_ORDER_TYPE","CUSTORDERTYPEDESCRIPTION");
      f.setUpperCase();

      f = headblk.addField("CUSTORDERTYPEDESCRIPTION");
      f.setSize(20);
      f.setDefaultNotVisible();
      f.setFunction("CUST_ORDER_TYPE_API.Get_Description(:CUST_ORDER_TYPE)");
      f.setReadOnly();

      f = headblk.addField("CURRENCY_CODE");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPARATE3CURRENCY_CODE: Currency");
      f.setDynamicLOV("CURRENCY_CODE", "COMPANY", 600, 445);
      f.setUpperCase();

      f = headblk.addField("OBJ_CUST_WARRANTY");      
      f.setHidden();
      f.setUpperCase();

      f = headblk.addField("CUST_WARRANTY");
      f.setSize(20);      
      f.setHidden();
      f.setUpperCase();
      f.setReadOnly();

      f = headblk.addField("CUST_WARR_TYPE");
      f.setSize(20);
      f.setLabel("PCMWACTIVESEPARATE3WARRTYPE: Warranty Type");
      f.setReadOnly();        
      f.setUpperCase();

      f = headblk.addField("CUST_WARR_TYPE_DUMMY");
      f.setSize(20);
      f.setLabel("PCMWACTIVESEPARATE3WARRTYPE: Warranty Type");
      f.setReadOnly();
      f.setFunction("''");
      f.setUpperCase();

      f = headblk.addField("WARR_DESC");
      f.setSize(50);
      f.setLabel("PCMWACTIVESEPARATE3WARR_DESC: Warranty Description");
      f.setFunction("Cust_Warranty_Type_API.Get_Warranty_Description(:CUST_WARRANTY,:CUST_WARR_TYPE)");
      f.setReadOnly();

      f = headblk.addField("WARR_DESC_DUMMY");
      f.setSize(50);
      f.setLabel("PCMWACTIVESEPARATE3WARR_DESC: Warranty Description");
      f.setFunction("''");
      f.setReadOnly();

      f = headblk.addField("CUST_ORDER_NO");
      f.setSize(16);
      f.setLabel("PCMWACTIVESEPARATE3CUST_ORDER_NO: Customer Order Reference");
      f.setDynamicLOV("ACTIVE_SEPARATE_CUSTOMER_ORDER", "CONTRACT ORDER_CONTRACT,CUST_ORDER_TYPE ORDER_ID", 600, 445);

      f = headblk.addField("CUST_ORDER_LINE_NO");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPARATE3CUST_ORDER_LINE_NO: Line No");
      f.setReadOnly();

      f = headblk.addField("CUST_ORDER_REL_NO");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPARATE3CUST_ORDER_REL_NO: Delivery No");
      f.setReadOnly();

      f = headblk.addField("LINE_STATUS");
      f.setSize(20);
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE3LINESTA: Line Status");
      f.setFunction("substr(Customer_Order_Line_API.Get_Objstate(CUST_ORDER_NO, CUST_ORDER_LINE_NO, CUST_ORDER_REL_NO, CUST_ORDER_LINE_ITEM_NO), 1, 35)"); 

      f= headblk.addField("ORDER_STATUS");
      f.setSize(20);
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE3ORDERSTA: Order Status");
      f.setFunction("substr(Customer_Order_API.Get_Objstate(CUST_ORDER_NO), 1, 35)");  

      f = headblk.addField("CUST_ORDER_LINE_ITEM_NO","Number");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPARATE3CUST_ORDER_LINE_ITEM_NO: Line Item No");
      f.setReadOnly();

      f = headblk.addField("DELIVERYDATE","Datetime");
      f.setSize(30);
      f.setLabel("PCMWACTIVESEPARATE3DELIVERYDATE: Planned Delivery Date");
      f.setFunction("nvl(CUSTOMER_ORDER_LINE_API.GET_PLANNED_DELIVERY_DATE(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO),Customer_Order_API.Get_Wanted_Delivery_Date(:CUST_ORDER_NO))");
      f.setReadOnly();

      f = headblk.addField("CUSTLINESALESPARTNO");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPARATE3CUSTLINESALESPARTNO: Sales Part No");
      f.setFunction("CUSTOMER_ORDER_LINE_API.GET_CATALOG_NO(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO)");
      f.setReadOnly();

      f = headblk.addField("CUSTLINESALESPARTDESC");
      f.setSize(20);
      f.setDefaultNotVisible();
      f.setFunction("CUSTOMER_ORDER_LINE_API.GET_CATALOG_DESC(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO)");
      f.setReadOnly();

      f = headblk.addField("CUSTLINEINVPART");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPARATE3CUSTLINEINVPART: Inventory Part");
      f.setFunction("CUSTOMER_ORDER_LINE_API.GET_PART_NO(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO)");
      f.setReadOnly();

      f = headblk.addField("CUSTLINEINVPARTDESC");
      f.setSize(20);
      f.setDefaultNotVisible();
      f.setFunction("CUSTOMER_ORDER_LINE_API.GET_PART_DESCRIPTION(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO)");
      f.setReadOnly();

      f = headblk.addField("BELONGSTOSITEAFTERDELIVERY");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPARATE3BELONGSTOSITEAFTERDELIVERY: Site");
      f.setFunction("CUSTOMER_ORDER_LINE_API.GET_SUP_SM_CONTRACT(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO)");
      f.setReadOnly();

      f = headblk.addField("BELONGSTOOBJECTAFTERDELIVERY");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPARATE3BELONGSTOOBJECTAFTERDELIVERY: Object");
      f.setFunction("CUSTOMER_ORDER_LINE_API.GET_SUP_SM_OBJECT(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO)");
      f.setReadOnly();

      f = headblk.addField("OBJECTAFTERDELIVERYDESC");
      f.setSize(20);
      f.setDefaultNotVisible();
      f.setFunction("MAINTENANCE_OBJECT_API.GET_MCH_NAME(CUSTOMER_ORDER_LINE_API.GET_SUP_SM_CONTRACT(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO),CUSTOMER_ORDER_LINE_API.GET_SUP_SM_OBJECT(:CUST_ORDER_NO,:CUST_ORDER_LINE_NO,:CUST_ORDER_REL_NO,:CUST_ORDER_LINE_ITEM_NO))");
      f.setReadOnly();
      // 040309  ARWILK  End (Bug#112970)

      //********** for Contact Centre *********

      f = headblk.addField("CASE_ID");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("WORK_ORDER_OID");
      f.setFunction("''");
      f.setHidden();

      //***************************************

      headblk.addField("TRANSFERRED").
      setFunction("''").
      setHidden();

      headblk.addField("OBSTATE").
      setFunction("''").
      setHidden(); 

      f = headblk.addField("COND_CODE_USAGE");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("COND_CODE");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("COND_CODE_DESC");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("LOT_BATCH_NO");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("SERIAL_NO");
      f.setHidden();

      f = headblk.addField("PART_NO");
      f.setHidden();

      f = headblk.addField("PART_DESC");
      f.setFunction("''");
      f.setHidden();    

      f = headblk.addField("PART_REV");
      f.setFunction("''");
      f.setHidden();    

      f = headblk.addField("AGRMNT_PART_NO");
      f.setFunction("''");
      f.setHidden();    

      headblk.addField("RECEIVE_ORDER_NO").
      setHidden();

      headblk.addField("IDENTITY_TYPE").
      setFunction("''").    
      setHidden();        

      headblk.addField("PARTY_TYPE").
      setFunction("''").    
      setHidden();     

      headblk.addField("VEHICLE").
      setFunction("''").    
      setHidden();

      headblk.addField("VEHICLEID").
      setFunction("''").    
      setHidden();

//      Bug 69047, Start
      headblk.addField("SETTING_TIME", "Number").
      setFunction("''"); 
//      Bug 69047, End

      f = headblk.addField("EXCEPTION_EXISTS");
      //attaches the function call only if the PROJ is installed.
      if (mgr.isModuleInstalled("PROJ"))
         f.setFunction("Activity_API.Object_Exceptions_Exist('ASWO',:WO_NO,NULL,NULL)");
      else
         f.setFunction("''");
      f.setHidden();

      f = headblk.addField("TEST_POINT_ID");
      f.setHidden();

      f = headblk.addField("PP_EXISTS"); 
      f.setHidden();  
      f.setFunction("''");

      // Bug Id 70921, Start
      f = headblk.addField("IS_VALID");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("EXIST_LINE");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("DUMMY_CONTRACT_ID");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("DUMMY_REQ_START_DATE","Datetime");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("DUMMY_REQ_END_DATE","Datetime");
      f.setFunction("''");
      f.setHidden();

      // Bug Id 70921, End

      headblk.setView("ACTIVE_SEPARATE");
      headblk.defineCommand("ACTIVE_SEPARATE_API","New__,Modify__,Remove__,RE_INIT__,CONFIRM__,TO_PREPARE__,RELEASE__,REPLAN__,RESTART__,START_ORDER__,PREPARE__,WORK__,REPORT__,FINISH__,CANCEL__");
      headset = headblk.getASPRowSet();

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("PCMWACTIVESEPARATE3SERREQ: Service Request"));

      headtbl.setWrap();

      //Web Alignment - enable multirow action
      headtbl.enableRowSelect();
      //

      headbar = mgr.newASPCommandBar(headblk);

      headbar.enableRowStatus();
      if (mgr.isPresentationObjectInstalled("equipw/WorkOrderCustomer.page"))
         headbar.addCustomCommand("objectForCustomer",mgr.translate("PCMWACTIVESEPARATE3OBJDSFORCUSTT: Objects for Customer..."));
      if (mgr.isPresentationObjectInstalled("equipw/MaintenanceObject4.page"))
         headbar.addCustomCommand("customforobj",mgr.translate("PCMWACTIVESEPARATE3CUSTFOROBJE: Customers for Object..."));
      headbar.addCustomCommand("customerAddress",mgr.translate("PCMWACTIVESEPARATE3CUSTADDRESSES: Addresses for Customer..."));

      //Web Alignment - add command valid conditions
      if (mgr.isPresentationObjectInstalled("equipw/MaintenanceObject4.page"))
      {
         headbar.addCommandValidConditions("customforobj",   "MCH_CODE",  "Disable",  "null");
//          Bug 72007, Start
         headbar.appendCommandValidConditions("customforobj",   "CONNECTION_TYPE_DB",  "Disable",  "VIM;CRO");
//          Bug 72007, End
      }
      if (mgr.isPresentationObjectInstalled("equipw/WorkOrderCustomer.page"))
      {
         headbar.addCommandValidConditions("objectForCustomer",   "CUSTOMER_NO",  "Disable",  "null");
//          Bug 72007, Start
         headbar.appendCommandValidConditions("objectForCustomer",   "CONNECTION_TYPE_DB",  "Disable",  "VIM;CRO");
//          Bug 72007, End
      }
      //

      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("newFuncObject",mgr.translate("PCMWACTIVESEPARATE3NEWFUNC: Create New Functional Object..."));
      headbar.addCustomCommand("newSerialObject",mgr.translate("PCMWACTIVESEPARATE3NEWSERIAL: Create New Serial Object..."));
      headbar.addCustomCommandSeparator(); 
      headbar.addCustomCommand("copy",mgr.translate("PCMWACTIVESEPARATE3COPY: Copy..."));
      headbar.addCustomCommand("PrepareWork",mgr.translate("PCMWACTIVESEPARATE3PREPARE: Prepare..."));
      headbar.addCustomCommand("reportIn",mgr.translate("PCMWACTIVESEPARATE3REPIN: Report In..."));
      headbar.addCustomCommand("preposting",mgr.translate("PCMWACTIVESEPARATE3PREPOST: Preposting..."));
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("changeConditionCode",mgr.translate("PCMWACTIVESEPARATE2CHANGECONDITION: Change of Condition Code..."));
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("printProposal",mgr.translate("PCMWACTIVESEPARATE3PRINT: Print Service Order"));
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("reinit",mgr.translate("PCMWACTIVESEPARATE3FAULTWORK: FaultReprt\\WorkRequest"));
      headbar.addCustomCommand("observed",mgr.translate("PCMWACTIVESEPARATE3OBSRVED: Observed"));
      headbar.addCustomCommand("underPrep",mgr.translate("PCMWACTIVESEPARATE3UNDPREP: Under Preparation"));
      headbar.addCustomCommand("prepared",mgr.translate("PCMWACTIVESEPARATE3PREPARED: Prepared"));
      headbar.addCustomCommand("released",mgr.translate("PCMWACTIVESEPARATE3RELEASED: Released"));
      headbar.addCustomCommand("started",mgr.translate("PCMWACTIVESEPARATE3STARTED: Started"));
      headbar.addCustomCommand("workDone",mgr.translate("PCMWACTIVESEPARATE3WRKDONE: Work Done"));
      headbar.addCustomCommand("reported",mgr.translate("PCMWACTIVESEPARATE3REPORTED: Reported"));
      headbar.addCustomCommand("finished",mgr.translate("PCMWACTIVESEPARATE3FINISHED: Finished"));
      headbar.addCustomCommand("cancelled",mgr.translate("PCMWACTIVESEPARATE3CANCLLED: Cancelled"));

      headbar.addCustomCommandSeparator();

      if (mgr.isPresentationObjectInstalled("enterw/CustomerInfo.page"))
         headbar.addCustomCommand("custInfo",mgr.translate("PCMWACTIVESEPARATE3CUSTINFO: Customer..."));

      if (mgr.isPresentationObjectInstalled("orderw/CustomerAgreement.page"))
         headbar.addCustomCommand("custAgreement",mgr.translate("PCMWACTIVESEPARATE3CUSTAGREEMENT: Customer Agreement..."));

      //Bug 68947, Start   
      headbar.addCustomCommand("srvConLineSearch",mgr.translate("PCMWACTIVESEPARATE3SRVCONLINE: Service Contract Line Search..."));
      //Bug 68947, End   
      headbar.addCustomCommand("actWoOrd",mgr.translate("PCMWACTIVESEPARATE3WORKORD: Work Orders for Object..."));
      headbar.addCustomCommand("deptResources",mgr.translate("PCMWACTIVESEPARATE3DEPTRES: Maintenance Organization Resources..."));
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("freeTimeSearch",mgr.translate("PCMWACTIVESEPARATE3SMFREETIMESEARCH: Search Engine..."));
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("mroObjReceiveO",mgr.translate("PCMWACTIVESEPARATE2MRECEIVEORDER: Create Receive Order...")); 
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("createQuot",mgr.translate("PCMWACTIVESEPARATE3CREWOQUOT: Create Quotation..."));
      headbar.addCustomCommand("prepWoQuot",mgr.translate("PCMWACTIVESEPARATE3PREPWOQUOT: Prepare Work Order Quotation..."));
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("grapicalObject",mgr.translate("PCMWACTIVESEPARATE3GRASTRUC: Graphical Object Structure ..."));
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("custWarr",mgr.translate("PCMWACTIVESEPARATE3CUSTWARRTYPE: Customer Warranty Type..."));

      // 031216  ARWILK  Begin  (Move RMB's into Groups)
      headbar.addCustomCommandGroup("CREATENEWOBJ",mgr.translate("PCMWACTIVESEPARATE3CRTNEWOBJ: Create New Object"));
      headbar.setCustomCommandGroup("newFuncObject","CREATENEWOBJ");
      headbar.setCustomCommandGroup("newSerialObject","CREATENEWOBJ");
      // 031216  ARWILK  End  (Move RMB's into Groups)

      headbar.addCustomCommandGroup("WOSTATUS",mgr.translate("PCMWACTIVESEPARATE3WOSTAT: Work Order Status"));
      headbar.setCustomCommandGroup("reinit", "WOSTATUS");
      headbar.setCustomCommandGroup("observed","WOSTATUS");
      headbar.setCustomCommandGroup("underPrep","WOSTATUS");
      headbar.setCustomCommandGroup("prepared","WOSTATUS");
      headbar.setCustomCommandGroup("released","WOSTATUS");
      headbar.setCustomCommandGroup("started","WOSTATUS");
      headbar.setCustomCommandGroup("workDone","WOSTATUS");
      headbar.setCustomCommandGroup("reported","WOSTATUS");
      headbar.setCustomCommandGroup("finished","WOSTATUS");
      headbar.setCustomCommandGroup("cancelled","WOSTATUS");

      //add the jobs tab
      headbar.addCustomCommand("activateBudgets", "");
      headbar.addCustomCommand("activateJobs", "");


      headbar.defineCommand(headbar.SAVERETURN,"saveReturn","checkMando()");
      headbar.defineCommand(headbar.SAVENEW,"saveNew","checkMando()");
      headbar.defineCommand(headbar.CANCELEDIT,"cancelEdit");
      headbar.defineCommand("prepared","prepared","preparedClient(i)");

      //Web Alignment - enable Multirow Layout
      headbar.enableMultirowAction();

      headbar.addCommandValidConditions("reinit",   "OBJSTATE",  "Enable",  "OBSERVED");
      headbar.addCommandValidConditions("observed",   "OBJSTATE",  "Enable",  "FAULTREPORT;WORKREQUEST;UNDERPREPARATION");
      headbar.addCommandValidConditions("underPrep",  "OBJSTATE",  "Enable",  "FAULTREPORT;WORKREQUEST;OBSERVED;PREPARED");
      headbar.addCommandValidConditions("prepared",   "OBJSTATE",  "Enable",  "FAULTREPORT;WORKREQUEST;OBSERVED;UNDERPREPARATION;RELEASED");
      headbar.addCommandValidConditions("released",   "OBJSTATE",  "Enable",  "FAULTREPORT;WORKREQUEST;OBSERVED;UNDERPREPARATION;PREPARED;STARTED");
      headbar.addCommandValidConditions("started",    "OBJSTATE",  "Enable",  "FAULTREPORT;WORKREQUEST;OBSERVED;UNDERPREPARATION;PREPARED;RELEASED;WORKDONE");
      headbar.addCommandValidConditions("workDone",   "OBJSTATE",  "Enable",  "FAULTREPORT;WORKREQUEST;OBSERVED;UNDERPREPARATION;PREPARED;RELEASED;STARTED");
      headbar.addCommandValidConditions("reported",   "OBJSTATE",  "Enable",  "FAULTREPORT;WORKREQUEST;OBSERVED;UNDERPREPARATION;PREPARED;RELEASED;STARTED;WORKDONE");
      headbar.addCommandValidConditions("finished",   "OBJSTATE",  "Enable",  "FAULTREPORT;WORKREQUEST;OBSERVED;UNDERPREPARATION;PREPARED;RELEASED;STARTED;WORKDONE;REPORTED");
      headbar.addCommandValidConditions("cancelled",  "OBJSTATE",  "Enable",  "FAULTREPORT;WORKREQUEST;OBSERVED;UNDERPREPARATION;PREPARED;RELEASED;STARTED;WORKDONE;REPORTED");

      // 040312  ARWILK  Begin (Bug#113080)
      headbar.addCommandValidConditions("newSerialObject",  "CUSTOMER_NO",  "Disable",  "null");
      // 040312  ARWILK  End (Bug#113080)
      headbar.addCommandValidConditions("customerAddress", "CUSTOMER_NO", "Disable", "null");

      if (mgr.isPresentationObjectInstalled("equipw/WorkOrderCustomer.page"))
         headbar.removeFromMultirowAction("objectForCustomer");
      if (mgr.isPresentationObjectInstalled("equipw/MaintenanceObject4.page"))
         headbar.removeFromMultirowAction("customforobj");
      headbar.removeFromMultirowAction("preposting");
      headbar.removeFromMultirowAction("changeConditionCode");

      if (mgr.isPresentationObjectInstalled("enterw/CustomerInfo.page"))
         headbar.removeFromMultirowAction("custInfo");

      if (mgr.isPresentationObjectInstalled("orderw/CustomerAgreement.page"))
         headbar.removeFromMultirowAction("custAgreement");

      //Bug 68947, Start   
      headbar.removeFromMultirowAction("srvConLineSearch");
      //Bug 68947, End   
      headbar.removeFromMultirowAction("actWoOrd");
      headbar.removeFromMultirowAction("deptResources");
      headbar.removeFromMultirowAction("mroObjReceiveO");
      headbar.removeFromMultirowAction("createQuot");
      headbar.removeFromMultirowAction("prepWoQuot");
      headbar.removeFromMultirowAction("grapicalObject");
      headbar.removeFromMultirowAction("custWarr");
      headbar.removeFromMultirowAction("copy");
      headbar.removeFromMultirowAction("freeTimeSearch");
      //

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      headlay.setDialogColumns(2);

      headlay.defineGroup("","WO_NO,REG_DATE,REPORTED_BY,STATE,CONTRACT",false,true);
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE3GRPLABEL1: Contact Information"),"CONTACT,PHONE_NO,ERR_DESCR,WORK_TYPE_ID,WORKTYPEDESCRIPTION,REFERENCE_NO", true, true);
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE3GRPLABEL2: Customer"),"CUSTOMER_NO,CUSTOMERDESCRIPTION,CONTRACT_ID,CONTRACT_NAME,LINE_NO,LINE_DESC,CONTRACT_TYPE,INVOICE_TYPE,WARRANTY_ROW_NO,CUSTOMERAGREEMENTDESCRIPTION,AGREEMENT_ID", true, true);
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE3GRPLABEL3: Planning Schedule"),"PLAN_S_DATE,REQUIRED_START_DATE,PLAN_F_DATE,REQUIRED_END_DATE,PLAN_HRS", true, true);   
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE3GRPLABEL4: Object"),"CONNECTION_TYPE,MCH_CODE,MCH_CODE_DESCRIPTION,MCH_CODE_CONTRACT,CBHASACTIVEWO,CBHASAGREEMENT,CBCURRPOSITION", true, true);
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE3GRPLABEL9: Object Warranty"),"CBHASCUSTWARRANTYONOBJ,CBHASWARRANTYONOBJ,", true, true); //CALL
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE3GRPLABEL8: Work Order Address"),"ADDRESS1,ADDRESS2,ADDRESS3,ADDRESS4,ADDRESS5,ADDRESS6", true, true);
      // 031003  ARWILK  Begin (Bug#104743)
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE3GRPLABEL5: Planning Information"),"ORG_CODE,ORGCODEDESCRIPTION,SETTINGTIME,PRIORITY_ID,PRIORITYDESCRIPTION,WORK_DESCR_LO", true, true);
      // 031003  ARWILK  End (Bug#104743)
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE3GRPLABEL6: More Information about the Fault"),"ERR_DISCOVER_CODE,DISCOVERDESCRIPTION,ERR_SYMPTOM,SYMPTOMDESCRIPTION,ERR_DESCR_LO", true, true);
      // 107820
      //headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE3GRPLABEL7: Work Order"),"CBHASWARRANTYONOBJ,REPAIR_FLAG,CBTRANSFEREDTOMOB,CBWARRWO",true,false);
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE3GRPLABEL7: Work Order"),"REPAIR_FLAG,FIXED_PRICE_DB", true, true);  //CALL
      headlay.defineGroup(mgr.translate("GRPLABEL8: Signatures"),"WORK_LEADER_SIGN,WORK_MASTER_SIGN,AUTHORIZE_CODE", true, true);
      // 040309  ARWILK  Begin (Bug#112970)
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE3ORDINVOINF: Order/Invoice Information"),"CUST_ORDER_TYPE,CUSTORDERTYPEDESCRIPTION,CURRENCY_CODE,CUST_WARR_TYPE,CUST_WARR_TYPE_DUMMY,WARR_DESC,WARR_DESC_DUMMY,CBMULCUST,CBWARRWO",true,true);
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE3AFTSALEINF: After Sales Information"),"CUST_ORDER_NO,DELIVERYDATE,CUST_ORDER_LINE_NO,CUST_ORDER_REL_NO,CUST_ORDER_LINE_ITEM_NO,CUSTLINESALESPARTNO,CUSTLINESALESPARTDESC,ORDER_STATUS,CUSTLINEINVPART,CUSTLINEINVPARTDESC,LINE_STATUS",true,true);
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE3BELONGTOOBJ: Belongs to After Delivery"),"BELONGSTOOBJECTAFTERDELIVERY,OBJECTAFTERDELIVERYDESC,BELONGSTOSITEAFTERDELIVERY",true,true);
      // 040309  ARWILK  End (Bug#112970)

      headlay.setAddressFieldList(fADDRESS1,fADDRESS2,fZIP_CODE,fCITY,fSTATE,fCOUNTY,fCOUNTRY_CODE,fCOUNTRY,"PCMWWORKORDERQUATATION: Address","ifs.pcmw.LocalizedPcmwAddress"); 

      headlay.setSimple("WORKTYPEDESCRIPTION");
      headlay.setSimple("CUSTOMERDESCRIPTION");
      headlay.setSimple("CUSTOMERAGREEMENTDESCRIPTION");
      headlay.setSimple("ORGCODEDESCRIPTION");
      headlay.setSimple("PRIORITYDESCRIPTION");
      headlay.setSimple("DISCOVERDESCRIPTION");
      headlay.setSimple("SYMPTOMDESCRIPTION");
      headlay.setSimple("MCH_CODE_DESCRIPTION");
      // 040309  ARWILK  Begin (Bug#112970)
      headlay.setSimple("CUSTORDERTYPEDESCRIPTION");
      headlay.setSimple("WARR_DESC");
      headlay.setSimple("WARR_DESC_DUMMY");
      headlay.setSimple("CUSTLINESALESPARTDESC");
      headlay.setSimple("CUSTLINEINVPARTDESC");
      headlay.setSimple("OBJECTAFTERDELIVERYDESC");
      // 040309  ARWILK  End (Bug#112970)

      headlay.setSimple("CONTRACT_NAME");
      headlay.setSimple("LINE_DESC");

      //add the jobs tab
      tabs = mgr.newASPTabContainer();
      tabs.addTab(mgr.translate("PCMWACTIVESEPARATE3BUDTAB: Budgets"), "javascript:commandSet('HEAD.activateBudgets','')");
      tabs.addTab(mgr.translate("PCMWACTIVESEPARATE3JOBSTAB: Jobs"), "javascript:commandSet('HEAD.activateJobs','')");

      //-----------------------------------------------------------------------
      //---------- This block used to update RMB list box in Header------------
      //-----------------------------------------------------------------------

      eventblk = mgr.newASPBlock("EVNTBLK");

      f = eventblk.addField("DEF_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = eventblk.addField("DEF_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = eventblk.addField("EVNTBLK_OBJEVENTS"); 
      f.setDbName("OBJEVENTS");   
      f.setHidden();

      f = eventblk.addField("EVNTBLK_WO_NO");
      f.setDbName("WO_NO");
      f.setHidden();

      f = eventblk.addField("EVNTBLK_CONTRACT");
      f.setHidden();
      f.setDbName("CONTRACT");

      f = eventblk.addField("ISFAULTREPAGREE");
      f.setFunction("''");
      f.setHidden();

      f = eventblk.addField("ISCLIENTFAUREPAGREE"); 
      f.setFunction("''");
      f.setHidden();

      f = eventblk.addField("TOFUNCT","Number"); 
      f.setFunction("''");
      f.setHidden();  

      f = eventblk.addField("ISSTDJOBID"); 
      f.setFunction("''");
      f.setHidden(); 

      f = eventblk.addField("ISSYMPTDESC");   
      f.setFunction("''");
      f.setHidden();

      f = eventblk.addField("ISEXECUTETIME","Number"); 
      f.setFunction("''");
      f.setHidden();

      f = eventblk.addField("ISAUTHRIZECODE");   
      f.setFunction("''");
      f.setHidden();

      /*f = eventblk.addField("ISSTDJODESC");   
      f.setFunction("''");
      f.setHidden();*/

      f = eventblk.addField("VALSYDATE","Datetime");   
      f.setFunction("''");
      f.setHidden();

      f = eventblk.addField("ISCUSTCREDIRSTOP","Number"); 
      f.setFunction("''");
      f.setHidden();

      f = eventblk.addField("ISHASAGREEMENT");   
      f.setFunction("''");
      f.setHidden();

      f = eventblk.addField("ISHASACTIVEWORKORDER");   
      f.setFunction("''");
      f.setHidden();  

//		  Bug 68362, Start        
      f = eventblk.addField("ISCATOBJ");
      f.setHidden();
      f.setFunction("''");
//		  Bug 68362, End        

      eventblk.setView("ACTIVE_SEPARATE");
      eventset = eventblk.getASPRowSet();

      //----------------------------------------------------------------
      //---------------- define pre post--------------------------------

      ref1 =  mgr.newASPBlock("REF1");
      f= ref1.addField("COMP");
      f = ref1.addField("STR_CODE");
      f = ref1.addField("CAL_ID");
      f = ref1.addField("WORK_DAY");

      blkPost = mgr.newASPBlock("POSTI");
      f = blkPost.addField("CODE_A","Number");
      f = blkPost.addField("CODE_B","Number");
      f=blkPost.addField("CONTROL_TYPE");


      //----------------------------------------------------------------------
      //---------------- define Budget--------------------------------
      //----------------------------------------------------------------------


      itemblk1 = mgr.newASPBlock("ITEM1");

      f = itemblk1.addField("ITEM1_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk1.addField("ITEM1_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk1.addField("ITEM1_WO_NO","Number");
      f.setSize(8);
      f.setHidden();
      f.setDbName("WO_NO");
      f.setLabel("PCMWACTIVESEPARATE3WO_NO: WO No");

      f = itemblk1.addField("WORK_ORDER_COST_TYPE");
      f.setSize(25);
      f.setMandatory();
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE3WORK_ORDER_COST_TYPE: Work Order Cost Type");

      f = itemblk1.addField("BUDGET_COST","Number");
      f.setSize(25);
      f.setLabel("PCMWACTIVESEPARATE3BUDGET_COST: Budget Cost");
      f.setCustomValidation("BUDGET_COST","BUDGET_COST");   

      f = itemblk1.addField("BUDGET_REVENUE","Number");
      f.setSize(25);
      f.setLabel("PCMWACTIVESEPARATE3BUDGET_REVENUE: Budget Revenue");
      f.setCustomValidation("BUDGET_REVENUE","BUDGET_REVENUE");

      f = itemblk1.addField("NBUDGETMARGIN", "Number");
      f.setSize(25);
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE3NBUDGETMARGIN: Budget Margin");
      f.setFunction("''");

      f = itemblk1.addField("NPLANNEDCOST","Number");
      f.setSize(25);
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE3NPLANNEDCOST: Planned Cost");
      f.setFunction("WORK_ORDER_BUDGET_API.Get_Planned_Cost(:WO_NO,:WORK_ORDER_COST_TYPE)");

      f = itemblk1.addField("NPLANNEDREVENUE","Number");
      f.setSize(25);
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE3NPLANNEDREVENUE: Planned Revenue");
      f.setFunction("WORK_ORDER_BUDGET_API.Get_Planned_Revenue(:WO_NO,:WORK_ORDER_COST_TYPE)");

      f = itemblk1.addField("NPLANNEDMARGIN", "Number");
      f.setSize(25);
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE3NPLANNEDMARGIN: Planned Margin");
      f.setFunction("''");

      f = itemblk1.addField("NACTUALCOST","Number");
      f.setSize(25);
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE3NACTUALCOST: Actual Cost");
      f.setFunction("WORK_ORDER_BUDGET_API.Get_Actual_Cost(:WO_NO,:WORK_ORDER_COST_TYPE)");

      f = itemblk1.addField("NACTUALREVENUE", "Number");
      f.setSize(25);
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE3NACTUALREVENUE: Actual Revenue");
      f.setFunction("''");

      f = itemblk1.addField("NACTUALMARGIN", "Number");
      f.setSize(25);
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE3NACTUALMARGIN: Actual Margin");
      f.setFunction("''");

      itemblk1.setView("WORK_ORDER_BUDGET");
      itemblk1.defineCommand("WORK_ORDER_BUDGET_API","New__,Modify__,Remove__");
      //itemblk1.setTitle("PCMWACTIVESEPARATE3BUDTITLE: Budget");

      itemset1 = itemblk1.getASPRowSet();
      itemblk1.setMasterBlock(headblk);

      itembar1 = mgr.newASPCommandBar(itemblk1);
      itembar1.disableCommand(itembar1.DELETE);
      itembar1.disableCommand(itembar1.NEWROW);
      itembar1.disableCommand(itembar1.DUPLICATEROW);   
      itembar1.defineCommand(itembar1.EDITROW,"editItem1");
      itembar1.defineCommand(itembar1.VIEWDETAILS,"viewDetailsITEM1");
      itembar1.defineCommand(itembar1.SAVERETURN,"saveReturnITEM1","checkItem1Fields(-1)");
      itembar1.defineCommand(itembar1.FORWARD,"forwardITEM1");
      itembar1.defineCommand(itembar1.BACKWARD,"backwardITEM1");

      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setTitle(mgr.translate("ACTSEP3ITEM1WOBUDGETS: Budgets"));
      itemtbl1.setWrap();

      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);
      itemlay1.setDialogColumns(3);




      // ----------------------------------------------------------------------
      // -----------------------------    ITEMBLK2   --------------------------
      // ----------------------------------------------------------------------

      itemblk2 = mgr.newASPBlock("ITEM2");

      itemblk2.addField("ITEM2_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk2.addField("ITEM2_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      itemblk2.addField("ITEM2_WO_NO", "Number", "#").
      setDbName("WO_NO").
      setHidden().
      setReadOnly().
      setInsertable().
      setMandatory();

      itemblk2.addField("JOB_ID", "Number").
      setLabel("ACTSEP3ITEM2JOBID: Job ID").
      setReadOnly().
      setInsertable().
      setMandatory();

      itemblk2.addField("STD_JOB_ID").
      setSize(15).
      setLabel("ACTSEP3ITEM2STDJOBID: Standard Job ID").
      setLOV("SeparateStandardJobLov.page", 600, 445).
      setUpperCase().
      setInsertable().
      setQueryable().
      setCustomValidation("STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION", "STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION,DESCRIPTION").
      setMaxLength(12);

      itemblk2.addField("STD_JOB_CONTRACT").
      setSize(10).
      setLabel("ACTSEP3ITEM2STDJOBCONTRACT: Site").
      setUpperCase().
      setReadOnly().
      setMaxLength(5);

      itemblk2.addField("STD_JOB_REVISION").
      setSize(10).
      setLabel("ACTSEP3ITEM2STDJOBREVISION: Revision").
      setDynamicLOV("SEPARATE_STANDARD_JOB_LOV", "STD_JOB_CONTRACT CONTRACT,STD_JOB_ID", 600, 445).    
      setCustomValidation("STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION", "DESCRIPTION").
      setUpperCase().
      setInsertable().
      setQueryable().
      setMaxLength(6);

      itemblk2.addField("DESCRIPTION").
      setSize(35).
      setLabel("ACTSEP3ITEM2WORKDESC: Work Description").
      setUpperCase().
      setMandatory().
      setInsertable().
      setMaxLength(4000);

      itemblk2.addField("ITEM2_QTY", "Number").
      setLabel("ACTSEP3ITEM2QTY: Quantity").
      setDbName("QTY").
      setMandatory().
      setInsertable();

      itemblk2.addField("ITEM2_COMPANY").
      setSize(20).
      setHidden().
      setDbName("COMPANY").
      setUpperCase().
      setInsertable();

      itemblk2.addField("EMPLOYEE_ID").
      setSize(15).
      setLabel("ACTSEP3ITEM2EMPLOYEEID: Employee ID").
      setDynamicLOV("EMPLOYEE_NO", "COMPANY", 600, 445).    
      setUpperCase().
      setInsertable().
      setQueryable().
      setMaxLength(11);

      itemblk2.addField("ITEM2_SIGN_ID").
      setSize(35).
      setDbName("SIGNATURE").
      setLabel("ACTSEP3ITEM2SIGNID: Signature").
      setQueryable().
      //setFunction("Company_Emp_API.Get_Person_Id(:ITEM2_COMPANY, :EMPLOYEE_ID)").
      setReadOnly();
      mgr.getASPField("EMPLOYEE_ID").setValidation("Company_Emp_API.Get_Person_Id", "COMPANY,EMPLOYEE_ID", "ITEM2_SIGN_ID");

      //(+) Bug 66406, Start
      itemblk2.addField("CONN_PM_NO", "Number" ,"#").
      setDbName("PM_NO").
      setSize(15).
      setReadOnly().
      setCustomValidation("CONN_PM_NO,CONN_PM_REVISION","CONN_PM_NO,CONN_PM_REVISION").
      setLabel("ACTSEPSERREQCONNPMNO: PM No");

      itemblk2.addField("CONN_PM_REVISION").
      setDbName("PM_REVISION").
      setSize(15).
      setReadOnly().
      setLabel("ACTSEPSERREQCONNPMREV: PM Revision");

      itemblk2.addField("CONN_PM_JOB_ID", "Number").
      setDbName("PM_JOB_ID"). 
      setSize(15).
      setReadOnly().
      setDynamicLOV("PM_ACTION_JOB", "CONN_PM_NO PM_NO, CONN_PM_REVISION PM_REVISION").
      setLabel("ACTSEPSERREQCONNPMJOBID: PM Job ID");
      //(+) Bug 66406, End

      itemblk2.addField("ITEM2_DATE_FROM", "Datetime").
      setSize(20).
      setDbName("DATE_FROM").
      setLabel("ACTSEP3ITEM2DATEFROM: Date From").
      setInsertable();

      itemblk2.addField("ITEM2_DATE_TO", "Datetime").
      setSize(20).
      setDbName("DATE_TO").
      setLabel("ACTSEP3ITEM2DATETO: Date To").
      setInsertable();

      itemblk2.addField("STD_JOB_FLAG", "Number").
      setHidden().
      setCustomValidation("ITEM2_WO_NO,JOB_ID,STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION", "N_JOB_EXIST,S_STD_JOB_EXIST,N_ROLE_EXIST,N_MAT_EXIST,N_TOOL_EXIST,N_PLANNING_EXIST,N_DOC_EXIST,S_STD_JOB_ID,S_STD_JOB_CONTRACT,S_STD_JOB_REVISION,N_QTY,S_AGREEMENT_ID").
      setInsertable();

      itemblk2.addField("KEEP_CONNECTIONS").
      setHidden().
      setSize(3).
      setInsertable();

      itemblk2.addField("RECONNECT").
      setHidden().
      setSize(3).
      setInsertable();

      // -----------------------------------------------------------------------
      // -----------------------  Hidden Fields --------------------------------
      // -----------------------------------------------------------------------

      itemblk2.addField("N_JOB_EXIST", "Number").
      setFunction("0").
      setHidden();

      itemblk2.addField("S_STD_JOB_EXIST").
      setFunction("''").
      setHidden();

      itemblk2.addField("N_ROLE_EXIST", "Number").
      setFunction("0").
      setHidden();

      itemblk2.addField("N_MAT_EXIST", "Number").
      setFunction("0").
      setHidden();

      itemblk2.addField("N_TOOL_EXIST", "Number").
      setFunction("0").
      setHidden();

      itemblk2.addField("N_PLANNING_EXIST", "Number").
      setFunction("0").
      setHidden();

      itemblk2.addField("N_DOC_EXIST", "Number").
      setFunction("0").
      setHidden();

      itemblk2.addField("S_STD_JOB_ID").
      setFunction("''").
      setHidden();

      itemblk2.addField("S_STD_JOB_CONTRACT").
      setFunction("''").
      setHidden();

      itemblk2.addField("S_STD_JOB_REVISION").
      setFunction("''").
      setHidden();

      itemblk2.addField("N_QTY", "Number").
      setFunction("0").
      setHidden();

      /*itemblk2.addField("S_IS_SEPARATE").
      setFunction("''").
      setHidden();*/

      itemblk2.addField("S_AGREEMENT_ID").
      setFunction("''").
      setHidden();

      itemblk2.setView("WORK_ORDER_JOB");
      itemblk2.defineCommand("WORK_ORDER_JOB_API","New__,Modify__,Remove__");
      itemblk2.setMasterBlock(headblk);

      itemset2 = itemblk2.getASPRowSet();

      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.setTitle(mgr.translate("ACTSEP3ITEM2WOJOBS: Jobs"));
      itemtbl2.setWrap();

      itembar2 = mgr.newASPCommandBar(itemblk2);
      itembar2.enableCommand(itembar2.FIND);

      itembar2.defineCommand(itembar2.NEWROW, "newRowITEM2");
      itembar2.defineCommand(itembar2.SAVERETURN, "saveReturnItem2", "checkITEM2SaveParams(i)");
      itembar2.defineCommand(itembar2.DELETE,"deleteRowITEM2");

      itemlay2 = itemblk2.getASPBlockLayout();
      itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);


   }

   public boolean checkMROObjRO()
   {
      ASPManager mgr = getASPManager();

      if ("VIM".equals(headset.getRow().getValue("CONNECTION_TYPE_DB")) && !mgr.isEmpty(headset.getRow().getValue("MCH_CODE")))
      {
         trans.clear();

         cmd = trans.addCustomFunction("GETRONO","ACTIVE_WORK_ORDER_API.Get_Receive_Order_No","RECEIVE_ORDER_NO");
         cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

         cmd = trans.addCustomFunction("GETCOMP","Site_API.Get_Company","COMPANY");
         cmd.addParameter("CONTRACT",headset.getValue("CONTRACT"));                

         cmd = trans.addCustomFunction("GETPARTYTYPE","PARTY_TYPE_API.Get_Client_Value(1)","PARTY_TYPE");

         cmd = trans.addCustomFunction("GETIDTYPE","IDENTITY_INVOICE_INFO_API.Get_Identity_Type","IDENTITY_TYPE");
         cmd.addReference("COMPANY","GETCOMP/DATA");
         cmd.addParameter("CUSTOMER_NO",headset.getValue("CUSTOMER_NO"));
         cmd.addReference("PARTY_TYPE","GETPARTYTYPE/DATA");

         cmd = trans.addCustomCommand("GETVIMINFO","ACTIVE_SEPARATE_API.Separate_Mro_Part_Serial");
         cmd.addParameter("PART_NO");
         cmd.addParameter("SERIAL_NO");
         cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

         trans = mgr.perform(trans);

         String roNo = trans.getValue("GETRONO/DATA/RECEIVE_ORDER_NO");
         String idType = trans.getValue("GETIDTYPE/DATA/IDENTITY_TYPE");
         String vimPartNo = trans.getValue("GETVIMINFO/DATA/PART_NO");
         String vimSerialNo = trans.getValue("GETVIMINFO/DATA/SERIAL_NO");

         if (mgr.isEmpty(roNo) && "EXTERN".equals(idType) && !mgr.isEmpty(vimPartNo) && !mgr.isEmpty(vimSerialNo))
            return true;
         else
            return false;
      }
      else
         return false;
   }


   public void checkObjAvaileble()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer availObj;
      trans.clear();
      // 107820
      //trans.addSecurityQuery("WORK_ORDER_CUSTOMER,MAINTENANCE_OBJECT,MOBMGR_WRKORDER,EQUIPMENT_ALL_OBJECT,MAINTENANCE_PRE_POSTING,CUSTOMER_AGREEMENT,CUSTOMER_INFO,OBJ_CUST_WARRANTY_TYPE,MRO_WO_FROM_VIM");
      //Bug 68947, Start
      trans.addSecurityQuery("WORK_ORDER_CUSTOMER,MAINTENANCE_OBJECT,EQUIPMENT_ALL_OBJECT,PRE_ACCOUNTING,CUSTOMER_AGREEMENT,CUSTOMER_INFO,OBJ_CUST_WARRANTY_TYPE,MRO_WO_FROM_VIM,PSC_CONTR_PRODUCT");
      //Bug 68947, End
      trans.addSecurityQuery("Active_Work_Order_API","Copy__");      
      trans.addSecurityQuery("PURCH_OBJ_RECEIVE_ORD_UTIL_API","Create_Mro_Receive_Order");
      trans.addSecurityQuery("IDENTITY_INVOICE_INFO_API","Get_Identity_Type");
      // 107820
      //trans.addPresentationObjectQuery("EQUIPW/WorkOrderCustomer.page,EQUIPW/MaintenanceObject4.page,PCMW/CopyWorkOrderDlg.page,SERORW/MobmgrEmpForWoDlg.page,EQUIPW/CopyFunctionalDlg.page,EQUIPW/CopySerialDlg.page,EQUIPW/NavigatorFrameSet.page,PCMW/ActiveSeparate2ServiceManagement.page,PCMW/ActiveSeperateReportInWorkOrderSM.page,PCMW/PrepostingDlg.page,ORDERW/CustomerAgreement.page,ENTERW/CustomerInfo.page,EQUIPW/MaintenanceObject2.page,PCMW/DepartmentResources.page,PCMW/ActiveSeparateDlg2.page,PCMW/WorkOrderQuotation.page,PCMW/WarrantyTypeDlg.page");
      trans.addPresentationObjectQuery("EQUIPW/WorkOrderCustomer.page,EQUIPW/MaintenanceObject4.page,PCMW/CopyWorkOrderDlg.page,EQUIPW/CopyFunctionalDlg.page,EQUIPW/CopySerialDlg.page,EQUIPW/NavigatorFrameSet.page,PCMW/ActiveSeparate2ServiceManagement.page,PCMW/ActiveSeperateReportInWorkOrderSM.page,MPCCOW/PreAccountingDlg.page,ORDERW/CustomerAgreement.page,ENTERW/CustomerInfo.page,EQUIPW/MaintenanceObject2.page,PCMW/DepartmentResources.page,PCMW/ActiveSeparateDlg2.page,PCMW/WorkOrderQuotation.page,PCMW/WarrantyTypeDlg.page");

      trans = mgr.perform(trans);

      availObj = trans.getSecurityInfo();

      if (availObj.itemExists("WORK_ORDER_CUSTOMER") && availObj.namedItemExists("EQUIPW/WorkOrderCustomer.page"))
         objectForCustomer_ = true;

      if (availObj.itemExists("MAINTENANCE_OBJECT")&&  availObj.namedItemExists("EQUIPW/MaintenanceObject4.page"))
         customforobj_ = true;

      /* 107820
      if ( availObj.itemExists("MOBMGR_WRKORDER")  &&  availObj.namedItemExists("SERORW/MobmgrEmpForWoDlg.page") )
          transToMob_ = true;
      */

      if (availObj.namedItemExists("EQUIPW/CopyFunctionalDlg.page") && mgr.isPresentationObjectInstalled("equipw/CopyFunctionalDlg.page"))
         newFuncObject_ = true;

      if (availObj.itemExists("EQUIPMENT_ALL_OBJECT") && availObj.namedItemExists("EQUIPW/CopySerialDlg.page") && mgr.isPresentationObjectInstalled("equipw/CopySerialDlg.page"))
         newSerialObject_ = true;

      if (availObj.itemExists("EQUIPMENT_ALL_OBJECT") && availObj.namedItemExists("EQUIPW/NavigatorFrameSet.page") && mgr.isPresentationObjectInstalled("equipw/NavigatorFrameSet.page"))
         grapicalObject_ = true;

      if (availObj.namedItemExists("PCMW/ActiveSeparate2ServiceManagement.page"))
         prepareWork_ = true;

      if (availObj.namedItemExists("PCMW/ActiveSeperateReportInWorkOrderSM.page"))
         reportIn_ = true;

      if (availObj.itemExists("PRE_ACCOUNTING") && availObj.namedItemExists("MPCCOW/PreAccountingDlg.page"))
         preposting_= true;

      if (availObj.itemExists("CUSTOMER_AGREEMENT"))
         printProposal_ = true;

      if (availObj.itemExists("CUSTOMER_AGREEMENT") && availObj.namedItemExists("ORDERW/CustomerAgreement.page"))
         custAgreement_= true;

      if (availObj.itemExists("CUSTOMER_INFO") && availObj.namedItemExists("ENTERW/CustomerInfo.page"))
         custInfo_ = true;

      if (availObj.itemExists("ACTIVE_WORK_ORDER") && availObj.namedItemExists("EQUIPW/MaintenanceObject2.page") && mgr.isPresentationObjectInstalled("equipw/MaintenanceObject2.page"))
         actWoOrd_= true;

      if (availObj.namedItemExists("PCMW/DepartmentResources.page"))
         deptResources_= true;

      if (availObj.namedItemExists("PCMW/ActiveSeparateDlg2.page"))
         createQuot_ = true;

      if (availObj.namedItemExists("PCMW/WorkOrderQuotation.page"))
         prepWoQuot_ = true;

      if (availObj.itemExists("OBJ_CUST_WARRANTY_TYPE") && availObj.namedItemExists("PCMW/WarrantyTypeDlg.page"))
         custWarr_ = true;

      if (availObj.itemExists("PURCH_OBJ_RECEIVE_ORD_UTIL_API.Create_Mro_Receive_Order") && availObj.itemExists("IDENTITY_INVOICE_INFO_API.Get_Identity_Type") && availObj.itemExists("MRO_WO_FROM_VIM"))
         mroCreateRO_ = true;

      if (availObj.itemExists("Active_Work_Order_API.Copy__") && availObj.namedItemExists("PCMW/CopyWorkOrderDlg.page"))
         copy_ = true;
      //Bug 68947, End   
      if (availObj.itemExists("PSC_CONTR_PRODUCT"))
         srvCon_ = true;
      //Bug 68947, Start   
   }

   public void setCheckboxForVim()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer vimAvailObj;
      trans.clear();
      trans.addSecurityQuery("Psc_Contr_Product_API","Search_For_Agreement");
      trans = mgr.perform(trans);

      vimAvailObj = trans.getSecurityInfo();

      if (vimAvailObj.itemExists("Psc_Contr_Product_API.Search_For_Agreement"))
         vimObject = true;

      isSecure = new String[3];
      String cbagreement = "FALSE";
      String cbcust = "FALSE";
      String cbsupp = "FALSE";
      trans.clear();
      if ( vimObject )
      {
         cmd = trans.addCustomCommand("GETPARTSER","Active_Separate_API.Get_Vim_Part_Serial");
         cmd.addParameter("PART_NO");
         cmd.addParameter("SERIAL_NO");
         cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

         cmd = trans.addCustomCommand("SEARCHAGRMNT","Psc_Contr_Product_API.Search_For_Agreement");
         cmd.addParameter("CBHASAGREEMENT");
         cmd.addParameter("CONTRACT_ID");
         cmd.addParameter("LINE_NO");
         cmd.addParameter("PART_REV");
         cmd.addParameter("AGRMNT_PART_NO");
         cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
         cmd.addReference("PART_NO","GETPARTSER/DATA");
         cmd.addReference("SERIAL_NO","GETPARTSER/DATA");
         cmd.addParameter("CUSTOMER_NO",headset.getRow().getValue("CUSTOMER_NO"));
         cmd.addParameter("WORK_TYPE_ID",headset.getRow().getFieldValue("WORK_TYPE_ID"));
         cmd.addParameter("CONTRACT",headset.getRow().getFieldValue("CONTRACT"));

         cmd = trans.addCustomFunction("GETSUPWAR","Part_Serial_Catalog_API.Get_Sup_Warranty_Id","CBWARRANTYONOBJECT");
         cmd.addReference("PART_NO","GETPARTSER/DATA");
         cmd.addReference("SERIAL_NO","GETPARTSER/DATA");

         cmd = trans.addCustomFunction("GETCUSTWAR","Part_Serial_Catalog_API.Get_Cust_Warranty_Id","CBHASCUSTWARRANTYONOBJ");
         cmd.addReference("PART_NO","GETPARTSER/DATA");
         cmd.addReference("SERIAL_NO","GETPARTSER/DATA");
      }

      trans = mgr.perform(trans);

      if ( vimObject )
      {

         cbagreement = trans.getValue("SEARCHAGRMNT/DATA/CBHASAGREEMENT");
         cbcust = trans.getValue("GETCUSTWAR/DATA/CBHASCUSTWARRANTYONOBJ");
         cbsupp = trans.getValue("GETSUPWAR/DATA/CBWARRANTYONOBJECT");
      }
      else
      {

         cbagreement = "";
         cbcust = "";
         cbsupp = "";
      }

      row = headset.getRow();
      row.setValue("MCH_CODE_CONTRACT","");

      if ("TRUE".equals(cbagreement))
         row.setValue("CBHASAGREEMENT","1");
      else
         row.setValue("CBHASAGREEMENT","0");

      if ("TRUE".equals(cbcust))
         row.setValue("CBHASCUSTWARRANTYONOBJ","TRUE");
      else
         row.setValue("CBHASCUSTWARRANTYONOBJ","FALSE");

      if ("TRUE".equals(cbsupp))
         row.setValue("CBWARRANTYONOBJECT","TRUE");
      else
         row.setValue("CBWARRANTYONOBJECT","FALSE");
      row.setValue("CBHASACTIVEWO","FALSE");

      headset.setRow(row);
   }

   public void DissableRmbs()
   {
      ASPManager mgr = getASPManager();

      if (mgr.isPresentationObjectInstalled("equipw/WorkOrderCustomer.page")  && !objectForCustomer_)
         headbar.removeCustomCommand("objectForCustomer");

      if (mgr.isPresentationObjectInstalled("equipw/MaintenanceObject4.page") && !customforobj_)
         headbar.removeCustomCommand("customforobj");

      /* 107820
      if ( mgr.isPresentationObjectInstalled("SerOrw/MobmgrEmpForWoDlg.page") &&  !transToMob_ )
          headbar.removeCustomCommand("transToMob");
      */

      if (!newFuncObject_)
         headbar.removeCustomCommand("newFuncObject");
      else
      {
         if (headset.countRows()>0 && headlay.isSingleLayout() && "VIM".equals(headset.getValue("CONNECTION_TYPE")))
            headbar.removeCustomCommand("newFuncObject");
      }

      if (!newSerialObject_)
         headbar.removeCustomCommand("newSerialObject");
      else
      {
         if (headset.countRows()>0 && headlay.isSingleLayout() && "VIM".equals(headset.getValue("CONNECTION_TYPE")))
            headbar.removeCustomCommand("newSerialObject");
      }

      if (!grapicalObject_)
         headbar.removeCustomCommand("grapicalObject");
      else
      {
         if (headset.countRows()>0 && headlay.isSingleLayout() && "VIM".equals(headset.getValue("CONNECTION_TYPE")))
            headbar.removeCustomCommand("grapicalObject");
      }

      if (!prepareWork_)
         headbar.removeCustomCommand("PrepareWork");

      if (!reportIn_)
         headbar.removeCustomCommand("reportIn");

      if (!preposting_)
         headbar.removeCustomCommand("preposting");

      if (!printProposal_)
         headbar.removeCustomCommand("printProposal");

      if (mgr.isPresentationObjectInstalled("enterw/CustomerInfo.page") && !custInfo_)
         headbar.removeCustomCommand("custInfo");

      if (mgr.isPresentationObjectInstalled("orderw/CustomerAgreement.page") && !custAgreement_)
         headbar.removeCustomCommand("custAgreement");

      //Bug 68947, Start 
      if (!srvCon_ || !(headset.countRows()>0 && headlay.isSingleLayout()) || !"VIM".equals(headset.getValue("CONNECTION_TYPE")))
         headbar.removeCustomCommand("srvConLineSearch");
      //Bug 68947, End   
      if (!actWoOrd_)
         headbar.removeCustomCommand("actWoOrd");
      else
      {
         if (headset.countRows()>0 && headlay.isSingleLayout() && "VIM".equals(headset.getValue("CONNECTION_TYPE")))
            headbar.removeCustomCommand("actWoOrd");
      }

      if (!deptResources_)
         headbar.removeCustomCommand("deptResources");

      if (!createQuot_)
         headbar.removeCustomCommand("createQuot");

      if (mroCreateRO_)
      {
         if (headlay.isSingleLayout())
         {
            if (!checkMROObjRO())
               headbar.removeCustomCommand("mroObjReceiveO");
         }
      }
      else
         headbar.removeCustomCommand("mroObjReceiveO");

      if (!prepWoQuot_)
         headbar.removeCustomCommand("prepWoQuot");

      if (!custWarr_)
         headbar.removeCustomCommand("custWarr");
      else
      {
         if (headset.countRows()>0 && headlay.isSingleLayout() && "VIM".equals(headset.getValue("CONNECTION_TYPE")))
            headbar.removeCustomCommand("custWarr");
      }

      if (!copy_)
         headbar.removeCustomCommand("copy");
   }  

   public void activateBudgets()
   {
      tabs.setActiveTab(1);
   }

   public void activateJobs()
   {
      tabs.setActiveTab(2);
   }

   // Bug Id 70921, Start
   public ASPBuffer calculateDatesForSrvcon(ASPBuffer buf)
   {

      ASPManager mgr = getASPManager();

      ASPBuffer newBuf = mgr.newASPBuffer();

      newBuf = buf;

      int ref = 0;

      trans.clear();

      String sPlanHrs;
      String sPlanStartDate = buf.getFieldValue("PLAN_S_DATE");
      String sRequiredStartDate =  buf.getFieldValue("REQUIRED_START_DATE");
      String sPlanCompleteDate = buf.getFieldValue("PLAN_F_DATE");
      String sRequiredEndDate = buf.getFieldValue("REQUIRED_END_DATE");
      String sContractId = buf.getValue("CONTRACT_ID");
      String sLineNo = buf.getValue("LINE_NO");
      String sWoNo = buf.getValue("WO_NO");
      String sPriorityId = buf.getValue("PRIORITY_ID");
      String sRegDate = buf.getFieldValue("REG_DATE");
      String sSavedSrvcon = "";

      if (checksec("ACTIVE_SEPARATE_API.Get_Dates_For_Contract", 1, isSecure))
      {
         cmd = trans.addCustomCommand("SCCONTRACTDATES", "ACTIVE_SEPARATE_API.Get_Dates_For_Contract");
         cmd.addParameter("REQUIRED_START_DATE");
         cmd.addParameter("REQUIRED_END_DATE");
         cmd.addParameter("PLAN_F_DATE"); 
         cmd.addParameter("PLAN_S_DATE",sPlanStartDate); 
         cmd.addParameter("CONTRACT_ID", sContractId);
         cmd.addParameter("LINE_NO", sLineNo);
         cmd.addParameter("WO_NO", sWoNo);
         cmd.addParameter("PRIORITY_ID", sPriorityId);
         cmd.addParameter("REG_DATE", sRegDate);
      }

      if (checksec("ACTIVE_SEPARATE_API.Get_Contract_Id", 1, isSecure))
      {
         cmd = trans.addCustomFunction("WOSAVEDSRVCON", "ACTIVE_SEPARATE_API.Get_Contract_Id","DUMMY_CONTRACT_ID");
         cmd.addParameter("WO_NO", sWoNo);
      }
      trans = mgr.perform(trans);

      sPlanStartDate = trans.getBuffer("SCCONTRACTDATES/DATA").getValue("PLAN_S_DATE");
      sRequiredStartDate = trans.getBuffer("SCCONTRACTDATES/DATA").getValue("REQUIRED_START_DATE");
      sPlanCompleteDate = trans.getBuffer("SCCONTRACTDATES/DATA").getValue("PLAN_F_DATE");
      sRequiredEndDate = trans.getBuffer("SCCONTRACTDATES/DATA").getValue("REQUIRED_END_DATE");
      sPlanHrs = trans.getBuffer("SCCONTRACTDATES/DATA").getValue("PLAN_HRS");
      sSavedSrvcon = trans.getBuffer("WOSAVEDSRVCON/DATA").getFieldValue("DUMMY_CONTRACT_ID");
      if (mgr.isEmpty("sSavedSrvcon"))
      {
         sSavedSrvcon = "";
      }

      newBuf.clear();
      newBuf.addItem("PLAN_HRS",sPlanHrs);
      newBuf.addItem("PLAN_S_DATE",sPlanStartDate);
      newBuf.addItem("REQUIRED_START_DATE",sRequiredStartDate);
      newBuf.addItem("PLAN_F_DATE",sPlanCompleteDate);
      newBuf.addItem("REQUIRED_END_DATE",sRequiredEndDate);
      newBuf.addItem("CONTRACT_ID",sContractId);
      newBuf.addItem("LINE_NO",sLineNo);
      newBuf.addItem("WO_NO",sWoNo);
      newBuf.addItem("PRIORITY_ID",sPriorityId);
      newBuf.addItem("DUMMY_CONTRACT_ID",sSavedSrvcon);
      trans.clear();

      bSrvconDateChange = true;

      return(newBuf);
   }

   public String getDatesForSrvconValidate(ASPBuffer buf)
   {

      ASPManager mgr = getASPManager();

      int ref = 0;

      trans.clear();

      String sReturnStr = "";

      String sPlanHrs = buf.getFieldValue("PLAN_HRS");
      String sPlanStartDate = buf.getFieldValue("PLAN_S_DATE");
      String sRequiredStartDate =  buf.getFieldValue("REQUIRED_START_DATE");
      String sPlanCompleteDate = buf.getFieldValue("PLAN_F_DATE");
      String sRequiredEndDate = buf.getFieldValue("REQUIRED_END_DATE");
      String sContractId = buf.getValue("CONTRACT_ID");
      String sLineNo = buf.getValue("LINE_NO");
      String sWoNo = buf.getValue("WO_NO");
      String sPriorityId = buf.getValue("PRIORITY_ID");
      String sRegDate = buf.getFieldValue("REG_DATE");
      String sSavedSrvcon = "";
      String sContractReqStart = "";
      String sContractReqEnd = "";

      if (checksec("PSC_CONTR_PRODUCT_API.Exist", 1, isSecure))
      {
         cmd = trans.addCustomCommand("EXIST_LINE", "PSC_CONTR_PRODUCT_API.Exist");
         cmd.addParameter("CONTRACT_ID", sContractId);
         cmd.addParameter("LINE_NO", sLineNo);
      }

      if (checksec("ACTIVE_SEPARATE_API.Get_Dates_For_Contract", 1, isSecure))
      {
         cmd = trans.addCustomCommand("SCCONTRACTDATES", "ACTIVE_SEPARATE_API.Get_Dates_For_Contract");
         cmd.addParameter("REQUIRED_START_DATE");
         cmd.addParameter("REQUIRED_END_DATE");
         cmd.addParameter("PLAN_F_DATE"); 
         cmd.addParameter("PLAN_S_DATE",sPlanStartDate); 
         cmd.addParameter("CONTRACT_ID", sContractId);
         cmd.addParameter("LINE_NO", sLineNo);
         cmd.addParameter("WO_NO", sWoNo);
         cmd.addParameter("PRIORITY_ID", sPriorityId);
         cmd.addParameter("REG_DATE", sRegDate);
      }

      if (checksec("ACTIVE_SEPARATE_API.Get_Contract_Id", 1, isSecure))
      {
         cmd = trans.addCustomFunction("WOSAVEDSRVCON", "ACTIVE_SEPARATE_API.Get_Contract_Id","DUMMY_CONTRACT_ID");
         cmd.addParameter("WO_NO", sWoNo);
      }
      trans = mgr.perform(trans);

      sPlanStartDate = trans.getBuffer("SCCONTRACTDATES/DATA").getFieldValue("PLAN_S_DATE");
      sRequiredStartDate = trans.getBuffer("SCCONTRACTDATES/DATA").getFieldValue("REQUIRED_START_DATE");
      sPlanCompleteDate = trans.getBuffer("SCCONTRACTDATES/DATA").getFieldValue("PLAN_F_DATE");
      sRequiredEndDate = trans.getBuffer("SCCONTRACTDATES/DATA").getFieldValue("REQUIRED_END_DATE");
      sPlanHrs = trans.getBuffer("SCCONTRACTDATES/DATA").getValue("PLAN_HRS");
      sSavedSrvcon = trans.getBuffer("WOSAVEDSRVCON/DATA").getFieldValue("DUMMY_CONTRACT_ID");

      sContractReqStart = sRequiredStartDate;
      sContractReqEnd = sRequiredEndDate;
      bSrvconDateChange = true;

      sReturnStr = (mgr.isEmpty(sPlanStartDate) ? "" : sPlanStartDate) + "^" + 
                   (mgr.isEmpty(sRequiredStartDate) ? "" : sRequiredStartDate) + "^" + 
                   (mgr.isEmpty(sPlanCompleteDate) ? "" : sPlanCompleteDate) + "^" + 
                   (mgr.isEmpty(sRequiredEndDate) ? "" : sRequiredEndDate) + "^" + 
                   (mgr.isEmpty(sPlanHrs) ? "" : sPlanHrs) + "^" +
                   (mgr.isEmpty(sSavedSrvcon) ? "" : sSavedSrvcon) + "^" +
                   (mgr.isEmpty(sContractReqStart) ? "" : sContractReqStart) + "^" +
                   (mgr.isEmpty(sContractReqEnd) ? "" : sContractReqEnd) + "^" ;

      trans.clear();
      return(sReturnStr);
   }

   public String calEndDatesAccPlanHrs (ASPBuffer buf)
   {

      ASPManager mgr = getASPManager();

      int ref = 0;
      String sPlanHrs = buf.getFieldValue("PLAN_HRS");
      String sContractId = buf.getFieldValue("CONTRACT_ID");
      String sLineNo = buf.getFieldValue("LINE_NO");
      String sOrgCode = buf.getFieldValue("ORG_CODE");
      String sContract = buf.getFieldValue("CONTRACT");
      String sPlanSDate = buf.getFieldValue("PLAN_S_DATE");
      String sReqSDate = buf.getFieldValue("REQUIRED_START_DATE");
      String sPlanCompleteDate = buf.getFieldValue("PLAN_F_DATE");
      String sRequiredEndDate = buf.getFieldValue("REQUIRED_END_DATE");
      String sReturnStr =  "^^"; 


      trans.clear();

      if (mgr.isEmpty(sContractId) || mgr.isEmpty(sLineNo))
      {
         if (checksec("Active_Work_Order_API.Calc_Completion_Date", 1, isSecure))
         {
            cmd = trans.addCustomFunction("CALCCOMPDATE", "Active_Work_Order_API.Calc_Completion_Date","PLAN_F_DATE");
            cmd.addParameter("ORG_CODE", sOrgCode);
            cmd.addParameter("CONTRACT", sContract);
            cmd.addParameter("PLAN_S_DATE", sPlanSDate);
            cmd.addParameter("PLAN_HRS", sPlanHrs);

            trans = mgr.perform(trans);

            sPlanCompleteDate = trans.getBuffer("CALCCOMPDATE/DATA").getFieldValue("PLAN_F_DATE");

         }
      }
      else
      {
         if (checksec("Active_Separate_Api.Cal_Srvcon_Dates_For_Exec_Time", 1, isSecure))
         {
            cmd = trans.addCustomCommand("SRVCONENDDATES", "Active_Separate_Api.Cal_Srvcon_Dates_For_Exec_Time");
            cmd.addParameter("PLAN_F_DATE");
            cmd.addParameter("REQUIRED_END_DATE");
            cmd.addParameter("PLAN_S_DATE",sPlanSDate); 
            cmd.addParameter("REQUIRED_START_DATE",sReqSDate); 
            cmd.addParameter("PLAN_HRS",sPlanHrs);
            cmd.addParameter("CONTRACT_ID", sContractId);
            cmd.addParameter("LINE_NO", sLineNo);

            trans = mgr.perform(trans);

            sPlanCompleteDate = trans.getBuffer("SRVCONENDDATES/DATA").getFieldValue("PLAN_F_DATE");
            sRequiredEndDate = trans.getBuffer("SRVCONENDDATES/DATA").getFieldValue("REQUIRED_END_DATE");

         }
      }
      sReturnStr = (mgr.isEmpty(sPlanCompleteDate) ? "" : sPlanCompleteDate) + "^" +
                   (mgr.isEmpty(sRequiredEndDate) ? "" : sRequiredEndDate) + "^" ; 

      trans.clear();
      return(sReturnStr);
   }

   // Bug Id 70921, End


   public void adjust()
   {
      ASPManager mgr = getASPManager();
      // Bug Id 70012, Start
      isSecure = new String[2];
      // Bug Id 70012, End

      DissableRmbs();
      if (mgr.isPresentationObjectInstalled("equipw/EquipmentAllObjectLightRedirect.page"))
         mgr.getASPField("MCH_CODE").setHyperlink("../equipw/EquipmentAllObjectLightRedirect.page","MCH_CODE,MCH_CODE_CONTRACT,WO_NO","NEWWIN");

      if ("TRUE".equals(caseFlag))
      {
         headbar.disableCommand(headbar.SAVENEW);
         headbar.disableCommand(headbar.NEWROW);
      }

      headbar.removeCustomCommand("activateBudgets");
      headbar.removeCustomCommand("activateJobs");

      if (headset.countRows()>0)
         comp = headset.getRow().getValue("COMPANY");

      //Bug 87935, Start, Modified the code to get correct pres objects
      if (headlay.isFindLayout())
      {
         isFind = true;
         mgr.getASPField("MCH_CODE").setDynamicLOV("MAINTENANCE_OBJECT");
      }
      else
      {
         mgr.getASPField("MCH_CODE").setLOV("MaintenanceObjectAddrLov1.page","MCH_CODE_CONTRACT",600,450);
         mgr.getASPField("MCH_CODE").setLOVProperty("WHERE","(OBJ_LEVEL IS NULL OR (OBJ_LEVEL IS NOT NULL AND Equipment_Object_Level_API.Get_Create_Wo(OBJ_LEVEL) = 'TRUE'))");
      }
      //Bug 87935, End

      if (headset.countRows()>0)
      {
         if (headlay.isSingleLayout()  ||   headset.countRows() == 1)
         {
            enabled_events = "^"+headset.getValue("DUP_OBJEVENTS");

            if (enabled_events.indexOf("^Confirm^",0)<0)
               headbar.disableCustomCommand("observed");

            if (enabled_events.indexOf("^ToPrepare^",0)<0)
               headbar.disableCustomCommand("underPrep");

            if (enabled_events.indexOf("^Prepare^",0)<0)
               headbar.disableCustomCommand("prepared");

            if ((enabled_events.indexOf("^Release^",0)<0) && (enabled_events.indexOf("^Replan^",0)<0))
               headbar.disableCustomCommand("released");

            if ((enabled_events.indexOf("^StartOrder^",0)<0) && (enabled_events.indexOf("^Restart^",0)<0))
               headbar.disableCustomCommand("started");

            if (enabled_events.indexOf("^Work^",0)<0)
               headbar.disableCustomCommand("workDone");

            if (enabled_events.indexOf("^Report^",0)<0)
               headbar.disableCustomCommand("reported");

            if (enabled_events.indexOf("^Finish^",0)<0)
               headbar.disableCustomCommand("finished");

            if (enabled_events.indexOf("^Cancel^",0)<0)
               headbar.disableCustomCommand("cancelled");

            //c = 0;


            if (!("FAULTREPORT".equals(headset.getValue("OBJSTATE"))))
            {
               headbar.removeCustomCommand("createQuot");
               //c++;
            }

            if (mgr.isEmpty(headset.getValue("QUOTATION_ID")))
            {
               if (prepWoQuot_)
                  headbar.removeCustomCommand("prepWoQuot");
               //c++;
            }
         }

         if (mgr.isPresentationObjectInstalled("enterw/CustomerInfo.page"))
            mgr.getASPField("CUSTOMER_NO").setHyperlink("../enterw/CustomerInfo.page","CUSTOMER_ID","NEWWIN");

         // Bug Id 70012, Start
         if (mgr.isModuleInstalled("PCMSCI"))
         {
            bPcmsciExist = true;
         }
         // Bug Id 70012, End


      }

      if (headset.countRows() == 0)
      {
         headbar.disableCommandGroup(headbar.CMD_GROUP_BROWSE);
         headbar.disableCommand(headbar.REMOVE);
         headbar.disableCommand(headbar.SUBMIT);
         headbar.disableCommand(headbar.PERFORM);

         headbar.removeCustomCommand("observed");
         headbar.removeCustomCommand("prepared");
         headbar.removeCustomCommand("cancelled");
         headbar.removeCustomCommand("changeConditionCode");
      }

      fldTitleReportedBy =  mgr.translate("PCMWACTIVESEPARATE3REPORTEDBYFLD: Reported+by");  

      lovTitleReportedBy =  mgr.translate("PCMWACTIVESEPARATE3REPORTEDBYLOV: List+of+Reported+by");  

      if (headlay.isEditLayout())
      {
         String cType = headset.getRow().getValue("CONNECTION_TYPE_DB");

         if ("VIM".equals(cType))
         {
            mgr.getASPField("MCH_CODE").setReadOnly();
            mgr.getASPField("MCH_CODE_CONTRACT").setReadOnly();
         }
      }

      if ((itemset1.countRows() == 6) && !headlay.isMultirowLayout())
      {
         sumColumns(); 
         itemset1.goTo(item1CurrRow);
      }

      if (headset.countRows() > 0)
      {
         if ((itemset1.countRows() < 6) && !headlay.isMultirowLayout() && !headlay.isNewLayout() && !headlay.isEditLayout())
         {
            okFindITEM1(); 
            sumColumns(); 
            itemset1.goTo(item1CurrRow);
         }
      }

      if (item1CurrRow == 6 && itemlay1.isSingleLayout())
         itembar1.disableCommand(itembar1.EDITROW);

      if ((headset.countRows()>0)&&(headlay.isSingleLayout()))
      {
         if (objectForCustomer_)
         {
            if ((mgr.isEmpty(headset.getRow().getValue("CUSTOMER_NO"))) && (mgr.isPresentationObjectInstalled("equipw/WorkOrderCustomer.page")))
               headbar.removeCustomCommand("objectForCustomer");
         }

         if (customforobj_)
         {
            if ((mgr.isEmpty(headset.getRow().getValue("MCH_CODE"))) && (mgr.isPresentationObjectInstalled("equipw/MaintenanceObject4.page")))
               headbar.removeCustomCommand("customforobj");
         }

         if (headset.countRows() > 0)
         {
            if (objectForCustomer_)
            {
               if ((!mgr.isEmpty(headset.getRow().getValue("CUST_ORDER_LINE_NO"))) && (mgr.isPresentationObjectInstalled("equipw/WorkOrderCustomer.page")))
                  headbar.removeCustomCommand("objectForCustomer");
            }
            if (customforobj_)
            {
               if ((!mgr.isEmpty(headset.getRow().getValue("CUST_ORDER_NO"))) && (!mgr.isEmpty(headset.getRow().getValue("MCH_CODE"))) && (mgr.isPresentationObjectInstalled("equipw/MaintenanceObject4.page")))
                  headbar.removeCustomCommand("customforobj");
            }
         }

         if ("VIM".equals(headset.getRow().getValue("CONNECTION_TYPE_DB")))
            setCheckboxForVim();
      }
      if (custWarr_)
      {

         if (headset.countRows() > 1 && !(headlay.isSingleLayout()))
            headbar.removeCustomCommand("custWarr");
      }

      if (newWarr)
      {
         mgr.getASPField("CUST_WARR_TYPE").setHidden();
         mgr.getASPField("WARR_DESC").setHidden();
      }
      else
      {
         mgr.getASPField("CUST_WARR_TYPE_DUMMY").setHidden();
         mgr.getASPField("WARR_DESC_DUMMY").setHidden();
      }

      if (custAgreement_)
      {
         if (headset.countRows()>0 && headlay.isSingleLayout())
         {
            if (mgr.isEmpty(headset.getRow().getValue("AGREEMENT_ID"))  && mgr.isPresentationObjectInstalled("orderw/CustomerAgreement.page"))
               headbar.removeCustomCommand("custAgreement");
         }
      }

      if (itemlay2.isVisible())
      {
         String sWhereStrForITEM2 = "CONTRACT = '" + headset.getValue("CONTRACT") + "'";

         if (itemlay2.isFindLayout())
         {
            mgr.getASPField("STD_JOB_ID").setLOV("StandardJobLov1.page", 600, 450);
            sWhereStrForITEM2 = sWhereStrForITEM2 + " AND STANDARD_JOB_TYPE_DB = '1'";
         }

         mgr.getASPField("STD_JOB_ID").setLOVProperty("WHERE", sWhereStrForITEM2);
      }

      if (headset.countRows()>0)
      {
         if ("VIM".equals(headset.getRow().getValue("CONNECTION_TYPE_DB")))
            mgr.getASPField("ORG_CODE").setDynamicLOV("ORG_WORKSHOP_LOV");
         else
            mgr.getASPField("ORG_CODE").setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV"); 
      }

      //Removed the scroll buttons of the parent when the child tabs is in new or edit mode
      if ( itemlay1.isEditLayout() || itemlay2.isNewLayout() || itemlay2.isEditLayout() )
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

//      Bug 68363, Start
      if (headset.countRows() > 0 && headlay.isSingleLayout())
      {
         getSettingTime();
      }
//      Bug 68363, End

      //(+) Bug 66406, Start
      if (itemlay2.isFindLayout())
         mgr.getASPField("CONN_PM_NO").setLOV("../pcmw/PmActionLov1.page",600,450);
      else if (itemlay2.isNewLayout())
         mgr.getASPField("CONN_PM_NO").setLOV("../pcmw/PmActionLov.page",600,450);
      //(+) Bug 66406, End 

   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "PCMWACTIVESEPARATE3TITLE: Service Request";
   }

   protected String getTitle()
   {
      return "PCMWACTIVESEPARATE3TITLE: Service Request";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      printHiddenField("HASPLANNING","");
      printHiddenField("PRPOSTCHANGED", "");
      //Bug 68947, Start   
      printHiddenField("TEMPCONTRACTID","");
      printHiddenField("TEMPLINENO", "");
      //Bug 68947, End   

      appendToHTML(headlay.show());

      if (headlay.isSingleLayout() && headset.countRows() > 0)
         appendToHTML(tabs.showTabsInit());

      if (headlay.isSingleLayout() && headset.countRows() > 0)
      {
         if (tabs.getActiveTab() == 1)
            appendToHTML(itemlay1.show());
         if (tabs.getActiveTab() == 2)
            appendToHTML(itemlay2.show());

      }

      appendToHTML("<table id=\"SND\" border=\"0\">\n");
      appendToHTML("<tr>\n");
      /*appendToHTML("<td><br>\n");
      appendToHTML(fmt.drawSubmit("SENDSMS",mgr.translate("PCMWACTIVESEPARATE3SENDSMSB: Send SMS"),""));
      appendToHTML("</td>\n");*/

      if (("CUSTORDER".equals(ctx.getGlobal("CALL_URL"))))
      {
         appendToHTML("      <td><br>\n");
         appendToHTML(fmt.drawSubmit("BACK",mgr.translate("PCMWACTIVESEPARATE3BACK: Back"),"BACK"));
         appendToHTML("</td>\n");
      }

      appendToHTML("</tr>\n");
      appendToHTML("</table>\n");

      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");


      appendDirtyJavaScript("window.name = \"ServiceRequest\";\n");

      appendDirtyJavaScript("function lovReportedBy(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    ss = '");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(comp));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("   if('");
      appendDirtyJavaScript(isFind); // XSS_Safe ILSOLK 20070713
      appendDirtyJavaScript("' == 'True')\n");
      appendDirtyJavaScript("      { \n");
      appendDirtyJavaScript("	   openLOVWindow('REPORTED_BY',i,\n");
      appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=' + URLClientEncode('");
      appendDirtyJavaScript(fldTitleReportedBy);
      appendDirtyJavaScript("') + '&__TITLE=' + URLClientEncode('");
      appendDirtyJavaScript(lovTitleReportedBy);
      appendDirtyJavaScript("') + ''\n");
      appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(ss) ,600,445,'validateReportedBy');\n");
      appendDirtyJavaScript("		}\n");
      appendDirtyJavaScript("	else\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("   openLOVWindow('REPORTED_BY',i,\n");
      appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=' + URLClientEncode('");
      appendDirtyJavaScript(fldTitleReportedBy);
      appendDirtyJavaScript("') + '&__TITLE=' + URLClientEncode('");
      appendDirtyJavaScript(lovTitleReportedBy);
      appendDirtyJavaScript("') + ''\n");
      appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
      appendDirtyJavaScript("		,600,445,'validateReportedBy');\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

      appendDirtyJavaScript("function validatePlanSDate(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkPlanSDate(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('PLAN_S_DATE',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('PLAN_F_DATE',i).value = getField_('PLAN_F_DATE',i).value;\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=PLAN_S_DATE'\n");
      appendDirtyJavaScript("		+ '&PLAN_S_DATE=' + URLClientEncode(getValue_('PLAN_S_DATE',i))\n");
      appendDirtyJavaScript("		+ '&PLAN_F_DATE=' + URLClientEncode(getValue_('PLAN_F_DATE',i))\n");
      appendDirtyJavaScript("		+ '&PLAN_HRS=' + URLClientEncode(getValue_('PLAN_HRS',i))\n");
      appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
      appendDirtyJavaScript("		+ '&HEAD_TEMP=' + URLClientEncode(getValue_('HEAD_TEMP',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'PLAN_S_DATE',i,'Planned Start') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('PLAN_F_DATE',i,0);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validatePlanFDate(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkPlanFDate(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('PLAN_F_DATE',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('PLAN_F_DATE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('PLAN_S_DATE',i).value = getField_('PLAN_S_DATE',i).value;\n");
      appendDirtyJavaScript("		getField_('HEAD_TEMP',i).value = '';\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=PLAN_F_DATE'\n");
      appendDirtyJavaScript("		+ '&PLAN_S_DATE=' + URLClientEncode(getValue_('PLAN_S_DATE',i))\n");
      appendDirtyJavaScript("		+ '&PLAN_F_DATE=' + URLClientEncode(getValue_('PLAN_F_DATE',i))\n");
      appendDirtyJavaScript("		+ '&PLAN_HRS=' + URLClientEncode(getValue_('PLAN_HRS',i))\n");
      appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
      appendDirtyJavaScript("		+ '&HEAD_TEMP=' + URLClientEncode(getValue_('HEAD_TEMP',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'PLAN_F_DATE',i,'Planned Completion') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('PLAN_F_DATE',i,0);\n");
      appendDirtyJavaScript("		assignValue_('PLAN_S_DATE',i,1);\n");
      appendDirtyJavaScript("		assignValue_('HEAD_TEMP',i,2);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	if (getValue_(\"PLAN_S_DATE\") != '')\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("	        var plannedCompleteDate = new Date( getValue_('PLAN_F_DATE',i) );\n");
      appendDirtyJavaScript("	        var plannedStartDate    = new Date( getValue_('PLAN_S_DATE',i) );\n");
      appendDirtyJavaScript("		if ( plannedCompleteDate < plannedStartDate )\n");
      appendDirtyJavaScript("		{\n");
      appendDirtyJavaScript("			alert('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE3COMPDATE: Completion Date is earlier than Start Date"));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("			getField_('PLAN_F_DATE',i).value = '';\n");
      appendDirtyJavaScript("		}\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validatePlanHrs(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkPlanHrs(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('PLAN_HRS',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('PLAN_F_DATE',i).value = getField_('PLAN_F_DATE',i).value;\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=PLAN_HRS'\n");
      appendDirtyJavaScript("		+ '&PLAN_S_DATE=' + URLClientEncode(getValue_('PLAN_S_DATE',i))\n");
      appendDirtyJavaScript("		+ '&PLAN_F_DATE=' + URLClientEncode(getValue_('PLAN_F_DATE',i))\n");
      appendDirtyJavaScript("		+ '&PLAN_HRS=' + URLClientEncode(getValue_('PLAN_HRS',i))\n");
      appendDirtyJavaScript("		+ '&REQUIRED_START_DATE=' + URLClientEncode(getValue_('REQUIRED_START_DATE',i))\n");
      appendDirtyJavaScript("		+ '&REQUIRED_END_DATE=' + URLClientEncode(getValue_('REQUIRED_END_DATE',i))\n");
      appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("		+ '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&CONTRACT_ID=' + URLClientEncode(getValue_('CONTRACT_ID',i))\n");
      appendDirtyJavaScript("		+ '&LINE_NO=' + URLClientEncode(getValue_('LINE_NO',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'PLAN_HRS',i,'Execution Time') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('PLAN_F_DATE',i,0);\n");
      appendDirtyJavaScript("		assignValue_('REQUIRED_END_DATE',i,1);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function checkMando()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  return checkReportedBy(0)&&\n");
      appendDirtyJavaScript("  checkErrDescr(0)&&\n");
      appendDirtyJavaScript("  checkOrgCode(0)&&\n");
      appendDirtyJavaScript("  showWarnings(0);\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("if('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(performRMB)); // Bug Id 68773
      appendDirtyJavaScript("' != \"\")\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  keys = '");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(keys));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("  winName  = '");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(performRMB));
      appendDirtyJavaScript("'.substr('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(performRMB));
      appendDirtyJavaScript("'.lastIndexOf('/')+1);\n");
      appendDirtyJavaScript("  window.open('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(performRMB));
      appendDirtyJavaScript("'+\".page?__TRANSFER=\"+URLClientEncode(keys),winName,\"scrollbars,resizable,status=yes,width=770,height=500\");\n");
      appendDirtyJavaScript("} \n");

      appendDirtyJavaScript("if('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(transferurl)); // Bug Id 68773
      appendDirtyJavaScript("' != \"\")\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" index1 =  '");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(transferurl));
      appendDirtyJavaScript("'.lastIndexOf('.page');\n");
      appendDirtyJavaScript(" index2 =  '");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(transferurl));
      appendDirtyJavaScript("'.lastIndexOf('/')+1;\n");
      appendDirtyJavaScript(" handle =  '");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(transferurl));
      appendDirtyJavaScript("'.slice(index2,index1);\n");
      appendDirtyJavaScript(" window.open('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(transferurl));
      appendDirtyJavaScript("',handle,\"alwaysRaised=yes,resizable,status=yes,scrollbars=yes,width=650,height=450\");\n");
      appendDirtyJavaScript(" transferurl = \"\";\n");
      appendDirtyJavaScript("}\n");

      /*appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(trnsfr);
      appendDirtyJavaScript("' == \"TRUE\")\n");
      appendDirtyJavaScript("     window.open(\"../common/scripts/SendSMSMessage.page\",\"test\",\"alwaysRaised=yes,resizable,status=yes,width=650,height=450\");\n");
 */

      appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(newman)); // Bug Id 68773
      appendDirtyJavaScript("' == \"TRUE\")\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   cntHEAD1.style.display='block';\n");
      //appendDirtyJavaScript("   cntHEAD1End.style.display='block';\n");
      appendDirtyJavaScript("   cntHEAD5.style.display='block';\n");
      //appendDirtyJavaScript("   cntHEAD5End.style.display='block';\n");
      appendDirtyJavaScript("   if ('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(caseFlag)); // Bug Id 68773
      appendDirtyJavaScript("' == \"TRUE\")\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      cntHEAD2.style.display='block';\n");
      //appendDirtyJavaScript("      cntHEAD2End.style.display='block';\n");
      appendDirtyJavaScript("   }   \n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovMchCode(i,params)\n");
      appendDirtyJavaScript("{\n"); 
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
      appendDirtyJavaScript("  var key_value = (getValue_('MCH_CODE',i).indexOf('%') !=-1)? getValue_('MCH_CODE',i):'';\n");
      appendDirtyJavaScript("  if (getValue_('CONNECTION_TYPE_DB',i) == 'VIM')\n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("          jLov = '../vimw/VimSerialStrWoLov.page';\n");
      appendDirtyJavaScript("  }\n");
      appendDirtyJavaScript("  else\n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("	 if( getRowStatus_('HEAD',i)=='QueryMode__' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("           jLov = '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=MAINTENANCE_OBJECT&__FIELD=Object+ID&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('MCH_CODE',i))\n"); 
      appendDirtyJavaScript("                  + '&MCH_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&CONTRACT=' + URLClientEncode(getValue_('MCH_CODE_CONTRACT',i));\n");       
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("       else\n");
      appendDirtyJavaScript("       {\n"); 
      appendDirtyJavaScript("  objwherecond = \" OBJ_LEVEL IS NULL OR (OBJ_LEVEL IS NOT NULL AND Equipment_Object_Level_API.Get_Create_Wo(OBJ_LEVEL) = 'TRUE')\";\n");
//      Call 144425 Start
      appendDirtyJavaScript("  	  jLov = 'MaintenanceObjectAddrLov1.page?MCH_CODE_CONTRACT=' +  URLClientEncode(getValue_('MCH_CODE_CONTRACT',i)) +'&CONNECTION_TYPE='+ URLClientEncode(getValue_('CONNECTION_TYPE_DB',i)) +'&__WHERE='+ objwherecond ;\n");
//      Call 144425 End
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("  }\n");
      appendDirtyJavaScript("  openLOVWindow('MCH_CODE',i,jLov");
      appendDirtyJavaScript(",600,450,'validateMchCode');\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(custFlag)); // Bug Id 68773
      appendDirtyJavaScript("' == \"TRUE\")\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   cntHEAD2.style.display='block';\n");
      //appendDirtyJavaScript("   cntHEAD2End.style.display='block';\n");
      appendDirtyJavaScript("   cntHEAD4.style.display='block';\n");
      //appendDirtyJavaScript("   cntHEAD4End.style.display='block';\n");
      //Call Id : 113079
      //appendDirtyJavaScript("   setMinimizeImage(document['groupHEAD2'],cntHEAD2,false)\n");
      appendDirtyJavaScript("   setMinimizeImage(document['groupHEAD2'],'cntHEAD2')\n");
      //appendDirtyJavaScript("   setMinimizeImage(document['groupHEAD4'],cntHEAD4,false)\n");
      appendDirtyJavaScript("   setMinimizeImage(document['groupHEAD4'],'cntHEAD4')\n");
      //End Call
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateReportedBy(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkReportedBy(i) ) return;\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=REPORTED_BY'\n");
      appendDirtyJavaScript("		+ '&REPORTED_BY=' + URLClientEncode(getValue_('REPORTED_BY',i))\n");
      appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'REPORTED_BY',i,'Reported by') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('REPORTED_BY_ID',i,0);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateContract(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkContract(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('CONTRACT',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('COMPANY',i).value = '';\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=CONTRACT'\n");
      appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'CONTRACT',i,'Site') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('COMPANY',i,0);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      //Bug 69819, Start, Modified validation
      appendDirtyJavaScript("function validateCustomerNo(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkCustomerNo(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('CUSTOMER_NO',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('CUST_NO',i).value = '';\n");
      appendDirtyJavaScript("		getField_('CLIENT_VALUE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('FAULT_REP_AGREE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('FAULT_REP_AGREE_CLIENT',i).value = '';\n");
      appendDirtyJavaScript("		getField_('CBHASAGREEMENT',i).checked = false;\n");
      appendDirtyJavaScript("		getField_('CUSTOMERDESCRIPTION',i).value = '';\n");
      appendDirtyJavaScript("		getField_('CONTRACT_ID',i).value = '';\n");
      appendDirtyJavaScript("		getField_('LINE_NO',i).value = '';\n");
      appendDirtyJavaScript("		validateContractId(i)\n");        
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=CUSTOMER_NO'\n");
      appendDirtyJavaScript("		+ '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
      appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
      appendDirtyJavaScript("		+ '&WORK_TYPE_ID=' + URLClientEncode(getValue_('WORK_TYPE_ID',i))\n");
      appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("		+ '&MCH_CODE_CONTRACT=' + URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&CONNECTION_TYPE_DB=' + URLClientEncode(getValue_('CONNECTION_TYPE_DB',i))\n");
//      Bug 69047, Start
      appendDirtyJavaScript("		+ '&CURRENCY_CODE=' + URLClientEncode(getValue_('CURRENCY_CODE',i))\n");
//        Bug 68363, Start
      //Bug 68947, Start   
      appendDirtyJavaScript("		+ '&CONTRACT_ID=' + URLClientEncode(getValue_('CONTRACT_ID',i))\n");
      appendDirtyJavaScript("		+ '&LINE_NO=' + URLClientEncode(getValue_('LINE_NO',i))\n");
      appendDirtyJavaScript("		+ '&WO_NO=' + URLClientEncode(getValue_('WO_NO',i))\n");
//        Bug 68363, End
//      Bug 69047, End
      //Bug 68947, End   
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'CUSTOMER_NO',i,'Customer No') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('CUST_NO',i,0);\n");
      appendDirtyJavaScript("		assignValue_('CLIENT_VALUE',i,1);\n");
      appendDirtyJavaScript("		assignValue_('FAULT_REP_AGREE',i,2);\n");
      appendDirtyJavaScript("		assignValue_('FAULT_REP_AGREE_CLIENT',i,3);\n");
      appendDirtyJavaScript("		assignValue_('ORG_CODE',i,6);\n");
      appendDirtyJavaScript("		assignValue_('ORGCODEDESCRIPTION',i,7);\n");
      appendDirtyJavaScript("		assignValue_('CBHASAGREEMENT',i,4);\n");
      appendDirtyJavaScript("       if (__getValidateValue(4)=='1')\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("        	getField_('CBHASAGREEMENT',i).checked = true;\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("       else\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("        	getField_('CBHASAGREEMENT',i).checked = false;\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("		assignValue_('CUSTOMERDESCRIPTION',i,5);\n");
//      Bug 69047, Start
      appendDirtyJavaScript("		assignValue_('CONTRACT_ID',i,8);\n");
      appendDirtyJavaScript("		assignValue_('LINE_NO',i,9);\n");
      appendDirtyJavaScript("		validateContractId(i)\n");
//      Bug 69047, End
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	if (getValue_('CUST_NO',i)==1) \n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("       message = '");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE3MSSG: Customer is Credit blocked"));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("       window.alert(message);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	if (getValue_('FAULT_REP_AGREE',i)==getValue_('FAULT_REP_AGREE_CLIENT',i))\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		message = '");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE3MESSG: Fault Report is not part of agreement for this work type"));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("        window.alert(message);\n");
      appendDirtyJavaScript("		document.form.AGREEMENT_ID.value = '';\n");
      appendDirtyJavaScript("		document.form.CUSTOMERAGREEMENTDESCRIPTION.value = '';\n");
      appendDirtyJavaScript("		document.form.ORG_CODE.value = '';\n");
      appendDirtyJavaScript("		document.form.ORGCODEDESCRIPTION.value = '';\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");
      //Bug 69819, End

      appendDirtyJavaScript("function hype()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   sHypeCode = document.form.MCH_CODE.value;\n");
      appendDirtyJavaScript("   cHypeContract = document.form.CONTRACT.value;\n");
      appendDirtyJavaScript("   if (sHypeCode == '')\n");
      appendDirtyJavaScript("       window.open(\"../equipw/EquipmentAllObject2.page\",\"hypeWin\",\"scrollbar,status=yes\");\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("		{\n");
      appendDirtyJavaScript("       window.open(\"../equipw/EquipmentAllObject2.page?MCH_CODE=\" + URLClientEncode(sHypeCode) + \"&CONTRACT=\" + URLClientEncode(cHypeContract),\"hypeWin\",\"scrollbars,status=yes\");\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function hypeCust()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   sHypeCust = document.form.CUSTOMER_NO.value;\n");
      appendDirtyJavaScript("   if (sHypeCust == '')\n");
      appendDirtyJavaScript("       window.open(\"../enterw/CustomerInfo.page\",\"hypeWin\",\"scrollbar,status=yes\");\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("		{\n");
      appendDirtyJavaScript("       window.open(\"../enterw/CustomerInfo.page?CUSTOMER_ID=\"+URLClientEncode(sHypeCust),\"hypeWin\",\"scrollbars,status=yes\");\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      //Bug 69819, Start, Modified validation
      appendDirtyJavaScript("function validateAgreementId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkAgreementId(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('AGREEMENT_ID',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('PLAN_HRS',i).value = '';\n");
      appendDirtyJavaScript("		getField_('AUTHORIZE_CODE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('FAULT_REP_AGREE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('FAULT_REP_AGREE_CLIENT',i).value = '';\n");
      appendDirtyJavaScript("		getField_('REQUIRED_START_DATE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('SETTINGTIME',i).value = '';\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=AGREEMENT_ID'\n");
      appendDirtyJavaScript("		+ '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
      appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
      appendDirtyJavaScript("		+ '&WORK_TYPE_ID=' + URLClientEncode(getValue_('WORK_TYPE_ID',i))\n");
      appendDirtyJavaScript("		+ '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ORGCODEDESCRIPTION=' + URLClientEncode(getValue_('ORGCODEDESCRIPTION',i))\n");
      appendDirtyJavaScript("		+ '&SETTINGTIME=' + URLClientEncode(getValue_('SETTINGTIME',i))\n");
      appendDirtyJavaScript("		+ '&REG_DATE=' + URLClientEncode(getValue_('REG_DATE',i))\n");
      appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("		+ '&REQUIRED_START_DATE=' + URLClientEncode(getValue_('REQUIRED_START_DATE',i))\n");
      appendDirtyJavaScript("		+ '&MCH_CODE_CONTRACT=' + URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&PLAN_S_DATE=' + URLClientEncode(getValue_('PLAN_S_DATE',i))\n");
      appendDirtyJavaScript("		+ '&CONNECTION_TYPE_DB=' + URLClientEncode(getValue_('CONNECTION_TYPE_DB',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'AGREEMENT_ID',i,'Agreement ID') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('ORG_CODE',i,0);\n");
      appendDirtyJavaScript("		assignValue_('ORGCODEDESCRIPTION',i,1);\n");
      appendDirtyJavaScript("		assignValue_('PLAN_HRS',i,2);\n");
      appendDirtyJavaScript("		assignValue_('AUTHORIZE_CODE',i,3);\n");
      appendDirtyJavaScript("		assignValue_('FAULT_REP_AGREE',i,4);\n");
      appendDirtyJavaScript("		assignValue_('FAULT_REP_AGREE_CLIENT',i,5);\n");
      appendDirtyJavaScript("		assignValue_('PLAN_S_DATE',i,6);\n");
      appendDirtyJavaScript("		assignValue_('REQUIRED_START_DATE',i,7);\n");
      appendDirtyJavaScript("		assignValue_('SETTINGTIME',i,8);\n");
      appendDirtyJavaScript("		assignValue_('PLAN_F_DATE',i,90);\n");
      appendDirtyJavaScript("		assignValue_('REQUIRED_END_DATE',i,10);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("    if ( getValue_('CONNECTION_TYPE_DB',i) == 'EQUIPMENT' && (getValue_('FAULT_REP_AGREE',i)== getValue_('FAULT_REP_AGREE_CLIENT',i)) )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		message = '");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE3MESSG: Fault Report is not part of agreement for this work type"));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("       window.alert(message);\n");
      appendDirtyJavaScript("		getField_('AGREEMENT_ID',i).value = '';\n");
      appendDirtyJavaScript("	}	\n");
      appendDirtyJavaScript("}\n");
      //Bug 69819, End

      appendDirtyJavaScript("function validateConnectionType(i)\n");
      appendDirtyJavaScript("{\n");      
      appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkConnectionType(i) ) return;\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=CONNECTION_TYPE'\n");
      appendDirtyJavaScript("		+ '&CONNECTION_TYPE=' + SelectURLClientEncode('CONNECTION_TYPE',i)		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'CONNECTION_TYPE',i,'Connection Type') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('CONNECTION_TYPE_DB',i,0);\n");
      appendDirtyJavaScript("		}\n");                  
      appendDirtyJavaScript(" return;\n");
      appendDirtyJavaScript("}\n");

      //Bug 69819, Start, Modified validation
      appendDirtyJavaScript("function validateMchCode(i)\n");
      appendDirtyJavaScript("{        \n");
      appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkMchCode(i) ) return;\n");      
      appendDirtyJavaScript("   if( getValue_('MCH_CODE',i)=='' )\n");
      appendDirtyJavaScript("	{    \n");
      appendDirtyJavaScript("           getField_('CBHASACTIVEWO',i).checked = false;\n");
      appendDirtyJavaScript("     	getField_('CBHASAGREEMENT',i).checked = false;\n");
      appendDirtyJavaScript("      	getField_('CBWARRANTYONOBJECT',i).checked = false;\n");
      appendDirtyJavaScript("           getField_('CBHASCUSTWARRANTYONOBJ',i).checked = false;\n");
      appendDirtyJavaScript("           getField_('CBHASWARRANTYONOBJ',i).checked = false;\n");
      appendDirtyJavaScript("		getField_('MCH_CODE_DESCRIPTION',i).value = '';     	\n");
      appendDirtyJavaScript("		getField_('SETTINGTIME',i).value = '';\n");
      //Bug Id 70921, Removed PlanHrs
      appendDirtyJavaScript("		getField_('ADDRESS_ID',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ADDRESS1',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ADDRESS2',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ADDRESS3',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ADDRESS4',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ADDRESS5',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ADDRESS6',i).value = '';\n");
      appendDirtyJavaScript("		getField_('AGREEMENT_ID',i).value = '';\n");
      appendDirtyJavaScript("		getField_('AUTHORIZE_CODE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('PART_NO',i).value = '';\n");
      appendDirtyJavaScript("		getField_('SERIAL_NO',i).value = '';\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=MCH_CODE'\n");
      appendDirtyJavaScript("		+ '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
      appendDirtyJavaScript("		+ '&CONNECTION_TYPE_DB=' + URLClientEncode(getValue_('CONNECTION_TYPE_DB',i))\n");
      appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
      appendDirtyJavaScript("		+ '&WORK_TYPE_ID=' + URLClientEncode(getValue_('WORK_TYPE_ID',i))\n");
      appendDirtyJavaScript("		+ '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ORGCODEDESCRIPTION=' + URLClientEncode(getValue_('ORGCODEDESCRIPTION',i))\n");
      appendDirtyJavaScript("		+ '&AUTHORIZE_CODE=' + URLClientEncode(getValue_('AUTHORIZE_CODE',i))\n");
      appendDirtyJavaScript("		+ '&REG_DATE=' + URLClientEncode(getValue_('REG_DATE',i))\n");
      appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("           + '&SETTINGTIME=' + URLClientEncode(getValue_('SETTINGTIME',i))\n");
      appendDirtyJavaScript("           + '&MCH_CODE_CONTRACT=' + URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&REQUIRED_START_DATE=' + URLClientEncode(getValue_('REQUIRED_START_DATE',i))\n");
      appendDirtyJavaScript("		+ '&PLAN_S_DATE=' + URLClientEncode(getValue_('PLAN_S_DATE',i))\n");
      appendDirtyJavaScript("		+ '&PLAN_F_DATE=' + URLClientEncode(getValue_('PLAN_F_DATE',i))\n");
      appendDirtyJavaScript("		+ '&REQUIRED_END_DATE=' + URLClientEncode(getValue_('REQUIRED_END_DATE',i))\n");      
//      Bug 69047, Start
      appendDirtyJavaScript("		+ '&CURRENCY_CODE=' + URLClientEncode(getValue_('CURRENCY_CODE',i))\n");      
//      Bug 69047, End
      appendDirtyJavaScript("		);\n");        
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'MCH_CODE',i,'Object ID') )\n");
      appendDirtyJavaScript("	{	\n");
      //Bug Id 70921, Removed PLAN_HRS, REQUIRED_START_DATE,PLAN_S_DATE,PLAN_F_DATE,REQUIRED_END_DATE
      appendDirtyJavaScript("		assignValue_('SETTINGTIME',i,0);\n");
      appendDirtyJavaScript("		assignValue_('CBWARRANTYONOBJECT',i,1);\n");
      appendDirtyJavaScript("    	assignValue_('CBHASAGREEMENT',i,2);\n");
      appendDirtyJavaScript("		assignValue_('CBHASACTIVEWO',i,3);\n");
      appendDirtyJavaScript("		assignValue_('ADDRESS_ID',i,4);\n");
      appendDirtyJavaScript("		assignValue_('ADDRESS1',i,5);\n");
      appendDirtyJavaScript("		assignValue_('ADDRESS2',i,6);\n");
      appendDirtyJavaScript("		assignValue_('ADDRESS3',i,7);\n");
      appendDirtyJavaScript("		assignValue_('ADDRESS4',i,8);\n");
      appendDirtyJavaScript("		assignValue_('ADDRESS5',i,9);\n");
      appendDirtyJavaScript("		assignValue_('ADDRESS6',i,10);\n");
      appendDirtyJavaScript("		assignValue_('AGREEMENT_ID',i,11);\n");
      appendDirtyJavaScript("		assignValue_('AUTHORIZE_CODE',i,12);\n");
      appendDirtyJavaScript("		assignValue_('ORG_CODE',i,13);\n");
      appendDirtyJavaScript("		assignValue_('ORGCODEDESCRIPTION',i,14);\n");
      appendDirtyJavaScript("		assignValue_('MCH_CODE_DESCRIPTION',i,15);\n");
      appendDirtyJavaScript("		assignValue_('MCH_CODE',i,16);\n");
      appendDirtyJavaScript("		assignValue_('MCH_CODE_CONTRACT',i,17);\n");      
      appendDirtyJavaScript("		assignValue_('PART_NO',i,18);\n");
      appendDirtyJavaScript("		assignValue_('SERIAL_NO',i,19);\n");
      appendDirtyJavaScript("		assignValue_('CBHASCUSTWARRANTYONOBJ',i,20);\n");
      appendDirtyJavaScript("    	assignValue_('CBHASWARRANTYONOBJ',i,21);\n");
//      Bug 69047, Start
      appendDirtyJavaScript("    	assignValue_('CONTRACT_ID',i,22);\n");
      appendDirtyJavaScript("    	assignValue_('LINE_NO',i,23);\n");
//      Bug 69047, End
      appendDirtyJavaScript("        if (__getValidateValue(2)== '1')\n");
      appendDirtyJavaScript("           getField_('CBWARRANTYONOBJECT',i).checked = true;\n");
      appendDirtyJavaScript("        else if (__getValidateValue(2)== '0')\n");
      appendDirtyJavaScript("           getField_('CBWARRANTYONOBJECT',i).checked = false;\n");
      appendDirtyJavaScript("        if (__getValidateValue(3)== '1')\n");
      appendDirtyJavaScript("		   getField_('CBHASAGREEMENT',i).checked = true;\n");
      appendDirtyJavaScript("        else if (__getValidateValue(3)== '0')\n");
      appendDirtyJavaScript("           getField_('CBHASAGREEMENT',i).checked = false;\n");
      appendDirtyJavaScript("        if (__getValidateValue(4)== '1')\n");
      appendDirtyJavaScript("		   getField_('CBHASACTIVEWO',i).checked = true;\n");
      appendDirtyJavaScript("        else if (__getValidateValue(4)== '0')\n");
      appendDirtyJavaScript("           getField_('CBHASACTIVEWO',i).checked = false;	\n");
      appendDirtyJavaScript("        if (__getValidateValue(25)== '1')\n");
      appendDirtyJavaScript("		   getField_('CBHASCUSTWARRANTYONOBJ',i).checked = true;\n");
      appendDirtyJavaScript("        else if (__getValidateValue(25)== '0')\n");
      appendDirtyJavaScript("           getField_('CBHASCUSTWARRANTYONOBJ',i).checked = false; \n");
      appendDirtyJavaScript("        if (__getValidateValue(26)== '1')\n");
      appendDirtyJavaScript("		   getField_('CBHASWARRANTYONOBJ',i).checked = true;\n");
      appendDirtyJavaScript("        else if (__getValidateValue(26)== '0')\n");
      appendDirtyJavaScript("           getField_('CBHASWARRANTYONOBJ',i).checked = false;	\n");
      appendDirtyJavaScript("    }\n"); 
//      Bug 69047, Start
      appendDirtyJavaScript("		validateContractId(i)\n");
//      Bug 69047, End
      appendDirtyJavaScript("}\n");     

      appendDirtyJavaScript("function validateWorkTypeId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkWorkTypeId(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('WORK_TYPE_ID',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('FAULT_REP_AGREE_CLIENT',i).value = '';\n");
      appendDirtyJavaScript("		getField_('FAULT_REP_AGREE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('WORKTYPEDESCRIPTION',i).value = '';\n");
      //aBug Id 70921, Removed PlanHrs
      appendDirtyJavaScript("		getField_('CONTRACT_ID',i).value = '';\n");
      appendDirtyJavaScript("		getField_('LINE_NO',i).value = '';\n");
      appendDirtyJavaScript("		validateContractId(i)\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=WORK_TYPE_ID'\n");
      appendDirtyJavaScript("		+ '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
      appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
      appendDirtyJavaScript("		+ '&WORK_TYPE_ID=' + URLClientEncode(getValue_('WORK_TYPE_ID',i))\n");
      appendDirtyJavaScript("		+ '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ORGCODEDESCRIPTION=' + URLClientEncode(getValue_('ORGCODEDESCRIPTION',i))\n");
      appendDirtyJavaScript("		+ '&SETTINGTIME=' + URLClientEncode(getValue_('SETTINGTIME',i))\n");
      appendDirtyJavaScript("		+ '&REG_DATE=' + URLClientEncode(getValue_('REG_DATE',i))\n");
      appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("		+ '&REQUIRED_START_DATE=' + URLClientEncode(getValue_('REQUIRED_START_DATE',i))\n");
      appendDirtyJavaScript("		+ '&MCH_CODE_CONTRACT=' + URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&PLAN_S_DATE=' + URLClientEncode(getValue_('PLAN_S_DATE',i))\n");
      appendDirtyJavaScript("		+ '&CONNECTION_TYPE_DB=' + URLClientEncode(getValue_('CONNECTION_TYPE_DB',i))\n");
//      Bug 69047, Start
      appendDirtyJavaScript("		+ '&CURRENCY_CODE=' + URLClientEncode(getValue_('CURRENCY_CODE',i))\n");
//      Bug 69047, End
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'WORK_TYPE_ID',i,'Work Type') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('FAULT_REP_AGREE_CLIENT',i,0);\n");
      appendDirtyJavaScript("		assignValue_('FAULT_REP_AGREE',i,1);\n");
      appendDirtyJavaScript("		assignValue_('ORG_CODE',i,2);\n");
      appendDirtyJavaScript("		assignValue_('ORGCODEDESCRIPTION',i,3);\n");
      appendDirtyJavaScript("		assignValue_('WORKTYPEDESCRIPTION',i,4);\n");
      //Bug Id 70921, removed PLAN_HRS,PLAN_S_DATE,REQUIRED_START_DATE
//      Bug 69047, Start
      appendDirtyJavaScript("		assignValue_('CONTRACT_ID',i,5);\n");
      appendDirtyJavaScript("		assignValue_('LINE_NO',i,6);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("		validateContractId(i)\n");
//      Bug 69047, End
      appendDirtyJavaScript("	if (getValue_('FAULT_REP_AGREE',i)==getValue_('FAULT_REP_AGREE_CLIENT',i))\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		message = '");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE3MESSG: Fault Report is not part of agreement for this work type"));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("       window.alert(message);\n");
      appendDirtyJavaScript("		getField_('AGREEMENT_ID',i).value = '';\n");
      appendDirtyJavaScript("	}	\n");
      appendDirtyJavaScript("}\n");
      //Bug 69819, End

      appendDirtyJavaScript("function preparedClient(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("if (");
      appendDirtyJavaScript(bException);
      appendDirtyJavaScript("){\n");
      appendDirtyJavaScript("      if (confirm('");
      appendDirtyJavaScript(mgr.translateJavaScript("ACTSEP2EXPEXIST: The Work Order causes a project exception. Do you want to continue?"));
      appendDirtyJavaScript("'))\n"); 
      appendDirtyJavaScript("      return true;\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("      return false;\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript("else\n");
      appendDirtyJavaScript("      return true;\n"); 
      appendDirtyJavaScript("}\n"); 

      //Web Alignment - simplify code for RMBs
      // XSS_Safe ILSOLK 20070706
      if (bOpenNewWindow)
      {
         appendDirtyJavaScript("  window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));
         appendDirtyJavaScript("\", \"");
         appendDirtyJavaScript(newWinHandle);
         appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
      }
      appendDirtyJavaScript("function checkITEM2SaveParams(i)\n");
      appendDirtyJavaScript("{");
      appendDirtyJavaScript("   r = __connect(\n");
      appendDirtyJavaScript("		  '");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=STD_JOB_FLAG'\n");
      appendDirtyJavaScript("       + '&ITEM2_WO_NO=' + URLClientEncode(getValue_('ITEM2_WO_NO',i))\n");
      appendDirtyJavaScript("       + '&JOB_ID=' + URLClientEncode(getValue_('JOB_ID',i))\n");
      appendDirtyJavaScript("       + '&STD_JOB_ID=' + URLClientEncode(getValue_('STD_JOB_ID',i))\n");
      appendDirtyJavaScript("       + '&STD_JOB_CONTRACT=' + URLClientEncode(getValue_('STD_JOB_CONTRACT',i))\n");
      appendDirtyJavaScript("       + '&STD_JOB_REVISION=' + URLClientEncode(getValue_('STD_JOB_REVISION',i))\n");
      appendDirtyJavaScript("       );\n");
      appendDirtyJavaScript("   window.status='';\n");
      appendDirtyJavaScript("   if( checkStatus_(r,'STD_JOB_FLAG',i,'STD_JOB_FLAG') )\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("           assignValue_('N_JOB_EXIST',i,0);\n");
      appendDirtyJavaScript("           assignValue_('S_STD_JOB_EXIST',i,1);\n");
      appendDirtyJavaScript("           assignValue_('N_ROLE_EXIST',i,2);\n");
      appendDirtyJavaScript("           assignValue_('N_MAT_EXIST',i,3);\n");
      appendDirtyJavaScript("           assignValue_('N_TOOL_EXIST',i,4);\n");
      appendDirtyJavaScript("           assignValue_('N_PLANNING_EXIST',i,5);\n");
      appendDirtyJavaScript("           assignValue_('N_DOC_EXIST',i,6);\n");
      appendDirtyJavaScript("           assignValue_('S_STD_JOB_ID',i,7);\n");
      appendDirtyJavaScript("           assignValue_('S_STD_JOB_CONTRACT',i,8);\n");
      appendDirtyJavaScript("           assignValue_('S_STD_JOB_REVISION',i,9);\n");
      appendDirtyJavaScript("           assignValue_('N_QTY',i,10);\n");
      //appendDirtyJavaScript("           assignValue_('S_IS_SEPARATE',i,11);\n");
      appendDirtyJavaScript("           assignValue_('S_AGREEMENT_ID',i,11);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   if (getValue_('N_JOB_EXIST',i) == 1)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if (getValue_('S_STD_JOB_EXIST',i) == 'TRUE')\n"); 
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         if ((getValue_('N_ROLE_EXIST',i) == 1 || getValue_('N_MAT_EXIST',i) == 1 || getValue_('N_TOOL_EXIST',i) == 1 || getValue_('N_PLANNING_EXIST',i) == 1 || getValue_('N_DOC_EXIST',i) == 1)\n");
      appendDirtyJavaScript("             && getValue_('STD_JOB_ID',i) == ''\n");
      appendDirtyJavaScript("             && getValue_('STD_JOB_REVISION',i) == '')\n");
      appendDirtyJavaScript("         {\n");
      appendDirtyJavaScript("            if (confirm('");
      appendDirtyJavaScript(mgr.translateJavaScript("WOJOB_REMSEP_STDJOB: Do you want to remove connected Operations, Materials, Planning, Tools/Facilities and Documents?"));
      appendDirtyJavaScript("'))\n");
      appendDirtyJavaScript("               getField_('KEEP_CONNECTIONS',i).value = 'NO';\n");
      appendDirtyJavaScript("            else\n");
      appendDirtyJavaScript("               getField_('KEEP_CONNECTIONS',i).value = 'YES';\n");
      appendDirtyJavaScript("         }\n");
      appendDirtyJavaScript("         else if ((getValue_('N_ROLE_EXIST',i) == 1 || getValue_('N_MAT_EXIST',i) == 1 || getValue_('N_TOOL_EXIST',i) == 1 || getValue_('N_PLANNING_EXIST',i) == 1 || getValue_('N_DOC_EXIST',i) == 1)\n"); 
      appendDirtyJavaScript("                  && getValue_('STD_JOB_ID',i) != ''\n");
      appendDirtyJavaScript("                  && getValue_('STD_JOB_REVISION',i) != ''\n");
      appendDirtyJavaScript("                  && (getValue_('S_STD_JOB_ID',i) != getValue_('STD_JOB_ID',i) || getValue_('S_STD_JOB_REVISION',i) != getValue_('STD_JOB_REVISION',i)))\n");
      appendDirtyJavaScript("         {\n");
      appendDirtyJavaScript("            if (confirm('");
      appendDirtyJavaScript(mgr.translateJavaScript("WOJOB_RECSEP_STDJOB: Do you want to remove and reconnect Operations, Materials, Tools/Facilities, Planning and Documents?"));
      appendDirtyJavaScript("'))\n");
      appendDirtyJavaScript("               getField_('RECONNECT',i).value = 'YES';\n");
      appendDirtyJavaScript("            else\n");
      appendDirtyJavaScript("               getField_('RECONNECT',i).value = 'NO';\n");
      appendDirtyJavaScript("         }\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   if (getValue_('STD_JOB_ID',i) != '' && getValue_('STD_JOB_CONTRACT',i) != '' && getValue_('STD_JOB_REVISION',i) != '' && getValue_('S_AGREEMENT_ID',i) != '')\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if (confirm('");
      appendDirtyJavaScript(mgr.translateJavaScript("WOJOB_INFO_STDJOB: Update with agreement prices?"));
      appendDirtyJavaScript("'))\n"); 
      appendDirtyJavaScript("         getField_('STD_JOB_FLAG',i).value = '1';\n");
      appendDirtyJavaScript("      else\n");
      appendDirtyJavaScript("         getField_('STD_JOB_FLAG',i).value = '0';\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   return true;\n");
      appendDirtyJavaScript("}");

      appendDirtyJavaScript("function lovOrgCode(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if(params) param = params;\n");
      appendDirtyJavaScript("   else param = '';\n");
      appendDirtyJavaScript("   var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
      appendDirtyJavaScript("   var key_value = (getValue_('ORG_CODE',i).indexOf('%') !=-1)? getValue_('ORG_CODE',i):'';\n");
      appendDirtyJavaScript("   var viewname = (getValue_('CONNECTION_TYPE_DB',i) == 'VIM')?'ORG_WORKSHOP_LOV':'ORG_CODE_ALLOWED_SITE_LOV';\n");
      appendDirtyJavaScript("   openLOVWindow('ORG_CODE',i,\n");
      appendDirtyJavaScript("   '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=' + ");
      appendDirtyJavaScript("   viewname");
      appendDirtyJavaScript("   + '&__FIELD=' + URLClientEncode('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE3ORG_CODE: Maintenance Organization"));
      appendDirtyJavaScript("   ') + '&__INIT=1&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("   + '&__KEY_VALUE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
      appendDirtyJavaScript("   + '&ORG_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("   + '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
      appendDirtyJavaScript("   ,600,445,'validateOrgCode');\n");
      appendDirtyJavaScript("}");

      appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(hasPlanning);
      appendDirtyJavaScript("' == 'TRUE' && readCookie(\"PageID_Has_Planing\")== 'TRUE')\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("	      writeCookie(\"PageID_Has_Planing\", \"FALSE\", '', COOKIE_PATH); \n");
      appendDirtyJavaScript("       if (confirm('");
      appendDirtyJavaScript(mgr.translateJavaScript("WOJOB_REMSEP_STDJOB: Do you want to remove connected Operations, Materials, Planning, Tools/Facilities and Documents?"));
      appendDirtyJavaScript("')){\n");
      appendDirtyJavaScript("		document.form.HASPLANNING.value = \"AAAA\";\n");
      appendDirtyJavaScript("		f.submit();\n");
      appendDirtyJavaScript("     } \n");
      appendDirtyJavaScript("     else\n");
      appendDirtyJavaScript("     {\n");
      appendDirtyJavaScript("		document.form.HASPLANNING.value = \"BBBB\";\n");
      appendDirtyJavaScript("		f.submit();\n");
      appendDirtyJavaScript("     } \n");
      appendDirtyJavaScript("   }\n");

      appendDirtyJavaScript("function setCustomerAddress(addr)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("		getField_('CUST_ADD_ID',i).value = addr;\n");
      appendDirtyJavaScript("		validateCustAddId(-1);\n");
      appendDirtyJavaScript(" 		setAddressValuesAddress1(getValue_('ADDRESS1',i),getValue_('ADDRESS2',i),getValue_('ADDRESS3',i),getValue_('ADDRESS4',i),getValue_('ADDRESS5',i),getValue_('ADDRESS6',i),'');\n");
      appendDirtyJavaScript("}\n");

//      Bug 69047, Start
      appendDirtyJavaScript("function validateContractId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("		setDirty();\n");
      appendDirtyJavaScript("	if( !checkContractId(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('CONTRACT_ID',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("	if( getValue_('LINE_NO',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("	if( getValue_('CONTRACT_ID',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('CONTRACT_ID',i).value = '';\n");
      appendDirtyJavaScript("		getField_('LINE_NO',i).value = '';\n");
      appendDirtyJavaScript("		getField_('CONTRACT_NAME',i).value = '';\n");
      appendDirtyJavaScript("		getField_('LINE_DESC',i).value = '';\n");
      appendDirtyJavaScript("		getField_('CONTRACT_TYPE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('INVOICE_TYPE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('AUTHORIZE_CODE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('AGREEMENT_ID',i).value = '';\n");
      appendDirtyJavaScript("		getField_('AGREEMENT_DESC',i).value = '';\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	var requiredStartDate = getValue_('REQUIRED_START_DATE',i);\n");
      appendDirtyJavaScript("	var requiredEndDate = getValue_('REQUIRED_END_DATE',i);\n");

      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("	APP_ROOT+ 'pcmw/ActiveSeparate3.page'+'?VALIDATE=CONTRACT_ID'\n");
      appendDirtyJavaScript("	+ '&CONTRACT_ID=' + URLClientEncode(getValue_('CONTRACT_ID',i))\n");
      appendDirtyJavaScript("	+ '&LINE_NO=' + URLClientEncode(getValue_('LINE_NO',i))\n");
      appendDirtyJavaScript("	+ '&WO_NO=' + URLClientEncode(getValue_('WO_NO',i))\n"); //Bug Id 70921
      appendDirtyJavaScript("	+ '&PLAN_S_DATE=' + URLClientEncode(getValue_('PLAN_S_DATE',i))\n"); //Bug Id 70921
      appendDirtyJavaScript("	+ '&PLAN_F_DATE=' + URLClientEncode(getValue_('PLAN_F_DATE',i))\n"); //Bug Id 70921
      appendDirtyJavaScript("	+ '&REQUIRED_START_DATE=' + URLClientEncode(getValue_('REQUIRED_START_DATE',i))\n"); //Bug Id 70921
      appendDirtyJavaScript("	+ '&REQUIRED_END_DATE=' + URLClientEncode(getValue_('REQUIRED_END_DATE',i))\n"); //Bug Id 70921
      appendDirtyJavaScript("	+ '&PLAN_HRS=' + URLClientEncode(getValue_('PLAN_HRS',i))\n"); //Bug Id 70921
      appendDirtyJavaScript("	+ '&PRIORITY_ID=' + URLClientEncode(getValue_('PRIORITY_ID',i))\n"); //Bug Id 70921
      appendDirtyJavaScript("	+ '&REG_DATE=' + URLClientEncode(getValue_('REG_DATE',i))\n");
      appendDirtyJavaScript("	);\n");
      appendDirtyJavaScript("	window.status='';\n");

      appendDirtyJavaScript("	if( checkStatus_(r,'CONTRACT_ID',i,'Contract ID') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('CONTRACT_ID',i,0);\n");
      appendDirtyJavaScript("		assignValue_('LINE_NO',i,1);\n");
      appendDirtyJavaScript("		assignValue_('CONTRACT_NAME',i,2);\n");
      appendDirtyJavaScript("		assignValue_('LINE_DESC',i,3);\n");
      appendDirtyJavaScript("		assignValue_('CONTRACT_TYPE',i,4);\n");
      appendDirtyJavaScript("		assignValue_('INVOICE_TYPE',i,5);\n");
      appendDirtyJavaScript("		assignValue_('CUSTOMER_NO',i,6);\n");
      appendDirtyJavaScript("		assignValue_('AUTHORIZE_CODE',i,7);\n");
      appendDirtyJavaScript("		assignValue_('AGREEMENT_ID',i,8);\n");
      appendDirtyJavaScript("		assignValue_('AGREEMENT_DESC',i,9);\n");
      //Bug Id 70921, Start
      appendDirtyJavaScript("		assignValue_('WORK_TYPE_ID',i,10);\n");
      appendDirtyJavaScript("		assignValue_('WORKTYPEDESCRIPTION',i,11);\n");
      appendDirtyJavaScript("		assignValue_('ORG_CODE',i,12);\n");
      appendDirtyJavaScript("		assignValue_('ORGCODEDESCRIPTION',i,13);\n");
      appendDirtyJavaScript("		assignValue_('PLAN_S_DATE',i,14);\n");
      appendDirtyJavaScript("		assignValue_('REQUIRED_START_DATE',i,15);\n");
      appendDirtyJavaScript("		assignValue_('PLAN_F_DATE',i,16);\n");
      appendDirtyJavaScript("		assignValue_('REQUIRED_END_DATE',i,17);\n");
      appendDirtyJavaScript("		assignValue_('PLAN_HRS',i,18);\n");
      appendDirtyJavaScript("		assignValue_('DUMMY_CONTRACT_ID',i,19);\n");
      appendDirtyJavaScript("		assignValue_('DUMMY_REQ_START_DATE',i,20);\n");
      appendDirtyJavaScript("		assignValue_('DUMMY_REQ_END_DATE',i,21);\n");

      if (bShowWarning)
      {
         appendDirtyJavaScript("		if (getValue_('DUMMY_CONTRACT_ID',i)=='' && !getValue_('CONTRACT_ID',i)=='' && !getValue_('LINE_NO',i)=='' && (requiredStartDate!='' || requiredEndDate!='')) \n");
         appendDirtyJavaScript("		{\n");
         appendDirtyJavaScript("       	message = '");
         appendDirtyJavaScript(    mgr.translateJavaScript("SRVCONDATEOVERWRITE2: Existing Date values in fields Required Start or Latest Completion will be replaced with the dates from Service Contract Line "));
         appendDirtyJavaScript("'		+ __getValidateValue(0) +' '+__getValidateValue(1);\n");
         appendDirtyJavaScript("       	window.alert(message);\n");
         appendDirtyJavaScript("		}\n");
      }
      //Bug Id 70921, End
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");
//      Bug 69047, End

      //Bug Id 70921, Start

      appendDirtyJavaScript("function validateLineNo(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("		setDirty();\n");
      appendDirtyJavaScript("	if( !checkLineNo(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('CONTRACT_ID',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("	if( getValue_('LINE_NO',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("	if( getValue_('LINE_NO',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('LINE_NO',i).value = '';\n");
      appendDirtyJavaScript("		getField_('LINE_DESC',i).value = '';\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	var requiredStartDate = getValue_('REQUIRED_START_DATE',i);\n");
      appendDirtyJavaScript("	var requiredEndDate = getValue_('REQUIRED_END_DATE',i);\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("	APP_ROOT+ 'pcmw/ActiveSeparate3.page'+'?VALIDATE=LINE_NO'\n");
      appendDirtyJavaScript("	+ '&CONTRACT_ID=' + URLClientEncode(getValue_('CONTRACT_ID',i))\n");
      appendDirtyJavaScript("	+ '&LINE_NO=' + URLClientEncode(getValue_('LINE_NO',i))\n");
      appendDirtyJavaScript("	+ '&WO_NO=' + URLClientEncode(getValue_('WO_NO',i))\n"); 
      appendDirtyJavaScript("	+ '&PLAN_S_DATE=' + URLClientEncode(getValue_('PLAN_S_DATE',i))\n"); 
      appendDirtyJavaScript("	+ '&PLAN_F_DATE=' + URLClientEncode(getValue_('PLAN_F_DATE',i))\n"); 
      appendDirtyJavaScript("	+ '&REQUIRED_START_DATE=' + URLClientEncode(getValue_('REQUIRED_START_DATE',i))\n"); 
      appendDirtyJavaScript("	+ '&REQUIRED_END_DATE=' + URLClientEncode(getValue_('REQUIRED_END_DATE',i))\n");
      appendDirtyJavaScript("	+ '&PLAN_HRS=' + URLClientEncode(getValue_('PLAN_HRS',i))\n"); 
      appendDirtyJavaScript("	+ '&PRIORITY_ID=' + URLClientEncode(getValue_('PRIORITY_ID',i))\n");
      appendDirtyJavaScript("	+ '&REG_DATE=' + URLClientEncode(getValue_('REG_DATE',i))\n");
      appendDirtyJavaScript("	);\n");
      appendDirtyJavaScript("	window.status='';\n");

      appendDirtyJavaScript("	if( checkStatus_(r,'LINE_NO',i,'Line No') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('CONTRACT_ID',i,0);\n");
      appendDirtyJavaScript("		assignValue_('LINE_NO',i,1);\n");
      appendDirtyJavaScript("		assignValue_('CONTRACT_NAME',i,2);\n");
      appendDirtyJavaScript("		assignValue_('LINE_DESC',i,3);\n");
      appendDirtyJavaScript("		assignValue_('CONTRACT_TYPE',i,4);\n");
      appendDirtyJavaScript("		assignValue_('INVOICE_TYPE',i,5);\n");
      appendDirtyJavaScript("		assignValue_('AGREEMENT_ID',i,6);\n");
      appendDirtyJavaScript("		assignValue_('AGREEMENT_DESC',i,7);\n");
      appendDirtyJavaScript("		assignValue_('WORK_TYPE_ID',i,8);\n");
      appendDirtyJavaScript("		assignValue_('WORKTYPEDESCRIPTION',i,9);\n");
      appendDirtyJavaScript("		assignValue_('ORG_CODE',i,10);\n");
      appendDirtyJavaScript("		assignValue_('ORGCODEDESCRIPTION',i,11);\n");
      appendDirtyJavaScript("		assignValue_('PLAN_S_DATE',i,12);\n");
      appendDirtyJavaScript("		assignValue_('REQUIRED_START_DATE',i,13);\n");
      appendDirtyJavaScript("		assignValue_('PLAN_F_DATE',i,14);\n");
      appendDirtyJavaScript("		assignValue_('REQUIRED_END_DATE',i,15);\n");
      appendDirtyJavaScript("		assignValue_('PLAN_HRS',i,16);\n");
      appendDirtyJavaScript("		assignValue_('DUMMY_CONTRACT_ID',i,17);\n");
      appendDirtyJavaScript("		assignValue_('DUMMY_REQ_START_DATE',i,18);\n");
      appendDirtyJavaScript("		assignValue_('DUMMY_REQ_END_DATE',i,19);\n");
      if (bShowWarning)
      {
         appendDirtyJavaScript("		if (getValue_('DUMMY_CONTRACT_ID',i)=='' && !getValue_('CONTRACT_ID',i)=='' && !getValue_('LINE_NO',i)=='' && (requiredStartDate!='' || requiredEndDate!='')) \n");
         appendDirtyJavaScript("		{\n");
         appendDirtyJavaScript("       	message = '");
         appendDirtyJavaScript(    mgr.translateJavaScript("SRVCONDATEOVERWRITE2: Existing Date values in fields Required Start or Latest Completion will be replaced with the dates from Service Contract Line "));
         appendDirtyJavaScript("'		+ __getValidateValue(0) +' '+__getValidateValue(1);\n");
         appendDirtyJavaScript("       	window.alert(message);\n");
         appendDirtyJavaScript("		}\n");
      }
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");
      //Bug Id 70921, End


      //Bug Id 70921, Start
      appendDirtyJavaScript("function validatePriorityId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("		setDirty();\n");
      appendDirtyJavaScript("	if( !checkPriorityId(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('PRIORITY_ID',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('PRIORITYDESCRIPTION',i).value = '';\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("	APP_ROOT+ 'pcmw/ActiveSeparate3.page'+'?VALIDATE=PRIORITY_ID'\n");
      appendDirtyJavaScript("	+ '&PRIORITY_ID=' + URLClientEncode(getValue_('PRIORITY_ID',i))\n");
      appendDirtyJavaScript("	+ '&PLAN_S_DATE=' + URLClientEncode(getValue_('PLAN_S_DATE',i))\n"); 
      appendDirtyJavaScript("	+ '&REQUIRED_START_DATE=' + URLClientEncode(getValue_('REQUIRED_START_DATE',i))\n"); 
      appendDirtyJavaScript("	+ '&CONTRACT_ID=' + URLClientEncode(getValue_('CONTRACT_ID',i))\n");
      appendDirtyJavaScript("	+ '&LINE_NO=' + URLClientEncode(getValue_('LINE_NO',i))\n");
      appendDirtyJavaScript("	);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'PRIORITY_ID',i,'Priority') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('PLAN_HRS',i,0);\n");
      appendDirtyJavaScript("		assignValue_('PLAN_F_DATE',i,1);\n");
      appendDirtyJavaScript("		assignValue_('REQUIRED_END_DATE',i,2);\n");
      appendDirtyJavaScript("		assignValue_('PRIORITYDESCRIPTION',i,3);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function showWarnings(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if (getValue_(\"PLAN_S_DATE\") != '' && getValue_(\"REQUIRED_START_DATE\") != '')\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("	        var requiredStartDate = new Date( getValue_('REQUIRED_START_DATE',i) );\n");
      appendDirtyJavaScript("	        var plannedStartDate    = new Date( getValue_('PLAN_S_DATE',i) );\n");
      appendDirtyJavaScript("		if ( requiredStartDate < plannedStartDate )\n");
      appendDirtyJavaScript("		{\n");
      appendDirtyJavaScript("      		if (confirm('");
      appendDirtyJavaScript(            mgr.translateJavaScript("PLNSDATEREQSDATE: Planned Start Date is later than the Required Start Date. Do you want to continue?"));
      appendDirtyJavaScript("'     		))\n"); 
      appendDirtyJavaScript("      			return true;\n");
      appendDirtyJavaScript("      		else\n");
      appendDirtyJavaScript("      			return false;\n"); 
      appendDirtyJavaScript("         }\n");
      appendDirtyJavaScript("	}\n");

      appendDirtyJavaScript("	if (getValue_(\"PLAN_F_DATE\") != '' && getValue_(\"REQUIRED_END_DATE\") != '')\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("	        var requiredEndDate = new Date( getValue_('REQUIRED_END_DATE',i) );\n");
      appendDirtyJavaScript("	        var plannedFinishDate    = new Date( getValue_('PLAN_F_DATE',i) );\n");
      appendDirtyJavaScript("		if ( requiredEndDate < plannedFinishDate )\n");
      appendDirtyJavaScript("		{\n");
      appendDirtyJavaScript("      		if (confirm('");
      appendDirtyJavaScript(            mgr.translateJavaScript("PLNCDATELATECDATE: Planned Completion Date is later than the Latest Completion Date. Do you want to continue?"));
      appendDirtyJavaScript("'     		))\n"); 
      appendDirtyJavaScript("      			return true;\n");
      appendDirtyJavaScript("      		else\n");
      appendDirtyJavaScript("      			return false;\n"); 
      appendDirtyJavaScript("         }\n");
      appendDirtyJavaScript("	}\n");

      if (bSrvconDateChange)
      {
         appendDirtyJavaScript("	if (getValue_(\"CONTRACT_ID\") != '' && getValue_(\"LINE_NO\") != ''  && getValue_(\"REQUIRED_START_DATE\") != ''  && getValue_(\"DUMMY_REQ_START_DATE\") != '')\n");
         appendDirtyJavaScript("	{\n");
         appendDirtyJavaScript("	        var requiredStartDate = new Date( getValue_('REQUIRED_START_DATE',i) );\n");
         appendDirtyJavaScript("	        var dummyReqStartDate    = new Date( getValue_('DUMMY_REQ_START_DATE',i) );\n");
         appendDirtyJavaScript("		if ( dummyReqStartDate < requiredStartDate )\n");
         appendDirtyJavaScript("		{\n");
         appendDirtyJavaScript("      		if (confirm('");
         appendDirtyJavaScript(          mgr.translateJavaScript("REQSTARTOVERWRITE: Required Start is later than the Required Start calculated from the Service Contract Line. Do you want to continue?"));
         appendDirtyJavaScript("'     		))\n"); 
         appendDirtyJavaScript("			{\n");
         appendDirtyJavaScript("				bSrvconDateChange = false;\n");
         appendDirtyJavaScript("      			return true;\n");
         appendDirtyJavaScript("			}\n");
         appendDirtyJavaScript("      		else\n");
         appendDirtyJavaScript("      			return false;\n"); 
         appendDirtyJavaScript("         }\n");
         appendDirtyJavaScript("	}\n");

         appendDirtyJavaScript("	if (getValue_(\"CONTRACT_ID\") != '' && getValue_(\"LINE_NO\") != ''  && getValue_(\"REQUIRED_END_DATE\") != ''  && getValue_(\"DUMMY_REQ_END_DATE\") != '')\n");
         appendDirtyJavaScript("	{\n");
         appendDirtyJavaScript("	        var requiredEndDate = new Date( getValue_('REQUIRED_END_DATE',i) );\n");
         appendDirtyJavaScript("	        var dummyReqEndDate    = new Date( getValue_('DUMMY_REQ_END_DATE',i) );\n");
         appendDirtyJavaScript("		if ( dummyReqEndDate < requiredEndDate )\n");
         appendDirtyJavaScript("		{\n");
         appendDirtyJavaScript("      		if (confirm('");
         appendDirtyJavaScript(          mgr.translateJavaScript("REQENDOVERWRITE: Latest Completion is later than the Latest Completion calculated from the Service Contract Line. Do you want to continue?"));
         appendDirtyJavaScript("'     		))\n"); 
         appendDirtyJavaScript("			{\n");
         appendDirtyJavaScript("				bSrvconDateChange = false;\n");
         appendDirtyJavaScript("      			return true;\n");
         appendDirtyJavaScript("			}\n");
         appendDirtyJavaScript("      		else\n");
         appendDirtyJavaScript("      			return false;\n"); 
         appendDirtyJavaScript("         }\n");
         appendDirtyJavaScript("	}\n");
      }
      appendDirtyJavaScript("return true;\n");
      appendDirtyJavaScript("}\n");
      //Bug Id 70921, End


// Bug Id 70921, Commented code below 
//      Bug 68363, Start
      /*   if (bFECust)   
         {
            bFECust = false;
            appendDirtyJavaScript("validateCustomerNo(0);\n");
            appendDirtyJavaScript("if (getValue_('CONTRACT_ID',0) != '')\n");
            appendDirtyJavaScript("	 validateContractId(0);\n");
         }*/
//      Bug 68363, End
      if (bPpChanged)
      {
         bPpChanged = false;
         appendDirtyJavaScript("if (confirm(\"");
         appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2REPEXTPP: Do you want to replace existing pre-postings?"));
         appendDirtyJavaScript("\")) {\n");
         appendDirtyJavaScript("	  document.form.PRPOSTCHANGED.value = \"TRUE\";\n");
         appendDirtyJavaScript("       f.submit();\n");
         appendDirtyJavaScript("     } \n");
      }

      //Bug 68947, Start   
      appendDirtyJavaScript("function setContractId(contrId,lineNo,lov)\n{\n");
      appendDirtyJavaScript("	if (lov){\n");
      appendDirtyJavaScript("		document.form.CONTRACT_ID.value = contrId;\n");
      appendDirtyJavaScript("		document.form.LINE_NO.value = lineNo;\n");
      appendDirtyJavaScript("	 	validateContractId(0);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	else{\n");
      appendDirtyJavaScript("		document.form.TEMPCONTRACTID.value = contrId;\n");
      appendDirtyJavaScript("		document.form.TEMPLINENO.value = lineNo;\n");
      appendDirtyJavaScript("		f.submit();\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovLineNo(i,params)\n{\n");
      //Bug Id 70012, Start
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('CONTRACT_ID',i).indexOf('%') !=-1)? getValue_('CONTRACT_ID',i):'';\n");
      appendDirtyJavaScript(" 	if(getValue_('CONNECTION_TYPE_DB',i) != 'VIM')\n");
      appendDirtyJavaScript(" 	{\n");
      appendDirtyJavaScript("		whereCond1 = '';\n"); 
      appendDirtyJavaScript("	      	whereCond1 = \" OBJSTATE = 'Active' \";\n"); 
      appendDirtyJavaScript(" 	if (document.form.CONTRACT.value != '') \n");
      appendDirtyJavaScript("	      		whereCond1 += \" AND CONTRACT = '\" + URLClientEncode(document.form.CONTRACT.value) + \"' \";\n"); 
      appendDirtyJavaScript(" 	if (document.form.CUSTOMER_NO.value != '') \n");
      //(-/+) Bug 70920, Modified where condition to consider category objects (Start).
      appendDirtyJavaScript("	      		whereCond1 += \" AND (CUSTOMER_NO = '\" + URLClientEncode(document.form.CUSTOMER_NO.value)+\"' OR Sc_Contract_Customer_API.Check_Exist(CONTRACT_ID, ( '\" + URLClientEncode(document.form.CUSTOMER_NO.value)+ \"')) = 'TRUE' ) \" ;\n"); 
      //(-/+) Bug 70920, Modified where condition to consider category objects (Finish).
      appendDirtyJavaScript(" 	if (document.form.WORK_TYPE_ID.value != '') \n");
      appendDirtyJavaScript("	      		whereCond1 += \" AND WORK_TYPE_ID = '\" + URLClientEncode(document.form.WORK_TYPE_ID.value) + \"' \";\n"); 
      appendDirtyJavaScript(" 	if (document.form.CONTRACT_ID.value != '') \n");
      appendDirtyJavaScript("	      		whereCond1 += \" AND CONTRACT_ID = '\" + URLClientEncode(document.form.CONTRACT_ID.value) + \"' \";\n"); 
      appendDirtyJavaScript(" 	if (document.form.MCH_CODE.value != '') \n");
      if (bPcmsciExist)
      {
         //(-/+) Bug 70920, Modified where condition to consider category objects (Start).
         appendDirtyJavaScript(" 	if (document.form.MCH_CODE.value != '' && document.form.MCH_CODE_CONTRACT.value != '') \n");
         appendDirtyJavaScript("	      		whereCond1 += \" AND (((CONTRACT_ID,LINE_NO) IN (SELECT contract_id, line_no FROM PSC_SRV_LINE_OBJECTS WHERE mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' AND mch_contract = '\" + URLClientEncode(document.form.MCH_CODE_CONTRACT.value) + \"')) OR (mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' AND mch_contract = '\" + URLClientEncode(document.form.MCH_CODE_CONTRACT.value) + \"') OR CONNECTION_TYPE_DB = 'CATEGORY')\";\n");
         //(-/+) Bug 70920, Modified where condition to consider category objects (Finish).
         // Filtering by From Date
         appendDirtyJavaScript(" 	if (document.form.PLAN_S_DATE.value != '') \n");
         appendDirtyJavaScript("	      		whereCond1 += \" AND Psc_Contr_Product_Api.Get_Date_From(CONTRACT_ID,LINE_NO) <= to_date(to_char(to_date('\" + URLClientEncode(document.form.PLAN_S_DATE.value) + \"','mm/dd/yy hh:mi:ss AM'),\'YYYYMMDD\'),\'YYYYMMDD\') \";\n"); 
         appendDirtyJavaScript(" 	else \n");
         appendDirtyJavaScript("	      		whereCond1 += \" AND Psc_Contr_Product_Api.Get_Date_From(CONTRACT_ID,LINE_NO) <= sysdate \";\n"); 
      }
      appendDirtyJavaScript("		openLOVWindow('CONTRACT_ID',i,\n");
      appendDirtyJavaScript("		'PscContrProductLov.page' + '?__VIEW=DUMMY&__INIT=1&__LOV=Y&__WHERE=' +whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("							,550,500,'validateContractId');\n");
      appendDirtyJavaScript(" 	}\n");
      appendDirtyJavaScript("	else\n");
      appendDirtyJavaScript("  window.open(\"");
      appendDirtyJavaScript(mgr.encodeStringForJavascript("ServiceContractLineSearchDlg.page?__TRANSFER="));
      appendDirtyJavaScript("\"+ URLClientEncode('");
      appendDirtyJavaScript(headset.getSelectedRows("CUSTOMER_NO, CUSTOMERDESCRIPTION, WO_NO, CONTRACT, MCH_CODE, WORK_TYPE_ID").format());
      appendDirtyJavaScript("'),'serviceContract','scrollbars,resizable,status=yes,width=770,height=500');\n");
      //Bug Id 70012, End
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function lovContractId(i,params)\n{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('CONTRACT_ID',i).indexOf('%') !=-1)? getValue_('CONTRACT_ID',i):'';\n");
      appendDirtyJavaScript(" 	if(getValue_('CONNECTION_TYPE_DB',i) != 'VIM')\n");
      //Bug Id 70012, Start
      appendDirtyJavaScript(" 	{\n");
      appendDirtyJavaScript("		whereCond1 = '';\n"); 
      appendDirtyJavaScript("	      	whereCond1 = \" OBJSTATE = 'Active' \";\n"); 
      appendDirtyJavaScript(" 	if (document.form.CONTRACT.value != '') \n");
      appendDirtyJavaScript("	      		whereCond1 += \" AND CONTRACT = '\" + URLClientEncode(document.form.CONTRACT.value) + \"' \";\n"); 
      appendDirtyJavaScript(" 	if (document.form.CUSTOMER_NO.value != '') \n");
      //(-/+) Bug 70920, Modified where condition to consider category objects (Start).
      appendDirtyJavaScript("	      		whereCond1 += \" AND (CUSTOMER_NO = '\" + URLClientEncode(document.form.CUSTOMER_NO.value)+\"' OR Sc_Contract_Customer_API.Check_Exist(CONTRACT_ID, ( '\" + URLClientEncode(document.form.CUSTOMER_NO.value)+ \"')) = 'TRUE' ) \" ;\n"); 
      //(-/+) Bug 70920, Modified where condition to consider category objects (Finish).
      appendDirtyJavaScript(" 	if (document.form.WORK_TYPE_ID.value != '') \n");
      appendDirtyJavaScript("	      		whereCond1 += \" AND WORK_TYPE_ID = '\" + URLClientEncode(document.form.WORK_TYPE_ID.value) + \"' \";\n"); 

      appendDirtyJavaScript(" 	if (document.form.MCH_CODE.value != '') \n");
      if (bPcmsciExist)
      {
         //(-/+) Bug 70920, Modified where condition to consider category objects (Start).
         appendDirtyJavaScript(" 	if (document.form.MCH_CODE.value != '' && document.form.MCH_CODE_CONTRACT.value != '') \n");
         appendDirtyJavaScript("	      		whereCond1 += \" AND (((CONTRACT_ID,LINE_NO) IN (SELECT contract_id, line_no FROM PSC_SRV_LINE_OBJECTS WHERE mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' AND mch_contract = '\" + URLClientEncode(document.form.MCH_CODE_CONTRACT.value) + \"')) OR (mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' AND mch_contract = '\" + URLClientEncode(document.form.MCH_CODE_CONTRACT.value) + \"') OR CONNECTION_TYPE_DB = 'CATEGORY')\";\n");
         //(-/+) Bug 70920, Modified where condition to consider category objects (Finish).
         // Filtering by From Date
         appendDirtyJavaScript(" 	if (document.form.PLAN_S_DATE.value != '') \n");
         appendDirtyJavaScript("	      		whereCond1 += \" AND Psc_Contr_Product_Api.Get_Date_From(CONTRACT_ID,LINE_NO) <= to_date(to_char(to_date('\" + URLClientEncode(document.form.PLAN_S_DATE.value) + \"','mm/dd/yy hh:mi:ss AM'),\'YYYYMMDD\'),\'YYYYMMDD\') \";\n"); 
         appendDirtyJavaScript(" 	else \n");
         appendDirtyJavaScript("	      		whereCond1 += \" AND Psc_Contr_Product_Api.Get_Date_From(CONTRACT_ID,LINE_NO) <= sysdate \";\n"); 
      }
      appendDirtyJavaScript("		openLOVWindow('CONTRACT_ID',i,\n");
      appendDirtyJavaScript("		'PscContrProductLov.page' + '?__VIEW=DUMMY&__INIT=1&__LOV=Y&__WHERE=' +whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("							,550,500,'validateContractId');\n");
      appendDirtyJavaScript(" 	}\n");
      //Bug Id 70012, End
      appendDirtyJavaScript("	else\n");
      appendDirtyJavaScript("  window.open(\"");
      appendDirtyJavaScript(mgr.encodeStringForJavascript("ServiceContractLineSearchDlg.page?__TRANSFER="));
      appendDirtyJavaScript("\"+ URLClientEncode('");
      appendDirtyJavaScript(headset.getSelectedRows("CUSTOMER_NO, CUSTOMERDESCRIPTION, WO_NO, CONTRACT, MCH_CODE, WORK_TYPE_ID").format());
      appendDirtyJavaScript("'),'serviceContract','scrollbars,resizable,status=yes,width=770,height=500');\n");
      appendDirtyJavaScript("}\n");
      //Bug 68947, End   
   }
}
