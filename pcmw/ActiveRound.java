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
*  File        : ActiveRound.java 
*  Created     : ASP2JAVA Tool 010225 CHDELK
*  Modified    : 
*  CHDELK  010402  Removed Pre Posting & Purchase Information RMBs 
*  BUNILK  010426  Disabled edit,duplicate and delete buttons of material line block
*                  when the material state is "Closed". Corrected some number value errors
*                  validation and okFind() functions; 
*  CHDELK  010430  Made some modifications to Unreserve() function  -77508
*                  Made some modifications to enableSalespartComp() function  -77517
*  CHCRLK  010612  Modified overwritten validate functions.
*  BUNILK  010710  Modified validations of Material block.
*  INROLK  010717  Added RMBs Print,Transfer to customer, Preposting. call id 77810.
*  BUNILK  010718  Modified PART_NO field validation. 
*  ARWILK  010805  Created new page for ReportInRoundDlg;removed block dlgblk 
*                  and other related immutable attributes.
*  ARWILK  010806  Corrected function for Manage Fixed Price Postings.
*  CHCRLK  010806  Modified print Methods.
*  BUNILK  010806  Made some changes in return AutoString of getContents() function so that to call       
*                  checkPartVal() javascript function from onBlur event of PART_NO field. Done some changes in 
*                  validatePartNo() method also.
*  CHCRLK  010815  Modified method salesPartComp().
*  CHCRLK  010817  Removed some fields from multirow layouts of Time Report(Comment) and 
*                  General(Inspection Note,Action Description,Testpoint,Testpoint Description,Location).
*  JEWILK  010821  Modified to refresh the form when the rmb 'Manage Fixed Price Postings' is closed.
*  SHCHLK  010903  (Call Id: 78049) Added 2 new fields Qty To Invoice & Discount to the postings block.
*                  Corrected the validations in the postings block. (Price amount Calculation is done according to Qty To Invoice)     
*  ARWILK  010903  Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction(). 
*                  (Caused when cancelNew is performed after saveNew)
*  BUNILK  010919  Added Pre Posting Action to Material and Material requisition blocks.  
*  JEWILK  010921  Modified method prePostHead() to properly add enabled values to the dataBuffer.
*  CHCRLK  011005  Performed security check for actions and hyperlinks.
*  JEWILK  011015  Modified validations of fields 'PLAN_QTY','CATALOG_NO','PRICE_LIST_NO' to 
*                  correctly display 'Sales Price' and 'Discounted Price'.
*  VAGULK  020506  Added a text area to "Inspection Note" Call ID - 29247
*  VAGULK  020611  Added a text area to comment field in time report BUG ID- 30789
*  SHAFLK  020621  Bug 30450,Added Validation for "Comment" field.
*  BUNILK  020816  Bug Id 31915 Removed some codes from Hour field validation part of Time Report tab so that to
*                  make it possible for enter more than 24 hours.  
*  BUNILK  020906  Bug ID 32182 Added a new method isModuleInst() to check availability of 
*                  DOCMAN and ORDER module inside preDefine method.  
*  SHAFLK  021108  Bug Id 34064,Changed methods printPicList & printWO. 
*  YAWILK  021205  Added MCH_CODE_CONTRACT Modified MCH_CODE max length to 100     
*  PAPELK  021205  To ensure all calls to apis in non staic components are coded dynamically.
*  BUNILK  021212  Merged with 2002-3 SP3
*  BUNILK  030121  Codes reviewed. 
*  YAWILK  030129  Call Id 93402 added itemMchCodeContract modified faultReport() and serviceRequest()
*  JEJALK  030430  Added Condition Code and Condition Code Description to the Material tab.
*  JEJALk  030503  Added action "Issued Serilas" to the materials tab. 
*  JEJALK  030503  Added action "Available to reserve" to the material tab.
*  JEJALK  030506  Modified the action "Issued Serilas" 
*  SHAFLK  030508  Added validation for Inspection note and changed run(), preDefine() and printWO() methods.
*  SHAFLK  030513  Changed okFindITEM0
*  INROLK  030624  Added RMB Returns.
*  CHAMLK  030826  Added Ownership, Owner and Owner Name to Materials Tab.
*  CHAMLK  030827  Added Ownership, Owner and Owner Name to Posting Tab.
*  NEKOLK  031011  Merge of Take OFF-Bug Id 37351, Changed adjust() and made Customer No editable in preDefine(). 
*  SHTOLK  031016  Call Id 106654, Removed the field 'MCH_CODE_CONTRACT' from itemblk1 and add the field 'CONTRACT'
*  JEWILK  031020  Added method releaseMat() to be called by RMB Action 'ITEM2.Release'. Call 106664.
*  CHAMLK  031020  Modified function preDefine() to remove ',' from the ITEM4_ROW_NO Number field.
*  CHAMLK  031020  Modified function performItem() to send a page refresh after the state change of the Materials tab.
*  PAPELK  031023  Call Id 108308.Modified permitAttr() by adding 2 params to be passed.Modified run() to 
*                  change display when returning from the action 'Permit Attributes...' accordingly.
*  PAPELK  031024  Call Id 108381.Modified properties of ASPField("DOCUMENTS").
*  ARWILK  031210  Edge Developments - (Replaced blocks with tabs, Replaced State links with RMB's, Removed Clone and Reset Methods)
*  SAPRLK  040129  Web Alignment - Added multirow action to master form and the tabs, simplifying code for RMBs, remove unnecessary method calls for hidden fields,
*                  removed unnecessary global variables.  
*  VAGULK  040210  Web Alignment - arranged the field order as given in Centura.
*  VAGULK  040319  Web Alignment ,modified getContents().
*  SHAFLK  040116  Bug Id 41815,Removed Java dependencies.
*  SHAFLK  040213  Bug Id 40256,Modified sparePartObject() ,objStructure() and HTML part. 
*  SAPRLK  040329  Merge with SP1.
*  SAPRLK  040401  Made Changes for Web Alignment.
*  SAPRLK  040421  Corrected Call 114222.
*  ARWILK  040430  Added new field CBPROJCONNECTED.
*  ARWILK  040616  Added a new job tab. Added job_id to materials tab.(Spec - AMEC109A)  
*  ARWILK  040629  Added RMB's connectToActivity and disconnectFromActivity (Spec AMME613A - Project Umbrella)
*  SHAFLK  040601  Bug Id 43389,Removed RMB Returns..
*  THWILK  040630  Merged Bug Id 43389. 
*  ARWILK  040716  Changed methods calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  NIJALK  040726  Added parameter CONTRACT to method call to Role_Sales_Part_API.Get_Def_Catalog_No. 
*  ThWilk  040729  Added Project Information Block.(IID AMUT219).
*  SHAFLK  040421  Bug Id 42866,Modified method issueFromInvent().
*  THWILK  040818  Merged Bug Id 42866 and modified method call MAKE_ISSUE_DETAIL. 
*  THWILK  040819  Call 116917,Modified getEnabledFields().
*  THWILK  040820  Call 116977,Modified Adjust().
*  THWILK  040820  Call 116940,Modified reserve().
*  VAGULK  040820  Added fields "SUPPLY_CODE ,SUPPLY_CODE_DB and ACTIVITY_SEQ" to Material tab (Call ID 116930)
*  THWILK  040820  Call 116920,Modified predefine().
*  THWILK  040820  Call 116931,Modified javascript to add fields ONCEGIVENERROR and HIDDENPARTNO.
*  NIJALK  040820  Call 116952: Modified the method adjust().
*  ARWILK  040823  Call 116918: Modified methods connectToActivity and disconnectFromActivity.
*  ThWilk  040824  Call 116923: Supported multirow functionality for permit tab.Added methods replacePermit(),createPermit()
*                  and preparePermit() to add RMB actions replace permit prepare permit and create permit.Modified method predefine().
*  NIJALK  040824  Call 116946, Modified validate(),predefine(),getcontents().
*  ThWilk  040825  Call 117297, Modified Adjust().
*  BUNILK  040825  Changed server function of QTYONHAND field.
*  ARWILK  040826  Call 116944, Modified methods manReserve, unreserve and adjust.
*  ThWilk  040826  Call 117300, Modified run(),authorize(),authorizeAll(). 
*  ARWILK  040826  Call 117314, Added saveReturnITEM4 and saveReturnNewITEM4.
*  NIJALK  040826  Call 117104, Modified run().
*  NIJALK  040826  Call 117295, Modified preDefine(), adjust().
*  NIJALK  040826  Call 117279, Modified completed(), notCompleted().
*  VAGULK  040827  Corrected Calls 117249 and 117251,fetched the default values of "Owernership" and "Supply_code" in the new layout.
*  ARWILK  040831  Call 117288, Modifed validations for CATALOG_NO,ORG_CODE,ROLE_CODE in TimeReport tab.
*  ARWILK  040831  Call 117285, Modified performItem.
*  ARWILK  040901  Resolved Material status refresh problem.
*  NIJALK  040901  Call 117415, Modified predefine(), Added saveReturnItem6().
*  ARWILK  040902  Call 117643, Modified printWO.
*  SHAFLK  040721  Bug Id 43249,Modified validations of PART_NO, CONDITION_CODE, PART_OWNERSHIP and OWNER and added some fields to predefine and modified HTML part. 
*  NIJALK  040902  Merged bug 43249.
*  ARWILK  040910  Modified availtoreserve.
*  NIJALK  040921  Modified deleteITEM1(),deleteITEM4().
*  NIJALK  040929  Added parameter project_id to method calls to Inventory_Part_In_Stock_API.Get_Inventory_Quantity.
*  nijalk  041001  Renamed Signature to 'Executed By' in Jobs tab. Modified validations for SIGN_ID,EMPLOYEE_ID.
*  BUNILK  041004  Changed server function of QTYONHAND field and modified validations of Part No, Condition Code, Ownership and owner fields of Material tab..
*  ARWILK  041005  LCS Merge: 46434.
*  SHAFLK  040906  Bug 46542, Modified method matReqUnissue().
*  NIJALK  041007  Merged 46542. 
*  SHAFLK  040812  Bug 45904, Modified method issueFromInvent().
*  NIJALK  041007  Merged 45904.
*  NIJALK  041020  Modified issueFromInvent.
*  ARWILK  041022  LOV Change for Fields STD_JOB_ID,STD_JOB_REVISION. (Spec AMEC111 - Standard Jobs as PM Templates)
*  BUNILK  041026  Modified validations of material tab, added new validation to supply code, modified new method of material tab
*  NIJALK  041029  Modified validate(), preDefine().
*  SHAFLK  040916  Bug 46621, Modified validations of PART_NO, CONDITION_CODE, PART_OWNERSHIP and OWNER. 
*  Chanlk  041105  Merged Bug 46621.
*  NIJALK  041105  Added field Std Job status to job tab. modified predefine(), validate().
*  NIJALK  041109  Added new field (check box) "Has Obsolete Jobs".
*  ARWILK  041110  Replaced getContents with printContents.
*  NIJALK  041112  Made the field "Employee ID" visible & read only in jobs tab.
*  NIJALK  041115  Replaced method Standard_Job_API.Get_Work_Description with Standard_Job_API.Get_Definition. 
*  NIJALK  041201  Added new parameters to method calls to Inventory_Part_In_Stock_API.Get_Inventory_Quantity.
*  Chanlk  041220  Call Id 120293. Remeoved the security Check for CUSTOMER_ORDER_PRICING_API.Get_Sales_Part_Price_Info.
*  NIJALK  050203  Modified validate(),preDefine().
*  NAMELK  050224  Merged Bug 48035 manually.
*  DIAMLK  050228  Replaced the field Pre_Posting_Id by Pre_Accounting_Id.(IID AMEC113 - Mandatory Pre-posting)
*  NIJALK  050302  Added new methods deleteITEM2(), saveReturnITEM2().Modified LOV for ITEM2_CONTRACT.
*  DIAMLK  050310  Bug ID:122509 - Modified the method okFind().
*  NIJALK  050405  Call 123040: Modified detchedPartList(),printContents(),adjust().
*  NIJALK  050405  Call 123081: Modified manReserve().
*  NIJALK  050511  Bug 123677: Added RMB "Project Activity Info".
*  NIJALK  050519  Modified availDetail(),preDefine().
*  DiAmlk  050613  Bug ID:124832 - Renamed the RMB Available to Reserve to Inventory Part in Stock... and
*                  modified the method availtoreserve.
*  SHAFLK  050505  Bug 51049, Modified issueFromInvent() and reserve().
*  NIJALK  050617  Merged bug 51049.
*  SHAFLK  050425  Bug 50830, Used mgr.translateJavaScript() method to get the correctly translated strings in JavaScript functions.
*  NIJALK  050617  Merged bug 50830. 
*  NIJALK  050729  Bug 125921, Modified adjust().
*  NIJALK  050801  Bug 126080, Modified issueFromInvent(),reserve().
*  NIJALK  050801  Bug 126081, Modified adjust().
*  NIJALK  050808  Bug 126177: Modified perform().
*  NIJALK  050811  Bug 126137, Modified availtoreserve().
*  AMNILK  051018  Bug 127777. Modified method connectToActivity().Remove calling okFind().
*                  Added new method okFindITEMConnected(String wo_no).
*  NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile).
*  ERALLK  051214  Bug 54280. Added column 'Quantity Available'.Modified the validate() function.
*  NIJALK  051227  Merged bug 54280.
*  NIJALK  060109  Changed DATE format to compatible with data formats in PG19.
*  NIJALK  060131  Call 132030: Added warning msg when finishing WO. Modified printContents(), finish(),run(). 
*                  Added new methods CheckAllMaterialIssued(),finish1().
*  NIJALK  060210  Call 133546: Renamed "STANDARD_INVENTORY" with "INVENT_ORDER".
*  NIJALK  060213  Call 132956: Modified projectActivityInfo().
*  NIJALK  060215  Call 133917: Set field CUSTOMER_NO read only.
*  NEKOLK  060223  Call 135167: Modified adjust() and run() .
*  NEKOLK  060223  Call 135165: Modified run() .
*  SULILK  060223  Call 135149: Modified okFind(),preDefine() .
*  SULILK  060223  Call 135151: Modified printContents() .
*  NIJALK  060223  Call 135198: Modified GetInventoryQuantity() & manReserve().
*  SULILK  060224  Call 135200: Modified run(),preDefine(),actionSecurityCheck(),disableCommands(). Renamed "managefixedPrice()" in to "manageSalesRevenues()"
*  NIJALK  060224  Call 135160: Modified validation to QTY,CATALOG_NO. Modified newRowITEM1().
*  ASSALK  060224  Call 135398: Added method saveReturnITEM1(). Modified adjust().
*  SULILK  060228  Call 135197: Modified methods preDefine(), setValuesInMaterials() and validate()
*  THWILK  060306  Call 136049: Modified methods preDefine(), validate() and validateItem1RoleCode(). Added javascript method lovItem1RoleCode.
*  RANFLK  060306  Call 136359: Modified method manReserve().
*  THWILK  060307  Fixed Call136568,Modified predefine().
*  SULILK  060309  Call 136647: Modified adjust(),run(),preDefine(),actionSecurityCheck().
*  THWILK  060310  Call 136653, Modified authorize(),authorizeAll() and authorizeCorrect().
*  NIJALK  060311  Call 136824: Modified activatePostings(),run().
*  SULILK  060317  NOIID: Modified preDefine() and added saveReturnITEM5().
*  NEKOLK  060509  Bug ID 57724,Set ROLE_CODE as edditable fld.
* -----------------------------------------------------------------------------------------
*  NIJALK  060508  Bug 56726, Modified run().
*  CHODLK  060510  Bug 56390, Modified methods faultReport,serviceRequest.
*  NIJALK  060515  Bug 56688, Modified actionSecurityCheck() and issueFromInvent().
*  CHODLK  060524  Bug 56390-2, Modified methods faultReport,serviceRequest.
*  SHAFLK  060525  Bug 57598, Modified validation for PART_NO and setValuesInMaterials().
*  AMNILK  060629  Merged with SP1 APP7.
*  JEWILK  060803  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged Bug Id:58214.
*  ILSOLK  060817  Set the MaxLength of Address1 as 100.
*  JEWILK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  NAMELK  060905  Merged bug 58216.
*  DiAmlk  060920  Modified the method saveReturnITEM1.
*  AMNILK  060927  MTPR904 Hink tasks Request ID: 45082. Changed methods run(),authorize(),authorizeAll() & preDefine().
*		   Added new two methods authorizeByUserByDefaultAtRound() and getDefaultUserAtRound().    
*  AMDILK  061101  MTPR904: Request id 45082.  Modified the authorizeByUserDefaultAtRound() to notify 
                   when there is no valid employee attched to the logged in user and Add a new field "Creation Date" to the posting tab  
*  SHAFLK  061005  Bug 60938, Added new RMB "Activity Info..."
*  NAMELK  061107  Merged Bug 60938.
*  AMNILK  061110  MEPR604: Original Source info in voucher rows.Modified adjust().
*  SHAFLK  061222  Bug 61959, Added new RMB "Budget..." 
*  AMNILK  070202  Merged LCS Bug Id: 61959.  
*  SHAFLK  070116  Bug 62854, Modified validation for Part No.
*  AMNILK  070208  Merged LCS Bug 62854.  
*  BUNILK  070405  Implemented "MTIS907 New Service Contract - Services" changes. 
*  SHAFLK  070228  Bug 63812, Modified printPicList.
*  ILSOLK  070410  Merged Bug ID 63812.
*  AMDILK  070516  Call Id 144886: Modified saveReturnITEM1()
*  AMDILK  070516  Call Id 144894: set the customer no to be fetched from the view
*  CHANLK  070517  Call 144491 Added Tranfered to mobile check box in preDefine()
*  AMDILK  070517  Call Id 144488: Modified preDefine()
*  AMDILK  070518  Call Id 144691: Inserted OBJSTATE to the buffer. Modified requisitions()
*  AMDILK  070518  Call Id 144887: Enable multiple row actions in time report tab
*  AMDILK  070518  Call Id 144561: Inserted "Site" to the permit tab
*  CHANLK  070522  Call 144650 Permit tab not refresh properly
*  CHANLK  070523  Call 144874 Page Refresh not working properly Modified completed() and NotCompleted().
*  ILSOLK  070524  Added duplicateRow() method.(Call ID 144919)
*  ASSALK  070523  Call ID 144544: Modified preDefine().
*  ASSALK  070524  Call ID 144551,144550. Modified preDefine(), getContents(), validate(), saveReturnItem6().
*  ASSALK  070529  Call ID 144543. Modified preDefine() changed field lengths.
*  ASSALK  070530  Call ID 144550,144551. added method deleteITEM6(). Modified okFindITEM2(),preDefine(),printContents(),printContents().
*                  Added confirmations to remove std job connections when delete a line connected to a std job.
*  AMDILK  070605  Call Id 144560: Inserted a format mask to the field "Permit Seq"
*  ASSALK  070605  Call ID 144540: Modified disconnectFromActivity(), connectToActivity().
*  AMDILK  070606  Call Id 144691: Enable the RMB "Release" in material tab when the wo status >= "Release"
*  AMDILK  070607  Call Id 145865: Inserted new service contract information; Contrat name, contract type, 
*                  line description and invoice type
*  AMDILK  070608  Call Id 144694: Modified okFindITEM5()
*  AMDILK  070614  Call Id 144694: Preserve the line information when navigate the last and first record
*  ILSOLK  070625  Eliminated SQL errors in web applications.
*  ILSOLK  070705  Added SQLInjection comments.
*  CHANLK  070710  Call 144600 set sales part site to read only in itemblk.
*  AMDILK  070711  Eliminated XSS security vulnerability 
*  BUNILK  070524  Bug 65048 Added format mask for PM_NO field.
*  CHANLK  070711  Merged bugId 65048
*  AMDILK  070711  Fetched the agreement informations from the given contrat id
*  AMDILK  070712  Eliminated XSS security vulnerability ( Inserted comments )
*  ILSOLK  070730  Eliminated LocalizationErrors. 
*  AMDILK  070731  Removed the scroll buttons of the parent when the child tabs are in new or edit modes
*  AMDILK  070803  Refreshed the permit tab after replacing the permit.  
*  AMDILK  070810  Call Id 147244: Fetched the wo site as the sales part site when entering a new material line
*  ASSALK  070910  CALL 148510, Modified preDefine(), issueFromInvent().
*  ASSALK  070911  Call 148513. Modified issueFromInvent().
*  ASSALK  070918  Call 148957. Modified run().
*  ASSALK  070918  Call 148958, Modified authorizeByUserDefaultAtRound().
*  SHAFLK  071108  Bug 67801, Modified validation for STD_JOB_ID.
* -----------------------------------------------------------------------------------------
* -----------------------APP7.5 SP1-------------------------------------
*   071214  ILSOLK     Bug Id 68773, Eliminated XSS.
*  SHAFLK  080130  Bug 70913, Removed visibility of Ref No, Phone No, Address field 1,2,3,4,5,6.
*  NIJALK  080202  Bug 66456, Modified validate(), availDetail(), getInventoryQuantity(), preDefine() and printContents().
*  SHAFLK  080130  Bug 70815, Modified run(), issue(), reserve(), preDefine() okFindITEM2() and okFindITEM5(). 
*  NIJALK  080306  Bug 72202, Modified okFindITEM5().
*  ARWILK  071130  Bug 66406, Added CONN_PM_NO, CONN_PM_REVISION, CONN_PM_JOB_ID.
*  SHAFLK  080616  Bug 74499, Added check for ORDER module in validations.
*  ILSOLK  080716  Bug 75401, Modified preDefine(),Added new fields "Current Position" and "Belongs to Object" into item0().
*  SHAFLK  080924  Bug 77304, Modified preDefine().
*  SHAFLK  081105  Bug 77824, Modified Validate().
*  SHAFLK  081121  Bug 78187, Modified issue().
*  SHAFLK  090206  Bug 80160, Modified preDefine().
*  CHANLK  090225  Bug 76767, Modified preDefine(), validate(), issue(), reserve().
*  CHANLK  090303  Bug 80177, Added new field 'Note' to the General group.
*  IMGULK  090312  Bug 77280, Modified authorizeAll() and changed condition SIGNATURE to WORK_ORDER_BOOK_STATUS. 
*  NEKOLK  090407 Bug 81894, made the field 'Note' as a text field.
*  NEKOLK  190410 Bug 80964,  modified okFind(),countFind(),search(),preDefine(), added workDone(), reported(),
*  SHAFLK  090630  Bug 82543  Modified detchedPartList(). 
*  HARPLK  090708  Bug 84436, Modified preDefine().
*  CHARLK  090806  Bug 84119, change method quickreport().
*  SHAFLK  090921  Bug 85901, Modified preDefine().
*  SUIJLK  090922  Bug 81023, Modified preDefine(), saveReturnITEM1(), deleteITEM1() and added method saveNewITEM1().
*  NIJALK  091106  Bug 85761, Modified preDefine().
*  NIJALK  100717  Bug 87741, Modified preDefine().
*  NIJALK  100719  Bug 89399, Modified preDefine(), printContents().
*  SaFalk  100731  Bug 90330, Modified validate().
*  SaFalk  100807  Bug 89703, Modified deleteITEM6().
* -----------------------------------------------------------------------
*/                               

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import java.util.*;

public class ActiveRound extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveRound");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPContext ctx;
    private ASPHTMLFormatter fmt;

    private ASPBlock prntblk;
    private ASPRowSet printset;

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

    private ASPBlock itemblk;
    private ASPRowSet itemset;
    private ASPCommandBar itembar;
    private ASPTable itemtbl;
    private ASPBlockLayout itemlay;

    private ASPBlock itemblk6;
    private ASPRowSet itemset6;
    private ASPCommandBar itembar6;
    private ASPTable itemtbl6;
    private ASPBlockLayout itemlay6;

    private ASPBlock eventblk1;
    private ASPRowSet eventset1;

    private ASPField f;
    private ASPBlock b;
    private ASPBlock ref1;
    private ASPBlock blkPost;
    private ASPTable dlgtbl;
    // 031210  ARWILK  Begin  (Replace blocks with tabs)
    private ASPTabContainer tabs;
    // 031210  ARWILK  End  (Replace blocks with tabs)

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================

    private String wo_No;
    private String row_Nos;
    private String getSelectedRows_;
    private String separator_;
    private String fmtdBuff;
    private String openCreRepNonSer;
    private String creRepNonSerPath;
    private ASPTransactionBuffer trans;
    private boolean multirow;
    private boolean multirow1;
    private boolean overview;
    private String activetab;
    private boolean bolNewRow;
    private boolean toOverview;
    private boolean satis;
    private boolean saveitem1;
    private boolean canPerform;
    private String lineitemno;
    private String manwono;
    private String qrystr;
    private String companyvar;
    private boolean isFind;
    private String comp;
    private String headCont;
    private String headComp;
    private String headWoNo;
    private String magFixed;
    private boolean comBarAct;
    private String woNo;
    private String woNoUnIs;
    private String nQtyLeft;
    private String partNo;
    private String sigval;
    private String creRepWO;
    private boolean matSingleMode;
    private boolean noReservation;
    private boolean noIssu;
    private String layout;
    private String hdrow;
    private String noteTextEntered;
    private ASPBuffer buff;
    private String head_command;
    private ASPTransactionBuffer secBuff;
    private ASPCommand cmd;
    private String val;
    private String company2;
    private String reportedById;
    private String reportInById;
    private ASPBuffer buf;
    private String strCustDesc;
    private String securityOk;
    private String strCatalogNo;
    private String strCatalogDesc;
    private String strRoleCode;
    private String strDesc;
    private String strOrgCode;
    private String strSig;
    private String strName;
    private String salesSecOk;
    private String roleSecOk;
    private double nBuyQtyDue;
    private double colListPrice;
    private String colPriceListNo;
    private double colDiscount;
    private double colAmountSales;
    private String colCmnt;
    private String colCatalogNo;
    private String colCatalogDesc;
    private String colSalesPartDesc;
    private double numSalesPartCost;
    private String colSalesPart;
    private String colLineDescription;
    private String colOrgCode;
    private String strCompany;
    private String sPermitType;
    private String sPermitDesc;
    private String strWorkOrder;
    private String strCost;
    private String strObj;
    private String strProj;
    private String strMeterial;
    private String strPersonal;
    private String strFixedPrice;
    private double nAmount;
    private String strExpenses;
    private String sPriceListNo2;
    private double nListPrice;
    private String strAgreement;
    private String strWorkOrderCost;
    private String sPriceListNo;
    private double nBaseUnitPrice;
    private String sPriceListNo1;
    private double nQty4;
    private String strClient3;
    private double nAgreementId;
    private double nQty;
    private double nList;
    private String sSignId;
    private String dueDate;
    private String nPreAccId;
    private String item2Contract;
    private String company;
    private String mchCode;
    private String item2Desc;
    private String item3Descr;
    private String signId;
    private String signName;
    private String intDestDesc;
    private String cataNo;
    private String cataDesc;
    private String sCodeA;
    private String sCodeB;
    private String sCodeC;
    private String sCodeD;
    private String sCodeF;
    private String sCodeE;
    private String sCodeG;
    private String sCodeH;
    private String sCodeI;
    private String sCodeJ;
    private String hasStruct;
    private String typeDesi;
    private double qtyOnHand;
    private String unitMeas;
    private String partDesc;
    private String spareId;
    private double nCost;
    private double nDiscount;
    private String nPriceListNo;
    private double planQty;
    private double nAmountCost;
    private double nCostTemp;
    private double nCostField;
    private double nSaleUnitPrice;
    private double nDiscountTemp;
    private double nListPriceNew;
    private double nCostAmt;
    private double nListPriceTemp;
    private int currrow;
    private ASPBuffer data;
    private ASPQuery q;
    private ASPBuffer retBuffer;
    private String ret_wo_no;
    private int headrowno;
    private String strWOCostType;
    private String strWOACcntType;
    private String strWOOrg;
    private String strWOSite;
    private String strcmnt;
    private String dateRequired;
    private String nPreAccoId;
    private String item2Cont;
    private String item2Company;
    private String mchDesc;
    private String sysDate;
    private int headsetRowNo;
    private int item2rowno;
    private int currHead;
    private int currrowItem2;
    private String strWorkOrdCost;
    private String strWorkAccount;
    private String scontract;
    private String sActiveFixedPrice;
    private String sNotFixedPrice;
    private double nSalesPriceVal;
    private double nQtyReqd;
    private double nSalesPriceAmnt;
    private ASPBuffer r;
    private int n;
    private int i;
    private String catalogNo;
    private String item1Contract;
    private String roleCode;
    private String item1OrgCode;
    private double numCost;
    private ASPBuffer row;
    private String spareCont;
    private double nPlanQty;
    private String cataCont;
    private String cusNo;
    private String agreeId;
    private String priceListNo;
    private String planLineNo;
    private String obj;
    private String calling_url;
    private String objstate;
    private String contract;
    private String wo_no;
    private int enabl10;
    private String code_a;
    private String code_b;
    private String code_c;
    private String code_d;
    private String code_e;
    private String code_f;
    private String code_g;
    private String code_h;
    private String code_i;
    private String code_j;
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
    private String scompany;
    private ASPBuffer newBuff;
    private String reportedInBy;
    private String sItemRealFDate;
    private String reportedInById;
    private ASPBuffer temp1;
    private String primary_key;
    private boolean flag;
    private int selectedrows;
    private String rownum;
    private int rows__;
    private String mform;
    private String catNo;
    private String bookSta;
    private String costTy;
    private String cusOrNo;
    private String sMaterial;
    private String sExpences;
    private String sExternal;
    private boolean enableF;
    private double nQtyAssign;
    private double nIssue;
    private int currRow;
    private String eventValItem;
    private ASPBuffer prePostBuffer;
    private ASPBuffer returnBuffer;
    private String orderNo;
    private String attr3;
    private String attr4;
    private String attr0;
    private ASPBuffer key;
    private int currrowItem;
    private String sStatusCodeReleased;
    private String bIssAllowed;
    private String dfStatus;
    private String canPerformPartSe;
    private String isAutoRepairable;
    private String isRepairable;
    private String bResAllowed;
    private double nQtyShort;
    private String strLableA;
    private String strLableB;
    private String strLableC;
    private String strLableD;
    private String strLableE;
    private String strLableF;
    private String strLableG;
    private String strLableH;
    private String strLableI;
    private String strLableJ;
    private String strBookingStatus;
    private String title;
    private String xx;
    private String yy;
    private String var1;
    private String var2;
    private String var3;
    private String var4;
    private String var5;
    private String var6;
    private String fldTitleEmpNo;
    private String fldTitleTestPointId;
    private String fldTitleItem4CatalogNo;
    private String fldTitleMchCode;
    private String fldTitleSignature;
    private String fldTitleRequisitionNo;
    private String lovTitleEmpNo;
    private String lovTitleTestPointId;
    private String lovTitleItem4CatalogNo;
    private String lovTitleMchCode;
    private String lovTitleSignature;
    private String lovTitleRequisitionNo;
    private String matLine;
    private String prtNo;
    private String cont;
    private String availDet;
    private String isSecure[];
    private int CurrRowNo;
    private String date_fmt;
    private String txt; 
    private String strConcatDesc;
    private double colSalesPartCost;
    private double colQty;  
    private double colAmount;
    private double nSalesPriceAmount; 
    private String colcAgreement;  
    private double listPrice;  
    private double qty;  
    private double salePriceAmt;
    private double nCostAmount;
    private String sRealFDateStr;
    private String sRealFDate;  
    private String attr1;  
    private String attr2; 
    private String hasSerialNum; 
    private double nCount;  
    private String enabled_events;
    private String wono;
    private String newWindowURL;
    private String queryStringVal;
    private boolean openFormInNewWin;
    private String sWindowHandle;
    private String sRowNo;
    private String singleRowNo;
    private boolean secCheck;
    private String objState;
    //Variables for security checking of actions
    private boolean ctxPreposting;      
    private boolean ctxRequisitions;
    private boolean ctxBudget;    
    private boolean ctxPrintWO;      
    private boolean ctxTransferToCusOrder;
    private boolean ctxCompleted;    
    private boolean ctxNotCompleted;    
    private boolean ctxFaultReport;     
    private boolean ctxServiceRequest;  
    private boolean ctxQuickReport;     
    private boolean ctxRoutePMAction;
    private boolean ctxPrePostHead;     
    private boolean ctxPrintPicList;    
    private boolean ctxSparePartObject;
    private boolean ctxObjStructure;    
    private boolean ctxDetchedPartList; 
    private boolean ctxReserve;      
    private boolean ctxManReserve;      
    private boolean ctxUnreserve;    
    private boolean ctxIssue;     
    private boolean ctxManIssue;  
    private boolean ctxAvailDetail;     
    private boolean ctxSupPerPart;      
    private boolean ctxMatReqUnissue;
    private boolean ctxPermitAttr;
    private boolean ctxAuthorize;    
    private boolean ctxAuthorizeAll;
    private boolean ctxAuthorizeCorrect;
    private boolean ctxSalesPartComp;
    private boolean ctxManageSalesRevenues;
    private boolean ctxHypCustInfo;
    private boolean ctxHypDocRef;
    private boolean ctxHypInvenPart;
    private boolean ctxAvailReserve;
    private boolean ctxMatReqIssued;
    private boolean bUnequalMatWo;
    private boolean bNoWoCust;
    private boolean bOpenNewWindow;
    private String urlString;
    private String newWinHandle;
    // 031210  ARWILK  End  (Replace links with RMB's)
    private String start;
    private String HeadWo_No;
    private String showMat;

    private ASPBuffer ret_data_buffer;
    private ASPBuffer temp;
    private String lout;
    private String issued;
    private String defaultUser = "";
    private boolean gotDefaultUser;
    private boolean returnedFmCorrectpostings = false;

    private boolean ctxIsAuthAllowed;
    private boolean ctxHasUnauthorized;
    private boolean ctxTransactionId;
    private boolean duplicateFlag;

    private String sConfirmMsg1 = "";
    private boolean bDeleteItem6 = false,bConfirmMsg1 = false;
    
    //===============================================================
    // Construction 
    //===============================================================
    public ActiveRound(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        wo_No = row_Nos;
        row_Nos = getSelectedRows_;
        getSelectedRows_ = separator_;
        separator_ = "";
        fmtdBuff =  "";
        openCreRepNonSer = creRepNonSerPath;
        creRepNonSerPath = "";
	duplicateFlag = false;

        ASPManager mgr = getASPManager();

        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();
        fmt = mgr.newASPHTMLFormatter();

        showMat = ctx.readValue("SHMAT","");

        multirow = ctx.readFlag("MULTIROW",false);
        multirow1 = ctx.readFlag("MULTIROW1",true);
        overview = ctx.readFlag("OVERVIEW",true);
        activetab = ctx.readValue("ACTIVETAB","0");
        bolNewRow= ctx.readFlag("NEWROW",false);
        toOverview = ctx.readFlag("TOOVW",false);
        satis = ctx.readFlag("SATIS",true);
        saveitem1 = ctx.readFlag("SAVEITEM1",false);
        canPerform = ctx.readFlag("CANPRF",false);
        wono = ctx.readValue("WONOV","");

        lineitemno = ctx.readValue("LINEITEMNO","");
        manwono = ctx.readValue("MANWONO","");
        qrystr = ctx.readValue("QRYSTR",""); 
        CurrRowNo = ctx.readNumber("CTXCURRO",CurrRowNo);

        date_fmt = mgr.getFormatMask("Date",true);

        companyvar =  ctx.readValue("COMPANYVAR",""); 

        isFind = ctx.readFlag("ISFIND",false);
        comp = ctx.readValue("COMP");
        headCont = ctx.readValue("HEADCONT","");
        headComp = ctx.readValue("HEADCOMP","");
        headWoNo = ctx.readValue("HEADWONO","");
        magFixed = ctx.readValue("MAGFIXED","FALSE");

        comBarAct = ctx.readFlag("COMBARACT",false);
        woNo = ctx.readValue("WONOU","");
        woNoUnIs = ctx.readValue("WONOUNIS","");
        nQtyLeft = ctx.readValue("NQTYLEFT","");  
        prtNo = ctx.readValue("PARTNO","");
        sigval = ctx.readValue("SIGVAL","");
        creRepWO = ctx.readValue("AUTOREP","FALSE");
        matSingleMode = ctx.readFlag("MATSINGLEMODE",false);
        noReservation = ctx.readFlag("NORESERV",true);
        noIssu = ctx.readFlag("NOISS",true);

        prtNo = ctx.readValue("PRTNO","");
        cont = ctx.readValue("CONT","");
        orderNo = ctx.readValue("ORDERNO","");
        partNo = ctx.readValue("PARTNO","");
        queryStringVal = ctx.readValue("CTXQUERYSTRINGVAL","");
        sRowNo = ctx.readValue("CTXSROWNO","");
        secCheck = ctx.readFlag("SECCHECK",secCheck);
        //Variables for security checking of actions
        ctxPreposting = ctx.readFlag("CTXPREPOSTING",ctxPreposting);      
        ctxRequisitions = ctx.readFlag("CTXREQUISITIONS",ctxRequisitions);
        ctxBudget =  ctx.readFlag("CTXBUDGET",ctxBudget);   
        ctxPrintWO = ctx.readFlag("CTXPRINTWO",ctxPrintWO);         
        ctxTransferToCusOrder = ctx.readFlag("CTXTRANSFERTOCUSORDER",ctxTransferToCusOrder);
        ctxCompleted = ctx.readFlag("CTXCOMPLETED",ctxCompleted);      
        ctxNotCompleted = ctx.readFlag("CTXNOTCOMPLETED",ctxNotCompleted);      
        ctxFaultReport = ctx.readFlag("CTXFAULTREPORT",ctxFaultReport);      
        ctxServiceRequest = ctx.readFlag("CTXSERVICEREQUEST",ctxServiceRequest);   
        ctxQuickReport = ctx.readFlag("CTXQUICKREPORT",ctxQuickReport);      
        ctxRoutePMAction = ctx.readFlag("CTXROUTEPMACTION",ctxRoutePMAction);
        ctxPrePostHead = ctx.readFlag("CTXPREPOSTHEAD",ctxPrePostHead);      
        ctxPrintPicList = ctx.readFlag("CTXPRINTPICLIST",ctxPrintPicList);   
        ctxSparePartObject = ctx.readFlag("CTXSPAREPARTOBJECT",ctxSparePartObject);       
        ctxObjStructure = ctx.readFlag("CTXOBJSTRUCTURE",ctxObjStructure);   
        ctxDetchedPartList = ctx.readFlag("CTXDETCHEDPARTLIST",ctxDetchedPartList);       
        ctxReserve = ctx.readFlag("CTXRESERVE",ctxReserve);         
        ctxManReserve = ctx.readFlag("CTXMANRESERVE",ctxManReserve);      
        ctxUnreserve = ctx.readFlag("CTXUNRESERVE",ctxUnreserve);      
        ctxIssue = ctx.readFlag("CTXISSUE",ctxIssue);   
        ctxManIssue = ctx.readFlag("CTXMANISSUE",ctxManIssue);         
        ctxAvailDetail = ctx.readFlag("CTXAVAILDETAIL",ctxAvailDetail);      
        ctxSupPerPart = ctx.readFlag("CTXSUPPERPART",ctxSupPerPart);      
        ctxMatReqUnissue = ctx.readFlag("CTXMATREQUNISSUE",ctxMatReqUnissue); 
        ctxPermitAttr = ctx.readFlag("CTXPERMITATTR",ctxPermitAttr);
        ctxAuthorize = ctx.readFlag("CTXAUTHORIZE",ctxAuthorize);               
        ctxAuthorizeAll = ctx.readFlag("CTXAUTHORIZEALL",ctxAuthorizeAll);         
        ctxAuthorizeCorrect = ctx.readFlag("CTXAUTHORIZECORRECT",ctxAuthorizeCorrect); 
        ctxSalesPartComp = ctx.readFlag("CTXSALESPARTCOMP",ctxSalesPartComp);       
        ctxManageSalesRevenues = ctx.readFlag("CTXMANAGESALESREVENUES",ctxManageSalesRevenues);
        ctxHypCustInfo = ctx.readFlag("CTXHYPCUSTINFO",ctxHypCustInfo);
        ctxHypDocRef = ctx.readFlag("CTXHYPDOCREF",ctxHypDocRef);
        ctxHypInvenPart = ctx.readFlag("CTXHYPINVENPART",ctxHypInvenPart);
        ctxAvailReserve = ctx.readFlag("CTXAVAILRESERVE",ctxAvailReserve);
        ctxMatReqIssued = ctx.readFlag("CTXMATREQISSUED",ctxMatReqIssued);

        ctxIsAuthAllowed = ctx.readFlag("CTXISAUTHALLOWED", false);
        ctxHasUnauthorized = ctx.readFlag("CTXALLAUTH", false);

        ctxTransactionId = ctx.readFlag("CTXTRANSACTIONID", ctxTransactionId);
        if (mgr.commandBarActivated())
        {
            eval(mgr.commandBarFunction());
            if ("ITEM0.SaveReturn".equals(mgr.readValue("__COMMAND")))
            {
                headset.refreshAllRows();
                itemset0.refreshRow();
            }

            if ("ITEM5.SaveReturn".equals(mgr.readValue("__COMMAND"))){
                itemset.refreshRow();                       
                setValuesInMaterials();
            }
                
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
        {
            validate();
        }
	//Bug 82543, Start
	else if ( !mgr.isEmpty(mgr.readValue("REFRESH_FLAG")))
	{
	    refresh();

	    if (tabs.getActiveTab() == 3)
		refreshForm();
	}
	//Bug 82543, End
        else if (!mgr.isEmpty(mgr.getQueryStringValue("REFRESHFLAG"))) 
        {
           refreshBlocks();
        }           
        else if (!mgr.isEmpty(mgr.getQueryStringValue("POP_ITEM")))
        {
            mgr.setPageExpiring();
            trans.clear();
            layout = ctx.getGlobal("LAYOUT");
            hdrow = ctx.getGlobal("HDROW");
            headlay.setLayoutMode(toInt(layout));

            ctx.setGlobal("LAYOUT","");
            ctx.setGlobal("HDROW","");
            qrystr = "";

            okFind(); 
            headset.goTo(toInt(hdrow));
            trans.clear();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("OKMANISSUE")) && !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();  
            activateMaterial();

            if (headset.countRows() != 1)
            {
                okFindITEM0();
                okFindITEM1();
                okFindITEM2();
                okFindITEM3();
                okFindITEM4();
            }

            matSingleMode = true;
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("OKMANRESERVE")) && !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();   

            if (headset.countRows() != 1)
            {
                okFindITEM0();
                okFindITEM1();
                okFindITEM2();
                okFindITEM3();
                okFindITEM4();
            }

            matSingleMode = true;
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("OKUNISSUE")) && !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();   

            if (headset.countRows() != 1)
            {
                int curr_head = Integer.parseInt(ctx.getGlobal("HEADCURR"));
                headset.goTo(curr_head);   

                okFindITEM0();
                okFindITEM1();
                okFindITEM2();
                okFindITEM3();
                okFindITEM4();

                int item2_head = Integer.parseInt(ctx.getGlobal("ITEM2CURR"));
                itemset2.goTo(item2_head);   
                int item_head = Integer.parseInt(ctx.getGlobal("ITEMCURR"));
                itemset.goTo(item_head);   
            }

            matSingleMode = true;
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("OKMANUNRESERVE")) && !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();   

            if (headset.countRows() != 1)
            {
                okFindITEM0();
                okFindITEM1();
                okFindITEM2();
                okFindITEM3();
                okFindITEM4();
            }

            matSingleMode = true;
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("NOTETEXTENT")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();   

            if (headset.countRows() != 1)
            {
                okFindITEM0();
                okFindITEM1();
                okFindITEM2();
                okFindITEM3();
                okFindITEM4();
            }

            if (itemset2.countRows() > 0)
            {

                noteTextEntered = mgr.readValue("NOTETEXTENT","");
                buff = itemset2.getRow();
                buff.setValue("SNOTETEXT",noteTextEntered);
                itemset2.setRow(buff);
            }
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
        {
            head_command="none"; 
            showMat = mgr.readValue("SHOWMAT","");

            if (mgr.isEmpty(showMat))
                headlay.setLayoutMode(headlay.SINGLE_LAYOUT);

            okFind();

            // This section is executed only when returning from the action 'Manage Fixed Price Postings'
            if (!mgr.isEmpty(mgr.getQueryStringValue("SINGLE_ROW_NO")))
            {
                if (headset.countRows() > 0)
                {
                    if (itemset4.countRows() > 0)
                    {
                        itemset4.goTo(toInt(mgr.getQueryStringValue("SINGLE_ROW_NO")));
                        itemlay4.setLayoutMode(itemlay4.SINGLE_LAYOUT);
                    }
                }
            }

            // This section is executed only when returning from the action 'Permit Attributes...'
            if (!mgr.isEmpty(mgr.getQueryStringValue("PERMIT_SINGLE_ROW_NO")))
            {
                if (headset.countRows() > 0)
                {
                    if (itemset3.countRows() > 0)
                    {
                        itemset3.goTo(toInt(mgr.getQueryStringValue("PERMIT_SINGLE_ROW_NO")));
                        itemlay3.setLayoutMode(itemlay3.SINGLE_LAYOUT);
                    }
                }
            }

            if (!mgr.isEmpty(mgr.getQueryStringValue("MANAGEREV")))
                this.activatePostings();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
        {

            if (!mgr.isEmpty(mgr.getQueryStringValue("START")))
            {
                headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
                start=mgr.getQueryStringValue("START");
                wono=mgr.getQueryStringValue("WONO");
                head_command="none";
		returnedFmCorrectpostings = true;
                activatePostings();
		returnedFmCorrectpostings = false;
		trans.clear();
            }

            okFind();
        }

        else if (mgr.dataTransfered())
        {
	    okFind();

        }
        else if ("CCCC".equals(mgr.readValue("BUTTONVAL")))
        {
            issued="FALSE";
            finish1();
        }
        else
        {
            startup();
        }    
            
        adjust();

        ctx.writeFlag("MULTIROW",multirow);
        ctx.writeFlag("MULTIROW1",multirow1);
        ctx.writeFlag("OVERVIEW",overview);
        ctx.writeValue("ACTIVETAB",activetab);
        ctx.writeFlag("NEWROW",bolNewRow);
        ctx.writeFlag("TOOVW",toOverview);
        ctx.writeFlag("SATIS",true);
        ctx.writeValue("COMPANYVAR",companyvar);
        ctx.writeNumber("CTXCURRO",CurrRowNo);  
        ctx.writeValue("QRYSTR",qrystr); 
        ctx.writeFlag("MATSINGLEMODE",matSingleMode);

        ctx.writeValue("PRTNO",prtNo);
        ctx.writeValue("CONT",cont);
        ctx.writeValue("WONOUNIS",woNoUnIs);
        ctx.writeValue("ORDERNO",orderNo);
        ctx.writeValue("PARTNO",partNo);
        ctx.writeValue("CTXQUERYSTRINGVAL",queryStringVal);
        ctx.writeValue("CTXSROWNO",sRowNo);   
        ctx.writeFlag("SECCHECK",secCheck);
        //Variables for security checking of actions
        ctx.writeFlag("CTXPREPOSTING",ctxPreposting);
        ctx.writeFlag("CTXREQUISITIONS",ctxRequisitions);
        ctx.writeFlag("CTXBUDGET",ctxBudget);
        ctx.writeFlag("CTXPRINTWO",ctxPrintWO);
        ctx.writeFlag("CTXTRANSFERTOCUSORDER",ctxTransferToCusOrder);
        ctx.writeFlag("CTXCOMPLETED",ctxCompleted);     
        ctx.writeFlag("CTXNOTCOMPLETED",ctxNotCompleted);     
        ctx.writeFlag("CTXFAULTREPORT",ctxFaultReport);    
        ctx.writeFlag("CTXSERVICEREQUEST",ctxServiceRequest); 
        ctx.writeFlag("CTXQUICKREPORT",ctxQuickReport);    
        ctx.writeFlag("CTXROUTEPMACTION",ctxRoutePMAction);
        ctx.writeFlag("CTXPREPOSTHEAD",ctxPrePostHead);
        ctx.writeFlag("CTXPRINTPICLIST",ctxPrintPicList);
        ctx.writeFlag("CTXSPAREPARTOBJECT",ctxSparePartObject);
        ctx.writeFlag("CTXOBJSTRUCTURE",ctxObjStructure);
        ctx.writeFlag("CTXDETCHEDPARTLIST",ctxDetchedPartList);
        ctx.writeFlag("CTXRESERVE",ctxReserve);
        ctx.writeFlag("CTXMANRESERVE",ctxManReserve);
        ctx.writeFlag("CTXUNRESERVE",ctxUnreserve);
        ctx.writeFlag("CTXISSUE",ctxIssue);
        ctx.writeFlag("CTXMANISSUE",ctxManIssue);
        ctx.writeFlag("CTXAVAILDETAIL",ctxAvailDetail);
        ctx.writeFlag("CTXSUPPERPART",ctxSupPerPart);
        ctx.writeFlag("CTXMATREQUNISSUE",ctxMatReqUnissue);
        ctx.writeFlag("CTXPERMITATTR",ctxPermitAttr);
        ctx.writeFlag("CTXAUTHORIZE",ctxAuthorize);                    
        ctx.writeFlag("CTXAUTHORIZEALL",ctxAuthorizeAll);              
        ctx.writeFlag("CTXAUTHORIZECORRECT",ctxAuthorizeCorrect);      
        ctx.writeFlag("CTXSALESPARTCOMP",ctxSalesPartComp);            
        ctx.writeFlag("CTXMANAGESALESREVENUES",ctxManageSalesRevenues);
        ctx.writeFlag("CTXHYPCUSTINFO",ctxHypCustInfo);
        ctx.writeFlag("CTXHYPDOCREF",ctxHypDocRef);
        ctx.writeFlag("CTXHYPINVENPART",ctxHypInvenPart);
        ctx.writeFlag("CTXAVAILRESERVE",ctxAvailReserve);
        ctx.writeFlag("CTXMATREQISSUED",ctxMatReqIssued);

        ctx.writeFlag("CTXISAUTHALLOWED", ctxIsAuthAllowed);
        ctx.writeFlag("CTXALLAUTH", ctxHasUnauthorized);

        ctx.writeFlag("CTXTRANSACTIONID", ctxTransactionId);

        // 031210  ARWILK  Begin  (Replace blocks with tabs)
        tabs.saveActiveTab();
        // 031210  ARWILK  End  (Replace blocks with tabs)
    }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
// This function is used to check security status for methods which
// are in modules Order,Purch and Invent .This function is called before
// the required method is performed.  -NUPE,21-07-2000

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


    public void startup()
    {
        ASPManager mgr = getASPManager();

        cmd = trans.addCustomFunction("CONTRA","User_Default_API.Get_Contract","CONTRACT");

        trans = mgr.submit(trans);  
        companyvar = trans.getValue("COMPAN/DATA/COMPANY");  
    }


    public void getcompany()
    {
        ASPManager mgr = getASPManager();      

        trans.clear();

        cmd = trans.addCustomFunction("DEFCO","User_Default_API.Get_Contract","CONTRACT");

        cmd = trans.addCustomFunction("COMPA","Site_API.Get_Company","COMPANY");
        cmd.addReference("CONTRACT", "DEFCO/DATA");

        trans = mgr.perform(trans);

        companyvar = trans.getValue("COMPA/DATA/COMPANY");   
    }

    public String convertToString(int max)
    {
        if (max ==0)
            return("'" + ret_data_buffer.getBufferAt(max).getValueAt(0) + "'");
        else
            return("'" + ret_data_buffer.getBufferAt(max).getValueAt(0) + "'," + convertToString(max-1));
    }


//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();

        val = mgr.readValue("VALIDATE");
        isSecure =  new String[15];
        //Bug 43249, start
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
        //Bug 76767, Start
        String sAvailable = "AVAILABLE";
        String sNettable = "NETTABLE";
        //Bug 76767, End

        cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
        cmd.addParameter("PART_OWNERSHIP",mgr.readValue("PART_OWNERSHIP"));
        trans = mgr.validate(trans);
        String sClientOwnershipDefault = trans.getValue("VALIDOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");
        trans.clear();
        //Bug 43249, end

        String sClientOwnershipConsignment = "CONSIGNMENT"  ;


        if ("CONTRACT".equals(val))
        {
            cmd = trans.addCustomFunction("COMPA","Site_API.Get_Company","COMPANY2");
            cmd.addParameter("CONTRACT");          

            trans = mgr.validate(trans);

            company2 = trans.getValue("COMPA/DATA/COMPANY2");

            txt = (mgr.isEmpty(company2) ? "" : company2)+ "^";

            mgr.responseWrite(txt);
        }
        else if ("REPORTED_BY".equals(val))
        {
            cmd = trans.addCustomFunction("REPORTEDBYID","Company_Emp_API.Get_Max_Employee_Id","REPORTED_BY_ID");
            cmd.addParameter("COMPANY2");         
            cmd.addParameter("REPORTED_BY"); 

            trans = mgr.validate(trans);

            reportedById = trans.getValue("REPORTEDBYID/DATA/REPORTED_BY_ID");

            txt = (mgr.isEmpty(reportedById) ? "" : (reportedById))+ "^";

            mgr.responseWrite(txt);
        }
        else if ("REPORT_IN_BY".equals(val))
        {
            cmd = trans.addCustomFunction("REPORTINBYID","Company_Emp_API.Get_Max_Employee_Id","REPORT_IN_BY_ID");
            cmd.addParameter("COMPANY2");
            cmd.addParameter("REPORT_IN_BY");          

            trans = mgr.validate(trans);

            reportInById = trans.getValue("REPORTINBYID/DATA/REPORT_IN_BY_ID");

            txt = (mgr.isEmpty(reportInById) ? "" : reportInById)+ "^";

            mgr.responseWrite(txt);
        }
        else if ("REAL_S_DATE".equals(val))
        {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("REAL_S_DATE",mgr.readValue("REAL_S_DATE",""));
            mgr.responseWrite(buf.getFieldValue("REAL_S_DATE") +"^" );
        }
        else if ("PLAN_S_DATE".equals(val))
        {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("PLAN_S_DATE",mgr.readValue("PLAN_S_DATE",""));
            mgr.responseWrite(buf.getFieldValue("PLAN_S_DATE") +"^" );
        }
        else if ("PLAN_F_DATE".equals(val))
        {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("PLAN_F_DATE",mgr.readValue("PLAN_F_DATE",""));
            mgr.responseWrite(buf.getFieldValue("PLAN_F_DATE") +"^" );
        }
        else if ("DATE_REQUIRED".equals(val))
        {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("DATE_REQUIRED",mgr.readValue("DATE_REQUIRED",""));
            mgr.responseWrite(buf.getFieldValue("DATE_REQUIRED") +"^" );
        }
        else if ("CUSTOMER_NO".equals(val))
        {
            cmd = trans.addCustomFunction("CUSTDESC","CUSTOMER_INFO_API.Get_Name","CUSTOMERDESCRIPTION");
            cmd.addParameter("CUSTOMER_NO");

            trans = mgr.validate(trans);

            strCustDesc = trans.getValue("CUSTDESC/DATA/CUSTOMERDESCRIPTION");
            txt = (mgr.isEmpty(strCustDesc) ? "" : strCustDesc)+ "^";

            mgr.responseWrite(txt);
        }
        else if ("INSPECTION_NOTE".equals(val))
        {
            String gen_note_yes_="";
            String gen_note_no_="";
            String gen_note_="";

            cmd = trans.addCustomFunction("GENYES","Generate_Note_API.Decode('1')","GENERATE_NOTE");
            cmd = trans.addCustomFunction("GENNO","Generate_Note_API.Decode('2')","GENERATE_NOTE");

            trans = mgr.validate(trans);

            gen_note_yes_ = trans.getValue("GENYES/DATA/GENERATE_NOTE");
            gen_note_no_ = trans.getValue("GENYES/DATA/GENERATE_NOTE");

            if (!mgr.isEmpty(mgr.readValue("INSPECTION_NOTE")))
                gen_note_ = gen_note_yes_;
            else
                gen_note_ = gen_note_no_;
            txt = (mgr.isEmpty(gen_note_) ? "" : gen_note_)+ "^";

            mgr.responseWrite(txt);
        }
        else if ("EMP_NO".equals(val))
        {
            securityOk = "";
            secBuff = mgr.newASPTransactionBuffer();
            secBuff.addSecurityQuery("SALES_PART");

            secBuff = mgr.perform(secBuff);

            if (secBuff.getSecurityInfo().itemExists("SALES_PART") && mgr.isModuleInstalled("ORDER"))
                securityOk = "TRUE";

            cmd = trans.addCustomFunction("PARA1", "Employee_Role_API.Get_Default_Role", "ITEM1_ROLE_CODE" );
            cmd.addParameter("ITEM1_COMPANY",mgr.readValue("ITEM1_COMPANY",""));
            cmd.addParameter("EMP_NO");

            cmd = trans.addCustomFunction("PARA2", "Role_API.Get_Description", "CMNT" );
            cmd.addReference("ITEM1_ROLE_CODE", "PARA1/DATA");

            cmd = trans.addCustomFunction("PARA3", "Employee_API.Get_Organization", "DEPART" );
            cmd.addParameter("ITEM1_COMPANY");
            cmd.addParameter("EMP_NO");

            cmd = trans.addCustomFunction("PARA4", "Company_Emp_API.Get_Person_Id", "EMP_SIGNATURE" );
            cmd.addParameter("ITEM1_COMPANY");
            cmd.addParameter("EMP_NO");

            cmd = trans.addCustomFunction("PARA5", "Person_Info_API.Get_Name", "NAME" );
            cmd.addReference("EMP_SIGNATURE", "PARA4/DATA");

            if ("TRUE".equals(securityOk))
            {
                cmd = trans.addCustomFunction("CATALO","Role_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addReference("ITEM1_ROLE_CODE", "PARA1/DATA");

                cmd = trans.addCustomFunction("CATDESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addReference("CATALOG_NO", "CATALO/DATA");
            }

            trans = mgr.validate(trans);       

            if ("TRUE".equals(securityOk))
            {
                strCatalogNo = trans.getValue("CATALO/DATA/CATALOG_NO");
                strCatalogDesc= trans.getValue("CATDESC/DATA/SALESPARTCATALOGDESC");
            }
            else
            {
                strCatalogNo = "";
                strCatalogDesc = "";
            }

            strRoleCode = trans.getValue("PARA1/DATA/ROLE_CODE");        
            strDesc = trans.getValue("PARA2/DATA/CMNT");
            strOrgCode = trans.getValue("PARA3/DATA/DEPART");
            strSig = trans.getValue("PARA4/DATA/EMP_SIGNATURE");
            strName = trans.getValue("PARA5/DATA/NAME");
            strConcatDesc = strName + "," + strOrgCode + "("+ strDesc +")"; 

            txt = (mgr.isEmpty(strRoleCode)? "" : strRoleCode) +"^"+ (mgr.isEmpty(strSig)? "" : strSig) +"^"+ (mgr.isEmpty(strName)? "" : strName) +"^"+ (mgr.isEmpty(strOrgCode)? "" : strOrgCode) +"^"+ (mgr.isEmpty(strConcatDesc)? "" : strConcatDesc) +"^"+ (mgr.isEmpty(strCatalogNo)? "" : strCatalogNo) +"^"+ (mgr.isEmpty(strCatalogDesc)? "" : strCatalogDesc) +"^" ;

            mgr.responseWrite(txt);
        }
        else if ("QTY".equals(val))
        {
            salesSecOk = "";
            roleSecOk  = "";
            securityOk = "";
            secBuff = mgr.newASPTransactionBuffer();
            secBuff.addSecurityQuery("SALES_PART");
            secBuff.addSecurityQuery("ROLE_SALES_PART");
            secBuff.addSecurityQuery("Company_Finance_API","Get_Currency_Code");
            secBuff.addSecurityQuery("Customer_Order_Pricing_API","Get_Valid_Price_List,Get_Sales_Part_Price_Info");

            secBuff = mgr.perform(secBuff);

            if (secBuff.getSecurityInfo().itemExists("SALES_PART") && mgr.isModuleInstalled("ORDER"))
                salesSecOk = "TRUE";

            if (secBuff.getSecurityInfo().itemExists("ROLE_SALES_PART") && mgr.isModuleInstalled("ORDER"))
                roleSecOk = "TRUE";

            if (secBuff.getSecurityInfo().itemExists("SALES_PART") && secBuff.getSecurityInfo().itemExists("Company_Finance_API.Get_Currency_Code") && secBuff.getSecurityInfo().itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List") && mgr.isModuleInstalled("ORDER"))
                securityOk = "TRUE";

            if ("TRUE".equals(salesSecOk))
            {
                cmd = trans.addCustomFunction("PARTCOST","Sales_Part_API.Get_Cost","COST1");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addParameter("CATALOG_NO");

                cmd = trans.addCustomFunction("CST", "Work_Order_Planning_Util_Api.Get_Cost", "COST3" );
                cmd.addParameter("ITEM1_ORG_CODE");
                cmd.addParameter("ITEM1_ROLE_CODE");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addParameter("CATALOG_NO");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addParameter("EMP_NO",mgr.readValue("EMP_NO"));
                cmd.addParameter("DUMMY","TRUE");
            }
            else
            {
                cmd = trans.addCustomFunction("CST", "Work_Order_Planning_Util_Api.Get_Cost", "COST3" );
                cmd.addParameter("ITEM1_ORG_CODE");
                cmd.addParameter("ITEM1_ROLE_CODE");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addParameter("DUMMY","");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addParameter("EMP_NO",mgr.readValue("EMP_NO"));
                cmd.addParameter("DUMMY","TRUE");
            }

            double qty=mgr.readNumberValue("QTY");

            if (isNaN(qty))
                qty=0;

            if (qty!=0)
            {
                if ("TRUE".equals(roleSecOk) &&  ! mgr.isEmpty(mgr.readValue("CATALOG_NO","")))
                {
                    double spc=mgr.readNumberValue("SALESPARTCOST");

                    if (isNaN(spc))
                        spc=0;

                    if (isNaN(colSalesPartCost))
                        colSalesPartCost=0;

                    colQty = mgr.readNumberValue("QTY");

                    if (isNaN(colQty))
                        colQty=0;

                    colAmount = colSalesPartCost * colQty;
                }

                cmd = trans.addCustomCommand("CALAMOUNT","Work_Order_Coding_API.Calc_Amount");
                cmd.addParameter("AMOUNT");
                cmd.addParameter("QTY");
                cmd.addParameter("ITEM1_ORG_CODE");
                cmd.addParameter("ITEM1_ROLE_CODE");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addParameter("EMP_NO",mgr.readValue("EMP_NO"));
                cmd.addParameter("DUMMY","TRUE");
            }

            if ("TRUE".equals(securityOk))
            {
                double qti=mgr.readNumberValue("QTY_TO_INVOICE");

                if (isNaN(qti))
                    qti=0;

                if (qti!=0)
                {
                    nBuyQtyDue = mgr.readNumberValue("QTY_TO_INVOICE");

                    if (isNaN(nBuyQtyDue))
                        nBuyQtyDue=0;
                }

                if (nBuyQtyDue==0)
                    nBuyQtyDue = 1;

                cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("BASE_PRICE");
                cmd.addParameter("SALE_PRICE");
                cmd.addParameter("DISCOUNT");
                cmd.addParameter("CURRENCY_RATE");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addParameter("CATALOG_NO");
                cmd.addParameter("ITEM1_CUSTOMER_NO");
                cmd.addParameter("AGREEMENT_ID");
                cmd.addParameter("PRICE_LIST_NO");
                cmd.addParameter("QTY1",mgr.getASPField("QTY1").formatNumber(nBuyQtyDue));
            }

            trans = mgr.validate(trans);

            colSalesPartCost = 0;

            colSalesPartCost = trans.getNumberValue("CST/DATA/COST3");
            if (isNaN(colSalesPartCost))
                colSalesPartCost=0;

            qty= mgr.readNumberValue("QTY");    

            if (isNaN(qty))
                qty=0;

            if (qty!=0)
                colAmount = trans.getNumberValue("CALAMOUNT/DATA/AMOUNT");

            if (isNaN(colAmount))
                colAmount=0;

            if ("TRUE".equals(securityOk))
            {
                colListPrice    = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE");

                if (isNaN(colListPrice))
                    colListPrice=0;

                colPriceListNo  = trans.getValue("PRICEINFO/DATA/PRICE_LIST_NO");
                colDiscount     = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");

                if (isNaN(colDiscount))
                    colDiscount=0;

                double bp=trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE");

                if (isNaN(bp))
                    bp=0;

                double qty1=trans.getNumberValue("PRICEINFO/DATA/QTY1");

                if (isNaN(qty1))
                    qty1=0;

                colAmountSales = bp * qty1;

                double qti=mgr.readNumberValue("QTY_TO_INVOICE");

                if (isNaN(qti))
                    qti=0;

                if (qti!=0)
                {
                    nSalesPriceAmount = colListPrice * qti;
                    colAmountSales    = nSalesPriceAmount - (colDiscount/100 * nSalesPriceAmount);         
                }
                else
                {
                    qty=mgr.readNumberValue("QTY");

                    if (isNaN(qty))
                        qty=0;

                    if (qty!=0)
                    {
                        nSalesPriceAmount = colListPrice * qty;
                        colAmountSales    = nSalesPriceAmount - (colDiscount/100 * nSalesPriceAmount);
                    }
                    else
                        colAmountSales    = 0;
                }

                colListPrice   = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE");

                if (isNaN(colListPrice))
                    colListPrice=0;
            }
            else
            {

                colListPrice = mgr.readNumberValue("LIST_PRICE");

                if (isNaN(colListPrice))
                    colListPrice=0;

                colAmountSales = mgr.readNumberValue("AMOUNTSALES");

                if (isNaN(colAmountSales))
                    colAmountSales=0;

                colPriceListNo = mgr.readValue("PRICE_LIST_NO","");

            }

            qty=mgr.readNumberValue("QTY");

            if (isNaN(qty))
                qty = 0;

            String colListPriceStr= mgr.getASPField("LIST_PRICE").formatNumber(colListPrice);
            String colAmountSalesStr=mgr.getASPField("AMOUNTSALES").formatNumber(colAmountSales);
            String colSalesPartCostStr=mgr.getASPField("SALESPARTCOST").formatNumber(colSalesPartCost);
            String colAmountStr=mgr.getASPField("AMOUNT").formatNumber(colAmount);

	    txt = colListPriceStr +"^"+ colAmountSalesStr +"^"+ (mgr.isEmpty(colPriceListNo) ? "":colPriceListNo) +"^"+ colSalesPartCostStr +"^"+ colAmountStr;
            
	    mgr.responseWrite(txt);
        }
        else if ("ITEM1_ROLE_CODE".equals(val))
        {
            String reqstr = null;
            int startpos = 0;
            int endpos = 0;
            int i = 0;
            String ar[] = new String[6];
            double orgco;
            double rolco;
            double cos;

            String sRoleCode = "";
            String sOrgContract = "";
            String new_role_code = mgr.readValue("ITEM1_ROLE_CODE","");
            
            if (new_role_code.indexOf("^",0)>0)
            {
                if (!mgr.isEmpty(mgr.readValue("EMP_NO")))
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
                sRoleCode = mgr.readValue("ITEM1_ROLE_CODE");
                sOrgContract = mgr.readValue("ITEM1_CONTRACT"); 
            }

            securityOk = "";
            salesSecOk = "";
            secBuff = mgr.newASPTransactionBuffer();
            secBuff.addSecurityQuery("SALES_PART");
            secBuff.addSecurityQuery("Company_Finance_API","Get_Currency_Code");
            secBuff.addSecurityQuery("Customer_Order_Pricing_API","Get_Valid_Price_List,Get_Sales_Part_Price_Info");

            secBuff = mgr.perform(secBuff);

            if (secBuff.getSecurityInfo().itemExists("SALES_PART") && mgr.isModuleInstalled("ORDER"))
                salesSecOk = "TRUE";

            if (secBuff.getSecurityInfo().itemExists("SALES_PART") && secBuff.getSecurityInfo().itemExists("Company_Finance_API.Get_Currency_Code") && secBuff.getSecurityInfo().itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List") && mgr.isModuleInstalled("ORDER"))
                securityOk = "TRUE";

            trans.clear();

            cmd = trans.addCustomFunction("DEFROLECAT","Role_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
            cmd.addParameter("ITEM1_ROLE_CODE",sRoleCode);
            cmd.addParameter("ITEM1_CONTRACT",sOrgContract);

            cmd = trans.addCustomFunction("DEFORGCAT","Organization_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
            cmd.addParameter("ITEM1_CONTRACT",sOrgContract);
            cmd.addParameter("ITEM1_ORG_CODE");

            if ("TRUE".equals(salesSecOk)) 
            {
                cmd = trans.addCustomFunction("DEFROLECATDESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
                cmd.addParameter("ITEM1_CONTRACT",sOrgContract);
                cmd.addReference("CATALOG_NO","DEFROLECAT/DATA");
    
                cmd = trans.addCustomFunction("DEFORGCATDESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
                cmd.addParameter("ITEM1_CONTRACT",sOrgContract);
                cmd.addReference("CATALOG_NO","DEFORGCAT/DATA");
            }

            trans = mgr.validate(trans);

            if (!mgr.isEmpty(trans.getValue("DEFROLECAT/DATA/CATALOG_NO")))
            {
                colCatalogNo = trans.getValue("DEFROLECAT/DATA/CATALOG_NO");
                if ("TRUE".equals(salesSecOk))
                    colCatalogDesc = trans.getValue("DEFROLECATDESC/DATA/SALESPARTCATALOGDESC");
            }
            else if (!mgr.isEmpty(trans.getValue("DEFORGCAT/DATA/CATALOG_NO")))
            {
                colCatalogNo = trans.getValue("DEFORGCAT/DATA/CATALOG_NO");
                if ("TRUE".equals(salesSecOk))
                    colCatalogDesc = trans.getValue("DEFORGCATDESC/DATA/SALESPARTCATALOGDESC");
            }
            else
            {
                colCatalogNo = mgr.readValue("CATALOG_NO","");
                colCatalogDesc = mgr.readValue("SALESPARTCATALOGDESC","");
            }

            trans.clear();

            if ("TRUE".equals(securityOk))
            {
                double nBuyQtyDue = mgr.readNumberValue("QTY_TO_INVOICE");

                if (isNaN(nBuyQtyDue))
                    nBuyQtyDue = 0;

                if (nBuyQtyDue == 0)
                    nBuyQtyDue = 1;

                cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("BASE_PRICE","0");
                cmd.addParameter("SALE_PRICE","0");
                cmd.addParameter("DISCOUNT","0");
                cmd.addParameter("CURRENCY_RATE","0");
                cmd.addParameter("ITEM1_CONTRACT",sOrgContract);
                cmd.addParameter("CATALOG_NO",colCatalogNo);
                cmd.addParameter("ITEM1_CUSTOMER_NO");
                cmd.addParameter("AGREEMENT_ID");
                cmd.addParameter("PRICE_LIST_NO");
                cmd.addParameter("QTY1",mgr.getASPField("QTY1").formatNumber(nBuyQtyDue));
            }

            double qty=mgr.readNumberValue("QTY");
            if (isNaN(qty))
                qty=0;

            double amt=mgr.readNumberValue("AMOUNT");
            if (isNaN(amt))
                amt=0;

            if (!mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE","")) && !mgr.isEmpty(mgr.readValue("ITEM1_CONTRACT","")) && qty!=0 && amt!=0 && !mgr.isEmpty(mgr.readValue("ITEM1_ROLE_CODE","")))
            {
                cmd = trans.addCustomCommand("CALAMOUNT","Work_Order_Coding_API.Calc_Amount");
                cmd.addParameter("AMOUNT");
                cmd.addParameter("QTY");
                cmd.addParameter("ITEM1_ORG_CODE");
                cmd.addParameter("ITEM1_ROLE_CODE",sRoleCode);
                cmd.addParameter("ITEM1_CONTRACT",sOrgContract);
            }

            double colAmount = mgr.readNumberValue("AMOUNT");
            if (isNaN(colAmount))
                colAmount = 0;

            if (!mgr.isEmpty(mgr.readValue("ITEM1_ROLE_CODE","")))
            {
                cmd = trans.addCustomFunction("ROLEDESC","Role_API.Get_Description","CMNT");
                cmd.addParameter("ITEM1_ROLE_CODE",sRoleCode);

                if (!"TRUE".equals(salesSecOk))
                {
                    if (!mgr.isEmpty(colCatalogNo))
                    {
                        qty=mgr.readNumberValue("QTY");
                        if (isNaN(qty))
                            qty=0;
                        if (qty==0)
                            colAmount = 0;
                        else
                        {
                            double colSalesPartCost = mgr.readNumberValue("SALESPARTCOST");
                            if (isNaN(colSalesPartCost))
                                colSalesPartCost = 0;

                            double colQty = mgr.readNumberValue("QTY");
                            if (isNaN(colQty))
                                colQty = 0;

                            colAmount = colSalesPartCost * colQty;
                        }
                    }
                }
            }

            trans = mgr.validate(trans);

            if ("TRUE".equals(securityOk))
            {
                colListPrice    = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE"); 
                if (isNaN(colListPrice))
                    colListPrice=0;

                colPriceListNo  = trans.getValue("PRICEINFO/DATA/PRICE_LIST_NO");
                colDiscount     = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");

                if (isNaN(colDiscount))
                    colDiscount=0;
                double bp=trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE");

                if (isNaN(bp))
                    bp=0;
                double qt=trans.getNumberValue("PRICEINFO/DATA/QTY1");

                if (isNaN(qt))
                    qt=0;
                colAmountSales = bp * qt;

                double qti=mgr.readNumberValue("QTY_TO_INVOICE");
                if (isNaN(qti))
                    qti=0;

                if (qti!=0)
                {
                    double qtyToInvoice = mgr.readNumberValue("QTY_TO_INVOICE");
                    if (isNaN(qtyToInvoice))
                        qtyToInvoice = 0;

                    nSalesPriceAmount = colListPrice * qtyToInvoice;
                    colAmountSales    = nSalesPriceAmount - (colDiscount/100 * nSalesPriceAmount);         
                }
                else
                {
                    qty=mgr.readNumberValue("QTY");
                    if (isNaN(qty))
                        qty=0;

                    if (qty!=0)
                    {
                        double nQty = mgr.readNumberValue("QTY");
                        if (isNaN(nQty))
                            nQty = 0;
                        nSalesPriceAmount = colListPrice * nQty;
                        colAmountSales    = nSalesPriceAmount - (colDiscount/100 * nSalesPriceAmount);
                    }
                    else
                        colAmountSales    = 0;
                }
                colListPrice   = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE");
                if (isNaN(colListPrice))
                    colListPrice=0;
            }
            else
            {
                colListPrice = mgr.readNumberValue("LIST_PRICE");
                if (isNaN(colListPrice))
                    colListPrice=0;

                colAmountSales = mgr.readNumberValue("AMOUNTSALES");
                if (isNaN(colAmountSales))
                    colAmountSales=0;

                colPriceListNo = mgr.readValue("PRICE_LIST_NO","");
            }
            qty=mgr.readNumberValue("QTY");
            if (isNaN(qty))
                qty=0;
            amt=mgr.readNumberValue("AMOUNT");
            if (isNaN(amt))
                amt=0;
            if (!mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE","")) && !mgr.isEmpty(mgr.readValue("ITEM1_CONTRACT","")) && qty!=0 && amt!=0 && !mgr.isEmpty(mgr.readValue("ITEM1_ROLE_CODE","")))
            {
                colAmount = trans.getNumberValue("CALAMOUNT/DATA/AMOUNT");
                if (isNaN(colAmount))
                    colAmount=0;
            }

            if (!mgr.isEmpty(mgr.readValue("ITEM1_ROLE_CODE","")))
                colCmnt = mgr.readValue("NAME","")+ ","+ mgr.readValue("ITEM1_CONTRACT","")+ ","+ mgr.readValue("ITEM1_ORG_CODE","")+"("+ trans.getValue("ROLEDESC/DATA/CMNT")+")";
            else
                colCmnt = mgr.readValue("NAME","") +","+ mgr.readValue("ITEM1_CONTRACT","") +","+ mgr.readValue("ITEM1_ORG_CODE","");

            String colListPriceStr = mgr.getASPField("LIST_PRICE").formatNumber(colListPrice);
            String colAmountStr = mgr.getASPField("AMOUNT").formatNumber(colAmount);
            String colAmountSalesStr = mgr.getASPField("AMOUNTSALES").formatNumber(colAmountSales);

            txt = colListPriceStr +"^"+ (mgr.isEmpty(colPriceListNo)?"": colPriceListNo) +"^"+ colAmountStr +"^"+ colAmountSalesStr +"^"+ (mgr.isEmpty(colCmnt) ? "": colCmnt ) +"^"+ (mgr.isEmpty(colCatalogNo)?"":colCatalogNo) +"^"+ (mgr.isEmpty(colCatalogDesc)?"":colCatalogDesc) +"^"+ (mgr.isEmpty(sRoleCode)?"":sRoleCode)+ "^" + (mgr.isEmpty(sOrgContract)?"":sOrgContract) +"^";
                      


            mgr.responseWrite(txt);
        }
        else if ("CATALOG_NO".equals(val))
        {
            securityOk = "";
            salesSecOk = "";
            secBuff = mgr.newASPTransactionBuffer();
            secBuff.addSecurityQuery("SALES_PART");
            secBuff.addSecurityQuery("Company_Finance_API","Get_Currency_Code");
            secBuff.addSecurityQuery("Customer_Order_Pricing_API","Get_Valid_Price_List,Get_Sales_Part_Price_Info");

            secBuff = mgr.perform(secBuff);

            if (secBuff.getSecurityInfo().itemExists("SALES_PART") && mgr.isModuleInstalled("ORDER"))
                salesSecOk = "TRUE";

            if (secBuff.getSecurityInfo().itemExists("SALES_PART") && secBuff.getSecurityInfo().itemExists("Company_Finance_API.Get_Currency_Code") && secBuff.getSecurityInfo().itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List") && mgr.isModuleInstalled("ORDER"))
                securityOk = "TRUE";

            if ("TRUE".equals(securityOk))
            {

                nBuyQtyDue = mgr.readNumberValue("QTY_TO_INVOICE");
                if (isNaN(nBuyQtyDue))
                    nBuyQtyDue=0;
                if (nBuyQtyDue==0)
                {
                    nBuyQtyDue = mgr.readNumberValue("QTY");
                    if (isNaN(nBuyQtyDue))
                        nBuyQtyDue=0;
                }

                if (nBuyQtyDue == 0)
                    nBuyQtyDue = 1;

                cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("BASE_PRICE");
                cmd.addParameter("SALE_PRICE");
                cmd.addParameter("DISCOUNT");
                cmd.addParameter("CURRENCY_RATE");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addParameter("CATALOG_NO");
                cmd.addParameter("ITEM1_CUSTOMER_NO");
                cmd.addParameter("AGREEMENT_ID");
                cmd.addParameter("PRICE_LIST_NO");
                cmd.addParameter("QTY1",mgr.getASPField("QTY1").formatNumber(nBuyQtyDue));
            }

            cmd = trans.addCustomFunction("ROLEDESC","Role_API.Get_Description","CMNT");
            cmd.addParameter("ITEM1_ROLE_CODE");

            if ("TRUE".equals(salesSecOk))
            {
                cmd = trans.addCustomFunction("DEFCATDESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addParameter("CATALOG_NO");

                cmd = trans.addCustomFunction("CST", "Work_Order_Planning_Util_Api.Get_Cost", "COST3" );
                cmd.addParameter("ITEM1_ORG_CODE");
                cmd.addParameter("ITEM1_ROLE_CODE");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addParameter("CATALOG_NO");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addParameter("EMP_NO",mgr.readValue("EMP_NO"));
                cmd.addParameter("DUMMY","TRUE");
            }
            else
            {
                cmd = trans.addCustomFunction("CST", "Work_Order_Planning_Util_Api.Get_Cost", "COST3" );
                cmd.addParameter("ITEM1_ORG_CODE");
                cmd.addParameter("ITEM1_ROLE_CODE");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addParameter("DUMMY","");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addParameter("EMP_NO",mgr.readValue("EMP_NO"));
                cmd.addParameter("DUMMY","TRUE");
            }

            trans = mgr.validate(trans);

            if ("TRUE".equals(securityOk))
            {
                colListPrice    = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE");
                if (isNaN(colListPrice))
                    colListPrice=0;
                colPriceListNo  = trans.getValue("PRICEINFO/DATA/PRICE_LIST_NO");
                colDiscount     = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");
                if (isNaN(colDiscount))
                    colDiscount=0;
                double bp=trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE");
                if (isNaN(bp))
                    bp=0;
                double qt=trans.getNumberValue("PRICEINFO/DATA/QTY1");
                if (isNaN(qt))
                    qt=0;
                colAmountSales = bp * qt;

                double qti=mgr.readNumberValue("QTY_TO_INVOICE");
                if (isNaN(qti))
                    qti=0;
                nSalesPriceAmount = colListPrice * qti;
                colAmountSales    = nSalesPriceAmount - (colDiscount/100 * nSalesPriceAmount);         
                if (qti==0)
                {
                    double qyt= mgr.readNumberValue("QTY");
                    if (isNaN(qyt))
                        qyt=0;
                    nSalesPriceAmount = colListPrice * qyt;
                    colAmountSales    = nSalesPriceAmount - (colDiscount/100 * nSalesPriceAmount);
                    if (qty==0)
                        colAmountSales    = 0;
                }
                colListPrice   = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE");
                if (isNaN(colListPrice))
                    colListPrice=0;
            }
            else
            {

                colListPrice = mgr.readNumberValue("LIST_PRICE");
                if (isNaN(colListPrice))
                    colListPrice=0;

                colAmountSales = mgr.readNumberValue("AMOUNTSALES");
                if (isNaN(colAmountSales))
                    colAmountSales=0;

                colPriceListNo = mgr.readValue("PRICE_LIST_NO","");
            }

            if ("TRUE".equals(salesSecOk))
                colSalesPartDesc = trans.getValue("DEFCATDESC/DATA/SALESPARTCATALOGDESC");
            else
                colSalesPartDesc = mgr.readValue("SALESPARTCATALOGDESC","");

            colCmnt = mgr.readValue("NAME","") +","+ mgr.readValue("ITEM1_CONTRACT","") +","+ mgr.readValue("ITEM1_ORG_CODE","") +"("+ trans.getValue("ROLEDESC/DATA/CMNT") +")"+ colSalesPartDesc;

            colSalesPartCost = 0;

            colSalesPartCost = trans.getNumberValue("CST/DATA/COST3");
            if (isNaN(colSalesPartCost))
                colSalesPartCost = 0;

            double qty = mgr.readNumberValue("QTY");

            if (isNaN(qty))
                qty = 0;

            if (qty == 0)
                colAmount = 0;
            else
                colAmount = numSalesPartCost * qty;    

            if (isNaN(colDiscount))
                colDiscount = 0;

            colAmount = colSalesPartCost * qty;
            colAmountSales = colListPrice * qty;

            colAmount -= (colDiscount/100) * colAmount;
            colAmount -= (colDiscount/100) * colAmountSales;

            String colListPriceStr = mgr.getASPField("LIST_PRICE").formatNumber(colListPrice);
            String colAmountStr = mgr.getASPField("AMOUNT").formatNumber(colAmount);
            String colAmountSalesStr = mgr.getASPField("AMOUNTSALES").formatNumber(colAmountSales);
            String colSalesPartCostStr = mgr.getASPField("SALESPARTCOST").formatNumber(colSalesPartCost);

            txt = colListPriceStr +"^"+ colAmountStr +"^"+ colAmountSalesStr +"^"+ (mgr.isEmpty(colPriceListNo)?"":colPriceListNo) +"^"+ (mgr.isEmpty(colCmnt) ? "":colCmnt) +"^"+ (mgr.isEmpty(colSalesPartDesc)?"":colSalesPartDesc) +"^"+ colSalesPartCostStr +"^";

            mgr.responseWrite(txt);
        }
        else if ("ITEM1_ORG_CODE".equals(val))
        {
            securityOk = "";
            secBuff = mgr.newASPTransactionBuffer();
            secBuff.addSecurityQuery("SALES_PART");

            secBuff = mgr.perform(secBuff);

            if (secBuff.getSecurityInfo().itemExists("SALES_PART") && mgr.isModuleInstalled("ORDER"))
                securityOk = "TRUE";

            double qty = mgr.readNumberValue("QTY");

            if (isNaN(qty))
                qty=0;

            double amt = mgr.readNumberValue("AMOUNT");

            if (isNaN(amt))
                amt=0;

            if (!mgr.isEmpty(mgr.readValue("ITEM1_CONTRACT","")) && !mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE","")) && qty!=0 && amt!=0 && !mgr.isEmpty(mgr.readValue("ITEM1_ROLE_CODE","")))
            {
                cmd = trans.addCustomCommand("CALAMOUNT","Work_Order_Coding_API.Calc_Amount");
                cmd.addParameter("AMOUNT");
                cmd.addParameter("QTY");
                cmd.addParameter("ITEM1_ORG_CODE");
                cmd.addParameter("ITEM1_ROLE_CODE");
                cmd.addParameter("ITEM1_CONTRACT");
            }

            if (!mgr.isEmpty(mgr.readValue("ITEM1_CONTRACT","")) && !mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE","")))
            {
                cmd = trans.addCustomFunction("ROLEDESC","Role_API.Get_Description","CMNT");
                cmd.addParameter("ITEM1_ROLE_CODE");
            }
            else
            {
                cmd = trans.addCustomFunction("GETORGCODE","Active_Work_Order_API.Get_Org_Code","ORCODE");
                cmd.addParameter("ITEM1_WO_NO");
            }

            trans = mgr.validate(trans);

            if ("TRUE".equals(securityOk) && mgr.isEmpty(mgr.readValue("ITEM1_ROLE_CODE","")))
            {
                colOrgCode         = mgr.readValue("ITEM1_ORG_CODE","");
                colCmnt            = mgr.readValue("CMNT","");

                amt = mgr.readNumberValue("AMOUNT");

                colAmount = mgr.readNumberValue("AMOUNT");

                if (isNaN(colAmount))
                    colAmount = 0;
            }
            else
            {
                qty = mgr.readNumberValue("QTY");

                if (isNaN(qty))
                    qty=0;

                amt=mgr.readNumberValue("AMOUNT");

                if (isNaN(amt))
                    amt=0;

                if (!mgr.isEmpty(mgr.readValue("ITEM1_CONTRACT","")) && !mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE","")) && qty!=0 && amt!=0 && !mgr.isEmpty(mgr.readValue("ITEM1_ROLE_CODE","")))
                {

                    colAmount = trans.getNumberValue("CALAMOUNT/DATA/MOUNT");

                    if (isNaN(colAmount))
                        colAmount = 0;
                }
                else if (qty != 0)
                    colAmount  = mgr.readNumberValue("AMOUNT");
                else if (qty == 0)
                    colAmount = 0;

                if (!mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE","")))
                {
                    colOrgCode = mgr.readValue("ITEM1_ORG_CODE","");
                    colCmnt = mgr.readValue("NAME","")+ ","+ mgr.readValue("ITEM1_CONTRACT","")+ ","+ mgr.readValue("ITEM1_ORG_CODE","")+"("+ trans.getValue("ROLEDESC/DATA/CMNT")+")";
                }
                else
                {
                    colOrgCode = trans.getValue("GETORGCODE/DATA/ORCODE");                
                    colCmnt = mgr.readValue("NAME","")+ ","+ mgr.readValue("ITEM1_CONTRACT","")+ "," + colOrgCode;
                }    
            }

            trans.clear();

            cmd = trans.addCustomFunction("DEFROLECAT","Role_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
            cmd.addParameter("ITEM1_ROLE_CODE");
            cmd.addParameter("ITEM1_CONTRACT");

            cmd = trans.addCustomFunction("DEFORGCAT","Organization_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("ITEM1_ORG_CODE");

            if ("TRUE".equals(securityOk)) 
            {
                cmd = trans.addCustomFunction("DEFROLECATDESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addReference("CATALOG_NO","DEFROLECAT/DATA");
    
                cmd = trans.addCustomFunction("DEFORGCATDESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addReference("CATALOG_NO","DEFORGCAT/DATA");
            }

            trans = mgr.validate(trans);

            if (!mgr.isEmpty(trans.getValue("DEFROLECAT/DATA/CATALOG_NO")))
            {
                colSalesPart = trans.getValue("DEFROLECAT/DATA/CATALOG_NO");
                if ("TRUE".equals(securityOk)) 
                    colLineDescription = trans.getValue("DEFROLECATDESC/DATA/SALESPARTCATALOGDESC");
            }
            else if (!mgr.isEmpty(trans.getValue("DEFORGCAT/DATA/CATALOG_NO")))
            {
                colSalesPart = trans.getValue("DEFORGCAT/DATA/CATALOG_NO");
                if ("TRUE".equals(securityOk)) 
                    colLineDescription = trans.getValue("DEFORGCATDESC/DATA/SALESPARTCATALOGDESC");
            }
            else
            {
                colSalesPart = mgr.readValue("CATALOG_NO","");
                colLineDescription = mgr.readValue("SALESPARTCATALOGDESC","");
            }

            String colAmountStr = mgr.getASPField("AMOUNT").formatNumber(colAmount);

            txt = (mgr.isEmpty(colOrgCode) ? "":colOrgCode) +"^"+ colAmountStr +"^"+ (mgr.isEmpty(colCmnt) ? "":colCmnt) +"^"+ (mgr.isEmpty(colSalesPart) ? "":colSalesPart) +"^"+ (mgr.isEmpty(colLineDescription) ? "":colLineDescription) +"^" ;

            mgr.responseWrite(txt);
        }
        else if ("ITEM1_CONTRACT".equals(val))
        {
            cmd = trans.addCustomFunction("COMP","Site_API.Get_Company","COMPANY_VAR");
            cmd.addParameter("ITEM1_CONTRACT");

            trans = mgr.validate(trans);

            strCompany = trans.getValue("COMP/DATA/COMPANY_VAR");
            txt = (mgr.isEmpty(strCompany) ? "" : (strCompany))+ "^";
            mgr.responseWrite(txt);
        }
        else if ("PRICE_LIST_NO".equals(val))
        {
            securityOk = "";
            secBuff = mgr.newASPTransactionBuffer();
            secBuff.addSecurityQuery("SALES_PART");
            secBuff.addSecurityQuery("Company_Finance_API","Get_Currency_Code");
            secBuff.addSecurityQuery("Customer_Order_Pricing_API","Get_Valid_Price_List,Get_Sales_Part_Price_Info");

            secBuff = mgr.perform(secBuff);

            if (secBuff.getSecurityInfo().itemExists("SALES_PART") && mgr.isModuleInstalled("ORDER"))
                salesSecOk = "TRUE";

            if (secBuff.getSecurityInfo().itemExists("SALES_PART") && secBuff.getSecurityInfo().itemExists("Company_Finance_API.Get_Currency_Code") && secBuff.getSecurityInfo().itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List") && mgr.isModuleInstalled("ORDER"))
                securityOk = "TRUE";

            if ("TRUE".equals(securityOk))
            {


                nBuyQtyDue = mgr.readNumberValue("QTY_TO_INVOICE");
                if (isNaN(nBuyQtyDue))
                    nBuyQtyDue=0;
                if (nBuyQtyDue==0)
                    nBuyQtyDue = mgr.readNumberValue("QTY");
                if (isNaN(nBuyQtyDue))
                    nBuyQtyDue=0;

                if (nBuyQtyDue==0)
                    nBuyQtyDue = 1;

                cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("BASE_PRICE");
                cmd.addParameter("SALE_PRICE");
                cmd.addParameter("DISCOUNT");
                cmd.addParameter("CURRENCY_RATE");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addParameter("CATALOG_NO");
                cmd.addParameter("ITEM1_CUSTOMER_NO");
                cmd.addParameter("AGREEMENT_ID");
                cmd.addParameter("PRICE_LIST_NO");
                cmd.addParameter("QTY1",mgr.getASPField("QTY1").formatNumber(nBuyQtyDue));
            }

            trans = mgr.validate(trans);


            if ("TRUE".equals(securityOk))
            {
                colListPrice    = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE"); 

                if (isNaN(colListPrice))
                    colListPrice=0;

                colPriceListNo  = trans.getValue("PRICEINFO/DATA/PRICE_LIST_NO");
                colDiscount     = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");

                if (isNaN(colDiscount))
                    colDiscount = 0;

                double bp = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE");

                if (isNaN(bp))
                    bp = 0;

                double qt = trans.getNumberValue("PRICEINFO/DATA/QTY1");

                if (isNaN(qt))
                    qt=0;

                colAmountSales = bp * qt;

                double qti = mgr.readNumberValue("QTY_TO_INVOICE");

                if (isNaN(qti))
                    qti=0;

                if (qti!=0)
                {
                    nSalesPriceAmount = colListPrice * qti;
                    colAmountSales    = nSalesPriceAmount - (colDiscount/100 * nSalesPriceAmount);         
                }
                else
                {
                    double qty=mgr.readNumberValue("QTY");
                    if (isNaN(qty))
                        qty=0;

                    if (qty!=0)
                    {
                        nSalesPriceAmount = colListPrice * qty;
                        colAmountSales    = nSalesPriceAmount - (colDiscount/100 * nSalesPriceAmount);
                    }
                    else
                        colAmountSales    = 0;
                }
                colListPrice   = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE");
                if (isNaN(colListPrice))
                    colListPrice=0;
            }
            else
            {
                colListPrice = mgr.readNumberValue("LIST_PRICE");
                if (isNaN(colListPrice))
                    colListPrice=0;


                colAmountSales = mgr.readNumberValue("AMOUNTSALES");
                if (isNaN(colAmountSales))
                    colAmountSales=0;

                colPriceListNo = mgr.readValue("PRICE_LIST_NO","");

            }

            colcAgreement = "1";

            listPrice = mgr.readNumberValue("LIST_PRICE");
            if (isNaN(listPrice))
                listPrice=0;
            qty = mgr.readNumberValue("QTY");
            if (isNaN(qty))
                qty=0;
            colAmountSales = listPrice * qty;

            String colListPriceStr=mgr.getASPField("LIST_PRICE").formatNumber(colListPrice);
            String colAmountSalesStr= mgr.getASPField("AMOUNTSALES").formatNumber(colAmountSales);

            txt = colListPriceStr +"^"+ colAmountSalesStr +"^"+ (mgr.isEmpty(colPriceListNo) ? "":colPriceListNo) +"^"+ (mgr.isEmpty(colcAgreement) ? "":colcAgreement) +"^";

            mgr.responseWrite(txt);
        }
        else if ("CMNT".equals(val))
        {

            String Comment = mgr.readValue("CMNT","");
            int leng = Comment.length();
            if (leng > 80)
                txt = Comment.substring(0,80);
            else
                txt = Comment;
            mgr.responseWrite(txt);
        }
        else if ("LIST_PRICE".equals(val))
        {
            colcAgreement = "1";
        }
        else if ("PERMIT_SEQ".equals(val))
        {
            cmd = trans.addCustomFunction("PERMITTYPE","PERMIT_API.Get_Permit_Type_Id","PERMIT_TYPE_ID");
            cmd.addParameter("PERMIT_SEQ");
            cmd = trans.addCustomFunction("PERMITDESC","PERMIT_API.Get_Description","PERMITDESCRIPTION");
            cmd.addParameter("PERMIT_SEQ");

            trans = mgr.validate(trans);

            sPermitType= trans.getValue("PERMITTYPE/DATA/PERMIT_TYPE_ID");
            sPermitDesc= trans.getValue("PERMITDESC/DATA/PERMITDESCRIPTION");

            txt =  (mgr.isEmpty(sPermitType) ? "" :sPermitType) + "^"      + (mgr.isEmpty(sPermitDesc) ? "" :sPermitDesc) + "^" ;

            mgr.responseWrite(txt);
        }
        else if ("ITEM4_CONTRACT".equals(val))
        {
            cmd = trans.addCustomFunction("COMP","Site_API.Get_Company","ITEM4_COMPANY");
            cmd.addParameter("ITEM4_CONTRACT");

            trans = mgr.validate(trans);

            strCompany = trans.getValue("COMP/DATA/ITEM$_COMPANY");

            txt = (mgr.isEmpty(strCompany) ? "" : (strCompany))+ "^";
            mgr.responseWrite(txt);
        }
        else if ("ITEM4_WORK_ORDER_COST_TYPE".equals(val))
        {
            cmd = trans.addCustomFunction("COSTCENT","Active_Work_Order_API.Get_Cost_Center","COST_CENTER");
            cmd.addParameter("WO_NO");
            cmd = trans.addCustomFunction("OBJ","Active_Work_Order_API.Get_Object_No ","OBJECT_NO");
            cmd.addParameter("WO_NO");
            cmd = trans.addCustomFunction("PROJ","Active_Work_Order_API.Get_Project_No ","PROJECT_NO");
            cmd.addParameter("WO_NO");

            cmd = trans.addCustomFunction("CL1","Work_Order_Cost_Type_Api.Get_Client_Value(1)","CLIENTVAL1");
            cmd = trans.addCustomFunction("CL2","Work_Order_Cost_Type_Api.Get_Client_Value(0)","CLIENTVAL2");
            cmd = trans.addCustomFunction("CL4","Work_Order_Cost_Type_Api.Get_Client_Value(4)","CLIENTVAL4");

            trans = mgr.validate(trans);

            strWorkOrder = mgr.readValue("ITEM4_WORK_ORDER_COST_TYPE","");
            strCost = trans.getValue("COSTCENT/DATA/COST_CENTER");
            strObj = trans.getValue("OBJ/DATA/OBJECT_NO");
            strProj = trans.getValue("PROJ/DATA/PROJECT_NO");
            strMeterial = trans.getValue("CL1/DATA/CLIENTVAL1");
            strPersonal = trans.getValue("CL2/DATA/CLIENTVAL2");
            strFixedPrice = trans.getValue("CL4/DATA/CLIENTVAL4");

            if (strFixedPrice.equals(strWorkOrder))
                nAmount = 0;
            else
                nAmount = 0;

            String strAmount=mgr.getASPField("ITEM4_AMOUNT").formatNumber(nAmount);

            txt =  (mgr.isEmpty(strCost) ? "": strCost)+ "^" + (mgr.isEmpty(strObj) ? "" : strObj)+ "^" + (mgr.isEmpty(strProj) ? "" : strProj)+ "^" +
                   strAmount + "^" +  (mgr.isEmpty(strMeterial) ? "" : strMeterial)+ "^" +  (mgr.isEmpty(strPersonal) ? "" : strPersonal)+ "^";

            mgr.responseWrite(txt);
        }
        else if ("ITEM4_CATALOG_NO".equals(val))
        {
            if (checksec("Sales_Part_API.Get_Catalog_Desc",1,isSecure))
            {
                cmd = trans.addCustomFunction("CATDESC","Sales_Part_API.Get_Catalog_Desc","LINEDESCRIPTION");
                cmd.addParameter("ITEM4_CONTRACT");
                cmd.addParameter("ITEM4_CATALOG_NO");
            }

            cmd = trans.addCustomFunction("CLIENT3","Work_Order_Cost_Type_Api.Get_Client_Value(3)","CLIENTVAL3");
            cmd = trans.addCustomFunction("CLIENT4","Work_Order_Cost_Type_Api.Get_Client_Value(4)","CLIENTVAL4");

            trans = mgr.validate(trans);

            if ("true".equals(isSecure[1]))
                strCatalogDesc = trans.getValue("CATDESC/DATA/LINEDESCRIPTION");
            else
                strCatalogDesc  = "";

            strExpenses = trans.getValue("CLIENT3/DATA/CLIENTVAL3");
            strFixedPrice = trans.getValue("CLIENT4/DATA/CLIENTVAL4");

            nAmount = mgr.readNumberValue("ITEM4_AMOUNT"); 

            if (isNaN(nAmount))
                nAmount = 0;

            sPriceListNo2 = ""; 
            nListPrice = 0; 
            nSalesPriceAmount = 0;
            strAgreement = "0" ;
            double nQtyToInv = 0;

            strWorkOrderCost= mgr.readValue("ITEM4_WORK_ORDER_COST_TYPE","");

            if (!(strFixedPrice.equals(strWorkOrderCost)))
            {
                if (strExpenses.equals(strWorkOrderCost))
                {
                    double qty = mgr.readNumberValue("ITEM4_QTY");

                    if (isNaN(qty))
                        qty = 0;

                    if (qty != 0)
                    {
                        trans.clear();

                        if (checksec("Sales_Part_API.Get_Cost",1,isSecure))
                        {
                            cmd = trans.addCustomFunction("COSTING","Sales_Part_API.Get_Cost","AMOUNT4");
                            cmd.addParameter("ITEM4_CONTRACT");
                            cmd.addParameter("ITEM4_CATALOG_NO");

                            trans = mgr.validate(trans);
                            nAmount = trans.getNumberValue("COSTING/DATA/AMOUNT4");

                            if (isNaN(nAmount))
                                nAmount = 0;
                        }
                        else
                            nAmount = 0;
                    }
                }

                trans.clear();

                nBuyQtyDue = mgr.readNumberValue("ITEM4_QTY");

                if (isNaN(nBuyQtyDue))
                    nBuyQtyDue = 0;

                sPriceListNo = mgr.readValue("ITEM4_PRICE_LIST_NO","");

                if (nBuyQtyDue == 0)
                    nBuyQtyDue = 1;

                cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("BASE_PRICE");
                cmd.addParameter("SALE_PRICE");
                cmd.addParameter("DISCOUNT");
                cmd.addParameter("CURRENCY_RATE");
                cmd.addParameter("ITEM4_CONTRACT");
                cmd.addParameter("ITEM4_CATALOG_NO");
                cmd.addParameter("CUSTOMER_NO",mgr.readValue("CUSTOMER_NO",""));
                cmd.addParameter("AGREEMENT_ID",mgr.readValue("AGREEMENT_ID",""));
                cmd.addParameter("PRICELISTNO4",sPriceListNo);
                cmd.addParameter("QTY1",mgr.getASPField("QTY1").formatNumber(nBuyQtyDue));

                trans = mgr.validate(trans);

                nBaseUnitPrice = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE");

                if (isNaN(nBaseUnitPrice))
                    nBaseUnitPrice = 0;

                sPriceListNo1 = trans.getValue("PRICEINFO/DATA/PRICELISTNO4");

                nListPrice = mgr.readNumberValue("ITEM4_LIST_PRICE");
                if (isNaN(nListPrice))
                    nListPrice = 0;

                nQtyToInv = mgr.readNumberValue("ITEM4_QTY_TO_INVOICE");

                if (isNaN(nQtyToInv))
                    nQtyToInv = 0;

                if (nQtyToInv == 0)
                    nQtyToInv = 1;

                if (mgr.isEmpty(sPriceListNo))
                    sPriceListNo2 = sPriceListNo1;

                nListPrice = nBaseUnitPrice;

                double nDisc = mgr.readNumberValue("ITEM4_DISCOUNT");

                if (isNaN(nDisc))
                    nDisc = 0;

                double nDiscSalesPrice = 0;

                if ((nDisc == 0) || (nDisc < 1))
                    nDiscSalesPrice = nListPrice;
                else
                    nDiscSalesPrice = (nListPrice - ((nDisc /100) * nListPrice));

                nSalesPriceAmount = nQtyToInv * nDiscSalesPrice;

                if ((mgr.isEmpty(sPriceListNo)) && (!mgr.isEmpty(sPriceListNo1)))
                    sPriceListNo2 = sPriceListNo1;
                else
                    sPriceListNo2 = sPriceListNo;

                strAgreement = "1" ;
            }
            else
                nAmount = 0;

            String strAmount = mgr.getASPField("ITEM4_AMOUNT").formatNumber(nAmount);
            String strQtyToInv = mgr.getASPField("ITEM4_QTY_TO_INVOICE").formatNumber(nQtyToInv);
            String strListPrice = mgr.getASPField("ITEM4_LIST_PRICE").formatNumber(nListPrice);
            String strSalesPriceAmount = mgr.getASPField("SALESPRICEAMOUNT").formatNumber(nSalesPriceAmount);

            txt =  (mgr.isEmpty(strCatalogDesc) ? "" :strCatalogDesc) + "^" + 
                   strAmount + "^" + 
                   (mgr.isEmpty(sPriceListNo2) ? "" :sPriceListNo2) + "^" + 
                   strListPrice + "^" + 
                   strSalesPriceAmount + "^" + 
                   (mgr.isEmpty(strAgreement) ? "": strAgreement ) + "^" + 
                   strQtyToInv + "^";

            mgr.responseWrite(txt);
        }
        else if ("ITEM4_PRICE_LIST_NO".equals(val))
        {
            sPriceListNo= mgr.readValue("ITEM4_PRICE_LIST_NO","");
            nBuyQtyDue= mgr.readNumberValue("ITEM4_QTY");
            if (isNaN(nBuyQtyDue))
                nBuyQtyDue=0;

            if (nBuyQtyDue==0)
                nBuyQtyDue=1;

            strAgreement = "0" ;

            cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("BASE_PRICE");
            cmd.addParameter("SALE_PRICE");
            cmd.addParameter("DISCOUNT");
            cmd.addParameter("CURRENCY_RATE");
            cmd.addParameter ("ITEM4_CONTRACT");
            cmd.addParameter ("ITEM4_CATALOG_NO");
            cmd.addParameter ("CUSTOMER_NO");
            cmd.addParameter ("AGREEMENT_ID");
            cmd.addParameter ("PRICELISTNO4",sPriceListNo);
            cmd.addParameter ("QTY1",mgr.getASPField("QTY1").formatNumber(nBuyQtyDue));

            trans = mgr.validate(trans);

            nBaseUnitPrice= trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE");
            if (isNaN(nBaseUnitPrice))
                nBaseUnitPrice=0;
            sPriceListNo1= trans.getValue("PRICEINFO/DATA/PRICELISTNO4");
            nListPrice= mgr.readNumberValue("ITEM4_LIST_PRICE");
            if (isNaN(nListPrice))
                nListPrice=0;
            nQty4= mgr.readNumberValue("ITEM4_QTY");
            if (isNaN(nQty4))
                nQty4=0;

            if (mgr.isEmpty( sPriceListNo))
                sPriceListNo2=sPriceListNo1;
            if (nListPrice==0)
                nListPrice=nBaseUnitPrice;
            if (!(nQty4==0))
                nSalesPriceAmount=nBaseUnitPrice*nBuyQtyDue;
            else
                nSalesPriceAmount   =0;

            if ((mgr.isEmpty( sPriceListNo)) && (!mgr.isEmpty( sPriceListNo1)))
                sPriceListNo2=sPriceListNo1;
            else
                sPriceListNo2=sPriceListNo;

            strAgreement = "1" ;
            nSalesPriceAmount=nListPrice* nQty4;

            String strListPrice=mgr.getASPField("ITEM4_LIST_PRICE").formatNumber(nListPrice);
            String strSalesPriceAmount=mgr.getASPField("SALESPRICEAMOUNT").formatNumber(nSalesPriceAmount);

            txt =  (mgr.isEmpty(sPriceListNo2) ? "" :sPriceListNo2) + "^" + 
                   strListPrice + "^" +
                   strSalesPriceAmount + "^"      + 
                   (mgr.isEmpty(strAgreement) ? "":strAgreement ) + "^" ;

            mgr.responseWrite(txt);
        }
        else if ("ITEM4_QTY".equals(val))
        {
            sPriceListNo = mgr.readValue("ITEM4_PRICE_LIST_NO","");

            cmd = trans.addCustomFunction("CLIENT3","Work_Order_Cost_Type_Api.Get_Client_Value(3)","CLIENTVAL3");

            trans = mgr.validate(trans);

            strClient3 = trans.getValue("CLIENT3/DATA/CLIENTVAL3");

            nAmount = 0;   
            strWorkOrderCost= mgr.readValue("ITEM4_WORK_ORDER_COST_TYPE","");
            nAgreementId = mgr.readNumberValue("ITEM4_AGREEMENT_PRICE_FLAG");

            if (isNaN(nAgreementId))
                nAgreementId = 0;

            if (nAgreementId != 0)
            {
                trans.clear();
                nBuyQtyDue = mgr.readNumberValue("ITEM4_QTY");

                if (isNaN(nBuyQtyDue))
                    nBuyQtyDue = 0;

                cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("BASE_PRICE");
                cmd.addParameter("SALE_PRICE");
                cmd.addParameter("DISCOUNT");
                cmd.addParameter("CURRENCY_RATE");
                cmd.addParameter("ITEM4_CONTRACT");
                cmd.addParameter("ITEM4_CATALOG_NO");
                cmd.addParameter("CUSTOMER_NO");
                cmd.addParameter("AGREEMENT_ID");
                cmd.addParameter("ITEM4_PRICE_LIST_NO");
                cmd.addParameter("QTY1",mgr.getASPField("QTY1").formatNumber(nBuyQtyDue));

                trans = mgr.validate(trans);

                nBaseUnitPrice = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE");

                if (isNaN(nBaseUnitPrice))
                    nBaseUnitPrice = 0;

                sPriceListNo1 = trans.getValue("PRICEINFO/DATA/PRICELISTNO4");
                nListPrice = mgr.readNumberValue("ITEM4_LIST_PRICE");

                if (isNaN(nListPrice))
                    nListPrice = 0;

                nQty4 = mgr.readNumberValue("ITEM4_QTY");

                if (isNaN(nQty4))
                    nQty4 = 0;

                double nQtytoIn = mgr.readNumberValue("ITEM4_QTY_TO_INVOICE");

                if (isNaN(nQtytoIn))
                    nQtytoIn = 0;

                double discount = mgr.readNumberValue("ITEM4_DISCOUNT");

                if (isNaN(discount))
                    discount = 0;

                if (mgr.isEmpty(sPriceListNo))
                    sPriceListNo2 = sPriceListNo1;

                if (nListPrice == 0)
                    nListPrice = nBaseUnitPrice;

                if (nQtytoIn != 0)
                {
                    nSalesPriceAmount = nListPrice * nQtytoIn;
                    nSalesPriceAmount = nSalesPriceAmount - (discount / 100 * nSalesPriceAmount);
                }
                else
                {
                    nSalesPriceAmount = nListPrice * nQtytoIn;
                }

                if ((mgr.isEmpty( sPriceListNo)) && (!mgr.isEmpty(sPriceListNo1)))
                    sPriceListNo2 = sPriceListNo1;
                else
                    sPriceListNo2 = sPriceListNo;
            }
            else
            {
                double nQtytoIn = mgr.readNumberValue("ITEM4_QTY_TO_INVOICE");

                if (isNaN(nQtytoIn))
                    nQtytoIn = 0;

                nList = mgr.readNumberValue("ITEM4_LIST_PRICE");
                if (isNaN(nList))
                    nList = 0;

                nSalesPriceAmount = nQtytoIn * nList;
            }

            if (strClient3.equals(strWorkOrderCost))
            {
                nQty4 = mgr.readNumberValue("ITEM4_QTY");

                if (isNaN(nQty4))
                    nQty4 = 0;

                if (nQty4!=0)
                {
                    trans.clear();

                    if (checksec("Sales_Part_API.Get_Cost",1,isSecure))
                    {
                        cmd = trans.addCustomFunction("COSTING","Sales_Part_API.Get_Cost","AMOUNT4");
                        cmd.addParameter("ITEM4_CONTRACT");
                        cmd.addParameter("ITEM4_CATALOG_NO");

                        trans = mgr.validate(trans);
                        nAmount = trans.getNumberValue("COSTING/DATA/AMOUNT4");
                        if (isNaN(nAmount))
                            nAmount=0;
                    }
                    else
                        nAmount= 0;
                }
            }

            String strAmount = mgr.getASPField("ITEM4_AMOUNT").formatNumber(nAmount);

            txt =  strAmount + "^" +
                   (mgr.isEmpty(sPriceListNo2) ? "" :sPriceListNo2) + "^";


            mgr.responseWrite(txt);
        }
        else if ("ITEM4_LIST_PRICE".equals(val))
        {
            nAgreementId = 0;
            nQty = mgr.readNumberValue("ITEM4_QTY");
            if (isNaN(nQty))
                nQty=0;
            nList =mgr.readNumberValue("ITEM4_LIST_PRICE");
            if (isNaN(nList))
                nList=0;
            nSalesPriceAmount =nQty* nList;

            String strSalesPriceAmount=mgr.getASPField("SALESPRICEAMOUNT").formatNumber(nSalesPriceAmount);
            String strAgreementId=mgr.getASPField("AGREEMENT_ID").formatNumber(nAgreementId);

            txt = strSalesPriceAmount + "^" + strAgreementId + "^";

            mgr.responseWrite(txt);
        }
        else if ("SIGNATURE".equals(val))
        {
            trans.clear();

            cmd = trans.addCustomFunction("MAXEMPID","Company_Emp_API.Get_Max_Employee_Id","SIGNATURE_ID");
            cmd.addParameter("ITEM4_COMPANY");
            cmd.addParameter("SIGNATURE");

            trans = mgr.validate(trans);

            sSignId= trans.getValue("MAXEMPID/DATA/SIGNATURE_ID");

            txt = (mgr.isEmpty(sSignId) ? "" : (sSignId))+ "^" ;
            mgr.responseWrite(txt);
        }
        else if ("ITEM4_DISCOUNT".equals(val))
        {
            trans.clear();

            double nDiscount = mgr.readNumberValue("ITEM4_DISCOUNT");
            if (isNaN(nDiscount))
                nDiscount=0;

            double nQtyInv =  mgr.readNumberValue("ITEM4_QTY_TO_INVOICE");
            if (isNaN(nQtyInv))
                nQtyInv=0;

            double nListPrice =  mgr.readNumberValue("ITEM4_LIST_PRICE");
            if (isNaN(nListPrice))
                nListPrice=0;

            double nDiscPrice = 0;

            if (nDiscount != 0)
                nDiscPrice = (nListPrice - ((nDiscount/100) * nListPrice));
            else
                nDiscPrice = mgr.readNumberValue("ITEM4_LIST_PRICE");

            double nSalesPriceAmount = nQtyInv * nDiscPrice;
            String strSalesPriceAmount=mgr.getASPField("SALESPRICEAMOUNT").formatNumber(nSalesPriceAmount);

            txt = (mgr.isEmpty(strSalesPriceAmount) ? "" : (strSalesPriceAmount))+ "^" ; 
            mgr.responseWrite(txt);
        }
        else if ("ITEM4_QTY_TO_INVOICE".equals(val))
        {
            trans.clear();

            double nDiscount = mgr.readNumberValue("ITEM4_DISCOUNT");
            if (isNaN(nDiscount))
                nDiscount=0;

            double nQtyInv =  mgr.readNumberValue("ITEM4_QTY_TO_INVOICE");
            if (isNaN(nQtyInv))
                nQtyInv=0;

            double nListPrice =  mgr.readNumberValue("ITEM4_LIST_PRICE");
            if (isNaN(nListPrice))
                nListPrice=0;

            double nDiscPrice = 0;

            if (nDiscount != 0)
                nDiscPrice = (nListPrice - ((nDiscount/100) * nListPrice));
            else
                nDiscPrice = nListPrice;

            double nSalesPriceAmount = nQtyInv * nDiscPrice;
            String strSalesPriceAmount=mgr.getASPField("SALESPRICEAMOUNT").formatNumber(nSalesPriceAmount);

            txt = (mgr.isEmpty(strSalesPriceAmount) ? "" : (strSalesPriceAmount))+ "^" ;
            mgr.responseWrite(txt);
        }
        //-------------------------------------------------------------------------------
        //-------------------------Material Tab Validations------------------------------
        //-------------------------------------------------------------------------------
        else if ("ITEM2_WO_NO".equals(val))
        {
            cmd = trans.addCustomFunction("PLANSDATE","Active_Work_Order_API.Get_Plan_S_Date","DUE_DATE");
            cmd.addParameter("WO_NO",mgr.readValue("ITEM2_WO_NO",""));

            cmd = trans.addCustomFunction("PREACCID","Active_Work_Order_API.Get_Pre_Accounting_Id","NPREACCOUNTINGID");
            cmd.addParameter("WO_NO",mgr.readValue("ITEM2_WO_NO",""));

            cmd = trans.addCustomFunction("ITEM2CONTRA","WORK_ORDER_API.Get_Contract","ITEM2_CONTRACT");
            cmd.addParameter("WO_NO",mgr.readValue("ITEM2_WO_NO",""));

            cmd = trans.addCustomFunction("COMP","Site_API.Get_Company","ITEM2_COMPANY");
            cmd.addReference("CONTRACT","ITEM2CONTRA/DATA");

            cmd = trans.addCustomFunction("MCHCOD","WORK_ORDER_API.Get_Mch_Code","MCHCODE");
            cmd.addParameter("WO_NO",mgr.readValue("ITEM2_WO_NO",""));

            cmd = trans.addCustomFunction("ITEM2DESC","Maintenance_Object_API.Get_Mch_Name","ITEM2DESCRIPTION");
            cmd.addReference("ITEM2_CONTRACT","ITEM2CONTRA/DATA");
            cmd.addReference("MCHCODE","MCHCOD/DATA");

            trans = mgr.validate(trans);

            dueDate = trans.getBuffer("PLANSDATE/DATA").getFieldValue("DUE_DATE");
            nPreAccId = trans.getValue("PREACCID/DATA/NPREACCOUNTINGID");
            item2Contract = trans.getBuffer("ITEM2CONTRA/DATA").getFieldValue("ITEM2_CONTRACT");
            company = trans.getBuffer("COMP/DATA").getFieldValue("ITEM2_COMPANY");
            mchCode = trans.getValue("MCHCOD/DATA/MCHCODE");
            item2Desc = trans.getValue("ITEM2DESC/DATA/ITEM2DESCRIPTION");

            txt = (mgr.isEmpty(dueDate) ? "": dueDate) + "^" +
                  (mgr.isEmpty(nPreAccId) ? "": nPreAccId) + "^" + 
                  (mgr.isEmpty(item2Contract) ? "": item2Contract) + "^" + 
                  (mgr.isEmpty(company) ? "": company) + "^" + 
                  (mgr.isEmpty(mchCode) ? "": mchCode) + "^" + 
                  (mgr.isEmpty(item2Desc) ? "": item2Desc) + "^";

            mgr.responseWrite(txt);
        }
        else if ("MCHCODE".equals(val))
        {
            cmd  = trans.addCustomFunction("ITEM2DESC","Maintenance_Object_API.Get_Mch_Name","ITEM2DESCRIPTION");
            cmd.addParameter("ITEM2_CONTRACT",mgr.readValue("ITEM2_CONTRACT",""));
            cmd.addParameter("MCHCODE");

            trans = mgr.validate(trans);

            item3Descr = trans.getValue("ITEM2DESC/DATA/ITEM2DESCRIPTION");
        }
        else if ("ITEM2_SIGNATURE".equals(val))
        {
            cmd = trans.addCustomFunction("SIGNID","Company_Emp_API.Get_Max_Employee_Id","ITEM2_SIGNATURE_ID");
            cmd.addParameter("COMPANY",mgr.readValue("ITEM2_COMPANY",""));
            cmd.addParameter("ITEM2_SIGNATURE");

            cmd = trans.addCustomFunction("SIGNNAME","EMPLOYEE_API.Get_Employee_Info","SIGNATURENAME");
            cmd.addParameter("COMPANY",mgr.readValue("ITEM2_COMPANY",""));
            cmd.addReference("ITEM2_SIGNATURE_ID","SIGNID/DATA");

            trans = mgr.validate(trans);

            signId = trans.getBuffer("SIGNID/DATA").getFieldValue("ITEM2_SIGNATURE_ID");
            signName = trans.getValue("SIGNNAME/DATA/SIGNATURENAME");

            txt = (mgr.isEmpty(signId) ? "": signId) + "^" + 
                  (mgr.isEmpty(signName) ? "": signName) + "^";

            mgr.responseWrite(txt);
        }
        else if ("INT_DESTINATION_ID".equals(val))
        {
            if (checksec("Internal_Destination_API.Get_Description",1,isSecure))
            {
                cmd = trans.addCustomFunction("INTDESTDESC","Internal_Destination_API.Get_Description","INT_DESTINATION_DESC");
                cmd.addParameter("CONTRACT",mgr.readValue("ITEM2_CONTRACT",""));
                cmd.addParameter("INT_DESTINATION_ID");

                trans = mgr.validate(trans);

                intDestDesc = trans.getValue("INTDESTDESC/DATA/INT_DESTINATION_DESC");
            }
            else
                intDestDesc = "";

            txt = (mgr.isEmpty(intDestDesc) ? "": intDestDesc) + "^";

            mgr.responseWrite(txt);
        }
        else if ("PART_NO".equals(val))
        {
            String dimQty = "";
            String salesPriceGroupId = "";
            String strQoh="";
            String suppCode = "";
            String defCond = new String();
            String condesc = new String();
            String sDefCondiCode= ""; 
            String qtyAvail1 = ""; 
            double qtyAvail = 0;
            String activeInd = "";
            String vendorNo = "";
            String custOwner = "";
            String partOwnership = "";
            String sOwner = mgr.readValue("OWNER");
            String ownership = "";
            //Bug 90330, Start
            double nExist = 0;
            //Bug 90330, End

            cmd = trans.addCustomFunction("GETCONDCODEUSAGEDB","PART_CATALOG_API.Get_Condition_Code_Usage_Db","COND_CODE_USAGE");
            cmd.addParameter("PART_NO");

            cmd = trans.addCustomFunction("DEFCONDCODE","CONDITION_CODE_API.Get_Default_Condition_Code","CONDITION_CODE");
            cmd = trans.addCustomFunction("MAINTREQSUP","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","SUPPLY_CODE");

            trans = mgr.validate(trans);
            if ("ALLOW_COND_CODE".equals(trans.getValue("GETCONDCODEUSAGEDB/DATA/COND_CODE_USAGE")))
            {
                sDefCondiCode = trans.getValue("DEFCONDCODE/DATA/CONDITION_CODE");
            }
            String sStandardInv = trans.getValue("MAINTREQSUP/DATA/SUPPLY_CODE");
            trans.clear();

            if (checksec("PURCHASE_PART_SUPPLIER_API.Get_Primary_Supplier_No",1,isSecure))
            {
                cmd = trans.addCustomFunction("VENDNO","PURCHASE_PART_SUPPLIER_API.Get_Primary_Supplier_No","VENDOR_NO");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("PART_NO");

                trans = mgr.validate(trans);

                vendorNo = trans.getValue("VENDNO/DATA/VENDOR_NO");
            
                trans.clear();
                cmd = trans.addCustomFunction("PARTOWNSHIP","Purchase_Part_Supplier_API.Get_Part_Ownership","PART_OWNERSHIP");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("PART_NO");
                cmd.addParameter("VENDOR_NO",vendorNo);

                cmd = trans.addCustomFunction("CUSTNO","Supplier_API.Get_Customer_No","CUSTOMER_NO");
                cmd.addParameter("VENDOR_NO",vendorNo);

                cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
                cmd.addReference("PART_OWNERSHIP","PARTOWNSHIP/DATA");

                trans = mgr.validate(trans);

                ownership = trans.getValue("PARTOWNSHIP/DATA/PART_OWNERSHIP");
                partOwnership = trans.getValue("VALIDOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");
                custOwner   = trans.getValue("CUSTNO/DATA/CUSTOMER_NO");
                if ((!mgr.isEmpty(vendorNo)) && "CUSTOMER OWNED".equals(partOwnership)) {
                       sClientOwnershipDefault = partOwnership;
                       sOwner = custOwner;
                }
            trans.clear();
            }

            if (checksec("Sales_Part_API.Get_Catalog_No_For_Part_No",1,isSecure))
            {
                cmd = trans.addCustomFunction("CATANO","Sales_Part_API.Get_Catalog_No_For_Part_No","ITEM_CATALOG_NO");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("PART_NO");

                cmd = trans.addCustomFunction("GETACT","Sales_Part_API.Get_Activeind","ACTIVEIND");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addReference("ITEM_CATALOG_NO","CATANO/DATA");
                cmd = trans.addCustomFunction("GETACTDB","Active_Sales_Part_API.Encode","ACTIVEIND_DB");
                cmd.addReference("ACTIVEIND","GETACT/DATA");
            }

            if (checksec("Sales_Part_API.Get_Catalog_Desc",2,isSecure))
            {
                cmd = trans.addCustomFunction("CATADESC","Sales_Part_API.Get_Catalog_Desc","ITEMCATALOGDESC");
                cmd.addParameter("CATALOG_CONTRACT");
                cmd.addReference("ITEM_CATALOG_NO","CATANO/DATA");
            }

            if (checksec("INVENTORY_PART_API.Get_Description",3,isSecure))
            {
                cmd = trans.addCustomFunction("PARTDESC","INVENTORY_PART_API.Get_Description","SPAREDESCRIPTION");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
            }

            cmd = trans.addCustomFunction("SPARESTRUCT","Equipment_Spare_Structure_API.Has_Spare_Structure_Boolean","HASSPARESTRUCTURE");
            cmd.addParameter("PART_NO");
            cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));

            if (checksec("INVENTORY_PART_API.Get_Dim_Quality",4,isSecure))
            {
                cmd = trans.addCustomFunction("DIMQUAL","INVENTORY_PART_API.Get_Dim_Quality","DIMQTY");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
            }

            if (checksec("INVENTORY_PART_API.Get_Type_Designation",5,isSecure))
            {
                cmd = trans.addCustomFunction("TYPEDESI","INVENTORY_PART_API.Get_Type_Designation","TYPEDESIGN");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
            }

            if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",6,isSecure))
            {
                if (sStandardInv.equals(mgr.readValue("SUPPLY_CODE")))
                {
                    //Qty OnHand
                    cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                    cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("CONFIGURATION");
                    cmd.addParameter("QTY_TYPE",sOnhand);
                    cmd.addParameter("EXPIRATION");
                    cmd.addParameter("SUPPLY_CONTROL");
                    cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                    cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
                    cmd.addParameter("OWNERSHIP_TYPE3");
                    cmd.addParameter("OWNERSHIP_TYPE4");
                    cmd.addParameter("OWNER",sOwner);
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
                    cmd.addParameter("ITEM2_ACTIVITY_SEQ","0");
                    cmd.addParameter("PROJECT_ID","");
                    cmd.addParameter("LOCATION_NO");
                    cmd.addParameter("ORDER_ISSUE");
                    cmd.addParameter("AUTOMAT_RESERV");
                    cmd.addParameter("MANUAL_RESERV");
                    cmd.addParameter("CONDITION_CODE",sDefCondiCode);

                    //Qty Available
                    cmd = trans.addCustomFunction("INVAVL","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
                    cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("CONFIGURATION");
                    cmd.addParameter("QTY_TYPE",sAvailable);
                    cmd.addParameter("EXPIRATION");
                    cmd.addParameter("SUPPLY_CONTROL", sNettable);
                    cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                    cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment); 
                    cmd.addParameter("OWNERSHIP_TYPE3");
                    cmd.addParameter("OWNERSHIP_TYPE4");
                    cmd.addParameter("OWNER",sOwner);
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
                    cmd.addParameter("ITEM2_ACTIVITY_SEQ","0");
                    cmd.addParameter("PROJECT_ID","");
                    cmd.addParameter("LOCATION_NO");
                    cmd.addParameter("ORDER_ISSUE");
                    cmd.addParameter("AUTOMAT_RESERV");
                    cmd.addParameter("MANUAL_RESERV");
                    cmd.addParameter("CONDITION_CODE",sDefCondiCode);
                }
                else
                {
                    //Bug 66456, Start, Added check on PROJ
                    if (mgr.isModuleInstalled("PROJ"))
                    {
                        cmd = trans.addCustomFunction("PROJID","Activity_API.Get_Project_Id","PROJECT_ID");
                        cmd.addParameter("ITEM2_ACTIVITY_SEQ",mgr.readValue("ITEM2_ACTIVITY_SEQ"));
                    }
                    //Bug 66456, End

                    //Qty OnHand
                    cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                    cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("CONFIGURATION");
                    cmd.addParameter("QTY_TYPE",sOnhand);
                    cmd.addParameter("EXPIRATION");
                    cmd.addParameter("SUPPLY_CONTROL");
                    cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                    cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
                    cmd.addParameter("OWNERSHIP_TYPE3");
                    cmd.addParameter("OWNERSHIP_TYPE4");
                    cmd.addParameter("OWNER",sOwner);
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
                    cmd.addParameter("INCLUDE_STANDARD",sFalse);
                    cmd.addParameter("INCLUDE_PROJECT",sTrue);
                    cmd.addParameter("ITEM2_ACTIVITY_SEQ","0");
                    //Bug 66456, Start, Added check on PROJ
                    if (mgr.isModuleInstalled("PROJ"))
                        cmd.addReference("PROJECT_ID", "PROJID/DATA");
                    else
                        cmd.addParameter("PROJECT_ID", "");
                    //Bug 66456, End
                    cmd.addParameter("LOCATION_NO");
                    cmd.addParameter("ORDER_ISSUE");
                    cmd.addParameter("AUTOMAT_RESERV");
                    cmd.addParameter("MANUAL_RESERV");
                    cmd.addParameter("CONDITION_CODE",sDefCondiCode);

                    //Qty Available
                    cmd = trans.addCustomFunction("INVAVL","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
                    cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("CONFIGURATION");
                    cmd.addParameter("QTY_TYPE",sAvailable);
                    cmd.addParameter("EXPIRATION");
                    cmd.addParameter("SUPPLY_CONTROL",sNettable);
                    cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                    cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment); 
                    cmd.addParameter("OWNERSHIP_TYPE3");
                    cmd.addParameter("OWNERSHIP_TYPE4");
                    cmd.addParameter("OWNER",sOwner);
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
                    cmd.addParameter("INCLUDE_STANDARD",sFalse);
                    cmd.addParameter("INCLUDE_PROJECT",sTrue);
                    cmd.addParameter("ITEM2_ACTIVITY_SEQ","0");
                    //Bug 66456, Start, Added check on PROJ
                    if (mgr.isModuleInstalled("PROJ"))
                        cmd.addReference("PROJECT_ID", "PROJID/DATA");
                    else
                        cmd.addParameter("PROJECT_ID", "");
                    //Bug 66456, End
                    cmd.addParameter("LOCATION_NO");
                    cmd.addParameter("ORDER_ISSUE");
                    cmd.addParameter("AUTOMAT_RESERV");
                    cmd.addParameter("MANUAL_RESERV");
                    cmd.addParameter("CONDITION_CODE",sDefCondiCode);
                }
            }

            if (checksec("Purchase_Part_Supplier_API.Get_Unit_Meas",7,isSecure))
            {
                cmd = trans.addCustomFunction("UNITMES","Purchase_Part_Supplier_API.Get_Unit_Meas","UNITMEAS");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
            }

            if (checksec("SALES_PART_API.GET_SALES_PRICE_GROUP_ID",8,isSecure))
            {
                cmd = trans.addCustomFunction("GETSALESPRICEGRPID","SALES_PART_API.GET_SALES_PRICE_GROUP_ID","SALES_PRICE_GROUP_ID");
                cmd.addParameter("CATALOG_CONTRACT");
                cmd.addReference("CATALOG_NO","CATANO/DATA");
            }

            if (checksec("INVENTORY_PART_API.Get_Supply_Code",9,isSecure))
            {
                cmd = trans.addCustomFunction("SUUPCODE","INVENTORY_PART_API.Get_Supply_Code ","SUPPLY_CODE");
                cmd.addParameter("CATALOG_CONTRACT");
                cmd.addReference("CATALOG_NO","CATANO/DATA");
            }

            cmd = trans.addCustomFunction("CONDALLOW","PART_CATALOG_API.Get_Condition_Code_Usage_Db","COND_CODE_USAGE");
            cmd.addParameter("PART_NO");

            trans = mgr.validate(trans);

            if ("true".equals(isSecure[1])){
                cataNo = trans.getBuffer("CATANO/DATA").getFieldValue("ITEM_CATALOG_NO");
                activeInd = trans.getValue("GETACTDB/DATA/ACTIVEIND_DB");
            }
            else
                cataNo = "";
            if ("true".equals(isSecure[2]))
                cataDesc = trans.getValue("CATADESC/DATA/ITEMCATALOGDESC");
            else
                cataDesc    = "";
            if ("true".equals(isSecure[3]))
                partDesc = trans.getValue("PARTDESC/DATA/SPAREDESCRIPTION");
            else
                partDesc    = "";
            if ("true".equals(isSecure[4]))
                dimQty = trans.getValue("DIMQUAL/DATA/DIMQTY");
            else
                dimQty = "";
            if ("true".equals(isSecure[5]))
                typeDesi = trans.getValue("TYPEDESI/DATA/TYPEDESIGN");
            else
                typeDesi    = "";
            if ("true".equals(isSecure[6]))
            {
                qtyOnHand = trans.getNumberValue("INVONHAND/DATA/QTYONHAND");
                if (isNaN(qtyOnHand))
                    qtyOnHand=0;
                strQoh=mgr.getASPField("QTYONHAND").formatNumber(qtyOnHand);

                qtyAvail = trans.getNumberValue("INVAVL/DATA/QTY_AVAILABLE");
                if ( isNaN(qtyAvail) )
                   qtyAvail = 0;
            }
            else
            {
                qtyOnHand = 0;
                if (isNaN(qtyOnHand))
                    qtyOnHand=0;
                strQoh="";

                qtyAvail = 0;
                if ( isNaN(qtyAvail) )
                   qtyAvail=0;
            }

            if ("true".equals(isSecure[7]))
                unitMeas = trans.getValue("UNITMES/DATA/UNITMEAS");
            else
                unitMeas    = "";
            if ("true".equals(isSecure[8]))
                salesPriceGroupId = trans.getValue("GETSALESPRICEGRPID/DATA/SALES_PRICE_GROUP_ID");
            else
                salesPriceGroupId   = "";
            if ("true".equals(isSecure[9]))
                suppCode = trans.getValue("SUUPCODE/DATA/SUPPLY_CODE");
            else
                suppCode    = "";
            
            qtyAvail1 = mgr.getASPField("QTY_AVAILABLE").formatNumber(qtyAvail);            

            hasStruct = trans.getValue("SPARESTRUCT/DATA/HASSPARESTRUCTURE");
            String condco = trans.getValue("CONDALLOW/DATA/COND_CODE_USAGE");

            trans.clear();

            cmd = trans.addCustomFunction("WOSITE","Work_Order_API.Get_Contract","WO_SITE");
            cmd.addParameter("ITEM_WO_NO",mgr.readValue("ITEM_WO_NO"));

            //Bug 90330, Start
            if (checksec("Sales_Part_API.Check_Exist",10,isSecure))
            {
               cmd = trans.addCustomFunction("CATEXT","Sales_Part_API.Check_Exist","CAT_EXIST");
               cmd.addReference("WO_SITE","WOSITE/DATA");
               cmd.addParameter("CATALOG_NO",cataNo);
            }
            //Bug 90330, End
            
            trans = mgr.validate(trans);

            //Bug 90330, Start
            if ("true".equals(isSecure[10]))
               nExist = trans.getNumberValue("CATEXT/DATA/CAT_EXIST");
            else
               nExist = 0;
            //Bug 90330, End

            if (nExist != 1)
            {
                cataNo = "";
                cataDesc = "";
                salesPriceGroupId = "";
                suppCode = "";
            }

            trans.clear();

            if ("ALLOW_COND_CODE".equals(condco))
            {
                cmd = trans.addCustomFunction("CONDCO","CONDITION_CODE_API.Get_Default_Condition_Code","DEF_COND_CODE");
                cmd = trans.addCustomFunction("CONCODE","CONDITION_CODE_API.Get_Description","CONDDESC");
                cmd.addReference("DEF_COND_CODE","CONDCO/DATA");
                trans = mgr.validate(trans);
                defCond = trans.getValue("CONDCO/DATA/DEF_COND_CODE");
                condesc = trans.getValue("CONCODE/DATA/CONDDESC");
                trans.clear();
            }

            if (checksec("Material_Requis_Line_API.CHECK_PART_NO__",1,isSecure))
            {
                cmd = trans.addCustomCommand("MATREQLINE","Material_Requis_Line_API.CHECK_PART_NO__");
                cmd.addParameter("SPAREDESCRIPTION",partDesc);
                cmd.addParameter("SUPPLY_CODE",suppCode);
                cmd.addParameter("UNITMEAS",unitMeas);
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
                cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));
            }

            mgr.perform(trans); 

            txt = (mgr.isEmpty(cataNo) ? "": cataNo) + "^" + 
                  (mgr.isEmpty(cataDesc) ? "": cataDesc) + "^" + 
                  (mgr.isEmpty(hasStruct) ? "": hasStruct) + "^" + 
                  (mgr.isEmpty(dimQty) ? "": dimQty) + "^" + 
                  (mgr.isEmpty(typeDesi) ? "": typeDesi) + "^" + 
                  (mgr.isEmpty(strQoh) ? "": strQoh) + "^" + 
                  (mgr.isEmpty(unitMeas) ? "": unitMeas) + "^" + 
                  (mgr.isEmpty(partDesc) ? "": partDesc) + "^" +
                  (mgr.isEmpty(salesPriceGroupId) ? "" : salesPriceGroupId) + "^"+
                  (mgr.isEmpty(defCond) ? "" : defCond) + "^"+
                  (mgr.isEmpty(condesc) ? "" : condesc) + "^"+ 
                  (mgr.isEmpty(qtyAvail1) ? "": qtyAvail1) + "^" + 
                  (mgr.isEmpty(activeInd) ? "": activeInd) + "^" +
                  (mgr.isEmpty(ownership) ? "": ownership) + "^" +
                  (mgr.isEmpty(sClientOwnershipDefault) ? "": sClientOwnershipDefault) + "^" +
                  (mgr.isEmpty(sOwner) ? "": sOwner) + "^" ;

            mgr.responseWrite(txt);
        }
        else if ("SUPPLY_CODE".equals(val))
        {
            String qtyOnHand1 = "";
            double qtyOnHand = 0;
            double qtyAvail = 0;

            sClientOwnershipDefault = mgr.readValue("PART_OWNERSHIP_DB");
            if (mgr.isEmpty(sClientOwnershipDefault))
                sClientOwnershipDefault = "COMPANY OWNED";

            if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",1,isSecure))
            {
                cmd = trans.addCustomFunction("MAINTREQSUP","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","SUPPLY_CODE");
                trans = mgr.validate(trans);
                String sStandardInv = trans.getValue("MAINTREQSUP/DATA/SUPPLY_CODE");
                trans.clear();

                if (sStandardInv.equals(mgr.readValue("SUPPLY_CODE")))
                {
                    //Qty OnHand
                    cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                    cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
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
                    cmd.addParameter("ITEM2_ACTIVITY_SEQ","0");
                    cmd.addParameter("PROJECT_ID","");
                    cmd.addParameter("LOCATION_NO");
                    cmd.addParameter("ORDER_ISSUE");
                    cmd.addParameter("AUTOMAT_RESERV");
                    cmd.addParameter("MANUAL_RESERV");
                    cmd.addParameter("CONDITION_CODE");

                    //Qty Available
                    cmd = trans.addCustomFunction("INVAVL","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
                    cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("CONFIGURATION");
                    cmd.addParameter("QTY_TYPE",sAvailable);
                    cmd.addParameter("EXPIRATION");
                    cmd.addParameter("SUPPLY_CONTROL", sNettable);
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
                    cmd.addParameter("ITEM2_ACTIVITY_SEQ","0");
                    cmd.addParameter("PROJECT_ID","");
                    cmd.addParameter("LOCATION_NO");
                    cmd.addParameter("ORDER_ISSUE");
                    cmd.addParameter("AUTOMAT_RESERV");
                    cmd.addParameter("MANUAL_RESERV");
                    cmd.addParameter("CONDITION_CODE");
                }
                else
                {
                    //Bug 66456, Start, Added check on PROJ
                    if (mgr.isModuleInstalled("PROJ"))
                    {
                        cmd = trans.addCustomFunction("PROJID","Activity_API.Get_Project_Id","PROJECT_ID");
                        cmd.addParameter("ITEM2_ACTIVITY_SEQ",mgr.readValue("ITEM2_ACTIVITY_SEQ"));
                    }
                    //Bug 66456, End

                    //Qty OnHand
                    cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                    cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
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
                    cmd.addParameter("INCLUDE_STANDARD",sFalse);
                    cmd.addParameter("INCLUDE_PROJECT",sTrue);
                    cmd.addParameter("ITEM2_ACTIVITY_SEQ","0");
                    //Bug 66456, Start, Added check on PROJ
                    if (mgr.isModuleInstalled("PROJ"))
                        cmd.addReference("PROJECT_ID", "PROJID/DATA");
                    else
                        cmd.addParameter("PROJECT_ID", "");
                    //Bug 66456, End
                    cmd.addParameter("LOCATION_NO");
                    cmd.addParameter("ORDER_ISSUE");
                    cmd.addParameter("AUTOMAT_RESERV");
                    cmd.addParameter("MANUAL_RESERV");
                    cmd.addParameter("CONDITION_CODE");

                    //Qty Available
                    cmd = trans.addCustomFunction("INVAVL","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
                    cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("CONFIGURATION");
                    cmd.addParameter("QTY_TYPE",sAvailable);
                    cmd.addParameter("EXPIRATION");
                    cmd.addParameter("SUPPLY_CONTROL", sNettable);
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
                    cmd.addParameter("INCLUDE_STANDARD",sFalse);
                    cmd.addParameter("INCLUDE_PROJECT",sTrue);
                    cmd.addParameter("ITEM2_ACTIVITY_SEQ","0");
                    //Bug 66456, Start, Added check on PROJ
                    if (mgr.isModuleInstalled("PROJ"))
                        cmd.addReference("PROJECT_ID", "PROJID/DATA");
                    else
                        cmd.addParameter("PROJECT_ID", "");
                    //Bug 66456, End
                    cmd.addParameter("LOCATION_NO");
                    cmd.addParameter("ORDER_ISSUE");
                    cmd.addParameter("AUTOMAT_RESERV");
                    cmd.addParameter("MANUAL_RESERV");
                    cmd.addParameter("CONDITION_CODE");
                }    
            }

            trans = mgr.validate(trans);
            if ("true".equals(isSecure[1]))
            {
                qtyOnHand1 = trans.getBuffer("INVONHAND/DATA").getFieldValue("QTYONHAND");
                qtyOnHand = trans.getNumberValue("NVONHAND/DATA/QTYONHAND");
                qtyAvail = trans.getNumberValue("INVAVL/DATA/QTY_AVAILABLE");
            }
            else
            {
                qtyOnHand1 = "";
                qtyOnHand = 0;
                qtyAvail = 0;
            }

            String qtyAvail1 = mgr.getASPField("QTY_AVAILABLE").formatNumber(qtyAvail);
                        
            txt = (mgr.isEmpty(qtyOnHand1) ? "": qtyOnHand1) + "^" +
                  (mgr.isEmpty(qtyAvail1) ? "": qtyAvail1) + "^" ;

            mgr.responseWrite(txt);    
        }


        else if ("PLAN_QTY".equals(val))
        {

            spareId = mgr.readValue("PART_NO","");
            nPriceListNo = mgr.readValue("ITEM_PRICE_LIST_NO","");

            nDiscount = mgr.readNumberValue("ITEM_DISCOUNT");
            if (isNaN(nDiscount))
                nDiscount=0;

            nListPrice = mgr.readNumberValue("ITEM_LIST_PRICE");
            if (isNaN(nListPrice))
                nListPrice=0;

            double nDiscountedPrice = mgr.readNumberValue("ITEMDISCOUNTEDPRICE");
            if (isNaN(nDiscountedPrice))
                nDiscountedPrice = 0;

            double nSalesPriceAmount = 0;

            if (!mgr.isEmpty(spareId))
            {
                /*if (checksec("Inventory_Part_API.Get_Inventory_Value_By_Method",1,isSecure))
                {
                    cmd = trans.addCustomFunction("GETINVVAL","Inventory_Part_API.Get_Inventory_Value_By_Method","ITEM_COST");
                    cmd.addParameter("SPARE_CONTRACT");
                    cmd.addParameter("PART_NO");

                    trans = mgr.validate(trans);

                    nCost = trans.getNumberValue("GETINVVAL/DATA/ITEM_COST");
                    if (isNaN(nCost))
                        nCost=0;
                }*/

                if (checksec("Active_Separate_API.Get_Inventory_Value",1,isSecure))
                {
                    cmd = trans.addCustomCommand("GETINVVAL","Active_Separate_API.Get_Inventory_Value");
                    cmd.addParameter("ITEM_COST");
                    cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));
                    cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
                    cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
                    cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
                    cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));

                    trans = mgr.validate(trans);

                    nCost = trans.getNumberValue("GETINVVAL/DATA/ITEM_COST");
                    
                    if (isNaN(nCost))
                        nCost=0;  
                }
                else
                    nCost=0;                  
            }

            planQty = mgr.readNumberValue("PLAN_QTY");
            if (isNaN(planQty))
                planQty=0;

            if (planQty==0)
                nAmountCost = 0;
            else
                nAmountCost = nCost * planQty;

            cataNo = mgr.readValue("ITEM_CATALOG_NO","");  

            if (!mgr.isEmpty(cataNo) && !(planQty==0))
            {
                trans.clear();

                cmd = trans.addCustomFunction("AGREID","Active_Work_Order_API.Get_Agreement_Id","AGREEMENT_ID");
                cmd.addParameter("ITEM_WO_NO");

                cmd = trans.addCustomFunction("CUSTONO","Active_Work_Order_API.Get_Customer_No","CUSTOMER_NO");
                cmd.addParameter("ITEM_WO_NO");

                cmd = trans.addCustomCommand("PRICEINF","Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("ITEM_BASE_PRICE","0");
                cmd.addParameter("ITEM_SALE_PRICE","0");
                cmd.addParameter("ITEM_DISCOUNT","0");
                cmd.addParameter("CURRENCY_RATE","0");
                cmd.addParameter("CATALOG_CONTRACT");
                cmd.addParameter("ITEM_CATALOG_NO");
                cmd.addReference("CUSTOMER_NO","CUSTONO/DATA");
                cmd.addReference("AGREEMENT_ID","AGREID/DATA");
                cmd.addParameter("ITEM_PRICE_LIST_NO");
                cmd.addParameter("PLAN_QTY");

                trans = mgr.validate(trans);

                if (mgr.isEmpty(nPriceListNo))
                    nPriceListNo = trans.getBuffer("PRICEINF/DATA").getFieldValue("ITEM_PRICE_LIST_NO");

                nDiscount = trans.getNumberValue("PRICEINF/DATA/DISCOUNT");
                if (isNaN(nDiscount))
                    nDiscount = 0;

                nListPrice = trans.getNumberValue("PRICEINF/DATA/ITEM_SALE_PRICE");
                if (isNaN(nListPrice))
                    nListPrice = 0;

                if (nListPrice == 0)
                {

                    nDiscountedPrice = nListPrice - (nDiscount / 100 * nListPrice);
                    nSalesPriceAmount = (nListPrice - (nDiscount / 100 * nListPrice)) * planQty;
                }
                else
                {
                    if (nDiscount == 0)
                    {
                        nSalesPriceAmount = nListPrice * planQty;
                        nDiscountedPrice = nListPrice;
                    }
                    else
                    {
                        nSalesPriceAmount = nListPrice * planQty; 
                        nSalesPriceAmount = nSalesPriceAmount - (nDiscount / 100 * nSalesPriceAmount);
                        nDiscountedPrice =  nListPrice - (nDiscount / 100 * nListPrice);
                    }
                }
            }

            txt = (nCost==0 ? mgr.getASPField("ITEM_COST").formatNumber(0): mgr.getASPField("ITEM_COST").formatNumber(nCost)) + "^" + (nAmountCost==0 ? mgr.getASPField("AMOUNTCOST").formatNumber(0): mgr.getASPField("AMOUNTCOST").formatNumber(nAmountCost)) + "^" + (mgr.isEmpty(nPriceListNo) ? "": nPriceListNo) + "^" + (nDiscount==0 ? mgr.getASPField("ITEM_DISCOUNT").formatNumber(0): mgr.getASPField("ITEM_DISCOUNT").formatNumber(nDiscount)) + "^" + (nListPrice==0 ? mgr.getASPField("ITEM_LIST_PRICE").formatNumber(0): mgr.getASPField("ITEM_LIST_PRICE").formatNumber(nListPrice)) + "^" + (nSalesPriceAmount==0 ? mgr.getASPField("ITEMSALESPRICEAMOUNT").formatNumber(0): mgr.getASPField("ITEMSALESPRICEAMOUNT").formatNumber(nSalesPriceAmount)) + "^" + (nDiscountedPrice==0 ? mgr.getASPField("ITEMDISCOUNTEDPRICE").formatNumber(0): mgr.getASPField("ITEMDISCOUNTEDPRICE").formatNumber(nDiscountedPrice)) + "^";

            mgr.responseWrite(txt); 
        }
        else if ("ITEM_CATALOG_NO".equals(val))
        {
            String salesPriceGroupId  = "";

            cmd = trans.addCustomFunction("CURRCO","ACTIVE_SEPARATE_API.Get_Currency_Code","CURRENCEY_CODE");
            cmd.addParameter("ITEM0_WO_NO");

            cmd = trans.addCustomFunction("AGREID","Active_Work_Order_API.Get_Agreement_Id","AGREEMENT_ID");
            cmd.addParameter("ITEM_WO_NO");

            cmd = trans.addCustomFunction("CUSTONO","Active_Work_Order_API.Get_Customer_No","CUSTOMER_NO");
            cmd.addParameter("ITEM_WO_NO");

            if (checksec("Customer_Order_Pricing_Api.Get_Valid_Price_List",1,isSecure))
            {
                cmd = trans.addCustomFunction("PRILST","Customer_Order_Pricing_Api.Get_Valid_Price_List","ITEM_PRICE_LIST_NO");
                cmd.addParameter("CATALOG_CONTRACT");
                cmd.addParameter("ITEM_CATALOG_NO");
                cmd.addReference("CUSTOMER_NO","CUSTONO/DATA");
                cmd.addReference("CURRENCEY_CODE","CURRCO/DATA");
            }

            cmd = trans.addCustomCommand("PRICEINF","Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("ITEM_BASE_PRICE","0"); 
            cmd.addParameter("ITEM_SALE_PRICE","0");
            cmd.addParameter("ITEM_DISCOUNT","0");
            cmd.addParameter("CURRENCY_RATE","0");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addParameter("ITEM_CATALOG_NO");
            cmd.addReference("CUSTOMER_NO","CUSTONO/DATA");
            cmd.addReference("AGREEMENT_ID","AGREID/DATA");
            cmd.addReference("ITEM_PRICE_LIST_NO","PRILST/DATA");
            cmd.addParameter("PLAN_QTY");

            if (checksec("Sales_Part_API.Get_Catalog_Desc",2,isSecure))
            {
                cmd = trans.addCustomFunction("CATADESC","Sales_Part_API.Get_Catalog_Desc","ITEMCATALOGDESC");
                cmd.addParameter("CATALOG_CONTRACT");
                cmd.addParameter("ITEM_CATALOG_NO");
            }

            if (checksec("SALES_PART_API.GET_SALES_PRICE_GROUP_ID",3,isSecure))
            {
                cmd = trans.addCustomFunction("GETSALESPRICEGRPID","SALES_PART_API.GET_SALES_PRICE_GROUP_ID","SALES_PRICE_GROUP_ID");
                cmd.addParameter("CATALOG_CONTRACT");
                cmd.addParameter("ITEM_CATALOG_NO");
            }

            spareId = mgr.readValue("PART_NO","");

            nCost = mgr.readNumberValue("ITEM_COST");
            if (isNaN(nCost))
                nCost=0;

            /*
            if (!mgr.isEmpty(spareId) && checksec("Inventory_Part_API.Get_Inventory_Value_By_Method",4,isSecure))
            {
                cmd = trans.addCustomFunction("GETINVVAL"," Inventory_Part_API.Get_Inventory_Value_By_Method","ITEM_COST");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("PART_NO");
            }*/

            if (!mgr.isEmpty(spareId) && checksec("Active_Separate_API.Get_Inventory_Value",4,isSecure))
            {
                cmd = trans.addCustomCommand("GETINVVAL","Active_Separate_API.Get_Inventory_Value");
                cmd.addParameter("ITEM_COST");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("PART_NO");
                cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
                cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
                cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE")); 
            }


            trans = mgr.validate(trans);

            if ("true".equals(isSecure[1]))
                nPriceListNo = trans.getValue("PRILST/DATA/ITEM_PRICE_LIST_NO");
            else
                nPriceListNo="";            

            if (mgr.isEmpty(nPriceListNo))
                nPriceListNo = trans.getValue("PRICEINF/DATA/ITEM_PRICE_LIST_NO");

            nDiscount = trans.getNumberValue("PRICEINF/DATA/ITEM_DISCOUNT");
            if (isNaN(nDiscount))
                nDiscount = 0;

            if ("true".equals(isSecure[2]))
                cataDesc = trans.getValue("CATADESC/DATA/ITEMCATALOGDESC");
            else
                cataDesc    = "";
            if ("true".equals(isSecure[3]))
                salesPriceGroupId = trans.getValue("GETSALESPRICEGRPID/DATA/SALES_PRICE_GROUP_ID");
            else
                salesPriceGroupId   = "";

            planQty = mgr.readNumberValue("PLAN_QTY");
            if (isNaN(planQty))
                planQty=0;

            double nListPrice = 0;
            double nSalesPriceAmount = 0;
            double nDiscountedPriceAmt = 0;

            nListPrice = trans.getNumberValue("PRICEINF/DATA/ITEM_SALE_PRICE");
            if (isNaN(nListPrice))
                nListPrice =0;

            if (nListPrice == 0)
            {
                nSalesPriceAmount  = (nListPrice - (nDiscount / 100 * nListPrice)) * planQty;
                nDiscountedPriceAmt = (nListPrice - (nDiscount / 100 * nListPrice));
            }
            else
            {
                if (nDiscount == 0)
                {
                    nSalesPriceAmount = nListPrice * planQty;
                    nDiscountedPriceAmt = nListPrice;
                }
                else
                {
                    nSalesPriceAmount = nListPrice * planQty;
                    nSalesPriceAmount = nSalesPriceAmount - (nDiscount / 100 * nSalesPriceAmount);
                    nDiscountedPriceAmt = (nListPrice - (nDiscount / 100 * nListPrice));
                }
            }

            if (!mgr.isEmpty(spareId))
            {
                if ("true".equals(isSecure[4]))
                {
                    nCost = trans.getNumberValue("GETINVVAL/DATA/ITEM_COST");
                    if (isNaN(nCost))
                        nCost=0;
                }
                else
                    nCost=0;                  
            }

            double nCostAmt = 0;

            if (planQty == 0)
                nCostAmt = 0;
            else
                nCostAmt    =  nCost *  planQty;
            
            String strListPrice = mgr.getASPField("ITEM_LIST_PRICE").formatNumber(nListPrice);
            String strCost = mgr.getASPField("ITEM_COST").formatNumber(nCost);
            String strCostAmt = mgr.getASPField("AMOUNTCOST").formatNumber(nCostAmt);
            String strDiscount = mgr.getASPField("ITEM_DISCOUNT").formatNumber(nDiscount);
            String strSalesPriceAmount = mgr.getASPField("SALESPRICEAMOUNT").formatNumber(nSalesPriceAmount);
            String strDiscountedPriceAmt = mgr.formatNumber("ITEMDISCOUNTEDPRICE",nDiscountedPriceAmt);

           

            txt = strListPrice + "^" +
                  strCost + "^" + 
                  strCostAmt + "^" + 
                  (mgr.isEmpty(cataDesc) ? "": cataDesc) + "^" + 
                  strDiscount + "^" + 
                  strSalesPriceAmount + "^"+strDiscountedPriceAmt + "^" + (mgr.isEmpty(nPriceListNo) ? "": nPriceListNo)+"^"+(mgr.isEmpty(salesPriceGroupId) ? "": salesPriceGroupId)+"^";
            
            mgr.responseWrite(txt);
        }
        else if ("ITEM_PRICE_LIST_NO".equals(val))
        {
            partNo = mgr.readValue("PART_NO","");
            nCostAmt = 0;
            String nPriceListNo = mgr.readValue("ITEM_PRICE_LIST_NO","");
            double nDiscountedPriceAmt = 0;

            if (!mgr.isEmpty(partNo))
            {
                /*
                if (checksec("Inventory_Part_API.Get_Inventory_Value_By_Method",1,isSecure))
                {
                    cmd = trans.addCustomFunction("GETINVAL","Inventory_Part_API.Get_Inventory_Value_By_Method","ITEM_COST");
                    cmd.addParameter("SPARE_CONTRACT");
                    cmd.addParameter("PART_NO");

                    trans = mgr.validate(trans);

                    nCost = trans.getNumberValue("GETINVAL/DATA/ITEM_COST");
                    if (isNaN(nCost))
                        nCost=0;
                }*/
                if (checksec("Active_Separate_API.Get_Inventory_Value",1,isSecure))
                {
                    cmd = trans.addCustomCommand("GETINVVAL","Active_Separate_API.Get_Inventory_Value");
                    cmd.addParameter("ITEM_COST");
                    cmd.addParameter("SPARE_CONTRACT");
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
                    cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
                    cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));
                    
                    trans = mgr.validate(trans);

                    nCost = trans.getNumberValue("GETINVVAL/DATA/ITEM_COST");
                    if (isNaN(nCost))
                        nCost=0;
                }
                else
                    nCost=0;                  
            }

            planQty = mgr.readNumberValue("PLAN_QTY");
            if (isNaN(planQty))
                planQty=0;

            if (planQty == 0)
                nCostAmt = 0;
            else
                nCostAmt    = nCost * planQty;

            cataNo = mgr.readValue("ITEM_CATALOG_NO",""); 

            if (!mgr.isEmpty(cataNo) && planQty!=0)
            {
                trans.clear(   );

                cmd = trans.addCustomFunction("AGREID","Active_Work_Order_API.Get_Agreement_Id","AGREEMENT_ID");
                cmd.addParameter("ITEM_WO_NO");

                cmd = trans.addCustomFunction("CUSTONO","Active_Work_Order_API.Get_Customer_No","CUSTOMER_NO");
                cmd.addParameter("ITEM_WO_NO");

                cmd = trans.addCustomCommand("PRICEINF","Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("ITEM_BASE_PRICE","0"); 
                cmd.addParameter("ITEM_SALE_PRICE","0");
                cmd.addParameter(      "ITEM_DISCOUNT","0");
                cmd.addParameter("CURRENCY_RATE","0");
                cmd.addParameter("CATALOG_CONTRACT");
                cmd.addParameter("ITEM_CATALOG_NO");
                cmd.addReference("CUSTOMER_NO","CUSTONO/DATA");
                cmd.addReference("AGREEMENT_ID","AGREID/DATA");
                cmd.addParameter("ITEM_PRICE_LIST_NO");
                cmd.addParameter("PLAN_QTY");

                trans = mgr.validate(trans);        

                if (mgr.isEmpty(nPriceListNo))
                    nPriceListNo = trans.getBuffer("PRICEINF/DATA").getFieldValue("ITEM_PRICE_LIST_NO");

                nDiscount = trans.getNumberValue("PRICEINF/DATA/ITEM_DISCOUNT");
                if (isNaN(nDiscount))
                    nDiscount = 0;


                nListPrice = trans.getNumberValue("PRICEINF/DATA/ITEM_SALE_PRICE");
                if (isNaN(nListPrice))
                    nListPrice =0;

                if (nListPrice == 0)
                {
                    nSalesPriceAmount  = (nListPrice - (nDiscount / 100 * nListPrice)) * planQty;
                    nDiscountedPriceAmt = (nListPrice - (nDiscount / 100 * nListPrice));
                }
                else
                {
                    if (nDiscount == 0)
                    {
                        nSalesPriceAmount = nListPrice * planQty;
                        nDiscountedPriceAmt = nListPrice;
                    }
                    else
                    {
                        nSalesPriceAmount = nListPrice * planQty;
                        nSalesPriceAmount = nSalesPriceAmount - (nDiscount / 100 * nSalesPriceAmount);
                        nDiscountedPriceAmt = (nListPrice - (nDiscount / 100 * nListPrice));
                    }
                }
            }

            String strListPrice=mgr.getASPField("ITEM_LIST_PRICE").formatNumber(nListPrice);
            String strCost=mgr.getASPField("ITEM_COST").formatNumber(nCost);
            String strCostAmt=mgr.getASPField("AMOUNTCOST").formatNumber(nCostAmt);
            String strDiscount=mgr.getASPField("ITEM_DISCOUNT").formatNumber(nDiscount);
            String strSalesPriceAmount=mgr.getASPField("SALESPRICEAMOUNT").formatNumber(nSalesPriceAmount);
            String nDiscountedPriceAmtStr = mgr.formatNumber("ITEMDISCOUNTEDPRICE",nDiscountedPriceAmt);

            txt =  strCost + "^" + 
                   strCostAmt + "^" + 
                   strListPrice + "^"+ 
                   strDiscount + "^" + 
                   strSalesPriceAmount + "^"+nDiscountedPriceAmtStr + "^";

            mgr.responseWrite(txt);
        }
        else if ("ITEM_DISCOUNT".equals(val))
        {
            nListPrice = mgr.readNumberValue("ITEM_LIST_PRICE");
            if (isNaN(nListPrice))
                nListPrice=0;

            planQty = mgr.readNumberValue("PLAN_QTY");
            if (isNaN(planQty))
                planQty=0;

            double discountedPrice = 0;

            nDiscount = mgr.readNumberValue("ITEM_DISCOUNT"); 
            if (isNaN(nDiscount))
                nDiscount=0;

            salePriceAmt =  nListPrice * planQty;
            salePriceAmt =  salePriceAmt - (nDiscount / 100 * salePriceAmt);
            discountedPrice = nListPrice - (nDiscount / 100 * nListPrice);       

            txt = (salePriceAmt==0 ? mgr.getASPField("ITEMSALESPRICEAMOUNT").formatNumber(0): mgr.getASPField("ITEMSALESPRICEAMOUNT").formatNumber(salePriceAmt)) + "^" + (discountedPrice==0 ? mgr.getASPField("ITEMDISCOUNTEDPRICE").formatNumber(0): mgr.getASPField("ITEMDISCOUNTEDPRICE").formatNumber(discountedPrice)) + "^";

            mgr.responseWrite(txt);
        }
        else if ("CONDITION_CODE".equals(val))
        {
            String qtyOnHand1 = ""; 
            String qtyAvail1 = "";
            double qtyOnHand = 0;
            double qtyAvail = 0;

            if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",1,isSecure))
            {
                cmd = trans.addCustomFunction("MAINTREQSUP","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","SUPPLY_CODE");
                trans = mgr.validate(trans);
                String sStandardInv = trans.getValue("MAINTREQSUP/DATA/SUPPLY_CODE");
                trans.clear();

                if ("COMPANY OWNED".equals(sClientOwnershipDefault))
                    sClientOwnershipConsignment="CONSIGNMENT";
                else
                    sClientOwnershipConsignment=null;


                if (sStandardInv.equals(mgr.readValue("SUPPLY_CODE")))
                {
                    //Qty OnHand
                    cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                    cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("CONFIGURATION");
                    cmd.addParameter("QTY_TYPE",sOnhand);
                    cmd.addParameter("EXPIRATION");
                    cmd.addParameter("SUPPLY_CONTROL");
                    cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                    cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
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
                    cmd.addParameter("ITEM2_ACTIVITY_SEQ","0");
                    cmd.addParameter("PROJECT_ID","");
                    cmd.addParameter("LOCATION_NO");
                    cmd.addParameter("ORDER_ISSUE");
                    cmd.addParameter("AUTOMAT_RESERV");
                    cmd.addParameter("MANUAL_RESERV");
                    cmd.addParameter("CONDITION_CODE");

                    //Qty Available
                    cmd = trans.addCustomFunction("INVAVL","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
                    cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("CONFIGURATION");
                    cmd.addParameter("QTY_TYPE",sAvailable);
                    cmd.addParameter("EXPIRATION");
                    cmd.addParameter("SUPPLY_CONTROL", sNettable);
                    cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                    cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
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
                    cmd.addParameter("ITEM2_ACTIVITY_SEQ","0");
                    cmd.addParameter("PROJECT_ID","");
                    cmd.addParameter("LOCATION_NO");
                    cmd.addParameter("ORDER_ISSUE");
                    cmd.addParameter("AUTOMAT_RESERV");
                    cmd.addParameter("MANUAL_RESERV");
                    cmd.addParameter("CONDITION_CODE");
                }
                else
                {
                    //Bug 66456, Start, Added check on PROJ
                    if (mgr.isModuleInstalled("PROJ"))
                    {
                        cmd = trans.addCustomFunction("PROJID","Activity_API.Get_Project_Id","PROJECT_ID");
                        cmd.addParameter("ITEM2_ACTIVITY_SEQ",mgr.readValue("ITEM2_ACTIVITY_SEQ"));
                    }
                    //Bug 66456, End

                    //Qty OnHand
                    cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                    cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("CONFIGURATION");
                    cmd.addParameter("QTY_TYPE",sOnhand);
                    cmd.addParameter("EXPIRATION");
                    cmd.addParameter("SUPPLY_CONTROL");
                    cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                    cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
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
                    cmd.addParameter("INCLUDE_STANDARD",sFalse);
                    cmd.addParameter("INCLUDE_PROJECT",sTrue);
                    cmd.addParameter("ITEM2_ACTIVITY_SEQ","0");
                    //Bug 66456, Start, Added check on PROJ
                    if (mgr.isModuleInstalled("PROJ"))
                        cmd.addReference("PROJECT_ID", "PROJID/DATA");
                    else
                        cmd.addParameter("PROJECT_ID", "");
                    //Bug 66456, End
                    cmd.addParameter("LOCATION_NO");
                    cmd.addParameter("ORDER_ISSUE");
                    cmd.addParameter("AUTOMAT_RESERV");
                    cmd.addParameter("MANUAL_RESERV");
                    cmd.addParameter("CONDITION_CODE");

                    //Qty Available
                    cmd = trans.addCustomFunction("INVAVL","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
                    cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("CONFIGURATION");
                    cmd.addParameter("QTY_TYPE",sAvailable);
                    cmd.addParameter("EXPIRATION");
                    cmd.addParameter("SUPPLY_CONTROL", sNettable);
                    cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                    cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
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
                    cmd.addParameter("INCLUDE_STANDARD",sFalse);
                    cmd.addParameter("INCLUDE_PROJECT",sTrue);
                    cmd.addParameter("ITEM2_ACTIVITY_SEQ","0");
                    //Bug 66456, Start, Added check on PROJ
                    if (mgr.isModuleInstalled("PROJ"))
                        cmd.addReference("PROJECT_ID", "PROJID/DATA");
                    else
                        cmd.addParameter("PROJECT_ID", "");
                    //Bug 66456, End
                    cmd.addParameter("LOCATION_NO");
                    cmd.addParameter("ORDER_ISSUE");
                    cmd.addParameter("AUTOMAT_RESERV");
                    cmd.addParameter("MANUAL_RESERV");
                    cmd.addParameter("CONDITION_CODE");
                }    
            }

            cmd = trans.addCustomFunction("CONCODE","CONDITION_CODE_API.Get_Description","CONDDESC");
            cmd.addParameter("CONDITION_CODE"); 
                                   
            trans = mgr.validate(trans);
            if ("true".equals(isSecure[1]))
            {
                qtyOnHand = trans.getNumberValue("INVONHAND/DATA/QTYONHAND"); 
                qtyAvail = trans.getNumberValue("INVAVL/DATA/QTY_AVAILABLE");
            }
            else
            {
                qtyOnHand = 0;  
                qtyAvail = 0;
            }

            qtyOnHand1 = mgr.getASPField("QTYONHAND").formatNumber(qtyOnHand);
            qtyAvail1 = mgr.getASPField("QTY_AVAILABLE").formatNumber(qtyAvail);

            String descri = trans.getValue("CONCODE/DATA/CONDDESC");

            trans.clear();
            
            if (checksec("Active_Separate_API.Get_Inventory_Value",1,isSecure))
            {
                cmd = trans.addCustomCommand("GETINVVAL","Active_Separate_API.Get_Inventory_Value");
                cmd.addParameter("ITEM_COST");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("PART_NO");
                cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
                cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
                cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));
    
                trans = mgr.validate(trans);
    
                nCost = trans.getNumberValue("GETINVVAL/DATA/ITEM_COST");
    
                if (isNaN(nCost))
                    nCost=0;
            } else
                nCost=0;

            planQty = mgr.readNumberValue("PLAN_QTY");
            if (isNaN(planQty))
                planQty=0;
           
            if (planQty == 0)
                nCostAmt = 0;
            else
                nCostAmt    = nCost * planQty;
           
           
            String strCost=mgr.getASPField("ITEM_COST").formatNumber(nCost);
            String strCostAmt=mgr.getASPField("AMOUNTCOST").formatNumber(nCostAmt);
            
            txt = (mgr.isEmpty(descri) ? "" : (descri))+ "^" +
                  (mgr.isEmpty(qtyOnHand1) ? "": qtyOnHand1) + "^" +
                  (mgr.isEmpty(qtyAvail1) ? "": qtyAvail1) + "^"+
                  (mgr.isEmpty(strCost) ? "": strCost) + "^" +
                  (mgr.isEmpty(strCostAmt) ? "": strCostAmt) + "^"; 

            mgr.responseWrite(txt);
        }
        else if ("PART_OWNERSHIP".equals(val))
        {
            String qtyOnHand1 = "";
            String qtyAvail1 = "";
            double qtyOnHand = 0;
            double qtyAvail = 0;

            cmd = trans.addCustomFunction("MAINTREQSUP","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","SUPPLY_CODE");
            trans = mgr.validate(trans);
            String sStandardInv = trans.getValue("MAINTREQSUP/DATA/SUPPLY_CODE");
            trans.clear();

            cmd = trans.addCustomFunction("VALOWNERSHIPDBROUTE", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
            cmd.addParameter("PART_OWNERSHIP");

            if ("COMPANY OWNED".equals(sClientOwnershipDefault))
                sClientOwnershipConsignment="CONSIGNMENT";
            else
                sClientOwnershipConsignment=null;


            if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",1,isSecure))
            {
                if (sStandardInv.equals(mgr.readValue("SUPPLY_CODE")))
                {
                    //Qty OnHand
                    cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                    cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("CONFIGURATION");
                    cmd.addParameter("QTY_TYPE",sOnhand);
                    cmd.addParameter("EXPIRATION");
                    cmd.addParameter("SUPPLY_CONTROL");
                    cmd.addReference("PART_OWNERSHIP_DB","VALOWNERSHIPDBROUTE/DATA");
                    cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
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
                    cmd.addParameter("ITEM2_ACTIVITY_SEQ","0");
                    cmd.addParameter("PROJECT_ID","");
                    cmd.addParameter("LOCATION_NO");
                    cmd.addParameter("ORDER_ISSUE");
                    cmd.addParameter("AUTOMAT_RESERV");
                    cmd.addParameter("MANUAL_RESERV");
                    cmd.addParameter("CONDITION_CODE");

                    //Qty Available
                    cmd = trans.addCustomFunction("INVAVL","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
                    cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("CONFIGURATION");
                    cmd.addParameter("QTY_TYPE",sAvailable);
                    cmd.addParameter("EXPIRATION");
                    cmd.addParameter("SUPPLY_CONTROL", sNettable);
                    cmd.addReference("PART_OWNERSHIP_DB","VALOWNERSHIPDBROUTE/DATA");
                    cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
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
                    cmd.addParameter("ITEM2_ACTIVITY_SEQ","0");
                    cmd.addParameter("PROJECT_ID","");
                    cmd.addParameter("LOCATION_NO");
                    cmd.addParameter("ORDER_ISSUE");
                    cmd.addParameter("AUTOMAT_RESERV");
                    cmd.addParameter("MANUAL_RESERV");
                    cmd.addParameter("CONDITION_CODE");
                }
                else
                {
                    //Bug 66456, Start, Added check on PROJ
                    if (mgr.isModuleInstalled("PROJ"))
                    {
                        cmd = trans.addCustomFunction("PROJID","Activity_API.Get_Project_Id","PROJECT_ID");
                        cmd.addParameter("ITEM2_ACTIVITY_SEQ",mgr.readValue("ITEM2_ACTIVITY_SEQ"));
                    }
                    //Bug 66456, End

                    //Qty OnHand
                    cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                    cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("CONFIGURATION");
                    cmd.addParameter("QTY_TYPE",sOnhand);
                    cmd.addParameter("EXPIRATION");
                    cmd.addParameter("SUPPLY_CONTROL");
                    cmd.addReference("PART_OWNERSHIP_DB","VALOWNERSHIPDBROUTE/DATA");
                    cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
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
                    cmd.addParameter("INCLUDE_STANDARD",sFalse);
                    cmd.addParameter("INCLUDE_PROJECT",sTrue);
                    cmd.addParameter("ITEM2_ACTIVITY_SEQ","0");
                    //Bug 66456, Start, Added check on PROJ
                    if (mgr.isModuleInstalled("PROJ"))
                        cmd.addReference("PROJECT_ID", "PROJID/DATA");
                    else
                        cmd.addParameter("PROJECT_ID", "");
                    //Bug 66456, End
                    cmd.addParameter("LOCATION_NO");
                    cmd.addParameter("ORDER_ISSUE");
                    cmd.addParameter("AUTOMAT_RESERV");
                    cmd.addParameter("MANUAL_RESERV");
                    cmd.addParameter("CONDITION_CODE");

                    //Qty Available
                    cmd = trans.addCustomFunction("INVAVL","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
                    cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("CONFIGURATION");
                    cmd.addParameter("QTY_TYPE",sAvailable);
                    cmd.addParameter("EXPIRATION");
                    cmd.addParameter("SUPPLY_CONTROL", sNettable);
                    cmd.addReference("PART_OWNERSHIP_DB","VALOWNERSHIPDBROUTE/DATA");
                    cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
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
                    cmd.addParameter("INCLUDE_STANDARD",sFalse);
                    cmd.addParameter("INCLUDE_PROJECT",sTrue);
                    cmd.addParameter("ITEM2_ACTIVITY_SEQ","0");
                    //Bug 66456, Start, Added check on PROJ
                    if (mgr.isModuleInstalled("PROJ"))
                        cmd.addReference("PROJECT_ID", "PROJID/DATA");
                    else
                        cmd.addParameter("PROJECT_ID", "");
                    //Bug 66456, End
                    cmd.addParameter("LOCATION_NO");
                    cmd.addParameter("ORDER_ISSUE");
                    cmd.addParameter("AUTOMAT_RESERV");
                    cmd.addParameter("MANUAL_RESERV");
                    cmd.addParameter("CONDITION_CODE");
                }
            }

            trans = mgr.validate(trans);

            if ("true".equals(isSecure[1]))
            {
                qtyOnHand = trans.getNumberValue("INVONHAND/DATA/QTYONHAND"); 
                qtyAvail = trans.getNumberValue("INVAVL/DATA/QTY_AVAILABLE");
            }
            else
            {
                qtyOnHand = 0;  
                qtyAvail = 0;
            }

            qtyOnHand1 = mgr.getASPField("QTYONHAND").formatNumber(qtyOnHand);
            qtyAvail1 = mgr.getASPField("QTY_AVAILABLE").formatNumber(qtyAvail);
            
            String sOwnershipDb = trans.getValue("VALOWNERSHIPDBROUTE/DATA/PART_OWNERSHIP_DB"); 

            txt = (mgr.isEmpty(sOwnershipDb) ? "" : (sOwnershipDb)) + "^"+
                  (mgr.isEmpty(qtyOnHand1) ? "": qtyOnHand1) + "^" +
                  (mgr.isEmpty(qtyAvail1) ? "": qtyAvail1) + "^" ; 

            mgr.responseWrite(txt);
        }
        else if ("OWNER".equals(val))
        {    
            String qtyOnHand1 = "";
            String qtyAvail1 = "";
            double qtyOnHand = 0;
            double qtyAvail = 0;

            if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",1,isSecure))
            {
                cmd = trans.addCustomFunction("MAINTREQSUP","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","SUPPLY_CODE");
                trans = mgr.validate(trans);
                String sStandardInv = trans.getValue("MAINTREQSUP/DATA/SUPPLY_CODE");
                trans.clear();

                if ("COMPANY OWNED".equals(sClientOwnershipDefault))
                    sClientOwnershipConsignment="CONSIGNMENT";
                else
                    sClientOwnershipConsignment=null;

                if (sStandardInv.equals(mgr.readValue("SUPPLY_CODE")))
                {
                    //Qty OnHand
                    cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                    cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("CONFIGURATION");
                    cmd.addParameter("QTY_TYPE",sOnhand);
                    cmd.addParameter("EXPIRATION");
                    cmd.addParameter("SUPPLY_CONTROL");
                    cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                    cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
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
                    cmd.addParameter("ITEM2_ACTIVITY_SEQ","0");
                    cmd.addParameter("PROJECT_ID","");
                    cmd.addParameter("LOCATION_NO");
                    cmd.addParameter("ORDER_ISSUE");
                    cmd.addParameter("AUTOMAT_RESERV");
                    cmd.addParameter("MANUAL_RESERV");
                    cmd.addParameter("CONDITION_CODE");

                    //Qty Available
                    cmd = trans.addCustomFunction("INVAVL","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
                    cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("CONFIGURATION");
                    cmd.addParameter("QTY_TYPE",sAvailable);
                    cmd.addParameter("EXPIRATION");
                    cmd.addParameter("SUPPLY_CONTROL", sNettable);
                    cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                    cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
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
                    cmd.addParameter("ITEM2_ACTIVITY_SEQ","0");
                    cmd.addParameter("PROJECT_ID","");
                    cmd.addParameter("LOCATION_NO");
                    cmd.addParameter("ORDER_ISSUE");
                    cmd.addParameter("AUTOMAT_RESERV");
                    cmd.addParameter("MANUAL_RESERV");
                    cmd.addParameter("CONDITION_CODE");
                }
                else
                {
                    //Bug 66456, Start, Added check on PROJ
                    if (mgr.isModuleInstalled("PROJ"))
                    {
                        cmd = trans.addCustomFunction("PROJID","Activity_API.Get_Project_Id","PROJECT_ID");
                        cmd.addParameter("ITEM2_ACTIVITY_SEQ",mgr.readValue("ITEM2_ACTIVITY_SEQ"));
                    }
                    //Bug 66456, End

                    //Qty OnHand
                    cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                    cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("CONFIGURATION");
                    cmd.addParameter("QTY_TYPE",sOnhand);
                    cmd.addParameter("EXPIRATION");
                    cmd.addParameter("SUPPLY_CONTROL");
                    cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                    cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
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
                    cmd.addParameter("INCLUDE_STANDARD",sFalse);
                    cmd.addParameter("INCLUDE_PROJECT",sTrue);
                    cmd.addParameter("ITEM2_ACTIVITY_SEQ","0");
                    //Bug 66456, Start, Added check on PROJ
                    if (mgr.isModuleInstalled("PROJ"))
                        cmd.addReference("PROJECT_ID", "PROJID/DATA");
                    else
                        cmd.addParameter("PROJECT_ID", "");
                    //Bug 66456, End
                    cmd.addParameter("LOCATION_NO");
                    cmd.addParameter("ORDER_ISSUE");
                    cmd.addParameter("AUTOMAT_RESERV");
                    cmd.addParameter("MANUAL_RESERV");
                    cmd.addParameter("CONDITION_CODE");

                    //Qty Available
                    cmd = trans.addCustomFunction("INVAVL","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
                    cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("CONFIGURATION");
                    cmd.addParameter("QTY_TYPE",sAvailable);
                    cmd.addParameter("EXPIRATION");
                    cmd.addParameter("SUPPLY_CONTROL", sNettable);
                    cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                    cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
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
                    cmd.addParameter("INCLUDE_STANDARD",sFalse);
                    cmd.addParameter("INCLUDE_PROJECT",sTrue);
                    cmd.addParameter("ITEM2_ACTIVITY_SEQ","0");
                    //Bug 66456, Start, Added check on PROJ
                    if (mgr.isModuleInstalled("PROJ"))
                        cmd.addReference("PROJECT_ID", "PROJID/DATA");
                    else
                        cmd.addParameter("PROJECT_ID", "");
                    //Bug 66456, End
                    cmd.addParameter("LOCATION_NO");
                    cmd.addParameter("ORDER_ISSUE");
                    cmd.addParameter("AUTOMAT_RESERV");
                    cmd.addParameter("MANUAL_RESERV");
                    cmd.addParameter("CONDITION_CODE");
                }
            }

            cmd = trans.addCustomFunction("GETOWNERNAMEROUTE", "CUSTOMER_INFO_API.Get_Name", "OWNER_NAME");
            cmd.addParameter("OWNER");

            cmd = trans.addCustomFunction("GETOWCUSTROUTE","ACTIVE_SEPARATE_API.Get_Customer_No", "WO_CUST");
            cmd.addParameter("WO_NO",mgr.readValue("ITEM2_WO_NO",""));

            trans = mgr.validate(trans);

            if ("true".equals(isSecure[1]))
            {
                qtyOnHand = trans.getNumberValue("INVONHAND/DATA/QTYONHAND");
                qtyAvail = trans.getNumberValue("INVAVL/DATA/QTY_AVAILABLE");
            }
            else
            {
                qtyOnHand = 0; 
                qtyAvail = 0;
            }
            
            qtyOnHand1 = mgr.getASPField("QTYONHAND").formatNumber(qtyOnHand);
            qtyAvail1 = mgr.getASPField("QTY_AVAILABLE").formatNumber(qtyAvail);

            String sOwnerName = trans.getValue("GETOWNERNAMEROUTE/DATA/OWNER_NAME");
            String sWoCust    = trans.getValue("GETOWCUSTROUTE/DATA/WO_CUST");

            txt = (mgr.isEmpty(sOwnerName) ? "" : (sOwnerName)) + "^" + (mgr.isEmpty(sWoCust) ? "": (sWoCust)) + "^" + (mgr.isEmpty(qtyOnHand1) ? "": qtyOnHand1) + "^" +(mgr.isEmpty(qtyAvail1) ? "": qtyAvail1) + "^" ; //Bug 43249 
            mgr.responseWrite(txt);
        }
        else if ("STD_JOB_ID".equals(val))
        {
            String valueStr = mgr.readValue("STD_JOB_ID");
            String stdJobId  = "";
            String stdJobContract = mgr.readValue("STD_JOB_CONTRACT");
            String stdJobRev = mgr.readValue("STD_JOB_REVISION");
            String desc = "";

            if (valueStr.indexOf("~") > -1)
            {
                String[] fieldValues = valueStr.split("~");

                stdJobId = fieldValues[0];
                stdJobRev = fieldValues[2];
            }
            else
            {
                stdJobId = valueStr;
                stdJobRev = mgr.readValue("STD_JOB_REVISION");
                //Bug 67801, start
                 trans.clear();

                 cmd = trans.addCustomFunction("GETREV", "Standard_Job_API.Get_Active_Revision", "STD_JOB_REVISION");
                 cmd.addParameter("STD_JOB_ID", stdJobId);
                 cmd.addParameter("STD_JOB_CONTRACT", stdJobContract);
                 
                 trans = mgr.validate(trans);

                 stdJobRev = trans.getValue("GETREV/DATA/STD_JOB_REVISION");
                //Bug 67801, end
            }

            trans.clear();

            cmd = trans.addCustomFunction("GETDESC", "Standard_Job_API.Get_Definition", "DESCRIPTION");
            cmd.addParameter("STD_JOB_ID", stdJobId);
            cmd.addParameter("STD_JOB_CONTRACT", stdJobContract);
            cmd.addParameter("STD_JOB_REVISION", stdJobRev);

            cmd = trans.addCustomFunction("WORKDESC","Standard_Job_API.Get_Work_description","DESCRIPTION");
            cmd.addParameter("STD_JOB_ID", stdJobId);
            cmd.addParameter("STD_JOB_CONTRACT", stdJobContract);
            cmd.addParameter("STD_JOB_REVISION", stdJobRev);

            cmd = trans.addCustomFunction("GETSTATUS","Standard_Job_API.Get_Status","STD_JOB_STATUS");
            cmd.addParameter("STD_JOB_ID",stdJobId);
            cmd.addParameter("STD_JOB_CONTRACT",stdJobContract);
            cmd.addParameter("STD_JOB_REVISION",stdJobRev);

            trans = mgr.validate(trans);

            String definition =   trans.getValue("GETDESC/DATA/DESCRIPTION");  
            String workDesc =   trans.getValue("WORKDESC/DATA/DESCRIPTION");  
            String status = trans.getValue("GETSTATUS/DATA/STD_JOB_STATUS");

            if (!mgr.isEmpty(workDesc)) {
               desc = workDesc;
            }
            else
               desc = definition;

            txt = (mgr.isEmpty(stdJobId)?"":stdJobId) + "^" + (mgr.isEmpty(stdJobRev)?"":stdJobRev) + "^" + (mgr.isEmpty(desc)? "":desc) + "^" + (mgr.isEmpty(status)?"":status) + "^"; 

            mgr.responseWrite(txt);
        }
        else if ("STD_JOB_REVISION".equals(val))
        {   String desc = "";
            trans.clear();

            cmd = trans.addCustomFunction("GETDESC", "Standard_Job_API.Get_Definition", "DESCRIPTION");
            cmd.addParameter("STD_JOB_ID", mgr.readValue("STD_JOB_ID"));
            cmd.addParameter("STD_JOB_CONTRACT", mgr.readValue("STD_JOB_CONTRACT"));
            cmd.addParameter("STD_JOB_REVISION", mgr.readValue("STD_JOB_REVISION"));

            cmd = trans.addCustomFunction("WORKDESC","Standard_Job_API.Get_Work_description","DESCRIPTION");
            cmd.addParameter("STD_JOB_ID", mgr.readValue("STD_JOB_ID"));
            cmd.addParameter("STD_JOB_CONTRACT", mgr.readValue("STD_JOB_CONTRACT"));
            cmd.addParameter("STD_JOB_REVISION", mgr.readValue("STD_JOB_REVISION"));

            cmd = trans.addCustomFunction("GETSTATUS","Standard_Job_API.Get_Status","STD_JOB_STATUS");
            cmd.addParameter("STD_JOB_ID", mgr.readValue("STD_JOB_ID"));
            cmd.addParameter("STD_JOB_CONTRACT", mgr.readValue("STD_JOB_CONTRACT"));
            cmd.addParameter("STD_JOB_REVISION", mgr.readValue("STD_JOB_REVISION"));

            trans = mgr.validate(trans);

            String definition =   trans.getValue("GETDESC/DATA/DESCRIPTION");  
            String workDesc =   trans.getValue("WORKDESC/DATA/DESCRIPTION");  
            String status = trans.getValue("GETSTATUS/DATA/STD_JOB_STATUS");
            if (!mgr.isEmpty(workDesc)) {
               desc = workDesc;
            }
            else
               desc = definition;

            txt = (mgr.isEmpty(desc)?"":desc) + "^"+(mgr.isEmpty(status)?"":status) + "^";

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

            String sAgreementId = "";

            trans.clear();

            cmd = trans.addCustomFunction("GETJOBEXIST", "Work_Order_Job_API.Check_Exist", "N_JOB_EXIST");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("JOB_ID"); 

            cmd = trans.addCustomFunction("GETAGR", "Active_Round_API.Get_Agreement_Id", "S_AGREEMENT_ID");
            cmd.addParameter("ITEM6_WO_NO");

            trans = mgr.validate(trans);

            nJobExist = new Double(trans.getNumberValue("GETJOBEXIST/DATA/N_JOB_EXIST")).intValue();
            sAgreementId = trans.getValue("GETAGR/DATA/S_AGREEMENT_ID");

            if (nJobExist == 1)
            {
                trans.clear();

                cmd = trans.addCustomFunction("GETSTDJOBEXIST", "Work_Order_Job_API.Std_Job_Exist", "S_STD_JOB_EXIST");
                cmd.addParameter("ITEM6_WO_NO");
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
                cmd.addParameter("ITEM6_WO_NO");
                cmd.addParameter("JOB_ID"); 

                cmd = trans.addCustomFunction("GETQTY", "Work_Order_Job_API.Get_Qty", "N_QTY");
                cmd.addParameter("ITEM6_WO_NO");
                cmd.addParameter("JOB_ID"); 

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
                  nQty + "^" +
                  (mgr.isEmpty(sAgreementId)?"":sAgreementId) + "^";

            mgr.responseWrite(txt);
        }
        //(-/+) Bug 66406, Start
        //if ("SIGN_ID".equals(val))
        else if ("SIGN_ID".equals(val))
        //(-/+) Bug 66406, End
        {
            String reqstr = null;
            int startpos = 0;
            int endpos = 0;
            int i = 0;
            String ar[] = new String[2];
            String emp_id = "";
            String sign = "";

            String new_sign = mgr.readValue("SIGN_ID","");

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
                cmd = trans.addCustomFunction("EMP","Employee_API.Get_Max_Maint_Emp","EMPLOYEE_ID");                     
                cmd.addParameter("COMPANY",mgr.readValue("ITEM6_COMPANY"));
                cmd.addParameter("SIGNATURE",new_sign);
                trans = mgr.perform(trans);

                sign = new_sign;
                emp_id = trans.getValue("EMP/DATA/EMPLOYEE_ID");

            }

            txt = (mgr.isEmpty(emp_id)?"":emp_id) + "^"+(mgr.isEmpty(sign)?"":sign) + "^";
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
	    String sAgreementId  = "";
	    String sAgreementDesc= "";
        
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

	    if (checksec("SC_CONTRACT_AGREEMENT_API.Get_First_Agreement",1,isSecure))
	    {
               cmd = trans.addCustomCommand("SCCONTRACTAGREEMENT","SC_CONTRACT_AGREEMENT_API.Get_First_Agreement");
               cmd.addParameter("AGREEMENT_ID");
               cmd.addParameter("AGREEMENT_DESC");
               cmd.addParameter("CONTRACT_ID",sContractId);
	    }
	    
            trans = mgr.validate(trans);

            sContractName  = trans.getValue("GETCONTRACTNAME/DATA/CONTRACT_NAME");
	    sLineDesc      = trans.getValue("GETLINEDESC/DATA/LINE_DESC");
	    sContractType  = trans.getValue("GETCONTRACTYPE/DATA/CONTRACT_TYPE");
	    sInvoiceType   = trans.getValue("GETINVOICETYPE/DATA/INVOICE_TYPE");
	    sAgreementId   = trans.getBuffer("SCCONTRACTAGREEMENT/DATA").getValue("AGREEMENT_ID");
	    sAgreementDesc = trans.getBuffer("SCCONTRACTAGREEMENT/DATA").getValue("AGREEMENT_DESC");

            txt =  (mgr.isEmpty(sContractId) ? "" : (sContractId)) + "^" +(mgr.isEmpty(sContractName)?"":sContractName) + "^" +
		   (mgr.isEmpty(sLineNo) ? "" : (sLineNo)) + "^" +(mgr.isEmpty(sLineDesc)?"":sLineDesc) + "^" +
		   (mgr.isEmpty(sContractType) ? "" : (sContractType)) + "^" +(mgr.isEmpty(sInvoiceType)?"":sInvoiceType) + "^" +
		   (mgr.isEmpty(sAgreementId) ? "" : (sAgreementId)) + "^" +(mgr.isEmpty(sAgreementDesc)?"":sAgreementDesc) + "^" ;
            
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
	    String sAgreementId  = "";
	    String sAgreementDesc= "";
        
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

            trans = mgr.validate(trans);
        
            sContractName  = trans.getValue("GETCONTRACTNAME/DATA/CONTRACT_NAME");
	    sLineDesc      = trans.getValue("GETLINEDESC/DATA/LINE_DESC");
	    sContractType  = trans.getValue("GETCONTRACTYPE/DATA/CONTRACT_TYPE");
	    sInvoiceType   = trans.getValue("GETINVOICETYPE/DATA/INVOICE_TYPE");
	    sAgreementId   = trans.getBuffer("SCCONTRACTAGREEMENT/DATA").getValue("AGREEMENT_ID");
	    sAgreementDesc = trans.getBuffer("SCCONTRACTAGREEMENT/DATA").getValue("AGREEMENT_DESC");

            txt =  (mgr.isEmpty(sContractId) ? "" : (sContractId)) + "^" +(mgr.isEmpty(sContractName)?"":sContractName) + "^" +
		   (mgr.isEmpty(sLineNo) ? "" : (sLineNo)) + "^" +(mgr.isEmpty(sLineDesc)?"":sLineDesc) + "^" +
		   (mgr.isEmpty(sContractType) ? "" : (sContractType)) + "^" +(mgr.isEmpty(sInvoiceType)?"":sInvoiceType) + "^" +
		   (mgr.isEmpty(sAgreementId) ? "" : (sAgreementId)) + "^" +(mgr.isEmpty(sAgreementDesc)?"":sAgreementDesc) + "^" ;

            mgr.responseWrite(txt);
        }

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
        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//----------------------  CMDBAR FUNCTIONS  ---------------------------
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
            if (headset.countRows()>0)
            {
                if (itemset2.countRows()>0)
                    itemset2.refreshAllRows();
                if (itemset.countRows()>0)
                    itemset.refreshAllRows();
            }
        }   
    }

    private String createTransferUrl(String url, ASPBuffer object)
    {
        ASPManager mgr = getASPManager();
        try
        {
            String pkg = mgr.pack(object,1900-url.length());
            char sep = url.indexOf('?')>0 ? '&' : '?';
            urlString = url + sep + "__TRANSFER=" + pkg ;
            return urlString;
        }
        catch (Throwable any)
        {
            return null;
        }
    }

    //-----------------------------------------------------------------------------
    //-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
    //-----------------------------------------------------------------------------

    public void newRow()
    {
        ASPManager mgr = getASPManager();

        cmd = trans.addEmptyCommand("HEAD","ACTIVE_ROUND_API.New__",headblk);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);
        data = trans.getBuffer("HEAD/DATA");
        headset.addRow(data);   
    }

    public void newRowITEM0()
    {
        ASPManager mgr = getASPManager();

        cmd = trans.addEmptyCommand("ITEM0","WORK_ORDER_PART_API.New__",itemblk0);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);
        data = trans.getBuffer("ITEM0/DATA");
        data.setFieldItem("ITEM0_WO_NO",mgr.readValue("WO_NO",""));
        itemset0.addRow(data);
    }

    public void newRowITEM1()
    {
        ASPManager mgr = getASPManager();
        String sCatNo = "";
        String sCatDesc = "";
        double cost = 0;

        ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer();
        secBuff.addSecurityQuery("SALES_PART");
        secBuff = mgr.perform(secBuff);

        cmd = trans.addCustomFunction("WOCOST","Work_Order_Cost_Type_API.Get_Client_Value(0)","WORK_ORDER_COST_TYPE");

        cmd = trans.addCustomFunction("WOACCNT","Work_Order_Account_Type_API.Get_Client_Value(0)","WORK_ORDER_ACCOUNT_TYPE");

        cmd = trans.addCustomFunction("WOORG","Active_Work_Order_API.Get_Org_Code","ORCODE");
        cmd.addParameter("ITEM1_WO_NO",headset.getValue("WO_NO"));

        cmd = trans.addCustomFunction("WOSITE","Active_Work_Order_API.Get_Contract","CONTRACT1");
        cmd.addParameter("ITEM1_CONTRACT",headset.getValue("WO_NO"));

        if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
        {
            cmd = trans.addCustomFunction("ORGSALESPART","Organization_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
            cmd.addReference("CONTRACT1","WOSITE/DATA");
            cmd.addReference("ORCODE","WOORG/DATA");

            cmd = trans.addCustomFunction("ORGSALESDESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
            cmd.addReference("CONTRACT1","WOSITE/DATA");
            cmd.addReference("CATALOG_NO","ORGSALESPART/DATA");

            cmd = trans.addCustomFunction("CST", "Work_Order_Planning_Util_Api.Get_Cost", "SALESPARTCOST" );
            cmd.addReference("ORCODE","WOORG/DATA");
            cmd.addParameter("DUMMY","");
            cmd.addReference("CONTRACT1","WOSITE/DATA");
            cmd.addReference("CATALOG_NO","ORGSALESPART/DATA");
            cmd.addReference("CONTRACT1","WOSITE/DATA");
            cmd.addParameter("DUMMY","");
            cmd.addParameter("DUMMY","TRUE");
        }
        else
        {
            cmd = trans.addCustomFunction("CST", "Work_Order_Planning_Util_Api.Get_Cost", "SALESPARTCOST" );
            cmd.addReference("ORCODE","WOORG/DATA");
            cmd.addParameter("DUMMY","");
            cmd.addReference("CONTRACT1","WOSITE/DATA");
            cmd.addParameter("DUMMY","");
            cmd.addReference("CONTRACT1","WOSITE/DATA");
            cmd.addParameter("DUMMY","");
            cmd.addParameter("DUMMY","TRUE");
        }

        cmd = trans.addEmptyCommand("ITEM1","WORK_ORDER_CODING_API.New__",itemblk1);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);

        strWOCostType= trans.getValue("WOCOST/DATA/WORK_ORDER_COST_TYPE");
        strWOACcntType= trans.getValue("WOACCNT/DATA/WORK_ORDER_ACCOUNT_TYPE");
        strWOOrg= trans.getValue("WOORG/DATA/ORCODE");
        strWOSite= trans.getValue("WOSITE/DATA/CONTRACT1");
        strcmnt= mgr.readValue("NAME","")+ ", " + strWOOrg;
        cost = trans.getNumberValue("CST/DATA/SALESPARTCOST");

        if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
        {
            sCatNo = trans.getValue("ORGSALESPART/DATA/CATALOG_NO");
            sCatDesc = trans.getValue("ORGSALESDESC/DATA/SALESPARTCATALOGDESC");
        }


        data = trans.getBuffer("ITEM1/DATA");
        data.setFieldItem("ITEM1_WO_NO",headset.getValue("WO_NO"));
        data.setFieldItem("WORK_ORDER_COST_TYPE",strWOCostType);
        data.setFieldItem("WORK_ORDER_ACCOUNT_TYPE",strWOACcntType);
        data.setFieldItem("ORG_CODE",strWOOrg);
        data.setFieldItem("ITEM1_CONTRACT",strWOSite);
        data.setFieldItem("ITEM1_COMPANY",headset.getValue("COMPANY"));
        data.setFieldItem("CATALOG_NO",sCatNo);
        data.setFieldItem("SALESPARTCATALOGDESC",sCatDesc);
        data.setNumberValue("SALESPARTCOST",cost);
        data.setNumberValue("AMOUNT",cost*(data.getNumberValue("QTY")));
        data.setFieldItem("CMNT",strcmnt);

        itemset1.addRow(data);
    }

    public void deleteITEM1()
    {
        ASPManager mgr = getASPManager();
        //Bug 81023, start
        trans.clear();
        //Bug id 81023, end

        int currow = headset.getCurrentRowNo();

        itemset1.store();

        if (itemlay1.isMultirowLayout())
        {
            itemset1.setSelectedRowsRemoved();
            itemset1.unselectRows();
        }
        else
            itemset1.setRemoved();

        //Bug id 81023, start
        cmd = trans.addCustomCommand("HRAUTHCONFCHECK", "Work_Order_Coding_API.Check_T_Hr_Auth_Confirmation");
        cmd.addInParameter("COMPANY", itemset1.getRow().getValue("COMPANY")); 
        cmd.addInParameter("EMP_NO", itemset1.getRow().getValue("EMP_NO")); 
        cmd.addInParameter("CRE_DATE", itemset1.getRow().getFieldValue("CRE_DATE")); 
        cmd.addInParameter("CATALOG_NO", "T"); 
        cmd.addInParameter("CONTRACT", itemset1.getRow().getValue("CONTRACT"));
        //Bug id 81023, end
        mgr.submit(trans);

        headset.goTo(currow);

        okFindITEM4();

        headset.goTo(currow);
    }

    public void newRowITEM2()
    {
        ASPManager mgr = getASPManager();

        cmd = trans.addEmptyCommand("ITEM2","MAINT_MATERIAL_REQUISITION_API.New__",itemblk2);
        cmd.setParameter("ITEM2_WO_NO",headset.getRow().getValue("WO_NO"));
        cmd.setOption("ACTION","PREPARE");

        cmd = trans.addCustomFunction("PLNDATE","Active_Work_Order_API.Get_Plan_S_Date","DUE_DATE");
        cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

        cmd = trans.addCustomFunction("PREACCOID","Active_Work_Order_API.Get_Pre_Accounting_Id","NPREACCOUNTINGID");
        cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

        cmd = trans.addCustomFunction("CONT","WORK_ORDER_API.Get_Contract","ITEM2_CONTRACT");
        cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

        cmd = trans.addCustomFunction("COMP","Site_API.Get_Company","ITEM2_COMPANY");
        cmd.addReference("ITEM2_CONTRACT","CONT/DATA");

        cmd = trans.addCustomFunction("MCHCOD","WORK_ORDER_API.Get_Mch_Code","MCHCODE");
        cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

        cmd = trans.addCustomFunction("MCHDESC","Maintenance_Object_API.Get_Mch_Name","ITEM2DESCRIPTION");
        cmd.addReference("ITEM2_CONTRACT","CONT/DATA");
        cmd.addReference("MCHCODE","MCHCOD/DATA");

        cmd = trans.addQuery("SUYSTDATE","SELECT SYSDATE SYST_DATE FROM DUAL");

        trans = mgr.perform(trans);

        data = trans.getBuffer("ITEM2/DATA");

        dateRequired = trans.getBuffer("PLNDATE/DATA").getFieldValue("DUE_DATE");
        nPreAccoId = trans.getBuffer("PREACCOID/DATA").getFieldValue("NPREACCOUNTINGID");
        item2Cont = trans.getBuffer("CONT/DATA").getFieldValue("ITEM2_CONTRACT");
        item2Company = trans.getBuffer("COMP/DATA").getFieldValue("ITEM2_COMPANY");
        mchCode = trans.getValue("MCHCOD/DATA/MCHCODE");
        mchDesc = trans.getValue("MCHDESC/DATA/ITEM2DESCRIPTION");
        sysDate = trans.getBuffer("SUYSTDATE/DATA").getFieldValue("SYST_DATE");

        if (!mgr.isEmpty(dateRequired))
            data.setFieldItem("DUE_DATE",dateRequired);
        else
            data.setFieldItem("DUE_DATE",sysDate);

        data.setFieldItem("NPREACCOUNTINGID",nPreAccoId);
        data.setFieldItem("ITEM2_CONTRACT",item2Cont);  
        data.setFieldItem("ITEM2_COMPANY",item2Company); 
        data.setFieldItem("MCHCODE",mchCode);
        data.setFieldItem("ITEM2DESCRIPTION",mchDesc);
        data.setFieldItem("ITEM2_ACTIVITY_SEQ",headset.getRow().getValue("ACTIVITY_SEQ"));

        itemset2.addRow(data);
    }

    public void newRowITEM3()
    {
        ASPManager mgr = getASPManager();

        cmd = trans.addEmptyCommand("ITEM3","WORK_ORDER_PERMIT_API.New__",itemblk3);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);
        data = trans.getBuffer("ITEM3/DATA");
        data.setFieldItem("ITEM3_WO_NO",headset.getRow().getValue("WO_NO"));
        itemset3.addRow(data);
    }

    public void newRowITEM4()
    {
        ASPManager mgr = getASPManager();

        cmd = trans.addCustomFunction("SPC21","Work_Order_Cost_Type_Api.Get_Client_Value(2)","CLIENTVAL3");
        cmd = trans.addCustomFunction("SP2C2","Work_Order_Account_Type_API.Get_Client_Value(0)","CLIENTVAL4");

        cmd = trans.addCustomFunction("WOSITE4","Active_Work_Order_API.Get_Contract","ITEM4_CONTRACT");
        cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

        cmd = trans.addCustomFunction("ITEM4COMP","Site_API.Get_Company","ITEM4_COMPANY");
        cmd.addReference("ITEM4_CONTRACT", "WOSITE4/DATA");

        cmd = trans.addCustomFunction("FIXPRICE","Active_Work_Order_API.Get_Fixed_Price","ACTIVEWORKORDERFIXEDPRICE");
        cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

        cmd = trans.addCustomFunction("FIX","Fixed_Price_API.Decode('1')","NOTFIXEDPRICE");

        cmd = trans.addCustomFunction("COSTCENT","Active_Work_Order_API.Get_Cost_Center","COST_CENTER");
        cmd.addParameter("ITEM4_WO_NO",headset.getRow().getValue("WO_NO"));

        cmd = trans.addCustomFunction("OBJ","Active_Work_Order_API.Get_Object_No ","OBJECT_NO");
        cmd.addParameter("ITEM4_WO_NO",headset.getRow().getValue("WO_NO"));

        cmd = trans.addCustomFunction("PROJ","Active_Work_Order_API.Get_Project_No ","PROJECT_NO");
        cmd.addParameter("ITEM4_WO_NO",headset.getRow().getValue("WO_NO"));

        cmd = trans.addEmptyCommand("ITEM4","WORK_ORDER_CODING_API.New__",itemblk4);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);

        strWorkOrdCost = trans.getValue("SPC21/DATA/CLIENTVAL3");
        strWorkAccount = trans.getValue("SP2C2/DATA/CLIENTVAL4");
        scontract= trans.getBuffer("WOSITE4/DATA").getFieldValue("ITEM4_CONTRACT");
        String sCompany=trans.getBuffer("ITEM4COMP/DATA").getFieldValue("ITEM4_COMPANY");
        sActiveFixedPrice= trans.getValue("FIXPRICE/DATA/ACTIVEWORKORDERFIXEDPRICE");
        sNotFixedPrice= trans.getValue("FIX/DATA/NOTFIXEDPRICE");
        strCost = trans.getValue("COSTCENT/DATA/COST_CENTER");
        strObj = trans.getValue("OBJ/DATA/OBJECT_NO");
        strProj = trans.getValue("PROJ/DATA/PROJECT_NO");

        data = trans.getBuffer("ITEM4/DATA");
        data.setFieldItem("ITEM4_WO_NO",headset.getValue("WO_NO"));
        data.setFieldItem("ITEM4_WORK_ORDER_COST_TYPE",strWorkOrdCost);
        data.setFieldItem("ITEM4_WORK_ORDER_ACCOUNT_TYPE",strWorkAccount );
        data.setFieldItem("ITEM4_CONTRACT",scontract);
        data.setFieldItem("ITEM4_COMPANY",sCompany);
        data.setFieldItem("ACTIVEWORKORDERFIXEDPRICE",sActiveFixedPrice);
        data.setFieldItem("NOTFIXEDPRICE",sNotFixedPrice);
        data.setFieldItem("COST_CENTER",strCost);
        data.setFieldItem("OBJECT_NO",strObj);
        data.setFieldItem("PROJECT_NO",strProj);
        bolNewRow = true;        
        itemset4.addRow(data);
    }

    public void saveReturnITEM4()
    {
        ASPManager mgr = getASPManager();

        int currHead = headset.getCurrentRowNo();

        itemset4.changeRow();
        itemset4.setValue("LINE_DESCRIPTION",itemset4.getValue("LINEDESCRIPTION"));

        mgr.submit(trans);
        headset.goTo(currHead);
    }

    public void duplicateRow()
    {
        ASPManager mgr = getASPManager();

        itemset4.goTo(itemset4.getRowSelected());

        ASPBuffer item4RowVals = itemset4.getRow();
        item4RowVals.setValue("ROW_NO","");
        item4RowVals.setValue("STATE","");
	item4RowVals.setValue("NOTE_ID","");
        itemset4.addRow(item4RowVals);

        itemlay4.setLayoutMode(itemlay4.NEW_LAYOUT);
    }

    public void saveReturnItem6()
    {
        ASPManager mgr = getASPManager();

        int currHead = headset.getCurrentRowNo();
        int currrowItem6 = itemset6.getCurrentRowNo();
        itemset6.changeRow();
        mgr.submit(trans);

        headset.goTo(currHead);
        headset.refreshRow();
        okFindITEM2();
        itemset6.refreshRow();
        itemset6.goTo(currrowItem6);
    }

    public void saveReturnNewITEM4()
    {
        saveReturnITEM4();
        trans.clear();
        newRowITEM4();
    }

    public void saveReturnITEM1()
    {
        ASPManager mgr = getASPManager();
        int currHead = headset.getCurrentRowNo();
        int currrowItem1 = itemset1.getCurrentRowNo();
        trans.clear();

        temp = mgr.newASPBuffer();
        itemset1.changeRow();        
        
        //Bug id 81023, start
        cmd = trans.addCustomCommand("HRAUTHCONFCHECK", "Work_Order_Coding_API.Check_T_Hr_Auth_Confirmation");
        cmd.addInParameter("COMPANY", itemset1.getRow().getValue("COMPANY")); 
        cmd.addInParameter("EMP_NO", itemset1.getRow().getValue("EMP_NO")); 
        cmd.addInParameter("CRE_DATE", itemset1.getRow().getFieldValue("CRE_DATE")); 
        cmd.addInParameter("CATALOG_NO", "T"); 
        cmd.addInParameter("CONTRACT", itemset1.getRow().getValue("CONTRACT"));       
        //Bug id 81023, end 
        

	// Commented by amdilk
        /*temp.addItem("CRE_DATE",itemset1.getValue("CRE_DATE"));
        temp.addItem("EMP_NO",itemset1.getValue("EMP_NO"));
        temp.addItem("EMP_SIGNATURE",itemset1.getValue("EMP_SIGNATURE"));
        temp.addItem("CONTRACT",itemset1.getValue("CONTRACT"));
        temp.addItem("ORG_CODE",itemset1.getValue("ORG_CODE"));
        temp.addItem("COMPANY",itemset1.getValue("COMPANY"));
        temp.addItem("ROLE_CODE",itemset1.getValue("ROLE_CODE"));
        temp.addItem("CATALOG_NO",itemset1.getValue("CATALOG_NO"));
        temp.addItem("QTY",itemset1.getValue("QTY"));
        temp.addItem("AMOUNT",itemset1.getValue("AMOUNT"));
        temp.addItem("LIST_PRICE",itemset1.getValue("LIST_PRICE"));
        temp.addItem("CMNT",itemset1.getValue("CMNT"));
        //temp.addItem("CUSTOMER_NO",itemset1.getValue("CUSTOMER_NO"));
        temp.addItem("WO_NO",itemset1.getValue("WO_NO"));
        temp.addItem("ROW_NO",itemset1.getValue("ROW_NO"));
        temp.addItem("WORK_ORDER_BOOK_STATUS",itemset1.getValue("WORK_ORDER_BOOK_STATUS"));
        temp.addItem("WORK_ORDER_COST_TYPE",itemset1.getValue("WORK_ORDER_COST_TYPE"));
        temp.addItem("WORK_ORDER_ACCOUNT_TYPE",itemset1.getValue("WORK_ORDER_ACCOUNT_TYPE"));
        temp.addItem("AGREEMENT_PRICE_FLAG",itemset1.getValue("AGREEMENT_PRICE_FLAG"));
        temp.addItem("QTY_TO_INVOICE",itemset1.getValue("QTY_TO_INVOICE"));
                                                            
        itemset1.setRow(temp);*/

        mgr.submit(trans);
        headset.goTo(currHead);
        okFindITEM4();
        itemset1.refreshAllRows();
        itemset1.goTo(currrowItem1); 
    }
    //Bug id 81023, start
    public void saveNewITEM1()
    {       
        ASPManager mgr = getASPManager();
        int currHead = headset.getCurrentRowNo();
        int currrowItem1 = itemset1.getCurrentRowNo();
        trans.clear();

        temp = mgr.newASPBuffer();
        itemset1.changeRow();                
        
        cmd = trans.addCustomCommand("HRAUTHCONFCHECK", "Work_Order_Coding_API.Check_T_Hr_Auth_Confirmation");
        cmd.addInParameter("COMPANY", itemset1.getRow().getValue("COMPANY")); 
        cmd.addInParameter("EMP_NO", itemset1.getRow().getValue("EMP_NO")); 
        cmd.addInParameter("CRE_DATE", itemset1.getRow().getFieldValue("CRE_DATE")); 
        cmd.addInParameter("CATALOG_NO", "T"); 
        cmd.addInParameter("CONTRACT", itemset1.getRow().getValue("CONTRACT"));       
        
        mgr.submit(trans);
        headset.goTo(currHead);
        trans.clear();        
        newRowITEM1();
    }
    //Bug id 81023, end 

    public void saveReturnITEM2()
    {
        ASPManager mgr = getASPManager();
        int currHead = headset.getCurrentRowNo();
        int currrowItem2 = itemset2.getCurrentRowNo();
        itemset2.changeRow();
        mgr.submit(trans);
        itemlay2.setLayoutMode(itemlay2.SINGLE_LAYOUT);
        itemset2.refreshAllRows();
        itemset2.goTo(currrowItem2);
        headset.goTo(currHead);
    }

    public void deleteITEM2()
    {
        ASPManager mgr = getASPManager();

        int currHead = headset.getCurrentRowNo();

        if (itemlay2.isMultirowLayout())
            itemset2.goTo(itemset2.getRowSelected());

        itemset2.unselectRows();
        itemset2.selectRow();

        itemset2.setRemoved();

        mgr.submit(trans);

        if (itemset2.countRows()>0)
            itemset2.refreshAllRows();
        headset.goTo(currHead);
    }

    public void deleteITEM4()
    {
        ASPManager mgr = getASPManager();

        int currow = headset.getCurrentRowNo();

        if (itemlay4.isMultirowLayout())
            itemset4.goTo(itemset4.getRowSelected());
        else
            itemset4.selectRow();

        trans.clear();
        itemset4.setRemoved();
        mgr.submit(trans);

        headset.goTo(currow);

        okFindITEM1();

        headset.goTo(currow);
    }    

    public void deleteITEM6()
    {
        ASPManager mgr = getASPManager();
        ctx = mgr.getASPContext();

        String checkDeleteStatus = mgr.readValue("CHECK_DELETE_ITEM6");
        
        if ("TRUE".equals(checkDeleteStatus)) {
            if (itemlay6.isMultirowLayout())
                itemset6.goTo(itemset6.getRowSelected());
            else
                itemset6.selectRow();
        
            ctx.writeValue("ITEM6ROWSELECTED",itemset6.getRowSelected()+"");

            int nJobExist = 0;
            String sStdJobExist = "";
            int nRoleExist = 0,nMatExist = 0,nToolExist = 0,nPlanningExist = 0,nDocExist = 0;
            String sStdJobId = "",sStdJobContract = "",sStdJobRevision = "",sIsSeparate = "";
            double nQty = 0;
            String sAgreementId = "";
    
            trans.clear();
    
            cmd = trans.addCustomFunction("GETJOBEXIST", "Work_Order_Job_API.Check_Exist", "N_JOB_EXIST");
            cmd.addParameter("ITEM6_WO_NO",itemset6.getValue("WO_NO"));
            cmd.addParameter("JOB_ID",itemset6.getValue("JOB_ID")); 
    
            cmd = trans.addCustomFunction("GETAGR", "Active_Round_API.Get_Agreement_Id", "S_AGREEMENT_ID");
            cmd.addParameter("ITEM6_WO_NO",itemset6.getValue("WO_NO"));
    
            trans = mgr.perform(trans);
    
            nJobExist = new Double(trans.getNumberValue("GETJOBEXIST/DATA/N_JOB_EXIST")).intValue();
            sAgreementId = trans.getValue("GETAGR/DATA/S_AGREEMENT_ID");
    
            bDeleteItem6 = true;
   
            if (nJobExist == 1)
            {
                trans.clear();                
    
                cmd = trans.addCustomFunction("GETSTDJOBEXIST", "Work_Order_Job_API.Std_Job_Exist", "S_STD_JOB_EXIST");
                cmd.addParameter("ITEM6_WO_NO",itemset6.getValue("WO_NO"));
                cmd.addParameter("JOB_ID",itemset6.getValue("JOB_ID"));         
    
                cmd = trans.addCustomCommand("GETDEFVALS", "Work_Order_Job_API.Check_Job_Connection");
                cmd.addParameter("N_ROLE_EXIST","0");
                cmd.addParameter("N_MAT_EXIST","0");
                cmd.addParameter("N_TOOL_EXIST","0");
                cmd.addParameter("N_PLANNING_EXIST","0");
                cmd.addParameter("N_DOC_EXIST","0");
                cmd.addParameter("S_STD_JOB_ID");
                cmd.addParameter("S_STD_JOB_CONTRACT");
                cmd.addParameter("S_STD_JOB_REVISION");
                cmd.addParameter("ITEM6_WO_NO",itemset6.getValue("WO_NO"));
                cmd.addParameter("JOB_ID",itemset6.getValue("JOB_ID")); 
    
                cmd = trans.addCustomFunction("GETQTY", "Work_Order_Job_API.Get_Qty", "N_QTY");
                cmd.addParameter("ITEM6_WO_NO",itemset6.getValue("WO_NO"));
                cmd.addParameter("JOB_ID",itemset6.getValue("JOB_ID"));
    
                cmd = trans.addCustomFunction("ISSEPA","Active_Separate_API.Is_Separate","IS_SEPARATE");
                cmd.addParameter("ITEM6_WO_NO",itemset6.getValue("WO_NO"));
    
                trans = mgr.perform(trans);
    
                sStdJobExist = trans.getValue("GETSTDJOBEXIST/DATA/S_STD_JOB_EXIST");
    
                nRoleExist = new Double(trans.getNumberValue("GETDEFVALS/DATA/N_ROLE_EXIST")).intValue();
                nMatExist = new Double(trans.getNumberValue("GETDEFVALS/DATA/N_MAT_EXIST")).intValue();
                nToolExist = new Double(trans.getNumberValue("GETDEFVALS/DATA/N_TOOL_EXIST")).intValue();
                nPlanningExist = new Double(trans.getNumberValue("GETDEFVALS/DATA/N_PLANNING_EXIST")).intValue();
                nDocExist = new Double(trans.getNumberValue("GETDEFVALS/DATA/N_DOC_EXIST")).intValue();
                sStdJobId = trans.getValue("GETDEFVALS/DATA/S_STD_JOB_ID");
                sStdJobContract = trans.getValue("GETDEFVALS/DATA/S_STD_JOB_CONTRACT");
                sStdJobRevision = trans.getValue("GETDEFVALS/DATA/S_STD_JOB_REVISION");
                sIsSeparate = trans.getValue("ISSEPA/DATA/IS_SEPARATE");
                nQty = trans.getNumberValue("GETQTY/DATA/N_QTY");
            }
    
            if (nJobExist == 1) {
                if ("TRUE".equals(sStdJobExist)) {
                    if (nRoleExist == 1 || nMatExist == 1 || nToolExist == 1 || nPlanningExist == 1 || nDocExist == 1) { 
                        if ("TRUE".equals(sIsSeparate)) {
                            bConfirmMsg1 = true;
                            sConfirmMsg1 = mgr.translate("WOJOBREMROUNDSTDJOB: Do you want to remove connected Operations, Materials, Planning, Tools/Facilities and Documents?");
                        }
                        else if ("FALSE".equals(sIsSeparate)) {
                            bConfirmMsg1 = true;
                            sConfirmMsg1 = mgr.translate("WOJOBREMROUNDSTDJOB2: Do you want to remove connected Materials and Documents?");
                        }
                    }
                }
            }
        }
        else if ("DELETE".equals(checkDeleteStatus)) {
            int currow = headset.getCurrentRowNo();

            if (itemlay6.isMultirowLayout())
                itemset6.goTo(Integer.parseInt(ctx.readValue("ITEM6ROWSELECTED")));
            else
                itemset6.selectRow();
            
            trans.clear();
            cmd = trans.addCustomCommand("GETDEFVALS", "Work_Order_Job_API.Check_Job_Connection");
            cmd.addParameter("N_ROLE_EXIST","0");
            cmd.addParameter("N_MAT_EXIST","0");
            cmd.addParameter("N_TOOL_EXIST","0");
            cmd.addParameter("N_PLANNING_EXIST","0");
            cmd.addParameter("N_DOC_EXIST","0");
            cmd.addParameter("S_STD_JOB_ID");
            cmd.addParameter("S_STD_JOB_CONTRACT");
            cmd.addParameter("S_STD_JOB_REVISION");
            cmd.addParameter("ITEM6_WO_NO",itemset6.getValue("WO_NO"));
            cmd.addParameter("JOB_ID",itemset6.getValue("JOB_ID")); 
    
            cmd = trans.addCustomFunction("GETQTY", "Work_Order_Job_API.Get_Qty", "N_QTY");
            cmd.addParameter("ITEM6_WO_NO",itemset6.getValue("WO_NO"));
            cmd.addParameter("JOB_ID",itemset6.getValue("JOB_ID"));
    
            trans = mgr.perform(trans);
    
            String sStdJobId = trans.getValue("GETDEFVALS/DATA/S_STD_JOB_ID");
            String sStdJobContract = trans.getValue("GETDEFVALS/DATA/S_STD_JOB_CONTRACT");
            String sStdJobRevision = trans.getValue("GETDEFVALS/DATA/S_STD_JOB_REVISION");
            double nQty = trans.getNumberValue("GETQTY/DATA/N_QTY");

            String keepCon = mgr.readValue("DUMMY_KEEP_CONNECTIONS");
            if ("TRUE".equals(keepCon)) {            
                trans.clear();
                cmd = trans.addCustomCommand("REMCONN","Work_Order_Job_API.Disconnect_Std_Job");
                cmd.addParameter("ITEM6_WO_NO",itemset6.getValue("WO_NO"));
                cmd.addParameter("JOB_ID",itemset6.getValue("JOB_ID"));
                cmd.addParameter("S_STD_JOB_ID",sStdJobId);
                cmd.addParameter("S_STD_JOB_CONTRACT",sStdJobContract);
                cmd.addParameter("S_STD_JOB_REVISION",sStdJobRevision);
                cmd.addParameter("N_QTY",nQty+"");
                trans = mgr.perform(trans);
            }
    
            trans.clear();
            itemset6.setRemoved();
            mgr.submit(trans);
    
            headset.goTo(currow);
            headset.refreshRow();
            okFindITEM2();
        }        
    }

    public void newRowITEM5()
    {
        ASPManager mgr = getASPManager();

        cmd = trans.addEmptyCommand("ITEM5","MAINT_MATERIAL_REQ_LINE_API.New__",itemblk);
        cmd.setParameter("SPARE_CONTRACT",itemset2.getRow().getFieldValue("ITEM2_CONTRACT"));
        cmd.setParameter("ITEM_WO_NO",itemset2.getRow().getFieldValue("ITEM2_WO_NO"));
        cmd.setParameter("ITEM0_MAINT_MATERIAL_ORDER_NO",itemset2.getRow().getFieldValue("MAINT_MATERIAL_ORDER_NO"));
        cmd.setOption("ACTION","PREPARE");

        trans = mgr.perform(trans);

        data = trans.getBuffer("ITEM5/DATA");
        data.setFieldItem("SPARE_CONTRACT",itemset2.getRow().getFieldValue("ITEM2_CONTRACT"));
        data.setFieldItem("DATE_REQUIRED",itemset2.getRow().getFieldValue("DUE_DATE"));
	data.setFieldItem("CATALOG_CONTRACT",headset.getRow().getValue("CONTRACT"));

        itemset.addRow(data);
    }

    public void newRowITEM6()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addEmptyCommand("ITEM6", "WORK_ORDER_JOB_API.New__", itemblk6);
        cmd.setParameter("ITEM6_WO_NO", headset.getValue("WO_NO"));
        cmd.setOption("ACTION", "PREPARE");

        cmd = trans.addCustomFunction("GETSDATE", "Active_Round_API.Get_Plan_S_Date", "DATE_FROM");
        cmd.addParameter("ITEM6_WO_NO", headset.getValue("WO_NO"));

        cmd = trans.addCustomFunction("GETFDATE", "Active_Round_API.Get_Plan_F_Date", "DATE_TO");
        cmd.addParameter("ITEM6_WO_NO", headset.getValue("WO_NO"));

        trans = mgr.perform(trans);

        data = trans.getBuffer("ITEM6/DATA");

        Date dPlanSDate = trans.getBuffer("GETSDATE/DATA").getFieldDateValue("DATE_FROM");
        Date dPlanFDate = trans.getBuffer("GETFDATE/DATA").getFieldDateValue("DATE_TO");

        data.setFieldDateItem("DATE_FROM", dPlanSDate);
        data.setFieldDateItem("DATE_TO", dPlanFDate);
        itemset6.addRow(data);
    }

    //-----------------------------------------------------------------------------
    //------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
    //-----------------------------------------------------------------------------

    public void okFind()
    {
        ASPManager mgr = getASPManager();
        String  curr_row_exists = "FALSE";

        q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT)");
        q.setOrderByClause("WO_NO");
        q.includeMeta("ALL");

        if (mgr.dataTransfered())
        {
            retBuffer =  mgr.getTransferedData();
            if (retBuffer.itemExists("WO_NO"))
            {
                ret_wo_no = retBuffer.getValue("WO_NO");
                q.addWhereCondition("WO_NO = ?");
                q.addParameter("WO_NO",ret_wo_no);

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

        if (!("none".equals(head_command)))
        {

            if ("release".equals(head_command))
                q.addWhereCondition("OBJEVENTS like '%Release%'");

            if ("startOrder".equals(head_command))
                q.addWhereCondition("OBJEVENTS like '%StartOrder%'");
       //bug id 80964,start
            if ("workDone".equals(head_command))
                q.addWhereCondition("OBJEVENTS like '%workDone%'");
            if ("reported".equals(head_command))
                q.addWhereCondition("OBJEVENTS like '%reported%'");
        //bug id 80964,end
            if ("finish".equals(head_command))
                q.addWhereCondition("OBJEVENTS like '%Finish%'");
        }

        mgr.querySubmit(trans,headblk);

        eval(headset.syncItemSets());

        if ("TRUE".equals(curr_row_exists))
        {
            headset.goTo(currrow);
            if ("1".equals(lout))
                headlay.setLayoutMode(headlay.MULTIROW_LAYOUT);
            else
                headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        }

        if (!mgr.isEmpty(start))
        {
            headset.first(); 
            do
            {
                HeadWo_No=headset.getValue("WO_NO");
                if (HeadWo_No.equals(wono))
                    headrowno = headset.getCurrentRowNo();

            }while (headset.next()); 

            headset.goTo(headrowno);
            okFindITEM0();
            okFindITEM1();
            okFindITEM2();
            okFindITEM3();
            okFindITEM4();
        }

        if ((headset.countRows() == 0) && (mgr.isEmpty(mgr.getQueryStringValue("WOFINISHED"))))
        {

            mgr.showAlert(mgr.translate("PCMWACTIVEROUNDNODATAFOUND: No data found."));
            headset.clear();

        }
        else if (headset.countRows() == 1)
        {
            okFindITEM0();
            okFindITEM1();
            okFindITEM2();
            okFindITEM3();
            okFindITEM4();
            okFindITEM6();
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        }

        qrystr = mgr.createSearchURL(headblk);
        queryStringVal = qrystr;
    }

    public void countFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT)");

        if (!("none".equals(head_command)))
        {

            if ("release".equals(head_command))
                q.addWhereCondition("OBJEVENTS like '%Release%'");

            if ("startOrder".equals(head_command))
                q.addWhereCondition("OBJEVENTS like '%StartOrder%'");
        //bug id 80964,start
            if ("workDone".equals(head_command))
                q.addWhereCondition("OBJEVENTS like '%workDone%'");
        
            if ("reported".equals(head_command))
                q.addWhereCondition("OBJEVENTS like '%reported%'");
         //bug id 80964,end
            if ("finish".equals(head_command))
                q.addWhereCondition("OBJEVENTS like '%Finish%'");
        }

        mgr.submit(trans);
        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();
    }


    /*public void okFindITEMConnected(String wo_no){

        //find the WO connected to the Project

        ASPManager mgr = getASPManager();
        trans.clear();
        q = trans.addQuery(headblk);
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",wo_no);
        q.includeMeta("ALL");
        mgr.querySubmit(trans,headblk);
    }*/


    public void okFindITEM0()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        headrowno = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk0);
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getValue("WO_NO"));
        q.setOrderByClause("PM_ORDER_NO");
        q.includeMeta("ALL");
        mgr.querySubmit(trans,itemblk0);
        headset.goTo(headrowno);
    }

    public void countFindITEM0()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk0);
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getValue("WO_NO"));
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
        itemset0.clear();
    }

    public void okFindITEM1()
    {
        ASPManager mgr = getASPManager();

        headrowno = headset.getCurrentRowNo();

        trans.clear();
        q = trans.addQuery(itemblk1);
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getValue("WO_NO"));
        q.addWhereCondition("WORK_ORDER_COST_TYPE = Work_Order_Cost_Type_API.Get_Client_Value(0)");
        q.addWhereCondition("WORK_ORDER_ACCOUNT_TYPE = Work_Order_Account_Type_API.Get_Client_Value(0)");
        q.includeMeta("ALL");
        mgr.querySubmit(trans,itemblk1);

        if (mgr.commandBarActivated())
        {

            if (itemset1.countRows() == 0 && "ITEM1.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWACTIVEROUNDNODATA: No data found."));
                itemset1.clear();
            }
        }

        if (itemset1.countRows()>0)
            setSalesPartCost();

        headset.goTo(headrowno);
    }

    public void countFindITEM1()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk1);
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getValue("WO_NO"));
        q.addWhereCondition("WORK_ORDER_COST_TYPE = Work_Order_Cost_Type_API.Get_Client_Value(0)");
        q.addWhereCondition("WORK_ORDER_ACCOUNT_TYPE = Work_Order_Account_Type_API.Get_Client_Value(0)");
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        itemlay1.setCountValue(toInt(itemset1.getRow().getValue("N")));
        itemset1.clear();
    }

    public void okFindITEM2()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        headrowno = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk2);
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getValue("WO_NO"));
        q.addWhereCondition("CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT)");
        q.includeMeta("ALL");

        mgr.querySubmit(trans,itemblk2);

        if (comBarAct)
        {
            if (itemset2.countRows() == 0 && "ITEM2.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWACTIVEROUNDNODATA: No data found."));
                itemset2.clear();
            }
        }

        headset.goTo(headrowno);
        if (itemset2.countRows() > 0) {
            okFindITEM5();
        }
        qrystr = mgr.createSearchURL(headblk);  
    }


    public void countFindITEM2()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk2);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getValue("WO_NO"));
        mgr.submit(trans);
        itemlay2.setCountValue(toInt(itemset2.getRow().getValue("N")));
        itemset2.clear();
    }

    public void okFindITEM3()
    {
        ASPManager mgr = getASPManager();

        headrowno = headset.getCurrentRowNo();

        trans.clear();

        q = trans.addQuery(itemblk3);
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getValue("WO_NO"));
        q.includeMeta("ALL");
        mgr.querySubmit(trans,itemblk3);

        if (mgr.commandBarActivated())
        {
            if (itemset3.countRows(   )    ==    0    &&    "ITEM3.OkFind".equals(   mgr.readValue(   "__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWACTIVEROUNDNODATA: No data found."));
                itemset3.clear();
            }
        }

        headset.goTo(headrowno);
    }

    public void countFindITEM3()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk3);
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getValue("WO_NO"));
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        itemlay3.setCountValue(toInt(itemset3.getRow().getValue("N")));
        itemset3.clear();
    }

    public void okFindITEM4()
    {
        ASPManager mgr = getASPManager();

        headrowno = headset.getCurrentRowNo();

        trans.clear();

        q = trans.addQuery(itemblk4);
        q.addWhereCondition("WO_NO = ?");
	if (returnedFmCorrectpostings) 
            q.addParameter("WO_NO",wono);
	else
            q.addParameter("WO_NO",headset.getValue("WO_NO"));
        q.addWhereCondition("WORK_ORDER_ACCOUNT_TYPE = Work_Order_Account_Type_API.Get_Client_Value(0)");
        q.includeMeta("ALL");
        mgr.querySubmit(trans,itemblk4);

        if (mgr.commandBarActivated())
        {
            if (itemset4.countRows() == 0 && "ITEM4.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWACTIVEROUNDNODATA: No data found."));
                itemset4.clear();
            }
        }

        headset.goTo(headrowno);
    }


    public void countFindITEM4()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk4);
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getValue("WO_NO"));
        q.addWhereCondition("WORK_ORDER_ACCOUNT_TYPE = Work_Order_Account_Type_API.Get_Client_Value(0)");
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        itemlay4.setCountValue(toInt(itemset4.getRow().getValue("N")));
        itemset4.clear();
    }

    public void okFindITEM5()
    {

        ASPManager mgr = getASPManager();

        //Bug 72202, Start, Added check on row count
        if (itemset2.countRows() > 0)
        {
            trans.clear();

            headsetRowNo = headset.getCurrentRowNo();

            item2rowno = itemset2.getCurrentRowNo();

	    if ( ("ITEM2.Forward".equals(mgr.readValue("__COMMAND"))) || ("ITEM2.Backward".equals(mgr.readValue("__COMMAND"))) ||
		 ("ITEM2.First".equals(mgr.readValue("__COMMAND")))   || ("ITEM2.Last".equals(mgr.readValue("__COMMAND"))) )
                q = trans.addEmptyQuery(itemblk);
            else
                q = trans.addQuery(itemblk);

            q.addWhereCondition("WO_NO = ? AND MAINT_MATERIAL_ORDER_NO = ?");
            q.addParameter("WO_NO",itemset2.getRow().getFieldValue("ITEM2_WO_NO"));
            q.addParameter("MAINT_MATERIAL_ORDER_NO",itemset2.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));
            q.includeMeta("ALL");

            mgr.querySubmit(trans,itemblk);

            headset.goTo(headsetRowNo);
            itemset2.goTo(item2rowno);

            if (itemset.countRows() > 0)
                setValuesInMaterials();
        }
        //Bug 72202, End
    }

    public void countFindITEM5()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ? AND MAINT_MATERIAL_ORDER_NO = ?");
        q.addParameter("WO_NO",itemset2.getRow().getFieldValue("ITEM2_WO_NO"));
        q.addParameter("MAINT_MATERIAL_ORDER_NO",itemset2.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));
        mgr.submit(trans);
        itemlay.setCountValue(toInt(itemset.getRow().getValue("N")));
        itemset.clear();
    }

    public void okFindITEM6()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        int headrowno = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk6);
        q.addWhereCondition("WO_NO = ? AND STD_JOB_CONTRACT = ?");
        q.addParameter("WO_NO",headset.getValue("WO_NO"));
        q.addParameter("STD_JOB_CONTRACT",headset.getValue("CONTRACT"));

        q.includeMeta("ALL");
        mgr.querySubmit(trans, itemblk6);

        headset.goTo(headrowno);
    }

    public void countFindITEM6()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        q = trans.addQuery(itemblk6);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ? AND STD_JOB_CONTRACT = ?");
        q.addParameter("WO_NO",headset.getValue("WO_NO"));
        q.addParameter("STD_JOB_CONTRACT",headset.getValue("CONTRACT"));
        q.includeMeta("ALL");

        int headrowno = headset.getCurrentRowNo();

        mgr.submit(trans);
        
        itemlay6.setCountValue(toInt(itemset6.getValue("N")));
        headset.goTo(headrowno);
    }

    public void search()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        q = trans.addEmptyQuery(headblk);
        q.addWhereCondition("CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT)");
        q.setOrderByClause("WO_NO");
        q.includeMeta("ALL");

        if (mgr.dataTransfered())
        {
            head_command="none";
            q.addOrCondition( mgr.getTransferedData() );
        }

        if (!("none".equals(head_command)))
        {
            if ("release".equals(head_command))
                q.addWhereCondition("OBJEVENTS like '%Release%'");

            if ("startOrder".equals(head_command))
                q.addWhereCondition("OBJEVENTS like '%StartOrder%'");
           //bug id 80964,start
            if ("workDone".equals(head_command))
                q.addWhereCondition("OBJEVENTS like '%workDone%'");
            if ("reported".equals(head_command))
                q.addWhereCondition("OBJEVENTS like '%reported%'");
        //bug id 80964,end
            if ("finish".equals(head_command))
                q.addWhereCondition("OBJEVENTS like '%Finish%'");
        }


        mgr.submit(trans);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWACTIVEROUNDNODATAFOUND: No data found."));
            headset.clear();
        }
    }

    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------

    public void getPriceInfo()
    {
        ASPManager mgr = getASPManager();

        // used to get some field values in itemset2.

        trans.clear();

        cmd = trans.addCustomCommand("GETPRICEINF","Work_Order_Coding_API.Get_Price_Info");
        cmd.addParameter("BASE_PRICE2");
        cmd.addParameter("SALE_PRICE2");
        cmd.addParameter("DISCOUNT2");
        cmd.addParameter("CURRENCY_RATE2");
        cmd.addParameter ("CATALOG_CONTRACT",itemset2.getValue("CATALOG_CONTRACT"));
        cmd.addParameter ("ITEM2_CATALOG_NO",itemset2.getValue("CATALOG_NO"));
        cmd.addParameter ("CUSTOMER_NO",headset.getValue("CUSTOMER_NO"));
        cmd.addParameter ("AGREEMENT_ID",headset.getValue("AGREEMENT_ID"));
        cmd.addParameter ("ITEM2_PRICE_LIST_NO",itemset2.getValue("PRICE_LIST_NO"));
        cmd.addParameter ("QTY_REQUIRED",itemset2.getValue("QTY_REQUIRED"));

        trans = mgr.validate(trans);    

        nSalesPriceVal = trans.getNumberValue("GETPRICEINF/DATA/BASE_PRICE2");
        if (isNaN(nSalesPriceVal))
            nSalesPriceVal=0;
        nQtyReqd = trans.getNumberValue("GETPRICEINF/DATA/QTY_REQUIRED");
        if (isNaN(nQtyReqd))
            nQtyReqd=0;
        if (( nSalesPriceVal==0 ) ||  (nQtyReqd==0 ))
            nSalesPriceAmnt = 0;
        else
            nSalesPriceAmnt = nSalesPriceVal*nQtyReqd;


        r = itemset2.getRow();
        r.setNumberValue("ITEM2_LISTPRICE",nSalesPriceVal);
        r.setNumberValue("ITEM2_SALESPRICEAMOUNT",nSalesPriceAmnt);
        itemset2.setRow(r);

        trans.clear();
    }


    public void setSalesPartCost()
    {
        int ref;
        ASPManager mgr = getASPManager();
        isSecure = new String[7];

        trans.clear();
        n = itemset1.countRows();
        itemset1.first();

        checksec("Sales_Part_API.Get_Cost",1,isSecure);

        if (n > 0)
        {
            for (i=0; i<=n; ++i)
            {
                catalogNo = itemset1.getRow().getValue("CATALOG_NO");
                item1Contract =itemset1.getRow().getFieldValue("ITEM1_CONTRACT");
                roleCode = itemset1.getRow().getValue("ROLE_CODE");
                item1OrgCode = itemset1.getRow().getFieldValue("ITEM1_ORG_CODE");

                if (isSecure[1] =="true")
                {
                    cmd = trans.addCustomFunction("COSTA"+i,"Sales_Part_API.Get_Cost","SALESPARTCOST");
                    cmd.addParameter("ITEM1_CONTRACT",item1Contract);
                    cmd.addParameter("CATALOG_NO",catalogNo);
                }

                cmd = trans.addCustomFunction("COSTB"+i,"Role_API.Get_Role_Cost","COST2");
                cmd.addParameter("ROLE_CODE",roleCode);

                cmd = trans.addCustomFunction("COSTC"+i,"Organization_API.Get_Org_Cost","COST3");
                cmd.addParameter("ITEM1_CONTRACT",item1Contract);
                cmd.addParameter("ITEM1_ORG_CODE",item1OrgCode);

                itemset1.next();
            }

            trans = mgr.validate(trans);
            itemset1.first();
            ref = 1;


            for (i=0; i<=n; ++i)
            {
                numCost = 0;
                row = itemset1.getRow();

                if (!mgr.isEmpty(itemset1.getRow().getValue("ROLE_CODE")))
                {
                    numCost= trans.getNumberValue("COSTB"+i+"/DATA/COST2");
                    if (isNaN(numCost))
                        numCost=0;
                }
                if (numCost==0 && !mgr.isEmpty(itemset1.getRow().getFieldValue("ITEM1_CONTRACT")) && !mgr.isEmpty(itemset1.getRow().getFieldValue("ITEM1_ORG_CODE")))
                {
                    numCost= trans.getNumberValue("COSTC"+i+"/DATA/COST3");
                    if (isNaN(numCost))
                        numCost=0;
                }

                if (numCost==0 && !mgr.isEmpty(itemset1.getRow().getValue("CATALOG_NO")))
                    if (isSecure[ref += 1] =="true")
                    {
                        numCost= trans.getNumberValue("COSTA"+i+"/DATA/SALESPARTCOST");
                        if (isNaN(numCost))
                            numCost=0;
                    }
                    else
                        numCost= 0;

                row.setNumberValue("SALESPARTCOST",numCost);
                itemset1.setRow(row);

                itemset1.next();
            }
        }
        itemset1.first();
    }


    public void setValuesInMaterials()
    {
        ASPManager mgr = getASPManager();

        trans.clear();      

        securityOk = "";
        secBuff = mgr.newASPTransactionBuffer();
        //secBuff.addSecurityQuery("Inventory_Part_API", "Get_Inventory_Value_By_Method");
        secBuff.addSecurityQuery("Active_Separate_API", "Get_Inventory_Value");
        secBuff = mgr.perform(secBuff);

        //if (secBuff.getSecurityInfo().itemExists("Inventory_Part_API.Get_Inventory_Value_By_Method"))
        if (secBuff.getSecurityInfo().itemExists("Active_Separate_API.Get_Inventory_Value"))
            securityOk = "TRUE";

        n = itemset.countRows();

        if (n > 0)
        {
            itemset.first();

            for (i=0; i<=n; ++i)
            {
                spareCont = itemset.getRow().getValue("SPARE_CONTRACT");
                partNo =itemset.getRow().getFieldValue("PART_NO");
                cataNo = itemset.getRow().getFieldValue("ITEM_CATALOG_NO");
                nPlanQty = itemset.getRow().getNumberValue("PLAN_QTY");
                if (isNaN(nPlanQty))
                    nPlanQty=0;
                cataCont = itemset.getRow().getFieldValue("CATALOG_CONTRACT");
                cusNo = headset.getRow().getFieldValue("CUSTOMER_NO");
                agreeId = headset.getRow().getFieldValue("AGREEMENT_ID");
                priceListNo = itemset.getRow().getFieldValue("ITEM_PRICE_LIST_NO");
                planLineNo = itemset.getRow().getFieldValue("PLAN_LINE_NO");

                
                String serialNo = itemset.getRow().getFieldValue("SERIAL_NO");
                String conditionCode = itemset.getRow().getFieldValue("CONDITION_CODE");
                String configurationId = itemset.getRow().getFieldValue("CONFIGURATION_ID");
                

                if ("TRUE".equals(securityOk))
                {
                    /*cmd = trans.addCustomFunction("GETCOST"+i,"Inventory_Part_API.Get_Inventory_Value_By_Method","ITEM_COST");
                    cmd.addParameter("SPARE_CONTRACT",spareCont);
                    cmd.addParameter("PART_NO",partNo);
                    */
                    
                    cmd = trans.addCustomCommand("GETCOST"+i,"Active_Separate_API.Get_Inventory_Value");
                    cmd.addParameter("ITEM_COST");
                    cmd.addParameter("SPARE_CONTRACT", spareCont);
                    cmd.addParameter("PART_NO", partNo);
                    cmd.addParameter("SERIAL_NO", serialNo);
                    cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
                    cmd.addParameter("CONDITION_CODE", conditionCode);  
                } 

                if ((!mgr.isEmpty(cataNo)) && !(nPlanQty==0))
                {
                    cmd = trans.addCustomCommand("PRICEINFO"+i,"Work_Order_Coding_API.Get_Price_Info");
                    cmd.addParameter("ITEM_BASE_PRICE","0");
                    cmd.addParameter("ITEM_SALE_PRICE","0");
                    cmd.addParameter("ITEM_DISCOUNT","0");
                    cmd.addParameter("ITEM_CURRENCY_RATE","0");
                    cmd.addParameter("CATALOG_CONTRACT",cataCont);
                    cmd.addParameter("ITEM_CATALOG_NO",cataNo);
                    cmd.addParameter("CUSTOMER_NO",cusNo);
                    cmd.addParameter("AGREEMENT_ID",agreeId);
                    cmd.addParameter("ITEM_PRICE_LIST_NO",priceListNo);
                    cmd.addParameter("PLAN_QTY",mgr.getASPField("PLAN_QTY").formatNumber(nPlanQty));

                    cmd = trans.addCustomFunction("LISTPRICE"+i,"WORK_ORDER_PLANNING_API.Get_Sales_Price","ITEM_LIST_PRICE");
                    cmd.addParameter("ITEM_WO_NO");
                    cmd.addParameter("PLAN_LINE_NO",planLineNo);
                }
                itemset.next();
            }

            trans = mgr.validate(trans);

            itemset.first();

            for (i=0; i<n; ++i)
            {
                nCost = 0;
                row = itemset.getRow();
                if (!mgr.isEmpty(itemset.getRow().getFieldValue("PART_NO")))
                {
                    if ("TRUE".equals(securityOk))
                    {
                        nCost= trans.getNumberValue("GETCOST"+i+"/DATA/ITEM_COST");
                        if (isNaN(nCost))
                            nCost=0;
                    }
                    else
                        nCost=0;                     
                }
                nPlanQty = itemset.getRow().getNumberValue("PLAN_QTY");
                if (isNaN(nPlanQty))
                    nPlanQty=0;

                if (!(nPlanQty==0))
                {
                    if ("TRUE".equals(securityOk))
                    {
                        nCostTemp = trans.getNumberValue("GETCOST"+i+"/DATA/ITEM_COST");
                        if (isNaN(nCostTemp))
                            nCostTemp=0;
                    }
                    else
                        nCostTemp=0;   
                    nCostAmount = nCostTemp * nPlanQty;
                }
                else
                    nCostAmount = 0;

                priceListNo = itemset.getRow().getFieldValue("ITEM_PRICE_LIST_NO");
                cataNo = itemset.getRow().getFieldValue("ITEM_CATALOG_NO");
                nDiscount = itemset.getRow().getNumberValue("DISCOUNT");
                if (isNaN(nDiscount))
                    nDiscount=0;

                if ((!mgr.isEmpty(cataNo)) && (!(nPlanQty==0)))
                {
                    listPrice = trans.getNumberValue("LISTPRICE"+i+"/DATA/ITEM_LIST_PRICE");
                    if (isNaN(listPrice))
                        listPrice=0;

                    if (mgr.isEmpty(priceListNo))
                        priceListNo = trans.getBuffer("PRICEINFO"+i+"/DATA").getValue("ITEM_PRICE_LIST_NO");

                    if (nDiscount==0)
                    {
                        nDiscount = trans.getNumberValue("PRICEINFO"+i+"/DATA/DISCOUNT");      
                        if (isNaN(nDiscount))
                            nDiscount=0;
                    }

                    if (listPrice==0)
                    {
                        listPrice = trans.getNumberValue("PRICEINFO"+i+"/DATA/ITEM_BASE_PRICE");
                        if (isNaN(listPrice))
                            listPrice=0;
                        planQty = trans.getNumberValue("PRICEINFO"+i+"/DATA/PLAN_QTY");  
                        if (isNaN(planQty))
                            planQty=0;
                        nSaleUnitPrice = trans.getNumberValue("PRICEINFO"+i+"/DATA/ITEM_SALE_PRICE");
                        if (isNaN(nSaleUnitPrice))
                            nSaleUnitPrice=0;
                        nSalesPriceAmount  = nSaleUnitPrice * planQty;
                    }
                    else
                    {
                        nListPriceTemp = trans.getNumberValue("LISTPRICE"+i+"/DATA/LIST_PRICE");
                        if (isNaN(nListPriceTemp))
                            nListPriceTemp = 0;

                        planQty = trans.getNumberValue("PRICEINFO"+i+"/DATA/PLAN_QTY");  
                        if (isNaN(planQty))
                            planQty=0;

                        nDiscountTemp = itemset.getRow().getNumberValue("DISCOUNT");
                        if (isNaN(nDiscountTemp))
                            nDiscountTemp=0;

                        if (nDiscountTemp==0)
                            nDiscountTemp = trans.getNumberValue("PRICEINFO"+i+"/DATA/ITEM_DISCOUNT");
                        if (isNaN(nDiscountTemp))
                            nDiscountTemp=0;

                        nSalesPriceAmount = nListPriceTemp * planQty;
                        nSalesPriceAmount = nSalesPriceAmount - (nDiscountTemp / 100 * nSalesPriceAmount);
                    }

                    //row.setValue("PRICE_LIST_NO",priceListNo);
                    //row.setNumberValue("LIST_PRICE",listPrice);
                    //row.setNumberValue("DISCOUNT",nDiscount);
                    row.setNumberValue("ITEMSALESPRICEAMOUNT",nSalesPriceAmount);
                }


                row.setNumberValue("ITEM_COST",nCost);
                row.setNumberValue("AMOUNTCOST",nCostAmount);

                itemset.setRow(row);

                itemset.next();
            }
        }
        itemset.first();
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void none()
    {
        ASPManager mgr = getASPManager();

        mgr.showAlert(mgr.translate("PCMWACTIVEROUNDNORMB: No RMB method has been selected."));
    }


    public void none1()
    {
        ASPManager mgr = getASPManager();

        mgr.showAlert(mgr.translate("PCMWACTIVEROUNDNORMB: No RMB method has been selected."));
    }


    public void none2()
    {
        ASPManager mgr = getASPManager();

        mgr.showAlert(mgr.translate("PCMWACTIVEROUNDNORMB: No RMB method has been selected."));
    }


    public void none3()
    {
        ASPManager mgr = getASPManager();

        mgr.showAlert(mgr.translate("PCMWACTIVEROUNDNORMB: No RMB method has been selected."));
    }


    public void report()
    {

        overview = false;
        headset.selectRows();

        if (headset.countSelectedRows()>0)
            headset.setFilterOn();

        multirow = true;

    }


    public void release()
    {
        ASPManager mgr = getASPManager();
        //Web Alignment - Enable multirow action
        perform("RELEASE__");
    }


    public void startOrder()
    {
        ASPManager mgr = getASPManager();
        //Web Alignment - Enable multirow action
        perform("START_ORDER__");

    }

   //bug id 80964,start
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
    public void finish()
    {
        ASPManager mgr = getASPManager();

        if (!CheckAllMaterialIssued())
        {
            if (headlay.isMultirowLayout())
                headset.goTo(headset.getRowSelected());

            currrow = headset.getCurrentRowNo();
            CurrRowNo = headset.getCurrentRowNo();
            scompany = headset.getRow().getValue("COMPANY");  
            scontract = headset.getRow().getValue("CONTRACT");         
            obj = headset.getRow().getValue("OBJEVENTS");

            if (obj.indexOf("Finish") > -1)
            {
                sWindowHandle = "ActiveRound"; 
                newWindowURL = "ActiveRoundFinishConfirmDlg.page?ROUTE_WO_NO="+mgr.URLEncode(headset.getValue("WO_NO"))+"&WINHANDVAL="+mgr.URLEncode(sWindowHandle)+"&QRYSTRVAL=";  
                
                openFormInNewWin = true;      
            }
            else
            {
                mgr.showAlert(mgr.translate("PCMWACTIVEROUNDNOFINI: Cannot perform 'Finished' on the selected line."));
            }
        }
        else
        {
            issued = "TRUE";
            ctx.setCookie( "PageID_my_cookie", "TRUE" );
        }
    }

    public void finish1()
    {
        ASPManager mgr = getASPManager();
            
        currrow = headset.getCurrentRowNo();
        CurrRowNo = headset.getCurrentRowNo();
        scompany = headset.getRow().getValue("COMPANY");  
        scontract = headset.getRow().getValue("CONTRACT");         
        obj = headset.getRow().getValue("OBJEVENTS");

        if (obj.indexOf("Finish") > -1)
        {
            sWindowHandle = "ActiveRound"; 
            newWindowURL = "ActiveRoundFinishConfirmDlg.page?ROUTE_WO_NO="+mgr.URLEncode(headset.getValue("WO_NO"))+"&WINHANDVAL="+mgr.URLEncode(sWindowHandle)+"&QRYSTRVAL=";  
            openFormInNewWin = true;      
        }
        else
        {
            mgr.showAlert(mgr.translate("PCMWACTIVEROUNDNOFINI: Cannot perform 'Finished' on the selected line."));
        }
    }

    public void requisitions()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.store();
        else
        {
            headset.unselectRows();
            headset.selectRow();
        }

        calling_url=mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url);

	//Retrive the current object status
	trans.clear();

	cmd = trans.addCustomFunction("GETOBJSTATE", "Active_Round_Api.Get_Obj_State", "OBJSTATE" );
        cmd.addParameter("WO_NO", headset.getRow().getValue("WO_NO"));

        trans = mgr.perform(trans);

	String sObjState  = trans.getBuffer("GETOBJSTATE/DATA").getFieldValue("OBJSTATE");

        buffer = mgr.newASPBuffer();
        row = buffer.addBuffer("0");
        row.addItem("WO_NO", headset.getRow().getValue("WO_NO"));
        row.addItem("MCH_CODE", "");
        row.addItem("MCH_NAME", "");
	row.addItem("OBJSTATE", headset.getRow().getValue("OBJSTATE"));

        bOpenNewWindow = true;
        urlString = createTransferUrl("WorkOrderRequisHeaderRMB.page", buffer);
        newWinHandle = "requisitions";     
    }

    public void budget()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        String calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);

        bOpenNewWindow = true;
        urlString = "RoundWorkOrderBudget.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO"));

        newWinHandle = "budget"; 
    }

    public void printWO()
    {
        ASPManager mgr = getASPManager();

        //Web Alignment - Enable multirow action
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
            attr1 = "REPORT_ID" + (char)31 + "ACTIVE_ROUND_WO_PRINT_REP" + (char)30;
            attr2 = "WO_NO_LIST" + (char)31 + headset.getValue("WO_NO") + (char)30;
            attr3 =  "";
            attr4 =  "";

            cmd = trans.addCustomCommand("PRNT" + i,"Archive_API.New_Client_Report");
            cmd.addParameter("ATTR0");                       
            cmd.addParameter("ATTR1",attr1);       
            cmd.addParameter("ATTR2",attr2);              
            cmd.addParameter("ATTR3",attr3);      
            cmd.addParameter("ATTR4",attr4);  

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
            printBuff.addItem("RESULT_KEY", trans.getValue("PRNT" + i + "/DATA/ATTR0"));
        }

        callPrintDlg(print,true);
    }

    public void transferToCusOrder()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.store();
        else
        {
            headset.unselectRows();
            headset.selectRow();
        }

        //Web Alignment - simplify code for RMBs
        bOpenNewWindow = true;
        urlString = "ActiveWorkOrder4.page?WO_NO="+mgr.URLEncode(headset.getValue("WO_NO"));
        newWinHandle = "transferToCustomerOrder";
    }

    public void disconnectFromActivity()
    {
        ASPManager mgr = getASPManager();
                
        if (headlay.isMultirowLayout())
        {
            headset.goTo(headset.getRowSelected());
        }

        trans.clear();
        int nRow = headset.getCurrentRowNo();
        ASPBuffer tempBuff = headset.getRow();
        tempBuff.setNumberValue("ACTIVITY_SEQ", mgr.readNumberValue("ACTIVITY_SEQ_PROJ"));
        headset.setRow(tempBuff);
        mgr.submit(trans);

        headset.refreshRow();
        headset.goTo(nRow);
    }

    public void connectToActivity(){
        ASPManager mgr = getASPManager();
        int headRowNo = headset.getCurrentRowNo();
        if (headlay.isMultirowLayout())
        {
            headset.goTo(headset.getRowSelected());
        }

        trans.clear();
        ASPBuffer tempBuff = headset.getRow();
        tempBuff.setNumberValue("ACTIVITY_SEQ", mgr.readNumberValue("ACTIVITY_SEQ_PROJ"));
        headset.setRow(tempBuff);
        String wo_no=headset.getRow().getValue("WO_NO");
        mgr.submit(trans);
        
        headset.refreshRow();        
        headset.goTo(headRowNo);
    }

    public void projectActivityInfo()
    {
        ASPManager mgr = getASPManager();
        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
        {
            headset.unselectRows();
            headset.selectRow();
        }

        String act_seq = mgr.getASPField("ACTIVITY_SEQ1").formatNumber(headset.getRow().getNumberValue("ACTIVITY_SEQ"));

        bOpenNewWindow = true;
        urlString = "ProjectActivityInfoDlg.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) + 
                    "&ACTIVITY_SEQ=" + mgr.URLEncode(act_seq) ;
        newWinHandle = "ProjectActivityInfoDlg";
    }

    public void activityInfo()
    {
        ASPManager mgr = getASPManager();
        trans.clear();
        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        String act_seq = mgr.getASPField("ACTIVITY_SEQ1").formatNumber(headset.getRow().getNumberValue("ACTIVITY_SEQ"));
        

        bOpenNewWindow = true;

        urlString = "../projw/Activity.page?ACTIVITY_SEQ=" + mgr.URLEncode(act_seq); 

        newWinHandle = "ActivityInfo"; 
    }

    public void preposting()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.store();
        else
        {
            headset.unselectRows();
            headset.selectRow();
        }

        calling_url=mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url);

        headset.selectRows();
        headset.selectRow();


        eval(headblk.generateAssignments());

        String contract = headset.getRow().getValue("CONTRACT");
        //String pre_posting_id = headset.getRow().getValue("PRE_POSTING_ID");
        String wo_no = headset.getRow().getValue("WO_NO");
        String enabl10 = "0";
        lout = (headlay.isMultirowLayout()?"1":"0");

        if (( !("Closed".equals(mgr.getASPField("OBJSTATE").getValue())) ))
        {
            String enabledArr[] = getEnabledFields();

            //Making the buffer to be passed to prepost.page
            ASPBuffer prepost_buffer = mgr.newASPBuffer();
            ASPBuffer data = prepost_buffer.addBuffer("dataBuffer");
            data.addItem("CONTRACT",headset.getValue("CONTRACT"));
            data.addItem("PRE_ACCOUNTING_ID",headset.getRow().getValue("PRE_ACCOUNTING_ID"));
            data.addItem("ENABL0",enabledArr[0]);
            data.addItem("ENABL1",enabledArr[1]);
            data.addItem("ENABL2",enabledArr[2]);
            data.addItem("ENABL3",enabledArr[3]);
            data.addItem("ENABL4",enabledArr[4]);
            data.addItem("ENABL5",enabledArr[5]);
            data.addItem("ENABL6",enabledArr[6]);
            data.addItem("ENABL7",enabledArr[7]);
            data.addItem("ENABL8",enabledArr[8]);
            data.addItem("ENABL9",enabledArr[9]);
            data.addItem("ENABL10",enabl10);

            ASPBuffer return_buffer = prepost_buffer.addBuffer("return_buffer");
            ASPBuffer ret = return_buffer.addBuffer("ROWS");
            ret.parse(headset.getRows("WO_NO").format());
            return_buffer.addItem("CURR_ROW",Integer.toString(headset.getCurrentRowNo()));
            return_buffer.addItem("LAYOUT",lout);

            mgr.transferDataTo("../mpccow/PreAccountingDlg.page",prepost_buffer);

        }
    }

    public void returns()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.store();
        else
        {
            headset.unselectRows();
            headset.selectRow();
        }

        String calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);

        bOpenNewWindow = true;
        urlString = createTransferUrl("WorkOrderReturns.page", headset.getSelectedRows("WO_NO"));
        newWinHandle = "returns";         
    }


    public String[] getEnabledFields()
    {
        String enabledArr[] = new String[10];

        ASPManager mgr = getASPManager();

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
        cmd.addParameter("STR_CODE","T51"); 
        cmd.addParameter("CONTROL_TYPE"); 
        cmd.addParameter("COMPANY", headset.getRow().getValue("COMPANY")); 

        trans = mgr.perform(trans);

        enabledArr[0] = trans.getValue("POSTI/DATA/CODE_A");
        enabledArr[1] = trans.getValue("POSTI/DATA/CODE_B");
        enabledArr[2] = trans.getValue("POSTI/DATA/CODE_C");
        enabledArr[3] = trans.getValue("POSTI/DATA/CODE_D");
        enabledArr[4] = trans.getValue("POSTI/DATA/CODE_E");
        enabledArr[5] = trans.getValue("POSTI/DATA/CODE_F");
        enabledArr[6] = trans.getValue("POSTI/DATA/CODE_G");
        enabledArr[7] = trans.getValue("POSTI/DATA/CODE_H");
        enabledArr[8] = trans.getValue("POSTI/DATA/CODE_I");
        enabledArr[9] = trans.getValue("POSTI/DATA/CODE_J");


        return enabledArr;

    }

    public void completed()
    {
        ASPManager mgr = getASPManager();
        int count = 0;
        String dbval;
        String rStatus0; 
        trans.clear();

        currrow = headset.getCurrentRowNo();          

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

        rStatus0 = headset.getValue("RSTATUS0");

        for (int i = 0;i < count;i++)
        {

            buf = itemset0.getRow();
            buf.setValue("ROUND_REPORT_IN_STATUS", rStatus0);
            itemset0.setRow(buf);    

            trans.clear();

            if (itemlay0.isMultirowLayout())
                itemset0.next();
        }

        mgr.submit(trans); 
        itemset0.first();

        for (int i = 0;i < count;i++)
        {
            itemset0.refreshRow();

            if (itemlay0.isMultirowLayout())
                itemset0.next();
        }    

        if (itemlay0.isMultirowLayout())
            itemset0.setFilterOff();

        headset.goTo(currrow);
        itemset0.refreshAllRows();
        activateGeneral();
    }


    public void notCompleted()
    {
        ASPManager mgr = getASPManager();
        String rStatus1;
        String dbval;
        int count = 0;

        trans.clear();

        currrow = headset.getCurrentRowNo();          
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

        rStatus1 = headset.getValue("RSTATUS1");

        for (int i = 0;i < count;i++)
        {

            buf = itemset0.getRow();
            buf.setValue("ROUND_REPORT_IN_STATUS", rStatus1);
            itemset0.setRow(buf);    

            trans.clear();

            if (itemlay0.isMultirowLayout())
                itemset0.next();
        }

        mgr.submit(trans); 

        itemset0.first();

        for (int i = 0;i < count;i++)
        {
            itemset0.refreshRow();

            if (itemlay0.isMultirowLayout())
                itemset0.next();
        }    

        if (itemlay0.isMultirowLayout())
            itemset0.setFilterOff();

        headset.goTo(currrow);
        itemset0.refreshAllRows();
        activateGeneral();
    }


    public void faultReport()
    {
        ASPManager mgr = getASPManager();

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());
        //Web Alignment - simplify code for RMBs
        bOpenNewWindow = true;
        urlString = "ActiveSeparate.page?MCH_CODE="+mgr.URLEncode( mgr.nvl(itemset0.getRow().getValue("MCH_CODE"),"")) +
			                               "&CONTRACT="+mgr.URLEncode( mgr.nvl(headset.getValue("CONTRACT"),"")) +
			                               "&MCH_CODE_CONTRACT="+mgr.URLEncode( mgr.nvl(itemset0.getRow().getValue("MCH_CODE_CONTRACT"),"")) +
			                               "&MCH_CODE_DESCRIPTION="+mgr.URLEncode( mgr.nvl(itemset0.getRow().getValue("OBJECTDESCRIPTION"),"")) +
			                               "&TEST_POINT_ID="+mgr.URLEncode( mgr.nvl(itemset0.getRow().getValue("TEST_POINT_ID"),"")) +
			                               "&PM_DESCR="+mgr.URLEncode( mgr.nvl(itemset0.getRow().getValue("ACTCODEIDDESCRIPTION"),"")) +
			                               "&PM_NO="+mgr.URLEncode( mgr.nvl(itemset0.getRow().getValue("PM_NO"),"")) +
			                               "&NOTE="+mgr.URLEncode( mgr.nvl(itemset0.getRow().getValue("INSPECTION_NOTE"),"")) ;
        newWinHandle = "faultr";

    }


    public void serviceRequest()
    {
        ASPManager mgr = getASPManager();

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());
        //Web Alignment - simplify code for RMBs
        bOpenNewWindow = true;
        urlString = "ActiveSeparate3.page?MCH_CODE="+mgr.URLEncode( mgr.nvl(itemset0.getRow().getValue("MCH_CODE"),"")) +
			                                  "&CONTRACT="+mgr.URLEncode( mgr.nvl(headset.getValue("CONTRACT"),"")) +
														 "&MCH_CODE_CONTRACT="+mgr.URLEncode( mgr.nvl(itemset0.getRow().getValue("MCH_CODE_CONTRACT"),"")) +
														 "&MCH_CODE_DESCRIPTION="+mgr.URLEncode( mgr.nvl(itemset0.getRow().getValue("OBJECTDESCRIPTION"),"")) +
														 "&TEST_POINT_ID="+mgr.URLEncode( mgr.nvl(itemset0.getRow().getValue("TEST_POINT_ID"),"")) +
														 "&PM_DESCR="+mgr.URLEncode( mgr.nvl(itemset0.getRow().getValue("ACTCODEIDDESCRIPTION"),"")) +
														 "&PM_NO="+mgr.URLEncode( mgr.nvl(itemset0.getRow().getValue("PM_NO"),"")) +
														 "&NOTE="+mgr.URLEncode( mgr.nvl(itemset0.getRow().getValue("INSPECTION_NOTE"),"")) ;
        newWinHandle = "sevreq";
    }

    public void quickReport()
    {
        ASPManager mgr = getASPManager();

        itemset0.storeSelections();   

        if (itemlay0.isMultirowLayout())
            itemset0.store();
        else
        {
            itemset0.unselectRows();
            itemset0.selectRow();
        }

        calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url);

        buffer = mgr.newASPBuffer();
        row = buffer.addBuffer("0");
        row.addItem("PM_NO",itemset0.getRow().getValue("PM_NO"));
        row = buffer.addBuffer("1");
        row.addItem("PM_REVISION",itemset0.getRow().getValue("PM_REVISION"));
        row = buffer.addBuffer("2");
        //Bug 84119, Start
        row.addItem("MCH_CODE_CONTRACT",itemset0.getRow().getValue("MCH_CODE_CONTRACT"));
        //Bug 84119, End
        row = buffer.addBuffer("3");
        row.addItem("MCH_CODE",itemset0.getRow().getValue("MCH_CODE"));
        row = buffer.addBuffer("4");
        row.addItem("TEST_POINT_ID",itemset0.getRow().getValue("TEST_POINT_ID"));
        row = buffer.addBuffer("5");
        row.addItem("TEST_POINT_DESCR",itemset0.getRow().getValue("MSEQOBJECTTESTPOINTDESCRIPTIO"));
        row = buffer.addBuffer("6");
        row.addItem("WO_NO",itemset0.getRow().getValue("WO_NO"));
        //Bug 84119, Start
        row = buffer.addBuffer("7");
        row.addItem("CONTRACT",headset.getRow().getValue("CONTRACT"));
        //Bug 84119, End
        //Web Alignment - simplify code for RMBs
        bOpenNewWindow = true;
        urlString = createTransferUrl("QuickReportInWorkDlg.page", buffer);
        newWinHandle = "quickReport";


    }


    public void routePMAction()
    {
        ASPManager mgr = getASPManager();

        if (itemlay0.isMultirowLayout())
            itemset0.store();
        else
        {
            itemset0.unselectRows();
            itemset0.selectRow();
        }

        buffer = mgr.newASPBuffer();
        row = buffer.addBuffer("0");
        row.addItem("PM_NO",itemset0.getRow().getValue("PM_NO"));
        row = buffer.addBuffer("1");
        row.addItem("PM_REVISION",itemset0.getRow().getValue("PM_REVISION"));


        //Web Alignment - open RMBs in a new window
        bOpenNewWindow = true;
        urlString = createTransferUrl("PmActionRound.page", buffer);
        newWinHandle = "routePM";
    }


    public void permitAttr()
    {
        ASPManager mgr = getASPManager();

        if (itemlay3.isMultirowLayout())
            itemset3.store();
        else
        {
            itemset3.unselectRows();
            itemset3.selectRow();
        }

        calling_url=mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url);

        primary_key = headset.getRow().getValue("WO_NO");
        ctx.setGlobal("FORM_NAME","ActiveRound.page");
        ctx.setGlobal("PRIMARY_KEY",primary_key); 

        itemset3.storeSelections();

        buffer=mgr.newASPBuffer();
        row=buffer.addBuffer("0");
        row.addItem("PERMIT_TYPE_ID",itemset3.getRow().getValue("PERMIT_TYPE_ID"));
        row=buffer.addBuffer("1");
        row.addItem("WO_NO",primary_key);
        String row_no = String.valueOf(itemset3.getCurrentRowNo());
        row=buffer.addBuffer("2");
        row.addItem("PERMIT_SINGLE_ROW_NO",row_no);

        //Web Alignment - open RMBs in a new window
        bOpenNewWindow = true;
        urlString = createTransferUrl("PermitTypeRMB.page", buffer);
        newWinHandle = "permitAttribute";
        //
    }

    public void preparePermit()
    {
        ASPManager mgr = getASPManager();

        String calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);


        if (itemlay3.isMultirowLayout())
            itemset3.store();
        else
        {
            itemset3.unselectRows();
            itemset3.selectRow();
        }

        bOpenNewWindow = true;
        urlString = createTransferUrl("Permit.page", itemset3.getSelectedRows("PERMIT_SEQ"));
        newWinHandle = "preparePermit"; 

    }

    public void createPermit()
    {
        ASPManager mgr = getASPManager();

        String work_ord_no = headset.getRow().getValue("WO_NO");

        bOpenNewWindow = true;
        urlString = "CreatePermitDlg.page?START=ARound&WO_NO=" + mgr.URLEncode(work_ord_no);
        newWinHandle = "create_permit";

    }

    public void replacePermit()
    {
        ASPManager mgr = getASPManager();

        String work_ord_no = headset.getRow().getValue("WO_NO");
        ctx.setGlobal("PRIMARY_KEY", work_ord_no); 

        if (itemlay3.isMultirowLayout())
            itemset3.goTo(itemset3.getRowSelected());
        else
            itemset3.selectRow();


        bOpenNewWindow = true;
        urlString = "ReplacePermitDlg.page?START=ARound&PERMIT_SEQ=" + mgr.URLEncode(itemset3.getRow().getValue("PERMIT_SEQ")) + "&WO_NO=" + mgr.URLEncode(work_ord_no);
        newWinHandle = "replace_permit";

    }

    public void getDefaultUserAtRound(){
	ASPManager mgr = getASPManager();
	trans.clear();
	ASPCommand cmd = trans.addCustomFunction("USER_SIGN","Fnd_Session_API.Get_Fnd_User","SIGN");
	trans = mgr.perform(trans);

	defaultUser = trans.getValue("USER_SIGN/DATA/SIGN");
	if (!mgr.isEmpty("defaultUser")) {
	    gotDefaultUser = true;
	}
    }

    public void authorizeByUserDefaultAtRound()
    {
	    String sComp = headset.getValue("COMPANY");  
	    String sEmpName;
	    String sAuthId;
	    String sWarning = "FALSE";
	    ASPBuffer dataBuffer;

	    ASPManager mgr = getASPManager();
	    
	    if (!gotDefaultUser) {
		getDefaultUserAtRound();
	    }

	    trans.clear();

	    cmd = trans.addCustomFunction("PERSON", "Person_Info_API.Get_Id_For_User", "SIGNATURE");
	    cmd.addParameter("SIGN", defaultUser);

	    cmd = trans.addCustomFunction("EMP1", "Company_Emp_API.Get_Max_Employee_Id", "MAX_EMP_ID");
	    cmd.addParameter("COMPANY", sComp);
	    cmd.addReference("SIGNATURE", "PERSON/DATA");

	    cmd = trans.addCustomFunction("EMP2", "Person_Info_API.Get_Name", "EMP_NAME");
	    cmd.addParameter("SIGN", defaultUser);

	    trans = mgr.perform(trans);

	    sEmpName = trans.getValue("EMP2/DATA/EMP_NAME");
	    sAuthId = trans.getValue("EMP1/DATA/MAX_EMP_ID");
	    String sSignature = trans.getValue("PERSON/DATA/SIGNATURE");
	    if (!mgr.isEmpty(sSignature))
	    {
		    trans.clear();
                    ASPBuffer selectedRowsBuf = itemset4.getSelectedRows("COMPANY,WO_NO,ROW_NO");
		    if (mgr.isEmpty(sAuthId)) 
		    {
			duplicateFlag = true;
                        mgr.showAlert(mgr.translate("PCMWAUTHORIZECODINGDLGNOVALIDEMP: A valid employee id does not exists for the logged in user " + defaultUser));
		    }
		    else 
		    {
			for (int i = 0; i < selectedRowsBuf.countItems(); i++)
			{
				dataBuffer = selectedRowsBuf.getBufferAt(i);
				cmd = trans.addCustomCommand("ADELIVER_" + i, "Work_Order_Coding_API.Authorize");
				cmd.addParameter("WO_NO", dataBuffer.getValueAt(1));
				cmd.addParameter("ROW_NO", dataBuffer.getValueAt(2));
				cmd.addParameter("SIGN", sAuthId);
				cmd = trans.addCustomFunction("GETWARNING_"+i,"Work_Order_Coding_API.Has_Error_Transactions","WARNING");
				cmd.addParameter("WO_NO", dataBuffer.getValueAt(1));
				cmd.addParameter("ROW_NO", dataBuffer.getValueAt(2));
			}
    
			trans = mgr.perform(trans);
    
			for (int i = 0; i < selectedRowsBuf.countItems(); i++)
			{
			    if ("TRUE".equals(trans.getBuffer("GETWARNING_"+i).getValue("WARNING")))
				sWarning = "TRUE";
			}
    
			if ("TRUE".equals(sWarning))
			    mgr.showAlert(mgr.translate("PCMWAUTHORIZECODINGDLGERRTRANS: Exists Error on Transactions. See Transaction History for more information."));
    
		     }              
	    }
	    else
		    mgr.showAlert(mgr.translate("PCMWAUTHORIZECODINGDLGAUTHNOTREG: The authorizer is not registered."));
    }

    //Web Alignment - Changes to suit multiRow action
    public void authorize()
    {
        int count;
        ASPManager mgr = getASPManager();

        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;

        if (itemlay4.isMultirowLayout())
        {
            itemset4.storeSelections();
            itemset4.setFilterOn();
            count = itemset4.countSelectedRows();
        }
        else
        {
            itemset4.unselectRows();
            itemset4.selectRow();
            count = 1;
        }

        for (int i = 0; i < count; i++)
        {
            rowBuff = mgr.newASPBuffer();
	    authorizeByUserDefaultAtRound();

	    if (duplicateFlag)
		break;
	    
	    itemset4.refreshAllRows();

            if (itemlay4.isMultirowLayout())
                itemset4.next();
        }

        if (itemlay4.isMultirowLayout())
            itemset4.setFilterOff();
    }

    public void authorizeAll()
    {
        ASPManager mgr = getASPManager();
	int currentRow = itemset4.getCurrentRowNo();
	
        headset.unselectRows();
        headset.setFilterOff();

	itemset4.first();
	itemset4.unselectRows();

        for (int i = 0; i < itemset4.countRows(); i++)
        {
            // BUG ID 77280, Modified check for signature to booking status
            if (mgr.isEmpty(itemset4.getValue("WORK_ORDER_BOOK_STATUS")))
            {
		itemset4.selectRow();
		authorizeByUserDefaultAtRound();

		if (duplicateFlag) 
		    break;

		itemset4.refreshRow();
		itemset4.unselectRow();
            }

            itemset4.next();
        }
	itemset4.goTo(currentRow);
    }

    public void authorizeCorrect()
    {
        int count;
        ASPManager mgr = getASPManager();

        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;

        row_Nos="";
        bOpenNewWindow = true;      

        if (itemlay4.isMultirowLayout())
        {
            itemset4.storeSelections();
            itemset4.setFilterOn();
            count = itemset4.countSelectedRows();
        }
        else
        {
            itemset4.unselectRows();
            count = 1;
        }

        transferBuffer = mgr.newASPBuffer();
        //getcompany();

        for (int i = 0; i < count; i++)
        {

            rowBuff = mgr.newASPBuffer();

            if (i == 0)
            {
                rowBuff.addItem("COMPANY2", headset.getValue("COMPANY2"));
                rowBuff.addItem("WO_NO", itemset4.getValue("WO_NO"));
                rowBuff.addItem("SIGNATURE", itemset4.getValue("SIGNATURE"));
            }
            else
            {
                rowBuff.addItem(null, headset.getValue("COMPANY2"));
                rowBuff.addItem(null, itemset4.getValue("WO_NO"));
                rowBuff.addItem(null, itemset4.getValue("SIGNATURE"));
            }

            transferBuffer.addBuffer("DATA", rowBuff);

            if (itemlay4.isMultirowLayout())
                itemset4.next();
        }

        if (itemlay4.isMultirowLayout())
            itemset4.setFilterOff();

        calling_url=mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url);
        urlString = createTransferUrl("AuthorizeCodingDlgSM.page?QRYSTR=" + mgr.URLEncode(qrystr), transferBuffer);
        newWinHandle = "AuthCorr"; 
    }

    public void manageSalesRevenues()
    {
        ASPManager mgr = getASPManager();

        if (itemlay4.isMultirowLayout())
            itemset4.goTo(itemset4.getRowSelected());
        else
            singleRowNo = String.valueOf(itemset4.getCurrentRowNo());

        currrow = headset.getCurrentRowNo();          
        calling_url=mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url);

        wo_No = itemset4.getRow().getValue("WO_NO");
        sRowNo = itemset4.getRow().getFieldValue("ITEM4_ROW_NO");

        magFixed = "TRUE";
    }

    public void salesPartComp()
    {
        ASPManager mgr = getASPManager();
        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;

        if (itemlay4.isMultirowLayout())
            itemset4.store();
        else
        {
            itemset4.unselectRows();
            itemset4.selectRow();
        }

        mform = mgr.getURL();
        enableSalesPartComp();

        calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url); 
        transferBuffer = mgr.newASPBuffer();

        if (enableF)
        {

            rowBuff = mgr.newASPBuffer();
            rowBuff.addItem("CATALOG_NO", itemset4.getValue("CATALOG_NO"));
            rowBuff.addItem("LIST_PRICE", itemset4.getValue("LIST_PRICE"));
            rowBuff.addItem("CONTRACT", itemset4.getValue("CONTRACT"));
            rowBuff.addItem("LINE_DESCRIPTION", itemset4.getValue("LINEDESCRIPTION"));
            rowBuff.addItem("WO_NO", itemset4.getValue("WO_NO"));
            rowBuff.addItem("ROW_NO", itemset4.getValue("ROW_NO"));
            rowBuff.addItem("CUSTOMER_NO", itemset4.getValue("CUSTOMER_NO"));
            rowBuff.addItem("AGREEMENT_ID", itemset4.getValue("AGREEMENT_ID"));
            rowBuff.addItem("FORM2", mform);
            rowBuff.addItem("SINGLE_ROW_NO", singleRowNo);

            transferBuffer.addBuffer("DATA", rowBuff);

            fmtdBuff = buffer.format();
            bOpenNewWindow = true;
            urlString = createTransferUrl("SalesPartComplimentaryDlg.page", transferBuffer);;
            newWinHandle = "sales"; 
        }
        else
            mgr.showAlert(mgr.translate("PCMWACTIVEROUNDONERECONLY11: Cannot perform the action."));
    }

    public void enableSalesPartComp()
    {
        ASPManager mgr = getASPManager();
        String strExpense;

        catNo = itemset4.getRow().getFieldValue("ITEM4_CATALOG_NO");
        bookSta = itemset4.getRow().getFieldValue("ITEM4_WORK_ORDER_BOOK_STATUS");   
        costTy = itemset4.getRow().getFieldValue("ITEM4_WORK_ORDER_COST_TYPE");   
        cusOrNo = itemset4.getRow().getFieldValue("ORDER_NO");   

        cmd = trans.addCustomFunction("FUNC1","Work_Order_Cost_Type_Api.Get_Client_Value(1)","CLIENTVAL1");
        cmd = trans.addCustomFunction("FUNC2","Work_Order_Cost_Type_Api.Get_Client_Value(2)","CLIENTVAL2");
        cmd = trans.addCustomFunction("FUNC3","Work_Order_Cost_Type_Api.Get_Client_Value(3)","CLIENTVAL3");

        trans = mgr.validate(trans);

        sMaterial = trans.getValue("FUNC1/DATA/CLIENTVAL1");
        sExpences = trans.getValue("FUNC3/DATA/CLIENTVAL3");
        sExternal = trans.getValue("FUNC2/DATA/CLIENTVAL2");

        enableF = true;
    }

//-----------------------------------------------------------------------------
//------------------------  ITEMBAR2 CUSTOM FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

    public void performItem(String command) 
    {
        ASPManager mgr = getASPManager();

        //Web Alignment - Enable Multirow Action

        trans.clear();

        int headcurr = headset.getCurrentRowNo();

        if (itemlay2.isMultirowLayout())
        {
            itemset2.storeSelections();
            itemset2.markSelectedRows(command);
            mgr.submit(trans);

            itemset2.refreshAllRows();
        }
        else
        {
            itemset2.unselectRows();
            itemset2.markRow(command);
            int currrow = itemset2.getCurrentRowNo();
            mgr.submit(trans);

            itemset2.goTo(currrow);
            itemset2.refreshRow();
        }   

        headset.goTo(headcurr);
    }

    public boolean noReserv()
    {
        //Web Alignment - Enable Multirow Action
        ASPManager mgr = getASPManager();

        int count = 0;
        int bufferItemCount = 0;
        ASPBuffer queryResults;

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
            itemset2.selectRow();

            count = 1;
        }

        for (int i = 0; i < count; i++)
        {
            cmd = trans.addQuery("GET_QTY_FOR_NO_RES_" + i, "select QTY_ASSIGNED from MAINT_MATERIAL_REQ_LINE where MAINT_MATERIAL_ORDER_NO = ?");
            cmd.addParameter("MAINT_MATERIAL_ORDER_NO",itemset2.getValue("MAINT_MATERIAL_ORDER_NO"));

            itemset2.next();
        }

        trans = mgr.perform(trans);

        if (itemlay2.isMultirowLayout())
            itemset2.setFilterOff();

        for (int i = 0; i < count; i++)
        {
            queryResults = trans.getBuffer("GET_QTY_FOR_NO_RES_" + i);

            bufferItemCount = queryResults.countItems();
            for (int j = 0; j < bufferItemCount; j++)
            {
                if (queryResults.getBufferAt(j).getNumberValue("QTY_ASSIGNED") > 0)
                    return false;
            }
        }

        return true;
    }

    public boolean noIssue()
    {
        //Web Alignment - Enable Multirow Action
        ASPManager mgr = getASPManager();

        int count = 0;
        int bufferItemCount = 0;
        ASPBuffer queryResults;

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
            itemset2.selectRow();

            count = 1;
        }

        for (int i = 0; i < count; i++)
        {
            // SQLInjection_Safe ILSOLK 20070705
            cmd = trans.addQuery("GET_QTY_FOR_NO_ISSUE_" + i, "select QTY from MAINT_MATERIAL_REQ_LINE where MAINT_MATERIAL_ORDER_NO = ?");
	    cmd.addParameter("MAINT_MATERIAL_ORDER_NO" ,itemset2.getValue("MAINT_MATERIAL_ORDER_NO"));

            itemset2.next();
        }

        trans = mgr.perform(trans);

        if (itemlay2.isMultirowLayout())
            itemset2.setFilterOff();

        for (int i = 0; i < count; i++)
        {
            queryResults = trans.getBuffer("GET_QTY_FOR_NO_ISSUE_" + i);

            bufferItemCount = queryResults.countItems();
            for (int j = 0; j < bufferItemCount; j++)
            {
                if (queryResults.getBufferAt(j).getNumberValue("QTY") > 0)
                    return false;
            }
        }

        return true;
    }

    public void plan()
    {
        ASPManager mgr = getASPManager();
        //Web Alignment - Enable Multirow Action  
        if (noReserv() && noIssue())
            performItem("PLAN__");
        else
            mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANNOTPLAN: Can not perform on selected line"));    
    }

    public void releaseMat()
    {
        ASPManager mgr = getASPManager();

        //Web Alignment - Enable Multirow Action  
        performItem("RELEASE__");
    }

    public void close()
    {
        ASPManager mgr = getASPManager();
        //Web Alignment - Enable Multirow Action
        if (noReserv())
            performItem("CLOSE__");
        else
            mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANNOTCLOSE: Can not perform on selected line"));    
    }

    public void prePostHead()
    {
        ASPManager mgr = getASPManager();

        calling_url=mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url);

        if (itemlay2.isMultirowLayout())
            itemset2.store();
        else
        {
            itemset2.unselectRows();
            itemset2.selectRow();
        }

        trans.clear();

        cmd = trans.addCustomCommand("ALLOCODEPART","Pre_Accounting_API.Get_Allowed_Codeparts");
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
        cmd.addParameter("STR_CODE","M50"); 
        cmd.addParameter("CONTROL_TYPE",""); 
        cmd.addParameter("COMPANY",headset.getRow().getValue("COMPANY")); 

        trans = mgr.perform(trans);

        code_a = trans.getValue("ALLOCODEPART/DATA/CODE_A");
        code_b = trans.getValue("ALLOCODEPART/DATA/CODE_B");
        code_c = trans.getValue("ALLOCODEPART/DATA/CODE_C");
        code_d = trans.getValue("ALLOCODEPART/DATA/CODE_D");
        code_e = trans.getValue("ALLOCODEPART/DATA/CODE_E");
        code_f = trans.getValue("ALLOCODEPART/DATA/CODE_F");
        code_g = trans.getValue("ALLOCODEPART/DATA/CODE_G");
        code_h = trans.getValue("ALLOCODEPART/DATA/CODE_H");
        code_i = trans.getValue("ALLOCODEPART/DATA/CODE_I");
        code_j = trans.getValue("ALLOCODEPART/DATA/CODE_J");

        nPreAccId = itemset2.getRow().getValue("NPREACCOUNTINGID");
        item2Contract = itemset2.getRow().getFieldValue("ITEM2_CONTRACT");

        prePostBuffer = mgr.newASPBuffer();
        data = prePostBuffer.addBuffer("dataBuffer");
        data.addItem("CONTRACT",item2Contract);
        data.addItem("PRE_ACCOUNTING_ID",nPreAccId);
        data.addItem("ENABL0",code_a);
        data.addItem("ENABL1",code_b);
        data.addItem("ENABL2",code_c);
        data.addItem("ENABL3",code_d);
        data.addItem("ENABL4",code_e);
        data.addItem("ENABL5",code_f);
        data.addItem("ENABL6",code_g);
        data.addItem("ENABL7",code_h);
        data.addItem("ENABL8",code_i);
        data.addItem("ENABL9",code_j);
        data.addItem("ENABL10","0");

        returnBuffer = prePostBuffer.addBuffer("return_buffer");
        returnBuffer.addItem("WO_NO",headset.getRow().getValue("WO_NO"));

        //Web Alignment - open RMBs in a new window
        bOpenNewWindow = true;
        urlString = createTransferUrl("../mpccow/PreAccountingDlg.page", prePostBuffer);
        newWinHandle = "prePostHead";
        //
    }

    public void printPicList()
    {
        ASPManager mgr = getASPManager();    

        //Web Alignment - Enable Multirow Action

        ASPBuffer print;
        ASPBuffer printBuff;
        String attr1;
        String attr2;
        String attr3;
        String attr4;

        if ( itemlay2.isMultirowLayout() )
          itemset2.goTo(itemset2.getRowSelected());
    
        String orderNo = itemset2.getRow().getValue("MAINT_MATERIAL_ORDER_NO");
        String orderList =  orderNo +";";

        trans.clear();

        cmd = trans.addCustomFunction("RESEXIST","MAINT_MATERIAL_REQUISITION_API.New_Assign_Exist","EXIST"); 
        cmd.addParameter("ORDER_LIST",orderList); 

        trans = mgr.perform(trans);

        
        String res_exist = trans.getValue("RESEXIST/DATA/EXIST");
	trans.clear();
	if ( !mgr.isEmpty(orderNo) )
	{
	    if ("1".equals(res_exist)) 
	    {

		attr1 = "REPORT_ID" + (char)31 + "MAINT_MATERIAL_REQUISITION_REP" + (char)30;
		attr2 = "MAINT_MTRL_ORDER_NO" + (char)31 + itemset2.getValue("MAINT_MATERIAL_ORDER_NO") + (char)30;
		attr3 =  "";
		attr4 =  "";

		cmd = trans.addCustomCommand("PRINTPICKLIST","Archive_API.New_Client_Report");
		cmd.addParameter("ATTR0");                       
		cmd.addParameter("ATTR1",attr1);       
		cmd.addParameter("ATTR2",attr2);              
		cmd.addParameter("ATTR3",attr3);      
		cmd.addParameter("ATTR4",attr4);  

		trans = mgr.perform(trans);

		String attr0 = trans.getValue("PRINTPICKLIST/DATA/ATTR0");

		print = mgr.newASPBuffer();

		printBuff = print.addBuffer("DATA");
		printBuff.addItem("RESULT_KEY", attr0);

		callPrintDlg(print,true);
	    }
	    else

		mgr.showAlert(mgr.translate("PCMWACTIVEROUNDNONEW: No new Assigned stock for this Material Order."));
	}
	else
	    mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANNOT: Can not perform on selected line"));
    }

    public void note()
    {
        ASPManager mgr = getASPManager();

        if (itemlay2.isMultirowLayout())
            itemset2.goTo(itemset2.getRowSelected());
        else
            itemset2.selectRow();

        //Web Alignment - simplify code for RMBs    
        bOpenNewWindow = true;
        urlString = "EditorDlg.page?SNOTETEXT="+mgr.URLEncode(itemset2.getRow().getValue("SNOTETEXT"))+"&FRMNAME=ActiveRound"+"&QRYSTR="+mgr.URLEncode(qrystr);
        newWinHandle = "note";
    }

    public void sparePartObject()
    {
        ASPManager mgr = getASPManager();

        if (itemlay2.isMultirowLayout())
            itemset2.goTo(itemset2.getRowSelected());
        else
            itemset2.selectRow();

        //Web Alignment - simplify code for RMBs
        bOpenNewWindow = true;
        urlString = "MaintenanceObject2.page?MCH_CODE="+mgr.URLEncode(itemset2.getRow().getValue("MCHCODE"))+"&CONTRACT="+mgr.URLEncode(itemset2.getRow().getValue("CONTRACT"))+"&WO_NO="+mgr.URLEncode(itemset2.getRow().getValue("WO_NO"))+"&ORDER_NO="+mgr.URLEncode(itemset.getRow().getFieldValue("ITEM0_MAINT_MATERIAL_ORDER_NO"));
        newWinHandle = "sparePart";
    }

    public void objStructure()
    {
        ASPManager mgr = getASPManager();

        if (itemlay2.isMultirowLayout())
            itemset2.goTo(itemset2.getRowSelected());
        else
            itemset2.selectRow();

        //Web Alignment - simplify code for RMBs
        bOpenNewWindow = true;
        urlString = "MaintenaceObject3.page?MCH_CODE="+mgr.URLEncode(itemset2.getRow().getValue("MCHCODE"))+"&CONTRACT="+mgr.URLEncode(itemset2.getRow().getValue("CONTRACT"))+"&ORDER_NO="+mgr.URLEncode(itemset.getRow().getFieldValue("ITEM0_MAINT_MATERIAL_ORDER_NO"));
        newWinHandle = "objStruct";
    }

    public void detchedPartList()
    {
        ASPManager mgr = getASPManager();
        String  sPartNo = "";
        int head_current = headset.getCurrentRowNo();   
        String s_head_curr = String.valueOf(head_current);

        int currrow = itemset2.getCurrentRowNo();

        String s_currrow = String.valueOf(currrow);
        ctx.setGlobal("CURRROWGLOBAL",s_currrow);
        ctx.setGlobal("WONOGLOBAL",s_head_curr);

        ASPBuffer buffer = mgr.newASPBuffer();
        ASPBuffer row = buffer.addBuffer("0");
        //Bug 82543, start
 //       if (itemset.countRows() > 0) {
        if (itemset.getRowSelected() != -1) {
            itemset.goTo(itemset.getRowSelected());
            sPartNo  = itemset.getRow().getValue("PART_NO");
        }
        if (!(mgr.isEmpty(sPartNo))) {
            trans.clear();
            cmd = trans.addCustomFunction("SPARESTRUCT","Equipment_Spare_Structure_API.Has_Spare_Structure","HASSPARESTRUCTURE");
            cmd.addParameter("PART_NO",itemset.getRow().getValue("PART_NO"));
            cmd.addParameter("CONTRACT",itemset.getRow().getValue("SPARE_CONTRACT"));
            trans = mgr.perform(trans);
            String hasSt = trans.getValue("SPARESTRUCT/DATA/HASSPARESTRUCTURE");
            if ("FALSE".equals(hasSt)) {
                mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANNOT: Can not perform on selected line"));
            }
            else{

                bOpenNewWindow = true;
                row.addItem("PART_NO",sPartNo);
                row.addItem("WO_NO",itemset2.getRow().getValue("WO_NO"));    
                row.addItem("FRAME","ActiveRound");
                row.addItem("QRYST",qrystr);
                row.addItem("ORDER_NO",itemset2.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));

                urlString = createTransferUrl("../equipw/EquipmentSpareStructure3.page", buffer);

                newWinHandle = "detachedPart"; 
            }
        }
        else {

            bOpenNewWindow = true;
            row.addItem("PART_NO",sPartNo);
            row.addItem("WO_NO",itemset2.getRow().getValue("WO_NO"));    
            row.addItem("FRAME","ActiveRound");
            row.addItem("QRYST",qrystr);
            row.addItem("ORDER_NO",itemset2.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));

            urlString = createTransferUrl("../equipw/EquipmentSpareStructure3.page", buffer);

            newWinHandle = "detachedPart"; 
        }
        //Bug 82543, end
    }

    //Web Alignment - Changes to suit multiRow action
    public void availDetail()
    {
        ASPManager mgr = getASPManager();
        String sProjectId;

        int count = 0;
        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;

        bOpenNewWindow = true;
        if (itemlay.isMultirowLayout())
        {
            itemset.storeSelections();
            itemset.setFilterOn();
            count = itemset.countSelectedRows();
        }
        else
        {
            itemset.unselectRows();
            count = 1;
        }

        trans.clear();

        //Bug 66456, Start, Added check on PROJ
        if (mgr.isModuleInstalled("PROJ"))
        {
            cmd = trans.addCustomFunction("GETPROJINV","MAINT_MAT_REQ_SUP_API.DECODE('PROJECT_INVENTORY')","SUPPLY_CODE");
    
            cmd = trans.addCustomFunction("GERPROJID","Activity_API.Get_Project_Id","PROJECT_ID");
            cmd.addParameter("ITEM_ACTIVITY_SEQ",headset.getRow().getValue("ACTIVITY_SEQ"));
    
            trans = mgr.perform(trans);
    
            if ((trans.getValue("GETPROJINV/DATA/SUPPLY_CODE")).equals(itemset.getValue("SUPPLY_CODE")))
                sProjectId = trans.getValue("GERPROJID/DATA/PROJECT_ID");
            else
                sProjectId = "*";
        }
        else
            sProjectId = "";
        //Bug 66456, End

        transferBuffer = mgr.newASPBuffer();

        for (int i = 0; i < count; i++)
        {
            rowBuff = mgr.newASPBuffer();

            if (i == 0)
            {
                rowBuff.addItem("PART_NO", itemset.getValue("PART_NO"));
                rowBuff.addItem("CONTRACT", itemset.getValue("SPARE_CONTRACT"));
                rowBuff.addItem("PROJECT_ID", sProjectId);
                rowBuff.addItem("CONFIGURATION_ID", "*");
            }
            else
            {
                rowBuff.addItem(null, itemset.getValue("PART_NO"));
                rowBuff.addItem(null, itemset.getValue("SPARE_CONTRACT"));
                rowBuff.addItem(null, sProjectId);
                rowBuff.addItem(null, "*");
            }

            transferBuffer.addBuffer("DATA", rowBuff);

            if (itemlay.isMultirowLayout())
                itemset.next();
        }

        if (itemlay.isMultirowLayout())
            itemset.setFilterOff();

        urlString = createTransferUrl("../invenw/InventoryPartAvailabilityPlanningQry.page", transferBuffer);
        newWinHandle = "QryInventPart"; 
    }
    //

    //Web Alignment - Changes to suit multiRow action
    public void supPerPart()
    {
        ASPManager mgr = getASPManager();

        int count = 0;
        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;

        bOpenNewWindow = true;
        if (itemlay.isMultirowLayout())
        {
            itemset.storeSelections();
            itemset.setFilterOn();
            count = itemset.countSelectedRows();
        }
        else
        {
            itemset.unselectRows();
            count = 1;
        }

        transferBuffer = mgr.newASPBuffer();

        for (int i = 0; i < count; i++)
        {
            rowBuff = mgr.newASPBuffer();

            if (i == 0)
            {
                rowBuff.addItem("PART_NO", itemset.getValue("PART_NO"));
            }
            else
            {
                rowBuff.addItem(null, itemset.getValue("PART_NO"));
            }

            transferBuffer.addBuffer("DATA", rowBuff);

            if (itemlay.isMultirowLayout())
                itemset.next();
        }

        if (itemlay.isMultirowLayout())
            itemset.setFilterOff();

        urlString = createTransferUrl("../purchw/PurchasePartSupplier.page", transferBuffer);
        newWinHandle = "SupPerPart";
    }
    //

    public void issue()
    {
       ASPManager mgr = getASPManager();
       double qtyOnHand = 0.0;
       isSecure = new String[15];
       double nQtyAvblToIssue = 0.0;
       double nTotQtyRes = 0.0;
       double nTotQtyPlanable = 0.0;
       double nCount = 0.0;
       int count = 0;
       int successCount = 0;
       ASPTransactionBuffer transForIssue;

       int currrow = itemset2.getCurrentRowNo();

       trans.clear();

       if (itemlay.isMultirowLayout())
       {
          itemset.storeSelections();
          itemset.setFilterOn();
          count = itemset.countSelectedRows();
       }
       else
       {
          itemset.unselectRows();
          count = 1;
       }

          trans.clear();
          cmd = trans.addCustomFunction("FINSTATEDEC","MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__","DB_STATE"); 
          cmd.addParameter("DB_STATE","Released"); 

          cmd = trans.addCustomFunction("ISSALLOW","MAINT_MATERIAL_REQ_LINE_API.Is_Issued_Allowed","ISS_ALLO");
          cmd.addParameter("WO_NO",itemset.getValue("WO_NO"));

          trans = mgr.perform(trans);
          String sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DB_STATE");
          String bIssAllowed = trans.getValue("ISSALLOW/DATA/ISS_ALLO");
          String dfStatus = itemset2.getValue("STATE");
          if (!("TRUE".equals(bIssAllowed)))
          {
             mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANNOTSTATWO5WO: Work order status not valid for material issue."));
             return ;
          }

          if (!(sStatusCodeReleased.equals(dfStatus)))
          {
             mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANNOTSTAT1: Maint Material Requisition status not valid for material issue."));
             return ;
          }

          itemset.first();
          transForIssue = mgr.newASPTransactionBuffer();

          for (int i = 0; i < count; i++)
          {
                 qtyOnHand = GetInventoryQuantity("ONHAND");;
                 double nRes = GetInventoryQuantity("RESERVED");
                 double qty_assign = itemset.getRow().getNumberValue("QTY_ASSIGNED");
                 if ( isNaN(qty_assign) )
                        qty_assign = 0;
    
                 double nAvailToIss = qtyOnHand - nRes + qty_assign;
                 String sAvailToIss = mgr.getASPField("QTY_ASSIGNED").formatNumber(nAvailToIss); 

                 double plan_qty1 = itemset.getNumberValue("PLAN_QTY");
                 if (isNaN(plan_qty1))
                    plan_qty1 = 0;

                 double qty1 = itemset.getNumberValue("QTY");
                 if (isNaN(qty1))
                    qty1 = 0;

                 double qty_outstanding = plan_qty1 - qty1; //(qty + qty_return); * ASSALK  Material Issue & Reserve modification.

                 if ( qty_outstanding > 0 )
                    canPerform = true;

                 if (!canPerform){
                     mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANNOTMAT1: No material requirements for selected item."));
                     return;
                 }
                 else
                 {
                    trans.clear();

                    if (plan_qty1 > nAvailToIss)
                        mgr.showAlert(mgr.translate("PCMWACTIVEROUNDAVAIL: Available quantity for part ") +itemset.getRow().getValue("PART_NO") + mgr.translate("PCMWACTIVESEPARATE2AVAIL1:  was ")  +sAvailToIss+ mgr.translate("PCMWACTIVESEPARATE2AVAI2: .")); 

                    if ( checksec("Inventory_Part_In_Stock_API.Get_Sales_Plannable_Qty_Res",2,isSecure) )
                    {
                       cmd = transForIssue.addCustomFunction("INVONHANDRES"+successCount,"Inventory_Part_In_Stock_API.Get_Sales_Plannable_Qty_Res","QTYRES");
                       cmd.addParameter("CONTRACT",itemset.getValue("SPARE_CONTRACT"));
                       cmd.addParameter("PART_NO",itemset.getValue("PART_NO"));
                       cmd.addParameter("CONFIGURATION","");
                    }
                    if ( checksec("Inventory_Part_In_Stock_API.Get_Sales_Plannable_Qty_Onhand",3,isSecure) )
                    {
                       cmd = transForIssue.addCustomFunction("INVONHANDPLAN"+successCount,"Inventory_Part_In_Stock_API.Get_Sales_Plannable_Qty_Onhand","QTYPLANNABLE");
                       cmd.addParameter("CONTRACT",itemset.getValue("SPARE_CONTRACT"));
                       cmd.addParameter("PART_NO",itemset.getValue("PART_NO"));
                       cmd.addParameter("CONFIGURATION","");
                    }
                    cmd = transForIssue.addCustomFunction("AUTOREP"+ successCount,"MAINTENANCE_INV_PART_API.Get_Auto_Repairable","AUTO_REPAIRABLE");
                    cmd.addParameter("ITEM2_CONTRACT",itemset2.getValue("CONTRACT"));
                    cmd.addParameter("PART_NO",itemset.getValue("PART_NO"));

                    cmd = transForIssue.addCustomFunction("REPAIR"+ successCount,"MAINTENANCE_INV_PART_API.Get_Repairable","REPAIRABLE");
                    cmd.addParameter("SPARE_CONTRACT",itemset.getValue("SPARE_CONTRACT"));
                    cmd.addParameter("PART_NO",itemset.getValue("PART_NO"));

                    //Bug 56688, Replaced Make_Issue_Detail with Make_Auto_Issue_Detail.
                    cmd = transForIssue.addCustomCommand("MAKEISSUDETA" + successCount,"MAINT_MATERIAL_REQ_LINE_API.Make_Auto_Issue_Detail");
                    cmd.addParameter("DUMMY_ACT_QTY_ISSUED");
                    cmd.addParameter("ITEM0_MAINT_MATERIAL_ORDER_NO",itemset.getRow().getFieldValue("ITEM0_MAINT_MATERIAL_ORDER_NO"));
                    cmd.addParameter("LINE_ITEM_NO",itemset.getRow().getValue("LINE_ITEM_NO"));
                    cmd.addParameter("LOCATION_NO","");    
                    cmd.addParameter("LOT_BATCH_NO","");
                    cmd.addParameter("SERIAL_NO","");
                    cmd.addParameter("ENG_CHG_LEVEL","");
                    cmd.addParameter("WAIV_DEV_REJ_NO","");
                    cmd.addParameter("PROJECT_NO","");
                    cmd.addParameter("ACTIVITY_SEQ","");
                    cmd.addParameter("QTY_TO_ISSUE","");
                    //Bug 56688, End

                    successCount++;

                 }

                 if (itemlay.isMultirowLayout())
                    itemset.next();
                 trans.clear();
             }


             trans = mgr.perform(transForIssue);

             itemset.first();

             for (int i = 0; i < successCount; i++)
             {
         		//Bug 76767, Start  
         		double nQtyIssued = trans.getNumberValue("MAKEISSUDETA" + i + "/DATA/DUMMY_ACT_QTY_ISSUED");
         		double nQtyShort = itemset.getNumberValue("PLAN_QTY")	- nQtyIssued;

         		if (nQtyShort > 0)
         		   mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANTISSUE: All material could not be issued for part &1. Remaining quantity to be issued: &2", itemset.getValue("PART_NO"), new Double(nQtyShort).toString()));
         		//Bug 76767, End

                 String isAutoRepairable = trans.getValue("AUTOREP"+i+"/DATA/AUTO_REPAIRABLE");
                 String isRepairable = trans.getValue("REPAIR"+i+"/DATA/REPAIRABLE");
                 if ( "true".equals(isSecure[2]) )
                    nTotQtyRes = trans.getNumberValue("INVONHANDRES"+i+"/DATA/QTYRES");
                 else
                    nTotQtyRes = 0;

                 if ( isNaN(nTotQtyRes) )
                    nTotQtyRes = 0;
                 if ( "true".equals(isSecure[3]) )
                    nTotQtyPlanable = trans.getNumberValue("INVONHANDPLAN"+i+"/DATA/QTYPLANNABLE");
                 else
                    nTotQtyPlanable = 0;

                 if ( isNaN(nTotQtyPlanable) )
                    nTotQtyPlanable = 0;

                 if (( "TRUE".equals(isAutoRepairable) ) &&  ( "TRUE".equals(isRepairable) ) )
                 {
                     double plan_qty = itemset.getNumberValue("PLAN_QTY");
                     if (isNaN(plan_qty))
                        plan_qty = 0;

                     double qty = itemset.getNumberValue("QTY");
                     if (isNaN(qty))
                        qty = 0;

                     double qty_assign = itemset.getNumberValue("QTY_ASSIGNED");
                     if ( isNaN(qty_assign) )
                        qty_assign = 0;

                    double nQtyPlanToIssue = (plan_qty - qty);
                    double nAvablQty =  (nTotQtyPlanable - nTotQtyRes);
                    openCreRepNonSer = "FALSE";
                    if (qty_assign==0)
                    {
                       if (nQtyPlanToIssue <= nAvablQty)
                       {
                          openCreRepNonSer = "TRUE";
                          nCount = nQtyPlanToIssue;
                       }
                       else
                       {
                          openCreRepNonSer = "TRUE";
                          nCount = nAvablQty;
                       }

                    }
                    else if (qty_assign > 0 )
                    {

                       if (qty_assign == plan_qty)
                       {
                          openCreRepNonSer = "TRUE";
                          nCount = qty_assign;
                       }
                       else if ((qty_assign < plan_qty))
                       {
                          if ((nAvablQty == 0))
                          {
                             openCreRepNonSer = "TRUE";
                             nCount = qty_assign;
                          }
                          else if ((nAvablQty >= (plan_qty - qty_assign)))
                          {
                             openCreRepNonSer = "TRUE";
                             nCount = plan_qty;
                          }
                          else if ((nAvablQty < (plan_qty - qty_assign)))
                          {
                             openCreRepNonSer = "TRUE";
                             nCount = (qty_assign + nAvablQty);
                          }
                       }
                    }
                    if (nCount == 0)
                    {
                       openCreRepNonSer = "FALSE";
                    }
                    creRepNonSerPath = "CreateRepairWorkOrderForNonSerialParts.page?WO_NO="+mgr.URLEncode(headset.getValue("WO_NO"))+
                                       "&PART_NO="+mgr.URLEncode(itemset.getValue("PART_NO"))+
                                       "&SPAREDESCRIPTION="+mgr.URLEncode(itemset.getValue("SPAREDESCRIPTION"))+
                                       "&SPARE_CONTRACT="+mgr.URLEncode(itemset.getValue("SPARE_CONTRACT"))+
                                       "&COUNT="+nCount;
                 }
                 if (itemlay.isMultirowLayout())
                       itemset.next();
             }


         if (itemlay.isMultirowLayout())
            itemset.setFilterOff();

         itemset2.goTo(currrow);
         okFindITEM5();

    }

    public void issueFromInvent()
    {
        ASPManager mgr = getASPManager();
        double qtyOnHand = 0.0;
        isSecure = new String[15];
        double nQtyAvblToIssue = 0.0;
        double nTotQtyRes = 0.0;
        double nTotQtyPlanable = 0.0;
        double nCount = 0.0;

        currrow = itemset2.getCurrentRowNo();

        if (itemlay.isMultirowLayout())
            itemset.goTo(itemset.getRowSelected());

        currrowItem = itemset.getCurrentRowNo();   

        trans.clear();

        cmd = trans.addCustomFunction("FINSTATEDEC","MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__","DB_STATE"); 
        cmd.addParameter("DB_STATE","Released"); 

        cmd = trans.addCustomFunction("ISSALLOW","MAINT_MATERIAL_REQ_LINE_API.Is_Issued_Allowed","ISS_ALLO");
        cmd.addParameter("WO_NO",itemset.getRow().getFieldValue("ITEM_WO_NO"));

        cmd = trans.addCustomFunction("MAINTREQSUP","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","SUPPLY_CODE");

        trans = mgr.perform(trans);

        sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DB_STATE");
        bIssAllowed = trans.getValue("ISSALLOW/DATA/ISS_ALLO");
        dfStatus = itemset2.getRow().getFieldValue("ITEM2_STATE");
        String sStandardInv = trans.getValue("MAINTREQSUP/DATA/SUPPLY_CODE");

        if (!("TRUE".equals(bIssAllowed)))
        {
            mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANNOTSTATWO5WO: Work order status not valid for material issue."));
            return ;
        }

        if (!(sStatusCodeReleased.equals(dfStatus)))
        {
            mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANNOTSTAT1: Maint Material Requisition status not valid for material issue."));
            return ;
        }

        trans.clear();

        cmd = trans.addCustomFunction("AUTOREP","MAINTENANCE_INV_PART_API.Get_Auto_Repairable","AUTO_REPAIRABLE");
        cmd.addParameter("SPARE_CONTRACT",itemset.getRow().getValue("SPARE_CONTRACT"));
        cmd.addParameter("PART_NO",itemset.getRow().getValue("PART_NO"));

        cmd = trans.addCustomFunction("REPAIR","MAINTENANCE_INV_PART_API.Get_Repairable","REPAIRABLE");
        cmd.addParameter("SPARE_CONTRACT",itemset.getRow().getValue("SPARE_CONTRACT"));
        cmd.addParameter("PART_NO",itemset.getRow().getValue("PART_NO"));

        cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
        cmd.addParameter("PART_OWNERSHIP",itemset.getRow().getValue("PART_OWNERSHIP"));

        //if ( checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",1,isSecure) )
        //{

        ///}

        if ( checksec("Inventory_Part_In_Stock_API.Get_Sales_Plannable_Qty_Res",2,isSecure) )
        {
            cmd = trans.addCustomFunction("INVONHANDRES","Inventory_Part_In_Stock_API.Get_Sales_Plannable_Qty_Res","QTYRES");
            cmd.addParameter("CONTRACT",itemset.getRow().getFieldValue("SPARE_CONTRACT"));
            cmd.addParameter("PART_NO",itemset.getRow().getValue("PART_NO"));
            cmd.addParameter("CONFIGURATION","");
        }
        if ( checksec("Inventory_Part_In_Stock_API.Get_Sales_Plannable_Qty_Onhand",3,isSecure) )
        {
            cmd = trans.addCustomFunction("INVONHANDPLAN","Inventory_Part_In_Stock_API.Get_Sales_Plannable_Qty_Onhand","QTYPLANNABLE");
            cmd.addParameter("CONTRACT",itemset.getRow().getFieldValue("SPARE_CONTRACT"));
            cmd.addParameter("PART_NO",itemset.getRow().getValue("PART_NO"));
            cmd.addParameter("CONFIGURATION","");
        }

        trans = mgr.perform(trans);
        isAutoRepairable = trans.getValue("AUTOREP/DATA/AUTO_REPAIRABLE");
        isRepairable = trans.getValue("REPAIR/DATA/REPAIRABLE");

        nQtyAvblToIssue = qtyOnHand;

        if ( "true".equals(isSecure[2]) )
            nTotQtyRes = trans.getNumberValue("INVONHANDRES/DATA/QTYRES");
        else
            nTotQtyRes = 0;

        if ( isNaN(nTotQtyRes) )
            nTotQtyRes = 0;

        if ( "true".equals(isSecure[3]) )
            nTotQtyPlanable = trans.getNumberValue("INVONHANDPLAN/DATA/QTYPLANNABLE");
        else
            nTotQtyPlanable = 0;

        if ( isNaN(nTotQtyPlanable) )
            nTotQtyPlanable = 0;

        double plan_qty=itemset.getRow().getNumberValue("PLAN_QTY");
        if (isNaN(plan_qty))
            plan_qty=0;
        double item_qty=itemset.getRow().getNumberValue("ITEM_QTY");
        if (isNaN(item_qty))
            item_qty=0;

        if (plan_qty > item_qty)
            canPerform = true;

        double qty_assign = itemset.getRow().getNumberValue("QTY_ASSIGNED");
        if ( isNaN(qty_assign) )
            qty_assign = 0;

        double qty_on = itemset.getRow().getNumberValue("QTYONHAND");
        if ( isNaN(qty_on) )
            qty_on = 0;if (!canPerform)
            mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANNOTMAT1: No material requirements for selected item."));
        else
        {
            qtyOnHand = GetInventoryQuantity("ONHAND");;
            double nRes = GetInventoryQuantity("RESERVED");
            double nAvailToIss = qtyOnHand - nRes + qty_assign;
            String sAvailToIss = mgr.getASPField("QTY_ASSIGNED").formatNumber(nAvailToIss);

            trans.clear();
            cmd = trans.addCustomCommand("MAKEISSUDETA","MAINT_MATERIAL_REQ_LINE_API.Make_Auto_Issue_Detail");
            cmd.addParameter("DUMMY_ACT_QTY_ISSUED");
            cmd.addParameter("ITEM0_MAINT_MATERIAL_ORDER_NO",itemset.getRow().getFieldValue("ITEM0_MAINT_MATERIAL_ORDER_NO"));
            cmd.addParameter("LINE_ITEM_NO",itemset.getRow().getValue("LINE_ITEM_NO"));
            cmd.addParameter("LOCATION_NO","");    
            cmd.addParameter("LOT_BATCH_NO","");
            cmd.addParameter("SERIAL_NO","");
            cmd.addParameter("ENG_CHG_LEVEL","");
            cmd.addParameter("WAIV_DEV_REJ_NO","");
            cmd.addParameter("PROJECT_NO","");
            cmd.addParameter("ACTIVITY_SEQ","");
            cmd.addParameter("QTY_TO_ISSUE","");

            trans = mgr.perform(trans);

            if (plan_qty > nAvailToIss)
                mgr.showAlert(mgr.translate("PCMWACTIVEROUNDAVAIL: Available quantity for part ") +itemset.getRow().getValue("PART_NO") + mgr.translate("PCMWACTIVESEPARATE2AVAIL1:  was ")  +sAvailToIss+ mgr.translate("PCMWACTIVESEPARATE2AVAI2: ."));

            if (( "TRUE".equals(isAutoRepairable) ) &&  ( "TRUE".equals(isRepairable) ) &&  ( "NOT SERIAL TRACKING".equals(hasSerialNum) ))
            {
                double nQtyPlanToIssue = (plan_qty - item_qty);
                double nAvablQty =  (nTotQtyPlanable - nTotQtyRes);
                openCreRepNonSer = "FALSE";
                if (qty_assign==0)
                {
                    if (nQtyPlanToIssue <= nAvablQty)
                    {
                        openCreRepNonSer = "TRUE";
                        nCount = nQtyPlanToIssue;
                    }
                    else
                    {
                        openCreRepNonSer = "TRUE";
                        nCount = nAvablQty;
                    }

                }
                else if (qty_assign > 0 )
                {

                    if (qty_assign == plan_qty)
                    {
                        openCreRepNonSer = "TRUE";
                        nCount = qty_assign;
                    }
                    else if ((qty_assign < plan_qty))
                    {
                        if ((nAvablQty == 0))
                        {
                            openCreRepNonSer = "TRUE";
                            nCount = qty_assign;
                        }
                        else if ((nAvablQty >= (plan_qty - qty_assign)))
                        {
                            openCreRepNonSer = "TRUE";
                            nCount = plan_qty;
                        }
                        else if ((nAvablQty < (plan_qty - qty_assign)))
                        {
                            openCreRepNonSer = "TRUE";
                            nCount = (qty_assign + nAvablQty);
                        }
                    }
                }
                if (nCount == 0)
                {
                    openCreRepNonSer = "FALSE";
                }
                creRepNonSerPath = "CreateRepairWorkOrderForNonSerialParts.page?WO_NO="+mgr.URLEncode(headset.getValue("WO_NO"))+
                                   "&PART_NO="+mgr.URLEncode(itemset.getRow().getValue("PART_NO"))+
                                   "&SPAREDESCRIPTION="+mgr.URLEncode(itemset.getRow().getValue("SPAREDESCRIPTION"))+
                                   "&SPARE_CONTRACT="+mgr.URLEncode(itemset.getRow().getValue("SPARE_CONTRACT"))+
                                   "&COUNT="+nCount;
            }

            itemset2.goTo(currrow);
            //okFindITEM5();
            itemset.goTo(currrowItem);
            itemset.refreshRow();
        }
    }

    public void manIssue()
    {
        ASPManager mgr = getASPManager();

        currrow = itemset2.getCurrentRowNo();

        if (itemlay.isMultirowLayout())
            itemset.goTo(itemset.getRowSelected());

        trans.clear();

        cmd = trans.addCustomFunction("FINSTATEDEC","MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__","DB_STATE"); 
        cmd.addParameter("DB_STATE","Released"); 

        cmd = trans.addCustomFunction("SERIALTRA","PART_CATALOG_API.Get_Serial_Tracking_Code_Db","SERIAL_TRACK");
        cmd.addParameter("PART_NO",itemset.getRow().getValue("PART_NO"));

        cmd = trans.addCustomFunction("AUTOREP","MAINTENANCE_INV_PART_API.Get_Auto_Repairable","AUTO_REPAIRABLE");
        cmd.addParameter("SPARE_CONTRACT",itemset.getRow().getValue("SPARE_CONTRACT"));
        cmd.addParameter("PART_NO",itemset.getRow().getValue("PART_NO"));

        cmd = trans.addCustomFunction("REPAIR","MAINTENANCE_INV_PART_API.Get_Repairable","REPAIRABLE");
        cmd.addParameter("SPARE_CONTRACT",itemset.getRow().getValue("SPARE_CONTRACT"));
        cmd.addParameter("PART_NO",itemset.getRow().getValue("PART_NO"));

        cmd = trans.addCustomFunction("ISSALLOW","MAINT_MATERIAL_REQ_LINE_API.Is_Issued_Allowed","ISS_ALLO");
        cmd.addParameter("WO_NO",itemset.getRow().getFieldValue("ITEM_WO_NO"));

        trans = mgr.perform(trans);

        sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DB_STATE");
        hasSerialNum = trans.getValue("SERIALTRA/DATA/SERIAL_TRACK");
        isAutoRepairable = trans.getValue("AUTOREP/DATA/AUTO_REPAIRABLE");
        isRepairable = trans.getValue("REPAIR/DATA/REPAIRABLE");
        bIssAllowed = trans.getValue("ISSALLOW/DATA/ISS_ALLO");
        dfStatus = itemset2.getRow().getFieldValue("ITEM2_STATE");

        if (!("TRUE".equals(bIssAllowed)))
        {
            mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANNOTSTATWO5WO: Work order status not valid for material issue."));
            return ;
        }

        if (!(sStatusCodeReleased.equals(dfStatus)))
        {
            mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANNOTSTAT1: Maint Material Requisition status not valid for material issue."));
            return ;
        }

        double plan_qty=itemset.getRow().getNumberValue("PLAN_QTY");
        if (isNaN(plan_qty))
            plan_qty=0;
        double item_qty=itemset.getRow().getNumberValue("ITEM_QTY");   
        if (isNaN(item_qty))
            item_qty=0;
        if (plan_qty > item_qty)
        {
            plan_qty=itemset.getRow().getNumberValue("PLAN_QTY");
            if (isNaN(plan_qty))
                plan_qty=0;
            item_qty=itemset.getRow().getNumberValue("ITEM_QTY");
            if (isNaN(item_qty))
                item_qty=0;
            double nQtyLeftNum = plan_qty - item_qty;

            if (nQtyLeftNum < 0)
                nQtyLeft = "0";
            else
                nQtyLeft    = String.valueOf(nQtyLeftNum);

            if (( "TRUE".equals(isAutoRepairable) ) &&  ( "TRUE".equals(isRepairable) ) &&  ( "NOT SERIAL TRACKING".equals(hasSerialNum) ))
                creRepWO = "TRUE";

            ctx.setGlobal("CURRROWGLOBAL",String.valueOf(currrow));

            bOpenNewWindow = true;
            urlString = "InventoryPartLocationDlg.page?PART_NO="+mgr.URLEncode(itemset.getRow().getValue("PART_NO"))+"&CONTRACT="+mgr.URLEncode(itemset.getRow().getValue("SPARE_CONTRACT"))+"&FRMNAME=ActiveRound"+"&QRYSTR="+mgr.URLEncode(qrystr)+"&WO_NO="+ mgr.URLEncode(itemset.getRow().getValue("WO_NO"))+"&LINE_ITEM_NO="+mgr.URLEncode(itemset.getRow().getValue("LINE_ITEM_NO"))+"&DESCRIPTION="+mgr.URLEncode(itemset.getRow().getValue("SPAREDESCRIPTION"))+"&QTYLEFT="+mgr.URLEncode(nQtyLeft)+"&MAINTMATORDNO="+mgr.URLEncode(itemset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"))+"&CREREPWO="+mgr.URLEncode(creRepWO);
            newWinHandle = "manIsssue";


        }
        else
            mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANNOTMAT: No material requirements for selected item."));

        itemset2.goTo(currrow); 
    }


    public void reserve()
    {
       ASPManager mgr = getASPManager();
       ASPTransactionBuffer transForReserve;

       int currrow = itemset2.getCurrentRowNo();
       int count = 0;
       int successCount = 0;

       String sStatusCodeReleased;
       String dfStatus;

       double nQtyShort;

       trans.clear();

       if (itemlay.isMultirowLayout())
       {
          itemset.storeSelections();
          itemset.setFilterOn();
          count = itemset.countSelectedRows();
       }
       else
       {
          itemset.unselectRows();
          count = 1;
       }

       cmd = trans.addCustomFunction("FINSTATEDEC", "MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__", "DB_STATE"); 
       cmd.addParameter("DB_STATE", "Released"); 

       cmd = trans.addCustomFunction("RESALLOW", "MAINT_MATERIAL_REQ_LINE_API.Is_Reservation_Allowed", "RES_ALLO");
       cmd.addParameter("WO_NO",itemset.getValue("WO_NO"));

       trans = mgr.perform(trans);

       sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DB_STATE");
       dfStatus = itemset2.getValue("STATE");

       if (!(sStatusCodeReleased.equals(dfStatus)))
       {
          mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANNOTSTAT2: Maint Material Requisition status not valid for material reserve."));
          return ;
       }

       if (!("TRUE".equals(trans.getValue("RESALLOW/DATA/RES_ALLO"))))
       {
          mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANNOTSTATWO2: Work order status not valid for material reserve."));
          return;
       }

       transForReserve = mgr.newASPTransactionBuffer();

       for (int i = 0; i < count; i++)
       {

          if (itemset.getNumberValue("PLAN_QTY") <= (itemset.getNumberValue("QTY") + itemset.getNumberValue("QTY_ASSIGNED"))) // + itemset.getNumberValue("QTY_RETURNED"))) * ASSALK  Material Issue & Reserve modification.
          {
             mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANNOTMAT3: No material requirements for selected item."));
             return;
          }

          double planQty = itemset.getRow().getNumberValue("PLAN_QTY");
          if ( isNaN( planQty) )
             planQty = 0;

          double qtyOnHand = GetInventoryQuantity("ONHAND");
          double nRes = GetInventoryQuantity("RESERVED");
          double nAvalToRes =  qtyOnHand - nRes;
          String sAvalToRes = mgr.getASPField("QTY_ASSIGNED").formatNumber(nAvalToRes);

          cmd = transForReserve.addCustomCommand("RESSHORT_" + successCount,"MAINT_MATERIAL_REQ_LINE_API.Make_Reservation_Short");
          cmd.addParameter("QTY_LEFT");
          cmd.addParameter("ITEM0_MAINT_MATERIAL_ORDER_NO",itemset.getRow().getFieldValue("ITEM0_MAINT_MATERIAL_ORDER_NO"));
          cmd.addParameter("LINE_ITEM_NO",itemset.getRow().getValue("LINE_ITEM_NO"));
          cmd.addParameter("LOCATION_NO","");    
          cmd.addParameter("LOT_BATCH_NO","");
          cmd.addParameter("SERIAL_NO","");
          cmd.addParameter("ENG_CHG_LEVEL","");
          cmd.addParameter("WAIV_DEV_REJ_NO","");
          cmd.addParameter("ACTIVITY_SEQ","");
          cmd.addParameter("PROJECT_NO","");
          cmd.addParameter("QTY_TO_ASSIGN","");

          successCount++;

          if (itemlay.isMultirowLayout())
             itemset.next();
          if (planQty > nAvalToRes)
          {
             mgr.showAlert(mgr.translate("PCMWACTIVEROUNDAVAIL: Available quantity for part ") +itemset.getRow().getValue("PART_NO") + mgr.translate("PCMWACTIVESEPARATE2AVAIL1:  was ")  +sAvalToRes+ mgr.translate("PCMWACTIVESEPARATE2AVAI2: .")); 
          }
          trans.clear();
       }
       trans = mgr.perform(transForReserve);

       itemset.first();

       for (int i = 0; i <= successCount; i++)
       {
          nQtyShort = trans.getNumberValue("RESSHORT_" + i + "/DATA/QTY_LEFT");

          //Bug 76767, Start, Modified the error message  
          if (nQtyShort > 0)
        	 mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCOULDNOTALL: All material could not be allocated for part &1. Remaining quantity to be reserved: &2", itemset.getValue("PART_NO"), new Double(nQtyShort).toString()));
          //Bug 76767, End  

          if (itemlay.isMultirowLayout())
             itemset.next();
       }

       if (itemlay.isMultirowLayout())
          itemset.setFilterOff();

       itemset2.goTo(currrow);
       okFindITEM5();
    }

    public void manReserve()
    {
        ASPManager mgr = getASPManager();

        currrow = itemset2.getCurrentRowNo();

        if (itemlay.isMultirowLayout())
            itemset.goTo(itemset.getRowSelected());

        trans.clear();

        cmd = trans.addCustomFunction("FINSTATEDEC","MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__","DB_STATE"); 
        cmd.addParameter("DB_STATE","Released"); 

        cmd = trans.addCustomFunction("RESALLOW","MAINT_MATERIAL_REQ_LINE_API.Is_Reservation_Allowed","RES_ALLO");
        cmd.addParameter("WO_NO",itemset.getRow().getFieldValue("ITEM_WO_NO"));

        trans = mgr.perform(trans);

        sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DB_STATE");
        bResAllowed = trans.getValue("RESALLOW/DATA/RES_ALLO");  
        dfStatus = itemset2.getRow().getFieldValue("ITEM2_STATE");

        if (!("TRUE".equals(bResAllowed)))
        {
            mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANNOTSTATWO5: Work order status not valid for material reserve."));
            return ;
        }

        if (!(sStatusCodeReleased.equals(dfStatus)))
        {
            mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANNOTSTAT5: Maint Material Requisition status not valid for material manual reserve."));
            return ;
        }

        double plan_qty=itemset.getRow().getNumberValue("PLAN_QTY");
        if (isNaN(plan_qty))
            plan_qty=0;
        double item_qty=itemset.getRow().getNumberValue("ITEM_QTY");
        if (isNaN(item_qty))
            item_qty=0;
        double qty_ass=itemset.getRow().getNumberValue("QTY_ASSIGNED");
        if (isNaN(qty_ass))
            qty_ass=0;

        if (plan_qty > (item_qty + qty_ass))
        {

            plan_qty=itemset.getRow().getNumberValue("PLAN_QTY");
            if (isNaN(plan_qty))
                plan_qty=0;
            item_qty=itemset.getRow().getNumberValue("ITEM_QTY");
            if (isNaN(item_qty))
                item_qty=0;
            qty_ass=itemset.getRow().getNumberValue("QTY_ASSIGNED");
            if (isNaN(qty_ass))
                qty_ass=0;
            double qty_ret=itemset.getRow().getNumberValue("QTY_RETURNED");
            if (isNaN(qty_ret))
                qty_ret=0;
            double nQtyLeftNum = plan_qty - item_qty - qty_ass - qty_ret;

            if (nQtyLeftNum < 0)
                nQtyLeft = "0";
            else
                nQtyLeft    = String.valueOf(nQtyLeftNum);

            ctx.setGlobal("CURRROWGLOBAL",String.valueOf(currrow));

            qrystr += "&WO_NO=" + headset.getValue("WO_NO");

            bOpenNewWindow = true;
            /*
            urlString = "MaterialRequisReservatDlg.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"))+"&LINE_ITEM_NO="+mgr.URLEncode(itemset.getRow().getValue("LINE_ITEM_NO"))+"&FRMNAME=ActiveRound"+"&QRYSTR="+mgr.URLEncode(qrystr)+"&PART_NO="+mgr.URLEncode(itemset.getRow().getValue("PART_NO"))+"&CONTRACT="+mgr.URLEncode(itemset.getRow().getValue("SPARE_CONTRACT"))+"&DESCRIPTION="+mgr.URLEncode(itemset.getRow().getValue("SPAREDESCRIPTION"))+"&QTYLEFT="+mgr.URLEncode(nQtyLeft)+"&MAINTMATORDNO="+mgr.URLEncode(itemset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"))+
                        "&OWNERSHIP=" + mgr.URLEncode(itemset.getRow().getValue("PART_OWNERSHIP")) +
                        "&OWNER=" + mgr.URLEncode(itemset.getRow().getValue("OWNER")) +
                        "&OWNERNAME=" + mgr.URLEncode(itemset.getRow().getValue("OWNER_NAME")) +
                        "&CONDITION_CODE=" + mgr.URLEncode(itemset.getRow().getValue("CONDITION_CODE"));    */

            urlString = "MaterialRequisReservatDlg.page?PART_NO="+mgr.URLEncode(itemset.getRow().getValue("PART_NO"))+"&CONTRACT="+mgr.URLEncode(itemset.getRow().getValue("SPARE_CONTRACT"))+"&FRMNAME=ActiveRound"+"&QRYSTR="+mgr.URLEncode(qrystr)+"&WO_NO="+ mgr.URLEncode(itemset.getRow().getValue("WO_NO"))+"&LINE_ITEM_NO="+mgr.URLEncode(itemset.getRow().getValue("LINE_ITEM_NO"))+"&DESCRIPTION="+mgr.URLEncode(itemset.getRow().getValue("SPAREDESCRIPTION"))+"&QTYLEFT="+mgr.URLEncode(nQtyLeft)+"&MAINTMATORDNO="+mgr.URLEncode(itemset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"))+"&CREREPWO="+mgr.URLEncode(creRepWO);
            newWinHandle = "manReserve";

        }
        else
            mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANNOTMAT: No material requirements for selected item."));

        itemset2.goTo(currrow); 
    }

    public void unreserve()
    {
        ASPManager mgr = getASPManager();

        currrow = itemset2.getCurrentRowNo();
        ctx.setGlobal("CURRROWGLOBAL",String.valueOf(currrow));

        if (itemlay.isMultirowLayout())
            itemset.goTo(itemset.getRowSelected());

        trans.clear();

        cmd = trans.addCustomFunction("FINSTATEDEC","MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__","DB_STATE"); 
        cmd.addParameter("DB_STATE","Released"); 

        trans = mgr.perform(trans);

        sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DB_STATE");

        dfStatus = itemset2.getRow().getFieldValue("ITEM2_STATE");

        if (!(sStatusCodeReleased.equals(dfStatus)))
        {
            mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANNOTSTAT6: Work order status not valid for material unreserve."));
            return ;
        }

        double qty_ass=itemset.getRow().getNumberValue("QTY_ASSIGNED");
        if (isNaN(qty_ass))
            qty_ass=0;

        if (qty_ass > 0)
        {
            double nQtyLeftNum = itemset.getRow().getNumberValue("QTY_ASSIGNED");
            if (isNaN(nQtyLeftNum))
                nQtyLeftNum=0;

            if (nQtyLeftNum < 0)
                nQtyLeft = "0";
            else
                nQtyLeft    = String.valueOf(nQtyLeftNum);

            qrystr += "&WO_NO=" + headset.getValue("WO_NO");

            //Web Alignment - simplify code for RMBs
            bOpenNewWindow = true;
            urlString = "MaterialRequisReservatDlg2.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"))+"&LINE_ITEM_NO="+mgr.URLEncode(itemset.getRow().getValue("LINE_ITEM_NO"))+"&FRMNAME=ActiveRound"+"&QRYSTR="+mgr.URLEncode(qrystr)+"&PART_NO="+mgr.URLEncode(itemset.getRow().getValue("PART_NO"))+"&CONTRACT="+mgr.URLEncode(itemset.getRow().getValue("SPARE_CONTRACT"))+"&DESCRIPTION="+mgr.URLEncode(itemset.getRow().getValue("SPAREDESCRIPTION"))+"&QTYLEFT="+mgr.URLEncode(nQtyLeft)+"&MAINTMATORDNO="+mgr.URLEncode(itemset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));
            newWinHandle = "manUnReserve";
        }
        else
            mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANNOTMAT: No material requirements for selected item."));
    }

    public void availtoreserve()
    {
        ASPManager mgr = getASPManager();

        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
        int count = 0;
        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;

        bOpenNewWindow = true;

        if (itemlay.isMultirowLayout())
        {
            itemset.storeSelections();
            itemset.setFilterOn();
            count = itemset.countSelectedRows();
        }
        else
        {
            itemset.unselectRows();
            count = 1;
        }

        transferBuffer = mgr.newASPBuffer();

        String sProjectId = headset.getValue("PROJECT_ID");

        for (int i = 0; i < count; i++)
        {
            rowBuff = mgr.newASPBuffer();

            if (i == 0)
            {
                rowBuff.addItem("CONTRACT", itemset.getValue("SPARE_CONTRACT"));
                rowBuff.addItem("PART_NO", itemset.getValue("PART_NO"));
            }
            else
            {
                rowBuff.addItem(null, itemset.getValue("SPARE_CONTRACT"));
                rowBuff.addItem(null, itemset.getValue("PART_NO"));
            }

            transferBuffer.addBuffer("DATA", rowBuff);

            if (itemlay.isMultirowLayout())
                itemset.next();
        }

        if (itemlay.isMultirowLayout())
            itemset.setFilterOff();

        ctx.setGlobal("TRNURL", mgr.getURL());  

        urlString = createTransferUrl("../invenw/InventoryPartInStockOvw.page", transferBuffer);
        newWinHandle = "availToReserve"; 
        // 040108  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void matReqIssued()
    {
        ASPManager mgr = getASPManager();
        int count = 0;
        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;
        bOpenNewWindow = true;

        if (itemlay.isMultirowLayout())
        {
            itemset.storeSelections();
            itemset.setFilterOn();
            count = itemset.countSelectedRows();
        }
        else
        {
            itemset.unselectRows();
            count = 1;
        }

        transferBuffer = mgr.newASPBuffer();
        String order_no_ = headset.getRow().getValue("WO_NO");

        for (int i = 0; i < count; i++)
        {
            rowBuff = mgr.newASPBuffer();

            if (i == 0)
            {
                rowBuff.addItem("ORDER_NO", order_no_);
                rowBuff.addItem("LINE_ITEM_NO", itemset.getValue("LINE_ITEM_NO"));
                rowBuff.addItem("TRANSACTION_CODE", "WOISS");
            }
            else
            {
                rowBuff.addItem(null, order_no_);
                rowBuff.addItem(null, itemset.getValue("LINE_ITEM_NO"));
                rowBuff.addItem(null, "WOISS");
            }

            transferBuffer.addBuffer("DATA", rowBuff);

            if (itemlay.isMultirowLayout())
                itemset.next();
        }

        if (itemlay.isMultirowLayout())
            itemset.setFilterOff();

        urlString = createTransferUrl("ActiveWorkOrderIssue.page", transferBuffer);
        newWinHandle = "matReqIssueRMB";
    }

    public void matReqUnissue()
    {
        ASPManager mgr = getASPManager();

        currrow = itemset2.getCurrentRowNo();

        if (itemlay.isMultirowLayout())
            itemset.goTo(itemset.getRowSelected());

        trans.clear();


        //Bug 46542, start
        cmd = trans.addCustomFunction("OSTATE","Active_Separate_API.Get_Obj_State","OBJSTATE");  
        cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO")); 

        trans = mgr.perform(trans);

        String objState  =trans.getValue("OSTATE/DATA/OBJSTATE");
        double qty=itemset.getRow().getNumberValue("QTY");
        if (isNaN(qty))
            qty=0;
        if (qty > 0)
        {
            if (!(( "WORKDONE".equals(objState) )|| ( "REPORTED".equals(objState) )|| ( "STARTED".equals(objState) )|| ( "RELEASED".equals(objState) )))
            {
                mgr.showAlert(mgr.translate("PCMWACTIVEROUNDUNISSUESTATNOTVALID: Work order status not valid for material Unissue."));
                return ;
            }
            else
            {
                ctx.setGlobal("CURRROWGLOBAL",String.valueOf(currrow));
                String head_curr_row=String.valueOf(headset.getCurrentRowNo());   
                String item2_curr_row=String.valueOf(itemset2.getCurrentRowNo());  
                String item_curr_row=String.valueOf(itemset.getCurrentRowNo());  
                ctx.setGlobal("HEADCURR",head_curr_row);
                ctx.setGlobal("ITEM2CURR",String.valueOf(item2_curr_row));
                ctx.setGlobal("ITEMCURR",String.valueOf(item_curr_row));
                //Web Alignment - simplify code for RMBs
                bOpenNewWindow = true;
                urlString = "ActiveWorkOrder.page?WO_NO="+mgr.URLEncode(itemset.getRow().getValue("WO_NO"))+"&MAINTMATORDNO="+mgr.URLEncode(itemset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"))+"&FRMNAME=ActiveRound"+"&QRYSTR="+mgr.URLEncode(qrystr)+"&PART_NO="+mgr.URLEncode(itemset.getRow().getValue("PART_NO"))+"&CONTRACT="+mgr.URLEncode(itemset.getRow().getValue("SPARE_CONTRACT"));
                newWinHandle = "manUnIsssue";
            }

        }
        else
            mgr.showAlert(mgr.translate("PCMWACTIVEROUNDCANNOTUNISSUE: Cannot perform Material Requisition Unissue on this record."));
        itemset2.goTo(currrow); 
    }


    public double GetInventoryQuantity(String sQtyType)
    {
        ASPManager mgr = getASPManager();

        isSecure = new String[15];
        String sOnhand = "ONHAND";
        String sPicking = "PICKING";
        String sF = "F" ;
        String sPallet = "PALLET"  ;
        String sDeep = "DEEP" ;
        String sBuffer = "BUFFER" ;
        String sDelivery = "DELIVERY"  ;
        String sShipment = "SHIPMENT"  ;
        String sManufacturing = "MANUFACTURING" ;
        String sOwnershipDb = "COMPANY OWNED"  ;
        String sClientOwnershipConsignment = "CONSIGNMENT"  ;
        double nQty = 0.0;
        String sTrue = "TRUE";
        String sFalse = "FALSE";
        boolean secOk1 =false;
        String sConditionCode = itemset.getRow().getValue("CONDITION_CODE");  

        trans.clear();
        cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
        cmd.addParameter("PART_OWNERSHIP",mgr.readValue("PART_OWNERSHIP"));
        trans = mgr.validate(trans);
        String sClientOwnershipDefault = trans.getValue("VALIDOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");
        trans.clear();

        if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",6,isSecure))
        {
            trans.clear();
            cmd = trans.addCustomFunction("MAINTREQSUP","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","SUPPLY_CODE");
            trans = mgr.validate(trans);
            String sStandardInv = trans.getValue("MAINTREQSUP/DATA/SUPPLY_CODE");
            trans.clear();
            secOk1 = true;

            if ("COMPANY OWNED".equals(sClientOwnershipDefault))
                sClientOwnershipConsignment="CONSIGNMENT";
            else
                sClientOwnershipConsignment=null;

            if (sStandardInv.equals(itemset.getRow().getValue("SUPPLY_CODE")))
            {
                trans.clear();
                cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                cmd.addParameter("CONTRACT",itemset.getRow().getFieldValue("SPARE_CONTRACT"));
                cmd.addParameter("PART_NO",itemset.getRow().getValue("PART_NO"));
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sQtyType);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL");
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
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
                cmd.addParameter("ITEM2_ACTIVITY_SEQ","0");
                cmd.addParameter("PROJECT_ID","");
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                cmd.addParameter("CONDITION_CODE",sConditionCode);

            }
            else
            {
                trans.clear();
                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))
                {
                    cmd = trans.addCustomFunction("PROJID","Activity_API.Get_Project_Id","PROJECT_ID");
                    cmd.addParameter("ITEM2_ACTIVITY_SEQ",mgr.readValue("ITEM2_ACTIVITY_SEQ"));
                }
                //Bug 66456, End
                cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                cmd.addParameter("CONTRACT",itemset.getRow().getFieldValue("SPARE_CONTRACT"));
                cmd.addParameter("PART_NO",itemset.getRow().getValue("PART_NO"));
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sQtyType);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL");
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
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
                cmd.addParameter("INCLUDE_STANDARD",sFalse);
                cmd.addParameter("INCLUDE_PROJECT",sTrue);
                cmd.addParameter("ITEM2_ACTIVITY_SEQ","0");
                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))
                    cmd.addReference("PROJECT_ID","PROJID/DATA");
                else
                    cmd.addParameter("PROJECT_ID","");
                //Bug 66456, End
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                cmd.addParameter("CONDITION_CODE",sConditionCode);
            }    
        }
        trans = mgr.perform(trans);


        if ( secOk1 )
        {
            nQty = trans.getNumberValue("INVONHAND/DATA/QTYONHAND");
        }
        else
        {
            nQty = 0.0;
        }

        return nQty;
    }

    public void setStringLables()
    {
        ASPManager mgr = getASPManager();

        if (headset.countRows()>0)
        {
            trans.clear();

            cmd = trans.addCustomFunction("DEFCONTRACT", "Site_API.Get_Company", "COMPANY" );
            cmd.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT"));


            cmd = trans.addCustomFunction("STRA", "Accounting_Code_Parts_API.Get_Name", "ACCNT" );
            cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
            cmd.addParameter("STRING","A");

            cmd = trans.addCustomFunction("STRB", "Accounting_Code_Parts_API.Get_Name", "COSTCENT" );
            cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
            cmd.addParameter("STRING","B");

            cmd = trans.addCustomFunction("STRC", "Accounting_Code_Parts_API.Get_Name", "CODE_C" );
            cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
            cmd.addParameter("STRING","C");

            cmd = trans.addCustomFunction("STRD", "Accounting_Code_Parts_API.Get_Name", "CODE_D" );
            cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
            cmd.addParameter("STRING","D");

            cmd = trans.addCustomFunction("STRE", "Accounting_Code_Parts_API.Get_Name", "CODE_E" );
            cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
            cmd.addParameter("STRING","E");


            cmd = trans.addCustomFunction("STRF", "Accounting_Code_Parts_API.Get_Name", "CODE_F" );
            cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
            cmd.addParameter("STRING","F");

            cmd = trans.addCustomFunction("STRG", "Accounting_Code_Parts_API.Get_Name", "CODE_G" );
            cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
            cmd.addParameter("STRING","G");

            cmd = trans.addCustomFunction("STRH", "Accounting_Code_Parts_API.Get_Name", "CODE_H" );
            cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
            cmd.addParameter("STRING","H");


            cmd = trans.addCustomFunction("STRI", "Accounting_Code_Parts_API.Get_Name", "CODE_I" );
            cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
            cmd.addParameter("STRING","I");

            cmd = trans.addCustomFunction("STRJ", "Accounting_Code_Parts_API.Get_Name", "CODE_J" );
            cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
            cmd.addParameter("STRING","J");


            trans = mgr.perform(trans);


            ctx.setGlobal("NAMEB","Cost cent");
            ctx.setGlobal("NAMEC","Region");
            ctx.setGlobal("NAMED","Sales");
            ctx.setGlobal("NAMEE","Code E");
            ctx.setGlobal("NAMEF","Project");
            ctx.setGlobal("NAMEG","Code G");
            ctx.setGlobal("NAMEH","Code H");
            ctx.setGlobal("NAMEI","Code I");
            ctx.setGlobal("NAMEJ","Code J");

            strLableA = trans.getValue("STRA/DATA/ACCNT");
            strLableB = trans.getValue("STRB/DATA/COSTCENT");
            strLableC = trans.getValue("STRC/DATA/CODE_C");
            strLableD = trans.getValue("STRD/DATA/CODE_D");
            strLableE = trans.getValue("STRE/DATA/CODE_E");
            strLableF = trans.getValue("STRF/DATA/CODE_F");
            strLableG = trans.getValue("STRG/DATA/CODE_G");
            strLableH = trans.getValue("STRH/DATA/CODE_H");
            strLableI = trans.getValue("STRI/DATA/CODE_I");
            strLableJ = trans.getValue("STRJ/DATA/CODE_J");

            mgr.getASPField("ACCNT").setLabel(strLableA);
            mgr.getASPField("COST_CENTER").setLabel(strLableB);
            mgr.getASPField("CODE_C").setLabel(strLableC );
            mgr.getASPField("CODE_D").setLabel(strLableD);
            mgr.getASPField("OBJECT_NO").setLabel(strLableE);
            mgr.getASPField("PROJECT_NO").setLabel(strLableF);
            mgr.getASPField("CODE_G").setLabel(strLableG );
            mgr.getASPField("CODE_H").setLabel(strLableH );
            mgr.getASPField("CODE_I").setLabel(strLableI);
            mgr.getASPField("CODE_J").setLabel(strLableJ);
            trans.clear();
        }
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

    public boolean CheckAllMaterialIssued()
    {
        ASPManager mgr = getASPManager();
        trans.clear();

        cmd = trans.addCustomFunction("NOTISSUED","Maint_Material_Req_Line_API.Is_All_Qty_Not_Issued","QTY");
        cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

        trans = mgr.perform(trans);

        double issued = trans.getNumberValue("NOTISSUED/DATA/QTY");
        if (issued == 1)
        {
            return true;
        }
        else
            return false;  
    }
    
    private void refreshBlocks()
    {
       ASPManager mgr = this.getASPManager();
       
       if ("TRUE".equals(mgr.readValue("REFRESHFLAG")))
       {
          if ("ITEMBLK3".equals(mgr.readValue("REFRESHBLOCK")))
             okFindITEM3();
       }
    }   

    public void refresh()
    {
        if (headlay.isSingleLayout())
            headset.refreshRow();
        else
            headset.refreshAllRows();
    }

    public void refreshForm()
    {
       ASPManager mgr = getASPManager();
       okFindITEM5();
       
    }
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

        f = headblk.addField("OBJEVENTS");
        f.setHidden();

        boolean orderInst = false;
        boolean inventInst = false;
        boolean purchInst = false;       

        f = headblk.addField("MODULENAME");
        f.setHidden();
        f.setFunction("''");

        if (isModuleInst("ORDER"))
            orderInst = true;
        else
            orderInst = false;

        if (isModuleInst("INVENT"))
            inventInst    =    true;
        else
            inventInst = false;

        if (isModuleInst("PURCH"))
            purchInst = true;
        else
            purchInst = false;      

        f = headblk.addField("ROUNDDEF_ID");
        f.setSize(21);
        f.setDynamicLOV("PM_ROUND_DEFINITION",600,450);
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVEROUNDROUID: List of Route ID"));
        f.setReadOnly();
        f.setMaxLength(8);
        f.setHilite();
        f.setLabel("PCMWACTIVEROUNDROUNDDEF_ID: Route ID");
        f.setUpperCase();

        f = headblk.addField("CONTRACT");
        f.setSize(13);
        f.setReadOnly();
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVEROUNDCONT: List of Site"));
        f.setMaxLength(5);
        f.setHilite();
        f.setCustomValidation("CONTRACT","COMPANY2 COMPANY");
        f.setLabel("PCMWACTIVEROUNDWOCONTRACT: WO Site");
        f.setUpperCase();

        f = headblk.addField("ROUNDDEFDESCRIPTION");
        f.setSize(21);
        f.setReadOnly();
        f.setHilite();
        f.setLabel("PCMWACTIVEROUNDROUNDDEFDESCRIPTION: Description");
        f.setFunction("Pm_Round_Definition_API.Get_Description(:ROUNDDEF_ID)");
        mgr.getASPField("ROUNDDEF_ID").setValidation("ROUNDDEFDESCRIPTION");

        f = headblk.addField("WO_NO","Number","#");
        f.setSize(14);
        f.setReadOnly();
        f.setMaxLength(8);
        f.setHilite();
        f.setLabel("PCMWACTIVEROUNDWO_NO: WO No");

        f = headblk.addField("MODULE");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("CLIENTVAL");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("COMPANY");
        f.setSize(6);
        f.setHidden();

	//Bug 87741, Start, Added the LOV
        f = headblk.addField("STATE");
        f.setSize(10);
        f.setReadOnly();
        f.setHilite();
        f.setLOV("ActiveRoundStatusLov.page",600,450);
        f.setLabel("PCMWACTIVEROUNDSTATE: Status");
	//Bug 87741, End

        f = headblk.addField("ORG_CODE");
        f.setSize(25);
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,450);
        f.setLabel("PCMWACTIVEROUNDORG_CODE: Maintenance Organization");
        f.setMaxLength(8);
        f.setReadOnly();
        f.setHilite();
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVEROUNDORG:  List of Maintenance Organization"));
        f.setUpperCase();

        f = headblk.addField("PLAN_S_DATE","Datetime");
        f.setSize(22);
        f.setLabel("PCMWACTIVEROUNDPLANSDATE: Planned Start");
        f.setCustomValidation("PLAN_S_DATE","PLAN_S_DATE");

        f = headblk.addField("PLAN_F_DATE","Datetime");
        f.setSize(22);
        f.setLabel("PCMWACTIVEROUNDPLAN_F_DATE: Planned Completion");
        f.setCustomValidation("PLAN_F_DATE","PLAN_F_DATE");

        f = headblk.addField("ROLE_CODE");
        f.setSize(8);
        f.setDynamicLOV("ROLE_TO_SITE_LOV","CONTRACT",600,450);
        f.setUpperCase();
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVEROUNDROCO: List of Craft")); 
        f.setLabel("PCMWACTIVEROUNDROLE_CODE: Craft");
        f.setMaxLength(40);

        f = headblk.addField("PLAN_HRS","Number");
        f.setSize(18);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVEROUNDPLAN_HRS: Planned Hours");

        //Bug 80177,Start
        //Bug 81894,Start
        f= headblk.addField("NOTE");
        f.setSize(22);
        f.setHeight(3);
        f.setLabel("PCMWACTIVEROUNDINSPECTIONNOTE: Note");
        f.setMaxLength(2000);
        //Bug 80177,End
        //Bug 81894,End 

        f = headblk.addField("CALL_CODE");
        f.setSize(14);
        f.setReadOnly();
        f.setDynamicLOV("MAINTENANCE_EVENT",600,450);
        f.setLabel("PCMWACTIVEROUNDCALL_CODE: Event");
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVEROUNDCACO: List of Event")); 
        f.setUpperCase();

        f = headblk.addField("VENDOR_NO");
        f.setSize(12);
        f.setReadOnly();
        f.setDynamicLOV("PARTY_TYPE_SUPPLIER",600,450);
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVEROUNDVENO: List of Contractor")); 
        f.setLabel("PCMWACTIVEROUNDVENDOR_NO: Contractor");
        f.setDefaultNotVisible();
        f.setUpperCase();

        f = headblk.addField("VENDORNAME");
        f.setSize(21);
        f.setReadOnly();
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDVENDORNAME: Contractor Name");
        f.setDefaultNotVisible();
        f.setFunction("Supplier_Info_API.Get_Name(:VENDOR_NO)");
        mgr.getASPField("VENDOR_NO").setValidation("VENDORNAME");

        f = headblk.addField("REAL_S_DATE","Datetime");
        f.setSize(22);
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVEROUNDREAL_S_DATE: Actual Start");
        f.setCustomValidation("REAL_S_DATE","REAL_S_DATE");

        f = headblk.addField("REAL_F_DATE","Datetime");
        f.setSize(20);
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDREAL_F_DATE: Actual Completion"); 

        f = headblk.addField("REPORTED_BY");
        f.setSize(14);
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY2 COMPANY",600,450);
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVEROUNDREP: Reported by")); 
        f.setLabel("PCMWACTIVEROUNDREPORTEDBY: Reported By");
        f.setMaxLength(20);
        f.setReadOnly();
        f.setCustomValidation("COMPANY2 COMPANY,REPORTED_BY","REPORTED_BY_ID");
        f.setUpperCase();

        f = headblk.addField("REPORTED_BY_ID");
        f.setSize(15);
        f.setHidden();
        f.setMaxLength(11);
        f.setLabel("PCMWACTIVEROUNDREPORTEDBYID: Reported By ID");
        f.setUpperCase();

        f = headblk.addField("REPORT_IN_BY");
        f.setSize(8);
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,450);
        f.setMaxLength(12);
        f.setCustomValidation("COMPANY,REPORT_IN_BY","REPORT_IN_BY_ID");
        f.setLabel("PCMWACTIVEROUNDREPORTINBY: Reported In By");
        f.setUpperCase();
        f.setDefaultNotVisible();

        f = headblk.addField("REPORT_IN_BY_ID");
        f.setSize(12);
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDREPORTINBYID: Reported In By ID");
        f.setUpperCase();

        f = headblk.addField("PM_TYPE");
        f.setSize(10);
        f.setSelectBox();
        f.enumerateValues("PM_TYPE_API");
        f.setMandatory();
        f.setMaxLength(20);
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDPMTYPE: PM Type");
        f.unsetSearchOnDbColumn();   

        f = headblk.addField("AUTHORIZE_CODE");
        f.setSize(10);
        f.setDynamicLOV("ORDER_COORDINATOR_LOV",600,450);
        f.setLabel("PCMWACTIVEROUNDAUTHORIZE_CODE: Coordinator");
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVEROUNDAUOTH: List of Coordinator")); 
        f.setUpperCase();
        f.setDefaultNotVisible();

        f = headblk.addField("CUSTOMER_NO");
        f.setSize(12);
        f.setDynamicLOV("CUSTOMER_INFO",600,450);
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVEROUNDCUNO: List of Customer No"));
        f.setLabel("PCMWACTIVEROUNDCUSTOMER_NO: Customer No");
        //f.setFunction("Sc_Service_Contract_API.Get_Customer_Id(:CONTRACT_ID)");
        f.setCustomValidation("CUSTOMER_NO","CUSTOMERDESCRIPTION");
        f.setUpperCase();
        f.setReadOnly();

        f = headblk.addField("CUSTOMERDESCRIPTION");
        f.setSize(24);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVEROUNDCUSTOMERDESCRIPTION: Customer Description");
         //Bug 84436, Start  
         if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("CUSTOMER_INFO_API.Get_Name(Sc_Service_Contract_API.Get_Customer_Id(:CONTRACT_ID))");
         else
            f.setFunction("''");
         //Bug 84436, End  
        
        f = headblk.addField("ADDRESS1");
        f.setSize(14);
        f.setMaxLength(100);
        f.setHidden();
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVEROUNDADDRESS1: Address1");
        f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address1(WO_NO)");
        mgr.getASPField("WO_NO").setValidation("ADDRESS1");

        f = headblk.addField("ADDRESS2");
        f.setSize(14);
        f.setMaxLength(35);
        f.setHidden();
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVEROUNDADDRESS2: Address2");
        f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address2(WO_NO)");
        mgr.getASPField("WO_NO").setValidation("ADDRESS2");

        f = headblk.addField("ADDRESS3");
        f.setSize(14);
        f.setMaxLength(35);
        f.setHidden();
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVEROUNDADDRESS3: Address3");
        f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address3(WO_NO)");
        mgr.getASPField("WO_NO").setValidation("ADDRESS3");

        f = headblk.addField("ADDRESS4");
        f.setSize(14);
        f.setMaxLength(35);
        f.setHidden();
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVEROUNDADDRESS4: Address4");
        f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address4(WO_NO)");
        mgr.getASPField("WO_NO").setValidation("ADDRESS4");

        f = headblk.addField("ADDRESS5");
        f.setSize(14);
        f.setMaxLength(35);
        f.setHidden();
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVEROUNDADDRESS5: Address5");
        f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address5(WO_NO)");
        mgr.getASPField("WO_NO").setValidation("ADDRESS5");

        f = headblk.addField("ADDRESS6");
        f.setSize(14);
        f.setMaxLength(35); 
        f.setHidden();
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVEROUNDADDRESS6: Address6");
        f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address6(WO_NO)");
        mgr.getASPField("WO_NO").setValidation("ADDRESS6");

        f = headblk.addField("CONTACT");
        f.setSize(10);
        f.setHidden();
        f.setMaxLength(30);
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVEROUNDCONTACT: Contact");

        f = headblk.addField("REFERENCE_NO");
        f.setSize(16);
        f.setMaxLength(25);
        f.setHidden();
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVEROUNDREFERENCENO: Reference No");
        f.setUpperCase();

        f = headblk.addField("PHONE_NO");
        f.setSize(12);
        f.setHidden();
        f.setMaxLength(20);
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVEROUNDPHONENO: Phone No");

        f = headblk.addField("FIXED_PRICE");
        f.setSize(14);
        f.setMaxLength(20);
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDFIXEDPRICE: Fixed Price");
        f.setSelectBox();
        f.enumerateValues("FIXED_PRICE_API");
        f.unsetSearchOnDbColumn();

        f = headblk.addField("ADDRESS_ID");
        f.setSize(14);
        f.setMaxLength(0);
        f.setHidden();
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVEROUNDADDRESSID: Address Id");
        f.setUpperCase();

        f = headblk.addField("CONTRACT_ID");                     
        f.setLOV("PscContrProductLov.page","CONTRACT,CUSTOMER_NO,HEAD_MCH_CODE");
        f.setCustomValidation("CONTRACT_ID,LINE_NO","CONTRACT_ID,CONTRACT_NAME,LINE_NO,LINE_DESC,CONTRACT_TYPE,INVOICE_TYPE,AGREEMENT_ID,AGREEMENT_DESC");
        f.setUpperCase();
        f.setMaxLength(15);
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVEROUNDCONTRACTID: Contract ID");
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
        f.setLabel("PCMWACTIVEROUNDCONTRACTNAME: Contract Name");
        f.setSize(15);

        f = headblk.addField("LINE_NO");
        f.setLOV("PscContrProductLov.page","CONTRACT,CUSTOMER_NO,HEAD_MCH_CODE,CONTRACT_ID");
        f.setCustomValidation("CONTRACT_ID,LINE_NO","CONTRACT_ID,CONTRACT_NAME,LINE_NO,LINE_DESC,CONTRACT_TYPE,INVOICE_TYPE,AGREEMENT_ID,AGREEMENT_DESC");
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVEROUNDLINENO: Line No");
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
        f.setLabel("PCMWACTIVEROUNDLINEDESC: Description");
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
        f.setLabel("PCMWACTIVEROUNDCONTRACTTYPE: Contract Type");
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
        f.setLabel("PCMWACTIVEROUNDINVTYPE: Invoice Type");
        f.setSize(15);

        f = headblk.addField("AGREEMENT_ID");
        f.setSize(9);
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDAGREEMENT_ID: Agreement ID");
        f.setUpperCase();
        f.setDefaultNotVisible();

	f = headblk.addField("AGREEMENT_DESC");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("PRE_ACCOUNTING_ID","Number");
        f.setHidden();

        f = headblk.addField("RSTATUS0");
        f.setFunction("Round_Report_In_Status_API.Get_Client_Value(0)");
        f.setHidden();

        f = headblk.addField("RSTATUS1");
        f.setFunction("Round_Report_In_Status_API.Get_Client_Value(1)");
        f.setHidden();

        f = headblk.addField("HEAD_MCH_CODE");
        f.setSize(6);
        f.setFunction("MCH_CODE");
        f.setHidden();
        f.setDefaultNotVisible();

        f = headblk.addField("HEAD_TEMP");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("COMPANY2");
        f.setHidden();
        f.setFunction("COMPANY");               

        //-------------------------------------------------------
        //   Block used for hyperlink documents.
        //-------------------------------------------------------

        f = headblk.addField("LU_NAME");
        f.setHidden();
        f.setFunction("'ActiveRound'");

        f = headblk.addField("KEY_REF");
        f.setHidden();
        f.setFunction("CONCAT('WO_NO=',CONCAT(WO_NO,'^'))");

        f = headblk.addField("DOCUMENT");
        if (isModuleInst("DOCMAN"))
            f.setFunction("substr(Doc_Reference_Object_API.Exist_Obj_Reference('ActiveRound',CONCAT('WO_NO=',CONCAT(WO_NO,'^'))),1, 5)");
        else
            f.setFunction("''");
        f.setUpperCase();
        f.setLabel("PCMWACTIVEROUNDDOCUMENTS: Documents");
        f.setCheckBox("FALSE,TRUE");
        f.setReadOnly();
        f.setSize(18);

        f = headblk.addField("CBPROJCONNECTED");
        f.setFunction("Active_Round_API.Is_Project_Connected(:WO_NO)");
        f.setLabel("PCMWACTIVEROUNDPROJCONN: Project Connected");
        f.setCheckBox("FALSE,TRUE");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setSize(5);

        headblk.addField("CBHASOBSOLETEJOBS").
        setLabel("PCMWACTIVEROUNDCBHASOBSOLETEJOBS: Has Obsolete Jobs").
        setFunction("WORK_ORDER_JOB_API.Check_Obsolete_Jobs(:WO_NO)").
        setDefaultNotVisible().
        setReadOnly().
        setCheckBox("FALSE,TRUE");

        //Bug 77304, start
        f = headblk.addField("CBTRANSTOMOB");
        if (mgr.isModuleInstalled("MMAINT"))
            f.setFunction("MOBMGR_WORK_ORDER_API.Check_Exist(:WO_NO)");
        else
            f.setFunction("''");
        f.setLabel("PCMWACTIVEROUNDCBTRANSTOMOB: Transferred To Mobile");
        f.setCheckBox("FALSE,TRUE");
        f.setReadOnly();
        f.setDefaultNotVisible();
        //Bug 77304, end

        if (mgr.isModuleInstalled("PROJ"))
        {
            f = headblk.addField("PROGRAM_ID");
            f.setLabel("PCMWACTIVEROUNDPROGRAMID: Program ID");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("PROJECT_API.Get_Program_Id(:HPROJECT_NO)");
            f.setSize(20);

            f = headblk.addField("PROGRAMDESC");
            f.setLabel("PCMWACTIVEROUNDPROGRAMDESC: Program Description");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("PROJECT_PROGRAM_API.Get_Description(PROJECT_API.Get_Company(:HPROJECT_NO),PROJECT_API.Get_Program_Id(:HPROJECT_NO))");
            f.setSize(30);

            f = headblk.addField("HPROJECT_NO");
            f.setSize(12);
            f.setReadOnly();
            f.setDbName("PROJECT_NO");
            f.setDefaultNotVisible();
            f.setLabel("PCMWACTIVEROUNDPROJECTNO: Project No");
            f.setMaxLength(10);

            f = headblk.addField("PROJECTDESC");
            f.setLabel("PCMWACTIVEROUNDPROJECTDESC: Project Description");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("PROJECT_API.Get_Description(:HPROJECT_NO)");
            f.setSize(30);

            f = headblk.addField("SUBPROJECT_ID");
            f.setLabel("PCMWACTIVEROUNDSUBPROJECTID: Sub Project ID");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("ACTIVITY_API.Get_Sub_Project_Id(:ACTIVITY_SEQ)");
            f.setSize(20);

            f = headblk.addField("SUBPROJECTDESC");
            f.setLabel("PCMWACTIVEROUNDSUBPROJECTDESC: Sub Project Description");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("ACTIVITY_API.Get_Sub_Project_Description(:ACTIVITY_SEQ)");
            f.setSize(30);

            f = headblk.addField("ACTIVITY_ID");
            f.setLabel("PCMWACTIVEROUNDACTIVITYID: Activity ID");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("ACTIVITY_API.Get_Activity_No(:ACTIVITY_SEQ)");
            f.setSize(20);

            f = headblk.addField("ACTIVITYDESC");
            f.setLabel("PCMWACTIVEROUNDACTIVITYDESC: Activity Description");
            f.setReadOnly();
            f.setDefaultNotVisible();
            f.setFunction("ACTIVITY_API.Get_Description(:ACTIVITY_SEQ)");
            f.setSize(30);
        }

        //Bug 66456, Start
        f = headblk.addField("ACTIVITY_SEQ","Number");
        f.setLabel("PCMWACTIVEROUNDACTIVITYSEQ: Activity Sequence");
        f.setSize(20);
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = headblk.addField("ACTIVITY_SEQ1","Number","#");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("PROJECT_ID");
        if (mgr.isModuleInstalled("PROJ"))
            f.setFunction("Activity_API.Get_Project_Id(:ACTIVITY_SEQ)");
        else
            f.setFunction("''");
        f.setHidden(); 
        //Bug 66456, End
                               
        f = headblk.addField("INCLUDE_STANDARD");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("INCLUDE_PROJECT");
        f.setFunction("''");
        f.setHidden(); 

	f = headblk.addField("MAX_EMP_ID");
	f.setSize(8);
	f.setHidden();
	f.setUpperCase();
	f.setFunction("''");

	f = headblk.addField("EMP_NAME");
	f.setHidden();
	f.setFunction("''");

	f = headblk.addField("SIGN");
	f.setSize(8);
	f.setHidden();
	f.setUpperCase();
	f.setFunction("''");
	 
	f = headblk.addField("WARNING"); 
	f.setHidden();
	f.setFunction("''");
	
        headblk.setView("ACTIVE_ROUND");
     //bug id 80964,start
        headblk.defineCommand("ACTIVE_ROUND_API","New__,Modify__,Remove__,RELEASE__,START_ORDER__,WORK__,REPORT__,FINISH__");
     //bug id 80964,end
        headblk.disableDocMan();

        headset = headblk.getASPRowSet();

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWACTIVEROUNDHD: Report in Route Work Order"));
        headtbl.setWrap();

        //MultiRow Action
        headtbl.enableRowSelect();
        //

        headbar = mgr.newASPCommandBar(headblk);
        headbar.setBorderLines(false,true);
        headbar.disableCommand(headbar.NEWROW);
        headbar.disableCommand(headbar.DUPLICATEROW);

        headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");

        headbar.addCustomCommand("none","");
        headbar.addCustomCommand("preposting",mgr.translate("PCMWACTIVEROUNDWORKORDERRPRE: Preposting..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("requisitions",mgr.translate("PCMWACTIVEROUNDREQUIRMBH: Purchase Requisitions..."));
        headbar.addCustomCommand("budget",mgr.translate("PCMWACTIVEROUNDBUDGET: Budget..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("release",mgr.translate("PCMWACTIVEROUNDRELEASEDRMBH: Released"));
        headbar.addCustomCommand("startOrder",mgr.translate("PCMWACTIVEROUNDSTARTEDRMBH: Started"));
        //bug id 80964,start
        headbar.addCustomCommand("workDone",mgr.translate("PCMWACTIVEROUNDWORKDONERMBH: Work Done"));
        headbar.addCustomCommand("reported",mgr.translate("PCMWACTIVEROUNDREPORTEDRMBH: Reported"));
         //bug id 80964,end
        headbar.addCustomCommand("finish",mgr.translate("PCMWACTIVEROUNDFINISHEDRMBH: Finished"));
        headbar.addCustomCommand("printWO",mgr.translate("PCMWACTIVERNDPRINTRMBH: Print..."));
        headbar.addCustomCommand("transferToCusOrder",mgr.translate("PCMWACTIVEROUNDTRANCUSORD: Transfer to Customer Order..."));


        if (mgr.isModuleInstalled("PROJ"))
        {

            headbar.addCustomCommand("connectToActivity","ACTROUTPROJCONNCONNACT: Connect to Project Activity...");
            headbar.addCustomCommand("disconnectFromActivity","ACTROUTPROJCONNDISCONNACT: Disconnect from Project Activity");
            headbar.addCustomCommand("projectActivityInfo", mgr.translate("ACTROPRJACTINFO: Project Connection Details..."));
            headbar.addCustomCommand("activityInfo", mgr.translate("ACTROPRJACTVITYINFO: Activity Info..."));
        }

        // 031210  ARWILK  Begin  (Replace blocks with tabs)
        headbar.addCustomCommand("activateGeneral", "");
        headbar.addCustomCommand("activateTimeReport", "");
        headbar.addCustomCommand("activateMaterial", "");
        headbar.addCustomCommand("activatePermits", "");
        headbar.addCustomCommand("activatePostings", "");
        headbar.addCustomCommand("activateJobs", "");
        // 031210  ARWILK  End  (Replace blocks with tabs)

        // 031210  ARWILK  Begin  (Replace links with RMB's)
        headbar.addCustomCommandGroup("WOSTATUS", mgr.translate("PCMWACTIVEROUNDWOSTUS: Work Order Status"));
        headbar.setCustomCommandGroup("release", "WOSTATUS");
        headbar.setCustomCommandGroup("startOrder", "WOSTATUS");
        //bug id 80964,start
        headbar.setCustomCommandGroup("workDone", "WOSTATUS");
        headbar.setCustomCommandGroup("reported", "WOSTATUS");
        //bug id 80964,end
        headbar.setCustomCommandGroup("finish", "WOSTATUS");
        // 031210  ARWILK  End  (Replace links with RMB's)

        if (mgr.isModuleInstalled("PROJ"))
        {
            headbar.addCustomCommandGroup("PROJCONNGRP", mgr.translate("ACTROUTEPROJCONNGRP: Project Connection"));
            headbar.setCustomCommandGroup("connectToActivity", "PROJCONNGRP");
            headbar.setCustomCommandGroup("disconnectFromActivity", "PROJCONNGRP");
            headbar.setCustomCommandGroup("projectActivityInfo", "PROJCONNGRP");
            headbar.setCustomCommandGroup("activityInfo", "PROJCONNGRP");
        }

        //MultiRow Action
        headbar.addCommandValidConditions("release","OBJSTATE","Enable","WORKREQUEST");
        headbar.addCommandValidConditions("startOrder","OBJSTATE","Enable","WORKREQUEST;RELEASED");

      //bug id 80964,start
        headbar.addCommandValidConditions("workDone","OBJSTATE","Enable","WORKREQUEST;RELEASED;STARTED");
        headbar.addCommandValidConditions("reported","OBJSTATE","Enable","WORKREQUEST;RELEASED;STARTED;WORKDONE");
       
        headbar.addCommandValidConditions("finish","OBJSTATE","Enable","WORKREQUEST;RELEASED;STARTED;WORKDONE;REPORTED");
       //bug id 80964,end
        headbar.addCommandValidConditions("transferToCusOrder","WO_NO","Disable","null");
        headbar.appendCommandValidConditions("transferToCusOrder","CUSTOMER_NO","Disable","null");

        if (mgr.isModuleInstalled("PROJ"))
        {
            headbar.addCommandValidConditions("connectToActivity", "ACTIVITY_SEQ", "Enable", "");
            headbar.addCommandValidConditions("disconnectFromActivity", "ACTIVITY_SEQ", "Disable", "");
            headbar.addCommandValidConditions("projectActivityInfo", "ACTIVITY_SEQ", "Disable", "");
            headbar.addCommandValidConditions("activityInfo", "PROJECT_NO", "Disable", "null");
        }

        headbar.enableMultirowAction();       
        headbar.removeFromMultirowAction("preposting");
        //headbar.removeFromMultirowAction("returns");
        headbar.removeFromMultirowAction("requisitions");
        headbar.removeFromMultirowAction("budget");
        headbar.removeFromMultirowAction("transferToCusOrder");
        // The RMB finish is allowed only in single row selection because 
        // the state change requires a data entry as a prerequisite. Check the method cancelled.

        headbar.removeFromMultirowAction("finish");
        //

        if (mgr.isModuleInstalled("PROJ"))
        {
            headbar.removeFromMultirowAction("connectToActivity");
            headbar.removeFromMultirowAction("disconnectFromActivity");
            headbar.removeFromMultirowAction("projectActivityInfo");
            headbar.removeFromMultirowAction("activityInfo");

            headbar.defineCommand("connectToActivity","connectToActivity","connectToActivityClient");
            headbar.defineCommand("disconnectFromActivity","disconnectFromActivity","disconnectFromActivityClient");
        }

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        headlay.setDialogColumns(2);
        headlay.defineGroup("","WO_NO,ROUNDDEF_ID,ROUNDDEFDESCRIPTION,CONTRACT,STATE,PLAN_S_DATE,PLAN_F_DATE,CALL_CODE,REAL_S_DATE,ORG_CODE,ROLE_CODE,PLAN_HRS,NOTE,VENDOR_NO,AUTHORIZE_CODE,CUSTOMER_NO,CONTRACT_ID,CONTRACT_NAME,LINE_NO,LINE_DESC,CONTRACT_TYPE,INVOICE_TYPE,DOCUMENT,CBPROJCONNECTED,CBHASOBSOLETEJOBS,CBTRANSTOMOB",false,true);
        if (mgr.isModuleInstalled("PROJ"))
            headlay.defineGroup(mgr.translate("PCMWACTIVEROUNDLABEL0: Project Information"),"PROGRAM_ID,PROGRAMDESC,HPROJECT_NO,PROJECTDESC,SUBPROJECT_ID,SUBPROJECTDESC,ACTIVITY_ID,ACTIVITYDESC,ACTIVITY_SEQ",true,false);
        headlay.setSimple("CUSTOMERDESCRIPTION");

        headlay.setSimple("CONTRACT_NAME");
	headlay.setSimple("LINE_DESC");

        //Web Alignment - Field Order
        headlay.setFieldOrder("WO_NO,ROUNDDEF_ID,ROUNDDEFDESCRIPTION,CONTRACT,STATE,PLAN_S_DATE,PLAN_F_DATE,CALL_CODE,REAL_S_DATE,ORG_CODE,ROLE_CODE,PLAN_HRS,VENDOR_NO,AUTHORIZE_CODE,CUSTOMER_NO,CUSTOMERDESCRIPTION,AGREEMENT_ID");

        // 031210  ARWILK  Begin  (Replace blocks with tabs)
        tabs = mgr.newASPTabContainer();
        tabs.addTab(mgr.translate("PCMWACTIVEROUNDGENERALTAB: General"), "javascript:commandSet('HEAD.activateGeneral','')");
        tabs.addTab(mgr.translate("PCMWACTIVEROUNDTIMEREPTAB: Time Report"), "javascript:commandSet('HEAD.activateTimeReport','')");
        tabs.addTab(mgr.translate("PCMWACTIVEROUNDMATERIALTAB: Materials"), "javascript:commandSet('HEAD.activateMaterial','')");
        tabs.addTab(mgr.translate("PCMWACTIVEROUNDPERMITSTAB: Permits"), "javascript:commandSet('HEAD.activatePermits','')");
        tabs.addTab(mgr.translate("PCMWACTIVEROUNDPOSTINGSTAB: Postings"), "javascript:commandSet('HEAD.activatePostings','')");
        tabs.addTab(mgr.translate("PCMWACTIVEROUNDJOBSTAB: Jobs"), "javascript:commandSet('HEAD.activateJobs','')");
        // 031210  ARWILK  End  (Replace blocks with tabs)

        // --------------------------------------------------------------------------------------------------------------
        // --------------------------------------------------------------------------------------------------------------
        // --------------------------------------------------------------------------------------------------------------

        b = mgr.newASPBlock("ORG_CODE");

        b.addField("CLIENT_VALUES0");

        b = mgr.newASPBlock("STATE");

        b.addField("CLIENT_VALUES1");

        b = mgr.newASPBlock("CALL_CODE");

        b.addField("CLIENT_VALUES2");

        b = mgr.newASPBlock("PM_TYPE");

        b.addField("CLIENT_VALUES3");

        b = mgr.newASPBlock("FIXED_PRICE");

        b.addField("CLIENT_VALUES4");
        head_command = headbar.getSelectedCustomCommand();  

        eventblk1 = mgr.newASPBlock("EVNTBLK1");

        f = eventblk1.addField("DEFE_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = eventblk1.addField("DEFE_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = eventblk1.addField("EVENT1_OBJEVENTS");
        f.setDbName("OBJEVENTS");
        f.setHidden();

        f = eventblk1.addField("EVNTBLK1_WO_NO");
        f.setDbName("WO_NO");
        f.setHidden();

        f = eventblk1.addField("EVENT1_MAINT_MATERIAL_ORDER_NO");
        f.setHidden();
        f.setDbName("MAINT_MATERIAL_ORDER_NO");

        eventblk1.setView("MAINT_MATERIAL_REQUISITION");
        eventset1 = eventblk1.getASPRowSet();


        //----------------------------------------------------------------
        //---------------- define pre post--------------------------------

        ref1 =  mgr.newASPBlock("REF1");
        f= ref1.addField("COMP");
        f = ref1.addField("STR_CODE");
        f = ref1.addField("CAL_ID");
        f = ref1.addField("WORK_DAY");

        blkPost = mgr.newASPBlock("POSTI");
        f = blkPost.addField("CODE_A");
        f = blkPost.addField("CODE_B");
        f=blkPost.addField("CONTROL_TYPE");

        //-------------------------------------------------------
        //   Block refers to PM ACTIONS of the General Tab (the table)
        //-------------------------------------------------------

        itemblk0 = mgr.newASPBlock("ITEM0");

        f = itemblk0.addField("ITEM0_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk0.addField("ITEM0_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk0.addField("PM_ORDER_NO","Number");
        f.setSize(9);
        f.setLabel("PCMWACTIVEROUNDPM_ORDER_NO: Order");

        f = itemblk0.addField("ROUND_REPORT_IN_STATUS");
        f.setSize(25);
        f.setMandatory();
        f.setLabel("PCMWACTIVEROUNDROUND_REPORT_IN_STATUS: Route Report In Status");

        f = itemblk0.addField("MCH_CODE");
        f.setSize(16);
        f.setDynamicLOV("MAINTENANCE_OBJECT","ITEM0_CONTRACT CONTRACT",600,450);
        f.setLabel("PCMWACTIVEROUNDMCH_CODE: Object ID");
        f.setUpperCase();
        f.setMaxLength(100);

        f = itemblk0.addField("OBJECTDESCRIPTION");
        f.setSize(22);
        f.setLabel("PCMWACTIVEROUNDOBJECTDESCRIPTION: Object Description");
        f.setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:ITEM0_CONTRACT,:MCH_CODE)");

	//    Bug 75401, Start
	f = itemblk0.addField("SUP_MCH_CODE");
	f.setSize(16);
	f.setLabel("PCMWACTIVEROUNDSUPMCHCODE: Belongs to Object");
	f.setFunction("MAINTENANCE_OBJECT_API.Get_Sup_Mch_Code(MCH_CODE_CONTRACT, MCH_CODE)");
	f.setMaxLength(100);

	f = itemblk0.addField("CURRENTPOSITION");
	f.setSize(22);
	f.setLabel("PCMWACTIVEROUNDCURRENTPOSITION: Current Position");
	f.setFunction("PART_SERIAL_CATALOG_API.Get_State(EQUIPMENT_SERIAL_API.Get_Part_No(:ITEM0_CONTRACT, :MCH_CODE),EQUIPMENT_SERIAL_API.Get_Serial_No(:ITEM0_CONTRACT, :MCH_CODE))");
	//    Bug 75401, End

        f = itemblk0.addField("INSPECTION_NOTE");
        f.setSize(50);
        f.setHeight(4);
        f.setMaxLength(2000);
        f.setCustomValidation("INSPECTION_NOTE","GENERATE_NOTE");      
        f.setLabel("PCMWACTIVEROUNDINSPECTNOTE: Inspection Note");
        f.setDefaultNotVisible();

        f = itemblk0.addField("GENERATE_NOTE_DB");
        f.setSize(25);
        f.setLabel("PCMWACTIVEROUNDGENERATENOTEDB: Generate Note");
        f.setCheckBox("2,1");

        f = itemblk0.addField("GENERATE_NOTE");
        f.setSize(25);
        f.setHidden();

        f = itemblk0.addField("ROUND_REPORT_IN_STATUS_DB");
        f.setHidden(); 

        f = itemblk0.addField("ITEM0_CONTRACT");
        f.setSize(11);
        f.setDynamicLOV("USER_ALLOWED_SITE",600,450);
        f.setMandatory();
        f.setLabel("PCMWACTIVEROUNDITEM0_CONTRACT: Site");
        f.setDbName("MCH_CODE_CONTRACT");
        f.setUpperCase();
        f.setMaxLength(5);

        f = itemblk0.addField("TEST_POINT_ID");
        f.setSize(11);
        f.setDynamicLOV("EQUIPMENT_OBJECT_TEST_PNT","CONTRACT,MCH_CODE",600,450);
        f.setLabel("PCMWACTIVEROUNDTEST_POINT_ID: Testpoint");
        f.setUpperCase();
        f.setDefaultNotVisible();

        f = itemblk0.addField("MSEQOBJECTTESTPOINTDESCRIPTIO");
        f.setSize(22);
        f.setLabel("PCMWACTIVEROUNDMSEQOBJECTTESTPOINTDESCRIPTIO: Testpoint Description");
        f.setFunction("EQUIPMENT_OBJECT_TEST_PNT_API.Get_Description(:ITEM0_CONTRACT,:MCH_CODE,:TEST_POINT_ID)");
        f.setDefaultNotVisible();

        f = itemblk0.addField("MSEQOBJECTTESTPOINTLOCATION");
        f.setSize(11);
        f.setLabel("PCMWACTIVEROUNDMSEQOBJECTTESTPOINTLOCATION: Location");
        f.setFunction("EQUIPMENT_OBJECT_TEST_PNT_API.Get_Location(:ITEM0_CONTRACT,:MCH_CODE,:TEST_POINT_ID)");
        f.setDefaultNotVisible();

        f = itemblk0.addField("ACTION_CODE_ID");
        f.setSize(13);
        f.setDynamicLOV("MAINTENANCE_ACTION",600,450);
        f.setLabel("PCMWACTIVEROUNDACTION_CODE_ID: Action");
        f.setUpperCase();

        f = itemblk0.addField("ACTCODEIDDESCRIPTION");
        f.setSize(26);
        f.setLabel("PCMWACTIVEROUNDACTCODEIDDESCRIPTION: Action Description");
        f.setFunction("MAINTENANCE_ACTION_API.Get_Description(:ACTION_CODE_ID)");
        f.setDefaultNotVisible();

        f = itemblk0.addField("PM_NO","Number", "#");
        f.setSize(11);
        f.setLabel("PCMWACTIVEROUNDPMNO: PM No");
        f.setReadOnly();

        f = itemblk0.addField("PM_REVISION");
        f.setSize(12);
        f.setLabel("PCMWACTIVEROUNDPMREV: PM Revision");
        f.setReadOnly();

        f = itemblk0.addField("PLAN_MEN","Number");
        f.setSize(15);
        f.setLabel("PCMWACTIVEROUNDPLAN_MEN: Planned Men");

        f = itemblk0.addField("ITEM0_PLAN_HRS","Number");
        f.setSize(17);
        f.setLabel("PCMWACTIVEROUNDITEM0_PLAN_HRS: Planned Hours");
        f.setDbName("PLAN_HRS");

        f = itemblk0.addField("ITEM0_WO_NO","Number");
        f.setSize(11);
        f.setMandatory();
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDITEM0WONO: WO No");
        f.setDbName("WO_NO");

        itemblk0.setView("ACTIVE_ROUND_ACTION");
        itemblk0.defineCommand("ACTIVE_ROUND_ACTION_API","New__,Modify__,Remove__");
        itemset0 = itemblk0.getASPRowSet();

        itemblk0.setMasterBlock(headblk);
        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
        itemlay0.setDialogColumns(3);  

        itembar0 = mgr.newASPCommandBar(itemblk0);
        itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
        itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
        itembar0.defineCommand(itembar0.SAVERETURN,null,"checkItem0Fields(-1)");
        itembar0.defineCommand(itembar0.SAVENEW,null,"checkItem0Fields(-1)");

        itembar0.disableCommand(itembar0.NEWROW);
        itembar0.disableCommand(itembar0.DUPLICATEROW);
        itembar0.disableCommand(itembar0.DELETE);   

        itembar0.addCustomCommand("none","");
        itembar0.addCustomCommand("completed",mgr.translate("PCMWACTIVEROUNDCOMPLETEDI0: Completed"));
        itembar0.addCustomCommand("notCompleted",mgr.translate("PCMWACTIVEROUNDNOTCOMPI0: Not Completed"));
        itembar0.addCustomCommandSeparator();
        itembar0.addCustomCommand("faultReport",mgr.translate("PCMWACTIVEROUNDFAULTREPI0: Fault Report..."));
        itembar0.addCustomCommand("serviceRequest",mgr.translate("PCMWACTIVEROUNDSERVREQI0: Service Request..."));
        itembar0.addCustomCommand("quickReport",mgr.translate("PCMWACTIVEROUNDQUICKI0: Quick Report In..."));
        itembar0.addCustomCommand("routePMAction",mgr.translate("PCMWACTIVEROUNDROUTPMACT: Route PM Action..."));

        itemtbl0 = mgr.newASPTable(itemblk0);  
        itemtbl0.setWrap();

        //MultiRow Action
        itemtbl0.enableRowSelect();

        itembar0.addCommandValidConditions("completed","ROUND_REPORT_IN_STATUS_DB","Enable","U");
        itembar0.addCommandValidConditions("notCompleted","ROUND_REPORT_IN_STATUS_DB","Enable","C");

        itembar0.enableMultirowAction();

        itembar0.removeFromMultirowAction("faultReport");
        itembar0.removeFromMultirowAction("serviceRequest");
        itembar0.removeFromMultirowAction("quickReport");
        itembar0.removeFromMultirowAction("routePMAction");
        //

        //-------------------------------------------------------
        //   Block refers to TIME REPORT tab
        //-------------------------------------------------------

        itemblk1 = mgr.newASPBlock("ITEM1");

        f = itemblk1.addField("ITEM1_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk1.addField("ITEM1_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk1.addField("CRE_DATE","Date");
        f.setSize(13);
        f.setInsertable();
        f.setLabel("PCMWACTIVEROUNDCRE_DATE: Creation Date");

        f = itemblk1.addField("EMP_NO");
        f.setSize(12);
        f.setDynamicLOV("EMPLOYEE_NO","ITEM1_COMPANY COMPANY,ITEM1_ORG_CODE ORG_CODE",600,450);
        f.setLabel("PCMWACTIVEROUNDEMPLID: Employee ID");
        f.setCustomValidation("EMP_NO,ITEM1_COMPANY,CUSTOMER_NO,ITEM1_CONTRACT,CATALOG_NO","ITEM1_ROLE_CODE,EMP_SIGNATURE,NAME,ITEM1_ORG_CODE,CMNT,CATALOG_NO,SALESPARTCATALOGDESC");   
        f.setInsertable();
        f.setUpperCase();

        f = itemblk1.addField("EMP_SIGNATURE");
        f.setSize(10);
        f.setDynamicLOV("EMPLOYEE_LOV",600,450);
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVEROUNDEMPSIGN: List of Signature"));
        f.setLabel("PCMWACTIVEROUNDEMP_SIGNATURE: Signature");
        f.setUpperCase();

        f = itemblk1.addField("RCODE");
        f.setFunction("''");
        f.setHidden();  

        f = itemblk1.addField("ORCODE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("CONTRACT1");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("COMPANY1");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("CMNT1");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("DEPART");
        f.setFunction("''");
        f.setHidden();  

        f = itemblk1.addField("CONTRACT_VAR");
        f.setHidden();  
        f.setFunction("''");

        f = itemblk1.addField("COST1","Money");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("COST2","Money");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("COST3","Money");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("BASE_PRICE","Money");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("SALE_PRICE","Money");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("DISCOUNT","Number");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("CURRENCY_RATE","Number");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("QTY1","Number");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("NAME");
        f.setSize(15);
        f.setLabel("PCMWACTIVEROUNDNAME: Name");
        f.setFunction("Person_Info_API.Get_Name(:EMP_SIGNATURE)");

        f = itemblk1.addField("ITEM1_CONTRACT");
        f.setSize(8);
        f.setMandatory();
        f.setLabel("PCMWACTIVEROUNDCONTRACT: Site");
        f.setCustomValidation("ITEM1_CONTRACT","ITEM1_COMPANY");
        f.setUpperCase();
        f.setDbName("CONTRACT");

        f = itemblk1.addField("ITEM1_ORG_CODE");
        f.setSize(12);
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ITEM1_CONTRACT CONTRACT",600,450);
        f.setCustomValidation("ITEM1_ORG_CODE,ITEM1_WO_NO,ITEM1_CONTRACT,AMOUNT,QTY,ITEM1_ROLE_CODE,CATALOG_NO,SALESPARTCATALOGDESC,CMNT,NAME","ITEM1_ORG_CODE,AMOUNT,CMNT,CATALOG_NO,SALESPARTCATALOGDESC");   
        f.setLabel("PCMWACTIVEROUNDORG_CODE: Maintenance Organization");
        f.setUpperCase();
        f.setDbName("ORG_CODE");
        f.setInsertable();

        f = itemblk1.addField("ITEM1_COMPANY");
        f.setSize(8);
        f.setMandatory();
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDCOMPANY: Company");
        f.setUpperCase();
        f.setDbName("COMPANY");

        f = itemblk1.addField("COMPANY_VAR");
        f.setHidden();
        f.setFunction("''");

        f = itemblk1.addField("ITEM1_ROLE_CODE");
        f.setSize(8);
        f.setDynamicLOV("ROLE_TO_SITE_LOV","ITEM1_CONTRACT CONTRACT",600,450);
        f.setLabel("PCMWACTIVEROUNDROLE_CODE: Craft");
        f.setCustomValidation("ITEM1_CUSTOMER_NO,ITEM1_CONTRACT,CATALOG_NO,PRICE_LIST_NO,AGREEMENT_ID,QTY1,QTY_TO_INVOICE,QTY,ITEM1_ORG_CODE,ITEM1_ROLE_CODE,AMOUNT,CMNT,NAME,SALESPARTCOST,LIST_PRICE,EMP_NO","LIST_PRICE,PRICE_LIST_NO,AMOUNT,AMOUNTSALES,CMNT,CATALOG_NO,SALESPARTCATALOGDESC,ITEM1_ROLE_CODE,ITEM1_CONTRACT");   
        f.setUpperCase();
        f.setDbName("ROLE_CODE");
        f.setInsertable();

	f = itemblk1.addField("ITEM1_CATALOG_CONTRACT");
	f.setDbName("CATALOG_CONTRACT");
        f.setHidden();

        f = itemblk1.addField("CATALOG_NO");
        f.setSize(17);
        if (orderInst)
            f.setDynamicLOV("SALES_PART_SERVICE_LOV","ITEM1_CONTRACT CONTRACT",600,450);
        f.setLabel("PCMWACTIVEROUNDCATALOG_NO: Sales Part Number");
        f.setCustomValidation("QTY_TO_INVOICE,QTY,ITEM1_CONTRACT,CATALOG_NO,ITEM1_CUSTOMER_NO,AGREEMENT_ID,PRICE_LIST_NO,QTY1,SALESPARTCATALOGDESC,NAME,ITEM1_ORG_CODE,ITEM1_ROLE_CODE,EMP_NO","LIST_PRICE,AMOUNT,AMOUNTSALES,PRICE_LIST_NO,CMNT,SALESPARTCATALOGDESC,SALESPARTCOST");   
        f.setUpperCase();
        f.setInsertable();
        f.setDefaultNotVisible();

        f = itemblk1.addField("SALESPARTCATALOGDESC");
        f.setSize(13);
        f.setLabel("PCMWACTIVEROUNDSALESPARTCATALOGDESC: Description");
        if (orderInst)
            f.setFunction("SALES_PART_API.Get_Catalog_Desc(:CONTRACT,:CATALOG_NO)");
        else
            f.setFunction("''");
        f.setDefaultNotVisible();

        f = itemblk1.addField("PRICE_LIST_NO");
        f.setSize(13);
        if (orderInst)
            f.setDynamicLOV("SALES_PRICE_LIST",600,450);
        f.setLabel("PCMWACTIVEROUNDPRICE_LIST_NO: Price List No");
        f.setCustomValidation("QTY_TO_INVOICE,QTY,ITEM1_CONTRACT,CATALOG_NO,ITEM1_CUSTOMER_NO,AGREEMENT_ID,PRICE_LIST_NO,QTY1,LIST_PRICE","LIST_PRICE,AMOUNTSALES,PRICE_LIST_NO,AGREEMENT_PRICE_FLAG");   
        f.setInsertable();
        f.setUpperCase();
        f.setDefaultNotVisible();

        f = itemblk1.addField("QTY","Number");
        f.setSize(8);
        f.setMandatory();
        f.setLabel("PCMWACTIVEROUNDHORS: Hours");
        f.setInsertable();
        f.setCustomValidation("ITEM1_CONTRACT,CATALOG_NO,ITEM1_ROLE_CODE,ITEM1_ORG_CODE,QTY,SALESPARTCOST,QTY_TO_INVOICE,ITEM1_CUSTOMER_NO,AGREEMENT_ID,PRICE_LIST_NO,QTY,LIST_PRICE,EMP_NO","LIST_PRICE,AMOUNTSALES,PRICE_LIST_NO,SALESPARTCOST,AMOUNT");

        f = itemblk1.addField("SALESPARTCOST","Money");
        f.setSize(8);
        f.setLabel("PCMWACTIVEROUNDSALESPARTCOST: Cost");
        f.setFunction("WORK_ORDER_PLANNING_UTIL_API.Get_Unit_Cost(:ITEM1_ORG_CODE,:ITEM1_ROLE_CODE,:ITEM1_CATALOG_CONTRACT,:CATALOG_NO,:ITEM1_CONTRACT,:EMP_NO,:ITEM1_WO_NO,:ROW_NO)");

        f = itemblk1.addField("AMOUNT","Money");
        f.setSize(13);
        f.setLabel("PCMWACTIVEROUNDAMOUNT: Cost Amount");
        f.setInsertable();
        f.setMandatory();

        f = itemblk1.addField("LIST_PRICE","Money");
        f.setSize(13);
        f.setLabel("PCMWACTIVEROUNDLIST_PRICE: Sales Price");
        f.setCustomValidation("LIST_PRICE","AGREEMENT_PRICE_FLAG");
        f.setDefaultNotVisible();

        f = itemblk1.addField("AMOUNTSALES","Money");
        f.setSize(20);
        f.setLabel("PCMWACTIVEROUNDAMOUNTSALES: Sales Price Amount");
        f.setFunction("(LIST_PRICE*QTY)");
        f.setDefaultNotVisible();

        f = itemblk1.addField("CMNT");
        f.setCustomValidation("CMNT","CMNT");
        f.setMaxLength(80);
        f.setSize(50);
        f.setLabel("PCMWACTIVEROUNDCMNT: Comment");
        f.setHeight(3);  
        f.setInsertable();
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_CUSTOMER_NO");
        f.setHidden();
        f.setDbName("CUSTOMER_NO");
        
        f = itemblk1.addField("ITEM1_WO_NO","Number","#");
        f.setSize(8);
        f.setDynamicLOV("ACTIVE_WORK_ORDER",600,450);
        f.setMandatory();
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDITEM1_WO_NO: WO No");
        f.setDbName("WO_NO");

        f = itemblk1.addField("ROW_NO","Number");
        f.setSize(8);
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDROW_NO: Row No");

        f = itemblk1.addField("WORK_ORDER_BOOK_STATUS");
        f.setSize(10);
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDWORK_ORDER_BOOK_STATUS: Booking Status");

        f = itemblk1.addField("WORK_ORDER_COST_TYPE");
        f.setSize(14);
        f.setMandatory();
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDWORK_ORDER_COST_TYPE: Cost Type");

        f = itemblk1.addField("WORK_ORDER_ACCOUNT_TYPE");
        f.setSize(8);
        f.setMandatory();
        f.setHidden();

        f = itemblk1.addField("AGREEMENT_PRICE_FLAG","Number");
        f.setSize(11);
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDUSEPRICELOGIC: Use Price Logic");

        f = itemblk1.addField("QTY_TO_INVOICE","Number");
        f.setHidden();

        f = itemblk1.addField("SYST_DATE","Date");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("DUMMY");
        f.setFunction("''");
        f.setHidden();

        itemblk1.setView("WORK_ORDER_CODING");
        itemblk1.defineCommand("WORK_ORDER_CODING_API","New__,Modify__,Remove__");
        itemset1 = itemblk1.getASPRowSet();

        itembar1 = mgr.newASPCommandBar(itemblk1);
        itemblk1.setMasterBlock(headblk);
        itemlay1 = itemblk1.getASPBlockLayout();
        itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);
        itemlay1.setDialogColumns(3);  

        itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
        itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
        itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1");
        itembar1.defineCommand(itembar1.DELETE,"deleteITEM1");
        itembar1.enableCommand(itembar1.FIND);
        itembar1.setBorderLines(false,true);   

	//Bug 89399, Start
	itembar1.defineCommand(itembar1.SAVERETURN,"saveReturnITEM1","checkMando()");
	itembar1.defineCommand(itembar1.SAVENEW,"saveNewITEM1","checkMando()");
	//Bug 89399, End

        itemtbl1 = mgr.newASPTable(itemblk1);
        itemtbl1.setWrap();
	itembar1.enableMultirowAction();
	itemtbl1.enableRowSelect();

        //-----------------------------------------------------------------------
        //-------------- This part belongs to MATERIALS TAB -----------------------
        //-----------------------------------------------------------------------

        itemblk2 = mgr.newASPBlock("ITEM2");

        f = itemblk2.addField("ITEM2_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk2.addField("ITEM2_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk2.addField("ITEM2_OBJSTATE");
        f.setHidden();
        f.setDbName("OBJSTATE");

        f = itemblk2.addField("ITEM2_OBJEVENTS");
        f.setHidden();
        f.setDbName("OBJEVENTS");

        f = itemblk2.addField("MAINT_MATERIAL_ORDER_NO","Number","#");
        f.setSize(12);
        f.setReadOnly();
        f.setLabel("PCMWACTIVEROUNDMAINT_MATERIAL_ORDER_NO: Order No");

        f = itemblk2.addField("ITEM2_WO_NO","Number","#");
        f.setSize(11);
        f.setDbName("WO_NO");
        f.setMaxLength(8);
        f.setReadOnly();
        f.setCustomValidation("ITEM2_WO_NO","DUE_DATE,NPREACCOUNTINGID,ITEM2_CONTRACT,ITEM2_COMPANY,MCHCODE,ITEM2DESCRIPTION");
        f.setDynamicLOV("ACTIVE_WO_NOT_REPORTED",600,445);
        f.setLabel("PCMWACTIVEROUNDWO_NO: WO No");
        f.setHidden();

        f = itemblk2.addField("MCHCODE");
        f.setSize(13);
        f.setHidden();
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVEROUNDMCHCODE: Object ID");
        f.setFunction("WORK_ORDER_API.Get_Mch_Code(:WO_NO)");
        f.setUpperCase();

        f = itemblk2.addField("ITEM2DESCRIPTION");
        f.setReadOnly();
        f.setHidden();
        f.setFunction("''");

        f = itemblk2.addField("ITEM2_SIGNATURE");
        f.setSize(8);
        f.setMaxLength(2000);
        f.setDbName("SIGNATURE");
        f.setCustomValidation("ITEM2_SIGNATURE,ITEM2_COMPANY","ITEM2_SIGNATURE_ID,SIGNATURENAME");
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445);
        f.setLabel("PCMWACTIVEROUNDITEM2SIGNATURE: Signature");
        f.setUpperCase();

        f = itemblk2.addField("SIGNATURENAME");
        f.setSize(15);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setFunction("''");

        f = itemblk2.addField("ITEM2_CONTRACT");
        f.setSize(10);
        f.setReadOnly();
        f.setDbName("CONTRACT");
        f.setMaxLength(5);
        f.setLabel("PCMWACTIVEROUNDCONTRACT: Site");
        f.setUpperCase();
        f.setInsertable();
        f.setMandatory();
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);

        f = itemblk2.addField("ENTERED","Date");
        f.setSize(15);
        f.setReadOnly();
        f.setLabel("PCMWACTIVEROUNDENTERED: Entered");

        f = itemblk2.addField("INT_DESTINATION_ID");
        f.setSize(8);
        f.setUpperCase();
        f.setMaxLength(30);
        f.setCustomValidation("INT_DESTINATION_ID,ITEM2_CONTRACT","INT_DESTINATION_DESC");
        if (inventInst)
            f.setDynamicLOV("INTERNAL_DESTINATION_LOV","CONTRACT",600,445);
        f.setLabel("PCMWACTIVEROUNDINT_DESTINATION_ID: Int Destination");

        f = itemblk2.addField("INT_DESTINATION_DESC");
        f.setSize(20);
        f.setDefaultNotVisible();
        f.setMaxLength(2000);

        f = itemblk2.addField("DUE_DATE","Date");
        f.setSize(15);
        f.setLabel("PCMWACTIVEROUNDDUE_DATE: Due Date");

        f = itemblk2.addField("ITEM2_STATE");
        f.setSize(10);
        f.setDbName("STATE");
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVEROUNDSTATE: Status");

        f = itemblk2.addField("NREQUISITIONVALUE", "Number");
        f.setSize(8);
        f.setHidden();
        f.setReadOnly();
        f.setLabel("PCMWACTIVEROUNDNREQUISITIONVALUE: Total Value");
        f.setFunction("''");

        f = itemblk2.addField("SNOTETEXT");
        f.setHidden();
        f.setFunction("''");

        f = itemblk2.addField("ITEM2_SIGNATURE_ID");
        f.setReadOnly();
        f.setDbName("SIGNATURE_ID");
        f.setInsertable();
        f.setHidden();
        f.setUpperCase();

        f = itemblk2.addField("NNOTEID", "Number");
        f.setInsertable();
        f.setReadOnly();
        f.setHidden();
        f.setFunction("''");

        f = itemblk2.addField("ORDERCLASS");
        f.setHidden();
        f.setReadOnly();
        f.setFunction("''");

        f = itemblk2.addField("SNOTEIDEXIST");
        f.setReadOnly();
        f.setHidden();
        f.setFunction("''");

        f = itemblk2.addField("ITEM2_COMPANY");
        f.setHidden();
        f.setDbName("COMPANY");
        f.setFunction("''");
        f.setUpperCase();

        f = itemblk2.addField("NPREACCOUNTINGID", "Number");
        f.setHidden();
        f.setReadOnly();
        f.setFunction("Active_Work_Order_API.Get_Pre_Accounting_Id(:ITEM2_WO_NO)");

        f = itemblk2.addField("ITEM2_ACTIVITY_SEQ","Number");
        f.setLabel("PCMWACTIVEROUNDITEM2ACTSEQ: Activity Seq");
        f.setDbName("ACTIVITY_SEQ");
        f.setSize(20);
        f.setHidden();

        f = itemblk2.addField("MUL_REQ_LINE");
        f.setReadOnly();
        f.setFunction("Maint_Material_Requisition_API.Multiple_Mat_Req_Exist(:ITEM2_WO_NO)");
        f.setLabel("PCMWACTIVEROUNDITEM3MULMATREQEXIST: Multiple Material Requisitions Exist"); 
        f.setCheckBox("FALSE,TRUE");
        f.setDefaultNotVisible();

        f = itemblk2.addField("EXIST");
        f.setFunction("''");
        f.setHidden();

        f = itemblk2.addField("ORDER_LIST");
        f.setFunction("''");
        f.setHidden();

	f = itemblk2.addField("WO_STATUS");
        f.setHidden();
        f.setFunction("Active_Separate_Api.Get_Obj_State(:ITEM2_WO_NO)");

        itemblk2.setView("MAINT_MATERIAL_REQUISITION");
        itemblk2.defineCommand("MAINT_MATERIAL_REQUISITION_API","New__,Modify__,Remove__,PLAN__,RELEASE__,CLOSE__");
        itemset2 = itemblk2.getASPRowSet();

        itemblk2.setMasterBlock(headblk);

        itemtbl2 = mgr.newASPTable(itemblk2);
        itemtbl2.setWrap();
        //Web Alignment - Enable Multirow
        itemtbl2.enableRowSelect();
        //
        itembar2 = mgr.newASPCommandBar(itemblk2);
        itembar2.addCustomCommand("plan",mgr.translate("PCMWACTIVEROUNDPLANCONS: Plan"));
        itembar2.addCustomCommand("releaseMat",mgr.translate("PCMWACTIVEROUNDRELEA: Release"));
        itembar2.addCustomCommand("close",mgr.translate("PCMWACTIVEROUNDCLOS: Close"));
        itembar2.addCustomCommandSeparator();
        if (mgr.isPresentationObjectInstalled("mpccow/PreAccountingDlg.page"))
            itembar2.addCustomCommand("prePostHead",mgr.translate("PCMWACTIVEROUNDPREPOSTHEAD: Pre Posting Header..."));
        itembar2.addCustomCommand("printPicList",mgr.translate("PCMWACTIVEROUNDPICLSTMAT: Pick List For Material Requistion - Printout..."));
        itembar2.addCustomCommandSeparator();

        //Web Alignment - Enable Multirow 

        itembar2.addCommandValidConditions("plan","OBJSTATE","Enable","Released");
        itembar2.addCommandValidConditions("releaseMat","WO_STATUS","Enable","RELEASED;STARTED;WORKDONE;REPORTED");
	itembar2.addCommandValidConditions("releaseMat","OBJSTATE","Disable","Released");
        itembar2.addCommandValidConditions("close","OBJSTATE","Enable","Released;Planned");

        itembar2.enableMultirowAction();
        if (mgr.isPresentationObjectInstalled("mpccow/PreAccountingDlg.page"))
            itembar2.removeFromMultirowAction("prePostHead");
        //

        itembar2.enableCommand(itembar2.FIND);
        itembar2.defineCommand(itembar2.DELETE,"deleteITEM2");
        itembar2.defineCommand(itembar2.NEWROW,"newRowITEM2");
        itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
        itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");

        itembar2.defineCommand(itembar2.SAVERETURN,"saveReturnITEM2","checkItem2Fields(-1)");
        itembar2.defineCommand(itembar2.SAVENEW,null,"checkItem2Fields(-1)");

        itemlay2 = itemblk2.getASPBlockLayout();
        itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);
        itemlay2.setSimple("INT_DESTINATION_DESC");
        itemlay2.setSimple("SIGNATURENAME");


        itemblk = mgr.newASPBlock("ITEM5");

        f = itemblk.addField("ITEM_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk.addField("ITEM_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk.addField("LINE_ITEM_NO","Number");
        f.setSize(8);
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setLabel("PCMWACTIVEROUNDLINE_ITEM_NO: Line No");

        f = itemblk.addField("ORDER_CLASS");
        f.setHidden();
        f.setFunction("''");

        f = itemblk.addField("PART_NO");
        f.setSize(14);
        f.setReadOnly();
        f.setInsertable();
        f.setMaxLength(25); 
        f.setCustomValidation("PART_NO,SPARE_CONTRACT,CATALOG_CONTRACT,OWNER,CONDITION_CODE,SUPPLY_CODE,ITEM0_MAINT_MATERIAL_ORDER_NO,PART_OWNERSHIP,ITEM_WO_NO","ITEM_CATALOG_NO,ITEMCATALOGDESC,SCODEA,SCODEB,SCODEC,SCODED,SCODEE,SCODEF,SCODEG,SCODEH,SCODEI,SCODEJ,HASSPARESTRUCTURE,DIMQTY,TYPEDESIGN,QTYONHAND,UNITMEAS,SPAREDESCRIPTION,CONDITION_CODE,CONDDESC,QTY_AVAILABLE,ACTIVEIND_DB,PART_OWNERSHIP,PART_OWNERSHIP_DB,OWNER");//Bug 43249
        if (inventInst)
            f.setDynamicLOV("INVENTORY_PART_WO_LOV","SPARE_CONTRACT CONTRACT",600,445);
        f.setMandatory();
        f.setLabel("PCMWACTIVEROUNDPART_NO: Part No");
        f.setUpperCase();

        f = itemblk.addField("SPAREDESCRIPTION");
        f.setSize(20);
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVEROUNDSPAREDESCRIPTION: Part Description");
        if (inventInst)
            f.setFunction("INVENTORY_PART_API.Get_Description(:SPARE_CONTRACT,:PART_NO)");
        else
            f.setFunction("''");

        f = itemblk.addField("CONDITION_CODE");
        f.setLabel("PCMWMAINTMATERIALREQUISITIONCONDITIONCODE: Condition Code");
        f.setSize(15);
        f.setDynamicLOV("CONDITION_CODE",600,445);
        f.setUpperCase(); 
        f.setCustomValidation("CONDITION_CODE,PART_NO,SPARE_CONTRACT,OWNER,PART_OWNERSHIP_DB,SUPPLY_CODE,ITEM0_MAINT_MATERIAL_ORDER_NO,ITEM2_ACTIVITY_SEQ,PART_OWNERSHIP,PLAN_QTY","CONDDESC,QTYONHAND,QTY_AVAILABLE,ITEM_COST,AMOUNTCOST"); //Bug 43249

        f = itemblk.addField("CONDDESC");
        f.setLabel("PCMWMAINTMATERIALREQUISITIONCONDDESC: Condition Code Description");
        f.setSize(20);
        f.setMaxLength(50);
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setFunction("CONDITION_CODE_API.Get_Description(:CONDITION_CODE)");

        f = itemblk.addField("PART_OWNERSHIP");
        f.setSize(25);
        f.setInsertable();
        f.setSelectBox();
        f.enumerateValues("PART_OWNERSHIP_API");
        f.setLabel("PCMWMAINTMATERIALREQUISITIONPARTOWNERSHIP: Ownership"); 
        f.setCustomValidation("PART_OWNERSHIP,PART_NO,SPARE_CONTRACT,OWNER,CONDITION_CODE,SUPPLY_CODE,ITEM0_MAINT_MATERIAL_ORDER_NO","PART_OWNERSHIP_DB,QTYONHAND,QTY_AVAILABLE"); //Bug 43249

        f = itemblk.addField("PART_OWNERSHIP_DB");
        f.setHidden();

        f = itemblk.addField("OWNER");
        f.setSize(15);
        f.setMaxLength(20);
        f.setInsertable();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONPARTOWNER: Owner"); 
        f.setCustomValidation("OWNER,PART_OWNERSHIP_DB,CONDITION_CODE,PART_NO,SPARE_CONTRACT,SUPPLY_CODE,ITEM0_MAINT_MATERIAL_ORDER_NO,PART_OWNERSHIP","OWNER_NAME,WO_CUST,QTYONHAND,QTY_AVAILABLE");     //Bug 43249
        f.setDynamicLOV("CUSTOMER_INFO");
        f.setUpperCase();

        f = itemblk.addField("WO_CUST");
        f.setHidden();
        f.setFunction("ACTIVE_SEPARATE_API.Get_Customer_No(:ITEM_WO_NO)");

        f = itemblk.addField("OWNER_NAME");
        f.setSize(20);
        f.setMaxLength(100);
        f.setReadOnly();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONPARTOWNERNAME: Owner Name");
        f.setFunction("CUSTOMER_INFO_API.Get_Name(:OWNER)");

        f = itemblk.addField("SPARE_CONTRACT");
        f.setSize(11);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(5);
        f.setMandatory();
        f.setLabel("PCMWACTIVEROUNDSPARE_CONTRACT: Site");
        f.setUpperCase();

        f = itemblk.addField("HASSPARESTRUCTURE");
        f.setSize(8);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVEROUNDHASSPARESTRUCTURE: Structure");
        f.setFunction("Equipment_Spare_Structure_API.Has_Spare_Structure_Boolean(:PART_NO,:SPARE_CONTRACT)");

        f = itemblk.addField("ITEM_JOB_ID", "Number");
        f.setDbName("JOB_ID");
        f.setSize(8);
        f.setInsertable();
        f.setDefaultNotVisible();
        f.setDynamicLOV("WORK_ORDER_JOB", "ITEM_WO_NO WO_NO");
        f.setLabel("PCMWACTIVEROUNDJOBID: Job Id");

        f = itemblk.addField("DIMQTY");
        f.setSize(11);
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setDefaultNotVisible();   
        f.setLabel("PCMWACTIVEROUNDDIMQTY: Dimension/ Quality");
        if (inventInst)
            f.setFunction("INVENTORY_PART_API.Get_Dim_Quality(:SPARE_CONTRACT,:PART_NO)");
        else
            f.setFunction("''");

        f = itemblk.addField("TYPEDESIGN");
        f.setSize(15);
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVEROUNDTYPEDESIGN: Type Designation");
        if (inventInst)
            f.setFunction("INVENTORY_PART_API.Get_Type_Designation(:SPARE_CONTRACT,:PART_NO)");
        else
            f.setFunction("''");

        f = itemblk.addField("DATE_REQUIRED","Date");
        f.setSize(13);
        f.setLabel("PCMWACTIVEROUNDDATE_REQUIRED: Date Required");

        f = itemblk.addField("SUPPLY_CODE");
        f.setSize(25);
        f.setMandatory();
        f.setInsertable();
        f.setSelectBox();
        f.setCustomValidation("PART_NO,SPARE_CONTRACT,CATALOG_CONTRACT,OWNER,CONDITION_CODE,SUPPLY_CODE,ITEM0_MAINT_MATERIAL_ORDER_NO,ITEM2_ACTIVITY_SEQ","QTYONHAND,QTY_AVAILABLE"); 
        f.enumerateValues("MAINT_MAT_REQ_SUP_API");
        f.setLabel("PCMWACTIVEROUNDSUPPLYCODE: Supply Code");
        f.setMaxLength(200);

        f = itemblk.addField("PLAN_QTY","Number");
        f.setSize(14);
        f.setCustomValidation("PLAN_QTY,PART_NO,SPARE_CONTRACT,ITEM_CATALOG_NO,CATALOG_CONTRACT,ITEM_PRICE_LIST_NO,PLAN_LINE_NO,ITEM_DISCOUNT,ITEM_WO_NO,ITEM_COST,ITEM_LIST_PRICE,ITEMDISCOUNTEDPRICE,SERIAL_NO,CONFIGURATION_ID,CONDITION_CODE","ITEM_COST,AMOUNTCOST,ITEM_PRICE_LIST_NO,ITEM_DISCOUNT,ITEM_LIST_PRICE,ITEMSALESPRICEAMOUNT,ITEMDISCOUNTEDPRICE");
        f.setMandatory();
        f.setLabel("PCMWACTIVEROUNDPLAN_QTY: Quantity Required");

        f = itemblk.addField("ITEM_QTY","Number");
        f.setSize(13);
        f.setDbName("QTY");
        f.setReadOnly();
        f.setLabel("PCMWACTIVEROUNDQTYISSUED: Quantity Issued");

        f = itemblk.addField("QTY_SHORT","Number");
        f.setSize(11);
        f.setMandatory();
        f.setHidden();
        f.setReadOnly();
        f.setLabel("PCMWACTIVEROUNDQTY_SHORT: Quantity Short");

        f = itemblk.addField("QTYONHAND","Number");
        f.setSize(17);
        f.setReadOnly();
        f.setLabel("PCMWACTIVEROUNDQTYONHAND: Quantity on Hand");
        if (inventInst)
            //f.setFunction("Inventory_Part_In_Stock_API.Get_Inventory_Quantity(:SPARE_CONTRACT,:PART_NO,NULL,'ONHAND',NULL,NULL,:PART_OWNERSHIP_DB,NULL,NULL,NULL,:OWNER,NULL,'PICKING','F','PALLET','DEEP','BUFFER','DELIVERY','SHIPMENT','MANUFACTURING',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,:CONDITION_CODE)");
            f.setFunction("Maint_Material_Req_Line_Api.Get_Qty_On_Hand(:ITEM0_MAINT_MATERIAL_ORDER_NO,:LINE_ITEM_NO)"); //Bug 46621
        else
            f.setFunction("''");

        //Bug 76767, Start, Modified the function call
                f = itemblk.addField("QTY_AVAILABLE","Number");
        f.setSize(17);
        f.setReadOnly();
        f.setLabel("PCMWACTIVEROUNDQTY_AVAILABLE: Quantity Available");
        if (inventInst)
      	  	f.setFunction("MAINT_MATERIAL_REQ_LINE_API.Get_Qty_Available(:ITEM0_MAINT_MATERIAL_ORDER_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''");
      //Bug 76767, End

        f = itemblk.addField("QTY_ASSIGNED","Number");
        f.setSize(11);
        f.setMandatory();
        f.setLabel("PCMWACTIVEROUNDQTY_ASSIGNED: Quantity Assigned");

        f = itemblk.addField("QTY_RETURNED","Number");
        f.setSize(11);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMandatory();
        f.setLabel("PCMWACTIVEROUNDQTY_RETURNED: Quantity Returned");

        f = itemblk.addField("UNITMEAS");
        f.setSize(11);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVEROUNDUNITMEAS: Unit");
        if (purchInst)
            f.setFunction("Purchase_Part_Supplier_API.Get_Unit_Meas(:SPARE_CONTRACT,:PART_NO)");
        else
            f.setFunction("''");

        f = itemblk.addField("CATALOG_CONTRACT");
        f.setSize(10);
        f.setMaxLength(5);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVEROUNDCATALOG_CONTRACT: Sales Part Site");
        f.setUpperCase();

        f = itemblk.addField("ITEM_CATALOG_NO");
        f.setSize(9);
        f.setDbName("CATALOG_NO");
        f.setMaxLength(25);
        f.setDefaultNotVisible();
        f.setCustomValidation("ITEM_CATALOG_NO,ITEM_WO_NO,CATALOG_CONTRACT,ITEM_PRICE_LIST_NO,PLAN_QTY,PLAN_LINE_NO,PART_NO,SPARE_CONTRACT,ITEM_COST,PLAN_QTY,ITEM_DISCOUNT,SERIAL_NO,CONFIGURATION_ID,CONDITION_CODE","ITEM_LIST_PRICE,ITEM_COST,AMOUNTCOST,ITEMCATALOGDESC,ITEM_DISCOUNT,ITEMSALESPRICEAMOUNT,ITEMDISCOUNTEDPRICE,ITEM_PRICE_LIST_NO,SALES_PRICE_GROUP_ID");
        if (orderInst)
            f.setDynamicLOV("SALES_PART_ACTIVE_LOV","CATALOG_CONTRACT CONTRACT",600,445);
        f.setLabel("PCMWACTIVEROUNDITEMCATALOG_NO: Sales Part Number");
        f.setUpperCase();

        f = itemblk.addField("ITEMCATALOGDESC");
        f.setSize(17);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVEROUNDITEMCATALOGDESC: Sales Part Description");
        if (orderInst)
            f.setFunction("SALES_PART_API.Get_Catalog_Desc(:CATALOG_CONTRACT,:ITEM_CATALOG_NO)");
        else
            f.setFunction("''");

        f = itemblk.addField("ITEM_PRICE_LIST_NO");
        f.setSize(10);
        f.setDbName("PRICE_LIST_NO");
        f.setMaxLength(10);
        f.setDefaultNotVisible();
        f.setCustomValidation("ITEM_PRICE_LIST_NO,SPARE_CONTRACT,PART_NO,ITEM_COST,PLAN_QTY,ITEM_CATALOG_NO,PLAN_QTY,ITEM_WO_NO,PLAN_LINE_NO,ITEM_DISCOUNT,CATALOG_CONTRACT,SERIAL_NO,CONFIGURATION_ID,CONDITION_CODE","ITEM_COST,AMOUNTCOST,ITEM_LIST_PRICE,ITEM_DISCOUNT,ITEMSALESPRICEAMOUNT,ITEMDISCOUNTEDPRICE");
        if (orderInst)
            f.setDynamicLOV("SALES_PRICE_LIST",600,445);
        f.setLabel("PCMWACTIVEROUNDITEM_PRICE_LIST_NO: Price List No");
        f.setUpperCase();

        f = itemblk.addField("ITEM_LIST_PRICE","Money");
        f.setSize(9);
        f.setDbName("LIST_PRICE");
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVEROUNDITEM_LIST_PRICE: Sales Price");

        f = itemblk.addField("ITEM_DISCOUNT","Number");
        f.setSize(14);
        f.setCustomValidation("ITEM_DISCOUNT,ITEM_LIST_PRICE,PLAN_QTY","ITEMSALESPRICEAMOUNT,ITEMDISCOUNTEDPRICE");
        f.setDefaultNotVisible();
        f.setDbName("DISCOUNT");
        f.setLabel("PCMWACTIVEROUNDDISCOUNT: Discount %");

        f = itemblk.addField("ITEMDISCOUNTEDPRICE", "Money");
        f.setSize(14);
        f.setReadOnly();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONITEMDISCOUNTEDPRICE: Discounted Price");
        f.setFunction("LIST_PRICE - (NVL(DISCOUNT, 0)/100*LIST_PRICE)");

        f = itemblk.addField("ITEMSALESPRICEAMOUNT", "Money");
        f.setSize(14);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVEROUNDITEMSALESPRICEAMOUNT: Price Amount");
        f.setFunction("''");

        f = itemblk.addField("ITEM_COST", "Money");
        f.setSize(11);
        f.setReadOnly();
        f.setDefaultNotVisible();
        //f.setDbName("COST");
        f.setLabel("PCMWACTIVEROUNDCOST: Cost");
        f.setFunction("''");

        f = itemblk.addField("AMOUNTCOST", "Money");
        f.setSize(11);
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVEROUNDAMOUNTCOST: Cost Amount");
        //f.setFunction("COST*PLAN_QTY");
        f.setFunction("''");
        f.setReadOnly();

        f = itemblk.addField("SCODEA");
        f.setSize(11);
        f.setLabel("PCMWACTIVEROUNDSCODEA: Account");
        f.setHidden();
        f.setFunction("''");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEB");
        f.setSize(11);
        f.setLabel("PCMWACTIVEROUNDSCODEB: Cost Center");
        f.setHidden();
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEF");
        f.setSize(11);
        f.setLabel("PCMWACTIVEROUNDSCODEF: Project No");
        f.setHidden();
        f.setFunction("''");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEE");
        f.setSize(11);
        f.setLabel("PCMWACTIVEROUNDSCODEE: Object No");
        f.setHidden();
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEC");
        f.setSize(11);
        f.setLabel("PCMWACTIVEROUNDSCODEC: Code C");
        f.setHidden();
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODED");
        f.setSize(11);
        f.setLabel("PCMWACTIVEROUNDSCODED: Code D");
        f.setHidden();
        f.setFunction("''");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEG");
        f.setSize(11);
        f.setLabel("PCMWACTIVEROUNDSCODEG: Code G");
        f.setHidden();
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEH");
        f.setSize(11);
        f.setLabel("PCMWACTIVEROUNDSCODEH: Code H");
        f.setHidden();
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEI");
        f.setSize(11);
        f.setLabel("PCMWACTIVEROUNDSCODEI: Code I");
        f.setHidden();
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEJ");
        f.setSize(11);
        f.setLabel("PCMWACTIVEROUNDSCODEJ: Code J");
        f.setFunction("''");
        f.setReadOnly();
        f.setHidden();
        f.setDefaultNotVisible();
        f.setMaxLength(10);

        f = itemblk.addField("ITEM_WO_NO","Number","#");
        f.setSize(17);
        f.setMandatory();
        f.setHidden();
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(8);
        f.setLabel("PCMWACTIVEROUNDITEMWONO: WO No");
        f.setDbName("WO_NO");

        f = itemblk.addField("PLAN_LINE_NO","Number");
        f.setSize(17);
        f.setReadOnly();
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDPLAN_LINE_NO: Plan Line No");

        f = itemblk.addField("ITEM0_MAINT_MATERIAL_ORDER_NO","Number","#");
        f.setSize(17);
        f.setHidden();
        f.setReadOnly();
        f.setLabel("PCMWACTIVEROUNDITEM0_MAINT_MATERIAL_ORDER_NO: Mat Req Order No");
        f.setDbName("MAINT_MATERIAL_ORDER_NO");

        f = itemblk.addField("ITEM_BASE_PRICE","Money");
        f.setHidden();
        f.setFunction("''");

        f = itemblk.addField("ITEM_SALE_PRICE","Money");
        f.setHidden();
        f.setFunction("''"); 

        f = itemblk.addField("DB_STATE");
        f.setHidden();
        f.setFunction("''"); 

        f = itemblk.addField("SERIAL_TRACK");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("AUTO_REPAIRABLE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("REPAIRABLE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("LOCATION_NO");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("ITEM_ACTIVITY_SEQ");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("LOT_BATCH_NO");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("SERIAL_NO");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("ENG_CHG_LEVEL");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("WAIV_DEV_REJ_NO");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("QTY_TO_ISSUE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("QTY_TO_ASSIGN");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("QTY_LEFT");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("ITEM_CURRENCY_RATE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("RES_ALLO");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("ISS_ALLO");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("COND_CODE_USAGE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("DEF_COND_CODE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("SUPPLY_CODE_DB");
        f.setHidden();

        f = itemblk.addField("CURRENCEY_CODE");
        f.setHidden();
        f.setFunction("ACTIVE_SEPARATE_API.Get_Currency_Code(:ITEM_WO_NO)");

        f = itemblk.addField("SALES_PRICE_GROUP_ID");
        f.setHidden();
        if (orderInst)
            f.setFunction("SALES_PART_API.GET_SALES_PRICE_GROUP_ID(:CATALOG_CONTRACT,:CATALOG_NO)");
        else
            f.setFunction("''");

        f = itemblk.addField("MCHCODEITEM");
        f.setHidden();
        f.setFunction("WORK_ORDER_API.Get_Mch_Code(:ITEM_WO_NO)");

        f = itemblk.addField("CONFIGURATION");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("QTY_TYPE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("EXPIRATION");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("SUPPLY_CONTROL");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("OWNERSHIP_TYPE1");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("OWNERSHIP_TYPE2");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("OWNERSHIP_TYPE3");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("OWNERSHIP_TYPE4");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("OWNER_VENDOR");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("LOCATION_TYPE1");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("LOCATION_TYPE2");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("LOCATION_TYPE3");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("LOCATION_TYPE4");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("LOCATION_TYPE5");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("LOCATION_TYPE6");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("LOCATION_TYPE7");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("LOCATION_TYPE8");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("ORDER_ISSUE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("AUTOMAT_RESERV");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("MANUAL_RESERV");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("QTYRES");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("QTYPLANNABLE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("CAT_EXIST","Number");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("WO_SITE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("CONFIGURATION_ID");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("ACTIVEIND");
        f.setHidden();
        f.setFunction("''");
        
        f = itemblk.addField("ACTIVEIND_DB");
        f.setHidden();
        f.setFunction("''");

        itemblk.setView("MAINT_MATERIAL_REQ_LINE");

        itemblk.defineCommand("MAINT_MATERIAL_REQ_LINE_API","New__,Modify__,Remove__");
        itemset = itemblk.getASPRowSet();
        itemblk.setMasterBlock(itemblk2);

        itembar = mgr.newASPCommandBar(itemblk);
        itembar.enableCommand(itembar.FIND);
        itembar.defineCommand(itembar.NEWROW,"newRowITEM5");
        itembar.defineCommand(itembar.COUNTFIND,"countFindITEM5");
        itembar.defineCommand(itembar.OKFIND,"okFindITEM5");

        //itembar.defineCommand(itembar.SAVERETURN,null,"checkItem5Owner()");

        itembar.addCustomCommand("sparePartObject",mgr.translate("PCMWACTIVEROUNDSPAREPARTOBJ: Spare Parts in Object..."));
        itembar.addCustomCommand("objStructure",mgr.translate("PCMWACTIVEROUNDOBJSTRUCT: Object Structure..."));
        itembar.addCustomCommand("detchedPartList",mgr.translate("PCMWACTIVEROUNDSPAREINDETACH: Spare Parts in Detached Part List..."));
        itembar.addCustomCommandSeparator();
        itembar.addCustomCommand("reserve",mgr.translate("PCMWACTIVEROUNDRESERV: Reserve"));
        itembar.addCustomCommand("manReserve",mgr.translate("PCMWACTIVEROUNDRESERVMAN: Reserve manually..."));
        if (mgr.isPresentationObjectInstalled("invenw/InventoryPartInStockOvw.page"))
            itembar.addCustomCommand("availtoreserve",mgr.translate("PCMWACTIVEROUNDUNAVAILABLETORESER: Inventory Part in Stock..."));
        itembar.addCustomCommand("unreserve",mgr.translate("PCMWACTIVEROUNDUNRESERV: Unreserve..."));
        itembar.addCustomCommand("issue",mgr.translate("PCMWACTIVEROUNDISSUE: Issue"));
        itembar.addCustomCommand("issueFromInvent",mgr.translate("PCMWACTIVEROUNDISSUE2: Issue From Invent"));

        itembar.addCustomCommand("manIssue",mgr.translate("PCMWACTIVEROUNDISSUEMAN: Issue manually..."));
        itembar.addCustomCommandSeparator();

        itembar.addCommandValidConditions("sparePartObject","MCHCODEITEM","Disable","null");
        itembar.addCommandValidConditions("objStructure","MCHCODEITEM","Disable","null");

        if (mgr.isPresentationObjectInstalled("invenw/InventoryPartAvailabilityPlanningQry.page"))
            itembar.addCustomCommand("availDetail",mgr.translate("PCMWACTIVEROUNDINVAVAILPLAN: Query - Inventory Part Availability Planning..."));
        if (mgr.isPresentationObjectInstalled("purchw/PurchasePartSupplier.page"))
            itembar.addCustomCommand("supPerPart",mgr.translate("PCMWACTIVEROUNDSUPFORPART: Supplier per Part..."));

        itembar.addCustomCommand("matReqUnissue",mgr.translate("PCMWACTIVEROUNDMATREQUNISSU: Material Requisition Unissue..."));
        itembar.addCustomCommand("matReqIssued",mgr.translate("PCMWACTIVEROUNDISSUEDDETAILS: Issued Part Details..."));
        if (mgr.isPresentationObjectInstalled("mpccow/PreAccountingDlg.page"))
        {
            itembar.addCustomCommandSeparator();
            itembar.addCustomCommand("prePostHead",mgr.translate("PCMWACTIVEROUNDPREPOSTDET: Pre Posting Detail..."));
        }

        itemtbl = mgr.newASPTable(itemblk);
        itemtbl.setWrap();

        //MultiRow Action
        itemtbl.enableRowSelect();

        itembar.enableMultirowAction();

        itembar.removeFromMultirowAction("manReserve");
        itembar.removeFromMultirowAction("unreserve");
        itembar.removeFromMultirowAction("issueFromInvent");
        itembar.removeFromMultirowAction("manIssue");
        itembar.removeFromMultirowAction("matReqUnissue");

        if (mgr.isPresentationObjectInstalled("mpccow/PreAccountingDlg.page"))
            itembar.removeFromMultirowAction("prePostHead");

        itembar.forceEnableMultiActionCommand("sparePartObject");
        itembar.forceEnableMultiActionCommand("objStructure");
        itembar.forceEnableMultiActionCommand("detchedPartList");
        //

        itemlay = itemblk.getASPBlockLayout();
        itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);

        //-------------------------------------------------------
        //   Block refers to PERMITS tab
        //-------------------------------------------------------

        itemblk3 = mgr.newASPBlock("ITEM3");

        f = itemblk3.addField("ITEM3_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk3.addField("ITEM3_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk3.addField("ITEM3_WO_NO","Number","#");
        f.setSize(8);
        f.setMandatory();
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDITEM3_WO_NO: WO Number");
        f.setDbName("WO_NO");

        f = itemblk3.addField("PERMIT_SEQ","Number", "######");
        f.setSize(15);
        f.setDynamicLOV("PERMIT",600,450);
        f.setLOVProperty("WHERE","CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
        f.setLabel("PCMWACTIVEROUNDPERMIT_SEQ: Permit");
        f.setCustomValidation("PERMIT_SEQ","PERMIT_TYPE_ID,PERMITDESCRIPTION");
        f.setInsertable();

        f = itemblk3.addField("PERMIT_TYPE_ID");
        f.setSize(15);
        f.setLabel("PCMWACTIVEROUNDPERMIT_TYPE_ID: Type");
        f.setFunction("PERMIT_API.Get_Permit_Type_Id(:PERMIT_SEQ)");

        f = itemblk3.addField("PERMITDESCRIPTION");
        f.setSize(52);
        f.setLabel("PCMWACTIVEROUNDPERMITDESCRIPTION: Description");
        f.setFunction("PERMIT_API.Get_Description(:PERMIT_SEQ)");

        f = itemblk3.addField("ITEM3_CONTRACT");
        f.setSize(10);
        f.setLabel("PCMWACTIVEROUNDPERMITCONTRACT: Site");
        f.setFunction("PERMIT_API.Get_Contract(:PERMIT_SEQ)");
	f.setReadOnly();

        itemblk3.setView("WORK_ORDER_PERMIT");
        itemblk3.defineCommand("WORK_ORDER_PERMIT_API","New__,Modify__,Remove__");
        itemset3= itemblk3.getASPRowSet();

        itemblk3.setMasterBlock(headblk);
        itemlay3 = itemblk3.getASPBlockLayout();
        itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);
        itemlay3.setDialogColumns(3);  

        itembar3 = mgr.newASPCommandBar(itemblk3);
        itembar3.defineCommand(itembar3.OKFIND,"okFindITEM3");
        itembar3.defineCommand(itembar3.COUNTFIND,"countFindITEM3");
        itembar3.defineCommand(itembar3.NEWROW,"newRowITEM3");
        itembar3.disableCommand(itembar3.EDITROW);
        itembar3.enableCommand(itembar3.FIND);

        itembar3.defineCommand(itembar3.SAVERETURN,null,"checkItem3Fields(-1)");
        itembar3.defineCommand(itembar3.SAVENEW,null,"checkItem3Fields(-1)");


        itemtbl3= mgr.newASPTable(itemblk3); 
        itemtbl3.setWrap();
        itembar3.setBorderLines(false,true);   

        itembar3.addCustomCommand("none","");
        itembar3.addCustomCommand("createPermit", mgr.translate("PCMWACTIVEROUNDRPERMITCREPERM: Create Permit..."));
        itembar3.addCustomCommand("permitAttr",mgr.translate("PCMWACTIVEROUNDPERMITATTI3: Permit Attributes..."));
        itembar3.addCustomCommand("preparePermit", mgr.translate("PCMWACTIVEROUNDPERMITPREPERM: Prepare Permit..."));
        itembar3.addCustomCommand("replacePermit", mgr.translate("PCMWACTIVEROUNDPERMITREPPERM: Replace Permit..."));

        itembar3.enableMultirowAction();
        itemtbl3.enableRowSelect();
        itembar3.forceEnableMultiActionCommand("createPermit");

        itembar3.addCommandValidConditions("permitAttr","PERMIT_SEQ","Disable","null");
        itembar3.addCommandValidConditions("preparePermit","PERMIT_SEQ","Disable","null");
        itembar3.removeFromMultirowAction("replacePermit");


        //-------------------------------------------------------
        //   Block refers to POSTINGS tab
        //-------------------------------------------------------

        itemblk4 = mgr.newASPBlock("ITEM4");

        f = itemblk4.addField("ITEM4_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk4.addField("ITEM4_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk4.addField("ITEM4_CONTRACT");
        f.setSize(8);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDCONTRACT: Site");
        f.setCustomValidation("ITEM4_CONTRACT","ITEM4_COMPANY");
        f.setUpperCase();
        f.setDbName("CONTRACT");
        f.setMaxLength(5);

        f = itemblk4.addField("ITEM4_COMPANY");
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDCOMPANY: Company");
        f.setDbName("COMPANY");

        f = itemblk4.addField("ITEM4_WORK_ORDER_COST_TYPE");
        f.setSize(12);
        f.setMandatory();
        f.setSelectBox();
        f.setInsertable();
        f.enumerateValues("WORK_ORDER_COST_TYPE_API");
        f.setLabel("PCMWACTIVEROUNDWORK_ORDER_COST_TYPE: Cost Type");
        f.setDbName("WORK_ORDER_COST_TYPE");
        f.setCustomValidation("ITEM4_WORK_ORDER_COST_TYPE,WO_NO","COST_CENTER,PROJECT_NO,OBJECT_NO,ITEM4_AMOUNT,CLIENTVAL1,CLIENTVAL2");
        f.unsetSearchOnDbColumn();

        f = itemblk4.addField("WORK_ORDER_COST_TYPE_DB");
        f.setFunction("WORK_ORDER_COST_TYPE_API.Encode(WORK_ORDER_COST_TYPE)");
        f.setHidden();

        f = itemblk4.addField("ITEM4_CATALOG_NO");
        f.setSize(16);
        if (orderInst)
            f.setDynamicLOV("SALES_PART","CONTRACT",600,450);
        f.setLabel("PCMWACTIVEROUNDCATALOG_NO: Sales Part Number");
        f.setUpperCase();
        f.setDbName("CATALOG_NO");
        f.setCustomValidation("ITEM4_CATALOG_NO,ITEM4_QTY_TO_INVOICE,ITEM4_DISCOUNT,ITEM4_CONTRACT,ITEM4_PRICE_LIST_NO,ITEM4_LIST_PRICE,ITEM4_QTY,ITEM4_WORK_ORDER_COST_TYPE,ITEM4_AMOUNT","LINEDESCRIPTION,ITEM4_AMOUNT,ITEM4_PRICE_LIST_NO,ITEM4_LIST_PRICE,SALESPRICEAMOUNT,ITEM4_AGREEMENT_PRICE_FLAG,ITEM4_QTY_TO_INVOICE");

        f = itemblk4.addField("LINEDESCRIPTION");
        f.setSize(13);
        f.setInsertable();
        f.setLabel("PCMWACTIVEROUNDLINEDESCRIPTION: Description");
        if (orderInst)
            f.setFunction("substr(nvl(LINE_DESCRIPTION, Sales_Part_API.Get_Catalog_Desc(CONTRACT,CATALOG_NO)), 1, 35)");
        else
            f.setFunction("substr(LINE_DESCRIPTION, 1, 35)");

        f = itemblk4.addField("ORDER_NO");
        f.setSize(19);
        f.setLabel("PCMWACTIVEROUNDORDER_NO: Customer Order No");
        f.setDefaultNotVisible();

        f = itemblk4.addField("ITEM4_PRICE_LIST_NO");
        f.setSize(13);
        if (orderInst)
            f.setDynamicLOV("SALES_PRICE_LIST",600,450);
        f.setLabel("PCMWACTIVEROUNDPRICE_LIST_NO: Price List No");
        f.setUpperCase();
        f.setDbName("PRICE_LIST_NO");
        f.setCustomValidation("ITEM4_PRICE_LIST_NO,ITEM4_CONTRACT,ITEM4_CATALOG_NO,ITEM4_LIST_PRICE,ITEM4_QTY","ITEM4_PRICE_LIST_NO,ITEM4_LIST_PRICE,SALESPRICEAMOUNT,ITEM4_AGREEMENT_PRICE_FLAG");
        f.setDefaultNotVisible();

	f = itemblk4.addField("ITEM4_CRE_DATE", "Date");
	f.setLabel("PCMWACTIVEROUNDCREDATE: Creation Date");
	f.setDbName("CRE_DATE");

        f = itemblk4.addField("ITEM4_QTY","Number");
        f.setSize(11);
        f.setLabel("PCMWACTIVEROUNDQTY: Hours/Qty");
        f.setDbName("QTY");
        f.setCustomValidation("ITEM4_QTY,ITEM4_QTY_TO_INVOICE,ITEM4_DISCOUNT,ITEM4_WORK_ORDER_COST_TYPE,ITEM4_CATALOG_NO,ITEM4_CONTRACT,ITEM4_PRICE_LIST_NO,ITEM4_LIST_PRICE,ITEM4_AGREEMENT_PRICE_FLAG","ITEM4_AMOUNT,ITEM4_PRICE_LIST_NO");
        f.setMandatory();

        f = itemblk4.addField("ITEM4_AMOUNT","Money");
        f.setSize(12);
        f.setLabel("PCMWACTIVEROUNDAMOUNT: Cost Amount");
        f.setDbName("AMOUNT");
        f.setDefaultNotVisible();

        f = itemblk4.addField("ITEM4_QTY_TO_INVOICE","Number");
        f.setSize(11);
        f.setLabel("ITEM4QTYIN: Qty To Invoice");
        f.setDbName("QTY_TO_INVOICE");
        f.setCustomValidation("ITEM4_QTY_TO_INVOICE,ITEM4_DISCOUNT,ITEM4_LIST_PRICE","SALESPRICEAMOUNT");

        f = itemblk4.addField("POSTING_PART_OWNERSHIP");
        f.setDbName("PART_OWNERSHIP");
        f.setSize(25);
        f.setReadOnly();
        f.setLabel("PCMWACTIVEROUNDPARTOWNERSHIP: Ownership");

        f = itemblk4.addField("POSTING_PART_OWNERSHIP_DB");
        f.setDbName("PART_OWNERSHIP_DB");
        f.setHidden();

        f = itemblk4.addField("POSTING_OWNER");
        f.setDbName("OWNER");
        f.setSize(15);
        f.setMaxLength(20);
        f.setLabel("PCMWACTIVEROUNDPARTOWNER: Owner");
        f.setUpperCase();
        f.setReadOnly();

        f = itemblk4.addField("POSTING_OWNER_NAME");
        f.setSize(20);
        f.setMaxLength(100);
        f.setReadOnly();
        f.setLabel("PCMWACTIVEROUNDPARTOWNERNAME: Owner Name");
        f.setFunction("CUSTOMER_INFO_API.Get_Name(:POSTING_OWNER)");

        f = itemblk4.addField("ITEM4_LIST_PRICE","Money");
        f.setSize(13);
        f.setLabel("PCMWACTIVEROUNDLIST_PRICE: Sales Price");
        f.setDbName("LIST_PRICE");
        f.setCustomValidation("ITEM4_LIST_PRICE,ITEM4_QTY","SALESPRICEAMOUNT,ITEM4_AGREEMENT_PRICE_FLAG");

        f = itemblk4.addField("ITEM4_DISCOUNT","Number");     
        f.setSize(13);
        f.setLabel("ITEM4DIS: Discount");
        f.setDbName("DISCOUNT");
        f.setCustomValidation("ITEM4_QTY_TO_INVOICE,ITEM4_DISCOUNT,ITEM4_LIST_PRICE","SALESPRICEAMOUNT");
        f.setDefaultNotVisible();

        f = itemblk4.addField("ITEM4_WORK_ORDER_ACCOUNT_TYPE");
        f.setSize(14);
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDWORK_ORDER_ACCOUNT_TYPE: Account Type");
        f.setDbName("WORK_ORDER_ACCOUNT_TYPE");

        f = itemblk4.addField("SALESPRICEAMOUNT","Money");
        f.setSize(19);
        f.setLabel("PCMWACTIVEROUNDSALESPRICEAMOUNT: Sales Price Amount");
        f.setFunction("((LIST_PRICE - (NVL(DISCOUNT, 0) / 100 * LIST_PRICE))*QTY_TO_INVOICE)");
        f.setReadOnly();

        f = itemblk4.addField("ITEM4_AGREEMENT_PRICE_FLAG","Number");
        f.setSize(25);
        f.setLabel("PCMWACTIVEROUNDUSEPRICELOGIC: Use Price Logic");
        f.setCheckBox("0,1");
        f.setDbName("AGREEMENT_PRICE_FLAG");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk4.addField("KEEP_REVENUE");
        f.setSize(14);
        f.setSelectBox();
        f.setMandatory();
        f.enumerateValues("Keep_Revenue_API");
        f.setReadOnly();
        f.setLabel("PCMWACTIVEROUNDKEEP_REVENUE: Keep Revenue");

        f.unsetSearchOnDbColumn();

        f = itemblk4.addField("CSS_TYPE");
        f.setSize(14);
        f.setLabel("PCMWACTIVEROUNDCSSTYPE: CSS Type");
        f.setReadOnly();

        f = itemblk4.addField("ITEM4_STATE");
        f.setDbName("STATE");
        f.setSize(14);
        f.setLabel("PCMWACTIVEROUNDITEM4STATUS: Status");
        f.setReadOnly();  

        f = itemblk4.addField("ITEM4_WORK_ORDER_BOOK_STATUS");
        f.setSize(14);
        f.setSelectBox();
        f.enumerateValues("WORK_ORDER_BOOK_STATUS_API");
        f.setLabel("PCMWACTIVEROUNDWORK_ORDER_BOOK_STATUS: Booking Status");
        f.setDbName("WORK_ORDER_BOOK_STATUS");
        f.setReadOnly();

        f = itemblk4.addField("SIGNATURE");
        f.setSize(13);
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,450);
        f.setLabel("PCMWACTIVEROUNDSIGNATURE: Auth Signature");
        f.setUpperCase();
        f.setCustomValidation("SIGNATURE,ITEM4_COMPANY","SIGNATURE_ID");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk4.addField("SIGNATURE_ID");
        f.setSize(10);
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDSIGNATUREID: Auth Signature");
        f.setUpperCase();
        f.setReadOnly();

        f = itemblk4.addField("ITEM4_CMNT");
        f.setSize(20);
        f.setLabel("PCMWACTIVEROUNDCMNT: Comment");
        f.setDbName("CMNT");
        f.setDefaultNotVisible();

        f = itemblk4.addField("SEARCH");
        f.setFunction("'Y'");
        f.setHidden();

        f = itemblk4.addField("TRANSACTION_ID");
        f.setLabel("PCMWACTIVEROUNDTRANSACTIONID: Transaction Id");
        f.setSize(10);
        f.setDefaultNotVisible();

        f = itemblk4.addField("ACCNT");
        f.setSize(8);
        f.setDynamicLOV("ACCOUNT","ITEM4_COMPANY COMPANY",600,450);
        f.setLabel("PCMWACTIVEROUNDACCONT: Account");
        f.setDefaultNotVisible();

        f = itemblk4.addField("COST_CENTER");
        f.setSize(12);
        f.setDynamicLOV("CODEB","ITEM4_COMPANY COMPANY",600,450);
        f.setLabel("PCMWACTIVEROUNDCOSTCENTER: Cost Center");
        f.setDefaultNotVisible();

        f = itemblk4.addField("PROJECT_NO");
        f.setSize(13);
        f.setDynamicLOV("CODEF","ITEM4_COMPANY COMPANY",600,450);
        f.setDefaultNotVisible();

        f = itemblk4.addField("OBJECT_NO");
        f.setSize(13);
        f.setDynamicLOV("CODEE","ITEM4_COMPANY COMPANY",600,450);
        f.setDefaultNotVisible();

        f = itemblk4.addField("CODE_C");
        f.setSize(8);
        f.setDynamicLOV("CODEC","ITEM4_COMPANY COMPANY",600,450);
        f.setDefaultNotVisible();

        f = itemblk4.addField("CODE_D");
        f.setSize(8);
        f.setDynamicLOV("CODED","ITEM4_COMPANY COMPANY",600,450);
        f.setDefaultNotVisible();

        f = itemblk4.addField("CODE_E");
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("CODE_F");
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("CODE_G");
        f.setSize(8);
        f.setDynamicLOV("CODEG","ITEM4_COMPANY COMPANY",600,450);
        f.setDefaultNotVisible();

        f = itemblk4.addField("CODE_H");
        f.setSize(8);
        f.setDynamicLOV("CODEH","ITEM4_COMPANY COMPANY",600,450);
        f.setDefaultNotVisible();

        f = itemblk4.addField("CODE_I");
        f.setSize(8);
        f.setDynamicLOV("CODEI","ITEM4_COMPANY COMPANY",600,450);
        f.setDefaultNotVisible();

        f = itemblk4.addField("CODE_J");
        f.setSize(8);
        f.setDynamicLOV("CODEJ","ITEM4_COMPANY COMPANY",600,450);
        f.setDefaultNotVisible();

        f = itemblk4.addField("ITEM4_ROW_NO","Number", "#");
        f.setSize(8);
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDROW_NO: Row No");
        f.setDbName("ROW_NO");

        f = itemblk4.addField("CDOCUMENTTEXT");
        f.setSize(15);
        f.setLabel("PCMWACTIVEROUNDCDOCUMENTTEXT: Document Text");
        f.setFunction("Document_Text_API.Note_Id_Exist(:NOTE_ID)");
        f.setCheckBox("0,1");
        f.setDefaultNotVisible();

        f = itemblk4.addField("REQUISITION_NO");
        f.setSize(13);
        f.setDynamicLOV("WORK_ORDER_REQUIS_HEADER","WO_NO",600,450);
        f.setLabel("PCMWACTIVEROUNDREQUISITION_NO: Requisition No");
        f.setDefaultNotVisible();

        f = itemblk4.addField("REQUISITION_LINE_NO");
        f.setSize(18);
        f.setLabel("PCMWACTIVEROUNDREQUISITION_LINE_NO: Requisition Line No");
        f.setDefaultNotVisible();

        f = itemblk4.addField("REQUISITION_RELEASE_NO");
        f.setSize(18);
        f.setLabel("PCMWACTIVEROUNDREQUISITION_RELEASE_NO: Requisition Release No");
        f.setDefaultNotVisible();

        f = itemblk4.addField("REQUISITIONVENDORNO");
        f.setSize(   19);
        f.setLabel("PCMWACTIVEROUNDREQUISITIONVENDORNO: Requisition Supplier No");
        if (purchInst)
            f.setFunction("Purchase_Req_Util_API.Get_Line_Vendor_No (:REQUISITION_NO,:REQUISITION_LINE_NO,:REQUISITION_RELEASE_NO)");
        else
            f.setFunction("''");
        f.setDefaultNotVisible();

        f = itemblk4.addField("ITEM4_EMP_NO");
        f.setSize(7);
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDEMPNO: Employee");
        f.setUpperCase();
        f.setDbName("EMP_NO");

        f = itemblk4.addField("ITEM4_WO_NO","Number","#");
        f.setSize(8);
        f.setDynamicLOV("ACTIVE_WORK_ORDER",600,450);
        f.setMandatory();
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDWO_NO: WO No");
        f.setDbName("WO_NO");

        f = itemblk4.addField("ITEM4_CATALOGDESC");
        f.setSize(8);
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDCATALOGDESC: Description");
        if (orderInst)
            f.setFunction("SALES_PART_API.Get_Catalog_Desc(CONTRACT,CATALOG_NO)");
        else
            f.setFunction("''");
        f.setDbName("CATALOGDESC");

        f = itemblk4.addField("NOTE_ID","Number");
        f.setSize(8);
        f.setReadOnly();
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDNOTE_ID: Note ID");

        f = itemblk4.addField("ACTIVEWORKORDERFIXEDPRICE");
        f.setSize(8);
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDACTIVEWORKORDERFIXEDPRICE: Fixed Price");
        f.setFunction("ACTIVE_WORK_ORDER_API.Get_Fixed_Price(:WO_NO)");

        f = itemblk4.addField("NOTFIXEDPRICE");
        f.setSize(8);
        f.setHidden();
        f.setLabel("PCMWACTIVEROUNDNOTFIXEDPRICE: Not Fixed Price");
        f.setFunction("Fixed_Price_API.Decode('1')");

        f = itemblk4.addField("CONTRACT4");
        f.setFunction("''");
        f.setHidden();

        f = itemblk4.addField("FIX1");
        f.setFunction("''");
        f.setHidden();

        f = itemblk4.addField("COMPANY4");
        f.setFunction("''");
        f.setHidden();

        f = itemblk4.addField("AMOUNT4","Number");
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("PRICELISTNO4");
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("CLIENTVAL1");
        f.setHidden();
        f.setFunction("''");    

        f = itemblk4.addField("CLIENTVAL2");
        f.setHidden();
        f.setFunction("''");    

        f = itemblk4.addField("CLIENTVAL3");
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("CLIENTVAL4");
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("STRING");
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("COSTCENT");
        f.setHidden();
        f.setFunction("''");

        f = itemblk4.addField("LINE_DESCRIPTION");
        f.setHidden();


        f = itemblk4.addField("AUTH_ALLOWED");
        f.setFunction("Work_Order_Coding_API.Is_Authorisation_Allowed(:WO_NO)");   
        f.setHidden();

        f = itemblk4.addField("HAS_UNAUTHORIZED");
        f.setFunction("''");   
        f.setHidden();

        f = itemblk4.addField("DUMMY_ACT_QTY_ISSUED","Number");
        f.setFunction("''");
        f.setHidden();

        itemblk4.setView("WORK_ORDER_CODING");
        itemblk4.defineCommand("WORK_ORDER_CODING_API","New__,Modify__,Remove__");
        itemset4= itemblk4.getASPRowSet();

        itemblk4.setMasterBlock(headblk);
        itemlay4 = itemblk4.getASPBlockLayout();
        itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT);
        itemlay4.setDialogColumns(3);  

        itembar4 = mgr.newASPCommandBar(itemblk4);
        itembar4.defineCommand(itembar4.OKFIND,"okFindITEM4");
        itembar4.defineCommand(itembar4.COUNTFIND,"countFindITEM4");
        itembar4.defineCommand(itembar4.NEWROW,"newRowITEM4");
        itembar4.defineCommand(itembar4.DELETE,"deleteITEM4");
        itembar4.enableCommand(itembar4.FIND); 

        itemtbl4= mgr.newASPTable(itemblk4);
        itemtbl4.setWrap();
        //Web Alignment - Enable MultiRow Action
        itemtbl4.enableRowSelect();
        //
        itembar4.setBorderLines(false,true);   

        itembar4.defineCommand(itembar4.SAVERETURN,"saveReturnITEM4","checkItem4Fields(-1)");
        itembar4.defineCommand(itembar4.SAVENEW,"saveReturnNewITEM4","checkItem4Fields(-1)"); 
        itembar4.defineCommand(itembar4.DUPLICATEROW, "duplicateRow");

        itembar4.addCustomCommand("none",""); 
        itembar4.addCustomCommand("authorize",mgr.translate("PCMWACTIVEROUNDAUTHSELI4: Authorize Selected Rows..."));
        itembar4.addCustomCommand("authorizeAll",mgr.translate("PCMWACTIVEROUNDAUTHALLNOAUHT: Authorize All Non Authorized..."));
        itembar4.addCustomCommand("authorizeCorrect",mgr.translate("PCMWACTIVEROUNDAUTHCORRE: Authorize and Correct Postings..."));
        itembar4.addCustomCommandSeparator();
        itembar4.addCustomCommand("salesPartComp",mgr.translate("PCMWACTIVEROUNDSAPACOMP: Sales Part Complimentary..."));
        itembar4.addCustomCommand("manageSalesRevenues",mgr.translate("PCMWACTIVEROUNDMAGSALEREV: Manage Sales Revenues..."));
        itembar4.addCustomCommandSeparator();

        //MultiRow Action
        itembar4.enableMultirowAction();
        itembar4.addCommandValidConditions("authorize","SIGNATURE","Enable","null");
        itembar4.removeFromMultirowAction("salesPartComp");
        //

        // ---------------------------------------------------------------------------------------
        // ---------------------------------    ITEMBLK6   ---------------------------------------
        // ---------------------------------------------------------------------------------------

        itemblk6 = mgr.newASPBlock("ITEM6");

        itemblk6.addField("ITEM6_OBJID").
        setHidden().
        setDbName("OBJID");

        itemblk6.addField("ITEM6_OBJVERSION").
        setHidden().
        setDbName("OBJVERSION");

        itemblk6.addField("ITEM6_WO_NO", "Number", "#").
        setDbName("WO_NO").
        setHidden().
        setReadOnly().
        setInsertable().        
        setMandatory();

        itemblk6.addField("JOB_ID", "Number").
        setLabel("ACTROUNDITEM6JOBID: Job ID").
        setReadOnly().
        setInsertable().
        setMaxLength(20).
        setMandatory();

        itemblk6.addField("STD_JOB_ID").
        setSize(12).
        setLabel("ACTROUNDITEM6STDJOBID: Standard Job ID").
        setLOV("RoundStandardJobLov.page", 600, 445).
        setUpperCase().
        setInsertable().
        setQueryable().
        setCustomValidation("STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION,DESCRIPTION", "STD_JOB_ID,STD_JOB_REVISION,DESCRIPTION,STD_JOB_STATUS").
        setMaxLength(12);

        // added this field to give removin std job id for the confirmation message
        itemblk6.addField("DUMMY_STD_JOB_ID").
        setFunction("STD_JOB_ID").
        setHidden();

        itemblk6.addField("STD_JOB_CONTRACT").
        setSize(10).
        setLabel("ACTROUNDITEM6STDJOBCONTRACT: Site").
        setUpperCase().
        setReadOnly().
        setMaxLength(5);

        itemblk6.addField("STD_JOB_REVISION").
        setSize(6).
        setLabel("ACTROUNDITEM6STDJOBREVISION: Revision").
        setDynamicLOV("ROUND_STANDARD_JOB_LOV", "STD_JOB_CONTRACT CONTRACT,STD_JOB_ID", 600, 445).    
        setCustomValidation("STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION", "DESCRIPTION,STD_JOB_STATUS").
        setUpperCase().
        setInsertable().
        setQueryable().
        setMaxLength(6);

        itemblk6.addField("DESCRIPTION").
        setSize(50).
        setLabel("ACTROUNDITEM6DESCRIPTION: Description").
        setMandatory().
        setInsertable().
        setMaxLength(4000);

        itemblk6.addField("ITEM6_QTY", "Number").
        setLabel("ACTROUNDITEM6QTY: Quantity").
        setMandatory().
        setDbName("QTY").
        setMaxLength(20).
        setInsertable();

        itemblk6.addField("STD_JOB_STATUS").
        setLabel("ACTROUNDITEM6STDJOBSTATUS: Std Job Status").
        setFunction("Standard_Job_API.Get_Status(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)").
        setReadOnly().
        setMaxLength(30);

        itemblk6.addField("ITEM6_COMPANY").
        setSize(20).
        setHidden().
        setUpperCase().
        setDbName("COMPANY").
        setInsertable();

        itemblk6.addField("SIGN_ID").
        setMaxLength(20).
        setLabel("ACTROUNDITEM6SIGNID: Executed By").
        setLOV("../mscomw/MaintEmployeeLov.page","ITEM6_COMPANY COMPANY",600,450).
        setQueryable().
        setUpperCase().
        setCustomValidation("ITEM6_COMPANY,SIGN_ID","EMPLOYEE_ID,SIGN_ID").
        setDbName("SIGNATURE");  

        itemblk6.addField("EMPLOYEE_ID").
        setSize(11).
        setLabel("ACTROUNDITEM6EMPLOYEEID: Employee ID").
        setUpperCase().
        setQueryable().
        setMaxLength(11).
        setReadOnly();

        //(+) Bug 66406, Start
        itemblk6.addField("CONN_PM_NO", "Number" ,"#").
        setDbName("PM_NO").
        setSize(15).
        setReadOnly().
        setCustomValidation("CONN_PM_NO,CONN_PM_REVISION","CONN_PM_NO,CONN_PM_REVISION").
        setLabel("ACTIVEROUNDCONNPMNO: PM No");

        itemblk6.addField("CONN_PM_REVISION").
        setDbName("PM_REVISION").
        setSize(15).
        setReadOnly().
        setLabel("ACTIVEROUNDCONNPMREV: PM Revision");

        itemblk6.addField("CONN_PM_JOB_ID", "Number").
        setDbName("PM_JOB_ID"). 
        setSize(15).
        setReadOnly().
        setDynamicLOV("PM_ACTION_JOB", "CONN_PM_NO PM_NO, CONN_PM_REVISION PM_REVISION").
        setLabel("ACTIVEROUNDCONNPMJOBID: PM Job ID");
        //(+) Bug 66406, End

        itemblk6.addField("DATE_FROM", "Datetime").
        setSize(20).
        setLabel("ACTROUNDITEM6DATEFROM: Date From").
        setInsertable();

        itemblk6.addField("DATE_TO", "Datetime").
        setSize(20).
        setLabel("ACTROUNDITEM6DATETO: Date To").
        setInsertable();

        itemblk6.addField("STD_JOB_FLAG", "Number").
        setHidden().
        setCustomValidation("ITEM6_WO_NO,JOB_ID,STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION", "N_JOB_EXIST,S_STD_JOB_EXIST,N_ROLE_EXIST,N_MAT_EXIST,N_TOOL_EXIST,N_PLANNING_EXIST,N_DOC_EXIST,S_STD_JOB_ID,S_STD_JOB_CONTRACT,S_STD_JOB_REVISION,N_QTY,S_AGREEMENT_ID").
        setInsertable();

        itemblk6.addField("KEEP_CONNECTIONS").
        setHidden().
        setSize(3).
        setInsertable();

        itemblk6.addField("RECONNECT").
        setHidden().
        setSize(3).
        setInsertable();

        // -----------------------------------------------------------------------
        // -----------------------  Hidden Fields --------------------------------
        // -----------------------------------------------------------------------

        itemblk6.addField("N_JOB_EXIST", "Number").
        setFunction("0").
        setHidden();

        itemblk6.addField("S_STD_JOB_EXIST").
        setFunction("''").
        setHidden();

        itemblk6.addField("N_ROLE_EXIST", "Number").
        setFunction("0").
        setHidden();

        itemblk6.addField("N_MAT_EXIST", "Number").
        setFunction("0").
        setHidden();

        itemblk6.addField("N_TOOL_EXIST", "Number").
        setFunction("0").
        setHidden();

        itemblk6.addField("N_PLANNING_EXIST", "Number").
        setFunction("0").
        setHidden();

        itemblk6.addField("N_DOC_EXIST", "Number").
        setFunction("0").
        setHidden();

        itemblk6.addField("S_STD_JOB_ID").
        setFunction("''").
        setHidden();

        itemblk6.addField("S_STD_JOB_CONTRACT").
        setFunction("''").
        setHidden();

        itemblk6.addField("S_STD_JOB_REVISION").
        setFunction("''").
        setHidden();

        itemblk6.addField("N_QTY", "Number").
        setFunction("0").
        setHidden();

        itemblk6.addField("S_AGREEMENT_ID").
        setFunction("''").
        setHidden();

        itemblk6.addField("IS_SEPARATE").
        setFunction("''").
        setHidden();

        itemblk6.setView("WORK_ORDER_JOB");
        itemblk6.defineCommand("WORK_ORDER_JOB_API","New__,Modify__,Remove__");
        itemblk6.setMasterBlock(headblk);

        itemset6 = itemblk6.getASPRowSet();

        itemtbl6 = mgr.newASPTable(itemblk6);
        itemtbl6.setTitle(mgr.translate("ACTROUNDITEM6WOJOBS: Jobs"));
        itemtbl6.setWrap();

        itembar6 = mgr.newASPCommandBar(itemblk6);
        itembar6.enableCommand(itembar6.FIND);

        itembar6.defineCommand(itembar6.NEWROW, "newRowITEM6");
        itembar6.defineCommand(itembar6.SAVERETURN, "saveReturnItem6", "checkITEM6SaveParams(i)");
        itembar6.defineCommand(itembar6.OKFIND,"okFindITEM6");
        itembar6.defineCommand(itembar6.COUNTFIND,"countFindITEM6");
        itembar6.defineCommand(itembar6.DELETE, "deleteITEM6", "checkStdJobDeleteParams(i)");

        itemlay6 = itemblk6.getASPBlockLayout();
        itemlay6.setDefaultLayoutMode(itemlay6.MULTIROW_LAYOUT);

        // ----------------------------------------------------------------------------------------
        // -------------------------------------    RMBBLK  ---------------------------------------
        // ----------------------------------------------------------------------------------------

        prntblk = mgr.newASPBlock("RMBBLK");
        prntblk.addField("ATTR0");
        prntblk.addField("ATTR1");
        prntblk.addField("ATTR2");
        prntblk.addField("ATTR3");
        prntblk.addField("ATTR4");
        prntblk.addField("RESULT_KEY");
    }

    /*
    public void checkObjAvaileble()
    {
        if (!again)
        {
            ASPManager mgr = getASPManager();

            ASPBuffer availObj;
            trans.clear();
            trans.addPresentationObjectQuery("INVENW/InventoryTransactionHist2Qry.page");
            trans = mgr.perform(trans);

            availObj = trans.getSecurityInfo();

            if (availObj.namedItemExists("INVENW/InventoryTransactionHist2Qry.page"))
                actEna0 = true;

            again = true;
        }
    }
    */

    // 031210  ARWILK  Begin  (Replace blocks with tabs)
    public void activateGeneral()
    {
        tabs.setActiveTab(1);
    }

    public void activateTimeReport()
    {
        tabs.setActiveTab(2);
    }

    public void activateMaterial()
    {
        tabs.setActiveTab(3);
    }

    public void activatePermits()
    {
        tabs.setActiveTab(4);
    }

    public void activatePostings()
    {
        tabs.setActiveTab(5);
        this.okFindITEM4();
    }

    public void activateJobs()
    {
        tabs.setActiveTab(6);
    }
    // 031210  ARWILK  End  (Replace blocks with tabs)

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        //Call Id : 114222
        if (headset.countRows() > 0 && headlay.isSingleLayout() )
        {
            if (itemset.countRows()==0 && (!matSingleMode))
            {
                if (mgr.isPresentationObjectInstalled("invenw/InventoryPartAvailabilityPlanningQry.page"))
                    itembar.removeCustomCommand("availDetail");
                if (mgr.isPresentationObjectInstalled("purchw/PurchasePartSupplier.page"))
                    itembar.removeCustomCommand("supPerPart");
                itembar.removeCustomCommand("reserve");
                itembar.removeCustomCommand("manReserve");
                itembar.removeCustomCommand("unreserve");
                if (mgr.isPresentationObjectInstalled("invenw/InventoryPartInStockOvw.page"))
                    itembar.removeCustomCommand("availtoreserve");
                itembar.removeCustomCommand("issue");
                itembar.removeCustomCommand("issueFromInvent");
                itembar.removeCustomCommand("manIssue");
                itembar.removeCustomCommand("matReqUnissue");
                itembar.removeCustomCommand("matReqIssued"); 
                if (mgr.isPresentationObjectInstalled("mpccow/PreAccountingDlg.page"))
                    itembar.removeCustomCommand("prePostHead");

            }
        }
        //

        // 031210  ARWILK  Begin  (Replace blocks with tabs)
        headbar.removeCustomCommand("activateGeneral");
        headbar.removeCustomCommand("activateTimeReport");
        headbar.removeCustomCommand("activateMaterial");
        headbar.removeCustomCommand("activatePermits");
        headbar.removeCustomCommand("activatePostings");
        headbar.removeCustomCommand("activateJobs");
        // 031210  ARWILK  End  (Replace blocks with tabs)
        if (itemset4.countRows() == 0)
        {
            itembar4.removeCustomCommand("authorizeAll");
            itembar4.removeCustomCommand("authorize");
            itembar4.removeCustomCommand("authorizeCorrect");
        }
        
        trans.clear();
        if (itemset4.countRows() > 0 && headset.countRows()>0)
            {

            cmd = trans.addCustomFunction("GETAUTHALLOWED","Work_Order_Coding_API.Is_Authorisation_Allowed","AUTH_ALLOWED");
            cmd.addParameter("WO_NO", headset.getRow().getValue("WO_NO"));

            cmd = trans.addCustomFunction("GETANYUNAUTH","Work_Order_Coding_API.Check_Any_Unauth_Rows","HAS_UNAUTHORIZED");
            cmd.addParameter("WO_NO", headset.getRow().getValue("WO_NO"));

            trans = mgr.perform(trans); 
            
            ctxIsAuthAllowed = ("TRUE".equals(trans.getValue("GETAUTHALLOWED/DATA/AUTH_ALLOWED")));
            ctxHasUnauthorized = ("TRUE".equals(trans.getValue("GETANYUNAUTH/DATA/HAS_UNAUTHORIZED")));
            if (ctxIsAuthAllowed)
            {
                if (ctxHasUnauthorized)
                {      
                    itembar4.enableCustomCommand("authorizeAll");
                    itembar4.enableCustomCommand("authorize");
                }
                else
                {
                    itembar4.disableCustomCommand("authorizeAll");
                    itembar4.disableCustomCommand("authorize");
                }
            }
            else
            {
                itembar4.disableCustomCommand("authorizeAll");
                itembar4.disableCustomCommand("authorize");
            }
        }

    /*    if (saveitem1==true)
            okFindITEM4();   */

        if (headset.countRows() > 0)
        {
            headCont = headset.getRow().getValue("CONTRACT");
            headComp = headset.getRow().getValue("COMPANY");
            headWoNo = headset.getRow().getValue("WO_NO");
        }

        if (itemlay0.isMultirowLayout()|| itemlay0.isSingleLayout())
            itembar0.enableCommand(itembar0.FIND);

        if (headset.countRows()>0)
            comp = headset.getRow().getValue("COMPANY");

        if (itemlay1.isFindLayout())
            isFind = true;

        if (headlay.isMultirowLayout())
        {
            mgr.getASPField("REPORTED_BY").unsetHidden();
            mgr.getASPField("VENDORNAME").unsetHidden();
            mgr.getASPField("FIXED_PRICE").unsetHidden();
            mgr.getASPField("DOCUMENT").unsetHidden();
            mgr.getASPField("ORG_CODE").unsetHidden();
            mgr.getASPField("PLAN_S_DATE").unsetHidden();
            mgr.getASPField("REAL_S_DATE").unsetHidden();
            mgr.getASPField("PLAN_F_DATE").unsetHidden();
            mgr.getASPField("PLAN_HRS").unsetHidden();

            mgr.getASPField("ORG_CODE").unsetReadOnly(); 

            mgr.getASPField("REPORTED_BY").setReadOnly();
            mgr.getASPField("VENDORNAME").setReadOnly();
            mgr.getASPField("CONTACT").setReadOnly();
            mgr.getASPField("REFERENCE_NO").setReadOnly();
            mgr.getASPField("PHONE_NO").setReadOnly();
            mgr.getASPField("FIXED_PRICE").setReadOnly();
            mgr.getASPField("ADDRESS1").setReadOnly();
            mgr.getASPField("ADDRESS2").setReadOnly();
            mgr.getASPField("ADDRESS3").setReadOnly();
            mgr.getASPField("ADDRESS4").setReadOnly();
            mgr.getASPField("ADDRESS5").setReadOnly();
            mgr.getASPField("ADDRESS6").setReadOnly();
            mgr.getASPField("DOCUMENT").setReadOnly();
        }
        else
        {
            mgr.getASPField("REPORTED_BY").unsetReadOnly();
            mgr.getASPField("VENDORNAME").unsetReadOnly();
            mgr.getASPField("CONTACT").unsetReadOnly();
            mgr.getASPField("REFERENCE_NO").unsetReadOnly();
            mgr.getASPField("PHONE_NO").unsetReadOnly();
            mgr.getASPField("FIXED_PRICE").unsetReadOnly();
            mgr.getASPField("ADDRESS1").unsetReadOnly();
            mgr.getASPField("ADDRESS2").unsetReadOnly();
            mgr.getASPField("ADDRESS3").unsetReadOnly();
            mgr.getASPField("ADDRESS4").unsetReadOnly();
            mgr.getASPField("ADDRESS5").unsetReadOnly();
            mgr.getASPField("ADDRESS6").unsetReadOnly();
            mgr.getASPField("DOCUMENT").unsetReadOnly();

            mgr.getASPField("REPORTED_BY").setHidden();
            mgr.getASPField("REPORT_IN_BY").setHidden();
            mgr.getASPField("PRE_ACCOUNTING_ID").setHidden();
        }

        if (headset.countRows() != 0)
        {
            setStringLables();      
            mgr.createSearchURL(headblk);

            mgr.getASPField("WO_NO").setReadOnly();
            mgr.getASPField("ROUNDDEF_ID").setReadOnly();
            mgr.getASPField("ROUNDDEFDESCRIPTION").setReadOnly();
            mgr.getASPField("CONTRACT").setReadOnly();
            mgr.getASPField("STATE").setReadOnly();
            mgr.getASPField("PLAN_HRS").setReadOnly();
            mgr.getASPField("VENDOR_NO").setReadOnly();
            mgr.getASPField("CALL_CODE").setReadOnly();
            mgr.getASPField("AGREEMENT_ID").setReadOnly();

            mgr.getASPField("PM_ORDER_NO").setReadOnly();
            mgr.getASPField("ROUND_REPORT_IN_STATUS").setReadOnly();
            mgr.getASPField("MCH_CODE").setReadOnly();
            mgr.getASPField("OBJECTDESCRIPTION").setReadOnly();
            mgr.getASPField("ITEM0_CONTRACT").setReadOnly();
            mgr.getASPField("TEST_POINT_ID").setReadOnly();
            mgr.getASPField("MSEQOBJECTTESTPOINTDESCRIPTIO").setReadOnly();
            mgr.getASPField("MSEQOBJECTTESTPOINTLOCATION").setReadOnly();
            mgr.getASPField("ACTION_CODE_ID").setReadOnly();
            mgr.getASPField("ACTCODEIDDESCRIPTION").setReadOnly();

            mgr.getASPField("LINE_ITEM_NO").setReadOnly();
            mgr.getASPField("PART_NO").setReadOnly();
            mgr.getASPField("SPAREDESCRIPTION").setReadOnly();
            mgr.getASPField("SPARE_CONTRACT").setReadOnly();
            mgr.getASPField("HASSPARESTRUCTURE").setReadOnly();
            mgr.getASPField("DIMQTY").setReadOnly();
            mgr.getASPField("TYPEDESIGN").setReadOnly();
            mgr.getASPField("QTYONHAND").setReadOnly();
            mgr.getASPField("QTY_ASSIGNED").setReadOnly();
            mgr.getASPField("UNITMEAS").setReadOnly();

            mgr.getASPField("PERMIT_SEQ").setReadOnly();
            mgr.getASPField("PERMIT_TYPE_ID").setReadOnly();
            mgr.getASPField("PERMITDESCRIPTION").setReadOnly();

            mgr.getASPField("ORDER_NO").setReadOnly();
            mgr.getASPField("SALESPRICEAMOUNT").setReadOnly();
            mgr.getASPField("ITEM4_AGREEMENT_PRICE_FLAG").setReadOnly(); 
            mgr.getASPField("ITEM4_WORK_ORDER_BOOK_STATUS").setReadOnly();
            mgr.getASPField("SIGNATURE").setReadOnly();
            mgr.getASPField("CDOCUMENTTEXT").setReadOnly();
            mgr.getASPField("REQUISITION_NO").setReadOnly();
            mgr.getASPField("REQUISITION_LINE_NO").setReadOnly();
            mgr.getASPField("REQUISITION_RELEASE_NO").setReadOnly();
            mgr.getASPField("REQUISITIONVENDORNO").setReadOnly();
            mgr.getASPField("DOCUMENT").setReadOnly();

            if (itemset1.countRows()>0)
            {

                strBookingStatus = itemset1.getRow().getValue("WORK_ORDER_BOOK_STATUS");
                if (mgr.isEmpty(strBookingStatus))
                {
                    mgr.getASPField("CRE_DATE").setReadOnly();
                    mgr.getASPField("EMP_NO").unsetReadOnly();
                    mgr.getASPField("EMP_SIGNATURE").setReadOnly();
                    mgr.getASPField("NAME").setReadOnly();
                    mgr.getASPField("ITEM1_ORG_CODE").unsetReadOnly();
                    mgr.getASPField("ITEM1_CONTRACT").setReadOnly();
                    mgr.getASPField("ITEM1_ROLE_CODE").unsetReadOnly();
                    mgr.getASPField("CATALOG_NO").unsetReadOnly();
                    mgr.getASPField("SALESPARTCATALOGDESC").unsetReadOnly();
                    mgr.getASPField("SALESPARTCOST").setReadOnly();
                    mgr.getASPField("LIST_PRICE").unsetReadOnly();
                    mgr.getASPField("QTY").unsetReadOnly();
                    mgr.getASPField("AMOUNT").unsetReadOnly();
                    mgr.getASPField("AMOUNTSALES").unsetReadOnly();
                    mgr.getASPField("CMNT").unsetReadOnly();
                }
                else
                {
                    mgr.getASPField("CRE_DATE").setReadOnly();
                    mgr.getASPField("EMP_NO").setReadOnly();
                    mgr.getASPField("EMP_SIGNATURE").setReadOnly();
                    mgr.getASPField("NAME").setReadOnly();
                    mgr.getASPField("ITEM1_ORG_CODE").setReadOnly();
                    mgr.getASPField("ITEM1_CONTRACT").setReadOnly();
                    mgr.getASPField("ITEM1_ROLE_CODE").setReadOnly();
                    mgr.getASPField("CATALOG_NO").setReadOnly();
                    mgr.getASPField("SALESPARTCATALOGDESC").setReadOnly();
                    mgr.getASPField("SALESPARTCOST").setReadOnly();
                    mgr.getASPField("LIST_PRICE").setReadOnly();
                    mgr.getASPField("QTY").setReadOnly();
                    mgr.getASPField("AMOUNT").setReadOnly();
                    mgr.getASPField("AMOUNTSALES").setReadOnly();
                    mgr.getASPField("CMNT").setReadOnly();
                }   
            }
        }

        if (headlay.isSingleLayout() && headset.countRows()>0)
        {
            title = mgr.translate("PCMWACTIVEROUNDREPTINRWOH: Report in Route Work Order - ");
            xx = headset.getRow().getValue("WO_NO");
            yy = headset.getRow().getValue("ROUNDDEFDESCRIPTION");
        }
        else
        {
            title = mgr.translate("PCMWACTIVEROUNDREPTINRWO: Report in Route Work Order");
            xx = "";
            yy = "";
        }

        var1 = ""; var2 = ""; var3 = ""; var4 = ""; var5 = ""; var6 = "";   

        fldTitleEmpNo = mgr.translate("PCMWACTIVEROUNDEMPNOFLD: Employee+ID");
        fldTitleTestPointId = mgr.translate("PCMWACTIVEROUNDTESTPOINTFLD: Testpoint");
        fldTitleItem4CatalogNo = mgr.translate("PCMWACTIVEROUNDITEM4CATFLD: Sales+Part+Number");
        fldTitleMchCode = mgr.translate("PCMWACTIVEROUNDMCHCODEFLD: Object+ID");
        fldTitleSignature = mgr.translate("PCMWACTIVEROUNDSIGNATUREFLD: Auth+Signature");
        fldTitleRequisitionNo = mgr.translate("PCMWACTIVEROUNDREQUISITIONFLD: Requisition+No");

        lovTitleEmpNo = mgr.translate("PCMWACTIVEROUNDEMPNOLOV: List+of+Employee+ID");
        lovTitleTestPointId = mgr.translate("PCMWACTIVEROUNDTESTPOINTLOV: List+of+Testpoint");
        lovTitleItem4CatalogNo = mgr.translate("PCMWACTIVEROUNDITEM4CATLOV: List+of+Sales+Part+Number");
        lovTitleMchCode = mgr.translate("PCMWACTIVEROUNDMCHCODELOV: List+of+Object+ID");
        lovTitleSignature = mgr.translate("PCMWACTIVEROUNDSIGNATURELOV: List+of+Auth+Signature");
        lovTitleRequisitionNo = mgr.translate("PCMWACTIVEROUNDREQUISITIONLOV: List+of+Requisition+No");

        if (matSingleMode || "TRUE".equals(showMat))
        {
            itemlay2.setLayoutMode(itemlay2.SINGLE_LAYOUT);
            matLine = ctx.getGlobal("CURRROWGLOBAL");
            itemset2.goTo(toInt(matLine));
            okFindITEM5();
            matSingleMode = false;
            activateMaterial();
        }

        if ((itemlay.isVisible()&& itemset2.countRows()>0) || "TRUE".equals(showMat))
        {
            String mat_state = itemset2.getRow().getFieldValue("ITEM2_STATE");

            trans.clear();

            cmd = trans.addCustomFunction("TRANSLATEDCLOSE","MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__","DB_STATE"); 
            cmd.addParameter("DB_STATE","Closed"); 

            trans = mgr.perform(trans);

            String db_mat_state = trans.getValue("TRANSLATEDCLOSE/DATA/DB_STATE");

            if (db_mat_state.equals(mat_state))
            {
                itembar.disableCommand(itembar.NEWROW);
                itembar.disableCommand(itembar.DELETE);
                itembar.disableCommand(itembar.DUPLICATEROW);
                itembar.disableCommand(itembar.EDITROW);
            }
        }

        if (!secCheck)
        {
            actionSecurityCheck();
            secCheck = true;
        }

        //Hyperlinks to Forms in other Modules
        if ((ctxHypCustInfo) && mgr.isPresentationObjectInstalled("enterw/CustomerInfo.page"))
            mgr.getASPField("CUSTOMER_NO").setHyperlink("../enterw/CustomerInfo.page","CUSTOMER_NO","NEWWIN");
        if ((ctxHypDocRef) && mgr.isPresentationObjectInstalled("docmaw/DocReference.page"))
            mgr.getASPField("DOCUMENT").setHyperlink("../docmaw/DocReference.page","LU_NAME,KEY_REF","NEWWIN");
        if ((ctxHypInvenPart) && mgr.isPresentationObjectInstalled("invenw/InventoryPart.page"))
            mgr.getASPField("PART_NO").setHyperlink("../invenw/InventoryPart.page","PART_NO","NEWWIN");

        String woCostTypeDb = "";
        
        if(headset.countRows()>0 && itemset4.countRows()>0)
        {
            woCostTypeDb = itemset4.getRow().getValue("WORK_ORDER_COST_TYPE_DB");

            if ("Personal".equals(headset.getRow().getValue("WORK_ORDER_COST_TYPE")))
            {
                if (headlay.isSingleLayout())
                    headbar.disableCommand(headbar.EDITROW);
            } 

            if (!mgr.isEmpty(itemset4.getRow().getValue("TRANSACTION_ID")))
            {
                if ("M".equals(woCostTypeDb))
                {
                    if ((ctxTransactionId) && mgr.isPresentationObjectInstalled("invenw/InventoryTransactionHist2Qry.page"))
                        mgr.getASPField("TRANSACTION_ID").setHyperlink("../invenw/InventoryTransactionHist2Qry.page","SEARCH,TRANSACTION_ID","NEWWIN");
                }
                else if (("P".equals(woCostTypeDb)) || ("T".equals(woCostTypeDb)))
                {
                    if ((ctxTransactionId) && mgr.isPresentationObjectInstalled("pcmw/WoTimeTransactionHist.page"))
                        mgr.getASPField("TRANSACTION_ID").setHyperlink("../pcmw/WoTimeTransactionHist.page","TRANSACTION_ID","NEWWIN");
                }
            }
        }
        
        disableCommands();

        if (itemlay.isVisible())
        {
            itembar.removeCustomCommand("issueFromInvent");
        }

        //Bug 66456, Start, Added check on PROJ
        if (!mgr.isPresentationObjectInstalled("projw/ConObjToActivityDlg.page") && mgr.isModuleInstalled("PROJ"))
        {
            headbar.disableCustomCommand("connectToActivity");
            headbar.disableCustomCommand("disconnectFromActivity");
            headbar.disableCustomCommand("activityInfo");
        }
        //Bug 66456, End

        if (headset.countRows()>0 && itemlay6.isVisible())
        {
            String sWhereStrForITEM6 = "CONTRACT = '" + headset.getValue("CONTRACT") + "'";

            if (itemlay6.isFindLayout())
            {
                mgr.getASPField("STD_JOB_ID").setLOV("StandardJobLov1.page", 600, 450);
                sWhereStrForITEM6 = sWhereStrForITEM6 + " AND STANDARD_JOB_TYPE_DB = '2'";
            }

            mgr.getASPField("STD_JOB_ID").setLOVProperty("WHERE", sWhereStrForITEM6);
        }
        if (headset.countRows()>0 && itemlay2.isVisible())
        {
            mgr.getASPField("ITEM2_CONTRACT").setLOVProperty("WHERE","Site_API.Get_Company(CONTRACT) = '"+headset.getValue("COMPANY")+"'");
        }
         if (!mgr.isEmpty(mgr.getQueryStringValue("IS_POSTING_ACTIVATE"))) {
             this.activatePostings();
         }
         
         //Removed the scroll buttons of the parent when the child tabs is in new or edit mode
         if ( itemlay0.isNewLayout() || itemlay0.isEditLayout() || itemlay1.isNewLayout() || itemlay1.isEditLayout() ||
                   itemlay2.isNewLayout() || itemlay2.isEditLayout() || itemlay3.isNewLayout() || itemlay3.isEditLayout() ||
              itemlay4.isNewLayout() || itemlay4.isEditLayout() || itemlay6.isNewLayout() || itemlay6.isEditLayout() ||
              itemlay.isNewLayout()  || itemlay.isEditLayout() )
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
         
         if ( itemlay.isNewLayout()  || itemlay.isEditLayout() ) {
             itembar2.disableCommand(headbar.DELETE);
                  itembar2.disableCommand(headbar.NEWROW);
                  itembar2.disableCommand(headbar.EDITROW);
                  itembar2.disableCommand(headbar.DELETE);
                  itembar2.disableCommand(headbar.DUPLICATEROW);
                  itembar2.disableCommand(headbar.FIND);
                  itembar2.disableCommand(headbar.BACKWARD);
                  itembar2.disableCommand(headbar.FORWARD);
         }
         //(+) Bug 66406, Start
         if (itemlay6.isFindLayout())
            mgr.getASPField("CONN_PM_NO").setLOV("../pcmw/PmActionLov1.page",600,450);
         else if (itemlay6.isNewLayout()) 
            mgr.getASPField("CONN_PM_NO").setLOV("../pcmw/PmActionLov.page",600,450);
         //(+) Bug 66406, End 

    }

    public void actionSecurityCheck()
    {
        ASPManager mgr = getASPManager();                     

        trans = mgr.newASPTransactionBuffer();              
        trans.addSecurityQuery("WORK_ORDER_REQUIS_HEADER,ACTIVE_ROUND_WO_PRINT_REP,WORK_ORDER_CODING,ACTIVE_ROUND_ACTION,ACTIVE_SEPARATE,HISTORICAL_SEPARATE,PM_ACTION,PRE_ACCOUNTING,MAINTENANCE_OBJECT,EQUIPMENT_SPARE_STRUC_DISTINCT,INVENTORY_PART_IN_STOCK_NOPAL,WORK_ORDER_PART_ALLOC,INVENTORY_PART_CONFIG,PURCHASE_PART_SUPPLIER,MAINT_MATERIAL_REQUISITION,PERMIT_TYPE,WORK_ORDER_CODING,CUSTOMER_ORDER,INVENTORY_PART_IN_STOCK,INVENTORY_TRANSACTION_HIST,WORK_ORDER_BUDGET");                   
        trans.addSecurityQuery("MAINT_MATERIAL_REQUISITION_RPI","Report_Printout");                   
        trans.addSecurityQuery("MAINT_MATERIAL_REQ_LINE_API","Make_Reservation_Short"); 
        trans.addSecurityQuery("MAINT_MATERIAL_REQ_LINE_API","Make_Auto_Issue_Detail");                          
        trans.addSecurityQuery("MAINT_MATERIAL_REQ_LINE_API","Make_Manual_Issue_Detail");                          
        trans.addSecurityQuery("Work_Order_Coding_API","Authorize");
        trans.addPresentationObjectQuery("PCMW/WorkOrderRequisHeaderRMB.page,PCMW/ActiveWorkOrder4.page,PCMW/ActiveSeparate.page,PCMW/ActiveSeparate3.page,PCMW/QuickReportInWorkDlg.page,PCMW/PmActionRound.page,MPCCOW/PreAccountingDlg.page,PCMW/MaintenanceObject2.page,PCMW/MaintenaceObject3.page,EQUIPW/EquipmentSpareStructure2.page,PCMW/MaterialRequisReservatDlg.page,PCMW/InventoryPartLocationDlg.page,PCMW/MaterialRequisReservatDlg2.page,INVENW/InventoryPartAvailabilityPlanningQry.page,PURCHW/PurchasePartSupplier.page,PCMW/ActiveWorkOrder.page,PCMW/PermitTypeRMB.page,PCMW/AuthorizeCodingDlg.page,PCMW/WorkOrderCodingDlg.page,PCMW/AuthorizeCodingDlgSM.page,ENTERW/CustomerInfo.page,DOCMAW/DocReference.page,INVENW/InventoryPart.page,INVENW/InventoryPartInStockOvw.page,PCMW/ActiveWorkOrderIssue.page,INVENW/InventoryTransactionHist2Qry.page,PCMW/WoTimeTransactionHist.page,PCMW/RoundWorkOrderBudget.page");

        trans = mgr.perform(trans); 
        ASPBuffer secBuff = trans.getSecurityInfo();

        if (secBuff.itemExists("PRE_ACCOUNTING") && secBuff.namedItemExists("MPCCOW/PreAccountingDlg.page"))
            ctxPreposting = true;
        if (secBuff.itemExists("WORK_ORDER_REQUIS_HEADER") && secBuff.namedItemExists("PCMW/WorkOrderRequisHeaderRMB.page"))
            ctxRequisitions = true;
        if (secBuff.itemExists("ACTIVE_ROUND_WO_PRINT_REP"))
            ctxPrintWO = true;
        if (secBuff.itemExists("WORK_ORDER_CODING") && secBuff.namedItemExists("PCMW/ActiveWorkOrder4.page"))
            ctxTransferToCusOrder  = true;
        if (secBuff.itemExists("ACTIVE_ROUND_ACTION"))
        {
            ctxCompleted  = true;
            ctxNotCompleted = true;
        }
        if (secBuff.itemExists("ACTIVE_SEPARATE") && secBuff.namedItemExists("PCMW/ActiveSeparate.page"))
            ctxFaultReport = true;
        if (secBuff.itemExists("ACTIVE_SEPARATE") && secBuff.namedItemExists("PCMW/ActiveSeparate3.page"))
            ctxServiceRequest = true;
        if (secBuff.itemExists("HISTORICAL_SEPARATE") && secBuff.namedItemExists("PCMW/QuickReportInWorkDlg.page"))
            ctxQuickReport = true;
        if (secBuff.itemExists("PM_ACTION") && secBuff.namedItemExists("PCMW/PmActionRound.page"))
            ctxRoutePMAction = true;
        if (secBuff.itemExists("PRE_ACCOUNTING") && secBuff.namedItemExists("MPCCOW/PreAccountingDlg.page"))
            ctxPrePostHead = true;
        if (secBuff.itemExists("MAINTENANCE_OBJECT") && secBuff.namedItemExists("PCMW/MaintenanceObject2.page"))
            ctxSparePartObject = true;
        if (secBuff.itemExists("MAINTENANCE_OBJECT") && secBuff.namedItemExists("PCMW/MaintenaceObject3.page"))
            ctxObjStructure = true;
        if (secBuff.itemExists("EQUIPMENT_SPARE_STRUC_DISTINCT") && secBuff.namedItemExists("EQUIPW/EquipmentSpareStructure2.page"))
            ctxDetchedPartList = true;
        if (secBuff.itemExists("INVENTORY_PART_IN_STOCK_NOPAL") && secBuff.namedItemExists("PCMW/MaterialRequisReservatDlg.page"))
            ctxManReserve = true;
        if (secBuff.itemExists("INVENTORY_PART_IN_STOCK_NOPAL") && secBuff.namedItemExists("PCMW/InventoryPartLocationDlg.page") && secBuff.itemExists("MAINT_MATERIAL_REQ_LINE_API.Make_Manual_Issue_Detail"))
            ctxManIssue = true;
        if (secBuff.itemExists("WORK_ORDER_PART_ALLOC") && secBuff.namedItemExists("PCMW/MaterialRequisReservatDlg2.page"))
            ctxUnreserve = true;
        if (secBuff.itemExists("INVENTORY_PART_CONFIG") && secBuff.namedItemExists("INVENW/InventoryPartAvailabilityPlanningQry.page"))
            ctxAvailDetail = true;
        if (secBuff.itemExists("PURCHASE_PART_SUPPLIER") && secBuff.namedItemExists("PURCHW/PurchasePartSupplier.page"))
            ctxSupPerPart = true;
        if (secBuff.itemExists("MAINT_MATERIAL_REQUISITION") && secBuff.namedItemExists("PCMW/ActiveWorkOrder.page"))
            ctxMatReqUnissue = true;
        if (secBuff.itemExists("PERMIT_TYPE") && secBuff.namedItemExists("PCMW/PermitTypeRMB.page"))
            ctxPermitAttr = true;
        if (secBuff.itemExists("WORK_ORDER_CODING") && secBuff.namedItemExists("PCMW/AuthorizeCodingDlg.page"))
            ctxAuthorizeAll = true;
        if (secBuff.itemExists("WORK_ORDER_CODING") && secBuff.namedItemExists("PCMW/WorkOrderCodingDlg.page"))
            ctxManageSalesRevenues = true;
        if (secBuff.itemExists("WORK_ORDER_CODING") && secBuff.itemExists("CUSTOMER_ORDER") && secBuff.namedItemExists("PCMW/AuthorizeCodingDlgSM.page"))
            ctxAuthorizeCorrect = true;
        if (secBuff.itemExists("MAINT_MATERIAL_REQUISITION_RPI.Report_Printout"))
            ctxPrintPicList = true;
        if (secBuff.itemExists("MAINT_MATERIAL_REQ_LINE_API.Make_Reservation_Short"))
            ctxReserve = true;
        if (secBuff.itemExists("MAINT_MATERIAL_REQ_LINE_API.Make_Auto_Issue_Detail"))
            ctxIssue = true;
        if (secBuff.itemExists("Work_Order_Coding_API.Authorize") && secBuff.namedItemExists("PCMW/AuthorizeCodingDlg.page"))
            ctxAuthorize = true;
        if (secBuff.namedItemExists("ENTERW/CustomerInfo.page"))
            ctxHypCustInfo = true;
        if (secBuff.namedItemExists("DOCMAW/DocReference.page"))
            ctxHypDocRef = true;
        if (secBuff.namedItemExists("INVENW/InventoryPart.page"))
            ctxHypInvenPart = true;
        if (secBuff.itemExists("INVENTORY_PART_IN_STOCK") && secBuff.namedItemExists("INVENW/InventoryPartInStockOvw.page"))
            ctxAvailReserve = true;

        if(secBuff.namedItemExists("INVENW/InventoryTransactionHist2Qry.page"))
            ctxTransactionId = true;
        if(secBuff.namedItemExists("PCMW/WoTimeTransactionHist.page"))
            ctxTransactionId = true; 

        if (secBuff.itemExists("INVENTORY_TRANSACTION_HIST") && secBuff.namedItemExists("PCMW/ActiveWorkOrderIssue.page"))
            ctxMatReqIssued = true;

        if (secBuff.itemExists("WORK_ORDER_BUDGET") && secBuff.namedItemExists("PCMW/RoundWorkOrderBudget.page"))
            ctxBudget = true;
    }

    public void disableCommands()
    {
        ASPManager mgr = getASPManager();                     

        if (!ctxPreposting)
            headbar.disableCustomCommand("preposting");
        if (!ctxRequisitions)
            headbar.disableCustomCommand("requisitions");
        if (!ctxBudget)
            headbar.disableCustomCommand("budget");
        if (!ctxPrintWO)
            headbar.disableCustomCommand("printWO");
        if (!ctxTransferToCusOrder)
            headbar.disableCustomCommand("transferToCusOrder");
        if (!ctxCompleted)
            itembar0.disableCustomCommand("completed");
        if (!ctxNotCompleted)
            itembar0.disableCustomCommand("notCompleted");
        if (!ctxFaultReport)
            itembar0.disableCustomCommand("faultReport");
        if (!ctxServiceRequest)
            itembar0.disableCustomCommand("serviceRequest");
        if (!ctxQuickReport)
            itembar0.disableCustomCommand("quickReport");
        if (!ctxRoutePMAction)
            itembar0.disableCustomCommand("routePMAction");
        if (!ctxPrintPicList)
            itembar2.disableCustomCommand("printPicList");
        if (!ctxSparePartObject)
            itembar.disableCustomCommand("sparePartObject");
        if (!ctxObjStructure)
            itembar.disableCustomCommand("objStructure");
        if (!ctxDetchedPartList)
            itembar.disableCustomCommand("detchedPartList");
        if (!ctxReserve)
            itembar.disableCustomCommand("reserve");
        if (!ctxManReserve)
            itembar.disableCustomCommand("manReserve");
        if (!ctxUnreserve)
            itembar.disableCustomCommand("unreserve");
        if (!ctxIssue)
            itembar.disableCustomCommand("issue");
        if (!ctxManIssue)
            itembar.disableCustomCommand("manIssue");
        if (!ctxMatReqUnissue)
            itembar.disableCustomCommand("matReqUnissue");
        if (!ctxPermitAttr)
            itembar3.disableCustomCommand("permitAttr");
        if (!ctxAuthorize)
            itembar4.disableCustomCommand("authorize");
        if (!ctxAuthorizeAll)
            itembar4.disableCustomCommand("authorizeAll");
        if (!ctxAuthorizeCorrect)
            itembar4.disableCustomCommand("authorizeCorrect");
        if (!ctxSalesPartComp)
            itembar4.disableCustomCommand("salesPartComp");
        if (!ctxManageSalesRevenues)
            itembar4.disableCustomCommand("manageSalesRevenues");
        if (!ctxMatReqIssued)
            itembar.disableCustomCommand("matReqIssued");

        if (mgr.isPresentationObjectInstalled("mpccow/PreAccountingDlg.page"))
        {
            if (!ctxPrePostHead)
            {
                itembar2.disableCustomCommand("prePostHead");
                itembar.disableCustomCommand("prePostHead");
            }
        }
        if (mgr.isPresentationObjectInstalled("invenw/InventoryPartAvailabilityPlanningQry.page"))
        {
            if (!ctxAvailDetail)
                itembar.disableCustomCommand("availDetail");
        }
        if (mgr.isPresentationObjectInstalled("purchw/PurchasePartSupplier.page "))
        {
            if (!ctxSupPerPart)
                itembar.disableCustomCommand("supPerPart");
        }

        if (mgr.isPresentationObjectInstalled("invenw/InventoryPartInStockOvw.page"))
        {
            if (!ctxAvailReserve)
                itembar.disableCustomCommand("availtoreserve");
        }
    }

     /*
    public void checkObjAvaileble()
    {
        if (!again)
        {
            ASPManager mgr = getASPManager();

            ASPBuffer availObj;
            trans.clear();
            trans.addPresentationObjectQuery("INVENW/InventoryTransactionHist2Qry.page");
            trans = mgr.perform(trans);

            availObj = trans.getSecurityInfo();

            if (availObj.namedItemExists("INVENW/InventoryTransactionHist2Qry.page"))
                actEna0 = true;

            again = true;
        }
    } */

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return title + xx + " " + yy;
    }

    protected String getTitle()
    {
        return "PCMWACTIVEROUNDTITLE: Report in Route Work Order";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        printHiddenField("CREREPNONSER",openCreRepNonSer);
        printHiddenField("HIDDENPARTNO","");
        printHiddenField("ONCEGIVENERROR","FALSE");
        printHiddenField("ACTIVITY_SEQ_PROJ", "");
        printHiddenField("BUTTONVAL", "");
        printHiddenField("REFRESHFLAG", "");
        printHiddenField("REFRESHBLOCK", "");
        printHiddenField("CHECK_DELETE_ITEM6","FALSE");
        printHiddenField("DUMMY_KEEP_CONNECTIONS","FALSE");
	//Bug 82543, Start
	printHiddenField("REFRESH_FLAG","");
	//Bug 82543, End

        appendToHTML(headlay.show());

        if (headlay.isSingleLayout() && (headset.countRows() > 0))
        {
            appendToHTML(tabs.showTabsInit());

            if (tabs.getActiveTab() == 1)
                appendToHTML(itemlay0.show());
            else if (tabs.getActiveTab() == 2)
                appendToHTML(itemlay1.show());
            else if (tabs.getActiveTab() == 3)
            {
                appendToHTML(itemlay2.show());
                if (itemlay2.isSingleLayout() && (itemset2.countRows() > 0))
                    appendToHTML(itemlay.show());
            }
            else if (tabs.getActiveTab() == 4)
                appendToHTML(itemlay3.show());
            else if (tabs.getActiveTab() == 5)
                appendToHTML(itemlay4.show());
            else if (tabs.getActiveTab() == 6)
                appendToHTML(itemlay6.show());
        }
        // 031210  ARWILK  End  (Replace blocks with tabs)

        //Overwritten lov functions

        appendDirtyJavaScript("function lovEmpNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript(" ss = '");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(comp));     // XSS_Safe Amdilk 20070711
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("   if('");
        appendDirtyJavaScript(isFind);    // XSS_Safe Amdilk 20070712
        appendDirtyJavaScript(                                                                              "' == 'True')\n");
        appendDirtyJavaScript("      { \n");
        appendDirtyJavaScript("       openLOVWindow('EMP_NO',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_NO&__FIELD=");
        appendDirtyJavaScript(fldTitleEmpNo);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleEmpNo);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&COMPANY='+ ss ,600,445,'validateEmpNo');\n");
        appendDirtyJavaScript("      }\n");
        appendDirtyJavaScript("   else\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("      openLOVWindow('EMP_NO',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_NO&__FIELD=");
        appendDirtyJavaScript(fldTitleEmpNo);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleEmpNo);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
        appendDirtyJavaScript("           + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
        appendDirtyJavaScript("           ,600,445,'validateEmpNo');\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovTestPointId(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("        jCont = f.ITEM0_CONTRACT.value;\n");
        appendDirtyJavaScript("        jMchCode = f.MCH_CODE.value; \n");
        appendDirtyJavaScript("   openLOVWindow('TEST_POINT_ID',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EQUIPMENT_OBJECT_TEST_PNT&__FIELD=");
        appendDirtyJavaScript(fldTitleTestPointId);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleTestPointId);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&CONTRACT=' + jCont\n");
        appendDirtyJavaScript("           + '&MCH_CODE=' + jMchCode\n");
        appendDirtyJavaScript("           ,600,450,'validateTestPointId');\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovItem4CatalogNo(i)\n");
        appendDirtyJavaScript("{ \n");
        appendDirtyJavaScript("        jCont = '");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(headCont));   // XSS_Safe Amdilk 20070711
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("   openLOVWindow('ITEM4_CATALOG_NO',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=SALES_PART&__FIELD=");
        appendDirtyJavaScript(fldTitleItem4CatalogNo);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleItem4CatalogNo);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&CONTRACT=' + jCont\n");
        appendDirtyJavaScript("           ,600,450,'validateItem4CatalogNo');\n");
        appendDirtyJavaScript("}  \n");

        appendDirtyJavaScript("function lovMchCode(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("        jCont = '");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(headCont));   // XSS_Safe Amdilk 20070711
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("   openLOVWindow('MCH_CODE',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=MAINTENANCE_OBJECT&__FIELD=");
        appendDirtyJavaScript(fldTitleMchCode);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleMchCode);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&CONTRACT=' + jCont\n");
        appendDirtyJavaScript("           ,600,450,'validateMchCode');\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovSignature(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("        jHeadComp = '");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(headComp));   // XSS_Safe Amdilk 20070711 
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("   openLOVWindow('SIGNATURE',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitleSignature);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleSignature);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&COMPANY=' + jHeadComp\n");
        appendDirtyJavaScript("           ,600,450,'validateSignature');\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovAccnt(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("        if( getRowStatus_('ITEM4',i)=='QueryMode__' )\n");
        appendDirtyJavaScript("        {\n");
        appendDirtyJavaScript("     openLOVWindow('ACCNT',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=ACCOUNT&__FIELD=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(strLableA));   // Bug Id 68773
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&COMPANY= null'\n");
        appendDirtyJavaScript("           ,600,450,'validateAccnt');\n");
        appendDirtyJavaScript("   }       \n");
        appendDirtyJavaScript("   else\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("      openLOVWindow('ACCNT',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=ACCOUNT&__FIELD=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(strLableA));   // Bug Id 68773
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&COMPANY=' + URLClientEncode(getValue_('ITEM4_COMPANY',i))\n");
        appendDirtyJavaScript("           ,600,450,'validateAccnt');\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}    \n");

        appendDirtyJavaScript("function lovCostCenter(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("        if( getRowStatus_('ITEM4',i)=='QueryMode__' )\n");
        appendDirtyJavaScript("        {\n");
        appendDirtyJavaScript("      openLOVWindow('COST_CENTER',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CODE_B&__FIELD=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(strLableB));   // Bug Id 68773
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&COMPANY= null'\n");
        appendDirtyJavaScript("           ,600,450,'validateCostCenter');\n");
        appendDirtyJavaScript("   }       \n");
        appendDirtyJavaScript("   else\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("      openLOVWindow('COST_CENTER',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CODE_B&__FIELD=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(strLableB));   // Bug Id 68773
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&COMPANY=' + URLClientEncode(getValue_('ITEM4_COMPANY',i))\n");
        appendDirtyJavaScript("           ,600,450,'validateCostCenter');\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovProjectNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("        if( getRowStatus_('ITEM4',i)=='QueryMode__' )\n");
        appendDirtyJavaScript("        {\n");
        appendDirtyJavaScript("     openLOVWindow('PROJECT_NO',i,\n");
        appendDirtyJavaScript("                   '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CODE_F&__FIELD=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(strLableF));   // Bug Id 68773
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&COMPANY= null'\n");
        appendDirtyJavaScript("           ,600,450,'validateProjectNo');\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("   else\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("     openLOVWindow('PROJECT_NO',i,\n");
        appendDirtyJavaScript("                   '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CODE_F&__FIELD=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(strLableF));   // Bug Id 68773
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&COMPANY=' + URLClientEncode(getValue_('ITEM4_COMPANY',i))\n");
        appendDirtyJavaScript("           ,600,450,'validateProjectNo');\n");
        appendDirtyJavaScript("   }       \n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovObjectNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("        if( getRowStatus_('ITEM4',i)=='QueryMode__' )\n");
        appendDirtyJavaScript("        {\n");
        appendDirtyJavaScript("     openLOVWindow('OBJECT_NO',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CODE_E&__FIELD=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(strLableE));   // Bug Id 68773
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&COMPANY= null'\n");
        appendDirtyJavaScript("           ,600,450,'validateObjectNo');\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("   else\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("     openLOVWindow('OBJECT_NO',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CODE_E&__FIELD=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(strLableE));   // Bug Id 68773
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&COMPANY=' + URLClientEncode(getValue_('ITEM4_COMPANY',i))\n");
        appendDirtyJavaScript("           ,600,450,'validateObjectNo');\n");
        appendDirtyJavaScript("   }       \n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovCodeC(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("        if( getRowStatus_('ITEM4',i)=='QueryMode__' )\n");
        appendDirtyJavaScript("        {\n");
        appendDirtyJavaScript("           openLOVWindow('CODE_C',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CODE_C&__FIELD=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(strLableC));   // Bug Id 68773
        appendDirtyJavaScript("'\n"); 
        appendDirtyJavaScript("           + '&COMPANY= null'\n");
        appendDirtyJavaScript("           ,600,450,'validateCodeC');\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("   else\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("       openLOVWindow('CODE_C',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CODE_C&__FIELD=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(strLableC));   // Bug Id 68773
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&COMPANY=' + URLClientEncode(getValue_('ITEM4_COMPANY',i))\n");
        appendDirtyJavaScript("           ,600,450,'validateCodeC');\n");
        appendDirtyJavaScript("   }       \n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovCodeD(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("        if( getRowStatus_('ITEM4',i)=='QueryMode__' )\n");
        appendDirtyJavaScript("        {\n");
        appendDirtyJavaScript("          openLOVWindow('CODE_D',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CODE_D&__FIELD=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(strLableD));    // Bug Id 68773
        appendDirtyJavaScript("'\n"); 
        appendDirtyJavaScript("           + '&COMPANY= null'\n");
        appendDirtyJavaScript("           ,600,450,'validateCodeD');\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("   else\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("     openLOVWindow('CODE_D',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CODE_D&__FIELD=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(strLableD));    // Bug Id 68773
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&COMPANY=' + URLClientEncode(getValue_('ITEM4_COMPANY',i))\n");
        appendDirtyJavaScript("           ,600,450,'validateCodeD');\n");
        appendDirtyJavaScript("   }       \n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovCodeG(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("        if( getRowStatus_('ITEM4',i)=='QueryMode__' )\n");
        appendDirtyJavaScript("        {\n");
        appendDirtyJavaScript("      openLOVWindow('CODE_G',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CODE_G&__FIELD=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(strLableG));   // Bug Id 68773
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&COMPANY= null'\n");
        appendDirtyJavaScript("           ,600,450,'validateCodeG');\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("   else\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("       openLOVWindow('CODE_G',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CODE_G&__FIELD=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(strLableG));   // Bug Id 68773
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&COMPANY=' + URLClientEncode(getValue_('ITEM4_COMPANY',i))\n");
        appendDirtyJavaScript("           ,600,450,'validateCodeG');\n");
        appendDirtyJavaScript("   }       \n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovCodeH(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("       if( getRowStatus_('ITEM4',i)=='QueryMode__' )\n");
        appendDirtyJavaScript("       {\n");
        appendDirtyJavaScript("    openLOVWindow('CODE_H',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CODE_H&__FIELD=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(strLableH));   // Bug Id 68773
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&COMPANY= null'\n");
        appendDirtyJavaScript("           ,600,450,'validateCodeH');\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript("       else\n");
        appendDirtyJavaScript("       {\n");
        appendDirtyJavaScript("         openLOVWindow('CODE_H',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CODE_H&__FIELD=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(strLableH));   // Bug Id 68773
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&COMPANY=' + URLClientEncode(getValue_('ITEM4_COMPANY',i))\n");
        appendDirtyJavaScript("           ,600,450,'validateCodeH');\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovCodeI(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("       if( getRowStatus_('ITEM4',i)=='QueryMode__' )\n");
        appendDirtyJavaScript("       {\n");
        appendDirtyJavaScript("         openLOVWindow('CODE_I',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CODE_I&__FIELD=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(strLableI));   // Bug Id 68773
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&COMPANY= null'\n");
        appendDirtyJavaScript("           ,600,450,'validateCodeI');\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript("       else\n");
        appendDirtyJavaScript("       {\n");
        appendDirtyJavaScript("         openLOVWindow('CODE_I',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CODE_I&__FIELD=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(strLableI));   // Bug Id 68773
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&COMPANY=' + URLClientEncode(getValue_('ITEM4_COMPANY',i))\n");
        appendDirtyJavaScript("           ,600,450,'validateCodeI');\n");
        appendDirtyJavaScript("       }           \n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovCodeJ(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("        if( getRowStatus_('ITEM4',i)=='QueryMode__' ) \n");
        appendDirtyJavaScript("        {\n");
        appendDirtyJavaScript("     openLOVWindow('CODE_J',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CODE_J&__FIELD=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(strLableJ));   // Bug Id 68773
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&COMPANY= null'\n");
        appendDirtyJavaScript("           ,600,450,'validateCodeJ');\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("   else\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("     openLOVWindow('CODE_J',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CODE_J&__FIELD=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(strLableJ));   // Bug Id 68773
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&COMPANY=' + URLClientEncode(getValue_('ITEM4_COMPANY',i))\n");
        appendDirtyJavaScript("           ,600,450,'validateCodeJ');\n");
        appendDirtyJavaScript("   }       \n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovRequisitionNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("        jWoNo = '");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(headWoNo));   // XSS_Safe Amdilk 20070711
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("   openLOVWindow('REQUISITION_NO',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=WORK_ORDER_REQUIS_HEADER&__FIELD=");
        appendDirtyJavaScript(fldTitleRequisitionNo);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleRequisitionNo);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&WO_NO=' + jWoNo\n");
        appendDirtyJavaScript("           ,600,450,'validateRequisitionNo');\n");
        appendDirtyJavaScript("}\n");

        //Overwritten Validation functions

        appendDirtyJavaScript("function validateItem4WorkOrderCostType(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if( getRowStatus_('ITEM4',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("   setDirty();\n");
        appendDirtyJavaScript("   if( !checkItem4WorkOrderCostType(i) ) return;\n");
        appendDirtyJavaScript("   window.status='Please wait for validation';\n");
        appendDirtyJavaScript("   r = __connect(\n");
        appendDirtyJavaScript("           '");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM4_WORK_ORDER_COST_TYPE'\n");
        appendDirtyJavaScript("           + '&ITEM4_WORK_ORDER_COST_TYPE=' + getField_('ITEM4_WORK_ORDER_COST_TYPE',i).options[getField_('ITEM4_WORK_ORDER_COST_TYPE',i).selectedIndex].value\n");
        appendDirtyJavaScript("           + '&WO_NO=' + URLClientEncode(getValue_('WO_NO',i))\n");
        appendDirtyJavaScript("           );\n");
        appendDirtyJavaScript("   if( checkStatus_(r,'ITEM4_WORK_ORDER_COST_TYPE',i,'Cost Type') )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("           assignValue_('COST_CENTER',i,0);\n");
        appendDirtyJavaScript("           assignValue_('PROJECT_NO',i,1);\n");
        appendDirtyJavaScript("           assignValue_('OBJECT_NO',i,2);\n");
        appendDirtyJavaScript("           assignValue_('ITEM4_AMOUNT',i,3);\n");
        appendDirtyJavaScript("           assignValue_('CLIENTVAL1',i,4);\n");
        appendDirtyJavaScript("           assignValue_('CLIENTVAL2',i,5);\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("   if(getField_('ITEM4_WORK_ORDER_COST_TYPE',i).selectedIndex == 1)\n");
        appendDirtyJavaScript("     window.alert('");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVEROUNDREGOFPER: Registration of Personnel costs is not allowed in this job. Use Time Report tab."));
        appendDirtyJavaScript("');\n");
        appendDirtyJavaScript("   if(getField_('ITEM4_WORK_ORDER_COST_TYPE',i).selectedIndex == 2)\n");
        appendDirtyJavaScript("     window.alert('");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVEROUNDREGOFMAT: Registration of Material costs is not allowed in this job. Use Material tab."));
        appendDirtyJavaScript("');\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateEmpNo(i)\n");
        appendDirtyJavaScript("{ \n");
        appendDirtyJavaScript("    j = 0;\n");
        appendDirtyJavaScript("    if (i == -1)\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("        j = i;\n");
        appendDirtyJavaScript("        i = -1;\n");
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("   if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("   setDirty();\n");
        appendDirtyJavaScript("   if( !checkEmpNo(i) ) return;\n");
        appendDirtyJavaScript("   if( getValue_('EMP_NO',i)=='' )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("           getField_('ITEM1_ROLE_CODE',i).value = '';\n");
        appendDirtyJavaScript("           getField_('EMP_SIGNATURE',i).value = '';\n");
        appendDirtyJavaScript("           getField_('NAME',i).value = '';\n");
        appendDirtyJavaScript("           getField_('ITEM1_ORG_CODE',i).value = '';\n");
        appendDirtyJavaScript("           getField_('CATALOG_NO',i).value = '';\n");
        appendDirtyJavaScript("           getField_('SALESPARTCATALOGDESC',i).value = '';\n");
        appendDirtyJavaScript("           return;\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("   window.status='Please wait for validation';\n");
        appendDirtyJavaScript("   r = __connect(\n");
        appendDirtyJavaScript("           '");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=EMP_NO'\n");
        appendDirtyJavaScript("           + '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
        appendDirtyJavaScript("           + '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
        appendDirtyJavaScript("           + '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
        appendDirtyJavaScript("           );\n");
        appendDirtyJavaScript("   if( checkStatus_(r,'EMP_NO',i,'Employee ID') )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("           assignValue_('ITEM1_ROLE_CODE',i,0);\n");
        appendDirtyJavaScript("           assignValue_('EMP_SIGNATURE',i,1);\n");
        appendDirtyJavaScript("           assignValue_('NAME',i,2);\n");
        appendDirtyJavaScript("           assignValue_('ITEM1_ORG_CODE',i,3);\n");
        appendDirtyJavaScript("           assignValue_('CMNT',i,4);\n");
        appendDirtyJavaScript("           assignValue_('CATALOG_NO',i,5);\n");
        appendDirtyJavaScript("           assignValue_('SALESPARTCATALOGDESC',i,6);\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("    if ( j == -1 )\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("        validateItem1OrgCode(1);\n");
        appendDirtyJavaScript("        validateItem1RoleCode(1);\n");
        appendDirtyJavaScript("        validateCatalogNo(1);\n");
        appendDirtyJavaScript("        validateCmnt(1);\n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("    j = 0;\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateItem1OrgCode(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("    j = 0;\n");
        appendDirtyJavaScript("    if (i == -1)\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("        j = i;\n");
        appendDirtyJavaScript("        i = -1;\n");
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("    if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("   setDirty();\n");
        appendDirtyJavaScript("   if( !checkItem1OrgCode(i) ) return;\n");
        appendDirtyJavaScript("   window.status='Please wait for validation';\n");
        appendDirtyJavaScript("    r = __connect(\n");
        appendDirtyJavaScript("           '");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM1_ORG_CODE'\n");
        appendDirtyJavaScript("           + '&ITEM1_ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_WO_NO=' + URLClientEncode(getValue_('ITEM1_WO_NO',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
        appendDirtyJavaScript("           + '&AMOUNT=' + URLClientEncode(getValue_('AMOUNT',i))\n");
        appendDirtyJavaScript("           + '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_ROLE_CODE=' + URLClientEncode(getValue_('ITEM1_ROLE_CODE',i))\n");
        appendDirtyJavaScript("           + '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
        appendDirtyJavaScript("           + '&SALESPARTCATALOGDESC=' + URLClientEncode(getValue_('SALESPARTCATALOGDESC',i))\n");
        appendDirtyJavaScript("           + '&CMNT=' + URLClientEncode(getValue_('CMNT',i))\n");
        appendDirtyJavaScript("           + '&NAME=' + URLClientEncode(getValue_('NAME',i))\n");
        appendDirtyJavaScript("           );\n");
        appendDirtyJavaScript("   if( checkStatus_(r,'ITEM1_ORG_CODE',i,'Maintenance Organization') )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("           assignValue_('ITEM1_ORG_CODE',i,0);\n");
        appendDirtyJavaScript("           assignValue_('AMOUNT',i,1);\n");
        appendDirtyJavaScript("           assignValue_('CMNT',i,2);\n");
        appendDirtyJavaScript("           assignValue_('CATALOG_NO',i,3);\n");
        appendDirtyJavaScript("           assignValue_('SALESPARTCATALOGDESC',i,4);\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("    if (j == -1)\n");
        appendDirtyJavaScript("        validateCatalogNo(1);\n");
        appendDirtyJavaScript("        validateCmnt(1);\n"); 
        appendDirtyJavaScript("    j = 0;\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validatePriceListNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("   setDirty();\n");
        appendDirtyJavaScript("   if( !checkPriceListNo(i) ) return;\n");
        appendDirtyJavaScript("   window.status='Please wait for validation';\n");
        appendDirtyJavaScript("    r = __connect(\n");
        appendDirtyJavaScript("           '");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=PRICE_LIST_NO'\n");
        appendDirtyJavaScript("           + '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
        appendDirtyJavaScript("           + '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
        appendDirtyJavaScript("           + '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_CUSTOMER_NO=' + URLClientEncode(getValue_('ITEM1_CUSTOMER_NO',i))\n");
        appendDirtyJavaScript("           + '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
        appendDirtyJavaScript("           + '&PRICE_LIST_NO=' + URLClientEncode(getValue_('PRICE_LIST_NO',i))\n");
        appendDirtyJavaScript("           + '&QTY1=' + URLClientEncode(getValue_('QTY1',i))\n");
        appendDirtyJavaScript("           + '&LIST_PRICE=' + URLClientEncode(getValue_('LIST_PRICE',i))\n");
        appendDirtyJavaScript("           );\n");
        appendDirtyJavaScript("   if( checkStatus_(r,'PRICE_LIST_NO',i,'Price List No') )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("           assignValue_('LIST_PRICE',i,0);\n");
        appendDirtyJavaScript("           assignValue_('AMOUNTSALES',i,1);\n");
        appendDirtyJavaScript("           assignValue_('PRICE_LIST_NO',i,2);\n");
        appendDirtyJavaScript("           assignValue_('AGREEMENT_PRICE_FLAG',i,3);\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovItem1RoleCode(i,params)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if(params) param = params;\n");
        appendDirtyJavaScript("	else param = '';\n");
        appendDirtyJavaScript("	whereCond1 = '';\n"); 
        appendDirtyJavaScript("	whereCond2 = '';\n");
        appendDirtyJavaScript(" if ((document.form.EMP_NO.value != '') && (document.form.ITEM1_COMPANY.value != '')) \n");
        appendDirtyJavaScript(" {\n");
        appendDirtyJavaScript("    if((document.form.ITEM1_ORG_CODE.value == '') && (document.form.ITEM1_CONTRACT.value == ''))\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.EMP_NO.value) + \"' \";\n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("    else if((document.form.ITEM1_ORG_CODE.value != '') && (document.form.ITEM1_CONTRACT.value == ''))\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.EMP_NO.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM1_ORG_CODE.value)+\"' \";\n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("    else if((document.form.ITEM1_ORG_CODE.value == '') && (document.form.ITEM1_CONTRACT.value != ''))\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.EMP_NO.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM1_CONTRACT.value)+\"' \";\n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("    else if((document.form.ITEM1_ORG_CODE.value != '') && (document.form.ITEM1_CONTRACT.value != ''))\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.EMP_NO.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM1_ORG_CODE.value)+ \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM1_CONTRACT.value)+\"' \";\n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript(" }\n");
        appendDirtyJavaScript(" else\n");
        appendDirtyJavaScript(" {\n");
        appendDirtyJavaScript("	   whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.ITEM1_COMPANY.value) + \"' \";\n"); 
        appendDirtyJavaScript(" }\n");
        appendDirtyJavaScript("	var enable_multichoice =(true && ITEM1_IN_FIND_MODE);\n");
        appendDirtyJavaScript("	var key_value = (getValue_('ITEM1_ROLE_CODE',i).indexOf('%') !=-1)? getValue_('ITEM1_ROLE_CODE',i):'';\n");
        appendDirtyJavaScript("	if( getValue_('EMP_NO',i)=='' )\n");
        appendDirtyJavaScript("       {\n");
        appendDirtyJavaScript("                  openLOVWindow('ITEM1_ROLE_CODE',i,\n");
        appendDirtyJavaScript("                     '../mscomw/RoleToSiteLov.page?__FIELD=Craft+ID&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM1_ROLE_CODE',i))\n"); 
        appendDirtyJavaScript("                      + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
        appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");       
        appendDirtyJavaScript("       	             ,550,500,'validateItem1RoleCode');\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript(" else\n");
        appendDirtyJavaScript("       {\n");        
        appendDirtyJavaScript("              openLOVWindow('ITEM1_ROLE_CODE',i,\n");
        appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Craft+ID&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM1_ROLE_CODE',i))\n"); 
        appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
        appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
        appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
        appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");       
        appendDirtyJavaScript("       	   ,550,500,'validateItem1RoleCode');\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript("}\n");


        appendDirtyJavaScript("function validateItem1RoleCode(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("    j = 0;\n");
        appendDirtyJavaScript("    if (i == -1)\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("        j = i;\n");
        appendDirtyJavaScript("        i = -1;\n");
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("    if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("   setDirty();\n");
        appendDirtyJavaScript("   if( !checkItem1RoleCode(i) ) return;\n");
        appendDirtyJavaScript("   window.status='Please wait for validation';\n");
        appendDirtyJavaScript("    r = __connect(\n");
        appendDirtyJavaScript("           '");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM1_ROLE_CODE'\n");
        appendDirtyJavaScript("           + '&ITEM1_CUSTOMER_NO=' + URLClientEncode(getValue_('ITEM1_CUSTOMER_NO',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
        appendDirtyJavaScript("           + '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
        appendDirtyJavaScript("           + '&PRICE_LIST_NO=' + URLClientEncode(getValue_('PRICE_LIST_NO',i))\n");
        appendDirtyJavaScript("           + '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
        appendDirtyJavaScript("           + '&QTY1=' + URLClientEncode(getValue_('QTY1',i))\n");
        appendDirtyJavaScript("           + '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
        appendDirtyJavaScript("           + '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_ROLE_CODE=' + URLClientEncode(getValue_('ITEM1_ROLE_CODE',i))\n");
        appendDirtyJavaScript("           + '&AMOUNT=' + URLClientEncode(getValue_('AMOUNT',i))\n");
        appendDirtyJavaScript("           + '&CMNT=' + URLClientEncode(getValue_('CMNT',i))\n");
        appendDirtyJavaScript("           + '&NAME=' + URLClientEncode(getValue_('NAME',i))\n");
        appendDirtyJavaScript("           + '&SALESPARTCOST=' + URLClientEncode(getValue_('SALESPARTCOST',i))\n");
        appendDirtyJavaScript("           + '&LIST_PRICE=' + URLClientEncode(getValue_('LIST_PRICE',i))\n");
        appendDirtyJavaScript("           + '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
        appendDirtyJavaScript("           );\n");
        appendDirtyJavaScript("   if( checkStatus_(r,'ITEM1_ROLE_CODE',i,'Craft') )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("           assignValue_('LIST_PRICE',i,0);\n");
        appendDirtyJavaScript("           assignValue_('PRICE_LIST_NO',i,1);\n");
        appendDirtyJavaScript("           assignValue_('AMOUNT',i,2);\n");
        appendDirtyJavaScript("           assignValue_('AMOUNTSALES',i,3);\n");
        appendDirtyJavaScript("           assignValue_('CMNT',i,4);\n");
        appendDirtyJavaScript("           assignValue_('CATALOG_NO',i,5);\n");
        appendDirtyJavaScript("           assignValue_('SALESPARTCATALOGDESC',i,6);\n");
        appendDirtyJavaScript("           assignValue_('ITEM1_ROLE_CODE',i,7);\n");
        appendDirtyJavaScript("           assignValue_('ITEM1_CONTRACT',i,8);\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("    if (j == -1)\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("        validateCatalogNo(1);\n");
        appendDirtyJavaScript("        validatePriceListNo(1);\n");
        appendDirtyJavaScript("        validateCmnt(1);\n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("    j = 0;\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateCatalogNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("    j = 0;\n");
        appendDirtyJavaScript("    if (i == -1)\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("        j = i;\n");
        appendDirtyJavaScript("        i = -1;\n");
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("    if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("   setDirty();\n");
        appendDirtyJavaScript("   if( !checkCatalogNo(i) ) return;\n");
        appendDirtyJavaScript("   window.status='Please wait for validation';\n");
        appendDirtyJavaScript("    r = __connect(\n");
        appendDirtyJavaScript("           '");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=CATALOG_NO'\n");
        appendDirtyJavaScript("           + '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
        appendDirtyJavaScript("           + '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
        appendDirtyJavaScript("           + '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_CUSTOMER_NO=' + URLClientEncode(getValue_('ITEM1_CUSTOMER_NO',i))\n");
        appendDirtyJavaScript("           + '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
        appendDirtyJavaScript("           + '&PRICE_LIST_NO=' + URLClientEncode(getValue_('PRICE_LIST_NO',i))\n");
        appendDirtyJavaScript("           + '&QTY1=' + URLClientEncode(getValue_('QTY1',i))\n");
        appendDirtyJavaScript("           + '&SALESPARTCATALOGDESC=' + URLClientEncode(getValue_('SALESPARTCATALOGDESC',i))\n");
        appendDirtyJavaScript("           + '&NAME=' + URLClientEncode(getValue_('NAME',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_ROLE_CODE=' + URLClientEncode(getValue_('ITEM1_ROLE_CODE',i))\n");
        appendDirtyJavaScript("           + '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
        appendDirtyJavaScript("           );\n");
        appendDirtyJavaScript("   if( checkStatus_(r,'CATALOG_NO',i,'Sales Part Number') )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("           assignValue_('LIST_PRICE',i,0);\n");
        appendDirtyJavaScript("           assignValue_('AMOUNT',i,1);\n");
        appendDirtyJavaScript("           assignValue_('AMOUNTSALES',i,2);\n");
        appendDirtyJavaScript("           assignValue_('PRICE_LIST_NO',i,3);\n");
        appendDirtyJavaScript("           assignValue_('CMNT',i,4);\n");
        appendDirtyJavaScript("           assignValue_('SALESPARTCATALOGDESC',i,5);\n");
        appendDirtyJavaScript("           assignValue_('SALESPARTCOST',i,6);\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("    if (j == -1)\n");
        appendDirtyJavaScript("        validatePriceListNo(1);\n");
        appendDirtyJavaScript("        validateCmnt(1);\n"); 
        appendDirtyJavaScript("    j = 0;\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validatePartNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("f.HIDDENPARTNO.value = \"\";\n");
        appendDirtyJavaScript("   if( getRowStatus_('ITEM5',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("   setDirty();\n");
        appendDirtyJavaScript("   if( !checkPartNo(i) ) return;\n");
        appendDirtyJavaScript("   window.status='Please wait for validation';\n");
        appendDirtyJavaScript("   r = __connect(\n");
        appendDirtyJavaScript("           '");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=PART_NO'\n");
        appendDirtyJavaScript("		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
        appendDirtyJavaScript("		+ '&SPARE_CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&OWNER=' + URLClientEncode(getValue_('OWNER',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_ACTIVITY_SEQ=' + URLClientEncode(getValue_('ITEM2_ACTIVITY_SEQ',i))\n");
        appendDirtyJavaScript("         + '&SUPPLY_CODE=' + SelectURLClientEncode('SUPPLY_CODE',i)		+ '&ITEM0_MAINT_MATERIAL_ORDER_NO=' + URLClientEncode(getValue_('ITEM0_MAINT_MATERIAL_ORDER_NO',i))");
        appendDirtyJavaScript("		+ '&CONDITION_CODE=' + URLClientEncode(getValue_('CONDITION_CODE',i))\n");
        appendDirtyJavaScript("        + '&WO_NO=' + URLClientEncode(document.form.ITEM2_WO_NO.value) \n");
        appendDirtyJavaScript("		+ '&PART_OWNERSHIP=' + URLClientEncode(getValue_('PART_OWNERSHIP',i))\n");
        appendDirtyJavaScript("		+ '&ITEM_WO_NO=' + URLClientEncode(getValue_('ITEM_WO_NO',i))\n");
        appendDirtyJavaScript("           );\n");
        appendDirtyJavaScript("   window.status='';\n");
        appendDirtyJavaScript("   if( checkStatus_(r,'PART_NO',i,'Part No') )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("           assignValue_('ITEM_CATALOG_NO',i,0);\n");
        appendDirtyJavaScript("           assignValue_('ITEMCATALOGDESC',i,1);\n");
        appendDirtyJavaScript("           assignValue_('HASSPARESTRUCTURE',i,2);\n");
        appendDirtyJavaScript("           assignValue_('DIMQTY',i,3);\n");
        appendDirtyJavaScript("           assignValue_('TYPEDESIGN',i,4);\n");
        appendDirtyJavaScript("           assignValue_('QTYONHAND',i,5);\n");
        appendDirtyJavaScript("           assignValue_('UNITMEAS',i,6);\n");
        appendDirtyJavaScript("           assignValue_('SPAREDESCRIPTION',i,7);\n");
        appendDirtyJavaScript("		assignValue_('SALES_PRICE_GROUP_ID',i,8);\n");
        appendDirtyJavaScript("		assignValue_('CONDITION_CODE',i,9);\n");
        appendDirtyJavaScript("		assignValue_('CONDDESC',i,10);\n"); 
        appendDirtyJavaScript("        assignValue_('QTY_AVAILABLE',i,11);\n");
        appendDirtyJavaScript("        assignValue_('ACTIVEIND_DB',i,12);\n");
        appendDirtyJavaScript("        assignValue_('PART_OWNERSHIP',i,13);\n");
        appendDirtyJavaScript("        assignValue_('PART_OWNERSHIP_DB',i,14);\n");
        appendDirtyJavaScript("        assignValue_('OWNER',i,15);\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("else{\n");
        appendDirtyJavaScript("f.HIDDENPARTNO.value = f.PART_NO.value;\n");
        appendDirtyJavaScript("f.ONCEGIVENERROR.value = \"TRUE\";\n");
        appendDirtyJavaScript("getField_('PART_NO',i).value = '';\n");
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("   if (f.ACTIVEIND_DB.value == 'N') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWACTIVEROUNDINVSALESPART: All sale parts connected to the part are inactive."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.ITEM_CATALOG_NO.value = ''; \n");
        appendDirtyJavaScript("      f.ITEMCATALOGDESC.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("        validateItemCatalogNo(i);\n");
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("window.name = \"ActiveRound\";\n");

        appendDirtyJavaScript("if (");
        appendDirtyJavaScript(openFormInNewWin);
        appendDirtyJavaScript(")\n");
        appendDirtyJavaScript("   window.open(\"");
        appendDirtyJavaScript(newWindowURL);
        appendDirtyJavaScript("\"+URLClientEncode(\"");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(queryStringVal));   // XSS_Safe Amdilk 20070711
        appendDirtyJavaScript("\")");
        appendDirtyJavaScript(",\"DlgWinHandle\",\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(magFixed));   // XSS_Safe Amdilk 20070711
        appendDirtyJavaScript("' == 'TRUE')\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   window.open(\"WorkOrderCodingDlg.page?ROW_NO=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(sRowNo));   // XSS_Safe Amdilk 20070711
        appendDirtyJavaScript("&WONO=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(wo_No));    // XSS_Safe Amdilk 20070711

        if ((tabs.getActiveTab() == 5) && itemlay4.isSingleLayout())
        {
            appendDirtyJavaScript("&SINGLE_ROW_NO=");
            appendDirtyJavaScript(singleRowNo);
        }

        appendDirtyJavaScript("\",\"AuthCorr\",\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(openCreRepNonSer);
        appendDirtyJavaScript("' == \"TRUE\")\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("//alert(\"creRepNonSerPath = \"+'");
        appendDirtyJavaScript(creRepNonSerPath);
        appendDirtyJavaScript("');\n");
        appendDirtyJavaScript("   if (document.form.CREREPNONSER.value == \"TRUE\")\n");
        appendDirtyJavaScript("      window.open('");
        appendDirtyJavaScript(creRepNonSerPath);
        appendDirtyJavaScript("',\"createRepWONonSer\",\"alwaysRaised=yes,resizable,status=yes,scrollbars=yes,width=790,height=575\");\n");
        appendDirtyJavaScript("   document.form.CREREPNONSER.value = \"FALSE\";\n");
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
        appendDirtyJavaScript("}\n");

        String outStr = out.toString();
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

        appendDirtyJavaScript("function validatePartOwnership(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM5',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkPartOwnership(i) ) return;\n");
        appendDirtyJavaScript(" if( getValue_('PART_OWNERSHIP',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=PART_OWNERSHIP'\n");
        appendDirtyJavaScript("		+ '&PART_OWNERSHIP=' + SelectURLClientEncode('PART_OWNERSHIP',i)		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
        appendDirtyJavaScript("		+ '&SPARE_CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&OWNER=' + URLClientEncode(getValue_('OWNER',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_ACTIVITY_SEQ=' + URLClientEncode(getValue_('ITEM2_ACTIVITY_SEQ',i))\n");
        appendDirtyJavaScript("         + '&SUPPLY_CODE=' + SelectURLClientEncode('SUPPLY_CODE',i)		+ '&ITEM0_MAINT_MATERIAL_ORDER_NO=' + URLClientEncode(getValue_('ITEM0_MAINT_MATERIAL_ORDER_NO',i))");
        appendDirtyJavaScript("		+ '&CONDITION_CODE=' + URLClientEncode(getValue_('CONDITION_CODE',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'PART_OWNERSHIP',i,'Ownership') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('PART_OWNERSHIP_DB',i,0);\n");
        appendDirtyJavaScript("		assignValue_('QTYONHAND',i,1);\n");
        appendDirtyJavaScript("	      assignValue_('QTY_AVAILABLE',i,2);\n");
        appendDirtyJavaScript("		if (f.PART_OWNERSHIP_DB.value == 'CONSIGNMENT'){\n");
        appendDirtyJavaScript("		alert('");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVEROUNDINVOWNER1: Ownership type Consignment is not allowed in Materials for Work Orders."));
        appendDirtyJavaScript("'); \n");
        appendDirtyJavaScript("      f.PART_OWNERSHIP.value = ''; \n");
        appendDirtyJavaScript("      f.PART_OWNERSHIP_DB.value = ''; \n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("		if (f.PART_OWNERSHIP_DB.value == 'SUPPLIER LOANED'){\n");
        appendDirtyJavaScript("		alert('");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVEROUNDINVOWNER2: Ownership type Supplier Loaned is not allowed in Materials for Work Orders."));
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
        appendDirtyJavaScript("	  var enable_multichoice =(true && ITEM5_IN_FIND_MODE);\n");
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

        appendDirtyJavaScript("function checkItem5Owner()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if (checkItem5Fields())\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("      if (f.WO_CUST.value != '' && f.OWNER.value != '' && f.WO_CUST.value != f.OWNER.value) \n");
        appendDirtyJavaScript("         return confirm('");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVEROUNDDIFFCUST: Owning Customer No is not the same as the Work Order Customer No. Still want to save this record?"));
        appendDirtyJavaScript("');\n");
        appendDirtyJavaScript("      else if (f.WO_CUST.value == '' && f.OWNER.value != '') \n");
        appendDirtyJavaScript("         return confirm('");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVEROUNDNOCUST: There is no Customer specified for the Work Order although you have entered an Owning Customer No. Still want to save this record?"));
        appendDirtyJavaScript("');\n");
        appendDirtyJavaScript("      else \n");
        appendDirtyJavaScript("         return true;\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateOwner(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if( getRowStatus_('ITEM5',i)=='QueryMode__' ) return;\n");
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
        appendDirtyJavaScript("      window.status='Please wait for validation';\n");
        appendDirtyJavaScript("      r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=OWNER'\n");
        appendDirtyJavaScript("                    + '&OWNER=' + URLClientEncode(getValue_('OWNER',i))\n");
        appendDirtyJavaScript("                    + '&PART_OWNERSHIP_DB=' + URLClientEncode(getValue_('PART_OWNERSHIP_DB',i))\n");
        appendDirtyJavaScript("                    + '&ITEM2_WO_NO=' + URLClientEncode(getValue_('ITEM2_WO_NO',i))\n");
        appendDirtyJavaScript("		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
        appendDirtyJavaScript("		+ '&SPARE_CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))\n");
        appendDirtyJavaScript("         + '&SUPPLY_CODE=' + SelectURLClientEncode('SUPPLY_CODE',i)		+ '&ITEM0_MAINT_MATERIAL_ORDER_NO=' + URLClientEncode(getValue_('ITEM0_MAINT_MATERIAL_ORDER_NO',i))");
        appendDirtyJavaScript("		+ '&ITEM2_ACTIVITY_SEQ=' + URLClientEncode(getValue_('ITEM2_ACTIVITY_SEQ',i))\n");
        appendDirtyJavaScript("		+ '&CONDITION_CODE=' + URLClientEncode(getValue_('CONDITION_CODE',i))\n");
        appendDirtyJavaScript("		+ '&PART_OWNERSHIP=' + URLClientEncode(getValue_('PART_OWNERSHIP',i))\n");
        appendDirtyJavaScript("                   );\n");
        appendDirtyJavaScript("      window.status='';\n");
        appendDirtyJavaScript("      if( checkStatus_(r,'OWNER',i,'Owner') )\n");
        appendDirtyJavaScript("      {\n");
        appendDirtyJavaScript("         assignValue_('OWNER_NAME',i,0);\n");
        appendDirtyJavaScript("         assignValue_('WO_CUST',i,1);\n");
        appendDirtyJavaScript("	      assignValue_('QTYONHAND',i,2);\n");
        appendDirtyJavaScript("	      assignValue_('QTY_AVAILABLE',i,3);\n");
        appendDirtyJavaScript("      }\n");
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP.value == 'Company Owned' && f.OWNER.value != '') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWACTIVEROUNDINVOWNER11: Owner should not be specified for Company Owned Stock."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.OWNER.value = ''; \n");
        appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP.value == 'Consignment' && f.OWNER.value != '') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWACTIVEROUNDINVOWNER12: Owner should not be specified for Consignment Stock."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.OWNER.value = ''; \n");
        appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP.value == 'Supplier Loaned' && f.OWNER.value != '') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWACTIVEROUNDINVOWNER13: Owner should not be specified for Supplier Loaned Stock."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.OWNER.value = ''; \n");
        appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP.value == '' && f.OWNER.value != '') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWACTIVEROUNDINVOWNER14: Owner should not be specified when there is no Ownership type."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.OWNER.value = ''; \n");
        appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("}\n");


        if ((tabs.getActiveTab() == 3) && itemlay.isSingleLayout() && bUnequalMatWo)
        {
            appendDirtyJavaScript("         if (confirm('");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVEROUNDDIFFCUST: Owning Customer No is not the same as the Work Order Customer No. Still want to save this record?"));
            appendDirtyJavaScript("'))\n"); 
            appendDirtyJavaScript("   { \n");
            appendDirtyJavaScript("      setITEM5Command('issueFromInvent');commandSet('ITEM5.Perform','');\n");
            appendDirtyJavaScript("   } \n");
        }
        else if ((tabs.getActiveTab() == 3) && itemlay.isSingleLayout() && bNoWoCust)
        {
            appendDirtyJavaScript("         if (confirm('");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVEROUNDNOCUST: There is no Customer specified for the Work Order although you have entered an Owning Customer No. Still want to save this record?"));
            appendDirtyJavaScript("'))\n");
            appendDirtyJavaScript("   { \n");
            appendDirtyJavaScript("      setITEM5Command('issueFromInvent');commandSet('ITEM5.Perform','');\n");
            appendDirtyJavaScript("   } \n");
        }
        else if ((tabs.getActiveTab() == 3) && itemlay.isMultirowLayout() && bUnequalMatWo)
        {
            appendDirtyJavaScript("         if (confirm('");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVEROUNDDIFFCUST: Owning Customer No is not the same as the Work Order Customer No. Still want to save this record?"));
            appendDirtyJavaScript("'))\n"); 
            appendDirtyJavaScript("   { \n");
            appendDirtyJavaScript("      initPop5("+currrowItem+");_menu(0);setTableCommand5('issueFromInvent');commandSet('ITEM5.Perform','');\n");
            appendDirtyJavaScript("   } \n");
        }
        else if ((tabs.getActiveTab() == 3) && itemlay.isMultirowLayout() && bNoWoCust)
        {
            appendDirtyJavaScript("         if (confirm('");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVEROUNDNOCUST: There is no Customer specified for the Work Order although you have entered an Owning Customer No. Still want to save this record?"));
            appendDirtyJavaScript("'))\n");
            appendDirtyJavaScript("   { \n");
            appendDirtyJavaScript("      initPop5("+ currrowItem +");_menu(0);setTableCommand5('issueFromInvent');commandSet('ITEM5.Perform','');\n");
            appendDirtyJavaScript("   } \n");
        }
        if (bOpenNewWindow)
        {
            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));   // XSS_Safe Amdilk 20070711
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
        }

        if (itemlay.isNewLayout())
        {
            appendDirtyJavaScript("   { \n");
            appendDirtyJavaScript("      f.PART_OWNERSHIP.selectedIndex = 1;\n");
            appendDirtyJavaScript("   } \n");
        }

        //Bug 66456, Start, Added check on PROJ
        if (mgr.isModuleInstalled("PROJ"))
        {
            if (itemlay.isNewLayout() && !mgr.isEmpty(headset.getValue("ACTIVITY_SEQ")))
            {
                appendDirtyJavaScript("   { \n");
                appendDirtyJavaScript("      f.SUPPLY_CODE.selectedIndex = 1;\n");
                appendDirtyJavaScript("   } \n");
            }
            else if (itemlay.isNewLayout() && mgr.isEmpty(headset.getValue("ACTIVITY_SEQ")))
            {
                appendDirtyJavaScript("   { \n");
                appendDirtyJavaScript("      f.SUPPLY_CODE.selectedIndex = 2;\n");
                appendDirtyJavaScript("   } \n");
            }
        }
        else if (itemlay.isNewLayout())
        {
            appendDirtyJavaScript("   { \n");
            appendDirtyJavaScript("      f.SUPPLY_CODE.selectedIndex = 2;\n");
            appendDirtyJavaScript("   } \n");
        }
        //Bug 66456, End

        if ("TRUE".equals(showMat))
            appendDirtyJavaScript("NEVER_EXPIRE = false;\n");

        // Overriden validateStdJobId()... 
        appendDirtyJavaScript("function validateStdJobId(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("    if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("    setDirty();\n");
        appendDirtyJavaScript("    if( !checkStdJobId(i) ) return;\n");
        appendDirtyJavaScript("    if( getValue_('STD_JOB_ID',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("    if( getValue_('STD_JOB_CONTRACT',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("    if( getValue_('STD_JOB_REVISION',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("    if( getValue_('DESCRIPTION',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("    if( getValue_('STD_JOB_ID',i)=='' )\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("        getField_('STD_JOB_ID',i).value = '';\n");
        appendDirtyJavaScript("        getField_('STD_JOB_REVISION',i).value = '';\n");
        appendDirtyJavaScript("        getField_('STD_JOB_STATUS',i).value = '';\n");
        appendDirtyJavaScript("        return;\n");
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("    window.status='Please wait for validation';\n");
        appendDirtyJavaScript("     r = __connect(\n");
        appendDirtyJavaScript("            APP_ROOT+ 'pcmw/ActiveRound.page'+'?VALIDATE=STD_JOB_ID'\n");
        appendDirtyJavaScript("            + '&STD_JOB_ID=' + URLClientEncode(getValue_('STD_JOB_ID',i))\n");
        appendDirtyJavaScript("            + '&STD_JOB_CONTRACT=' + URLClientEncode(getValue_('STD_JOB_CONTRACT',i))\n");
        appendDirtyJavaScript("            + '&STD_JOB_REVISION=' + URLClientEncode(getValue_('STD_JOB_REVISION',i))\n");
        appendDirtyJavaScript("            + '&DESCRIPTION=' + URLClientEncode(getValue_('DESCRIPTION',i))\n");
        appendDirtyJavaScript("            );\n");
        appendDirtyJavaScript("    window.status='';\n");
        appendDirtyJavaScript("\n");
        appendDirtyJavaScript("    if( checkStatus_(r,'STD_JOB_ID',i,'Standard Job ID') )\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("        assignValue_('STD_JOB_ID',i,0);\n");
        appendDirtyJavaScript("        assignValue_('STD_JOB_REVISION',i,1);\n");
        appendDirtyJavaScript("        assignValue_('DESCRIPTION',i,2);\n");
        appendDirtyJavaScript("        assignValue_('STD_JOB_STATUS',i,3);\n");
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("}\n");
        
        appendDirtyJavaScript("function checkStdJobDeleteParams(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   f.CHECK_DELETE_ITEM6.value = 'TRUE';\n");
        appendDirtyJavaScript("   return true;\n");
        appendDirtyJavaScript("}\n");
        
        appendDirtyJavaScript("function checkITEM6SaveParams(i)\n");
        appendDirtyJavaScript("{");
        appendDirtyJavaScript("   r = __connect(\n");
        appendDirtyJavaScript("		  '");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=STD_JOB_FLAG'\n");
        appendDirtyJavaScript("       + '&ITEM6_WO_NO=' + URLClientEncode(getValue_('ITEM6_WO_NO',i))\n");
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
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVEROUNDREMMATERIAL: Do you want to remove connected Materials and Documents from job "));
        appendDirtyJavaScript("' + getValue_('DUMMY_STD_JOB_ID',i) + '?");
        appendDirtyJavaScript("'))\n");
        appendDirtyJavaScript("               getField_('KEEP_CONNECTIONS',i).value = 'NO';\n");
        appendDirtyJavaScript("            else\n");
        appendDirtyJavaScript("               getField_('KEEP_CONNECTIONS',i).value = 'YES';\n");
        appendDirtyJavaScript("         }\n");
        appendDirtyJavaScript("         else if ((getValue_('N_ROLE_EXIST',i) == 1 || getValue_('N_MAT_EXIST',i) == 1 || getValue_('N_TOOL_EXIST',i) == 1 || getValue_('N_PLANNING_EXIST',i) == 1 || getValue_('N_DOC_EXIST',i) == 1)\n"); 
        appendDirtyJavaScript("                  && getValue_('STD_JOB_ID',i) != ''\n");
        appendDirtyJavaScript("                  && getValue_('STD_JOB_REVISION',i) != ''\n");
        appendDirtyJavaScript("                  && getValue_('N_QTY',i) != ''\n");
        appendDirtyJavaScript("                  && (getValue_('S_STD_JOB_ID',i) != getValue_('STD_JOB_ID',i) || getValue_('S_STD_JOB_REVISION',i) != getValue_('STD_JOB_REVISION',i) || getValue_('N_QTY',i) != getValue_('ITEM6_QTY',i)))\n");
        appendDirtyJavaScript("         {\n");
        appendDirtyJavaScript("            if (confirm('");
        appendDirtyJavaScript(mgr.translateJavaScript("WOJOB_RECROUND_STDJOB: Do you want to remove and reconnect Materials and Documents?"));
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

        appendDirtyJavaScript("function connectToActivityClient()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   window.open('../projw/ConObjToActivityDlg.page','CONOBJDLG','status,resizable,scrollbars,width=800,height=300,left=100,top=200');\n");
        appendDirtyJavaScript("   return false;\n");
        appendDirtyJavaScript("}\n");
        
        appendDirtyJavaScript("function refreshPermitTab()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   f.REFRESHFLAG.value = 'TRUE';\n");
        appendDirtyJavaScript("   f.REFRESHBLOCK.value = 'ITEMBLK3';\n");
        appendDirtyJavaScript("   f.submit();\n");
        appendDirtyJavaScript("}\n");        

        appendDirtyJavaScript("function disconnectFromActivityClient()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   return confirm('" + mgr.translateJavaScript("ACTROUNDHEADERDISCACT: Are you sure you want to remove the Project Connection ?") + "');\n");
        appendDirtyJavaScript("}\n");

        //Bug 66456, Start, Added check on PROJ
        if (mgr.isModuleInstalled("PROJ"))
        {
            appendDirtyJavaScript("function setActivitySeq(activitySeq)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   getField_('ACTIVITY_SEQ_PROJ', -1).value = activitySeq;\n");
            appendDirtyJavaScript("   commandSet('HEAD.connectToActivity','');\n");
            appendDirtyJavaScript("}\n");
        }
        //Bug 66456, End

        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(issued);   // XSS_Safe Amdilk 20070712
        appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"PageID_my_cookie\")==\"TRUE\")\n");
        appendDirtyJavaScript("{ \n");
        appendDirtyJavaScript("      if (confirm(\"");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVEROUNDUNISSUE: All required material has not been issued. Do you want to continue?"));
        appendDirtyJavaScript("\")) {\n");
        appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"CCCC\";\n");
        appendDirtyJavaScript("		writeCookie(\"PageID_my_cookie\", \"FALSE\", '', COOKIE_PATH); \n");
        appendDirtyJavaScript("		f.submit();\n");
        appendDirtyJavaScript("     } \n");
        appendDirtyJavaScript("} \n");

        if (bDeleteItem6) {
            appendDirtyJavaScript("f.DUMMY_KEEP_CONNECTIONS.value = 'FALSE';\n");
            if (bConfirmMsg1) {
                appendDirtyJavaScript("if (confirm('");
                appendDirtyJavaScript(sConfirmMsg1);
                appendDirtyJavaScript("'))\n");
                appendDirtyJavaScript("   f.DUMMY_KEEP_CONNECTIONS.value = 'TRUE';\n");
            }
            
            appendDirtyJavaScript("f.CHECK_DELETE_ITEM6.value = 'DELETE';\n");
            appendDirtyJavaScript("commandSet('ITEM6.Delete','');\n");
        }

        appendDirtyJavaScript("function refreshPage()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   f.REFRESHFLAG.value = 'TRUE';\n");
        appendDirtyJavaScript("   f.REFRESHBLOCK.value = 'ITEMBLK3';\n");
        appendDirtyJavaScript("   f.submit();\n");
        appendDirtyJavaScript("}\n"); 

	//Bug 82543, Start
	appendDirtyJavaScript("function refreshParent()\n");
	appendDirtyJavaScript("{\n");
	appendDirtyJavaScript("   f.REFRESH_FLAG.value = 'TRUE';\n");
	appendDirtyJavaScript("   f.submit();\n");
	appendDirtyJavaScript("}\n");
	//Bug 82543, End

	//Bug 89399, Start
	appendDirtyJavaScript("function checkMando()\n");
	appendDirtyJavaScript("{\n");
	appendDirtyJavaScript("   if (f.QTY.value != '' && f.QTY.value == 0 )  \n");
	appendDirtyJavaScript("   {\n");
	appendDirtyJavaScript("             alert('");
	appendDirtyJavaScript(                 mgr.translate("PCMWACTIVEROUNDNOTVALIDQTY: Reported hours value for "));
	appendDirtyJavaScript("'+f.ITEM1_WO_NO.value+' ");
	appendDirtyJavaScript(                 mgr.translate("PCMWACTIVEROUNDNOTVALIDQTY1: is not allowed to be zero."));
	appendDirtyJavaScript("             ');\n");
	appendDirtyJavaScript("      return false;\n"); 
	appendDirtyJavaScript("   } \n");
	appendDirtyJavaScript("	  return checkItem1Fields(-1);\n");
	appendDirtyJavaScript("}\n");
	//Bug 89399, End
    }
}

