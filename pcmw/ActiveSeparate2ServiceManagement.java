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
*  File        : ActiveSeparate2ServiceManagement.java 
*  Created     : ASP2JAVA Tool  010226
*  Modified    :
*  BUNILK  010307  Corrected some conversion errors.
*  BUNILK  010331  Modified matReqUnissue() function so as to refresh the form after unIssue.
*                  Modified servreqvord() function to give alert messeges when 
*                  it is not nessecery to go to service request form. Added nessecery codes to 
*                  disable new button of Material line block after checking Material status.       
*                  Modified getCostVal() function and some validation parts of craft tab.     
*  BUNILK  010405  Modified preDefine() method so as to filter standerd job LOV by site.
*  CHDELK  010406  Modified the sparePartObject() to open the Spare Parts for Object form in Java Script 
*  BUNILK  010408  Overwritten saveReturn() methods for Material line Planning and Craft      
*                  blocks to repopulate budget block after new records of those blocks.                    
*  BUNILK  010417  Changed deprecated getQueryString() method to getQueryStringValue() new method.     
*                  Done some corrections in javascript codes. Changed passing parameter to "QUOTATION_ID" from "WO_NO"
*                  in prepWorkOrderQuot()   
*  JEWILK  010418  Removed Pre Posting actions in Material tab (Call 62860)
*  JEWILK  010423  Modified SaveReturn() to properly save the fields 'Warranty Type' & 'Supplier'
*                  when returned from the rmb 'Supplier Warranty Type'.(Call 77349)
*  CHDELK  010424  Changed all Date field lengths to 22
*  BUNILK  010425  Disabled edit,duplicate and delete buttons of material line block when the material state is "Closed";   
*  INROLK  010430  inro Changed Sales Price to ReadOnly.
*  BUNILK  010601  Changed newRowITEM2() so as to get Work order's Site and Department as default values for 
*                  new craft line.           
*  BUNILK  010608  Modified all validations of Material line.
*  BUNILK  010611  Changed all the overwritten javascript validation functions so as to work with netscape browser.
*  JEWILK  010620  Changed Craft tab according to new chages in Centura. Modified all the validations of Craft tab. 
*                  Set 'Sales Price' and 'Discount' editable (call #65625).
*  BUNILK  010716  Modified PART_NO field validation, Added Copy... Action.  
*  BUNILK  010806  Made some changes in return AutoString of getContents() function so that to call       
*                  checkPartVal() javascript function from onBlur event of PART_NO field. Done some changes in 
*                  validatePartNo() method also.
*  CHCRLK  010807  Modified methods printWO(), printServO() and printPicList(). 
*  BUNILK  010808  Overwrote delete methods of Craft and Material Line blocks. 
*  BUNILK  010813  Added new Action "Pick List For Work Order - Printout.." to header part.
*  BUNILK  010818  Modified newRowITEM5() method.
*  BUNILK  010823  Modified validation part of "ITEM5_CATALOG_NO" field.
*  ARWILK  010904  Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction(). 
*                  (Caused when cancelNew is performed after saveNew)  
*  BUNILK  010918  Added Pre Posting Action to Material and Material requisition blocks.  
*  BUNILK  011011  Added addCommandValidConditions() method for headblk state changing actions.  
*  BUNILK  011011  Added addCommandValidConditions() method for headblk state changing actions.  
*  JEWILK  011015  Modified validations of fields 'PLAN_QTY','CATALOG_NO','PRICE_LIST_NO' to 
*                  correctly display 'Sales Price' and 'Discounted Price'.
*  BUNILK  011016  Added Back hyperlinkt to Material block and a new field "Man Hours" to headblk. 
*  BUNILK  011016  Modified validations of Planning block.
*  KUWILK  020401  Call ID 78674 corrected.
*  SHAFLK  020429  Bug 29239,Removed function validateItem2PlanHrs().
*  SHAFLK  020508  Bug 29946,Added hyperlinks for the operations "Spare Parts in Object" and "Object Structure" under the header level of Material section. 
*  SHAFLK  020509  Bug 29943,Made changes to refresh in same page after RMB retrn. 
*  SHAFLK  020726  Bug Id 31771 Added security check for MOBMGR_WORK_ORDER_API.Check_Exist & Work_Center_Int_API.
*  BUNILK  020906  Bug ID 32182 Added a new method isModuleInst() to check availability of 
*                  DOCMAN,SERORD and ORDER module inside preDefine method. 
*  SHAFLK  021108  Bug Id 34064,Changed methods printPicList,pickListForWork,printServO & printWO.    
*  BUNILK  021121  Added three new fields (Object Site, Connection Type and Object description) and replaced 
*                  work order site by Object site whenever it refer to Object.    
*  BUNILK  021213  Merged with 2002-3 SP3
*  SHAFLK  030110  Changed validation of PRICE_LIST_NO. 
*  SHAFLK  030225  Changed HTML part.
*  CHCRLK  030610  Added action "Create MRO Object Receive Order" to the header.
*  CHCRLK  030610  Modified properties for field CONNECTION_TYPE_DB.
*  INROLK  030624  IID ADAM305NA Added RMB Returns.
*  CHCRLK  030821  Modified properties of field ITEM3_WO_NO in Materials section.
*  CHAMLK  030923  Added RMB Tool and Facilities.
*  ARWILK  031008  (Bug#106197) Modified functions preDefine and validate, also moved check boxes from 'General' group to the 'Work Order' group.(Check method comments)
*  CHAMLK  031018  Modifed validations of std job, customer no and agreement id in order to update the work order with valid 
*                  agreement prices, if one exists.
*  THWILK  031018  Call ID 108197-Changed the constant PCMWACTIVESEPARATE2MRECEIVEO.
*  CHAMLK  031020  Modified functions checkObjAvaileble() and checkMROObjRO() to enable action Create MRO Object Receive Order for EQUIPMENT objects as well.
*  CHCRLK  031024  Added Owner Code fields and modified the cost validations to consider Condition Code in Materials.
*  JEWILK  031025  Modified method checkMROObjRO() to check the conditions according to centura. Call 100823.
*  CHAMLK  031028  Modified procedure preDefine() to modify the format of the ITEM_WO_NO.
*  CHCRLK  031031  Modified method saveReturn() to repopulate the budget tab after saving header data.
*  CHAMLK  031110  Added Operation No to item block for Material Requisition Line.
*  JEWILK  031111  Renamed tab 'Crafts' as 'Operations'. Call 110575.
*  JEWILK  031111  Added field 'SIGN_ID' to itemblk2 and its validation. Made field 'SIGN' as Read Only.
*                  Modified method getSalesPartInfo() to use for the SIGN_ID validation as well. Call 110576. 
*  ARWILK  031204  Edge Developments - (Replaced blocks with tabs, Replaced State links with RMB's, Removed Clone and Reset Methods)
*  ARWILK  031224  Edge Developments - (Replaced links with multirow RMB's)
*  DIMALK  040120  Replaced calls to package Active_Separate1_API with Active_Separate_API
*  ARWILK  040123  Edge Developments - (Enable Multirow RMB actions, Remove uneccessary global variables, Enhance Performance, Clean Javascript Code)
*  SAPRLK  040209  Web Alignment - calls and security checks to ActiveSeparate2RMB.page has been set to ActiveSeparateReportCOInfo.page.
*  VAGULK  040303  Web Alignment - Arranged field order according to Centura application.
*  THWILK  040304  Call ID 112686,Disallowed Quantity to be updated on planning tab for cost types Material,Personnel and Tools.Also disallowed
*                  registering of new lines for cost type Tools.Modified saveReturnItem5() & added java script method.
*  THWILK  040318  Call ID 112865 - Modified prePost() and added javascript code to open the preposting page in another window.
*  ARWILK  040319  Bug#112936 - Added Purchase Requisitions and Corrected the Population of the Budget Tab.
*  ARWILK  040319  Bug#112772 - Corrected the problem of Status RMB popups.
*  ARWILK  040319  Modified getContent().
*  SHAFLK  040113  Bug Id 41286, Changed validation of ROKE_CODE.
*  SHAFLK  040119  Bug Id 41815, Removed Java dependencies.
*  SHAFLK  040213  Bug Id 40256, Modified sparePartObjectJS() and objStructureJS().
*  SHAFLK  040217  Bug 40788, Changed validation of SIGN_ID and modified method getSalesPartInfo.
*  SHAFLK  040308  Bug 42994, Added method saveNewItem5() and modified methods saveReturnItem5(), predefine().
*  SAPRLK  040323  Merge with SP1.
*  SAPRLK  040421  Corrected Call 114222.
*  ARWILK  040430  Added new field CBPROJCONNECTED.
*  SAPRLK  040603  Added key PM_REVISION, new lov for PM_NO, and to validate the PM_REVISION.
*  ARWILK  040609  Removed the field STD_JOB_ID (For IID AMEC109A).
*  ARWILK  040617  Added a new job tab. Added job_id to materials, operations, planning tabs.(Spec - AMEC109A)  
*  SHAFLK  040416  Bug 43848, Modified methods run(), finished() ,adjust() and HTML part.Added methods CheckObjInRepairShop() and finshed1().
*  THWILK  040625  Merged Bug 43848.
*  ARWILK  040629  Added RMB's connectToActivity and disconnectFromActivity (Spec AMME613A - Project Umbrella)
*  ARWILK  040709  Added ORG_CONTRACT to the itemblk2. (IID AMEC500C: Resource Planning) 
*  VAGULK  040721  Added fields "SUPPLY_CODE ,SUPPLY_CODE_DB and ACTIVITY_SEQ" to Material tab (SCME612 - Project Inventory)
*  NIJALK  040727  Added new parameter CONTRACT to method calls to Role_Sales_Part_API.Get_Def_Catalog_No.  
*  SHAFLK  040705  Bug 42838, Changed adjust(). 
*  THWILK  040806  Merged Bug 42838.
*  SHAFLK  040708  Bug 41817, Modified methods run(), finished(), reported() and HTML part.Added methods CheckAllMaterialIssued() and reported1().
*  THWILK  040810  Merged Bug 41817 and added required parameters to Make_Issue_Detail method call.
*  SHAFLK  040629  Bug 45550, Changed validation of QTY_TO_INVOICE. 
*  THWILK  040816  Merged Bug 45550.
*  NEKOLK  040706  Bug id 43810,added new fld LINE_DESCRIPTION ,and added fillLineDesc().call it from saveReturnItem6(),okFindITEM5().Modified Adjust().
*  THWILK  040817  Merged Bug 43810.
*  SHAFLK  040421  Bug 42866, Modified method issue().
*  THWILK  040818  Merged Bug 42866.
*  ARWILK  040820  Changed LOV's of STD_JOB_ID and STD_JOB_REVISION.
*  NIJALK  040820  Call 116952, Modified adjust().
*  NIJALK  040824  Call 116946, Modified validate(), predefine(), getContents().
*  BUNILK  040825  Changed server function of QTYONHAND field.
*  ThWilk  040825  Call 117308, Modified checkToolFacilitySite(),released(),started().
*  NIJALK  040826  Call 117104, Modified run().
*  VAGULK  040826  Corrected Calls 117249 and 117251,fetched the default values of "Owernership" and "Supply_code" in the new layout.
*  NIJALK  040827  Call 117451, modified adjust(), saveReturnITEM4(). 
*  NIJALK  040827  Removed method calls to Role_Sales_Part_API.Get_Def_Contract().
*  ARWILK  040830  Call ID 117423, Modified predefine.
*  NIJALK  040830  Call 117458, Modified validate().
*  ARWILK  040901  Resolved Material status refresh problem.
*  NIJALK  040901  Call 117415, Added saveReturnitem7(). Modified predefine(),validate().
*  SHAFLK  040722  Bug 43249, Modified validations of PART_NO, CONDITION_CODE, PART_OWNERSHIP and OWNER and added some fields to predefine and modified HTML part. 
*  NIJALK  040902  Merged bug 43249.
*  NIJALK  040908  Call 117750: Modified preDefine().
*  NIJALK  040909  Modified Validate(). Removed error msg given when sign_id does not exist.
*  ARWILK  040910  Modified availtoreserve.
*  NIJALK  040922  Modified perform().
*  NIJALK  040929  Added parameter project_id to method calls to Inventory_Part_In_Stock_API.Get_Inventory_Quantity.
*  BUNILK  040930  Changed server function of QTYONHAND field and modified validations of Part No, Condition Code, Ownership and owner fields of Material tab..
*  NIJALK  041001  Renamed 'Work Master' to 'Executed By' in head block & 'Signature' to 'Executed By' in Jobs,Operaions Tabs. 
                         Modified validations of SIGN,SIGN_ID. Set validation for ITEM2_JOB_ID.
*  SHAFLK  040906  Bug 46542, Modified method matReqUnissue().
*  NIJALK  041007  Merged 46542. 
*  SHAFLK  040812  Bug 45904, Modified method issue(), manissue() and HTML part. 
*  NIJALK  041007  Merged 45904.
*  ARWILK  041022  LOV Change for Fields STD_JOB_ID,STD_JOB_REVISION. (Spec AMEC111 - Standard Jobs as PM Templates)
*  BUNILK  041026  Modified validations of material tab, added new validation to supply code, modified new method of material tab
*  NIJALK  041028  Modified validate(), preDefine().
*  SHAFLK  040916  Bug 46621, Modified validations of PART_NO, CONDITION_CODE, PART_OWNERSHIP and OWNER. 
*  Chanlk  041105  Merged Bug 46621.
*  NIJALK  041105  Added field Std Job Status to job tab. Modified preDefine() and validate().
*  NIJALK  041109  Added check box "Has Obsolete Jobs".
*  ARWILK  041110  Replaced getContents with printContents and removed obsolete code.
*  NIJALK  041111  Made the field "Employee ID" to visible and read only in jobs and operations tab.
*  NIJALK  041115  Replaced method Standard_Job_API.Get_Work_Description with Standard_Job_API.Get_Definition.
*  ARWILK  041118  Added two new RMB's freeTimeSearch and freeTimeSearchFromOperations.
*  NIJALK  041201  Added new parameters to method calls to Inventory_Part_In_Stock_API.Get_Inventory_Quantity.
*  Chanlk  041213  Bug Id 120439, Removed field LAST_ACTIVITY_DATE.
*  Chanlk  041220  Call 120293: removed the security check for the CUSTOMER_ORDER_PRICING_API.Get_Sales_Part_Price_Info
*  NIJALK  040106  Modified predefine(),validate(),printContents().
*  NIJALK  050119  Modified validation to the field "ROLE_CODE".
*  NAMELK  050120  Materials Tab: Check Box MUL_REQ_LINE added,Set LOV to field ITEM3_CONTRACT. Code added to adjust().
*  NIJALK  050201  Modified run(),sparePartObject().
*  NIJALK  050201  Modified detchedPartList(),sparePartObject(),run(),validate(),preDefine().
*  Chanlk  050207  Set function for WO_KEY_VALUE.
*  NIJALK  050221  Added function duplicateRow(). Modified preDefine().
*  NIJALK  050224  Modified preDefine(),saveReturnItem(). Added deleteItem().
*  NAMELK  050224  Merged Bug 48035 manually.
*  DIAMLK  050228  Replaced the field Pre_Posting_Id by Pre_Accounting_Id.(IID AMEC113 - Mandatory Pre-posting)
*  DIAMLK  050310  Bug ID:122509 - Modified the method okFind().
*  DIAMLK  050317  Bug ID:122708 - Modified the method newRowITEM6().
*  DIAMLK  050321  Modified the method run().
*  NIJALK  050405  Call 123081: Modified manReserve().
*  NEKOLK  050406  Merged - Bug 48852, Modified issueFromInvent and reserve().
*  NIJALK  050407  Call 123086: Modified manReserve(), reserve().
*  NIJALK  050504  Bug 123677: Added RMB "Project Activity Info...".
*  NIJALK  050505  Bug 123698: Set a warning msg when changing state to "Prepared" if WO causes a project exception.
*  NIJALK  050506  Bug 123099: Modified validate(), preDefine(), double getCost(*,*,*...) and printContents().
*  NIJALk  050511  Bug 123677: Modified preDefine().
*  THWILK  050519  Added the functionality required under Manage Maintenance Jobs(AMEC114).
*  NIJALK  050519  Modified availDetail(), preDefine().
*  THWILK  050521  Added Planned Hours column to Jobs Tab and set the Job Id column to hidden.
*  THWILK  050526  Modified okFindItem8().
*  SHAFLK  050330  Modified issueFromInvent and manIssue().
*  NIJALK  050527  Merged bug 50258.
*  THWILK  050608  Added the functionality required under Work Order Team & Qualifications(AMEC114).
*  THWILK  050610  Modified validate() & javascript methods.
*  THWILK  050610  Modified lovItem8Sign javascript method.
*  DiAmlk  050613  Bug ID:124832 - Renamed the RMB Available to Reserve to Inventory Part in Stock... and
*                  modified the method availtoreserve.
*  THWILK  050615  Modified printContents(),lovItem2OrgCode,lovItem8OrgCode,lovRoleCode,lovItem8RoleCode,lovTeamId,lovItem8TeamId,lovSign,
*                  lovItem8Sign & added lovItem2Contract,lovItem8Contract,lovTeamContract,lovItem8TeamContract javascript methods.
*  SHAFLK  050425  Bug 50830, Used mgr.translateJavaScript() method to get the correctly translated strings in JavaScript functions.
*  NIJALK  050617  Merged bug 50830. 
*  THWILK  050622  Modified run(),additionalQualifications1() and additionalQualifications2(). 
*  THWILK  050630  Added the RMB "Predecessors" under Work Order Dependencies(AMEC114). 
*  THWILK  050705  Modified run() and disconnectOperations(). 
*  THWILK  050707  Modified methods run(),additionalQualifications1(),additionalQualifications2(),predecessors1(),
*                  predecessors2() and connectExistingOperations() and adjust().
*  DiAmlk  050711  Bug ID:125442 - Modified the method validate.
*  SHAFLK  050512  Bug 51147, Modified label of OP_STATUS_ID.
*  THWILK  050718  Merged LCS Bug 51147.
*  THWILK  050719  Modified run().
*  THWILK  050725  Added methods resheduleOperations1(),resheduleOperations2(),resheduleOperations3()
*                  moveOperation1(),moveOperation2() and modified predefine() and run().(WO Dependencies spec)
*  THWILK  050727  Call 125919- Modified predefine().
*  NIJALK  050728  Bug 126023, Modified preDefine(), issue().
*  NIJALK  050805  Bug 126194: Modified saveReturn(). Set value for qrystr.
*  NIJALK  050808  Bug 126177: Modified released(),started(),perform().
*  NIJALK  050811  Bug 126137, Modified availtoreserve().
*  NIJALK  050824  Modified adjust().
*  NIJALK  050826  Bug 126558: Modified preDefine(), adjust() to Set the cost field read only for automatically generated external lines.
*  NIJALK  050902  Bug 126825: Modified field COST_CENTER.
*  THWILK  050923  Added some required functionality under search for and allocate employees spec.
*  SHAFLK  050919  Bug 52880, Modified adjust().
*  NIJALK  051005  Merged bug 52880.
*  THWILK  051013  Added the required functionality under AMEC114:Multiple Allocations.
*  THWILK  051018  Modified Validate(). 
*  THWILK  051021  Modified Predefine().
*  THWILK  051027  Call Id: 128092.Modified Item8PlanMenCheck,Item2PlanMenCheck,saveReturnITEM2,okFindITEM2 and run methods.Added saveReturnITEM8.  
*  THWILK  051109  Modified Predefine().
*  NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile).
*  NIJALK  051220  LCS 54415, Replaced server method Get_Contract with Get_Def_Contract .
*  ERALLK  051214  Bug 54280. Added column 'Quantity Available'.Modified the validate() function.
*  NIJALK  051227  Merged bug 54280.
*  NIJALK  060109  Changed DATE format to compatible with data formats in PG19.
*  THWILK  060126  Corrected localization errors.
*  NIJALK  060210  Call 133546: Renamed "STANDARD_INVENTORY" with ""INVENT_ORDER.
*  NIJALK  060213  Call 132956: Modified projectActivityInfo().
*  NIJALK  060217  Call 134322: Modified okFind().
*  NEKOLK  060223  Call 135228: Mdoified  duplicateRow().
*  NIJALK  060301  Call 135769: Modified mroObjReceiveO().
*  NIJALK  060302  Call 136122: Modified activateOperations(),activateMaterial(),activatePlanning(),activateJobs(),activateBudget().
*  SULILK  060304  Call 134906: Modified manReserve(), GetInventoryQuantity(), manIssue(). 
*  THWILK  060307  Call 136518, Modified predefine().
*  NEKOLK  060308  Call 135828: Added refreshForm() and made changes in predefine().
*  THWILK  060308  Call 136743, Modified Predefine().
*  ASSALK  060316  Material Issue & Reserve modification. Issue and reserve made available after
*                    unissue all materials.
*  SULILK  060317  NOIID: Modified validate(),setValuesInMaterials(),preDefine(),saveReturnItem6(),run().
* ----------------------------------------------------------------------------
*  NIJALK  060509  Bug 57099, Modified matReqUnissue().
*  NIJALK  060510  Bug 57256, Modified manIssue(), GetInventoryQuantity().
*  NIJALK  060515  Bug 56688, Modified issue(), run(), checkObjAvaileble(), adjust().
*  SHAFLK  060516  Bug 57826, Modified okFindItem3() and okFindItem6()
*  SHAFLK  060525  Bug 57598, Modified validation for PART_NO and and setValuesInMaterials().
*  SHAFLK  060703  Bug 59071, Modified validation for STD_JOB_FLAG and function checkITEM7SaveParams() and newRowITEM8().
*  SHAFLK  060704  Bug 59115, Modified validation for STD_JOB_FLAG and function checkITEM7SaveParams().
*  SHAFLK  060721  Bug 59115, Modified validations.
*  AMNILK  060629  Merged with SP1 APP7.
* ----------------------------------------------------------------------------
*  AMDILK  060724  Merged with the Bug ID 59071
*  AMDILK  060725  Merged with the Bug ID 59115
*  ILSOLK  060727  Merged Bug ID 59224.
*  JEWILK  060803  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged Bug Id:58214.
*  ILSOLK  060817  Set the MaxLength of Address1 as 100.
*  ILSOLK  060904  Merged Bug 59699.
*  JEWILK  060816  Bug 58216, Eliminated SQL Injection security vulnerability.
*  NAMELK  060906  Merged Bug 58216.
*  AMDILK  060920  Call Id 139566: Change the base lov in thejavascript function lovWorkMasterSign()
*  NEKOLK  061012  Bug 60835, Modified preDefine() validate() and Java Script part.
*  AMNILK  061102  Merged LCS Bug 60835.
*  SHAFLK  061101  Bug 61515, Modified method adjust().
*  AMNILK  061106  Merged LCS Bug ID: 61515.
*  SHAFLK  061005  Bug 60938, Added new RMB "Activity Info..."
*  NAMELK  061107  Merged LCS Bug 60938. 
*  SHAFLK  070116  Bug 62854, Modified validation for Part No.
*  AMNILK  070208  Merged LCS Bug 62854.   
*  ILSOLK  070216  Modifed for MTPR904 Hink tasks ReqId 45088.setLovPropery() for Object Id.
*  ChAmlk  061016  Bug 61103, Modified okFind() to remove web security issues.
*  ILSOLK  070227  Merged BugId 61103.
*  SHAFLK  061120  Bug 61466, Added 'Supplier Loaned' stock to Materials.
*  ILSOLK  070302  Merged Bug ID 61446.
*  BUNILK  070504  Implemented "MTIS907 New Service Contract - Services" changes.
*  SHAFLK  070228  Bug 63812, Modified printPicList.
*  ILSOLK  070410  Merged Bug Id 63812.
*  AMDILK  070503  Call Id 142273: Inserted a new RMB "Update Spare Parts in Object" to the materials tab
*  ILSOLK  070504  Modified for Call ID 143348.
*  AMNILK  070510  Call Id: 143296. Added new field REMAIN_HRS to planning shedule.
*  CHANLK  070517  Call 143350 connection Type filter for Object id lov.
*  CHANLK  070517  Call 144491 Added Tranfered to mobile check box in preDefine()
*  AMDILK  070518  Call Id 144691: Inserted OBJSTATE to the buffer. Modified requisitions()
*  ASSALK  070523  Call ID 144544: Modified preDefine().
*  CHANLK  070529  Call 144866 remove ITEM_PLAN_LINE_NO duplication from duplicateITEM6()
*  AMDILK  070531  Call Id 145443: Disable the RMB "Update Spare Parts in Object" when spare parts exists 
*  AMNILK  070531  Call Id: 144747. Modified Adjust() method and changed the LOV view for Price_List_No.
*  ASSALK  070601  Call ID: 143627. Modified mroObjReceiveO(). 
*  AMNILK  070604  Call Id:  143216. Modified adjust().
*  AMNILK  070604  Call Id: 144729. Modified Validate() and adjust().
*  CHANLK  070605  Call 143412 change Lov Object Id in find mode.
*  ASSALK  070606  Call 146006. Modified preDefine(), printContents(). 
*  AMDILK  070608  Call Id 144694: Modified okFindITEM5().
*  ILSOLK  070611  Modified cancelled().(Call ID 146119)
*  AMDILK  070614  Call Id 144694: Preserve the line information when navigate the last and first record
*  AMDILK  070622  Call Id 146307: Calculated the planned completion time when the execution time is given
*  ILSOLK  070706  Eliminated XSS.
*  NIJALK  070525  Bug 64744, Modified run(), saveReturn(), preDefine() and printContents(). Added updatePostings().
*  AMDILK  070716  Merged bug 64744.
*  NIJALK  070420  Bug 64572, Modified preDefine().
*  ILSOLK  070718  Merged Bug ID 64572.
*  ILSOLK  070730  Eliminated LocalizationErrors.
*  AMDILK  070821  Modified validate()
*  ASSALK  070831  Call ID: 148009. Change rmb text 'Search Engine' to 'Free Time Search'.
*  ILSOLK  070905  If WOState in ('RELEASED', 'STARTED', 'WORKDONE','PREPARED','UNDERPREPARATION') Enabled Release RMB in Material.
*                  Modified preDefine()(Call ID 148213)
*  ASSALK  070910  CALL 148510, Modified preDefine(), issue().
*  ILSOLK  070911  Changed for Call Id 148507.
*  ILSOLK  070611  Date format not supported by oracle.(Call ID 148501)
*  SHAFLK  071108  Bug 67801, Modified validation for STD_JOB_ID.
*  SHAFLK  071123  Bug 69392, Checked for ORDER installed. 
*  SHAFLK  071211  Bug 69851, Modified reported().
* ----------------------------------------------------------------------------
* -----------------------APP7.5 SP1-------------------------------------
*  071214  ILSOLK  Bug Id 68773, Eliminated XSS.
*  071219  SHAFLK  Bug 69914, Added setStringLables().
*  SHAFLK  071219  Bug 70147, Modified saveReturnItem7(). 
*  CHANLK  080121  Bug 68947, Added RMB Service Contract Line Search. 
*  SHAFLK  080102  Bug 70891, Modified finished() and finished1().
*  NIJALK  080202  Bug 66456, Modified validate(), GetInventoryQuantity(), availDetail() and printContents().
*  CHANLK  080205  Bug 66842,Remove maxLength of PM_NO
*  SHAFLK  080107  Bug 70948, Modified function lovSign.
*  SHAFLK  080130  Bug 70815, Modified run(), issue(), reserve(), preDefine() okFindITEM4() and okFindITEM3(). 
* -----------------------------------------------------------------------
*  AMNILK  080225  Bug Id 70012, Modified coInformation(),PrintContents() and adjust().
*  NIJALK  080306  Bug 72202, Modified okFindITEM6().
*  ARWILK  071130  Bug 66406, Added CONN_PM_NO, CONN_PM_REVISION, CONN_PM_JOB_ID.
*  AMNILK  080317  Bug Id 70921, Added new methods getDatesForSrvcon(),calEndDatesAccPlanHrs().
*                  Modified validate().
*  ARWILK  080502  Bug 70920, Overode lovLineNo and lovContractId.
*  SHAFLK  080506  Bug 73203, Modified preDefine().
*  SHAFLK  080516  Bug 73800, Modified preDefine().
*  NIJALK  080820  Bug 72644, Added RMB "Optimize Planning Schedule...".
*  SHAFLK  080924  Bug 77304, Modified preDefine().
*  SHAFLK  081105  Bug 77824, Modified Validate().
*  SHAFLK  081121  Bug 78187, Modified issue().
*  SHAFLK  090217  Bug 79436  Modified preDefine(), released() and started(). 
*  CHANLK  090225  Bug 76767, Modified preDefine(), validate(), issue(), reserve().
*  SHAFLK  090305  Bug 80959  Modified preDefine() and added reinit(). 
*  SHAFLK  090630  Bug 82543  Modified detchedPartList(). 
*  NIJALK  090704  Bug 82543, Modified run() and printContents().
*  HARPLK  090708  Bug 84436, Modified preDefine().
*  CHANLK  090721  Bug 83532, Added RMB Work Order Address.
*  SHAFLK  090728  Bug 84886, Modified printContents().
*  SHAFLK  090826  Bug 85531, Modified duplicateRow().
*  SHAFLK  090916  Bug 85917, Modified preDefine().
*  SHAFLK  090921  Bug 85901, Modified preDefine().
*  SHAFLK  091001  Bug 86269, Modified preDefine().
*  SHAFLK  100210  Bug 88904, Modified printContents().
*  SHAFLK  100211  Bug 88922  Modified printContents(), getDatesForSrvconValidate(), preDefine() and validate(). 
*  NIJALK  100218  Bug 87766, Modified preDefine() and setCheckBoxValues(). Removed data field CBHASSTRUCTU.
*  SHAFLK  100226  Bug 89213  Modified printContents()
*  SHAFLK  100318  Bug 89298, Modified performItem().
*  SHAFLK  100405  Bug 89883, Modified preDefine().
*  NIJALK  100419  Bug 87935, Modified preDefine().
*  NIJALK  100420  Bug 85045, Modified javascript functions checkMandoItem2() and checkItem9Fields().
*  SHAFLK  100427  Bug 90204, Modified editRowITEM2().
*  VIATLK  100721  Bug 91376, Modified printWO(), printServO()and pickListForWork() .
*  NIJALK  100722  Bug 92043, Modified java script function checkMando().
*  NIJALK  100726  Bug 92043, Modified java script function checkMando().
*  SaFalk  100731  Bug 89703, Added methods deleteRow7(), deleteRowITEM7() and modified methods run(), preDefine() and printContents().
*  SaFalk  100814  Bug 90872, Modified preDefine().
*  ILSOLK  100916  Bug 93061, Modified saveReturn().
*  SHAFLK  100917  Bug 93005, Modified preDefine().
*  SHAFLK  100920  Bug 93011, Modified performItem().
* -----------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

public class ActiveSeparate2ServiceManagement extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeparate2ServiceManagement");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPContext ctx;
   private ASPHTMLFormatter fmt;

   private ASPBlock prntblk;
   private ASPRowSet printset;

   private ASPBlock tempblk;

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

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

   private ASPBlock itemblk;
   private ASPRowSet itemset;
   private ASPCommandBar itembar;
   private ASPTable itemtbl;
   private ASPBlockLayout itemlay;

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
   private ASPBlock blkPost;
   private ASPBlock teblk;
   // 031204  ARWILK  Begin  (Replace blocks with tabs)
   private ASPTabContainer tabs;
   // 031204  ARWILK  End  (Replace blocks with tabs)

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================

   private String mchCode;
   private String sWONo;
   private String objState;
   private ASPTransactionBuffer trans;
   private int curentrow;
   private int currrow;
   private boolean isFind;
   private boolean comBarAct;
   private String qrystr;
   private String rRowN;
   private String rRawId;
   private boolean supWar;
   private String rWarrType;
   private boolean matSingleMode;
   private boolean updateBudg;
   private ASPBuffer buff;
   private ASPBuffer temp;
   private ASPCommand cmd;
   private ASPTransactionBuffer secBuff;
   private ASPBuffer buf;
   private String flg;
   private String finish;
   private ASPQuery q;
   private ASPBuffer data;
   private ASPCommand c;
   private ASPBuffer row;
   private ASPBuffer r;
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
   private String enabl0;
   private String enabl1;
   private String enabl2;
   private String enabl3;
   private String enabl4;
   private String enabl5;
   private String enabl6;
   private String enabl7;
   private String enabl8;
   private String enabl9;
   private String title_;
   private String fldTitleReportedBy;
   private String fldTitleWorkLeaderSign;
   private String fldTitlePreparedBy;
   private String fldTitleWorkMasterSign;
   private String fldTitleSign;
   private String fldTitleObjectId;
   private String lovTitleReportedBy;
   private String lovTitleWorkLeaderSign;
   private String lovTitlePreparedBy;
   private String lovTitleWorkMasterSign;
   private String lovTitleSign;
   private String openCreRepNonSer;
   private String creRepNonSerPath;
   private boolean actEna1;
   private boolean actEna2;
   private boolean actEna3;
   private boolean actEna4;
   private boolean actEna5;
   private boolean actEna6;
   private boolean actEna7;
   private boolean actEna8;
   private boolean actEna9;
   private boolean actEna10;
   private boolean actEna11;
   private boolean actEna12;
   private boolean actEna13;
   private boolean actEna14;
   private boolean actEna15;
   private boolean actEna16;
   private boolean actEna17;
   private boolean actEna18;
   private boolean actEna19;
   private boolean actEna20;
   private boolean actEna21;
   private boolean actEna22;
   private boolean actEna23;
   private boolean actEna24;
   private boolean actEna25;
   private boolean actEna26;
   private boolean actEna27;
   private boolean actEna28;
   private boolean actEna29;
   private boolean actEna30;
   private boolean actEna31;
//  Bug 68947, Start
   private boolean actEna32;
//  Bug 68947, End
   private boolean again;
   private String showMat; 
   // 031204  ARWILK  Begin  (Replace links with RMB's)
   private boolean bOpenNewWindow;
   private String urlString;
   private String newWinHandle;
   // 031204  ARWILK  End  (Replace links with RMB's)
   private String transferurl; 
   private String repair;
   private String unissue; 
   private String creRepWO;
   private String headMchCode;
   private String headMchDesc;
   private ASPBuffer ret_data_buffer;
   private String lout;
   private int enabl10;
   private boolean bException;
   private String headNo;
   private int newWinHeight = 600;
   private int newWinWidth = 900;
   private String clearAlloc0;
   private String clearAlloc1;
   private String clearAlloc2;
   private String rowString;
   private int headRowNo;
   private int currentRow;
   private String planMen="";
   private boolean bPpChanged;
   private String strLableB;
   private String strLableE;
   private String strLableF;
//  Bug 68947, Start
   private boolean bFECust;
//  Bug 68947, End

   //Bug Id 70012,Start
   private boolean bPcmsciExist;
   // Bug Id 70012,End

   // Bug Id 70921,Start
   private boolean bSrvconDateChange;
   // Bug Id 70921,End
   
   //Bug 89703, Start
   private String hasPlanning;
   //Bug 89703, End

   //===============================================================
   // Construction 
   //===============================================================
   public ActiveSeparate2ServiceManagement(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() 
   {
      mchCode = "";
      sWONo =  "";
      objState = "";
      openCreRepNonSer = creRepNonSerPath;
      creRepNonSerPath = "";

      //Bug Id 70921, Start
      bSrvconDateChange = true;
      //Bug Id 70921, End

      //Bug 89703, Start
      hasPlanning = "";
      //Bug 89703, End
      
      ASPManager mgr = getASPManager();

      trans = mgr.newASPTransactionBuffer();
      ctx = mgr.getASPContext();

      fmt = mgr.newASPHTMLFormatter();

      curentrow = ctx.readNumber("CURRENTROW",0);
      isFind = ctx.readFlag("ISFIND",false);
      comBarAct = ctx.readFlag("COMBARACT",false);
      qrystr = ctx.readValue("QRYSTR","");
      sWONo = ctx.readValue("VALWONOPRESM",sWONo); 
      rRowN = ctx.readValue("RROWN","");
      rRawId = ctx.readValue("RRAWID","");
      supWar = ctx.readFlag("SUPWAR",false);
      rWarrType = ctx.readValue("RWARRTYPE","");
      rRowN = ctx.readValue("RROWN","");
      rRawId = ctx.readValue("RRAWID","");
      objState = ctx.readValue("OBJESTATE","");
      matSingleMode = ctx.readFlag("MATSINGLEMODE",false);
      updateBudg = ctx.readFlag("UPDATEBUDG",false);
      actEna1 = ctx.readFlag("ACTENA1",false);
      actEna2 = ctx.readFlag("ACTENA2",false);
      actEna3 = ctx.readFlag("ACTENA3",false);
      actEna4 = ctx.readFlag("ACTENA4",false);
      actEna5 = ctx.readFlag("ACTENA5",false);
      actEna6 = ctx.readFlag("ACTENA6",false);
      actEna7 = ctx.readFlag("ACTENA7",false);
      actEna8 = ctx.readFlag("ACTENA8",false);
      actEna9 = ctx.readFlag("ACTENA9",false);
      actEna10 = ctx.readFlag("ACTENA10",false);
      actEna11 = ctx.readFlag("ACTENA11",false);
      actEna12 = ctx.readFlag("ACTENA12",false);
      actEna13 = ctx.readFlag("ACTENA13",false);
      actEna14 = ctx.readFlag("ACTENA14",false);
      actEna15 = ctx.readFlag("ACTENA15",false);
      actEna16 = ctx.readFlag("ACTENA16",false);
      actEna17 = ctx.readFlag("ACTENA17",false);
      actEna18 = ctx.readFlag("ACTENA18",false);
      actEna19 = ctx.readFlag("ACTENA19",false);
      actEna20 = ctx.readFlag("ACTENA20",false);
      actEna21 = ctx.readFlag("ACTENA21",false);
      actEna22 = ctx.readFlag("ACTENA22",false);
      actEna23 = ctx.readFlag("ACTENA23",false);
      actEna24 = ctx.readFlag("ACTENA24",false);
      actEna25 = ctx.readFlag("ACTENA25",false);
      actEna26 = ctx.readFlag("ACTENA26",false);
      actEna27 = ctx.readFlag("ACTENA27",false);
      actEna28 = ctx.readFlag("ACTENA28",false);
      actEna29 = ctx.readFlag("ACTENA29",false);
      actEna30 = ctx.readFlag("ACTENA30",false);
      actEna31 = ctx.readFlag("ACTENA31",false);
      again = ctx.readFlag("AGAIN",false);
      showMat = ctx.readValue("SHMAT",""); 
      repair =  ctx.readValue("REPAIR","FALSE");
      unissue = ctx.readValue("UNISSUE","FALSE"); 
      creRepWO = ctx.readValue("AUTOREP","FALSE");
      headMchCode = ctx.readValue("HEADMCHCODE","");
      headMchDesc = ctx.readValue("HEADMCHDESC","");
      headRowNo = ctx.readNumber("HEADROWNO",0);
      currentRow = ctx.readNumber("CURRENTROW",0);
      //Bug Id 70012, Start
      bPcmsciExist = ctx.readFlag("PCMSCIEXIST",false);
      //Bug Id 70012, End

      if (mgr.commandBarActivated())
      {
         comBarAct = true; 

         clearItem4();

         eval(mgr.commandBarFunction());

         if ("HEAD.SaveReturn".equals(mgr.readValue("__COMMAND")))
            headset.refreshRow();
         //if ("ITEM6.SaveReturn".equals(mgr.readValue("__COMMAND")))
         //    itemset.refreshRow();
         /*if ("ITEM8.SaveReturn".equals(mgr.readValue("__COMMAND")))
         {
            okFindITEM2();
            if (itemset8.countRows()>0)
              itemset8.refreshAllRows();
         } */
         if ("ITEM2.SaveReturn".equals(mgr.readValue("__COMMAND")))
         {
            headset.refreshRow();
            if (itemset2.countRows()>0)
               itemset2.refreshAllRows();
         }
         if ("ITEM2.Delete".equals(mgr.readValue("__COMMAND")))
            headset.refreshRow();
         if ("ITEM9.Delete".equals(mgr.readValue("__COMMAND")))
            headset.refreshRow();
         if ("ITEM9.SaveReturn".equals(mgr.readValue("__COMMAND")))
         {
            headset.refreshRow();
            itemset9.refreshRow();
         }
         if ("ITEM6.SaveReturn".equals(mgr.readValue("__COMMAND")))
         {
            headset.refreshRow();
            itemset.refreshRow();
            setValuesInMaterials();
         }

      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      //Bug 89703, Start
      else if ("AAAA".equals(mgr.readValue("HASPLANNING")))
         deleteRow7("YES");
      else if ("BBBB".equals(mgr.readValue("HASPLANNING")))
         deleteRow7("NO");
      //Bug 89703, End
      else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("FAULT_REP_FLAG")))
         okFind();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("DOCANCEL")))
      {
         okFind();
         doCancel();
         if (headset.countRows() != 1)
         {
            String s_sel_wo = ctx.getGlobal("WONOGLOBAL");
            int sel_wo = Integer.parseInt(s_sel_wo);
            headset.goTo(sel_wo);      

            okFindITEM2();
            okFindITEM3();
            okFindITEM5();
            okFindITEM7();
         }
      }
      else if ("TRUE".equals(mgr.readValue("REFRESHPARENT1")))
         predecessors1();
      else if ("TRUE".equals(mgr.readValue("REFRESHPARENT2")))
         predecessors2();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("WOQUALIFICATION")))
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();   

         if (headset.countRows() != 1)
         {
            headNo = ctx.getGlobal("HEADGLOBAL");
            int headSetNo = Integer.parseInt(headNo);
            headset.goTo(headSetNo);   
            okFindITEM2();
            okFindITEM3();
            okFindITEM4();
            okFindITEM5();
            okFindITEM6();
            okFindITEM7();
         }
      }

      else if (!mgr.isEmpty(mgr.getQueryStringValue("PREDECESSORS")))
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();   
         if (headset.countRows() != 1)
         {
            headNo = ctx.getGlobal("HEADGLOBAL");
            int headSetNo = Integer.parseInt(headNo);
            headset.goTo(headSetNo);   
            okFindITEM2();
            okFindITEM3();
            okFindITEM4();
            okFindITEM5();
            okFindITEM6();
            okFindITEM7();
         }
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("EMPLOYEE")))
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();   

         if (headset.countRows() != 1)
         {
            headNo = ctx.getGlobal("HEADGLOBAL");
            int headSetNo = Integer.parseInt(headNo);
            headset.goTo(headSetNo);   
            okFindITEM2();
            okFindITEM3();
            okFindITEM4();
            okFindITEM5();
            okFindITEM6();
            okFindITEM7();
         }
      }



      else if (!mgr.isEmpty(mgr.getQueryStringValue("OKSPAREPART")))
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();
         this.activateMaterial();

         if (headset.countRows() != 1)
         {
            String s_sel_wo = ctx.getGlobal("WONOGLOBAL");
            int sel_wo = Integer.parseInt(s_sel_wo);
            headset.goTo(sel_wo);      

            okFindITEM2();
            okFindITEM3();
            okFindITEM4();
            okFindITEM5();
            okFindITEM7();
         }

         matSingleMode = true;
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("OKMANISSUE")) && !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();
         this.activateMaterial();

         if (headset.countRows() != 1)
         {
            String s_sel_wo = ctx.getGlobal("WONOGLOBAL");
            int sel_wo = Integer.parseInt(s_sel_wo);
            headset.goTo(sel_wo);     

            okFindITEM2();
            okFindITEM3();
            okFindITEM4();
            okFindITEM5();
            okFindITEM7();
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

            okFindITEM2();
            okFindITEM3();
            okFindITEM4();
            okFindITEM5();
            okFindITEM7();
         }

         matSingleMode = true;
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("OKMANUNRESERVE")) && !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();   

         if (headset.countRows() != 1)
         {
            String s_sel_wo = ctx.getGlobal("WONOGLOBAL");
            int sel_wo = Integer.parseInt(s_sel_wo);
            headset.goTo(sel_wo);         

            okFindITEM2();
            okFindITEM3();
            okFindITEM4();
            okFindITEM5();
            okFindITEM7();
         }

         matSingleMode = true;
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("OKUNISSUE")) && !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();   

         if (headset.countRows() != 1)
         {
            String s_sel_wo = ctx.getGlobal("WONOGLOBAL");
            int sel_wo = Integer.parseInt(s_sel_wo);
            headset.goTo(sel_wo);         

            okFindITEM2();
            okFindITEM3();
            okFindITEM4();
            okFindITEM5();
            okFindITEM7();
         }
         matSingleMode = true;
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("NOTETEXTENT")))
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();   

         if (headset.countRows() != 1)
         {
            okFindITEM2();
            okFindITEM3();
            okFindITEM4();
            okFindITEM5();
            okFindITEM7();
         }

         if (itemset3.countRows() > 0)
         {
            buff = itemset3.getRow();
            buff.setValue("SNOTETEXT", mgr.readValue("NOTETEXTENT",""));
            itemset3.setRow(buff);
         }
      }

      else if ("CCCC".equals(mgr.readValue("BUTTONVAL")))
      {
         unissue="FALSE";
         //clearItem4();
         reported1();
      }

      else if ("REPORT".equals(mgr.readValue("STATEVAL")))
      {
         unissue="FALSE";
         //clearItem4();
         reported();
      }

      else if ("AAAA".equals(mgr.readValue("BUTTONVAL")))
      {
         repair="FALSE";
         unissue="FALSE";
         //clearItem4();
         finished1();
      }
      else if ("BBBB".equals(mgr.readValue("BUTTONVAL")))
      {
         repair="FALSE";
         unissue="FALSE";
         //clearItem4();
         finished1();
      }
      else if ("FINIS".equals(mgr.readValue("STATEVAL")))
      {
         repair="FALSE";
         unissue="FALSE";
         //clearItem4();
         finished();
      }
      else if ("CANCE".equals(mgr.readValue("STATEVAL")))
      {
         //clearItem4();  
         cancelled();  
      }
      else if ("DDDD".equals(mgr.readValue("BUTTONVAL")))
      {
         clearAlloc0="FALSE";
         clearAllocation0();
      }
      else if ("EEEE".equals(mgr.readValue("BUTTONVAL")))
      {
         clearAlloc1="FALSE";
         clearAllocation1();
      }
      else if ("FFFF".equals(mgr.readValue("BUTTONVAL")))
      {
         clearAlloc2="FALSE";
         clearAllocation2();
      }

      else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("RWARRTYPEENT")))
      {
         okFind();

         headlay.setLayoutMode(headlay.EDIT_LAYOUT);

         rWarrType = mgr.readValue("RWARRTYPEENT", "");
         rRowN = mgr.readValue("RROWNENT", "");
         rRawId = mgr.readValue("RWARRIDENT", "");  

         temp = headset.getRow();

         trans.clear(); 

         cmd  = trans.addCustomFunction("SUPPLIER","OBJECT_SUPPLIER_WARRANTY_API.Get_Vendor_No","SUPPLIER");
         cmd.addParameter("CONTRACT", headset.getRow().getValue("MCH_CODE_CONTRACT"));
         cmd.addParameter("MCH_CODE", headset.getRow().getValue("MCH_CODE"));
         cmd.addParameter("OBJ_SUP_WARRANTY", rRowN);

         cmd  = trans.addCustomFunction("SUPPDESC","Maintenance_Supplier_API.Get_Description","SUPP_DESCR");
         cmd.addReference("SUPPLIER","SUPPLIER/DATA");

         trans = mgr.validate(trans);

         temp = headset.getRow();

         temp.setValue("OBJ_SUP_WARRANTY",rRowN);
         temp.setValue("SUP_WARRANTY",rRawId);
         temp.setValue("SUP_WARR_TYPE",rWarrType);
         temp.setValue("SUP_WARR_DESC", mgr.URLDecode(mgr.readValue("RWARRDESC","")));
         temp.setValue("SUPPLIER",trans.getValue("SUPPLIER/DATA/SUPPLIER"));
         temp.setValue("SUPP_DESCR",trans.getValue("SUPPDESC/DATA/SUPP_DESCR"));

         headset.setRow(temp);

         mgr.getASPField("SUP_WARR_TYPE").unsetReadOnly();

         supWar = true;
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
      {
         showMat = mgr.readValue("SHOWMAT","");

         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();
         if ("TRUE".equals(showMat))
            this.activateMaterial();
      }
      else if (mgr.dataTransfered())
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         okFind();
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else if ("TRUE".equals(mgr.readValue("PRPOSTCHANGED")))
         updatePostings();
//  Bug 68947, Start
      else if (!mgr.isEmpty(mgr.readValue("TEMPCONTRACTID")))
         updateContract();
//  Bug 68947, End
      //Bug 82543, Start
      else if ( !mgr.isEmpty(mgr.readValue("REFRESH_FLAG")))
      {
	  refresh();

	  if (tabs.getActiveTab() == 2)
	      refreshForm();
      }
      //Bug 82543, End

      checkObjAvaileble();
      adjust(); 

      ctx.writeNumber("CURRENTROW", curentrow);
      ctx.writeValue("QRYSTR", qrystr);
      ctx.writeValue("VALWONOPRESM", sWONo); 
      ctx.writeFlag("SUPWAR", supWar);
      ctx.writeValue("RWARRTYPE", rWarrType);
      ctx.writeValue("RROWN", rRowN);
      ctx.writeValue("RRAWID", rRawId);
      ctx.writeFlag("MATSINGLEMODE", matSingleMode);
      ctx.writeFlag("UPDATEBUDG", updateBudg);
      ctx.writeFlag("ACTENA1", actEna1);
      ctx.writeFlag("ACTENA2", actEna2);
      ctx.writeFlag("ACTENA3", actEna3);
      ctx.writeFlag("ACTENA4", actEna4);
      ctx.writeFlag("ACTENA5", actEna5);
      ctx.writeFlag("ACTENA6", actEna6);
      ctx.writeFlag("ACTENA7", actEna7);
      ctx.writeFlag("ACTENA8", actEna8);
      ctx.writeFlag("ACTENA9", actEna9);
      ctx.writeFlag("ACTENA10", actEna10);
      ctx.writeFlag("ACTENA11", actEna11);
      ctx.writeFlag("ACTENA12", actEna12);
      ctx.writeFlag("ACTENA13", actEna13);
      ctx.writeFlag("ACTENA14", actEna14);
      ctx.writeFlag("ACTENA15", actEna15);
      ctx.writeFlag("ACTENA16", actEna16);
      ctx.writeFlag("ACTENA17", actEna17);
      ctx.writeFlag("ACTENA18", actEna18);
      ctx.writeFlag("ACTENA19", actEna19);
      ctx.writeFlag("ACTENA20", actEna20);
      ctx.writeFlag("ACTENA21", actEna21);
      ctx.writeFlag("ACTENA22", actEna22);
      ctx.writeFlag("ACTENA23", actEna23);
      ctx.writeFlag("ACTENA24", actEna24);
      ctx.writeFlag("ACTENA25", actEna25);
      ctx.writeFlag("ACTENA26", actEna26);
      ctx.writeFlag("ACTENA27", actEna27);
      ctx.writeFlag("ACTENA28", actEna28);
      ctx.writeFlag("ACTENA29", actEna29);
      ctx.writeFlag("ACTENA30", actEna30);
      ctx.writeFlag("ACTENA31", actEna31);
      ctx.writeFlag("AGAIN", again);
      ctx.writeNumber("HEADROWNO",headRowNo);
      ctx.writeNumber("CURRENTROW",currentRow);
      //Bug Id 70012, Start
      ctx.writeFlag("PCMSCIEXIST", bPcmsciExist);
      //Bug Id 70012, End

      // 031204  ARWILK  Begin  (Replace blocks with tabs)
      tabs.saveActiveTab();
      // 031204  ARWILK  End  (Replace blocks with tabs)
   }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
// This function is used to check security status for methods which
// are in modules Order,Purch and Invent .This function is called before
// the required method is performed.  -NUPE,21-07-2000

   public boolean checksec( String method,int ref) 
   {
      ASPManager mgr = getASPManager();
      String isSecure[] = new String[6] ;

      isSecure[ref] = "false" ; 
      String splitted[] = split(method, "."); 

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


      String val = mgr.readValue("VALIDATE");
      String txt = "";
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
      String teamDesc;

      cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
      cmd.addParameter("PART_OWNERSHIP",mgr.readValue("PART_OWNERSHIP"));
      trans = mgr.validate(trans);
      String sClientOwnershipDefault = trans.getValue("VALIDOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");
      trans.clear();

      String sClientOwnershipConsignment = "CONSIGNMENT"  ;

      if ("AUTHORIZE_CODE".equals(val))
      {
         cmd = trans.addCustomFunction("AUTHONAME","PERSON_INFO_API.Get_Name","AUTHORIZECODENAME");
         cmd.addParameter("AUTHORIZE_CODE");

         trans = mgr.validate(trans);

         String authoName = trans.getValue("AUTHONAME/DATA/AUTHORIZECODENAME");

         txt = (mgr.isEmpty(authoName) ? "": (authoName))+ "^";
         mgr.responseWrite(txt);
      }

      else if ("CONTRACT".equals(val))
      {
         cmd = trans.addCustomFunction("COMP", "SITE_API.Get_Company", "COMPANY");
         cmd.addParameter("CONTRACT");

         trans = mgr.validate(trans);

         String compa = trans.getValue("COMP/DATA/COMPANY");

         txt = (mgr.isEmpty(compa) ? "":compa)+ "^";
         mgr.responseWrite(txt);
      }
      else if ("MCH_CODE".equals(val))
      {
         String description = "";
         String mchContract = mgr.readValue("MCH_CODE_CONTRACT");
         String mchCode = mgr.readValue("MCH_CODE");

         if (mgr.readValue("MCH_CODE").indexOf("~") > -1)
         {
            String strAttr = mgr.readValue("MCH_CODE");

            mchCode = strAttr.substring(0, strAttr.indexOf("~"));       
            String validationAttrAtr = strAttr.substring(strAttr.indexOf("~") + 1, strAttr.length());
            mchContract =  validationAttrAtr.substring(validationAttrAtr.indexOf("~") + 1, validationAttrAtr.length());                

            cmd = trans.addCustomFunction("OBJECTESC","Maintenance_Object_API.Get_Mch_Name","MCH_CODE_DESCRIPTION");
            cmd.addParameter("MCH_CODE_CONTRACT", mchContract);
            cmd.addParameter("MCH_CODE", mchCode);
         }
         else
         {
            if (mgr.isEmpty(mchContract))
            {
               cmd = trans.addCustomFunction("MCHCONTR","EQUIPMENT_OBJECT_API.Get_Def_Contract","MCH_CODE_CONTRACT");
               cmd.addParameter("MCH_CODE");
            }

            cmd = trans.addCustomFunction("OBJECTESC","Maintenance_Object_API.Get_Mch_Name","MCH_CODE_DESCRIPTION");
            if (mgr.isEmpty(mchContract))
               cmd.addReference("MCH_CODE_CONTRACT","MCHCONTR/DATA");
            else
               cmd.addParameter("MCH_CODE_CONTRACT",mchContract);
            cmd.addParameter("MCH_CODE");
         }

         cmd = trans.addCustomFunction("GETCRIICAL","EQUIPMENT_FUNCTIONAL_API.Get_Criticality","CRITICALITY");
         if (mgr.isEmpty(mchContract))
            cmd.addReference("MCH_CODE_CONTRACT","MCHCONTR/DATA");
         else
            cmd.addParameter("MCH_CODE_CONTRACT",mchContract);
         cmd.addParameter("MCH_CODE",mchCode);

         trans = mgr.validate(trans);

         description = trans.getValue("OBJECTESC/DATA/MCH_CODE_DESCRIPTION");
         String criticality = trans.getValue("GETCRIICAL/DATA/CRITICALITY");

         if (mgr.readValue("MCH_CODE").indexOf("~") == -1 && mgr.isEmpty(mchContract))
            mchContract = trans.getValue("MCHCONTR/DATA/MCH_CODE_CONTRACT");

         txt = (mgr.isEmpty(description) ? "" : (description))+ "^" + (mgr.isEmpty(criticality) ? "" : (criticality))+ "^" + (mgr.isEmpty(mchContract) ? "" : (mchContract))+ "^" + (mgr.isEmpty(mchCode) ? "" : (mchCode))+ "^";
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
      else if ("REPORTED_BY".equals(val))
      {
         cmd = trans.addCustomFunction("REPBYID","Company_Emp_API.Get_Max_Employee_Id","REPORTED_BY_ID");
         cmd.addParameter("COMPANY");
         cmd.addParameter("REPORTED_BY");

         cmd = trans.addCustomFunction("REPNAME","PERSON_INFO_API.Get_Name","NAME");
         cmd.addParameter("REPORTED_BY");

         trans = mgr.validate(trans);

         String reportById = trans.getValue("REPBYID/DATA/REPORTED_BY_ID");
         String name = trans.getValue("REPNAME/DATA/NAME");

         txt = (mgr.isEmpty(reportById) ? "" : (reportById))+ "^" + (mgr.isEmpty(name) ? "" : (name))+ "^";
         mgr.responseWrite(txt);
      }
      else if ("WORK_LEADER_SIGN".equals(val))
      {
         cmd = trans.addCustomFunction("WOLDRSIGNID","Company_Emp_API.Get_Max_Employee_Id","WORK_LEADER_SIGN_ID");
         cmd.addParameter("COMPANY");
         cmd.addParameter("WORK_LEADER_SIGN");

         cmd = trans.addCustomFunction("WORKLEADNAME","PERSON_INFO_API.Get_Name","WORKLEADERNAME");
         cmd.addParameter("WORK_LEADER_SIGN");

         trans = mgr.validate(trans);

         String woSignId = trans.getValue("WOLDRSIGNID/DATA/WORK_LEADER_SIGN_ID");
         String workLeaderName = trans.getValue("WORKLEADNAME/DATA/WORKLEADERNAME");

         txt = (mgr.isEmpty(woSignId) ? "": (woSignId))+ "^" + (mgr.isEmpty(workLeaderName) ? "": (workLeaderName))+ "^";
         mgr.responseWrite(txt);
      }
      else if ("WORK_MASTER_SIGN".equals(val))
      {
         cmd = trans.addCustomFunction("WOMASSIGNID","Company_Emp_API.Get_Max_Employee_Id","WORK_MASTER_SIGN_ID");
         cmd.addParameter("COMPANY");
         cmd.addParameter("WORK_MASTER_SIGN");

         cmd = trans.addCustomFunction("WOMASSNAME","PERSON_INFO_API.Get_Name","WORKMASTERBYNAME");
         cmd.addParameter("WORK_MASTER_SIGN");

         trans = mgr.validate(trans);

         String woMasterId = trans.getValue("WOMASSIGNID/DATA/WORK_MASTER_SIGN_ID");
         String woMasterName = trans.getValue("WOMASSNAME/DATA/WORKMASTERBYNAME");

         txt = (mgr.isEmpty(woMasterId) ? "": (woMasterId))+ "^" + (mgr.isEmpty(woMasterName) ? "": (woMasterName))+ "^";
         mgr.responseWrite(txt);
      }
      else if ("CUSTOMER_NO".equals(val))
      {
         secBuff = mgr.newASPTransactionBuffer();
         secBuff.addSecurityQuery("Cust_Ord_Customer");

         secBuff = mgr.perform(secBuff);

         if (secBuff.getSecurityInfo().itemExists("Cust_Ord_Customer"))
         {
            cmd = trans.addCustomFunction("ISCRESTOP", "Cust_Ord_Customer_API.Customer_Is_Credit_Stopped", "CREDITSTOP");
            cmd.addParameter("CUSTOMER_NO");
            cmd.addParameter("COMPANY");
         }

         cmd = trans.addCustomFunction("CUSTDESC", "CUSTOMER_INFO_API.Get_Name", "CUSTOMERDESCRIPTION");
         cmd.addParameter("CUSTOMER_NO");

         trans = mgr.validate(trans);
         String sUpdatePrice = "0";

         if (secBuff.getSecurityInfo().itemExists("Cust_Ord_Customer"))
            txt = (trans.getValue("ISCRESTOP/DATA/CREDITSTOP")) +"^"+ (mgr.isEmpty(trans.getValue("CUSTDESC/DATA/CUSTOMERDESCRIPTION"))? "":trans.getValue("CUSTDESC/DATA/CUSTOMERDESCRIPTION")) +"^" +(mgr.isEmpty(sUpdatePrice) ? "": (sUpdatePrice))+ "^";
         else
            txt   = "2" +"^"+ (mgr.isEmpty(trans.getValue("CUSTDESC/DATA/CUSTOMERDESCRIPTION"))? "":trans.getValue("CUSTDESC/DATA/CUSTOMERDESCRIPTION")) +"^" +(mgr.isEmpty(sUpdatePrice) ? "": (sUpdatePrice))+ "^";

         mgr.responseWrite(txt);
      }
      else if ("PREPARED_BY".equals(val))
      {
         cmd = trans.addCustomFunction("PREPID", "Company_Emp_API.Get_Max_Employee_Id", "PREPARED_BY_ID");
         cmd.addParameter("COMPANY");
         cmd.addParameter("PREPARED_BY");

         cmd = trans.addCustomFunction("PREPBYNAME", "PERSON_INFO_API.Get_Name", "PREPAREDBYNAME");
         cmd.addParameter("PREPARED_BY");

         trans = mgr.validate(trans);

         String prepId = trans.getValue("PREPID/DATA/PREPARED_BY_ID");
         String prepName = trans.getValue("PREPBYNAME/DATA/PREPAREDBYNAME");

         txt = (mgr.isEmpty(prepId) ? "": (prepId))+ "^" + (mgr.isEmpty(prepName) ? "": (prepName))+ "^";
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

      else if ("ITEM2_ORG_CODE".equals(val))
      {
         String colRoleCode = mgr.readValue("ROLE_CODE","");
         String colOrgContract = "";
         String colOrgCode = "";
         String colCatalogContract = mgr.readValue("CATALOG_CONTRACT","");
         String colCatalogNo  = mgr.readValue("CATALOG_NO","");

         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[6];

         String new_org_code = mgr.readValue("ITEM2_ORG_CODE","");

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
               colOrgCode = ar[3];
               colOrgContract = ar[4];
            }
            else
            {
               if (!mgr.isEmpty(mgr.readValue("SIGN")))
               {
                  for (i=0 ; i<4; i++)
                  {
                     endpos = new_org_code.indexOf("^",startpos);
                     reqstr = new_org_code.substring(startpos,endpos);
                     ar[i] = reqstr;
                     startpos= endpos+1;
                  }
                  colOrgCode = ar[2];
                  colOrgContract = ar[3];
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
                  colOrgCode = ar[0];
                  colOrgContract = ar[1];
               }
            }
         }
         else
         {
            colOrgCode = mgr.readValue("ITEM2_ORG_CODE");
            colOrgContract = mgr.readValue("ITEM2_CONTRACT"); 
         }

         double planMen = mgr.readNumberValue ("PLAN_MEN");
         if (isNaN(planMen)) planMen = 0;

         double planHrs = mgr.readNumberValue ("ITEM2_PLAN_HRS");
         if (isNaN(planHrs)) planHrs = 0;

         double nCost = getCost(colOrgContract,colOrgCode,colRoleCode,colCatalogContract,colCatalogNo,mgr.readValue("ITEM2_WO_NO"));
         double nCostAmount = countCostAmount(nCost,planMen,planHrs);

         String nCostStr = mgr.getASPField("COST").formatNumber(nCost);
         String colAmountCostStr = mgr.getASPField("COSTAMOUNT").formatNumber(nCostAmount);

         //bug id 59224,start
         trans.clear();  
         cmd = trans.addCustomFunction("GETITEM2ORGDESC","Organization_Api.Get_Description","ITEM2_ORG_CODE_DESC");
         cmd.addParameter("ITEM2_CONTRACT",colOrgContract);
         cmd.addParameter("ITEM2_ORG_CODE",colOrgCode);

         trans = mgr.validate(trans);

         String sitem2OrgDesc = trans.getValue("GETITEM2ORGDESC/DATA/ITEM2_ORG_CODE_DESC");
         trans.clear();
         //bug id 59224,end

         if (mgr.isEmpty(colRoleCode) || (!mgr.isEmpty(colRoleCode) && mgr.isEmpty(colCatalogNo)))
         {
            String cataNo = "";
            String cataContract = "";
            String cataDesc = "";

            trans.clear();

            secBuff = mgr.newASPTransactionBuffer();
            secBuff.addSecurityQuery("SALES_PART");

            secBuff = mgr.perform(secBuff);

            if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
            {
               cmd = trans.addCustomFunction("CATANO", "Organization_Sales_Part_API.Get_Def_Catalog_No", "CATALOG_NO");
               cmd.addParameter("ITEM2_CONTRACT", colOrgContract);
               cmd.addParameter("ITEM2_ORG_CODE", colOrgCode);

               cmd = trans.addCustomFunction("DEFCONT", "Organization_Sales_Part_API.Get_Def_Contract", "CATALOG_CONTRACT");
               cmd.addParameter("ITEM2_CONTRACT", colOrgContract);
               cmd.addParameter("ITEM2_ORG_CODE", colOrgCode);

               cmd = trans.addCustomFunction("CATADESC", "Sales_Part_API.Get_Catalog_Desc", "CATALOGDESC");
               cmd.addReference("CATALOG_CONTRACT", "DEFCONT/DATA");
               cmd.addReference("CATALOG_NO", "CATANO/DATA");

               trans = mgr.validate(trans);

               cataNo = trans.getValue("CATANO/DATA/CATALOG_NO");
               cataContract = trans.getValue("DEFCONT/DATA/CATALOG_CONTRACT");
               cataDesc = trans.getValue("CATADESC/DATA/CATALOGDESC");
            }
            txt = (mgr.isEmpty(cataNo) ? "": (cataNo))+ "^" + 
                  (mgr.isEmpty(cataDesc) ? "": (cataDesc))+ "^" + 
                  (mgr.isEmpty(nCostStr) ? "": (nCostStr))+ "^" + 
                  (mgr.isEmpty(colAmountCostStr) ? "": (colAmountCostStr))+ "^"+
                  (mgr.isEmpty(colOrgCode) ? "": (colOrgCode))+ "^" + 
                  (mgr.isEmpty(colOrgContract) ? "": (colOrgContract))+ "^"+
                  (mgr.isEmpty(sitem2OrgDesc) ? "": (sitem2OrgDesc))+ "^";
         }
         else
            txt   = mgr.readValue("CATALOG_NO","")+ "^" + 
                    mgr.readValue("CATALOGDESC","")+ "^" + 
                    (mgr.isEmpty(nCostStr) ? "": (nCostStr)) + "^" + 
                    (mgr.isEmpty(colAmountCostStr) ? "": (colAmountCostStr)) + "^"+
                    (mgr.isEmpty(colOrgCode) ? "": (colOrgCode))+ "^" + 
                    (mgr.isEmpty(colOrgContract) ? "": (colOrgContract))+ "^"+
                    (mgr.isEmpty(sitem2OrgDesc) ? "": (sitem2OrgDesc))+ "^";

         mgr.responseWrite(txt);   
      }
      else if ("ROLE_CODE".equals(val))
      {
         String colRoleCode = "";
         String colOrgContract = "";
         String colOrgCode = mgr.readValue("ITEM2_ORG_CODE","");
         String colCatalogContract = mgr.readValue("CATALOG_CONTRACT","");
         String colCatalogNo  = mgr.readValue("CATALOG_NO","");
         double planMen = mgr.readNumberValue ("PLAN_MEN");
         if (isNaN(planMen)) planMen = 0;

         double planHrs = mgr.readNumberValue ("ITEM2_PLAN_HRS");
         if (isNaN(planHrs)) planHrs = 0;

         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[6];

         String new_role_code = mgr.readValue("ROLE_CODE","");

         if (new_role_code.indexOf("^",0)>0)
         {
            if (!mgr.isEmpty(mgr.readValue("SIGN")))
            {
               for (i=0 ; i<5; i++)
               {
                  endpos = new_role_code.indexOf("^",startpos);
                  reqstr = new_role_code.substring(startpos,endpos);
                  ar[i] = reqstr;
                  startpos= endpos+1;
               }
               colRoleCode = ar[2];
               colOrgContract = ar[4];
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
               colRoleCode = ar[0];
               colOrgContract = ar[1];

            }
         }
         else
         {
            colRoleCode = mgr.readValue("ROLE_CODE");
            colOrgContract = mgr.readValue("ITEM2_CONTRACT"); 
         }

         trans.clear();

         double nCost = getCost(colOrgContract,colOrgCode,colRoleCode,colCatalogContract,colCatalogNo,mgr.readValue("ITEM2_WO_NO"));
         double nCostAmount = countCostAmount(nCost,planMen,planHrs);

         String nCostStr = mgr.getASPField("COST").formatNumber(nCost);
         String colAmountCostStr = mgr.getASPField("COSTAMOUNT").formatNumber(nCostAmount);

         trans.clear();

         cmd = trans.addCustomFunction("ROLECATANO", "Role_Sales_Part_API.Get_Def_Catalog_No", "CATALOG_NO");
         cmd.addParameter("ROLE_CODE");  
         cmd.addParameter("ITEM2_CONTRACT");

         //Bug 69392, start
         if (mgr.isModuleInstalled("ORDER"))
         {
            cmd = trans.addCustomFunction("DEFACODESC", "Sales_Part_API.Get_Catalog_Desc", "CATALOGDESC");
            cmd.addParameter("ITEM2_CONTRACT");
            cmd.addReference("CATALOG_NO", "ROLECATANO/DATA");      
         }
         //Bug 69392, end

         //bug id 59224,start
         cmd = trans.addCustomFunction("ITEM2ROLEDESC","ROLE_API.Get_Description","ROLE_CODE_DESC");
         cmd.addParameter("ROLE_CODE",colRoleCode);

         trans = mgr.validate(trans);

         String sitem2roleDesc = trans.getValue("ITEM2ROLEDESC/DATA/ROLE_CODE_DESC");
         //bug id 59224,end


         String catalogNo = trans.getValue("ROLECATANO/DATA/CATALOG_NO");
         String catalogContract = mgr.readValue("ITEM2_CONTRACT","");

         //Bug 69392, start
         String catalogDescp = "";
         if (mgr.isModuleInstalled("ORDER"))
            catalogDescp = trans.getValue("DEFACODESC/DATA/CATALOGDESC");
         //Bug 69392, end

         trans.clear();
         cmd = trans.addCustomFunction("EXIST","User_Allowed_Site_API.Is_Authorized","USERALLOWED");
         cmd.addParameter("CATALOG_CONTRACT",catalogContract);

         if (mgr.isEmpty(catalogNo))
         {
            cmd = trans.addCustomFunction("ROLECATANO", "Organization_Sales_Part_API.Get_Def_Catalog_No ", "CATALOG_NO");
            cmd.addParameter("ITEM2_CONTRACT");  
            cmd.addParameter("ITEM2_ORG_CODE",colOrgCode);

            //Bug 69392, start
            if (mgr.isModuleInstalled("ORDER"))
            {
               cmd = trans.addCustomFunction("DEFACODESC", "Sales_Part_API.Get_Catalog_Desc", "CATALOGDESC");
               cmd.addParameter("ITEM2_CONTRACT");
               cmd.addReference("CATALOG_NO", "ROLECATANO/DATA");      
            }
            //Bug 69392, end
         }

         trans=mgr.validate(trans); 
         String exist=trans.getValue("EXIST/DATA/USERALLOWED");

         if (mgr.isEmpty(catalogNo))
         {
            catalogNo = trans.getValue("ROLECATANO/DATA/CATALOG_NO");
            //Bug 69392, start
            if (mgr.isModuleInstalled("ORDER"))
               catalogDescp = trans.getValue("DEFACODESC/DATA/CATALOGDESC");
            //Bug 69392, end
         }

         if ("0".equals(exist))
         {
            catalogNo = "";
            catalogContract = "";
            catalogDescp = ""; 
         }

         txt = (mgr.isEmpty(catalogNo) ? "": (catalogNo)) + "^" + 
               (mgr.isEmpty(nCostStr) ? "": (nCostStr))+ "^" + 
               (mgr.isEmpty(colAmountCostStr) ? "": (colAmountCostStr))+ "^" + 
               (mgr.isEmpty(catalogDescp) ? "": (catalogDescp))+ "^"+
               (mgr.isEmpty(colRoleCode) ? "": (colRoleCode))+ "^" + 
               (mgr.isEmpty(colOrgContract) ? "": (colOrgContract))+ "^"+
               (mgr.isEmpty(sitem2roleDesc) ? "": (sitem2roleDesc))+ "^";

         mgr.responseWrite(txt);   
      }
      else if ("SIGN_ID".equals(val))
      {
         String signId = mgr.readValue("SIGN_ID");
         String sSign = "";      
         String sOrgContract = mgr.readValue("ITEM2_CONTRACT");
         String sOrgCode = mgr.readValue("ITEM2_ORG_CODE");
         String sDefRole = "";
         String sCatalogNo0 = mgr.readValue("CATALOG_NO","");     
         String sCatalogNo = mgr.readValue("CATALOG_NO","");     
         String sCatalogContract = mgr.readValue("CATALOG_CONTRACT");
         String sCatalogDesc = mgr.readValue("CATALOGDESC");
         String sCatalogNo1 = "";     
         String sCatalogContract1 = "";
         String sCatalogDesc1 = "";
         String sCatalogNo2 = "";     
         String sCatalogContract2 = "";
         String sCatalogDesc2 = "";
         String sPriceListNo = mgr.readValue("PRICE_LIST_NO");
         String sDiscount = mgr.readValue("DISCOUNT");
         String sListPrice = mgr.readValue("LISTPRICE");
         String sSalesPriceAmount = mgr.readValue("SALESPRICEAMOUNT");
         String sDiscountedPrice = mgr.readValue("DISCOUNTED_PRICE");  

         cmd = trans.addCustomFunction("EMPEXIST", "Employee_API.Is_Emp_Exist", "ITEM2_TEMP");
         cmd.addParameter("ITEM2_COMPANY");
         cmd.addParameter("SIGN_ID");  

         trans.addSecurityQuery("SALES_PART");
         trans.addSecurityQuery("ORGANIZATION_SALES_PART");
         trans.addSecurityQuery("ROLE_SALES_PART");
         trans.addSecurityQuery("Customer_Order_Pricing_API", "Get_Valid_Price_List");

         trans = mgr.perform(trans);

         boolean bEmpExist = !("0".equals(trans.getValue("EMPEXIST/DATA/ITEM2_TEMP")));

         boolean bSalesPartOk = 
         trans.getSecurityInfo().itemExists("SALES_PART") &&
         trans.getSecurityInfo().itemExists("ORGANIZATION_SALES_PART") &&
         trans.getSecurityInfo().itemExists("ROLE_SALES_PART");

         boolean bPricingOk = trans.getSecurityInfo().itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List");

         if (bEmpExist)
         {
            trans.clear();

            cmd = trans.addCustomFunction("GETSIGN", "Company_Emp_API.Get_Person_Id", "SIGN");
            cmd.addParameter("ITEM2_COMPANY");
            cmd.addParameter("SIGN_ID");  

            cmd = trans.addCustomFunction("GETDEFROLE", "Employee_Role_API.Get_Default_Role", "ROLE_CODE");
            cmd.addParameter("ITEM2_COMPANY");
            cmd.addParameter("SIGN_ID");  

            cmd = trans.addCustomFunction("GETORGCODE", "Employee_API.Get_Organization", "ITEM2_ORG_CODE");
            cmd.addParameter("ITEM2_COMPANY");
            cmd.addParameter("SIGN_ID");  

            trans = mgr.validate(trans);

            sSign = trans.getValue("GETSIGN/DATA/SIGN");
            sOrgCode = trans.getValue("GETORGCODE/DATA/ORG_CODE");
            String sDefRole1 = trans.getValue("GETDEFROLE/DATA/ROLE_CODE");

            if (!mgr.isEmpty(sDefRole1))
               sDefRole =sDefRole1;

            if (bSalesPartOk)
            {
               trans.clear();

               if (mgr.isEmpty(sDefRole))
               {
                  cmd = trans.addCustomFunction("GETDEFCATNO1", "Organization_Sales_Part_API.Get_Def_Catalog_No", "CATALOG_NO");
                  cmd.addParameter("CONTRACT", sOrgContract);
                  cmd.addParameter("ORG_CODE", sOrgCode);

                  cmd = trans.addCustomFunction("GETDEFCONTRACT1", "Organization_Sales_Part_API.Get_Def_Contract", "CATALOG_CONTRACT");
                  cmd.addParameter("CONTRACT", sOrgContract);
                  cmd.addParameter("ORG_CODE", sOrgCode);

                  //Bug 69392, start
                  if (mgr.isModuleInstalled("ORDER"))
                  {
                     cmd = trans.addCustomFunction("GETDEFCATDESC1","Sales_Part_API.Get_Catalog_Desc","CATALOGDESC");
                     cmd.addReference("CATALOG_CONTRACT","GETDEFCONTRACT1/DATA");
                     cmd.addReference("CATALOG_NO","GETDEFCATNO1/DATA");
                  }
                  //Bug 69392, end

                  trans = mgr.validate(trans);

                  sCatalogNo1 = trans.getValue("GETDEFCATNO1/DATA/CATALOG_NO");
                  sCatalogContract1 = trans.getValue("GETDEFCONTRACT1/DATA/CATALOG_CONTRACT");
                  //Bug 69392, start
                  if (mgr.isModuleInstalled("ORDER"))
                     sCatalogDesc1 = trans.getValue("GETDEFCATDESC1/DATA1/CATALOGDESC");
                  //Bug 69392, end

               }
               else
               {
                  cmd = trans.addCustomFunction("GETDEFCATNO2", "Role_Sales_Part_API.Get_Def_Catalog_No", "CATALOG_NO");
                  cmd.addParameter("ROLE_CODE", sDefRole);
                  cmd.addParameter("CONTRACT", sOrgContract);

                  //Bug 69392, start
                  if (mgr.isModuleInstalled("ORDER"))
                  {
                     cmd = trans.addCustomFunction("GETDEFCATDESC2","Sales_Part_API.Get_Catalog_Desc","CATALOGDESC");
                     cmd.addParameter("CONTRACT", sOrgContract);
                     cmd.addReference("CATALOG_NO","GETDEFCATNO2/DATA");
                  }
                  //Bug 69392, end

                  trans = mgr.validate(trans);

                  sCatalogNo2 = trans.getValue("GETDEFCATNO2/DATA/CATALOG_NO");
                  sCatalogContract2 = sOrgContract;
                  //Bug 69392, start
                  if (mgr.isModuleInstalled("ORDER"))
                     sCatalogDesc2 = trans.getValue("GETDEFCATDESC2/DATA/CATALOGDESC");
                  //Bug 69392, end

               }


               if (!mgr.isEmpty(sCatalogNo2))
               {
                  sCatalogNo = sCatalogNo2;
                  sCatalogContract = sCatalogContract2;
                  sCatalogDesc2 =  sCatalogContract2;
               }
               else if (!mgr.isEmpty(sCatalogNo1))
               {
                  sCatalogNo = sCatalogNo1;
                  sCatalogContract = sCatalogContract1;
                  sCatalogDesc =  sCatalogContract1;
               }

               trans.clear();
               if (!sCatalogNo0.equals(sCatalogNo))
               {


                  if (bPricingOk)
                  {
                     cmd = trans.addCustomFunction("GETCURRCODE","ACTIVE_SEPARATE_API.Get_Currency_Code","CURRENCY_CODE");
                     cmd.addParameter("ITEM2_WO_NO");

                     cmd = trans.addCustomFunction("GETCUSTOMERNO","Active_Work_Order_API.Get_Customer_No","CUSTOMER_NO");
                     cmd.addParameter("ITEM2_WO_NO");

                     cmd = trans.addCustomFunction("GETPRICELIST","Customer_Order_Pricing_Api.Get_Valid_Price_List","PRICE_LIST_NO");
                     cmd.addParameter("CATALOG_CONTRACT", sCatalogContract);
                     cmd.addParameter("CATALOG_NO", sCatalogNo);
                     cmd.addReference("CUSTOMER_NO", "GETCUSTOMERNO/DATA");
                     cmd.addReference("CURRENCY_CODE", "GETCURRCODE/DATA");
                  }

                  trans = mgr.validate(trans);


                  if (bPricingOk)
                     sPriceListNo = trans.getValue("GETPRICELIST/DATA/PRICE_LIST_NO");

                  String salesPartInfo[] = getSalesPartInfo(false,true,sPriceListNo,sCatalogContract,sCatalogNo);
                  sDiscount = salesPartInfo[1];
                  sListPrice = salesPartInfo[2];
                  sSalesPriceAmount = salesPartInfo[3];
                  sDiscountedPrice = salesPartInfo[4];
               }
            }
            txt = (mgr.isEmpty(sSign) ? "": (sSign))+ "^" + 
                  (mgr.isEmpty(sDefRole) ? "": (sDefRole))+ "^"+
                  (mgr.isEmpty(sOrgContract) ? "": (sOrgContract))+ "^"+
                  (mgr.isEmpty(sOrgCode) ? "": (sOrgCode))+ "^"+
                  (mgr.isEmpty(sCatalogContract) ? "": (sCatalogContract))+ "^"+
                  (mgr.isEmpty(sCatalogNo) ? "": (sCatalogNo))+ "^"+
                  (mgr.isEmpty(sCatalogDesc) ? "": (sCatalogDesc))+ "^"+
                  (mgr.isEmpty(sPriceListNo) ? "": (sPriceListNo))+ "^"+
                  (mgr.isEmpty(sListPrice) ? "": (sListPrice))+ "^"+
                  (mgr.isEmpty(sDiscount) ? "": (sDiscount))+ "^"+
                  (mgr.isEmpty(sDiscountedPrice) ? "": (sDiscountedPrice))+ "^"+
                  (mgr.isEmpty(sSalesPriceAmount) ? "": (sSalesPriceAmount))+ "^";
            mgr.responseWrite(txt);
         }
         else
         {
            txt = "" + "^" + "" + "^" + (mgr.isEmpty(sOrgContract) ? "": (sOrgContract))+ "^"+
                  "" + "^" + (mgr.isEmpty(sCatalogContract) ? "": (sCatalogContract))+ "^"+
                  "" + "^" + "" + "^" + "" + "^" + "" + "^" + "" + "^" + "" + "^" + "" + "^" ;
            mgr.responseWrite(txt); 
         }    
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
         String sOrgCode = mgr.readValue("ITEM2_ORG_CODE");
         String sOrgContract = mgr.readValue("ITEM2_CONTRACT");
         String roleCode = mgr.readValue("ROLE_CODE");
         String depart = "";
         String departCont = "";
         double planHrs=0;
         String planHours;
         double totManHrs=0;
         double remainHrs=0;
         String remainHours;

         String new_sign = mgr.readValue("SIGN","");

         String allocatedHours;
         double allocatedHrs = mgr.readNumberValue("ITEM2_PLAN_HRS");
         if (isNaN(allocatedHrs))
            allocatedHrs = 0;
         allocatedHours = mgr.getASPField("ITEM8_TOTAL_ALLOCATED_HOURS").formatNumber(allocatedHrs);

         totManHrs = mgr.readNumberValue("TOTAL_MAN_HOURS");
         if (isNaN(totManHrs))
            totManHrs = 0;

         remainHrs = totManHrs - allocatedHrs ;
         remainHours = mgr.getASPField("ITEM8_TOTAL_ALLOCATED_HOURS").formatNumber(remainHrs);

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
            cmd = trans.addCustomFunction("SIGNID","Employee_API.Get_Max_Maint_Emp","SIGN_ID");                     
            cmd.addParameter("ITEM2_COMPANY");
            cmd.addParameter("SIGN");

            trans = mgr.validate(trans);
            emp_id = trans.getValue("SIGNID/DATA/SIGN_ID");
            sign = new_sign;

            trans.clear();
         }

         if (mgr.isEmpty(sOrgCode))
         {
            cmd = trans.addCustomFunction("EMPORGCOD","Employee_API.Get_Organization","ORG_CODE");
            cmd.addParameter("ITEM2_COMPANY");
            cmd.addParameter("SIGN_ID",emp_id);
         }
         if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
         {
            cmd = trans.addCustomFunction("EMPORGCONT","Employee_API.Get_Org_Contract","ITEM2_ORG_CONTRACT");
            cmd.addParameter("ITEM2_COMPANY");
            cmd.addParameter("SIGN_ID",emp_id);
         }

         trans = mgr.validate(trans);

         if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
            sOrgContract = trans.getValue("EMPORGCONT/DATA/ITEM2_ORG_CONTRACT");
         if (mgr.isEmpty(sOrgCode))
            sOrgCode = trans.getValue("EMPORGCOD/DATA/ORG_CODE");

         trans.clear();

         cmd = trans.addCustomFunction("CATCONT","Active_Separate_Api.Get_Contract","ITEM2_CONTRACT");
         cmd.addParameter("ITEM2_WO_NO",mgr.readValue("ITEM2_WO_NO"));

         if (mgr.isEmpty(roleCode))
         {
            cmd = trans.addCustomFunction("DEFROLE","Employee_Role_API.Get_Default_Role","ROLE_CODE");
            cmd.addParameter("ITEM2_COMPANY");
            cmd.addParameter("SIGN_ID",emp_id); 

            cmd = trans.addCustomFunction("ROLECATANO","Role_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
            cmd.addReference("ROLE_CODE","DEFROLE/DATA");
            cmd.addReference("ITEM2_CONTRACT","CATCONT/DATA");

            //Bug 69392, start
            if (mgr.isModuleInstalled("ORDER"))
            {
               cmd = trans.addCustomFunction("DEFACODESC","Sales_Part_API.Get_Catalog_Desc","CATALOGDESC");
               cmd.addReference("ITEM2_CONTRACT","CATCONT/DATA");
               cmd.addReference("CATALOG_NO","ROLECATANO/DATA");
            }
            //Bug 69392, end
         }
         else
         {
            cmd = trans.addCustomFunction("ROLECATANO","Role_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
            cmd.addParameter("ROLE_CODE",roleCode);
            cmd.addReference("ITEM2_CONTRACT","CATCONT/DATA");

            //Bug 69392, start
            if (mgr.isModuleInstalled("ORDER"))
            {
               cmd = trans.addCustomFunction("DEFACODESC","Sales_Part_API.Get_Catalog_Desc","CATALOGDESC");
               cmd.addReference("ITEM2_CONTRACT","CATCONT/DATA");
               cmd.addReference("CATALOG_NO","ROLECATANO/DATA"); 
            }
            //Bug 69392, end
         }

         trans = mgr.validate(trans);

         String catalogNo = trans.getValue("ROLECATANO/DATA/CATALOG_NO");
         String catalogContract = trans.getValue("CATCONT/DATA/CONTRACT");

         //Bug 69392, start
         String catalogDescp = "";
         if (mgr.isModuleInstalled("ORDER"))
            catalogDescp = trans.getValue("DEFACODESC/DATA/CATALOGDESC");
         //Bug 69392, end
         String signId = emp_id;

         if (mgr.isEmpty(roleCode))
            roleCode = trans.getValue("DEFROLE/DATA/ROLE_CODE");

         if (mgr.isEmpty(roleCode) && mgr.isEmpty(catalogNo) && (catalogContract == sOrgContract))
         {
            trans.clear();

            cmd = trans.addCustomFunction("ORGCATANO","Organization_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
            cmd.addParameter("ITEM2_CONTRACT",sOrgContract);
            cmd.addParameter("ITEM2_ORG_CODE",sOrgCode);

            //Bug 69392, start
            if (mgr.isModuleInstalled("ORDER"))
            {
               cmd = trans.addCustomFunction("ORGACODESC","Sales_Part_API.Get_Catalog_Desc","CATALOGDESC");
               cmd.addParameter("ITEM2_CONTRACT",sOrgContract);
               cmd.addReference("CATALOG_NO","ORGCATANO/DATA");
            }
            //Bug 69392, end

            trans = mgr.validate(trans);

            catalogNo = trans.getValue("ORGCATANO/DATA/CATALOG_NO");
            //Bug 69392, start
            if (mgr.isModuleInstalled("ORDER"))
               catalogDescp = trans.getValue("ORGACODESC/DATA/CATALOGDESC");
            //Bug 69392, end
         }

         String colRoleCode = "";
         if (mgr.isEmpty(roleCode))
            colRoleCode = mgr.readValue("ROLE_CODE","");
         else
            colRoleCode = roleCode;

         double planMen = mgr.readNumberValue ("PLAN_MEN");
         if (isNaN(planMen)) planMen = 0;

         planHrs = mgr.readNumberValue ("PLAN_HRS");
         if (isNaN(planHrs)) planHrs = 0;

         double nCost = getCost(sOrgContract,sOrgCode,colRoleCode,catalogContract,catalogNo,mgr.readValue("ITEM2_WO_NO"));
         double nCostAmount = countCostAmount(nCost,planMen,planHrs);

         String nCostStr = mgr.getASPField("COST").formatNumber(nCost);
         String colAmountCostStr = mgr.getASPField("COSTAMOUNT").formatNumber(nCostAmount);
         //bug id 59224,start
         trans.clear();
         cmd = trans.addCustomFunction("GETITEM2VAlORGDESC","Organization_Api.Get_Description","ITEM2_ORG_CODE_DESC");
         cmd.addParameter("ITEM2_CONTRACT",sOrgContract);
         cmd.addParameter("ITEM2_ORG_CODE",sOrgCode);

         cmd = trans.addCustomFunction("GETITEM2VAlROLECODE","ROLE_API.Get_Description","ROLE_CODE_DESC");
         cmd.addParameter("ROLE_CODE",roleCode);


         trans = mgr.validate(trans);

         String sitem2OrgDesc = trans.getValue("GETITEM2VAlORGDESC/DATA/ITEM2_ORG_CODE_DESC");
         String sroleDesc = trans.getValue("GETITEM2VAlROLECODE/DATA/ROLE_CODE_DESC");

         txt = (mgr.isEmpty(signId) ? "": (signId))+ "^" + 
               (mgr.isEmpty(roleCode) ? "": (roleCode))+ "^" + 
               (mgr.isEmpty(sOrgContract) ? "": (sOrgContract))+ "^" +
               (mgr.isEmpty(sOrgCode) ? "": (sOrgCode))+ "^" +
               (mgr.isEmpty(catalogNo) ? "": (catalogNo)) + "^" + 
               (mgr.isEmpty(catalogDescp) ? "": (catalogDescp))+ "^" + 
               (mgr.isEmpty(nCostStr) ? "": (nCostStr))+ "^" + 
               (mgr.isEmpty(colAmountCostStr) ? "": (colAmountCostStr))+ "^"+
               (mgr.isEmpty(sign) ? "": (sign))+ "^" +
               (mgr.isEmpty(allocatedHours)? "": (allocatedHours))+ "^" +  //allocated hours
               (mgr.isEmpty(allocatedHours)? "": (allocatedHours))+ "^" +  //total allocated hours
               (mgr.isEmpty(remainHours)? "": (remainHours))+ "^"+
               (mgr.isEmpty(sitem2OrgDesc)? "": (sitem2OrgDesc))+ "^"+
               (mgr.isEmpty(sroleDesc)? "": (sroleDesc))+ "^";

         mgr.responseWrite(txt);
      }

      else if ("ALLOCATED_HOURS".equals(val))
      {
         double allocHours = mgr.readNumberValue ("ALLOCATED_HOURS");
         if (isNaN(allocHours)) allocHours = 0;
         double totManHrs = mgr.readNumberValue ("TOTAL_MAN_HOURS");
         if (isNaN(totManHrs)) totManHrs = 0;
         double remainHrs = totManHrs - allocHours;

         String allocatedHrs = mgr.getASPField("TOTAL_ALLOCATED_HOURS").formatNumber(allocHours);
         String remainingHrs = mgr.getASPField("TOTAL_REMAINING_HOURS").formatNumber(remainHrs);

         txt = (mgr.isEmpty(allocatedHrs) ? "": (allocatedHrs)) + "^" +  //total allocated hrs
               (mgr.isEmpty(remainingHrs) ? "": (remainingHrs)) + "^";

         mgr.responseWrite(txt);    

      }
      else if ("PLAN_MEN".equals(val) || "ITEM2_PLAN_HRS".equals(val))
      {
         String colRoleCode = mgr.readValue("ROLE_CODE","");
         String colOrgContract = mgr.readValue("ITEM2_CONTRACT","");
         String colOrgCode = mgr.readValue("ITEM2_ORG_CODE","");
         String colCatalogContract = mgr.readValue("CATALOG_CONTRACT","");
         String colCatalogNo  = mgr.readValue("CATALOG_NO","");

         double planMen = mgr.readNumberValue ("PLAN_MEN");
         if (isNaN(planMen)) planMen = 0;

         double planHrs = mgr.readNumberValue ("ITEM2_PLAN_HRS");
         if (isNaN(planHrs)) planHrs = 0;

         double totAllocHrs = mgr.readNumberValue("TOTAL_ALLOCATED_HOURS");
         if (isNaN(totAllocHrs))
            totAllocHrs = 0;

         double manHours  = planHrs * planMen;
         double remainHrs = manHours - totAllocHrs;

         double nCost = getCost(colOrgContract,colOrgCode,colRoleCode,colCatalogContract,colCatalogNo,mgr.readValue("ITEM2_WO_NO"));
         double nCostAmount = countCostAmount(nCost,planMen,planHrs);

         String nCostStr = mgr.getASPField("COST").formatNumber(nCost);
         String colAmountCostStr = mgr.getASPField("COSTAMOUNT").formatNumber(nCostAmount);

         trans.clear();

         String salesPartInfo[] = getSalesPartInfo(true,false,"","","");

         String strPriceListNo = salesPartInfo[0];
         String strDiscount = salesPartInfo[1];
         String strListPrice = salesPartInfo[2];
         String strSalesPriceAmount = salesPartInfo[3];
         String strDiscountedPrice = salesPartInfo[4];

         String manHoursStr = mgr.getASPField("TOTAL_MAN_HOURS").formatNumber(manHours);
         String remainHrsStr = mgr.getASPField("TOTAL_REMAINING_HOURS").formatNumber(remainHrs);

         txt = (mgr.isEmpty(nCostStr) ? "": (nCostStr)) + "^" + 
               (mgr.isEmpty(colAmountCostStr) ? "": (colAmountCostStr)) + "^" + 
               (mgr.isEmpty(strPriceListNo) ? "" : strPriceListNo) + "^" +
               (mgr.isEmpty(strDiscount) ? "" : strDiscount) + "^" +
               (mgr.isEmpty(strListPrice) ? "" : strListPrice) + "^" +
               (mgr.isEmpty(strSalesPriceAmount) ? "" : strSalesPriceAmount) + "^" +
               (mgr.isEmpty(strDiscountedPrice) ? "" : strDiscountedPrice) + "^" +
               (mgr.isEmpty(manHoursStr)?"":manHoursStr) + "^" + (mgr.isEmpty(remainHrsStr)?"":remainHrsStr) + "^" ;

         mgr.responseWrite(txt);     

      }
      else if ("CATALOG_CONTRACT".equals(val))
      {
         String colRoleCode = mgr.readValue("ROLE_CODE","");
         String colOrgContract = mgr.readValue("ITEM2_CONTRACT","");
         String colOrgCode = mgr.readValue("ITEM2_ORG_CODE","");
         String colCatalogContract = mgr.readValue("CATALOG_CONTRACT","");
         String colCatalogNo  = mgr.readValue("CATALOG_NO","");
         double planMen = mgr.readNumberValue ("PLAN_MEN");
         if (isNaN(planMen)) planMen = 0;

         double planHrs = mgr.readNumberValue ("ITEM2_PLAN_HRS");
         if (isNaN(planHrs)) planHrs = 0;

         double nCost = getCost(colOrgContract,colOrgCode,colRoleCode,colCatalogContract,colCatalogNo,mgr.readValue("ITEM2_WO_NO"));
         double nCostAmount = countCostAmount(nCost,planMen,planHrs);

         String nCostStr = mgr.getASPField("COST").formatNumber(nCost);
         String colAmountCostStr = mgr.getASPField("COSTAMOUNT").formatNumber(nCostAmount);

         String salesPartInfo[] = getSalesPartInfo(false,false,"","","");

         String strPriceListNo = salesPartInfo[0];
         String strDiscount = salesPartInfo[1];
         String strListPrice = salesPartInfo[2];
         String strSalesPriceAmount = salesPartInfo[3];
         String strDiscountedPrice = salesPartInfo[4];

         txt = (mgr.isEmpty(nCostStr) ? "": (nCostStr)) + "^" + 
               (mgr.isEmpty(colAmountCostStr) ? "": (colAmountCostStr)) + "^" + 
               (mgr.isEmpty(strPriceListNo) ? "" : strPriceListNo) + "^" +
               (mgr.isEmpty(strDiscount) ? "" : strDiscount) + "^" +
               (mgr.isEmpty(strListPrice) ? "" : strListPrice) + "^" +
               (mgr.isEmpty(strSalesPriceAmount) ? "" : strSalesPriceAmount) + "^" +
               (mgr.isEmpty(strDiscountedPrice) ? "" : strDiscountedPrice) + "^" ;

         mgr.responseWrite(txt);     
      }
      else if ("CATALOG_NO".equals(val))
      {
         String colPriceList = "";

         secBuff = mgr.newASPTransactionBuffer();
         secBuff.addSecurityQuery("Customer_Order_Pricing_Api","Get_Valid_Price_List");

         secBuff = mgr.perform(secBuff);

         String item2RowStatus = mgr.readValue("ITEM2_ROWSTATUS");
         boolean getPriceList = false;

         if (secBuff.getSecurityInfo().itemExists("Customer_Order_Pricing_Api.Get_Valid_Price_List") && ("NEW".equals(item2RowStatus)))
            getPriceList = true;

         trans.clear();

         //Bug 69392, start
         if (mgr.isModuleInstalled("ORDER"))
         {
            cmd = trans.addCustomFunction("GETCATALOGDESCFUNC","Sales_Part_API.Get_Catalog_Desc","CATALOGDESC");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addParameter("CATALOG_NO");
         }
         //Bug 69392, end
         if (getPriceList)
         {
            cmd = trans.addCustomFunction("GETPRICELISTFUNC","Customer_Order_Pricing_Api.Get_Valid_Price_List","PRICE_LIST_NO");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addParameter("CATALOG_NO");
            cmd.addParameter("CUSTOMER_NO");
            cmd.addParameter("HEAD_CURRENCEY_CODE");
         }

         trans = mgr.perform(trans);

         //Bug 69392, start
         String colCatalogDesc = "";
         if (mgr.isModuleInstalled("ORDER"))
            colCatalogDesc = trans.getValue("GETCATALOGDESCFUNC/DATA/CATALOGDESC");
         //Bug 69392, end

         if (getPriceList)
            colPriceList = trans.getValue("GETPRICELISTFUNC/DATA/PRICE_LIST_NO");
         else
            colPriceList = mgr.readValue("PRICE_LIST_NO");

         String colRoleCode = mgr.readValue("ROLE_CODE","");
         String colOrgContract = mgr.readValue("ITEM2_CONTRACT","");
         String colOrgCode = mgr.readValue("ITEM2_ORG_CODE","");
         String colCatalogContract = mgr.readValue("CATALOG_CONTRACT","");
         String colCatalogNo  = mgr.readValue("CATALOG_NO","");
         double planMen = mgr.readNumberValue ("PLAN_MEN");
         if (isNaN(planMen)) planMen = 0;

         double planHrs = mgr.readNumberValue ("ITEM2_PLAN_HRS");
         if (isNaN(planHrs)) planHrs = 0;

         double nCost = getCost(colOrgContract,colOrgCode,colRoleCode,colCatalogContract,colCatalogNo,mgr.readValue("ITEM2_WO_NO"));
         double nCostAmount = countCostAmount(nCost,planMen,planHrs);

         String nCostStr = mgr.getASPField("COST").formatNumber(nCost);
         String colAmountCostStr = mgr.getASPField("COSTAMOUNT").formatNumber(nCostAmount);

         String salesPartInfo[] = getSalesPartInfo(false,false,colPriceList,"","");

         String strPriceListNo = salesPartInfo[0];
         String strDiscount = salesPartInfo[1];
         String strListPrice = salesPartInfo[2];
         String strSalesPriceAmount = salesPartInfo[3];
         String strDiscountedPrice = salesPartInfo[4];

         txt = (mgr.isEmpty(colCatalogDesc) ? "" : colCatalogDesc) + "^" +
               (mgr.isEmpty(nCostStr) ? "" : nCostStr) + "^" +
               (mgr.isEmpty(colAmountCostStr) ? "" : colAmountCostStr) + "^" +
               (mgr.isEmpty(colPriceList) ? "" : colPriceList) + "^" +
               (mgr.isEmpty(strDiscount) ? "" : strDiscount) + "^" +
               (mgr.isEmpty(strListPrice) ? "" : strListPrice) + "^" +
               (mgr.isEmpty(strSalesPriceAmount) ? "" : strSalesPriceAmount) + "^" +
               (mgr.isEmpty(strDiscountedPrice) ? "" : strDiscountedPrice) + "^" ;

         mgr.responseWrite(txt);
      }
      else if ("PRICE_LIST_NO".equals(val))
      {
         String colRoleCode = mgr.readValue("ROLE_CODE","");
         String colOrgContract = mgr.readValue("ITEM2_CONTRACT","");
         String colOrgCode = mgr.readValue("ITEM2_ORG_CODE","");
         String colCatalogContract = mgr.readValue("CATALOG_CONTRACT","");
         String colCatalogNo  = mgr.readValue("CATALOG_NO","");
         double planMen = mgr.readNumberValue ("PLAN_MEN");
         if (isNaN(planMen)) planMen = 0;

         double planHrs = mgr.readNumberValue ("ITEM2_PLAN_HRS");
         if (isNaN(planHrs)) planHrs = 0;

         double nCost = getCost(colOrgContract,colOrgCode,colRoleCode,colCatalogContract,colCatalogNo,mgr.readValue("ITEM2_WO_NO"));
         double nCostAmount = countCostAmount(nCost,planMen,planHrs);

         String nCostStr = mgr.getASPField("COST").formatNumber(nCost);
         String colAmountCostStr = mgr.getASPField("COSTAMOUNT").formatNumber(nCostAmount);

         String salesPartInfo[] = getSalesPartInfo(false,false,"","","");

         String strPriceListNo = salesPartInfo[0];
         String strDiscount = salesPartInfo[1];
         String strListPrice = salesPartInfo[2];
         String strSalesPriceAmount = salesPartInfo[3];
         String strDiscountedPrice = salesPartInfo[4];

         txt = (mgr.isEmpty(nCostStr) ? "": (nCostStr)) + "^" + 
               (mgr.isEmpty(colAmountCostStr) ? "": (colAmountCostStr)) + "^" + 
               (mgr.isEmpty(strPriceListNo) ? "" : strPriceListNo) + "^" +
               (mgr.isEmpty(strListPrice) ? "" : strListPrice) + "^" +
               (mgr.isEmpty(strSalesPriceAmount) ? "" : strSalesPriceAmount) + "^" +
               (mgr.isEmpty(strDiscount) ? "" : strDiscount) + "^" +
               (mgr.isEmpty(strDiscountedPrice) ? "" : strDiscountedPrice) + "^" ;

         mgr.responseWrite(txt);     
      }
      else if ("LISTPRICE".equals(val) || "DISCOUNT".equals(val))
      {
         double nBuyQtyDue = 0;
         double colDiscountedPrice = 0;
         double colSalesPriceAmount = 0;

         double planHrs = mgr.readNumberValue("ITEM2_PLAN_HRS");
         double planMen = mgr.readNumberValue("PLAN_MEN");

         if (isNaN(planMen) && !isNaN(planHrs))
            nBuyQtyDue = planHrs;
         else if (!isNaN(planMen) && !isNaN(planHrs))
            nBuyQtyDue = planHrs*planMen;


         double colDiscount = mgr.readNumberValue("DISCOUNT");
         if (isNaN(colDiscount)) colDiscount = 0;

         double colListPrice = mgr.readNumberValue("LISTPRICE");
         if (isNaN(colListPrice)) colListPrice = 0;

         colDiscountedPrice = (colListPrice - (colDiscount/100 * colListPrice));
         colSalesPriceAmount = (colListPrice - (colDiscount/100 * colListPrice)) * nBuyQtyDue; 

         String strDiscountedPrice = mgr.getASPField("DISCOUNTED_PRICE").formatNumber(colDiscountedPrice);
         String strSalesPriceAmount = mgr.getASPField("SALESPRICEAMOUNT").formatNumber(colSalesPriceAmount);

         txt = (mgr.isEmpty(strDiscountedPrice) ? "" : strDiscountedPrice) + "^" +
               (mgr.isEmpty(strSalesPriceAmount) ? "" : strSalesPriceAmount) + "^"; 

         mgr.responseWrite(txt);
      }
      else if ("COST".equals(val))
      {
         double nCost = mgr.readNumberValue("COST");
         if (isNaN(nCost))
            nCost = 0;

         String colOrgContract = mgr.readValue("ITEM2_CONTRACT","");
         String colOrgCode = mgr.readValue("ITEM2_ORG_CODE","");
         String colRoleCode = mgr.readValue("ROLE_CODE","");
         String sOrgContract = mgr.readValue("ITEM2_CONTRACT","");
         String sOrgCode = mgr.readValue("ORG_CODE","");
         String cataNum  = mgr.readValue("CATALOG_NO","");

         nCost = getCostVal(nCost,colOrgContract,colOrgCode,colRoleCode,sOrgContract,sOrgCode,cataNum);

         String nCostStr = mgr.getASPField("COST").formatNumber(nCost);
         String colAmountCostStr = countCostVal(nCost);

         txt = (mgr.isEmpty(nCostStr) ? "": (nCostStr))+ "^" + (mgr.isEmpty(colAmountCostStr) ? "": (colAmountCostStr))+ "^";

         mgr.responseWrite(txt);
      }
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

         txt = (mgr.isEmpty(dueDate) ? "": dueDate) + "^" + (mgr.isEmpty(nPreAccId) ? "": nPreAccId) + "^" + (mgr.isEmpty(item3Contract) ? "": item3Contract) + "^" + (mgr.isEmpty(company) ? "": company) + "^" + (mgr.isEmpty(mchCode) ? "": mchCode) + "^" + (mgr.isEmpty(item3Desc) ? "": item3Desc) + "^";

         mgr.responseWrite(txt);
      }
      else if ("MCHCODE".equals(val))
      {
         cmd  = trans.addCustomFunction("ITEM3DESC","Maintenance_Object_API.Get_Mch_Name","ITEM3DESCRIPTION");
         cmd.addParameter("ITEM3_CONTRACT",mgr.readValue("ITEM3_CONTRACT",""));
         cmd.addParameter("MCHCODE");

         trans = mgr.validate(trans);

         String item3Descr = trans.getValue("ITEM3DESC/DATA/ITEM3DESCRIPTION");

         txt = (mgr.isEmpty(item3Descr) ? "": item3Descr) + "^";

         mgr.responseWrite(txt);
      }
      else if ("SIGNATURE".equals(val))
      {
         cmd = trans.addCustomFunction("SIGNID","Company_Emp_API.Get_Max_Employee_Id","SIGNATURE_ID");
         cmd.addParameter("COMPANY",mgr.readValue("ITEM3_COMPANY",""));
         cmd.addParameter("SIGNATURE");

         cmd = trans.addCustomFunction("SIGNNAME","EMPLOYEE_API.Get_Employee_Info","SIGNATURENAME");
         cmd.addParameter("COMPANY",mgr.readValue("ITEM3_COMPANY",""));
         cmd.addReference("SIGNATURE_ID","SIGNID/DATA");

         trans = mgr.validate(trans);

         String signId = trans.getValue("SIGNID/DATA/SIGNATURE_ID");
         String signName = trans.getValue("SIGNNAME/DATA/SIGNATURENAME");

         txt = (mgr.isEmpty(signId) ? "": signId) + "^" + (mgr.isEmpty(signName) ? "": signName) + "^";

         mgr.responseWrite(txt);
      }
      else if ("INT_DESTINATION_ID".equals(val))
      {
         cmd = trans.addCustomFunction("INTDESTDESC","Internal_Destination_API.Get_Description","INT_DESTINATION_DESC");
         cmd.addParameter("CONTRACT",mgr.readValue("ITEM3_CONTRACT",""));
         cmd.addParameter("INT_DESTINATION_ID");

         trans = mgr.validate(trans);

         String intDestDesc = trans.getValue("INTDESTDESC/DATA/INT_DESTINATION_DESC");

         txt = (mgr.isEmpty(intDestDesc) ? "": intDestDesc) + "^";

         mgr.responseWrite(txt);
      }
      else if ("PART_NO".equals(val))
      {
         String defCond = new String();
         String condesc = new String();
         String sDefCondiCode= "";
         String qtyOnHand1 = "";
         String qtyAvail1 = "";
         double qtyOnHand = 0;
         double qtyAvail = 0;
         String activeInd = "";
         String vendorNo = "";
         String custOwner = "";
         String partOwnership = "";
         String sOwner = mgr.readValue("OWNER");
         String ownership = "";

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
         if (checksec("PURCHASE_PART_SUPPLIER_API.Get_Primary_Supplier_No",1))
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
            if ((!mgr.isEmpty(vendorNo)) && "CUSTOMER OWNED".equals(partOwnership))
            {
               sClientOwnershipDefault = partOwnership;
               sOwner = custOwner;
            }
            trans.clear();
         }

         //Bug 69392, start
         if (mgr.isModuleInstalled("ORDER"))
         {

            cmd = trans.addCustomFunction("CATANO","Sales_Part_API.Get_Catalog_No_For_Part_No","ITEM_CATALOG_NO");
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");

            cmd = trans.addCustomFunction("CATADESC","Sales_Part_API.Get_Catalog_Desc","ITEMCATALOGDESC");
            cmd.addParameter("ITEM_CATALOG_CONTRACT");
            cmd.addReference("ITEM_CATALOG_NO","CATANO/DATA");

            cmd = trans.addCustomFunction("GETACT","Sales_Part_API.Get_Activeind","ACTIVEIND");
            cmd.addParameter("ITEM_CATALOG_CONTRACT");
            cmd.addReference("ITEM_CATALOG_NO","CATANO/DATA");

            cmd = trans.addCustomFunction("GETACTDB","Active_Sales_Part_API.Encode","ACTIVEIND_DB");
            cmd.addReference("ACTIVEIND","GETACT/DATA");
         }
         //Bug 69392, end

         cmd = trans.addCustomFunction("PARTDESC","INVENTORY_PART_API.Get_Description","SPAREDESCRIPTION");
         cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
         cmd.addParameter("PART_NO");

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

         if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",1))
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
         }
         cmd = trans.addCustomFunction("UNITMES","Purchase_Part_Supplier_API.Get_Unit_Meas","UNITMEAS");
         cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
         cmd.addParameter("PART_NO");

         //Bug 69392, start
         if (mgr.isModuleInstalled("ORDER"))
         {
            cmd = trans.addCustomFunction("GETSALESPRICEGRPID","SALES_PART_API.GET_SALES_PRICE_GROUP_ID","SALES_PRICE_GROUP_ID");
            cmd.addParameter("ITEM_CATALOG_CONTRACT");
            cmd.addReference("CATALOG_NO","CATANO/DATA");
         }
         //Bug 69392, end
         cmd = trans.addCustomFunction("SUUPCODE","INVENTORY_PART_API.Get_Supply_Code ","SUPPLY_CODE");
         cmd.addParameter("CATALOG_CONTRACT");
         cmd.addReference("CATALOG_NO","CATANO/DATA");

         cmd = trans.addCustomFunction("CONDALLOW","PART_CATALOG_API.Get_Condition_Code_Usage_Db","COND_CODE_USAGE");
         cmd.addParameter("PART_NO");

         trans = mgr.validate(trans);

         //Bug 69392, start
         String cataNo = "";
         String cataDesc = "";
         String salesPriceGroupId = "";
         if (mgr.isModuleInstalled("ORDER"))
         {
            cataNo = trans.getBuffer("CATANO/DATA").getFieldValue("ITEM_CATALOG_NO");
            cataDesc = trans.getValue("CATADESC/DATA/ITEMCATALOGDESC");
            activeInd = trans.getValue("GETACTDB/DATA/ACTIVEIND_DB");
            salesPriceGroupId = trans.getValue("GETSALESPRICEGRPID/DATA/SALES_PRICE_GROUP_ID");
         }
         //Bug 69392, end
         String hasStruct = trans.getValue("SPARESTRUCT/DATA/HASSPARESTRUCTURE");
         String dimQty = trans.getValue("DIMQUAL/DATA/DIMQTY");
         String typeDesi = trans.getValue("TYPEDESI/DATA/TYPEDESIGN");
         if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",1))
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
         String unitMeas = trans.getValue("UNITMES/DATA/UNITMEAS");
         String partDesc = trans.getValue("PARTDESC/DATA/SPAREDESCRIPTION");
         String suppCode = trans.getValue("SUUPCODE/DATA/SUPPLY_CODE");
         String condco = trans.getValue("CONDALLOW/DATA/COND_CODE_USAGE");

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
         cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
         cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));

         mgr.perform(trans); 

         txt = (mgr.isEmpty(cataNo) ? "": cataNo) + "^" + 
               (mgr.isEmpty(cataDesc) ? "": cataDesc) + "^" + 
               (mgr.isEmpty(hasStruct) ? "": hasStruct) + "^" + 
               (mgr.isEmpty(dimQty) ? "": dimQty) + "^" +
               (mgr.isEmpty(typeDesi) ? "": typeDesi) + "^" + 
               (mgr.isEmpty(qtyOnHand1) ? "": qtyOnHand1) + "^" +
               (mgr.isEmpty(unitMeas) ? "": unitMeas) + "^" +
               (mgr.isEmpty(partDesc) ? "": partDesc) + "^"+
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

         trans = mgr.validate(trans);
         qtyOnHand = trans.getBuffer("INVONHAND/DATA").getFieldValue("QTYONHAND");
         double qtyAvail = trans.getNumberValue("INVAVL/DATA/QTY_AVAILABLE"); 
         String qtyAvail1 = mgr.getASPField("QTY_AVAILABLE").formatNumber(qtyAvail);

         txt = (mgr.isEmpty(qtyOnHand) ? "": qtyOnHand) + "^" + (mgr.isEmpty(qtyAvail1) ? "": qtyAvail1) + "^";

         mgr.responseWrite(txt);
      }
      else if ("CONDITION_CODE".equals(val))
      {
         String qtyOnHand1 = "";
         String qtyAvail1 = "";
         double qtyOnHand = 0;
         double qtyAvail = 0;

         if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",1))
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
         }

         cmd = trans.addCustomFunction("CONCODE","CONDITION_CODE_API.Get_Description","CONDDESC");
         cmd.addParameter("CONDITION_CODE");

         double nCost = mgr.readNumberValue("ITEM_COST");
         if (isNaN(nCost))
            nCost = 0;

         String strCost;

         if ("CUSTOMER OWNED".equals(mgr.readValue("PART_OWNERSHIP_DB")))
            nCost = 0;
         else
         {
            /*if (!mgr.isEmpty(mgr.readValue("PART_NO")) && checksec("INVENTORY_PART_COST_API.Get_Cost",1))
            {
                cmd = trans.addCustomFunction("GETCOST","INVENTORY_PART_COST_API.Get_Cost","ITEM_COST");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("PART_NO");
                cmd.addParameter("CONFIG_ID","*");
                cmd.addParameter("CONDITION_CODE");
            } */
            if (checksec("Active_Separate_API.Get_Inventory_Value",1))
            {
               cmd = trans.addCustomCommand("GETCOST","Active_Separate_API.Get_Inventory_Value");
               cmd.addParameter("ITEM_COST");
               cmd.addParameter("SPARE_CONTRACT");
               cmd.addParameter("PART_NO");
               cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
               cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
               cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));

               //trans = mgr.validate(trans);

               //nCostStr = trans.getBuffer("GETINVVAL/DATA").getFieldValue("ITEM_COST");

               //if (isNaN(nCost))
               //    nCost=0;
            }
            else
               strCost = "";
         }      

         trans = mgr.validate(trans);

         String descri = trans.getValue("CONCODE/DATA/CONDDESC");                        

         if (!mgr.isEmpty(mgr.readValue("PART_NO")) && checksec("INVENTORY_PART_COST_API.Get_Cost",1) && !"CUSTOMER OWNED".equals(mgr.readValue("PART_OWNERSHIP_DB")))
            strCost = trans.getBuffer("GETCOST/DATA").getFieldValue("ITEM_COST");
         else
            strCost = mgr.getASPField("ITEM_COST").formatNumber(nCost);
         if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",1))
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

         //Start fetching Amount Cost
         double nAmountCost = 0;
         String strAmountCost;
         double planQty = mgr.readNumberValue("PLAN_QTY");

         if (isNaN(planQty))
            planQty = 0;

         if (planQty == 0)
            strAmountCost = mgr.getASPField("AMOUNTCOST").formatNumber(nAmountCost);
         else
         {
            if (!mgr.isEmpty(mgr.readValue("PART_NO")) && checksec("INVENTORY_PART_COST_API.Get_Cost",1) && !"CUSTOMER OWNED".equals(mgr.readValue("PART_OWNERSHIP_DB")))
            {
               double nCostTemp = trans.getNumberValue("GETCOST/DATA/ITEM_COST");
               if (isNaN(nCostTemp))
                  nCostTemp = 0;
               nAmountCost = nCostTemp * planQty;
            }
            else
               nAmountCost = nCost * planQty;  

            strAmountCost = mgr.getASPField("AMOUNTCOST").formatNumber(nAmountCost);
         } 
         //End fetching Amount Cost

         txt = (mgr.isEmpty(descri) ? "" : (descri))+ "^" + 
               (mgr.isEmpty(strCost) ? "": (strCost)) + "^" +
               (mgr.isEmpty(strAmountCost) ? "": (strAmountCost)) + "^" +
               (mgr.isEmpty(qtyOnHand1) ? "": qtyOnHand1) + "^" + 
               (mgr.isEmpty(qtyAvail1) ? "": qtyAvail1) + "^"; 

         mgr.responseWrite(txt);
      }

      else if ("PART_OWNERSHIP".equals(val))
      {
         String qtyOnHand1 = ""; 
         String qtyAvail1 = "";
         double qtyOnHand = 0;
         double qtyAvail = 0;

         cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
         cmd.addParameter("PART_OWNERSHIP");

         trans = mgr.validate(trans);
         String sOwnershipDb = trans.getValue("VALIDOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");
         trans.clear();

         if ("COMPANY OWNED".equals(sOwnershipDb))
            sClientOwnershipConsignment="CONSIGNMENT";
         else
            sClientOwnershipConsignment=null;

         if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",1))
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
               cmd = trans.addCustomFunction("INVAVl","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
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
               cmd = trans.addCustomFunction("INVAVl","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
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

         }
         trans = mgr.validate(trans);
         if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",1))
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

         if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",1))
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
               cmd.addParameter("CONDITION_CODE");
            }
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

         if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",1))
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

         if ("CUSTOMER OWNED".equals(sOwnershipDb))
            sOwnerName = trans.getValue("GETOWNERNAME/DATA/OWNER_NAME");
         if ("SUPPLIER LOANED".equals(sOwnershipDb))
            sOwnerName = trans.getValue("GETOWNERNAME1/DATA/OWNER_NAME");
         String sWoCust    = trans.getValue("GETOWCUST/DATA/WO_CUST");

         txt = (mgr.isEmpty(sOwnerName) ? "" : (sOwnerName)) + "^" + (mgr.isEmpty(sWoCust) ? "": (sWoCust)) + "^"+ (mgr.isEmpty(qtyOnHand1) ? "": qtyOnHand1) + "^" +(mgr.isEmpty(qtyAvail1) ? "": qtyAvail1) + "^" ; 

         mgr.responseWrite(txt);
      }
      else if ("ORG_CODE".equals(val))
      {
         cmd = trans.addCustomFunction("GETORGDESC", "Organization_API.Get_Description", "ORGCODEDESCRIPTION");
         cmd.addParameter("CONTRACT");
         cmd.addParameter("ORG_CODE");

         trans = mgr.validate(trans);

         String sOrgDesc = trans.getValue("GETORGDESC/DATA/ORGCODEDESCRIPTION");

         txt = (mgr.isEmpty(sOrgDesc) ? "" : (sOrgDesc))+ "^";  
         mgr.responseWrite(txt);
      }
      else if ("PLAN_QTY".equals(val))
      {
         String spareId = mgr.readValue("PART_NO","");

         double nCost = mgr.readNumberValue("ITEM_COST");
         if (isNaN(nCost))
            nCost = 0;

         double nDiscount = mgr.readNumberValue("ITEM_DISCOUNT");
         if (isNaN(nDiscount))
            nDiscount = 0;

         double nListPrice = mgr.readNumberValue("LIST_PRICE");
         if (isNaN(nListPrice))
            nListPrice = 0;

         double nDiscountedPriceAmt = mgr.readNumberValue("ITEMDISCOUNTEDPRICE");
         if (isNaN(nDiscountedPriceAmt))
            nDiscountedPriceAmt = 0;

         String nPriceListNo = mgr.readValue("ITEM_PRICE_LIST_NO","");
         double nSalesPriceAmount = 0;
         String nCostStr; 
         trans.clear();
         cmd = trans.addCustomFunction("GETOBJSUPL","MAINT_MATERIAL_REQ_LINE_API.Is_Obj_Supp_Loaned","OBJ_LOAN");
         cmd.addParameter("ITEM_WO_NO");

         trans = mgr.validate(trans);

         String sObjSup = trans.getValue("GETOBJSUPL/DATA/OBJ_LOAN");

         trans.clear();

         if ( "CUSTOMER OWNED".equals(mgr.readValue("PART_OWNERSHIP_DB")) || "TRUE".equals(sObjSup) || "SUPPLIER LOANED".equals(mgr.readValue("PART_OWNERSHIP_DB")))
         {
            nCost = 0;
            nCostStr = mgr.getASPField("ITEM_COST").formatNumber(nCost);
         }
         else
         {
            if (!mgr.isEmpty(spareId))
            {
               /*   cmd = trans.addCustomFunction("GETINVVAL","INVENTORY_PART_COST_API.Get_Cost","ITEM_COST");
                  cmd.addParameter("SPARE_CONTRACT");
                  cmd.addParameter("PART_NO");
                  cmd.addParameter("CONFIG_ID","*");
                  cmd.addParameter("CONDITION_CODE");

                  trans = mgr.validate(trans);

                  nCostStr = trans.getBuffer("GETINVVAL/DATA").getFieldValue("ITEM_COST"); */

               if (checksec("Active_Separate_API.Get_Inventory_Value",1))
               {
                  cmd = trans.addCustomCommand("GETINVVAL","Active_Separate_API.Get_Inventory_Value");
                  cmd.addParameter("ITEM_COST");
                  cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));
                  cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
                  cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
                  cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
                  cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));

                  trans = mgr.validate(trans);

                  //nCost = trans.getNumberValue("GETINVVAL/DATA/ITEM_COST");
                  nCostStr = trans.getBuffer("GETINVVAL/DATA").getFieldValue("ITEM_COST");

                  //if (isNaN(nCost))
                  //    nCost=0;
               }
               else
                  nCostStr = "";
            }
            else
               nCostStr    = mgr.getASPField("ITEM_COST").formatNumber(nCost);    
         }        

         double planQty = mgr.readNumberValue("PLAN_QTY");
         if (isNaN(planQty))
            planQty = 0;

         double nAmountCost = 0;
         String nAmountCostStr;

         if (planQty == 0)
         {
            nAmountCostStr = mgr.getASPField("AMOUNTCOST").formatNumber(nAmountCost);
         }
         else
         {
            if (!mgr.isEmpty(spareId))
            {
               double nCostTemp = trans.getNumberValue("GETINVVAL/DATA/ITEM_COST");
               if (isNaN(nCostTemp))
                  nCostTemp = 0;
               if ( "CUSTOMER OWNED".equals(mgr.readValue("PART_OWNERSHIP_DB")) || "TRUE".equals(sObjSup) || "SUPPLIER LOANED".equals(mgr.readValue("PART_OWNERSHIP_DB")))
                  nCostTemp = 0;
               nAmountCost = nCostTemp * planQty;
            }
            else
               nAmountCost = nCost * planQty;  

            nAmountCostStr = mgr.getASPField("AMOUNTCOST").formatNumber(nAmountCost);
         } 

         String cataNo = mgr.readValue("ITEM_CATALOG_NO","");  
         String nListPriceStr; 
         String nDiscountStr; 
         String nSalesPriceAmountStr;
         String nDiscountedPriceStr;

         if (!mgr.isEmpty(cataNo) && planQty != 0)
         {
            trans.clear();

            cmd = trans.addCustomFunction("AGREID","Active_Work_Order_API.Get_Agreement_Id","AGREEMENT_ID");
            cmd.addParameter("ITEM_WO_NO");

            cmd = trans.addCustomFunction("CUSTONO","Active_Work_Order_API.Get_Customer_No","CUSTOMER_NO");
            cmd.addParameter("ITEM_WO_NO");

            cmd = trans.addCustomCommand("PRICEINF","Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("BASE_PRICE","0");
            cmd.addParameter("SALE_PRICE","0");
            cmd.addParameter("ITEM_DISCOUNT","0");
            cmd.addParameter("CURRENCY_RATE","0");
            cmd.addParameter("ITEM_CATALOG_CONTRACT");
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

            nListPrice = trans.getNumberValue("PRICEINF/DATA/SALE_PRICE");

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

         nListPriceStr = mgr.getASPField("LIST_PRICE").formatNumber(nListPrice);
         nSalesPriceAmountStr = mgr.getASPField("ITEMSALESPRICEAMOUNT").formatNumber(nSalesPriceAmount);
         nDiscountedPriceStr = mgr.getASPField("ITEMDISCOUNTEDPRICE").formatNumber(nDiscountedPriceAmt);
         nDiscountStr = mgr.getASPField("ITEM_DISCOUNT").formatNumber(nDiscount);

         txt = (mgr.isEmpty(nCostStr) ? "": nCostStr) + "^" + (mgr.isEmpty(nAmountCostStr) ? "": nAmountCostStr) + "^" + (mgr.isEmpty(nPriceListNo) ? "": nPriceListNo) + "^" + (mgr.isEmpty(nDiscountStr) ? "": nDiscountStr) + "^" + (mgr.isEmpty(nListPriceStr) ? "": nListPriceStr) + "^" + (mgr.isEmpty(nSalesPriceAmountStr) ? "": nSalesPriceAmountStr) + "^" + (mgr.isEmpty(nDiscountedPriceStr) ? "": nDiscountedPriceStr) + "^";

         mgr.responseWrite(txt); 
      }
      else if ("ITEM_CATALOG_NO".equals(val))
      {

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

            cmd = trans.addCustomFunction("PRILST","Customer_Order_Pricing_Api.Get_Valid_Price_List","ITEM_PRICE_LIST_NO");
            cmd.addParameter("ITEM_CATALOG_CONTRACT");
            cmd.addParameter("ITEM_CATALOG_NO");
            cmd.addReference("CUSTOMER_NO","CUSTONO/DATA");
            cmd.addReference("CURRENCEY_CODE","CURRCO/DATA");
         }
         //Bug 69392, end

         cmd = trans.addCustomCommand("PRICEINF","Work_Order_Coding_API.Get_Price_Info");
         cmd.addParameter("BASE_PRICE","0"); 
         cmd.addParameter("SALE_PRICE","0");
         cmd.addParameter("ITEM_DISCOUNT","0");
         cmd.addParameter("CURRENCY_RATE","0");
         cmd.addParameter("ITEM_CATALOG_CONTRACT");
         cmd.addParameter("ITEM_CATALOG_NO");
         cmd.addReference("CUSTOMER_NO","CUSTONO/DATA");
         cmd.addReference("AGREEMENT_ID","AGREID/DATA");
         cmd.addParameter("ITEM_PRICE_LIST_NO");
         cmd.addParameter("PLAN_QTY");

         //Bug 69392, start
         if (mgr.isModuleInstalled("ORDER"))
         {
            cmd = trans.addCustomFunction("CATADESC","Sales_Part_API.Get_Catalog_Desc","ITEMCATALOGDESC");
            cmd.addParameter("ITEM_CATALOG_CONTRACT");
            cmd.addParameter("ITEM_CATALOG_NO");

            cmd = trans.addCustomFunction("GETSALESPRICEGRPID","SALES_PART_API.GET_SALES_PRICE_GROUP_ID","SALES_PRICE_GROUP_ID");
            cmd.addParameter("ITEM_CATALOG_CONTRACT");
            cmd.addParameter("ITEM_CATALOG_NO");
         }
         //Bug 69392, end

         String spareId = mgr.readValue("PART_NO","");

         double nCost = mgr.readNumberValue("ITEM_COST");

         if (isNaN(nCost))
            nCost = 0;

         if ( "CUSTOMER OWNED".equals(mgr.readValue("PART_OWNERSHIP_DB")) || "TRUE".equals(sObjSup) || "SUPPLIER LOANED".equals(mgr.readValue("PART_OWNERSHIP_DB")))
            nCost = 0;
         else
         {
            if (!mgr.isEmpty(spareId) && checksec("Active_Separate_API.Get_Inventory_Value",4))
            {
               cmd = trans.addCustomCommand("GETINVVAL","Active_Separate_API.Get_Inventory_Value");
               cmd.addParameter("ITEM_COST");
               cmd.addParameter("SPARE_CONTRACT");
               cmd.addParameter("PART_NO");
               cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
               cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
               cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE")); 
            }
            /*if (!mgr.isEmpty(spareId))
            {
                cmd = trans.addCustomFunction("GETINVVAL","INVENTORY_PART_COST_API.Get_Cost","ITEM_COST");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("PART_NO");
                cmd.addParameter("CONFIG_ID","*");
                cmd.addParameter("CONDITION_CODE"); 

            } */
         }

         trans = mgr.validate(trans);

         //Bug 69392, start
         String nPriceListNo = "";
         if (mgr.isModuleInstalled("ORDER"))
            nPriceListNo =  trans.getValue("PRILST/DATA/PRICE_LIST_NO");
         //Bug 69392, end

         if (mgr.isEmpty(nPriceListNo))
            nPriceListNo = trans.getBuffer("PRICEINF/DATA").getValue("ITEM_PRICE_LIST_NO");

         double nDiscount = trans.getNumberValue("PRICEINF/DATA/DISCOUNT");
         if (isNaN(nDiscount))
            nDiscount = 0;

         double nListPrice = 0;

         double planQty = mgr.readNumberValue("PLAN_QTY");
         if (isNaN(planQty))
            planQty = 0;

         double nSalesPriceAmount = 0;
         double nDiscountedPriceAmt = 0;

         nListPrice = trans.getNumberValue("PRICEINF/DATA/SALE_PRICE");
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

         String nCostStr;

         if ( !mgr.isEmpty(spareId))
         {

            if ( "CUSTOMER OWNED".equals(mgr.readValue("PART_OWNERSHIP_DB")) || "TRUE".equals(sObjSup) || "SUPPLIER LOANED".equals(mgr.readValue("PART_OWNERSHIP_DB")))
            {
               nCost = 0;
               nCostStr = mgr.getASPField("ITEM_COST").formatNumber(nCost);
            }
            else
               nCostStr = trans.getBuffer("GETINVVAL/DATA").getFieldValue("ITEM_COST");
         }
         else
            nCostStr = mgr.getASPField("ITEM_COST").formatNumber(nCost);


         double nCostAmt = 0;
         String nCostAmtStr = "";

         if (planQty == 0)
         {
            nCostAmtStr = mgr.getASPField("AMOUNTCOST").formatNumber(nCostAmt);
         }
         else
         {
            double nCostTemp = mgr.readNumberValue("ITEM_COST");
            if (isNaN(nCostTemp))
               nCostTemp =0;

            if ( "CUSTOMER OWNED".equals(mgr.readValue("PART_OWNERSHIP_DB")) || "TRUE".equals(sObjSup) || "SUPPLIER LOANED".equals(mgr.readValue("PART_OWNERSHIP_DB")))
            {
               nCostTemp =0;
            }
            else
            {
               if (!mgr.isEmpty(spareId))
               {
                  nCostTemp = trans.getNumberValue("GETINVVAL/DATA/ITEM_COST");
                  if (isNaN(nCostTemp))
                     nCostTemp = 0;
               }
            }

            nCostAmt = nCostTemp * planQty; 

            nCostAmtStr = mgr.getASPField("AMOUNTCOST").formatNumber(nCostAmt);
         }

         //Bug 69392, start
         String cataDesc = "";
         String salesPriceGroupId = "";
         if (mgr.isModuleInstalled("ORDER"))
         {
            cataDesc = trans.getValue("CATADESC/DATA/ITEMCATALOGDESC");
            salesPriceGroupId = trans.getValue("GETSALESPRICEGRPID/DATA/SALES_PRICE_GROUP_ID");
         }
         //Bug 69392, end
         String nListPriceStr = mgr.formatNumber("LIST_PRICE",nListPrice);
         String nDiscountStr = mgr.getASPField("ITEM_DISCOUNT").formatNumber(nDiscount);     
         String nSalesPriceAmountStr = mgr.getASPField("ITEMSALESPRICEAMOUNT").formatNumber(nSalesPriceAmount);
         String nDiscountedPriceAmtStr = mgr.getASPField("ITEMDISCOUNTEDPRICE").formatNumber(nDiscountedPriceAmt);


         txt = (mgr.isEmpty(nListPriceStr) ? "": nListPriceStr) + "^" + (mgr.isEmpty(nCostStr) ? "": nCostStr) + "^" + (mgr.isEmpty(nCostAmtStr) ? "": nCostAmtStr) + "^" + (mgr.isEmpty(cataDesc) ? "": cataDesc) + "^" + (mgr.isEmpty(nDiscountStr) ? "": nDiscountStr) + "^" + (mgr.isEmpty(nSalesPriceAmountStr) ? "": nSalesPriceAmountStr) + "^" +(mgr.isEmpty(nDiscountedPriceAmtStr) ? "": nDiscountedPriceAmtStr) + "^"+(mgr.isEmpty(nPriceListNo) ? "": nPriceListNo) + "^" + (mgr.isEmpty(salesPriceGroupId)?"":salesPriceGroupId) + "^";

         mgr.responseWrite(txt);
      }
      else if ("ITEM_PRICE_LIST_NO".equals(val))
      {
         String partNo = mgr.readValue("PART_NO","");
         String nPriceListNo = mgr.readValue("ITEM_PRICE_LIST_NO","");

         String nCostStr = "";
         double nCostAmt = 0;
         String nCostAmtStr = "";
         double nListPrice = 0;
         double nSalesPriceAmount = 0;
         double nDiscountedPriceAmt = 0;
         double nDiscount = 0;

         if ("CUSTOMER OWNED".equals(mgr.readValue("PART_OWNERSHIP_DB")))
         {
            double nCost = 0;                            
            nCostStr = mgr.getASPField("ITEM_COST").formatNumber(nCost);
         }
         else
         {
            if (!mgr.isEmpty(partNo))
            {
               /*cmd = trans.addCustomFunction("GETINVVAL","INVENTORY_PART_COST_API.Get_Cost","ITEM_COST");
               cmd.addParameter("SPARE_CONTRACT");
               cmd.addParameter("PART_NO");
               cmd.addParameter("CONFIG_ID","*");
               cmd.addParameter("CONDITION_CODE");

               trans = mgr.validate(trans);

               nCostStr = trans.getBuffer("GETINVVAL/DATA").getFieldValue("ITEM_COST"); */
               if (checksec("Active_Separate_API.Get_Inventory_Value",1))
               {
                  cmd = trans.addCustomCommand("GETINVVAL","Active_Separate_API.Get_Inventory_Value");
                  cmd.addParameter("ITEM_COST");
                  cmd.addParameter("SPARE_CONTRACT");
                  cmd.addParameter("PART_NO");
                  cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
                  cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
                  cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));

                  trans = mgr.validate(trans);

                  //nCost = trans.getNumberValue("GETINVVAL/DATA/ITEM_COST");
                  nCostStr = trans.getBuffer("GETINVVAL/DATA").getFieldValue("ITEM_COST");

                  //if (isNaN(nCost))
                  //    nCost=0;
               }
               else
                  nCostStr    = "";
            }
         }

         double planQty = mgr.readNumberValue("PLAN_QTY");
         if (isNaN(planQty))
            planQty = 0;

         if (planQty == 0 || "CUSTOMER OWNED".equals(mgr.readValue("PART_OWNERSHIP_DB")))
            nCostAmtStr = mgr.getASPField("AMOUNTCOST").formatNumber(nCostAmt);
         else
         {
            double nCostTemp = trans.getNumberValue("GETINVAL/DATA/ITEM_COST");
            if (isNaN(nCostTemp))
               nCostTemp = 0;

            nCostAmt = nCostTemp * planQty;
            nCostAmtStr = mgr.getASPField("AMOUNTCOST").formatNumber(nCostAmt);
         }

         String cataNo = mgr.readValue("ITEM_CATALOG_NO",""); 

         if (!mgr.isEmpty(cataNo) && planQty != 0)
         {
            trans.clear();

            cmd = trans.addCustomFunction("AGREID","Active_Work_Order_API.Get_Agreement_Id","AGREEMENT_ID");
            cmd.addParameter("ITEM_WO_NO");

            cmd = trans.addCustomFunction("CUSTONO","Active_Work_Order_API.Get_Customer_No","CUSTOMER_NO");
            cmd.addParameter("ITEM_WO_NO");

            cmd = trans.addCustomCommand("PRICEINF","Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("BASE_PRICE","0"); 
            cmd.addParameter("SALE_PRICE","0");
            cmd.addParameter("ITEM_DISCOUNT","0");
            cmd.addParameter("CURRENCY_RATE","0");
            cmd.addParameter("ITEM_CATALOG_CONTRACT");
            cmd.addParameter("ITEM_CATALOG_NO");
            cmd.addReference("CUSTOMER_NO","CUSTONO/DATA");
            cmd.addReference("AGREEMENT_ID","AGREID/DATA");
            cmd.addParameter("ITEM_PRICE_LIST_NO");
            cmd.addParameter("PLAN_QTY");

            trans = mgr.validate(trans);        

            if (mgr.isEmpty(nPriceListNo))
               nPriceListNo = trans.getBuffer("PRICEINF/DATA").getValue("ITEM_PRICE_LIST_NO");

            nDiscount = trans.getNumberValue("PRICEINF/DATA/DISCOUNT");;
            if (isNaN(nDiscount))
               nDiscount = 0;

            if (nListPrice == 0)
            {
               nListPrice = trans.getNumberValue("PRICEINF/DATA/SALE_PRICE");
               if (isNaN(nListPrice))
                  nListPrice =0;

               nSalesPriceAmount  = (nListPrice - (nDiscount / 100 * nListPrice)) * planQty;
               nDiscountedPriceAmt = (nListPrice - (nDiscount / 100 * nListPrice));
            }
            else
            {
               if (nDiscount == 0)
                  nSalesPriceAmount = nListPrice * planQty;
               else
               {
                  nSalesPriceAmount = nListPrice * planQty;
                  nSalesPriceAmount = nSalesPriceAmount - (nDiscount / 100 * nSalesPriceAmount);
                  nDiscountedPriceAmt = (nListPrice - (nDiscount / 100 * nListPrice));
               }
            }


         }

         String nListPriceStr = mgr.formatNumber("LIST_PRICE",nListPrice);
         String nDiscountStr = mgr.getASPField("ITEM_DISCOUNT").formatNumber(nDiscount);     
         String nSalesPriceAmountStr = mgr.getASPField("ITEMSALESPRICEAMOUNT").formatNumber(nSalesPriceAmount);
         String nDiscountedPriceAmtStr = mgr.getASPField("ITEMDISCOUNTEDPRICE").formatNumber(nDiscountedPriceAmt);

         txt =  (mgr.isEmpty(nCostStr) ? "": nCostStr) + "^" + (mgr.isEmpty(nCostAmtStr) ? "": nCostAmtStr) + "^" + (mgr.isEmpty(nListPriceStr) ? "": nListPriceStr) + "^"+ (mgr.isEmpty(nDiscountStr) ? "": nDiscountStr) + "^" + (mgr.isEmpty(nSalesPriceAmountStr) ? "": nSalesPriceAmountStr) + "^"+(mgr.isEmpty(nDiscountedPriceAmtStr) ? "": nDiscountedPriceAmtStr) + "^";

         mgr.responseWrite(txt);
      }
      else if ("AGREEMENT_ID".equals(val))
      {
         trans.clear();

         trans = mgr.validate(trans);
         String sUpdatePrice = "0";

         txt = (mgr.isEmpty(sUpdatePrice) ? "": sUpdatePrice)+ "^";
         mgr.responseWrite(txt);
      }
      else if ("DATE_FROM".equals(val))
      {
         buf = mgr.newASPBuffer();
         buf.addFieldItem("DATE_FROM",mgr.readValue("DATE_FROM",""));      
         DateFormat df= DateFormat.getInstance();
         Date dt = null;
         try
         {
            dt = df.parse(mgr.readValue("DATE_FROM",""));
         }
         catch (ParseException e)
         {
            e.printStackTrace();
         }
         buf.addFieldDateItem("DATE_FROM_MASKED", dt);
         txt = mgr.readValue("DATE_FROM","") + "^"
               +buf.getFieldValue("DATE_FROM_MASKED") + "^";
         mgr.responseWrite(txt);
      }
      else if ("DATE_TO".equals(val))
      {
         buf = mgr.newASPBuffer();
         buf.addFieldItem("DATE_TO",mgr.readValue("DATE_TO",""));    
         mgr.responseWrite(mgr.readValue("DATE_TO",""));
      }
      else if ("DATE_REQUIRED".equals(val))
      {
         buf = mgr.newASPBuffer();
         buf.addFieldItem("DATE_REQUIRED",mgr.readValue("DATE_REQUIRED",""));    
         mgr.responseWrite(mgr.readValue("DATE_REQUIRED",""));
      }
      //-------------------------------------------------------------------------------
      //------------ This validation part modified. Refer bug id #70349 ---------------
      //-------------------------------------------------------------------------------
      else

         if ("PLAN_S_DATE".equals(val))
      {
         buf = mgr.newASPBuffer();
         buf.addFieldItem("PLAN_S_DATE",mgr.readValue("PLAN_S_DATE",""));                                                                                                            
         trans = mgr.perform(trans);

         trans.clear();

         cmd = trans.addCustomFunction("PLANFDATE","active_separate_api.Get_New_Comp_Date","PLAN_F_DATE");
         cmd.addParameter("WO_NO",mgr.readValue("WO_NO",""));
         cmd.addParameter("PLAN_S_DATE",mgr.readValue("PLAN_S_DATE",""));

         trans = mgr.validate(trans);

         String planFdate = trans.getBuffer("PLANFDATE/DATA").getFieldValue("PLAN_F_DATE");

         txt = (mgr.isEmpty(planFdate) ? "": planFdate) ;
         mgr.responseWrite(txt);
      }

      else if ("PLAN_HRS".equals(val))
      {

         String opExists = mgr.readValue("OP_EXIST");
         String manHours,allocHours;

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
         String sTempTxt = "";

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


         if ("TRUE".equals(opExists))
         {
            cmd = trans.addCustomFunction("MANHOURS","Work_Order_ROLE_API.Get_Total_Planned_Hours","MAN_HOURS");
            cmd.addParameter("WO_NO",mgr.readValue("WO_NO"));

            cmd = trans.addCustomFunction("ALLOCHOURS","Work_Order_ROLE_API.Get_Total_Allocated_Hours","ALLOC_HOURS");
            cmd.addParameter("WO_NO",mgr.readValue("WO_NO"));

            trans = mgr.validate(trans);
            manHours = trans.getValue("MANHOURS/DATA/MAN_HOURS"); 
            allocHours = trans.getValue("ALLOCHOURS/DATA/ALLOC_HOURS"); 
         }
         else
         {
            manHours   = mgr.readValue("PLAN_HRS");
            allocHours = mgr.readValue("PLAN_HRS");
         }


         // Calculate the planned completion date
         if ( !mgr.isEmpty(mgr.readValue("PLAN_HRS")) && !mgr.isEmpty(mgr.readValue("PLAN_S_DATE")))
            sTempTxt = calEndDatesAccPlanHrs(newBuff);  //Bug Id 70921, use calendar
         else
            sTempTxt = sPlanFDate + "^" + sReqEndDate + "^"; 

         txt= (mgr.isEmpty(manHours) ? "" : (manHours))+ "^" +
              (mgr.isEmpty(allocHours) ? "" : (allocHours))+ "^" +
              sTempTxt ;

         mgr.responseWrite(txt);
      }
      else if ("REG_DATE".equals(val))
      {
         buf = mgr.newASPBuffer();
         buf.addFieldItem("REG_DATE",mgr.readValue("REG_DATE",""));

         txt = mgr.readValue("REG_DATE","") +"^";
         mgr.responseWrite(txt);     
      }
      else if ("REQUIRED_START_DATE".equals(val))
      {
         buf = mgr.newASPBuffer();
         buf.addFieldItem("REQUIRED_START_DATE",mgr.readValue("REQUIRED_START_DATE",""));     
         txt = mgr.readValue("REQUIRED_START_DATE","");
         mgr.responseWrite(txt);
      }
      else if ("REQUIRED_END_DATE".equals(val))
      {
         buf = mgr.newASPBuffer();
         buf.addFieldItem("REQUIRED_END_DATE",mgr.readValue("REQUIRED_END_DATE",""));      
         txt = mgr.readValue("REQUIRED_END_DATE","");
         mgr.responseWrite(txt);
      }
      else if ("ITEM5_WORK_ORDER_COST_TYPE".equals(val))
      {
         cmd = trans.addCustomFunction("CL1","Work_Order_Cost_Type_Api.Get_Client_Value(1)","CLIENTVAL1");
         cmd = trans.addCustomFunction("CL0","Work_Order_Cost_Type_Api.Get_Client_Value(0)","CLIENTVAL0");
         cmd = trans.addCustomFunction("CL4","Work_Order_Cost_Type_Api.Get_Client_Value(4)","CLIENTVAL4");

         trans = mgr.validate(trans);

         String strWorkOrder = mgr.readValue("ITEM5_WORK_ORDER_COST_TYPE","");
         String clientVal5 = mgr.readValue("CLIENTVAL5","");
         String strMaterial = trans.getValue("CL1/DATA/CLIENTVAL1");
         String strPersonnel = trans.getValue("CL0/DATA/CLIENTVAL0");
         String strFixedPrice = trans.getValue("CL4/DATA/CLIENTVAL4");
         String woInvType = mgr.readValue("WORK_ORDER_INVOICE_TYPE","");
         double nAmount; 
         String nAmountStr = "";

         if (strFixedPrice.equals(strWorkOrder))
         {
            trans.clear();
            cmd = trans.addCustomFunction("INVTYPE","Work_Order_Invoice_Type_Api.Get_Client_Value(0)","WORK_ORDER_INVOICE_TYPE");    

            cmd = trans.addCustomFunction("WOINVENC", "Work_Order_Invoice_Type_API.Encode","CLIENTVAL5");
            cmd.addReference("WORK_ORDER_INVOICE_TYPE","INVTYPE/DATA");

            trans = mgr.validate(trans);

            woInvType = trans.getValue("INVTYPE/DATA/WORK_ORDER_INVOICE_TYPE");
            clientVal5 = trans.getValue("WOINVENC/DATA/CLIENTVAL5");
            nAmount = 0;
         }
         else
         {
            trans.clear();

            cmd = trans.addCustomFunction("INVTYPE","Work_Order_Invoice_Type_Api.Get_Client_Value(1)","WORK_ORDER_INVOICE_TYPE");

            trans = mgr.validate(trans);

            woInvType = trans.getValue("INVTYPE/DATA/WORK_ORDER_INVOICE_TYPE");

            nAmount = mgr.readNumberValue("ITEM5COST"); 
            if (isNaN(nAmount))
               nAmount = 0;
         }     

         if (nAmount != 0)
            nAmountStr = mgr.getASPField("ITEM5COST").formatNumber(nAmount);

         txt = nAmountStr + "^" +  (mgr.isEmpty(strMaterial) ? "" : (strMaterial))+ "^" +  (mgr.isEmpty(strPersonnel) ? "" : (strPersonnel))+ "^"
               +  (mgr.isEmpty(strFixedPrice ) ? "" : (strFixedPrice ))+ "^" +  (mgr.isEmpty(woInvType) ? "" : (woInvType))+ "^" + (mgr.isEmpty(clientVal5) ? "" : (clientVal5))+ "^";

         mgr.responseWrite(txt);
      }
      else if ("ITEM5_CATALOG_NO".equals(val))
      {

         //Bug 69392, start
         if (mgr.isModuleInstalled("ORDER"))
         {
            cmd = trans.addCustomFunction("VALPRICELIST","Customer_Order_Pricing_Api.Get_Valid_Price_List","ITEM5_PRICE_LIST_NO");
            cmd.addParameter("ITEM5_CATALOG_CONTRACT");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addParameter("CUSTOMER_NO");
            cmd.addParameter("HEAD_CURRENCEY_CODE");

            cmd = trans.addCustomFunction("SALEPARTCOS","Sales_Part_API.Get_Cost","ITEM5COST");
            cmd.addParameter("ITEM5_CATALOG_CONTRACT");
            cmd.addParameter("ITEM5_CATALOG_NO");
         }
         //Bug 69392, end

         if (!mgr.isEmpty(mgr.readValue("ORG_CODE")))
         {
            cmd = trans.addCustomFunction("ORGACOST","Organization_API.Get_Org_Cost","ITEM5COST");
            cmd.addParameter("CONTRACT");
            cmd.addParameter("ORG_CODE");
         }

         double quantityVar = mgr.readNumberValue("QUANTITY");
         if (isNaN(quantityVar))
            quantityVar = 0;

         double qtyToInvVar = mgr.readNumberValue("QTY_TO_INVOICE");
         if (isNaN(qtyToInvVar))
            qtyToInvVar = 0;

         double nQty = 0;
         if (qtyToInvVar != 0)
            nQty = qtyToInvVar;
         else
            nQty = quantityVar;

         double nsalesPrice = 0;
         double discountVar = 0;
         double discountedPrice = 0;
         double slsPriceAmt = 0;

         if (!mgr.isEmpty(mgr.readValue("ITEM5_CATALOG_NO")) && (quantityVar != 0))
         {
            cmd = trans.addCustomCommand("INFO5", "Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("ITEM5_BASE_UNIT_PRICE","0");
            cmd.addParameter("ITEM5_SALE_UNIT_PRICE","0");
            cmd.addParameter("ITEM5_DISCOUNT","0");
            cmd.addParameter("ITEM5_CURRENCY_RATE","0");
            cmd.addParameter("ITEM5_CATALOG_CONTRACT",mgr.readValue("ITEM5_CATALOG_CONTRACT",""));
            cmd.addParameter("ITEM5_CATALOG_NO",mgr.readValue("ITEM5_CATALOG_NO",""));
            cmd.addParameter("CUSTOMER_NO",mgr.readValue("CUSTOMER_NO",""));
            cmd.addParameter("AGREEMENT_ID",mgr.readValue("AGREEMENT_ID",""));
            cmd.addReference("ITEM5_PRICE_LIST_NO","VALPRICELIST/DATA");
            cmd.addParameter("QUANTITY",mgr.formatNumber("QUANTITY",nQty));
         }

         cmd = trans.addCustomFunction("WOINV", "Work_Order_Invoice_Type_API.Encode","WOINVOICETYPE");
         cmd.addParameter("WORK_ORDER_INVOICE_TYPE");

         if (checksec("Sales_Part_API.Get_Catalog_Desc",1))
         {
            cmd = trans.addCustomFunction("CATDESC5","Sales_Part_API.Get_Catalog_Desc","ITEM5_CATALOGDESC");
            cmd.addParameter("ITEM5_CATALOG_CONTRACT");
            cmd.addParameter("ITEM5_CATALOG_NO");
         }

         trans = mgr.validate(trans);

         //Bug 69392, start
         String priceListNo = "";
         double nCost  = 0;
         if (mgr.isModuleInstalled("ORDER"))
         {
            priceListNo = trans.getBuffer("VALPRICELIST/DATA").getFieldValue("ITEM5_PRICE_LIST_NO");

            nCost = trans.getNumberValue("SALEPARTCOS/DATA/COST");
         }
         //Bug 69392, end
         if (isNaN(nCost))
            nCost = 0;

         if (nCost == 0 && !mgr.isEmpty(mgr.readValue("ORG_CODE")))
            nCost = trans.getNumberValue("ORGACOST/DATA/COST");

         if (!mgr.isEmpty(mgr.readValue("ITEM5_CATALOG_NO")) && (quantityVar != 0))
         {
            if (mgr.isEmpty(priceListNo))
               priceListNo = trans.getBuffer("INFO5/DATA").getFieldValue("ITEM5_PRICE_LIST_NO");

            nsalesPrice = trans.getNumberValue("INFO5/DATA/ITEM5_SALE_UNIT_PRICE");    
            if (isNaN(nsalesPrice))
               nsalesPrice = 0;

            discountVar = trans.getNumberValue("INFO5/DATA/DISCOUNT");                   
            if (isNaN(discountVar))
               discountVar = 0;

            discountedPrice = (nsalesPrice - (discountVar / 100 * nsalesPrice));
            slsPriceAmt = discountedPrice * nQty;
         }
         double costAmount;

         if (nCost > 0)
            costAmount = nCost * quantityVar;
         else
            costAmount = 0;    

         String woInvTypeVar = trans.getValue("WOINV/DATA/WOINVOICETYPE");


         double nVal;
         double deduction;

         if ("AR".equals(woInvTypeVar))
         {
            if (discountVar == 0)
               slsPriceAmt = nsalesPrice * quantityVar;
            else
            {
               nVal = nsalesPrice * quantityVar;
               deduction = discountVar / 100 * nVal; 
               slsPriceAmt = nVal - deduction;
            }
         }
         else if ("MINQ".equals(woInvTypeVar))
         {
            if (quantityVar > qtyToInvVar)
            {
               if (discountVar == 0)
                  slsPriceAmt = nsalesPrice * quantityVar;
               else
               {
                  nVal = nsalesPrice * quantityVar;
                  deduction = discountVar / 100 * nVal; 
                  slsPriceAmt = nVal - deduction;
               }
            }
            else
            {
               if (discountVar == 0)
                  slsPriceAmt = nsalesPrice * qtyToInvVar;
               else
               {
                  nVal = nsalesPrice * qtyToInvVar;
                  deduction = discountVar / 100 * nVal; 
                  slsPriceAmt = nVal - deduction;
               }      
            }    
         }
         else if ("MAXQ".equals(woInvTypeVar))
         {
            if (quantityVar < qtyToInvVar)
            {
               if (discountVar == 0)
                  slsPriceAmt = nsalesPrice * quantityVar;
               else
               {
                  nVal = nsalesPrice * quantityVar;
                  deduction = discountVar / 100 * nVal; 
                  slsPriceAmt = nVal - deduction;
               }
            }
            else
            {
               if (discountVar == 0)
                  slsPriceAmt = nsalesPrice * qtyToInvVar;
               else
               {
                  nVal = nsalesPrice * qtyToInvVar;
                  deduction = discountVar / 100 * nVal; 
                  slsPriceAmt = nVal - deduction;
               }             
            }    
         }
         else if (( "FL".equals(woInvTypeVar) ) ||  ( mgr.isEmpty(woInvTypeVar) ))
         {
            if (discountVar == 0)
               slsPriceAmt = nsalesPrice * qtyToInvVar;
            else
            {
               nVal = nsalesPrice * qtyToInvVar;
               deduction = discountVar / 100 * nVal; 
               slsPriceAmt = nVal - deduction;      
            }  
         }

         String cataloDesc = trans.getValue("CATDESC5/DATA/ITEM5_CATALOGDESC");
         String lineDesc = cataloDesc;
         String costAmtVar = mgr.formatNumber("ITEM5COST",nCost);
         String costAmountStr = mgr.formatNumber("ITEM5_COST_AMOUNT",costAmount);
         String salesPrice = mgr.formatNumber("ITEM5_SALES_PRICE",nsalesPrice);
         String discount = mgr.formatNumber("ITEM5_DISCOUNT",discountVar);
         String slsPriceAmtStr = mgr.formatNumber("ITEM5_SALES_PRICE_AMOUNT",slsPriceAmt);


         txt =(mgr.isEmpty(costAmtVar) ? "": (costAmtVar))+ "^" +(mgr.isEmpty(cataloDesc) ? "": (cataloDesc))+ "^" +(mgr.isEmpty(lineDesc) ? "": (lineDesc))+ "^" + costAmountStr + "^" +(mgr.isEmpty(priceListNo) ? "": (priceListNo))+ "^" +(mgr.isEmpty(salesPrice) ? "": (salesPrice))+ "^" +(mgr.isEmpty(discount) ? "": (discount))+ "^" + slsPriceAmtStr + "^"; 
         mgr.responseWrite(txt);
      }
      else if ("ITEM_DISCOUNT".equals(val))
      {
         double nListPrice = mgr.readNumberValue("LIST_PRICE");
         if (isNaN(nListPrice))
            nListPrice = 0;

         double planQty = mgr.readNumberValue("PLAN_QTY");
         if (isNaN(planQty))
            planQty = 0;

         double nDiscount = mgr.readNumberValue("ITEM_DISCOUNT");
         if (isNaN(nDiscount))
            nDiscount = 0;

         double discountedPrice = 0;

         double salePriceAmt =  nListPrice * planQty;
         salePriceAmt =  salePriceAmt - (nDiscount / 100 * salePriceAmt);
         discountedPrice = nListPrice - (nDiscount/100*nListPrice); 

         String salePriceAmtStr = mgr.getASPField("ITEMSALESPRICEAMOUNT").formatNumber(salePriceAmt);
         String strDiscountedPriceAmt = mgr.getASPField("ITEMDISCOUNTEDPRICE").formatNumber(discountedPrice);

         txt = (mgr.isEmpty(salePriceAmtStr) ? "": salePriceAmtStr) + "^" + (mgr.isEmpty(strDiscountedPriceAmt) ? "": strDiscountedPriceAmt) + "^";

         mgr.responseWrite(txt);
      }
      else if ("QUANTITY".equals(val))
      {   //Bug 69392, start
         if (mgr.isModuleInstalled("ORDER"))
         {
            cmd = trans.addCustomFunction("SALEPARTCOS","Sales_Part_API.Get_Cost","ITEM5COST");
            cmd.addParameter("ITEM5_CATALOG_CONTRACT");
            cmd.addParameter("ITEM5_CATALOG_NO");
         }
         //Bug 69392, end
         if (!mgr.isEmpty(mgr.readValue("ORG_CODE")))
         {
            cmd = trans.addCustomFunction("ORGACOST","Organization_API.Get_Org_Cost","ITEM5COST");
            cmd.addParameter("CONTRACT");
            cmd.addParameter("ORG_CODE");
         }

         trans = mgr.validate(trans);

         //Bug 69392, start
         double nCost  = 0;
         if (mgr.isModuleInstalled("ORDER"))
            nCost = trans.getNumberValue("SALEPARTCOS/DATA/COST");
         //Bug 69392, end

         if (isNaN(nCost))
            nCost = 0;

         if (nCost == 0 && !mgr.isEmpty(mgr.readValue("ORG_CODE")))
            nCost = trans.getNumberValue("ORGACOST/DATA/COST");


         double nQuantity = mgr.readNumberValue("QUANTITY");
         if (isNaN(nQuantity))
            nQuantity = 0;

         String salesPriceAmtStr = countSalesPriceAmount();

         double nCostAmt;

         if (nCost > 0)
            nCostAmt = nCost *  nQuantity;
         else
            nCostAmt    = 0;

         String nCostAmtStr = mgr.getASPField("ITEM5_COST_AMOUNT").formatNumber(nCostAmt);
         String costAmtVar = mgr.formatNumber("ITEM5COST",nCost);

         txt = (mgr.isEmpty(salesPriceAmtStr) ? "": salesPriceAmtStr) + "^" + (mgr.isEmpty(nCostAmtStr) ? "": nCostAmtStr) + "^" + (mgr.isEmpty(costAmtVar) ? "": costAmtVar) + "^";   

         mgr.responseWrite(txt);  
      }
      else if ("QTY_TO_INVOICE".equals(val))
      {
         String catalogNo = mgr.readValue("ITEM5_CATALOG_NO","");
         String colPriceListNo = mgr.readValue("ITEM5_PRICE_LIST_NO",""); 

         double nDiscount = mgr.readNumberValue("ITEM5_DISCOUNT");
         if (isNaN(nDiscount))
            nDiscount = 0;

         double nSalesPrice = mgr.readNumberValue("ITEM5_SALES_PRICE");
         if (isNaN(nSalesPrice))
            nSalesPrice = 0;

         double nQuantity = mgr.readNumberValue("QUANTITY");
         if (isNaN(nQuantity))
            nQuantity = 0;

         double nQuantityToInv = mgr.readNumberValue("QTY_TO_INVOICE");
         if (isNaN(nQuantityToInv))
            nQuantityToInv = 0;

         double nSalesPriceNew = 0;

         double nQty = 0;
         if (nQuantityToInv != 0)
            nQty = nQuantityToInv;
         else
            nQty = nQuantity;

         if (! mgr.isEmpty(catalogNo)  &&   nQuantityToInv != 0)
         {
            cmd = trans.addCustomCommand("GETPRICEINFO","Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("ITEM5_BASE_UNIT_PRICE","0");
            cmd.addParameter("ITEM5_SALE_UNIT_PRICE","0");
            cmd.addParameter("ITEM5_DISCOUNT","0");
            cmd.addParameter("ITEM5_CURRENCY_RATE","0");
            cmd.addParameter("ITEM5_CATALOG_CONTRACT");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addParameter("CUSTOMER_NO");
            cmd.addParameter("AGREEMENT_ID");
            cmd.addParameter("ITEM5_PRICE_LIST_NO");
            cmd.addParameter("QUANTITY",mgr.formatNumber("QUANTITY",nQty));
         }

         //Bug 69392, start
         if (mgr.isModuleInstalled("ORDER"))
         {
            cmd = trans.addCustomFunction("SALEPARTCOS","Sales_Part_API.Get_Cost","ITEM5COST");
            cmd.addParameter("ITEM5_CATALOG_CONTRACT");
            cmd.addParameter("ITEM5_CATALOG_NO");
         }
         //Bug 69392, end

         if (!mgr.isEmpty(mgr.readValue("ORG_CODE")))
         {
            cmd = trans.addCustomFunction("ORGACOST","Organization_API.Get_Org_Cost","ITEM5COST");
            cmd.addParameter("CONTRACT");
            cmd.addParameter("ORG_CODE");
         }

         trans = mgr.validate(trans);

         //Bug 69392, start
         double nCost = 0 ;
         if (mgr.isModuleInstalled("ORDER"))
            nCost = trans.getNumberValue("SALEPARTCOS/DATA/COST");
         //Bug 69392, end

         if (isNaN(nCost))
            nCost = 0;

         if (nCost == 0 && !mgr.isEmpty(mgr.readValue("ORG_CODE")))
            nCost = trans.getNumberValue("ORGACOST/DATA/COST");

         if (! mgr.isEmpty(catalogNo)  &&   nQuantityToInv != 0)
         {
            if (mgr.isEmpty(colPriceListNo))
               colPriceListNo = trans.getBuffer("GETPRICEINFO/DATA").getFieldValue("ITEM5_PRICE_LIST_NO");

            nSalesPriceNew  = trans.getNumberValue("GETPRICEINFO/DATA/ITEM5_SALE_UNIT_PRICE");
            if (isNaN(nSalesPriceNew))
               nSalesPriceNew = 0;

            nDiscount = trans.getNumberValue("GETPRICEINFO/DATA/DISCOUNT");
            if (isNaN(nDiscount))
               nDiscount = 0;
         }

         String salesPrice = mgr.getASPField("ITEM5_SALES_PRICE").formatNumber(nSalesPriceNew);
         String discount   = mgr.getASPField("ITEM5_DISCOUNT").formatNumber(nDiscount);

         trans.clear();     

         String salesPriceAmtStr = countSalesPriceAmount();
         String costAmtVar = mgr.formatNumber("ITEM5COST",nCost);

         double nCostAmt;

         if (nCost > 0)
            nCostAmt = nCost *  nQuantity;
         else
            nCostAmt    = 0;

         String nCostAmtStr = mgr.getASPField("ITEM5_COST_AMOUNT").formatNumber(nCostAmt);

         txt = (mgr.isEmpty(colPriceListNo) ? "": colPriceListNo) + "^" + salesPrice + "^" + salesPriceAmtStr + "^" +  discount + "^"; 

         mgr.responseWrite(txt);
      }
      else if ("ITEM5COST".equals(val))
      {
         double nQuantity = mgr.readNumberValue("QUANTITY");
         if (isNaN(nQuantity))
            nQuantity = 0;

         double nCost = mgr.readNumberValue("ITEM5COST");
         if (isNaN(nCost))
            nCost =0 ;

         String salesPriceAmtStr = countSalesPriceAmount(); 
         double nCostAmt;

         if (nCost > 0)
            nCostAmt = nCost *  nQuantity;
         else
            nCostAmt    = 0; 

         String nCostAmtStr = mgr.getASPField("ITEM5COST").formatNumber(nCostAmt);

         txt = salesPriceAmtStr + "^" + nCostAmtStr + "^"; 

         mgr.responseWrite(txt);
      }
      else if ("ITEM5_SALES_PRICE".equals(val) ||   "ITEM5_DISCOUNT".equals(val))
      {
         String salesPriceAmtStr = countSalesPriceAmount(); 

         txt = (mgr.isEmpty(salesPriceAmtStr) ? "": salesPriceAmtStr) + "^";

         mgr.responseWrite(txt);
      }
      else if ("WORK_ORDER_INVOICE_TYPE".equals(val))
      {
         cmd = trans.addCustomFunction("WOINVENC", "Work_Order_Invoice_Type_API.Encode","CLIENTVAL5");
         cmd.addParameter("WORK_ORDER_INVOICE_TYPE");

         trans = mgr.validate(trans);
         String clientVal5 = trans.getValue("WOINVENC/DATA/CLIENTVAL5");

         trans.clear();

         String salesPriceAmtStr = countSalesPriceAmount(); 

         txt = (mgr.isEmpty(clientVal5) ? "" : (clientVal5))+ "^" + salesPriceAmtStr + "^";

         mgr.responseWrite(txt);    
      }
      else if ("OBJ_SUP_WARRANTY".equals(val))
      {
         cmd  = trans.addCustomFunction("SUPP","Object_Supplier_Warranty_API.Get_Warranty_Id","SUP_WARRANTY");
         cmd.addParameter("MCH_CODE_CONTRACT");
         cmd.addParameter("MCH_CODE");
         cmd.addParameter("OBJ_SUP_WARRANTY");

         cmd  = trans.addCustomFunction("SUPPLIER","OBJECT_SUPPLIER_WARRANTY_API.Get_Vendor_No","SUPPLIER");
         cmd.addParameter("MCH_CODE_CONTRACT");
         cmd.addParameter("MCH_CODE");
         cmd.addParameter("OBJ_SUP_WARRANTY");

         cmd  = trans.addCustomFunction("SUPPDESC","Maintenance_Supplier_API.Get_Description","SUPP_DESCR");
         cmd.addReference("SUP_WARRANTY","SUPPLIER/DATA");

         trans = mgr.validate(trans);

         String suppl = trans.getValue("SUPP/DATA/SUP_WARRANTY");
         String supplier = trans.getValue("SUPPLIER/DATA/SUPPLIER");
         String suppdesc = trans.getValue("SUPPDESC/DATA/SUPP_DESCR");

         txt = (mgr.isEmpty(suppl) ? "": suppl) + "^" + (mgr.isEmpty(supplier) ? "": supplier) + "^" + (mgr.isEmpty(suppdesc) ? "": suppdesc) + "^";

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

         cmd = trans.addCustomFunction("GETWORKDESC", "Standard_Job_API.Get_Work_Description", "DESCRIPTION");
         cmd.addParameter("STD_JOB_ID", stdJobId);
         cmd.addParameter("STD_JOB_CONTRACT", stdJobContract);
         cmd.addParameter("STD_JOB_REVISION", stdJobRev);

         cmd = trans.addCustomFunction("GETSTATUS","Standard_Job_API.Get_Status","STD_JOB_STATUS");
         cmd.addParameter("STD_JOB_ID", stdJobId);
         cmd.addParameter("STD_JOB_CONTRACT", stdJobContract);
         cmd.addParameter("STD_JOB_REVISION", stdJobRev);

         trans = mgr.validate(trans);

         String definition = trans.getValue("GETDESC/DATA/DESCRIPTION");
         String workDesc = trans.getValue("GETWORKDESC/DATA/DESCRIPTION");
         String status = trans.getValue("GETSTATUS/DATA/STD_JOB_STATUS");

         if (!mgr.isEmpty(workDesc)) {
            desc = workDesc;
         }
         else
            desc = definition;

         txt = (mgr.isEmpty(stdJobId)?"":stdJobId) + "^" + (mgr.isEmpty(stdJobRev)?"":stdJobRev) + "^" + (mgr.isEmpty(desc)?"":desc) + "^"+ (mgr.isEmpty(status)?"":status) + "^";

         mgr.responseWrite(txt);
      }

      else if ("STD_JOB_REVISION".equals(val))
      {
         String valueStr = mgr.readValue("STD_JOB_REVISION");
         String stdJobId  = mgr.readValue("STD_JOB_ID");
         String stdJobContract = mgr.readValue("STD_JOB_CONTRACT");
         String stdJobRev = "";
         String desc = "";

         if (valueStr.indexOf("~") > -1)
         {
            String[] fieldValues = valueStr.split("~");

            stdJobId = fieldValues[0];
            stdJobRev = fieldValues[2];
         }
         else
         {
            stdJobId  = mgr.readValue("STD_JOB_ID");
            stdJobRev = valueStr;
         }

         trans.clear();

         cmd = trans.addCustomFunction("GETDESC", "Standard_Job_API.Get_Definition", "DESCRIPTION");
         cmd.addParameter("STD_JOB_ID", stdJobId);
         cmd.addParameter("STD_JOB_CONTRACT", stdJobContract);
         cmd.addParameter("STD_JOB_REVISION", stdJobRev);

         cmd = trans.addCustomFunction("GETWORKDESC", "Standard_Job_API.Get_Work_Description", "DESCRIPTION");
         cmd.addParameter("STD_JOB_ID", stdJobId);
         cmd.addParameter("STD_JOB_CONTRACT", stdJobContract);
         cmd.addParameter("STD_JOB_REVISION", stdJobRev);

         cmd = trans.addCustomFunction("GETSTATUS","Standard_Job_API.Get_Status","STD_JOB_STATUS");
         cmd.addParameter("STD_JOB_ID", stdJobId);
         cmd.addParameter("STD_JOB_CONTRACT", stdJobContract);
         cmd.addParameter("STD_JOB_REVISION", stdJobRev);

         trans = mgr.validate(trans);

         String definition = trans.getValue("GETDESC/DATA/DESCRIPTION");
         String workDesc = trans.getValue("GETWORKDESC/DATA/DESCRIPTION");
         String status = trans.getValue("GETSTATUS/DATA/STD_JOB_STATUS");

         if (!mgr.isEmpty(workDesc)) {
            desc = workDesc;
         }
         else
            desc = definition;

         txt = (mgr.isEmpty(stdJobId)?"":stdJobId) + "^" + (mgr.isEmpty(stdJobRev)?"":stdJobRev) + "^" + (mgr.isEmpty(desc)?"":desc) + "^"+ (mgr.isEmpty(status)?"":status) + "^";

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
         String sOutExsist  = "";
         String sOverlapExsist  = "";
         String sQty ="";
         Date dDateFrom ;
         ASPBuffer buff = mgr.newASPBuffer();
         buff.addFieldItem("ITEM7_DATE_FROM",mgr.readValue("ITEM7_DATE_FROM"));

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

            cmd  = trans.addCustomFunction("GETOUTEXIST","WORK_ORDER_ROLE_API.Out_Of_Lines_Exist","OUTEXIST");
            cmd.addParameter("ITEM7_WO_NO");
            cmd.addParameter("JOB_ID");
            cmd.addParameter("ITEM7_DATE_TO");

            cmd  = trans.addCustomFunction("GETOVEREXIST","WORK_ORDER_ROLE_API.Overlap_Bookings_Exist","OVERLAPEXIST");
            cmd.addParameter("ITEM7_WO_NO");
            cmd.addParameter("JOB_ID");
            cmd.addParameter("ITEM7_DATE_FROM");
            cmd.addParameter("ITEM7_DATE_TO");

            cmd  = trans.addCustomFunction("GETFROMDATE","Work_Order_Job_API.Get_Date_From","ITEM7_DATE_FROM");
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
            sOutExsist = trans.getValue("GETOUTEXIST/DATA/OUTEXIST");
            sOverlapExsist  = trans.getValue("GETOVEREXIST/DATA/OVERLAPEXIST");
            dDateFrom       = trans.getBuffer("GETFROMDATE/DATA").getFieldDateValue("ITEM7_DATE_FROM");

            if ("TRUE".equals(sOverlapExsist)  &&  !(buff.getFieldDateValue("ITEM7_DATE_FROM").equals(dDateFrom)))
               sOverlapExsist = "TRUE";
            else
               sOverlapExsist = "FALSE";

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
               (mgr.isEmpty(sAgreementId)?"":sAgreementId) + "^" +
               (mgr.isEmpty(sOutExsist)?"":sOutExsist) + "^" +
               (mgr.isEmpty(sOverlapExsist)?"":sOverlapExsist) + "^" ;

         mgr.responseWrite(txt);
      }

      else if ("ITEM7_SIGN_ID".equals(val))
      {
         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[2];
         String emp_id = "";
         String sign = "";

         String new_sign = mgr.readValue("ITEM7_SIGN_ID","");

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


      else if ("ITEM2_JOB_ID".equals(val))
      {
         cmd = trans.addCustomFunction("GETEMP","Work_Order_Job_API.Get_Executed_By","EMPLOYEE_ID");
         cmd.addParameter("ITEM2_WO_NO");
         cmd.addParameter("ITEM2_JOB_ID");

         cmd = trans.addCustomFunction("GETPERSON","Company_Emp_API.Get_Person_Id","SIGN");
         cmd.addParameter("ITEM2_COMPANY");
         cmd.addReference("EMPLOYEE_ID","GETEMP/DATA");

         trans = mgr.validate(trans);

         String emp_id = trans.getValue("GETEMP/DATA/EMPLOYEE_ID");
         String sign = trans.getValue("GETPERSON/DATA/SIGN");


         txt = (mgr.isEmpty(emp_id)?"":emp_id) + "^"+
               (mgr.isEmpty(sign)?"":sign) + "^";
         mgr.responseWrite(txt);
      }

      //----validations for operations under jobs tab--------------

      else if ("ITEM8_TEAM_ID".equals(val))
      {
         cmd = trans.addCustomFunction("TDESC", "Maint_Team_API.Get_Description", "ITEM8_TEAMDESC" );    
         cmd.addParameter("ITEM8_TEAM_ID");
         cmd.addParameter("ITEM8_TEAM_CONTRACT");
         trans = mgr.validate(trans);   
         teamDesc  = trans.getValue("TDESC/DATA/ITEM8_TEAMDESC");

         txt =  (mgr.isEmpty(teamDesc) ? "" : (teamDesc)) + "^";
         mgr.responseWrite(txt);

      }

      else if ("ITEM8_ORG_CODE".equals(val))
      {
         String colRoleCode = mgr.readValue("ITEM8_ROLE_CODE","");
         String colOrgContract = "";
         String colOrgCode = "";
         String colCatalogContract = mgr.readValue("ITEM8_CATALOG_CONTRACT","");
         String colCatalogNo  = mgr.readValue("ITEM8_CATALOG_NO","");

         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[6];

         String new_org_code = mgr.readValue("ITEM8_ORG_CODE","");

         if (new_org_code.indexOf("^",0)>0)
         {
            if (!mgr.isEmpty(mgr.readValue("ITEM8_ROLE_CODE")))
            {
               for (i=0 ; i<5; i++)
               {
                  endpos = new_org_code.indexOf("^",startpos);
                  reqstr = new_org_code.substring(startpos,endpos);
                  ar[i] = reqstr;
                  startpos= endpos+1;
               }
               colOrgCode = ar[3];
               colOrgContract = ar[4];
            }
            else
            {
               if (!mgr.isEmpty(mgr.readValue("ITEM8_SIGN")))
               {
                  for (i=0 ; i<4; i++)
                  {
                     endpos = new_org_code.indexOf("^",startpos);
                     reqstr = new_org_code.substring(startpos,endpos);
                     ar[i] = reqstr;
                     startpos= endpos+1;
                  }
                  colOrgCode = ar[2];
                  colOrgContract = ar[3];
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
                  colOrgCode = ar[0];
                  colOrgContract = ar[1];
               }
            }
         }
         else
         {
            colOrgCode = mgr.readValue("ITEM8_ORG_CODE");
            colOrgContract = mgr.readValue("ITEM8_CONTRACT"); 
         }

         double planMen = mgr.readNumberValue ("ITEM8_PLAN_MEN");
         if (isNaN(planMen)) planMen = 0;

         double planHrs = mgr.readNumberValue ("ITEM8_PLAN_HRS");
         if (isNaN(planHrs)) planHrs = 0;

         double nCost = getCostItem8(colOrgContract,colOrgCode,colRoleCode,colCatalogContract,colCatalogNo,mgr.readValue("ITEM8_WO_NO"));
         double nCostAmount = countCostAmount(nCost,planMen,planHrs);

         String nCostStr = mgr.getASPField("ITEM8_COST").formatNumber(nCost);
         String colAmountCostStr = mgr.getASPField("ITEM8_COSTAMOUNT").formatNumber(nCostAmount);

         //bug id 59224,start
         trans.clear();
         cmd = trans.addCustomFunction("GETITEM8ORGDESC","Organization_Api.Get_Description","ITEM8_ORG_CODE_DESC");
         cmd.addParameter("ITEM8_CONTRACT",colOrgContract);
         cmd.addParameter("ITEM8_ORG_CODE",colOrgCode);

         trans = mgr.validate(trans);

         String sitem8OrgDesc = trans.getValue("GETITEM8ORGDESC/DATA/ITEM8_ORG_CODE_DESC");
         trans.clear();
         //bug id 59224,end

         if (mgr.isEmpty(colRoleCode))
         {
            String cataNo = "";
            String cataContract = "";
            String cataDesc = "";

            trans.clear();

            secBuff = mgr.newASPTransactionBuffer();
            secBuff.addSecurityQuery("SALES_PART");

            secBuff = mgr.perform(secBuff);

            if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
            {
               cmd = trans.addCustomFunction("CATANO", "Organization_Sales_Part_API.Get_Def_Catalog_No", "ITEM8_CATALOG_NO");
               cmd.addParameter("ITEM8_CONTRACT", colOrgContract);
               cmd.addParameter("ITEM8_ORG_CODE", colOrgCode);

               cmd = trans.addCustomFunction("DEFCONT", "Organization_Sales_Part_API.Get_Def_Contract", "ITEM8_CATALOG_CONTRACT");
               cmd.addParameter("ITEM8_CONTRACT", colOrgContract);
               cmd.addParameter("ITEM8_ORG_CODE", colOrgCode);

               cmd = trans.addCustomFunction("CATADESC", "Sales_Part_API.Get_Catalog_Desc", "ITEM8_CATALOGDESC");
               cmd.addReference("ITEM8_CATALOG_CONTRACT", "DEFCONT/DATA");
               cmd.addReference("ITEM8_CATALOG_NO", "CATANO/DATA");

               trans = mgr.validate(trans);

               cataNo = trans.getValue("CATANO/DATA/CATALOG_NO");
               cataContract = trans.getValue("DEFCONT/DATA/CATALOG_CONTRACT");
               cataDesc = trans.getValue("CATADESC/DATA/ITEM8_CATALOGDESC");
            }

            txt = (mgr.isEmpty(cataNo) ? "": (cataNo))+ "^" + 
                  (mgr.isEmpty(cataDesc) ? "": (cataDesc))+ "^" + 
                  (mgr.isEmpty(nCostStr) ? "": (nCostStr))+ "^" + 
                  (mgr.isEmpty(colAmountCostStr) ? "": (colAmountCostStr))+ "^"+
                  (mgr.isEmpty(colOrgCode) ? "": (colOrgCode))+ "^" + 
                  (mgr.isEmpty(colOrgContract) ? "": (colOrgContract))+ "^"+
                  (mgr.isEmpty(sitem8OrgDesc) ? "": (sitem8OrgDesc))+ "^";
         }
         else
            txt   = mgr.readValue("ITEM8_CATALOG_NO","")+ "^" + 
                    mgr.readValue("ITEM8_CATALOGDESC","")+ "^" + 
                    (mgr.isEmpty(nCostStr) ? "": (nCostStr)) + "^" + 
                    (mgr.isEmpty(colAmountCostStr) ? "": (colAmountCostStr)) + "^"+
                    (mgr.isEmpty(colOrgCode) ? "": (colOrgCode))+ "^" + 
                    (mgr.isEmpty(colOrgContract) ? "": (colOrgContract))+ "^"+
                    (mgr.isEmpty(sitem8OrgDesc) ? "": (sitem8OrgDesc))+ "^";

         mgr.responseWrite(txt);   
      }
      else if ("ITEM8_ROLE_CODE".equals(val))
      {
         String colRoleCode = "";
         String colOrgContract = "";
         String colOrgCode = mgr.readValue("ITEM8_ORG_CODE","");
         String colCatalogContract = mgr.readValue("ITEM8_CATALOG_CONTRACT","");
         String colCatalogNo  = mgr.readValue("ITEM8_CATALOG_NO","");
         double planMen = mgr.readNumberValue ("ITEM8_PLAN_MEN");
         if (isNaN(planMen)) planMen = 0;

         double planHrs = mgr.readNumberValue ("ITEM8_PLAN_HRS");
         if (isNaN(planHrs)) planHrs = 0;

         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[6];

         String new_role_code = mgr.readValue("ITEM8_ROLE_CODE","");

         if (new_role_code.indexOf("^",0)>0)
         {
            if (!mgr.isEmpty(mgr.readValue("ITEM8_SIGN")))
            {
               for (i=0 ; i<5; i++)
               {
                  endpos = new_role_code.indexOf("^",startpos);
                  reqstr = new_role_code.substring(startpos,endpos);
                  ar[i] = reqstr;
                  startpos= endpos+1;
               }
               colRoleCode = ar[2];
               colOrgContract = ar[4];
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
               colRoleCode = ar[0];
               colOrgContract = ar[1];

            }
         }
         else
         {
            colRoleCode = mgr.readValue("ITEM8_ROLE_CODE");
            colOrgContract = mgr.readValue("ITEM8_CONTRACT"); 
         }

         trans.clear();

         double nCost = getCostItem8(colOrgContract,colOrgCode,colRoleCode,colCatalogContract,colCatalogNo,mgr.readValue("ITEM8_WO_NO"));
         double nCostAmount = countCostAmount(nCost,planMen,planHrs);

         String nCostStr = mgr.getASPField("ITEM8_COST").formatNumber(nCost);
         String colAmountCostStr = mgr.getASPField("ITEM8_COSTAMOUNT").formatNumber(nCostAmount);

         trans.clear();

         cmd = trans.addCustomFunction("ROLECATANO", "Role_Sales_Part_API.Get_Def_Catalog_No", "ITEM8_CATALOG_NO");
         cmd.addParameter("ITEM8_ROLE_CODE");  
         cmd.addParameter("ITEM8_CONTRACT");

         //Bug 69392, start
         if (mgr.isModuleInstalled("ORDER"))
         {
            cmd = trans.addCustomFunction("DEFACODESC", "Sales_Part_API.Get_Catalog_Desc", "ITEM8_CATALOGDESC");
            cmd.addParameter("ITEM8_CONTRACT");
            cmd.addReference("ITEM8_CATALOG_NO", "ROLECATANO/DATA");    
         }
         //Bug 69392, end
         //bug id 59224,start
         cmd = trans.addCustomFunction("ITEM8ROLEDESC","ROLE_API.Get_Description","ITEM8_ROLE_CODE_DESC");
         cmd.addParameter("ITEM8_ROLE_CODE",colRoleCode);

         trans = mgr.validate(trans);

         String sitem8roleDesc = trans.getValue("ITEM8ROLEDESC/DATA/ITEM8_ROLE_CODE_DESC");
         String catalogNo = trans.getValue("ROLECATANO/DATA/CATALOG_NO");
         String catalogContract = mgr.readValue("ITEM8_CONTRACT","");

         //Bug 69392, start
         String catalogDescp = "";
         if (mgr.isModuleInstalled("ORDER"))
            catalogDescp = trans.getValue("DEFACODESC/DATA/ITEM8_CATALOGDESC");
         //Bug 69392, end

         trans.clear();
         cmd = trans.addCustomFunction("EXIST","User_Allowed_Site_API.Is_Authorized","USERALLOWED");
         cmd.addParameter("ITEM8_CATALOG_CONTRACT",catalogContract);

         if (mgr.isEmpty(catalogNo))
         {
            cmd = trans.addCustomFunction("ROLECATANO", "Organization_Sales_Part_API.Get_Def_Catalog_No ", "ITEM8_CATALOG_NO");
            cmd.addParameter("ITEM8_CONTRACT");  
            cmd.addParameter("ITEM8_ORG_CODE",colOrgCode);

            //Bug 69392, start
            if (mgr.isModuleInstalled("ORDER"))
            {
               cmd = trans.addCustomFunction("DEFACODESC", "Sales_Part_API.Get_Catalog_Desc", "ITEM8_CATALOGDESC");
               cmd.addParameter("ITEM8_CONTRACT");
               cmd.addReference("ITEM8_CATALOG_NO", "ROLECATANO/DATA");      
            }
            //Bug 69392, end
         }

         trans=mgr.validate(trans); 
         String exist=trans.getValue("EXIST/DATA/USERALLOWED");

         if (mgr.isEmpty(catalogNo))
         {
            catalogNo = trans.getValue("ROLECATANO/DATA/CATALOG_NO");
            //Bug 69392, start
            if (mgr.isModuleInstalled("ORDER"))
               catalogDescp = trans.getValue("DEFACODESC/DATA/ITEM8_CATALOGDESC");
            //Bug 69392, end
         }

         if ("0".equals(exist))
         {
            catalogNo = "";
            catalogContract = "";
            catalogDescp = ""; 
         }

         txt = (mgr.isEmpty(catalogNo) ? "": (catalogNo)) + "^" + 
               (mgr.isEmpty(nCostStr) ? "": (nCostStr))+ "^" + 
               (mgr.isEmpty(colAmountCostStr) ? "": (colAmountCostStr))+ "^" + 
               (mgr.isEmpty(catalogDescp) ? "": (catalogDescp))+ "^"+
               (mgr.isEmpty(colRoleCode) ? "": (colRoleCode))+ "^" + 
               (mgr.isEmpty(colOrgContract) ? "": (colOrgContract))+ "^"+
               (mgr.isEmpty(sitem8roleDesc) ? "": (sitem8roleDesc))+ "^";

         mgr.responseWrite(txt);   
      }
      else if ("ITEM8_SIGN_ID".equals(val))
      {

         String signId = mgr.readValue("ITEM8_SIGN_ID");
         String sSign = "";      
         String sOrgContract = mgr.readValue("ITEM8_CONTRACT");
         String sOrgCode = mgr.readValue("ITEM8_ORG_CODE");
         String sDefRole = "";
         String sCatalogNo0 = mgr.readValue("ITEM8_CATALOG_NO","");     
         String sCatalogNo = mgr.readValue("ITEM8_CATALOG_NO","");     
         String sCatalogContract = mgr.readValue("ITEM8_CATALOG_CONTRACT");
         String sCatalogDesc = mgr.readValue("ITEM8_CATALOGDESC");
         String sCatalogNo1 = "";     
         String sCatalogContract1 = "";
         String sCatalogDesc1 = "";
         String sCatalogNo2 = "";     
         String sCatalogContract2 = "";
         String sCatalogDesc2 = "";
         String sPriceListNo = mgr.readValue("ITEM8_PRICE_LIST_NO");
         String sDiscount = mgr.readValue("ITEM8_DISCOUNT");
         String sListPrice = mgr.readValue("ITEM8_LISTPRICE");
         String sSalesPriceAmount = mgr.readValue("ITEM8_SALESPRICEAMOUNT");
         String sDiscountedPrice = mgr.readValue("ITEM8_DISCOUNTED_PRICE");  
         //

         cmd = trans.addCustomFunction("EMPEXIST", "Employee_API.Is_Emp_Exist", "ITEM8_TEMP1");
         cmd.addParameter("ITEM8_COMPANY");
         cmd.addParameter("ITEM8_SIGN_ID");  

         trans.addSecurityQuery("SALES_PART");
         trans.addSecurityQuery("ORGANIZATION_SALES_PART");
         trans.addSecurityQuery("ROLE_SALES_PART");
         trans.addSecurityQuery("Customer_Order_Pricing_API", "Get_Valid_Price_List");

         trans = mgr.perform(trans);

         boolean bEmpExist = !("0".equals(trans.getValue("EMPEXIST/DATA/ITEM8_TEMP1")));

         boolean bSalesPartOk = 
         trans.getSecurityInfo().itemExists("SALES_PART") &&
         trans.getSecurityInfo().itemExists("ORGANIZATION_SALES_PART") &&
         trans.getSecurityInfo().itemExists("ROLE_SALES_PART");

         boolean bPricingOk = trans.getSecurityInfo().itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List");

         if (bEmpExist)
         {
            trans.clear();

            cmd = trans.addCustomFunction("GETSIGN", "Company_Emp_API.Get_Person_Id", "ITEM8_SIGN");
            cmd.addParameter("ITEM8_COMPANY");
            cmd.addParameter("ITEM8_SIGN_ID");  

            cmd = trans.addCustomFunction("GETDEFROLE", "Employee_Role_API.Get_Default_Role", "ITEM8_ROLE_CODE");
            cmd.addParameter("ITEM8_COMPANY");
            cmd.addParameter("ITEM8_SIGN_ID");  

            cmd = trans.addCustomFunction("GETORGCODE", "Employee_API.Get_Organization", "ITEM8_ORG_CODE");
            cmd.addParameter("ITEM8_COMPANY");
            cmd.addParameter("ITEM8_SIGN_ID");  

            trans = mgr.validate(trans);

            sSign = trans.getValue("GETSIGN/DATA/SIGN");
            sOrgCode = trans.getValue("GETORGCODE/DATA/ORG_CODE");
            String sDefRole1 = trans.getValue("GETDEFROLE/DATA/ROLE_CODE");

            if (!mgr.isEmpty(sDefRole1))
               sDefRole =sDefRole1;

            if (bSalesPartOk)
            {
               trans.clear();

               if (mgr.isEmpty(sDefRole))
               {
                  cmd = trans.addCustomFunction("GETDEFCATNO1", "Organization_Sales_Part_API.Get_Def_Catalog_No", "ITEM8_CATALOG_NO");
                  cmd.addParameter("ITEM8_CONTRACT", sOrgContract);
                  cmd.addParameter("ITEM8_ORG_CODE", sOrgCode);

                  cmd = trans.addCustomFunction("GETDEFCONTRACT1", "Organization_Sales_Part_API.Get_Def_Contract", "ITEM8_CATALOG_CONTRACT");
                  cmd.addParameter("ITEM8_CONTRACT", sOrgContract);
                  cmd.addParameter("ITEM8_ORG_CODE", sOrgCode);

                  //Bug 69392, start
                  if (mgr.isModuleInstalled("ORDER"))
                  {

                     cmd = trans.addCustomFunction("GETDEFCATDESC1","Sales_Part_API.Get_Catalog_Desc","ITEM8_CATALOGDESC");
                     cmd.addReference("ITEM8_CATALOG_CONTRACT","GETDEFCONTRACT1/DATA");
                     cmd.addReference("ITEM8_CATALOG_NO","GETDEFCATNO1/DATA");

                  }
                  //Bug 69392, end
                  trans = mgr.validate(trans);

                  sCatalogNo1 = trans.getValue("GETDEFCATNO1/DATA/CATALOG_NO");
                  sCatalogContract1 = trans.getValue("GETDEFCONTRACT1/DATA/CATALOG_CONTRACT");
                  //Bug 69392, start
                  if (mgr.isModuleInstalled("ORDER"))
                     sCatalogDesc1 = trans.getValue("GETDEFCATDESC1/DATA1/ITEM8_CATALOGDESC");
                  //Bug 69392, end

               }
               else
               {
                  cmd = trans.addCustomFunction("GETDEFCATNO2", "Role_Sales_Part_API.Get_Def_Catalog_No", "ITEM8_CATALOG_NO");
                  cmd.addParameter("ITEM8_ROLE_CODE", sDefRole);
                  cmd.addParameter("ITEM8_CONTRACT", sOrgContract);

                  //Bug 69392, start
                  if (mgr.isModuleInstalled("ORDER"))
                  {
                     cmd = trans.addCustomFunction("GETDEFCATDESC2","Sales_Part_API.Get_Catalog_Desc","ITEM8_CATALOGDESC");
                     cmd.addParameter("ITEM8_CONTRACT", sOrgContract);
                     cmd.addReference("ITEM8_CATALOG_NO","GETDEFCATNO2/DATA");
                  }
                  //Bug 69392, end
                  trans = mgr.validate(trans);

                  sCatalogNo2 = trans.getValue("GETDEFCATNO2/DATA/CATALOG_NO");
                  sCatalogContract2 = sOrgContract;

                  //Bug 69392, start
                  if (mgr.isModuleInstalled("ORDER"))
                     sCatalogDesc2 = trans.getValue("GETDEFCATDESC2/DATA/ITEM8_CATALOGDESC");
                  //Bug 69392, end

               }


               if (!mgr.isEmpty(sCatalogNo2))
               {
                  sCatalogNo = sCatalogNo2;
                  sCatalogContract = sCatalogContract2;
                  sCatalogDesc2 =  sCatalogContract2;
               }
               else if (!mgr.isEmpty(sCatalogNo1))
               {
                  sCatalogNo = sCatalogNo1;
                  sCatalogContract = sCatalogContract1;
                  sCatalogDesc =  sCatalogContract1;
               }

               trans.clear();
               if (!sCatalogNo0.equals(sCatalogNo))
               {


                  if (bPricingOk)
                  {
                     cmd = trans.addCustomFunction("GETCURRCODE","ACTIVE_SEPARATE_API.Get_Currency_Code","CURRENCY_CODE");
                     cmd.addParameter("ITEM8_WO_NO");

                     cmd = trans.addCustomFunction("GETCUSTOMERNO","Active_Work_Order_API.Get_Customer_No","CUSTOMER_NO");
                     cmd.addParameter("ITEM8_WO_NO");

                     cmd = trans.addCustomFunction("GETPRICELIST","Customer_Order_Pricing_Api.Get_Valid_Price_List","ITEM8_PRICE_LIST_NO");
                     cmd.addParameter("ITEM8_CATALOG_CONTRACT", sCatalogContract);
                     cmd.addParameter("ITEM8_CATALOG_NO", sCatalogNo);
                     cmd.addReference("CUSTOMER_NO", "GETCUSTOMERNO/DATA");
                     cmd.addReference("CURRENCY_CODE", "GETCURRCODE/DATA");
                  }

                  trans = mgr.validate(trans);


                  if (bPricingOk)
                     sPriceListNo = trans.getValue("GETPRICELIST/DATA/PRICE_LIST_NO");

                  String salesPartInfo[] = getSalesPartInfoItem8(false,true,sPriceListNo,sCatalogContract,sCatalogNo);
                  sDiscount = salesPartInfo[1];
                  sListPrice = salesPartInfo[2];
                  sSalesPriceAmount = salesPartInfo[3];
                  sDiscountedPrice = salesPartInfo[4];
               }
            }


            txt = (mgr.isEmpty(sSign) ? "": (sSign))+ "^" + 
                  (mgr.isEmpty(sDefRole) ? "": (sDefRole))+ "^"+
                  (mgr.isEmpty(sOrgContract) ? "": (sOrgContract))+ "^"+
                  (mgr.isEmpty(sOrgCode) ? "": (sOrgCode))+ "^"+
                  (mgr.isEmpty(sCatalogContract) ? "": (sCatalogContract))+ "^"+
                  (mgr.isEmpty(sCatalogNo) ? "": (sCatalogNo))+ "^"+
                  (mgr.isEmpty(sCatalogDesc) ? "": (sCatalogDesc))+ "^"+
                  (mgr.isEmpty(sPriceListNo) ? "": (sPriceListNo))+ "^"+
                  (mgr.isEmpty(sListPrice) ? "": (sListPrice))+ "^"+
                  (mgr.isEmpty(sDiscount) ? "": (sDiscount))+ "^"+
                  (mgr.isEmpty(sDiscountedPrice) ? "": (sDiscountedPrice))+ "^"+
                  (mgr.isEmpty(sSalesPriceAmount) ? "": (sSalesPriceAmount))+ "^";
            mgr.responseWrite(txt);
         }
         else
         {

            txt = "" + "^" + "" + "^" + (mgr.isEmpty(sOrgContract) ? "": (sOrgContract))+ "^"+
                  "" + "^" + (mgr.isEmpty(sCatalogContract) ? "": (sCatalogContract))+ "^"+
                  "" + "^" + "" + "^" + "" + "^" + "" + "^" + "" + "^" + "" + "^" + "" + "^" ;
            mgr.responseWrite(txt); 
         }    
      }
      else if ("ITEM8_SIGN".equals(val))
      {
         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[2];
         String emp_id = "";
         String sign = "";
         String sOrgCode = mgr.readValue("ITEM8_ORG_CODE");
         String sOrgContract = mgr.readValue("ITEM8_CONTRACT");
         String roleCode = mgr.readValue("ITEM8_ROLE_CODE");
         String new_sign = mgr.readValue("ITEM8_SIGN","");
         String depart = "";
         String departCont = "";
         String planHours;
         double totManHrs=0;
         double remainHrs=0;
         String remainHours;
         String allocatedHours;
         double allocatedHrs = mgr.readNumberValue("ITEM8_PLAN_HRS");
         if (isNaN(allocatedHrs))
            allocatedHrs = 0;
         allocatedHours = mgr.getASPField("ITEM8_TOTAL_ALLOCATED_HOURS").formatNumber(allocatedHrs);

         totManHrs = mgr.readNumberValue("ITEM8_TOTAL_MAN_HOURS");
         if (isNaN(totManHrs))
            totManHrs = 0;

         remainHrs = totManHrs - allocatedHrs ;
         remainHours = mgr.getASPField("ITEM8_TOTAL_ALLOCATED_HOURS").formatNumber(remainHrs);
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
            cmd = trans.addCustomFunction("SIGNID","Employee_API.Get_Max_Maint_Emp","ITEM8_SIGN_ID");                     
            cmd.addParameter("ITEM8_COMPANY");
            cmd.addParameter("ITEM8_SIGN");

            trans = mgr.validate(trans);
            emp_id = trans.getValue("SIGNID/DATA/SIGN_ID");
            sign = new_sign;

            trans.clear();
         }

         if (mgr.isEmpty(sOrgCode))
         {
            cmd = trans.addCustomFunction("EMPORGCOD","Employee_API.Get_Organization","ITEM8_ORG_CODE");
            cmd.addParameter("ITEM8_COMPANY");
            cmd.addParameter("ITEM8_SIGN_ID",emp_id);
         }
         if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
         {
            cmd = trans.addCustomFunction("EMPORGCONT","Employee_API.Get_Org_Contract","ITEM8_ORG_CONTRACT");
            cmd.addParameter("ITEM8_COMPANY");
            cmd.addParameter("ITEM8_SIGN_ID",emp_id);
         }

         trans = mgr.validate(trans);

         if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
            sOrgContract = trans.getValue("EMPORGCONT/DATA/ORG_CONTRACT");
         if (mgr.isEmpty(sOrgCode))
            sOrgCode = trans.getValue("EMPORGCOD/DATA/ORG_CODE");

         trans.clear();

         cmd = trans.addCustomFunction("CATCONT","Active_Separate_Api.Get_Contract","ITEM8_CONTRACT");
         cmd.addParameter("ITEM8_WO_NO",mgr.readValue("ITEM8_WO_NO"));

         if (mgr.isEmpty(roleCode))
         {
            cmd = trans.addCustomFunction("DEFROLE","Employee_Role_API.Get_Default_Role","ITEM8_ROLE_CODE");
            cmd.addParameter("ITEM8_COMPANY",mgr.readValue("ITEM8_COMPANY"));
            cmd.addParameter("ITEM8_SIGN_ID",emp_id); 

            cmd = trans.addCustomFunction("ROLECATANO","Role_Sales_Part_API.Get_Def_Catalog_No","ITEM8_CATALOG_NO");
            cmd.addReference("ITEM8_ROLE_CODE","DEFROLE/DATA");
            cmd.addReference("ITEM8_CONTRACT","CATCONT/DATA");

            //Bug 69392, start
            if (mgr.isModuleInstalled("ORDER"))
            {
               cmd = trans.addCustomFunction("DEFACODESC","Sales_Part_API.Get_Catalog_Desc","ITEM8_CATALOGDESC");
               cmd.addReference("ITEM8_CONTRACT","CATCONT/DATA");
               cmd.addReference("ITEM8_CATALOG_NO","ROLECATANO/DATA"); 
            }
            //Bug 69392, end
         }
         else
         {
            cmd = trans.addCustomFunction("ROLECATANO","Role_Sales_Part_API.Get_Def_Catalog_No","ITEM8_CATALOG_NO");
            cmd.addParameter("ITEM8_ROLE_CODE",roleCode);
            cmd.addReference("ITEM8_CONTRACT","CATCONT/DATA");

            //Bug 69392, start
            if (mgr.isModuleInstalled("ORDER"))
            {
               cmd = trans.addCustomFunction("DEFACODESC","Sales_Part_API.Get_Catalog_Desc","ITEM8_CATALOGDESC");
               cmd.addReference("ITEM8_CONTRACT","CATCONT/DATA");
               cmd.addReference("ITEM8_CATALOG_NO","ROLECATANO/DATA");
            }
            //Bug 69392, end
         }

         trans = mgr.validate(trans);

         String catalogNo = trans.getValue("ROLECATANO/DATA/CATALOG_NO");
         String catalogContract = trans.getValue("CATCONT/DATA/CONTRACT");

         //Bug 69392, start
         String catalogDescp = "";
         if (mgr.isModuleInstalled("ORDER"))
            catalogDescp = trans.getValue("DEFACODESC/DATA/ITEM8_CATALOGDESC");
         //Bug 69392, end
         String signId = emp_id;

         if (mgr.isEmpty(roleCode))
            roleCode = trans.getValue("DEFROLE/DATA/ROLE_CODE");

         if (mgr.isEmpty(roleCode) && mgr.isEmpty(catalogNo) && (catalogContract == sOrgContract))
         {
            trans.clear();

            cmd = trans.addCustomFunction("ORGCATANO","Organization_Sales_Part_API.Get_Def_Catalog_No","ITEM8_CATALOG_NO");
            cmd.addParameter("ITEM8_CONTRACT",sOrgContract);
            cmd.addParameter("ITEM8_ORG_CODE",sOrgCode);
            //Bug 69392, start
            if (mgr.isModuleInstalled("ORDER"))
            {
               cmd = trans.addCustomFunction("ORGACODESC","Sales_Part_API.Get_Catalog_Desc","ITEM8_CATALOGDESC");
               cmd.addParameter("ITEM8_CONTRACT",sOrgContract);
               cmd.addReference("ITEM8_CATALOG_NO","ORGCATANO/DATA");
            }
            //Bug 69392, end

            trans = mgr.validate(trans);

            catalogNo = trans.getValue("ORGCATANO/DATA/CATALOG_NO");
            //Bug 69392, start
            if (mgr.isModuleInstalled("ORDER"))
               catalogDescp = trans.getValue("ORGACODESC/DATA/ITEM8_CATALOGDESC");
            //Bug 69392, end
         }

         String colRoleCode = "";
         if (mgr.isEmpty(roleCode))
            colRoleCode = mgr.readValue("ITEM8_ROLE_CODE","");
         else
            colRoleCode = roleCode;

         double planMen = mgr.readNumberValue ("ITEM8_PLAN_MEN");
         if (isNaN(planMen)) planMen = 0;

         double planHrs = mgr.readNumberValue ("ITEM8_PLAN_HRS");
         if (isNaN(planHrs)) planHrs = 0;

         double nCost = getCostItem8(sOrgContract,sOrgCode,colRoleCode,catalogContract,catalogNo,mgr.readValue("ITEM8_WO_NO"));
         double nCostAmount = countCostAmount(nCost,planMen,planHrs);

         String nCostStr = mgr.getASPField("ITEM8_COST").formatNumber(nCost);
         String colAmountCostStr = mgr.getASPField("ITEM8_COSTAMOUNT").formatNumber(nCostAmount);
         //bug id 59224,start
         trans.clear();
         cmd = trans.addCustomFunction("GETITEM8ORGDESC","Organization_Api.Get_Description","ITEM8_ORG_CODE_DESC");
         cmd.addParameter("ITEM8_CONTRACT",sOrgContract);
         cmd.addParameter("ITEM8_ORG_CODE",sOrgCode);


         cmd = trans.addCustomFunction("ITEM8ROLEDESC","ROLE_API.Get_Description","ITEM8_ROLE_CODE_DESC");
         cmd.addParameter("ITEM8_ROLE_CODE",roleCode);


         trans = mgr.validate(trans);

         String sitem8OrgDesc = trans.getValue("GETITEM8ORGDESC/DATA/ITEM8_ORG_CODE_DESC");
         String sitem8roleDesc = trans.getValue("ITEM8ROLEDESC/DATA/ITEM8_ROLE_CODE_DESC");

         //bug id 59224,end

         txt = (mgr.isEmpty(signId) ? "": (signId))+ "^" + 
               (mgr.isEmpty(roleCode) ? "": (roleCode))+ "^" + 
               (mgr.isEmpty(sOrgContract) ? "": (sOrgContract))+ "^" +
               (mgr.isEmpty(sOrgCode) ? "": (sOrgCode))+ "^" +
               (mgr.isEmpty(catalogNo) ? "": (catalogNo)) + "^" + 
               (mgr.isEmpty(nCostStr) ? "": (nCostStr))+ "^" + 
               (mgr.isEmpty(colAmountCostStr) ? "": (colAmountCostStr))+ "^" + 
               (mgr.isEmpty(catalogDescp) ? "": (catalogDescp))+ "^"+
               (mgr.isEmpty(sign) ? "": (sign))+ "^" +
               (mgr.isEmpty(allocatedHours)? "": (allocatedHours))+ "^" +  //allocated hours
               (mgr.isEmpty(allocatedHours)? "": (allocatedHours))+ "^" +  //total allocated hours
               (mgr.isEmpty(remainHours)? "": (remainHours))+ "^"+
               (mgr.isEmpty(sitem8OrgDesc)? "": (sitem8OrgDesc))+ "^"+
               (mgr.isEmpty(sitem8roleDesc)? "": (sitem8roleDesc))+ "^";

         mgr.responseWrite(txt);
      }
      else if ("ITEM8_PLAN_MEN".equals(val) || "ITEM8_PLAN_HRS".equals(val))
      {
         String colRoleCode = mgr.readValue("ITEM8_ROLE_CODE","");
         String colOrgContract = mgr.readValue("ITEM8_CONTRACT","");
         String colOrgCode = mgr.readValue("ITEM8_ORG_CODE","");
         String colCatalogContract = mgr.readValue("ITEM8_CATALOG_CONTRACT","");
         String colCatalogNo  = mgr.readValue("ITEM8_CATALOG_NO","");

         double planMen = mgr.readNumberValue ("ITEM8_PLAN_MEN");
         if (isNaN(planMen)) planMen = 0;

         double planHrs = mgr.readNumberValue ("ITEM8_PLAN_HRS");
         if (isNaN(planHrs)) planHrs = 0;

         double totAllocHrs = mgr.readNumberValue("ITEM8_TOTAL_ALLOCATED_HOURS");
         if (isNaN(totAllocHrs))
            totAllocHrs = 0;

         double manHours =  planHrs * planMen;
         double remainHrs = manHours - totAllocHrs;

         double nCost = getCostItem8(colOrgContract,colOrgCode,colRoleCode,colCatalogContract,colCatalogNo,mgr.readValue("ITEM8_WO_NO"));
         double nCostAmount = countCostAmount(nCost,planMen,planHrs);

         String nCostStr = mgr.getASPField("ITEM8_COST").formatNumber(nCost);
         String colAmountCostStr = mgr.getASPField("ITEM8_COSTAMOUNT").formatNumber(nCostAmount);

         trans.clear();

         String salesPartInfo[] = getSalesPartInfoItem8(true,false,"","","");

         String strPriceListNo = salesPartInfo[0];
         String strDiscount = salesPartInfo[1];
         String strListPrice = salesPartInfo[2];
         String strSalesPriceAmount = salesPartInfo[3];
         String strDiscountedPrice = salesPartInfo[4];
         String manHoursStr = mgr.getASPField("ITEM8_TOTAL_MAN_HOURS").formatNumber(manHours);
         String remainHrsStr = mgr.getASPField("ITEM8_TOTAL_REMAINING_HOURS").formatNumber(remainHrs);

         txt = (mgr.isEmpty(nCostStr) ? "": (nCostStr)) + "^" + 
               (mgr.isEmpty(colAmountCostStr) ? "": (colAmountCostStr)) + "^" + 
               (mgr.isEmpty(strPriceListNo) ? "" : strPriceListNo) + "^" +
               (mgr.isEmpty(strDiscount) ? "" : strDiscount) + "^" +
               (mgr.isEmpty(strListPrice) ? "" : strListPrice) + "^" +
               (mgr.isEmpty(strSalesPriceAmount) ? "" : strSalesPriceAmount) + "^" +
               (mgr.isEmpty(strDiscountedPrice) ? "" : strDiscountedPrice) + "^" + 
               (mgr.isEmpty(manHoursStr)?"":manHoursStr) + "^" +
               (mgr.isEmpty(remainHrsStr)?"":remainHrsStr) + "^";

         mgr.responseWrite(txt);     

      }
      else if ("ITEM8_ALLOCATED_HOURS".equals(val))
      {
         double allocHours = mgr.readNumberValue ("ITEM8_ALLOCATED_HOURS");
         if (isNaN(allocHours)) allocHours = 0;
         double totManHrs = mgr.readNumberValue ("ITEM8_TOTAL_MAN_HOURS");
         if (isNaN(totManHrs)) totManHrs = 0;
         double remainHrs = totManHrs - allocHours;

         String allocatedHrs = mgr.getASPField("ITEM8_TOTAL_ALLOCATED_HOURS").formatNumber(allocHours);
         String remainingHrs = mgr.getASPField("ITEM8_TOTAL_REMAINING_HOURS").formatNumber(remainHrs);

         txt = (mgr.isEmpty(allocatedHrs) ? "": (allocatedHrs)) + "^" +  //total allocated hrs
               (mgr.isEmpty(remainingHrs) ? "": (remainingHrs)) + "^";

         mgr.responseWrite(txt);    

      }
      else if ("ITEM8_CATALOG_CONTRACT".equals(val))
      {
         String colRoleCode = mgr.readValue("ITEM8_ROLE_CODE","");
         String colOrgContract = mgr.readValue("ITEM8_CONTRACT","");
         String colOrgCode = mgr.readValue("ITEM8_ORG_CODE","");
         String colCatalogContract = mgr.readValue("ITEM8_CATALOG_CONTRACT","");
         String colCatalogNo  = mgr.readValue("ITEM8_CATALOG_NO","");
         double planMen = mgr.readNumberValue ("ITEM8_PLAN_MEN");
         if (isNaN(planMen)) planMen = 0;

         double planHrs = mgr.readNumberValue ("ITEM8_PLAN_HRS");
         if (isNaN(planHrs)) planHrs = 0;

         double nCost = getCostItem8(colOrgContract,colOrgCode,colRoleCode,colCatalogContract,colCatalogNo,mgr.readValue("ITEM8_WO_NO"));
         double nCostAmount = countCostAmount(nCost,planMen,planHrs);

         String nCostStr = mgr.getASPField("ITEM8_COST").formatNumber(nCost);
         String colAmountCostStr = mgr.getASPField("ITEM8_COSTAMOUNT").formatNumber(nCostAmount);

         String salesPartInfo[] = getSalesPartInfoItem8(false,false,"","","");

         String strPriceListNo = salesPartInfo[0];
         String strDiscount = salesPartInfo[1];
         String strListPrice = salesPartInfo[2];
         String strSalesPriceAmount = salesPartInfo[3];
         String strDiscountedPrice = salesPartInfo[4];

         txt = (mgr.isEmpty(nCostStr) ? "": (nCostStr)) + "^" + 
               (mgr.isEmpty(colAmountCostStr) ? "": (colAmountCostStr)) + "^" + 
               (mgr.isEmpty(strPriceListNo) ? "" : strPriceListNo) + "^" +
               (mgr.isEmpty(strDiscount) ? "" : strDiscount) + "^" +
               (mgr.isEmpty(strListPrice) ? "" : strListPrice) + "^" +
               (mgr.isEmpty(strSalesPriceAmount) ? "" : strSalesPriceAmount) + "^" +
               (mgr.isEmpty(strDiscountedPrice) ? "" : strDiscountedPrice) + "^" ;

         mgr.responseWrite(txt);     
      }
      else if ("ITEM8_CATALOG_NO".equals(val))
      {
         String colPriceList = "";

         secBuff = mgr.newASPTransactionBuffer();
         secBuff.addSecurityQuery("Customer_Order_Pricing_Api","Get_Valid_Price_List");

         secBuff = mgr.perform(secBuff);

         String item8RowStatus = mgr.readValue("ITEM8_ROWSTATUS");
         boolean getPriceList = false;

         if (secBuff.getSecurityInfo().itemExists("Customer_Order_Pricing_Api.Get_Valid_Price_List") && ("NEW".equals(item8RowStatus)))
            getPriceList = true;

         trans.clear();

         //Bug 69392, start
         if (mgr.isModuleInstalled("ORDER"))
         {
            cmd = trans.addCustomFunction("GETCATALOGDESCFUNC","Sales_Part_API.Get_Catalog_Desc","ITEM8_CATALOGDESC");
            cmd.addParameter("ITEM8_CATALOG_CONTRACT");
            cmd.addParameter("ITEM8_CATALOG_NO");
         }
         //Bug 69392, end

         if (getPriceList)
         {
            cmd = trans.addCustomFunction("GETPRICELISTFUNC","Customer_Order_Pricing_Api.Get_Valid_Price_List","ITEM8_PRICE_LIST_NO");
            cmd.addParameter("ITEM8_CATALOG_CONTRACT");
            cmd.addParameter("ITEM8_CATALOG_NO");
            cmd.addParameter("CUSTOMER_NO");
            cmd.addParameter("HEAD_CURRENCEY_CODE");
         }

         trans = mgr.perform(trans);
         String colCatalogDesc = trans.getValue("GETCATALOGDESCFUNC/DATA/ITEM8_CATALOGDESC");

         if (getPriceList)
            colPriceList = trans.getValue("GETPRICELISTFUNC/DATA/PRICE_LIST_NO");
         else
            colPriceList = mgr.readValue("ITEM8_PRICE_LIST_NO");

         String colRoleCode = mgr.readValue("ITEM8_ROLE_CODE","");
         String colOrgContract = mgr.readValue("ITEM8_CONTRACT","");
         String colOrgCode = mgr.readValue("ITEM8_ORG_CODE","");
         String colCatalogContract = mgr.readValue("ITEM8_CATALOG_CONTRACT","");
         String colCatalogNo  = mgr.readValue("ITEM8_CATALOG_NO","");
         double planMen = mgr.readNumberValue ("ITEM8_PLAN_MEN");
         if (isNaN(planMen)) planMen = 0;

         double planHrs = mgr.readNumberValue ("ITEM8_PLAN_HRS");
         if (isNaN(planHrs)) planHrs = 0;

         double nCost = getCostItem8(colOrgContract,colOrgCode,colRoleCode,colCatalogContract,colCatalogNo,mgr.readValue("ITEM8_WO_NO"));
         double nCostAmount = countCostAmount(nCost,planMen,planHrs);

         String nCostStr = mgr.getASPField("ITEM8_COST").formatNumber(nCost);
         String colAmountCostStr = mgr.getASPField("ITEM8_COSTAMOUNT").formatNumber(nCostAmount);

         String salesPartInfo[] = getSalesPartInfoItem8(false,false,colPriceList,"","");

         String strPriceListNo = salesPartInfo[0];
         String strDiscount = salesPartInfo[1];
         String strListPrice = salesPartInfo[2];
         String strSalesPriceAmount = salesPartInfo[3];
         String strDiscountedPrice = salesPartInfo[4];

         txt = (mgr.isEmpty(colCatalogDesc) ? "" : colCatalogDesc) + "^" +
               (mgr.isEmpty(nCostStr) ? "" : nCostStr) + "^" +
               (mgr.isEmpty(colAmountCostStr) ? "" : colAmountCostStr) + "^" +
               (mgr.isEmpty(colPriceList) ? "" : colPriceList) + "^" +
               (mgr.isEmpty(strDiscount) ? "" : strDiscount) + "^" +
               (mgr.isEmpty(strListPrice) ? "" : strListPrice) + "^" +
               (mgr.isEmpty(strSalesPriceAmount) ? "" : strSalesPriceAmount) + "^" +
               (mgr.isEmpty(strDiscountedPrice) ? "" : strDiscountedPrice) + "^" ;

         mgr.responseWrite(txt);
      }
      else if ("ITEM8_PRICE_LIST_NO".equals(val))
      {
         String colRoleCode = mgr.readValue("ITEM8_ROLE_CODE","");
         String colOrgContract = mgr.readValue("ITEM8_CONTRACT","");
         String colOrgCode = mgr.readValue("ITEM8_ORG_CODE","");
         String colCatalogContract = mgr.readValue("ITEM8_CATALOG_CONTRACT","");
         String colCatalogNo  = mgr.readValue("ITEM8_CATALOG_NO","");
         double planMen = mgr.readNumberValue ("ITEM8_PLAN_MEN");
         if (isNaN(planMen)) planMen = 0;

         double planHrs = mgr.readNumberValue ("ITEM8_PLAN_HRS");
         if (isNaN(planHrs)) planHrs = 0;

         double nCost = getCostItem8(colOrgContract,colOrgCode,colRoleCode,colCatalogContract,colCatalogNo,mgr.readValue("ITEM8_WO_NO"));
         double nCostAmount = countCostAmount(nCost,planMen,planHrs);

         String nCostStr = mgr.getASPField("ITEM8_COST").formatNumber(nCost);
         String colAmountCostStr = mgr.getASPField("ITEM8_COSTAMOUNT").formatNumber(nCostAmount);

         String salesPartInfo[] = getSalesPartInfoItem8(false,false,"","","");

         String strPriceListNo = salesPartInfo[0];
         String strDiscount = salesPartInfo[1];
         String strListPrice = salesPartInfo[2];
         String strSalesPriceAmount = salesPartInfo[3];
         String strDiscountedPrice = salesPartInfo[4];

         txt = (mgr.isEmpty(nCostStr) ? "": (nCostStr)) + "^" + 
               (mgr.isEmpty(colAmountCostStr) ? "": (colAmountCostStr)) + "^" + 
               (mgr.isEmpty(strPriceListNo) ? "" : strPriceListNo) + "^" +
               (mgr.isEmpty(strListPrice) ? "" : strListPrice) + "^" +
               (mgr.isEmpty(strSalesPriceAmount) ? "" : strSalesPriceAmount) + "^" +
               (mgr.isEmpty(strDiscount) ? "" : strDiscount) + "^" +
               (mgr.isEmpty(strDiscountedPrice) ? "" : strDiscountedPrice) + "^" ;

         mgr.responseWrite(txt);     
      }
      else if ("ITEM8_LISTPRICE".equals(val) || "ITEM8_DISCOUNT".equals(val))
      {
         double nBuyQtyDue = 0;
         double colDiscountedPrice = 0;
         double colSalesPriceAmount = 0;

         double planHrs = mgr.readNumberValue("ITEM8_PLAN_HRS");
         double planMen = mgr.readNumberValue("ITEM8_PLAN_MEN");

         if (isNaN(planMen) && !isNaN(planHrs))
            nBuyQtyDue = planHrs;
         else if (!isNaN(planMen) && !isNaN(planHrs))
            nBuyQtyDue = planHrs*planMen;


         double colDiscount = mgr.readNumberValue("ITEM8_DISCOUNT");
         if (isNaN(colDiscount)) colDiscount = 0;

         double colListPrice = mgr.readNumberValue("ITEM8_LISTPRICE");
         if (isNaN(colListPrice)) colListPrice = 0;

         colDiscountedPrice = (colListPrice - (colDiscount/100 * colListPrice));
         colSalesPriceAmount = (colListPrice - (colDiscount/100 * colListPrice)) * nBuyQtyDue; 

         String strDiscountedPrice = mgr.getASPField("ITEM8_DISCOUNTED_PRICE").formatNumber(colDiscountedPrice);
         String strSalesPriceAmount = mgr.getASPField("ITEM8_SALESPRICEAMOUNT").formatNumber(colSalesPriceAmount);

         txt = (mgr.isEmpty(strDiscountedPrice) ? "" : strDiscountedPrice) + "^" +
               (mgr.isEmpty(strSalesPriceAmount) ? "" : strSalesPriceAmount) + "^"; 

         mgr.responseWrite(txt);
      }
      else if ("ITEM8_COST".equals(val))
      {
         double nCost = mgr.readNumberValue("ITEM8_COST");
         if (isNaN(nCost))
            nCost = 0;

         String colOrgContract = mgr.readValue("ITEM8_CONTRACT","");
         String colOrgCode = mgr.readValue("ITEM8_ORG_CODE","");
         String colRoleCode = mgr.readValue("ITEM8_ROLE_CODE","");
         String sOrgContract = mgr.readValue("ITEM8_CONTRACT","");
         String sOrgCode = mgr.readValue("ITEM8_ORG_CODE","");
         String cataNum  = mgr.readValue("ITEM8_CATALOG_NO","");

         nCost = getCostValItem8(nCost,colOrgContract,colOrgCode,colRoleCode,sOrgContract,sOrgCode,cataNum);

         String nCostStr = mgr.getASPField("ITEM8_COST").formatNumber(nCost);
         String colAmountCostStr = countCostValItem8(nCost);

         txt = (mgr.isEmpty(nCostStr) ? "": (nCostStr))+ "^" + (mgr.isEmpty(colAmountCostStr) ? "": (colAmountCostStr))+ "^";

         mgr.responseWrite(txt);
      }

      else if ("ITEM8_DATE_FROM".equals(val))
      {
         buf = mgr.newASPBuffer();
         buf.addFieldItem("ITEM8_DATE_FROM",mgr.readValue("ITEM8_DATE_FROM",""));      
         DateFormat df= DateFormat.getInstance();
         Date dt = null;
         try
         {
            dt = df.parse(mgr.readValue("ITEM8_DATE_FROM",""));
         }
         catch (ParseException e)
         {
            e.printStackTrace();
         }
         buf.addFieldDateItem("ITEM8_DATE_FROM_MASKED", dt);
         txt = mgr.readValue("ITEM8_DATE_FROM","") + "^"
               +buf.getFieldValue("ITEM8_DATE_FROM_MASKED") + "^";
         mgr.responseWrite(txt);
      }
      else if ("ITEM8_DATE_TO".equals(val))
      {
         buf = mgr.newASPBuffer();
         buf.addFieldItem("ITEM8_DATE_TO",mgr.readValue("ITEM8_DATE_TO",""));    
         mgr.responseWrite(mgr.readValue("ITEM8_DATE_TO",""));
      }

      else if ("ITEM8_JOB_ID".equals(val))
      {
         cmd = trans.addCustomFunction("GETEMP","Work_Order_Job_API.Get_Executed_By","ITEM8_SIGN_ID");
         cmd.addParameter("ITEM8_WO_NO");
         cmd.addParameter("ITEM8_JOB_ID");

         cmd = trans.addCustomFunction("GETPERSON","Company_Emp_API.Get_Person_Id","ITEM8_SIGN");
         cmd.addParameter("ITEM8_COMPANY");
         cmd.addReference("ITEM8_SIGN_ID","GETEMP/DATA");

         trans = mgr.validate(trans);

         String emp_id = trans.getValue("GETEMP/DATA/SIGN_ID");
         String sign = trans.getValue("GETPERSON/DATA/SIGN");
         txt = (mgr.isEmpty(emp_id)?"":emp_id) + "^"+
               (mgr.isEmpty(sign)?"":sign) + "^";
         mgr.responseWrite(txt);
      }

      //---------------

      else if ("ITEM9_ORG_CODE".equals(val))
      {
         double orgco;
         double rolco;
         double cos;
         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[6];

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
               if (!mgr.isEmpty(mgr.readValue("ITEM9_SIGN")))
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
            sOrgContract = mgr.readValue("ITEM9_CONTRACT"); 
         }

         //bug id 59224,start
         trans.clear(); 
         cmd = trans.addCustomFunction("GETITEM9ORGDESC","Organization_Api.Get_Description","ITEM9_ORG_CODE_DESC");
         cmd.addParameter("ITEM9_CONTRACT",sOrgContract);
         cmd.addParameter("ITEM9_ORG_CODE",sOrgCode);


         //bug id 59224,end

         trans = mgr.validate(trans);

         String sitem9OrgDesc = trans.getValue("GETITEM9ORGDESC/DATA/ITEM9_ORG_CODE_DESC");

         txt = (mgr.isEmpty(sOrgCode)?"":sOrgCode)+"^"+(mgr.isEmpty(sOrgContract)?"":sOrgContract)+"^"+(mgr.isEmpty(sitem9OrgDesc)?"":sitem9OrgDesc)+"^";
         mgr.responseWrite(txt);
      }
      else if ("ITEM9_ROLE_CODE".equals(val))
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
         String new_role_code = mgr.readValue("ITEM9_ROLE_CODE","");

         if (new_role_code.indexOf("^",0)>0)
         {
            if (!mgr.isEmpty(mgr.readValue("ITEM9_SIGN")))
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
            sOrgContract = mgr.readValue("ITEM9_CONTRACT"); 
         }

         //bug id 59224,start
         trans.clear();
         cmd = trans.addCustomFunction("ITEM9ROLEDESC","ROLE_API.Get_Description","ITEM9_ROLE_CODE_DESC");
         cmd.addParameter("ITEM9_ROLE_CODE",sRoleCode);

         trans = mgr.validate(trans);
         String sitem9roleDesc = trans.getValue("ITEM9ROLEDESC/DATA/ITEM9_ROLE_CODE_DESC");

         //bug id 59224,end


         txt = (mgr.isEmpty(sRoleCode)?"":sRoleCode)+"^"+(mgr.isEmpty(sOrgContract)?"":sOrgContract)+"^"+(mgr.isEmpty(sitem9roleDesc)?"":sitem9roleDesc)+"^";
         mgr.responseWrite(txt);   
      }
      else if ("ITEM9_SIGN_ID".equals(val))
      {
         String signId = mgr.readValue("ITEM9_SIGN_ID");
         String sDefRole = ""; 

         cmd = trans.addCustomFunction("EMPEXIST","Employee_API.Is_Emp_Exist","ITEM9_TEMP");
         cmd.addParameter("ITEM9_COMPANY");
         cmd.addParameter("ITEM9_SIGN_ID");  

         trans = mgr.validate(trans);

         boolean bEmpExist = !("0".equals(trans.getValue("EMPEXIST/DATA/ITEM9_TEMP")));

         if (bEmpExist)
         {
            trans.clear();

            cmd = trans.addCustomFunction("GETSIGN","Company_Emp_API.Get_Person_Id","ITEM9_SIGN");
            cmd.addParameter("ITEM9_COMPANY");
            cmd.addParameter("ITEM9_SIGN_ID");  

            cmd = trans.addCustomFunction("GETDEFROLE","Employee_Role_API.Get_Default_Role","ITEM9_ROLE_CODE");
            cmd.addParameter("ITEM9_COMPANY");
            cmd.addParameter("ITEM9_SIGN_ID");  

            cmd = trans.addCustomFunction("GETORGCODE","Employee_API.Get_Organization","ITEM9_ORG_CODE");
            cmd.addParameter("ITEM9_COMPANY");
            cmd.addParameter("ITEM9_SIGN_ID");  

            trans = mgr.validate(trans);

            String sSign = trans.getValue("GETSIGN/DATA/SIGN");
            String sOrgCode = trans.getValue("GETORGCODE/DATA/ORG_CODE");
            String sDefRole1 = trans.getValue("GETDEFROLE/DATA/ROLE_CODE");
            if (!mgr.isEmpty(sDefRole1))
               sDefRole =sDefRole1;


            txt = (mgr.isEmpty(sSign) ? "": (sSign))+ "^" + 
                  (mgr.isEmpty(sDefRole) ? "": (sDefRole))+ "^"+ 
                  (mgr.isEmpty(sOrgCode) ? "": (sOrgCode))+ "^";

            mgr.responseWrite(txt);
         }
         else
         {
            txt = "" + "^" + "" + "^" + "" + "^";
            mgr.responseWrite(txt);
         }
      }
      else if ("ITEM9_SIGN".equals(val))
      {
         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[2];
         String emp_id = "";
         String sign = "";
         String sOrgContract = mgr.readValue("ITEM9_CONTRACT","");
         String sOrgCode = mgr.readValue("ITEM9_ORG_CODE","");
         String roleCode = mgr.readValue("ITEM9_ROLE_CODE");
         String depart = "";

         String new_sign = mgr.readValue("ITEM9_SIGN","");

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
            cmd = trans.addCustomFunction("SIGNID","Employee_API.Get_Max_Maint_Emp","ITEM9_SIGN_ID");                     
            cmd.addParameter("ITEM9_COMPANY");
            cmd.addParameter("ITEM9_SIGN");

            trans = mgr.validate(trans);
            emp_id = trans.getValue("SIGNID/DATA/SIGN_ID");
            sign = new_sign;

            trans.clear();
         }

         if (mgr.isEmpty(roleCode))
         {
            cmd = trans.addCustomFunction("DEFROLE","Employee_Role_API.Get_Default_Role","ITEM9_ROLE_CODE");
            cmd.addParameter("ITEM9_COMPANY");
            cmd.addParameter("ITEM9_SIGN_ID",emp_id);
         }

         if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
         {
            cmd = trans.addCustomFunction("EMPORGCONT","Employee_API.Get_Org_Contract","ITEM9_CONTRACT");
            cmd.addParameter("ITEM9_COMPANY");
            cmd.addParameter("ITEM9_SIGN_ID",emp_id);
         }

         if (mgr.isEmpty(sOrgCode))
         {
            cmd = trans.addCustomFunction("EMPORGCOD","Employee_API.Get_Organization","ITEM9_ORG_CODE");
            cmd.addParameter("ITEM9_COMPANY");
            cmd.addParameter("ITEM9_SIGN_ID",emp_id);
         }

         trans = mgr.validate(trans);

         if (mgr.isEmpty(roleCode))
            roleCode = trans.getValue("DEFROLE/DATA/ROLE_CODE");

         if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
            sOrgContract = trans.getValue("EMPORGCONT/DATA/CONTRACT");

         if (mgr.isEmpty(sOrgCode))
         {
            depart = trans.getValue("EMPORGCOD/DATA/ORG_CODE");
            sOrgCode = depart;
         }


         String colOrgCode  = depart;
         String colRoleCode = roleCode;
         trans.clear();
         //bug id 59224,start

         cmd = trans.addCustomFunction("GETITEM9ORGDESC","Organization_Api.Get_Description","ITEM9_ORG_CODE_DESC");
         cmd.addParameter("ITEM9_CONTRACT",sOrgContract);
         cmd.addParameter("ITEM9_ORG_CODE",sOrgCode);


         cmd = trans.addCustomFunction("ITEM9ROLEDESC","ROLE_API.Get_Description","ITEM9_ROLE_CODE_DESC");
         cmd.addParameter("ITEM9_ROLE_CODE",roleCode);


         trans = mgr.validate(trans);

         String sitem9OrgDesc = trans.getValue("GETITEM9ORGDESC/DATA/ITEM9_ORG_CODE_DESC");
         String sitem9roleDesc = trans.getValue("ITEM9ROLEDESC/DATA/ITEM9_ROLE_CODE_DESC");

         //bug id 59224,end


         txt = (mgr.isEmpty(emp_id) ? "": (emp_id))+ "^" + 
               (mgr.isEmpty(roleCode) ? "": (roleCode))+ "^"+ 
               (mgr.isEmpty(sOrgCode) ? "": (sOrgCode))+ "^"+ 
               (mgr.isEmpty(sign)? "": (sign))+ "^"+
               (mgr.isEmpty(sOrgContract)? "": (sOrgContract))+ "^"+
               (mgr.isEmpty(sitem9OrgDesc)? "": (sitem9OrgDesc))+ "^"+
               (mgr.isEmpty(sitem9roleDesc)? "": (sitem9roleDesc))+ "^";
         mgr.responseWrite(txt);

      }

      else if ("ITEM9_DATE_FROM".equals(val))
      {
         buf = mgr.newASPBuffer();
         buf.addFieldItem("ITEM9_DATE_FROM",mgr.readValue("ITEM9_DATE_FROM",""));      

         DateFormat df= DateFormat.getInstance();
         Date dt = null;
         try
         {
            dt = df.parse(mgr.readValue("ITEM9_DATE_FROM",""));
         }
         catch (ParseException e)
         {
            e.printStackTrace();
         }
         buf.addFieldDateItem("ITEM9_DATE_FROM_MASKED", dt);
         txt =buf.getFieldValue("ITEM9_DATE_FROM_MASKED") + "^";
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

         if (sContractId.indexOf("^") > -1)
         {
            String strAttr = sContractId;
            sContractId = strAttr.substring(0, strAttr.indexOf("^"));
            sLineNo = strAttr.substring(strAttr.indexOf("^") + 1, strAttr.length());
         }
         //Bug Id 70921, Start
         String sCoordinator = null;
         String sAgreementId = "";
         String sAgreementDesc = "";
         String connTypeDb = mgr.readValue("CONNECTION_TYPE_DB");
         String sWrkTypeId = "";
         String sWrkTypeDesc = "";
         String sOrgCode = "";
         String sOrgCodeDesc = "";
         String sReturnStr = "^^^^^^^^";
         //Bug Id 70921, End

          //Bug Id 84436, Start
         if (checksec("Sc_Service_Contract_API.Get_Customer_Id", 1))
         {
         cmd = trans.addCustomFunction("SCCONTRACTCUSTOMER","Sc_Service_Contract_API.Get_Customer_Id","CUSTOMER_NO");
         cmd.addParameter("CONTRACT_ID",sContractId);
         }
         //Bug Id 84436, End
         cmd = trans.addCustomFunction("CUSTNAME", "CUSTOMER_INFO_API.Get_Name", "CUSTOMERDESCRIPTION");
         cmd.addReference("CUSTOMER_NO","SCCONTRACTCUSTOMER/DATA");

         //  Bug 68947, Start
         //Bug Id 84436, Start
         if (checksec("Sc_Service_Contract_API.Get_Contract_Name", 1))
         {
         cmd = trans.addCustomFunction("SCCONTRACTNAME","SC_SERVICE_CONTRACT_API.Get_Contract_Name","CONTRACT_NAME");
         cmd.addParameter("CONTRACT_ID",sContractId);
         }
          if (checksec("PSC_CONTR_PRODUCT_API.Get_Description", 1))
         {
         cmd = trans.addCustomFunction("PSCLINEDESC", "PSC_CONTR_PRODUCT_API.Get_Description", "LINE_DESC");
         cmd.addParameter("CONTRACT_ID",sContractId);
         cmd.addParameter("LINE_NO",sLineNo);
          }
         //Bug Id 84436, End
         
         //Bug Id 70921, Start
         if (checksec("Sc_Service_Contract_API.Get_Authorize_Code", 1))
         {
            cmd = trans.addCustomFunction("GETCOORDINATOR", "Sc_Service_Contract_API.Get_Authorize_Code", "AUTHORIZE_CODE");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }

         if (checksec("PSC_CONTR_PRODUCT_API.Get_Work_Type_Id", 1))
         {
            cmd = trans.addCustomFunction("GETWORKTYPE", "PSC_CONTR_PRODUCT_API.Get_Work_Type_Id", "WORK_TYPE_ID");
            cmd.addParameter("CONTRACT_ID", sContractId);
            cmd.addParameter("LINE_NO", sLineNo);
         }

         if (checksec("WORK_TYPE_API.Get_Description", 1))
         {
            cmd = trans.addCustomFunction("GETWORKTYPEDESC", "WORK_TYPE_API.Get_Description", "WORKTYPEDESCRIPTION");
            cmd.addParameter("WORK_TYPE_ID");
         }

         if ("VIM".equals(connTypeDb))
         {
            if (checksec("PSC_CONTR_PRODUCT_API.Get_Work_Shop_Code", 1))
            {
               cmd = trans.addCustomFunction("GETORGCODE", "PSC_CONTR_PRODUCT_API.Get_Work_Shop_Code", "ORG_CODE");
               cmd.addParameter("CONTRACT_ID", sContractId);
               cmd.addParameter("LINE_NO", sLineNo);
            }
         }
         else if (checksec("PSC_CONTR_PRODUCT_API.Get_Maint_Org", 1))
         {
            cmd = trans.addCustomFunction("GETORGCODE", "PSC_CONTR_PRODUCT_API.Get_Maint_Org", "ORG_CODE");
            cmd.addParameter("CONTRACT_ID", sContractId);
            cmd.addParameter("LINE_NO", sLineNo);
         }


         if (checksec("ORGANIZATION_API.Get_Description", 1))
         {
            cmd = trans.addCustomFunction("ORGCODEDESC", "ORGANIZATION_API.Get_Description", "ORGCODEDESCRIPTION");
            cmd.addParameter("CONTRACT");
            cmd.addParameter("ORG_CODE");
         }

         if (checksec("SC_SERVICE_CONTRACT_API.Exist", 1))
         {

            cmd = trans.addCustomCommand("EXIST_LINE", "SC_SERVICE_CONTRACT_API.Exist");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }

         if (checksec("SC_CONTRACT_AGREEMENT_API.Get_First_Agreement", 1))
         {
            cmd = trans.addCustomCommand("SCCONTRACTAGREEMENT", "SC_CONTRACT_AGREEMENT_API.Get_First_Agreement");
            cmd.addParameter("AGREEMENT_ID");
            cmd.addParameter("DESCRIPTION");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }
         //Bug Id 70921, End


         trans = mgr.validate(trans);

         String sCustNo   = trans.getValue("SCCONTRACTCUSTOMER/DATA/CUSTOMER_NO");
         String sCustName = trans.getValue("CUSTNAME/DATA/CUSTOMERDESCRIPTION");
         String sConName   = trans.getValue("SCCONTRACTNAME/DATA/CONTRACT_NAME");
         String sLineDesc = trans.getValue("PSCLINEDESC/DATA/LINE_DESC");

         //Bug Id 70921, Start
         sCoordinator = trans.getValue("GETCOORDINATOR/DATA/AUTHORIZE_CODE");
         sAgreementId = trans.getBuffer("SCCONTRACTAGREEMENT/DATA").getValue("AGREEMENT_ID");
         sAgreementDesc = trans.getBuffer("SCCONTRACTAGREEMENT/DATA").getValue("DESCRIPTION");
         sWrkTypeId = trans.getBuffer("GETWORKTYPE/DATA").getValue("WORK_TYPE_ID");
         sWrkTypeDesc = trans.getBuffer("GETWORKTYPEDESC/DATA").getValue("WORKTYPEDESCRIPTION");
         sOrgCode = trans.getBuffer("GETORGCODE/DATA").getValue("ORG_CODE");
         sOrgCodeDesc = trans.getBuffer("ORGCODEDESC/DATA").getValue("ORGCODEDESCRIPTION");

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
                      (mgr.isEmpty(sPlanHrs) ? "" : sPlanHrs) + "^^^^";

         if (!mgr.isEmpty(sContractId) && !mgr.isEmpty(sLineNo))
         {

            sReturnStr = getDatesForSrvconValidate(buf);
         }

         txt = (mgr.isEmpty(sContractId) ? "" : (sContractId)) + "^" 
               + (mgr.isEmpty(sLineNo) ? "" : (sLineNo)) + "^" 
               + (mgr.isEmpty(sConName) ? "" : sConName) + "^" 
               + (mgr.isEmpty(sLineDesc) ? "" : sLineDesc) + "^"
               + (mgr.isEmpty(sCustNo) ? "" : sCustNo) + "^" 
               + (mgr.isEmpty(sCustName)?"":sCustName)+"^" 
               + (mgr.isEmpty(sCoordinator) ? "" : sCoordinator) + "^"
               + (mgr.isEmpty(sAgreementId) ? "" : sAgreementId) + "^" 
               + (mgr.isEmpty(sAgreementDesc) ? "" : sAgreementDesc) + "^"
               + (mgr.isEmpty(sWrkTypeId) ? "" : sWrkTypeId) + "^" 
               + (mgr.isEmpty(sWrkTypeDesc) ? "" : sWrkTypeDesc) + "^" 
               + (mgr.isEmpty(sOrgCode) ? "" : sOrgCode) + "^" 
               + (mgr.isEmpty(sOrgCodeDesc) ? "" : sOrgCodeDesc) + "^"
               + sReturnStr;

         //Bug Id 70921, End

//  Bug 68947, End
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

         String sAgreementDesc = "";
         String connTypeDb = mgr.readValue("CONNECTION_TYPE_DB");
         String sWrkTypeId = "";
         String sWrkTypeDesc = "";
         String sOrgCode = "";
         String sOrgCodeDesc = "";
         String sReturnStr = "^^^^^^^^";
         //Bug Id 70921, End

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
          //Bug Id 84436, Start
         if (checksec("Sc_Service_Contract_API.Get_Contract_Name", 1))
         {
         cmd = trans.addCustomFunction("SCCONTRACTNAME","SC_SERVICE_CONTRACT_API.Get_Contract_Name","CONTRACT_NAME");
         cmd.addParameter("CONTRACT_ID",sContractId);
         }
         
         if (checksec("PSC_CONTR_PRODUCT_API.Get_Description", 1))
         {
         cmd = trans.addCustomFunction("PSCLINEDESC", "PSC_CONTR_PRODUCT_API.Get_Description", "LINE_DESC");
         cmd.addParameter("CONTRACT_ID",sContractId);
         cmd.addParameter("LINE_NO",sLineNo);
         }
          //Bug Id 84436, End

         //Bug Id 70921, Start
         if (checksec("Sc_Service_Contract_API.Get_Authorize_Code", 1))
         {
            cmd = trans.addCustomFunction("GETCOORDINATOR", "Sc_Service_Contract_API.Get_Authorize_Code", "AUTHORIZE_CODE");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }

         if (checksec("PSC_CONTR_PRODUCT_API.Get_Work_Type_Id", 1))
         {
            cmd = trans.addCustomFunction("GETWORKTYPE", "PSC_CONTR_PRODUCT_API.Get_Work_Type_Id", "WORK_TYPE_ID");
            cmd.addParameter("CONTRACT_ID", sContractId);
            cmd.addParameter("LINE_NO", sLineNo);
         }

         if (checksec("WORK_TYPE_API.Get_Description", 1))
         {
            cmd = trans.addCustomFunction("GETWORKTYPEDESC", "WORK_TYPE_API.Get_Description", "WORKTYPEDESCRIPTION");
            cmd.addParameter("WORK_TYPE_ID");
         }

         if ("VIM".equals(connTypeDb))
         {
            if (checksec("PSC_CONTR_PRODUCT_API.Get_Work_Shop_Code", 1))
            {
               cmd = trans.addCustomFunction("GETORGCODE", "PSC_CONTR_PRODUCT_API.Get_Work_Shop_Code", "ORG_CODE");
               cmd.addParameter("CONTRACT_ID", sContractId);
               cmd.addParameter("LINE_NO", sLineNo);
            }
         }
         else if (checksec("PSC_CONTR_PRODUCT_API.Get_Maint_Org", 1))
         {
            cmd = trans.addCustomFunction("GETORGCODE", "PSC_CONTR_PRODUCT_API.Get_Maint_Org", "ORG_CODE");
            cmd.addParameter("CONTRACT_ID", sContractId);
            cmd.addParameter("LINE_NO", sLineNo);
         }


         if (checksec("ORGANIZATION_API.Get_Description", 1))
         {
            cmd = trans.addCustomFunction("ORGCODEDESC", "ORGANIZATION_API.Get_Description", "ORGCODEDESCRIPTION");
            cmd.addParameter("CONTRACT");
            cmd.addParameter("ORG_CODE");
         }

         if (checksec("SC_SERVICE_CONTRACT_API.Exist", 1))
         {

            cmd = trans.addCustomCommand("EXIST_LINE", "SC_SERVICE_CONTRACT_API.Exist");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }
         //Bug Id 70921, End


         trans = mgr.validate(trans);

         String sAgreementId = trans.getBuffer("SCCONTRACTAGREEMENT/DATA").getValue("AGREEMENT_ID");
         String sConName   = trans.getValue("SCCONTRACTNAME/DATA/CONTRACT_NAME");
         String sLineDesc = trans.getValue("PSCLINEDESC/DATA/LINE_DESC");

         //Bug Id 70921, Start
         sAgreementDesc = trans.getBuffer("SCCONTRACTAGREEMENT/DATA").getValue("DESCRIPTION");
         sWrkTypeId = trans.getBuffer("GETWORKTYPE/DATA").getValue("WORK_TYPE_ID");
         sWrkTypeDesc = trans.getBuffer("GETWORKTYPEDESC/DATA").getValue("WORKTYPEDESCRIPTION");
         sOrgCode = trans.getBuffer("GETORGCODE/DATA").getValue("ORG_CODE");
         sOrgCodeDesc = trans.getBuffer("ORGCODEDESC/DATA").getValue("ORGCODEDESCRIPTION");

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
                      (mgr.isEmpty(sPlanHrs) ? "" : sPlanHrs) + "^^^^";

         if (!mgr.isEmpty(sContractId) && !mgr.isEmpty(sLineNo))
         {

            sReturnStr = getDatesForSrvconValidate(buf);
         }

         txt =  (mgr.isEmpty(sContractId) ? "" : (sContractId)) + "^" + (mgr.isEmpty(sLineNo) ? "" : (sLineNo)) + "^" +
                (mgr.isEmpty(sConName)?"":sConName) + "^" + (mgr.isEmpty(sLineDesc)?"":sLineDesc) + "^" +
                (mgr.isEmpty(sAgreementId)?"":sAgreementId)+ "^" + (mgr.isEmpty(sAgreementDesc)?"":sAgreementDesc)+ "^" + 
                (mgr.isEmpty(sWrkTypeId) ? "" : sWrkTypeId) + "^" + (mgr.isEmpty(sWrkTypeDesc) ? "" : sWrkTypeDesc) + "^" +
                (mgr.isEmpty(sOrgCode) ? "" : sOrgCode) + "^" + (mgr.isEmpty(sOrgCodeDesc) ? "" : sOrgCodeDesc) + "^" +sReturnStr;

         //Bug Id 70921, End

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

         if (checksec("MAINTENANCE_PRIORITY_API.Exist", 1))
         {

            cmd = trans.addCustomCommand("EXIST_LINE", "MAINTENANCE_PRIORITY_API.Exist");
            cmd.addParameter("PRIORITY_ID", sPriorityId);
         }

         if (checksec("ACTIVE_SEPARATE_API.Get_Dates_For_Priority", 1))
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

         if (checksec("MAINTENANCE_PRIORITY_API.Get_Description", 1))
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

   public String countSalesPriceAmount()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      cmd = trans.addCustomFunction("WOINVTYPE","Work_Order_Invoice_Type_API.Encode","WOINVOICETYPE");
      cmd.addParameter("WORK_ORDER_INVOICE_TYPE");

      trans = mgr.validate(trans);

      String woInvType = trans.getValue("WOINVTYPE/DATA/WOINVOICETYPE");

      double nDiscount = mgr.readNumberValue("ITEM5_DISCOUNT");
      if (isNaN(nDiscount))
         nDiscount = 0;

      double nSalesPrice = mgr.readNumberValue("ITEM5_SALES_PRICE");
      if (isNaN(nSalesPrice))
         nSalesPrice = 0;

      double nQuantity = mgr.readNumberValue("QUANTITY");
      if (isNaN(nQuantity))
         nQuantity = 0;

      double nQuantityToInv = mgr.readNumberValue("QTY_TO_INVOICE");
      if (isNaN(nQuantityToInv))
         nQuantityToInv = 0;

      double salesPriceAmt = 0;

      if ("AR".equals(woInvType))
      {
         if (nDiscount == 0)
         {
            salesPriceAmt = nSalesPrice * nQuantity;
         }
         else
         {
            salesPriceAmt = nSalesPrice *  nQuantity;
            salesPriceAmt = salesPriceAmt - (nDiscount / 100 * salesPriceAmt);
         }
      }
      else if ("MINQ".equals(woInvType))
      {
         if (nQuantity > nQuantityToInv)
         {
            if (nDiscount == 0)
            {
               salesPriceAmt = nSalesPrice *  nQuantity;
            }
            else
            {
               salesPriceAmt = nSalesPrice *  nQuantity;
               salesPriceAmt = salesPriceAmt - (nDiscount / 100 * salesPriceAmt);
            }
         }
         else
         {
            if (nDiscount == 0)
            {
               salesPriceAmt = nSalesPrice *  nQuantityToInv;
            }
            else
            {
               salesPriceAmt = nSalesPrice *  nQuantityToInv;
               salesPriceAmt = salesPriceAmt - (nDiscount / 100 * salesPriceAmt);
            }
         }
      }
      else if ("MAXQ".equals(woInvType))
      {
         if (nQuantity < nQuantityToInv)
         {
            if (nDiscount == 0)
            {
               salesPriceAmt = nSalesPrice *  nQuantity;
            }
            else
            {
               salesPriceAmt = nSalesPrice *  nQuantity;
               salesPriceAmt = salesPriceAmt - (nDiscount / 100 * salesPriceAmt);
            } 
         }
         else
         {
            if (nDiscount == 0)
            {
               salesPriceAmt = nSalesPrice *  nQuantityToInv;
            }
            else
            {
               salesPriceAmt = nSalesPrice *  nQuantityToInv;
               salesPriceAmt = salesPriceAmt - (nDiscount / 100 * salesPriceAmt);
            }

         }
      }
      else if ("FL".equals(woInvType))
      {
         if (nDiscount == 0)
         {
            salesPriceAmt = nSalesPrice *  nQuantityToInv;
         }
         else
         {
            salesPriceAmt = nSalesPrice *  nQuantityToInv;
            salesPriceAmt = salesPriceAmt - (nDiscount / 100 * salesPriceAmt);
         }
      }
      else if (mgr.isEmpty(woInvType))
      {
         if (nDiscount == 0)
         {
            salesPriceAmt = nSalesPrice *  nQuantityToInv;
         }
         else
         {
            salesPriceAmt = nSalesPrice *  nQuantityToInv;
            salesPriceAmt = salesPriceAmt - (nDiscount / 100 * salesPriceAmt);
         } 
      }

      String salesPriceAmtStr = mgr.getASPField("ITEM5_SALES_PRICE_AMOUNT").formatNumber(salesPriceAmt);
      return salesPriceAmtStr;
   }


   public double getCostVal(double nCost,String colOrgContract,String colOrgCode,String colRoleCode,String sOrgContract,String sOrgCode,String colCataNo)
   {
      ASPManager mgr = getASPManager();

      double nListprice = 0;
      String nCostStr = "";

      if (nCost != 0 &&   mgr.isEmpty(colRoleCode))
         return nCost;

      if (!mgr.isEmpty(colRoleCode))
      {
         trans.clear();

         cmd = trans.addCustomFunction("ROLECOS","Role_API.Get_Role_Cost","COST");
         cmd.addParameter("ROLE_CODE",colRoleCode);

         trans = mgr.validate(trans);

         nCost = trans.getNumberValue("ROLECOS/DATA/COST");
         if (isNaN(nCost))
            nCost = 0;
      }

      if (nCost == 0 &&  !mgr.isEmpty(colOrgCode) && !mgr.isEmpty(colOrgContract))
      {
         trans.clear();

         cmd = trans.addCustomFunction("ORGCOS","Organization_API.Get_Org_Cost","COST");
         cmd.addParameter("ITEM2_CONTRACT",colOrgContract);
         cmd.addParameter("ITEM2_ORG_CODE",colOrgCode);

         trans = mgr.validate(trans);

         nCost = trans.getNumberValue("ORGCOS/DATA/COST");
         if (isNaN(nCost))
            nCost = 0;
      }

      if (nCost == 0 && !mgr.isEmpty(sOrgCode) && !mgr.isEmpty(sOrgContract))
      {
         trans.clear();

         cmd = trans.addCustomFunction("ORGCOS","Organization_API.Get_Org_Cost","COST");
         cmd.addParameter("ITEM2_CONTRACT",sOrgContract);
         cmd.addParameter("ITEM2_ORG_CODE",sOrgCode);

         trans = mgr.validate(trans);

         nCost = trans.getNumberValue("ORGCOS/DATA/COST");
         if (isNaN(nCost))
            nCost = 0;
      }

      if (nCost == 0 &&  ! mgr.isEmpty(colCataNo))
      {
         //Bug 69392, start
         if (mgr.isModuleInstalled("ORDER"))
         {
            cmd = trans.addCustomFunction("SALECOST","Sales_Part_API.Get_Cost","COST");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addParameter("CATALOG_NO");

            trans = mgr.validate(trans);

            nCost = trans.getNumberValue("SALECOST/DATA/COST"); 
         }
         //Bug 69392, end
         if (isNaN(nCost))
            nCost = 0;
      }
      return nCost;
   }

   //method used for cost calculations for Itemblock8
   public double getCostValItem8(double nCost,String colOrgContract,String colOrgCode,String colRoleCode,String sOrgContract,String sOrgCode,String colCataNo)
   {
      ASPManager mgr = getASPManager();

      double nListprice = 0;
      String nCostStr = "";

      if (nCost != 0 &&   mgr.isEmpty(colRoleCode))
         return nCost;

      if (!mgr.isEmpty(colRoleCode))
      {
         trans.clear();

         cmd = trans.addCustomFunction("ROLECOS","Role_API.Get_Role_Cost","ITEM8_COST");
         cmd.addParameter("ITEM8_ROLE_CODE",colRoleCode);

         trans = mgr.validate(trans);

         nCost = trans.getNumberValue("ROLECOS/DATA/ITEM8_COST");
         if (isNaN(nCost))
            nCost = 0;
      }

      if (nCost == 0 &&  !mgr.isEmpty(colOrgCode) && !mgr.isEmpty(colOrgContract))
      {
         trans.clear();

         cmd = trans.addCustomFunction("ORGCOS","Organization_API.Get_Org_Cost","ITEM8_COST");
         cmd.addParameter("ITEM8_CONTRACT",colOrgContract);
         cmd.addParameter("ITEM8_ORG_CODE",colOrgCode);

         trans = mgr.validate(trans);

         nCost = trans.getNumberValue("ORGCOS/DATA/ITEM8_COST");
         if (isNaN(nCost))
            nCost = 0;
      }

      if (nCost == 0 && !mgr.isEmpty(sOrgCode) && !mgr.isEmpty(sOrgContract))
      {
         trans.clear();

         cmd = trans.addCustomFunction("ORGCOS","Organization_API.Get_Org_Cost","ITEM8_COST");
         cmd.addParameter("ITEM8_CONTRACT",sOrgContract);
         cmd.addParameter("ITEM8_ORG_CODE",sOrgCode);

         trans = mgr.validate(trans);

         nCost = trans.getNumberValue("ORGCOS/DATA/ITEM8_COST");
         if (isNaN(nCost))
            nCost = 0;
      }

      if (nCost == 0 &&  ! mgr.isEmpty(colCataNo))
      {
         //Bug 69392, start
         if (mgr.isModuleInstalled("ORDER"))
         {
            cmd = trans.addCustomFunction("SALECOST","Sales_Part_API.Get_Cost","ITEM8_COST");
            cmd.addParameter("ITEM8_CATALOG_CONTRACT");
            cmd.addParameter("ITEM8_CATALOG_NO");

            trans = mgr.validate(trans);

            nCost = trans.getNumberValue("SALECOST/DATA/ITEM8_COST"); 
         }
         //Bug 69392, end
         if (isNaN(nCost))
            nCost = 0;
      }
      return nCost;
   }



   public String countCostVal(double nCost)
   {
      ASPManager mgr = getASPManager();

      double nListprice = nCost;

      double colPlanHrs = mgr.readNumberValue("ITEM2_PLAN_HRS");
      if (isNaN(colPlanHrs))
         colPlanHrs = 0;

      double colPlanMen = mgr.readNumberValue("PLAN_MEN");
      if (isNaN(colPlanMen))
         colPlanMen = 0;

      double colAmountCost;

      if (colPlanHrs == 0)
         colAmountCost = 0;
      else
      {
         if (colPlanMen == 0)
            colAmountCost = nListprice * colPlanHrs;
         else
            colAmountCost = nListprice * colPlanHrs * colPlanMen;
      }
      String colAmountCostStr = mgr.getASPField("COSTAMOUNT").formatNumber(colAmountCost);
      return colAmountCostStr;
   }



//method used for cost calculations for Itemblock8
   public String countCostValItem8(double nCost)
   {
      ASPManager mgr = getASPManager();

      double nListprice = nCost;

      double colPlanHrs = mgr.readNumberValue("ITEM8_PLAN_HRS");
      if (isNaN(colPlanHrs))
         colPlanHrs = 0;

      double colPlanMen = mgr.readNumberValue("ITEM8_PLAN_MEN");
      if (isNaN(colPlanMen))
         colPlanMen = 0;

      double colAmountCost;

      if (colPlanHrs == 0)
         colAmountCost = 0;
      else
      {
         if (colPlanMen == 0)
            colAmountCost = nListprice * colPlanHrs;
         else
            colAmountCost = nListprice * colPlanHrs * colPlanMen;
      }
      String colAmountCostStr = mgr.getASPField("ITEM8_COSTAMOUNT").formatNumber(colAmountCost);
      return colAmountCostStr;
   }



   private double getCost(String orgContract,String orgCode,String roleCode,String catalogContract,String catalogNo, String woNo)
   {
      ASPManager mgr = getASPManager();
      double cost;

      trans.clear();

      if (mgr.isEmpty(orgCode) && mgr.isEmpty(roleCode))
      {
         cmd = trans.addCustomFunction("GETORG","Active_Work_Order_API.Get_Org_Code","ITEM2_ORG_CODE");
         cmd.addParameter("ITEM2_WO_NO",woNo);

         cmd = trans.addCustomFunction("GETORGSITE","Active_Work_Order_API.Get_Contract","ITEM2_CONTRACT");
         cmd.addParameter("ITEM2_WO_NO",woNo);

         cmd = trans.addCustomFunction("GETORGCOST","Organization_API.Get_Org_Cost","ORGCOST");
         cmd.addReference("ITEM2_CONTRACT","GETORGSITE/DATA");
         cmd.addReference("ITEM2_ORG_CODE","GETORG/DATA");
      }
      else
      {
         cmd = trans.addCustomFunction("GETCOSTFUNC","Work_Order_Planning_Util_Api.Get_Cost","COST");
         cmd.addParameter("ITEM2_ORG_CODE",orgCode);
         cmd.addParameter("ROLE_CODE",roleCode);
         cmd.addParameter("CATALOG_CONTRACT",catalogContract);
         cmd.addParameter("CATALOG_NO",catalogNo);
         cmd.addParameter("ITEM2_CONTRACT",orgContract);
      } 

      trans = mgr.perform(trans);

      if (mgr.isEmpty(orgCode) && mgr.isEmpty(roleCode))
         cost = trans.getNumberValue ("GETORGCOST/DATA/ORGCOST");
      else
         cost = trans.getNumberValue ("GETCOSTFUNC/DATA/COST");

      if (isNaN(cost)) cost = 0;

      return cost;
   }

   //method used for cost calculations for Itemblock8
   private double getCostItem8(String orgContract,String orgCode,String roleCode,String catalogContract,String catalogNo, String woNo)
   {
      ASPManager mgr = getASPManager();
      double cost;

      trans.clear();

      if (mgr.isEmpty(orgCode) && mgr.isEmpty(roleCode))
      {
         cmd = trans.addCustomFunction("GETORG","Active_Work_Order_API.Get_Org_Code","ITEM8_ORG_CODE");
         cmd.addParameter("ITEM8_WO_NO",woNo);

         cmd = trans.addCustomFunction("GETORGSITE","Active_Work_Order_API.Get_Contract","ITEM8_CONTRACT");
         cmd.addParameter("ITEM8_WO_NO",woNo);

         cmd = trans.addCustomFunction("GETORGCOST","Organization_API.Get_Org_Cost","ITEM8_ORGCOST");
         cmd.addReference("ITEM8_CONTRACT","GETORGSITE/DATA");
         cmd.addReference("ITEM8_ORG_CODE","GETORG/DATA");
      }
      else
      {
         cmd = trans.addCustomFunction("GETCOSTFUNC","Work_Order_Planning_Util_Api.Get_Cost","ITEM8_COST");
         cmd.addParameter("ITEM8_ORG_CODE",orgCode);
         cmd.addParameter("ITEM8_ROLE_CODE",roleCode);
         cmd.addParameter("ITEM8_CATALOG_CONTRACT",catalogContract);
         cmd.addParameter("ITEM8_CATALOG_NO",catalogNo);
         cmd.addParameter("ITEM8_CONTRACT",orgContract);
      } 

      trans = mgr.perform(trans);

      if (mgr.isEmpty(orgCode) && mgr.isEmpty(roleCode))
         cost = trans.getNumberValue ("GETORGCOST/DATA/ITEM8_ORGCOST");
      else
         cost = trans.getNumberValue ("GETCOSTFUNC/DATA/ITEM8_COST");

      if (isNaN(cost)) cost = 0;

      return cost;
   }


   private double countCostAmount(double cost, double planMen, double planHrs)
   {
      ASPManager mgr = getASPManager();

      double costAmount = 0;

      if (isNaN(planHrs) || (planHrs==0))
         costAmount = 0;
      else
      {
         if (isNaN(planMen) || (planMen==0))
            costAmount = cost*planHrs;
         else
            costAmount = cost*planHrs*planMen;
      }

      if (isNaN(costAmount)) costAmount = 0;

      return costAmount;
   }

   private String[] getSalesPartInfo(boolean clientSalesPrice, boolean clientDiscount, String priceListNo, String catalogContract, String catalogNo)
   {
      ASPManager mgr = getASPManager();

      String outArr[] = new String[5];
      String txtOut = "";

      secBuff = mgr.newASPTransactionBuffer();
      secBuff.addSecurityQuery("Company_Finance_API","Get_Currency_Code");
      secBuff.addSecurityQuery("Customer_Order_Pricing_API","Get_Valid_Price_List,Get_Sales_Part_Price_Info");

      secBuff = mgr.perform(secBuff);

      if (secBuff.getSecurityInfo().itemExists("Company_Finance_API.Get_Currency_Code") &&
          secBuff.getSecurityInfo().itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List"))
      {

         double nBuyQtyDue = 0;

         double planHrs = mgr.readNumberValue("ITEM2_PLAN_HRS");
         double planMen = mgr.readNumberValue("PLAN_MEN");

         if (isNaN(planMen) && !isNaN(planHrs))
            nBuyQtyDue = planHrs;
         else if (!isNaN(planMen) && !isNaN(planHrs))
            nBuyQtyDue = planHrs*planMen;

         if (mgr.isEmpty(priceListNo))
            priceListNo = mgr.readValue("PRICE_LIST_NO");

         String nBuyQtyDueStr = mgr.getASPField("BUY_QTY_DUE").formatNumber(nBuyQtyDue);

         trans.clear();

         cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
         cmd.addParameter("BASE_UNIT_PRICE","0");
         cmd.addParameter("SALE_UNIT_PRICE","0");
         cmd.addParameter("DISCOUNT","0");
         cmd.addParameter("CURRENCY_RATE","0");
         if (mgr.isEmpty(catalogContract))
            cmd.addParameter("CATALOG_CONTRACT");
         else
            cmd.addParameter("CATALOG_CONTRACT",catalogContract);
         if (mgr.isEmpty(catalogNo))
            cmd.addParameter("CATALOG_NO");
         else
            cmd.addParameter("CATALOG_NO",catalogNo);
         cmd.addParameter("CUSTOMER_NO");
         cmd.addParameter("AGREEMENT_ID");
         cmd.addParameter("PRICE_LIST_NO",priceListNo);
         cmd.addParameter("BUY_QTY_DUE",nBuyQtyDueStr);

         trans = mgr.validate(trans);

         priceListNo = trans.getValue("PRICEINFO/DATA/PRICE_LIST_NO");

         double nSaleUnitPrice = trans.getNumberValue("PRICEINFO/DATA/SALE_UNIT_PRICE");

         if (isNaN(nSaleUnitPrice))
            nSaleUnitPrice = 0;

         double colDiscount = mgr.readNumberValue("DISCOUNT");
         double nDiscount = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");

         if (!clientDiscount)
            colDiscount = nDiscount;

         if (!(isNaN(nDiscount) ))
            colDiscount = nDiscount;

         if (isNaN(nDiscount))
            nDiscount = 0;

         if (isNaN(colDiscount))
            colDiscount = nDiscount;

         double colDiscountedPrice = 0;
         double colSalesPriceAmount = 0;

         double colListPrice = mgr.readNumberValue("LISTPRICE");
         if (!clientSalesPrice) colListPrice = nSaleUnitPrice;

         if (isNaN(colListPrice))
         {
            colListPrice = nSaleUnitPrice;
            colDiscountedPrice = (nSaleUnitPrice - (colDiscount/100 * nSaleUnitPrice));
            colSalesPriceAmount = (nSaleUnitPrice - (colDiscount/100 * nSaleUnitPrice)) * nBuyQtyDue; 
         }
         else
         {
            if (colDiscount==0)
            {
               colDiscountedPrice = colListPrice;
               colSalesPriceAmount = colListPrice * nBuyQtyDue;
            }
            else
            {
               colDiscountedPrice = (colListPrice - (colDiscount/100 * colListPrice));
               colSalesPriceAmount = (colListPrice - (colDiscount/100 * colListPrice)) * nBuyQtyDue; 
            }
         }

         String strDiscount = mgr.getASPField("DISCOUNT").formatNumber(colDiscount);          
         String strListPrice = mgr.getASPField("LISTPRICE").formatNumber(colListPrice);          
         String strSalesPriceAmount = mgr.getASPField("SALESPRICEAMOUNT").formatNumber(colSalesPriceAmount);          
         String strDiscountedPrice = mgr.getASPField("DISCOUNTED_PRICE").formatNumber(colDiscountedPrice);          

         outArr[0] = priceListNo;
         outArr[1] = strDiscount;
         outArr[2] = strListPrice;
         outArr[3] = strSalesPriceAmount;
         outArr[4] = strDiscountedPrice;

         txtOut = (mgr.isEmpty(priceListNo) ? "": (priceListNo)) + "^" + 
                  (mgr.isEmpty(strDiscount) ? "": (strDiscount)) + "^" + 
                  (mgr.isEmpty(strListPrice) ? "": (strListPrice)) + "^" + 
                  (mgr.isEmpty(strSalesPriceAmount) ? "": (strSalesPriceAmount)) + "^" +
                  (mgr.isEmpty(strDiscountedPrice) ? "": (strDiscountedPrice));
      }
      else
      {
         outArr[0] = mgr.readValue("PRICE_LIST_NO");
         outArr[1] = mgr.readValue("DISCOUNT");
         outArr[2] = mgr.readValue("LISTPRICE");
         outArr[3] = mgr.readValue("SALESPRICEAMOUNT");
         outArr[4] = mgr.readValue("DISCOUNTED_PRICE");

         txtOut = mgr.readValue("PRICE_LIST_NO") + "^" +
                  mgr.readValue("DISCOUNT") + "^" +
                  mgr.readValue("LISTPRICE") + "^" +
                  mgr.readValue("SALESPRICEAMOUNT") + "^" +
                  mgr.readValue("DISCOUNTED_PRICE");
      }

      return outArr;
   }

   //method used for cost calculations for Itemblock8
   private String[] getSalesPartInfoItem8(boolean clientSalesPrice, boolean clientDiscount, String priceListNo, String catalogContract, String catalogNo)
   {
      ASPManager mgr = getASPManager();

      String outArr[] = new String[5];
      String txtOut = "";

      secBuff = mgr.newASPTransactionBuffer();
      secBuff.addSecurityQuery("Company_Finance_API","Get_Currency_Code");
      secBuff.addSecurityQuery("Customer_Order_Pricing_API","Get_Valid_Price_List,Get_Sales_Part_Price_Info");

      secBuff = mgr.perform(secBuff);

      if (secBuff.getSecurityInfo().itemExists("Company_Finance_API.Get_Currency_Code") &&
          secBuff.getSecurityInfo().itemExists("Customer_Order_Pricing_API.Get_Valid_Price_List"))
      {

         double nBuyQtyDue = 0;

         double planHrs = mgr.readNumberValue("ITEM8_PLAN_HRS");
         double planMen = mgr.readNumberValue("ITEM8_PLAN_MEN");

         if (isNaN(planMen) && !isNaN(planHrs))
            nBuyQtyDue = planHrs;
         else if (!isNaN(planMen) && !isNaN(planHrs))
            nBuyQtyDue = planHrs*planMen;

         if (mgr.isEmpty(priceListNo))
            priceListNo = mgr.readValue("ITEM8_PRICE_LIST_NO");

         String nBuyQtyDueStr = mgr.getASPField("ITEM8_BUY_QTY_DUE").formatNumber(nBuyQtyDue);

         trans.clear();

         cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
         cmd.addParameter("ITEM8_BASE_UNIT_PRICE","0");
         cmd.addParameter("ITEM8_SALE_UNIT_PRICE","0");
         cmd.addParameter("ITEM8_DISCOUNT","0");
         cmd.addParameter("ITEM8_CURRENCY_RATE","0");
         if (mgr.isEmpty(catalogContract))
            cmd.addParameter("ITEM8_CATALOG_CONTRACT");
         else
            cmd.addParameter("ITEM8_CATALOG_CONTRACT",catalogContract);
         if (mgr.isEmpty(catalogNo))
            cmd.addParameter("ITEM8_CATALOG_NO");
         else
            cmd.addParameter("ITEM8_CATALOG_NO",catalogNo);
         cmd.addParameter("CUSTOMER_NO");
         cmd.addParameter("AGREEMENT_ID");
         cmd.addParameter("ITEM8_PRICE_LIST_NO",priceListNo);
         cmd.addParameter("ITEM8_BUY_QTY_DUE",nBuyQtyDueStr);

         trans = mgr.validate(trans);

         priceListNo = trans.getValue("PRICEINFO/DATA/PRICE_LIST_NO");
         double nSaleUnitPrice = trans.getNumberValue("PRICEINFO/DATA/ITEM8_SALE_UNIT_PRICE");
         if (isNaN(nSaleUnitPrice))
            nSaleUnitPrice = 0;

         double colDiscount = mgr.readNumberValue("ITEM8_DISCOUNT");
         double nDiscount = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");

         if (!clientDiscount)
            colDiscount = nDiscount;

         if (!(isNaN(nDiscount) ))
            colDiscount = nDiscount;

         if (isNaN(nDiscount))
            nDiscount = 0;

         if (isNaN(colDiscount))
            colDiscount = nDiscount;

         double colDiscountedPrice = 0;
         double colSalesPriceAmount = 0;

         double colListPrice = mgr.readNumberValue("ITEM8_LISTPRICE");
         if (!clientSalesPrice) colListPrice = nSaleUnitPrice;

         if (isNaN(colListPrice))
         {
            colListPrice = nSaleUnitPrice;
            colDiscountedPrice = (nSaleUnitPrice - (colDiscount/100 * nSaleUnitPrice));
            colSalesPriceAmount = (nSaleUnitPrice - (colDiscount/100 * nSaleUnitPrice)) * nBuyQtyDue; 
         }
         else
         {
            if (colDiscount==0)
            {
               colDiscountedPrice = colListPrice;
               colSalesPriceAmount = colListPrice * nBuyQtyDue;
            }
            else
            {
               colDiscountedPrice = (colListPrice - (colDiscount/100 * colListPrice));
               colSalesPriceAmount = (colListPrice - (colDiscount/100 * colListPrice)) * nBuyQtyDue; 
            }
         }

         String strDiscount = mgr.getASPField("ITEM8_DISCOUNT").formatNumber(colDiscount);          
         String strListPrice = mgr.getASPField("ITEM8_LISTPRICE").formatNumber(colListPrice);          
         String strSalesPriceAmount = mgr.getASPField("ITEM8_SALESPRICEAMOUNT").formatNumber(colSalesPriceAmount);          
         String strDiscountedPrice = mgr.getASPField("ITEM8_DISCOUNTED_PRICE").formatNumber(colDiscountedPrice);          

         outArr[0] = priceListNo;
         outArr[1] = strDiscount;
         outArr[2] = strListPrice;
         outArr[3] = strSalesPriceAmount;
         outArr[4] = strDiscountedPrice;

         txtOut = (mgr.isEmpty(priceListNo) ? "": (priceListNo)) + "^" + 
                  (mgr.isEmpty(strDiscount) ? "": (strDiscount)) + "^" + 
                  (mgr.isEmpty(strListPrice) ? "": (strListPrice)) + "^" + 
                  (mgr.isEmpty(strSalesPriceAmount) ? "": (strSalesPriceAmount)) + "^" +
                  (mgr.isEmpty(strDiscountedPrice) ? "": (strDiscountedPrice));
      }
      else
      {
         outArr[0] = mgr.readValue("ITEM8_PRICE_LIST_NO");
         outArr[1] = mgr.readValue("ITEM8_DISCOUNT");
         outArr[2] = mgr.readValue("ITEM8_LISTPRICE");
         outArr[3] = mgr.readValue("ITEM8_SALESPRICEAMOUNT");
         outArr[4] = mgr.readValue("ITEM8_DISCOUNTED_PRICE");

         txtOut = mgr.readValue("ITEM8_PRICE_LIST_NO") + "^" +
                  mgr.readValue("ITEM8_DISCOUNT") + "^" +
                  mgr.readValue("ITEM8_LISTPRICE") + "^" +
                  mgr.readValue("ITEM8_SALESPRICEAMOUNT") + "^" +
                  mgr.readValue("ITEM8_DISCOUNTED_PRICE");
      }

      return outArr;
   }



   public void toolsFacilities()
   {
      ASPManager mgr = getASPManager();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      bOpenNewWindow = true;
      urlString = createTransferUrl("WorkOrderToolsFacilities.page", headset.getSelectedRows("WO_NO"));
      newWinHandle = "toolsFacilities";       
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
   }
   public void refreshForm()
   {
      ASPManager mgr = getASPManager();
      // headset.refreshRow();
      // okFindITEM3();
      okFindITEM6();
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

   //Bug 72644, Start
   public void optimizeSchedule()
   {
       ASPManager mgr = getASPManager();

       if (headlay.isMultirowLayout())
          headset.goTo(headset.getRowSelected());
       else
          headset.selectRow();

       urlString = createTransferUrl("OptimizeScheduleDlg.page", headset.getSelectedRows("WO_NO"));
       
       newWinHandle = "optimizeSchedule"; 
       newWinHeight = 275;
       newWinWidth  = 550;

       bOpenNewWindow = true;
   }

   public void refresh()
   {
       if (headlay.isSingleLayout())
           headset.refreshRow();
       else
           headset.refreshAllRows();
   }
   //Bug 72644, End

   public boolean CheckObjInRepairShop()
   {
      ASPManager mgr = getASPManager();
      String objState = "";
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
         return true;
      else
         return false;  
   }


   private String getDateTimeFormat(String type)
   {
      if ("JAVA".equals(type))
      {
         return "yyyy-MM-dd HH:mm:ss";
      }
      else if ("SQL".equals(type))
      {
         return "yyyy-MM-dd HH24:mi:ss";
      }
      else
         return getASPManager().getFormatMask("Datetime",true);
   }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR FUNCTIONS  --------------------------------
//-----------------------------------------------------------------------------

   public void okFind()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer retBuffer;
      String  curr_row_exists = "FALSE";

      trans.clear();

      q = trans.addQuery(headblk);
      q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");

      if (mgr.dataTransfered())
      {
         retBuffer =  mgr.getTransferedData();
         if (retBuffer.itemExists("CURR_ROW"))
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
         {
            q.addOrCondition(mgr.getTransferedData());
         }
      }

      q.setOrderByClause("WO_NO");
      q.includeMeta("ALL");

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
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTNODATA: No data found."));
         headset.clear();
      }

      if (headset.countRows() == 1)
      {
         if (tabs.getActiveTab() == 1)
            okFindITEM2();
         else if (tabs.getActiveTab() == 2)
            okFindITEM3();
         else if (tabs.getActiveTab() == 3)
            okFindITEM4();
         else if (tabs.getActiveTab() == 4)
            okFindITEM5();
         else if (tabs.getActiveTab() == 5)
            okFindITEM7();

         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         if ("1".equals(headset.getRow().getValue("EXCEPTION_EXISTS")))
            bException = true;
         else
            bException = false;
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
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }

   public void newRow()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addQuery("REPBY","SELECT Fnd_Session_API.Get_Fnd_User FROM DUAL");

      cmd = trans.addCustomFunction( "GETCON","User_Default_API.Get_Contract","CONTRACT1" );

      cmd = trans.addCustomFunction( "GETCOM","Site_API.Get_Company","COMPANY1" );
      cmd.addReference("CONTRACT1","GETCON/DATA");

      trans = mgr.perform(trans);

      String fndUser = trans.getValue("REPBY/DATA/GET_FND_USER");
      String comp =  trans.getValue("GETCOM/DATA/COMPANY1");

      trans.clear();

      cmd = trans.addEmptyCommand("HEAD","ACTIVE_SEPARATE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");

      cmd = trans.addCustomFunction("PERSONINFO", "Person_Info_API.Get_Id_For_User", "USERID" );
      cmd.addParameter("FNDUSER",fndUser);

      cmd = trans.addCustomFunction("REPBYNAME","PERSON_INFO_API.Get_Name","NAME");
      cmd.addReference("USERID","PERSONINFO/DATA");

      cmd = trans.addCustomFunction("REPBID","Company_Emp_API.Get_Max_Employee_Id","REPORTED_BY_ID");
      cmd.addParameter("COMPANY",comp);
      cmd.addReference("USERID","PERSONINFO/DATA");

      trans = mgr.perform(trans);

      String repBy = trans.getValue("PERSONINFO/DATA/USERID");   
      String repByName = trans.getValue("REPBYNAME/DATA/NAME");
      String    repById    =    trans.getValue(   "REPBID/DATA/REPORTED_BY_ID");

      data = trans.getBuffer("HEAD/DATA");

      data.setFieldItem("REPORTED_BY",repBy); 
      data.setFieldItem("NAME",repByName); 
      data.setFieldItem("REPORTED_BY_ID",repById);

      headset.addRow(data);
   }

   public void duplicateRow()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      headlay.setLayoutMode(headlay.NEW_LAYOUT);

      trans.clear();

      /* cmd = trans.addCustomFunction("GETCON","Maintenance_Configuration_API.Get_Connection_Type","CONNECTION_TYPE");

       cmd = trans.addCustomFunction("GETCON1","MAINT_CONNECTION_TYPE_API.Decode","CONNECTION_TYPE");
       cmd.addReference("CONNECTION_TYPE","GETCON/DATA");

       cmd = trans.addCustomFunction("GETCONDB","MAINT_CONNECTION_TYPE_API.Encode","CONNECTION_TYPE_DB");
       cmd.addParameter("CONNECTION_TYPE","GETCON/DATA");  */

      cmd = trans.addEmptyCommand("HEAD","ACTIVE_SEPARATE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("HEAD/DATA");

      data.setFieldItem("CONTRACT",headset.getValue("CONTRACT")); 
      data.setFieldItem("CONNECTION_TYPE",headset.getValue("CONNECTION_TYPE"));
      data.setFieldItem("CONNECTION_TYPE_DB",headset.getValue("CONNECTION_TYPE_DB"));
      if ("VIM".equals(headset.getValue("CONNECTION_TYPE_DB")))
         data.setFieldItem("MCH_CODE","");
      else
         data.setFieldItem("MCH_CODE",headset.getValue("MCH_CODE"));
      data.setFieldItem("MCH_CODE_CONTRACT",headset.getValue("MCH_CODE_CONTRACT"));
      data.setFieldItem("LATESTTRANSAC",headset.getValue("LATESTTRANSAC"));
      data.setFieldItem("GROUPID",headset.getValue("GROUPID"));
      data.setFieldItem("CRITICALITY",headset.getValue("CRITICALITY"));
      data.setFieldItem("ORG_CODE",headset.getValue("ORG_CODE"));
      data.setFieldItem("ERR_DESCR",headset.getValue("ERR_DESCR"));
      data.setFieldItem("PLAN_S_DATE",headset.getRow().getFieldValue("PLAN_S_DATE"));
      data.setFieldItem("PLAN_F_DATE",headset.getRow().getFieldValue("PLAN_F_DATE"));
      data.setFieldItem("PLAN_HRS",headset.getValue("PLAN_HRS"));
      data.setFieldItem("PLANNED_MAN_HRS",headset.getValue("PLANNED_MAN_HRS"));
      data.setFieldItem("CALL_CODE",headset.getValue("CALL_CODE"));
      data.setFieldItem("OP_STATUS_ID",headset.getValue("OP_STATUS_ID"));
      data.setFieldItem("PRIORITY_ID",headset.getValue("PRIORITY_ID"));
      data.setFieldItem("WORK_TYPE_ID",headset.getValue("WORK_TYPE_ID"));
      data.setFieldItem("WORK_DESCR_LO",headset.getValue("WORK_DESCR_LO"));
      data.setFieldItem("REPORTED_BY",headset.getValue("REPORTED_BY"));
      data.setFieldItem("REPAIR_FLAG",headset.getValue("REPAIR_FLAG"));
      data.setFieldItem("PREPARED_BY",headset.getValue("PREPARED_BY"));
      data.setFieldItem("PREPARED_BY_ID",headset.getValue("PREPARED_BY_ID"));
      data.setFieldItem("WORK_MASTER_SIGN",headset.getValue("WORK_MASTER_SIGN"));
      data.setFieldItem("WORK_MASTER_SIGN_ID",headset.getValue("WORK_MASTER_SIGN_ID"));
      data.setFieldItem("WORK_LEADER_SIGN",headset.getValue("WORK_LEADER_SIGN"));
      data.setFieldItem("WORK_LEADER_SIGN_ID",headset.getValue("WORK_LEADER_SIGN_ID"));
      data.setFieldItem("AUTHORIZE_CODE",headset.getValue("AUTHORIZE_CODE"));
      data.setFieldItem("COST_CENTER",headset.getValue("COST_CENTER"));
      data.setFieldItem("PROJECT_NO",headset.getValue("PROJECT_NO"));
      data.setFieldItem("OBJECT_NO",headset.getValue("OBJECT_NO"));
      data.setFieldItem("REG_DATE",headset.getRow().getFieldValue("REG_DATE"));
      data.setFieldItem("TEST_POINT_ID",headset.getValue("TEST_POINT_ID"));
      data.setFieldItem("QUOTATION_ID",headset.getValue("QUOTATION_ID"));
      data.setFieldItem("SALESMANCODE",headset.getValue("SALESMANCODE"));
      data.setFieldItem("CUSTOMER_NO",headset.getValue("CUSTOMER_NO"));
      data.setFieldItem("CREDITSTOP",headset.getValue("CREDITSTOP"));
      data.setFieldItem("STATEVAL",headset.getValue("STATEVAL"));
      data.setFieldItem("CONTACT",headset.getValue("CONTACT"));
      data.setFieldItem("PHONE_NO",headset.getValue("PHONE_NO"));
      data.setFieldItem("ADDRESS1",headset.getValue("ADDRESS1"));
      data.setFieldItem("ADDRESS2",headset.getValue("ADDRESS2"));
      data.setFieldItem("ADDRESS3",headset.getValue("ADDRESS3"));
      data.setFieldItem("ADDRESS4",headset.getValue("ADDRESS4"));
      data.setFieldItem("ADDRESS5",headset.getValue("ADDRESS5"));
      data.setFieldItem("ADDRESS6",headset.getValue("ADDRESS6"));
      data.setFieldItem("REPORTED_BY_ID",headset.getValue("REPORTED_BY_ID"));
      data.setFieldItem("WORKTYPEDESCRIPTION",headset.getValue("WORKTYPEDESCRIPTION"));
      data.setFieldItem("ERR_DISCOVER_CODE",headset.getValue("ERR_DISCOVER_CODE"));
      data.setFieldItem("ERR_SYMPTOM",headset.getValue("ERR_SYMPTOM"));
      data.setFieldItem("ERR_DESCR_LO",headset.getValue("ERR_DESCR_LO"));
      data.setFieldItem("COMPANY",headset.getValue("COMPANY"));
      data.setFieldItem("ACTION_CODE_ID",headset.getValue("ACTION_CODE_ID"));
      data.setFieldItem("MCHWORKCENTER",headset.getValue("MCH_CODE"));
      data.setFieldItem("MCHRESOURCENO",headset.getValue("MCHRESOURCENO"));
      data.setFieldItem("MCHRESOUCELOADHOURS",headset.getValue("MCHRESOUCELOADHOURS"));
      data.setFieldItem("AGREEMENT_ID",headset.getValue("AGREEMENT_ID"));
      data.setFieldItem("FAULT_REP_FLAG",headset.getValue("FAULT_REP_FLAG"));
      data.setFieldItem("REQUIRED_START_DATE",headset.getRow().getFieldValue("REQUIRED_START_DATE"));
      data.setFieldItem("REQUIRED_END_DATE",headset.getRow().getFieldValue("REQUIRED_END_DATE"));
      data.setFieldItem("WO_KEY_VALUE",headset.getValue("WO_KEY_VALUE"));
      data.setFieldItem("NORGANIZATIONORG_COST",headset.getValue("NORGANIZATIONORG_COST"));
      data.setFieldItem("NOTE",headset.getValue("NOTE"));
      data.setFieldItem("STD_TEXT",headset.getValue("STD_TEXT"));
      data.setFieldItem("MODULE",headset.getValue("MODULE"));
      data.setFieldItem("SITE_DATE",headset.getRow().getFieldValue("SITE_DATE"));
      data.setFieldItem("SET_TIME",headset.getValue("SET_TIME"));
      data.setFieldItem("EXEC_TIME",headset.getValue("EXEC_TIME"));
      data.setFieldItem("REPAIRFLAG",headset.getValue("REPAIRFLAG"));
      data.setFieldItem("CONTRACT1",headset.getValue("CONTRACT1"));
      data.setFieldItem("COMPANY1",headset.getValue("COMPANY1"));
      data.setFieldItem("USERID",headset.getValue("USERID"));
      data.setFieldItem("FNDUSER",headset.getValue("FNDUSER"));
      data.setFieldItem("INFO",headset.getValue("INFO"));
      data.setFieldItem("ACTION",headset.getValue("ACTION"));
      data.setFieldItem("ATTR",headset.getValue("ATTR"));
      data.setFieldItem("TRANSFERRED",headset.getValue("TRANSFERRED"));
      data.setFieldItem("HEAD_ISVALID",headset.getValue("HEAD_ISVALID"));
      data.setFieldItem("HEAD_FACSEQUENCE",headset.getValue("HEAD_FACSEQUENCE"));
      data.setFieldItem("ISVIM",headset.getValue("ISVIM"));
      data.setFieldItem("OBJ_SUP_WARRANTY",headset.getValue("OBJ_SUP_WARRANTY"));
      data.setFieldItem("SUP_WARR_DESC",headset.getValue("SUP_WARR_DESC"));
      data.setFieldItem("SUPPLIER",headset.getValue("SUPPLIER"));
      data.setFieldItem("SUP_WARRANTY",headset.getValue("SUP_WARRANTY"));
      data.setFieldItem("SUP_WARR_TYPE",headset.getValue("SUP_WARR_TYPE"));
      data.setFieldItem("DIFF",headset.getValue("DIFF"));
      data.setFieldItem("HEAD_CURRENCEY_CODE",headset.getValue("HEAD_CURRENCEY_CODE"));
      data.setFieldItem("UPDATE_PRICE",headset.getValue("UPDATE_PRICE"));
      data.setFieldItem("RECEIVE_ORDER_NO",headset.getValue("RECEIVE_ORDER_NO"));
      data.setFieldItem("IDENTITY_TYPE",headset.getValue("IDENTITY_TYPE"));
      data.setFieldItem("PARTY_TYPE",headset.getValue("PARTY_TYPE"));
      data.setFieldItem("CONNECTION_TYPE_DB",trans.getValue("GETCON1/DATA/CONNECTION_TYPE"));
      data.setFieldItem("HEAD_PART_NO",headset.getValue("HEAD_PART_NO"));
      data.setFieldItem("HEAD_SERIAL_NO",headset.getValue("HEAD_SERIAL_NO"));
      data.setFieldItem("ACTIVITY_SEQ",headset.getValue("ACTIVITY_SEQ"));
      data.setFieldItem("PROJECT_ID",headset.getValue("PROJECT_ID"));
      data.setFieldItem("INCLUDE_STANDARD",headset.getValue("INCLUDE_STANDARD"));
      data.setFieldItem("INCLUDE_PROJECT",headset.getValue("INCLUDE_PROJECT"));

      headset.addRow(data);

   }   

   public void duplicateITEM9()
   {
      ASPManager mgr = getASPManager();

      if (itemlay9.isMultirowLayout())
         itemset9.goTo(itemset9.getRowSelected());
      else
         itemset9.selectRow();

      itemlay9.setLayoutMode(itemlay9.NEW_LAYOUT);

      cmd = trans.addEmptyCommand("ITEM9","WORK_ORDER_ROLE_API.New__",itemblk9);
      cmd.setParameter("ITEM9_WO_NO",headset.getRow().getValue("WO_NO"));
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("ITEM9/DATA");
      data.setFieldItem("ITEM9_WO_NO",headset.getRow().getValue("WO_NO")); 
      data.setFieldItem("PARENT_ROW_NO",itemset2.getRow().getValue("ROW_NO")); 
      data.setFieldItem("ITEM9_COMPANY",itemset2.getRow().getValue("COMPANY"));
      data.setFieldItem("ITEM9_CONTRACT",itemset9.getRow().getValue("CONTRACT")); 
      data.setFieldItem("ITEM9_ORG_CODE",itemset9.getRow().getValue("ORG_CODE")); 
      data.setFieldItem("ITEM9_SIGN",itemset9.getRow().getValue("SIGN")); 
      data.setFieldItem("ITEM9_SIGN_ID",itemset9.getRow().getValue("SIGN_ID")); 
      data.setFieldItem("ITEM9_TEAM_CONTRACT",itemset9.getRow().getValue("TEAM_CONTRACT")); 


      itemset9.addRow(data);                        
   }



   public void saveReturn()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();
      String sPpExist = "";

      int currrow = headset.getCurrentRowNo();
      headset.changeRow();

      //Bug 93061,Start added mch_code_contract as a param
      trans1.clear();
      cmd = trans1.addCustomFunction("PPEXISTS","ACTIVE_SEPARATE_API.Exist_Pre_Postings","PP_EXISTS");
      cmd.addParameter("WO_NO", headset.getValue("WO_NO"));
      cmd.addParameter("MCH_CODE_CONTRACT", headset.getValue("MCH_CODE_CONTRACT"));
      cmd.addParameter("MCH_CODE", headset.getValue("MCH_CODE"));
      trans1 = mgr.perform(trans1);
      sPpExist = trans1.getValue("PPEXISTS/DATA/PP_EXISTS");
      //Bug 93061,End

      if (supWar)
      {
         temp = headset.getRow();
         temp.setValue("SUP_WARR_TYPE",rWarrType);
         temp.setValue("SUP_WARRANTY",rRawId);
         temp.setValue("OBJ_SUP_WARRANTY",rRowN);

         headset.setRow(temp);
         headset.setEdited("SUP_WARR_TYPE,SUP_WARRANTY,OBJ_SUP_WARRANTY");
         supWar = false;   
      }
      trans.clear();

      mgr.submit(trans);
      trans.clear();

      headset.goTo(currrow);
      okFindITEM2();
      okFindITEM3();
      okFindITEM4();      
      okFindITEM5();
      okFindITEM7();

      qrystr = mgr.getURL()+"?SEARCH=Y&WO_NO="+headset.getRow().getValue("WO_NO");

      if ("REMOVED".equals(sPpExist))
         mgr.showAlert("PCMWACTIVESEPARATE2SMPPREMOVED: Existing pre-posting values are not updated");
      else if ("CHANGED".equals(sPpExist))
         bPpChanged = true;
   }

   public void saveNew()
   {
      int currrow = headset.getCurrentRowNo();
      saveReturn();
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

   public void okFindITEM2()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      int headrowno = headset.getCurrentRowNo();
      q = trans.addQuery(itemblk2);
      q.addWhereCondition("WO_NO = ? AND PARENT_ROW_NO IS NULL");
      q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
      q.includeMeta("ALL");
      mgr.submit(trans);

      if (comBarAct)
      {
         if (itemset2.countRows() == 0 && "ITEM2.OkFind".equals(mgr.readValue("__COMMAND")))
         {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTNODATA: No data found."));
            itemset2.clear();
         }
      }
      headset.goTo(headrowno);
      if (itemset2.countRows() > 0)
         okFindITEM9();
   }

   public void countFindITEM2()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk2);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("WO_NO = ? AND PARENT_ROW_NO IS NULL");
      q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
      int headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);
      itemlay2.setCountValue(toInt(itemset2.getRow().getValue("N")));
      itemset2.clear();
      headset.goTo(headrowno);
   }

   public void newRowITEM2()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM2","WORK_ORDER_ROLE_API.New__",itemblk2);
      cmd.setParameter("ITEM2_WO_NO",headset.getRow().getValue("WO_NO"));
      cmd.setOption("ACTION","PREPARE");

      c = mgr.newASPCommand();      
      c.defineCustomFunction("Site_API.Get_Company");
      c.addLocalReference("COMPANY",c.OUT);
      c.addLocalReference("CONTRACT",c.IN);
      c.setOption("FORCE","Y");
      cmd.addPostCommand(c);

      cmd = trans.addCustomFunction("GETCURRANCYCODE","ACTIVE_SEPARATE_API.Get_Currency_Code","CURRENCY_CODE");
      cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

      cmd = trans.addCustomFunction("GETCATCONT","ACTIVE_SEPARATE_API.Get_Contract","CATALOG_CONTRACT");
      cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

      //bug ID 59224,start
      cmd = trans.addCustomFunction("GETITEM2ORGDESC","Organization_Api.Get_Description","ITEM2_ORG_CODE_DESC");
      cmd.addParameter("ITEM2_CONTRACT",headset.getRow().getValue("CONTRACT"));
      cmd.addParameter("ITEM2_ORG_CODE",headset.getRow().getValue("ORG_CODE"));

      trans = mgr.perform(trans);
      String sitem2OrgDesc = trans.getValue("GETITEM2ORGDESC/DATA/ITEM2_ORG_CODE_DESC");
      //bug ID 59224,end

      String compan = trans.getValue("ITEM2/DATA/COMPANY");
      String currencyCode = trans.getValue("GETCURRANCYCODE/DATA/CURRENCY_CODE");
      String catcont = trans.getValue("GETCATCONT/DATA/CATALOG_CONTRACT");

      data = trans.getBuffer("ITEM2/DATA");
      data.setFieldItem("ITEM2_WO_NO",headset.getRow().getValue("WO_NO")); 
      data.setFieldItem("ITEM2_ORG_CODE",headset.getRow().getValue("ORG_CODE")); 
      data.setFieldItem("ITEM2_ORG_CODE_DESC",sitem2OrgDesc);
      data.setFieldItem("ITEM2_CONTRACT",headset.getRow().getValue("CONTRACT")); 
      data.setFieldItem("ITEM2_COMPANY",compan);

      if (!mgr.isEmpty(headset.getRow().getValue("PLAN_S_DATE")))
      {

         data.setFieldDateItem("DATE_FROM",headset.getRow().getFieldDateValue("PLAN_S_DATE"));
         //data.setFieldItem("DATE_FROM",headset.getRow().getFieldValue("PLAN_S_DATE"));
         DateFormat df= DateFormat.getInstance();
         Date dt = null;
         try
         {
            dt = df.parse(headset.getRow().getFieldValue("PLAN_S_DATE"));
         }
         catch (ParseException e)
         {
            e.printStackTrace();
         }
         data.addFieldDateItem("DATE_FROM_MASKED", dt);
         data.setFieldDateItem("DATE_FROM_MASKED", dt);
      }

      //data.setFieldDateItem("DATE_FROM",headset.getRow().getFieldDateValue("PLAN_S_DATE"));
      data.setFieldDateItem("DATE_TO",headset.getRow().getFieldDateValue("PLAN_F_DATE"));
      data.setFieldItem("CURRENCY_CODE",currencyCode);
      data.setFieldItem("ITEM2_ROWSTATUS","NEW");
      data.setFieldItem("CATALOG_CONTRACT",catcont);
      itemset2.addRow(data);
   }

   public void editRowITEM2(){
       ASPManager mgr = getASPManager();

       data = itemset2.getRow();
       if (!mgr.isEmpty(itemset2.getRow().getFieldValue("DATE_FROM"))) {

           DateFormat df= DateFormat.getInstance();
           Date dt = null;
           try
           {
               dt = df.parse(data.getFieldValue("DATE_FROM"));
           }
           catch (ParseException e)
           {
               e.printStackTrace();
           }
           data.addFieldDateItem("DATE_FROM_MASKED", dt);
           data.setFieldDateItem("DATE_FROM_MASKED", dt);

           itemset2.setRow(data);
           itembar2.getASPRowSet().store();
       }

       itemlay2.setLayoutMode(itemlay2.EDIT_LAYOUT);
   }

   public void saveReturnItem2()
   {
      ASPManager mgr = getASPManager();
      if ("FALSE".equals(mgr.readValue("SAVEEXECUTE")))
         itemlay2.setLayoutMode(itemlay2.EDIT_LAYOUT);
      else
      {
         int currHead = headset.getCurrentRowNo();
         int currrowItem2 = itemset2.getCurrentRowNo();
         itemset2.changeRow();
         mgr.submit(trans);
         headset.goTo(currHead);
         okFindITEM5();
         okFindITEM4();
         okFindITEM9();
         itemset2.goTo(currrowItem2);
      }
   }

   public void deleteITEM2()
   {
      ASPManager mgr = getASPManager();

      itemset2.store();

      if (itemlay2.isMultirowLayout())
      {
         itemset2.setSelectedRowsRemoved();
         itemset2.unselectRows();
      }
      else
         itemset2.setRemoved();

      mgr.submit(trans);

      okFindITEM5();
   }

   public void okFindITEM9()
   {
      if (itemset2.countRows() > 0)
      {

         ASPManager mgr = getASPManager();

         trans.clear();

         int headrowno = headset.getCurrentRowNo();
         int currrow = itemset2.getCurrentRowNo();

         q = trans.addQuery(itemblk9);
         q.addWhereCondition("WO_NO = ? AND PARENT_ROW_NO = ?");
         q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
         q.addParameter("ROW_NO",itemset2.getRow().getValue("ROW_NO"));

         q.includeMeta("ALL");
         mgr.submit(trans);

         if (itemset9.countRows() == 0 && "ITEM9.OkFind".equals(mgr.readValue("__COMMAND")))
         {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVMGTNODATA: No data found."));
            itemset9.clear();
         }


         headset.goTo(headrowno);
         itemset2.goTo(currrow);
      }
   }


   public void countFindITEM9()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk9);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("WO_NO = ? AND PARENT_ROW_NO = ?");
      q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
      q.addParameter("ROW_NO",itemset2.getRow().getValue("ROW_NO"));
      int headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);
      itemlay9.setCountValue(toInt(itemset9.getRow().getValue("N")));
      itemset9.clear();
      headset.goTo(headrowno);
   }     

   public void newRowITEM9()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM9","WORK_ORDER_ROLE_API.New__",itemblk9);
      cmd.setParameter("ITEM9_WO_NO",headset.getRow().getValue("WO_NO"));
      cmd.setOption("ACTION","PREPARE");
      c = mgr.newASPCommand();      
      c.defineCustomFunction("Site_API.Get_Company");
      c.addLocalReference("COMPANY","OUT");
      c.addLocalReference("CONTRACT","IN");
      c.setOption("FORCE","Y");
      cmd.addPostCommand(c);


      trans = mgr.perform(trans);
      String compan = trans.getValue("ITEM9/DATA/COMPANY");
      data = trans.getBuffer("ITEM9/DATA");
      data.setFieldItem("ITEM9_WO_NO",headset.getRow().getValue("WO_NO")); 
      data.setFieldItem("ITEM9_CONTRACT",headset.getRow().getValue("CONTRACT")); 
      data.setFieldItem("ITEM9_ORG_CODE",headset.getRow().getValue("ORG_CODE")); 
      data.setFieldItem("ITEM9_ORG_CODE_DESC",headset.getRow().getValue("ORGCODEDESCRIPTION")); 
      data.setFieldItem("PARENT_ROW_NO",itemset2.getRow().getValue("ROW_NO")); 
      data.setFieldItem("ITEM9_ROLE_CODE",itemset2.getRow().getValue("ROLE_CODE")); 
      data.setFieldItem("ITEM9_ROLE_CODE_DESC",itemset2.getRow().getValue("ROLE_CODE_DESC"));
      data.setFieldItem("ALLOCATED_HOURS",itemset2.getRow().getValue("PLAN_HRS")); 
      data.setFieldItem("ITEM9_TEAM_ID",itemset2.getRow().getValue("TEAM_ID")); 
      data.setFieldItem("ITEM9_TEAM_CONTRACT",itemset2.getRow().getValue("TEAM_CONTRACT")); 
      data.setFieldItem("ITEM9_COMPANY",compan);



      if (!mgr.isEmpty(headset.getRow().getValue("PLAN_S_DATE")))
      {
         data.setFieldItem("ITEM9_DATE_FROM",headset.getRow().getFieldValue("PLAN_S_DATE"));
         DateFormat df= DateFormat.getInstance();
         Date dt = null;
         try
         {
            dt = df.parse(headset.getRow().getFieldValue("PLAN_S_DATE"));
         }
         catch (ParseException e)
         {
            e.printStackTrace();
         }
         data.addFieldDateItem("ITEM9_DATE_FROM_MASKED", dt);
         data.setFieldDateItem("ITEM9_DATE_FROM_MASKED", dt);
      }
      if (!mgr.isEmpty(headset.getRow().getValue("PLAN_F_DATE")))
         data.setFieldItem("ITEM9_DATE_TO",headset.getRow().getFieldValue("PLAN_F_DATE"));

      itemset9.addRow(data);
   }

   public void editRowITEM9()
   {
      ASPManager mgr = getASPManager();

      data = itemset9.getRow();

      DateFormat df= DateFormat.getInstance();
      Date dt = null;
      try
      {
         dt = df.parse(data.getFieldValue("ITEM9_DATE_FROM"));
      }
      catch (ParseException e)
      {
         e.printStackTrace();
      }
      data.addFieldDateItem("ITEM9_DATE_FROM_MASKED", dt);
      data.setFieldDateItem("ITEM9_DATE_FROM_MASKED", dt);

      itemset9.setRow(data);
      itembar9.getASPRowSet().store();
      itemlay9.setLayoutMode(itemlay9.EDIT_LAYOUT);

   }


   public void saveReturnITEM8()
   {
      ASPManager mgr = getASPManager();

      if ("FALSE".equals(mgr.readValue("SAVEEXECUTE")))
         itemlay8.setLayoutMode(itemlay8.EDIT_LAYOUT);
      else
      {
         int currHead = headset.getCurrentRowNo();
         int currrowItem7 = itemset7.getCurrentRowNo();
         int currrowItem8 = itemset8.getCurrentRowNo();
         itemset8.changeRow();
         mgr.submit(trans);
         headset.goTo(currHead);
         itemset7.goTo(currrowItem7);
         itemset8.goTo(currrowItem8);
         okFindITEM2();
      }
   }

   public void saveReturnITEM9()
   {
      ASPManager mgr = getASPManager();

      int currHead = headset.getCurrentRowNo();
      int currrowItem2 = itemset2.getCurrentRowNo();
      int currrowItem9 = itemset9.getCurrentRowNo();
      itemset9.changeRow();
      mgr.submit(trans);


      trans.clear();
      q = trans.addEmptyQuery(itemblk2);
      q.addWhereCondition("WO_NO = ? AND PARENT_ROW_NO IS NULL");
      q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
      q.includeMeta("ALL");
      mgr.submit(trans);

      headset.goTo(currHead);
      itemset2.goTo(currrowItem2);
      itemset9.goTo(currrowItem9);
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
         cmd.addParameter("ITEM7_QTY",itemset7.getRow().getValue("QTY")); 
      }

      cmd = trans.addCustomCommand("REMOVEJOBLINE","Work_Order_Job_API.Remove_Job_Line");
      cmd.addParameter("ITEM7_WO_NO",itemset7.getRow().getValue("WO_NO"));
      cmd.addParameter("JOB_ID",itemset7.getRow().getValue("JOB_ID")); 

      trans = mgr.perform(trans);

      trans.clear();
      okFindITEM7();
   }
   //Bug 89703, End

   public void okFindITEM3()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      int headrowno = headset.getCurrentRowNo();
      q = trans.addQuery(itemblk3);
      q.addWhereCondition("WO_NO = ? AND CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT)");
      q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
      q.includeMeta("ALL");

      mgr.querySubmit(trans,itemblk3);

      if (comBarAct)
      {
         if (itemset3.countRows() == 0 && "ITEM3.OkFind".equals(mgr.readValue("__COMMAND")))
         {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTNODATA: No data found."));
            itemset3.clear();
         }
      }
      headset.goTo(headrowno);  
      if (itemset3.countRows() > 0)
      {
         okFindITEM6();
      }

   }

   public void countFindITEM3()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk3);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("WO_NO = ? AND CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT)");
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

      cmd = trans.addQuery("SUYSTDATE","SELECT SYSDATE SYS_DATE FROM DUAL");

      trans = mgr.perform(trans);

      String dateRequired = trans.getBuffer("PLNDATE/DATA").getFieldValue("DUE_DATE");
      String nPreAccoId = trans.getBuffer("PREACCOID/DATA").getFieldValue("NPREACCOUNTINGID");
      String item3Cont = trans.getBuffer("CONT/DATA").getFieldValue("ITEM3_CONTRACT");
      String item3Company = trans.getBuffer("COMP/DATA").getFieldValue("ITEM3_COMPANY");
      String mchCode = trans.getValue("MCHCOD/DATA/MCHCODE");
      String mchDesc = trans.getValue("MCHDESC/DATA/ITEM3DESCRIPTION");
      String sysDate = trans.getBuffer("SUYSTDATE/DATA").getFieldValue("SYS_DATE");

      data = trans.getBuffer("ITEM3/DATA");

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

   public void okFindITEM4()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      int headrowno = headset.getCurrentRowNo();
      q = trans.addQuery(itemblk4);
      q.addWhereCondition("WO_NO = ?");
      q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
      q.includeMeta("ALL");

      mgr.submit(trans);

      trans.clear();

      // to refresh empty column values and add summary details
      fillRows();
      //sumColumns();

      headset.goTo(headrowno);
   }

   public void emptyOkFindITEM4()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      int headrowno = headset.getCurrentRowNo();

      q = trans.addEmptyQuery(itemblk4);
      q.addWhereCondition("WO_NO = ?");
      q.addParameter("WO_NO", headset.getRow().getValue("WO_NO"));
      q.includeMeta("ALL");

      mgr.submit(trans);

      trans.clear();

      // to refresh empty column values and add summary details
      fillRows();
      //sumColumns();

      headset.goTo(headrowno);
   }

   public void saveReturnITEM4()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      curentrow = itemset4.getCurrentRowNo();


      //if (itemlay4.isMultirowLayout())
      //    curentrow = itemset4.getRowSelected();

      itemset4.goTo(curentrow);


      String sWoNo = mgr.readValue("ITEM4_WO_NO","");
      String workOrderCostType = mgr.readValue("WORK_ORDER_COST_TYPE","");

      double budgCost = mgr.readNumberValue("BUDGET_COST");   
      if (isNaN(budgCost))
         budgCost = 0;

      double budgRev = mgr.readNumberValue("BUDGET_REVENUE");
      if (isNaN(budgRev))
         budgRev = 0;

      double budgMargin = mgr.readNumberValue("NBUDGETMARGIN");
      if (isNaN(budgMargin))
         budgMargin = 0;

      double planCost = mgr.readNumberValue("NPLANNEDCOST");
      if (isNaN(planCost))
         planCost = 0;

      double planRev = mgr.readNumberValue("NPLANNEDREVENUE");
      if (isNaN(planRev))
         planRev = 0;

      double planMargin = mgr.readNumberValue("NPLANNEDMARGIN");
      if (isNaN(planMargin))
         planMargin = 0;

      double actCost = mgr.readNumberValue("NACTUALCOST");
      if (isNaN(actCost))
         actCost = 0;

      double actRev = mgr.readNumberValue("NACTUALREVENUE");
      if (isNaN(actRev))
         actRev = 0;

      double actMargin = mgr.readNumberValue("NACTUALMARGIN");
      if (isNaN(actMargin))
         actMargin = 0;

      buff = itemset4.getRow();

      buff.setValue("WO_NO", sWoNo);
      buff.setValue("WORK_ORDER_COST_TYPE", workOrderCostType);
      buff.setNumberValue("BUDGET_COST", budgCost);
      buff.setNumberValue("BUDGET_REVENUE", budgRev);
      buff.setNumberValue("NBUDGETMARGIN", budgMargin);
      buff.setNumberValue("NPLANNEDCOST", planCost);
      buff.setNumberValue("NPLANNEDREVENUE", planRev);
      buff.setNumberValue("NPLANNEDMARGIN", planMargin);
      buff.setNumberValue("NACTUALCOST", actCost);
      buff.setNumberValue("NACTUALREVENUE", actRev);
      buff.setNumberValue("NACTUALMARGIN", actMargin);

      itemset4.setRow(buff);
      mgr.submit(trans);

      // to refresh emoty column values and add summary details
      emptyOkFindITEM4();

      itemlay4.setLayoutMode(itemlay4.getHistoryMode());
      itemset4.goTo(curentrow);
   }

   public void saveReturnItem5()
   {
      ASPManager mgr = getASPManager();
      trans.clear();

      cmd = trans.addCustomFunction("RETURNQUANTITY","WORK_ORDER_PLANNING_API.Get_Qty","QUANTITY");
      cmd.addParameter("WO_NO");
      cmd.addParameter("PLAN_LINE_NO");

      cmd = trans.addCustomFunction("COST_TYPE","WORK_ORDER_COST_TYPE_API.Encode","CLIENTVAL0");
      cmd.addParameter("ITEM5_WORK_ORDER_COST_TYPE");
      trans = mgr.perform(trans);

      String qty = trans.getValue("RETURNQUANTITY/DATA/QUANTITY");
      String cost_type = trans.getValue("COST_TYPE/DATA/CLIENTVAL0");

      if ((cost_type.equals("P"))||(cost_type.equals("M"))||(cost_type.equals("T")))
      {

         if (!qty.equals(mgr.readValue("QUANTITY")))
         {
            mgr.showError(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCOSTTYPE: Quantity is not updatable when Work Order Cost Type is Personnel,Material or Tools/Facilities."));
         }
      }

      trans.clear();
      int currHead = headset.getCurrentRowNo();
      int currrowItem5 = itemset5.getCurrentRowNo();
      itemset5.changeRow();
      String status = itemset5.getRowStatus();
      mgr.submit(trans);
      itemset5.goTo(currrowItem5);
      headset.goTo(currHead);

      if ("Modify__".equals(status) && "T".equals(itemset5.getRow().getFieldValue("WORK_ORDER_COST_TYPE_DB")))
      {
         String lsAttr =  "CATALOG_NO_CONTRACT" + (char)31 + itemset5.getRow().getFieldValue("ITEM5_CATALOG_CONTRACT") + (char)30
                          +"CATALOG_NO" + (char)31 + itemset5.getRow().getFieldValue("ITEM5_CATALOG_NO") + (char)30
                          +"SALES_PRICE" + (char)31 + itemset5.getRow().getFieldValue("ITEM5_SALES_PRICE") + (char)30
                          +"DISCOUNT" + (char)31 + itemset5.getRow().getFieldValue("ITEM5_DISCOUNT") + (char)30;

         trans.clear();
         cmd = trans.addCustomFunction("GETROW", "Work_Order_Tool_Facility_Api.Get_Row_No_For_Planning","ROW_NO");
         cmd.addParameter("ITEM5_WO_NO"); 
         cmd.addParameter("PLAN_LINE_NO");


         cmd = trans.addCustomCommand("SETVAL","Work_Order_Tool_Facility_Api.Set_Field_Value");
         cmd.addParameter("ITEM5_WO_NO"); 
         cmd.addReference("ROW_NO","GETROW/DATA"); 
         cmd.addParameter("ATTR",lsAttr); 

         trans=mgr.perform(trans);
      }

      okFindITEM4();
   }

   public void saveNewItem5()
   {
      saveReturnItem5();
      newRowITEM5();
   }

   public void okFindITEM5()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      int headrowno = headset.getCurrentRowNo();
      q = trans.addQuery(itemblk5);
      q.addWhereCondition("WO_NO = ?");
      q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
      q.includeMeta("ALL");
      mgr.submit(trans);

      if (comBarAct)
      {
         if (  itemset5.countRows(  )   ==   0   &&   "ITEM5.OkFind".equals(  mgr.readValue(  "__COMMAND")))
         {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTNODATA: No data found."));
            itemset5.clear();
         }
      }

      headset.goTo(headrowno);
      countSalesPriceAmountItem5();
      getCostAmountItem5();

   }

   public void countFindITEM5()
   {
      ASPManager mgr = getASPManager();

      int headrowno = headset.getCurrentRowNo();
      q = trans.addQuery(itemblk5);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("WO_NO = ?");
      q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
      mgr.submit(trans);
      itemlay5.setCountValue(toInt(itemset5.getRow().getValue("N")));
      itemset5.clear();
      headset.goTo(headrowno);
   }

   public void newRowITEM5()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM5","WORK_ORDER_PLANNING_API.New__",itemblk5);
      cmd.setParameter("ITEM5_WO_NO",headset.getRow().getValue("WO_NO"));
      cmd.setOption("ACTION","PREPARE");

      cmd = trans.addCustomFunction("GETCONT","ACTIVE_SEPARATE_API.Get_Contract","ITEM5_CATALOG_CONTRACT");  
      cmd.addParameter("ITEM5_WO_NO",headset.getRow().getValue("WO_NO"));

      trans = mgr.perform(trans);

      data = trans.getBuffer("ITEM5/DATA");
      data.setValue("CATALOG_CONTRACT",trans.getValue("GETCONT/DATA/CATALOG_CONTRACT"));
      itemset5.addRow(data);

      if (!mgr.isEmpty(headset.getRow().getValue("QUOTATION_ID")))
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEWOCREATEFROMQUOTATION: The Work Order is created from a Work Order Quotation."));
   }

   public void backITEM()
   {
      int currentrow = itemset3.getRowSelected();
      itemset3.goTo(currentrow);
      itemlay3.setLayoutMode(itemlay3.MULTIROW_LAYOUT);
      if (updateBudg)
         okFindITEM4();
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
      itemset3.goTo(   currrowItem3);
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

   public void okFindITEM6()
   {
      ASPManager mgr = getASPManager();

      //Bug 72202, Start, Added check on row count
      if (itemset3.countRows() > 0)
      {
         trans.clear();
         int headsetRowNo = headset.getCurrentRowNo();
         int item3rowno = itemset3.getCurrentRowNo();

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

   public void countFindITEM6()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("WO_NO = ? AND MAINT_MATERIAL_ORDER_NO = ?");
      q.addParameter("ITEM3_WO_NO",itemset3.getRow().getFieldValue("ITEM3_WO_NO"));
      q.addParameter("MAINT_MATERIAL_ORDER_NO",itemset3.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));
      mgr.submit(trans);
      itemlay.setCountValue(toInt(itemset.getRow().getValue("N")));
      itemset.clear();
   }

   public void newRowITEM6()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("ITEM6","MAINT_MATERIAL_REQ_LINE_API.New__",itemblk);
      cmd.setParameter("SPARE_CONTRACT",itemset3.getRow().getFieldValue("ITEM3_CONTRACT"));
      cmd.setParameter("CATALOG_CONTRACT",headset.getRow().getValue("CONTRACT"));
      cmd.setParameter("ITEM_WO_NO",itemset3.getRow().getFieldValue("ITEM3_WO_NO"));
      cmd.setParameter("ITEM0_MAINT_MATERIAL_ORDER_NO",itemset3.getRow().getFieldValue("MAINT_MATERIAL_ORDER_NO"));
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("ITEM6/DATA");

      data.setFieldItem("SPARE_CONTRACT",itemset3.getRow().getFieldValue("ITEM3_CONTRACT"));                      
      data.setFieldItem("DATE_REQUIRED",itemset3.getRow().getFieldValue("DUE_DATE"));
      data.setFieldItem("ITEM_CATALOG_CONTRACT",headset.getRow().getValue("CONTRACT"));

      itemset.addRow(data);
   }

   public void duplicateITEM6()
   {
      ASPManager mgr = getASPManager();

      if (itemlay.isMultirowLayout())
         itemset.goTo(itemset.getRowSelected());
      else
         itemset.selectRow();

      itemlay.setLayoutMode(itemlay.NEW_LAYOUT);

      cmd = trans.addEmptyCommand("ITEM6","MAINT_MATERIAL_REQ_LINE_API.New__",itemblk);
      cmd.setParameter("ITEM_WO_NO",itemset3.getRow().getFieldValue("ITEM3_WO_NO"));
      cmd.setParameter("ITEM0_MAINT_MATERIAL_ORDER_NO",itemset3.getRow().getFieldValue("MAINT_MATERIAL_ORDER_NO"));
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("ITEM6/DATA");

      data.setFieldItem("PART_NO",itemset.getRow().getValue("PART_NO"));
      data.setFieldItem("SPAREDESCRIPTION",itemset.getRow().getValue("SPAREDESCRIPTION"));      
      data.setFieldItem("CONDITION_CODE",itemset.getRow().getValue("CONDITION_CODE"));
      data.setFieldItem("CONDDESC",itemset.getRow().getValue("CONDDESC"));
      data.setFieldItem("SPARE_CONTRACT",itemset.getRow().getValue("SPARE_CONTRACT"));
      data.setFieldItem("HASSPARESTRUCTURE",itemset.getRow().getValue("HASSPARESTRUCTURE"));
      data.setFieldItem("DIMQTY",itemset.getRow().getValue("DIMQTY"));
      data.setFieldItem("TYPEDESIGN",itemset.getRow().getValue("TYPEDESIGN"));
      data.setFieldItem("DATE_REQUIRED",itemset.getRow().getFieldValue("DATE_REQUIRED"));
      data.setFieldItem("PLAN_QTY",itemset.getRow().getValue("PLAN_QTY"));
      data.setFieldItem("QTY_SHORT",itemset.getRow().getValue("QTY_SHORT"));
      data.setFieldItem("QTYONHAND",itemset.getRow().getValue("QTYONHAND"));
      data.setFieldItem("ITEM_CATALOG_NO",itemset.getRow().getFieldValue("ITEM_CATALOG_NO"));
      data.setFieldItem("ITEMCATALOGDESC",itemset.getRow().getValue("ITEMCATALOGDESC"));
      data.setFieldItem("ITEM_PRICE_LIST_NO",itemset.getRow().getFieldValue("ITEM_PRICE_LIST_NO"));
      data.setFieldItem("LIST_PRICE",itemset.getRow().getFieldValue("LIST_PRICE"));
      data.setFieldItem("UNITMEAS",itemset.getRow().getValue("UNITMEAS"));
      data.setFieldItem("ITEM_CATALOG_CONTRACT",itemset.getRow().getFieldValue("ITEM_CATALOG_CONTRACT"));
      data.setFieldItem("ITEM_DISCOUNT",itemset.getRow().getFieldValue("ITEM_DISCOUNT"));
      data.setFieldItem("ITEMSALESPRICEAMOUNT",itemset.getRow().getFieldValue("ITEMSALESPRICEAMOUNT"));
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

   public void deleteITEM6()
   {
      ASPManager mgr = getASPManager();

      itemset.store();

      if (itemlay.isMultirowLayout())
      {
         itemset.setSelectedRowsRemoved();
         itemset.unselectRows();
      }
      else
         itemset.setRemoved();

      mgr.submit(trans);

      okFindITEM5();
      okFindITEM4();
   }

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

      okFindITEM2();
      okFindITEM3();
      okFindITEM4();
      okFindITEM5();
      okFindITEM8();
      itemset7.goTo(currrowItem7);
   }

   public void okFindITEM8()
   {
      ASPManager mgr = getASPManager();
      if (itemset7 != null && itemset7.countRows() > 0)
      {
         trans.clear();
         int headrowNo = headset.getCurrentRowNo();
         int item7rowNo = itemset7.getCurrentRowNo();
         q = trans.addQuery(itemblk8);
         q.addWhereCondition("WO_NO = ? AND JOB_ID = ? AND PARENT_ROW_NO IS NULL");
         q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
         q.addParameter("JOB_ID",itemset7.getRow().getValue("JOB_ID"));
         q.includeMeta("ALL");
         mgr.submit(trans);

         if (comBarAct)
         {
            if (itemset8.countRows() == 0 && "ITEM8.OkFind".equals(mgr.readValue("__COMMAND")))
            {
               mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTNODATA: No data found."));
               itemset8.clear();
            }
         }
         headset.goTo(headrowNo);
         itemset7.goTo(item7rowNo);
      }
   }

   public void countFindITEM8()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk8);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("WO_NO = ? AND JOB_ID = ? AND PARENT_ROW_NO IS NULL");
      q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
      q.addParameter("JOB_ID",itemset7.getRow().getValue("JOB_ID"));
      int headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);
      itemlay8.setCountValue(toInt(itemset8.getRow().getValue("N")));
      itemset8.clear();
      itemset8.goTo(headrowno);
   }

   public void newRowITEM8()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM8","WORK_ORDER_ROLE_API.New__",itemblk8);
      cmd.setParameter("ITEM8_WO_NO",headset.getRow().getValue("WO_NO"));
      cmd.setOption("ACTION","PREPARE");

      c = mgr.newASPCommand();      
      c.defineCustomFunction("Site_API.Get_Company");
      c.addLocalReference("COMPANY",c.OUT);
      c.addLocalReference("CONTRACT",c.IN);
      c.setOption("FORCE","Y");
      cmd.addPostCommand(c);

      cmd = trans.addCustomFunction("GETCURRANCYCODE","ACTIVE_SEPARATE_API.Get_Currency_Code","CURRENCY_CODE");
      cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

      cmd = trans.addCustomFunction("GETCATCONT","ACTIVE_SEPARATE_API.Get_Contract","ITEM8_CATALOG_CONTRACT");
      cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

      //bug ID 59224,start

      cmd = trans.addCustomFunction("GETITEM8ORGDESC","Organization_Api.Get_Description","ITEM8_ORG_CODE_DESC");
      cmd.addParameter("ITEM8_CONTRACT",headset.getRow().getValue("CONTRACT"));
      cmd.addParameter("ITEM8_ORG_CODE",headset.getRow().getValue("ORG_CODE"));

      trans = mgr.perform(trans);
      String sitem8OrgDesc = trans.getValue("GETITEM8ORGDESC/DATA/ITEM8_ORG_CODE_DESC");

      String compan = trans.getValue("ITEM8/DATA/COMPANY");
      String currencyCode = trans.getValue("GETCURRANCYCODE/DATA/CURRENCY_CODE");
      String catcont = trans.getValue("GETCATCONT/DATA/CATALOG_CONTRACT");

      data = trans.getBuffer("ITEM8/DATA");
      data.setFieldItem("ITEM8_WO_NO",headset.getRow().getValue("WO_NO")); 
      data.setFieldItem("ITEM8_CONTRACT",headset.getRow().getValue("CONTRACT")); 
      data.setFieldItem("ITEM8_ORG_CODE",headset.getRow().getValue("ORG_CODE")); 
      data.setFieldItem("ITEM8_ORG_CODE_DESC",sitem8OrgDesc); 
      data.setFieldItem("ITEM8_CONTRACT",headset.getRow().getValue("CONTRACT")); 
      data.setFieldItem("ITEM8_JOB_ID",itemset7.getRow().getValue("JOB_ID")); 
      data.setFieldItem("ITEM8_COMPANY",compan);
      if (mgr.isEmpty(itemset7.getRow().getValue("DATE_FROM")))
      {
         data.setFieldDateItem("ITEM8_DATE_FROM", headset.getRow().getFieldDateValue("PLAN_S_DATE"));
         DateFormat df= DateFormat.getInstance();
         Date dt = null;
         try
         {
            dt = df.parse(headset.getRow().getFieldValue("PLAN_S_DATE"));
         }
         catch (ParseException e)
         {
            e.printStackTrace();
         }
         data.addFieldDateItem("ITEM8_DATE_FROM_MASKED", dt);
         data.setFieldDateItem("ITEM8_DATE_FROM_MASKED",dt);
      }
      else
      {
         data.setFieldDateItem("ITEM8_DATE_FROM", itemset7.getRow().getFieldDateValue("ITEM7_DATE_FROM"));
         DateFormat df= DateFormat.getInstance();
         Date dt = null;
         try
         {
            dt = df.parse(headset.getRow().getFieldValue("PLAN_S_DATE"));
         }
         catch (ParseException e)
         {
            e.printStackTrace();
         }
         data.addFieldDateItem("ITEM8_DATE_FROM_MASKED", dt);
         data.setFieldDateItem("ITEM8_DATE_FROM_MASKED",dt);
      }
      if (mgr.isEmpty(itemset7.getRow().getValue("DATE_TO")))
      {
         data.setFieldDateItem("ITEM8_DATE_TO", headset.getRow().getFieldDateValue("PLAN_F_DATE"));
      }
      else
      {
         data.setFieldDateItem("ITEM8_DATE_TO", itemset7.getRow().getFieldDateValue("ITEM7_DATE_TO"));
      }
      data.setFieldItem("CURRENCY_CODE",currencyCode);
      data.setFieldItem("ITEM8_ROWSTATUS","NEW");
      data.setFieldItem("ITEM8_CATALOG_CONTRACT",catcont);
      itemset8.addRow(data);
   }


   public void editRowITEM8()
   {
      ASPManager mgr = getASPManager();

      data = itemset8.getRow();

      DateFormat df= DateFormat.getInstance();
      Date dt = null;
      try
      {
         dt = df.parse(data.getFieldValue("ITEM8_DATE_FROM"));
      }
      catch (ParseException e)
      {
         e.printStackTrace();
      }
      data.addFieldDateItem("ITEM8_DATE_FROM_MASKED", dt);
      data.setFieldDateItem("ITEM8_DATE_FROM_MASKED", dt);

      itemset8.setRow(data);
      itembar8.getASPRowSet().store();
      itemlay8.setLayoutMode(itemlay8.EDIT_LAYOUT);

   }

   public void setValuesInMaterials()
   {
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
            String spareCont = itemset.getRow().getValue("SPARE_CONTRACT");
            String partNo =itemset.getRow().getFieldValue("PART_NO");
            String condCode =itemset.getRow().getFieldValue("CONDITION_CODE");
            String partOwnDb =itemset.getRow().getFieldValue("PART_OWNERSHIP_DB");
            String cataNo = itemset.getRow().getFieldValue("ITEM_CATALOG_NO");
            double nPlanQty = itemset.getRow().getNumberValue("PLAN_QTY");
            if (isNaN(nPlanQty))
               nPlanQty = 0;
            String cataCont = itemset.getRow().getFieldValue("CATALOG_CONTRACT");
            String cusNo = headset.getRow().getFieldValue("CUSTOMER_NO");
            String agreeId = headset.getRow().getFieldValue("AGREEMENT_ID");
            String priceListNo = itemset.getRow().getFieldValue("ITEM_PRICE_LIST_NO");
            String planLineNo = itemset.getRow().getFieldValue("PLAN_LINE_NO");

            String serialNo = itemset.getRow().getFieldValue("SERIAL_NO");
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

            if (!"CUSTOMER OWNED".equals(partOwnDb))
            {
               /*cmd = trans.addCustomFunction("GETCOST"+i,"Inventory_Part_Cost_API.Get_Cost","ITEM_COST");
               cmd.addParameter("SPARE_CONTRACT",spareCont);
               cmd.addParameter("PART_NO",partNo);
               cmd.addParameter("CONFIG_ID","*");
               cmd.addParameter("CONDITION_CODE",condCode); */

               cmd = trans.addCustomCommand("GETCOST"+i,"Active_Separate_API.Get_Inventory_Value");
               cmd.addParameter("ITEM_COST");
               cmd.addParameter("SPARE_CONTRACT", spareCont);
               cmd.addParameter("PART_NO", partNo);
               cmd.addParameter("SERIAL_NO", serialNo);
               cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
               cmd.addParameter("CONDITION_CODE", condCode);
            }

            if ((!mgr.isEmpty(cataNo)) && (nPlanQty != 0))
            {
               String nPlanQtyStr = mgr.getASPField("PLAN_QTY").formatNumber(nPlanQty);

               cmd = trans.addCustomCommand("PRICEINFO"+i,"Work_Order_Coding_API.Get_Price_Info");
               cmd.addParameter("BASE_PRICE","0");
               cmd.addParameter("SALE_PRICE","0");
               cmd.addParameter("ITEM_DISCOUNT","0");
               cmd.addParameter("ITEM_CURRENCY_RATE","0");
               cmd.addParameter("CATALOG_CONTRACT",cataCont);
               cmd.addParameter("ITEM_CATALOG_NO",cataNo);
               cmd.addParameter("CUSTOMER_NO",cusNo);
               cmd.addParameter("AGREEMENT_ID",agreeId);
               cmd.addParameter("ITEM_PRICE_LIST_NO",priceListNo);
               cmd.addParameter("PLAN_QTY",nPlanQtyStr);

               cmd = trans.addCustomFunction("LISTPRICE"+i,"WORK_ORDER_PLANNING_API.Get_Sales_Price","LIST_PRICE");
               cmd.addParameter("ITEM_WO_NO",itemset.getRow().getFieldValue("ITEM_WO_NO"));
               cmd.addParameter("PLAN_LINE_NO",planLineNo);
            }
            itemset.next();
         }           

         trans = mgr.validate(trans);

         itemset.first();

         for (int i=0; i<n; ++i)
         {
            double nCost = 0;
            String  ownerName = "";

            row = itemset.getRow();
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
               if ("CUSTOMER OWNED".equals(itemset.getRow().getFieldValue("PART_OWNERSHIP_DB")))
                  nCost = 0;
               else
                  nCost   = trans.getNumberValue("GETCOST"+i+"/DATA/ITEM_COST");
               if (isNaN(nCost))
                  nCost = 0;
               if ( "SUPPLIER LOANED".equals(itemset.getRow().getValue("PART_OWNERSHIP_DB")) || "TRUE".equals(sObjLoan))
                  nCost = 0;
            }

            double nPlanQty = itemset.getRow().getNumberValue("PLAN_QTY");
            if (isNaN(nPlanQty))
               nPlanQty = 0;
            double nCostAmount; 

            if (nPlanQty != 0)
            {
               double nCostTemp = trans.getNumberValue("GETCOST"+i+"/DATA/ITEM_COST");
               if (isNaN(nCostTemp))
                  nCostTemp = 0;
               if ( "SUPPLIER LOANED".equals(itemset.getRow().getValue("PART_OWNERSHIP_DB")) || "TRUE".equals(sObjLoan))
                  nCostTemp = 0;
               nCostAmount = nCostTemp * nPlanQty;
            }
            else
               nCostAmount = 0;

            String priceListNo = itemset.getRow().getFieldValue("ITEM_PRICE_LIST_NO");
            String cataNo = itemset.getRow().getFieldValue("ITEM_CATALOG_NO");
            double nDiscount = itemset.getRow().getNumberValue("DISCOUNT");
            if (isNaN(nDiscount))
               nDiscount = 0;

            if ((!mgr.isEmpty(cataNo)) && (nPlanQty != 0))
            {
               double listPrice = trans.getNumberValue("LISTPRICE"+i+"/DATA/LIST_PRICE");
               if (isNaN(listPrice))
                  listPrice = 0;

               if (mgr.isEmpty(priceListNo))
                  priceListNo = trans.getBuffer("PRICEINFO"+i+"/DATA").getFieldValue("ITEM_PRICE_LIST_NO");

               if (nDiscount == 0)
               {
                  nDiscount = trans.getNumberValue("PRICEINFO"+i+"/DATA/ITEM_DISCOUNT");   
                  if (isNaN(nDiscount))
                     nDiscount = 0;
               }

               double nSalesPriceAmount;

               if (listPrice == 0)
               {
                  listPrice = trans.getNumberValue("PRICEINFO"+i+"/DATA/BASE_PRICE");
                  if (isNaN(listPrice))
                     listPrice = 0;

                  double planQty = trans.getNumberValue("PRICEINFO"+i+"/DATA/PLAN_QTY");  
                  if (isNaN(planQty))
                     planQty = 0;

                  double nSaleUnitPrice = trans.getNumberValue("PRICEINFO"+i+"/DATA/SALE_PRICE");
                  if (isNaN(nSaleUnitPrice))
                     nSaleUnitPrice = 0;

                  nSalesPriceAmount  = nSaleUnitPrice * planQty;
               }
               else
               {
                  double nListPriceTemp = trans.getNumberValue("LISTPRICE"+i+"/DATA/LIST_PRICE");
                  if (isNaN(nListPriceTemp))
                     nListPriceTemp = 0;

                  double planQty = trans.getNumberValue("PRICEINFO"+i+"/DATA/PLAN_QTY");  
                  if (isNaN(planQty))
                     planQty = 0;

                  double nDiscountTemp = itemset.getRow().getNumberValue("DISCOUNT");
                  if (isNaN(nDiscountTemp))
                     nDiscountTemp = 0;

                  if (nDiscountTemp == 0)
                  {
                     nDiscountTemp = trans.getNumberValue("PRICEINFO"+i+"/DATA/ITEM_DISCOUNT");
                     if (isNaN(nDiscountTemp))
                        nDiscountTemp = 0;
                  }

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
            row.setValue("OWNER_NAME",ownerName);

            itemset.setRow(row);

            itemset.next();
         }
      }
      itemset.first();
   }

   public void okFindITEM7()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      int headrowno = headset.getCurrentRowNo();

      q = trans.addQuery(itemblk7);
      q.addWhereCondition("WO_NO = ? AND STD_JOB_CONTRACT = ?");
      q.addParameter("WO_NO",headset.getValue("WO_NO"));
      q.addParameter("CONTRACT",headset.getValue("CONTRACT"));

      q.includeMeta("ALL");
      mgr.querySubmit(trans, itemblk7);

      headset.goTo(headrowno);
      if (itemset7 != null && itemset7.countRows() > 0)
         okFindITEM8();
   }

   public void countFindITEM7()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      int headrowno = headset.getCurrentRowNo();

      q = trans.addQuery(itemblk7);
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

      cmd = trans.addCustomFunction("GETSDATE", "Active_Separate_API.Get_Plan_S_Date", "ITEM7_DATE_FROM");
      cmd.addParameter("ITEM7_WO_NO", headset.getValue("WO_NO"));

      cmd = trans.addCustomFunction("GETFDATE", "Active_Separate_API.Get_Plan_F_Date", "ITEM7_DATE_TO");
      cmd.addParameter("ITEM7_WO_NO", headset.getValue("WO_NO"));

      trans = mgr.perform(trans);

      data = trans.getBuffer("ITEM7/DATA");

      Date dPlanSDate = trans.getBuffer("GETSDATE/DATA").getFieldDateValue("ITEM7_DATE_FROM");
      Date dPlanFDate = trans.getBuffer("GETFDATE/DATA").getFieldDateValue("ITEM7_DATE_TO");

      data.setFieldDateItem("ITEM7_DATE_FROM", dPlanSDate);
      data.setFieldDateItem("ITEM7_DATE_TO", dPlanFDate);
      itemset7.addRow(data);
   }

   public void fillRows()
   {
      ASPManager mgr = getASPManager();

      curentrow = itemset4.getCurrentRowNo();

      trans.clear();

      if (itemset4.countRows()>0)
      {
         int n = itemset4.countRows();
         itemset4.first();
         for (int i=0; i<=n; ++i)
         {
            double act_rev = itemset4.getRow().getNumberValue("NACTUALREVENUE");  
            if (isNaN(act_rev))
               act_rev = 0;

            String s_act_rev = mgr.getASPField("NACTUALREVENUE").formatNumber(act_rev);

            cmd = trans.addCustomFunction( "ACTCOST"+i, "Work_Order_Budget_API.Get_Actual_Cost","NACTUALCOST");
            cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
            cmd.addParameter("WORK_ORDER_COST_TYPE",itemset4.getRow().getValue("WORK_ORDER_COST_TYPE"));

            cmd = trans.addCustomCommand( "ACTREV"+i, "Work_Order_Budget_API.Actual_Revenue");
            cmd.addParameter("NACTUALREVENUE",s_act_rev);
            cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
            cmd.addParameter("WORK_ORDER_COST_TYPE",itemset4.getRow().getValue("WORK_ORDER_COST_TYPE"));

            itemset4.next();
         }
         trans = mgr.perform(trans);
         itemset4.first();

         for (int i=0; i<=n; ++i)
         {
            double budgRev = itemset4.getRow().getNumberValue("BUDGET_REVENUE");
            if (isNaN(budgRev))
               budgRev = 0;

            double budgCost = itemset4.getRow().getNumberValue("BUDGET_COST");
            if (isNaN(budgCost))
               budgCost = 0;

            double planRev = itemset4.getRow().getNumberValue("NPLANNEDREVENUE");
            if (isNaN(planRev))
               planRev = 0;

            double planCost = itemset4.getRow().getNumberValue("NPLANNEDCOST");
            if (isNaN(planCost))
               planCost = 0;

            double actCost = trans.getNumberValue("ACTCOST"+i+"/DATA/NACTUALCOST");
            if (isNaN(actCost))
               actCost = 0;

            double actRev = trans.getNumberValue("ACTREV"+i+"/DATA/NACTUALREVENUE");
            if (isNaN(actRev))
               actRev = 0;

            double actMargin =  actRev - actCost;
            double budgMargin = budgRev - budgCost;
            double planMargin = planRev - planCost;

            buf = itemset4.getRow();

            buf.setNumberValue("NACTUALCOST",actCost);
            buf.setNumberValue("NACTUALREVENUE",actRev);
            buf.setNumberValue("NACTUALMARGIN",actMargin);
            buf.setNumberValue("NBUDGETMARGIN",budgMargin);
            buf.setNumberValue("NPLANNEDMARGIN",planMargin);

            itemset4.setRow(buf);

            itemset4.next();
         }  
         itemset4.first();
      }

      itemset4.goTo(curentrow);
   }

   public void fillLineDesc()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      if (itemset5.countRows()>0)
      {
         int n = itemset5.countRows();
         itemset5.first();
         String lineDesc;
         for (int i=0; i<=n; ++i)
         {
            lineDesc = itemset5.getRow().getValue("ITEM5_CATALOGDESC");

            if (mgr.isEmpty(itemset5.getRow().getValue("LINE_DESCRIPTION")))
            {
               buf = itemset5.getRow();

               buf.setValue("LINE_DESCRIPTION",lineDesc);
               itemset5.setRow(buf);
            }

            itemset5.next();
         }  
         itemset5.first();
      }
   }

   public void clearItem4()
   {
      ASPManager mgr = getASPManager();

      if (itemset4.countRows() > 6)
      {
         curentrow = itemset4.getCurrentRowNo();

         itemset4.last();

         if (mgr.translate("PCMWACTIVESEPARATESERVICEWMANAGEMENTSUMMARY: Summary").equals(itemset4.getRow().getValue("WORK_ORDER_COST_TYPE")))
            itemset4.clearRow();

         itemset4.goTo(curentrow);
      }
   }

   public void editItem4()
   {
      ASPManager mgr = getASPManager();

      if (itemlay4.isMultirowLayout())
         curentrow = itemset4.getRowSelected();
      else
         curentrow = itemset4.getCurrentRowNo();

      //sumColumns();

      itemset4.storeSelections();
      itemset4.goTo(curentrow);
      itemlay4.setLayoutMode(itemlay4.EDIT_LAYOUT);
   }

   public void sumColumns()
   {
      ASPManager mgr = getASPManager();

      //clearItem4();

      int n = itemset4.countRows();

      int dummyCurr = curentrow;


      curentrow = itemset4.getCurrentRowNo();


      itemset4.first();

      double totBudgCost = 0;
      double totBudgRev = 0;
      double totBudgMargin = 0;
      double totplannCost = 0;
      double totPlannRev = 0;
      double totplannMargin = 0;
      double totActCost = 0;
      double totActRev = 0;
      double totActMargin = 0;

      for (int i=1; i<=n; i++)
      {
         double budCost = itemset4.getRow().getNumberValue("BUDGET_COST");
         if (isNaN(budCost))
            budCost = 0;

         double budRev = itemset4.getRow().getNumberValue("BUDGET_REVENUE");
         if (isNaN(budRev))
            budRev = 0;

         double budMargine = itemset4.getRow().getNumberValue("NBUDGETMARGIN");
         if (isNaN(budMargine))
            budMargine = 0;

         double plannCost = itemset4.getRow().getNumberValue("NPLANNEDCOST");
         if (isNaN(plannCost))
            plannCost = 0;

         double plannRev =  itemset4.getRow().getNumberValue("NPLANNEDREVENUE");
         if (isNaN(plannRev))
            plannRev = 0;

         double planMargin = itemset4.getRow().getNumberValue("NPLANNEDMARGIN");
         if (isNaN(planMargin))
            planMargin = 0;

         double actCost = itemset4.getRow().getNumberValue("NACTUALCOST");
         if (isNaN(actCost))
            actCost = 0;

         double actRev = itemset4.getRow().getNumberValue("NACTUALREVENUE");
         if (isNaN(actRev))
            actRev = 0;

         double actMargin = itemset4.getRow().getNumberValue("NACTUALMARGIN");
         if (isNaN(actMargin))
            actMargin = 0;

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
      data = trans.getBuffer("ITEM4/DATA");
      itemset4.addRow(data);

      String summary = mgr.translate("PCMWACTIVESEPARATESERVICEWMANAGEMENTSUMMARY: Summary");

      buf = itemset4.getRow();

      buf.setValue("WORK_ORDER_COST_TYPE",summary);  
      buf.setNumberValue("BUDGET_COST",totBudgCost);
      buf.setNumberValue("BUDGET_REVENUE",totBudgRev);
      buf.setNumberValue("NBUDGETMARGIN",totBudgMargin);
      buf.setNumberValue("NPLANNEDCOST",totplannCost);
      buf.setNumberValue("NPLANNEDREVENUE",totPlannRev);
      buf.setNumberValue("NPLANNEDMARGIN",totplannMargin);
      buf.setNumberValue("NACTUALCOST",totActCost);
      buf.setNumberValue("NACTUALREVENUE",totActRev);
      buf.setNumberValue("NACTUALMARGIN",totActMargin);

      itemset4.setRow(buf);

      //if (("ITEM4.Forward".equals(mgr.readValue("__COMMAND")) && (curentrow == itemset4.countRows() - 1)) || ("ITEM4.Last".equals(mgr.readValue("__COMMAND"))))
      if ((dummyCurr == curentrow && curentrow == 5) && ("ITEM4.Forward".equals(mgr.readValue("__COMMAND"))) )
         itemset4.last();
      else
         itemset4.goTo(curentrow);
   }


   public void findCostAmount()
   {
      ASPManager mgr = getASPManager();

      if (headset.countRows()>0 && itemset2.countRows()>0)
      {
         itemset2.first();
         int n = itemset2.countRows(); 
         for (int i=1;i<=n;i++)
         {
            double nListPrice = itemset2.getRow().getNumberValue("COST");
            if (isNaN(nListPrice))
               nListPrice = 0;

            double PlanHrs = itemset2.getRow().getNumberValue("PLAN_HRS");
            if (isNaN(PlanHrs))
               PlanHrs = 0;

            double PlanMen = itemset2.getRow().getNumberValue("PLAN_MEN");
            if (isNaN(PlanMen))
               PlanMen = 0;

            double amountCost;

            if (PlanHrs == 0)
               amountCost = 0;
            else
            {
               if (PlanMen == 0)
                  amountCost = nListPrice * PlanHrs;
               else
                  amountCost = nListPrice * PlanHrs * PlanMen;
            }

            r = itemset2.getRow();
            r.setNumberValue("COSTAMOUNT",amountCost);

            itemset2.setRow(r);
            itemset2.next();
         }
         itemset2.first();
      }
   }

   public void getCost()
   {
      int count;
      ASPManager mgr = getASPManager();

      boolean secExists = false;
      secBuff = mgr.newASPTransactionBuffer();
      secBuff.addSecurityQuery("SALES_PART");

      secBuff = mgr.perform(secBuff);

      if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
         secExists = true;

      int noRows =  itemset2.countRows();
      double costVar;

      itemset2.first();
      for (count=1; count<= noRows; ++count)
      {
         trans.clear();  
         costVar = itemset2.getRow().getNumberValue("COST");
         if (isNaN(costVar))
            costVar = 0;

         if (!mgr.isEmpty(itemset2.getRow().getValue("ROLE_CODE")))
         {
            cmd = trans.addCustomFunction("ROLECOST"+count,"Role_API.Get_Role_Cost","COST");
            cmd.addParameter("ROLE_CODE",itemset2.getRow().getValue("ROLE_CODE")); 
            trans = mgr.perform(trans);

            costVar = trans.getNumberValue("ROLECOST"+count+"/DATA/COST"); 
            if (isNaN(costVar))
               costVar = 0;
         }
         if ((costVar == 0) && (!mgr.isEmpty(itemset2.getRow().getValue("ITEM2_ORG_CODE"))) && (!mgr.isEmpty(itemset2.getRow().getValue("ITEM2_CONTRACT"))))
         {
            cmd = trans.addCustomFunction("ORGCOST1"+count,"Organization_API.Get_Org_Cost","COST");
            cmd.addParameter("CONTRACT",itemset2.getRow().getValue("ITEM2_CONTRACT")); 
            cmd.addParameter("ORG_CODE",itemset2.getRow().getValue("ITEM2_ORG_CODE")); 
            trans = mgr.perform(trans);

            costVar = trans.getNumberValue("ORGCOST1"+count+"/DATA/COST");       
            if (isNaN(costVar))
               costVar = 0;
         }
         if ((costVar == 0) && (!mgr.isEmpty(headset.getRow().getValue("CONTRACT"))) && (!mgr.isEmpty(headset.getRow().getValue("ORG_CODE"))))
         {
            cmd = trans.addCustomFunction("ORGCOST2"+count,"Organization_API.Get_Org_Cost","COST");
            cmd.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT")); 
            cmd.addParameter("ORG_CODE",headset.getRow().getValue("ORG_CODE")); 
            trans = mgr.perform(trans);

            costVar = trans.getNumberValue("ORGCOST2"+count+"/DATA/COST");           
            if (isNaN(costVar))
               costVar = 0;
         }
         if ((costVar == 0) && (!mgr.isEmpty(headset.getRow().getValue("CATALOG_NO"))) && (secExists))
         {
            //Bug 69392, start
            if (mgr.isModuleInstalled("ORDER"))
            {
               cmd = trans.addCustomFunction("SLSPARTCOST"+count,"Sales_Part_API.Get_Cost","COST");
               cmd.addParameter("CATALOG_CONTRACT",itemset2.getRow().getValue("CATALOG_CONTRACT")); 
               cmd.addParameter("CATALOG_NO",itemset2.getRow().getValue("CATALOG_NO")); 
               trans = mgr.perform(trans);

               costVar = trans.getNumberValue("SLSPARTCOST"+count+"/DATA/COST"); 
            }
            //Bug 69392, end
            if (isNaN(costVar))
               costVar = 0;
         }
         buf = itemset2.getRow();
         buf.setNumberValue("COST",costVar);
         itemset2.setRow(buf);

         itemset2.next();   
      }
      findCostAmount();
   }

   public void getCostItem3()
   {
      int count;
      ASPManager mgr = getASPManager();

      secBuff = mgr.newASPTransactionBuffer();
      secBuff.addSecurityQuery("INVENTORY_PART");

      secBuff = mgr.perform(secBuff);

      if (secBuff.getSecurityInfo().itemExists("INVENTORY_PART"))
      {
         int noRows =  itemset3.countRows();
         itemset3.first();
         for (count=1; count<= noRows; ++count)
         {
            if (!mgr.isEmpty(itemset3.getRow().getValue("PART_NO")))
            {
               trans.clear();
               cmd = trans.addCustomFunction("INVENVALMETH"+count,"Inventory_Part_API.Get_Inventory_Value_By_Method","ITEM3_COST");
               cmd.addParameter("ITEM3_CATALOG_CONTRACT",itemset3.getRow().getValue("ITEM3_CATALOG_CONTRACT"));                        
               cmd.addParameter("ITEM3_CATALOG_NO",itemset3.getRow().getValue("ITEM3_CATALOG_NO"));                    
            }
            itemset3.next();
         }    

         trans = mgr.perform(trans);
         itemset3.first(); 
         for (count=1; count<= noRows; ++count)
         {
            if (!mgr.isEmpty(itemset3.getRow().getValue("PART_NO")))
            {
               double listPriceVar = trans.getNumberValue("INVENVALMETH"+count+"/DATA/COST");
               if (isNaN(listPriceVar))
                  listPriceVar = 0;
               buf = itemset3.getRow();
               buf.setNumberValue("ITEM3_COST",listPriceVar);
               if (!mgr.isEmpty(itemset3.getRow().getValue("QTY_REQUIRED")))
               {
                  double planQty = itemset3.getRow().getNumberValue("QTY_REQUIRED");
                  if (isNaN(planQty))
                     planQty = 0;
                  buf.setNumberValue("ITEM3_AMOUNT_COST",(listPriceVar * planQty)); 
               }
               itemset3.setRow(buf); 
            }
            itemset3.next();
         }    
         trans.clear();
      }
   }

   public void countSalesPriceAmountItem5()
   {
      int count;
      ASPManager mgr = getASPManager();

      int noRows =  itemset5.countRows();
      itemset5.first();
      trans.clear();
      for (count=1; count<= noRows; ++count)
      {
         cmd = trans.addCustomFunction("WOINV"+count, "Work_Order_Invoice_Type_API.Encode","WOINVOICETYPE");
         cmd.addParameter("WORK_ORDER_INVOICE_TYPE",itemset5.getRow().getValue("WORK_ORDER_INVOICE_TYPE"));
         itemset5.next();
      }   
      trans = mgr.perform(trans); 

      itemset5.first();
      for (count=1; count<= noRows; ++count)
      {
         String woInvTypeVar = trans.getValue("WOINV"+count+"/DATA/WOINVOICETYPE");

         double slsPriceVar = itemset5.getRow().getNumberValue("SALES_PRICE");
         if (isNaN(slsPriceVar))
            slsPriceVar = 0;

         double qtyVar = itemset5.getRow().getNumberValue("QUANTITY");
         if (isNaN(qtyVar))
            qtyVar = 0;

         double qtyToInvVar = itemset5.getRow().getNumberValue("QTY_TO_INVOICE");
         if (isNaN(qtyToInvVar))
            qtyToInvVar = 0;

         double discountVar = itemset5.getRow().getNumberValue("DISCOUNT");
         if (isNaN(discountVar))
            discountVar = 0;

         buf = itemset5.getRow();

         if ("AR".equals(woInvTypeVar))
         {
            if (discountVar == 0)
               buf.setNumberValue("ITEM5_SALES_PRICE_AMOUNT", (slsPriceVar * qtyVar));
            else
            {
               double val = slsPriceVar * qtyVar;
               double deduction = discountVar / 100 * val; 
               buf.setNumberValue("ITEM5_SALES_PRICE_AMOUNT", (val - deduction));
            }
         }

         else if ("MINQ".equals(woInvTypeVar))
         {
            if (qtyVar > qtyToInvVar)
            {
               if (discountVar == 0)
                  buf.setNumberValue("ITEM5_SALES_PRICE_AMOUNT",(slsPriceVar * qtyVar));
               else
               {
                  double val = slsPriceVar * qtyVar;
                  double deduction = discountVar / 100 * val; 
                  buf.setNumberValue("ITEM5_SALES_PRICE_AMOUNT",(val - deduction));
               }
            }
            else
            {
               if (discountVar == 0)
                  buf.setNumberValue("ITEM5_SALES_PRICE_AMOUNT",(slsPriceVar * qtyToInvVar));
               else
               {
                  double val = slsPriceVar * qtyToInvVar;
                  double deduction = discountVar / 100 * val; 
                  buf.setNumberValue("ITEM5_SALES_PRICE_AMOUNT",(val - deduction));
               }     
            }    
         }
         else if ("MAXQ".equals(woInvTypeVar))
         {
            if (qtyVar < qtyToInvVar)
            {
               if (discountVar == 0)
                  buf.setNumberValue("ITEM5_SALES_PRICE_AMOUNT",(slsPriceVar * qtyVar));
               else
               {
                  double val = slsPriceVar * qtyVar;
                  double deduction = discountVar / 100 * val; 
                  buf.setNumberValue("ITEM5_SALES_PRICE_AMOUNT",(val - deduction));
               }
            }
            else
            {
               if (discountVar == 0)
                  buf.setNumberValue("ITEM5_SALES_PRICE_AMOUNT",(slsPriceVar * qtyToInvVar));
               else
               {
                  double val = slsPriceVar * qtyToInvVar;
                  double deduction = discountVar / 100 * val; 
                  buf.setNumberValue("ITEM5_SALES_PRICE_AMOUNT",(val - deduction));
               }            
            }     
         }
         else if (( "FL".equals(woInvTypeVar) ) ||  ( mgr.isEmpty(woInvTypeVar) ))
         {
            if (discountVar == 0)
               buf.setNumberValue("ITEM5_SALES_PRICE_AMOUNT",(slsPriceVar * qtyToInvVar));
            else
            {
               double val = slsPriceVar * qtyToInvVar;
               double deduction = discountVar / 100 * val; 
               buf.setNumberValue("ITEM5_SALES_PRICE_AMOUNT",(val - deduction));      
            }  
         }
         itemset5.setRow(buf); 
         itemset5.next();
      }
      trans.clear();
   }


   public void getCostItem5()
   {
      ASPManager mgr = getASPManager();
      double costAmtVar = 0;

      if (!mgr.isEmpty(itemset5.getRow().getValue("CATALOG_NO")))
      {
         trans.clear();

         //Bug 69392, start
         if (mgr.isModuleInstalled("ORDER"))
         {
            cmd = trans.addCustomFunction("GETCOST5","Sales_Part_API.Get_Cost","ITEM5COST");
            cmd.addParameter("ITEM5_CATALOG_CONTRACT",itemset5.getRow().getValue("CATALOG_CONTRACT"));
            cmd.addParameter("ITEM5_CATALOG_NO",itemset5.getRow().getValue("CATALOG_NO"));

            trans = mgr.perform(trans); 

            costAmtVar = trans.getNumberValue("GETCOST5/DATA/COST");
         }
         //Bug 69392, end
         if (isNaN(costAmtVar))
            costAmtVar = 0;

         if ((costAmtVar == 0) && (!mgr.isEmpty(headset.getRow().getValue("CONTRACT"))) && (!mgr.isEmpty(headset.getRow().getValue("ORG_CODE"))))
         {
            trans.clear();

            cmd = trans.addCustomFunction("GETORGCOST5","Organization_API.Get_Org_Cost","ITEM5COST");
            cmd.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT")); 
            cmd.addParameter("ORG_CODE",headset.getRow().getValue("ORG_CODE"));

            trans = mgr.perform(trans); 

            costAmtVar = trans.getNumberValue("GETORGCOST5/DATA/COST");      
            if (isNaN(costAmtVar))
               costAmtVar = 0;
         }
         buf = itemset5.getRow();
         buf.setNumberValue("COST",costAmtVar);
         itemset5.setRow(buf);
      }
      trans.clear();
   }


   public void getSalesPartInfoItem5()
   {
      ASPManager mgr = getASPManager();

      if ((checksec("Company_Finance_API.Get_Currency_Code",2)) && (checksec("Customer_Order_Pricing_API.Get_Valid_Price_List",3)))
      {
         trans.clear();

         cmd = trans.addCustomCommand("INFO5", "Work_Order_Coding_API.Get_Price_Info");
         cmd.addParameter("ITEM5_BASE_UNIT_PRICE","");
         cmd.addParameter("ITEM5_SALE_UNIT_PRICE","");
         cmd.addParameter("ITEM5_DISCOUNT","");
         cmd.addParameter("ITEM5_CURRENCY_RATE","");
         cmd.addParameter("ITEM5_CATALOG_CONTRACT",itemset5.getRow().getValue("CATALOG_CONTRACT"));
         cmd.addParameter("ITEM5_CATALOG_NO",itemset5.getRow().getValue("CATALOG_NO"));
         cmd.addParameter("CUSTOMER_NO",headset.getRow().getValue("CUSTOMER_NO"));
         cmd.addParameter("AGREEMENT_ID",headset.getRow().getValue("AGREEMENT_ID"));
         cmd.addParameter("ITEM5_PRICE_LIST_NO",itemset5.getRow().getValue("PRICE_LIST_NO"));
         cmd.addParameter("ITEM5_BUY_QTY_DUE",itemset5.getRow().getValue("QTY_REQUIRED"));

         trans = mgr.perform(trans); 

         double baseUnitPriceVar = trans.getNumberValue("INFO5/DATA/ITEM5_BASE_UNIT_PRICE");
         if (isNaN(baseUnitPriceVar))
            baseUnitPriceVar = 0;

         double saleUnitPriceVar = trans.getNumberValue("INFO5/DATA/ITEM5_SALE_UNIT_PRICE");
         if (isNaN(saleUnitPriceVar))
            saleUnitPriceVar = 0;

         double discountVar = trans.getNumberValue("INFO5/DATA/ITEM5_DISCOUNT");
         if (isNaN(discountVar))
            discountVar = 0;

         double currencyVar = trans.getNumberValue("INFO5/DATA/ITEM5_CURRENCY_RATE");
         if (isNaN(currencyVar))
            currencyVar = 0;

         String priceListNoVar = trans.getValue("INFO5/DATA/ITEM5_PRICE_LIST_NO");
         double buyQtyDueVar = trans.getNumberValue("INFO5/DATA/ITEM5_BUY_QTY_DUE");                      
         if (isNaN(buyQtyDueVar))
            buyQtyDueVar = 0;

         buf = itemset5.getRow();
         if (mgr.isEmpty(itemset5.getRow().getValue("ITEM5_PRICE_LIST_NO")))
            buf.setValue("ITEM5_PRICE_LIST_NO",priceListNoVar);
         buf.setNumberValue("ITEM5_SALES_PRICE",baseUnitPriceVar);
         buf.setNumberValue("ITEM5_DISCOUNT",discountVar);
         buf.setNumberValue("ITEM5_SALES_PRICE_AMOUNT",(saleUnitPriceVar * buyQtyDueVar));
         itemset5.setRow(buf); 
         trans.clear();
      }
   }

   public void getCostAmountItem5()
   {
      int count;
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomFunction("GETCOSTTYPE2","Work_Order_Cost_Type_Api.Get_Client_Value(2)","COSTTYPE2");
      cmd = trans.addCustomFunction("GETCOSTTYPE3","Work_Order_Cost_Type_Api.Get_Client_Value(3)","COSTTYPE3");

      trans = mgr.perform(trans);

      String sCostTypeExternal = trans.getValue("GETCOSTTYPE2/DATA/COSTTYPE2");
      String sCostTypeExpenses = trans.getValue("GETCOSTTYPE3/DATA/COSTTYPE3");

      trans.clear();
      secBuff = mgr.newASPTransactionBuffer();
      secBuff.addSecurityQuery("SALES_PART");

      secBuff = mgr.perform(secBuff);

      boolean secAvailable = false;

      if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
         secAvailable = true;
      else
         secAvailable = false;    

      int noRows =  itemset5.countRows();
      itemset5.first();
      for (count=1; count<= noRows; ++count)
      {
         String woCostTypeVar = itemset5.getRow().getValue("WORK_ORDER_COST_TYPE");

         if (( sCostTypeExternal.equals(woCostTypeVar) ) ||  ( sCostTypeExpenses.equals(woCostTypeVar) ))
         {
            if (mgr.isEmpty(itemset5.getRow().getValue("COST")))
            {
               if (secAvailable)
                  getCostItem5();
            }
         }
         buf = itemset5.getRow();
         double costVar = itemset5.getRow().getNumberValue("COST");
         if (isNaN(costVar))
            costVar = 0;

         double qtyVar = itemset5.getRow().getNumberValue("QUANTITY");              
         if (isNaN(qtyVar))
            qtyVar = 0;

         if (costVar > 0)
            buf.setNumberValue("ITEM5_COST_AMOUNT",(costVar * qtyVar));
         else
            buf.setNumberValue("ITEM5_COST_AMOUNT",0);
         itemset5.setRow(buf);   
         itemset5.next();   
      }     
   }

//-----------------------------------------------------------------------------
//------------------------  HEADBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   // 031204  ARWILK  Begin  (Replace links with RMB's)
   public void reportInWorkOrderSM()
   {
      ASPManager mgr = getASPManager();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      if (headlay.isMultirowLayout())
         headset.store();
      else
      {
         headset.unselectRows();
         headset.selectRow();
      }

      bOpenNewWindow = true;
      urlString = createTransferUrl("ActiveSeperateReportInWorkOrderSM.page", headset.getSelectedRows("WO_NO"));
      newWinHandle = "reportInWorkOrderSM"; 
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
   }
   // 031204  ARWILK  End  (Replace links with RMB's)

   public void structure()
   {
      ASPManager mgr = getASPManager();

      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      if (headlay.isMultirowLayout())
         headset.store();
      else
      {
         headset.unselectRows();
         headset.selectRow();
      }

      bOpenNewWindow = true;
      urlString = createTransferUrl("SeparateWorkOrder.page", headset.getSelectedRows("WO_NO"));
      newWinHandle = "structure"; 
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
   }

   public void prePost()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      enabl10 = 0;
      String winname1="prepareWorkOrdSM";

      String contract = headset.getRow().getValue("CONTRACT");
      String wo_no = headset.getRow().getValue("WO_NO");
      lout = (headlay.isMultirowLayout()?"1":"0");

      GetEnabledFields();
      makePrePostBuffer();
   }

   public void returns()
   {
      ASPManager mgr = getASPManager();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL",calling_url);

      bOpenNewWindow = true;
      urlString = createTransferUrl("WorkOrderReturns.page", headset.getSelectedRows("WO_NO"));
      newWinHandle = "returns";       
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
   }

   //----------------------------- get the enabled fields to be  passed to preposting RMB-----------------------------------

   public void GetEnabledFields()
   {
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


      if (!("0".equals(code_a)))  enabl0 = "1";
      else enabl0 = "0";
      if ("0".equals(code_b))  enabl1 = "0";
      else enabl1 = "1";
      if ("0".equals(code_c))  enabl2 = "0";
      else enabl2 = "1";
      if ("0".equals(code_d))  enabl3 = "0";
      else enabl3 = "1";
      if ("0".equals(code_e))  enabl4 = "0";
      else enabl4 = "1";
      if ("0".equals(code_f))  enabl5 = "0";
      else enabl5 = "1";
      if ("0".equals(code_g))  enabl6 = "0";
      else enabl6 = "1";
      if ("0".equals(code_h))  enabl7 = "0";
      else enabl7 = "1";
      if ("0".equals(code_i))  enabl8 = "0";
      else enabl8 = "1";
      if ("0".equals(code_j))  enabl9 = "0";
      else enabl9 = "1"; 
   }

   public void  makePrePostBuffer()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer prepost_buffer = mgr.newASPBuffer();
      ASPBuffer data = prepost_buffer.addBuffer("dataBuffer");
      data.addItem("CONTRACT",headset.getValue("CONTRACT"));
      data.addItem("PRE_ACCOUNTING_ID",headset.getRow().getValue("PRE_ACCOUNTING_ID"));
      data.addItem("ENABL0",enabl0);
      data.addItem("ENABL1",enabl1);
      data.addItem("ENABL2",enabl2);
      data.addItem("ENABL3",enabl3);
      data.addItem("ENABL4",enabl4);
      data.addItem("ENABL5",enabl5);
      data.addItem("ENABL6",enabl6);
      data.addItem("ENABL7",enabl7);
      data.addItem("ENABL8",enabl8);
      data.addItem("ENABL9",enabl9);
      data.addItem("ENABL10",Integer.toString(enabl10));

      ASPBuffer return_buffer = prepost_buffer.addBuffer("return_buffer");
      ASPBuffer ret = return_buffer.addBuffer("ROWS");
      ret.parse(headset.getRows("WO_NO").format());
      return_buffer.addItem("CURR_ROW",Integer.toString(headset.getCurrentRowNo()));
      return_buffer.addItem("LAYOUT",lout);

      mgr.transferDataTo("../mpccow/PreAccountingDlg.page",prepost_buffer);
   }

   public void printWO()
   {
      ASPManager mgr = getASPManager();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
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
         int nWoNo = new Double(headset.getValue("WO_NO")).intValue();
        
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

      // 040123  ARWILK  End  (Enable Multirow RMB actions)
      callPrintDlg(print,true);       
   }

   public void printServO()
   {
      ASPManager mgr = getASPManager();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
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
         int nWoNo = new Double(headset.getRow().getFieldValue("WO_NO")).intValue();
        
         attr1 = "REPORT_ID" + (char)31 + "PRINT_WORK_ORDER_SE_MA_REP" + (char)30;       
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

      // 040123  ARWILK  End  (Enable Multirow RMB actions)
      callPrintDlg(print,true);       
   }

   public void requisitions()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer transferBuffer;
      ASPBuffer rowBuff;

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      //Retrive the current object status
      trans.clear();

      cmd = trans.addCustomFunction("GETOBJSTATE", "Active_Separate_Api.Get_Obj_State", "OBJSTATE" );
      cmd.addParameter("WO_NO", headset.getRow().getValue("WO_NO"));

      trans = mgr.perform(trans);

      String sObjState  = trans.getBuffer("GETOBJSTATE/DATA").getFieldValue("OBJSTATE");

      transferBuffer = mgr.newASPBuffer();
      rowBuff = mgr.newASPBuffer();

      rowBuff.addItem("WO_NO", headset.getRow().getValue("WO_NO"));
      rowBuff.addItem("MCH_CODE", headset.getRow().getValue("MCH_CODE"));
      rowBuff.addItem("MCH_NAME", headset.getRow().getValue("MCH_CODE_DESCRIPTION"));
      rowBuff.addItem("CUSTOMER_NO", headset.getRow().getValue("CUSTOMER_NO"));
      rowBuff.addItem("AGREEMENT_ID", headset.getRow().getValue("AGREEMENT_ID"));
      rowBuff.addItem("OBJSTATE", sObjState);

      transferBuffer.addBuffer("DATA", rowBuff);

      bOpenNewWindow = true;
      urlString = createTransferUrl("WorkOrderRequisHeaderRMB.page", transferBuffer);
      newWinHandle = "requisitions";        
   }

   public void freeNotes()
   {
      ASPManager mgr = getASPManager();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();
      // 040123  ARWILK  End  (Enable Multirow RMB actions)

      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      // 040123  ARWILK  Begin  (Remove uneccessary global variables)
      bOpenNewWindow = true;
      urlString = "WorkOrderReportPageOvw2.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO"));
      newWinHandle = "freeNotes"; 
      // 040123  ARWILK  End  (Remove uneccessary global variables)
   }

   public void permits()
   {
      ASPManager mgr = getASPManager();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();
      // 040123  ARWILK  End  (Enable Multirow RMB actions)

      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      // 040123  ARWILK  Begin  (Remove uneccessary global variables)
      bOpenNewWindow = true;
      urlString = "WorkOrderPermit.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO"));
      newWinHandle = "permits"; 
      // 040123  ARWILK  End  (Remove uneccessary global variables)
   }

   public void suppWarr()
   {
      ASPManager mgr = getASPManager();

      int currrow = headset.getCurrentRowNo();

      headset.goTo(currrow);

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      // 040123  ARWILK  Begin  (Remove uneccessary global variables)
      bOpenNewWindow = true;
      urlString = "SupplierWarrantyType.page?MCH_CODE=" + mgr.URLEncode(headset.getRow().getValue("MCH_CODE")) +
                  "&CONTRACT=" + mgr.URLEncode(headset.getRow().getValue("MCH_CODE_CONTRACT")) +
                  "&ROW_NO=" + mgr.URLEncode(headset.getRow().getValue("OBJ_SUP_WARRANTY")) +
                  "&WARRANTY_ID=" + mgr.URLEncode(headset.getRow().getValue("SUP_WARRANTY")) +
                  "&WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) +
                  "&FRMNAME=" + mgr.URLEncode("prepareWorkOrdSM") +
                  "&QRYSTR=" + mgr.URLEncode(qrystr);

      newWinHandle = "custwarr"; 
      // 040123  ARWILK  End  (Remove uneccessary global variables)
   }

   public void repInWiz()
   {
      // 040123  ARWILK  Begin  (Remove uneccessary global variables)
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      bOpenNewWindow = true;

      urlString = "ReportInWorkOrderWiz.page?WO_NO=" + mgr.URLEncode(headset.getValue("WO_NO"));

      newWinHandle = "repInWiz"; 
      // 040123  ARWILK  End  (Remove uneccessary global variables)
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

   public void freeTimeSearchFromOperations1()
   {
      ASPManager mgr = getASPManager();

      if (itemlay2.isMultirowLayout())
         itemset2.goTo(itemset2.getRowSelected());

      bOpenNewWindow = true;
      urlString = "FreeTimeSearchEngine.page?RES_TYPE=EMPLOYEE" +
                  "&CONTRACT=" + (mgr.isEmpty(itemset2.getValue("CONTRACT"))?"":itemset2.getValue("CONTRACT")) + 
                  "&ORG_CODE="+ (mgr.isEmpty(itemset2.getValue("ORG_CODE"))?"":itemset2.getValue("ORG_CODE")) + 
                  "&COMPETENCE="+ (mgr.isEmpty(itemset2.getValue("ROLE_CODE"))?"":itemset2.getValue("ROLE_CODE")) +
                  "&EMPLOYEE="+ (mgr.isEmpty(itemset2.getValue("SIGN_ID"))?"":itemset2.getValue("SIGN_ID")) +
                  "&WO_NO="+ (mgr.isEmpty(itemset2.getValue("WO_NO"))?"":itemset2.getValue("WO_NO")) +
                  "&ROW_NO="+ (mgr.isEmpty(itemset2.getValue("ROW_NO"))?"":itemset2.getValue("ROW_NO")) +
                  "&OBJ_ID="+ (mgr.isEmpty(itemset2.getValue("OBJID"))?"":itemset2.getValue("OBJID")) +
                  "&OBJ_VERSION="+ (mgr.isEmpty(itemset2.getValue("OBJVERSION"))?"":itemset2.getValue("OBJVERSION"));
      newWinHandle = "freeTimeSearchFromOperations"; 
   }

   public void freeTimeSearchFromOperations2()
   {
      ASPManager mgr = getASPManager();

      if (itemlay8.isMultirowLayout())
         itemset8.goTo(itemset8.getRowSelected());

      bOpenNewWindow = true;
      urlString = "FreeTimeSearchEngine.page?RES_TYPE=EMPLOYEE" +
                  "&CONTRACT=" + (mgr.isEmpty(itemset8.getValue("CONTRACT"))?"":itemset8.getValue("CONTRACT")) + 
                  "&ORG_CODE="+ (mgr.isEmpty(itemset8.getValue("ORG_CODE"))?"":itemset8.getValue("ORG_CODE")) + 
                  "&COMPETENCE="+ (mgr.isEmpty(itemset8.getValue("ROLE_CODE"))?"":itemset8.getValue("ROLE_CODE")) +
                  "&EMPLOYEE="+ (mgr.isEmpty(itemset8.getValue("SIGN_ID"))?"":itemset8.getValue("SIGN_ID")) +
                  "&WO_NO="+ (mgr.isEmpty(itemset8.getValue("WO_NO"))?"":itemset8.getValue("WO_NO")) +
                  "&ROW_NO="+ (mgr.isEmpty(itemset8.getValue("ROW_NO"))?"":itemset8.getValue("ROW_NO")) +
                  "&OBJ_ID="+ (mgr.isEmpty(itemset8.getValue("OBJID"))?"":itemset8.getValue("OBJID")) +
                  "&OBJ_VERSION="+ (mgr.isEmpty(itemset8.getValue("OBJVERSION"))?"":itemset8.getValue("OBJVERSION"));
      newWinHandle = "freeTimeSearchFromOperations2"; 
   }


   public void additionalQualifications1()
   {
      ASPManager mgr = getASPManager();
      //this is needed when a new WO is created.(createSearchURL is undefined at this instance)
      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      int head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);
      ctx.setGlobal("HEADGLOBAL", s_head_curr);

      if (itemlay2.isMultirowLayout())
         itemset2.goTo(itemset2.getRowSelected());

      bOpenNewWindow = true;
      urlString = "WOQualificationsDlg.page?RES_TYPE=QUALIFICATIONS"+
                  "&WO_NO="+ (mgr.isEmpty(itemset2.getValue("WO_NO"))?"":itemset2.getValue("WO_NO")) +
                  "&ROW_NO="+ (mgr.isEmpty(itemset2.getValue("ROW_NO"))?"":itemset2.getValue("ROW_NO"))+
                  "&QRYSTR=" + mgr.URLEncode(qrystr) +
                  "&FRMNAME=prepareWorkOrdSM";

      newWinHandle = "additionalQualifications1"; 
   }

   public void additionalQualifications2()
   {
      ASPManager mgr = getASPManager();
      //this is needed when a new WO is created.(createSearchURL is undefined at this instance)
      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      int head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);
      ctx.setGlobal("HEADGLOBAL", s_head_curr);

      if (itemlay8.isMultirowLayout())
         itemset8.goTo(itemset8.getRowSelected());

      bOpenNewWindow = true;
      urlString = "WOQualificationsDlg.page?RES_TYPE=QUALIFICATIONS"+
                  "&WO_NO="+ (mgr.isEmpty(itemset8.getValue("WO_NO"))?"":itemset8.getValue("WO_NO")) +
                  "&ROW_NO="+ (mgr.isEmpty(itemset8.getValue("ROW_NO"))?"":itemset8.getValue("ROW_NO"))+
                  "&QRYSTR=" + mgr.URLEncode(qrystr) +
                  "&FRMNAME=prepareWorkOrdSM";

      newWinHandle = "additionalQualifications2"; 
   }
   //-----------

   public void allocateEmployees0()
   {
      //called at operations tab
      ASPManager mgr = getASPManager();
      int count;
      ASPBuffer data_buff;
      ASPBuffer rowBuff;
      //this is needed when a new WO is created.(createSearchURL is undefined at this instance)
      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);
      ctx.setGlobal("QRYSTR", qrystr);
      ctx.setGlobal("FRAMENAME","prepareWorkOrd");

      ctx.setGlobal("WO_NO", itemset2.getValue("WO_NO"));
      int head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);
      ctx.setGlobal("HEADGLOBAL", s_head_curr);


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

      //int planMen=0;
      //double planHrs,manHrs=0;
      data_buff = mgr.newASPBuffer(); 
      for (int i = 0; i < count; i++)
      {

         rowBuff = mgr.newASPBuffer();  
         /*if (mgr.isEmpty(itemset2.getValue("PLAN_MEN"))) 
            planMen = 0; 
         else 
            planMen = Integer.parseInt(itemset2.getValue("PLAN_MEN"));

         if (mgr.isEmpty(itemset2.getValue("PLAN_HRS"))) 
            planHrs = 0;
         else 
            planHrs = itemset2.getNumberValue("PLAN_HRS");
         
         manHrs = planMen * planHrs;
         
         if ((manHrs >0 ) && (mgr.isEmpty(itemset2.getValue("SIGN_ID")))) 
         {*/ 
         rowBuff.addItem("ROW_NO", itemset2.getValue("ROW_NO"));
         rowBuff.addItem("ROW_NO", itemset2.getValue("ROW_NO"));
         data_buff.addBuffer("DATA", rowBuff);    
         //}

         if (itemlay2.isMultirowLayout())
            itemset2.next();

      }

      if (itemlay2.isMultirowLayout())
         itemset2.setFilterOff();

      bOpenNewWindow = true;
      urlString= createTransferUrl("AllocateEmployees.page?&RES_TYPE=EMPLOYEE"+
                                   "&WO_NO="+ (mgr.isEmpty(headset.getValue("WO_NO"))?"":headset.getValue("WO_NO")+
                                               "&QRYSTR=" + mgr.URLEncode(qrystr) +
                                               "&FRMNAME=prepareWorkOrdSM"),data_buff);


      newWinHandle = "allocateEmployees0"; 
   }

   public void allocateEmployees1()
   {
      //called at jobs tab
      ASPManager mgr = getASPManager();
      int count;
      int count1;
      ASPBuffer data_buff;
      ASPBuffer rowBuff;
      //this is needed when a new WO is created.(createSearchURL is undefined at this instance)
      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      ctx.setGlobal("WO_NO", headset.getValue("WO_NO"));
      int head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);
      ctx.setGlobal("HEADGLOBAL", s_head_curr);


      if (itemlay7.isMultirowLayout())
      {
         itemset7.storeSelections();
         itemset7.setFilterOn();
         count = itemset7.countSelectedRows();
      }
      else
      {
         itemset7.unselectRows();
         count = 1;
      }

      for (int i = 0; i < count; i++)
      {
         cmd = trans.addQuery("GETROWS"+i,"SELECT ROW_NO,PLAN_HRS,PLAN_MEN,SIGN_ID FROM WORK_ORDER_ROLE WHERE WO_NO = ? AND JOB_ID = ?");
         cmd.addParameter("WO_NO",headset.getValue("WO_NO"));
         cmd.addParameter("JOB_ID",itemset7.getValue("JOB_ID"));

         if (itemlay7.isMultirowLayout())
            itemset7.next();

      }

      trans = mgr.perform(trans);
      int planMen,planHrs,manHrs;
      data_buff = mgr.newASPBuffer();
      for (int i = 0; i < count; i++)
      {
         ASPBuffer getEvent = trans.getBuffer("GETROWS"+i);
         count1 = getEvent.countItems();

         for (int j = 0; j < count1 - 1; j++)
         {
            rowBuff = mgr.newASPBuffer();
            /*if (mgr.isEmpty(getEvent.getBufferAt(j).getValue("PLAN_MEN"))) 
               planMen = 0; 
            else 
               planMen = Integer.parseInt(getEvent.getBufferAt(j).getValue("PLAN_MEN"));

            if (mgr.isEmpty(getEvent.getBufferAt(j).getValue("PLAN_HRS"))) 
               planHrs = 0;
            else 
               planHrs = Integer.parseInt(getEvent.getBufferAt(j).getValue("PLAN_HRS"));

            manHrs = planMen * planHrs;
            

            if ((manHrs > 0 ) && (mgr.isEmpty(getEvent.getBufferAt(j).getValue("SIGN_ID")))) 
            { */
            rowBuff.addItem("ROW_NO", getEvent.getBufferAt(j).getValue("ROW_NO"));
            rowBuff.addItem("ROW_NO", getEvent.getBufferAt(j).getValue("ROW_NO"));
            data_buff.addBuffer("DATA", rowBuff);
            //}
         }  

      }


      if (itemlay7.isMultirowLayout())
         itemset7.setFilterOff();

      bOpenNewWindow = true;
      urlString= createTransferUrl("AllocateEmployees.page?&RES_TYPE=EMPLOYEE"+
                                   "&WO_NO="+ (mgr.isEmpty(headset.getValue("WO_NO"))?"":headset.getValue("WO_NO")+
                                               "&QRYSTR=" + mgr.URLEncode(qrystr) +
                                               "&FRMNAME=prepareWorkOrdSM"),data_buff);

      newWinHandle = "allocateEmployees1";   
   } 

   public void allocateEmployees2()
   {
      //called at operations in Jobs tab
      ASPManager mgr = getASPManager();
      int count;
      ASPBuffer data_buff;
      ASPBuffer rowBuff;
      //this is needed when a new WO is created.(createSearchURL is undefined at this instance)
      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      ctx.setGlobal("WO_NO", itemset8.getValue("WO_NO"));
      int head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);
      ctx.setGlobal("HEADGLOBAL", s_head_curr);


      if (itemlay8.isMultirowLayout())
      {
         itemset8.storeSelections();
         itemset8.setFilterOn();
         count = itemset8.countSelectedRows();
      }
      else
      {
         itemset8.unselectRows();
         count = 1;
      }
      //int planMen,planHrs,manHrs;
      data_buff = mgr.newASPBuffer();

      for (int i = 0; i < count; i++)
      {

         rowBuff = mgr.newASPBuffer();  
         /*if (mgr.isEmpty(itemset8.getValue("PLAN_MEN"))) 
            planMen = 0; 
         else 
            planMen = Integer.parseInt(itemset8.getValue("PLAN_MEN"));

         if (mgr.isEmpty(itemset8.getValue("PLAN_HRS"))) 
            planHrs = 0;
         else 
            planHrs = Integer.parseInt(itemset8.getValue("PLAN_HRS"));
         
         manHrs = planMen * planHrs;
         
         if ((manHrs >0 ) && (mgr.isEmpty(itemset8.getValue("SIGN_ID")))) 
         {*/ 
         rowBuff.addItem("ROW_NO", itemset8.getValue("ROW_NO"));
         rowBuff.addItem("ROW_NO", itemset8.getValue("ROW_NO"));
         data_buff.addBuffer("DATA", rowBuff);    
         //}

         if (itemlay8.isMultirowLayout())
            itemset8.next();
      }

      if (itemlay8.isMultirowLayout())
         itemset8.setFilterOff();


      bOpenNewWindow = true;
      urlString= createTransferUrl("AllocateEmployees.page?&RES_TYPE=EMPLOYEE"+
                                   "&WO_NO="+ (mgr.isEmpty(headset.getValue("WO_NO"))?"":headset.getValue("WO_NO")+
                                               "&QRYSTR=" + mgr.URLEncode(qrystr) +
                                               "&FRMNAME=prepareWorkOrdSM"),data_buff);


      newWinHandle = "allocateEmployees2"; 
   }

   public void selectAllocation0()
   {
      //called at operations tab
      ASPManager mgr = getASPManager();
      headRowNo = headset.getCurrentRowNo();   

      int count;
      rowString="";
      if (itemlay2.isMultirowLayout())
      {
         itemset2.storeSelections();
         count = itemset2.countRows();

      }
      else
      {
         currentRow=itemset2.getCurrentRowNo();
         rowString =String.valueOf(currentRow); 
         itemset2.unselectRows();
         count = 1;
      } 


      itemset2.first();
      for (int i = 0; i < count; i++)
      {

         if (itemset2.isRowSelected())
         {
            if (i==0)
               rowString =String.valueOf(itemset2.getCurrentRowNo());
            else
               rowString = rowString + "^" + String.valueOf(itemset2.getCurrentRowNo());  

            currentRow=itemset2.getCurrentRowNo();
         }

         if (itemlay2.isMultirowLayout())
            itemset2.next();
      }

      if (count!=1 )
         rowString = rowString + "^";

      if (itemlay2.isMultirowLayout())
         itemset2.setFilterOff();

      clearAlloc0 = "TRUE";
      ctx.setGlobal("ROWSTRING", rowString);
      ctx.setCookie( "PageID_clearAllocation1", "TRUE" );

   }

   public void clearAllocation0()
   {
      //refers to operations tab
      ASPManager mgr = getASPManager();
      rowString = ctx.getGlobal("ROWSTRING");
      String ar[] = split(rowString,"^"); 

      if (rowString.indexOf("^",0)>0)
      {
         itemset2.first();
         for (int i=0 ; i<itemset2.countRows(); i++)
         {

            itemset2.goTo(i);

            for (int j=0 ; j<(ar.length - 1); j++)
            {

               if (i == Integer.parseInt(ar[j]))
               {

                  ASPBuffer buff = itemset2.getRow();
                  if (!mgr.isEmpty(itemset2.getValue("SIGN")))
                     buff.setFieldItem("SIGN","");
                  if (!mgr.isEmpty(itemset2.getValue("SIGN_ID")))
                     buff.setFieldItem("SIGN_ID","");
                  if (!mgr.isEmpty(itemset2.getValue("TEAM_ID")))
                     buff.setFieldItem("TEAM_ID","");

                  itemset2.setRow(buff);
               }
            }
            itemset2.next();


         }
      }
      else
      {
         itemset2.goTo(currentRow);
         if (!mgr.isEmpty(itemset2.getValue("SIGN")))
            itemset2.setValue("SIGN","");
         if (!mgr.isEmpty(itemset2.getValue("SIGN_ID")))
            itemset2.setValue("SIGN_ID","");
         if (!mgr.isEmpty(itemset2.getValue("TEAM_ID")))
            itemset2.setValue("TEAM_ID","");

      }

      mgr.submit(trans);
      headset.goTo(headRowNo);   
   }


   public void selectAllocation1()
   {
      //called at jobs tab
      ASPManager mgr = getASPManager();
      headRowNo = headset.getCurrentRowNo();   

      int count;
      rowString="";

      if (itemlay7.isMultirowLayout())
      {
         itemset7.storeSelections();
         count = itemset7.countRows();

      }
      else
      {

         currentRow=itemset7.getCurrentRowNo();
         rowString =String.valueOf(currentRow); 
         itemset7.unselectRows();
         count = 1;

      } 


      itemset7.first();
      for (int i = 0; i < count; i++)
      {

         if (itemset7.isRowSelected())
         {
            if (i==0)
               rowString =String.valueOf(itemset7.getCurrentRowNo());
            else
               rowString = rowString + "^" + String.valueOf(itemset7.getCurrentRowNo());  

            currentRow=itemset7.getCurrentRowNo();
         }

         if (itemlay7.isMultirowLayout())
            itemset7.next();
      }

      if (count!=1 )
         rowString = rowString + "^";

      if (itemlay7.isMultirowLayout())
         itemset7.setFilterOff();

      clearAlloc1 = "TRUE";
      ctx.setGlobal("ROWSTRING", rowString);
      ctx.setCookie( "PageID_clearAllocation2", "TRUE" );

   }

   public void clearAllocation1()
   {
      //relates to jobs tab
      ASPManager mgr = getASPManager();
      rowString = ctx.getGlobal("ROWSTRING");
      String ar[] = split(rowString,"^"); 
      trans.clear();

      if (rowString.indexOf("^",0)>0)
      {
         itemset7.first();
         for (int i=0 ; i<itemset7.countRows(); i++)
         {
            itemset7.goTo(i);
            for (int j=0 ; j<(ar.length - 1); j++)
            {
               if (i == Integer.parseInt(ar[j]))
               {
                  cmd = trans.addCustomCommand("JOBLINES"+i,"Work_Order_Role_API.Clear_Allocation");
                  cmd.addParameter("WO_NO",headset.getValue("WO_NO"));
                  cmd.addParameter("JOB_ID",itemset7.getValue("JOB_ID"));
               }
            }
            itemset7.next();


         }


      }
      else
      {
         itemset7.goTo(currentRow);
         cmd = trans.addCustomCommand("JOBLINES","Work_Order_Role_API.Clear_Allocation");
         cmd.addParameter("WO_NO",headset.getValue("WO_NO"));
         cmd.addParameter("JOB_ID",itemset7.getValue("JOB_ID"));

      }
      mgr.submit(trans);
      headset.goTo(headRowNo);   


   }

   public void selectAllocation2()
   {
      //called from operations in the jobs tab.
      ASPManager mgr = getASPManager();
      headRowNo = headset.getCurrentRowNo();   

      int count;
      rowString="";
      if (itemlay8.isMultirowLayout())
      {
         itemset7.storeSelections();
         count = itemset8.countRows();

      }
      else
      {
         currentRow=itemset8.getCurrentRowNo();
         rowString =String.valueOf(currentRow); 
         itemset8.unselectRows();
         count = 1;
      } 


      itemset8.first();
      for (int i = 0; i < count; i++)
      {

         if (itemset8.isRowSelected())
         {
            if (i==0)
               rowString =String.valueOf(itemset8.getCurrentRowNo());
            else
               rowString = rowString + "^" + String.valueOf(itemset8.getCurrentRowNo());  

            currentRow=itemset8.getCurrentRowNo();
         }

         if (itemlay8.isMultirowLayout())
            itemset8.next();
      }

      if (count!=1 )
         rowString = rowString + "^";

      if (itemlay8.isMultirowLayout())
         itemset8.setFilterOff();

      clearAlloc2 = "TRUE";
      ctx.setGlobal("ROWSTRING", rowString);
      ctx.setCookie( "PageID_clearAllocation3", "TRUE" );

   }

   public void clearAllocation2()
   {
      //refers to operations tab
      ASPManager mgr = getASPManager();
      rowString = ctx.getGlobal("ROWSTRING");
      String ar[] = split(rowString,"^"); 

      if (rowString.indexOf("^",0)>0)
      {
         itemset8.first();
         for (int i=0 ; i<itemset8.countRows(); i++)
         {

            itemset8.goTo(i);

            for (int j=0 ; j<(ar.length - 1); j++)
            {

               if (i == Integer.parseInt(ar[j]))
               {

                  ASPBuffer buff = itemset8.getRow();
                  if (!mgr.isEmpty(itemset8.getValue("SIGN")))
                     buff.setFieldItem("SIGN","");
                  if (!mgr.isEmpty(itemset8.getValue("SIGN_ID")))
                     buff.setFieldItem("SIGN_ID","");
                  if (!mgr.isEmpty(itemset8.getValue("TEAM_ID")))
                     buff.setFieldItem("TEAM_ID","");

                  itemset8.setRow(buff);
               }
            }
            itemset8.next();


         }
      }
      else
      {
         itemset8.goTo(currentRow);
         if (!mgr.isEmpty(itemset8.getValue("SIGN")))
            itemset8.setValue("SIGN","");
         if (!mgr.isEmpty(itemset8.getValue("SIGN_ID")))
            itemset8.setValue("SIGN_ID","");
         if (!mgr.isEmpty(itemset8.getValue("TEAM_ID")))
            itemset8.setValue("TEAM_ID","");

      }

      mgr.submit(trans);
      headset.goTo(headRowNo);   
   }



   public void resheduleOperations1()
   {
      ASPManager mgr = getASPManager();
      //this is needed when a new WO is created.(createSearchURL is undefined at this instance)
      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      int head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);
      ctx.setGlobal("HEADGLOBAL", s_head_curr);

      if (itemlay2.isMultirowLayout())
         itemset2.goTo(itemset2.getRowSelected());
      bOpenNewWindow = true;
      urlString = "ReSheduleDlg.page?RES_TYPE=RESCHEDULEOP"+
                  "&WO_NO="+ (mgr.isEmpty(itemset2.getValue("WO_NO"))?"":itemset2.getValue("WO_NO")) +
                  "&ROW_NO="+ (mgr.isEmpty(itemset2.getValue("ROW_NO"))?"":itemset2.getValue("ROW_NO"))+
                  "&PARENT_LU=WorkOrderRole" + 
                  "&QRYSTR=" + mgr.URLEncode(qrystr) +
                  "&FRMNAME=prepareWorkOrdSM";

      newWinHandle = "resheduleOperations1"; 
      newWinHeight = 550;
      newWinWidth  = 550;

   }

   public void resheduleOperations2()
   {
      ASPManager mgr = getASPManager();
      //this is needed when a new WO is created.(createSearchURL is undefined at this instance)
      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      int head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);
      ctx.setGlobal("HEADGLOBAL", s_head_curr);

      if (itemlay8.isMultirowLayout())
         itemset8.goTo(itemset8.getRowSelected());

      bOpenNewWindow = true;
      urlString = "ReSheduleDlg.page?RES_TYPE=RESCHEDULEOP"+
                  "&WO_NO="+ (mgr.isEmpty(itemset8.getValue("WO_NO"))?"":itemset8.getValue("WO_NO")) +
                  "&ROW_NO="+ (mgr.isEmpty(itemset8.getValue("ROW_NO"))?"":itemset8.getValue("ROW_NO"))+
                  "&QRYSTR=" + mgr.URLEncode(qrystr) +
                  "&PARENT_LU=WorkOrderJob" + 
                  "&FRMNAME=prepareWorkOrdSM";

      newWinHandle = "resheduleOperations2"; 
      newWinHeight = 550;
      newWinWidth  = 550;

   }


   public void resheduleOperations3()
   {
      ASPManager mgr = getASPManager();
      //this is needed when a new WO is created.(createSearchURL is undefined at this instance)
      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      int head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);
      ctx.setGlobal("HEADGLOBAL", s_head_curr);

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      String rowNo = "";
      bOpenNewWindow = true;
      urlString = "ReSheduleDlg.page?RES_TYPE=RESCHEDULEOP"+
                  "&WO_NO="+ (mgr.isEmpty(headset.getValue("WO_NO"))?"":headset.getValue("WO_NO")) +
                  "&ROW_NO="+mgr.URLEncode(rowNo) +
                  "&QRYSTR=" + mgr.URLEncode(qrystr) +
                  "&PARENT_LU=ActiveSeparate" + 
                  "&FRMNAME=prepareWorkOrdSM";

      newWinHandle = "resheduleOperations3"; 
      newWinHeight = 550;
      newWinWidth  = 550;
   }

   public void moveOperation1()
   {
      ASPManager mgr = getASPManager();
      //this is needed when a new WO is created.(createSearchURL is undefined at this instance)
      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      int head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);
      ctx.setGlobal("HEADGLOBAL", s_head_curr);

      if (itemlay2.isMultirowLayout())
         itemset2.goTo(itemset2.getRowSelected());
      bOpenNewWindow = true;
      urlString = "MoveOperDlg.page?RES_TYPE=SHIFTOP"+
                  "&WO_NO="+ (mgr.isEmpty(itemset2.getValue("WO_NO"))?"":itemset2.getValue("WO_NO")) +
                  "&ROW_NO="+ (mgr.isEmpty(itemset2.getValue("ROW_NO"))?"":itemset2.getValue("ROW_NO"))+
                  "&QRYSTR=" + mgr.URLEncode(qrystr) +
                  "&FRMNAME=prepareWorkOrd";

      newWinHandle = "moveOperation1"; 
      newWinHeight = 300;
      newWinWidth  = 450;

   }

   public void moveOperation2()
   {
      ASPManager mgr = getASPManager();
      //this is needed when a new WO is created.(createSearchURL is undefined at this instance)
      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      int head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);
      ctx.setGlobal("HEADGLOBAL", s_head_curr);

      if (itemlay8.isMultirowLayout())
         itemset8.goTo(itemset8.getRowSelected());

      bOpenNewWindow = true;
      urlString = "MoveOperDlg.page?RES_TYPE=SHIFTOP"+
                  "&WO_NO="+ (mgr.isEmpty(itemset8.getValue("WO_NO"))?"":itemset8.getValue("WO_NO")) +
                  "&ROW_NO="+ (mgr.isEmpty(itemset8.getValue("ROW_NO"))?"":itemset8.getValue("ROW_NO"))+
                  "&QRYSTR=" + mgr.URLEncode(qrystr) +
                  "&FRMNAME=prepareWorkOrd";

      newWinHandle = "moveOperation2"; 
      newWinHeight = 300;
      newWinWidth  = 450;
   }

   public void predecessors1()
   {
      ASPManager mgr = getASPManager();
      int count;
      ASPBuffer data_buff;
      ASPBuffer rowBuff;
      //this is needed when a new STD JOB is created.(createSearchURL is undefined at this instance)
      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      ctx.setGlobal("KEY_NO1", itemset2.getValue("WO_NO"));
      ctx.setGlobal("KEY_NO2", "0");
      ctx.setGlobal("KEY_NO3", "0");
      ctx.setGlobal("STR_LU", "WorkOrderRole");
      int head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);
      ctx.setGlobal("HEADGLOBAL", s_head_curr);


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

      data_buff = mgr.newASPBuffer();

      for (int i = 0; i < count; i++)
      {
         rowBuff = mgr.newASPBuffer();

         if (i == 0)
         {
            rowBuff.addItem("ROW_NO", itemset2.getValue("ROW_NO"));
            rowBuff.addItem("ROW_NO", itemset2.getValue("ROW_NO"));
         }
         else
         {
            rowBuff.addItem(null, itemset2.getValue("ROW_NO"));
            rowBuff.addItem(null, itemset2.getValue("ROW_NO"));
         }

         data_buff.addBuffer("DATA", rowBuff);

         if (itemlay2.isMultirowLayout())
            itemset2.next();
      }

      if (itemlay2.isMultirowLayout())
         itemset2.setFilterOff();

      bOpenNewWindow = true;
      if ("TRUE".equals(mgr.readValue("REFRESHPARENT1")))
         urlString= createTransferUrl("ConnectPredecessorsDlg.page?&BLOCK=ITEM2", data_buff);
      else
         urlString= createTransferUrl("ConnectPredecessorsDlg.page?&QRYSTR=" + mgr.URLEncode(qrystr) +
                                      "&FRMNAME=prepareWorkOrdSM", data_buff);
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
      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      ctx.setGlobal("KEY_NO1", itemset8.getValue("WO_NO"));
      ctx.setGlobal("KEY_NO2", "0");
      ctx.setGlobal("KEY_NO3", "0");
      ctx.setGlobal("STR_LU", "WorkOrderRole");
      int head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);
      ctx.setGlobal("HEADGLOBAL", s_head_curr);


      if (itemlay8.isMultirowLayout())
      {
         itemset8.storeSelections();
         itemset8.setFilterOn();
         count = itemset8.countSelectedRows();
      }
      else
      {
         itemset8.unselectRows();
         count = 1;
      }

      data_buff = mgr.newASPBuffer();

      for (int i = 0; i < count; i++)
      {
         rowBuff = mgr.newASPBuffer();

         if (i == 0)
         {
            rowBuff.addItem("ROW_NO", itemset8.getValue("ROW_NO"));
            rowBuff.addItem("ROW_NO", itemset8.getValue("ROW_NO"));
         }
         else
         {
            rowBuff.addItem(null, itemset8.getValue("ROW_NO"));
            rowBuff.addItem(null, itemset8.getValue("ROW_NO"));
         }

         data_buff.addBuffer("DATA", rowBuff);

         if (itemlay8.isMultirowLayout())
            itemset8.next();
      }

      if (itemlay8.isMultirowLayout())
         itemset8.setFilterOff();

      bOpenNewWindow = true;
      if ("TRUE".equals(mgr.readValue("REFRESHPARENT2")))
         urlString= createTransferUrl("ConnectPredecessorsDlg.page?&BLOCK=ITEM8", data_buff);
      else
         urlString= createTransferUrl("ConnectPredecessorsDlg.page?&QRYSTR=" + mgr.URLEncode(qrystr) +
                                      "&FRMNAME=prepareWorkOrdSM", data_buff);
      newWinHandle = "predecessors2"; 
      newWinHeight = 550;
      newWinWidth  = 650;

   }

   public void connectExistingOperations()
   {
      ASPManager mgr = getASPManager();

      //this is needed when a new STD JOB is created.(createSearchURL is undefined at this instance)
      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      int head_current   = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);
      ctx.setGlobal("HEADGLOBAL", s_head_curr);

      if (itemlay8.isMultirowLayout())
         itemset8.goTo(itemset8.getRowSelected());

      bOpenNewWindow = true;
      urlString = "ConnectOperationsDlg.page?"+ "&WO_NO="+ (mgr.isEmpty(headset.getValue("WO_NO"))?"":headset.getValue("WO_NO")) +
                  "&JOB_ID="+ (mgr.isEmpty(itemset7.getValue("JOB_ID"))?"":itemset7.getValue("JOB_ID"))+
                  "&QRYSTR=" + mgr.URLEncode(qrystr) + "&FRMNAME=prepareWorkOrdSM";
      newWinHandle = "connectExistingOperations"; 
      newWinHeight = 550;
      newWinWidth  = 550;
   }

   public void disconnectOperations()
   {
      ASPManager mgr = getASPManager();
      int count=0;
      int head_current   = headset.getCurrentRowNo();
      if (itemlay8.isMultirowLayout())
      {
         itemset8.storeSelections();
         itemset8.setFilterOn();
         count = itemset8.countSelectedRows();
      }
      else
      {
         itemset8.unselectRows();
         count = 1;
      }
      itemset8.first();

      for (int i = 0; i < count; i++)
      {
         buff = itemset8.getRow();
         buff.setValue("JOB_ID","");
         itemset8.setRow(buff);
         itemset8.next();
      }

      trans = mgr.submit(trans);
      headset.goTo(head_current);
      okFindITEM8();
      itemset2.refreshAllRows();
   }


   public void schonobjaddr()
   {
      ASPManager mgr = getASPManager();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      int count = 0;
      ASPBuffer transferBuffer;
      ASPBuffer rowBuff;

      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

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
            rowBuff.addItem("CONTRACT", headset.getValue("MCH_CODE_CONTRACT"));
            rowBuff.addItem("MCH_CODE", headset.getValue("MCH_CODE"));
         }
         else
         {
            rowBuff.addItem(null, headset.getValue("MCH_CODE_CONTRACT"));
            rowBuff.addItem(null, headset.getValue("MCH_CODE"));
         }

         transferBuffer.addBuffer("DATA", rowBuff);

         if (headlay.isMultirowLayout())
            headset.next();
      }

      if (headlay.isMultirowLayout())
         headset.setFilterOff();

      urlString = createTransferUrl("../equipw/EquipmentObjectAddress.page", transferBuffer);
      newWinHandle = "schonobjaddr"; 
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
   }

//   Bug 83532, Start
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
      newWinHandle = "workorderaddr"; 
   }
//   Bug 83532, End
   public void coInformation()
   {
      ASPManager mgr = getASPManager();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      bOpenNewWindow = true;
      //Bug Id 70012, Start
      urlString = createTransferUrl("ActiveSeparateReportCOInfo.page?&PASS_PLN_S_DATE="+mgr.URLEncode(headset.getRow().getValue("PLAN_S_DATE"))
                                    +"&PASS_WORK_TYPE_ID="+mgr.URLEncode(headset.getRow().getValue("WORK_TYPE_ID"))
                                    +"&PASS_CON_TYPE_DB="+mgr.URLEncode(headset.getRow().getValue("CONNECTION_TYPE_DB")), headset.getSelectedRows("WO_NO"));
      //Bug Id 70012, End

      newWinHandle = "coInformation";            
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
   }

   public void copy()
   {
      // 040123  ARWILK  Begin  (Remove uneccessary global variables)
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();


      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      bOpenNewWindow = true;
      urlString = "CopyWorkOrderDlg.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) +
                  "&FAULT_REP_FLAG=" + mgr.URLEncode(headset.getRow().getValue("FAULT_REP_FLAG"));

      newWinHandle = "copy"; 
      // 040123  ARWILK  End  (Remove uneccessary global variables)
   }

   public void prepWorkOrderQuot()
   {
      ASPManager mgr = getASPManager();

      String calling_url = mgr.getURL();
      ctx.setGlobal("CALLING_URL", calling_url);

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      if (headlay.isMultirowLayout())
         headset.store();
      else
      {
         headset.unselectRows();
         headset.selectRow();
      }

      bOpenNewWindow = true;
      urlString = createTransferUrl("WorkOrderQuotation.page", headset.getSelectedRows("QUOTATION_ID"));
      newWinHandle = "prepWorkOrderQuot";
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
   }


   public void servreqvord()
   {
      ASPManager mgr = getASPManager();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      if (headlay.isMultirowLayout())
         headset.store();
      else
      {
         headset.unselectRows();
         headset.selectRow();
      }

      bOpenNewWindow = true;
      urlString = createTransferUrl("ActiveSeparate3.page", headset.getSelectedRows("WO_NO"));
      newWinHandle = "servreqvord";
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
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
      row = buffer.addBuffer("0");
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
      r = headset.getRow();
      r.setValue("CONTRACT_ID",mgr.readValue("TEMPCONTRACTID"));
      r.setValue("LINE_NO",mgr.readValue("TEMPLINENO"));
      headset.setRow(r);
      bFECust = true;
   }
   //Bug 68947, End   

   public void mroObjReceiveO()
   {
      ASPManager mgr = getASPManager();        

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      int count = 0;
      ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();

      if (checkMROObjRO())
      {
         if (headlay.isMultirowLayout())
            headset.first();

         if (headlay.isMultirowLayout())
         {
            headset.storeSelections();
            headset.setFilterOn();
            count = headset.countSelectedRows();
         }
         else
         {
            headset.unselectRows();
            headset.selectRow();
            count = 1;
         }

         trans.clear();

         for (int i = 0; i < count; i++)
         {
            cmd = trans.addCustomCommand("CREMRORO_" + i,"ACTIVE_SEPARATE_API.Create_Mro_Ro");
            cmd.addParameter("RECEIVE_ORDER_NO");
            cmd.addParameter("WO_NO", headset.getValue("WO_NO"));     

            if (headlay.isMultirowLayout())
               headset.next();
         }

         trans = mgr.perform(trans);
         if (headlay.isMultirowLayout())
            headset.first();

         trans1.clear();

         String woCreated = "";
         boolean bCreated = false;
         String woNotCreated = "";
         boolean bNotCreated = false;

         for (int i = 0; i < count; i++)
         {
            if (mgr.isEmpty(trans.getValue("CREMRORO_" + i + "/DATA/RECEIVE_ORDER_NO")))
            {
               if (!bNotCreated)
                  woNotCreated += headset.getValue("WO_NO");
               else
                  woNotCreated += ", " + headset.getValue("WO_NO");
               bNotCreated = true;
            }
            else
            {
               if (!bCreated)
                  woCreated += trans.getValue("CREMRORO_" + i + "/DATA/RECEIVE_ORDER_NO");
               else
                  woCreated += ", " + trans.getValue("CREMRORO_" + i + "/DATA/RECEIVE_ORDER_NO");
               bCreated = true;
               cmd = trans1.addCustomFunction("GETREQSDATE_" + i,"ACTIVE_SEPARATE_API.Get_Required_Start_Date","REQUIRED_START_DATE");
               cmd.addParameter("WO_NO", headset.getValue("WO_NO"));
            }               

            if (headlay.isMultirowLayout())
               headset.next();
         }

         trans1 = mgr.perform(trans1);

         String allInfo = "";
         if (bNotCreated)
            allInfo = mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTRONOTCRE: Receive Order(s) not Created for Work Order &1.",woNotCreated);
         if (bCreated)
            allInfo += "\n" + mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTROCRE: Receive Order &1 has been created.",woCreated);
         allInfo += "\n";

         if (headlay.isMultirowLayout())
            headset.first();

         for (int i = 0; i < count; i++)
         {
            if (mgr.isEmpty(trans1.getValue("GETREQSDATE_" + i + "/DATA/REQUIRED_START_DATE")))
               allInfo += mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTINVREQSD: Required Start is not specified for Work Order &1. Update corresponding Planned Receipt Date for Receive Order &2.", headset.getValue("WO_NO"), trans.getValue("CREMRORO_" + i + "/DATA/RECEIVE_ORDER_NO")) + "\n";

            if (headlay.isMultirowLayout())
               headset.next();
         }                        

         mgr.showAlert(allInfo);

         trans.clear();
         trans1.clear();

         if (headlay.isMultirowLayout())
            headset.first();

         for (int i = 0; i < count; i++)
         {
            headset.refreshRow();
            if (headlay.isMultirowLayout())
               headset.next();
         }

         if (headlay.isMultirowLayout())
            headset.setFilterOff();
      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTPERMRO: Cannot perform Create MRO Object Receive Order on this record."));
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
   }

   public void perform(String command) 
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
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

         if ((!command.equals("FINISH__")) && (!command.equals("CANCEL__")))
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
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
   }

   //---------------------------------------------------------
   // -----------Check Tools /Facility  Site ________________

   public boolean checkToolFacilitySite()
   {
      ASPManager mgr = getASPManager();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
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

      for (int i = 0;i < count;i++)
      {
         cmd = trans.addCustomCommand("WOTOOLFACILITY"+i,"Work_Order_Tool_Facility_API.Check_Valid_Sites");
         cmd.addParameter("HEAD_ISVALID");
         cmd.addParameter("HEAD_FACSEQUENCE");
         cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

         if (headlay.isMultirowLayout())
            headset.next();
      }

      trans = mgr.perform(trans);

      if (headlay.isMultirowLayout())
         headset.setFilterOff();

      for (int i = 0;i < count;i++)
      {
         if ("FALSE".equals(trans.getValue("WOTOOLFACILITY" + i + "/DATA/HEAD_ISVALID")))
            return false;
      }

      return true;
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
   }

   public void reinit()
   {
      ASPManager mgr = getASPManager();
      perform("RE_INIT__");
   }

   public void observed()
   {
      ASPManager mgr = getASPManager();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      perform("CONFIRM__");
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
   }

   public void underPrep()
   {
      ASPManager mgr = getASPManager();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      perform("TO_PREPARE__");
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
   }

   public void prepared()
   {
      ASPManager mgr = getASPManager();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      if (checkToolFacilitySite())
         perform("PREPARE__") ;
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2NOTVALID: Please enter Tools and Facility Details."));
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
   }

   public void released()
   {
      ASPManager mgr = getASPManager();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      int count = 0;
      String eventVal = "";

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
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
   }

   public void started()
   {
      ASPManager mgr = getASPManager();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      int count = 0;
      String eventVal = "";

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
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
   }

   public void workDone()
   {
      ASPManager mgr = getASPManager();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      if (checkToolFacilitySite())
         perform("WORK__");
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2NOTVALID: Please enter Tools and Facility Details."));
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
   }

   public void reported()
   {
      ASPManager mgr = getASPManager();
      unissue = "FALSE";
      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
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
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2NOTVALID: Please enter Tools and Facility Details."));
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
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
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTNOTVALID: Please enter Tools and Facility Details."));

   }

   public void finished()
   {
      ASPManager mgr = getASPManager();

      if (checkToolFacilitySite())
      {
         if ((!CheckObjInRepairShop()) && (!CheckAllMaterialIssued()))
         {
            int rowno = headset.getCurrentRowNo();
            //Bug 70891, start
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
                  okFindITEM2();
                  okFindITEM3();
                  okFindITEM4();
                  okFindITEM5(); 
               }
            }
            else
               mgr.redirectTo("ActiveSeparate2ServiceManagement.page");
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

         int rowno = headset.getCurrentRowNo();
         //Bug 70891, start
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
               okFindITEM2();
               okFindITEM3();
               okFindITEM4();
               okFindITEM5(); 
            }
         }
         else
            mgr.redirectTo("ActiveSeparate2ServiceManagement.page");
         //Bug 70891, end

      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTNOTVALID: Please enter Tools and Facility Details."));

   }

   public void cancelled()
   {
      ASPManager mgr = getASPManager();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      if (mgr.isPresentationObjectInstalled("VIMW/CancelCause.page"))
      {
         trans.clear();
         cmd = trans.addCustomFunction("WOVIM","Work_Order_From_Vim_API.Is_Vim_Work_Order","ISVIM"); 
         cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

         trans = mgr.perform(trans);

         String wovim = trans.getValue("WOVIM/DATA/ISVIM");

         if ("TRUE".equals(wovim))
         {
            if (headlay.isMultirowLayout())
               headset.goTo(headset.getRowSelected());
            else
               headset.selectRow();

            String calling_url_wo = mgr.getURL();
            ctx.setGlobal("CALLING_URL_WO",calling_url_wo);

            mgr.redirectTo("EnterCancelCauseDlg.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO"))); 

         }
         else
         {
            int rowno = headset.getCurrentRowNo();
            trans.clear();
            perform("CANCEL__");

            headset.goTo(rowno);
            headset.clearRow();

            if (headlay.isSingleLayout() && (headset.countRows()>0))
            {
               okFindITEM2();
               okFindITEM3();
               okFindITEM4();
               okFindITEM5(); 
            }
         }
      }
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
   }

   public void doCancel()
   {
      ASPManager mgr = getASPManager();

      if (!"FALSE".equals(mgr.getQueryStringValue("DOCANCEL")))
      {
         trans.clear();
         perform("CANCEL__");
         mgr.redirectTo("ActiveSeparate2.page");
      }
   }

   public void turnToRepOrd()
   {
      ASPManager mgr = getASPManager();

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      trans.clear();
      q = trans.addQuery("SEROBJ", "select count(*) from EQUIPMENT_SERIAL where MCH_CODE = ?");
      q.addParameter("MCH_CODE",headset.getValue("MCH_CODE"));

      trans = mgr.perform(trans);

      buff = trans.getBuffer("SEROBJ/DATA");
      String noRowsStr = buff.getValueAt(0);
      int noRows = Integer.parseInt(noRowsStr);

      if (noRows > 0)
      {
         // 040123  ARWILK  Begin  (Remove uneccessary global variables)
         bOpenNewWindow = true;

         urlString = "MoveToRepairWorkshopDlg.page?MCH_CODE=" + mgr.URLEncode(headset.getRow().getValue("MCH_CODE")) +
                     "&CONTRACT=" + mgr.URLEncode(headset.getRow().getValue("CONTRACT")) +
                     "&QRYSTR=" + mgr.URLEncode(qrystr) +
                     "&WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) +
                     "&FRMNAME=prepareWorkOrdSM";

         newWinHandle = "turnToRepOrd"; 
         // 040123  ARWILK  End  (Remove uneccessary global variables)
      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCANNOTPER: Cannot perform Turn into Repair Order on this record."));
   }

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

   public void pickListForWork()
   {
      ASPManager mgr = getASPManager();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
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
         int nWoNo = new Double(headset.getValue("WO_NO")).intValue();
         
         attr1 = "REPORT_ID" + (char)31 + "WO_ORDER_PRINT_REP" + (char)30;        
         attr2 = "WO_NO_LIST" + (char)31 + nWoNo + (char)30;        
         attr3 =  "";
         attr4 =  "";

         cmd = trans.addCustomCommand("PRINTPIC" + i,"Archive_API.New_Client_Report");
         cmd.addParameter("ATTR0");                       
         cmd.addParameter("ATTR1", attr1);       
         cmd.addParameter("ATTR2", attr2);              
         cmd.addParameter("ATTR3", attr3);      
         cmd.addParameter("ATTR4", attr4);  

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
         printBuff.addItem("RESULT_KEY", trans.getValue("PRINTPIC" + i + "/DATA/ATTR0"));
      }

      // 040123  ARWILK  End  (Enable Multirow RMB actions)
      callPrintDlg(print,true);
   }

//-----------------------------------------------------------------------------
//------------------------  ITEMBAR3 CUSTOM FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

   public void performItem( String command) 
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      int headcurrow = headset.getCurrentRowNo();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      if (itemlay3.isMultirowLayout())
      {
         itemset3.storeSelections();
         itemset3.markSelectedRows(command);
         mgr.submit(trans);

         itemset3.refreshAllRows();
      }
      else
      {
          itemset3.unselectRows();
          itemset3.markRow(command);
          int currrow = itemset3.getCurrentRowNo();
          mgr.submit(trans);
          itemset3.goTo(currrow);
          itemset3.refreshRow();
      }   
      // 040123  ARWILK  End  (Enable Multirow RMB actions) 

      headset.goTo(headcurrow);
   } 

   public boolean noReserv()
   {
      int n = itemset.countRows();
      itemset.first();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)

      if (n > 0)
      {
         for (int i = 0; i < n; ++i)
         {
            if (toDouble(itemset.getRow().getValue("QTY_ASSIGNED")) > 0)
               return false;

            itemset.next();
         }

         itemset.first(); 
      }

      return true;
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
   }

   public boolean noIssue()
   {
      int n = itemset.countRows();
      itemset.first();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)

      if (n > 0)
      {
         for (int i = 0; i < n; ++i)
         {
            if (toDouble(itemset.getRow().getValue("QTY")) > 0)
               return false;

            itemset.next();
         }
         itemset.first(); 
      }

      return true;
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
   }

   public void plan()
   {
      ASPManager mgr = getASPManager();

      int head_current = headset.getCurrentRowNo(); 

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      if (noReserv() && noIssue())
         performItem("PLAN__");
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCANNOT: Cannot perform on selected line"));    

      // 040123  ARWILK  End  (Enable Multirow RMB actions)

      headset.goTo(head_current);
   }

   public void release()
   {
      ASPManager mgr = getASPManager();

      int head_current = headset.getCurrentRowNo(); 

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      performItem("RELEASE__");
      // 040123  ARWILK  End  (Enable Multirow RMB actions)

      headset.goTo(head_current);
   }


   public void close()
   {
      ASPManager mgr = getASPManager();

      int head_current = headset.getCurrentRowNo(); 

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      if (noReserv())
         performItem("CLOSE__");
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCANNOT: Cannot perform on selected line"));    
      // 040123  ARWILK  End  (Enable Multirow RMB actions)

      headset.goTo(head_current);
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

            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTNONEW: No new Assigned stock for this Material Order."));
      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCANNOT: Cannot perform on selected line"));

   }

   // 031204  ARWILK  Begin  (Replace links with RMB's)
   public void sparePartObject()
   {
      ASPManager mgr = getASPManager();

      int head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);

      int currrow = itemset3.getCurrentRowNo();

      String s_currrow = String.valueOf(currrow);
      ctx.setGlobal("CURRROWGLOBAL",s_currrow);
      ctx.setGlobal("WONOGLOBAL",s_head_curr);

      bOpenNewWindow = true;

      urlString = "MaintenanceObject2.page?MCH_CODE=" + mgr.URLEncode(headset.getValue("MCH_CODE")) +
                  "&CONTRACT=" + mgr.URLEncode(headset.getValue("MCH_CODE_CONTRACT")) +
                  "&WO_NO=" + mgr.URLEncode(headset.getValue("WO_NO")) +
                  "&ORDER_NO=" + mgr.URLEncode(itemset3.getValue("MAINT_MATERIAL_ORDER_NO")) +
                  "&QRYSTR=" + mgr.URLEncode(qrystr) +
                  "&FRMNAME=prepareWorkOrdSM";

      newWinHandle = "sparePartSM"; 
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

      urlString = "UpDateSparePartsObject.page?MCH_CODE=" + mgr.URLEncode(headset.getValue("MCH_CODE")) +
                  "&MCH_CODE_CONTRACT=" + mgr.URLEncode(headset.getValue("MCH_CODE_CONTRACT")) +
                  "&PART_NO=" + mgr.URLEncode(itemset.getValue("PART_NO")) +
                  "&PART_DESC=" + mgr.URLEncode(itemset.getValue("SPAREDESCRIPTION")) +
                  "&SPARE_CONTRACT=" + mgr.URLEncode(itemset.getValue("SPARE_CONTRACT")) +
                  "&FRMNAME=" + mgr.URLEncode("updateSpareparts");

      newWinWidth  = 600;
      newWinHeight = 300;
      newWinHandle = "updateSpareparts"; 

      itemset.refreshAllRows();

   }


   public void objStructure()
   {
      ASPManager mgr = getASPManager();

      int head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);

      int currrow = itemset3.getCurrentRowNo();

      String s_currrow = String.valueOf(currrow);
      ctx.setGlobal("CURRROWGLOBAL",s_currrow);
      ctx.setGlobal("WONOGLOBAL",s_head_curr);

      bOpenNewWindow = true;

      urlString = "MaintenaceObject3.page?MCH_CODE=" + mgr.URLEncode(headset.getValue("MCH_CODE")) +
                  "&CONTRACT=" + mgr.URLEncode(headset.getValue("MCH_CODE_CONTRACT")) +
                  "&WO_NO=" + mgr.URLEncode(headset.getValue("WO_NO")) +
                  "&ORDER_NO=" + mgr.URLEncode(itemset3.getValue("MAINT_MATERIAL_ORDER_NO")) +
                  "&QRYSTR=" + mgr.URLEncode(qrystr) +
                  "&FRMNAME=prepareWorkOrdSM";

      newWinHandle = "objStructSM";
   } 
   // 031204  ARWILK  End  (Replace links with RMB's)

   public void detchedPartList()
   {
       ASPManager mgr = getASPManager();

       String sPartNo = "";
       int head_current = headset.getCurrentRowNo();   
       String s_head_curr = String.valueOf(head_current);

       int currrow = itemset3.getCurrentRowNo();

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
               mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCANNOT: Cannot perform on selected line"));
           }
           else{

               bOpenNewWindow = true;
               row.addItem("PART_NO",sPartNo);
               row.addItem("WO_NO",itemset3.getRow().getValue("WO_NO"));    
               row.addItem("FRAME","prepareWorkOrdSM");
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
           row.addItem("FRAME","prepareWorkOrdSM");
           row.addItem("QRYST",qrystr);
           row.addItem("ORDER_NO",itemset3.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));

           urlString = createTransferUrl("../equipw/EquipmentSpareStructure3.page", buffer);

           newWinHandle = "detachedPart"; 
       }
       //Bug 82543, end
   }
   
   public void availDetail()
   {
      ASPManager mgr = getASPManager();
      String sProjectId;

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
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
      newWinHandle = "availDetail"; 
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
   }

   public void supPerPart()
   {
      ASPManager mgr = getASPManager();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      if (itemlay.isMultirowLayout())
         itemset.store();
      else
      {
         itemset.unselectRows();
         itemset.selectRow();
      }

      bOpenNewWindow = true;
      urlString = createTransferUrl("../purchw/PurchasePartSupplier.page", itemset.getSelectedRows("PART_NO"));
      newWinHandle = "supPerPart"; 
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
   }

   public void issue()
   {
      ASPManager mgr = getASPManager();
      boolean secOk2 =false;
      boolean secOk3 =false;
      double qtyOnHand = 0.0;
      double nQtyAvblToIssue = 0.0;
      double nTotQtyRes = 0.0;
      double nTotQtyPlanable = 0.0;
      double nCount = 0.0;
      int count = 0;
      int successCount = 0;
      ASPTransactionBuffer transForIssue;
      boolean canPerform = true;

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
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCANNOTSTATWO5WO: Work order status not valid for material issue."));
         return ;
      }

      if (!(sStatusCodeReleased.equals(dfStatus)))
      {
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCANNOTSTAT1: Maint Material Requisition status not valid for material issue."));
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
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCANNOTMAT1: No material requirements for selected item."));
            return;
         }
         else
         {
            trans.clear();

            if (plan_qty1 > nAvailToIss)
                mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTAVAIL: Available quantity for part ") +itemset.getRow().getValue("PART_NO") + mgr.translate("PCMWACTIVESEPARATE2AVAIL1:  was ")  +sAvailToIss+ mgr.translate("PCMWACTIVESEPARATE2AVAI2: .")); 
            
            if ( checksec("Inventory_Part_In_Stock_API.Get_Sales_Plannable_Qty_Res",2) )
            {
               cmd = transForIssue.addCustomFunction("INVONHANDRES"+successCount,"Inventory_Part_In_Stock_API.Get_Sales_Plannable_Qty_Res","QTYRES");
               cmd.addParameter("CONTRACT",itemset.getValue("SPARE_CONTRACT"));
               cmd.addParameter("PART_NO",itemset.getValue("PART_NO"));
               cmd.addParameter("CONFIGURATION","");
               secOk2 = true;
            }
            if ( checksec("Inventory_Part_In_Stock_API.Get_Sales_Plannable_Qty_Onhand",3) )
            {
               cmd = transForIssue.addCustomFunction("INVONHANDPLAN"+successCount,"Inventory_Part_In_Stock_API.Get_Sales_Plannable_Qty_Onhand","QTYPLANNABLE");
               cmd.addParameter("CONTRACT",itemset.getValue("SPARE_CONTRACT"));
               cmd.addParameter("PART_NO",itemset.getValue("PART_NO"));
               cmd.addParameter("CONFIGURATION","");
               secOk3 = true;
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
            cmd.addParameter("ORDER_NO",itemset.getRow().getFieldValue("ITEM0_MAINT_MATERIAL_ORDER_NO"));
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
  		    mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SSMCANTISSUE: All material could not be issued for part &1. Remaining quantity to be issued: &2", itemset.getValue("PART_NO"), new Double(nQtyShort).toString()));
  		 //Bug 76767, End

         String isAutoRepairable = trans.getValue("AUTOREP"+i+"/DATA/AUTO_REPAIRABLE");
         String isRepairable = trans.getValue("REPAIR"+i+"/DATA/REPAIRABLE");
         if ( secOk2 )
            nTotQtyRes = trans.getNumberValue("INVONHANDRES"+i+"/DATA/QTYRES");
         else
            nTotQtyRes = 0;

         if ( isNaN(nTotQtyRes) )
            nTotQtyRes = 0;
         if ( secOk3 )
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
      okFindITEM6();

   }


   public void manIssue()
   {
      ASPManager mgr = getASPManager();
      headMchCode = headset.getRow().getValue("MCH_CODE");
      headMchDesc = headset.getRow().getValue("MCH_CODE_DESCRIPTION");

      int head_current = 0;
      double nQtyLeft = 0;

      head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);

      int currrow = itemset3.getCurrentRowNo();

      if (itemlay.isMultirowLayout())
         itemset.goTo(itemset.getRowSelected());
      else
         itemset.selectRow();

      int currRowItem = itemset.getCurrentRowNo(); 

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
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCANNOTSTATWO5WO: Work order status not valid for material issue."));
         return ;
      }

      if (!(sStatusCodeReleased.equals(dfStatus)))
      {
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCANNOTSTAT1: Maint Material Requisition status not valid for material issue."));
         return ;
      }

      double plan_qty = itemset.getRow().getNumberValue("PLAN_QTY");
      if (isNaN(plan_qty))
         plan_qty = 0;

      double qty = itemset.getRow().getNumberValue("QTY");
      if (isNaN(qty))
         qty = 0;

      double qty_return = itemset.getRow().getNumberValue("QTY_RETURNED");
      if ( isNaN(qty_return) )
         qty_return = 0;

      double qty_outstanding = plan_qty - qty; //(qty + qty_return); * ASSALK  Material Issue & Reserve modification.

      if ( qty_outstanding > 0 )
      {
         double nQtyLeftNum = plan_qty - qty;
         nQtyLeft = nQtyLeftNum;

         if (nQtyLeftNum < 0)
            nQtyLeft = 0;

         String currrowStr = String.valueOf(currrow);
         if ( ( "TRUE".equals(isAutoRepairable) ) &&  ( "TRUE".equals(isRepairable) ) &&  ( "NOT SERIAL TRACKING".equals(hasSerialNum) ) )
            creRepWO = "TRUE";
         ctx.setGlobal("CURRROWGLOBAL",currrowStr);
         ctx.setGlobal("WONOGLOBAL",s_head_curr);

         // 040123  ARWILK  Begin  (Remove uneccessary global variables)
         bOpenNewWindow = true;
         urlString = "InventoryPartLocationDlg.page?PART_NO=" + mgr.URLEncode(itemset.getRow().getValue("PART_NO")) +
                     "&CONTRACT=" + mgr.URLEncode(itemset.getRow().getValue("SPARE_CONTRACT")) +
                     "&FRMNAME=" + mgr.URLEncode("prepareWorkOrdSM") +
                     "&QRYSTR=" + mgr.URLEncode(qrystr) +
                     "&WO_NO=" + mgr.URLEncode(itemset.getRow().getFieldValue("ITEM_WO_NO")) +
                     "&LINE_ITEM_NO=" + mgr.URLEncode(itemset.getRow().getValue("LINE_ITEM_NO")) +
                     "&DESCRIPTION=" + mgr.URLEncode(itemset.getRow().getValue("SPAREDESCRIPTION")) +
                     "&HEAD_CONDITION_CODE=" + mgr.URLEncode(itemset.getRow().getValue("CONDITION_CODE")) +
                     "&HEAD_CONDITION_CODE_DESC=" + mgr.URLEncode(itemset.getRow().getValue("CONDDESC")) +
                     "&QTYLEFT=" + mgr.URLEncode(new Double(nQtyLeft).toString()) +
                     "&MAINTMATORDNO=" + mgr.URLEncode(itemset.getRow().getFieldValue("ITEM0_MAINT_MATERIAL_ORDER_NO"))+
                     "&CREREPWO="+creRepWO+
                     "&MCH_CODE="+headMchCode+
                     "&MCH_DESCRIPTION="+headMchDesc+
                     "&OWNERSHIP=" + mgr.URLEncode(itemset.getRow().getValue("PART_OWNERSHIP")) +
                     "&OWNER=" + mgr.URLEncode(itemset.getRow().getValue("OWNER")) +
                     "&OWNERNAME=" + mgr.URLEncode(itemset.getRow().getValue("OWNER_NAME"));

         newWinHandle = "manIssue"; 
         // 040123  ARWILK  End  (Remove uneccessary global variables)
      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCANNOTMAT: No material requirements for selected item."));

      itemset3.goTo(currrow); 

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
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCANNOTSTAT2: Maint Material Requisition status not valid for material reserve."));
         return ;
      }

      if (!("TRUE".equals(trans.getValue("RESALLOW/DATA/RES_ALLO"))))
      {
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCANNOTSTATWO2: Work order status not valid for material reserve."));
         return;
      }

      transForReserve = mgr.newASPTransactionBuffer();

      for (int i = 0; i < count; i++)
      {

         if (itemset.getNumberValue("PLAN_QTY") <= (itemset.getNumberValue("QTY") + itemset.getNumberValue("QTY_ASSIGNED"))) // + itemset.getNumberValue("QTY_RETURNED"))) * ASSALK  Material Issue & Reserve modification.
         {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCANNOTMAT3: No material requirements for selected item."));
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
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTAVAIL: Available quantity for part ") +itemset.getRow().getValue("PART_NO") + mgr.translate("PCMWACTIVESEPARATE2AVAIL1:  was ")  +sAvalToRes+ mgr.translate("PCMWACTIVESEPARATE2AVAI2: .")); 
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
	     mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCOULDNOTALL: All material could not be allocated for part &1. Remaining quantity to be reserved: &2", itemset.getValue("PART_NO"), new Double(nQtyShort).toString()));
	 //Bug 76767, End  

         if (itemlay.isMultirowLayout())
            itemset.next();
      }

      if (itemlay.isMultirowLayout())
         itemset.setFilterOff();

      itemset3.goTo(currrow);
      okFindITEM6();
   }


   public void manReserve()
   {
      ASPManager mgr = getASPManager();

      int head_current = 0;
      double nQtyLeft = 0;

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);

      int currrow = itemset3.getCurrentRowNo();

      if (itemlay.isMultirowLayout())
         itemset.goTo(itemset.getRowSelected());
      else
         itemset.selectRow();

      int currRowItem = itemset.getCurrentRowNo();

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
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCANNOTSTATWO5: Work order status not valid for material reserve."));
         return ;
      }

      if (!(sStatusCodeReleased.equals(dfStatus)))
      {
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCANNOTSTAT5: Maint Material Requisition status not valid for material reserve."));
         return ;
      }

      double plan_qty = itemset.getRow().getNumberValue("PLAN_QTY");
      if (isNaN(plan_qty))
         plan_qty = 0;

      double qty = itemset.getRow().getNumberValue("QTY");
      if (isNaN(qty))
         qty = 0;

      double qty_assign = itemset.getRow().getNumberValue("QTY_ASSIGNED");
      if (isNaN(qty_assign))
         qty_assign = 0;

      double qty_Ret = itemset.getRow().getNumberValue("QTY_RETURNED");
      if (isNaN(qty_Ret))
         qty_Ret = 0;

      if (plan_qty > ( qty + qty_assign)) // + qty_Ret)) * ASSALK  Material Issue & Reserve modification.
      {
         double nQtyLeftNum = plan_qty - qty - qty_assign;
         nQtyLeft = nQtyLeftNum;

         String currrowStr = String.valueOf(currrow);
         ctx.setGlobal("CURRROWGLOBAL",currrowStr);
         ctx.setGlobal("WONOGLOBAL",s_head_curr);

         // 040123  ARWILK  Begin  (Remove uneccessary global variables)
         bOpenNewWindow = true;
         urlString = "MaterialRequisReservatDlg.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) +
                     "&LINE_ITEM_NO=" + mgr.URLEncode(itemset.getRow().getValue("LINE_ITEM_NO")) +
                     "&FRMNAME=" + mgr.URLEncode("prepareWorkOrdSM") +
                     "&QRYSTR=" + mgr.URLEncode(qrystr) +
                     "&PART_NO=" + mgr.URLEncode(itemset.getRow().getValue("PART_NO")) +
                     "&CONTRACT=" + mgr.URLEncode(itemset.getRow().getValue("SPARE_CONTRACT")) +
                     "&DESCRIPTION=" + mgr.URLEncode(itemset.getRow().getValue("SPAREDESCRIPTION")) +
                     "&CONDITION_CODE=" + mgr.URLEncode(itemset.getRow().getValue("CONDITION_CODE")) +
                     "&CONDITION_CODE_DESC=" + mgr.URLEncode(itemset.getRow().getValue("CONDDESC")) +
                     "&QTYLEFT=" + mgr.URLEncode(new Double(nQtyLeft).toString()) +
                     "&MAINTMATORDNO=" + mgr.URLEncode(itemset.getRow().getFieldValue("ITEM0_MAINT_MATERIAL_ORDER_NO"))+
                     "&OWNERSHIP=" + mgr.URLEncode(itemset.getRow().getValue("PART_OWNERSHIP")) +
                     "&OWNER=" + mgr.URLEncode(itemset.getRow().getValue("OWNER")) +
                     "&OWNERNAME=" + mgr.URLEncode(itemset.getRow().getValue("OWNER_NAME")) ;

         newWinHandle = "manReserve"; 
         // 040123  ARWILK  End  (Remove uneccessary global variables)
      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCANNOTMAT: No material requirements for selected item."));

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

      int head_current = 0;
      double nQtyLeft = 0;

      if (headlay.isMultirowLayout())
         headset.goTo(headset.getRowSelected());
      else
         headset.selectRow();

      head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);

      int currrow = itemset3.getCurrentRowNo();

      if (itemlay.isMultirowLayout())
         itemset.goTo(itemset.getRowSelected());
      else
         itemset.selectRow();

      int currRowItem = itemset.getCurrentRowNo();

      trans.clear();

      cmd = trans.addCustomFunction("FINSTATEDEC","MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__","DB_STATE"); 
      cmd.addParameter("DB_STATE","Released"); 

      trans = mgr.perform(trans);

      String sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DB_STATE");
      String dfStatus = itemset3.getRow().getFieldValue("ITEM3_STATE");

      if (!(sStatusCodeReleased.equals(dfStatus)))
      {
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCANNOTSTAT6: Maint Material Requisition status not valid for material unreserve."));
         return ;
      }

      double qty_assign = itemset.getRow().getNumberValue("QTY_ASSIGNED");
      if (isNaN(qty_assign))
         qty_assign = 0;

      if (qty_assign > 0)
      {
         nQtyLeft = qty_assign;

         String currrowStr = String.valueOf(currrow);
         ctx.setGlobal("CURRROWGLOBAL",currrowStr);
         ctx.setGlobal("WONOGLOBAL",s_head_curr);

         // 040123  ARWILK  Begin  (Remove uneccessary global variables)
         bOpenNewWindow = true;
         urlString = "MaterialRequisReservatDlg2.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) +
                     "&LINE_ITEM_NO=" + mgr.URLEncode(itemset.getRow().getValue("LINE_ITEM_NO")) +
                     "&FRMNAME=" + mgr.URLEncode("prepareWorkOrdSM") +
                     "&QRYSTR=" + mgr.URLEncode(qrystr) +
                     "&PART_NO=" + mgr.URLEncode(itemset.getRow().getValue("PART_NO")) +
                     "&CONTRACT=" + mgr.URLEncode(itemset.getRow().getValue("SPARE_CONTRACT")) +
                     "&DESCRIPTION=" + mgr.URLEncode(itemset.getRow().getValue("SPAREDESCRIPTION")) +
                     "&QTYLEFT=" + mgr.URLEncode(new Double(nQtyLeft).toString()) +
                     "&MAINTMATORDNO=" + mgr.URLEncode(itemset.getRow().getFieldValue("ITEM0_MAINT_MATERIAL_ORDER_NO"));

         newWinHandle = "unreserve"; 
         // 040123  ARWILK  End  (Remove uneccessary global variables)
      }
      else
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCANNOTMAT: No material requirements for selected item."));
   }


   public void matReqUnissue()
   {
      ASPManager mgr = getASPManager();

      int head_current = headset.getCurrentRowNo();   
      String s_head_curr = String.valueOf(head_current);

      int currrow = itemset3.getCurrentRowNo();

      if (itemlay.isMultirowLayout())
         itemset.goTo(itemset.getRowSelected());
      else
         itemset.selectRow();

      trans.clear();

      cmd = trans.addCustomFunction("FINSTATEDEC","MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__","DB_STATE"); 
      cmd.addParameter("DB_STATE","Released"); 

      cmd = trans.addCustomFunction("OSTATE","Active_Separate_API.Get_Obj_State","OBSTATE");  
      cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO")); 

      trans = mgr.perform(trans);

      String objState  =trans.getBuffer("OSTATE/DATA").getFieldValue("OBSTATE");

      String sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DB_STATE");
      String dfStatus = itemset3.getRow().getFieldValue("ITEM3_STATE");

      double qty = itemset.getRow().getNumberValue("QTY");

      if (isNaN(qty))
         qty = 0;

      if (qty > 0)
      {

         if (!(( "WORKDONE".equals(objState) )|| ( "REPORTED".equals(objState) )|| ( "STARTED".equals(objState) )|| ( "RELEASED".equals(objState) )))
         {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCANNOTUNISSUESTAT1: Maint Material Requisition status not valid for material Unissue."));
            return ;
         }
         else
         {
            String s_currrow = String.valueOf(currrow);
            ctx.setGlobal("CURRROWGLOBAL",s_currrow);
            ctx.setGlobal("WONOGLOBAL",s_head_curr);

            // 040123  ARWILK  Begin  (Remove uneccessary global variables)
            bOpenNewWindow = true;
            urlString = "ActiveWorkOrder.page?WO_NO=" + mgr.URLEncode(itemset.getRow().getFieldValue("ITEM_WO_NO")) +
                        "&MAINTMATORDNO=" + mgr.URLEncode(itemset.getRow().getFieldValue("ITEM0_MAINT_MATERIAL_ORDER_NO")) +
                        "&LINE_ITEM_NO="+ mgr.URLEncode(itemset.getRow().getValue("LINE_ITEM_NO"))+
                        "&FRMNAME=" + mgr.URLEncode("prepareWorkOrdSM") +
                        "&QRYSTR=" + mgr.URLEncode(qrystr);


            newWinHandle = "matReqUnissue"; 
            // 040123  ARWILK  End  (Remove uneccessary global variables)
         } 
      }
      else
      {
         mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTNOISSUEMATERIAL: Cannot perform Material Requisition Unissue on this record."));
      }

      itemset3.goTo(currrow); 
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
      boolean secOk4 =false;

      trans.clear();
      cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
      cmd.addParameter("PART_OWNERSHIP",itemset.getRow().getValue("PART_OWNERSHIP"));
      trans = mgr.validate(trans);
      String sClientOwnershipDefault = trans.getValue("VALIDOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");
      trans.clear();

      if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",1))
      {
         secOk4 =true;
      }
      if ( secOk4 )
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

      if ( secOk4 )
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


//-----------------------------------------------------------------------------
//-------------------------  CUSTOM FUNCTIONS  --------------------------------
//-----------------------------------------------------------------------------
//------------Sets the check boxes in general tab------------------------------

   public void setCheckBoxValues()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      tempblk = getASPBlock("TEMP");

      cmd = trans.addCustomFunction("CBWARRNT","OBJECT_SUPPLIER_WARRANTY_API.Has_Warranty","CBWARRNOBJECT");
      cmd.addParameter("CONTRACT",headset.getRow().getValue("MCH_CODE_CONTRACT"));
      cmd.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));
      cmd.addParameter("REG_DATE",headset.getRow().getFieldValue("REG_DATE"));

      trans = mgr.perform(trans);

      String cbwarranty = trans.getValue("CBWARRNT/DATA/CBWARRNOBJECT");
      String cbwarranty_var = "";
      String cbmobile_var = "";

      if ("TRUE".equals(cbwarranty))
         cbwarranty_var = "TRUE";

      row = headset.getRow();
      row.setValue("CBWARRNOBJECT",cbwarranty_var);

      headset.setRow(row);
   }

   //Bug 69914, start
   public void setStringLables()
   {
      ASPManager mgr = getASPManager();

      if (headset.countRows()>0)
      {
         trans.clear();

         cmd = trans.addCustomFunction("DEFCONTRACT", "Site_API.Get_Company", "COMPANY" );
         cmd.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT"));    

         cmd = trans.addCustomFunction("STRB", "Accounting_Code_Parts_API.Get_Name", "COST_CENTER" );
         cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
         cmd.addParameter("STRING","B");

         cmd = trans.addCustomFunction("STRC", "Accounting_Code_Parts_API.Get_Name", "PROJECT_NO" );
         cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
         cmd.addParameter("STRING","F");

         cmd = trans.addCustomFunction("STRE", "Accounting_Code_Parts_API.Get_Name", "OBJECT_NO" );
         cmd.addReference("COMPANY", "DEFCONTRACT/DATA");
         cmd.addParameter("STRING","E");

         trans = mgr.perform(trans);

         strLableB = trans.getValue("STRB/DATA/COST_CENTER");
         strLableF = trans.getValue("STRC/DATA/PROJECT_NO");
         strLableE = trans.getValue("STRE/DATA/OBJECT_NO");
         trans.clear();

         mgr.getASPField("COST_CENTER").setLabel(strLableB);
         mgr.getASPField("OBJECT_NO").setLabel(strLableE);
         mgr.getASPField("PROJECT_NO").setLabel(strLableF);
      }
   }
   //Bug 69914, end

   public void setWorkCenterValues()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer();
      secBuff.addSecurityQuery("Work_Center_Int_API");

      secBuff = mgr.perform(secBuff);

      if (secBuff.getSecurityInfo().itemExists("Work_Center_Int_API"))
      {
         cmd = trans.addCustomFunction("WORKCENTER","Work_Center_Int_API.Get_Mch_Work_Center","MCHWORKCENTER");
         cmd.addParameter("CONTRACT",headset.getRow().getValue("MCH_CODE_CONTRACT"));
         cmd.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));

         cmd = trans.addCustomFunction("RESOURCENO","Work_Center_Int_API.Get_Mch_Resource_No","MCHRESOURCENO");
         cmd.addParameter("CONTRACT",headset.getRow().getValue("MCH_CODE_CONTRACT"));
         cmd.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));

         cmd = trans.addCustomFunction("RESOUCELOADHOURS","Work_Center_Int_API.Get_Mch_Load_Hours","MCHRESOUCELOADHOURS");
         cmd.addParameter("CONTRACT",headset.getRow().getValue("MCH_CODE_CONTRACT"));
         cmd.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));
         cmd.addParameter("PLAN_S_DATE",headset.getRow().getFieldValue("PLAN_S_DATE"));
         cmd.addParameter("PLAN_F_DATE",headset.getRow().getFieldValue("PLAN_F_DATE"));
         trans = mgr.perform(trans);

         String workcent = trans.getValue("WORKCENTER/DATA/MCHWORKCENTER");
         String resource = trans.getValue("RESOURCENO/DATA/MCHRESOURCENO");
         String resourcehours = trans.getValue("RESOUCELOADHOURS/DATA/MCHRESOUCELOADHOURS");

         row = headset.getRow();
         row.setValue("MCHWORKCENTER",workcent);
         row.setValue("MCHRESOURCENO",resource);
         row.setValue("MCHRESOUCELOADHOURS",resourcehours);
         headset.setRow(row);
      }
   }

   public void preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
      setHidden();

      headblk.addField("OBJVERSION").
      setHidden();

      headblk.addField("OBJSTATE").
      setHidden();

      headblk.addField("WO_NO","Number", "#").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTWONO: WO No").
      setReadOnly().
      setHilite().
      setMaxLength(8);

      headblk.addField("CONTRACT").
      setSize(5).
      setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTWOCONTRACT: WO Site").
      setCustomValidation("CONTRACT","COMPANY").
      setUpperCase().
      setHilite().
      setInsertable().
      setReadOnly().
      setMaxLength(5);

      headblk.addField("CONNECTION_TYPE").
      setSize(20).
      setSelectBox().
      setReadOnly().
      setMandatory(      ).
      setInsertable().
      setCustomValidation("CONNECTION_TYPE","CONNECTION_TYPE_DB").
      setHilite().    
      enumerateValues("MAINT_CONNECTION_TYPE_API").
      setLabel("PCMWACTIVESEPARATE2SERVICEMGTCONNECTIONTYPE: Connection Type").
      setHilite().    
      enumerateValues("MAINT_CONNECTION_TYPE_API").
      setLabel("PCMWACTIVESEPARATE2SERVICEMGTCONTYPE: Connection Type").
      setUpperCase();

      //Bug 87935, Start, Modified the code to get correct pres objects
      f = headblk.addField("MCH_CODE");
      f.setSize(12);
      f.setLOV("MaintenanceObjectLov1.page","MCH_CODE_CONTRACT,CONNECTION_TYPE",600,450);
      f.setLOVProperty("WHERE","(OBJ_LEVEL IS NULL OR (OBJ_LEVEL IS NOT NULL AND quipment_Object_Level_API.Get_Create_Wo(OBJ_LEVEL) = 'TRUE'))");
      f.setCustomValidation("MCH_CODE_CONTRACT,MCH_CODE","MCH_CODE_DESCRIPTION,CRITICALITY,MCH_CODE_CONTRACT,MCH_CODE");
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTMCHCODE: Object ID");
      f.setUpperCase();
      f.setHilite();
      f.setMaxLength(100);
      //Bug 87935, End

      headblk.addField("MCH_CODE_DESCRIPTION").
      setSize(20).
      setHilite().   
      setDefaultNotVisible().
      setLabel("PCMWACTIVESEPARATE2SERVICEMGTMCHCODEDESC: Description").
      setReadOnly();

      headblk.addField("MCH_CODE_CONTRACT").
      setSize(5).
      setHilite().    
      setLabel("PCMWACTIVESEPARATE2SERVICEMGTMCHCODECONTRACT: Site").
      setUpperCase().
      setDynamicLOV("USER_ALLOWED_SITE_LOV");

      headblk.addField("LATESTTRANSAC").
      setSize(50).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTLATESTTRANSACTION: Latest Transaction").
      setFunction("PART_SERIAL_CATALOG_API.Get_Alt_Latest_Transaction(:MCH_CODE_CONTRACT,:MCH_CODE)").
      setReadOnly().
      setMaxLength(2000);

      headblk.addField("GROUPID").
      setSize(12).
      setDynamicLOV("EQUIPMENT_OBJ_GROUP",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTGROUPID: Group ID").
      setDefaultNotVisible().
      setFunction("EQUIPMENT_OBJECT_API.Get_Group_Id(:MCH_CODE_CONTRACT,:MCH_CODE)").
      setReadOnly();

      headblk.addField("CRITICALITY").
      setSize(12).
      setDynamicLOV("EQUIPMENT_CRITICALITY",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCRITICALITY: Criticality").
      setFunction("EQUIPMENT_FUNCTIONAL_API.Get_Criticality(:MCH_CODE_CONTRACT,:MCH_CODE)").
      setReadOnly().
      setDefaultNotVisible().
      setMaxLength(2000);

      headblk.addField("ORG_CODE").
      setSize(12).
      setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,450).
      setMandatory().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTORGCODE: Maintenance Organization").
      setUpperCase().
      setCustomValidation("CONTRACT,ORG_CODE","ORGCODEDESCRIPTION").
      setMaxLength(8);

      headblk.addField("ORGCODEDESCRIPTION").
      setSize(28).
      setFunction("Organization_API.Get_Description(:CONTRACT,:ORG_CODE)").
      setReadOnly().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTORGDESC: Organization Description").
      setMaxLength(2000).
      setDefaultNotVisible();

      headblk.addField("ERR_DESCR").
      setSize(50).
      setMandatory().
      setDefaultNotVisible().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTERRDESCR: Directive").
      setHilite().
      setMaxLength(60);

      headblk.addField("STATE").
      setSize(19).
      setLOV("ActiveSeparateLov1.page",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSTATE: Status").
      setReadOnly().
      setHilite().
      setMaxLength(30);

      headblk.addField("PLAN_S_DATE","Datetime").
      setSize(30).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPLANSDATE: Start").
      setCustomValidation("WO_NO,PLAN_S_DATE","PLAN_F_DATE");

      headblk.addField("PLAN_F_DATE","Datetime").
      setSize(30).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPLANFDATE: Completion");

      headblk.addField("PLAN_HRS","Number").
      setDefaultNotVisible().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPLANHRS: Execution Time").
      setCustomValidation("PLAN_HRS,OP_EXIST,WO_NO,PLAN_S_DATE,PLAN_F_DATE,REQUIRED_START_DATE,REQUIRED_END_DATE,CUSTOMER_NO,ORG_CODE,CONTRACT,CONTRACT_ID,LINE_NO","MAN_HOURS,ALLOC_HOURS,PLAN_F_DATE,REQUIRED_END_DATE"); //Bug Id 70921

      headblk.addField("REMAIN_HRS","Number").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTREMAINHRS: Remaining Hrs Until Latest Completion").
      setFunction("Active_Separate_Api.Get_Remain_Hrs_To_Complete(:REQUIRED_END_DATE, :CONTRACT_ID, :LINE_NO)").
      setReadOnly();

      headblk.addField("PLANNED_MAN_HRS","Number").
      setDefaultNotVisible().
      setHidden().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPLANMENHRS: Project Budget Hours");

      headblk.addField("MAN_HOURS","Number").
      setDefaultNotVisible().
      setReadOnly().
      setFunction("WORK_ORDER_ROLE_API.Get_Total_Planned_Hours(:WO_NO)").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPLANMANHOURS: Total Man Hours");

      headblk.addField("ALLOC_HOURS","Number").
      setDefaultNotVisible().
      setReadOnly().
      setFunction("WORK_ORDER_ROLE_API.Get_Total_Allocated_Hours(:WO_NO)").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTALLOCATEDHRS: Allocated Hours");

      headblk.addField("REMAIN_HOURS","Number").
      setDefaultNotVisible().
      setFunction("WORK_ORDER_ROLE_API.Get_Total_Planned_Hours(:WO_NO) - WORK_ORDER_ROLE_API.Get_Total_Allocated_Hours(:WO_NO)").
      setReadOnly().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTREMAININGHOURS: Remaining Hours");


      headblk.addField("CALL_CODE").
      setSize(12).
      setDefaultNotVisible().
      setDynamicLOV("MAINTENANCE_EVENT",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCALLCODE: Event").
      setUpperCase().
      setMaxLength(10);

      headblk.addField("OP_STATUS_ID").
      setSize(12).
      setDefaultNotVisible().
      setDynamicLOV("OPERATIONAL_STATUS",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTOPSTATUS: Op Status").
      setUpperCase().
      setMaxLength(3);

      headblk.addField("PRIORITY_ID").
      setSize(8).
      setDynamicLOV("MAINTENANCE_PRIORITY",600,450).
      setCustomValidation("PRIORITY_ID,PLAN_S_DATE,REQUIRED_START_DATE,CONTRACT_ID,LINE_NO","PLAN_HRS,PLAN_F_DATE,REQUIRED_END_DATE,PRIORITYDESCRIPTION").  //Bug Id 70921
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPRIORITYID: Priority").
      setUpperCase().
      setDefaultNotVisible().
      setMaxLength(1);

      headblk.addField("WORK_TYPE_ID").
      setSize(12).
      setDefaultNotVisible().
      setDynamicLOV("WORK_TYPE",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTWORKTYPEID: Work Type").
      setUpperCase().
      setMaxLength(20);

      headblk.addField("WORK_DESCR_LO").
      setSize(50).
      setDefaultNotVisible().
      setHeight(4).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTWORKDESCRLO: Work Description").
      setMaxLength(2000);

      headblk.addField("VENDOR_NO").
      setSize(12).
      setDefaultNotVisible().
      setDynamicLOV("SUPPLIER_INFO",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTVENDORNO: Contractor").
      setUpperCase().
      setMaxLength(20);

      headblk.addField("VENDORNAME").
      setSize(29).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTVENDORNAME: Contractor Name").
      setFunction("Supplier_Info_API.Get_Name(:VENDOR_NO)").
      setReadOnly().
      setDefaultNotVisible().
      setMaxLength(2000);
      mgr.getASPField("VENDOR_NO").setValidation("VENDORNAME");

      headblk.addField("REPORTED_BY").
      setSize(12).
      setDynamicLOV("EMPLOYEE_LOV",600,450).
      setMandatory().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTREPORTEDBY: Reported by").
      setUpperCase().
      setCustomValidation("COMPANY,REPORTED_BY","REPORTED_BY_ID,NAME").
      setReadOnly().
      setInsertable().
      setHilite().
      setMaxLength(20);

      headblk.addField("REPAIR_FLAG").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTREPAIRFLAG: Repair Work Order").
      setDefaultNotVisible().
      setCheckBox("FALSE,TRUE").
      setMaxLength(5);

      headblk.addField("PREPARED_BY").
      setSize(12).
      setDynamicLOV("EMPLOYEE_LOV",600,450).
      setCustomValidation("PREPARED_BY,COMPANY","PREPARED_BY_ID,PREPAREDBYNAME").
      setDefaultNotVisible().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPREPAREDBY: Prepared by").
      setUpperCase().
      setMaxLength(20);

      headblk.addField("PREPAREDBYNAME").
      setSize(29).
      setDefaultNotVisible().
      setFunction("PERSON_INFO_API.Get_Name(:PREPARED_BY)").
      setReadOnly().
      setMaxLength(2000);  

      headblk.addField("PREPARED_BY_ID").
      setHidden().
      setDefaultNotVisible().
      setUpperCase().
      setMaxLength(11);

      headblk.addField("WORK_MASTER_SIGN").
      setSize(12).
      setDynamicLOV("MAINT_EMPLOYEE",600,450).
      setCustomValidation("WORK_MASTER_SIGN,COMPANY","WORK_MASTER_SIGN_ID,WORKMASTERBYNAME").
      setDefaultNotVisible().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTWORKMASTERSIGN: Executed By").
      setUpperCase().
      setMaxLength(20);

      headblk.addField("WORKMASTERBYNAME").
      setSize(29).
      setFunction("PERSON_INFO_API.Get_Name(:WORK_MASTER_SIGN)").
      setReadOnly().
      setDefaultNotVisible().
      setMaxLength(2000);  

      headblk.addField("WORK_MASTER_SIGN_ID").
      setHidden().
      setUpperCase().
      setDefaultNotVisible().
      setMaxLength(11);

      headblk.addField("WORK_LEADER_SIGN").
      setSize(12).
      setDynamicLOV("EMPLOYEE_LOV",600,450).
      setCustomValidation("WORK_LEADER_SIGN,COMPANY","WORK_LEADER_SIGN_ID,WORKLEADERNAME").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTWORKLEADSIGN: Work Leader").
      setUpperCase().
      setDefaultNotVisible().
      setMaxLength(20);

      headblk.addField("WORKLEADERNAME").
      setSize(29).
      setFunction("PERSON_INFO_API.Get_Name(:WORK_LEADER_SIGN)").
      setReadOnly().
      setDefaultNotVisible().
      setMaxLength(2000);  

      headblk.addField("WORK_LEADER_SIGN_ID").
      setSize(6).
      setHidden().
      setUpperCase();

      headblk.addField("AUTHORIZE_CODE").
      setSize(12).
      setDynamicLOV("ORDER_COORDINATOR_LOV",600,450).
      setCustomValidation("AUTHORIZE_CODE","AUTHORIZECODENAME").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTAUTHORIZECODE: Coordinator").
      setUpperCase().
      setDefaultNotVisible().
      setMaxLength(20);

      headblk.addField("AUTHORIZECODENAME").
      setSize(29).
      setFunction("PERSON_INFO_API.Get_Name(:AUTHORIZE_CODE)").
      setReadOnly().
      setDefaultNotVisible().
      setMaxLength(2000);

      headblk.addField("COST_CENTER").
      setSize(17).
      setDefaultNotVisible().
      setFunction("Pre_Accounting_Api.Get_Cost_Center(:PRE_ACCOUNTING_ID)").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCOSTCENTER: Cost Center").
      setMaxLength(10);

      headblk.addField("PROJECT_NO").
      setSize(10).
      setDefaultNotVisible().
      setFunction("Pre_Accounting_Api.Get_Project_No(:PRE_ACCOUNTING_ID)").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPROJECTNO: Project No").
      setMaxLength(10);

      headblk.addField("OBJECT_NO").
      setSize(8).
      setDefaultNotVisible().
      setFunction("Pre_Accounting_Api.Get_Object_No(:PRE_ACCOUNTING_ID)").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTOBJECTNO: Object no").
      setMaxLength(10);

      headblk.addField("REG_DATE","Datetime").
      setSize(22).
      setMandatory().
      setDefaultNotVisible().
      setCustomValidation("REG_DATE","REG_DATE").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTREGDATE: Reg. Date").
      setReadOnly().
      setInsertable();

      headblk.addField("TEST_POINT_ID").
      setSize(12).
      setDynamicLOV("EQUIPMENT_OBJECT_TEST_PNT",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTTESTPOINTID: Test point").
      setUpperCase().
      setDefaultNotVisible().
      setMaxLength(6);

      headblk.addField("PM_NO","Number", "#").
      setDefaultNotVisible().
      setLOV("PmActionLov1.page",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPMNO: PM No").
      setReadOnly();

      headblk.addField("PM_REVISION").
      setSize(8).
      setDefaultNotVisible().
      setDynamicLOV("PM_ACTION","PM_NO",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPMREV: PM Revision").
      setReadOnly().
      setMaxLength(6);

      headblk.addField("QUOTATION_ID","Number").
      setDefaultNotVisible().
      setDynamicLOV("WORK_ORDER_QUOTATION_LOV",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTQUOTATIONID: Quotation ID").
      setReadOnly(); 

      headblk.addField("SALESMANCODE").
      setSize(18).
      setDynamicLOV("PERSON_INFO",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSALESMANCODE: Salesman Code").
      setDefaultNotVisible().
      setFunction("WORK_ORDER_QUOTATION_API.Get_Salesman_Code(:QUOTATION_ID)").
      setReadOnly();

      headblk.addField("CUSTOMER_NO").
      setSize(12).
      setDynamicLOV("CUSTOMER_INFO",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCUSTOMERNO: Customer No").
      //setFunction("Sc_Service_Contract_API.Get_Customer_Id(:CONTRACT_ID)").
      setUpperCase().
      setHilite().
      setCustomValidation("CUSTOMER_NO,COMPANY,AGREEMENT_ID,WO_NO","CREDITSTOP,CUSTOMERDESCRIPTION,UPDATE_PRICE").
      setMaxLength(20);

      headblk.addField("CUSTOMERDESCRIPTION").
      setSize(28).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCUSTOMERDESC: Customer Name").
      //setFunction("CUSTOMER_INFO_API.Get_Name(Sc_Service_Contract_API.Get_Customer_Id(:CONTRACT_ID))").
      setFunction("CUSTOMER_INFO_API.Get_Name(:CUSTOMER_NO)").
      setReadOnly().
      setDefaultNotVisible().
      setHilite().
      setMaxLength(2000);

      headblk.addField("CREDITSTOP").
      setFunction("''").
      setHidden();    

      headblk.addField("STATEVAL").
      setFunction("''").
      setHidden();

      headblk.addField("CONTACT").
      setSize(12).
      setHidden().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCONTACT: Contact").
      setMaxLength(30).
      setReadOnly();

      headblk.addField("REFERENCE_NO").
      setSize(12).
      setDefaultNotVisible().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTREFERENCENO: Reference No").
      setMaxLength(25); 

      headblk.addField("PHONE_NO").
      setSize(12).
      setHidden().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPHONENO: Phone No").
      setMaxLength(20);

      headblk.addField("PM_TYPE").
      setHidden();

      headblk.addField("ADDRESS1").
      setSize(18).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTADDRESS1: Address1").
      setFunction("WORK_ORDER_ADDRESS_API.Get_Address1(:WO_NO)").
      setMaxLength(100).
      setDefaultNotVisible().
      setReadOnly().
      setInsertable();

      headblk.addField("ADDRESS2").
      setSize(16).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTADDRESS2: Address2").
      setFunction("WORK_ORDER_ADDRESS_API.Get_Address2(:WO_NO)").
      setMaxLength(35).   
      setDefaultNotVisible().
      setReadOnly().  
      setInsertable();

      headblk.addField("ADDRESS3").
      setSize(16).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTADDRESS3: Address3").
      setFunction("WORK_ORDER_ADDRESS_API.Get_Address3(:WO_NO)").
      setMaxLength(35).   
      setDefaultNotVisible().
      setReadOnly().   
      setInsertable();

      headblk.addField("ADDRESS4").
      setSize(16).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTADRESS4: Address4").
      setFunction("WORK_ORDER_ADDRESS_API.Get_Address4(:WO_NO)").
      setMaxLength(35).
      setDefaultNotVisible().
      setReadOnly();   

      headblk.addField("ADDRESS5").
      setSize(16).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTADDRESS5: Address5").
      setFunction("WORK_ORDER_ADDRESS_API.Get_Address5(:WO_NO)").
      setMaxLength(35).
      setDefaultNotVisible().
      setReadOnly();   

      headblk.addField("ADDRESS6").
      setSize(16).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTADDRESS6: Address6").
      setFunction("WORK_ORDER_ADDRESS_API.Get_Address6(:WO_NO)").
      setMaxLength(35).
      setDefaultNotVisible().
      setReadOnly();

      headblk.addField("LU_NAME").
      setHidden().
      setFunction("'ActiveSeparate'");

      headblk.addField("KEY_REF").
      setHidden().
      setFunction("CONCAT('WO_NO=',CONCAT(:WO_NO,'^'))");

      f = headblk.addField("DOCUMENT");
      if (mgr.isModuleInstalled("DOCMAN"))
         f.setFunction("substr(Doc_Reference_Object_API.Exist_Obj_Reference('ActiveSeparate',CONCAT('WO_NO=',CONCAT(:WO_NO,'^'))),1, 5)");
      else
         f.setFunction("''");
      f.setUpperCase();
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTDOCUMENT: Has Documents");
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setCheckBox("FALSE,TRUE");
      f.setSize(18);

      headblk.addField("NAME").
      setSize(28).
      setFunction("PERSON_INFO_API.Get_Name(:REPORTED_BY)").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTREPORTBYDESC: Name").
      setReadOnly().
      setDefaultNotVisible().
      setHilite().
      setMaxLength(2000);

      headblk.addField("REPORTED_BY_ID").
      setSize(6).
      setHidden().
      setUpperCase();

      headblk.addField("WORKTYPEDESCRIPTION").
      setSize(29).
      setFunction("WORK_TYPE_API.Get_Description(:WORK_TYPE_ID)").
      setReadOnly().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTWORKDESC: Work Type Description").
      setMaxLength(2000).
      setDefaultNotVisible();
      mgr.getASPField("WORK_TYPE_ID").setValidation("WORKTYPEDESCRIPTION");

      headblk.addField("PRIORITYDESCRIPTION").
      setSize(33).
      setFunction("Maintenance_Priority_API.Get_Description(:PRIORITY_ID)").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPRORITYDESC: Priority Description").
      setReadOnly().
      setMaxLength(2000).
      setDefaultNotVisible();
      mgr.getASPField("PRIORITY_ID").setValidation("PRIORITYDESCRIPTION");

      headblk.addField("CRITICALITYDESCRIPTION").
      setSize(28).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCRICALTYDESC: Criticality Description").
      setFunction("EQUIPMENT_FUNCTIONAL_API.Get_Criticality_Description(:MCH_CODE_CONTRACT,:MCH_CODE)").
      setReadOnly().
      setDefaultNotVisible().
      setMaxLength(2000);

      headblk.addField("HEAD_TEMP").
      setHidden().
      setFunction("''");

      headblk.addField("ERR_DISCOVER_CODE").
      setSize(12).
      setDynamicLOV("WORK_ORDER_DISC_CODE",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTERRDISCODE: Discovery").
      setUpperCase().
      setDefaultNotVisible().
      setMaxLength(3);

      headblk.addField("DISCOVERDESCRIPTION").
      setSize(28).
      setFunction("WORK_ORDER_DISC_CODE_API.Get_Description(:ERR_DISCOVER_CODE)").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTERRDISCODESC: Discovery Description").
      setReadOnly().
      setMaxLength(2000).
      setDefaultNotVisible();
      mgr.getASPField("ERR_DISCOVER_CODE").setValidation("DISCOVERDESCRIPTION");

      headblk.addField("ERR_SYMPTOM").
      setSize(12).
      setDynamicLOV("WORK_ORDER_SYMPT_CODE",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTERRSYMPTM: Symptom").
      setUpperCase().
      setDefaultNotVisible().
      setMaxLength(3);

      headblk.addField("SYMPTOMDESCRIPTION").
      setSize(28).
      setFunction("WORK_ORDER_SYMPT_CODE_API.Get_Description(:ERR_SYMPTOM)").
      setReadOnly().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSYMTDISCODESC: Symptom Description").
      setMaxLength(2000).
      setDefaultNotVisible();
      mgr.getASPField("ERR_SYMPTOM").setValidation("SYMPTOMDESCRIPTION");

      headblk.addField("ERR_DESCR_LO").
      setSize(49).
      setHeight(4).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTERRDESCRLO: Description").
      setDefaultNotVisible().
      setMaxLength(2000);

      headblk.addField("TESTPOINTIDDES").                                                                
      setSize(28).                                                                                     
      setDefaultNotVisible().                                                                          
      setReadOnly().                                                                                   
      setFunction("EQUIPMENT_OBJECT_TEST_PNT_api.get_description(:MCH_CODE_CONTRACT,:MCH_CODE,:TEST_POINT_ID)");

      headblk.addField("CBHASDOCUMENTS").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCBHASDOCUMENTS: Has Documents").
      setFunction("''").
      setHidden();

      f = headblk.addField("PRE_ACCOUNTING_ID","Number");
      f.setHidden();

      headblk.addField("COMPANY").
      setSize(6).
      setHidden().
      setUpperCase().
      setMaxLength(20);

      headblk.addField("OPSTATUSIDDESCR").
      setSize(29).
      setFunction("OPERATIONAL_STATUS_API.Get_Description(:OP_STATUS_ID)").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTOPSTATUSDESC: Operational Status Description").
      setReadOnly().
      setMaxLength(2000).
      setDefaultNotVisible();
      mgr.getASPField("OP_STATUS_ID").setValidation("OPSTATUSIDDESCR");

      headblk.addField("CALLCODEDESCRIPTION").
      setSize(29).
      setFunction("MAINTENANCE_EVENT_API.Get_Description(:CALL_CODE)").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCALLCODEDESC: Event Description").
      setReadOnly().
      setMaxLength(2000).
      setDefaultNotVisible();
      mgr.getASPField("CALL_CODE").setValidation("CALLCODEDESCRIPTION");

      headblk.addField("ACTION_CODE_ID").
      setSize(19).
      setDynamicLOV("MAINTENANCE_ACTION",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTACTIONCODEID: Action").
      setUpperCase().
      setReadOnly().
      setDefaultNotVisible().
      setMaxLength(10);

      headblk.addField("PM_DESCR").
      setSize(36).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPMDESCR: Generation info").
      setReadOnly().
      setDefaultNotVisible().
      setMaxLength(2000);

      headblk.addField("MCHWORKCENTER").
      setSize(11).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTMCHWORKCENTER: Work Center").
      setFunction("''").
      setReadOnly().
      setDefaultNotVisible().
      setMaxLength(2000);

      headblk.addField("MCHRESOURCENO").
      setSize(29).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTMCHRESOURCENO: Resource").
      setFunction("''").
      setReadOnly().
      setDefaultNotVisible().
      setMaxLength(2000);

      headblk.addField("MCHRESOUCELOADHOURS","Number").
      setDefaultNotVisible().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTMCHRESOULOADHRS: Load").
      setFunction("''").
      setReadOnly();

//  Bug 68947, Start
      headblk.addField("CONTRACT_ID").         
      setLOV("PscContrProductLov.page","CONTRACT,CUSTOMER_NO,MCH_CODE").
      setUpperCase().
      setMaxLength(15).
      //Bug Id 70921, added dates for validation
      setCustomValidation("CONTRACT_ID,LINE_NO,WO_NO,PLAN_S_DATE,PLAN_F_DATE,REQUIRED_START_DATE,REQUIRED_END_DATE,PLAN_HRS,PRIORITY_ID,REG_DATE","CONTRACT_ID,LINE_NO,CONTRACT_NAME,LINE_DESC,CUSTOMER_NO,CUSTOMERDESCRIPTION,AUTHORIZE_CODE,AGREEMENT_ID,DESCRIPTION,WORK_TYPE_ID,WORKTYPEDESCRIPTION,ORG_CODE,ORGCODEDESCRIPTION,PLAN_S_DATE,REQUIRED_START_DATE,PLAN_F_DATE,REQUIRED_END_DATE,PLAN_HRS,DUMMY_CONTRACT_ID,DUMMY_REQ_START_DATE,DUMMY_REQ_END_DATE").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCONTRACTID: Contract ID").
      setSize(15);

      //Bug 84436, Start 
      f = headblk.addField("CONTRACT_NAME");
      f.setDefaultNotVisible();      
      if (mgr.isModuleInstalled("SRVCON"))
         f.setFunction("SC_SERVICE_CONTRACT_API.Get_Contract_Name(:CONTRACT_ID)");
      else
         f.setFunction("''");      
      f.setReadOnly();      
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCONTRACTNAME: Contract Name");     
      f.setSize(15);
      //Bug 84436, End
      
      headblk.addField("LINE_NO").
      setLOV("PscContrProductLov.page","CONTRACT,CUSTOMER_NO,MCH_CODE,CONTRACT_ID").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTLINENO: Line No").
      //Bug Id 70921, added dates for validation
      setCustomValidation("CONTRACT_ID,LINE_NO,WO_NO,PLAN_S_DATE,PLAN_F_DATE,REQUIRED_START_DATE,REQUIRED_END_DATE,PLAN_HRS,PRIORITY_ID,REG_DATE","CONTRACT_ID,LINE_NO,CONTRACT_NAME,LINE_DESC,AGREEMENT_ID,DESCRIPTION,WORK_TYPE_ID,WORKTYPEDESCRIPTION,ORG_CODE,ORGCODEDESCRIPTION,PLAN_S_DATE,REQUIRED_START_DATE,PLAN_F_DATE,REQUIRED_END_DATE,PLAN_HRS,DUMMY_CONTRACT_ID,DUMMY_REQ_START_DATE,DUMMY_REQ_END_DATE").
      setSize(10);                      

      //Bug 84436, Start 
      f= headblk.addField("LINE_DESC");
      f.setDefaultNotVisible();
      if (mgr.isModuleInstalled("PCMSCI"))
         f.setFunction("PSC_CONTR_PRODUCT_API.Get_Description(:CONTRACT_ID,:LINE_NO)");
      else
            f.setFunction("''");
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTLINEDESC: Description");
      f.setSize(15);
      //Bug 84436, End 
//  Bug 68947, End

      headblk.addField("AGREEMENT_ID").
      setSize(25).
      setReadOnly().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTAGREEMENTID: Agreement ID").
      setUpperCase().
      setHilite().
      setDefaultNotVisible();

      headblk.addField("FAULT_REP_FLAG","Number").
      setHidden();

      headblk.addField("REQUIRED_START_DATE","Datetime").
      setCustomValidation("REQUIRED_START_DATE","REQUIRED_START_DATE").    
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTREQUIREDSTARTDATE: Required Start").
      setDefaultNotVisible().
      setSize(30);

      headblk.addField("REQUIRED_END_DATE","Datetime").
      setCustomValidation("REQUIRED_END_DATE","REQUIRED_END_DATE").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTREQUIREDENDDATE: Latest Completion").
      setDefaultNotVisible().
      setSize(30);    

      headblk.addField("WO_KEY_VALUE").
      setUpperCase().
      setHidden().
      setFunction("WO_KEY_VALUE");

      headblk.addField("NORGANIZATIONORG_COST","Number").
      setLabel("PCMWACTIVESEPARATE2SMORGCOST: Organization Cost").
      setFunction("ORGANIZATION_API.Get_Org_Cost(:CONTRACT,:ORG_CODE)").
      setHidden();

      headblk.addField("NOTE").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTNOTE: Inspection Note").
      setDefaultNotVisible().
      setReadOnly().
      setSize(36);

      headblk.addField("CODE_C").
      setUpperCase().
      setHidden();

      headblk.addField("CODE_D").
      setUpperCase().
      setHidden();

      headblk.addField("CODE_E").
      setFunction("''").    
      setHidden();

      headblk.addField("CODE_F").
      setFunction("''").    
      setHidden();

      headblk.addField("CODE_G").
      setUpperCase().
      setHidden();

      headblk.addField("CODE_H").
      setUpperCase().
      setHidden();

      headblk.addField("CODE_I").
      setUpperCase().
      setHidden();

      headblk.addField("CODE_J").
      setUpperCase().
      setHidden();

      headblk.addField("STD_TEXT").
      setFunction("''").    
      setHidden();

      headblk.addField("MODULE").
      setFunction("''").
      setHidden();

      headblk.addField("SITE_DATE","Datetime").
      setFunction("''").
      setHidden();

      headblk.addField("SET_TIME","Number").
      setFunction("''").
      setHidden();

      headblk.addField("EXEC_TIME","Number").
      setFunction("''").
      setHidden();

      headblk.addField("HAS_MATERIAL").
      setFunction("''").
      setHidden();

      headblk.addField("HAS_ROLE").
      setFunction("''").
      setHidden();

      headblk.addField("CBWARRNOBJECT").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCBWARRNOBJECT: Supplier Warranty").
      setFunction("''").
      setReadOnly().
      setDefaultNotVisible().
      setCheckBox("FALSE,TRUE"); 

      headblk.addField("FIXED_PRICE_DB_VAL").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCBFIXEDPRICE: Fixed Price").
      setReadOnly().
      setDefaultNotVisible().
      setDbName("FIXED_PRICE_DB").
      setCheckBox("1,2");   

      headblk.addField("CBHASDOCS").
      setFunction("''").
      setHidden();

      //Bug 87766, Start, Modified label and function
      headblk.addField("CBISCONN").
      setFunction("substr(WORK_ORDER_CONNECTION_API.Has_Connection_Up(:WO_NO),1,5)").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTISCONN: In a Structure").
      setReadOnly().
      setDefaultNotVisible().
      setCheckBox("FALSE,TRUE");

      headblk.addField("CBHASCONN").
      setFunction("substr(WORK_ORDER_CONNECTION_API.Has_Connection_Down(:WO_NO),1,5)").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTHASCONN: Has Structure").
      setReadOnly().
      setDefaultNotVisible().
      setCheckBox("FALSE,TRUE");
      //Bug 87766, End

      headblk.addField("CBWARRANTY").
      setFunction("''").
      setDefaultNotVisible().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTWARRANTYONOBJ: Warranty on Object").
      setCheckBox("FALSE,TRUE");

      headblk.addField("REPAIRFLAG").
      setFunction("''").
      setHidden().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTREPAIRFLAG: Repair Work Order").
      setCheckBox("FALSE,TRUE").
      setDefaultNotVisible();

      f = headblk.addField("CBHASDOCUMNTS");
      if (mgr.isModuleInstalled("DOCMAN"))
         f.setFunction("substr(Doc_Reference_Object_API.Exist_Obj_Reference('ActiveSeparate',CONCAT('WO_NO=',CONCAT(:WO_NO,'^'))),1, 5)");
      else
         f.setFunction("''");
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTHASDOC: Documents");
      f.setCheckBox("FALSE,TRUE");
      f.setDefaultNotVisible();
      f.setReadOnly();

      headblk.addField("OBJEVENTS").
      setHidden(); 

      headblk.addField("CONTRACT1").
      setHidden().
      setFunction("''");

      headblk.addField("COMPANY1").
      setHidden().
      setFunction("''");  

      headblk.addField("USERID").
      setHidden().
      setFunction("''");  

      headblk.addField("FNDUSER").
      setHidden().
      setFunction("''");  

      headblk.addField("INFO").
      setHidden().
      setFunction("''");  

      headblk.addField("ACTION").
      setHidden().
      setFunction("''");  

      headblk.addField("ATTR").
      setHidden().
      setFunction("''");  

      headblk.addField("TRANSFERRED").
      setFunction("''").
      setHidden();

      headblk.addField("OBSTATE").
      setFunction("''").
      setHidden();  

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

      //-----------------------------------------------------------------------
      //-----------------------------------------------------------------------

      headblk.addField("OBJ_SUP_WARRANTY","Number").
      setCustomValidation("OBJ_SUP_WARRANTY,MCH_CODE,MCH_CODE_CONTRACT","SUP_WARRANTY").
      setHidden();   

      headblk.addField("SUP_WARR_DESC").
      setSize(50).
      setDefaultNotVisible().
      setReadOnly().
      setFunction("Sup_Warranty_Type_api.Get_Warranty_Description(:SUP_WARRANTY,:SUP_WARR_TYPE)").
      setMaxLength(20);     

      headblk.addField("SUPPLIER").
      setSize(20).
      setFunction("OBJECT_SUPPLIER_WARRANTY_api.Get_Vendor_No(:MCH_CODE_CONTRACT,:MCH_CODE,:OBJ_SUP_WARRANTY)").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSUPWARR: Supplier").
      setDefaultNotVisible().
      setReadOnly().
      setUpperCase();    

      headblk.addField("SUP_WARRANTY").
      setHidden();

      headblk.addField("SUP_WARR_TYPE").
      setSize(20).
      setDefaultNotVisible().
      setReadOnly().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTOBJSUPWARR: Warranty Type").
      setMaxLength(20);    

      headblk.addField("SUPP_DESCR").
      setDefaultNotVisible().
      setSize(50).
      setFunction("Maintenance_Supplier_API.Get_Description(OBJECT_SUPPLIER_WARRANTY_api.Get_Vendor_No(:MCH_CODE_CONTRACT,:MCH_CODE,:OBJ_SUP_WARRANTY))").
      setReadOnly().  
      setMaxLength(20); 

      headblk.addField("DIFF","Number").
      setHidden().
      setFunction("''");

      headblk.addField("HEAD_CURRENCEY_CODE").
      setHidden().
      setFunction("ACTIVE_SEPARATE_API.Get_Currency_Code(:WO_NO)");

      headblk.addField("UPDATE_PRICE").
      setFunction("''").
      setHidden();

      headblk.addField("RECEIVE_ORDER_NO").
      setHidden();

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      headblk.addField("IDENTITY_TYPE").
      setFunction("IDENTITY_INVOICE_INFO_API.Get_Identity_Type(Site_API.Get_Company(:CONTRACT), :CUSTOMER_NO, PARTY_TYPE_API.Get_Client_Value(1))").    
      setHidden();        
      // 040123  ARWILK  End  (Enable Multirow RMB actions)

      headblk.addField("PARTY_TYPE").
      setFunction("''").    
      setHidden();        

      headblk.addField("CONNECTION_TYPE_DB").
      setHidden();

      headblk.addField("HEAD_PART_NO").
      setFunction("''").    
      setHidden();        

      headblk.addField("HEAD_SERIAL_NO").
      setFunction("''").    
      setHidden();        

      f = headblk.addField("CBPROJCONNECTED");
      f.setFunction("Active_Separate_API.Is_Project_Connected(:WO_NO)");
      f.setLabel("PCMWACTIVESEPARATESMPROJCONN: Project Connected");
      f.setCheckBox("FALSE,TRUE");
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setSize(5);

      headblk.addField("CBHASOBSOLETEJOBS").
      setLabel("PCMWACTIVESEPARATE2CBHASOBSOLETEJOBS: Has Obsolete Jobs").
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
      f.setLabel("PCMWACTIVESEPARATE2CBHTRANSTOMOB: Transferred To Mobile");
      f.setCheckBox("FALSE,TRUE");
      f.setReadOnly();
      f.setDefaultNotVisible();
      //Bug 77304, end

      f = headblk.addField("ACTIVITY_SEQ","Number");
      f.setLabel("ACTSEP2SMHEADACTSEQ: Activity Seq");
      f.setHidden();

      f = headblk.addField("ACTIVITY_SEQ1","Number","#");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("PROJECT_ID");
      f.setHidden();
      //attached the function call only if the PROJ is installed.
      if (mgr.isModuleInstalled("PROJ"))
         f.setFunction("Activity_API.Get_Project_Id(:ACTIVITY_SEQ)");
      else
         f.setFunction("''");

      f = headblk.addField("INCLUDE_STANDARD");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("INCLUDE_PROJECT");
      f.setFunction("''");
      f.setHidden();

      f = headblk.addField("EXCEPTION_EXISTS");
      f.setHidden();
      //attached the function call only if the PROJ is installed.
      if (mgr.isModuleInstalled("PROJ"))
         f.setFunction("Activity_API.Object_Exceptions_Exist('ASWO',:WO_NO,NULL,NULL)");
      else
         f.setFunction("''");

      f = headblk.addField("PP_EXISTS"); 
      f.setHidden();  
      f.setFunction("''");

      f = headblk.addField("STRING");
      f.setHidden();
      f.setFunction("''");

      //Bug 72644, Start
      f = headblk.addField("JOB_EXIST"); 
      f.setHidden();  
      f.setFunction("Active_Work_Order_API.Has_Job(:WO_NO)");

      f = headblk.addField("TOOL_EXIST"); 
      f.setHidden();  
      f.setFunction("Active_Work_Order_API.Has_Facility(:WO_NO)");
      //Bug 72644, End

      headblk.setView("ACTIVE_SEPARATE");
      headblk.defineCommand("ACTIVE_SEPARATE_API","New__,Modify__,Remove__,RE_INIT__,CONFIRM__,TO_PREPARE__,RELEASE__,REPLAN__,RESTART__,START_ORDER__,PREPARE__,WORK__,REPORT__,FINISH__,CANCEL__");
      headblk.disableDocMan();

      f = headblk.addField("OP_EXIST"); 
      f.setHidden();  
      f.setFunction("Active_Work_Order_API.Has_Role(:WO_NO)");

      // Bug Id 70921, Start
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


      headset = headblk.getASPRowSet();

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTHD: Prepare Work Order for Service Management"));
      headtbl.setWrap();
      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      headtbl.enableRowSelect();
      // 040123  ARWILK  End  (Enable Multirow RMB actions)

      headbar = mgr.newASPCommandBar(headblk);
      headbar.defineCommand(headbar.DUPLICATEROW,"duplicateRow");

      // 031204  ARWILK  Begin  (Replace links with RMB's)
      headbar.addCustomCommand("reportInWorkOrderSM", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTREPOIN: Report In..."));
      headbar.addCustomCommand("structure", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSTRUCTURE: Structure..."));
      headbar.addCustomCommand("prePost", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPREPOSTING: Preposting..."));
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("copy", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCOPY: Copy..."));
      headbar.addCustomCommand("printWO", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPRNT: Print..."));
      headbar.addCustomCommand("servreqvord", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSERVREQORD: Service Request Order..."));
      headbar.addCustomCommand("turnToRepOrd", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTTURNREPORD: Turn into Repair Order..."));
      headbar.addCustomCommand("prepWorkOrderQuot", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPREPAREWORKORDERQUOT: Prepare Work Order Quotation..."));
      headbar.addCustomCommand("printServO", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPRNTSERO: Print Service Order..."));
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("freeTimeSearch",mgr.translate("PCMWACTIVESEPARATE2SMFREETIMESEARCHH: Free Time Search..."));
      headbar.addCustomCommandSeparator();
      // 031204  ARWILK  End  (Replace links with RMB's)
      headbar.addCustomCommand("reinit",mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTFAULTWORK: FaultReprt\\WorkRequest"));
      headbar.addCustomCommand("observed", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTOBSERVED: Observed"));
      headbar.addCustomCommand("underPrep", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTUNDPREP: Under Preparation"));
      headbar.addCustomCommand("prepared", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPREPARED: Prepared"));
      headbar.addCustomCommand("released", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTRELEASED: Released"));
      headbar.addCustomCommand("started", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSTARTED: Started"));
      headbar.addCustomCommand("workDone", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTWRKDONE: Work Done"));
      headbar.addCustomCommand("reported", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTREPORTED: Reported"));
      headbar.addCustomCommand("finished", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTFINISHED: Finished"));
      headbar.addCustomCommand("cancelled", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCANCELLED: Cancelled"));
      if (mgr.isModuleInstalled("PROJ"))
      {
         headbar.addCustomCommand("connectToActivity","ACTSEP2SMPROJCONNCONNACT: Connect to Project Activity...");
         headbar.addCustomCommand("disconnectFromActivity","ACTSEP2SMPROJCONNDISCONNACT: Disconnect from Project Activity");
         headbar.addCustomCommand("projectActivityInfo", mgr.translate("ACTSEP2SMPRJACTINFO: Project Connection Details..."));
         headbar.addCustomCommand("activityInfo", mgr.translate("ACTSEP2SMPRJACTVITYINFO: Activity Info..."));
      }
      headbar.addCustomCommand("pickListForWork", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPICKLISTFOERWORK: Pick List For Work Order - Printout..."));
      headbar.addCustomCommandSeparator();
      if (mgr.isPresentationObjectInstalled("equipw/EquipmentObjectAddress.page"))
      {
         headbar.addCustomCommand("schonobjaddr", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSCHONOBJADDR: Search on Object Address..."));
// 		Bug 83532, Start
         headbar.addCustomCommand("workorderaddr", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTWORKORDERADDR: Work Order Address..."));
//			Bug 83532, End
         headbar.addCustomCommandSeparator();
      }
      headbar.addCustomCommand("suppWarr", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSUPPWARR: Supplier Warranty Type..."));
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("mroObjReceiveO", mgr.translate("PCMWACTIVESEPARATE2SERMGTRECEIVEORDER: Create Recieve Order...")); 
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("requisitions",mgr.translate("PCMWACTIVESEPARATE2SMREQUISITIONS: Requisitions..."));
      headbar.addCustomCommand("freeNotes", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTFREENOTES: Free Notes..."));
      headbar.addCustomCommand("toolsFacilities", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTTF: Tools and Facilities..."));
      headbar.addCustomCommand("returns", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTRETURNS: Returns...")); 
      headbar.addCustomCommand("permits", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPERMITS: Permits..."));
      headbar.addCustomCommand("coInformation", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCOINFORMATION: CO Information..."));
      //Bug 68947, Start   
      headbar.addCustomCommand("srvConLineSearch",mgr.translate("PCMWACTIVESEPARATE2SMSRVCONLINE: Service Contract Line Search..."));
      //Bug 68947, End   
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("repInWiz", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTREPORTINWIZ: Report in Wizard..."));
      headbar.addCustomCommand("resheduleOperations3",mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTRESHEDULEOPERATIONSHEAD3: Reschedule Operations..."));
      //Bug 72644, Start   
      headbar.addCustomCommand("optimizeSchedule",mgr.translate("PCMWACTIVESEPARATE2SMOPTSCH: Optimize Planning Schedule..."));
      headbar.addCustomCommand("refresh",mgr.translate("PCMWACTIVESEPARATE2SMREFRESH: Refresh"));
      //Bug 72644, End

      headbar.addCustomCommand("refreshForm","");
      headbar.addCustomCommand("refreshMaterialTab","");

      // 031204  ARWILK  Begin  (Replace links with RMB's)
      headbar.addCustomCommandGroup("WOSTATUS", mgr.translate("PCMWACTIVESEPARATE2SERMWORKORDERSTUS: Work Order Status"));
      headbar.setCustomCommandGroup("reinit", "WOSTATUS");
      headbar.setCustomCommandGroup("observed", "WOSTATUS");
      headbar.setCustomCommandGroup("underPrep", "WOSTATUS");
      headbar.setCustomCommandGroup("prepared", "WOSTATUS");
      headbar.setCustomCommandGroup("released", "WOSTATUS");
      headbar.setCustomCommandGroup("started", "WOSTATUS");
      headbar.setCustomCommandGroup("workDone", "WOSTATUS");
      headbar.setCustomCommandGroup("reported", "WOSTATUS");
      headbar.setCustomCommandGroup("finished", "WOSTATUS");
      headbar.setCustomCommandGroup("cancelled", "WOSTATUS");
      // 031204  ARWILK  End  (Replace links with RMB's)

      if (mgr.isModuleInstalled("PROJ"))
      {
         headbar.addCustomCommandGroup("PROJCONNGRP", mgr.translate("ACTSEP2SMPROJCONNGRP: Project Connection"));
         headbar.setCustomCommandGroup("connectToActivity", "PROJCONNGRP");
         headbar.setCustomCommandGroup("disconnectFromActivity", "PROJCONNGRP");
         headbar.setCustomCommandGroup("projectActivityInfo", "PROJCONNGRP");
         headbar.setCustomCommandGroup("activityInfo", "PROJCONNGRP");
      }

      // 031204  ARWILK  Begin  (Replace blocks with tabs)
      headbar.addCustomCommand("activateOperations", "");
      headbar.addCustomCommand("activateMaterial", "");
      headbar.addCustomCommand("activateBudget", "");
      headbar.addCustomCommand("activatePlanning", "");
      headbar.addCustomCommand("activateJobs", "");
      // 031204  ARWILK  End  (Replace blocks with tabs)s

      headbar.defineCommand(headbar.SAVERETURN, "saveReturn","checkMando()"); //Bug Id 70921

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

      headbar.addCommandValidConditions("resheduleOperations3",    "OP_EXIST",  "Enable",  "TRUE");    
      // Bug 90872, Start
      headbar.appendCommandValidConditions("resheduleOperations3", "PLAN_S_DATE", "Disable", "");
      headbar.appendCommandValidConditions("resheduleOperations3", "PLAN_F_DATE", "Disable", "");
      // Bug 90872, End

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      headbar.addCommandValidConditions("servreqvord",        "OBJSTATE",      "Enable",   "FAULTREPORT;WORKREQUEST;OBSERVED;UNDERPREPARATION;PREPARED");
      headbar.addCommandValidConditions("prepWorkOrderQuot",  "QUOTATION_ID",  "Disable",  "null");

      headbar.addCommandValidConditions("mroObjReceiveO",     "RECEIVE_ORDER_NO", "Enable",   "null");
      headbar.appendCommandValidConditions("mroObjReceiveO",  "IDENTITY_TYPE",    "EXTERN");

      if (mgr.isModuleInstalled("PROJ"))
      {
         headbar.addCommandValidConditions("connectToActivity", "ACTIVITY_SEQ", "Enable", "");
         headbar.addCommandValidConditions("disconnectFromActivity", "ACTIVITY_SEQ", "Disable", "");
         headbar.addCommandValidConditions("projectActivityInfo", "ACTIVITY_SEQ", "Disable", "");
         headbar.addCommandValidConditions("activityInfo", "", "Enable", "");
      }

      //Bug 72644, Start
      headbar.addCommandValidConditions("optimizeSchedule", "PLAN_S_DATE", "Disable", "");
      headbar.appendCommandValidConditions("optimizeSchedule", "PLAN_F_DATE", "Disable", "");
      //Bug 72644, End

      headbar.enableMultirowAction();
      headbar.removeFromMultirowAction("prePost");
      headbar.removeFromMultirowAction("copy");               
      headbar.removeFromMultirowAction("turnToRepOrd");                   
      if (mgr.isPresentationObjectInstalled("equipw/EquipmentObjectAddress.page"))
      {
         headbar.removeFromMultirowAction("schonobjaddr");       
// 		Bug 83532, Start
         headbar.removeFromMultirowAction("workorderaddr");
// 		Bug 83532, Start
     }
      headbar.removeFromMultirowAction("suppWarr");  
      headbar.removeFromMultirowAction("freeNotes");
      headbar.removeFromMultirowAction("toolsFacilities");
      headbar.removeFromMultirowAction("returns"); 
      headbar.removeFromMultirowAction("permits");
      headbar.removeFromMultirowAction("coInformation");
      //Bug 68947, Start   
      headbar.removeFromMultirowAction("srvConLineSearch");
      //Bug 68947, End   
      headbar.removeFromMultirowAction("repInWiz");
      // The RMB cancelled is allowed only in single row selection because 
      // the state change requires a data entry as a prerequisite. Check the method cancelled.

      headbar.removeFromMultirowAction("cancelled");
      // 040123  ARWILK  End  (Enable Multirow RMB actions)
      headbar.removeFromMultirowAction("freeTimeSearch");

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
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
      // 031008  ARWILK  Begin (GUI Modifications)
//  Bug 68947, Start
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTGRPLABEL1: General"),"WO_NO,CONTRACT,ERR_DESCR,REPORTED_BY,NAME,STATE,CONNECTION_TYPE,MCH_CODE,MCH_CODE_DESCRIPTION,MCH_CODE_CONTRACT,ORG_CODE,ORGCODEDESCRIPTION,CRITICALITY,CRITICALITYDESCRIPTION,LATESTTRANSAC,CUSTOMER_NO,CUSTOMERDESCRIPTION,CONTRACT_ID,CONTRACT_NAME,LINE_NO,LINE_DESC,AGREEMENT_ID,REFERENCE_NO,REG_DATE",true,true);
      //Bug 87766, Start, Added CBHASCONN and CBISCONN
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTGRPLABEL7: Work Order"),"CBWARRNOBJECT,REPAIR_FLAG,FIXED_PRICE_DB_VAL,CBHASDOCUMNTS,CBHASCONN,CBPROJCONNECTED,CBISCONN,CBHASOBSOLETEJOBS,CBTRANSTOMOB",true,true);
      //Bug 87766, End
      // 031008  ARWILK  End (GUI Modifications)
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTGRPLABEL2: Prepare Info"),"WORK_DESCR_LO,OP_STATUS_ID,OPSTATUSIDDESCR,CALL_CODE,CALLCODEDESCRIPTION,VENDOR_NO,VENDORNAME,WORK_LEADER_SIGN,WORKLEADERNAME,WORK_MASTER_SIGN,WORKMASTERBYNAME,PREPARED_BY,PREPAREDBYNAME,WORK_TYPE_ID,WORKTYPEDESCRIPTION,PRIORITY_ID,PRIORITYDESCRIPTION,AUTHORIZE_CODE,AUTHORIZECODENAME,ACTION_CODE_ID,",true,false);
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTGRPLABEL3: Planning Schedule"),"PLAN_S_DATE,REQUIRED_START_DATE,PLAN_F_DATE,REQUIRED_END_DATE,PLAN_HRS,PLANNED_MAN_HRS,REMAIN_HRS",true,false);
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTGRPLABEL8: Operation Hours"),"MAN_HOURS,ALLOC_HOURS,REMAIN_HOURS",true,false);
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTGRPLABEL4: Fault Report Info"),"ERR_DISCOVER_CODE,DISCOVERDESCRIPTION,ERR_SYMPTOM,SYMPTOMDESCRIPTION,ERR_DESCR_LO",true,false);
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTGRPLABEL5: PM Information"),"PM_NO,PM_REVISION,TEST_POINT_ID,TESTPOINTIDDES,NOTE,PM_DESCR",true,false);   
      headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTGRPLABEL6: Supplier Warranty Info"),"SUP_WARR_TYPE,SUP_WARR_DESC,SUPPLIER,SUPP_DESCR",true,false);
      headlay.setDialogColumns(2);

      headlay.setSimple("NAME");
      headlay.setSimple("ORGCODEDESCRIPTION");
      headlay.setSimple("WORKTYPEDESCRIPTION");
      headlay.setSimple("PRIORITYDESCRIPTION");
      headlay.setSimple("CRITICALITYDESCRIPTION");
      headlay.setSimple("OPSTATUSIDDESCR");
      headlay.setSimple("CALLCODEDESCRIPTION");
      headlay.setSimple("VENDORNAME");
      headlay.setSimple("DISCOVERDESCRIPTION");
      headlay.setSimple("SYMPTOMDESCRIPTION");
      headlay.setSimple("WORKLEADERNAME");
      headlay.setSimple("WORKMASTERBYNAME");
      headlay.setSimple("PREPAREDBYNAME");
      headlay.setSimple("MCH_CODE_DESCRIPTION");
      headlay.setSimple("AUTHORIZECODENAME"); 
      headlay.setSimple("TESTPOINTIDDES");
      headlay.setSimple("CUSTOMERDESCRIPTION");
      headlay.setSimple("SUPP_DESCR");
      headlay.setSimple("SUP_WARR_DESC");
      headlay.setSimple("CONTRACT_NAME");
      headlay.setSimple("LINE_DESC");
      headlay.setFieldSpan("LATESTTRANSAC",1,3);
//  Bug 68947, End

      // 031204  ARWILK  Begin  (Replace blocks with tabs)
      tabs = mgr.newASPTabContainer();
      tabs.addTab("OPERATIONS","PCMWACTIVESEPARATE2SERMANOPERTAB: Operations", "javascript:commandSet('HEAD.activateOperations','')"); 
      tabs.addTab("MATERIALS","PCMWACTIVESEPARATE2SERMANMATTAB: Materials", "javascript:commandSet('HEAD.activateMaterial','')");
      tabs.addTab("BUDGET","PCMWACTIVESEPARATE2SERMANBUDGTAB: Budget", "javascript:commandSet('HEAD.activateBudget','')");
      tabs.addTab("PLANNING","PCMWACTIVESEPARATE2SERMANPLANNTAB: Planning", "javascript:commandSet('HEAD.activatePlanning','')");
      tabs.addTab("JOBS","PCMWACTIVESEPARATE2SERMANJOBSTAB: Jobs", "javascript:commandSet('HEAD.activateJobs','')");
      // 031204  ARWILK  End  (Replace blocks with tabs)

      //-----------------------------------------------------------------------
      //-------------- This part belongs to CRAFTS TAB ------------------------
      //-----------------------------------------------------------------------

      itemblk2 = mgr.newASPBlock("ITEM2");

      itemblk2.addField("ITEM2_OBJID").
      setHidden().
      setDbName("OBJID");

      itemblk2.addField("ITEM2_OBJVERSION").
      setHidden().
      setDbName("OBJVERSION");

      itemblk2.addField("ALLOC_NO","Number").
      setDbName("ALLOCATION_NO").
      setHidden().
      setLabel("PCMWACTIVESEPARATE2SERVMGTALLOCATIONNO: Allocation No");

      itemblk2.addField("ITEM2_WO_NO","Number","#").
      setMandatory().
      setHidden().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM2WONO: WO No").
      setDbName("WO_NO");

      itemblk2.addField("ROW_NO","Number").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTROWNO: Operation No").
      setReadOnly().
      setInsertable();

      itemblk2.addField("TEMP").      
      setHidden().
      setFunction("''");

      itemblk2.addField("ITEM2_ORG_CONTRACT").      
      setHidden().
      setFunction("''");

      itemblk2.addField("ITEM2_COMPANY").
      setSize(8).
      setHidden().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM2COMPANY: Company").
      setUpperCase().
      setDbName("COMPANY").
      setMaxLength(20);

      itemblk2.addField("ITEM2_DESCRIPTION").
      setSize(27).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM2DESCRIPTION: Description").
      setDbName("DESCRIPTION").
      setDefaultNotVisible().
      setMaxLength(60);

      itemblk2.addField("SIGN").
      setSize(10).
      setLOV("../mscomw/MaintEmployeeLov.page","ITEM2_COMPANY COMPANY",600,450).
      //setCustomValidation("ITEM2_COMPANY,SIGN,ROLE_CODE,ITEM2_CONTRACT,ITEM2_ORG_CODE,ORG_CODE,COST,CATALOG_NO,PLAN_MEN,ITEM2_PLAN_HRS,ITEM2_WO_NO,ITEM2_WO_NO,ALLOCATED_HOURS,TOTAL_MAN_HOURS","SIGN_ID,ROLE_CODE,ITEM2_CONTRACT,ITEM2_ORG_CODE,CATALOG_NO,COST,COSTAMOUNT,CATALOGDESC,SIGN,ALLOCATED_HOURS,TOTAL_ALLOCATED_HOURS,TOTAL_REMAINING_HOURS,ITEM2_ORG_CODE_DESC,ROLE_CODE_DESC").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSIGNA: Executed By").
      setUpperCase().
      setMaxLength(20);

      itemblk2.addField("SIGN_ID").
      setSize(18).
      setMaxLength(11).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSIGNID: Employee ID").
      //setCustomValidation("SIGN_ID,ITEM2_COMPANY,ITEM2_WO_NO,ITEM2_PLAN_HRS,PLAN_MEN,ROLE_CODE,ITEM2_CONTRACT,ITEM2_ORG_CODE,CATALOG_NO,CATALOG_CONTRACT,CATALOGDESC,PRICE_LIST_NO,LISTPRICE,DISCOUNT,DISCOUNTED_PRICE,SALESPRICEAMOUNT","SIGN,ROLE_CODE,ITEM2_CONTRACT,ITEM2_ORG_CODE,CATALOG_CONTRACT,CATALOG_NO,CATALOGDESC,PRICE_LIST_NO,LISTPRICE,DISCOUNT,DISCOUNTED_PRICE,SALESPRICEAMOUNT").
      setUpperCase().
      setReadOnly();

      itemblk2.addField("ITEM2_ORG_CODE").
      setSize(11).
      setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV", "ITEM2_CONTRACT CONTRACT",600,450).
      setCustomValidation("ITEM2_CONTRACT,ITEM2_ORG_CODE,ROLE_CODE,CATALOG_NO,CATALOG_CONTRACT,CATALOGDESC,COST,COSTAMOUNT,ITEM2_PLAN_HRS,ORG_CODE,PLAN_MEN,SIGN,ITEM2_WO_NO","CATALOG_NO,CATALOGDESC,COST,COSTAMOUNT,ITEM2_ORG_CODE,ITEM2_CONTRACT,ITEM2_ORG_CODE_DESC").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM2ORGCODE: Maint.Org.").
      setUpperCase().
      setDbName("ORG_CODE").
      setMaxLength(8);

      //Bug ID 59224,start
      itemblk2.addField("ITEM2_ORG_CODE_DESC").
      setLabel("PCMWACTIVESEPARATE2ORGCODEDESC: Maint. Org. Description").
      setFunction("Organization_Api.Get_Description(:ITEM2_CONTRACT,:ITEM2_ORG_CODE)").
      setReadOnly();
      //Bug ID 59224,end


      itemblk2.addField("ITEM2_CONTRACT").
      setSize(8).
      setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM2CONTRACT: Maint.Org. Site").
      setUpperCase().
      setDbName("CONTRACT").
      setMaxLength(5);

      itemblk2.addField("ROLE_CODE").
      setSize(9).
      setDynamicLOV("ROLE_TO_SITE_LOV","ITEM2_CONTRACT CONTRACT",600,445).
      setCustomValidation("ROLE_CODE,ITEM2_CONTRACT,ITEM2_ORG_CODE,ORG_CODE,COST,CATALOG_NO,PLAN_MEN,ITEM2_PLAN_HRS,SIGN,ITEM2_WO_NO,CATALOG_CONTRACT","CATALOG_NO,COST,COSTAMOUNT,CATALOGDESC,ROLE_CODE,ITEM2_CONTRACT,ROLE_CODE_DESC").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTROLECODE: Craft ID").
      setUpperCase().
      setMaxLength(10);

      itemblk2.addField("ROLE_CODE_DESC").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTROLECODEDESC: Craft Description").
      setFunction("ROLE_API.Get_Description(:ROLE_CODE)").
      setReadOnly();

      itemblk2.addField("PLAN_MEN","Number").
      //setCustomValidation("PLAN_MEN,ITEM2_PLAN_HRS,COST,ROLE_CODE,ITEM2_CONTRACT,ITEM2_ORG_CODE,ORG_CODE,CATALOG_CONTRACT,CATALOG_NO,PRICE_LIST_NO,CUSTOMER_NO,AGREEMENT_ID,LISTPRICE,DISCOUNT,DISCOUNTED_PRICE,ITEM2_WO_NO,TOTAL_ALLOCATED_HOURS","COST,COSTAMOUNT,PRICE_LIST_NO,DISCOUNT,LISTPRICE,SALESPRICEAMOUNT,DISCOUNTED_PRICE,TOTAL_MAN_HOURS,TOTAL_REMAINING_HOURS").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPLANMEN: Planned Men");

      itemblk2.addField("ITEM2_PLAN_HRS","Number").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM2PLANHRS: Planned Hours").
      //setCustomValidation("PLAN_MEN,ITEM2_PLAN_HRS,COST,ROLE_CODE,ITEM2_CONTRACT,ITEM2_ORG_CODE,ORG_CODE,CATALOG_CONTRACT,CATALOG_NO,PRICE_LIST_NO,CUSTOMER_NO,AGREEMENT_ID,LISTPRICE,DISCOUNT,DISCOUNTED_PRICE,ITEM2_WO_NO,TOTAL_ALLOCATED_HOURS","COST,COSTAMOUNT,PRICE_LIST_NO,DISCOUNT,LISTPRICE,SALESPRICEAMOUNT,DISCOUNTED_PRICE,TOTAL_MAN_HOURS,TOTAL_REMAINING_HOURS").
      setDbName("PLAN_HRS");

      itemblk2.addField("TOTAL_MAN_HOURS","Number").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTMANHRS: Total Man Hours").
      setFunction(":PLAN_MEN * :PLAN_HRS").
      setReadOnly();

      itemblk2.addField("ALLOCATED_HOURS","Number").
      setCustomValidation("ALLOCATED_HOURS,TOTAL_MAN_HOURS","TOTAL_ALLOCATED_HOURS,TOTAL_REMAINING_HOURS").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTALLOCATEDHRS: Allocated Hours");

      itemblk2.addField("TOTAL_ALLOCATED_HOURS","Number").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTTOTALLOCATEDHRS: Total Allocated Hours").
      setFunction("Work_Order_Role_API.Get_Total_Alloc(:ITEM2_WO_NO,:ROW_NO)").
      setReadOnly();

      itemblk2.addField("TOTAL_REMAINING_HOURS","Number").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTREMAININGHRS: Total Remaining Hours").
      setFunction("(:PLAN_MEN * :PLAN_HRS) - Work_Order_Role_API.Get_Total_Alloc(:ITEM2_WO_NO,:ROW_NO)").
      setReadOnly();

      itemblk2.addField("TEAM_CONTRACT").
      setSize(7).
      setDynamicLOV("USER_ALLOWED_SITE_LOV","ITEM2_COMPANY COMPANY",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCONT: Team Site").
      setMaxLength(5).
      setInsertable().
      setUpperCase();

      itemblk2.addField("TEAM_ID").
      setSize(13).
      setCustomValidation("TEAM_ID,TEAM_CONTRACT","TEAMDESC").
      setDynamicLOV("MAINT_TEAM","TEAM_CONTRACT",600,450).
      setMaxLength(20).
      setInsertable().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTTID: Team ID").
      setUpperCase();

      itemblk2.addField("TEAMDESC").
      setFunction("Maint_Team_API.Get_Description(:TEAM_ID,:TEAM_CONTRACT)").
      setSize(40).
      setMaxLength(200).
      setReadOnly().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTDESC: Description");

      itemblk2.addField("CATALOG_CONTRACT").
      setSize(15).
      setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCATACONTRACT: Sales Part Site").
      //setCustomValidation("PLAN_MEN,ITEM2_PLAN_HRS,COST,ROLE_CODE,ITEM2_CONTRACT,ITEM2_ORG_CODE,ORG_CODE,CATALOG_CONTRACT,CATALOG_NO,PRICE_LIST_NO,CUSTOMER_NO,AGREEMENT_ID,LISTPRICE,DISCOUNT,DISCOUNTED_PRICE,ITEM2_WO_NO","COST,COSTAMOUNT,PRICE_LIST_NO,DISCOUNT,LISTPRICE,SALESPRICEAMOUNT,DISCOUNTED_PRICE").
      setUpperCase().
      setReadOnly().
      setMaxLength(5);

      itemblk2.addField("CATALOG_NO").
      setSize(17).
      setDynamicLOV("SALES_PART_SERVICE_LOV","CATALOG_CONTRACT CONTRACT",600,450).
      //setCustomValidation("CATALOG_CONTRACT,CATALOG_NO,PRICE_LIST_NO,PLAN_MEN,CUSTOMER_NO,AGREEMENT_ID,ITEM2_CONTRACT,ITEM2_ORG_CODE,ITEM2_PLAN_HRS,ORG_CODE,ROLE_CODE,HEAD_CURRENCEY_CODE,DISCOUNTED_PRICE,ITEM2_ROWSTATUS,ITEM2_WO_NO","CATALOGDESC,COST,COSTAMOUNT,PRICE_LIST_NO,DISCOUNT,LISTPRICE,SALESPRICEAMOUNT,DISCOUNTED_PRICE").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCATOGNO: Sales Part").
      setUpperCase().
      setMaxLength(25);

      f = itemblk2.addField("CATALOGDESC");
      f.setSize(25);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCATALOGDESCR: Sales Part Description");
      if (mgr.isModuleInstalled("ORDER"))
         f.setFunction("SALES_PART_API.Get_Catalog_Desc(:CATALOG_CONTRACT,:CATALOG_NO)");
      else
         f.setFunction("''");
      f.setReadOnly();
      f.setMaxLength(2000);

      itemblk2.addField("PRICE_LIST_NO").
      setSize(13).
      setDynamicLOV("SALES_PRICE_LIST_SITE_LOV2",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPRICELISTNO: Price List No").
      setUpperCase().
      setCustomValidation("PLAN_MEN,ITEM2_PLAN_HRS,COST,ROLE_CODE,ITEM2_CONTRACT,ITEM2_ORG_CODE,ORG_CODE,CATALOG_CONTRACT,CATALOG_NO,PRICE_LIST_NO,CUSTOMER_NO,AGREEMENT_ID,ITEM2_WO_NO","COST,COSTAMOUNT,PRICE_LIST_NO,LISTPRICE,SALESPRICEAMOUNT,DISCOUNT,DISCOUNTED_PRICE").      
      setDefaultNotVisible().   
      setMaxLength(10);

      itemblk2.addField("COST","Money").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCOSTITEM2: Cost").
      setFunction("WORK_ORDER_PLANNING_UTIL_API.Get_Cost(:ORG_CODE,:ROLE_CODE,:CATALOG_CONTRACT,:CATALOG_NO,:ITEM2_CONTRACT)").
      setCustomValidation("COST,ORG_CODE,ROLE_CODE,ITEM2_CONTRACT,ITEM2_ORG_CODE,CATALOG_NO","COST,COSTAMOUNT").
      setDefaultNotVisible().   
      setReadOnly();

      itemblk2.addField("COSTAMOUNT","Money").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCOSTAMT: Cost Amount").
      setFunction("NVL(:PLAN_MEN,1)*:PLAN_HRS*(WORK_ORDER_PLANNING_UTIL_API.Get_Cost(:ORG_CODE,:ROLE_CODE,:CATALOG_CONTRACT,:CATALOG_NO,:ITEM2_CONTRACT))").
      setDefaultNotVisible().    
      setReadOnly();

      itemblk2.addField("LISTPRICE", "Money").  
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTLSTPRICE: Sales Price").
      setDbName("SALES_PRICE").
      setCustomValidation("LISTPRICE,DISCOUNT,PLAN_MEN,ITEM2_PLAN_HRS","DISCOUNTED_PRICE,SALESPRICEAMOUNT").
      setDefaultNotVisible();

      itemblk2.addField("DISCOUNT","Number").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTDISCOUNT: Discount").
      setCustomValidation("LISTPRICE,DISCOUNT,PLAN_MEN,ITEM2_PLAN_HRS","DISCOUNTED_PRICE,SALESPRICEAMOUNT");

      itemblk2.addField("DISCOUNTED_PRICE","Money").
      setFunction(":LISTPRICE - (NVL(:DISCOUNT, 0)/100*:LISTPRICE)").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTDISCOUNTEDPRICE: Discounted Price").
      setDefaultNotVisible().    
      setReadOnly();   

      itemblk2.addField("SALESPRICEAMOUNT", "Money").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSALESPRICEANT: Sales Price Amount").
      setFunction("NVL(:PLAN_MEN, 1)*:PLAN_HRS*:LISTPRICE - (NVL(:DISCOUNT, 0)/100*(NVL(:PLAN_MEN, 1)*:PLAN_HRS*:LISTPRICE))").
      setReadOnly();

      itemblk2.addField("ITEM2_JOB_ID", "Number").
      setDbName("JOB_ID").
      setInsertable().
      setDefaultNotVisible().
      setDynamicLOV("WORK_ORDER_JOB", "ITEM2_WO_NO WO_NO").
      setCustomValidation("ITEM2_COMPANY,ITEM2_WO_NO,ITEM2_JOB_ID","SIGN_ID,SIGN").
      setLabel("PCMWACTIVESEPARATE2SMITEM2JOBID: Job Id");

      itemblk2.addField("DATE_FROM","Datetime").
      setSize(22).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTDATEFROM: Date from").
      setDefaultNotVisible().   
      setCustomValidation("ITEM2_WO_NO,DATE_FROM","ITEM2_TEMP,DATE_FROM_MASKED");

      itemblk2.addField("DATE_TO","Datetime").
      setSize(22).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTDATETO: Date to").
      setDefaultNotVisible().   
      setCustomValidation("ITEM2_WO_NO,DATE_TO","ITEM2_TEMP");

      itemblk2.addField("DATE_FROM_MASKED","Datetime", getDateTimeFormat("JAVA")).
      setHidden().
      setFunction("''");

      itemblk2.addField("PREDECESSOR").
      setSize(22).
      setLabel("PCMWACTIVESEPARATE2SERVICEMGTPRED: Predecessors").
      setLOV("ConnectPredecessorsDlg.page",600,450).
      setFunction("Wo_Role_Dependencies_API.Get_Predecessors(:ITEM2_WO_NO, :ROW_NO)");

      itemblk2.addField("TOOLS").
      setSize(12).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTTOOLS: Tools").
      setDefaultNotVisible().   
      setMaxLength(25);

      itemblk2.addField("REMARK").
      setSize(14).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTREMARK: Remark").
      setDefaultNotVisible().   
      setMaxLength(80);

      itemblk2.addField("BASE_UNIT_PRICE","Money").
      setFunction("''").
      setHidden();

      itemblk2.addField("ITEM2_AGREEMENT_ID").
      setFunction("''").
      setHidden();

      itemblk2.addField("SALE_UNIT_PRICE","Money").
      setFunction("''").
      setHidden();

      itemblk2.addField("CURRENCY_RATE").
      setFunction("''").
      setHidden();

      itemblk2.addField("BUY_QTY_DUE","Number").
      setFunction("''").
      setHidden();

      itemblk2.addField("ITEM2_CUSTOMER_NO").
      setFunction("''").
      setHidden();

      itemblk2.addField("ITEM2_TEMP").
      setFunction("''").
      setHidden();

      itemblk2.addField("PRICELISTNO").
      setFunction("''").
      setHidden();

      itemblk2.addField("ITEM2_ROWSTATUS").
      setFunction("''").
      setHidden();

      itemblk2.addField("USERALLOWED").
      setFunction("''").
      setHidden();

      itemblk2.addField("CBADDQUALIFICATION").
      setLabel("PCMWACTIVESEP2SERVICEMGTCBADDQUAL: Additional Qualifications").
      setFunction("Work_Order_Role_API.Check_Qualifications_Exist(:ITEM2_WO_NO,:ROW_NO)").
      setCheckBox("0,1").
      setReadOnly().
      setQueryable();

      itemblk2.setView("WORK_ORDER_ROLE");
      itemblk2.defineCommand("WORK_ORDER_ROLE_API","New__,Modify__,Remove__");
      itemset2 = itemblk2.getASPRowSet();

      itemblk2.setMasterBlock(headblk);

      itembar2 = mgr.newASPCommandBar(itemblk2);
      itembar2.enableCommand(itembar2.FIND);
      itembar2.defineCommand(itembar2.NEWROW,"newRowITEM2");
      itembar2.defineCommand(itembar2.EDITROW, "editRowITEM2");
      itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
      itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");

      itembar2.defineCommand(itembar2.SAVERETURN,"saveReturnItem2","checkMandoItem2();Item2PlanMenCheck()");
      itembar2.defineCommand(itembar2.SAVENEW,null,"checkMandoItem2()");
      itembar2.defineCommand(itembar2.DELETE,"deleteITEM2");

      itembar2.addCustomCommand("freeTimeSearchFromOperations1",mgr.translate("PCMWACTIVESEPARATE2SMFREETIMESEARCH1: Free Time Search..."));
      itembar2.addCustomCommand("allocateEmployees0",mgr.translate("PCMWACTIVESEPARATE2SERMHTALLOCATEEMPLOYEES0: Allocate Employees..."));
      itembar2.addCustomCommand("selectAllocation0",mgr.translate("PCMWACTIVESEPARATE2SERMHTCLEARALLOCATION0: Clear Allocation..."));
      itembar2.addCustomCommand("additionalQualifications1",mgr.translate("PCMWACTIVESEPARATE2SERVICEMGTADDQUALIFICATIONS2: Additional Qualifications..."));
      itembar2.addCustomCommand("predecessors1",mgr.translate("PCMWACTIVESEPARATE2SERVICEMGTPREDECESSORS1: Predecessors..."));
      itembar2.addCustomCommand("resheduleOperations1",mgr.translate("PCMWACTIVESEPARATE2SERVICEMGTRESHEDULEOPERATIONSITEM2: Reschedule Operations..."));
      itembar2.addCustomCommand("moveOperation1",mgr.translate("PCMWACTIVESEPARATE2SERVICEMGTMOVEOPERATION1: Move Operation..."));
      itembar2.removeFromMultirowAction("resheduleOperations1");
      itembar2.removeFromMultirowAction("moveOperation1");

      //Bug 90872, Start
      itembar2.addCommandValidConditions("resheduleOperations1",    "DATE_FROM",  "Disable",  "");
      itembar2.appendCommandValidConditions("resheduleOperations1",    "DATE_TO",  "Disable",  "");
      //Bug 90872, End
      itembar2.addCommandValidConditions("moveOperation1",    "DATE_FROM",  "Disable",  "");                              
      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.setWrap();
      itemtbl2.enableRowSelect();
      itembar2.enableMultirowAction();

      itemlay2 = itemblk2.getASPBlockLayout();
      itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);
      itemlay2.setDialogColumns(3);  

      //------------Allocation on OLperations - child of operations-------------

      itemblk9 = mgr.newASPBlock("ITEM9");

      itemblk9.addField("ITEM9_OBJID").
      setHidden().
      setDbName("OBJID");

      itemblk9.addField("ITEM9_OBJVERSION").
      setHidden().
      setDbName("OBJVERSION");

      itemblk9.addField("PARENT_ROW_NO","Number").
      setLabel("PCMWACTIVESEP2SERVICEMGTPOPERATIONNO: Parent Operation No").
      setHidden();

      itemblk9.addField("ITEM9_ROW_NO","Number").
      setLabel("PCMWACTIVESEP2SERVICEMGTITEM9ROWNO: Operation No").
      setDbName("ROW_NO").
      setHidden().
      setInsertable();

      itemblk9.addField("ALLOCATION_NO","Number").
      setReadOnly().
      setInsertable().
      setLabel("PCMWACTIVESEP2SERVICEMGTALLOCNO: Allocation No");

      itemblk9.addField("ITEM9_DESCRIPTION").
      setDbName("DESCRIPTION").
      setLabel("PCMWACTIVESEP2SERVICEMGTALLOCDESC: Description");

      itemblk9.addField("ITEM9_SIGN").
      setSize(20).
      setLOV("../mscomw/MaintEmployeeLov.page","ITEM9_COMPANY COMPANY",600,450).
      setCustomValidation("ITEM9_COMPANY,ITEM9_SIGN,ITEM9_ROLE_CODE,ITEM9_CONTRACT,ITEM9_ORG_CODE,ORG_CODE","ITEM9_SIGN_ID,ITEM9_ROLE_CODE,ITEM9_ORG_CODE,ITEM9_SIGN,ITEM9_CONTRACT").
      setLOVProperty("TITLE","PCMWACTIVESEPARATE2LOVTITLE5: List of Employee").
      setLabel("PCMWACTIVESEP2SERVICEMGTITEM8SIGN: Executed By").
      setMandatory().
      setDbName("SIGN").
      setUpperCase().
      setMaxLength(20);

      itemblk9.addField("ITEM9_SIGN_ID").
      setSize(18).
      setMaxLength(11).
      setLabel("PCMWACTIVESEP2SERVICEMGTITEM9SIGNID: Employee ID").
      setDbName("SIGN_ID").
      setCustomValidation("ITEM9_SIGN_ID,ITEM9_COMPANY,ITEM9_ROLE_CODE","ITEM9_SIGN,ITEM9_ROLE_CODE,ITEM9_ORG_CODE").
      setUpperCase().
      setReadOnly();

      itemblk9.addField("ITEM9_ORG_CODE").
      setSize(8).
      setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ITEM9_CONTRACT CONTRACT",600,450).
      setCustomValidation("ITEM9_CONTRACT,ITEM9_ORG_CODE,ITEM9_ROLE_CODE,ITEM9_SIGN,ITEM9_WO_NO","ITEM9_ORG_CODE,ITEM9_CONTRACT,ITEM9_ORG_CODE_DESC").
      setLabel("PCMWACTIVESEP2SERVICEMGTITEM9ORGCODE: Maint.Org.").
      setUpperCase().
      setDbName("ORG_CODE").
      setMaxLength(8);
      //Bug ID 59224,start
      itemblk9.addField("ITEM9_ORG_CODE_DESC").
      setLabel("PCMWACTIVESEPARATE1ORGCODEDESC: Maint. Org. Description").
      setFunction("Organization_Api.Get_Description(:ITEM9_CONTRACT,:ITEM9_ORG_CODE)").
      setReadOnly();

      itemblk9.addField("ITEM9_CONTRACT").
      setSize(8).
      setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
      setLabel("PPCMWACTIVESEP2SERVICEMGTITEM9CONTRACT: Maint.Org. Site").
      setUpperCase().
      setDbName("CONTRACT").
      setMaxLength(5);

      //Bug ID 59224,start
      itemblk9.addField("ITEM9_ROLE_CODE").
      setSize(10).
      setDynamicLOV("ROLE_TO_SITE_LOV","ITEM9_CONTRACT CONTRACT",600,445).
      setCustomValidation("ITEM9_ROLE_CODE,ITEM9_CONTRACT,ITEM9_ORG_CODE,ITEM9_WO_NO","ITEM9_ROLE_CODE,ITEM9_CONTRACT,ITEM9_ROLE_CODE_DESC").
      setLabel("PCMWACTIVESEP2SERVICEMGTITEM9ROLECODE: Craft ID").
      setDbName("ROLE_CODE").
      setUpperCase().
      setMaxLength(10);

      itemblk9.addField("ITEM9_ROLE_CODE_DESC").
      setLabel("PCMWACTIVESEPARATE2ROLECODEDESC: Craft Description").
      setFunction("ROLE_API.Get_Description(:ITEM9_ROLE_CODE)").
      setReadOnly();
      //Bug ID 59224,end
      itemblk9.addField("ITEM9_ALLOCATED_HOURS","Number").
      setDbName("ALLOCATED_HOURS").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTALLOCATEDHRS: Allocated Hours");


      itemblk9.addField("ITEM9_TEAM_CONTRACT").
      setSize(7).
      setDynamicLOV("USER_ALLOWED_SITE_LOV","ITEM9_COMPANY COMPANY",600,450).
      setLabel("PCMWACTIVESEP2SERVICEMGTITEM9TCONT: Team Site").
      setMaxLength(5).
      setDbName("TEAM_CONTRACT").
      setInsertable().
      setUpperCase();

      itemblk9.addField("ITEM9_TEAM_ID").
      setSize(13).
      setCustomValidation("ITEM9_TEAM_ID,ITEM9_TEAM_CONTRACT","ITEM9_TEAMDESC").
      setDynamicLOV("MAINT_TEAM","TEAM_CONTRACT",600,450).
      setMaxLength(20).
      setDbName("TEAM_ID").
      setInsertable().
      setLabel("PCMWACTIVESEP2SERVICEMGTITEM9TEAMID: Team ID").
      setUpperCase();

      itemblk9.addField("ITEM9_TEAMDESC").
      setFunction("Maint_Team_API.Get_Description(:ITEM9_TEAM_ID,:ITEM9_TEAM_CONTRACT)").
      setSize(40).
      setMaxLength(200).
      setReadOnly().
      setLabel("PCMWACTIVESEP2SERVICEMGTITEM9DESC: Description");

      itemblk9.addField("ITEM9_DATE_FROM","Datetime").
      setSize(22).
      setDbName("DATE_FROM").
      setMandatory().
      setCustomValidation("ITEM9_DATE_FROM","ITEM9_DATE_FROM_MASKED").
      setLabel("PCMWACTIVESEP2SERVICEMGTITEM9DFROM: Date from");

      itemblk9.addField("ITEM9_DATE_FROM_MASKED","Datetime", getDateTimeFormat("JAVA")).
      setHidden().
      setFunction("''");

      itemblk9.addField("ITEM9_DATE_TO","Datetime").
      setSize(22).
      setDbName("DATE_TO").
      setMandatory().
      setLabel("PCMWACTIVESEP2SERVICEMGTITEM9DTO: Date to");


      itemblk9.addField("ITEM9_WO_NO","Number","#").
      setMandatory().
      setHidden().
      setDbName("WO_NO");

      itemblk9.addField("ITEM9_COMPANY").
      setSize(18).
      setHidden().
      setLabel("PCMWACTIVESEP2SERVICEMGTITEM9COMPANY: Company").
      setUpperCase().
      setDbName("COMPANY").
      setMaxLength(20);

      itemblk9.addField("ITEM9_TEMP").
      setFunction("''").
      setHidden();

      itemblk9.setView("WORK_ORDER_ROLE");
      itemblk9.defineCommand("WORK_ORDER_ROLE_API","New__,Modify__,Remove__");
      itemset9 = itemblk9.getASPRowSet();

      itemblk9.setMasterBlock(itemblk2);

      itembar9 = mgr.newASPCommandBar(itemblk9);
      itembar9.enableCommand(itembar9.FIND);
      itembar9.defineCommand(itembar9.NEWROW,"newRowITEM9");
      itembar9.defineCommand(itembar9.EDITROW, "editRowITEM9");
      itembar9.defineCommand(itembar9.COUNTFIND,"countFindITEM9");
      itembar9.defineCommand(itembar9.OKFIND,"okFindITEM9");
      itembar9.defineCommand(itembar9.DUPLICATEROW,"duplicateITEM9");

      itembar9.defineCommand(itembar9.SAVERETURN, "saveReturnITEM9","checkItem9Fields(-1)");
      itemtbl9 = mgr.newASPTable(itemblk9);
      itemtbl9.setWrap();
      itemtbl9.enableRowSelect();
      itembar9.enableMultirowAction();

      itemlay9 = itemblk9.getASPBlockLayout();
      itemlay9.setDefaultLayoutMode(itemlay9.MULTIROW_LAYOUT);
      itemlay9.setDialogColumns(3);  

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

      f = itemblk3.addField("ITEM3_WO_NO","Number","#");
      f.setSize(11);
      f.setDbName("WO_NO");
      f.setMaxLength(8);
      f.setReadOnly();
      f.setCustomValidation("ITEM3_WO_NO","DUE_DATE,NPREACCOUNTINGID,ITEM3_CONTRACT,ITEM3_COMPANY,MCHCODE,ITEM3DESCRIPTION");
      f.setDynamicLOV("ACTIVE_WO_NOT_REPORTED",600,445);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTWO_NO: WO No");

      f = itemblk3.addField("MAINT_MATERIAL_ORDER_NO","Number","#");
      f.setSize(12);
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTMAINT_MATERIAL_ORDER_NO: Order No");

      f = itemblk3.addField("MCHCODE");
      f.setSize(13);
      f.setHidden();
      f.setReadOnly();
      f.setMaxLength(2000);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTMCHCODE: Object ID");
      f.setFunction("WORK_ORDER_API.Get_Mch_Code(:WO_NO)");
      f.setUpperCase();

      f = itemblk3.addField("ITEM3DESCRIPTION");
      f.setSize(30);
      f.setReadOnly();
      f.setMaxLength(2000);
      f.setHidden();
      f.setFunction("''");

      f = itemblk3.addField("SIGNATURE");
      f.setSize(8);
      f.setMaxLength(2000);
      f.setCustomValidation("SIGNATURE,ITEM3_COMPANY","SIGNATURE_ID,SIGNATURENAME");
      f.setDynamicLOV("EMPLOYEE_LOV","ITEM3_COMPANY COMPANY",600,445);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSIGNATURE: Signature");
      f.setUpperCase();

      f = itemblk3.addField("SIGNATURENAME");
      f.setSize(15);
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setFunction("EMPLOYEE_API.Get_Employee_Info(Site_API.Get_Company(:ITEM3_CONTRACT),:SIGNATURE_ID)");
      f.setMaxLength(2000);

      f = itemblk3.addField("ITEM3_CONTRACT");
      f.setSize(10);
      f.setReadOnly();
      f.setDbName("CONTRACT");
      f.setMaxLength(5);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCONTRACT: Site");
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
      f.setUpperCase();
      f.setInsertable();
      f.setMandatory();

      f = itemblk3.addField("ENTERED","Date");
      f.setSize(22);
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTENTERED: Entered");

      f = itemblk3.addField("INT_DESTINATION_ID");
      f.setSize(8);
      f.setMaxLength(30);
      f.setUpperCase();
      f.setCustomValidation("INT_DESTINATION_ID,ITEM3_CONTRACT","INT_DESTINATION_DESC");
      f.setDynamicLOV("INTERNAL_DESTINATION_LOV","ITEM3_CONTRACT CONTRACT",600,445);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTINT_DESTINATION_ID: Int Destination");

      f = itemblk3.addField("INT_DESTINATION_DESC");
      f.setSize(15);
      f.setDefaultNotVisible();
      f.setMaxLength(2000);

      f = itemblk3.addField("DUE_DATE","Date");
      f.setSize(22);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTDUE_DATE: Due Date");

      f = itemblk3.addField("ITEM3_STATE");
      f.setSize(10);
      f.setDbName("STATE");
      f.setReadOnly();
      f.setMaxLength(2000);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSTATE: Status");

      f = itemblk3.addField("NREQUISITIONVALUE", "Number");
      f.setHidden();
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTNREQUISITIONVALUE: Total Value");
      f.setFunction("''");

      f = itemblk3.addField("SNOTETEXT");
      f.setSize(15);
      f.setHidden();
      f.setMaxLength(2000);
      f.setFunction("''");

      f = itemblk3.addField("SIGNATURE_ID");
      f.setSize(6);
      f.setReadOnly();
      f.setInsertable();
      f.setMaxLength(11);
      f.setHidden();
      f.setUpperCase();

      f = itemblk3.addField("NNOTEID", "Number");
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
      f.setMaxLength(20);
      f.setFunction("Site_API.Get_Company(:ITEM3_CONTRACT)");
      f.setUpperCase();

      f = itemblk3.addField("NPREACCOUNTINGID", "Number");
      f.setHidden();
      f.setReadOnly();
      f.setMaxLength(10);
      f.setFunction("Active_Work_Order_API.Get_Pre_Accounting_Id(:ITEM3_WO_NO)");

      f = itemblk3.addField("SYS_DATE","Date");
      f.setFunction("''");
      f.setHidden();

      f = itemblk3.addField("ITEM3_ACTIVITY_SEQ","Number");
      f.setLabel("ACTSEP2SMITEM3ACTSEQ: Activity Seq");
      f.setDbName("ACTIVITY_SEQ");
      f.setHidden();

      f = itemblk3.addField("MUL_REQ_LINE");
      f.setReadOnly();
      f.setFunction("Maint_Material_Requisition_API.Multiple_Mat_Req_Exist(:ITEM3_WO_NO)");
      f.setLabel("ACTSEP2SMITEM3MULMATREQEXIST: Multiple Material Requisitions Exist"); 
      f.setCheckBox("FALSE,TRUE");
      f.setDefaultNotVisible();

      f = itemblk3.addField("ORDER_NO","Number");
      f.setFunction("''");
      f.setReadOnly();
      f.setHidden();

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
      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      itemtbl3.enableRowSelect();
      // 040123  ARWILK  End  (Enable Multirow RMB actions)

      itembar3 = mgr.newASPCommandBar(itemblk3);
      itembar3.addCustomCommand("plan", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPLANCONS: Plan"));
      itembar3.addCustomCommand("release", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTRELEA: Release"));
      itembar3.addCustomCommand("close", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCLOS: Close"));
      itembar3.addCustomCommandSeparator();
      itembar3.addCustomCommand("printPicList", mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPICLSTMAT: Pick List For Material Requistion - Printout..."));

      itembar3.enableCommand(itembar3.FIND);
      itembar3.defineCommand(itembar3.DELETE,"deleteItem");
      itembar3.defineCommand(itembar3.NEWROW, "newRowITEM3");
      itembar3.defineCommand(itembar3.COUNTFIND, "countFindITEM3");
      itembar3.defineCommand(itembar3.OKFIND, "okFindITEM3");
      itembar3.defineCommand(itembar3.SAVERETURN, "saveReturnItem", "checkItem3Fields()");
      itembar3.defineCommand(itembar3.SAVENEW, null, "checkItem3Fields()");
      itembar3.defineCommand(itembar3.BACK, "backITEM");

      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      itembar3.addCommandValidConditions("plan",         "OBJSTATE",               "Enable",    "Released");
      itembar3.addCommandValidConditions("release",      "OBJSTATE",               "Enable",    "Planned");
      itembar3.appendCommandValidConditions("release",   "WO_STATUS",              "Enable",    "RELEASED;STARTED;WORKDONE;PREPARED;UNDERPREPARATION");
      itembar3.addCommandValidConditions("close",        "OBJSTATE",               "Enable",    "Planned;Released");

      itembar3.addCommandValidConditions("printPicList", "MAINT_MATERIAL_ORDER_NO", "Disable",  "null");

      itembar3.enableMultirowAction();
      // 040123  ARWILK  End  (Enable Multirow RMB actions)

      itemlay3 = itemblk3.getASPBlockLayout();
      itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);
      itemlay3.setSimple("INT_DESTINATION_DESC");
      itemlay3.setSimple("SIGNATURENAME");

      //--------------------------------------------------------------------------------------------------------
      //--------------------------------------------------------------------------------------------------------
      //--------------------------------------------------------------------------------------------------------

      itemblk = mgr.newASPBlock("ITEM6");

      f = itemblk.addField("ITEM_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk.addField("ITEM_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk.addField("LINE_ITEM_NO","Number");
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTLINE_ITEM_NO: Line No");

      f = itemblk.addField("PART_NO");
      f.setSize(14);
      f.setReadOnly();
      f.setInsertable();
      f.setMaxLength(25);
      f.setHyperlink("../invenw/InventoryPart.page","PART_NO","NEWWIN"); 
      f.setCustomValidation("PART_NO,SPARE_CONTRACT,ITEM_CATALOG_CONTRACT,OWNER,CONDITION_CODE,SUPPLY_CODE,ITEM0_MAINT_MATERIAL_ORDER_NO,PART_OWNERSHIP,ITEM_WO_NO","ITEM_CATALOG_NO,ITEMCATALOGDESC,SCODEA,SCODEB,SCODEC,SCODED,SCODEE,SCODEF,SCODEG,SCODEH,SCODEI,SCODEJ,CONDITION_CODE,CONDDESC,QTY_AVAILABLE,ACTIVEIND_DB,PART_OWNERSHIP,PART_OWNERSHIP_DB,OWNER"); 
      f.setDynamicLOV("INVENTORY_PART_WO_LOV","SPARE_CONTRACT CONTRACT",600,445);
      f.setMandatory();
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPART_NO: Part No");
      f.setUpperCase();

      f = itemblk.addField("SPAREDESCRIPTION");
      f.setSize(20);
      f.setReadOnly();
      f.setMaxLength(2000);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSPAREDESCRIPTION: Part Description");
      f.setFunction("INVENTORY_PART_API.Get_Description(:SPARE_CONTRACT,:PART_NO)");

      // 031008  ARWILK  Begin (Bug#106197)
      f = itemblk.addField("CONDITION_CODE");
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCONDCODE: Condition Code");
      f.setSize(15);
      f.setDynamicLOV("CONDITION_CODE",600,445);
      f.setUpperCase(); 
      f.setCustomValidation("CONDITION_CODE,PART_OWNERSHIP_DB,SPARE_CONTRACT,PART_NO,CONFIG_ID,PLAN_QTY,OWNER,SUPPLY_CODE,ITEM0_MAINT_MATERIAL_ORDER_NO,ITEM3_ACTIVITY_SEQ,PART_OWNERSHIP","CONDDESC,ITEM_COST,AMOUNTCOST,QTYONHAND,QTY_AVAILABLE");   

      f = itemblk.addField("CONDDESC");
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCONDDESC: Condition Code Description");
      f.setSize(20);
      f.setMaxLength(50);
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setFunction("CONDITION_CODE_API.Get_Description(:CONDITION_CODE)");
      // 031008  ARWILK  Begin (Bug#106197)		

      f = itemblk.addField("CONFIG_ID");
      f.setFunction("''");
      f.setHidden();

      f = itemblk.addField("PART_OWNERSHIP");
      f.setSize(25);
      f.setInsertable();
      f.setSelectBox();
      f.enumerateValues("PART_OWNERSHIP_API");
      f.setLabel("PCMWACTIVESEPARATE2PARTOWNERSHIP: Ownership"); 
      f.setCustomValidation("PART_OWNERSHIP,PART_NO,SPARE_CONTRACT,OWNER,CONDITION_CODE,SUPPLY_CODE,ITEM0_MAINT_MATERIAL_ORDER_NO","PART_OWNERSHIP_DB,QTYONHAND,QTY_AVAILABLE"); 

      f = itemblk.addField("PART_OWNERSHIP_DB");
      f.setSize(20);
      f.setHidden();

      f = itemblk.addField("OWNER");
      f.setSize(15);
      f.setMaxLength(20);
      f.setInsertable();
      f.setLabel("PCMWACTIVESEPARATE2PARTOWNER: Owner"); 
      f.setCustomValidation("OWNER,PART_OWNERSHIP_DB,CONDITION_CODE,PART_NO,SPARE_CONTRACT,SUPPLY_CODE,ITEM0_MAINT_MATERIAL_ORDER_NO,PART_OWNERSHIP","OWNER_NAME,WO_CUST,QTYONHAND,QTY_AVAILABLE");     
      f.setDynamicLOV("CUSTOMER_INFO");
      f.setUpperCase();

      f = itemblk.addField("WO_CUST");
      f.setSize(20);
      f.setHidden();
      f.setFunction("ACTIVE_SEPARATE_API.Get_Customer_No(:ITEM3_WO_NO)");

      f = itemblk.addField("OWNER_NAME");
      f.setSize(20);
      f.setMaxLength(100);
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE2PARTOWNERNAME: Owner Name");
      f.setFunction("CUSTOMER_INFO_API.Get_Name(:OWNER)");

      f = itemblk.addField("SPARE_CONTRACT");
      f.setSize(11);
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setMaxLength(5);
      f.setMandatory();
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSPARE_CONTRACT: Site");
      f.setUpperCase();

      f = itemblk.addField("HASSPARESTRUCTURE");
      f.setSize(8);
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setMaxLength(2000);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTHASSPARESTRUCTURE: Structure");
      f.setFunction("Equipment_Spare_Structure_API.Has_Spare_Structure_Boolean(:PART_NO,:SPARE_CONTRACT)");

      f = itemblk.addField("ITEM_JOB_ID", "Number");
      f.setDbName("JOB_ID");
      f.setInsertable();
      f.setDefaultNotVisible();
      f.setDynamicLOV("WORK_ORDER_JOB", "ITEM_WO_NO WO_NO");
      f.setLabel("PCMWACTIVESEPARATE2SMITEMJOBID: Job Id");

      f = itemblk.addField("CRAFT_LINE_NO","Number", "#");
      f.setDynamicLOV("WORK_ORDER_ROLE","WO_NO",600,445);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCRFTLINNO: Operation No");

      f = itemblk.addField("DIMQTY");
      f.setSize(11);
      f.setReadOnly();
      f.setMaxLength(2000);
      f.setDefaultNotVisible();   
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTDIMQTY: Dimension/ Quality");
      f.setFunction("INVENTORY_PART_API.Get_Dim_Quality(:SPARE_CONTRACT,:PART_NO)");

      f = itemblk.addField("TYPEDESIGN");
      f.setSize(15);
      f.setReadOnly();
      f.setMaxLength(2000);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTTYPEDESIGN: Type Designation");
      f.setFunction("INVENTORY_PART_API.Get_Type_Designation(:SPARE_CONTRACT,:PART_NO)");

      f = itemblk.addField("DATE_REQUIRED","Date");
      f.setSize(22);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTDATE_REQUIRED: Date Required");

      f = itemblk.addField("SUPPLY_CODE");
      f.setSize(25);
      f.setMandatory();
      f.setInsertable();
      f.setSelectBox();
      f.setCustomValidation("PART_NO,SPARE_CONTRACT,ITEM_CATALOG_CONTRACT,OWNER,CONDITION_CODE,SUPPLY_CODE,ITEM0_MAINT_MATERIAL_ORDER_NO,ITEM3_ACTIVITY_SEQ","QTYONHAND,QTY_AVAILABLE");
      f.enumerateValues("MAINT_MAT_REQ_SUP_API");
      f.setLabel("PCMWACTIVESEPARATE2SMSUPPLYCODE: Supply Code");
      f.setMaxLength(200);

      f = itemblk.addField("PLAN_QTY","Number");
      f.setCustomValidation("PLAN_QTY,PART_NO,SPARE_CONTRACT,ITEM_CATALOG_NO,ITEM_CATALOG_CONTRACT,ITEM_PRICE_LIST_NO,ITEM_PLAN_LINE_NO,ITEM_DISCOUNT,ITEM_WO_NO,ITEM_COST,LIST_PRICE,ITEMDISCOUNTEDPRICE,CONDITION_CODE,CONFIG_ID,PART_OWNERSHIP_DB","ITEM_COST,AMOUNTCOST,ITEM_PRICE_LIST_NO,ITEM_DISCOUNT,LIST_PRICE,ITEMSALESPRICEAMOUNT,ITEMDISCOUNTEDPRICE");
      f.setMandatory();
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPLAN_QTY: Quantity Required");

      f = itemblk.addField("QTY","Number");
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTQTY: Quantity Issued");

      f = itemblk.addField("QTY_SHORT","Number");
      f.setMandatory();
      f.setHidden();
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTQTY_SHORT: Quantity Short");

      f = itemblk.addField("QTYONHAND","Number");
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTQTYONHAND: Quantity on Hand");
      if (mgr.isModuleInstalled("INVENT"))
         f.setFunction("Maint_Material_Req_Line_Api.Get_Qty_On_Hand(:ITEM0_MAINT_MATERIAL_ORDER_NO,:LINE_ITEM_NO)");
      else
         f.setFunction("''"); 

      //Bug 76767, Start, Modified the function call
      f = itemblk.addField("QTY_AVAILABLE","Number");
      f.setSize(17);
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTQTY_AVAILABLE: Quantity Available");
      if (mgr.isModuleInstalled("INVENT"))
          f.setFunction("MAINT_MATERIAL_REQ_LINE_API.Get_Qty_Available(:ITEM0_MAINT_MATERIAL_ORDER_NO,:LINE_ITEM_NO)");
      else
         f.setFunction("''"); 
      //Bug 76767, End

      f = itemblk.addField("QTY_ASSIGNED","Number");
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTQTY_ASSIGNED: Quantity Assigned");

      f = itemblk.addField("QTY_RETURNED","Number");
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setMandatory();
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTQTY_RETURNED: Quantity Returned");

      f = itemblk.addField("UNITMEAS");
      f.setSize(11);
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setMaxLength(2000);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTUNITMEAS: Unit");
      f.setFunction("Purchase_Part_Supplier_API.Get_Unit_Meas(:SPARE_CONTRACT,:PART_NO)");

      f = itemblk.addField("ITEM_CATALOG_CONTRACT");
      f.setSize(10);
      f.setDbName("CATALOG_CONTRACT");
      f.setMaxLength(5);
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCATALOG_CONTRACT: Sales Part Site");
      f.setUpperCase();

      f = itemblk.addField("ITEM_CATALOG_NO");
      f.setSize(9);
      f.setDbName("CATALOG_NO");
      f.setMaxLength(25);
      f.setDefaultNotVisible();
      f.setCustomValidation("ITEM_CATALOG_NO,ITEM_WO_NO,ITEM_CATALOG_CONTRACT,ITEM_PRICE_LIST_NO,PLAN_QTY,ITEM_PLAN_LINE_NO,PART_NO,SPARE_CONTRACT,ITEM_COST,PLAN_QTY,ITEM_DISCOUNT,CURRENCEY_CODE,LIST_PRICE,CONDITION_CODE,CONFIG_ID,PART_OWNERSHIP_DB","LIST_PRICE,ITEM_COST,AMOUNTCOST,ITEMCATALOGDESC,ITEM_DISCOUNT,ITEMSALESPRICEAMOUNT,ITEMDISCOUNTEDPRICE,ITEM_PRICE_LIST_NO");
      f.setDynamicLOV("SALES_PART_ACTIVE_LOV","ITEM_CATALOG_CONTRACT CONTRACT",600,445);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCATALOG_NO: Sales Part Number");
      f.setUpperCase();

      f = itemblk.addField("ITEMCATALOGDESC");
      f.setSize(17);
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setMaxLength(2000);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCATALOGDESC: Sales Part Description");
      if (mgr.isModuleInstalled("ORDER"))
         f.setFunction("SALES_PART_API.Get_Catalog_Desc(:CATALOG_CONTRACT,:CATALOG_NO)");
      else
         f.setFunction("''");

      f = itemblk.addField("ITEM_PRICE_LIST_NO");
      f.setSize(10);
      f.setMaxLength(10);
      f.setDbName("PRICE_LIST_NO");
      f.setDefaultNotVisible();
      f.setCustomValidation("ITEM_PRICE_LIST_NO,SPARE_CONTRACT,PART_NO,ITEM_COST,PLAN_QTY,ITEM_CATALOG_NO,PLAN_QTY,ITEM_WO_NO,ITEM_PLAN_LINE_NO,ITEM_DISCOUNT,ITEM_CATALOG_CONTRACT,CONDITION_CODE,CONFIG_ID,PART_OWNERSHIP_DB","ITEM_COST,AMOUNTCOST,LIST_PRICE,ITEM_DISCOUNT,ITEMSALESPRICEAMOUNT,ITEMDISCOUNTEDPRICE");
      f.setDynamicLOV("SALES_PRICE_LIST","SALES_PRICE_GROUP_ID,CURRENCY_CODE",600,445);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPRICE_LIST_NO: Price List No");
      f.setUpperCase();

      f = itemblk.addField("SALES_PRICE_GROUP_ID");
      f.setHidden();
      if (mgr.isModuleInstalled("ORDER"))
         f.setFunction("SALES_PART_API.GET_SALES_PRICE_GROUP_ID(:CATALOG_CONTRACT,:CATALOG_NO)");
      else
         f.setFunction("''");

      f = itemblk.addField("CURRENCY_CODE");
      f.setHidden();
      f.setFunction("ACTIVE_SEPARATE_API.GET_CURRENCY_CODE(:ITEM_WO_NO)");

      f = itemblk.addField("LIST_PRICE","Money");
      f.setDefaultNotVisible();
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTLIST_PRICE: Sales Price");
      f.setReadOnly();

      f = itemblk.addField("ITEM_DISCOUNT","Number");
      f.setDefaultNotVisible();
      f.setDbName("DISCOUNT");
      f.setCustomValidation("ITEM_DISCOUNT,LIST_PRICE,PLAN_QTY","ITEMSALESPRICEAMOUNT,ITEMDISCOUNTEDPRICE");
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEMDISCOUNT: Discount %");

      f = itemblk.addField("CURRENCEY_CODE");
      f.setHidden();
      f.setFunction("ACTIVE_SEPARATE_API.Get_Currency_Code(:ITEM_WO_NO)");

      f = itemblk.addField("ITEMDISCOUNTEDPRICE", "Money");
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEMDISCOUNTEDPRICE: Discounted Price");
      f.setFunction(":LIST_PRICE - (NVL(:DISCOUNT, 0)/100*:LIST_PRICE)");

      f = itemblk.addField("ITEMSALESPRICEAMOUNT", "Money");
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSALESPRICEAMOUNT: Price Amount");
      f.setFunction("''");

      f = itemblk.addField("ITEM_COST", "Money");
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCOST: Cost");
      f.setFunction("''");

      f = itemblk.addField("AMOUNTCOST", "Money");
      f.setDefaultNotVisible();
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTAMOUNTCOST: Cost Amount");
      f.setFunction("''");
      f.setReadOnly();

      f = itemblk.addField("SCODEA");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSCODEA: Account");
      f.setFunction("''");
      f.setReadOnly();
      f.setHidden();
      f.setDefaultNotVisible();
      f.setMaxLength(10);

      f = itemblk.addField("SCODEB");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSCODEB: Cost Center");
      f.setFunction("''");
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setHidden();
      f.setMaxLength(10);

      f = itemblk.addField("SCODEF");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSCODEF: Project No");
      f.setFunction("''");
      f.setReadOnly();
      f.setHidden();
      f.setDefaultNotVisible();
      f.setMaxLength(10);

      f = itemblk.addField("SCODEE");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSCODEE: Object No");
      f.setFunction("''");
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setHidden();
      f.setMaxLength(10);

      f = itemblk.addField("SCODEC");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSCODEC: Code C");
      f.setFunction("''");
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setHidden();
      f.setMaxLength(10);

      f = itemblk.addField("SCODED");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSCODED: Code D");
      f.setFunction("''");
      f.setReadOnly();
      f.setHidden();
      f.setDefaultNotVisible();
      f.setMaxLength(10);

      f = itemblk.addField("SCODEG");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSCODEG: Code G");
      f.setFunction("''");
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setHidden();
      f.setMaxLength(10);

      f = itemblk.addField("SCODEH");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSCODEH: Code H");
      f.setFunction("''");
      f.setReadOnly();
      f.setHidden();
      f.setMaxLength(10);

      f = itemblk.addField("SCODEI");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSCODEI: Code I");
      f.setFunction("''");
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setHidden();
      f.setMaxLength(10);

      f = itemblk.addField("SCODEJ");
      f.setSize(11);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSCODEJ: Code J");
      f.setFunction("''");
      f.setReadOnly();
      f.setHidden();
      f.setDefaultNotVisible();
      f.setMaxLength(10);

      f = itemblk.addField("ITEM_WO_NO","Number","#");
      f.setMandatory();
      f.setHidden();
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setMaxLength(8);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM0_WO_NO: Work Order No");
      f.setDbName("WO_NO");

      f = itemblk.addField("ITEM_PLAN_LINE_NO","Number");
      f.setReadOnly();
      f.setHidden();
      f.setDbName("PLAN_LINE_NO");
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPLAN_LINE_NO: Plan Line No");

      f = itemblk.addField("ITEM0_MAINT_MATERIAL_ORDER_NO","Number","#");
      f.setHidden();
      f.setReadOnly();
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM0_MAINT_MATERIAL_ORDER_NO: Mat Req Order No");
      f.setDbName("MAINT_MATERIAL_ORDER_NO");

      f = itemblk.addField("BASE_PRICE","Money");
      f.setHidden();
      f.setFunction("''");

      f = itemblk.addField("SALE_PRICE","Money");
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

      f = itemblk.addField("SUPPLY_CODE_DB");
      f.setHidden();

      f = itemblk.addField("COND_CODE_USAGE");
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

      f = itemblk.addField("OBJ_LOAN");
      f.setFunction("''");
      f.setHidden();

      f = itemblk.addField("SPARE_PART_EXIST", "Number");
      f.setFunction("Equipment_Object_Spare_Api.Check_Exist(Active_Work_Order_API.Get_Mch_Code(:ITEM_WO_NO),:SPARE_CONTRACT,:PART_NO,Active_Work_Order_API.Get_Mch_Code_Contract(:ITEM_WO_NO))");
      f.setHidden();

      f = itemblk.addField("DUMMY_ACT_QTY_ISSUED","Number");
      f.setFunction("''");
      f.setHidden();

      itemblk.setView("MAINT_MATERIAL_REQ_LINE");
      itemblk.defineCommand("MAINT_MATERIAL_REQ_LINE_API","New__,Modify__,Remove__");
      itemblk.setMasterBlock(itemblk3);

      itemset = itemblk.getASPRowSet();

      itemtbl = mgr.newASPTable(itemblk);
      itemtbl.setWrap();
      // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
      itemtbl.enableRowSelect();
      // 040123  ARWILK  End  (Enable Multirow RMB actions)

      itembar = mgr.newASPCommandBar(itemblk);
      itembar.enableCommand(itembar.FIND);
      itembar.defineCommand(itembar.NEWROW,"newRowITEM6");
      itembar.defineCommand(itembar.COUNTFIND,"countFindITEM6");
      itembar.defineCommand(itembar.OKFIND,"okFindITEM6");
      itembar.defineCommand(itembar.DUPLICATEROW,"duplicateITEM6");
      itembar.defineCommand(itembar.SAVENEW,null,"checkItem6Owner()");
      itembar.defineCommand(itembar.DELETE,"deleteITEM6");

      // 031204  ARWILK  Begin  (Replace State links with RMB's)
      itembar.addCustomCommand("sparePartObject",mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSPAREPARTOBJ: Spare Parts in Object..."));
      itembar.addCustomCommand("updateSparePartObject", mgr.translate("PCMWACTIVESEPARATE2UPDATESPRPARTS: Update Spare Parts in Object..."));
      itembar.addCustomCommand("objStructure",mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTOBJSTRUCT: Object Structure..."));
      // 031204  ARWILK  End  (Replace State links with RMB's)
      itembar.addCustomCommand("detchedPartList",mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSPAREINDETACH: Spare Parts in Detached Part List..."));
      itembar.addCustomCommandSeparator();
      itembar.addCustomCommand("reserve",mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTRESERV: Reserve"));
      itembar.addCustomCommand("manReserve",mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTRESERVMAN: Reserve manually..."));
      if (mgr.isPresentationObjectInstalled("invenw/InventoryPartInStockOvw.page"))
         itembar.addCustomCommand("availtoreserve",mgr.translate("PCMWACTIVESEPARATE2SMAVAILTORESERVE: Inventory Part in Stock..."));
      itembar.addCustomCommand("unreserve",mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTUNRESERV: Unreserve..."));
      itembar.addCustomCommand("issue",mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTISSUE: Issue"));
      itembar.addCustomCommand("manIssue",mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTISSUEMAN: Issue manually..."));
      itembar.addCustomCommandSeparator();
      itembar.addCustomCommand("availDetail",mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTINVAVAILPLAN: Query - Inventory Part Availability Planning..."));
      itembar.addCustomCommand("supPerPart",mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSUPFORPART: Supplier per Part..."));
      itembar.addCustomCommand("matReqUnissue",mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTMATREQUNISSU: Material Requisition Unissue..."));

      // 031224  ARWILK  Begin  (Links with multirow RMB's)
      itembar.enableMultirowAction();
      itembar.removeFromMultirowAction("manReserve");
      itembar.removeFromMultirowAction("unreserve");
      itembar.removeFromMultirowAction("manIssue");
      itembar.removeFromMultirowAction("matReqUnissue");
      // 031224  ARWILK  End  (Links with multirow RMB's)

      itembar.forceEnableMultiActionCommand("sparePartObject");
      itembar.forceEnableMultiActionCommand("objStructure");
      itembar.forceEnableMultiActionCommand("detchedPartList");

      itembar.addCommandValidConditions("updateSparePartObject","SPARE_PART_EXIST","Enable","0");

      itemlay = itemblk.getASPBlockLayout();
      itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);

      //-----------------------------------------------------------------------
      //-------------- This part belongs to BUDGET ---------------------
      //-----------------------------------------------------------------------

      itemblk4 = mgr.newASPBlock("ITEM4");

      itemblk4.addField("ITEM4_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk4.addField("ITEM4_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      itemblk4.addField("ITEM4_WO_NO","Number","#").
      setDbName("WO_NO").
      setHidden().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTWNO: WO Number");

      itemblk4.addField("WORK_ORDER_COST_TYPE").
      setSize(14).
      setMandatory().
      setReadOnly().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTWORKORDERCOSTTYPE: Work Order Cost Type");

      itemblk4.addField("BUDGET_COST","Money").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTBUDGETCOST: Budget Cost");

      itemblk4.addField("BUDGET_REVENUE","Money").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTBUDGETREVENUE: Budget Revenue");

      itemblk4.addField("NBUDGETMARGIN", "Money").
      setReadOnly().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTNBUDGETMARGN: Budget Margin").
      setFunction("''");

      itemblk4.addField("NPLANNEDCOST","Money").
      setReadOnly().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTNPLANNDCOST: Planned Cost").
      setFunction("WORK_ORDER_BUDGET_API.Get_Planned_Cost(:WO_NO,:WORK_ORDER_COST_TYPE)");

      itemblk4.addField("NPLANNEDREVENUE","Money").
      setReadOnly().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTNPLANNEDREVEN: Planned Revenue").
      setFunction("WORK_ORDER_BUDGET_API.Get_Planned_Revenue(:WO_NO,:WORK_ORDER_COST_TYPE)");

      itemblk4.addField("NPLANNEDMARGIN", "Money").
      setReadOnly().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTNPLANNEDMAR: Planned Margin").
      setFunction("''");

      itemblk4.addField("NACTUALCOST","Money").
      setReadOnly().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTNACTUCOST: Actual Cost").
      setFunction("WORK_ORDER_BUDGET_API.Get_Actual_Cost(:WO_NO,:WORK_ORDER_COST_TYPE)");

      itemblk4.addField("NACTUALREVENUE", "Money").
      setReadOnly().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTNACTUREVENUE: Actual Revenue").
      setFunction("''");

      itemblk4.addField("NACTUALMARGIN", "Money").
      setReadOnly().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTNACTMARGIN: Actual Margin").
      setFunction("''");

      itemblk4.setView("WORK_ORDER_BUDGET");
      itemblk4.defineCommand("WORK_ORDER_BUDGET_API","Modify__");
      itemset4 = itemblk4.getASPRowSet();
      itemblk4.setMasterBlock(headblk);

      itembar4 = mgr.newASPCommandBar(itemblk4);
      itembar4.defineCommand(itembar4.EDITROW, "editItem4");
      itembar4.defineCommand(itembar4.SAVERETURN, "saveReturnITEM4");

      //itembar4.addCommandValidConditions("editItem4","WORK_ORDER_COST_TYPE","Disable","Summary");

      itemtbl4 = mgr.newASPTable(itemblk4);
      itemtbl4.setWrap();

      itemlay4 = itemblk4.getASPBlockLayout();
      itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT);
      itemlay4.setDialogColumns(4);

      //-----------------------------------------------------------------------
      //-------------------  This part belongs to Planning  -------------------
      //-----------------------------------------------------------------------
      itemblk5 = mgr.newASPBlock("ITEM5");

      itemblk5.addField("ITEM5_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk5.addField("ITEM5_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      itemblk5.addField("ITEM5_WO_NO").
      setSize(8).
      setDbName("WO_NO").
      setMandatory(). 
      setHidden().
      setReadOnly().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTWONO: WO No");

      itemblk5.addField("PLAN_LINE_NO","Number").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPLANLINENO: Plan Line No");

      itemblk5.addField("ITEM5_WORK_ORDER_COST_TYPE").
      setSize(8).
      setMandatory().
      setSelectBox().
      setDbName("WORK_ORDER_COST_TYPE").
      enumerateValues("WORK_ORDER_COST_TYPE_API").
      setCustomValidation("ITEM5_WORK_ORDER_COST_TYPE,WORK_ORDER_INVOICE_TYPE,ITEM5COST","ITEM5COST,CLIENTVAL1,CLIENTVAL0,CLIENTVAL4,WORK_ORDER_INVOICE_TYPE,CLIENTVAL4").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTWRKORDCOSTTYPE5: Work Order Cost Type");

      itemblk5.addField("DATE_CREATED","Date").
      setSize(22).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTDATECREATED: Date Created");

      itemblk5.addField("PLAN_DATE","Date").
      setSize(22).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPLANDATE: Plan Date");

      itemblk5.addField("QUANTITY","Number").
      setCustomValidation("ITEM5_CATALOG_NO,ITEM5_CATALOG_CONTRACT,ORG_CODE,QUANTITY,WORK_ORDER_INVOICE_TYPE,ITEM5_DISCOUNT,ITEM5_SALES_PRICE,QTY_TO_INVOICE,ITEM5COST","ITEM5_SALES_PRICE_AMOUNT,ITEM5_COST_AMOUNT,ITEM5COST").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTQUANTITY: Quantity");

      itemblk5.addField("QTY_TO_INVOICE","Number").
      setCustomValidation("ITEM5_SALES_PRICE,ITEM5_DISCOUNT,WORK_ORDER_INVOICE_TYPE,QUANTITY,QTY_TO_INVOICE,ITEM5_CATALOG_CONTRACT,ITEM5_CATALOG_NO,ITEM5_PRICE_LIST_NO,CUSTOMER_NO,AGREEMENT_ID,ORG_CODE","ITEM5_PRICE_LIST_NO,ITEM5_SALES_PRICE,ITEM5_SALES_PRICE_AMOUNT,ITEM5_DISCOUNT").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTQTYTOINVOICE: Qty To Invoice");

      itemblk5.addField("ITEM5_CATALOG_CONTRACT").
      setSize(9).
      setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCATALOGCONTRACT5: Sales Part Site").
      setDbName("CATALOG_CONTRACT").
      setReadOnly().
      setUpperCase();

      itemblk5.addField("ITEM5_CATALOG_NO").
      setSize(10).
      setDbName("CATALOG_NO").
      setCustomValidation("ITEM5_CATALOG_NO,ITEM5_CATALOG_CONTRACT,ORG_CODE,ITEM5_WORK_ORDER_COST_TYPE,QUANTITY,CUSTOMER_NO,AGREEMENT_ID,ITEM5_PRICE_LIST_NO,ITEM5COST,ITEM5_DISCOUNT,QTY_TO_INVOICE,ITEM5_SALES_PRICE,WORK_ORDER_INVOICE_TYPE,HEAD_CURRENCEY_CODE","ITEM5COST,ITEM5_CATALOGDESC,LINE_DESCRIPTION,ITEM5_COST_AMOUNT,ITEM5_PRICE_LIST_NO,ITEM5_SALES_PRICE,ITEM5_DISCOUNT,ITEM5_SALES_PRICE_AMOUNT").
      setUpperCase().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCATALOGNO5: Sales Part Number"); 

      f = itemblk5.addField("ITEM5_CATALOGDESC"); 
      f.setSize(35);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCATALOGDESCR5: Sales Part Description");
      if (mgr.isModuleInstalled("ORDER"))
         f.setFunction("SALES_PART_API.Get_Catalog_Desc(:ITEM5_CATALOG_CONTRACT,:ITEM5_CATALOG_NO)");
      else
         f.setFunction("''");
      f.setReadOnly();
      f.setDefaultNotVisible();
      f.setMaxLength(2000);
      f.setHidden();

      itemblk5.addField("LINE_DESCRIPTION").
      setSize(35).
      setLabel("LINEDESCRIPTION: Sales Part Description").
      setDefaultNotVisible();

      itemblk5.addField("ITEM5COST","Money").
      setCustomValidation("ITEM5COST,QUANTITY,QTY_TO_INVOICE,ITEM5_SALES_PRICE,ITEM5_DISCOUNT,WORK_ORDER_INVOICE_TYPE","ITEM5_SALES_PRICE_AMOUNT,ITEM5_COST_AMOUNT").
      setDbName("COST").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCOST5: Cost");

      itemblk5.addField("ITEM5_COST_AMOUNT","Money").
      setFunction("''").
      setDefaultNotVisible().
      setReadOnly().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTCOS_TAMOUNT5: Cost Amount");

      itemblk5.addField("ITEM5_SALES_PRICE","Money").
      setCustomValidation("QUANTITY,QTY_TO_INVOICE,ITEM5_SALES_PRICE,ITEM5_DISCOUNT,WORK_ORDER_INVOICE_TYPE","ITEM5_SALES_PRICE_AMOUNT").
      setDbName("SALES_PRICE").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSALESPRICE5: Sales Price");

      itemblk5.addField("ITEM5_DISCOUNT","Number").
      setDbName("DISCOUNT").
      setCustomValidation("QUANTITY,QTY_TO_INVOICE,ITEM5_SALES_PRICE,ITEM5_DISCOUNT,WORK_ORDER_INVOICE_TYPE","ITEM5_SALES_PRICE_AMOUNT").
      setDefaultNotVisible().   
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTDISCOUNT5: Discount");

      itemblk5.addField("ITEM5_SALES_PRICE_AMOUNT","Money").
      setFunction("''").
      setDefaultNotVisible().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM5SLSPRICEAMT: Price Amount");

      itemblk5.addField("ITEM5_PRICE_LIST_NO").
      setSize(13).
      setDbName("PRICE_LIST_NO").
      setDynamicLOV("SALES_PRICE_LIST",600,445).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPRICELISTNO5: Price List No").
      setUpperCase();

      itemblk5.addField("ITEM5_JOB_ID", "Number").
      setDbName("JOB_ID").
      setInsertable().
      setDefaultNotVisible().
      setDynamicLOV("WORK_ORDER_JOB", "ITEM5_WO_NO WO_NO").
      setLabel("PCMWACTIVESEPARATE2SMITEM5JOBID: Job Id");

      itemblk5.addField("WORK_ORDER_INVOICE_TYPE").
      setSize(8).
      setMandatory().
      setSelectBox().
      setCustomValidation("QUANTITY,QTY_TO_INVOICE,ITEM5_SALES_PRICE,ITEM5_DISCOUNT,WORK_ORDER_INVOICE_TYPE","CLIENTVAL5,ITEM5_SALES_PRICE_AMOUNT").
      enumerateValues("WORK_ORDER_INVOICE_TYPE_API").
      setDefaultNotVisible().   
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTWOINVTYPE: WO Invoice Type");

      itemblk5.addField("WOINVOICETYPE").
      setFunction("''").
      setHidden();

      itemblk5.addField("CLIENTVAL0").
      setFunction("''").
      setHidden();

      itemblk5.addField("CLIENTVAL1").
      setFunction("''").
      setHidden();

      itemblk5.addField("COSTTYPE2").
      setFunction("''").
      setHidden();

      itemblk5.addField("COSTTYPE3").                
      setFunction("''").
      setHidden();        

      itemblk5.addField("CLIENTVAL4").
      setFunction("''").
      setHidden();

      itemblk5.addField("CLIENTVAL5").
      setFunction("''").
      setHidden();    

      itemblk5.addField("ITEM5_BASE_UNIT_PRICE","Money").
      setFunction("''").
      setHidden();

      itemblk5.addField("ITEM5_SALE_UNIT_PRICE","Money").
      setFunction("''").
      setHidden();

      itemblk5.addField("ITEM5_CURRENCY_RATE").
      setFunction("''").
      setHidden();

      itemblk5.addField("ITEM5_BUY_QTY_DUE").
      setFunction("''").
      setHidden();    

      itemblk5.addField("WORK_ORDER_COST_TYPE_DB").
      setHidden();

      itemblk5.addField("IS_AUTO_LINE","Number").
      setFunction("Work_Order_Planning_API.Is_Auto_External_Line(:ITEM5_WO_NO,:PLAN_LINE_NO)").
      setHidden();

      itemblk5.setView("WORK_ORDER_PLANNING");
      itemblk5.defineCommand("Work_Order_Planning_API","New__,Modify__,Remove__");
      itemset5 = itemblk5.getASPRowSet();

      itemblk5.setMasterBlock(headblk);

      itembar5 = mgr.newASPCommandBar(itemblk5);
      itembar5.enableCommand(itembar5.FIND);   
      itembar5.defineCommand(itembar5.NEWROW,"newRowITEM5");
      itembar5.defineCommand(itembar5.COUNTFIND,"countFindITEM5");
      itembar5.defineCommand(itembar5.OKFIND,"okFindITEM5");   

      itembar5.defineCommand(itembar5.SAVERETURN, "saveReturnItem5", "checkItem5Fields()");
      itembar5.defineCommand(itembar5.SAVENEW, "saveNewItem5", "checkItem5Fields()");

      itemtbl5 = mgr.newASPTable(itemblk5);
      itemtbl5.setWrap();

      itemlay5 = itemblk5.getASPBlockLayout();
      itemlay5.setDefaultLayoutMode(itemlay5.MULTIROW_LAYOUT);
      itemlay5.setDialogColumns(3);       

      //-----------------------------------------------------------------------
      //------------------------------   ITEMBLK7    --------------------------
      //-----------------------------------------------------------------------

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
      setLabel("ACTSEPSMJOBITEM7JOBID: Job ID").
      setReadOnly().
      setInsertable().
      setMandatory();

      itemblk7.addField("STD_JOB_ID").
      setSize(15).
      setLabel("ACTSEPSMJOBITEM7STDJOBID: Standard Job ID").
      setLOV("SeparateStandardJobLov.page", 600, 445).
      setUpperCase().
      setInsertable().
      setQueryable().
      setCustomValidation("STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION", "STD_JOB_ID,STD_JOB_REVISION,DESCRIPTION,STD_JOB_STATUS").
      setMaxLength(12);

      itemblk7.addField("STD_JOB_CONTRACT").
      setSize(10).
      setLabel("ACTSEPSMJOBITEM7STDJOBCONTRACT: Site").
      setUpperCase().
      setReadOnly().
      setMaxLength(5);

      itemblk7.addField("STD_JOB_REVISION").
      setSize(10).
      setLabel("ACTSEPSMJOBITEM7STDJOBREVISION: Revision").
      setLOV("SeparateStandardJobLov.page", 600, 445).
      setCustomValidation("STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION", "STD_JOB_ID,STD_JOB_REVISION,DESCRIPTION,STD_JOB_STATUS").
      setUpperCase().
      setInsertable().
      setQueryable().
      setMaxLength(6);

      itemblk7.addField("DESCRIPTION").
      setSize(35).
      setLabel("ACTSEPSMJOBITEM7DESCRIPTION: Description").
      setMandatory().
      setInsertable().
      setMaxLength(4000);

      itemblk7.addField("ITEM7_QTY", "Number").
      setDbName("QTY").
      setLabel("ACTSEPSMJOBITEM7QTY: Quantity").
      setMandatory().
      setInsertable();

      itemblk7.addField("ITEM7_COMPANY").
      setDbName("COMPANY").
      setSize(20).
      setHidden().
      setUpperCase().
      setInsertable();

      itemblk7.addField("STD_JOB_STATUS").
      setLabel("ACTSEPSMJOBSTDJOBSTATUS: Std Job Status").
      setFunction("Standard_Job_API.Get_Status(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)").
      setReadOnly();

      itemblk7.addField("ITEM7_SIGN_ID").
      setDbName("SIGNATURE").
      setSize(35).
      setLabel("ACTSEPSMJOBITEM7SIGNID: Executed By").
      setQueryable().
      setUpperCase().
      setLOV("../mscomw/MaintEmployeeLov.page","ITEM7_COMPANY COMPANY",600,450).
      setCustomValidation("ITEM7_COMPANY,ITEM7_SIGN_ID","EMPLOYEE_ID,ITEM7_SIGN_ID");

      itemblk7.addField("EMPLOYEE_ID").
      setSize(15).
      setLabel("ACTSEPSMJOBITEM7EMPLOYEEID: Employee ID").
      setUpperCase().
      setMaxLength(11).
      setReadOnly();

      //(+) Bug 66406, Start
      itemblk7.addField("CONN_PM_NO", "Number" ,"#").
      setDbName("PM_NO").
      setSize(15).
      setReadOnly().
      setCustomValidation("CONN_PM_NO,CONN_PM_REVISION","CONN_PM_NO,CONN_PM_REVISION").
      setLabel("ACTSEPSMCONNPMNO: PM No");

      itemblk7.addField("CONN_PM_REVISION").
      setDbName("PM_REVISION").
      setSize(15).
      setReadOnly().
      setLabel("ACTSEPSMCONNPMREV: PM Revision");

      itemblk7.addField("CONN_PM_JOB_ID", "Number").
      setDbName("PM_JOB_ID"). 
      setSize(15).
      setReadOnly().
      setDynamicLOV("PM_ACTION_JOB", "CONN_PM_NO PM_NO, CONN_PM_REVISION PM_REVISION").
      setLabel("ACTSEPSMCONNPMJOBID: PM Job ID");
      //(+) Bug 66406, End

      itemblk7.addField("ITEM7_DATE_FROM", "Datetime").
      setDbName("DATE_FROM").
      setSize(20).
      setLabel("ACTSEPSMJOBITEM7DATEFROM: Date From").
      setInsertable();

      itemblk7.addField("ITEM7_DATE_TO", "Datetime").
      setDbName("DATE_TO").
      setSize(20).
      setLabel("ACTSEPSMJOBITEM7DATETO: Date To").
      setInsertable();

      itemblk7.addField("ITEM7_PLAN_HRS","Number").
      setFunction("Work_Order_Job_API.Get_Total_Plan_Hours(:ITEM7_WO_NO, :JOB_ID)").
      setReadOnly().
      setLabel("PCMWACTIVESEPARATE2SEREMANAGEMENTPHRS: Planned Hours");

      itemblk7.addField("STD_JOB_FLAG", "Number").
      setHidden().
      setCustomValidation("ITEM7_WO_NO,JOB_ID,STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION,ITEM7_DATE_TO,ITEM7_DATE_FROM", "N_JOB_EXIST,S_STD_JOB_EXIST,N_ROLE_EXIST,N_MAT_EXIST,N_TOOL_EXIST,N_PLANNING_EXIST,N_DOC_EXIST,S_STD_JOB_ID,S_STD_JOB_CONTRACT,S_STD_JOB_REVISION,N_QTY,S_AGREEMENT_ID,OUTEXIST,OVERLAPEXIST").
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

      itemblk7.addField("OUTEXIST").
      setFunction("''").
      setHidden();

      itemblk7.addField("OVERLAPEXIST").
      setFunction("''").
      setHidden();

      itemblk7.setView("WORK_ORDER_JOB");
      itemblk7.defineCommand("WORK_ORDER_JOB_API","New__,Modify__,Remove__");
      itemblk7.setMasterBlock(headblk);

      itemset7 = itemblk7.getASPRowSet();

      itemtbl7 = mgr.newASPTable(itemblk7);
      itemtbl7.setTitle(mgr.translate("ACTSEPSMJOBITEM7WOJOBS: Jobs"));
      itemtbl7.setWrap();
      itemtbl7.enableRowSelect();

      itembar7 = mgr.newASPCommandBar(itemblk7);
      itembar7.enableCommand(itembar7.FIND);

      itembar7.defineCommand(itembar7.NEWROW, "newRowITEM7");
      itembar7.defineCommand(itembar7.SAVERETURN, "saveReturnItem7", "checkITEM7SaveParams(i)");
      itembar7.defineCommand(itembar7.COUNTFIND,"countFindITEM7");
      //Bug 89703, Start
      itembar7.defineCommand(itembar7.OKFIND,"okFindITEM7"); 
      //Bug 89703, End
      itembar7.defineCommand(itembar7.DELETE,"deleteRowITEM7");
      itembar7.addCustomCommand("allocateEmployees1",mgr.translate("PCMWACTIVESEPARATE2SERMHTALLOCATEEMPLOYEES1: Allocate Employees..."));
      itembar7.addCustomCommand("selectAllocation1",mgr.translate("PCMWACTIVESEPARATE2SERMHTCLEARALLOCATION1: Clear Allocation..."));
      itembar7.enableMultirowAction();

      itemlay7 = itemblk7.getASPBlockLayout();
      itemlay7.setDefaultLayoutMode(itemlay7.MULTIROW_LAYOUT);


      //-------------------operations block under jobs tab------------------------

      itemblk8 = mgr.newASPBlock("ITEM8");

      itemblk8.addField("ITEM8_OBJID").
      setHidden().
      setDbName("OBJID");

      itemblk8.addField("ITEM8_OBJVERSION").
      setHidden().
      setDbName("OBJVERSION");

      itemblk8.addField("ITEM8_WO_NO","Number","#").
      setMandatory().
      setHidden().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM7WONO: WO No").
      setDbName("WO_NO");

      itemblk8.addField("ITEM8_ROW_NO","Number").
      setDbName("ROW_NO").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8RNO: Operation No").
      setReadOnly().
      setInsertable();

      itemblk8.addField("ITEM8_TEMP").      
      setHidden().
      setFunction("''");

      itemblk8.addField("ITEM8_ORG_CONTRACT").      
      setHidden().
      setFunction("''");

      itemblk8.addField("ITEM8_COMPANY").
      setSize(8).
      setHidden().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8COMPANY: Company").
      setUpperCase().
      setDbName("COMPANY").
      setMaxLength(20);

      itemblk8.addField("ITEM8_DESCRIPTION").
      setSize(27).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8DESCRIPTION: Description").
      setDbName("DESCRIPTION").
      setDefaultNotVisible().
      setMaxLength(60);

      itemblk8.addField("ITEM8_SIGN").
      setSize(10).
      setDbName("SIGN").
      setLOV("../mscomw/MaintEmployeeLov.page","ITEM8_COMPANY COMPANY",600,450).
      setCustomValidation("ITEM8_COMPANY,ITEM8_SIGN,ITEM8_ROLE_CODE,ITEM8_CONTRACT,ITEM8_ORG_CODE,ORG_CODE,ITEM8_COST,ITEM8_CATALOG_NO,ITEM8_PLAN_MEN,ITEM8_PLAN_HRS,ITEM8_WO_NO","ITEM8_SIGN_ID,ITEM8_ROLE_CODE,ITEM8_CONTRACT,ITEM8_ORG_CODE,ITEM8_CATALOG_NO,ITEM8_COST,ITEM8_COSTAMOUNT,ITEM8_CATALOGDESC,ITEM8_SIGN,ITEM8_ALLOCATED_HOURS,ITEM8_ORG_CODE_DESC,ITEM8_ROLE_CODE_DESC").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8SIG: Executed By").
      setUpperCase().
      setMaxLength(20);

      itemblk8.addField("ITEM8_SIGN_ID").
      setSize(18).
      setDbName("SIGN_ID").
      setMaxLength(11).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8SIGID: Employee ID").
      setCustomValidation("ITEM8_SIGN_ID,ITEM8_COMPANY,ITEM8_WO_NO,ITEM8_PLAN_HRS,ITEM8_PLAN_MEN,ITEM8_ROLE_CODE,ITEM8_CONTRACT,ITEM8_ORG_CODE,ITEM8_CATALOG_NO,ITEM8_CATALOG_CONTRACT,ITEM8_CATALOGDESC,ITEM8_PRICE_LIST_NO,ITEM8_LISTPRICE,ITEM8_DISCOUNT,ITEM8_DISCOUNTED_PRICE,ITEM8_SALESPRICEAMOUNT","ITEM8_SIGN,ITEM8_ROLE_CODE,ITEM8_CONTRACT,ITEM8_ORG_CODE,ITEM8_CATALOG_CONTRACT,ITEM8_CATALOG_NO,ITEM8_CATALOGDESC,ITEM8_PRICE_LIST_NO,ITEM8_LISTPRICE,ITEM8_DISCOUNT,ITEM8_DISCOUNTED_PRICE,ITEM8_SALESPRICEAMOUNT").
      setUpperCase().
      setReadOnly();

      itemblk8.addField("ITEM8_ORG_CODE").
      setSize(11).
      setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV", "ITEM8_CONTRACT CONTRACT",600,450).
      setCustomValidation("ITEM8_CONTRACT,ITEM8_ORG_CODE,ITEM8_ROLE_CODE,ITEM8_CATALOG_NO,ITEM8_CATALOG_CONTRACT,ITEM8_CATALOGDESC,ITEM8_COST,ITEM8_COSTAMOUNT,ITEM8_PLAN_HRS,ITEM8_ORG_CODE,ITEM8_PLAN_MEN,ITEM8_SIGN,ITEM8_WO_NO","ITEM8_CATALOG_NO,ITEM8_CATALOGDESC,ITEM8_COST,ITEM8_COSTAMOUNT,ITEM8_ORG_CODE,ITEM8_CONTRACT,ITEM8_ORG_CODE_DESC").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8ORGCODE: Maint.Org.").
      setUpperCase().
      setDbName("ORG_CODE").
      setMaxLength(8);

      //Bug ID 59224,start
      itemblk8.addField("ITEM8_ORG_CODE_DESC").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8ORGCODEDESC: Maint. Org. Description").
      setFunction("Organization_Api.Get_Description(:ITEM8_CONTRACT,:ITEM8_ORG_CODE)").
      setReadOnly();
      itemblk8.addField("ITEM8_CONTRACT").
      setSize(8).
      setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8CONTRACT: Maint.Org. Site").
      setUpperCase().
      setDbName("CONTRACT").
      setMaxLength(5);

      //Bug ID 59224,start

      itemblk8.addField("ITEM8_ROLE_CODE").
      setSize(9).
      setDynamicLOV("ROLE_TO_SITE_LOV","ITEM8_CONTRACT CONTRACT",600,445).
      setCustomValidation("ITEM8_ROLE_CODE,ITEM8_CONTRACT,ITEM8_ORG_CODE,ORG_CODE,ITEM8_COST,ITEM8_CATALOG_NO,ITEM8_PLAN_MEN,ITEM8_PLAN_HRS,ITEM8_SIGN,ITEM8_WO_NO,ITEM8_CATALOG_CONTRACT","ITEM8_CATALOG_NO,ITEM8_COST,ITEM8_COSTAMOUNT,ITEM8_CATALOGDESC,ITEM8_ROLE_CODE_DESC").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8RCODE: Craft ID").
      setUpperCase().
      setDbName("ROLE_CODE").
      setMaxLength(10);

      itemblk8.addField("ITEM8_ROLE_CODE_DESC").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8ROLECODEDESC: Craft Description").
      setFunction("ROLE_API.Get_Description(:ITEM8_ROLE_CODE)").
      setReadOnly();
      //Bug ID 59224,end
      itemblk8.addField("ITEM8_PLAN_MEN","Number").
      setCustomValidation("ITEM8_PLAN_MEN,ITEM8_PLAN_HRS,ITEM8_COST,ITEM8_ROLE_CODE,ITEM8_CONTRACT,ITEM8_ORG_CODE,ORG_CODE,ITEM8_CATALOG_CONTRACT,ITEM8_CATALOG_NO,ITEM8_PRICE_LIST_NO,CUSTOMER_NO,AGREEMENT_ID,ITEM8_LISTPRICE,ITEM8_DISCOUNT,ITEM8_DISCOUNTED_PRICE,ITEM8_WO_NO,ITEM8_TOTAL_ALLOCATED_HOURS","ITEM8_COST,ITEM8_COSTAMOUNT,ITEM8_PRICE_LIST_NO,ITEM8_DISCOUNT,ITEM8_LISTPRICE,ITEM8_SALESPRICEAMOUNT,ITEM8_DISCOUNTED_PRICE,ITEM8_TOTAL_MAN_HOURS,ITEM8_TOTAL_REMAINING_HOURS").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8PMEN: Planned Men").
      setDbName("PLAN_MEN");

      itemblk8.addField("ITEM8_PLAN_HRS","Number").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8PLANHRS: Planned Hours").
      setCustomValidation("ITEM8_PLAN_MEN,ITEM8_PLAN_HRS,ITEM8_COST,ITEM8_ROLE_CODE,ITEM8_CONTRACT,ITEM8_ORG_CODE,ORG_CODE,ITEM8_CATALOG_CONTRACT,ITEM8_CATALOG_NO,ITEM8_PRICE_LIST_NO,CUSTOMER_NO,AGREEMENT_ID,ITEM8_LISTPRICE,ITEM8_DISCOUNT,ITEM8_DISCOUNTED_PRICE,ITEM8_WO_NO,ITEM8_TOTAL_ALLOCATED_HOURS","ITEM8_COST,ITEM8_COSTAMOUNT,ITEM8_PRICE_LIST_NO,ITEM8_DISCOUNT,ITEM8_LISTPRICE,ITEM8_SALESPRICEAMOUNT,ITEM8_DISCOUNTED_PRICE,ITEM8_TOTAL_MAN_HOURS,ITEM8_TOTAL_REMAINING_HOURS").
      setDbName("PLAN_HRS");

      itemblk8.addField("ITEM8_TOTAL_MAN_HOURS","Number").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTMANHRS: Total Man Hours").
      setFunction(":PLAN_MEN * :PLAN_HRS").
      setReadOnly();

      itemblk8.addField("ITEM8_ALLOCATED_HOURS","Number").
      setDbName("ALLOCATED_HOURS").
      setCustomValidation("ITEM8_ALLOCATED_HOURS,ITEM8_TOTAL_MAN_HOURS","ITEM8_TOTAL_ALLOCATED_HOURS,ITEM8_TOTAL_REMAINING_HOURS").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTALLOCATEDHRS: Allocated Hours");

      itemblk8.addField("ITEM8_TOTAL_ALLOCATED_HOURS","Number").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTTOTALLOCATEDHRS: Total Allocated Hours").
      setFunction("Work_Order_Role_API.Get_Total_Alloc(:ITEM8_WO_NO,:ROW_NO)").
      setReadOnly();

      itemblk8.addField("ITEM8_TOTAL_REMAINING_HOURS","Number").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTREMAININGHRS: Total Remaining Hours").
      setFunction("(:PLAN_MEN * :PLAN_HRS) - Work_Order_Role_API.Get_Total_Alloc(:ITEM8_WO_NO,:ROW_NO)").
      setReadOnly();

      itemblk8.addField("ITEM8_TEAM_CONTRACT").
      setSize(7).
      setDynamicLOV("USER_ALLOWED_SITE_LOV","ITEM8_COMPANY COMPANY",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8CONT: Team Site").
      setMaxLength(5).
      setDbName("TEAM_CONTRACT").
      setInsertable().
      setUpperCase();

      itemblk8.addField("ITEM8_TEAM_ID").
      setCustomValidation("ITEM8_TEAM_ID,ITEM8_TEAM_CONTRACT","ITEM8_TEAMDESC").
      setSize(13).
      setDynamicLOV("MAINT_TEAM","ITEM8_TEAM_CONTRACT TEAM_CONTRACT",600,450).
      setDbName("TEAM_ID").
      setMaxLength(20).
      setInsertable().
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8TID: Team ID").
      setUpperCase();

      itemblk8.addField("ITEM8_TEAMDESC").
      setFunction("Maint_Team_API.Get_Description(:ITEM8_TEAM_ID,:ITEM8_TEAM_CONTRACT)").
      setSize(40).
      setMaxLength(200).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8DESC: Description").
      setReadOnly();

      itemblk8.addField("ITEM8_CATALOG_CONTRACT").
      setSize(15).
      setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8CATCONT: Sales Part Site").
      setCustomValidation("ITEM8_PLAN_MEN,ITEM8_PLAN_HRS,ITEM8_COST,ITEM8_ROLE_CODE,ITEM8_CONTRACT,ITEM8_ORG_CODE,ORG_CODE,ITEM8_CATALOG_CONTRACT,ITEM8_CATALOG_NO,ITEM8_PRICE_LIST_NO,CUSTOMER_NO,AGREEMENT_ID,ITEM8_LISTPRICE,ITEM8_DISCOUNT,ITEM8_DISCOUNTED_PRICE,ITEM8_WO_NO","ITEM8_COST,ITEM8_COSTAMOUNT,ITEM8_PRICE_LIST_NO,ITEM8_DISCOUNT,ITEM8_LISTPRICE,ITEM8_SALESPRICEAMOUNT,ITEM8_DISCOUNTED_PRICE").
      setUpperCase().
      setReadOnly().
      setDbName("CATALOG_CONTRACT").
      setMaxLength(5);

      itemblk8.addField("ITEM8_CATALOG_NO").
      setSize(17).
      setDynamicLOV("SALES_PART_SERVICE_LOV","ITEM8_CATALOG_CONTRACT CONTRACT",600,450).
      setCustomValidation("ITEM8_CATALOG_CONTRACT,ITEM8_CATALOG_NO,ITEM8_PRICE_LIST_NO,ITEM8_PLAN_MEN,CUSTOMER_NO,AGREEMENT_ID,ITEM8_CONTRACT,ITEM8_ORG_CODE,ITEM8_PLAN_HRS,ORG_CODE,ITEM8_ROLE_CODE,HEAD_CURRENCEY_CODE,ITEM8_DISCOUNTED_PRICE,ITEM8_ROWSTATUS,ITEM8_WO_NO","ITEM8_CATALOGDESC,ITEM8_COST,ITEM8_COSTAMOUNT,ITEM8_PRICE_LIST_NO,ITEM8_DISCOUNT,ITEM8_LISTPRICE,ITEM8_SALESPRICEAMOUNT,ITEM8_DISCOUNTED_PRICE").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8CANO: Sales Part").
      setUpperCase().
      setDbName("CATALOG_NO").
      setMaxLength(25);

      f = itemblk8.addField("ITEM8_CATALOGDESC");
      f.setSize(25);
      f.setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8CADESCR: Sales Part Description");
      if (mgr.isModuleInstalled("ORDER"))
         f.setFunction("SALES_PART_API.Get_Catalog_Desc(:ITEM8_CATALOG_CONTRACT,:ITEM8_CATALOG_NO)");
      else
         f.setFunction("''");
      f.setReadOnly();
      f.setMaxLength(2000);

      itemblk8.addField("ITEM8_PRICE_LIST_NO").
      setSize(13).
      setDbName("PRICE_LIST_NO").
      setDynamicLOV("SALES_PRICE_LIST",600,450).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8PLSTNO: Price List No").
      setUpperCase().
      setCustomValidation("ITEM8_PLAN_MEN,ITEM8_PLAN_HRS,ITEM8_COST,ITEM8_ROLE_CODE,ITEM8_CONTRACT,ITEM8_ORG_CODE,ORG_CODE,ITEM8_CATALOG_CONTRACT,ITEM8_CATALOG_NO,ITEM8_PRICE_LIST_NO,CUSTOMER_NO,AGREEMENT_ID,ITEM8_WO_NO","ITEM8_COST,ITEM8_COSTAMOUNT,ITEM8_PRICE_LIST_NO,ITEM8_LISTPRICE,ITEM8_SALESPRICEAMOUNT,ITEM8_DISCOUNT,ITEM8_DISCOUNTED_PRICE").      
      setDefaultNotVisible().   
      setMaxLength(10);

      itemblk8.addField("ITEM8_COST","Money","#.##").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8CITEM: Cost").
      setFunction("WORK_ORDER_PLANNING_UTIL_API.Get_Cost(:ORG_CODE,:ITEM8_ROLE_CODE,:ITEM8_CATALOG_CONTRACT,:ITEM8_CATALOG_NO,:ITEM8_CONTRACT)").
      setCustomValidation("ITEM8_COST,ORG_CODE,ITEM8_ROLE_CODE,ITEM8_CONTRACT,ITEM8_ORG_CODE,ITEM8_CATALOG_NO","ITEM8_COST,ITEM8_COSTAMOUNT").
      setDefaultNotVisible().   
      setReadOnly();

      itemblk8.addField("ITEM8_COSTAMOUNT","Money","#.##").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8CAMT: Cost Amount").
      setFunction("NVL(:ITEM8_PLAN_MEN,1)*:ITEM8_PLAN_HRS*(WORK_ORDER_PLANNING_UTIL_API.Get_Cost(:ORG_CODE,:ITEM8_ROLE_CODE,:ITEM8_CATALOG_CONTRACT,:ITEM8_CATALOG_NO,:ITEM8_CONTRACT))").
      setDefaultNotVisible().    
      setReadOnly();

      itemblk8.addField("ITEM8_LISTPRICE", "Money").  
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8LPRICE: Sales Price").
      setDbName("SALES_PRICE").
      setCustomValidation("ITEM8_LISTPRICE,ITEM8_DISCOUNT,ITEM8_PLAN_MEN,ITEM8_PLAN_HRS","ITEM8_DISCOUNTED_PRICE,ITEM8_SALESPRICEAMOUNT").
      setDefaultNotVisible();

      itemblk8.addField("ITEM8_DISCOUNT","Number").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8DISC: Discount").
      setDbName("DISCOUNT").
      setCustomValidation("ITEM8_LISTPRICE,ITEM8_DISCOUNT,ITEM8_PLAN_MEN,ITEM8_PLAN_HRS","ITEM8_DISCOUNTED_PRICE,ITEM8_SALESPRICEAMOUNT");

      itemblk8.addField("ITEM8_DISCOUNTED_PRICE","Money").
      setFunction(":ITEM8_LISTPRICE - (NVL(:ITEM8_DISCOUNT, 0)/100*:ITEM8_LISTPRICE)").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8DISCPRICE: Discounted Price").
      setDefaultNotVisible().    
      setReadOnly();   

      itemblk8.addField("ITEM8_SALESPRICEAMOUNT", "Money").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8SPAMT: Sales Price Amount").
      setFunction("NVL(:ITEM8_PLAN_MEN, 1)*:ITEM8_PLAN_HRS*:ITEM8_LISTPRICE - (NVL(:ITEM8_DISCOUNT, 0)/100*(NVL(:ITEM8_PLAN_MEN, 1)*:ITEM8_PLAN_HRS*:ITEM8_LISTPRICE))").
      setReadOnly();

      itemblk8.addField("ITEM8_JOB_ID", "Number").
      setDbName("JOB_ID").
      setInsertable().
      setDefaultNotVisible().
      setDynamicLOV("WORK_ORDER_JOB", "ITEM8_WO_NO WO_NO").
      setCustomValidation("ITEM8_COMPANY,ITEM8_WO_NO,ITEM8_JOB_ID","ITEM8_SIGN_ID,ITEM8_SIGN").
      setLabel("PCMWACTIVESEPARATE2SMITEM2JOBID: Job Id").
      setHidden();

      itemblk8.addField("ITEM8_DATE_FROM","Datetime").
      setSize(22).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8DFROM: Date from").
      setDefaultNotVisible().   
      setDbName("DATE_FROM").
      setCustomValidation("ITEM8_WO_NO,ITEM8_DATE_FROM","ITEM8_TEMP1,ITEM8_DATE_FROM_MASKED");

      itemblk8.addField("ITEM8_DATE_TO","Datetime").
      setSize(22).
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8DTO: Date to").
      setDefaultNotVisible().   
      setDbName("DATE_TO").
      setCustomValidation("ITEM8_WO_NO,ITEM8_DATE_TO","ITEM8_TEMP1");

      itemblk8.addField("ITEM8_DATE_FROM_MASKED","Datetime", getDateTimeFormat("JAVA")).
      setHidden().
      setFunction("''");

      itemblk8.addField("ITEM8_PREDECESSOR").
      setSize(22).
      setLabel("PCMWACTIVESEPARATE2SERVICEMGTITEM8PRED: Predecessors").
      setLOV("ConnectPredecessorsDlg.page",600,450).
      setFunction("Wo_Role_Dependencies_API.Get_Predecessors(:ITEM8_WO_NO, :ITEM8_ROW_NO)");

      itemblk8.addField("ITEM8_TOOLS").
      setSize(12).
      setDbName("TOOLS").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTTITEM8TLS: Tools").
      setDefaultNotVisible().   
      setMaxLength(25);

      itemblk8.addField("ITEM8_REMARK").
      setSize(14).
      setDbName("REMARK").
      setLabel("PCMWACTIVESEPARATE2SERVICEMANAGEMENTITEM8REM: Remark").
      setDefaultNotVisible().   
      setMaxLength(80);

      itemblk8.addField("ITEM8_BASE_UNIT_PRICE","Money").
      setFunction("''").
      setHidden();

      itemblk8.addField("ITEM8_AGREEMENT_ID").
      setFunction("''").
      setHidden();

      itemblk8.addField("ITEM8_SALE_UNIT_PRICE","Money").
      setFunction("''").
      setHidden();

      itemblk8.addField("ITEM8_CURRENCY_RATE").
      setFunction("''").
      setHidden();

      itemblk8.addField("ITEM8_BUY_QTY_DUE","Number").
      setFunction("''").
      setHidden();

      itemblk8.addField("ITEM8_CUSTOMER_NO").
      setFunction("''").
      setHidden();

      itemblk8.addField("ITEM8_TEMP1").
      setFunction("''").
      setHidden();

      itemblk8.addField("ITEM8_PRICELISTNO").
      setFunction("''").
      setHidden();

      itemblk8.addField("ITEM8_ROWSTATUS").
      setFunction("''").
      setHidden();

      itemblk8.addField("ITEM8_USERALLOWED").
      setFunction("''").
      setHidden();

      itemblk8.addField("ITEM8_CBADDQUALIFICATION").
      setLabel("PCMWACTIVESEP2SERVICEMGTCBADDQUAL8: Additional Qualifications").
      setFunction("Work_Order_Role_API.Check_Qualifications_Exist(:ITEM8_WO_NO,:ITEM8_ROW_NO)").
      setCheckBox("0,1").
      setReadOnly().
      setQueryable();

      itemblk8.setView("WORK_ORDER_ROLE");
      itemblk8.defineCommand("WORK_ORDER_ROLE_API","New__,Modify__,Remove__");
      itemset8 = itemblk8.getASPRowSet();

      itemblk8.setMasterBlock(itemblk7);

      itembar8 = mgr.newASPCommandBar(itemblk8);
      itembar8.enableCommand(itembar8.FIND);
      itembar8.defineCommand(itembar8.NEWROW,"newRowITEM8");
      itembar8.defineCommand(itembar8.EDITROW, "editRowITEM8");
      itembar8.defineCommand(itembar8.COUNTFIND,"countFindITEM8");
      itembar8.defineCommand(itembar8.OKFIND,"okFindITEM8");

      itembar8.defineCommand(itembar8.SAVERETURN,"saveReturnITEM8","checkMandoItem8();Item8PlanMenCheck()");
      itembar8.defineCommand(itembar8.SAVENEW,null,"checkMandoItem8()");

      itembar8.enableMultirowAction();
      itembar8.addCustomCommand("freeTimeSearchFromOperations2",mgr.translate("PCMWACTIVESEPARATE2SMFREETIMESEARCH2: Free Time Search..."));
      itembar8.addCustomCommand("allocateEmployees2",mgr.translate("PCMWACTIVESEPARATE2SERMHTALLOCATEEMPLOYEES2: Allocate Employees..."));
      itembar8.addCustomCommand("selectAllocation2",mgr.translate("PCMWACTIVESEPARATE2SERMHTCLEARALLOCATION2: Clear Allocation..."));
      itembar8.addCustomCommand("connectExistingOperations",mgr.translate("PCMWACTIVESEPARATE2SERMGTCONEXISTINGOPER: Connect Existing Operation..."));
      itembar8.addCustomCommand("disconnectOperations",mgr.translate("PCMWACTIVESEPARATE2SERMGTDISCONNECTOPER: Disconnect Operation..."));
      itembar8.addCustomCommand("additionalQualifications2",mgr.translate("PCMWACTIVESEPARATE2SERVICEMGTADDQUALIFICATIONS2: Additional Qualifications..."));
      itembar8.addCustomCommand("predecessors2",mgr.translate("PCMWACTIVESEPARATE2SERVICEMGTPREDECESSORS2: Predecessors..."));
      itembar8.addCustomCommand("resheduleOperations2",mgr.translate("PCMWACTIVESEPARATE2RESHEDULEOPERATIONSITEM8: Reschedule Operations..."));
      itembar8.addCustomCommand("moveOperation2",mgr.translate("PCMWACTIVESEPARATE2SERVICEMGTMOVEOPERATION2: Move Operation..."));
      itembar8.forceEnableMultiActionCommand("connectExistingOperations");
      itembar8.removeFromMultirowAction("additionalQualifications2");
      itembar8.removeFromMultirowAction("resheduleOperations2");
      itembar8.removeFromMultirowAction("moveOperation2");

      //Bug 90872, Start
      itembar8.addCommandValidConditions("resheduleOperations2",    "DATE_FROM",  "Disable",  "");
      itembar8.appendCommandValidConditions("resheduleOperations2",    "DATE_TO",  "Disable",  "");
      //Bug 90872, End
      itembar8.addCommandValidConditions("moveOperation2", "DATE_FROM",  "Disable",  "");                              

      itemtbl8 = mgr.newASPTable(itemblk8);
      itemtbl8.setWrap();
      itemtbl8.enableRowSelect();

      itemlay8 = itemblk8.getASPBlockLayout();
      itemlay8.setDefaultLayoutMode(itemlay8.MULTIROW_LAYOUT);
      itemlay8.setDialogColumns(3);  


      //-----------------------operations block for jobs end---------------------------------

      //-----------------------------------------------------------------------
      //----------------- block used for setcheckbox() funtion ----------------
      //-----------------------------------------------------------------------

      tempblk = mgr.newASPBlock("TEMP");
      f = tempblk.addField("LUNAME");

      //-----------------------------------------------------------------------
      //----------------- block used for RMB status funtions  -----------------
      //-----------------------------------------------------------------------

      prntblk = mgr.newASPBlock("RMBBLK");
      prntblk.addField("ATTR0");
      prntblk.addField("ATTR1");
      prntblk.addField("ATTR2");
      prntblk.addField("ATTR3");
      prntblk.addField("ATTR4");
      prntblk.addField("RESULT_KEY");

      //-----------------------------------------------------------------------
      //------------------------ POSTINGS BLOCK -------------------------------
      //-----------------------------------------------------------------------

      blkPost = mgr.newASPBlock("POSTI");
      f = blkPost.addField("CODE_A","Number");
      f = blkPost.addField("CODE_B","Number");
      f = blkPost.addField("CONTROL_TYPE");
      f = blkPost.addField("STR_CODE");


      //--------------------------------------------------------------------------
      //--------------------------------------------------------------------------

      teblk = mgr.newASPBlock("TE");

      teblk.addField("ORGCOST");
      teblk.addField("ROLECOST");
      teblk.addField("ITEM8_ORGCOST");

      enableConvertGettoPost();

   }


   public void setLOVNames()
   {
      ASPManager mgr = getASPManager();


      String viewName = "CODE_" ;

      String coscentb =  itemset4.getRow().getValue("COST_CENTERB");
      String projnob =  itemset4.getRow().getValue("PROJECT_NOB");
      String objnob  =  itemset4.getRow().getValue("OBJECT_NOB");

      String headertext1 = mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTHEADTEXT1: Cost Center");
      ctx.setGlobal("NAME"+coscentb ,headertext1);
      String headertext2 = mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTHEADTEXT2: Project");
      ctx.setGlobal("NAME"+projnob,headertext2);
      String headertext3 = mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTHEADTEXT3: Region");
      ctx.setGlobal("NAME"+objnob,headertext3);

      String coscentView = viewName + coscentb;
      String projnoView = viewName + projnob;
      String objnoView = viewName + objnob;

      mgr.getASPField("ITEM4_COST_CENTER").setDynamicLOV(coscentView,"COMPANY",600,445);
      mgr.getASPField("ITEM4_PROJECT_NO").setDynamicLOV(projnoView,"COMPANY",600,445);
      mgr.getASPField("ITEM4_OBJECT_NO").setDynamicLOV(objnoView,"COMPANY",600,445);   
   }

   public boolean checkMROObjRO()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      int count = 0;

      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
         count = headset.countSelectedRows();
      }
      else
      {
         headset.unselectRows();
         headset.selectRow();
         count = 1;
      }

      for (int i = 0; i < count; i++)
      {
         cmd = trans.addCustomCommand("GETVIMINFO_" + i, "ACTIVE_SEPARATE_API.Separate_Mro_Part_Serial");
         cmd.addParameter("HEAD_PART_NO");
         cmd.addParameter("HEAD_SERIAL_NO");
         cmd.addParameter("WO_NO", headset.getValue("WO_NO"));

         if (headlay.isMultirowLayout())
            headset.next();
      }

      trans = mgr.perform(trans);

      if (headlay.isMultirowLayout())
         headset.setFilterOff();

      for (int i = 0; i < count; i++)
      {
         if (mgr.isEmpty(trans.getValue("GETVIMINFO_" + i + "/DATA/HEAD_PART_NO")) && mgr.isEmpty(trans.getValue("GETVIMINFO_" + i + "/DATA/HEAD_SERIAL_NO")))
            return false;
      }

      return true;
   }

   public void checkObjAvaileble()
   {
      if (!again)
      {
         ASPManager mgr = getASPManager();

         ASPBuffer availObj;
         trans.clear();
         //Bug 68947, Start   
         trans.addSecurityQuery("WORK_ORDER_REQUIS_HEADER,SEPARATE_WORK_ORDER,WORK_ORDER_REPORT_PAGE,WORK_ORDER_PERMIT,OBJ_SUPP_WARRANTY_TYPE,WORK_ORDER_QUOTATION,CUSTOMER_AGREEMENT,EQUIPMENT_OBJECT_ADDRESS,PRE_ACCOUNTING,EQUIPMENT_OBJECT_SPARE,PM_ACTION,EQUIPMENT_SPARE_STRUCTURE,INVENTORY_PART_IN_STOCK_NOPAL,WORK_ORDER_PART_ALLOC,INVENTORY_PART_CONFIG,PURCHASE_PART_SUPPLIER,INVENTORY_TRANSACTION_HIST,MRO_WO_FROM_VIM,COMPETENCY_GROUP_LOV1,PSC_CONTR_PRODUCT");
         //Bug 68947, End   
         trans.addPresentationObjectQuery("PCMW/WorkOrderRequisHeaderRMB.page,PCMW/SeparateWorkOrder.page,PCMW/WorkOrderReportPageOvw2.page,PCMW/WorkOrderPermit.page,PCMW/ReportInWorkOrderWiz.page,PCMW/ActiveSeparateReportCOInfo.page,PCMW/SupplierWarrantyType.page,PCMW/CopyWorkOrderDlg.page,PCMW/ActiveSeparate3.page,PCMW/WorkOrderQuotation.page,PCMW/MoveToRepairWorkshopDlg.page,MPCCOW/PreAccountingDlg.page,PCMW/MaintenanceObject2.page,PCMW/MaintenaceObject3.page,EQUIPW/EquipmentSpareStructure3.page,PCMW/MaterialRequisReservatDlg.page,PCMW/MaterialRequisReservatDlg2.page,INVENW/InventoryPartAvailabilityPlanningQry.page,PURCHW/PurchasePartSupplier.page,PCMW/ActiveWorkOrder.page,EQUIPW/EquipmentObjectAddress.page,PCMW/InventoryPartLocationDlg.page");
         trans.addSecurityQuery("Active_Work_Order_API","Copy__");
         trans.addSecurityQuery("Wo_Order_RPI","Report_Printout");         
         trans.addSecurityQuery("PURCH_OBJ_RECEIVE_ORD_UTIL_API","Create_Mro_Receive_Order");
         trans.addSecurityQuery("IDENTITY_INVOICE_INFO_API","Get_Identity_Type");
         trans.addSecurityQuery("MAINT_MATERIAL_REQUISITION_RPI","Report_Printout");
         trans.addSecurityQuery("MAINT_MATERIAL_REQ_LINE_API","Make_Reservation_Short,Make_Auto_Issue_Detail,Make_Manual_Issue_Detail");

         trans = mgr.perform(trans);
         availObj = trans.getSecurityInfo();

         if (availObj.itemExists("WORK_ORDER_REQUIS_HEADER") && availObj.namedItemExists("PCMW/WorkOrderRequisHeaderRMB.page"))
            actEna1 = true;

         if (availObj.itemExists("SEPARATE_WORK_ORDER") && availObj.namedItemExists("PCMW/SeparateWorkOrder.page"))
            actEna2 = true;

         if (availObj.itemExists("PRE_ACCOUNTING") && availObj.namedItemExists("MPCCOW/PreAccountingDlg.page"))
            actEna3 = true;

         if (availObj.namedItemExists("PCMW/ReportInWorkOrderWiz.page"))
            actEna5 = true;

         if (availObj.itemExists("WORK_ORDER_REPORT_PAGE") && availObj.namedItemExists("PCMW/WorkOrderReportPageOvw2.page"))
            actEna6 = true;

         if (availObj.itemExists("WORK_ORDER_PERMIT") && availObj.namedItemExists("PCMW/WorkOrderPermit.page"))
            actEna7 = true;

         if (availObj.namedItemExists("PCMW/ActiveSeparateReportCOInfo.page"))
            actEna8 = true;

         if (availObj.itemExists("OBJ_SUPP_WARRANTY_TYPE") && availObj.namedItemExists("PCMW/SupplierWarrantyType.page"))
            actEna9 = true;

         if (availObj.namedItemExists("PCMW/CopyWorkOrderDlg.page") && availObj.itemExists("Active_Work_Order_API.Copy__"))
            actEna10 = true;

         if (availObj.namedItemExists("PCMW/ActiveSeparate3.page"))
            actEna11 = true;

         if (availObj.itemExists("WORK_ORDER_QUOTATION") && availObj.namedItemExists("PCMW/WorkOrderQuotation.page"))
            actEna12 = true;

         if (availObj.itemExists("CUSTOMER_AGREEMENT"))
            actEna13 = true;

         if (availObj.namedItemExists("PCMW/MoveToRepairWorkshopDlg.page"))
            actEna14 = true;

         if (availObj.namedItemExists("EQUIPW/EquipmentObjectAddress.page") && availObj.itemExists("EQUIPMENT_OBJECT_ADDRESS"))
            actEna15 = true;

         if (availObj.itemExists("Wo_Order_RPI.Report_Printout"))
            actEna16 = true;

         if (availObj.itemExists("PRE_ACCOUNTING") && availObj.namedItemExists("MPCCOW/PreAccountingDlg.page"))
            actEna17 = true;

         if (availObj.itemExists("MAINT_MATERIAL_REQUISITION_RPI.Report_Printout"))
            actEna18 = true;

         if (availObj.itemExists("EQUIPMENT_OBJECT_SPARE") && availObj.namedItemExists("PCMW/MaintenanceObject2.page"))
            actEna19 = true;

         if (availObj.itemExists("PM_ACTION") && availObj.namedItemExists("PCMW/MaintenaceObject3.page"))
            actEna20 = true;

         if (availObj.itemExists("EQUIPMENT_SPARE_STRUCTURE") && availObj.namedItemExists("EQUIPW/EquipmentSpareStructure3.page"))
            actEna21 = true;

         if (availObj.itemExists("MAINT_MATERIAL_REQ_LINE_API.Make_Reservation_Short"))
            actEna22 = true;

         if (availObj.itemExists("INVENTORY_PART_IN_STOCK_NOPAL") && availObj.namedItemExists("PCMW/MaterialRequisReservatDlg.page"))
            actEna23 = true;

         if (availObj.itemExists("WORK_ORDER_PART_ALLOC") && availObj.namedItemExists("PCMW/MaterialRequisReservatDlg2.page"))
            actEna24 = true;

         if (availObj.itemExists("MAINT_MATERIAL_REQ_LINE_API.Make_Auto_Issue_Detail"))
            actEna25 = true;

         if (availObj.itemExists("INVENTORY_PART_CONFIG") && availObj.namedItemExists("INVENW/InventoryPartAvailabilityPlanningQry.page"))
            actEna26 = true;

         if (availObj.itemExists("PURCHASE_PART_SUPPLIER") && availObj.namedItemExists("PURCHW/PurchasePartSupplier.page"))
            actEna27 = true;

         if (availObj.itemExists("INVENTORY_TRANSACTION_HIST") && availObj.namedItemExists("PCMW/ActiveWorkOrder.page"))
            actEna28 = true;

         if (availObj.itemExists("PURCH_OBJ_RECEIVE_ORD_UTIL_API.Create_Mro_Receive_Order") && availObj.itemExists("IDENTITY_INVOICE_INFO_API.Get_Identity_Type"))
            actEna29 = true;
         if (availObj.itemExists("COMPETENCY_GROUP_LOV1"))
            actEna30 = true;

         if ( availObj.itemExists("INVENTORY_PART_IN_STOCK_NOPAL") && availObj.namedItemExists("PCMW/InventoryPartLocationDlg.page") && availObj.itemExists("MAINT_MATERIAL_REQ_LINE_API.Make_Manual_Issue_Detail"))
            actEna31 = true;
         //Bug 68947, Start   
         if (availObj.itemExists("PSC_CONTR_PRODUCT"))
            actEna32 = true;
         //Bug 68947, End   
      }
   }

   // 040123  ARWILK  Begin  (Enable Multirow RMB actions)
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
   // 040123  ARWILK  End  (Enable Multirow RMB actions)

   // 031204  ARWILK  Begin  (Replace blocks with tabs)
   public void activateOperations()
   {
      tabs.setActiveTab(1);
      okFindITEM2();
   }

   public void activateMaterial()
   {
      tabs.setActiveTab(2);
      okFindITEM3();
   }

   public void activateBudget()
   {
      tabs.setActiveTab(3);
      okFindITEM4();
   }

   public void activatePlanning()
   {
      tabs.setActiveTab(4);
      okFindITEM5();
   }

   public void activateJobs()
   {
      tabs.setActiveTab(5);
      okFindITEM7();
   }
   // 031204  ARWILK  End  (Replace blocks with tabs)

   public String addNumToDate(String sDate,double nNumber)
   {
      ASPManager mgr = getASPManager();
      buf = mgr.newASPBuffer();
      buf.addFieldItem("PLAN_S_DATE", sDate);      

      trans.clear();
      q = trans.addQuery("DATEAFTER","select ? + ? PLAN_F_DATE from DUAL");
      q.addParameter("PLAN_S_DATE", mgr.readValue("PLAN_S_DATE")); 
      q.addParameter("PLAN_HRS", nNumber/24+"" );
      q.includeMeta("ALL");
      trans = mgr.perform(trans);

      String output = trans.getBuffer("DATEAFTER/DATA").getFieldValue("PLAN_F_DATE");
      trans.clear();

      return output;
   }

   //Bug Id 70921, Start
   public String getDatesForSrvconValidate(ASPBuffer buf)
   {

      ASPManager mgr = getASPManager();

      int ref = 0;

      trans.clear();

      String sReturnStr = "";

      String sPlanHrs = buf.getFieldValue("PLAN_HRS");;
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

      if (checksec("PSC_CONTR_PRODUCT_API.Exist", 1))
      {
         cmd = trans.addCustomCommand("EXIST_LINE", "PSC_CONTR_PRODUCT_API.Exist");
         cmd.addParameter("CONTRACT_ID", sContractId);
         cmd.addParameter("LINE_NO", sLineNo);
      }

      if (checksec("ACTIVE_SEPARATE_API.Get_Dates_For_Contract", 1))
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

      if (checksec("ACTIVE_SEPARATE_API.Get_Contract_Id", 1))
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
         if (checksec("Active_Work_Order_API.Calc_Completion_Date", 1))
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
         if (checksec("Active_Separate_Api.Cal_Srvcon_Dates_For_Exec_Time", 1))
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

      //Bug 69914, start
      setStringLables();
      //Bug 69914, end

      if (matSingleMode || "TRUE".equals(showMat))
      {
         activateMaterial();
         itemlay3.setLayoutMode(itemlay3.SINGLE_LAYOUT);
         String matLine = ctx.findGlobal("CURRROWGLOBAL");   
         int matLineInt = Integer.parseInt(matLine);

         itemset3.goTo(matLineInt);
         //clearItem4();
         okFindITEM6();
         matSingleMode = false;
         updateBudg = true;
      }

      // 031204  ARWILK  Begin  (Replace blocks with tabs)
      headbar.removeCustomCommand("activateOperations");
      headbar.removeCustomCommand("activateMaterial");
      headbar.removeCustomCommand("activateBudget");
      headbar.removeCustomCommand("activatePlanning");
      headbar.removeCustomCommand("activateJobs");

      headbar.removeCustomCommand("refreshForm");
      // 031204  ARWILK  End  (Replace blocks with tabs)s

      headbar.removeCustomCommand("refreshMaterialTab");

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
         }

         if (itemlay2.isSingleLayout() && (itemset2.countRows() > 0))
         {
            /* int planMen,planHrs;
             if (mgr.isEmpty(itemset2.getValue("PLAN_MEN"))) 
                 planMen =0; 
             else 
                 planMen= Integer.parseInt(itemset2.getValue("PLAN_MEN"));

             if (mgr.isEmpty(itemset2.getValue("PLAN_HRS"))) 
                 planHrs =0;
             else 
                 planHrs= Integer.parseInt(itemset2.getValue("PLAN_HRS"));

             
             int manHrs = planMen * planHrs;
             
             if ((manHrs == 0) && (!mgr.isEmpty(itemset2.getValue("SIGN_ID")))) 
                itembar2.removeCustomCommand("allocateEmployees0"); */   

            if (!mgr.isEmpty(itemset2.getValue("SIGN_ID")))
               itembar9.disableCommand(itembar9.NEWROW);
         }

         /* if (itemlay8.isSingleLayout() &&(itemset8.countRows() > 0)) 
          {
              int planMen,planHrs;
              if (mgr.isEmpty(itemset8.getValue("PLAN_MEN"))) 
                  planMen =0; 
              else 
                  planMen= Integer.parseInt(itemset8.getValue("PLAN_MEN"));

              if (mgr.isEmpty(itemset8.getValue("PLAN_HRS"))) 
                  planHrs =0;
              else 
                  planHrs= Integer.parseInt(itemset8.getValue("PLAN_HRS"));

              
              int manHrs = planMen * planHrs;
              
              if ((manHrs == 0) && (!mgr.isEmpty(itemset8.getValue("SIGN_ID")))) 
                 itembar8.removeCustomCommand("allocateEmployees2");    
          }  */


         if (itemlay8.isNewLayout()  && (itemset7.countRows() > 0))
            mgr.getASPField("ITEM8_PREDECESSOR").setReadOnly();

         if (itemlay2.isNewLayout())
            mgr.getASPField("PREDECESSOR").setReadOnly();

         if (itemset7.countRows() > 0)
         {
            if (itemlay8.isEditLayout())
               planMen = itemset8.getValue("PLAN_MEN");
         }

         if (itemlay2.isEditLayout())
            planMen = itemset2.getValue("PLAN_MEN");

      }

      if (!actEna2)
         headbar.removeCustomCommand("structure");

      if (!actEna3)
         headbar.removeCustomCommand("prePost");

      if (!actEna5)
         headbar.removeCustomCommand("repInWiz");

      if (!actEna6)
         headbar.removeCustomCommand("freeNotes");

      if (!actEna7)
         headbar.removeCustomCommand("permits");

      if (!actEna8)
         headbar.removeCustomCommand("coInformation");

      //Bug 68947, Start   
      if (!actEna32 || !(headset.countRows()>0 && headlay.isSingleLayout()) || !"VIM".equals(headset.getValue("CONNECTION_TYPE")))
         headbar.removeCustomCommand("srvConLineSearch");
      //Bug 68947, End   

      if (!actEna9)
         headbar.removeCustomCommand("suppWarr");

      if (!actEna10)
         headbar.removeCustomCommand("copy");

      if (!actEna11)
         headbar.removeCustomCommand("servreqvord");

      if (!actEna12)
         headbar.removeCustomCommand("prepWorkOrderQuot");

      if (!actEna13)
         headbar.removeCustomCommand("printServO");

      if (!actEna14)
         headbar.removeCustomCommand("turnToRepOrd");

      if (mgr.isPresentationObjectInstalled("equipw/EquipmentObjectAddress.page"))
      {
         if (!actEna15)
            headbar.removeCustomCommand("schonobjaddr");
      }

      if (!actEna16)
         headbar.removeCustomCommand("pickListForWork");

      if (!actEna18)
         itembar3.removeCustomCommand("printPicList");

      if (!actEna19)
         itembar.removeCustomCommand("sparePartObject");

      if (!actEna20)
         itembar.removeCustomCommand("objStructure");

      if (!actEna21)
         itembar.removeCustomCommand("detchedPartList");

      if (!actEna22)
         itembar.removeCustomCommand("reserve");

      if (!actEna23)
         itembar.removeCustomCommand("manReserve");

      if (!actEna24)
         itembar.removeCustomCommand("unreserve");

      if (!actEna25)
         itembar.removeCustomCommand("issue");

      if (mgr.isPresentationObjectInstalled("invenw/InventoryPartAvailabilityPlanningQry.page"))
      {
         if (!actEna26)
            itembar.removeCustomCommand("availDetail");
      }

      if (mgr.isPresentationObjectInstalled("purchw/PurchasePartSupplier.page"))
      {
         if (!actEna27)
            itembar.removeCustomCommand("supPerPart");
      }

      if (!actEna28)
         itembar.removeCustomCommand("matReqUnissue");

      if (actEna29)
      {
         if (headlay.isSingleLayout() && headset.countRows()>0)
         {
            if (!checkMROObjRO())
               headbar.removeCustomCommand("mroObjReceiveO");
         }
      }
      else
         headbar.removeCustomCommand("mroObjReceiveO");
      if (!actEna30)
      {
         itembar2.removeCustomCommand("additionalQualifications1");
         itembar8.removeCustomCommand("additionalQualifications2");
      }

      if ( !actEna31 )
         itembar.removeCustomCommand("manIssue");

      if (mgr.isPresentationObjectInstalled("equipw/EquipmentAllObjectLightRedirect.page"))
         mgr.getASPField("MCH_CODE").setHyperlink("../equipw/EquipmentAllObjectLightRedirect.page","MCH_CODE,MCH_CODE_CONTRACT,WO_NO","NEWWIN");

      if (mgr.isPresentationObjectInstalled("enterw/CustomerInfo.page"))
         mgr.getASPField("CUSTOMER_NO").setHyperlink("../enterw/CustomerInfo.page","CUSTOMER_NO","NEWWIN");

      if (mgr.isPresentationObjectInstalled("docmaw/DocReference.page"))
      {
         mgr.getASPField("DOCUMENT").setHyperlink("../docmaw/DocReference.page","LU_NAME,KEY_REF","NEWWIN");
         mgr.getASPField("CBHASDOCUMNTS").setHyperlink("../docmaw/DocReference.page","LU_NAME,KEY_REF","NEWWIN");
      }

      if (headset.countRows() == 1)
         headbar.disableCommand(headbar.BACK);

      if (headlay.isFindLayout() || itemlay2.isFindLayout())
         isFind = true;

      if (headlay.isNewLayout())
      {
         trans.clear();  
         trans.addQuery("SYSDAT","SELECT SYSDATE SYS_DATE FROM DUAL");
         trans = mgr.perform(trans);          
         String sys_date = trans.getBuffer("SYSDAT/DATA").getValue("SYS_DATE");

         row = headset.getRow();
         row.setValue("REG_DATE",sys_date);
         headset.setRow(row); 
      }

      if (headlay.isSingleLayout() && 3 == tabs.getActiveTab())
         sumColumns();

      if (headlay.isSingleLayout() && headset.countRows() > 0)
      {
         setCheckBoxValues();
         setWorkCenterValues();
      }

      if (itemlay2.isNewLayout() || itemlay2.isEditLayout() )
      {

         trans.clear();

         cmd = trans.addCustomFunction("GETCURRANCYCODE","ACTIVE_SEPARATE_API.Get_Currency_Code","CURRENCY_CODE");
         cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
         //Bug 69392, start
         if (mgr.isModuleInstalled("ORDER"))
         {
            cmd = trans.addCustomFunction("GETSALESPRICEGRPID","SALES_PART_API.GET_SALES_PRICE_GROUP_ID","SALES_PRICE_GROUP_ID");
            cmd.addParameter("ITEM_CATALOG_CONTRACT");
            cmd.addParameter("ITEM_CATALOG_NO");
         }
         //Bug 69392, end

         trans = mgr.perform(trans);
         String currencyCodeForLov = trans.getValue("GETCURRANCYCODE/DATA/CURRENCY_CODE");

         //Bug 69392, start
         String SalesPriceGrpForLov = "";
         if (mgr.isModuleInstalled("ORDER"))
            SalesPriceGrpForLov = trans.getValue("GETSALESPRICEGRPID/DATA/SALES_PRICE_GROUP_ID");
         //Bug 69392, end

         if (currencyCodeForLov == null)
         {
            currencyCodeForLov = "IS NOT NULL";    
         }
         else
         {
            currencyCodeForLov = "='"+currencyCodeForLov+"'";
         }

         if (SalesPriceGrpForLov == null)
         {
            SalesPriceGrpForLov = "IS NOT NULL";    
         }
         else
         {
            SalesPriceGrpForLov = "= "+SalesPriceGrpForLov+"'";
         }

         String sContractForLov = itemset2.getRow().getFieldValue("ITEM2_CONTRACT");

         String lovWhere = "(SALES_PRICE_GROUP_ID "+ SalesPriceGrpForLov+") AND ( CURRENCY_CODE "+currencyCodeForLov+") AND ( CONTRACT ='"
                           +sContractForLov+"')";

         mgr.getASPField("PRICE_LIST_NO").setLOVProperty("WHERE",lovWhere);
      }

      if (itemlay2.isNewLayout() || itemlay2.isEditLayout() || itemlay3.isNewLayout() || itemlay3.isEditLayout() || itemlay4.isEditLayout() || itemlay2.isFindLayout() || itemlay3.isFindLayout())
      {
         headbar.disableCommand(headbar.DELETE);
         headbar.disableCommand(headbar.NEWROW);
         headbar.disableCommand(headbar.EDITROW);
         headbar.disableCommand(headbar.DELETE);
         headbar.disableCommand(headbar.DUPLICATEROW);
         headbar.disableCommand(headbar.FIND);
         headbar.disableCommand(headbar.BACKWARD);
         headbar.disableCommand(headbar.FORWARD);

         headbar.removeCustomCommand("structure");
         headbar.removeCustomCommand("freeNotes");
         headbar.removeCustomCommand("permits");
         headbar.removeCustomCommand("observed");
         headbar.removeCustomCommand("underPrep");
         headbar.removeCustomCommand("prepared");
         headbar.removeCustomCommand("released");
         headbar.removeCustomCommand("started");
         headbar.removeCustomCommand("workDone");
         headbar.removeCustomCommand("reported");
         headbar.removeCustomCommand("finished");
         headbar.removeCustomCommand("cancelled");
         headbar.removeCustomCommand("coInformation");
         //Bug 68947, Start   
         headbar.removeCustomCommand("srvConLineSearch");
         //Bug 68947, End   
         headbar.removeCustomCommand("servreqvord");
         headbar.removeCustomCommand("prepWorkOrderQuot");
         headbar.removeCustomCommand("printServO");
         headbar.removeCustomCommand("prePost");
         headbar.removeCustomCommand("turnToRepOrd");
         headbar.removeCustomCommand("repInWiz");
         headbar.removeCustomCommand("suppWarr");

         if (mgr.isPresentationObjectInstalled("equipw/EquipmentObjectAddress.page"))
            headbar.removeCustomCommand("schonobjaddr");
      }

      if (!itemlay2.isMultirowLayout())
      {
         mgr.getASPField("ITEM2_DESCRIPTION").unsetDefaultNotVisible();
         mgr.getASPField("PRICE_LIST_NO").unsetDefaultNotVisible();
         mgr.getASPField("TOOLS").unsetDefaultNotVisible();
         mgr.getASPField("REMARK").unsetDefaultNotVisible();
         mgr.getASPField("COSTAMOUNT").unsetDefaultNotVisible();
         mgr.getASPField("LISTPRICE").unsetDefaultNotVisible();                         
      }

      if (itemlay4.isEditLayout())
      {
         mgr.getASPField("WORK_ORDER_COST_TYPE").unsetSelectBox();
         mgr.getASPField("WORK_ORDER_COST_TYPE").setReadOnly();                      
      }

      if (!itemlay5.isMultirowLayout())
      {
         mgr.getASPField("LINE_DESCRIPTION").unsetDefaultNotVisible();
         mgr.getASPField("ITEM5_COST_AMOUNT").unsetDefaultNotVisible();
         mgr.getASPField("ITEM5_SALES_PRICE_AMOUNT").unsetDefaultNotVisible();               
      }
      if (itemlay5.isEditLayout())
      {
         if ("M".equals(itemset5.getRow().getFieldValue("WORK_ORDER_COST_TYPE_DB")) || "P".equals(itemset5.getRow().getFieldValue("WORK_ORDER_COST_TYPE_DB")) || "T".equals(itemset5.getRow().getFieldValue("WORK_ORDER_COST_TYPE_DB")))
            mgr.getASPField("LINE_DESCRIPTION").setReadOnly();

         // Set the cost field read only for automatically generated external lines.
         if ("1".equals(itemset5.getRow().getFieldValue("IS_AUTO_LINE")))
            mgr.getASPField("ITEM5COST").setReadOnly();
      }

      if (headlay.isSingleLayout() && headset.countRows()>0)
      {
         title_ = mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTTRE: Prepare Work Order for Service Management");

      }
      else
      {
         title_ = mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTTRBE: Prepare Work Order for Service Management");
      }  

      fldTitleReportedBy = mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTREPORTEDBYFLD: Reported+by");   
      fldTitleWorkLeaderSign = mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTWORKLEADERFLD: Work+Leader");   
      fldTitlePreparedBy = mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPREPAREDBYFLD: Prepared+by");   
      fldTitleWorkMasterSign = mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTWORKMASTERFLD: Work+Master");  
      fldTitleSign = mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSIGNLFLD: Signature");     
      fldTitleObjectId = mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTOBJECTID: Object+ID"); 

      lovTitleReportedBy = mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTREPORTEDBYLOV: List+of+Reported+by");   
      lovTitleWorkLeaderSign = mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTWORKLEADERLOV: List+of+Work+Leader");   
      lovTitlePreparedBy = mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTPREPAREDBYLOV: List+of+Prepared+by");   
      lovTitleWorkMasterSign = mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTWORKMASTERLOV: List+of+Work+Master");  
      lovTitleSign = mgr.translate("PCMWACTIVESEPARATE2SERVICEMANAGEMENTSIGNLOV: List+of+Signature");     

      if (headset.countRows() > 0)
         sWONo = headset.getRow().getValue("WO_NO");
      else
         sWONo   = "";

      if (itemlay.isVisible() && itemset3.countRows()>0)
      {
         String mat_state = itemset3.getRow().getFieldValue("ITEM3_STATE");

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

      if (headlay.isSingleLayout() && headset.countRows()>0)
      {
         String connType = headset.getValue("CONNECTION_TYPE");

         trans.clear();

         cmd = trans.addCustomFunction("CONNTYPE","MAINT_CONNECTION_TYPE_API.Encode","CONNECTION_TYPE_DB");
         cmd.addParameter("CONNECTION_TYPE",connType);

         trans = mgr.perform(trans);

         String connTypeDB = trans.getValue("CONNTYPE/DATA/CONNECTION_TYPE_DB");

         if ("VIM".equals(connTypeDB))
         {
            mgr.getASPField("MCH_CODE_CONTRACT").setHidden();  
         }
      }

      if (mgr.isModuleInstalled("PROJ") && !mgr.isPresentationObjectInstalled("projw/ConObjToActivityDlg.page"))
      {
         headbar.disableCustomCommand("connectToActivity");
         headbar.disableCustomCommand("disconnectFromActivity");
         headbar.disableCustomCommand("activityInfo");
      }

      if (itemset5.countRows() > 0)
      {
         String lineType = itemset5.getValue("WORK_ORDER_COST_TYPE");
         if (mgr.isEmpty(lineType))
         {
            lineType = "XXXXX";
         }
         String lovwhere = "CONTRACT='"+itemset5.getValue("CATALOG_CONTRACT")+"'";
         trans.clear();

         cmd = trans.addCustomFunction("CL1","Work_Order_Cost_Type_Api.Get_Client_Value(1)","CLIENTVAL1");

         trans = mgr.perform(trans);

         String strMaterial = trans.getValue("CL1/DATA/CLIENTVAL1");


         if (lineType.equals(strMaterial))
            mgr.getASPField("ITEM5_CATALOG_NO").setDynamicLOV("SALES_PART_ACTIVE_LOV",600,445);
         else
            mgr.getASPField("ITEM5_CATALOG_NO").setDynamicLOV("SALES_PART_SERVICE_LOV",600,445); 

         mgr.getASPField("ITEM5_CATALOG_NO").setLOVProperty("WHERE",lovwhere);
      }

      if (itemlay7.isVisible() && headset.countRows() > 0)
      {
         String sWhereStrForITEM7 = "CONTRACT = '" + headset.getValue("CONTRACT") + "'";

         if (itemlay7.isFindLayout())
         {
            mgr.getASPField("STD_JOB_ID").setLOV("StandardJobLov1.page", 600, 450);
            sWhereStrForITEM7 = sWhereStrForITEM7 + " AND STANDARD_JOB_TYPE_DB = '1'";
         }

         mgr.getASPField("STD_JOB_ID").setLOVProperty("WHERE", sWhereStrForITEM7);
         mgr.getASPField("STD_JOB_REVISION").setLOVProperty("WHERE", sWhereStrForITEM7);
      }

      if (itemlay3.isVisible() && headset.countRows() > 0)
      {
         mgr.getASPField("ITEM3_CONTRACT").setLOVProperty("WHERE","Site_API.Get_Company(CONTRACT) = Site_API.Get_Company('"+headset.getRow().getValue("CONTRACT")+"')");
      }

      if (headlay.isFindLayout())
      {
         if (mgr.isModuleInstalled("SRVCON"))
            mgr.getASPField("CONTRACT_ID").setDynamicLOV("SC_SERVICE_CONTRACT");
      }
      else
      {
         mgr.getASPField("CONTRACT_ID").setLOV("PscContrProductLov.page","CONTRACT,CUSTOMER_NO,MCH_CODE");
      }

      /*if (mgr.translate("PCMWACTIVESEPARATESERVICEWMANAGEMENTSUMMARY: Summary").equals(itemset4.getRow().getValue("WORK_ORDER_COST_TYPE")))
     itembar4.removeCustomCommand("editItem4");*/
      // Bug Id 70012, Start
      if (mgr.isModuleInstalled("PCMSCI"))
      {
         bPcmsciExist = true;
      }
      // Bug Id 70012, End
      //(+) Bug 66406, Start
      if (itemlay7.isFindLayout())
         mgr.getASPField("CONN_PM_NO").setLOV("../pcmw/PmActionLov1.page",600,450);
      else if (itemlay7.isNewLayout())
         mgr.getASPField("CONN_PM_NO").setLOV("../pcmw/PmActionLov.page",600,450);
      //(+) Bug 66406, End 

      //Bug 72644, Start
      if (headset.countRows()>0 && "FALSE".equals(headset.getValue("OP_EXIST")) && "FALSE".equals(headset.getValue("JOB_EXIST")) && "FALSE".equals(headset.getValue("TOOL_EXIST")))
          headbar.removeCustomCommand("optimizeSchedule");

      headbar.removeCustomCommand("refresh");
      //Bug 72644, End
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
      return "PCMWACTIVESEPARATE2SERVICEMANAGEMENTTITLE: Prepare Work Order for Service Management";
   }

   protected void printContents() throws FndException
   {
      //Bug 68947, Start   
      printHiddenField("TEMPCONTRACTID","");
      printHiddenField("TEMPLINENO", "");
      //Bug 68947, End  
      //Bug 82543, Start
      printHiddenField("REFRESH_FLAG","");
      //Bug 82543, End
      //Bug 89703, Start
      printHiddenField("HASPLANNING","");
      //Bug 89703, End

      ASPManager mgr = getASPManager();
      /*String sOracleFormat = mgr.replace(mgr.getFormatMask("Datetime",true),"mm","MI");
      if (sOracleFormat.indexOf("HH")>-1)
      {
          if ("HH".equals(sOracleFormat.substring(sOracleFormat.indexOf("HH"),sOracleFormat.indexOf("HH")+2)))
              sOracleFormat = mgr.replace(sOracleFormat,"HH","HH24");

      }*/
      if (headlay.isSingleLayout() && headset.countRows() > 0)
      {

         if (itemlay8.isEditLayout() && (itemset8.countRows() > 0))
         {
            appendDirtyJavaScript("   f.ITEM8_PREDECESSOR.disabled = true;\n");

         }
         if (itemlay2.isEditLayout() && (itemset2.countRows() > 0))
         {
            appendDirtyJavaScript("   f.PREDECESSOR.disabled = true;\n");

         }

      }

      printHiddenField("STATEVAL","");
      printHiddenField("BUTTONVAL","");
      printHiddenField("ONCEGIVENERROR","FALSE");
      printHiddenField("HIDDENPARTNO","");
      printHiddenField("ACTIVITY_SEQ_PROJ", "");
      printHiddenField("CREREPNONSER", openCreRepNonSer);
      printHiddenField("REFRESHPARENT1", "FALSE");
      printHiddenField("REFRESHPARENT2", "FALSE");
      printHiddenField("PLANMEN", planMen);
      printHiddenField("SAVEEXECUTE", "");
      printHiddenField("PRPOSTCHANGED", "");

      // 031204  ARWILK  Begin  (Replace blocks with tabs)
      appendToHTML(headlay.show());

      if (headlay.isSingleLayout() && headset.countRows() > 0)
         appendToHTML(tabs.showTabsInit());
      // 031204  ARWILK  End  (Replace blocks with tabs)

      if (headlay.isSingleLayout() && headset.countRows() > 0)
      {
         if (tabs.getActiveTab() == 1)
         {
            appendToHTML(itemlay2.show());
            if (itemlay2.isSingleLayout() && (itemset2.countRows() > 0))
               appendToHTML(itemlay9.show());
         }
         else if (tabs.getActiveTab() == 2)
         {
            appendToHTML(itemlay3.show());
            if (itemlay3.isSingleLayout() && (itemset3.countRows() > 0))
               appendToHTML(itemlay.show());
         }
         else if (tabs.getActiveTab() == 3)
            appendToHTML(itemlay4.show());
         else if (tabs.getActiveTab() == 4)
            appendToHTML(itemlay5.show());
         else if (tabs.getActiveTab() == 5)
         {
            appendToHTML(itemlay7.show());
            if (itemlay7.isSingleLayout() && (itemset7.countRows() > 0))
               appendToHTML(itemlay8.show());
         }

      }
      // 031204  ARWILK  End  (Replace blocks with tabs)
      appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(clearAlloc0);
      appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"PageID_clearAllocation1\")==\"TRUE\")\n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript("      if (confirm(\"");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2SERVICEMGTCLEARALLOC: Clear Allocations on selected line(s)"));
      appendDirtyJavaScript("\")) {\n");
      appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"DDDD\";\n");
      appendDirtyJavaScript("		writeCookie(\"PageID_clearAllocation1\", \"FALSE\", '', COOKIE_PATH); \n");
      appendDirtyJavaScript("		f.submit();\n");
      appendDirtyJavaScript("     } \n");
      appendDirtyJavaScript("} \n");

      appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(clearAlloc1);
      appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"PageID_clearAllocation2\")==\"TRUE\")\n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript("      if (confirm(\"");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2SERVICEMGTCLEARALLOC: Clear Allocations on selected line(s)"));
      appendDirtyJavaScript("\")) {\n");
      appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"EEEE\";\n");
      appendDirtyJavaScript("		writeCookie(\"PageID_clearAllocation2\", \"FALSE\", '', COOKIE_PATH); \n");
      appendDirtyJavaScript("		f.submit();\n");
      appendDirtyJavaScript("     } \n");
      appendDirtyJavaScript("} \n");

      appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(clearAlloc2);
      appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"PageID_clearAllocation3\")==\"TRUE\")\n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript("      if (confirm(\"");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2SERVICEMGTCLEARALLOC: Clear Allocations on selected line(s)"));
      appendDirtyJavaScript("\")) {\n");
      appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"FFFF\";\n");
      appendDirtyJavaScript("		writeCookie(\"PageID_clearAllocation3\", \"FALSE\", '', COOKIE_PATH); \n");
      appendDirtyJavaScript("		f.submit();\n");
      appendDirtyJavaScript("     } \n");
      appendDirtyJavaScript("} \n");
      //------
      appendDirtyJavaScript("if ('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript(unissue)); // Bug Id 68773
      appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"PageID_my_cookie1\")==\"TRUE\")\n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript("      if (confirm(\"");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2SERVICEMANAGEMENTUNISSUE: All required material has not been issued"));
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
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2SERVICEMANAGEMENTUNISSUE: All required material has not been issued"));
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
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2SERVICEMANAGEMENTREPAIR: This object is in repair shop. Do you still want to continue?"));
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

      appendDirtyJavaScript("if('");
      appendDirtyJavaScript(transferurl);
      appendDirtyJavaScript("' != \"\")\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" index1 =  '");
      appendDirtyJavaScript(transferurl);
      appendDirtyJavaScript("'.lastIndexOf('.page');\n");
      appendDirtyJavaScript(" index2 =  '");
      appendDirtyJavaScript(transferurl);
      appendDirtyJavaScript("'.lastIndexOf('/')+1;\n");
      appendDirtyJavaScript(" handle =  '");
      appendDirtyJavaScript(transferurl);
      appendDirtyJavaScript("'.slice(index2,index1);\n");
      appendDirtyJavaScript(" window.open('");
      appendDirtyJavaScript(transferurl);
      appendDirtyJavaScript("',handle,\"alwaysRaised=yes,resizable,status=yes,scrollbars=yes,width=650,height=450\");\n");
      appendDirtyJavaScript(" transferurl = \"\";\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovMchCode(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("if(params) param = params;\n");
      appendDirtyJavaScript("else param = '';\n");
      appendDirtyJavaScript("var enable_multichoice = HEAD_IN_FIND_MODE;\n");
      appendDirtyJavaScript("var key_value = (getValue_('MCH_CODE',i).indexOf('%') !=-1)? getValue_('MCH_CODE',i):'';\n");
      appendDirtyJavaScript("if( HEAD_IN_FIND_MODE )\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	  openLOVWindow('MCH_CODE',i,\n");
      appendDirtyJavaScript("                 '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=MAINTENANCE_OBJECT&__FIELD=Object+ID&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                 + '&__KEY_VALUE=' + URLClientEncode(getValue_('MCH_CODE',i))\n"); 
      appendDirtyJavaScript("                 + '&MCH_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                 + '&CONTRACT=' + URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	   		,550,500,'validateMchCode');\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("else\n");
      appendDirtyJavaScript("{\n");    
      appendDirtyJavaScript("  jCont = getValue_('MCH_CODE_CONTRACT',i);\n");
      appendDirtyJavaScript("  if(jCont)\n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("       objwherecond = \" OBJ_LEVEL IS NULL OR (OBJ_LEVEL IS NOT NULL AND Equipment_Object_Level_API.Get_Create_Wo(OBJ_LEVEL) = 'TRUE')\";\n");
      appendDirtyJavaScript("  	    jLov = 'MaintenanceObjectLov1.page?CONTRACT=' +  URLClientEncode(getValue_('MCH_CODE_CONTRACT',i)) +'&CONNECTION_TYPE=' +  URLClientEncode(getValue_('CONNECTION_TYPE',i)) +'&__WHERE='+ objwherecond ;\n");
      appendDirtyJavaScript("       openLOVWindow('MCH_CODE',i,jLov");     
      appendDirtyJavaScript("       ,600,450,'validateMchCode');\n");
      appendDirtyJavaScript("  }\n");
      appendDirtyJavaScript("  else\n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("       objwherecond = \" CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT) AND OBJ_LEVEL IS NULL OR (OBJ_LEVEL IS NOT NULL AND Equipment_Object_Level_API.Get_Create_Wo(OBJ_LEVEL) = 'TRUE')\";\n");
      appendDirtyJavaScript("  	    jLov = 'MaintenanceObjectLov1.page?CONNECTION_TYPE=' +  URLClientEncode(getValue_('CONNECTION_TYPE',i)) +'&__WHERE='+ objwherecond ;\n");
      appendDirtyJavaScript("       openLOVWindow('MCH_CODE',i,jLov");     
      appendDirtyJavaScript("       ,600,450,'validateMchCode');\n");
      appendDirtyJavaScript("  }\n");
      appendDirtyJavaScript("}\n");    
      appendDirtyJavaScript("}\n");


       appendDirtyJavaScript("window.name = \"prepareWorkOrdSM\"\n");

      appendDirtyJavaScript("function lovReportedBy(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  if('");
      appendDirtyJavaScript(isFind); // XSS_Safe ILSOLK 20070713
      appendDirtyJavaScript("' == 'True')\n");
      appendDirtyJavaScript("  { \n");
      appendDirtyJavaScript("    openLOVWindow('REPORTED_BY',i,\n");
      appendDirtyJavaScript("	'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
      appendDirtyJavaScript(fldTitleReportedBy);
      appendDirtyJavaScript("&__TITLE=");
      appendDirtyJavaScript(lovTitleReportedBy);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("	,600,450,'validateReportedBy');\n");
      appendDirtyJavaScript("  }	\n");
      appendDirtyJavaScript("  else\n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("    openLOVWindow('REPORTED_BY',i,\n");
      appendDirtyJavaScript("	'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
      appendDirtyJavaScript(fldTitleReportedBy);
      appendDirtyJavaScript("&__TITLE=");
      appendDirtyJavaScript(lovTitleReportedBy);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("	+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
      appendDirtyJavaScript("	,600,450,'validateReportedBy');\n");
      appendDirtyJavaScript("  }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovWorkLeaderSign(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  if('");
      appendDirtyJavaScript(isFind); // XSS_Safe ILSOLK 20070713
      appendDirtyJavaScript("' == 'True')\n");
      appendDirtyJavaScript("  { \n");
      appendDirtyJavaScript("    openLOVWindow('WORK_LEADER_SIGN',i,\n");
      appendDirtyJavaScript("   	'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
      appendDirtyJavaScript(fldTitleWorkLeaderSign);
      appendDirtyJavaScript("&__TITLE=");
      appendDirtyJavaScript(lovTitleWorkLeaderSign);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript(" 	,600,450,'validateWorkLeaderSign');\n");
      appendDirtyJavaScript("  }\n");
      appendDirtyJavaScript("  else\n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("    openLOVWindow('WORK_LEADER_SIGN',i,\n");
      appendDirtyJavaScript("	'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
      appendDirtyJavaScript(fldTitleWorkLeaderSign);
      appendDirtyJavaScript("&__TITLE=");
      appendDirtyJavaScript(lovTitleWorkLeaderSign);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("	+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
      appendDirtyJavaScript("	,600,450,'validateWorkLeaderSign');\n");
      appendDirtyJavaScript("  } 	\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovPreparedBy(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  if('");
      appendDirtyJavaScript(isFind); // XSS_Safe ILSOLK 20070713
      appendDirtyJavaScript("' == 'True')\n");
      appendDirtyJavaScript("  { \n");
      appendDirtyJavaScript("     openLOVWindow('PREPARED_BY',i,\n");
      appendDirtyJavaScript("      	'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
      appendDirtyJavaScript(fldTitlePreparedBy);
      appendDirtyJavaScript("&__TITLE=");
      appendDirtyJavaScript(lovTitlePreparedBy);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("	 ,600,450,'validatePreparedBy');\n");
      appendDirtyJavaScript("  }\n");
      appendDirtyJavaScript("  else\n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("     openLOVWindow('PREPARED_BY',i,\n");
      appendDirtyJavaScript("      	'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
      appendDirtyJavaScript(fldTitlePreparedBy);
      appendDirtyJavaScript("&__TITLE=");
      appendDirtyJavaScript(lovTitlePreparedBy);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("	+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
      appendDirtyJavaScript("	,600,450,'validatePreparedBy');\n");
      appendDirtyJavaScript("  }		\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovWorkMasterSign(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  if('");
      appendDirtyJavaScript(isFind); // XSS_Safe ILSOLK 20070713
      appendDirtyJavaScript("' == 'True')\n");
      appendDirtyJavaScript("  { \n");
      appendDirtyJavaScript("    openLOVWindow('WORK_MASTER_SIGN',i,\n");
      appendDirtyJavaScript("	'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=MAINT_EMPLOYEE&__FIELD=");
      appendDirtyJavaScript(fldTitleWorkMasterSign);
      appendDirtyJavaScript("&__TITLE=");
      appendDirtyJavaScript(lovTitleWorkMasterSign);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("	,600,450,'validateWorkMasterSign');\n");
      appendDirtyJavaScript("  }\n");
      appendDirtyJavaScript("  else\n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("    openLOVWindow('WORK_MASTER_SIGN',i,\n");
      appendDirtyJavaScript("	'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=MAINT_EMPLOYEE&__FIELD=");
      appendDirtyJavaScript(fldTitleWorkMasterSign);
      appendDirtyJavaScript("&__TITLE=");
      appendDirtyJavaScript(lovTitleWorkMasterSign);
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("	+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
      appendDirtyJavaScript("	,600,450,'validateWorkMasterSign');\n");
      appendDirtyJavaScript("  }	\n");
      appendDirtyJavaScript("}\n");
      //Bug 70948, start
      appendDirtyJavaScript("function lovSign(i,params)\n");
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
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM2_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('SIGN',i).indexOf('%') !=-1)? getValue_('SIGN',i):'';\n");
      appendDirtyJavaScript("	if( getValue_('ROLE_CODE',i)=='' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("              openLOVWindow('SIGN',i,\n");
      appendDirtyJavaScript("                  '../mscomw/MaintEmployeeLov.page?__FIELD=Executed+By&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('SIGN',i))\n"); 
      appendDirtyJavaScript("                  + '&PERSON_ID=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM2_ORG_CODE',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM2_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&CONTRACT=' + URLClientEncode(getValue_('ITEM2_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateSign');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("       {\n");        
      appendDirtyJavaScript("              openLOVWindow('SIGN',i,\n");
      appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Executed+By&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('SIGN',i))\n"); 
      appendDirtyJavaScript("                  + '&SIGNATURE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM2_ORG_CODE',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM2_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM2_CONTRACT',i))\n");       
      appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateSign');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("}\n");
      //Bug 70948, end

      appendDirtyJavaScript("function lovTestPointId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if('");
      appendDirtyJavaScript(isFind);
      appendDirtyJavaScript("' == 'True')\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("     openLOVWindow('TEST_POINT_ID',i,\n");
      appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EQUIPMENT_OBJECT_TEST_PNT&__FIELD=Test+point&__INIT=1'\n");
      appendDirtyJavaScript("		,600,450,'validateTestPointId');\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("     openLOVWindow('TEST_POINT_ID',i,\n");
      appendDirtyJavaScript("        '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EQUIPMENT_OBJECT_TEST_PNT&__FIELD=Test+point&__INIT=1'\n");
      appendDirtyJavaScript("        + '&CONTRACT=' + URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))\n");
      appendDirtyJavaScript("        + '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
      appendDirtyJavaScript("        ,600,450,'validateTestPointId');\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      //Bug 85045, Start, Set Maint Org Site to mandatory if Maint Org, Executed By or Craft has values
      appendDirtyJavaScript("function checkMandoItem2()\n");
      appendDirtyJavaScript("{\n");      
      appendDirtyJavaScript("      if ((f.ITEM2_ORG_CODE.value != '' || f.ROLE_CODE.value != '' || f.SIGN.value != '' || f.SIGN_ID.value != '') && f.ITEM2_CONTRACT.value == '')  \n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWACTIVESEPARATE2SERVICEMANAGEMENTORGMASITEMAN: The field [Maint. Org. Site] must have a value"));
      appendDirtyJavaScript("          ');\n");
      appendDirtyJavaScript("   return false;\n"); 
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript(" return checkRowNo(0);\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function checkItem9Fields()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("      if ((f.ITEM9_ORG_CODE.value != '' || f.ITEM9_ROLE_CODE.value != '' || f.ITEM9_SIGN.value != '' || f.ITEM9_SIGN_ID.value != '') && f.ITEM9_CONTRACT.value == '')  \n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWACTIVESEPARATE2SERVICEMANAGEMENTORGMASITEMAN: The field [Maint. Org. Site] must have a value"));
      appendDirtyJavaScript("          ');\n");
      appendDirtyJavaScript("   return false;\n"); 
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("  return checkItem9Sign(0) &&\n");
      appendDirtyJavaScript("  checkItem9DateFrom(0) &&\n");
      appendDirtyJavaScript("  checkItem9DateTo(0);\n");
      appendDirtyJavaScript("}\n");
      //Bug 85045, End

      appendDirtyJavaScript("function checkMandoItem3()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  return checkPartNo(0) &&\n");
      appendDirtyJavaScript("  checkSpareContract(0) &&\n");
      appendDirtyJavaScript("  checkQtyRequired(0) &&\n");
      appendDirtyJavaScript("  checkDateRequired(0);\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateCustomerNo(i)\n");
      appendDirtyJavaScript("{   \n");
      appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkCustomerNo(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('CUSTOMER_NO',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('CREDITSTOP',i).value = '';\n");
      appendDirtyJavaScript("		getField_('CUSTOMERDESCRIPTION',i).value = '';\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=CUSTOMER_NO'\n");
      appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
      appendDirtyJavaScript("		+ '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
      appendDirtyJavaScript("		+ '&WO_NO=' + URLClientEncode(getValue_('WO_NO',i))\n");

      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("    retStr = r.split(\"^\");\n");
      appendDirtyJavaScript("    if(retStr[0] == ' 1')\n");
      appendDirtyJavaScript("    {   \n");
      appendDirtyJavaScript("        message = '");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2SERVICEMANAGEMENTMSSG: Customer is Credit blocked"));
      appendDirtyJavaScript("';\n");
      appendDirtyJavaScript("        window.alert(message);\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    if( checkStatus_(r,'CUSTOMER_NO',i,'Customer No') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('CREDITSTOP',i,0);\n");
      appendDirtyJavaScript("		assignValue_('CUSTOMERDESCRIPTION',i,1);\n");
      appendDirtyJavaScript("		assignValue_('UPDATE_PRICE',i,2);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateItem5WorkOrderCostType(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM5',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkItem5WorkOrderCostType(i) ) return;\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM5_WORK_ORDER_COST_TYPE'\n");
      appendDirtyJavaScript("		+ '&ITEM5_WORK_ORDER_COST_TYPE=' + getField_('ITEM5_WORK_ORDER_COST_TYPE',i).options[getField_('ITEM5_WORK_ORDER_COST_TYPE',i).selectedIndex].value\n");
      appendDirtyJavaScript("		+ '&WORK_ORDER_INVOICE_TYPE=' + getField_('WORK_ORDER_INVOICE_TYPE',i).options[getField_('WORK_ORDER_INVOICE_TYPE',i).selectedIndex].value + '&ITEM5COST=' + URLClientEncode(getValue_('ITEM5COST',i))\n");

      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'ITEM5_WORK_ORDER_COST_TYPE',i,'Work Order Cost Type') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('ITEM5COST',i,0);\n");
      appendDirtyJavaScript("		assignValue_('CLIENTVAL1',i,1);\n");
      appendDirtyJavaScript("		assignValue_('CLIENTVAL0',i,2);\n");
      appendDirtyJavaScript("		assignValue_('CLIENTVAL4',i,3);\n");
      appendDirtyJavaScript("		assignSelectBoxValue_('WORK_ORDER_INVOICE_TYPE',i,4);\n");
      appendDirtyJavaScript("		assignValue_('CLIENTVAL5',i,5);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("        if(getField_('ITEM5_WORK_ORDER_COST_TYPE',i).selectedIndex == 1)\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("     window.alert('");
      appendDirtyJavaScript(mgr.translateJavaScript("Registration of Personnal costs is not allowed in this tab."));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("          getField_('ITEM5_WORK_ORDER_COST_TYPE',i).value = '';\n");
      appendDirtyJavaScript("          getField_('ITEM5COST',i).value = '';\n");
      appendDirtyJavaScript("          getField_('CLIENTVAL1',i).value = '';\n");
      appendDirtyJavaScript("          getField_('CLIENTVAL0',i).value = '';\n");
      appendDirtyJavaScript("          getField_('CLIENTVAL4',i).value = '';\n");
      appendDirtyJavaScript("          getField_('CLIENTVAL5',i).value = '';\n");
      appendDirtyJavaScript("          getField_('WORK_ORDER_INVOICE_TYPE',i).value = '';                                                  \n");
      appendDirtyJavaScript("        }     \n");
      appendDirtyJavaScript("        if(getField_('ITEM5_WORK_ORDER_COST_TYPE',i).selectedIndex == 2)\n");
      appendDirtyJavaScript("        {\n");
      appendDirtyJavaScript("     window.alert('");
      appendDirtyJavaScript(mgr.translateJavaScript("Registration of Material costs is not allowed in this tab."));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("          getField_('ITEM5_WORK_ORDER_COST_TYPE',i).value = '';\n");
      appendDirtyJavaScript("          getField_('ITEM5COST',i).value = '';\n");
      appendDirtyJavaScript("          getField_('CLIENTVAL1',i).value = '';\n");
      appendDirtyJavaScript("          getField_('CLIENTVAL0',i).value = '';\n");
      appendDirtyJavaScript("          getField_('CLIENTVAL4',i).value = '';\n");
      appendDirtyJavaScript("          getField_('CLIENTVAL5',i).value = '';\n");
      appendDirtyJavaScript("          getField_('WORK_ORDER_INVOICE_TYPE',i).value = '';\n");
      appendDirtyJavaScript("        }  \n");
      appendDirtyJavaScript("        if(getField_('ITEM5_WORK_ORDER_COST_TYPE',i).selectedIndex == 6)\n");
      appendDirtyJavaScript("        {\n");
      appendDirtyJavaScript("     window.alert('");
      appendDirtyJavaScript(mgr.translateJavaScript("Registration of Tools & Facility costs is not allowed in this tab."));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("          getField_('ITEM5_WORK_ORDER_COST_TYPE',i).value = '';\n");
      appendDirtyJavaScript("          getField_('ITEM5COST',i).value = '';\n");
      appendDirtyJavaScript("          getField_('CLIENTVAL1',i).value = '';\n");
      appendDirtyJavaScript("          getField_('CLIENTVAL0',i).value = '';\n");
      appendDirtyJavaScript("          getField_('CLIENTVAL4',i).value = '';\n");
      appendDirtyJavaScript("          getField_('CLIENTVAL5',i).value = '';\n");
      appendDirtyJavaScript("          getField_('WORK_ORDER_INVOICE_TYPE',i).value = '';\n");
      appendDirtyJavaScript("        }  \n");

      appendDirtyJavaScript("	if(getField_('ITEM5_WORK_ORDER_COST_TYPE',i).selectedIndex == 5)\n");
      appendDirtyJavaScript("          f.ITEM5COST.readOnly=true;\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function checkItem5Fields(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	fld1 = getField_('CLIENTVAL5',i);\n");
      appendDirtyJavaScript("	if (fld1.value == \"FL\" )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("	   return checkPlanLineNo(i) &&\n");
      appendDirtyJavaScript("	   checkItem5WorkOrderCostType(i) &&\n");
      appendDirtyJavaScript("	   checkDateCreated(i) &&\n");
      appendDirtyJavaScript("	   checkPlanDate(i) &&\n");
      appendDirtyJavaScript("	   checkQuantity(i) &&\n");
      appendDirtyJavaScript("	   checkQtyToInvoice(i) &&\n");
      appendDirtyJavaScript("	   checkItem5CatalogContract(i) &&\n");
      appendDirtyJavaScript("	   checkItem5CatalogNo(i) &&\n");
      appendDirtyJavaScript("	   checkItem5Catalogdesc(i) &&\n");
      appendDirtyJavaScript("	   checkItem5cost(i) &&\n");
      appendDirtyJavaScript("	   checkItem5CostAmount(i) &&\n");
      appendDirtyJavaScript("	   checkItem5SalesPrice(i) &&\n");
      appendDirtyJavaScript("	   checkItem5Discount(i) &&\n");
      appendDirtyJavaScript("	   checkItem5SalesPriceAmount(i) &&\n");
      appendDirtyJavaScript("	   checkItem5PriceListNo(i) &&\n");
      appendDirtyJavaScript("	   checkWorkOrderInvoiceType(i);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	else\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("	   return checkPlanLineNo(i) &&\n");
      appendDirtyJavaScript("	   checkItem5WorkOrderCostType(i) &&\n");
      appendDirtyJavaScript("	   checkDateCreated(i) &&\n");
      appendDirtyJavaScript("	   checkPlanDate(i) &&\n");
      appendDirtyJavaScript("	   checkQuantity(i) &&\n");
      appendDirtyJavaScript("	   checkQtyToInvoice1(i) &&\n");
      appendDirtyJavaScript("	   checkItem5CatalogContract(i) &&\n");
      appendDirtyJavaScript("	   checkItem5CatalogNo(i) &&\n");
      appendDirtyJavaScript("	   checkItem5Catalogdesc(i) &&\n");
      appendDirtyJavaScript("	   checkItem5cost(i) &&\n");
      appendDirtyJavaScript("	   checkItem5CostAmount(i) &&\n");
      appendDirtyJavaScript("	   checkItem5SalesPrice(i) &&\n");
      appendDirtyJavaScript("	   checkItem5Discount(i) &&\n");
      appendDirtyJavaScript("	   checkItem5SalesPriceAmount(i) &&\n");
      appendDirtyJavaScript("	   checkItem5PriceListNo(i) &&\n");
      appendDirtyJavaScript("	   checkWorkOrderInvoiceType(i);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function checkQtyToInvoice(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	fld = getField_('QTY_TO_INVOICE',i);\n");
      appendDirtyJavaScript("	return checkMandatory_(fld,'Qty To Invoice','');\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function checkQtyToInvoice1(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	fld = getField_('QTY_TO_INVOICE',i);\n");
      appendDirtyJavaScript("	return true;\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validatePartNo(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("f.HIDDENPARTNO.value = \"\";\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkPartNo(i) ) return;\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=PART_NO'\n");
      appendDirtyJavaScript("		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
      appendDirtyJavaScript("		+ '&SPARE_CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM_CATALOG_CONTRACT=' + URLClientEncode(getValue_('ITEM_CATALOG_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&OWNER=' + URLClientEncode(getValue_('OWNER',i))\n");
      appendDirtyJavaScript("		+ '&CONDITION_CODE=' + URLClientEncode(getValue_('CONDITION_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM3_ACTIVITY_SEQ=' + URLClientEncode(getValue_('ITEM3_ACTIVITY_SEQ',i))\n");
      appendDirtyJavaScript("         + '&SUPPLY_CODE=' + SelectURLClientEncode('SUPPLY_CODE',i)		+ '&ITEM0_MAINT_MATERIAL_ORDER_NO=' + URLClientEncode(getValue_('ITEM0_MAINT_MATERIAL_ORDER_NO',i))");
      appendDirtyJavaScript("		+ '&WO_NO=' + URLClientEncode(document.form.ITEM3_WO_NO.value)\n");
      appendDirtyJavaScript("		+ '&PART_OWNERSHIP=' + URLClientEncode(getValue_('PART_OWNERSHIP',i))\n");      
      appendDirtyJavaScript("		+ '&ITEM_WO_NO=' + URLClientEncode(getValue_('ITEM_WO_NO',i))\n");         
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'PART_NO',i,'Part No') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('ITEM_CATALOG_NO',i,0);\n");
      appendDirtyJavaScript("		assignValue_('ITEMCATALOGDESC',i,1);\n");
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
      appendDirtyJavaScript("f.HIDDENPARTNO.value = f.PART_NO.value;\n");
      appendDirtyJavaScript("f.ONCEGIVENERROR.value = \"TRUE\";\n");
      appendDirtyJavaScript("getField_('PART_NO',i).value = '';\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("   if (f.ACTIVEIND_DB.value == 'N') \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWACTIVESEPARATE2SERVICEMANAGEMENTINVSALESPART: All sale parts connected to the part are inactive."));
      appendDirtyJavaScript("          ');\n");
      appendDirtyJavaScript("      f.ITEM_CATALOG_NO.value = ''; \n");
      appendDirtyJavaScript("      f.ITEMCATALOGDESC.value = ''; \n");   
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("        validateItemCatalogNo(i);\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovRoleCode(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript(" if ((document.form.SIGN_ID.value != '') && (document.form.ITEM2_COMPANY.value != '')) \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if((document.form.ITEM2_ORG_CODE.value == '') && (document.form.ITEM2_CONTRACT.value == ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.SIGN_ID.value) + \"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.SIGN_ID.value) + \"' \";\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM2_ORG_CODE.value != '') && (document.form.ITEM2_CONTRACT.value == ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.SIGN_ID.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM2_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.SIGN_ID.value) + \"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM2_ORG_CODE.value)+\"' \";\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM2_ORG_CODE.value == '') && (document.form.ITEM2_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.SIGN_ID.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM2_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.SIGN_ID.value) + \"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM2_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM2_ORG_CODE.value != '') && (document.form.ITEM2_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.SIGN_ID.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM2_ORG_CODE.value)+ \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM2_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.SIGN_ID.value) + \"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM2_ORG_CODE.value)+\"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM2_CONTRACT.value)+\"' \";\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("	   whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.ITEM2_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript("	   whereCond2 = \" Site_API.Get_Company(MAINT_ORG_CONTRACT) = '\" + URLClientEncode(document.form.ITEM2_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if (document.form.TEAM_ID.value != '')\n");
      appendDirtyJavaScript("	   whereCond1 += \" AND 1 IN (SELECT Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,ROLE_CODE ,MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.TEAM_ID.value+\"' AND CONTRACT = '\"+ document.form.TEAM_CONTRACT.value+\"' AND \" +whereCond2 +\")\";\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM2_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ROLE_CODE',i).indexOf('%') !=-1)? getValue_('ROLE_CODE',i):'';\n");
      appendDirtyJavaScript("	if( getValue_('SIGN_ID',i)=='' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("                  openLOVWindow('ROLE_CODE',i,\n");
      appendDirtyJavaScript("                     '../mscomw/RoleToSiteLov.page?__FIELD=Craft+ID&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n"); 
      appendDirtyJavaScript("                      + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM2_COMPANY',i))\n");
      appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM2_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	             ,550,500,'validateRoleCode');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("       {\n");        
      appendDirtyJavaScript("              openLOVWindow('ROLE_CODE',i,\n");
      appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Craft+ID&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n"); 
      appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM2_ORG_CODE',i))\n");
      appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('SIGN_ID',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM2_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM2_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateRoleCode');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateRoleCode(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    if( getRowStatus_('ITEM2',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("    setDirty();\n");
      appendDirtyJavaScript("    if( !checkRoleCode(i) ) return;\n");
      appendDirtyJavaScript("    if( getValue_('ROLE_CODE',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("    if( getValue_('ITEM2_CONTRACT',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("    if( getValue_('ITEM2_ORG_CODE',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("    if( getValue_('ORG_CODE',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("    if( getValue_('COST',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("    if( getValue_('CATALOG_NO',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("    if( getValue_('PLAN_MEN',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("    if( getValue_('ITEM2_PLAN_HRS',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("    if( getValue_('SIGN',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("    if( getValue_('ITEM2_WO_NO',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("    if( getValue_('CATALOG_CONTRACT',i).indexOf('%') != -1) return;\n");
      appendDirtyJavaScript("    if( getValue_('ROLE_CODE',i)=='' )\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("        getField_('CATALOG_NO',i).value = '';\n");
      appendDirtyJavaScript("        getField_('COST',i).value = '';\n");
      appendDirtyJavaScript("        getField_('COSTAMOUNT',i).value = '';\n");
      appendDirtyJavaScript("        getField_('CATALOGDESC',i).value = '';\n");
      appendDirtyJavaScript("        getField_('ROLE_CODE',i).value = '';\n");
      appendDirtyJavaScript("        getField_('ITEM2_CONTRACT',i).value = '';\n");
      appendDirtyJavaScript("        getField_('ROLE_CODE_DESC',i).value = '';\n");
      appendDirtyJavaScript("        return;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    window.status='Please wait for validation';\n");
      appendDirtyJavaScript("     r = __connect(\n");
      appendDirtyJavaScript("            APP_ROOT+ 'pcmw/ActiveSeparate2ServiceManagement.page'+'?VALIDATE=ROLE_CODE'\n");
      appendDirtyJavaScript("            + '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
      appendDirtyJavaScript("            + '&ITEM2_CONTRACT=' + URLClientEncode(getValue_('ITEM2_CONTRACT',i))\n");
      appendDirtyJavaScript("            + '&ITEM2_ORG_CODE=' + URLClientEncode(getValue_('ITEM2_ORG_CODE',i))\n");
      appendDirtyJavaScript("            + '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
      appendDirtyJavaScript("            + '&COST=' + URLClientEncode(getValue_('COST',i))\n");
      appendDirtyJavaScript("            + '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
      appendDirtyJavaScript("            + '&PLAN_MEN=' + URLClientEncode(getValue_('PLAN_MEN',i))\n");
      appendDirtyJavaScript("            + '&ITEM2_PLAN_HRS=' + URLClientEncode(getValue_('ITEM2_PLAN_HRS',i))\n");
      appendDirtyJavaScript("            + '&SIGN=' + URLClientEncode(getValue_('SIGN',i))\n");
      appendDirtyJavaScript("            + '&ITEM2_WO_NO=' + URLClientEncode(getValue_('ITEM2_WO_NO',i))\n");
      appendDirtyJavaScript("            + '&CATALOG_CONTRACT=' + URLClientEncode(getValue_('CATALOG_CONTRACT',i))\n");
      appendDirtyJavaScript("            );\n");
      appendDirtyJavaScript("    window.status='';\n");
      appendDirtyJavaScript("    if( checkStatus_(r,'ROLE_CODE',i,'Craft ID') )\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("        assignValue_('CATALOG_NO',i,0);\n");
      appendDirtyJavaScript("        assignValue_('COST',i,1);\n");
      appendDirtyJavaScript("        assignValue_('COSTAMOUNT',i,2);\n");
      appendDirtyJavaScript("        assignValue_('CATALOGDESC',i,3);\n");
      appendDirtyJavaScript("        assignValue_('ROLE_CODE',i,4);\n");
      appendDirtyJavaScript("        assignValue_('ITEM2_CONTRACT',i,5);\n");
      appendDirtyJavaScript("        assignValue_('ROLE_CODE_DESC',i,6);\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("	   validateCatalogNo(i);\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovItem2OrgCode(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript(" if (document.form.ROLE_CODE.value != '') \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if((document.form.SIGN_ID.value == '') && (document.form.ITEM2_CONTRACT.value == ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ROLE_CODE.value) + \"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.SIGN_ID.value == '') && (document.form.ITEM2_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ROLE_CODE.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM2_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM2_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.SIGN_ID.value != '') && (document.form.ITEM2_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ROLE_CODE.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM2_CONTRACT.value)+\"' AND EMP_NO = '\" +URLClientEncode(document.form.SIGN_ID.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM2_CONTRACT.value)+\"' AND MEMBER_EMP_NO = '\" +URLClientEncode(document.form.SIGN_ID.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else if ((document.form.ROLE_CODE.value == '') && (document.form.SIGN_ID.value != '')) \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if (document.form.ITEM2_CONTRACT.value == '') \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMPLOYEE_ID = '\" +URLClientEncode(document.form.SIGN_ID.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" +URLClientEncode(document.form.SIGN_ID.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMPLOYEE_ID = '\" +URLClientEncode(document.form.SIGN_ID.value)+\"' AND CONTRACT = '\" +URLClientEncode(document.form.ITEM2_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" +URLClientEncode(document.form.SIGN_ID.value)+\"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM2_CONTRACT.value)+\"'\";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else if ((document.form.ROLE_CODE.value == '') && (document.form.SIGN_ID.value == '')) \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if (document.form.ITEM2_CONTRACT.value != '') \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" CONTRACT = '\" +URLClientEncode(document.form.ITEM2_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM2_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if (document.form.ITEM2_CONTRACT.value != '') \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" CONTRACT = '\" +URLClientEncode(document.form.ITEM2_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM2_CONTRACT.value)+\"' \";\n"); 
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
      appendDirtyJavaScript("	   whereCond1 += \" ORG_CODE IN (SELECT MAINT_ORG FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.TEAM_ID.value+\"' AND CONTRACT = '\"+ document.form.TEAM_CONTRACT.value+\"'\";\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("	   whereCond1 += \" (ORG_CODE,EMP_NO,1) IN (SELECT MAINT_ORG,MEMBER_EMP_NO,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\"+ document.form.ROLE_CODE.value+\"',MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.TEAM_ID.value+\"' AND CONTRACT='\" + URLClientEncode(document.form.TEAM_CONTRACT.value) +\"' \";\n"); 
      appendDirtyJavaScript(" if(whereCond2 != '' )\n");
      appendDirtyJavaScript("	           whereCond1 += \" AND \" +whereCond2 +\" \";\n"); 
      appendDirtyJavaScript("	whereCond1 += \")\";\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM2_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM2_ORG_CODE',i).indexOf('%') !=-1)? getValue_('ITEM2_ORG_CODE',i):'';\n");
      appendDirtyJavaScript("	if( getValue_('ROLE_CODE',i)=='' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("	            if( getValue_('SIGN',i)=='' )\n");
      appendDirtyJavaScript("             {\n");
      appendDirtyJavaScript("                  openLOVWindow('ITEM2_ORG_CODE',i,\n");
      appendDirtyJavaScript("                     '../mscomw/OrgCodeAllowedSiteLov.page?__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM2_ORG_CODE',i))\n"); 
      appendDirtyJavaScript("                      + '&ORG_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM2_COMPANY',i))\n");
      appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM2_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	             ,550,500,'validateItem2OrgCode');\n");
      appendDirtyJavaScript("             }\n");
      appendDirtyJavaScript("	            else\n");
      appendDirtyJavaScript("             {\n");
      appendDirtyJavaScript("                 openLOVWindow('ITEM2_ORG_CODE',i,\n");
      appendDirtyJavaScript("                     '../mscomw/MaintEmployeeLov.page?&__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                     + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM2_ORG_CODE',i))\n"); 
      appendDirtyJavaScript("                     + '&ORG_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                     + '&EMPLOYEE_ID=' + URLClientEncode(getValue_('SIGN_ID',i))\n");
      appendDirtyJavaScript("                     + '&COMPANY=' + URLClientEncode(getValue_('ITEM2_COMPANY',i))\n");
      appendDirtyJavaScript("                     + '&CONTRACT=' + URLClientEncode(getValue_('ITEM2_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	            ,550,500,'validateItem2OrgCode');\n");
      appendDirtyJavaScript("             }\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("       {\n");        
      appendDirtyJavaScript("              openLOVWindow('ITEM2_ORG_CODE',i,\n");
      appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM2_ORG_CODE',i))\n"); 
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('SIGN_ID',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM2_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM2_CONTRACT',i))\n");       
      appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateItem2OrgCode');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateItem2OrgCode(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM2',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkItem2OrgCode(i) ) return;\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM2_ORG_CODE'\n");
      appendDirtyJavaScript("		+ '&ITEM2_CONTRACT=' + URLClientEncode(getValue_('ITEM2_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM2_ORG_CODE=' + URLClientEncode(getValue_('ITEM2_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
      appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&CATALOG_CONTRACT=' + URLClientEncode(getValue_('CATALOG_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&CATALOGDESC=' + URLClientEncode(getValue_('CATALOGDESC',i))\n");
      appendDirtyJavaScript("		+ '&COST=' + URLClientEncode(getValue_('COST',i))\n");
      appendDirtyJavaScript("		+ '&COSTAMOUNT=' + URLClientEncode(getValue_('COSTAMOUNT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM2_PLAN_HRS=' + URLClientEncode(getValue_('ITEM2_PLAN_HRS',i))\n");
      appendDirtyJavaScript("		+ '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&PLAN_MEN=' + URLClientEncode(getValue_('PLAN_MEN',i))\n");
      appendDirtyJavaScript("		+ '&SIGN=' + URLClientEncode(getValue_('SIGN',i))\n");
      appendDirtyJavaScript("		+ '&ITEM2_WO_NO=' + URLClientEncode(getValue_('ITEM2_WO_NO',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'ITEM2_ORG_CODE',i,'Maintenance Organization') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('CATALOG_NO',i,0);\n");
      appendDirtyJavaScript("		assignValue_('CATALOGDESC',i,1);\n");
      appendDirtyJavaScript("		assignValue_('COST',i,2);\n");
      appendDirtyJavaScript("		assignValue_('COSTAMOUNT',i,3);\n");
      appendDirtyJavaScript("		assignValue_('ITEM2_ORG_CODE',i,4);\n");
      appendDirtyJavaScript("		assignValue_('ITEM2_CONTRACT',i,5);\n");
      appendDirtyJavaScript("		assignValue_('ITEM2_ORG_CODE_DESC',i,6);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("if(  getField_('ROLE_CODE',i).value ==  '')\n");
      appendDirtyJavaScript("	validateCatalogNo(i);\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovItem2Contract(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript(" if (document.form.ITEM2_COMPANY.value != '') \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("	      whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.ITEM2_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM2_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM2_CONTRACT',i).indexOf('%') !=-1)? getValue_('ITEM2_CONTRACT',i):'';\n");
      appendDirtyJavaScript("                  openLOVWindow('ITEM2_CONTRACT',i,\n");
      appendDirtyJavaScript("'");
      appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=USER_ALLOWED_SITE_LOV&__FIELD=Maint.Org.+Site&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM2_CONTRACT',i))\n");
      appendDirtyJavaScript("+ '&ITEM2_CONTRACT=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript(",550,500,'validateItem2Contract');\n");
      appendDirtyJavaScript("}\n");


      /*appendDirtyJavaScript("function validateCatalogNo(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM2',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkCatalogNo(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('CATALOG_NO',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('CATALOGDESC',i).value = '';\n");
      appendDirtyJavaScript("		getField_('PRICE_LIST_NO',i).value = '';\n");
      appendDirtyJavaScript("		getField_('DISCOUNT',i).value = '';\n");
      appendDirtyJavaScript("		getField_('LISTPRICE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('SALESPRICEAMOUNT',i).value = '';\n");
      appendDirtyJavaScript("		getField_('DISCOUNTED_PRICE',i).value = '';\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=CATALOG_NO'\n");
      appendDirtyJavaScript("		+ '&CATALOG_CONTRACT=' + URLClientEncode(getValue_('CATALOG_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&PRICE_LIST_NO=' + URLClientEncode(getValue_('PRICE_LIST_NO',i))\n");
      appendDirtyJavaScript("		+ '&PLAN_MEN=' + URLClientEncode(getValue_('PLAN_MEN',i))\n");
      appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("		+ '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
      appendDirtyJavaScript("		+ '&ITEM2_CONTRACT=' + URLClientEncode(getValue_('ITEM2_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM2_ORG_CODE=' + URLClientEncode(getValue_('ITEM2_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM2_PLAN_HRS=' + URLClientEncode(getValue_('ITEM2_PLAN_HRS',i))\n");
      appendDirtyJavaScript("		+ '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
      appendDirtyJavaScript("		+ '&HEAD_CURRENCEY_CODE=' + URLClientEncode(getValue_('HEAD_CURRENCEY_CODE',i))\n");
      appendDirtyJavaScript("		+ '&DISCOUNTED_PRICE=' + URLClientEncode(getValue_('DISCOUNTED_PRICE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM2_ROWSTATUS=' + URLClientEncode(getValue_('ITEM2_ROWSTATUS',i))\n");
      appendDirtyJavaScript("		+ '&ITEM2_WO_NO=' + URLClientEncode(getValue_('ITEM2_WO_NO',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'CATALOG_NO',i,'Sales Part') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('CATALOGDESC',i,0);\n");
      appendDirtyJavaScript("		assignValue_('COST',i,1);\n");
      appendDirtyJavaScript("		assignValue_('COSTAMOUNT',i,2);\n");
      appendDirtyJavaScript("		assignValue_('PRICE_LIST_NO',i,3);\n");
      appendDirtyJavaScript("		assignValue_('DISCOUNT',i,4);\n");
      appendDirtyJavaScript("		assignValue_('LISTPRICE',i,5);\n");
      appendDirtyJavaScript("		assignValue_('SALESPRICEAMOUNT',i,6);\n");
      appendDirtyJavaScript("		assignValue_('DISCOUNTED_PRICE',i,7);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validatePlanMen(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM2',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkPlanMen(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('PLAN_MEN',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('COST',i).value = '';\n");
      appendDirtyJavaScript("		getField_('COSTAMOUNT',i).value = '';\n");
      appendDirtyJavaScript("		getField_('PRICE_LIST_NO',i).value = '';\n");
      appendDirtyJavaScript("		getField_('DISCOUNT',i).value = '';\n");
      appendDirtyJavaScript("		getField_('LISTPRICE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('SALESPRICEAMOUNT',i).value = '';\n");
      appendDirtyJavaScript("		getField_('DISCOUNTED_PRICE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('TOTAL_MAN_HOURS',i).value = '';\n");
      appendDirtyJavaScript("		getField_('TOTAL_REMAINING_HOURS',i).value = '';\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=PLAN_MEN'\n");
      appendDirtyJavaScript("		+ '&PLAN_MEN=' + URLClientEncode(getValue_('PLAN_MEN',i))\n");
      appendDirtyJavaScript("		+ '&ITEM2_PLAN_HRS=' + URLClientEncode(getValue_('ITEM2_PLAN_HRS',i))\n");
      appendDirtyJavaScript("		+ '&COST=' + URLClientEncode(getValue_('COST',i))\n");
      appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM2_CONTRACT=' + URLClientEncode(getValue_('ITEM2_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM2_ORG_CODE=' + URLClientEncode(getValue_('ITEM2_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&CATALOG_CONTRACT=' + URLClientEncode(getValue_('CATALOG_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&PRICE_LIST_NO=' + URLClientEncode(getValue_('PRICE_LIST_NO',i))\n");
      appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("		+ '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
      appendDirtyJavaScript("		+ '&LISTPRICE=' + URLClientEncode(getValue_('LISTPRICE',i))\n");
      appendDirtyJavaScript("		+ '&DISCOUNT=' + URLClientEncode(getValue_('DISCOUNT',i))\n");
      appendDirtyJavaScript("		+ '&DISCOUNTED_PRICE=' + URLClientEncode(getValue_('DISCOUNTED_PRICE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM2_WO_NO=' + URLClientEncode(getValue_('ITEM2_WO_NO',i))\n");
      appendDirtyJavaScript("		+ '&TOTAL_ALLOCATED_HOURS=' + URLClientEncode(getValue_('TOTAL_ALLOCATED_HOURS',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'PLAN_MEN',i,'Planned Men') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('COST',i,0);\n");
      appendDirtyJavaScript("		assignValue_('COSTAMOUNT',i,1);\n");
      appendDirtyJavaScript("		assignValue_('PRICE_LIST_NO',i,2);\n");
      appendDirtyJavaScript("		assignValue_('DISCOUNT',i,3);\n");
      appendDirtyJavaScript("		assignValue_('LISTPRICE',i,4);\n");
      appendDirtyJavaScript("		assignValue_('SALESPRICEAMOUNT',i,5);\n");
      appendDirtyJavaScript("		assignValue_('DISCOUNTED_PRICE',i,6);\n");
      appendDirtyJavaScript("		assignValue_('TOTAL_MAN_HOURS',i,7);\n");
      appendDirtyJavaScript("		assignValue_('TOTAL_REMAINING_HOURS',i,8);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	validateCatalogNo(i);\n");
      appendDirtyJavaScript("}\n");*/

      appendDirtyJavaScript("function validateSign(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM2',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkSign(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('SIGN',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('SIGN_ID',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ROLE_CODE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('CATALOG_NO',i).value = '';\n");
      appendDirtyJavaScript("		getField_('CATALOGDESC',i).value = '';\n");
      appendDirtyJavaScript("		getField_('COST',i).value = '';\n");
      appendDirtyJavaScript("		getField_('COSTAMOUNT',i).value = '';\n");
      appendDirtyJavaScript("		getField_('SIGN',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ALLOCATED_HOURS',i).value = '';\n");
      appendDirtyJavaScript("		getField_('TOTAL_ALLOCATED_HOURS',i).value = '';\n");
      appendDirtyJavaScript("		getField_('TOTAL_REMAINING_HOURS',i).value = '';\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=SIGN'\n");
      appendDirtyJavaScript("		+ '&ITEM2_COMPANY=' + URLClientEncode(getValue_('ITEM2_COMPANY',i))\n");
      appendDirtyJavaScript("		+ '&SIGN=' + URLClientEncode(getValue_('SIGN',i))\n");
      appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM2_CONTRACT=' + URLClientEncode(getValue_('ITEM2_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM2_ORG_CODE=' + URLClientEncode(getValue_('ITEM2_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&COST=' + URLClientEncode(getValue_('COST',i))\n");
      appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&PLAN_MEN=' + URLClientEncode(getValue_('PLAN_MEN',i))\n");
      appendDirtyJavaScript("		+ '&ITEM2_PLAN_HRS=' + URLClientEncode(getValue_('ITEM2_PLAN_HRS',i))\n");
      appendDirtyJavaScript("		+ '&ITEM2_WO_NO=' + URLClientEncode(getValue_('ITEM2_WO_NO',i))\n");
      appendDirtyJavaScript("		+ '&ALLOCATED_HOURS=' + URLClientEncode(getValue_('ALLOCATED_HOURS',i))\n");
      appendDirtyJavaScript("		+ '&TOTAL_MAN_HOURS=' + URLClientEncode(getValue_('TOTAL_MAN_HOURS',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'SIGN',i,'Executed By') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('SIGN_ID',i,0);\n");
      appendDirtyJavaScript("		assignValue_('ROLE_CODE',i,1);\n");
      appendDirtyJavaScript("		assignValue_('ITEM2_CONTRACT',i,2);\n");
      appendDirtyJavaScript("		assignValue_('ITEM2_ORG_CODE',i,3);\n");
      appendDirtyJavaScript("		assignValue_('CATALOG_NO',i,4);\n");
      appendDirtyJavaScript("		assignValue_('CATALOGDESC',i,5);\n");
      appendDirtyJavaScript("		assignValue_('COST',i,6);\n");
      appendDirtyJavaScript("		assignValue_('COSTAMOUNT',i,7);\n");
      appendDirtyJavaScript("		assignValue_('SIGN',i,8);\n");
      appendDirtyJavaScript("		assignValue_('ALLOCATED_HOURS',i,9);\n");
      appendDirtyJavaScript("		assignValue_('TOTAL_ALLOCATED_HOURS',i,10);\n");
      appendDirtyJavaScript("		assignValue_('TOTAL_REMAINING_HOURS',i,11);\n");
      appendDirtyJavaScript("		assignValue_('ITEM2_ORG_CODE_DESC',i,12);\n");
      appendDirtyJavaScript("		assignValue_('ROLE_CODE_DESC',i,13);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");


      // XSS_Safe ILSOLK 20070706
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
      appendDirtyJavaScript(" if(document.form.ITEM2_COMPANY.value != '')\n");
      appendDirtyJavaScript(" if( whereCond1=='')\n");
      appendDirtyJavaScript("		whereCond1 = \"COMPANY = '\" +URLClientEncode(document.form.ITEM2_COMPANY.value)+\"' \";\n"); 
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("		whereCond1 += \" AND COMPANY = '\" +URLClientEncode(document.form.ITEM2_COMPANY.value)+\"' \";\n"); 
      appendDirtyJavaScript(" if( whereCond1 !='')\n");
      appendDirtyJavaScript("	           whereCond1 += \" AND \";\n"); 
      appendDirtyJavaScript(" whereCond1 += \" nvl(to_date(to_char(to_date('\" +URLClientEncode(document.form.DATE_FROM_MASKED.value)+\"','" +getDateTimeFormat("SQL") + "'),\'YYYYMMDD\'),\'YYYYMMDD\'),to_date(to_char(sysdate,\'YYYYMMDD\'),\'YYYYMMDD\')) BETWEEN nvl(VALID_FROM,to_date(\'00010101\',\'YYYYMMDD\')) AND nvl(VALID_TO,to_date(\'99991231\',\'YYYYMMDD\')) \";\n"); 
      appendDirtyJavaScript(" if(document.form.SIGN_ID.value != '')\n");
      appendDirtyJavaScript("		whereCond2 = \"MEMBER_EMP_NO = '\" +URLClientEncode(document.form.SIGN_ID.value)+\"' \";\n"); 
      appendDirtyJavaScript(" if(document.form.ITEM2_ORG_CODE.value != '')\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript(" if( whereCond2=='')\n");
      appendDirtyJavaScript("		whereCond2 = \"MAINT_ORG = '\" +URLClientEncode(document.form.ITEM2_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript(" else \n");
      appendDirtyJavaScript("		whereCond2+= \" AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM2_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if(document.form.ITEM2_CONTRACT.value != '')\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript(" if(whereCond2=='' )\n");
      appendDirtyJavaScript("		whereCond2 = \"MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM2_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript(" else \n");
      appendDirtyJavaScript("		whereCond2 += \" AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM2_CONTRACT.value)+\"' \";\n"); 
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
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM2_IN_FIND_MODE);\n"); 
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
      appendDirtyJavaScript(" if (document.form.ITEM2_COMPANY.value != '') \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("	      whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.ITEM2_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM2_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('TEAM_CONTRACT',i).indexOf('%') !=-1)? getValue_('TEAM_CONTRACT',i):'';\n");
      appendDirtyJavaScript("                  openLOVWindow('TEAM_CONTRACT',i,\n");
      appendDirtyJavaScript("'");
      appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=USER_ALLOWED_SITE_LOV&__FIELD=Team+Site&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('TEAM_CONTRACT',i))\n");
      appendDirtyJavaScript("+ '&TEAM_CONTRACT=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript(",550,500,'validateTeamContract');\n");
      appendDirtyJavaScript("}\n");


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
      appendDirtyJavaScript("		if (f.CONNECTION_TYPE_DB.value == 'VIM'){\n");
      appendDirtyJavaScript("		alert('");
      appendDirtyJavaScript(mgr.translateJavaScript("VIMWORDERHEADERPCMWACTIVESEPARATE2NOVIMALLOWED: Registration of VIM is not allowed."));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("	f.CONNECTION_TYPE.selectedIndex = 1;\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("		}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validatePartOwnership(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
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
      appendDirtyJavaScript("		+ '&CONDITION_CODE=' + URLClientEncode(getValue_('CONDITION_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM3_ACTIVITY_SEQ=' + URLClientEncode(getValue_('ITEM3_ACTIVITY_SEQ',i))\n");
      appendDirtyJavaScript("         + '&SUPPLY_CODE=' + SelectURLClientEncode('SUPPLY_CODE',i)		+ '&ITEM0_MAINT_MATERIAL_ORDER_NO=' + URLClientEncode(getValue_('ITEM0_MAINT_MATERIAL_ORDER_NO',i))");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'PART_OWNERSHIP',i,'Ownership') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('PART_OWNERSHIP_DB',i,0);\n");
      appendDirtyJavaScript("		assignValue_('QTYONHAND',i,1);\n");
      appendDirtyJavaScript("		assignValue_('QTY_AVAILABLE',i,2);\n");
      appendDirtyJavaScript("		if (f.PART_OWNERSHIP_DB.value == 'CONSIGNMENT'){\n");
      appendDirtyJavaScript("		alert('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2INVOWNER1: Ownership type Consignment is not allowed in Materials for Work Orders."));
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
      appendDirtyJavaScript("	  var enable_multichoice =(true && ITEM6_IN_FIND_MODE);\n");
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

      appendDirtyJavaScript("function checkItem6Owner()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (checkItem6Fields())\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if (f.WO_CUST.value != '' && f.OWNER.value != '' && f.WO_CUST.value != f.OWNER.value && f.PART_OWNERSHIP_DB.value == 'CUSTOMER OWNED')  \n");
      appendDirtyJavaScript("         return confirm('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2DIFFCUST: Owning Customer No is not the same as the Work Order Customer No. Still want to save this record?"));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("      else if (f.WO_CUST.value == '' && f.OWNER.value != '' && f.PART_OWNERSHIP_DB.value == 'CUSTOMER OWNED')  \n");
      appendDirtyJavaScript("         return confirm('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2NOCUST: There is no Customer specified for the Work Order although you have entered an Owning Customer No. Still want to save this record?"));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("      else \n");
      appendDirtyJavaScript("         return true;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateOwner(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
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
      appendDirtyJavaScript("		+ '&CONDITION_CODE=' + URLClientEncode(getValue_('CONDITION_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM3_ACTIVITY_SEQ=' + URLClientEncode(getValue_('ITEM3_ACTIVITY_SEQ',i))\n");
      appendDirtyJavaScript("         + '&SUPPLY_CODE=' + SelectURLClientEncode('SUPPLY_CODE',i)		+ '&ITEM0_MAINT_MATERIAL_ORDER_NO=' + URLClientEncode(getValue_('ITEM0_MAINT_MATERIAL_ORDER_NO',i))");
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
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWACTIVESEPARATE2INVOWNER11: Owner should not be specified for Company Owned Stock."));
      appendDirtyJavaScript("          ');\n");
      appendDirtyJavaScript("      f.OWNER.value = ''; \n");
      appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("   if (f.PART_OWNERSHIP.value == 'Consignment' && f.OWNER.value != '') \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWACTIVESEPARATE2INVOWNER12: Owner should not be specified for Consignment Stock."));
      appendDirtyJavaScript("          ');\n");
      appendDirtyJavaScript("      f.OWNER.value = ''; \n");
      appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("   if (f.PART_OWNERSHIP.value == '' && f.OWNER.value != '') \n");
      appendDirtyJavaScript("   { \n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWACTIVESEPARATE2INVOWNER14: Owner should not be specified when there is no Ownership type."));
      appendDirtyJavaScript("          ');\n");
      appendDirtyJavaScript("      f.OWNER.value = ''; \n");
      appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
      appendDirtyJavaScript("   } \n");
      appendDirtyJavaScript("}\n");

      // 031204  ARWILK  Begin  (Replace links with RMB's)
      // XSS_Safe ILSOLK 20070706
      if (bOpenNewWindow)
      {
         appendDirtyJavaScript("  window.open(\"");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));
         appendDirtyJavaScript("\", \"");
         appendDirtyJavaScript(newWinHandle);
         appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=");      
         appendDirtyJavaScript(newWinWidth);
         appendDirtyJavaScript(",height=");
         appendDirtyJavaScript(newWinHeight);
         appendDirtyJavaScript("\"); \n"); 
      }
      // 031204  ARWILK  End  (Replace links with RMB's)

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
      appendDirtyJavaScript("       + '&ITEM7_DATE_TO=' + URLClientEncode(getValue_('ITEM7_DATE_TO',i))\n");
      appendDirtyJavaScript("       + '&ITEM7_DATE_FROM=' + URLClientEncode(getValue_('ITEM7_DATE_FROM',i))\n");
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
      appendDirtyJavaScript("           assignValue_('OUTEXIST',i,12);\n");
      appendDirtyJavaScript("           assignValue_('OVERLAPEXIST',i,13);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   if (getValue_('OUTEXIST',i) == 'TRUE')\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(       mgr.translateJavaScript("PCMWACTIVESEPARATE2SEREMANAGEMENTCANNOTUPDATEDATETO: Job ID end date cannot be a date before the Operation To Date"));
      appendDirtyJavaScript("          ');\n");
      appendDirtyJavaScript("   return false;\n"); 
      appendDirtyJavaScript("   } \n");
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
      appendDirtyJavaScript("   if (getValue_('OVERLAPEXIST',i) == 'TRUE')\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("            if (confirm('");
      appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2SEREMANAGEMENTCANNOTUPDATEDATEFROM: Overlaps with existing booking(s). Still want to save this allocation?"));
      appendDirtyJavaScript("'))\n");
      appendDirtyJavaScript("               return true;\n"); 
      appendDirtyJavaScript("            else\n");
      appendDirtyJavaScript("               return false;\n"); 
      appendDirtyJavaScript("   } \n");
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
      appendDirtyJavaScript("   return confirm('" + mgr.translateJavaScript("ACTSEP2SMHEADERDISCACT: Are you sure you want to remove the Project Connection ?") + "');\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function setActivitySeq(activitySeq)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   getField_('ACTIVITY_SEQ_PROJ', -1).value = activitySeq;\n");
      appendDirtyJavaScript("   commandSet('HEAD.connectToActivity','');\n");
      appendDirtyJavaScript("}\n");

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

      //-----validations for new operations under jobs tab.
      //NOTE : The changes done to the javascript methods in the operation tab
      //       should also be done to the below methods as well.

      appendDirtyJavaScript("function checkMandoItem8()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  return checkItem8RowNo(0);\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovItem8OrgCode(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript(" if (document.form.ITEM8_ROLE_CODE.value != '') \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if((document.form.ITEM8_SIGN_ID.value == '') && (document.form.ITEM8_CONTRACT.value == ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ITEM8_ROLE_CODE.value) + \"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM8_SIGN_ID.value == '') && (document.form.ITEM8_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ITEM8_ROLE_CODE.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM8_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM8_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM8_SIGN_ID.value != '') && (document.form.ITEM8_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ITEM8_ROLE_CODE.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM8_CONTRACT.value)+\"' AND EMP_NO = '\" +URLClientEncode(document.form.ITEM8_SIGN_ID.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM8_CONTRACT.value)+\"' AND MEMBER_EMP_NO = '\" +URLClientEncode(document.form.ITEM8_SIGN_ID.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else if ((document.form.ITEM8_ROLE_CODE.value == '') && (document.form.ITEM8_SIGN_ID.value != '')) \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if (document.form.ITEM8_CONTRACT.value == '') \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMPLOYEE_ID = '\" +URLClientEncode(document.form.ITEM8_SIGN_ID.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" +URLClientEncode(document.form.ITEM8_SIGN_ID.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMPLOYEE_ID = '\" +URLClientEncode(document.form.ITEM8_SIGN_ID.value)+\"' AND CONTRACT = '\" +URLClientEncode(document.form.ITEM8_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" +URLClientEncode(document.form.ITEM8_SIGN_ID.value)+\"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM8_CONTRACT.value)+\"'\";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else if ((document.form.ITEM8_ROLE_CODE.value == '') && (document.form.ITEM8_SIGN_ID.value == '')) \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if (document.form.ITEM8_CONTRACT.value != '') \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" CONTRACT = '\" +URLClientEncode(document.form.ITEM8_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM8_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if (document.form.ITEM8_CONTRACT.value != '') \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" CONTRACT = '\" +URLClientEncode(document.form.ITEM8_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM8_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if (document.form.ITEM8_TEAM_ID.value != '')\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript(" if(whereCond1 != '' )\n");
      appendDirtyJavaScript("	           whereCond1 += \" AND \";\n"); 
      appendDirtyJavaScript(" if (document.form.ITEM8_ROLE_CODE.value == '')\n");
      appendDirtyJavaScript("	   whereCond1 += \" ORG_CODE IN (SELECT MAINT_ORG FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.ITEM8_TEAM_ID.value+\"' AND CONTRACT = '\"+ document.form.ITEM8_TEAM_CONTRACT.value+\"' \";\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("	   whereCond1 += \" (ORG_CODE,EMP_NO,1) IN (SELECT MAINT_ORG,MEMBER_EMP_NO,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\"+ document.form.ITEM8_ROLE_CODE.value+\"',MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.ITEM8_TEAM_ID.value+\"' AND CONTRACT='\" + URLClientEncode(document.form.ITEM8_TEAM_CONTRACT.value) +\"' \";\n");
      appendDirtyJavaScript(" if(whereCond2 != '' )\n");
      appendDirtyJavaScript("	           whereCond1 += \" AND \" +whereCond2 +\" \";\n"); 
      appendDirtyJavaScript("	whereCond1 += \")\";\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM8_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM8_ORG_CODE',i).indexOf('%') !=-1)? getValue_('ITEM8_ORG_CODE',i):'';\n");
      appendDirtyJavaScript("	if( getValue_('ITEM8_ROLE_CODE',i)=='' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("	            if( getValue_('ITEM8_SIGN',i)=='' )\n");
      appendDirtyJavaScript("             {\n");
      appendDirtyJavaScript("                  openLOVWindow('ITEM8_ORG_CODE',i,\n");
      appendDirtyJavaScript("                     '../mscomw/OrgCodeAllowedSiteLov.page?__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM8_ORG_CODE',i))\n"); 
      appendDirtyJavaScript("                      + '&ORG_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM8_COMPANY',i))\n");
      appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM8_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	             ,550,500,'validateItem8OrgCode');\n");
      appendDirtyJavaScript("             }\n");
      appendDirtyJavaScript("	            else\n");
      appendDirtyJavaScript("             {\n");
      appendDirtyJavaScript("                 openLOVWindow('ITEM8_ORG_CODE',i,\n");
      appendDirtyJavaScript("                     '../mscomw/MaintEmployeeLov.page?&__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                     + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM8_ORG_CODE',i))\n"); 
      appendDirtyJavaScript("                     + '&ORG_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                     + '&EMPLOYEE_ID=' + URLClientEncode(getValue_('ITEM8_SIGN_ID',i))\n");
      appendDirtyJavaScript("                     + '&COMPANY=' + URLClientEncode(getValue_('ITEM8_COMPANY',i))\n");
      appendDirtyJavaScript("                     + '&CONTRACT=' + URLClientEncode(getValue_('ITEM8_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	            ,550,500,'validateItem8OrgCode');\n");
      appendDirtyJavaScript("             }\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("       {\n");        
      appendDirtyJavaScript("              openLOVWindow('ITEM8_ORG_CODE',i,\n");
      appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM8_ORG_CODE',i))\n"); 
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('ITEM8_SIGN_ID',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM8_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM8_CONTRACT',i))\n");       
      appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(getValue_('ITEM8_ROLE_CODE',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateItem8OrgCode');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateItem8OrgCode(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM8',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkItem8OrgCode(i) ) return;\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM8_ORG_CODE'\n");
      appendDirtyJavaScript("		+ '&ITEM8_CONTRACT=' + URLClientEncode(getValue_('ITEM8_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_ORG_CODE=' + URLClientEncode(getValue_('ITEM8_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_ROLE_CODE=' + URLClientEncode(getValue_('ITEM8_ROLE_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_CATALOG_NO=' + URLClientEncode(getValue_('ITEM8_CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_CATALOG_CONTRACT=' + URLClientEncode(getValue_('ITEM8_CATALOG_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_CATALOGDESC=' + URLClientEncode(getValue_('ITEM8_CATALOGDESC',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_COST=' + URLClientEncode(getValue_('ITEM8_COST',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_COSTAMOUNT=' + URLClientEncode(getValue_('ITEM8_COSTAMOUNT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_PLAN_HRS=' + URLClientEncode(getValue_('ITEM8_PLAN_HRS',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_ORG_CODE=' + URLClientEncode(getValue_('ITEM8_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_PLAN_MEN=' + URLClientEncode(getValue_('ITEM8_PLAN_MEN',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_SIGN=' + URLClientEncode(getValue_('ITEM8_SIGN',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_WO_NO=' + URLClientEncode(getValue_('ITEM8_WO_NO',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'ITEM8_ORG_CODE',i,'Maintenance Organization') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_CATALOG_NO',i,0);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_CATALOGDESC',i,1);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_COST',i,2);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_COSTAMOUNT',i,3);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_ORG_CODE',i,4);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_CONTRACT',i,5);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_ORG_CODE_DESC',i,6);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("if(  getField_('ITEM8_ROLE_CODE',i).value ==  '')\n");
      appendDirtyJavaScript("	validateItem8CatalogNo(i);\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovItem8Contract(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript(" if (document.form.ITEM8_COMPANY.value != '') \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("	      whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.ITEM8_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM8_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM8_CONTRACT',i).indexOf('%') !=-1)? getValue_('ITEM8_CONTRACT',i):'';\n");
      appendDirtyJavaScript("                  openLOVWindow('ITEM8_CONTRACT',i,\n");
      appendDirtyJavaScript("'");
      appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=USER_ALLOWED_SITE_LOV&__FIELD=Maint.Org.+Site&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM8_CONTRACT',i))\n");
      appendDirtyJavaScript("+ '&ITEM8_CONTRACT=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript(",550,500,'validateItem8Contract');\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function validateItem8CatalogNo(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM8',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkItem8CatalogNo(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('ITEM8_CATALOG_NO',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('ITEM8_CATALOGDESC',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_PRICE_LIST_NO',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_DISCOUNT',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_LISTPRICE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_SALESPRICEAMOUNT',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_DISCOUNTED_PRICE',i).value = '';\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM8_CATALOG_NO'\n");
      appendDirtyJavaScript("		+ '&ITEM8_CATALOG_CONTRACT=' + URLClientEncode(getValue_('ITEM8_CATALOG_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_CATALOG_NO=' + URLClientEncode(getValue_('ITEM8_CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_PRICE_LIST_NO=' + URLClientEncode(getValue_('ITEM8_PRICE_LIST_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_PLAN_MEN=' + URLClientEncode(getValue_('ITEM8_PLAN_MEN',i))\n");
      appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("		+ '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_CONTRACT=' + URLClientEncode(getValue_('ITEM8_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_ORG_CODE=' + URLClientEncode(getValue_('ITEM8_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_PLAN_HRS=' + URLClientEncode(getValue_('ITEM8_PLAN_HRS',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_ORG_CODE=' + URLClientEncode(getValue_('ITEM8_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_ROLE_CODE=' + URLClientEncode(getValue_('ITEM8_ROLE_CODE',i))\n");
      appendDirtyJavaScript("		+ '&HEAD_CURRENCEY_CODE=' + URLClientEncode(getValue_('HEAD_CURRENCEY_CODE',i))\n");
      appendDirtyJavaScript("		+ '&DISCOUNTED_PRICE=' + URLClientEncode(getValue_('ITEM8_DISCOUNTED_PRICE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_ROWSTATUS=' + URLClientEncode(getValue_('ITEM8_ROWSTATUS',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_WO_NO=' + URLClientEncode(getValue_('ITEM8_WO_NO',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'ITEM8_CATALOG_NO',i,'Sales Part') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_CATALOGDESC',i,0);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_COST',i,1);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_COSTAMOUNT',i,2);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_PRICE_LIST_NO',i,3);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_DISCOUNT',i,4);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_LISTPRICE',i,5);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_SALESPRICEAMOUNT',i,6);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_DISCOUNTED_PRICE',i,7);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function lovItem8RoleCode(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript(" if ((document.form.ITEM8_SIGN_ID.value != '') && (document.form.ITEM8_COMPANY.value != '')) \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if((document.form.ITEM8_ORG_CODE.value == '') && (document.form.ITEM8_CONTRACT.value == ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.ITEM8_SIGN_ID.value) + \"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.ITEM8_SIGN_ID.value) + \"' \";\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM8_ORG_CODE.value != '') && (document.form.ITEM8_CONTRACT.value == ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.ITEM8_SIGN_ID.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM8_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.ITEM8_SIGN_ID.value) + \"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM8_ORG_CODE.value)+\"' \";\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM8_ORG_CODE.value == '') && (document.form.ITEM8_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.ITEM8_SIGN_ID.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM8_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.ITEM8_SIGN_ID.value) + \"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM8_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM8_ORG_CODE.value != '') && (document.form.ITEM8_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.ITEM8_SIGN_ID.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM8_ORG_CODE.value)+ \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM8_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.ITEM8_SIGN_ID.value) + \"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM8_ORG_CODE.value)+\"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM8_CONTRACT.value)+\"' \";\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("	   whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.ITEM8_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript("	   whereCond2 = \" Site_API.Get_Company(MAINT_ORG_CONTRACT) = '\" + URLClientEncode(document.form.ITEM8_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if (document.form.ITEM8_TEAM_ID.value != '')\n");
      appendDirtyJavaScript("	   whereCond1 += \" AND 1 IN (SELECT Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,ROLE_CODE,MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.ITEM8_TEAM_ID.value+\"' AND CONTRACT = '\"+ document.form.ITEM8_TEAM_CONTRACT.value+\"' AND \" +whereCond2 +\")\";\n"); 
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM8_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM8_ROLE_CODE',i).indexOf('%') !=-1)? getValue_('ITEM8_ROLE_CODE',i):'';\n");
      appendDirtyJavaScript("	if( getValue_('ITEM8_SIGN_ID',i)=='' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("                  openLOVWindow('ITEM8_ROLE_CODE',i,\n");
      appendDirtyJavaScript("                     '../mscomw/RoleToSiteLov.page?__FIELD=Craft+ID&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM8_ROLE_CODE',i))\n"); 
      appendDirtyJavaScript("                      + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM8_COMPANY',i))\n");
      appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM8_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	             ,550,500,'validateItem8RoleCode');\n");
      appendDirtyJavaScript("       }\n"); 
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("       {\n");        
      appendDirtyJavaScript("              openLOVWindow('ITEM8_ROLE_CODE',i,\n");
      appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Craft+ID&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM8_ROLE_CODE',i))\n"); 
      appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM8_ORG_CODE',i))\n");
      appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('ITEM8_SIGN_ID',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM8_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM8_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateItem8RoleCode');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateItem8RoleCode(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM8',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkItem8RoleCode(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('ITEM8_ROLE_CODE',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('ITEM8_CATALOG_NO',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_COST',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_COSTAMOUNT',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_CATALOGDESC',i).value = '';\n");
      appendDirtyJavaScript("		validateItem8OrgCode(i);\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n"); 
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM8_ROLE_CODE'\n");  
      appendDirtyJavaScript("		+ '&ITEM8_ROLE_CODE=' + URLClientEncode(getValue_('ITEM8_ROLE_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_CONTRACT=' + URLClientEncode(getValue_('ITEM8_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_ORG_CODE=' + URLClientEncode(getValue_('ITEM8_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_COST=' + URLClientEncode(getValue_('ITEM8_COST',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_CATALOG_NO=' + URLClientEncode(getValue_('ITEM8_CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_PLAN_MEN=' + URLClientEncode(getValue_('ITEM8_PLAN_MEN',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_PLAN_HRS=' + URLClientEncode(getValue_('ITEM8_PLAN_HRS',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_SIGN=' + URLClientEncode(getValue_('ITEM8_SIGN',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_WO_NO=' + URLClientEncode(getValue_('ITEM8_WO_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_CATALOG_CONTRACT=' + URLClientEncode(getValue_('ITEM8_CATALOG_CONTRACT',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'ITEM8_ROLE_CODE',i,'Craft ID') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_CATALOG_NO',i,0);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_COST',i,1);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_COSTAMOUNT',i,2);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_CATALOGDESC',i,3);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_ROLE_CODE',i,4);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_CONTRACT',i,5);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_ROLE_CODE_DESC',i,6);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	validateItem8CatalogNo(i);\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateItem8PlanMen(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM8',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkItem8PlanMen(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('ITEM8_PLAN_MEN',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('ITEM8_COST',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_COSTAMOUNT',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_PRICE_LIST_NO',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_DISCOUNT',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_LISTPRICE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_SALESPRICEAMOUNT',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_DISCOUNTED_PRICE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_TOTAL_MAN_HOURS',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_TOTAL_REMAINING_HOURS',i).value = '';\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM8_PLAN_MEN'\n");
      appendDirtyJavaScript("		+ '&ITEM8_PLAN_MEN=' + URLClientEncode(getValue_('ITEM8_PLAN_MEN',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_PLAN_HRS=' + URLClientEncode(getValue_('ITEM8_PLAN_HRS',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_COST=' + URLClientEncode(getValue_('ITEM8_COST',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_ROLE_CODE=' + URLClientEncode(getValue_('ITEM8_ROLE_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_CONTRACT=' + URLClientEncode(getValue_('ITEM8_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_ORG_CODE=' + URLClientEncode(getValue_('ITEM8_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_ORG_CODE=' + URLClientEncode(getValue_('ITEM8_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_CATALOG_CONTRACT=' + URLClientEncode(getValue_('ITEM8_CATALOG_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_CATALOG_NO=' + URLClientEncode(getValue_('ITEM8_CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_PRICE_LIST_NO=' + URLClientEncode(getValue_('ITEM8_PRICE_LIST_NO',i))\n");
      appendDirtyJavaScript("		+ '&CUSTOMER_NO=' + URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("		+ '&AGREEMENT_ID=' + URLClientEncode(getValue_('AGREEMENT_ID',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_LISTPRICE=' + URLClientEncode(getValue_('ITEM8_LISTPRICE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_DISCOUNT=' + URLClientEncode(getValue_('ITEM8_DISCOUNT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_DISCOUNTED_PRICE=' + URLClientEncode(getValue_('ITEM8_DISCOUNTED_PRICE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_WO_NO=' + URLClientEncode(getValue_('ITEM8_WO_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_TOTAL_ALLOCATED_HOURS=' + URLClientEncode(getValue_('ITEM8_TOTAL_ALLOCATED_HOURS',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'ITEM8_PLAN_MEN',i,'Planned Men') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_COST',i,0);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_COSTAMOUNT',i,1);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_PRICE_LIST_NO',i,2);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_DISCOUNT',i,3);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_LISTPRICE',i,4);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_SALESPRICEAMOUNT',i,5);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_DISCOUNTED_PRICE',i,6);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_TOTAL_MAN_HOURS',i,7);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_TOTAL_REMAINING_HOURS',i,8);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	validateItem8CatalogNo(i);\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovItem8Sign(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript(" if (document.form.ITEM8_ROLE_CODE.value == '')\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       if ((document.form.ITEM8_ORG_CODE.value != '') && (document.form.ITEM8_CONTRACT.value == '') && (document.form.ITEM8_COMPANY.value != ''))\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM8_COMPANY.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM8_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM8_COMPANY.value) + \"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM8_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else if(document.form.ITEM8_ORG_CODE.value == '' && document.form.ITEM8_CONTRACT.value != '' && document.form.ITEM8_COMPANY.value != '')\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM8_COMPANY.value) + \"' AND CONTRACT = '\" +URLClientEncode(document.form.ITEM8_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM8_COMPANY.value) + \"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM8_CONTRACT.value)+\"' \";\n");
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else if(document.form.ITEM8_ORG_CODE.value != '' && document.form.ITEM8_CONTRACT.value != '' && document.form.ITEM8_COMPANY.value != '')\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM8_COMPANY.value) + \"' AND CONTRACT = '\" +URLClientEncode(document.form.ITEM8_CONTRACT.value)+\"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM8_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM8_COMPANY.value) + \"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM8_CONTRACT.value)+\"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM8_ORG_CODE.value)+\"' \";\n");
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else if(document.form.ITEM8_ORG_CODE.value == '' && document.form.ITEM8_CONTRACT.value == '' && document.form.ITEM8_COMPANY.value != '')\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM8_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM8_COMPANY.value) + \"' \";\n");
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("		 whereCond1 = \" COMPANY IS NOT NULL \";\n"); 
      appendDirtyJavaScript("		 whereCond2 = \" COMPANY IS NOT NULL \";\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("    if((document.form.ITEM8_ORG_CODE.value != '') && (document.form.ITEM8_CONTRACT.value != '') && (document.form.ITEM8_COMPANY.value != ''))\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM8_CONTRACT.value)+\"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM8_ORG_CODE.value)+\"' AND ROLE_CODE='\" +URLClientEncode(document.form.ITEM8_ROLE_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript("		 whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM8_CONTRACT.value)+\"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM8_ORG_CODE.value)+\"' \";\n");
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else if(document.form.ITEM8_ORG_CODE.value == '' && document.form.ITEM8_CONTRACT.value == '' && document.form.ITEM8_COMPANY.value != '')\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ITEM8_ROLE_CODE.value) + \"' \";\n"); 
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" COMPANY IS NOT NULL AND ROLE_CODE = '\" + URLClientEncode(document.form.ITEM8_ROLE_CODE.value) + \"' \";\n");  
      appendDirtyJavaScript("		 whereCond2 = \" COMPANY IS NOT NULL \";\n"); 
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" if(document.form.ITEM8_TEAM_ID.value != '')\n");                           
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("    if(document.form.ITEM8_ROLE_CODE.value == '')\n");
      appendDirtyJavaScript("	   whereCond1 += \" AND EMPLOYEE_ID IN (SELECT MEMBER_EMP_NO FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID='\" + URLClientEncode(document.form.ITEM8_TEAM_ID.value) +\"' AND CONTRACT = '\"+ document.form.ITEM8_TEAM_CONTRACT.value+\"' AND \" +whereCond2 +\") \";\n"); 
      appendDirtyJavaScript("       else\n");
      appendDirtyJavaScript("	   whereCond1 += \" AND (EMP_NO,1)IN (SELECT MEMBER_EMP_NO,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\" + URLClientEncode(document.form.ITEM8_ROLE_CODE.value) + \"',MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID='\" + URLClientEncode(document.form.ITEM8_TEAM_ID.value) +\"' AND CONTRACT = '\"+ document.form.ITEM8_TEAM_CONTRACT.value+\"' AND \" +whereCond2 +\") \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM8_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM8_SIGN',i).indexOf('%') !=-1)? getValue_('ITEM8_SIGN',i):'';\n");
      appendDirtyJavaScript("	if( getValue_('ITEM8_ROLE_CODE',i)=='' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("              openLOVWindow('ITEM8_SIGN',i,\n");
      appendDirtyJavaScript("                  '../mscomw/MaintEmployeeLov.page?__FIELD=Executed+By&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM8_SIGN',i))\n"); 
      appendDirtyJavaScript("                  + '&PERSON_ID=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM8_ORG_CODE',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM8_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&CONTRACT=' + URLClientEncode(getValue_('ITEM8_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateItem8Sign');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("       {\n");        
      appendDirtyJavaScript("              openLOVWindow('ITEM8_SIGN',i,\n");
      appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Executed+By&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM8_SIGN',i))\n"); 
      appendDirtyJavaScript("                  + '&SIGNATURE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM8_ORG_CODE',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM8_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM8_CONTRACT',i))\n");       
      appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(getValue_('ITEM8_ROLE_CODE',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateItem8Sign');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("}\n");

      // XSS_Safe ILSOLK 20070706
      appendDirtyJavaScript("function lovItem8TeamId(i,params)"); 
      appendDirtyJavaScript("{"); 
      appendDirtyJavaScript("	if(params) param = params;\n"); 
      appendDirtyJavaScript("	else param = '';\n"); 
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript(" if(document.form.ITEM8_TEAM_CONTRACT.value != '')\n");
      appendDirtyJavaScript("		whereCond1 = \"CONTRACT = '\" +URLClientEncode(document.form.ITEM8_TEAM_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("		whereCond1 = \"CONTRACT IS NOT NULL \";\n"); 
      appendDirtyJavaScript(" if(document.form.ITEM8_COMPANY.value != '')\n");
      appendDirtyJavaScript(" if( whereCond1=='')\n");
      appendDirtyJavaScript("		whereCond1 = \"COMPANY = '\" +URLClientEncode(document.form.ITEM8_COMPANY.value)+\"' \";\n"); 
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("		whereCond1 += \" AND COMPANY = '\" +URLClientEncode(document.form.ITEM8_COMPANY.value)+\"' \";\n"); 
      appendDirtyJavaScript(" if( whereCond1 !='')\n");
      appendDirtyJavaScript("	           whereCond1 += \" AND \";\n");
      appendDirtyJavaScript(" whereCond1 += \" nvl(to_date(to_char(to_date('\" +URLClientEncode(document.form.ITEM8_DATE_FROM_MASKED.value)+\"','" + getDateTimeFormat("SQL") + "'),\'YYYYMMDD\'),\'YYYYMMDD\'),to_date(to_char(sysdate,\'YYYYMMDD\'),\'YYYYMMDD\')) BETWEEN nvl(VALID_FROM,to_date(\'00010101\',\'YYYYMMDD\')) AND nvl(VALID_TO,to_date(\'99991231\',\'YYYYMMDD\')) \";\n"); 
      appendDirtyJavaScript(" if(document.form.ITEM8_SIGN_ID.value != '')\n");
      appendDirtyJavaScript("		whereCond2 = \"MEMBER_EMP_NO = '\" +URLClientEncode(document.form.ITEM8_SIGN_ID.value)+\"' \";\n"); 
      appendDirtyJavaScript(" if(document.form.ITEM8_ORG_CODE.value != '')\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript(" if( whereCond2=='')\n");
      appendDirtyJavaScript("		whereCond2 = \"MAINT_ORG = '\" +URLClientEncode(document.form.ITEM8_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript(" else \n");
      appendDirtyJavaScript("		whereCond2+= \" AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM8_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if(document.form.ITEM8_CONTRACT.value != '')\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript(" if(whereCond2=='' )\n");
      appendDirtyJavaScript("		whereCond2 = \"MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM8_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript(" else \n");
      appendDirtyJavaScript("		whereCond2 += \" AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM8_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if(whereCond2 !='' )\n");
      appendDirtyJavaScript("     {\n");
      appendDirtyJavaScript("        if(whereCond1 !='' )\n");
      appendDirtyJavaScript("	           whereCond1 += \" AND \";\n"); 
      appendDirtyJavaScript("        if(document.form.ITEM8_ROLE_CODE.value == '' )\n");
      appendDirtyJavaScript("	           whereCond1 += \" TEAM_ID IN (SELECT TEAM_ID FROM MAINT_TEAM_MEMBER_SERV WHERE \" +whereCond2 +\")\";\n"); 
      appendDirtyJavaScript("        else \n");
      appendDirtyJavaScript("	           whereCond1 += \" (TEAM_ID,1) IN (SELECT TEAM_ID,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\"+ document.form.ITEM8_ROLE_CODE.value+\"' ,MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE \" +whereCond2 +\")\";\n"); 
      appendDirtyJavaScript("     }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM8_IN_FIND_MODE);\n"); 
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM8_TEAM_ID',i).indexOf('%') !=-1)? getValue_('ITEM8_TEAM_ID',i):'';\n"); 
      appendDirtyJavaScript("	openLOVWindow('ITEM8_TEAM_ID',i,\n"); 
      appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=MAINT_TEAM&__FIELD=Team+Id&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''"); 
      appendDirtyJavaScript("		+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM8_TEAM_ID',i))"); 
      appendDirtyJavaScript("		+ '&ITEM8_TEAM_ID=' + URLClientEncode(key_value)"); 
      appendDirtyJavaScript("		,550,500,'validateItem8TeamId');\n"); 
      appendDirtyJavaScript("}\n"); 

      appendDirtyJavaScript("function lovItem8TeamContract(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript(" if (document.form.ITEM8_COMPANY.value != '') \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("	      whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.ITEM8_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM8_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM8_TEAM_CONTRACT',i).indexOf('%') !=-1)? getValue_('ITEM8_TEAM_CONTRACT',i):'';\n");
      appendDirtyJavaScript("                  openLOVWindow('ITEM8_TEAM_CONTRACT',i,\n");
      appendDirtyJavaScript("'");
      appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=USER_ALLOWED_SITE_LOV&__FIELD=Team+Site&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM8_TEAM_CONTRACT',i))\n");
      appendDirtyJavaScript("+ '&ITEM8_TEAM_CONTRACT=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript(",550,500,'validateItem8TeamContract');\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateItem8Sign(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM8',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkItem8Sign(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('ITEM8_SIGN',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('ITEM8_SIGN_ID',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_ROLE_CODE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_CATALOG_NO',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_CATALOGDESC',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_COST',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_COSTAMOUNT',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_SIGN',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_ALLOCATED_HOURS',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_TOTAL_ALLOCATED_HOURS',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ITEM8_TOTAL_REMAINING_HOURS',i).value = '';\n");


      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM8_SIGN'\n");
      appendDirtyJavaScript("		+ '&ITEM8_COMPANY=' + URLClientEncode(getValue_('ITEM8_COMPANY',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_SIGN=' + URLClientEncode(getValue_('ITEM8_SIGN',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_ROLE_CODE=' + URLClientEncode(getValue_('ITEM8_ROLE_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_CONTRACT=' + URLClientEncode(getValue_('ITEM8_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_ORG_CODE=' + URLClientEncode(getValue_('ITEM8_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_COST=' + URLClientEncode(getValue_('ITEM8_COST',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_CATALOG_NO=' + URLClientEncode(getValue_('ITEM8_CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_PLAN_MEN=' + URLClientEncode(getValue_('ITEM8_PLAN_MEN',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_PLAN_HRS=' + URLClientEncode(getValue_('ITEM8_PLAN_HRS',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_WO_NO=' + URLClientEncode(getValue_('ITEM8_WO_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_ALLOCATED_HOURS=' + URLClientEncode(getValue_('ITEM8_ALLOCATED_HOURS',i))\n");
      appendDirtyJavaScript("		+ '&ITEM8_TOTAL_MAN_HOURS=' + URLClientEncode(getValue_('ITEM8_TOTAL_MAN_HOURS',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'ITEM8_SIGN',i,'Executed By') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_SIGN_ID',i,0);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_ROLE_CODE',i,1);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_CONTRACT',i,2);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_ORG_CODE',i,3);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_CATALOG_NO',i,4);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_CATALOGDESC',i,5);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_COST',i,6);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_COSTAMOUNT',i,7);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_SIGN',i,8);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_ALLOCATED_HOURS',i,9);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_TOTAL_ALLOCATED_HOURS',i,10);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_TOTAL_REMAINING_HOURS',i,11);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_ORG_CODE_DESC',i,12);\n");
      appendDirtyJavaScript("		assignValue_('ITEM8_ROLE_CODE_DESC',i,13);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

      //validations for multiple allocations in operations tab.
      //-------------------------------------------------------------------


      appendDirtyJavaScript("function lovItem9OrgCode(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript(" if (document.form.ITEM9_ROLE_CODE.value != '') \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if((document.form.ITEM9_SIGN_ID.value == '') && (document.form.ITEM9_CONTRACT.value == ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ITEM9_ROLE_CODE.value) + \"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM9_SIGN_ID.value == '') && (document.form.ITEM9_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ITEM9_ROLE_CODE.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM8_SIGN_ID.value != '') && (document.form.ITEM9_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ITEM9_ROLE_CODE.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_CONTRACT.value)+\"' AND EMP_NO = '\" +URLClientEncode(document.form.ITEM9_SIGN_ID.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_CONTRACT.value)+\"' AND MEMBER_EMP_NO = '\" +URLClientEncode(document.form.ITEM9_SIGN_ID.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else if ((document.form.ITEM9_ROLE_CODE.value == '') && (document.form.ITEM9_SIGN_ID.value != '')) \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if (document.form.ITEM9_CONTRACT.value == '') \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMPLOYEE_ID = '\" +URLClientEncode(document.form.ITEM9_SIGN_ID.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" +URLClientEncode(document.form.ITEM9_SIGN_ID.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMPLOYEE_ID = '\" +URLClientEncode(document.form.ITEM9_SIGN_ID.value)+\"' AND CONTRACT = '\" +URLClientEncode(document.form.ITEM9_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" +URLClientEncode(document.form.ITEM9_SIGN_ID.value)+\"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_CONTRACT.value)+\"'\";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else if ((document.form.ITEM9_ROLE_CODE.value == '') && (document.form.ITEM9_SIGN_ID.value == '')) \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if (document.form.ITEM9_CONTRACT.value != '') \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" CONTRACT = '\" +URLClientEncode(document.form.ITEM9_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if (document.form.ITEM9_CONTRACT.value != '') \n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" CONTRACT = '\" +URLClientEncode(document.form.ITEM9_CONTRACT.value)+\"' \";\n");  
      appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_CONTRACT.value)+\"' \";\n"); 
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
      appendDirtyJavaScript("	   whereCond1 += \" ORG_CODE IN (SELECT MAINT_ORG FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.ITEM9_TEAM_ID.value+\"' AND CONTRACT='\" + URLClientEncode(document.form.ITEM9_TEAM_CONTRACT.value) +\"' \";\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("	   whereCond1 += \" (ORG_CODE,EMP_NO,1) IN (SELECT MAINT_ORG,MEMBER_EMP_NO,,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\"+ document.form.ITEM9_ROLE_CODE.value+\"',MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.ITEM9_TEAM_ID.value+\"' AND CONTRACT='\" + URLClientEncode(document.form.ITEM9_TEAM_CONTRACT.value) +\"' \";\n");
      appendDirtyJavaScript(" if(whereCond2 != '' )\n");
      appendDirtyJavaScript("	           whereCond1 += \" AND \" +whereCond2 +\" \";\n"); 
      appendDirtyJavaScript("	whereCond1 += \")\";\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM9_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM9_ORG_CODE',i).indexOf('%') !=-1)? getValue_('ITEM9_ORG_CODE',i):'';\n");
      appendDirtyJavaScript("	if( getValue_('ITEM9_ROLE_CODE',i)=='' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("	            if( getValue_('ITEM9_SIGN',i)=='' )\n");
      appendDirtyJavaScript("             {\n");
      appendDirtyJavaScript("                  openLOVWindow('ITEM9_ORG_CODE',i,\n");
      appendDirtyJavaScript("                     '../mscomw/OrgCodeAllowedSiteLov.page?__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM9_ORG_CODE',i))\n"); 
      appendDirtyJavaScript("                      + '&ORG_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM9_COMPANY',i))\n");
      appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM9_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	             ,550,500,'validateItem9OrgCode');\n");
      appendDirtyJavaScript("             }\n");
      appendDirtyJavaScript("	            else\n");
      appendDirtyJavaScript("             {\n");
      appendDirtyJavaScript("                 openLOVWindow('ITEM9_ORG_CODE',i,\n");
      appendDirtyJavaScript("                     '../mscomw/MaintEmployeeLov.page?&__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                     + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM9_ORG_CODE',i))\n"); 
      appendDirtyJavaScript("                     + '&ORG_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                     + '&EMPLOYEE_ID=' + URLClientEncode(getValue_('ITEM9_SIGN_ID',i))\n");
      appendDirtyJavaScript("                     + '&COMPANY=' + URLClientEncode(getValue_('ITEM9_COMPANY',i))\n");
      appendDirtyJavaScript("                     + '&CONTRACT=' + URLClientEncode(getValue_('ITEM9_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	            ,550,500,'validateItem9OrgCode');\n");
      appendDirtyJavaScript("             }\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("       {\n");        
      appendDirtyJavaScript("              openLOVWindow('ITEM9_ORG_CODE',i,\n");
      appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM9_ORG_CODE',i))\n"); 
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('ITEM9_SIGN_ID',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM9_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM9_CONTRACT',i))\n");       
      appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(getValue_('ITEM9_ROLE_CODE',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateItem9OrgCode');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovItem9Contract(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript(" if (document.form.ITEM9_COMPANY.value != '') \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("	      whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.ITEM9_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM9_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM9_CONTRACT',i).indexOf('%') !=-1)? getValue_('ITEM9_CONTRACT',i):'';\n");
      appendDirtyJavaScript("                  openLOVWindow('ITEM9_CONTRACT',i,\n");
      appendDirtyJavaScript("'");
      appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=USER_ALLOWED_SITE_LOV&__FIELD=Maint.Org.+Site&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM9_CONTRACT',i))\n");
      appendDirtyJavaScript("+ '&ITEM9_CONTRACT=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript(",550,500,'validateItem9Contract');\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovItem9RoleCode(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript(" if ((document.form.ITEM9_SIGN_ID.value != '') && (document.form.ITEM9_COMPANY.value != '')) \n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    if((document.form.ITEM9_ORG_CODE.value == '') && (document.form.ITEM9_CONTRACT.value == ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.ITEM9_SIGN_ID.value) + \"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.ITEM9_SIGN_ID.value) + \"' \";\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM9_ORG_CODE.value != '') && (document.form.ITEM9_CONTRACT.value == ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.ITEM9_SIGN_ID.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.ITEM9_SIGN_ID.value) + \"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+\"' \";\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM9_ORG_CODE.value == '') && (document.form.ITEM9_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.ITEM9_SIGN_ID.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.ITEM9_SIGN_ID.value) + \"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    else if((document.form.ITEM9_ORG_CODE.value != '') && (document.form.ITEM9_CONTRACT.value != ''))\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.ITEM9_SIGN_ID.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+ \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.ITEM9_SIGN_ID.value) + \"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+\"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_CONTRACT.value)+\"' \";\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("	   whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.ITEM9_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript("	   whereCond2 = \" Site_API.Get_Company(MAINT_ORG_CONTRACT) = '\" + URLClientEncode(document.form.ITEM9_COMPANY.value) + \"' \";\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if (document.form.ITEM9_TEAM_ID.value != '')\n");
      appendDirtyJavaScript("	   whereCond1 += \" AND 1 IN (SELECT Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,ROLE_CODE,MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.ITEM9_TEAM_ID.value+\"' AND CONTRACT='\" + URLClientEncode(document.form.ITEM9_TEAM_CONTRACT.value) +\"' AND \" +whereCond2 +\")\";\n"); 
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM9_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM9_ROLE_CODE',i).indexOf('%') !=-1)? getValue_('ITEM9_ROLE_CODE',i):'';\n");
      appendDirtyJavaScript("	if( getValue_('ITEM9_SIGN_ID',i)=='' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("                  openLOVWindow('ITEM9_ROLE_CODE',i,\n");
      appendDirtyJavaScript("                     '../mscomw/RoleToSiteLov.page?__FIELD=Craft+ID&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM9_ROLE_CODE',i))\n"); 
      appendDirtyJavaScript("                      + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM9_COMPANY',i))\n");
      appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM9_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	             ,550,500,'validateItem9RoleCode');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("       {\n");        
      appendDirtyJavaScript("              openLOVWindow('ITEM9_ROLE_CODE',i,\n");
      appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Craft+ID&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM9_ROLE_CODE',i))\n"); 
      appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM9_ORG_CODE',i))\n");
      appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('ITEM9_SIGN_ID',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM9_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM9_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateItem9RoleCode');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateItem9Sign(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("if( getRowStatus_('ITEM9',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("setDirty();\n");
      appendDirtyJavaScript("if( !checkItem9Sign(i) ) return;\n");
      appendDirtyJavaScript("if( getValue_('ITEM9_SIGN',i)=='' )\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("	   getField_('ITEM9_SIGN_ID',i).value = '';\n");
      appendDirtyJavaScript("	   getField_('ITEM9_ROLE_CODE',i).value = '';\n");
      appendDirtyJavaScript("	   getField_('ITEM9_SIGN',i).value = '';\n");
      appendDirtyJavaScript("	   return;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("  window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM9_SIGN'\n");
      appendDirtyJavaScript("	+ '&ITEM9_COMPANY=' + URLClientEncode(getValue_('ITEM9_COMPANY',i))\n");
      appendDirtyJavaScript("	+ '&ITEM9_SIGN=' + URLClientEncode(getValue_('ITEM9_SIGN',i))\n");
      appendDirtyJavaScript("	+ '&ITEM9_ROLE_CODE=' + URLClientEncode(getValue_('ITEM9_ROLE_CODE',i))\n");
      appendDirtyJavaScript("	+ '&ITEM9_CONTRACT=' + URLClientEncode(getValue_('ITEM9_CONTRACT',i))\n");
      appendDirtyJavaScript("	+ '&ITEM9_ORG_CODE=' + URLClientEncode(getValue_('ITEM9_ORG_CODE',i))\n");
      appendDirtyJavaScript("	+ '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
      appendDirtyJavaScript("	);\n");
      appendDirtyJavaScript("window.status='';\n");
      appendDirtyJavaScript("\n");
      appendDirtyJavaScript("if( checkStatus_(r,'ITEM9_SIGN',i,'Executed By') )\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	assignValue_('ITEM9_SIGN_ID',i,0);\n");
      appendDirtyJavaScript("	assignValue_('ITEM9_ROLE_CODE',i,1);\n");
      appendDirtyJavaScript("	assignValue_('ITEM9_ORG_CODE',i,2);\n");
      appendDirtyJavaScript("	assignValue_('ITEM9_SIGN',i,3);\n");
      appendDirtyJavaScript("	assignValue_('ITEM9_CONTRACT',i,4);\n");
      appendDirtyJavaScript("	assignValue_('ITEM9_ORG_CODE_DESC',i,5);\n");
      appendDirtyJavaScript("	assignValue_('ITEM9_ROLE_CODE_DESC',i,6);\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function lovItem9Sign(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	whereCond1 = '';\n"); 
      appendDirtyJavaScript("	whereCond2 = '';\n");
      appendDirtyJavaScript(" if (document.form.ITEM9_ROLE_CODE.value == '')\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("       if ((document.form.ITEM9_ORG_CODE.value != '') && (document.form.ITEM9_CONTRACT.value == '') && (document.form.ITEM9_COMPANY.value != ''))\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM9_COMPANY.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM9_COMPANY.value) + \"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else if(document.form.ITEM9_ORG_CODE.value == '' && document.form.ITEM9_CONTRACT.value != '' && document.form.ITEM9_COMPANY.value != '')\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM9_COMPANY.value) + \"' AND CONTRACT = '\" +URLClientEncode(document.form.ITEM9_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM9_COMPANY.value) + \"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_CONTRACT.value)+\"' \";\n");
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else if(document.form.ITEM9_ORG_CODE.value != '' && document.form.ITEM9_CONTRACT.value != '' && document.form.ITEM9_COMPANY.value != '')\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM9_COMPANY.value) + \"' AND CONTRACT = '\" +URLClientEncode(document.form.ITEM9_CONTRACT.value)+\"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM9_COMPANY.value) + \"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_CONTRACT.value)+\"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+\"' \";\n");
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else if(document.form.ITEM9_ORG_CODE.value == '' && document.form.ITEM9_CONTRACT.value == '' && document.form.ITEM9_COMPANY.value != '')\n");
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
      appendDirtyJavaScript("    if((document.form.ITEM9_ORG_CODE.value != '') && (document.form.ITEM9_CONTRACT.value != '') && (document.form.ITEM9_COMPANY.value != ''))\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_CONTRACT.value)+\"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+\"' AND ROLE_CODE='\" +URLClientEncode(document.form.ITEM9_ROLE_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript("		 whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_CONTRACT.value)+\"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+\"' \";\n");
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else if(document.form.ITEM9_ORG_CODE.value == '' && document.form.ITEM9_CONTRACT.value == '' && document.form.ITEM9_COMPANY.value != '')\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ITEM9_ROLE_CODE.value) + \"' \";\n"); 
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("       else\n");
      appendDirtyJavaScript("          {\n");
      appendDirtyJavaScript("		 whereCond1 = \" COMPANY IS NOT NULL AND ROLE_CODE = '\" + URLClientEncode(document.form.ITEM9_ROLE_CODE.value) + \"' \";\n");  
      appendDirtyJavaScript("		 whereCond2 = \" COMPANY IS NOT NULL \";\n"); 
      appendDirtyJavaScript("          }\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript(" if(document.form.ITEM9_TEAM_ID.value != '')\n");                           
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("    if(document.form.ITEM9_ROLE_CODE.value == '')\n");
      appendDirtyJavaScript("	   whereCond1 += \" AND EMPLOYEE_ID IN (SELECT MEMBER_EMP_NO FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID='\" + URLClientEncode(document.form.ITEM9_TEAM_ID.value) +\"' AND CONTRACT = '\"+ document.form.ITEM9_TEAM_CONTRACT.value+\"' AND \" +whereCond2 +\") \";\n"); 
      appendDirtyJavaScript("       else\n");
      appendDirtyJavaScript("	   whereCond1 += \" AND (EMP_NO,1)IN (SELECT MEMBER_EMP_NO,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\" + URLClientEncode(document.form.ITEM9_ROLE_CODE.value) + \"',MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID='\" + URLClientEncode(document.form.ITEM9_TEAM_ID.value) +\"' AND CONTRACT = '\"+ document.form.ITEM9_TEAM_CONTRACT.value+\"' AND \" +whereCond2 +\") \";\n"); 
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM9_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM9_SIGN',i).indexOf('%') !=-1)? getValue_('ITEM9_SIGN',i):'';\n");
      appendDirtyJavaScript("	if( getValue_('ITEM9_ROLE_CODE',i)=='' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("              openLOVWindow('ITEM9_SIGN',i,\n");
      appendDirtyJavaScript("                  '../mscomw/MaintEmployeeLov.page?__FIELD=Executed+By&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM9_SIGN',i))\n"); 
      appendDirtyJavaScript("                  + '&PERSON_ID=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM9_ORG_CODE',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM9_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&CONTRACT=' + URLClientEncode(getValue_('ITEM9_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateItem9Sign');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("       {\n");        
      appendDirtyJavaScript("              openLOVWindow('ITEM9_SIGN',i,\n");
      appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Executed+By&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM9_SIGN',i))\n"); 
      appendDirtyJavaScript("                  + '&SIGNATURE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM9_ORG_CODE',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM9_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM9_CONTRACT',i))\n");       
      appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(getValue_('ITEM9_ROLE_CODE',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateItem9Sign');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("}\n");

      // XSS_Safe ILSOLK 20070706
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
      appendDirtyJavaScript(" whereCond1 += \" nvl(to_date(to_char(to_date('\" +URLClientEncode(document.form.ITEM9_DATE_FROM_MASKED.value)+\"','" +getDateTimeFormat("SQL") + "'),\'YYYYMMDD\'),\'YYYYMMDD\'),to_date(to_char(sysdate,\'YYYYMMDD\'),\'YYYYMMDD\')) BETWEEN nvl(VALID_FROM,to_date(\'00010101\',\'YYYYMMDD\')) AND nvl(VALID_TO,to_date(\'99991231\',\'YYYYMMDD\')) \";\n"); 
      appendDirtyJavaScript(" if(document.form.ITEM9_SIGN_ID.value != '')\n");
      appendDirtyJavaScript("		whereCond2 = \"MEMBER_EMP_NO = '\" +URLClientEncode(document.form.ITEM9_SIGN_ID.value)+\"' \";\n"); 
      appendDirtyJavaScript(" if(document.form.ITEM9_ORG_CODE.value != '')\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript(" if( whereCond2=='')\n");
      appendDirtyJavaScript("		whereCond2 = \"MAINT_ORG = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript(" else \n");
      appendDirtyJavaScript("		whereCond2+= \" AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM9_ORG_CODE.value)+\"' \";\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if(document.form.ITEM9_CONTRACT.value != '')\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript(" if(whereCond2=='' )\n");
      appendDirtyJavaScript("		whereCond2 = \"MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript(" else \n");
      appendDirtyJavaScript("		whereCond2 += \" AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM9_CONTRACT.value)+\"' \";\n"); 
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if(whereCond2 !='' )\n");
      appendDirtyJavaScript("     {\n");
      appendDirtyJavaScript("        if(whereCond1 !='' )\n");
      appendDirtyJavaScript("	           whereCond1 += \" AND \";\n"); 
      appendDirtyJavaScript("        if(document.form.ITEM9_ROLE_CODE.value == '' )\n");
      appendDirtyJavaScript("	           whereCond1 += \" TEAM_ID IN (SELECT TEAM_ID FROM MAINT_TEAM_MEMBER_SERV WHERE \" +whereCond2 +\")\";\n"); 
      appendDirtyJavaScript("        else \n");
      appendDirtyJavaScript("	           whereCond1 += \" (TEAM_ID,1) IN (SELECT TEAM_ID,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\"+ document.form.ITEM9_ROLE_CODE.value+\"' ,MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE \" +whereCond2 +\")\";\n"); 
      appendDirtyJavaScript("     }\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM9_IN_FIND_MODE);\n"); 
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM9_TEAM_ID',i).indexOf('%') !=-1)? getValue_('ITEM9_TEAM_ID',i):'';\n"); 
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


      //-------------------------------------------------------------------

      appendDirtyJavaScript("function lovPredecessor(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   document.form.REFRESHPARENT1.value = \"TRUE\";\n");
      appendDirtyJavaScript("   submit();\n");  
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovItem8Predecessor(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   document.form.REFRESHPARENT2.value = \"TRUE\";\n");
      appendDirtyJavaScript("   submit();\n");  
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function setPredValues(sPredList,sblock)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("if (sPredList!='-1') \n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("     if (sblock =='ITEM2') \n");
      appendDirtyJavaScript("        getField_('PREDECESSOR', -1).value = sPredList;\n");
      appendDirtyJavaScript("     else \n");
      appendDirtyJavaScript("        getField_('ITEM8_PREDECESSOR', -1).value = sPredList;\n");
      appendDirtyJavaScript("  }\n");
      appendDirtyJavaScript("else\n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("     if (sblock =='ITEM2') \n");
      appendDirtyJavaScript("       getField_('PREDECESSOR', -1).value = '';\n");
      appendDirtyJavaScript("     else\n");
      appendDirtyJavaScript("       getField_('ITEM8_PREDECESSOR', -1).value = '';\n");
      appendDirtyJavaScript("  }\n"); 
      appendDirtyJavaScript("}\n"); 

      appendDirtyJavaScript("function Item2PlanMenCheck()\n");
      appendDirtyJavaScript("{\n");
      if (!mgr.isEmpty(planMen))
      {
         if (Integer.parseInt(planMen) < 2)
         {
            appendDirtyJavaScript("if (document.form.PLAN_MEN.value>1) \n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("      if (confirm(\"");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2ITEM2WARNING: Updating Planned Men when having an employee allocated to the operation will move the employee to the multi-allocation level instead."));
            appendDirtyJavaScript("\")){\n");
            appendDirtyJavaScript("     return true;\n");
            appendDirtyJavaScript("     } \n");
            appendDirtyJavaScript("     else;\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   document.form.SAVEEXECUTE.value = \"FALSE\";\n");
            appendDirtyJavaScript("   return false;\n");
            appendDirtyJavaScript("  }\n");
            appendDirtyJavaScript(" }\n");
         }
         else
            appendDirtyJavaScript("return true;\n"); 
      }
      else
         appendDirtyJavaScript("return true;\n"); 

      appendDirtyJavaScript("}\n"); 


      appendDirtyJavaScript("function Item8PlanMenCheck()\n");
      appendDirtyJavaScript("{\n");
      if (!mgr.isEmpty(planMen))
      {
         if (Integer.parseInt(planMen) < 2)
         {
            appendDirtyJavaScript("if (document.form.ITEM8_PLAN_MEN.value>1) \n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("      if (confirm(\"");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2ITEM8WARNING: Updating Planned Men when having an employee allocated to the operation will move the employee to the multi-allocation level instead."));
            appendDirtyJavaScript("\")){\n");
            appendDirtyJavaScript("     return true;\n");
            appendDirtyJavaScript("     } \n");
            appendDirtyJavaScript("     else;\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   document.form.SAVEEXECUTE.value = \"FALSE\";\n");
            appendDirtyJavaScript("   return false;\n");
            appendDirtyJavaScript("  }\n");
            appendDirtyJavaScript(" }\n");
         }
         else
            appendDirtyJavaScript("return true;\n"); 
      }
      else
         appendDirtyJavaScript("return true;\n"); 

      appendDirtyJavaScript("}\n"); 

      if (bPpChanged)
      {
         bPpChanged = false;
         appendDirtyJavaScript("if (confirm(\"");
         appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2SMREPEXTPP: Do you want to replace existing pre-postings?"));
         appendDirtyJavaScript("\")) {\n");
         appendDirtyJavaScript("	  document.form.PRPOSTCHANGED.value = \"TRUE\";\n");
         appendDirtyJavaScript("       f.submit();\n");
         appendDirtyJavaScript("     } \n");
      }
      //Bug 68947, Start   
      if (bFECust)
      {
         bFECust = false;
         appendDirtyJavaScript("validateCustomerNo(0)\n");
         appendDirtyJavaScript("if (getValue_('CONTRACT_ID',0) != '')\n");
         appendDirtyJavaScript("	 validateContractId(0)\n");
      }

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
      appendDirtyJavaScript("}\n");
      //Bug Id 70012, End

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

      //Bug Id 70921, Start
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
      appendDirtyJavaScript("		getField_('AUTHORIZE_CODE',i).value = '';\n");
      appendDirtyJavaScript("		getField_('AGREEMENT_ID',i).value = '';\n");
      appendDirtyJavaScript("		getField_('DESCRIPTION',i).value = '';\n");
      appendDirtyJavaScript("		return;\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	var requiredStartDate = getValue_('REQUIRED_START_DATE',i);\n");
      appendDirtyJavaScript("	var requiredEndDate = getValue_('REQUIRED_END_DATE',i);\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("	APP_ROOT+ 'pcmw/ActiveSeparate2ServiceManagement.page'+'?VALIDATE=CONTRACT_ID'\n");
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

      appendDirtyJavaScript("	if( checkStatus_(r,'CONTRACT_ID',i,'Contract ID') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('CONTRACT_ID',i,0);\n");
      appendDirtyJavaScript("		assignValue_('LINE_NO',i,1);\n");
      appendDirtyJavaScript("		assignValue_('CONTRACT_NAME',i,2);\n");
      appendDirtyJavaScript("		assignValue_('LINE_DESC',i,3);\n");
      appendDirtyJavaScript("		assignValue_('CUSTOMER_NO',i,4);\n");
      appendDirtyJavaScript("		assignValue_('CUSTOMERDESCRIPTION',i,5);\n");

      appendDirtyJavaScript("		assignValue_('AUTHORIZE_CODE',i,6);\n");
      appendDirtyJavaScript("		assignValue_('AGREEMENT_ID',i,7);\n");
      appendDirtyJavaScript("		assignValue_('DESCRIPTION',i,8);\n");
      appendDirtyJavaScript("		assignValue_('WORK_TYPE_ID',i,9);\n");
      appendDirtyJavaScript("		assignValue_('WORKTYPEDESCRIPTION',i,10);\n");
      appendDirtyJavaScript("		assignValue_('ORG_CODE',i,11);\n");
      appendDirtyJavaScript("		assignValue_('ORGCODEDESCRIPTION',i,12);\n");
      appendDirtyJavaScript("		assignValue_('PLAN_S_DATE',i,13);\n");
      appendDirtyJavaScript("		assignValue_('REQUIRED_START_DATE',i,14);\n");
      appendDirtyJavaScript("		assignValue_('PLAN_F_DATE',i,15);\n");
      appendDirtyJavaScript("		assignValue_('REQUIRED_END_DATE',i,16);\n");
      appendDirtyJavaScript("		assignValue_('PLAN_HRS',i,17);\n");
      appendDirtyJavaScript("		assignValue_('DUMMY_CONTRACT_ID',i,18);\n");
      appendDirtyJavaScript("		assignValue_('DUMMY_REQ_START_DATE',i,19);\n");
      appendDirtyJavaScript("		assignValue_('DUMMY_REQ_END_DATE',i,20);\n");

      appendDirtyJavaScript("		if (getValue_('DUMMY_CONTRACT_ID',i)=='' && !getValue_('CONTRACT_ID',i)=='' && !getValue_('LINE_NO',i)=='' && (requiredStartDate!='' || requiredEndDate!='')) \n");
      appendDirtyJavaScript("		{\n");
      appendDirtyJavaScript("       	message = '");
      appendDirtyJavaScript(     mgr.translateJavaScript("SRVCONDATEOVERWRITE2: Existing Date values in fields Required Start or Latest Completion will be replaced with the dates from Service Contract Line "));
      appendDirtyJavaScript("'		+ __getValidateValue(0) +' '+__getValidateValue(1);\n");
      appendDirtyJavaScript("       	window.alert(message);\n");
      appendDirtyJavaScript("		}\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

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
      appendDirtyJavaScript("	APP_ROOT+ 'pcmw/ActiveSeparate2ServiceManagement.page'+'?VALIDATE=LINE_NO'\n");
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
      appendDirtyJavaScript("		assignValue_('AGREEMENT_ID',i,4);\n");
      appendDirtyJavaScript("		assignValue_('DESCRIPTION',i,5);\n");
      appendDirtyJavaScript("		assignValue_('WORK_TYPE_ID',i,6);\n");
      appendDirtyJavaScript("		assignValue_('WORKTYPEDESCRIPTION',i,7);\n");
      appendDirtyJavaScript("		assignValue_('ORG_CODE',i,8);\n");
      appendDirtyJavaScript("		assignValue_('ORGCODEDESCRIPTION',i,9);\n");
      appendDirtyJavaScript("		assignValue_('PLAN_S_DATE',i,10);\n");
      appendDirtyJavaScript("		assignValue_('REQUIRED_START_DATE',i,11);\n");
      appendDirtyJavaScript("		assignValue_('PLAN_F_DATE',i,12);\n");
      appendDirtyJavaScript("		assignValue_('REQUIRED_END_DATE',i,13);\n");
      appendDirtyJavaScript("		assignValue_('PLAN_HRS',i,14);\n");
      appendDirtyJavaScript("		assignValue_('DUMMY_CONTRACT_ID',i,15);\n");
      appendDirtyJavaScript("		assignValue_('DUMMY_REQ_START_DATE',i,16);\n");
      appendDirtyJavaScript("		assignValue_('DUMMY_REQ_END_DATE',i,17);\n");
      appendDirtyJavaScript("		if (getValue_('DUMMY_CONTRACT_ID',i)=='' && !getValue_('CONTRACT_ID',i)=='' && !getValue_('LINE_NO',i)=='' && (requiredStartDate!='' || requiredEndDate!='')) \n");
      appendDirtyJavaScript("		{\n");
      appendDirtyJavaScript("       	message = '");
      appendDirtyJavaScript(     mgr.translateJavaScript("SRVCONDATEOVERWRITE2: Existing Date values in fields Required Start or Latest Completion will be replaced with the dates from Service Contract Line "));
      appendDirtyJavaScript("'		+ __getValidateValue(0) +' '+__getValidateValue(1);\n");
      appendDirtyJavaScript("       	window.alert(message);\n");
      appendDirtyJavaScript("		}\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");

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
      appendDirtyJavaScript("	APP_ROOT+ 'pcmw/ActiveSeparate2ServiceManagement.page'+'?VALIDATE=PRIORITY_ID'\n");
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

      appendDirtyJavaScript("function checkMando()\n");
      appendDirtyJavaScript("{\n");
      //Bug 92043, Start
      appendDirtyJavaScript("  return (checkHeadFields(i) && showWarnings(0));\n");
      //Bug 92043, End
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

      //Bug 82543, Start
      appendDirtyJavaScript("function refreshPage()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   f.REFRESH_FLAG.value = 'TRUE';\n");
      appendDirtyJavaScript("   f.submit();\n");
      appendDirtyJavaScript("}\n");
      //Bug 82543, End

      //Bug Id 70921, End

   }
}
