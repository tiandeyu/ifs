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
*  File        : ActiveSeparate2.java 
*  Created     : ASP2JAVA Tool  010214
*  Modified    :  
*  BUNILK  010222  Corrected some conversion errors.
*  BUNILK  010307  Corrected some errors in state changing functions of Material tab.   
*  BUNILK  010405  Disabled new button of material line block when the material state is "Closed"; 
*                  Modified preDefine() method so as to filter standerd job LOV by site.
*  BUNILK  010417  Changed deprecated getQueryString() method to getQueryStringValue() new method.     
*                  Done some corrections in javascript codes. 
*  JEWILK  010419  Removed Pre Posting actions in Material tab (Call 62860)
*  JEWILK  010423  Modified SaveReturn() to properly save the fields 'Warranty Type' & 'Supplier'
*                  when returned from the rmb 'Supplier Warranty Type'. (Call 77349)
*  CHDELK  010424  Made necessary corrections for Maint Material Unissue RMB               
*  BUNILK  010425  Disabled edit,duplicate and delete buttons of material line block when the material state is "Closed";   
*  INROLK  010430  Changed Sales Price to ReadOnly.
*  BUNILK  010430  Modified validation of signature field.
*  JEWILK  010526  Changed the LOV of the field 'PRICE_LIST_NO' to 'SALES_PRICE_LIST_LOV'. (call #65481) 
*                  Set fields 'SPARE_CONTRACT' and 'CATALOG_CONTRACT' editable.
*                  Overwritten javascript function 'lovCatalogNo()'.
*  JEWILK  010529  Modified the validation of field 'PRICE_LIST_NO' to fetch the Sales price correctly, 
*                  when the 'PART_NO' is not yet entered.
*  BUNILK  010608  Modified all validations of Material line.  
*  BUNILK  010611  Chnged all the overwritten javascript validation functions so as to work with netscape browser.  
*  BUNILK  010617  Modified transToMob() method so that to check whether required view is installed in database. 
*  BUNILK  010628  Called search methods of Craft and Material blocks after saving a new record of head block.  
*  BUNILK  010716  Modified PART_NO field validation. Added Copy... Action.
*  BUNILK  010806  Made some changes in return AutoString of getContents() function so that to call       
*                  checkPartVal() javascript function from onBlur event of PART_NO field. Done some changes in 
*                  validatePartNo() method also. Modified sparePartObject() method and associated javascripts. 
*  CHCRLK  010807  Modified methods printWO() and printPicList().
*  BUNILK  010813  Added new Action "Pick List For Work Order - Printout.." to header part.
*  BUNILK  010818  Modified planning() method.
*  NUPELK  010821  Modified detchedPartList() method and its pop up window call 
*  ARWILK  010904  Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction(). 
*                  (Caused when cancelNew is performed after saveNew)  
*  NUPELK  010904  Modified data transfer to and from the form Detached Spare Parts List 
*  BUNILK  010918  Added Pre Posting Action to Material and Material requisition blocks.  
*  BUNILK  010927  Added security check for Actions.
*  BUNILK  011011  Added addCommandValidConditions() method for headblk state changing actions.  
*  JEWILK  011015  Modified validations of fields 'PLAN_QTY','CATALOG_NO','PRICE_LIST_NO' to 
*                  correctly display 'Sales Price' and 'Discounted Price'.
*  BUNILK  011016  Added Back hyperlinkt to Material block and a new field "Man Hours" to headblk. 
*  INROLK  011019  Added Decode to Warranty Description. call id 70691.
*  ARWILK  020326  Changed RMB's SparePartInObject and ObjectStructure so that they work when there are no records in itemset.
*  KUWILK  020401  Call ID 78674 corrected.
*  SHAFLK  020724  Bug Id 31771 Added security check for MOBMGR_WORK_ORDER_API.Check_Exist.
*  BUNILK  020906  Bug ID 32182 Added a new method isModuleInst() to check availability of 
*                  DOCMAN,SERORD and ORDER module inside preDefine method.
*  SHAFLK  020916  Bug Id 32871, Modified newRowITEM2.
*  SHAFLK  021108  Bug Id 34064, Changed methods printPicList,pickListForWork & printWO.
*  BUNILK  021118  Added three new fields (Object Site, Connection Type and Object description) and replaced 
*                  work order site by Object site whenever it refer to Object.   
*  YAWILK  021209  Added SPARE_CONTRACT parameter to RBM option RMBEquipmentSpareStructure.page        
*  BUNILK  021213  Merged with 2002-3 SP3
*  CHAMLK  021217  Added Instruction Type, Location, Sign Requirement, Reference Number, Product No, Model No, 
*                  Function No, Sub Function No, Bottom Function No to the crafts tab. Extended the length of 
*                  the Remarks field to 2000.
*  CHAMLK  030101  Added a new action called Maint Task.
*  NIMHLK  030110  Added a new action to call condition code dialog according to specification W110 - Condition Codes.
*  JEJALK  030331  Renamed Crafts tab to Operations and Row No to Operations No and Craft Line no to Operation No.
*  SHAFLK  030306  Made all calls to apis in non static components dynamically.
*  CHAMLK  030403  Added new tab Tools and Facilities.
*  JEJALK  030404  Added new method (checkToolFacilitySite) to check invalid Tools/Facility details when changing States.
*  CHAMLK  030407  Added a new LOV called ConnectToolsFacilitiesLov for fields tool facility id, site and department in 
*                  block 5 (Tools and Facilities) to fetch tool facility id, site and department from the same lov line.
*  JEJALK  030407  Added checkToolFacilitySite method to the finished method.
*  JEJALK  030430  Added Condition Code and Condition Code Description to the Material tab.
*  JEJALK  030502  Added "Issued Serilas" action  to the Lines of the Material tab.
*  JEJALK  030503  Added "Available to Reserve" to lines of the Materials tab. 
*  CHCRLK  030506  Corrected and modified actions Reserve Manually and Issue Manually.
*  JEJALK  030506  Modified the " Issued Serials" action.
*  INROLK  030619  Added RMB Returns.
*  CHCRLK  030724  Added function checkHasVimAgreement() & Has Agreements checkbox to header.
*  CHAMLK  030729  Added Part Ownership, Owner and Owner Name to Materials tab.
*  CHCRLK  030801  Made the Has Agreements checkbox hidden in find mode. 
*  CHCRLK  030929  Added function lovMchCode to enabled the MCH_CODE lov in find mode. 
*  ARWILK  031008  Moved check boxes from 'General' group to the 'Work Order' group.
*  JEWILK  031010  Modified javascript function checkMando() to check ConnectionType as well. Call 105294.
*  JEWILK  031016  Removed setUpperCase() method from CONNECTION_TYPE. Set Number mask # to WO_NO.
*  CHAMLK  031018  Modifed validations of std job, customer no and agreement id in order to update the work order with valid 
*                  agreement prices, if one exists.  
*  JEWILK  031021  Made field CONNECTION_TYPE readonly in New mode. Call 105294.
*  CHAMLK  031024  Modified procedure preDefine() to modify the format of the ITEM_WO_NO
*  JEWILK  031107  Added field SIGN_ID and its validation. Set field SIGN as Read Only. Call 110141.
*  CHAMLK  031110  Added Catalog Contract as a hidden field to the Operations tab (itemblk2).
*  ARWILK  031204  Edge Developments - (Replaced blocks with tabs, Replaced State links with RMB's, Removed Clone and Reset Methods)
*  ARWILK  031223  Edge Developments - (Replaced links with multirow RMB's)
*  SHAFLK  031223  Bug Id 40919,Changed copy() method.
*  ARWILK  040108  Edge Developments - (Enable Multirow RMB actions, Remove uneccessary global variables, Enhance Performance, Clean Javascript Code)
*  YAWILK  040121  Replaced 'ACTIVE_WORK_ORDER1_API' with 'ACTIVE_WORK_ORDER_API'.
*  VAGULK  040202  Edge Developments - Arranged field order according to the Centura Application.
*  SAPRLK  040209  Web Alignment - calls to ActiveSeparate2RMB.page has been set to ActiveSeparateReportCOInfo.page.
*  ARWILK  040308  (Bug#112601) Removed pseudo columns from the headblk. (They are not mandatory for the addCommandValidConditions).
*  THWILK  040317  Call ID 112865 - Modified prePost() and added javascript code to open the preposting page in another window.
*  SHAFLK  040116  Bug Id 41815,Removed Java dependencies.
*  SHAFLK  040213  Bug Id 40256, Modified sparePartObjectJS() and objStructureJS(). 
*  SHAFLK  040225  Bug 40788, Changed validation of SIGN_ID .
*  SAPRLK  040323  Merge with SP1.
*  SAPRLK  040421  Corrected Call 114222.
*  ARWILK  040430  Added new field CBPROJCONNECTED.
*  SAPRLK  040603  Added key PM_REVISION, new lov for PM_NO, and to validate the PM_REVISION.
*  ARWILK  040609  Removed the field STD_JOB_ID (For IID AMEC109A).
*  ARWILK  040617  Added a new job tab. Added job_id to materials, operations, tools and facilities tabs.(Spec - AMEC109A)  
*  SHAFLK  040416  Bug 43848, Modified methods run(), finished() and HTML part.Added methods CheckObjInRepairShop() and finshed1().
*  THWILK  040625  Merged Bug 43848.
*  ARWILK  040629  Added RMB's connectToActivity and disconnectFromActivity (IID AMME613A - Project Umbrella)
*  ARWILK  040709  Added ORG_CONTRACT to the itemblk2. (IID AMEC500C: Resource Planning) 
*  VAGULK  040721  Added fields "SUPPLY_CODE ,SUPPLY_CODE_DB and ACTIVITY_SEQ" to Material tab (SCME612 - Project Inventory)
*  SHAFLK  040708  Bug 41817, Modified methods run(), finished(), reported() and HTML part.Added methods CheckAllMaterialIssued() and reported1().
*  ThWilk  040810  Merged Bug 41817 and changed method call Make_Issue_Detail.
*  SHAFLK  040421  Bug 42866, Modified method issueFromInvent()
*  ThWilk  040818  Merged Bug 42866.
*  ThWilk  040819  Call 116835,Modified okFind(),activateOperations(),activateToolsAndFacilities(),activateMaterial(), and activateJobs() to improve performance. 
*  ARWILK  040820  Changed LOV's of STD_JOB_ID and STD_JOB_REVISION.
*  NIJALK  040820  Call 116952, Modified adjust().
*  NIJALK  040824  Call 116946, Modified predefine(),validate(),getContents().
*  ThWilk  040824  Call 117106, Modified availtoreserve().
*  BUNILK  040825  Changed server function of QTYONHAND field.
*  ThWilk  040825  Call 117308, Modified checkToolFacilitySite(),released(),started().
*  NIJALK  040826  Call 117104, Modified method run().
*  VAGULK  040826  Corrected Calls 117249 and 117251,fetched the default values of "Owernership" and "Supply_code" in the new layout.
*  ARWILK  040830  Call ID 117423, Modified predefine and removed method findCostAmount.
*  NIJALK  040830  Call 117450, Modified preDefine().
*  ARWILK  040901  Resolved Material status refresh problem.
*  NIJALK  040901  Call 117415, Modified predefine(). Added saveReturnITEM6().
*  SHAFLK  040722  Bug 43249, Modified validations of PART_NO, CONDITION_CODE, PART_OWNERSHIP and OWNER and added some fields to predefine and modified HTML part. 
*  NIJALK  040902  Merged 43249.
*  NIJALK  040908  Call 117750: Modified preDefine().
*  NIJALK  040909  Modified validate(). Removed error msg given when sign_id does not exist.
*  ARWILK  040910  Modified availtoreserve.
*  NIJALK  040922  Modified perform().
*  BUNILK  040923  Modified availtoreserve() method.
*  NIJALK  040929  Added parameter project_id to method calls to Inventory_Part_In_Stock_API.Get_Inventory_Quantity.
*  BUNILK  040930  Changed server function of QTYONHAND field and modified validations of Part No, Condition Code, Ownership and owner fields of Material tab..
*  NIJALK  041001  Renamed 'Work Master' to 'Executed By' in head block & 'Signature' to 'Executed By' in Jobs,Operaions Tabs. 
                    Modified validations of SIGN,SIGN_ID.Set validation for ITEM2_JOB_ID.
*  ARWILK  041005  LCS Merge: 46434.
*  ARWILK  041005  LCS Merge: 46394.
*  SHAFLK  040906  Bug 46542, Modified method matReqUnissue().
*  NIJALK  041007  Merged 46542. 
*  SHAFLK  040812  Bug 45904, Modified methods issueFromInvent(), manissue() and HTML part.
*  NIJALK  041007  Merged 45904.
*  ARWILK  041022  LOV Change for Fields STD_JOB_ID,STD_JOB_REVISION. (Spec AMEC111 - Standard Jobs as PM Templates)
*  BUNILK  041026  Modified validations of material tab, added new validation to supply code, modified new method of material tab
*  NIJALK  041028  Modified validate(), preDefine().
*  SHAFLK  040916  Bug 46621, Modified validations of PART_NO, CONDITION_CODE, PART_OWNERSHIP and OWNER. 
*  Chanlk  041105  Mergde Bug 46621.
*  NIJALK  041105  Added Std Job status to Jobs tab. Modified predefine(), validate().
*  NIJALK  041109  Added check box "Has Obsolete Jobs".
*  ARWILK  041110  Replaced getContents with printContents and removed obsolete code.
*  NIJALK  041111  Made the field "Employee ID" to visible & read only in jobs and operations tabs.
*  NIJALK  041115  Replaced method Standard_Job_API.Get_Work_Description with Standard_Job_API.Get_Definition.
*  ARWILK  041118  Added two new RMB's freeTimeSearch, freeTimeSearchFromToolsAndFac and freeTimeSearchFromOperations.
*  NIJALk  041201  Added new parameters to method calls to Inventory_Part_In_Stock_API.Get_Inventory_Quantity.
*  Chanlk  041213  Bug Id 120439, Removed field LAST_ACTIVITY_DATE.
*  HIWELK  041229  Bug 48478, Changed 'Remark' field to a multiline field.
*  Chanlk  050103  Mergde Bug 48478.
*  NIJALK  040105  Modified printContents(),validate(),predefine().
*  Chanlk  050113  Change the field properties of remark.
*  NAMELK  050120  Materials Tab: Check Box MUL_REQ_LINE added,Set LOV to field ITEM3_CONTRACT. Code added to adjust().
*  NIJALK  050202  Modified detchedPartList(),sparePartObject(), run(), adjust(),validate(),preDefine().
*  NIJALK  050222  Added function duplicateRow(). Modified preDefine().
*  NIJALK  050224  Modified preDefine(), saveReturnItem(). Added deleteItem().
*  NAMELK  050224  Merged Bug 48035 manually.
*  DIAMLK  050228  Replaced the field Pre_Posting_Id by Pre_Accounting_Id.(IID AMEC113 - Mandatory Pre-posting)
*  DIAMLK  050310  Bug ID:122505 - Modified the method okFind().
*  DIAMLK  050317  Bug ID:122708 - Modified the method newRowITEM6().
*  DIAMLK  050321  Modified the method run().
*  NIJALK  050405  Call 123081: Modified manReserve().
*  NEKOLK  050407  Merged - Bug 48852, Modified issueFromInvent and reserve().
*  NIJALK  050407  Call 123086: Modified reserve(), manReserve().
*  NIJALK  050504  Bug 123677: Added RMB "Project Activity Info...".
*  NIJALK  050505  Bug 123698: Set a warning msg when changing state to "Prepared" if WO causes a project exception.
*  NIJALK  050505  Bug 123099: Modified validate(), preDefine(), printContents().
*  THWILK  050517  Added the functionality required under Manage Maintenance Jobs(AMEC114).
*  THWILK  050519  Minor changes done under Manage Maintenance Jobs(AMEC114).
*  NIJALK  050519  Modified availDetail(),preDefine().
*  THWILK  050520  Modified translation constant for disconnect Operations in preDefine().
*  THWILK  050521  Added Planned Hours column to Jobs Tab.
*  THWILK  050526  Modified predefine() and okFindItem7().
*  SHAFLK  050330  Bug 50258, Modified issueFromInvent and manIssue().
*  NIJALK  050527  Merged 50258.
*  THWILK  050608  Added the functionality required under Work Order Team & Qualifications(AMEC114).
*  THWILK  050610  Modified validate() & javascript methods.
*  THWILK  050610  Modified lovItem7Sign javascript method.
*  DiAmlk  050613  Bug ID:124832 - Renamed the RMB Available to Reserve to Inventory Part in Stock... and
*                  modified the method availtoreserve.
*  THWILK  050615  Modified printContents(),lovItem2OrgCode,lovItem7OrgCode,lovRoleCode,lovItem7RoleCode,lovTeamId,lovItem7TeamId,lovSign,
*                  lovItem7Sign & added lovItem2Contract,lovItem7Contract,lovTeamContract,lovItem7TeamContract javascript methods.
*  SHAFLK  050425  Bug 50830, Used mgr.translateJavaScript() and getJSLabel() methods to get the correctly translated strings in JavaScript functions.
*  NIJALK  050617  Merged bug 50830. 
*  THWILK  050622  Modified run(),additionalQualifications1() and additionalQualifications2().
*  THWILK  050630  Added the RMB "Predecessors" under Work Order Dependencies(AMEC114). 
*  THWILK  050705  Modified disconnectOperations(). 
*  THWILK  050707  Modified methods run(),additionalQualifications1(),additionalQualifications2(),predecessors1(),
*                  predecessors2() and connectExistingOperations().
*  SHAFLK  050512  Bug 51147, Modified label of OP_STATUS_ID.
*  THWILK  050718  Merged LCS Bug 51147.
*  THWILK  050719  Modified run().
*  THWILK  050725  Added methods resheduleOperations1(),resheduleOperations2(),resheduleOperations3()
*                  moveOperation1(),moveOperation2() and modified predefine() and run().(WO Dependencies spec)
*  THWILK  050727  Call 125919- Modified predefine().
*  NIJALK  050805  Bug 126194: Modified saveReturn(). Set value for qrystr.
*  NIJALK  050808  Bug 126177: Modified released(),started(),perform().
*  NIJALK  050809  Bug 126196, Modified adjust().
*  NIJALK  050905  Bug 126137: Modified availtoreserve().
*  NIJALK  050905  Bug 126825: Modified preDefine().
*  THWILK  050920  Added some required functionality under search for and allocate employees spec.
*  THWILK  050923  Modified selectAllocation0,clearAllocation0,selectAllocation1,clearAllocation1,selectAllocation2,clearAllocation2.
*  SHAFLK  050919  Bug 52880, Modified checkHasVimAgreement() and adjust().
*  NIJALK  051005  Merged bug 52880.
*  THWILK  051013  Added the required functionality under AMEC114:Multiple Allocations.
*  SHAFLK  051011  Bug 51044, Added method duplicateITEM5().
*  NIJALK  051014  Merged bug 51044. 
*  THWILK  051018  Modified Validate().
*  THWILK  051021  Modified Predefine(). 
*  AMNILK  051024  Call Id: 128088.Modified Adjust(). 
*  THWILK  051027  Call Id: 128092.Modified Item7PlanMenCheck(),Item2PlanMenCheck,saveReturnITEM2 and run methods.Added saveReturnITEM7. 
*  NIJALK  051031  Bug 128243: Modified validate().
*  THWILK  051109  Modified Predefine().
*  NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile).
*  NEKOLK  051125  Bug 54415, Changed server method Get_Contract to Get_Def_Contract .
*  NIJALK  051220  Merged bug 54415.
*  ERALLK  051214  Bug 54280. Added column 'Quantity Available'.Modified the validate() function.
*  NIJALK  051227  Merged bug 54280.
*  NIJALK  060109  Changed DATE format to compatible with data formats in PG19.
*  THWILK  060126  Corrected localization errors.
*  NIJALK  060210  Call 133546: Renamed "STANDARD_INVENTORY" with "INVENT_ORDER".
*  NIJALK  060213  Call 132956: Modified projectActivityInfo().
*  SULILK  060222  Call 134906: Modified manReserve(), GetInventoryQuantity(), manIssue(). 
*  NEKOLK  060223  Call 135228: Modified  duplicateRow().
*  NEKOLK  060308  Call 135828: Added refreshForm() and made changes in predefine().
*  THWILK  060308  Call 136743, Modified Predefine().
*  NIJALK  060315  Call 137380: Set field COMPANY to Global. 
*  SULILK  060316  Modified preDefine(),validate(),setValuesInMaterials() and added saveReturnITEM4().
*  ASSALK  060316  Material Issue & Reserve modification. Issue and reserve made available after
*                    unissue all materials.
*  NIJALK  060321  Rollback the functionality added by 137380. Removed the Global Connection.
*  NIJALK  060323  Call 137380: Overwrite java scripts for LOVs for REPORTED_BY,PREPARED_BY,WORK_LEADER_SIGN & WORK_MASTER_SIGN to show LOV in FIND mode.
* ----------------------------------------------------------------------------
*  NIJALK  060509  Bug 57099, Modified matReqUnissue().
*  NIJALK  060510  Bug 57256, Modified manIssue(), GetInventoryQuantity().
*  NIJALK  060515  Bug 56688, Modified run(), issueFromInvent(), checkObjAvaileble(), adjust().
*  SHAFLK  060516  Bug 57826, Modified okFindItem3() and okFindItem4()
*  SHAFLK  060525  Bug 57598, Modified validation for PART_NO and and setValuesInMaterials().
*  SHAFLK  060703  Bug 59071, Modified validation for STD_JOB_FLAG and function checkITEM6SaveParams() and newRowITEM7().
*  SHAFLK  060704  Bug 59115, Modified validation for STD_JOB_FLAG and function checkITEM6SaveParams().
*  SHAFLK  060721  Bug 59115, Modified validations.
*  AMNILK  060629  Merged with SP1 APP7.
* ----------------------------------------------------------------------------
*  AMDILK  060724  Merged with the Bug ID 59071
*  AMDILK  060725  Merged with the Bug ID 59115
*  ILSOLK  060727  Merged Bug ID 59224.
*  CHCRLK  060727  Modified method run() to handle data transfer from VIM.
*  JEWILK  060803  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged Bug Id: 58214.
*  ILSOLK  060817  Set the MaxLength of Address1 as 100.
*  JEWILK  060816  Bug 58216, Eliminated SQL Injection security vulnerability.
*  NAMELK  060905  Merged bug 58216.
*  NEKOLK  061012  Bug 60835, Modified preDefine() validate() and Java Script part.
*  AMNILK  061102  Merged LCS Bug 60835.
*  SHAFLK  061101  Bug 61515, Modified method adjust().
*  AMNILK  061106  Merged LCS Bug ID: 61515.
*  SHAFLK  061005  Bug 60938, Added new RMB "Activity Info..." 
*  NAMELK  061107  Merged Bug 60938.
*  SHAFLK  060731  Bug 59613, Modified changeConditionCode(). 
*  SHAFLK  070116  Bug 62854, Modified validation for Part No.
*  AMNILK  070208  Merged LCS Bug 62854.   
*  SHAFLK  070108  Bug 62784, Modified preDefine().
*  AMNILK  070209  Merged LCS Bug Id: 62784.
*  ILSOLK  070215  Modifed for MTPR904 Hink tasks ReqId 45088.setLovPropery() for Object Id.
*  SHAFLK  061120  Bug 61466, Added 'Supplier Loaned' stockt to Materials.
*  ILSOLK  070302  Merged Bug ID 61446.
*  BUNILK  070405  Implemented "MTIS907 New Service Contract - Services" changes.
*  SHAFLK  070228  Bug 63812, Modified printPicList.
*  ILSOLK  070410  Merged Bug Id 63812.
*  ARWILK  070410  MTIS907: Replaced calls to SERIAL_PART_AGR_DETAILS_API with Psc_Contr_Product_API. Also added hidden field LINE_NO to header.
*  AMDILK  070427  Call Id 142273: Inserted a new RMB "Update Spare Parts in Object" to the materials tab
*  AMDILK  070507  Call Id 143805: Modified freeTimeSerach(), insert an attribute to the query string
*  CHANLK  070517  Call 144491 Added Tranfered to mobile check box in preDefine()
*  AMDILK  070518  Call Id 144691: Inserted OBJSTATE to the buffer. Modified requisitions()
*  ASSALK  070523  Call ID 144544: Modified preDefine().
*  AMDILK  070531  Call Id 145443: Disable the RMB "Update Spare Parts in Object" when spare parts exists 
*  ASSALK  070531  Call ID 145441: Added script function validateDates().
*  ASSALK  070604  Call 145441. Modified validate(),preDefine(),printContents(), removed script function validateDates().
*  CHANLK  070606  Call 145149 Change disconnectFromActivity() set "PROJECT_NO" null
*  AMDILK  070608  Call Id 144694: Modified okFindITEM5()
*  AMDILK  070614  Call Id 144694: Preserve the line information when navigate the last and first record
*  AMDILK  070622  Call Id 146307: Calculated the planned completion time when the execution time is given
*  ILSOLK  070625  Eliminated SQL errors in web applications.
*  ILSOLK  070626  Modified cancelled() & perform().(Call Id 146421)
*  ASSALK  070626  Call ID 144849: Modified printContent(), run(), cancelled() and added method refreshRows().
*  CHANLK  070703  Call 145181 LovTeamId date format not supported by oracle.
*                  Modified printContents(), lovTeamId, lovItem7TeamId,lovItem8TeamId & editRowItem2, editRowItem8, editRowItem7.
*  ILSOLK  070705  Added SQLInjection comments,Eliminated XSS.
*  BUNILK  070525  Bug 65048 Added format mask for PM_NO field.
*  CHANLK  070711  Merged bugId 65048
*  NIJALK  070524  Bug 64744, Modified run(), saveReturn(), preDefine() and printContents(). Added updatePostings().
*  AMDILK  070716  Merged bug 64744
*  CHODLK  070416  BUg 64572, Modified method preDefine().
*  ILSOLK  070718  Merged Bug Id 64572.
*  AMDILK  070731  Removed the scroll buttons of the parent when the child tab(Jobs) is in new or edit modes
*  AMNILK  070827  Call Id: 147707. Modified issueFromInvent().
*  ASSALK  070831  Call ID: 148009. Change rmb text 'Search Engine' to 'Free Time Search'.
*  ILSOLK  070905  If WOState in ('RELEASED', 'STARTED', 'WORKDONE','PREPARED','UNDERPREPARATION') Enabled Release RMB in Material.
*                  Modified preDefine()(Call ID 148213)
*  ASSALK  070911  Call 148513. Modified issueFromInvent().
*  ILSOLK  070913  Object Id filterd using Connection Type.(Call ID 148826) 
*  ILSOLK  070914  Test Point Id filtered using contract & object(Call Id 148872.)
*  SHAFLK  071108  Bug 67801, Modified validation for STD_JOB_ID.
*  SHAFLK  071123  Bug 69392, Checked for ORDER installed.
*  NIJALK  071207  Bug 69841, Modified preDefine().  
* ----------------------------------------------------------------------------
* -----------------------APP7.5 SP1-------------------------------------
*   071214  ILSOLK     Bug Id 68773, Eliminated XSS.
*   071219  SHAFLK    Bug 69914, Added setStringLables().
*   SHAFLK  071219    Bug 70147, Modified saveReturnITEM6(). 
*   SHAFLK  080102  Bug 70891, Modified finished() and finished1().
*   NIJALK  080202  Bug 66456, Modified validate(), GetInventoryQuantity(), availDetail() & printContents(). 
*   SHAFLK  080107  Bug 70948, Modified function lovSign.
*   SHAFLK  080130  Bug 70815, Modified run(), issue(), reserve(), preDefine() okFindITEM4() and okFindITEM3(). 
* -----------------------------------------------------------------------
*   AMNILK  080225  Bug Id 70012, Modified coInformation(),reportInWorkOrder().
*   NIJALK  080306  Bug 72202, Modified okFindITEM3(), okFindITEM4().
*   AMNILK  080401  Bug Id 70921, Modified validate() for plan_hrs.
*   SHAFLK  080421  Bug 73203, Modified preDefine().
*   ASSALK  080507  Bug 72214, Winglet merge.
*   SHAFLK  080514  Bug 73688, Added labels for set simpled fields.
*   ASSALK  080514  Bug 72214, corrections done to checkHasVimAgreement().
*   SHAFLK  080516  Bug 73800, Modified preDefine().
*   ASSALK  080528  Bug 72214, Modified perDefine().
*   SHAFLK  080528  Bug 73800, Modified okfind().
*   ASSAKL  080811  Bug 75641, Modified validate().
*   NIJALK  080820  Bug 72644, Added RMB "Optimize Planning Schedule...".
*   SHAFLK  080908  Bug 76867, Modified preDefine().
*   SHAFLK  080924  Bug 77304, Modified preDefine().
*   SHAFLK  081105  Bug 77824, Modified Validate().
*   SHAFLK  081121  Bug 78187, Modified issue().
*   SHAFLK  090217  Bug 79436  Modified preDefine(), released() and started(). 
*   CHANLK  090225  Bug 76767, Modified preDefine(), validate(), issue(), reserve().
*   SHAFLK  090305  Bug 80959  Modified preDefine() and added reinit(). 
*   SHAFLK  090630  Bug 82543  Modified detchedPartList(). 
*   NIJALK  090704  Bug 82543, Modified run() and printContents().
*   HARPLK  090708  Bug 84436, Modified preDefine().
*   CHANLK  090721  Bug 83532, Added RMB Work Order Address.
*   SHAFLK  090728  Bug 84886, Modified printContents().
*   CHANLK  090730  Bug 82934  Column renamed. ToolFacility.Quantity to Planed Quantity.
*   SHAFLK  090826  Bug 85531, Modified duplicateRow().
*   SHAFLK  090916  Bug 85917, Modified preDefine().
*   SHAFLK  090921  Bug 85901, Modified preDefine().
*   SHAFLK  091001  Bug 86269, Modified preDefine().
*   NIJALK  100218  Bug 87766, Modified setCheckBoxValues(), run() and preDefine().
*   SHAFLK  100318  Bug 89298, Modified performItem().
*   NIJALK  100419  Bug 87935, Modified adjust().
*   NIJALK  100420  Bug 85045, Modified javascript functions checkMandoItem2() and checkItem8Fields().
*   SHAFLK  100427  Bug 90204, Modified editRowITEM2().
*   CHANLK 	100707  Bug 91191  Change validation in field Customer Name
*   VIATLK  100721  Bug 91376, Modified printWO() and pickListForWork().
*   ILSOLK  100723  Bug 92020, Modified cancelled().
*   SaFalk  100731  Bug 89703, Added methods deleteRow6(), deleterITEM6() and modified methods run(), preDefine() and printContents().
*   SaFalk  100814  Bug 90872, Modified preDefine().
*   ILSOLK  100916  Bug 93061, Modified saveReturn().
*   SHAFLK  100916  Bug 93011, Modified performItem().
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

public class ActiveSeparate2 extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeparate2");

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

    private ASPBlock itemblk;
    private ASPRowSet itemset;
    private ASPCommandBar itembar;
    private ASPTable itemtbl;
    private ASPBlockLayout itemlay;

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

    private ASPField f;
    private ASPBlock blkPost;
    private ASPBlock teblk;
    // 031204  ARWILK  Begin  (Replace blocks with tabs)
    private ASPTabContainer tabs;
    // 031204  ARWILK  End  (Replace blocks with tabs)

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private String cbwarranty_var;
    private String current_url;
    private String qrystr;
    private String transferred;
    private String openCreRepNonSer;
    private String creRepNonSerPath;
    private ASPTransactionBuffer trans;
    private boolean canPerform;
    private boolean comBarAct;
    private String rRowN;
    private String rRawId;
    private boolean supWar;
    private String rWarrType;
    private boolean matSingleMode;
    private String noteTextEntered;
    private ASPBuffer buff;
    private String rWarrDesc;
    private ASPBuffer temp;
    private ASPCommand cmd;
    private ASPTransactionBuffer secBuff;
    private String val;
    private ASPBuffer buf;
    private String suppl;
    private String supplier;
    private String suppdesc;
    private double colPlanHrs;
    private double colPlanMen;
    private double colAmountCost;
    private String numCost;
    private ASPQuery q;
    private ASPBuffer retBuffer;
    private String ret_wo_no;
    private ASPBuffer data;
    private ASPCommand c;
    private ASPBuffer row;
    private ASPBuffer r;
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
    private int noRows;
    private ASPBuffer prePostBuffer;
    private ASPBuffer returnBuffer;
    private ASPBuffer buffer;
    private String title_;
    private String objState;  
    private String txt; 
    private String showMat;
    private String isSecure[];
    private String securityOk;      
    private int currrowItem4;
    private int currrow;
    private String repair;
    private String unissue;
    private String clearAlloc0;
    private String clearAlloc1;
    private String clearAlloc2;
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
    private boolean again;
    private boolean bUnequalMatWo;
    private boolean bNoWoCust;
    // 031204  ARWILK  Begin  (Replace links with RMB's)
    private boolean bOpenNewWindow;
    private String urlString;
    private String newWinHandle;
    // 031204  ARWILK  End  (Replace links with RMB's)
    private String transferurl;
    private double nActivitySeqNo;
    private ASPBuffer ret_data_buffer;
    private String lout;
    private int enabl10;
    private boolean bException;
    private String headNo;
    private int newWinHeight = 600;
    private int newWinWidth = 900;
    private String rowString;
    private int headRowNo;
    private int currentRow;
    private String planMen="";
    private String openCancelDlg = "";
    private String sWoNo = "";
    private boolean bPpChanged;
    private String strLableB;
    private String strLableE;
    private String strLableF;
    // Bug 72214, start
    private String sIsLastWo = "";
    // Bug 72214, end
    // Bug 92020, Start
    private boolean bvimwo;
    // Bug 92020, End 
    //Bug 89703, Start
    private String hasPlanning;
    //Bug 89703, End

    //===============================================================
    // Construction 
    //===============================================================
    public ActiveSeparate2(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        cbwarranty_var =  "";
        current_url =  "";
        qrystr =  "";
        transferred = "";
        objState = "";
        openCreRepNonSer = creRepNonSerPath;
        creRepNonSerPath = "";
        //Bug 89703, Start
        hasPlanning = "";
        //Bug 89703, End
        
        ASPManager mgr = getASPManager();

        trans = mgr.newASPTransactionBuffer();
        ctx = mgr.getASPContext();
        fmt = mgr.newASPHTMLFormatter();

        canPerform = ctx.readFlag("CANPRF",false);
        comBarAct = ctx.readFlag("COMBARACT",false);
        qrystr = ctx.readValue("QRYSTR",qrystr);
        rRowN = ctx.readValue("RROWN","");
        rRawId = ctx.readValue("RRAWID","");
        supWar = ctx.readFlag("SUPWAR",false);
        rWarrType = ctx.readValue("RWARRTYPE","");
        rRowN = ctx.readValue("RROWN","");
        rRawId = ctx.readValue("RRAWID","");
        transferred = ctx.readValue("TRANSFERRED","");
        objState = ctx.readValue("OBJESTATE","");
        headRowNo = ctx.readNumber("HEADROWNO",0);
        currentRow = ctx.readNumber("CURRENTROW",0);


        matSingleMode = ctx.readFlag("MATSINGLEMODE",false);
        showMat = ctx.readValue("SHMAT","");
        transferurl = ctx.readValue("TRNURL","");


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

        again = ctx.readFlag("AGAIN",false);
        repair =  ctx.readValue("REPAIR","FALSE");

        if (mgr.commandBarActivated())
        {
            comBarAct = true; 
            eval(mgr.commandBarFunction());
            if ("HEAD.SaveReturn".equals(mgr.readValue("__COMMAND")))
                headset.refreshRow();
            if ("ITEM2.Delete".equals(mgr.readValue("__COMMAND")))
                headset.refreshRow();
            if ("ITEM8.Delete".equals(mgr.readValue("__COMMAND")))
                headset.refreshRow();
            //if ("ITEM4.SaveReturn".equals(mgr.readValue("__COMMAND")))
            //    itemset.refreshRow();
            if ("ITEM2.SaveReturn".equals(mgr.readValue("__COMMAND")))
            {
                headset.refreshRow();
                if (itemset2.countRows()>0)
                {
                    itemset2.refreshAllRows();

                }
            }
            if ("ITEM8.SaveReturn".equals(mgr.readValue("__COMMAND")))
            {
                headset.refreshRow();
                itemset8.refreshRow();
            }
            if ("ITEM4.SaveReturn".equals(mgr.readValue("__COMMAND")))
            {
                headset.refreshRow();
                itemset.refreshRow();
                setValuesInMaterials();
            }
    

        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        // Bug 89703, Start
        else if ("AAAA".equals(mgr.readValue("HASPLANNING")))
           deleteRow6("YES");
        else if ("BBBB".equals(mgr.readValue("HASPLANNING")))
           deleteRow6("NO");
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
                okFindITEM8();
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
                okFindITEM8();
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
                okFindITEM8();
            }
        }
        else if ("TRUE".equals(mgr.readValue("PUSH_FINDMODE"))) {
           refreshRows();
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

                okFindITEM2();
                okFindITEM3();
                okFindITEM5();
                okFindITEM6();
            }
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


        else if ("CANCE".equals(mgr.readValue("STATEVAL")))
            cancelled();
        else if ("CCCC".equals(mgr.readValue("BUTTONVAL")))
        {
            unissue="FALSE";
            reported1();
        }
        else if ("AAAA".equals(mgr.readValue("BUTTONVAL")))
        {
            repair="FALSE";
            unissue="FALSE";
            finished1();
        }
        else if ("REPORT".equals(mgr.readValue("STATEVAL")))
        {
            unissue="FALSE";
            reported();
        }
        else if ("FINIS".equals(mgr.readValue("STATEVAL")))
        {
            repair="FALSE";
            unissue="FALSE";
            finished();
        }
        else if ("BBBB".equals(mgr.readValue("BUTTONVAL")))
        {
            repair="FALSE";
            unissue="FALSE";
            finished1();
        }
        // Bug 72214, start
        else if ("ISLASTWO".equals(mgr.readValue("BUTTONVAL")))
        {
           sIsLastWo="FALSE";
           finished0();
        }
        // Bug 72214, end
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("FAULT_REP_FLAG")))
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("OKSPAREPART")))
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
                okFindITEM5();
                okFindITEM6();
            }

            matSingleMode = true;
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("OKMANISSUE")) && !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();
            this.activateMaterial();

            if (headset.countRows() == 1)
            {
                okFindITEM3();
            }

            if (headset.countRows() != 1)
            {
                String s_sel_wo = ctx.getGlobal("WONOGLOBAL");
                int sel_wo = Integer.parseInt(s_sel_wo);
                headset.goTo(sel_wo);  

                okFindITEM2();
                okFindITEM3();
                okFindITEM5();
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
                String s_sel_wo = ctx.getGlobal("WONOGLOBAL");
                int sel_wo = Integer.parseInt(s_sel_wo);
                headset.goTo(sel_wo);   

                okFindITEM2();
                okFindITEM3();
                okFindITEM5();
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
                int curr_head = Integer.parseInt(ctx.getGlobal("HEADCURR"));
                headset.goTo(curr_head);   
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
                okFindITEM5();
                okFindITEM6();
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
                okFindITEM5();
                okFindITEM6();
            }

            if (itemset3.countRows() > 0)
            {
                noteTextEntered = mgr.readValue("NOTETEXTENT","");
                buff = itemset3.getRow();
                buff.setValue("SNOTETEXT",noteTextEntered);
                itemset3.setRow(buff);
            }
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("RWARRTYPEENT")))
        {
            okFind();

            headlay.setLayoutMode(headlay.EDIT_LAYOUT);

            rWarrDesc = mgr.URLDecode(mgr.readValue("RWARRDESC",""));
            rWarrType = mgr.readValue("RWARRTYPEENT","");
            rRowN = mgr.readValue("RROWNENT","");
            rRawId = mgr.readValue("RWARRIDENT","");  

            temp = headset.getRow();

            trans.clear(); 

            cmd  = trans.addCustomFunction("GETSUPPLIER","OBJECT_SUPPLIER_WARRANTY_API.Get_Vendor_No","SUPPLIER");
            cmd.addParameter("CONTRACT",headset.getRow().getValue("MCH_CODE_CONTRACT"));
            cmd.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));
            cmd.addParameter("OBJ_SUP_WARRANTY",rRowN);

            cmd  = trans.addCustomFunction("SUPPDESC","Maintenance_Supplier_API.Get_Description","SUPP_DESCR");
            cmd.addReference("SUPPLIER","GETSUPPLIER/DATA");

            trans = mgr.validate(trans);

            temp = headset.getRow();

            temp.setValue("OBJ_SUP_WARRANTY",rRowN);
            temp.setValue("SUP_WARRANTY",rRawId);
            temp.setValue("SUP_WARR_TYPE",rWarrType);
            temp.setValue("SUP_WARR_DESC",rWarrDesc);
            temp.setValue("SUPPLIER",trans.getValue("SUPPLIER/DATA/SUPPLIER"));
            temp.setValue("SUPP_DESCR",trans.getValue("SUPPDESC/DATA/SUPP_DESCR"));

            headset.setRow(temp);
            supWar = true;
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
        {
            showMat = mgr.readValue("SHOWMAT","");

            if (mgr.isEmpty(showMat))
                headlay.setLayoutMode(headlay.SINGLE_LAYOUT);

            okFind();   
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("CUSTOMER_NO")))
        {
            okFind();   
        }
        else if (mgr.dataTransfered())
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
        {
            showMat = mgr.readValue("SHOWMAT","");
            okFind();
        }
        else if ("TRUE".equals(mgr.readValue("PRPOSTCHANGED")))
            updatePostings();
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

        ctx.writeValue("QRYSTR",qrystr);
        ctx.writeFlag("SUPWAR",supWar);
        ctx.writeValue("RWARRTYPE",rWarrType);
        ctx.writeValue("RROWN",rRowN);
        ctx.writeValue("RRAWID",rRawId);
        ctx.writeValue("TRANSFERRED",transferred);
        ctx.writeValue("OBJESTATE",objState);
        ctx.writeNumber("HEADROWNO",headRowNo);
        ctx.writeNumber("CURRENTROW",currentRow);
        ctx.writeFlag("MATSINGLEMODE",matSingleMode);

        ctx.writeFlag("ACTENA1",actEna1);
        ctx.writeFlag("ACTENA2",actEna2);
        ctx.writeFlag("ACTENA3",actEna3);
        ctx.writeFlag("ACTENA4",actEna4);
        ctx.writeFlag("ACTENA5",actEna5);
        ctx.writeFlag("ACTENA6",actEna6);
        ctx.writeFlag("ACTENA7",actEna7);
        ctx.writeFlag("ACTENA8",actEna8);
        ctx.writeFlag("ACTENA9",actEna9);
        ctx.writeFlag("ACTENA10",actEna10);
        ctx.writeFlag("ACTENA11",actEna11);
        ctx.writeFlag("ACTENA12",actEna12);
        ctx.writeFlag("ACTENA13",actEna13);
        ctx.writeFlag("ACTENA14",actEna14);
        ctx.writeFlag("ACTENA15",actEna15);
        ctx.writeFlag("ACTENA16",actEna16);
        ctx.writeFlag("ACTENA17",actEna17);
        ctx.writeFlag("ACTENA18",actEna18);
        ctx.writeFlag("ACTENA19",actEna19);
        ctx.writeFlag("ACTENA20",actEna20);
        ctx.writeFlag("ACTENA21",actEna21);
        ctx.writeFlag("ACTENA22",actEna22);
        ctx.writeFlag("ACTENA23",actEna23);
        ctx.writeFlag("ACTENA24",actEna24);
        ctx.writeFlag("ACTENA25",actEna25);
        ctx.writeFlag("ACTENA26",actEna26);
        ctx.writeFlag("AGAIN",again);

        // 031204  ARWILK  Begin  (Replace blocks with tabs)
        tabs.saveActiveTab();
        // 031204  ARWILK  End  (Replace blocks with tabs)
    }

    //-----------------------------------------------------------------------------
    //-------------------------   UTILITY FUNCTION  ------------------------------
    //-----------------------------------------------------------------------------
    // This function is used to check security status for methods which
    // are in modules Docman, Order, Purch and Invent .This function is called before
    // the required method is performed.  -PAPELK 

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

    public String convertToString(int max)
    {
        if (max ==0)
            //return("'" + ret_data_buffer.getBufferAt(max).getValueAt(0) + "'");
	    return(" ? ");   
        else
            //return("'" + ret_data_buffer.getBufferAt(max).getValueAt(0) + "'," + convertToString(max-1));
	    return(" ? , " + convertToString(max-1));   
    }
    //-----------------------------------------------------------------------------
    //-----------------------------  VALIDATE FUNCTION  ---------------------------
    //-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();

        val = mgr.readValue("VALIDATE");

        isSecure = new String[15]; 
        String sOnhand = "ONHAND";
        //String sReserve = "RESERVED";
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


        //----------------------------------------------------//
        //------ Validate T/F data ---------------------------//
        //----------------------------------------------------//

        if ("TOOL_FACILITY_ID".equals(val))
        {
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
            String sToolCostAmt  = null;
            String sCatalogNo = mgr.readValue("ITEM5_CATALOG_NO");
            String sCatalogContract = mgr.readValue("ITEM5_CATALOG_NO_CONTRACT");
            String sCatalogDesc = mgr.readValue("ITEM5_CATALOG_NO_DESC");
            String sCatalogPrice = mgr.readValue("ITEM5_SALES_PRICE");
            String sCatalogPriceCurr = mgr.readValue("ITEM5_SALES_CURRENCY");
            String sCatalogDiscount  = mgr.readValue("ITEM5_DISCOUNT");
            String sCatalogDisPrice  = mgr.readValue("ITEM5_DISCOUNTED_PRICE");
            String sPlannedPriceAmt  = mgr.readValue("ITEM5_PLANNED_PRICE");

            String reqstr = null;
            int startpos = 0;
            int endpos = 0;
            int i = 0;
            String ar[] = new String[3];

            String new_tf_id = mgr.readValue("TOOL_FACILITY_ID","");

            if (new_tf_id.indexOf("^",0)>0)
            {
                for (i=0 ; i<3; i++)
                {
                    endpos = new_tf_id.indexOf("^",startpos);
                    reqstr = new_tf_id.substring(startpos,endpos);
                    ar[i] = reqstr;
                    startpos= endpos+1;
                }
                sToolId = ar[0];
                sToolCont = ar[1];
                sToolOrg = ar[2];
            }
            else
            {
                sToolId = new_tf_id;
                trans.clear();
                // Bug 75641 start
		cmd = trans.addCustomFunction("GETORG","Connect_Tools_Facilities_API.Get_Active_Maint_Org","ITEM5_ORG_CODE");
	        // Bug 75641 end
                cmd.addParameter("TOOL_FACILITY_ID",sToolId);
                cmd.addParameter("ITEM5_CONTRACT",sToolCont);
                trans = mgr.validate(trans);

                sToolOrg = trans.getValue("GETORG/DATA/ORG_CODE");
            }

            trans.clear();

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

            cmd = trans.addCustomFunction("TFCOST","Work_Order_Tool_Facility_API.Get_Cost","ITEM5_COST");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addReference("TOOL_FACILITY_TYPE","TFTYPE/DATA");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("TFCOSTCURR","Work_Order_Tool_Facility_API.Get_Cost_Currency","ITEM5_COST_CURRENCY");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM5_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addReference("TOOL_FACILITY_TYPE","TFTYPE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFNOTE","Connect_Tools_Facilities_API.Get_Note","ITEM5_NOTE");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addParameter("ITEM5_CONTRACT",sToolCont);
            cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);

            cmd = trans.addCustomFunction("TFSALESPART","Connect_Tools_Facilities_API.Get_Catalog_No","ITEM5_CATALOG_NO");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addParameter("ITEM5_CONTRACT",sToolCont);
            cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);

            cmd = trans.addCustomFunction("WOSITE","Active_Separate_API.Get_Contract","ITEM5_WO_CONTRACT");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("SPSITE","Work_Order_Tool_Facility_API.Sales_Part_Exist_On_Wosite","ITEM5_SP_SITE");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","ITEM5_CATALOG_NO_DESC");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPPRICE","Work_Order_Tool_Facility_API.Calculate_Sales_Price","ITEM5_SALES_PRICE");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("TFSPCURR","Work_Order_Tool_Facility_API.Get_Sales_Currency","ITEM5_SALES_CURRENCY");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");

            cmd = trans.addCustomFunction("TFSPDISCOUNT","Work_Order_Tool_Facility_API.Get_Discount","ITEM5_DISCOUNT");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM5_DISCOUNTED_PRICE");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM5_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM5_DISCOUNT","TFSPDISCOUNT/DATA");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM5_PLANNED_PRICE");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
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

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM5_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM5_COST_AMOUNT").formatNumber(GetToolCostAmt);

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

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM5_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM5_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

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
                  (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" + (mgr.isEmpty(sNote) ? "" : (sNote))+ "^" +
                  (mgr.isEmpty(sToolId) ? "" : (sToolId))+ "^" + (mgr.isEmpty(sToolCont) ? "" : (sToolCont))+ "^" +
                  (mgr.isEmpty(sToolOrg) ? "" : (sToolOrg))+ "^" ;

            mgr.responseWrite(txt);
        }
        else if ("TOOL_FACILITY_TYPE".equals(val))
        {
            String sQty      = mgr.readValue("ITEM5_QTY");
            String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
            String sToolCostAmt = null;

            cmd = trans.addCustomFunction("TFTYPEDESC","Tool_Facility_Type_API.Get_Type_Description","TYPE_DESCRIPTION");
            cmd.addParameter("TOOL_FACILITY_TYPE");

            cmd = trans.addCustomFunction("TFCOST","Work_Order_Tool_Facility_API.Get_Cost","ITEM5_COST");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("TFCOSTCURR","Work_Order_Tool_Facility_API.Get_Cost_Currency","ITEM5_COST_CURRENCY");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM5_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

            trans = mgr.validate(trans);

            String sTypeDesc = trans.getValue("TFTYPEDESC/DATA/TYPE_DESCRIPTION");

            double ToolCost = trans.getNumberValue("TFCOST/DATA/COST");
            String costStr = mgr.getASPField("ITEM5_COST").formatNumber(ToolCost);

            String sToolCostCurr = trans.getValue("TFCOSTCURR/DATA/COST_CURRENCY");

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM5_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM5_COST_AMOUNT").formatNumber(GetToolCostAmt);

            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
                sToolCostAmt = costAmtStr;

            txt = (mgr.isEmpty(sTypeDesc) ? "" : (sTypeDesc))+ "^" + (mgr.isEmpty(costStr) ? "" : (costStr))+ "^" + 
                  (mgr.isEmpty(sToolCostCurr) ? "" : (sToolCostCurr))+ "^" + (mgr.isEmpty(sToolCostAmt) ? "" : (sToolCostAmt))+ "^";

            mgr.responseWrite(txt);                         
        }
        else if ("PLAN_HRS".equals(val))
        {
            String opExists = mgr.readValue("OP_EXIST");
	    //Bug Id 70921, Start
	    String sOrgCode = mgr.readValue("ORG_CODE");
	    String sContract = mgr.readValue("CONTRACT");
	    String sPlanSDate = mgr.readValue("PLAN_S_DATE");
	    String sPlanHrs = mgr.readValue("PLAN_HRS"); 
	    //Bug Id 70921, End
            String manHours,allocHours;
	    String sCompletionDate = null;

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
                manHours = mgr.readValue("PLAN_HRS");
                allocHours = mgr.readValue("PLAN_HRS");
            }

	    // Calculate the planned completion date
	    //Bug Id 70921, Start Calculate the planned completion date according to maint org calendar
	    trans.clear();
	    if ( !mgr.isEmpty(mgr.readValue("PLAN_HRS")) && !mgr.isEmpty(mgr.readValue("PLAN_S_DATE")) ){
		if (checksec("Active_Work_Order_API.Calc_Completion_Date", 1,isSecure)) {
		    cmd = trans.addCustomFunction("CALCCOMPDATE", "Active_Work_Order_API.Calc_Completion_Date","PLAN_F_DATE");
		    cmd.addParameter("ORG_CODE", sOrgCode);
		    cmd.addParameter("CONTRACT", sContract);
		    cmd.addParameter("PLAN_S_DATE", sPlanSDate);
		    cmd.addParameter("PLAN_HRS", sPlanHrs);

		    trans = mgr.perform(trans);

		    sCompletionDate = trans.getBuffer("CALCCOMPDATE/DATA").getFieldValue("PLAN_F_DATE");

		}
	    }
            else 
		sCompletionDate = mgr.readValue("PLAN_F_DATE"); 
	    //Bug Id 70921, End

            txt = (mgr.isEmpty(manHours) ? "" : (manHours))+ "^" +
                  (mgr.isEmpty(allocHours) ? "" : (allocHours))+ "^" +
		  (mgr.isEmpty(sCompletionDate) ? "" : (sCompletionDate))+ "^" ;
            mgr.responseWrite(txt); 
        }

        else if ("ITEM5_CONTRACT".equals(val))
        {
            String sToolId   = mgr.readValue("TOOL_FACILITY_ID");
            String sToolCont = mgr.readValue("ITEM5_CONTRACT");
            String sToolOrg  = mgr.readValue("ITEM5_ORG_CODE");
            String sQty      = mgr.readValue("ITEM5_QTY");
            String sNote     = mgr.readValue("ITEM5_NOTE");
            String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
            String sToolCostAmt = null;
            String sCatalogNo = mgr.readValue("ITEM5_CATALOG_NO");
            String sCatalogContract = mgr.readValue("ITEM5_CATALOG_NO_CONTRACT");
            String sCatalogDesc = mgr.readValue("ITEM5_CATALOG_NO_DESC");
            String sCatalogPrice = mgr.readValue("ITEM5_SALES_PRICE");
            String sCatalogPriceCurr = mgr.readValue("ITEM5_SALES_CURRENCY");
            String sCatalogDiscount  = mgr.readValue("ITEM5_DISCOUNT");
            String sCatalogDisPrice  = mgr.readValue("ITEM5_DISCOUNTED_PRICE");
            String sPlannedPriceAmt  = mgr.readValue("ITEM5_PLANNED_PRICE");
            String sWn = mgr.readValue("ITEM5_WO_NO");

            String reqstr = null;
            int startpos = 0;
            int endpos = 0;
            int i = 0;
            String ar[] = new String[3];

            String new_tf_id = mgr.readValue("ITEM5_CONTRACT","");

            if (new_tf_id.indexOf("^",0)>0)
            {
                for (i=0 ; i<3; i++)
                {
                    endpos = new_tf_id.indexOf("^",startpos);
                    reqstr = new_tf_id.substring(startpos,endpos);
                    ar[i] = reqstr;
                    startpos= endpos+1;
                }
                sToolId = ar[0];
                sToolCont = ar[1];
                sToolOrg = ar[2];
            }
            else
            {
                sToolId = new_tf_id;
                sToolCont ="";
                sToolOrg ="";
            }

            cmd = trans.addCustomFunction("TFNOTE","Connect_Tools_Facilities_API.Get_Note","ITEM5_NOTE");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addParameter("ITEM5_CONTRACT",sToolCont);
            cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);

            cmd = trans.addCustomFunction("TFQTY","Connect_Tools_Facilities_API.Get_Qty","ITEM5_QTY");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addParameter("ITEM5_CONTRACT",sToolCont);
            cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);  

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM5_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSALESPART","Connect_Tools_Facilities_API.Get_Catalog_No","ITEM5_CATALOG_NO");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addParameter("ITEM5_CONTRACT",sToolCont);
            cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);

            cmd = trans.addCustomFunction("WOSITE","Active_Separate_API.Get_Contract","ITEM5_WO_CONTRACT");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("SPSITE","Work_Order_Tool_Facility_API.Sales_Part_Exist_On_Wosite","ITEM5_SP_SITE");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","ITEM5_CATALOG_NO_DESC");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPPRICE","Work_Order_Tool_Facility_API.Calculate_Sales_Price","ITEM5_SALES_PRICE");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("TFSPCURR","Work_Order_Tool_Facility_API.Get_Sales_Currency","ITEM5_SALES_CURRENCY");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");

            cmd = trans.addCustomFunction("TFSPDISCOUNT","Work_Order_Tool_Facility_API.Get_Discount","ITEM5_DISCOUNT");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM5_DISCOUNTED_PRICE");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM5_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM5_DISCOUNT","TFSPDISCOUNT/DATA");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM5_PLANNED_PRICE");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
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

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM5_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM5_COST_AMOUNT").formatNumber(GetToolCostAmt);

            String sCatalogNum = trans.getValue("TFSALESPART/DATA/CATALOG_NO");

            String sWoSite = trans.getValue("WOSITE/DATA/ITEM5_WO_CONTRACT");

            String sSpSite = trans.getValue("SPSITE/DATA/ITEM5_SP_SITE");

            String sGetCatalogDesc = trans.getValue("TFSPDESC/DATA/ITEM5_CATALOG_NO_DESC");

            double GetCatalogPrice = trans.getNumberValue("TFSPPRICE/DATA/SALES_PRICE");
            String catPriceStr = mgr.getASPField("ITEM5_SALES_PRICE").formatNumber(GetCatalogPrice);

            String sGetCatalogPriceCurr = trans.getValue("TFSPCURR/DATA/PRICE_CURRENCY");

            double GetCatalogDiscount = trans.getNumberValue("TFSPDISCOUNT/DATA/DISCOUNT");
            String catDiscount = mgr.getASPField("ITEM5_DISCOUNT").formatNumber(GetCatalogDiscount);

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM5_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM5_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

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
                  (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" + (mgr.isEmpty(sToolId) ? "" : (sToolId))+ "^" + 
                  (mgr.isEmpty(sToolCont) ? "" : (sToolCont))+ "^" + (mgr.isEmpty(sToolOrg) ? "" : (sToolOrg))+ "^" ;

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
            String sToolCostAmt = null;
            String sCatalogNo = mgr.readValue("ITEM5_CATALOG_NO");
            String sCatalogContract = mgr.readValue("ITEM5_CATALOG_NO_CONTRACT");
            String sCatalogDesc = mgr.readValue("ITEM5_CATALOG_NO_DESC");
            String sCatalogPrice = mgr.readValue("ITEM5_SALES_PRICE");
            String sCatalogPriceCurr = mgr.readValue("ITEM5_SALES_CURRENCY");
            String sCatalogDiscount  = mgr.readValue("ITEM5_DISCOUNT");
            String sCatalogDisPrice  = mgr.readValue("ITEM5_DISCOUNTED_PRICE");
            String sPlannedPriceAmt  = mgr.readValue("ITEM5_PLANNED_PRICE");

            String reqstr = null;
            int startpos = 0;
            int endpos = 0;
            int i = 0;
            String ar[] = new String[3];

            String new_tf_id = mgr.readValue("ITEM5_ORG_CODE","");

            if (new_tf_id.indexOf("^",0)>0)
            {
                for (i=0 ; i<3; i++)
                {
                    endpos = new_tf_id.indexOf("^",startpos);
                    reqstr = new_tf_id.substring(startpos,endpos);
                    ar[i] = reqstr;
                    startpos= endpos+1;
                }
                sToolId = ar[0];
                sToolCont = ar[1];
                sToolOrg = ar[2];
            }
            else
            {
                sToolId = new_tf_id;
                sToolCont ="";
                sToolOrg ="";
            }

            cmd = trans.addCustomFunction("TFNOTE","Connect_Tools_Facilities_API.Get_Note","ITEM5_NOTE");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addParameter("ITEM5_CONTRACT",sToolCont);
            cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);

            cmd = trans.addCustomFunction("TFQTY","Connect_Tools_Facilities_API.Get_Qty","ITEM5_QTY");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addParameter("ITEM5_CONTRACT",sToolCont);
            cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);  

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM5_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSALESPART","Connect_Tools_Facilities_API.Get_Catalog_No","ITEM5_CATALOG_NO");
            cmd.addParameter("TOOL_FACILITY_ID",sToolId);
            cmd.addParameter("ITEM5_CONTRACT",sToolCont);
            cmd.addParameter("ITEM5_ORG_CODE",sToolOrg);

            cmd = trans.addCustomFunction("WOSITE","Active_Separate_API.Get_Contract","ITEM5_WO_CONTRACT");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("SPSITE","Work_Order_Tool_Facility_API.Sales_Part_Exist_On_Wosite","ITEM5_SP_SITE");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","ITEM5_CATALOG_NO_DESC");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");

            cmd = trans.addCustomFunction("TFSPPRICE","Work_Order_Tool_Facility_API.Calculate_Sales_Price","ITEM5_SALES_PRICE");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("TFSPCURR","Work_Order_Tool_Facility_API.Get_Sales_Currency","ITEM5_SALES_CURRENCY");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");

            cmd = trans.addCustomFunction("TFSPDISCOUNT","Work_Order_Tool_Facility_API.Get_Discount","ITEM5_DISCOUNT");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM5_DISCOUNTED_PRICE");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM5_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM5_DISCOUNT","TFSPDISCOUNT/DATA");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM5_PLANNED_PRICE");
            cmd.addReference("ITEM5_CATALOG_NO","TFSALESPART/DATA");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
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

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM5_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM5_COST_AMOUNT").formatNumber(GetToolCostAmt);

            String sCatalogNum = trans.getValue("TFSALESPART/DATA/CATALOG_NO");

            String sWoSite = trans.getValue("WOSITE/DATA/ITEM5_WO_CONTRACT");

            String sSpSite = trans.getValue("SPSITE/DATA/ITEM5_SP_SITE");

            String sGetCatalogDesc = trans.getValue("TFSPDESC/DATA/ITEM5_CATALOG_NO_DESC");

            double GetCatalogPrice = trans.getNumberValue("TFSPPRICE/DATA/SALES_PRICE");
            String catPriceStr = mgr.getASPField("ITEM5_SALES_PRICE").formatNumber(GetCatalogPrice);

            String sGetCatalogPriceCurr = trans.getValue("TFSPCURR/DATA/PRICE_CURRENCY");

            double GetCatalogDiscount = trans.getNumberValue("TFSPDISCOUNT/DATA/DISCOUNT");
            String catDiscount = mgr.getASPField("ITEM5_DISCOUNT").formatNumber(GetCatalogDiscount);

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM5_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM5_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

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
                  (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" + (mgr.isEmpty(sToolId) ? "" : (sToolId))+ "^" + 
                  (mgr.isEmpty(sToolCont) ? "" : (sToolCont))+ "^" + (mgr.isEmpty(sToolOrg) ? "" : (sToolOrg))+ "^" ;

            mgr.responseWrite(txt);
        }
        else if ("ITEM5_QTY".equals(val))
        {
            String sQty      = mgr.readValue("ITEM5_QTY");
            String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
            String sToolCostAmt = null;
            String sPlannedPriceAmt  = null;

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM5_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM5_PLANNED_PRICE");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addParameter("ITEM5_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM5_SALES_PRICE");
            cmd.addParameter("ITEM5_DISCOUNT");

            trans = mgr.validate(trans);

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM5_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM5_COST_AMOUNT").formatNumber(GetToolCostAmt);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            {
                sPlannedPriceAmt = plannedPriceAmt;
                sToolCostAmt = costAmtStr;
            }

            txt = (mgr.isEmpty(sToolCostAmt) ? "" : (sToolCostAmt))+ "^" + (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^"; 

            mgr.responseWrite(txt);
        }
        else if ("ITEM5_PLANNED_HOUR".equals(val))
        {
            String sQty      = mgr.readValue("ITEM5_QTY");
            String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
            String sToolCostAmt = null;
            String sPlannedPriceAmt  = null;

            cmd = trans.addCustomFunction("TFCOSTAMT","Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount","ITEM5_COST_AMOUNT");
            cmd.addParameter("TOOL_FACILITY_ID");
            cmd.addParameter("TOOL_FACILITY_TYPE");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM5_PLANNED_PRICE");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addParameter("ITEM5_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM5_SALES_PRICE");
            cmd.addParameter("ITEM5_DISCOUNT");

            trans = mgr.validate(trans);

            double GetToolCostAmt = trans.getNumberValue("TFCOSTAMT/DATA/ITEM5_COST_AMOUNT");
            String costAmtStr = mgr.getASPField("ITEM5_COST_AMOUNT").formatNumber(GetToolCostAmt);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
            {
                sPlannedPriceAmt = plannedPriceAmt;
                sToolCostAmt = costAmtStr;
            }

            txt = (mgr.isEmpty(sToolCostAmt) ? "" : (sToolCostAmt))+ "^" + (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^"; 

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
            String sCatalogDisPrice  = null;
            String sPlannedPriceAmt  = null;

            cmd = trans.addCustomFunction("WOSITE","Active_Separate_API.Get_Contract","ITEM5_WO_CONTRACT");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("SPSITE","Work_Order_Tool_Facility_API.Sales_Part_Exist_On_Wosite","ITEM5_SP_SITE");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_CATALOG_NO");

            cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","ITEM5_CATALOG_NO_DESC");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_CATALOG_NO");

            cmd = trans.addCustomFunction("TFSPPRICE","Work_Order_Tool_Facility_API.Calculate_Sales_Price","ITEM5_SALES_PRICE");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");

            cmd = trans.addCustomFunction("TFSPCURR","Work_Order_Tool_Facility_API.Get_Sales_Currency","ITEM5_SALES_CURRENCY");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");

            cmd = trans.addCustomFunction("TFSPDISCOUNT","Work_Order_Tool_Facility_API.Get_Discount","ITEM5_DISCOUNT");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM5_DISCOUNTED_PRICE");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addReference("ITEM5_SALES_PRICE","TFSPPRICE/DATA");
            cmd.addReference("ITEM5_DISCOUNT","TFSPDISCOUNT/DATA");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM5_PLANNED_PRICE");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addReference("ITEM5_WO_CONTRACT","WOSITE/DATA");
            cmd.addParameter("ITEM5_WO_NO");
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

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM5_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM5_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);


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
        else if ("ITEM5_SALES_PRICE".equals(val))
        {
            String sQty      = mgr.readValue("ITEM5_QTY");
            String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
            String sCatalogDisPrice  = null;
            String sPlannedPriceAmt  = null;

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM5_DISCOUNTED_PRICE");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addParameter("ITEM5_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM5_SALES_PRICE");
            cmd.addParameter("ITEM5_DISCOUNT");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM5_PLANNED_PRICE");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addParameter("ITEM5_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM5_SALES_PRICE");
            cmd.addParameter("ITEM5_DISCOUNT");

            trans = mgr.validate(trans);

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM5_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM5_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
                sPlannedPriceAmt = plannedPriceAmt;

            sCatalogDisPrice = catDiscountedPrice;

            txt = (mgr.isEmpty(sCatalogDisPrice) ? "" : (sCatalogDisPrice))+ "^" + (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" ;
            mgr.responseWrite(txt);
        }
        else if ("ITEM5_DISCOUNT".equals(val))
        {
            String sQty      = mgr.readValue("ITEM5_QTY");
            String sPlannedHour = mgr.readValue("ITEM5_PLANNED_HOUR");
            String sCatalogDisPrice  = null;
            String sPlannedPriceAmt  = null;

            cmd = trans.addCustomFunction("TFSPDISTEDPRICE","Work_Order_Tool_Facility_API.Calculate_Discounted_Price","ITEM5_DISCOUNTED_PRICE");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addParameter("ITEM5_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM5_SALES_PRICE");
            cmd.addParameter("ITEM5_DISCOUNT");

            cmd = trans.addCustomFunction("TFPLANPRAMT","Work_Order_Tool_Facility_API.Calculated_Price_Amount","ITEM5_PLANNED_PRICE");
            cmd.addParameter("ITEM5_CATALOG_NO");
            cmd.addParameter("ITEM5_CATALOG_NO_CONTRACT");
            cmd.addParameter("ITEM5_WO_NO");
            cmd.addParameter("ITEM5_QTY",sQty);
            cmd.addParameter("ITEM5_PLANNED_HOUR",sPlannedHour);
            cmd.addParameter("ITEM5_SALES_PRICE");
            cmd.addParameter("ITEM5_DISCOUNT");

            trans = mgr.validate(trans);

            double GetCatalogDisPrice = trans.getNumberValue("TFSPDISTEDPRICE/DATA/ITEM5_DISCOUNTED_PRICE");
            String catDiscountedPrice = mgr.getASPField("ITEM5_DISCOUNTED_PRICE").formatNumber(GetCatalogDisPrice);

            double GetPlannedPriceAmt = trans.getNumberValue("TFPLANPRAMT/DATA/ITEM5_PLANNED_PRICE");
            String plannedPriceAmt = mgr.getASPField("ITEM5_PLANNED_PRICE").formatNumber(GetPlannedPriceAmt);

            if (!mgr.isEmpty(sPlannedHour) || !mgr.isEmpty(sQty))
                sPlannedPriceAmt = plannedPriceAmt;

            sCatalogDisPrice = catDiscountedPrice;

            txt = (mgr.isEmpty(sCatalogDisPrice) ? "" : (sCatalogDisPrice))+ "^" + (mgr.isEmpty(sPlannedPriceAmt) ? "" : (sPlannedPriceAmt))+ "^" ;
            mgr.responseWrite(txt); 
        }
        //----------------------------------------------------//
        //------ End of T/F Validate -------------------------//
        //----------------------------------------------------//
        else if ("ITEM2_CATALOG_CONTRACT".equals(val))
        {
            cmd = trans.addCustomFunction("VALCOMP","Site_API.Get_Company","COMPANY_VAR");
            cmd.addParameter("ITEM1_CONTRACT");

            trans = mgr.validate(trans);

            String strCompany = trans.getValue("VALCOMP/DATA/COMPANY_VAR");
            txt = (mgr.isEmpty(strCompany)?"":strCompany)+ "^";
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

        else if ("ITEM2_PLAN_HRS".equals(val) || "PLAN_MEN".equals(val))
        {
            cmd = trans.addCustomFunction("ORGCO","Organization_API.Get_Org_Cost","ORGCOST");
            cmd.addParameter("ITEM2_CONTRACT");
            cmd.addParameter("ITEM2_ORG_CODE");

            cmd = trans.addCustomFunction("ROLCO","Role_API.Get_Role_Cost","ROLECOST");
            cmd.addParameter("ROLE_CODE");

            trans = mgr.validate(trans);

            double orgco = trans.getNumberValue("ORGCO/DATA/ORGCOST");
            if (isNaN(orgco))
                orgco = 0;

            double rolco = trans.getNumberValue("ROLCO/DATA/ROLECOST");
            if (isNaN(rolco))
                rolco = 0;

            double cos;
            double nListPrice;

            if (rolco == 0)
                nListPrice = orgco;
            else
                nListPrice = rolco;

            double PlanHrs = mgr.readNumberValue("ITEM2_PLAN_HRS");
            if (isNaN(PlanHrs))
                PlanHrs = 0;

            double PlanMen = mgr.readNumberValue("PLAN_MEN");
            if (isNaN(PlanMen))
                PlanMen = 0;

            double totAllocHrs = mgr.readNumberValue("TOTAL_ALLOCATED_HOURS");
            if (isNaN(totAllocHrs))
                totAllocHrs = 0;

            double manHours =  PlanHrs * PlanMen;
            double remainHrs = manHours - totAllocHrs;

            double AmountCost;

            if (PlanHrs == 0)
                AmountCost = 0;
            else
            {
                if (PlanMen == 0)
                    AmountCost = nListPrice * PlanHrs;
                else
                    AmountCost = nListPrice * PlanHrs * PlanMen;
            }

            String AmountCostStr = mgr.getASPField("COSTAMOUNT").formatNumber(AmountCost);
            String sCost = mgr.getASPField("COST").formatNumber(nListPrice);
            String manHoursStr = mgr.getASPField("TOTAL_MAN_HOURS").formatNumber(manHours);
            String remainHrsStr = mgr.getASPField("TOTAL_REMAINING_HOURS").formatNumber(remainHrs);
            txt = (mgr.isEmpty(sCost)?"":sCost) + "^" + (mgr.isEmpty(AmountCostStr)?"":AmountCostStr) +"^" + 
                  (mgr.isEmpty(manHoursStr)?"":manHoursStr) + "^" + (mgr.isEmpty(remainHrsStr)?"":remainHrsStr) + "^" ;

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
            String mchCodeContract = mgr.readValue("MCH_CODE_CONTRACT");
            String mchCode = mgr.readValue("MCH_CODE");
            if (mgr.readValue("MCH_CODE").indexOf("~") > -1)
            {
                String strAttr = mgr.readValue("MCH_CODE");

                mchCode = strAttr.substring(0,strAttr.indexOf("~"));       
                String validationAttrAtr = strAttr.substring(strAttr.indexOf("~")+1,strAttr.length());
                mchCodeContract =  validationAttrAtr.substring(validationAttrAtr.indexOf("~")+1,validationAttrAtr.length());                

                cmd = trans.addCustomFunction("OBJECTESC","Maintenance_Object_API.Get_Mch_Name","MCH_CODE_DESCRIPTION");
                cmd.addParameter("MCH_CODE_CONTRACT",mchCodeContract);
                cmd.addParameter("MCH_CODE",mchCode);
            }
            else
            {
                if (mgr.isEmpty(mchCodeContract))
                {
                    cmd = trans.addCustomFunction("MCHCONTR","EQUIPMENT_OBJECT_API.Get_Def_Contract","MCH_CODE_CONTRACT");
                    cmd.addParameter("MCH_CODE");
                }

                cmd = trans.addCustomFunction("OBJECTESC","Maintenance_Object_API.Get_Mch_Name","MCH_CODE_DESCRIPTION");
                if (mgr.isEmpty(mchCodeContract))
                    cmd.addReference("MCH_CODE_CONTRACT","MCHCONTR/DATA");
                else
                    cmd.addParameter("MCH_CODE_CONTRACT",mchCodeContract);
                cmd.addParameter("MCH_CODE");
            }

            cmd = trans.addCustomFunction("GETCRIICAL","EQUIPMENT_FUNCTIONAL_API.Get_Criticality","CRITICALITY");

            if (mgr.isEmpty(mchCodeContract))
                cmd.addReference("MCH_CODE_CONTRACT","MCHCONTR/DATA");
            else
                cmd.addParameter("MCH_CODE_CONTRACT",mchCodeContract);
            cmd.addParameter("MCH_CODE",mchCode);

            trans = mgr.validate(trans);

            if (mgr.readValue("MCH_CODE").indexOf("~") == -1 && mgr.isEmpty(mchCodeContract))
                mchCodeContract = trans.getValue("MCHCONTR/DATA/MCH_CODE_CONTRACT");

            description = trans.getValue("OBJECTESC/DATA/MCH_CODE_DESCRIPTION");
            String criticality = trans.getValue("GETCRIICAL/DATA/CRITICALITY");

            txt = (mgr.isEmpty(description) ? "" : (description))+ "^" + (mgr.isEmpty(criticality) ? "" : (criticality))+ "^" + (mgr.isEmpty(mchCodeContract) ? "" : (mchCodeContract))+ "^" + (mgr.isEmpty(mchCode) ? "" : (mchCode))+ "^";
            mgr.responseWrite(txt);
        }
        else if ("ORG_CODE".equals(val))
        {
            cmd = trans.addCustomFunction("GETORGDESC", "Organization_API.Get_Description", "ORGCODEDESCR");
            cmd.addParameter("CONTRACT");
            cmd.addParameter("ORG_CODE");

            trans = mgr.validate(trans);

            String sOrgDesc = trans.getValue("GETORGDESC/DATA/ORGCODEDESCR");

            txt = (mgr.isEmpty(sOrgDesc) ? "" : (sOrgDesc))+ "^";  
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
            String                                  name                                  =                                  trans.getValue(                                 "REPNAME/DATA/NAME");

            txt = (mgr.isEmpty(reportById) ? "" : (reportById))+ "^" + (mgr.isEmpty(name) ? "" : (name))+ "^";
            mgr.responseWrite(txt);
        }
        else if ("WORK_LEADER_SIGN".equals(val))
        {
            cmd = trans.addCustomFunction("WOLDRSIGNID","Company_Emp_API.Get_Max_Employee_Id","WORK_LEADER_SIGN_ID");
            cmd.addParameter("COMPANY");
            cmd.addParameter("WORK_LEADER_SIGN");

            cmd = trans.addCustomFunction("WORKLEADNAME","PERSON_INFO_API.Get_Name","WORKLEADRNAME");
            cmd.addParameter("WORK_LEADER_SIGN");

            trans = mgr.validate(trans);

            String woSignId = trans.getValue("WOLDRSIGNID/DATA/WORK_LEADER_SIGN_ID");
            String workLeaderName = trans.getValue("WORKLEADNAME/DATA/WORKLEADRNAME");

            txt = (mgr.isEmpty(woSignId) ? "": (woSignId))+ "^" + (mgr.isEmpty(workLeaderName) ? "": (workLeaderName))+ "^";
            mgr.responseWrite(txt);
        }
        else if ("WORK_MASTER_SIGN".equals(val))
        {
            cmd = trans.addCustomFunction("WOMASSIGNID","Company_Emp_API.Get_Max_Employee_Id","WORK_MASTER_SIGN_ID");
            cmd.addParameter("COMPANY");
            cmd.addParameter("WORK_MASTER_SIGN");

            cmd = trans.addCustomFunction("WOMASSNAME","PERSON_INFO_API.Get_Name","WORKMASBYNAME");
            cmd.addParameter("WORK_MASTER_SIGN");

            trans = mgr.validate(trans);

            String woMasterId = trans.getValue("WOMASSIGNID/DATA/WORK_MASTER_SIGN_ID");
            String woMasterName = trans.getValue("WOMASSNAME/DATA/WORKMASBYNAME");

            txt = (mgr.isEmpty(woMasterId) ? "": (woMasterId))+ "^" + (mgr.isEmpty(woMasterName) ? "": (woMasterName))+ "^";
            mgr.responseWrite(txt);
        }
        else if ("PREPARED_BY".equals(val))
        {
            cmd = trans.addCustomFunction("PREPID","Company_Emp_API.Get_Max_Employee_Id","PREPARED_BY_ID");
            cmd.addParameter("COMPANY");
            cmd.addParameter("PREPARED_BY");

            cmd = trans.addCustomFunction("PREPBYNAME","PERSON_INFO_API.Get_Name","PREPAREDBYNAME");
            cmd.addParameter("PREPARED_BY");

            trans = mgr.validate(trans);

            String prepId = trans.getValue("PREPID/DATA/PREPARED_BY_ID");
            String prepName = trans.getValue("PREPBYNAME/DATA/PREPAREDBYNAME");

            txt = (mgr.isEmpty(prepId) ? "": (prepId))+ "^" + (mgr.isEmpty(prepName) ? "": (prepName))+ "^";
            mgr.responseWrite(txt);
        }
        else if ("AUTHORIZE_CODE".equals(val))
        {
            cmd = trans.addCustomFunction("AUTHONAME","PERSON_INFO_API.Get_Name","AUTHOCODENAME");
            cmd.addParameter("AUTHORIZE_CODE");

            trans = mgr.validate(trans);

            String authoName = trans.getValue("AUTHONAME/DATA/AUTHOCODENAME");

            txt = (mgr.isEmpty(authoName) ? "": (authoName))+ "^";
            mgr.responseWrite(txt);
        }
        else if ("ITEM2_CONTRACT".equals(val))
        {
            cmd = trans.addCustomFunction("COMP","Site_API.Get_Company","TEMP");
            cmd.addParameter("ITEM2_CONTRACT");

            trans = mgr.validate(trans);

            String compani = trans.getValue("COMP/DATA/TEMP");

            txt = (mgr.isEmpty(compani) ? "": (compani))+ "^";
            mgr.responseWrite(txt);
        }
        else if ("ITEM2_ORG_CODE".equals(val))
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
                    sOrgCode = ar[3];
                    sOrgContract = ar[4];
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
                sOrgCode = mgr.readValue("ITEM2_ORG_CODE");
                sOrgContract = mgr.readValue("ITEM2_CONTRACT"); 
            }

            if (mgr.isEmpty(sOrgCode) && mgr.isEmpty(mgr.readValue("ROLE_CODE")))
            {
                cmd = trans.addCustomFunction("GETORG","Active_Work_Order_API.Get_Org_Code","ITEM2_ORG_CODE");
                cmd.addParameter("ITEM2_WO_NO",mgr.readValue("ITEM2_WO_NO"));

                cmd = trans.addCustomFunction("GETORGSITE","Active_Work_Order_API.Get_Contract","ITEM2_CONTRACT");
                cmd.addParameter("ITEM2_WO_NO",mgr.readValue("ITEM2_WO_NO"));

                cmd = trans.addCustomFunction("GETORGCOST","Organization_API.Get_Org_Cost","ORGCOST");
                cmd.addReference("ITEM2_CONTRACT","GETORGSITE/DATA");
                cmd.addReference("ITEM2_ORG_CODE","GETORG/DATA");
            }
            else
            {
                cmd = trans.addCustomFunction("ORGCO","Organization_API.Get_Org_Cost","ORGCOST");
                cmd.addParameter("ITEM2_CONTRACT",sOrgContract);
                cmd.addParameter("ITEM2_ORG_CODE",sOrgCode);

                cmd = trans.addCustomFunction("ROLCO","Role_API.Get_Role_Cost","ROLECOST");
                cmd.addParameter("ROLE_CODE");
            }
	     //bug id 59224,start
                
                cmd = trans.addCustomFunction("GETITEM2ORGDESC","Organization_Api.Get_Description","ITEM2_ORG_CODE_DESC");
                cmd.addParameter("ITEM2_CONTRACT",sOrgContract);
                cmd.addParameter("ITEM2_ORG_CODE",sOrgCode);
            
                trans = mgr.validate(trans);

                String sitem2OrgDesc = trans.getValue("GETITEM2ORGDESC/DATA/ITEM2_ORG_CODE_DESC");
             //bug id 59224,end

            if (mgr.isEmpty(sOrgCode) && mgr.isEmpty(mgr.readValue("ROLE_CODE")))
            {
                cos = trans.getNumberValue("GETORGCOST/DATA/ORGCOST");
                if (isNaN(cos))
                    cos = 0;
            }
            else
            {
                orgco = trans.getNumberValue("ORGCO/DATA/ORGCOST");
                if (isNaN(orgco))
                    orgco = 0;

                rolco = trans.getNumberValue("ROLCO/DATA/ROLECOST");
                if (isNaN(rolco))
                    rolco = 0;

                if (rolco == 0)
                    cos = orgco;
                else
                    cos   = rolco;
            }

            String colAmountCostStr = countCostVal(cos);

            String cosStr = mgr.getASPField("COST").formatNumber(cos);
            txt = (mgr.isEmpty(cosStr)?"":cosStr)+"^"+(mgr.isEmpty(colAmountCostStr)?"":colAmountCostStr)+"^"+
                  (mgr.isEmpty(sOrgCode)?"":sOrgCode)+"^"+(mgr.isEmpty(sOrgContract)?"":sOrgContract)+"^"+(mgr.isEmpty(sitem2OrgDesc)?"":sitem2OrgDesc)+"^";
            mgr.responseWrite(txt);
        }
        else if ("ROLE_CODE".equals(val))
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
                sOrgContract = mgr.readValue("ITEM2_CONTRACT"); 
            }

            if (mgr.isEmpty(sRoleCode) && mgr.isEmpty(mgr.readValue("ITEM2_ORG_CODE")))
            {
                cmd = trans.addCustomFunction("GETORG","Active_Work_Order_API.Get_Org_Code","ITEM2_ORG_CODE");
                cmd.addParameter("ITEM2_WO_NO",mgr.readValue("ITEM2_WO_NO"));

                cmd = trans.addCustomFunction("GETORGSITE","Active_Work_Order_API.Get_Contract","ITEM2_CONTRACT");
                cmd.addParameter("ITEM2_WO_NO",mgr.readValue("ITEM2_WO_NO"));

                cmd = trans.addCustomFunction("GETORGCOST","Organization_API.Get_Org_Cost","ORGCOST");
                cmd.addReference("ITEM2_CONTRACT","GETORGSITE/DATA");
                cmd.addReference("ITEM2_ORG_CODE","GETORG/DATA");
            }
            else
            {
                cmd = trans.addCustomFunction("ORGCO","Organization_API.Get_Org_Cost","ORGCOST");
                cmd.addParameter("ITEM2_CONTRACT",sOrgContract);
                cmd.addParameter("ITEM2_ORG_CODE",mgr.readValue("ITEM2_ORG_CODE"));

                cmd = trans.addCustomFunction("ROLCO","Role_API.Get_Role_Cost","ROLECOST");
                cmd.addParameter("ROLE_CODE",sRoleCode);
            }
            //bug id 59224,start
            cmd = trans.addCustomFunction("ROLEDESC","ROLE_API.Get_Description","ROLE_CODE_DESC");
            cmd.addParameter("ROLE_CODE",sRoleCode);

            trans = mgr.validate(trans);

            String sroleDesc = trans.getValue("ROLEDESC/DATA/ROLE_CODE_DESC");

            //bug id 59224,end

            if (mgr.isEmpty(sRoleCode) && mgr.isEmpty(mgr.readValue("ITEM2_ORG_CODE")))
            {
                cos = trans.getNumberValue("GETORGCOST/DATA/ORGCOST");
                if (isNaN(cos))
                    cos = 0;
            }
            else
            {
                orgco = trans.getNumberValue("ORGCO/DATA/ORGCOST");
                if (isNaN(orgco))
                    orgco = 0;

                rolco = trans.getNumberValue("ROLCO/DATA/ROLECOST");
                if (isNaN(rolco))
                    rolco = 0;

                if (rolco == 0)
                    cos = orgco;
                else
                    cos   = rolco;
            }

            String colAmountCostStr = countCostVal(cos); 
            String cosStr = mgr.getASPField("COST").formatNumber(cos);

            txt = (mgr.isEmpty(cosStr)?"":cosStr)+"^"+(mgr.isEmpty(colAmountCostStr)?"":colAmountCostStr)+"^"+
                  (mgr.isEmpty(sRoleCode)?"":sRoleCode)+"^"+(mgr.isEmpty(sOrgContract)?"":sOrgContract)+"^"+(mgr.isEmpty(sroleDesc)?"":sroleDesc)+"^";
            mgr.responseWrite(txt);   
        }
        else if ("SIGN_ID".equals(val))
        {
            String signId = mgr.readValue("SIGN_ID");
            String sDefRole = ""; 

            cmd = trans.addCustomFunction("EMPEXIST","Employee_API.Is_Emp_Exist","ITEM2_TEMP");
            cmd.addParameter("ITEM2_COMPANY");
            cmd.addParameter("SIGN_ID");  

            trans = mgr.validate(trans);

            boolean bEmpExist = !("0".equals(trans.getValue("EMPEXIST/DATA/ITEM2_TEMP")));

            if (bEmpExist)
            {
                trans.clear();

                cmd = trans.addCustomFunction("GETSIGN","Company_Emp_API.Get_Person_Id","SIGN");
                cmd.addParameter("ITEM2_COMPANY");
                cmd.addParameter("SIGN_ID");  

                cmd = trans.addCustomFunction("GETDEFROLE","Employee_Role_API.Get_Default_Role","ROLE_CODE");
                cmd.addParameter("ITEM2_COMPANY");
                cmd.addParameter("SIGN_ID");  

                cmd = trans.addCustomFunction("GETORGCODE","Employee_API.Get_Organization","ITEM2_ORG_CODE");
                cmd.addParameter("ITEM2_COMPANY");
                cmd.addParameter("SIGN_ID");  

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
        else if ("SIGN".equals(val))
        {
            String reqstr = null;
            int startpos = 0;
            int endpos = 0;
            int i = 0;
            double planHrs=0;
            String planHours;
            String ar[] = new String[2];
            String emp_id = "";
            String sign = "";
            String depart = "";
            double totManHrs=0;
            double remainHrs=0;
            String remainHours;
            String sOrgContract = mgr.readValue("ITEM2_CONTRACT","");
            String sOrgCode = mgr.readValue("ITEM2_ORG_CODE","");
            String roleCode = mgr.readValue("ROLE_CODE");
            String new_sign = mgr.readValue("SIGN","");

            String allocatedHours;
            double allocatedHrs = mgr.readNumberValue("ITEM2_PLAN_HRS");
            if (isNaN(allocatedHrs))
                allocatedHrs = 0;
            allocatedHours = mgr.getASPField("ITEM7_TOTAL_ALLOCATED_HOURS").formatNumber(allocatedHrs);

            totManHrs = mgr.readNumberValue("TOTAL_MAN_HOURS");
            if (isNaN(totManHrs))
                totManHrs = 0;
            remainHrs = totManHrs - allocatedHrs ;
            remainHours = mgr.getASPField("ITEM7_TOTAL_ALLOCATED_HOURS").formatNumber(remainHrs);

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


            if (mgr.isEmpty(roleCode))
            {
                cmd = trans.addCustomFunction("DEFROLE","Employee_Role_API.Get_Default_Role","ROLE_CODE");
                cmd.addParameter("ITEM2_COMPANY");
                cmd.addParameter("SIGN_ID",emp_id);
            }

            if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
            {
                cmd = trans.addCustomFunction("EMPORGCONT","Employee_API.Get_Org_Contract","ORG_CONTRACT");
                cmd.addParameter("ITEM2_COMPANY");
                cmd.addParameter("SIGN_ID",emp_id);
            }

            if (mgr.isEmpty(sOrgCode))
            {
                cmd = trans.addCustomFunction("EMPORGCOD","Employee_API.Get_Organization","ITEM2_ORG_CODE");
                cmd.addParameter("ITEM2_COMPANY");
                cmd.addParameter("SIGN_ID",emp_id);
            }

            trans = mgr.validate(trans);

            if (mgr.isEmpty(roleCode))
                roleCode = trans.getValue("DEFROLE/DATA/ROLE_CODE");

            if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
                sOrgContract = trans.getValue("EMPORGCONT/DATA/ORG_CONTRACT");

            if (mgr.isEmpty(sOrgCode))
            {
                depart = trans.getValue("EMPORGCOD/DATA/ORG_CODE");
                sOrgCode = depart;
            }

            double nCost = mgr.readNumberValue("COST");
            if (isNaN(nCost))
                nCost = 0;

            String colOrgCode = depart;
            String colRoleCode = roleCode;

            nCost = getCostVal(nCost,sOrgContract,colOrgCode,colRoleCode,sOrgContract,sOrgCode);

            String nCostStr = mgr.getASPField("COST").formatNumber(nCost);
            String colAmountCostStr = countCostVal(nCost);
            trans.clear();
            //bug id 59224,start
                
                cmd = trans.addCustomFunction("GETITEM2VAlORGDESC","Organization_Api.Get_Description","ITEM2_ORG_CODE_DESC");
                cmd.addParameter("ITEM2_CONTRACT",sOrgContract);
                cmd.addParameter("ITEM2_ORG_CODE",sOrgCode);
            
                cmd = trans.addCustomFunction("GETITEM2VAlROLECODE","ROLE_API.Get_Description","ROLE_CODE_DESC");
                cmd.addParameter("ROLE_CODE",roleCode);


            trans = mgr.validate(trans);

            String sitem2OrgDesc = trans.getValue("GETITEM2VAlORGDESC/DATA/ITEM2_ORG_CODE_DESC");
            String sroleDesc = trans.getValue("GETITEM2VAlROLECODE/DATA/ROLE_CODE_DESC");

            //bug id 59224,end

            txt = (mgr.isEmpty(emp_id) ? "": (emp_id))+ "^" + 
                  (mgr.isEmpty(roleCode) ? "": (roleCode))+ "^"+ 
                  (mgr.isEmpty(sOrgCode) ? "": (sOrgCode))+ "^"+ 
                  (mgr.isEmpty(nCostStr) ? "": (nCostStr)) + "^"+
                  (mgr.isEmpty(colAmountCostStr) ? "": (colAmountCostStr))+ "^"+
                  (mgr.isEmpty(sign)? "": (sign))+ "^"+
                  (mgr.isEmpty(sOrgContract)? "": (sOrgContract))+ "^" +
                  (mgr.isEmpty(allocatedHours)? "": (allocatedHours))+ "^" +  //allocated hours
                  (mgr.isEmpty(allocatedHours)? "": (allocatedHours))+ "^" +  //total allocated hours
                  (mgr.isEmpty(remainHours)? "": (remainHours))+ "^"+
                  (mgr.isEmpty(sitem2OrgDesc)? "": (sitem2OrgDesc))+ "^"+
                  (mgr.isEmpty(sroleDesc)? "": (sroleDesc))+ "^";
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


        else if ("CUSTOMER_NO".equals(val))
        {
            trans.clear();

            trans = mgr.validate(trans);
            String sUpdatePrice = "0";
            txt = (mgr.isEmpty(sUpdatePrice) ? "": sUpdatePrice)+ "^";
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
        //-------------------------------------------------------------------------------
        //------------ This validation part modified. Refer bug id #70349 ---------------
        //-------------------------------------------------------------------------------
        else if ("PLAN_S_DATE".equals(val))
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

	    boolean isErr = false;
            if (!mgr.isEmpty(mgr.readValue("PLAN_F_DATE")))
            {
                trans.clear();
                ASPQuery q = trans.addQuery("GETDATE","SELECT 'TRUE' DUMMY_RESULT FROM DUAL WHERE ? > ?");
                q.addParameter("PLAN_F_DATE",mgr.readValue("PLAN_F_DATE"));
                q.addParameter("PLAN_S_DATE",mgr.readValue("PLAN_S_DATE"));

                trans = mgr.perform(trans);
                String result = trans.getValue("GETDATE/DATA/DUMMY_RESULT");

                if (!"TRUE".equals(result)) {
                    txt = "No_Data_Found" + mgr.translate("PCMWPLANEDDATE: Planned Completion is earlier than Planned Start.");
                    isErr = true;
                }
            }

            if (!isErr )
                txt = (mgr.isEmpty(planFdate) ? "": planFdate) ;

            mgr.responseWrite(txt);

        }
        else if ("PLAN_F_DATE".equals(val))
        {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("PLAN_F_DATE",mgr.readValue("PLAN_F_DATE",""));
            boolean isErr = false;
            if (!mgr.isEmpty(mgr.readValue("PLAN_S_DATE"))) {
                trans.clear();
                ASPQuery q = trans.addQuery("GETDATE","SELECT 'TRUE' DUMMY_RESULT FROM DUAL WHERE ? > ?");
                q.addParameter("PLAN_F_DATE",mgr.readValue("PLAN_F_DATE"));
                q.addParameter("PLAN_S_DATE",mgr.readValue("PLAN_S_DATE"));

                trans = mgr.perform(trans);
                String result = trans.getValue("GETDATE/DATA/DUMMY_RESULT");

                if (!"TRUE".equals(result)) {
                    txt = "No_Data_Found" + mgr.translate("PCMWPLANEDDATE: Planned Completion is earlier than Planned Start.");
                    isErr = true;
                }
            }

            if (!isErr )
                txt = mgr.readValue("PLAN_F_DATE") +"^"+ mgr.readValue("PLAN_S_DATE","") +"^";
            
            mgr.responseWrite(txt);     
        }
        else if ("REQUIRED_START_DATE".equals(val))
        {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("REQUIRED_START_DATE",mgr.readValue("REQUIRED_START_DATE",""));

            txt = mgr.readValue("REQUIRED_START_DATE") +"^";
            mgr.responseWrite(txt);    
        }
        else if ("REQUIRED_END_DATE".equals(val))
        {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("REQUIRED_END_DATE",mgr.readValue("REQUIRED_END_DATE",""));

            txt = mgr.readValue("REQUIRED_END_DATE","") +"^";
            mgr.responseWrite(txt);    
        }
        else if ("REG_DATE".equals(val))
        {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("REG_DATE",mgr.readValue("REG_DATE",""));

            txt = mgr.readValue("REG_DATE","") +"^";
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
            String intDestDesc = "";    

            if (checksec("Internal_Destination_API.Get_Description",1,isSecure))
            {
                cmd = trans.addCustomFunction("INTDESTDESC","Internal_Destination_API.Get_Description","INT_DESTINATION_DESC");
                cmd.addParameter("CONTRACT",mgr.readValue("ITEM3_CONTRACT",""));
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
            String cataNo = "";
            String cataDesc = "";
            String partDesc = "";
            String dimQty = "";
            String typeDesi = "";
            String qtyOnHand1 = "";
            String qtyAvail1 = "";
            String unitMeas = "";
            String salesPriceGroupId = "";
            String suppCode = "";
            String defCond = new String();
            String condesc = new String();
            String sDefCondiCode= "";
            double qtyOnHand = 0;
            double qtyAvail = 0;
            String activeInd = "";
            String vendorNo = "";
            String custOwner = "";
            String partOwnership = "";
            String sOwner = mgr.readValue("OWNER");
            String ownership = "";

            cmd = trans.addCustomFunction("MAINTREQSUP","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","SUPPLY_CODE");

            cmd = trans.addCustomFunction("GETCONDCODEUSAGEDB","PART_CATALOG_API.Get_Condition_Code_Usage_Db","COND_CODE_USAGE");
            cmd.addParameter("PART_NO");

            cmd = trans.addCustomFunction("DEFCONDCODE","CONDITION_CODE_API.Get_Default_Condition_Code","CONDITION_CODE");

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
                cmd = trans.addCustomFunction("CATANO","Sales_Part_API.Get_Catalog_No_For_Part_No","CATALOG_NO");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("PART_NO");
            }

            if (checksec("Sales_Part_API.Get_Catalog_Desc",2,isSecure))
            {
                cmd = trans.addCustomFunction("CATADESC","Sales_Part_API.Get_Catalog_Desc","CATALOGDESC");
                cmd.addParameter("CATALOG_CONTRACT");
                cmd.addReference("CATALOG_NO","CATANO/DATA");
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

                    //Qty Reserved
                    cmd = trans.addCustomFunction("INVAVL","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
                    cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("CONFIGURATION");
                    cmd.addParameter("QTY_TYPE", sAvailable);
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

            if ( checksec("Sales_Part_API.Get_Activeind",10,isSecure) )
            {
               cmd = trans.addCustomFunction("GETACT","Sales_Part_API.Get_Activeind","ACTIVEIND");
               cmd.addParameter("CATALOG_CONTRACT");
               cmd.addReference("CATALOG_NO","CATANO/DATA");
            }

            if ( checksec("Active_Sales_Part_API.Encode",11,isSecure) )
            {
               cmd = trans.addCustomFunction("GETACTDB","Active_Sales_Part_API.Encode","ACTIVEIND_DB");
               cmd.addReference("ACTIVEIND","GETACT/DATA");
            }

            trans = mgr.validate(trans);

            if ("true".equals(isSecure[1]))
                cataNo = trans.getValue("CATANO/DATA/CATALOG_NO");
            else
                cataNo = "";
            if ("true".equals(isSecure[2]))
                cataDesc = trans.getValue("CATADESC/DATA/CATALOGDESC");
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
                qtyAvail  = trans.getNumberValue("INVAVL/DATA/QTY_AVAILABLE");
            }
            else
            {
                qtyOnHand = 0;
                qtyAvail = 0;
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

            if ( "true".equals(isSecure[11]) )
               activeInd = trans.getValue("GETACTDB/DATA/ACTIVEIND_DB");
            else
               activeInd = "";

            qtyOnHand1 = mgr.getASPField("QTYONHAND").formatNumber(qtyOnHand); 
            qtyAvail1 = mgr.getASPField("QTY_AVAILABLE").formatNumber(qtyAvail);

            String hasStruct = trans.getValue("SPARESTRUCT/DATA/HASSPARESTRUCTURE");
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
                  (mgr.isEmpty(qtyOnHand1) ? "": qtyOnHand1) + "^" + 
                  (mgr.isEmpty(unitMeas) ? "": unitMeas) + "^" + 
                  (mgr.isEmpty(partDesc) ? "": partDesc) + "^" + 
                  (mgr.isEmpty(salesPriceGroupId) ? "" : salesPriceGroupId) + "^"+
                  (mgr.isEmpty(defCond) ? "": (defCond)) + "^"+
                  (mgr.isEmpty(condesc) ? "": (condesc)) + "^"+
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
            double  qtyOnHand = 0;
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

                    //Qty Reserved
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

                    //Qty Reserved
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

                    //Qty Reserved
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

            trans = mgr.validate(trans);
            if ("true".equals(isSecure[1]))
            {
                qtyOnHand = trans.getNumberValue("QTYONHAND");
                qtyOnHand1 = trans.getBuffer("INVONHAND/DATA").getFieldValue("QTYONHAND");
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

            String nPriceListNo = mgr.readValue("PRICE_LIST_NO","");  

            double nDiscountedPriceAmt = mgr.readNumberValue("ITEMDISCOUNTEDPRICE");
            if (isNaN(nDiscountedPriceAmt))
                nDiscountedPriceAmt = 0;

            double nSalesPriceAmount = 0;
            String nSalesPriceAmountStr; 
            String nCostStr;
            String nListPriceStr;
            String nDiscountStr;
            String nDiscountedPriceAmtStr;

            String serialNo = mgr.readValue("SERIAL_NO");
            trans.clear();
            cmd = trans.addCustomFunction("GETOBJSUPL","MAINT_MATERIAL_REQ_LINE_API.Is_Obj_Supp_Loaned","OBJ_LOAN");
            cmd.addParameter("ITEM_WO_NO");
        
            trans = mgr.validate(trans);
        
            String sObjSup = trans.getValue("GETOBJSUPL/DATA/OBJ_LOAN");
        
            trans.clear();

            if (!mgr.isEmpty(spareId))
            {
                /*if (checksec("Inventory_Part_API.Get_Inventory_Value_By_Method",1,isSecure))
                {
                    cmd = trans.addCustomFunction("GETINVVAL","Inventory_Part_API.Get_Inventory_Value_By_Method","ITEM_COST");
                    cmd.addParameter("SPARE_CONTRACT");
                    cmd.addParameter("PART_NO");

                    trans = mgr.validate(trans);

                    nCostStr = trans.getBuffer("GETINVVAL/DATA").getFieldValue("ITEM_COST");
                } */
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

                    //nCost = trans.getNumberValue("GETINVVAL/DATA/ITEM_COST");
                    nCostStr = trans.getBuffer("GETINVVAL/DATA").getFieldValue("ITEM_COST");

                    //if (isNaN(nCost))
                    //    nCost=0;  
                }
                else
                    nCostStr    = "";
                if ("TRUE".equals(sObjSup) || "SUPPLIER LOANED".equals(mgr.readValue("PART_OWNERSHIP_DB")))
	        {
		    nCost = 0;
		    nCostStr = mgr.getASPField("ITEM_COST").formatNumber(nCost);
                }
            } 
            else
                nCostStr    = mgr.getASPField("ITEM_COST").formatNumber(nCost); 

            double planQty = mgr.readNumberValue("PLAN_QTY");
            if (isNaN(planQty))
                planQty = 0;

            String nAmountCostStr; 
            double nAmountCost = 0;

            if (planQty == 0)
                nAmountCostStr = mgr.getASPField("AMOUNTCOST").formatNumber(nAmountCost);
            else
            {
                if (!mgr.isEmpty(spareId))
                {
                    double nCostTemp = trans.getNumberValue("GETINVVAL/DATA/ITEM_COST");
                    if ("TRUE".equals(sObjSup) || "SUPPLIER LOANED".equals(mgr.readValue("PART_OWNERSHIP_DB"))) 
		    {
                        nCostTemp = 0;
                    }

                    if (isNaN(nCostTemp))
                        nCostTemp = 0;
                    nAmountCost = nCostTemp * planQty;
                }
                else
                {
                    double nCostField = mgr.readNumberValue("ITEM_COST");
                    if (isNaN(nCostField))
                        nCostField = 0;
                    nAmountCost = nCostField * planQty;  
                }  
                nAmountCostStr = mgr.getASPField("AMOUNTCOST").formatNumber(nAmountCost);
            }

            String cataNo = mgr.readValue("CATALOG_NO","");  

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
                cmd.addParameter("CATALOG_CONTRACT");
                cmd.addParameter("CATALOG_NO");
                cmd.addReference("CUSTOMER_NO","CUSTONO/DATA");
                cmd.addReference("AGREEMENT_ID","AGREID/DATA");
                cmd.addParameter("PRICE_LIST_NO",nPriceListNo);
                cmd.addParameter("PLAN_QTY");

                trans = mgr.validate(trans);

                if (mgr.isEmpty(nPriceListNo))
                    nPriceListNo = trans.getBuffer("PRICEINF/DATA").getFieldValue("PRICE_LIST_NO");

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
            nSalesPriceAmountStr = mgr.getASPField("SALESPRICEAMOUNT").formatNumber(nSalesPriceAmount);
            nDiscountedPriceAmtStr = mgr.getASPField("ITEMDISCOUNTEDPRICE").formatNumber(nDiscountedPriceAmt);
            nDiscountStr = mgr.getASPField("ITEM_DISCOUNT").formatNumber(nDiscount);

            txt = (mgr.isEmpty(nCostStr) ? "": nCostStr) + "^" + (mgr.isEmpty(nAmountCostStr) ? "": nAmountCostStr) + "^" + (mgr.isEmpty(nPriceListNo) ? "": nPriceListNo) + "^" + (mgr.isEmpty(nDiscountStr) ? "": nDiscountStr) + "^" + (mgr.isEmpty(nListPriceStr) ? "": nListPriceStr) + "^" + (mgr.isEmpty(nSalesPriceAmountStr) ? "": nSalesPriceAmountStr) + "^" + (mgr.isEmpty(nDiscountedPriceAmtStr) ? "": nDiscountedPriceAmtStr) + "^";

            mgr.responseWrite(txt); 
        }
        else if ("CATALOG_NO".equals(val))
        {
            String nPriceListNo = "";
            String cataDesc = "";
            String salesPriceGroupId = "";

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

            if (checksec("Customer_Order_Pricing_Api.Get_Valid_Price_List",1,isSecure))
            {
                cmd = trans.addCustomFunction("PRILST","Customer_Order_Pricing_Api.Get_Valid_Price_List","PRICE_LIST_NO");
                cmd.addParameter("CATALOG_CONTRACT");
                cmd.addParameter("CATALOG_NO");
                cmd.addReference("CUSTOMER_NO","CUSTONO/DATA");
                cmd.addReference("CURRENCEY_CODE","CURRCO/DATA");
            }

            cmd = trans.addCustomCommand("PRICEINF","Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("BASE_PRICE","0"); 
            cmd.addParameter("SALE_PRICE","0");
            cmd.addParameter("ITEM_DISCOUNT","0");
            cmd.addParameter("CURRENCY_RATE","0");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addParameter("CATALOG_NO");
            cmd.addReference("CUSTOMER_NO","CUSTONO/DATA");
            cmd.addReference("AGREEMENT_ID","AGREID/DATA");
            cmd.addReference("PRICE_LIST_NO","PRILST/DATA");
            cmd.addParameter("PLAN_QTY");

            if (checksec("Sales_Part_API.Get_Catalog_Desc",2,isSecure))
            {
                cmd = trans.addCustomFunction("CATADESC","Sales_Part_API.Get_Catalog_Desc","CATALOGDESC");
                cmd.addParameter("CATALOG_CONTRACT");
                cmd.addParameter("CATALOG_NO");
            }

            if (checksec("SALES_PART_API.GET_SALES_PRICE_GROUP_ID",3,isSecure))
            {
                cmd = trans.addCustomFunction("GETSALESPRICEGRPID","SALES_PART_API.GET_SALES_PRICE_GROUP_ID","SALES_PRICE_GROUP_ID");
                cmd.addParameter("CATALOG_CONTRACT");
                cmd.addParameter("CATALOG_NO");
            }

            String spareId = mgr.readValue("PART_NO","");

            double nCost = mgr.readNumberValue("ITEM_COST");
            if (isNaN(nCost))
                nCost = 0;

            /*if (!mgr.isEmpty(spareId))
            {
                if (checksec("Inventory_Part_API.Get_Inventory_Value_By_Method",4,isSecure))
                {
                    cmd = trans.addCustomFunction("GETINVVAL","Inventory_Part_API.Get_Inventory_Value_By_Method","ITEM_COST");
                    cmd.addParameter("SPARE_CONTRACT");
                    cmd.addParameter("PART_NO");
                }
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
                nPriceListNo =  trans.getValue("PRILST/DATA/PRICE_LIST_NO");
            else
                nPriceListNo = "";               

            if (mgr.isEmpty(nPriceListNo))
                nPriceListNo = trans.getValue("PRICEINF/DATA/PRICE_LIST_NO");

            double nDiscount = trans.getNumberValue("PRICEINF/DATA/DISCOUNT");
            if (isNaN(nDiscount))
                nDiscount = 0;

            String nCostStr;
            String nDiscountStr;
            String nSalesPriceAmountStr;
            String nCostAmtStr;
            double nListPrice = 0;
            double nSalesPriceAmount = 0;
            double nDiscountedPriceAmt = 0;

            if ("true".equals(isSecure[2]))
                cataDesc = trans.getValue("CATADESC/DATA/CATALOGDESC");
            else
                cataDesc    = "";
            if ("true".equals(isSecure[3]))
                salesPriceGroupId = trans.getValue("GETSALESPRICEGRPID/DATA/SALES_PRICE_GROUP_ID");
            else
                salesPriceGroupId   = "";               

            double planQty = mgr.readNumberValue("PLAN_QTY");
            if (isNaN(planQty))
                planQty = 0;

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

            if (!mgr.isEmpty(spareId))
            {
                if ("true".equals(isSecure[4]))
                    nCostStr = trans.getBuffer("GETINVVAL/DATA").getFieldValue("ITEM_COST");
                else
                    nCostStr    = "";
            }
            else
                nCostStr    = mgr.getASPField("ITEM_COST").formatNumber(nCost);
            if ("TRUE".equals(sObjSup) || "SUPPLIER LOANED".equals(mgr.readValue("PART_OWNERSHIP_DB")))
	    {
                 nCost = 0;
                 nCostStr = mgr.getASPField("ITEM_COST").formatNumber(nCost);
            }

            if (planQty == 0)
            {
                double nCostAmt = 0;
                nCostAmtStr = mgr.getASPField("AMOUNTCOST").formatNumber(nCostAmt);
            }
            else
            {
                double nCostTemp = mgr.readNumberValue("ITEM_COST");
                if (isNaN(nCostTemp))
                    nCostTemp = 0;

                if (!mgr.isEmpty(spareId))
                {
                    nCostTemp = trans.getNumberValue("GETINVVAL/DATA/ITEM_COST");
                    if (isNaN(nCostTemp))
                        nCostTemp = 0;
                }

                if ("TRUE".equals(sObjSup) || "SUPPLIER LOANED".equals(mgr.readValue("PART_OWNERSHIP_DB"))) 
                    nCostTemp = 0;

                double nCostAmt = nCostTemp * planQty; 

                nCostAmtStr = mgr.getASPField("AMOUNTCOST").formatNumber(nCostAmt);
            }  

            String nListPriceStr = mgr.formatNumber("LIST_PRICE",nListPrice);
            nDiscountStr = mgr.getASPField("ITEM_DISCOUNT").formatNumber(nDiscount);     
            nSalesPriceAmountStr = mgr.getASPField("SALESPRICEAMOUNT").formatNumber(nSalesPriceAmount);
            String nDiscountedPriceAmtStr = mgr.getASPField("ITEMDISCOUNTEDPRICE").formatNumber(nDiscountedPriceAmt);

            txt = (mgr.isEmpty(nListPriceStr) ? "": nListPriceStr) + "^" + (mgr.isEmpty(nCostStr) ? "": nCostStr) + "^" + (mgr.isEmpty(nCostAmtStr) ? "": nCostAmtStr) + "^" + (mgr.isEmpty(cataDesc) ? "": cataDesc) + "^" + (mgr.isEmpty(nDiscountStr) ? "": nDiscountStr) + "^"+ (mgr.isEmpty(nSalesPriceAmountStr) ? "": nSalesPriceAmountStr) + "^" + (mgr.isEmpty(salesPriceGroupId)?"":salesPriceGroupId) + "^" + (mgr.isEmpty(nDiscountedPriceAmtStr)?"":nDiscountedPriceAmtStr) + "^"+ (mgr.isEmpty(nPriceListNo)?"":nPriceListNo) + "^";

            mgr.responseWrite(txt);
        }
        else if ("PRICE_LIST_NO".equals(val))
        {
            String partNo = mgr.readValue("PART_NO","");
            String nPriceListNo = mgr.readValue("PRICE_LIST_NO","");

            double nCostAmt = 0;
            double nListPrice = 0;
            double nSalesPriceAmount = 0;
            String nCostStr = "";
            String nCostAmtStr = "" ; 
            double nDiscountedPriceAmt = 0;
            double nDiscount = 0;

            if (!mgr.isEmpty(partNo))
            {
                /*if (checksec("Inventory_Part_API.Get_Inventory_Value_By_Method",1,isSecure))
                {
                    cmd = trans.addCustomFunction("GETINVAL","Inventory_Part_API.Get_Inventory_Value_By_Method","ITEM_COST");
                    cmd.addParameter("SPARE_CONTRACT");
                    cmd.addParameter("PART_NO");

                    trans = mgr.validate(trans);

                    nCostStr = trans.getBuffer("GETINVAL/DATA").getFieldValue("ITEM_COST");
                } */
                if (checksec("Active_Separate_API.Get_Inventory_Value",1,isSecure))
                {
                    cmd = trans.addCustomCommand("GETINVAL","Active_Separate_API.Get_Inventory_Value");
                    cmd.addParameter("ITEM_COST");
                    cmd.addParameter("SPARE_CONTRACT");
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
                    cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
                    cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));
                    
                    trans = mgr.validate(trans);

                    //nCost = trans.getNumberValue("GETINVVAL/DATA/ITEM_COST");
                    nCostStr = trans.getBuffer("GETINVAL/DATA").getFieldValue("ITEM_COST");

                    //if (isNaN(nCost))
                    //    nCost=0;
                }
                else
                    nCostStr    = "";                    
            }

            double planQty = mgr.readNumberValue("PLAN_QTY");
            if (isNaN(planQty))
                planQty = 0;

            if (planQty == 0)
            {
                nCostAmtStr = mgr.getASPField("AMOUNTCOST").formatNumber(nCostAmt);
            }
            else
            {
                double nCostTemp = trans.getNumberValue("GETINVAL/DATA/ITEM_COST");
                if (isNaN(nCostTemp))
                    nCostTemp = 0;

                nCostAmt = nCostTemp * planQty;
                nCostAmtStr = mgr.getASPField("AMOUNTCOST").formatNumber(nCostAmt);
            } 

            String cataNo = mgr.readValue("CATALOG_NO",""); 

            if (!mgr.isEmpty(cataNo) &&   planQty != 0)
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
                cmd.addParameter("CATALOG_CONTRACT");
                cmd.addParameter("CATALOG_NO");
                cmd.addReference("CUSTOMER_NO","CUSTONO/DATA");
                cmd.addReference("AGREEMENT_ID","AGREID/DATA");
                cmd.addParameter("PRICE_LIST_NO");
                cmd.addParameter("PLAN_QTY");

                trans = mgr.validate(trans);        

                if (mgr.isEmpty(nPriceListNo))
                    nPriceListNo = trans.getValue("PRICEINF/DATA/PRICE_LIST_NO");

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

            String nListPriceStr = mgr.formatNumber("LIST_PRICE",nListPrice);
            String nDiscountStr = mgr.getASPField("ITEM_DISCOUNT").formatNumber(nDiscount);     
            String nSalesPriceAmountStr = mgr.getASPField("SALESPRICEAMOUNT").formatNumber(nSalesPriceAmount);
            String nDiscountedPriceAmtStr = mgr.getASPField("ITEMDISCOUNTEDPRICE").formatNumber(nDiscountedPriceAmt);

            txt =  (mgr.isEmpty(nCostStr) ? "": nCostStr) + "^" + (mgr.isEmpty(nCostAmtStr) ? "": nCostAmtStr) + "^" + (mgr.isEmpty(nListPriceStr) ? "": nListPriceStr) + "^"+ (mgr.isEmpty(nDiscountStr) ? "": nDiscountStr) + "^" + (mgr.isEmpty(nSalesPriceAmountStr) ? "": nSalesPriceAmountStr) + "^" + (mgr.isEmpty(nDiscountedPriceAmtStr) ? "" : nDiscountedPriceAmtStr) + "^";

            mgr.responseWrite(txt); 
        }
        else if ("ITEM_DISCOUNT".equals(val))
        {
            String salePriceAmtStr;
            String strDiscountedPriceAmt;

            double nListPrice = mgr.readNumberValue("LIST_PRICE");
            if (isNaN(nListPrice))
                nListPrice = 0;

            double planQty = mgr.readNumberValue("PLAN_QTY");
            if (isNaN(planQty))
                planQty = 0;

            double nDiscount = mgr.readNumberValue("ITEM_DISCOUNT");
            if (isNaN(nDiscount))
                nDiscount = 0;

            double discountedPrice;

            double salePriceAmt =  nListPrice * planQty;
            salePriceAmt =  salePriceAmt - (nDiscount / 100 * salePriceAmt);
            discountedPrice = nListPrice - (nDiscount/100*nListPrice); 

            salePriceAmtStr = mgr.getASPField("SALESPRICEAMOUNT").formatNumber(salePriceAmt);
            strDiscountedPriceAmt = mgr.getASPField("ITEMDISCOUNTEDPRICE").formatNumber(discountedPrice);

            txt = (mgr.isEmpty(salePriceAmtStr) ? "": salePriceAmtStr) + "^"+(mgr.isEmpty(strDiscountedPriceAmt) ? "": strDiscountedPriceAmt) + "^";

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

            suppl = trans.getValue("SUPP/DATA/SUP_WARRANTY");
            supplier = trans.getValue("SUPPLIER/DATA/SUPPLIER");
            suppdesc = trans.getValue("SUPPDESC/DATA/SUPP_DESCR");

            txt = (mgr.isEmpty(suppl) ? "": suppl) + "^" + (mgr.isEmpty(supplier) ? "": supplier) + "^" + (mgr.isEmpty(suppdesc) ? "": suppdesc) + "^";

            mgr.responseWrite(txt);   

        }
        else if ("CONDITION_CODE".equals(val))
        {
            String qtyOnHand1 = ""; 
            String qtyAvail1 = "";
            double qtyOnHand = 0;
            double qtyAvail = 0;

            String nCostStr;
            String nCostAmtStr;
            double nCostAmt = 0;

            sClientOwnershipDefault = mgr.readValue("PART_OWNERSHIP_DB");
            if (mgr.isEmpty(sClientOwnershipDefault))
                sClientOwnershipDefault = "COMPANY OWNED";

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
    
                nCostStr = trans.getBuffer("GETINVVAL/DATA").getFieldValue("ITEM_COST");
    
                //if (isNaN(nCost))
                //    nCost=0;
            } else
                nCostStr = "";

            double planQty = mgr.readNumberValue("PLAN_QTY");
            if (isNaN(planQty))
                planQty=0;
           
            if (planQty == 0)
            {
                nCostAmtStr = mgr.getASPField("AMOUNTCOST").formatNumber(nCostAmt);
            }
            else
            {
                double nCostTemp = trans.getNumberValue("GETINVVAL/DATA/ITEM_COST"); 
                if (isNaN(nCostTemp))
                    nCostTemp = 0;

                nCostAmt = nCostTemp * planQty;
                nCostAmtStr = mgr.getASPField("AMOUNTCOST").formatNumber(nCostAmt);
            }

            txt = (mgr.isEmpty(descri) ? "" : (descri))+ "^" +
                  (mgr.isEmpty(qtyOnHand1) ? "": qtyOnHand1) + "^" + 
                  (mgr.isEmpty(qtyAvail1) ? "": qtyAvail1) + "^"+
                  (mgr.isEmpty(nCostStr) ? "": nCostStr) + "^" +
                  (mgr.isEmpty(nCostAmtStr) ? "": nCostAmtStr) + "^"; 

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

            if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",1,isSecure))
            {
                cmd = trans.addCustomFunction("MAINTREQSUP","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","SUPPLY_CODE");
                trans = mgr.validate(trans);
                String sStandardInv = trans.getValue("MAINTREQSUP/DATA/SUPPLY_CODE");
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

                    //Qty Reserved
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
                    //Qty OnHand
                    //Bug 66456, Start, Added check on PROJ
                    if (mgr.isModuleInstalled("PROJ"))
                    {
                        cmd = trans.addCustomFunction("PROJID","Activity_API.Get_Project_Id","PROJECT_ID");
                        cmd.addParameter("ITEM3_ACTIVITY_SEQ",mgr.readValue("ITEM3_ACTIVITY_SEQ"));
                    }
                    //Bug 66456, End

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

                    //Qty Reserved
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
            String sOwnerName="";
            String sOwnershipDb = mgr.readValue("PART_OWNERSHIP_DB");

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
                    cmd.addParameter("ITEM3_ACTIVITY_SEQ","");
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
                    cmd.addParameter("ITEM3_ACTIVITY_SEQ","");
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

            if ("CUSTOMER OWNED".equals(sOwnershipDb))
                 sOwnerName = trans.getValue("GETOWNERNAME/DATA/OWNER_NAME");
            if ("SUPPLIER LOANED".equals(sOwnershipDb))
                 sOwnerName = trans.getValue("GETOWNERNAME1/DATA/OWNER_NAME");
            String sWoCust    = trans.getValue("GETOWCUST/DATA/WO_CUST");

            txt = (mgr.isEmpty(sOwnerName) ? "" : (sOwnerName)) + "^" + (mgr.isEmpty(sWoCust) ? "": (sWoCust)) + "^" + (mgr.isEmpty(qtyOnHand1) ? "": qtyOnHand1) + "^" +(mgr.isEmpty(qtyAvail1) ? "": qtyAvail1) + "^" ; 

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

            txt = (mgr.isEmpty(stdJobId)?"":stdJobId) + "^" + (mgr.isEmpty(stdJobRev)?"":stdJobRev) + "^" + (mgr.isEmpty(desc)?"":desc) + "^" + (mgr.isEmpty(status)?"":status) + "^";

            mgr.responseWrite(txt);
        }
        else if ("STD_JOB_REVISION".equals(val))
        {
            String desc = "";
            trans.clear();

            cmd = trans.addCustomFunction("GETDESC", "Standard_Job_API.Get_Definition", "DESCRIPTION");
            cmd.addParameter("STD_JOB_ID", mgr.readValue("STD_JOB_ID"));
            cmd.addParameter("STD_JOB_CONTRACT", mgr.readValue("STD_JOB_CONTRACT"));
            cmd.addParameter("STD_JOB_REVISION", mgr.readValue("STD_JOB_REVISION"));

            cmd = trans.addCustomFunction("GETWORKDESC", "Standard_Job_API.Get_Work_Description", "DESCRIPTION");
            cmd.addParameter("STD_JOB_ID", mgr.readValue("STD_JOB_ID"));
            cmd.addParameter("STD_JOB_CONTRACT", mgr.readValue("STD_JOB_CONTRACT"));
            cmd.addParameter("STD_JOB_REVISION", mgr.readValue("STD_JOB_REVISION"));

            cmd = trans.addCustomFunction("GETSTATUS","Standard_Job_API.Get_Status","STD_JOB_STATUS");
            cmd.addParameter("STD_JOB_ID", mgr.readValue("STD_JOB_ID"));
            cmd.addParameter("STD_JOB_CONTRACT", mgr.readValue("STD_JOB_CONTRACT"));
            cmd.addParameter("STD_JOB_REVISION", mgr.readValue("STD_JOB_REVISION"));

            trans = mgr.validate(trans);

            String definition = trans.getValue("GETDESC/DATA/DESCRIPTION");
            String workDesc = trans.getValue("GETWORKDESC/DATA/DESCRIPTION");
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
            String sOutExsist  = "";
	    String sOverlapExsist  = "";
            Date dDateFrom ;
            String sQty = "";
            ASPBuffer buff = mgr.newASPBuffer();
            buff.addFieldItem("ITEM6_DATE_FROM",mgr.readValue("ITEM6_DATE_FROM"));

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

                cmd  = trans.addCustomFunction("GETOUTEXIST","WORK_ORDER_ROLE_API.Out_Of_Lines_Exist","OUTEXIST");
                cmd.addParameter("ITEM6_WO_NO");
                cmd.addParameter("JOB_ID");
                cmd.addParameter("ITEM6_DATE_TO");

                cmd  = trans.addCustomFunction("GETOVEREXIST","WORK_ORDER_ROLE_API.Overlap_Bookings_Exist","OVERLAPEXIST");
                cmd.addParameter("ITEM6_WO_NO");
                cmd.addParameter("JOB_ID");
                cmd.addParameter("ITEM6_DATE_FROM");
                cmd.addParameter("ITEM6_DATE_TO");


                cmd  = trans.addCustomFunction("GETFROMDATE","Work_Order_Job_API.Get_Date_From","ITEM6_DATE_FROM");
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
                sOutExsist = trans.getValue("GETOUTEXIST/DATA/OUTEXIST");
                sOverlapExsist  = trans.getValue("GETOVEREXIST/DATA/OVERLAPEXIST");
                dDateFrom       = trans.getBuffer("GETFROMDATE/DATA").getFieldDateValue("ITEM6_DATE_FROM");

                if ("TRUE".equals(sOverlapExsist)  &&  !(buff.getFieldDateValue("ITEM6_DATE_FROM").equals(dDateFrom)))
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
                  (mgr.isEmpty(sAgreementId)?"":sAgreementId) + "^"+
                  (mgr.isEmpty(sOutExsist)?"":sOutExsist) + "^" +
                  (mgr.isEmpty(sOverlapExsist)?"":sOverlapExsist) + "^" ;

            mgr.responseWrite(txt);
        }
        else if ("ITEM6_SIGN_ID".equals(val))
        {
            String reqstr = null;
            int startpos = 0;
            int endpos = 0;
            int i = 0;
            String ar[] = new String[2];
            String emp_id = "";
            String sign = "";

            String new_sign = mgr.readValue("ITEM6_SIGN_ID","");

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
        else if ("ITEM2_JOB_ID".equals(val))
        {
            cmd = trans.addCustomFunction("GETEMP","Work_Order_Job_API.Get_Executed_By","SIGN_ID");
            cmd.addParameter("ITEM2_WO_NO");
            cmd.addParameter("ITEM2_JOB_ID");

            cmd = trans.addCustomFunction("GETPERSON","Company_Emp_API.Get_Person_Id","SIGN");
            cmd.addParameter("ITEM2_COMPANY");
            cmd.addReference("SIGN_ID","GETEMP/DATA");

            trans = mgr.validate(trans);

            String emp_id = trans.getValue("GETEMP/DATA/SIGN_ID");
            String sign = trans.getValue("GETPERSON/DATA/SIGN");

            txt = (mgr.isEmpty(emp_id)?"":emp_id) + "^"+(mgr.isEmpty(sign)?"":sign) + "^";
            mgr.responseWrite(txt);
        }
        //--------validations for block7-operations 

        else if ("ITEM7_TEAM_ID".equals(val))
        {
            cmd = trans.addCustomFunction("TDESC", "Maint_Team_API.Get_Description", "ITEM7_TEAMDESC" );    
            cmd.addParameter("ITEM7_TEAM_ID");
            cmd.addParameter("ITEM7_TEAM_CONTRACT");
            trans = mgr.validate(trans);   
            teamDesc  = trans.getValue("TDESC/DATA/ITEM7_TEAMDESC");

            txt =  (mgr.isEmpty(teamDesc) ? "" : (teamDesc)) + "^";
            mgr.responseWrite(txt);

        }
        else if ("ITEM7_DATE_FROM".equals(val))
        {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("ITEM7_DATE_FROM",mgr.readValue("ITEM7_DATE_FROM",""));      

            DateFormat df= DateFormat.getInstance();
            Date dt = null;
            try
            {
                dt = df.parse(mgr.readValue("ITEM7_DATE_FROM",""));
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            buf.addFieldDateItem("ITEM7_DATE_FROM_MASKED", dt);
            txt =buf.getFieldValue("ITEM7_DATE_FROM_MASKED") + "^";
            mgr.responseWrite(txt);
        }

        else if ("ITEM7_CATALOG_CONTRACT".equals(val))
        {
            cmd = trans.addCustomFunction("VALCOMP","Site_API.Get_Company","ITEM7_COMPANY_VAR");
            cmd.addParameter("ITEM1_CONTRACT");

            trans = mgr.validate(trans);

            String strCompany = trans.getValue("VALCOMP/DATA/ITEM7_COMPANY_VAR");
            txt = (mgr.isEmpty(strCompany)?"":strCompany)+ "^";
            mgr.responseWrite(txt);
        }
        else if ("ITEM7_PLAN_HRS".equals(val) || "ITEM7_PLAN_MEN".equals(val))
        {
            cmd = trans.addCustomFunction("ORGCO","Organization_API.Get_Org_Cost","ITEM7_ORGCOST");
            cmd.addParameter("ITEM7_CONTRACT");
            cmd.addParameter("ITEM7_ORG_CODE");

            cmd = trans.addCustomFunction("ROLCO","Role_API.Get_Role_Cost","ITEM7_ROLECOST");
            cmd.addParameter("ITEM7_ROLE_CODE");

            trans = mgr.validate(trans);

            double orgco = trans.getNumberValue("ORGCO/DATA/ITEM7_ORGCOST");
            if (isNaN(orgco))
                orgco = 0;

            double rolco = trans.getNumberValue("ROLCO/DATA/ITEM7_ROLECOST");
            if (isNaN(rolco))
                rolco = 0;

            double cos;
            double nListPrice;

            if (rolco == 0)
                nListPrice = orgco;
            else
                nListPrice = rolco;

            double PlanHrs = mgr.readNumberValue("ITEM7_PLAN_HRS");
            if (isNaN(PlanHrs))
                PlanHrs = 0;

            double PlanMen = mgr.readNumberValue("ITEM7_PLAN_MEN");
            if (isNaN(PlanMen))
                PlanMen = 0;

            double totAllocHrs = mgr.readNumberValue("ITEM7_TOTAL_ALLOCATED_HOURS");
            if (isNaN(totAllocHrs))
                totAllocHrs = 0;

            double manHours =  PlanHrs * PlanMen;
            double remainHrs = manHours - totAllocHrs;

            double AmountCost;

            if (PlanHrs == 0)
                AmountCost = 0;
            else
            {
                if (PlanMen == 0)
                    AmountCost = nListPrice * PlanHrs;
                else
                    AmountCost = nListPrice * PlanHrs * PlanMen;
            }

            String AmountCostStr = mgr.getASPField("ITEM7_COSTAMOUNT").formatNumber(AmountCost);
            String sCost = mgr.getASPField("ITEM7_COST").formatNumber(nListPrice);
            String manHoursStr = mgr.getASPField("ITEM7_TOTAL_MAN_HOURS").formatNumber(manHours);
            String remainHrsStr = mgr.getASPField("ITEM7_TOTAL_REMAINING_HOURS").formatNumber(remainHrs);

            txt = (mgr.isEmpty(sCost)?"":sCost) + "^" + (mgr.isEmpty(AmountCostStr)?"":AmountCostStr) + "^"+
                  (mgr.isEmpty(manHoursStr)?"":manHoursStr) + "^" + (mgr.isEmpty(remainHrsStr)?"":remainHrsStr) + "^" ;

            mgr.responseWrite(txt);
        }

        else if ("ITEM7_ALLOCATED_HOURS".equals(val))
        {
            double allocHours = mgr.readNumberValue ("ITEM7_ALLOCATED_HOURS");
            if (isNaN(allocHours)) allocHours = 0;
            double totManHrs = mgr.readNumberValue ("ITEM7_TOTAL_MAN_HOURS");
            if (isNaN(totManHrs)) totManHrs = 0;
            double remainHrs = totManHrs - allocHours;

            String allocatedHrs = mgr.getASPField("ITEM7_TOTAL_ALLOCATED_HOURS").formatNumber(allocHours);
            String remainingHrs = mgr.getASPField("ITEM7_TOTAL_REMAINING_HOURS").formatNumber(remainHrs);

            txt = (mgr.isEmpty(allocatedHrs) ? "": (allocatedHrs)) + "^" +  //total allocated hrs
                  (mgr.isEmpty(remainingHrs) ? "": (remainingHrs)) + "^";

            mgr.responseWrite(txt);    

        }

        else if ("ITEM7_CONTRACT".equals(val))
        {
            cmd = trans.addCustomFunction("COMP","Site_API.Get_Company","ITEM7_TEMP");
            cmd.addParameter("ITEM7_CONTRACT");

            trans = mgr.validate(trans);

            String compani = trans.getValue("COMP/DATA/ITEM7_TEMP");

            txt = (mgr.isEmpty(compani) ? "": (compani))+ "^";
            mgr.responseWrite(txt);
        }
        else if ("ITEM7_ORG_CODE".equals(val))
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
            String new_org_code = mgr.readValue("ITEM7_ORG_CODE","");

            if (new_org_code.indexOf("^",0)>0)
            {
                if (!mgr.isEmpty(mgr.readValue("ITEM7_ROLE_CODE")))
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
                    if (!mgr.isEmpty(mgr.readValue("ITEM7_SIGN")))
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
                sOrgCode = mgr.readValue("ITEM7_ORG_CODE");
                sOrgContract = mgr.readValue("ITEM7_CONTRACT"); 
            }

            if (mgr.isEmpty(sOrgCode) && mgr.isEmpty(mgr.readValue("ITEM7_ROLE_CODE")))
            {
                cmd = trans.addCustomFunction("GETORG","Active_Work_Order_API.Get_Org_Code","ITEM7_ORG_CODE");
                cmd.addParameter("ITEM7_WO_NO",mgr.readValue("ITEM7_WO_NO"));

                cmd = trans.addCustomFunction("GETORGSITE","Active_Work_Order_API.Get_Contract","ITEM7_CONTRACT");
                cmd.addParameter("ITEM7_WO_NO",mgr.readValue("ITEM7_WO_NO"));

                cmd = trans.addCustomFunction("GETORGCOST","Organization_API.Get_Org_Cost","ITEM7_ORGCOST");
                cmd.addReference("ITEM7_CONTRACT","GETORGSITE/DATA");
                cmd.addReference("ITEM7_ORG_CODE","GETORG/DATA");
            }
            else
            {
                cmd = trans.addCustomFunction("ORGCO","Organization_API.Get_Org_Cost","ITEM7_ORGCOST");
                cmd.addParameter("ITEM7_CONTRACT",sOrgContract);
                cmd.addParameter("ITEM7_ORG_CODE",sOrgCode);

                cmd = trans.addCustomFunction("ROLCO","Role_API.Get_Role_Cost","ITEM7_ROLECOST");
                cmd.addParameter("ITEM7_ROLE_CODE");
            }

               //bug id 59224,start
                
                cmd = trans.addCustomFunction("GETITEM7ORGDESC","Organization_Api.Get_Description","ITEM7_ORG_CODE_DESC");
                cmd.addParameter("ITEM7_CONTRACT",sOrgContract);
                cmd.addParameter("ITEM7_ORG_CODE",sOrgCode);
            
                trans = mgr.validate(trans);

                String sitem7OrgDesc = trans.getValue("GETITEM7ORGDESC/DATA/ITEM7_ORG_CODE_DESC");
                //bug id 59224,end

            if (mgr.isEmpty(sOrgCode) && mgr.isEmpty(mgr.readValue("ITEM7_ROLE_CODE")))
            {
                cos = trans.getNumberValue("GETORGCOST/DATA/ITEM7_ORGCOST");
                if (isNaN(cos))
                    cos = 0;
            }
            else
            {
                orgco = trans.getNumberValue("ORGCO/DATA/ITEM7_ORGCOST");
                if (isNaN(orgco))
                    orgco = 0;

                rolco = trans.getNumberValue("ROLCO/DATA/ITEM7_ROLECOST");
                if (isNaN(rolco))
                    rolco = 0;

                if (rolco == 0)
                    cos = orgco;
                else
                    cos   = rolco;
            }

            String colAmountCostStr = countCostValItem7(cos);

            String cosStr = mgr.getASPField("ITEM7_COST").formatNumber(cos);
            txt = (mgr.isEmpty(cosStr)?"":cosStr)+"^"+(mgr.isEmpty(colAmountCostStr)?"":colAmountCostStr)+"^"+
                  (mgr.isEmpty(sOrgCode)?"":sOrgCode)+"^"+(mgr.isEmpty(sOrgContract)?"":sOrgContract)+"^"+(mgr.isEmpty(sitem7OrgDesc)?"":sitem7OrgDesc)+"^";
            mgr.responseWrite(txt);
        }

        else if ("ITEM7_ROLE_CODE".equals(val))
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
            String new_role_code = mgr.readValue("ITEM7_ROLE_CODE","");

            if (new_role_code.indexOf("^",0)>0)
            {
                if (!mgr.isEmpty(mgr.readValue("ITEM7_SIGN")))
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
                sRoleCode = mgr.readValue("ITEM7_ROLE_CODE");
                sOrgContract = mgr.readValue("ITEM7_CONTRACT"); 
            }

            if (mgr.isEmpty(sRoleCode) && mgr.isEmpty(mgr.readValue("ITEM7_ORG_CODE")))
            {
                cmd = trans.addCustomFunction("GETORG","Active_Work_Order_API.Get_Org_Code","ITEM7_ORG_CODE");
                cmd.addParameter("ITEM7_WO_NO",mgr.readValue("ITEM7_WO_NO"));

                cmd = trans.addCustomFunction("GETORGSITE","Active_Work_Order_API.Get_Contract","ITEM7_CONTRACT");
                cmd.addParameter("ITEM7_WO_NO",mgr.readValue("ITEM7_WO_NO"));

                cmd = trans.addCustomFunction("GETORGCOST","Organization_API.Get_Org_Cost","ITEM7_ORGCOST");
                cmd.addReference("ITEM7_CONTRACT","GETORGSITE/DATA");
                cmd.addReference("ITEM7_ORG_CODE","GETORG/DATA");
            }
            else
            {
                cmd = trans.addCustomFunction("ORGCO","Organization_API.Get_Org_Cost","ITEM7_ORGCOST");
                cmd.addParameter("ITEM7_CONTRACT",sOrgContract);
                cmd.addParameter("ITEM7_ORG_CODE",mgr.readValue("ITEM7_ORG_CODE"));

                cmd = trans.addCustomFunction("ROLCO","Role_API.Get_Role_Cost","ITEM7_ROLECOST");
                cmd.addParameter("ITEM7_ROLE_CODE",sRoleCode);
            }
            //bug id 59224,start
            cmd = trans.addCustomFunction("ITEM7ROLEDESC","ROLE_API.Get_Description","ITEM7_ROLE_CODE_DESC");
            cmd.addParameter("ITEM7_ROLE_CODE",sRoleCode);

            trans = mgr.validate(trans);

            String sitem7roleDesc = trans.getValue("ITEM7ROLEDESC/DATA/ITEM7_ROLE_CODE_DESC");
            //bug id 59224,end

            if (mgr.isEmpty(sRoleCode) && mgr.isEmpty(mgr.readValue("ITEM7_ORG_CODE")))
            {
                cos = trans.getNumberValue("GETORGCOST/DATA/ITEM7_ORGCOST");
                if (isNaN(cos))
                    cos = 0;
            }
            else
            {
                orgco = trans.getNumberValue("ORGCO/DATA/ITEM7_ORGCOST");
                if (isNaN(orgco))
                    orgco = 0;

                rolco = trans.getNumberValue("ROLCO/DATA/ITEM7_ROLECOST");
                if (isNaN(rolco))
                    rolco = 0;

                if (rolco == 0)
                    cos = orgco;
                else
                    cos   = rolco;
            }

            String colAmountCostStr = countCostValItem7(cos); 
            String cosStr = mgr.getASPField("ITEM7_COST").formatNumber(cos);

            txt = (mgr.isEmpty(cosStr)?"":cosStr)+"^"+(mgr.isEmpty(colAmountCostStr)?"":colAmountCostStr)+"^"+
                  (mgr.isEmpty(sRoleCode)?"":sRoleCode)+"^"+(mgr.isEmpty(sOrgContract)?"":sOrgContract)+"^"+(mgr.isEmpty(sitem7roleDesc)?"":sitem7roleDesc)+"^";
            mgr.responseWrite(txt);   
        }

        else if ("ITEM7_SIGN_ID".equals(val))
        {
            String signId = mgr.readValue("ITEM7_SIGN_ID");
            String sDefRole = ""; 

            cmd = trans.addCustomFunction("EMPEXIST","Employee_API.Is_Emp_Exist","ITEM7_TEMP1");
            cmd.addParameter("ITEM7_COMPANY");
            cmd.addParameter("ITEM7_SIGN_ID");  

            trans = mgr.validate(trans);

            boolean bEmpExist = !("0".equals(trans.getValue("EMPEXIST/DATA/ITEM7_TEMP1")));

            if (bEmpExist)
            {
                trans.clear();

                cmd = trans.addCustomFunction("GETSIGN","Company_Emp_API.Get_Person_Id","ITEM7_SIGN");
                cmd.addParameter("ITEM7_COMPANY");
                cmd.addParameter("ITEM7_SIGN_ID");  

                cmd = trans.addCustomFunction("GETDEFROLE","Employee_Role_API.Get_Default_Role","ITEM7_ROLE_CODE");
                cmd.addParameter("ITEM7_COMPANY");
                cmd.addParameter("ITEM7_SIGN_ID");  

                cmd = trans.addCustomFunction("GETORGCODE","Employee_API.Get_Organization","ITEM7_ORG_CODE");
                cmd.addParameter("ITEM7_COMPANY");
                cmd.addParameter("ITEM7_SIGN_ID");  

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
        else if ("ITEM7_SIGN".equals(val))
        {
            String reqstr = null;
            int startpos = 0;
            int endpos = 0;
            int i = 0;
            String ar[] = new String[2];
            String emp_id = "";
            String sign = "";
            double totManHrs=0;
            double remainHrs=0;
            String remainHours;
            String sOrgContract = mgr.readValue("ITEM7_CONTRACT","");
            String sOrgCode = mgr.readValue("ITEM7_ORG_CODE","");
            String roleCode = mgr.readValue("ITEM7_ROLE_CODE");
            String depart = "";
            String new_sign = mgr.readValue("ITEM7_SIGN","");
            String allocatedHours;
            double allocatedHrs = mgr.readNumberValue("ITEM7_PLAN_HRS");
            if (isNaN(allocatedHrs))
                allocatedHrs = 0;
            allocatedHours = allocatedHours = mgr.getASPField("ITEM7_TOTAL_ALLOCATED_HOURS").formatNumber(allocatedHrs);

            totManHrs = mgr.readNumberValue("ITEM7_TOTAL_MAN_HOURS");
            if (isNaN(totManHrs))
                totManHrs = 0;
            remainHrs = totManHrs - allocatedHrs ;
            remainHours = allocatedHours = mgr.getASPField("ITEM7_TOTAL_ALLOCATED_HOURS").formatNumber(remainHrs);


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
                cmd = trans.addCustomFunction("SIGNID","Employee_API.Get_Max_Maint_Emp","ITEM7_SIGN_ID");                     
                cmd.addParameter("ITEM7_COMPANY");
                cmd.addParameter("ITEM7_SIGN");

                trans = mgr.validate(trans);
                emp_id = trans.getValue("SIGNID/DATA/SIGN_ID");
                sign = new_sign;

                trans.clear();
            }

            if (mgr.isEmpty(roleCode))
            {
                cmd = trans.addCustomFunction("DEFROLE","Employee_Role_API.Get_Default_Role","ITEM7_ROLE_CODE");
                cmd.addParameter("ITEM7_COMPANY");
                cmd.addParameter("ITEM7_SIGN_ID",emp_id);
            }

            if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
            {
                cmd = trans.addCustomFunction("EMPORGCONT","Employee_API.Get_Org_Contract","ITEM7_ORG_CONTRACT");
                cmd.addParameter("ITEM7_COMPANY");
                cmd.addParameter("ITEM7_SIGN_ID",emp_id);
            }

            if (mgr.isEmpty(sOrgCode))
            {
                cmd = trans.addCustomFunction("EMPORGCOD","Employee_API.Get_Organization","ITEM7_ORG_CODE");
                cmd.addParameter("ITEM7_COMPANY");
                cmd.addParameter("ITEM7_SIGN_ID",emp_id);
            }

            trans = mgr.validate(trans);

            if (mgr.isEmpty(roleCode))
                roleCode = trans.getValue("DEFROLE/DATA/ROLE_CODE");

            if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
                sOrgContract = trans.getValue("EMPORGCONT/DATA/ORG_CONTRACT");

            if (mgr.isEmpty(sOrgCode))
            {
                depart = trans.getValue("EMPORGCOD/DATA/ORG_CODE");
                sOrgCode = depart;
            }

            double nCost = mgr.readNumberValue("ITEM7_COST");
            if (isNaN(nCost))
                nCost = 0;

            String colOrgCode = depart;
            String colRoleCode = roleCode;

            nCost = getCostValItem7(nCost,sOrgContract,colOrgCode,colRoleCode,sOrgContract,sOrgCode);

            String nCostStr = mgr.getASPField("ITEM7_COST").formatNumber(nCost);
            String colAmountCostStr = countCostValItem7(nCost);
            trans.clear();
            //bug id 59224,start
                
            cmd = trans.addCustomFunction("ITEM7ORGDESC","Organization_Api.Get_Description","ITEM7_ORG_CODE_DESC");
            cmd.addParameter("ITEM7_CONTRACT",sOrgContract);
            cmd.addParameter("ITEM7_ORG_CODE",sOrgCode);
           
            cmd = trans.addCustomFunction("ITEM7ROLEDESC","ROLE_API.Get_Description","ITEM7_ROLE_CODE_DESC");
            cmd.addParameter("ITEM7_ROLE_CODE",roleCode);


            trans = mgr.validate(trans);

            String sitem7OrgDesc = trans.getValue("ITEM7ORGDESC/DATA/ITEM7_ORG_CODE_DESC");
            String sitem7roleDesc = trans.getValue("ITEM7ROLEDESC/DATA/ITEM7_ROLE_CODE_DESC");

            //bug id 59224,end

            txt = (mgr.isEmpty(emp_id) ? "": (emp_id))+ "^" + 
                  (mgr.isEmpty(roleCode) ? "": (roleCode))+ "^"+ 
                  (mgr.isEmpty(sOrgCode) ? "": (sOrgCode))+ "^"+ 
                  (mgr.isEmpty(nCostStr) ? "": (nCostStr)) + "^"+
                  (mgr.isEmpty(colAmountCostStr) ? "": (colAmountCostStr))+ "^"+
                  (mgr.isEmpty(sign)? "": (sign))+ "^"+
                  (mgr.isEmpty(sOrgContract)? "": (sOrgContract))+ "^" +
                  (mgr.isEmpty(allocatedHours)? "": (allocatedHours))+ "^" +  //allocated hours
                  (mgr.isEmpty(allocatedHours)? "": (allocatedHours))+ "^" +  //total allocated hours
                  (mgr.isEmpty(remainHours)? "": (remainHours))+ "^"+
                  (mgr.isEmpty(sitem7OrgDesc)? "": (sitem7OrgDesc))+ "^"+
                  (mgr.isEmpty(sitem7roleDesc)? "": (sitem7roleDesc))+ "^";
            mgr.responseWrite(txt);

        }
        else if ("ITEM7_DATE_FROM".equals(val))
        {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("DATE_FROM",mgr.readValue("ITEM7_DATE_FROM",""));      
            mgr.responseWrite(mgr.readValue("ITEM7_DATE_FROM",""));
        }
        else if ("ITEM7_DATE_TO".equals(val))
        {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("DATE_TO",mgr.readValue("ITEM7_DATE_TO",""));    
            mgr.responseWrite(mgr.readValue("ITEM7_DATE_TO",""));
        }

        else if ("ITEM7_JOB_ID".equals(val))
        {
            cmd = trans.addCustomFunction("GETEMP","Work_Order_Job_API.Get_Executed_By","ITEM7_SIGN_ID");
            cmd.addParameter("ITEM7_WO_NO");
            cmd.addParameter("ITEM7_JOB_ID");

            cmd = trans.addCustomFunction("GETPERSON","Company_Emp_API.Get_Person_Id","ITEM7_SIGN");
            cmd.addParameter("ITEM7_COMPANY");
            cmd.addReference("ITEM7_SIGN_ID","GETEMP/DATA");

            trans = mgr.validate(trans);

            String emp_id = trans.getValue("GETEMP/DATA/SIGN_ID");
            String sign = trans.getValue("GETPERSON/DATA/SIGN");

            txt = (mgr.isEmpty(emp_id)?"":emp_id) + "^"+(mgr.isEmpty(sign)?"":sign) + "^";
            mgr.responseWrite(txt);
        }

        //-------------
        else if ("ITEM8_ORG_CODE".equals(val))
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
                    sOrgCode = ar[3];
                    sOrgContract = ar[4];
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
                sOrgCode = mgr.readValue("ITEM8_ORG_CODE");
                sOrgContract = mgr.readValue("ITEM8_CONTRACT"); 
            }

            //bug id 59224,start
              // trans.clear(); 
                cmd = trans.addCustomFunction("GETITEM8ORGDESC","Organization_Api.Get_Description","ITEM8_ORG_CODE_DESC");
                cmd.addParameter("ITEM8_CONTRACT",sOrgContract);
                cmd.addParameter("ITEM8_ORG_CODE",sOrgCode);
            

           //bug id 59224,end

            trans = mgr.validate(trans);

            String sitem8OrgDesc = trans.getValue("GETITEM8ORGDESC/DATA/ITEM8_ORG_CODE_DESC");


            txt = (mgr.isEmpty(sOrgCode)?"":sOrgCode)+"^"+(mgr.isEmpty(sOrgContract)?"":sOrgContract)+"^"+(mgr.isEmpty(sitem8OrgDesc)?"":sitem8OrgDesc)+"^";
            mgr.responseWrite(txt);
        }
        else if ("ITEM8_ROLE_CODE".equals(val))
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
                sRoleCode = mgr.readValue("ITEM8_ROLE_CODE");
                sOrgContract = mgr.readValue("ITEM8_CONTRACT"); 
            }
            //bug id 59224,start
            //trans.clear();
            cmd = trans.addCustomFunction("ITEM8ROLEDESC","ROLE_API.Get_Description","ITEM8_ROLE_CODE_DESC");
            cmd.addParameter("ITEM8_ROLE_CODE",sRoleCode);

            trans = mgr.validate(trans);
            String sitem8roleDesc = trans.getValue("ITEM8ROLEDESC/DATA/ITEM8_ROLE_CODE_DESC");

            //bug id 59224,end

            txt = (mgr.isEmpty(sRoleCode)?"":sRoleCode)+"^"+(mgr.isEmpty(sOrgContract)?"":sOrgContract)+"^"+(mgr.isEmpty(sitem8roleDesc)?"":sitem8roleDesc)+"^";
            mgr.responseWrite(txt);   
        }
        else if ("ITEM8_SIGN_ID".equals(val))
        {
            String signId = mgr.readValue("ITEM8_SIGN_ID");
            String sDefRole = ""; 

            cmd = trans.addCustomFunction("EMPEXIST","Employee_API.Is_Emp_Exist","ITEM8_TEMP");
            cmd.addParameter("ITEM8_COMPANY");
            cmd.addParameter("ITEM8_SIGN_ID");  

            trans = mgr.validate(trans);

            boolean bEmpExist = !("0".equals(trans.getValue("EMPEXIST/DATA/ITEM8_TEMP")));

            if (bEmpExist)
            {
                trans.clear();

                cmd = trans.addCustomFunction("GETSIGN","Company_Emp_API.Get_Person_Id","ITEM8_SIGN");
                cmd.addParameter("ITEM8_COMPANY");
                cmd.addParameter("ITEM8_SIGN_ID");  

                cmd = trans.addCustomFunction("GETDEFROLE","Employee_Role_API.Get_Default_Role","ITEM8_ROLE_CODE");
                cmd.addParameter("ITEM8_COMPANY");
                cmd.addParameter("ITEM8_SIGN_ID");  

                cmd = trans.addCustomFunction("GETORGCODE","Employee_API.Get_Organization","ITEM8_ORG_CODE");
                cmd.addParameter("ITEM8_COMPANY");
                cmd.addParameter("ITEM8_SIGN_ID");  

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
        else if ("ITEM8_SIGN".equals(val))
        {
            String reqstr = null;
            int startpos = 0;
            int endpos = 0;
            int i = 0;
            String ar[] = new String[2];
            String emp_id = "";
            String sign = "";
            String sOrgContract = mgr.readValue("ITEM8_CONTRACT","");
            String sOrgCode = mgr.readValue("ITEM8_ORG_CODE","");
            String roleCode = mgr.readValue("ITEM8_ROLE_CODE");
            String depart = "";

            String new_sign = mgr.readValue("ITEM8_SIGN","");

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

            if (mgr.isEmpty(roleCode))
            {
                cmd = trans.addCustomFunction("DEFROLE","Employee_Role_API.Get_Default_Role","ITEM8_ROLE_CODE");
                cmd.addParameter("ITEM8_COMPANY");
                cmd.addParameter("ITEM8_SIGN_ID",emp_id);
            }

            if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
            {
                cmd = trans.addCustomFunction("EMPORGCONT","Employee_API.Get_Org_Contract","ITEM8_CONTRACT");
                cmd.addParameter("ITEM8_COMPANY");
                cmd.addParameter("ITEM8_SIGN_ID",emp_id);
            }

            if (mgr.isEmpty(sOrgCode))
            {
                cmd = trans.addCustomFunction("EMPORGCOD","Employee_API.Get_Organization","ITEM8_ORG_CODE");
                cmd.addParameter("ITEM8_COMPANY");
                cmd.addParameter("ITEM8_SIGN_ID",emp_id);
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
                
                cmd = trans.addCustomFunction("GETITEM8ORGDESC","Organization_Api.Get_Description","ITEM8_ORG_CODE_DESC");
                cmd.addParameter("ITEM8_CONTRACT",sOrgContract);
                cmd.addParameter("ITEM8_ORG_CODE",sOrgCode);
            
                           
                cmd = trans.addCustomFunction("ITEM8ROLEDESC","ROLE_API.Get_Description","ITEM8_ROLE_CODE_DESC");
                cmd.addParameter("ITEM8_ROLE_CODE",roleCode);


                trans = mgr.validate(trans);

                String sitem8OrgDesc = trans.getValue("GETITEM8ORGDESC/DATA/ITEM8_ORG_CODE_DESC");
                String sitem8roleDesc = trans.getValue("ITEM8ROLEDESC/DATA/ITEM8_ROLE_CODE_DESC");

            //bug id 59224,end


            txt = (mgr.isEmpty(emp_id) ? "": (emp_id))+ "^" + 
                  (mgr.isEmpty(roleCode) ? "": (roleCode))+ "^"+ 
                  (mgr.isEmpty(sOrgCode) ? "": (sOrgCode))+ "^"+ 
                  (mgr.isEmpty(sign)? "": (sign))+ "^"+
                  (mgr.isEmpty(sOrgContract)? "": (sOrgContract))+ "^"+
                  (mgr.isEmpty(sitem8OrgDesc)? "": (sitem8OrgDesc))+ "^"+
                  (mgr.isEmpty(sitem8roleDesc)? "": (sitem8roleDesc))+ "^";
            mgr.responseWrite(txt);

        }

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
            txt = buf.getFieldValue("ITEM8_DATE_FROM_MASKED") + "^";
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


    public String countCostVal( double cost )
    {
        ASPManager mgr = getASPManager();
        double nListprice = cost;

        colPlanHrs = mgr.readNumberValue("ITEM2_PLAN_HRS");
        if (isNaN(colPlanHrs))
            colPlanHrs = 0;

        colPlanMen = mgr.readNumberValue("PLAN_MEN");
        if (isNaN(colPlanMen))
            colPlanMen = 0;

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

    //method used for cost calculations for Itemblock7
    public String countCostValItem7( double cost )
    {
        ASPManager mgr = getASPManager();
        double nListprice = cost;

        colPlanHrs = mgr.readNumberValue("ITEM7_PLAN_HRS");
        if (isNaN(colPlanHrs))
            colPlanHrs = 0;

        colPlanMen = mgr.readNumberValue("ITEM7_PLAN_MEN");
        if (isNaN(colPlanMen))
            colPlanMen = 0;

        if (colPlanHrs == 0)
            colAmountCost = 0;
        else
        {
            if (colPlanMen == 0)
                colAmountCost = nListprice * colPlanHrs;
            else
                colAmountCost = nListprice * colPlanHrs * colPlanMen;
        }
        String colAmountCostStr = mgr.getASPField("ITEM7_COSTAMOUNT").formatNumber(colAmountCost);

        return colAmountCostStr;
    }


    public double getCostVal(double nCost,String colOrgContract,String colOrgCode,String colRoleCode,String sOrgContract,String sOrgCode)
    {
        ASPManager mgr = getASPManager();

        if (!mgr.isEmpty(colRoleCode))
        {
            trans.clear();

            cmd = trans.addCustomFunction("ROLECOS","Role_API.Get_Role_Cost","COST");
            cmd.addParameter("ROLE_CODE",colRoleCode);

            trans = mgr.validate(trans);

            nCost = trans.getNumberValue("ROLECOS/DATA/COST");

            if (isNaN(nCost))
                nCost = 0;

            numCost = trans.getBuffer("ROLECOS/DATA").getValue("COST");
        }
        if (nCost == 0 && !mgr.isEmpty(colOrgContract) && !mgr.isEmpty(colOrgCode))
        {
            trans.clear();

            cmd = trans.addCustomFunction("ORGCOS","Organization_API.Get_Org_Cost","COST");
            cmd.addParameter("ITEM2_CONTRACT",colOrgContract);
            cmd.addParameter("ITEM2_ORG_CODE",colOrgCode);

            trans = mgr.validate(trans);

            nCost = trans.getNumberValue("ORGCOS/DATA/COST");
            if (isNaN(nCost))
                nCost = 0;

            numCost = trans.getBuffer("ORGCOS/DATA").getValue("COST");
        }

        if (nCost == 0 && !mgr.isEmpty(sOrgContract) && !mgr.isEmpty(sOrgCode))
        {
            trans.clear();

            cmd = trans.addCustomFunction("ORGCOS","Organization_API.Get_Org_Cost","COST");
            cmd.addParameter("ITEM2_CONTRACT",sOrgContract);
            cmd.addParameter("ITEM2_ORG_CODE",sOrgCode);

            trans = mgr.validate(trans);

            nCost = trans.getNumberValue("ORGCOS/DATA/COST");
            if (isNaN(nCost))
                nCost = 0;

            numCost = trans.getBuffer("ORGCOS/DATA").getValue("COST");
        }

        return nCost;
    }

    //method used for cost calculations for Itemblock7
    public double getCostValItem7(double nCost,String colOrgContract,String colOrgCode,String colRoleCode,String sOrgContract,String sOrgCode)
    {
        ASPManager mgr = getASPManager();

        if (!mgr.isEmpty(colRoleCode))
        {
            trans.clear();

            cmd = trans.addCustomFunction("ROLECOS","Role_API.Get_Role_Cost","ITEM7_COST");
            cmd.addParameter("ITEM7_ROLE_CODE",colRoleCode);

            trans = mgr.validate(trans);

            nCost = trans.getNumberValue("ROLECOS/DATA/ITEM7_COST");

            if (isNaN(nCost))
                nCost = 0;

            numCost = trans.getBuffer("ROLECOS/DATA").getValue("ITEM7_COST");
        }
        if (nCost == 0 && !mgr.isEmpty(colOrgContract) && !mgr.isEmpty(colOrgCode))
        {
            trans.clear();

            cmd = trans.addCustomFunction("ORGCOS","Organization_API.Get_Org_Cost","ITEM7_COST");
            cmd.addParameter("ITEM7_CONTRACT",colOrgContract);
            cmd.addParameter("ITEM7_ORG_CODE",colOrgCode);

            trans = mgr.validate(trans);

            nCost = trans.getNumberValue("ORGCOS/DATA/ITEM7_COST");
            if (isNaN(nCost))
                nCost = 0;

            numCost = trans.getBuffer("ORGCOS/DATA").getValue("ITEM7_COST");
        }

        if (nCost == 0 && !mgr.isEmpty(sOrgContract) && !mgr.isEmpty(sOrgCode))
        {
            trans.clear();

            cmd = trans.addCustomFunction("ORGCOS","Organization_API.Get_Org_Cost","ITEM7_COST");
            cmd.addParameter("ITEM7_CONTRACT",sOrgContract);
            cmd.addParameter("ITEM7_ORG_CODE",sOrgCode);

            trans = mgr.validate(trans);

            nCost = trans.getNumberValue("ORGCOS/DATA/ITEM7_COST");
            if (isNaN(nCost))
                nCost = 0;

            numCost = trans.getBuffer("ORGCOS/DATA").getValue("ITEM7_COST");
        }

        return nCost;
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

        trans.clear();
        cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
        cmd.addParameter("PART_OWNERSHIP",itemset.getRow().getValue("PART_OWNERSHIP"));
        trans = mgr.validate(trans);
        String sClientOwnershipDefault = trans.getValue("VALIDOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");
        trans.clear();

        if (checksec("Inventory_Part_In_Stock_API.Get_Inventory_Quantity",1,isSecure))
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
                cmd.addParameter("CONDITION_CODE",itemset.getRow().getFieldValue("CONDITION_CODE"));
            }    
        }
        trans = mgr.perform(trans);


        if ( "true".equals(isSecure[1]) )
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
    
    private String getDateTimeFormat(String type){
        if("JAVA".equals(type)){
            return "yyyy-MM-dd HH:mm:ss";
        }
        else if ("SQL".equals(type)){
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

        q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");

        if (mgr.dataTransfered())
        {
            retBuffer =  mgr.getTransferedData();
	    ret_data_buffer = mgr.newASPBuffer();

            if (retBuffer.itemExists("CURR_ROW"))
            {
                curr_row_exists = "TRUE";
                ret_data_buffer = retBuffer.getBuffer("ROWS");
                currrow = Integer.parseInt(retBuffer.getValue("CURR_ROW"));
                lout = retBuffer.getValue("LAYOUT");

                q.addOrCondition(ret_data_buffer);
            }
            else
            {
		q.addOrCondition(mgr.getTransferedData());
            }
        }

        q.setOrderByClause("WO_NO");
        q.includeMeta("ALL");

        mgr.querySubmit(trans, headblk);

        eval(headset.syncItemSets());

        if ("TRUE".equals(curr_row_exists))
        {
            headset.goTo(currrow);
            if ("1".equals(lout))
                headlay.setLayoutMode(headlay.MULTIROW_LAYOUT);
        }

        if (headset.countRows() == 1)
        {
            if (tabs.getActiveTab() == 1)
                okFindITEM2();
            else if (tabs.getActiveTab() == 2)
                okFindITEM3();
            else if (tabs.getActiveTab() == 3)
                okFindITEM5();
            else if (tabs.getActiveTab() == 4)
                okFindITEM6();
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            if ("1".equals(headset.getRow().getValue("EXCEPTION_EXISTS")))
                bException = true;
            else
                bException = false;
        }
        else if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2NODATA: No data found."));
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
        String repById = trans.getValue("REPBID/DATA/REPORTED_BY_ID");

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
        data.setFieldItem("DIFF",headset.getValue("DIFF")); 
        data.setFieldItem("PLAN_F_DATE",headset.getRow().getFieldValue("PLAN_F_DATE")); 
        data.setFieldItem("PLAN_HRS",headset.getValue("PLAN_HRS")); 
        data.setFieldItem("PLANNED_MAN_HRS",headset.getValue("PLANNED_MAN_HRS")); 
        data.setFieldItem("OP_STATUS_ID",headset.getValue("OP_STATUS_ID")); 
        data.setFieldItem("PRIORITY_ID",headset.getValue("PRIORITY_ID")); 
        data.setFieldItem("WORK_TYPE_ID",headset.getValue("WORK_TYPE_ID")); 
        data.setFieldItem("WORK_DESCR_LO",headset.getValue("WORK_DESCR_LO")); 
        data.setFieldItem("VENDOR_NO",headset.getValue("VENDOR_NO")); 
        data.setFieldItem("REPORTED_BY",headset.getValue("REPORTED_BY")); 
        data.setFieldItem("PREPARED_BY",headset.getValue("PREPARED_BY")); 
        data.setFieldItem("WORK_MASTER_SIGN",headset.getValue("WORK_MASTER_SIGN")); 
        data.setFieldItem("WORK_LEADER_SIGN",headset.getValue("WORK_LEADER_SIGN")); 
        data.setFieldItem("AUTHORIZE_CODE",headset.getValue("AUTHORIZE_CODE")); 
        data.setFieldItem("COST_CENTER",headset.getValue("COST_CENTER")); 
        data.setFieldItem("PROJECT_NO",headset.getValue("PROJECT_NO")); 
        data.setFieldItem("OBJECT_NO",headset.getValue("OBJECT_NO")); 
        data.setFieldItem("REG_DATE",headset.getRow().getFieldValue("REG_DATE")); 
        data.setFieldItem("TEST_POINT_ID",headset.getValue("TEST_POINT_ID")); 
        data.setFieldItem("QUOTATION_ID",headset.getValue("QUOTATION_ID")); 
        data.setFieldItem("SALESMANCODE",headset.getValue("SALESMANCODE")); 
        data.setFieldItem("CUSTOMER_NO",headset.getValue("CUSTOMER_NO")); 
        data.setFieldItem("CONTACT",headset.getValue("CONTACT")); 
        data.setFieldItem("REFERENCE_NO",headset.getValue("REFERENCE_NO")); 
        data.setFieldItem("PHONE_NO",headset.getValue("PHONE_NO")); 
        data.setFieldItem("ADDRESS1",headset.getValue("ADDRESS1")); 
        data.setFieldItem("ADDRESS2",headset.getValue("ADDRESS2")); 
        data.setFieldItem("ADDRESS3",headset.getValue("ADDRESS3")); 
        data.setFieldItem("ADDRESS4",headset.getValue("ADDRESS4")); 
        data.setFieldItem("ADDRESS5",headset.getValue("ADDRESS5")); 
        data.setFieldItem("ADDRESS6",headset.getValue("ADDRESS6")); 
        data.setFieldItem("LU_NAME",headset.getValue("LU_NAME")); 
        data.setFieldItem("KEY_REF",headset.getValue("KEY_REF")); 
        data.setFieldItem("REPAIR_FLAG",headset.getValue("REPAIR_FLAG")); 
        data.setFieldItem("OPSTATUSIDDES",headset.getValue("OPSTATUSIDDES")); 
        data.setFieldItem("REQUIRED_START_DATE",headset.getRow().getFieldValue("REQUIRED_START_DATE")); 
        data.setFieldItem("REQUIRED_END_DATE",headset.getRow().getFieldValue("REQUIRED_END_DATE")); 
        data.setFieldItem("COMPANY",headset.getValue("COMPANY")); 
        data.setFieldItem("REPORTED_BY_ID",headset.getValue("REPORTED_BY_ID")); 
        data.setFieldItem("WORK_LEADER_SIGN_ID",headset.getValue("WORK_LEADER_SIGN_ID")); 
        data.setFieldItem("AGREEMENT_ID",headset.getValue("AGREEMENT_ID")); 
        data.setFieldItem("WO_KEY_VALUE",headset.getValue("WO_KEY_VALUE")); 
        data.setFieldItem("STD_TEXT",headset.getValue("STD_TEXT")); 
        data.setFieldItem("ISVIM",headset.getValue("ISVIM")); 
        data.setFieldItem("PREPARED_BY_ID",headset.getValue("PREPARED_BY_ID")); 
        data.setFieldItem("WORK_MASTER_SIGN_ID",headset.getValue("WORK_MASTER_SIGN_ID")); 
        data.setFieldItem("ERR_DISCOVER_CODE",headset.getValue("ERR_DISCOVER_CODE")); 
        data.setFieldItem("ERR_SYMPTOM",headset.getValue("ERR_SYMPTOM")); 
        data.setFieldItem("ERR_DESCR_LO",headset.getValue("ERR_DESCR_LO")); 
        data.setFieldItem("HEAD_TEMP",headset.getValue("HEAD_TEMP")); 
        //data.setFieldItem("PRE_ACCOUNTING_ID",headset.getValue("PRE_ACCOUNTING_ID")); 
        data.setFieldItem("ACTION_CODE_ID",headset.getValue("ACTION_CODE_ID")); 
        data.setFieldItem("FAULT_REP_FLAG",headset.getValue("FAULT_REP_FLAG")); 
        data.setFieldItem("NORGANIZATIONORG_COST",headset.getValue("NORGANIZATIONORG_COST")); 
        data.setFieldItem("NOTE",headset.getValue("NOTE")); 
        data.setFieldItem("MODULE",headset.getValue("MODULE")); 
        data.setFieldItem("SITE_DATE",headset.getRow().getFieldValue("SITE_DATE")); 
        data.setFieldItem("SET_TIME",headset.getValue("SET_TIME")); 
        data.setFieldItem("EXEC_TIME",headset.getValue("EXEC_TIME")); 
        data.setFieldItem("CONTRACT1",headset.getValue("CONTRACT1")); 
        data.setFieldItem("COMPANY1",headset.getValue("COMPANY1")); 
        data.setFieldItem("USERID",headset.getValue("USERID")); 
        data.setFieldItem("FNDUSER",headset.getValue("FNDUSER")); 
        data.setFieldItem("INFO",headset.getValue("INFO")); 
        data.setFieldItem("ATTR",headset.getValue("ATTR")); 
        data.setFieldItem("ACTION",headset.getValue("ACTION")); 
        data.setFieldItem("OBJ_SUP_WARRANTY",headset.getValue("OBJ_SUP_WARRANTY")); 
        data.setFieldItem("SUP_WARR_DESC",headset.getValue("SUP_WARR_DESC")); 
        data.setFieldItem("SUPPLIER",headset.getValue("SUPPLIER")); 
        data.setFieldItem("SUP_WARRANTY",headset.getValue("SUP_WARRANTY")); 
        data.setFieldItem("SUP_WARR_TYPE",headset.getValue("SUP_WARR_TYPE")); 
        data.setFieldItem("SUPP_DESCR",headset.getValue("SUPP_DESCR")); 
        data.setFieldItem("TRANSFERRED",headset.getValue("TRANSFERRED")); 
        data.setFieldItem("UPDATE_PRICE",headset.getValue("UPDATE_PRICE")); 
        data.setFieldItem("HEAD_ISVALID",headset.getValue("HEAD_ISVALID")); 
        data.setFieldItem("HEAD_FACSEQUENCE",headset.getValue("HEAD_FACSEQUENCE")); 
        data.setFieldItem("PART_REV",headset.getValue("PART_REV")); 
        data.setFieldItem("AGRMNT_PART_NO",headset.getValue("AGRMNT_PART_NO")); 
        data.setFieldItem("HEAD_PART_NO",headset.getValue("HEAD_PART_NO")); 
        data.setFieldItem("HEAD_SERIAL_NO",headset.getValue("HEAD_SERIAL_NO")); 
        data.setFieldItem("CONDITION_CODE_USAGE",headset.getValue("CONDITION_CODE_USAGE")); 
        data.setFieldItem("ACTIVITY_SEQ",headset.getValue("ACTIVITY_SEQ")); 
        data.setFieldItem("PROJECT_ID",headset.getValue("PROJECT_ID")); 
        data.setFieldItem("INCLUDE_STANDARD",headset.getValue("INCLUDE_STANDARD")); 
        data.setFieldItem("INCLUDE_PROJECT",headset.getValue("INCLUDE_PROJECT")); 

        headset.addRow(data);
    }   

    public void saveNew()
    {
        ASPManager mgr = getASPManager();

        headset.changeRow();
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

        int currrow = headset.getCurrentRowNo();
        mgr.submit(trans);
        headset.goTo(currrow);
        okFindITEM2();
        okFindITEM3();
        okFindITEM5();
        headlay.setLayoutMode(headlay.getHistoryMode());

        qrystr = mgr.getURL()+"?SEARCH=Y&WO_NO="+headset.getRow().getValue("WO_NO");
    }

    public void saveReturn()
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();
        String sPpExist = "";

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

        int currrow = headset.getCurrentRowNo();
        mgr.submit(trans);
        headset.goTo(currrow);
        okFindITEM2();
        okFindITEM3();
        okFindITEM5();
        headlay.setLayoutMode(headlay.getHistoryMode());

        qrystr = mgr.getURL()+"?SEARCH=Y&WO_NO="+headset.getRow().getValue("WO_NO");

        if ("REMOVED".equals(sPpExist))
            mgr.showAlert("PCMWACTIVESEPARATE2PPREMOVED: Existing pre-posting values are not updated");
        else if ("CHANGED".equals(sPpExist))
            bPpChanged = true;
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
                mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2NODATA: No data found."));
                itemset2.clear();
            }
        }

        headset.goTo(headrowno);
        if (itemset2.countRows() > 0)
            okFindITEM8();

    }

    public void countFindITEM2()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk2);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ?");
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
        c.addLocalReference("COMPANY","OUT");
        c.addLocalReference("CONTRACT","IN");
        c.setOption("FORCE","Y");
        cmd.addPostCommand(c);
//bug ID 59224,start
        
         cmd = trans.addCustomFunction("GETITEM2ORGDESC","Organization_Api.Get_Description","ITEM2_ORG_CODE_DESC");
         cmd.addParameter("ITEM2_CONTRACT",headset.getRow().getValue("CONTRACT"));
         cmd.addParameter("ITEM2_ORG_CODE",headset.getRow().getValue("ORG_CODE"));

        trans = mgr.perform(trans);
        String compan = trans.getValue("ITEM2/DATA/COMPANY");
        String sitem2OrgDesc = trans.getValue("GETITEM2ORGDESC/DATA/ITEM2_ORG_CODE_DESC");
//bug ID 59224,end
        data = trans.getBuffer("ITEM2/DATA");
        data.setFieldItem("ITEM2_WO_NO",headset.getRow().getValue("WO_NO")); 
        data.setFieldItem("ITEM2_CONTRACT",headset.getRow().getValue("CONTRACT")); 
        data.setFieldItem("ITEM2_ORG_CODE",headset.getRow().getValue("ORG_CODE"));
        data.setFieldItem("ITEM2_ORG_CODE_DESC",sitem2OrgDesc);
        data.setFieldItem("ITEM2_COMPANY",compan);
        if (!mgr.isEmpty(headset.getRow().getValue("PLAN_S_DATE"))){

            data.setFieldItem("DATE_FROM",headset.getRow().getFieldValue("PLAN_S_DATE"));
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
        if (!mgr.isEmpty(headset.getRow().getValue("PLAN_F_DATE")))
            data.setFieldItem("DATE_TO",headset.getRow().getFieldValue("PLAN_F_DATE"));

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
    //-----

    public void okFindITEM8()
    {
        if (itemset2.countRows() > 0)
        {
            ASPManager mgr = getASPManager();

            trans.clear();

            int headrowno = headset.getCurrentRowNo();
            int currrow = itemset2.getCurrentRowNo();

            // SQLInjection_Safe ILSOLK 20070705
            q = trans.addQuery(itemblk8);
            q.addWhereCondition("WO_NO = ? AND PARENT_ROW_NO= ?");
	    q.addParameter("WO_NO" , headset.getRow().getValue("WO_NO"));
	    q.addParameter("PARENT_ROW_NO" , itemset2.getRow().getValue("ROW_NO"));
            q.includeMeta("ALL");
            mgr.submit(trans);

            if (comBarAct)
            {
                if (itemset8.countRows() == 0 && "ITEM8.OkFind".equals(mgr.readValue("__COMMAND")))
                {
                    mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE8NODATA: No data found."));
                    itemset8.clear();
                }
            }

            headset.goTo(headrowno);
            itemset2.goTo(currrow);

        }
    }

    public void countFindITEM8()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk8);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ? AND PARENT_ROW_NO = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        q.addParameter("ROW_NO",itemset2.getRow().getValue("ROW_NO"));
        int headrowno = headset.getCurrentRowNo();
        mgr.submit(trans);
        itemlay8.setCountValue(toInt(itemset8.getRow().getValue("N")));
        itemset8.clear();
        headset.goTo(headrowno);
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
        c.addLocalReference("COMPANY","OUT");
        c.addLocalReference("CONTRACT","IN");
        c.setOption("FORCE","Y");
        cmd.addPostCommand(c);

        //bug ID 59224,start

        cmd = trans.addCustomFunction("GETITEM8ORGDESC","Organization_Api.Get_Description","ITEM8_ORG_CODE_DESC");
        cmd.addParameter("ITEM8_CONTRACT",headset.getRow().getValue("CONTRACT"));
        cmd.addParameter("ITEM8_ORG_CODE",headset.getRow().getValue("ORG_CODE"));

        trans = mgr.perform(trans);
        String sitem8OrgDesc = trans.getValue("GETITEM8ORGDESC/DATA/ITEM8_ORG_CODE_DESC");
        //bug ID 59224,end
        
        String compan = trans.getValue("ITEM8/DATA/COMPANY");
        data = trans.getBuffer("ITEM8/DATA");
        data.setFieldItem("ITEM8_WO_NO",headset.getRow().getValue("WO_NO")); 
        data.setFieldItem("ITEM8_CONTRACT",headset.getRow().getValue("CONTRACT")); 
        data.setFieldItem("ITEM8_ORG_CODE",headset.getRow().getValue("ORG_CODE"));
        data.setFieldItem("ITEM8_ORG_CODE_DESC",sitem8OrgDesc); 
        data.setFieldItem("PARENT_ROW_NO",itemset2.getRow().getValue("ROW_NO")); 
        data.setFieldItem("ITEM8_ROLE_CODE",itemset2.getRow().getValue("ROLE_CODE")); 
        data.setFieldItem("ITEM8_ROLE_CODE_DESC",itemset2.getRow().getValue("ROLE_CODE_DESC"));
        data.setFieldItem("ALLOCATED_HOURS",itemset2.getRow().getValue("PLAN_HRS")); 
        data.setFieldItem("ITEM8_TEAM_ID",itemset2.getRow().getValue("TEAM_ID")); 
        data.setFieldItem("ITEM8_TEAM_CONTRACT",itemset2.getRow().getValue("TEAM_CONTRACT")); 
        data.setFieldItem("ITEM8_COMPANY",compan);



        if (!mgr.isEmpty(headset.getRow().getValue("PLAN_S_DATE"))){
            data.setFieldItem("ITEM8_DATE_FROM",headset.getRow().getFieldValue("PLAN_S_DATE"));
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
        if (!mgr.isEmpty(headset.getRow().getValue("PLAN_F_DATE")))
            data.setFieldItem("ITEM8_DATE_TO",headset.getRow().getFieldValue("PLAN_F_DATE"));

        itemset8.addRow(data);
    }

    public void editRowITEM8(){
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
    //-----
    public void okFindITEM3()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        int headrowno = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk3);

        q.addWhereCondition("CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT) AND WO_NO = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        q.includeMeta("ALL");

        mgr.querySubmit(trans,itemblk3);

        if (comBarAct)
        {
            if (itemset3.countRows() == 0 && "ITEM3.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2NODATA: No data found."));
                itemset3.clear();
            }
        }
        if (itemset3.countRows() > 0) {
          okFindITEM4();
        }

        headset.goTo(headrowno);
    }

    public void countFindITEM3()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk3);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        mgr.submit(trans);
        itemlay3.setCountValue(toInt(itemset3.getRow().getValue("N")));
        itemset3.clear();
    }

    public void okFindITEM4()
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
            q.addParameter("WO_NO",itemset3.getRow().getFieldValue("ITEM3_WO_NO"));
            q.addParameter("MAINT_MATERIAL_ORDER_NO",itemset3.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));
            q.includeMeta("ALL");

            mgr.querySubmit(trans,itemblk);

            headset.goTo(headsetRowNo);
            itemset3.goTo(item3rowno);

            if (itemset.countRows() > 0)
                setValuesInMaterials();
        }
        //Bug 72202,End
    }

    public void okFindITEM5()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        if (headset.countRows()>0)
        {

            int headrowno = headset.getCurrentRowNo();

            q = trans.addQuery(itemblk5);
            q.addWhereCondition("WO_NO = ?");
            q.addParameter("WO_NO", headset.getRow().getValue("WO_NO"));
            q.includeMeta("ALL");
            mgr.submit(trans);

            if (comBarAct)
            {
                if (itemset5.countRows() == 0 && "ITEM5.OkFind".equals(mgr.readValue("__COMMAND")))
                {
                    mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE5NODATA: No data found."));
                    itemset5.clear();
                }
            }

            headset.goTo(headrowno);
        }
    }

    public void countFindITEM5()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk5);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO", headset.getRow().getValue("WO_NO"));
        int headrowno = headset.getCurrentRowNo();
        mgr.submit(trans);
        itemlay5.setCountValue(toInt(itemset5.getRow().getValue("N")));
        itemset5.clear();
        headset.goTo(headrowno);
    }

    public void newRowITEM5()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        cmd = trans.addEmptyCommand("ITEM5","WORK_ORDER_TOOL_FACILITY_API.New__",itemblk5);
        cmd.setParameter("ITEM5_WO_NO",headset.getRow().getValue("WO_NO"));
        cmd.setOption("ACTION","PREPARE");

        trans = mgr.perform(trans);

        data = trans.getBuffer("ITEM5/DATA");

        if (!mgr.isEmpty(headset.getRow().getValue("PLAN_S_DATE")))
            data.setFieldItem("FROM_DATE_TIME",headset.getRow().getFieldValue("PLAN_S_DATE"));
        if (!mgr.isEmpty(headset.getRow().getValue("PLAN_F_DATE")))
            data.setFieldItem("TO_DATE_TIME",headset.getRow().getFieldValue("PLAN_F_DATE"));

        itemset5.addRow(data);
    }

    public void okFindITEM6()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        int headrowno = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk6);
        q.addWhereCondition("WO_NO = ? AND STD_JOB_CONTRACT = ?");
        q.addParameter("WO_NO",headset.getValue("WO_NO"));
        q.addParameter("CONTRACT",headset.getValue("CONTRACT"));

        q.includeMeta("ALL");
        mgr.querySubmit(trans, itemblk6);

        headset.goTo(headrowno);
        if (itemset6.countRows() > 0)
            okFindITEM7();
    }

    public void countFindITEM6()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        int headrowno = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk6);
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

        cmd = trans.addCustomFunction("GETSDATE", "Active_Separate_API.Get_Plan_S_Date", "ITEM6_DATE_FROM");
        cmd.addParameter("ITEM6_WO_NO", headset.getValue("WO_NO"));

        cmd = trans.addCustomFunction("GETFDATE", "Active_Separate_API.Get_Plan_F_Date", "ITEM6_DATE_TO");
        cmd.addParameter("ITEM6_WO_NO", headset.getValue("WO_NO"));

        trans = mgr.perform(trans);

        data = trans.getBuffer("ITEM6/DATA");

        Date dPlanSDate = trans.getBuffer("GETSDATE/DATA").getFieldDateValue("ITEM6_DATE_FROM");
        Date dPlanFDate = trans.getBuffer("GETFDATE/DATA").getFieldDateValue("ITEM6_DATE_TO");

        data.setFieldDateItem("ITEM6_DATE_FROM", dPlanSDate);
        data.setFieldDateItem("ITEM6_DATE_TO", dPlanFDate);
        itemset6.addRow(data);
    }


    public void okFindITEM7()
    {
        ASPManager mgr = getASPManager();
        if (itemset6 != null && itemset6.countRows() > 0)
        {

            trans.clear();

            int headrowNo = headset.getCurrentRowNo();
            int itemset6rowNo = itemset6.getCurrentRowNo();

            q = trans.addQuery(itemblk7);

            q.addWhereCondition("WO_NO = ? AND JOB_ID = ?");
            q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
            q.addParameter("JOB_ID",itemset6.getRow().getValue("JOB_ID"));

            q.includeMeta("ALL");

            mgr.submit(trans);

            if (comBarAct)
            {
                if (itemset7.countRows() == 0 && "ITEM7.OkFind".equals(mgr.readValue("__COMMAND")))
                {
                    mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE7NODATA: No data found."));
                    itemset7.clear();
                }
            }

            headset.goTo(headrowNo);
            itemset6.goTo(itemset6rowNo);
        }
    }

    public void countFindITEM7()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk7);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ? AND JOB_ID = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        q.addParameter("JOB_ID",itemset6.getRow().getValue("JOB_ID"));
        int headrowno = itemset7.getCurrentRowNo();
        mgr.submit(trans);
        itemlay7.setCountValue(toInt(itemset7.getRow().getValue("N")));
        itemset7.clear();
        itemset7.goTo(headrowno);
    }     

    public void newRowITEM7()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        cmd = trans.addEmptyCommand("ITEM7","WORK_ORDER_ROLE_API.New__",itemblk7);
        cmd.setParameter("ITEM7_WO_NO",headset.getRow().getValue("WO_NO"));
        cmd.setOption("ACTION","PREPARE");
        c = mgr.newASPCommand();      
        c.defineCustomFunction("Site_API.Get_Company");
        c.addLocalReference("COMPANY","OUT");
        c.addLocalReference("CONTRACT","IN");
        c.setOption("FORCE","Y");
        cmd.addPostCommand(c);
        //bug ID 59224,start

        cmd = trans.addCustomFunction("GETITEM7ORGDESC","Organization_Api.Get_Description","ITEM7_ORG_CODE_DESC");
        cmd.addParameter("ITEM7_CONTRACT",headset.getRow().getValue("CONTRACT"));
        cmd.addParameter("ITEM7_ORG_CODE",headset.getRow().getValue("ORG_CODE"));

        trans = mgr.perform(trans);
        String compan = trans.getValue("ITEM7/DATA/COMPANY");
         String sitem7OrgDesc = trans.getValue("GETITEM7ORGDESC/DATA/ITEM7_ORG_CODE_DESC");
        //bug ID 59224,end
        data = trans.getBuffer("ITEM7/DATA");
        data.setFieldItem("ITEM7_WO_NO",headset.getRow().getValue("WO_NO")); 
        data.setFieldItem("ITEM7_CONTRACT",headset.getRow().getValue("CONTRACT")); 
        data.setFieldItem("ITEM7_ORG_CODE",headset.getRow().getValue("ORG_CODE"));
        data.setFieldItem("ITEM7_ORG_CODE_DESC",sitem7OrgDesc);
        data.setFieldItem("ITEM7_CONTRACT",headset.getRow().getValue("CONTRACT")); 
        data.setFieldItem("ITEM7_JOB_ID",itemset6.getRow().getValue("JOB_ID")); 
        data.setFieldItem("ITEM7_COMPANY",compan);
        
        if (mgr.isEmpty(itemset6.getRow().getValue("DATE_FROM"))) {
            data.setFieldDateItem("ITEM7_DATE_FROM", headset.getRow().getFieldDateValue("PLAN_S_DATE"));
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
            data.addFieldDateItem("ITEM7_DATE_FROM_MASKED", dt);
            data.setFieldDateItem("ITEM7_DATE_FROM_MASKED", dt);
        }
        else{
            data.setFieldDateItem("ITEM7_DATE_FROM", itemset6.getRow().getFieldDateValue("ITEM6_DATE_FROM"));
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
            data.addFieldDateItem("ITEM7_DATE_FROM_MASKED", dt);
            data.setFieldDateItem("ITEM7_DATE_FROM_MASKED", dt);
        }
        if (mgr.isEmpty(itemset6.getRow().getValue("DATE_TO"))) {
            data.setFieldDateItem("ITEM7_DATE_TO", headset.getRow().getFieldDateValue("PLAN_F_DATE"));
        }
        else{
            data.setFieldDateItem("ITEM7_DATE_TO", itemset6.getRow().getFieldDateValue("ITEM6_DATE_TO"));
        }

        itemset7.addRow(data);
    }

    public void editRowITEM7(){
        ASPManager mgr = getASPManager();

        data = itemset7.getRow();

        DateFormat df= DateFormat.getInstance();
        Date dt = null;
        try
        {
            dt = df.parse(data.getFieldValue("ITEM7_DATE_FROM"));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        data.addFieldDateItem("ITEM7_DATE_FROM_MASKED", dt);
        data.setFieldDateItem("ITEM7_DATE_FROM_MASKED", dt);

        itemset7.setRow(data);
        itembar7.getASPRowSet().store();
        itemlay7.setLayoutMode(itemlay7.EDIT_LAYOUT);
        
    }

    public void  duplicateITEM5()
    {
        ASPManager mgr = getASPManager();

        if ( itemlay5.isMultirowLayout() )
            itemset5.goTo(itemset5.getRowSelected());

        itemlay5.setLayoutMode(itemlay5.NEW_LAYOUT);

        trans.clear();
        cmd = trans.addEmptyCommand("ITEM5","WORK_ORDER_TOOL_FACILITY_API.New__",itemblk5);
        cmd.setParameter("ITEM5_WO_NO",headset.getRow().getValue("WO_NO"));
        cmd.setOption("ACTION","PREPARE");


        trans = mgr.perform(trans);

        data = trans.getBuffer("ITEM5/DATA");

        data.setFieldItem("TOOL_FACILITY_ID",itemset5.getRow().getValue("TOOL_FACILITY_ID"));
        data.setFieldItem("TOOL_FACILITY_DESC",itemset5.getRow().getValue("TOOL_FACILITY_DESC"));
        data.setFieldItem("TOOL_FACILITY_TYPE",itemset5.getRow().getValue("TOOL_FACILITY_TYPE"));
        data.setFieldItem("TYPE_DESCRIPTION",itemset5.getRow().getValue("TYPE_DESCRIPTION"));
        data.setFieldItem("ITEM5_CONTRACT",itemset5.getRow().getFieldValue("ITEM5_CONTRACT"));
        data.setFieldItem("ITEM5_ORG_CODE",itemset5.getRow().getFieldValue("ITEM5_ORG_CODE"));
        data.setFieldItem("ITEM5_QTY",itemset5.getRow().getFieldValue("ITEM5_QTY"));
        data.setFieldItem("ITEM5_PLANNED_HOUR",itemset5.getRow().getFieldValue("ITEM5_PLANNED_HOUR"));
        data.setFieldItem("ITEM5_CRAFT_LINE_NO",itemset5.getRow().getFieldValue("ITEM5_CRAFT_LINE_NO"));
        data.setFieldItem("ITEM5_COST",itemset5.getRow().getFieldValue("ITEM5_COST"));
        data.setFieldItem("ITEM5_COST_CURRENCY",itemset5.getRow().getFieldValue("ITEM5_COST_CURRENCY"));
        data.setFieldItem("ITEM5_COST_AMOUNT",itemset5.getRow().getFieldValue("ITEM5_COST_AMOUNT"));
        data.setFieldItem("ITEM5_CATALOG_NO_CONTRACT",itemset5.getRow().getFieldValue("ITEM5_CATALOG_NO_CONTRACT"));
        data.setFieldItem("ITEM5_CATALOG_NO",itemset5.getRow().getFieldValue("ITEM5_CATALOG_NO"));
        data.setFieldItem("ITEM5_CATALOG_NO_DESC",itemset5.getRow().getFieldValue("ITEM5_CATALOG_NO_DESC"));
        data.setFieldItem("ITEM5_SALES_PRICE",itemset5.getRow().getFieldValue("ITEM5_SALES_PRICE"));
        data.setFieldItem("ITEM5_SALES_CURRENCY",itemset5.getRow().getFieldValue("ITEM5_SALES_CURRENCY"));
        data.setFieldItem("ITEM5_DISCOUNT",itemset5.getRow().getFieldValue("ITEM5_DISCOUNT"));
        data.setFieldItem("ITEM5_DISCOUNTED_PRICE",itemset5.getRow().getFieldValue("ITEM5_DISCOUNTED_PRICE"));
        data.setFieldItem("ITEM5_PLANNED_PRICE",itemset5.getRow().getFieldValue("ITEM5_PLANNED_PRICE"));
        data.setFieldItem("FROM_DATE_TIME",itemset5.getRow().getFieldValue("FROM_DATE_TIME"));
        data.setFieldItem("TO_DATE_TIME",itemset5.getRow().getFieldValue("TO_DATE_TIME"));
        data.setFieldItem("ITEM5_NOTE",itemset5.getRow().getFieldValue("ITEM5_NOTE"));

        itemset5.addRow(data);                        
    }



    // ----------------------------------------------------------------------------------------
    // ---------------------------     Custom Functions     -----------------------------------
    // ----------------------------------------------------------------------------------------

    public void setValuesInMaterials()
    {
        ASPManager mgr = getASPManager();

        securityOk = "";
        ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer();
        //secBuff.addSecurityQuery("Inventory_Part_API","Get_Inventory_Value_By_Method");
        secBuff.addSecurityQuery("Active_Separate_API", "Get_Inventory_Value");
        secBuff = mgr.perform(secBuff);

        //if (secBuff.getSecurityInfo().itemExists("Inventory_Part_API.Get_Inventory_Value_By_Method"))
        if (secBuff.getSecurityInfo().itemExists("Active_Separate_API.Get_Inventory_Value"))
            securityOk = "true";

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
                String cataNo = itemset.getRow().getFieldValue("CATALOG_NO");
                String nPlanQty = itemset.getRow().getFieldValue("PLAN_QTY");
                String cataCont = itemset.getRow().getFieldValue("CATALOG_CONTRACT");
                String cusNo = headset.getRow().getFieldValue("CUSTOMER_NO");
                String agreeId = headset.getRow().getFieldValue("AGREEMENT_ID");
                String priceListNo = itemset.getRow().getFieldValue("PRICE_LIST_NO");
                String planLineNo = itemset.getRow().getFieldValue("PLAN_LINE_NO");

                String serialNo = itemset.getRow().getFieldValue("SERIAL_NO");
                String conditionCode = itemset.getRow().getFieldValue("CONDITION_CODE");
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

                if ("true".equals(securityOk))
                {
                    /*cmd = trans.addCustomFunction("GETCOST"+i,"Inventory_Part_API.Get_Inventory_Value_By_Method","ITEM_COST");
                    cmd.addParameter("SPARE_CONTRACT",spareCont);
                    cmd.addParameter("PART_NO",partNo); */

                    cmd = trans.addCustomCommand("GETCOST"+i,"Active_Separate_API.Get_Inventory_Value");
                    cmd.addParameter("ITEM_COST");
                    cmd.addParameter("SPARE_CONTRACT", spareCont);
                    cmd.addParameter("PART_NO", partNo);
                    cmd.addParameter("SERIAL_NO", serialNo);
                    cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
                    cmd.addParameter("CONDITION_CODE", conditionCode);
                }

                if ((!mgr.isEmpty(cataNo)) && (!mgr.isEmpty(nPlanQty)))
                {
                    cmd = trans.addCustomCommand("PRICEINFO"+i,"Work_Order_Coding_API.Get_Price_Info");
                    cmd.addParameter("BASE_PRICE","0");
                    cmd.addParameter("SALE_PRICE","0");
                    cmd.addParameter("ITEM_DISCOUNT","0");
                    cmd.addParameter("ITEM_CURRENCY_RATE","0");
                    cmd.addParameter("CATALOG_CONTRACT",cataCont);
                    cmd.addParameter("CATALOG_NO",cataNo);
                    cmd.addParameter("CUSTOMER_NO",cusNo);
                    cmd.addParameter("AGREEMENT_ID",agreeId);
                    cmd.addParameter("PRICE_LIST_NO",priceListNo);
                    cmd.addParameter("PLAN_QTY",nPlanQty);

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
                    if ("true".equals(securityOk))
                    {
                        nCost= trans.getNumberValue("GETCOST"+i+"/DATA/ITEM_COST");
                        if (isNaN(nCost))
                            nCost = 0;
                    }
                    else
                        nCost   = 0;
                }
                if ( "SUPPLIER LOANED".equals(itemset.getRow().getValue("PART_OWNERSHIP_DB")) || "TRUE".equals(sObjLoan))
                   nCost = 0;

                double nPlanQty = itemset.getRow().getNumberValue("PLAN_QTY");

                if (isNaN(nPlanQty))
                    nPlanQty = 0;

                double nCostTemp;                
                double nCostAmount;

                if (nPlanQty != 0)
                {
                    if ("true".equals(securityOk))
                    {
                        nCostTemp = trans.getNumberValue("GETCOST"+i+"/DATA/ITEM_COST");
                        if (isNaN(nCostTemp))
                            nCostTemp = 0;
                    }
                    else
                        nCostTemp = 0;
                    if ( "SUPPLIER LOANED".equals(itemset.getRow().getValue("PART_OWNERSHIP_DB")) || "TRUE".equals(sObjLoan))
                        nCostTemp = 0;
                    nCostAmount = nCostTemp * nPlanQty;
                }
                else
                    nCostAmount = 0;

                String priceListNo = itemset.getRow().getFieldValue("PRICE_LIST_NO");
                String cataNo = itemset.getRow().getFieldValue("CATALOG_NO");
                double nDiscount = itemset.getRow().getNumberValue("DISCOUNT");
                if (isNaN(nDiscount))
                    nDiscount = 0;

                if ((!mgr.isEmpty(cataNo)) && (nPlanQty != 0))
                {
                    double listPrice = trans.getNumberValue("LISTPRICE"+i+"/DATA/LIST_PRICE");
                    if (isNaN(listPrice))
                        listPrice = 0;

                    double nSalesPriceAmount;

                    if (mgr.isEmpty(priceListNo))
                        priceListNo = trans.getBuffer("PRICEINFO"+i+"/DATA").getFieldValue("PRICE_LIST_NO");

                    if (nDiscount == 0)
                        nDiscount = trans.getNumberValue("PRICEINFO"+i+"/DATA/ITEM_DISCOUNT");

                    if (isNaN(nDiscount))
                        nDiscount = 0;

                    double planQty ;

                    if (listPrice == 0)
                    {
                        listPrice = trans.getNumberValue("PRICEINFO"+i+"/DATA/BASE_PRICE");
                        if (isNaN(listPrice))
                            listPrice = 0;

                        planQty = trans.getNumberValue("PRICEINFO"+i+"/DATA/PLAN_QTY");  
                        if (isNaN(planQty))
                            planQty = 0;

                        double nSaleUnitPrice = trans.getNumberValue("PRICEINFO"+i+"/DATA/SALE_PRICE");
                        if (isNaN(nSaleUnitPrice))
                            nSaleUnitPrice =0;
                        nSalesPriceAmount  = nSaleUnitPrice * planQty;
                    }
                    else
                    {
                        double nListPriceTemp = trans.getNumberValue("LISTPRICE"+i+"/DATA/LIST_PRICE");
                        if (isNaN(nListPriceTemp))
                            nListPriceTemp = 0;

                        planQty = trans.getNumberValue("PRICEINFO"+i+"/DATA/PLAN_QTY");  
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
                    row.setNumberValue("SALESPRICEAMOUNT",nSalesPriceAmount);
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

    public void countFindITEM4()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ? AND MAINT_MATERIAL_ORDER_NO = ?");
        q.addParameter("WO_NO",itemset3.getRow().getFieldValue("ITEM3_WO_NO"));
        q.addParameter("MAINT_MATERIAL_ORDER_NO",itemset3.getRow().getFieldValue("MAINT_MATERIAL_ORDER_NO"));
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

    public void newRowITEM4()
    {
        ASPManager mgr = getASPManager();

        cmd = trans.addEmptyCommand("ITEM4","MAINT_MATERIAL_REQ_LINE_API.New__",itemblk);
        cmd.setParameter("SPARE_CONTRACT",itemset3.getRow().getFieldValue("ITEM3_CONTRACT"));
        cmd.setParameter("CATALOG_CONTRACT",headset.getRow().getValue("CONTRACT"));
        cmd.setParameter("ITEM_WO_NO",itemset3.getRow().getFieldValue("ITEM3_WO_NO"));
        cmd.setParameter("ITEM0_MAINT_MATERIAL_ORDER_NO",itemset3.getRow().getFieldValue("MAINT_MATERIAL_ORDER_NO"));
        cmd.setOption("ACTION","PREPARE");

        cmd = trans.addCustomFunction("GETCURRCODE","ACTIVE_SEPARATE_API.GET_CURRENCY_CODE","CURRENCY_CODE");
        cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

        trans = mgr.perform(trans);

        String currCode = trans.getValue("GETCURRCODE/DATA/CURRENCY_CODE");
        String item3Contract = itemset3.getValue("CONTRACT");

        data = trans.getBuffer("ITEM4/DATA");
        data.setFieldItem("DATE_REQUIRED",itemset3.getRow().getFieldValue("DUE_DATE"));
        data.setFieldItem("SPARE_CONTRACT",item3Contract);
        data.setFieldItem("CURRENCY_CODE",currCode);
        data.setFieldItem("CATALOG_CONTRACT",headset.getRow().getValue("CONTRACT"));


        itemset.addRow(data);
    }

    public void duplicateITEM4()
    {
        ASPManager mgr = getASPManager();

        if (itemlay.isMultirowLayout())
            itemset.goTo(itemset.getRowSelected());
        else
            itemset.selectRow();

        itemlay.setLayoutMode(itemlay.NEW_LAYOUT);

        cmd = trans.addEmptyCommand("ITEM4","MAINT_MATERIAL_REQ_LINE_API.New__",itemblk);
        cmd.setParameter("ITEM_WO_NO",itemset3.getRow().getFieldValue("ITEM3_WO_NO"));
        cmd.setParameter("ITEM0_MAINT_MATERIAL_ORDER_NO",itemset3.getRow().getFieldValue("MAINT_MATERIAL_ORDER_NO"));
        cmd.setOption("ACTION","PREPARE");

        trans = mgr.perform(trans);

        data = trans.getBuffer("ITEM4/DATA");

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
        data.setFieldItem("CATALOG_NO",itemset.getRow().getValue("CATALOG_NO"));
        data.setFieldItem("CATALOGDESC",itemset.getRow().getValue("CATALOGDESC"));
        data.setFieldItem("PRICE_LIST_NO",itemset.getRow().getValue("PRICE_LIST_NO"));
        data.setFieldItem("LIST_PRICE",itemset.getRow().getFieldValue("LIST_PRICE"));
        data.setFieldItem("UNITMEAS",itemset.getRow().getValue("UNITMEAS"));
        data.setFieldItem("CATALOG_CONTRACT",itemset.getRow().getValue("CATALOG_CONTRACT"));
        data.setFieldItem("PLAN_LINE_NO",itemset.getRow().getValue("PLAN_LINE_NO"));
        data.setFieldItem("ITEM_DISCOUNT",itemset.getRow().getFieldValue("ITEM_DISCOUNT"));
        data.setFieldItem("SALESPRICEAMOUNT",itemset.getRow().getFieldValue("SALESPRICEAMOUNT"));
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

    public void duplicateITEM8()
    {
        ASPManager mgr = getASPManager();

        if (itemlay8.isMultirowLayout())
            itemset8.goTo(itemset8.getRowSelected());
        else
            itemset8.selectRow();

        itemlay8.setLayoutMode(itemlay8.NEW_LAYOUT);

        cmd = trans.addEmptyCommand("ITEM8","WORK_ORDER_ROLE_API.New__",itemblk8);
        cmd.setParameter("ITEM8_WO_NO",headset.getRow().getValue("WO_NO"));
        cmd.setOption("ACTION","PREPARE");

        trans = mgr.perform(trans);

        data = trans.getBuffer("ITEM8/DATA");
        data.setFieldItem("ITEM8_WO_NO",headset.getRow().getValue("WO_NO")); 
        data.setFieldItem("PARENT_ROW_NO",itemset2.getRow().getValue("ROW_NO")); 
        data.setFieldItem("ITEM8_COMPANY",itemset2.getRow().getValue("COMPANY"));
        data.setFieldItem("ITEM8_CONTRACT",itemset8.getRow().getValue("CONTRACT")); 
        data.setFieldItem("ITEM8_ORG_CODE",itemset8.getRow().getValue("ORG_CODE")); 
        data.setFieldItem("ITEM8_SIGN",itemset8.getRow().getValue("SIGN")); 
        data.setFieldItem("ITEM8_SIGN_ID",itemset8.getRow().getValue("SIGN_ID")); 
        data.setFieldItem("ITEM8_TEAM_CONTRACT",itemset8.getRow().getValue("TEAM_CONTRACT")); 


        itemset8.addRow(data);                        
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

    public void saveReturnITEM2()
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
            okFindITEM8();
            headset.goTo(currHead);
            itemset2.goTo(currrowItem2);
        }
    }


    public void saveReturnITEM6()
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
        okFindITEM2();
        okFindITEM3();
        okFindITEM5();
        okFindITEM7();
        itemset6.goTo(currrowItem6);
    }

    public void saveReturnITEM7()
    {
        ASPManager mgr = getASPManager();
        if ("FALSE".equals(mgr.readValue("SAVEEXECUTE")))
            itemlay7.setLayoutMode(itemlay7.EDIT_LAYOUT);
        else
        {
            int currHead = headset.getCurrentRowNo();
            int currrowItem6 = itemset6.getCurrentRowNo();
            int currrowItem7 = itemset7.getCurrentRowNo();   
            itemset7.changeRow();
            mgr.submit(trans);
            headset.goTo(currHead);
            itemset6.goTo(currrowItem6);
            itemset7.goTo(currrowItem7);   
        }
    }


    public void saveReturnITEM8()
    {
        ASPManager mgr = getASPManager();

        int currHead = headset.getCurrentRowNo();
        int currrowItem2 = itemset2.getCurrentRowNo();
        int currrowItem8 = itemset8.getCurrentRowNo();
        itemset8.changeRow();
        mgr.submit(trans);

        trans.clear();
        q = trans.addEmptyQuery(itemblk2);
        q.addWhereCondition("WO_NO = ? AND PARENT_ROW_NO IS NULL");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        q.includeMeta("ALL");
        mgr.submit(trans);

        headset.goTo(currHead);
        itemset2.goTo(currrowItem2);
        itemset8.goTo(currrowItem8);
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
      okFindITEM6();
       
    }
    //Bug 89703, End

    //-----------------------------------------------------------------------------
    //------------------------  HEADBAR CUSTOM FUNCTIONS  --------------------------
    //-----------------------------------------------------------------------------

    // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
    // 031204  ARWILK  Begin  (Replace links with RMB's)
    public void reportInWorkOrder()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.store();
        else
        {
            headset.unselectRows();
            headset.selectRow();
        }

        bOpenNewWindow = true;
	//Bug Id 70012, Start
        urlString = createTransferUrl("ActiveSeperateReportInWorkOrder.page?&PASS_PLN_S_DATE="+mgr.URLEncode(headset.getRow().getValue("PLAN_S_DATE"))
				      +"&PASS_WORK_TYPE_ID="+mgr.URLEncode(headset.getRow().getValue("WORK_TYPE_ID"))
				      +"&PASS_CON_TYPE_DB="+mgr.URLEncode(headset.getRow().getValue("CONNECTION_TYPE_DB")), headset.getSelectedRows("WO_NO"));
	//Bug Id 70012, End	

        newWinHandle = "reportInWo"; 
    }
    // 031204  ARWILK  End  (Replace links with RMB's)


    public void refreshForm()
    {
       ASPManager mgr = getASPManager();
       //headset.refreshRow();
       //okFindITEM3();
       okFindITEM4();
       
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


    public void structure()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.store();
        else
        {
            headset.unselectRows();
            headset.selectRow();
        }

        bOpenNewWindow = true;
        urlString = createTransferUrl("SeparateWorkOrder.page", headset.getSelectedRows("WO_NO"));
        newWinHandle = "structureSep"; 
    }
    // 040108  ARWILK  End  (Enable Multirow RMB actions)

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

        String contract = headset.getRow().getValue("CONTRACT");
        String wo_no = headset.getRow().getValue("WO_NO");
        lout = (headlay.isMultirowLayout()?"1":"0");

        GetEnabledFields();
        makePrePostBuffer();

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

        String code_a = trans.getValue("POSTI/DATA/CODE_A");
        String code_b = trans.getValue("POSTI/DATA/CODE_B");
        String code_c = trans.getValue("POSTI/DATA/CODE_C");
        String code_d = trans.getValue("POSTI/DATA/CODE_D");
        String code_e = trans.getValue("POSTI/DATA/CODE_E");
        String code_f = trans.getValue("POSTI/DATA/CODE_F");
        String code_g = trans.getValue("POSTI/DATA/CODE_G");
        String code_h = trans.getValue("POSTI/DATA/CODE_H");
        String code_i = trans.getValue("POSTI/DATA/CODE_I");
        String code_j = trans.getValue("POSTI/DATA/CODE_J");


        if ("0".equals(code_a))  enabl0 = "0";
        else enabl0 = "1";

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

    //Making the buffer to be passed to prepost.page
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

        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
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
            printBuff.addItem("RESULT_KEY", trans.getValue("PRNT" + i + "/DATA/ATTR0"));
        }

        // 040108  ARWILK  End  (Enable Multirow RMB actions)
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

        // 040108  ARWILK  Begin  (Remove uneccessary global variables)	
        bOpenNewWindow = true;
        urlString = createTransferUrl("WorkOrderRequisHeaderRMB.page", transferBuffer);
        newWinHandle = "requisitions";        
        // 040108  ARWILK  End  (Remove uneccessary global variables)   
    }

    public void freeNotes()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        String calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);

        // 040108  ARWILK  Begin  (Remove uneccessary global variables)
        bOpenNewWindow = true;
        urlString = "WorkOrderReportPageOvw2.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO"));

        newWinHandle = "freeNotes"; 
        // 040108  ARWILK  End  (Remove uneccessary global variables)
    }

    public void permits()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        String calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);

        // 040108  ARWILK  Begin  (Remove uneccessary global variables)
        bOpenNewWindow = true;
        urlString = "WorkOrderPermit.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO"));

        newWinHandle = "permits"; 
        // 040108  ARWILK  End  (Remove uneccessary global variables)
    }

    public void workCenterLoad()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        // 040108  ARWILK  Begin  (Remove uneccessary global variables)
        bOpenNewWindow = true;
        urlString = "ActiveSeparate2WorkCenterLoad.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO"));

        newWinHandle = "test"; 
        // 040108  ARWILK  End  (Remove uneccessary global variables)
    }

    public void planning()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        String calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);

        // 040108  ARWILK  Begin  (Remove uneccessary global variables)	
        bOpenNewWindow = true;
        urlString = createTransferUrl("WorkOrderPlanning.page", headset.getSelectedRows("WO_NO, CUSTOMER_NO, AGREEMENT_ID, ORG_CODE, QUOTATION_ID"));
        newWinHandle = "planning";      
        // 040108  ARWILK  End  (Remove uneccessary global variables)   
    }

    public void coInformation()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        String calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);

        // 040108  ARWILK  Begin  (Remove uneccessary global variables)	
        bOpenNewWindow = true;
        //Bug Id 70012, Start
	urlString = createTransferUrl("ActiveSeparateReportCOInfo.page?&PASS_PLN_S_DATE="+mgr.URLEncode(headset.getRow().getValue("PLAN_S_DATE"))
				      +"&PASS_WORK_TYPE_ID="+mgr.URLEncode(headset.getRow().getValue("WORK_TYPE_ID"))
				      +"&PASS_CON_TYPE_DB="+mgr.URLEncode(headset.getRow().getValue("CONNECTION_TYPE_DB")), headset.getSelectedRows("WO_NO"));
	//Bug Id 70012, End
        newWinHandle = "coInformation";         
        // 040108  ARWILK  End  (Remove uneccessary global variables)   
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

        // 040108  ARWILK  Begin  (Remove uneccessary global variables)	
        bOpenNewWindow = true;
        urlString = createTransferUrl("MaintTask.page", headset.getSelectedRows("WO_NO"));
        newWinHandle = "maintTask";         
        // 040108  ARWILK  End  (Remove uneccessary global variables)   
    }

    public void returns()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        String calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);

        // 040108  ARWILK  Begin  (Remove uneccessary global variables)	
        bOpenNewWindow = true;
        urlString = createTransferUrl("WorkOrderReturns.page", headset.getSelectedRows("WO_NO"));
        newWinHandle = "returns";       
        // 040108  ARWILK  End  (Remove uneccessary global variables)   
    }

    public void copy()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();


        String calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);

        // 040108  ARWILK  Begin  (Remove uneccessary global variables)
        bOpenNewWindow = true;
        urlString = "CopyWorkOrderDlg.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) +
                    "&FAULT_REP_FLAG=" + mgr.URLEncode(headset.getRow().getValue("FAULT_REP_FLAG")) +
                    "&COMPANY=" + mgr.URLEncode(headset.getRow().getValue("COMPANY"));
        newWinHandle = "copy"; 
        // 040108  ARWILK  End  (Remove uneccessary global variables)
    }

    public void suppWarr()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        // 040108  ARWILK  Begin  (Remove uneccessary global variables)
        bOpenNewWindow = true;
        urlString = "SupplierWarrantyType.page?MCH_CODE=" + mgr.URLEncode(headset.getRow().getValue("MCH_CODE")) +
                    "&CONTRACT=" + mgr.URLEncode(headset.getRow().getValue("MCH_CODE_CONTRACT")) +
                    "&ROW_NO=" + mgr.URLEncode(headset.getRow().getValue("OBJ_SUP_WARRANTY")) +
                    "&WARRANTY_ID=" + mgr.URLEncode(headset.getRow().getValue("SUP_WARRANTY")) +
                    "&WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) +
                    "&FRMNAME=" + mgr.URLEncode("prepareWorkOrd") +
                    "&QRYSTR=" + mgr.URLEncode(qrystr);

        newWinHandle = "custwarr"; 
        // 040108  ARWILK  End  (Remove uneccessary global variables)
    }

    public void repInWiz()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        // 040108  ARWILK  Begin  (Remove uneccessary global variables)
        bOpenNewWindow = true;
        urlString = "ReportInWorkOrderWiz.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO"));

        newWinHandle = "reportInWiz"; 
        // 040108  ARWILK  End  (Remove uneccessary global variables)
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
                    "&PLAN_HRS="+ (mgr.isEmpty(headset.getValue("PLAN_HRS"))?"":headset.getValue("PLAN_HRS")) +
	            "&FRMNAME=" + mgr.URLEncode("prepareWorkOrd");
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
        newWinHandle = "freeTimeSearchFromOperations1"; 
    }

    public void freeTimeSearchFromOperations2()
    {
        ASPManager mgr = getASPManager();

        if (itemlay7.isMultirowLayout())
            itemset7.goTo(itemset7.getRowSelected());

        bOpenNewWindow = true;
        urlString = "FreeTimeSearchEngine.page?RES_TYPE=EMPLOYEE" +
                    "&CONTRACT=" + (mgr.isEmpty(itemset7.getValue("CONTRACT"))?"":itemset7.getValue("CONTRACT")) + 
                    "&ORG_CODE="+ (mgr.isEmpty(itemset7.getValue("ORG_CODE"))?"":itemset7.getValue("ORG_CODE")) + 
                    "&COMPETENCE="+ (mgr.isEmpty(itemset7.getValue("ROLE_CODE"))?"":itemset7.getValue("ROLE_CODE")) +
                    "&EMPLOYEE="+ (mgr.isEmpty(itemset7.getValue("SIGN_ID"))?"":itemset7.getValue("SIGN_ID")) +
                    "&WO_NO="+ (mgr.isEmpty(itemset7.getValue("WO_NO"))?"":itemset7.getValue("WO_NO")) +
                    "&ROW_NO="+ (mgr.isEmpty(itemset7.getValue("ROW_NO"))?"":itemset7.getValue("ROW_NO")) +
                    "&OBJ_ID="+ (mgr.isEmpty(itemset7.getValue("OBJID"))?"":itemset7.getValue("OBJID")) +
                    "&OBJ_VERSION="+ (mgr.isEmpty(itemset7.getValue("OBJVERSION"))?"":itemset7.getValue("OBJVERSION"));
        newWinHandle = "freeTimeSearchFromOperations2"; 
    }


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

        // int planMen=0;
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
            { */
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
                                                 "&FRMNAME=" + mgr.URLEncode("prepareWorkOrd") +
                                                 "&QRYSTR=" + mgr.URLEncode(qrystr)),data_buff);


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

        ctx.setGlobal("WO_NO", itemset2.getValue("WO_NO"));
        int head_current = headset.getCurrentRowNo();   
        String s_head_curr = String.valueOf(head_current);
        ctx.setGlobal("HEADGLOBAL", s_head_curr);


        if (itemlay6.isMultirowLayout())
        {
            itemset6.storeSelections();
            itemset6.setFilterOn();
            count = itemset6.countSelectedRows();
        }
        else
        {
            itemset6.unselectRows();
            count = 1;
        }

        for (int i = 0; i < count; i++)
        {
            cmd = trans.addQuery("GETROWS"+i,"SELECT ROW_NO,PLAN_HRS,PLAN_MEN,SIGN_ID FROM WORK_ORDER_ROLE WHERE WO_NO = ? AND JOB_ID = ?");
            cmd.addParameter("WO_NO",headset.getValue("WO_NO"));
            cmd.addParameter("JOB_ID",itemset6.getValue("JOB_ID"));

            if (itemlay6.isMultirowLayout())
                itemset6.next();

        }

        trans = mgr.perform(trans);
        //int planMen,planHrs,manHrs;
        data_buff = mgr.newASPBuffer();
        for (int i = 0; i < count; i++)
        {
            ASPBuffer getEvent = trans.getBuffer("GETROWS"+i);
            count1 = getEvent.countItems();

            for (int j = 0; j < count1 - 1; j++)
            {
                rowBuff = mgr.newASPBuffer();
                //manHrs  = 0;
                //planMen = 0;
                //planHrs = 0;
                /* if (mgr.isEmpty(getEvent.getBufferAt(j).getValue("PLAN_MEN"))) 
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


        if (itemlay6.isMultirowLayout())
            itemset6.setFilterOff();

        bOpenNewWindow = true;
        urlString= createTransferUrl("AllocateEmployees.page?&RES_TYPE=EMPLOYEE"+
                                     "&WO_NO="+ (mgr.isEmpty(headset.getValue("WO_NO"))?"":headset.getValue("WO_NO")+
                                                 "&FRMNAME=" + mgr.URLEncode("prepareWorkOrd") +
                                                 "&QRYSTR=" + mgr.URLEncode(qrystr)),data_buff);

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
        // int planMen,planHrs,manHrs;
        data_buff = mgr.newASPBuffer();

        for (int i = 0; i < count; i++)
        {

            rowBuff = mgr.newASPBuffer();  
            /*if (mgr.isEmpty(itemset7.getValue("PLAN_MEN"))) 
               planMen = 0; 
            else 
               planMen = Integer.parseInt(itemset7.getValue("PLAN_MEN"));
 
            if (mgr.isEmpty(itemset7.getValue("PLAN_HRS"))) 
               planHrs = 0;
            else 
               planHrs = Integer.parseInt(itemset7.getValue("PLAN_HRS"));
            
            manHrs = planMen * planHrs;
            
            if ((manHrs >0 ) && (mgr.isEmpty(itemset7.getValue("SIGN_ID")))) 
            {*/ 
            rowBuff.addItem("ROW_NO", itemset7.getValue("ROW_NO"));
            rowBuff.addItem("ROW_NO", itemset7.getValue("ROW_NO"));
            data_buff.addBuffer("DATA", rowBuff);    
            //}

            if (itemlay7.isMultirowLayout())
                itemset7.next();
        }

        if (itemlay7.isMultirowLayout())
            itemset7.setFilterOff();


        bOpenNewWindow = true;
        urlString= createTransferUrl("AllocateEmployees.page?&RES_TYPE=EMPLOYEE"+
                                     "&WO_NO="+ (mgr.isEmpty(headset.getValue("WO_NO"))?"":headset.getValue("WO_NO")+
                                                 "&FRMNAME=" + mgr.URLEncode("prepareWorkOrd") +
                                                 "&QRYSTR=" + mgr.URLEncode(qrystr)),data_buff);


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

        if (itemlay6.isMultirowLayout())
        {
            itemset6.storeSelections();
            count = itemset6.countRows();

        }
        else
        {

            currentRow=itemset6.getCurrentRowNo();
            rowString =String.valueOf(currentRow); 
            itemset6.unselectRows();
            count = 1;

        } 


        itemset6.first();
        for (int i = 0; i < count; i++)
        {

            if (itemset6.isRowSelected())
            {
                if (i==0)
                    rowString =String.valueOf(itemset6.getCurrentRowNo());
                else
                    rowString = rowString + "^" + String.valueOf(itemset6.getCurrentRowNo());  

                currentRow=itemset6.getCurrentRowNo();
            }

            if (itemlay6.isMultirowLayout())
                itemset6.next();
        }

        if (count!=1 )
            rowString = rowString + "^";

        if (itemlay6.isMultirowLayout())
            itemset6.setFilterOff();
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
            itemset6.first();
            for (int i=0 ; i<itemset6.countRows(); i++)
            {
                itemset6.goTo(i);
                for (int j=0 ; j<(ar.length - 1); j++)
                {
                    if (i == Integer.parseInt(ar[j]))
                    {
                        cmd = trans.addCustomCommand("JOBLINES"+i,"Work_Order_Role_API.Clear_Allocation");
                        cmd.addParameter("WO_NO",headset.getValue("WO_NO"));
                        cmd.addParameter("JOB_ID",itemset6.getValue("JOB_ID"));
                    }
                }
                itemset6.next();


            }


        }
        else
        {
            itemset6.goTo(currentRow);
            cmd = trans.addCustomCommand("JOBLINES","Work_Order_Role_API.Clear_Allocation");
            cmd.addParameter("WO_NO",headset.getValue("WO_NO"));
            cmd.addParameter("JOB_ID",itemset6.getValue("JOB_ID"));

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
            itemset7.first();
            for (int i=0 ; i<itemset7.countRows(); i++)
            {

                itemset7.goTo(i);

                for (int j=0 ; j<(ar.length - 1); j++)
                {

                    if (i == Integer.parseInt(ar[j]))
                    {

                        ASPBuffer buff = itemset7.getRow();
                        if (!mgr.isEmpty(itemset7.getValue("SIGN")))
                            buff.setFieldItem("SIGN","");
                        if (!mgr.isEmpty(itemset7.getValue("SIGN_ID")))
                            buff.setFieldItem("SIGN_ID","");
                        if (!mgr.isEmpty(itemset7.getValue("TEAM_ID")))
                            buff.setFieldItem("TEAM_ID","");

                        itemset7.setRow(buff);
                    }
                }
                itemset7.next();


            }
        }
        else
        {
            itemset7.goTo(currentRow);
            if (!mgr.isEmpty(itemset7.getValue("SIGN")))
                itemset7.setValue("SIGN","");
            if (!mgr.isEmpty(itemset7.getValue("SIGN_ID")))
                itemset7.setValue("SIGN_ID","");
            if (!mgr.isEmpty(itemset7.getValue("TEAM_ID")))
                itemset7.setValue("TEAM_ID","");

        }

        mgr.submit(trans);
        headset.goTo(headRowNo);   
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
                    "&FRMNAME=prepareWorkOrd";

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

        if (itemlay7.isMultirowLayout())
            itemset7.goTo(itemset7.getRowSelected());

        bOpenNewWindow = true;
        urlString = "WOQualificationsDlg.page?RES_TYPE=QUALIFICATIONS"+
                    "&WO_NO="+ (mgr.isEmpty(itemset7.getValue("WO_NO"))?"":itemset7.getValue("WO_NO")) +
                    "&ROW_NO="+ (mgr.isEmpty(itemset7.getValue("ROW_NO"))?"":itemset7.getValue("ROW_NO"))+
                    "&QRYSTR=" + mgr.URLEncode(qrystr) +
                    "&FRMNAME=prepareWorkOrd";

        newWinHandle = "additionalQualifications2"; 
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
                    "&FRMNAME=prepareWorkOrd";

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

        if (itemlay7.isMultirowLayout())
            itemset7.goTo(itemset7.getRowSelected());

        bOpenNewWindow = true;
        urlString = "ReSheduleDlg.page?RES_TYPE=RESCHEDULEOP"+
                    "&WO_NO="+ (mgr.isEmpty(itemset7.getValue("WO_NO"))?"":itemset7.getValue("WO_NO")) +
                    "&ROW_NO="+ (mgr.isEmpty(itemset7.getValue("ROW_NO"))?"":itemset7.getValue("ROW_NO"))+
                    "&QRYSTR=" + mgr.URLEncode(qrystr) +
                    "&PARENT_LU=WorkOrderJob" + 
                    "&FRMNAME=prepareWorkOrd";

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
                    "&FRMNAME=prepareWorkOrd";

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

        if (itemlay7.isMultirowLayout())
            itemset7.goTo(itemset7.getRowSelected());

        bOpenNewWindow = true;
        urlString = "MoveOperDlg.page?RES_TYPE=SHIFTOP"+
                    "&WO_NO="+ (mgr.isEmpty(itemset7.getValue("WO_NO"))?"":itemset7.getValue("WO_NO")) +
                    "&ROW_NO="+ (mgr.isEmpty(itemset7.getValue("ROW_NO"))?"":itemset7.getValue("ROW_NO"))+
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
                                         "&FRMNAME=prepareWorkOrd", data_buff);

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

        ctx.setGlobal("KEY_NO1", itemset7.getValue("WO_NO"));
        ctx.setGlobal("KEY_NO2", "0");
        ctx.setGlobal("KEY_NO3", "0");
        ctx.setGlobal("STR_LU", "WorkOrderRole");
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

        data_buff = mgr.newASPBuffer();

        for (int i = 0; i < count; i++)
        {
            rowBuff = mgr.newASPBuffer();

            if (i == 0)
            {
                rowBuff.addItem("ROW_NO", itemset7.getValue("ROW_NO"));
                rowBuff.addItem("ROW_NO", itemset7.getValue("ROW_NO"));
            }
            else
            {
                rowBuff.addItem(null, itemset7.getValue("ROW_NO"));
                rowBuff.addItem(null, itemset7.getValue("ROW_NO"));
            }

            data_buff.addBuffer("DATA", rowBuff);

            if (itemlay7.isMultirowLayout())
                itemset7.next();
        }

        if (itemlay7.isMultirowLayout())
            itemset7.setFilterOff();

        bOpenNewWindow = true;
        if ("TRUE".equals(mgr.readValue("REFRESHPARENT2")))
            urlString= createTransferUrl("ConnectPredecessorsDlg.page?&BLOCK=ITEM7", data_buff);
        else
            urlString= createTransferUrl("ConnectPredecessorsDlg.page?&QRYSTR=" + mgr.URLEncode(qrystr) +
                                         "&FRMNAME=prepareWorkOrd", data_buff);

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

        if (itemlay7.isMultirowLayout())
            itemset7.goTo(itemset7.getRowSelected());

        bOpenNewWindow = true;
        urlString = "ConnectOperationsDlg.page?"+ "&WO_NO="+ (mgr.isEmpty(headset.getValue("WO_NO"))?"":headset.getValue("WO_NO")) +
                    "&JOB_ID="+ (mgr.isEmpty(itemset6.getValue("JOB_ID"))?"":itemset6.getValue("JOB_ID")) +
                    "&QRYSTR=" + mgr.URLEncode(qrystr) + "&FRMNAME=prepareWorkOrd";
        newWinHandle = "connectExistingOperations"; 
        newWinHeight = 550;
        newWinWidth  = 550;
    }

    public void disconnectOperations()
    {
        ASPManager mgr = getASPManager();
        int count=0;
        int head_current   = headset.getCurrentRowNo();
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
        itemset7.first();

        for (int i = 0; i < count; i++)
        {
            buff = itemset7.getRow();
            buff.setValue("JOB_ID","");
            itemset7.setRow(buff);
            itemset7.next();
        }

        trans = mgr.submit(trans);
        headset.goTo(head_current);
        okFindITEM7();

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
       newWinHandle = "workorderaddr"; 
    }
//    Bug 83532, End

    public void turnToRepOrd()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        trans.clear();
        q = trans.addQuery("SEROBJ", "select count(*) from EQUIPMENT_SERIAL where MCH_CODE = ?");
        q.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));

        trans = mgr.perform(trans);

        buff = trans.getBuffer("SEROBJ/DATA");
        String noRowsStr = buff.getValueAt(0);

        noRows = Integer.parseInt(noRowsStr);

        if (noRows > 0)
        {
            // 040108  ARWILK  Begin  (Remove uneccessary global variables)
            bOpenNewWindow = true;
            urlString = "MoveToRepairWorkshopDlg.page?MCH_CODE=" + mgr.URLEncode(headset.getRow().getValue("MCH_CODE")) + 
                        "&CONTRACT=" + mgr.URLEncode(headset.getRow().getValue("MCH_CODE_CONTRACT")) +
                        "&QRYSTR=" + mgr.URLEncode(qrystr) +
                        "&WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) +
                        "&FRMNAME=" + mgr.URLEncode("prepareWorkOrd");

            newWinHandle = "turnToRepWO"; 
            // 040108  ARWILK  End  (Remove uneccessary global variables)
        }
        else
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTPER: Cannot perform Turn into Repair Order on this record."));
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
        headset.setValue("PROJECT_NO", "");
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

    public void changeConditionCode()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd;
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
        sPartNo = headset.getRow().getValue("HEAD_PART_NO");
        sSerialNo = headset.getRow().getValue("HEAD_SERIAL_NO");

        if (mgr.isEmpty(sPartNo)) {
                cmd = trans.addCustomCommand("GETPARTSER","Active_Separate_API.Get_Vim_Part_Serial");
                cmd.addParameter("HEAD_PART_NO");
                cmd.addParameter("HEAD_SERIAL_NO");
                cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
                trans = mgr.perform(trans);

                sPartNo = trans.getValue("GETPARTSER/DATA/HEAD_PART_NO");
                sSerialNo = trans.getValue("GETPARTSER/DATA/HEAD_SERIAL_NO");

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
        else{
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTPERCHACON: Cannot change the condition code."));
        }
        //Bug 59613, end
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

        // 040108  ARWILK  Begin  (Remove uneccessary global variables)
        bOpenNewWindow = true;
        urlString = "WorkOrderBudget.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO"));

        newWinHandle = "budget"; 
        // 040108  ARWILK  End  (Remove uneccessary global variables)
    }

    public void perform(String command) 
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
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
                // Bug 72214, start
                else if (command.equals("CANCEL__")) {
                   headlay.setLayoutMode(headlay.FIND_LAYOUT);
                }
                // Bug 72214, end
            }
        }   
        // 040108  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void reinit()
    {
       ASPManager mgr = getASPManager();
       perform("RE_INIT__");
    }

    public void observed()
    {
        ASPManager mgr = getASPManager();

        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
        perform("CONFIRM__");
        // 040108  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void underPrep()
    {
        ASPManager mgr = getASPManager();

        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
        perform("TO_PREPARE__");
        // 040108  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void prepared()
    {
        ASPManager mgr = getASPManager();

        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
        // Bug 72214, start
        String sResult = checkCBSAndToolFacilitySite("prepared");
        if (sResult == "TRUE")
            perform("PREPARE__") ;
        else if (sResult == "CBSSINGLEROW")
        {
           bOpenNewWindow = true;
           urlString= "ActiveSeparateDlg3.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) + "&EVENT=PREPARE__";
           newWinHandle = "changeWoState";
           newWinHeight = 550;
           newWinWidth  = 575;
        }
        else if (sResult == "CBSMULTIROW")
           mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CBSNOTVALID: Selected work orders include work orders that are part of a work order structure or part of a project or both. Please perform action on a single work order."));
        else if (sResult == "LACKTOOLS")
           mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2NOTVALID: Please enter Tools and Facility Details."));
        // Bug 72214, end
        // 040108  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void released()
    {
        ASPManager mgr = getASPManager();

        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
        // Bug 72214, start
        String sResult = checkCBSAndToolFacilitySite("released");
        if (sResult == "TRUE")
        {
            int count;
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
	       String eventVal = headset.getRow().getValue("OBJEVENTS");
					   
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
        else if (sResult == "CBSSINGLEROW")
	{
	   bOpenNewWindow = true;
	   urlString= "ActiveSeparateDlg3.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) + "&EVENT=RELEASE__";
	   newWinHandle = "changeWoState";
	   newWinHeight = 550;
	   newWinWidth  = 575;
	}
	else if (sResult == "CBSMULTIROW")
	   mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CBSNOTVALID: Selected work orders include work orders that are part of a work order structure or part of a project or both. Please perform action on a single work order."));
	else if (sResult == "LACKTOOLS")
	   mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2NOTVALID: Please enter Tools and Facility Details."));
	// Bug 72214, end
        // 040108  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void started()
    {
        ASPManager mgr = getASPManager();

        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
        // Bug 72214, start

        String sResult = checkCBSAndToolFacilitySite("started");
        		  
	if (sResult == "TRUE")
        {
            int count;
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
		 String eventVal = headset.getRow().getValue("OBJEVENTS");

		 if (eventVal.indexOf("StartOrder") != -1)
		    headset.markRow("START_ORDER__");
		 else if (eventVal.indexOf("Restart") != -1)
		    headset.markRow("RESTART__");

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
	else if (sResult == "CBSSINGLEROW")
	{
	   bOpenNewWindow = true;
	   urlString= "ActiveSeparateDlg3.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) + "&EVENT=START_ORDER__";
	   newWinHandle = "changeWoState";
	   newWinHeight = 550;
	   newWinWidth  = 575;
        }
        else if (sResult == "CBSMULTIROW")
	   mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CBSNOTVALID: Selected work orders include work orders that are part of a work order structure or part of a project or both. Please perform action on a single work order."));
	else if (sResult == "LACKTOOLS")
           mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2NOTVALID: Please enter Tools and Facility Details."));
        // Bug 72214, end
        // 040108  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void workDone()
    {
        ASPManager mgr = getASPManager();

        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
        if (checkToolFacilitySite())
            perform("WORK__");
        else
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2NOTVALID: Please enter Tools and Facility Details."));
        // 040108  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void reported()
    {
        ASPManager mgr = getASPManager();
        unissue = "FALSE";
        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
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
        // 040108  ARWILK  End  (Enable Multirow RMB actions)
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
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2NOTVALID: Please enter Tools and Facility Details."));
    }

    // Bug 72214, start
    public void finished()
    {
       ASPManager mgr = getASPManager();
       
       if ("VIM".equals(headset.getRow().getValue("CONNECTION_TYPE_DB"))) {
	  trans.clear();
       
	  cmd = trans.addCustomFunction("GETHASCONUP","WORK_ORDER_CONNECTION_API.Has_Connection_Up","DUMMY_HAS_CON_UP");
	  cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
     
	  trans = mgr.perform(trans);
     
	  String sHasConUp = trans.getValue("GETHASCONUP/DATA/DUMMY_HAS_CON_UP");
	  String sLastWo = "";
     
	  if ("TRUE".equals(sHasConUp)) {
	     trans.clear();
     
	     cmd = trans.addCustomFunction("GETLASTWO","Work_Order_From_Vim_API.Is_Last_Wo","DUMMY_LAST_WO");
	     cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
       
	     trans = mgr.perform(trans);
       
	     sLastWo = trans.getValue("GETLASTWO/DATA/DUMMY_LAST_WO");
     
	     if ("TRUE".equals(sLastWo)) {
		sIsLastWo = "TRUE";
		ctx.setCookie( "PageID_IsLastWo1", "TRUE" );
		return;
	     }
	  }
       }
       finished0();
    }
   
   
   public void  finished0()
   // Bug 72214, end
    {
        ASPManager mgr = getASPManager();
        unissue = "FALSE";

        repair = "FALSE";

        // Bug 72214, start
        String sResult = checkCBSAndToolFacilitySite("prepared");
        if (sResult == "TRUE")
	// Bug 72214, end
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
                if (rows > 1 ) {
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
                    mgr.redirectTo("ActiveSeparate2.page");
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
	// Bug 72214, start
        else if (sResult == "CBSMULTIROW")
           mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CBSNOTVALID: Selected work orders include work orders that are part of a work order structure or part of a project or both. Please perform action on a single work order."));
        else if (sResult == "LACKTOOLS")
           mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2NOTVALID: Please enter Tools and Facility Details."));
	// Bug 72214, end

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
            if (rows > 1 ) {
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
                mgr.redirectTo("ActiveSeparate2.page");
            //Bug 70891, end

        }
        else
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2NOTVALID: Please enter Tools and Facility Details."));

    }

    public void refreshRows()
    {
       ASPManager mgr = getASPManager();
       mgr.redirectTo("ActiveSeparate2.page");
    }

    public void cancelled()
    {
        ASPManager mgr = getASPManager();
        //Bug 92020, Start
        int rowno = headset.getCurrentRowNo();
        int rows =  headset.countRows();
        bvimwo = false;

        if (headlay.isMultirowLayout())
	{
	    headset.goTo(headset.getRowSelected());
	    rowno = headset.getCurrentRowNo();
	}
        //Bug 92020, End

        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
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
                
                openCancelDlg = "TRUE";
                sWoNo = headset.getRow().getValue("WO_NO");
                bvimwo = true;

            }
        }
	//Bug 92020,Start
	if (!bvimwo)
	{
	    trans.clear();
	    perform("CANCEL__");

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
	    {
                headset.clearRow();
	    }
	    
	}
	//Bug 92020,End
	
        // 040108  ARWILK  End  (Enable Multirow RMB actions)
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

    public void pickListForWork()
    {
        ASPManager mgr = getASPManager();

        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
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
            printBuff.addItem("RESULT_KEY", trans.getValue("PRINTPIC" + i + "/DATA/ATTR0"));
        }

        // 040108  ARWILK  End  (Enable Multirow RMB actions)
        callPrintDlg(print,true);
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
	  urlString= "ActiveSeparateDlg4.page?WO_NO=" + mgr.URLEncode(headset.getValue("WO_NO")) ;
	  newWinHandle = "rescheduleWo";
	  newWinHeight = 550;
	  newWinWidth  = 575;
       }
       else
	  mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2NOCBS: Site &1 is not connected to a scheduling server!", headset.getRow().getValue("CONTRACT")));
    }
    // Bug 72214, end

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
        trans.clear();

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

        String objState = trans.getValue("GETSTATE/DATA/OBJSTATE");
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

    //---------------------------------------------------------
    // -----------Check Tools /Facility  Site ________________

    public boolean checkToolFacilitySite()
    {
        ASPManager mgr = getASPManager();

        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
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
        // 040108  ARWILK  End  (Enable Multirow RMB actions)
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
      
       String eventVal;
       String sEvent = null;
       for (int i = 0;i < count;i++)
       {
	  cmd = trans.addCustomCommand("WOTOOLFACILITY"+i,"Work_Order_Tool_Facility_API.Check_Valid_Sites");
	  cmd.addParameter("HEAD_ISVALID");
	  cmd.addParameter("HEAD_FACSEQUENCE");
	  cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

	  eventVal = headset.getRow().getValue("OBJEVENTS");
         
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

    //-----------------------------------------------------------------------------
    //------------------------  ITEMBAR3 CUSTOM FUNCTIONS  ------------------------
    //-----------------------------------------------------------------------------

    public void performItem(String command) 
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        int headcurrow = headset.getCurrentRowNo();

        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
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
        // 040108  ARWILK  End  (Enable Multirow RMB actions)

        headset.goTo(headcurrow);
    }

    public boolean noReserv()
    {
        ASPManager mgr = getASPManager();

        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
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
        // 040108  ARWILK  End  (Enable Multirow RMB actions)
    }

    public boolean noIssue()
    {
        ASPManager mgr = getASPManager();

        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
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
            // SQLInjection_Safe ILSOLK 20070705
            cmd = trans.addQuery("GET_QTY_FOR_NO_ISSUE_" + i, "select QTY from MAINT_MATERIAL_REQ_LINE where MAINT_MATERIAL_ORDER_NO = ? ");
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
        // 040108  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void plan()
    {
        ASPManager mgr = getASPManager();

        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
        if (noReserv() && noIssue())
            performItem("PLAN__");
        else
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOT: Cannot perform on selected line"));    

        // 040108  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void release()
    {
        ASPManager mgr = getASPManager();

        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
        performItem("RELEASE__");
        // 040108  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void close()
    {
        ASPManager mgr = getASPManager();

        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
        if (noReserv())
            performItem("CLOSE__");
        else
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOT: Cannot perform on selected line"));    
        // 040108  ARWILK  End  (Enable Multirow RMB actions)
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

		mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2NONEW: No new Assigned stock for this Material Order."));
	}
	else
	    mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOT: Cannot perform on selected line"));

    }

    public void note()
    {
        ASPManager mgr = getASPManager();

        if (itemlay3.isMultirowLayout())
            itemset3.goTo(itemset3.getRowSelected());
        else
            itemset3.selectRow();

        // 040108  ARWILK  Begin  (Remove uneccessary global variables)
        bOpenNewWindow = true;
        urlString = "EditorDlg.page?SNOTETEXT=" + mgr.URLEncode(itemset3.getRow().getValue("SNOTETEXT")) +
                    "&FRMNAME=" + mgr.URLEncode("prepareWorkOrd") +
                    "&QRYSTR=" + mgr.URLEncode(qrystr);

        newWinHandle = "note"; 
        // 040108  ARWILK  End  (Remove uneccessary global variables)
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
                    "&FRMNAME=prepareWorkOrd";

        newWinHandle = "sparePart"; 
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

        bOpenNewWindow = true;

        urlString = "MaintenaceObject3.page?MCH_CODE=" + mgr.URLEncode(headset.getValue("MCH_CODE")) +
                    "&CONTRACT=" + mgr.URLEncode(headset.getValue("MCH_CODE_CONTRACT")) +
                    "&WO_NO=" + mgr.URLEncode(headset.getValue("WO_NO")) +
                    "&ORDER_NO=" + mgr.URLEncode(itemset3.getValue("MAINT_MATERIAL_ORDER_NO")) +
                    "&QRYSTR=" + mgr.URLEncode(qrystr) +
                    "&FRMNAME=prepareWorkOrd";

        newWinHandle = "objStruct";
    } 
    // 031204  ARWILK  End  (Replace links with RMB's)

    public void detchedPartList()
    {
        ASPManager mgr = getASPManager();

        int count = 0;
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
//        if (itemset.countRows() > 0) {
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
                mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOT: Cannot perform on selected line"));
            }
            else{

                bOpenNewWindow = true;
                row.addItem("PART_NO",sPartNo);
                row.addItem("WO_NO",itemset3.getRow().getValue("WO_NO"));    
                row.addItem("FRAME","prepareWorkOrd");
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
            row.addItem("FRAME","prepareWorkOrd");
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
        // 040108  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void supPerPart()
    {
        ASPManager mgr = getASPManager();

        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
        if (itemlay.isMultirowLayout())
            itemset.store();
        else
        {
            itemset.unselectRows();
            itemset.selectRow();
        }

        bOpenNewWindow = true;
        urlString = createTransferUrl("../purchw/PurchasePartSupplier.page", itemset.getSelectedRows("PART_NO"));
        newWinHandle = "suppPerPart"; 
        // 040108  ARWILK  End  (Enable Multirow RMB actions)
    }

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
             mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTSTATWO5WO: Work order status not valid for material issue."));
             return ;
          }

          if (!(sStatusCodeReleased.equals(dfStatus)))
          {
             mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTSTAT1: Maint Material Requisition status not valid for material issue."));
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
                     mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTMAT1: No material requirements for selected item."));
                     return;
                 }
                 else
                 {
                    trans.clear();

                    if (plan_qty1 > nAvailToIss)
                        mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2AVAIL: Available quantity for part ") +itemset.getRow().getValue("PART_NO") + mgr.translate("PCMWACTIVESEPARATE2AVAIL1:  was ")  +sAvailToIss+ mgr.translate("PCMWACTIVESEPARATE2AVAI2: .")); 

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
                    cmd.addParameter("ITEM3_CONTRACT",itemset3.getValue("CONTRACT"));
                    cmd.addParameter("PART_NO",itemset.getValue("PART_NO"));

                    cmd = transForIssue.addCustomFunction("REPAIR"+ successCount,"MAINTENANCE_INV_PART_API.Get_Repairable","REPAIRABLE");
                    cmd.addParameter("SPARE_CONTRACT",itemset.getValue("SPARE_CONTRACT"));
                    cmd.addParameter("PART_NO",itemset.getValue("PART_NO"));

                    //Bug 56688, Replaced Make_Issue_Detail with Make_Auto_Issue_Detail.
                    cmd = transForIssue.addCustomCommand("MAKEISSUDETA" + successCount,"MAINT_MATERIAL_REQ_LINE_API.Make_Auto_Issue_Detail");
                    cmd.addParameter("DUMMY_ACT_ISSUE");
                    cmd.addParameter("MAINT_MATERIAL_ORDER_NO",itemset.getRow().getFieldValue("ITEM0_MAINT_MATERIAL_ORDER_NO"));
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
         		double nQtyIssued = trans.getNumberValue("MAKEISSUDETA" + i + "/DATA/DUMMY_ACT_ISSUE");
         		double nQtyShort = itemset.getNumberValue("PLAN_QTY")	- nQtyIssued;
         		
         		if (nQtyShort > 0)
         		   mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANTISSUE: All material could not be issued for part &1. Remaining quantity to be issued: &2", itemset.getValue("PART_NO"), new Double(nQtyShort).toString()));
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
         okFindITEM4();

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

        int currrow = itemset3.getCurrentRowNo();

        if (itemlay.isMultirowLayout())
            itemset.goTo(itemset.getRowSelected());
        else
            itemset.selectRow();

        currrowItem4 = itemset.getCurrentRowNo(); 

        trans.clear();

        cmd = trans.addCustomFunction("MAINTREQSUP","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","SUPPLY_CODE");
        trans = mgr.perform(trans);

        String sStandardInv = trans.getValue("MAINTREQSUP/DATA/SUPPLY_CODE");

        trans.clear();

        cmd = trans.addCustomFunction("FINSTATEDEC","MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__","DB_STATE"); 
        cmd.addParameter("DB_STATE","Released"); 

        cmd = trans.addCustomFunction("ISSALLOW","MAINT_MATERIAL_REQ_LINE_API.Is_Issued_Allowed","ISS_ALLO");
        cmd.addParameter("WO_NO",itemset.getRow().getFieldValue("ITEM_WO_NO"));

        cmd = trans.addCustomFunction("AUTOREP","MAINTENANCE_INV_PART_API.Get_Auto_Repairable","AUTO_REPAIRABLE");
        cmd.addParameter("ITEM3_CONTRACT",itemset3.getRow().getFieldValue("ITEM3_CONTRACT"));
        cmd.addParameter("PART_NO",itemset.getRow().getValue("PART_NO"));

        cmd = trans.addCustomFunction("REPAIR","MAINTENANCE_INV_PART_API.Get_Repairable","REPAIRABLE");
        cmd.addParameter("SPARE_CONTRACT",itemset.getRow().getValue("SPARE_CONTRACT"));
        cmd.addParameter("PART_NO",itemset.getRow().getValue("PART_NO"));

        cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
        cmd.addParameter("PART_OWNERSHIP",itemset.getRow().getValue("PART_OWNERSHIP"));

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

        String sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DB_STATE");
        String bIssAllowed = trans.getValue("ISSALLOW/DATA/ISS_ALLO");
        String dfStatus = itemset3.getRow().getFieldValue("ITEM3_STATE");
        String isAutoRepairable = trans.getValue("AUTOREP/DATA/AUTO_REPAIRABLE");
        String isRepairable = trans.getValue("REPAIR/DATA/REPAIRABLE");

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

        if (!("TRUE".equals(bIssAllowed)))
        {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTSTATWO5WO: Work order status not valid for material issue."));
            return ;
        }

        if (!(sStatusCodeReleased.equals(dfStatus)))
        {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTSTAT1: Maint Material Requisition status not valid for material issue."));
            return ;
        }


        double plan_qty = itemset.getRow().getNumberValue("PLAN_QTY");
        if (isNaN(plan_qty))
            plan_qty = 0;

        double qty = itemset.getRow().getNumberValue("QTY");
        if (isNaN(qty))
            qty = 0;

        double qty_assign = itemset.getRow().getNumberValue("QTY_ASSIGNED");
        if ( isNaN(qty_assign) )
            qty_assign = 0;

        double qty_on = itemset.getRow().getNumberValue("QTYONHAND");
        if ( isNaN(qty_on) )
            qty_on = 0;

        double qty_return = itemset.getRow().getNumberValue("QTY_RETURNED");
        if ( isNaN(qty_return) )
            qty_return = 0;

        double qty_outstanding = plan_qty - qty; //(qty + qty_return); * ASSALK  Material Issue & Reserve modification.

        if ( qty_outstanding > 0 )
            canPerform = true;

        if (!canPerform)
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTMAT1: No material requirements for selected item."));
        else
        {

            qtyOnHand = GetInventoryQuantity("ONHAND");;
            double nRes = GetInventoryQuantity("RESERVED");
            double nAvailToIss = qtyOnHand - nRes + qty_assign;
            String sAvailToIss = mgr.getASPField("QTY_ASSIGNED").formatNumber(nAvailToIss);

            trans.clear();

            cmd = trans.addCustomCommand("MAKEISSUDETA","MAINT_MATERIAL_REQ_LINE_API.Make_Auto_Issue_Detail");
	    cmd.addParameter("DUMMY_ACT_ISSUE");
            cmd.addParameter("MAINT_MATERIAL_ORDER_NO",itemset.getRow().getFieldValue("ITEM0_MAINT_MATERIAL_ORDER_NO"));
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
                mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2AVAIL: Available quantity for part ") +itemset.getRow().getValue("PART_NO") + mgr.translate("PCMWACTIVESEPARATE2AVAIL1:  was ")  +sAvailToIss+ mgr.translate("PCMWACTIVESEPARATE2AVAI2: ."));


            if (( "TRUE".equals(isAutoRepairable) ) &&  ( "TRUE".equals(isRepairable) ) )
            {
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
                                   "&CONTRACT="+mgr.URLEncode(headset.getRow().getValue("CONTRACT"))+
                                   "&MCH_CODE="+mgr.URLEncode(headset.getRow().getValue("MCH_CODE"))+
                                   "&DESCRIPTION="+mgr.URLEncode(headset.getRow().getValue("MCH_CODE_DESCRIPTION"))+
                                   "&PART_NO="+mgr.URLEncode(itemset.getRow().getValue("PART_NO"))+
                                   "&SPAREDESCRIPTION="+mgr.URLEncode(itemset.getRow().getValue("SPAREDESCRIPTION"))+
                                   "&SPARE_CONTRACT="+mgr.URLEncode(itemset.getRow().getValue("SPARE_CONTRACT"))+
                                   "&COUNT="+nCount;


            }
            itemset3.goTo(currrow);
            //okFindITEM4();
            itemset.goTo(currrowItem4);
            itemset.refreshRow();
        }
    }

    public void matReqUnissue()
    {
        ASPManager mgr = getASPManager();

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
                mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTUNISSUESTAT1: Maint Material Requisition status not valid for material Unissue."));
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

                // 040108  ARWILK  Begin  (Remove uneccessary global variables)
                bOpenNewWindow = true;
                urlString = "ActiveWorkOrder.page?WO_NO=" + mgr.URLEncode(itemset.getRow().getFieldValue("ITEM_WO_NO")) +
                            "&MAINTMATORDNO=" + mgr.URLEncode(itemset.getRow().getFieldValue("ITEM0_MAINT_MATERIAL_ORDER_NO")) +
                            "&FRMNAME=" + mgr.URLEncode("prepareWorkOrd") +
                            "&QRYSTR=" + mgr.URLEncode(qrystr) +
                            "&PART_NO=" + mgr.URLEncode(itemset.getRow().getValue("PART_NO")) +
                            "&CONTRACT=" + mgr.URLEncode(itemset.getRow().getValue("SPARE_CONTRACT"))+
                            "&LINE_ITEM_NO="+ mgr.URLEncode(itemset.getRow().getValue("LINE_ITEM_NO"));

                newWinHandle = "manUnIssue"; 
                // 040108  ARWILK  End  (Remove uneccessary global variables)
            } 
        }
        else
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTPERFORM: Cannot perform Material Requisition Unissue on this record."));

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
          mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTSTAT2: Maint Material Requisition status not valid for material reserve."));
          return ;
       }

       if (!("TRUE".equals(trans.getValue("RESALLOW/DATA/RES_ALLO"))))
       {
          mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTSTATWO2: Work order status not valid for material reserve."));
          return;
       }

       transForReserve = mgr.newASPTransactionBuffer();

       for (int i = 0; i < count; i++)
       {

          if (itemset.getNumberValue("PLAN_QTY") <= (itemset.getNumberValue("QTY") + itemset.getNumberValue("QTY_ASSIGNED"))) // + itemset.getNumberValue("QTY_RETURNED"))) * ASSALK  Material Issue & Reserve modification.
          {
             mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTMAT3: No material requirements for selected item."));
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
             mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2AVAIL: Available quantity for part ") +itemset.getRow().getValue("PART_NO") + mgr.translate("PCMWACTIVESEPARATE2AVAIL1:  was ")  +sAvalToRes+ mgr.translate("PCMWACTIVESEPARATE2AVAI2: .")); 
          }
          trans.clear();
       }

       trans = mgr.perform(transForReserve);

       itemset.first();

       for (int i = 0; i <= successCount; i++)
       {
          nQtyShort = trans.getNumberValue("RESSHORT_" + i + "/DATA/QTY_LEFT");

          if (nQtyShort > 0)
             mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2COULDNOTALL: All material could not be allocated for part &1. Remaining quantity to be reserved: &2", itemset.getValue("PART_NO"), new Double(nQtyShort).toString()));
//          mgr.showInfo(mgr.translate("PCMWACTIVESEPARATE2COULDNOTALL: All material could not be allocated for part &1. Remaining quantity: &2.", itemset.getValue("PART_NO"), new Double(nQtyShort).toString()));

          if (itemlay.isMultirowLayout())
             itemset.next();
       }

       if (itemlay.isMultirowLayout())
          itemset.setFilterOff();

       itemset3.goTo(currrow);
       okFindITEM4();
    }

    public void manIssue()
    {
        int head_current = 0;
        double nQtyLeft = 0;
        String creRepWO = "FALSE";

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        head_current = headset.getCurrentRowNo();   
        String s_head_curr = String.valueOf(head_current);

        ASPManager mgr = getASPManager();

        int currrow = itemset3.getCurrentRowNo();

        if (itemlay.isMultirowLayout())
            itemset.goTo(itemset.getRowSelected());
        else
            itemset.selectRow();

        trans.clear();

        cmd = trans.addCustomFunction("FINSTATEDEC","MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__","DB_STATE"); 
        cmd.addParameter("DB_STATE","Released"); 

        cmd = trans.addCustomFunction("SERIALTRA","PART_CATALOG_API.Get_Serial_Tracking_Code_Db","SERIAL_TRACK");
        cmd.addParameter("PART_NO",itemset.getRow().getValue("PART_NO"));

        cmd = trans.addCustomFunction("AUTOREP","MAINTENANCE_INV_PART_API.Get_Auto_Repairable","AUTO_REPAIRABLE");
        cmd.addParameter("ITEM3_CONTRACT",itemset3.getRow().getFieldValue("ITEM3_CONTRACT"));
        cmd.addParameter("PART_NO",itemset.getRow().getValue("PART_NO"));

        cmd = trans.addCustomFunction("REPAIR","MAINTENANCE_INV_PART_API.Get_Repairable","REPAIRABLE");
        cmd.addParameter("SPARE_CONTRACT",itemset.getRow().getValue("SPARE_CONTRACT"));
        cmd.addParameter("PART_NO",itemset.getRow().getValue("PART_NO"));

        cmd = trans.addCustomFunction("ISSALLOW","MAINT_MATERIAL_REQ_LINE_API.Is_Issued_Allowed","ISS_ALLO");
        cmd.addParameter("WO_NO",itemset.getRow().getFieldValue("ITEM_WO_NO"));

        trans = mgr.perform(trans);

        String sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DB_STATE");
        String hasSerialNum = trans.getValue("SERIALTRA/DATA/SERIAL_TRACK");
        String isAutoRepairable = trans.getValue("AUTOREP/DATA/AUTO_REPAIRABLE");
        String isRepairable = trans.getValue("REPAIR/DATA/REPAIRABLE");
        String bIssAllowed = trans.getValue("ISSALLOW/DATA/ISS_ALLO");
        String dfStatus = itemset3.getRow().getFieldValue("ITEM3_STATE");

        if (!("TRUE".equals(bIssAllowed)))
        {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTSTATWO5WO: Work order status not valid for material issue."));
            return ;
        }

        if (!(sStatusCodeReleased.equals(dfStatus)))
        {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTSTAT1: Maint Material Requisition status not valid for material issue."));
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
            double nQtyLeftNum = ( plan_qty - qty );

            if (nQtyLeftNum < 0)
                nQtyLeft = 0;
            else
                nQtyLeft    =    nQtyLeftNum;

            if (( "TRUE".equals(isAutoRepairable) ) &&  ( "TRUE".equals(isRepairable) ) &&  ( "NOT SERIAL TRACKING".equals(hasSerialNum) ))
                creRepWO = "TRUE";

            String currrowStr = String.valueOf(currrow);
            ctx.setGlobal("CURRROWGLOBAL",currrowStr);
            ctx.setGlobal("WONOGLOBAL",s_head_curr);

            // 040108  ARWILK  Begin  (Remove uneccessary global variables)
            bOpenNewWindow = true;
            urlString = "InventoryPartLocationDlg.page?PART_NO=" + mgr.URLEncode(itemset.getRow().getValue("PART_NO")) +
                        "&CONTRACT=" + mgr.URLEncode(itemset.getRow().getValue("SPARE_CONTRACT")) +
                        "&FRMNAME=" + mgr.URLEncode("prepareWorkOrd") +
                        "&QRYSTR=" + mgr.URLEncode(mgr.isEmpty(qrystr)?(mgr.getURL() + "?WO_NO=" + itemset.getRow().getFieldValue("ITEM_WO_NO")):qrystr) +
                        "&WO_NO=" + mgr.URLEncode(itemset.getRow().getFieldValue("ITEM_WO_NO")) +
                        "&LINE_ITEM_NO=" + mgr.URLEncode(itemset.getRow().getValue("LINE_ITEM_NO")) +
                        "&DESCRIPTION=" + mgr.URLEncode(itemset.getRow().getValue("SPAREDESCRIPTION")) +
                        "&HEAD_CONDITION_CODE=" + mgr.URLEncode(itemset.getRow().getValue("CONDITION_CODE")) +
                        "&HEAD_CONDITION_CODE_DESC=" + mgr.URLEncode(itemset.getRow().getValue("CONDDESC")) +
                        "&QTYLEFT=" + mgr.URLEncode(new Double(nQtyLeft).toString()) +
                        "&MAINTMATORDNO=" + mgr.URLEncode(itemset.getRow().getFieldValue("ITEM0_MAINT_MATERIAL_ORDER_NO"))+
                        "&CREREPWO="+creRepWO+
                        "&MCH_CODE="+mgr.URLEncode(headset.getRow().getValue("MCH_CODE"))+
                        "&MCH_CODE_DESCRIPTION="+mgr.URLEncode(headset.getRow().getValue("MCH_CODE_DESCRIPTION"))+
                        "&OWNERSHIP=" + mgr.URLEncode(itemset.getRow().getValue("PART_OWNERSHIP")) +
                        "&OWNER=" + mgr.URLEncode(itemset.getRow().getValue("OWNER")) +
                        "&OWNERNAME=" + mgr.URLEncode(itemset.getRow().getValue("OWNER_NAME"));

            newWinHandle = "manIssue"; 
            // 040108  ARWILK  End  (Remove uneccessary global variables)
        }
        else
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTMAT: No material requirements for selected item."));

        itemset3.goTo(currrow); 

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
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTSTATWO5: Work order status not valid for material reserve."));
            return ;
        }

        if (!(sStatusCodeReleased.equals(dfStatus)))
        {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTSTAT5: Maint Material Requisition status not valid for material reserve."));
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

        double qty_return = itemset.getRow().getNumberValue("QTY_RETURNED");
        if (isNaN(qty_return))
            qty_return = 0;

        if (plan_qty > ( qty + qty_assign )) // + qty_return)) * ASSALK  Material Issue & Reserve modification.
        {
            double nQtyLeftNum = plan_qty - qty - qty_assign;
            nQtyLeft = nQtyLeftNum;

            String currrowStr = String.valueOf(currrow);

            ctx.setGlobal("CURRROWGLOBAL",currrowStr);
            ctx.setGlobal("WONOGLOBAL",s_head_curr);

            // 040108  ARWILK  Begin  (Remove uneccessary global variables)
            bOpenNewWindow = true;
            urlString = "MaterialRequisReservatDlg.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) +
                        "&LINE_ITEM_NO=" + mgr.URLEncode(itemset.getRow().getValue("LINE_ITEM_NO")) +
                        "&FRMNAME=" + mgr.URLEncode("prepareWorkOrd") +
                        "&QRYSTR=" + mgr.URLEncode(mgr.isEmpty(qrystr)?(mgr.getURL() + "?WO_NO=" + headset.getRow().getValue("WO_NO")):qrystr) +
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
            // 040108  ARWILK  End  (Remove uneccessary global variables)
        }
        else
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTMAT: No material requirements for selected item."));

        itemset3.goTo(currrow); 
    }

    public void unreserve()
    {
        int head_current = 0;
        double nQtyLeft = 0;

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        head_current = headset.getCurrentRowNo(); 
        String s_head_curr = String.valueOf(head_current);

        ASPManager mgr = getASPManager();

        int currrow = itemset3.getCurrentRowNo();

        if (itemlay.isMultirowLayout())
            itemset.goTo(itemset.getRowSelected());
        else
            itemset.selectRow();

        trans.clear();

        cmd = trans.addCustomFunction("FINSTATEDEC","MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__","DB_STATE"); 
        cmd.addParameter("DB_STATE","Released"); 

        trans = mgr.perform(trans);

        String sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DB_STATE");
        String dfStatus = itemset3.getRow().getFieldValue("ITEM3_STATE");

        if (!(sStatusCodeReleased.equals(dfStatus)))
        {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTSTAT6: Maint Material Requisition status not valid for material unreserve."));
            return ;
        }

        double qty_assign = itemset.getRow().getNumberValue("QTY_ASSIGNED");
        if (isNaN(qty_assign))
            qty_assign = 0;

        if (qty_assign > 0)
        {
            double nQtyLeftNum = qty_assign;

            nQtyLeft = nQtyLeftNum;
            String currrowStr = String.valueOf(currrow);
            ctx.setGlobal("CURRROWGLOBAL",currrowStr);
            ctx.setGlobal("WONOGLOBAL",s_head_curr);

            // 040108  ARWILK  Begin  (Remove uneccessary global variables)
            bOpenNewWindow = true;
            urlString = "MaterialRequisReservatDlg2.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) +
                        "&LINE_ITEM_NO=" + mgr.URLEncode(itemset.getRow().getValue("LINE_ITEM_NO")) +
                        "&FRMNAME=" + mgr.URLEncode("prepareWorkOrd") +
                        "&QRYSTR=" + mgr.URLEncode(qrystr) +
                        "&PART_NO=" + mgr.URLEncode(itemset.getRow().getValue("PART_NO")) +
                        "&CONTRACT=" + mgr.URLEncode(itemset.getRow().getValue("SPARE_CONTRACT")) +
                        "&DESCRIPTION=" + mgr.URLEncode(itemset.getRow().getValue("SPAREDESCRIPTION")) +
                        "&QTYLEFT=" + mgr.URLEncode(new Double(nQtyLeft).toString()) +
                        "&MAINTMATORDNO=" + mgr.URLEncode(itemset.getRow().getFieldValue("ITEM0_MAINT_MATERIAL_ORDER_NO"));

            newWinHandle = "manUnReserve"; 
            // 040108  ARWILK  End  (Remove uneccessary global variables)
        }
        else
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATE2CANNOTMAT: No material requirements for selected item."));
    }

    //-----------------------------------------------------------------------------
    //-------------------------  CUSTOM FUNCTIONS  --------------------------------
    //-----------------------------------------------------------------------------
    //------------Sets the check boxes in general tab------------------------------

    public void setCheckBoxValues()
    {
        ASPManager mgr = getASPManager();
        String cbagreement_var = "FALSE";

        trans.clear();

        tempblk = getASPBlock("TEMP");

        cmd = trans.addCustomFunction("CBWARRNT","OBJECT_SUPPLIER_WARRANTY_API.Has_Warranty","CBWARRNOBJECT");
        cmd.addParameter("CONTRACT",headset.getRow().getValue("MCH_CODE_CONTRACT"));
        cmd.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));
        cmd.addParameter("REG_DATE",headset.getRow().getFieldValue("REG_DATE"));

        trans = mgr.perform(trans);

        String cbwarranty = trans.getValue("CBWARRNT/DATA/CBWARRNOBJECT");

        if ("TRUE".equals(cbwarranty))
            cbwarranty_var = "TRUE";

        if (checkHasVimAgreement())
            cbagreement_var = "TRUE";

        row = headset.getRow();
        row.setValue("CBWARRNOBJECT",cbwarranty_var);
        row.setValue("CBHASAGRMNT",cbagreement_var);

        headset.setRow(row);
    }


    public boolean checkHasVimAgreement()
    {
        ASPManager mgr = getASPManager();
        String cbagreement = "";
        isSecure = new String[7];

        // Bug 72214, start
        if ("VIM".equals(headset.getValue("CONNECTION_TYPE_DB")))
        {
            trans.clear();
            if ( checksec("Psc_Contr_Product_API.Search_For_Agreement",2,isSecure) )
            {
               cmd = trans.addCustomFunction("GETVIMWO","Work_Order_From_Vim_API.Is_Vim_Work_Order","DUMMY_VIM_WO");
	       cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

               if ("true".equals(isSecure[2])) 
	       {
		  trans = mgr.perform(trans);
   
		  String isVimWo = trans.getValue("GETVIMWO/DATA/DUMMY_VIM_WO");
      
		  trans.clear();

		  if ("TRUE".equals(isVimWo)) {
                     cmd = trans.addCustomCommand("GETPARTSER","Active_Separate_API.Get_Vim_Part_Serial");
		     cmd.addParameter("HEAD_PART_NO");
		     cmd.addParameter("HEAD_SERIAL_NO");
		     cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
	   
		     cmd = trans.addCustomCommand("SEARCHAGRMNT","Psc_Contr_Product_API.Search_For_Agreement");
		     cmd.addParameter("CBHASAGRMNT");
		     cmd.addParameter("CONTRACT_ID");
		     cmd.addParameter("LINE_NO");
		     cmd.addParameter("PART_REV");
		     cmd.addParameter("AGRMNT_PART_NO");
		     cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
		     cmd.addReference("HEAD_PART_NO","GETPARTSER/DATA");
		     cmd.addReference("HEAD_SERIAL_NO","GETPARTSER/DATA");
		     cmd.addParameter("CUSTOMER_NO",headset.getRow().getValue("CUSTOMER_NO"));
		     cmd.addParameter("WORK_TYPE_ID",headset.getRow().getFieldValue("WORK_TYPE_ID"));
		     cmd.addParameter("CONTRACT",headset.getRow().getFieldValue("CONTRACT")); 
		  }
	
		  trans = mgr.perform(trans);
	
		  if ("true".equals(isSecure[2]))
		     cbagreement = trans.getValue("SEARCHAGRMNT/DATA/CBHASAGRMNT");
		  else
		     cbagreement = "";
		  if ("TRUE".equals(cbagreement))
		     return true;
		  else
		     return false;
	       }
	    }            
	}
        return false; 
	// Bug 72214, end
    }

    //--------- T/F Best Fit search
    public void tfbestfit()
    {
        ASPManager mgr = getASPManager();

        current_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",current_url);

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        // 040108  ARWILK  Begin  (Remove uneccessary global variables)
        bOpenNewWindow = true;
        urlString = "../appsrw/BestFitSearchWiz.page";
        newWinHandle = "bestFitWiz"; 
        // 040108  ARWILK  End  (Remove uneccessary global variables)
    }
    //----------------------------

    public void freeTimeSearchFromToolsAndFac()
    {
        ASPManager mgr = getASPManager();

        if (itemlay5.isMultirowLayout())
            itemset5.goTo(itemset5.getRowSelected());

        bOpenNewWindow = true;
        urlString = "FreeTimeSearchEngine.page?RES_TYPE=TOOLS_AND_FACILITY" +
                    "&CONTRACT=" + (mgr.isEmpty(itemset5.getValue("CONTRACT"))?"":itemset5.getValue("CONTRACT")) + 
                    "&ORG_CODE="+ (mgr.isEmpty(itemset5.getValue("ORG_CODE"))?"":itemset5.getValue("ORG_CODE")) + 
                    "&TOOL_FACILITY_TYPE="+ (mgr.isEmpty(itemset5.getValue("TOOL_FACILITY_TYPE"))?"":itemset5.getValue("TOOL_FACILITY_TYPE")) +
                    "&TOOL_FACILITY_ID="+ (mgr.isEmpty(itemset5.getValue("TOOL_FACILITY_ID"))?"":itemset5.getValue("TOOL_FACILITY_ID")) +
                    "&WO_NO="+ (mgr.isEmpty(itemset5.getValue("WO_NO"))?"":itemset5.getValue("WO_NO")) +
                    "&ROW_NO="+ (mgr.isEmpty(itemset5.getValue("ROW_NO"))?"":itemset5.getValue("ROW_NO")) +
                    "&OBJ_ID="+ (mgr.isEmpty(itemset5.getValue("OBJID"))?"":itemset5.getValue("OBJID")) +
                    "&OBJ_VERSION="+ (mgr.isEmpty(itemset5.getValue("OBJVERSION"))?"":itemset5.getValue("OBJVERSION"));
        newWinHandle = "freeTimeSearchFromToolsAndFac"; 
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

        headblk.addField("WO_NO","Number","#").
        setLabel("PCMWACTIVESEPARATE2WONO: WO No").
        setReadOnly().
        setHilite();  

        headblk.addField("CONTRACT").
        setSize(5).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setLabel("PCMWACTIVESEPARATE2CONTRACT: WO Site").
        setCustomValidation("CONTRACT","COMPANY").
        setUpperCase().
        setHilite().  
        setInsertable().
        setReadOnly().
        setMaxLength(7);

        headblk.addField("CONNECTION_TYPE").
        setSize(20).
        setSelectBox().
        setReadOnly().
        setMandatory().
        setCustomValidation("CONNECTION_TYPE","CONNECTION_TYPE_DB").
        setHilite().    
        enumerateValues("MAINT_CONNECTION_TYPE_API").
        setLabel("PCMWACTIVESEPARATE2CONNECTIONTYPE: Connection Type");

        headblk.addField("MCH_CODE").
        setSize(13).
        setCustomValidation("MCH_CODE_CONTRACT,MCH_CODE","MCH_CODE_DESCRIPTION,CRITICALITY,MCH_CODE_CONTRACT,MCH_CODE").
        setLabel("PCMWACTIVESEPARATE2MCHCODE: Object ID").
        setUpperCase().
        setHilite().
        setMaxLength(100);

        headblk.addField("MCH_CODE_DESCRIPTION").
        setSize(20).
        setHilite().   
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2MCHCODEDESC: Description").
        setReadOnly();

        headblk.addField("MCH_CODE_CONTRACT").
        setSize(5).
        setHilite().    
        setLabel("PCMWACTIVESEPARATE2MCHCODECONTRACT: Site").
        setUpperCase().
        setDynamicLOV("USER_ALLOWED_SITE_LOV");

        headblk.addField("LATESTTRANSAC").
        setSize(20).
        setLabel("PCMWACTIVESEPARATE2LATESTTRANSACTION: Latest Transaction").
        setFunction("PART_SERIAL_CATALOG_API.Get_Alt_Latest_Transaction(:MCH_CODE_CONTRACT,:MCH_CODE)").
        setReadOnly().
        setMaxLength(2000);

        headblk.addField("GROUPID").
        setSize(12).
        setDefaultNotVisible().
        setDynamicLOV("EQUIPMENT_OBJ_GROUP",600,450).
        setLabel("PCMWACTIVESEPARATE2GROUPID: Group ID").
        setFunction("EQUIPMENT_OBJECT_API.Get_Group_Id(:MCH_CODE_CONTRACT,:MCH_CODE)").
        setReadOnly(); 

        headblk.addField("CRITICALITY").
        setSize(12).
        setDefaultNotVisible().
        setDynamicLOV("EQUIPMENT_CRITICALITY",600,450).
        setLabel("PCMWACTIVESEPARATE2CRITICALITY: Criticality").
        setFunction("EQUIPMENT_FUNCTIONAL_API.Get_Criticality(:MCH_CODE_CONTRACT,:MCH_CODE)").
        setReadOnly().
        setMaxLength(2000);

        headblk.addField("CRITIDESC").
        setSize(29).
        setDefaultNotVisible().
        setFunction("EQUIPMENT_FUNCTIONAL_API.Get_Criticality_Description(:MCH_CODE_CONTRACT,:MCH_CODE)").
        setLabel("PCMWACTIVESEPARATE2CRITIDESC: Criticality Description").
        setReadOnly().
        setMaxLength(2000);

        headblk.addField("ORG_CODE").
        setSize(12).
        setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,450).
        setMandatory().
        setLabel("PCMWACTIVESEPARATE2ORGCODE: Maintenance Organization").
        setCustomValidation("CONTRACT,ORG_CODE","ORGCODEDESCR").
        setUpperCase().
        setHilite().
        setMaxLength(8);

        headblk.addField("ERR_DESCR").
        setSize(50).
        setMandatory().
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2ERRDESCR: Directive").
        setHilite().
        setMaxLength(60);

        headblk.addField("STATE").
        setSize(20).
        setLOV("ActiveSeparateLov1.page",600,450).
        setLabel("PCMWACTIVESEPARATE2STATE: Status").
        setReadOnly().
        setHilite().
        setMaxLength(30);

        headblk.addField("PLAN_S_DATE","Datetime").
        setSize(30).
        setLabel("PCMWACTIVESEPARATE2PLANSDATE: Planned Start").
        setCustomValidation("WO_NO,PLAN_S_DATE,PLAN_F_DATE","PLAN_F_DATE");

        headblk.addField("DIFF","Number").
        setHidden().
        setFunction("''");

        headblk.addField("PLAN_F_DATE","Datetime").
        setSize(30).
        setLabel("PCMWACTIVESEPARATE2PLANFDATE: Planned Completion").
        setCustomValidation("WO_NO,PLAN_S_DATE,PLAN_F_DATE","PLAN_F_DATE");

        headblk.addField("PLAN_HRS","Number").
        setDefaultNotVisible().
        setCustomValidation("PLAN_HRS,OP_EXIST,WO_NO,PLAN_S_DATE,PLAN_F_DATE,ORG_CODE,CONTRACT","MAN_HOURS,ALLOC_HOURS,PLAN_F_DATE"). //Bug Id 70921
        setLabel("PCMWACTIVESEPARATE2PLANHRS: Execution Time");

        headblk.addField("PLANNED_MAN_HRS","Number").
        setDefaultNotVisible().
        setHidden().
        setLabel("PCMWACTIVESEPARATE2PROJBUDGETHRS: Project Budget Hours");

        headblk.addField("MAN_HOURS","Number").
        setDefaultNotVisible().
        setReadOnly().
        setFunction("WORK_ORDER_ROLE_API.Get_Total_Planned_Hours(:WO_NO)").
        setLabel("PCMWACTIVESEPARATE2PLANMANHOURS: Total Man Hours");

        headblk.addField("ALLOC_HOURS","Number").
        setDefaultNotVisible().
        setReadOnly().
        setFunction("WORK_ORDER_ROLE_API.Get_Total_Allocated_Hours(:WO_NO)").
        setLabel("PCMWACTIVESEPARATE2ALLOCATEDHOURS: Allocated Hours");

        headblk.addField("REMAIN_HOURS","Number").
        setDefaultNotVisible().
        setFunction("WORK_ORDER_ROLE_API.Get_Total_Planned_Hours(:WO_NO) - WORK_ORDER_ROLE_API.Get_Total_Allocated_Hours(:WO_NO)").
        setReadOnly().
        setLabel("PCMWACTIVESEPARATE2REMAININGHOURS: Remaining Hours");

        headblk.addField("CALL_CODE").
        setSize(12).
        setDefaultNotVisible().
        setDynamicLOV("MAINTENANCE_EVENT",600,450).
        setLabel("PCMWACTIVESEPARATE2CALLCODE: Event").
        setUpperCase().
        setMaxLength(10);       

        headblk.addField("OP_STATUS_ID").
        setSize(12).
        setDefaultNotVisible().
        setDynamicLOV("OPERATIONAL_STATUS",600,450).
        setLabel("PCMWACTIVESEPARATE2OPSTATUS: Op Status").
        setUpperCase().
        setMaxLength(3); 

        headblk.addField("PRIORITY_ID").
        setSize(8).
        setDynamicLOV("MAINTENANCE_PRIORITY",600,450).
        setLabel("PCMWACTIVESEPARATE2PRIORITYID: Priority").
        setUpperCase().
        setDefaultNotVisible().
        setMaxLength(1);  

        headblk.addField("WORK_TYPE_ID").
        setSize(12).
        setDynamicLOV("WORK_TYPE",600,450).
        setLabel("PCMWACTIVESEPARATE2WORKTYPEID: Work Type").
        setDefaultNotVisible().
        setUpperCase().
        setMaxLength(20);

        headblk.addField("WORK_DESCR_LO").
        setSize(50).
        setDefaultNotVisible().
        setHeight(4).
        setLabel("PCMWACTIVESEPARATE2WORKDESCRLO: Work Description").
        setMaxLength(2000);

        headblk.addField("VENDOR_NO").
        setSize(12).
        setDynamicLOV("SUPPLIER_INFO",600,450).
        setLabel("PCMWACTIVESEPARATE2VENDORNO: Contractor").
        setUpperCase().
        setDefaultNotVisible().
        setMaxLength(20);

        headblk.addField("VENDORNAME").
        setSize(29).
        setDefaultNotVisible().
        setFunction("Supplier_Info_API.Get_Name(:VENDOR_NO)").
        setReadOnly().
        setLabel("PCMWACTIVESEPARATE2VENDORNAME: Contractor Name").
        setMaxLength(2000);
        mgr.getASPField("VENDOR_NO").setValidation("VENDORNAME");

        headblk.addField("REPORTED_BY").
        setSize(13).
        setDynamicLOV("EMPLOYEE_LOV",600,450).
        setLOVProperty("TITLE","PCMWACTIVESEPARATE2LOVREPORTEDBY: List of Reported by").
        setMandatory().
        setLabel("PCMWACTIVESEPARATE2REPORTEDBY: Reported by").
        setUpperCase().
        setCustomValidation("COMPANY,REPORTED_BY","REPORTED_BY_ID,NAME").
        setReadOnly().
        setInsertable().
        setHilite().
        setMaxLength(20); 

        headblk.addField("PREPARED_BY").
        setSize(12).
        setDynamicLOV("EMPLOYEE_LOV",600,450).
        setLOVProperty("TITLE","PCMWACTIVESEPARATE2LOVTITLE3: List of Prepared by").
        setCustomValidation("PREPARED_BY,COMPANY","PREPARED_BY_ID,PREPAREDBYNAME").
        setLabel("PCMWACTIVESEPARATE2PREPAREDBY: Prepared by").
        setDefaultNotVisible().
        setUpperCase().
        setMaxLength(20);      

        headblk.addField("WORK_MASTER_SIGN").
        setSize(12).
        setDynamicLOV("MAINT_EMPLOYEE",600,450).
        setLOVProperty("TITLE","PCMWACTIVESEPARATE2LOVTITLE4: List of Employees").
        setCustomValidation("WORK_MASTER_SIGN,COMPANY","WORK_MASTER_SIGN_ID,WORKMASBYNAME").
        setLabel("PCMWACTIVESEPARATE2WORKMASTERSIGN: Executed By").
        setDefaultNotVisible().
        setUpperCase().
        setMaxLength(20);

        headblk.addField("WORK_LEADER_SIGN").
        setSize(12).
        setDynamicLOV("EMPLOYEE_LOV",600,450).
        setLOVProperty("TITLE","PCMWACTIVESEPARATE2LOVTITLEWLEADER: List of Work Leader").
        setCustomValidation("WORK_LEADER_SIGN,COMPANY","WORK_LEADER_SIGN_ID,WORKLEADRNAME").
        setLabel("PCMWACTIVESEPARATE2WORKLEADSIGN: Work Leader").
        setDefaultNotVisible().
        setUpperCase().
        setMaxLength(20);

        headblk.addField("CONTRACT_ID").setHidden();

        headblk.addField("LINE_NO").setHidden();

        //Bug 84436, Start 
        f = headblk.addField("AUTHORIZE_CODE");
        f.setSize(12);
        f.setDefaultNotVisible();
        f.setDynamicLOV("ORDER_COORDINATOR_LOV",600,450);
        f.setLabel("PCMWACTIVESEPARATE2AUTHORIZECODE: Coordinator");
        if(mgr.isModuleInstalled("SRVCON"))
            f.setFunction("Sc_Service_Contract_API.Get_Authorize_Code(:CONTRACT_ID)");
        else
           f.setFunction("''");
        f.setCustomValidation("AUTHORIZE_CODE","AUTHOCODENAME");
        f.setUpperCase();
        f.setMaxLength(20);  
        //Bug 84436, End
        
        headblk.addField("COST_CENTER").
        setSize(17).
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2COSTCENTER: Cost Center").
        setFunction("Pre_Accounting_Api.Get_Cost_Center(:PRE_ACCOUNTING_ID)").
        setMaxLength(10);

        headblk.addField("PROJECT_NO").
        setSize(10).
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2PROJECTNO: Project No").
        setFunction("Pre_Accounting_Api.Get_Project_No(:PRE_ACCOUNTING_ID)").
        setMaxLength(10);

        headblk.addField("OBJECT_NO").
        setSize(8).
        setDefaultNotVisible().
        setFunction("Pre_Accounting_Api.Get_Object_No(:PRE_ACCOUNTING_ID)").
        setLabel("PCMWACTIVESEPARATE2OBJECTNO: Object no").
	setMaxLength(10);  

        headblk.addField("REG_DATE","Datetime").
        setSize(22).
        setMandatory().
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2REGDATE: Reg. Date").
        setCustomValidation("REG_DATE","REG_DATE").
        setReadOnly().
        setInsertable(); 

        headblk.addField("TEST_POINT_ID").
        setSize(8).
        setDynamicLOV("EQUIPMENT_OBJECT_TEST_PNT","MCH_CODE_CONTRACT CONTRACT,MCH_CODE MCH_CODE",600,450).
        setLabel("PCMWACTIVESEPARATE2TESTPOINTID: Test Point").
        setUpperCase().
        setDefaultNotVisible().
        setMaxLength(6);        

        headblk.addField("PM_NO","Number","#").
        setLabel("PCMWACTIVESEPARATE2PMNO: PM No").
        setLOV("PmActionLov1.page",600,450).
        setDefaultNotVisible().
        setReadOnly();

        headblk.addField("PM_REVISION").
        setSize(8). 
        setDynamicLOV("PM_ACTION","PM_NO",600,450).
        setLabel("PCMWACTIVESEPARATE2PMREV: PM Revision").
        setDefaultNotVisible().
        setReadOnly().
        setMaxLength(6); 

        headblk.addField("QUOTATION_ID","Number").
        setDefaultNotVisible().
        setDynamicLOV("WORK_ORDER_QUOTATION_LOV",600,450).
        setLabel("PCMWACTIVESEPARATE2QUOTATIONID: Quotation Id").
        setReadOnly(); 

        headblk.addField("SALESMANCODE").
        setSize(   18).
        setDefaultNotVisible().
        setDynamicLOV("PERSON_INFO",600,450).
        setLabel("PCMWACTIVESEPARATE2SALESMANCODE: Salesman Code").
        setFunction("WORK_ORDER_QUOTATION_API.Get_Salesman_Code(:QUOTATION_ID)").
        setReadOnly();

        //Bug 84436, Start
        f = headblk.addField("CUSTOMER_NO");
        f.setSize(   14);
        f.setDefaultNotVisible();
        f.setDynamicLOV("CUSTOMER_INFO",600,450);
        if(mgr.isModuleInstalled("SRVCON"))   
            f.setFunction("Sc_Service_Contract_API.Get_Customer_Id(:CONTRACT_ID)");
        else
           f.setFunction("''");
        f.setLabel("PCMWACTIVESEPARATE2CUSTOMERNO: Customer No");
        f.setUpperCase();
        f.setMaxLength(20);
                
//      Bug 91191, Start
        f = headblk.addField("CUSTODESCR");
        f.setSize(26);
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPARATE2CUSTOMERDESC: Customer Name");
        if(mgr.isModuleInstalled("SRVCON")){   
            f.setFunction("CUSTOMER_INFO_API.Get_Name(Sc_Service_Contract_API.Get_Customer_Id(:CONTRACT_ID))");
        	mgr.getASPField("CUSTOMER_NO").setValidation("CUSTODESCR");
        }
        else
           f.setFunction("''");
        f.setReadOnly();
        f.setMaxLength(2000);
 //       mgr.getASPField("CUSTOMER_NO").setValidation("CUSTODESCR");
        //Bug 84436, End
//      Bug 91191, End
        
        headblk.addField("CONTACT").
        setSize(12).
        setHidden().
        setLabel("PCMWACTIVESEPARATE2CONTACT: Contact").
        setMaxLength(30).
        setReadOnly();

        headblk.addField("REFERENCE_NO").
        setSize(12).
        setHidden().
        setLabel("PCMWACTIVESEPARATE2REFERENCENO: Reference No").
        setMaxLength(25).
        setReadOnly(); 

        headblk.addField("PHONE_NO").
        setSize(12).
        setHidden().
        setLabel("PCMWACTIVESEPARATE2PHONENO: Phone No").
        setMaxLength(20);

        headblk.addField("PM_TYPE").
        setDefaultNotVisible().
        setHidden();

        headblk.addField("ADDRESS1").
        setSize(18).
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2ADDRESS1: Address1").
        setFunction("WORK_ORDER_ADDRESS_API.Get_Address1(:WO_NO)").
        setMaxLength(100).
        setReadOnly().
        setInsertable();

        headblk.addField("ADDRESS2").
        setSize(16).
        setLabel("PCMWACTIVESEPARATE2ADDRESS2: Address2").
        setFunction("WORK_ORDER_ADDRESS_API.Get_Address2(:WO_NO)").
        setMaxLength(35).   
        setDefaultNotVisible().
        setReadOnly().  
        setInsertable();

        headblk.addField("ADDRESS3").
        setSize(16).
        setLabel("PCMWACTIVESEPARATE2ADDRESS3: Address3").
        setFunction("WORK_ORDER_ADDRESS_API.Get_Address3(:WO_NO)").
        setMaxLength(35).   
        setDefaultNotVisible().
        setReadOnly().   
        setInsertable();

        headblk.addField("ADDRESS4").
        setSize(16).
        setLabel("PCMWACTIVESEPARATE2ADRESS4: Address4").
        setFunction("WORK_ORDER_ADDRESS_API.Get_Address4(:WO_NO)").
        setMaxLength(35).
        setDefaultNotVisible().
        setReadOnly();   

        headblk.addField("ADDRESS5").
        setSize(16).
        setLabel("PCMWACTIVESEPARATE2ADDRESS5: Address5").
        setFunction("WORK_ORDER_ADDRESS_API.Get_Address5(:WO_NO)").
        setMaxLength(35).
        setDefaultNotVisible().
        setReadOnly();   

        headblk.addField("ADDRESS6").
        setSize(16).
        setLabel("PCMWACTIVESEPARATE2ADDRESS6: Address6").
        setFunction("WORK_ORDER_ADDRESS_API.Get_Address6(:WO_NO)").
        setMaxLength(35).
        setDefaultNotVisible().
        setReadOnly();

        headblk.addField("LU_NAME").
        setHidden().
        setDefaultNotVisible().
        setFunction("'ActiveSeparate'");

        headblk.addField("KEY_REF").
        setHidden().
        setDefaultNotVisible().
        setFunction("CONCAT('WO_NO=',CONCAT(WO_NO,'^'))");   

        f = headblk.addField("DOCUMENT");
        if (mgr.isModuleInstalled("DOCMAN"))
            f.setFunction("substr(Doc_Reference_Object_API.Exist_Obj_Reference('ActiveSeparate',CONCAT('WO_NO=',CONCAT(WO_NO,'^'))),1, 5)");
        else
            f.setFunction("''");
        f.setUpperCase();
        f.setLabel("PCMWACTIVESEPARATE2DOCUMENT: Has Documents");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setCheckBox("FALSE,TRUE");
        f.setSize(18);

        headblk.addField("NAME").
        setSize(30).
        setFunction("PERSON_INFO_API.Get_Name(:REPORTED_BY)").
        setLabel("PCMWACTIVESEPARATE2REPORTEDBYNAME: Reported by Name").
        setReadOnly().
        setDefaultNotVisible().
        setHilite().
        setMaxLength(2000);

	//Bug 87766, Start, Modified the label and function
	headblk.addField("CBISCONNECTED").
        setLabel("PCMWACTIVESEPARATE2CBISCONNECTED: In a Structure").
        setFunction("substr(WORK_ORDER_CONNECTION_API.Has_Connection_Up(:WO_NO),1,5)").
        setReadOnly().
        setDefaultNotVisible().
        setCheckBox("FALSE,TRUE");
	//Bug 87766, End

        headblk.addField("CBWARRNOBJECT").
        setLabel("PCMWACTIVESEPARATE2CBWARRNOBJECT: Supplier Warranty").
        setFunction("''").
        setDefaultNotVisible().
        setReadOnly().
        setCheckBox("FALSE,TRUE");

	//Bug 87766, Start, Modified the label and function
        headblk.addField("CBHASSTRUCTU").
        setLabel("PCMWACTIVESEPARATE2CBHASSTRUCTU: Has Structure").
        setFunction("substr(WORK_ORDER_CONNECTION_API.Has_Connection_Down(:WO_NO),1,5)").
        setDefaultNotVisible().
        setReadOnly().
        setCheckBox("FALSE,TRUE");
	//Bug 87766, End

        headblk.addField("REPAIR_FLAG").
        setLabel("PCMWACTIVESEPARATE2REPAIR_FLAG: Repair Work Order").
        setReadOnly().
        setDefaultNotVisible().
        setCheckBox("FALSE,TRUE");

        //Bug 69841, Start
        headblk.addField("FIXED_PRICE_DB").
        setLabel("PCMWACTIVESEPARATE2FIXEDPRICE: Fixed Price").
        setHidden().
        setCheckBox("1,2"); 
        //Bug 69841, End   

        f = headblk.addField("CBHASDOCNTS");
        if (mgr.isModuleInstalled("DOCMAN"))
            f.setFunction("substr(Doc_Reference_Object_API.Exist_Obj_Reference('ActiveSeparate',CONCAT('WO_NO=',CONCAT(WO_NO,'^'))),1, 5)");
        else
            f.setFunction("''");
        f.setLabel("PCMWACTIVESEPARATE2CBHASDOCNTS: Documents");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setCheckBox("FALSE,TRUE");  

        headblk.addField("CBHASAGRMNT").
        setLabel("PCMWACTIVESEPARATE2CBHASSERCONTRACT: Has Service Contract").
        setFunction("Active_Work_Order_APi.Has_Service_Contract(:WO_NO)").
        setDefaultNotVisible().
        setReadOnly().
        setCheckBox("FALSE,TRUE");

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
        f.setLabel("PCMWACTIVESEPARATE2CBTRANSTOMOB: Transferred To Mobile");
        f.setCheckBox("FALSE,TRUE");
        f.setReadOnly();
        f.setDefaultNotVisible();
        //Bug 77304, end

        headblk.addField("ORGCODEDESCR").
        setSize(29).
        setFunction("Organization_API.Get_Description(:CONTRACT,:ORG_CODE)").
        setLabel("PCMWACTIVESEPARATE2ORGCODEDESCR: Maintenance Organization Desc.").
        setHilite().
        setReadOnly().
        setDefaultNotVisible().
        setMaxLength(2000);

        headblk.addField("PRIORITYDESC").
        setSize(33).
        setFunction("Maintenance_Priority_API.Get_Description(:PRIORITY_ID)").
        setLabel("PCMWACTIVESEPARATE2PRIORITYDESC: Priority Description").
        setReadOnly().
        setDefaultNotVisible().
        setMaxLength(2000);
        mgr.getASPField("PRIORITY_ID").setValidation("PRIORITYDESC");

        headblk.addField("WORKTYPEDESC").
        setSize(29).
        setFunction("WORK_TYPE_API.Get_Description(:WORK_TYPE_ID)").
        setLabel("PCMWACTIVESEPARATE2WORKTYPEDESC: Work Type Desc.").
        setReadOnly().
        setDefaultNotVisible().
        setMaxLength(2000);
        mgr.getASPField("WORK_TYPE_ID").setValidation("WORKTYPEDESC");

        headblk.addField("WORKLEADRNAME").
        setSize(29).
        setFunction("PERSON_INFO_API.Get_Name(:WORK_LEADER_SIGN)").
        setLabel("PCMWACTIVESEPARATE2WORKLEADRNAME: Work Leader Name").
        setReadOnly().
        setDefaultNotVisible().
        setMaxLength(2000);

        headblk.addField("OPSTATUSIDDES").
        setSize(29).
        setFunction("OPERATIONAL_STATUS_API.Get_Description(:OP_STATUS_ID)").
        setLabel("PCMWACTIVESEPARATE2OPSTATUSIDDES: Op Status Desc.").
        setReadOnly().
        setDefaultNotVisible().
        setMaxLength(2000);
        mgr.getASPField("OP_STATUS_ID").setValidation("OPSTATUSIDDES"); 

        headblk.addField("REQUIRED_START_DATE","Datetime").
        setLabel("PCMWACTIVESEPARATE2REQUIREDSTARTDATE: Required Start").
        setCustomValidation("REQUIRED_START_DATE","REQUIRED_START_DATE").
        setDefaultNotVisible().
        setSize(30);

        headblk.addField("REQUIRED_END_DATE","Datetime").
        setLabel("PCMWACTIVESEPARATE2REQUIREDENDDATE: Required Completion").
        setCustomValidation("REQUIRED_END_DATE","REQUIRED_END_DATE").
        setDefaultNotVisible().
        setSize(30); 

        headblk.addField("CALLCODEDESC").
        setSize(29).
        setFunction("MAINTENANCE_EVENT_API.Get_Description(:CALL_CODE)").
        setLabel("PCMWACTIVESEPARATE2CALLCODEDESC: Event Description").
        setReadOnly().
        setDefaultNotVisible().
        setMaxLength(2000);
        mgr.getASPField("CALL_CODE").setValidation("CALLCODEDESC"); 

        headblk.addField("PREPAREDBYNAME").
        setSize(29).
        setFunction("PERSON_INFO_API.Get_Name(:PREPARED_BY)").
        setLabel("PCMWACTIVESEPARATE2PREPAREDBYNAME: Prepared by Name").
        setReadOnly().
        setDefaultNotVisible().
        setMaxLength(2000);

        headblk.addField("WORKMASBYNAME").
        setSize(29).
        setFunction("PERSON_INFO_API.Get_Name(:WORK_MASTER_SIGN)").
        setLabel("PCMWACTIVESEPARATE2WORKMASBYNAME: Executed By Name").
        setReadOnly().
        setDefaultNotVisible().
        setMaxLength(2000);

        headblk.addField("AUTHOCODENAME").
        setSize(29).
        setFunction("PERSON_INFO_API.Get_Name(:AUTHORIZE_CODE)").
        setLabel("PCMWACTIVESEPARATE2AUTHOCODENAME: Coordinator Name").
        setReadOnly().
        setDefaultNotVisible().
        setMaxLength(2000);

        //--------------------------------------------------------------------    

        headblk.addField("COMPANY").
        setSize(6).
        setHidden().
        setUpperCase().
        setMaxLength(20);

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

        //----------------------------------------------------------------------

        headblk.addField("REPORTED_BY_ID").
        setSize(6).
        setHidden().
        setUpperCase();

        headblk.addField("WORK_LEADER_SIGN_ID").
        setSize(6).
        setHidden().
        setUpperCase(); 

        f = headblk.addField("AGREEMENT_ID");
        f.setSize(12);
        if (mgr.isModuleInstalled("ORDER"))
            f.setDynamicLOV("CUSTOMER_AGREEMENT",600,450);
        f.setLabel("PCMWACTIVESEPARATE2AGREEMENTID: Agreement Id");
        f.setDefaultNotVisible();
        f.setUpperCase();
        f.setHidden();
        f.setMaxLength(10); 

        headblk.addField("WO_KEY_VALUE").
        setUpperCase().
        setHidden().
        setFunction("WO_KEY_VALUE");

        headblk.addField("STD_TEXT").
        setFunction("''").    
        setHidden(); 

        headblk.addField("HAS_MATERIAL").
        setFunction("''").
        setHidden();

        headblk.addField("HAS_ROLE").
        setFunction("''").
        setHidden();

        headblk.addField("CBHASDOCS").
        setFunction("''").
        setHidden();  

        headblk.addField("ISVIM").
        setHidden().
        setUpperCase().
        setFunction("''").
        setMaxLength(11);

        headblk.addField("PREPARED_BY_ID").
        setHidden().
        setUpperCase().
        setMaxLength(11);

        headblk.addField("WORK_MASTER_SIGN_ID").
        setHidden().
        setUpperCase().
        setMaxLength(11);

        headblk.addField("ERR_DISCOVER_CODE").
        setSize(6).
        setDynamicLOV("WORK_ORDER_DISC_CODE",600,450).
        setLabel("PCMWACTIVESEPARATE2ERRDISCODE: Discovery").
        setUpperCase().
        setDefaultNotVisible().
        setMaxLength(3);  

        headblk.addField("DISCODESCR").
        setSize(28).
        setFunction("WORK_ORDER_DISC_CODE_API.Get_Description(:ERR_DISCOVER_CODE)").
        setLabel("PCMWACTIVESEPARATE2ERRDISCODESC: Discovery Description").
        setReadOnly().
        setDefaultNotVisible().
        setMaxLength(2000);
        mgr.getASPField("ERR_DISCOVER_CODE").setValidation("DISCODESCR");

        headblk.addField("ERR_SYMPTOM").
        setSize(13).
        setDynamicLOV("WORK_ORDER_SYMPT_CODE",600,450).
        setLabel("PCMWACTIVESEPARATE2ERRSYMPTM: Symptom").
        setUpperCase().
        setDefaultNotVisible().
        setMaxLength(3);

        headblk.addField("SYMPTOMDESCR").
        setSize(30).
        setFunction("WORK_ORDER_SYMPT_CODE_API.Get_Description(:ERR_SYMPTOM)").
        setReadOnly().
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2SYMTDISCODESC: Symptom Description").
        setMaxLength(2000);
        mgr.getASPField("ERR_SYMPTOM").setValidation("SYMPTOMDESCR");  

        headblk.addField("ERR_DESCR_LO").
        setSize(43).
        setHeight(4).
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2ERRDESCRLO: Description").
        setMaxLength(2000);   

        headblk.addField("TESTPOINTIDDES").
        setSize(35).
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2TESTPOINTIDDES: Test Point Desc.").
        setReadOnly().
        setFunction("EQUIPMENT_OBJECT_TEST_PNT_api.get_description(:MCH_CODE_CONTRACT,:MCH_CODE,:TEST_POINT_ID)");

        headblk.addField("PM_DESCR").
        setSize(36).
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2PMDESCR: Generation Info").
        setReadOnly().
        setMaxLength(2000);    

        headblk.addField("HEAD_TEMP").
        setHidden().
        setFunction("''");

        f = headblk.addField("PRE_ACCOUNTING_ID","Number");
        f.setHidden();

        headblk.addField("ACTION_CODE_ID").
        setSize(30).
        setDynamicLOV("MAINTENANCE_ACTION",600,450).
        setLabel("PCMWACTIVESEPARATE2ACTIONCODEID: Action").
        setUpperCase().
        setDefaultNotVisible().
        setReadOnly().
        setMaxLength(10);

        headblk.addField("FAULT_REP_FLAG","Number").
        setHidden();

        headblk.addField("NORGANIZATIONORG_COST","Number").
        setLabel("PCMWACTIVESEPARATE2NORGANIZATNCOST: Org Cost").
        setFunction("ORGANIZATION_API.Get_Org_Cost(:CONTRACT,:ORG_CODE)").
        setHidden();

        headblk.addField("NOTE").
        setLabel("PCMWACTIVESEPARATE2NOTE: Inspection Note").
        setDefaultNotVisible().
        setReadOnly().
        setSize(36);

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

        headblk.addField("ATTR").
        setHidden().
        setFunction("''");      

        headblk.addField("ACTION").
        setHidden().
        setFunction("''");

        f = headblk.addField("DUMMY_RESULT");
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
	// Bug 72214, end

        //-------------------------------------------------------------------------------------
        //--------------------------------------------------------------------------------------

        headblk.addField("OBJ_SUP_WARRANTY","Number").
        setCustomValidation("OBJ_SUP_WARRANTY,MCH_CODE,MCH_CODE_CONTRACT","SUP_WARRANTY").
        setHidden();   

        headblk.addField("SUP_WARR_DESC").
        setSize(50).
        setDefaultNotVisible().
        setLabel("PCMWACTIVESEPARATE2SUP_WARR_DESC: Warranty Desc.").
        setReadOnly().
        setFunction("Sup_Warranty_Type_api.Get_Warranty_Description(:SUP_WARRANTY,:SUP_WARR_TYPE)").
        setMaxLength(20);     

        headblk.addField("SUPPLIER").
        setSize(20).
        setFunction("OBJECT_SUPPLIER_WARRANTY_api.Get_Vendor_No(:MCH_CODE_CONTRACT,:MCH_CODE,:OBJ_SUP_WARRANTY)").
        setLabel("PCMWACTIVESEPARATE2SUPWARR: Supplier").
        setDefaultNotVisible().
        setReadOnly().
        setUpperCase();    

        headblk.addField("SUP_WARRANTY").
        setHidden();

        headblk.addField("SUP_WARR_TYPE").
        setSize(20).
        setDefaultNotVisible().
        setReadOnly().
        setLabel("PCMWACTIVESEPARATE2OBJSUPWARR: Warranty Type").
        setMaxLength(20); 

        headblk.addField("SUPP_DESCR").
        setDefaultNotVisible().
        setSize(50).
        setFunction("Maintenance_Supplier_API.Get_Description(OBJECT_SUPPLIER_WARRANTY_api.Get_Vendor_No(:MCH_CODE_CONTRACT,:MCH_CODE,:OBJ_SUP_WARRANTY))").
        setReadOnly(). 
        setLabel("PCMWACTIVESEPARATE2SUPP_DESCR: Supplier Name").
        setMaxLength(20);

        headblk.addField("TRANSFERRED").
        setFunction("''").
        setHidden();

        headblk.addField("OBSTATE").
        setFunction("''").
        setHidden();

        headblk.addField("UPDATE_PRICE").
        setFunction("''").
        setHidden();

        headblk.addField("CONNECTION_TYPE_DB").
        setHidden();

        headblk.addField("HEAD_ISVALID").
        setFunction("''").
        setHidden();

        headblk.addField("HEAD_FACSEQUENCE").
        setFunction("''").
        setHidden();

        headblk.addField("PART_REV").
        setFunction("''").
        setHidden();

        headblk.addField("AGRMNT_PART_NO").
        setFunction("''").
        setHidden();

        headblk.addField("HEAD_PART_NO").
        setFunction("EQUIPMENT_SERIAL_API.Get_Part_No(:CONTRACT, :MCH_CODE)").
        setHidden();

        headblk.addField("HEAD_SERIAL_NO").
        setFunction("EQUIPMENT_SERIAL_API.Get_Serial_No(:CONTRACT, :MCH_CODE)").
        setHidden();

        headblk.addField("CONDITION_CODE_USAGE").
        setFunction("PART_CATALOG_API.Get_Condition_Code_Usage_Db(EQUIPMENT_SERIAL_API.Get_Part_No(:CONTRACT, :MCH_CODE))").
        setHidden();

        f = headblk.addField("CBPROJCONNECTED");
        f.setFunction("Active_Separate_API.Is_Project_Connected(:WO_NO)");
        f.setLabel("PCMWACTIVESEPARATEPROJCONN: Project Connected");
        f.setCheckBox("FALSE,TRUE");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setSize(5);

        f = headblk.addField("ACTIVITY_SEQ","Number");
        f.setLabel("ACTSEP2HEADACTSEQ: Activity Seq");
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

        f = headblk.addField("OP_EXIST"); 
        f.setHidden();  
        f.setFunction("Active_Work_Order_API.Has_Role(:WO_NO)");

        // Bug 72214, start
	f = headblk.addField("SCHEDULING_CONSTRAINTS");
	f.setLabel("PCMWACTIVESEPARATE2SCHEDCONST: Scheduling Constraints");
	f.setSize(35);
	f.setReadOnly();
	 
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

        headset = headblk.getASPRowSet();

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWACTIVESEPARATE2HD: Prepare Work Order"));
        headtbl.setWrap();
        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
        headtbl.enableRowSelect();
        // 040108  ARWILK  End  (Enable Multirow RMB actions)

        headbar = mgr.newASPCommandBar(headblk);

        headbar.defineCommand(headbar.DUPLICATEROW,"duplicateRow");
        // 031204  ARWILK  Begin  (Replace links with RMB's)
        headbar.addCustomCommand("reportInWorkOrder",mgr.translate("PCMWACTIVESEPARATE2REPINWO: Report In..."));
        // 031204  ARWILK  End  (Replace links with RMB's)
        headbar.addCustomCommand("structure",mgr.translate("PCMWACTIVESEPARATE2STRUCTURE: Structure..."));
        headbar.addCustomCommand("prePost",mgr.translate("PCMWACTIVESEPARATE2PREPOSTING: Preposting..."));
        headbar.addCustomCommandSeparator();
        // 031204  ARWILK  Begin  (Replace links with RMB's)
        headbar.addCustomCommand("copy",mgr.translate("PCMWACTIVESEPARATE2COPY: Copy..."));
        headbar.addCustomCommand("printWO",mgr.translate("PCMWACTIVESEPARATE2PRINT: Print..."));
        headbar.addCustomCommand("turnToRepOrd",mgr.translate("PCMWACTIVESEPARATE2TURNREPORD: Turn into Repair Order...")); 
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("freeTimeSearch",mgr.translate("PCMWACTIVESEPARATE2SMFREETIMESEARCH1: Free Time Search..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("reinit",mgr.translate("PCMWACTIVESEPARATE2FAULTWORK: FaultReprt\\WorkRequest"));
        headbar.addCustomCommand("observed",mgr.translate("PCMWACTIVESEPARATE2OBSERVED: Observed"));
        headbar.addCustomCommand("underPrep",mgr.translate("PCMWACTIVESEPARATE2UNDPREP: Under Preparation"));
        headbar.addCustomCommand("prepared",mgr.translate("PCMWACTIVESEPARATE2PREPARED: Prepared"));
        headbar.addCustomCommand("released",mgr.translate("PCMWACTIVESEPARATE2RELEASED: Released"));
        headbar.addCustomCommand("started",mgr.translate("PCMWACTIVESEPARATE2STARTED: Started"));
        headbar.addCustomCommand("workDone",mgr.translate("PCMWACTIVESEPARATE2WRKDONE: Work Done"));
        headbar.addCustomCommand("reported",mgr.translate("PCMWACTIVESEPARATE2REPORTED: Reported"));
        headbar.addCustomCommand("finished",mgr.translate("PCMWACTIVESEPARATE2FINISHED: Finished"));
        headbar.addCustomCommand("cancelled",mgr.translate("PCMWACTIVESEPARATE2CANCELLED: Cancelled"));
        if (mgr.isModuleInstalled("PROJ"))
        {
            headbar.addCustomCommand("connectToActivity","ACTSEP2PROJCONNCONNACT: Connect to Project Activity...");
            headbar.addCustomCommand("disconnectFromActivity","ACTSEP2PROJCONNDISCONNACT: Disconnect from Project Activity");
            headbar.addCustomCommand("projectActivityInfo", mgr.translate("ACTSEP2PRJACTINFO: Project Connection Details..."));
            headbar.addCustomCommand("activityInfo", mgr.translate("ACTSEP2PRJACTVITYINFO: Activity Info..."));
        }
        headbar.addCustomCommand("changeConditionCode",mgr.translate("PCMWACTIVESEPARATE2CHANGECONDITION: Change of Condition Code..."));
        headbar.addCustomCommand("pickListForWork",mgr.translate("PCMWACTIVESEPARATE2PICKLISTFOERWORK: Pick List For Work Order - Printout..."));
        
        //Bug 83532, Start
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("workorderaddr", mgr.translate("PCMWACTIVESEPARATE2WORKORDERADDR: Work Order Address..."));
        headbar.addCustomCommandSeparator();
        //Bug 83532, End
        
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("suppWarr",mgr.translate("PCMWACTIVESEPARATE2SUPPWARR: Supplier Warranty Type..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("budget",mgr.translate("PCMWACTIVESEPARATE2BUDGET: Budget..."));
        headbar.addCustomCommand("freeNotes",mgr.translate("PCMWACTIVESEPARATE2FREENOTES: Free Notes..."));
        headbar.addCustomCommand("planning",mgr.translate("PCMWACTIVESEPARATE2PLANNING: Planning..."));
        headbar.addCustomCommand("requisitions",mgr.translate("PCMWACTIVESEPARATE2REQUISITIONS: Requisitions..."));
        headbar.addCustomCommand("returns",mgr.translate("PCMWACTIVESEPARATE2RETURNS: Returns...")); 
        headbar.addCustomCommand("permits",mgr.translate("PCMWACTIVESEPARATE2PERMITS: Permits..."));
        headbar.addCustomCommand("coInformation",mgr.translate("PCMWACTIVESEPARATE2COINFORNOSMION: CO Information..."));
        headbar.addCustomCommand("maintTask",mgr.translate("PCMWACTIVESEPARATE2MAINTTASK: Maint Task...")); 
        // 031204  ARWILK  End  (Replace links with RMB's)
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("workCenterLoad",mgr.translate("PCMWACTIVESEPARATE2WORKCENTLOAD: Work Center Load..."));
        headbar.addCustomCommand("repInWiz",mgr.translate("PCMWACTIVESEPARATE2REPORTINWIZ: Report in Wizard..."));
        headbar.addCustomCommand("resheduleOperations3",mgr.translate("PCMWACTIVESEPARATE2RESHEDULEOPERATIONSHEAD: Reschedule Operations..."));
        //Bug 72644, Start   
        headbar.addCustomCommand("optimizeSchedule",mgr.translate("PCMWACTIVESEPARATE2OPTSCH: Optimize Planning Schedule..."));
        headbar.addCustomCommand("refresh",mgr.translate("PCMWACTIVESEPARATE2REFRESH: Refresh"));
        //Bug 72644, End
        // Bug 72214, start
	if (mgr.isModuleInstalled("CBS"))
	   headbar.addSecureCustomCommand("resheduleWoStructProj",mgr.translate("PCMWACTIVESEPARATE2RESHEDULEWOSTRUCTPROJ: Reschedule Work Order Structure/Project..."),"Scheduling_Site_Config_API.Check_Maint_Contract_Web");
        // Bug 72214, end

        // 031204  ARWILK  Begin  (Replace links with RMB's)
        headbar.addCustomCommandGroup("WOSTATUS", mgr.translate("PCMWACTIVESEPARATE2WORKORDERSTUS: Work Order Status"));
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
            headbar.addCustomCommandGroup("PROJCONNGRP", mgr.translate("ACTSEP2PROJCONNGRP: Project Connection"));
            headbar.setCustomCommandGroup("connectToActivity", "PROJCONNGRP");
            headbar.setCustomCommandGroup("disconnectFromActivity", "PROJCONNGRP");
            headbar.setCustomCommandGroup("projectActivityInfo", "PROJCONNGRP");
            headbar.setCustomCommandGroup("activityInfo", "PROJCONNGRP");
        }

        // 031204  ARWILK  Begin  (Replace blocks with tabs)
        headbar.addCustomCommand("activateOperations", "");
        headbar.addCustomCommand("activateMaterial", "");
        headbar.addCustomCommand("activateToolsAndFacilities", "");
        headbar.addCustomCommand("activateJobs", "");
        // 031204  ARWILK  End  (Replace blocks with tabs)

        headbar.defineCommand(headbar.SAVERETURN,"saveReturn");
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
        // Bug 72214, start
        // Bug 76867, start
	if (mgr.isModuleInstalled("CBS")){
            headbar.addCommandValidConditions("resheduleWoStructProj","OBJSTATE","Enable","PREPARED;RELEASED;STARTED");      
            headbar.appendCommandValidConditions("resheduleWoStructProj","ENABLE_RESCHEDULE", "Disable", "FALSE");
        }
        // Bug 76867, end
        // Bug 72214, end


        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
        //headbar.addCommandValidConditions("changeConditionCode",    "CONDITION_CODE_USAGE",    "Enable",   "ALLOW_COND_CODE");

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
        headbar.removeFromMultirowAction("changeConditionCode");
        headbar.removeFromMultirowAction("suppWarr");
        headbar.removeFromMultirowAction("budget");
        headbar.removeFromMultirowAction("freeNotes");
        headbar.removeFromMultirowAction("planning");
        headbar.removeFromMultirowAction("requisitions");
        headbar.removeFromMultirowAction("returns"); 
        headbar.removeFromMultirowAction("permits");
        headbar.removeFromMultirowAction("coInformation");
        headbar.removeFromMultirowAction("maintTask"); 
        headbar.removeFromMultirowAction("workCenterLoad");
        headbar.removeFromMultirowAction("repInWiz");
        headbar.removeFromMultirowAction("freeTimeSearch");
//		Bug 83532, Start
        headbar.removeFromMultirowAction("workorderaddr");
//		Bug 83532, End
        // Bug 72214, start
	if (mgr.isModuleInstalled("CBS"))
	   headbar.removeFromMultirowAction("resheduleWoStructProj");
        // Bug 72214, end

        // The RMB cancelled is allowed only in single row selection because 
        // the state change requires a data entry as a prerequisite. Check the method cancelled.

        headbar.removeFromMultirowAction("cancelled");
        // 040108  ARWILK  End  (Enable Multirow RMB actions)

        headbar.addCustomCommand("refreshForm","");
        headbar.addCustomCommand("refreshMaterialTab","");

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
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE2GRPLABEL1: General"),"WO_NO,CONTRACT,ERR_DESCR,REPORTED_BY,NAME,STATE,CONNECTION_TYPE,MCH_CODE,MCH_CODE_DESCRIPTION,MCH_CODE_CONTRACT,ORG_CODE,ORGCODEDESCR,CRITICALITY,CRITIDESC,LATESTTRANSAC",true,true);
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE2GRPLABEL7: Work Order"),"CBWARRNOBJECT,REPAIR_FLAG,CBHASDOCNTS,CBHASAGRMNT,CBHASSTRUCTU,CBPROJCONNECTED,CBISCONNECTED,CBHASOBSOLETEJOBS,CBTRANSTOMOB",true,true);
        // 031008  ARWILK  End (GUI Modifications)
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE2GRPLABEL2: Prepare Info"),"WORK_DESCR_LO,OP_STATUS_ID,OPSTATUSIDDES,CALL_CODE,CALLCODEDESC,VENDOR_NO,VENDORNAME,WORK_LEADER_SIGN,WORKLEADRNAME,WORK_MASTER_SIGN,WORKMASBYNAME,PREPARED_BY,PREPAREDBYNAME,WORK_TYPE_ID,WORKTYPEDESC,PRIORITY_ID,PRIORITYDESC",true,false);
        // (Bug 72214, start
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE2GRPLABEL3: Planning Schedule"),"PLAN_S_DATE,REQUIRED_START_DATE,PLAN_F_DATE,REQUIRED_END_DATE,PLAN_HRS,PLANNED_MAN_HRS,SCHEDULING_CONSTRAINTS",true,false);
	// Bug 72214, end
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE2GRPLABEL8: Operation Hours"),"MAN_HOURS,ALLOC_HOURS,REMAIN_HOURS",true,false);
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE2GRPLABEL4: Fault Report Info"),"ERR_DISCOVER_CODE,DISCODESCR,ERR_SYMPTOM,SYMPTOMDESCR,ERR_DESCR_LO,REG_DATE",true,false);
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE2GRPLABEL5: PM Information"),"PM_NO,PM_REVISION,NOTE,TEST_POINT_ID,TESTPOINTIDDES,PM_DESCR",true,false);
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATE2GRPLABEL6: Supplier Warranty Info"),"SUP_WARR_TYPE,SUP_WARR_DESC,SUPPLIER,SUPP_DESCR",true,false);
        headlay.setDialogColumns(2);   

        headlay.setSimple("MCH_CODE_DESCRIPTION");
        headlay.setSimple("CRITIDESC");
        headlay.setSimple("NAME");
        headlay.setSimple("ORGCODEDESCR");
        headlay.setSimple("PRIORITYDESC");
        headlay.setSimple("VENDORNAME");
        headlay.setSimple("WORKTYPEDESC");
        headlay.setSimple("WORKLEADRNAME");
        headlay.setSimple("OPSTATUSIDDES");
        headlay.setSimple("CALLCODEDESC");
        headlay.setSimple("PREPAREDBYNAME"); 
        headlay.setSimple("WORKMASBYNAME");
        headlay.setSimple("AUTHOCODENAME"); 
        headlay.setSimple("DISCODESCR");
        headlay.setSimple("SYMPTOMDESCR");
        headlay.setSimple("TESTPOINTIDDES");
        headlay.setSimple("VENDORNAME");
        headlay.setSimple("SUPP_DESCR");
        headlay.setSimple("SUP_WARR_DESC");

        // 031204  ARWILK  Begin  (Replace blocks with tabs)
        tabs = mgr.newASPTabContainer();
        tabs.addTab(mgr.translate("PCMWACTIVESEPARATE2OPERTAB: Operations"), "javascript:commandSet('HEAD.activateOperations','')");
        tabs.addTab(mgr.translate("PCMWACTIVESEPARATE2MATERIALTAB: Materials"), "javascript:commandSet('HEAD.activateMaterial','')");
        tabs.addTab(mgr.translate("PCMWACTIVESEPARATE2TOOLFACTAB: Tools and Facilities"), "javascript:commandSet('HEAD.activateToolsAndFacilities','')");
        tabs.addTab(mgr.translate("PCMWACTIVESEPARATE2JOBSTAB: Jobs"), "javascript:commandSet('HEAD.activateJobs','')");

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
        setLabel("PCMWACTIVESEPARATE2ALLOCATIONNO: Allocation No");

        itemblk2.addField("ITEM2_WO_NO","Number","#").
        setMandatory().
        setHidden().
        setLabel("PCMWACTIVESEPARATE2ITEM2WONO: WO No").
        setDbName("WO_NO");

        itemblk2.addField("ROW_NO","Number").
        setLabel("PCMWACTIVESEPARATE2ROWNO: Operation No").
        setReadOnly().
        setInsertable();

        itemblk2.addField("ITEM2_COMPANY").
        setSize(18).
        setHidden().
        setLabel("PCMWACTIVESEPARATE2ITEM2COMPANY: Company").
        setUpperCase().
        setDbName("COMPANY").
        setMaxLength(20);

        itemblk2.addField("TEMP").
        setHidden().
        setFunction("''"); 

        itemblk2.addField("ITEM2_DESCRIPTION").
        setSize(29).
        setLabel("PCMWACTIVESEPARATE2ITEM2DESCRIPTION: Description").
        setDbName("DESCRIPTION").
        setMaxLength(60);

        itemblk2.addField("SIGN").
        setSize(20).
        setLOV("../mscomw/MaintEmployeeLov.page","ITEM2_COMPANY COMPANY",600,450).
        setLOVProperty("TITLE","PCMWACTIVESEPARATE2LOVTITLEEMP2: List of Employee").
        setCustomValidation("ITEM2_COMPANY,SIGN,ROLE_CODE,ITEM2_CONTRACT,ITEM2_ORG_CODE,ORG_CODE,COST,PLAN_MEN,ITEM2_PLAN_HRS,ALLOCATED_HOURS,TOTAL_MAN_HOURS","SIGN_ID,ROLE_CODE,ITEM2_ORG_CODE,COST,COSTAMOUNT,SIGN,ITEM2_CONTRACT,ALLOCATED_HOURS,TOTAL_ALLOCATED_HOURS,TOTAL_REMAINING_HOURS,ITEM2_ORG_CODE_DESC,ROLE_CODE_DESC").        
        setLOVProperty("TITLE","PCMWACTIVESEPARATE2LOVTITLE5: List of Employee").
        setLabel("PCMWACTIVESEPARATE2SIGN: Executed By").
        setUpperCase().
        setMaxLength(20);

        itemblk2.addField("SIGN_ID").
        setSize(18).
        setMaxLength(11).
        setLabel("PCMWACTIVESEPARATE2SIGNID: Employee ID").
        setCustomValidation("SIGN_ID,ITEM2_COMPANY,ROLE_CODE","SIGN,ROLE_CODE,ITEM2_ORG_CODE").
        setUpperCase().
        setReadOnly();

        itemblk2.addField("ITEM2_ORG_CODE").
        setSize(8).
        setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ITEM2_CONTRACT CONTRACT",600,450).
        setCustomValidation("ITEM2_CONTRACT,ITEM2_ORG_CODE,ROLE_CODE,PLAN_MEN,ITEM2_PLAN_HRS,SIGN,ITEM2_WO_NO","COST,COSTAMOUNT,ITEM2_ORG_CODE,ITEM2_CONTRACT,ITEM2_ORG_CODE_DESC").
        setLabel("PCMWACTIVESEPARATE2ITEM2ORGCODE: Maint.Org.").
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
        setLabel("PCMWACTIVESEPARATE2ITEM2CONTRACT: Maint.Org. Site").
        setUpperCase().
        setDbName("CONTRACT").
        setMaxLength(5);

//Bug ID 59224,start

        itemblk2.addField("ROLE_CODE").
        setSize(10).
        setDynamicLOV("ROLE_TO_SITE_LOV","ITEM2_CONTRACT CONTRACT",600,445).
        setCustomValidation("ROLE_CODE,ITEM2_CONTRACT,ITEM2_ORG_CODE,PLAN_MEN,ITEM2_PLAN_HRS,ITEM2_WO_NO","COST,COSTAMOUNT,ROLE_CODE,ITEM2_CONTRACT,ROLE_CODE_DESC").
        setLabel("PCMWACTIVESEPARATE2ROLECODE: Craft ID").
        setUpperCase().
        setMaxLength(10);

         itemblk2.addField("ROLE_CODE_DESC").
       setLabel("PCMWACTIVESEPARATE2ROLECODEDESC: Craft Description").
       setFunction("ROLE_API.Get_Description(:ROLE_CODE)").
       setReadOnly();
//Bug ID 59224,end

        itemblk2.addField("PLAN_MEN","Number").
        setCustomValidation("PLAN_MEN,ITEM2_PLAN_HRS,COST,ITEM2_CONTRACT,ITEM2_ORG_CODE,ROLE_CODE,TOTAL_ALLOCATED_HOURS","COST,COSTAMOUNT,TOTAL_MAN_HOURS,TOTAL_REMAINING_HOURS").
        setLabel("PCMWACTIVESEPARATE2PLANMEN: Planned Men");

        itemblk2.addField("ITEM2_PLAN_HRS","Number").
        setCustomValidation("PLAN_MEN,ITEM2_PLAN_HRS,COST,ITEM2_CONTRACT,ITEM2_ORG_CODE,ROLE_CODE,TOTAL_ALLOCATED_HOURS","COST,COSTAMOUNT,TOTAL_MAN_HOURS,TOTAL_REMAINING_HOURS").
        setLabel("PCMWACTIVESEPARATE2ITEM2PLANHRS: Planned Hours").
        setDbName("PLAN_HRS");

        itemblk2.addField("TOTAL_MAN_HOURS","Number").
        setLabel("PCMWACTIVESEPARATE2MANHRS: Total Man Hours").
        setFunction(":PLAN_MEN * :PLAN_HRS").
        setReadOnly();

        itemblk2.addField("ALLOCATED_HOURS","Number").
        setCustomValidation("ALLOCATED_HOURS,TOTAL_MAN_HOURS","TOTAL_ALLOCATED_HOURS,TOTAL_REMAINING_HOURS").
        setLabel("PCMWACTIVESEPARATE2ALLOCATEDHOURS: Allocated Hours");

        itemblk2.addField("TOTAL_ALLOCATED_HOURS","Number").
        setLabel("PCMWACTIVESEPARATE2TOTALLOCATEDHRS: Total Allocated Hours").
        setFunction("Work_Order_Role_API.Get_Total_Alloc(:ITEM2_WO_NO,:ROW_NO)").
        setReadOnly();

        itemblk2.addField("TOTAL_REMAINING_HOURS","Number").
        setLabel("PCMWACTIVESEPARATE2TOTALREMAININGHRS: Total Remaining Hours").
        setFunction("(:PLAN_MEN * :PLAN_HRS) - Work_Order_Role_API.Get_Total_Alloc(:ITEM2_WO_NO,:ROW_NO)").
        setReadOnly();

        itemblk2.addField("TEAM_CONTRACT").
        setSize(7).
        setDynamicLOV("USER_ALLOWED_SITE_LOV","ITEM2_COMPANY COMPANY",600,450).
        setLabel("PCMWACTIVESEPARATE2CONT: Team Site").
        setMaxLength(5).
        setInsertable().
        setUpperCase();

        itemblk2.addField("TEAM_ID").
        setSize(13).
        setCustomValidation("TEAM_ID,TEAM_CONTRACT","TEAMDESC").
        setDynamicLOV("MAINT_TEAM","TEAM_CONTRACT",600,450).
        setMaxLength(20).
        setInsertable().
        setLabel("PCMWACTIVESEPARATE2TID: Team ID").
        setUpperCase();

        itemblk2.addField("TEAMDESC").
        setFunction("Maint_Team_API.Get_Description(:TEAM_ID,:TEAM_CONTRACT)").
        setSize(40).
        setMaxLength(200).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATE2DESC: Description");

        itemblk2.addField("ITEM2_CATALOG_CONTRACT").
        setSize(8).
        setCustomValidation("ITEM2_CATALOG_CONTRACT","ITEM2_COMPANY").
        setHidden().
        setDbName("CATALOG_CONTRACT").
        setUpperCase();

        itemblk2.addField("ITEM2_CATALOG_NO").
        setDbName("CATALOG_NO").
        setSize(17).
        setHidden().
        setUpperCase().
        setMaxLength(25);

        itemblk2.addField("COST","Money","#.##").
        setLabel("PCMWACTIVESEPARATE2COST: Cost").
        setDefaultNotVisible().
        setFunction("nvl(Role_API.Get_Role_Cost(ROLE_CODE),Organization_API.Get_Org_Cost(CONTRACT,ORG_CODE))").
        setReadOnly(); 

        itemblk2.addField("COSTAMOUNT","Money","#.##").
        setLabel("PCMWACTIVESEPARATE2COSTAMT: Cost Amount").
        setFunction("NVL(:PLAN_MEN,1)*:PLAN_HRS*(WORK_ORDER_PLANNING_UTIL_API.Get_Cost(:ORG_CODE,:ROLE_CODE,:ITEM2_CATALOG_CONTRACT,:ITEM2_CATALOG_NO,:ITEM2_CONTRACT))").
        setDefaultNotVisible().
        setReadOnly();

        itemblk2.addField("ITEM2_JOB_ID", "Number").
        setDbName("JOB_ID").
        setInsertable().
        setDefaultNotVisible().
        setDynamicLOV("WORK_ORDER_JOB","ITEM2_WO_NO WO_NO").
        setCustomValidation("ITEM2_COMPANY,ITEM2_WO_NO,ITEM2_JOB_ID","SIGN_ID,SIGN").
        setLabel("PCMWACTIVESEPARATE2ITEM2JOBID: Job Id");

        itemblk2.addField("DATE_FROM","Datetime").
        setSize(22).
        setLabel("PCMWACTIVESEPARATE2DATEFROM: Date from").
        setDefaultNotVisible().
        setCustomValidation("ITEM2_WO_NO,DATE_FROM","ITEM2_TEMP,DATE_FROM_MASKED");

        itemblk2.addField("DATE_TO","Datetime").
        setSize(22).
        setLabel("PCMWACTIVESEPARATE2DATETO: Date to").
        setDefaultNotVisible().
        setCustomValidation("ITEM2_WO_NO,DATE_TO","ITEM2_TEMP");

        itemblk2.addField("DATE_FROM_MASKED","Datetime", getDateTimeFormat("JAVA")).
        setHidden().
        setFunction("''");

        itemblk2.addField("PREDECESSOR").
        setSize(22).
        setLabel("PCMWACTIVESEPARATE2PRED: Predecessors").
        setLOV("ConnectPredecessorsDlg.page",600,450).
        setFunction("Wo_Role_Dependencies_API.Get_Predecessors(:ITEM2_WO_NO, :ROW_NO)");

        itemblk2.addField("TOOLS").
        setSize(20).
        setLabel("PCMWACTIVESEPARATE2TOOLS: Tools").
        setDefaultNotVisible().
        setMaxLength(25);

        itemblk2.addField("REMARK").
        setSize(29).
        setLabel("PCMWACTIVESEPARATE2REMARK: Remark").
        setDefaultNotVisible().
        setMaxLength(2000).
        setHeight(4);

        itemblk2.addField("ORG_CONTRACT").
        setFunction("''").
        setHidden();

        itemblk2.addField("BASE_UNIT_PRICE").
        setFunction("''").
        setHidden();

        itemblk2.addField("SALE_UNIT_PRICE").
        setFunction("''").
        setHidden();

        itemblk2.addField("DISCOUNT","Number").
        setFunction("''").
        setHidden();

        itemblk2.addField("CURRENCY_RATE").
        setFunction("''").
        setHidden();

        itemblk2.addField("BUY_QTY_DUE").
        setFunction("''").
        setHidden();

        itemblk2.addField("ITEM2_TEMP").
        setFunction("''").
        setHidden();

        itemblk2.addField("COMPANY_VAR").
        setHidden().
        setFunction("''");

        itemblk2.addField("INSTRUCTION_TYPE").
        setSize(10).
        setDynamicLOV("INSTRUCTION_TYPE",600,450).
        setLabel("PCMWACTIVESEPARATE2INSTTYPE: Instruction Type"). 
        setUpperCase(). 
        setDefaultNotVisible().
        setMaxLength(10);

        itemblk2.addField("LOCATION").
        setSize(10).
        setDynamicLOV("INSTRUCTION_LOCATION",600,450).
        setLabel("PCMWACTIVESEPARATE2LOCATION: Location").
        setUpperCase().
        setDefaultNotVisible().
        setMaxLength(10);

        itemblk2.addField("SIGN_REQUIREMENT").
        setSize(10).
        setDynamicLOV("OPERATION_SIGN_REQUIREMENT",600,450).
        setLabel("PCMWACTIVESEPARATE2SIGNREQ: Sign Requirement"). 
        setUpperCase(). 
        setDefaultNotVisible().
        setMaxLength(10);

        itemblk2.addField("REFERENCE_NUMBER").
        setSize(25).
        setLabel("PCMWACTIVESEPARATE2REFNO: Reference Number"). 
        setDefaultNotVisible().
        setMaxLength(25);

        itemblk2.addField("PRODUCT_NO").
        setSize(25).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATE2PRODNO: Product No"). 
        setDefaultNotVisible(). 
        setMaxLength(25);

        itemblk2.addField("MODEL_NO").
        setSize(25).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATE2MODELNO: Model No"). 
        setDefaultNotVisible(). 
        setMaxLength(25);

        itemblk2.addField("FUNCTION_NO").
        setSize(25).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATE2FUNCNO: Function No"). 
        setDefaultNotVisible(). 
        setMaxLength(25);

        itemblk2.addField("SUB_FUNCTION_NO").
        setSize(25).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATE2SUBFUNCNO: Sub Function No"). 
        setDefaultNotVisible(). 
        setMaxLength(25);

        itemblk2.addField("BOTTOM_FUNCTION_NO").
        setSize(25).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATE2BOTFUNCNO: Bottom Function No"). 
        setDefaultNotVisible(). 
        setMaxLength(25);

        itemblk2.addField("CBADDQUALIFICATION").
        setLabel("PCMWACTIVESEPARATE2CBADDQUAL: Additional Qualifications").
        setFunction("Work_Order_Role_API.Check_Qualifications_Exist(:ITEM2_WO_NO,:ROW_NO)").
        setCheckBox("0,1").
        setReadOnly().
        setQueryable();

        // Bug 72214, start
	itemblk2.addField("SCHEDULED_STATUS").
	setLabel("PCMWACTIVESEPARATE2SCHEDSTATE: Scheduling Status").
	setSize(25).
	setReadOnly();
   
	itemblk2.addField("LATEST_UPDATE", "Datetime").
	setLabel("PCMWACTIVESEPARATE2LATESTUP: Latest Update").
	setSize(25).
	setReadOnly();

	itemblk2.addField("SCHEDULING_INFO").
	setHidden();
   
	itemblk2.addField("SCHEDULING_INFO_HIDDEN").
	setLabel("PCMWACTIVESEPARATE2SCHEDINFO: Scheduling Info").
	setFunction("PCM_CBS_INT_API.Translate_Sched_Info(:SCHEDULING_INFO)").
	setSize(35).
	setReadOnly();
	// Bug 72214, end

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

        //itembar2.defineCommand(itembar2.SAVERETURN,null,"checkMandoItem2()");
        itembar2.defineCommand(itembar2.SAVERETURN,"saveReturnITEM2","checkMandoItem2();Item2PlanMenCheck()");
        itembar2.defineCommand(itembar2.SAVENEW,null,"checkMandoItem2()");

        itembar2.addCustomCommand("freeTimeSearchFromOperations1",mgr.translate("PCMWACTIVESEPARATE2SMFREETIMESEARCH11: Free Time Search..."));
        itembar2.addCustomCommand("allocateEmployees0",mgr.translate("PCMWACTIVESEPARATE2ALLOCATEEMPLOYEES0: Allocate Employees..."));
        itembar2.addCustomCommand("selectAllocation0",mgr.translate("PCMWACTIVESEPARATE2CLEARALLOCATION0: Clear Allocation..."));
        itembar2.addCustomCommand("additionalQualifications1",mgr.translate("PCMWACTIVESEPARATE2ADDQUALIFICATIONS1: Additional Qualifications..."));
        itembar2.addCustomCommand("predecessors1",mgr.translate("PCMWACTIVESEPARATE2ADDPREDECESSORS1: Predecessors..."));
        itembar2.addCustomCommand("resheduleOperations1",mgr.translate("PCMWACTIVESEPARATE2RESHEDULEOPERATIONSITEM2: Reschedule Operations..."));
        itembar2.addCustomCommand("moveOperation1",mgr.translate("PCMWACTIVESEPARATE2MOVEOPERATION1: Move Operation..."));
        itembar2.removeFromMultirowAction("resheduleOperations1");
        itembar2.removeFromMultirowAction("moveOperation1");
        itembar2.removeFromMultirowAction("additionalQualifications1");
        itembar2.removeFromMultirowAction("freeTimeSearchFromOperations1");

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

        //------------Allocation on Operations - child of operations-------------

        itemblk8 = mgr.newASPBlock("ITEM8");

        itemblk8.addField("ITEM8_OBJID").
        setHidden().
        setDbName("OBJID");

        itemblk8.addField("ITEM8_OBJVERSION").
        setHidden().
        setDbName("OBJVERSION");  

        itemblk8.addField("PARENT_ROW_NO","Number").
        setLabel("PCMWACTIVESEPARATE2POPERATIONNO: Parent Operation No").
        setHidden();

        itemblk8.addField("ITEM8_ROW_NO","Number").
        setLabel("PCMWACTIVESEPARATE2ITEM8ROWNO: Operation No").
        setDbName("ROW_NO").
        setHidden().
        setInsertable();

        itemblk8.addField("ALLOCATION_NO","Number").
        setReadOnly().
        setInsertable().
        setLabel("PCMWACTIVESEPARATE2ALLOCNO: Allocation No");


        itemblk8.addField("ITEM8_DESCRIPTION").
        setDbName("DESCRIPTION").
        setLabel("PCMWACTIVESEPARATE2ALLOCDESC: Description");

        itemblk8.addField("ITEM8_SIGN").
        setSize(20).
        setMandatory().
        setLOV("../mscomw/MaintEmployeeLov.page","ITEM8_COMPANY COMPANY",600,450).
        setCustomValidation("ITEM8_COMPANY,ITEM8_SIGN,ITEM8_ROLE_CODE,ITEM8_CONTRACT,ITEM8_ORG_CODE,ORG_CODE","ITEM8_SIGN_ID,ITEM8_ROLE_CODE,ITEM8_ORG_CODE,ITEM8_SIGN,ITEM8_CONTRACT,ITEM8_ORG_CODE_DESC").
        setLOVProperty("TITLE","PCMWACTIVESEPARATE2LOVTITLE5: List of Employee").
        setLabel("PCMWACTIVESEPARATE2ITEM8SIGN: Executed By").
        setDbName("SIGN").
        setUpperCase().
        setMaxLength(20);

        itemblk8.addField("ITEM8_SIGN_ID").
        setSize(18).
        setMaxLength(11).
        setLabel("PCMWACTIVESEPARATE2ITEM8SIGNID: Employee ID").
        setDbName("SIGN_ID").
        setCustomValidation("ITEM8_SIGN_ID,ITEM8_COMPANY,ITEM8_ROLE_CODE","ITEM8_SIGN,ITEM8_ROLE_CODE,ITEM8_ORG_CODE").
        setUpperCase().
        setReadOnly();

        itemblk8.addField("ITEM8_ORG_CODE").
        setSize(8).
        setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ITEM8_CONTRACT CONTRACT",600,450).
        setCustomValidation("ITEM8_CONTRACT,ITEM8_ORG_CODE,ITEM8_ROLE_CODE,ITEM8_SIGN,ITEM8_WO_NO","ITEM8_ORG_CODE,ITEM8_CONTRACT,ITEM8_ORG_CODE_DESC").
        setLabel("PCMWACTIVESEPARATE2ITEM8ORGCODE: Maint.Org.").
        setUpperCase().
        setDbName("ORG_CODE").
        setMaxLength(8);

        //Bug ID 59224,start
        itemblk8.addField("ITEM8_ORG_CODE_DESC").
        setLabel("PCMWACTIVESEPARATE1ORGCODEDESC: Maint. Org. Description").
        setFunction("Organization_Api.Get_Description(:ITEM8_CONTRACT,:ITEM8_ORG_CODE)").
        setReadOnly();
        //Bug ID 59224,end
        itemblk8.addField("ITEM8_CONTRACT").
        setSize(8).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setLabel("PCMWACTIVESEPARATE2ITEM8CONTRACT: Maint.Org. Site").
        setUpperCase().
        setDbName("CONTRACT").
        setMaxLength(5);
        
        //Bug ID 59224,start
        itemblk8.addField("ITEM8_ROLE_CODE").
        setSize(10).
        setDynamicLOV("ROLE_TO_SITE_LOV","ITEM8_CONTRACT CONTRACT",600,445).
        setCustomValidation("ITEM8_ROLE_CODE,ITEM8_CONTRACT,ITEM8_ORG_CODE,ITEM8_WO_NO","ITEM8_ROLE_CODE,ITEM8_CONTRACT,ITEM8_ROLE_CODE_DESC").
        setLabel("PCMWACTIVESEPARATE2ITEM8ROLECODE: Craft ID").
        setDbName("ROLE_CODE").
        setUpperCase().
        setMaxLength(10);

         itemblk8.addField("ITEM8_ROLE_CODE_DESC").
        setLabel("PCMWACTIVESEPARATE2ROLECODEDESC: Craft Description").
        setFunction("ROLE_API.Get_Description(:ITEM8_ROLE_CODE)").
        setReadOnly();
      //Bug ID 59224,end


        itemblk8.addField("ITEM8_ALLOCATED_HOURS","Number").
        setDbName("ALLOCATED_HOURS").
        setLabel("PCMWACTIVESEPARATE2ALLOCATEDHOURS: Allocated Hours");

        itemblk8.addField("ITEM8_TEAM_CONTRACT").
        setSize(7).
        setDynamicLOV("USER_ALLOWED_SITE_LOV","ITEM8_COMPANY COMPANY",600,450).
        setLabel("PCMWACTIVESEPARATE2ITEM8TCONT: Team Site").
        setMaxLength(5).
        setDbName("TEAM_CONTRACT").
        setInsertable().
        setUpperCase();

        itemblk8.addField("ITEM8_TEAM_ID").
        setSize(13).
        setCustomValidation("ITEM8_TEAM_ID,ITEM8_TEAM_CONTRACT","ITEM8_TEAMDESC").
        setDynamicLOV("MAINT_TEAM","TEAM_CONTRACT",600,450).
        setMaxLength(20).
        setDbName("TEAM_ID").
        setInsertable().
        setLabel("PCMWACTIVESEPARATE2ITEM8TEAMID: Team ID").
        setUpperCase();

        itemblk8.addField("ITEM8_TEAMDESC").
        setFunction("Maint_Team_API.Get_Description(:ITEM8_TEAM_ID,:ITEM8_TEAM_CONTRACT)").
        setSize(40).
        setMaxLength(200).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATE2ITEM8DESC: Description");

        itemblk8.addField("ITEM8_DATE_FROM","Datetime").
        setSize(22).
        setDbName("DATE_FROM").
        setMandatory().
        setCustomValidation("ITEM8_DATE_FROM", "ITEM8_DATE_FROM_MASKED").
        setLabel("PCMWACTIVESEPARATE2ITEM8DFROM: Date from");

        itemblk8.addField("ITEM8_DATE_FROM_MASKED","Datetime", getDateTimeFormat("JAVA")).
        setHidden().
        setFunction("''");

        itemblk8.addField("ITEM8_DATE_TO","Datetime").
        setSize(22).
        setDbName("DATE_TO").
        setMandatory().
        setLabel("PCMWACTIVESEPARATE2ITEM8DTO: Date to");

        itemblk8.addField("ITEM8_WO_NO","Number","#").
        setMandatory().
        setHidden().
        setDbName("WO_NO");

        itemblk8.addField("ITEM8_COMPANY").
        setSize(18).
        setHidden().
        setLabel("PCMWACTIVESEPARATE2ITEM8COMPANY: Company").
        setUpperCase().
        setDbName("COMPANY").
        setMaxLength(20);

        itemblk8.addField("ITEM8_TEMP").
        setFunction("''").
        setHidden();   

        itemblk8.setView("WORK_ORDER_ROLE");
        itemblk8.defineCommand("WORK_ORDER_ROLE_API","New__,Modify__,Remove__");
        itemset8 = itemblk8.getASPRowSet();

        itemblk8.setMasterBlock(itemblk2);

        itembar8 = mgr.newASPCommandBar(itemblk8);
        itembar8.enableCommand(itembar8.FIND);
        itembar8.defineCommand(itembar8.NEWROW,"newRowITEM8");
        itembar8.defineCommand(itembar8.EDITROW, "editRowITEM8");
        itembar8.defineCommand(itembar8.COUNTFIND,"countFindITEM8");
        itembar8.defineCommand(itembar8.OKFIND,"okFindITEM8");
        itembar8.defineCommand(itembar8.DUPLICATEROW,"duplicateITEM8");

        itembar8.defineCommand(itembar8.SAVERETURN, "saveReturnITEM8","checkItem8Fields(-1)");
        itemtbl8 = mgr.newASPTable(itemblk8);
        itemtbl8.setWrap();
        itemtbl8.enableRowSelect();
        itembar8.enableMultirowAction();

        itemlay8 = itemblk8.getASPBlockLayout();
        itemlay8.setDefaultLayoutMode(itemlay8.MULTIROW_LAYOUT);
        itemlay8.setDialogColumns(3);  

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
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2MAINT_MATERIAL_ORDER_NO: Order No");

        f = itemblk3.addField("ITEM3_WO_NO","Number","#");
        f.setSize(11);
        f.setDbName("WO_NO");
        f.setMaxLength(8);
        f.setReadOnly();
        f.setCustomValidation("ITEM3_WO_NO","DUE_DATE,NPREACCOUNTINGID,ITEM3_CONTRACT,ITEM3_COMPANY,MCHCODE,ITEM3DESCRIPTION");
        f.setDynamicLOV("ACTIVE_WO_NOT_REPORTED",600,445);
        f.setLabel("PCMWACTIVESEPARATE2WO_NO: WO No");

        f = itemblk3.addField("MCHCODE");
        f.setSize(13);
        f.setHidden();
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2MCHCODE: Object ID");
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
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445);
        f.setLabel("PCMWACTIVESEPARATE2SIGNATURE: Signature");
        f.setUpperCase();

        f = itemblk3.addField("SIGNATURENAME");
        f.setSize(15);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPARATE2SIGNATURENAME: Signature Name");
        f.setMaxLength(2000);
        f.setFunction("EMPLOYEE_API.Get_Employee_Info(Site_API.Get_Company(:ITEM3_CONTRACT),:SIGNATURE_ID)");

        f = itemblk3.addField("ITEM3_CONTRACT");
        f.setSize(10);
        f.setReadOnly();
        f.setDbName("CONTRACT");
        f.setMaxLength(5);
        f.setLabel("PCMWACTIVESEPARATE2ITEM3CONTRACT: Site");
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setUpperCase();
        f.setInsertable();
        f.setMandatory();

        f = itemblk3.addField("ENTERED","Date");
        f.setSize(22);
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2ENTERED: Entered");

        f = itemblk3.addField("INT_DESTINATION_ID");
        f.setSize(8);
        f.setMaxLength(30);
        f.setUpperCase();
        f.setCustomValidation("INT_DESTINATION_ID,ITEM3_CONTRACT","INT_DESTINATION_DESC");
        if (mgr.isModuleInstalled("INVENT"))
            f.setDynamicLOV("INTERNAL_DESTINATION_LOV","ITEM3_CONTRACT CONTRACT",600,445);
        f.setLabel("PCMWACTIVESEPARATE2INT_DESTINATION_ID: Int Destination");

        f = itemblk3.addField("INT_DESTINATION_DESC");
        f.setSize(15);
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPARATE2INT_DESTINATION_DESC: Int Destination Desc.");
        f.setMaxLength(2000);

        f = itemblk3.addField("DUE_DATE","Date");
        f.setSize(22);
        f.setLabel("PCMWACTIVESEPARATE2DUE_DATE: Due Date");

        f = itemblk3.addField("ITEM3_STATE");
        f.setSize(10);
        f.setDbName("STATE");
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2STATE: Status");

        f = itemblk3.addField("NREQUISITIONVALUE", "Number");
        f.setHidden();
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2NREQUISITIONVALUE: Total Value");
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
        f.setHidden();
        f.setReadOnly();
        f.setFunction("Active_Work_Order_API.Get_Pre_Accounting_Id(:ITEM3_WO_NO)");

        f = itemblk3.addField("SYS_DATE","Date");
        f.setFunction("''");
        f.setHidden();

        f = itemblk3.addField("ITEM3_ACTIVITY_SEQ","Number");
        f.setLabel("ACTSEP2ITEM3ACTSEQ: Activity Seq");
        f.setDbName("ACTIVITY_SEQ");
        f.setHidden();

        f = itemblk3.addField("MUL_REQ_LINE");
        f.setReadOnly();
        f.setFunction("Maint_Material_Requisition_API.Multiple_Mat_Req_Exist(:ITEM3_WO_NO)");
        f.setLabel("ACTSEP2ITEM3MULMATREQEXIST: Multiple Material Requisitions Exist"); 
        f.setCheckBox("FALSE,TRUE");
        f.setDefaultNotVisible();

        f = itemblk3.addField("EXIST");
        f.setFunction("''");
        f.setHidden();

        f = itemblk3.addField("ORDER_LIST");
        f.setFunction("''");
        f.setHidden();

        f = itemblk3.addField("DUMMY_ACT_ISSUE","Number");
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
        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
        itemtbl3.enableRowSelect();
        // 040108  ARWILK  End  (Enable Multirow RMB actions)

        itembar3 = mgr.newASPCommandBar(itemblk3);
        itembar3.addCustomCommand("plan", mgr.translate("PCMWACTIVESEPARATE2PLANCONS: Plan"));
        itembar3.addCustomCommand("release", mgr.translate("PCMWACTIVESEPARATE2RELEA: Release"));
        itembar3.addCustomCommand("close", mgr.translate("PCMWACTIVESEPARATE2CLOS: Close"));
        itembar3.addCustomCommandSeparator();
        itembar3.addCustomCommand("printPicList", mgr.translate("PCMWACTIVESEPARATE2PICLSTMAT: Pick List For Material Requistion - Printout..."));
        itembar3.addCustomCommandSeparator();

        itembar3.enableCommand(itembar3.FIND);
        itembar3.defineCommand(itembar3.DELETE,"deleteItem");
        itembar3.defineCommand(itembar3.NEWROW, "newRowITEM3");
        itembar3.defineCommand(itembar3.COUNTFIND, "countFindITEM3");
        itembar3.defineCommand(itembar3.OKFIND, "okFindITEM3");
        itembar3.defineCommand(itembar3.SAVERETURN, "saveReturnItem", "checkItem3Fields()");
        itembar3.defineCommand(itembar3.SAVENEW, null, "checkItem3Fields()");

        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
        itembar3.addCommandValidConditions("plan",    "OBJSTATE", "Enable", "Released");
        itembar3.addCommandValidConditions("release", "OBJSTATE", "Enable", "Planned");
	itembar3.appendCommandValidConditions("release", "WO_STATUS", "Enable", "RELEASED;STARTED;WORKDONE;PREPARED;UNDERPREPARATION");
        itembar3.addCommandValidConditions("close",   "OBJSTATE", "Enable", "Planned;Released");

        itembar3.enableMultirowAction();
        // 040108  ARWILK  End  (Enable Multirow RMB actions)

        itemlay3 = itemblk3.getASPBlockLayout();
        itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);
        itemlay3.setSimple("INT_DESTINATION_DESC");
        itemlay3.setSimple("SIGNATURENAME");
        itemlay3.setFieldOrder("ITEM3_WO_NO,MCHCODE");

        // ----------------------------------------------------------------------------------------------
        // ----------------------------------------------------------------------------------------------
        // ----------------------------------------------------------------------------------------------


        itemblk = mgr.newASPBlock("ITEM4");

        f = itemblk.addField("ITEM_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk.addField("ITEM_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk.addField("LINE_ITEM_NO","Number");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2LINE_ITEM_NO: Line No");       

        f = itemblk.addField("PART_NO");
        f.setSize(14);
        f.setReadOnly();
        f.setInsertable();
        f.setMaxLength(25);
        f.setHyperlink("../invenw/InventoryPart.page","PART_NO","NEWWIN"); 
        f.setCustomValidation("PART_NO,SPARE_CONTRACT,CATALOG_CONTRACT,OWNER,CONDITION_CODE,SUPPLY_CODE,ITEM0_MAINT_MATERIAL_ORDER_NO,PART_OWNERSHIP,ITEM_WO_NO","CATALOG_NO,CATALOGDESC,SCODEA,SCODEB,SCODEC,SCODED,SCODEE,SCODEF,SCODEG,SCODEH,SCODEI,SCODEJ,HASSPARESTRUCTURE,DIMQTY,TYPEDESIGN,QTYONHAND,UNITMEAS,SPAREDESCRIPTION,SALES_PRICE_GROUP_ID,CONDITION_CODE,CONDDESC,QTY_AVAILABLE,ACTIVEIND_DB,PART_OWNERSHIP,PART_OWNERSHIP_DB,OWNER");  
        if (mgr.isModuleInstalled("INVENT"))
            f.setDynamicLOV("INVENTORY_PART_WO_LOV","SPARE_CONTRACT CONTRACT",600,445);
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPARATE2PART_NO: Part No");
        f.setUpperCase();

        f = itemblk.addField("SPAREDESCRIPTION");
        f.setSize(20);
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2SPAREDESCRIPTION: Part Description");
        if (mgr.isModuleInstalled("INVENT"))
            f.setFunction("INVENTORY_PART_API.Get_Description(:SPARE_CONTRACT,:PART_NO)");
        else
            f.setFunction("''");

        f = itemblk.addField("CONDITION_CODE");
        f.setLabel("PCMWACTIVESEPARATE2CONDITIONCODE: Condition Code");
        f.setSize(15);
        f.setDynamicLOV("CONDITION_CODE",600,445);
        f.setUpperCase(); 
        f.setCustomValidation("CONDITION_CODE,PART_NO,SPARE_CONTRACT,OWNER,PART_OWNERSHIP_DB,SUPPLY_CODE,ITEM0_MAINT_MATERIAL_ORDER_NO,ITEM3_ACTIVITY_SEQ,PART_OWNERSHIP,PLAN_QTY","CONDDESC,QTYONHAND,QTY_AVAILABLE,ITEM_COST,AMOUNTCOST"); 

        f = itemblk.addField("CONDDESC");
        f.setLabel("PCMWACTIVESEPARATE2CONDDESC: Condition Code Description");
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
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPARATE2PARTOWNERNAME: Owner Name");
        f.setFunction("''");

        f = itemblk.addField("SPARE_CONTRACT");
        f.setSize(11);
        f.setDefaultNotVisible();
        f.setMaxLength(5);
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPARATE2SPARE_CONTRACT: Site");
        f.setUpperCase();
        f.setReadOnly();

        f = itemblk.addField("HASSPARESTRUCTURE");
        f.setSize(8);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2HASSPARESTRUCTURE: Structure");
        f.setFunction("Equipment_Spare_Structure_API.Has_Spare_Structure_Boolean(:PART_NO,:SPARE_CONTRACT)");

        f = itemblk.addField("ITEM_JOB_ID", "Number");
        f.setDbName("JOB_ID");
        f.setInsertable();
        f.setDefaultNotVisible();
        f.setDynamicLOV("WORK_ORDER_JOB", "ITEM_WO_NO WO_NO");
        f.setLabel("PCMWACTIVESEPARATE2ITEMJOBID: Job Id");

        f = itemblk.addField("CRAFT_LINE_NO","Number");
        f.setDynamicLOV("WORK_ORDER_ROLE","WO_NO",600,445);
        f.setLabel("PCMWACTIVESEPARATE2CRFTLINNO: Operation No");

        f = itemblk.addField("DIMQTY");
        f.setSize(11);
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setDefaultNotVisible();   
        f.setLabel("PCMWACTIVESEPARATE2DIMQTY: Dimension/ Quality");
        if (mgr.isModuleInstalled("INVENT"))
            f.setFunction("INVENTORY_PART_API.Get_Dim_Quality(:SPARE_CONTRACT,:PART_NO)");
        else
            f.setFunction("''");       

        f = itemblk.addField("TYPEDESIGN");
        f.setSize(15);
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2TYPEDESIGN: Type Designation");
        f.setFunction("INVENTORY_PART_API.Get_Type_Designation(:SPARE_CONTRACT,:PART_NO)");
        if (mgr.isModuleInstalled("INVENT"))
            f.setFunction("INVENTORY_PART_API.Get_Type_Designation(:SPARE_CONTRACT,:PART_NO)");
        else
            f.setFunction("''");        

        f = itemblk.addField("DATE_REQUIRED","Date");
        f.setSize(22);
        f.setLabel("PCMWACTIVESEPARATE2DATE_REQUIRED: Date Required");

        f = itemblk.addField("SUPPLY_CODE");
        f.setSize(25);
        f.setCustomValidation("PART_NO,SPARE_CONTRACT,CATALOG_CONTRACT,OWNER,CONDITION_CODE,SUPPLY_CODE,ITEM0_MAINT_MATERIAL_ORDER_NO,ITEM3_ACTIVITY_SEQ","QTYONHAND,QTY_AVAILABLE"); 
        f.setMandatory();
        f.setInsertable();
        f.setSelectBox();

        f.enumerateValues("MAINT_MAT_REQ_SUP_API");
        f.setLabel("PCMWACTIVESEPARATE2SUPPLYCODE: Supply Code");
        f.setMaxLength(200);

        f = itemblk.addField("PLAN_QTY","Number");
        f.setCustomValidation("PLAN_QTY,PART_NO,SPARE_CONTRACT,CATALOG_NO,CATALOG_CONTRACT,PRICE_LIST_NO,PLAN_LINE_NO,ITEM_DISCOUNT,ITEM_WO_NO,ITEM_COST,LIST_PRICE,ITEMDISCOUNTEDPRICE,SERIAL_NO,CONFIGURATION_ID,CONDITION_CODE","ITEM_COST,AMOUNTCOST,PRICE_LIST_NO,ITEM_DISCOUNT,LIST_PRICE,SALESPRICEAMOUNT,ITEMDISCOUNTEDPRICE");
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPARATE2PLAN_QTY: Quantity Required");

        f = itemblk.addField("QTY","Number");
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2QTY: Quantity Issued");   

        f = itemblk.addField("QTY_SHORT","Number");     
        f.setMandatory();
        f.setHidden();
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2QTY_SHORT: Quantity Short");


        f = itemblk.addField("QTYONHAND","Number");
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2QTYONHAND: Quantity on Hand");
        if (mgr.isModuleInstalled("INVENT"))
            f.setFunction("Maint_Material_Req_Line_Api.Get_Qty_On_Hand(:ITEM0_MAINT_MATERIAL_ORDER_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''"); 

        f = itemblk.addField("QTY_AVAILABLE","Number");
        f.setSize(17);
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2QTY_AVAILABLE: Quantity Available");
        if (mgr.isModuleInstalled("INVENT"))
            f.setFunction("MAINT_MATERIAL_REQ_LINE_API.Get_Qty_Available(:ITEM0_MAINT_MATERIAL_ORDER_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''");

        f = itemblk.addField("QTY_ASSIGNED","Number");
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2QTY_ASSIGNED: Quantity Assigned");

        f = itemblk.addField("QTY_RETURNED","Number");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPARATE2QTY_RETURNED: Quantity Returned");

        f = itemblk.addField("UNITMEAS");
        f.setSize(11);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2UNITMEAS: Unit");
        if (mgr.isModuleInstalled("PURCH"))
            f.setFunction("Purchase_Part_Supplier_API.Get_Unit_Meas(:SPARE_CONTRACT,:PART_NO)");
        else
            f.setFunction("''");

        f = itemblk.addField("CATALOG_CONTRACT");
        f.setSize(10);
        f.setMaxLength(5);
        f.setDefaultNotVisible();
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PCMWACTIVESEPARATE2CATALOG_CONTRACT: Sales Part Site");
        f.setUpperCase();
        f.setReadOnly();

        f = itemblk.addField("CATALOG_NO");  
        f.setSize(9);
        f.setMaxLength(25);
        f.setDefaultNotVisible();
        f.setCustomValidation("CATALOG_NO,ITEM_WO_NO,CATALOG_CONTRACT,PRICE_LIST_NO,PLAN_QTY,PLAN_LINE_NO,PART_NO,SPARE_CONTRACT,ITEM_COST,PLAN_QTY,ITEM_DISCOUNT,CURRENCEY_CODE","LIST_PRICE,ITEM_COST,AMOUNTCOST,CATALOGDESC,ITEM_DISCOUNT,SALESPRICEAMOUNT,SALES_PRICE_GROUP_ID,ITEMDISCOUNTEDPRICE,PRICE_LIST_NO");
        if (mgr.isModuleInstalled("ORDER"))
            f.setDynamicLOV("SALES_PART_ACTIVE_LOV","CATALOG_CONTRACT CONTRACT",600,445);
        f.setLabel("PCMWACTIVESEPARATE2CATALOG_NO: Sales Part Number");
        f.setUpperCase();

        f = itemblk.addField("CATALOGDESC");
        f.setSize(17);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setLabel("PCMWACTIVESEPARATE2CATALOGDESC: Sales Part Description");
        if (mgr.isModuleInstalled("ORDER"))
            f.setFunction("SALES_PART_API.Get_Catalog_Desc(:CATALOG_CONTRACT,:CATALOG_NO)");
        else
            f.setFunction("''");

        f = itemblk.addField("SALES_PRICE_GROUP_ID");
        f.setHidden();
        if (mgr.isModuleInstalled("ORDER"))
            f.setFunction("SALES_PART_API.GET_SALES_PRICE_GROUP_ID(:CATALOG_CONTRACT,:CATALOG_NO)");
        else
            f.setFunction("''");

        f = itemblk.addField("CURRENCY_CODE");
        f.setHidden();
        f.setFunction("ACTIVE_SEPARATE_API.GET_CURRENCY_CODE(:ITEM_WO_NO)");

        f = itemblk.addField("PRICE_LIST_NO");
        f.setSize(10);
        f.setMaxLength(10);
        f.setDefaultNotVisible();
        f.setCustomValidation("PRICE_LIST_NO,SPARE_CONTRACT,PART_NO,ITEM_COST,PLAN_QTY,CATALOG_NO,PLAN_QTY,ITEM_WO_NO,PLAN_LINE_NO,ITEM_DISCOUNT,CATALOG_CONTRACT","ITEM_COST,AMOUNTCOST,LIST_PRICE,ITEM_DISCOUNT,SALESPRICEAMOUNT,ITEMDISCOUNTEDPRICE");
        if (mgr.isModuleInstalled("ORDER"))
            f.setDynamicLOV("SALES_PRICE_LIST_LOV","SALES_PRICE_GROUP_ID,CURRENCY_CODE",600,445);
        f.setLabel("PCMWACTIVESEPARATE2PRICE_LIST_NO: Price List No");
        f.setUpperCase();

        f = itemblk.addField("LIST_PRICE","Money");
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPARATE2LIST_PRICE: Sales Price");
        f.setReadOnly();

        f = itemblk.addField("ITEM_DISCOUNT","Number");
        f.setDefaultNotVisible();
        f.setDbName("DISCOUNT");
        f.setCustomValidation("ITEM_DISCOUNT,LIST_PRICE,PLAN_QTY","SALESPRICEAMOUNT,ITEMDISCOUNTEDPRICE");
        f.setLabel("PCMWACTIVESEPARATE2DISCOUNT: Discount %");

        f = itemblk.addField("CURRENCEY_CODE");
        f.setHidden();
        f.setFunction("ACTIVE_SEPARATE_API.Get_Currency_Code(:ITEM_WO_NO)");

        f = itemblk.addField("ITEMDISCOUNTEDPRICE", "Money");
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2ITEMDISCOUNTEDPRICE: Discounted Price");
        f.setFunction("LIST_PRICE - (NVL(DISCOUNT, 0)/100*LIST_PRICE)");


        f = itemblk.addField("SALESPRICEAMOUNT", "Money");  
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPARATE2SALESPRICEAMOUNT: Price Amount");
        f.setFunction("''");

        f = itemblk.addField("ITEM_COST", "Money");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPARATE2COST: Cost");
        f.setFunction("''");

        f = itemblk.addField("AMOUNTCOST", "Money");
        f.setDefaultNotVisible();
        f.setLabel("PCMWACTIVESEPARATE2AMOUNTCOST: Cost Amount");
        f.setFunction("''");
        f.setReadOnly();

        f = itemblk.addField("SCODEA");   
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2SCODEA: Account");
        f.setFunction("''");
        f.setReadOnly();
        f.setHidden();
        f.setDefaultNotVisible();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEB");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2SCODEB: Cost Center");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setHidden();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEF");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2SCODEF: Project No");
        f.setFunction("''");
        f.setReadOnly();
        f.setHidden();
        f.setDefaultNotVisible();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEE");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2SCODEE: Object No");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setHidden();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEC");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2SCODEC: Code C");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setHidden();
        f.setMaxLength(10);

        f = itemblk.addField("SCODED");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2SCODED: Code D");
        f.setFunction("''");
        f.setReadOnly();
        f.setHidden();
        f.setDefaultNotVisible();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEG");   
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2SCODEG: Code G");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setHidden();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEH");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2SCODEH: Code H");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setHidden();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEI");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2SCODEI: Code I");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setHidden();
        f.setMaxLength(10);

        f = itemblk.addField("SCODEJ");
        f.setSize(11);
        f.setLabel("PCMWACTIVESEPARATE2SCODEJ: Code J");
        f.setFunction("''");
        f.setReadOnly();
        f.setHidden();
        f.setDefaultNotVisible();
        f.setMaxLength(10);

        f = itemblk.addField("ITEM_WO_NO","Number", "#");
        f.setMandatory();
        f.setHidden();
        f.setDefaultNotVisible();  
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2ITEM0_WO_NO: Work Order No");
        f.setDbName("WO_NO");

        f = itemblk.addField("PLAN_LINE_NO","Number");
        f.setReadOnly();
        f.setHidden();
        f.setLabel("PCMWACTIVESEPARATE2PLAN_LINE_NO: Plan Line No");

        f = itemblk.addField("ITEM0_MAINT_MATERIAL_ORDER_NO","Number","#");
        f.setHidden();
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATE2ITEM0_MAINT_MATERIAL_ORDER_NO: Mat Req Order No");
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

        f = itemblk.addField("COND_CODE_USAGE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("COND_CODE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("COND_CODE_DESC");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("PART_DESC");
        f.setFunction("''");
        f.setHidden();

        f = itemblk.addField("SUPPLY_CODE_DB");
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

        itemblk.setView("MAINT_MATERIAL_REQ_LINE");

        itemblk.defineCommand("MAINT_MATERIAL_REQ_LINE_API","New__,Modify__,Remove__");
        itemblk.setMasterBlock(itemblk3);

        itemset = itemblk.getASPRowSet();

        itemtbl = mgr.newASPTable(itemblk);
        itemtbl.setWrap();
        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
        itemtbl.enableRowSelect();
        // 040108  ARWILK  End  (Enable Multirow RMB actions)

        itembar = mgr.newASPCommandBar(itemblk);
        itembar.enableCommand(itembar.FIND);
        itembar.defineCommand(itembar.NEWROW,"newRowITEM4");
        itembar.defineCommand(itembar.COUNTFIND,"countFindITEM4");
        itembar.defineCommand(itembar.OKFIND,"okFindITEM4");

        //itembar.defineCommand(itembar.SAVERETURN,null,"checkItem4Owner()");
        itembar.defineCommand(itembar.DUPLICATEROW,"duplicateITEM4");

        // 031204  ARWILK  Begin  (Replace State links with RMB's)
        itembar.addCustomCommand("sparePartObject",mgr.translate("PCMWACTIVESEPARATE2SPAREPARTOBJ: Spare Parts in Object..."));
        itembar.addCustomCommand("updateSparePartObject", mgr.translate("PCMWACTIVESEPARATE2UPDATESPRPARTS: Update Spare Parts in Object..."));
	itembar.addCustomCommand("objStructure",mgr.translate("PCMWACTIVESEPARATE2OBJSTRUCT: Object Structure..."));
        // 031204  ARWILK  End  (Replace State links with RMB's)
        itembar.addCustomCommand("detchedPartList",mgr.translate("PCMWACTIVESEPARATE2SPAREINDETACH: Spare Parts in Detached Part List..."));
        itembar.addCustomCommandSeparator();
        itembar.addCustomCommand("reserve",mgr.translate("PCMWACTIVESEPARATE2RESERV: Reserve"));
        itembar.addCustomCommand("manReserve",mgr.translate("PCMWACTIVESEPARATE2RESERVMAN: Reserve manually..."));
        if (mgr.isPresentationObjectInstalled("invenw/InventoryPartInStockOvw.page"))
            itembar.addCustomCommand("availtoreserve",mgr.translate("PCMWACTIVESEPARATE2AVAILTORESERVE: Inventory Part in Stock..."));
        itembar.addCustomCommand("unreserve",mgr.translate("PCMWACTIVESEPARATE2UNRESERV: Unreserve..."));
        itembar.addCustomCommand("issue",mgr.translate("PCMWACTIVESEPARATE2ISSUE: Issue"));
        itembar.addCustomCommand("manIssue",mgr.translate("PCMWACTIVESEPARATE2ISSUEMAN: Issue manually..."));
        itembar.addCustomCommandSeparator();
        if (mgr.isPresentationObjectInstalled("invenw/InventoryPartAvailabilityPlanningQry.page"))
            itembar.addCustomCommand("availDetail",mgr.translate("PCMWACTIVESEPARATE2INVAVAILPLAN: Query - Inventory Part Availability Planning..."));
        if (mgr.isPresentationObjectInstalled("purchw/PurchasePartSupplier.page"))
            itembar.addCustomCommand("supPerPart",mgr.translate("PCMWACTIVESEPARATE2SUPFORPART: Supplier per Part..."));
        itembar.addCustomCommand("matReqUnissue",mgr.translate("PCMWACTIVESEPARATE2MATREQUNISSU: Material Requisition Unissue..."));
        itembar.addCustomCommand("matReqIssued",mgr.translate("PCMWACTIVESEPARATE2ISSUEDPDETAILS: Issued Part Details..."));

        // 031223  ARWILK  Begin  (Links with multirow RMB's)
        itembar.enableMultirowAction();
        itembar.removeFromMultirowAction("manReserve");
        itembar.removeFromMultirowAction("unreserve");
        itembar.removeFromMultirowAction("manIssue");
        itembar.removeFromMultirowAction("matReqUnissue");
        // 031223  ARWILK  End  (Links with multirow RMB's)


        itembar.forceEnableMultiActionCommand("sparePartObject");
        itembar.forceEnableMultiActionCommand("objStructure");
        itembar.forceEnableMultiActionCommand("detchedPartList");

        itembar.addCommandValidConditions("updateSparePartObject","SPARE_PART_EXIST","Enable","0");

        itemlay = itemblk.getASPBlockLayout();
        itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);

        //-----------------------------------------------------------------------
        //---------- This part belongs to the Tools and Facilities Tab ----------
        //-----------------------------------------------------------------------

        itemblk5 = mgr.newASPBlock("ITEM5");

        f = itemblk5.addField("ITEM5_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = itemblk5.addField("ITEM5_OBJVERSION");
        f.setDbName("OBJVERSION");
        f.setHidden();

        f = itemblk5.addField("ITEM5_WO_NO","Number", "#");
        f.setDbName("WO_NO");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5WONO: Wo No");
        f.setMandatory();
        f.setInsertable();
        f.setReadOnly();
        f.setHidden();

        f = itemblk5.addField("ITEM5_ROW_NO","Number");
        f.setDbName("ROW_NO");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5ROWNO: Row No");
        f.setReadOnly();

        f = itemblk5.addField("ITEM5_CONTRACT");
        f.setDbName("CONTRACT");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5CONTRACT: Site");
        f.setLOV("ConnectToolsFacilitiesLov.page","TOOL_FACILITY_ID,ITEM5_ORG_CODE ORG_CODE,TOOL_FACILITY_TYPE",600,450);
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPARATE2ITEM5LOVTITLE: Connect Tools and Facilities"));
        f.setCustomValidation("ITEM5_CONTRACT,ITEM5_ORG_CODE,TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM5_QTY,ITEM5_PLANNED_HOUR,ITEM5_WO_NO,ITEM5_NOTE,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_CONTRACT,ITEM5_CATALOG_NO_DESC,ITEM5_SALES_PRICE,ITEM5_SALES_CURRENCY,ITEM5_DISCOUNT,ITEM5_DISCOUNTED_PRICE,ITEM5_PLANNED_PRICE","ITEM5_NOTE,ITEM5_QTY,ITEM5_COST_AMOUNT,ITEM5_CATALOG_NO_CONTRACT,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_DESC,ITEM5_SALES_PRICE,ITEM5_SALES_CURRENCY,ITEM5_DISCOUNT,ITEM5_DISCOUNTED_PRICE,ITEM5_PLANNED_PRICE,TOOL_FACILITY_ID,ITEM5_CONTRACT,ITEM5_ORG_CODE");
        f.setSize(8);
        f.setMaxLength(5);
        f.setUpperCase();

        f = itemblk5.addField("TOOL_FACILITY_ID");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5TFID: Tool/Facility Id");
        f.setLOV("ConnectToolsFacilitiesLov.page","ITEM5_CONTRACT CONTRACT,ITEM5_ORG_CODE ORG_CODE,TOOL_FACILITY_TYPE",600,450);
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPARATE2ITEM5LOVTITLE: Connect Tools and Facilities"));
        f.setCustomValidation("TOOL_FACILITY_ID,ITEM5_CONTRACT,ITEM5_ORG_CODE,ITEM5_WO_NO,ITEM5_QTY,ITEM5_PLANNED_HOUR,ITEM5_NOTE,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_CONTRACT,ITEM5_CATALOG_NO_DESC,ITEM5_SALES_PRICE,ITEM5_SALES_CURRENCY,ITEM5_DISCOUNT,ITEM5_DISCOUNTED_PRICE,ITEM5_PLANNED_PRICE","TOOL_FACILITY_DESC,TOOL_FACILITY_TYPE,TYPE_DESCRIPTION,ITEM5_QTY,ITEM5_COST,ITEM5_COST_CURRENCY,ITEM5_COST_AMOUNT,ITEM5_CATALOG_NO_CONTRACT,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_DESC,ITEM5_SALES_PRICE,ITEM5_SALES_CURRENCY,ITEM5_DISCOUNT,ITEM5_DISCOUNTED_PRICE,ITEM5_PLANNED_PRICE,ITEM5_NOTE,TOOL_FACILITY_ID,ITEM5_CONTRACT,ITEM5_ORG_CODE");
        f.setSize(20);
        f.setMaxLength(40);
        f.setInsertable();
        f.setUpperCase();

        f = itemblk5.addField("TOOL_FACILITY_DESC");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5TFIDDESC: Tool/Facility Description");
        f.setFunction("Tool_Facility_API.Get_Tool_Facility_Description(:TOOL_FACILITY_ID)");
        f.setSize(30);
        f.setMaxLength(200);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk5.addField("TOOL_FACILITY_TYPE");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5TFTYPE: Tool/Facility Type");
        f.setDynamicLOV("TOOL_FACILITY_TYPE",600,445);
        f.setCustomValidation("TOOL_FACILITY_TYPE,TOOL_FACILITY_ID,ITEM5_WO_NO,ITEM5_QTY,ITEM5_PLANNED_HOUR","TYPE_DESCRIPTION,ITEM5_COST,ITEM5_COST_CURRENCY,ITEM5_COST_AMOUNT");
        f.setSize(20);
        f.setMaxLength(40);
        f.setInsertable();
        f.setUpperCase();
        f.setMandatory();

        f = itemblk5.addField("TYPE_DESCRIPTION");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5TFTYPEDESC: Type Description");
        f.setFunction("Tool_Facility_Type_API.Get_Type_Description(:TOOL_FACILITY_TYPE)");
        f.setSize(30);
        f.setMaxLength(200);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk5.addField("ITEM5_ORG_CODE");
        f.setDbName("ORG_CODE");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5ORGCODE: Maintenance Organization");
        f.setLOV("ConnectToolsFacilitiesLov.page","TOOL_FACILITY_ID,ITEM5_CONTRACT CONTRACT,TOOL_FACILITY_TYPE",600,450);
        f.setLOVProperty("TITLE",mgr.translate("PCMWACTIVESEPARATE2ITEM5LOVTITLE: Connect Tools and Facilities"));
        f.setCustomValidation("ITEM5_ORG_CODE,ITEM5_CONTRACT,TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM5_QTY,ITEM5_PLANNED_HOUR,ITEM5_WO_NO,ITEM5_NOTE,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_CONTRACT,ITEM5_CATALOG_NO_DESC,ITEM5_SALES_PRICE,ITEM5_SALES_CURRENCY,ITEM5_DISCOUNT,ITEM5_DISCOUNTED_PRICE,ITEM5_PLANNED_PRICE","ITEM5_NOTE,ITEM5_QTY,ITEM5_COST_AMOUNT,ITEM5_CATALOG_NO_CONTRACT,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_DESC,ITEM5_SALES_PRICE,ITEM5_SALES_CURRENCY,ITEM5_DISCOUNT,ITEM5_DISCOUNTED_PRICE,ITEM5_PLANNED_PRICE,TOOL_FACILITY_ID,ITEM5_CONTRACT,ITEM5_ORG_CODE");
        f.setSize(8);
        f.setMaxLength(8);
        f.setInsertable();
        f.setUpperCase();

        f = itemblk5.addField("ITEM5_QTY", "Number");
        f.setDbName("QTY");
        //Bug ID 82934, Start
        f.setLabel("PCMWACTIVESEPARATE2ITEM5QTY: Planned Quantity");
        //Bug ID 82934, End
        f.setCustomValidation("ITEM5_QTY,ITEM5_PLANNED_HOUR,TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM5_WO_NO,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_CONTRACT,ITEM5_SALES_PRICE,ITEM5_DISCOUNT","ITEM5_COST_AMOUNT,ITEM5_PLANNED_PRICE");
        f.setInsertable();

        f = itemblk5.addField("ITEM5_PLANNED_HOUR", "Number");
        f.setDbName("PLANNED_HOUR");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5PLNHRS: Planned Hours");
        f.setCustomValidation("ITEM5_PLANNED_HOUR,ITEM5_QTY,TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM5_WO_NO,ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_CONTRACT,ITEM5_SALES_PRICE,ITEM5_DISCOUNT","ITEM5_COST_AMOUNT,ITEM5_PLANNED_PRICE");
        f.setInsertable();

        f = itemblk5.addField("ITEM5_JOB_ID", "Number");
        f.setDbName("JOB_ID");
        f.setInsertable();
        f.setDefaultNotVisible();
        f.setDynamicLOV("WORK_ORDER_JOB", "ITEM5_WO_NO WO_NO");
        f.setLabel("PCMWACTIVEROUNDITEM5JOBID: Job Id");

        f = itemblk5.addField("ITEM5_CRAFT_LINE_NO", "Number");
        f.setDbName("CRAFT_LINE_NO");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5CRAFTLINENO: Operation No");
        f.setDynamicLOV("WORK_ORDER_ROLE","ITEM5_WO_NO WO_NO",600,445);
        f.setInsertable();
        f.setDefaultNotVisible();

        f = itemblk5.addField("ITEM5_PLAN_LINE_NO", "Number");
        f.setDbName("PLAN_LINE_NO");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5PLANLINENO: Plan Line No");
        f.setReadOnly();
        f.setHidden();

        f = itemblk5.addField("ITEM5_COST", "Money");
        f.setDbName("COST");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5COST: Cost");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk5.addField("ITEM5_COST_CURRENCY");
        f.setDbName("COST_CURRENCY");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5COSTCURR: Cost Currency");
        f.setSize(10);
        f.setMaxLength(3);
        f.setReadOnly();
        f.setHidden();

        f = itemblk5.addField("ITEM5_COST_AMOUNT", "Money");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5COSTAMT: Cost Amount");
        f.setFunction("Work_Order_Tool_Facility_API.Calculate_Planned_Cost_Amount(:TOOL_FACILITY_ID, :TOOL_FACILITY_TYPE, :ITEM5_WO_NO, :ITEM5_QTY, :ITEM5_PLANNED_HOUR)");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk5.addField("ITEM5_CATALOG_NO_CONTRACT");
        f.setDbName("CATALOG_NO_CONTRACT");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5CATALOGCONTRACT: Sales Part Site");
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setSize(8);
        f.setMaxLength(5);
        f.setReadOnly();
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();

        f = itemblk5.addField("ITEM5_CATALOG_NO");
        f.setDbName("CATALOG_NO");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5CATALOGNO: Sales Part");
        f.setDynamicLOV("SALES_PART_SERVICE_LOV", "ITEM5_CATALOG_NO_CONTRACT CONTRACT",600,445);
        f.setCustomValidation("ITEM5_CATALOG_NO,ITEM5_CATALOG_NO_CONTRACT,ITEM5_WO_NO,ITEM5_QTY,ITEM5_PLANNED_HOUR","ITEM5_CATALOG_NO_DESC,ITEM5_SALES_PRICE,ITEM5_SALES_CURRENCY,ITEM5_DISCOUNT,ITEM5_DISCOUNTED_PRICE,ITEM5_PLANNED_PRICE");
        f.setSize(20);
        f.setMaxLength(25);
        f.setInsertable();
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();

        f = itemblk5.addField("ITEM5_CATALOG_NO_DESC");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5CATALOGDESC: Sales Part Description");
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
        f.setLabel("PCMWACTIVESEPARATE2ITEM5SALESPRICE: Sales Price");
        f.setCustomValidation("ITEM5_SALES_PRICE,ITEM5_CATALOG_NO_CONTRACT,ITEM5_CATALOG_NO,ITEM5_WO_NO,ITEM5_QTY,ITEM5_PLANNED_HOUR,ITEM5_DISCOUNT","ITEM5_DISCOUNTED_PRICE,ITEM5_PLANNED_PRICE");
        f.setInsertable();
        f.setDefaultNotVisible();
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();

        f = itemblk5.addField("ITEM5_SALES_CURRENCY");
        f.setDbName("PRICE_CURRENCY");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5SALESCURR: Sales Currency");
        f.setSize(10);
        f.setMaxLength(3);
        f.setReadOnly();
        f.setHidden();
        f.setDefaultNotVisible();
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();

        f = itemblk5.addField("ITEM5_DISCOUNT", "Number");
        f.setDbName("DISCOUNT");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5DISCOUNT: Discount");
        f.setCustomValidation("ITEM5_DISCOUNT,ITEM5_SALES_PRICE,ITEM5_CATALOG_NO_CONTRACT,ITEM5_CATALOG_NO,ITEM5_WO_NO,ITEM5_QTY,ITEM5_PLANNED_HOUR","ITEM5_DISCOUNTED_PRICE,ITEM5_PLANNED_PRICE");
        f.setInsertable();
        f.setDefaultNotVisible();
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();

        f = itemblk5.addField("ITEM5_DISCOUNTED_PRICE", "Money");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5DISCOUNTEDPRICE: Discounted Price");
        f.setFunction("''");
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();
        else
            f.setFunction("Work_Order_Tool_Facility_API.Calculate_Discounted_Price(:ITEM5_CATALOG_NO,:ITEM5_CATALOG_NO_CONTRACT,:ITEM5_WO_NO,:ITEM5_QTY,:ITEM5_PLANNED_HOUR,:ITEM5_SALES_PRICE,:ITEM5_DISCOUNT)");

        f = itemblk5.addField("ITEM5_PLANNED_PRICE", "Money");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5PLANNEDPRICE: Planned Price Amount");
        f.setFunction("''");
        f.setReadOnly();
        f.setDefaultNotVisible();
        if (!mgr.isModuleInstalled("ORDER"))
            f.setHidden();
        else
            f.setFunction("Work_Order_Tool_Facility_API.Calculated_Price_Amount(:ITEM5_CATALOG_NO,:ITEM5_CATALOG_NO_CONTRACT,:ITEM5_WO_NO,:ITEM5_QTY,:ITEM5_PLANNED_HOUR,:ITEM5_SALES_PRICE,:ITEM5_DISCOUNT)");

        f = itemblk5.addField("FROM_DATE_TIME", "Datetime");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5FROMDATE: From Date/Time");
        f.setSize(20);
        f.setInsertable();
        f.setDefaultNotVisible();

        f = itemblk5.addField("TO_DATE_TIME", "Datetime");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5TODATE: To Date/Time");
        f.setSize(20);
        f.setInsertable();
        f.setDefaultNotVisible();

        f = itemblk5.addField("ITEM5_NOTE");
        f.setDbName("NOTE");
        f.setLabel("PCMWACTIVESEPARATE2ITEM5NOTE: Note");
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

        itemblk5.setView("WORK_ORDER_TOOL_FACILITY");
        itemblk5.defineCommand("WORK_ORDER_TOOL_FACILITY_API","New__,Modify__,Remove__");
        itemset5 = itemblk5.getASPRowSet();

        itemblk5.setMasterBlock(headblk);

        itembar5 = mgr.newASPCommandBar(itemblk5);
        itembar5.enableCommand(itembar5.FIND);
        itembar5.defineCommand(itembar5.NEWROW,"newRowITEM5");
        itembar5.defineCommand(itembar5.COUNTFIND,"countFindITEM5");
        itembar5.defineCommand(itembar5.OKFIND,"okFindITEM5");
        itembar5.defineCommand(itembar5.DUPLICATEROW,"duplicateITEM5");
        itembar5.addCustomCommand("tfbestfit",mgr.translate("PCMWACTIVESEPARATE2ITEM5BESTFIT: Best Fit Search..."));
        itembar5.addCustomCommandSeparator();
        itembar5.addCustomCommand("freeTimeSearchFromToolsAndFac",mgr.translate("PCMWWOTOOLFACFREETIMESEARCH1: Free Time Search..."));

        itemtbl5 = mgr.newASPTable(itemblk5);
        itemtbl5.setWrap();

        itemlay5 = itemblk5.getASPBlockLayout();
        itemlay5.setDefaultLayoutMode(itemlay5.MULTIROW_LAYOUT);
        itemlay5.setDialogColumns(2);  

        // ----------------------------------------------------------------------
        // -----------------------------    ITEMBLK6   --------------------------
        // ----------------------------------------------------------------------

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
        setLabel("ACTSEP2ITEM6JOBID: Job ID").
        setReadOnly().
        setInsertable().
        setMandatory();

        itemblk6.addField("STD_JOB_ID").
        setSize(15).
        setLabel("ACTSEP2ITEM6STDJOBID: Standard Job ID").
        setLOV("SeparateStandardJobLov.page", 600, 445).
        setUpperCase().
        setInsertable().
        setQueryable().
        setCustomValidation("STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION", "STD_JOB_ID,STD_JOB_REVISION,DESCRIPTION,STD_JOB_STATUS").
        setMaxLength(12);

        itemblk6.addField("STD_JOB_CONTRACT").
        setSize(10).
        setLabel("ACTSEP2ITEM6STDJOBCONTRACT: Site").
        setUpperCase().
        setReadOnly().
        setMaxLength(5);

        itemblk6.addField("STD_JOB_REVISION").
        setSize(10).
        setLabel("ACTSEP2ITEM6STDJOBREVISION: Revision").
        setDynamicLOV("SEPARATE_STANDARD_JOB_LOV", "STD_JOB_CONTRACT CONTRACT,STD_JOB_ID", 600, 445).    
        setCustomValidation("STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION", "DESCRIPTION,STD_JOB_STATUS").
        setUpperCase().
        setInsertable().
        setQueryable().
        setMaxLength(6);

        itemblk6.addField("DESCRIPTION").
        setSize(35).
        setLabel("ACTSEP2ITEM6DESCRIPTION: Description").
        setMandatory().
        setInsertable().
        setMaxLength(4000);

        itemblk6.addField("ITEM6_QTY", "Number").
        setLabel("ACTSEP2ITEM6QTY: Quantity").
        setDbName("QTY").
        setMandatory().
        setInsertable();

        itemblk6.addField("STD_JOB_STATUS").
        setLabel("ACTSEP2STDJOBSTATUS: Std Job Status").
        setFunction("Standard_Job_API.Get_Status(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)").
        setReadOnly();

        itemblk6.addField("ITEM6_COMPANY").
        setSize(20).
        setHidden().
        setDbName("COMPANY").
        setUpperCase().
        setInsertable();

        itemblk6.addField("ITEM6_SIGN_ID").
        setSize(35).
        setDbName("SIGNATURE").
        setLOV("../mscomw/MaintEmployeeLov.page","ITEM6_COMPANY COMPANY",600,450).
        setLabel("ACTSEP2ITEM6SIGNID: Executed By").
        setQueryable().
        setUpperCase().
        setCustomValidation("ITEM6_COMPANY,ITEM6_SIGN_ID","EMPLOYEE_ID,ITEM6_SIGN_ID");

        itemblk6.addField("EMPLOYEE_ID").
        setSize(15).
        setLabel("ACTSEP2ITEM6EMPLOYEEID: Employee ID").
        setUpperCase().
        setMaxLength(11).
        setReadOnly();

        //(+) Bug 66406, Start
        itemblk6.addField("CONN_PM_NO", "Number" ,"#").
        setDbName("PM_NO").
        setSize(15).
        setReadOnly().
        setCustomValidation("CONN_PM_NO,CONN_PM_REVISION","CONN_PM_NO,CONN_PM_REVISION").
        setLabel("ACTSEPNORMALCONNPMNO: PM No");

        itemblk6.addField("CONN_PM_REVISION").
        setDbName("PM_REVISION").
        setSize(15).
        setReadOnly().
        setLabel("ACTSEPNORMALCONNPMREV: PM Revision");

        itemblk6.addField("CONN_PM_JOB_ID", "Number").
        setDbName("PM_JOB_ID").  
        setSize(15).
        setReadOnly().
        setDynamicLOV("PM_ACTION_JOB", "CONN_PM_NO PM_NO, CONN_PM_REVISION PM_REVISION").
        setLabel("ACTSEPNORMALCONNPMJOBID: PM Job ID"); 
        //(+) Bug 66406, End

        itemblk6.addField("ITEM6_DATE_FROM", "Datetime").
        setSize(20).
        setDbName("DATE_FROM").
        setLabel("ACTSEP2ITEM6DATEFROM: Date From").
        setInsertable();

        itemblk6.addField("ITEM6_DATE_TO", "Datetime").
        setSize(20).
        setDbName("DATE_TO").
        setLabel("ACTSEP2ITEM6DATETO: Date To").
        setInsertable();

        itemblk6.addField("ITEM6_PLAN_HRS","Number").
        setFunction("Work_Order_Job_API.Get_Total_Plan_Hours(:ITEM6_WO_NO, :JOB_ID)").
        setReadOnly().
        setLabel("PCMWACTIVESEPARATE2ITEM6PHRS: Planned Hours");

        itemblk6.addField("STD_JOB_FLAG", "Number").
        setHidden().
        setCustomValidation("ITEM6_WO_NO,JOB_ID,STD_JOB_ID,STD_JOB_CONTRACT,STD_JOB_REVISION,ITEM6_DATE_TO,ITEM6_DATE_FROM", "N_JOB_EXIST,S_STD_JOB_EXIST,N_ROLE_EXIST,N_MAT_EXIST,N_TOOL_EXIST,N_PLANNING_EXIST,N_DOC_EXIST,S_STD_JOB_ID,S_STD_JOB_CONTRACT,S_STD_JOB_REVISION,N_QTY,S_AGREEMENT_ID,OUTEXIST,OVERLAPEXIST").
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

        itemblk6.addField("OUTEXIST").
        setFunction("''").
        setHidden();

        itemblk6.addField("OVERLAPEXIST").
        setFunction("''").
        setHidden();

        itemblk6.setView("WORK_ORDER_JOB");
        itemblk6.defineCommand("WORK_ORDER_JOB_API","New__,Modify__,Remove__");
        itemblk6.setMasterBlock(headblk);

        itemset6 = itemblk6.getASPRowSet();

        itemtbl6 = mgr.newASPTable(itemblk6);
        itemtbl6.setTitle(mgr.translate("ACTSEP2ITEM6WOJOBS: Jobs"));
        itemtbl6.setWrap();
        itemtbl6.enableRowSelect();

        itembar6 = mgr.newASPCommandBar(itemblk6);
        itembar6.enableCommand(itembar6.FIND);
        itembar6.defineCommand(itembar6.COUNTFIND,"countFindITEM6");
        itembar6.defineCommand(itembar6.OKFIND,"okFindITEM6"); 
        itembar6.defineCommand(itembar6.NEWROW, "newRowITEM6");
        itembar6.defineCommand(itembar6.SAVERETURN, "saveReturnITEM6", "checkITEM6SaveParams(i)");
        //Bug 89703, Start
        itembar6.defineCommand(itembar6.DELETE,"deleteRowITEM6");
        //Bug 89703, End
        itembar6.addCustomCommand("allocateEmployees1",mgr.translate("PCMWACTIVESEPARATE2ALLOCATEEMPLOYEES1: Allocate Employees..."));
        itembar6.addCustomCommand("selectAllocation1",mgr.translate("PCMWACTIVESEPARATE2CLEARALLOCATION1: Clear Allocation..."));
        itembar6.enableMultirowAction();

        itemlay6 = itemblk6.getASPBlockLayout();
        itemlay6.setDefaultLayoutMode(itemlay6.MULTIROW_LAYOUT);


        //------operations block under jobs tab------------------------


        itemblk7 = mgr.newASPBlock("ITEM7");

        itemblk7.addField("ITEM7_OBJID").
        setHidden().
        setDbName("OBJID");

        itemblk7.addField("ITEM7_OBJVERSION").
        setHidden().
        setDbName("OBJVERSION");

        itemblk7.addField("ITEM7_WO_NO","Number","#").
        setMandatory().
        setHidden().
        setLabel("PCMWACTIVESEPARATE2ITEM7WONO: WO No").
        setDbName("WO_NO");

        itemblk7.addField("ITEM7_ROW_NO","Number").
        setLabel("PCMWACTIVESEPARATE2ITEM7RNO: Operation No"). 
        setReadOnly().
        setDbName("ROW_NO").
        setInsertable();

        itemblk7.addField("ITEM7_COMPANY").
        setSize(18).
        setHidden().
        setLabel("PCMWACTIVESEPARATE2ITEM7COMP: Company").
        setUpperCase().
        setDbName("COMPANY").
        setMaxLength(20);

        itemblk7.addField("ITEM7_TEMP").
        setHidden().
        setFunction("''"); 

        itemblk7.addField("ITEM7_DESCRIPTION").
        setSize(29).
        setLabel("PCMWACTIVESEPARATE2ITEM7DESC: Description").
        setDbName("DESCRIPTION").
        setMaxLength(60);

        itemblk7.addField("ITEM7_SIGN").
        setSize(20).
        setLOV("../mscomw/MaintEmployeeLov.page","ITEM7_COMPANY COMPANY",600,450).
        setCustomValidation("ITEM7_COMPANY,ITEM7_SIGN,ITEM7_ROLE_CODE,ITEM7_CONTRACT,ORG_CODE,ITEM7_ORG_CODE,ITEM7_COST,ITEM7_PLAN_MEN,ITEM7_PLAN_HRS,ITEM7_ALLOCATED_HOURS,ITEM7_TOTAL_MAN_HOURS","ITEM7_SIGN_ID,ITEM7_ROLE_CODE,ITEM7_ORG_CODE,ITEM7_COST,ITEM7_COSTAMOUNT,ITEM7_SIGN,ITEM7_CONTRACT,ITEM7_ALLOCATED_HOURS,ITEM7_TOTAL_ALLOCATED_HOURS,ITEM7_TOTAL_REMAINING_HOURS,ITEM7_ORG_CODE_DESC,ITEM7_ROLE_CODE_DESC").
        setLOVProperty("TITLE","PCMWACTIVESEPARATE2LOVTITLE7: List of Employee").
        setLabel("PCMWACTIVESEPARATE2ITEM7SIGN: Executed By").
        setUpperCase().
        setDbName("SIGN").
        setMaxLength(20);

        itemblk7.addField("ITEM7_SIGN_ID").
        setSize(18).
        setMaxLength(11).
        setLabel("PCMWACTIVESEPARATE2ITEM7SIGNID: Employee ID").
        setCustomValidation("ITEM7_SIGN_ID,ITEM7_COMPANY,ITEM7_ROLE_CODE","ITEM7_SIGN,ITEM7_ROLE_CODE,ITEM7_ORG_CODE").
        setUpperCase().
        setDbName("SIGN_ID").
        setReadOnly();

        itemblk7.addField("ITEM7_ORG_CODE").
        setSize(8).
        setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ITEM7_CONTRACT CONTRACT",600,450).
        setCustomValidation("ITEM7_CONTRACT,ITEM7_ORG_CODE,ITEM7_ROLE_CODE,ITEM7_PLAN_MEN,ITEM7_PLAN_HRS,ITEM7_SIGN,ITEM7_WO_NO","ITEM7_COST,ITEM7_COSTAMOUNT,ITEM7_ORG_CODE,ITEM7_CONTRACT,ITEM7_ORG_CODE_DESC").
        setLabel("PCMWACTIVESEPARATE2ITEM7ORGCODE: Maint.Org.").
        setUpperCase().
        setDbName("ORG_CODE").
        setMaxLength(8);

          //Bug ID 59224,start
        itemblk7.addField("ITEM7_ORG_CODE_DESC").
        setLabel("PCMWACTIVESEPARATE7ORGCODEDESC: Maint. Org. Description").
        setFunction("Organization_Api.Get_Description(:ITEM7_CONTRACT,:ITEM7_ORG_CODE)").
        setReadOnly();
        //Bug ID 59224,end
        itemblk7.addField("ITEM7_CONTRACT").
        setSize(8).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setLabel("PCMWACTIVESEPARATE2ITEM7CONTRACT: Maint.Org. Site").
        setUpperCase().
        setDbName("CONTRACT").
        setMaxLength(5);

        //Bug ID 59224,start

        itemblk7.addField("ITEM7_ROLE_CODE").
        setSize(10).
        setDynamicLOV("ROLE_TO_SITE_LOV","ITEM7_CONTRACT CONTRACT",600,445).
        setCustomValidation("ITEM7_ROLE_CODE,ITEM7_CONTRACT,ITEM7_ORG_CODE,ITEM7_PLAN_MEN,ITEM7_PLAN_HRS,ITEM7_WO_NO","ITEM7_COST,ITEM7_COSTAMOUNT,ITEM7_ROLE_CODE,ITEM7_CONTRACT,ITEM7_ROLE_CODE_DESC").
        setLabel("PCMWACTIVESEPARATE2ITEM7ROLECODE: Craft ID").
        setUpperCase().
        setDbName("ROLE_CODE").
        setMaxLength(10);

        itemblk7.addField("ITEM7_ROLE_CODE_DESC").
        setLabel("PCMWACTIVESEPARATE7ROLECODEDESC: Craft Description").
        setFunction("ROLE_API.Get_Description(:ITEM7_ROLE_CODE)").
        setReadOnly();
       //Bug ID 59224,end
        itemblk7.addField("ITEM7_PLAN_MEN","Number").
        setCustomValidation("ITEM7_PLAN_MEN,ITEM7_PLAN_HRS,ITEM7_COST,ITEM7_CONTRACT,ITEM7_ORG_CODE,ITEM7_ROLE_CODE,ITEM7_TOTAL_ALLOCATED_HOURS","ITEM7_COST,ITEM7_COSTAMOUNT,ITEM7_TOTAL_MAN_HOURS,ITEM7_TOTAL_REMAINING_HOURS").
        setDbName("PLAN_MEN").
        setLabel("PCMWACTIVESEPARATE2ITEM7PMEN: Planned Men");

        itemblk7.addField("ITEM7_PLAN_HRS","Number").
        setCustomValidation("ITEM7_PLAN_MEN,ITEM7_PLAN_HRS,ITEM7_COST,ITEM7_CONTRACT,ITEM7_ORG_CODE,ITEM7_ROLE_CODE,ITEM7_TOTAL_ALLOCATED_HOURS","ITEM7_COST,ITEM7_COSTAMOUNT,ITEM7_TOTAL_MAN_HOURS,ITEM7_TOTAL_REMAINING_HOURS").
        setLabel("PCMWACTIVESEPARATE2ITEM2ITEM7PLANHRS: Planned Hours").
        setDbName("PLAN_HRS");

        itemblk7.addField("ITEM7_TOTAL_MAN_HOURS","Number").
        setLabel("PCMWACTIVESEPARATE2ITEM7TOTMANHRS: Total Man Hours").
        setFunction(":PLAN_MEN * :PLAN_HRS").
        setReadOnly();

        itemblk7.addField("ITEM7_ALLOCATED_HOURS","Number").
        setDbName("ALLOCATED_HOURS").
        setCustomValidation("ITEM7_ALLOCATED_HOURS,ITEM7_TOTAL_MAN_HOURS","ITEM7_TOTAL_ALLOCATED_HOURS,ITEM7_TOTAL_REMAINING_HOURS").
        setLabel("PCMWACTIVESEPARATE2ITEM7ALLOCATEDHRS: Allocated Hours");

        itemblk7.addField("ITEM7_TOTAL_ALLOCATED_HOURS","Number").
        setLabel("PCMWACTIVESEPARATE2TOTALLOCATEDHRS: Total Allocated Hours").
        setFunction("Work_Order_Role_API.Get_Total_Alloc(:WO_NO,:ROW_NO)").
        setReadOnly();

        itemblk7.addField("ITEM7_TOTAL_REMAINING_HOURS","Number").
        setLabel("PCMWACTIVESEPARATE2TOTALREMAININGHRS: Total Remaining Hours").
        setFunction("(:PLAN_MEN * :PLAN_HRS) - Work_Order_Role_API.Get_Total_Alloc(:WO_NO,:ROW_NO)").
        setReadOnly();

        itemblk7.addField("ITEM7_TEAM_CONTRACT").
        setSize(7).
        setDynamicLOV("USER_ALLOWED_SITE_LOV","ITEM7_COMPANY COMPANY",600,450).
        setLabel("PCMWACTIVESEPARATE2ITEM7CONT: Team Site").
        setMaxLength(5).
        setDbName("TEAM_CONTRACT").
        setInsertable().
        setUpperCase();

        itemblk7.addField("ITEM7_TEAM_ID").
        setCustomValidation("ITEM7_TEAM_ID,ITEM7_TEAM_CONTRACT","ITEM7_TEAMDESC").
        setSize(13).
        setDynamicLOV("MAINT_TEAM","ITEM7_TEAM_CONTRACT TEAM_CONTRACT",600,450).
        setDbName("TEAM_ID").
        setMaxLength(20).
        setInsertable().
        setLabel("PCMWACTIVESEPARATE2ITEM7TID: Team ID").
        setUpperCase();

        itemblk7.addField("ITEM7_TEAMDESC").
        setFunction("Maint_Team_API.Get_Description(:TEAM_ID,:TEAM_CONTRACT)").
        setSize(40).
        setMaxLength(200).
        setLabel("PCMWACTIVESEPARATE2ITEM7DESC: Description").
        setReadOnly();

        itemblk7.addField("ITEM7_CATALOG_CONTRACT").
        setSize(8).
        setCustomValidation("ITEM7_CATALOG_CONTRACT","ITEM7_COMPANY").
        setHidden().
        setDbName("CATALOG_CONTRACT").
        setUpperCase();

        itemblk7.addField("ITEM7_CATALOG_NO").
        setDbName("CATALOG_NO").
        setSize(17).
        setHidden().
        setUpperCase().
        setMaxLength(25);

        itemblk7.addField("ITEM7_COST","Money","#.##").
        setLabel("PCMWACTIVESEPARATE2ITEM7COST: Cost").
        setDefaultNotVisible().
        setFunction("nvl(Role_API.Get_Role_Cost(:ROLE_CODE),Organization_API.Get_Org_Cost(:CONTRACT,:ORG_CODE))").
        setReadOnly(); 

        itemblk7.addField("ITEM7_COSTAMOUNT","Money","#.##").
        setLabel("PCMWACTIVESEPARATE2ITEM7COSTAMT: Cost Amount").
        setFunction("NVL(:PLAN_MEN,1)*:PLAN_HRS*(WORK_ORDER_PLANNING_UTIL_API.Get_Cost(:ORG_CODE,:ROLE_CODE,:CATALOG_CONTRACT,:CATALOG_NO,:CONTRACT))").
        setDefaultNotVisible().
        setReadOnly();

        itemblk7.addField("ITEM7_JOB_ID", "Number").
        setDbName("JOB_ID").
        setInsertable().
        setHidden().
        setCustomValidation("ITEM7_COMPANY,ITEM7_WO_NO,ITEM7_JOB_ID","ITEM7_SIGN_ID,ITEM7_SIGN").
        setLabel("PCMWACTIVESEPARATE2ITEM7JOBID: Job Id");

        itemblk7.addField("ITEM7_DATE_FROM","Datetime").
        setSize(22).
        setLabel("PCMWACTIVESEPARATE2ITEM7DATEFROM: Date from").
        setDefaultNotVisible().
        setCustomValidation("ITEM7_DATE_FROM","ITEM7_DATE_FROM_MASKED").
        setDbName("DATE_FROM");

        itemblk7.addField("ITEM7_DATE_FROM_MASKED","Datetime", getDateTimeFormat("JAVA")).
        setHidden().
        setFunction("''");

        itemblk7.addField("ITEM7_DATE_TO","Datetime").
        setSize(22).
        setLabel("PCMWACTIVESEPARATE2ITEM7DATETO: Date to").
        setDefaultNotVisible().
        setDbName("DATE_TO");

        itemblk7.addField("ITEM7_PREDECESSOR").
        setSize(22).
        setLabel("PCMWACTIVESEPARATE2ITEM7PRED: Predecessors").
        setLOV("ConnectPredecessorsDlg.page",600,450).
        setFunction("Wo_Role_Dependencies_API.Get_Predecessors(:WO_NO,:ROW_NO)");

        itemblk7.addField("ITEM7_TOOLS").
        setSize(20).
        setLabel("PCMWACTIVESEPARATE2ITEM7TOOLS: Tools").
        setDefaultNotVisible().
        setDbName("TOOLS").
        setMaxLength(25);

        itemblk7.addField("ITEM7_REMARK").
        setSize(29).
        setLabel("PCMWACTIVESEPARATE2ITEM7REMARK: Remark").
        setDefaultNotVisible().
        setMaxLength(2000).
        setDbName("REMARK").
        setHeight(4);

        itemblk7.addField("ITEM7_ORG_CONTRACT").
        setFunction("''").
        setHidden();

        itemblk7.addField("ITEM7_BASE_UNIT_PRICE").
        setFunction("''").
        setHidden();

        itemblk7.addField("ITEM7_SALE_UNIT_PRICE").
        setFunction("''").
        setHidden();

        itemblk7.addField("ITEM7_DISCOUNT","Number").
        setFunction("''").
        setHidden();

        itemblk7.addField("ITEM7_CURRENCY_RATE").
        setFunction("''").
        setHidden();

        itemblk7.addField("ITEM7_BUY_QTY_DUE").
        setFunction("''").
        setHidden();

        itemblk7.addField("ITEM7_TEMP1").
        setFunction("''").
        setHidden();

        itemblk7.addField("ITEM7_COMPANY_VAR").
        setHidden().
        setFunction("''");

        itemblk7.addField("ITEM7_INSTRUCTION_TYPE").
        setSize(10).
        setDynamicLOV("INSTRUCTION_TYPE",600,450).
        setLabel("PCMWACTIVESEPARATE2ITEM7INSTTYPE: Instruction Type"). 
        setUpperCase(). 
        setDefaultNotVisible().
        setDbName("INSTRUCTION_TYPE").
        setMaxLength(10);

        itemblk7.addField("ITEM7_LOCATION").
        setSize(10).
        setDynamicLOV("INSTRUCTION_LOCATION",600,450).
        setLabel("PCMWACTIVESEPARATE2ITEM7LOCATION: Location").
        setUpperCase().
        setDefaultNotVisible().
        setDbName("LOCATION").
        setMaxLength(10);

        itemblk7.addField("ITEM7_SIGN_REQUIREMENT").
        setSize(10).
        setDynamicLOV("OPERATION_SIGN_REQUIREMENT",600,450).
        setLabel("PCMWACTIVESEPARATE2ITEM7SIGNREQ: Sign Requirement"). 
        setUpperCase(). 
        setDefaultNotVisible().
        setDbName("SIGN_REQUIREMENT").
        setMaxLength(10);

        itemblk7.addField("ITEM7_REFERENCE_NUMBER").
        setSize(25).
        setLabel("PCMWACTIVESEPARATE2ITEM7REF: Reference Number"). 
        setDefaultNotVisible().
        setDbName("REFERENCE_NUMBER").
        setMaxLength(25);

        itemblk7.addField("ITEM7_PRODUCT_NO").
        setSize(25).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATE2ITEM7PRODNO: Product No"). 
        setDefaultNotVisible(). 
        setDbName("PRODUCT_NO").
        setMaxLength(25);

        itemblk7.addField("ITEM7_MODEL_NO").
        setSize(25).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATE2ITEM7MODELNO: Model No"). 
        setDefaultNotVisible(). 
        setDbName("MODEL_NO").
        setMaxLength(25);

        itemblk7.addField("ITEM7_FUNCTION_NO").
        setSize(25).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATE2ITEM7FUNCNO: Function No"). 
        setDefaultNotVisible(). 
        setDbName("FUNCTION_NO").
        setMaxLength(25);

        itemblk7.addField("ITEM7_SUB_FUNCTION_NO").
        setSize(25).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATE2ITEM7SUBFUNCNO: Sub Function No"). 
        setDefaultNotVisible(). 
        setDbName("SUB_FUNCTION_NO").
        setMaxLength(25);

        itemblk7.addField("ITEM7_BOTTOM_FUNCTION_NO").
        setSize(25).
        setReadOnly().
        setLabel("PCMWACTIVESEPARATE2ITEM7BOTFUNCNO: Bottom Function No"). 
        setDefaultNotVisible(). 
        setDbName("BOTTOM_FUNCTION_NO").
        setMaxLength(25);

        itemblk7.addField("ITEM7_CBADDQUALIFICATION").
        setLabel("PCMWACTIVESEPARATE2ITEM7CBADDQUAL7: Additional Qualifications").
        setFunction("Work_Order_Role_API.Check_Qualifications_Exist(:WO_NO,:ROW_NO)").
        setCheckBox("0,1").
        setReadOnly().
        setQueryable();

        itemblk7.setView("WORK_ORDER_ROLE");
        itemblk7.defineCommand("WORK_ORDER_ROLE_API","New__,Modify__,Remove__");
        itemset7 = itemblk7.getASPRowSet();

        itemblk7.setMasterBlock(itemblk6);

        itembar7 = mgr.newASPCommandBar(itemblk7);
        itembar7.enableCommand(itembar7.FIND);
        itembar7.defineCommand(itembar7.NEWROW,"newRowITEM7");
        itembar7.defineCommand(itembar7.EDITROW, "editRowITEM7");
        itembar7.defineCommand(itembar7.COUNTFIND,"countFindITEM7");
        itembar7.defineCommand(itembar7.OKFIND,"okFindITEM7");

        itembar7.defineCommand(itembar7.SAVERETURN,"saveReturnITEM7","checkMandoItem7();Item7PlanMenCheck()");
        itembar7.defineCommand(itembar7.SAVENEW,null,"checkMandoItem7()");

        itembar7.addCustomCommand("freeTimeSearchFromOperations2",mgr.translate("PCMWACTIVESEPARATE2SMFREETIMESEARCH21: Free Time Search..."));
        itembar7.addCustomCommand("allocateEmployees2",mgr.translate("PCMWACTIVESEPARATE2ALLOCATEEMPLOYEES2: Allocate Employees..."));
        itembar7.addCustomCommand("selectAllocation2",mgr.translate("PCMWACTIVESEPARATE2CLEARALLOCATION2: Clear Allocation..."));
        itembar7.addCustomCommand("connectExistingOperations",mgr.translate("PCMWACTIVESEPARATE2CONEXISTINGOPER: Connect Existing Operation..."));
        itembar7.addCustomCommand("disconnectOperations",mgr.translate("PCMWACTIVESEPARATE2DISCONEXISTINGOPER: Disconnect Operation..."));
        itembar7.addCustomCommand("additionalQualifications2",mgr.translate("PCMWACTIVESEPARATE2ADDQUALIFICATIONS2: Additional Qualifications..."));
        itembar7.addCustomCommand("predecessors2",mgr.translate("PCMWACTIVESEPARATE2ADDPREDECESSORS2: Predecessors..."));
        itembar7.addCustomCommand("resheduleOperations2",mgr.translate("PCMWACTIVESEPARATE2RESHEDULEOPERATIONSITEM7: Reschedule Operations..."));
        itembar7.addCustomCommand("moveOperation2",mgr.translate("PCMWACTIVESEPARATE2MOVEOPERATION2: Move Operation..."));
        itembar7.forceEnableMultiActionCommand("connectExistingOperations");
        itembar7.removeFromMultirowAction("additionalQualifications2");
        itembar7.removeFromMultirowAction("resheduleOperations2");
        itembar7.removeFromMultirowAction("moveOperation2");
        itembar7.removeFromMultirowAction("freeTimeSearchFromOperations2");
        //Bug 90872, Start
        itembar7.addCommandValidConditions("resheduleOperations2",    "DATE_FROM",  "Disable",  "");
        itembar7.appendCommandValidConditions("resheduleOperations2",    "DATE_TO",  "Disable",  "");
        //Bug 90872, End
        itembar7.addCommandValidConditions("moveOperation2",    "DATE_FROM",  "Disable",  "");                              

        itemtbl7 = mgr.newASPTable(itemblk7);
        itemtbl7.setWrap();
        itemtbl7.enableRowSelect();
        itembar7.enableMultirowAction();

        itemlay7 = itemblk7.getASPBlockLayout();
        itemlay7.setDefaultLayoutMode(itemlay7.MULTIROW_LAYOUT);
        itemlay7.setDialogColumns(3);  


        //-------------------operations block for jobs end-------


        //---------------Operations----------------------------------------------

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
        f = blkPost.addField("CODE_A");
        f = blkPost.addField("CODE_B");
        f = blkPost.addField("CONTROL_TYPE");
        f = blkPost.addField("STR_CODE");

        //--------------------------------------------------------------------------
        //--------------------------------------------------------------------------

        teblk = mgr.newASPBlock("TE");

        teblk.addField("ORGCOST");
        teblk.addField("ROLECOST");
        teblk.addField("ITEM7_ORGCOST");
        teblk.addField("ITEM7_ROLECOST");

	enableConvertGettoPost();
    }

    public void checkObjAvaileble()
    {
        if (!again)
        {
            ASPManager mgr = getASPManager();

            ASPBuffer availObj;
            trans.clear();
            // 107820
            trans.addSecurityQuery("WORK_ORDER_FROM_VIM,WORK_ORDER_REQUIS_HEADER,SEPARATE_WORK_ORDER,WORK_ORDER_BUDGET,WORK_ORDER_REPORT_PAGE,WORK_ORDER_PERMIT,WORK_ORDER_PLANNING,OBJ_SUPP_WARRANTY_TYPE,PRE_ACCOUNTING,EQUIPMENT_OBJECT_SPARE,PM_ACTION,EQUIPMENT_SPARE_STRUCTURE,INVENTORY_PART_IN_STOCK_NOPAL,WORK_ORDER_PART_ALLOC,INVENTORY_PART_CONFIG,PURCHASE_PART_SUPPLIER,INVENTORY_TRANSACTION_HIST,PURCHASE_PART_SUPPLIER,INVENTORY_PART_IN_STOCK,COMPETENCY_GROUP_LOV1");
            trans.addSecurityQuery("Wo_Order_RPI","Report_Printout");
            trans.addSecurityQuery("MAINT_MATERIAL_REQUISITION_RPI","Report_Printout");
            trans.addSecurityQuery("MAINT_MATERIAL_REQ_LINE_API","Make_Reservation_Short,Make_Auto_Issue_Detail,Make_Manual_Issue_Detail");
            // 107820
            trans.addPresentationObjectQuery("PCMW/WorkOrderRequisHeaderRMB.page,PCMW/SeparateWorkOrder.page,PCMW/WorkOrderBudget.page,PCMW/WorkOrderReportPageOvw2.page,PCMW/WorkOrderPermit.page,PCMW/WorkOrderPlanning.page,PCMW/SupplierWarrantyType.page,MPCCOW/PreAccountingDlg.page,PCMW/MaintenanceObject2.page,PCMW/MaintenaceObject3.page,PCMW/RMBEquipmentSpareStructure.page,PCMW/MaterialRequisReservatDlg.page,PCMW/MaterialRequisReservatDlg2.page,INVENW/InventoryPartAvailabilityPlanningQry.page,PURCHW/PurchasePartSupplier.page,PCMW/ActiveWorkOrder.page,PURCHW/PurchasePartSupplier.page,PCMW/ActiveWorkOrderIssue.page,invenw/InventoryPartInStockOvw.page,PCMW/MainTask.page,EQUIPW/EquipmentSpareStructure3.page,PCMW/InventoryPartLocationDlg.page");

            trans = mgr.perform(trans);

            availObj = trans.getSecurityInfo();

            if (availObj.itemExists("WORK_ORDER_REQUIS_HEADER") && availObj.namedItemExists("PCMW/WorkOrderRequisHeaderRMB.page"))
                actEna1 = true;

            if (availObj.itemExists("SEPARATE_WORK_ORDER") && availObj.namedItemExists("PCMW/SeparateWorkOrder.page"))
                actEna2 = true;

            if (availObj.itemExists("PRE_ACCOUNTING") && availObj.namedItemExists("MPCCOW/PreAccountingDlg.page"))
                actEna3 = true;

            if (availObj.itemExists("WORK_ORDER_BUDGET") && availObj.namedItemExists("PCMW/WorkOrderBudget.page"))
                actEna5 = true;

            if (availObj.itemExists("WORK_ORDER_REPORT_PAGE") && availObj.namedItemExists("PCMW/WorkOrderReportPageOvw2.page"))
                actEna6 = true;

            if (availObj.itemExists("WORK_ORDER_PERMIT") && availObj.namedItemExists("PCMW/WorkOrderPermit.page"))
                actEna7 = true;

            if (availObj.itemExists("WORK_ORDER_PLANNING") && availObj.namedItemExists("PCMW/WorkOrderPlanning.page"))
                actEna8 = true;

            if (availObj.itemExists("OBJ_SUPP_WARRANTY_TYPE") && availObj.namedItemExists("PCMW/SupplierWarrantyType.page"))
                actEna9 = true;

            if (availObj.itemExists("Wo_Order_RPI.Report_Printout"))
                actEna10 = true;

            if (availObj.itemExists("PRE_ACCOUNTING") && availObj.namedItemExists("MPCCOW/PreAccountingDlg.page"))
                actEna11 = true;

            if (availObj.itemExists("MAINT_MATERIAL_REQUISITION_RPI.Report_Printout"))
                actEna12 = true;

            if (availObj.itemExists("EQUIPMENT_OBJECT_SPARE") && availObj.namedItemExists("PCMW/MaintenanceObject2.page"))
                actEna13 = true;

            if (availObj.itemExists("PM_ACTION") && availObj.namedItemExists("PCMW/MaintenaceObject3.page"))
                actEna14 = true;

            if (availObj.itemExists("EQUIPMENT_SPARE_STRUCTURE") && availObj.namedItemExists("EQUIPW/EquipmentSpareStructure3.page"))
                actEna15 = true;

            if (availObj.itemExists("MAINT_MATERIAL_REQ_LINE_API.Make_Reservation_Short"))
                actEna16 = true;

            if (availObj.itemExists("INVENTORY_PART_IN_STOCK_NOPAL") && availObj.namedItemExists("PCMW/MaterialRequisReservatDlg.page"))
                actEna17 = true;

            if (availObj.itemExists("WORK_ORDER_PART_ALLOC") && availObj.namedItemExists("PCMW/MaterialRequisReservatDlg2.page"))
                actEna18 = true;

            if (availObj.itemExists("MAINT_MATERIAL_REQ_LINE_API.Make_Auto_Issue_Detail"))
                actEna19 = true;

            if (availObj.itemExists("INVENTORY_PART_CONFIG") && availObj.namedItemExists("INVENW/InventoryPartAvailabilityPlanningQry.page"))
                actEna20 = true;

            if (availObj.itemExists("PURCHASE_PART_SUPPLIER") && availObj.namedItemExists("PURCHW/PurchasePartSupplier.page"))
                actEna21 = true;

            if (availObj.itemExists("INVENTORY_TRANSACTION_HIST") && availObj.namedItemExists("PCMW/ActiveWorkOrder.page"))
                actEna22 = true;

            if (availObj.itemExists("INVENTORY_PART_IN_STOCK") && availObj.namedItemExists("invenw/InventoryPartInStockOvw.page"))
                actEna23 = true;

            if (availObj.itemExists("INVENTORY_TRANSACTION_HIST") && availObj.namedItemExists("PCMW/ActiveWorkOrderIssue.page"))
                actEna24 = true;
            if (availObj.itemExists("COMPETENCY_GROUP_LOV1"))
                actEna25 = true;
            
            if ( availObj.itemExists("INVENTORY_PART_IN_STOCK_NOPAL") && availObj.namedItemExists("PCMW/InventoryPartLocationDlg.page") && availObj.itemExists("MAINT_MATERIAL_REQ_LINE_API.Make_Manual_Issue_Detail"))
                actEna26 = true;

            again = true;
        }
    }

    // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
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
    // 040108  ARWILK  End  (Enable Multirow RMB actions)

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

    public void activateToolsAndFacilities()
    {
        tabs.setActiveTab(3);
        okFindITEM5();
    }

    public void activateJobs()
    {
        tabs.setActiveTab(4);
        okFindITEM6();
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
            int matLineNum = Integer.parseInt(matLine);

            itemset3.goTo(matLineNum);
            okFindITEM4();
            matSingleMode = false;
            showMat = "TRUE";
        }

        if (headset.countRows() > 0 && headlay.isSingleLayout())
        {

            if (itemset.countRows()==0)
            {
                if (mgr.isPresentationObjectInstalled("invenw/InventoryPartAvailabilityPlanningQry.page"))
                    itembar.removeCustomCommand("availDetail");
                if (mgr.isPresentationObjectInstalled("purchw/PurchasePartSupplier.page"))
                    itembar.removeCustomCommand("supPerPart");
                itembar.removeCustomCommand("reserve");
                itembar.removeCustomCommand("unreserve");
                itembar.removeCustomCommand("issue");
                itembar.removeCustomCommand("manIssue");
                itembar.removeCustomCommand("matReqUnissue");
                itembar.removeCustomCommand("matReqIssued");    

            }
            if (itemlay7.isNewLayout()  && (itemset6.countRows() > 0))
                mgr.getASPField("ITEM7_PREDECESSOR").setReadOnly();

            if (itemlay2.isSingleLayout() && (itemset2.countRows() > 0))
            {
                /*int planMen,planHrs;
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
                   itembar2.removeCustomCommand("allocateEmployees0");   */ 
                if (!mgr.isEmpty(itemset2.getValue("SIGN_ID")))
                    itembar8.disableCommand(itembar8.NEWROW);



            }
            /*if (itemlay7.isSingleLayout() &&(itemset7.countRows() > 0)) 
            {
                int planMen,planHrs;
                if (mgr.isEmpty(itemset7.getValue("PLAN_MEN"))) 
                    planMen =0; 
                else 
                    planMen= Integer.parseInt(itemset7.getValue("PLAN_MEN"));

                if (mgr.isEmpty(itemset7.getValue("PLAN_HRS"))) 
                    planHrs =0;
                else 
                    planHrs= Integer.parseInt(itemset7.getValue("PLAN_HRS"));

                
                int manHrs = planMen * planHrs;
                
                if ((manHrs == 0) && (!mgr.isEmpty(itemset7.getValue("SIGN_ID")))) 
                   itembar7.removeCustomCommand("allocateEmployees2");    
            } */



            if (itemlay2.isNewLayout())
                mgr.getASPField("PREDECESSOR").setReadOnly();

            if (itemset6.countRows() > 0)
            {
                if (itemlay7.isEditLayout())
                    planMen = itemset7.getValue("PLAN_MEN");

            }

            if (itemlay2.isEditLayout())
                planMen = itemset2.getValue("PLAN_MEN");



        }

        // 031204  ARWILK  Begin  (Replace blocks with tabs)
        headbar.removeCustomCommand("activateOperations");
        headbar.removeCustomCommand("activateMaterial");
        headbar.removeCustomCommand("activateToolsAndFacilities");
        headbar.removeCustomCommand("activateJobs");
        // 031204  ARWILK  End  (Replace blocks with tabs)

        if (!actEna1)
            headbar.removeCustomCommand("requisitions");
        if (!actEna2)
            headbar.removeCustomCommand("structure");
        if (!actEna3)
            headbar.removeCustomCommand("prePost");
        if (!actEna5)
            headbar.removeCustomCommand("budget");
        if (!actEna6)
            headbar.removeCustomCommand("freeNotes");
        if (!actEna7)
            headbar.removeCustomCommand("permits");
        if (!actEna8)
            headbar.removeCustomCommand("planning");
        if (!actEna9)
            headbar.removeCustomCommand("suppWarr");
        if (!actEna10)
            headbar.removeCustomCommand("pickListForWork");
        if (!actEna12)
            itembar3.removeCustomCommand("printPicList");
        if (!actEna13)
            itembar.removeCustomCommand("sparePartObject");
        if (!actEna14)
            itembar.removeCustomCommand("objStructure");
        if (!actEna15)
            itembar.removeCustomCommand("detchedPartList");
        if (!actEna16)
            itembar.removeCustomCommand("reserve");
        if (!actEna17)
            itembar.removeCustomCommand("manReserve");
        if (!actEna18)
            itembar.removeCustomCommand("unreserve");
        if (!actEna19)
            itembar.removeCustomCommand("issue");

        if (mgr.isPresentationObjectInstalled("invenw/InventoryPartAvailabilityPlanningQry.page"))
        {
            if (!actEna20)
                itembar.removeCustomCommand("availDetail");
        }

        if (mgr.isPresentationObjectInstalled("purchw/PurchasePartSupplier.page"))
        {
            if (!actEna21)
                itembar.removeCustomCommand("supPerPart");
        }

        if (!actEna22)
            itembar.removeCustomCommand("matReqUnissue");


        if (mgr.isPresentationObjectInstalled("invenw/InventoryPartInStockOvw.page"))
        {
            if (!actEna23)
                itembar.removeCustomCommand("availtoreserve");
        }

        if (!actEna24)
            itembar.removeCustomCommand("matReqIssued");

        if (!actEna25)
        {
            itembar2.removeCustomCommand("additionalQualifications1");
            itembar7.removeCustomCommand("additionalQualifications2");
        }

        if ( !actEna26 )
         itembar.removeCustomCommand("manIssue");

        if ( !(mgr.isModuleInstalled("VIM")) )
            headbar.removeCustomCommand("maintTask");

        if (mgr.isPresentationObjectInstalled("equipw/EquipmentAllObjectLightRedirect.page"))
            mgr.getASPField("MCH_CODE").setHyperlink("../equipw/EquipmentAllObjectLightRedirect.page","MCH_CODE,MCH_CODE_CONTRACT,WO_NO","NEWWIN");
        if (mgr.isPresentationObjectInstalled("docmaw/DocReference.page"))
        {
            mgr.getASPField("DOCUMENT").setHyperlink("../docmaw/DocReference.page","LU_NAME,KEY_REF","NEWWIN");
            mgr.getASPField("CBHASDOCNTS").setHyperlink("../docmaw/DocReference.page","LU_NAME,KEY_REF","NEWWIN");
        }

        if (headset.countRows() == 1)
            headbar.disableCommand(headbar.BACK);

	//Bug 87935, Start, Rewrite the code for setting LOV and LOV properties so that correct pres objects are generated
        if (headlay.isFindLayout())
        {
            mgr.getASPField("CBHASAGRMNT").setHidden();
            mgr.getASPField("MCH_CODE").setDynamicLOV("MAINTENANCE_OBJECT");                                                        
        }
        else
	{
	   mgr.getASPField("MCH_CODE").setLOV("MaintenanceObjectLov1.page","MCH_CODE_CONTRACT CONTRACT, CONNECTION_TYPE CONNECTION_TYPE");
	   mgr.getASPField("MCH_CODE").setLOVProperty("WHERE","(OBJ_LEVEL IS NULL OR (OBJ_LEVEL IS NOT NULL AND Equipment_Object_Level_API.Get_Create_Wo(OBJ_LEVEL) = 'TRUE'))");                                                       
	}
	//Bug 87935, End
 
        if (headlay.isSingleLayout() && headset.countRows()>0)
        {
            setCheckBoxValues();

            if ("VIM".equals(headset.getValue("CONNECTION_TYPE_DB")))
                mgr.getASPField("MCH_CODE_CONTRACT").setHidden();
        }

        if (itemlay2.isNewLayout() || itemlay2.isEditLayout() || itemlay3.isNewLayout() || itemlay3.isEditLayout() || 
	    itemlay5.isNewLayout() || itemlay5.isEditLayout() || itemlay2.isFindLayout()|| itemlay3.isFindLayout() ||
	    itemlay5.isFindLayout()|| itemlay6.isEditLayout() || itemlay6.isNewLayout() )
        {
            headbar.disableCommand(headbar.DELETE);
            headbar.disableCommand(headbar.NEWROW);
            headbar.disableCommand(headbar.EDITROW);
            headbar.disableCommand(headbar.DELETE);
            headbar.disableCommand(headbar.DUPLICATEROW);
            headbar.disableCommand(headbar.FIND);
            headbar.disableCommand(headbar.BACKWARD);
            headbar.disableCommand(headbar.FORWARD);

            headbar.removeCustomCommand("requisitions");
            headbar.removeCustomCommand("structure");
            headbar.removeCustomCommand("budget");
            headbar.removeCustomCommand("changeConditionCode");
            headbar.removeCustomCommand("prePost");
            headbar.removeCustomCommand("freeNotes");
            headbar.removeCustomCommand("permits");
            headbar.removeCustomCommand("repInWiz");
            headbar.removeCustomCommand("workCenterLoad");
            headbar.removeCustomCommand("planning");
            headbar.removeCustomCommand("coInformation");
            headbar.removeCustomCommand("observed");
            headbar.removeCustomCommand("underPrep");
            headbar.removeCustomCommand("prepared");
            headbar.removeCustomCommand("released");
            headbar.removeCustomCommand("started");
            headbar.removeCustomCommand("workDone");
            headbar.removeCustomCommand("reported");
            headbar.removeCustomCommand("finished");
            headbar.removeCustomCommand("cancelled");
            headbar.removeCustomCommand("suppWarr");
            headbar.removeCustomCommand("turnToRepOrd");
            headbar.removeCustomCommand("maintTask");

        }

        if (headlay.isNewLayout() || headlay.isEditLayout())
            mgr.getASPField("CUSTOMER_NO").setHidden();

        if (headlay.isSingleLayout() && headset.countRows()>0)
            title_ = mgr.translate("PCMWACTIVESEPARATE2TRE: Prepare Work Order");
        else
            title_ = mgr.translate("PCMWACTIVESEPARATE2TRBE: Prepare Work Order");

        if (itemlay.isVisible() || "TRUE".equals(showMat))
        {
            if (itemset3.countRows()>0)
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

        if (itemlay3.isVisible() && (itemset3.countRows() > 0))
        {
            mgr.getASPField("ITEM3_CONTRACT").setLOVProperty("WHERE","Site_API.Get_Company(CONTRACT) = Site_API.Get_Company('"+headset.getRow().getValue("CONTRACT")+"')");
        }

        headbar.removeCustomCommand("refreshForm");
        headbar.removeCustomCommand("refreshMaterialTab");

        //(+) Bug 66406, Start
        if (itemlay6.isFindLayout())
           mgr.getASPField("CONN_PM_NO").setLOV("../pcmw/PmActionLov1.page",600,450);
        else if (itemlay6.isNewLayout()) 
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
        return "PCMWACTIVESEPARATE2TITLE: Prepare Work Order";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();
        
        //Bug 89703, Start
        printHiddenField("HASPLANNING","");
        //Bug 89703, End

        if (headlay.isSingleLayout() && headset.countRows() > 0)
        {

            if (itemlay7.isEditLayout() && (itemset7.countRows() > 0))
            {
                appendDirtyJavaScript("   f.ITEM7_PREDECESSOR.disabled = true;\n");

            }
            if (itemlay2.isEditLayout() && (itemset2.countRows() > 0))
            {
                appendDirtyJavaScript("   f.PREDECESSOR.disabled = true;\n");

            }

        }

        printHiddenField("HIDDENPARTNO", "");
        printHiddenField("ONCEGIVENERROR", "FALSE");
        printHiddenField("CREREPNONSER", openCreRepNonSer);
        printHiddenField("BSAVEWITHPRICE", "");
        printHiddenField("STATEVAL", "");
        printHiddenField("BUTTONVAL", "");
        printHiddenField("ACTIVITY_SEQ_PROJ", "");
        printHiddenField("REFRESHPARENT1", "FALSE");
        printHiddenField("REFRESHPARENT2", "FALSE");
        printHiddenField("PLANMEN", planMen);
        printHiddenField("SAVEEXECUTE", "");
        printHiddenField("PUSH_FINDMODE", "");
        printHiddenField("PRPOSTCHANGED", "");
	//Bug 82543, Start
	printHiddenField("REFRESH_FLAG","");
	//Bug 82543, End

        // 031204  ARWILK  Begin  (Replace blocks with tabs)
        appendToHTML(headlay.show());

        if (headlay.isSingleLayout() && headset.countRows() > 0)
            appendToHTML(tabs.showTabsInit());

        if (headlay.isSingleLayout() && headset.countRows() > 0)
        {
            if (tabs.getActiveTab() == 1)
            {
                appendToHTML(itemlay2.show());
                if (itemlay2.isSingleLayout() && (itemset2.countRows() > 0))
                    appendToHTML(itemlay8.show());
            }
            else if (tabs.getActiveTab() == 2)
            {
                appendToHTML(itemlay3.show());
                if (itemlay3.isSingleLayout() && (itemset3.countRows() > 0))
                    appendToHTML(itemlay.show());
            }
            else if (tabs.getActiveTab() == 3)
                appendToHTML(itemlay5.show());
            else if (tabs.getActiveTab() == 4)
            {
                appendToHTML(itemlay6.show());
                if (itemlay6.isSingleLayout() && (itemset6.countRows() > 0))
                    appendToHTML(itemlay7.show());
            }
        }
        // 031204  ARWILK  End  (Replace blocks with tabs)

        // Bug 72214, start
	appendDirtyJavaScript("if ('");
	appendDirtyJavaScript(sIsLastWo);
	appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"PageID_IsLastWo1\")==\"TRUE\")\n");
	appendDirtyJavaScript("{ \n");
	appendDirtyJavaScript("      alert(\"");
	appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2ISLASTWO: This is the last work order in the work order structure, remember to also finish the topmost work order."));
	appendDirtyJavaScript("\");\n");
	appendDirtyJavaScript("	   document.form.BUTTONVAL.value = \"ISLASTWO\";\n");
	appendDirtyJavaScript("	   writeCookie(\"PageID_IsLastWo1\", \"FALSE\", '',COOKIE_PATH); \n");
	appendDirtyJavaScript("	   f.submit();\n");
	appendDirtyJavaScript("} \n");
	// Bug 72214, end

        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(openCancelDlg);
        appendDirtyJavaScript("' == \"TRUE\")\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("  jWoNo = '");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(sWoNo));
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("   window.open(\"EnterCancelCauseDlg.page?WO_NO=\"+URLClientEncode(jWoNo)+\"&CALLER=\"+URLClientEncode('ActiveSeparate2'),\"mainFrame\",\"alwaysRaised=yes,resizable,status=yes,scrollbars=yes,width=800,height=500\");   \n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function refreshWorkOrder()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   f.PUSH_FINDMODE.value = 'TRUE';\n");
        appendDirtyJavaScript("   f.submit();\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(clearAlloc0);
        appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"PageID_clearAllocation1\")==\"TRUE\")\n");
        appendDirtyJavaScript("{ \n");
        appendDirtyJavaScript("      if (confirm(\"");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2CLEARALLOC1: Clear Allocations on selected lines"));
        appendDirtyJavaScript("\")) {\n");
        appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"DDDD\";\n");
        appendDirtyJavaScript("		writeCookie(\"PageID_clearAllocation1\", \"FALSE\", '',COOKIE_PATH); \n");
        appendDirtyJavaScript("		f.submit();\n");
        appendDirtyJavaScript("     } \n");
        appendDirtyJavaScript("} \n");

        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(clearAlloc1);
        appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"PageID_clearAllocation2\")==\"TRUE\")\n");
        appendDirtyJavaScript("{ \n");
        appendDirtyJavaScript("      if (confirm(\"");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2CLEARALLOC2: Clear Allocations on selected lines"));
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
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2CLEARALLOC3: Clear Allocations on selected lines"));
        appendDirtyJavaScript("\")) {\n");
        appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"FFFF\";\n");
        appendDirtyJavaScript("		writeCookie(\"PageID_clearAllocation3\", \"FALSE\", '', COOKIE_PATH); \n");
        appendDirtyJavaScript("		f.submit();\n");
        appendDirtyJavaScript("     } \n");
        appendDirtyJavaScript("} \n");

        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(unissue);
        appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"PageID_my_cookie1\")==\"TRUE\")\n");
        appendDirtyJavaScript("{ \n");
        appendDirtyJavaScript("      if (confirm(\"");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2UNISSUE: All required material has not been issued"));
        appendDirtyJavaScript("\")) {\n");
        appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"AAAA\";\n");
        appendDirtyJavaScript("		writeCookie(\"PageID_my_cookie1\", \"FALSE\", '', COOKIE_PATH); \n");
        appendDirtyJavaScript("		f.submit();\n");
        appendDirtyJavaScript("     } \n");
        appendDirtyJavaScript("} \n");
        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(unissue); 
        appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"PageID_my_cookie2\")==\"TRUE\")\n");
        appendDirtyJavaScript("{ \n");
        appendDirtyJavaScript("      if (confirm(\"");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2UNISSUE: All required material has not been issued"));
        appendDirtyJavaScript("\")) {\n");
        appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"CCCC\";\n");
        appendDirtyJavaScript("		writeCookie(\"PageID_my_cookie2\", \"FALSE\", '', COOKIE_PATH); \n");
        appendDirtyJavaScript("		f.submit();\n");
        appendDirtyJavaScript("     } \n");
        appendDirtyJavaScript("} \n");

        appendDirtyJavaScript("window.name = \"prepareWorkOrd\"\n");
        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(repair)); //Bug Id 68773
        appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"PageID_my_cookie\")==\"TRUE\")\n");
        appendDirtyJavaScript("{ \n");
        appendDirtyJavaScript("      if (confirm(\"");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2REPAIR: This object is in repair shop. Do you still want to continue?"));
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
        appendDirtyJavaScript(mgr.encodeStringForJavascript(transferurl)); // Bug Id 68773
        appendDirtyJavaScript("' != \"\")\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript(" index1 =  '");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(transferurl)); // XSS_Safe ILSOLK 20070713
        appendDirtyJavaScript("'.lastIndexOf('.page');\n");
        appendDirtyJavaScript(" index2 =  '");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(transferurl)); // XSS_Safe ILSOLK 20070713
        appendDirtyJavaScript("'.lastIndexOf('/')+1;\n");
        appendDirtyJavaScript(" handle =  '");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(transferurl)); // XSS_Safe ILSOLK 20070713
        appendDirtyJavaScript("'.slice(index2,index1);\n");
        appendDirtyJavaScript(" window.open('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(transferurl)); // XSS_Safe ILSOLK 20070713
        appendDirtyJavaScript("',handle,\"alwaysRaised=yes,resizable,status=yes,scrollbars=yes,width=650,height=450\");\n");
        appendDirtyJavaScript(" transferurl = \"\";\n");
        appendDirtyJavaScript("}\n");

	//Bug 85045, Start, Set Maint Org Site to mandatory if Maint Org, Executed By or Craft has values
        appendDirtyJavaScript("function checkMandoItem2()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("      if ((f.ITEM2_ORG_CODE.value != '' || f.ROLE_CODE.value != '' || f.SIGN.value != '' || f.SIGN_ID.value != '') && f.ITEM2_CONTRACT.value == '')  \n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWACTIVESEPARATE2ORGMASITEMAN: The field [Maint. Org. Site] must have a value"));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("   return false;\n"); 
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("  return checkRowNo(0);\n");
        appendDirtyJavaScript("}\n");
              
        appendDirtyJavaScript("function checkItem8Fields()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("      if ((f.ITEM8_ORG_CODE.value != '' || f.ITEM8_ROLE_CODE.value != '' || f.ITEM8_SIGN.value != '' || f.ITEM8_SIGN_ID.value != '') && f.ITEM8_CONTRACT.value == '')  \n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWACTIVESEPARATE2ORGMASITEMAN: The field [Maint. Org. Site] must have a value"));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("   return false;\n"); 
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("  return checkItem8Sign(0) &&\n");
        appendDirtyJavaScript("  checkItem8DateFrom(0) &&\n");
        appendDirtyJavaScript("  checkItem8DateTo(0);\n");
        appendDirtyJavaScript("}\n");
	//Bug 85045, End

        appendDirtyJavaScript("function validatePartNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("f.HIDDENPARTNO.value = \"\";\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM4',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkPartNo(i) ) return;\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=PART_NO'\n");
        appendDirtyJavaScript("		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
        appendDirtyJavaScript("		+ '&SPARE_CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&CATALOG_CONTRACT=' + URLClientEncode(getValue_('CATALOG_CONTRACT',i))\n");
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
        appendDirtyJavaScript("		assignValue_('CATALOG_NO',i,0);\n");
        appendDirtyJavaScript("		assignValue_('CATALOGDESC',i,1);\n");
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
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWACTIVESEPARATE2INVSALESPART: All sale parts connected to the part are inactive."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.CATALOG_NO.value = ''; \n");
        appendDirtyJavaScript("      f.CATALOGDESC.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("        validateCatalogNo(i);\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function checkMandoItem3()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("  return checkPartNo(0) &&\n");
        appendDirtyJavaScript("  checkSpareContract(0) &&\n");
        appendDirtyJavaScript("  checkQtyRequired(0) &&\n");
        appendDirtyJavaScript("  checkDateRequired(0);\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovMchCode(i,params)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if(params) param = params;\n");
        appendDirtyJavaScript("	else param = '';\n");
        appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
        appendDirtyJavaScript("	var key_value = (getValue_('MCH_CODE',i).indexOf('%') !=-1)? getValue_('MCH_CODE',i):'';\n");
        appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' )\n");
        appendDirtyJavaScript("       {\n");
        appendDirtyJavaScript("              openLOVWindow('MCH_CODE',i,\n");
        appendDirtyJavaScript("                  '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=MAINTENANCE_OBJECT&__FIELD=Object+ID&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('MCH_CODE',i))\n"); 
        appendDirtyJavaScript("                  + '&MCH_CODE=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript("                  + '&CONTRACT=' + URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))\n");       
        appendDirtyJavaScript("       	   ,550,500,'validateMchCode');\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript("       else\n");
        appendDirtyJavaScript("       {\n");    
        appendDirtyJavaScript("  objwherecond = \" OBJ_LEVEL IS NULL OR (OBJ_LEVEL IS NOT NULL AND Equipment_Object_Level_API.Get_Create_Wo(OBJ_LEVEL) = 'TRUE')\";\n");
        appendDirtyJavaScript("	       openLOVWindow('MCH_CODE',i,\n");
        appendDirtyJavaScript("		   'MaintenanceObjectLov1.page?'+param+'?MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("		   + '&CONTRACT=' + URLClientEncode(getValue_('MCH_CODE_CONTRACT',i)) + '&CONNECTION_TYPE=' + URLClientEncode(getValue_('CONNECTION_TYPE',i)) +'&__WHERE='+ objwherecond \n");
        appendDirtyJavaScript("		   ,550,500,'validateMchCode');\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript("}\n"); 

	// XSS_Safe ILSOLK 20070705
        appendDirtyJavaScript("function lovCatalogNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	openLOVWindow('CATALOG_NO',i,\n");
        appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=SALES_PART_ACTIVE_LOV&__INIT=1'\n");
        appendDirtyJavaScript("		+ '&__FIELD=' + URLClientEncode('"+mgr.encodeStringForJavascript(mgr.getASPField("CATALOG_NO").getJSLabel())+"')\n");
        appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CATALOG_CONTRACT',i))\n");
        appendDirtyJavaScript("		,600,445,'validateCatalogNo');\n");
        appendDirtyJavaScript("}\n");

        if ("TRUE".equals(showMat))
            appendDirtyJavaScript("NEVER_EXPIRE = false;\n");

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
        appendDirtyJavaScript("	   whereCond1 += \" ORG_CODE IN (SELECT MAINT_ORG FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.TEAM_ID.value+\"' AND CONTRACT = '\"+ document.form.TEAM_CONTRACT.value+\"' \";\n");
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
        appendDirtyJavaScript("	if( getValue_('ITEM2_ORG_CODE',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('COST',i).value = '';\n");
        appendDirtyJavaScript("		getField_('COSTAMOUNT',i).value = '';\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM2_ORG_CODE'\n");
        appendDirtyJavaScript("		+ '&ITEM2_CONTRACT=' + URLClientEncode(getValue_('ITEM2_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_ORG_CODE=' + URLClientEncode(getValue_('ITEM2_ORG_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
        appendDirtyJavaScript("		+ '&PLAN_MEN=' + URLClientEncode(getValue_('PLAN_MEN',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_PLAN_HRS=' + URLClientEncode(getValue_('ITEM2_PLAN_HRS',i))\n");
        appendDirtyJavaScript("		+ '&SIGN=' + URLClientEncode(getValue_('SIGN',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_WO_NO=' + URLClientEncode(getValue_('ITEM2_WO_NO',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM2_ORG_CODE',i,'Maintenance Organization') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('COST',i,0);\n");
        appendDirtyJavaScript("		assignValue_('COSTAMOUNT',i,1);\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_ORG_CODE',i,2);\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_CONTRACT',i,3);\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_ORG_CODE_DESC',i,4);\n");
        appendDirtyJavaScript("	}\n");
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
        appendDirtyJavaScript("	   whereCond1 += \" AND 1 IN (SELECT Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,ROLE_CODE,MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.TEAM_ID.value+\"' AND CONTRACT='\" + URLClientEncode(document.form.TEAM_CONTRACT.value) +\"' AND \" +whereCond2 +\")\";\n"); 
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
        appendDirtyJavaScript("	if( getRowStatus_('ITEM2',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkRoleCode(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('ROLE_CODE',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('COST',i).value = '';\n");
        appendDirtyJavaScript("		getField_('COSTAMOUNT',i).value = '';\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ROLE_CODE'\n"); 
        appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_CONTRACT=' + URLClientEncode(getValue_('ITEM2_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_ORG_CODE=' + URLClientEncode(getValue_('ITEM2_ORG_CODE',i))\n");
        appendDirtyJavaScript("		+ '&PLAN_MEN=' + URLClientEncode(getValue_('PLAN_MEN',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_PLAN_HRS=' + URLClientEncode(getValue_('ITEM2_PLAN_HRS',i))\n");
        appendDirtyJavaScript("		+ '&SIGN=' + URLClientEncode(getValue_('SIGN',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_WO_NO=' + URLClientEncode(getValue_('ITEM2_WO_NO',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ROLE_CODE',i,'Craft ID') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('COST',i,0);\n");
        appendDirtyJavaScript("		assignValue_('COSTAMOUNT',i,1);\n");
        appendDirtyJavaScript("		assignValue_('ROLE_CODE',i,2);\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_CONTRACT',i,3);\n");
        appendDirtyJavaScript("		assignValue_('ROLE_CODE_DESC',i,4);\n");
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
        appendDirtyJavaScript("		getField_('TOTAL_MAN_HOURS',i).value = '';\n");
        appendDirtyJavaScript("		getField_('TOTAL_REMAINING_HOURS',i).value = '';\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=PLAN_MEN'\n");
        appendDirtyJavaScript("		+ '&PLAN_MEN=' + URLClientEncode(getValue_('PLAN_MEN',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_PLAN_HRS=' + URLClientEncode(getValue_('ITEM2_PLAN_HRS',i))\n");
        appendDirtyJavaScript("		+ '&COST=' + URLClientEncode(getValue_('COST',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_CONTRACT=' + URLClientEncode(getValue_('ITEM2_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_ORG_CODE=' + URLClientEncode(getValue_('ITEM2_ORG_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
        appendDirtyJavaScript("		+ '&TOTAL_ALLOCATED_HOURS=' + URLClientEncode(getValue_('TOTAL_ALLOCATED_HOURS',i))\n");

        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'PLAN_MEN',i,'Pl.Men') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('COST',i,0);\n");
        appendDirtyJavaScript("		assignValue_('COSTAMOUNT',i,1);\n");
        appendDirtyJavaScript("		assignValue_('TOTAL_MAN_HOURS',i,2);\n");
        appendDirtyJavaScript("		assignValue_('TOTAL_REMAINING_HOURS',i,3);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateItem2PlanHrs(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM2',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem2PlanHrs(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('ITEM2_PLAN_HRS',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('COST',i).value = '';\n");
        appendDirtyJavaScript("		getField_('COSTAMOUNT',i).value = '';\n");
        appendDirtyJavaScript("		getField_('TOTAL_MAN_HOURS',i).value = '';\n");
        appendDirtyJavaScript("		getField_('TOTAL_REMAINING_HOURS',i).value = '';\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM2_PLAN_HRS'\n");
        appendDirtyJavaScript("		+ '&PLAN_MEN=' + URLClientEncode(getValue_('PLAN_MEN',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_PLAN_HRS=' + URLClientEncode(getValue_('ITEM2_PLAN_HRS',i))\n");
        appendDirtyJavaScript("		+ '&COST=' + URLClientEncode(getValue_('COST',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_CONTRACT=' + URLClientEncode(getValue_('ITEM2_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_ORG_CODE=' + URLClientEncode(getValue_('ITEM2_ORG_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
        appendDirtyJavaScript("		+ '&TOTAL_ALLOCATED_HOURS=' + URLClientEncode(getValue_('TOTAL_ALLOCATED_HOURS',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM2_PLAN_HRS',i,'Pl.Hrs') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('COST',i,0);\n");
        appendDirtyJavaScript("		assignValue_('COSTAMOUNT',i,1);\n");
        appendDirtyJavaScript("		assignValue_('TOTAL_MAN_HOURS',i,2);\n");
        appendDirtyJavaScript("		assignValue_('TOTAL_REMAINING_HOURS',i,3);\n");
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

        appendDirtyJavaScript("function validateItem5Qty(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM5',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem5Qty(i) ) return;\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM5_QTY'\n");
        appendDirtyJavaScript("		+ '&ITEM5_QTY=' + URLClientEncode(getValue_('ITEM5_QTY',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM5_PLANNED_HOUR',i))\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_WO_NO=' + URLClientEncode(getValue_('ITEM5_WO_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_CATALOG_NO=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_SALES_PRICE=' + URLClientEncode(getValue_('ITEM5_SALES_PRICE',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_DISCOUNT=' + URLClientEncode(getValue_('ITEM5_DISCOUNT',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM5_QTY',i,'Quantity') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_COST_AMOUNT',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_PLANNED_PRICE',i,1);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateItem5PlannedHour(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("if( getRowStatus_('ITEM5',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem5PlannedHour(i) ) return;\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM5_PLANNED_HOUR'\n");
        appendDirtyJavaScript("		+ '&ITEM5_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM5_PLANNED_HOUR',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_QTY=' + URLClientEncode(getValue_('ITEM5_QTY',i))\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_WO_NO=' + URLClientEncode(getValue_('ITEM5_WO_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_CATALOG_NO=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_SALES_PRICE=' + URLClientEncode(getValue_('ITEM5_SALES_PRICE',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_DISCOUNT=' + URLClientEncode(getValue_('ITEM5_DISCOUNT',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM5_PLANNED_HOUR',i,'Planned Hours') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_COST_AMOUNT',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_PLANNED_PRICE',i,1);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateItem5Discount(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("        if( getRowStatus_('ITEM5',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem5Discount(i) ) return;\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM5_DISCOUNT'\n");
        appendDirtyJavaScript("		+ '&ITEM5_DISCOUNT=' + URLClientEncode(getValue_('ITEM5_DISCOUNT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_SALES_PRICE=' + URLClientEncode(getValue_('ITEM5_SALES_PRICE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_CATALOG_NO=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_WO_NO=' + URLClientEncode(getValue_('ITEM5_WO_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_QTY=' + URLClientEncode(getValue_('ITEM5_QTY',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM5_PLANNED_HOUR',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_SALES_PRICE=' + URLClientEncode(getValue_('ITEM5_SALES_PRICE',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_DISCOUNT=' + URLClientEncode(getValue_('ITEM5_DISCOUNT',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM5_DISCOUNT',i,'Discount') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_DISCOUNTED_PRICE',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_PLANNED_PRICE',i,1);\n");
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
        appendDirtyJavaScript("window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=TOOL_FACILITY_ID'\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_ORG_CODE=' + URLClientEncode(getValue_('ITEM5_ORG_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_WO_NO=' + URLClientEncode(getValue_('ITEM5_WO_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_QTY=' + URLClientEncode(getValue_('ITEM5_QTY',i))\n");
        appendDirtyJavaScript("		+ '&ITEM5_PLANNED_HOUR=' + URLClientEncode(getValue_('ITEM5_PLANNED_HOUR',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_NOTE=' + URLClientEncode(getValue_('ITEM5_NOTE',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_CATALOG_NO=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_CONTRACT',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_CATALOG_NO_DESC=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_DESC',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_SALES_PRICE=' + URLClientEncode(getValue_('ITEM5_SALES_PRICE',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_SALES_CURRENCY=' + URLClientEncode(getValue_('ITEM5_SALES_CURRENCY',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_DISCOUNT=' + URLClientEncode(getValue_('ITEM5_DISCOUNT',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_DISCOUNTED_PRICE=' + URLClientEncode(getValue_('ITEM5_DISCOUNTED_PRICE',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_PLANNED_PRICE=' + URLClientEncode(getValue_('ITEM5_PLANNED_PRICE',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'TOOL_FACILITY_ID',i,'Tool/Facility Id') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_DESC',i,0);\n");
        appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_TYPE',i,1);\n");
        appendDirtyJavaScript("		assignValue_('TYPE_DESCRIPTION',i,2);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_QTY',i,3);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_COST',i,4);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_COST_CURRENCY',i,5);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_COST_AMOUNT',i,6);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO_CONTRACT',i,7);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO',i,8);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO_DESC',i,9);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_SALES_PRICE',i,10);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_SALES_CURRENCY',i,11);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_DISCOUNT',i,12);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_DISCOUNTED_PRICE',i,13);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_PLANNED_PRICE',i,14);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_NOTE',i,15);\n");
        appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_ID',i,16);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CONTRACT',i,17);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_ORG_CODE',i,18);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateItem5Contract(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM5',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem5Contract(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('ITEM5_CONTRACT',i)!='' )\n");
        appendDirtyJavaScript("        {\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
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
        appendDirtyJavaScript("		+ '&ITEM5_WO_NO=' + URLClientEncode(getValue_('ITEM5_WO_NO',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_NOTE=' + URLClientEncode(getValue_('ITEM5_NOTE',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_CATALOG_NO=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_CONTRACT',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_CATALOG_NO_DESC=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_DESC',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_SALES_PRICE=' + URLClientEncode(getValue_('ITEM5_SALES_PRICE',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_SALES_CURRENCY=' + URLClientEncode(getValue_('ITEM5_SALES_CURRENCY',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_DISCOUNT=' + URLClientEncode(getValue_('ITEM5_DISCOUNT',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_DISCOUNTED_PRICE=' + URLClientEncode(getValue_('ITEM5_DISCOUNTED_PRICE',i))\n");
        appendDirtyJavaScript("                + '&ITEM5_PLANNED_PRICE=' + URLClientEncode(getValue_('ITEM5_PLANNED_PRICE',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM5_CONTRACT',i,'Site') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_NOTE',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_QTY',i,1);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_COST_AMOUNT',i,2);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO_CONTRACT',i,3);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO',i,4);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO_DESC',i,5);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_SALES_PRICE',i,6);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_SALES_CURRENCY',i,7);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_DISCOUNT',i,8);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_DISCOUNTED_PRICE',i,9);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_PLANNED_PRICE',i,10);\n");
        appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_ID',i,11);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CONTRACT',i,12);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_ORG_CODE',i,13);\n");
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
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
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
        appendDirtyJavaScript("		+ '&ITEM5_WO_NO=' + URLClientEncode(getValue_('ITEM5_WO_NO',i))\n");
        appendDirtyJavaScript("         + '&ITEM5_NOTE=' + URLClientEncode(getValue_('ITEM5_NOTE',i))\n");
        appendDirtyJavaScript("         + '&ITEM5_CATALOG_NO=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO',i))\n");
        appendDirtyJavaScript("         + '&ITEM5_CATALOG_NO_CONTRACT=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_CONTRACT',i))\n");
        appendDirtyJavaScript("         + '&ITEM5_CATALOG_NO_DESC=' + URLClientEncode(getValue_('ITEM5_CATALOG_NO_DESC',i))\n");
        appendDirtyJavaScript("         + '&ITEM5_SALES_PRICE=' + URLClientEncode(getValue_('ITEM5_SALES_PRICE',i))\n");
        appendDirtyJavaScript("         + '&ITEM5_SALES_CURRENCY=' + URLClientEncode(getValue_('ITEM5_SALES_CURRENCY',i))\n");
        appendDirtyJavaScript("         + '&ITEM5_DISCOUNT=' + URLClientEncode(getValue_('ITEM5_DISCOUNT',i))\n");
        appendDirtyJavaScript("         + '&ITEM5_DISCOUNTED_PRICE=' + URLClientEncode(getValue_('ITEM5_DISCOUNTED_PRICE',i))\n");
        appendDirtyJavaScript("         + '&ITEM5_PLANNED_PRICE=' + URLClientEncode(getValue_('ITEM5_PLANNED_PRICE',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM5_ORG_CODE',i,'Maintenance Organization') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_NOTE',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_QTY',i,1);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_COST_AMOUNT',i,2);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO_CONTRACT',i,3);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO',i,4);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CATALOG_NO_DESC',i,5);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_SALES_PRICE',i,6);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_SALES_CURRENCY',i,7);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_DISCOUNT',i,8);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_DISCOUNTED_PRICE',i,9);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_PLANNED_PRICE',i,10);\n");
        appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_ID',i,11);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_CONTRACT',i,12);\n");
        appendDirtyJavaScript("		assignValue_('ITEM5_ORG_CODE',i,13);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("}\n");           

        appendDirtyJavaScript("function validatePartOwnership(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM4',i)=='QueryMode__' ) return;\n");
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
        appendDirtyJavaScript("         + '&SUPPLY_CODE=' + SelectURLClientEncode('SUPPLY_CODE',i)		+ '&ITEM0_MAINT_MATERIAL_ORDER_NO=' + URLClientEncode(getValue_('ITEM0_MAINT_MATERIAL_ORDER_NO',i))");
        appendDirtyJavaScript("		+ '&CONDITION_CODE=' + URLClientEncode(getValue_('CONDITION_CODE',i))\n");
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
        appendDirtyJavaScript("	  var enable_multichoice =(true && ITEM4_IN_FIND_MODE);\n");
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

        appendDirtyJavaScript("function validateOwner(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if( getRowStatus_('ITEM4',i)=='QueryMode__' ) return;\n");
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
        appendDirtyJavaScript("                    + '&PART_OWNERSHIP=' + URLClientEncode(getValue_('PART_OWNERSHIP',i))\n");
        appendDirtyJavaScript("                    + '&ITEM3_WO_NO=' + URLClientEncode(getValue_('ITEM3_WO_NO',i))\n");
        appendDirtyJavaScript("		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
        appendDirtyJavaScript("		+ '&SPARE_CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&CONDITION_CODE=' + URLClientEncode(getValue_('CONDITION_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM3_ACTIVITY_SEQ=' + URLClientEncode(getValue_('ITEM3_ACTIVITY_SEQ',i))\n");
        appendDirtyJavaScript("         + '&SUPPLY_CODE=' + SelectURLClientEncode('SUPPLY_CODE',i)		+ '&ITEM0_MAINT_MATERIAL_ORDER_NO=' + URLClientEncode(getValue_('ITEM0_MAINT_MATERIAL_ORDER_NO',i))");
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

        if (itemlay.isSingleLayout() && bUnequalMatWo)
        {
            appendDirtyJavaScript("         if (confirm('");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2DIFFCUST: Owning Customer No is not the same as the Work Order Customer No. Still want to save this record?"));
            appendDirtyJavaScript("'))\n"); 
            appendDirtyJavaScript("   { \n");
            appendDirtyJavaScript("      setITEM4Command('issueFromInvent');commandSet('ITEM4.Perform','');\n");
            appendDirtyJavaScript("   } \n");
        }
        else if (itemlay.isSingleLayout() && bNoWoCust)
        {
            appendDirtyJavaScript("         if (confirm('");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2NOCUST: There is no Customer specified for the Work Order although you have entered an Owning Customer No. Still want to save this record?"));
            appendDirtyJavaScript("'))\n");
            appendDirtyJavaScript("   { \n");
            appendDirtyJavaScript("      setITEM4Command('issueFromInvent');commandSet('ITEM4.Perform','');\n");
            appendDirtyJavaScript("   } \n");
        }
        else if (itemlay.isMultirowLayout() && bUnequalMatWo)
        {
            appendDirtyJavaScript("         if (confirm('");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2DIFFCUST: Owning Customer No is not the same as the Work Order Customer No. Still want to save this record?"));
            appendDirtyJavaScript("'))\n"); 
            appendDirtyJavaScript("   { \n");
            appendDirtyJavaScript("      initPop4("+currrowItem4+");_menu(0);setTableCommand4('issueFromInvent');commandSet('ITEM4.Perform','');\n"); // XSS_Safe ILSOLK 20070713
            appendDirtyJavaScript("   } \n");
        }
        else if (itemlay.isMultirowLayout() && bNoWoCust)
        {
            appendDirtyJavaScript("         if (confirm('");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2NOCUST: There is no Customer specified for the Work Order although you have entered an Owning Customer No. Still want to save this record?"));
            appendDirtyJavaScript("'))\n");
            appendDirtyJavaScript("   { \n");
            appendDirtyJavaScript("      initPop4("+currrowItem4+");_menu(0);setTableCommand4('issueFromInvent');commandSet('ITEM4.Perform','');\n"); // XSS_Safe ILSOLK 20070713
            appendDirtyJavaScript("   } \n");
        }

        // 040108  ARWILK  Begin  (Remove uneccessary global variables)
        // XSS_Safe ILSOLK 20070705
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
        // 040108  ARWILK  End  (Remove uneccessary global variables)

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
        appendDirtyJavaScript("       + '&ITEM6_DATE_TO=' + URLClientEncode(getValue_('ITEM6_DATE_TO',i))\n");
	appendDirtyJavaScript("       + '&ITEM6_DATE_FROM=' + URLClientEncode(getValue_('ITEM6_DATE_FROM',i))\n");
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
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWACTIVESEPARATE2CANNOTUPDATEDATETO: Job ID end date cannot be a date before the Operation To Date"));
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
        appendDirtyJavaScript("                  && (getValue_('S_STD_JOB_ID',i) != getValue_('STD_JOB_ID',i) || getValue_('S_STD_JOB_REVISION',i) != getValue_('STD_JOB_REVISION',i) || getValue_('N_QTY',i) != getValue_('ITEM6_QTY',i)))\n");
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
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2CANNOTUPDATEDATEFROM: Overlaps with existing booking(s). Still want to save this allocation?"));
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
        appendDirtyJavaScript("   return confirm('" + mgr.translateJavaScript("ACTSEP2HEADERDISCACT: Are you sure you want to remove the Project Connection ?") + "');\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function setActivitySeq(activitySeq)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   getField_('ACTIVITY_SEQ_PROJ', -1).value = activitySeq;\n");
        appendDirtyJavaScript("   commandSet('HEAD.connectToActivity','');\n");
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


        appendDirtyJavaScript("function validateSign(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM2',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkSign(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('SIGN',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('SIGN_ID',i).value = '';\n");
        appendDirtyJavaScript("		getField_('ROLE_CODE',i).value = '';\n");
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
        appendDirtyJavaScript("		+ '&SIGN=' + URLClientEncode(getValue_('SIGN',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_COMPANY=' + URLClientEncode(getValue_('ITEM2_COMPANY',i))\n");
        appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_CONTRACT=' + URLClientEncode(getValue_('ITEM2_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_ORG_CODE=' + URLClientEncode(getValue_('ITEM2_ORG_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
        appendDirtyJavaScript("		+ '&COST=' + URLClientEncode(getValue_('COST',i))\n");
        appendDirtyJavaScript("		+ '&PLAN_MEN=' + URLClientEncode(getValue_('PLAN_MEN',i))\n");
        appendDirtyJavaScript("		+ '&ITEM2_PLAN_HRS=' + URLClientEncode(getValue_('ITEM2_PLAN_HRS',i))\n");
        appendDirtyJavaScript("		+ '&ALLOCATED_HOURS=' + URLClientEncode(getValue_('ALLOCATED_HOURS',i))\n");
        appendDirtyJavaScript("		+ '&TOTAL_MAN_HOURS=' + URLClientEncode(getValue_('TOTAL_MAN_HOURS',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'SIGN',i,'Executed By') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('SIGN_ID',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ROLE_CODE',i,1);\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_ORG_CODE',i,2);\n");
        appendDirtyJavaScript("		assignValue_('COST',i,3);\n");
        appendDirtyJavaScript("		assignValue_('COSTAMOUNT',i,4);\n");
        appendDirtyJavaScript("		assignValue_('SIGN',i,5);\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_CONTRACT',i,6);\n");
        appendDirtyJavaScript("		assignValue_('ALLOCATED_HOURS',i,7);\n");
        appendDirtyJavaScript("		assignValue_('TOTAL_ALLOCATED_HOURS',i,8);\n");
        appendDirtyJavaScript("		assignValue_('TOTAL_REMAINING_HOURS',i,9);\n");
        appendDirtyJavaScript("		assignValue_('ITEM2_ORG_CODE_DESC',i,10);\n");
        appendDirtyJavaScript("		assignValue_('ROLE_CODE_DESC',i,11);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        //-----validations for new operations under jobs tab.
        //NOTE : The changes done to the javascript methods in the operation tab
        //       should also be done to the below methods as well.

        appendDirtyJavaScript("function checkMandoItem7()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("  return checkItem7RowNo(0);\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovItem7OrgCode(i,params)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if(params) param = params;\n");
        appendDirtyJavaScript("	else param = '';\n");
        appendDirtyJavaScript("	whereCond1 = '';\n"); 
        appendDirtyJavaScript("	whereCond2 = '';\n");
        appendDirtyJavaScript(" if (document.form.ITEM7_ROLE_CODE.value != '') \n");
        appendDirtyJavaScript(" {\n");
        appendDirtyJavaScript("    if((document.form.ITEM7_SIGN_ID.value == '') && (document.form.ITEM7_CONTRACT.value == ''))\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("	      whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ITEM7_ROLE_CODE.value) + \"' \";\n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("    else if((document.form.ITEM7_SIGN_ID.value == '') && (document.form.ITEM7_CONTRACT.value != ''))\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("	      whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ITEM7_ROLE_CODE.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM7_CONTRACT.value)+\"' \";\n");  
        appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM7_CONTRACT.value)+\"' \";\n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("    else if((document.form.ITEM7_SIGN_ID.value != '') && (document.form.ITEM7_CONTRACT.value != ''))\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("	      whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ITEM7_ROLE_CODE.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM7_CONTRACT.value)+\"' AND EMP_NO = '\" +URLClientEncode(document.form.ITEM7_SIGN_ID.value)+\"' \";\n");  
        appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM7_CONTRACT.value)+\"' AND MEMBER_EMP_NO = '\" +URLClientEncode(document.form.ITEM7_SIGN_ID.value)+\"' \";\n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript(" }\n");
        appendDirtyJavaScript(" else if ((document.form.ITEM7_ROLE_CODE.value == '') && (document.form.ITEM7_SIGN_ID.value != '')) \n");
        appendDirtyJavaScript(" {\n");
        appendDirtyJavaScript("    if (document.form.ITEM7_CONTRACT.value == '') \n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("	      whereCond1 = \" EMPLOYEE_ID = '\" +URLClientEncode(document.form.ITEM7_SIGN_ID.value)+\"' \";\n");  
        appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" +URLClientEncode(document.form.ITEM7_SIGN_ID.value)+\"' \";\n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("    else\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("	      whereCond1 = \" EMPLOYEE_ID = '\" +URLClientEncode(document.form.ITEM7_SIGN_ID.value)+\"' AND CONTRACT = '\" +URLClientEncode(document.form.ITEM7_CONTRACT.value)+\"' \";\n");  
        appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" +URLClientEncode(document.form.ITEM7_SIGN_ID.value)+\"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM7_CONTRACT.value)+\"'\";\n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript(" }\n");
        appendDirtyJavaScript(" else if ((document.form.ITEM7_ROLE_CODE.value == '') && (document.form.ITEM7_SIGN_ID.value == '')) \n");
        appendDirtyJavaScript(" {\n");
        appendDirtyJavaScript("    if (document.form.ITEM7_CONTRACT.value != '') \n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("	      whereCond1 = \" CONTRACT = '\" +URLClientEncode(document.form.ITEM7_CONTRACT.value)+\"' \";\n");  
        appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM7_CONTRACT.value)+\"' \";\n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("    else\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("	whereCond1 = '';\n"); 
        appendDirtyJavaScript("	whereCond2 = '';\n");
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript(" }\n");
        appendDirtyJavaScript(" else\n");
        appendDirtyJavaScript(" {\n");
        appendDirtyJavaScript("    if (document.form.ITEM7_CONTRACT.value != '') \n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("	      whereCond1 = \" CONTRACT = '\" +URLClientEncode(document.form.ITEM7_CONTRACT.value)+\"' \";\n");  
        appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM7_CONTRACT.value)+\"' \";\n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("    else\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("	whereCond1 = '';\n"); 
        appendDirtyJavaScript("	whereCond2 = '';\n");
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript(" }\n");
        appendDirtyJavaScript(" if (document.form.ITEM7_TEAM_ID.value != '')\n");
        appendDirtyJavaScript(" {\n");
        appendDirtyJavaScript(" if(whereCond1 != '' )\n");
        appendDirtyJavaScript("	           whereCond1 += \" AND \";\n");
        appendDirtyJavaScript(" if (document.form.ITEM7_ROLE_CODE.value == '')\n");
        appendDirtyJavaScript("	   whereCond1 += \" ORG_CODE IN (SELECT MAINT_ORG FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.ITEM7_TEAM_ID.value+\"' AND CONTRACT='\" + URLClientEncode(document.form.ITEM7_TEAM_CONTRACT.value) +\"' \";\n");
        appendDirtyJavaScript("    else\n");
        appendDirtyJavaScript("	   whereCond1 += \" (ORG_CODE,EMP_NO,1) IN (SELECT MAINT_ORG,MEMBER_EMP_NO,,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\"+ document.form.ITEM7_ROLE_CODE.value+\"',MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.ITEM7_TEAM_ID.value+\"' AND CONTRACT='\" + URLClientEncode(document.form.ITEM7_TEAM_CONTRACT.value) +\"' \";\n");
        appendDirtyJavaScript(" if(whereCond2 != '' )\n");
        appendDirtyJavaScript("	           whereCond1 += \" AND \" +whereCond2 +\" \";\n"); 
        appendDirtyJavaScript("	whereCond1 += \")\";\n"); 
        appendDirtyJavaScript(" }\n");
        appendDirtyJavaScript("	var enable_multichoice =(true && ITEM7_IN_FIND_MODE);\n");
        appendDirtyJavaScript("	var key_value = (getValue_('ITEM7_ORG_CODE',i).indexOf('%') !=-1)? getValue_('ITEM7_ORG_CODE',i):'';\n");
        appendDirtyJavaScript("	if( getValue_('ITEM7_ROLE_CODE',i)=='' )\n");
        appendDirtyJavaScript("       {\n");
        appendDirtyJavaScript("	            if( getValue_('ITEM7_SIGN',i)=='' )\n");
        appendDirtyJavaScript("             {\n");
        appendDirtyJavaScript("                  openLOVWindow('ITEM7_ORG_CODE',i,\n");
        appendDirtyJavaScript("                     '../mscomw/OrgCodeAllowedSiteLov.page?__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM7_ORG_CODE',i))\n"); 
        appendDirtyJavaScript("                      + '&ORG_CODE=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM7_COMPANY',i))\n");
        appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM7_CONTRACT',i))\n");       
        appendDirtyJavaScript("       	             ,550,500,'validateItem7OrgCode');\n");
        appendDirtyJavaScript("             }\n");
        appendDirtyJavaScript("	            else\n");
        appendDirtyJavaScript("             {\n");
        appendDirtyJavaScript("                 openLOVWindow('ITEM7_ORG_CODE',i,\n");
        appendDirtyJavaScript("                     '../mscomw/MaintEmployeeLov.page?&__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("                     + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM7_ORG_CODE',i))\n"); 
        appendDirtyJavaScript("                     + '&ORG_CODE=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript("                     + '&EMPLOYEE_ID=' + URLClientEncode(getValue_('ITEM7_SIGN_ID',i))\n");
        appendDirtyJavaScript("                     + '&COMPANY=' + URLClientEncode(getValue_('ITEM7_COMPANY',i))\n");
        appendDirtyJavaScript("                     + '&CONTRACT=' + URLClientEncode(getValue_('ITEM7_CONTRACT',i))\n");       
        appendDirtyJavaScript("       	            ,550,500,'validateItem7OrgCode');\n");
        appendDirtyJavaScript("             }\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript(" else\n");
        appendDirtyJavaScript("       {\n");        
        appendDirtyJavaScript("              openLOVWindow('ITEM7_ORG_CODE',i,\n");
        appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM7_ORG_CODE',i))\n"); 
        appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('ITEM7_SIGN_ID',i))\n");
        appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM7_COMPANY',i))\n");
        appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM7_CONTRACT',i))\n");       
        appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(getValue_('ITEM7_ROLE_CODE',i))\n");       
        appendDirtyJavaScript("       	   ,550,500,'validateItem7OrgCode');\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateItem7OrgCode(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM7',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem7OrgCode(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('ITEM7_ORG_CODE',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('ITEM7_COST',i).value = '';\n");
        appendDirtyJavaScript("		getField_('ITEM7_COSTAMOUNT',i).value = '';\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM7_ORG_CODE'\n");
        appendDirtyJavaScript("		+ '&ITEM7_CONTRACT=' + URLClientEncode(getValue_('ITEM7_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_ORG_CODE=' + URLClientEncode(getValue_('ITEM7_ORG_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_ROLE_CODE=' + URLClientEncode(getValue_('ITEM7_ROLE_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_PLAN_MEN=' + URLClientEncode(getValue_('ITEM7_PLAN_MEN',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_PLAN_HRS=' + URLClientEncode(getValue_('ITEM7_PLAN_HRS',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_SIGN=' + URLClientEncode(getValue_('ITEM7_SIGN',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_WO_NO=' + URLClientEncode(getValue_('ITEM7_WO_NO',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM7_ORG_CODE',i,'Maintenance Organization') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_COST',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_COSTAMOUNT',i,1);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_ORG_CODE',i,2);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_CONTRACT',i,3);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_ORG_CODE_DESC',i,4);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovItem7Contract(i,params)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if(params) param = params;\n");
        appendDirtyJavaScript("	else param = '';\n");
        appendDirtyJavaScript("	whereCond1 = '';\n"); 
        appendDirtyJavaScript(" if (document.form.ITEM7_COMPANY.value != '') \n");
        appendDirtyJavaScript(" {\n");
        appendDirtyJavaScript("	      whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.ITEM7_COMPANY.value) + \"' \";\n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("	var enable_multichoice =(true && ITEM7_IN_FIND_MODE);\n");
        appendDirtyJavaScript("	var key_value = (getValue_('ITEM7_CONTRACT',i).indexOf('%') !=-1)? getValue_('ITEM7_CONTRACT',i):'';\n");
        appendDirtyJavaScript("                  openLOVWindow('ITEM7_CONTRACT',i,\n");
        appendDirtyJavaScript("'");
        appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=USER_ALLOWED_SITE_LOV&__FIELD=Maint.Org.+Site&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM7_CONTRACT',i))\n");
        appendDirtyJavaScript("+ '&ITEM7_CONTRACT=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript(",550,500,'validateItem7Contract');\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovItem7RoleCode(i,params)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if(params) param = params;\n");
        appendDirtyJavaScript("	else param = '';\n");
        appendDirtyJavaScript("	whereCond1 = '';\n"); 
        appendDirtyJavaScript("	whereCond2 = '';\n");
        appendDirtyJavaScript(" if ((document.form.ITEM7_SIGN_ID.value != '') && (document.form.ITEM7_COMPANY.value != '')) \n");
        appendDirtyJavaScript(" {\n");
        appendDirtyJavaScript("    if((document.form.ITEM7_ORG_CODE.value == '') && (document.form.ITEM7_CONTRACT.value == ''))\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.ITEM7_SIGN_ID.value) + \"' \";\n"); 
        appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.ITEM7_SIGN_ID.value) + \"' \";\n");
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("    else if((document.form.ITEM7_ORG_CODE.value != '') && (document.form.ITEM7_CONTRACT.value == ''))\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.ITEM7_SIGN_ID.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM7_ORG_CODE.value)+\"' \";\n"); 
        appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.ITEM7_SIGN_ID.value) + \"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM7_ORG_CODE.value)+\"' \";\n");
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("    else if((document.form.ITEM7_ORG_CODE.value == '') && (document.form.ITEM7_CONTRACT.value != ''))\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.ITEM7_SIGN_ID.value) + \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM7_CONTRACT.value)+\"' \";\n"); 
        appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.ITEM7_SIGN_ID.value) + \"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM7_CONTRACT.value)+\"' \";\n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("    else if((document.form.ITEM7_ORG_CODE.value != '') && (document.form.ITEM7_CONTRACT.value != ''))\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("	      whereCond1 = \" EMP_NO = '\" + URLClientEncode(document.form.ITEM7_SIGN_ID.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM7_ORG_CODE.value)+ \"' AND ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM7_CONTRACT.value)+\"' \";\n"); 
        appendDirtyJavaScript("	      whereCond2 = \" MEMBER_EMP_NO = '\" + URLClientEncode(document.form.ITEM7_SIGN_ID.value) + \"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM7_ORG_CODE.value)+\"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM7_CONTRACT.value)+\"' \";\n");
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript(" }\n");
        appendDirtyJavaScript(" else\n");
        appendDirtyJavaScript(" {\n");
        appendDirtyJavaScript("	   whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.ITEM7_COMPANY.value) + \"' \";\n"); 
        appendDirtyJavaScript("	   whereCond2 = \" Site_API.Get_Company(MAINT_ORG_CONTRACT) = '\" + URLClientEncode(document.form.ITEM7_COMPANY.value) + \"' \";\n"); 
        appendDirtyJavaScript(" }\n");
        appendDirtyJavaScript(" if (document.form.ITEM7_TEAM_ID.value != '')\n");
        appendDirtyJavaScript("	   whereCond1 += \" AND 1 IN (SELECT Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,ROLE_CODE,MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.ITEM7_TEAM_ID.value+\"' AND CONTRACT='\" + URLClientEncode(document.form.ITEM7_TEAM_CONTRACT.value) +\"' AND \" +whereCond2 +\")\";\n"); 
        appendDirtyJavaScript("	var enable_multichoice =(true && ITEM7_IN_FIND_MODE);\n");
        appendDirtyJavaScript("	var key_value = (getValue_('ITEM7_ROLE_CODE',i).indexOf('%') !=-1)? getValue_('ITEM7_ROLE_CODE',i):'';\n");
        appendDirtyJavaScript("	if( getValue_('ITEM7_SIGN_ID',i)=='' )\n");
        appendDirtyJavaScript("       {\n");
        appendDirtyJavaScript("                  openLOVWindow('ITEM7_ROLE_CODE',i,\n");
        appendDirtyJavaScript("                     '../mscomw/RoleToSiteLov.page?__FIELD=Craft+ID&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM7_ROLE_CODE',i))\n"); 
        appendDirtyJavaScript("                      + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM7_COMPANY',i))\n");
        appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM7_CONTRACT',i))\n");       
        appendDirtyJavaScript("       	             ,550,500,'validateItem7RoleCode');\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript(" else\n");
        appendDirtyJavaScript("       {\n");        
        appendDirtyJavaScript("              openLOVWindow('ITEM7_ROLE_CODE',i,\n");
        appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Craft+ID&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM7_ROLE_CODE',i))\n"); 
        appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM7_ORG_CODE',i))\n");
        appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('ITEM7_SIGN_ID',i))\n");
        appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM7_COMPANY',i))\n");
        appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM7_CONTRACT',i))\n");       
        appendDirtyJavaScript("       	   ,550,500,'validateItem7RoleCode');\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateItem7RoleCode(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM7',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem7RoleCode(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('ITEM7_ROLE_CODE',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('ITEM7_COST',i).value = '';\n");
        appendDirtyJavaScript("		getField_('ITEM7_COSTAMOUNT',i).value = '';\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM7_ROLE_CODE'\n"); 
        appendDirtyJavaScript("		+ '&ITEM7_ROLE_CODE=' + URLClientEncode(getValue_('ITEM7_ROLE_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_CONTRACT=' + URLClientEncode(getValue_('ITEM7_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_ORG_CODE=' + URLClientEncode(getValue_('ITEM7_ORG_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_PLAN_MEN=' + URLClientEncode(getValue_('ITEM7_PLAN_MEN',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_PLAN_HRS=' + URLClientEncode(getValue_('ITEM7_PLAN_HRS',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_SIGN=' + URLClientEncode(getValue_('ITEM7_SIGN',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_WO_NO=' + URLClientEncode(getValue_('ITEM7_WO_NO',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM7_ROLE_CODE',i,'Craft ID') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_COST',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_COSTAMOUNT',i,1);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_ROLE_CODE',i,2);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_CONTRACT',i,3);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_ROLE_CODE_DESC',i,4);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateItem7PlanMen(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM7',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem7PlanMen(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('ITEM7_PLAN_MEN',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('ITEM7_COST',i).value = '';\n");
        appendDirtyJavaScript("		getField_('ITEM7_COSTAMOUNT',i).value = '';\n");
        appendDirtyJavaScript("		getField_('ITEM7_TOTAL_MAN_HOURS',i).value = '';\n");
        appendDirtyJavaScript("		getField_('ITEM7_TOTAL_REMAINING_HOURS',i).value = '';\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM7_PLAN_MEN'\n");
        appendDirtyJavaScript("		+ '&ITEM7_PLAN_MEN=' + URLClientEncode(getValue_('ITEM7_PLAN_MEN',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_PLAN_HRS=' + URLClientEncode(getValue_('ITEM7_PLAN_HRS',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_COST=' + URLClientEncode(getValue_('ITEM7_COST',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_CONTRACT=' + URLClientEncode(getValue_('ITEM7_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_ORG_CODE=' + URLClientEncode(getValue_('ITEM7_ORG_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_ROLE_CODE=' + URLClientEncode(getValue_('ITEM7_ROLE_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_TOTAL_ALLOCATED_HOURS=' + URLClientEncode(getValue_('ITEM7_TOTAL_ALLOCATED_HOURS',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM7_PLAN_MEN',i,'Pl.Men') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_COST',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_COSTAMOUNT',i,1);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_TOTAL_MAN_HOURS',i,2);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_TOTAL_REMAINING_HOURS',i,3);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateItem7PlanHrs(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM7',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem7PlanHrs(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('ITEM7_PLAN_HRS',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('ITEM7_COST',i).value = '';\n");
        appendDirtyJavaScript("		getField_('ITEM7_COSTAMOUNT',i).value = '';\n");
        appendDirtyJavaScript("		getField_('ITEM7_TOTAL_MAN_HOURS',i).value = '';\n");
        appendDirtyJavaScript("		getField_('ITEM7_TOTAL_REMAINING_HOURS',i).value = '';\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM7_PLAN_HRS'\n");
        appendDirtyJavaScript("		+ '&ITEM7_PLAN_MEN=' + URLClientEncode(getValue_('ITEM7_PLAN_MEN',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_PLAN_HRS=' + URLClientEncode(getValue_('ITEM7_PLAN_HRS',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_COST=' + URLClientEncode(getValue_('ITEM7_COST',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_CONTRACT=' + URLClientEncode(getValue_('ITEM7_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_ORG_CODE=' + URLClientEncode(getValue_('ITEM7_ORG_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_ROLE_CODE=' + URLClientEncode(getValue_('ITEM7_ROLE_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_TOTAL_ALLOCATED_HOURS=' + URLClientEncode(getValue_('ITEM7_TOTAL_ALLOCATED_HOURS',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM7_PLAN_HRS',i,'Pl.Hrs') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_COST',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_COSTAMOUNT',i,1);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_TOTAL_MAN_HOURS',i,2);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_TOTAL_REMAINING_HOURS',i,3);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n"); 

        appendDirtyJavaScript("function lovItem7Sign(i,params)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if(params) param = params;\n");
        appendDirtyJavaScript("	else param = '';\n");
        appendDirtyJavaScript("	whereCond1 = '';\n"); 
        appendDirtyJavaScript("	whereCond2 = '';\n");
        appendDirtyJavaScript(" if (document.form.ITEM7_ROLE_CODE.value == '')\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("       if ((document.form.ITEM7_ORG_CODE.value != '') && (document.form.ITEM7_CONTRACT.value == '') && (document.form.ITEM7_COMPANY.value != ''))\n");
        appendDirtyJavaScript("          {\n");
        appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM7_COMPANY.value) + \"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM7_ORG_CODE.value)+\"' \";\n"); 
        appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM7_COMPANY.value) + \"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM7_ORG_CODE.value)+\"' \";\n"); 
        appendDirtyJavaScript("          }\n");
        appendDirtyJavaScript("       else if(document.form.ITEM7_ORG_CODE.value == '' && document.form.ITEM7_CONTRACT.value != '' && document.form.ITEM7_COMPANY.value != '')\n");
        appendDirtyJavaScript("          {\n");
        appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM7_COMPANY.value) + \"' AND CONTRACT = '\" +URLClientEncode(document.form.ITEM7_CONTRACT.value)+\"' \";\n"); 
        appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM7_COMPANY.value) + \"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM7_CONTRACT.value)+\"' \";\n");
        appendDirtyJavaScript("          }\n");
        appendDirtyJavaScript("       else if(document.form.ITEM7_ORG_CODE.value != '' && document.form.ITEM7_CONTRACT.value != '' && document.form.ITEM7_COMPANY.value != '')\n");
        appendDirtyJavaScript("          {\n");
        appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM7_COMPANY.value) + \"' AND CONTRACT = '\" +URLClientEncode(document.form.ITEM7_CONTRACT.value)+\"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM7_ORG_CODE.value)+\"' \";\n"); 
        appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM7_COMPANY.value) + \"' AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM7_CONTRACT.value)+\"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM7_ORG_CODE.value)+\"' \";\n");
        appendDirtyJavaScript("          }\n");
        appendDirtyJavaScript("       else if(document.form.ITEM7_ORG_CODE.value == '' && document.form.ITEM7_CONTRACT.value == '' && document.form.ITEM7_COMPANY.value != '')\n");
        appendDirtyJavaScript("          {\n");
        appendDirtyJavaScript("		 whereCond1 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM7_COMPANY.value) + \"' \";\n"); 
        appendDirtyJavaScript("		 whereCond2 = \" COMPANY = '\" + URLClientEncode(document.form.ITEM7_COMPANY.value) + \"' \";\n");
        appendDirtyJavaScript("          }\n");
        appendDirtyJavaScript("       else\n");
        appendDirtyJavaScript("       {\n");
        appendDirtyJavaScript("		 whereCond1 = \" COMPANY IS NOT NULL \";\n"); 
        appendDirtyJavaScript("		 whereCond2 = \" COMPANY IS NOT NULL \";\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript(" else\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("    if((document.form.ITEM7_ORG_CODE.value != '') && (document.form.ITEM7_CONTRACT.value != '') && (document.form.ITEM7_COMPANY.value != ''))\n");
        appendDirtyJavaScript("          {\n");
        appendDirtyJavaScript("		 whereCond1 = \" ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM7_CONTRACT.value)+\"' AND ORG_CODE = '\" +URLClientEncode(document.form.ITEM7_ORG_CODE.value)+\"' AND ROLE_CODE='\" +URLClientEncode(document.form.ITEM7_ROLE_CODE.value)+\"' \";\n"); 
        appendDirtyJavaScript("		 whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM7_CONTRACT.value)+\"' AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM7_ORG_CODE.value)+\"' \";\n");
        appendDirtyJavaScript("          }\n");
        appendDirtyJavaScript("       else if(document.form.ITEM7_ORG_CODE.value == '' && document.form.ITEM7_CONTRACT.value == '' && document.form.ITEM7_COMPANY.value != '')\n");
        appendDirtyJavaScript("          {\n");
        appendDirtyJavaScript("		 whereCond1 = \" ROLE_CODE = '\" + URLClientEncode(document.form.ITEM7_ROLE_CODE.value) + \"' \";\n"); 
        appendDirtyJavaScript("          }\n");
        appendDirtyJavaScript("       else\n");
        appendDirtyJavaScript("          {\n");
        appendDirtyJavaScript("		 whereCond1 = \" COMPANY IS NOT NULL AND ROLE_CODE = '\" + URLClientEncode(document.form.ITEM7_ROLE_CODE.value) + \"' \";\n");  
        appendDirtyJavaScript("		 whereCond2 = \" COMPANY IS NOT NULL \";\n"); 
        appendDirtyJavaScript("          }\n");
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript(" if(document.form.ITEM7_TEAM_ID.value != '')\n");                           
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("    if(document.form.ITEM7_ROLE_CODE.value == '')\n");
        appendDirtyJavaScript("	   whereCond1 += \" AND EMPLOYEE_ID IN (SELECT MEMBER_EMP_NO FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID='\" + URLClientEncode(document.form.ITEM7_TEAM_ID.value) +\"' AND CONTRACT = '\"+ document.form.ITEM7_TEAM_CONTRACT.value+\"' AND \" +whereCond2 +\") \";\n"); 
        appendDirtyJavaScript("       else\n");
        appendDirtyJavaScript("	   whereCond1 += \" AND (EMP_NO,1)IN (SELECT MEMBER_EMP_NO,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\" + URLClientEncode(document.form.ITEM7_ROLE_CODE.value) + \"',MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID='\" + URLClientEncode(document.form.ITEM7_TEAM_ID.value) +\"' AND CONTRACT = '\"+ document.form.ITEM7_TEAM_CONTRACT.value+\"' AND \" +whereCond2 +\") \";\n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("	var enable_multichoice =(true && ITEM7_IN_FIND_MODE);\n");
        appendDirtyJavaScript("	var key_value = (getValue_('ITEM7_SIGN',i).indexOf('%') !=-1)? getValue_('ITEM7_SIGN',i):'';\n");
        appendDirtyJavaScript("	if( getValue_('ITEM7_ROLE_CODE',i)=='' )\n");
        appendDirtyJavaScript("       {\n");
        appendDirtyJavaScript("              openLOVWindow('ITEM7_SIGN',i,\n");
        appendDirtyJavaScript("                  '../mscomw/MaintEmployeeLov.page?__FIELD=Executed+By&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM7_SIGN',i))\n"); 
        appendDirtyJavaScript("                  + '&PERSON_ID=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM7_ORG_CODE',i))\n");
        appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM7_COMPANY',i))\n");
        appendDirtyJavaScript("                  + '&CONTRACT=' + URLClientEncode(getValue_('ITEM7_CONTRACT',i))\n");       
        appendDirtyJavaScript("       	   ,550,500,'validateItem7Sign');\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript(" else\n");
        appendDirtyJavaScript("       {\n");        
        appendDirtyJavaScript("              openLOVWindow('ITEM7_SIGN',i,\n");
        appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Executed+By&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM7_SIGN',i))\n"); 
        appendDirtyJavaScript("                  + '&SIGNATURE=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM7_ORG_CODE',i))\n");
        appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM7_COMPANY',i))\n");
        appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM7_CONTRACT',i))\n");       
        appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(getValue_('ITEM7_ROLE_CODE',i))\n");       
        appendDirtyJavaScript("       	   ,550,500,'validateItem7Sign');\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateItem7Sign(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM7',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem7Sign(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('ITEM7_SIGN',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('ITEM7_SIGN_ID',i).value = '';\n");
        appendDirtyJavaScript("		getField_('ITEM7_ROLE_CODE',i).value = '';\n");
        appendDirtyJavaScript("		getField_('ITEM7_COST',i).value = '';\n");
        appendDirtyJavaScript("		getField_('ITEM7_COSTAMOUNT',i).value = '';\n");
        appendDirtyJavaScript("		getField_('ITEM7_SIGN',i).value = '';\n");
        appendDirtyJavaScript("		getField_('ITEM7_ALLOCATED_HOURS',i).value = '';\n");
        appendDirtyJavaScript("		getField_('ITEM7_TOTAL_ALLOCATED_HOURS',i).value = '';\n");
        appendDirtyJavaScript("		getField_('ITEM7_TOTAL_REMAINING_HOURS',i).value = '';\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM7_SIGN'\n");
        appendDirtyJavaScript("		+ '&ITEM7_SIGN=' + URLClientEncode(getValue_('ITEM7_SIGN',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_COMPANY=' + URLClientEncode(getValue_('ITEM7_COMPANY',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_ROLE_CODE=' + URLClientEncode(getValue_('ITEM7_ROLE_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_CONTRACT=' + URLClientEncode(getValue_('ITEM7_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_ORG_CODE=' + URLClientEncode(getValue_('ITEM7_ORG_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ORG_CODE=' + URLClientEncode(getValue_('ITEM7_ORG_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_COST=' + URLClientEncode(getValue_('ITEM7_COST',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_PLAN_MEN=' + URLClientEncode(getValue_('ITEM7_PLAN_MEN',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_PLAN_HRS=' + URLClientEncode(getValue_('ITEM7_PLAN_HRS',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_ALLOCATED_HOURS=' + URLClientEncode(getValue_('ITEM7_ALLOCATED_HOURS',i))\n");
        appendDirtyJavaScript("		+ '&ITEM7_TOTAL_MAN_HOURS=' + URLClientEncode(getValue_('ITEM7_TOTAL_MAN_HOURS',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM7_SIGN',i,'Executed By') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_SIGN_ID',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_ROLE_CODE',i,1);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_ORG_CODE',i,2);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_COST',i,3);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_COSTAMOUNT',i,4);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_SIGN',i,5);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_CONTRACT',i,6);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_ALLOCATED_HOURS',i,7);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_TOTAL_ALLOCATED_HOURS',i,8);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_TOTAL_REMAINING_HOURS',i,9);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_ORG_CODE_DESC',i,10);\n");
        appendDirtyJavaScript("		assignValue_('ITEM7_ROLE_CODE_DESC',i,11);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n"); 

        appendDirtyJavaScript("function lovItem7TeamId(i,params)"); 
        appendDirtyJavaScript("{"); 
        appendDirtyJavaScript("	if(params) param = params;\n"); 
        appendDirtyJavaScript("	else param = '';\n"); 
        appendDirtyJavaScript("	whereCond1 = '';\n"); 
        appendDirtyJavaScript("	whereCond2 = '';\n");
        appendDirtyJavaScript(" if(document.form.ITEM7_TEAM_CONTRACT.value != '')\n");
        appendDirtyJavaScript("		whereCond1 = \"CONTRACT = '\" +URLClientEncode(document.form.ITEM7_TEAM_CONTRACT.value)+\"' \";\n"); 
        appendDirtyJavaScript(" else\n");
        appendDirtyJavaScript("		whereCond1 = \"CONTRACT IS NOT NULL \";\n"); 
        appendDirtyJavaScript(" if(document.form.ITEM7_COMPANY.value != '')\n");
        appendDirtyJavaScript(" if( whereCond1=='')\n");
        appendDirtyJavaScript("		whereCond1 = \"COMPANY = '\" +URLClientEncode(document.form.ITEM7_COMPANY.value)+\"' \";\n"); 
        appendDirtyJavaScript(" else\n");
        appendDirtyJavaScript("		whereCond1 += \" AND COMPANY = '\" +URLClientEncode(document.form.ITEM7_COMPANY.value)+\"' \";\n"); 
        appendDirtyJavaScript(" if( whereCond1 !='')\n");
        appendDirtyJavaScript("	           whereCond1 += \" AND \";\n"); 
        appendDirtyJavaScript(" whereCond1 += \" nvl(to_date(to_char(to_date('\" +URLClientEncode(document.form.ITEM7_DATE_FROM_MASKED.value)+\"','" +getDateTimeFormat("SQL") + "'),\'YYYYMMDD\'),\'YYYYMMDD\'),to_date(to_char(sysdate,\'YYYYMMDD\'),\'YYYYMMDD\')) BETWEEN nvl(VALID_FROM,to_date(\'00010101\',\'YYYYMMDD\')) AND nvl(VALID_TO,to_date(\'99991231\',\'YYYYMMDD\')) \";\n"); 
        appendDirtyJavaScript(" if(document.form.ITEM7_SIGN_ID.value != '')\n");
        appendDirtyJavaScript("		whereCond2 = \"MEMBER_EMP_NO = '\" +URLClientEncode(document.form.ITEM7_SIGN_ID.value)+\"' \";\n"); 
        appendDirtyJavaScript(" if(document.form.ITEM7_ORG_CODE.value != '')\n");
        appendDirtyJavaScript(" {\n");
        appendDirtyJavaScript(" if( whereCond2=='')\n");
        appendDirtyJavaScript("		whereCond2 = \"MAINT_ORG = '\" +URLClientEncode(document.form.ITEM7_ORG_CODE.value)+\"' \";\n"); 
        appendDirtyJavaScript(" else \n");
        appendDirtyJavaScript("		whereCond2+= \" AND MAINT_ORG = '\" +URLClientEncode(document.form.ITEM7_ORG_CODE.value)+\"' \";\n"); 
        appendDirtyJavaScript(" }\n");
        appendDirtyJavaScript(" if(document.form.ITEM7_CONTRACT.value != '')\n");
        appendDirtyJavaScript(" {\n");
        appendDirtyJavaScript(" if(whereCond2=='' )\n");
        appendDirtyJavaScript("		whereCond2 = \"MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM7_CONTRACT.value)+\"' \";\n"); 
        appendDirtyJavaScript(" else \n");
        appendDirtyJavaScript("		whereCond2 += \" AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ITEM7_CONTRACT.value)+\"' \";\n"); 
        appendDirtyJavaScript(" }\n");
        appendDirtyJavaScript(" if(whereCond2 !='' )\n");
        appendDirtyJavaScript("     {\n");
        appendDirtyJavaScript("        if(whereCond1 !='' )\n");
        appendDirtyJavaScript("	           whereCond1 += \" AND \";\n"); 
        appendDirtyJavaScript("        if(document.form.ITEM7_ROLE_CODE.value == '' )\n");
        appendDirtyJavaScript("	           whereCond1 += \" TEAM_ID IN (SELECT TEAM_ID FROM MAINT_TEAM_MEMBER_SERV WHERE \" +whereCond2 +\")\";\n"); 
        appendDirtyJavaScript("        else \n");
        appendDirtyJavaScript("	           whereCond1 += \" (TEAM_ID,1) IN (SELECT TEAM_ID,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\"+ document.form.ITEM7_ROLE_CODE.value+\"' ,MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE \" +whereCond2 +\")\";\n"); 
        appendDirtyJavaScript("     }\n");
        appendDirtyJavaScript("	var enable_multichoice =(true && ITEM7_IN_FIND_MODE);\n"); 
        appendDirtyJavaScript("	var key_value = (getValue_('ITEM7_TEAM_ID',i).indexOf('%') !=-1)? getValue_('ITEM7_TEAM_ID',i):'';\n"); 
        appendDirtyJavaScript("	openLOVWindow('ITEM7_TEAM_ID',i,\n"); 
        appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=MAINT_TEAM&__FIELD=Team+Id&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''"); 
        appendDirtyJavaScript("		+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM7_TEAM_ID',i))"); 
        appendDirtyJavaScript("		+ '&ITEM7_TEAM_ID=' + URLClientEncode(key_value)"); 
        appendDirtyJavaScript("		,550,500,'validateItem7TeamId');\n"); 
        appendDirtyJavaScript("}\n"); 

        appendDirtyJavaScript("function lovItem7TeamContract(i,params)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if(params) param = params;\n");
        appendDirtyJavaScript("	else param = '';\n");
        appendDirtyJavaScript("	whereCond1 = '';\n"); 
        appendDirtyJavaScript(" if (document.form.ITEM7_COMPANY.value != '') \n");
        appendDirtyJavaScript(" {\n");
        appendDirtyJavaScript("	      whereCond1 = \" Site_API.Get_Company(CONTRACT) = '\" + URLClientEncode(document.form.ITEM7_COMPANY.value) + \"' \";\n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("	var enable_multichoice =(true && ITEM7_IN_FIND_MODE);\n");
        appendDirtyJavaScript("	var key_value = (getValue_('ITEM7_TEAM_CONTRACT',i).indexOf('%') !=-1)? getValue_('ITEM7_TEAM_CONTRACT',i):'';\n");
        appendDirtyJavaScript("                  openLOVWindow('ITEM7_TEAM_CONTRACT',i,\n");
        appendDirtyJavaScript("'");
        appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=USER_ALLOWED_SITE_LOV&__FIELD=Team+Site&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM7_TEAM_CONTRACT',i))\n");
        appendDirtyJavaScript("+ '&ITEM7_TEAM_CONTRACT=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript(",550,500,'validateItem7TeamContract');\n");
        appendDirtyJavaScript("}\n");

        //validations for multiple allocations in operations tab.
        //-------------------------------------------------------------------


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
        appendDirtyJavaScript("	   whereCond1 += \" ORG_CODE IN (SELECT MAINT_ORG FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.ITEM8_TEAM_ID.value+\"' AND CONTRACT='\" + URLClientEncode(document.form.ITEM8_TEAM_CONTRACT.value) +\"' \";\n");
        appendDirtyJavaScript("    else\n");
        appendDirtyJavaScript("	   whereCond1 += \" (ORG_CODE,EMP_NO,1) IN (SELECT MAINT_ORG,MEMBER_EMP_NO,,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\"+ document.form.ITEM8_ROLE_CODE.value+\"',MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.ITEM8_TEAM_ID.value+\"' AND CONTRACT='\" + URLClientEncode(document.form.ITEM8_TEAM_CONTRACT.value) +\"' \";\n");
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

        appendDirtyJavaScript("function validateItem8Sign(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("if( getRowStatus_('ITEM8',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("setDirty();\n");
        appendDirtyJavaScript("if( !checkItem8Sign(i) ) return;\n");
        appendDirtyJavaScript("if( getValue_('ITEM8_SIGN',i)=='' )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("	   getField_('ITEM8_SIGN_ID',i).value = '';\n");
        appendDirtyJavaScript("	   getField_('ITEM8_ROLE_CODE',i).value = '';\n");
        appendDirtyJavaScript("	   getField_('ITEM8_SIGN',i).value = '';\n");
        appendDirtyJavaScript("	   return;\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("  window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM8_SIGN'\n");
        appendDirtyJavaScript("	+ '&ITEM8_COMPANY=' + URLClientEncode(getValue_('ITEM8_COMPANY',i))\n");
        appendDirtyJavaScript("	+ '&ITEM8_SIGN=' + URLClientEncode(getValue_('ITEM8_SIGN',i))\n");
        appendDirtyJavaScript("	+ '&ITEM8_ROLE_CODE=' + URLClientEncode(getValue_('ITEM8_ROLE_CODE',i))\n");
        appendDirtyJavaScript("	+ '&ITEM8_CONTRACT=' + URLClientEncode(getValue_('ITEM8_CONTRACT',i))\n");
        appendDirtyJavaScript("	+ '&ITEM8_ORG_CODE=' + URLClientEncode(getValue_('ITEM8_ORG_CODE',i))\n");
        appendDirtyJavaScript("	+ '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
        appendDirtyJavaScript("	);\n");
        appendDirtyJavaScript("window.status='';\n");
        appendDirtyJavaScript("\n");
        appendDirtyJavaScript("if( checkStatus_(r,'ITEM8_SIGN',i,'Executed By') )\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	assignValue_('ITEM8_SIGN_ID',i,0);\n");
        appendDirtyJavaScript("	assignValue_('ITEM8_ROLE_CODE',i,1);\n");
        appendDirtyJavaScript("	assignValue_('ITEM8_ORG_CODE',i,2);\n");
        appendDirtyJavaScript("	assignValue_('ITEM8_SIGN',i,3);\n");
        appendDirtyJavaScript("	assignValue_('ITEM8_CONTRACT',i,4);\n");
        appendDirtyJavaScript("	assignValue_('ITEM8_ORG_CODE_DESC',i,5);\n");
        appendDirtyJavaScript("	assignValue_('ITEM8_ROLE_CODE_DESC',i,6);\n");
        appendDirtyJavaScript("}\n");
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
        appendDirtyJavaScript("	   whereCond1 += \" AND 1 IN (SELECT Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,ROLE_CODE,MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.ITEM8_TEAM_ID.value+\"' AND CONTRACT='\" + URLClientEncode(document.form.ITEM8_TEAM_CONTRACT.value) +\"' AND \" +whereCond2 +\")\";\n"); 
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


        //-------------------------------------------------------------------
        appendDirtyJavaScript("function lovPredecessor(i,params)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   document.form.REFRESHPARENT1.value = \"TRUE\";\n");
        appendDirtyJavaScript("   submit();\n");  
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovItem7Predecessor(i,params)\n");
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
        appendDirtyJavaScript("        getField_('ITEM7_PREDECESSOR', -1).value = sPredList;\n");
        appendDirtyJavaScript("  }\n");
        appendDirtyJavaScript("else\n");
        appendDirtyJavaScript("  {\n");
        appendDirtyJavaScript("     if (sblock =='ITEM2') \n");
        appendDirtyJavaScript("       getField_('PREDECESSOR', -1).value = '';\n");
        appendDirtyJavaScript("     else\n");
        appendDirtyJavaScript("       getField_('ITEM7_PREDECESSOR', -1).value = '';\n");
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


        appendDirtyJavaScript("function Item7PlanMenCheck()\n");
        appendDirtyJavaScript("{\n");
        if (!mgr.isEmpty(planMen))
        {
            if (Integer.parseInt(planMen) < 2)
            {
                appendDirtyJavaScript("if (document.form.ITEM7_PLAN_MEN.value>1) \n");
                appendDirtyJavaScript("{\n");
                appendDirtyJavaScript("      if (confirm(\"");
                appendDirtyJavaScript(mgr.translateJavaScript("PCMWACTIVESEPARATE2ITEM7WARNING: Updating Planned Men when having an employee allocated to the operation will move the employee to the multi-allocation level instead."));
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

        // LOVs are filtered by COMPANY if not in FIND mode
        if (headlay.isFindLayout())
        {
            appendDirtyJavaScript("function lovReportedBy(i,params)");
            appendDirtyJavaScript("{");
            appendDirtyJavaScript("	if(params) param = params;");
            appendDirtyJavaScript("	else param = '';");
            appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);");
            appendDirtyJavaScript("	var key_value = (getValue_('REPORTED_BY',i).indexOf('%') !=-1)? getValue_('REPORTED_BY',i):'';");
            appendDirtyJavaScript("	openLOVWindow('REPORTED_BY',i,");
            appendDirtyJavaScript("		APP_ROOT + 'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=Reported+by&__INIT=1' + '&__TITLE=PCMWACTIVESEPARATE2LOVREPORTEDBY%3A+List+of+Reported+by&MULTICHOICE='+enable_multichoice+''");
            appendDirtyJavaScript("		+ '&__KEY_VALUE=' + URLClientEncode(getValue_('REPORTED_BY',i))");
            appendDirtyJavaScript("		+ '&REPORTED_BY=' + URLClientEncode(key_value)");
            appendDirtyJavaScript("		,600,450,'validateReportedBy');");
            appendDirtyJavaScript("}");

            appendDirtyJavaScript("function lovPreparedBy(i,params)");
            appendDirtyJavaScript("{");
            appendDirtyJavaScript("	if(params) param = params;");
            appendDirtyJavaScript("	else param = '';");
            appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);");
            appendDirtyJavaScript("	var key_value = (getValue_('PREPARED_BY',i).indexOf('%') !=-1)? getValue_('PREPARED_BY',i):'';");
            appendDirtyJavaScript("	openLOVWindow('PREPARED_BY',i,");
            appendDirtyJavaScript("		APP_ROOT + 'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=Prepared+by&__INIT=1' + '&__TITLE=PCMWACTIVESEPARATE2LOVTITLE3%3A+List+of+Prepared+by&MULTICHOICE='+enable_multichoice+''");
            appendDirtyJavaScript("		+ '&__KEY_VALUE=' + URLClientEncode(getValue_('PREPARED_BY',i))");
            appendDirtyJavaScript("		+ '&PREPARED_BY=' + URLClientEncode(key_value)");
            appendDirtyJavaScript("		,600,450,'validatePreparedBy');");
            appendDirtyJavaScript("}");

            appendDirtyJavaScript("function lovWorkLeaderSign(i,params)");
            appendDirtyJavaScript("{");
            appendDirtyJavaScript("	if(params) param = params;");
            appendDirtyJavaScript("	else param = '';");
            appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);");
            appendDirtyJavaScript("	var key_value = (getValue_('WORK_LEADER_SIGN',i).indexOf('%') !=-1)? getValue_('WORK_LEADER_SIGN',i):'';");
            appendDirtyJavaScript("	openLOVWindow('WORK_LEADER_SIGN',i,");
            appendDirtyJavaScript("		APP_ROOT + 'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=Work+Leader&__INIT=1' + '&__TITLE=PCMWACTIVESEPARATE2LOVTITLEWLEADER%3A+List+of+Work+Leader&MULTICHOICE='+enable_multichoice+''");
            appendDirtyJavaScript("		+ '&__KEY_VALUE=' + URLClientEncode(getValue_('WORK_LEADER_SIGN',i))");
            appendDirtyJavaScript("		+ '&WORK_LEADER_SIGN=' + URLClientEncode(key_value)");
            appendDirtyJavaScript("		,600,450,'validateWorkLeaderSign');");
            appendDirtyJavaScript("}");

            appendDirtyJavaScript("function lovWorkMasterSign(i,params)");
            appendDirtyJavaScript("{");
            appendDirtyJavaScript("	if(params) param = params;");
            appendDirtyJavaScript("	else param = '';");
            appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);");
            appendDirtyJavaScript("	var key_value = (getValue_('WORK_MASTER_SIGN',i).indexOf('%') !=-1)? getValue_('WORK_MASTER_SIGN',i):'';");
            appendDirtyJavaScript("	openLOVWindow('WORK_MASTER_SIGN',i,");
            appendDirtyJavaScript("		APP_ROOT + 'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=MAINT_EMPLOYEE&__FIELD=Executed+By&__INIT=1' + '&__TITLE=PCMWACTIVESEPARATE2LOVTITLE4%3A+List+of+Employees&MULTICHOICE='+enable_multichoice+''");
            appendDirtyJavaScript("		+ '&__KEY_VALUE=' + URLClientEncode(getValue_('WORK_MASTER_SIGN',i))");
            appendDirtyJavaScript("		+ '&WORK_MASTER_SIGN=' + URLClientEncode(key_value)");
            appendDirtyJavaScript("		,600,450,'validateWorkMasterSign');");
            appendDirtyJavaScript("}");
        }
        else
        {
            appendDirtyJavaScript("function lovReportedBy(i,params)");
            appendDirtyJavaScript("{");
            appendDirtyJavaScript("	if(params) param = params;");
            appendDirtyJavaScript("	else param = '';");
            appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);");
            appendDirtyJavaScript("	var key_value = (getValue_('REPORTED_BY',i).indexOf('%') !=-1)? getValue_('REPORTED_BY',i):'';");
            appendDirtyJavaScript("	openLOVWindow('REPORTED_BY',i,");
            appendDirtyJavaScript("		APP_ROOT + 'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=Reported+by&__INIT=1' + '&__TITLE=PCMWACTIVESEPARATE2LOVREPORTEDBY%3A+List+of+Reported+by&MULTICHOICE='+enable_multichoice+''");
            appendDirtyJavaScript("		+ '&__KEY_VALUE=' + URLClientEncode(getValue_('REPORTED_BY',i))");
            appendDirtyJavaScript("		+ '&REPORTED_BY=' + URLClientEncode(key_value)");
            appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))");
            appendDirtyJavaScript("		,600,450,'validateReportedBy');");
            appendDirtyJavaScript("}");

            appendDirtyJavaScript("function lovPreparedBy(i,params)");
            appendDirtyJavaScript("{");
            appendDirtyJavaScript("	if(params) param = params;");
            appendDirtyJavaScript("	else param = '';");
            appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);");
            appendDirtyJavaScript("	var key_value = (getValue_('PREPARED_BY',i).indexOf('%') !=-1)? getValue_('PREPARED_BY',i):'';");
            appendDirtyJavaScript("	openLOVWindow('PREPARED_BY',i,");
            appendDirtyJavaScript("		APP_ROOT + 'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=Prepared+by&__INIT=1' + '&__TITLE=PCMWACTIVESEPARATE2LOVTITLE3%3A+List+of+Prepared+by&MULTICHOICE='+enable_multichoice+''");
            appendDirtyJavaScript("		+ '&__KEY_VALUE=' + URLClientEncode(getValue_('PREPARED_BY',i))");
            appendDirtyJavaScript("		+ '&PREPARED_BY=' + URLClientEncode(key_value)");
            appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))");
            appendDirtyJavaScript("		,600,450,'validatePreparedBy');");
            appendDirtyJavaScript("}");

            appendDirtyJavaScript("function lovWorkLeaderSign(i,params)");
            appendDirtyJavaScript("{");
            appendDirtyJavaScript("	if(params) param = params;");
            appendDirtyJavaScript("	else param = '';");
            appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);");
            appendDirtyJavaScript("	var key_value = (getValue_('WORK_LEADER_SIGN',i).indexOf('%') !=-1)? getValue_('WORK_LEADER_SIGN',i):'';");
            appendDirtyJavaScript("	openLOVWindow('WORK_LEADER_SIGN',i,");
            appendDirtyJavaScript("		APP_ROOT + 'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=Work+Leader&__INIT=1' + '&__TITLE=PCMWACTIVESEPARATE2LOVTITLEWLEADER%3A+List+of+Work+Leader&MULTICHOICE='+enable_multichoice+''");
            appendDirtyJavaScript("		+ '&__KEY_VALUE=' + URLClientEncode(getValue_('WORK_LEADER_SIGN',i))");
            appendDirtyJavaScript("		+ '&WORK_LEADER_SIGN=' + URLClientEncode(key_value)");
            appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))");
            appendDirtyJavaScript("		,600,450,'validateWorkLeaderSign');");
            appendDirtyJavaScript("}");

            appendDirtyJavaScript("function lovWorkMasterSign(i,params)");
            appendDirtyJavaScript("{");
            appendDirtyJavaScript("	if(params) param = params;");
            appendDirtyJavaScript("	else param = '';");
            appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);");
            appendDirtyJavaScript("	var key_value = (getValue_('WORK_MASTER_SIGN',i).indexOf('%') !=-1)? getValue_('WORK_MASTER_SIGN',i):'';");
            appendDirtyJavaScript("	openLOVWindow('WORK_MASTER_SIGN',i,");
            appendDirtyJavaScript("		APP_ROOT + 'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=MAINT_EMPLOYEE&__FIELD=Executed+By&__INIT=1' + '&__TITLE=PCMWACTIVESEPARATE2LOVTITLE4%3A+List+of+Employees&MULTICHOICE='+enable_multichoice+''");
            appendDirtyJavaScript("		+ '&__KEY_VALUE=' + URLClientEncode(getValue_('WORK_MASTER_SIGN',i))");
            appendDirtyJavaScript("		+ '&WORK_MASTER_SIGN=' + URLClientEncode(key_value)");
            appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))");
            appendDirtyJavaScript("		,600,450,'validateWorkMasterSign');");
            appendDirtyJavaScript("}");
        }

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

	//Bug 82543, Start
	appendDirtyJavaScript("function refreshPage()\n");
	appendDirtyJavaScript("{\n");
	appendDirtyJavaScript("   f.REFRESH_FLAG.value = 'TRUE';\n");
	appendDirtyJavaScript("   f.submit();\n");
	appendDirtyJavaScript("}\n");
	//Bug 82543, End
    }
}

