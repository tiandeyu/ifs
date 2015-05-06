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
*  File        : ActiveSeperateReportInWorkOrderSM.java 
*  Created     : ASP2JAVA Tool  2001-03-04  - Created Using the ASP file ActiveSeperateReportInWorkOrderSM.asp
*  Modified    :
*  JEWILK  010320  Fixed conversion errors and done necessary adjustments.
*  BUNILK  010408  Overwritten saveReturn() methods for Material line and Time Report 
*                  blocks to repopulate budget block after new records of those blocks.     
*  JEWILK  010418  Removed Pre Posting actions in Material tab (Call 62860)
*  JEWILK  010611  Modified method run() to fetch the necessary values when returning from 'Create from planning'. (call #65296)
*                  Changed 'document.ClientUtil' as 'document.applets[0]' in javascript.
*  BUNILK  010614  Modified all validation parts of the material line block. 
*  JEWILK  010618  Modified functionality in Time Report tab.(call 65296, 66205).
*  JEWILK  010621  Removed rmb 'Create from planning' from commandbar and added as a hyperlink in the 'Time Report' tab.
*                  Set default layout mode as MULTIROW in the 'Time Report' tab.
*  BUNILK  010703  Modified all state changing methods of headblk and perform method. Addded new method checkState(),
*                  Removed unwanted actions and command buttons when itemblks are in find edit or new mode.Added state changing 
*                  Actions for multi record mode also    
*  BUNILK  010716  Modified PART_NO field validation. 
*  BUNILK  010724  Modified Craft block validations.
*  INROLK  010727  Added RMB move Move Non Serial to Inventory... call id 77825.
*  JEWILK  010802  Set some fields default visible in budget tab.
*  JEWILK  010802  Modified function validate, to fetch the planning value for sales price, in Time Report tab.
*  JEWILK  010806  Overwrote deleteITEM1 and deleteITEM5, to refresh the budget block.
*  BUNILK  010806  Made some changes in return AutoString of getContents() function so that to call       
*                  checkPartVal() javascript function from onBlur event of PART_NO field. Done some changes in 
*                  validatePartNo() method also. Modified sparePartObject() method and associated javascripts. 
*  CHCRLK  010809  Modified print methods.
*  JEWILK  010815  Modified function moveNonSerial() to transfer 'ERR_DESCR' also.
*  ARWILK  010904  Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction(). 
*                  (Caused when cancelNew is performed after saveNew)
*  JEWILK  010912  Modified method 'saveReturnItem5' to goto newly saved record in single record mode.
*  JEWILK  010921  Added action Pre Posting to Material and Material Requisition blocks.
*  JEWILK  011008  Checked security permissions for RMB Actions.
*  BUNILK  011011  Added addCommandValidConditions() method for headblk state changing actions.  
*  VAGULK  011011  Changed the Exec Dept LOV to 'ORG_CODE_ALLOWED_SITE_LOV'
*  JEWILK  011015  Modified validations of fields 'PLAN_QTY','CATALOG_NO','PRICE_LIST_NO' to 
*                  correctly display 'Sales Price' and 'Discounted Price'.
*  BUNILK  011016  Added Back hyperlinkt to Material block
*  JEWILK  011010  Modified to clear the row from the rowset, when the WO is 'Finished'. call# 70504
*  JEWILK  011021  Modified the validation of 'CATALOG_NO' to correctly fetch the 'PRICE_LIST_NO'. 
*                  Set Field 'ITEM1_ORG_CODE' Mandatory. call# 71069
*  JEWILK  020331  Modified javascript function 'validateEmpNo()' to avoid further validation when the emp_no is invalid.
*  JAPELK  020401  WO_NO LOV did not filter from the user_allowed_site.
*  SHAFLK  020508  Bug 29946,Added hyperlinks for the operations "Spare Parts in Object" and "Object Structure" under the header level of Material section. 
*  SHAFLK  020509  Bug 29943,Made changes to refresh in same page after RMB return. 
*  SHAFLK  020531  Bug 30450,Changed "Comment" field to type text area.
*  SHAFLK  020619  Bug 30450,Added Validation for "Comment" field.
*  SHAFLK  020726  Bug Id 31771 Added security check for MOBMGR_WORK_ORDER_API.Check_Exist.
*  BUNILK  020830  Bug Id 32182 Modified setCheckBoxValue method so that to check security for Domanw component. 
*                  and added a new method isModuleInst() to check availability of ORDER module inside preDefine method.    
*  SHAFLK  021108  Bug Id 34064,Changed methods printAuth,printPicList & printWO.    
*  SHAFLK  021115  Bug Id 34164, Modified methods preDefine and newRowITEM1. 
*  ---------------------Generic WO-------------------------------------------
*  INROLK  021120  Added MCH_CODE_CONTRACT and CONNECTION_TYPE.
*  -------------------------------------------------------------------------
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  BUNILK  021216  Merged with 2002-3 SP3
*  BUNILK  030121  Codes reviewed.
*  SHAFLK  030211  Added "prntblk"  and removed "printblk" .
*  SHAFLK  030508  Modified setCheckBoxValue(), preDefine() and adjust() methods.
*  CHCRLK  030611  Added action "Create MRO Object Receive Order" to the header.
*  CHCRLK  030612  Added actions "Return Object" & "Return Not Operational Object" to the header.
*  INROLK  030624  IID ADAM305NA Added RMB Returns.
*  CHAMLK  030923  Added RMB Tools and Facilities.
*  JEWILK  030925  Corrected overridden javascript function toggleStyleDisplay(). 
*  THWILK  031018  Call ID 108197,Changed the constant PCMWACTIVESEPERATEREPORTINWORKORDERSMMRECEIVEO.
*  CHAMLK  031021  Allowed MRO functionality for EQUIPMENT objects as well.
*  CHAMLK  031022  Modified function preDefine() in order to remove setUpperCase() for CONNECTION_TYPE.
*  THWILK  031024  Call ID 104789 - Added an extra parameter(#)to the deffinition of the field WO_NO.
*  PAPELK  031024  Call ID 108606.Removed ASPField refering CustomerNo in TimeReport tab.
*  JEWILK  031025  Modified method checkMROObjRO() to check the conditions according to centura. Call 100823.
*  CHCRLK  031106  Modified translation constants in methods returnObj(), returnNonOpObj() & mroObjReceiveO(). [Call 100823]
*  SAPRLK  031216  Web Alignment, Converting blocks to tabs, removing 'clone' & 'doReset' methods, converting links to RMBs.
*  ARWILK  031224  Edge Developments - (Replaced links with multirow RMB's)
*  DIMALK  040120  Replaced calls to package Active_Separate1_API with Active_Separate_API  
*  SAPRLK  040129  Web Alignment - Added multirow action to master form and the tabs, simplifying code for RMBs, remove unnecessary method calls for hidden fields,
*                  removed unnecessary global variables.  
*  VAGULK  040210  Web Alignment - Arranged field order as in Centura and removed unnecessary codes.
*  SAPRLK  040212  Web Alignment - simplifying code for RMBs, adding code to open tabs implemented as RMBs in a new window, change of conditional code in validate method.
*  ARWILK  040316  Bug#112935 - Resolved RMB problems.
*  VAGULK  040318  Web Alignment ,modified getContents().
*  SHAFLK  040119  Bug Id 41815,Removed Java dependencies.
*  SHAFLK  040202  Bug Id 42065, Changed Vlidation of ROLE_CODE, EMP_NO and modified method newRowITEM1(). 
*  SHAFLK  040213  Bug Id 40256, Modified sparePartObjectJS() and objStructureJS().
*  SHAFLK  040308  Bug Id 40788, Modified Validation() for EMP_NO , method validateEmpNo() and predefine(). 
*  SAPRLK  040325  Merged with SP1.
*  SAPRLK  040401  Made Changes for Web Alignment.
*  THWILK  040420  Call 112950,Modified permits() and used cookie "PageID_CurrentWindow" to prevent the opening of the 
*                  previously visited window when an error is encountered.
*  SAPRLK  040421  Corrected Call 114222.
*  THWILK  040421  Web Alignment - Set the PageID_CurrentWindow cookie in all RMB's which opens in separate windows.
*  SAPRLK  040422  Corrected Call Id 114250, set the active tab to the Time Report tab when the form is refreshed to insert a new record in the 'Create from Planning' 
*                  & 'Connect/Reconnect to Planning' RMBs.
*  SAPRLK  040603  Added key PM_REVISION.
*  ARWILK  040617  Added a new job tab. Added job_id to materials tab.(Spec - AMEC109A)  
*  SHAFLK  040416  Bug 43848, Modified methods run(), finished() and HTML part.Added methods CheckObjInRepairShop() and finshed1().
*  THWILK  040625  Bug 43848 Merged.
*  ARWILK  040629  Added RMB's connectToActivity and disconnectFromActivity (Spec AMME613A - Project Umbrella)
*  ARWILK  040720  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  VAGULK  040721  Added fields "SUPPLY_CODE ,SUPPLY_CODE_DB and ACTIVITY_SEQ" to Material tab (SCME612 - Project Inventory)
*  NIJALK  040727  Added new parameter CONTRACT to method calls to Role_Sales_Part_API.Get_Def_Catalog_No.  
*  SHAFLK  040708  Bug 41817, Modified methods run(), finished(), reported() and HTML part.Added methods CheckAllMaterialIssued() and reported1().
*  ThWilk  040810  Merged Bug 41817.
*  SHAFLK  040421  Bug 42866, Modified method issue().
*  ThWilk  040818  Merged Bug 42866 and modified method call MAKE_ISSUE_DETAIL.
*  ARWILK  040820  Changed LOV's of STD_JOB_ID and STD_JOB_REVISION.
*  BUNILK  040825  Changed server function of QTYONHAND field and added some necessary fields for it. 
*  NIJALK  040825  Call id 117187,  modified CheckObjInRepairShop().
*  ThWilk  040825  Call 117308, Modified released() and started().
*  NIJALK  040826  Call 117104, Modified run().
*  NIJALK  040826  Call 117295, Changed the column contract in time report tab to read only.
*  BUNILK  040826  Call 117328, Modified finished()  
*  VAGULK  040827  Corrected Calls 117249 and 117251,fetched the default values of "Owernership" and "Supply_code" in the new layout(Added fields
*                  PART_OWNERSHIP,OWNER,OWNER_NAME and SUPPLY_CODE.
*  ARWILK  040901  Call 117155, Modified method getContents.
*  ARWILK  040901  Resolved Material status refresh problem.
*  NIJALK  040901  Call 117415, Modified predefine(), Added saveReturnItem6().
*  SHAFLK  040722  Bug 43249, Modified validation of PART_NO and added fields and validations for CONDITION_CODE, PART_OWNERSHIP and OWNER and added some fields to predefine and modified HTML part. 
*  NIJALK  040902  Merged bug 43249.
*  NIJALK  040909  Call 117187: Modified Perform().
*  ARWILK  040910  Modified availtoreserve.
*  NIJALK  040929  Added parameter project_id to method calls to Inventory_Part_In_Stock_API.Get_Inventory_Quantity.
*  BUNILK  040930  Changed server function of QTYONHAND field and modified validations of Part No, Condition Code, Ownership and owner fields of Material tab..
*  NIJALK  041001  Renamed field 'Signature' to 'Executed By' in Jobs tab. Modified validations of SIGN_ID,EMPLOYEE_ID.
*  ARWILK  041005  LCS Merge: 45565.
*  SHAFLK  040906  Bug 46542, Modified method matReqUnissue().
*  NIJALK  041007  Merged 46542. 
*  SHAFLK  040812  Bug 45904, Modified method issue(), manissue() and HTML part. 
*  NIJALK  041007  Merged 45904.
*  ARWILK  041011  Handles some problems in Condition Code and Ownership functionality.
*  ARWILK  041022  LOV Change for Fields STD_JOB_ID,STD_JOB_REVISION. (Spec AMEC111 - Standard Jobs as PM Templates)
*  BUNILK  041026  Modified validations of material tab, added new validation to supply code, modified new method of material tab
*  NIJALK  041029  Modified validate(), preDefine().
*  NIJALK  041105  Added Std Job Id to Jobs Tab. Modified preDefine(), validate().
*  SHAFLK  040916  Bug 46241, Modified validation of PART_NO ,CONDITION_CODE, PART_OWNERSHIP and OWNER . 
*  CHANLK  041105  Merged Bug 46621.
*  NAMELK  041110  Duplicated Translation Tags Corrected.
*  NIJALK  041112  Made the field "Employee Id" visible and read only in jobs tab.
*  ARWILK  041115  Replaced getContents with printContents.
*  NIJALK  041115  Replaced method Standard_Job_API.Get_Work_Description with Standard_Job_API.Get_Definition.
*  NIJALK  041202  Added new parameters to method calls to Inventory_Part_In_Stock_API.Get_Inventory_Quantity.
*  NIJALK  041209  Modified validations to "Comment" field in Time Report Tab.
*  NIJALK  041210  Call 120514: Modified validate().
*  Chanlk  041220  Call 120293: removed the security check for the CUSTOMER_ORDER_PRICING_API.Get_Sales_Part_Price_Info.
*  NIJALK  050103  Modified validate().
*  Chanlk  050120  Change thevalidate functions of craft,maint org, empno.
*  NAMELK  050120  Materials Tab: Check Box MUL_REQ_LINE added,Set LOV to field ITEM3_CONTRACT. Code added to adjust().
*  NIJALK  050202  Modified detchedPartList(),sparePartObject(),adjust(),validate(),preDefine() and printContents().
*  NIJALK  050224  Modified preDefine, saveReturn. Added deleteItem().
*  NAMELK  050224  Merged Bug 48035 manually.
*  DIAMLK  050301  Replaced the field Pre_Posting_Id by Pre_Accounting_Id.(IID AMEC113 - Mandatory Pre-posting)
*  DIAMLK  050310  Bug ID:122509 - Modified the method okFind().
*  NIJALK  050405  Call 123081: Modified manReserve().
*  NEKOLK  050407  Merged - Bug 48852, Modified issue() and reserve().
*  NIJALK  050407  Call 123086: Modified manReserve(), reserve().
*  NIJALK  050420  Bug 123558: Added field "Sales Part Site" to Time Report Tab.
*  NIJALK  050505  Bug 123698: Set a warning msg when changing state to "Prepared" if WO causes a project exception.
*  NIJALK  050511  Bug 123677: Added RMB "Project Activity Info...".
*  NIJALK  050519  Modified availDetail(), preDefine().
*  SHAFLK  050330  Bug 50258, Modified issue() and manIssue().
*  NIJALK  050527  Merged bug 50258.
*  DiAmlk  050613  Bug ID:124832 - Renamed the RMB Available to Reserve to Inventory Part in Stock... and
*                  modified the method availtoreserve.
*  SHAFLK  050425  Bug 50830, Used mgr.translateJavaScript() method to get the correctly translated strings in JavaScript functions. 
*  NIJALK  050617  Merged bug 50830.
*  DiAmlk  050711  Bug ID:125442 - Modified the method validate. 
*  NIJALK  050805  Bug 126176, Modified issue().
*  NIJALK  050808  Bug 126177: Modified released(),started(),perform().
*  NIJALK  050811  Bug 126137, Modified availtoreserve().
*  NIJALK  050905  Set field AMOUNT read only.
*  SHAFLK  050919  Bug 52880, Modified adjust().
*  NIJALK  051004  Merged bug 52880.
*  THWILK  051026  Added functionality required to implement RMB "Create From Allocation".
*  ERALLK  051214  Bug 54280. Added column 'Quantity Available'.Modified the validate() function.
*  NIJALK  051227  Merged bug 54280.
*  NIJALK  060110  Changed DATE format to compatible with data formats in PG19.
*  NIJALK  060201  Call 132376: Modified newRowITEM1().
*  NIJALK  060202  Call 132375: Replaced method call to Organization_API.Get_Org_Cost() with Work_Order_Planning_Util_Api.Get_Cost().
*  NIJALK  060202  Call 132126: Changed the data type of BUDGET_COST,BUDGET_REVENUE,PLANNED_COST,PLANNED_REVENUE,ACTUAL_COST,ACTUAL_REVENUE to "Money".
*  NIJALK  060228  Call 132375: Added parameter EMP_NO to Work_Order_Planning_Util_Api.Get_Cost() in validate function.
*  NIJALK  060210  Call 133546: Renamed "STANDARD_INVENTORY" with "INVENT_ORDER".
*  NIJALK  060213  Call 132956: Modified projectActivityInfo().
*  SULILK  060304  Call 134906: Modified manReserve(), GetInventoryQuantity(), manIssue().
*  SULILK  060306  Call 135985: Modified checkObjAvailable().
*  NEKOLK  060308  Call 135828: Added refreshForm() and made changes in predefine().
*  ASSALK  060316  Material Issue & Reserve modification. Issue and reserve made available after
*                    unissue all materials.
*  SULILK  060322  Call 135197: Modified preDefine(),run(),validate(),setValuesInMaterials().
*  --------------------------------------------------------------------------
*  NIJALK  060509  Bug 57099, Modified matReqUnissue().
*  NIJALK  060510  Bug 57256, Modified manIssue(), GetInventoryQuantity().
*  NIJALK  060515  Bug 56688, Modified issue(),checkObjAvailable().
*  SHAFLK  060525  Bug 57598, Modified validation for PART_NO and setValuesInMaterials().
*  SHAFLK  060601  Bug 58197, Modified run and getContents().
*  JEWILK  060803  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged bug Id: 58214.
*  JEWILK  060816  Bug 58216, Eliminated SQL Injection security vulnerability.
*  NAMELK  060906  Merged Bug 58216.
*  SHAFLK  060914  Bug 60342, Added new fields TEAM_ID, TEAM_CONTRACT and accordinly changed validations and lov filtering of time report tab and RMB Create from planning.
*  AMNILK  060926  Merged Bug Id: 60342.
*  SHAFLK  061101  Bug 61515, Modified method adjust().
*  AMNILK  061106  Merged LCS Bug ID: 61515.
*  SHAFLK  061005  Bug 60938, Added new RMB "Activity Info..."
*  NAMELK  061107  Merged LCS Bug ID: 60938. 
*  SHAFLK  070116  Bug 62854, Modified validation for Part No.
*  AMNILK  070208  Merged LCS Bug 62854.   
*  AMNILK  070212  Call Id: 140950. Modified method Started(). 
*  SHAFLK  061120  Bug 61466, Added 'Supplier Loaned' stock to Material.
*  ILSOLK  070302  Merged Bug ID 61446.
*  BUNILK  070405  Implemented "MTIS907 New Service Contract - Services" changes.
*  SHAFLK  070228  Bug 63812, Modified printPicList.
*  ILSOLK  070410  Merged Bug ID 63812.
*  NAMELK  070420  Call 141863,Modified run(),cancelled() & javascript code added.
*  ASSALK  070424  Call 142871. Modified getContents().
*  AMDILK  070503  Call Id 142273: Inserted a new RMB "Update Spare Parts in Object" to the materials tab
*  CHANLK  070515  Call Id 144631: Customer info not fetched.
*  CHANLK  070517  Call 144491 Added Tranfered to mobile check box in preDefine()
*  AMDILK  070518  Call Id 144691: Inserted OBJSTATE to the buffer. Modified requisitions()
*  AMDILK  070518  Call Id 144897: Changed the focus after delting a time report line
*  AMDILK  070522  Call Id 144902: Save the previous query when inserting a line from the planning
*  AMDILK  070531  Call Id 145443: Disable the RMB "Update Spare Parts in Object" when spare parts exists 
*  CHANLK  070606  Call 144731 set agreement field read only.
*  ILSOLK  070608  Set WORK_ORDER_CONNECTION_API.HAS_STRUCTURE Method for Structure check box.(Call ID 145955)
*  AMDILK  070608  Call Id 144694: Modified okFindITEM5()
*  ILSOLK  070611  CodeReview for Call Id 145955. 
*  AMDILK  070614  Call Id 144694: Preserve the line information when navigate the last and first record
*  ILSOLK  070709  Eliminated XSS.
*  AMNILK  070712  Elminated SQL Injection.
*  NIJALK  070420  Bug 64572, Modified preDefine().
*  ILSOLK  070718  Merged Bug ID 64572.
*  AMDILK  070731  Removed the scroll buttons of the parent when the child tab(Job) is in new or edit modes
*  AMDILK  070801  Modified run() in order to load the page in Firefox
*  ILSOLK  070905  If WOState in ('RELEASED', 'STARTED', 'WORKDONE','PREPARED','UNDERPREPARATION') Enabled Release RMB in Material.
*                  Modified preDefine()(Call ID 148213)
*  ASSALK  070910  CALL 148510, Modified preDefine(), issue().
*  ASSALK  070918  call 148513, Modified issue().
*  SHAFLK  071108  Bug 67801, Modified validation for STD_JOB_ID.
*  CHANLK  071108  Bug 67871, Modified preDefine()remove headlay.setFieldOrder().
*  SHAFLK  071123  Bug 69392, Checked for ORDER installed.
*  ARWILK  071130  Bug 66406, Added CONN_PM_NO, CONN_PM_REVISION, CONN_PM_JOB_ID.
*  --------------------------------------------------------------------------
* -----------------------APP7.5 SP1-------------------------------------
*  071214  ILSOLK  Bug Id 68773, Eliminated XSS.
*  SHAFLK  071219  Bug 70147, Modified saveReturnITEM6(). 
*  CHANLK  080121  Bug 68947, Added RMB Service Contract Line Search. 
*  SHAFLK  080102  Bug 70891, Modified finished() and finished1().
*  NIJALK  080202  Bug 66456, Modified validate(), GetInventoryQuantity(), availDetail() and getContents().
*  SHAFLK  080106  Bug 70948, Modified lovEmpNo().
*  CHANLK  080205  Bug 66842,Remove maxLength of PM_NO
*  SHAFLK  080130  Bug 70815, Modified run(), issue(), reserve(), preDefine() okFindITEM5() and okFindITEM3(). 
* -----------------------------------------------------------------------
*  AMNILK  080225  Bug Id 70012, Modified COInformation(), getContents().
*  NIJALK  080306  Bug 72202, Modified okFindITEM5().
*  ARWILK  080502  Bug 70920, Overode lovLineNo and lovContractId.
*  CHANLK  080626  Bug 74288, arrange the field order.
*  SHAFLK  080714  Bug 75563, Modified preDefine().
*  SHAFLK  080924  Bug 77304, Modified preDefine().
*  SHAFLK  081121  Bug 78187, Modified issue().
*  SHAFLK  090217  Bug 79436  Modified preDefine(), released() and started(). 
*  CHANLK  090225  Bug 76767, Modified preDefine(), validate(), issue(), reserve().
*  SHAFLK  090305  Bug 80959  Modified preDefine() and added reinit().
*  SHAFLK  090630  Bug 82543  Modified detchedPartList(). 
*  NIJALK  090704  Bug 82543, Modified run() and printContents(). Added refresh().
*  HARPLK  090708  Bug 84436, Modified preDefine(),Validate().
*  CHANLK  090721  Bug 83532, Added RMB Work Order Address.
*  SHAFLK  090810  Bug 85099, Modified run(), detchedPartList() and printContents().
*  SHAFLK  090917  Bug 85917, Modified preDefine().
*  SHAFLK  090921  Bug 85901, Modified preDefine().
*  SUIJLK  090922  Bug 81023, Modified preDefine(), saveReturnItem1, deleteITEM1. Added new method saveNewItem1
*  INROLK  091027  Bug 86298, Modified Validate(), refreshBlocks() , and javaScript.
*  SHAFLK  091110  Bug 87041  Modified newRowITEM5().
*  SUIJLK  091116  Bug 87026  Modified run().
*  CHANLK  091125  Bug 87334  Modified Run() moved call to validate method to the beginning.
*  SHAFLK  100128  Bug 87329  Modified cancelled() and perform().
*  SHAFLK  100210  Bug 88904, Modified printContents().
*  NIJALK  100218  Bug 87766, Modified setCheckBoxValue() and preDefine(). Added new data field CBINASTRUCTURE.
*  SHAFLK  100318  Bug 89298, Modified performItem().
*  SHAFLK  100405  Bug 89883, Modified preDefine().
*  NIJALK  100426  Bug 85045, Modified okFindNeW(), refreshBlocks() and run().
*  NIJALK  100719  Bug 89399, Modified getContents().
*  VIATLK  100721  Bug 91376, Modified printWO() and printAuth().
*  SaFalk  100731  Bug 89703, Added methods deleteRow6(), deleteRowITEM6() and modified run(), preDefine() and printContents().
*  CHANLK  101025  Bug 93736, Modified performItem().
* -----------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import java.util.*;

public class ActiveSeperateReportInWorkOrderSM extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeperateReportInWorkOrderSM");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPHTMLFormatter fmt;
   private ASPForm frm;
   private ASPContext ctx;

   private ASPBlock prntblk;
   private ASPRowSet printset;

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   private ASPPopup headpopup;
   private ASPRowSet eventset;

   private ASPBlock itemblk0;
   private ASPRowSet itemset0;
   private ASPCommandBar itembar0;
   private ASPTable itemtbl0;

   private ASPBlock itemblk1;
   private ASPRowSet itemset1;
   private ASPCommandBar itembar1;
   private ASPTable itemtbl1;
   private ASPBlockLayout itemlay1;

   private ASPBlock itemblk2;
   private ASPRowSet itemset2;
   private ASPCommandBar itembar2;
   private ASPTable itemtbl2;

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

   private ASPBlock tempblk;
   private ASPBlock eventblk1;
   private ASPRowSet eventset1;
   private ASPField f;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private String strLableJ;
   private String sWONo;
   private String crePlanPath;
   private String wordno1;
   private String creFromPlan;
   private int sentrowNo;
   private String pm_no;
   private ASPTransactionBuffer trans;
   private ASPTransactionBuffer trans1;
   private boolean postingflg;
   private String helpbase;
   private String qrystr;
   private String wono;
   private String transFlag;
   private boolean comBarAct;
   private boolean isFind;
   private String comp;
   private String connReconToPlan;
   private boolean sentNoEmployee;
   private boolean sentEmployee;
   private boolean recordCancel;
   private String language;
   private String itemMchCode;
   private String itemContract;
   private String itemWoNo;
   private String itemSpareId;
   private String nQtyLeft;
   private String manReserveOk;
   private boolean matSingleMode;
   private String layout;
   private String hdrow;
   private String noteTextEntered;
   private String sent_wo_no;
   private String sent_emp_name;
   private String sent_description;
   private String sent_department;
   private String sent_craft;
   private String sent_team_contract;
   private String sent_team_id;
   private String sent_emp_id;
   private String sent_planLineNo;
   private String sent_site;
   private String sent_salesPartNo;
   private String sent_priceListNo;
   private String sent_salesPrice;
   private String sent_empNo;
   private String canceled_wo_no;
   private int currrow;
   private int headrowno;
   private int headsetRowNo;
   private int item3rowno;
   private int currHead;
   private int currrowItem3;
   private int headCurrrow;
   private String woNo;
   private String workOrderCostType;
   private String cbwarranty;
   private String cbhasdocuments;
   private String cbhasstructure;
   private String cbnote;
   private String clientValue;
   private String calling_url;
   private String callingurl1;
   private String current_url;
   private int nCount;
   private int currRow;
   private String eventValItem;
   private ASPBuffer prePostBuffer;
   private int currrowItem;
   private String bResAllowed;
   private String salesPart;
   private String customerNo;
   private String agreementId;
   private String head_command;
   private String objlevel;
   private String lblempno;
   private String lblempsig;
   private String lblrepBy1;
   private String empno;
   private String empsig;
   private String repBy1;
   private String title_;
   private String strLableA;
   private String strLableB;
   private String strLableC;
   private String strLableD;
   private String strLableE;
   private String strLableF;
   private String strLableG;
   private String strLableH;
   private String strLableI;
   private int curentrow;
   private int rowToEdit;
   private int item4CurrRow;
   private String sent_discount;
   private boolean emptyQry = false;

   private boolean isSecurityChecked;
   private ASPBuffer actionsBuffer;
   private String showMat;
   private String openCreRepNonSer;
   private String creRepNonSerPath;

   //Web Alignment - replacing blocks with tabs
   private ASPTabContainer tabs;   
   //

   //Web Alignment - replace Links with RMBs
   private boolean bOpenNewWindow;
   private String urlString;
   private String newWinHandle;
   //
   private ASPCommand cmd;
   private String repair;
   private String unissue;
   private String creRepWO;
   private String headMchCode;
   private String headMchDesc;

   private ASPBuffer ret_data_buffer;
   private String lout;
   private boolean bException;
   private String headNo;
   private boolean bFirstRequest;
   private String isSecure[];
   private ASPTransactionBuffer secBuff;
   private boolean bCancelPage;
   private boolean bvimwo;
   private String  wonumber;
//  Bug 68947, Start
   private boolean bFECust;
//  Bug 68947, End
   //Bug Id 70012,Start
   private boolean bPcmsciExist;
   // Bug Id 70012,End
   //Bug 85045, Start
   private String sent_org_contract;
   //Bug 85045, End
   //Bug 89703, Start
   private String hasPlanning;
   //Bug 89703, End

   //===============================================================
   // Construction 
   //===============================================================
   public ActiveSeperateReportInWorkOrderSM(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() 
   {
      crePlanPath =  wordno1 ;
      sentrowNo =  1;
      openCreRepNonSer = creRepNonSerPath;
      creRepNonSerPath = "";

      ASPManager mgr = getASPManager(); 

      fmt = mgr.newASPHTMLFormatter();
      frm = mgr.getASPForm();
      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      trans1 = mgr.newASPTransactionBuffer();

      postingflg = ctx.readFlag("POST",false);
      helpbase = ctx.readValue("HELPBASE","");

      strLableA = ctx.readValue("LABLEA",strLableA );
      strLableB= ctx.readValue("LABLEB",strLableB );
      strLableC = ctx.readValue("LABLEC",strLableC );
      strLableD = ctx.readValue("LABLED",strLableD );
      strLableE = ctx.readValue("LABLEE",strLableE );
      strLableF = ctx.readValue("LABLEF",strLableF );
      strLableG = ctx.readValue("LABLEG",strLableG );
      strLableH = ctx.readValue("LABLEH",strLableH );
      strLableI = ctx.readValue("LABLEI",strLableI );
      strLableJ = ctx.readValue("LABLEJ",strLableJ );

      curentrow = ctx.readNumber("CURRENTROW",0);
      qrystr = ctx.readValue("QRYSTR","");  
      sWONo = ctx.readValue("VALWONORESM",sWONo); 
      wono = ctx.readValue("WONOV","");
      transFlag = ctx.readValue("TRANSFLAG","false");
      comBarAct = ctx.readFlag("COMBARACT",false);
      isFind = ctx.readFlag("ISFIND",false);
      comp = ctx.readValue("COMP");
      crePlanPath = ctx.readValue("CREPL",crePlanPath);
      creFromPlan = ctx.readValue("CREFRMPL","flase");
      connReconToPlan = ctx.readValue("CONNREFRMPL","flase");
      rowToEdit = ctx.readNumber("ROTEDTI",1);
      sentNoEmployee = ctx.readFlag("SENTNOEM",false);
      sentEmployee = ctx.readFlag("SENTEMP",false);
      sentrowNo = ctx.readNumber("SENTRWON",sentrowNo);
      recordCancel = ctx.readFlag("RECCENR",false);
      item4CurrRow = ctx.readNumber("ITEM4CURRROW",0);
      language = mgr.getConfigParameter("APPLICATION/LANGUAGE");
      itemMchCode = ctx.readValue("ITEMMCHCODE","");
      itemContract = ctx.readValue("ITEMCONTRACT","");
      itemWoNo = ctx.readValue("ITEMWONO","");
      itemSpareId = ctx.readValue("ITEMSPAREID","");
      nQtyLeft = ctx.readValue("NQTYLEFT","");
      manReserveOk = ctx.readValue("MANRESERVEOK","");
      matSingleMode = ctx.readFlag("MATSINGLEMODE",false);

      isSecurityChecked = ctx.readFlag("SECURITYCHECKED",false);
      actionsBuffer = ctx.readBuffer("ACTIONSBUFFER");
      showMat = ctx.readValue("SHMAT",""); 
      repair =  ctx.readValue("REPAIR","FALSE"); 
      unissue = ctx.readValue("UNISSUE","FALSE"); 
      creRepWO = ctx.readValue("AUTOREP","FALSE");
      headMchCode = ctx.readValue("HEADMCHCODE","");
      headMchDesc = ctx.readValue("HEADMCHDESC","");
      //Bug Id 70012, Start
      bPcmsciExist = ctx.readFlag("PCMSCIEXIST",false);
      //Bug Id 70012, End

      if (mgr.commandBarActivated())
      {
         comBarAct = true; 
         clearItem4();
         eval(mgr.commandBarFunction());
         if ("ITEM5.SaveReturn".equals(mgr.readValue("__COMMAND")))
         {
            headset.refreshRow();
            itemset.refreshRow();
            setValuesInMaterials();
         }

      }

      //Bug 87334, Start
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      //Bug 87334, End
      //Bug 89703, Start
      else if ("AAAA".equals(mgr.readValue("HASPLANNING")))
         deleteRow6("YES");
      else if ("BBBB".equals(mgr.readValue("HASPLANNING")))
         deleteRow6("NO");
      //Bug 89703, End

      else if ("OK".equals(mgr.readValue("FIRST_REQUEST")) || ( !mgr.isExplorer() && !(mgr.isEmpty(mgr.readValue("NEWCREVAL_WO","")))) )
      {
         sent_wo_no = mgr.readValue("NEWCREVAL_WO","");

         if (mgr.isEmpty(mgr.getQueryStringValue("NEWCANCELVAL_WO")))
         {
            sent_emp_name = mgr.readValue("NEWCREVAL_EMPID","");
            sent_description = mgr.readValue("NEWCREVAL_DESCRIPTION","");
            sent_department = mgr.readValue("NEWCREVAL_ORG_CODE","");
	    //Bug 85045, Start
            sent_org_contract = mgr.readValue("NEWCREVAL_ORG_CONTRACT","");
	    //Bug 85045, End
            sent_craft = mgr.readValue("NEWCREVAL_ROLE_CODE","");
            sent_team_contract = mgr.readValue("NEWCREVAL_TEAM_CONTRACT","");
            sent_team_id = mgr.readValue("NEWCREVAL_TEAM_ID","");
            sent_emp_id = mgr.readValue("NEWCREVAL_SIGN","");
            sent_planLineNo = mgr.readValue("NEWCREVAL_PLAN_LINE_NO","");
            sent_site = mgr.readValue("NEWCREVAL_CONTRACT","");
            sent_salesPartNo = mgr.readValue("NEWCREVAL_SALNO","");
            sent_priceListNo = mgr.readValue("NEWCREVAL_PRICE_LIST_NO","");
            sent_salesPrice = mgr.readValue("NEWCREVAL_SALES_PRICE","");
            sent_discount = mgr.readValue("NEWCREVAL_DISCOUNT","");
            creFromPlan = "false"; 
            recordCancel = false;
            sentEmployee = true;
            sentNoEmployee = false;             
         }
         else
         {
            canceled_wo_no = mgr.readValue("NEWCANCELVAL_WO","");
            recordCancel = true;
            sentEmployee = true;
            sentNoEmployee = false;
            creFromPlan = "false"; 
         }
         //Call Id : 114250
         tabs.setActiveTab(2);  
         //
         okFindNeW();        
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

         clearItem4();
         okFind(); 
         headset.goTo(toInt(hdrow));
         trans.clear();
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("NOTETEXTENT")))
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();   

         if (headset.countRows() != 1)
         {
            okFindITEM1();
            okFindITEM3();
            okFindITEM4();
         }

         if (itemset3.countRows() > 0)
         {
            noteTextEntered = mgr.readValue("NOTETEXTENT","");
            ASPBuffer buff = itemset3.getRow();
            buff.setValue("SNOTETEXT",noteTextEntered);
            itemset3.setRow(buff);
         }
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("DOCANCEL")))
      {
         okFind();
         doCancel();
         if (headset.countRows() != 1)
         {
            String s_sel_wo = ctx.getGlobal("WONOGLOBAL");
            int sel_wo = Integer.parseInt(s_sel_wo);
            headset.goTo(sel_wo);      

            okFindITEM1();
            okFindITEM3();
            okFindITEM4();
         }

      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("CANCELCAUSEDLG")))
      {
         okFind(); 
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("TIMEREPORT")))
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();   

         if (headset.countRows() != 1)
         {
            headNo = ctx.getGlobal("HEADGLOBAL");
            int headSetNo = Integer.parseInt(headNo);
            headset.goTo(headSetNo);   
            okFindITEM1();
            okFindITEM3();
            okFindITEM4();
            okFindITEM6();

         }
         tabs.setActiveTab(2);
      }

      else if (!mgr.isEmpty(mgr.getQueryStringValue("OKSPAREPART")))
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();   

         if (headset.countRows() != 1)
         {
            String s_sel_wo = ctx.getGlobal("WONOGLOBAL");
            int sel_wo = Integer.parseInt(s_sel_wo);
            headset.goTo(sel_wo);      

            okFindITEM1();
            okFindITEM3();
            okFindITEM4();
         }
         matSingleMode = true;
      }

      else if (!mgr.isEmpty(mgr.getQueryStringValue("OKMANISSUE")) && !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();  
         activateMaterialTab();

         if (headset.countRows() != 1)
         {
            okFindITEM1();
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
            String s_sel_wo = ctx.getGlobal("WONOGLOBAL");
            int sel_wo = Integer.parseInt(s_sel_wo);
            headset.goTo(sel_wo);      
            okFindITEM1();
            okFindITEM3();
            okFindITEM4();
         }
         matSingleMode = true;
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("OKMANUNRESERVE")) && !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();   

         if (headset.countRows() != 1)
         {
            okFindITEM1();
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
            okFindITEM1();
            okFindITEM3();
            okFindITEM4();
         }
         matSingleMode = true;
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("OKDETATTACHED")) && !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();   

         if (headset.countRows() != 1)
         {
            String s_sel_wo = ctx.getGlobal("WONOGLOBAL");
            int sel_wo = Integer.parseInt(s_sel_wo);
            headset.goTo(sel_wo);      
            okFindITEM1();
            okFindITEM3();
            okFindITEM4();
         }
         matSingleMode = true;
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
         showMat = mgr.readValue("SHOWMAT","");  
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();
         if ("TRUE".equals(showMat))
            this.activateMaterialTab();
      }
      //Bug id 87026, start
      else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
      {
          showMat = mgr.readValue("SHOWMAT","");
          headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
          okFind();
          if("TRUE".equals(showMat))
             this.activateMaterialTab();
      }
      //Bug id 87026, end
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
         clearItem4();
         okFind();
      }
      else if (mgr.dataTransfered())
      {
         okFind();
      }
      //  Bug 68947, Start
      else if (!mgr.isEmpty(mgr.readValue("TEMPCONTRACTID")))
         updateContract();
//  Bug 68947, End

      else if ("CCCC".equals(mgr.readValue("BUTTONVAL")))
      {
         unissue="FALSE";
         clearItem4();
         reported1();
      }

      else if ("REPORT".equals(mgr.readValue("STATEVAL")))
      {
         unissue="FALSE";
         clearItem4();
         reported();
      }

      else if ("FINIS".equals(mgr.readValue("STATEVAL")))
      {
         repair="FALSE";
         unissue="FALSE";
         clearItem4();
         finished();
      }

      else if ("AAAA".equals(mgr.readValue("BUTTONVAL")))
      {
         repair="FALSE";
         unissue="FALSE";
         clearItem4();
         finished1();
      }
      else if ("BBBB".equals(mgr.readValue("BUTTONVAL")))
      {
         repair="FALSE";
         unissue="FALSE";
         clearItem4();
         finished1();
      }
      else if ("CANCE".equals(mgr.readValue("STATEVAL")))
      {
         clearItem4();  
         cancelled();  
      }

      else if (!mgr.isEmpty(mgr.getQueryStringValue("NEWCREVAL_WO")))
         bFirstRequest =true;

      else if (!mgr.isEmpty(mgr.getQueryStringValue("NEWCONNVAL_WO")))
      {
         sent_wo_no = mgr.readValue("NEWCONNVAL_WO","");
         sentEmployee = false;
         sentNoEmployee = false;

         if (!mgr.isEmpty(mgr.getQueryStringValue("NEWCONNVAL_CONTRACT")))
         {
            sent_site = mgr.readValue("NEWCONNVAL_CONTRACT","");
            sent_salesPartNo = mgr.readValue("NEWCONNVAL_SALNO","");
            sent_planLineNo = mgr.readValue("NEWCONNVAL_PLANO","");
            sentrowNo = toInt(mgr.readNumberValue("NEWCONNVAL_ROWNO"));
            sentNoEmployee = true;   
            recordCancel = false; 
            connReconToPlan = "false";         
         }

         if (!mgr.isEmpty(mgr.getQueryStringValue("NEWCANCELVAL_WO")))
         {
            canceled_wo_no = mgr.readValue("NEWCANCELVAL_WO","");
            recordCancel = true;

            sentEmployee = false;
            sentNoEmployee = true;
            connReconToPlan = "false";
            sentrowNo = toInt(mgr.readNumberValue("NEWCONNVAL_ROWNO"));
         }
         //Call Id : 114250
         tabs.setActiveTab(2);  
         //
         okFindNeW();      
      }
      else if ("TRUE".equals(mgr.readValue("REFRESH_FLAG")))
      {
         sent_wo_no = ctx.findGlobal("NEWCREVAL_WO");

         if (! mgr.isEmpty(sent_wo_no))
         {
            sent_emp_name = ctx.findGlobal("NEWCREVAL_EMPID");
            sent_description = ctx.findGlobal("NEWCREVAL_DESCRIPTION");
            sent_department = ctx.findGlobal("NEWCREVAL_ORG_CODE");
	    //Bug 85045, Start
            sent_org_contract = ctx.findGlobal("NEWCREVAL_ORG_CONTRACT");
	    //Bug 85045, End
            sent_craft = ctx.findGlobal("NEWCREVAL_ROLE_CODE");
            sent_team_contract = ctx.findGlobal("NEWCREVAL_TEAM_CONTRACT");
            sent_team_id = ctx.findGlobal("NEWCREVAL_TEAM_ID");
            sent_emp_id = ctx.findGlobal("NEWCREVAL_SIGN");
            sent_planLineNo = ctx.findGlobal("NEWCREVAL_PLAN_LINE_NO");
            sent_site = ctx.findGlobal("NEWCREVAL_CONTRACT");
            sent_salesPartNo = ctx.findGlobal("NEWCREVAL_SALNO");
            sent_priceListNo = ctx.findGlobal("NEWCREVAL_PRICE_LIST_NO");
            sent_salesPrice = ctx.findGlobal("NEWCREVAL_SALES_PRICE");
            sent_discount = ctx.findGlobal("NEWCREVAL_DISCOUNT");
            sent_empNo = ctx.findGlobal("NEWCREVAL_EMP");
            creFromPlan = "false"; 
            recordCancel = false;
            sentEmployee = true;
            sentNoEmployee = false;             
         }
         else
         {
            canceled_wo_no = ctx.readValue("NEWCANCELVAL_WO","");
            recordCancel = true;
            sentEmployee = true;
            sentNoEmployee = false;
            creFromPlan = "false"; 
         }

         refreshBlocks();
      }

      checkObjAvailable();
      adjust();
      adjustActions();

      tabs.saveActiveTab();

      ctx.writeFlag("POST",postingflg );
      ctx.writeValue("HELPBASE",helpbase);

      ctx.writeValue("LABLEA",strLableA );
      ctx.writeValue("LABLEB",strLableB );
      ctx.writeValue("LABLEC",strLableC );
      ctx.writeValue("LABLED",strLableD );
      ctx.writeValue("LABLEE",strLableE );
      ctx.writeValue("LABLEF",strLableF );
      ctx.writeValue("LABLEG",strLableG );
      ctx.writeValue("LABLEH",strLableH );
      ctx.writeValue("LABLEI",strLableI );
      ctx.writeValue("LABLEJ",strLableJ );
      ctx.writeValue("VALWONORESM",sWONo);
      ctx.writeNumber("CURRENTROW",curentrow);
      ctx.writeValue("QRYSTR",qrystr);
      ctx.writeValue("CREPL",crePlanPath);
      ctx.writeValue("CREFRMPL",creFromPlan);
      ctx.writeValue("CONNREFRMPL",connReconToPlan);
      ctx.writeNumber("ROTEDTI",rowToEdit);
      ctx.writeFlag("SENTNOEM",sentNoEmployee);
      ctx.writeNumber("SENTRWON",sentrowNo);
      ctx.writeFlag("RECCENR",recordCancel); 
      ctx.writeNumber("ITEM4CURRROW",item4CurrRow);
      ctx.writeFlag("MATSINGLEMODE",matSingleMode);

      ctx.writeFlag("SECURITYCHECKED",isSecurityChecked);
      ctx.writeBuffer("ACTIONSBUFFER",actionsBuffer);
   }

//-----------------------------------------------------------------------------
//-----------------------------  PERFORM FUNCTION  ----------------------------
//-----------------------------------------------------------------------------

   public void perform(String command)
   {
      ASPManager mgr = getASPManager();
      // Web Alignment - Enable Multirow Action
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

         if (!command.equals("FINISH__") && !command.equals("CANCEL__"))
         {
            headset.goTo(currrow);
            headset.refreshRow();
            if (headset.countRows()>0)
            {
               if (itemset3.countRows()>0)
                  itemset3.refreshAllRows();
               if (itemset.countRows()>0)
                  itemset.refreshAllRows();
            }
         }
      }  
   }

   //---------------------------------------------------------
   // -----------Check Tools /Facility  Site ________________

   public boolean checkToolFacilitySite()
   {
      ASPManager mgr = getASPManager();

      String WoNo = headset.getRow().getValue("WO_NO");
      trans.clear();


      cmd = trans.addCustomCommand("WOTOOLFACILITY","Work_Order_Tool_Facility_API.Check_Valid_Sites");
      cmd.addParameter("HEAD_ISVALID");
      cmd.addParameter("HEAD_FACSEQUENCE");
      cmd.addParameter("WO_NO",WoNo);

      trans = mgr.perform(trans);

      String isValid = trans.getValue("WOTOOLFACILITY/DATA/HEAD_ISVALID");
      String facilitySequence = trans.getValue("WOTOOLFACILITY/DATA/HEAD_FACSEQUENCE");


      if ("TRUE".equals(isValid))
         return true;
      else
         return false;  
   }   

   public void reinit()
   {
      ASPManager mgr = getASPManager();
      perform("RE_INIT__");
   }

   public void observed()
   {
      ASPManager mgr = getASPManager();

      // Web Alignment - Enable Multirow Action
      perform("CONFIRM__");
   }


   public void underPrep()      
   {
      ASPManager mgr = getASPManager();

      // Web Alignment - Enable Multirow Action
      perform("TO_PREPARE__");
   }


   public void prepared()
   {
      ASPManager mgr = getASPManager();

      // Web Alignment - Enable Multirow Action
      if (checkToolFacilitySite())
      {
         trans.clear();
         perform("PREPARE__") ; 
      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2NOTVALID: Please enter Tools and Facility Details."));
   }


   public void released()
   {
      ASPManager mgr = getASPManager();
      int count;

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());

      String eventVal = headset.getRow().getValue("OBJEVENTS");

      // Web Alignment - Enable Multirow Action
      if (checkToolFacilitySite())
      {
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
            currrow = headset.getCurrentRowNo();
         }

         trans.clear();

         for (int i = 0; i < count; i ++)
         {
            eventVal = headset.getRow().getValue("OBJEVENTS");

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
            if (itemset3.countRows()>0)
               itemset3.refreshAllRows();
            if (itemset.countRows()>0)
               itemset.refreshAllRows();
         }
      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2NOTVALID: Please enter Tools and Facility Details."));
   }


   public void started()
   {
      ASPManager mgr = getASPManager();
      int count;
      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());

      String eventVal = headset.getRow().getValue("OBJEVENTS");

      // Web Alignment - Enable Multirow Action
      if (checkToolFacilitySite())
      {
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
            currrow = headset.getCurrentRowNo();
         }

         trans.clear();

         for (int i = 0; i < count; i ++)
         {
            eventVal = headset.getRow().getValue("OBJEVENTS");

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
            if (itemset3.countRows()>0)
               itemset3.refreshAllRows();
            if (itemset.countRows()>0)
               itemset.refreshAllRows();
         }
      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2NOTVALID: Please enter Tools and Facility Details."));
   }


   public void workDone()
   {
      ASPManager mgr = getASPManager();

      // Web Alignment - Enable Multirow Action
      if (checkToolFacilitySite())
      {
         trans.clear();
         perform("WORK__"); 
      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2NOTVALID: Please enter Tools and Facility Details."));
   }


   public void reported()                 
   {
      ASPManager mgr = getASPManager();
      unissue = "FALSE";
      // Web Alignment - Enable Multirow Action
      if (checkToolFacilitySite())
      {
         if (!CheckAllMaterialIssued())
         {
            trans.clear();
            perform("REPORT__"); 
         }
         else
         {
            ctx.setCookie( "PageID_my_cookie2", "TRUE" );
            unissue = "TRUE";
         }
      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERSMNOTVALID: Please enter Tools and Facility Details."));

   }

   public void reported1()                 
   {
      ASPManager mgr = getASPManager();


      if (checkToolFacilitySite())
      {
         trans.clear();
         perform("REPORT__"); 
      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERSMNOTVALID: Please enter Tools and Facility Details."));

   }

   public void finished()
   {
      ASPManager mgr = getASPManager();
      unissue = "FALSE";

      if (checkToolFacilitySite())
      {
         if ((!CheckObjInRepairShop()) && (!CheckAllMaterialIssued()))
         {

            //Bug 70891, start
            int rowno = headset.getCurrentRowNo();
            int rows =  headset.countRows(); 
            //Bug 70891, end  
            trans.clear();
            perform("FINISH__");

            //Bug 70891, start
            if (rows > 1 )
            {
               headset.goTo(rowno);
               headset.clearRow();
               if (headlay.isSingleLayout() && (headset.countRows()>0))
               {
                  okFindITEM1();
                  okFindITEM3();
                  okFindITEM4();
               }
            }
            else
               mgr.redirectTo("ActiveSeperateReportInWorkOrderSM.page");
            //Bug 70891, end
         }
         else if (CheckAllMaterialIssued())
         {
            ctx.setCookie( "PageID_my_cookie1", "TRUE" );
            unissue = "TRUE";
         }

         if (CheckObjInRepairShop())
         {
            ctx.setCookie( "PageID_my_cookie", "TRUE" ); 
            repair = "TRUE";
         }
      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2NOTVALID: Please enter Tools and Facility Details."));

   }

   public void finished1()
   {
      ASPManager mgr = getASPManager();

      if (checkToolFacilitySite())
      {
         //Bug 70891, start
         int rowno = headset.getCurrentRowNo();
         int rows =  headset.countRows(); 
         //Bug 70891, end  
         trans.clear();
         perform("FINISH__");

         //Bug 70891, start
         if (rows > 1 )
         {
            headset.goTo(rowno);
            headset.clearRow();
            if (headlay.isSingleLayout() && (headset.countRows()>0))
            {
               okFindITEM1();
               okFindITEM3();
               okFindITEM4();
            }
         }
         else
            mgr.redirectTo("ActiveSeperateReportInWorkOrderSM.page");
         //Bug 70891, end
      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2NOTVALID: Please enter Tools and Facility Details."));

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

      urlString = mgr.getURL();
      ctx.setGlobal("CALLING_URL", urlString);
      ASPBuffer buffer = mgr.newASPBuffer();
      ASPBuffer row = buffer.addBuffer("0");
      row.addItem("CUSTOMER_NO", headset.getRow().getValue("CUSTOMER_NO"));
      row.addItem("WO_NO", headset.getRow().getValue("WO_NO"));
      row.addItem("CONTRACT", headset.getRow().getValue("CONTRACT"));
      row.addItem("MCH_CODE", headset.getRow().getValue("MCH_CODE"));
      row.addItem("WORK_TYPE_ID", headset.getRow().getValue("WORK_TYPE_ID"));

      bOpenNewWindow = true;
      urlString = createTransferUrl("ServiceContractLineSearchDlg.page", buffer);
      newWinHandle = "srvConLineSearch";

   }

   public void updateContract()
   {
      ASPManager mgr = getASPManager();
      if (!headlay.isEditLayout())
         headlay.setLayoutMode(headlay.EDIT_LAYOUT);
      ASPBuffer r = headset.getRow();
      r.setValue("CONTRACT_ID",mgr.readValue("TEMPCONTRACTID"));
      r.setValue("LINE_NO",mgr.readValue("TEMPLINENO"));
      headset.setRow(r);
      bFECust = true;
   }
   //Bug 68947, End   

// Bug 83532, Start
   public void workorderaddr()
   {
      ASPManager mgr = getASPManager();

      int count = 0;
      ASPBuffer transferBuffer;
      ASPBuffer rowBuff;

      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();
      bOpenNewWindow = true;

      transferBuffer = mgr.newASPBuffer();
      rowBuff = mgr.newASPBuffer();
      rowBuff.addItem("WO_NO", headset.getRow().getValue("WO_NO"));

      transferBuffer.addBuffer("DATA", rowBuff);

      ctx.setCookie("PageID_CurrentWindow", "*"); 
      urlString = createTransferUrl("../pcmw/WorkOrderAddressDlg.page?__DYNAMIC_DEF_KEY=ACTIVE_SEPARATE", transferBuffer);
      newWinHandle = "workorderaddr"; 
   }
//   Bug 83532, End

   public void disconnectFromActivity()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.store();
      else
      {
         headset.unselectRows();
         headset.selectRow();
      }

      int nRow = headset.getCurrentRowNo();
      headset.setNumberValue("ACTIVITY_SEQ", mgr.readNumberValue("ACTIVITY_SEQ_PROJ"));
      mgr.submit(trans);
      headset.goTo(nRow);
      headset.refreshRow();
      if ("1".equals(headset.getRow().getValue("EXCEPTION_EXISTS")))
         bException = true;
      else
         bException = false;
   }

   public void connectToActivity()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.store();
      else
      {
         headset.unselectRows();
         headset.selectRow();
      }

      int nRow = headset.getCurrentRowNo();
      headset.setNumberValue("ACTIVITY_SEQ", mgr.readNumberValue("ACTIVITY_SEQ_PROJ"));
      mgr.submit(trans);
      headset.goTo(nRow);
      headset.refreshRow();
      if ("1".equals(headset.getRow().getValue("EXCEPTION_EXISTS")))
         bException = true;
      else
         bException = false;
   }

   public void projectActivityInfo()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      String act_seq = mgr.getASPField("ACTIVITY_SEQ1").formatNumber(headset.getRow().getNumberValue("ACTIVITY_SEQ"));

      ctx.setCookie("PageID_CurrentWindow", "*"); 
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

      ctx.setCookie("PageID_CurrentWindow", "*");
      bOpenNewWindow = true;

      urlString = "../projw/Activity.page?ACTIVITY_SEQ=" + mgr.URLEncode(act_seq); 

      newWinHandle = "ActivityInfo"; 
   }

   public void cancelled()
   {
      ASPManager mgr = getASPManager();

      bvimwo = false;

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());

      int rowno = headset.getCurrentRowNo();
      int rows =  headset.countRows(); 

      // Web Alignment - Enable Multirow Action
      if (!mgr.isEmpty(mgr.getQueryStringValue("DOCANCEL")))
      {
         trans.clear();
         perform("CANCEL__");
      }
      else
      {
         // Let the user enter cancellation cause for WOs from vim.
         if (mgr.isPresentationObjectInstalled("VIMW/CancelCause.page"))
         {
            trans.clear();
            cmd = trans.addCustomFunction("WOVIM","Work_Order_From_Vim_API.Is_Vim_Work_Order","ISVIM"); 
            cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

            trans = mgr.perform(trans);

            String wovim = trans.getValue("WOVIM/DATA/ISVIM");

            if ("TRUE".equals(wovim))
            {
               String calling_url_wo = mgr.getURL();
               bCancelPage = true;
               wonumber = headset.getRow().getValue("WO_NO");
               bvimwo = true;
               //ctx.setGlobal("CALLING_URL_WO",calling_url_wo);
               //mgr.redirectTo("EnterCancelCauseDlg.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"))); 

            }
         }
         if (!bvimwo)
         {
            trans.clear();
            perform("CANCEL__");
         }
      }
      //
      if (rows > 1 ) {
            headset.goTo(rowno);
            headset.clearRow();
            if (headlay.isSingleLayout() && (headset.countRows()>0))
            {
                okFindITEM1();
                okFindITEM3();
                okFindITEM4();
                okFindITEM6();
            }
       }
       else
           headset.clearRow();
   }

   public void doCancel()
   {
      ASPManager mgr = getASPManager();
      // Web Alignment - Enable Multirow Action
      if (!"FALSE".equals(mgr.getQueryStringValue("DOCANCEL")))
      {
         trans.clear();
         perform("CANCEL__");
         mgr.redirectTo("ActiveSeparate2ServiceManagement.page");
      }

   }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTION  ------------------------------
//-----------------------------------------------------------------------------
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
      String txt;
      ASPTransactionBuffer secBuff;
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

      String sClientOwnershipConsignment = "CONSIGNMENT"  ;


      String val = mgr.readValue("VALIDATE");

      if ("NOTE".equals(val))
      {
         String c_gen_note;
         String cmb_gen_note;

         if (!(mgr.isEmpty(mgr.readValue("NOTE"))))
         {
            c_gen_note ="TRUE";
            cmb_gen_note = "0";
         }
         else
         {
            c_gen_note = "FALSE";
            cmb_gen_note = "1";
         }

         txt =(mgr.isEmpty(c_gen_note) ? "": c_gen_note) + "^"+(mgr.isEmpty(cmb_gen_note) ? "": cmb_gen_note) + "^";

         mgr.responseWrite(txt); 
      }
      else if ("CB_GENERATE_NOTE".equals(val))
      {
         String c_gen_note;
         String cmb_gen_note;

         c_gen_note = mgr.readValue("CB_GENERATE_NOTE","");

         if ("TRUE".equals(c_gen_note))
            cmb_gen_note = "0";
         else
            cmb_gen_note = "1";

         txt =(mgr.isEmpty(cmb_gen_note) ? "": cmb_gen_note) + "^";

         mgr.responseWrite(txt); 
      }
      else if ("REAL_S_DATE".equals(val))
      {
         ASPBuffer buff = mgr.newASPBuffer();
         buff.addFieldItem("REAL_S_DATE",mgr.readValue("REAL_S_DATE"));
         mgr.responseWrite(mgr.readValue("REAL_S_DATE"));
      }
      else if ("REAL_F_DATE".equals(val))
      {
         ASPBuffer buff = mgr.newASPBuffer();
         buff.addFieldItem("REAL_F_DATE",mgr.readValue("REAL_F_DATE"));
         mgr.responseWrite(mgr.readValue("REAL_F_DATE"));
      }
      else if ("CUSTOMER_NO".equals(val))
      {
         secBuff = mgr.newASPTransactionBuffer();
         secBuff.addSecurityQuery("Cust_Ord_Customer");

         secBuff = mgr.perform(secBuff);

         if (secBuff.getSecurityInfo().itemExists("Cust_Ord_Customer"))
         {
            cmd = trans.addCustomFunction("ISCRESTOP","Cust_Ord_Customer_API.Customer_Is_Credit_Stopped","CREDITSTOP");
            cmd.addParameter("CUSTOMER_NO");
            cmd.addParameter("COMPANY");
         }

         cmd = trans.addCustomFunction("CUSTNAME","CUSTOMER_INFO_API.Get_Name","CUSTOMERNAME");
         cmd.addParameter("CUSTOMER_NO");

         trans = mgr.validate(trans);

         if (secBuff.getSecurityInfo().itemExists("Cust_Ord_Customer"))
            txt = trans.getValue("ISCRESTOP/DATA/CREDITSTOP") +"^"+trans.getValue("CUSTNAME/DATA/CUSTOMERNAME") +"^" ;
         else
            txt = "2" +"^"+trans.getValue("CUSTNAME/DATA/CUSTOMERNAME") +"^";

         mgr.responseWrite(txt);
      }
      else if ("EMP_NO".equals(val))
      {
         boolean securityOk = false;
         String strCatalogNo = null;
         String strCatalogDesc = null;
         String strCatalogNo1 = null;
         String strCatalogDesc1 = null;
         String strRoleCode = "";
         String strDesc = "";
         String strOrgCode;
         String strSig = "";
         String strName = "";
         String strConcatDesc;
         String sDefRole = "";  
         String sDefOrg = mgr.readValue("ITEM1_ORG_CODE");

         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[4];
         String emp_id = "";
         String emp_sign = "";
         String site = "";
         String new_sign = mgr.readValue("EMP_NO","");

         if (new_sign.indexOf("^",0)>0)
         {
            for (i=0 ; i<4; i++)
            {
               endpos = new_sign.indexOf("^",startpos);
               reqstr = new_sign.substring(startpos,endpos);
               ar[i] = reqstr;
               startpos= endpos+1;
            }
            emp_sign = ar[0];
            emp_id = ar[1];
            site = ar[3];

         }
         else
         {
            emp_id = mgr.readValue ("EMP_NO");;
            emp_sign = mgr.readValue ("EMP_SIGNATURE");
            site= mgr.readValue ("ITEM1_CONTRACT");
         }
         secBuff = mgr.newASPTransactionBuffer();
         secBuff.addSecurityQuery("SALES_PART");
         secBuff = mgr.perform(secBuff);

         if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
            securityOk = true;

         cmd = trans.addCustomFunction("PARA1", "Employee_Role_API.Get_Default_Role", "ROLE_CODE" );
         cmd.addParameter("ITEM1_COMPANY",mgr.readValue("ITEM1_COMPANY",""));
         cmd.addParameter("EMP_NO",emp_id);

         cmd = trans.addCustomFunction("PARA2", "Role_API.Get_Description", "CMNT" );
         cmd.addReference("ROLE_CODE", "PARA1/DATA");

         cmd = trans.addCustomFunction("PARA3", "Employee_API.Get_Organization", "DEPART" );
         cmd.addParameter("ITEM1_COMPANY");
         cmd.addParameter("EMP_NO",emp_id);

         cmd = trans.addCustomFunction("PARA4", "Company_Emp_API.Get_Person_Id", "EMP_SIGNATURE" );
         cmd.addParameter("ITEM1_COMPANY");
         cmd.addParameter("EMP_NO",emp_id);

         cmd = trans.addCustomFunction("PARA5", "Person_Info_API.Get_Name", "NAME" );
         cmd.addReference("EMP_SIGNATURE", "PARA4/DATA");

         if (securityOk)
         {
            cmd = trans.addCustomFunction("CATALO","Role_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addReference("ROLE_CODE", "PARA1/DATA");

            cmd = trans.addCustomFunction("CATDESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addReference("CATALOG_NO", "CATALO/DATA");
         }

         trans = mgr.validate(trans);

         if (securityOk)
         {
            strCatalogNo1 = trans.getValue("CATALO/DATA/CATALOG_NO");
            strCatalogDesc1= trans.getValue("CATDESC/DATA/SALESPARTCATALOGDESC");
            if (!mgr.isEmpty(strCatalogNo1))
            {
               strCatalogNo   = strCatalogNo1;
               strCatalogDesc = strCatalogDesc1;
            }
            else
            {
               strCatalogNo   = mgr.readValue("CATALOG_NO","");
               strCatalogDesc = mgr.readValue("SALESPARTCATALOGDESC","");
            }

         }

         strRoleCode = trans.getValue("PARA1/DATA/ROLE_CODE");       
         if (mgr.isEmpty(strRoleCode))
            strRoleCode = sDefRole;
         strDesc = trans.getValue("PARA2/DATA/CMNT");
         strOrgCode = trans.getValue("PARA3/DATA/DEPART");
         if (mgr.isEmpty(strOrgCode))
            strOrgCode = sDefOrg;
         strSig = trans.getValue("PARA4/DATA/EMP_SIGNATURE");
         strName = trans.getValue("PARA5/DATA/NAME");
         strConcatDesc = (mgr.isEmpty(strName)? "" : (strName + ",")) + strOrgCode +(mgr.isEmpty(strDesc)? "" : (", ("+ strDesc +")")) ; 

         txt = (mgr.isEmpty(emp_id)? "" : emp_id) +"^"+
               (mgr.isEmpty(strRoleCode)? "" : strRoleCode) +"^"+ 
               (mgr.isEmpty(strSig)? "" : strSig) +"^"+ 
               (mgr.isEmpty(strName)? "" : strName) +"^"+ 
               (mgr.isEmpty(strOrgCode)? "" : strOrgCode) +"^"+ 
               (mgr.isEmpty(site)? "" : site) +"^"+ 
               (mgr.isEmpty(strConcatDesc)? "" : strConcatDesc) +"^"+ 
               (mgr.isEmpty(strCatalogNo)? "" : strCatalogNo) +"^"+ 
               (mgr.isEmpty(strCatalogDesc)? "" : strCatalogDesc) +"^" ;

         mgr.responseWrite(txt);
      }
      else if ("QTY".equals(val))
      {

         txt=null;
         double colAmount = 0;
         double nBuyQtyDue=0;
         double colSalesPartCost=0;
         double colAmountSales=0;
         double colListPrice=0;

         double colDiscount = mgr.readNumberValue("DISCOUNT");
         if (isNaN(colDiscount))
            colDiscount = 0;

         double nSalesPriceAmount=0;
         String strColAmount = null;
         String strColAmountSales =null;
         String strColListPrice = null;
         String strColSalesPartCost = null;
         String strDisplayDiscount = null;
         String colPriceListNo;
         boolean securityOk = false;
         boolean salesSecOk = false;
         boolean roleSecOk = false;
         boolean isError = false;
         double colDiscountPrice = 0;

         secBuff = mgr.newASPTransactionBuffer();
         secBuff.addSecurityQuery("SALES_PART");
         secBuff.addSecurityQuery("ROLE_SALES_PART");
         secBuff.addSecurityQuery("Company_Finance_API","Get_Currency_Code");
         secBuff.addSecurityQuery("Customer_Order_Pricing_API","Get_Valid_Price_List,Get_Sales_Part_Price_Info");

         secBuff = mgr.perform(secBuff);

         if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
            salesSecOk = true;

         if (secBuff.getSecurityInfo().itemExists("ROLE_SALES_PART"))
            roleSecOk = true;

         if (secBuff.getSecurityInfo().itemExists("SALES_PART") && secBuff.getSecurityInfo().itemExists("Company_Finance_API.Get_Currency_Code") && secBuff.getSecurityInfo().itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List"))
            securityOk = true;

         if (salesSecOk)
         {
            cmd = trans.addCustomFunction("CST", "Work_Order_Planning_Util_Api.Get_Cost", "COST3" );
            cmd.addParameter("ITEM1_ORG_CODE");
            cmd.addParameter("ROLE_CODE");
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
            cmd.addParameter("ROLE_CODE");
            cmd.addParameter("DUMMY","");
            cmd.addParameter("DUMMY","");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("EMP_NO",mgr.readValue("EMP_NO"));
            cmd.addParameter("DUMMY","TRUE");
         }

         if (!mgr.isEmpty(mgr.readValue("QTY")))
         {
            if (mgr.readNumberValue("QTY") > 24)
            {
               txt = "No_Data_Found" + mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMERRMSGMORE:  You cannot register more than 24 hours per day.");
               isError = true;
            }

            else if (roleSecOk &&  ! mgr.isEmpty(mgr.readValue("CATALOG_NO")))
            {
               colSalesPartCost = mgr.readNumberValue("SALESPARTCOST");
               double colQty = mgr.readNumberValue("QTY");

               colAmount = colSalesPartCost * colQty;
            }
            else
            {
               cmd = trans.addCustomCommand("CALAMOUNT","Work_Order_Coding_API.Calc_Amount");
               cmd.addParameter("AMOUNT");
               cmd.addParameter("QTY");
               cmd.addParameter("ITEM1_ORG_CODE");
               cmd.addParameter("ROLE_CODE");
               cmd.addParameter("ITEM1_CONTRACT");
               cmd.addParameter("EMP_NO",mgr.readValue("EMP_NO"));
               cmd.addParameter("DUMMY","TRUE");
            }
         }
         if (securityOk)
         {
            nBuyQtyDue = mgr.readNumberValue("QTY");

            if (isNaN(nBuyQtyDue)  ||   toDouble(nBuyQtyDue) == 0)
               nBuyQtyDue = 1;

            cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("BASE_PRICE","0");
            cmd.addParameter("SALE_PRICE","0");
            cmd.addParameter("DISCOUNT","0");
            cmd.addParameter("CURRENCY_RATE","0");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("CATALOG_NO");
            cmd.addParameter("CUSTOMER_NO");
            cmd.addParameter("AGREEMENT_ID");
            cmd.addParameter("PRICE_LIST_NO");
            cmd.addParameter("QTY1",mgr.formatNumber("QTY1",nBuyQtyDue));
         }

         trans = mgr.validate(trans);

         colSalesPartCost = 0;

         colSalesPartCost = trans.getBuffer("CST/DATA").getNumberValue("COST3");

         if (isNaN(colSalesPartCost))
            colSalesPartCost = 0;

         if (! mgr.isEmpty(mgr.readValue("QTY"))  &&   mgr.readNumberValue("QTY") <= 24  &&  ( !roleSecOk ||   mgr.isEmpty(mgr.readValue("CATALOG_NO")) ))
            colAmount = trans.getBuffer("CALAMOUNT/DATA").getNumberValue("AMOUNT");

         if (securityOk)
         {
            colListPrice = mgr.readNumberValue("LIST_PRICE");
            if (isNaN(colListPrice))
            {
               colListPrice = trans.getNumberValue("PRICEINFO/DATA/SALE_PRICE"); 
               if (isNaN(colListPrice))
                  colListPrice = 0;
            }

            colDiscount     = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");
            if (isNaN(colDiscount))
               colDiscount = 0;

            double qty = mgr.readNumberValue("QTY");
            if (isNaN(qty))
               qty = 0;

            colDiscountPrice = colListPrice - (colDiscount/100 * colListPrice);  
            colAmountSales = colDiscountPrice * qty;

         }
         else
         {
            colListPrice = mgr.readNumberValue("LIST_PRICE");
            colAmountSales = mgr.readNumberValue("AMOUNTSALES");
            colPriceListNo = mgr.readValue("PRICE_LIST_NO","");
         }

         if (!isError)
         {
            if (!isNaN(colListPrice))
               strColListPrice   = mgr.getASPField("LIST_PRICE").formatNumber(colListPrice);
            if (!isNaN(colAmountSales))
               strColAmountSales = mgr.getASPField("AMOUNTSALES").formatNumber(colAmountSales);
            if (!isNaN(colSalesPartCost))
               strColSalesPartCost = mgr.getASPField("SALESPARTCOST").formatNumber(colSalesPartCost);
            if (!isNaN(colAmount))
               strColAmount = mgr.getASPField("AMOUNT").formatNumber(colAmount);
            if (!isNaN(colDiscount))
               strDisplayDiscount = mgr.getASPField("DISCOUNT").formatNumber(colDiscount);

            String strColDiscountPrice = mgr.formatNumber("ITEM1DISCOUNTEDPRICE",colDiscountPrice);

            txt = (mgr.isEmpty(strColListPrice)?"":strColListPrice) +"^"+ 
                  (mgr.isEmpty(strColAmountSales)?"":strColAmountSales) +"^"+ 
                  (mgr.isEmpty(strColSalesPartCost)?"":strColSalesPartCost) +"^"+ 
                  (mgr.isEmpty(strColAmount)?"":strColAmount) +"^"+ 
                  (mgr.isEmpty(strDisplayDiscount)?"":strDisplayDiscount) +"^"+
                  (mgr.isEmpty(strColDiscountPrice)?"":strColDiscountPrice) +"^";
         }

         mgr.responseWrite(txt);
      }
      else if ("ROLE_CODE".equals(val))
      {
         double colAmount=0;
         double colSalesPartCost=0;
         double colQty=0;
         double nBuyQtyDue=0;
         double colListPrice=0;

         double colDiscount = mgr.readNumberValue("DISCOUNT");
         if (isNaN(colDiscount))
            colDiscount = 0;

         double colAmountSales=0;
         double nSalesPriceAmount=0;
         String strColAmount = null;
         String strColAmountSales = null;
         String strColListPrice = null;
         String strDisplayDiscount = null;
         String colPriceListNo;
         String colCmnt;
         String colCatalogNo;
         String colCatalogDesc;
         String colCatalogNo1;
         String colCatalogDesc1;
         boolean securityOk = false;
         boolean salesSecOk = false;
         double colDiscountPrice = 0;
         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[6];

         String sRoleCode = "";
         String sOrgContract = "";
         String new_role_code = mgr.readValue("ROLE_CODE","");

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
            sRoleCode = mgr.readValue ("ROLE_CODE");;
            sOrgContract = mgr.readValue ("ITEM1_CONTRACT");
         }

         secBuff = mgr.newASPTransactionBuffer();
         secBuff.addSecurityQuery("SALES_PART");
         secBuff.addSecurityQuery("Company_Finance_API","Get_Currency_Code");
         secBuff.addSecurityQuery("Customer_Order_Pricing_API","Get_Valid_Price_List,Get_Sales_Part_Price_Info");

         secBuff = mgr.perform(secBuff);

         if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
            salesSecOk = true;

         if (secBuff.getSecurityInfo().itemExists("SALES_PART") && secBuff.getSecurityInfo().itemExists("Company_Finance_API.Get_Currency_Code") && secBuff.getSecurityInfo().itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List"))
            securityOk = true;

         if (securityOk)
         {
            nBuyQtyDue = mgr.readNumberValue("QTY_TO_INVOICE");
            if (isNaN(nBuyQtyDue))
               nBuyQtyDue = 0;

            if (isNaN(nBuyQtyDue) || (nBuyQtyDue == 0))
               nBuyQtyDue = 1;

            cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("BASE_PRICE","0");
            cmd.addParameter("SALE_PRICE","0");
            cmd.addParameter("DISCOUNT","0");
            cmd.addParameter("CURRENCY_RATE","0");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("CATALOG_NO");
            cmd.addParameter("CUSTOMER_NO");
            cmd.addParameter("AGREEMENT_ID");
            cmd.addParameter("PRICE_LIST_NO");
            cmd.addParameter("QTY1",mgr.formatNumber("QTY1",nBuyQtyDue));
         }

         if (!mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE")) && !mgr.isEmpty(mgr.readValue("QTY")) && !mgr.isEmpty(mgr.readValue("AMOUNT")) && !mgr.isEmpty(mgr.readValue("ROLE_CODE")))
         {
            cmd = trans.addCustomCommand("CALAMOUNT","Work_Order_Coding_API.Calc_Amount");
            cmd.addParameter("AMOUNT");
            cmd.addParameter("QTY");
            cmd.addParameter("ITEM1_ORG_CODE");
            cmd.addParameter("ROLE_CODE",sRoleCode);
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("EMP_NO",mgr.readValue("EMP_NO"));
            cmd.addParameter("DUMMY","TRUE");
         }

         colAmount = mgr.readNumberValue("AMOUNT");

         if (!mgr.isEmpty(mgr.readValue("ROLE_CODE")))
         {
            cmd = trans.addCustomFunction("ROLEDESC","Role_API.Get_Description","CMNT");
            cmd.addParameter("ROLE_CODE",sRoleCode);

            if (salesSecOk)
            {
               cmd = trans.addCustomFunction("DEFCATNO","Role_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
               cmd.addParameter("ROLE_CODE",sRoleCode);
               cmd.addParameter("ITEM1_CONTRACT");

               cmd = trans.addCustomFunction("CATNOEXIST","Sales_Part_API.Check_Exist","CATALOG_EXIST");
               cmd.addParameter("CONTRACT");
               cmd.addReference("CATALOG_NO","DEFCATNO/DATA");

               cmd = trans.addCustomFunction("DEFCATDESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
               cmd.addParameter("ITEM1_CONTRACT");
               cmd.addReference("CATALOG_NO","DEFCATNO/DATA");
            }
            else
            {
               if (!mgr.isEmpty(mgr.readValue("CATALOG_NO")))
               {
                  if (mgr.isEmpty(mgr.readValue("QTY")))
                     colAmount = 0;
                  else
                  {
                     colSalesPartCost = mgr.readNumberValue("SALESPARTCOST");
                     colQty = mgr.readNumberValue("QTY");

                     colAmount = colSalesPartCost * colQty;
                  }
               }
            }        
         }
         else
         {
            if (salesSecOk)
            {
               cmd = trans.addCustomFunction("ORGDEFCATNO","Organization_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
               cmd.addParameter("ITEM1_CONTRACT");
               cmd.addParameter("ITEM1_ORG_CODE");

               cmd = trans.addCustomFunction("CATNOEXIST","Sales_Part_API.Check_Exist","CATALOG_EXIST");
               cmd.addParameter("CONTRACT");
               cmd.addReference("CATALOG_NO","ORGDEFCATNO/DATA");
            }


         }

         trans = mgr.validate(trans);

         if (securityOk)
         {
            colListPrice = trans.getNumberValue("PRICEINFO/DATA/SALE_PRICE"); 
            if (isNaN(colListPrice))
               colListPrice = 0;

            colDiscount     = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");
            if (isNaN(colDiscount))
               colDiscount = 0;

            double qtyInv = mgr.readNumberValue("QTY_TO_INVOICE");
            if (isNaN(qtyInv))
               qtyInv = 0;

            colDiscountPrice =  colListPrice - (colDiscount/100*colListPrice);
            colAmountSales =  colDiscountPrice * qtyInv;

         }
         else
         {
            colListPrice = mgr.readNumberValue("LIST_PRICE");
            colAmountSales = mgr.readNumberValue("AMOUNTSALES");
            colPriceListNo = mgr.readValue("PRICE_LIST_NO","");
         }

         if (!mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE")) && !mgr.isEmpty(mgr.readValue("QTY")) && !mgr.isEmpty(mgr.readValue("AMOUNT")) && !mgr.isEmpty(mgr.readValue("ROLE_CODE")))
            colAmount = trans.getBuffer("CALAMOUNT/DATA").getNumberValue("AMOUNT");

         if (!mgr.isEmpty(mgr.readValue("ROLE_CODE")))
         {
            colCmnt = (mgr.isEmpty(mgr.readValue("NAME",""))? "" :(mgr.readValue("NAME","")+ ","))+ mgr.readValue("ITEM1_ORG_CODE","")+",("+ trans.getValue("ROLEDESC/DATA/CMNT")+")"; 

            if (salesSecOk)
            {
               colCatalogNo1   = trans.getValue("DEFCATNO/DATA/CATALOG_NO");
               colCatalogDesc1 = trans.getValue("DEFCATDESC/DATA/SALESPARTCATALOGDESC");
               if (!mgr.isEmpty(colCatalogNo1))
               {
                  colCatalogNo   = colCatalogNo1;
                  colCatalogDesc = colCatalogDesc1;
               }
               else
               {
                  colCatalogNo   = mgr.readValue("CATALOG_NO","");
                  colCatalogDesc = mgr.readValue("SALESPARTCATALOGDESC","");
               }
            }
            else
            {
               colCatalogNo   = mgr.readValue("CATALOG_NO","");
               colCatalogDesc = mgr.readValue("SALESPARTCATALOGDESC","");
            }
         }
         else
         {
            colCatalogNo1   = trans.getValue("ORGDEFCATNO/DATA/CATALOG_NO");
            colCatalogDesc1 = trans.getValue("DEFCATDESC/DATA/SALESPARTCATALOGDESC");
            colCatalogNo   = colCatalogNo1;
            colCatalogDesc = colCatalogDesc1;
            colCmnt        = (mgr.isEmpty(mgr.readValue("NAME",""))? "" :(mgr.readValue("NAME","")+ ","))+ mgr.readValue("ITEM1_ORG_CODE","");
         }


         double cat_Exist = trans.getBuffer("CATNOEXIST/DATA").getNumberValue("CATALOG_EXIST");

         if (cat_Exist == 0)
         {
            colCatalogNo = mgr.readValue("CATALOG_NO","");
            colCatalogDesc = mgr.readValue("SALESPARTCATALOGDESC","");
         }


         if (!isNaN(colListPrice))
            strColListPrice = mgr.getASPField("LIST_PRICE").formatNumber(colListPrice);
         if (!isNaN(colAmount))
            strColAmount = mgr.getASPField("AMOUNT").formatNumber(colAmount);
         if (!isNaN(colAmountSales))
            strColAmountSales = mgr.getASPField("AMOUNTSALES").formatNumber(colAmountSales);
         if (!isNaN(colDiscount))
            strDisplayDiscount = mgr.getASPField("DISCOUNT").formatNumber(colDiscount);

         String strColDiscountPrice = mgr.formatNumber("ITEM1DISCOUNTEDPRICE",colDiscountPrice);

         txt = (mgr.isEmpty(sRoleCode)?"":sRoleCode) +"^"+ 
               (mgr.isEmpty(sOrgContract)?"":sOrgContract) +"^"+
               (mgr.isEmpty(strColListPrice)?"":strColListPrice) +"^"+ 
               (mgr.isEmpty(strColAmount)?"":strColAmount) +"^"+ 
               (mgr.isEmpty(strColAmountSales)?"":strColAmountSales) +"^"+ 
               (mgr.isEmpty(colCmnt)?"":colCmnt) +"^"+ 
               (mgr.isEmpty(colCatalogNo)?"":colCatalogNo) +"^"+ 
               (mgr.isEmpty(colCatalogDesc)?"":colCatalogDesc) +"^"+ 
               (mgr.isEmpty(strDisplayDiscount)?"":strDisplayDiscount) +"^"+
               (mgr.isEmpty(strColDiscountPrice)?"":strColDiscountPrice) +"^";

         mgr.responseWrite(txt);
      }
      else if ("TEAM_ID".equals(val))
      {
         cmd = trans.addCustomFunction("TDESC", "Maint_Team_API.Get_Description", "TEAMDESC" );    
         cmd.addParameter("TEAM_ID");
         cmd.addParameter("TEAM_CONTRACT");
         trans = mgr.validate(trans);   
         String teamDesc  = trans.getValue("TDESC/DATA/TEAMDESC");

         txt =  (mgr.isEmpty(teamDesc) ? "" : (teamDesc)) + "^";
         mgr.responseWrite(txt);

      }

      else if ("PLAN_LINE_NO".equals(val))
      {

         cmd = trans.addCustomFunction("PARA20", "Work_Order_Role_API.Get_Plan_Team_Contract", "TEAM_CONTRACT"); 
         cmd.addParameter("WO_NO");
         cmd.addParameter("PLAN_LINE_NO");

         cmd = trans.addCustomFunction("PARA21", "Work_Order_Role_API.Get_Plan_Team_Id", "TEAM_ID"); 
         cmd.addParameter("WO_NO");
         cmd.addParameter("PLAN_LINE_NO");

         trans = mgr.validate(trans);

         String strTeamCon = trans.getValue("PARA20/DATA/TEAM_CONTRACT");
         String strTeamId = trans.getValue("PARA21/DATA/TEAM_ID");

         txt = (mgr.isEmpty(strTeamCon)? "" : strTeamCon) +"^"+ (mgr.isEmpty(strTeamId)? "" : strTeamId) +"^";

         mgr.responseWrite(txt);
      }
      else if ("CATALOG_NO".equals(val))
      {
         double nBuyQtyDue=0;
         double colListPrice=0;

         double colDiscount = mgr.readNumberValue("DISCOUNT");
         if (isNaN(colDiscount))
            colDiscount = 0;

         double colAmountSales=0;
         double nSalesPriceAmount=0;
         double colSalesPartCost = 0;
         double colAmount=0;
         double numSalesPartCost = 0;
         String strColAmountSales = null;
         String strColListPrice = null;
         String strColAmount = null;
         String colPriceListNo = null;
         String strColSalesPartCost = null;
         String strDisplayDiscount = null;
         String colSalesPartDesc;
         String colCmnt;
         boolean securityOk = false;
         boolean salesSecOk = false;
         boolean priceListOk = false;
         double colDiscountPrice = 0;

         secBuff = mgr.newASPTransactionBuffer();
         secBuff.addSecurityQuery("SALES_PART");
         secBuff.addSecurityQuery("Company_Finance_API","Get_Currency_Code");
         secBuff.addSecurityQuery("Customer_Order_Pricing_API","Get_Valid_Price_List,Get_Sales_Part_Price_Info");

         secBuff = mgr.perform(secBuff);

         if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
            salesSecOk = true;

         if (secBuff.getSecurityInfo().itemExists("SALES_PART") && secBuff.getSecurityInfo().itemExists("Company_Finance_API.Get_Currency_Code") && secBuff.getSecurityInfo().itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List"))
            securityOk = true;

         if (secBuff.getSecurityInfo().itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List"))
            priceListOk = true;

         if (priceListOk)
         {
            cmd = trans.addCustomFunction("GETPRCLIST","Customer_Order_Pricing_API.Get_Valid_Price_List","PRICE_LIST_NO");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("CATALOG_NO");
            cmd.addParameter("CUSTOMER_NO");
            cmd.addParameter("CURRENCY_CODE");
         }

         if (securityOk)
         {
            nBuyQtyDue = mgr.readNumberValue("QTY_TO_INVOICE");
            if (isNaN(nBuyQtyDue))
               nBuyQtyDue = 0;

            if (isNaN(nBuyQtyDue) || (nBuyQtyDue == 0))
               nBuyQtyDue = 1;

            cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("BASE_PRICE","0");
            cmd.addParameter("SALE_PRICE","0");
            cmd.addParameter("DISCOUNT","0");
            cmd.addParameter("CURRENCY_RATE","0");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("CATALOG_NO");
            cmd.addParameter("CUSTOMER_NO");
            cmd.addParameter("AGREEMENT_ID");
            cmd.addReference("PRICE_LIST_NO","GETPRCLIST/DATA");
            cmd.addParameter("QTY1",mgr.formatNumber("QTY1",nBuyQtyDue));
         }

         cmd = trans.addCustomFunction("ROLEDESC","Role_API.Get_Description","CMNT");
         cmd.addParameter("ROLE_CODE");

         if (salesSecOk)
         {
            cmd = trans.addCustomFunction("DEFCATDESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("CATALOG_NO");

            cmd = trans.addCustomFunction("CST", "Work_Order_Planning_Util_Api.Get_Cost", "COST3" );
            cmd.addParameter("ITEM1_ORG_CODE");
            cmd.addParameter("ROLE_CODE");
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
            cmd.addParameter("ROLE_CODE");
            cmd.addParameter("DUMMY","");
            cmd.addParameter("DUMMY","");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("EMP_NO",mgr.readValue("EMP_NO"));
            cmd.addParameter("DUMMY","TRUE");
         }

         trans = mgr.validate(trans);

         if (securityOk)
         {
            colListPrice    = trans.getNumberValue("PRICEINFO/DATA/SALE_PRICE"); 
            if (isNaN(colListPrice))
               colListPrice = 0;

            colDiscount     = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");
            if (isNaN(colDiscount))
               colDiscount = 0;

            colAmountSales = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE") * trans.getNumberValue("PRICEINFO/DATA/QTY1");

            double qtyInv = mgr.readNumberValue("QTY_TO_INVOICE");
            if (isNaN(qtyInv))
               qtyInv = 0;

            colDiscountPrice = colListPrice - (colDiscount/100 * colListPrice);         
            colAmountSales = colDiscountPrice * qtyInv;

         }
         else
         {
            colListPrice = mgr.readNumberValue("LIST_PRICE");
            colAmountSales = mgr.readNumberValue("AMOUNTSALES");
         }

         if (priceListOk)
            colPriceListNo = trans.getValue("GETPRCLIST/DATA/PRICE_LIST_NO");

         if (salesSecOk)
            colSalesPartDesc = trans.getValue("DEFCATDESC/DATA/SALESPARTCATALOGDESC");
         else
            colSalesPartDesc = mgr.readValue("SALESPARTCATALOGDESC","");

         String sDescr = trans.getValue("ROLEDESC/DATA/CMNT");
         colCmnt = (mgr.isEmpty(mgr.readValue("NAME",""))? "" :(mgr.readValue("NAME","")+ ","))+ mgr.readValue("ITEM1_ORG_CODE","") + (mgr.isEmpty(sDescr) ? "" : (",("+ sDescr +")"))+ (mgr.isEmpty(colSalesPartDesc) ? "" :(", "+colSalesPartDesc));

         colSalesPartCost = trans.getBuffer("CST/DATA").getNumberValue("COST3");
         numSalesPartCost = trans.getNumberValue("CST/DATA/COST3");

         if (isNaN(numSalesPartCost))
            numSalesPartCost = 0;

         if (mgr.isEmpty(mgr.readValue("QTY")))
            colAmount = 0;
         else
            colAmount = numSalesPartCost * mgr.readNumberValue("QTY");    

         String agree_id = mgr.readValue("AGREEMENT_ID","");

         int colcAgreement;

         if (mgr.isEmpty(agree_id))
            colcAgreement = 0;
         else
            colcAgreement = 1;

         if (!isNaN(colListPrice))
            strColListPrice = mgr.getASPField("LIST_PRICE").formatNumber(colListPrice);
         if (!isNaN(colAmount))
            strColAmount = mgr.getASPField("AMOUNT").formatNumber(colAmount);
         if (!isNaN(colAmountSales))
            strColAmountSales = mgr.getASPField("AMOUNTSALES").formatNumber(colAmountSales);
         if (!isNaN(colSalesPartCost))
            strColSalesPartCost = mgr.getASPField("SALESPARTCOST").formatNumber(colSalesPartCost);

         String strColcAgreement = mgr.getASPField("AGREEMENT_PRICE_FLAG").formatNumber(colcAgreement);
         strDisplayDiscount = mgr.formatNumber("DISCOUNT",colDiscount);

         String strColDiscountPrice = mgr.formatNumber("ITEM1DISCOUNTEDPRICE",colDiscountPrice);

         txt = (mgr.isEmpty(strColListPrice)?"":strColListPrice) +"^"+ 
               (mgr.isEmpty(strColAmount)?"":strColAmount) +"^"+ 
               (mgr.isEmpty(strColAmountSales)?"":strColAmountSales) +"^"+ 
               (mgr.isEmpty(colCmnt)?"":colCmnt) +"^"+ 
               (mgr.isEmpty(colSalesPartDesc)?"":colSalesPartDesc) +"^"+ 
               (mgr.isEmpty(strColSalesPartCost)?"":strColSalesPartCost) +"^"+ 
               (mgr.isEmpty(strDisplayDiscount)?"":strDisplayDiscount) +"^"+
               (mgr.isEmpty(strColcAgreement)?"":strColcAgreement) + "^"+
               (mgr.isEmpty(strColDiscountPrice)?"":strColDiscountPrice) + "^"+
               (mgr.isEmpty(colPriceListNo)?"":colPriceListNo) + "^";

         mgr.responseWrite(txt);
      }
      else if ("ITEM1_ORG_CODE".equals(val))
      {
         double colAmount = 0;
         String strColAmount = null;
         String colOrgCode;
         String colCmnt;
         String colSalesPart;
         String colLineDescription;
         boolean securityOk = false;
         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[6];

         String sOrgCode = "";
         String sOrgContract = "";
         String new_org_code = mgr.readValue("ITEM1_ORG_CODE","");
         String colRoleCode = mgr.readValue("ROLE_CODE","");
         String colCatalogNo  = mgr.readValue("CATALOG_NO","");
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
               if (!mgr.isEmpty(mgr.readValue("EMP_NO")))
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
            sOrgCode = mgr.readValue ("ITEM1_ORG_CODE");
            sOrgContract= mgr.readValue ("ITEM1_CONTRACT");

         }

         secBuff = mgr.newASPTransactionBuffer();
         secBuff.addSecurityQuery("SALES_PART");

         secBuff = mgr.perform(secBuff);

         if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
            securityOk = true;

         if (securityOk && (mgr.isEmpty(colRoleCode) || (!mgr.isEmpty(colRoleCode) && mgr.isEmpty(colCatalogNo))))
         {
            cmd = trans.addCustomFunction("DEFCAT","Organization_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("ITEM1_ORG_CODE",sOrgCode);

            cmd = trans.addCustomFunction("CATNOEXIST","Sales_Part_API.Check_Exist","CATALOG_EXIST");
            cmd.addParameter("CONTRACT");
            cmd.addReference("CATALOG_NO","DEFCAT/DATA");

            cmd = trans.addCustomFunction("DEFCATDESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addReference("CATALOG_NO","DEFCAT/DATA");
         }
         else
         {
            if (!mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE")) && !mgr.isEmpty(mgr.readValue("QTY")) && !mgr.isEmpty(mgr.readValue("AMOUNT")) && !mgr.isEmpty(mgr.readValue("ROLE_CODE")))
            {
               cmd = trans.addCustomCommand("CALAMOUNT","Work_Order_Coding_API.Calc_Amount");
               cmd.addParameter("AMOUNT");
               cmd.addParameter("QTY");
               cmd.addParameter("ITEM1_ORG_CODE",sOrgCode);
               cmd.addParameter("ROLE_CODE");
               cmd.addParameter("ITEM1_CONTRACT");
               cmd.addParameter("EMP_NO",mgr.readValue("EMP_NO"));
               cmd.addParameter("DUMMY","TRUE");
            }
            if (!mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE")))
            {
               cmd = trans.addCustomFunction("ROLEDESC","Role_API.Get_Description","CMNT");
               cmd.addParameter("ROLE_CODE");
            }
            else
            {
               cmd = trans.addCustomFunction("GETORGCODE","Active_Work_Order_API.Get_Org_Code","ORCODE");
               cmd.addParameter("ITEM1_WO_NO");
            }
         }

         trans = mgr.validate(trans);


         if (securityOk && (mgr.isEmpty(colRoleCode) || (!mgr.isEmpty(colRoleCode) && mgr.isEmpty(colCatalogNo))))
         {
            double cat_Exist = trans.getBuffer("CATNOEXIST/DATA").getNumberValue("CATALOG_EXIST");

            colSalesPart       = trans.getValue("DEFCAT/DATA/CATALOG_NO");
            colLineDescription = trans.getValue("DEFCATDESC/DATA/SALESPARTCATALOGDESC");
            colOrgCode         = mgr.readValue("ITEM1_ORG_CODE","");
            colCmnt            = mgr.readValue("CMNT","");
            colAmount       = mgr.readNumberValue("AMOUNT");
         }
         else
         {
            colSalesPart       = mgr.readValue("CATALOG_NO","");
            colLineDescription = mgr.readValue("SALESPARTCATALOGDESC","");

            if (!mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE")) && !mgr.isEmpty(mgr.readValue("QTY")) && !mgr.isEmpty(mgr.readValue("AMOUNT")) && !mgr.isEmpty(mgr.readValue("ROLE_CODE")))
               colAmount = trans.getBuffer("CALAMOUNT/DATA").getNumberValue("AMOUNT");

            else if (!mgr.isEmpty(mgr.readValue("QTY")))
               colAmount  = mgr.readNumberValue("AMOUNT");

            else if (mgr.isEmpty(mgr.readValue("QTY")))
               colAmount = 0;

            if (!mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE")))
            {
               colOrgCode = mgr.readValue("ITEM1_ORG_CODE","");
               //Bug id 86298, Start
               //colCmnt    = (mgr.isEmpty(mgr.readValue("NAME",""))? "" :(mgr.readValue("NAME","")+ ","))+ mgr.readValue("ITEM1_ORG_CODE","")+",("+ trans.getValue("ROLEDESC/DATA/CMNT")+")";
               colCmnt    = (mgr.isEmpty(mgr.readValue("NAME",""))? "" :(mgr.readValue("NAME","")+ ","))+sOrgCode+",("+ trans.getValue("ROLEDESC/DATA/CMNT")+")";
               //Bug id 86298, End
            }
            else
            {
               colOrgCode = trans.getValue("GETORGCODE/DATA/ORCODE");                
               colCmnt    = (mgr.isEmpty(mgr.readValue("NAME",""))? "" :(mgr.readValue("NAME","")+ ",")) + colOrgCode;
            }    
         }

         if (!isNaN(colAmount))
            strColAmount = mgr.getASPField("AMOUNT").formatNumber(colAmount);

         txt = (mgr.isEmpty(sOrgCode)?"":sOrgCode) +"^"+
               (mgr.isEmpty(sOrgContract)?"":sOrgContract) +"^"+
               (mgr.isEmpty(strColAmount)?"":strColAmount) +"^"+ 
               (mgr.isEmpty(colCmnt)?"":colCmnt) +"^"+ 
               (mgr.isEmpty(colSalesPart)?"":colSalesPart) +"^"+ 
               (mgr.isEmpty(colLineDescription)?"":colLineDescription) +"^" ;
         mgr.responseWrite(txt);
      }
      else if ("ITEM1_CONTRACT".equals(val))
      {
         cmd = trans.addCustomFunction("COMP","Site_API.Get_Company","COMPANY_VAR");
         cmd.addParameter("ITEM1_CONTRACT");

         trans = mgr.validate(trans);

         String strCompany = trans.getValue("COMP/DATA/COMPANY_VAR");

         txt = (mgr.isEmpty(strCompany) ? "" : (strCompany))+ "^";

         mgr.responseWrite(txt);
      }
      else if ("PRICE_LIST_NO".equals(val))
      {
         double nBuyQtyDue = 0;
         double colListPrice = 0;

         double colDiscount = mgr.readNumberValue("DISCOUNT");
         if (isNaN(colDiscount))
            colDiscount = 0;

         double colAmountSales = 0;
         double nSalesPriceAmount = 0;
         double listPrice;
         double qty;
         String strColAmountSales = null;
         String strColListPrice = null;
         String colPriceListNo;
         int colcAgreement;
         String strDisplayDiscount = null;
         boolean securityOk = false;
         boolean salesSecOk = false;
         double colDiscountPrice = 0;

         secBuff = mgr.newASPTransactionBuffer();
         secBuff.addSecurityQuery("SALES_PART");
         secBuff.addSecurityQuery("Company_Finance_API","Get_Currency_Code");
         secBuff.addSecurityQuery("Customer_Order_Pricing_API","Get_Valid_Price_List,Get_Sales_Part_Price_Info");

         secBuff = mgr.perform(secBuff);

         if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
            salesSecOk = true;

         if (secBuff.getSecurityInfo().itemExists("SALES_PART") && secBuff.getSecurityInfo().itemExists("Company_Finance_API.Get_Currency_Code") && secBuff.getSecurityInfo().itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List"))
            securityOk = true;

         if (securityOk)
         {
            nBuyQtyDue = mgr.readNumberValue("QTY_TO_INVOICE");
            if (isNaN(nBuyQtyDue))
               nBuyQtyDue = 0;

            if (isNaN(nBuyQtyDue) || (nBuyQtyDue == 0))
               nBuyQtyDue = 1;

            cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("BASE_PRICE","0");
            cmd.addParameter("SALE_PRICE","0");
            cmd.addParameter("DISCOUNT","0");
            cmd.addParameter("CURRENCY_RATE","0");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("CATALOG_NO");
            cmd.addParameter("CUSTOMER_NO");
            cmd.addParameter("AGREEMENT_ID");
            cmd.addParameter("PRICE_LIST_NO");
            cmd.addParameter("QTY1",mgr.formatNumber("QTY1",nBuyQtyDue));
         }

         trans = mgr.validate(trans);

         if (securityOk)
         {
            colListPrice    = trans.getNumberValue("PRICEINFO/DATA/SALE_PRICE"); 
            if (isNaN(colListPrice))
               colListPrice = 0;

            colDiscount     = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");
            if (isNaN(colDiscount))
               colDiscount = 0;

            double qtyInv = mgr.readNumberValue("QTY_TO_INVOICE");
            if (isNaN(qtyInv))
               qtyInv = 0;

            colDiscountPrice = colListPrice -(colDiscount/100*colListPrice);
            colAmountSales = colDiscountPrice * qtyInv;
         }
         else
         {
            colListPrice = mgr.readNumberValue("LIST_PRICE");
            if (isNaN(colListPrice))
               colListPrice = 0;

            colAmountSales = mgr.readNumberValue("AMOUNTSALES");
            if (isNaN(colAmountSales))
               colAmountSales = 0;
         }

         String agree_id = mgr.readValue("AGREEMENT_ID","");

         if (mgr.isEmpty(agree_id))
            colcAgreement = 0;
         else
            colcAgreement = 1;

         listPrice = mgr.readNumberValue("LIST_PRICE");
         qty = mgr.readNumberValue("QTY");

         if (!isNaN(colListPrice))
            strColListPrice = mgr.getASPField("LIST_PRICE").formatNumber(colListPrice);
         if (!isNaN(colAmountSales))
            strColAmountSales = mgr.getASPField("AMOUNTSALES").formatNumber(colAmountSales);

         String strColcAgreement = mgr.formatNumber("AGREEMENT_PRICE_FLAG",colcAgreement);
         strDisplayDiscount = mgr.formatNumber("DISCOUNT",colDiscount);
         String strColDiscountPrice = mgr.formatNumber("ITEM1DISCOUNTEDPRICE",colDiscountPrice);

         txt = (mgr.isEmpty(strColListPrice)?"":strColListPrice) +"^"+ 
               (mgr.isEmpty(strColAmountSales)?"":strColAmountSales) +"^"+ 
               strColcAgreement +"^"+
               (mgr.isEmpty(strDisplayDiscount)?"":strDisplayDiscount) +"^"+
               (mgr.isEmpty(strColDiscountPrice)?"":strColDiscountPrice) +"^";

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
         int colcAgreement = 0;
         String strColcAgreement = mgr.formatNumber("AGREEMENT_PRICE_FLAG",colcAgreement);
         txt =  colcAgreement +"^";
         mgr.responseWrite(txt);
      }
      else if ("DISCOUNT".equals(val))
      {
         int colcAgreement = 0;
         String strColcAgreement = mgr.formatNumber("AGREEMENT_PRICE_FLAG",colcAgreement);
         txt =  colcAgreement +"^";
         mgr.responseWrite(txt);
      }
      else if ("ALTERNATIVE_CUSTOMER".equals(val))
      {
         int colcAgreement = 0;
         String strColcAgreement = mgr.formatNumber("AGREEMENT_PRICE_FLAG",colcAgreement);
         txt =  colcAgreement +"^";
         mgr.responseWrite(txt);
      }
      else if ("BUDGET_COST".equals(val))
      {
         double budCost = mgr.readNumberValue("BUDGET_COST");
         double budRev = mgr.readNumberValue("BUDGET_REVENUE");

         double budMarg = budRev - budCost;

         txt = (isNaN(budMarg) ? "" :mgr.formatNumber("BUDGET_MARGIN",budMarg)) + "^";      
         mgr.responseWrite(txt);
      }
      else if ("BUDGET_REVENUE".equals(val))
      {
         double budCost = mgr.readNumberValue("BUDGET_COST");
         double budRev = mgr.readNumberValue("BUDGET_REVENUE");

         double budMarg = budRev - budCost;

         txt = (isNaN(budMarg) ? "" :mgr.formatNumber("BUDGET_MARGIN",budMarg)) + "^";      
         mgr.responseWrite(txt);
      }
      else if ("REQUIRED_START_DATE".equals(val))
      {
         ASPBuffer buff = mgr.newASPBuffer();
         buff.addFieldItem("REQUIRED_START_DATE",mgr.readValue("REQUIRED_START_DATE",""));

         trans.addCustomFunction("SYSDTETM","Maintenance_Site_Utility_API.Get_Site_Date","TEMP_DATE_TIME").
         addParameter("CONTRACT");

         trans = mgr.validate(trans);

         String sysdateTime = trans.getBuffer("SYSDTETM/DATA").getFieldValue("TEMP_DATE_TIME");

         Date startDate = buff.getFieldDateValue("REQUIRED_START_DATE");
         Date today = trans.getBuffer("SYSDTETM/DATA").getFieldDateValue("TEMP_DATE_TIME");

         boolean isBefore = startDate.before(today);

         mgr.responseWrite(mgr.formatDate("REQUIRED_START_DATE",startDate));
      }
      else if ("REQUIRED_END_DATE".equals(val))
      {
         ASPBuffer buff = mgr.newASPBuffer();   
         buff.addFieldItem("REQUIRED_END_DATE",mgr.readValue("REQUIRED_END_DATE",""));
         buff.addFieldItem("REQUIRED_START_DATE",mgr.readValue("REQUIRED_START_DATE",""));

         Date startDate = buff.getFieldDateValue("REQUIRED_START_DATE");
         Date endDate = buff.getFieldDateValue("REQUIRED_END_DATE");

         boolean isError = endDate.before(startDate);

         if (isError)
            mgr.responseWrite("ERROR_VAL");
         else
            mgr.responseWrite(mgr.formatDate("REQUIRED_END_DATE",endDate));
      }
      //-----------------------------------------------------------------------------
      //-------------Validations for Material block----------------------------------
      //-----------------------------------------------------------------------------


      else if ("ITEM3_WO_NO".equals(val))
      {
         cmd = trans.addCustomFunction("PLANSDATE","Active_Work_Order_API.Get_Plan_S_Date","DUE_DATE");
         cmd.addParameter("WO_NO",mgr.readValue("ITEM3_WO_NO",""));

         cmd = trans.addCustomFunction("PREACCID","Active_Work_Order_API.Get_Pre_Accounting_Id","NPREACCOUNTINGID");
         cmd.addParameter("WO_NO",mgr.readValue("ITEM3_WO_NO",""));

         cmd = trans.addCustomFunction("ITEM3CONTRA","WORK_ORDER_API.Get_Contract","ITEM3_CONTRACT");
         cmd.addParameter("WO_NO",mgr.readValue("ITEM3_WO_NO",""));

         cmd = trans.addCustomFunction("COMP","Site_API.Get_Company","ITEM3_COMPANY");
         cmd.addReference("CONTRACT","ITEM3CONTRA/DATA");

         cmd = trans.addCustomFunction("MCHCOD","WORK_ORDER_API.Get_Mch_Code","MCHCODE");
         cmd.addParameter("WO_NO",mgr.readValue("ITEM3_WO_NO",""));

         cmd = trans.addCustomFunction("ITEM3DESC","Maintenance_Object_API.Get_Mch_Name","ITEM3DESCRIPTION");
         cmd.addReference("CONTRACT","ITEM3CONTRA/DATA");
         cmd.addReference("MCHCODE","MCHCOD/DATA");

         trans = mgr.validate(trans);

         String dueDate = trans.getBuffer("PLANSDATE/DATA").getFieldValue("DUE_DATE");
         String nPreAccId = trans.getValue("PREACCID/DATA/NPREACCOUNTINGID");
         String item3Contract = trans.getBuffer("ITEM3CONTRA/DATA").getFieldValue("ITEM3_CONTRACT");
         String company = trans.getBuffer("COMP/DATA").getFieldValue("ITEM3_COMPANY");
         String mchCode = trans.getValue("MCHCOD/DATA/MCHCODE");
         String item3Desc = trans.getValue("ITEM3DESC/DATA/ITEM3DESCRIPTION");

         txt = (mgr.isEmpty(dueDate)?"":dueDate) +"^"+ 
               (mgr.isEmpty(nPreAccId)?"":nPreAccId) +"^"+ 
               (mgr.isEmpty(item3Contract)?"":item3Contract) +"^"+ 
               (mgr.isEmpty(company)?"":company) +"^"+ 
               (mgr.isEmpty(mchCode)?"":mchCode) +"^"+ 
               (mgr.isEmpty(item3Desc)?"":item3Desc) +"^";

         mgr.responseWrite(txt);
      }
      else if ("ITEM3_SIGNATURE".equals(val))
      {
         cmd = trans.addCustomFunction("SIGNID","Company_Emp_API.Get_Max_Employee_Id","ITEM3_SIGNATURE_ID");
         cmd.addParameter("COMPANY",mgr.readValue("ITEM3_COMPANY",""));
         cmd.addParameter("ITEM3_SIGNATURE");

         cmd = trans.addCustomFunction("SIGNNAME","EMPLOYEE_API.Get_Employee_Info","SIGNATURENAME");
         cmd.addParameter("COMPANY",mgr.readValue("ITEM3_COMPANY",""));
         cmd.addReference("ITEM3_SIGNATURE_ID","SIGNID/DATA");

         trans = mgr.validate(trans);

         String signId = trans.getBuffer("SIGNID/DATA").getFieldValue("ITEM3_SIGNATURE_ID");
         String signName = trans.getValue("SIGNNAME/DATA/SIGNATURENAME");

         txt = (mgr.isEmpty(signId)?"":signId) +"^"+ 
               (mgr.isEmpty(signName)?"":signName) +"^";

         mgr.responseWrite(txt);
      }
      else if ("INT_DESTINATION_ID".equals(val))
      {
         cmd = trans.addCustomFunction("INTDESTDESC","Internal_Destination_API.Get_Description","INT_DESTINATION_DESC");
         cmd.addParameter("CONTRACT",mgr.readValue("ITEM3_CONTRACT",""));
         cmd.addParameter("INT_DESTINATION_ID");

         trans = mgr.validate(trans);

         String intDestDesc = trans.getValue("INTDESTDESC/DATA/INT_DESTINATION_DESC");

         txt = (mgr.isEmpty(intDestDesc)?"":intDestDesc) +"^";

         mgr.responseWrite(txt);
      }
      else if ("PART_NO".equals(val))
      {
         String sDefCondiCode= "";
         String sDesc="";
         String activeInd = "";
         String vendorNo = "";
         String custOwner = "";
         String partOwnership = "";
         String sOwner = mgr.readValue("OWNER");
         String ownership = "";

         cmd = trans.addCustomFunction("GETCONDCODEUSAGEDB","PART_CATALOG_API.Get_Condition_Code_Usage_Db","COND_CODE_USAGE");
         cmd.addParameter("PART_NO");

         cmd = trans.addCustomFunction("DEFCONDCODE","CONDITION_CODE_API.Get_Default_Condition_Code","CONDITION_CODE");

         cmd = trans.addCustomFunction("CONCODE","CONDITION_CODE_API.Get_Description","CONDDESC");
         cmd.addReference("CONDITION_CODE","DEFCONDCODE/DATA");
         cmd = trans.addCustomFunction("MAINTREQSUP","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","SUPPLY_CODE");

         trans = mgr.validate(trans);
         if ("ALLOW_COND_CODE".equals(trans.getValue("GETCONDCODEUSAGEDB/DATA/COND_CODE_USAGE")))
         {
            sDefCondiCode = trans.getValue("DEFCONDCODE/DATA/CONDITION_CODE");
            sDesc = trans.getValue("CONCODE/DATA/CONDDESC"); 
         }
         String sStandardInv = trans.getValue("MAINTREQSUP/DATA/SUPPLY_CODE");
         trans.clear();

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
         if ((!mgr.isEmpty(vendorNo)) && "CUSTOMER OWNED".equals(partOwnership))
         {
            sClientOwnershipDefault = partOwnership;
            sOwner = custOwner;
         }
         trans.clear();

         //Bug 69392, start
         if (mgr.isModuleInstalled("ORDER"))
         {
            cmd = trans.addCustomFunction("CATANO","Sales_Part_API.Get_Catalog_No_For_Part_No","ITEM5_CATALOG_NO");
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");

            cmd = trans.addCustomFunction("CATADESC","Sales_Part_API.Get_Catalog_Desc","ITEM5CATALOGDESC");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addReference("ITEM5_CATALOG_NO","CATANO/DATA");

            cmd = trans.addCustomFunction("GETACT","Sales_Part_API.Get_Activeind","ACTIVEIND");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addReference("ITEM5_CATALOG_NO","CATANO/DATA");
            cmd = trans.addCustomFunction("GETACTDB","Active_Sales_Part_API.Encode","ACTIVEIND_DB");
            cmd.addReference("ACTIVEIND","GETACT/DATA");
         }
         //Bug 69392, end 
         cmd = trans.addCustomFunction("SPARESTRUCT","Equipment_Spare_Structure_API.Has_Spare_Structure_Boolean","HASSPARESTRUCTURE");
         cmd.addParameter("PART_NO");
         cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));

         cmd = trans.addCustomFunction("DIMQUAL","INVENTORY_PART_API.Get_Dim_Quality","DIMQTY");
         cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
         cmd.addParameter("PART_NO");

         cmd = trans.addCustomFunction("TYPEDESI","INVENTORY_PART_API.Get_Type_Designation","TYPEDESIGN");
         cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
         cmd.addParameter("PART_NO");

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
            cmd.addParameter("ITEM3_ACTIVITY_SEQ","0");
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
            cmd.addParameter("ITEM3_ACTIVITY_SEQ","0");
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
               cmd.addParameter("ITEM3_ACTIVITY_SEQ",mgr.readValue("ITEM3_ACTIVITY_SEQ"));
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
            cmd.addParameter("ITEM3_ACTIVITY_SEQ","0");
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
            cmd.addParameter("INCLUDE_STANDARD",sFalse);
            cmd.addParameter("INCLUDE_PROJECT",sTrue);
            cmd.addParameter("ITEM3_ACTIVITY_SEQ","0");
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

         cmd = trans.addCustomFunction("UNITMES","Purchase_Part_Supplier_API.Get_Unit_Meas","UNITMEAS");
         cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
         cmd.addParameter("PART_NO");

         cmd = trans.addCustomFunction("PARTDESC","INVENTORY_PART_API.Get_Description","SPAREDESCRIPTION");
         cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
         cmd.addParameter("PART_NO");

         //Bug 69392, start 
         if (mgr.isModuleInstalled("ORDER"))
         {
            cmd = trans.addCustomFunction("GETSALESPRICEGRPID","SALES_PART_API.GET_SALES_PRICE_GROUP_ID","SALES_PRICE_GROUP_ID");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addReference("CATALOG_NO","CATANO/DATA");
         }
         //Bug 69392, end 
         cmd = trans.addCustomFunction("SUUPCODE","INVENTORY_PART_API.Get_Supply_Code ","SUPPLY_CODE");
         cmd.addParameter("CATALOG_CONTRACT");
         cmd.addReference("CATALOG_NO","CATANO/DATA");

         trans = mgr.validate(trans);

         //Bug 69392, start 
         String cataNo = "";
         String cataDesc = "";
         String salesPriceGroupId = "";
         if (mgr.isModuleInstalled("ORDER"))
         {
            cataNo = trans.getBuffer("CATANO/DATA").getFieldValue("ITEM5_CATALOG_NO");
            cataDesc = trans.getValue("CATADESC/DATA/ITEM5CATALOGDESC");
            activeInd = trans.getValue("GETACTDB/DATA/ACTIVEIND_DB");
            salesPriceGroupId = trans.getValue("GETSALESPRICEGRPID/DATA/SALES_PRICE_GROUP_ID");
         }
         //Bug 69392, end 
         String hasStruct = trans.getValue("SPARESTRUCT/DATA/HASSPARESTRUCTURE");
         String dimQty = trans.getValue("DIMQUAL/DATA/DIMQTY");
         String typeDesi = trans.getValue("TYPEDESI/DATA/TYPEDESIGN");
         double qtyOnHand = trans.getNumberValue("INVONHAND/DATA/QTYONHAND");
         String qtyOnHand1 = mgr.getASPField("QTYONHAND").formatNumber(qtyOnHand);
         String unitMeas = trans.getValue("UNITMES/DATA/UNITMEAS");
         String partDesc = trans.getValue("PARTDESC/DATA/SPAREDESCRIPTION");
         String suppCode = trans.getValue("SUUPCODE/DATA/SUPPLY_CODE");
         double qtyAvail = trans.getNumberValue("INVAVL/DATA/QTY_AVAILABLE");
         String qtyAvail1 = mgr.getASPField("QTY_AVAILABLE").formatNumber(qtyAvail);

         trans.clear();

         cmd = trans.addCustomFunction("WOSITE","Work_Order_API.Get_Contract","WO_SITE");
         cmd.addParameter("ITEM_WO_NO",mgr.readValue("ITEM_WO_NO"));
         //Bug 69392, start
         if (mgr.isModuleInstalled("ORDER"))
         {
            cmd = trans.addCustomFunction("CATEXT","Sales_Part_API.Check_Exist","CAT_EXIST");
            cmd.addReference("WO_SITE","WOSITE/DATA");
            cmd.addParameter("CATALOG_NO",cataNo);
         }
         //Bug 69392, end

         trans = mgr.validate(trans);

         //Bug 69392, start
         double nExist = 0;
         if (mgr.isModuleInstalled("ORDER"))
            nExist = trans.getNumberValue("CATEXT/DATA/CAT_EXIST");
         //Bug 69392, end

         if (nExist != 1)
         {
            cataNo = "";
            cataDesc = "";
            salesPriceGroupId = "";
            suppCode = "";
         }
         trans.clear();

         cmd = trans.addCustomCommand("MATREQLINE","Material_Requis_Line_API.CHECK_PART_NO__");
         cmd.addParameter("SPAREDESCRIPTION",partDesc);
         cmd.addParameter("SUPPLY_CODE",suppCode);
         cmd.addParameter("UNITMEAS",unitMeas);
         cmd.addParameter("PART_NO",mgr.readValue("PART_NO",""));
         cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT",""));

         mgr.perform(trans); 

         txt = (mgr.isEmpty(cataNo) ? "": cataNo) + "^" + 
               (mgr.isEmpty(cataDesc) ? "": cataDesc) + "^" + 
               (mgr.isEmpty(hasStruct) ? "": hasStruct) + "^" + 
               (mgr.isEmpty(dimQty) ? "": dimQty) + "^" + 
               (mgr.isEmpty(typeDesi) ? "": typeDesi) + "^" + 
               (mgr.isEmpty(qtyOnHand1) ? "": qtyOnHand1) + "^" + 
               (mgr.isEmpty(unitMeas) ? "": unitMeas) + "^" + 
               (mgr.isEmpty(partDesc) ? "": partDesc) + "^"+
               (mgr.isEmpty(salesPriceGroupId) ? "" : salesPriceGroupId) + "^" +
               (mgr.isEmpty(sDefCondiCode) ? "": sDefCondiCode) + "^"+
               (mgr.isEmpty(sDesc) ? "": sDesc) + "^"+ 
               (mgr.isEmpty(qtyAvail1) ? "": qtyAvail1) + "^" +
               (mgr.isEmpty(activeInd) ? "": activeInd) + "^" +
               (mgr.isEmpty(ownership) ? "": ownership) + "^" +
               (mgr.isEmpty(sClientOwnershipDefault) ? "": sClientOwnershipDefault) + "^" +
               (mgr.isEmpty(sOwner) ? "": sOwner) + "^" ;

         mgr.responseWrite(txt);
      }
      else if ("SUPPLY_CODE".equals(val))
      {
         String qtyOnHand = "";
         sClientOwnershipDefault = mgr.readValue("PART_OWNERSHIP_DB");
         if (mgr.isEmpty(sClientOwnershipDefault))
            sClientOwnershipDefault = "COMPANY OWNED";

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
            cmd.addParameter("ITEM3_ACTIVITY_SEQ","0");
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
            cmd.addParameter("ITEM3_ACTIVITY_SEQ","0");
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
               cmd.addParameter("ITEM3_ACTIVITY_SEQ",mgr.readValue("ITEM3_ACTIVITY_SEQ"));
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
            cmd.addParameter("ITEM3_ACTIVITY_SEQ","0");
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
            cmd = trans.addCustomFunction("INV","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
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
            cmd.addParameter("ITEM3_ACTIVITY_SEQ","0");
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
         trans = mgr.validate(trans);
         qtyOnHand = trans.getBuffer("INVONHAND/DATA").getFieldValue("QTYONHAND");
         double qtyAvail = trans.getNumberValue("INVAVL/DATA/QTY_AVAILABLE"); 
         String qtyAvail1 = mgr.getASPField("QTY_AVAILABLE").formatNumber(qtyAvail);

         txt = (mgr.isEmpty(qtyOnHand) ? "": qtyOnHand) + "^" +
               (mgr.isEmpty(qtyAvail1) ? "": qtyAvail1) + "^" ; 

         mgr.responseWrite(txt);
      }
      else if ("PLAN_QTY".equals(val))
      {
         String spareId = mgr.readValue("PART_NO","");
         double nAmountCost = 0;
         double nCost = mgr.readNumberValue("ITEM_COST");
         if (isNaN(nCost))
            nCost = 0;

         double nDiscount = mgr.readNumberValue("ITEM_DISCOUNT");
         if (isNaN(nDiscount))
            nDiscount = 0;

         double nListPrice = mgr.readNumberValue("ITEM5_LIST_PRICE");
         if (isNaN(nListPrice))
            nListPrice = 0;

         String nPriceListNo = mgr.readValue("ITEM5_PRICE_LIST_NO","");

         double nDiscountedPrice = mgr.readNumberValue("ITEMDISCOUNTEDPRICE");
         if (isNaN(nDiscountedPrice))
            nDiscountedPrice = 0;

         double nSalesPriceAmount = 0;
         trans.clear();
         cmd = trans.addCustomFunction("GETOBJSUPL","MAINT_MATERIAL_REQ_LINE_API.Is_Obj_Supp_Loaned","OBJ_LOAN");
         cmd.addParameter("ITEM_WO_NO");

         trans = mgr.validate(trans);

         String sObjSup = trans.getValue("GETOBJSUPL/DATA/OBJ_LOAN");

         trans.clear();

         if (!mgr.isEmpty(spareId))
         {
            /*cmd = trans.addCustomFunction("GETINVVAL","Inventory_Part_API.Get_Inventory_Value_By_Method","ITEM_COST");
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");*/

            cmd = trans.addCustomCommand("GETINVVAL","Active_Separate_API.Get_Inventory_Value");
            cmd.addParameter("ITEM_COST");
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");
            cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
            cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
            cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));

            trans = mgr.validate(trans);

            nCost = trans.getBuffer("GETINVVAL/DATA").getNumberValue("ITEM_COST");
            if (isNaN(nCost))
               nCost = 0;
            if ("TRUE".equals(sObjSup) || "SUPPLIER LOANED".equals(mgr.readValue("PART_OWNERSHIP_DB")))
               nCost = 0;
         }

         double planQty = mgr.readNumberValue("PLAN_QTY");
         if (isNaN(planQty))
            planQty = 0;

         if (planQty == 0)
            nAmountCost = 0;
         else
            nAmountCost = nCost * planQty;

         String cataNo = mgr.readValue("ITEM5_CATALOG_NO","");  

         if (!mgr.isEmpty(cataNo) && (planQty != 0))
         {
            trans.clear();

            cmd = trans.addCustomFunction("AGREID","Active_Work_Order_API.Get_Agreement_Id","AGREEMENT_ID");
            cmd.addParameter("ITEM_WO_NO");

            cmd = trans.addCustomFunction("CUSTONO","Active_Work_Order_API.Get_Customer_No","CUSTOMER_NO");
            cmd.addParameter("ITEM_WO_NO");

            cmd = trans.addCustomCommand("PRICEINF","Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("ITEM5_BASE_PRICE","0");
            cmd.addParameter("ITEM5_SALE_PRICE","0");
            cmd.addParameter("ITEM_DISCOUNT","0");
            cmd.addParameter("CURRENCY_RATE","0");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addReference("CUSTOMER_NO","CUSTONO/DATA");
            cmd.addReference("AGREEMENT_ID","AGREID/DATA");
            cmd.addParameter("ITEM5_PRICE_LIST_NO");
            cmd.addParameter("PLAN_QTY");

            trans = mgr.validate(trans);

            if (mgr.isEmpty(nPriceListNo))
               nPriceListNo = trans.getBuffer("PRICEINF/DATA").getFieldValue("ITEM5_PRICE_LIST_NO");

            nDiscount = trans.getNumberValue("PRICEINF/DATA/DISCOUNT");
            if (isNaN(nDiscount))
               nDiscount = 0;

            nListPrice = trans.getBuffer("PRICEINF/DATA").getNumberValue("ITEM5_SALE_PRICE");
            if (isNaN(nListPrice))
               nListPrice = 0;

            if (nListPrice == 0)
            {
               nDiscountedPrice = nListPrice - (nDiscount/100*nListPrice);
               nSalesPriceAmount  = (nListPrice - (nDiscount/100*nListPrice)) * planQty;
            }
            else
            {
               if (nDiscount == 0)
               {
                  nSalesPriceAmount =  nListPrice * planQty;
                  nDiscountedPrice = nListPrice;
               }
               else
               {
                  nSalesPriceAmount = nListPrice * planQty;
                  nSalesPriceAmount = nSalesPriceAmount - (nDiscount/100*nSalesPriceAmount);
                  nDiscountedPrice = (nListPrice - (nDiscount/100*nListPrice));

               }
            }
         }

         String strCost = mgr.getASPField("ITEM_COST").formatNumber(nCost);
         String strAmountCost = mgr.getASPField("AMOUNTCOST").formatNumber(nAmountCost);
         String strListPrice = mgr.getASPField("ITEM5_LIST_PRICE").formatNumber(nListPrice);
         String strSalesPriceAmount = mgr.getASPField("ITEM5SALESPRICEAMOUNT").formatNumber(nSalesPriceAmount);
         String strDiscount = mgr.getASPField("ITEM_DISCOUNT").formatNumber(nDiscount);    
         String strDiscountedPrice = mgr.formatNumber("ITEMDISCOUNTEDPRICE",nDiscountedPrice);

         txt = (mgr.isEmpty(strCost) ? "" : strCost) + "^" + 
               (mgr.isEmpty(strAmountCost) ? "" : strAmountCost) + "^" + 
               (mgr.isEmpty(nPriceListNo) ? "" : nPriceListNo) + "^" + 
               (mgr.isEmpty(strDiscount) ? "" : strDiscount) + "^" + 
               (mgr.isEmpty(strListPrice) ? "" : strListPrice) + "^" + 
               (mgr.isEmpty(strSalesPriceAmount) ? "" : strSalesPriceAmount) + "^"+
               (mgr.isEmpty(strDiscountedPrice) ? "" : strDiscountedPrice) + "^";

         mgr.responseWrite(txt); 
      }
      //(-/+) Bug 66406, Start
      //if ("CONDITION_CODE".equals(val))
      else if ("CONDITION_CODE".equals(val))
      //(-/+) Bug 66406, End
      {
         String qtyOnHand1 = ""; 
         String qtyAvail1 = "";
         double qtyOnHand = 0;
         double qtyAvail = 0;
         double nCostAmt = 0;
         double nCost = 0;
         double planQty = 0;

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
            cmd.addParameter("ITEM3_ACTIVITY_SEQ","0");
            cmd.addParameter("PROJECT_ID","");
            cmd.addParameter("LOCATION_NO");
            cmd.addParameter("ORDER_ISSUE");
            cmd.addParameter("AUTOMAT_RESERV");
            cmd.addParameter("MANUAL_RESERV");
            cmd.addParameter("CONDITION_CODE");

            //QtyAvailable
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
            cmd.addParameter("ITEM3_ACTIVITY_SEQ","0");
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
               cmd.addParameter("ITEM3_ACTIVITY_SEQ",mgr.readValue("ITEM3_ACTIVITY_SEQ"));
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
            cmd.addParameter("ITEM3_ACTIVITY_SEQ","0");
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

            //QtyAvailable
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
            cmd.addParameter("ITEM3_ACTIVITY_SEQ","0");
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
         cmd = trans.addCustomFunction("CONCODE","CONDITION_CODE_API.Get_Description","CONDDESC");
         cmd.addParameter("CONDITION_CODE");

         trans = mgr.validate(trans);
         qtyOnHand = trans.getNumberValue("INVONHAND/DATA/QTYONHAND"); 
         qtyOnHand1 = mgr.getASPField("QTYONHAND").formatNumber(qtyOnHand);
         qtyAvail = trans.getNumberValue("INVAVL/DATA/QTY_AVAILABLE");
         qtyAvail1 = mgr.getASPField("QTY_AVAILABLE").formatNumber(qtyAvail);
         String descri = trans.getValue("CONCODE/DATA/CONDDESC");

         trans.clear();

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
         //(-) Bug 66406, Start
         //mgr.endResponse();
         //(-) Bug 66406, End
      }
      //(-/+) Bug 66406, Start
      //if ("PART_OWNERSHIP".equals(val))
      else if ("PART_OWNERSHIP".equals(val))
      //(-/+) Bug 66406, End
      {
         String qtyOnHand1 = "";
         String qtyAvail1 = "";
         double qtyOnHand = 0;
         double qtyAvail = 0;

         cmd = trans.addCustomFunction("MAINTREQSUP","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","SUPPLY_CODE");

         cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
         cmd.addParameter("PART_OWNERSHIP");

         trans = mgr.validate(trans);
         String sStandardInv = trans.getValue("MAINTREQSUP/DATA/SUPPLY_CODE");
         String sOwnershipDb = trans.getValue("VALIDOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");
         trans.clear();

         if ("COMPANY OWNED".equals(sOwnershipDb))
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
            cmd.addParameter("PART_OWNERSHIP_DB",sOwnershipDb);
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
            cmd.addParameter("ITEM3_ACTIVITY_SEQ","0");
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
            cmd.addParameter("PART_OWNERSHIP_DB",sOwnershipDb);
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
            cmd.addParameter("ITEM3_ACTIVITY_SEQ","0");
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
               cmd.addParameter("ITEM3_ACTIVITY_SEQ",mgr.readValue("ITEM3_ACTIVITY_SEQ"));
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
            cmd.addParameter("PART_OWNERSHIP_DB",sOwnershipDb);
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
            cmd.addParameter("ITEM3_ACTIVITY_SEQ","0");
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
            cmd.addParameter("PART_OWNERSHIP_DB",sOwnershipDb);
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
            cmd.addParameter("ITEM3_ACTIVITY_SEQ","0");
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

         trans = mgr.validate(trans);
         qtyOnHand = trans.getNumberValue("INVONHAND/DATA/QTYONHAND"); 
         qtyOnHand1 = mgr.getASPField("QTYONHAND").formatNumber(qtyOnHand);
         qtyAvail = trans.getNumberValue("INVAVL/DATA/QTY_AVAILABLE");
        qtyAvail1 = mgr.getASPField("QTY_AVAILABLE").formatNumber(qtyAvail);

         txt = (mgr.isEmpty(sOwnershipDb) ? "" : (sOwnershipDb)) + "^"+
               (mgr.isEmpty(qtyOnHand1) ? "": qtyOnHand1) + "^" +
               (mgr.isEmpty(qtyAvail1) ? "": qtyAvail1) + "^" ; 

         mgr.responseWrite(txt);
         //(-) Bug 66406, Start
         //mgr.endResponse();
         //(-) Bug 66406, End
      }
      //(-/+) Bug 66406, Start
      //if ("OWNER".equals(val))
      else if ("OWNER".equals(val))
      //(-/+) Bug 66406, End
      {
         String sOwnershipDb = mgr.readValue("PART_OWNERSHIP_DB");
         String qtyOnHand1 = "";
         String qtyAvail1 = "";
         double qtyOnHand = 0;
         double qtyAvail = 0;
         String sOwnerName="";

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
            cmd.addParameter("ITEM3_ACTIVITY_SEQ","0");
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
            cmd.addParameter("ITEM3_ACTIVITY_SEQ","0");
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
               cmd.addParameter("ITEM3_ACTIVITY_SEQ",mgr.readValue("ITEM3_ACTIVITY_SEQ"));
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
            cmd.addParameter("ITEM3_ACTIVITY_SEQ","0");
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
            cmd.addParameter("ITEM3_ACTIVITY_SEQ","0");
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

         if ("CUSTOMER OWNED".equals(sOwnershipDb))
         {
            cmd = trans.addCustomFunction("GETOWNERNAME", "CUSTOMER_INFO_API.Get_Name", "OWNER_NAME");
            cmd.addParameter("OWNER");
         }
         if ("SUPPLIER LOANED".equals(sOwnershipDb))
         {
            cmd = trans.addCustomFunction("GETOWNERNAME1", "Supplier_API.Get_Vendor_Name", "OWNER_NAME");
            cmd.addParameter("OWNER");
         }

         cmd = trans.addCustomFunction("GETOWCUST","ACTIVE_SEPARATE_API.Get_Customer_No", "WO_CUST");
         cmd.addParameter("WO_NO",mgr.readValue("ITEM3_WO_NO",""));

         trans = mgr.validate(trans);

         qtyOnHand = trans.getNumberValue("INVONHAND/DATA/QTYONHAND");
         qtyOnHand1 = mgr.getASPField("QTYONHAND").formatNumber(qtyOnHand);
         qtyAvail = trans.getNumberValue("INVAVL/DATA/QTY_AVAILABLE");
         qtyAvail1 = mgr.getASPField("QTY_AVAILABLE").formatNumber(qtyAvail);
         if ("CUSTOMER OWNED".equals(sOwnershipDb))
            sOwnerName = trans.getValue("GETOWNERNAME/DATA/OWNER_NAME");
         if ("SUPPLIER LOANED".equals(sOwnershipDb))
            sOwnerName = trans.getValue("GETOWNERNAME1/DATA/OWNER_NAME");
         String sWoCust    = trans.getValue("GETOWCUST/DATA/WO_CUST");

         txt = (mgr.isEmpty(sOwnerName) ? "" : (sOwnerName)) + "^" + (mgr.isEmpty(sWoCust) ? "": (sWoCust)) + "^" + (mgr.isEmpty(qtyOnHand1) ? "": qtyOnHand1) + "^" + (mgr.isEmpty(qtyAvail1) ? "": qtyAvail1) + "^" ;           
         mgr.responseWrite(txt);
         mgr.endResponse();
      }
      else if ("ITEM5_CATALOG_NO".equals(val))
      {
         cmd = trans.addCustomFunction("GETOBJSUPL","MAINT_MATERIAL_REQ_LINE_API.Is_Obj_Supp_Loaned","OBJ_LOAN");
         cmd.addParameter("ITEM_WO_NO");

         trans = mgr.validate(trans);

         String sObjSup = trans.getValue("GETOBJSUPL/DATA/OBJ_LOAN");

         trans.clear();    

         cmd = trans.addCustomFunction("CURRCO","ACTIVE_SEPARATE_API.Get_Currency_Code","CURRENCEY_CODE");
         cmd.addParameter("ITEM_WO_NO");

         cmd = trans.addCustomFunction("AGREID","Active_Work_Order_API.Get_Agreement_Id","AGREEMENT_ID");
         cmd.addParameter("ITEM_WO_NO");

         cmd = trans.addCustomFunction("CUSTONO","Active_Work_Order_API.Get_Customer_No","CUSTOMER_NO");
         cmd.addParameter("ITEM_WO_NO");

         //Bug 69392, start
         if (mgr.isModuleInstalled("ORDER"))
         {
            cmd = trans.addCustomFunction("PRILST","Customer_Order_Pricing_Api.Get_Valid_Price_List","ITEM5_PRICE_LIST_NO");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addReference("CUSTOMER_NO","CUSTONO/DATA");
            cmd.addReference("CURRENCEY_CODE","CURRCO/DATA");
         }
         //Bug 69392, end

         cmd = trans.addCustomCommand("PRICEINF","Work_Order_Coding_API.Get_Price_Info");
         cmd.addParameter("ITEM5_BASE_PRICE","0"); 
         cmd.addParameter("ITEM5_SALE_PRICE","0");
         cmd.addParameter("ITEM_DISCOUNT","0");
         cmd.addParameter("CURRENCY_RATE","0");
         cmd.addParameter("CATALOG_CONTRACT");
         cmd.addParameter("ITEM5_CATALOG_NO");
         cmd.addReference("CUSTOMER_NO","CUSTONO/DATA");
         cmd.addReference("AGREEMENT_ID","AGREID/DATA");
         cmd.addReference("ITEM5_PRICE_LIST_NO","PRILST/DATA");
         cmd.addParameter("PLAN_QTY");

         //Bug 69392, start
         if (mgr.isModuleInstalled("ORDER"))
         {
            cmd = trans.addCustomFunction("CATADESC","Sales_Part_API.Get_Catalog_Desc","ITEM5CATALOGDESC");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addParameter("ITEM5_CATALOG_NO");

            cmd = trans.addCustomFunction("GETSALESPRICEGRPID","SALES_PART_API.GET_SALES_PRICE_GROUP_ID","SALES_PRICE_GROUP_ID");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addParameter("ITEM5_CATALOG_NO");
         }
         //Bug 69392, end

         String spareId = mgr.readValue("PART_NO");

         double nCost = mgr.readNumberValue("ITEM_COST");
         if (isNaN(nCost))
            nCost = 0;

         if (!mgr.isEmpty(spareId))
         {
            /*cmd = trans.addCustomFunction("GETINVVAL"," Inventory_Part_API.Get_Inventory_Value_By_Method","ITEM_COST");
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");  */

            cmd = trans.addCustomCommand("GETINVVAL","Active_Separate_API.Get_Inventory_Value");
            cmd.addParameter("ITEM_COST");
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");
            cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
            cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
            cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));
         }
         trans = mgr.validate(trans);

         //Bug 69392, start
         String nPriceListNo = "";
         if (mgr.isModuleInstalled("ORDER"))
            nPriceListNo =  trans.getBuffer("PRILST/DATA").getFieldValue("ITEM5_PRICE_LIST_NO");
         //Bug 69392, end

         if (mgr.isEmpty(nPriceListNo))
            nPriceListNo = trans.getBuffer("PRICEINF/DATA").getValue("ITEM5_PRICE_LIST_NO");

         double nDiscount = trans.getNumberValue("PRICEINF/DATA/DISCOUNT");
         if (isNaN(nDiscount))
            nDiscount = 0;

         double nListPrice = 0;

         double planQty = mgr.readNumberValue("PLAN_QTY");
         if (isNaN(planQty))
            planQty = 0;

         double nSalesPriceAmount = 0;
         double nDiscountedPriceAmt = 0;

         nListPrice = trans.getNumberValue("PRICEINF/DATA/ITEM5_SALE_PRICE");
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
            nCost = trans.getBuffer("GETINVVAL/DATA").getNumberValue("ITEM_COST");
            if (isNaN(nCost))
               nCost = 0;
         }
         if ("TRUE".equals(sObjSup) || "SUPPLIER LOANED".equals(mgr.readValue("PART_OWNERSHIP_DB")))
            nCost = 0;

         double nCostAmt = 0;

         if (isNaN(planQty))
            nCostAmt = 0;
         else
            nCostAmt    = nCost * planQty; 

         //Bug 69392, start
         String cataDesc = "";
         String salesPriceGroupId = "";
         if (mgr.isModuleInstalled("ORDER"))
         {
            cataDesc = trans.getValue("CATADESC/DATA/ITEM5CATALOGDESC");
            salesPriceGroupId = trans.getValue("GETSALESPRICEGRPID/DATA/SALES_PRICE_GROUP_ID");
         }
         //Bug 69392, end

         String strListPrice = mgr.getASPField("ITEM5_LIST_PRICE").formatNumber(nListPrice);
         String strCost = mgr.getASPField("ITEM_COST").formatNumber(nCost);
         String strCostAmt = mgr.getASPField("AMOUNTCOST").formatNumber(nCostAmt);
         String strDiscount = mgr.getASPField("ITEM_DISCOUNT").formatNumber(nDiscount);
         String strSalesPriceAmount = mgr.getASPField("ITEM5SALESPRICEAMOUNT").formatNumber(nSalesPriceAmount);
         String strnDiscountedPriceAmt = mgr.formatNumber("ITEMDISCOUNTEDPRICE",nDiscountedPriceAmt);


         txt = (mgr.isEmpty(strListPrice) ? "" : strListPrice) + "^" + 
               (mgr.isEmpty(strCost) ? "" : strCost) + "^" + 
               (mgr.isEmpty(strCostAmt) ? "" : strCostAmt) + "^" + 
               (mgr.isEmpty(cataDesc) ? "" : cataDesc) + "^" + 
               (mgr.isEmpty(strDiscount) ? "" : strDiscount) + "^"+ 
               (mgr.isEmpty(strSalesPriceAmount) ? "" : strSalesPriceAmount) + "^"+
               (mgr.isEmpty(strnDiscountedPriceAmt) ? "" : strnDiscountedPriceAmt) + "^"+
               (mgr.isEmpty(nPriceListNo) ? "" : nPriceListNo) + "^" +
               (mgr.isEmpty(salesPriceGroupId) ? "" : salesPriceGroupId) + "^";


         mgr.responseWrite(txt);
      }
      else if ("ITEM5_PRICE_LIST_NO".equals(val))
      {
         double nDiscount = 0;
         double nCost = 0;
         double nCostAmt = 0;
         double nListPrice = 0;
         double nSalesPriceAmount = 0;
         double nDiscountedPriceAmt = 0;
         String partNo = mgr.readValue("PART_NO","");
         String nPriceListNo = mgr.readValue("ITEM5_PRICE_LIST_NO","");

         if (!mgr.isEmpty(partNo))
         {
            /*cmd = trans.addCustomFunction("GETINVAL","Inventory_Part_API.Get_Inventory_Value_By_Method","ITEM_COST");
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");*/

            cmd = trans.addCustomCommand("GETINVAL","Active_Separate_API.Get_Inventory_Value");
            cmd.addParameter("ITEM_COST");
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");
            cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
            cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
            cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));

            trans = mgr.validate(trans);

            nCost = trans.getBuffer("GETINVAL/DATA").getNumberValue("ITEM_COST");
            if (isNaN(nCost))
               nCost = 0;
         }
         double planQty = mgr.readNumberValue("PLAN_QTY");
         if (isNaN(planQty))
            planQty = 0;

         if (planQty == 0)
            nCostAmt = 0;
         else
            nCostAmt    = nCost * planQty;

         String cataNo = mgr.readValue("ITEM5_CATALOG_NO",""); 

         if (!mgr.isEmpty(cataNo) && planQty != 0)
         {
            trans.clear();

            cmd = trans.addCustomFunction("AGREID","Active_Work_Order_API.Get_Agreement_Id","AGREEMENT_ID");
            cmd.addParameter("ITEM_WO_NO");

            cmd = trans.addCustomFunction("CUSTONO","Active_Work_Order_API.Get_Customer_No","CUSTOMER_NO");
            cmd.addParameter("ITEM_WO_NO");

            cmd = trans.addCustomCommand("PRICEINF","Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("ITEM5_BASE_PRICE","0"); 
            cmd.addParameter("ITEM5_SALE_PRICE","0");
            cmd.addParameter("ITEM_DISCOUNT","0");
            cmd.addParameter("CURRENCY_RATE","0");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addReference("CUSTOMER_NO","CUSTONO/DATA");
            cmd.addReference("AGREEMENT_ID","AGREID/DATA");
            cmd.addParameter("ITEM5_PRICE_LIST_NO");
            cmd.addParameter("PLAN_QTY");

            trans = mgr.validate(trans);        

            if (mgr.isEmpty(nPriceListNo))
               nPriceListNo = trans.getBuffer("PRICEINF/DATA").getFieldValue("ITEM5_PRICE_LIST_NO");

            nDiscount = trans.getNumberValue("PRICEINF/DATA/DISCOUNT");
            if (isNaN(nDiscount))
               nDiscount = 0;

            nListPrice = trans.getNumberValue("PRICEINF/DATA/ITEM5_SALE_PRICE");
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

         String strCost = mgr.getASPField("ITEM_COST").formatNumber(nCost);
         String strCostAmt = mgr.getASPField("AMOUNTCOST").formatNumber(nCostAmt);
         String strDiscount = mgr.getASPField("ITEM_DISCOUNT").formatNumber(nDiscount);
         String strListPrice = mgr.getASPField("ITEM5_LIST_PRICE").formatNumber(nListPrice);
         String strSalesPriceAmount = mgr.getASPField("ITEM5SALESPRICEAMOUNT").formatNumber(nSalesPriceAmount);
         String strDiscountedPriceAmt = mgr.formatNumber("ITEMDISCOUNTEDPRICE",nDiscountedPriceAmt);

         txt =  (mgr.isEmpty(strCost) ? "" : strCost) + "^" + 
                (mgr.isEmpty(strCostAmt) ? "" : strCostAmt) + "^" + 
                (mgr.isEmpty(strListPrice) ? "" : strListPrice) + "^"+ 
                (mgr.isEmpty(strDiscount) ? "" : strDiscount) + "^" + 
                (mgr.isEmpty(strSalesPriceAmount) ? "" : strSalesPriceAmount) + "^"+
                (mgr.isEmpty(strDiscountedPriceAmt) ? "" : strDiscountedPriceAmt) + "^";

         mgr.responseWrite(txt); 
      }
      else if ("ITEM_DISCOUNT".equals(val))
      {
         double nListPrice;
         double planQty;
         double nDiscount;
         double salePriceAmt;
         double discountedPrice;
         String strSalePriceAmt = null;
         String strDiscountedPriceAmt = null;

         nListPrice = mgr.readNumberValue("ITEM5_LIST_PRICE");
         planQty = mgr.readNumberValue("PLAN_QTY");
         nDiscount = mgr.readNumberValue("ITEM_DISCOUNT");

         salePriceAmt =  nListPrice * planQty;
         salePriceAmt =  salePriceAmt - (nDiscount / 100 * salePriceAmt);
         discountedPrice = nListPrice - (nDiscount/100*nListPrice); 

         if (!isNaN(salePriceAmt))
            strSalePriceAmt = mgr.getASPField("ITEM5SALESPRICEAMOUNT").formatNumber(salePriceAmt);

         if (!isNaN(discountedPrice))
            strDiscountedPriceAmt = mgr.formatNumber("ITEMDISCOUNTEDPRICE",discountedPrice);

         txt = (mgr.isEmpty(strSalePriceAmt) ? "" : strSalePriceAmt) + "^" + (mgr.isEmpty(strDiscountedPriceAmt) ? "" : strDiscountedPriceAmt) + "^";

         mgr.responseWrite(txt);
      }
      else if ("OWNER".equals(val))
      {
         cmd = trans.addCustomFunction("GETOWNERNAME", "CUSTOMER_INFO_API.Get_Name", "OWNER_NAME");
         cmd.addParameter("OWNER");

         cmd = trans.addCustomFunction("GETOWCUST","ACTIVE_SEPARATE_API.Get_Customer_No", "WO_CUST");
         cmd.addParameter("WO_NO",mgr.readValue("ITEM3_WO_NO",""));

         trans = mgr.validate(trans);

         String sOwnerName = trans.getValue("GETOWNERNAME/DATA/OWNER_NAME");
         String sWoCust    = trans.getValue("GETOWCUST/DATA/WO_CUST");

         txt = (mgr.isEmpty(sOwnerName) ? "" : (sOwnerName)) + "^" + (mgr.isEmpty(sWoCust) ? "": (sWoCust)) + "^";  
         mgr.responseWrite(txt);
      }

      else if ("STD_JOB_ID".equals(val))
      {
         String valueStr = mgr.readValue("STD_JOB_ID");
         String stdJobId  = "";
         String stdJobContract = mgr.readValue("STD_JOB_CONTRACT");
         String stdJobRev = mgr.readValue("STD_JOB_REVISION");

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

         cmd = trans.addCustomFunction("GETSTATUS","Standard_Job_API.Get_Status","STD_JOB_STATUS");
         cmd.addParameter("STD_JOB_ID", stdJobId);
         cmd.addParameter("STD_JOB_CONTRACT", stdJobContract);
         cmd.addParameter("STD_JOB_REVISION", stdJobRev);

         trans = mgr.validate(trans);

         String desc = trans.getValue("GETDESC/DATA/DESCRIPTION");
         String status = trans.getValue("GETSTATUS/DATA/STD_JOB_STATUS");

         txt = (mgr.isEmpty(stdJobId)?"":stdJobId) + "^" + (mgr.isEmpty(stdJobRev)?"":stdJobRev) + "^" + (mgr.isEmpty(desc)?"":desc) + "^"+(mgr.isEmpty(status)?"":status) + "^";

         mgr.responseWrite(txt);
      }
      else if ("STD_JOB_REVISION".equals(val))
      {
         trans.clear();

         cmd = trans.addCustomFunction("GETDESC", "Standard_Job_API.Get_Definition", "DESCRIPTION");
         cmd.addParameter("STD_JOB_ID", mgr.readValue("STD_JOB_ID"));
         cmd.addParameter("STD_JOB_CONTRACT", mgr.readValue("STD_JOB_CONTRACT"));
         cmd.addParameter("STD_JOB_REVISION", mgr.readValue("STD_JOB_REVISION"));

         cmd = trans.addCustomFunction("GETSTATUS","Standard_Job_API.Get_Status","STD_JOB_STATUS");
         cmd.addParameter("STD_JOB_ID", mgr.readValue("STD_JOB_ID"));
         cmd.addParameter("STD_JOB_CONTRACT", mgr.readValue("STD_JOB_CONTRACT"));
         cmd.addParameter("STD_JOB_REVISION", mgr.readValue("STD_JOB_REVISION"));

         trans = mgr.validate(trans);

         String desc = trans.getValue("GETDESC/DATA/DESCRIPTION");
         String status = trans.getValue("GETSTATUS/DATA/STD_JOB_STATUS");

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
         String sQty = "";

         double nQty = 0;

         String sAgreementId = "";

         trans.clear();

         cmd = trans.addCustomFunction("GETJOBEXIST", "Work_Order_Job_API.Check_Exist", "N_JOB_EXIST");
         cmd.addParameter("ITEM6_WO_NO");
         cmd.addParameter("JOB_ID"); 

         cmd = trans.addCustomFunction("GETAGR", "Active_Separate_API.Get_Agreement_Id", "S_AGREEMENT_ID");
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
            sQty = mgr.getASPField("N_QTY").formatNumber(nQty);
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
               sQty + "^" +
               (mgr.isEmpty(sAgreementId)?"":sAgreementId) + "^";

         mgr.responseWrite(txt);
      }
      else if ("SIGN_ID".equals(val))
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
         String sContractId = mgr.readValue("CONTRACT_ID");
         String sLineNo = mgr.readValue("LINE_NO");

         if (sContractId.indexOf("^") > -1)
         {
            String strAttr = sContractId;
            sContractId = strAttr.substring(0, strAttr.indexOf("^"));       
            sLineNo = strAttr.substring(strAttr.indexOf("^") + 1, strAttr.length());
         }
         //Call 144631
         //Bug 84436, Start
          if (mgr.isModuleInstalled("SRVCON"))
         {
         cmd = trans.addCustomFunction("SCCONTRACTCUSTOMER","Sc_Service_Contract_API.Get_Customer_Id","CUSTOMER_NO");
         cmd.addParameter("CONTRACT_ID",sContractId);
         }
         //Bug 84436, End
//  Bug 68947, Start
         cmd = trans.addCustomFunction("CUSTNAME", "CUSTOMER_INFO_API.Get_Name", "CUSTOMERNAME");
         cmd.addReference("CUSTOMER_NO","SCCONTRACTCUSTOMER/DATA");

          //Bug 84436, Start
          if (mgr.isModuleInstalled("SRVCON"))
         {
         cmd = trans.addCustomFunction("SCCONTRACTNAME","SC_SERVICE_CONTRACT_API.Get_Contract_Name","CONTRACT_NAME");
         cmd.addParameter("CONTRACT_ID",sContractId);
          }
         
          if (mgr.isModuleInstalled("PCMSCI"))
         {
         cmd = trans.addCustomFunction("PSCLINEDESC", "PSC_CONTR_PRODUCT_API.Get_Description", "LINE_DESC");
         cmd.addParameter("CONTRACT_ID",sContractId);
         cmd.addParameter("LINE_NO",sLineNo);
          }
          //Bug 84436, End
         
         trans = mgr.validate(trans);

         String sCustNo   = trans.getValue("SCCONTRACTCUSTOMER/DATA/CUSTOMER_NO");
         String sCustName = trans.getValue("CUSTNAME/DATA/CUSTOMERNAME");
         String sConName   = trans.getValue("SCCONTRACTNAME/DATA/CONTRACT_NAME");
         String sLineDesc = trans.getValue("PSCLINEDESC/DATA/LINE_DESC");

         txt =  (mgr.isEmpty(sContractId) ? "" : (sContractId)) + "^" 
                +(mgr.isEmpty(sConName)?"":sConName)+"^"
                +(mgr.isEmpty(sLineNo)?"":sLineNo)+"^" 
                +(mgr.isEmpty(sLineDesc)?"":sLineDesc)+"^"
                +(mgr.isEmpty(sCustNo)?"":sCustNo)+"^" 
                +(mgr.isEmpty(sCustName)?"":sCustName)+"^"; 
//  Bug 68947, End
         //End Call 144631
//	    	txt =  (mgr.isEmpty(sContractId) ? "" : (sContractId)) + "^" +(mgr.isEmpty(sLineNo)?"":sLineNo)+"^"; 
         mgr.responseWrite(txt);
      }
      else if ("LINE_NO".equals(val))
      {
         String sContractId = mgr.readValue("CONTRACT_ID");
         String sLineNo = mgr.readValue("LINE_NO");

         if (sLineNo.indexOf("^") > -1)
         {
            String strAttr = sLineNo;
            sContractId = strAttr.substring(0, strAttr.indexOf("^"));       
            sLineNo =  strAttr.substring(strAttr.indexOf("^") + 1, strAttr.length());                
         }
         cmd = trans.addCustomCommand("SCCONTRACTAGREEMENT","SC_CONTRACT_AGREEMENT_API.Get_First_Agreement");
         cmd.addParameter("AGREEMENT_ID");
         cmd.addParameter("DESCRIPTION");
         cmd.addParameter("CONTRACT_ID",sContractId);
//  Bug 68947, Start
         //Bug 84436, Start
         if (mgr.isModuleInstalled("SRVCON"))
         {
         cmd = trans.addCustomFunction("SCCONTRACTNAME","SC_SERVICE_CONTRACT_API.Get_Contract_Name","CONTRACT_NAME");
         cmd.addParameter("CONTRACT_ID",sContractId);
         }
         
          if (mgr.isModuleInstalled("PCMSCI"))
         {
         cmd = trans.addCustomFunction("PSCLINEDESC", "PSC_CONTR_PRODUCT_API.Get_Description", "LINE_DESC");
         cmd.addParameter("CONTRACT_ID",sContractId);
         cmd.addParameter("LINE_NO",sLineNo);
         }
         //Bug 84436, End

         trans = mgr.validate(trans);

         String sAgreementId = trans.getBuffer("SCCONTRACTAGREEMENT/DATA").getValue("AGREEMENT_ID");
         String sConName   = trans.getValue("SCCONTRACTNAME/DATA/CONTRACT_NAME");
         String sLineDesc = trans.getValue("PSCLINEDESC/DATA/LINE_DESC");

         txt =  (mgr.isEmpty(sContractId) ? "" : (sContractId)) + "^" 
                +(mgr.isEmpty(sConName)?"":sConName)+"^"
                +(mgr.isEmpty(sLineNo)?"":sLineNo) + "^" 
                +(mgr.isEmpty(sLineDesc)?"":sLineDesc)+"^"
                +(mgr.isEmpty(sAgreementId)?"":sAgreementId) + "^"; 
//  Bug 68947, End
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

//----------------- GET VLAUES FOR BLOCK 4 ------------------------------------

   public void fillRows( int headrowno)
   {
      double actCost;
      double actRev;
      double actMargin;
      double planRev;
      double planCost;
      double planMargin;
      double budgCost;
      double budgRev;
      double budgMargin;

      ASPManager mgr = getASPManager();

      headset.goTo(headrowno);
      trans.clear();

      if (itemset4.countRows()>0)
      {
         int n = itemset4.countRows();
         itemset4.first();
         for (int i=0; i<=n; ++i)
         {
            cmd = trans.addCustomFunction( "ACTCOST"+i, "Work_Order_Budget_API.Get_Actual_Cost","ACTUAL_COST");
            cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
            cmd.addParameter("WORK_ORDER_COST_TYPE",itemset4.getRow().getValue("WORK_ORDER_COST_TYPE"));

            cmd = trans.addCustomCommand( "ACTREV"+i, "Work_Order_Budget_API.Actual_Revenue");
            cmd.addParameter("ACTUAL_REVENUE",itemset4.getRow().getFieldValue("ACTUAL_REVENUE"));
            cmd.addParameter("WO_NO",headset.getRow().getFieldValue("WO_NO"));
            cmd.addParameter("WORK_ORDER_COST_TYPE",itemset4.getRow().getFieldValue("WORK_ORDER_COST_TYPE"));

            itemset4.next();
         }
         trans = mgr.perform(trans);
         itemset4.first();

         for (int i=0; i<=n; ++i)
         {
            budgRev = itemset4.getRow().getNumberValue("BUDGET_REVENUE");
            budgRev = (isNaN(budgRev)?0:budgRev);

            budgCost = itemset4.getRow().getNumberValue("BUDGET_COST");
            budgCost = (isNaN(budgCost)?0:budgCost);

            planRev = itemset4.getRow().getNumberValue("NPLANNEDREVENUE");
            planRev = (isNaN(planRev)?0:planRev);

            planCost = itemset4.getRow().getNumberValue("NPLANNEDCOST");
            planCost = (isNaN(planCost)?0:planCost);

            actCost = trans.getNumberValue("ACTCOST"+i+"/DATA/ACTUAL_COST");
            actCost = (isNaN(actCost)?0:actCost);

            actRev = trans.getNumberValue("ACTREV"+i+"/DATA/ACTUAL_REVENUE");
            actRev = (isNaN(actRev)?0:actRev);

            actMargin =  actRev - actCost;
            budgMargin = budgRev - budgCost;
            planMargin = planRev - planCost;

            ASPBuffer buf = itemset4.getRow();

            buf.setNumberValue("ACTUAL_COST",actCost);
            buf.setNumberValue("ACTUAL_REVENUE",actRev);
            buf.setNumberValue("ACTUAL_MARGIN",actMargin);
            buf.setNumberValue("BUDGET_MARGIN",budgMargin);

            itemset4.setRow(buf);
            itemset4.next();
         }
         itemset4.first();
      }
   }


   public void sumColumns()
   {
      ASPManager mgr = getASPManager();

      double totBudgCost = 0;
      double totBudgRev = 0;
      double totBudgMargin = 0;
      double totplannCost = 0;
      double totPlannRev = 0;
      double totplannMargin = 0;
      double totActCost = 0;
      double totActRev = 0;
      double totActMargin = 0;

      double budCost;
      double budRev;
      double budMargine;
      double plannCost;
      double plannRev;
      double planMargin;
      double actCost;
      double actRev;
      double actMargin;

      int n = itemset4.countRows();
      itemset4.first();

      for (int i=1; i<=n; i++)
      {
         budCost = itemset4.getRow().getNumberValue("BUDGET_COST");
         budCost = (isNaN(budCost)?0:budCost);

         budRev = itemset4.getRow().getNumberValue("BUDGET_REVENUE");
         budRev = (isNaN(budRev)?0:budRev);

         budMargine = itemset4.getRow().getNumberValue("BUDGET_MARGIN");
         budMargine = (isNaN(budMargine)?0:budMargine);

         plannCost = itemset4.getRow().getNumberValue("PLANNED_COST");
         plannCost = (isNaN(plannCost)?0:plannCost);

         plannRev =  itemset4.getRow().getNumberValue("PLANNED_REVENUE");
         plannRev = (isNaN(plannRev)?0:plannRev);

         planMargin = itemset4.getRow().getNumberValue("PLANNED_MARGIN");
         planMargin = (isNaN(planMargin)?0:planMargin);

         actCost = itemset4.getRow().getNumberValue("ACTUAL_COST");
         actCost = (isNaN(actCost)?0:actCost);

         actRev = itemset4.getRow().getNumberValue("ACTUAL_REVENUE");
         actRev = (isNaN(actRev)?0:actRev);

         actMargin = itemset4.getRow().getNumberValue("ACTUAL_MARGIN");
         actMargin = (isNaN(actMargin)?0:actMargin);

         totBudgCost = totBudgCost + budCost;
         totBudgRev =  totBudgRev + budRev;
         totBudgMargin = totBudgMargin + budMargine;
         totplannCost =  totplannCost + plannCost;
         totPlannRev = totPlannRev + plannRev;
         totplannMargin = totplannMargin + planMargin;
         totActCost =  totActCost + actCost;
         totActRev =  totActRev + actRev;
         totActMargin =  totActMargin + actMargin; 

         itemset4.next();
      }

      ASPBuffer data = trans.getBuffer("ITEM4/DATA");
      itemset4.addRow(data);

      ASPBuffer buf = itemset4.getRow();

      buf.setValue("WORK_ORDER_COST_TYPE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMSUMMARY: Summary"));  
      buf.setNumberValue("BUDGET_COST",totBudgCost);
      buf.setNumberValue("BUDGET_REVENUE",totBudgRev);
      buf.setNumberValue("BUDGET_MARGIN",totBudgMargin);
      buf.setNumberValue("PLANNED_COST",totplannCost);
      buf.setNumberValue("PLANNED_REVENUE",totPlannRev);
      buf.setNumberValue("PLANNED_MARGIN",totplannMargin);
      buf.setNumberValue("ACTUAL_COST",totActCost);
      buf.setNumberValue("ACTUAL_REVENUE",totActRev);
      buf.setNumberValue("ACTUAL_MARGIN",totActMargin);

      itemset4.setRow(buf);
      itemset4.first();

   }

//-----------------------------------------------------------------------------
//-------------------------- HEAD BLOCK ---------------------------------------
//-----------------------------------------------------------------------------   

   public void okFindNeW()
   {
      boolean salesSec = false;

      ASPManager mgr = getASPManager();

      ASPQuery q = trans.addQuery(headblk);
      q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
      q.addWhereCondition("WO_NO = ?");
      q.addParameter("WO_NO",sent_wo_no);
      q.setOrderByClause("WO_NO");
      q.includeMeta("ALL");

      if (mgr.dataTransfered())
         q.addOrCondition( mgr.getTransferedData() );

      itemset4.clear();

      mgr.submit(trans);

      eval(headset.syncItemSets());

      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMNODATA: No data found."));
         headset.clear();
      }
      setStringLables();

      if (headset.countRows() == 1)
      {
         okFindITEM1();
         okFindITEM3();
         okFindITEM4();
         okFindITEM6();
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
      }

      qrystr = mgr.createSearchURL(headblk);

      if (sentEmployee)
      {
         itemlay1.setLayoutMode(itemlay1.NEW_LAYOUT);
         trans.clear();     
         newRowITEM1();

         if (!recordCancel)
         {
            ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer();
            secBuff.addSecurityQuery("SALES_PART");

            secBuff = mgr.perform(secBuff);

            if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
               salesSec = true;

            trans.clear();     
            cmd = trans.addCustomFunction("GETEMPID","Company_Emp_API.Get_Max_Employee_Id","EMP_NO");
            cmd.addParameter("COMPANY",itemset1.getRow().getFieldValue("COMPANY"));
            cmd.addParameter("EMP_SIGNATURE",sent_emp_id);

            cmd = trans.addCustomFunction("GETEMPNAME","Person_Info_API.Get_Name","NAME");
            cmd.addParameter("EMP_SIGNATURE",sent_emp_id);

            if (salesSec &&  ! mgr.isEmpty(sent_salesPartNo))
            {
               cmd = trans.addCustomFunction("PARTCOST","Sales_Part_API.Get_Cost","COST1");
               cmd.addParameter("ITEM1_CONTRACT",sent_site);
               cmd.addParameter("CATALOG_NO",sent_salesPartNo);     

               cmd = trans.addCustomFunction("DEFCATDESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
               cmd.addParameter("ITEM1_CONTRACT",sent_site);
               cmd.addParameter("CATALOG_NO",sent_salesPartNo);      
            }

            trans = mgr.perform(trans); 

            String empNo = trans.getValue("GETEMPID/DATA/EMP_NO");
            String empName = trans.getValue("GETEMPNAME/DATA/NAME");

            itemset1.setValue("NAME",sent_emp_name);
            itemset1.setValue("LINE_DESCRIPTION",sent_description);
	    //Bug 85045, Start
	    if (!mgr.isEmpty(sent_department))
	    {
	       itemset1.setValue("ORG_CODE",sent_department);
	    }
	    if (!mgr.isEmpty(sent_org_contract))
	    {
	       itemset1.setValue("CONTRACT",sent_org_contract);
	    }
	    //Bug 85045, End
            itemset1.setValue("ROLE_CODE",sent_craft);      
            itemset1.setValue("TEAM_CONTRACT",sent_team_contract);      
            itemset1.setValue("TEAM_ID",sent_team_id);      
            itemset1.setValue("EMP_SIGNATURE",sent_emp_id);
            itemset1.setValue("EMP_NO",empNo);      
            itemset1.setValue("NAME",empName);      
            itemset1.setValue("PLAN_LINE_NO",sent_planLineNo);
	    //Bug 85045, Start
            itemset1.setValue("CAT_CONTRACT",sent_site); 
	    //Bug 85045, End
            itemset1.setValue("CATALOG_NO",sent_salesPartNo); 
            itemset1.setValue("PRICE_LIST_NO",sent_priceListNo);

            if (!mgr.isEmpty(sent_salesPrice))
               itemset1.setNumberValue("LIST_PRICE",toDouble(sent_salesPrice));

            if (!mgr.isEmpty(sent_discount))
               itemset1.setNumberValue("DISCOUNT",toDouble(sent_discount));

            if (salesSec &&  ! mgr.isEmpty(sent_salesPartNo))
            {
               itemset1.setNumberValue("SALESPARTCOST",trans.getNumberValue("PARTCOST/DATA/COST1"));
               itemset1.setValue("SALESPARTCATALOGDESC",trans.getValue("DEFCATDESC/DATA/SALESPARTCATALOGDESC"));
            }
         }
      }
      else if (sentNoEmployee)
      {
         itemset1.goTo(sentrowNo);
         itemlay1.setLayoutMode(itemlay1.EDIT_LAYOUT);

         if (!recordCancel)
         {
	    //Bug 85045, Start
            itemset1.setValue("CAT_CONTRACT",sent_site); 
	    //Bug 85045, End
            itemset1.setValue("CATALOG_NO",sent_salesPartNo); 
         }
      }
   }


   public void okFind()
   {
      ASPManager mgr = getASPManager();
      String  curr_row_exists = "FALSE";
      ASPBuffer retBuffer;

      ASPQuery q = trans.addQuery(headblk);
      q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
      q.setOrderByClause("WO_NO");
      q.includeMeta("ALL");

      if (mgr.dataTransfered())
      {
         retBuffer =  mgr.getTransferedData();
         if (retBuffer.itemExists("WO_NO"))
         {
            String ret_wo_no = retBuffer.getValue("WO_NO");
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

      mgr.querySubmit(trans,headblk);

      eval(headset.syncItemSets());

      if ("TRUE".equals(curr_row_exists))
      {
         headset.goTo(currrow);
         if ("1".equals(lout))
            headlay.setLayoutMode(headlay.MULTIROW_LAYOUT);
      }

      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMNODATA: No data found."));
         headset.clear();
         headlay.setLayoutMode(headlay.MULTIROW_LAYOUT);
      }

      setStringLables();

      if (headset.countRows() == 1)
      {
         okFindITEM1();
         okFindITEM3();
         okFindITEM4();
         okFindITEM6();
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         if ("1".equals(headset.getRow().getValue("EXCEPTION_EXISTS")))
            bException = true;
         else
            bException = false;
      }

      qrystr = mgr.createSearchURL(headblk);

      creFromPlan = "false";
      connReconToPlan = "false";
   }


   public void countFind()
   {
      ASPManager mgr = getASPManager();

      ASPQuery q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");

      mgr.submit(trans);

      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }


   public void newRow()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("HEAD","ACTIVE_SEPARATE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);
      ASPBuffer data = trans.getBuffer("HEAD/DATA");
      data.setValue("FAULT_REP_FLAG","0");

      headset.addRow(data);
   }

//----------------------- BLOCK 1 ---------------------------------------------

   public void okFindITEM1()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      headrowno = headset.getCurrentRowNo();

      ASPQuery q = trans.addQuery(itemblk1);
      q.addWhereCondition("WO_NO = ?");
      q.addWhereCondition("WORK_ORDER_COST_TYPE =  Work_Order_Cost_Type_API.Get_Client_Value(0) AND WORK_ORDER_ACCOUNT_TYPE =  Work_Order_Account_Type_API.Get_Client_Value(0) ");
      q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

      q.includeMeta("ALL");

      mgr.querySubmit(trans,itemblk1);

      if (mgr.commandBarActivated())
      {
         if (itemset1.countRows() == 0 && "ITEM1.OkFind".equals(mgr.readValue("__COMMAND")))
         {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMNODATA: No data found."));
            itemset1.clear();
         }
      }
      if (itemset1.countRows()>0)
         setSalesPartCost();

      headset.goTo(headrowno);
   }


   public void newRowITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPQuery q = trans.addQuery(headblk);


      String currCode = null;
      String cataNo = null;
      String cataDesc = null;   

      trans.clear();

      ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer();
      secBuff.addSecurityQuery("Customer_Agreement_API","Get_Currency_Code");
      secBuff.addSecurityQuery("Cust_Ord_Customer_API","Get_Currency_Code");
      secBuff.addSecurityQuery("SALES_PART");

      secBuff = mgr.perform(secBuff);

      cmd = trans.addCustomFunction("ORGCO", "Active_Work_Order_API.Get_Org_Code", "DEPART" );
      cmd.addParameter("ITEM1_WO_NO",headset.getValue("WO_NO"));

      cmd = trans.addCustomFunction("CON", "Active_Work_Order_API.Get_Contract", "CONTRACT_VAR" );
      cmd.addParameter("ITEM1_CONTRACT",headset.getValue("WO_NO"));

      if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
      {
         cmd = trans.addCustomFunction("CATANO","Organization_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
         cmd.addReference("CONTRACT_VAR","CON/DATA");
         cmd.addReference("DEPART","ORGCO/DATA");

         cmd = trans.addCustomFunction("CATADESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
         cmd.addReference("CONTRACT_VAR","CON/DATA");
         cmd.addReference("CATALOG_NO","CATANO/DATA");

         cmd = trans.addCustomFunction("CST", "Work_Order_Planning_Util_Api.Get_Cost", "SALESPARTCOST" );
         cmd.addReference("DEPART","ORGCO/DATA");
         cmd.addParameter("DUMMY","");
         cmd.addReference("CONTRACT_VAR","CON/DATA");
         cmd.addReference("CATALOG_NO","CATANO/DATA");
         cmd.addReference("CONTRACT_VAR","CON/DATA");
         cmd.addParameter("DUMMY","");
         cmd.addParameter("DUMMY","TRUE");
      }
      else
      {
         cmd = trans.addCustomFunction("CST", "Work_Order_Planning_Util_Api.Get_Cost", "SALESPARTCOST" );
         cmd.addReference("DEPART","ORGCO/DATA");
         cmd.addParameter("DUMMY","");
         cmd.addParameter("DUMMY","");
         cmd.addParameter("DUMMY","");
         cmd.addReference("CONTRACT_VAR","CON/DATA");
         cmd.addParameter("DUMMY","");
         cmd.addParameter("DUMMY","TRUE");
      }

      cmd = trans.addCustomFunction("SP21","Work_Order_Cost_Type_Api.Get_Client_Value(0)","CLIENTVAL3");
      cmd = trans.addCustomFunction("SP22","Work_Order_Account_Type_API.Get_Client_Value(0)","CLIENTVAL4");

      if (secBuff.getSecurityInfo().itemExists("Customer_Agreement_API.Get_Currency_Code") && secBuff.getSecurityInfo().itemExists("Cust_Ord_Customer_API.Get_Currency_Code"))
      {
         // SQLInjection_Safe AMNILK 20070712

         String sqlCurrCode = "SELECT nvl(Customer_Agreement_API.Get_Currency_Code(Active_Work_Order_API.Get_Agreement_Id(?)),Cust_Ord_Customer_API.Get_Currency_Code(?)) CURRCODE FROM DUAL";
         q = trans.addQuery("GETCURRCODE",sqlCurrCode);
         q.addParameter("WO_NO" ,headset.getValue("WO_NO"));
         q.addParameter("CUSTOMER_NO" ,headset.getValue("CUSTOMER_NO"));
      }

      cmd = trans.addEmptyCommand("ITEM1","WORK_ORDER_CODING_API.New__",itemblk1);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);

      String strOrgCode = trans.getValue("ORGCO/DATA/DEPART");
      String strSite = trans.getValue("CON/DATA/CONTRACT_VAR");
      String strWorkOrdCost = trans.getValue("SP21/DATA/CLIENTVAL3");
      String strWorkAccount = trans.getValue("SP22/DATA/CLIENTVAL4");
      String strComment= strOrgCode ;
      double cost = trans.getNumberValue("CST/DATA/SALESPARTCOST");

      if (secBuff.getSecurityInfo().itemExists("Customer_Agreement_API.Get_Currency_Code") && secBuff.getSecurityInfo().itemExists("Cust_Ord_Customer_API.Get_Currency_Code"))
         currCode = trans.getValue("GETCURRCODE/DATA/CURRCODE");
      else
         currCode    = null;

      if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
      {
         cataNo = trans.getValue("CATANO/DATA/CATALOG_NO");
         cataDesc = trans.getValue("CATADESC/DATA/SALESPARTCATALOGDESC");
      }


      ASPBuffer data = trans.getBuffer("ITEM1/DATA");
      data.setFieldItem("ITEM1_WO_NO",headset.getValue("WO_NO"));
      data.setFieldItem("ITEM1_CONTRACT",strSite );
      data.setFieldItem("ITEM1_COMPANY",headset.getValue("COMPANY"));
      data.setFieldItem("ITEM1_ORG_CODE",strOrgCode );
      data.setFieldItem("WORK_ORDER_COST_TYPE",strWorkOrdCost );
      data.setFieldItem("WORK_ORDER_ACCOUNT_TYPE",strWorkAccount );
      data.setFieldItem("CMNT",strComment);
      data.setFieldItem("CURRENCY_CODE",currCode);
      data.setFieldItem("CATALOG_NO",cataNo);
      data.setFieldItem("SALESPARTCATALOGDESC",cataDesc);
      data.setNumberValue("SALESPARTCOST",cost);
      data.setFieldItem("CAT_CONTRACT",headset.getValue("CONTRACT"));

      itemset1.addRow(data);
   }


   public void countFindITEM1()
   {
      ASPManager mgr = getASPManager();

      headrowno = headset.getCurrentRowNo();
      ASPQuery q = trans.addQuery(itemblk1);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("WO_NO = ?");
      q.addWhereCondition("WORK_ORDER_COST_TYPE =  Work_Order_Cost_Type_API.Get_Client_Value(0) AND WORK_ORDER_ACCOUNT_TYPE =  Work_Order_Account_Type_API.Get_Client_Value(0) ");
      q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

      mgr.submit(trans);

      itemlay1.setCountValue(toInt(itemset1.getRow().getValue("N")));
      itemset1.clear();

      headrowno = headset.getCurrentRowNo();
   }


   public void saveReturnItem1()
   {
      ASPManager mgr = getASPManager();

      int currHead = headset.getCurrentRowNo();
      int currrowItem1 = itemset1.getCurrentRowNo();

      itemset1.changeRow();
      
      //Bug id 81023, start
      trans.clear();
      cmd = trans.addCustomCommand("HRAUTHCONFCHECK", "Work_Order_Coding_API.Check_T_Hr_Auth_Confirmation");
      cmd.addInParameter("COMPANY", itemset1.getRow().getValue("COMPANY")); 
      cmd.addInParameter("EMP_NO", itemset1.getRow().getValue("EMP_NO")); 
      cmd.addInParameter("CRE_DATE", itemset1.getRow().getFieldValue("CRE_DATE")); 
      cmd.addInParameter("CATALOG_NO", "T"); 
      cmd.addInParameter("CONTRACT", itemset1.getRow().getValue("CONTRACT"));
      //Bug id 81023, end

      mgr.submit(trans);

      headset.goTo(currHead);
      okFindITEM4();
      itemset1.goTo(currrowItem1);
   }
   
   //Bug id 81023, start
   public void saveNewItem1()
   {
      ASPManager mgr = getASPManager();

      int currHead = headset.getCurrentRowNo();
      int currrowItem1 = itemset1.getCurrentRowNo();

      itemset1.changeRow();     
      
      trans.clear();
      cmd = trans.addCustomCommand("HRAUTHCONFCHECK", "Work_Order_Coding_API.Check_T_Hr_Auth_Confirmation");
      cmd.addInParameter("COMPANY", itemset1.getRow().getValue("COMPANY")); 
      cmd.addInParameter("EMP_NO", itemset1.getRow().getValue("EMP_NO")); 
      cmd.addInParameter("CRE_DATE", itemset1.getRow().getFieldValue("CRE_DATE")); 
      cmd.addInParameter("CATALOG_NO", "T"); 
      cmd.addInParameter("CONTRACT", itemset1.getRow().getValue("CONTRACT"));
      
      mgr.submit(trans);
      trans.clear();

      headset.goTo(currHead);
      newRowITEM1();
   }
   //Bug id 81023, end

   public void saveReturnItem6()
   {
      ASPManager mgr = getASPManager();

      int currHead = headset.getCurrentRowNo();
      int currrowItem6 = itemset6.getCurrentRowNo();
      itemset6.changeRow();
      mgr.submit(trans);

      headset.goTo(currHead);
      //Bug 70147, start
      headset.refreshRow();
      //Bug 70147, end

      okFindITEM3();
      okFindITEM4();

      itemset6.goTo(currrowItem6);
   }

   //Bug 89703, Start
   public void deleteRowITEM6()
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

      if (itemlay6.isMultirowLayout())
         itemset6.goTo(itemset6.getRowSelected());
      itemset6.selectRow();

      int currrow = itemset6.getCurrentRowNo();
      ctx.setGlobal("CURRROWGLOBAL", String.valueOf(currrow));

      trans.clear();

      cmd = trans.addCustomFunction("GETJOBEXIST", "Work_Order_Job_API.Check_Exist", "N_JOB_EXIST");
      cmd.addParameter("ITEM6_WO_NO",itemset6.getRow().getValue("WO_NO"));
      cmd.addParameter("JOB_ID",itemset6.getRow().getValue("JOB_ID"));

      cmd = trans.addCustomCommand("GETDEFVALS", "Work_Order_Job_API.Check_Job_Connection");
      cmd.addParameter("N_ROLE_EXIST","0");
      cmd.addParameter("N_MAT_EXIST","0");
      cmd.addParameter("N_TOOL_EXIST","0");
      cmd.addParameter("N_PLANNING_EXIST","0");
      cmd.addParameter("N_DOC_EXIST","0");
      cmd.addParameter("S_STD_JOB_ID");
      cmd.addParameter("S_STD_JOB_CONTRACT");
      cmd.addParameter("S_STD_JOB_REVISION");
      cmd.addParameter("ITEM6_WO_NO",itemset6.getRow().getValue("WO_NO"));
      cmd.addParameter("JOB_ID",itemset6.getRow().getValue("JOB_ID"));

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
            ctx.setCookie( "PageID_Has_Planning", "TRUE" );
         }
      }
      if (!"TRUE".equals(hasPlanning))
         deleteRow6("NO");
   }

   public void deleteRow6(String removeConn)
   {
      ASPManager mgr = getASPManager();

      int currHead = Integer.parseInt(ctx.findGlobal("HEADROWGLOBAL",""));
      headset.goTo(currHead);   
      int curr_row = Integer.parseInt(ctx.findGlobal("CURRROWGLOBAL",""));
      itemset6.goTo(curr_row);   

      itemset6.selectRow();

      trans.clear();

      if ("YES".equals(removeConn))
      {
         //Disconnect std job
         cmd = trans.addCustomCommand("REMOVECONN","Work_Order_Job_API.Disconnect_Std_Job");
         cmd.addParameter("ITEM6_WO_NO",itemset6.getRow().getValue("WO_NO"));
         cmd.addParameter("JOB_ID",itemset6.getRow().getValue("JOB_ID"));
         cmd.addParameter("STD_JOB_ID",itemset6.getRow().getValue("STD_JOB_ID"));
         cmd.addParameter("STD_JOB_CONTRACT",itemset6.getRow().getValue("STD_JOB_CONTRACT"));
         cmd.addParameter("STD_JOB_REVISION",itemset6.getRow().getValue("STD_JOB_REVISION")); 
         cmd.addParameter("ITEM6_QTY",itemset6.getRow().getValue("QTY")); 
      }

      cmd = trans.addCustomCommand("REMOVEJOBLINE","Work_Order_Job_API.Remove_Job_Line");
      cmd.addParameter("ITEM6_WO_NO",itemset6.getRow().getValue("WO_NO"));
      cmd.addParameter("JOB_ID",itemset6.getRow().getValue("JOB_ID")); 

      trans = mgr.perform(trans);
      trans.clear();
      clearItem4();
      okFindITEM6();
      okFindITEM4();
   }
   //Bug 89703, End

   public void deleteITEM1()
   {
      ASPManager mgr = getASPManager();

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
      trans.clear();
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


//----------------------------------BLOCK 3 ---------------------

   public void okFindITEM3()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      headrowno = headset.getCurrentRowNo();
      ASPQuery q = trans.addQuery(itemblk3);
      q.addWhereCondition("WO_NO = ? AND CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT)");
      q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
      q.includeMeta("ALL");

      mgr.querySubmit(trans,itemblk3);

      if (comBarAct)
      {
         if (itemset3.countRows() == 0 && "ITEM3.OkFind".equals(mgr.readValue("__COMMAND")))
         {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMNODATA: No data found."));
            itemset3.clear();
         }
      }
      headset.goTo(headrowno);

      if (itemset3.countRows() > 0)
         okFindITEM5();
   }


   public void countFindITEM3()
   {
      ASPManager mgr = getASPManager();

      ASPQuery q = trans.addQuery(itemblk3);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("WO_NO = ?");
      q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
      mgr.submit(trans);
      itemlay3.setCountValue(toInt(itemset3.getRow().getValue("N")));
      itemset3.clear();
   }

   public void newRowITEM3()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("ITEM3","MAINT_MATERIAL_REQUISITION_API.New__",itemblk3);
      cmd.setParameter("ITEM3_WO_NO",headset.getRow().getValue("WO_NO"));
      cmd.setOption("ACTION","PREPARE");

      cmd = trans.addCustomFunction("PLNDATE","Active_Work_Order_API.Get_Plan_S_Date","DUE_DATE");
      cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

      cmd = trans.addCustomFunction("PREACCOID","Active_Work_Order_API.Get_Pre_Accounting_Id","NPREACCOUNTINGID");
      cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

      cmd = trans.addCustomFunction("CONT","WORK_ORDER_API.Get_Contract","ITEM3_CONTRACT");
      cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

      cmd = trans.addCustomFunction("COMP","Site_API.Get_Company","ITEM3_COMPANY");
      cmd.addReference("ITEM3_CONTRACT","CONT/DATA");

      cmd = trans.addCustomFunction("MCHCOD","WORK_ORDER_API.Get_Mch_Code","MCHCODE");
      cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

      cmd = trans.addCustomFunction("MCHDESC","Maintenance_Object_API.Get_Mch_Name","ITEM3DESCRIPTION");
      cmd.addReference("ITEM3_CONTRACT","CONT/DATA");
      cmd.addReference("MCHCODE","MCHCOD/DATA");

      cmd = trans.addQuery("SYSTDATE","SELECT SYSDATE SYS_DATE FROM DUAL");

      trans = mgr.perform(trans);

      String dateRequired = trans.getBuffer("PLNDATE/DATA").getFieldValue("DUE_DATE");
      String nPreAccoId = trans.getBuffer("PREACCOID/DATA").getFieldValue("NPREACCOUNTINGID");
      String item3Cont = trans.getBuffer("CONT/DATA").getFieldValue("ITEM3_CONTRACT");
      String item3Company = trans.getBuffer("COMP/DATA").getFieldValue("ITEM3_COMPANY");
      String mchCode = trans.getValue("MCHCOD/DATA/MCHCODE");
      String mchDesc = trans.getValue("MCHDESC/DATA/ITEM3DESCRIPTION");
      String sysDate = trans.getBuffer("SYSTDATE/DATA").getFieldValue("SYS_DATE");

      ASPBuffer data = trans.getBuffer("ITEM3/DATA");

      if (!mgr.isEmpty(dateRequired))
         data.setFieldItem("DUE_DATE",dateRequired);
      else
         data.setFieldItem("DUE_DATE",sysDate);

      data.setFieldItem("NPREACCOUNTINGID",nPreAccoId);
      data.setFieldItem("ITEM3_CONTRACT",item3Cont);  
      data.setFieldItem("ITEM3_COMPANY",item3Company); 
      data.setFieldItem("MCHCODE",mchCode);
      data.setFieldItem("ITEM3DESCRIPTION",mchDesc);
      data.setFieldItem("ITEM3_ACTIVITY_SEQ",headset.getRow().getValue("ACTIVITY_SEQ"));

      itemset3.addRow(data);
   }

//------------------------ BLOCK 4 --------------------------

   public void okFindITEM4()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      ASPQuery q = trans.addEmptyQuery(itemblk4);
      q.addWhereCondition("WO_NO = ?");
      q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.querySubmit(trans,itemblk4);
      trans.clear();
      fillRows(headrowno);
      headset.goTo(headrowno);
   }


   public void clearItem4()
   {
      if (itemset4.countRows() > 5)
      {
         itemset4.last();
         itemset4.clearRow();
         itemset4.first();
      }
   }


   public void editItem4()
   {
      ASPManager mgr = getASPManager();

      if (itemlay4.isMultirowLayout())
         curentrow = itemset4.getRowSelected();
      else
         curentrow = item4CurrRow;

      if (curentrow == 5)
         mgr.showAlert("PCMWACTIVESEPERATEREPORTINWORKORDERSMCANNOT: Cannot perform on selected line");
      else
      {
         item4CurrRow = curentrow;

         if (itemset4.countRows() == 5)
            sumColumns();

         itemset4.goTo(curentrow);
         itemlay4.setLayoutMode(itemlay4.EDIT_LAYOUT);
      }
   }


   public void viewDetailsITEM4()
   {
      curentrow = itemset4.getRowSelected();
      item4CurrRow = curentrow;  
      sumColumns();
      itemset4.storeSelections();
      itemset4.goTo(curentrow);
      itemlay4.setLayoutMode(itemlay4.SINGLE_LAYOUT);
   }


   public void saveReturnITEM4()
   {
      double budgCost;
      double budgRev;

      ASPManager mgr = getASPManager();

      headCurrrow = headset.getCurrentRowNo();
      trans.clear();

      itemset4.goTo(curentrow);

      woNo = mgr.readValue("ITEM4_WO_NO","");
      workOrderCostType = mgr.readValue("ITEM4_WORK_ORDER_COST_TYPE","");
      budgCost = mgr.readNumberValue("BUDGET_COST");
      budgRev = mgr.readNumberValue("BUDGET_REVENUE");

      ASPBuffer buff = itemset4.getRow();

      buff.setValue("WO_NO",woNo);
      buff.setValue("ITEM4_WORK_ORDER_COST_TYPE",workOrderCostType);
      buff.setNumberValue("BUDGET_COST",budgCost);
      buff.setNumberValue("BUDGET_REVENUE",budgRev);

      itemset4.setRow(buff);
      mgr.submit(trans);
      headset.goTo(headCurrrow);
      okFindITEM4();
      sumColumns();
      itemlay4.setLayoutMode(itemlay4.getHistoryMode());

      if (itemlay4.isSingleLayout())
         itemset4.goTo(curentrow);

      headset.goTo(headCurrrow);
   }

   public void forwardITEM4()
   {
      if (item4CurrRow<5)
         item4CurrRow++;
   }


   public void backwardITEM4()
   {
      if (item4CurrRow>0)
         item4CurrRow--;
   }

   public void okFindITEM5()
   {
      ASPManager mgr = getASPManager();

      //Bug 72202, Start, Added check on row count
      if (itemset3.countRows() > 0)
      {
         trans.clear();
         headsetRowNo = headset.getCurrentRowNo();
         item3rowno = itemset3.getCurrentRowNo();

         ASPQuery q;

         /*if (emptyQry)
             q = trans.addEmptyQuery(itemblk);
         else
             q = trans.addQuery(itemblk);*/

         if ( ("ITEM3.Forward".equals(mgr.readValue("__COMMAND"))) || ("ITEM3.Backward".equals(mgr.readValue("__COMMAND"))) ||
              ("ITEM3.First".equals(mgr.readValue("__COMMAND")))   || ("ITEM3.Last".equals(mgr.readValue("__COMMAND")))  )
            q = trans.addEmptyQuery(itemblk);
         else
            q = trans.addQuery(itemblk);

         q.addWhereCondition("WO_NO = ? AND MAINT_MATERIAL_ORDER_NO = ?");
         q.addParameter("ITEM3_WO_NO",itemset3.getRow().getFieldValue("ITEM3_WO_NO"));
         q.addParameter("MAINT_MATERIAL_ORDER_NO",itemset3.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));
         q.includeMeta("ALL");

         mgr.querySubmit(trans,itemblk);
         headset.goTo(headsetRowNo);
         itemset3.goTo(item3rowno);

         if (itemset.countRows() > 0)
            setValuesInMaterials();
      }
      //Bug 72202, End
   }

   public void countFindITEM5()
   {
      ASPManager mgr = getASPManager();

      ASPQuery q = trans.addQuery(itemblk);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("WO_NO = ? AND MAINT_MATERIAL_ORDER_NO = ?");
      q.addParameter("ITEM3_WO_NO",itemset3.getRow().getFieldValue("ITEM3_WO_NO"));
      q.addParameter("MAINT_MATERIAL_ORDER_NO",itemset3.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));
      mgr.submit(trans);
      itemlay.setCountValue(toInt(itemset.getRow().getValue("N")));
      itemset.clear();
   }

   public void newRowITEM5()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("ITEM5","MAINT_MATERIAL_REQ_LINE_API.New__",itemblk);
      cmd.setParameter("SPARE_CONTRACT",itemset3.getRow().getFieldValue("ITEM3_CONTRACT"));
      cmd.setParameter("CATALOG_CONTRACT",headset.getRow().getValue("CONTRACT"));
      cmd.setParameter("ITEM_WO_NO",itemset3.getRow().getFieldValue("ITEM3_WO_NO"));
      cmd.setParameter("ITEM5_MAINT_MATERIAL_ORDER_NO",itemset3.getRow().getFieldValue("MAINT_MATERIAL_ORDER_NO"));
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      ASPBuffer data = trans.getBuffer("ITEM5/DATA");

      data.setFieldItem("SPARE_CONTRACT",itemset3.getRow().getFieldValue("ITEM3_CONTRACT"));
      data.setFieldItem("DATE_REQUIRED",itemset3.getRow().getFieldValue("DUE_DATE"));
      data.setFieldItem("PART_OWNERSHIP_DB","COMPANY OWNED");
      data.setFieldItem("CATALOG_CONTRACT",headset.getRow().getValue("CONTRACT"));

      itemset.addRow(data);
   }

   public void deleteITEM5()
   {
      ASPManager mgr = getASPManager();

      if (itemlay.isMultirowLayout())
         itemset.goTo(itemset.getRowSelected());

      itemset.setRemoved();

      mgr.submit(trans);

      okFindITEM4();
   }

   public void duplicateITEM5()
   {
      ASPManager mgr = getASPManager();

      if (itemlay.isMultirowLayout())
         itemset.goTo(itemset.getRowSelected());

      itemlay.setLayoutMode(itemlay.NEW_LAYOUT);

      cmd = trans.addEmptyCommand("ITEM5","MAINT_MATERIAL_REQ_LINE_API.New__",itemblk);
      cmd.setParameter("ITEM_WO_NO",itemset3.getRow().getFieldValue("ITEM3_WO_NO"));
      cmd.setParameter("ITEM5_MAINT_MATERIAL_ORDER_NO",itemset3.getRow().getFieldValue("MAINT_MATERIAL_ORDER_NO"));
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      ASPBuffer data = trans.getBuffer("ITEM5/DATA");

      data.setFieldItem("PART_NO",itemset.getRow().getValue("PART_NO"));
      data.setFieldItem("SPAREDESCRIPTION",itemset.getRow().getValue("SPAREDESCRIPTION"));
      data.setFieldItem("SPARE_CONTRACT",itemset.getRow().getValue("SPARE_CONTRACT"));
      data.setFieldItem("HASSPARESTRUCTURE",itemset.getRow().getValue("HASSPARESTRUCTURE"));
      data.setFieldItem("DIMQTY",itemset.getRow().getValue("DIMQTY"));
      data.setFieldItem("TYPEDESIGN",itemset.getRow().getValue("TYPEDESIGN"));
      data.setFieldItem("DATE_REQUIRED",itemset.getRow().getFieldValue("DATE_REQUIRED"));
      data.setFieldItem("PLAN_QTY",itemset.getRow().getValue("PLAN_QTY"));
      data.setFieldItem("QTY_SHORT",itemset.getRow().getValue("QTY_SHORT"));
      data.setFieldItem("QTYONHAND",itemset.getRow().getValue("QTYONHAND"));
      data.setFieldItem("ITEM5_CATALOG_NO",itemset.getRow().getFieldValue("ITEM5_CATALOG_NO"));
      data.setFieldItem("ITEM5CATALOGDESC",itemset.getRow().getValue("ITEM5CATALOGDESC"));
      data.setFieldItem("ITEM5_PRICE_LIST_NO",itemset.getRow().getFieldValue("ITEM5_PRICE_LIST_NO"));
      data.setFieldItem("ITEM5_LIST_PRICE",itemset.getRow().getFieldValue("ITEM5_LIST_PRICE"));
      data.setFieldItem("UNITMEAS",itemset.getRow().getValue("UNITMEAS"));
      data.setFieldItem("CATALOG_CONTRACT",itemset.getRow().getValue("CATALOG_CONTRACT"));
      data.setFieldItem("ITEM5_PLAN_LINE_NO",itemset.getRow().getFieldValue("ITEM5_PLAN_LINE_NO"));
      data.setFieldItem("ITEM_DISCOUNT",itemset.getRow().getFieldValue("ITEM_DISCOUNT"));
      data.setFieldItem("ITEM5SALESPRICEAMOUNT",itemset.getRow().getFieldValue("ITEM5SALESPRICEAMOUNT"));
      data.setFieldItem("SCODEA",itemset.getRow().getValue("SCODEA"));
      data.setFieldItem("SCODEB",itemset.getRow().getValue("SCODEB"));
      data.setFieldItem("SCODEC",itemset.getRow().getValue("SCODEC"));
      data.setFieldItem("SCODED",itemset.getRow().getValue("SCODED"));
      data.setFieldItem("SCODEE",itemset.getRow().getValue("SCODEE"));
      data.setFieldItem("SCODEF",itemset.getRow().getValue("SCODEF"));
      data.setFieldItem("SCODEG",itemset.getRow().getValue("SCODEG"));
      data.setFieldItem("SCODEH",itemset.getRow().getValue("SCODEH"));
      data.setFieldItem("SCODEI",itemset.getRow().getValue("SCODEI"));
      data.setFieldItem("SCODEJ",itemset.getRow().getValue("SCODEJ"));

      itemset.addRow(data);                        
   }

   public void saveReturnItem()
   {
      ASPManager mgr = getASPManager();

      currHead = headset.getCurrentRowNo();
      currrowItem3 = itemset3.getCurrentRowNo();
      itemset3.changeRow();
      mgr.submit(trans);
      itemlay3.setLayoutMode(itemlay3.SINGLE_LAYOUT);
      itemset3.refreshAllRows();
      itemset3.goTo(currrowItem3);
      headset.goTo(currHead);
   }

   public void deleteItem()
   {
      ASPManager mgr = getASPManager();

      int currHead = headset.getCurrentRowNo();

      if (itemlay3.isMultirowLayout())
         itemset3.goTo(itemset3.getRowSelected());

      itemset3.unselectRows();
      itemset3.selectRow();

      itemset3.setRemoved();

      mgr.submit(trans);

      if (itemset3.countRows()>0)
         itemset3.refreshAllRows();
      headset.goTo(currHead);
   }

   public void setValuesInMaterials()
   {
      String spareCont;
      String partNo;
      String cataNo;
      String cataCont;
      String cusNo;
      String agreeId;
      String priceListNo;
      String planLineNo;
      double nPlanQty;
      double nCost;
      double nCostAmount;
      double nDiscount;
      double listPrice;
      double planQty;
      double nSaleUnitPrice;
      double nSalesPriceAmount;
      String serialNo;
      String configId;
      String conditionCode;

      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomFunction("OBJLOAN","MAINT_MATERIAL_REQ_LINE_API.Is_Obj_Supp_Loaned","OBJ_LOAN");
      cmd.addParameter("WO_NO",itemset.getRow().getFieldValue("ITEM_WO_NO"));   

      trans = mgr.perform(trans);

      String sObjLoan = trans.getValue("OBJLOAN/DATA/OBJ_LOAN");

      trans.clear();

      int n = itemset.countRows();

      if (n > 0)
      {
         itemset.first();

         for (int i=0; i<=n; ++i)
         {
            spareCont = itemset.getRow().getValue("SPARE_CONTRACT");
            partNo =itemset.getRow().getFieldValue("PART_NO");
            cataNo = itemset.getRow().getFieldValue("ITEM5_CATALOG_NO");

            serialNo = itemset.getRow().getFieldValue("SERIAL_NO");
            configId = itemset.getRow().getFieldValue("CONFIGURATION_ID");
            conditionCode = itemset.getRow().getFieldValue("CONDITION_CODE");

            nPlanQty = itemset.getRow().getNumberValue("PLAN_QTY");
            if (isNaN(nPlanQty))
               nPlanQty = 0;

            cataCont = itemset.getRow().getFieldValue("CATALOG_CONTRACT");
            cusNo = headset.getRow().getFieldValue("CUSTOMER_NO");
            agreeId = headset.getRow().getFieldValue("AGREEMENT_ID");
            priceListNo = itemset.getRow().getFieldValue("ITEM5_PRICE_LIST_NO");
            planLineNo = itemset.getRow().getFieldValue("ITEM5_PLAN_LINE_NO");
            String owner = itemset.getRow().getFieldValue("OWNER");
            String sOwnershipDb = itemset.getRow().getFieldValue("PART_OWNERSHIP_DB");

            if ("CUSTOMER OWNED".equals(sOwnershipDb))
            {
               cmd = trans.addCustomFunction("GETOWNERNAME"+i, "CUSTOMER_INFO_API.Get_Name", "OWNER_NAME");
               cmd.addParameter("OWNER",owner);
            }
            if ("SUPPLIER LOANED".equals(sOwnershipDb))
            {
               cmd = trans.addCustomFunction("GETOWNERNAME1"+i, "Supplier_API.Get_Vendor_Name", "OWNER_NAME");
               cmd.addParameter("OWNER",owner);
            }

            /*cmd = trans.addCustomFunction("GETCOST"+i,"Inventory_Part_API.Get_Inventory_Value_By_Method","ITEM_COST");
            cmd.addParameter("SPARE_CONTRACT",spareCont);
            cmd.addParameter("PART_NO",partNo); */

            cmd = trans.addCustomCommand("GETCOST"+i,"Active_Separate_API.Get_Inventory_Value");
            cmd.addParameter("ITEM_COST");
            cmd.addParameter("SPARE_CONTRACT",spareCont);
            cmd.addParameter("PART_NO",partNo);
            cmd.addParameter("SERIAL_NO",serialNo);
            cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
            cmd.addParameter("CONDITION_CODE",conditionCode);

            double getInvVal = trans.getNumberValue("GETCOST/DATA/ITEM_COST");

            if ((!mgr.isEmpty(cataNo)) && (nPlanQty != 0))
            {
               cmd = trans.addCustomCommand("PRICEINFO"+i,"Work_Order_Coding_API.Get_Price_Info");
               cmd.addParameter("ITEM5_BASE_PRICE","0");
               cmd.addParameter("ITEM5_SALE_PRICE","0");
               cmd.addParameter("ITEM_DISCOUNT","0");
               cmd.addParameter("ITEM_CURRENCY_RATE","0");
               cmd.addParameter("CATALOG_CONTRACT",cataCont);
               cmd.addParameter("ITEM5_CATALOG_NO",cataNo);
               cmd.addParameter("CUSTOMER_NO",cusNo);
               cmd.addParameter("AGREEMENT_ID",agreeId);
               cmd.addParameter("ITEM5_PRICE_LIST_NO",priceListNo);
               cmd.addParameter("PLAN_QTY",mgr.formatNumber("PLAN_QTY",nPlanQty));

               cmd = trans.addCustomFunction("LISTPRICE"+i,"WORK_ORDER_PLANNING_API.Get_Sales_Price","ITEM5_LIST_PRICE");
               cmd.addParameter("ITEM_WO_NO",itemset.getRow().getFieldValue("ITEM_WO_NO"));
               cmd.addParameter("ITEM5_PLAN_LINE_NO",planLineNo);
            }
            itemset.next();
         }

         trans = mgr.validate(trans);

         itemset.first();

         for (int i=0; i<n; ++i)
         {
            nCost = 0;
            String  ownerName = "";

            ASPBuffer row = itemset.getRow();

            if ("CUSTOMER OWNED".equals(itemset.getRow().getFieldValue("PART_OWNERSHIP_DB")))
            {
               ownerName= trans.getValue("GETOWNERNAME"+i+"/DATA/OWNER_NAME");
            }
            if ("SUPPLIER LOANED".equals(itemset.getRow().getFieldValue("PART_OWNERSHIP_DB")))
            {
               ownerName= trans.getValue("GETOWNERNAME1"+i+"/DATA/OWNER_NAME");
            }
            if (!mgr.isEmpty(itemset.getRow().getFieldValue("PART_NO")))
            {
               nCost= trans.getBuffer("GETCOST"+i+"/DATA").getNumberValue("ITEM_COST");
               if (isNaN(nCost))
                  nCost = 0;
               if ( "SUPPLIER LOANED".equals(itemset.getRow().getValue("PART_OWNERSHIP_DB")) || "TRUE".equals(sObjLoan))
                  nCost = 0;
            }

            nPlanQty = itemset.getRow().getNumberValue("PLAN_QTY");
            if (isNaN(nPlanQty))
               nPlanQty = 0;

            if (nPlanQty != 0)
            {
               double nCostTemp = trans.getBuffer("GETCOST"+i+"/DATA").getNumberValue("ITEM_COST");
               if (isNaN(nCostTemp))
                  nCostTemp = 0;
               if ( "SUPPLIER LOANED".equals(itemset.getRow().getValue("PART_OWNERSHIP_DB")) || "TRUE".equals(sObjLoan))
                  nCostTemp = 0;

               nCostAmount = nCostTemp * nPlanQty;
            }
            else
               nCostAmount = 0;


            priceListNo = itemset.getRow().getFieldValue("ITEM5_PRICE_LIST_NO");
            cataNo = itemset.getRow().getFieldValue("ITEM5_CATALOG_NO");
            nDiscount = itemset.getRow().getNumberValue("DISCOUNT");

            if ((!mgr.isEmpty(cataNo)) && (nPlanQty != 0))
            {
               listPrice = trans.getBuffer("LISTPRICE"+i+"/DATA").getNumberValue("LIST_PRICE");               

               if (mgr.isEmpty(priceListNo))
                  priceListNo = trans.getBuffer("PRICEINFO"+i+"/DATA").getFieldValue("ITEM5_PRICE_LIST_NO");

               if (isNaN(nDiscount))
                  nDiscount = trans.getBuffer("PRICEINFO"+i+"/DATA").getNumberValue("ITEM_DISCOUNT");

               if (isNaN(listPrice))
               {
                  listPrice = trans.getBuffer("PRICEINFO"+i+"/DATA").getNumberValue("ITEM5_BASE_PRICE");

                  planQty = trans.getBuffer("PRICEINFO"+i+"/DATA").getNumberValue("PLAN_QTY");
                  if (isNaN(planQty))
                     planQty = 0;

                  nSaleUnitPrice = trans.getBuffer("PRICEINFO"+i+"/DATA").getNumberValue("ITEM5_SALE_PRICE");
                  if (isNaN(nSaleUnitPrice))
                     nSaleUnitPrice = 0;

                  nSalesPriceAmount  = nSaleUnitPrice * planQty;
               }
               else
               {
                  double nListPriceTemp = trans.getBuffer("LISTPRICE"+i+"/DATA").getNumberValue("LIST_PRICE");
                  if (isNaN(nListPriceTemp))
                     nListPriceTemp = 0;

                  planQty = trans.getBuffer("PRICEINFO"+i+"/DATA").getNumberValue("PLAN_QTY"); 
                  if (isNaN(planQty))
                     planQty = 0;

                  double nDiscountTemp = itemset.getRow().getNumberValue("DISCOUNT");
                  if (isNaN(nDiscountTemp))
                     nDiscountTemp = trans.getBuffer("PRICEINFO"+i+"/DATA").getNumberValue("DISCOUNT");
                  if (isNaN(nDiscountTemp))
                     nDiscountTemp = 0;

                  nSalesPriceAmount = nListPriceTemp * planQty;
                  nSalesPriceAmount = nSalesPriceAmount - (nDiscountTemp / 100 * nSalesPriceAmount);
               }

               //row.setValue("PRICE_LIST_NO",priceListNo);
               //row.setNumberValue("ITEM5_LIST_PRICE",listPrice);
               //row.setNumberValue("ITEM_DISCOUNT",nDiscount);
               row.setNumberValue("ITEM5SALESPRICEAMOUNT",nSalesPriceAmount);
            }
            row.setNumberValue("ITEM_COST",nCost);
            row.setNumberValue("AMOUNTCOST",nCostAmount);
            row.setValue("OWNER_NAME",ownerName);

            itemset.setRow(row);
            itemset.next();
         }
      }
      itemset.first();
   }

   public void okFindITEM6()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      int headrowno = headset.getCurrentRowNo();

      ASPQuery q = trans.addQuery(itemblk6);
      q.addWhereCondition("WO_NO = ? AND STD_JOB_CONTRACT = ?");
      q.addParameter("WO_NO",headset.getValue("WO_NO"));
      q.addParameter("CONTRACT",headset.getValue("CONTRACT"));

      q.includeMeta("ALL");
      mgr.querySubmit(trans, itemblk6);

      headset.goTo(headrowno);
   }

   public void countFindITEM6()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      int headrowno = headset.getCurrentRowNo();

      ASPQuery q = trans.addQuery(itemblk6);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("WO_NO = ? AND STD_JOB_CONTRACT = ?");
      q.addParameter("WO_NO",headset.getValue("WO_NO"));
      q.addParameter("CONTRACT",headset.getValue("CONTRACT"));
      mgr.submit(trans);

      headset.goTo(headrowno);

      itemlay6.setCountValue(toInt(itemset6.getRow().getValue("N")));
      itemset6.clear();
   }

   public void newRowITEM6()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      cmd = trans.addEmptyCommand("ITEM6", "WORK_ORDER_JOB_API.New__", itemblk6);
      cmd.setParameter("ITEM6_WO_NO", headset.getValue("WO_NO"));
      cmd.setOption("ACTION", "PREPARE");

      cmd = trans.addCustomFunction("GETSDATE", "Active_Separate_API.Get_Plan_S_Date", "DATE_FROM");
      cmd.addParameter("ITEM6_WO_NO", headset.getValue("WO_NO"));

      cmd = trans.addCustomFunction("GETFDATE", "Active_Separate_API.Get_Plan_F_Date", "DATE_TO");
      cmd.addParameter("ITEM6_WO_NO", headset.getValue("WO_NO"));

      trans = mgr.perform(trans);

      ASPBuffer data = trans.getBuffer("ITEM6/DATA");

      Date dPlanSDate = trans.getBuffer("GETSDATE/DATA").getFieldDateValue("DATE_FROM");
      Date dPlanFDate = trans.getBuffer("GETFDATE/DATA").getFieldDateValue("DATE_TO");        

      data.setFieldDateItem("DATE_FROM", dPlanSDate);
      data.setFieldDateItem("DATE_TO", dPlanFDate);
      itemset6.addRow(data);
   }

   public void setDisable(int headrowno)
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      headset.goTo(headrowno);

      pm_no = headset.getRow().getValue("PM_NO");

      if (mgr.isEmpty(pm_no))
      {
         mgr.getASPField("NOTE").setReadOnly();
         mgr.getASPField("CB_GENERATE_NOTE").setReadOnly();      
      }
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void clear()
   {
      headset.clear();
      headtbl.clearQueryRow();
      itemset1.clear();
      itemtbl1.clearQueryRow();
      itemset3.clear();
      itemtbl3.clearQueryRow();
   }

//-----------------------------------------------------------------------------
//------------------------  FUNCTION FOR CHECK BOX  ---------------------------
//-----------------------------------------------------------------------------


//-----------------------------------------------------------------------------
//------------------------  FUNCTION FOR CHECK BOX  ---------------------------
//-----------------------------------------------------------------------------

   public void setCheckBoxValue( int headrowno) 
   {
      String cbwarranty_var = null;
      String cbhasdocuments_var = null;
      String cbnote_var = null;
      String cbmobile_var = null;

      ASPManager mgr = getASPManager();

      trans.clear();

      ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer();
      // 107820
      secBuff.addSecurityQuery("DOC_REFERENCE_OBJECT_API","EXIST_OBJ_REFERENCE");

      secBuff = mgr.perform(secBuff);

      headset.goTo(headrowno);

      if (secBuff.getSecurityInfo().itemExists("DOC_REFERENCE_OBJECT_API.EXIST_OBJ_REFERENCE"))
      {

         cmd = trans.addCustomFunction("CBDOCU","DOC_REFERENCE_OBJECT_API.EXIST_OBJ_REFERENCE","CBHASDOCUMENTS");
         cmd.addParameter("LUNAME","ActiveWorkOrder");
         cmd.addParameter("WO_KEY_VALUE",headset.getRow().getValue("WO_KEY_VALUE"));
      }

      cmd = trans.addCustomFunction("CBWARRNT","OBJECT_SUPPLIER_WARRANTY_API.Has_Warranty","CBWARRANTYONOBJECT");
      cmd.addParameter("CONTRACT",headset.getRow().getValue("MCH_CODE_CONTRACT"));
      cmd.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));
      cmd.addParameter("REG_DATE",headset.getRow().getFieldValue("REG_DATE"));

      trans = mgr.perform(trans);

      cbwarranty = trans.getValue("CBWARRNT/DATA/CBWARRANTYONOBJECT");

      if (secBuff.getSecurityInfo().itemExists("DOC_REFERENCE_OBJECT_API.EXIST_OBJ_REFERENCE"))
         cbhasdocuments = trans.getValue("CBDOCU/DATA/CBHASDOCUMENTS");
      cbnote = headset.getRow().getFieldValue("CMB_GENERATE_NOTE");
      clientValue = headset.getRow().getFieldValue("DUMMY_GENERATE_NOTE_YES");

      if ("TRUE".equals(cbwarranty))
         cbwarranty_var = "TRUE";

      if ("TRUE".equals(cbhasdocuments))
         cbhasdocuments_var = "TRUE";

      if (!mgr.isEmpty(clientValue))
      {
         if (clientValue.equals(cbnote))
            cbnote_var = "TRUE";
      }

      ASPBuffer row = headset.getRow();

      row.setValue("CBWARRANTYONOBJECT",cbwarranty_var);
      row.setValue("CB_GENERATE_NOTE",cbnote_var);
      // 107820
      headset.setRow(row); 

   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  FOR HEAD -----------------
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  FOR HEAD -----------------
//-----------------------------------------------------------------------------



   private String createTransferUrl(String url,
                                    ASPBuffer object)
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


   public void none()
   {
      ASPManager mgr = getASPManager();
      mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMNORMBSEL: No RMB method has been selected."));
   }


   public void printWO()
   {
      ASPManager mgr = getASPManager();
      //Web Alignment - enable multirow action
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

      //Bug ID 91376, Start
      for (int i = 0;i < count;i++)
      {
         int nWorNo = new Double(headset.getRow().getFieldValue("WO_NO")).intValue();
      
          attr1 = "REPORT_ID" + (char)31 + "ACTIVE_SEP_WO_PRINT_REP" + (char)30;         
          attr2 = "WO_NO_LIST" + (char)31 + nWorNo + (char)30;              
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
      //Bug ID 91376, End
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

   //Web Alignment - Replacing links with RMBs
   public void prepareWO()
   {
      ASPManager mgr = getASPManager();

      //Web Alignment - enable multirow action
      int count = 0;
      ASPBuffer transferBuffer;
      ASPBuffer rowBuff;

      bOpenNewWindow = true;

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

      ctx.setCookie("PageID_CurrentWindow", "*");
      urlString = createTransferUrl("ActiveSeparate2ServiceManagement.page", transferBuffer);

      newWinHandle = "PrepareWO"; 
      //
   }

   public void sparePartObject()
   {
      ASPManager mgr = getASPManager();

      bOpenNewWindow = true;
      ctx.setCookie("PageID_CurrentWindow", "*");

      int head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);

      int currrow = itemset3.getCurrentRowNo();

      String s_currrow = String.valueOf(currrow);
      ctx.setGlobal("CURRROWGLOBAL",s_currrow);
      ctx.setGlobal("WONOGLOBAL",s_head_curr);

      urlString = "MaintenanceObject2.page?MCH_CODE=" +  mgr.URLEncode(headset.getValue("MCH_CODE")) +
                  "&CONTRACT="+  mgr.URLEncode(headset.getValue("CONTRACT")) +
                  "&WO_NO="+ mgr.URLEncode(headset.getValue("WO_NO")) +
                  "&ORDER_NO=" + mgr.URLEncode(itemset3.getValue("MAINT_MATERIAL_ORDER_NO")) +
                  "&QRYSTR="+ mgr.URLEncode(qrystr)+
                  "&FRMNAME=ReportInWOSM" ;

      newWinHandle = "sparePartObject"; 
   }


   public void updateSparePartObject()
   {
      ASPManager mgr = getASPManager();

      String calling_url = mgr.getURL();
      ctx.setCookie("PageID_CurrentWindow", "*");

      bOpenNewWindow = true;

      if (headset.countRows()>0)
      {
         int curr = headset.getCurrentRowNo();
         ctx.setGlobal("CURRROWGLOBAL",Integer.toString(curr));
      }

      if (itemlay.isMultirowLayout())
         itemset.goTo(itemset.getRowSelected());

      urlString = "UpDateSparePartsObject.page?MCH_CODE=" + mgr.URLEncode(headset.getValue("MCH_CODE")) +
                  "&MCH_CODE_CONTRACT=" + mgr.URLEncode(headset.getValue("MCH_CODE_CONTRACT")) +
                  "&PART_NO=" + mgr.URLEncode(itemset.getValue("PART_NO")) +
                  "&PART_DESC=" + mgr.URLEncode(itemset.getValue("SPAREDESCRIPTION")) +
                  "&SPARE_CONTRACT=" + mgr.URLEncode(itemset.getValue("SPARE_CONTRACT")) +
                  "&FRMNAME=FRMNAME=ReportInWOSM";

      newWinHandle = "updateSpareparts"; 

      itemset.refreshAllRows();

   }


   public void objStructure()
   {
      ASPManager mgr = getASPManager();

      bOpenNewWindow = true;
      ctx.setCookie("PageID_CurrentWindow", "*");

      urlString = "MaintenaceObject3.page?MCH_CODE=" + mgr.URLEncode(headset.getValue("MCH_CODE")) +
                  "&CONTRACT="+  mgr.URLEncode(headset.getValue("CONTRACT")) +
                  "&WO_NO="+ mgr.URLEncode(headset.getValue("WO_NO")) +
                  "&ORDER_NO=" + mgr.URLEncode(itemset3.getValue("MAINT_MATERIAL_ORDER_NO")) +
                  "&QRYSTR="+ mgr.URLEncode(qrystr) +
                  "&FRMNAME=ReportInWOSM" ;

      newWinHandle = "objStruct"; 

   }
   //

   public void requisitions()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL",calling_url);

      trans.clear();

      cmd = trans.addCustomFunction("GETOBJSTATE", "Active_Separate_Api.Get_Obj_State", "OBJSTATE" );
      cmd.addParameter("WO_NO", headset.getRow().getValue("WO_NO"));

      trans = mgr.perform(trans);

      String sObjState  = trans.getBuffer("GETOBJSTATE/DATA").getFieldValue("OBJSTATE");

      //Web Alignment - open tabs implemented as RMBs in a new window
      bOpenNewWindow = true;
      ctx.setCookie("PageID_CurrentWindow", "*");
      urlString = "WorkOrderRequisHeaderRMB.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"))+
                  "&MCH_CODE="+mgr.URLEncode(headset.getRow().getValue("MCH_CODE"))+
                  "&MCH_CODE_DESCRIPTION="+mgr.URLEncode(headset.getRow().getValue("MCH_CODE_DESCRIPTION"))+
                  "&CUSTOMER_NO="+mgr.URLEncode(headset.getRow().getValue("CUSTOMER_NO"))+
                  "&AGREEMENT_ID="+mgr.URLEncode(headset.getRow().getValue("AGREEMENT_ID")) +
                  "&OBJSTATE="+sObjState;
      newWinHandle = "PurchRequisitions"; 
      //
   }


   public void structure()
   {
      ASPManager mgr = getASPManager();

      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL",calling_url);

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      bOpenNewWindow = true;
      ctx.setCookie("PageID_CurrentWindow", "*");
      urlString = "SeparateWorkOrder.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"));
      newWinHandle = "Structure"; 
      //
   }


   public void freeNotes()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL",calling_url);

      //Web Alignment - open tabs implemented as RMBs in a new window
      bOpenNewWindow = true;
      ctx.setCookie("PageID_CurrentWindow", "*");
      urlString = "WorkOrderReportPageOvw2.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"));
      newWinHandle = "freeNotes";
      //
   }


   public void workCenterLoad()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      //Web Alignment - simplify code for RMBs
      bOpenNewWindow = true;
      ctx.setCookie("PageID_CurrentWindow", "*");
      urlString = "ActiveSeparate2WorkCenterLoad.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"));
      newWinHandle = "WCLoad";
      //

   }

   public void returns()
   {
      ASPManager mgr = getASPManager();


      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL",calling_url);

      ASPBuffer buff = mgr.newASPBuffer();
      ASPBuffer row = buff.addBuffer("0");
      row.addItem("WO_NO",headset.getRow().getValue("WO_NO")) ;
      //Web Alignment - open tabs implemented as RMBs in a new window
      bOpenNewWindow = true;
      ctx.setCookie("PageID_CurrentWindow", "*");
      urlString = createTransferUrl("WorkOrderReturns.page", buff);
      newWinHandle = "WOReturns";
      //
   }

   public void toolsFacilities()
   {
      ASPManager mgr = getASPManager();


      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL",calling_url);


      //Web Alignment - open tabs implemented as RMBs in a new window
      bOpenNewWindow = true;
      ctx.setCookie("PageID_CurrentWindow", "*");
      urlString = createTransferUrl("WorkOrderReportInToolsFacilities.page", headset.getSelectedRows("WO_NO"));
      newWinHandle = "ToolsandFacilities";
      //
   }


   public void permits()
   {
      ASPManager mgr = getASPManager();
      ctx.setCookie("PageID_CurrentWindow", "*");

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL",calling_url);

      //Web Alignment - open form in new window
      bOpenNewWindow = true;           
      urlString = "WorkOrderPermit.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"));
      newWinHandle = "Permits";
      //
   }


   public void preposting()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      calling_url=mgr.getURL();
      ctx.setGlobal("CALLING_URL",calling_url);

      headset.selectRows();
      headset.selectRow();


      eval(headblk.generateAssignments());

      String contract = headset.getRow().getValue("CONTRACT");
      String wo_no = headset.getRow().getValue("WO_NO");
      String enabl10 = "0";
      lout = (headlay.isMultirowLayout()?"1":"0");

      if (( !("Closed".equals(mgr.getASPField("OBJSTATE").getValue())) ))
      {
         String enabledArr[] = GetEnabledFields();

         ctx.setCookie("PageID_CurrentWindow", "*");

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


   public void postings()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.store();
      else
      {
         headset.unselectRows();
         headset.selectRow();
      }

      calling_url = mgr.getURL();
      callingurl1 = mgr.getURL();
      ctx.setGlobal("CALLING_URL",calling_url);

      //Web Alignment - simplify code for RMBs
      bOpenNewWindow = true;           
      ctx.setCookie("PageID_CurrentWindow", "*");
      urlString = "WorkOrderCoding1.page?WO_NO="+mgr.URLEncode(headset.getValue("WO_NO"))+"&CUSTOMER_NO1="+mgr.URLEncode(headset.getValue("CUSTOMER_NO"))+"&AGREEMENT_ID1="+mgr.URLEncode(headset.getValue("AGREEMENT_ID"))+"&FORM="+mgr.URLEncode(callingurl1);
      newWinHandle = "Postings";
      //
   }

//----------------------------- get the enabled fields to be  passed to prepost.page-----------------------------------


   public String[] GetEnabledFields()
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
      cmd.addParameter("STR_CODE","T50"); 
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

      headset.setFilterOn();

      return enabledArr;

   }


   public void placeSerial()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      current_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL",current_url);

      //Web Alignment - open RMBs in a new window
      bOpenNewWindow = true;
      ctx.setCookie("PageID_CurrentWindow", "*");
      urlString = createTransferUrl("PartSerialCatalogDlg.page", headset.getSelectedRows("WO_NO,ERR_DESCR"));
      newWinHandle = "placeSerial";
      //
   }


   public void moveSerial()
   {
      if (headlay.isMultirowLayout())
         disableMoveStoInovw();
      else
         moveSerial2();
   }

   public void moveNonSerial()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();


      //Web Alignment - Simplify code for RMBs
      urlString = "MoveNonSerialToInventDlg.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"))+"&ERR_DESCR="+mgr.URLEncode(headset.getRow().getValue("ERR_DESCR"));
      bOpenNewWindow = true;
      ctx.setCookie("PageID_CurrentWindow", "*");
      newWinHandle = "MoveNonSerial";
      //
   }

   public void moveSerial2()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();
      //Web Alignment - simplify code for RMBs
      bOpenNewWindow = true;           
      ctx.setCookie("PageID_CurrentWindow", "*");
      urlString = "ActiveSeparateDlg.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) + 
                  "&ERR_DESCR=" + mgr.URLEncode(headset.getRow().getValue("ERR_DESCR")) + 
                  "&CONTRACT=" + mgr.URLEncode(headset.getRow().getValue("MCH_CODE_CONTRACT")) + 
                  "&MCH_CODE="+mgr.URLEncode(headset.getRow().getValue("MCH_CODE")) + 
                  "&CONNECTION_TYPE="+mgr.URLEncode(headset.getRow().getValue("CONNECTION_TYPE"));
      newWinHandle = "moveSerial2";
   }


   public void createFromPlanTime()
   {
      ASPManager mgr = getASPManager();


      if (itemlay1.isMultirowLayout())
         itemset1.goTo(itemset1.getRowSelected());
      else
         itemset1.selectRow();

      creFromPlan = "true";
      crePlanPath = headset.getRow().getValue("WO_NO");

      //Web Alignment - simplify code for RMBs
      bOpenNewWindow = true;           
      ctx.setCookie("PageID_CurrentWindow", "*");
      urlString = "WorkOrderRolePlanningLov.page?WO_NO="+mgr.URLEncode(crePlanPath)+"&FRMNAME=ReportInWOSM&RMBNAME=CreatePlan";
      newWinHandle = "CreatePlan";
      //
      current_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL",current_url);
   }


   public void connectToPlanTime()
   {
      ASPManager mgr = getASPManager();


      if (itemlay1.isMultirowLayout())
         itemset1.goTo(itemset1.getRowSelected());
      else
         itemset1.selectRow();

      connReconToPlan = "true";
      crePlanPath = headset.getRow().getValue("WO_NO");

      //Web Alignment - simplify code for RMBs
      bOpenNewWindow = true;           
      ctx.setCookie("PageID_CurrentWindow", "*");
      urlString = "WorkOrderRolePlanningLov.page?WO_NO="+mgr.URLEncode(crePlanPath)+"&FRMNAME=ReportInWOSM&RMBNAME=ConnectPlan&EDITROWNUM="+mgr.URLEncode(rowToEdit+"");
      newWinHandle = "ConnectPlan";
      //

      current_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL",current_url);

   }

   public void createFromAllocation()
   {
      ASPManager mgr = getASPManager();

      if (itemlay1.isMultirowLayout())
         itemset1.goTo(itemset1.getRowSelected());
      else
         itemset1.selectRow();

      int head_current   = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);
      ctx.setGlobal("HEADGLOBAL", s_head_curr);

      urlString = "TimeReportFromAllocDlg.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"))+
                  "&FRMNAME=" + mgr.URLEncode("ReportInWOSM") +
                  "&QRYSTR=" + mgr.URLEncode(qrystr);

      bOpenNewWindow = true;
      ctx.setCookie("PageID_CurrentWindow", "*");
      newWinHandle = "createAlloc";
   }

   public void measurements()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      calling_url=mgr.getURL();
      ctx.setGlobal("CALLING_URL",calling_url);

      headset.storeSelections();
      nCount=headset.countSelectedRows();

      ASPBuffer buffer=mgr.newASPBuffer();
      ASPBuffer row=buffer.addBuffer("0");
      row.addItem("WO_NO",headset.getRow().getValue("WO_NO"));
      row.addItem("CONTRACT",headset.getRow().getValue("MCH_CODE_CONTRACT"));
      row.addItem("MCH_CODE",headset.getRow().getValue("MCH_CODE"));

      mgr.transferDataTo("../equipw/RMBMaintenanceObject.page",buffer);
   }


   public void transferToCusOrder()
   {
      ASPManager mgr = getASPManager();

      calling_url=mgr.getURL();
      ctx.setGlobal("CALLING_URL",calling_url);


      int count = 0;
      ASPBuffer transferBuffer;
      ASPBuffer rowBuff;
      boolean check_ = true;

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

      bOpenNewWindow = true;
      ctx.setCookie("PageID_CurrentWindow", "*");
      urlString = createTransferUrl("ActiveWorkOrder4.page", transferBuffer);
      newWinHandle = "transferToCustomerOrder";

   }


   public void disableCusCmd()
   {
      ASPManager mgr = getASPManager();

      if (headset.countRows()>0)
      {
         String wordno = headset.getRow().getValue("WO_NO");
         String custno = headset.getRow().getValue("CUSTOMER_NO");
         String mchCode = headset.getRow().getValue("MCH_CODE");

         if (!(!mgr.isEmpty(wordno) && !mgr.isEmpty(custno)) && (headlay.isSingleLayout()))
         {
            headbar.removeCustomCommand("transferToCusOrder");
         }
      }
   }


   public void coInformation()
   {
      ASPManager mgr = getASPManager();


      if (headlay.isMultirowLayout())
         headset.store();
      else
      {
         headset.unselectRows();
         headset.selectRow();
      }

      calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL",calling_url);

      ASPBuffer buff = mgr.newASPBuffer();
      ASPBuffer row = buff.addBuffer("0");
      row.addItem("WO_NO",headset.getRow().getValue("WO_NO")) ;

      //Web Alignment - open tabs implemented as RMBs in a new window
      bOpenNewWindow = true;
      ctx.setCookie("PageID_CurrentWindow", "*");
      //Bug Id 70012, Start
      urlString = createTransferUrl("ActiveSeparateReportCOInfo.page?&PASS_PLN_S_DATE="+mgr.URLEncode(headset.getRow().getValue("PLAN_S_DATE"))
                                    +"&PASS_WORK_TYPE_ID="+mgr.URLEncode(headset.getRow().getValue("WORK_TYPE_ID"))
                                    +"&PASS_CON_TYPE_DB="+mgr.URLEncode(headset.getRow().getValue("CONNECTION_TYPE_DB")), buff);
      //Bug Id 70012, End

      newWinHandle = "COInformation";
      //
   }


   public void mroObjReceiveO()
   {
      ASPManager mgr = getASPManager();        
      ASPBuffer buff,row;
      boolean enabled;

      if (headlay.isMultirowLayout())
      {
         headset.goTo(headset.getRowSelected());
         if (checkMROObjRO())
            enabled = true;
         else
            enabled = false;
      }
      else
         enabled = true;

      if (enabled)
      {
         trans.clear();

         cmd = trans.addCustomCommand("CREMRORO","ACTIVE_SEPARATE_API.Create_Mro_Ro");
         cmd.addParameter("RECEIVE_ORDER_NO");
         cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

         trans = mgr.perform(trans);

         String roNo = trans.getValue("CREMRORO/DATA/RECEIVE_ORDER_NO");

         if (mgr.isEmpty(roNo))
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERSMRONOTCRE: Receive Order was not Created."));
         else
         {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERSMNEWROCRE: Receive Order " +roNo+ "  has been created."));

            trans.clear();
            cmd = trans.addCustomFunction("GETREQSDATE","ACTIVE_SEPARATE_API.Get_Required_Start_Date","REQUIRED_START_DATE");
            cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

            trans = mgr.perform(trans);

            String reqSdate =  trans.getValue("GETREQSDATE/DATA/REQUIRED_START_DATE");
            if (mgr.isEmpty(reqSdate))
               mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERSMINVREQSD: Required Start is not specified for Work Order " +headset.getValue("WO_NO")+ ". Update corresponding Planned Receipt Date for Receive Order " +roNo+ "."));
         }
      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERSMCANNOTPERMRO: Cannot perform Create MRO Object Receive Order on this record."));        
   }


   public void returnObj()
   {
      ASPManager mgr = getASPManager();        
      ASPBuffer buff,row;
      boolean enabled;

      if (headlay.isMultirowLayout())
      {
         headset.goTo(headset.getRowSelected());
         if (checkReturnObj())
            enabled = true;
         else
            enabled = false;
      }
      else
         enabled = true;

      if (enabled)
      {
         trans.clear();

         cmd = trans.addCustomCommand("RETOBJ","ACTIVE_SEPARATE_API.Return_Object");
         cmd.addParameter("CUST_ORDER_NO");
         cmd.addParameter("CUST_ORDER_LINE_NO");
         cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

         trans = mgr.perform(trans);

         String custOrdNo = trans.getValue("RETOBJ/DATA/CUST_ORDER_NO");
         String custLineNo = trans.getValue("RETOBJ/DATA/CUST_ORDER_LINE_NO");

         if (!mgr.isEmpty(custOrdNo) && !mgr.isEmpty(custLineNo))
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERSMRESONCO: Object was reserved on Customer Order " +custOrdNo+ " Line No" +custLineNo+ "."));
      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERSMCANNOTPERRETOBJ: Cannot perform Return Object on this record."));        
   }


   public void returnNonOpObj()
   {
      ASPManager mgr = getASPManager();        
      ASPBuffer buff,row;
      boolean enabled;

      if (headlay.isMultirowLayout())
      {
         headset.goTo(headset.getRowSelected());
         if (checkReturnNonOpObj())
            enabled = true;
         else
            enabled = false;
      }
      else
         enabled = true;

      if (enabled)
      {
         trans.clear();

         cmd = trans.addCustomCommand("RETOBJ","ACTIVE_SEPARATE_API.Return_Object");
         cmd.addParameter("CUST_ORDER_NO");
         cmd.addParameter("CUST_ORDER_LINE_NO");
         cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

         trans = mgr.perform(trans);

         String custOrdNo = trans.getValue("RETOBJ/DATA/CUST_ORDER_NO");
         String custLineNo = trans.getValue("RETOBJ/DATA/CUST_ORDER_LINE_NO");

         if (!mgr.isEmpty(custOrdNo) && !mgr.isEmpty(custLineNo))
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERSMRESONCO: Object was reserved on Customer Order " +custOrdNo+ " Line No" +custLineNo+ "."));
      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERSMCANNOTPERRETNONOPOBJ: Cannot perform Return Not Operational Object on this record."));        
   }

   public boolean CheckObjInRepairShop()
   {
      ASPManager mgr = getASPManager();
      ASPCommand cmd;
      String objState = new String();
      trans.clear();

      if (headset.countRows()>0)
      {
         cmd = trans.addCustomFunction("GETPARTNO","EQUIPMENT_SERIAL_API.GET_PART_NO","PART_NO");
         cmd.addParameter("CONTRACT",headset.getValue("CONTRACT"));
         cmd.addParameter("MCH_CODE",headset.getValue("MCH_CODE"));

         cmd = trans.addCustomFunction("GETSERIALNO","EQUIPMENT_SERIAL_API.GET_SERIAL_NO","SERIAL_NO");
         cmd.addParameter("CONTRACT",headset.getValue("CONTRACT"));
         cmd.addParameter("MCH_CODE",headset.getValue("MCH_CODE"));

         cmd = trans.addCustomFunction("GETSTATE","Part_Serial_Catalog_Api.Get_Objstate","OBJSTATE");
         cmd.addReference("PART_NO","GETPARTNO/DATA");
         cmd.addReference("SERIAL_NO","GETSERIALNO/DATA");

         trans = mgr.perform(trans);

         objState = trans.getValue("GETSTATE/DATA/OBJSTATE");
      }

      if ("InRepairWorkshop".equals(objState))
         return true;
      else
         return false;  
   }

   public boolean CheckAllMaterialIssued()
   {
      ASPManager mgr = getASPManager();
      ASPCommand cmd;

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
//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  FOR ITEM BLOCK3 MATERIAL
//-----------------------------------------------------------------------------

   public void printAuth()
   {
      ASPManager mgr = getASPManager();
      //Bug ID 91376, Start
      int nWoNo = new Double(headset.getRow().getFieldValue("WO_NO")).intValue();
      //Bug ID 91376, End

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());

      prntblk = getASPBlock("PRINT");
      printset = prntblk.getASPRowSet();

      String attr1 = "REPORT_ID" + (char)31 + "WORK_ORDER_AUTHORIZATION_REP" + (char)30;
      //Bug ID 91376, Start
      String attr2 = "WO_NO_LIST" + (char)31 + nWoNo + (char)30;       
      //Bug ID 91376, End
      String attr3 =  "";
      String attr4 =  "";

      cmd = trans.addCustomCommand( "PRNT","Archive_API.New_Client_Report");
      cmd.addParameter("ATTR0");                       
      cmd.addParameter("ATTR1",attr1);       
      cmd.addParameter("ATTR2",attr2);              
      cmd.addParameter("ATTR3",attr3);      
      cmd.addParameter("ATTR4",attr4);       

      trans = mgr.perform(trans);

      String attr0 = trans.getValue("PRNT/DATA/ATTR0");

      ASPBuffer print = mgr.newASPBuffer();
      ASPBuffer printBuff = print.addBuffer("DATA");
      printBuff.addItem("RESULT_KEY",attr0);

      callPrintDlg(print,true);
   }

   public void performItem( String command) 
   {
      ASPManager mgr = getASPManager();
      //Web Alignment - Enable Multirow Action

      trans.clear();

      int headcurrow = headset.getCurrentRowNo();

      if (itemlay3.isMultirowLayout())
      {
         itemset3.storeSelections();
         itemset3.markSelectedRows(command);
         mgr.submit(trans);

         itemset3.refreshAllRows();
      }
      else
      {
    	  //Bug 93736, Start
    	  itemset3.unselectRows();
          itemset3.markRow(command);
          int currrow = itemset3.getCurrentRowNo();
          mgr.submit(trans);
          itemset3.goTo(currrow);
          itemset3.refreshRow();
    	  //Bug 93736, End
      }   

      headset.goTo(headcurrow);
   }

   public boolean noReserv()
   {

      //Web Alignment - Enable Multirow Action
      ASPManager mgr = getASPManager();

      int count = 0;
      int bufferItemCount = 0;
      ASPBuffer queryResults;

      trans.clear();

      if (itemlay3.isMultirowLayout())
      {
         itemset3.storeSelections();
         itemset3.setFilterOn();

         count = itemset3.countSelectedRows();
      }
      else
      {
         itemset3.unselectRows();
         itemset3.selectRow();

         count = 1;
      }

      for (int i = 0; i < count; i++)
      {
         cmd = trans.addQuery("GET_QTY_FOR_NO_RES_" + i, "select QTY_ASSIGNED from MAINT_MATERIAL_REQ_LINE where MAINT_MATERIAL_ORDER_NO = ?");
         cmd.addParameter("MAINT_MATERIAL_ORDER_NO",itemset3.getValue("MAINT_MATERIAL_ORDER_NO"));

         itemset3.next();
      }

      trans = mgr.perform(trans);

      if (itemlay3.isMultirowLayout())
         itemset3.setFilterOff();

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

      if (itemlay3.isMultirowLayout())
      {
         itemset3.storeSelections();
         itemset3.setFilterOn();

         count = itemset3.countSelectedRows();
      }
      else
      {
         itemset3.unselectRows();
         itemset3.selectRow();

         count = 1;
      }

      for (int i = 0; i < count; i++)
      {
         cmd = trans.addQuery("GET_QTY_FOR_NO_ISSUE_" + i, "select QTY from MAINT_MATERIAL_REQ_LINE where MAINT_MATERIAL_ORDER_NO = ?");
         cmd.addParameter("MAINT_MATERIAL_ORDER_NO",itemset3.getValue("MAINT_MATERIAL_ORDER_NO"));

         itemset3.next();
      }

      trans = mgr.perform(trans);

      if (itemlay3.isMultirowLayout())
         itemset3.setFilterOff();

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
         mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCANNOT: Cannot perform on selected line"));    
   }


   public void release()
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
         mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCANNOT: Cannot perform on selected line"));    
   }

   public void prePostHead()
   {
      String enabledArr[] = new String[10];

      ASPManager mgr = getASPManager();

      calling_url=mgr.getURL();
      ctx.setGlobal("CALLING_URL",calling_url);

      if (itemlay3.isMultirowLayout())
         itemset3.store();
      else
      {
         itemset3.unselectRows();
         itemset3.selectRow();
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

      enabledArr[0] = trans.getValue("ALLOCODEPART/DATA/CODE_A");
      enabledArr[1] = trans.getValue("ALLOCODEPART/DATA/CODE_B");
      enabledArr[2] = trans.getValue("ALLOCODEPART/DATA/CODE_C");
      enabledArr[3] = trans.getValue("ALLOCODEPART/DATA/CODE_D");
      enabledArr[4] = trans.getValue("ALLOCODEPART/DATA/CODE_E");
      enabledArr[5] = trans.getValue("ALLOCODEPART/DATA/CODE_F");
      enabledArr[6] = trans.getValue("ALLOCODEPART/DATA/CODE_G");
      enabledArr[7] = trans.getValue("ALLOCODEPART/DATA/CODE_H");
      enabledArr[8] = trans.getValue("ALLOCODEPART/DATA/CODE_I");
      enabledArr[9] = trans.getValue("ALLOCODEPART/DATA/CODE_J");


      String nPreAccId = itemset3.getRow().getValue("NPREACCOUNTINGID");
      String item3Contract = itemset3.getRow().getFieldValue("ITEM3_CONTRACT");

      prePostBuffer = mgr.newASPBuffer();
      ASPBuffer data = prePostBuffer.addBuffer("dataBuffer");
      data.addItem("CONTRACT",item3Contract);
      data.addItem("PRE_ACCOUNTING_ID",nPreAccId);
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
      data.addItem("ENABL10","0");

      ASPBuffer returnBuffer = prePostBuffer.addBuffer("return_buffer");
      returnBuffer.addItem("WO_NO",headset.getRow().getValue("WO_NO"));

      //Web Alignment - open RMBs in a new window
      bOpenNewWindow = true;
      ctx.setCookie("PageID_CurrentWindow", "*");
      urlString = createTransferUrl("../mpccow/PreAccountingDlg.page", prePostBuffer);
      newWinHandle = "prePostHead";
      //

   }


   public void printPicList()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer print;
      ASPBuffer printBuff;
      String attr1;
      String attr2;
      String attr3;
      String attr4;

      if ( itemlay3.isMultirowLayout() )
         itemset3.goTo(itemset3.getRowSelected());

      String orderNo = itemset3.getRow().getValue("MAINT_MATERIAL_ORDER_NO");
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
            attr2 = "MAINT_MTRL_ORDER_NO" + (char)31 + itemset3.getValue("MAINT_MATERIAL_ORDER_NO") + (char)30;
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

            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERSMNONEW: No new Assigned stock for this Material Order."));
      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERSMCANNOT: Cannot perform on selected line"));
   }

   public void note()
   {
      ASPManager mgr = getASPManager();
      if (itemlay3.isMultirowLayout())
         itemset3.goTo(itemset3.getRowSelected());
      //Web Alignment - simplify code for RMBs
      bOpenNewWindow = true;           
      ctx.setCookie("PageID_CurrentWindow", "*");
      urlString = "EditorDlg.page?SNOTETEXT="+mgr.URLEncode(itemset3.getRow().getValue("SNOTETEXT"))+"&FRMNAME=ReportInWOSM&QRYSTR="+mgr.URLEncode(qrystr);
      newWinHandle = "note";
   }

   public void detchedPartList()
   {
       ASPManager mgr = getASPManager();

       String sPartNo = "";
       calling_url=mgr.getURL();
       ctx.setGlobal("CALLING_URL",calling_url);

       int head_current = headset.getCurrentRowNo();   
       String s_head_curr = String.valueOf(head_current);

       ctx.setGlobal("WONOGLOBAL",s_head_curr);

       ctx.setCookie("PageID_CurrentWindow", "*");

       ASPBuffer buffer = mgr.newASPBuffer();
       ASPBuffer row = buffer.addBuffer("0");      
       //Bug 82543, start
       if (itemset.getRowSelected() != -1) {
           itemset.goTo(itemset.getRowSelected());
           sPartNo  = itemset.getRow().getValue("PART_NO");
       }

       int currrow = itemset3.getCurrentRowNo();
       String s_currrow = String.valueOf(currrow);
       ctx.setGlobal("CURRROWGLOBAL",s_currrow);
       if (!(mgr.isEmpty(sPartNo))) {
           trans.clear();
           cmd = trans.addCustomFunction("SPARESTRUCT","Equipment_Spare_Structure_API.Has_Spare_Structure","HASSPARESTRUCTURE");
           cmd.addParameter("PART_NO",itemset.getRow().getValue("PART_NO"));
           cmd.addParameter("CONTRACT",itemset.getRow().getValue("SPARE_CONTRACT"));
           trans = mgr.perform(trans);
           String hasSt = trans.getValue("SPARESTRUCT/DATA/HASSPARESTRUCTURE");
           if ("FALSE".equals(hasSt)) {
               mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCANNOT: Cannot perform on selected line"));
           }
           else{

               bOpenNewWindow = true;
               row.addItem("PART_NO",sPartNo);
               row.addItem("WO_NO",itemset3.getRow().getValue("WO_NO"));    
               row.addItem("FRAME","ReportInWOSM");
               row.addItem("QRYST",qrystr);
               row.addItem("ORDER_NO",itemset3.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));

               urlString = createTransferUrl("../equipw/EquipmentSpareStructure3.page", buffer);

               newWinHandle = "detachedPart"; 
           }
       }
       else {

           bOpenNewWindow = true;
           row.addItem("PART_NO",sPartNo);
           row.addItem("WO_NO",itemset3.getRow().getValue("WO_NO"));    
           row.addItem("FRAME","ReportInWOSM");
           row.addItem("QRYST",qrystr);
           row.addItem("ORDER_NO",itemset3.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));

           urlString = createTransferUrl("../equipw/EquipmentSpareStructure3.page", buffer);

           newWinHandle = "detachedPart"; 
       }
       //Bug 82543, end
   }

   public void availDetail()
   {
      //Web Alignment - Enable Multirow Action
      ASPManager mgr = getASPManager();
      String sProjectId;

      calling_url=mgr.getURL();
      ctx.setGlobal("CALLING_URL",calling_url);


      int count = 0;
      ASPBuffer transferBuffer;
      ASPBuffer rowBuff;

      bOpenNewWindow = true;
      ctx.setCookie("PageID_CurrentWindow", "*");
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

      //Bug 66456, Start, Added check on PROJ
      if (mgr.isModuleInstalled("PROJ"))
      {
         trans.clear();

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
      newWinHandle = "AvailDetail"; 
   }

   public void supPerPart()
   {
      //Web Alignment - Enable Multirow Action
      ASPManager mgr = getASPManager();
      int count = 0;
      ASPBuffer transferBuffer;
      ASPBuffer rowBuff;

      bOpenNewWindow = true;
      ctx.setCookie("PageID_CurrentWindow", "*");
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


   public void issue()
   {
      ASPManager mgr = getASPManager();
      double qtyOnHand = 0.0;
      double nQtyAvblToIssue = 0.0;
      double nTotQtyRes = 0.0;
      double nTotQtyPlanable = 0.0;
      double nCount = 0.0;
      int count = 0;
      int successCount = 0;
      ASPTransactionBuffer transForIssue;
      ASPTransactionBuffer secBuff;
      boolean securityOk = false; 
      boolean canPerform = true;
      secBuff = mgr.newASPTransactionBuffer();
      secBuff.addSecurityQuery("Inventory_Part_In_Stock_API");
      secBuff = mgr.perform(secBuff);

      if ( secBuff.getSecurityInfo().itemExists("Inventory_Part_In_Stock_API") )
         securityOk = true;

      int currrow = itemset3.getCurrentRowNo();

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
      String dfStatus = itemset3.getValue("STATE");
      if (!("TRUE".equals(bIssAllowed)))
      {
         mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCANNOTSTATWO5WO: Work order status not valid for material issue."));
         return ;
      }

      if (!(sStatusCodeReleased.equals(dfStatus)))
      {
         mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCANNOTSTAT1: Maint Material Requisition status not valid for material issue."));
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

         if (!canPerform)
         {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCANNOTMAT1: No material requirements for selected item."));
            return;
         }
         else
         {
            trans.clear();

            if (plan_qty1 > nAvailToIss)
                mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMAVAIL: Available quantity for part ") +itemset.getRow().getValue("PART_NO") + mgr.translate("PCMWACTIVESEPARATE2AVAIL1:  was ")  +sAvailToIss+ mgr.translate("PCMWACTIVESEPARATE2AVAI2: .")); 

            if ( securityOk )
            {
               cmd = transForIssue.addCustomFunction("INVONHANDRES"+successCount,"Inventory_Part_In_Stock_API.Get_Sales_Plannable_Qty_Res","QTYRES");
               cmd.addParameter("CONTRACT",itemset.getValue("SPARE_CONTRACT"));
               cmd.addParameter("PART_NO",itemset.getValue("PART_NO"));
               cmd.addParameter("CONFIGURATION","");
            }
            if ( securityOk )
            {
               cmd = transForIssue.addCustomFunction("INVONHANDPLAN"+successCount,"Inventory_Part_In_Stock_API.Get_Sales_Plannable_Qty_Onhand","QTYPLANNABLE");
               cmd.addParameter("CONTRACT",itemset.getValue("SPARE_CONTRACT"));
               cmd.addParameter("PART_NO",itemset.getValue("PART_NO"));
               cmd.addParameter("CONFIGURATION","");
            }
            cmd = transForIssue.addCustomFunction("AUTOREP"+ successCount,"MAINTENANCE_INV_PART_API.Get_Auto_Repairable","AUTO_REPAIRABLE");
            cmd.addParameter("ITEM3_CONTRACT",itemset3.getValue("CONTRACT"));
            cmd.addParameter("PART_NO",itemset.getValue("PART_NO"));

            cmd = transForIssue.addCustomFunction("REPAIR"+ successCount,"MAINTENANCE_INV_PART_API.Get_Repairable","REPAIRABLE");
            cmd.addParameter("SPARE_CONTRACT",itemset.getValue("SPARE_CONTRACT"));
            cmd.addParameter("PART_NO",itemset.getValue("PART_NO"));

            //Bug 56688, Replaced Make_Issue_Detail with Make_Auto_Issue_Detail.
            cmd = transForIssue.addCustomCommand("MAKEISSUDETA" + successCount,"MAINT_MATERIAL_REQ_LINE_API.Make_Auto_Issue_Detail");
            cmd.addParameter("DUMMY_ACT_QTY_ISSUED");
            cmd.addParameter("ITEM5_MAINT_MATERIAL_ORDER_NO",itemset.getRow().getFieldValue("ITEM5_MAINT_MATERIAL_ORDER_NO"));
            cmd.addParameter("LINE_ITEM_NO",itemset.getRow().getValue("LINE_ITEM_NO"));
            cmd.addParameter("LOCATION_NO","");    
            cmd.addParameter("LOT_BATCH_NO","");
            cmd.addParameter("SERIAL_NO","");
            cmd.addParameter("ENG_CHG_LEVEL","");
            cmd.addParameter("WAIV_DEV_REJ_NO","");
            cmd.addParameter("ITEM2_PROJECT_NO","");
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
  		   mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWOSMCANTISSUE: All material could not be issued for part &1. Remaining quantity to be issued: &2", itemset.getValue("PART_NO"), new Double(nQtyShort).toString()));
  		//Bug 76767, End

         String isAutoRepairable = trans.getValue("AUTOREP"+i+"/DATA/AUTO_REPAIRABLE");
         String isRepairable = trans.getValue("REPAIR"+i+"/DATA/REPAIRABLE");
         if ( securityOk)
            nTotQtyRes = trans.getNumberValue("INVONHANDRES"+i+"/DATA/QTYRES");
         else
            nTotQtyRes = 0;

         if ( isNaN(nTotQtyRes) )
            nTotQtyRes = 0;
         if ( securityOk)
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
                               "&CONTRACT="+mgr.URLEncode(headset.getValue("CONTRACT"))+
                               "&MCH_CODE="+mgr.URLEncode(headset.getValue("MCH_CODE"))+
                               "&DESCRIPTION="+mgr.URLEncode(headset.getValue("MCH_CODE_DESCRIPTION"))+
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

      itemset3.goTo(currrow);
      okFindITEM5();

   }

   public void refreshForm()
   {
      ASPManager mgr = getASPManager();
      //headset.refreshRow();
      okFindITEM5();
      //okFindITEM3();
   }


   public void refreshMaterialTab()
   {
      ASPManager mgr = getASPManager();

      int nCurrentRow = itemset.getCurrentRowNo(); 
      int nHeadcurrent= itemset3.getCurrentRowNo();

      headset.refreshRow();   
      itemset.refreshAllRows();

      itemset3.goTo(nHeadcurrent); 
      itemset.goTo(nCurrentRow);
   }


   public void reserve()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer transForReserve;

      int currrow = itemset3.getCurrentRowNo();
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
      dfStatus = itemset3.getValue("STATE");

      if (!(sStatusCodeReleased.equals(dfStatus)))
      {
         mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCANNOTSTAT2: Maint Material Requisition status not valid for material reserve."));
         return ;
      }

      if (!("TRUE".equals(trans.getValue("RESALLOW/DATA/RES_ALLO"))))
      {
         mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCANNOTSTATWO2: Work order status not valid for material reserve."));
         return;
      }

      transForReserve = mgr.newASPTransactionBuffer();

      for (int i = 0; i < count; i++)
      {

         if (itemset.getNumberValue("PLAN_QTY") <= (itemset.getNumberValue("QTY") + itemset.getNumberValue("QTY_ASSIGNED"))) // + itemset.getNumberValue("QTY_RETURNED"))) * ASSALK  Material Issue & Reserve modification.
         {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCANNOTMAT3: No material requirements for selected item."));
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
         cmd.addParameter("MAINT_MATERIAL_ORDER_NO", itemset.getValue("MAINT_MATERIAL_ORDER_NO"));
         cmd.addParameter("LINE_ITEM_NO", itemset.getValue("LINE_ITEM_NO"));
         cmd.addParameter("LOCATION_NO", "");    
         cmd.addParameter("LOT_BATCH_NO", "");
         cmd.addParameter("SERIAL_NO", "");
         cmd.addParameter("ENG_CHG_LEVEL", "");
         cmd.addParameter("WAIV_DEV_REJ_NO", "");
         cmd.addParameter("ACTIVITY_SEQ", "");
         cmd.addParameter("PROJECT_ID", "");
         cmd.addParameter("QTY_TO_ASSIGN", "");

         successCount++;

         if (itemlay.isMultirowLayout())
            itemset.next();
         if (planQty > nAvalToRes)
         {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMAVAIL: Available quantity for part ") +itemset.getRow().getValue("PART_NO") + mgr.translate("PCMWACTIVESEPARATE2AVAIL1:  was ")  +sAvalToRes+ mgr.translate("PCMWACTIVESEPARATE2AVAI2: .")); 
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
        	 mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWOSMCOULDNOTALL: All material could not be allocated for part &1. Remaining quantity to be reserved: &2", itemset.getValue("PART_NO"), new Double(nQtyShort).toString()));
         //Bug 76767, End  

         if (itemlay.isMultirowLayout())
            itemset.next();
      }

      if (itemlay.isMultirowLayout())
         itemset.setFilterOff();

      itemset3.goTo(currrow);
      okFindITEM5();
   }

   public void manIssue()
   {
      ASPManager mgr = getASPManager();

      int currhead = headset.getCurrentRowNo();
      int currrow = itemset3.getCurrentRowNo();

      headMchCode = headset.getRow().getValue("MCH_CODE");
      headMchDesc = headset.getRow().getValue("MCH_CODE_DESCRIPTION");

      if (itemlay.isMultirowLayout())
         itemset.goTo(itemset.getRowSelected());

      trans.clear();

      cmd = trans.addCustomFunction("FINSTATEDEC","MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__","DB_STATE"); 
      cmd.addParameter("DB_STATE","Released"); 

      cmd = trans.addCustomFunction("ISSALLOW","MAINT_MATERIAL_REQ_LINE_API.Is_Issued_Allowed","ISS_ALLO");
      cmd.addParameter("WO_NO",itemset.getRow().getFieldValue("ITEM_WO_NO"));

      cmd = trans.addCustomFunction("SERIALTRA","PART_CATALOG_API.Get_Serial_Tracking_Code_Db","SERIAL_TRACK");
      cmd.addParameter("PART_NO",itemset.getRow().getValue("PART_NO"));

      cmd = trans.addCustomFunction("AUTOREP","MAINTENANCE_INV_PART_API.Get_Auto_Repairable","AUTO_REPAIRABLE");
      cmd.addParameter("ITEM3_CONTRACT",itemset3.getRow().getFieldValue("ITEM3_CONTRACT"));
      cmd.addParameter("PART_NO",itemset.getRow().getValue("PART_NO"));

      cmd = trans.addCustomFunction("REPAIR","MAINTENANCE_INV_PART_API.Get_Repairable","REPAIRABLE");
      cmd.addParameter("ITEM3_CONTRACT",itemset3.getRow().getFieldValue("ITEM3_CONTRACT"));
      cmd.addParameter("PART_NO",itemset.getRow().getValue("PART_NO"));

      trans = mgr.perform(trans);

      String hasSerialNum = trans.getValue("SERIALTRA/DATA/SERIAL_TRACK");
      String isAutoRepairable = trans.getValue("AUTOREP/DATA/AUTO_REPAIRABLE");
      String isRepairable = trans.getValue("REPAIR/DATA/REPAIRABLE");
      String sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DB_STATE");
      String bIssAllowed = trans.getValue("ISSALLOW/DATA/ISS_ALLO");
      String dfStatus = itemset3.getRow().getFieldValue("ITEM3_STATE");

      if (!("TRUE".equals(bIssAllowed)))
      {
         mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCANNOTSTATWO5WO: Work order status not valid for material issue."));
         return ;
      }

      if (!(sStatusCodeReleased.equals(dfStatus)))
      {
         mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMMTRQSNTVLD: Maint Material Requisition status not valid for material issue."));
         return ;
      }

      double planQty = itemset.getRow().getNumberValue("PLAN_QTY");
      if (isNaN(planQty))
         planQty = 0;

      double qty = itemset.getRow().getNumberValue("QTY");
      if (isNaN(qty))
         qty = 0;

      double qty_return = itemset.getRow().getNumberValue("QTY_RETURNED");
      if ( isNaN(qty_return) )
         qty_return = 0;

      double qty_outstanding = planQty - qty; //(qty + qty_return); * ASSALK  Material Issue & Reserve modification.

      if ( qty_outstanding > 0 )
      {

         double tempQtyLeft = planQty - qty;
         if (tempQtyLeft < 0)
            tempQtyLeft = 0;

         nQtyLeft = String.valueOf(tempQtyLeft);
         if ( ( "TRUE".equals(isAutoRepairable) ) &&  ( "TRUE".equals(isRepairable) ) &&  ( "NOT SERIAL TRACKING".equals(hasSerialNum) ) )
            creRepWO = "TRUE";

         //Web Alignment - simplify code for RMBs    
         bOpenNewWindow = true;           
         ctx.setCookie("PageID_CurrentWindow", "*");
         urlString = "InventoryPartLocationDlg.page?PART_NO="+mgr.URLEncode(itemset.getRow().getValue("PART_NO"))+
                     "&CONTRACT="+mgr.URLEncode(itemset.getRow().getValue("SPARE_CONTRACT"))+
                     "&FRMNAME=ReportInWOSM&QRYSTR="+mgr.URLEncode(qrystr)+
                     "&WO_NO="+mgr.URLEncode(itemset.getRow().getValue("WO_NO"))+
                     "&LINE_ITEM_NO="+mgr.URLEncode(itemset.getRow().getValue("LINE_ITEM_NO"))+
                     "&DESCRIPTION="+mgr.URLEncode(itemset.getRow().getValue("SPAREDESCRIPTION"))+
                     "&HEAD_CONDITION_CODE=" + mgr.URLEncode(itemset.getRow().getValue("CONDITION_CODE")) +
                     "&HEAD_CONDITION_CODE_DESC=" + mgr.URLEncode(itemset.getRow().getValue("CONDDESC")) +
                     "&QTYLEFT="+mgr.URLEncode(nQtyLeft)+
                     "&MAINTMATORDNO="+mgr.URLEncode(itemset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"))+
                     "&CREREPWO="+creRepWO+
                     "&MCH_CODE="+headMchCode+
                     "&MCH_DESCRIPTION="+headMchDesc+
                     "&OWNERSHIP=" + mgr.URLEncode(itemset.getRow().getValue("PART_OWNERSHIP")) +
                     "&OWNER=" + mgr.URLEncode(itemset.getRow().getValue("OWNER")) +
                     "&OWNERNAME=" + mgr.URLEncode(itemset.getRow().getValue("OWNER_NAME"));
         newWinHandle = "manIsssue";

         ctx.setGlobal("CURRHEADGLOBAL",String.valueOf(currhead));
         ctx.setGlobal(   "CURRROWGLOBAL",String.valueOf(   currrow));
      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCANNOTMAT: No material requirements for selected item."));

      itemset3.goTo(currrow); 
   }

   public void manReserve()
   {
      ASPManager mgr = getASPManager();

      int currhead = headset.getCurrentRowNo();
      int currrow = itemset3.getCurrentRowNo();

      if (itemlay.isMultirowLayout())
         itemset.goTo(itemset.getRowSelected());

      trans.clear();

      cmd = trans.addCustomFunction("FINSTATEDEC","MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__","DB_STATE"); 
      cmd.addParameter("DB_STATE","Released"); 

      cmd = trans.addCustomFunction("RESALLOW","MAINT_MATERIAL_REQ_LINE_API.Is_Reservation_Allowed","RES_ALLO");
      cmd.addParameter("WO_NO",itemset.getRow().getFieldValue("ITEM_WO_NO"));

      trans = mgr.perform(trans);

      String sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DB_STATE");
      String bResAllowed = trans.getValue("RESALLOW/DATA/RES_ALLO");  
      String dfStatus = itemset3.getRow().getFieldValue("ITEM3_STATE");

      if (!("TRUE".equals(bResAllowed)))
      {
         mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCANNOTSTATWO5: Work order status not valid for material reserve."));
         return ;
      }

      if (!(sStatusCodeReleased.equals(dfStatus)))
      {
         mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCANNOTSTAT5: Maint Material Requisition status not valid for material manual reserve."));
         return ;
      }

      double planQty = itemset.getRow().getNumberValue("PLAN_QTY");
      if (isNaN(planQty))
         planQty = 0;

      double qty = itemset.getRow().getNumberValue("QTY");
      if (isNaN(qty))
         qty = 0;

      double qtyAssigned = itemset.getRow().getNumberValue("QTY_ASSIGNED");
      if (isNaN(qtyAssigned))
         qtyAssigned = 0;

      double qtyReturned = itemset.getRow().getNumberValue("QTY_RETURNED");
      if (isNaN(qtyReturned))
         qtyReturned = 0;

      if (planQty > (qty + qtyAssigned)) // + qtyReturned)) * ASSALK  Material Issue & Reserve modification.
      {

         double tempQtyLeft = planQty - qty - qtyAssigned;
         nQtyLeft = String.valueOf(tempQtyLeft);

         //Web Alignment - simplify code for RMBs    
         bOpenNewWindow = true;           
         ctx.setCookie("PageID_CurrentWindow", "*");
         urlString = "MaterialRequisReservatDlg.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"))+
                     "&LINE_ITEM_NO="+mgr.URLEncode(itemset.getRow().getValue("LINE_ITEM_NO"))+
                     "&FRMNAME=ReportInWOSM&QRYSTR="+mgr.URLEncode(qrystr)+
                     "&PART_NO="+mgr.URLEncode(itemset.getRow().getValue("PART_NO"))+
                     "&CONTRACT="+mgr.URLEncode(itemset.getRow().getValue("SPARE_CONTRACT"))+
                     "&DESCRIPTION="+mgr.URLEncode(itemset.getRow().getValue("SPAREDESCRIPTION"))+
                     "&CONDITION_CODE=" + mgr.URLEncode(itemset.getRow().getValue("CONDITION_CODE")) +
                     "&CONDITION_CODE_DESC=" + mgr.URLEncode(itemset.getRow().getValue("CONDDESC")) +
                     "&QTYLEFT="+mgr.URLEncode(nQtyLeft)+
                     "&MAINTMATORDNO="+mgr.URLEncode(itemset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"))+
                     "&OWNERSHIP=" + mgr.URLEncode(itemset.getRow().getValue("PART_OWNERSHIP")) +
                     "&OWNER=" + mgr.URLEncode(itemset.getRow().getValue("OWNER")) +
                     "&OWNERNAME=" + mgr.URLEncode(itemset.getRow().getValue("OWNER_NAME")) ;
         newWinHandle = "manReserve";
         //

         ctx.setGlobal("CURRHEADGLOBAL",String.valueOf(currhead));
         ctx.setGlobal("CURRROWGLOBAL",String.valueOf(currrow));
      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCANNOTMAT: No material requirements for selected item."));

      itemset3.goTo(currrow); 
   }

   public void availtoreserve()
   {
      ASPManager mgr = getASPManager();

      // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
      int count = 0;
      ASPBuffer transferBuffer;
      ASPBuffer rowBuff;

      bOpenNewWindow = true;
      ctx.setCookie("PageID_CurrentWindow", "*");

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

   public void unreserve()
   {
      ASPManager mgr = getASPManager();

      int currhead = headset.getCurrentRowNo();
      int currrow = itemset3.getCurrentRowNo();

      if (itemlay.isMultirowLayout())
         itemset.goTo(itemset.getRowSelected());

      trans.clear();

      cmd = trans.addCustomFunction("FINSTATEDEC","MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__","DB_STATE"); 
      cmd.addParameter("DB_STATE","Released"); 

      trans = mgr.perform(trans);

      String sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DB_STATE");
      String dfStatus = itemset3.getRow().getFieldValue("ITEM3_STATE");

      if (!(sStatusCodeReleased.equals(dfStatus)))
      {
         mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCANNOTSTAT6: Work order status not valid for material unreserve."));
         return ;
      }

      double qtyAssigned = itemset.getRow().getNumberValue("QTY_ASSIGNED");
      if (isNaN(qtyAssigned))
         qtyAssigned = 0;

      if (qtyAssigned > 0)
      {
         nQtyLeft = String.valueOf(qtyAssigned);

         //Web Alignment - simplify code for RMBs
         bOpenNewWindow = true;
         ctx.setCookie("PageID_CurrentWindow", "*");
         urlString = "MaterialRequisReservatDlg2.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"))+"&LINE_ITEM_NO="+mgr.URLEncode(itemset.getRow().getValue("LINE_ITEM_NO"))+"&FRMNAME=ReportInWOSM&QRYSTR="+mgr.URLEncode(qrystr)+"&PART_NO="+mgr.URLEncode(itemset.getRow().getValue("PART_NO"))+"&CONTRACT="+mgr.URLEncode(itemset.getRow().getValue("SPARE_CONTRACT"))+"&DESCRIPTION="+mgr.URLEncode(itemset.getRow().getValue("SPAREDESCRIPTION"))+"&QTYLEFT="+mgr.URLEncode(nQtyLeft)+"&MAINTMATORDNO="+mgr.URLEncode(itemset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));
         newWinHandle = "manUnReserve";
         //

         ctx.setGlobal("CURRHEADGLOBAL",String.valueOf(currhead));
         ctx.setGlobal("CURRROWGLOBAL",String.valueOf(currrow));
      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCANNOTMAT: No material requirements for selected item."));
   }


   public void matReqUnissue()
   {
      ASPManager mgr = getASPManager();

      int currhead = headset.getCurrentRowNo();
      int currrow = itemset3.getCurrentRowNo();

      if (itemlay.isMultirowLayout())
         itemset.goTo(itemset.getRowSelected());

      trans.clear();

      cmd = trans.addCustomFunction("FINSTATEDEC","MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__","DB_STATE"); 
      cmd.addParameter("DB_STATE","Released"); 

      cmd = trans.addCustomFunction("OSTATE","Active_Separate_API.Get_Obj_State","OBJSTATE");  
      cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO")); 

      trans = mgr.perform(trans);

      String objState  =trans.getBuffer("OSTATE/DATA").getFieldValue("OBJSTATE");

      String sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DB_STATE");
      String dfStatus = itemset3.getRow().getFieldValue("ITEM3_STATE");

      double qty = itemset.getRow().getNumberValue("QTY");
      if (isNaN(qty))
         qty = 0;

      if (qty > 0)
      {
         if (!(( "WORKDONE".equals(objState) )|| ( "REPORTED".equals(objState) )|| ( "STARTED".equals(objState) )|| ( "RELEASED".equals(objState) )))
         {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMWOSTNVLDMTUN: Work order status not valid for material Unissue."));
            return ;
         }
         else
         {
            //Web Alignment - simplify code for RMBs
            bOpenNewWindow = true;
            ctx.setCookie("PageID_CurrentWindow", "*");
            urlString = "ActiveWorkOrder.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"))+"&MAINTMATORDNO="+mgr.URLEncode(itemset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"))+"&LINE_ITEM_NO="+mgr.URLEncode(itemset.getRow().getValue("LINE_ITEM_NO"))+"&FRMNAME=ReportInWOSM&QRYSTR="+mgr.URLEncode(qrystr);
            newWinHandle = "manUnIsssue";
            ctx.setGlobal("CURRROWGLOBAL",String.valueOf(currrow));
         }
      }
      else
      {
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERSMNOISSUEMATERIAL: Cannot perform Material Requisition Unissue on this record."));
      }

      itemset3.goTo(currrow); 
   }

   public void setSalesPartCost()
   {
      double colSalesPartCost = 0;
      double colListPrice = 0;
      double colDiscount = 0;
      double colAmountSales = 0;
      double qtyToInvoice = 0;
      double nSalesPriceAmount = 0;
      boolean salesSecOk = false;
      boolean salesPart = false;

      ASPManager mgr = getASPManager();

      trans.clear();
      int n = itemset1.countRows();
      customerNo  = headset.getValue("CUSTOMER_NO");
      agreementId = headset.getValue("AGREEMENT_ID");
      itemset1.first();

      if (n > 0)
      {
         ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer();
         secBuff.addSecurityQuery("Sales_Part_API","Get_Cost");
         secBuff.addSecurityQuery("SALES_PART");

         secBuff = mgr.perform(secBuff);

         if (secBuff.getSecurityInfo().itemExists("Sales_Part_API.Get_Cost"))
            salesSecOk = true;
         if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
            salesPart = true;

         for (int i=0; i<=n; ++i)
         {
            String catalogNo     = itemset1.getRow().getValue("CATALOG_NO");
            String item1Contract = itemset1.getRow().getFieldValue("ITEM1_CONTRACT");
            String roleCode      = itemset1.getRow().getValue("ROLE_CODE");
            String item1OrgCode  = itemset1.getRow().getFieldValue("ITEM1_ORG_CODE");
            String priceListNo   = itemset1.getRow().getFieldValue("PRICE_LIST_NO");
            String qty           = itemset1.getRow().getFieldValue("QTY");
            String empNo         = itemset1.getRow().getFieldValue("EMP_NO");

            if (salesSecOk)
            {
               cmd = trans.addCustomFunction("CST"+i, "Work_Order_Planning_Util_Api.Get_Cost", "COST3" );
               cmd.addParameter("ITEM1_ORG_CODE",item1OrgCode);
               cmd.addParameter("ROLE_CODE",roleCode);
               cmd.addParameter("ITEM1_CONTRACT",item1Contract);  
               cmd.addParameter("CATALOG_NO",catalogNo);
               cmd.addParameter("ITEM1_CONTRACT",item1Contract);  
               cmd.addParameter("EMP_NO",empNo);
               cmd.addParameter("DUMMY","TRUE");
            }
            else
            {
               cmd = trans.addCustomFunction("CST"+i, "Work_Order_Planning_Util_Api.Get_Cost", "COST3" );
               cmd.addParameter("ITEM1_ORG_CODE",item1OrgCode);
               cmd.addParameter("ROLE_CODE",roleCode);
               cmd.addParameter("DUMMY","");
               cmd.addParameter("DUMMY","");
               cmd.addParameter("ITEM1_CONTRACT",item1Contract);  
               cmd.addParameter("EMP_NO",empNo);
               cmd.addParameter("DUMMY","TRUE");
            }

            if (salesPart)
            {
               cmd = trans.addCustomCommand("PRICEINFO"+i,"Work_Order_Coding_API.Get_Price_Info");
               cmd.addParameter("BASE_PRICE");
               cmd.addParameter("SALE_PRICE");
               cmd.addParameter("DISCOUNT");
               cmd.addParameter("CURRENCY_RATE");
               cmd.addParameter("ITEM1_CONTRACT",item1Contract);
               cmd.addParameter("CATALOG_NO",catalogNo);
               cmd.addParameter("CUSTOMER_NO",customerNo);
               cmd.addParameter("AGREEMENT_ID",agreementId);
               cmd.addParameter("PRICE_LIST_NO",priceListNo);
               cmd.addParameter("QTY",qty);          
            }
            itemset1.next();
         }

         trans = mgr.validate(trans);
         itemset1.first();

         for (int i=0; i<=n; ++i)
         {
            ASPBuffer row = itemset1.getRow();

            colSalesPartCost = trans.getNumberValue("CST"+i+"/DATA/COST3");

            if (isNaN(colSalesPartCost))
               colSalesPartCost = 0;

            if (salesPart)
            {
               colListPrice    = trans.getNumberValue("PRICEINFO"+i+"/DATA/BASE_PRICE"); 
               if (isNaN(colListPrice))
                  colListPrice = 0;

               colDiscount     = trans.getNumberValue("PRICEINFO"+i+"/DATA/DISCOUNT");
               if (isNaN(colDiscount))
                  colDiscount = 0;

               colAmountSales = colListPrice * trans.getNumberValue("PRICEINFO"+i+"/DATA/QTY");
               if (isNaN(colAmountSales))
                  colAmountSales = 0;

               qtyToInvoice = itemset1.getNumberValue("QTY_TO_INVOICE");
               if (isNaN(qtyToInvoice))
                  qtyToInvoice = 0;

               if (qtyToInvoice != 0)
               {
                  nSalesPriceAmount = colListPrice * qtyToInvoice;
                  colAmountSales    = nSalesPriceAmount - (colDiscount/100 * nSalesPriceAmount);         
               }
               else
               {
                  double tempQty = itemset1.getNumberValue("QTY");
                  if (isNaN(tempQty))
                     tempQty = 0;

                  if (tempQty != 0)
                  {
                     nSalesPriceAmount = colListPrice * tempQty;
                     colAmountSales    = nSalesPriceAmount - (colDiscount/100 * nSalesPriceAmount);
                  }
                  else
                     colAmountSales    = 0;
               }
            }

            row.setNumberValue("SALESPARTCOST",colSalesPartCost);
            row.setNumberValue("BASE_PRICE",colListPrice);
            row.setNumberValue("AMOUNTSALES",colAmountSales);

            itemset1.setRow(row);

            itemset1.next();
         }
      }
      itemset1.first();
   }

   public double GetInventoryQuantity(String sQtyType)
   {
      ASPManager mgr = getASPManager();

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

      ASPTransactionBuffer secBuff;
      boolean securityOk = false;
      secBuff = mgr.newASPTransactionBuffer();
      secBuff.addSecurityQuery("Inventory_Part_In_Stock_API");
      secBuff = mgr.perform(secBuff);

      if ( secBuff.getSecurityInfo().itemExists("Inventory_Part_In_Stock_API") )
         securityOk = true;

      trans.clear();

      cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
      cmd.addParameter("PART_OWNERSHIP",itemset.getRow().getValue("PART_OWNERSHIP"));
      trans = mgr.validate(trans);
      String sClientOwnershipDefault = trans.getValue("VALIDOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");
      trans.clear();

      if ( securityOk )
      {
         trans.clear();
         cmd = trans.addCustomFunction("MAINTREQSUP","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","SUPPLY_CODE");
         trans = mgr.validate(trans);
         String sStandardInv = trans.getValue("MAINTREQSUP/DATA/SUPPLY_CODE");
         trans.clear();

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
            cmd.addParameter("CONFIGURATION",itemset.getRow().getValue("CONFIGURATION"));
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
            cmd.addParameter("ITEM3_ACTIVITY_SEQ","0");
            cmd.addParameter("PROJECT_ID","");
            cmd.addParameter("LOCATION_NO");
            cmd.addParameter("ORDER_ISSUE");
            cmd.addParameter("AUTOMAT_RESERV");
            cmd.addParameter("MANUAL_RESERV");
            //cmd.addParameter("CONDITION_CODE");
            cmd.addParameter("CONDITION_CODE",itemset.getRow().getFieldValue("CONDITION_CODE"));
         }
         else
         {
            trans.clear();
            //Bug 66456, Start, Added check on PROJ
            if (mgr.isModuleInstalled("PROJ"))
            {
               cmd = trans.addCustomFunction("PROJID","Activity_API.Get_Project_Id","PROJECT_ID");
               cmd.addParameter("ITEM3_ACTIVITY_SEQ",mgr.readValue("ITEM3_ACTIVITY_SEQ"));
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
            cmd.addParameter("ITEM3_ACTIVITY_SEQ","0");
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
            //cmd.addParameter("CONDITION_CODE");
            cmd.addParameter("CONDITION_CODE",itemset.getRow().getFieldValue("CONDITION_CODE"));
         }    
      }
      trans = mgr.perform(trans);

      if ( securityOk )
      {
         nQty = trans.getNumberValue("INVONHAND/DATA/QTYONHAND");
         if ( isNaN(nQty) )
            nQty = 0.0;
      }
      else
      {
         nQty = 0.0;
      }

      return nQty;
   }



   public boolean isModuleInst(String module_)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();
      cmd = mgr.newASPCommand();
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


   private void refreshBlocks()
   {
      ASPManager mgr = getASPManager();

      String sRefreshBLock = mgr.readValue("REFRESH_BLOCK");
      boolean salesSec = false;
	  String empName= null;

      if ("ITEM3EDIT".equals(sRefreshBLock))
      {
         itemlay1.setLayoutMode(itemlay1.NEW_LAYOUT);

         setStringLables();
         trans.clear();     
         newRowITEM1();

         if (!recordCancel)
         {
            ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer();
            secBuff.addSecurityQuery("SALES_PART");

            secBuff = mgr.perform(secBuff);

            if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
               salesSec = true;

            trans.clear(); 
            /*
            cmd = trans.addCustomFunction("GETEMPID","Company_Emp_API.Get_Max_Employee_Id","EMP_NO");
            cmd.addParameter("COMPANY",itemset1.getRow().getFieldValue("COMPANY"));
            cmd.addParameter("EMP_SIGNATURE",sent_emp_id);
*/
           if (! mgr.isEmpty(sent_emp_id))
           {   
               cmd = trans.addCustomFunction("GETEMPNAME","Person_Info_API.Get_Name","NAME");
               cmd.addParameter("EMP_SIGNATURE",sent_emp_id);
           }
            if (salesSec &&  ! mgr.isEmpty(sent_salesPartNo))
            {
               cmd = trans.addCustomFunction("PARTCOST","Sales_Part_API.Get_Cost","COST1");
               cmd.addParameter("ITEM1_CONTRACT",sent_site);
               cmd.addParameter("CATALOG_NO",sent_salesPartNo);     

               cmd = trans.addCustomFunction("DEFCATDESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
               cmd.addParameter("ITEM1_CONTRACT",sent_site);
               cmd.addParameter("CATALOG_NO",sent_salesPartNo);      
            }

            trans = mgr.perform(trans); 

            //String empNo = trans.getValue("GETEMPID/DATA/EMP_NO");
             if (!mgr.isEmpty(sent_emp_id))
          	         empName = trans.getValue("GETEMPNAME/DATA/NAME");

            itemset1.setValue("NAME",sent_emp_name);
            itemset1.setValue("LINE_DESCRIPTION",sent_description);
	    //Bug 85045, Start
	    if (!mgr.isEmpty(sent_department))
	    {
	       itemset1.setValue("ORG_CODE",sent_department);
	    }
	    if (!mgr.isEmpty(sent_org_contract))
	    {
	       itemset1.setValue("CONTRACT",sent_org_contract);
	    }
	    //Bug 85045, End
            itemset1.setValue("ROLE_CODE",sent_craft);      
            itemset1.setValue("TEAM_CONTRACT",sent_team_contract);      
            itemset1.setValue("TEAM_ID",sent_team_id);      
            itemset1.setValue("EMP_SIGNATURE",sent_emp_id);
            itemset1.setValue("EMP_NO",sent_empNo);      
            itemset1.setValue("NAME",empName);      
            itemset1.setValue("PLAN_LINE_NO",sent_planLineNo);
	    //Bug 85045, Start
            itemset1.setValue("CAT_CONTRACT",sent_site); 
	    //Bug 85045, End
            itemset1.setValue("CATALOG_NO",sent_salesPartNo); 
            itemset1.setValue("PRICE_LIST_NO",sent_priceListNo);

            if (!mgr.isEmpty(sent_salesPrice))
               itemset1.setNumberValue("LIST_PRICE",toDouble(sent_salesPrice));

            if (!mgr.isEmpty(sent_discount))
               itemset1.setNumberValue("DISCOUNT",toDouble(sent_discount));

            if (salesSec &&  ! mgr.isEmpty(sent_salesPartNo))
            {
               itemset1.setNumberValue("SALESPARTCOST",trans.getNumberValue("PARTCOST/DATA/COST1"));
               itemset1.setValue("SALESPARTCATALOGDESC",trans.getValue("DEFCATDESC/DATA/SALESPARTCATALOGDESC"));
            }
         }
      }
   }


   public void preDefine()
   {
      ASPManager mgr = getASPManager(); 


      tempblk = mgr.newASPBlock("TEMP");
      f = tempblk.addField("LUNAME");


      headblk = mgr.newASPBlock("HEAD");

      f = headblk.addField("OBJID");
      f.setHidden();

      f = headblk.addField("OBJVERSION");
      f.setHidden();

      f = headblk.addField("OBJSTATE");
      f.setHidden();  

      boolean orderInst = false;
      boolean docmanInst = false;

      f = headblk.addField("MODULENAME");
      f.setHidden();
      f.setFunction("''");

      if (isModuleInst("ORDER"))
         orderInst = true;
      else
         orderInst = false;

      if (isModuleInst("DOCMAN"))
         docmanInst = true;
      else
         docmanInst = false;      

      f = headblk.addField("WO_NO","Number","#");
      f.setLOV("ActiveSeparateLov.page",600,445);
      f.setLOVProperty("WHERE","CONTRACT IN (SELECT CONTRACT FROM USER_ALLOWED_SITE_LOV)");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMWONO: WO No");
      f.setHilite();
      f.setSize(15);
      f.setReadOnly();

      f = headblk.addField("CONTRACT");
      f.setSize(5);
      f.setHilite();
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMWOCONTRACT: WO Site");
      f.setUpperCase();
      f.setReadOnly();
      f.setSize(15);

      f = headblk.addField("CONNECTION_TYPE");
      f.setSize(15);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCONNTYPE: ConnectionType");   
      f.setMaxLength(15);
      f.setReadOnly();
      f.setSelectBox();
      f.enumerateValues("MAINT_CONNECTION_TYPE_API");
      f.setHilite();


      f = headblk.addField("MCH_CODE");
      f.setSize(15);
      f.setHilite();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMMCHCODE: Object ID");
      f.setDynamicLOV("MAINTENANCE_OBJECT",600,450);
      f.setUpperCase();
      f.setMaxLength(100);
      f.setReadOnly();

      f = headblk.addField("MCH_CODE_DESCRIPTION");
      f.setSize(20);
      f.setHilite();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMOBJDESC: Object Description");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("MCH_CODE_CONTRACT");
      f.setSize(13);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERMCHCODECONT: Site");   
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
      f.setUpperCase();
      f.setMaxLength(5);
      f.setReadOnly();
      f.setHilite();

      f = headblk.addField("STATE");
      f.setSize(15);
      f.setHilite();
      f.setLOV("ActiveSeparateLov1.page",600,450);
      f.setMandatory();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMSTATE: Status");
      f.setReadOnly();


      f = headblk.addField("ERR_DESCR");
      f.setSize(45);
      f.setHilite();
      f.setMandatory();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMERRDESCR: Directive");
      f.setMaxLength(60);
      f.setDefaultNotVisible();

      f = headblk.addField("ORG_CODE");
      f.setSize(15);
      f.setHilite();
      f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMORGCODE: Maintenance Organization");
      f.setUpperCase();
      f.setMaxLength(8);
      f.setReadOnly();

      f = headblk.addField("ORGCODEDESCRIPTION");
      f.setSize(30);
      f.setHilite();
      f.setFunction("Organization_Api.Get_Description(:CONTRACT,:ORG_CODE)");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMORG_CODEDESC: Organization Description"); 
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("CRITICALITY");
      f.setSize(15);
      f.setDynamicLOV("EQUIPMENT_CRITICALITY",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCRITICALITY: Criticality");
      f.setFunction("EQUIPMENT_FUNCTIONAL_API.Get_Criticality(:CONTRACT,:MCH_CODE)");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("CRITICALITYDESCRIPTION");
      f.setSize(20);
      f.setFunction("EQUIPMENT_FUNCTIONAL_API.Get_Criticality_Description(:CONTRACT,:MCH_CODE)");
      f.setReadOnly();
      f.setMaxLength(2000);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCRITICALITYDESCRP: Criticality Description");
      f.setDefaultNotVisible();

      f = headblk.addField("CBWARRANTYONOBJECT");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCBWARRANTYONOBJECT: Supplier Warranty");
      f.setFunction("''");
      f.setCheckBox("FALSE,TRUE");
      f.setReadOnly();
      f.setDefaultNotVisible();

      //Bug 87766, Start, Modified label and function of CBHASSTRUCTURE. Added CBINASTRUCTURE.
      f = headblk.addField("CBHASSTRUCTURE");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCBHASSTRUCTURE: Has Structure");
      f.setFunction("substr(WORK_ORDER_CONNECTION_API.Has_Connection_Down(:WO_NO),1,5)");
      f.setCheckBox("FALSE,TRUE");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("CBINASTRUCTURE");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCBINASTRUCTURE: In a Structure");
      f.setFunction("substr(WORK_ORDER_CONNECTION_API.Has_Connection_Up(:WO_NO),1,5)");
      f.setCheckBox("FALSE,TRUE");
      f.setReadOnly();
      f.setDefaultNotVisible();
      //Bug 87766, End

      f = headblk.addField("CBHASDOCUMENTS");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCBHASDOCUMENTS: Documents");
      if (docmanInst)
         f.setFunction("substr(Doc_Reference_Object_API.Exist_Obj_Reference('ActiveSeparate',CONCAT('WO_NO=',CONCAT(WO_NO,'^'))),1, 5)");
      else
         f.setFunction("''"); 
      f.setCheckBox("FALSE,TRUE");
      f.setReadOnly(); 
      f.setDefaultNotVisible();

      //Bug 77304, start
      f = headblk.addField("CBTRANSTOMOB");
      if (mgr.isModuleInstalled("MMAINT"))
          f.setFunction("MOBMGR_WORK_ORDER_API.Check_Exist(:WO_NO)");
      else
          f.setFunction("''");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCBTRANSTOMOB: Transferred To Mobile");
      f.setCheckBox("FALSE,TRUE");
      f.setReadOnly();
      f.setDefaultNotVisible();
      //Bug 77304, end

      //----------Fields got from ITEMBLK0------------------


      f = headblk.addField("ERR_CLASS");
      f.setSize(10);
      f.setDynamicLOV("WORK_ORDER_CLASS_CODE",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMERRCLASS: Class");
      f.setUpperCase();
      f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMERCL: List of Class"));
      f.setDefaultNotVisible();

      f = headblk.addField("ERRCLASSDESCR");
      f.setFunction("WORK_ORDER_CLASS_CODE_API.Get_Description(:ERR_CLASS)");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMERRCLASSDESC: Class Description");
      mgr.getASPField("ERR_CLASS").setValidation("ERRCLASSDESCR");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("ERR_TYPE");
      f.setSize(10);
      f.setDynamicLOV("WORK_ORDER_TYPE_CODE",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMERRTYPE: Type");
      f.setUpperCase();
      f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMERTY: List of Type"));
      f.setDefaultNotVisible();

      f = headblk.addField("ERRTYPEDESC");
      f.setSize(22);
      f.setFunction("WORK_ORDER_TYPE_CODE_API.Get_Description(:ERR_TYPE)");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMERRTYPEDESC: Type Description");
      mgr.getASPField("ERR_TYPE").setValidation("ERRTYPEDESC");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("PERFORMED_ACTION_ID");
      f.setSize(10);
      f.setDynamicLOV("MAINTENANCE_PERF_ACTION",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMPERFORMEDACTIONID: Performed Action");
      f.setUpperCase();
      f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMPEACID: List of Performed Action"));
      f.setDefaultNotVisible();

      f = headblk.addField("PERFORMEDACTIONDESC");
      f.setFunction("MAINTENANCE_PERF_ACTION_API.Get_Description(:PERFORMED_ACTION_ID)");
      mgr.getASPField("PERFORMED_ACTION_ID").setValidation("PERFORMEDACTIONDESC");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMPERFORMEDACTIONDESC: Performed Action Description");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("ERR_CAUSE");
      f.setSize(10);
      f.setDynamicLOV("MAINTENANCE_CAUSE_CODE",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMERRCAUSE: Cause");
      f.setUpperCase();
      f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMERCA: List of Cause"));
      f.setDefaultNotVisible();

      f = headblk.addField("ERRCAUSEDESC");
      f.setSize(22);
      f.setFunction("MAINTENANCE_CAUSE_CODE_API.Get_Description(:ERR_CAUSE)");
      mgr.getASPField("ERR_CAUSE").setValidation("ERRCAUSEDESC");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = headblk.addField("ERR_CAUSE_LO");
      f.setSize(40);
      f.setHeight(3);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMERRCAUSELO: Cause Description");
      f.setDefaultNotVisible();

      f = headblk.addField("WORK_DONE");
      f.setSize(42);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMWORKDONE: Work Done");
      f.setMaxLength(60);
      //031110  ARWILK  End  (Bug#110392)
      f.setDefaultNotVisible();

      f = headblk.addField("PERFORMED_ACTION_LO");
      f.setSize(40);
      f.setHeight(3);
        //Bug 74288, Start
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMPERFORMEDACTIONLO: Work Details");
        //Bug 74288, End
      f.setDefaultNotVisible();

      f = headblk.addField("NOTE");
      f.setSize(75);
      f.setInsertable();
      f.setCustomValidation("NOTE","CB_GENERATE_NOTE,CMB_GENERATE_NOTE");
      f.setMaxLength(2000);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMINSPNOTE: Inspection Note");
      f.setDefaultNotVisible();

      f = headblk.addField("CB_GENERATE_NOTE");
      f.setCheckBox("FALSE,TRUE");
      f.setFunction("GENERATE_NOTE");
      f.setCustomValidation("CB_GENERATE_NOTE","CMB_GENERATE_NOTE");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCBGENNOTE: Generate Note");
      f.setDefaultNotVisible();

      f = headblk.addField("CMB_GENERATE_NOTE");
      f.setSize(14);
      f.setDbName("GENERATE_NOTE");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCMBGENNOTE: Generate Note API");
      f.setHidden();

      f = headblk.addField("DUMMY_GENERATE_NOTE_YES");
      f.setFunction("Generate_Note_API.Decode('1')");
      f.setHidden();

      f = headblk.addField("DUMMY_GENERATE_NOTE_NO");
      f.setFunction("Generate_Note_API.Decode('2')");
      f.setHidden();

      f = headblk.addField("REAL_S_DATE","Datetime");
      f.setSize(25);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMREALSDATE: Actual Start");
      f.setCustomValidation("REAL_S_DATE","REAL_S_DATE");
      f.setDefaultNotVisible();

      f = headblk.addField("PLAN_S_DATE","Datetime");
      f.setSize(25);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMPLANSDATE: Planned Start");
      f.setHidden();

      f = headblk.addField("REAL_F_DATE","Datetime");
      f.setSize(25);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMREALFDATE: Actual Completion");
      f.setCustomValidation("REAL_F_DATE","REAL_F_DATE");
      f.setDefaultNotVisible();

      f = headblk.addField("PLAN_F_DATE","Datetime");
      f.setSize(25);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMPLANFDATE: Planned Completion");
      f.setHidden();

      f = headblk.addField("REQUIRED_START_DATE","Datetime");
      f.setSize(25);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMREQSTARTDATE: Required Start");
      f.setCustomValidation("REQUIRED_START_DATE,REQUIRED_END_DATE","REQUIRED_START_DATE");

      f = headblk.addField("REQUIRED_END_DATE","Datetime");
      f.setSize(25);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMREQENDDATE: Latest Completion");
      f.setCustomValidation("REQUIRED_START_DATE,REQUIRED_END_DATE","REQUIRED_END_DATE");

      f = headblk.addField("CURRENTPOSITION");
      f.setSize(45);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCURRENTPOSITION: Latest Transaction");
      f.setFunction("PART_SERIAL_CATALOG_API.Get_Alt_Latest_Transaction(:MCH_CODE_CONTRACT,:MCH_CODE)");
      f.setReadOnly();

      f = headblk.addField("PRE_ACCOUNTING_ID","Number");
      f.setHidden();

      f = headblk.addField("COMPANY");
      f.setHidden();
      f.setUpperCase();

      f = headblk.addField("REG_DATE","Datetime");
      f.setSize(23);
      f.setMandatory();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMREGDATE: Reg. Date");
      f.setReadOnly();
      f.setInsertable();
      f.setDefaultNotVisible();

      f = headblk.addField("REPORTED_BY");
      f.setSize(15);
      f.setDynamicLOV("EMPLOYEE_LOV",600,450);
      f.setMandatory();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMREPORTEDBY2: Reported by");
      f.setUpperCase();
      f.setReadOnly();
      f.setInsertable();
      f.setMaxLength(20); 

      f = headblk.addField("CUSTOMER_NO"); 
      f.setSize(15);
      f.setDynamicLOV("CUSTOMER_INFO",600,450);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCUSTOMERNO: Customer No");
//        f.setFunction("Sc_Service_Contract_API.Get_Customer_Id(:CONTRACT_ID)");
      f.setUpperCase();
      f.setHilite();
      f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCUNO: List of Customer No"));
      f.setCustomValidation("CUSTOMER_NO,COMPANY","CREDITSTOP,CUSTOMERNAME");

      f = headblk.addField("CREDITSTOP");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("CUSTOMERNAME");
      f.setSize(20);
      f.setHilite();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCUSTOMERNAME: Customer Name");
      //  f.setFunction("CUSTOMER_INFO_API.Get_Name(Sc_Service_Contract_API.Get_Customer_Id(:CONTRACT_ID))");
      f.setFunction("CUSTOMER_INFO_API.Get_Name(:CUSTOMER_NO)");
      f.setReadOnly();
      f.setDefaultNotVisible();

//  Bug 68947, Start
      f = headblk.addField("CONTRACT_ID");              
      f.setUpperCase();
      f.setMaxLength(15);
      f.setLOV("PscContrProductLov.page","CONTRACT,CUSTOMER_NO,MCH_CODE,CUSTOMER_NO,CUSTOMERNAME");
      f.setCustomValidation("CONTRACT_ID,LINE_NO","CONTRACT_ID,CONTRACT_NAME,LINE_NO,LINE_DESC,CUSTOMER_NO,CUSTOMERNAME");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCONTRACTID: Contract ID");
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
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCONTRACTNAME: Contract Name");
      f.setSize(15);

      f = headblk.addField("LINE_NO");
      f.setLOV("PscContrProductLov.page","CONTRACT,CUSTOMER_NO,MCH_CODE,CONTRACT_ID");
      f.setCustomValidation("CONTRACT_ID,LINE_NO","CONTRACT_ID,CONTRACT_NAME,LINE_NO,LINE_DESC,AGREEMENT_ID");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMLINENO: Line No");
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
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTLINEDESC: Description");
      f.setSize(15); 
//  Bug 68947, End

      f = headblk.addField("AGREEMENT_ID");
      f.setSize(15);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMAGREEMENTID: Agreement Id");
      f.setReadOnly();
      f.setHilite();
      f.setUpperCase();
      f.setMaxLength(10);
      f.setDefaultNotVisible();  

      f = headblk.addField("LU_NAME");
      f.setHidden();
      f.setFunction("'ActiveSeparate'");

      f = headblk.addField("KEY_REF");
      f.setHidden();
      f.setFunction("CONCAT('WO_NO=',CONCAT(WO_NO,'^'))");

      f = headblk.addField("STATEVAL");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("PNT");
      f.setFunction("''");
      f.setHidden(); 

      f = headblk.addField("TEMP_TIME","Datetime");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("TEMP_DATE","Date");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("TEMP_DATE_TIME","Datetime");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("DATEDIFF","Number","########.#####");
      f.setFunction("''");
      f.setHidden();  

      //---------------unwanted stuff----------------------

      f = headblk.addField("FAULT_REP_FLAG","Number");
      f.setHidden();

      f = headblk.addField("WO_KEY_VALUE");
      f.setHidden();
      f.setFunction("WO_KEY_VALUE");

      //Fields to handle dynamic LOV's

      f = headblk.addField("CONTRACT1");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("COMPANY1");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("CODEPARTCOSTCENTER");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("VIEWNAME");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("PKGNAME");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("INTERNAME");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("LOGCODEPARTCOSTCENTER");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("CODEPARTB");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("STR_CODE");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("CONTROL_TYPE");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("MODULE");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("CODE_A");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("CODE_B");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("FIXED_PRICE_DB");
      f.setHidden();

      f = headblk.addField("OBJLEVEL");
      f.setFunction("''");
      f.setHidden();

      headblk.addField("HEAD_ISVALID").
      setFunction("''").
      setHidden();

      headblk.addField("HEAD_FACSEQUENCE").
      setFunction("''").
      setHidden();

      headblk.addField("ISVIM").
      setHidden().
      setUpperCase().
      setFunction("''").
      setMaxLength(11);

      headblk.addField("DUMMY_ACT_QTY_ISSUED","Number").
      setFunction("''").
      setHidden();

      //Fields to handle PRINT methods

      //-------------------------------------------------------------

      f = headblk.addField("ERR_DISCOVER_CODE");
      f.setSize(6);
      f.setDynamicLOV("WORK_ORDER_DISC_CODE",600,450);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMERRDISCODE: Discovery");
      f.setUpperCase();
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setMaxLength(3);  

      f = headblk.addField("DISCODESCR");
      f.setSize(27);
      f.setFunction("WORK_ORDER_DISC_CODE_API.Get_Description(:ERR_DISCOVER_CODE)");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMERRDISCODESC: Discovery Description");
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setMaxLength(2000);
      mgr.getASPField("ERR_DISCOVER_CODE").setValidation("DISCODESCR");

      f = headblk.addField("ERR_SYMPTOM");
      f.setSize(6);
      f.setDynamicLOV("WORK_ORDER_SYMPT_CODE",600,450);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMERRSYMPTM: Symptom");
      f.setUpperCase();
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setMaxLength(3);

      f = headblk.addField("SYMPTOMDESCR");
      f.setSize(27);
      f.setFunction("WORK_ORDER_SYMPT_CODE_API.Get_Description(:ERR_SYMPTOM)");
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMSYMTDISCODESC: Symptom Description");
      f.setMaxLength(2000);
      mgr.getASPField("ERR_SYMPTOM").setValidation("SYMPTOMDESCR");  

      f = headblk.addField("ERR_DESCR_LO");
      f.setSize(43);
      f.setHeight(4);
      f.setDefaultNotVisible();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMERRDESCRLO: Description");
      f.setReadOnly();
      f.setMaxLength(2000);

      f = headblk.addField("TEST_POINT_ID");
      f.setSize(10);
      f.setDynamicLOV("EQUIPMENT_OBJECT_TEST_PNT",600,450);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMTESTPOINTID: Test point");
      f.setUpperCase();
      f.setMaxLength(6);
      f.setDefaultNotVisible();

      f = headblk.addField("TESTPOINTIDDES");
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setFunction("EQUIPMENT_OBJECT_TEST_PNT_api.get_description(:CONTRACT,:MCH_CODE,:TEST_POINT_ID)");

      f = headblk.addField("PM_NO","Number");
      f.setSize(8);
      f.setDynamicLOV("PM_ACTION",600,450);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMPMNO: PM No");
      f.setHidden();
      f.setReadOnly();

      f = headblk.addField("PM_REVISION");
      f.setHidden();

      f = headblk.addField("PM_DESCR");
      f.setSize(36);
      f.setDefaultNotVisible();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMPMDESCR: Generation info");
      f.setReadOnly();
      f.setMaxLength(2000);

      f = headblk.addField("HEAD_TEMP");
      f.setHidden();
      f.setFunction("''");   

      headblk.addField("OBJEVENTS").
      setHidden();  

      f = headblk.addField("NON_SERIAL_LOCATION_DB");
      f.setHidden();

      f = headblk.addField("TRANSFERRED");
      f.setFunction("''");
      f.setHidden();

      headblk.addField("CONNECTION_TYPE_DB").
      setHidden();

      headblk.addField("RECEIVE_ORDER_NO").
      setHidden();

      headblk.addField("IDENTITY_TYPE").
      setFunction("''").    
      setHidden();        

      headblk.addField("PARTY_TYPE").
      setFunction("''").    
      setHidden();        

      headblk.addField("HEAD_PART_NO").
      setFunction("''").    
      setHidden();        

      headblk.addField("HEAD_SERIAL_NO").
      setFunction("''").    
      setHidden();        

      headblk.addField("OP_COND").
      setFunction("''").    
      setHidden();        

      headblk.addField("OBJ_STATE").
      setFunction("''").    
      setHidden();

      headblk.addField("ISCONN").
      setFunction("''").    
      setHidden();        

      headblk.addField("CUST_ORDER_NO").
      setHidden();         

      headblk.addField("CUST_ORDER_LINE_NO").
      setHidden();         

      f = headblk.addField("ACTIVITY_SEQ","Number");
      f.setLabel("ACTSEPREPSMHEADACTSEQ: Activity Seq");
      f.setSize(20);
      f.setHidden();

      f = headblk.addField("ACTIVITY_SEQ1","Number","#");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("PROJECT_ID");
      //attaches the function call only if the PROJ is installed.
      if (mgr.isModuleInstalled("PROJ"))
         f.setFunction("Activity_API.Get_Project_Id(:ACTIVITY_SEQ)");
      else
         f.setFunction("''");
      f.setHidden();  

      f = headblk.addField("INCLUDE_STANDARD");
      f.setFunction("''");
      f.setHidden();  

      f = headblk.addField("INCLUDE_PROJECT");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("EXCEPTION_EXISTS");
      //attaches the function call only if the PROJ is installed.
      if (mgr.isModuleInstalled("PROJ"))
         f.setFunction("Activity_API.Object_Exceptions_Exist('ASWO',:WO_NO,NULL,NULL)");
      else
         f.setFunction("''");
      f.setHidden();

//  Bug 68947, Start
      headblk.addField("WORK_TYPE_ID").
      setHidden();
//  Bug 68947, End

      headblk.setView("ACTIVE_SEPARATE");
      headblk.defineCommand("ACTIVE_SEPARATE_API","New__,Modify__,Remove__,RE_INIT__, CONFIRM__ ,TO_PREPARE__ ,PREPARE__,RELEASE__,START_ORDER__,WORK__,REPORT__,FINISH__,CANCEL__,RESTART__,REPLAN__");
      headblk.disableDocMan();
      headset = headblk.getASPRowSet();

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMHD: Report In Work Order for SM"));
      headbar = mgr.newASPCommandBar(headblk);

      headtbl.setWrap();

      //Web Alignment - Multirow Action
      headtbl.enableRowSelect();
      //

      //Web Alignment - Adding Tabs
      tabs = mgr.newASPTabContainer();
      tabs.setTabSpace(5);  
      tabs.setTabWidth(100);

      tabs.addTab("BUDGET","ACTSEPREPBUDGET: Budget","javascript:commandSet('HEAD.activateBudgetTab','')");
      tabs.addTab("TIMEREP","ACTSEPREPTIMEREP: Time Report","javascript:commandSet('HEAD.activateTimeReportTab','')");
      tabs.addTab("MATERIAL","ACTSEPREPMATERIAL: Material","javascript:commandSet('HEAD.activateMaterialTab','')");
      tabs.addTab("JOBS","ACTSEPREPJOBS: Jobs","javascript:commandSet('HEAD.activateJobsTab','')");

      headbar.addCustomCommand("activateBudgetTab", "");
      headbar.addCustomCommand("activateTimeReportTab", "");
      headbar.addCustomCommand("activateMaterialTab", "");  
      headbar.addCustomCommand("activateJobsTab", "");
      //

      headbar.addCustomCommand("prepareWO",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMPREPARE: Prepare..."));
      headbar.addCustomCommand("preposting",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMRPRE: Preposting..."));
      headbar.addCustomCommand("transferToCusOrder",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMTRANCUSORD1: Transfer to Customer Order..."));
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("placeSerial",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMPLACSER: Place Serial in Equipment Structure..."));
      headbar.addCustomCommand("moveSerial",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMMOVESER: Move Serial to Inventory..."));
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("moveNonSerial",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMMOVENONSER: Move Non Serial to Inventory..."));   
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("printWO",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMPRNT: Print..."));
      headbar.addCustomCommand("printAuth",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMPRNTAUTH: Print Authorization..."));
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("reinit",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMFAULTWORK: FaultReprt\\WorkRequest"));
      headbar.addCustomCommand("observed",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMOBSER: Observed"));
      headbar.addCustomCommand("underPrep",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMUNDEPREP: Under Preparation"));
      headbar.addCustomCommand("prepared",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMPREPED: Prepared"));
      headbar.addCustomCommand("released",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMREL: Released"));
      headbar.addCustomCommand("started",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMSTAR: Started"));
      headbar.addCustomCommand("workDone",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMWRKDNE: Work Done"));
      headbar.addCustomCommand("reported",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMREPTED: Reported"));
      headbar.addCustomCommand("finished",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMFINED: Finished"));
      headbar.addCustomCommand("cancelled",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCANED: Cancelled"));

      //Bug 83532, Start
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("workorderaddr", mgr.translate("PCMWACTIVESEPERATEREPORTINSMWORKORDERADDR: Work Order Address..."));
      headbar.addCustomCommandSeparator();
      //Bug 83532, End

      if (mgr.isModuleInstalled("PROJ"))
      {
         headbar.addCustomCommand("connectToActivity","ACTSEPREPSMPROJCONNCONNACT: Connect to Project Activity...");
         headbar.addCustomCommand("disconnectFromActivity","ACTSEPREPSMPROJCONNDISCONNACT: Disconnect from Project Activity");
         headbar.addCustomCommand("projectActivityInfo", mgr.translate("ACTSEPREPSMPRJACTINFO: Project Connection Details..."));
         headbar.addCustomCommand("activityInfo", mgr.translate("ACTSEPREPSMPRJACTVITYINFO: Activity Info..."));
      }
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("mroObjReceiveO",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMMRECEIVEORDER: Create MRO Object Receive Order")); 
      headbar.addCustomCommand("returnObj",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMRETOBJ: Return Object")); 
      headbar.addCustomCommand("returnNonOpObj",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMRETNONOPOBJ: Return Not Operational Object")); 
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("freeNotes",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMFREENOTES: Free Notes..."));
      headbar.addCustomCommand("postings",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMPOSTING: Postings..."));
      headbar.addCustomCommand("requisitions",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMRREQUIS: Purchase Requisitions..."));
      headbar.addCustomCommand("permits",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMPERMITS: Permits..."));
      headbar.addCustomCommand("coInformation",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCOINFORMATION: CO Information..."));
      headbar.addCustomCommand("workCenterLoad",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMWORKCENTLOAD: Work Center Load..."));
      headbar.addCustomCommand("toolsFacilities",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMTF: Tools and Facilities..."));
      headbar.addCustomCommand("returns",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMRETURNS: Returns...")); 
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("structure",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMSTRUCTURE: Structure..."));

      headbar.addCustomCommand("refreshForm","");
      headbar.addCustomCommand("refreshMaterialTab","");

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
      if (mgr.isModuleInstalled("PROJ"))
      {
         headbar.addCommandValidConditions("connectToActivity", "ACTIVITY_SEQ", "Enable", "");
         headbar.addCommandValidConditions("disconnectFromActivity", "ACTIVITY_SEQ", "Disable", "");
         headbar.addCommandValidConditions("projectActivityInfo", "ACTIVITY_SEQ", "Disable", "");
         headbar.addCommandValidConditions("activityInfo", "", "Enable", "");
      }

      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.DUPLICATEROW);

      head_command = headbar.getSelectedCustomCommand();

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      //Bug 67871, Start
      //headlay.setFieldOrder("WO_NO,CONTRACT,CONNECTION_TYPE,MCH_CODE,MCH_CODE_DESCRIPTION,MCH_CODE_CONTRACT,CURRENTPOSITION,CRITICALITY,CRITICALITYDESCRIPTION,ERR_DESCR,CUSTOMER_NO,CUSTOMERNAME,AGREEMENT_ID,ORG_CODE,ORGCODEDESCRIPTION,REG_DATE,STATE,REPORTED_BY,CBWARRANTYONOBJECT,CBHASSTRUCTURE,CBHASDOCUMENTS,CBTRANSTOMOB,WORK_DONE,PERFORMED_ACTION_LO,ERR_CAUSE_LO,ERR_CLASS,ERRCLASSDESCR,ERR_TYPE,ERRTYPEDESC,PERFORMED_ACTION_ID,PERFORMEDACTIONDESC,ERR_CAUSE,ERRCAUSEDESC,REAL_S_DATE,REAL_F_DATE,REQUIRED_START_DATE,REQUIRED_END_DATE,ERR_DISCOVER_CODE,DISCODESCR,ERR_SYMPTOM,SYMPTOMDESCR,ERR_DESCR_LO,TEST_POINT_ID,TESTPOINTIDDES,PM_NO,PM_DESCR,");
      //Bug 67871, End
      // 107820
//  Bug 68947, Start
      //Bug 87766, Start, Added CBINASTRUCTURE
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMGRPLABEL1: General"),"WO_NO,CONTRACT,ERR_DESCR,STATE,CONNECTION_TYPE,MCH_CODE,MCH_CODE_DESCRIPTION,MCH_CODE_CONTRACT,PM_NO,ORG_CODE,ORGCODEDESCRIPTION,CURRENTPOSITION,CRITICALITY,CRITICALITYDESCRIPTION,CUSTOMER_NO,CUSTOMERNAME,CONTRACT_ID,CONTRACT_NAME,LINE_NO,LINE_DESC,AGREEMENT_ID,CBWARRANTYONOBJECT,CBHASSTRUCTURE,CBHASDOCUMENTS,CBINASTRUCTURE,CBTRANSTOMOB",true,true);
      //Bug 87766, End
        //Bug 74288, Start
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMGRPLABEL2: Report In"),"ERR_CLASS,ERRCLASSDESCR,ERR_TYPE,ERRTYPEDESC,PERFORMED_ACTION_ID,PERFORMEDACTIONDESC,ERR_CAUSE,ERRCAUSEDESC,ERR_CAUSE_LO,WORK_DONE,TEST_POINT_ID,TESTPOINTIDDES,PERFORMED_ACTION_LO,REAL_S_DATE,REQUIRED_START_DATE,REAL_F_DATE,REQUIRED_END_DATE",true,false);
//      headlay.defineGroup(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMGRPLABEL3: Detailed Info on Work Done"),"ERR_CAUSE_LO,PERFORMED_ACTION_LO",true,false);
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMGRPLABEL4: Fault Report Info"),"REG_DATE,REPORTED_BY,ERR_DISCOVER_CODE,DISCODESCR,ERR_SYMPTOM,SYMPTOMDESCR,ERR_DESCR_LO",true,false);
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMGRPLABEL5: PM Information"),"NOTE,CB_GENERATE_NOTE,CMB_GENERATE_NOTE",true,false);
        //Bug 74288, End

      headlay.setDialogColumns(2);

      headlay.setSimple("MCH_CODE_DESCRIPTION");
      headlay.setSimple("CRITICALITYDESCRIPTION");
      headlay.setSimple("ERRCLASSDESCR");
      headlay.setSimple("ERRTYPEDESC");
      headlay.setSimple("PERFORMEDACTIONDESC");
      headlay.setSimple("ERRCAUSEDESC");
      headlay.setSimple("ORGCODEDESCRIPTION");
      headlay.setSimple("CUSTOMERNAME");
      headlay.setSimple("DISCODESCR");
      headlay.setSimple("SYMPTOMDESCR");
      headlay.setSimple("TESTPOINTIDDES");
      headlay.setSimple("CONTRACT_NAME");
      headlay.setSimple("LINE_DESC");
//  Bug 68947, End

      //Web Alignment - Replacing Links with RMBs
      headbar.addCustomCommandGroup("OBJSTATUS",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMWOSTATUS: Work Order Status"));
      headbar.setCustomCommandGroup("reinit", "OBJSTATUS");
      headbar.setCustomCommandGroup("observed","OBJSTATUS"); 
      headbar.setCustomCommandGroup("underPrep","OBJSTATUS"); 
      headbar.setCustomCommandGroup("prepared","OBJSTATUS"); 
      headbar.setCustomCommandGroup("workDone","OBJSTATUS"); 
      headbar.setCustomCommandGroup("reported","OBJSTATUS"); 
      headbar.setCustomCommandGroup("finished","OBJSTATUS"); 
      headbar.setCustomCommandGroup("cancelled","OBJSTATUS"); 
      headbar.setCustomCommandGroup("released","OBJSTATUS"); 
      headbar.setCustomCommandGroup("started","OBJSTATUS");

      if (mgr.isModuleInstalled("PROJ"))
      {
         headbar.addCustomCommandGroup("PROJCONNGRP", mgr.translate("ACTSEPREPSMPROJCONNGRP: Project Connection"));
         headbar.setCustomCommandGroup("connectToActivity", "PROJCONNGRP");
         headbar.setCustomCommandGroup("disconnectFromActivity", "PROJCONNGRP");
         headbar.setCustomCommandGroup("projectActivityInfo", "PROJCONNGRP");
         headbar.setCustomCommandGroup("activityInfo", "PROJCONNGRP");
      }

      //Web Alignment - Multirow Action
      headbar.enableMultirowAction();
      headbar.removeFromMultirowAction("preposting");
      headbar.removeFromMultirowAction("placeSerial");
      headbar.removeFromMultirowAction("moveSerial");
      headbar.removeFromMultirowAction("moveNonSerial");
      headbar.removeFromMultirowAction("printAuth");
      headbar.removeFromMultirowAction("mroObjReceiveO");
      headbar.removeFromMultirowAction("returnObj");
      headbar.removeFromMultirowAction("returnNonOpObj");
      headbar.removeFromMultirowAction("freeNotes");
      headbar.removeFromMultirowAction("postings");
      headbar.removeFromMultirowAction("requisitions");
      headbar.removeFromMultirowAction("permits");
      headbar.removeFromMultirowAction("coInformation");
      headbar.removeFromMultirowAction("workCenterLoad");
      headbar.removeFromMultirowAction("toolsFacilities");
      headbar.removeFromMultirowAction("returns");
      headbar.removeFromMultirowAction("structure");

//		Bug 83532, Start
      headbar.removeFromMultirowAction("workorderaddr");
//		Bug 83532, Start

      if (mgr.isModuleInstalled("PROJ"))
      {
         headbar.removeFromMultirowAction("connectToActivity");
         headbar.removeFromMultirowAction("disconnectFromActivity");
         headbar.removeFromMultirowAction("projectActivityInfo");
         headbar.removeFromMultirowAction("activityInfo");

         headbar.defineCommand("connectToActivity","connectToActivity","connectToActivityClient");
         headbar.defineCommand("disconnectFromActivity","disconnectFromActivity","disconnectFromActivityClient");
      }

      headbar.defineCommand("prepared","prepared","preparedClient(i)");

      headbar.addCommandValidConditions("moveNonSerial", "NON_SERIAL_LOCATION_DB", "Disable", "I;N;S;null", false);
      headbar.appendCommandValidConditions("moveNonSerial", "WO_NO", "Disable", "null");

      headbar.addCommandValidConditions("transferToCusOrder", "WO_NO", "Disable", "null", false);
      headbar.appendCommandValidConditions("transferToCusOrder", "CUSTOMER_NO", "Disable", "null");
      //

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


      //-----------------------------------------------------------------------
      //---------- This block used to update RMB list box in Header------------
      //-----------------------------------------------------------------------
      //This block is for print purposes 

      prntblk = mgr.newASPBlock("PRINT");
      prntblk.addField("ATTR0");
      prntblk.addField("ATTR1");
      prntblk.addField("ATTR2");
      prntblk.addField("ATTR3");
      prntblk.addField("ATTR4");
      prntblk.addField("RESULT_KEY");      

      //===============================================================================

      itemblk0 = mgr.newASPBlock("ITEM0");

      f = itemblk0.addField("ITEM0_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk0.addField("ITEM0_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk0.addField("ITEM0_OBJSTATE");
      f.setHidden();
      f.setDbName("OBJSTATE");

      f = itemblk0.addField("ITEM0_OBJEVENTS");
      f.setHidden();
      f.setDbName("OBJEVENTS");


      f = itemblk0.addField("ITEM0_ERR_CLASS");
      f.setSize(6);
      f.setLOV("WorkOrderClassCodeLov.page",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMERRCLASS: Class");
      f.setUpperCase();
      f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMERCL: List of Class"));

      f = itemblk0.addField("ITEM0_WO_NO","Number","#");
      f.setSize(7);
      f.setHidden();
      f.setDbName("WO_NO");

      f = itemblk0.addField("ITEM0_MCH_CODE");
      f.setHidden();
      f.setDbName("MCH_CODE");
      f.setUpperCase();

      f = itemblk0.addField("ITEM0_CONTRACT");
      f.setHidden();
      f.setDbName("CONTRACT");
      f.setUpperCase();

      f = itemblk0.addField("ITEM0_AGREEMENT_ID");
      f.setSize(16);
      f.setLOV("../orderw/CustomerAgreementLov.page",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMAGREEMENTID: Agreement Id");
      f.setUpperCase();
      f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMAGID: List of Agreement Id"));

      f = itemblk0.addField("AUTHORIZE_CODE");
      f.setSize(16);
      f.setLOV("../mpccow/OrderCoordinatorLovLov.page",600,445);
      //Bug 84436, Start
      if (mgr.isModuleInstalled("SRVCON"))
         f.setFunction("Sc_Service_Contract_API.Get_Authorize_Code(:CONTRACT_ID)");
      else
         f.setFunction("''");
      //Bug 84436, End  
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMAUTHORIZECODE: Coordinator");
      f.setUpperCase();

      itemblk0.setView("ACTIVE_SEPARATE");

      itemblk0.defineCommand("ACTIVE_SEPARATE_API","New__,Modify__,Remove__,CONFIRM__");
      itemset0 = itemblk0.getASPRowSet();

      itembar0 = mgr.newASPCommandBar(itemblk0);

      itemtbl0 = mgr.newASPTable(itemblk0);

      itembar0.addCustomCommand("none","");


      //=================================Time Report Tab======================================================

      itemblk1 = mgr.newASPBlock("ITEM1");

      f = itemblk1.addField("ITEM1_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk1.addField("ITEM1_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk1.addField("CRE_DATE","Date");
      f.setSize(13);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCREDATE: Creation Date");
      f.setReadOnly();
      f.setInsertable();
      f.setDefaultNotVisible();

      f = itemblk1.addField("ITEM1_COMPANY");
      f.setHidden();
      f.setDbName("COMPANY");

      f = itemblk1.addField("EMP_NO");
      f.setSize(11);
      f.setLOV("../mscomw/MaintEmployeeLov.page","ITEM1_COMPANY COMPANY",600,450);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMEMPNO2: Employee ID");
      f.setUpperCase();
      f.setCustomValidation("EMP_NO,ITEM1_COMPANY,CUSTOMER_NO,ITEM1_CONTRACT,CATALOG_NO,ITEM1_ORG_CODE,ROLE_CODE","EMP_NO,ROLE_CODE,EMP_SIGNATURE,NAME,ITEM1_ORG_CODE,ITEM1_CONTRACT,CMNT,CATALOG_NO,SALESPARTCATALOGDESC");
      f.setMaxLength(11);

      f = itemblk1.addField("EMP_SIGNATURE");
      f.setSize(8);
      f.setDynamicLOV("EMPLOYEE_LOV",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMEMPSIGNATURE: Signature");
      f.setUpperCase();

      f = itemblk1.addField("NAME");
      f.setSize(16);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMNAME: Name");
      f.setFunction("Person_Info_API.Get_Name(:EMP_SIGNATURE)");
      f.setDefaultNotVisible();

      f = itemblk1.addField("ITEM1_ORG_CODE");
      f.setSize(11);
      f.setLOV("../mscomw/OrgCodeAllowedSiteLov.page","ITEM1_CONTRACT CONTRACT",600,445);
      f.setCustomValidation("ITEM1_ORG_CODE,ITEM1_WO_NO,ITEM1_CONTRACT,AMOUNT,QTY,ROLE_CODE,CATALOG_NO,SALESPARTCATALOGDESC,CMNT,NAME,CONTRACT,EMP_NO,ROLE_CODE","ITEM1_ORG_CODE,ITEM1_CONTRACT,AMOUNT,CMNT,CATALOG_NO,SALESPARTCATALOGDESC");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMITEM1ORGCODE: Maintenance Organization");
      f.setDbName("ORG_CODE");
      f.setMandatory();
      f.setUpperCase();
      f.setMaxLength(8);
      f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMORCO: List of Maintenance Organization"));

      f = itemblk1.addField("ITEM1_CONTRACT");
      f.setSize(11);
      f.setCustomValidation("ITEM1_CONTRACT","ITEM1_COMPANY");
      f.setMandatory();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMITEM1CONTRACT: Maint. Org Site");
      f.setDbName("CONTRACT");
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV");
      f.setUpperCase();

      f = itemblk1.addField("COMPANY_VAR");
      f.setHidden();
      f.setFunction("''");

      f = itemblk1.addField("ROLE_CODE");
      f.setSize(13);
      f.setLOV("../mscomw/RoleToSiteLov.page","ITEM1_CONTRACT CONTRACT",600,445);
      f.setCustomValidation("CUSTOMER_NO,ITEM1_CONTRACT,CATALOG_NO,PRICE_LIST_NO,AGREEMENT_ID,QTY1,QTY_TO_INVOICE,QTY,ITEM1_ORG_CODE,ROLE_CODE,AMOUNT,CMNT,NAME,SALESPARTCOST,LIST_PRICE,EMP_NO,CONTRACT,CATALOG_EXIST","ROLE_CODE,ITEM1_CONTRACT,LIST_PRICE,PRICE_LIST_NO,AMOUNT,AMOUNTSALES,CMNT,CATALOG_NO,SALESPARTCATALOGDESC,DISCOUNT");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMROLECODE: Craft");
      f.setUpperCase();
      f.setMaxLength(10);
      f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMROCO: List of Craft"));

      f = itemblk1.addField("TEAM_CONTRACT");
      f.setSize(7);
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV","ITEM2_COMPANY COMPANY",600,450);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMTEAMCONT: Maint. Team Site");
      f.setMaxLength(5);
      f.setInsertable();
      f.setUpperCase();

      f = itemblk1.addField("TEAM_ID");
      f.setSize(13);
      f.setCustomValidation("TEAM_ID,TEAM_CONTRACT","TEAMDESC");
      f.setDynamicLOV("MAINT_TEAM","TEAM_CONTRACT CONTRACT",600,450);
      f.setMaxLength(20);
      f.setInsertable();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMTEAMID: Team ID");
      f.setUpperCase();

      f = itemblk1.addField("TEAMDESC");
      f.setFunction("Maint_Team_API.Get_Description(:TEAM_ID,:TEAM_CONTRACT)");
      f.setSize(40);
      f.setMaxLength(200);
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMTEAMDESC: Team Description");

      f = itemblk1.addField("CAT_CONTRACT");
      f.setFunction("''");
      f.setHidden();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCATCONTRACT: Sales Part Site");

      f = itemblk1.addField("CATALOG_NO");
      f.setSize(17);
      if (orderInst)
         f.setDynamicLOV("SALES_PART_SERVICE_LOV","CAT_CONTRACT CONTRACT",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCATALOGNO: Sales Part");
      f.setUpperCase();
      f.setCustomValidation("QTY_TO_INVOICE,QTY,ITEM1_CONTRACT,CATALOG_NO,CUSTOMER_NO,AGREEMENT_ID,PRICE_LIST_NO,QTY1,SALESPARTCATALOGDESC,NAME,ITEM1_ORG_CODE,ROLE_CODE,CURRENCY_CODE,EMP_NO","LIST_PRICE,AMOUNT,AMOUNTSALES,PRICE_LIST_NO,CMNT,SALESPARTCATALOGDESC,SALESPARTCOST,DISCOUNT");
      f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCANO: List of Sales Part Number"));

      f = itemblk1.addField("SALESPARTCATALOGDESC");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMSALESPARTCATALOGDESC: Description");
      if (orderInst)
         f.setFunction("SALES_PART_API.Get_Catalog_Desc(:ITEM1_CONTRACT,:CATALOG_NO)");
      else
         f.setFunction("''");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk1.addField("CATALOG_EXIST","Number");
      f.setFunction("''");
      f.setHidden();  

      f = itemblk1.addField("DEPART");
      f.setFunction("''");
      f.setHidden();  

      f = itemblk1.addField("CONTRACT_VAR");
      f.setHidden();  
      f.setFunction("''");

      f = itemblk1.addField("COST1","Money");
      f.setHidden();  
      f.setFunction("''");

      f = itemblk1.addField("COST2","Money");
      f.setHidden();  
      f.setFunction("''");

      f = itemblk1.addField("COST3","Money");
      f.setHidden();  
      f.setFunction("''");

      f = itemblk1.addField("PRICE_LIST_NO");
      f.setSize(13);
      if (orderInst)
         f.setDynamicLOV("SALES_PRICE_LIST","ITEM1_CONTRACT CONTRACT",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMPRICELISTNO: Price List No");
      f.setDefaultNotVisible();
      f.setUpperCase();
      f.setCustomValidation("QTY_TO_INVOICE,QTY,ITEM1_CONTRACT,CATALOG_NO,CUSTOMER_NO,AGREEMENT_ID,PRICE_LIST_NO,QTY1,LIST_PRICE","LIST_PRICE,AMOUNTSALES,PRICE_LIST_NO,AGREEMENT_PRICE_FLAG,DISCOUNT");
      f.setMaxLength(10);
      f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMPRLINO: List of Price List No"));

      f = itemblk1.addField("QTY","Number");
      f.setSize(11);
      f.setMandatory();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMQTY: Hours");
      f.setCustomValidation("ITEM1_CONTRACT,CATALOG_NO,ROLE_CODE,ITEM1_ORG_CODE,QTY,SALESPARTCOST,QTY_TO_INVOICE,CUSTOMER_NO,AGREEMENT_ID,PRICE_LIST_NO,QTY,LIST_PRICE,EMP_NO","LIST_PRICE,AMOUNTSALES,SALESPARTCOST,AMOUNT,DISCOUNT,ITEM1DISCOUNTEDPRICE");

      f = itemblk1.addField("SALESPARTCOST","Money");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMSALESPARTCOST: Cost");
      f.setFunction("''");
      f.setReadOnly();
      f.setDefaultNotVisible();

      f = itemblk1.addField("AMOUNT","Money");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMAMOUNT: Cost Amount");
      f.setReadOnly();

      f = itemblk1.addField("LIST_PRICE","Money");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMLISTPRICE: Sales Price");
      f.setCustomValidation("LIST_PRICE","AGREEMENT_PRICE_FLAG");
      f.setReadOnly();

      f = itemblk1.addField("DISCOUNT","Number");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMDISC: Discount%");
      f.setCustomValidation("DISCOUNT","AGREEMENT_PRICE_FLAG");
      f.setDefaultNotVisible();
      f.setReadOnly();

      f = itemblk1.addField("ITEM1DISCOUNTEDPRICE", "Money");
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setLabel("PCMWACTIVESEPARATE2REPORTINWORKORDERSMDISCOUNTEDPRICEITEM1: Discounted Price");
      f.setFunction("LIST_PRICE - (NVL(DISCOUNT, 0)/100*LIST_PRICE)");

      f = itemblk1.addField("AMOUNTSALES","Money");
      f.setSize(17);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMAMOUNTSALES: Price Amount");
      f.setFunction("(LIST_PRICE*QTY)");
      f.setReadOnly();

      f = itemblk1.addField("CMNT");
      f.setCustomValidation("CMNT","CMNT");
      f.setSize(30); 
      f.setHeight(4); 
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCMNT: Comment");
      f.setDefaultNotVisible();
      f.setMaxLength(80); 

      f = itemblk1.addField("ITEM1_WO_NO","Number","#");
      f.setLOV("ActiveWorkOrderLov.page",600,445);
      f.setMandatory();
      f.setHidden();
      f.setDbName("WO_NO");

      f = itemblk1.addField("ROW_NO","Number");
      f.setSize(11);
      f.setHidden();

      f = itemblk1.addField("ORCODE");
      f.setHidden();
      f.setFunction("''");

      f = itemblk1.addField("WORK_ORDER_BOOK_STATUS");
      f.setHidden();

      f = itemblk1.addField("WORK_ORDER_COST_TYPE");
      f.setMandatory();
      f.setHidden();

      f = itemblk1.addField("WORK_ORDER_ACCOUNT_TYPE");
      f.setMandatory();
      f.setHidden();

      f = itemblk1.addField("AGREEMENT_PRICE_FLAG","Number");
      f.setHidden();

      f = itemblk1.addField("BASE_PRICE","Money");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("SALE_PRICE","Money");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("PLAN_LINE_NO","Number");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMPLLINENO: Plan Line No");
      f.setDefaultNotVisible();
      f.setDynamicLOV("WORK_ORDER_ROLE_PLANNING_LOV","ITEM1_WO_NO WO_NO",600,445);
      f.setCustomValidation("WO_NO,PLAN_LINE_NO","TEAM_CONTRACT,TEAM_ID");

      f = itemblk1.addField("CURRENCY_RATE","Number");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("QTY1","Number");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("QTY_TO_INVOICE","Number");
      f.setHidden();

      f = itemblk1.addField("LINE_DESCRIPTION");
      f.setHidden();

      f = itemblk1.addField("ALTERNATIVE_CUSTOMER");
      f.setDynamicLOV("CUSTOMER_INFO");
      f.setSize(20);
      f.setUpperCase();
      f.setDefaultNotVisible();
      f.setCustomValidation("ALTERNATIVE_CUSTOMER","AGREEMENT_PRICE_FLAG");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMALTERNATIVECUSTOMER: Alternative Customer");

      f = itemblk1.addField("CURRENCY_CODE");
      f.setHidden();
      if (orderInst)
         f.setFunction("nvl(Customer_Agreement_API.Get_Currency_Code(Active_Work_Order_API.Get_Agreement_Id(WO_NO)),Cust_Ord_Customer_API.Get_Currency_Code(CUSTOMER_NO))");
      else
         f.setFunction("''");

      f = itemblk1.addField("IS_SEPARATE");
      f.setFunction("ACTIVE_SEPARATE_API.Is_Separate(:WO_NO)");
      f.setHidden();

      f = itemblk1.addField("DUMMY");
      f.setFunction("''");
      f.setHidden();

      itemblk1.setView("WORK_ORDER_CODING");
      itemblk1.defineCommand("WORK_ORDER_CODING_API","New__,Modify__,Remove__");
      itemset1 = itemblk1.getASPRowSet();

      itemblk1.setMasterBlock(headblk);

      itembar1 = mgr.newASPCommandBar(itemblk1);

      itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
      itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1");
      itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
      itembar1.defineCommand(itembar1.SAVERETURN,"saveReturnItem1","checkMando()");
      //Bug id 81023, start
      itembar1.defineCommand(itembar1.SAVENEW,"saveNewItem1","checkMando()");
      //Bug id 81023, end
      itembar1.defineCommand(itembar1.DELETE,"deleteITEM1");

      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setWrap(); 

      //Web Alignment - Multirow Action
      itemtbl1.enableRowSelect();
      //

      //Web Alignment - replacing links with RMBs
      itembar1.addCustomCommand("createFromPlanTime",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCREFROMPLANNING: Create From Planning..."));
      itembar1.addCustomCommand("connectToPlanTime",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCONNRECOFROPLSM: Connect/Reconnect To Planning..."));
      itembar1.addCustomCommand("createFromAllocation",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCREATETIMEALLOC: Create From Allocation..."));

      //itembar1.addCommandValidConditions("createFromAllocation",   "IS_SEPARATE",  "Enable",  "TRUE");

      itembar1.forceEnableMultiActionCommand("createFromPlanTime");
      itembar1.forceEnableMultiActionCommand("createFromAllocation");

      // 031224  ARWILK  Begin  (Links with multirow RMB's)
      itembar1.enableMultirowAction();
      itembar1.removeFromMultirowAction("connectToPlanTime");
      // 031224  ARWILK  End  (Links with multirow RMB's)


      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);
      itemlay1.setDialogColumns(3);

      // --------------------------------------------------------------------------------------------
      // --------------------------------------------------------------------------------------------
      // --------------------------------------------------------------------------------------------

      itemblk2 = mgr.newASPBlock("ITEM2");

      f = itemblk2.addField("ITEM2_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk2.addField("ITEM2_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk2.addField("ITEM2_CONTRACT");
      f.setSize(11);
      f.setLOV("../mpccoW/UserAllowedSiteLovLov.page",600,445);
      f.setHidden();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMITEM2CONTRACT: Site");
      f.setDbName("CONTRACT");
      f.setUpperCase();

      f = itemblk2.addField("ITEM2_COMPANY");
      f.setHidden();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMITEM2COMPANY: Company Id");
      f.setDbName("COMPANY");

      f = itemblk2.addField("ITEM2_WORK_ORDER_COST_TYPE");
      f.setSize(11);
      f.setMandatory();
      f.setSelectBox();
      f.enumerateValues("WORK_ORDER_COST_TYPE_API");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMITEM2WORKORDETTYPE: Cost Type");
      f.setDbName("WORK_ORDER_COST_TYPE");
      f.setCustomValidation("ITEM2_WORK_ORDER_COST_TYPE,ITEM2_WO_NO","ITEM2_AMOUNT,CLIENTVAL1,CLIENTVAL2,ACTIVEWORKORDERFIXEDPRICE,NOTFIXEDPRICE,ITEM2_COST_CENTER,ITEM2_OBJECT_NO,ITEM2_PROJECT_NO");


      f = itemblk2.addField("ITEM2_CATALOG_NO");
      f.setSize(17);
      f.setLOV("../orderw/SalesPartLov.page","CONTRACT",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMITEM2CATALOGNO: Sales Part Number");
      f.setDbName("CATALOG_NO");
      f.setUpperCase();
      f.setCustomValidation("ITEM2_CATALOG_NO,ITEM2_PRICE_LIST_NO,ITEM2_QTY,ITEM2_WORK_ORDER_COST_TYPE,ITEM2_CONTRACT,CUSTOMER_NO,AGREEMENT_ID,ITEM2_AMOUNT","LINEDESCRIPTION,STRING,ITEM2_AMOUNT");
      f.setMaxLength(25);
      f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCATNO: List of Sales Part Number"));

      f = itemblk2.addField("LINEDESCRIPTION");
      f.setSize(16);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMLINEDESCRIPTION: Description");
      f.setReadOnly();
      if (orderInst)
         f.setFunction("substr(nvl(LINE_DESCRIPTION, Sales_Part_API.Get_Catalog_Desc(CONTRACT,CATALOG_NO)), 1, 35)");
      else
         f.setFunction("LINE_DESCRIPTION");

      f = itemblk2.addField("ORDER_NO");
      f.setSize(16);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCUSTORDNO: Customer Order No");
      f.setReadOnly();
      f.setUpperCase();

      f = itemblk2.addField("ITEM2_PRICE_LIST_NO");
      f.setSize(13);
      f.setLOV("../orderw/SalesPriceListLov.page",600,445);
      f.setCustomValidation("ITEM2_PRICE_LIST_NO,ITEM2_QTY,ITEM2_CONTRACT,ITEM2_CATALOG_NO,CUSTOMER_NO,AGREEMENT_ID","SALESPRICEAMOUNT,ITEM2_AGREEMENT_PRICE_FLAG");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMITEM2PRICELISTNO: Price List No");
      f.setDbName("PRICE_LIST_NO");
      f.setUpperCase();
      f.setMaxLength(10);
      f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMPRLINO2: List of Price List No"));

      f = itemblk2.addField("ITEM2_QTY","Number");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMITEM2QTY: Hours/Qty");
      f.setCustomValidation("ITEM2_PRICE_LIST_NO,ITEM2_QTY,ITEM2_WORK_ORDER_COST_TYPE,ITEM2_CONTRACT,ITEM2_CATALOG_NO,CUSTOMER_NO,AGREEMENT_ID","SALESPRICEAMOUNT");
      f.setDbName("QTY");

      f = itemblk2.addField("ITEM2_LIST_PRICE","Number");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMITEM2LISTPRICE: Sales Price");
      f.setCustomValidation("ITEM2_LIST_PRICE,ITEM2_QTY","SALESPRICEAMOUNT,ITEM2_AGREEMENT_PRICE_FLAG");
      f.setDbName("LIST_PRICE");

      f = itemblk2.addField("ITEM2_WORK_ORDER_ACCOUNT_TYPE");
      f.setSize(12);
      f.setHidden();
      f.setDbName("WORK_ORDER_ACCOUNT_TYPE");

      f = itemblk2.addField("SALESPRICEAMOUNT","Number");
      f.setSize(17);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMSALESPRICEAMOUNT: Sales Price Amount");
      f.setFunction("(LIST_PRICE*QTY)");
      f.setReadOnly();

      f = itemblk2.addField("ITEM2_AMOUNT","Number");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMITEM2AMOUNT: Cost Amount");
      f.setDbName("AMOUNT");
      f.setReadOnly();
      f.setInsertable();

      f = itemblk2.addField("AMOUNT_VAR","Number");
      f.setHidden();
      f.setFunction("''");

      f = itemblk2.addField("CONTRACT_VAR1");
      f.setHidden();
      f.setFunction("''");

      f = itemblk2.addField("ITEM2_AGREEMENT_PRICE_FLAG","Number");
      f.setSize(22);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMITEM2USEPRICELOGIC: Use Price Logic");
      f.setCheckBox("0,1");
      f.setDbName("AGREEMENT_PRICE_FLAG");
      f.setReadOnly();

      f = itemblk2.addField("KEEP_REVENUE");
      f.setSize(14);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMKEEPREVENUE: Keep Revenue");
      f.setSelectBox();
      f.enumerateValues("KEEP_REVENUE_API");

      f = itemblk2.addField("ITEM2_WORK_ORDER_BOOK_STATUS");
      f.setSize(13);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMITEM2WORKORDERBOOKSTATUS: Booking Status");
      f.setDbName("WORK_ORDER_BOOK_STATUS");
      f.setReadOnly();

      f = itemblk2.addField("SIGNATURE");
      f.setSize(13);
      f.setLOV("../mscomw/EmployeeLovLov.page","ITEM2_COMPANY COMPANY",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMAUTHSIGNATURE: Auth Signature");
      f.setUpperCase();
      f.setMaxLength(20);
      f.setReadOnly();

      f = itemblk2.addField("SIGNATURE_ID");
      f.setSize(13);
      f.setHidden();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMSIGNATUREID: Auth Signature");
      f.setUpperCase();

      f = itemblk2.addField("ITEM2_CMNT");
      f.setSize(19);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMITEM2CMNT: Comment");
      f.setDbName("CMNT");
      f.setMaxLength(80);

      f = itemblk2.addField("ACCNT");
      f.setSize(11);
      f.setLOV("../accruw/AccountLov.page","ITEM2_COMPANY COMPANY",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMACCNT: Account");
      f.setMaxLength(10);

      f = itemblk2.addField("ITEM2_COST_CENTER");
      f.setSize(11);
      f.setLOV("../accruw/CodeBLov.page","ITEM2_COMPANY COMPANY",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMITEM2COSTCENTER: Cost Center");
      f.setDbName("COST_CENTER");
      f.setMaxLength(10);

      f = itemblk2.addField("ITEM2_PROJECT_NO");
      f.setSize(11);
      f.setLOV("../accruw/CodeFLov.page","ITEM2_COMPANY COMPANY",600,445);
      f.setDbName("PROJECT_NO");
      f.setMaxLength(10);

      f = itemblk2.addField("ITEM2_OBJECT_NO");
      f.setSize(11);
      f.setLOV("../accruw/CodeELov.page","ITEM2_COMPANY COMPANY",600,445);
      f.setDbName("OBJECT_NO");
      f.setMaxLength(10);

      f = itemblk2.addField("CODE_C");
      f.setSize(11);
      f.setLOV("../accruw/CodeCLov.page","ITEM2_COMPANY COMPANY",600,445);
      f.setMaxLength(10);

      f = itemblk2.addField("CODE_D");
      f.setSize(11);
      f.setLOV("../accruw/CodeDLov.page","ITEM2_COMPANY COMPANY",600,445);
      f.setMaxLength(10);

      f = itemblk2.addField("CODE_E");
      f.setHidden();
      f.setFunction("''");

      f = itemblk2.addField("CODE_F");
      f.setHidden();
      f.setFunction("''");
      f.setMaxLength(10);

      f = itemblk2.addField("CODE_G");
      f.setSize(11);
      f.setLOV("../accruw/CodeGLov.page","ITEM2_COMPANY COMPANY",600,445);
      f.setMaxLength(10);

      f = itemblk2.addField("CODE_H");
      f.setSize(11);
      f.setLOV("../accruw/CodeHLov.page","ITEM2_COMPANY COMPANY",600,445);
      f.setMaxLength(10);

      f = itemblk2.addField("CODE_I");
      f.setSize(11);
      f.setLOV("../accruw/CodeILov.page","ITEM2_COMPANY COMPANY",600,445);
      f.setMaxLength(10);

      f = itemblk2.addField("CODE_J");
      f.setSize(11);
      f.setLOV("../accruw/CodeJLov.page","ITEM2_COMPANY COMPANY",600,445);
      f.setMaxLength(10);

      f = itemblk2.addField("ITEM2_ROW_NO","Number");
      f.setHidden();
      f.setDbName("ROW_NO");

      f = itemblk2.addField("CDOCUMENTTEXT");
      f.setSize(14);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCDOCUMENTTEXT: Document Text");
      f.setFunction("Document_Text_API.Note_Id_Exist(:NOTE_ID)");
      f.setCheckBox("0,1");
      f.setReadOnly();

      f = itemblk2.addField("REQUISITION_NO");
      f.setSize(13);
      f.setLOV("WorkOrderRequisHeaderLov.page","WO_NO",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMREQUISITIONNO: Requisition No");
      f.setMaxLength(12);
      f.setReadOnly();


      f = itemblk2.addField("REQUISITION_LINE_NO");
      f.setSize(16);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMREQUISITIONLINENO: Requisition Line No");
      f.setMaxLength(4);

      f = itemblk2.addField("REQUISITION_RELEASE_NO");
      f.setSize(21);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMREQUISITIONRELEASENO: Requisition Release No");
      f.setMaxLength(4);

      f = itemblk2.addField("REQUISITIONVENDORNO");
      f.setSize(20);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMREQUISITIONVENDORNO: Requisition Supplier No");
      f.setFunction("Purchase_Req_Util_API.Get_Line_Vendor_No (:REQUISITION_NO,:REQUISITION_LINE_NO,:REQUISITION_RELEASE_NO)");
      mgr.getASPField("REQUISITION_NO").setValidation("REQUISITIONVENDORNO");

      f = itemblk2.addField("ITEM2_EMP_NO");
      f.setSize(9);
      f.setHidden();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMITEM2EMPNO: Employee");
      f.setDbName("EMP_NO");
      f.setUpperCase();

      f = itemblk2.addField("ITEM2_WO_NO","Number","#");
      f.setSize(11);
      f.setMandatory();
      f.setHidden();
      f.setDbName("WO_NO");

      f = itemblk2.addField("CATALOGDESC");
      f.setSize(11);
      f.setHidden();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCATALOGDESC: Description");
      if (orderInst)
         f.setFunction("SALES_PART_API.Get_Catalog_Desc(:ITEM2_CONTRACT,:ITEM2_CATALOG_NO)");
      else
         f.setFunction("''");

      f = itemblk2.addField("NOTE_ID","Number");
      f.setSize(11);
      f.setHidden();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMNOTEID: Note ID");

      f = itemblk2.addField("CLIENTVAL1");
      f.setHidden();
      f.setFunction("''");    

      f = itemblk2.addField("CLIENTVAL2");
      f.setHidden();
      f.setFunction("''");    

      f = itemblk2.addField("CLIENTVAL3");
      f.setHidden();
      f.setFunction("''");

      f = itemblk2.addField("CLIENTVAL4");
      f.setHidden();
      f.setFunction("''");

      f = itemblk2.addField("STRING");
      f.setHidden();
      f.setFunction("''");

      f = itemblk2.addField("COSTCENT");
      f.setHidden();
      f.setFunction("''");

      f = itemblk2.addField("ACTIVEWORKORDERFIXEDPRICE");
      f.setSize(11);
      f.setHidden();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMACTIVEWORKORDERFIXEDPRICE: Fixed Price");
      f.setFunction("ACTIVE_WORK_ORDER_API.Get_Fixed_Price(:ITEM2_WO_NO)");

      f = itemblk2.addField("NOTFIXEDPRICE");
      f.setSize(11);
      f.setHidden();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMNOTFIXEDPRICE: Not Fixed Price");
      f.setFunction("Fixed_Price_API.Decode('1')");

      itemblk2.setView("WORK_ORDER_CODING");
      itemblk2.defineCommand("WORK_ORDER_CODING_API","New__,Modify__,Remove__");
      itemset2 = itemblk2.getASPRowSet();

      itembar2 = mgr.newASPCommandBar(itemblk2);

      itemtbl2 = mgr.newASPTable(itemblk2);


      //-----------------------------------------------------------------------
      //-------------- This part belongs to MATERIALS TAB -----------------------
      //-----------------------------------------------------------------------

      itemblk3 = mgr.newASPBlock("ITEM3");

      f = itemblk3.addField("ITEM3_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk3.addField("ITEM3_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk3.addField("ITEM3_OBJSTATE");
      f.setHidden();
      f.setDbName("OBJSTATE");

      f = itemblk3.addField("ITEM3_OBJEVENTS");
      f.setHidden();
      f.setDbName("OBJEVENTS");

      f = itemblk3.addField("MAINT_MATERIAL_ORDER_NO","Number","#");
      f.setSize(12);
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMMAINT_MATERIAL_ORDER_NO: Order No");

      f = itemblk3.addField("ITEM3_WO_NO","Number","#");
      f.setSize(11);
      f.setDbName("WO_NO");
      f.setMaxLength(8);
      f.setReadOnly();
      f.setCustomValidation("ITEM3_WO_NO","DUE_DATE,NPREACCOUNTINGID,ITEM3_CONTRACT,ITEM3_COMPANY,MCHCODE,ITEM3DESCRIPTION");
      f.setDynamicLOV("ACTIVE_WO_NOT_REPORTED",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMWO_NO: WO No");

      f = itemblk3.addField("MCHCODE");
      f.setSize(13);
      f.setHidden();
      f.setReadOnly();
      f.setMaxLength(2000);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMMCHCODE: Object ID");
      f.setFunction("WORK_ORDER_API.Get_Mch_Code(:WO_NO)");
      f.setUpperCase();

      f = itemblk3.addField("ITEM3DESCRIPTION");
      f.setSize(30);
      f.setReadOnly();
      f.setMaxLength(2000);
      f.setHidden();
      f.setFunction("''");

      f = itemblk3.addField("ITEM3_SIGNATURE");
      f.setSize(8);
      f.setMaxLength(2000);
      f.setDbName("SIGNATURE");
      f.setCustomValidation("ITEM3_SIGNATURE,ITEM3_COMPANY","ITEM3_SIGNATURE_ID,SIGNATURENAME");
      f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMSIGNATURE: Signature");
      f.setUpperCase();

      f = itemblk3.addField("SIGNATURENAME");
      f.setSize(15);
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setMaxLength(2000);
      f.setFunction("EMPLOYEE_API.Get_Employee_Info(Site_API.Get_Company(:ITEM3_CONTRACT),:ITEM3_SIGNATURE_ID)");

      f = itemblk3.addField("ITEM3_CONTRACT");
      f.setSize(10);
      f.setReadOnly();
      f.setDbName("CONTRACT");
      f.setMaxLength(5);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCONTRACT: Site");
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
      f.setUpperCase();
      f.setInsertable();
      f.setMandatory();

      f = itemblk3.addField("ENTERED","Date");
      f.setSize(15);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMENTERED: Entered");

      f = itemblk3.addField("INT_DESTINATION_ID");
      f.setSize(8);
      f.setUpperCase();
      f.setMaxLength(30);
      f.setCustomValidation("INT_DESTINATION_ID,ITEM3_CONTRACT","INT_DESTINATION_DESC");
      f.setDynamicLOV("INTERNAL_DESTINATION_LOV","ITEM3_CONTRACT CONTRACT",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMINT_DESTINATION_ID: Int Destination");

      f = itemblk3.addField("INT_DESTINATION_DESC");
      f.setSize(15);
      f.setDefaultNotVisible();
      f.setMaxLength(2000);

      f = itemblk3.addField("DUE_DATE","Date");
      f.setSize(15);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMDUE_DATE: Due Date");

      f = itemblk3.addField("ITEM3_STATE");
      f.setSize(10);
      f.setDbName("STATE");
      f.setReadOnly();
      f.setMaxLength(2000);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMSTATE: Status");

      f = itemblk3.addField("NREQUISITIONVALUE", "Number");
      f.setSize(8);
      f.setHidden();
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMNREQUISITIONVALUE: Total Value");
      f.setFunction("''");

      f = itemblk3.addField("SNOTETEXT");
      f.setSize(15);
      f.setHidden();
      f.setMaxLength(2000);
      f.setFunction("''");

      f = itemblk3.addField("ITEM3_SIGNATURE_ID");
      f.setSize(6);
      f.setDbName("SIGNATURE_ID");
      f.setReadOnly();
      f.setInsertable();
      f.setMaxLength(11);
      f.setHidden();
      f.setUpperCase();

      f = itemblk3.addField("NNOTEID", "Number");
      f.setSize(6);
      f.setInsertable();
      f.setMaxLength(10);
      f.setReadOnly();
      f.setHidden();
      f.setFunction("''");

      f = itemblk3.addField("ORDERCLASS");
      f.setSize(3);
      f.setHidden();
      f.setMaxLength(3);
      f.setReadOnly();
      f.setFunction("''");

      f = itemblk3.addField("SNOTEIDEXIST");
      f.setSize(4);
      f.setMaxLength(2000);
      f.setReadOnly();
      f.setHidden();
      f.setFunction("''");

      f = itemblk3.addField("ITEM3_COMPANY");
      f.setSize(6);
      f.setHidden();
      f.setFunction("Site_API.Get_Company(:ITEM3_CONTRACT)"); 
      f.setMaxLength(20);
      f.setUpperCase();

      f = itemblk3.addField("NPREACCOUNTINGID", "Number");
      f.setSize(11);
      f.setHidden();
      f.setReadOnly();
      f.setMaxLength(10);
      f.setFunction("Active_Work_Order_API.Get_Pre_Accounting_Id(:ITEM3_WO_NO)");

      f = itemblk3.addField("SYS_DATE","Date");
      f.setFunction("''");
      f.setHidden();

      f = itemblk3.addField("ITEM3_ACTIVITY_SEQ","Number");
      f.setLabel("ACTSEP2REPORTINWORKORDERITEM3ACTSEQ: Activity Seq");
      f.setDbName("ACTIVITY_SEQ");
      f.setSize(20);
      f.setHidden();

      f = itemblk3.addField("MUL_REQ_LINE");
      f.setReadOnly();
      f.setFunction("Maint_Material_Requisition_API.Multiple_Mat_Req_Exist(:ITEM3_WO_NO)");
      f.setLabel("ACTSEPREPINWOSMITEM3MULMATREQEXIST: Multiple Material Requisitions Exist"); 
      f.setCheckBox("FALSE,TRUE");
      f.setDefaultNotVisible();

      f = itemblk3.addField("EXIST");
      f.setFunction("''");
      f.setHidden();

      f = itemblk3.addField("ORDER_LIST");
      f.setFunction("''");
      f.setHidden();

      f = itemblk3.addField("WO_STATUS");
      f.setHidden();
      f.setFunction("Active_Separate_Api.Get_Obj_State(:ITEM3_WO_NO)");


      itemblk3.setView("MAINT_MATERIAL_REQUISITION");
      itemblk3.defineCommand("MAINT_MATERIAL_REQUISITION_API","New__,Modify__,Remove__,PLAN__,RELEASE__,CLOSE__");
      itemset3 = itemblk3.getASPRowSet();

      itemblk3.setMasterBlock(headblk);

      itemtbl3 = mgr.newASPTable(itemblk3);
      itemtbl3.setWrap();
      //Web Alignment - Enable Multirow 
      itemtbl3.enableRowSelect();
      //
      itembar3 = mgr.newASPCommandBar(itemblk3);
      itembar3.addCustomCommand("plan",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMPLANCONS: Plan"));
      itembar3.addCustomCommand("release",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMRELEA: Release"));
      itembar3.addCustomCommand("close",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCLOS: Close"));
      itembar3.addCustomCommandSeparator();
      itembar3.addCustomCommand("prePostHead",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMPREPOSTHEAD: Pre Posting Header..."));
      itembar3.addCustomCommand("printPicList",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMPICLSTMAT: Pick List For Material Requistion - Printout..."));
      itembar3.addCustomCommandSeparator();

      itembar3.addCommandValidConditions("plan","OBJSTATE","Enable","Released");
      itembar3.addCommandValidConditions("release","OBJSTATE","Enable","Planned");
      itembar3.appendCommandValidConditions("release", "WO_STATUS", "Enable", "RELEASED;STARTED;WORKDONE;PREPARED;UNDERPREPARATION");
      itembar3.addCommandValidConditions("close","OBJSTATE","Enable","Released;Planned");

      //Web Alignment - Enable Multirow 
      itembar3.enableMultirowAction();
      itembar3.removeFromMultirowAction("prePostHead");
      //

      itembar3.enableCommand(itembar3.FIND);
      itembar3.defineCommand(itembar3.DELETE,"deleteItem");
      itembar3.defineCommand(itembar3.NEWROW,"newRowITEM3");
      itembar3.defineCommand(itembar3.COUNTFIND,"countFindITEM3");
      itembar3.defineCommand(itembar3.OKFIND,"okFindITEM3");
      itembar3.defineCommand(itembar3.SAVERETURN,"saveReturnItem","checkItem3Fields(-1)");
      itembar3.defineCommand(itembar3.SAVENEW,null,"checkItem3Fields(-1)"); 

      itemlay3 = itemblk3.getASPBlockLayout();
      itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);
      itemlay3.setSimple("INT_DESTINATION_DESC");
      itemlay3.setSimple("SIGNATURENAME");

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
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMLINE_ITEM_NO: Line No");

      f = itemblk.addField("PART_NO");
      f.setSize(14);
      f.setReadOnly();
      f.setInsertable();
      f.setMaxLength(25);
      f.setHyperlink("../invenw/InventoryPart.page","PART_NO","NEWWIN"); 
      f.setCustomValidation("PART_NO,SPARE_CONTRACT,CATALOG_CONTRACT,OWNER,CONDITION_CODE,SUPPLY_CODE,ITEM5_MAINT_MATERIAL_ORDER_NO,PART_OWNERSHIP,ITEM_WO_NO","ITEM5_CATALOG_NO,ITEM5CATALOGDESC,SCODEA,SCODEB,SCODEC,SCODED,SCODEE,SCODEF,SCODEG,SCODEH,SCODEI,SCODEJ,HASSPARESTRUCTURE,DIMQTY,TYPEDESIGN,QTYONHAND,UNITMEAS,SALES_PRICE_GROUP_ID,CONDITION_CODE,CONDDESC,QTY_AVAILABLE,ACTIVEIND_DB,PART_OWNERSHIP,PART_OWNERSHIP_DB,OWNER");    
      f.setDynamicLOV("INVENTORY_PART_WO_LOV","SPARE_CONTRACT CONTRACT",600,445);
      f.setMandatory();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMPART_NO: Part No");
      f.setUpperCase();

      f = itemblk.addField("SPAREDESCRIPTION");
      f.setSize(20);
      f.setReadOnly();
      f.setMaxLength(2000);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMSPAREDESCRIPTION: Part Description");
      f.setFunction("INVENTORY_PART_API.Get_Description(:SPARE_CONTRACT,:PART_NO)");

      f = itemblk.addField("CONDITION_CODE");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCONDITIONCODE: Condition Code");
      f.setSize(15);
      f.setDynamicLOV("CONDITION_CODE",600,445);
      f.setUpperCase(); 
      f.setCustomValidation("CONDITION_CODE,PART_NO,SPARE_CONTRACT,OWNER,PART_OWNERSHIP_DB,SUPPLY_CODE,ITEM5_MAINT_MATERIAL_ORDER_NO,ITEM3_ACTIVITY_SEQ,PART_OWNERSHIP,PLAN_QTY","CONDDESC,QTYONHAND,QTY_AVAILABLE,ITEM_COST,AMOUNTCOST"); 

      f = itemblk.addField("CONDDESC");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCONDDESC: Condition Code Description");
      f.setSize(20);
      f.setMaxLength(50);
      f.setReadOnly();
      f.setFunction("CONDITION_CODE_API.Get_Description(:CONDITION_CODE)");

      f = itemblk.addField("PART_OWNERSHIP");
      f.setSize(25);
      f.setInsertable();
      f.setSelectBox();
      f.enumerateValues("PART_OWNERSHIP_API");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMPARTOWNERSHIP: Ownership"); 
      f.setCustomValidation("PART_OWNERSHIP,PART_NO,SPARE_CONTRACT,OWNER,CONDITION_CODE,SUPPLY_CODE,ITEM5_MAINT_MATERIAL_ORDER_NO","PART_OWNERSHIP_DB,QTYONHAND,QTY_AVAILABLE"); 

      f = itemblk.addField("PART_OWNERSHIP_DB");
      f.setSize(20);
      f.setHidden();

      f = itemblk.addField("OWNER");
      f.setSize(15);
      f.setMaxLength(20);
      f.setInsertable();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMPARTOWNER: Owner"); 
      f.setCustomValidation("OWNER,PART_OWNERSHIP_DB,CONDITION_CODE,PART_NO,SPARE_CONTRACT,SUPPLY_CODE,ITEM5_MAINT_MATERIAL_ORDER_NO,PART_OWNERSHIP","OWNER_NAME,WO_CUST,QTYONHAND,QTY_AVAILABLE");     
      f.setDynamicLOV("CUSTOMER_INFO");
      f.setUpperCase();

      f = itemblk.addField("WO_CUST");
      f.setSize(20);
      f.setHidden();
      f.setFunction("ACTIVE_SEPARATE_API.Get_Customer_No(:ITEM3_WO_NO)");

      f = itemblk.addField("OWNER_NAME");
      f.setSize(20);
      f.setMaxLength(100);
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMPARTOWNERNAME: Owner Name");
      f.setFunction("CUSTOMER_INFO_API.Get_Name(:OWNER)");

      f = itemblk.addField("SPARE_CONTRACT");
      f.setSize(11);
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setMaxLength(5);
      f.setMandatory();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMSPARE_CONTRACT: Site");
      f.setUpperCase();

      f = itemblk.addField("HASSPARESTRUCTURE");
      f.setSize(8);
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setMaxLength(2000);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMHASSPARESTRUCTURE: Structure");
      f.setFunction("Equipment_Spare_Structure_API.Has_Spare_Structure_Boolean(:PART_NO,:SPARE_CONTRACT)");

      f = itemblk.addField("ITEM_JOB_ID", "Number");
      f.setDbName("JOB_ID");
      f.setSize(8);
      f.setInsertable();
      f.setDefaultNotVisible();
      f.setDynamicLOV("WORK_ORDER_JOB", "ITEM_WO_NO WO_NO");
      f.setLabel("PCMWACTIVESEPERATEREPWOSMITEMJOBID: Job Id");

      f = itemblk.addField("DIMQTY");
      f.setSize(11);
      f.setReadOnly();
      f.setMaxLength(2000);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMDIMQTY: Dimension/ Quality");
      f.setDefaultNotVisible();   
      f.setFunction("INVENTORY_PART_API.Get_Dim_Quality(:SPARE_CONTRACT,:PART_NO)");

      f = itemblk.addField("TYPEDESIGN");
      f.setSize(15);
      f.setReadOnly();
      f.setMaxLength(2000);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMTYPEDESIGN: Type Designation");
      f.setFunction("INVENTORY_PART_API.Get_Type_Designation(:SPARE_CONTRACT,:PART_NO)");

      f = itemblk.addField("DATE_REQUIRED","Date");
      f.setSize(13);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMDATE_REQUIRED: Date Required");

      f = itemblk.addField("SUPPLY_CODE");
      f.setSize(25);
      f.setMandatory();
      f.setInsertable();
      f.setSelectBox();
      f.setCustomValidation("PART_NO,SPARE_CONTRACT,CATALOG_CONTRACT,OWNER,CONDITION_CODE,SUPPLY_CODE,ITEM5_MAINT_MATERIAL_ORDER_NO,ITEM3_ACTIVITY_SEQ","QTYONHAND,QTY_AVAILABLE");
      f.enumerateValues("MAINT_MAT_REQ_SUP_API");
      f.setLabel("PCMWACTIVESEPARATE2REPORTINWORKORDERSMSUPPLYCODE: Supply Code");
      f.setMaxLength(200);

      f = itemblk.addField("COND_CODE_USAGE");
      f.setFunction("''");
      f.setHidden();

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

      f = itemblk.addField("PLAN_QTY","Number");
      f.setSize(14);
      f.setCustomValidation("PLAN_QTY,PART_NO,SPARE_CONTRACT,ITEM5_CATALOG_NO,CATALOG_CONTRACT,ITEM5_PRICE_LIST_NO,ITEM5_PLAN_LINE_NO,ITEM_DISCOUNT,ITEM_WO_NO,ITEM_COST,ITEM5_LIST_PRICE,ITEMDISCOUNTEDPRICE,SERIAL_NO,CONFIGURATION_ID,CONDITION_CODE,PART_OWNERSHIP_DB","ITEM_COST,AMOUNTCOST,ITEM5_PRICE_LIST_NO,ITEM_DISCOUNT,ITEM5_LIST_PRICE,ITEM5SALESPRICEAMOUNT,ITEMDISCOUNTEDPRICE");
      f.setMandatory();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMPLAN_QTY: Quantity Required");

      f = itemblk.addField("ITEM_QTY","Number");
      f.setSize(13);
      f.setReadOnly();
      f.setDbName("QTY");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMITEMQTY: Quantity Issued");

      f = itemblk.addField("QTY_SHORT","Number");
      f.setSize(11);
      f.setMandatory();
      f.setHidden();
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMQTY_SHORT: Quantity Short");

      f = itemblk.addField("QTYONHAND","Number");
      f.setSize(17);
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMQTYONHAND: Quantity on Hand");
      if (mgr.isModuleInstalled("INVENT"))
         f.setFunction("Maint_Material_Req_Line_Api.Get_Qty_On_Hand(:ITEM5_MAINT_MATERIAL_ORDER_NO,:LINE_ITEM_NO)");
      else
         f.setFunction("''"); 

      //Bug 76767, Start, Modified the function call
      f = itemblk.addField("QTY_AVAILABLE","Number");
      f.setSize(17);
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMQTY_AVAILABLE: Quantity Available");
      if (mgr.isModuleInstalled("INVENT"))
          f.setFunction("MAINT_MATERIAL_REQ_LINE_API.Get_Qty_Available(:ITEM5_MAINT_MATERIAL_ORDER_NO,:LINE_ITEM_NO)");
      else
         f.setFunction("''"); 
      //Bug 76767, End

      f = itemblk.addField("QTY_ASSIGNED","Number");
      f.setSize(11);
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMQTY_ASSIGNED: Quantity Assigned");

      f = itemblk.addField("QTY_RETURNED","Number");
      f.setSize(11);
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setMandatory();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMQTY_RETURNED: Quantity Returned");

      f = itemblk.addField("UNITMEAS");
      f.setSize(11);
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setMaxLength(2000);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMUNITMEAS: Unit");
      f.setFunction("Purchase_Part_Supplier_API.Get_Unit_Meas(:SPARE_CONTRACT,:PART_NO)");

      f = itemblk.addField("CATALOG_CONTRACT");
      f.setSize(10);
      f.setMaxLength(5);
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCATALOG_CONTRACT: Sales Part Site");
      f.setUpperCase();

      f = itemblk.addField("ITEM5_CATALOG_NO");
      f.setSize(9);
      f.setDbName("CATALOG_NO");
      f.setMaxLength(25);
      f.setDefaultNotVisible();
      f.setCustomValidation("ITEM5_CATALOG_NO,ITEM_WO_NO,CATALOG_CONTRACT,ITEM5_PRICE_LIST_NO,PLAN_QTY,ITEM5_PLAN_LINE_NO,PART_NO,SPARE_CONTRACT,ITEM_COST,PLAN_QTY,ITEM_DISCOUNT,PART_OWNERSHIP_DB","ITEM5_LIST_PRICE,ITEM_COST,AMOUNTCOST,ITEM5CATALOGDESC,ITEM_DISCOUNT,ITEM5SALESPRICEAMOUNT,ITEMDISCOUNTEDPRICE,ITEM5_PRICE_LIST_NO,SALES_PRICE_GROUP_ID");
      f.setDynamicLOV("SALES_PART_ACTIVE_LOV","CATALOG_CONTRACT CONTRACT",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCATALOG_NO: Sales Part Number");
      f.setUpperCase();

      f = itemblk.addField("ITEM5CATALOGDESC");
      f.setSize(17);
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setMaxLength(2000);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMSALESCATALOGDESC: Sales Part Description");
      if (orderInst)
         f.setFunction("SALES_PART_API.Get_Catalog_Desc(:CATALOG_CONTRACT,:ITEM5_CATALOG_NO)");
      else
         f.setFunction("''");

      f = itemblk.addField("ITEM5_PRICE_LIST_NO");
      f.setSize(10);
      f.setMaxLength(10);
      f.setDefaultNotVisible();
      f.setDbName("PRICE_LIST_NO");
      f.setCustomValidation("ITEM5_PRICE_LIST_NO,SPARE_CONTRACT,PART_NO,ITEM_COST,PLAN_QTY,ITEM5_CATALOG_NO,PLAN_QTY,ITEM_WO_NO,ITEM5_PLAN_LINE_NO,ITEM_DISCOUNT,CATALOG_CONTRACT","ITEM_COST,AMOUNTCOST,ITEM5_LIST_PRICE,ITEM_DISCOUNT,ITEM5SALESPRICEAMOUNT,ITEMDISCOUNTEDPRICE");
      f.setDynamicLOV("SALES_PRICE_LIST","SALES_PRICE_GROUP_ID,CURRENCEY_CODE",600,445);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMPRICE_LIST_NO: Price List No");
      f.setUpperCase();

      f = itemblk.addField("ITEM5_LIST_PRICE","Money");
      f.setSize(9);
      f.setDbName("LIST_PRICE");
      f.setDefaultNotVisible();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMLIST_PRICE: Sales Price");

      f = itemblk.addField("ITEM_DISCOUNT","Number");
      f.setSize(14);
      f.setDefaultNotVisible();
      f.setDbName("DISCOUNT");
      f.setCustomValidation("ITEM_DISCOUNT,ITEM5_LIST_PRICE,PLAN_QTY","ITEM5SALESPRICEAMOUNT,ITEMDISCOUNTEDPRICE");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMDISCOUNT: Discount %");

      f = itemblk.addField("CURRENCEY_CODE");
      f.setHidden();
      f.setFunction("ACTIVE_SEPARATE_API.Get_Currency_Code(:ITEM_WO_NO)");

      f = itemblk.addField("ITEMDISCOUNTEDPRICE", "Money");
      f.setSize(14);
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE2REPORTINWORKORDERSMDISCOUNTEDPRICE: Discounted Price");
      f.setFunction("LIST_PRICE - (NVL(DISCOUNT, 0)/100*LIST_PRICE)");

      f = itemblk.addField("ITEM5SALESPRICEAMOUNT", "Money");
      f.setSize(14);
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMPRICEAMOUNT: Price Amount");
      f.setFunction("''");

      f = itemblk.addField("ITEM_COST", "Money");
      f.setSize(11);
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMCOST: Cost");
      f.setFunction("''");

      f = itemblk.addField("AMOUNTCOST", "Money");
      f.setSize(11);
      f.setDefaultNotVisible(   );
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMAMOUNTCOST: Cost Amount");
      f.setFunction("''");
      f.setReadOnly();

      f = itemblk.addField("SCODEA");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMSCODEA: Account");
      f.setFunction("''");
      f.setReadOnly();
      f.setHidden();
      f.setDefaultNotVisible();
      f.setMaxLength(10);

      f = itemblk.addField("SCODEB");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMSCODEB: Cost Center");
      f.setFunction("''");
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setHidden();
      f.setMaxLength(10);

      f = itemblk.addField("SCODEF");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMSCODEF: Project No");
      f.setFunction("''");
      f.setReadOnly();
      f.setHidden();
      f.setDefaultNotVisible();
      f.setMaxLength(10);

      f = itemblk.addField("SCODEE");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMSCODEE: Object No");
      f.setFunction("''");
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setHidden();
      f.setMaxLength(10);

      f = itemblk.addField("SCODEC");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMSCODEC: Code C");
      f.setFunction("''");
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setHidden();
      f.setMaxLength(10);

      f = itemblk.addField("SCODED");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMSCODED: Code D");
      f.setFunction("''");
      f.setReadOnly();
      f.setHidden();
      f.setDefaultNotVisible();
      f.setMaxLength(10);

      f = itemblk.addField("SCODEG");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMSCODEG: Code G");
      f.setFunction("''");
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setHidden();
      f.setMaxLength(10);

      f = itemblk.addField("SCODEH");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMSCODEH: Code H");
      f.setFunction("''");
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setHidden();
      f.setMaxLength(10);

      f = itemblk.addField("SCODEI");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMSCODEI: Code I");
      f.setFunction("''");
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setHidden();
      f.setMaxLength(10);

      f = itemblk.addField("SCODEJ");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMSCODEJ: Code J");
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
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMITEM0_WO_NO: Work Order No");
      f.setDbName("WO_NO");

      f = itemblk.addField("ITEM5_PLAN_LINE_NO","Number");
      f.setSize(17);
      f.setDbName("PLAN_LINE_NO");
      f.setReadOnly();
      f.setHidden();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMPLAN_LINE_NO: Plan Line No");

      f = itemblk.addField("ITEM5_MAINT_MATERIAL_ORDER_NO","Number","#");
      f.setSize(17);
      f.setHidden();
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMITEM5_MAINT_MATERIAL_ORDER_NO: Mat Req Order No");
      f.setDbName("MAINT_MATERIAL_ORDER_NO");

      f = itemblk.addField("ITEM5_BASE_PRICE","Money");
      f.setHidden();
      f.setFunction("''");

      f = itemblk.addField("ITEM5_SALE_PRICE","Money");
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

      f = itemblk.addField("SALES_PRICE_GROUP_ID");
      f.setHidden();
      if (orderInst)
         f.setFunction("SALES_PART_API.GET_SALES_PRICE_GROUP_ID(:CATALOG_CONTRACT,:ITEM5_CATALOG_NO)");
      else
         f.setFunction("''");

      f = itemblk.addField("SUPPLY_CODE_DB");
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

      f = itemblk.addField("OBJ_LOAN");
      f.setFunction("''");
      f.setHidden();

      f = itemblk.addField("SPARE_PART_EXIST", "Number");
      f.setFunction("Equipment_Object_Spare_Api.Check_Exist(Active_Work_Order_API.Get_Mch_Code(:ITEM_WO_NO),:SPARE_CONTRACT,:PART_NO,Active_Work_Order_API.Get_Mch_Code_Contract(:ITEM_WO_NO))");
      f.setHidden();

      f = itemblk.addField("VENDOR_NO");
      f.setFunction("''");
      f.setHidden();

      itemblk.setView("MAINT_MATERIAL_REQ_LINE");

      itemblk.defineCommand("MAINT_MATERIAL_REQ_LINE_API","New__,Modify__,Remove__");
      itemset = itemblk.getASPRowSet();
      itemblk.setMasterBlock(itemblk3);

      itembar = mgr.newASPCommandBar(itemblk);
      itembar.enableCommand(itembar.FIND);
      itembar.defineCommand(itembar.NEWROW,"newRowITEM5");
      itembar.defineCommand(itembar.COUNTFIND,"countFindITEM5");
      itembar.defineCommand(itembar.OKFIND,"okFindITEM5");
      itembar.defineCommand(itembar.DUPLICATEROW,"duplicateITEM5");
      itembar.defineCommand(itembar.DELETE,"deleteITEM5");

      itemtbl = mgr.newASPTable(itemblk);
      itemtbl.setWrap();

      //Web Alignment - Enable Multiow Action
      itemtbl.enableRowSelect();
      //

      //Replacing links with RMBs
      itembar.addCustomCommand("sparePartObject",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMSPARTSINOBJECT: Spare Parts in Object..."));
      itembar.addCustomCommand("updateSparePartObject", mgr.translate("PCMWACTIVESEPARATE2UPDATESPRPARTS: Update Spare Parts in Object..."));
      itembar.addCustomCommand("objStructure",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSPARTSINOBJECTSTRUC: Object Structure..."));
      itembar.addCustomCommand("detchedPartList",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMSPAREINDETACH: Spare Parts in Detached Part List..."));
      itembar.addCustomCommandSeparator();
      itembar.addCustomCommand("reserve",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMRESERV: Reserve"));
      itembar.addCustomCommand("manReserve",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMRESERVMAN: Reserve manually..."));
      if (mgr.isPresentationObjectInstalled("invenw/InventoryPartInStockOvw.page"))
         itembar.addCustomCommand("availtoreserve",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERUNRESERVAVAILTORESERVE: Inventory Part in Stock..."));
      itembar.addCustomCommand("unreserve",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMUNRESERV: Unreserve..."));
      itembar.addCustomCommand("issue",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMISSUE: Issue"));
      itembar.addCustomCommand("manIssue",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMISSUEMAN: Issue manually..."));
      itembar.addCustomCommandSeparator();
      itembar.addCustomCommand("availDetail",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMINVAVAILPLAN: Query - Inventory Part Availability Planning..."));
      itembar.addCustomCommand("supPerPart",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMSUPFORPART: Supplier per Part..."));
      itembar.addCustomCommand("matReqUnissue",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMMATREQUNISSU: Material Requisition Unissue..."));
      itembar.addCustomCommandSeparator();
      itembar.addCustomCommand("prePostHead",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMPREPOSTDET: Pre Posting Detail..."));


      itembar.forceEnableMultiActionCommand("sparePartObject");
      itembar.forceEnableMultiActionCommand("objStructure");
      itembar.forceEnableMultiActionCommand("detchedPartList");

      itembar.addCommandValidConditions("updateSparePartObject","SPARE_PART_EXIST","Enable","0");

      // 031224  ARWILK  Begin  (Links with multirow RMB's)
      itembar.enableMultirowAction();
      itembar.removeFromMultirowAction("manReserve");
      itembar.removeFromMultirowAction("unreserve");
      itembar.removeFromMultirowAction("manIssue");       
      itembar.removeFromMultirowAction("matReqUnissue");      
      itembar.removeFromMultirowAction("prePostHead");
      // 031224  ARWILK  End  (Links with multirow RMB's)

      itemlay = itemblk.getASPBlockLayout();
      itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT); 

      //-----------------------------------------------------------------------
      //-------------- This part belongs to BUDGET ---------------------
      //-----------------------------------------------------------------------

      itemblk4 = mgr.newASPBlock("ITEM4");

      f = itemblk4.addField("ITEM4_OBJID");
      f.setDbName("OBJID");
      f.setHidden();

      f = itemblk4.addField("ITEM4_OBJVERSION");
      f.setDbName("OBJVERSION");
      f.setHidden();

      f = itemblk4.addField("ITEM4_WO_NO");
      f.setHidden();
      f.setDbName("WO_NO");

      f = itemblk4.addField("ITEM4_WORK_ORDER_COST_TYPE");
      f.setSize(20);
      f.setDbName("WORK_ORDER_COST_TYPE");
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMWOORDERCOSTTYPE: Work Order Cost Type");
      f.setReadOnly();

      f = itemblk4.addField("WORK_ORDER_COST_TYPE_DB");
      f.setSize(20);
      f.setHidden();
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMWOORDERCOSTTYPE: Work Order Cost Type");

      f = itemblk4.addField("BUDGET_COST","Money");
      f.setSize(18);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMBUDGETCOST: Budget Cost");
      f.setCustomValidation("BUDGET_COST,BUDGET_REVENUE","BUDGET_MARGIN");

      f = itemblk4.addField("BUDGET_REVENUE","Money");
      f.setSize(18);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMBUDGETREVENUE: Budget Revenue");
      f.setCustomValidation("BUDGET_COST,BUDGET_REVENUE","BUDGET_MARGIN");

      f = itemblk4.addField("BUDGET_MARGIN","Number");
      f.setSize(18);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMBUDGETMARGIN: Budget Margin");
      f.setFunction("0"); 
      f.setReadOnly();

      f = itemblk4.addField("PLANNED_COST","Money");
      f.setSize(18);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMPLANNEDCOST: Planned Cost");
      f.setFunction("WORK_ORDER_BUDGET_API.Get_Planned_Cost(:WO_NO,:WORK_ORDER_COST_TYPE)");
      f.setReadOnly();

      f = itemblk4.addField("PLANNED_REVENUE","Money");
      f.setSize(18);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMPLANNEDREV: Planned Revenue");
      f.setFunction("WORK_ORDER_BUDGET_API.Get_Planned_Revenue(:WO_NO,:WORK_ORDER_COST_TYPE)");
      f.setReadOnly();

      f = itemblk4.addField("PLANNED_MARGIN","Number");
      f.setSize(18);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMPLANNEDMARGIN: Planned Margin");
      f.setFunction("WORK_ORDER_BUDGET_API.Get_Planned_Revenue(:WO_NO,:WORK_ORDER_COST_TYPE) - WORK_ORDER_BUDGET_API.Get_Planned_Cost(:WO_NO,:WORK_ORDER_COST_TYPE)"); 
      f.setReadOnly();

      f = itemblk4.addField("ACTUAL_COST","Money");
      f.setSize(18);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMACCCOST: Actual Cost");
      f.setFunction("0"); 
      f.setReadOnly();

      f = itemblk4.addField("ACTUAL_REVENUE","Money");
      f.setSize(18);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMACCREV: Actual Revenue");
      f.setReadOnly();
      f.setFunction("0"); 

      f = itemblk4.addField("ACTUAL_MARGIN","Number");
      f.setSize(18);
      f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSMACCMAR: Actual Margin");
      f.setFunction("0"); 
      f.setReadOnly();

      itemblk4.setView("WORK_ORDER_BUDGET");
      itemblk4.defineCommand("WORK_ORDER_BUDGET_API","New__,Modify__,Remove__");
      itemset4 = itemblk4.getASPRowSet();

      itemblk4.setMasterBlock(headblk);

      itembar4 = mgr.newASPCommandBar(itemblk4);
      itembar4.disableCommand(itembar4.DELETE);
      itembar4.disableCommand(itembar4.NEWROW);
      itembar4.disableCommand(itembar4.DUPLICATEROW);
      itembar4.defineCommand(itembar4.EDITROW,"editItem4");
      itembar4.defineCommand(itembar4.VIEWDETAILS,"viewDetailsITEM4");
      itembar4.defineCommand(itembar4.SAVERETURN,"saveReturnITEM4");
      itembar4.defineCommand(itembar4.FORWARD,"forwardITEM4");
      itembar4.defineCommand(itembar4.BACKWARD,"backwardITEM4");
      itembar4.defineCommand(itembar4.OKFIND,"okFindITEM4");

      itemtbl4 = mgr.newASPTable(itemblk4);
      itemtbl4.setWrap();  
      itemlay4 = itemblk4.getASPBlockLayout();
      itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT);
      itemlay4.setDialogColumns(4);

      //---------------------------------------------------------------------------------
      //-----------------------------------  ITEMBLK6 -----------------------------------
      //---------------------------------------------------------------------------------

      itemblk6 = mgr.newASPBlock("ITEM6");

      itemblk6.addField("ITEM6_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk6.addField("ITEM6_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      itemblk6.addField("ITEM6_WO_NO", "Number", "#").
      setDbName("WO_NO").
      setHidden().
      setReadOnly().
      setInsertable().
      setMandatory();

      itemblk6.addField("JOB_ID", "Number").
      setLabel("ACTSEPREPSMITEM6JOBID: Job ID").
      setReadOnly().
      setInsertable().
      setMandatory();

      itemblk6.addField("STD_JOB_ID").
      setSize(15).
      setLabel("ACTSEPREPSMITEM6STDJOBID: Standard Job ID").
      setLOV("SeparateStandardJobLov.page", 600, 445).
      setUpperCase().
      setInsertable().
      setQueryable().
      setCustomValidation("STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION", "STD_JOB_ID,STD_JOB_REVISION,DESCRIPTION,STD_JOB_STATUS").
      setMaxLength(12);

      itemblk6.addField("STD_JOB_CONTRACT").
      setSize(10).
      setLabel("ACTSEPREPSMITEM6STDJOBCONTRACT: Site").
      setUpperCase().
      setReadOnly().
      setMaxLength(5);

      itemblk6.addField("STD_JOB_REVISION").
      setSize(10).
      setLabel("ACTSEPREPSMITEM6STDJOBREVISION: Revision").
      setDynamicLOV("SEPARATE_STANDARD_JOB_LOV", "STD_JOB_CONTRACT CONTRACT,STD_JOB_ID", 600, 445).    
      setCustomValidation("STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION", "DESCRIPTION,STD_JOB_STATUS").
      setUpperCase().
      setInsertable().
      setQueryable().
      setMaxLength(6);

      itemblk6.addField("DESCRIPTION").
      setSize(35).
      setLabel("ACTSEPREPSMITEM6DESCRIPTION: Description").
      setUpperCase().
      setMandatory().
      setInsertable().
      setMaxLength(4000);

      itemblk6.addField("ITEM6_QTY", "Number").
      setDbName("QTY").
      setLabel("ACTSEPREPSMITEM6QTY: Quantity").
      setMandatory().
      setInsertable();

      itemblk6.addField("ITEM6_COMPANY").
      setDbName("COMPANY").
      setSize(20).
      setHidden().
      setUpperCase().
      setInsertable();

      itemblk6.addField("STD_JOB_STATUS").
      setLabel("ACTSEPREPSMITEM6STDJOBSTATUS: Std Job Status").
      setFunction("Standard_Job_API.Get_Status(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)").
      setReadOnly();

      itemblk6.addField("SIGN_ID").
      setSize(35).
      setLabel("ACTSEPREPSMITEM6SIGNID: Executed By").
      setQueryable().
      setDbName("SIGNATURE").
      setUpperCase().
      setLOV("../mscomw/MaintEmployeeLov.page","ITEM6_COMPANY COMPANY",600,450).
      setCustomValidation("ITEM6_COMPANY,SIGN_ID","EMPLOYEE_ID,SIGN_ID");

      itemblk6.addField("EMPLOYEE_ID").
      setSize(15).
      setLabel("ACTSEPREPSMITEM6EMPLOYEEID: Employee ID").
      setUpperCase().
      setReadOnly().
      setMaxLength(11);

      //(+) Bug 66406, Start
      itemblk6.addField("CONN_PM_NO", "Number" ,"#").
      setDbName("PM_NO").
      setSize(15).
      setReadOnly().
      setCustomValidation("CONN_PM_NO,CONN_PM_REVISION","CONN_PM_NO,CONN_PM_REVISION").
      setLabel("ACTSEPREPSMCONNPMNO: PM No");

      itemblk6.addField("CONN_PM_REVISION").
      setDbName("PM_REVISION").
      setSize(15).
      setReadOnly().
      setLabel("ACTSEPREPSMCONNPMREV: PM Revision");

      itemblk6.addField("CONN_PM_JOB_ID", "Number").
      setDbName("PM_JOB_ID"). 
      setSize(15).
      setReadOnly().
      setDynamicLOV("PM_ACTION_JOB", "CONN_PM_NO PM_NO, CONN_PM_REVISION PM_REVISION").
      setLabel("ACTSEPREPSMCONNPMJOBID: PM Job ID");
      //(+) Bug 66406, End

      itemblk6.addField("DATE_FROM", "Datetime").
      setSize(20).
      setLabel("ACTSEPREPSMITEM6DATEFROM: Date From").
      setInsertable();

      itemblk6.addField("DATE_TO", "Datetime").
      setSize(20).
      setLabel("ACTSEPREPSMITEM6DATETO: Date To").
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

      itemblk6.setView("WORK_ORDER_JOB");
      itemblk6.defineCommand("WORK_ORDER_JOB_API","New__,Modify__,Remove__");
      itemblk6.setMasterBlock(headblk);

      itemset6 = itemblk6.getASPRowSet();

      itemtbl6 = mgr.newASPTable(itemblk6);
      itemtbl6.setTitle(mgr.translate("ACTSEPREPSMITEM6WOJOBS: Jobs"));
      itemtbl6.setWrap();

      itembar6 = mgr.newASPCommandBar(itemblk6);
      itembar6.enableCommand(itembar6.FIND);

      itembar6.defineCommand(itembar6.OKFIND,"okFindITEM6");
      itembar6.defineCommand(itembar6.COUNTFIND,"countFindITEM6");
      itembar6.defineCommand(itembar6.NEWROW,"newRowITEM6");
      itembar6.defineCommand(itembar6.SAVERETURN,"saveReturnItem6","checkITEM6SaveParams(i)");
      //Bug 89703, Start
      itembar6.defineCommand(itembar6.DELETE,"deleteRowITEM6");
      //Bug 89703, End

      itemlay6 = itemblk6.getASPBlockLayout();
      itemlay6.setDefaultLayoutMode(itemlay6.MULTIROW_LAYOUT);
   }


   //Web Alignment - Adding Tabs
   public void activateBudgetTab()
   {
      tabs.setActiveTab(1);
   }
   public void activateTimeReportTab()
   {
      tabs.setActiveTab(2);
   }

   public void activateMaterialTab()
   {
      tabs.setActiveTab(3);
   }

   public void activateJobsTab()
   {
      tabs.setActiveTab(4);
   }
   //

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

         trans.clear();
      }
   }


   public void disableMoveStoIn()
   {
      ASPManager mgr = getASPManager();

      if (headset.countRows()>0)
      {
         trans.clear();
         cmd = trans.addCustomFunction("OBJLEV", "Maintenance_Object_API.Get_Obj_Level", "OBJLEVEL" );
         cmd.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT"));
         cmd.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));

         trans = mgr.perform(trans);

         objlevel = trans.getValue("OBJLEV/DATA/OBJLEVEL");

         if (!mgr.isEmpty(objlevel) && headlay.isSingleLayout())
            headbar.removeCustomCommand("moveSerial");
      }
   }

   public void disableMoveStoInovw()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());

      if (headset.countRows()>0)
      {
         trans.clear();
         cmd = trans.addCustomFunction("OBJLEV", "Maintenance_Object_API.Get_Obj_Level", "OBJLEVEL" );
         cmd.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT"));
         cmd.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));

         trans = mgr.perform(trans);

         objlevel = trans.getValue("OBJLEV/DATA/OBJLEVEL");

         if (!mgr.isEmpty(objlevel) && headlay.isSingleLayout())
            headbar.removeCustomCommand("moveSerial");

         if (!mgr.isEmpty(objlevel) && headlay.isMultirowLayout())
            mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMCANTMOVESERIAL: Cannot perform the action."));

         else
            moveSerial2();
      }
   }

   public boolean checkMROObjRO()
   {
      ASPManager mgr = getASPManager();

      if (!mgr.isEmpty(headset.getRow().getValue("MCH_CODE")))
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
         cmd.addParameter("HEAD_PART_NO");
         cmd.addParameter("HEAD_SERIAL_NO");
         cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

         trans = mgr.perform(trans);

         String roNo = trans.getValue("GETRONO/DATA/RECEIVE_ORDER_NO");
         String idType = trans.getValue("GETIDTYPE/DATA/IDENTITY_TYPE");
         String vimPartNo = trans.getValue("GETVIMINFO/DATA/HEAD_PART_NO");
         String vimSerialNo = trans.getValue("GETVIMINFO/DATA/HEAD_SERIAL_NO");

         if (mgr.isEmpty(roNo) && "EXTERN".equals(idType) && !mgr.isEmpty(vimPartNo) && !mgr.isEmpty(vimSerialNo))
            return true;
         else
            return false;
      }
      else
         return false;
   }

   public boolean checkReturnObj()
   {
      ASPManager mgr = getASPManager();

      if (!mgr.isEmpty(headset.getRow().getValue("MCH_CODE")))
      {
         trans.clear();

         cmd = trans.addCustomCommand("PARTSERIAL","ACTIVE_SEPARATE_API.Separate_Mro_Part_Serial");
         cmd.addParameter("HEAD_PART_NO");
         cmd.addParameter("HEAD_SERIAL_NO");
         cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

         cmd = trans.addCustomFunction("GET0PCOND","PART_SERIAL_CATALOG_API.Get_Operational_Condition_Db","OP_COND");
         cmd.addReference("HEAD_PART_NO","PARTSERIAL/DATA");
         cmd.addReference("HEAD_SERIAL_NO","PARTSERIAL/DATA");

         cmd = trans.addCustomFunction("GETOBJSTATE","PART_SERIAL_CATALOG_API.Get_Objstate","OBJ_STATE");
         cmd.addReference("HEAD_PART_NO","PARTSERIAL/DATA");
         cmd.addReference("HEAD_SERIAL_NO","PARTSERIAL/DATA");

         cmd = trans.addCustomFunction("GETISCONN","CONNECT_CUSTOMER_ORDER_API.Is_Wo_Connected_To_Mro_Line","ISCONN");
         cmd.addParameter("CUST_ORDER_NO",headset.getValue("CUST_ORDER_NO"));
         cmd.addParameter("WO_NO",headset.getValue("WO_NO"));
         cmd.addReference("HEAD_PART_NO","PARTSERIAL/DATA");

         trans = mgr.perform(trans);

         String opCond   = trans.getValue("GET0PCOND/DATA/OP_COND");
         String objState = trans.getValue("GETOBJSTATE/DATA/OBJ_STATE");
         String isConn   = trans.getValue("GETISCONN/DATA/ISCONN");

         if ("OPERATIONAL".equals(opCond) && "InInventory".equals(objState) && "FALSE".equals(isConn))
            return true;
         else
            return false;
      }
      else
         return false;
   }

   public boolean checkReturnNonOpObj()
   {
      ASPManager mgr = getASPManager();

      if (!mgr.isEmpty(headset.getRow().getValue("MCH_CODE")))
      {
         trans.clear();

         cmd = trans.addCustomCommand("PARTSERIAL","ACTIVE_SEPARATE_API.Separate_Mro_Part_Serial");
         cmd.addParameter("HEAD_PART_NO");
         cmd.addParameter("HEAD_SERIAL_NO");
         cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

         cmd = trans.addCustomFunction("GET0PCOND","PART_SERIAL_CATALOG_API.Get_Operational_Condition_Db","OP_COND");
         cmd.addReference("HEAD_PART_NO","PARTSERIAL/DATA");
         cmd.addReference("HEAD_SERIAL_NO","PARTSERIAL/DATA");

         cmd = trans.addCustomFunction("GETOBJSTATE","PART_SERIAL_CATALOG_API.Get_Objstate","OBJ_STATE");
         cmd.addReference("HEAD_PART_NO","PARTSERIAL/DATA");
         cmd.addReference("HEAD_SERIAL_NO","PARTSERIAL/DATA");

         cmd = trans.addCustomFunction("GETISCONN","CONNECT_CUSTOMER_ORDER_API.Is_Wo_Connected_To_Mro_Line","ISCONN");
         cmd.addParameter("CUST_ORDER_NO",headset.getValue("CUST_ORDER_NO"));
         cmd.addParameter("WO_NO",headset.getValue("WO_NO"));
         cmd.addReference("HEAD_PART_NO","PARTSERIAL/DATA");

         trans = mgr.perform(trans);

         String opCond   = trans.getValue("GET0PCOND/DATA/OP_COND");
         String objState = trans.getValue("GETOBJSTATE/DATA/OBJ_STATE");
         String isConn   = trans.getValue("GETISCONN/DATA/ISCONN");

         if ("NON_OPERATIONAL".equals(opCond) && "InInventory".equals(objState) && "FALSE".equals(isConn))
            return true;
         else
            return false;
      }
      else
         return false;
   }

   public void checkObjAvailable()
   {
      ASPManager mgr = getASPManager();

      if (!isSecurityChecked)
      {
         trans.clear();

         trans.addSecurityQuery("WORK_ORDER_CODING,"+
                                "WORK_ORDER_REQUIS_HEADER,"+
                                "ACTIVE_WORK_ORDER,"+
                                "SEPARATE_WORK_ORDER,"+
                                "PART_SERIAL_CATALOG,"+
                                "ACTIVE_SEPARATE_REPAIR,"+
                                "CUSTOMER_AGREEMENT,"+
                                "WORK_ORDER_PLANNING,"+
                                "PRE_ACCOUNTING,"+
                                "MAINTENANCE_OBJECT,"+
                                "EQUIPMENT_SPARE_STRUC_DISTINCT,"+
                                "MATERIAL_REQUIS_RESERVAT,"+
                                "INVENTORY_PART_IN_STOCK_NOPAL,"+
                                "INVENTORY_PART_CONFIG,"+
                                "PURCHASE_PART_SUPPLIER,"+
                                "MAINT_MATERIAL_REQUISITION,"+
                                "MRO_WO_FROM_VIM");

         trans.addSecurityQuery("Work_Order_Coding_API","Authorize");
         trans.addSecurityQuery("MAINT_MATERIAL_REQUISITION_RPI","Report_Printout");
         trans.addSecurityQuery("MAINT_MATERIAL_REQ_LINE_API","Make_Reservation_Short,Make_Auto_Issue_Detail,Make_Manual_Issue_Detail");         
         trans.addSecurityQuery("PURCH_OBJ_RECEIVE_ORD_UTIL_API","Create_Mro_Receive_Order");
         trans.addSecurityQuery("IDENTITY_INVOICE_INFO_API","Get_Identity_Type");
         trans.addSecurityQuery("CONNECT_CUSTOMER_ORDER_API","Connect_Wo_To_Mro_Line");
         trans.addSecurityQuery("CONNECT_CUSTOMER_ORDER_API","Is_Wo_Connected_To_Mro_Line");

         trans.addPresentationObjectQuery("PCMW/ActiveSeparate2ServiceManagement.page,"+
                                          "PCMW/WorkOrderReportPageOvw2.page,"+
                                          "PCMW/WorkOrderCoding1.page,"+
                                          "PCMW/WorkOrderRequisHeaderRMB.page,"+
                                          "PCMW/WorkOrderPermit.page,"+
                                          "PCMW/ActiveSeparateReportCOInfo.page,"+
                                          "PCMW/ActiveSeparate2WorkCenterLoad.page,"+
                                          "PCMW/ActiveWorkOrder4.page,"+
                                          "PCMW/SeparateWorkOrder.page,"+
                                          "PCMW/PartSerialCatalogDlg.page,"+
                                          "PCMW/ActiveSeparateDlg.page,"+
                                          "PCMW/MoveNonSerialToInventDlg.page,"+
                                          "PCMW/WorkOrderRolePlanningLov.page,"+
                                          "MPCCOW/PreAccountingDlg.page,"+
                                          "PCMW/MaintenanceObject2.page,"+
                                          "PCMW/MaintenaceObject3.page,"+
                                          //"PCMW/RMBEquipmentSpareStructure.page,"+
                                          "EQUIPW/EquipmentSpareStructure3.page,"+
                                          "PCMW/MaterialRequisReservatDlg.page,"+
                                          "PCMW/MaterialRequisReservatDlg2.page,"+
                                          "PCMW/InventoryPartLocationDlg.page,"+
                                          "INVENW/InventoryPartAvailabilityPlanningQry.page,"+
                                          "PURCHW/PurchasePartSupplier.page,"+
                                          "PCMW/ActiveWorkOrder.page");

         trans = mgr.perform(trans);

         ASPBuffer secViewBuff = trans.getSecurityInfo();

         actionsBuffer = mgr.newASPBuffer();

         if (secViewBuff.namedItemExists("PCMW/ActiveSeparate2ServiceManagement.page"))
            actionsBuffer.addItem("okHeadPrepare","");

         if (secViewBuff.namedItemExists("PCMW/WorkOrderReportPageOvw2.page"))
            actionsBuffer.addItem("okHeadFreeNotes","");

         if (secViewBuff.itemExists("WORK_ORDER_CODING") && secViewBuff.namedItemExists("PCMW/WorkOrderCoding1.page"))
            actionsBuffer.addItem("okHeadPostings","");

         if (secViewBuff.itemExists("WORK_ORDER_REQUIS_HEADER") && secViewBuff.namedItemExists("PCMW/WorkOrderRequisHeaderRMB.page"))
            actionsBuffer.addItem("okHeadRequisitions","");

         if (secViewBuff.namedItemExists("PCMW/WorkOrderPermit.page"))
            actionsBuffer.addItem("okHeadPermits","");

         if (secViewBuff.namedItemExists("PCMW/ActiveSeparateReportCOInfo.page"))
            actionsBuffer.addItem("okHeadCoInformation","");

         if (secViewBuff.namedItemExists("PCMW/ActiveSeparate2WorkCenterLoad.page"))
            actionsBuffer.addItem("okHeadWorkCenterLoad","");

         if (secViewBuff.itemExists("PRE_ACCOUNTING") && secViewBuff.namedItemExists("MPCCOW/PreAccountingDlg.page"))
            actionsBuffer.addItem("okHeadPreposting","");

         if (secViewBuff.itemExists("ACTIVE_WORK_ORDER") && secViewBuff.namedItemExists("PCMW/ActiveWorkOrder4.page"))
            actionsBuffer.addItem("okHeadTransferToCusOrder","");

         if (secViewBuff.itemExists("SEPARATE_WORK_ORDER") && secViewBuff.namedItemExists("PCMW/SeparateWorkOrder.page"))
            actionsBuffer.addItem("okHeadStructure","");

         if (secViewBuff.itemExists("PART_SERIAL_CATALOG") && secViewBuff.namedItemExists("PCMW/PartSerialCatalogDlg.page"))
            actionsBuffer.addItem("okHeadPlaceSerial","");

         if (secViewBuff.namedItemExists("PCMW/ActiveSeparateDlg.page"))
            actionsBuffer.addItem("okHeadMoveSerial","");

         if (secViewBuff.itemExists("ACTIVE_SEPARATE_REPAIR") && secViewBuff.namedItemExists("PCMW/MoveNonSerialToInventDlg.page"))
            actionsBuffer.addItem("okHeadMoveNonSerial","");

         if (secViewBuff.itemExists("CUSTOMER_AGREEMENT"))
            actionsBuffer.addItem("okHeadPrintAuth","");

         if (secViewBuff.itemExists("WORK_ORDER_PLANNING") && secViewBuff.namedItemExists("PCMW/WorkOrderRolePlanningLov.page"))
            actionsBuffer.addItem("okItem1CreateFromPlanTime","");

         if (secViewBuff.itemExists("Work_Order_Coding_API.Authorize") && secViewBuff.namedItemExists("PCMW/WorkOrderRolePlanningLov.page"))
            actionsBuffer.addItem("okItem1ConnectToPlanTime","");

         if (secViewBuff.itemExists("PRE_ACCOUNTING") && secViewBuff.namedItemExists("MPCCOW/PreAccountingDlg.page"))
         {
            actionsBuffer.addItem("okItem3PrePostHead","");
            actionsBuffer.addItem("okItemPrePostHead","");
         }

         if (secViewBuff.itemExists("MAINT_MATERIAL_REQUISITION_RPI.Report_Printout"))
            actionsBuffer.addItem("okItem3PrintPicList","");

         if (secViewBuff.itemExists("MAINTENANCE_OBJECT"))
         {
            if (secViewBuff.namedItemExists("PCMW/MaintenanceObject2.page"))
               actionsBuffer.addItem("okItemSparePartObject","");
            if (secViewBuff.namedItemExists("PCMW/MaintenaceObject3.page"))
               actionsBuffer.addItem("okItemObjStructure","");
         }

         /*
         if (secViewBuff.itemExists("EQUIPMENT_SPARE_STRUC_DISTINCT") && secViewBuff.namedItemExists("PCMW/RMBEquipmentSpareStructure.page"))
             actionsBuffer.addItem("okItemDetchedPartList","");
         */
         if (secViewBuff.itemExists("EQUIPMENT_SPARE_STRUC_DISTINCT") && secViewBuff.namedItemExists("EQUIPW/EquipmentSpareStructure3.page"))
            actionsBuffer.addItem("okItemDetchedPartList","");

         if (secViewBuff.itemExists("MAINT_MATERIAL_REQ_LINE_API.Make_Reservation_Short"))
            actionsBuffer.addItem("okItemReserve","");

         if (secViewBuff.itemExists("MATERIAL_REQUIS_RESERVAT"))
         {
            if (secViewBuff.namedItemExists("PCMW/MaterialRequisReservatDlg.page"))
               actionsBuffer.addItem("okItemManReserve","");
            if (secViewBuff.namedItemExists("PCMW/MaterialRequisReservatDlg2.page"))
               actionsBuffer.addItem("okItemUnreserve","");
         }

         if (secViewBuff.itemExists("MAINT_MATERIAL_REQ_LINE_API.Make_Auto_Issue_Detail"))
            actionsBuffer.addItem("okItemIssue","");

         if (secViewBuff.itemExists("INVENTORY_PART_IN_STOCK_NOPAL") && secViewBuff.namedItemExists("PCMW/InventoryPartLocationDlg.page") && secViewBuff.itemExists("MAINT_MATERIAL_REQ_LINE_API.Make_Manual_Issue_Detail"))
            actionsBuffer.addItem("okItemManIssue","");

         if (secViewBuff.itemExists("INVENTORY_PART_CONFIG") && secViewBuff.namedItemExists("INVENW/InventoryPartAvailabilityPlanningQry.page"))
            actionsBuffer.addItem("okItemAvailDetail","");

         if (secViewBuff.itemExists("PURCHASE_PART_SUPPLIER") && secViewBuff.namedItemExists("PURCHW/PurchasePartSupplier.page"))
            actionsBuffer.addItem("okItemSupPerPart","");

         if (secViewBuff.itemExists("MAINT_MATERIAL_REQUISITION") && secViewBuff.namedItemExists("PCMW/ActiveWorkOrder.page"))
            actionsBuffer.addItem("okItemMatReqUnissue","");

         if (secViewBuff.itemExists("PURCH_OBJ_RECEIVE_ORD_UTIL_API.Create_Mro_Receive_Order") && secViewBuff.itemExists("IDENTITY_INVOICE_INFO_API.Get_Identity_Type"))
            actionsBuffer.addItem("mroObjReceiveO","");

         if (secViewBuff.itemExists("CONNECT_CUSTOMER_ORDER_API.Connect_Wo_To_Mro_Line") && secViewBuff.itemExists("CONNECT_CUSTOMER_ORDER_API.Is_Wo_Connected_To_Mro_Line"))
         {
            actionsBuffer.addItem("returnObj","");   
            actionsBuffer.addItem("returnNonOpObj","");   
         }

         isSecurityChecked = true;
      }
   }

   public void adjustActions()
   {
      ASPManager mgr = getASPManager();

      // Removing actions which are not allowed to the user.
      // headbar
      if (!actionsBuffer.itemExists("okHeadFreeNotes"))
         headbar.removeCustomCommand("freeNotes");

      if (!actionsBuffer.itemExists("okHeadPostings"))
         headbar.removeCustomCommand("postings");

      if (!actionsBuffer.itemExists("okHeadRequisitions"))
         headbar.removeCustomCommand("requisitions");

      if (!actionsBuffer.itemExists("okHeadPermits"))
         headbar.removeCustomCommand("permits");

      if (!actionsBuffer.itemExists("okHeadCoInformation"))
         headbar.removeCustomCommand("coInformation");

      if (!actionsBuffer.itemExists("okHeadWorkCenterLoad"))
         headbar.removeCustomCommand("workCenterLoad");

      if (!actionsBuffer.itemExists("okHeadPreposting"))
         headbar.removeCustomCommand("preposting");

      if (!actionsBuffer.itemExists("okHeadTransferToCusOrder"))
         headbar.removeCustomCommand("transferToCusOrder");

      if (!actionsBuffer.itemExists("okHeadStructure"))
         headbar.removeCustomCommand("structure");

      if (!actionsBuffer.itemExists("okHeadPlaceSerial"))
         headbar.removeCustomCommand("placeSerial");

      if (!actionsBuffer.itemExists("okHeadMoveSerial"))
         headbar.removeCustomCommand("moveSerial");

      if (!actionsBuffer.itemExists("okHeadMoveNonSerial"))
         headbar.removeCustomCommand("moveNonSerial");

      if (!actionsBuffer.itemExists("okHeadPrintAuth"))
         headbar.removeCustomCommand("printAuth");

      if (!actionsBuffer.itemExists("mroObjReceiveO"))
         headbar.removeCustomCommand("mroObjReceiveO");
      else
      {
         if (headset.countRows()>0 && headlay.isSingleLayout())
         {
            if (!checkMROObjRO())
               headbar.removeCustomCommand("mroObjReceiveO");
         }
      }   

      if (!actionsBuffer.itemExists("returnObj"))
         headbar.removeCustomCommand("returnObj");
      else
      {
         if (headset.countRows()>0 && headlay.isSingleLayout())
         {
            if (!checkReturnObj())
               headbar.removeCustomCommand("returnObj");
         }
      }   

      if (!actionsBuffer.itemExists("returnNonOpObj"))
         headbar.removeCustomCommand("returnNonOpObj");
      else
      {
         if (headset.countRows()>0 && headlay.isSingleLayout())
         {
            if (!checkReturnNonOpObj())
               headbar.removeCustomCommand("returnNonOpObj");
         }
      }   

      // itembar1
      if (!actionsBuffer.itemExists("okItem1CreateFromPlanTime"))
         itembar1.removeCustomCommand("createFromPlanTime");

      if (!actionsBuffer.itemExists("okItem1ConnectToPlanTime"))
         itembar1.removeCustomCommand("connectToPlanTime");

      // itembar3
      if (!actionsBuffer.itemExists("okItem3PrePostHead"))
      {
         itembar3.removeCustomCommand("prePostHead");
         itembar.removeCustomCommand("prePostHead");
      }

      if (!actionsBuffer.itemExists("okItem3PrintPicList"))
         itembar3.removeCustomCommand("printPicList");

      // itembar

      if (!actionsBuffer.itemExists("okItemDetchedPartList"))
         itembar.removeCustomCommand("detchedPartList");

      if (!actionsBuffer.itemExists("okItemReserve"))
         itembar.removeCustomCommand("reserve");

      if (!actionsBuffer.itemExists("okItemManReserve"))
         itembar.removeCustomCommand("manReserve");

      if (!actionsBuffer.itemExists("okItemUnreserve"))
         itembar.removeCustomCommand("unreserve");

      if (!actionsBuffer.itemExists("okItemIssue"))
         itembar.removeCustomCommand("issue");

      if (!actionsBuffer.itemExists("okItemManIssue"))
         itembar.removeCustomCommand("manIssue");

      if (!actionsBuffer.itemExists("okItemAvailDetail"))
         itembar.removeCustomCommand("availDetail");

      if (!actionsBuffer.itemExists("okItemSupPerPart"))
         itembar.removeCustomCommand("supPerPart");

      if (!actionsBuffer.itemExists("okItemMatReqUnissue"))
         itembar.removeCustomCommand("matReqUnissue");

      if (!actionsBuffer.itemExists("okItemSparePartObject"))
         itembar.removeCustomCommand("sparePartObject");

      if (!actionsBuffer.itemExists("okItemObjStructure"))
         itembar.removeCustomCommand("objStructure");

      if (!actionsBuffer.itemExists("okHeadPrepare"))
         headbar.removeCustomCommand("prepareWO");
   }

   public void adjust()
   {
      ASPManager mgr = getASPManager();

      //Call Id 114222
      if (headset.countRows() > 0 && headlay.isSingleLayout())
      {
         if (itemset.countRows()==0)
         {
            itembar.removeCustomCommand("availDetail");
            itembar.removeCustomCommand("supPerPart");
            itembar.removeCustomCommand("reserve");
            itembar.removeCustomCommand("manReserve");
            itembar.removeCustomCommand("unreserve");
            itembar.removeCustomCommand("issue");
            itembar.removeCustomCommand("manIssue");
            itembar.removeCustomCommand("matReqUnissue");
            itembar.removeCustomCommand("prePostHead");  
         }
      }
      //

      headbar.removeCustomCommand("activateBudgetTab");
      headbar.removeCustomCommand("activateTimeReportTab");
      headbar.removeCustomCommand("activateMaterialTab");
      headbar.removeCustomCommand("activateJobsTab");

      headbar.removeCustomCommand("refreshForm");
      headbar.removeCustomCommand("refreshMaterialTab");

      lblempno = mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMLBLABC: Employee+ID");
      lblempsig =  mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMLBLABCD: Signature");
      lblrepBy1 = mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMLBLABCDE: Reported+by");
      empno = mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMABCSM: List+of+Employee+ID");
      empsig =  mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMABCDSM: List+of+Signature");
      repBy1 = mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMABCDESM: List+of+Reported+by");

      if (mgr.isPresentationObjectInstalled("docmaw/DocReference.page"))
         mgr.getASPField("CBHASDOCUMENTS").setHyperlink("../docmaw/DocReference.page","LU_NAME,KEY_REF","NEWWIN");
      if (mgr.isPresentationObjectInstalled("enterw/CustomerInfo.page"))
         mgr.getASPField("CUSTOMER_NO").setHyperlink("../enterw/CustomerInfo.page","CUSTOMER_NO","NEWWIN");
      if (mgr.isPresentationObjectInstalled("equipw/EquipmentAllObjectLightRedirect.page"))
         mgr.getASPField("MCH_CODE").setHyperlink("../equipw/EquipmentAllObjectLightRedirect.page","MCH_CODE,MCH_CODE_CONTRACT,WO_NO","NEWWIN");
      mgr.getASPField("EMP_SIGNATURE").setReadOnly();
      mgr.getASPField("NAME").setReadOnly();

      if (headset.countRows() == 1)
         headbar.disableCommand(headbar.BACK);

      if (itemlay1.isMultirowLayout()|| itemlay1.isSingleLayout())
         itembar1.enableCommand(itembar1.FIND);

      if (headset.countRows()>0)
         comp = headset.getRow().getValue("COMPANY");

      if (headlay.isFindLayout() || itemlay1.isFindLayout())
         isFind = true;

      if ((itemset4.countRows() == 6) && !headlay.isMultirowLayout())
      {
         sumColumns(); 
         itemset4.goTo(item4CurrRow);
      }

      if (headset.countRows() > 0)
      {
         if ((itemset4.countRows() < 6) && !headlay.isMultirowLayout())
         {
            okFindITEM4(); 
            sumColumns(); 
            itemset4.goTo(item4CurrRow);
         }
      }

      if (item4CurrRow == 6 && itemlay4.isSingleLayout())
      {
         itembar4.disableCommand(itembar4.EDITROW);
      }

      disableMoveStoIn();
      disableCusCmd();

      title_ = mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSMTRE: Report In Work Order for Service Management");

      // To be considered when toggled to master detail, for no loss of data

      if (headlay.isMultirowLayout())
      {
         if (mgr.isPresentationObjectInstalled("equipw/EquipmentAllObjectLightRedirect.class"))
            mgr.getASPField("MCH_CODE").setHyperlink("../equipw/EquipmentAllObjectLightRedirect.page","MCH_CODE,MCH_CODE_CONTRACT,WO_NO","NEWWIN");
      }

      if (headset.countRows()>0)
      {
         headrowno = headset.getCurrentRowNo();
         setCheckBoxValue(headrowno);
         setDisable(headrowno);
      }

      if (headset.countRows() > 0)
         sWONo = headset.getRow().getValue("WO_NO");
      else
         sWONo   = "";

      if (matSingleMode || "TRUE".equals(showMat))
      {
         this.activateMaterialTab();
         itemlay3.setLayoutMode(itemlay3.SINGLE_LAYOUT);
         String matLineStr = ctx.findGlobal("CURRROWGLOBAL");    
         int matLine = Integer.parseInt(matLineStr);

         itemset3.goTo(matLine);
         clearItem4();
         okFindITEM5();
         matSingleMode = false;
      }

      if ( itemlay1.isNewLayout()  || itemlay1.isEditLayout() || itemlay3.isNewLayout()  || itemlay3.isEditLayout() || 
           itemlay4.isEditLayout() || itemlay1.isFindLayout() || itemlay3.isFindLayout() || itemlay4.isFindLayout() ||
           itemlay6.isNewLayout()  || itemlay6.isEditLayout() )
      {
         headbar.removeCustomCommand("observed");
         headbar.removeCustomCommand("underPrep");
         headbar.removeCustomCommand("prepared");
         headbar.removeCustomCommand("released");
         headbar.removeCustomCommand("started");
         headbar.removeCustomCommand("workDone");
         headbar.removeCustomCommand("reported");
         headbar.removeCustomCommand("finished");
         headbar.removeCustomCommand("cancelled");
         headbar.removeCustomCommand("printAuth");
         headbar.removeCustomCommand("moveSerial");
         headbar.removeCustomCommand("structure");
         headbar.removeCustomCommand("freeNotes");
         headbar.removeCustomCommand("postings");
         headbar.removeCustomCommand("requisitions");
         headbar.removeCustomCommand("permits");
         headbar.removeCustomCommand("preposting");
         headbar.removeCustomCommand("transferToCusOrder");
         headbar.removeCustomCommand("workCenterLoad");
         headbar.removeCustomCommand("placeSerial");
         headbar.removeCustomCommand("coInformation");
         headbar.removeCustomCommand("moveNonSerial");

         headbar.disableCommand(headbar.DELETE);
         headbar.disableCommand(headbar.NEWROW);
         headbar.disableCommand(headbar.EDITROW);
         headbar.disableCommand(headbar.DELETE);
         headbar.disableCommand(headbar.DUPLICATEROW);
         headbar.disableCommand(headbar.FIND);
         headbar.disableCommand(headbar.BACKWARD);
         headbar.disableCommand(headbar.FORWARD);
      }

      if (mgr.isModuleInstalled("PROJ") && !mgr.isPresentationObjectInstalled("projw/ConObjToActivityDlg.page"))
      {
         headbar.disableCustomCommand("connectToActivity");
         headbar.disableCustomCommand("disconnectFromActivity");
         headbar.disableCustomCommand("activityInfo");
      }

      if (headset.countRows()>0 && itemlay6.isVisible())
      {
         String sWhereStrForITEM6 = "CONTRACT = '" + headset.getValue("CONTRACT") + "'";

         if (itemlay6.isFindLayout())
         {
            mgr.getASPField("STD_JOB_ID").setLOV("StandardJobLov1.page", 600, 450);
            sWhereStrForITEM6 = sWhereStrForITEM6 + " AND STANDARD_JOB_TYPE_DB = '1'";
         }

         mgr.getASPField("STD_JOB_ID").setLOVProperty("WHERE", sWhereStrForITEM6);
      }

      if (headset.countRows()>0 && itemlay3.isVisible())
      {
         mgr.getASPField("ITEM3_CONTRACT").setLOVProperty("WHERE","Site_API.Get_Company(CONTRACT) = Site_API.Get_Company('"+headset.getRow().getValue("CONTRACT")+"')");

      }
      // Bug Id 70012, Start
      if (mgr.isModuleInstalled("PCMSCI"))
      {
         bPcmsciExist = true;
      }
      // Bug Id 70012, End

      //(+) Bug 66406, Start
      if (itemlay6.isFindLayout())
         mgr.getASPField("CONN_PM_NO").setLOV("../pcmw/PmActionLov1.page",600,450);
      else if (itemlay6.isNewLayout())
         mgr.getASPField("CONN_PM_NO").setLOV("../pcmw/PmActionLov.page",600,450);
      //(+) Bug 66406, End 

   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return title_;
   }

   protected String getTitle()
   {
      return "PCMWACTIVESEPERATEREPORTINWORKORDERSMTITLE: Report In Work Order for Service Management";
   }

   protected AutoString getContents() throws FndException
   { 
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();
      out.clear();

      if (bFirstRequest && mgr.isExplorer())
      {
         out.append("<html>\n"); 
         out.append("<head></head>\n"); 
         out.append("<body>"); 
         out.append("<form name='form' method='POST'action='"+mgr.getURL()+"?"+mgr.getQueryString()+"'>"); 
         out.append(fmt.drawHidden("FIRST_REQUEST", "OK"));            
         out.append("</form></body></html>"); 
         appendDirtyJavaScript("document.form.submit();"); 
         return out;
      }

      out.append("<html>\n");
      out.append("<head>");
      out.append(mgr.generateHeadTag("PCMWACTIVESEPERATEREPORTINWORKORDERSMTITLE: Report In Work Order for Service Management"));
      out.append("<title></title>\n");
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append(mgr.startPresentation(title_));
      out.append(headlay.show());


      if (headlay.isSingleLayout() && (headset.countRows() > 0))
      {
         out.append(tabs.showTabsInit());

         if (tabs.getActiveTab() == 1)
            out.append(itemlay4.show());
         else if (tabs.getActiveTab() == 2)
            out.append(itemlay1.show());
         else if (tabs.getActiveTab() == 3)
         {
            out.append(itemlay3.show());

            if (itemlay3.isSingleLayout() && itemset3.countRows()>0)
            {
               out.append(itemlay.show());
            }
         }
         else if (tabs.getActiveTab() == 4)
            out.append(itemlay6.show());
      }

      out.append(fmt.drawHidden("STATEVAL", ""));            
      out.append(fmt.drawHidden("PNT", ""));            
      out.append(fmt.drawHidden("CREFROMPLAN", ""));            
      out.append(fmt.drawHidden("BUTTONVAL", ""));            
      out.append(fmt.drawHidden("HIDDENPARTNO", ""));            
      out.append(fmt.drawHidden("ONCEGIVENERROR", "FALSE"));            
      out.append(fmt.drawHidden("CREREPNONSER", openCreRepNonSer));            
      out.append(fmt.drawHidden("ACTIVITY_SEQ_PROJ", ""));
      out.append(fmt.drawHidden("REFRESH_FLAG", "FALSE"));
      out.append(fmt.drawHidden("REFRESH_BLOCK", ""));
      //Bug 68947, Start   
      out.append(fmt.drawHidden("TEMPCONTRACTID",""));
      out.append(fmt.drawHidden("TEMPLINENO", ""));
      //Bug 68947, End 
      //Bug 89703, Start
      out.append(fmt.drawHidden("HASPLANNING", ""));
      //Bug 89703, End

      appendDirtyJavaScript("window.name = \"ReportInWOSM\";\n");

      appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(unissue)); // Bug Id68773
      appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"PageID_my_cookie1\")==\"TRUE\")\n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript("      if (confirm(\""); 
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATEREPSMWOORDERSMUNISSUE: All required material has not been issued. Do you want to continue ?"));
      appendDirtyJavaScript("\")) {\n");
      appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"AAAA\";\n");
      appendDirtyJavaScript("		writeCookie(\"PageID_my_cookie1\", \"FALSE\", '', COOKIE_PATH); \n");
      appendDirtyJavaScript("		f.submit();\n");
      appendDirtyJavaScript("     } \n");
      appendDirtyJavaScript("} \n");

      appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(unissue));  // Bug Id 68773
      appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"PageID_my_cookie2\")==\"TRUE\")\n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript("      if (confirm(\""); 
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATEREPSMWOORDERSMUNISSUE: All required material has not been issued. Do you want to continue ?"));
      appendDirtyJavaScript("\")) {\n");
      appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"CCCC\";\n");
      appendDirtyJavaScript("		writeCookie(\"PageID_my_cookie2\", \"FALSE\", '', COOKIE_PATH); \n");
      appendDirtyJavaScript("		f.submit();\n");
      appendDirtyJavaScript("     } \n");
      appendDirtyJavaScript("} \n");

      appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(repair)); // Bug Id 68773
      appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"PageID_my_cookie\")==\"TRUE\")\n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript("      if (confirm(\"");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATEREPORTINWORKORDERSMREPAIR: This object is in repair shop. Do you still want to continue?"));
      appendDirtyJavaScript("\")) {\n");
      appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"BBBB\";\n");
      appendDirtyJavaScript("		writeCookie(\"PageID_my_cookie\", \"FALSE\", '', COOKIE_PATH); \n");
      appendDirtyJavaScript("		f.submit();\n");
      appendDirtyJavaScript("     } \n");
      appendDirtyJavaScript("} \n");

      appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(openCreRepNonSer);
      appendDirtyJavaScript("' == \"TRUE\")\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (document.form.CREREPNONSER.value == \"TRUE\")\n");
      appendDirtyJavaScript("      window.open('");
      appendDirtyJavaScript(creRepNonSerPath);
      appendDirtyJavaScript("',\"createRepWONonSer\",\"alwaysRaised=yes,resizable,status=yes,scrollbars=yes,width=790,height=575\");\n");
      appendDirtyJavaScript("   document.form.CREREPNONSER.value = \"FALSE\";\n");
      appendDirtyJavaScript("}\n");

      //Bug 70948, start
      appendDirtyJavaScript("function lovEmpNo(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript(" if(document.form.TEAM_ID.value != '')\n");                           
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("    if(document.form.ROLE_CODE.value == '')\n");
      appendDirtyJavaScript("	   whereCond1 = \"EMPLOYEE_ID IN (SELECT MEMBER_EMP_NO FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID='\" + URLClientEncode(document.form.TEAM_ID.value) +\"' AND CONTRACT = '\"+ document.form.TEAM_CONTRACT.value+\"') \";\n"); 
      appendDirtyJavaScript("       else\n");
      appendDirtyJavaScript("	   whereCond1 = \"(EMP_NO,1)IN (SELECT MEMBER_EMP_NO,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\" + URLClientEncode(document.form.ROLE_CODE.value) + \"',MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID='\" + URLClientEncode(document.form.TEAM_ID.value) +\"' AND CONTRACT = '\"+ document.form.TEAM_CONTRACT.value+\"') \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM1_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('EMP_NO',i).indexOf('%') !=-1)? getValue_('EMP_NO',i):'';\n");
      appendDirtyJavaScript("	if( getValue_('ROLE_CODE',i)=='' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("              openLOVWindow('EMP_NO',i,\n");
      appendDirtyJavaScript("                  '../mscomw/MaintEmployeeLov.page?__FIELD=Executed+By&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('EMP_NO',i))\n"); 
      appendDirtyJavaScript("                  + '&PERSON_ID=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateEmpNo');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("       {\n");        
      appendDirtyJavaScript("              openLOVWindow('EMP_NO',i,\n");
      appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Executed+By&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('EMP_NO',i))\n"); 
      appendDirtyJavaScript("                  + '&SIGNATURE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");       
      appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateEmpNo');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("}\n");
      //Bug 70948, end

      appendDirtyJavaScript("function lovEmpSignature(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	ss = '");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(comp));  // XSS_Safe ILSOLK 20070709
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("	if('");
      appendDirtyJavaScript(isFind); // XSS_Safe ILSOLK 20070713
      appendDirtyJavaScript("' == 'true')\n");
      appendDirtyJavaScript("    { \n");
      appendDirtyJavaScript("	openLOVWindow('EMP_SIGNATURE',i,\n");
      appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
      appendDirtyJavaScript(lblempsig);
      appendDirtyJavaScript("&__TITLE=");
      appendDirtyJavaScript(empsig);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("		+ '&COMPANY='+ ss ,600,445,'validateEmpSignature');\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	else\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("        openLOVWindow('EMP_SIGNATURE',i,\n");
      appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
      appendDirtyJavaScript(lblempsig);
      appendDirtyJavaScript("&__TITLE=");
      appendDirtyJavaScript(empsig);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
      appendDirtyJavaScript("		,600,445,'validateEmpSignature');\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovReportedBy(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	ss = '");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(comp)); // XSS_Safe ILSOLK 20070709
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("	if('");
      appendDirtyJavaScript(isFind); // XSS_Safe ILSOLK 20070713
      appendDirtyJavaScript("' == 'true')\n");
      appendDirtyJavaScript("    { \n");
      appendDirtyJavaScript("        openLOVWindow('REPORTED_BY',i,\n");
      appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
      appendDirtyJavaScript(lblrepBy1);
      appendDirtyJavaScript("+by&__TITLE=");
      appendDirtyJavaScript(repBy1);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("		,600,450,'validateReportedBy');\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("	else\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("	   openLOVWindow('REPORTED_BY',i,\n");
      appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
      appendDirtyJavaScript(lblrepBy1);
      appendDirtyJavaScript("+by&__TITLE=");
      appendDirtyJavaScript(repBy1);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
      appendDirtyJavaScript("		,600,450,'validateReportedBy');\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovTestPointId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if('");
      appendDirtyJavaScript(isFind); // XSS_Safe ILSOLK 20070713
      appendDirtyJavaScript("' == 'true')\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("        openLOVWindow('TEST_POINT_ID',i,\n");
      appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EQUIPMENT_OBJECT_TEST_PNT&__FIELD=Test+point&__INIT=1'\n");
      appendDirtyJavaScript("		,600,450,'validateTestPointId');\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("        openLOVWindow('TEST_POINT_ID',i,\n");
      appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EQUIPMENT_OBJECT_TEST_PNT&__FIELD=Test+point&__INIT=1'\n");
      appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
      appendDirtyJavaScript("		,600,450,'validateTestPointId');    \n");
      appendDirtyJavaScript("    }    \n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovItem1OrgCode(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      //Bug Id 86298, Start
      appendDirtyJavaScript("        lovtitle = '");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPERATEREPORTINWORKORDERSMLOVTIT: List of Maintenance Organization"));
      appendDirtyJavaScript("';\n");
       //Bug Id 86298, End
      appendDirtyJavaScript(" if (document.form.ROLE_CODE.value != '') \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if((document.form.EMP_NO.value == '') && (document.form.ITEM1_CONTRACT.value == ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ROLE_CODE.value) + \"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.EMP_NO.value == '') && (document.form.ITEM1_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ROLE_CODE.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM1_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM1_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.EMP_NO.value != '') && (document.form.ITEM1_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ROLE_CODE.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM1_CONTRACT.value)+\"' AND EMP_NO = '\" +URLClientEncode(document.form.EMP_NO.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM1_CONTRACT.value)+\"' AND MEMBER_EMP_NO = '\" +URLClientEncode(document.form.EMP_NO.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else if ((document.form.ROLE_CODE.value == '') && (document.form.EMP_NO.value != '')) \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if (document.form.ITEM1_CONTRACT.value == '') \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMPLOYEE_ID = '\" +URLClientEncode(document.form.EMP_NO.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" +URLClientEncode(document.form.EMP_NO.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMPLOYEE_ID = '\" +URLClientEncode(document.form.EMP_NO.value)+\"' AND CONTRACT = '\" +URLClientEncode(document.form.ITEM1_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" +URLClientEncode(document.form.EMP_NO.value)+\"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM1_CONTRACT.value)+\"'\";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else if ((document.form.ROLE_CODE.value == '') && (document.form.EMP_NO.value == '')) \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if (document.form.ITEM1_CONTRACT.value != '') \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" CONTRACT = '\" +URLClientEncode(document.form.ITEM1_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM1_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if (document.form.ITEM1_CONTRACT.value != '') \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" CONTRACT = '\" +URLClientEncode(document.form.ITEM1_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM1_CONTRACT.value)+\"' \";\n"); 
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
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM1_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM1_ORG_CODE',i).indexOf('%') !=-1)? getValue_('ITEM1_ORG_CODE',i):'';\n");
      appendDirtyJavaScript("	if( getValue_('ROLE_CODE',i)=='' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("	            if( getValue_('EMP_NO',i)=='' )\n");
      appendDirtyJavaScript("             {\n");
      appendDirtyJavaScript("                  openLOVWindow('ITEM1_ORG_CODE',i,\n");
      //Bug Id 86298, Start
      appendDirtyJavaScript("                     '../mscomw/OrgCodeAllowedSiteLov.page?__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&__TITLE='+lovtitle+'&MULTICHOICE='+enable_multichoice+''\n");
      //Bug Id 86298, End
      appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n"); 
      appendDirtyJavaScript("                      + '&ORG_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
      appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	             ,550,500,'validateItem1OrgCode');\n");
      appendDirtyJavaScript("             }\n");
      appendDirtyJavaScript("	            else\n");
      appendDirtyJavaScript("             {\n");
      appendDirtyJavaScript("                 openLOVWindow('ITEM1_ORG_CODE',i,\n");
      //Bug Id 86298, Start
      appendDirtyJavaScript("                     '../mscomw/MaintEmployeeLov.page?&__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&__TITLE='+lovtitle+'&MULTICHOICE='+enable_multichoice+''\n");
      //Bug Id 86298, End
      appendDirtyJavaScript("                     + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n"); 
      appendDirtyJavaScript("                     + '&ORG_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                     + '&EMPLOYEE_ID=' + URLClientEncode(getValue_('EMP_NO',i))\n");
      appendDirtyJavaScript("                     + '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
      appendDirtyJavaScript("                     + '&CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	            ,550,500,'validateItem1OrgCode');\n");
      appendDirtyJavaScript("             }\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("       {\n");        
      appendDirtyJavaScript("              openLOVWindow('ITEM1_ORG_CODE',i,\n");
      //Bug Id 86298, Start
      appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&__TITLE='+lovtitle+'&MULTICHOICE='+enable_multichoice+''\n");
      //Bug Id 86298, End
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n"); 
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");       
      appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateItem1OrgCode');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovRoleCode(i,params)\n");
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
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.EMP_NO.value) + \"' \";\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM1_ORG_CODE.value != '') && (document.form.ITEM1_CONTRACT.value == ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.EMP_NO.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM1_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.EMP_NO.value) + \"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM1_ORG_CODE.value)+\"' \";\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM1_ORG_CODE.value == '') && (document.form.ITEM1_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.EMP_NO.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM1_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.EMP_NO.value) + \"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM1_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM1_ORG_CODE.value != '') && (document.form.ITEM1_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.EMP_NO.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM1_ORG_CODE.value)+ \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM1_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.EMP_NO.value) + \"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM1_ORG_CODE.value)+\"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM1_CONTRACT.value)+\"' \";\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("	   whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.ITEM1_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript("	   whereCond2 = \" Site_API.Get_Company(MAINT_ORG_CONTRACT) = '\" + URLClientEncode(document.form.ITEM1_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if (document.form.TEAM_ID.value != '')\n");
      appendDirtyJavaScript("	   whereCond1 += \" AND 1 IN (SELECT Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,ROLE_CODE,MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.TEAM_ID.value+\"' AND CONTRACT='\" + URLClientEncode(document.form.TEAM_CONTRACT.value) +\"' AND \" +whereCond2 +\")\";\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM1_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ROLE_CODE',i).indexOf('%') !=-1)? getValue_('ROLE_CODE',i):'';\n");
      appendDirtyJavaScript("	if( getValue_('EMP_NO',i)=='' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("                  openLOVWindow('ROLE_CODE',i,\n");
      appendDirtyJavaScript("                     '../mscomw/RoleToSiteLov.page?__FIELD=Craft+ID&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n"); 
      appendDirtyJavaScript("                      + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
      appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	             ,550,500,'validateRoleCode');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("       {\n");        
      appendDirtyJavaScript("              openLOVWindow('ROLE_CODE',i,\n");
      appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Craft+ID&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n"); 
      appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
      appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateRoleCode');\n");
      appendDirtyJavaScript("       }\n");
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
      appendDirtyJavaScript(" if(document.form.ITEM1_COMPANY.value != '')\n");
      appendDirtyJavaScript(" if( whereCond1=='')\n");
      appendDirtyJavaScript("		whereCond1 = \"COMPANY = '\" +URLClientEncode(document.form.ITEM1_COMPANY.value)+\"' \";\n"); 
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("		whereCond1 += \" AND COMPANY = '\" +URLClientEncode(document.form.ITEM1_COMPANY.value)+\"' \";\n"); 
      appendDirtyJavaScript(" if( whereCond1 !='')\n");
      appendDirtyJavaScript("	           whereCond1 += \" AND \";\n"); 
      appendDirtyJavaScript("	whereCond1 += \" to_date(to_char(sysdate,\'YYYYMMDD\'),\'YYYYMMDD\') BETWEEN nvl(VALID_FROM,to_date(\'00010101\',\'YYYYMMDD\')) AND nvl(VALID_TO,to_date(\'99991231\',\'YYYYMMDD\')) \";\n"); 
      appendDirtyJavaScript(" if(document.form.EMP_NO.value != '')\n");
      appendDirtyJavaScript("		whereCond2 = \"MEMBER_EMP_NO = '\" +URLClientEncode(document.form.EMP_NO.value)+\"' \";\n"); 
      appendDirtyJavaScript(" if(document.form.ITEM1_ORG_CODE.value != '')\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript(" if( whereCond2=='')\n");
      appendDirtyJavaScript("		whereCond2 = \"MAINT_ORG = '\" +URLClientEncode(document.form.ITEM1_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript(" else \n");
      appendDirtyJavaScript("		whereCond2+= \" AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM1_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if(document.form.ITEM1_CONTRACT.value != '')\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript(" if(whereCond2=='' )\n");
      appendDirtyJavaScript("		whereCond2 = \"MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM1_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript(" else \n");
      appendDirtyJavaScript("		whereCond2 += \" AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM1_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if(whereCond2 !='' )\n");
      appendDirtyJavaScript("     {\n");
      appendDirtyJavaScript("        if(whereCond1 !='' )\n");
      appendDirtyJavaScript("	           whereCond1 += \" AND \";\n"); 
      appendDirtyJavaScript("        if(document.form.ROLE_CODE.value == '' )\n");
      appendDirtyJavaScript("	           whereCond1 += \" TEAM_ID IN (SELECT TEAM_ID FROM MAINT_TEAM_MEMBER_SERV WHERE \" +whereCond2 +\")\";\n"); 
      appendDirtyJavaScript("        else \n");
      appendDirtyJavaScript("	           whereCond1 += \" (TEAM_ID,1) IN (SELECT TEAM_ID,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\"+ document.form.ROLE_CODE.value+\"' ,MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE \" +whereCond2 +\")\";\n"); 
      appendDirtyJavaScript("     }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM1_IN_FIND_MODE);\n"); 
      appendDirtyJavaScript("	var key_value = (getValue_('TEAM_ID',i).indexOf('%') !=-1)? getValue_('TEAM_ID',i):'';\n"); 
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
      appendDirtyJavaScript(" if (document.form.ITEM1_COMPANY.value != '') \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("	      whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.ITEM1_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM1_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('TEAM_CONTRACT',i).indexOf('%') !=-1)? getValue_('TEAM_CONTRACT',i):'';\n");
      appendDirtyJavaScript("                  openLOVWindow('TEAM_CONTRACT',i,\n");
      appendDirtyJavaScript("'");
      appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=USER_ALLOWED_SITE_LOV&__FIELD=Team+Site&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('TEAM_CONTRACT',i))\n");
      appendDirtyJavaScript("+ '&TEAM_CONTRACT=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript(",550,500,'validateTeamContract');\n");
      appendDirtyJavaScript("}\n");

      //Bug 89399, Start, Added alert when qty is zero
      appendDirtyJavaScript("function checkMando()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (f.QTY.value != '' && f.QTY.value == 0 )  \n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("             alert('");
      appendDirtyJavaScript(                 mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERSMNOTVALIDQTY: Reported hours value for "));
      appendDirtyJavaScript("'+f.ITEM1_WO_NO.value+' ");
      appendDirtyJavaScript(                 mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERSMNOTVALIDQTY1: is not allowed to be zero."));
      appendDirtyJavaScript("             ');\n");
      appendDirtyJavaScript("   return false;\n"); 
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("  return checkItem1Contract(0) &&\n");
      appendDirtyJavaScript("  checkQty(0) &&\n");
      appendDirtyJavaScript("  checkItem1OrgCode(0) &&\n");
      appendDirtyJavaScript("  checkAmount(0);\n");
      appendDirtyJavaScript("}\n");
      //Bug 89399, End

      appendDirtyJavaScript("function checkMando3()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  return checkPartNo(0) &&\n");
      appendDirtyJavaScript("  checkSpareContract(0) && \n");
      appendDirtyJavaScript("  checkQtyRequired(0);\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateNote(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkNote(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('NOTE',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('CB_GENERATE_NOTE',i).value = '';\n");
      appendDirtyJavaScript("		document.form.CB_GENERATE_NOTE.checked = false;\n");
      appendDirtyJavaScript("		document.form.CMB_GENERATE_NOTE.value = document.form.DUMMY_GENERATE_NOTE_NO.value; \n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=NOTE'\n");
      appendDirtyJavaScript("		+ '&NOTE=' + URLClientEncode(getValue_('NOTE',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'NOTE',i,'Inspection Note') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		checkVal = __getValidateValue(0);\n");
      appendDirtyJavaScript("		if(checkVal == 'TRUE')\n");
      appendDirtyJavaScript("		 document.form.CB_GENERATE_NOTE.checked = true;\n");
      appendDirtyJavaScript("		 document.form.CMB_GENERATE_NOTE.value = document.form.DUMMY_GENERATE_NOTE_YES.value; \n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateCbGenerateNote(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkCbGenerateNote(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('CB_GENERATE_NOTE',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('CMB_GENERATE_NOTE',i).value = document.form.DUMMY_GENERATE_NOTE_NO.value;\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=CB_GENERATE_NOTE'\n");
      appendDirtyJavaScript("		+ '&CB_GENERATE_NOTE=' + URLClientEncode(getValue_('CB_GENERATE_NOTE',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'CB_GENERATE_NOTE',i,'Generate Note') )\n");
      appendDirtyJavaScript("	{		\n");
      appendDirtyJavaScript("		if(document.form.CB_GENERATE_NOTE.checked == true)\n");
      appendDirtyJavaScript("		 document.form.CMB_GENERATE_NOTE.value = document.form.DUMMY_GENERATE_NOTE_YES.value; \n");
      appendDirtyJavaScript("		else \n");
      appendDirtyJavaScript("		 document.form.CMB_GENERATE_NOTE.value = document.form.DUMMY_GENERATE_NOTE_NO.value; \n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateEmpNo(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    j = 0;\n");
      appendDirtyJavaScript("    if (i == -1)\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("        j = i;\n");
      appendDirtyJavaScript("        i = -1;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkEmpNo(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('EMP_NO',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('ROLE_CODE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('EMP_SIGNATURE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('NAME',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM1_ORG_CODE',i).value = '';\n");
      appendDirtyJavaScript("        getField_('CATALOG_NO',i).value = '';\n");
      appendDirtyJavaScript("		getField_('SALESPARTCATALOGDESC',i).value = '';\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=EMP_NO'\n");
      appendDirtyJavaScript("		+ '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
      appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'EMP_NO',i,'Employee ID') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('EMP_NO',i,0);\n");
      appendDirtyJavaScript("		assignValue_('ROLE_CODE',i,1);\n");
      appendDirtyJavaScript("		assignValue_('EMP_SIGNATURE',i,2);\n");
      appendDirtyJavaScript("		assignValue_('NAME',i,3);\n");
      appendDirtyJavaScript("		assignValue_('ITEM1_ORG_CODE',i,4);\n");
      appendDirtyJavaScript("		assignValue_('ITEM1_CONTRACT',i,5);\n");
      appendDirtyJavaScript("		assignValue_('CMNT',i,6);\n");
      appendDirtyJavaScript("		assignValue_('CATALOG_NO',i,7);\n");
      appendDirtyJavaScript("		assignValue_('SALESPARTCATALOGDESC',i,8);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	dataElements = r.indexOf('^^^^');\n");  // index will not be -1 only if emp_no is invalid.
      appendDirtyJavaScript("    if ( (j==-1) && (dataElements==-1) )\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("        validateItem1OrgCode(1);\n");
      appendDirtyJavaScript("        validateRoleCode(1);\n");
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
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkItem1OrgCode(i) ) return;\n");
      appendDirtyJavaScript("    r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM1_ORG_CODE'\n");
      appendDirtyJavaScript("		+ '&ITEM1_ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_WO_NO=' + URLClientEncode(getValue_('ITEM1_WO_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&AMOUNT=' + URLClientEncode(getValue_('AMOUNT',i))\n");
      appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
      appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
      appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&SALESPARTCATALOGDESC=' + URLClientEncode(getValue_('SALESPARTCATALOGDESC',i))\n");
      appendDirtyJavaScript("		+ '&CMNT=' + URLClientEncode(getValue_('CMNT',i))\n");
      appendDirtyJavaScript("		+ '&NAME=' + URLClientEncode(getValue_('NAME',i))\n");
      appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'ITEM1_ORG_CODE',i,'Maintenance Organization') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('ITEM1_ORG_CODE',i,0);\n");
      appendDirtyJavaScript("		assignValue_('ITEM1_CONTRACT',i,1);\n");
      appendDirtyJavaScript("		assignValue_('AMOUNT',i,2);\n");
      appendDirtyJavaScript("		assignValue_('CMNT',i,3);\n");
      appendDirtyJavaScript("		assignValue_('CATALOG_NO',i,4);\n");
      appendDirtyJavaScript("		assignValue_('SALESPARTCATALOGDESC',i,5);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("    if (j == -1)\n");
      appendDirtyJavaScript("        validateCatalogNo(1);\n");
      appendDirtyJavaScript("        validateCmnt(1);\n"); 
      appendDirtyJavaScript("    j = 0;\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validatePriceListNo(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkPriceListNo(i) ) return;\n");
      appendDirtyJavaScript("    r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=PRICE_LIST_NO'\n");
      appendDirtyJavaScript("		+ '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
      appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("		+ '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
      appendDirtyJavaScript("		+ '&PRICE_LIST_NO=' + URLClientEncode(getValue_('PRICE_LIST_NO',i))\n");
      appendDirtyJavaScript("		+ '&QTY1=' + URLClientEncode(getValue_('QTY1',i))\n");
      appendDirtyJavaScript("		+ '&LIST_PRICE=' + URLClientEncode(getValue_('LIST_PRICE',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'PRICE_LIST_NO',i,'Price List No') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('LIST_PRICE',i,0);\n");
      appendDirtyJavaScript("		assignValue_('AMOUNTSALES',i,1);\n");
      appendDirtyJavaScript("		assignValue_('AGREEMENT_PRICE_FLAG',i,2);\n");
      appendDirtyJavaScript("         assignValue_('DISCOUNT',i,3);\n");
      appendDirtyJavaScript("         assignValue_('ITEM1DISCOUNTEDPRICE',i,4);\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateRoleCode(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    j = 0;\n");
      appendDirtyJavaScript("    if (i == -1)\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("        j = i;\n");
      appendDirtyJavaScript("        i = -1;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkRoleCode(i) ) return;\n");
      appendDirtyJavaScript("    r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ROLE_CODE'\n");
      appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&PRICE_LIST_NO=' + URLClientEncode(getValue_('PRICE_LIST_NO',i))\n");
      appendDirtyJavaScript("		+ '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
      appendDirtyJavaScript("		+ '&QTY1=' + URLClientEncode(getValue_('QTY1',i))\n");
      appendDirtyJavaScript("		+ '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
      appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
      appendDirtyJavaScript("		+ '&AMOUNT=' + URLClientEncode(getValue_('AMOUNT',i))\n");
      appendDirtyJavaScript("		+ '&CMNT=' + URLClientEncode(getValue_('CMNT',i))\n");
      appendDirtyJavaScript("		+ '&NAME=' + URLClientEncode(getValue_('NAME',i))\n");
      appendDirtyJavaScript("		+ '&SALESPARTCOST=' + URLClientEncode(getValue_('SALESPARTCOST',i))\n");
      appendDirtyJavaScript("		+ '&LIST_PRICE=' + URLClientEncode(getValue_('LIST_PRICE',i))\n");
      appendDirtyJavaScript("		+ '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
      appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&CATALOG_EXIST=' + URLClientEncode(getValue_('CATALOG_EXIST',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'ROLE_CODE',i,'Craft') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('ROLE_CODE',i,0);\n");
      appendDirtyJavaScript("		assignValue_('ITEM1_CONTRACT',i,1);\n");
      appendDirtyJavaScript("		assignValue_('LIST_PRICE',i,2);\n");
      appendDirtyJavaScript("		assignValue_('AMOUNT',i,3);\n");
      appendDirtyJavaScript("		assignValue_('AMOUNTSALES',i,4);\n");
      appendDirtyJavaScript("		assignValue_('CMNT',i,5);\n");
      appendDirtyJavaScript("		assignValue_('CATALOG_NO',i,6);\n");
      appendDirtyJavaScript("		assignValue_('SALESPARTCATALOGDESC',i,7);\n");
      appendDirtyJavaScript("         assignValue_('DISCOUNT',i,8);\n");
      appendDirtyJavaScript("		assignValue_('ITEM1DISCOUNTEDPRICE',i,9);\n");
      appendDirtyJavaScript("    }\n");
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
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkCatalogNo(i) ) return;\n");
      appendDirtyJavaScript("    r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=CATALOG_NO'\n");
      appendDirtyJavaScript("		+ '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
      appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("		+ '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
      appendDirtyJavaScript("		+ '&PRICE_LIST_NO=' + URLClientEncode(getValue_('PRICE_LIST_NO',i))\n");
      appendDirtyJavaScript("		+ '&QTY1=' + URLClientEncode(getValue_('QTY1',i))\n");
      appendDirtyJavaScript("		+ '&SALESPARTCATALOGDESC=' + URLClientEncode(getValue_('SALESPARTCATALOGDESC',i))\n");
      appendDirtyJavaScript("		+ '&NAME=' + URLClientEncode(getValue_('NAME',i))\n");
      appendDirtyJavaScript("		+ '&ITEM1_ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
      appendDirtyJavaScript("		+ '&CURRENCY_CODE=' + URLClientEncode(getValue_('CURRENCY_CODE',i))\n");
      appendDirtyJavaScript("		+ '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'CATALOG_NO',i,'Sales Part Number') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('LIST_PRICE',i,0);\n");
      appendDirtyJavaScript("		assignValue_('AMOUNT',i,1);\n");
      appendDirtyJavaScript("		assignValue_('AMOUNTSALES',i,2);\n");
      appendDirtyJavaScript("		assignValue_('CMNT',i,3);\n");
      appendDirtyJavaScript("		assignValue_('SALESPARTCATALOGDESC',i,4);\n");
      appendDirtyJavaScript("           assignValue_('SALESPARTCOST',i,5);\n");
      appendDirtyJavaScript("           assignValue_('DISCOUNT',i,6);\n");
      appendDirtyJavaScript("           assignValue_('AGREEMENT_PRICE_FLAG',i,7);\n");
      appendDirtyJavaScript("           assignValue_('ITEM1DISCOUNTEDPRICE',i,8);\n");
      appendDirtyJavaScript("           assignValue_('PRICE_LIST_NO',i,9);\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    if (j == -1)\n");
      appendDirtyJavaScript("        validatePriceListNo(1);\n");
      appendDirtyJavaScript("        validateCmnt(1);\n"); 
      appendDirtyJavaScript("    j = 0;\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateCustomerNo(i)\n");
      appendDirtyJavaScript("{   \n");
      appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkCustomerNo(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('CUSTOMER_NO',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("	    getField_('CREDITSTOP',i).value = '';\n");
      appendDirtyJavaScript("        getField_('CUSTOMERNAME',i).value = '';\n");
      appendDirtyJavaScript("        return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=CUSTOMER_NO'\n");
      appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("    retStr = r.split(\"^\");\n");
      appendDirtyJavaScript("    if(retStr[0] == ' 1')\n");
      appendDirtyJavaScript("    {   \n");
      appendDirtyJavaScript("        message = '");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPERATEREPORTINWORKORDERSMMSSG: Customer is Credit blocked"));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("        window.alert(message);\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    if( checkStatus_(r,'CUSTOMER_NO',i,'Customer No') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("        assignValue_('CREDITSTOP',i,0);\n");
      appendDirtyJavaScript("        assignValue_('CUSTOMERNAME',i,1);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function faultRepInfo()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  window.open(\"ActiveSeparate2FaultReportInfo.page?WO_NO=\"+URLClientEncode(");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(sWONo)); // XSS_Safe ILSOLK 20070709
      appendDirtyJavaScript("),\"test\",\"resizable,status=yes,width=750,height=450\"); \n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function pmInfo()\n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript("  window.open(\"ActiveSeparate2PmInfo.page?WO_NO=\"+URLClientEncode(");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(sWONo)); // XSS_Safe ILSOLK 20070709
      appendDirtyJavaScript("),\"test\",\"resizable,status=yes,width=750,height=450\"); \n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("if('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(transFlag)); // Bug Id 68773
      appendDirtyJavaScript("' == \"TRUE\")\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  jwizWoNo = '");
      appendDirtyJavaScript(wordno1);
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("  window.open(\"ActiveWorkOrder4.page?WO_NO=\"+URLClientEncode(jwizWoNo),\"transferToCustomerOrder\",\"resizable,scrollbars,status=yes,width=727,height=450\");\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateRequiredEndDate(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkRequiredEndDate(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('REQUIRED_END_DATE',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('REQUIRED_END_DATE',i).value = '';\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=REQUIRED_END_DATE'\n");
      appendDirtyJavaScript("		+ '&REQUIRED_END_DATE=' + URLClientEncode(getValue_('REQUIRED_END_DATE',i))\n");
      appendDirtyJavaScript("		+ '&REQUIRED_START_DATE=' + URLClientEncode(getValue_('REQUIRED_START_DATE',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'REQUIRED_END_DATE',i,'Latest Completion') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		if (\"ERROR_VAL\" == r.substring(1,10))\n");
      appendDirtyJavaScript("		{\n");
      appendDirtyJavaScript("			alert('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPERATEREPORTINWORKORDERSMERRMSG: Latest Completion Date is earlier than Required Start Date"));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("			f.REQUIRED_END_DATE.value = '';\n");
      appendDirtyJavaScript("		}\n");
      appendDirtyJavaScript("		else\n");
      appendDirtyJavaScript("		{\n");
      appendDirtyJavaScript("			assignValue_('REQUIRED_END_DATE',i,0);\n");
      appendDirtyJavaScript("		}\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateRequiredStartDate(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if (f.TEMP_TIME.value == f.REQUIRED_START_DATE.value)\n");
      appendDirtyJavaScript(" 		return;\n");
      appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkRequiredStartDate(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('REQUIRED_START_DATE',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('TEMP_DATE_TIME',i).value = '';\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=REQUIRED_START_DATE'\n");
      appendDirtyJavaScript("		+ '&REQUIRED_START_DATE=' + URLClientEncode(getValue_('REQUIRED_START_DATE',i))\n");
      appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'REQUIRED_START_DATE',i,'Required Start') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('REQUIRED_START_DATE',i,0);\n");
      appendDirtyJavaScript("		f.TEMP_TIME.value = f.REQUIRED_START_DATE.value;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validatePartNo(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM5',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkPartNo(i) ) return;\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=PART_NO'\n");
      appendDirtyJavaScript("		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
      appendDirtyJavaScript("		+ '&SPARE_CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&CATALOG_CONTRACT=' + URLClientEncode(getValue_('CATALOG_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&WO_NO=' + URLClientEncode(document.form.ITEM3_WO_NO.value)\n");
      appendDirtyJavaScript("		+ '&OWNER=' + URLClientEncode(getValue_('OWNER',i))\n");
      appendDirtyJavaScript("		+ '&ITEM3_ACTIVITY_SEQ=' + URLClientEncode(getValue_('ITEM3_ACTIVITY_SEQ',i))\n");
      appendDirtyJavaScript("         + '&SUPPLY_CODE=' + SelectURLClientEncode('SUPPLY_CODE',i)		+ '&ITEM5_MAINT_MATERIAL_ORDER_NO=' + URLClientEncode(getValue_('ITEM5_MAINT_MATERIAL_ORDER_NO',i))");
      appendDirtyJavaScript("		+ '&CONDITION_CODE=' + URLClientEncode(getValue_('CONDITION_CODE',i))\n");
      appendDirtyJavaScript("		+ '&PART_OWNERSHIP=' + URLClientEncode(getValue_('PART_OWNERSHIP',i))\n");
      appendDirtyJavaScript("		+ '&ITEM_WO_NO=' + URLClientEncode(getValue_('ITEM_WO_NO',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'PART_NO',i,'Part No') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO',i,0);\n");
      appendDirtyJavaScript("		assignValue_('ITEM5CATALOGDESC',i,1);\n");
      appendDirtyJavaScript("		assignValue_('HASSPARESTRUCTURE',i,2);\n");
      appendDirtyJavaScript("		assignValue_('DIMQTY',i,3);\n");
      appendDirtyJavaScript("		assignValue_('TYPEDESIGN',i,4);\n");
      appendDirtyJavaScript("		assignValue_('QTYONHAND',i,5);\n");
      appendDirtyJavaScript("		assignValue_('UNITMEAS',i,6);\n");
      appendDirtyJavaScript("		assignValue_('SPAREDESCRIPTION',i,7);\n");
      appendDirtyJavaScript("		assignValue_('SALES_PRICE_GROUP_ID',i,8);\n");
      appendDirtyJavaScript("		assignValue_('CONDITION_CODE',i,9);\n");
      appendDirtyJavaScript("		assignValue_('CONDDESC',i,10);\n"); 
      appendDirtyJavaScript("        assignValue_('QTY_AVAILABLE',i,11);\n");
      appendDirtyJavaScript("        assignValue_('ACTIVEIND_DB',i,12);\n");
      appendDirtyJavaScript("        assignValue_('PART_OWNERSHIP',i,13);\n");
      appendDirtyJavaScript("        assignValue_('PART_OWNERSHIP_DB',i,14);\n");
      appendDirtyJavaScript("        assignValue_('OWNER',i,15);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("else{\n");
      appendDirtyJavaScript("getField_('PART_NO',i).value = '';\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("   if (f.ACTIVEIND_DB.value == 'N') \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWACTIVESEPARATEREPWORKORDERINVSALESPART: All sale parts connected to the part are inactive."));
      appendDirtyJavaScript("          ');\n");
      appendDirtyJavaScript("      f.ITEM5_CATALOG_NO.value = ''; \n");
      appendDirtyJavaScript("      f.ITEM5CATALOGDESC.value = ''; \n");   
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("        validateItem5CatalogNo(i);\n");
      appendDirtyJavaScript("}\n");

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
      appendDirtyJavaScript("		+ '&ITEM3_ACTIVITY_SEQ=' + URLClientEncode(getValue_('ITEM3_ACTIVITY_SEQ',i))\n");
      appendDirtyJavaScript("         + '&SUPPLY_CODE=' + SelectURLClientEncode('SUPPLY_CODE',i)		+ '&ITEM5_MAINT_MATERIAL_ORDER_NO=' + URLClientEncode(getValue_('ITEM5_MAINT_MATERIAL_ORDER_NO',i))");
      appendDirtyJavaScript("		+ '&CONDITION_CODE=' + URLClientEncode(getValue_('CONDITION_CODE',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'PART_OWNERSHIP',i,'Ownership') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('PART_OWNERSHIP_DB',i,0);\n");       
      appendDirtyJavaScript("		assignValue_('QTYONHAND',i,1);\n");
      appendDirtyJavaScript("		assignValue_('QTY_AVAILABLE',i,2);\n");
      appendDirtyJavaScript("	        if (f.PART_OWNERSHIP_DB.value == 'CONSIGNMENT'){\n");
      appendDirtyJavaScript("		alert('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATEREPWORKORDERINVOWNER1: Ownership type Consignment is not allowed in Materials for Work Orders."));
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
      appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'SUPPLIER LOANED') \n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("	  var key_value = (getValue_('OWNER',i).indexOf('%') !=-1)? getValue_('OWNER',i):'';\n");
      appendDirtyJavaScript("	  openLOVWindow('OWNER',i,\n");
      appendDirtyJavaScript("'");
      appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PURCHASE_PART_SUPPLIER_LOV&__FIELD=Owner&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('OWNER',i))\n"); 
      appendDirtyJavaScript("                  + '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
      appendDirtyJavaScript("                  + '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");       
      appendDirtyJavaScript(",550,500,'validateOwner');\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function checkItem5Owner()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (checkItem5Fields())\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if (f.WO_CUST.value != '' && f.OWNER.value != '' && f.WO_CUST.value != f.OWNER.value && f.PART_OWNERSHIP_DB.value == 'CUSTOMER OWNED')  \n");
      appendDirtyJavaScript("         return confirm('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATEREPWORKORDERDIFFCUST: Owning Customer No is not the same as the Work Order Customer No. Still want to save this record?"));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("      else if (f.WO_CUST.value == '' && f.OWNER.value != '' && f.PART_OWNERSHIP_DB.value == 'CUSTOMER OWNED') \n");
      appendDirtyJavaScript("         return confirm('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATEREPWORKORDERNOCUST: There is no Customer specified for the Work Order although you have entered an Owning Customer No. Still want to save this record?"));
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
      appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'CUSTOMER OWNED' || f.PART_OWNERSHIP_DB.value == 'SUPPLIER LOANED') \n");
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
      appendDirtyJavaScript("                    + '&ITEM_WO_NO=' + URLClientEncode(getValue_('ITEM_WO_NO',i))\n");
      appendDirtyJavaScript("		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
      appendDirtyJavaScript("		+ '&SPARE_CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))\n");
      appendDirtyJavaScript("         + '&SUPPLY_CODE=' + SelectURLClientEncode('SUPPLY_CODE',i)		+ '&ITEM5_MAINT_MATERIAL_ORDER_NO=' + URLClientEncode(getValue_('ITEM5_MAINT_MATERIAL_ORDER_NO',i))");
      appendDirtyJavaScript("		+ '&ITEM3_ACTIVITY_SEQ=' + URLClientEncode(getValue_('ITEM3_ACTIVITY_SEQ',i))\n");
      appendDirtyJavaScript("		+ '&CONDITION_CODE=' + URLClientEncode(getValue_('CONDITION_CODE',i))\n");
      appendDirtyJavaScript("		+ '&PART_OWNERSHIP=' + URLClientEncode(getValue_('PART_OWNERSHIP',i))\n");
      appendDirtyJavaScript("                   );\n");
      appendDirtyJavaScript("      window.status='';\n");
      appendDirtyJavaScript("      if( checkStatus_(r,'OWNER',i,'Owner') )\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         assignValue_('OWNER_NAME',i,0);\n");
      appendDirtyJavaScript("         assignValue_('WO_CUST',i,1);\n");
      appendDirtyJavaScript("		assignValue_('QTYONHAND',i,2);\n");
      appendDirtyJavaScript("		assignValue_('QTY_AVAILABLE',i,3);\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("   if (f.PART_OWNERSHIP.value == 'Company Owned' && f.OWNER.value != '') \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWACTIVESEPARATEREPWORKORDERINVOWNER11: Owner should not be specified for Company Owned Stock."));
      appendDirtyJavaScript("          ');\n");
      appendDirtyJavaScript("      f.OWNER.value = ''; \n");
      appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("   if (f.PART_OWNERSHIP.value == 'Consignment' && f.OWNER.value != '') \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWACTIVESEPARATEREPWORKORDERINVOWNER12: Owner should not be specified for Consignment Stock."));
      appendDirtyJavaScript("          ');\n");
      appendDirtyJavaScript("      f.OWNER.value = ''; \n");
      appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("   if (f.PART_OWNERSHIP.value == '' && f.OWNER.value != '') \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWACTIVESEPARATEREPWORKORDERINVOWNER14: Owner should not be specified when there is no Ownership type."));
      appendDirtyJavaScript("          ');\n");
      appendDirtyJavaScript("      f.OWNER.value = ''; \n");
      appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
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

      if (bOpenNewWindow)
      {
         appendDirtyJavaScript("if (readCookie(\"PageID_CurrentWindow\") == \"*\")");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("  window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString)); // XSS_Safe ILSOLK 20070709
         appendDirtyJavaScript("\", \"");
         appendDirtyJavaScript(newWinHandle);
         appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
         appendDirtyJavaScript("}\n");
      }

      if (itemlay.isNewLayout())
      {
         appendDirtyJavaScript("   { \n");
         appendDirtyJavaScript("      f.PART_OWNERSHIP.selectedIndex = 1;\n");
         appendDirtyJavaScript("   } \n");
      }

      if (itemlay.isNewLayout() && !mgr.isEmpty(headset.getValue("ACTIVITY_SEQ")))
      {
         appendDirtyJavaScript("   { \n");
         //Bug 66456, Start, Added check on PROJ
         if (mgr.isModuleInstalled("PROJ"))
            appendDirtyJavaScript("      f.SUPPLY_CODE.selectedIndex = 1;\n");
         else
            appendDirtyJavaScript("      f.SUPPLY_CODE.selectedIndex = 2;\n");
         //Bug 66456, End
         appendDirtyJavaScript("   } \n");
      }
      else if (itemlay.isNewLayout() && mgr.isEmpty(headset.getValue("ACTIVITY_SEQ")))
      {
         appendDirtyJavaScript("   { \n");
         appendDirtyJavaScript("      f.SUPPLY_CODE.selectedIndex = 2;\n");
         appendDirtyJavaScript("   } \n");
      }

      //Bug 89703, Start
      appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(hasPlanning);
      appendDirtyJavaScript("' == 'TRUE' && readCookie(\"PageID_Has_Planning\")== 'TRUE')\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("	      writeCookie(\"PageID_Has_Planning\", \"FALSE\", '', COOKIE_PATH); \n");
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
      //Bug 89703, End
      
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
      appendDirtyJavaScript(mgr.translateJavaScript("WOJOB_REMSEP_STDJOB: Do you want to remove connected Operations, Materials, Planning, Tools/Facilities and Documents?"));
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
      appendDirtyJavaScript("               if (confirm('");
      appendDirtyJavaScript(mgr.translateJavaScript("WOJOB_RECSEP_STDJOB: Do you want to remove and reconnect Operations, Materials, Tools/Facilities, Planning and Documents?"));
      appendDirtyJavaScript("'))\n");
      appendDirtyJavaScript("                  getField_('RECONNECT',i).value = 'YES';\n");
      appendDirtyJavaScript("               else\n");
      appendDirtyJavaScript("                  getField_('RECONNECT',i).value = 'NO';\n");
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

      appendDirtyJavaScript("function connectToActivityClient()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   window.open('../projw/ConObjToActivityDlg.page','CONOBJDLG','status,resizable,scrollbars,width=800,height=300,left=100,top=200');\n");
      appendDirtyJavaScript("   return false;\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function disconnectFromActivityClient()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   return confirm('" + mgr.translateJavaScript("ACTSEPREPSMHEADERDISCACT: Are you sure you want to remove the Project Connection ?") + "');\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function setActivitySeq(activitySeq)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   getField_('ACTIVITY_SEQ_PROJ', -1).value = activitySeq;\n");
      appendDirtyJavaScript("   commandSet('HEAD.connectToActivity','');\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function checkItem4Owner()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (checkItem4Fields())\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if (f.WO_CUST.value != '' && f.OWNER.value != '' && f.WO_CUST.value != f.OWNER.value && f.PART_OWNERSHIP_DB.value == 'CUSTOMER OWNED')  \n");
      appendDirtyJavaScript("         return confirm('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2DIFFCUST: Owning Customer No is not the same as the Work Order Customer No. Still want to save this record?"));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("      else if (f.WO_CUST.value == '' && f.OWNER.value != '' && f.PART_OWNERSHIP_DB.value == 'CUSTOMER OWNED') \n");
      appendDirtyJavaScript("         return confirm('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2NOCUST: There is no Customer specified for the Work Order although you have entered an Owning Customer No. Still want to save this record?"));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("      else \n");
      appendDirtyJavaScript("         return true;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function refreshPermitBlock()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   f.REFRESH_FLAG.value  = 'TRUE'; \n");
      appendDirtyJavaScript("   f.REFRESH_BLOCK.value = 'ITEM3EDIT'; \n");
      appendDirtyJavaScript("   f.submit(); \n");
      appendDirtyJavaScript("}\n");

      if (bCancelPage)
      {
         appendDirtyJavaScript("  jwonumber = '");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(wonumber));
         appendDirtyJavaScript("';\n");
         appendDirtyJavaScript("window.open ('EnterCancelCauseDlg.page?WO_NO='+URLClientEncode(jwonumber)+'&FRMNAME=ActiveSeprepInWOSM&QRYSTR="+mgr.URLEncode(qrystr)+"','cancelCause','status=yes,resizable=1,scrollbars=yes,width=750,height=600'); \n");
      }
      //Bug 68947, Start   
      if (bFECust)
      {
         bFECust = false;
         appendDirtyJavaScript("validateCustomerNo(0)\n");
         appendDirtyJavaScript("if (getValue_('CONTRACT_ID',0) != '')\n");
         appendDirtyJavaScript("	 validateContractId(0)\n");
      }

      appendDirtyJavaScript("function setContractId(contrId,lineNo)\n{\n");
      appendDirtyJavaScript("	document.form.TEMPCONTRACTID.value = contrId;\n");
      appendDirtyJavaScript("	document.form.TEMPLINENO.value = lineNo;\n");
      appendDirtyJavaScript("	f.submit();\n}\n");

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
      //(-/+) Bug 70920, Modified where condition to consider category objects (Start).
      appendDirtyJavaScript(" 	if (document.form.MCH_CODE.value != '' && document.form.CONTRACT.value != '') \n");
      appendDirtyJavaScript("	      		whereCond1 += \" AND (((CONTRACT_ID,LINE_NO) IN (SELECT contract_id, line_no FROM PSC_SRV_LINE_OBJECTS WHERE mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' AND mch_contract = '\" + URLClientEncode(document.form.CONTRACT.value) + \"')) OR (mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' AND mch_contract = '\" + URLClientEncode(document.form.CONTRACT.value) + \"') OR CONNECTION_TYPE_DB = 'CATEGORY')\";\n");
      //(-/+) Bug 70920, Modified where condition to consider category objects (Finish).
      if (bPcmsciExist)
      {  // Filtering by From Date
         //appendDirtyJavaScript(" 	if (document.form.PLAN_S_DATE.value != '') \n");
         //appendDirtyJavaScript("	      		whereCond1 += \" AND Psc_Contr_Product_Api.Get_Date_From(CONTRACT_ID,LINE_NO) <= to_date(to_char(to_date('\" + URLClientEncode(document.form.PLAN_S_DATE.value) + \"','" + mgr.getASPField("PLAN_S_DATE").getMask() + "'),\'YYYYMMDD\'),\'YYYYMMDD\') \";\n"); 
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
      appendDirtyJavaScript("+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n"); 
      appendDirtyJavaScript("+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
      appendDirtyJavaScript("+ '&WORK_TYPE_ID=' + URLClientEncode(getValue_('WORK_TYPE_ID',i))\n");
      appendDirtyJavaScript(",'serviceContract','scrollbars,resizable,status=yes,width=770,height=500');\n");
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
      //(-/+) Bug 70920, Modified where condition to consider category objects (Start).
      appendDirtyJavaScript(" 	if (document.form.MCH_CODE.value != '' && document.form.CONTRACT.value != '') \n");
      appendDirtyJavaScript("	      		whereCond1 += \" AND (((CONTRACT_ID,LINE_NO) IN (SELECT contract_id, line_no FROM PSC_SRV_LINE_OBJECTS WHERE mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' AND mch_contract = '\" + URLClientEncode(document.form.CONTRACT.value) + \"')) OR (mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' AND mch_contract = '\" + URLClientEncode(document.form.CONTRACT.value) + \"') OR CONNECTION_TYPE_DB = 'CATEGORY')\";\n");
      //(-/+) Bug 70920, Modified where condition to consider category objects (Finish).
      if (bPcmsciExist)
      {  // Filtering by From Date
         //appendDirtyJavaScript(" 	if (document.form.PLAN_S_DATE.value != '') \n");
         //appendDirtyJavaScript("	      		whereCond1 += \" AND Psc_Contr_Product_Api.Get_Date_From(CONTRACT_ID,LINE_NO) <= to_date(to_char(to_date('\" + URLClientEncode(document.form.PLAN_S_DATE.value) + \"','" + mgr.getASPField("PLAN_S_DATE").getMask() + "'),\'YYYYMMDD\'),\'YYYYMMDD\') \";\n"); 
         //appendDirtyJavaScript(" 	else \n");
         appendDirtyJavaScript("	      		whereCond1 += \" AND Psc_Contr_Product_Api.Get_Date_From(CONTRACT_ID,LINE_NO) <= sysdate \";\n"); 
      }
      appendDirtyJavaScript("		openLOVWindow('CONTRACT_ID',i,\n");
      appendDirtyJavaScript("		'PscContrProductLov.page' + '?__VIEW=DUMMY&__INIT=1&__LOV=Y&__WHERE=' +whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("							,550,500,'validateContractId');\n");
      appendDirtyJavaScript(" 	}\n");
      //Bug Id 70012, End
      appendDirtyJavaScript("	else\n");
      appendDirtyJavaScript("  window.open('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript("ServiceContractLineSearchDlg.page?"));
      appendDirtyJavaScript("' + '&CUSTOMER_NO='+ URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("+ '&WO_NO=' + URLClientEncode(getValue_('WO_NO',i))\n");
      appendDirtyJavaScript("+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n"); 
      appendDirtyJavaScript("+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
      appendDirtyJavaScript("+ '&WORK_TYPE_ID=' + URLClientEncode(getValue_('WORK_TYPE_ID',i))\n");
      appendDirtyJavaScript(",'serviceContract','scrollbars,resizable,status=yes,width=770,height=500');\n");
      appendDirtyJavaScript("}\n");
      //Bug 68947, End   

      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }

}

