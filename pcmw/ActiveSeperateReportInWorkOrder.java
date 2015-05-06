/* 
*                  IFS Research & Development 
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
*  File        : ActiveSeperateReportInWorkOrder    .java 
*  Created     : ASP2JAVA Tool  010214  Created Using the ASP file ActiveSeperateReportInWorkOrder.asp
*  Modified    :
*  JEWILK  010320  Fixed conversion errors and done necessary adjustments.
*  BUNILK  010408  Overwritten saveReturn() methods for Material line and Time Report 
*                  blocks to repopulate budget block after new records of those blocks.     
*  JEWILK  010418  Removed Pre Posting actions in Material tab (Call 62860)
*  INROLK  010504  Did changes for RMB Requisition.
*  JEWILK  010611  Modified method run() to fetch the necessary values when returning from 'Create from planning'. (call #65296)
*                  Changed 'document.ClientUtil' as 'document.applets[0]' in javascript.
*  JEWILK  010618  Modified functionality in Time Report tab.(call 65296, 66205).
*  JEWILK  010621  Removed rmb 'Create from planning' from commandbar and added as a hyperlink in the 'Time Report' tab.
*                  Set default layout mode as MULTIROW in the 'Time Report' tab.
*  BUNILK  010703  Modified all state changing methods of headblk and perform method. Addded new method checkState(),
*                  Removed unwanted actions and command buttons when itemblks are in find edit or new mode. Added state changing 
*                  Actions for multi record mode also    
*  BUNILK  010716  Modified PART_NO field validation. 
*  INROLK  010727  Changed functon moveNonSerial(). call id 77825.
*  JEWILK  010802  Modified Budget tab to show correct values for column "planned Margin".
*  BUNILK  010806  Made some changes in return AutoString of getContents() function so that to call       
*                  checkPartVal() javascript function from onBlur event of PART_NO field. Done some changes in 
*                  validatePartNo() method also. Modified sparePartObject() method and associated javascripts. 
*  CHCRLK  010808  Modified methods printWO(), printAuth() and printPicList().
*  JEWILK  010815  Modified function moveNonSerial() to transfer 'ERR_DESCR' also.
*  ARWILK  010904  Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction(). 
*                  (Caused when cancelNew is performed after saveNew)
*  JEWILK  010912  Modified method 'saveReturnItem5' to goto newly saved record in single record mode.
*  JEWILK  010921  Added action Pre Posting to Material and Material Requisition blocks.
*  JEWILK  011008  Checked security permissions for RMB Actions.
*  BUNILK  011011  Added addCommandValidConditions() method for headblk state changing actions.  
*  VAGULK  011011  Changed the Exec Dept LOV To 'ORG_CODE_ALLOWED_SITE_LOV'
*  JEWILK  011015  Modified validations of fields 'PLAN_QTY','CATALOG_NO','PRICE_LIST_NO' to 
*                  correctly display 'Sales Price' and 'Discounted Price'.
*  BUNILK  011016  Added Back hyperlinkt to Material block
*  BUNILK  011017  Removed some fields and related codes from budget block.   
*  JEWILK  011010  Modified to clear the row from the rowset, when the WO is 'Finished'. call# 70504
*  JEWILK  011021  Set Field 'ITEM1_ORG_CODE' Mandatory.  call# 71069 
*  SHAFLK  020508  Bug 29946,Added hyperlinks for the operations "Spare Parts in Object" and "Object Structure" under the header level of Material section. 
*  SHAFLK  020509  Bug 29943,Made changes to refresh in same page after RMB return.
*  SHAFLK  020531  Bug 30450,Changed "Comment" field to type text area.
*  SHAFLK  020619  Bug 30450,Added Validation for "Comment" field.
*  SHAFLK  020726  Bug Id 31771 Added security check for MOBMGR_WORK_ORDER_API.Check_Exist.
*  BUNILK  020815  Bug Id 31915 Removed some codes from Hour field validation part of Time Report tab so that to
*                  make it possible for enter more than 24 hours.
*  BUNILK  020830  Bug Id 32182 Modified setCheckBoxValue method so that to check security for Domanw component. 
*                  and added a new method isModuleInst() to check availability of ORDER module inside preDefine method. 
*  SHAFLK  021108  Bug Id 34064,Changed methods printAuth,printPicList & printWO.    
*  ---------------------Generic WO-------------------------------------------
*  INROLK  021115  Added MCH_CODE_CONTRACT and CONNECTION_TYPE.  
* ----------------------------------------------------------------------------
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  BUNILK  021213  Merged with 2002-3 SP3
*  CHAMLK  021218  Added Craft Row Number and Craft Reference Number.
*  CHAMLK  030102  Added a new action called Maint Task.
*  NIMHLK  030113  Added a new action to call condition code dialog according to specification W110 - Condition Codes.
*  SHAFLK  030310  Modified validation of signature field.
*  SHAFLK  030310  Modified validation of PART_NO field and preDefine(). 
*  JEJALK  030331  Renamed Craft Row No to Operation No , Craft Reference No to Reference No.
*  CHAMLK  030403  Added new tab Tools and Facilities.
*  JEJALK  030404  Added new method (checkToolFacilitySite) to check invalid Tools/Facility details when changing States.
*  JEJALK  030430  Added Condition Code and Description to the Material Tab.
*  JEJALK  030503  Added new action " Available to Reserve" to the Material tab.
*  SHAFLK  030508  Modified setCheckBoxValue(), preDefine() and adjust() methods.
*  INROLK  030624  IID ADAM305NA Added RMB Returns.
*  CHAMLK  030825  Added Part Ownership, Owner and Owner Name to Materials Tab.
*  JEWILK  030925  Corrected overridden javascript function toggleStyleDisplay().
*  CHCRLK  031022  Made Sales Part Number & Description visible in the Time Report tab. [Call ID 108247]  
*  CHAMLK  031022  Modified function preDefine() to remove the setUpperCase() in CONNECTION_TYPE. 
*  THWILK  031024  Call ID 104789 - Added an extra parameter(#)to the deffinition of the field WO_NO.
*  PAPELK  031024  Call ID 108606.Removed ASPField refering CustomerNo in TimeReport tab.
*  CHCRLK  031028  Removed reference to db column catalog_no (Sales Part No) in Time Report tab. [Call ID 108247]
*  CHCRLK  031104  Added back reference to db column catalog_no (Sales Part No) in Time Report tab but removed its validations in javascript methods. [Call ID 108247]
*  CHCRLK  031107  Modified method validate() to correct validations on field QTY in Time Report tab. [Call ID 108247]
*  ARWILK  031110  (Bug#110424) Made the RMB's 'Available to Reserve' and 'Issued Serials' open in new windows. (Check method comments)
*  SAPRLK  031216  Web Alignment, Converting blocks to tabs, removing 'clone' & 'doReset' methods, converting links to RMBs.
*  ARWILK  031224  Edge Developments - (Replaced links with multirow RMB's)
*  SAPRLK  040129  Web Alignment - Added multirow action to master form and the tabs, simplifying code for RMBs, remove unnecessary method calls for hidden fields,
*                  removed unnecessary global variables.  
*  VAGULK  040209  Web Alignment - Arranged the field order as given in Centura and removed unnecessary method calls.
*  SAPRLK  040212  Web Alignment - simplifying code for RMBs, adding code to open tabs implemented as RMBs in a new window, change of conditional code in validate method.  
*  ARWILK  040316  Bug#112935 - Resolved RMB problems.
*  VAGULK  040318  Web Alignment ,modified getContents().
*  SHAFLK  040119  Bug Id 41815,Removed Java dependencies.
*  SHAFLK  040213  Bug Id 40256, Modified sparePartObjectJS() and objStructureJS(). 
*  SHAFLK  040305  Bug Id 40788, Modified Validation() for EMP_NO and method validateEmpNo().
*  SHAFLK  040308  Bug Id 40788, Modified okFindNeW().
*  SAPRLK  040323  Merge with SP1.
*  SAPRLK  040401  Made Changes for Web Alignment.
*  THWILK  040420  Call 112950,Modified permits() and used cookie "PageID_CurrentWindow" to prevent the opening of the 
*                  previously visited window when an error is encountered.
*  SAPRLK  040421  Corrected Call 114222.
*  THWILK  040421  Web Alignment - Set the PageID_CurrentWindow cookie in all RMB's which opens in separate windows.
*  SAPRLK  040422  Corrected Call Id 114250, set the active tab to the Time Report tab when the form is refreshed to insert a new record in the 'Create from Planning' 
*                  & 'Connect/Reconnect to Planning' RMBs.
*  SAPRLK  040603  Added key PM_REVISION.
*  ARWILK  040617  Added a new job tab. Added job_id to materials, tools and facilities tabs.(Spec - AMEC109A)  
*  SHAFLK  040416  Bug 43848, Modified methods run(), finished() and HTML part.Added methods CheckObjInRepairShop() and finshed1().
*  THWILK  040625  Merged Bug 43848.
*  ARWILK  040629  Added RMB's connectToActivity and disconnectFromActivity (Spec AMME613A - Project Umbrella)
*  ARWILK  040720  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  VAGULK  040721  Added fields "SUPPLY_CODE ,SUPPLY_CODE_DB and ACTIVITY_SEQ" to Material tab (SCME612 - Project Inventory)
*  NIJALK  040727  Added new parameter CONTRACT to method calls to Role_Sales_Part_API.Get_Def_Catalog_No.  
*  SHAFLK  040708  Bug 41817, Modified methods run(), finished(), reported() and HTML part.Added methods CheckAllMaterialIssued() and reported1().
*  THWILK  040810  Merged Bug 41817.
*  SHAFLK  040421  Bug 42866, Modified method issue().
*  THWILK  040818  Merged Bug 42866 and modified method call MAKE_ISSUE_DETAIL.
*  ARWILK  040820  Changed LOV's of STD_JOB_ID and STD_JOB_REVISION.
*  NIJALK  040824  Call 116946, Modified validate(),predefine(),getContents().
*  BUNILK  040825  Changed server function of QTYONHAND field.
*  NIJALK  040825  Call 117187, modified CheckObjInRepairShop().
*  ThWilk  040825  Call 117308, Modified released() and started().
*  NIJALK  040826  Call 117104, Modified run().
*  NIJALK  040826  Call 117295, changed the field contract in time report tab to read only.
*  BUNILK  040826  Call 117328, Modified finished()  
*  VAGULK  040826  Corrected Calls 117249 and 117251,fetched the default values of "Owernership" and "Supply_code" in the new layout.
*  ARWILK  040901  Call 117155, Modified method getContents.
*  ARWILK  040901  Resolved Material status refresh problem.
*  NIJALK  040901  Call id 117415, Modified predefine(), Added saveReturnItem7().
*  SHAFLK  040721  Bug 43249, Modified validations of PART_NO, ITEM5_CONDITION_CODE, PART_OWNERSHIP and OWNER and added some fields to predefine and modified HTML part. 
*  NIJALK  040902  Merged bug 43249.
*  NIJALK  040908  Call 117750: Modified preDefine().
*  ARWILK  040910  Modified availtoreserve.
*  VAGULK  040921  Added field RELEASE_CERTIFICATE and RMB - Release Certificate (AMAD111:Report FAA Form)
*  NIJALK  040929  Added parameter project_id to method calls to Inventory_Part_In_Stock_API.Get_Inventory_Quantity.
*  BUNILK  040930  Changed server function of QTYONHAND field and modified validations of Part No, Condition Code, Ownership and owner fields of Material tab..
*  NIJALK  041001  Renamed 'Signature' to "Executed By" in Jobs tab. Modified validations of SIGN_ID,EMPLOYEE_ID.
*  ARWILK  041005  LCS Merge: 46434.
*  ARWILK  041005  LCS Merge: 46394.
*  ARWILK  041005  LCS Merge: 45565.
*  SHAFLK  040906  Bug 46542, Modified method matReqUnissue().
*  NIJALK  041007  Merged 46542. 
*  VAGULK  041007  Added RMB 'Print Authorized Release Certificate'(AMAD111:Report FAA Form).
*  SHAFLK  040812  Bug 45904, Modified method issueFromInvent(), manissue() and HTML part. 
*  NIJALK  041008  Merged 45904.
*  ARWILK  041011  Handles some problems in Condition Code and Ownership functionality.
*  VAGULK  041021  Renamed RMB to 'Print /Reprint Auth Release Certificate'.
*  ARWILK  041022  LOV Change for Fields STD_JOB_ID,STD_JOB_REVISION. (Spec AMEC111 - Standard Jobs as PM Templates)
*  BUNILK  041026  Modified validations of material tab, added new validation to supply code, modified new method of material tab
*  NIJALK  041028  Modified validate(), preDefine().
*  VAGULK  041103  Modified printAuthRelCerti(),Call ID 119216.
*  NIJALK  041105  Added Std Job Id to Jobs tab. Modified preDefine(), validate();
*  SHAFLK  040916  Bug 46621, Modified validations of PART_NO, ITEM5_CONDITION_CODE, PART_OWNERSHIP and OWNER . 
*  Chanlk  041105  merged bug 46621.
*  NAMELK  041109  Duplicated Translation Tags Corrected.
*  NIJALK  041112  Made the field "Employee ID" visible and read only in jobs tab.
*  ARWILK  041115  Replaced getContents with printContents.
*  NIJALK  041115  Replaced method Standard_Job_API.Get_Work_Description with Standard_Job_API.Get_Definition.
*  NIJALK  041201  Added new parameters to method calls to Inventory_Part_In_Stock_API.Get_Inventory_Quantity.
*  NIJALK  041208  Modified validations to "Comment" field in Time Report Tab.
*  NIJALK  041210  Call 120514: Modified validate().
*  Chanlk  041220  Call 120293: removed the security check for the CUSTOMER_ORDER_PRICING_API.Get_Sales_Part_Price_Info
*  Chanlk  041222  Call 120440: Changed the fieled properties for CustomerNo in preDefine.
*  Chanlk  050112  Change the lov properties of the emp no, maint org, role code.
*  NAMELK  050120  Materials Tab: Check Box MUL_REQ_LINE added,Set LOV to field ITEM3_CONTRACT. Code added to adjust().
*  NIJALK  050202  Modified detchedPartList(), sparePartObject(), adjudt(),preDefine(),validate().
*  NIJALK  050224  Modified preDefine(), saveReturn(). Added deleteItem().
*  NAMELK  050224  Merged Bug 48035 manually.
*  DIAMLK  050228  Replaced the field Pre_Posting_Id by Pre_Accounting_Id.(IID AMEC113 - Mandatory Pre-posting)
*  DIAMLK  050310  Bug ID:122509 - Modified the method okFind().
*  NIJALK  050405  Call 123081: Modified manReserve().
*  NEKOLK  050407  Merged - Bug 48852, Modified issueFromInvent and reserve().
*  NIJALK  050407  Call 123086: Modified manReserve(), reserve().
*  NIJALK  050505  Bug 123698: Set a warning msg when changing state to "Prepared" if WO causes a project exception.
*  NIJALK  050519  Modified availDetail(), preDefine().
*  SHAFLK  050330  Bug 50258, Modified issueFromInvent and manIssue().
*  NIJALK  050527  Merged bug 50258.
*  DiAmlk  050613  Bug ID:124832 - Renamed the RMB Available to Reserve to Inventory Part in Stock... and
*                  modified the method availtoreserve.
*  SHAFLK  050425  Bug 50830, Used mgr.translateJavaScript() method to get the correctly translated strings in JavaScript functions.
*  NIJALK  050617  Merged bug 50830. 
*  BUNILK  050706  Added command valid conditions for 'Release Certificate' action.
*  NIJALK  050805  Bug 126176, Modified issueFromInvent().
*  NIJALK  050808  Bug 126177: Modified released(),started(),perform().
*  NIJALK  050811  Bug 126137, Modified availtoreserve().
*  NIJALK  050824  Modified matReqUnissue(),okFind().
*  NIJALK  050905  Set field AMOUNT read only.
*  SHAFLK  050919  Bug 52880, Modified adjust().
*  NIJALK  051004  Merged bug 52880.   
*  SHAFLK  051011  Bug 51044, Added method duplicateITEM6().
*  NIJALK  051014  Merged bug 51044.
*  THWILK  051026  Added functionality required to implement RMB "Create From Allocation".
*  ERALLK  051214  Bug 54280. Added column 'Quantity Available'.Modified the validate() function.
*  NIJALK  051227  Merged bug 54280.
*  BUNILK  060104  Added new pop-up menu and moved 'Print/Reprint Airworthiness Release Cetificate'  and  'View Airworthiness Release Certificate...' to it in headblk.
*  NIJALK  060202  Call 132375: Replaced method call to Organization_API.Get_Org_Cost() with Work_Order_Planning_Util_Api.Get_Cost().
*  NIJALK  060202  Call 132126: Changed the data type of BUDGET_COST,PLANNED_COST,ACTUAL_COST to "Money".
*  NIJALK  060228  Call 132375: Added parameter EMP_NO to Work_Order_Planning_Util_Api.Get_Cost() in validate function.
*  NIJALK  060210  Call 133546: Renamed "STANDARD_INVENTORY" with "INVENT_ORDER".
*  NIJALK  060213  Call 132956: Modified projectActivityInfo().
*  SULILK  060304  Call 134906: Modified manReserve(), GetInventoryQuantity(), manIssue().
*  ASSALK  060316  Material Issue & Reserve modification. Issue and reserve made available after
*                    unissue all materials.
*  SULILK  060317  NOIID:Added checksec() and modified validate().preDefine(),saveReturnItem5(),run().
*  NIJALK  060321  Bug 137726: Modified checkObjAvailable().
* ----------------------------------------------------------------------------
*  NIJALK  060509  Bug 57099, Modified matReqUnissue().
*  NIJALK  060510  Bug 57256, Modified manIssue(), GetInventoryQuantity().
*  NIJALK  060515  Bug 56688, Modified issueFromInvent(),checkObjAvailable().
*  SHAFLK  060525  Bug 57598, Modified validation for PART_NO and setValuesInMaterials().
*  SHAFLK  060601  Bug 58197, Modified run and getContents().
*  AMNILK  060629  Merged with SP1 APP7.
*  JEWILK  060803  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged Bug Id: 58214.
*  JEWILK  060816  Bug 58216, Eliminated SQL Injection security vulnerability.
*  NAMELK  060906  Merged Bug 58216.
*  AMNILK  060914  MTPR904 Hink tasks.Request Id: 38283.Modified the started() method to refresh the headset rows.
*  AMNILK  060919  Error at WO row hyperlink in Report in WO page.Modified okFindITEM5(), countFindITEM5().
*  AMNILK  060919  Call Id: 139505. Modified preDefine() method.Removed itemtbl1.enableRowSelect().
*  AMNILK  060921  Call Id: 139505. Modified the adjust() method.
*  SHAFLK  060914  Bug 60342, Added new fields TEAM_ID, TEAM_CONTRACT and accoringly changed validations and lov filtering of time report tab and RMB Create from planning.
*  AMNILK  060926  Merged Bug Id: 60342.
*  SHAFLK  061101  Bug 61515, Modified method adjust().
*  AMNILK  061106  Merged LCS Bug ID: 61515.
*  SHAFLK  061005  Bug 60938, Added new RMB "Activity Info..." 
*  AMNILK  061107  Merged LCS Bug ID: 60938.
*  SHAFLK  060731  Bug 59613, Modified changeConditionCode(). 
*  SHAFLK  070116  Bug 62854, Modified validation for Part No.
*  AMNILK  070208  Merged LCS Bug 62854.   
*  SHAFLK  061120  Bug 61466, Added 'Supplier Loaned' stock to Material.
*  ILSOLK  070302  Merged Bug Id 61446. 
*  SHAFLK  070228  Bug 63812, Modified printPicList.
*  ILSOLK  070410  Merged Bug Id 63812.
*  NAMELK  070420  Call 141863,Modified run(),cancelled() & javascript code added.
*  ASSALK  070424  Call 142871. Modified getContents().
*  AMDILK  070503  Call Id 142273: Inserted a new RMB "Update Spare Parts in Object" to the materials tab
*  ASSALK  070508  Call ID 142871: Modified adjust().
*  CHANLK  070517  Call 144491 Added Tranfered to mobile check box in preDefine()
*  AMDILK  070518  Call Id 144691: Inserted OBJSTATE to the buffer. Modified requisitions()
*  ASSALK  070528  Call ID 145243. Modified validate().
*  AMDILK  070531  Call Id 145443: Disable the RMB "Update Spare Parts in Object" when spare parts exists 
*  AMDILK  070608  Call Id 144694: Modified okFindITEM5()
*  AMDILK  070614  Call Id 144694: Preserve the line information when navigate the last and first record
*  ASSALK  070705  Webification. Added 4 RMB actions of RMB group Receive Order Handling.
*  ILSOLK  070706  Eliminated XSS.
*  NIJALK  070420  Bug 64572, Modified preDefine().
*  ILSOLK  070718  Merged Bug ID 64572.
*  ASSALK  070718  Webification corrections. Modified returnObject().
*  ILSOLK  070730  Eliminated LocalizationErrors.
*  AMDILK  070731  Removed the scroll buttons of the parent when the child tab(Job) is in new or edit modes
*  AMDILK  070801  Modified run() in order to load the page in Firefox
*  ILSOLK  070905  If WOState in ('RELEASED', 'STARTED', 'WORKDONE','PREPARED','UNDERPREPARATION') Enabled Release RMB in Material.
*                  Modified preDefine()(Call ID 148213)
*  ASSALK  070910  CALL 148510, Modified preDefine(), issueFromInvent().
*  ASSALK  070911  CALL 148513. Modified issueFromInvent().
*  SHAFLK  071108  Bug 67801, Modified validation for STD_JOB_ID.
*  CHANLK  071108  Bug 67871, Modified preDefine()remove headlay.setFieldOrder().
*  SHAFLK  071123  Bug 69392, Checked for ORDER installed.
*  SHAFLK  081121  Bug 78187, Modified issue().
* ----------------------------------------------------------------------------
* -----------------------APP7.5 SP1-------------------------------------
*  071214  ILSOLK  Bug Id 68773, Eliminated XSS.
*  SHAFLK  071219  Bug 70147, Modified saveReturnItem7(). 
*  SHAFLK  080102  Bug 70891, Modified finished() and finished1().
*  NIJALK  080202  Bug 66456, Modified validate(), GetInventoryQuantity(), availDetail() and getContents().
*  SHAFLK  080106  Bug 70948, Modified lovEmpNo().
*  CHANLK  080205  Bug 66842,Remove maxLength of PM_NO
*  SHAFLK  080130  Bug 70815, Modified run(), issue(), reserve(), preDefine(), okFindITEM5() and okFindITEM3(). 
* -----------------------------------------------------------------------
*  AMNILK  080225  Bug Id 70012, Modified coInformation().
*  NIJALK  080306  Bug 72202, Modified okFindITEM5().
*  ARWILK  071130  Bug 66406, Added CONN_PM_NO, CONN_PM_REVISION, CONN_PM_JOB_ID.
*  SHAFLK  080402  Bug 72788, Modified preDefine() and validateRoleCode.
*  ILSOLK  080421  Bug 73136, Modified preDefine().
*  ASSALK  080507  Bug 72214, Winglet merge.
*  CHANLK  080626  Bug 74288, arrange the field order.
*  SHAFLK  080714  Bug 75563, Modified preDefine().
*  SHAFLK  080908  Bug 76867, Modified preDefine().
*  SHAFLK  080924  Bug 77304, Modified preDefine().
*  SHAFLK  081006  Bug 77557, Modified preDefine() and released().
*  SHAFLK  090217  Bug 79436  Modified preDefine(). 
*  CHANLK  090225  Bug 76767, Modified preDefine(), validate(), issue(), reserve().
*  SHAFLK  090305  Bug 80959  Modified preDefine() and added reinit().
*  SHAFLK  090630  Bug 82543  Modified detchedPartList(). 
*  NIJALK  090704  Bug 82543, Modified run() and printContents(). Added refresh().
*  HARPLK  090721  Bug 83757, Modified preDefine().
*  CHANLK  090721  Bug 83532, Added RMB Work Order Address.
*  CHANLK  090730  Bug 82934, Column renamed. ToolFacility.Quantity to Planned Quantity.
*  SHAFLK  090810  Bug 85099, Modified run(), detchedPartList() and printContents().
*  HARPLK  090828  Bug 83757, Modified run(),validate().  
*  SHAFLK  090917  Bug 85917, Modified preDefine().
*  SHAFLK  090921  Bug 85901, Modified preDefine().
*  SUIJLK  090922  Bug 81023, Modified preDefine(), saveReturnItem1(). Added new methods deleteItem1() and saveNewItem1()
*  SHAFLK  091110  Bug 87041  Modified newRowITEM5().
*  SUIJLK  091116  Bug 87026  Modified run().
*  CHANLK  091125  Bug 87334  Modified Run() moved call to validate method to the beginning.
*  SHAFLK  100128  Bug 87329  Modified cancelled() and perform().
*  NIJALK  100218  Bug 87766, Modified setCheckBoxValue() and preDefine(). Added new column CBINASTRUCTURE.
*  SHAFLK  100318  Bug 89298, Modified performItem().
*  NIJALK  100426  Bug 85045, Modified okFindNeW(), newRowITEM1() and run().
*  VIATLK  100709  Bug 91376, Modified printAuth() and printWO().
*  NIJALK  100719  Bug 89399, Modified getContents().
*  SaFalk  100731  Bug 89703, Added methods deleteRow7(), deleteRowITEM7() and modified methods run(), preDefine() and printContents().
*  CHANLK  101025  Bug 93736, Modified performItem().
* -----------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;                               
import ifs.fnd.*;

import java.util.*;

public class ActiveSeperateReportInWorkOrder extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeperateReportInWorkOrder");

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

    private ASPBlock itemblk7;
    private ASPRowSet itemset7;
    private ASPCommandBar itembar7;
    private ASPTable itemtbl7;
    private ASPBlockLayout itemlay7;

    private ASPPopup headpopup;
    private ASPBlock printblk;
    private ASPRowSet eventset;
    private ASPBlock tempblk;
    private ASPBlock eventblk1;
    private ASPRowSet eventset1;
    private ASPField f;

    private ASPTabContainer tabs;    

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================

    private String sWONo;
    private int currrow;
    private String crePlanPath;
    private String creFromPlan;
    private int sentrowNo;
    private boolean nextPrev;
    private ASPTransactionBuffer trans;
    private ASPTransactionBuffer trans1;
    private boolean postingflg;
    private String helpbase;
    private String qrystr;
    private boolean comBarAct;
    private String noteId;
    private String noteIdVal;
    private boolean isFind;
    private String comp;
    private String connReconToPlan;
    private boolean sentNoEmployee;
    private boolean sentEmployee;
    private boolean recordCancel;
    private String itemMchCode;
    private String itemContract;
    private String itemWoNo;
    private String itemSpareId;
    private String nQtyLeft;
    private boolean matSingleMode;
    private boolean updateBudg;
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
    private String sent_discount;
    private String canceled_wo_no;
    private ASPCommand cmd;
    private int headrowno;
    private String calling_url;
    private String callingurl1;
    private String current_url;
    private String head_command;
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
    private String strLableJ; 
    private int curentrow;
    private int rowToEdit;
    private int item4CurrRow; 
    private String fmtdBuffer;
    private String availDetailPath;
    private String supPerPartPath;
    private boolean satis;
    private boolean emptyQry = false;
    private String openCreRepNonSer;
    private String creRepNonSerPath;

    private boolean isSecurityChecked;
    private ASPBuffer actionsBuffer;
    private String showMat; 
    private int currrowItem4;
    private boolean bUnequalMatWo;
    private boolean bNoWoCust;

    //Web Alignment - replace Links with RMBs
    private boolean bOpenNewWindow;
    private String urlString;
    private String newWinHandle;
    private String repair;
    private String unissue;
    //

    private String performRMB;
    private String URLString;
    private String WindowName;
    private String creRepWO;
    private String headMchCode;
    private String headMchDesc;
    private String isSecure[];

    private ASPBuffer ret_data_buffer;
    private String lout;
    private boolean bException;
    private String headNo;

    private ASPTransactionBuffer secBuff;
    private String securityOk;

    private boolean bFirstRequest;
    private boolean bCancelPage;
    private boolean bvimwo;
    private String  wonumber;
    private boolean bReqStartDateEmpty;
    private String sWoNo = "";
    private String sRecieveOrdNo = "";
    private boolean bRecieveOrdCreated;
    private boolean bRecieveOrdNotCreated;
    private String sResCustNo = "";
    private String sResCustLineNo = "";
    private boolean bObjReturned;
    private boolean bObjNotReturned;
    //Bug Id 70012,Start
    private String sPlanSDate;
    private String sWorkTypeId;
    private String sConTypeDb;
    // Bug Id 70012,End
    // Bug 72214, start
    private String sIsLastWo = "";
    // Bug 72214, end
    //Bug 83757,start
    private String sent_signId;
    //Bug 83757,end
    //Bug 85045, Start
    private String sent_org_contract;
    //Bug 85045, End
    //Bug 89703, Start
    private String hasPlanning;
    //Bug 89703, End

    //===============================================================
    // Construction 
    //===============================================================
    public ActiveSeperateReportInWorkOrder(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        sentrowNo =  1;
        nextPrev =  false;
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
        sWONo = ctx.readValue("VALWONO",sWONo); 
        comBarAct = ctx.readFlag("COMBARACT",false);
        noteId = ctx.readValue("NOTEID","");
        noteIdVal = ctx.readValue("NOTEIDVAL","");

        isFind = ctx.readFlag("ISFIND",false);
        comp = ctx.readValue("COMP");

        crePlanPath = ctx.readValue("CREPL",crePlanPath);
        creFromPlan = ctx.readValue("CREFRMPL","false");
        connReconToPlan = ctx.readValue("CONNREFRMPL","false");
        rowToEdit = ctx.readNumber("ROTEDTI",1);
        item4CurrRow = ctx.readNumber("ITEM4CURRROW",0);

        sentNoEmployee = ctx.readFlag("SENTNOEM",false);
        sentEmployee = ctx.readFlag("SENTEMP",false);
        sentrowNo = ctx.readNumber("SENTRWON",sentrowNo);
        recordCancel = ctx.readFlag("RECCENR",false);

        itemMchCode = ctx.readValue("ITEMMCHCODE","");
        itemContract = ctx.readValue("ITEMCONTRACT","");
        itemWoNo = ctx.readValue("ITEMWONO","");
        itemSpareId = ctx.readValue("ITEMSPAREID","");
        nQtyLeft = ctx.readValue("NQTYLEFT","");

        matSingleMode = ctx.readFlag("MATSINGLEMODE",false);
        updateBudg = ctx.readFlag("UPDATEBUDG",false);
        satis = ctx.readFlag("SATIS",true);

        isSecurityChecked = ctx.readFlag("SECURITYCHECKED",false);
        actionsBuffer = ctx.readBuffer("ACTIONSBUFFER");
        showMat = ctx.readValue("SHMAT",""); 
        repair =  ctx.readValue("REPAIR","FALSE");
        unissue = ctx.readValue("UNISSUE","FALSE"); 
        creRepWO = ctx.readValue("AUTOREP","FALSE");
        headMchCode = ctx.readValue("HEADMCHCODE","");
        headMchDesc = ctx.readValue("HEADMCHDESC","");
        //Bug Id 70012, Start
	sWorkTypeId = ctx.readValue("WORKTYPEID","");
	sPlanSDate = ctx.readValue("PLANSDATE","");
	sConTypeDb = ctx.readValue("CONNECTIONTYPEDB","");
        //Bug Id 70012, End

        if (mgr.commandBarActivated())
        {
            comBarAct = true; 
            clearlastItem4();
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
           deleteRow7("YES");
        else if ("BBBB".equals(mgr.readValue("HASPLANNING")))
           deleteRow7("NO");
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
                //Bug 83757,start
                sent_signId = mgr.readValue("NEWCREVAL_EMP","");
                //Bug 83757,end
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
                okFindITEM7();
            }
            tabs.setActiveTab(2);
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

            clearlastItem4();
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
                okFindITEM6();
            }

            if (itemset3.countRows() > 0)
            {
                noteTextEntered = mgr.readValue("NOTETEXTENT","");
                ASPBuffer buff = itemset3.getRow();
                buff.setValue("SNOTETEXT",noteTextEntered);
                itemset3.setRow(buff);
            }
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
                okFindITEM6();
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
                int currHead = toInt(ctx.getGlobal("CURRHEADGLOBAL"));
                headset.goTo(currHead);
                okFindITEM1();
                okFindITEM3();
                okFindITEM4();
                okFindITEM6();
            }

            matSingleMode = true;

        }

        else if (!mgr.isEmpty(mgr.getQueryStringValue("OKMANRESERVE")) && !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();   

            if (headset.countRows() != 1)
            {
                int currHead = toInt(ctx.getGlobal("CURRHEADGLOBAL"));
                headset.goTo(currHead);
                okFindITEM1();
                okFindITEM3();
                okFindITEM4();
                okFindITEM6();
            }

            matSingleMode = true; 
        }

        else if (!mgr.isEmpty(mgr.getQueryStringValue("OKMANUNRESERVE")) && !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();   

            if (headset.countRows() != 1)
            {
                int currHead = toInt(ctx.getGlobal("CURRHEADGLOBAL"));
                headset.goTo(currHead);
                okFindITEM1();
                okFindITEM3();
                okFindITEM4();
                okFindITEM6();
            }

            matSingleMode = true; 
        }

        else if (!mgr.isEmpty(mgr.getQueryStringValue("OKUNISSUE")) && !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();   

            if (headset.countRows() != 1)
            {
                int currHead = toInt(ctx.getGlobal("CURRHEADGLOBAL"));
                headset.goTo(currHead);
                okFindITEM1();
                okFindITEM3();
                okFindITEM4();
                okFindITEM6();
            }

            matSingleMode = true; 
        }

        else if (!mgr.isEmpty(mgr.getQueryStringValue("OKDETATTACHED")) && !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();   

            if (headset.countRows() != 1)
            {
                int currHead = toInt(ctx.getGlobal("CURRHEADGLOBAL"));
                headset.goTo(currHead);
                okFindITEM1();
                okFindITEM3();
                okFindITEM4();
                okFindITEM6();
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
            clearlastItem4();
            okFind();
        }


        else if (!mgr.isEmpty(mgr.getQueryStringValue("DOCANCEL")))
        {
            okFind();
            doCancel();
            if (headset.countRows() != 1)
            {
                int currHead = toInt(ctx.getGlobal("CURRHEADGLOBAL"));
                headset.goTo(currHead);
                okFindITEM1();
                okFindITEM3();
                okFindITEM4();
                okFindITEM6();
            }
        }

        else if ("CCCC".equals(mgr.readValue("BUTTONVAL")))
        {
            unissue="FALSE";
            clearlastItem4();
            reported1();
        }
        else if ("REPORT".equals(mgr.readValue("STATEVAL")))
        {
            unissue="FALSE";
            clearlastItem4();
            reported();
        }
        // Bug 72214, start
        else if ("ISLASTWO".equals(mgr.readValue("BUTTONVAL")))
        {
           sIsLastWo="FALSE";
           clearlastItem4();
           finished0();
        }
        // Bug 72214, end
        else if ("FINIS".equals(mgr.readValue("STATEVAL")))
        {
            repair="FALSE";
            unissue="FALSE";
            clearlastItem4();
            finished();
        }


        else if ("AAAA".equals(mgr.readValue("BUTTONVAL")))
        {
            repair="FALSE";
            unissue="FALSE";
            clearlastItem4(); 
            finished1();
        }
        else if ("BBBB".equals(mgr.readValue("BUTTONVAL")))
        {
            repair="FALSE";
            unissue="FALSE";
            clearlastItem4();
            finished1();
        }

        else if ("CANCE".equals(mgr.readValue("STATEVAL")))
        {
            clearlastItem4();  
            cancelled();
        }

        else if ("DO".equals(mgr.readValue("CREFROMPLAN")))
        {
            clearlastItem4(); 
            createFromPlanTime();    
        }

        else if (mgr.dataTransfered())
            okFind();


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

        //Bug Id 70012, Start
        if (!mgr.isEmpty(mgr.getQueryStringValue("PASS_PLN_S_DATE"))){
	    sPlanSDate = mgr.readValue("PASS_PLN_S_DATE");
	}
        if (!mgr.isEmpty(mgr.getQueryStringValue("PASS_WORK_TYPE_ID"))){
	    sWorkTypeId = mgr.readValue("PASS_WORK_TYPE_ID");
	}

	if (!mgr.isEmpty(mgr.getQueryStringValue("PASS_CON_TYPE_DB"))) {
	    sConTypeDb = mgr.readValue("PASS_CON_TYPE_DB");
	}
        //Bug Id 70012, End

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
        ctx.writeValue("VALWONO",sWONo);
        ctx.writeNumber("CURRENTROW",curentrow);
        ctx.writeValue("QRYSTR",qrystr);
        ctx.writeValue("CREPL",crePlanPath);
        ctx.writeValue("CREFRMPL",creFromPlan);
        ctx.writeValue("CONNREFRMPL",connReconToPlan);
        ctx.writeNumber("ROTEDTI",rowToEdit);
        ctx.writeNumber("ITEM4CURRROW",item4CurrRow);
        ctx.writeFlag("SENTNOEM",sentNoEmployee);
        ctx.writeFlag("SENTEMP",sentEmployee);
        ctx.writeNumber("SENTRWON",sentrowNo);
        ctx.writeFlag("RECCENR",recordCancel);
        ctx.writeFlag("MATSINGLEMODE",matSingleMode);
        ctx.writeFlag("UPDATEBUDG",updateBudg);
        ctx.writeFlag("SATIS",satis);

        ctx.writeFlag("SECURITYCHECKED",isSecurityChecked);
        ctx.writeBuffer("ACTIONSBUFFER",actionsBuffer);   
        //Bug Id 70012, Start
	ctx.writeValue("WORKTYPEID",sWorkTypeId);
	ctx.writeValue("PLANSDATE",sPlanSDate);
	ctx.writeValue("CONNECTIONTYPEDB",sConTypeDb);
        //Bug Id 70012, End
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
        // Enable Multirow Action
        perform("TO_PREPARE__");
    }


    public void prepared()
    {
        ASPManager mgr = getASPManager();
        // Web Alignment - Enable Multirow Action
        // Bug 72214, start
        String sResult = checkCBSAndToolFacilitySite("prepared");

        if (sResult == "TRUE")
	{
	   trans.clear();
	   perform("PREPARE__") ;
	}
        else if (sResult == "CBSSINGLEROW")
        {
           bOpenNewWindow = true;
           ctx.setCookie("PageID_CurrentWindow", "*");
           urlString= "ActiveSeparateDlg3.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) + "&EVENT=PREPARE__";
           newWinHandle = "changeWoState";
        }
        else if (sResult == "CBSMULTIROW")
           mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERCBSNOTVALID: Selected work orders include work orders that are part of a work order structure or part of a project or both. Please perform action on a single work order."));
        else if (sResult == "LACKTOOLS")
           mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERNOTVALID: Please enter Tools and Facility Details."));
        // Bug 72214, end
    }


    public void released()
    {
        ASPManager mgr = getASPManager();
        int count;

        // Bug 72214, start
        String sResult = checkCBSAndToolFacilitySite("released");

        String eventVal = headset.getRow().getValue("OBJEVENTS");
        // Web Alignment - Enable Multirow Action
        if (sResult == "TRUE")
        // Bug 72214, end
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
                headset.setFilterOff();
                headset.refreshAllRows();
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
	// Bug 72214, start
        else if (sResult == "CBSSINGLEROW")
        {
           bOpenNewWindow = true;
           ctx.setCookie("PageID_CurrentWindow", "*");
           urlString= "ActiveSeparateDlg3.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) + "&EVENT=RELEASE__";
           newWinHandle = "changeWoState";
        }
        else if (sResult == "CBSMULTIROW")
           mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERCBSNOTVALID: Selected work orders include work orders that are part of a work order structure or part of a project or both. Please perform action on a single work order."));
        else if (sResult == "LACKTOOLS")
           mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERNOTVALID: Please enter Tools and Facility Details."));
        // Bug 72214, end
    }

    public void started()
    {
        ASPManager mgr = getASPManager();
        int count;

        // Bug 72214, start
        String sResult = checkCBSAndToolFacilitySite("started");

        String eventVal = headset.getRow().getValue("OBJEVENTS");
        // Web Alignment - Enable Multirow Action
        if (sResult == "TRUE")
        // Bug 72214, end
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
                headset.setFilterOff();
    		headset.refreshAllRows();
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
	// Bug 72214, start
        else if (sResult == "CBSSINGLEROW")
        {
           bOpenNewWindow = true;
           ctx.setCookie("PageID_CurrentWindow", "*");
           urlString= "ActiveSeparateDlg3.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) + "&EVENT=START_ORDER__";
           newWinHandle = "changeWoState";
        }
        else if (sResult == "CBSMULTIROW")
           mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERCBSNOTVALID: Selected work orders include work orders that are part of a work order structure or part of a project or both. Please perform action on a single work order."));
        else if (sResult == "LACKTOOLS")
           mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERNOTVALID: Please enter Tools and Facility Details."));
        // Bug 72214, end
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
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERNOTVALID: Please enter Tools and Facility Details."));
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
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERNOTVALID: Please enter Tools and Facility Details."));
    }

    public void reported1()
    {
        ASPManager mgr = getASPManager();
        unissue = "FALSE";


        if (checkToolFacilitySite())
        {
            trans.clear();
            perform("REPORT__");
        }
        else
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERNOTVALID: Please enter Tools and Facility Details."));


    }


    // Bug 72214, start
    public void finished()
    {
       ASPManager mgr = getASPManager();
       int rowno = headset.getCurrentRowNo();

       if ("VIM".equals(headset.getRow().getValue("CONNECTION_TYPE_DB"))) 
       {  
          trans.clear();

          cmd = trans.addCustomFunction("GETHASCONUP","WORK_ORDER_CONNECTION_API.Has_Connection_Up","DUMMY_HAS_CON_UP");
          cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
          
          trans = mgr.perform(trans);

          String sHasConUp = trans.getValue("GETHASCONUP/DATA/DUMMY_HAS_CON_UP");
          String sLastWo = "";
          
          if ("TRUE".equals(sHasConUp)) 
          {
             trans.clear();

             cmd = trans.addCustomFunction("GETLASTWO","Work_Order_From_Vim_API.Is_Last_Wo","DUMMY_LAST_WO");
             cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
             
             trans = mgr.perform(trans);

             sLastWo = trans.getValue("GETLASTWO/DATA/DUMMY_LAST_WO");
             
             if ("TRUE".equals(sLastWo)) 
             {
                sIsLastWo = "TRUE";
                ctx.setCookie( "PageID_IsLastWoRep1", "TRUE" );
                headset.goTo(rowno);
                return;
             }            
          }
       }
       finished0();
    }
   
    //Bug 43848, start
    public void  finished0()
    // Bug 72214, end    
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
                    mgr.redirectTo("ActiveSeperateReportInWorkOrder.page");
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
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERNOTVALID: Please enter Tools and Facility Details.")); 

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
                mgr.redirectTo("ActiveSeperateReportInWorkOrder.page");
            //Bug 70891, end

        }
        else
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERNOTVALID: Please enter Tools and Facility Details."));
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

    // Bug 72214, start
    public String checkCBSAndToolFacilitySite(String command)
    {
       ASPManager mgr = getASPManager();

       int count = 0;
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

       String sEvent = null;
       for (int i = 0;i < count;i++)
       {
          cmd = trans.addCustomCommand("WOTOOLFACILITY"+i,"Work_Order_Tool_Facility_API.Check_Valid_Sites");
          cmd.addParameter("HEAD_ISVALID");
          cmd.addParameter("HEAD_FACSEQUENCE");
          cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

          String eventVal = headset.getRow().getValue("OBJEVENTS");

          if ("prepared".equals(command))
          {
              if (eventVal.indexOf("Prepare") != -1)
                 sEvent = "Prepare";
          }
          else if ("released".equals(command))
          {
              if (eventVal.indexOf("Release") != -1)
                 sEvent = "Release";
              else if (eventVal.indexOf("Replan") != -1)
                 sEvent = "Replan";
          }
          else if ("started".equals(command))
          {
              if (eventVal.indexOf("StartOrder") != -1)
                 sEvent = "StartOrder";
              else if (eventVal.indexOf("Restart") != -1)
                 sEvent = "Restart";
          }

          cmd = trans.addCustomFunction("CBSENABLED"+i,"Pcm_Cbs_Int_API.Is_Str_Trns_Enabled","SEND_TO_CBS");
          cmd.addParameter("WO_NO",headset.getValue("WO_NO"));
          cmd.addParameter("EVENT",sEvent);

          if (headlay.isMultirowLayout())
             headset.next();
       }

       trans = mgr.perform(trans);

       if (headlay.isMultirowLayout())
          headset.setFilterOff();

       boolean sSendToCbs = false;
       for (int i = 0;i < count;i++)
       {
          if ("FALSE".equals(trans.getValue("WOTOOLFACILITY" + i + "/DATA/HEAD_ISVALID")))
             return "LACKTOOLS";

          if ("TRUE".equals(trans.getValue("CBSENABLED"+i + "/DATA/SEND_TO_CBS")))
             sSendToCbs = true;
       }

       if (sSendToCbs)
       {
           if (count > 1)
              return "CBSMULTIROW";
           else
              return "CBSSINGLEROW";      
       }

       return "TRUE";
    }
    // Bug 72214, end


    public boolean CheckObjInRepairShop()
    {
        ASPManager mgr = getASPManager();
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


    public void doCancel()
    {
        ASPManager mgr = getASPManager();

        if (!"FALSE".equals(mgr.getQueryStringValue("DOCANCEL")))
        {
            trans.clear();
            perform("CANCEL__");
            mgr.redirectTo("ActiveSeperateReportInWorkOrder.page");
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
        isSecure =  new String[15];
        String txt;
        ASPManager mgr = getASPManager();
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
        String sFalse ="FALSE";
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

        //-------------------------------------------------------------------//
        //--------------------- Validate T/F data ---------------------------//
        //-------------------------------------------------------------------//

        if ("TOOL_FACILITY_ID".equals(val))
        {
            String sToolId   = mgr.readValue("TOOL_FACILITY_ID");
            String sToolCont = mgr.readValue("ITEM6_CONTRACT");
            String sToolOrg  = mgr.readValue("ITEM6_ORG_CODE");
            String sToolType = mgr.readValue("TOOL_FACILITY_TYPE");
            String sToolDesc = null;
            String sTypeDesc = null;
            String sQty      = mgr.readValue("ITEM6_QTY");
            String sPlannedHour = mgr.readValue("ITEM6_PLANNED_HOUR");
            String sNote     = mgr.readValue("ITEM6_NOTE");
            String sToolCost = null;
            String sToolCostCurr = null;
            String sToolCostAmt  = null;
            String sCatalogNo = mgr.readValue("ITEM6_CATALOG_NO");
            String sCatalogContract = mgr.readValue("ITEM6_CATALOG_NO_CONTRACT");
            String sCatalogDesc = mgr.readValue("ITEM6_CATALOG_NO_DESC");
            String sCatalogPrice = mgr.readValue("ITEM6_SALES_PRICE");
            String sCatalogPriceCurr = mgr.readValue("ITEM6_SALES_CURRENCY");
            String sCatalogDiscount  = mgr.readValue("ITEM6_DISCOUNT");
            String sCatalogDisPrice  = mgr.readValue("ITEM6_DISCOUNTED_PRICE");
            String sPlannedPriceAmt  = mgr.readValue("ITEM6_PLANNED_PRICE");

            cmd = trans.addCustomFunction("TFDESC","Tool_Facility_API.Get_Tool_Facility_Description","TOOL_FACILITY_DESC");
            cmd.addParameter("TOOL_FACILITY_ID");

            cmd = trans.addCustomFunction("TFTYPE","Tool_Facility_API.Get_Tool_Facility_Type","TOOL_FACILITY_TYPE");
            cmd.addParameter("TOOL_FACILITY_ID");

            cmd = trans.addCustomFunction("TFTYPEDESC","Tool_Facility_Type_API.Get_Type_Description","TYPE_DESCRIPTION");
            cmd.addReference("TOOL_FACILITY_TYPE","TFTYPE/DATA");

            cmd = trans.addCustomFunction("TFQTY","Connect_Tools_Facilities_API.Get_Qty","ITEM6_QTY");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("ITEM6_CONTRACT");
            cmd.addParameter("ITEM6_ORG_CODE");

            cmd = trans.addCustomFunction("TFCOST","Work_Order_Tool_Facility_API.Get_Cost","ITEM6_COST");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addReference("TOOL_FACILITY_TYPE","TFTYPE/DATA");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("TFCOSTCURR","Work_Order_Tool_Facility_API.Get_Cost_Currency","ITEM6_COST_CURRENCY");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM6_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addReference("TOOL_FACILITY_TYPE","TFTYPE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFNOTE","Connect_Tools_Facilities_API.Get_Note","ITEM6_NOTE");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("ITEM6_CONTRACT");
            cmd.addParameter("ITEM6_ORG_CODE");

            cmd = trans.addCustomFunction("TFSALESPART","Connect_Tools_Facilities_API.Get_Catalog_No","ITEM6_CATALOG_NO");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("ITEM6_CONTRACT");
            cmd.addParameter("ITEM6_ORG_CODE");

            cmd = trans.addCustomFunction("WOSITE","Active_Separate_API.Get_Contract","ITEM6_WO_CONTRACT");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("SPSITE","Work_Order_Tool_Facility_API.Sales_Part_Exist_On_Wosite","ITEM6_SP_SITE");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","ITEM6_CATALOG_NO_DESC");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPPRICE","Work_Order_Tool_Facility_API.Calculate_Sales_Price","ITEM6_SALES_PRICE");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("TFSPCURR","Work_Order_Tool_Facility_API.Get_Sales_Currency","ITEM6_SALES_CURRENCY");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");

            cmd = trans.addCustomFunction("TFSPDISCOUNT","Work_Order_Tool_Facility_API.Get_Discount","ITEM6_DISCOUNT");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM6_DISCOUNTED_PRICE");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM6_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM6_DISCOUNT","TFSPDISCOUNT/DATA");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM6_PLANNED_PRICE");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM6_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM6_DISCOUNT","TFSPDISCOUNT/DATA");

            trans = mgr.validate(trans);

            sToolDesc = trans.getValue("TFDESC/DATA/TOOL_FACILITY_DESC");
            String sGetToolType = trans.getValue("TFTYPE/DATA/TOOL_FACILITY_TYPE");
            sTypeDesc = trans.getValue("TFTYPEDESC/DATA/TYPE_DESCRIPTION");

            double TfQty = trans.getNumberValue("TFQTY/DATA/QTY");
            String qtyStr = mgr.getASPField("ITEM6_QTY").formatNumber(TfQty);
            if (mgr.isEmpty(sQty))
                sQty = qtyStr;

            double ToolCost = trans.getNumberValue("TFCOST/DATA/COST");
            String costStr = mgr.getASPField("ITEM6_COST").formatNumber(ToolCost);

            sToolCostCurr = trans.getValue("TFCOSTCURR/DATA/COST_CURRENCY");

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM6_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM6_COST_AMOUNT").formatNumber(GetToolCostAmt);

            String sGetNote = trans.getValue("TFNOTE/DATA/NOTE");

            String sCatalogNum = trans.getValue("TFSALESPART/DATA/CATALOG_NO");

            String sWoSite = trans.getValue("WOSITE/DATA/ITEM6_WO_CONTRACT");
            String sSpSite = trans.getValue("SPSITE/DATA/ITEM6_SP_SITE");

            String sGetCatalogDesc = trans.getValue("TFSPDESC/DATA/ITEM6_CATALOG_NO_DESC");

            double GetCatalogPrice = trans.getNumberValue("TFSPPRICE/DATA/SALES_PRICE");
            String catPriceStr = mgr.getASPField("ITEM6_SALES_PRICE").formatNumber(GetCatalogPrice);

            String sGetCatalogPriceCurr = trans.getValue("TFSPCURR/DATA/PRICE_CURRENCY");

            double GetCatalogDiscount = trans.getNumberValue("TFSPDISCOUNT/DATA/DISCOUNT");
            String catDiscount = mgr.getASPField("ITEM6_DISCOUNT").formatNumber(GetCatalogDiscount);

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM6_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM6_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM6_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM6_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            if (mgr.isEmpty(sToolType))
                sToolType = sGetToolType;

            if (mgr.isEmpty(sNote))
                sNote = sGetNote;
            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            {
                sPlannedPriceAmt = plannedPriceAmt;
                sToolCostAmt = costAmtStr;
            }
            if ("TRUE".equals(sSpSite))
            {
                if (!mgr.isEmpty(sToolId) && !mgr.isEmpty(sToolCont) && !mgr.isEmpty(sToolOrg) && mgr.isEmpty(sCatalogNo))
                {
                    sCatalogNo = sCatalogNum;
                    sCatalogContract = sWoSite;
                    sCatalogDesc = sGetCatalogDesc;
                    sCatalogPrice = catPriceStr;
                    sCatalogPriceCurr = sGetCatalogPriceCurr;
                    sCatalogDiscount = catDiscount;
                    sCatalogDisPrice = catDiscountedPrice;
                }
            }

            txt = (mgr.isEmpty(sToolDesc) ? "" : (sToolDesc))+ "^" + (mgr.isEmpty(sToolType) ? "" : (sToolType))+ "^" + 
                  (mgr.isEmpty(sTypeDesc) ? "" : (sTypeDesc))+ "^" + (mgr.isEmpty(sQty) ? "" : (sQty))+ "^" + 
                  (mgr.isEmpty(costStr) ? "" : (costStr))+ "^" + (mgr.isEmpty(sToolCostCurr) ? "" : (sToolCostCurr))+ "^" + 
                  (mgr.isEmpty(sToolCostAmt) ? "" : (sToolCostAmt))+ "^" + (mgr.isEmpty(sCatalogContract) ? "" : (sCatalogContract))+ "^" + 
                  (mgr.isEmpty(sCatalogNo) ? "" : (sCatalogNo))+ "^" + (mgr.isEmpty(sCatalogDesc) ? "" : (sCatalogDesc))+ "^" + 
                  (mgr.isEmpty(sCatalogPrice) ? "" : (sCatalogPrice))+ "^" + (mgr.isEmpty(sCatalogPriceCurr) ? "" : (sCatalogPriceCurr))+ "^" + 
                  (mgr.isEmpty(sCatalogDiscount) ? "" : (sCatalogDiscount))+ "^" + (mgr.isEmpty(sCatalogDisPrice) ? "" : (sCatalogDisPrice))+ "^" + 
                  (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" + (mgr.isEmpty(sNote) ? "" : (sNote))+ "^" ;

            mgr.responseWrite(txt);
        }

        else if ("TOOL_FACILITY_TYPE".equals(val))
        {
            String sQty      = mgr.readValue("ITEM6_QTY");
            String sPlannedHour = mgr.readValue("ITEM6_PLANNED_HOUR");
            String sToolCostAmt = null;

            cmd = trans.addCustomFunction("TFTYPEDESC","Tool_Facility_Type_API.Get_Type_Description","TYPE_DESCRIPTION");
            cmd.addParameter("TOOL_FACILITY_TYPE");

            cmd = trans.addCustomFunction("TFCOST","Work_Order_Tool_Facility_API.Get_Cost","ITEM6_COST");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("TFCOSTCURR","Work_Order_Tool_Facility_API.Get_Cost_Currency","ITEM6_COST_CURRENCY");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM6_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);

            trans = mgr.validate(trans);

            String sTypeDesc = trans.getValue("TFTYPEDESC/DATA/TYPE_DESCRIPTION");

            double ToolCost = trans.getNumberValue("TFCOST/DATA/COST");
            String costStr = mgr.getASPField("ITEM6_COST").formatNumber(ToolCost);

            String sToolCostCurr = trans.getValue("TFCOSTCURR/DATA/COST_CURRENCY");

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM6_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM6_COST_AMOUNT").formatNumber(GetToolCostAmt);

            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
                sToolCostAmt = costAmtStr;

            txt = (mgr.isEmpty(sTypeDesc) ? "" : (sTypeDesc))+ "^" + (mgr.isEmpty(costStr) ? "" : (costStr))+ "^" + 
                  (mgr.isEmpty(sToolCostCurr) ? "" : (sToolCostCurr))+ "^" + (mgr.isEmpty(sToolCostAmt) ? "" : (sToolCostAmt))+ "^";

            mgr.responseWrite(txt); 
        }


        else if ("ITEM6_CONTRACT".equals(val))
        {
            String sToolId   = mgr.readValue("TOOL_FACILITY_ID");
            String sToolCont = mgr.readValue("ITEM6_CONTRACT");
            String sToolOrg  = mgr.readValue("ITEM6_ORG_CODE");
            String sQty      = mgr.readValue("ITEM6_QTY");
            String sNote     = mgr.readValue("ITEM6_NOTE");
            String sPlannedHour = mgr.readValue("ITEM6_PLANNED_HOUR");
            String sToolCostAmt = null;
            String sCatalogNo = mgr.readValue("ITEM6_CATALOG_NO");
            String sCatalogContract = mgr.readValue("ITEM6_CATALOG_NO_CONTRACT");
            String sCatalogDesc = mgr.readValue("ITEM6_CATALOG_NO_DESC");
            String sCatalogPrice = mgr.readValue("ITEM6_SALES_PRICE");
            String sCatalogPriceCurr = mgr.readValue("ITEM6_SALES_CURRENCY");
            String sCatalogDiscount  = mgr.readValue("ITEM6_DISCOUNT");
            String sCatalogDisPrice  = mgr.readValue("ITEM6_DISCOUNTED_PRICE");
            String sPlannedPriceAmt  = mgr.readValue("ITEM6_PLANNED_PRICE");
            String sWn = mgr.readValue("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("TFNOTE","Connect_Tools_Facilities_API.Get_Note","ITEM6_NOTE");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("ITEM6_CONTRACT");
            cmd.addParameter("ITEM6_ORG_CODE");

            cmd = trans.addCustomFunction("TFQTY","Connect_Tools_Facilities_API.Get_Qty","ITEM6_QTY");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("ITEM6_CONTRACT");
            cmd.addParameter("ITEM6_ORG_CODE");  

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM6_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSALESPART","Connect_Tools_Facilities_API.Get_Catalog_No","ITEM6_CATALOG_NO");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("ITEM6_CONTRACT");
            cmd.addParameter("ITEM6_ORG_CODE");

            cmd = trans.addCustomFunction("WOSITE","Active_Separate_API.Get_Contract","ITEM6_WO_CONTRACT");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("SPSITE","Work_Order_Tool_Facility_API.Sales_Part_Exist_On_Wosite","ITEM6_SP_SITE");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","ITEM6_CATALOG_NO_DESC");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPPRICE","Work_Order_Tool_Facility_API.Calculate_Sales_Price","ITEM6_SALES_PRICE");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("TFSPCURR","Work_Order_Tool_Facility_API.Get_Sales_Currency","ITEM6_SALES_CURRENCY");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");

            cmd = trans.addCustomFunction("TFSPDISCOUNT","Work_Order_Tool_Facility_API.Get_Discount","ITEM6_DISCOUNT");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM6_DISCOUNTED_PRICE");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM6_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM6_DISCOUNT","TFSPDISCOUNT/DATA");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM6_PLANNED_PRICE");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM6_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM6_DISCOUNT","TFSPDISCOUNT/DATA");

            trans = mgr.validate(trans);

            String sGetNote = trans.getValue("TFNOTE/DATA/NOTE");

            double TfQty = trans.getNumberValue("TFQTY/DATA/QTY");
            String qtyStr = mgr.getASPField("ITEM6_QTY").formatNumber(TfQty);
            if (mgr.isEmpty(sQty))
                sQty = qtyStr;

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM6_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM6_COST_AMOUNT").formatNumber(GetToolCostAmt);

            String sCatalogNum = trans.getValue("TFSALESPART/DATA/CATALOG_NO");

            String sWoSite = trans.getValue("WOSITE/DATA/ITEM6_WO_CONTRACT");

            String sSpSite = trans.getValue("SPSITE/DATA/ITEM6_SP_SITE");

            String sGetCatalogDesc = trans.getValue("TFSPDESC/DATA/ITEM6_CATALOG_NO_DESC");

            double GetCatalogPrice = trans.getNumberValue("TFSPPRICE/DATA/SALES_PRICE");
            String catPriceStr = mgr.getASPField("ITEM6_SALES_PRICE").formatNumber(GetCatalogPrice);

            String sGetCatalogPriceCurr = trans.getValue("TFSPCURR/DATA/PRICE_CURRENCY");

            double GetCatalogDiscount = trans.getNumberValue("TFSPDISCOUNT/DATA/DISCOUNT");
            String catDiscount = mgr.getASPField("ITEM6_DISCOUNT").formatNumber(GetCatalogDiscount);

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM6_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM6_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM6_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM6_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            if (mgr.isEmpty(sNote))
                sNote = sGetNote;
            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            {
                sPlannedPriceAmt = plannedPriceAmt;
                sToolCostAmt = costAmtStr;
            }
            if ("TRUE".equals(sSpSite))
            {
                if (!mgr.isEmpty(sToolId) && !mgr.isEmpty(sToolCont) && !mgr.isEmpty(sToolOrg) && mgr.isEmpty(sCatalogNo))
                {
                    sCatalogNo = sCatalogNum;
                    sCatalogContract = sWoSite;
                    sCatalogDesc = sGetCatalogDesc;
                    sCatalogPrice = catPriceStr;
                    sCatalogPriceCurr = sGetCatalogPriceCurr;
                    sCatalogDiscount = catDiscount;
                    sCatalogDisPrice = catDiscountedPrice;
                }
            }

            txt = (mgr.isEmpty(sNote) ? "" : (sNote))+ "^" + (mgr.isEmpty(sQty) ? "" : (sQty))+ "^" + 
                  (mgr.isEmpty(sToolCostAmt) ? "" : (sToolCostAmt))+ "^" + (mgr.isEmpty(sCatalogContract) ? "" : (sCatalogContract))+ "^" + 
                  (mgr.isEmpty(sCatalogNo) ? "" : (sCatalogNo))+ "^" + (mgr.isEmpty(sCatalogDesc) ? "" : (sCatalogDesc))+ "^" + 
                  (mgr.isEmpty(sCatalogPrice) ? "" : (sCatalogPrice))+ "^" + (mgr.isEmpty(sCatalogPriceCurr) ? "" : (sCatalogPriceCurr))+ "^" + 
                  (mgr.isEmpty(sCatalogDiscount) ? "" : (sCatalogDiscount))+ "^" + (mgr.isEmpty(sCatalogDisPrice) ? "" : (sCatalogDisPrice))+ "^" + 
                  (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" ;

            mgr.responseWrite(txt);
        }
        else if ("ITEM6_ORG_CODE".equals(val))
        {
            String sToolId   = mgr.readValue("TOOL_FACILITY_ID");
            String sToolCont = mgr.readValue("ITEM6_CONTRACT");
            String sToolOrg  = mgr.readValue("ITEM6_ORG_CODE");
            String sQty      = mgr.readValue("ITEM6_QTY");
            String sNote     = mgr.readValue("ITEM6_NOTE");
            String sPlannedHour = mgr.readValue("ITEM6_PLANNED_HOUR");
            String sToolCostAmt = null;
            String sCatalogNo = mgr.readValue("ITEM6_CATALOG_NO");
            String sCatalogContract = mgr.readValue("ITEM6_CATALOG_NO_CONTRACT");
            String sCatalogDesc = mgr.readValue("ITEM6_CATALOG_NO_DESC");
            String sCatalogPrice = mgr.readValue("ITEM6_SALES_PRICE");
            String sCatalogPriceCurr = mgr.readValue("ITEM6_SALES_CURRENCY");
            String sCatalogDiscount  = mgr.readValue("ITEM6_DISCOUNT");
            String sCatalogDisPrice  = mgr.readValue("ITEM6_DISCOUNTED_PRICE");
            String sPlannedPriceAmt  = mgr.readValue("ITEM6_PLANNED_PRICE");

            cmd = trans.addCustomFunction("TFNOTE","Connect_Tools_Facilities_API.Get_Note","ITEM6_NOTE");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("ITEM6_CONTRACT");
            cmd.addParameter("ITEM6_ORG_CODE");

            cmd = trans.addCustomFunction("TFQTY","Connect_Tools_Facilities_API.Get_Qty","ITEM6_QTY");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("ITEM6_CONTRACT");
            cmd.addParameter("ITEM6_ORG_CODE");  

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM6_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSALESPART","Connect_Tools_Facilities_API.Get_Catalog_No","ITEM6_CATALOG_NO");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("ITEM6_CONTRACT");
            cmd.addParameter("ITEM6_ORG_CODE");

            cmd = trans.addCustomFunction("WOSITE","Active_Separate_API.Get_Contract","ITEM6_WO_CONTRACT");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("SPSITE","Work_Order_Tool_Facility_API.Sales_Part_Exist_On_Wosite","ITEM6_SP_SITE");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","ITEM6_CATALOG_NO_DESC");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPPRICE","Work_Order_Tool_Facility_API.Calculate_Sales_Price","ITEM6_SALES_PRICE");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("TFSPCURR","Work_Order_Tool_Facility_API.Get_Sales_Currency","ITEM6_SALES_CURRENCY");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");

            cmd = trans.addCustomFunction("TFSPDISCOUNT","Work_Order_Tool_Facility_API.Get_Discount","ITEM6_DISCOUNT");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM6_DISCOUNTED_PRICE");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM6_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM6_DISCOUNT","TFSPDISCOUNT/DATA");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM6_PLANNED_PRICE");
            cmd.addReference("ITEM6_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM6_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM6_DISCOUNT","TFSPDISCOUNT/DATA");


            trans = mgr.validate(trans);

            String sGetNote = trans.getValue("TFNOTE/DATA/NOTE");

            double TfQty = trans.getNumberValue("TFQTY/DATA/QTY");
            String qtyStr = mgr.getASPField("ITEM6_QTY").formatNumber(TfQty);
            if (mgr.isEmpty(sQty))
                sQty = qtyStr;

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM6_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM6_COST_AMOUNT").formatNumber(GetToolCostAmt);

            String sCatalogNum = trans.getValue("TFSALESPART/DATA/CATALOG_NO");

            String sWoSite = trans.getValue("WOSITE/DATA/ITEM6_WO_CONTRACT");

            String sSpSite = trans.getValue("SPSITE/DATA/ITEM6_SP_SITE");

            String sGetCatalogDesc = trans.getValue("TFSPDESC/DATA/ITEM6_CATALOG_NO_DESC");

            double GetCatalogPrice = trans.getNumberValue("TFSPPRICE/DATA/SALES_PRICE");
            String catPriceStr = mgr.getASPField("ITEM6_SALES_PRICE").formatNumber(GetCatalogPrice);

            String sGetCatalogPriceCurr = trans.getValue("TFSPCURR/DATA/PRICE_CURRENCY");

            double GetCatalogDiscount = trans.getNumberValue("TFSPDISCOUNT/DATA/DISCOUNT");
            String catDiscount = mgr.getASPField("ITEM6_DISCOUNT").formatNumber(GetCatalogDiscount);

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM6_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM6_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM6_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM6_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            if (mgr.isEmpty(sNote))
                sNote = sGetNote;
            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            {
                sPlannedPriceAmt = plannedPriceAmt;
                sToolCostAmt = costAmtStr;
            }
            if ("TRUE".equals(sSpSite))
            {
                if (!mgr.isEmpty(sToolId) && !mgr.isEmpty(sToolCont) && !mgr.isEmpty(sToolOrg) && mgr.isEmpty(sCatalogNo))
                {
                    sCatalogNo = sCatalogNum;
                    sCatalogContract = sWoSite;
                    sCatalogDesc = sGetCatalogDesc;
                    sCatalogPrice = catPriceStr;
                    sCatalogPriceCurr = sGetCatalogPriceCurr;
                    sCatalogDiscount = catDiscount;
                    sCatalogDisPrice = catDiscountedPrice;
                }
            }

            txt = (mgr.isEmpty(sNote) ? "" : (sNote))+ "^" + (mgr.isEmpty(sQty) ? "" : (sQty))+ "^" + 
                  (mgr.isEmpty(sToolCostAmt) ? "" : (sToolCostAmt))+ "^" + (mgr.isEmpty(sCatalogContract) ? "" : (sCatalogContract))+ "^" + 
                  (mgr.isEmpty(sCatalogNo) ? "" : (sCatalogNo))+ "^" + (mgr.isEmpty(sCatalogDesc) ? "" : (sCatalogDesc))+ "^" + 
                  (mgr.isEmpty(sCatalogPrice) ? "" : (sCatalogPrice))+ "^" + (mgr.isEmpty(sCatalogPriceCurr) ? "" : (sCatalogPriceCurr))+ "^" + 
                  (mgr.isEmpty(sCatalogDiscount) ? "" : (sCatalogDiscount))+ "^" + (mgr.isEmpty(sCatalogDisPrice) ? "" : (sCatalogDisPrice))+ "^" + 
                  (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" ;

            mgr.responseWrite(txt);
        }
        else if ("ITEM6_QTY".equals(val))
        {
            String sQty      = mgr.readValue("ITEM6_QTY");
            String sPlannedHour = mgr.readValue("ITEM6_PLANNED_HOUR");
            String sToolCostAmt = null;
            String sPlannedPriceAmt  = null;

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM6_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM6_PLANNED_PRICE");
            cmd.addParameter("ITEM6_CATALOG_NO");
            cmd.addParameter("ITEM6_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM6_SALES_PRICE");
            cmd.addParameter("ITEM6_DISCOUNT");

            trans = mgr.validate(trans);

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM6_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM6_COST_AMOUNT").formatNumber(GetToolCostAmt);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM6_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM6_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            {
                sPlannedPriceAmt = plannedPriceAmt;
                sToolCostAmt = costAmtStr;
            }

            txt = (mgr.isEmpty(sToolCostAmt) ? "" : (sToolCostAmt))+ "^" + (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^"; 

            mgr.responseWrite(txt);
        }
        else if ("ITEM6_PLANNED_HOUR".equals(val))
        {
            String sQty      = mgr.readValue("ITEM6_QTY");
            String sPlannedHour = mgr.readValue("ITEM6_PLANNED_HOUR");
            String sToolCostAmt = null;
            String sPlannedPriceAmt  = null;

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM6_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM6_PLANNED_PRICE");
            cmd.addParameter("ITEM6_CATALOG_NO");
            cmd.addParameter("ITEM6_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM6_SALES_PRICE");
            cmd.addParameter("ITEM6_DISCOUNT");

            trans = mgr.validate(trans);

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM6_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM6_COST_AMOUNT").formatNumber(GetToolCostAmt);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM6_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM6_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            {
                sPlannedPriceAmt = plannedPriceAmt;
                sToolCostAmt = costAmtStr;
            }

            txt = (mgr.isEmpty(sToolCostAmt) ? "" : (sToolCostAmt))+ "^" + (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^"; 

            mgr.responseWrite(txt);  
        }

        else if ("ITEM6_CATALOG_NO".equals(val))
        {
            String sQty      = mgr.readValue("ITEM6_QTY");
            String sPlannedHour = mgr.readValue("ITEM6_PLANNED_HOUR");
            String sSalesPart = mgr.readValue("ITEM6_CATALOG_NO");
            String sCatalogDesc = null;
            String sCatalogPrice = null;
            String sCatalogPriceCurr = null;
            String sCatalogDiscount  = null;
            String sCatalogDisPrice  = null;
            String sPlannedPriceAmt  = null;

            cmd = trans.addCustomFunction("WOSITE","Active_Separate_API.Get_Contract","ITEM6_WO_CONTRACT");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("SPSITE","Work_Order_Tool_Facility_API.Sales_Part_Exist_On_Wosite","ITEM6_SP_SITE");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_CATALOG_NO");

            cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","ITEM6_CATALOG_NO_DESC");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_CATALOG_NO");

            cmd = trans.addCustomFunction("TFSPPRICE","Work_Order_Tool_Facility_API.Calculate_Sales_Price","ITEM6_SALES_PRICE");
            cmd.addParameter("ITEM6_CATALOG_NO");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");

            cmd = trans.addCustomFunction("TFSPCURR","Work_Order_Tool_Facility_API.Get_Sales_Currency","ITEM6_SALES_CURRENCY");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");

            cmd = trans.addCustomFunction("TFSPDISCOUNT","Work_Order_Tool_Facility_API.Get_Discount","ITEM6_DISCOUNT");
            cmd.addParameter("ITEM6_CATALOG_NO");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM6_DISCOUNTED_PRICE");
            cmd.addParameter("ITEM6_CATALOG_NO");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM6_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM6_DISCOUNT","TFSPDISCOUNT/DATA");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM6_PLANNED_PRICE");
            cmd.addParameter("ITEM6_CATALOG_NO");
            cmd.addReference("ITEM6_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM6_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM6_DISCOUNT","TFSPDISCOUNT/DATA");

            trans = mgr.validate(trans);

            String sWoSite = trans.getValue("WOSITE/DATA/ITEM6_WO_CONTRACT");
            String sSpSite = trans.getValue("SPSITE/DATA/ITEM6_SP_SITE");

            String sGetCatalogDesc = trans.getValue("TFSPDESC/DATA/ITEM6_CATALOG_NO_DESC");

            double GetCatalogPrice = trans.getNumberValue("TFSPPRICE/DATA/SALES_PRICE");
            String catPriceStr = mgr.getASPField("ITEM6_SALES_PRICE").formatNumber(GetCatalogPrice);

            String sGetCatalogPriceCurr = trans.getValue("TFSPCURR/DATA/PRICE_CURRENCY");

            double GetCatalogDiscount = trans.getNumberValue("TFSPDISCOUNT/DATA/DISCOUNT");
            String catDiscount = mgr.getASPField("ITEM6_DISCOUNT").formatNumber(GetCatalogDiscount);

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM6_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM6_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM6_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM6_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);


            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            {
                sPlannedPriceAmt = plannedPriceAmt;
            }
            if ("TRUE".equals(sSpSite))
            {
                sCatalogDesc = sGetCatalogDesc;
                sCatalogPrice = catPriceStr;
                sCatalogPriceCurr = sGetCatalogPriceCurr;
                sCatalogDiscount = catDiscount;
                sCatalogDisPrice = catDiscountedPrice;
            }

            txt = (mgr.isEmpty(sCatalogDesc) ? "" : (sCatalogDesc))+ "^" + (mgr.isEmpty(sCatalogPrice) ? "" : (sCatalogPrice))+ "^" + 
                  (mgr.isEmpty(sCatalogPriceCurr) ? "" : (sCatalogPriceCurr))+ "^" + (mgr.isEmpty(sCatalogDiscount) ? "" : (sCatalogDiscount))+ "^" + 
                  (mgr.isEmpty(sCatalogDisPrice) ? "" : (sCatalogDisPrice))+ "^" + (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" ;

            mgr.responseWrite(txt);
        }
        else if ("ITEM6_SALES_PRICE".equals(val))
        {
            String sQty      = mgr.readValue("ITEM6_QTY");
            String sPlannedHour = mgr.readValue("ITEM6_PLANNED_HOUR");
            String sCatalogDisPrice  = null;
            String sPlannedPriceAmt  = null;

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM6_DISCOUNTED_PRICE");
            cmd.addParameter("ITEM6_CATALOG_NO");
            cmd.addParameter("ITEM6_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM6_SALES_PRICE");
            cmd.addParameter("ITEM6_DISCOUNT");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM6_PLANNED_PRICE");
            cmd.addParameter("ITEM6_CATALOG_NO");
            cmd.addParameter("ITEM6_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM6_SALES_PRICE");
            cmd.addParameter("ITEM6_DISCOUNT");

            trans = mgr.validate(trans);

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM6_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM6_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM6_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM6_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
                sPlannedPriceAmt = plannedPriceAmt;

            sCatalogDisPrice = catDiscountedPrice;

            txt = (mgr.isEmpty(sCatalogDisPrice) ? "" : (sCatalogDisPrice))+ "^" + (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" ;
            mgr.responseWrite(txt);
        }
        else if ("ITEM6_DISCOUNT".equals(val))
        {
            String sQty      = mgr.readValue("ITEM6_QTY");
            String sPlannedHour = mgr.readValue("ITEM6_PLANNED_HOUR");
            String sCatalogDisPrice  = null;
            String sPlannedPriceAmt  = null;

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM6_DISCOUNTED_PRICE");
            cmd.addParameter("ITEM6_CATALOG_NO");
            cmd.addParameter("ITEM6_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM6_SALES_PRICE");
            cmd.addParameter("ITEM6_DISCOUNT");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM6_PLANNED_PRICE");
            cmd.addParameter("ITEM6_CATALOG_NO");
            cmd.addParameter("ITEM6_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM6_WO_NO");
            cmd.addParameter("ITEM6_QTY",sQty);
            cmd.addParameter("ITEM6_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM6_SALES_PRICE");
            cmd.addParameter("ITEM6_DISCOUNT");

            trans = mgr.validate(trans);

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM6_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM6_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM6_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM6_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
                sPlannedPriceAmt = plannedPriceAmt;

            sCatalogDisPrice = catDiscountedPrice;

            txt = (mgr.isEmpty(sCatalogDisPrice) ? "" : (sCatalogDisPrice))+ "^" + (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" ;
            mgr.responseWrite(txt);
        }

        //--------------------------------------------------------------------------//
        //---------------------------- End of T/F Validate -------------------------//
        //--------------------------------------------------------------------------//

        else if ("NOTE".equals(val))
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

            txt =(mgr.isEmpty(c_gen_note) ? "" : c_gen_note) + "^"+(mgr.isEmpty(cmb_gen_note) ? "" : cmb_gen_note) + "^";

            mgr.responseWrite(txt); 
        }
        else if ("CB_GENERATE_NOTE".equals(val))
        {
            String c_gen_note;
            String cmb_gen_note;

            c_gen_note = mgr.readValue("CB_GENERATE_NOTE","");

            if ("TRUE".equals(c_gen_note))
            {
                cmb_gen_note = "0";
            }
            else
            {
                cmb_gen_note = "1";
            }

            txt =(mgr.isEmpty(cmb_gen_note) ? "" : cmb_gen_note) + "^";

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
        else if ("DATE_REQUIRED".equals(val))
        {
            ASPBuffer buff = mgr.newASPBuffer();
            buff.addFieldItem("DATE_REQUIRED",mgr.readValue("DATE_REQUIRED"));
            mgr.responseWrite(mgr.readValue("DATE_REQUIRED"));
        }
        else if ("REQUIRED_START_DATE".equals(val))
        {
            ASPBuffer buff = mgr.newASPBuffer();
            buff.addFieldItem("REQUIRED_START_DATE",mgr.readValue("REQUIRED_START_DATE"));

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
            buff.addFieldItem("REQUIRED_END_DATE",mgr.readValue("REQUIRED_END_DATE"));
            buff.addFieldItem("REQUIRED_START_DATE",mgr.readValue("REQUIRED_START_DATE"));

            Date startDate = buff.getFieldDateValue("REQUIRED_START_DATE");
            Date endDate = buff.getFieldDateValue("REQUIRED_END_DATE");

            boolean isError = endDate.before(startDate);

            if (isError)
                mgr.responseWrite("ERROR_VAL");
            else
                mgr.responseWrite(mgr.formatDate("REQUIRED_END_DATE",endDate));
        }
        // Validate Plan line no
        else if ("PLAN_LINE_NO".equals(val))
        {
            cmd = trans.addCustomFunction("PARA10", "Work_Order_Role_API.Get_Plan_Row_No", "CRAFT_ROW" );
            cmd.addParameter("WO_NO");
            cmd.addParameter("PLAN_LINE_NO");

            cmd = trans.addCustomFunction("PARA11", "Work_Order_Role_API.Get_Plan_Ref_Number", "CRAFT_REFERENCE_NUMBER"); 
            cmd.addParameter("WO_NO");
            cmd.addParameter("PLAN_LINE_NO");
            
	    cmd = trans.addCustomFunction("PARA20", "Work_Order_Role_API.Get_Plan_Team_Contract", "TEAM_CONTRACT"); 
            cmd.addParameter("WO_NO");
            cmd.addParameter("PLAN_LINE_NO");

            cmd = trans.addCustomFunction("PARA21", "Work_Order_Role_API.Get_Plan_Team_Id", "TEAM_ID"); 
            cmd.addParameter("WO_NO");
            cmd.addParameter("PLAN_LINE_NO");

            trans = mgr.validate(trans);

            String strCraftRow   = trans.getValue("PARA10/DATA/CRAFT_ROW");
            String strCraftRefNo = trans.getValue("PARA11/DATA/CRAFT_REFERENCE_NUMBER");
            String strTeamCon = trans.getValue("PARA20/DATA/TEAM_CONTRACT");
            String strTeamId = trans.getValue("PARA21/DATA/TEAM_ID");

            txt = (mgr.isEmpty(strCraftRow)? "" : strCraftRow) +"^"+ (mgr.isEmpty(strCraftRefNo)? "" : strCraftRefNo) +"^"+ (mgr.isEmpty(strTeamCon)? "" : strTeamCon) +"^"+ (mgr.isEmpty(strTeamId)? "" : strTeamId) +"^";

            mgr.responseWrite(txt);
        }
        //
        else if ("EMP_NO".equals(val))
        {
            boolean securityOk = false;
            String strCatalogNo = null;
            String strCatalogDesc = null;
            String sDefRole = "";
            String sDefOrg = mgr.readValue("ITEM1_ORG_CODE");

            secBuff = mgr.newASPTransactionBuffer();
            secBuff.addSecurityQuery("SALES_PART");
            secBuff = mgr.perform(secBuff);

            String reqstr = null;
            String ar[] = new String[5];
            String emp_id = "";
            String emp_sign = "";
            String site = "";
            String new_sign = mgr.readValue("EMP_NO","");

            if (new_sign.indexOf("^",0)>0)
            {
                ar[0] = new_sign.substring(0,new_sign.indexOf("^"));
                new_sign = new_sign.substring(new_sign.indexOf("^")+1,new_sign.length());
                ar[1] = new_sign.substring(0,new_sign.indexOf("^"));
                new_sign = new_sign.substring(new_sign.indexOf("^")+1,new_sign.length());
                ar[2] = new_sign.substring(0,new_sign.indexOf("^"));
                new_sign = new_sign.substring(new_sign.indexOf("^")+1,new_sign.length());
                ar[3] = new_sign.substring(0,new_sign.indexOf("^"));
                new_sign = new_sign.substring(new_sign.indexOf("^")+1,new_sign.length());

                emp_sign = ar[0];
                emp_id = ar[1];
                
                if(new_sign.indexOf("^",0)>0)
                    site = new_sign.substring(0,new_sign.indexOf("^"));
                else
                    site = ar[3]; 
            }
            else
            {
                emp_id = mgr.readValue ("EMP_NO");;
                emp_sign = mgr.readValue ("EMP_SIGNATURE");
                site= mgr.readValue ("ITEM1_CONTRACT");
            }
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
                strCatalogNo = trans.getValue("CATALO/DATA/CATALOG_NO");
                strCatalogDesc= trans.getValue("CATDESC/DATA/SALESPARTCATALOGDESC");
            }

            String strRoleCode = trans.getValue("PARA1/DATA/ROLE_CODE");        
            if (mgr.isEmpty(strRoleCode))
                strRoleCode = sDefRole;

            String strDesc = trans.getValue("PARA2/DATA/CMNT");
            String strOrgCode = trans.getValue("PARA3/DATA/DEPART");
            if (mgr.isEmpty(strOrgCode))
                strOrgCode = sDefOrg;

            String strSig = trans.getValue("PARA4/DATA/EMP_SIGNATURE");
            String strName = trans.getValue("PARA5/DATA/NAME");
            String strConcatDesc = (mgr.isEmpty(strName)? "" : (strName + ", ")) + (mgr.isEmpty(strOrgCode)? "" : strOrgCode) + ",("+ strDesc +")"; 

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
            double nBuyQtyDue;
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
            String colPriceListNo;
            boolean securityOk = false;
            boolean salesSecOk = false;
            boolean roleSecOk = false;
            boolean isError = false;
            double colDiscountPrice = 0;
            String strDisplayDiscount = "";

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
                cmd = trans.addCustomFunction("CST", "Work_Order_Planning_Util_Api.Get_Cost", "SALESPARTCOST" );
                cmd.addParameter("ITEM1_ORG_CODE");
                cmd.addParameter("ROLE_CODE");
                //Bug 83757,Start
                cmd.addParameter("ITEM1_CATALOG_CONTRACT");
                //Bug 83757,End
                cmd.addParameter("CATALOG_NO");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addParameter("EMP_NO",mgr.readValue("EMP_NO"));
                cmd.addParameter("DUMMY","TRUE");
            }
            else
            {
                cmd = trans.addCustomFunction("CST", "Work_Order_Planning_Util_Api.Get_Cost", "SALESPARTCOST" );
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
                if (roleSecOk &&  ! mgr.isEmpty(mgr.readValue("CATALOG_NO")))
                {
                    double colQty;

                    colSalesPartCost = mgr.readNumberValue("SALESPARTCOST");
                    if (isNaN(colSalesPartCost))
                        colSalesPartCost = 0;

                    colQty = mgr.readNumberValue("QTY");
                    if (isNaN(colQty))
                        colQty = 0;

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

            colSalesPartCost = 0;

            colSalesPartCost = trans.getBuffer("CST/DATA").getNumberValue("SALESPARTCOST");

            if (isNaN(colSalesPartCost))
                colSalesPartCost = 0;

            if (!mgr.isEmpty(mgr.readValue("QTY"))  &&   (mgr.readNumberValue("QTY") <= 24)  &&  ( !roleSecOk || mgr.isEmpty(mgr.readValue("CATALOG_NO"))))
                colAmount = trans.getBuffer("CALAMOUNT/DATA").getNumberValue("AMOUNT");

            if (isNaN(colAmount))
                colAmount = 0;

            if (securityOk)
            {
                colListPrice = trans.getNumberValue("PRICEINFO/DATA/SALE_PRICE"); 
                if (isNaN(colListPrice))
                    colListPrice = 0;

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
                      (mgr.isEmpty(strColAmount)?"":strColAmount)+"^"+
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
            String colPriceListNo;
            String colCmnt = "";
            String colCatalogNo = null;
            String colCatalogDesc = null;
            boolean securityOk = false;
            boolean salesSecOk = false;
            double colDiscountPrice = 0;
            String strDisplayDiscount = null;

            String reqstr = null;
            int startpos = 0;
            int endpos = 0;
            int i = 0;
            String ar[] = new String[10];

            String sRoleCode = "";
            String sOrgContract = "";
            String new_role_code = mgr.readValue("ROLE_CODE","");

            //Bug 83757,Start
            String strCatalog ="";
            String strAgreementId = "";
            String strCatalogDesc ="";
            String strCustno = "";
            //Bug 83757,End

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

            trans.clear();
            secBuff = mgr.newASPTransactionBuffer();
            secBuff.addSecurityQuery("SALES_PART");
            secBuff.addSecurityQuery("Company_Finance_API","Get_Currency_Code");
            secBuff.addSecurityQuery("Customer_Order_Pricing_API","Get_Valid_Price_List,Get_Sales_Part_Price_Info");

            secBuff = mgr.perform(secBuff);

            if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
                salesSecOk = true;

            if (secBuff.getSecurityInfo().itemExists("SALES_PART") && secBuff.getSecurityInfo().itemExists("Company_Finance_API.Get_Currency_Code") && secBuff.getSecurityInfo().itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List"))
                securityOk = true;

             trans.clear();

             //Bug 83757,Start

            if (!mgr.isEmpty(mgr.readValue("ROLE_CODE")))
            {
                cmd = trans.addCustomFunction("ROLEDESC","Role_API.Get_Description","CMNT");
                cmd.addParameter("ROLE_CODE",sRoleCode);

                if (salesSecOk)
                {
                   cmd = trans.addCustomFunction("DEFCATNO","Role_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
                    cmd.addParameter("ROLE_CODE",sRoleCode);
                    cmd.addParameter("ITEM1_CATALOG_CONTRACT",mgr.readValue("ITEM1_CATALOG_CONTRACT")); 
                    
                      trans = mgr.validate(trans);
                    
                     strCatalog = trans.getValue("DEFCATNO/DATA/CATALOG_NO");   
                     
                     if( !mgr.isEmpty(strCatalog))
                     {
                        
                        cmd = trans.addCustomFunction("DEFCATALOGNO","Organization_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");                        
                        cmd.addParameter("ITEM1_CATALOG_CONTRACT",mgr.readValue("ITEM1_CATALOG_CONTRACT")); 
                        cmd.addParameter("ITEM1_ORG_CODE",mgr.readValue("ITEM1_ORG_CODE"));
                        
                         cmd = trans.addCustomFunction("DEFCATDESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATADESC");
                         cmd.addParameter("ITEM1_CATALOG_CONTRACT"); 
                         cmd.addReference("CATALOG_NO",trans.getValue("DEFCATALOGNO/DATA/CATALOG_NO"));
                         
                         trans.clear();
                         trans = mgr.validate(trans);
                         
                         strCatalogDesc =trans.getValue("DEFCATDESC/DATA/SALESPARTCATADESC");
                     }
                     else
                     {
                        if( mgr.isEmpty(mgr.readValue("ALTERNATIVE_CUSTOMER")))
                        {
                           strAgreementId =  mgr.readValue("AGREEMENT_ID");
                           
                           if(mgr.isEmpty(strAgreementId))
                               strColListPrice = "0";
                           else
                               strColListPrice = "1";
                        }
                        else
                        {
                           strCustno = mgr.readValue("ALTERNATIVE_CUSTOMER");
                           strAgreementId = "";
                           strColListPrice = "0";
                        }
                        
                        strCustno =  mgr.readValue("CUSTOMER_NO");
                        strAgreementId = mgr.readValue("AGREEMENT_ID");
                        
                         if(mgr.isEmpty(strCustno))
                            colPriceListNo = "";
                         else
                            colPriceListNo = mgr.readValue("PRICE_LIST_NO");
                        
                         cmd = trans.addCustomCommand("PRINFO","Work_Order_Coding_API.Get_Price_Info");
                         cmd.addParameter("ITEM1_CATALOG_CONTRACT"); 
                         cmd.addReference("CATALOG_NO","DEFCATNO/DATA");
                         cmd.addParameter("CUSTOMER_NO",strCustno);
                         cmd.addParameter("AGREEMENT_ID",strAgreementId);                         
                         cmd.addParameter("PRICE_LIST_NO",colPriceListNo);
                         cmd.addParameter("QTY_TO_INVOICE");
                         cmd.addParameter("ITEM1_WO_NO");
                         cmd.addParameter("CRE_DATE");   
                        
                        trans.clear();
                        trans = mgr.validate(trans);
                     }
                }
                //Bug 83757,end
                else
                {
                    if (!mgr.isEmpty(mgr.readValue("CATALOG_NO")))
                    {
                        if (mgr.isEmpty(mgr.readValue("QTY")))
                            colAmount = 0;
                        else
                        {
                            colSalesPartCost = mgr.readNumberValue("SALESPARTCOST");
                            if (isNaN(colSalesPartCost))
                                colSalesPartCost = 0;

                            colQty = mgr.readNumberValue("QTY");
                            if (isNaN(colQty))
                                colQty = 0;

                            colAmount = colSalesPartCost * colQty;
                        }
                    }
                }
            }

            if (securityOk)
            {
                if (!mgr.isEmpty(mgr.readValue("QTY_TO_INVOICE")))
                    nBuyQtyDue = mgr.readNumberValue("QTY_TO_INVOICE");
                else
                    nBuyQtyDue = mgr.readNumberValue("QTY");

                if (isNaN(nBuyQtyDue) || nBuyQtyDue == 0)
                    nBuyQtyDue = 1;

                cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("BASE_PRICE","0");
                cmd.addParameter("SALE_PRICE","0");
                cmd.addParameter("DISCOUNT","0");
                cmd.addParameter("CURRENCY_RATE","0");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addReference("CATALOG_NO","DEFCATNO/DATA");
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
            if (isNaN(colAmount))
                colAmount = 0;
            
            trans.clear();
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

            if (!mgr.isEmpty(mgr.readValue("ROLE_CODE",sRoleCode)))
            {
                String sComment = trans.getValue("ROLEDESC/DATA/CMNT");
                colCmnt = (mgr.isEmpty(mgr.readValue("NAME",""))? "" :(mgr.readValue("NAME","") + ", "))+ mgr.readValue("ITEM1_ORG_CODE","")+ (mgr.isEmpty(sComment) ? "" :(",("+ sComment +")")); 

                if (salesSecOk)
                {
                    colCatalogNo   = trans.getValue("DEFCATNO/DATA/CATALOG_NO");
                    colCatalogDesc = trans.getValue("DEFCATDESC/DATA/SALESPARTCATALOGDESC");
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
                colCmnt        = (mgr.isEmpty(mgr.readValue("NAME",""))? "" :(mgr.readValue("NAME","") + ", "))+ mgr.readValue("ITEM1_ORG_CODE","");
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
                  (mgr.isEmpty(strDisplayDiscount)?"":strDisplayDiscount) +"^"+
                  (mgr.isEmpty(strColDiscountPrice)?"":strColDiscountPrice) +"^";

            mgr.responseWrite(txt);
        }
        else if ("CATALOG_NO".equals(val))
        {
            double nBuyQtyDue=0;
            double colListPrice=0;
            double colDiscount=0;
            double colAmountSales=0;
            double nSalesPriceAmount=0;
            double colSalesPartCost=0;
            double colAmount=0;
            double numSalesPartCost=0;
            String strColAmountSales = null;
            String strColListPrice = null;
            String strColAmount = null;
            String colPriceListNo = null;
            String strColSalesPartCost = null;
            String colCmnt = "";
            String colSalesPartDesc = null;
            boolean securityOk = false;
            boolean salesSecOk = false;

            numSalesPartCost = 0;

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
                if (!mgr.isEmpty(mgr.readValue("QTY_TO_INVOICE")))
                    nBuyQtyDue = mgr.readNumberValue("QTY_TO_INVOICE");
                else
                    nBuyQtyDue = mgr.readNumberValue("QTY");

                if (isNaN(nBuyQtyDue)  ||  nBuyQtyDue == 0)
                    nBuyQtyDue = 1;

                cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("BASE_PRICE");
                cmd.addParameter("SALE_PRICE");
                cmd.addParameter("DISCOUNT");
                cmd.addParameter("CURRENCY_RATE");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addParameter("CATALOG_NO");
                cmd.addParameter("CUSTOMER_NO");
                cmd.addParameter("AGREEMENT_ID");
                cmd.addParameter("PRICE_LIST_NO");
                cmd.addParameter("QTY1",mgr.formatNumber("QTY1",nBuyQtyDue));
            }

            cmd = trans.addCustomFunction("ROLEDESC","Role_API.Get_Description","CMNT");
            cmd.addParameter("ROLE_CODE");

            if (salesSecOk)
            {
                cmd = trans.addCustomFunction("DEFCATDESC","Sales_Part_API.Get_Catalog_Desc","SALESPARTCATALOGDESC");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addParameter("CATALOG_NO");

                cmd = trans.addCustomFunction("CST", "Work_Order_Planning_Util_Api.Get_Cost", "SALESPARTCOST" );
                cmd.addParameter("ITEM1_ORG_CODE");
                cmd.addParameter("ROLE_CODE"); 
                //Bug 83757,Start
                cmd.addParameter("ITEM1_CATALOG_CONTRACT");
                //Bug 83757,End
                cmd.addParameter("CATALOG_NO");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addParameter("EMP_NO",mgr.readValue("EMP_NO"));
                cmd.addParameter("DUMMY","TRUE");
            }
            else
            {
                cmd = trans.addCustomFunction("CST", "Work_Order_Planning_Util_Api.Get_Cost", "SALESPARTCOST" );
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
                colListPrice    = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE"); 
                if (isNaN(colListPrice))
                    colListPrice = 0;

                colPriceListNo  = trans.getValue("PRICEINFO/DATA/PRICE_LIST_NO");

                colDiscount     = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");
                if (isNaN(colDiscount))
                    colDiscount = 0;

                double qty1 = trans.getNumberValue("PRICEINFO/DATA/QTY1");
                if (isNaN(qty1))
                    qty1 = 0;

                colAmountSales = colListPrice * qty1;

                if (!mgr.isEmpty(mgr.readValue("QTY_TO_INVOICE")))
                {
                    double qtyToInvoice = mgr.readNumberValue("QTY_TO_INVOICE");
                    if (isNaN(qtyToInvoice))
                        qtyToInvoice = 0;

                    nSalesPriceAmount = colListPrice * qtyToInvoice;
                    colAmountSales    = nSalesPriceAmount - (colDiscount/100 * nSalesPriceAmount);         
                }
                else
                {
                    if (!mgr.isEmpty(mgr.readValue("QTY")))
                    {
                        double qty = mgr.readNumberValue("QTY");
                        if (isNaN(qty))
                            qty = 0;

                        nSalesPriceAmount = colListPrice * qty;
                        colAmountSales    = nSalesPriceAmount - (colDiscount/100 * nSalesPriceAmount);
                    }
                    else
                        colAmountSales    = 0;
                }
            }
            else
            {
                colListPrice = mgr.readNumberValue("LIST_PRICE");
                colAmountSales = mgr.readNumberValue("AMOUNTSALES");

                colPriceListNo = mgr.readValue("PRICE_LIST_NO","");
            }

            if (salesSecOk)
                colSalesPartDesc = trans.getValue("DEFCATDESC/DATA/SALESPARTCATALOGDESC");
            else
                colSalesPartDesc = mgr.readValue("SALESPARTCATALOGDESC","");

            String sComments = trans.getValue("ROLEDESC/DATA/CMNT");
            colCmnt = (mgr.isEmpty(mgr.readValue("NAME",""))? "" : (mgr.readValue("NAME","") +"," ))+ mgr.readValue("ITEM1_ORG_CODE","") + (mgr.isEmpty(sComments) ?"" :(",("+ sComments +")"))+ (mgr.isEmpty(colSalesPartDesc) ?"" :(", "+ colSalesPartDesc));

            colSalesPartCost = 0;

            colSalesPartCost = trans.getBuffer("CST/DATA").getNumberValue("SALESPARTCOST");
            numSalesPartCost = trans.getNumberValue("CST/DATA/SALESPARTCOST");

            if (isNaN(numSalesPartCost))
                numSalesPartCost = 0;

            if (mgr.isEmpty(mgr.readValue("QTY")))
                colAmount = 0;
            else
                colAmount = numSalesPartCost * mgr.readNumberValue("QTY");    

            if (!isNaN(colListPrice))
                strColListPrice = mgr.getASPField("LIST_PRICE").formatNumber(colListPrice);
            if (!isNaN(colAmount))
                strColAmount = mgr.getASPField("AMOUNT").formatNumber(colAmount);
            if (!isNaN(colAmountSales))
                strColAmountSales = mgr.getASPField("AMOUNTSALES").formatNumber(colAmountSales);
            if (!isNaN(colSalesPartCost))
                strColSalesPartCost = mgr.getASPField("SALESPARTCOST").formatNumber(colSalesPartCost);

            txt = (mgr.isEmpty(strColListPrice)?"":strColListPrice) +"^"+ (mgr.isEmpty(strColAmount)?"":strColAmount) +"^"+ (mgr.isEmpty(strColAmountSales)?"":strColAmountSales) +"^"+ (mgr.isEmpty(colPriceListNo)?"":colPriceListNo) +"^"+ (mgr.isEmpty(colCmnt)?"":colCmnt) +"^"+ (mgr.isEmpty(colSalesPartDesc)?"":colSalesPartDesc) +"^"+ (mgr.isEmpty(strColSalesPartCost)?"":strColSalesPartCost) +"^";

            mgr.responseWrite(txt);
        }
        else if ("ITEM1_ORG_CODE".equals(val))
        {
            double colAmount = 0;
            String strColAmount = null;
            String colOrgCode;
            String colCmnt = "";
            String colSalesPart = null;
            String colLineDescription = null;
            boolean securityOk = false;
            double colSalesPartCost = 0;
            String strCost = null;

            String reqstr = null;
            int startpos = 0;
            int endpos = 0;
            int i = 0;
            String ar[] = new String[6];

            String sOrgCode = "";
            String sOrgContract = "";
            String new_org_code = mgr.readValue("ITEM1_ORG_CODE","");

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

           //Bug 83757,Start

            if ((securityOk &&   mgr.isEmpty(mgr.readValue("ROLE_CODE"))) ||  (!mgr.isEmpty(mgr.readValue("ROLE_CODE")) && mgr.isEmpty(mgr.readValue("ITEM1_CATALOG_CONTRACT"))))
            {
                cmd = trans.addCustomFunction("DEFCAT","Organization_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
                cmd.addParameter("ITEM1_CONTRACT",sOrgContract);
                cmd.addParameter("ITEM1_ORG_CODE",sOrgCode);

                cmd = trans.addCustomFunction("DEFCATDESC","Organization_Sales_Part_API.Get_Def_Contract","SALESPARTCATALOGDESC");
                cmd.addParameter("ITEM1_CONTRACT");
                cmd.addParameter("ITEM1_ORG_CODE",sOrgCode);
                
                trans.clear();
                trans = mgr.perform(trans);
                
                String sCatNo = trans.getValue("DEFCAT/DATA/CATALOG_NO");               
                
                if( !mgr.isEmpty(sCatNo))
                {
                                            
                      cmd = trans.addCustomFunction("GETCONTRACT"," Active_Work_Order_API.Get_Contract","CONTRACT_NO");
                      cmd.addParameter("ITEM1_WO_NO");                      
                      
                      cmd = trans.addCustomFunction("CHKEXIST","Sales_Part_API.Check_Exist","CHECK_EXIST");
                      cmd.addParameter("ITEM1_CONTRACT");
                      cmd.addParameter("CATALOG_NO");
                      
                      trans.clear();
                      trans = mgr.perform(trans);
                      
                      String checkExist = trans.getValue("CHKEXIST/DATA/CHECK_EXIST");
                      
                      if("1".equals(checkExist))
                      {
                         cmd = trans.addCustomFunction("CATINFO","ACTIVE_WORK_ORDER_API.Get_Sales_Part_Defaults","CAT_INFO");
                         cmd.addParameter("CATALOG_NO");
                         cmd.addParameter("ITEM1_CATALOG_CONTRACT");                       
                         
                         cmd = trans.addCustomFunction("CUSTONO","Active_Work_Order_API.Get_Customer_No","CUSTOMER_NO");
                         cmd.addParameter("ITEM1_WO_NO");
                         
                         cmd = trans.addCustomFunction("CURRCO","ACTIVE_SEPARATE_API.Get_Currency_Code","CURRENCEY_CODE");
                         cmd.addParameter("ITEM1_WO_NO");
                         
                        cmd = trans.addCustomFunction("PRILST","Customer_Order_Pricing_Api.Get_Valid_Price_List","ITEM5_PRICE_LIST_NO");
                        cmd.addParameter("ITEM1_CATALOG_CONTRACT");
                        cmd.addParameter("CATALOG_NO");
                        cmd.addReference("CUSTOMER_NO","CUSTONO/DATA");
                        cmd.addReference("CURRENCEY_CODE","CURRCO/DATA");
                        
                        cmd = trans.addCustomFunction("GETCONTRACT1"," Active_Work_Order_API.Get_Contract","CONTRACT_NO1");
                        cmd.addParameter("ITEM1_WO_NO");                      
                      
                        cmd = trans.addCustomFunction("CHKEXIST1","Sales_Part_API.Check_Exist","CHECK_EXIST1");
                        cmd.addParameter("ITEM1_CONTRACT");
                        cmd.addParameter("CATALOG_NO");
                        
                        trans.clear();
                        trans = mgr.perform(trans);
                        
                        String checkExist1 = trans.getValue("CHKEXIST1/DATA/CHECK_EXIST1");
                      
                      if("1".equals(checkExist1))
                      {
                           trans.clear();
                         cmd = trans.addCustomFunction("CATDESC1","Sales_Part_API.Get_Catalog_Desc","CAT_DESC");
                         cmd.addParameter("ITEM1_CATALOG_CONTRACT");
                         cmd.addParameter("CATALOG_NO");
                         
                         trans = mgr.perform(trans);
                         
                         String catDesc = trans.getValue("CATDESC1/DATA/CAT_DESC");              
                          
                      }
                         
                         if (isModuleInst1("ORDER"))
                         {
                               cmd = trans.addCustomFunction("GETSALESPRICEGRPID","SALES_PART_API.GET_SALES_PRICE_GROUP_ID","SALES_PRICE_GROUP_ID");
                               cmd.addParameter("ITEM1_CATALOG_CONTRACT");
                               cmd.addReference("CATALOG_NO","DEFCAT/DATA/CATALOG_NO");
                         }
                        
                        cmd = trans.addCustomFunction("GETCURRENCY","Active_Work_Order_API.Get_Currency_From_Wo","GET_CURR");
                        cmd.addParameter("ITEM1_WO_NO");
                      }
                      
                         cmd = trans.addCustomFunction("CATDESC","Sales_Part_API.Get_Catalog_Desc","CAT_DESC");
                         cmd.addParameter("ITEM1_CATALOG_CONTRACT");
                         cmd.addParameter("CATALOG_NO");
                }
            }
            //Bug 83757,end
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
            trans.clear();
            cmd = trans.addCustomFunction("GETCOST","Work_Order_Planning_Util_Api.Get_Cost","COST3");
            cmd.addParameter("ITEM1_ORG_CODE",sOrgCode);
            cmd.addParameter("ROLE_CODE");
            //Bug 83757,Start
            cmd.addParameter("ITEM1_CATALOG_CONTRACT");
            //Bug 83757,End
            cmd.addParameter("CATALOG_NO");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("EMP_NO",mgr.readValue("EMP_NO"));

            trans = mgr.validate(trans);

            if (securityOk &&   mgr.isEmpty(mgr.readValue("ROLE_CODE")))
            {
                colSalesPart       = trans.getValue("DEFCAT/DATA/CATALOG_NO");
                colLineDescription = trans.getValue("DEFCATDESC/DATA/SALESPARTCATALOGDESC");
                colOrgCode         = mgr.readValue("ITEM1_ORG_CODE","");
                colCmnt            = mgr.readValue("CMNT","");

                colAmount          = mgr.readNumberValue("AMOUNT");
            }
            else
            {
                colSalesPart       = mgr.readValue("CATALOG_NO","");
                colLineDescription = mgr.readValue("SALESPARTCATALOGDESC","");

                if (!mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE")) && !mgr.isEmpty(mgr.readValue("QTY")) && !mgr.isEmpty(mgr.readValue("AMOUNT")) && !mgr.isEmpty(mgr.readValue("ROLE_CODE")))
                    colAmount = trans.getBuffer("CALAMOUNT/DATA").getNumberValue("AMOUNT");

                else if (!mgr.isEmpty(mgr.readValue("QTY")))
                {
                    if (mgr.isEmpty(mgr.readValue("AMOUNT")))
                        colAmount  = mgr.readNumberValue("AMOUNT");
                    else
                        colAmount  = mgr.readNumberValue("AMOUNT");
                }

                else if (mgr.isEmpty(mgr.readValue("QTY")))
                    colAmount = 0;

                if (!mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE")))
                {
                    String sEmpName = mgr.readValue("NAME","");
                    colOrgCode = mgr.readValue("ITEM1_ORG_CODE","");
                    colCmnt    = (mgr.isEmpty(sEmpName)? "" : sEmpName)+ ", "+ mgr.readValue("ITEM1_ORG_CODE")+", ("+ trans.getValue("ROLEDESC/DATA/CMNT")+")";
                }
                else
                {
                    String sEmpName = mgr.readValue("NAME","");
                    colOrgCode = trans.getValue("GETORGCODE/DATA/ORCODE");                
                    colCmnt    = (mgr.isEmpty(sEmpName)? "" : sEmpName)+ ", " + colOrgCode;
                }    
            }

            colSalesPartCost = trans.getNumberValue("GETCOST/DATA/COST3");

            if (!isNaN(colAmount))
                strColAmount = mgr.getASPField("AMOUNT").formatNumber(colAmount);

            if (!isNaN(colSalesPartCost))
                strCost = mgr.getASPField("AMOUNT").formatNumber(colSalesPartCost);


            txt = (mgr.isEmpty(sOrgCode)?"":sOrgCode) +"^"+
                  (mgr.isEmpty(sOrgContract)?"":sOrgContract) +"^"+
                  (mgr.isEmpty(strColAmount)?"":strColAmount) +"^"+ 
                  (mgr.isEmpty(colCmnt)?"":colCmnt) +"^"+ 
                  (mgr.isEmpty(colSalesPart)?"":colSalesPart) +"^"+ 
                  (mgr.isEmpty(colLineDescription)?"":colLineDescription) +"^"+
                  (mgr.isEmpty(strCost)?"":strCost) +"^" ;

            mgr.responseWrite(txt);
        }
        else if ("ITEM1_CONTRACT".equals(val))
        {
            cmd = trans.addCustomFunction("COMP","Site_API.Get_Company","COMPANY_VAR");
            cmd.addParameter("ITEM1_CONTRACT");

            trans = mgr.validate(trans);

            String strCompany = trans.getValue("COMP/DATA/COMPANY_VAR");
            txt = (mgr.isEmpty(strCompany)?"":strCompany)+ "^";
            mgr.responseWrite(txt);
        }
        else if ("PRICE_LIST_NO".equals(val))
        {
            double nBuyQtyDue=0;
            double colListPrice=0;
            double colDiscount=0;
            double colAmountSales=0;
            double nSalesPriceAmount=0;
            double listPrice=0;
            String strColAmountSales = null;
            String strColListPrice = null;
            String colPriceListNo;
            String colcAgreement;
            boolean securityOk = false;
            boolean salesSecOk = false;

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
                if (!mgr.isEmpty(mgr.readValue("QTY_TO_INVOICE")))
                    nBuyQtyDue = mgr.readNumberValue("QTY_TO_INVOICE");
                else
                    nBuyQtyDue = mgr.readNumberValue("QTY");

                if (isNaN(nBuyQtyDue)  ||  nBuyQtyDue == 0)
                    nBuyQtyDue = 1;

                cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("BASE_PRICE");
                cmd.addParameter("SALE_PRICE");
                cmd.addParameter("DISCOUNT");
                cmd.addParameter("CURRENCY_RATE");
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
                colListPrice    = trans.getNumberValue("PRICEINFO/DATA/BASE_PRICE"); 
                if (isNaN(colListPrice))
                    colListPrice = 0;

                colPriceListNo  = trans.getValue("PRICEINFO/DATA/PRICE_LIST_NO");

                colDiscount     = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");
                if (isNaN(colDiscount))
                    colDiscount = 0;

                double qty1 = trans.getNumberValue("PRICEINFO/DATA/QTY1");
                if (isNaN(qty1))
                    qty1 = 0;

                colAmountSales = colListPrice * qty1;

                if (!mgr.isEmpty(mgr.readValue("QTY_TO_INVOICE")))
                {
                    double qtyToInvoice = mgr.readNumberValue("QTY_TO_INVOICE");
                    if (isNaN(qtyToInvoice))
                        qtyToInvoice = 0;

                    nSalesPriceAmount = colListPrice * qtyToInvoice;
                    colAmountSales    = nSalesPriceAmount - (colDiscount/100 * nSalesPriceAmount);         
                }
                else
                {
                    if (!mgr.isEmpty(mgr.readValue("QTY")))
                    {
                        double qty = mgr.readNumberValue("QTY");
                        if (isNaN(qty))
                            qty = 0;

                        nSalesPriceAmount = colListPrice * qty;
                        colAmountSales    = nSalesPriceAmount - (colDiscount/100 * nSalesPriceAmount);
                    }
                    else
                        colAmountSales    = 0;
                }
            }
            else
            {
                colListPrice = mgr.readNumberValue("LIST_PRICE");
                colAmountSales = mgr.readNumberValue("AMOUNTSALES");

                colPriceListNo = mgr.readValue("PRICE_LIST_NO","");
            }

            colcAgreement = "1";

            listPrice = mgr.readNumberValue("LIST_PRICE");

            if (!isNaN(colListPrice))
                strColListPrice = mgr.getASPField("LIST_PRICE").formatNumber(colListPrice);
            if (!isNaN(colAmountSales))
                strColAmountSales = mgr.getASPField("AMOUNTSALES").formatNumber(colAmountSales);

            txt = (mgr.isEmpty(strColListPrice)?"":strColListPrice) +"^"+ (mgr.isEmpty(strColAmountSales)?"":strColAmountSales) +"^"+ (mgr.isEmpty(colPriceListNo)?"":colPriceListNo) +"^"+ colcAgreement +"^";

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

            String dueDate = trans.getFieldValue("PLANSDATE/DATA/DUE_DATE");
            String nPreAccId = trans.getValue("PREACCID/DATA/NPREACCOUNTINGID");
            String item3Contract = trans.getBuffer("ITEM3CONTRA/DATA").getFieldValue("ITEM3_CONTRACT");
            String company = trans.getBuffer("COMP/DATA").getFieldValue("ITEM3_COMPANY");
            String mchCode = trans.getValue("MCHCOD/DATA/MCHCODE");
            String item3Desc = trans.getValue("ITEM3DESC/DATA/ITEM3DESCRIPTION");

            txt = (mgr.isEmpty(dueDate)?"":dueDate) + "^" + (mgr.isEmpty(nPreAccId)?"":nPreAccId) + "^" + (mgr.isEmpty(item3Contract)?"":item3Contract) + "^" + (mgr.isEmpty(company)?"":company) + "^" + (mgr.isEmpty(mchCode)?"":mchCode) + "^" + (mgr.isEmpty(item3Desc)?"":item3Desc) + "^";

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

            txt = (mgr.isEmpty(signId)?"":signId) + "^" + (mgr.isEmpty(signName)?"":signName) + "^";

            mgr.responseWrite(txt);
        }
        else if ("INT_DESTINATION_ID".equals(val))
        {
            cmd = trans.addCustomFunction("INTDESTDESC","Internal_Destination_API.Get_Description","INT_DESTINATION_DESC");
            cmd.addParameter("CONTRACT",mgr.readValue("ITEM3_CONTRACT",""));
            cmd.addParameter("INT_DESTINATION_ID");

            trans = mgr.validate(trans);

            String intDestDesc = trans.getValue("INTDESTDESC/DATA/INT_DESTINATION_DESC");

            txt = (mgr.isEmpty(intDestDesc)?"":intDestDesc) + "^";

            mgr.responseWrite(txt);
        }
        else if ("PART_NO".equals(val))
        {
            String cataNo =""; 
            String cataDesc = "";
            String salesPriceGroupId = "";
            String defCond = new String();
            String condesc = new String();
            String sDefCondiCode= "";
            String activeInd = "";
            String vendorNo = "";
            String custOwner = "";
            String partOwnership = "";
            String sOwner = mgr.readValue("OWNER");
            String ownership = "";

            cmd = trans.addCustomFunction("GETCONDCODEUSAGEDB","PART_CATALOG_API.Get_Condition_Code_Usage_Db","COND_CODE_USAGE");
            cmd.addParameter("PART_NO");

            cmd = trans.addCustomFunction("DEFCONDCODE","CONDITION_CODE_API.Get_Default_Condition_Code","ITEM5_CONDITION_CODE");
            cmd = trans.addCustomFunction("MAINTREQSUP","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","SUPPLY_CODE");

            trans = mgr.validate(trans);
            if ("ALLOW_COND_CODE".equals(trans.getValue("GETCONDCODEUSAGEDB/DATA/COND_CODE_USAGE")))
            {
                sDefCondiCode = trans.getBuffer("DEFCONDCODE/DATA").getFieldValue("ITEM5_CONDITION_CODE");
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

            if (isModuleInst1("ORDER"))
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
                cmd.addParameter("ITEM5_CONDITION_CODE",sDefCondiCode);

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
                cmd.addParameter("ITEM5_CONDITION_CODE",sDefCondiCode);
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
                cmd.addParameter("ITEM5_CONDITION_CODE",sDefCondiCode);

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
                cmd.addParameter("ITEM5_CONDITION_CODE",sDefCondiCode);
            }

            cmd = trans.addCustomFunction("UNITMES","Purchase_Part_Supplier_API.Get_Unit_Meas","UNITMEAS");
            cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
            cmd.addParameter("PART_NO");

            cmd = trans.addCustomFunction("PARTDESC","INVENTORY_PART_API.Get_Description","SPAREDESCRIPTION");
            cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
            cmd.addParameter("PART_NO");

            if (isModuleInst1("ORDER"))
            {
                cmd = trans.addCustomFunction("GETSALESPRICEGRPID","SALES_PART_API.GET_SALES_PRICE_GROUP_ID","SALES_PRICE_GROUP_ID");
                cmd.addParameter("CATALOG_CONTRACT");
                cmd.addReference("CATALOG_NO","CATANO/DATA");
            }

            cmd = trans.addCustomFunction("SUUPCODE","INVENTORY_PART_API.Get_Supply_Code ","SUPPLY_CODE");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addReference("CATALOG_NO","CATANO/DATA");

            cmd = trans.addCustomFunction("CONDALLOW","PART_CATALOG_API.Get_Condition_Code_Usage_Db","ITEM5_COND_CODE_USAGE");
            cmd.addParameter("PART_NO");

            trans = mgr.validate(trans);

            if (isModuleInst1("ORDER"))
            {
                cataNo = trans.getBuffer("CATANO/DATA").getFieldValue("ITEM5_CATALOG_NO");
                cataDesc = trans.getValue("CATADESC/DATA/ITEM5CATALOGDESC");
                salesPriceGroupId = trans.getValue("GETSALESPRICEGRPID/DATA/SALES_PRICE_GROUP_ID");
                activeInd = trans.getValue("GETACTDB/DATA/ACTIVEIND_DB");
            }
            else
            {
                cataNo = "";
                cataDesc = "";
                salesPriceGroupId = "";
                activeInd = "";
            }

            String hasStruct = trans.getValue("SPARESTRUCT/DATA/HASSPARESTRUCTURE");
            String dimQty = trans.getValue("DIMQUAL/DATA/DIMQTY");
            String typeDesi = trans.getValue("TYPEDESI/DATA/TYPEDESIGN");
            double qtyOnHand = trans.getNumberValue("INVONHAND/DATA/QTYONHAND");
            String qtyOnHand1 = mgr.getASPField("QTYONHAND").formatNumber(qtyOnHand);
            String unitMeas = trans.getValue("UNITMES/DATA/UNITMEAS");
            String partDesc = trans.getValue("PARTDESC/DATA/SPAREDESCRIPTION");
            String suppCode = trans.getValue("SUUPCODE/DATA/SUPPLY_CODE");
            String condco = trans.getValue("CONDALLOW/DATA/ITEM5_COND_CODE_USAGE");
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
            double nExist = 0 ;
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

            cmd = trans.addCustomCommand("MATREQLINE","Material_Requis_Line_API.CHECK_PART_NO__");
            cmd.addParameter("SPAREDESCRIPTION",partDesc);
            cmd.addParameter("SUPPLY_CODE",suppCode);
            cmd.addParameter("UNITMEAS",unitMeas);
            cmd.addParameter("PART_NO",mgr.readValue("PART_NO",""));
            cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT",""));

            mgr.perform(trans); 

            txt = (mgr.isEmpty(cataNo) ? "" : cataNo) + "^" + 
                  (mgr.isEmpty(cataDesc) ? "" : cataDesc) + "^" +
                  (mgr.isEmpty(hasStruct) ? "" : hasStruct) + "^" + 
                  (mgr.isEmpty(dimQty) ? "" : dimQty) + "^" + 
                  (mgr.isEmpty(typeDesi) ? "" : typeDesi) + "^" + 
                  (mgr.isEmpty(qtyOnHand1) ? "" : qtyOnHand1) + "^" + 
                  (mgr.isEmpty(unitMeas) ? "" : unitMeas) + "^" + 
                  (mgr.isEmpty(partDesc) ? "" : partDesc) + "^" + 
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
                cmd.addParameter("ITEM5_CONDITION_CODE");

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
                cmd.addParameter("ITEM5_CONDITION_CODE");
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
                cmd.addParameter("ITEM5_CONDITION_CODE");

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
                cmd.addParameter("ITEM5_CONDITION_CODE");
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
            double nAmountCost =0;

            String spareId = mgr.readValue("PART_NO","");

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

            double nSalesPriceAmount = 0;

            double nDiscountedPrice = mgr.readNumberValue("ITEMDISCOUNTEDPRICE");
            if (isNaN(nDiscountedPrice))
                nDiscountedPrice = 0;

            trans.clear();
            cmd = trans.addCustomFunction("GETOBJSUPL","MAINT_MATERIAL_REQ_LINE_API.Is_Obj_Supp_Loaned","OBJ_LOAN");
            cmd.addParameter("ITEM_WO_NO");
            
            trans = mgr.validate(trans);
            
            String sObjSup = trans.getValue("GETOBJSUPL/DATA/OBJ_LOAN");
            
            trans.clear();

            if (!mgr.isEmpty(spareId))
            {
               /* cmd = trans.addCustomFunction("GETINVVAL","Inventory_Part_API.Get_Inventory_Value_By_Method","ITEM_COST");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("PART_NO");

                trans = mgr.validate(trans);

                nCost = trans.getBuffer("GETINVVAL/DATA").getNumberValue("ITEM_COST");
                if (isNaN(nCost))
                    nCost = 0;*/

                if (checksec("Active_Separate_API.Get_Inventory_Value",1,isSecure))
                {
                    cmd = trans.addCustomCommand("GETINVVAL","Active_Separate_API.Get_Inventory_Value");
                    cmd.addParameter("ITEM_COST");
                    cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));
                    cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
                    cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
                    cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
                    cmd.addParameter("ITEM5_CONDITION_CODE",mgr.readValue("ITEM5_CONDITION_CODE"));

                    trans = mgr.validate(trans);

                    nCost = trans.getNumberValue("GETINVVAL/DATA/ITEM_COST");
                    
                    if (isNaN(nCost))
                        nCost=0;  
                }
                else
                    nCost=0;
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

            txt = (mgr.isEmpty(strCost) ? "" : strCost) + "^" + (mgr.isEmpty(strAmountCost) ? "" : strAmountCost) + "^" + (mgr.isEmpty(nPriceListNo) ? "" : nPriceListNo) + "^" + (mgr.isEmpty(strDiscount) ? "" : strDiscount) + "^" + (mgr.isEmpty(strListPrice) ? "" : strListPrice) + "^" + (mgr.isEmpty(strSalesPriceAmount) ? "" : strSalesPriceAmount) + "^"+(mgr.isEmpty(strDiscountedPrice) ? "" : strDiscountedPrice) + "^";

            mgr.responseWrite(txt); 
        }
        else if ("ITEM5_CATALOG_NO".equals(val))
        {
            String spareId = mgr.readValue("PART_NO","");

            trans.clear();
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
            /*if (!mgr.isEmpty(spareId))
            {
                cmd = trans.addCustomFunction("GETINVVAL"," Inventory_Part_API.Get_Inventory_Value_By_Method","ITEM_COST");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("PART_NO");
            }  */

            if (!mgr.isEmpty(spareId) && checksec("Active_Separate_API.Get_Inventory_Value",4,isSecure))
            {
                cmd = trans.addCustomCommand("GETINVVAL","Active_Separate_API.Get_Inventory_Value");
                cmd.addParameter("ITEM_COST");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("PART_NO");
                cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
                cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
                cmd.addParameter("ITEM5_CONDITION_CODE",mgr.readValue("ITEM5_CONDITION_CODE")); 
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
            double nCost = mgr.readNumberValue("ITEM_COST");
            if (isNaN(nCost))
                nCost = 0;

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
            String salesPriceGroupId = "";
            String cataDesc = "";
            if (mgr.isModuleInstalled("ORDER")){
                 salesPriceGroupId = trans.getValue("GETSALESPRICEGRPID/DATA/SALES_PRICE_GROUP_ID");
                 cataDesc = trans.getValue("CATADESC/DATA/ITEM5CATALOGDESC");
            }
            //Bug 69392, end
            String strListPrice = mgr.getASPField("ITEM5_LIST_PRICE").formatNumber(nListPrice);
            String strCost = mgr.getASPField("ITEM_COST").formatNumber(nCost);
            String strCostAmt = mgr.getASPField("AMOUNTCOST").formatNumber(nCostAmt);
            String strDiscount = mgr.getASPField("ITEM_DISCOUNT").formatNumber(nDiscount);
            String strSalesPriceAmount = mgr.getASPField("ITEM5SALESPRICEAMOUNT").formatNumber(nSalesPriceAmount);
            String strnDiscountedPriceAmt = mgr.formatNumber("ITEMDISCOUNTEDPRICE",nDiscountedPriceAmt);

            txt = (mgr.isEmpty(strListPrice) ? "" : strListPrice) + "^" + (mgr.isEmpty(strCost) ? "" : strCost) + "^" + (mgr.isEmpty(strCostAmt) ? "" : strCostAmt) + "^" + (mgr.isEmpty(cataDesc) ? "" : cataDesc) + "^" + (mgr.isEmpty(strDiscount) ? "" : strDiscount) + "^"+ (mgr.isEmpty(strSalesPriceAmount) ? "" : strSalesPriceAmount) + "^"+(mgr.isEmpty(strnDiscountedPriceAmt) ? "" : strnDiscountedPriceAmt) + "^"+ (mgr.isEmpty(nPriceListNo) ? "" : nPriceListNo) + "^" + (mgr.isEmpty(salesPriceGroupId) ? "" : salesPriceGroupId) + "^";

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
                cmd.addParameter("PART_NO");

                trans = mgr.validate(trans);

                nCost = trans.getBuffer("GETINVAL/DATA").getNumberValue("ITEM_COST");
                if (isNaN(nCost))
                    nCost = 0;*/
                if (checksec("Active_Separate_API.Get_Inventory_Value",1,isSecure))
                {
                    cmd = trans.addCustomCommand("GETINVAL","Active_Separate_API.Get_Inventory_Value");
                    cmd.addParameter("ITEM_COST");
                    cmd.addParameter("SPARE_CONTRACT");
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
                    cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
                    cmd.addParameter("ITEM5_CONDITION_CODE",mgr.readValue("ITEM5_CONDITION_CODE"));
                    
                    trans = mgr.validate(trans);

                    nCost = trans.getBuffer("GETINVAL/DATA").getNumberValue("ITEM_COST");
                    if (isNaN(nCost))
                        nCost=0;
                }
                else
                    nCost=0;
            }

            double planQty = mgr.readNumberValue("PLAN_QTY");
            if (isNaN(planQty))
                planQty = 0;

            if (planQty == 0)
                nCostAmt = 0;
            else
                nCostAmt    = nCost * planQty;

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

            txt =  (mgr.isEmpty(strCost) ? "" : strCost) + "^" + (mgr.isEmpty(strCostAmt) ? "" : strCostAmt) + "^" + (mgr.isEmpty(strListPrice) ? "" : strListPrice) + "^"+ (mgr.isEmpty(strDiscount) ? "" : strDiscount) + "^" + (mgr.isEmpty(strSalesPriceAmount) ? "" : strSalesPriceAmount) + "^" +(mgr.isEmpty(strDiscountedPriceAmt) ? "" : strDiscountedPriceAmt) + "^";

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
            String strDiscountedPriceAmt =  null;

            nListPrice = mgr.readNumberValue("ITEM5_LIST_PRICE");
            if (isNaN(nListPrice))
                nListPrice = 0;

            planQty = mgr.readNumberValue("PLAN_QTY");
            if (isNaN(planQty))
                planQty = 0;

            nDiscount = mgr.readNumberValue("ITEM_DISCOUNT");
            if (isNaN(nDiscount))
                nDiscount = 0;

            salePriceAmt =  nListPrice * planQty;
            salePriceAmt =  salePriceAmt - (nDiscount / 100 * salePriceAmt);
            discountedPrice = nListPrice - (nDiscount/100*nListPrice); 

            if (!isNaN(salePriceAmt))
                strSalePriceAmt = mgr.getASPField("ITEM5SALESPRICEAMOUNT").formatNumber(salePriceAmt);

            if (!isNaN(discountedPrice))
                strDiscountedPriceAmt = mgr.formatNumber("ITEMDISCOUNTEDPRICE",discountedPrice);

            txt = (mgr.isEmpty(strSalePriceAmt) ? "" : strSalePriceAmt) + "^"+ (mgr.isEmpty(strDiscountedPriceAmt) ? "" : strDiscountedPriceAmt) + "^";

            mgr.responseWrite(txt);
        }
        else if ("ITEM5_CONDITION_CODE".equals(val))
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
                cmd.addParameter("ITEM5_CONDITION_CODE");

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
                cmd.addParameter("ITEM5_CONDITION_CODE");
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
                cmd.addParameter("ITEM5_CONDITION_CODE");

                //Qty Available
                cmd = trans.addCustomFunction("INVAVl","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
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
                cmd.addParameter("ITEM5_CONDITION_CODE");
            }

            cmd = trans.addCustomFunction("CONCODE","CONDITION_CODE_API.Get_Description","CONDDESC");
            cmd.addParameter("ITEM5_CONDITION_CODE");

            trans = mgr.validate(trans);
            qtyOnHand = trans.getNumberValue("INVONHAND/DATA/QTYONHAND"); 
            qtyOnHand1 = mgr.getASPField("QTYONHAND").formatNumber(qtyOnHand);
            qtyAvail = trans.getNumberValue("INVAVL/DATA/QTY_AVAILABLE");
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
                cmd.addParameter("ITEM5_CONDITION_CODE",mgr.readValue("ITEM5_CONDITION_CODE"));
    
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

            cmd = trans.addCustomFunction("REPVALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
            cmd.addParameter("PART_OWNERSHIP");

            trans = mgr.validate(trans);
            String sStandardInv = trans.getValue("MAINTREQSUP/DATA/SUPPLY_CODE");
            String sOwnershipDb = trans.getValue("REPVALIDOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");
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
                cmd.addParameter("ITEM5_CONDITION_CODE");

                //Qty ReservedAvailable
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
                cmd.addParameter("ITEM5_CONDITION_CODE");
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
                cmd.addParameter("ITEM5_CONDITION_CODE");

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
                cmd.addParameter("ITEM5_CONDITION_CODE");
            }

            trans = mgr.validate(trans);
            qtyOnHand = trans.getNumberValue("INVONHAND/DATA/QTYONHAND"); 
            qtyOnHand1 = mgr.getASPField("QTYONHAND").formatNumber(qtyOnHand);
            qtyAvail = trans.getNumberValue("INVAVL/DATA/QTY_AVAILABLE");
            qtyAvail1 = mgr.getASPField("QTY_AVAILABLE").formatNumber(qtyAvail);

            txt = (mgr.isEmpty(sOwnershipDb) ? "" : (sOwnershipDb)) + "^" +
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
            String sOwnerName="";
            String sOwnershipDb = mgr.readValue("PART_OWNERSHIP_DB");

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
                cmd.addParameter("ITEM5_CONDITION_CODE");

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
                cmd.addParameter("ITEM5_CONDITION_CODE");
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
                cmd.addParameter("ITEM5_CONDITION_CODE");

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
                cmd.addParameter("ITEM5_CONDITION_CODE");
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

            cmd = trans.addCustomFunction("REPGETOWCUST","ACTIVE_SEPARATE_API.Get_Customer_No", "WO_CUST");
            cmd.addParameter("WO_NO",mgr.readValue("ITEM_WO_NO",""));

            trans = mgr.validate(trans);
            qtyOnHand = trans.getNumberValue("INVONHAND/DATA/QTYONHAND");
            qtyOnHand1 = mgr.getASPField("QTYONHAND").formatNumber(qtyOnHand);
            qtyAvail = trans.getNumberValue("INVAVL/DATA/QTY_AVAILABLE");
            qtyAvail1 = mgr.getASPField("QTY_AVAILABLE").formatNumber(qtyAvail);
            if ("CUSTOMER OWNED".equals(sOwnershipDb))
                   sOwnerName = trans.getValue("GETOWNERNAME/DATA/OWNER_NAME");
            if ("SUPPLIER LOANED".equals(sOwnershipDb))
                   sOwnerName = trans.getValue("GETOWNERNAME1/DATA/OWNER_NAME");

            String sWoCust    = trans.getValue("REPGETOWCUST/DATA/WO_CUST");

            txt = (mgr.isEmpty(sOwnerName) ? "" : (sOwnerName)) + "^" + (mgr.isEmpty(sWoCust) ? "": (sWoCust)) + "^" + (mgr.isEmpty(qtyOnHand1) ? "": qtyOnHand1) + "^" + (mgr.isEmpty(qtyAvail1) ? "": qtyAvail1) + "^" ;    		       
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
            cmd.addParameter("ITEM7_WO_NO");
            cmd.addParameter("JOB_ID"); 

            cmd = trans.addCustomFunction("GETAGR", "Active_Separate_API.Get_Agreement_Id", "S_AGREEMENT_ID");
            cmd.addParameter("ITEM7_WO_NO");

            trans = mgr.validate(trans);

            nJobExist = new Double(trans.getNumberValue("GETJOBEXIST/DATA/N_JOB_EXIST")).intValue();
            sAgreementId = trans.getValue("GETAGR/DATA/S_AGREEMENT_ID");

            if (nJobExist == 1)
            {
                trans.clear();

                cmd = trans.addCustomFunction("GETSTDJOBEXIST", "Work_Order_Job_API.Std_Job_Exist", "S_STD_JOB_EXIST");
                cmd.addParameter("ITEM7_WO_NO");
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
                cmd.addParameter("ITEM7_WO_NO");
                cmd.addParameter("JOB_ID"); 

                cmd = trans.addCustomFunction("GETQTY", "Work_Order_Job_API.Get_Qty", "N_QTY");
                cmd.addParameter("ITEM7_WO_NO");
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
                cmd.addParameter("COMPANY",mgr.readValue("ITEM7_COMPANY"));
                cmd.addParameter("SIGNATURE",new_sign);
                trans = mgr.perform(trans);

                sign = new_sign;
                emp_id = trans.getValue("EMP/DATA/EMPLOYEE_ID");

            }

            txt = (mgr.isEmpty(emp_id)?"":emp_id) + "^"+(mgr.isEmpty(sign)?"":sign) + "^";
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
//-----------------------------  CUSTOM FUNCTIONS  ----------------------------
//-----------------------------------------------------------------------------

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

//----------------- GET VLAUES FOR BLOCK 4 ------------------------------------

    public void fillRows( int headrowno) 
    {
        double actCost;
        double planCost;
        double budgCost;

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

                itemset4.next();
            }
            trans = mgr.perform(trans);
            itemset4.first();

            for (int i=0; i<=n; ++i)
            {

                budgCost = itemset4.getRow().getNumberValue("BUDGET_COST");
                budgCost = (isNaN(budgCost)?0:budgCost);

                planCost = itemset4.getRow().getNumberValue("NPLANNEDCOST");
                planCost = (isNaN(planCost)?0:planCost);

                actCost = trans.getNumberValue("ACTCOST"+i+"/DATA/ACTUAL_COST");
                actCost = (isNaN(actCost)?0:actCost);

                ASPBuffer buf = itemset4.getRow();

                buf.setNumberValue("ACTUAL_COST",actCost);

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

        for (int i=1; i<=n; i++)
        {
            budCost = itemset4.getRow().getNumberValue("BUDGET_COST");
            budCost = (isNaN(budCost)?0:budCost);

            plannCost = itemset4.getRow().getNumberValue("PLANNED_COST");
            plannCost = (isNaN(plannCost)?0:plannCost);

            actCost = itemset4.getRow().getNumberValue("ACTUAL_COST");
            actCost = (isNaN(actCost)?0:actCost);

            totBudgCost = totBudgCost + budCost;
            totplannCost =  totplannCost + plannCost;
            totActCost =  totActCost + actCost;

            itemset4.next();
        }

        ASPBuffer data = trans.getBuffer("ITEM4/DATA");
        itemset4.addRow(data);

        ASPBuffer buf = itemset4.getRow();

        buf.setValue("WORK_ORDER_COST_TYPE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSUMMARY: Summary"));  
        buf.setNumberValue("BUDGET_COST",totBudgCost);
        buf.setNumberValue("PLANNED_COST",totplannCost);
        buf.setNumberValue("ACTUAL_COST",totActCost);
        buf.setValue("EDITABLE","FALSE");

        itemset4.setRow(buf);
        itemset4.goTo(item4CurrRow);
    }

//-----------------------------------------------------------------------------
//-------------------------- HEAD BLOCK ---------------------------------------

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

        q.includeMeta("ALL");

        itemset4.clear();

        mgr.submit(trans);

        eval(headset.syncItemSets());

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERNODATA: No data found."));
            headset.clear();
        }
        setStringLables();

        if (headset.countRows() == 1)
        {
            okFindITEM1();
            okFindITEM3();
            okFindITEM4();
            okFindITEM6();
            okFindITEM7();
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
                itemset1.setValue("PLAN_LINE_NO",sent_planLineNo);
                 //Bug 83757,start
                itemset1.setValue("EMP_NO",sent_signId); 
                 //Bug 83757,End
                itemset1.setValue("NAME",empName);
                //Bug 83757,start
                itemset1.setValue("CATALOG_CONTRACT",sent_site); 
                //Bug 83757,End   
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
                itemset1.setValue("CONTRACT",sent_site);
                itemset1.setValue("CATALOG_NO",sent_salesPartNo); 
            }
        }
    }


    public void okFind()
    {
        ASPManager mgr = getASPManager();
        ASPBuffer retBuffer;
        String  curr_row_exists = "FALSE";

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

        itemset4.clear();

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
            mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERNODATA: No data found."));
            headset.clear();
            headlay.setLayoutMode(               headlay.MULTIROW_LAYOUT);
        }
        setStringLables();

        if (headset.countRows() == 1)
        {
            okFindITEM1();
            okFindITEM3();
            okFindITEM4();
            okFindITEM6();
            okFindITEM7();
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            if ("1".equals(headset.getRow().getValue("EXCEPTION_EXISTS")))
                bException = true;
            else
                bException = false;
        }

        qrystr = mgr.createSearchURL(headblk);

        //Set qrystr if page is opened from a page other than navigator...
        if (mgr.dataTransfered() && headset.countRows()>0)
        {
            for (int i=0;i<headset.countRows();i++)
            {
                headset.goTo(i);
                if (i == 0)
                    qrystr = qrystr+"&WO_NO="+headset.getRow().getValue("WO_NO");
                else
                    qrystr = qrystr+";"+headset.getRow().getValue("WO_NO");
            }
        }

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
        data.setNumberValue("FAULT_REP_FLAG",0);

        headset.addRow(data);
    }

//----------------------- BLOCK 1 ---------------------------------------------

    public void okFindITEM1()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();

        ASPQuery q = trans.addQuery(itemblk1);
        q.addWhereCondition("WO_NO = ? AND WORK_ORDER_COST_TYPE =  Work_Order_Cost_Type_API.Get_Client_Value(0) AND WORK_ORDER_ACCOUNT_TYPE =  Work_Order_Account_Type_API.Get_Client_Value(0) ");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

        q.includeMeta("ALL");     

        mgr.querySubmit(trans,itemblk1);

        if (mgr.commandBarActivated())
        {
            if (itemset1.countRows() == 0 && "ITEM1.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERNODATA: No data found."));
                itemset1.clear();
            }
        }

        headset.goTo(headrowno);
    }


    public void newRowITEM1()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addCustomFunction("ORGCO", "Active_Work_Order_API.Get_Org_Code", "DEPART" );
        cmd.addParameter("ITEM1_WO_NO",headset.getValue("WO_NO"));

        cmd = trans.addCustomFunction("CON", "Active_Work_Order_API.Get_Contract", "CONTRACT_VAR" );
        cmd.addParameter("ITEM1_CONTRACT",headset.getValue("WO_NO"));

        cmd = trans.addCustomFunction("CST", "Work_Order_Planning_Util_Api.Get_Cost", "SALESPARTCOST" );
        cmd.addReference("DEPART","ORGCO/DATA");
        cmd.addParameter("DUMMY","");
        cmd.addParameter("DUMMY","");
        cmd.addParameter("DUMMY","");
        cmd.addReference("CONTRACT_VAR","CON/DATA");
        cmd.addParameter("DUMMY","");
        cmd.addParameter("DUMMY","TRUE");

        cmd = trans.addCustomFunction("SP21","Work_Order_Cost_Type_Api.Get_Client_Value(0)","CLIENTVAL3");
        cmd = trans.addCustomFunction("SP22","Work_Order_Account_Type_API.Get_Client_Value(0)","CLIENTVAL4");

        cmd = trans.addEmptyCommand("ITEM1","WORK_ORDER_CODING_API.New__",itemblk1);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);

        String strOrgCode = trans.getValue("ORGCO/DATA/DEPART");
        String strSite = trans.getValue("CON/DATA/CONTRACT_VAR");
        String strWorkOrdCost = trans.getValue("SP21/DATA/CLIENTVAL3");
        String strWorkAccount = trans.getValue("SP22/DATA/CLIENTVAL4");
        String strComment= strOrgCode;
        double cost = trans.getNumberValue("CST/DATA/SALESPARTCOST");

        ASPBuffer data = trans.getBuffer("ITEM1/DATA");
        data.setFieldItem("ITEM1_WO_NO",headset.getValue("WO_NO"));
        data.setFieldItem("ITEM1_CONTRACT",strSite );
        data.setFieldItem("ITEM1_COMPANY",headset.getValue("COMPANY"));
        data.setFieldItem("ITEM1_ORG_CODE",strOrgCode );
        data.setFieldItem("WORK_ORDER_COST_TYPE",strWorkOrdCost );
        data.setFieldItem("WORK_ORDER_ACCOUNT_TYPE",strWorkAccount );
        data.setFieldItem("CMNT",strComment);
        data.setNumberValue("SALESPARTCOST",cost);
	//Bug 85045, Start
        data.setFieldItem("CATALOG_CONTRACT",strSite);
	//Bug 85045, End

        itemset1.addRow(data);
    }


    public void countFindITEM1()
    {
        ASPManager mgr = getASPManager();

        headrowno = headset.getCurrentRowNo();
        ASPQuery q = trans.addQuery(itemblk1);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ? AND WORK_ORDER_COST_TYPE =  Work_Order_Cost_Type_API.Get_Client_Value(0) AND WORK_ORDER_ACCOUNT_TYPE =  Work_Order_Account_Type_API.Get_Client_Value(0) ");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        q.addWhereCondition("WORK_ORDER_COST_TYPE =  Work_Order_Cost_Type_API.Get_Client_Value(0) AND WORK_ORDER_ACCOUNT_TYPE =  Work_Order_Account_Type_API.Get_Client_Value(0) ");

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
        headset.goTo(currHead);
        trans.clear();
        newRowITEM1();
    }
    
    public void deleteITEM1()
    {
        ASPManager mgr = getASPManager();        
        trans.clear();

        int currow = headset.getCurrentRowNo();

        itemset1.store();
        if (itemlay1.isMultirowLayout())
        {
            itemset1.setSelectedRowsRemoved();
            itemset1.unselectRows();
        }
        else
            itemset1.setRemoved();

        
        cmd = trans.addCustomCommand("HRAUTHCONFCHECK", "Work_Order_Coding_API.Check_T_Hr_Auth_Confirmation");
        cmd.addInParameter("COMPANY", itemset1.getRow().getValue("COMPANY")); 
        cmd.addInParameter("EMP_NO", itemset1.getRow().getValue("EMP_NO")); 
        cmd.addInParameter("CRE_DATE", itemset1.getRow().getFieldValue("CRE_DATE")); 
        cmd.addInParameter("CATALOG_NO", "T"); 
        cmd.addInParameter("CONTRACT", itemset1.getRow().getValue("CONTRACT"));
        
        mgr.submit(trans);
        
        headset.goTo(currow);

        okFindITEM4();

        headset.goTo(currow);
    }
    //Bug 81023, End

    public void saveReturnItem7()
    {
        ASPManager mgr = getASPManager();

        int currHead = headset.getCurrentRowNo();
        int currrowItem7 = itemset7.getCurrentRowNo();
        itemset7.changeRow();
        mgr.submit(trans);

        headset.goTo(currHead);
        //Bug 70147, start
        headset.refreshRow();
        //Bug 70147, end

        okFindITEM3();
        okFindITEM4();
        okFindITEM6();

        itemset7.goTo(currrowItem7);
    }
    
    //Bug 89703, Start
    public void deleteRowITEM7()
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

       if (itemlay7.isMultirowLayout())
          itemset7.goTo(itemset7.getRowSelected());
       itemset7.selectRow();

       int currrow = itemset7.getCurrentRowNo();
       ctx.setGlobal("CURRROWGLOBAL", String.valueOf(currrow));

       trans.clear();

       cmd = trans.addCustomFunction("GETJOBEXIST", "Work_Order_Job_API.Check_Exist", "N_JOB_EXIST");
       cmd.addParameter("ITEM7_WO_NO",itemset7.getRow().getValue("WO_NO"));
       cmd.addParameter("JOB_ID",itemset7.getRow().getValue("JOB_ID"));

       cmd = trans.addCustomCommand("GETDEFVALS", "Work_Order_Job_API.Check_Job_Connection");
       cmd.addParameter("N_ROLE_EXIST","0");
       cmd.addParameter("N_MAT_EXIST","0");
       cmd.addParameter("N_TOOL_EXIST","0");
       cmd.addParameter("N_PLANNING_EXIST","0");
       cmd.addParameter("N_DOC_EXIST","0");
       cmd.addParameter("S_STD_JOB_ID");
       cmd.addParameter("S_STD_JOB_CONTRACT");
       cmd.addParameter("S_STD_JOB_REVISION");
       cmd.addParameter("ITEM7_WO_NO",itemset7.getRow().getValue("WO_NO"));
       cmd.addParameter("JOB_ID",itemset7.getRow().getValue("JOB_ID"));

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
          deleteRow7("NO");
    }

    public void deleteRow7(String removeConn)
    {
       ASPManager mgr = getASPManager();

       int currHead = Integer.parseInt(ctx.findGlobal("HEADROWGLOBAL",""));
       headset.goTo(currHead);   
       int curr_row = Integer.parseInt(ctx.findGlobal("CURRROWGLOBAL",""));
       itemset7.goTo(curr_row);   

       itemset7.selectRow();

       trans.clear();

       if ("YES".equals(removeConn))
       {
          //Disconnect std job
          cmd = trans.addCustomCommand("REMOVECONN","Work_Order_Job_API.Disconnect_Std_Job");
          cmd.addParameter("ITEM7_WO_NO",itemset7.getRow().getValue("WO_NO"));
          cmd.addParameter("JOB_ID",itemset7.getRow().getValue("JOB_ID"));
          cmd.addParameter("STD_JOB_ID",itemset7.getRow().getValue("STD_JOB_ID"));
          cmd.addParameter("STD_JOB_CONTRACT",itemset7.getRow().getValue("STD_JOB_CONTRACT"));
          cmd.addParameter("STD_JOB_REVISION",itemset7.getRow().getValue("STD_JOB_REVISION")); 
          cmd.addParameter("ITEM6_QTY",itemset7.getRow().getValue("QTY")); 
       }

       cmd = trans.addCustomCommand("REMOVEJOBLINE","Work_Order_Job_API.Remove_Job_Line");
       cmd.addParameter("ITEM7_WO_NO",itemset7.getRow().getValue("WO_NO"));
       cmd.addParameter("JOB_ID",itemset7.getRow().getValue("JOB_ID")); 

       trans = mgr.perform(trans);

       trans.clear();

       clearItem4();
       okFindITEM7();
       okFindITEM4();
    }
    //Bug 89703, End

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
                mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERNODATA: No data found."));
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

    public void okFindITEM5()
    {
        ASPManager mgr = getASPManager();

        //Bug 72202, Start, Added check on row count
        if (itemset3.countRows() > 0)
        {
            trans.clear();
            int headsetRowNo = headset.getCurrentRowNo();
            int item3rowno = itemset3.getCurrentRowNo();

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

        cmd = trans.addQuery("SUYSTDATE","SELECT SYSDATE SYS_DATE FROM DUAL");

        trans = mgr.perform(trans);

        String dateRequired = trans.getBuffer("PLNDATE/DATA").getFieldValue("DUE_DATE");
        String nPreAccoId = trans.getBuffer("PREACCOID/DATA").getFieldValue("NPREACCOUNTINGID");
        String item3Cont = trans.getBuffer("CONT/DATA").getFieldValue("ITEM3_CONTRACT");
        String item3Company = trans.getBuffer("COMP/DATA").getFieldValue("ITEM3_COMPANY");
        String mchCode = trans.getValue("MCHCOD/DATA/MCHCODE");
        String mchDesc = trans.getValue("MCHDESC/DATA/ITEM3DESCRIPTION");
        String sysDate = trans.getBuffer("SUYSTDATE/DATA").getFieldValue("SYS_DATE");

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


    public void backITEM()
    {
        curentrow = itemset3.getRowSelected();
        itemset3.goTo(curentrow);
        itemlay3.setLayoutMode(itemlay3.MULTIROW_LAYOUT);
        if (updateBudg)
            okFindITEM4();
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
        data.setFieldItem("ITEM5_CONDITION_CODE",itemset.getRow().getFieldValue("ITEM5_CONDITION_CODE"));
        data.setFieldItem("CONDDESC",itemset.getRow().getValue("CONDDESC"));
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

        int currHead = headset.getCurrentRowNo();
        int currrowItem3 = itemset3.getCurrentRowNo();
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

        ASPManager mgr = getASPManager();

        trans.clear(); 
        cmd = trans.addCustomFunction("OBJLOAN","MAINT_MATERIAL_REQ_LINE_API.Is_Obj_Supp_Loaned","OBJ_LOAN");
        cmd.addParameter("WO_NO",itemset.getRow().getFieldValue("ITEM_WO_NO"));   

        trans = mgr.perform(trans);

        String sObjLoan = trans.getValue("OBJLOAN/DATA/OBJ_LOAN");

        trans.clear();   

        securityOk = "";
        secBuff = mgr.newASPTransactionBuffer();
        //secBuff.addSecurityQuery("Inventory_Part_API", "Get_Inventory_Value_By_Method");
        secBuff.addSecurityQuery("Active_Separate_API", "Get_Inventory_Value");
        secBuff = mgr.perform(secBuff);

        //if (secBuff.getSecurityInfo().itemExists("Inventory_Part_API.Get_Inventory_Value_By_Method"))
        if (secBuff.getSecurityInfo().itemExists("Active_Separate_API.Get_Inventory_Value"))
            securityOk = "TRUE";

        int n = itemset.countRows();

        if (n > 0)
        {
            itemset.first();
            for (int i=0; i<=n; ++i)
            {
                spareCont = itemset.getRow().getValue("SPARE_CONTRACT");
                partNo =itemset.getRow().getFieldValue("PART_NO");
                cataNo = itemset.getRow().getFieldValue("ITEM5_CATALOG_NO");

                nPlanQty = itemset.getRow().getNumberValue("PLAN_QTY");
                if (isNaN(nPlanQty))
                    nPlanQty = 0;

                cataCont = itemset.getRow().getFieldValue("CATALOG_CONTRACT");
                cusNo = headset.getRow().getFieldValue("CUSTOMER_NO");
                agreeId = headset.getRow().getFieldValue("AGREEMENT_ID");
                priceListNo = itemset.getRow().getFieldValue("ITEM5_PRICE_LIST_NO");
                planLineNo = itemset.getRow().getFieldValue("ITEM5_PLAN_LINE_NO");

                String serialNo = itemset.getRow().getFieldValue("SERIAL_NO");
                String conditionCode = itemset.getRow().getFieldValue("ITEM5_CONDITION_CODE");
                String configurationId = itemset.getRow().getFieldValue("CONFIGURATION_ID");
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
                cmd.addParameter("PART_NO",partNo);*/
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
                    cmd.addParameter("ITEM5_CONDITION_CODE", conditionCode);  
                }

                if ((!mgr.isEmpty(cataNo)) && ((nPlanQty != 0)))
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

            debug(itemset.getRow().getValue("SUPPLY_CODE"));

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
                //cmd.addParameter("ITEM5_CONDITION_CODE");
                cmd.addParameter("ITEM5_CONDITION_CODE",itemset.getRow().getFieldValue("ITEM5_CONDITION_CODE"));

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
                //cmd.addParameter("ITEM5_CONDITION_CODE");
                cmd.addParameter("ITEM5_CONDITION_CODE",itemset.getRow().getFieldValue("ITEM5_CONDITION_CODE"));
            }    
        }
        trans = mgr.perform(trans);

        if ( securityOk )
        {
            nQty = trans.getNumberValue("INVONHAND/DATA/QTYONHAND");
            if ( isNaN(nQty) )
                nQty = 0;
        }
        else
        {
            nQty = 0.0;
        }

        return nQty;
    }



//------------------------ BLOCK 4 --------------------------

    public void okFindITEM4()
    {
        ASPManager mgr = getASPManager();

        headrowno = headset.getCurrentRowNo();
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
        if (itemset4.countRows() > 0)
            itemset4.clear();
    }


    public void clearlastItem4()
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

        if (curentrow == 6)
            mgr.showAlert("PCMWACTIVESEPERATEREPORTINWORKORDERCANNOT: Cannot perform on selected line");
        else
        {
            item4CurrRow = curentrow;

            if (itemset4.countRows() == 6)
                sumColumns();

            itemset4.goTo(curentrow);
            itemlay4.setLayoutMode(itemlay4.EDIT_LAYOUT);
            headlay.setLayoutMode(headlay.getLayoutMode());
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
        double planCost;
        double actCost;

        ASPManager mgr = getASPManager();

        int headCurrrow = headset.getCurrentRowNo();
        trans.clear();

        itemset4.goTo(curentrow);

        String woNo = mgr.readValue("ITEM4_WO_NO","");
        String workOrderCostType = mgr.readValue("ITEM4_WORK_ORDER_COST_TYPE","");
        budgCost = mgr.readNumberValue("BUDGET_COST");
        planCost = mgr.readNumberValue("PLANNED_COST");
        actCost = mgr.readNumberValue("ACTUAL_COST");

        ASPBuffer buff = itemset4.getRow();

        buff.setValue("WO_NO",woNo);
        buff.setValue("ITEM4_WORK_ORDER_COST_TYPE",workOrderCostType);
        buff.setNumberValue("BUDGET_COST",budgCost);
        buff.setNumberValue("NPLANNEDCOST",planCost);
        buff.setNumberValue("NACTUALCOST",actCost);

        itemset4.setRow(buff);
        mgr.submit(trans);

        trans.clear();
        headset.goTo(headCurrrow);
        okFindITEM4();

        sumColumns();
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

//------------------ Block 6 (T/F) --------------------------------------------
    public void okFindITEM6()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();

        ASPQuery q = trans.addQuery(itemblk6);
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

        q.includeMeta("ALL");     

        mgr.querySubmit(trans,itemblk6);

        if (mgr.commandBarActivated())
        {
            if (itemset6.countRows() == 0 && "ITEM6.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERNODATA: No data found."));
                itemset6.clear();
            }
        }

        headset.goTo(headrowno);
    }


    public void newRowITEM6()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        cmd = trans.addEmptyCommand("ITEM6","WORK_ORDER_TOOL_FACILITY_API.New__",itemblk6);
        cmd.setParameter("ITEM6_WO_NO",headset.getRow().getValue("WO_NO"));
        cmd.setOption("ACTION","PREPARE");

        trans = mgr.perform(trans);

        ASPBuffer data = trans.getBuffer("ITEM6/DATA");

        if (!mgr.isEmpty(headset.getRow().getValue("PLAN_S_DATE")))
            data.setFieldItem("FROM_DATE_TIME",headset.getRow().getFieldValue("PLAN_S_DATE"));
        if (!mgr.isEmpty(headset.getRow().getValue("PLAN_F_DATE")))
            data.setFieldItem("TO_DATE_TIME",headset.getRow().getFieldValue("PLAN_F_DATE"));

        itemset6.addRow(data);
    }

    public void  duplicateITEM6()
    {
        ASPManager mgr = getASPManager();

        if ( itemlay6.isMultirowLayout() )
            itemset6.goTo(itemset6.getRowSelected());

        itemlay6.setLayoutMode(itemlay6.NEW_LAYOUT);

        cmd = trans.addEmptyCommand("ITEM6","WORK_ORDER_TOOL_FACILITY_API.New__",itemblk6);
        cmd.setParameter("ITEM6_WO_NO",headset.getRow().getValue("WO_NO"));
        cmd.setOption("ACTION","PREPARE");

        trans = mgr.perform(trans);

        ASPBuffer data = trans.getBuffer("ITEM6/DATA");

        data.setFieldItem("TOOL_FACILITY_ID",itemset6.getRow().getValue("TOOL_FACILITY_ID"));
        data.setFieldItem("TOOL_FACILITY_DESC",itemset6.getRow().getValue("TOOL_FACILITY_DESC"));
        data.setFieldItem("TOOL_FACILITY_TYPE",itemset6.getRow().getValue("TOOL_FACILITY_TYPE"));
        data.setFieldItem("TYPE_DESCRIPTION",itemset6.getRow().getValue("TYPE_DESCRIPTION"));
        data.setFieldItem("ITEM6_CONTRACT",itemset6.getRow().getFieldValue("ITEM6_CONTRACT"));
        data.setFieldItem("ITEM6_ORG_CODE",itemset6.getRow().getFieldValue("ITEM6_ORG_CODE"));
        data.setFieldItem("ITEM6_QTY",itemset6.getRow().getFieldValue("ITEM6_QTY"));
        data.setFieldItem("ITEM6_PLANNED_HOUR",itemset6.getRow().getFieldValue("ITEM6_PLANNED_HOUR"));
        data.setFieldItem("ITEM6_REPORTED_HOUR",itemset6.getRow().getFieldValue("ITEM6_REPORTED_HOUR"));
        data.setFieldItem("ITEM6_CRAFT_LINE_NO",itemset6.getRow().getFieldValue("ITEM6_CRAFT_LINE_NO"));
        data.setFieldItem("ITEM6_COST",itemset6.getRow().getFieldValue("ITEM6_COST"));
        data.setFieldItem("ITEM6_COST_CURRENCY",itemset6.getRow().getFieldValue("ITEM6_COST_CURRENCY"));
        data.setFieldItem("ITEM6_COST_AMOUNT",itemset6.getRow().getFieldValue("ITEM6_COST_AMOUNT"));
        data.setFieldItem("ITEM6_CATALOG_NO_CONTRACT",itemset6.getRow().getFieldValue("ITEM6_CATALOG_NO_CONTRACT"));
        data.setFieldItem("ITEM6_CATALOG_NO",itemset6.getRow().getFieldValue("ITEM6_CATALOG_NO"));
        data.setFieldItem("ITEM6_CATALOG_NO_DESC",itemset6.getRow().getFieldValue("ITEM6_CATALOG_NO_DESC"));
        data.setFieldItem("ITEM6_SALES_PRICE",itemset6.getRow().getFieldValue("ITEM6_SALES_PRICE"));
        data.setFieldItem("ITEM6_SALES_CURRENCY",itemset6.getRow().getFieldValue("ITEM6_SALES_CURRENCY"));
        data.setFieldItem("ITEM6_DISCOUNT",itemset6.getRow().getFieldValue("ITEM6_DISCOUNT"));
        data.setFieldItem("ITEM6_DISCOUNTED_PRICE",itemset6.getRow().getFieldValue("ITEM6_DISCOUNTED_PRICE"));
        data.setFieldItem("ITEM6_PLANNED_PRICE",itemset6.getRow().getFieldValue("ITEM6_PLANNED_PRICE"));
        data.setFieldItem("FROM_DATE_TIME",itemset6.getRow().getFieldValue("FROM_DATE_TIME"));
        data.setFieldItem("TO_DATE_TIME",itemset6.getRow().getFieldValue("TO_DATE_TIME"));
        data.setFieldItem("ITEM6_NOTE",itemset6.getRow().getFieldValue("ITEM6_NOTE"));

        itemset6.addRow(data);                        
    }


    public void countFindITEM6()
    {
        ASPManager mgr = getASPManager();

        headrowno = headset.getCurrentRowNo();
        ASPQuery q = trans.addQuery(itemblk6);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

        mgr.submit(trans);

        itemlay6.setCountValue(toInt(itemset6.getRow().getValue("N")));
        itemset6.clear();

        headrowno = headset.getCurrentRowNo();
    }

    public void saveReturnItem6()
    {
        ASPManager mgr = getASPManager();

        int currHead = headset.getCurrentRowNo();
        int currrowItem6 = itemset6.getCurrentRowNo();
        itemset6.changeRow();
        mgr.submit(trans);
        headset.goTo(currHead);
        okFindITEM4();
        itemset6.goTo(currrowItem6);
    }

    //------------------ End of block 6 -------------------------------------------

    public void okFindITEM7()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        int headrowno = headset.getCurrentRowNo();

        ASPQuery q = trans.addQuery(itemblk7);
        q.addWhereCondition("WO_NO = ? AND STD_JOB_CONTRACT = ?");
        q.addParameter("WO_NO",headset.getValue("WO_NO"));
        q.addParameter("CONTRACT",headset.getValue("CONTRACT"));

        q.includeMeta("ALL");
        mgr.querySubmit(trans, itemblk7);

        headset.goTo(headrowno);
    }

    public void countFindITEM7()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        int headrowno = headset.getCurrentRowNo();

        ASPQuery q = trans.addQuery(itemblk7);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ? AND STD_JOB_CONTRACT = ?");
        q.addParameter("WO_NO",headset.getValue("WO_NO"));
        q.addParameter("CONTRACT",headset.getValue("CONTRACT"));
        mgr.submit(trans);

        headset.goTo(headrowno);

        itemlay7.setCountValue(toInt(itemset7.getRow().getValue("N")));
        itemset7.clear();
    }

    public void newRowITEM7()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addEmptyCommand("ITEM7", "WORK_ORDER_JOB_API.New__", itemblk7);
        cmd.setParameter("ITEM7_WO_NO", headset.getValue("WO_NO"));
        cmd.setOption("ACTION", "PREPARE");

        cmd = trans.addCustomFunction("GETSDATE", "Active_Separate_API.Get_Plan_S_Date", "DATE_FROM");
        cmd.addParameter("ITEM7_WO_NO", headset.getValue("WO_NO"));

        cmd = trans.addCustomFunction("GETFDATE", "Active_Separate_API.Get_Plan_F_Date", "DATE_TO");
        cmd.addParameter("ITEM7_WO_NO", headset.getValue("WO_NO"));

        trans = mgr.perform(trans);

        ASPBuffer data = trans.getBuffer("ITEM7/DATA");

        Date dPlanSDate = trans.getBuffer("GETSDATE/DATA").getFieldDateValue("DATE_FROM");
        Date dPlanFDate = trans.getBuffer("GETFDATE/DATA").getFieldDateValue("DATE_TO");

        data.setFieldDateItem("DATE_FROM", dPlanSDate);
        data.setFieldDateItem("DATE_TO", dPlanFDate);
        itemset7.addRow(data);
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
        String cbnote_var = null;
        String cbmobile_var = null;
        String cbhasdocuments = null;

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
        cmd.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT"));
        cmd.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));
        cmd.addParameter("REG_DATE",headset.getRow().getFieldValue("REG_DATE"));

        trans = mgr.perform(trans);

        String cbwarranty = trans.getValue("CBWARRNT/DATA/CBWARRANTYONOBJECT");

        if (secBuff.getSecurityInfo().itemExists("DOC_REFERENCE_OBJECT_API.EXIST_OBJ_REFERENCE"))
            cbhasdocuments = trans.getValue("CBDOCU/DATA/CBHASDOCUMENTS");

        String note = headset.getRow().getValue("NOTE");
        String cbnote = headset.getRow().getFieldValue("CMB_GENERATE_NOTE");
        String clientValue = headset.getRow().getFieldValue("DUMMY_GENERATE_NOTE_YES");

        if (clientValue.equals(cbnote))
            cbnote_var = "TRUE";

        ASPBuffer row = headset.getRow();
        row.setValue("CBWARRANTYONOBJECT",cbwarranty);
        row.setValue("CB_GENERATE_NOTE",cbnote_var);
        // 107820
        headset.setRow(row);       
    }

    public void setDisable(int headrowno)
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        headset.goTo(headrowno);
        String pm_no = headset.getRow().getValue("PM_NO");

        if (mgr.isEmpty(pm_no))
        {
            mgr.getASPField("NOTE").setReadOnly();
            mgr.getASPField("CB_GENERATE_NOTE").setReadOnly();      
        }
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  FOR HEAD -----------------
//-----------------------------------------------------------------------------

    public void none()
    {
        ASPManager mgr = getASPManager();

        mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERNORMBSEL: No RMB method has been selected."));
    }

// Some Extra None functions to remove the devider lines in overview mode RMB

    public void none1()
    {
        ASPManager mgr = getASPManager();

        mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERNORMBSEL: No RMB method has been selected."));
    }


    public void none2()
    {
        ASPManager mgr = getASPManager();

        mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERNORMBSEL: No RMB method has been selected."));
    }


    public void none3()
    {
        ASPManager mgr = getASPManager();

        mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERNORMBSEL: No RMB method has been selected."));
    }


    public void printWO()
    {
        ASPManager mgr = getASPManager();

        //Web Alignment - multirow action
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

        // Bug ID 91376, Start
        for (int i = 0;i < count;i++)
        {
            int nWoNo = new Double(headset.getRow().getValue("WO_NO")).intValue();
            attr1 = "REPORT_ID" + (char)31 + "ACTIVE_SEP_WO_PRINT_REP" + (char)30;
            attr2 = "WO_NO_LIST" + (char)31 + nWoNo + (char)30;
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
        // Bug ID 91376, End

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
        urlString = createTransferUrl("ActiveSeparate2.page", transferBuffer);

        newWinHandle = "PrepareWO"; 
        //
    }

    public void sparePartObject()
    {
        ASPManager mgr = getASPManager();
        //Web Alignment - simplify code for  RMBs
        int head_current = headset.getCurrentRowNo();   
        String s_head_curr = String.valueOf(head_current);

        int currrow = itemset3.getCurrentRowNo();

        String s_currrow = String.valueOf(currrow);
        ctx.setGlobal("CURRROWGLOBAL",s_currrow);
        ctx.setGlobal("WONOGLOBAL",s_head_curr);

        bOpenNewWindow = true;
        ctx.setCookie("PageID_CurrentWindow", "*");
        urlString = "MaintenanceObject2.page?MCH_CODE=" + mgr.URLEncode(headset.getValue("MCH_CODE")) +
                    "&CONTRACT="+  mgr.URLEncode(headset.getValue("CONTRACT")) +
                    "&WO_NO="+ mgr.URLEncode(headset.getValue("WO_NO")) +
                    "&ORDER_NO="+ mgr.URLEncode(itemset3.getValue("MAINT_MATERIAL_ORDER_NO")) +
                    "&QRYSTR="+ mgr.URLEncode(qrystr)+
                    "&FRMNAME=ReportInWO" ;

        newWinHandle = "sparePartObject"; 
    }


    public void updateSparePartObject()
    {
        ASPManager mgr = getASPManager();

	String calling_url = mgr.getURL();
	ctx.setGlobal("CALLING_URL", calling_url);
        
        if (headset.countRows()>0)
        {
            int curr = headset.getCurrentRowNo();
            ctx.setGlobal("CURRROWGLOBAL",Integer.toString(curr));
        }

	if (itemlay.isMultirowLayout())
            itemset.goTo(itemset.getRowSelected());
        
        bOpenNewWindow = true;
        ctx.setCookie("PageID_CurrentWindow", "*");
        urlString = "UpDateSparePartsObject.page?MCH_CODE=" + mgr.URLEncode(headset.getValue("MCH_CODE")) +
                    "&MCH_CODE_CONTRACT=" + mgr.URLEncode(headset.getValue("MCH_CODE_CONTRACT")) +
                    "&PART_NO=" + mgr.URLEncode(itemset.getValue("PART_NO")) +
                    "&PART_DESC=" + mgr.URLEncode(itemset.getValue("SPAREDESCRIPTION")) +
	            "&SPARE_CONTRACT=" + mgr.URLEncode(itemset.getValue("SPARE_CONTRACT")) +
                    "&FRMNAME=ReportInWO";

	newWinHandle = "updateSpareparts"; 

        itemset.refreshAllRows();

    }


    public void objStructure()
    {
        ASPManager mgr = getASPManager();
        //Web Alignment - simplify code for  RMBs
        bOpenNewWindow = true;
        ctx.setCookie("PageID_CurrentWindow", "*");
        urlString = "MaintenaceObject3.page?MCH_CODE=" + mgr.URLEncode(headset.getValue("MCH_CODE")) +
                    "&CONTRACT="+  mgr.URLEncode(headset.getValue("CONTRACT")) +
                    "&WO_NO="+ mgr.URLEncode(headset.getValue("WO_NO")) +
                    "&ORDER_NO="+ mgr.URLEncode(itemset3.getValue("MAINT_MATERIAL_ORDER_NO")) +
                    "&QRYSTR="+ mgr.URLEncode(qrystr) +
                    "&FRMNAME=ReportInWO" ;

        newWinHandle = "objStruct"; 

    }


    public void requisitions()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url);

	//Retrive the current object status
	trans.clear();

	cmd = trans.addCustomFunction("GETOBJSTATE", "Active_Separate_Api.Get_Obj_State", "OBJSTATE" );
        cmd.addParameter("WO_NO", headset.getRow().getValue("WO_NO"));

        trans = mgr.perform(trans);

	String sObjState  = trans.getBuffer("GETOBJSTATE/DATA").getFieldValue("OBJSTATE");

        ASPBuffer buff = mgr.newASPBuffer();
        ASPBuffer row = buff.addBuffer("0");
        row.addItem("WO_NO",headset.getRow().getValue("WO_NO"));
        row.addItem("MCH_CODE",headset.getRow().getValue("MCH_CODE"));
        row.addItem("MCH_NAME",headset.getRow().getValue("DESCRIPTION"));
        row.addItem("OBJSTATE", sObjState);

        //Web Alignment - open tabs implemented as RMBs in a new window
        bOpenNewWindow = true;
        ctx.setCookie("PageID_CurrentWindow", "*");
        urlString = createTransferUrl("WorkOrderRequisHeaderRMB.page", buff);
        newWinHandle    =    "WORequisitions";
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
        //Web Alignment - simplify code for RMBs
        bOpenNewWindow = true;    
        ctx.setCookie("PageID_CurrentWindow", "*");
        urlString = "ActiveSeparate2WorkCenterLoad.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"));
        newWinHandle = "WorkCentLoad";
        //
        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
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


    public void coInformation()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url);

        ASPBuffer buff = mgr.newASPBuffer();
        ASPBuffer row = buff.addBuffer("0");
        row.addItem("WO_NO",headset.getRow().getValue("WO_NO")) ;

        //Web Alignment - open tabs implemented as RMBs in a new window
        bOpenNewWindow = true;
        ctx.setCookie("PageID_CurrentWindow", "*");
        //Bug Id 70012, Start
	urlString = createTransferUrl("ActiveSeparateReportCOInfo.page?&PASS_PLN_S_DATE="+sPlanSDate
				      +"&PASS_WORK_TYPE_ID="+sWorkTypeId
				      +"&PASS_CON_TYPE_DB="+sConTypeDb, buff);
	//Bug Id 70012, End

        newWinHandle = "COInfo";
        //
    }

    public void maintTask()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        String calling_url_wo = mgr.getURL();
        ctx.setGlobal("CALLING_URL_WO", calling_url_wo);

        //Web Alignment - open tabs implemented as RMBs in a new window
        bOpenNewWindow = true;
        ctx.setCookie("PageID_CurrentWindow", "*");
        urlString = createTransferUrl("MaintTask.page", headset.getSelectedRows("WO_NO"));
        newWinHandle = "maintTask";         
        //
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


    public void preposting()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        calling_url=mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url);

        eval(headblk.generateAssignments());

        String contract = headset.getRow().getValue("CONTRACT");
        String enabl10 = "0";
        lout = (headlay.isMultirowLayout()?"1":"0");

        if (( !("Closed".equals(mgr.getASPField("OBJSTATE").getValue())) ))
        {
            String enabledArr[] = getEnabledFields();

            //bOpenNewWindow = true;
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
            headset.goTo(headset.getRowSelected());

        callingurl1 = mgr.getURL();
        calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url);

        String cust_no = headset.getRow().getValue("CUSTOMER_NO");
        String agreement = headset.getRow().getValue("AGREEMENT_ID");

        //Web Alignment - open tabs implemented as RMBs in a new window
        bOpenNewWindow = true;
        ctx.setCookie("PageID_CurrentWindow", "*");
        urlString = "WorkOrderCoding1.page?WO_NO="+mgr.URLEncode(headset.getValue("WO_NO"))+"&FORM="+mgr.URLEncode(callingurl1)+"&CUSTOMERNO="+mgr.URLEncode(cust_no)+"&AGREEMENT_ID"+mgr.URLEncode(agreement);

        newWinHandle = "postings";
        //

    }


    public void transferToCusOrder()
    {
        //Web Alignment - Enable Multirow Action
        ASPManager mgr = getASPManager();

        calling_url=mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url);


        int count = 0;
        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;

        ctx.setCookie("PageID_CurrentWindow", "*");

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


    public void printAuth()
    {
        boolean canprint = false;

        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
        {
            ASPTransactionBuffer secViewBuff = mgr.newASPTransactionBuffer();
            secViewBuff.addSecurityQuery("CUSTOMER_AGREEMENT");

            secViewBuff = mgr.perform(secViewBuff);

            if (!(secViewBuff.getSecurityInfo().itemExists("CUSTOMER_AGREEMENT")))
            {
                mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERPRINTAUTHO: Cannot perform Print Authorization"));
                canprint = false;
            }
            else
            {
                headset.goTo(headset.getRowSelected());
                canprint = true;
            }
        }

        // Bug ID 91376, Start
        if (headlay.isSingleLayout()  ||   canprint)
        {
            int nWoNo = new Double(headset.getRow().getValue("WO_NO")).intValue();
            String attr1 = "REPORT_ID" + (char)31 + "WORK_ORDER_AUTHORIZATION_REP" + (char)30;
            String attr2 = "WO_NO_LIST" + (char)31 + nWoNo + (char)30;
            String attr3 =  "";
            String attr4 =  "";

            cmd = trans.addCustomCommand( "PRNT","Archive_API.New_Client_Report");
            cmd.addParameter("ATTR0");                       
            cmd .addParameter("ATTR1",attr1);       
            cmd.addParameter("ATTR2",attr2);              
            cmd.addParameter("ATTR3",attr3);      
            cmd.addParameter("ATTR4",attr4);       

            trans = mgr.perform(trans);

            String attr0 = trans.getValue("PRNT/DATA/ATTR0");

            ASPBuffer print = mgr.newASPBuffer();
            ASPBuffer printBuff=print.addBuffer("DATA");
            printBuff.addItem("RESULT_KEY",attr0);

            callPrintDlg(print,false);
        }
        //Bug ID 91376, End
    }


    public void printReleaseCertificate()
    {
        ASPManager mgr = getASPManager();

        //Web Alignment - multirow action
        int count = 0;
        ASPBuffer print;
        ASPBuffer printBuff;
        String attr1;
        String attr2;
        String attr3;
        String attr4;                                                          
        // Bug ID 91376, Start
        int nWoNo = new Double(headset.getRow().getValue("WO_NO")).intValue();
        // Bug ID 91376, End

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

        // Bug ID 91376, Start
        for (int i = 0;i < count; i++)
        {
            attr1 = "REPORT_ID" + (char)31 + "RELEASE_CERTIFICATE_REP" + (char)30;
            attr2 = "WO_NO" + (char)31 + nWoNo + (char)30;
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
        // Bug ID 91376, End

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

    public void printAuthRelCerti()
    {
        ASPManager mgr = getASPManager();
        trans.clear();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());

        String RelCertifFlag = headset.getRow().getValue("RELEASE_CERTIFICATE");
        String worderNo = headset.getRow().getValue("WO_NO");


        if ("TRUE".equals(RelCertifFlag))
        {
            printReleaseCertificate();

        }
        else
        {
            cmd = trans.addCustomCommand("GETERRMSGWO","Release_Certificate_API.Check_Work_Order");
            cmd.addParameter("WO_NO",worderNo);

            trans = mgr.perform(trans);

            URLString = "PrintAuthReleaseCertificateWiz.page?WO_NO="+mgr.URLEncode(headset.getValue("WO_NO"));


            performRMB   = "TRUE";
            WindowName   = "PRINTAUTHRELCERTI"; 
        }

    }


    public void printReleaseCertif()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());

        URLString = "ReleaseCertificate.page?WO_NO="+mgr.URLEncode(headset.getValue("WO_NO"));
        URLString = "ReleaseCertificate.page?WO_NO="+mgr.URLEncode(headset.getValue("WO_NO"));

        performRMB   = "TRUE";
        WindowName   = "PRINTRELEASECERTIF"; 

    }

//----------------------------- get the enabled fields to be  passed to prepost.page-----------------------------------

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


    public void moveSerial2()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();
        //Web Alignment - simplify code for  RMBs
        urlString = "ActiveSeparateDlg.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) + 
                    "&ERR_DESCR=" + mgr.URLEncode(headset.getRow().getValue("ERR_DESCR")) + 
                    "&CONTRACT=" + mgr.URLEncode(headset.getRow().getValue("MCH_CODE_CONTRACT")) + 
                    "&MCH_CODE=" + mgr.URLEncode(headset.getRow().getValue("MCH_CODE")) + 
                    "&CONNECTION_TYPE="+mgr.URLEncode(headset.getRow().getValue("CONNECTION_TYPE"));
        bOpenNewWindow = true;
        ctx.setCookie("PageID_CurrentWindow", "*"); 
        newWinHandle = "moveSerial2";

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

        current_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",current_url);    

        //Web Alignment - Simplify code for RMBs
        urlString = "WorkOrderRolePlanningLov.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"))+"&FRMNAME=ReportInWO&RMBNAME=CreatePlan";
        bOpenNewWindow = true;
        ctx.setCookie("PageID_CurrentWindow", "*");
        newWinHandle = "CreatePlan";
    }

    public void createFromAllocation()
    {
        ASPManager mgr = getASPManager();

        if (itemlay1.isMultirowLayout())
            itemset1.goTo(itemset1.getRowSelected());
        else
            itemset1.selectRow();

        int head_current = headset.getCurrentRowNo();   
        String s_head_curr = String.valueOf(head_current);
        ctx.setGlobal("HEADGLOBAL", s_head_curr);

        urlString = "TimeReportFromAllocDlg.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"))+
                    "&FRMNAME=" + mgr.URLEncode("ReportInWO") +
                    "&QRYSTR=" + mgr.URLEncode(qrystr);

        bOpenNewWindow = true;
        ctx.setCookie("PageID_CurrentWindow", "*");
        newWinHandle = "createAlloc";
    }


    public void connectToPlanTime()
    {
        ASPManager mgr = getASPManager();


        if (itemlay1.isMultirowLayout())
            itemset1.goTo(itemset1.getRowSelected());
        else
            itemset1.selectRow();

        connReconToPlan = "true";

        current_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",current_url); 

        //Web Alignment - Simplify code for RMBs
        urlString = "WorkOrderRolePlanningLov.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"))+"&FRMNAME=ReportInWO&RMBNAME=ConnectPlan&EDITROWNUM="+itemset1.getCurrentRowNo();
        bOpenNewWindow = true;
        ctx.setCookie("PageID_CurrentWindow", "*");
        newWinHandle = "ConnectPlann";
        //

        itemlay1.setLayoutMode(itemlay1.EDIT_LAYOUT); 
        cmd = trans.addEmptyCommand("ITEM1","WORK_ORDER_CODING_API.Modify__",itemblk1);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);
    }

//  Bug 83532, Start
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

       urlString = createTransferUrl("../pcmw/WorkOrderAddressDlg.page?__DYNAMIC_DEF_KEY=ACTIVE_SEPARATE", transferBuffer);
       ctx.setCookie("PageID_CurrentWindow", "*");
       newWinHandle = "workorderaddr"; 
    }
//    Bug 83532, End
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

    public void receiveOrder()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
           headset.goTo(headset.getRowSelected());
        
        if (!mgr.isEmpty(headset.getValue("RECEIVE_ORDER_NO")))
        {
            URLString = "../purchw/MroObjectReceiveOrder.page?ORDER_NO="+mgr.URLEncode(headset.getValue("RECEIVE_ORDER_NO"));
            performRMB = "TRUE";
            newWinHandle   = "MroObjectRecieveOrder";
        }
        else
            mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERNOTPERFORM1: Cannot perform on the selected record."));
    }

    public void changeConditionCode()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd;
        ASPBuffer buff,row;


        if (headlay.isMultirowLayout())
            headset.store();
        else
        {
            headset.unselectRows();
            headset.selectRow();
        }

        trans.clear();

        cmd = trans.addCustomFunction("GETPARTNO","EQUIPMENT_SERIAL_API.GET_PART_NO","PART_NO");
        cmd.addParameter("CONTRACT",headset.getValue("CONTRACT"));
        cmd.addParameter("MCH_CODE",headset.getValue("MCH_CODE"));

        cmd = trans.addCustomFunction("GETCONDCODEUSAGEDB","PART_CATALOG_API.Get_Condition_Code_Usage_Db","COND_CODE_USAGE");
        cmd.addReference("PART_NO","GETPARTNO/DATA");

        cmd = trans.addCustomFunction("GETSERIALNO","EQUIPMENT_SERIAL_API.GET_SERIAL_NO","SERIAL_NO");
        cmd.addParameter("CONTRACT",headset.getValue("CONTRACT"));
        cmd.addParameter("MCH_CODE",headset.getValue("MCH_CODE"));

        cmd = trans.addCustomFunction("CONDCODEMANAGER","Condition_Code_Manager_API.Get_Condition_Code","COND_CODE");
        cmd.addReference("PART_NO","GETPARTNO/DATA");
        cmd.addReference("SERIAL_NO","GETSERIALNO/DATA");
        cmd.addParameter("LOT_BATCH_NO","NULL");

        cmd = trans.addCustomFunction("GETPARTDESC","Part_Catalog_API.Get_Description","PART_DESC");
        cmd.addReference("PART_NO","GETPARTNO/DATA");

        cmd = trans.addCustomFunction("GETCONDCODEDESC","Condition_Code_API.Get_Description","COND_CODE_DESC");
        cmd.addReference("COND_CODE","CONDCODEMANAGER/DATA");

        trans = mgr.perform(trans);  

        String sPartNo = trans.getValue("GETPARTNO/DATA/PART_NO");
        String sSerialNo = trans.getValue("GETSERIALNO/DATA/SERIAL_NO");
        String sCondCodeUseDb = trans.getValue("GETCONDCODEUSAGEDB/DATA/COND_CODE_USAGE");
        String sPartDesc = trans.getValue("GETPARTDESC/DATA/PART_DESC");
        String condition_code_ = trans.getValue("CONDCODEMANAGER/DATA/COND_CODE");
        String sCondCodeDesc = trans.getValue("GETPARTDESC/DATA/COND_CODE_DESC");
        String sLotBatchNo = "*";
        String sWoNo = headset.getValue("WO_NO");

        if (headlay.isMultirowLayout())
        {
            headset.setFilterOff();
        }
        if (("ALLOW_COND_CODE".equals(sCondCodeUseDb)) && !mgr.isEmpty(headset.getValue("MCH_CODE")) && !mgr.isEmpty(sPartNo) && !mgr.isEmpty(sSerialNo))
        {
            buff = mgr.newASPBuffer();
            row = buff.addBuffer("0");
            row.addItem("PART_NO",sPartNo);
            row = buff.addBuffer("1");
            row.addItem("PART_DESCRIPTION",sPartDesc);
            row = buff.addBuffer("2");
            row.addItem("SERIAL_NO",sSerialNo);
            row = buff.addBuffer("3");
            row.addItem("LOT_BATCH_NO",sLotBatchNo);
            row = buff.addBuffer("4");
            row.addItem("ITEM5_CONDITION_CODE",condition_code_);
            row = buff.addBuffer("5");
            row.addItem("CONDITION_CODE_DESC",sCondCodeDesc);
            row = buff.addBuffer("6");
            row.addItem("WO_NO",sWoNo);

            String calling_url = mgr.getURL();
            ctx.setGlobal("CALLING_URL",calling_url);

            //Web Alignment - open RMBs in a new window
            bOpenNewWindow = true;
            ctx.setCookie("PageID_CurrentWindow", "*");
            urlString = createTransferUrl("../partcw/ChangeConditionCodeDlg.page", buff);
            newWinHandle = "changeCondCode";
            //
        }
        else
        {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CONDICODE: Cannot change the condition code."));
        }

    }

    // Bug 72214, start
    public void resheduleWoStructProj()
    {
       ASPManager mgr = getASPManager();

       if (headlay.isMultirowLayout())
          headset.goTo(headset.getRowSelected());
       else
          headset.selectRow();

       cmd = trans.addCustomFunction("CBSENABLED","Scheduling_Site_Config_API.Check_Maint_Contract_Web","SEND_TO_CBS");
       cmd.addParameter("CONTRACT",headset.getValue("CONTRACT"));

       trans = mgr.perform(trans);

       if ("TRUE".equals(trans.getValue("CBSENABLED/DATA/SEND_TO_CBS")))
       {
          trans.clear();
          bOpenNewWindow = true;
           ctx.setCookie("PageID_CurrentWindow", "*");
          urlString= "ActiveSeparateDlg4.page?WO_NO=" + mgr.URLEncode(headset.getValue("WO_NO")) ;
          newWinHandle = "rescheduleWo";
       }
       else
          mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPINWONOCBS: Site &1 is not connected to a scheduling server!", headset.getRow().getValue("CONTRACT")));
    }
    // Bug 72214, end

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  FOR ITEM BLOCK3 MATERIAL
//-----------------------------------------------------------------------------

    public void performItem(String command) 
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
            mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCANNOT: Cannot perform on selected line"));    
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
            mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCANNOT: Cannot perform on selected line"));    
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

        ASPBuffer prePostBuffer = mgr.newASPBuffer();
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

		mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERNONEW: No new Assigned stock for this Material Order."));
	}
	else
	    mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERCANNOT: Cannot perform on selected line"));
    }

    public void note()
    {
        ASPManager mgr = getASPManager();
        if (itemlay3.isMultirowLayout())
            itemset3.goTo(itemset3.getRowSelected());

        noteIdVal = itemset3.getRow().getValue("SNOTETEXT");
        //Web Alignment - simplify code for RMBs   
        bOpenNewWindow = true;
        ctx.setCookie("PageID_CurrentWindow", "*");
        urlString = "EditorDlg.page?SNOTETEXT="+mgr.URLEncode(itemset3.getRow().getValue("SNOTETEXT"))+"&FRMNAME=ReportInWO&QRYSTR="+mgr.URLEncode(qrystr);
        newWinHandle = "note";
    }

    public void detchedPartList()
    {
        ASPManager mgr = getASPManager();
        String sPartNo = "";

        calling_url=mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url);
        int head_current = headset.getCurrentRowNo();   
        ctx.setGlobal("CURRHEADGLOBAL",String.valueOf(head_current));

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
                mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERCANNOT: Cannot perform on selected line"));
            }
            else{

                bOpenNewWindow = true;
                row.addItem("PART_NO",sPartNo);
                row.addItem("WO_NO",itemset3.getRow().getValue("WO_NO"));    
                row.addItem("FRAME","ReportInWO");
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
            row.addItem("FRAME","ReportInWO");
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

                 if (!canPerform){
                     mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCANNOTMAT1: No material requirements for selected item."));
                     return;
                 }
                 else
                 {
                    trans.clear();

                    if (plan_qty1 > nAvailToIss)
                        mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERAVAIL: Available quantity for part ") +itemset.getRow().getValue("PART_NO") + mgr.translate("PCMWACTIVESEPARATE2AVAIL1:  was ")  +sAvailToIss+ mgr.translate("PCMWACTIVESEPARATE2AVAI2: .")); 

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
         		   mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWOCANTISSUE: All material could not be issued for part &1. Remaining quantity to be issued: &2", itemset.getValue("PART_NO"), new Double(nQtyShort).toString()));
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

    public void returnObject()
    {
       ASPManager mgr = getASPManager();
       int curro = headset.getCurrentRowNo();

       if (headlay.isMultirowLayout())
          headset.goTo(headset.getRowSelected());

       trans.clear();

       cmd = trans.addCustomCommand("RETURNOBJ","ACTIVE_SEPARATE_API.Return_Object");
       cmd.addParameter("RES_CUST_NO");
       cmd.addParameter("RES_CUST_LINE_NO");
       cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

       trans = mgr.perform(trans);

       sResCustNo = trans.getValue("RETURNOBJ/DATA/RES_CUST_NO");
       sResCustLineNo = trans.getValue("RETURNOBJ/DATA/RES_CUST_LINE_NO");

       if (!mgr.isEmpty(sResCustNo) && !mgr.isEmpty(sResCustLineNo)) 
          bObjReturned = true;
       else
          bObjNotReturned = false;

       headset.refreshRow();
       headset.goTo(curro);
    }

    public void returnNonOperationalObj()
    {
       returnObject();
    }

    public void createRecieveOrder()
    {
       ASPManager mgr = getASPManager();
       int curro = headset.getCurrentRowNo();

       if (headlay.isMultirowLayout())
          headset.goTo(headset.getRowSelected());
       
       trans.clear();
   
       cmd = trans.addCustomCommand("CREATRECIVEORD","ACTIVE_SEPARATE_API.Create_Mro_Ro"); 
       cmd.addParameter("RECEIVE_ORDER_NO");
       cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

       trans = mgr.perform(trans);

       sRecieveOrdNo = trans.getValue("CREATRECIVEORD/DATA/RECEIVE_ORDER_NO");

       if (!mgr.isEmpty(sRecieveOrdNo)) 
       {
          bRecieveOrdCreated = true;

          trans.clear();
          cmd = trans.addCustomFunction("GETREQDATE","ACTIVE_SEPARATE_API.Get_Required_Start_Date","REQ_START_DATE");
          cmd.addParameter("WO_NO",headset.getValue("WO_NO"));
          trans = mgr.perform(trans);

          String sReqStartDate = trans.getValue("GETREQDATE/DATA/REQ_START_DATE");
          
          if (mgr.isEmpty(sReqStartDate)) 
          {
             bReqStartDateEmpty = true;
             sWoNo = headset.getValue("WO_NO");
          }
          headset.refreshRow();
       }
       else
          bRecieveOrdNotCreated = true;

       headset.goTo(curro);
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
          mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCANNOTSTAT2: Maint Material Requisition status not valid for material reserve."));
          return ;
       }

       if (!("TRUE".equals(trans.getValue("RESALLOW/DATA/RES_ALLO"))))
       {
          mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCANNOTSTATWO2: Work order status not valid for material reserve."));
          return;
       }

       transForReserve = mgr.newASPTransactionBuffer();

       for (int i = 0; i < count; i++)
       {

          if (itemset.getNumberValue("PLAN_QTY") <= (itemset.getNumberValue("QTY") + itemset.getNumberValue("QTY_ASSIGNED"))) // + itemset.getNumberValue("QTY_RETURNED"))) * ASSALK  Material Issue & Reserve modification.
          {
             mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCANNOTMAT3: No material requirements for selected item."));
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
             mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERAVAIL: Available quantity for part ") +itemset.getRow().getValue("PART_NO") + mgr.translate("PCMWACTIVESEPARATE2AVAIL1:  was ")  +sAvalToRes+ mgr.translate("PCMWACTIVESEPARATE2AVAI2: .")); 
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
        	  mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWOCOULDNOTALL: All material could not be allocated for part &1. Remaining quantity to be reserved: &2", itemset.getValue("PART_NO"), new Double(nQtyShort).toString()));
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
            mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCANNOTSTATWO5WO: Work order status not valid for material issue."));
            return ;
        }

        if (!(sStatusCodeReleased.equals(dfStatus)))
        {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCANNOTSTAT1: Maint Material Requisition status not valid for material issue."));
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
                        "&FRMNAME=ReportInWO&QRYSTR="+mgr.URLEncode(qrystr)+
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
            ctx.setGlobal("CURRROWGLOBAL",String.valueOf(currrow));
        }
        else
            mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCANNOTMAT: No material requirements for selected item."));

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
            mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCANNOTSTATWO5: Work order status not valid for material reserve."));
            return ;
        }

        if (!(sStatusCodeReleased.equals(dfStatus)))
        {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCANNOTSTAT5: Maint Material Requisition status not valid for material manual reserve."));
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
                        "&FRMNAME=ReportInWO&QRYSTR="+mgr.URLEncode(qrystr)+
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
            mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCANNOTMAT: No material requirements for selected item."));

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

    //Web Alignment - MultiRow Action
    public void matReqIssued()
    {
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
            mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCANNOTSTAT6: Work order status not valid for material unreserve."));
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
            urlString = "MaterialRequisReservatDlg2.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"))+"&LINE_ITEM_NO="+mgr.URLEncode(itemset.getRow().getValue("LINE_ITEM_NO"))+"&FRMNAME=ReportInWO&QRYSTR="+mgr.URLEncode(qrystr)+"&PART_NO="+mgr.URLEncode(itemset.getRow().getValue("PART_NO"))+"&CONTRACT="+mgr.URLEncode(itemset.getRow().getValue("SPARE_CONTRACT"))+"&DESCRIPTION="+mgr.URLEncode(itemset.getRow().getValue("SPAREDESCRIPTION"))+"&QTYLEFT="+mgr.URLEncode(nQtyLeft)+"&MAINTMATORDNO="+mgr.URLEncode(itemset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));
            newWinHandle = "manUnReserve";
            //
            ctx.setGlobal("CURRHEADGLOBAL",String.valueOf(currhead));
            ctx.setGlobal("CURRROWGLOBAL",String.valueOf(currrow));
        }
        else
            mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCANNOTMAT: No material requirements for selected item."));
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
                mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSTNVMTUNISS: Work order status not valid for material Unissue."));
                return ;
            }
            else
            {
                //Web Alignment - simplify code for RMBs
                bOpenNewWindow = true; 
                ctx.setCookie("PageID_CurrentWindow", "*"); 
                urlString = "ActiveWorkOrder.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"))+"&MAINTMATORDNO="+mgr.URLEncode(itemset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"))+"&LINE_ITEM_NO="+mgr.URLEncode(itemset.getRow().getValue("LINE_ITEM_NO"))+"&FRMNAME=ReportInWO&QRYSTR="+mgr.URLEncode(qrystr); 
                newWinHandle = "manUnIsssue"; 
                ctx.setGlobal("CURRHEADGLOBAL",String.valueOf(currhead));
                ctx.setGlobal("CURRROWGLOBAL",String.valueOf(currrow));
                
            }
        }
        else
        {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERNOISSUEMATERIAL: Cannot perform Material Requisition Unissue on this record."));
        }
        itemset3.goTo(currrow); 

    }

    public void refreshForm()
    {
      ASPManager mgr = getASPManager();
      okFindITEM5();
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


    public void work()
    {
        perform("WORK__");
    }


    public void report()
    {
        perform("REPORT__");
    }


    public void finish()
    {
        perform("FINISH__");
        headset.clearRow();
    }


    public void cancel()
    {
        perform("CANCEL__");
    }

    public boolean isModuleInst1(String module_)
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();
        ASPCommand cmd = mgr.newASPCommand();
        String modVersion;

        cmd = trans1.addCustomFunction("MODVERS","module_api.get_version","MODULENAME");
        cmd.addParameter("MODULENAME",module_);

        trans1 = mgr.perform(trans1);
        modVersion = trans1.getValue("MODVERS/DATA/MODULENAME");

        if (!mgr.isEmpty(modVersion))
            return true;
        else
            return false;
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
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERWONO: WO No");
        f.setHilite();
        f.setSize(15);
        f.setReadOnly();

        f = headblk.addField("CONTRACT");
        f.setHilite();
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERWOCONTRACT: WO Site");
        f.setUpperCase();
        f.setReadOnly();
        f.setSize(13);

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
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERMCHCODE: Object ID");   
        f.setDynamicLOV("MAINTENANCE_OBJECT",600,450);
        f.setUpperCase();
        f.setHilite();
        f.setMaxLength(100);
        f.setReadOnly();

        f = headblk.addField("MCH_CODE_DESCRIPTION");
        f.setSize(20);
        f.setHilite();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDEROBJDESC: Object Description");
        f.setReadOnly();

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
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSTATE: Status");
        f.setReadOnly();

        f = headblk.addField("ERR_DESCR");
        f.setSize(45);
        f.setHilite();
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERERRDESCR: Directive");
        f.setMaxLength(60);
        f.setDefaultNotVisible();

        f = headblk.addField("ORG_CODE");
        f.setSize(15);
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,445);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERORGCODE: Maintenance Organization");
        f.setUpperCase();
        f.setHilite();
        f.setMaxLength(8);
        f.setReadOnly();

        f = headblk.addField("ORGCODEDESCRIPTION");
        f.setSize(30);
        f.setHilite();
        f.setFunction("Organization_Api.Get_Description(:CONTRACT,:ORG_CODE)");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERORG_CODEDESC: Organization Description"); 
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("CRITICALITY");
        f.setSize(13);
        f.setDynamicLOV("EQUIPMENT_CRITICALITY",600,445);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCRITICALITY: Criticality");
        f.setFunction("EQUIPMENT_FUNCTIONAL_API.Get_Criticality(:CONTRACT,:MCH_CODE)");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("CRITICALITYDESCRIPTION");
        f.setSize(22);
        f.setFunction("EQUIPMENT_FUNCTIONAL_API.Get_Criticality_Description(:CONTRACT,:MCH_CODE)");
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCRITICALITYDESCRP: Criticality Description");
        f.setDefaultNotVisible();

        f = headblk.addField("CBWARRANTYONOBJECT");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCBWARRANTYONOBJECT: Supplier Warranty");
        f.setFunction("''");
        f.setCheckBox("FALSE,TRUE");
        f.setReadOnly();
        f.setDefaultNotVisible();

	//Bug 87766, Start, Modified label and function of CBHASSTRUCTURE. Added CBINASTRUCTURE.
        f = headblk.addField("CBHASSTRUCTURE");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCBHASSTRUCTURE: Has Structure");
        f.setFunction("substr(WORK_ORDER_CONNECTION_API.Has_Connection_Down(:WO_NO),1,5)");
        f.setCheckBox("FALSE,TRUE");
        f.setReadOnly();
        f.setDefaultNotVisible();

	f = headblk.addField("CBINASTRUCTURE");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCBINASTRUCTURE: In a Structure");
        f.setFunction("substr(WORK_ORDER_CONNECTION_API.Has_Connection_Up(:WO_NO),1,5)");
        f.setCheckBox("FALSE,TRUE");
        f.setReadOnly();
        f.setDefaultNotVisible();
	//Bug 87766, End

        f = headblk.addField("CBHASDOCUMENTS");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCBHASDOCUMENTS: Documents");
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
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCBTRANSTOMOB: Transferred To Mobile");
        f.setCheckBox("FALSE,TRUE");
        f.setReadOnly();
        f.setDefaultNotVisible();
        //Bug 77304, end

        f = headblk.addField("RELEASE_CERTIFICATE");
        f.setSize(13);
        f.setCheckBox("FALSE,TRUE");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERRELEASECERT: Release Certificate");
        f.setReadOnly();
        f.setFunction("RELEASE_CERTIFICATE_API.HAS_RELEASE_CERTIFICATE(:WO_NO)");
        f.setDefaultNotVisible();

        f = headblk.addField("ISVIM");
        f.setHidden();
        f.setUpperCase();
        f.setFunction("''");

        f = headblk.addField("CUST_ORDER_NO");
        f.setHidden();

        //----------Fields got from ITEMBLK0------------------ 

        f = headblk.addField("ERR_CLASS");
        f.setSize(10);
        f.setDynamicLOV("WORK_ORDER_CLASS_CODE",600,445);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERERRCLASS: Class");
        f.setUpperCase();
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERERCL: List of Class"));
        f.setDefaultNotVisible();

        f = headblk.addField("ERRCLASSDESCR");
        f.setFunction("WORK_ORDER_CLASS_CODE_API.Get_Description(:ERR_CLASS)");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERERRCLASSDESC: Class Description");
        mgr.getASPField("ERR_CLASS").setValidation("ERRCLASSDESCR");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("ERR_TYPE");
        f.setSize(10);
        f.setDynamicLOV("WORK_ORDER_TYPE_CODE",600,445);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERERRTYPE: Type");
        f.setUpperCase();
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERERTY: List of Type"));
        f.setDefaultNotVisible();

        f = headblk.addField("ERRTYPEDESC");
        f.setSize(22);
        f.setFunction("WORK_ORDER_TYPE_CODE_API.Get_Description(:ERR_TYPE)");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERERRTYPEDESC: Type Description");
        mgr.getASPField("ERR_TYPE").setValidation("ERRTYPEDESC");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("PERFORMED_ACTION_ID");
        f.setSize(10);
        f.setDynamicLOV("MAINTENANCE_PERF_ACTION",600,445);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERPERFORMEDACTIONID: Performed Action");
        f.setUpperCase();
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERPEACID: List of Performed Action"));
        f.setDefaultNotVisible();

        f = headblk.addField("PERFORMEDACTIONDESC");
        f.setFunction("MAINTENANCE_PERF_ACTION_API.Get_Description(:PERFORMED_ACTION_ID)");
        mgr.getASPField("PERFORMED_ACTION_ID").setValidation("PERFORMEDACTIONDESC");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERPERFORMEDACTIONDESC: Performed Action Description");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("ERR_CAUSE");
        f.setSize(10);
        f.setDynamicLOV("MAINTENANCE_CAUSE_CODE",600,445);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERERRCAUSE: Cause");
        f.setUpperCase();
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERERCA: List of Cause"));
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
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERERRCAUSELO: Cause Description");
        f.setDefaultNotVisible();

        f = headblk.addField("WORK_DONE");
        f.setSize(42);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERWORKDONE: Work Done");
        //031110  ARWILK  Begin  (Bug#110392)
        f.setMaxLength(60);
        //031110  ARWILK  End  (Bug#110392)
        f.setDefaultNotVisible();

        f = headblk.addField("PERFORMED_ACTION_LO");
        f.setSize(40);
        f.setHeight(3);
        //Bug 74288, Start
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERPERFORMEDACTIONLO: Work Details");
        //Bug 74288, End
        f.setDefaultNotVisible();

        f = headblk.addField("NOTE");
        f.setSize(75);
        f.setInsertable();
        f.setCustomValidation("NOTE","CB_GENERATE_NOTE,CMB_GENERATE_NOTE");
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERINSPNOTE: Inspection Note");
        f.setDefaultNotVisible();

        f = headblk.addField("CB_GENERATE_NOTE");
        f.setCheckBox("FALSE,TRUE");
        f.setFunction("GENERATE_NOTE");
        f.setCustomValidation("CB_GENERATE_NOTE","CMB_GENERATE_NOTE");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCBGENNOTE: Generate Note");
        f.setDefaultNotVisible();

        f = headblk.addField("CMB_GENERATE_NOTE");
        f.setSize(14);
        f.setDbName("GENERATE_NOTE");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCMBGENNOTE: Generate Note API");
        f.setHidden();

        f = headblk.addField("DUMMY_GENERATE_NOTE_YES");
        f.setFunction("Generate_Note_API.Decode('1')");
        f.setHidden();

        f = headblk.addField("DUMMY_GENERATE_NOTE_NO");
        f.setFunction("Generate_Note_API.Decode('2')");
        f.setHidden();

        f = headblk.addField("REAL_S_DATE","Datetime");
        f.setSize(25);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERREALSDATE: Actual Start");
        f.setCustomValidation("REAL_S_DATE","REAL_S_DATE");
        f.setDefaultNotVisible();

        f = headblk.addField("PLAN_S_DATE","Datetime");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERPLANSDATE: Planned Start");
        f.setHidden();

        f = headblk.addField("REAL_F_DATE","Datetime");
        f.setSize(25);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERREALFDATE: Actual Completion");
        f.setCustomValidation("REAL_F_DATE","REAL_F_DATE");
        f.setDefaultNotVisible();

        f = headblk.addField("TEMP_DATE","Date");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("TEMP_DATE_TIME","Datetime");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("DATEDIFF","Number","########.#####");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("TEMP_TIME","Datetime");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("REQUIRED_START_DATE","Datetime");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERREQSTRTDT: Required Start");
        f.setSize(25);
        f.setCustomValidation("REQUIRED_START_DATE,CONTRACT","REQUIRED_START_DATE");

        f = headblk.addField("REQUIRED_END_DATE","Datetime");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERREQUIRENDDT: Latest Completion");
        f.setSize(25);
        f.setCustomValidation("REQUIRED_END_DATE,REQUIRED_START_DATE","REQUIRED_END_DATE");

        f = headblk.addField("PLAN_F_DATE","Datetime");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERPLANFDATE: Planned Completion");
        f.setHidden();

        f = headblk.addField("CURRENTPOSITION");
        f.setSize(45);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCURRENTPOSITION: Latest Transaction");
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
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERREGDATE: Reg. Date");
        f.setReadOnly();
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("CUSTOMER_NO");
        f.setSize(16);
        //f.setLOV("../enterw/CustomerInfoLov.page",600,445);
        //f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCUSTOMERNO: Customer No");
        //f.setUpperCase();
        //f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCUNO: List of Customer No"));  
        //f.setDefaultNotVisible();
        f.setHidden();

        f = headblk.addField("CUSTOMERNAME");
        f.setSize(18);
        //f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCUSTOMERNAME: Customer Name");
        f.setFunction("CUSTOMER_INFO_API.Get_Name(:CUSTOMER_NO)");
        //f.setReadOnly();
        //f.setDefaultNotVisible();
        f.setHidden();

        f = headblk.addField("REPORTED_BY");
        f.setSize(13);
        f.setDynamicLOV("EMPLOYEE_LOV",600,450);
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERREPORTEDBY1: Reported by");
        f.setUpperCase();
        f.setReadOnly();
        f.setInsertable();
        f.setMaxLength(20);  

        f = headblk.addField("REP_BY_NAME");
        f.setSize(30);
        f.setFunction("PERSON_INFO_API.Get_Name(:REPORTED_BY)");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);

        f = headblk.addField("AGREEMENT_ID");
        f.setHidden(); 

        f = headblk.addField("LU_NAME");
        f.setHidden();
        f.setFunction("'ActiveSeparate'");

        f = headblk.addField("KEY_REF");
        f.setHidden();
        f.setFunction("CONCAT('WO_NO=',CONCAT(WO_NO,'^'))");      

        f= headblk.addField("STATEVAL");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("PNT");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("RECEIVE_ORDER_NO").        
        setHidden();
        //----------------------------------------------------

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
        f.setReadOnly();
        f.setHidden();
        f.setSize(13);

        f = headblk.addField("OBJLEVEL");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("PARTY_TYPE","Number").
        setHidden().
        setFunction("''");

        f = headblk.addField("S_PARTY_TYPE").
        setHidden().
        setFunction("''");

        f = headblk.addField("IDENTITY_TYPE").
        setHidden().
        setFunction("''");

        f = headblk.addField("VIM_SERIAL_NO").
        setHidden().
        setFunction("''");

        f = headblk.addField("VIM_PART_NO").
        setHidden().
        setFunction("''");

        f = headblk.addField("REQ_START_DATE","Datetime").
        setHidden().
        setFunction("''");

        f = headblk.addField("DUMMY_FLD").
        setHidden().
        setFunction("''");

        f = headblk.addField("OPER_STATUS").
        setHidden().
        setFunction("''");

        f = headblk.addField("PART_OBJ_STATE").
        setHidden().
        setFunction("''");

        f = headblk.addField("RES_CUST_LINE_NO").
        setHidden().
        setFunction("''");

        f = headblk.addField("RES_CUST_NO").
        setHidden().
        setFunction("''");

        //-------------------------------------------------------------
        f = headblk.addField("ERR_DISCOVER_CODE");
        f.setSize(6);
        f.setDynamicLOV("WORK_ORDER_DISC_CODE",600,450);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERERRDISCODE: Discovery");
        f.setUpperCase();
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(3);  

        f = headblk.addField("DISCODESCR");
        f.setSize(27);
        f.setFunction("WORK_ORDER_DISC_CODE_API.Get_Description(:ERR_DISCOVER_CODE)");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERERRDISCODESC: Discovery Description");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        mgr.getASPField("ERR_DISCOVER_CODE").setValidation("DISCODESCR");

        f = headblk.addField("ERR_SYMPTOM");
        f.setSize(6);
        f.setDynamicLOV("WORK_ORDER_SYMPT_CODE",600,450);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERERRSYMPTM: Symptom");
        f.setUpperCase();
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(3);

        f = headblk.addField("SYMPTOMDESCR");
        f.setSize(27);
        f.setFunction("WORK_ORDER_SYMPT_CODE_API.Get_Description(:ERR_SYMPTOM)");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSYMTDISCODESC: Symptom Description");
        f.setMaxLength(2000);
        mgr.getASPField("ERR_SYMPTOM").setValidation("SYMPTOMDESCR");  

        //Bug 73136 Start
        f = headblk.addField("ERR_DESCR_LO");
        f.setSize(43);
        f.setHeight(4);
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERERRDESCRLO: Fault Desc");
        f.setReadOnly();
        f.setMaxLength(2000);
        //Bug 73136 End

        f = headblk.addField("TEST_POINT_ID");
        f.setSize(10);
        f.setDynamicLOV("EQUIPMENT_OBJECT_TEST_PNT",600,450);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERTESTPOINTID: Test point");
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
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERPMNO: PM No");
        f.setReadOnly();
        f.setHidden();
        f.setDefaultNotVisible(); 

        f = headblk.addField("PM_REVISION");
        f.setHidden();

        f = headblk.addField("PM_DESCR");
        f.setSize(36);
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERPMDESCR: Generation info");
        f.setReadOnly();
        f.setMaxLength(2000);   

        f = headblk.addField("NON_SERIAL_LOCATION_DB");
        f.setHidden();

        f = headblk.addField("COND_CODE_USAGE");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("COND_CODE");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("COND_CODE_DESC");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("PART_DESC");
        f.setFunction("''");
        f.setHidden();

        //Fields to handle PRINT methods

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


        f = headblk.addField("HEAD_TEMP");
        f.setHidden();
        f.setFunction("''");

        // End PRINT method

        f = headblk.addField("OBJEVENTS");
        f.setHidden();

        f = headblk.addField("TRANSFERRED");
        f.setFunction("''");
        f.setHidden();


        headblk.addField("HEAD_ISVALID").
        setFunction("''").
        setHidden();

        headblk.addField("HEAD_FACSEQUENCE").
        setFunction("''").
        setHidden();

        f = headblk.addField("ACTIVITY_SEQ","Number");
        f.setLabel("ACTSEPREPHEADACTSEQ: Activity Seq");
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

        f = headblk.addField("DUMMY_ACT_QTY_ISSUED","Number");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("EXCEPTION_EXISTS");
        //attaches the function call only if the PROJ is installed.
        if (mgr.isModuleInstalled("PROJ"))
            f.setFunction("Activity_API.Object_Exceptions_Exist('ASWO',:WO_NO,NULL,NULL)");
        else
           f.setFunction("''");
        f.setHidden();

        // Bug 72214, start
        headblk.addField("DUMMY_VIM_WO").
        setHidden().
        setFunction("''");

        headblk.addField("DUMMY_LAST_WO").
        setHidden().
        setFunction("''");

        headblk.addField("DUMMY_HAS_CON_UP").
        setHidden().
        setFunction("''");

        f = headblk.addField("SEND_TO_CBS");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("EVENT");
        f.setFunction("''");
        f.setHidden();
        
        f = headblk.addField("ENABLE_RESCHEDULE");
        f.setFunction(":ACTIVITY_SEQ||WORK_ORDER_CONNECTION_API.HAS_STRUCTURE(:WO_NO)");
        f.setHidden();
        // Bug 72214, end

        headblk.setView("ACTIVE_SEPARATE");
        headblk.defineCommand("ACTIVE_SEPARATE_API","New__,Modify__,Remove__,RE_INIT__,CONFIRM__,TO_PREPARE__,RELEASE__,REPLAN__,RESTART__,START_ORDER__,PREPARE__,WORK__,REPORT__,FINISH__,CANCEL__");
        headblk.disableDocMan();
        headset = headblk.getASPRowSet();

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERHD: Report In Work Order"));
        headtbl.setWrap();
        headtbl.enableRowSelect();

        headbar = mgr.newASPCommandBar(headblk);

        headbar.addCustomCommand("prepareWO",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERPREPARE: Prepare..."));
        headbar.addCustomCommand("preposting",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERRPRE: Preposting..."));
        headbar.addCustomCommand("transferToCusOrder",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERTRANCUSORD: Transfer to Customer Order..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("placeSerial",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERPLACSER: Place Serial in Equipment Structure..."));
        headbar.addCustomCommand("moveSerial",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERMOVESER: Move Serial to Inventory..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("moveNonSerial",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERMOVENONSER: Move Non Serial to Inventory..."));   
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("printWO",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERPRNT: Print..."));
        headbar.addCustomCommand("printAuth",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERPRNTAUTH: Print Authorization..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("reinit",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERFAULTWORK: FaultReprt\\WorkRequest"));
        headbar.addCustomCommand("observed",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDEROBSER: Observed"));
        headbar.addCustomCommand("underPrep",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERUNDEPREP: Under Preparation"));
        headbar.addCustomCommand("prepared",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERPREPED: Prepared"));
        headbar.addCustomCommand("released",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERREL: Released"));
        headbar.addCustomCommand("started",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSTAR: Started"));
        headbar.addCustomCommand("workDone",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERWRKDNE: Work Done"));
        headbar.addCustomCommand("reported",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERREPTED: Reported"));
        headbar.addCustomCommand("finished",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERFINED: Finished"));
        headbar.addCustomCommand("cancelled",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCANED: Cancelled"));
        if (mgr.isModuleInstalled("PROJ"))
        {
            headbar.addCustomCommand("connectToActivity","ACTSEPREPPROJCONNCONNACT: Connect to Project Activity...");
            headbar.addCustomCommand("disconnectFromActivity","ACTSEPREPPROJCONNDISCONNACT: Disconnect from Project Activity");
            headbar.addCustomCommand("projectActivityInfo", mgr.translate("ACTSEPREPPRJACTINFO: Project Connection Details..."));
            headbar.addCustomCommand("activityInfo", mgr.translate("ACTSEPREPPRJACTVITYINFO: Activity Info..."));
        }
        headbar.addCustomCommand("changeConditionCode",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCHGCOND: Change of Condition Code..."));
        //Bug 83532, Start
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("workorderaddr", mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERADDR: Work Order Address..."));
        headbar.addCustomCommandSeparator();
        //Bug 83532, End
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("printAuthRelCerti",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERPRINTREPRTAUTHRELCERTI: Print/Reprint Authorized Release Certificate..."));
        headbar.addCustomCommand("printReleaseCertif",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERPRNTRELCERTIF: View Airworthiness Release Certificate..."));
        headbar.addCustomCommandSeparator();
        headbar.addSecureCustomCommand("createRecieveOrder",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCREARECORD: Create Receive Order..."),"PURCH_OBJ_RECEIVE_ORD_UTIL_API.Create_Mro_Receive_Order");
        if (mgr.isPresentationObjectInstalled("purchw/MroObjectReceiveOrder.page")) 
        {
           headbar.addCustomCommand("receiveOrder",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERRECIEORD: Receive Order..."));
           headbar.addCommandValidConditions("receiveOrder","RECEIVE_ORDER_NO","Disable","");
        }
        headbar.addSecureCustomCommand("returnObject",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERRETOBJ: Return Object"),"CONNECT_CUSTOMER_ORDER_API.Connect_Wo_To_Mro_Line");
        headbar.addSecureCustomCommand("returnNonOperationalObj",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERRETNOPROBJ: Return Non Operational Object"),"CONNECT_CUSTOMER_ORDER_API.Connect_Wo_To_Mro_Line");
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("freeNotes",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERFREENOTES: Free Notes..."));
        headbar.addCustomCommand("postings",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERPOSTING: Postings..."));
        headbar.addCustomCommand("requisitions",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERRREQUIS: Purchase Requisitions..."));
        headbar.addCustomCommand("permits",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERPERMITS: Permits..."));
        headbar.addCustomCommand("coInformation",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCOINFORMATION: CO Information..."));
        headbar.addCustomCommand("workCenterLoad",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERWORKCENTLOAD: Work Center Load..."));
        headbar.addCustomCommand("maintTask",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERMAINTTASK: Maint Task...")); 
        headbar.addCustomCommand("returns",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERRETURNS: Returns...")); 
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("structure",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSTRUCTURE: Structure..."));
        headbar.addCustomCommandSeparator();
        // Bug 72214, start
        if (mgr.isModuleInstalled("CBS"))
           headbar.addSecureCustomCommand("resheduleWoStructProj",mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERRESHEDULEWOSTRUCTPROJ: Reschedule Work Order Structure/Project..."),"Scheduling_Site_Config_API.Check_Maint_Contract_Web");
        
        headbar.addCustomCommandGroup("OBJSTATUS", mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERWOSTATUS: Work Order Status"));
        headbar.setCustomCommandGroup("reinit", "OBJSTATUS");
        headbar.setCustomCommandGroup("observed", "OBJSTATUS");
        headbar.setCustomCommandGroup("underPrep", "OBJSTATUS");
        headbar.setCustomCommandGroup("prepared", "OBJSTATUS");
        headbar.setCustomCommandGroup("released", "OBJSTATUS");
        headbar.setCustomCommandGroup("started", "OBJSTATUS");
        headbar.setCustomCommandGroup("workDone", "OBJSTATUS");
        headbar.setCustomCommandGroup("reported", "OBJSTATUS");
        headbar.setCustomCommandGroup("finished", "OBJSTATUS");
        headbar.setCustomCommandGroup("cancelled", "OBJSTATUS");
        
        if (mgr.isModuleInstalled("PROJ"))
        {
            headbar.addCustomCommandGroup("PROJCONNGRP", mgr.translate("ACTSEPREPPROJCONNGRP: Project Connection"));
            headbar.setCustomCommandGroup("connectToActivity", "PROJCONNGRP");
            headbar.setCustomCommandGroup("disconnectFromActivity", "PROJCONNGRP");
            headbar.setCustomCommandGroup("projectActivityInfo", "PROJCONNGRP");
            headbar.setCustomCommandGroup("activityInfo", "PROJCONNGRP");
        }

        headbar.addCustomCommandGroup("AIRWORTHINESS", mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERAIRWOTHINESS: Airworthiness Release Certificate"));
        headbar.setCustomCommandGroup("printAuthRelCerti", "AIRWORTHINESS"); 
        headbar.setCustomCommandGroup("printReleaseCertif", "AIRWORTHINESS");
        
        headbar.addCustomCommandGroup("RECIEVEORD", mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERRECIEORDHANDLE: Receive Order Handling"));
        headbar.setCustomCommandGroup("createRecieveOrder","RECIEVEORD");
        if (mgr.isPresentationObjectInstalled("purchw/MroObjectReceiveOrder.page")) 
           headbar.setCustomCommandGroup("receiveOrder","RECIEVEORD");
        headbar.setCustomCommandGroup("returnObject","RECIEVEORD");
        headbar.setCustomCommandGroup("returnNonOperationalObj","RECIEVEORD");


        headbar.addCommandValidConditions("moveNonSerial", "NON_SERIAL_LOCATION_DB", "Disable", "I;N;S;null", false);
        headbar.appendCommandValidConditions("moveNonSerial", "WO_NO", "Disable", "null");

        headbar.addCommandValidConditions("transferToCusOrder", "WO_NO", "Disable", "null", false);
        headbar.appendCommandValidConditions("transferToCusOrder", "CUSTOMER_NO", "Disable", "null");

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

        headbar.addCommandValidConditions("printAuthRelCerti",   "OBJSTATE",  "Enable",  "FAULTREPORT;WORKREQUEST;STARTED;WORKDONE;REPORTED");

        headbar.addCommandValidConditions("printReleaseCertif",   "OBJSTATE",  "Enable",  "FAULTREPORT;WORKREQUEST;STARTED;WORKDONE;REPORTED");
        headbar.appendCommandValidConditions("printReleaseCertif", "RELEASE_CERTIFICATE", "Disable", "FALSE");

        headbar.addCustomCommand("refreshForm","");
	headbar.addCustomCommand("refreshMaterialTab","");


        // Bug 72214, start
        // Bug 76867, start
        if (mgr.isModuleInstalled("CBS")){
            headbar.addCommandValidConditions("resheduleWoStructProj","OBJSTATE","Enable","PREPARED;RELEASED;STARTED");      
            headbar.appendCommandValidConditions("resheduleWoStructProj","ENABLE_RESCHEDULE", "Disable", "FALSE");
        }
        // Bug 76867, end
        // Bug 72214, end

        //Web Alignment - Multirow Action
        headbar.enableMultirowAction();
        headbar.removeFromMultirowAction("createRecieveOrder");
        headbar.removeFromMultirowAction("returnObject");
        headbar.removeFromMultirowAction("returnNonOperationalObj");
        headbar.removeFromMultirowAction("preposting");
        headbar.removeFromMultirowAction("placeSerial");
        headbar.removeFromMultirowAction("moveSerial");
        headbar.removeFromMultirowAction("moveNonSerial");
        headbar.removeFromMultirowAction("printAuth");
        headbar.removeFromMultirowAction("changeConditionCode");
        headbar.removeFromMultirowAction("freeNotes");
        headbar.removeFromMultirowAction("postings");
        headbar.removeFromMultirowAction("requisitions");
        headbar.removeFromMultirowAction("permits");
        headbar.removeFromMultirowAction("coInformation");
        headbar.removeFromMultirowAction("workCenterLoad");
        headbar.removeFromMultirowAction("maintTask");
        headbar.removeFromMultirowAction("returns");
        headbar.removeFromMultirowAction("structure");

        // The RMB cancelled is allowed only in single row selection because 
        // the state change requires a data entry as a prerequisite. Check the method cancelled.
        headbar.removeFromMultirowAction("cancelled");
        // 
//  	  Bug 83532, Start
        headbar.removeFromMultirowAction("workorderaddr");
//		  Bug 83532, End

        if (mgr.isModuleInstalled("PROJ"))
        {
            headbar.removeFromMultirowAction("connectToActivity");
            headbar.removeFromMultirowAction("disconnectFromActivity");
            headbar.removeFromMultirowAction("projectActivityInfo");
            headbar.removeFromMultirowAction("activityInfo");

            headbar.defineCommand("connectToActivity","connectToActivity","connectToActivityClient");
            headbar.defineCommand("disconnectFromActivity","disconnectFromActivity","disconnectFromActivityClient");
        }

        // Bug 72214, start
        if (mgr.isModuleInstalled("CBS"))
           headbar.removeFromMultirowAction("resheduleWoStructProj");
        // Bug 72214, end
        
        headbar.defineCommand("prepared","prepared","preparedClient(i)");

        headbar.disableCommand(headbar.NEWROW);
        headbar.disableCommand(headbar.DUPLICATEROW);



        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        //Bug 67871, Start
        //headlay.setFieldOrder("MCH_CODE,MCH_CODE_DESCRIPTION,MCH_CODE_CONTRACT,CONNECTION_TYPE,CONTRACT,CURRENTPOSITION,CRITICALITY,CRITICALITYDESCRIPTION,WO_NO,ERR_DESCR,ORG_CODE,REG_DATE,STATE,REPORTED_BY,CBWARRANTYONOBJECT,CBHASSTRUCTURE,CBHASDOCUMENTS,CBTRANSTOMOB,RELEASE_CERTIFICATE,WORK_DONE,PERFORMED_ACTION_LO,ERR_CAUSE_LO,ERR_CLASS,ERRCLASSDESCR,ERR_TYPE,ERRTYPEDESC,PERFORMED_ACTION_ID,PERFORMEDACTIONDESC,ERR_CAUSE,ERRCAUSEDESC,REAL_S_DATE,REAL_F_DATE,ERR_DISCOVER_CODE,DISCODESCR,ERR_SYMPTOM,SYMPTOMDESCR,ERR_DESCR_LO,TEST_POINT_ID,TESTPOINTIDDES,PM_NO,PM_DESCR");
        //Bug 67871, End
        // 107820
	//Bug 87766, Start, Added CBINASTRUCTURE
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERGRPLABEL1: General"),"WO_NO,CONTRACT,ERR_DESCR,STATE,CONNECTION_TYPE,MCH_CODE,MCH_CODE_DESCRIPTION,MCH_CODE_CONTRACT,PM_NO,ORG_CODE,ORGCODEDESCRIPTION,CURRENTPOSITION,CRITICALITY,CRITICALITYDESCRIPTION,CBWARRANTYONOBJECT,CBHASSTRUCTURE,CBHASDOCUMENTS,CBINASTRUCTURE,RELEASE_CERTIFICATE,CBTRANSTOMOB",true,true);
	//Bug 87766, End
        //Bug 74288, Start
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERGRPLABEL2: Report In"),"ERR_CLASS,ERRCLASSDESCR,ERR_TYPE,ERRTYPEDESC,PERFORMED_ACTION_ID,PERFORMEDACTIONDESC,ERR_CAUSE,ERRCAUSEDESC,ERR_CAUSE_LO,WORK_DONE,TEST_POINT_ID,TESTPOINTIDDES,PERFORMED_ACTION_LO,REAL_S_DATE,REQUIRED_START_DATE,REAL_F_DATE,REQUIRED_END_DATE",true,false);
        //headlay.defineGroup(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERGRPLABEL3: Detailed Info on Work Done"),"ERR_CAUSE_LO,PERFORMED_ACTION_LO",true,false);
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERGRPLABEL4: Fault Report Info"),"REG_DATE,REPORTED_BY,REP_BY_NAME,ERR_DISCOVER_CODE,DISCODESCR,ERR_SYMPTOM,SYMPTOMDESCR,ERR_DESCR_LO",true,false);
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERGRPLABEL5: PM Information"),"NOTE,CB_GENERATE_NOTE,CMB_GENERATE_NOTE",true,false);

        //Bug 74288, End

        headlay.setDialogColumns(2);

        headlay.setSimple("MCH_CODE_DESCRIPTION");
        headlay.setSimple("CRITICALITYDESCRIPTION");
        headlay.setSimple("ERRCLASSDESCR");
        headlay.setSimple("ERRTYPEDESC");
        headlay.setSimple("PERFORMEDACTIONDESC");
        headlay.setSimple("ERRCAUSEDESC");
        headlay.setSimple("ORGCODEDESCRIPTION");
        headlay.setSimple("REP_BY_NAME");
        headlay.setSimple("DISCODESCR");
        headlay.setSimple("SYMPTOMDESCR");
        headlay.setSimple("TESTPOINTIDDES");

        tabs = mgr.newASPTabContainer();
        tabs.setTabSpace(5);  
        tabs.setTabWidth(100);

        tabs.addTab(mgr.translate("ACTSEPREPWOBUDGET: Budget"),"javascript:commandSet('HEAD.activateBudgetTab','')");
        tabs.addTab(mgr.translate("ACTSEPREPWOTIMEREP: Time Report"),"javascript:commandSet('HEAD.activateTimeReportTab','')");
        tabs.addTab(mgr.translate("ACTSEPREPWOMATERIAL: Material"),"javascript:commandSet('HEAD.activateMaterialTab','')");
        tabs.addTab(mgr.translate("ACTSEPREPWOTOOLFAC: Tools and Facilities"),"javascript:commandSet('HEAD.activateToolsnFacilitiesTab','')");
        tabs.addTab(mgr.translate("ACTSEPREPWOJOBS: Jobs"),"javascript:commandSet('HEAD.activateJobsTab','')");

        headbar.addCustomCommand("activateTimeReportTab","");
        headbar.addCustomCommand("activateMaterialTab","");
        headbar.addCustomCommand("activateToolsnFacilitiesTab","");
        headbar.addCustomCommand("activateBudgetTab","");
        headbar.addCustomCommand("activateJobsTab", "");

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

        printblk = mgr.newASPBlock("PRINT");

        f = printblk .addField("RESULT_KEY");
        f.setFunction("''");
        f.setHidden();

        printset = printblk.getASPRowSet();

        //=====================Time Report tab===================================================================


        itemblk1 = mgr.newASPBlock("ITEM1");

        f = itemblk1.addField("ITEM1_OBJID");
        f.setHidden();
        f.setDbName("OBJID");


        f = itemblk1.addField("ITEM1_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk1.addField("CRE_DATE","Date");
        f.setSize(12);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCREDATE: Creation Date");
        f.setReadOnly();
        f.setInsertable();
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_COMPANY");
        f.setHidden();
        f.setDbName("COMPANY");

        f = itemblk1.addField("EMP_NO");
        f.setSize(10);
        f.setLOV("../mscomw/MaintEmployeeLov.page","ITEM1_COMPANY COMPANY",600,450);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDEREMPNO1: Employee ID");
        f.setUpperCase();
        f.setCustomValidation("EMP_NO,ITEM1_COMPANY,CUSTOMER_NO,ITEM1_CONTRACT,CATALOG_NO,ROLE_CODE,ITEM1_ORG_CODE","EMP_NO,ROLE_CODE,EMP_SIGNATURE,NAME,ITEM1_ORG_CODE,ITEM1_CONTRACT,CMNT,CATALOG_NO,SALESPARTCATALOGDESC");
        f.setMaxLength(11);

        f = itemblk1.addField("EMP_SIGNATURE");
        f.setSize(8);
        f.setDynamicLOV("EMPLOYEE_LOV",600,445);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDEREMPSIGNATURE: Signature");
        f.setUpperCase();

        f = itemblk1.addField("NAME");
        f.setSize(16);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERNAME: Name");
        f.setFunction("Person_Info_API.Get_Name(:EMP_SIGNATURE)");
        f.setDefaultNotVisible();

        f = itemblk1.addField("PLAN_LINE_NO","Number");
        f.setSize(8);
        f.setLOV("WorkOrderRolePlanningLovLov.page","WO_NO",600,450);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDEREMPPLANLINNO: Plan Line No"); 
        f.setUpperCase();
        f.setCustomValidation("WO_NO,PLAN_LINE_NO","CRAFT_ROW,CRAFT_REFERENCE_NUMBER,TEAM_CONTRACT,TEAM_ID");

        f = itemblk1.addField("CRAFT_ROW","Number");
        f.setSize(8);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCRAFTROW: Operation No");
        f.setFunction("Work_Order_Role_API.Get_Plan_Row_No(:WO_NO, :PLAN_LINE_NO)" ) ;
        f.setReadOnly(); 

        f = itemblk1.addField("CRAFT_REFERENCE_NUMBER");
        f.setSize(25);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCRAFTREFNO: Reference Number");
        f.setFunction("Work_Order_Role_API.Get_Plan_Ref_Number(:WO_NO, :PLAN_LINE_NO)" ) ;
        f.setMaxLength(25); 
        f.setReadOnly();

        f = itemblk1.addField("ITEM1_ORG_CODE");
        f.setSize(11);
        f.setLOV("../mscomw/OrgCodeAllowedSiteLov.page","ITEM1_CONTRACT CONTRACT",600,445);
        f.setCustomValidation("ITEM1_ORG_CODE,ITEM1_WO_NO,ITEM1_CONTRACT,AMOUNT,QTY,ROLE_CODE,CATALOG_NO,SALESPARTCATALOGDESC,CMNT,NAME,EMP_NO","ITEM1_ORG_CODE,ITEM1_CONTRACT,AMOUNT,CMNT,CATALOG_NO,SALESPARTCATALOGDESC,SALESPARTCOST");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERITEM1ORGCODE: Maintenance Organization");
        f.setMandatory();
        f.setDbName("ORG_CODE");
        f.setUpperCase();
        f.setMaxLength(8);
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERORCO: List of Maintenance Organization"));

        f = itemblk1.addField("ITEM1_CONTRACT");
        f.setSize(11);
        f.setCustomValidation("ITEM1_CONTRACT","ITEM1_COMPANY");
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERITEM1CONTRACT: Maint. Org Site");
        f.setDbName("CONTRACT");
        f.setUpperCase();

        //Bug 83757,start
        f = itemblk1.addField("ITEM1_CATALOG_CONTRACT");
        f.setSize(10);
        f.setMaxLength(5);
        f.setReadOnly();
        f.setHidden();
        f.setDbName("CATALOG_CONTRACT");       
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCATACONTRACT: Sales Part Site");
        f.setUpperCase();
        //Bug 83757,End

        f = itemblk1.addField("COMPANY_VAR");
        f.setHidden();
        f.setFunction("''");

        f = itemblk1.addField("ROLE_CODE");
        f.setSize(10);
        f.setLOV("../mscomw/RoleToSiteLov.page","ITEM1_CONTRACT CONTRACT",600,445);
         //Bug 83757,start
        f.setCustomValidation("CUSTOMER_NO,ITEM1_CONTRACT,CATALOG_NO,PRICE_LIST_NO,AGREEMENT_ID,QTY1,QTY_TO_INVOICE,QTY,ITEM1_ORG_CODE,ROLE_CODE,AMOUNT,CMNT,NAME,SALESPARTCOST,LIST_PRICE,EMP_NO","ROLE_CODE,ITEM1_CONTRACT,LIST_PRICE,AMOUNT,AMOUNTSALES,CMNT,DISCOUNT,ITEM1DISCOUNTEDPRICE,ITEM1_CATALOG_CONTRACT");
        //Bug 83757,End
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERROLECODE: Craft");
        f.setUpperCase();
        f.setMaxLength(10);
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERROCO: List of Craft"));

        f = itemblk1.addField("TEAM_CONTRACT");
        f.setSize(7);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV","ITEM2_COMPANY COMPANY",600,450);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERTEAMCONT: Maint. Team Site");
        f.setMaxLength(5);
        f.setInsertable();
        f.setUpperCase();

        f = itemblk1.addField("TEAM_ID");
        f.setSize(13);
        f.setCustomValidation("TEAM_ID,TEAM_CONTRACT","TEAMDESC");
        f.setDynamicLOV("MAINT_TEAM","TEAM_CONTRACT CONTRACT",600,450);
        f.setMaxLength(20);
        f.setInsertable();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERTEAMID: Team ID");
        f.setUpperCase();

        f = itemblk1.addField("TEAMDESC");
        f.setFunction("Maint_Team_API.Get_Description(:TEAM_ID,:TEAM_CONTRACT)");
        f.setSize(40);
        f.setMaxLength(200);
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERTEAMDESC: Team Description");

        f = itemblk1.addField("CATALOG_NO");
        f.setSize(17);
        f.setDynamicLOV("SALES_PART_SERVICE_LOV","ITEM1_CONTRACT CONTRACT",600,445);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCATALOGNO: Sales Part Number");
        f.setUpperCase();
        f.setCustomValidation("QTY_TO_INVOICE,QTY,ITEM1_CONTRACT,CATALOG_NO,CUSTOMER_NO,AGREEMENT_ID,PRICE_LIST_NO,QTY1,SALESPARTCATALOGDESC,NAME,ITEM1_ORG_CODE,ROLE_CODE,EMP_NO","LIST_PRICE,AMOUNT,AMOUNTSALES,PRICE_LIST_NO,CMNT,SALESPARTCATALOGDESC,SALESPARTCOST");
        f.setReadOnly();
        f.setHidden();
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCANO: List of Sales Part Number"));

        f = itemblk1.addField("SALESPARTCATALOGDESC");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSALESPARTCATALOGDESC: Description");
        if (orderInst)
            f.setFunction("SALES_PART_API.Get_Catalog_Desc(:ITEM1_CONTRACT,:CATALOG_NO)");
        else
            f.setFunction("''");
        f.setReadOnly();
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
        f.setDynamicLOV("SALES_PRICE_LIST","ITEM1_CONTRACT CONTRACT",600,445);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERPRICELISTNO: Price List No");
        f.setUpperCase();
        f.setCustomValidation("QTY_TO_INVOICE,QTY,ITEM1_CONTRACT,CATALOG_NO,CUSTOMER_NO,AGREEMENT_ID,PRICE_LIST_NO,QTY1,LIST_PRICE","LIST_PRICE,AMOUNTSALES,PRICE_LIST_NO,AGREEMENT_PRICE_FLAG");
        f.setMaxLength(10);
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERPRLINO: List of Price List No"));
        f.setHidden();

        f = itemblk1.addField("QTY","Number");
        f.setSize(11);
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERITM1QTY: Hours");
        f.setCustomValidation("ITEM1_CONTRACT,CATALOG_NO,ROLE_CODE,ITEM1_ORG_CODE,QTY,SALESPARTCOST,QTY_TO_INVOICE,CUSTOMER_NO,AGREEMENT_ID,PRICE_LIST_NO,QTY,LIST_PRICE,EMP_NO","LIST_PRICE,AMOUNTSALES,SALESPARTCOST,AMOUNT,DISCOUNT,ITEM1DISCOUNTEDPRICE");

        f = itemblk1.addField("LIST_PRICE","Money");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERLISTPRICE: Sales Price");
        f.setCustomValidation("LIST_PRICE","AGREEMENT_PRICE_FLAG");
        f.setReadOnly();
        f.setHidden();

        f = itemblk1.addField("ITEM1DISCOUNTEDPRICE", "Money");
        f.setSize(14);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setHidden();
        f.setLabel("PCMWACTIVESEPARATE2REPORTINWORKORDERSMDISCOUNTEDPRICEITEM1: Discounted Price");
        f.setFunction("LIST_PRICE - (NVL(DISCOUNT, 0)/100*LIST_PRICE)");

        f = itemblk1.addField("SALESPARTCOST","Money","#.##");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSALESPARTCOST: Cost");
        //Bug 83757,Start
        f.setFunction("WORK_ORDER_PLANNING_UTIL_API.Get_Cost(:ITEM1_ORG_CODE,:ROLE_CODE,:ITEM1_CATALOG_CONTRACT,:CATALOG_NO,:ITEM1_CONTRACT,:EMP_NO,'TRUE')");
        //Bug 83757,End
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk1.addField("AMOUNT","Money","#.##");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERAMOUNT: Cost Amount");
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = itemblk1.addField("AMOUNTSALES","Money","#.##");
        f.setSize(17);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERAMOUNTSALES: Price Amount");
        f.setFunction("(LIST_PRICE*QTY)");
        f.setReadOnly();
        f.setHidden();

        f = itemblk1.addField("CMNT");
        f.setCustomValidation("CMNT","CMNT");
        f.setSize(30);
        f.setHeight(4);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCMNT: Comment");
        f.setDefaultNotVisible();
        f.setMaxLength(80);

        //Bug 83757,Start
        f = itemblk1.addField("ALTERNATIVE_CUSTOMER");
        f.setDynamicLOV("CUSTOMER_INFO");
        f.setSize(20);
        f.setUpperCase();
        f.setHidden();
        f.setCustomValidation("ALTERNATIVE_CUSTOMER","AGREEMENT_PRICE_FLAG");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERALTERNATIVECUSTOMER: Alternative Customer");
        //Bug 83757,End

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
        f.setSize(11);
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

        f = itemblk1.addField("DISCOUNT","Number");
        f.setFunction("''");
        f.setHidden();

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

        itemtbl1 = mgr.newASPTable(itemblk1);
        itemtbl1.setWrap();


        itembar1.addCustomCommand("createFromPlanTime",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCREFROMPLANNING: Create From Planning..."));
        itembar1.addCustomCommand("connectToPlanTime",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCONNRECOFROPLAN: Connect/Reconnect To Planning..."));
        itembar1.addCustomCommand("createFromAllocation",mgr.translate("PCMWACTIVESEPREPORTINWOCREATETIMEALLOC: Create From Allocation..."));

        itembar1.addCommandValidConditions("createFromAllocation",   "IS_SEPARATE",  "Enable",  "TRUE");

        itembar1.forceEnableMultiActionCommand("createFromPlanTime");
        itembar1.forceEnableMultiActionCommand("createFromAllocation");

        //Web Alignment - Multirow Action
        // 031224  ARWILK  Begin  (Links with multirow RMB's)
        itembar1.enableMultirowAction();
        itembar1.removeFromMultirowAction("connectToPlanTime");
        // 031224  ARWILK  End  (Links with multirow RMB's)

        itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
        itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1");
        itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");

        itembar1.defineCommand(itembar1.SAVERETURN,"saveReturnItem1","checkMando()");
        //Bug id 81023, start
        itembar1.defineCommand(itembar1.SAVENEW,"saveNewItem1","checkMando()");
        itembar1.defineCommand(itembar1.DELETE, "deleteITEM1");
        //Bug id 81023, end

        itemlay1 = itemblk1.getASPBlockLayout();
        itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);
        itemlay1.setDialogColumns(3);

        // ------------------------------------------------------------------------------------------------------
        // ------------------------------------------------------------------------------------------------------
        // ------------------------------------------------------------------------------------------------------

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
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERITEM2CONTRACT: Site");
        f.setDbName("CONTRACT");
        f.setUpperCase();

        f = itemblk2.addField("ITEM2_COMPANY");
        f.setHidden();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERITEM2COMPANY: Company Id");
        f.setDbName("COMPANY");

        f = itemblk2.addField("ITEM2_WORK_ORDER_COST_TYPE");
        f.setSize(11);
        f.setMandatory();
        f.setSelectBox();
        f.enumerateValues("WORK_ORDER_COST_TYPE_API");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERITEM2WORKORDETTYPE: Cost Type");
        f.setDbName("WORK_ORDER_COST_TYPE");
        f.setCustomValidation("ITEM2_WORK_ORDER_COST_TYPE,ITEM2_WO_NO","ITEM2_AMOUNT,CLIENTVAL1,CLIENTVAL2,ACTIVEWORKORDERFIXEDPRICE,NOTFIXEDPRICE,ITEM2_COST_CENTER,ITEM2_OBJECT_NO,ITEM2_PROJECT_NO");


        f = itemblk2.addField("ITEM2_CATALOG_NO");
        f.setSize(17);
        f.setLOV("../orderw/SalesPartLov.page","CONTRACT",600,445);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERITEM2CATALOGNO: Sales Part Number");
        f.setDbName("CATALOG_NO");
        f.setUpperCase();
        f.setCustomValidation("ITEM2_CATALOG_NO,ITEM2_PRICE_LIST_NO,ITEM2_QTY,ITEM2_WORK_ORDER_COST_TYPE,ITEM2_CONTRACT,CUSTOMER_NO,AGREEMENT_ID,ITEM2_AMOUNT","LINEDESCRIPTION,STRING,ITEM2_AMOUNT");
        f.setMaxLength(25);
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCATNO: List of Sales Part Number"));

        f = itemblk2.addField("LINEDESCRIPTION");
        f.setSize(16);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERLINEDESCRIPTION: Description");
        f.setReadOnly();
        if (orderInst)
            f.setFunction("substr(nvl(LINE_DESCRIPTION, Sales_Part_API.Get_Catalog_Desc(CONTRACT,CATALOG_NO)), 1, 35)");
        else
            f.setFunction("LINE_DESCRIPTION");

        f = itemblk2.addField("ORDER_NO");
        f.setSize(16);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERITEM2CUSORDNO: Customer Order No");
        f.setReadOnly();
        f.setUpperCase();

        f = itemblk2.addField("ITEM2_PRICE_LIST_NO");
        f.setSize(13);
        f.setLOV("../orderw/SalesPriceListLov.page",600,445);
        f.setCustomValidation("ITEM2_PRICE_LIST_NO,ITEM2_QTY,ITEM2_CONTRACT,ITEM2_CATALOG_NO,CUSTOMER_NO,AGREEMENT_ID","SALESPRICEAMOUNT,ITEM2_AGREEMENT_PRICE_FLAG");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERITEM2PRICELISTNO: Price List No");
        f.setDbName("PRICE_LIST_NO");
        f.setUpperCase();
        f.setMaxLength(10);
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERPRLINO2: List of Price List No"));

        f = itemblk2.addField("ITEM2_QTY","Number");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERITEM2QTY: Hours/Qty");
        f.setCustomValidation("ITEM2_PRICE_LIST_NO,ITEM2_QTY,ITEM2_WORK_ORDER_COST_TYPE,ITEM2_CONTRACT,ITEM2_CATALOG_NO,CUSTOMER_NO,AGREEMENT_ID","SALESPRICEAMOUNT");
        f.setDbName("QTY");

        f = itemblk2.addField("ITEM2_LIST_PRICE","Number");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERITEM2LISTPRICE: Sales Price");
        f.setCustomValidation("ITEM2_LIST_PRICE,ITEM2_QTY","SALESPRICEAMOUNT,ITEM2_AGREEMENT_PRICE_FLAG");
        f.setDbName("LIST_PRICE");

        f = itemblk2.addField("ITEM2_WORK_ORDER_ACCOUNT_TYPE");
        f.setSize(12);
        f.setHidden();
        f.setDbName("WORK_ORDER_ACCOUNT_TYPE");

        f = itemblk2.addField("SALESPRICEAMOUNT","Number");
        f.setSize(17);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSALESPRICEAMOUNT: Sales Price Amount");
        f.setFunction("(LIST_PRICE*QTY)");
        f.setReadOnly();

        f = itemblk2.addField("ITEM2_AMOUNT","Number");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERITEM2AMOUNT: Cost Amount");
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
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERITEM2USEPRICELOGIC: Use Price Logic");
        f.setCheckBox("0,1");
        f.setDbName("AGREEMENT_PRICE_FLAG");
        f.setReadOnly();

        f = itemblk2.addField("KEEP_REVENUE");
        f.setSize(14);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERKEEPREVENUE: Keep Revenue");
        f.setSelectBox();
        f.enumerateValues("KEEP_REVENUE_API");

        f = itemblk2.addField("ITEM2_WORK_ORDER_BOOK_STATUS");
        f.setSize(13);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERITEM2WORKORDERBOOKSTATUS: Booking Status");
        f.setDbName("WORK_ORDER_BOOK_STATUS");
        f.setReadOnly();

        f = itemblk2.addField("SIGNATURE");
        f.setSize(13);
        f.setLOV("../mscomw/EmployeeLovLov.page","ITEM2_COMPANY COMPANY",600,445);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERAUTHSIGNATURE: Auth Signature");
        f.setUpperCase();
        f.setMaxLength(20);
        f.setReadOnly();

        f = itemblk2.addField("SIGNATURE_ID");
        f.setSize(13);
        f.setHidden();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSIGNATUREID: Auth Signature");
        f.setUpperCase();

        f = itemblk2.addField("ITEM2_CMNT");
        f.setSize(19);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERITEM2CMNT: Comment");
        f.setDbName("CMNT");
        f.setMaxLength(80);

        f = itemblk2.addField("ACCNT");
        f.setSize(11);
        f.setLOV("../accruw/AccountLov.page","ITEM2_COMPANY COMPANY",600,445);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERACCNT: Account");
        f.setMaxLength(10);

        f = itemblk2.addField("ITEM2_COST_CENTER");
        f.setSize(11);
        f.setLOV("../accruw/CodeBLov.page","ITEM2_COMPANY COMPANY",600,445);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERITEM2COSTCENTER: Cost Center");
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
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCDOCUMENTTEXT: Document Text");
        f.setFunction("Document_Text_API.Note_Id_Exist(:NOTE_ID)");
        f.setCheckBox("0,1");
        f.setReadOnly();

        f = itemblk2.addField("REQUISITION_NO");
        f.setSize(13);
        f.setLOV("WorkOrderRequisHeaderLov.page","WO_NO",600,445);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERREQUISITIONNO: Requisition No");
        f.setMaxLength(12);
        f.setReadOnly();

        f = itemblk2.addField("REQUISITION_LINE_NO");
        f.setSize(16);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERREQUISITIONLINENO: Requisition Line No");
        f.setMaxLength(4);

        f = itemblk2.addField("REQUISITION_RELEASE_NO");
        f.setSize(21);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERREQUISITIONRELEASENO: Requisition Release No");
        f.setMaxLength(4);

        f = itemblk2.addField("REQUISITIONVENDORNO");
        f.setSize(20);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERREQUISITIONVENDORNO: Requisition Supplier No");
        f.setFunction("Purchase_Req_Util_API.Get_Line_Vendor_No (:REQUISITION_NO,:REQUISITION_LINE_NO,:REQUISITION_RELEASE_NO)");
        mgr.getASPField("REQUISITION_NO").setValidation("REQUISITIONVENDORNO");

        f = itemblk2.addField("ITEM2_EMP_NO");
        f.setSize(9);
        f.setHidden();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERITEM2EMPNO: Employee");
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
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCATALOGDESC: Description");
        if (orderInst)
            f.setFunction("SALES_PART_API.Get_Catalog_Desc(:ITEM2_CONTRACT,:ITEM2_CATALOG_NO)");
        else
            f.setFunction("''");

        f = itemblk2.addField("NOTE_ID","Number");
        f.setSize(11);
        f.setHidden();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERNOTEID: Note ID");

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
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERACTIVEWORKORDERFIXEDPRICE: Fixed Price");
        f.setFunction("ACTIVE_WORK_ORDER_API.Get_Fixed_Price(:ITEM2_WO_NO)");

        f = itemblk2.addField("NOTFIXEDPRICE");
        f.setSize(11);
        f.setHidden();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERNOTFIXEDPRICE: Not Fixed Price");
        f.setFunction("Fixed_Price_API.Decode('1')");

        itemblk2.setView("WORK_ORDER_CODING");
        itemblk2.defineCommand("WORK_ORDER_CODING_API","New__,Modify__,Remove__");
        itemset2 = itemblk2.getASPRowSet();

        itembar2 = mgr.newASPCommandBar(itemblk2);
        itembar2.setBorderLines(true,true);

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
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERMAINT_MATERIAL_ORDER_NO: Order No");

        f = itemblk3.addField("ITEM3_WO_NO","Number","#");
        f.setSize(11);
        f.setDbName("WO_NO");
        f.setMaxLength(8);
        f.setReadOnly();
        f.setCustomValidation("ITEM3_WO_NO","DUE_DATE,NPREACCOUNTINGID,ITEM3_CONTRACT,ITEM3_COMPANY,MCHCODE,ITEM3DESCRIPTION");
        f.setDynamicLOV("ACTIVE_WO_NOT_REPORTED",600,445);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERWO_NO: WO No");

        f = itemblk3.addField("MCHCODE");
        f.setSize(13);
        f.setHidden();
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERMCHCODE: Object ID");
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
        f.setDynamicLOV("EMPLOYEE_LOV","ITEM3_COMPANY COMPANY",600,445);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSIGNATURE: Signature");
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
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCONTRACT: Site");
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setUpperCase();
        f.setInsertable();
        f.setMandatory();

        f = itemblk3.addField("ENTERED","Date");
        f.setSize(15);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERENTERED: Entered");

        f = itemblk3.addField("INT_DESTINATION_ID");
        f.setSize(8);
        f.setUpperCase();
        f.setMaxLength(30);
        f.setCustomValidation("INT_DESTINATION_ID,ITEM3_CONTRACT","INT_DESTINATION_DESC");
        f.setDynamicLOV("INTERNAL_DESTINATION_LOV","ITEM3_CONTRACT CONTRACT",600,445);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERINT_DESTINATION_ID: Int Destination");

        f = itemblk3.addField("INT_DESTINATION_DESC");
        f.setSize(15);
        f.setDefaultNotVisible();
        f.setMaxLength(2000);

        f = itemblk3.addField("DUE_DATE","Date");
        f.setSize(15);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERDUE_DATE: Due Date");

        f = itemblk3.addField("ITEM3_STATE");
        f.setSize(10);
        f.setDbName("STATE");
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSTATE: Status");

        f = itemblk3.addField("NREQUISITIONVALUE", "Number");
        f.setSize(8);
        f.setHidden();
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERNREQUISITIONVALUE: Total Value");
        f.setFunction("''");

        f = itemblk3.addField("SNOTETEXT");
        f.setHidden();
        f.setFunction("''");

        f = itemblk3.addField("ITEM3_SIGNATURE_ID");
        f.setDbName("SIGNATURE_ID");
        f.setInsertable();
        f.setHidden();
        f.setUpperCase();

        f = itemblk3.addField("NNOTEID", "Number");
        f.setInsertable();
        f.setHidden();
        f.setFunction("''");

        f = itemblk3.addField("ORDERCLASS");
        f.setHidden();
        f.setFunction("''");

        f = itemblk3.addField("SNOTEIDEXIST");
        f.setHidden();
        f.setFunction("''");

        f = itemblk3.addField("ITEM3_COMPANY");
        f.setHidden();
        f.setFunction("Site_API.Get_Company(:ITEM3_CONTRACT)");
        f.setUpperCase();

        f = itemblk3.addField("NPREACCOUNTINGID", "Number");
        f.setHidden();
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
        f.setLabel("ACTSEPREPINWOITEM3MULMATREQEXIST: Multiple Material Requisitions Exist"); 
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
        itembar3.addCustomCommand("plan",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERPLANCONS: Plan"));
        itembar3.addCustomCommand("release",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERRELEA: Release"));
        itembar3.addCustomCommand("close",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCLOS: Close"));
        itembar3.addCustomCommandSeparator();
        itembar3.addCustomCommand("prePostHead",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERPREPOSTHEAD: Pre Posting Header..."));
        itembar3.addCustomCommand("printPicList",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERPICLSTMAT: Pick List For Material Requistion - Printout..."));
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
        itembar3.defineCommand(itembar3.BACK,"backITEM"); 

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
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERLINE_ITEM_NO: Line No");

        f = itemblk.addField("PART_NO");
        f.setSize(14);
        f.setReadOnly();
        f.setInsertable();
        f.setMaxLength(25);
        f.setHyperlink("../invenw/InventoryPart.page","PART_NO","NEWWIN"); 
        f.setCustomValidation("PART_NO,SPARE_CONTRACT,CATALOG_CONTRACT,OWNER,ITEM5_CONDITION_CODE,SUPPLY_CODE,ITEM5_MAINT_MATERIAL_ORDER_NO,PART_OWNERSHIP,ITEM_WO_NO","ITEM5_CATALOG_NO,ITEM5CATALOGDESC,SCODEA,SCODEB,SCODEC,SCODED,SCODEE,SCODEF,SCODEG,SCODEH,SCODEI,SCODEJ,HASSPARESTRUCTURE,DIMQTY,TYPEDESIGN,QTYONHAND,UNITMEAS,ITEM5_CONDITION_CODE,CONDDESC,QTY_AVAILABLE,ACTIVEIND_DB,PART_OWNERSHIP,PART_OWNERSHIP_DB,OWNER");     
        f.setDynamicLOV("INVENTORY_PART_WO_LOV","SPARE_CONTRACT CONTRACT",600,445);
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERPART_NO: Part No");
        f.setUpperCase();

        f = itemblk.addField("SPAREDESCRIPTION");
        f.setSize(20);
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSPAREDESCRIPTION: Part Description");
        f.setFunction("INVENTORY_PART_API.Get_Description(:SPARE_CONTRACT,:PART_NO)");

        f = itemblk.addField("ITEM5_CONDITION_CODE");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCONDITIONCODE: Condition Code");
        f.setSize(15);
        f.setDbName("CONDITION_CODE");
        f.setDynamicLOV("CONDITION_CODE",600,445);
        f.setUpperCase(); 
        f.setCustomValidation("ITEM5_CONDITION_CODE,PART_NO,SPARE_CONTRACT,OWNER,PART_OWNERSHIP_DB,SUPPLY_CODE,ITEM5_MAINT_MATERIAL_ORDER_NO,ITEM3_ACTIVITY_SEQ,PART_OWNERSHIP,PLAN_QTY","CONDDESC,QTYONHAND,QTY_AVAILABLE,ITEM_COST,AMOUNTCOST");  

        f = itemblk.addField("CONDDESC");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCONDDESC: Condition Code Description");
        f.setSize(20);
        f.setMaxLength(50);
        f.setReadOnly();
        f.setFunction("CONDITION_CODE_API.Get_Description(:ITEM5_CONDITION_CODE)"); 

        f = itemblk.addField("PART_OWNERSHIP");
        f.setSize(25);
        f.setInsertable();
        f.setSelectBox();
        f.enumerateValues("PART_OWNERSHIP_API");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERPARTOWNERSHIP: Ownership"); 
        f.setCustomValidation("PART_OWNERSHIP,PART_NO,SPARE_CONTRACT,OWNER,ITEM5_CONDITION_CODE,SUPPLY_CODE,ITEM5_MAINT_MATERIAL_ORDER_NO","PART_OWNERSHIP_DB,QTYONHAND,QTY_AVAILABLE"); 

        f = itemblk.addField("PART_OWNERSHIP_DB");
        f.setSize(20);
        f.setHidden();

        f = itemblk.addField("OWNER");
        f.setSize(15);
        f.setMaxLength(20);
        f.setInsertable();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERPARTOWNER: Owner"); 
        f.setCustomValidation("OWNER,PART_OWNERSHIP_DB,ITEM5_CONDITION_CODE,PART_NO,SPARE_CONTRACT,SUPPLY_CODE,ITEM5_MAINT_MATERIAL_ORDER_NO,PART_OWNERSHIP,ITEM_WO_NO","OWNER_NAME,WO_CUST,QTYONHAND,QTY_AVAILABLE");     
        f.setDynamicLOV("CUSTOMER_INFO");
        f.setUpperCase();

        f = itemblk.addField("WO_CUST");
        f.setHidden();
        f.setFunction("ACTIVE_SEPARATE_API.Get_Customer_No(:ITEM3_WO_NO)");

        f = itemblk.addField("OWNER_NAME");
        f.setSize(20);
        f.setMaxLength(100);
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERPARTOWNERNAME: Owner Name");
        f.setFunction("''");

        f = itemblk.addField("SPARE_CONTRACT");
        f.setSize(11);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(5);
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSPARE_CONTRACT: Site");
        f.setUpperCase();

        f = itemblk.addField("HASSPARESTRUCTURE");
        f.setSize(8);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERHASSPARESTRUCTURE: Structure");
        f.setFunction("Equipment_Spare_Structure_API.Has_Spare_Structure_Boolean(:PART_NO,:SPARE_CONTRACT)");

        f = itemblk.addField("ITEM_JOB_ID", "Number");
        f.setDbName("JOB_ID");
        f.setSize(8);
        f.setInsertable();
        f.setDefaultNotVisible();
        f.setDynamicLOV("WORK_ORDER_JOB", "ITEM_WO_NO WO_NO");
        f.setLabel("PCMWACTIVESEPERATEREPWOITEMJOBID: Job Id");

        f = itemblk.addField("CRAFT_LINE_NO","Number");
        f.setSize(15);
        f.setDynamicLOV("WORK_ORDER_ROLE","WO_NO",600,445);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCRAFTLINENO: Craft Line No");
        f.setDefaultNotVisible();   

        f = itemblk.addField("DIMQTY");
        f.setSize(11);
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setDefaultNotVisible();   
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERDIMQTY: Dimension/ Quality");
        f.setFunction("INVENTORY_PART_API.Get_Dim_Quality(:SPARE_CONTRACT,:PART_NO)");

        f = itemblk.addField("TYPEDESIGN");
        f.setSize(15);
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERTYPEDESIGN: Type Designation");
        f.setFunction("INVENTORY_PART_API.Get_Type_Designation(:SPARE_CONTRACT,:PART_NO)");

        f = itemblk.addField("DATE_REQUIRED","Date");
        f.setSize(13);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERDATE_REQUIRED: Date Required");
        f.setCustomValidation("DATE_REQUIRED","DATE_REQUIRED");

        f = itemblk.addField("SUPPLY_CODE");
        f.setSize(25);
        f.setMandatory();
        f.setInsertable();
        f.setSelectBox();
        f.setCustomValidation("PART_NO,SPARE_CONTRACT,CATALOG_CONTRACT,OWNER,ITEM5_CONDITION_CODE,SUPPLY_CODE,ITEM5_MAINT_MATERIAL_ORDER_NO,ITEM3_ACTIVITY_SEQ","QTYONHAND,QTY_AVAILABLE");
        f.enumerateValues("MAINT_MAT_REQ_SUP_API");
        f.setLabel("PCMWACTIVESEPARATE2REPORTINWORKORDERSUPPLYCODE: Supply Code");
        f.setMaxLength(200);

        f = itemblk.addField("PLAN_QTY","Number");
        f.setSize(14);
        f.setCustomValidation("PLAN_QTY,PART_NO,SPARE_CONTRACT,ITEM5_CATALOG_NO,CATALOG_CONTRACT,ITEM5_PRICE_LIST_NO,ITEM5_PLAN_LINE_NO,ITEM_DISCOUNT,ITEM_WO_NO,ITEM_COST,ITEM5_LIST_PRICE,ITEMDISCOUNTEDPRICE,SERIAL_NO,CONFIGURATION_ID,ITEM5_CONDITION_CODE,PART_OWNERSHIP_DB","ITEM_COST,AMOUNTCOST,ITEM5_PRICE_LIST_NO,ITEM_DISCOUNT,ITEM5_LIST_PRICE,ITEM5SALESPRICEAMOUNT,ITEMDISCOUNTEDPRICE");
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERPLAN_QTY: Quantity Required");

        f = itemblk.addField("ITEM_QTY","Number");
        f.setSize(13);
        f.setReadOnly();
        f.setDbName("QTY");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERITMQTY: Quantity Issued");

        f = itemblk.addField("QTY_SHORT","Number");
        f.setSize(11);
        f.setMandatory();
        f.setHidden();
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERQTY_SHORT: Quantity Short");

        f = itemblk.addField("QTYONHAND","Number");
        f.setSize(17);
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERQTYONHAND: Quantity on Hand");
        if (mgr.isModuleInstalled("INVENT"))
            f.setFunction("Maint_Material_Req_Line_Api.Get_Qty_On_Hand(:ITEM5_MAINT_MATERIAL_ORDER_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''"); 

        //Bug 76767, Start, Modified the function call
        f = itemblk.addField("QTY_AVAILABLE","Number");
        f.setSize(17);
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERQTY_AVAILABLE: Quantity Available");
        if (mgr.isModuleInstalled("INVENT"))
            f.setFunction("MAINT_MATERIAL_REQ_LINE_API.Get_Qty_Available(:ITEM5_MAINT_MATERIAL_ORDER_NO,:LINE_ITEM_NO)");
        else
           f.setFunction("''"); 
        //Bug 76767, End

        f = itemblk.addField("QTY_ASSIGNED","Number");
        f.setSize(11);
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERQTY_ASSIGNED: Quantity Assigned");

        f = itemblk.addField("QTY_RETURNED","Number");
        f.setSize(11);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERQTY_RETURNED: Quantity Returned");

        f = itemblk.addField("UNITMEAS");
        f.setSize(11);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERUNITMEAS: Unit");
        f.setFunction("Purchase_Part_Supplier_API.Get_Unit_Meas(:SPARE_CONTRACT,:PART_NO)");

        f = itemblk.addField("CATALOG_CONTRACT");
        f.setSize(10);
        f.setMaxLength(5);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCATALOG_CONTRACT: Sales Part Site");
        f.setUpperCase();

        f = itemblk.addField("ITEM5_CATALOG_NO");
        f.setSize(9);
        f.setDbName("CATALOG_NO");
        f.setMaxLength(25);
        f.setDefaultNotVisible();
        f.setCustomValidation("ITEM5_CATALOG_NO,ITEM_WO_NO,CATALOG_CONTRACT,ITEM5_PRICE_LIST_NO,PLAN_QTY,ITEM5_PLAN_LINE_NO,PART_NO,SPARE_CONTRACT,ITEM_COST,PLAN_QTY,ITEM_DISCOUNT","ITEM5_LIST_PRICE,ITEM_COST,AMOUNTCOST,ITEM5CATALOGDESC,ITEM_DISCOUNT,ITEM5SALESPRICEAMOUNT,ITEMDISCOUNTEDPRICE,ITEM5_PRICE_LIST_NO,SALES_PRICE_GROUP_ID");
        if (orderInst)
            f.setDynamicLOV("SALES_PART_ACTIVE_LOV","CATALOG_CONTRACT CONTRACT",600,445);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCATALOG_NO: Sales Part Number");
        f.setUpperCase();

        f = itemblk.addField("ITEM5CATALOGDESC");
        f.setSize(17);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSALESCATALOGDESC: Sales Part Description");
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
        if (orderInst)
            f.setDynamicLOV("SALES_PRICE_LIST","SALES_PRICE_GROUP_ID,CURRENCEY_CODE",600,445);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERPRICE_LIST_NO: Price List No");
        f.setUpperCase();

        f = itemblk.addField("ITEM5_LIST_PRICE","Money");
        f.setSize(9);
        f.setDbName("LIST_PRICE");
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERLIST_PRICE: Sales Price");

        f = itemblk.addField("ITEM_DISCOUNT","Number");
        f.setSize(14);
        f.setCustomValidation("ITEM_DISCOUNT,ITEM5_LIST_PRICE,PLAN_QTY","ITEM5SALESPRICEAMOUNT,ITEMDISCOUNTEDPRICE");
        f.setDefaultNotVisible();
        f.setDbName("DISCOUNT");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERDISCOUNT: Discount %");

        f = itemblk.addField("ITEMDISCOUNTEDPRICE", "Money");
        f.setSize(14);
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2REPORTINWORKORDERDISCOUNTEDPRICE: Discounted Price");
        f.setFunction("LIST_PRICE - (NVL(DISCOUNT, 0)/100*LIST_PRICE)");

        f = itemblk.addField("ITEM5SALESPRICEAMOUNT", "Money");
        f.setSize(14);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERPRICEAMOUNT: Price Amount");
        f.setFunction("''");

        f = itemblk.addField("ITEM_COST", "Money");
        f.setSize(11);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERCOST: Cost");
        f.setFunction("''");

        f = itemblk.addField("AMOUNTCOST", "Money");
        f.setSize(11);
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERAMOUNTCOST: Cost Amount");
        f.setFunction("''");
        f.setReadOnly();

        f = itemblk.addField("SCODEA");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSCODEA: Account");
        f.setFunction("''");
        f.setReadOnly();
        f.setHidden();
        f.setDefaultNotVisible();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEB");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSCODEB: Cost Center");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setHidden();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEF");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSCODEF: Project No");
        f.setFunction("''");
        f.setReadOnly();
        f.setHidden();
        f.setDefaultNotVisible();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEE");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSCODEE: Object No");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setHidden();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEC");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSCODEC: Code C");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setHidden();
        f.setMaxLength(10);

        f = itemblk.addField("SCODED");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSCODED: Code D");
        f.setFunction("''");
        f.setReadOnly();
        f.setHidden();
        f.setDefaultNotVisible();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEG");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSCODEG: Code G");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setHidden();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEH");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSCODEH: Code H");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setHidden();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEI");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSCODEI: Code I");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setHidden();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEJ");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERSCODEJ: Code J");
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
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERITEM0_WO_NO: Work Order No");
        f.setDbName("WO_NO");

        f = itemblk.addField("ITEM5_PLAN_LINE_NO","Number");
        f.setSize(17);
        f.setDbName("PLAN_LINE_NO");
        f.setReadOnly();
        f.setHidden();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERPLAN_LINE_NO: Plan Line No");

        f = itemblk.addField("ITEM5_MAINT_MATERIAL_ORDER_NO","Number","#");
        f.setSize(17);
        f.setHidden();
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERITEM5_MAINT_MATERIAL_ORDER_NO: Mat Req Order No");
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

        f = itemblk.addField("CURRENCEY_CODE");
        f.setHidden();
        f.setFunction("ACTIVE_SEPARATE_API.Get_Currency_Code(:ITEM_WO_NO)");

        f = itemblk.addField("SALES_PRICE_GROUP_ID");
        f.setHidden();
        if (orderInst)
            f.setFunction("SALES_PART_API.GET_SALES_PRICE_GROUP_ID(:CATALOG_CONTRACT,:ITEM5_CATALOG_NO)");
        else
            f.setFunction("''");

        f = itemblk.addField("SUPPLY_CODE_DB");
        f.setHidden();

        f = itemblk.addField("ITEM5_COND_CODE_USAGE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("DEF_COND_CODE");
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
        f.setHidden(               );

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

        f = itemblk.addField("VENDOR_NO");
        f.setFunction("''");
        f.setHidden();
          
        f = itemblk.addField("OBJ_LOAN");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("SPARE_PART_EXIST", "Number");
        f.setFunction("Equipment_Object_Spare_Api.Check_Exist(Active_Work_Order_API.Get_Mch_Code(:ITEM_WO_NO),:SPARE_CONTRACT,:PART_NO,Active_Work_Order_API.Get_Mch_Code_Contract(:ITEM_WO_NO))");
        f.setHidden();

        itemblk.setView("MAINT_MATERIAL_REQ_LINE");

        itemblk.defineCommand("MAINT_MATERIAL_REQ_LINE_API","New__,Modify__,Remove__");
        itemset = itemblk.getASPRowSet();
        itemblk.setMasterBlock(itemblk3);

        itemtbl = mgr.newASPTable(itemblk);
        itemtbl.setWrap();

        itembar = mgr.newASPCommandBar(itemblk);
        itembar.enableCommand(itembar.FIND);
        itembar.defineCommand(itembar.NEWROW,"newRowITEM5");
        itembar.defineCommand(itembar.COUNTFIND,"countFindITEM5");
        itembar.defineCommand(itembar.OKFIND,"okFindITEM5");
        itembar.defineCommand(itembar.DUPLICATEROW,"duplicateITEM5");

        //itembar.defineCommand(itembar.SAVERETURN,null,"checkItem5Owner()");

        itembar.addCustomCommand("sparePartObject",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSPARTSINOBJECT: Spare Parts in Object..."));
        itembar.addCustomCommand("updateSparePartObject", mgr.translate("PCMWACTIVESEPARATE2UPDATESPRPARTS: Update Spare Parts in Object..."));
        itembar.addCustomCommand("objStructure",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDEROBJECTSTRUCT: Object Structure..."));
        itembar.addCustomCommand("detchedPartList",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSPAREINDETACH: Spare Parts in Detached Part List..."));
        itembar.addCustomCommandSeparator();
        itembar.addCustomCommand("reserve",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERRESERV: Reserve"));
        itembar.addCustomCommand("manReserve",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERRESERVMAN: Reserve manually..."));
        if (mgr.isPresentationObjectInstalled("invenw/InventoryPartInStockOvw.page"))
            itembar.addCustomCommand("availtoreserve",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERUNRESERVAVAILTORESERVE: Inventory Part in Stock..."));
        itembar.addCustomCommand("unreserve",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERUNRESERV: Unreserve..."));
        itembar.addCustomCommand("issue",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERISSUE: Issue"));
        itembar.addCustomCommand("issueFromInvent",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERISSUE2: Issue From Invent"));
        itembar.addCustomCommand("manIssue",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERISSUEMAN: Issue manually..."));
        itembar.addCustomCommandSeparator();
        itembar.addCustomCommand("availDetail",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERINVAVAILPLAN: Query - Inventory Part Availability Planning..."));
        itembar.addCustomCommand("supPerPart",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERSUPFORPART: Supplier per Part..."));
        itembar.addCustomCommand("matReqUnissue",mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERMATREQUNISSU: Material Requisition Unissue..."));
        //031110  ARWILK  Begin  (Bug#110424)
        itembar.addCustomCommand("matReqIssued",mgr.translate("PCMWACTSEPREPINWOORDISSUEDODET: Issued Part Details..."));
        //031110  ARWILK  End  (Bug#110424)
        itembar.addCustomCommandSeparator();
        itembar.addCustomCommand(   "prePostHead",mgr.translate(   "PCMWACTIVESEPERATEREPORTINWORKORDERPREPOSTDET: Pre Posting Detail..."));

        itembar.forceEnableMultiActionCommand("sparePartObject");
        itembar.forceEnableMultiActionCommand("objStructure");
        itembar.forceEnableMultiActionCommand("detchedPartList");

        itembar.addCommandValidConditions("updateSparePartObject","SPARE_PART_EXIST","Enable","0");

        //Web Alignment - Multirow Actions
        itemtbl.enableRowSelect();
        itembar.enableMultirowAction();
        itembar.removeFromMultirowAction("manReserve");
        itembar.removeFromMultirowAction("unreserve");
        itembar.removeFromMultirowAction("issueFromInvent");
        itembar.removeFromMultirowAction("manIssue");
        itembar.removeFromMultirowAction("matReqUnissue");
        itembar.removeFromMultirowAction("prePostHead");
        //
        itemlay = itemblk.getASPBlockLayout();
        itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);   


        //----------------------------------------------------------------------//
        //------------- This part belongs to the Tools and Facilities ----------//
        //----------------------------------------------------------------------//

        itemblk6 = mgr.newASPBlock("ITEM6");

        f = itemblk6.addField("ITEM6_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = itemblk6.addField("ITEM6_OBJVERSION");
        f.setDbName("OBJVERSION");
        f.setHidden();

        f = itemblk6.addField("ITEM6_WO_NO","Number","#");
        f.setDbName("WO_NO");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6WONO: Wo No");
        f.setSize(8);
        f.setMandatory();
        f.setInsertable();
        f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("ITEM6_ROW_NO","Number");
        f.setDbName("ROW_NO");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6ROWNO: Row No");
        f.setSize(8);
        f.setReadOnly();

        f = itemblk6.addField("TOOL_FACILITY_ID");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6TFID: Tool/Facility Id");
        f.setDynamicLOV("CONNECT_TOOLS_FACILITIES_LOV3","ITEM6_CONTRACT CONTRACT,ITEM6_ORG_CODE ORG_CODE,TOOL_FACILITY_TYPE",600,445);
        f.setCustomValidation("TOOL_FACILITY_ID,ITEM6_CONTRACT,ITEM6_ORG_CODE,ITEM6_WO_NO,ITEM6_QTY,ITEM6_PLANNED_HOUR,ITEM6_NOTE,ITEM6_CATALOG_NO,ITEM6_CATALOG_NO_CONTRACT,ITEM6_CATALOG_NO_DESC,ITEM6_SALES_PRICE,ITEM6_SALES_CURRENCY,ITEM6_DISCOUNT,ITEM6_DISCOUNTED_PRICE,ITEM6_PLANNED_PRICE","TOOL_FACILITY_DESC,TOOL_FACILITY_TYPE,TYPE_DESCRIPTION,ITEM6_QTY,ITEM6_COST,ITEM6_COST_CURRENCY,ITEM6_COST_AMOUNT,ITEM6_CATALOG_NO_CONTRACT,ITEM6_CATALOG_NO,ITEM6_CATALOG_NO_DESC,ITEM6_SALES_PRICE,ITEM6_SALES_CURRENCY,ITEM6_DISCOUNT,ITEM6_DISCOUNTED_PRICE,ITEM6_PLANNED_PRICE,ITEM6_NOTE");
        f.setSize(20);
        f.setMaxLength(40);
        f.setInsertable();
        f.setUpperCase();

        f = itemblk6.addField("TOOL_FACILITY_DESC");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6TFIDDESC: Tool/Facility Description");
        f.setFunction("Tool_Facility_API.Get_Tool_Facility_Description(:TOOL_FACILITY_ID)");
        f.setSize(30);
        f.setMaxLength(200);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk6.addField("TOOL_FACILITY_TYPE");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6TFTYPE: Tool/Facility Type");
        f.setDynamicLOV("TOOL_FACILITY_TYPE",600,445);
        f.setCustomValidation("TOOL_FACILITY_TYPE,TOOL_FACILITY_ID,ITEM6_WO_NO,ITEM6_QTY,ITEM6_PLANNED_HOUR","TYPE_DESCRIPTION,ITEM6_COST,ITEM6_COST_CURRENCY,ITEM6_COST_AMOUNT");
        f.setSize(20);
        f.setMaxLength(40);
        f.setInsertable();
        f.setUpperCase();
        f.setMandatory();

        f = itemblk6.addField("TYPE_DESCRIPTION");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6TFTYPEDESC: Type Description");
        f.setFunction("Tool_Facility_Type_API.Get_Type_Description(:TOOL_FACILITY_TYPE)");
        f.setSize(30);
        f.setMaxLength(200);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk6.addField("ITEM6_CONTRACT");
        f.setDbName("CONTRACT");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6CONTRACT: Site");
        f.setDynamicLOV("CONNECT_TOOLS_FACILITIES_SITE","TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM6_ORG_CODE ORG_CODE",600,445);
        f.setCustomValidation("ITEM6_CONTRACT,ITEM6_ORG_CODE,TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM6_QTY,ITEM6_PLANNED_HOUR,ITEM6_WO_NO,ITEM6_NOTE,ITEM6_CATALOG_NO,ITEM6_CATALOG_NO_CONTRACT,ITEM6_CATALOG_NO_DESC,ITEM6_SALES_PRICE,ITEM6_SALES_CURRENCY,ITEM6_DISCOUNT,ITEM6_DISCOUNTED_PRICE,ITEM6_PLANNED_PRICE","ITEM6_NOTE,ITEM6_QTY,ITEM6_COST_AMOUNT,ITEM6_CATALOG_NO_CONTRACT,ITEM6_CATALOG_NO,ITEM6_CATALOG_NO_DESC,ITEM6_SALES_PRICE,ITEM6_SALES_CURRENCY,ITEM6_DISCOUNT,ITEM6_DISCOUNTED_PRICE,ITEM6_PLANNED_PRICE");
        f.setSize(8);
        f.setMaxLength(5);
        f.setInsertable();
        f.setUpperCase();

        f = itemblk6.addField("ITEM6_ORG_CODE");
        f.setDbName("ORG_CODE");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6ORGCODE: Maintenance Organization");
        f.setDynamicLOV("CONNECT_TOOLS_FACILITIES_ORG","TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM6_CONTRACT CONTRACT",600,445);
        f.setCustomValidation("ITEM6_ORG_CODE,ITEM6_CONTRACT,TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM6_QTY,ITEM6_PLANNED_HOUR,ITEM6_WO_NO,ITEM6_NOTE,ITEM6_CATALOG_NO,ITEM6_CATALOG_NO_CONTRACT,ITEM6_CATALOG_NO_DESC,ITEM6_SALES_PRICE,ITEM6_SALES_CURRENCY,ITEM6_DISCOUNT,ITEM6_DISCOUNTED_PRICE,ITEM6_PLANNED_PRICE","ITEM6_NOTE,ITEM6_QTY,ITEM6_COST_AMOUNT,ITEM6_CATALOG_NO_CONTRACT,ITEM6_CATALOG_NO,ITEM6_CATALOG_NO_DESC,ITEM6_SALES_PRICE,ITEM6_SALES_CURRENCY,ITEM6_DISCOUNT,ITEM6_DISCOUNTED_PRICE,ITEM6_PLANNED_PRICE");
        f.setSize(8);
        f.setMaxLength(8);
        f.setInsertable();
        f.setUpperCase();

        f = itemblk6.addField("ITEM6_QTY", "Number");
        f.setDbName("QTY");
        //Bug ID 82934, Start
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6QTY: Planned Quantity");
        //Bug ID 82934, End
        f.setCustomValidation("ITEM6_QTY,ITEM6_PLANNED_HOUR,TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM6_WO_NO,ITEM6_CATALOG_NO,ITEM6_CATALOG_NO_CONTRACT,ITEM6_SALES_PRICE,ITEM6_DISCOUNT","ITEM6_COST_AMOUNT,ITEM6_PLANNED_PRICE");
        f.setSize(10);
        f.setInsertable();

        f = itemblk6.addField("ITEM6_PLANNED_HOUR", "Number");
        f.setDbName("PLANNED_HOUR");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6PLNHRS: Planned Hours");
        f.setCustomValidation("ITEM6_PLANNED_HOUR,ITEM6_QTY,TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM6_WO_NO,ITEM6_CATALOG_NO,ITEM6_CATALOG_NO_CONTRACT,ITEM6_SALES_PRICE,ITEM6_DISCOUNT","ITEM6_COST_AMOUNT,ITEM6_PLANNED_PRICE");
        f.setSize(10);
        f.setInsertable();

        f = itemblk6.addField("ITEM6_REPORT_HOUR", "Number");
        f.setDbName("REPORT_HOUR");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6REPHR: Report Hours");
        f.setSize(10);
        f.setInsertable();
        f.setDefaultNotVisible();

        f = itemblk6.addField("ITEM6_REPORTED_HOUR", "Number");
        f.setDbName("REPORTED_HOUR");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6REPTEDHRS: Reported Hours");
        f.setSize(10);
        f.setReadOnly();

        f = itemblk6.addField("ITEM6_JOB_ID", "Number");
        f.setDbName("JOB_ID");
        f.setSize(8);
        f.setInsertable();
        f.setDefaultNotVisible();
        f.setDynamicLOV("WORK_ORDER_JOB", "ITEM6_WO_NO WO_NO");
        f.setLabel("PCMWACTIVESEPERATEREPWOITEM6JOBID: Job Id");

        f = itemblk6.addField("ITEM6_CRAFT_LINE_NO", "Number");
        f.setDbName("CRAFT_LINE_NO");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6CRAFTLINENO: Operation No");
        f.setDynamicLOV("WORK_ORDER_ROLE","ITEM6_WO_NO WO_NO",600,445);
        f.setSize(8);
        f.setInsertable();
        f.setDefaultNotVisible();

        f = itemblk6.addField("ITEM6_PLAN_LINE_NO", "Number");
        f.setDbName("PLAN_LINE_NO");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6PLANLINENO: Plan Line No");
        f.setSize(8);
        f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("ITEM6_COST", "Money");
        f.setDbName("COST");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6COST: Cost");
        f.setSize(12);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk6.addField("ITEM6_COST_CURRENCY");
        f.setDbName("COST_CURRENCY");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6COSTCURR: Cost Currency");
        f.setSize(10);
        f.setMaxLength(3);
        f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("ITEM6_COST_AMOUNT", "Money");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6COSTAMT: Cost Amount");
        f.setFunction("Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount(:TOOL_FACILITY_ID, :TOOL_FACILITY_TYPE, :ITEM6_WO_NO, :ITEM6_QTY, :ITEM6_PLANNED_HOUR)");
        f.setSize(12);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk6.addField("ITEM6_CATALOG_NO_CONTRACT");
        f.setDbName("CATALOG_NO_CONTRACT");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6CATALOGCONT: Sales Part Site");
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setSize(8);
        f.setMaxLength(5);
        f.setReadOnly();
        if (!orderInst)
            f.setHidden();

        f = itemblk6.addField("ITEM6_CATALOG_NO");
        f.setDbName("CATALOG_NO");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6CATALOGNO: Sales Part");
        f.setDynamicLOV("SALES_PART_SERVICE_LOV", "ITEM6_CATALOG_NO_CONTRACT CONTRACT",600,445);
        f.setCustomValidation("ITEM6_CATALOG_NO,ITEM6_CATALOG_NO_CONTRACT,ITEM6_WO_NO,ITEM6_QTY,ITEM6_PLANNED_HOUR","ITEM6_CATALOG_NO_DESC,ITEM6_SALES_PRICE,ITEM6_SALES_CURRENCY,ITEM6_DISCOUNT,ITEM6_DISCOUNTED_PRICE,ITEM6_PLANNED_PRICE");
        f.setSize(20);
        f.setMaxLength(25);
        f.setInsertable();
        if (!orderInst)
            f.setHidden();

        f = itemblk6.addField("ITEM6_CATALOG_NO_DESC");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6CATALOGDESC: Sales Part Description");
        f.setFunction("''");
        f.setSize(30);
        f.setMaxLength(35);
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (!orderInst)
            f.setHidden();
        else
            f.setFunction("Connect_Tools_Facilities_API.Get_Sales_Part_Desc(:ITEM6_CATALOG_NO_CONTRACT,:ITEM6_CATALOG_NO)");   

        f = itemblk6.addField("ITEM6_SALES_PRICE", "Money");
        f.setDbName("SALES_PRICE");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6SALESPRICE: Sales Price");
        f.setCustomValidation("ITEM6_SALES_PRICE,ITEM6_CATALOG_NO_CONTRACT,ITEM6_CATALOG_NO,ITEM6_WO_NO,ITEM6_QTY,ITEM6_PLANNED_HOUR,ITEM6_DISCOUNT","ITEM6_DISCOUNTED_PRICE,ITEM6_PLANNED_PRICE");
        f.setSize(12);
        f.setInsertable();
        f.setDefaultNotVisible();
        if (!orderInst)
            f.setHidden();

        f = itemblk6.addField("ITEM6_SALES_CURRENCY");
        f.setDbName("PRICE_CURRENCY");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6SALESCURR: Sales Currency");
        f.setSize(10);
        f.setMaxLength(3);
        f.setReadOnly();
        f.setHidden();
        f.setDefaultNotVisible();
        if (!orderInst)
            f.setHidden();

        f = itemblk6.addField("ITEM6_DISCOUNT", "Number");
        f.setDbName("DISCOUNT");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6DISCOUNT: Discount");
        f.setCustomValidation("ITEM6_DISCOUNT,ITEM6_SALES_PRICE,ITEM6_CATALOG_NO_CONTRACT,ITEM6_CATALOG_NO,ITEM6_WO_NO,ITEM6_QTY,ITEM6_PLANNED_HOUR","ITEM6_DISCOUNTED_PRICE,ITEM6_PLANNED_PRICE");
        f.setSize(8);
        f.setInsertable();
        f.setDefaultNotVisible();
        if (!orderInst)
            f.setHidden();

        f = itemblk6.addField("ITEM6_DISCOUNTED_PRICE", "Money");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6DISCPRICE: Discounted Price");
        f.setFunction("''");
        f.setSize(12);
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (!orderInst)
            f.setHidden();
        else
            f.setFunction("Work_Order_Tool_Facility_API.Calculate_Discounted_Price(:ITEM6_CATALOG_NO,:ITEM6_CATALOG_NO_CONTRACT,:ITEM6_WO_NO,:ITEM6_QTY,:ITEM6_PLANNED_HOUR,:ITEM6_SALES_PRICE,:ITEM6_DISCOUNT)");

        f = itemblk6.addField("ITEM6_PLANNED_PRICE", "Money");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6PLANPRICE: Planned Price Amount");
        f.setFunction("''");
        f.setSize(12);
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (!orderInst)
            f.setHidden();
        else
            f.setFunction("Work_Order_Tool_Facility_API.Calculated_Price_Amount(:ITEM6_CATALOG_NO,:ITEM6_CATALOG_NO_CONTRACT,:ITEM6_WO_NO,:ITEM6_QTY,:ITEM6_PLANNED_HOUR,:ITEM6_SALES_PRICE,:ITEM6_DISCOUNT)");

        f = itemblk6.addField("FROM_DATE_TIME", "Datetime");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6FROMDATE: From Date/Time");
        f.setSize(20);
        f.setInsertable();
        f.setDefaultNotVisible();

        f = itemblk6.addField("TO_DATE_TIME", "Datetime");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6TODATE: To Date/Time");
        f.setSize(20);
        f.setInsertable();
        f.setDefaultNotVisible();

        f = itemblk6.addField("ITEM6_NOTE");
        f.setDbName("NOTE");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDER6NOTE: Note");
        f.setSize(20);
        f.setHeight(4);
        f.setMaxLength(2000);
        f.setInsertable();
        f.setDefaultNotVisible();

        f = itemblk6.addField("ITEM6_WO_CONTRACT");
        f.setFunction("''");
        f.setHidden();

        f = itemblk6.addField("ITEM6_SP_SITE");
        f.setFunction("''");
        f.setHidden();

        itemblk6.setView("WORK_ORDER_TOOL_FACILITY");
        itemblk6.defineCommand("WORK_ORDER_TOOL_FACILITY_API","New__,Modify__,Remove__");
        itemset6 = itemblk6.getASPRowSet();

        itemblk6.setMasterBlock(headblk);

        itembar6 = mgr.newASPCommandBar(itemblk6);
        itembar6.enableCommand(itembar6.FIND);
        itembar6.defineCommand(itembar6.NEWROW,"newRowITEM6");
        itembar6.defineCommand(itembar6.COUNTFIND,"countFindITEM6");
        itembar6.defineCommand(itembar6.OKFIND,"okFindITEM6");
        itembar6.defineCommand(itembar6.DUPLICATEROW,"duplicateITEM6");

        itemtbl6 = mgr.newASPTable(itemblk6);
        itemtbl6.setWrap();

        itemlay6 = itemblk6.getASPBlockLayout();
        itemlay6.setDefaultLayoutMode(itemlay6.MULTIROW_LAYOUT);
        itemlay6.setDialogColumns(3);  

        itemlay6.setFieldOrder("ITEM6_ROW_NO,ITEM6_CONTRACT,TOOL_FACILITY_ID,TOOL_FACILITY_DESC,TOOL_FACILITY_TYPE,TYPE_DESCRIPTION,ITEM6_ORG_CODE,ITEM6_QTY,ITEM6_PLANNED_HOUR,ITEM6_REPORT_HOUR,ITEM6_REPORTED_HOUR,ITEM6_CRAFT_LINE_NO,ITEM6_COST,ITEM6_COST_AMOUNT,ITEM6_CATALOG_NO_CONTRACT,ITEM6_CATALOG_NO,ITEM6_CATALOG_NO_DESC,ITEM6_SALES_PRICE");

        //-----------------------------------------------------------------------//
        //----------------------- End of T/F ------------------------------------//
        //-----------------------------------------------------------------------//

        //-----------------------------------------------------------------------
        //-------------- This part belongs to BUDGET ----------------------------
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
        f.setSize(25);
        f.setDbName("WORK_ORDER_COST_TYPE");
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERWOORDERCOSTTYPE: Work Order Cost Type");
        f.setReadOnly();

        f = itemblk4.addField("WORK_ORDER_COST_TYPE_DB");
        f.setHidden();

        f = itemblk4.addField("BUDGET_COST","Money");
        f.setSize(25);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERBUDGETCOST: Budget Cost");

        f = itemblk4.addField("PLANNED_COST","Money");
        f.setSize(25);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERPLANNEDCOST: Planned Cost");
        f.setFunction("WORK_ORDER_BUDGET_API.Get_Planned_Cost(:WO_NO,:WORK_ORDER_COST_TYPE)");
        f.setReadOnly();

        f = itemblk4.addField("ACTUAL_COST","Money");
        f.setSize(25);
        f.setLabel("PCMWACTIVESEPERATEREPORTINWORKORDERACCCOST: Actual Cost");
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

        itemtbl4 = mgr.newASPTable(itemblk4);
        itemtbl4.setWrap();

        itemlay4 = itemblk4.getASPBlockLayout();
        itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT);
        itemlay4.setDialogColumns(4);

        // ---------------------------------------------------------------------------------------
        // -------------------------------------   ITEMBLK7   ------------------------------------
        // ---------------------------------------------------------------------------------------

        itemblk7 = mgr.newASPBlock("ITEM7");

        itemblk7.addField("ITEM7_OBJID").
        setDbName("OBJID").
        setHidden();

        itemblk7.addField("ITEM7_OBJVERSION").
        setDbName("OBJVERSION").
        setHidden();

        itemblk7.addField("ITEM7_WO_NO", "Number", "#").
        setDbName("WO_NO").
        setHidden().
        setReadOnly().
        setInsertable().
        setMandatory();

        itemblk7.addField("JOB_ID", "Number").
        setLabel("ACTSEPREPITEM7JOBID: Job ID").
        setReadOnly().
        setInsertable().
        setMandatory();

        itemblk7.addField("STD_JOB_ID").
        setSize(15).
        setLabel("ACTSEPREPITEM7STDJOBID: Standard Job ID").
        setLOV("SeparateStandardJobLov.page", 600, 445).
        setUpperCase().
        setInsertable().
        setQueryable().
        setCustomValidation("STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION", "STD_JOB_ID,STD_JOB_REVISION,DESCRIPTION,STD_JOB_STATUS").
        setMaxLength(12);

        itemblk7.addField("STD_JOB_CONTRACT").
        setSize(10).
        setLabel("ACTSEPREPITEM7STDJOBCONTRACT: Site").
        setUpperCase().
        setReadOnly().
        setMaxLength(5);

        itemblk7.addField("STD_JOB_REVISION").
        setSize(10).
        setLabel("ACTSEPREPITEM7STDJOBREVISION: Revision").
        setDynamicLOV("SEPARATE_STANDARD_JOB_LOV", "STD_JOB_CONTRACT CONTRACT,STD_JOB_ID", 600, 445).    
        setCustomValidation("STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION", "DESCRIPTION,STD_JOB_STATUS").
        setUpperCase().
        setInsertable().
        setQueryable().
        setMaxLength(6);

        itemblk7.addField("DESCRIPTION").
        setSize(35).
        setLabel("ACTSEPREPITEM7DESCRIPTION: Description").
        setUpperCase().
        setMandatory().
        setInsertable().
        setMaxLength(4000);

        itemblk7.addField("ITEM7_QTY", "Number").
        setDbName("QTY").
        setLabel("ACTSEPREPITEM7QTY: Quantity").
        setMandatory().
        setInsertable();

        itemblk7.addField("ITEM7_COMPANY").
        setDbName("COMPANY").
        setSize(20).
        setHidden().
        setUpperCase().
        setInsertable();

        itemblk7.addField("STD_JOB_STATUS").
        setLabel("ACTSEPREPITEM7STDJOBSTATUS: Std Job Status").
        setFunction("Standard_Job_API.Get_Status(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)").
        setReadOnly();

        itemblk7.addField("SIGN_ID").
        setSize(35).
        setLabel("ACTSEPREPITEM7SIGNID: Executed By").
        setQueryable().
        setDbName("SIGNATURE").
        setUpperCase().
        setLOV("../mscomw/MaintEmployeeLov.page","ITEM7_COMPANY COMPANY",600,450).
        //Bug 83757, Start
        setCustomValidation("ITEM7_COMPANY,SIGN_ID","ITEM1_ORG_CODE,ITEM1_CONTRACT");
        //Bug 83757, End

        itemblk7.addField("EMPLOYEE_ID").
        setSize(15).
        setLabel("ACTSEPREPITEM7EMPLOYEEID: Employee ID").
        setUpperCase().
        setReadOnly().
        setMaxLength(11);

        //(+) Bug 66406, Start
        itemblk7.addField("CONN_PM_NO", "Number" ,"#").
        setDbName("PM_NO").
        setSize(15).
        setReadOnly().
        setCustomValidation("CONN_PM_NO,CONN_PM_REVISION","CONN_PM_NO,CONN_PM_REVISION").
        setLabel("ACTSEPREPNORMCONNPMNO: PM No");

        itemblk7.addField("CONN_PM_REVISION").
        setDbName("PM_REVISION").
        setSize(15).
        setReadOnly().
        setLabel("ACTSEPREPNORMCONNPMREV: PM Revision");

        itemblk7.addField("CONN_PM_JOB_ID", "Number").
        setDbName("PM_JOB_ID"). 
        setSize(15).
        setReadOnly().
        setDynamicLOV("PM_ACTION_JOB", "CONN_PM_NO PM_NO, CONN_PM_REVISION PM_REVISION").
        setLabel("ACTSEPREPNORMCONNPMJOBID: PM Job ID");
        //(+) Bug 66406, End

        itemblk7.addField("DATE_FROM", "Datetime").
        setSize(20).
        setLabel("ACTSEPREPITEM7DATEFROM: Date From").
        setInsertable();

        itemblk7.addField("DATE_TO", "Datetime").
        setSize(20).
        setLabel("ACTSEPREPITEM7DATETO: Date To").
        setInsertable();

        itemblk7.addField("STD_JOB_FLAG", "Number").
        setHidden().
        setCustomValidation("ITEM7_WO_NO,JOB_ID,STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION", "N_JOB_EXIST,S_STD_JOB_EXIST,N_ROLE_EXIST,N_MAT_EXIST,N_TOOL_EXIST,N_PLANNING_EXIST,N_DOC_EXIST,S_STD_JOB_ID,S_STD_JOB_CONTRACT,S_STD_JOB_REVISION,N_QTY,S_AGREEMENT_ID").
        setInsertable();

        itemblk7.addField("KEEP_CONNECTIONS").
        setHidden().
        setSize(3).
        setInsertable();

        itemblk7.addField("RECONNECT").
        setHidden().
        setSize(3).
        setInsertable();

        // -----------------------------------------------------------------------
        // -----------------------  Hidden Fields --------------------------------
        // -----------------------------------------------------------------------

        itemblk7.addField("N_JOB_EXIST", "Number").
        setFunction("0").
        setHidden();

        itemblk7.addField("S_STD_JOB_EXIST").
        setFunction("''").
        setHidden();

        itemblk7.addField("N_ROLE_EXIST", "Number").
        setFunction("0").
        setHidden();

        itemblk7.addField("N_MAT_EXIST", "Number").
        setFunction("0").
        setHidden();

        itemblk7.addField("N_TOOL_EXIST", "Number").
        setFunction("0").
        setHidden();

        itemblk7.addField("N_PLANNING_EXIST", "Number").
        setFunction("0").
        setHidden();

        itemblk7.addField("N_DOC_EXIST", "Number").
        setFunction("0").
        setHidden();

        itemblk7.addField("S_STD_JOB_ID").
        setFunction("''").
        setHidden();

        itemblk7.addField("S_STD_JOB_CONTRACT").
        setFunction("''").
        setHidden();

        itemblk7.addField("S_STD_JOB_REVISION").
        setFunction("''").
        setHidden();

        itemblk7.addField("N_QTY", "Number").
        setFunction("0").
        setHidden();

        itemblk7.addField("S_AGREEMENT_ID").
        setFunction("''").
        setHidden();

        itemblk7.setView("WORK_ORDER_JOB");
        itemblk7.defineCommand("WORK_ORDER_JOB_API","New__,Modify__,Remove__");
        itemblk7.setMasterBlock(headblk);

        itemset7 = itemblk7.getASPRowSet();

        itemtbl7 = mgr.newASPTable(itemblk7);
        itemtbl7.setTitle(mgr.translate("ACTSEPREPITEM7WOJOBS: Jobs"));
        itemtbl7.setWrap();

        itembar7 = mgr.newASPCommandBar(itemblk7);
        itembar7.enableCommand(itembar7.FIND);

        itembar7.defineCommand(itembar7.NEWROW, "newRowITEM7");
        itembar7.defineCommand(itembar7.SAVERETURN, "saveReturnItem7", "checkITEM7SaveParams(i)");
        itembar7.defineCommand(itembar7.COUNTFIND,"countFindITEM7");
        itembar7.defineCommand(itembar7.OKFIND,"okFindITEM7");
        //Bug 89703, Start
        itembar7.defineCommand(itembar7.DELETE,"deleteRowITEM7");
        //Bug 89703, End

        itemlay7 = itemblk7.getASPBlockLayout();
        itemlay7.setDefaultLayoutMode(itemlay7.MULTIROW_LAYOUT);
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

    public void activateToolsnFacilitiesTab()
    {
        tabs.setActiveTab(4);
    }

    public void activateJobsTab()
    {
        tabs.setActiveTab(5);
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

            String objlevel = trans.getValue("OBJLEV/DATA/OBJLEVEL");

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

            String objlevel = trans.getValue("OBJLEV/DATA/OBJLEVEL");

            if (!mgr.isEmpty(objlevel) && headlay.isSingleLayout())
                headbar.removeCustomCommand("moveSerial");

            if (!mgr.isEmpty(objlevel) && headlay.isMultirowLayout())
                mgr.showAlert(mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERCANTMOVESERIAL: Cannot perform the action."));
            else
                moveSerial2();
        }
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
                                   "INVENTORY_PART_IN_STOCK");

            trans.addSecurityQuery("Work_Order_Coding_API","Authorize");
            trans.addSecurityQuery("MAINT_MATERIAL_REQUISITION_RPI","Report_Printout");
            trans.addSecurityQuery("MAINT_MATERIAL_REQ_LINE_API","Make_Reservation_Short,Make_Auto_Issue_Detail,Make_Manual_Issue_Detail");

            trans.addPresentationObjectQuery("PCMW/ActiveSeparate2.page,"+
                                             "PCMW/WorkOrderReportPageOvw2.page,"+
                                             "PCMW/WorkOrderCoding1.page,"+
                                             "PCMW/WorkOrderRequisHeaderRMB.page,"+
                                             "PCMW/WorkOrderPermit.page,"+
                                             "PCMW/ActiveSeparateReportCOInfo.page,"+
                                             "PCMW/ActiveSeparate2WorkCenterLoad.page,"+
                                             "PCMW/MaintTask.page,"+
                                             "PCMW/ActiveWorkOrder4.page,"+
                                             "PCMW/SeparateWorkOrder.page,"+
                                             "PCMW/PartSerialCatalogDlg.page,"+
                                             "PCMW/ActiveSeparateDlg.page,"+
                                             "PCMW/MoveNonSerialToInventDlg.page,"+
                                             "PCMW/WorkOrderRolePlanningLov.page,"+
                                             "MPCCOW/PreAccountingDlg.page,"+
                                             "PCMW/MaintenanceObject2.page,"+
                                             "PCMW/MaintenaceObject3.page,"+
                                             "EQUIPW/EquipmentSpareStructure2.page,"+
                                             "PCMW/MaterialRequisReservatDlg.page,"+
                                             "PCMW/MaterialRequisReservatDlg2.page,"+
                                             "PCMW/InventoryPartLocationDlg.page,"+
                                             "INVENW/InventoryPartAvailabilityPlanningQry.page,"+
                                             "PURCHW/PurchasePartSupplier.page,"+
                                             "PCMW/ActiveWorkOrder.page,"+
                                             "INVENW/InventoryPartInStockOvw.page,"+
                                             "EQUIPW/EquipmentSpareStructure3.page");


            trans = mgr.perform(trans);

            ASPBuffer secViewBuff = trans.getSecurityInfo();

            actionsBuffer = mgr.newASPBuffer();

            if (secViewBuff.namedItemExists("PCMW/ActiveSeparate2.page"))
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

            if ( (isModuleInst1("VIM")))
                actionsBuffer.addItem("okHeadMaintTask","");

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
                actionsBuffer.addItem("okItem3PrePostHead","");

            if (secViewBuff.itemExists("MAINT_MATERIAL_REQUISITION_RPI.Report_Printout"))
                actionsBuffer.addItem("okItem3PrintPicList","");

            if (secViewBuff.itemExists("MAINTENANCE_OBJECT"))
            {
                if (secViewBuff.namedItemExists("PCMW/MaintenanceObject2.page"))
                    actionsBuffer.addItem("okItemSparePartObject","");
                if (secViewBuff.namedItemExists("PCMW/MaintenaceObject3.page"))
                    actionsBuffer.addItem("okItemObjStructure","");
            }

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

            if (secViewBuff.itemExists("INVENTORY_PART_IN_STOCK") && secViewBuff.namedItemExists("invenw/InventoryPartInStockOvw.page"))
                actionsBuffer.addItem("okItemAvailReserve","");

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

        if (!actionsBuffer.itemExists("okHeadMaintTask"))
            headbar.removeCustomCommand("maintTask");

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

        // itembar1
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

        //Web Alignment - Replacing links with RMBs
        if (!actionsBuffer.itemExists("okItemSparePartObject"))
            itembar.removeCustomCommand("sparePartObject");

        if (!actionsBuffer.itemExists("okItemObjStructure"))
            itembar.removeCustomCommand("objStructure");

        if (!actionsBuffer.itemExists("okHeadPrepare"))
            headbar.removeCustomCommand("prepareWO");
        //
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();    

        //Call Id : 114222
        if (headset.countRows() > 0 && headlay.isSingleLayout())
        {
            if (itemset.countRows()==0)
            {
                itembar.removeCustomCommand("availDetail");
                itembar.removeCustomCommand("supPerPart");
                itembar.removeCustomCommand("reserve");
                itembar.removeCustomCommand("manReserve");
                if (mgr.isPresentationObjectInstalled("invenw/InventoryPartInStockOvw.page"))
                    itembar.removeCustomCommand("availtoreserve");
                itembar.removeCustomCommand("unreserve");
                itembar.removeCustomCommand("issue");
                itembar.removeCustomCommand("manIssue");
                itembar.removeCustomCommand("issueFromInvent");
                itembar.removeCustomCommand("matReqUnissue");
                itembar.removeCustomCommand("matReqIssued");    
                itembar.removeCustomCommand("prePostHead");    
            }
        
            trans.clear();

            cmd = trans.addCustomFunction("PARTYTYPE","PARTY_TYPE_API.Get_Client_Value","S_PARTY_TYPE"); 
            cmd.addParameter("PARTY_TYPE","1");

            cmd = trans.addCustomFunction("GETCOMPANY","Site_API.Get_Company","COMPANY");
            cmd.addParameter("CONTRACT",headset.getValue("CONTRACT"));

            cmd = trans.addCustomFunction("GETRECIEVEORD","ACTIVE_WORK_ORDER_API.Get_Receive_Order_No","RECEIVE_ORDER_NO");
            cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

            cmd = trans.addCustomCommand("GETVIMPARTSN","ACTIVE_SEPARATE_API.Separate_Mro_Part_Serial");
            cmd.addParameter("VIM_PART_NO");
            cmd.addParameter("VIM_SERIAL_NO");
            cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

            ASPTransactionBuffer secBuff;
            boolean bSecurityOk1 = false;
            boolean bSecurityOk2 = false;
            secBuff = mgr.newASPTransactionBuffer();
            secBuff.addSecurityQuery("IDENTITY_INVOICE_INFO_API","Get_Identity_Type");
            secBuff.addSecurityQuery("CONNECT_CUSTOMER_ORDER_API","Is_Wo_Connected_To_Mro_Line");
            secBuff = mgr.perform(secBuff);
      
            if ( secBuff.getSecurityInfo().itemExists("IDENTITY_INVOICE_INFO_API.Get_Identity_Type") )
               bSecurityOk1 = true;

            if (secBuff.getSecurityInfo().itemExists("CONNECT_CUSTOMER_ORDER_API.Is_Wo_Connected_To_Mro_Line"))
               bSecurityOk2 = true;

            if (bSecurityOk1) 
            {
               cmd = trans.addCustomFunction("GETIDENTYPE","IDENTITY_INVOICE_INFO_API.Get_Identity_Type","IDENTITY_TYPE");
               cmd.addReference("COMPANY","GETCOMPANY/DATA");
               cmd.addParameter("CUSTOMER_NO",headset.getValue("CUSTOMER_NO"));
               cmd.addReference("S_PARTY_TYPE","PARTYTYPE/DATA");
            }
            else
               headbar.disableCustomCommand("createRecieveOrder");

            cmd = trans.addCustomFunction("GETOPRSTAT","PART_SERIAL_CATALOG_API.Get_Operational_Condition_Db","OPER_STATUS");
            cmd.addReference("VIM_PART_NO","GETVIMPARTSN/DATA");
            cmd.addReference("VIM_SERIAL_NO","GETVIMPARTSN/DATA");

            cmd = trans.addCustomFunction("GETSNOPRSTAT","PART_SERIAL_CATALOG_API.Get_Objstate","PART_OBJ_STATE");
            cmd.addReference("VIM_PART_NO","GETVIMPARTSN/DATA");
            cmd.addReference("VIM_SERIAL_NO","GETVIMPARTSN/DATA");

            if (bSecurityOk2) 
            {
               cmd = trans.addCustomFunction("ISCONTOMROLINE","CONNECT_CUSTOMER_ORDER_API.Is_Wo_Connected_To_Mro_Line","DUMMY_FLD");
               cmd.addParameter("ORDER_NO",headset.getValue("CUST_ORDER_NO"));
               cmd.addParameter("WO_NO",headset.getValue("WO_NO"));
               cmd.addReference("VIM_PART_NO","GETVIMPARTSN/DATA");
            }
            
            trans = mgr.perform(trans);

            sRecieveOrdNo = trans.getValue("GETRECIEVEORD/DATA/RECEIVE_ORDER_NO");
            String sIdentityType = trans.getValue("GETIDENTYPE/DATA/IDENTITY_TYPE");
            String sVimPartNo = trans.getValue("GETVIMPARTSN/DATA/VIM_PART_NO");
            String sVimSerialNo = trans.getValue("GETVIMPARTSN/DATA/VIM_SERIAL_NO");
            String sOperCondition = trans.getValue("GETOPRSTAT/DATA/OPER_STATUS");
            String sObjState = trans.getValue("GETSNOPRSTAT/DATA/PART_OBJ_STATE");
            String sObjConToMroLine = trans.getValue("ISCONTOMROLINE/DATA/DUMMY_FLD");

            if (!(mgr.isEmpty(sRecieveOrdNo) && ("EXTERN").equals(sIdentityType) && !mgr.isEmpty(sVimPartNo) && !mgr.isEmpty(sVimSerialNo)))
               headbar.disableCustomCommand("createRecieveOrder");


            if (!("OPERATIONAL".equals(sOperCondition) && "InInventory".equals(sObjState) && "FALSE".equals(sObjConToMroLine) && !mgr.isEmpty(sVimPartNo) && !mgr.isEmpty(sVimSerialNo) && "EXTERN".equals(sIdentityType)))
               headbar.disableCustomCommand("returnObject");

            if (!("NON_OPERATIONAL".equals(sOperCondition) && "InInventory".equals(sObjState) && "FALSE".equals(sObjConToMroLine) && !mgr.isEmpty(sVimPartNo) && !mgr.isEmpty(sVimSerialNo) && "EXTERN".equals(sIdentityType)))
               headbar.disableCustomCommand("returnNonOperationalObj");
        }
        else
        {
           headbar.disableCommand("createRecieveOrder");
           headbar.disableCommand("returnObject");
           headbar.disableCommand("returnNonOperationalObj");
        }
        //
	if (!(itemset1.countRows() == 0)) {
	    itemtbl1.enableRowSelect();
	}

        headbar.removeCustomCommand("refreshForm");
	headbar.removeCustomCommand("refreshMaterialTab");

        lblempno = mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERLBLABC: Employee+ID");
        lblempsig =  mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERLBLABCD: Signature");
        lblrepBy1 = mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERLBLABCDE: Reported+by"); 
        empno = mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERABC: List+of+Employee+ID");
        empsig =  mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERABCD: List+of+Signature");
        repBy1 = mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERABCDE: List+of+Reported+by");

        headbar.removeCustomCommand("activateTimeReportTab");
        headbar.removeCustomCommand("activateMaterialTab");
        headbar.removeCustomCommand("activateToolsnFacilitiesTab");
        headbar.removeCustomCommand("activateBudgetTab");
        headbar.removeCustomCommand("activateJobsTab");

        if (mgr.isPresentationObjectInstalled("docmaw/DocReference.page"))
            mgr.getASPField("CBHASDOCUMENTS").setHyperlink("../docmaw/DocReference.page","LU_NAME,KEY_REF","NEWWIN");

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

        if (( itemset4.countRows() == 6 ) &&  ! headlay.isMultirowLayout())
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

        if (item4CurrRow == 6 &&   itemlay4.isSingleLayout())
            itembar4.disableCommand(itembar4.EDITROW);

        disableMoveStoIn();

        title_ = mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERTRE: Report In Work Order");

        if (headset.countRows() > 0)
        {
            headrowno = headset.getCurrentRowNo();
            setCheckBoxValue(headrowno);
            setDisable(headrowno);

            // Remove New Button of Material block when the status of WO is 'Reported'.
            String tempObjState = headset.getValue("OBJSTATE");
            if ("REPORTED".equals(tempObjState))
            {
                itembar3.disableCommand(itembar3.NEWROW);
                itembar.disableCommand(itembar.NEWROW);
            }
        }

        if (headlay.isSingleLayout() && headset.countRows()>0)
        {
            ASPTransactionBuffer secViewBuff = mgr.newASPTransactionBuffer();
            secViewBuff.addSecurityQuery("CUSTOMER_AGREEMENT");

            secViewBuff = mgr.perform(secViewBuff);

            if (!(secViewBuff.getSecurityInfo().itemExists("CUSTOMER_AGREEMENT")))
                headbar.removeCustomCommand("printAuth");
        }

        if (headset.countRows() > 0)
            sWONo = headset.getRow().getValue("WO_NO");
        else
            sWONo   = "";

        if (( "false".equals(creFromPlan) )&& ( "false".equals(connReconToPlan) ))
        {
            if (itemset1.countRows() == 0)
                itembar1.removeCustomCommand("connectToPlanTime");
        }

        if (matSingleMode || "TRUE".equals(showMat))
        {
            this.activateMaterialTab();
            itemlay3.setLayoutMode(itemlay3.SINGLE_LAYOUT);
            String matLine = ctx.findGlobal("CURRROWGLOBAL");  
            itemset3.goTo(Integer.parseInt(matLine));
            clearItem4();
            okFindITEM5();
            matSingleMode = false;
            updateBudg = true;
        }

        if (itemlay.isVisible() || "TRUE".equals(showMat))
        {
            itembar.removeCustomCommand("issueFromInvent");
        }

        if (itemlay6.isNewLayout() || itemlay6.isEditLayout() || itemlay1.isNewLayout() || itemlay1.isEditLayout() || 
	    itemlay3.isNewLayout() || itemlay3.isEditLayout() || itemlay4.isEditLayout()|| itemlay1.isFindLayout() || 
	    itemlay3.isFindLayout()|| itemlay4.isFindLayout() || itemlay6.isFindLayout()|| itemlay7.isNewLayout()  ||
	    itemlay7.isEditLayout() )
        {
            headbar.disableCommand(headbar.DELETE);
            headbar.disableCommand(headbar.NEWROW);
            headbar.disableCommand(headbar.EDITROW);
            headbar.disableCommand(headbar.DELETE);
            headbar.disableCommand(headbar.DUPLICATEROW);
            headbar.disableCommand(headbar.FIND);
            headbar.disableCommand(headbar.BACKWARD);
            headbar.disableCommand(headbar.FORWARD);

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
            headbar.removeCustomCommand("maintTask");
            headbar.removeCustomCommand("placeSerial");
            headbar.removeCustomCommand("coInformation");
            headbar.removeCustomCommand("moveNonSerial");
            headbar.removeCustomCommand("changeConditionCode");

        }

        if (itemlay1.isFindLayout() || itemlay1.isNewLayout() || itemlay1.isEditLayout())
        {
            itembar1.removeCustomCommand("createFromPlanTime");
        }

        if (mgr.isModuleInstalled("PROJ") && !mgr.isPresentationObjectInstalled("projw/ConObjToActivityDlg.page"))
        {
            headbar.disableCustomCommand("connectToActivity");
            headbar.disableCustomCommand("disconnectFromActivity");
            headbar.disableCustomCommand("activityInfo");
        }

        if (headset.countRows()>0 && itemlay7.isVisible())
        {
            String sWhereStrForITEM7 = "CONTRACT = '" + headset.getValue("CONTRACT") + "'";

            if (itemlay7.isFindLayout())
            {
                mgr.getASPField("STD_JOB_ID").setLOV("StandardJobLov1.page", 600, 450);
                sWhereStrForITEM7 = sWhereStrForITEM7 + " AND STANDARD_JOB_TYPE_DB = '1'";
            }

            mgr.getASPField("STD_JOB_ID").setLOVProperty("WHERE", sWhereStrForITEM7);
        }

        if (itemlay3.isVisible())
        {
            mgr.getASPField("ITEM3_CONTRACT").setLOVProperty("WHERE","Site_API.Get_Company(CONTRACT) = Site_API.Get_Company('"+mgr.getASPField("CONTRACT").getValue()+"')");

        }

        //(+) Bug 66406, Start
        if (itemlay7.isFindLayout())
           mgr.getASPField("CONN_PM_NO").setLOV("../pcmw/PmActionLov1.page",600,450);
        else if (itemlay7.isNewLayout()) 
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
        return "PCMWACTIVESEPERATEREPORTINWORKORDERTITLE: Report In Work Order";
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
       out.append(mgr.generateHeadTag("PCMWACTIVESEPERATEREPORTINWORKORDERTITLE: Report In Work Order"));
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
                {
                    out.append(itemlay1.show());
                }
                else if (tabs.getActiveTab() == 3)
                {
                    out.append(itemlay3.show());
    
                    ctx.setGlobal("CURRROWGLOBAL", String.valueOf(headset.getCurrentRowNo()));
                    ctx.setGlobal("WONOGLOBAL", String.valueOf(itemset3.getCurrentRowNo()));
    
                    if (itemlay3.isSingleLayout() && itemset3.countRows() > 0)
                    {
                        out.append(itemlay.show());
                    }
    
                }
                else if (tabs.getActiveTab() == 4)
                    out.append(itemlay6.show());
                else if (tabs.getActiveTab() == 5)
                    out.append(itemlay7.show());
            }
    
            out.append(fmt.drawHidden("STATEVAL", ""));            
            out.append(fmt.drawHidden("PNT", ""));            
            out.append(fmt.drawHidden("CREFROMPLAN", ""));            
            out.append(fmt.drawHidden("BUTTONVAL", ""));            
            out.append(fmt.drawHidden("HIDDENPARTNO", ""));            
            out.append(fmt.drawHidden("ONCEGIVENERROR", "FALSE"));            
            out.append(fmt.drawHidden("CREREPNONSER", openCreRepNonSer));            
            out.append(fmt.drawHidden("ACTIVITY_SEQ_PROJ", ""));
	         //Bug 82543, Start
            out.append(fmt.drawHidden("REFRESH_FLAG", ""));
	         //Bug 82543, End
            //Bug 89703, Start
            out.append(fmt.drawHidden("HASPLANNING", ""));
            //Bug 89703, End

            appendDirtyJavaScript("window.name = \"ReportInWO\";\n");
    
            appendDirtyJavaScript("if ('");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(unissue)); // Bug Id 68773
            appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"PageID_my_cookie1\")==\"TRUE\")\n");
            appendDirtyJavaScript("{ \n");
            appendDirtyJavaScript("      if (confirm(\""); 
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATEREPWOORDERSMUNISSUE: All required material has not been issued. Do you want to continue ?"));
            appendDirtyJavaScript("\")) {\n");
            appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"AAAA\";\n");
            appendDirtyJavaScript("		writeCookie(\"PageID_my_cookie1\", \"FALSE\", '', COOKIE_PATH); \n");
            appendDirtyJavaScript("		f.submit();\n");
            appendDirtyJavaScript("     } \n");
            appendDirtyJavaScript("} \n");

            // Bug 72214, start
            appendDirtyJavaScript("if ('");
            appendDirtyJavaScript(sIsLastWo);
            appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"PageID_IsLastWoRep1\")==\"TRUE\")\n");
            appendDirtyJavaScript("{ \n");
            appendDirtyJavaScript("      alert(\"");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATEREPORTINWORKORDERISLASTWO: This is the last work order in the work order structure, remember to also finish the topmost work order."));
            appendDirtyJavaScript("\");\n");
            appendDirtyJavaScript("	 document.form.BUTTONVAL.value = \"ISLASTWO\";\n");
            appendDirtyJavaScript("	 writeCookie(\"PageID_IsLastWoRep1\", \"FALSE\", '',COOKIE_PATH); \n");
            appendDirtyJavaScript("	 f.submit();\n");
            appendDirtyJavaScript("} \n");
            // Bug 72214, end
         
            appendDirtyJavaScript("if ('");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(unissue)); // Bug Id 68773 
            appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"PageID_my_cookie2\")==\"TRUE\")\n");
            appendDirtyJavaScript("{ \n");
            appendDirtyJavaScript("      if (confirm(\""); 
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATEREPWOORDERSMUNISSUE: All required material has not been issued. Do you want to continue ?"));
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
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATEREPORTINWORKORDERREPAIR: This object is in repair shop. Do you still want to continue?"));
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
    
            // XSS_Safe ILSOLK 20070706
            appendDirtyJavaScript("function lovEmpSignature(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	ss = '");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(comp));
            appendDirtyJavaScript("';\n");
            appendDirtyJavaScript("	if('");
            appendDirtyJavaScript(isFind); // XSS_Safe ILSOLK 20070713
            appendDirtyJavaScript("' == 'true')\n");
            appendDirtyJavaScript("  { \n");
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
            appendDirtyJavaScript("   openLOVWindow('EMP_SIGNATURE',i,\n");
            appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
            appendDirtyJavaScript(lblempsig);
            appendDirtyJavaScript("&__TITLE=");
            appendDirtyJavaScript(empsig);
            appendDirtyJavaScript("'\n");
            appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
            appendDirtyJavaScript("		,600,445,'validateEmpSignature');\n");
            appendDirtyJavaScript("   }\n");
            appendDirtyJavaScript("}\n"); 
    
            // XSS_Safe ILSOLK 20070706
            appendDirtyJavaScript("function lovReportedBy(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	ss = '");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(comp));
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
            appendDirtyJavaScript("'		\n");
            appendDirtyJavaScript("		,600,450,'validateReportedBy');\n");
            appendDirtyJavaScript("    }\n");
            appendDirtyJavaScript("    else\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("	   openLOVWindow('REPORTED_BY',i,\n");
            appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
            appendDirtyJavaScript(lblrepBy1);
            appendDirtyJavaScript("+by&__TITLE=");
            appendDirtyJavaScript(repBy1);
            appendDirtyJavaScript("'\n");
            appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
            appendDirtyJavaScript("		,600,450,'validateReportedBy');  	\n");
            appendDirtyJavaScript("	}	\n");
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
            appendDirtyJavaScript("                     '../mscomw/OrgCodeAllowedSiteLov.page?__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
            appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n"); 
            appendDirtyJavaScript("                      + '&ORG_CODE=' + URLClientEncode(key_value)\n");
            appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
            appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");       
            appendDirtyJavaScript("       	             ,550,500,'validateItem1OrgCode');\n");
            appendDirtyJavaScript("             }\n");
            appendDirtyJavaScript("	            else\n");
            appendDirtyJavaScript("             {\n");
            appendDirtyJavaScript("                 openLOVWindow('ITEM1_ORG_CODE',i,\n");
            appendDirtyJavaScript("                     '../mscomw/MaintEmployeeLov.page?&__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
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
            //Bug 83757,Start
            appendDirtyJavaScript("APP_ROOT + 'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMP_ROLE_LOV3&__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
            //Bug 83757,End
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
            //Bug 83757,Start 
            appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.EMP_NO.value) + \"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM1_CONTRACT.value)+\"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM1_ORG_CODE.value)+\"' \";\n");
            //Bug 83757,End
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
            //Bug 83757,Start
            appendDirtyJavaScript("                  + '&ITEM1_CATALOG_CONTRACT=' +URLClientEncode(getValue_('ITEM1_CATALOG_CONTRACT',i))\n");
            appendDirtyJavaScript("                  + '&ALTERNATIVE_CUSTOMER=' +URLClientEncode(getValue_('ALTERNATIVE_CUSTOMER',i))\n");
            appendDirtyJavaScript("                  + '&AGREEMENT_ID=' +URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
            appendDirtyJavaScript("                  + '&CUSTOMER_NO=' +URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
            appendDirtyJavaScript("                  + '&PRICE_LIST_NO=' +URLClientEncode(getValue_('PRICE_LIST_NO',i))\n");
            //Bug 83757,End
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
	    appendDirtyJavaScript(                 mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERNOTVALIDQTY: Reported hours value for "));
	    appendDirtyJavaScript("'+f.ITEM1_WO_NO.value+' ");
	    appendDirtyJavaScript(                 mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERNOTVALIDQTY1: is not allowed to be zero."));
	    appendDirtyJavaScript("             ');\n");
	    appendDirtyJavaScript("   return false;\n"); 
	    appendDirtyJavaScript("   } \n");
	    //Bug 89399, End

            appendDirtyJavaScript("  return checkItem1Contract(0) &&\n");
            appendDirtyJavaScript("  checkQty(0) &&\n");
            appendDirtyJavaScript("  checkItem1OrgCode(0) &&\n");
            appendDirtyJavaScript("  checkAmount(0);\n");
            appendDirtyJavaScript("}\n"); 
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
            appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
            appendDirtyJavaScript("		+ '&ITEM1_ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");             appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'EMP_NO',i,'Employee ID') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('EMP_NO',i,0);\n");
            appendDirtyJavaScript("		assignValue_('ROLE_CODE',i,1);\n");
            appendDirtyJavaScript("		assignValue_('EMP_SIGNATURE',i,2);\n");
            appendDirtyJavaScript("		assignValue_('NAME',i,3);\n");
            appendDirtyJavaScript("		assignValue_('ITEM1_ORG_CODE',i,4);\n");
            appendDirtyJavaScript("		assignValue_('ITEM1_CONTRACT',i,5);\n");
            appendDirtyJavaScript("		assignValue_('CMNT',i,6);\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("    if ( j == -1 )\n");
            appendDirtyJavaScript("    {\n");
            appendDirtyJavaScript("        validateItem1OrgCode(1);\n");
            appendDirtyJavaScript("        validateRoleCode(1);\n");
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
            //Bug 83757, Start
            appendDirtyJavaScript("		+ '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
            //Bug 83757, End
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'ITEM1_ORG_CODE',i,'Maintenance Organization') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('ITEM1_ORG_CODE',i,0);\n");
            appendDirtyJavaScript("		assignValue_('ITEM1_CONTRACT',i,1);\n");
            appendDirtyJavaScript("		assignValue_('AMOUNT',i,2);\n");
            appendDirtyJavaScript("		assignValue_('CMNT',i,3);\n");
            appendDirtyJavaScript("		assignValue_('CATALOG_NO',i,4);\n");
            appendDirtyJavaScript("		assignValue_('SALESPARTCATALOGDESC',i,5);\n");
            appendDirtyJavaScript("		assignValue_('SALESPARTCOST',i,6);\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("    if (j == -1)\n");
            appendDirtyJavaScript("        validateCmnt(1);\n"); 
            appendDirtyJavaScript("    j = 0;\n");
            appendDirtyJavaScript("}\n"); 
    
            appendDirtyJavaScript("function validatePriceListNo(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
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
            appendDirtyJavaScript("		assignValue_('PRICE_LIST_NO',i,2);\n");
            appendDirtyJavaScript("		assignValue_('AGREEMENT_PRICE_FLAG',i,3);\n");
            appendDirtyJavaScript("	}\n");
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
            //Bug 83757,Start
             appendDirtyJavaScript("		+ '&ITEM1_CATALOG_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CATALOG_CONTRACT',i))\n");
             //Bug 83757,End
            appendDirtyJavaScript("		+ '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'ROLE_CODE',i,'Craft') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('ROLE_CODE',i,0);\n");
            appendDirtyJavaScript("		assignValue_('ITEM1_CONTRACT',i,1);\n");
            appendDirtyJavaScript("		assignValue_('LIST_PRICE',i,2);\n");
            appendDirtyJavaScript("		assignValue_('AMOUNT',i,3);\n");
            appendDirtyJavaScript("		assignValue_('AMOUNTSALES',i,4);\n");
            appendDirtyJavaScript("		assignValue_('CMNT',i,5);\n");
            appendDirtyJavaScript("             assignValue_('DISCOUNT',i,6);\n");
            appendDirtyJavaScript("		assignValue_('ITEM1DISCOUNTEDPRICE',i,7);\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("    if (j == -1)\n");
            appendDirtyJavaScript("    {\n");
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
            appendDirtyJavaScript("		+ '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'CATALOG_NO',i,'Sales Part Number') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('LIST_PRICE',i,0);\n");
            appendDirtyJavaScript("		assignValue_('AMOUNT',i,1);\n");
            appendDirtyJavaScript("		assignValue_('AMOUNTSALES',i,2);\n");
            appendDirtyJavaScript("		assignValue_('PRICE_LIST_NO',i,3);\n");
            appendDirtyJavaScript("		assignValue_('CMNT',i,4);\n");
            appendDirtyJavaScript("		assignValue_('SALESPARTCATALOGDESC',i,5);\n");
            appendDirtyJavaScript("		assignValue_('SALESPARTCOST',i,6);\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("    if (j == -1)\n");
            appendDirtyJavaScript("        validatePriceListNo(1);\n");
            appendDirtyJavaScript("        validateCmnt(1);\n"); 
            appendDirtyJavaScript("    j = 0;\n");
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
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPERATEREPORTINWORKORDERERRMSG: Latest Completion Date is earlier than Required Start Date"));
            appendDirtyJavaScript("');\n");
            appendDirtyJavaScript("			f.REQUIRED_END_DATE.value = '';\n");
            appendDirtyJavaScript("		}\n");
            appendDirtyJavaScript("		else\n");
            appendDirtyJavaScript("		{\n");
            appendDirtyJavaScript("			assignValue_('REQUIRED_END_DATE',i,0);\n");
            appendDirtyJavaScript("		}\n");
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
            appendDirtyJavaScript("		+ '&ITEM5_CONDITION_CODE=' + URLClientEncode(getValue_('ITEM5_CONDITION_CODE',i))\n");
            appendDirtyJavaScript("		+ '&PART_OWNERSHIP=' + URLClientEncode(getValue_('PART_OWNERSHIP',i))\n");      
            appendDirtyJavaScript("		+ '&ITEM_WO_NO=' + URLClientEncode(getValue_('ITEM_WO_NO',i))\n");             appendDirtyJavaScript("		);\n");
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
            appendDirtyJavaScript("		assignValue_('ITEM5_CONDITION_CODE',i,9);\n");
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
    
            appendDirtyJavaScript("if('");
            appendDirtyJavaScript(availDetailPath);
            appendDirtyJavaScript("' != \"\")\n");
            appendDirtyJavaScript("{ \n");
            appendDirtyJavaScript("   jBuff = \"");
            appendDirtyJavaScript(fmtdBuffer);
            appendDirtyJavaScript("\";\n");
            appendDirtyJavaScript("   window.open('");
            appendDirtyJavaScript(availDetailPath);
            appendDirtyJavaScript("'+URLClientEncode(jBuff)");
            appendDirtyJavaScript(",\"wndAvailDetail\",\"scrollbars,resizable,status=yes,width=770,height=575\");\n");
            appendDirtyJavaScript("}\n"); 
            appendDirtyJavaScript("if('");
            appendDirtyJavaScript(supPerPartPath);
            appendDirtyJavaScript("' != \"\")\n");
            appendDirtyJavaScript("{ \n");
            appendDirtyJavaScript("   jBuff = \"");
            appendDirtyJavaScript(fmtdBuffer);
            appendDirtyJavaScript("\";\n");
            appendDirtyJavaScript("   window.open('");
            appendDirtyJavaScript(supPerPartPath);
            appendDirtyJavaScript("'+URLClientEncode(jBuff)");
            appendDirtyJavaScript(",\"wndSupPerPart\",\"scrollbars,resizable,status=yes,width=770,height=575\");\n");
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
    
            appendDirtyJavaScript("function validateItem6Qty(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkItem6Qty(i) ) return;\n");
            appendDirtyJavaScript("	window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	r = __connect(\n");
            appendDirtyJavaScript("		'");
            appendDirtyJavaScript(mgr.getURL());
            appendDirtyJavaScript("?VALIDATE=ITEM6_QTY'\n");
            appendDirtyJavaScript("		+ '&ITEM6_QTY=' + URLClientEncode(getValue_('ITEM6_QTY',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM6_PLANNED_HOUR',i))\n");
            appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
            appendDirtyJavaScript("		+ '&TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_WO_NO=' + URLClientEncode(getValue_('ITEM6_WO_NO',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_CATALOG_NO=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO_CONTRACT',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_SALES_PRICE=' + URLClientEncode(getValue_('ITEM6_SALES_PRICE',i))\n");
            appendDirtyJavaScript("          + '&ITEM6_DISCOUNT=' + URLClientEncode(getValue_('ITEM6_DISCOUNT',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	window.status='';\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'ITEM6_QTY',i,'Quantity') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_COST_AMOUNT',i,0);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_PLANNED_PRICE',i,1);\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");
    
            appendDirtyJavaScript("function validateItem6PlannedHour(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkItem6PlannedHour(i) ) return;\n");
            appendDirtyJavaScript("	window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	r = __connect(\n");
            appendDirtyJavaScript("		'");
            appendDirtyJavaScript(mgr.getURL());
            appendDirtyJavaScript("?VALIDATE=ITEM6_PLANNED_HOUR'\n");
            appendDirtyJavaScript("		+ '&ITEM6_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM6_PLANNED_HOUR',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_QTY=' + URLClientEncode(getValue_('ITEM6_QTY',i))\n");
            appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
            appendDirtyJavaScript("		+ '&TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_WO_NO=' + URLClientEncode(getValue_('ITEM6_WO_NO',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_CATALOG_NO=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO_CONTRACT',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_SALES_PRICE=' + URLClientEncode(getValue_('ITEM6_SALES_PRICE',i))\n");
            appendDirtyJavaScript("                + '&ITEM6_DISCOUNT=' + URLClientEncode(getValue_('ITEM6_DISCOUNT',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	window.status='';\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'ITEM6_PLANNED_HOUR',i,'Planned Hours') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_COST_AMOUNT',i,0);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_PLANNED_PRICE',i,1);\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");
    
            appendDirtyJavaScript("function validateItem6Discount(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("        if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkItem6Discount(i) ) return;\n");
            appendDirtyJavaScript("	window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	r = __connect(\n");
            appendDirtyJavaScript("		'");
            appendDirtyJavaScript(mgr.getURL());
            appendDirtyJavaScript("?VALIDATE=ITEM6_DISCOUNT'\n");
            appendDirtyJavaScript("		+ '&ITEM6_DISCOUNT=' + URLClientEncode(getValue_('ITEM6_DISCOUNT',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_SALES_PRICE=' + URLClientEncode(getValue_('ITEM6_SALES_PRICE',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO_CONTRACT',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_CATALOG_NO=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_WO_NO=' + URLClientEncode(getValue_('ITEM6_WO_NO',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_QTY=' + URLClientEncode(getValue_('ITEM6_QTY',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM6_PLANNED_HOUR',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_SALES_PRICE=' + URLClientEncode(getValue_('ITEM6_SALES_PRICE',i))\n");
            appendDirtyJavaScript("                + '&ITEM6_DISCOUNT=' + URLClientEncode(getValue_('ITEM6_DISCOUNT',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	window.status='';\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'ITEM6_DISCOUNT',i,'Discount') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_DISCOUNTED_PRICE',i,0);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_PLANNED_PRICE',i,1);\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");
    
            appendDirtyJavaScript("function validateToolFacilityId(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkToolFacilityId(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('TOOL_FACILITY_ID',i)=='' )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		getField_('TOOL_FACILITY_DESC',i).value = '';\n");
            appendDirtyJavaScript("		validateToolFacilityType(i);\n");
            appendDirtyJavaScript("		return;\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	r = __connect(\n");
            appendDirtyJavaScript("		'");
            appendDirtyJavaScript(mgr.getURL());
            appendDirtyJavaScript("?VALIDATE=TOOL_FACILITY_ID'\n");
            appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_CONTRACT=' + URLClientEncode(getValue_('ITEM6_CONTRACT',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_ORG_CODE=' + URLClientEncode(getValue_('ITEM6_ORG_CODE',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_WO_NO=' + URLClientEncode(getValue_('ITEM6_WO_NO',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_QTY=' + URLClientEncode(getValue_('ITEM6_QTY',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM6_PLANNED_HOUR',i))\n");
            appendDirtyJavaScript("                + '&ITEM6_NOTE=' + URLClientEncode(getValue_('ITEM6_NOTE',i))\n");
            appendDirtyJavaScript("                + '&ITEM6_CATALOG_NO=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO',i))\n");
            appendDirtyJavaScript("                + '&ITEM6_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO_CONTRACT',i))\n");
            appendDirtyJavaScript("                + '&ITEM6_CATALOG_NO_DESC=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO_DESC',i))\n");
            appendDirtyJavaScript("                + '&ITEM6_SALES_PRICE=' + URLClientEncode(getValue_('ITEM6_SALES_PRICE',i))\n");
            appendDirtyJavaScript("                + '&ITEM6_SALES_CURRENCY=' + URLClientEncode(getValue_('ITEM6_SALES_CURRENCY',i))\n");
            appendDirtyJavaScript("                + '&ITEM6_DISCOUNT=' + URLClientEncode(getValue_('ITEM6_DISCOUNT',i))\n");
            appendDirtyJavaScript("                + '&ITEM6_DISCOUNTED_PRICE=' + URLClientEncode(getValue_('ITEM6_DISCOUNTED_PRICE',i))\n");
            appendDirtyJavaScript("                + '&ITEM6_PLANNED_PRICE=' + URLClientEncode(getValue_('ITEM6_PLANNED_PRICE',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	window.status='';\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'TOOL_FACILITY_ID',i,'Tool/Facility Id') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_DESC',i,0);\n");
            appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_TYPE',i,1);\n");
            appendDirtyJavaScript("		assignValue_('TYPE_DESCRIPTION',i,2);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_QTY',i,3);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_COST',i,4);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_COST_CURRENCY',i,5);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_COST_AMOUNT',i,6);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_CATALOG_NO_CONTRACT',i,7);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_CATALOG_NO',i,8);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_CATALOG_NO_DESC',i,9);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_SALES_PRICE',i,10);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_SALES_CURRENCY',i,11);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_DISCOUNT',i,12);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_DISCOUNTED_PRICE',i,13);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_PLANNED_PRICE',i,14);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_NOTE',i,15);\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");
    
            appendDirtyJavaScript("function validateItem6Contract(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkItem6Contract(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('ITEM6_CONTRACT',i)!='' )\n");
            appendDirtyJavaScript("        {\n");
            appendDirtyJavaScript("	window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	r = __connect(\n");
            appendDirtyJavaScript("		'");
            appendDirtyJavaScript(mgr.getURL());
            appendDirtyJavaScript("?VALIDATE=ITEM6_CONTRACT'\n");
            appendDirtyJavaScript("		+ '&ITEM6_CONTRACT=' + URLClientEncode(getValue_('ITEM6_CONTRACT',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_ORG_CODE=' + URLClientEncode(getValue_('ITEM6_ORG_CODE',i))\n");
            appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
            appendDirtyJavaScript("		+ '&TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_QTY=' + URLClientEncode(getValue_('ITEM6_QTY',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM6_PLANNED_HOUR',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_WO_NO=' + URLClientEncode(getValue_('ITEM6_WO_NO',i))\n");
            appendDirtyJavaScript("                + '&ITEM6_NOTE=' + URLClientEncode(getValue_('ITEM6_NOTE',i))\n");
            appendDirtyJavaScript("                + '&ITEM6_CATALOG_NO=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO',i))\n");
            appendDirtyJavaScript("                + '&ITEM6_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO_CONTRACT',i))\n");
            appendDirtyJavaScript("                + '&ITEM6_CATALOG_NO_DESC=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO_DESC',i))\n");
            appendDirtyJavaScript("                + '&ITEM6_SALES_PRICE=' + URLClientEncode(getValue_('ITEM6_SALES_PRICE',i))\n");
            appendDirtyJavaScript("                + '&ITEM6_SALES_CURRENCY=' + URLClientEncode(getValue_('ITEM6_SALES_CURRENCY',i))\n");
            appendDirtyJavaScript("                + '&ITEM6_DISCOUNT=' + URLClientEncode(getValue_('ITEM6_DISCOUNT',i))\n");
            appendDirtyJavaScript("                + '&ITEM6_DISCOUNTED_PRICE=' + URLClientEncode(getValue_('ITEM6_DISCOUNTED_PRICE',i))\n");
            appendDirtyJavaScript("                + '&ITEM6_PLANNED_PRICE=' + URLClientEncode(getValue_('ITEM6_PLANNED_PRICE',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	window.status='';\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'ITEM6_CONTRACT',i,'Site') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_NOTE',i,0);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_QTY',i,1);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_COST_AMOUNT',i,2);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_CATALOG_NO_CONTRACT',i,3);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_CATALOG_NO',i,4);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_CATALOG_NO_DESC',i,5);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_SALES_PRICE',i,6);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_SALES_CURRENCY',i,7);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_DISCOUNT',i,8);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_DISCOUNTED_PRICE',i,9);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_PLANNED_PRICE',i,10);\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("    }\n");
            appendDirtyJavaScript("}\n");
    
            appendDirtyJavaScript("function validateItem6OrgCode(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkItem6OrgCode(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('ITEM6_ORG_CODE',i)!='' )\n");
            appendDirtyJavaScript("{\n");                
            appendDirtyJavaScript("	window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	r = __connect(\n");
            appendDirtyJavaScript("		'");
            appendDirtyJavaScript(mgr.getURL());
            appendDirtyJavaScript("?VALIDATE=ITEM6_ORG_CODE'\n");
            appendDirtyJavaScript("		+ '&ITEM6_ORG_CODE=' + URLClientEncode(getValue_('ITEM6_ORG_CODE',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_CONTRACT=' + URLClientEncode(getValue_('ITEM6_CONTRACT',i))\n");
            appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
            appendDirtyJavaScript("		+ '&TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_QTY=' + URLClientEncode(getValue_('ITEM6_QTY',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM6_PLANNED_HOUR',i))\n");
            appendDirtyJavaScript("		+ '&ITEM6_WO_NO=' + URLClientEncode(getValue_('ITEM6_WO_NO',i))\n");
            appendDirtyJavaScript("         + '&ITEM6_NOTE=' + URLClientEncode(getValue_('ITEM6_NOTE',i))\n");
            appendDirtyJavaScript("         + '&ITEM6_CATALOG_NO=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO',i))\n");
            appendDirtyJavaScript("         + '&ITEM6_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO_CONTRACT',i))\n");
            appendDirtyJavaScript("         + '&ITEM6_CATALOG_NO_DESC=' + URLClientEncode(getValue_('ITEM6_CATALOG_NO_DESC',i))\n");
            appendDirtyJavaScript("         + '&ITEM6_SALES_PRICE=' + URLClientEncode(getValue_('ITEM6_SALES_PRICE',i))\n");
            appendDirtyJavaScript("         + '&ITEM6_SALES_CURRENCY=' + URLClientEncode(getValue_('ITEM6_SALES_CURRENCY',i))\n");
            appendDirtyJavaScript("         + '&ITEM6_DISCOUNT=' + URLClientEncode(getValue_('ITEM6_DISCOUNT',i))\n");
            appendDirtyJavaScript("         + '&ITEM6_DISCOUNTED_PRICE=' + URLClientEncode(getValue_('ITEM6_DISCOUNTED_PRICE',i))\n");
            appendDirtyJavaScript("         + '&ITEM6_PLANNED_PRICE=' + URLClientEncode(getValue_('ITEM6_PLANNED_PRICE',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	window.status='';\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'ITEM6_ORG_CODE',i,'Maintenance Organization') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_NOTE',i,0);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_QTY',i,1);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_COST_AMOUNT',i,2);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_CATALOG_NO_CONTRACT',i,3);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_CATALOG_NO',i,4);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_CATALOG_NO_DESC',i,5);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_SALES_PRICE',i,6);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_SALES_CURRENCY',i,7);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_DISCOUNT',i,8);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_DISCOUNTED_PRICE',i,9);\n");
            appendDirtyJavaScript("		assignValue_('ITEM6_PLANNED_PRICE',i,10);\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");
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
            appendDirtyJavaScript("         + '&SUPPLY_CODE=' + SelectURLClientEncode('SUPPLY_CODE',i)		+ '&ITEM5_MAINT_MATERIAL_ORDER_NO=' + URLClientEncode(getValue_('ITEM5_MAINT_MATERIAL_ORDER_NO',i))");
            appendDirtyJavaScript("		+ '&ITEM3_ACTIVITY_SEQ=' + URLClientEncode(getValue_('ITEM3_ACTIVITY_SEQ',i))\n");
            appendDirtyJavaScript("		+ '&ITEM5_CONDITION_CODE=' + URLClientEncode(getValue_('ITEM5_CONDITION_CODE',i))\n");
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
            appendDirtyJavaScript("                    + '&ITEM3_WO_NO=' + URLClientEncode(getValue_('ITEM3_WO_NO',i))\n");
            appendDirtyJavaScript("		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
            appendDirtyJavaScript("		+ '&SPARE_CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))\n");
            appendDirtyJavaScript("		+ '&ITEM3_ACTIVITY_SEQ=' + URLClientEncode(getValue_('ITEM3_ACTIVITY_SEQ',i))\n");
            appendDirtyJavaScript("         + '&SUPPLY_CODE=' + SelectURLClientEncode('SUPPLY_CODE',i)		+ '&ITEM5_MAINT_MATERIAL_ORDER_NO=' + URLClientEncode(getValue_('ITEM5_MAINT_MATERIAL_ORDER_NO',i))");
            appendDirtyJavaScript("		+ '&ITEM5_CONDITION_CODE=' + URLClientEncode(getValue_('ITEM5_CONDITION_CODE',i))\n");   
            appendDirtyJavaScript("		+ '&PART_OWNERSHIP=' + URLClientEncode(getValue_('PART_OWNERSHIP',i))\n");      
            appendDirtyJavaScript("		+ '&ITEM_WO_NO=' + URLClientEncode(getValue_('ITEM_WO_NO',i))\n");
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
    
    
            if (itemlay.isSingleLayout() && bUnequalMatWo)
            {
                appendDirtyJavaScript("         if (confirm('");
                appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATEREPWORKORDERDIFFCUST: Owning Customer No is not the same as the Work Order Customer No. Still want to save this record?"));
                appendDirtyJavaScript("'))\n"); 
                appendDirtyJavaScript("   { \n");
                appendDirtyJavaScript("      setITEM5Command('issueFromInvent');commandSet('ITEM5.Perform','');\n");
                appendDirtyJavaScript("   } \n");
            }
            else if (itemlay.isSingleLayout() && bNoWoCust)
            {
                appendDirtyJavaScript("         if (confirm('");
                appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATEREPWORKORDERNOCUST: There is no Customer specified for the Work Order although you have entered an Owning Customer No. Still want to save this record?"));
                appendDirtyJavaScript("'))\n");
                appendDirtyJavaScript("   { \n");
                appendDirtyJavaScript("      setITEM5Command('issueFromInvent');commandSet('ITEM5.Perform','');\n");
                appendDirtyJavaScript("   } \n");
            }
            else if (itemlay.isMultirowLayout() && bUnequalMatWo)
            {
                appendDirtyJavaScript("         if (confirm('");
                appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATEREPWORKORDERDIFFCUST: Owning Customer No is not the same as the Work Order Customer No. Still want to save this record?"));
                appendDirtyJavaScript("'))\n"); 
                appendDirtyJavaScript("   { \n");
                appendDirtyJavaScript("      initPop5(" + currrowItem4 + ");_menu(0);setTableCommand5('issueFromInvent');commandSet('ITEM5.Perform','');\n"); // XSS_Safe ILSOLK 20070713
                appendDirtyJavaScript("   } \n");
            }
            else if (itemlay.isMultirowLayout() && bNoWoCust)
            {
                appendDirtyJavaScript("         if (confirm('");
                appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATEREPWORKORDERNOCUST: There is no Customer specified for the Work Order although you have entered an Owning Customer No. Still want to save this record?"));
                appendDirtyJavaScript("'))\n");
                appendDirtyJavaScript("   { \n");
                appendDirtyJavaScript("      initPop5(" + currrowItem4 + ");_menu(0);setTableCommand5('issueFromInvent');commandSet('ITEM5.Perform','');\n"); // XSS_Safe ILSOLK 20070713
                appendDirtyJavaScript("   } \n");
            }
    
            // XSS_Safe ILSOLK 20070706
            if (bOpenNewWindow)
            {
                appendDirtyJavaScript("if (readCookie(\"PageID_CurrentWindow\") == \"*\")");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("  window.open(\"");
                appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));
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
            
            appendDirtyJavaScript("function checkITEM7SaveParams(i)\n");
            appendDirtyJavaScript("{");
            appendDirtyJavaScript("   r = __connect(\n");
            appendDirtyJavaScript("		  '");
            appendDirtyJavaScript(mgr.getURL());
            appendDirtyJavaScript("?VALIDATE=STD_JOB_FLAG'\n");
            appendDirtyJavaScript("       + '&ITEM7_WO_NO=' + URLClientEncode(getValue_('ITEM7_WO_NO',i))\n");
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
            appendDirtyJavaScript("                  && (getValue_('S_STD_JOB_ID',i) != getValue_('STD_JOB_ID',i) || getValue_('S_STD_JOB_REVISION',i) != getValue_('STD_JOB_REVISION',i) || getValue_('N_QTY',i) != getValue_('ITEM7_QTY',i)))\n");
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
            appendDirtyJavaScript("   return confirm('" + mgr.translateJavaScript("ACTSEPREPHEADERDISCACT: Are you sure you want to remove the Project Connection ?") + "');\n");
            appendDirtyJavaScript("}\n");
    
            appendDirtyJavaScript("function setActivitySeq(activitySeq)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   getField_('ACTIVITY_SEQ_PROJ', -1).value = activitySeq;\n");
            appendDirtyJavaScript("   commandSet('HEAD.connectToActivity','');\n");
            appendDirtyJavaScript("}\n");
    
            // XSS_Safe ILSOLK 20070706
            appendDirtyJavaScript("if('");
            appendDirtyJavaScript(performRMB);
            appendDirtyJavaScript("' == \"TRUE\")\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("  url_to_go = '");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(URLString));
            appendDirtyJavaScript("';\n");
            appendDirtyJavaScript("  window_name = '");
            appendDirtyJavaScript(WindowName);
            appendDirtyJavaScript("';\n");
            appendDirtyJavaScript("  window.open(url_to_go,window_name,\"resizable,status=yes,width=750,height=550\");\n");
            appendDirtyJavaScript("}\n");

            if (bCancelPage)
	    {
                appendDirtyJavaScript("  jwonumber = '");
                appendDirtyJavaScript(mgr.encodeStringForJavascript(wonumber));
                appendDirtyJavaScript("';\n");
                appendDirtyJavaScript("window.open ('EnterCancelCauseDlg.page?WO_NO='+URLClientEncode(jwonumber)+" + 
                                      "'&FRMNAME=ActiveSeprepInWO&QRYSTR="+mgr.URLEncode(qrystr)+"','cancelCause'," + 
                                      "'status=yes,resizable=1,scrollbars=yes,width=750,height=600'); \n");
            }

            if (bRecieveOrdCreated) 
               appendDirtyJavaScript("alert('" + mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERRECORDC1: Receive Order &1 has been created.",mgr.encodeStringForJavascript(sRecieveOrdNo)) + "');\n");
                  if (bReqStartDateEmpty)
                     appendDirtyJavaScript("alert('" + 
                                     mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDEREMPRED: Required Start is not specified for Work Order &1. Update corresponding Planned Receipt Date for Receive Order &2.",
                                                   mgr.encodeStringForJavascript(sWoNo),
                                                   mgr.encodeStringForJavascript(sRecieveOrdNo)) + "');\n");
            
            if (bRecieveOrdNotCreated) 
               appendDirtyJavaScript("alert('" + 
                                     mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDERRECORDNC: Receive Order was not Created.") + "');\n");

            if (bObjReturned)
               appendDirtyJavaScript("alert('" +
                                     mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDEROBJRETURED: Object was reserved on Customer Order &1 Line No &2.",
                                                   sResCustNo,
                                                   sResCustLineNo) + "');\n");

            if (bObjNotReturned)
               appendDirtyJavaScript("alert('" + 
                                     mgr.translate("PCMWACTIVESEPERATEREPORTINWORKORDEROBJNRET: Object was not reserved on a Customer Order.") +
                                     "');\n");
         
       out.append(mgr.endPresentation());
       out.append("</form>\n");
       out.append("</body>\n");
       out.append("</html>");
       return out;
    }

 }
