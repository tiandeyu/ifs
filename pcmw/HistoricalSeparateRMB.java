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
*  File        : HistoricalSeparateRMB.java 
*  Created     : ARWILK 010215
*  Modified    :
*  CHCRLK  010802  Added sections Prepare, Prepare Codes, Signatures and Work Center Load.
*  BUNILK  011016  Added new field PLANNED_MAN_HRS to headblk.
*  JEWILK  011018  Modified okFindITEM..()  fuctions and removed unnecessary okFindITEM..Temp() functions.
*  JEWILK  011008  Checked security permissions for RMB Actions.
*  SHAFLK  020726  Bug Id 31771 Added security check for Work_Center_Int_API.
*  BUNILK  020826  Bug Id 32286 Added setDefaultNotVisible() method for some fields and removed 
*                  setHidden() property of them which was set conditionaly from adjust() method.     
*  BUNILK  020828  Bug Id 32307 Added new menu option 'Print...' for Action menu. 
*  BUNILK  020909  Bug Id 32379 new method isModuleInst() to check availability of ORDER and DOCMAN modules inside preDefine method.
*  JEJALK  021127  Takeoff(beta) Modifications.
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  BUNILK  021213  Merged with 2002-3 SP3
*  CHCRLK  021218  Maintenance Order: Added new block Maint Task.
*  SHAFLK  030319  Modified method okFind() and LOv for Object Id.
*  JEJALK  030331  Renamed Crafts tab to Operations tab, Row No to Operations No.
*  JEJALK  030403  Added Tools and Facilities inforamtion.
*  CHCRLK  030430  Added CONDITION_CODE & CONDITION_CODE_DESC to Materials tab.
*  INROLK  030624  Added RMB Returns.
*  CHAMLK  030826  Added Ownership, Owner and Owner Name to Materials Tab.
*  CHAMLK  030827  Added Ownership, Owner and Owner Name to Posting Tab.
*  VAGULK  030910  Added OperParam tab to Maint Task. Asynchronous Reporting. 
*  CHAMLK  031022  Modified function preDefine() in order to remove uppercase from connection type.
*  THWILK  031024  Call ID 104789 - Added an extra parameter(#)to the deffinition of the field WO_NO.
*  ARWILK  031210  Edge Developments - (Replaced blocks with tabs, Replaced State links with RMB's, Removed Clone and Reset Methods)
*  VAGULK  040126  Made the field order according to the Centura application.
*  ARWILK  040128  Edge Developments - (Enable Multirow RMB actions, Remove uneccessary global variables, Enhance Performance, Clean Javascript Code)
* ------------------------------ EDGE - SP1 Merge ---------------------------
*  SHAFLK  040225  Bug Id 42925, modified method okFind().
*  VAGULK  040325  Merged with SP1.
*  ARWILK  040430  Added new field CBPROJCONNECTED.
*  SAPRLK  040608  Added key PM_REVISION. Called PmActionLov1.page to query for PM_NO.
*  ARWILK  040617  Added a new job tab. Added job_id to materials, operations, tools and facilities tabs.(Spec - AMEC109A)  
*  ARWILK  040722  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  ThWilk  040728  Added the field ACTIVITY_SEQ as a hidden field. (IID AMUT219)
*  ThWilk  040728  Added Project Information Block.(IID AMUT219).
*  SHAFLK  040707  Bug Id 44442, Added method setSalesPartDesc() and replaced SALESPARTCATALOGDESC with LINE_DESCRIPTION.
*  ThWilk  040806  Merged Bug 44442.
*  SHAFLK  040705  Bug Id 44107, Added some fields to Posting tab.
*  ThWilk  040806  Merged Bug Id 44107.
*  ARWILK  040820  Disable New,Remove,Duplicate,Edit operations for Jobs tab. Also modified okFindITEM9 method.
*  NIJALK  040825  Call 117312, Changed the name of the tab to 'Materials' (Earlier this was 'Meterials').
*  SHAFLK  040726  Bug 43249, Modified method preDefine().
*  NIJALK  040902  Merged bug 43249.
*  NIJALK  040929  Added parameter project_id to method calls to Inventory_Part_In_Stock_API.Get_Inventory_Quantity.
*  ARWILK  041001  LCS Merge:46394
*  VAGULK  041021  Added RMBs 'Print/Reprint Auth Release Certificate' and 'Release Certificate',Call ID 118856.
*  VAGULK  041103  Modified printAuthRelCerti().
*  SHAFLK  040916  Bug 46621, Modified method preDefine().
*  Chanlk  041105  Merged Bug 46621.
*  NIJALK  041105  Added field Std Job Status to Jobs tab.
*  NAMELK  041109  Duplicated Translation Tags Corrected.
*  NIJALK  041112  Modified preDefine().
*  DIAMLK  050301  Replaced the Pre_Posting_Id with Pre_Accounting_Id in headblk.(IID AMEC113 Mandatory Pre-posting)
*  THWILK  050617  Modified predefine(),checkObjAvailable() and added additionalQualifications().
*  THWILK  051013  Added the required functionality under AMEC114:Multiple Allocations.
*  THWILK  060126  Corrected localization errors.
*  THWILK  060307  Fixed Call136568,Modified predefine().
*  SULILK  060308  Call 136634: Modified okFindITEM10(), preDefine(). 
*  NIJALK  060322  Renamed 'Work Master' with 'Executed By'.
*  NEKOLK  060510  Bug 57371,Added window.name.
*  AMNILK  060629  Merged with SP1 APP7.
*  ILSOLK  060727  Merged Bug ID 59224.
*  JEWILK  060803  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged Bug Id: 58214.
*  DIAMLK  060818  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMNILK  060906  Merged Bug Id: 58216.
*  AMDILK  060911  MTPR904: Request id 21686. Add a new RMB option "Copy Historical Work Order". Add a
*                  new method "copy()" and modified preDefine()
*  AMDILK  060913  MTPR904: Request id 35874. Insert a confirmation to view the reopened WO. 
*                  Modofied methods printContents(), preDefine(), reopen()
*  AMNILK  061110  MEPR604: Original Source info in voucher rows.Modified adjust().
*  BUNILK  070524  Bug 65048 Added format mask for PM_NO field.
*  CHANLK  070711  Merged bugId 65048
*  AMNILK  070716  Eliminated XSS Secutiry vulnerability.
*  AMDILK  070802  Modified printContents() inorder to view historical WO's (when the quotation is given 
*                  as a query string value) in other web browsers
*  SHAFLK  071112  Bug 69193 Changed Number format of some fields.
*  CHANLK  071201  Bug 69168, Change field order in report in group
*  CHANLK  080205  Bug 66842,Remove maxLength of PM_NO
*  SHAFLK  080319  Bug 72455,Modified method reopen().
*  ILSOLK  080421  Bug 73136,Modified preDefine().
*  SHAFLK  080714  Bug 75563, Modified preDefine().
*  SUIJLK  090407  Bug 81463, Modified preDefine().
*  HARPLK  090710  Bug 82715, Modified preDefine().
*  CHANLK  090721  Bug 83532, Added RMB Work Order Address.
*  CHANLK  090730  Bug 82934  Column renamed. ToolFacility.Quantity to Planed Quantity.
*  SHAFLK  090828  Bug 85575, Modified preDefine().
*  SHAFLK  100121  Bug 87309, Modified preDefine().
*  NIJALK  100218  Bug 87766, Modified setCheckBoxes() and preDefine().
*  SaFalk  100729  Bug 89818, Modified preDefine().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class HistoricalSeparateRMB extends ASPPageProvider
{
	//===============================================================
	// Static constants 
	//===============================================================
	public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.HistoricalSeparateRMB");

	//===============================================================
	// Instances created on page creation (immutable attributes) 
	//===============================================================
	private ASPContext ctx;

	private ASPBlock prntblk;
	private ASPRowSet printset;

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

	private ASPBlock itemblk6;
	private ASPRowSet itemset6;
	private ASPCommandBar itembar6;
	private ASPTable itemtbl6;
	private ASPBlockLayout itemlay6;

	private ASPField f;

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

	private ASPBlock itemblk10;
	private ASPRowSet itemset10;
	private ASPCommandBar itembar10;
	private ASPTable itemtbl10;
	private ASPBlockLayout itemlay10;

        private ASPBuffer actionsBuffer;
	private ASPBuffer row;
	private ASPCommand cmd;
	// 031210  ARWILK  Begin  (Replace blocks with tabs)
	private ASPTabContainer tabs;
	// 031210  ARWILK  End  (Replace blocks with tabs)

	//===============================================================
	// Transient temporary variables (never cloned) 
	//===============================================================
	private ASPTransactionBuffer trans;
	private String calling_url;
	private boolean isSecurityChecked;
	// 040128  ARWILK  Begin  (Remove uneccessary global variables)
	private boolean bOpenNewWindow;
	private String urlString;
	private String newWinHandle;
        private boolean toGetMsages;
	// 040128  ARWILK  End  (Remove uneccessary global variables)	
	private String qrystr;
	private String performRMB;
	private String URLString;
	private String WindowName;
        private boolean bFirstRequest= false;
        private String sQuotationID;
        private ASPHTMLFormatter fmt;
        private boolean byBuff1= false;
       
	//===============================================================
	// Construction 
	//===============================================================
	public HistoricalSeparateRMB(ASPManager mgr, String page_path)
	{
		super(mgr,page_path);
	}

	public void run() 
	{
		ASPManager mgr = getASPManager();

		ctx = mgr.getASPContext();
		trans = mgr.newASPTransactionBuffer();

		isSecurityChecked = ctx.readFlag("SECURITYCHECKED",false);
		actionsBuffer = ctx.readBuffer("ACTIONSBUFFER");
                
		if (mgr.dataTransfered())
			calling_url = ctx.getGlobal("CALLING_URL");
		else
			calling_url	= mgr.getURL();

		ctx.setGlobal("CALLING_URL", calling_url);  

		if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
			okFind();
		else if (mgr.commandBarActivated())
			eval(mgr.commandBarFunction());
		else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
			validate();
		else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
		{
			okFind();
			trans.clear();
		}
                
		else if (mgr.dataTransfered())
		     okFind();
		else if ( !mgr.isEmpty(mgr.getQueryStringValue("QUOTATION_ID")) )
		      bFirstRequest = true;
		else if ("OK".equals(mgr.readValue("FIRST_REQUEST")) )
		{   
		    sQuotationID = mgr.readValue("QUOTATION_ID");
		    byBuff1 = true;
		    okFindHis();
		}

		checkObjAvailable();
		adjust();
		adjustActions();

		ctx.writeFlag("SECURITYCHECKED",isSecurityChecked);
		ctx.writeBuffer("ACTIONSBUFFER",actionsBuffer);
                
		// 031210  ARWILK  Begin  (Replace blocks with tabs)
		tabs.saveActiveTab();
		// 031210  ARWILK  End  (Replace blocks with tabs)
	}

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

	public void validate()
	{
		ASPManager mgr = getASPManager();

		String val = mgr.readValue("VALIDATE");
		mgr.showError("VALIDATE not implemented");
		mgr.endResponse();
	}

        public void calculateRemainingHrs()
        {
          ASPManager mgr = getASPManager();
          trans.clear();

          if (itemlay3.isMultirowLayout())
              itemset3.goTo(itemset3.getRowSelected());
          ASPBuffer data = itemset3.getRow();
          double totmanHrs = itemset3.getNumberValue("TOTAL_MAN_HOURS"); 
          double totAllocatedHrs = itemset3.getNumberValue("TOTAL_ALLOCATED_HOURS"); 
          double remainingHrs =   totmanHrs -  totAllocatedHrs;
          data.setNumberValue("TOTAL_REMAINING_HOURS",remainingHrs);
          itemset3.addRow(data);
        }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void newRow()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		ASPCommand cmd = trans.addEmptyCommand("HEAD","HISTORICAL_SEPARATE_API.New__",headblk);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);

		ASPBuffer data = trans.getBuffer("HEAD/DATA");
		headset.addRow(data);
	}

	public void countFind()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		ASPQuery q = trans.addQuery(headblk);
		q.addWhereCondition("CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT)");
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);

		headlay.setCountValue(toInt(headset.getRow().getValue("N")));
		headset.clear();  

		setCheckBoxes(true);
	}

	public void okFind()
	{
		ASPManager mgr = getASPManager();

		trans.clear();      

		ASPQuery q = trans.addQuery(headblk);
		q.addWhereCondition("CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT)");
		q.setOrderByClause("WO_NO");
		q.includeMeta("ALL");

		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());

		mgr.querySubmit(trans,headblk);
		eval(headset.syncItemSets());

		if (headset.countRows() == 1)
		{
			okFindITEM1();
			setSalesPartDesc();
			okFindITEM2();
			okFindITEM4();
			okFindITEM5();
			okFindITEM6();
			okFindITEM7();
			okFindITEM8();
			okFindITEM9();
                        okFindITEM3();

			headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
		}
		else if (headset.countRows() == 0)
		{
			mgr.showAlert(mgr.translate("PCMWHISTORICALSEPARATERMBNODATAFOUND: No data found."));
			headset.clear();
		}
		qrystr = mgr.createSearchURL(headblk);
		setCheckBoxes(false);
	}
        public void okFindHis()
        {
         ASPManager mgr = getASPManager();

         trans.clear();      

         ASPQuery q = trans.addQuery(headblk);
         q.addWhereCondition("CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT)");
         q.setOrderByClause("WO_NO");
         q.includeMeta("ALL");

        if ( mgr.dataTransfered() )
        {
          if ( byBuff1 == true )
         {
              //Bug 58216 start
              q.addWhereCondition("QUOTATION_ID = ?");
              q.addParameter("QUOTATION_ID",sQuotationID);
              //Bug 58216 end
          }
      else
      q.addOrCondition( mgr.getTransferedData() );
   }

                   mgr.querySubmit(trans,headblk);
         eval(headset.syncItemSets());

         if (headset.countRows() == 1)
         {
                 okFindITEM1();
                 setSalesPartDesc();
                 okFindITEM2();
                 okFindITEM4();
                 okFindITEM5();
                 okFindITEM6();
                 okFindITEM7();
                 okFindITEM8();
                 okFindITEM9();
                 okFindITEM3();

                 headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
         }
         else if (headset.countRows() == 0)
         {
                 mgr.showAlert(mgr.translate("PCMWHISTORICALSEPARATERMBNODATAFOUND: No data found."));
                 headset.clear();
         }
         qrystr = mgr.createSearchURL(headblk);
         setCheckBoxes(false);
 }



//-----------------------------------------------------------------------------
//----------------------  CMDBAR EDIT-IT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

	public void newRowITEM1()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		ASPCommand cmd = trans.addEmptyCommand("ITEM1","WORK_ORDER_PERMIT_API.New__",itemblk1);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);

		ASPBuffer data = trans.getBuffer("ITEM1/DATA");
		data.setFieldItem("ITEM1_WO_NO",headset.getValue("WO_NO"));
		itemset1.addRow(data);
	}

	public void newRowITEM2()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		ASPCommand cmd = trans.addEmptyCommand("ITEM2","WORK_ORDER_CODING_API.New__",itemblk2);
		cmd.setOption("ACTION","PREPARE");
		trans = mgr.perform(trans);

		ASPBuffer data = trans.getBuffer("ITEM2/DATA");
		data.setFieldItem("ITEM2_WO_NO",headset.getValue("WO_NO"));
		itemset2.addRow(data);
	}


//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

	public void searchAfterReopen()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		ASPQuery q = trans.addEmptyQuery(headblk);
		q.addWhereCondition("CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT)");
		q.setOrderByClause("WO_NO");
		q.includeMeta("ALL");

		if (mgr.dataTransfered())
			q.addOrCondition(mgr.getTransferedData());

		if (!headlay.isMultirowLayout())
		{
			q = trans.addEmptyQuery(itemblk);
			q.addMasterConnection("HEAD","WO_NO","ITEM_WO_NO");
			q.addWhereCondition("SPARE_CONTRACT = User_Allowed_Site_API.Authorized(SPARE_CONTRACT)");
			q.includeMeta("ALL");

			q = trans.addEmptyQuery(itemblk1);
			q.addMasterConnection("HEAD","WO_NO","ITEM1_WO_NO");
			q.includeMeta("ALL");

			q = trans.addEmptyQuery(itemblk2);
			q.addMasterConnection("HEAD","WO_NO","ITEM2_WO_NO");
			q.addWhereCondition("WORK_ORDER_ACCOUNT_TYPE = Work_Order_Account_Type_API.Get_Client_Value(0)");
			q.includeMeta("ALL");

			q = trans.addEmptyQuery(itemblk3);
			q.addMasterConnection("HEAD","WO_NO","ITEM3_WO_NO");
			q.includeMeta("ALL");  

			q = trans.addEmptyQuery(itemblk4);
			q.addMasterConnection("HEAD","WO_NO","ITEM4_WO_NO");
			q.addWhereCondition("WORK_ORDER_COST_TYPE =  Work_Order_Cost_Type_API.Get_Client_Value(0) AND WORK_ORDER_ACCOUNT_TYPE =  Work_Order_Account_Type_API.Get_Client_Value(0) ");      
			q.includeMeta("ALL");

			if (isModuleInst1("VIM"))
			{

				q = trans.addEmptyQuery(itemblk6);
				q.addMasterConnection("HEAD","WO_NO","ITEM6_WO_NO");
				q.includeMeta("ALL");
			}

			q = trans.addEmptyQuery(itemblk7);
			q.addMasterConnection("HEAD","WO_NO","ITEM7_WO_NO");
			q.includeMeta("ALL");  

		}

		mgr.submit(trans);
		setCheckBoxes(false);
	}

//-----------------------------------------------------------------------------
//-----------------------  CMDBAR SEARCH-IT FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

	public void okFindITEM1()
	{
		ASPManager mgr = getASPManager();

		int headrowno;

		if (headset.countRows() != 0)
		{
			headrowno = headset.getCurrentRowNo();

			trans.clear();

			ASPQuery q = trans.addQuery(itemblk1);
                        //Bug 58216 start
			q.addWhereCondition("WO_NO = ?");
                        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
                        //Bug 58216 end
			q.includeMeta("ALL");
			mgr.submit(trans);

			if (mgr.commandBarActivated())
			{
				if (itemset1.countRows() == 0 && "ITEM1.OkFind".equals(mgr.readValue("__COMMAND")))
				{
					mgr.showAlert(mgr.translate("PCMWHISTORICALSEPARATERMBNODATA: No data found."));
					itemset1.clear();
				}
			}

			setCheckBoxes(false);
			headset.goTo(headrowno);
		}
	}

	public void okFindITEM2()
	{
		ASPManager mgr = getASPManager();

		int headrowno;

		if (headset.countRows() != 0)
		{
			trans.clear();

			headrowno = headset.getCurrentRowNo();

			ASPQuery q = trans.addQuery(itemblk2);
                        //Bug 58216 start
			q.addWhereCondition("WO_NO = ?");
			q.addWhereCondition("WORK_ORDER_ACCOUNT_TYPE = Work_Order_Account_Type_API.Get_Client_Value(0)");
                        q.addParameter("WO_NO",headset.getValue("WO_NO"));
                        //Bug 58216 end
			q.includeMeta("ALL");
			mgr.submit(trans);

			if (mgr.commandBarActivated())
			{
				if (itemset2.countRows() == 0 && "ITEM2.OkFind".equals(mgr.readValue("__COMMAND")))
				{
					mgr.showAlert(mgr.translate("PCMWHISTORICALSEPARATERMBNODATA: No data found."));
					itemset2.clear();
				}
			}

			setCheckBoxes(false);
			headset.goTo(headrowno);
		}
	}

	public void okFindITEM3()
	{
		ASPManager mgr = getASPManager();

		int headrowno;

		if (headset.countRows() != 0)
		{
			trans.clear();
			headrowno = headset.getCurrentRowNo();

			itemset3.clear();
			ASPQuery q = trans.addQuery(itemblk3);
                        //Bug 58216 start
			q.addWhereCondition("WO_NO = ? AND PARENT_ROW_NO IS NULL");
                        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
                        //Bug 58216 end
			q.includeMeta("ALL");

			if (mgr.commandBarActivated())
			{
				if (itemset3.countRows() == 0 && "ITEM3.OkFind".equals(mgr.readValue("__COMMAND")))
				{
					mgr.showAlert(mgr.translate("PCMWHISTORICALSEPARATERMBNODATA: No data found."));
					itemset3.clear();
				}
			}

			mgr.submit(trans);
			headset.goTo(headrowno);
                        //if (itemset3 != null && itemset3.countRows() > 0)
                          // okFindITEM10();
		}
	}

        public void okFindITEM10()
        {
            ASPManager mgr = getASPManager();

            if(headset.countRows()>0 && itemset3.countRows()>0){
                trans.clear();

                int headrowno = headset.getCurrentRowNo();
                int currrow = itemset3.getCurrentRowNo();
    
                ASPQuery q = trans.addQuery(itemblk10);
                //Bug 58216 start
                q.addWhereCondition("WO_NO = ? AND PARENT_ROW_NO= ?");
                q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
                q.addParameter("ROW_NO",itemset3.getRow().getValue("ROW_NO"));
                //Bug 58216 end
                
                q.includeMeta("ALL");
                mgr.submit(trans);
    
                  if (itemset10.countRows() == 0 && "ITEM10.OkFind".equals(mgr.readValue("__COMMAND")))
                    {
                        mgr.showAlert(mgr.translate("PCMWHISTORICALSEPARATERMBNODATA: No data found."));
                        itemset10.clear();
                    }
                
    
                headset.goTo(headrowno);
                itemset3.goTo(currrow);
                //calculateRemainingHrs();   
            } 
            
        }

        public void countFindITEM10()
        {
            ASPManager mgr = getASPManager();

            ASPQuery q = trans.addQuery(itemblk10);
            q.setSelectList("to_char(count(*)) N");
            //Bug 58216 start
            q.addWhereCondition("WO_NO = ? AND PARENT_ROW_NO= ?");
            q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
            q.addParameter("ROW_NO",itemset3.getRow().getValue("ROW_NO"));
            //Bug 58216 end
            int headrowno = headset.getCurrentRowNo();
            mgr.submit(trans);
            itemlay10.setCountValue(toInt(itemset10.getRow().getValue("N")));
            itemset10.clear();
            headset.goTo(headrowno);
        }    

	public void okFindITEM4()
	{
		ASPManager mgr = getASPManager();

		int headrowno;

		if (headset.countRows() != 0)
		{
			trans.clear();
			headrowno = headset.getCurrentRowNo();

			itemset4.clear();   
			ASPQuery q = trans.addQuery(itemblk4);
                        //Bug 58216 start
			q.addWhereCondition("WO_NO = ?");
			q.addWhereCondition("WORK_ORDER_COST_TYPE =  Work_Order_Cost_Type_API.Get_Client_Value(0) AND WORK_ORDER_ACCOUNT_TYPE =  Work_Order_Account_Type_API.Get_Client_Value(0) ");
                        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
                        //Bug 58216 end
			q.includeMeta("ALL");
			mgr.submit(trans);

			if (mgr.commandBarActivated())
			{
				if (itemset4.countRows() == 0 && "ITEM4.OkFind".equals(mgr.readValue("__COMMAND")))
				{
					mgr.showAlert(mgr.translate("PCMWHISTORICALSEPARATERMBNODATA: No data found."));
					itemset4.clear();
				}
			}

			headset.goTo(headrowno);
		}
	}

	public void okFindITEM5()
	{
		ASPManager mgr = getASPManager();

		int headrowno;

		if (headset.countRows() != 0)
		{
			trans.clear();

			headrowno = headset.getCurrentRowNo();

			ASPQuery q = trans.addQuery(itemblk5);
                        //Bug 58216 start
			q.addWhereCondition("WO_NO = ?");
                        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
                        //Bug 58216 end
			q.includeMeta("ALL");

			mgr.submit(trans);

			if (mgr.commandBarActivated())
			{
				if (itemset5.countRows() == 0 && "ITEM5.OkFind".equals(mgr.readValue("__COMMAND")))
				{
					mgr.showAlert(mgr.translate("PCMWHISTORICALSEPARATERMBNODATA: No data found."));
					itemset5.clear();
				}
			}

			headset.goTo(headrowno);
		}
	}

	public void okFindITEM()
	{
		ASPManager mgr = getASPManager();

		int headsetRowNo;
		int item5rowno;

		if (itemset5.countRows() > 0)
		{
			trans.clear();

			headsetRowNo = headset.getCurrentRowNo();
			item5rowno = itemset5.getCurrentRowNo();

			ASPQuery q = trans.addQuery(itemblk);
                        //Bug 58216 start
			q.addWhereCondition("WO_NO = ?");
			q.addWhereCondition("MAINT_MATERIAL_ORDER_NO = ?");
                        q.addParameter("WO_NO",itemset5.getRow().getFieldValue("ITEM5_WO_NO"));
                        q.addParameter("MAINT_MATERIAL_ORDER_NO",itemset5.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));
                        //Bug 58216 end
			q.includeMeta("ALL");

			mgr.submit(trans);

			if (mgr.commandBarActivated())
			{
				if (itemset.countRows() == 0 && "ITEM.OkFind".equals(mgr.readValue("__COMMAND")))
				{
					mgr.showAlert(mgr.translate("PCMWHISTORICALSEPARATERMBNODATA: No data found."));
					itemset.clear();
				}
			}

			headset.goTo(headsetRowNo);
			itemset5.goTo(item5rowno);
		}
	}

	public void okFindITEM6()
	{
		ASPManager mgr = getASPManager();

		int headrowno;
		if (isModuleInst1("VIM"))
		{

			if (headset.countRows() != 0)
			{
				trans.clear();
				headrowno = headset.getCurrentRowNo();

				itemset6.clear();   
				ASPQuery q = trans.addQuery(itemblk6);
                                //Bug 58216 start
				q.addWhereCondition("WO_NO = ?");
                                q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
                                //Bug 58216 end
				q.includeMeta("ALL");
				mgr.submit(trans);

				if (mgr.commandBarActivated())
				{
					if (itemset6.countRows() == 0 && "ITEM6.OkFind".equals(mgr.readValue("__COMMAND")))
					{
						mgr.showAlert(mgr.translate("PCMWHISTORICALSEPARATERMBNODATA: No data found."));
						itemset6.clear();
					}
				}

				headset.goTo(headrowno);
			}
		}
	}


	public void okFindITEM7()
	{
		ASPManager mgr = getASPManager();

		int headrowno;

		if (headset.countRows() != 0)
		{
			trans.clear();
			headrowno = headset.getCurrentRowNo();

			itemset7.clear();
			ASPQuery q = trans.addQuery(itemblk7);
                        //Bug 58216 start
			q.addWhereCondition("WO_NO = ?");
                        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
                        //Bug 58216 end
			q.includeMeta("ALL");
			mgr.submit(trans);

			if (mgr.commandBarActivated())
			{
				if (itemset7.countRows() == 0 && "ITEM7.OkFind".equals(mgr.readValue("__COMMAND")))
				{
					mgr.showAlert(mgr.translate("PCMWHISTORICALSEPARATERMBNODATA: No data found."));
					itemset7.clear();
				}
			}
			headset.goTo(headrowno);
		}
	}

	public void okFindITEM8()
	{
		ASPManager mgr = getASPManager();

		int headrowno;
		if (isModuleInst1("VIM"))
		{

			if (headset.countRows() != 0)
			{
				trans.clear();
				headrowno = headset.getCurrentRowNo();

				itemset8.clear();
				ASPQuery q = trans.addQuery(itemblk8);
                                //Bug 58216 start
				q.addWhereCondition("WO_NO = ?");
                                q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
                                //Bug 58216 end
				q.includeMeta("ALL");
				mgr.submit(trans);

				if (mgr.commandBarActivated())
				{
					if (itemset8.countRows() == 0 && "ITEM8.OkFind".equals(mgr.readValue("__COMMAND")))
					{
						mgr.showAlert(mgr.translate("PCMWHISTORICALSEPARATERMBNODATA: No data found."));
						itemset8.clear();
					}
				}
				headset.goTo(headrowno);
			}
		}
	}

	public void okFindITEM9()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		int headrowno = headset.getCurrentRowNo();

		ASPQuery q = trans.addQuery(itemblk9);
                //Bug 58216 start
		q.addWhereCondition("WO_NO = ? AND STD_JOB_CONTRACT = ?");
                q.addParameter("WO_NO",headset.getValue("WO_NO"));
                q.addParameter("CONTRACT",headset.getValue("CONTRACT"));
                //Bug 58216 end

		q.includeMeta("ALL");
		//mgr.submit(trans);
		mgr.querySubmit(trans,itemblk9);

		headset.goTo(headrowno);
	}

	public void countFindITEM1()
	{
		ASPManager mgr = getASPManager();

		int currrow;
		trans.clear();

		currrow = headset.getCurrentRowNo();

		ASPQuery q = trans.addQuery(itemblk1);
                //Bug 58216 start
		q.addWhereCondition("WO_NO = ?");
                q.addParameter("WO_NO",headset.getValue("WO_NO"));
                //Bug 58216 end
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);

		itemlay1.setCountValue(toInt(itemset1.getRow().getValue("N")));
		itemset1.clear();
		headset.goTo(currrow);   
	}

	public void countFindITEM2()
	{
		ASPManager mgr = getASPManager();

		int currrow;
		trans.clear();

		currrow = headset.getCurrentRowNo();

		ASPQuery q = trans.addQuery(itemblk2);
                //Bug 58216 start
		q.addWhereCondition("WO_NO = ?");
		q.addWhereCondition("WORK_ORDER_ACCOUNT_TYPE = Work_Order_Account_Type_API.Get_Client_Value(0)");
                q.addParameter("WO_NO",headset.getValue("WO_NO"));
                //Bug 58216 end
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);

		itemlay2.setCountValue(toInt(itemset2.getRow().getValue("N")));
		itemset2.clear();
		headset.goTo(currrow);
	}

	public void countFindITEM3()
	{
		ASPManager mgr = getASPManager();

		int currrow;
		trans.clear();

		currrow = headset.getCurrentRowNo();

		ASPQuery q = trans.addQuery(itemblk3);
                //Bug 58216 start
		q.addWhereCondition("WO_NO = ?");
                q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
                //Bug 58216 end
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);

		itemlay3.setCountValue(toInt(itemset3.getRow().getValue("N")));
		itemset3.clear();
		headset.goTo(currrow);
	}

	public void countFindITEM4()
	{
		ASPManager mgr = getASPManager();

		int currrow;
		trans.clear();

		currrow = headset.getCurrentRowNo();

		ASPQuery q = trans.addQuery(itemblk4);
		q.setSelectList("to_char(count(*)) N");
                //Bug 58216 start
		q.addWhereCondition("WO_NO = ?");
		q.addWhereCondition("WORK_ORDER_COST_TYPE =  Work_Order_Cost_Type_API.Get_Client_Value(0) AND WORK_ORDER_ACCOUNT_TYPE =  Work_Order_Account_Type_API.Get_Client_Value(0) ");
                q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
                //Bug 58216 end
		mgr.submit(trans);

		itemlay4.setCountValue(toInt(itemset4.getRow().getValue("N")));
		itemset4.clear();
		headset.goTo(currrow);
	}

	public void countFindITEM6()
	{
		ASPManager mgr = getASPManager();

		int currrow;
		trans.clear();

		currrow = headset.getCurrentRowNo();
		ASPQuery q = trans.addQuery(itemblk6);
		q.setSelectList("to_char(count(*)) N");
                //Bug 58216 start
		q.addWhereCondition("WO_NO = ?");
                q.addParameter("WO_NO",headset.getValue("WO_NO"));
                //Bug 58216 end

		mgr.submit(trans);

		itemlay6.setCountValue(toInt(itemset6.getRow().getValue("N")));
		itemset6.clear();
		headset.goTo(currrow);
	}

	public void countFindITEM7()
	{
		ASPManager mgr = getASPManager();

		int currrow;
		trans.clear();

		currrow = headset.getCurrentRowNo();

		ASPQuery q = trans.addQuery(itemblk7);
                //Bug 58216 start
		q.addWhereCondition("WO_NO = ?");
                q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
                //Bug 58216 end
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);

		itemlay7.setCountValue(toInt(itemset7.getRow().getValue("N")));
		itemset7.clear();
		headset.goTo(currrow);
	}

	public void countFindITEM8()
	{
		ASPManager mgr = getASPManager();

		int currrow;
		trans.clear();

		currrow = headset.getCurrentRowNo();

		ASPQuery q = trans.addQuery(itemblk8);
                //Bug 58216 start
		q.addWhereCondition("WO_NO = ?");
                q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
                //Bug 58216 end
		q.setSelectList("to_char(count(*)) N");
		mgr.submit(trans);

		itemlay8.setCountValue(toInt(itemset8.getRow().getValue("N")));
		itemset8.clear();
		headset.goTo(currrow);
	}

	public void countFindITEM9()
	{
		ASPManager mgr = getASPManager();

		trans.clear();

		int headrowno = headset.getCurrentRowNo();

		ASPQuery q = trans.addQuery(itemblk9);
		q.setSelectList("to_char(count(*)) N");
                //Bug 58216 start
		q.addWhereCondition("WO_NO = ? AND STD_JOB_CONTRACT = ?");
                q.addParameter("WO_NO",headset.getValue("WO_NO"));
                q.addParameter("CONTRACT",headset.getValue("CONTRACT"));
                //Bug 58216 end
		mgr.submit(trans);

		headset.goTo(headrowno);

		itemlay9.setCountValue(toInt(itemset9.getRow().getValue("N")));
		itemset9.clear();
	}

	//-----------------------------------------------------------------------------
	//----------------------------  RMB FUNCTION  ---------------------------------
	//-----------------------------------------------------------------------------

	public void structure()
	{
		ASPManager mgr = getASPManager();

		String current_url = mgr.getURL();
		ctx.setGlobal("CALLING_URL", current_url);

		// 040128  ARWILK  Begin  (Enable Multirow RMB actions)
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
		// 040128  ARWILK  End  (Enable Multirow RMB actions)
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
    
	    bOpenNewWindow = true;
	    urlString = "CopyHistWorkOrderDlg.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) +
			"&FAULT_REP_FLAG=" + mgr.URLEncode(headset.getRow().getValue("FAULT_REP_FLAG")) +
			"&COMPANY=" + mgr.URLEncode(headset.getRow().getValue("COMPANY"));
	    newWinHandle = "copy"; 
	}
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

      urlString = createTransferUrl("../pcmw/WorkOrderAddressDlg.page?__DYNAMIC_DEF_KEY=HISTORICAL_SEPARATE", transferBuffer);
      newWinHandle = "workorderaddr"; 
   }
//   Bug 83532, End

	public void reopen() 
	{
		ASPManager mgr = getASPManager();

		int count = 0;

		String current_url = mgr.getURL();
		ctx.setGlobal("CALLING_URL", current_url);

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
			headset.selectRow();
			count = 1;
		}
                
                for (int i = 0; i < count; i++)
		{
			cmd = trans.addCustomCommand("REOPEN_WO_" + i, "Historical_Work_Order_API.Recreate_Work_Order");
			cmd.addParameter("WO_NO", headset.getValue("WO_NO"));

                        //Bug 72455, start
                        int rowno = headset.getCurrentRowNo();
                        headset.goTo(rowno);
                        headset.clearRow();
                        //Bug 72455, end

			if (headlay.isMultirowLayout())
				headset.next();
		}

		if ("TRUE".equals(mgr.readValue("VIEWWORKORDER"))) {
		    URLString = createTransferUrl("ActiveSeparate2.page", headset.getSelectedRows("WO_NO"));
		    performRMB   = "TRUE";
		    WindowName   = "PRINTAUTHRELCERTI";
		}

		trans = mgr.perform(trans);

		if (headlay.isMultirowLayout())
			headset.setFilterOff();

	}

	public void requisitions()
	{
		ASPManager mgr = getASPManager();

		String current_url = mgr.getURL();
		ctx.setGlobal("CALLING_URL", current_url);

		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());
		else
			headset.selectRow();

		// 040128  ARWILK  Begin  (Remove uneccessary global variables)	
		bOpenNewWindow = true;
		urlString = createTransferUrl("WorkOrderRequisHeaderRMBRO.page", headset.getSelectedRows("WO_NO"));
		newWinHandle = "requisitions";      
		// 040128  ARWILK  End  (Remove uneccessary global variables)   
	}

	public void budget()
	{
		ASPManager mgr = getASPManager();

		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());
		else
			headset.selectRow();

		calling_url = mgr.getURL();
		ctx.setGlobal("CALLING_URL", calling_url);

		// 040128  ARWILK  Begin  (Remove uneccessary global variables)
		bOpenNewWindow = true;
		urlString = "HistWorkOrderBudget.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO"));
		newWinHandle = "budget"; 
		// 040128  ARWILK  End  (Remove uneccessary global variables)
	}

	public void freeNotes()
	{
		ASPManager mgr = getASPManager();

		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());
		else
			headset.selectRow();

		calling_url = mgr.getURL();
		ctx.setGlobal("CALLING_URL", calling_url);

		// 040128  ARWILK  Begin  (Remove uneccessary global variables)
		bOpenNewWindow = true;
		urlString = "HistWorkOrderReportPage.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO"));
		newWinHandle = "freeNotes"; 
		// 040128  ARWILK  End  (Remove uneccessary global variables)
	}


	public void coInfo()
	{
		ASPManager mgr = getASPManager();

		if (headlay.isMultirowLayout())
			headset.goTo(headset.getRowSelected());
		else
			headset.selectRow();

		calling_url = mgr.getURL();
		ctx.setGlobal("CALLING_URL", calling_url);

		// 040128  ARWILK  Begin  (Remove uneccessary global variables)
		bOpenNewWindow = true;
		urlString = "HistoricalSeparateCOInfo.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO"));
		newWinHandle = "coInfo"; 
		// 040128  ARWILK  End  (Remove uneccessary global variables)
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

		// 040128  ARWILK  Begin  (Remove uneccessary global variables)	
		bOpenNewWindow = true;
		urlString = createTransferUrl("WorkOrderReturns.page", headset.getSelectedRows("WO_NO"));
		newWinHandle = "returns";       
		// 040128  ARWILK  End  (Remove uneccessary global variables)   
	}

	public void printHistWO()
	{
		ASPManager mgr = getASPManager();

		// 040128  ARWILK  Begin  (Enable Multirow RMB actions)
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
			attr1 = "REPORT_ID" + (char)31 + "HISTORICAL_SEP_WO_PRINT_REP" + (char)30;
			attr2 = "WO_NO_LIST" + (char)31 + headset.getValue("WO_NO") + (char)30 + "CONTRACT" + (char)31 + headset.getValue("CONTRACT") + (char)30;
			attr3 =  "";
			attr4 =  "";

			cmd = trans.addCustomCommand("PRNT" + i,"Archive_API.New_Client_Report");
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
			printBuff.addItem("RESULT_KEY", trans.getValue("PRNT" + i + "/DATA/ATTR0"));
		}

		// 040128  ARWILK  End  (Enable Multirow RMB actions)
		callPrintDlg(print,true);   
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

		for (int i = 0;i < count; i++)
		{
			attr1 = "REPORT_ID" + (char)31 + "RELEASE_CERTIFICATE_REP" + (char)30;
			attr2 = "WO_NO" + (char)31 + headset.getValue("WO_NO") + (char)30;
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


		performRMB   = "TRUE";
		WindowName   = "PRINTRELEASECERTIF"; 

	}


	public void viewPermit()
	{
		ASPManager mgr = getASPManager();

		String current_url = mgr.getURL();
		ctx.setGlobal("CALLING_URL",current_url);

		// 040128  ARWILK  Begin  (Enable Multirow RMB actions)
		if (itemlay1.isMultirowLayout())
			itemset1.store();
		else
		{
			itemset1.unselectRows();
			itemset1.selectRow();
		}

		bOpenNewWindow = true;
		urlString = createTransferUrl("Permit.page", itemset1.getSelectedRows("PERMIT_SEQ"));
		newWinHandle = "viewPermit"; 
		// 040128  ARWILK  End  (Enable Multirow RMB actions)
	}

	public void setCheckBoxes(boolean fieldFlag) 
	{
		ASPManager mgr = getASPManager();

		String warrantyOnObject;
		String existObjReference = "";

		trans.clear();

		boolean docInstal = false;

		ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer();
		secBuff.addSecurityQuery("DOC_REFERENCE_OBJECT_API","EXIST_OBJ_REFERENCE");

		secBuff = mgr.perform(secBuff);

		if (secBuff.getSecurityInfo().itemExists("DOC_REFERENCE_OBJECT_API.EXIST_OBJ_REFERENCE"))
			docInstal = true;

		if (headset.countRows()>0)
		{
			cmd = trans.addCustomFunction("HASWARRANTY", "EQUIPMENT_OBJECT_API.HAS_WARRANTY__","CBWARRANTYONOBJECT");

			if (fieldFlag)
			{
				cmd.addParameter("CONTRACT", mgr.readValue("CONTRACT"));
				cmd.addParameter("MCH_CODE", mgr.readValue("MCH_CODE"));
			}
			else
			{
				cmd.addParameter("CONTRACT", headset.getRow().getValue("CONTRACT"));
				cmd.addParameter("MCH_CODE", headset.getRow().getValue("MCH_CODE"));
			}

			if (docInstal)
			{

				cmd = trans.addCustomFunction("EXOBJREF", "DOC_REFERENCE_OBJECT_API.EXIST_OBJ_REFERENCE","CBHASDOCUMENTS");

				if (fieldFlag)
				{
					cmd.addParameter("REPAIR_FLAG", "HistoricalSeparate");
					cmd.addParameter("WO_KEY_VALUE", mgr.readValue("WO_KEY_VALUE"));         
				}
				else
				{
					cmd.addParameter("REPAIR_FLAG", "HistoricalSeparate");
					cmd.addParameter("WO_KEY_VALUE", headset.getRow().getValue("WO_KEY_VALUE"));
				}
			}

			trans = mgr.perform(trans);

			warrantyOnObject = trans.getValue("HASWARRANTY/DATA/CBWARRANTYONOBJECT");

			if (docInstal)
				existObjReference = trans.getValue("EXOBJREF/DATA/CBHASDOCUMENTS");

			ASPBuffer buff = headset.getRow();

			if ("TRUE".equals(warrantyOnObject))
				buff.setValue("CBWARRANTYONOBJECT", "TRUE");
			else
				buff.setValue("CBWARRANTYONOBJECT", "FALSE");

			if ("TRUE".equals(existObjReference))
				buff.setValue("CBHASDOCUMENTS", "TRUE");
			else
				buff.setValue("CBHASDOCUMENTS", "FALSE");      

			headset.setRow(buff);
		}
	}

	public void setWorkCenterValues()
	{
		ASPManager mgr = getASPManager();

		trans.clear();
		ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer();
		secBuff.addSecurityQuery("Work_Center_Int_API");

		secBuff = mgr.perform(secBuff);

		if (secBuff.getSecurityInfo().itemExists("Work_Center_Int_API"))
		{
			cmd = trans.addCustomFunction("WORKCENT","Work_Center_Int_API.Get_Mch_Work_Center","WORKCENTER");
			cmd.addParameter("CONTRACT", headset.getRow().getValue("CONTRACT"));
			cmd.addParameter("MCH_CODE", headset.getRow().getValue("MCH_CODE"));

			cmd = trans.addCustomFunction("RESOURCE","Work_Center_Int_API.Get_Mch_Resource_No","RESOURCENO");
			cmd.addParameter("CONTRACT", headset.getRow().getValue("CONTRACT"));
			cmd.addParameter("MCH_CODE", headset.getRow().getValue("MCH_CODE"));

			cmd = trans.addCustomFunction("RESOUCELOADHOURS","Work_Center_Int_API.Get_Mch_Load_Hours","LOADHOURS");
			cmd.addParameter("CONTRACT", headset.getRow().getValue("CONTRACT"));
			cmd.addParameter("MCH_CODE", headset.getRow().getValue("MCH_CODE"));
			cmd.addParameter("PLAN_S_DATE", headset.getRow().getFieldValue("PLAN_S_DATE"));
			cmd.addParameter("PLAN_F_DATE", headset.getRow().getFieldValue("PLAN_F_DATE"));

			trans = mgr.perform(trans);

			String workcent = trans.getValue("WORKCENT/DATA/WORKCENTER");
			String resource = trans.getValue("RESOURCE/DATA/RESOURCENO");
			String resourcehours = trans.getValue("RESOUCELOADHOURS/DATA/LOADHOURS");

			row = headset.getRow();
			row.setValue("WORKCENTER",workcent);
			row.setValue("RESOURCENO",resource);
			row.setValue("LOADHOURS",resourcehours);
			headset.setRow(row);
		}
	}


	public void  setSalesPartDesc()
	{
		int i;
		String catalogNo;
		String Contract;
		ASPManager mgr = getASPManager();
		String catDesc;
		trans.clear();
		int n = itemset2.countRows();
		itemset2.first();

		if (n > 0)
		{
			for (i=0; i<=n; ++i)
			{
				catalogNo = itemset2.getRow().getValue("CATALOG_NO");
				Contract =itemset2.getRow().getValue("CONTRACT");

				if (isModuleInst1("ORDER"))
				{
					cmd = trans.addCustomFunction("DESC"+i,"Sales_Part_API.Get_Catalog_Desc","LINE_DESCRIPTION");
					cmd.addParameter("CONTRACT",Contract);
					cmd.addParameter("CATALOG_NO",catalogNo);
				}
				itemset2.next();
			}

			trans = mgr.validate(trans);
			itemset2.first();

			for (i=0; i<=n; ++i)
			{
				row = itemset2.getRow();

				if (isModuleInst1("ORDER"))
				{

					catDesc= trans.getValue("DESC"+i+"/DATA/LINE_DESCRIPTION");

					if (mgr.isEmpty(itemset2.getRow().getValue("LINE_DESCRIPTION")))
						row.setValue("LINE_DESCRIPTION",catDesc);
				}

				itemset2.setRow(row);

				itemset2.next();
			}
		}
		itemset2.first();
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

        public void additionalQualifications()
        {
        ASPManager mgr = getASPManager();
        String calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);

        if (itemlay3.isMultirowLayout())
            itemset3.goTo(itemset3.getRowSelected());

        bOpenNewWindow = true;
        urlString = "WOQualificationsDlg.page?RES_TYPE=HISTQUALIFICATIONS"+
                    "&WO_NO="+ (mgr.isEmpty(itemset3.getValue("WO_NO"))?"":itemset3.getValue("WO_NO"))+
                    "&ROW_NO="+ (mgr.isEmpty(itemset3.getValue("ROW_NO"))?"":itemset3.getValue("ROW_NO")) ;
                    
        newWinHandle = "additionalQualifications"; 
       }


	public void preDefine()
	{
		ASPManager mgr = getASPManager();

		headblk = mgr.newASPBlock("HEAD");

		f = headblk.addField("OBJID");
		f.setHidden();

		f = headblk.addField("OBJVERSION");
		f.setHidden();

		f = headblk.addField("MODULENAME");
		f.setHidden();
		f.setFunction("''");

		f = headblk.addField("MCH_CODE");
		f.setSize(16);
		f.setMaxLength(100);
		f.setMandatory();
		f.setReadOnly();
		f.setHilite();
		f.setDynamicLOV("EQUIPMENT_OBJECT",600,450);      
		f.setLabel("PCMWHISTORICALSEPARATERMBMCHCODE: Object ID");
		f.setUpperCase();

		f = headblk.addField("MCH_CODE_DESCRIPTION");
		f.setSize(20);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBMCHNAME: Description");

		f = headblk.addField("MCH_CODE_CONTRACT");
		f.setSize(8);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
		f.setMandatory();
		f.setHilite();   
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBMCHCODECONTRACT: Site");
		f.setUpperCase();

		f = headblk.addField("CONNECTION_TYPE_DB");
		f.setHidden();

		f = headblk.addField("CONNECTION_TYPE");
		f.setSize(16);
		f.setMandatory();
		f.setReadOnly();
		f.setHilite();
		f.setLabel("PCMWHISTORICALSEPARATERMBCONNECTIONTYPE: Connection Type");

		f = headblk.addField("WO_NO","Number","#");
		f.setSize(8);
		f.setHilite();   
		f.setMandatory();
		f.setLabel("PCMWHISTORICALSEPARATERMBWONO: WO No");
		f.setReadOnly();

		f = headblk.addField("CONTRACT");
		f.setSize(8);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
		f.setMandatory();
		f.setHilite();   
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBCONTRACT: WO Site");
		f.setUpperCase();

		f = headblk.addField("MCHTYPE");
		f.setSize(12);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBMCHTYPE: Object Type");
      //Bug 81463, start
		//f.setDefaultNotVisible();
      //Bug 81463, end
		f.setFunction("EQUIPMENT_OBJECT_API.Get_Mch_Type(:MCH_CODE_CONTRACT,:MCH_CODE)");

		f = headblk.addField("WOSTATUSID");
		f.setSize(15);
		f.setLabel("PCMWHISTORICALSEPARATERMBWOSTATUSID: Status");
		f.setFunction("substr(ACTIVE_SEPARATE_API.FINITE_STATE_DECODE__(WO_STATUS_ID),1,30)");
      f.setSelectBox();
      f.enumerateValues("HISTORICAL_WORK_ORDER_API.Enumerate_States__","");
		f.setReadOnly();
		f.setHilite();   

		f = headblk.addField("GROUPID");
		f.setSize(8);
		f.setReadOnly();      
      //Bug 81463, start
		//f.setDefaultNotVisible();
      //Bug 81463, end
		f.setLabel("PCMWHISTORICALSEPARATERMBGROUPID: Group Id");
		f.setFunction("EQUIPMENT_OBJECT_API.Get_Group_Id(:MCH_CODE_CONTRACT,:MCH_CODE)");

		f = headblk.addField("CATEGORYID");
		f.setSize(16);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBCATEGORYID: Category Id");
      //Bug 81463, start
		//f.setDefaultNotVisible();
      //Bug 81463, end
		f.setFunction("EQUIPMENT_OBJECT_API.Get_Category_Id(:MCH_CODE_CONTRACT,:MCH_CODE)");
      
      //Bug 81463, start
      f = headblk.addField("MCHCODEPOS");
		f.setSize(16);
		f.setReadOnly();     
      f.setHilite();      
		f.setLabel("PCMWHISTORICALSEPARATERMBMCHCODEPOS: Position");		
		f.setFunction("EQUIPMENT_OBJECT_API.Get_Mch_Pos(:MCH_CODE_CONTRACT,:MCH_CODE)");
      //Bug 81463, end

		f = headblk.addField("MANUFACTURERID");
		f.setSize(16);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBMANUFACTURERID: Manufacturer");
		f.setDefaultNotVisible();
		f.setFunction("MAINTENANCE_OBJECT_API.Get_Manufacturer_No(:MCH_CODE_CONTRACT,:MCH_CODE)");

		f = headblk.addField("TYPEDESIGNATION");
		f.setSize(20);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBTYPEDESIGNATION: Type Designation");
		f.setDefaultNotVisible();
		f.setFunction("EQUIPMENT_OBJECT_API.Get_Type(:MCH_CODE_CONTRACT,:MCH_CODE)");

		f = headblk.addField("ORG_CODE");
		f.setSize(10);
		f.setReadOnly();
		f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBORGCODE: Maintenance Organization");
		f.setUpperCase();
		f.setMaxLength(8);
		f.setHilite();   

		f = headblk.addField("ORGCODEDESCRIPTION");
		f.setSize(25);
		f.setFunction("Organization_Api.Get_Description(:CONTRACT,:ORG_CODE)");
		//mgr.getASPField("ORG_CODE").setValidation("ORGCODEDESCRIPTION");
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setHilite();  

		f = headblk.addField("ERR_DESCR");
		f.setSize(30);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBERRDESCR: Directive");
      //Bug 81463, start
		//f.setDefaultNotVisible();
      //Bug 81463, end
		f.setMaxLength(60);
		f.setHilite();

		f = headblk.addField("REALSDATE","Datetime");
		f.setDbName("REAL_S_DATE");
		f.setDefaultNotVisible();
		f.setSize(18);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBREALSDATE: Actual Start");

		f = headblk.addField("REALFDATE","Datetime");
		f.setDbName("REAL_F_DATE");
		f.setSize(22);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBREALFDATE: Actual Completion");

		f = headblk.addField("PLAN_S_DATE","Datetime");
		f.setSize(22);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBPLANSDATE: Planned Start");

		f = headblk.addField("PLAN_F_DATE","Datetime");
		f.setSize(22);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBPLANFDATE: Planned Completion");

		f = headblk.addField("REQUIRED_END_DATE","Datetime");
		f.setSize(22);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBREQENDDATE: Latest Completion");

		f = headblk.addField("PLAN_HRS","Number");
		f.setSize(14);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBPLANHRS: Planned Hours");

		f = headblk.addField("PLANNED_MAN_HRS","Number");
		f.setSize(10);
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBMANHRS: Man Hours");

		f = headblk.addField("PLAN_MEN","Number");
		f.setSize(14);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBPLANMEN: Planned Men");

		f = headblk.addField("CALL_CODE");
		f.setSize(10);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBCALLCODE: Event");
		f.setUpperCase();
		f.setMaxLength(10);
		f.setDefaultNotVisible();
		f.setHyperlink("MaintenanceEventOvw.page","CONTRACT","NEWWIN");

		f = headblk.addField("CALL_CODE_DESC");
		f.setSize(25);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBCALLCODEDESC: Event Desc");
		f.setDefaultNotVisible();
		f.setFunction("MAINTENANCE_EVENT_API.Get_Description(:CALL_CODE)");

		f = headblk.addField("OP_STATUS_ID");
		f.setSize(8);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setDynamicLOV("OPERATIONAL_STATUS",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBOPSTATUSID: Operational Status");
		f.setUpperCase();
		f.setMaxLength(3);
		f.setHyperlink("OperationalStatusOvw.page","CONTRACT","NEWWIN");

		f = headblk.addField("OP_STATUS_DESC");
		f.setSize(25);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBOPSTATUSDESC: Operational Status Desc");
		f.setFunction("OPERATIONAL_STATUS_API.Get_Description(:OP_STATUS_ID)");

		f = headblk.addField("PRIORITY_ID");
		f.setSize(10);
		f.setReadOnly();
		f.setDynamicLOV("MAINTENANCE_PRIORITY",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBPRIORITYID: Priority ID");
		f.setUpperCase();
		f.setMaxLength(1);
		f.setDefaultNotVisible();
		f.setLOVProperty("TITLE",mgr.translate("PCMWHISTORICALSEPARATERMBPRID: List of Priority ID"));

		f = headblk.addField("PRIORITYDESCRIPTION");
		f.setSize(25);
		f.setFunction("Maintenance_Priority_API.Get_Description(:PRIORITY_ID)");
		mgr.getASPField("PRIORITY_ID").setValidation("PRIORITYDESCRIPTION");
		f.setDefaultNotVisible();
		f.setReadOnly();

		f = headblk.addField("WORK_TYPE_ID");
		f.setSize(8);
		f.setReadOnly();
		f.setDynamicLOV("WORK_TYPE",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBWORKTYPEID: Work Type");
		f.setUpperCase();
		f.setMaxLength(20);
		f.setDefaultNotVisible();
		f.setHyperlink("WorkType.page","WORK_TYPE_ID","NEWWIN");

		f = headblk.addField("WORKTYPEDESCRIPTION");
		f.setSize(25);
		f.setFunction("WORK_TYPE_API.Get_Description(:WORK_TYPE_ID)");
		mgr.getASPField("WORK_TYPE_ID").setValidation("WORKTYPEDESCRIPTION");
		f.setDefaultNotVisible();
		f.setReadOnly();

		f = headblk.addField("VENDOR_NO");
		f.setSize(12);
		f.setReadOnly();
		f.setMandatory();
		f.setLabel("PCMWHISTORICALSEPARATERMBVENDORNO: Contractor");
		f.setDefaultNotVisible();
		f.setUpperCase();
		f.setMaxLength(20);

		f = headblk.addField("VENDOR_NAME");
		f.setSize(25);
		f.setLabel("PCMWHISTORICALSEPARATERMBVENDORNAME: Contractor Name");
		f.setFunction("Maintenance_Supplier_API.Get_Description(:VENDOR_NO)");
		f.setDefaultNotVisible();
		f.setReadOnly();

		f = headblk.addField("PM_TYPE");
		f.setSize(18);
		f.setMandatory();
		f.setReadOnly();
		f.setSelectBox();
      //Bug 81463, start
		//f.setDefaultNotVisible();
      //Bug 81463, end
		f.enumerateValues("PM_TYPE_API");
		f.setLabel("PCMWHISTORICALSEPARATERMBPMTYPE: PM Type");
		f.setMaxLength(30);

		f = headblk.addField("REPORTED_BY");
		f.setSize(12);
		f.setReadOnly();
      //Bug 81463, start
		//f.setDefaultNotVisible();
      //Bug 81463, end
		f.setLabel("PCMWHISTORICALSEPARATERMBREPORTEDBY: Reported by");
		f.setUpperCase();
		f.setMaxLength(20);
		f.setLOVProperty("TITLE",mgr.translate("PCMWHISTORICALSEPARATERMBREPBY: List of Reported by"));

		f = headblk.addField("REPORTED_BY_ID");
		f.setSize(6);
		f.setHidden();

		f = headblk.addField("NAME");
		f.setSize(27);
		f.setDefaultNotVisible();
		f.setFunction("PERSON_INFO_API.Get_Name(:REPORTED_BY)");
		mgr.getASPField("REPORTED_BY").setValidation("NAME");
		f.setReadOnly();

		f = headblk.addField("PREPARED_BY");
		f.setSize(12);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBPREPAREDBY: Prepared by");
		f.setUpperCase();
		f.setMaxLength(20);
		f.setDefaultNotVisible();
		f.setLOVProperty("TITLE",mgr.translate("PCMWHISTORICALSEPARATERMBPREBY: List of Prepared by"));

		f = headblk.addField("WORK_MASTER_SIGN");
		f.setSize(12);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBWORKMASTRSIGN: Executed By");
		f.setUpperCase();
		f.setMaxLength(20);
		f.setDefaultNotVisible();
		f.setLOVProperty("TITLE",mgr.translate("PCMWHISTORICALSEPARATERMBWOMASN: List of Executed By"));

		f = headblk.addField("WORK_LEADER_SIGN");
		f.setSize(12);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBWORKLEADERSIGN: Work Leader");
		f.setUpperCase();
		f.setMaxLength(20);
		f.setDefaultNotVisible();
		f.setLOVProperty("TITLE",mgr.translate("PCMWHISTORICALSEPARATERMBWOLESI: List of Work Leader"));

		f = headblk.addField("AUTHORIZE_CODE");
		f.setSize(12);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setDynamicLOV("ORDER_COORDINATOR",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBAUTHORIZECODE: Coordinator");
		f.setUpperCase();
		f.setMaxLength(20);

		f = headblk.addField("COST_CENTER");
		f.setSize(12);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBCOSTCENTER: Cost Center");
		f.setMaxLength(10);

		f = headblk.addField("OBJECT_NO");
		f.setSize(10);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBOBJECTNO: Object no");
		f.setMaxLength(10);

		f = headblk.addField("REPAIR_FLAG");
		f.setSize(20);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBREPAIRFLAG: Repair Work Order");
		f.setCheckBox("FALSE,TRUE");

		f = headblk.addField("ERRCAUSE");
		f.setDbName("ERR_CAUSE");  
		f.setSize(10);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setDynamicLOV("MAINTENANCE_CAUSE_CODE",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBERRCAUSE: Error Cause");
		f.setUpperCase();
		f.setMaxLength(3);
		f.setLOVProperty("TITLE",mgr.translate("PCMWHISTORICALSEPARATERMBLSTERRCA: List of Error Cause"));

		f = headblk.addField("CAUSECODEDESC");
		f.setSize(22);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBCAUSECODEDESC: Error Description");
		f.setFunction("MAINTENANCE_CAUSE_CODE_API.Get_Description(:ERRCAUSE)");

		f = headblk.addField("ERR_SYMPTOM");
		f.setSize(10);
		f.setReadOnly();
		f.setDynamicLOV("WORK_ORDER_SYMPT_CODE",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBERRSYMPTOM: Symptom ID");
		f.setUpperCase();
		f.setMaxLength(3);
		f.setDefaultNotVisible();
		f.setLOVProperty("TITLE",mgr.translate("PCMWHISTORICALSEPARATERMBERSY: List of Symptom ID"));

		f = headblk.addField("SYMPTOMDESCRIPTION");
		f.setSize(22);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setFunction("WORK_ORDER_SYMPT_CODE_API.Get_Description(:ERR_SYMPTOM)");

                //Bug 73136 Start
		f = headblk.addField("ERR_DESCR_LO");
		f.setSize(48);
		f.setLabel("PCMWHISTORICALSEPARATERMBERRDESCRLO: Fault Desc");
		f.setDefaultNotVisible();
		f.setHeight(3);
		f.setReadOnly();
                //Bug 73136 End

		f = headblk.addField("ERRCLASS");
		f.setDbName("ERR_CLASS");
		f.setSize(10);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setDynamicLOV("WORK_ORDER_CLASS_CODE",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBERRCLASS: Class ID");
		f.setUpperCase();
		f.setMaxLength(3);
		f.setLOVProperty("TITLE",mgr.translate("PCMWHISTORICALSEPARATERMBLISTCLSID: List of Class ID"));

		f = headblk.addField("WOCLASSCODEDESC");
		f.setSize(28);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBWOCLASSCODEDESC: Class Description");
		f.setFunction("WORK_ORDER_CLASS_CODE_API.Get_Description(:ERRCLASS)");

		f = headblk.addField("ERRTYPE");
		f.setDbName("ERR_TYPE");
		f.setSize(10);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setDynamicLOV("WORK_ORDER_TYPE_CODE",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBERRTYPE: Type");
		f.setUpperCase();
		f.setMaxLength(3);

		f = headblk.addField("WOTYPECODEDESC");
		f.setSize(25);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBWOTYPECODEDESC: Type Description");
		f.setFunction("WORK_ORDER_TYPE_CODE_API.Get_Description(:ERRTYPE)");

		f = headblk.addField("ERR_DISCOVER_CODE");
		f.setSize(16);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setDynamicLOV("WORK_ORDER_DISC_CODE",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBERRDISCOVERCODE: Discovery Code");
		f.setUpperCase();
		f.setMaxLength(3);
		f.setLOVProperty("TITLE",mgr.translate("PCMWHISTORICALSEPARATERMBERDISCO: List of Discovery Code"));

		f = headblk.addField("DISCOVERDESCRIPTION");
		f.setSize(25);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setFunction("WORK_ORDER_DISC_CODE_API.Get_Description(:ERR_DISCOVER_CODE)");
		mgr.getASPField("ERR_DISCOVER_CODE").setValidation("DISCOVERDESCRIPTION");

		f = headblk.addField("ACTION_CODE_ID");
		f.setSize(12);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setDynamicLOV("MAINTENANCE_ACTION",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBACTIONCODEID: Action");
		f.setUpperCase();
		f.setMaxLength(10);
		f.setLOVProperty("TITLE",mgr.translate("PCMWHISTORICALSEPARATERMBACCOID: List of Action"));

		f = headblk.addField("PM_NO","Number","#");
		f.setSize(10);
		f.setReadOnly();
		f.setLOV("PmActionLov1.page",600,450);
		f.setMandatory();
      //Bug 81463, start
		//f.setDefaultNotVisible();
      //Bug 81463, end
		f.setLabel("PCMWHISTORICALSEPARATERMBPMNO: PM No");
		f.setLOVProperty("TITLE",mgr.translate("PCMWHISTORICALSEPARATERMBPNO: List of PM No"));

		f = headblk.addField("PM_REVISION");
		f.setSize(10);
		f.setReadOnly();
		f.setDynamicLOV("PM_ACTION","PM_NO",600,445);
		f.setLOVProperty("TITLE",mgr.translate("PCMWHISTORICALSEPARATERMBPREV: List of PM Revisions"));
		f.setMandatory();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBPMREV: PM Revision");
		f.setMaxLength(6);

		f = headblk.addField("TEST_POINT_ID");
		f.setSize(12);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setDynamicLOV("EQUIPMENT_OBJECT_TEST_PNT","MCH_CODE_CONTRACT,MCH_CODE",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBTESTPOINTID: Testpoint ID");
		f.setUpperCase();
		f.setMaxLength(6);
		f.setLOVProperty("TITLE",mgr.translate("PCMWHISTORICALSEPARATERMBTEPOID: List of Testpoint ID"));

		f = headblk.addField("PLAN_F_WEEK");
		f.setSize(25);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBPLANFWEEK: Planned Completion Week");
		f.setMaxLength(4);

		f = headblk.addField("REQUIRED_START_DATE","Datetime");
		f.setSize(20);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBREQUIREDSTARTDATE: Required Start");

		f = headblk.addField("WORK_DESCR_LO");
		f.setSize(45);
		f.setDefaultNotVisible();
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBWORKDESCRLO: Work Description");

		f = headblk.addField("COMPANY");
		f.setSize(8);
		f.setHidden();
		f.setLabel("PCMWHISTORICALSEPARATERMBCOMPANY: Company");
		f.setUpperCase();
		f.setMaxLength(20);

		f = headblk.addField("QUOTATION_ID","Number");
		f.setSize(16);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setDynamicLOV("WORK_ORDER_QUOTATION",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBQUOTATIONID: Quotation Id");

		f = headblk.addField("WOQUOTSALESMAN");
		f.setSize(16);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setDynamicLOV("SALES_PART_SALESMAN",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBWOQUOTSALESMAN: Salesman Code");
		f.setFunction("WORK_ORDER_QUOTATION_API.Get_Salesman_Code(:QUOTATION_ID)");

		f = headblk.addField("CUSTOMER_NO");
		f.setSize(16);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setDynamicLOV("CUSTOMER_INFO",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBCUSTOMERNO: Customer No");
		f.setUpperCase();
		f.setMaxLength(20);

		f = headblk.addField("CUSTOMERNAME");
		f.setSize(35);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBCUSTOMERNAME: Customer Name");
		f.setFunction("CUSTOMER_INFO_API.Get_Name(:CUSTOMER_NO)");

		f = headblk.addField("AGREEMENT_ID");
		f.setSize(16);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setDynamicLOV("CUSTOMER_AGREEMENT",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBAGREEMENTID: Agreement Id");
		f.setUpperCase();
		f.setMaxLength(10);

		f = headblk.addField("CONTACT");
		f.setSize(16);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBCONTACT: Contact");
		f.setMaxLength(30);

		f = headblk.addField("REFERENCE_NO");
		f.setSize(12);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBREFERENCENO: Reference No");
		f.setUpperCase();
		f.setMaxLength(25);

		f = headblk.addField("PHONE_NO");
		f.setSize(12);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBPHONENO: Phone No");
		f.setMaxLength(20);

		f = headblk.addField("FIXED_PRICE");
		f.setSize(16);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.enumerateValues("FIXED_PRICE_API");
		f.setLabel("PCMWHISTORICALSEPARATERMBFIXEDPRICE: Fixed Price");
		f.setCheckBox("FALSE,TRUE");
		f.setMaxLength(20);

		f = headblk.addField("NOTE_ID","Number");
		f.setSize(10);
		f.setDefaultNotVisible();
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBNOTEID: Note ID");
		f.setMaxLength(10);

		f = headblk.addField("LU_NAME");
		f.setHidden();
		f.setFunction("'HistoricalSeparate'");

		f = headblk.addField("KEY_REF");
		f.setHidden();
		f.setFunction("CONCAT('WO_NO=',CONCAT(WO_NO,'^'))");

		f = headblk.addField("DOCUMENT");
		if (mgr.isModuleInstalled("DOCMAN"))
			f.setFunction("substr(Doc_Reference_Object_API.Exist_Obj_Reference('HistoricalSeparate',CONCAT('WO_NO=',CONCAT(WO_NO,'^'))),1, 5)");
		else
			f.setFunction("''");
		f.setUpperCase();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBDOCUMENT: Has Documents");
		f.setReadOnly();
		f.setCheckBox("FALSE,TRUE");
		f.setSize(18);

		f = headblk.addField("REG_DATE","Datetime");
		f.setSize(13);
		f.setMandatory();
      //Bug 81463, start
		//f.setDefaultNotVisible();
      //Bug 81463, end
		f.setLabel("PCMWHISTORICALSEPARATERMBREGDATE: Date");
		f.setReadOnly();

		//Bug 87766, Start, Modified the label and the function
		f = headblk.addField("CBISCONNECTED");
		f.setSize(21);
		f.setLabel("PCMWHISTORICALSEPARATERMBCBISCONNECTED: In a Structure");
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setCheckBox("FALSE,TRUE");
		f.setFunction("substr(WORK_ORDER_CONNECTION_API.Has_Connection_Up(:WO_NO),1,5)");

		f = headblk.addField("CBHASCONNECTIONS");
		f.setSize(20);
		f.setLabel("PCMWHISTORICALSEPARATERMBCBHASCONNECTIONS: Has Structure");
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setCheckBox("FALSE,TRUE");
		f.setFunction("substr(WORK_ORDER_CONNECTION_API.Has_Connection_Down(:WO_NO),1,5)");
		//Bug 87766, End

		f = headblk.addField("CBWARRANTYONOBJECT");
		f.setSize(21);
		f.setLabel("PCMWHISTORICALSEPARATERMBCBWARRANTYONOBJECT: Warranty on Object");
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setCheckBox("FALSE,TRUE");
		f.setFunction("''");

		f = headblk.addField("FIXED_PRICE_DB");
		f.setSize(20);
		f.setLabel("PCMWHISTORICALSEPARATERMBFIXEDPRICEDB: Fixed Price");
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setCheckBox("FALSE,TRUE");
		f.setHidden();

		f = headblk.addField("CBHASDOCUMENTS");
		f.setSize(20);
		f.setLabel("PCMWHISTORICALSEPARATERMBCBHASDOCUMENTS: Has Documents");
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setCheckBox("FALSE,TRUE");
		f.setFunction("''");

		f = headblk.addField("NOTE");
		f.setSize(20);
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBNOTE: Inspection Note");
		f.setReadOnly();
		f.setMaxLength(45);

		f = headblk.addField("PM_DESCR");
		f.setSize(20);
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBPMDESCR: PM Description");
		f.setReadOnly();
		f.setMaxLength(45);

		f = headblk.addField("PRE_ACCOUNTING_ID","Number");
                f.setHidden();

		f = headblk.addField("WO_KEY_VALUE");
		f.setSize(11);
		f.setHidden();
		f.setUpperCase();

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

		f = headblk.addField("CAN_REOPEN_WO");
		f.setHidden();
		// 040128  ARWILK  Begin  (Enable Multirow RMB actions)
		f.setFunction("Historical_Work_Order_API.Check_Work_Order(:WO_NO)");
		// 040128  ARWILK  End  (Enable Multirow RMB actions)

		f = headblk.addField("ERR_CLASS");
		f.setSize(10);
		f.setDynamicLOV("WORK_ORDER_CLASS_CODE",600,445);
		f.setLOVProperty("TITLE", mgr.translate("PCMWHISTORICALSEPARATERMBLISTCLASS: List of Class"));
		f.setLabel("PCMWHISTORICALSEPARATERMBERRCLS: Class");
		f.setDefaultNotVisible();
		f.setUpperCase();
		f.setReadOnly();

		f = headblk.addField("ERRCLASSDESCR");
		f.setSize(28);
		f.setLabel("PCMWHISTORICALSEPARATERMBERRCLSDES: Class Description"); 
		f.setReadOnly();
		f.setFunction("WORK_ORDER_CLASS_CODE_API.Get_Description(:ERR_CLASS)");
		f.setDefaultNotVisible();
		mgr.getASPField("ERR_CLASS").setValidation("ERRCLASSDESCR");

		f = headblk.addField("ERR_TYPE");
		f.setSize(10);
		f.setDefaultNotVisible();
		f.setDynamicLOV("WORK_ORDER_TYPE_CODE",600,445);
		f.setLOVProperty("TITLE", mgr.translate("PCMWHISTORICALSEPARATERMBERTY: List of Type"));
		f.setLabel("PCMWHISTORICALSEPARATERMBERRTYPE: Type");
		f.setUpperCase();
		f.setReadOnly();

		f = headblk.addField("ERRTYPEDESC");
		f.setSize(28);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBERRTYPEDES: Type Description");
		f.setFunction("WORK_ORDER_TYPE_CODE_API.Get_Description(:ERR_TYPE)");
		mgr.getASPField("ERR_TYPE").setValidation("ERRTYPEDESC");

		f = headblk.addField("PERFORMED_ACTION_ID");
		f.setSize(10);
		f.setDefaultNotVisible();
		f.setDynamicLOV("MAINTENANCE_PERF_ACTION",600,445);
		f.setLOVProperty("TITLE", mgr.translate("PCMWHISTORICALSEPARATERMBPEACID: List of Performed Action"));
		f.setLabel("PCMWHISTORICALSEPARATERMBPERFACTID: Performed Action");
		f.setUpperCase();
		f.setReadOnly();

		f = headblk.addField("PERFORMEDACTIONDESC");
		f.setSize(28);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBPERFORMEDACTIONDESC: Performed Action Description");
		f.setFunction("MAINTENANCE_PERF_ACTION_API.Get_Description(:PERFORMED_ACTION_ID)");
		mgr.getASPField("PERFORMED_ACTION_ID").setValidation("PERFORMEDACTIONDESC");

		f = headblk.addField("ERR_CAUSE");
		f.setSize(10);
		f.setLabel("PCMWHISTORICALSEPARATERMBPERFACTDES: Performed Action Description");
		f.setDynamicLOV("MAINTENANCE_CAUSE_CODE",600,445);
		f.setLOVProperty("TITLE", mgr.translate("PCMWHISTORICALSEPARATERMBLSTCA: List of Cause"));
		f.setLabel("PCMWHISTORICALSEPARATERMBERERCAUSE: Cause");
		f.setDefaultNotVisible();
		f.setUpperCase();
		f.setReadOnly();

		f = headblk.addField("ERRCAUSEDESC");
		f.setSize(28);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setFunction("MAINTENANCE_CAUSE_CODE_API.Get_Description(:ERR_CAUSE)");
		mgr.getASPField("ERR_CAUSE").setValidation("ERRCAUSEDESC");

		f = headblk.addField("ERR_CAUSE_LO");
		f.setSize(46);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBECLO: Cause Description");
		f.setHeight(2);

		f = headblk.addField("WORK_DONE");
		f.setReadOnly();
		f.setSize(47);
		f.setLabel("PCMWHISTORICALSEPARATERMBWORK_DONE: Work Done");

		f = headblk.addField("PERFORMED_ACTION_LO");
		f.setSize(46);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBPERFORMEDACTIONLO: Work Details");
		f.setHeight(2);

		f = headblk.addField("WO_STATUS_ID");
		f.setSize(25);
		f.setHidden();
		f.setLabel("PCMWHISTORICALSEPARATERMBWOSTID: Status");
		f.setReadOnly(); 

		f = headblk.addField("LATEST_TRANSACTION");
		f.setSize(40);
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTSEPRMBCURRPOS: Latest Transaction");
		f.setFunction("PART_SERIAL_CATALOG_API.Get_Alt_Latest_Transaction(:CONTRACT,:MCH_CODE)");
		f.setReadOnly();

		f = headblk.addField("WORKCENTER");
		f.setSize(20);
		f.setDefaultNotVisible();

		f.setLabel("PCMWHISTSEPRMBWORKCENTER: Work Center");
		f.setFunction("''");
		f.setReadOnly(); 

		f = headblk.addField("RESOURCENO");
		f.setFunction("''");
		f.setSize(20);
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTSEPRMBRESOURCENO: Resource");
		f.setReadOnly(); 

		f = headblk.addField("LOADHOURS");
		f.setFunction("''");
		f.setSize(20);
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTSEPRMBLOADHOURS: Load");
		f.setReadOnly(); 

		f = headblk.addField("CBPROJCONNECTED");
		f.setFunction("Historical_Separate_API.Is_Project_Connected(:WO_NO)");
		f.setLabel("PCMWHISTSEPARATEPROJCONN: Project Connected");
		f.setCheckBox("FALSE,TRUE");
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setSize(5);

		f = headblk.addField("RELEASE_CERTIFICATE");
		f.setSize(13);
		f.setCheckBox("FALSE,TRUE");
		f.setLabel("PCMWHISTORICALSEPARATERMBRELEASECERT: Release Certificate");
		f.setReadOnly();
		f.setFunction("RELEASE_CERTIFICATE_API.HAS_RELEASE_CERTIFICATE(:WO_NO)");
		f.setDefaultNotVisible();

		f = headblk.addField("PROJECT_NO");
		f.setSize(12);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBPROJECTNO: Project No");
		f.setMaxLength(10);

		if (mgr.isModuleInstalled("PROJ"))
		{

			f = headblk.addField("PROGRAM_ID");
			f.setLabel("PCMWHISTSEPARATERMBPROGRAMID: Program ID");
			f.setReadOnly();
			f.setDefaultNotVisible();
			f.setFunction("PROJECT_API.Get_Program_Id(:PROJECT_NO)");
			f.setSize(20);

			f = headblk.addField("PROGRAMDESC");
			f.setLabel("PCMWHISTSEPARATERMBPROGRAMDESC: Program Description");
			f.setReadOnly();
			f.setDefaultNotVisible();
			f.setFunction("PROJECT_PROGRAM_API.Get_Description(PROJECT_API.Get_Company(:PROJECT_NO),PROJECT_API.Get_Program_Id(:PROJECT_NO))");
			f.setSize(30);

			f = headblk.addField("PROJECTDESC");
			f.setLabel("PCMWHISTSEPARATERMBPROJECTDESC: Project Description");
			f.setReadOnly();
			f.setDefaultNotVisible();
			f.setFunction("PROJECT_API.Get_Description(:PROJECT_NO)");
			f.setSize(30);

			f = headblk.addField("SUBPROJECT_ID");
			f.setLabel("PCMWHISTSEPARATERMBSUBPROJECTID: Sub Project ID");
			f.setReadOnly();
			f.setDefaultNotVisible();
			f.setFunction("ACTIVITY_API.Get_Sub_Project_Id(:ACTIVITY_SEQ)");
			f.setSize(20);

			f = headblk.addField("SUBPROJECTDESC");
			f.setLabel("PCMWHISTSEPARATERMBSUBPROJECTDESC: Sub Project Description");
			f.setReadOnly();
			f.setDefaultNotVisible();
			f.setFunction("ACTIVITY_API.Get_Sub_Project_Description(:ACTIVITY_SEQ)");
			f.setSize(30);

			f = headblk.addField("ACTIVITY_ID");
			f.setLabel("PCMWHISTSEPARATERMBACTIVITYID: Activity ID");
			f.setReadOnly();
			f.setDefaultNotVisible();
			f.setFunction("ACTIVITY_API.Get_Activity_No(:ACTIVITY_SEQ)");
			f.setSize(20);

			f = headblk.addField("ACTIVITYDESC");
			f.setLabel("PCMWHISTSEPARATERMBACTIVITYDESC: Activity Description");
			f.setReadOnly();
			f.setDefaultNotVisible();
			f.setFunction("ACTIVITY_API.Get_Description(:ACTIVITY_SEQ)");
			f.setSize(30);

			f = headblk.addField("ACTIVITY_SEQ","Number");
			f.setLabel("PCMWHISTSEPARATERMBACTIVITYSEQ: Activity Sequence");
			f.setSize(12);
			f.setDefaultNotVisible();
			f.setReadOnly();

			f = headblk.addField("FAULT_REP_FLAG","Number");
			f.setHidden();
		}

		headblk.setView("HISTORICAL_SEPARATE");
		headblk.defineCommand("HISTORICAL_SEPARATE_API", "");
		headblk.disableDocMan();
		headset = headblk.getASPRowSet();

		headtbl = mgr.newASPTable(headblk);
		headtbl.setTitle(mgr.translate("PCMWHISTORICALSEPARATERMBHD: Historical Work Order"));
		headtbl.setWrap();
		// 040128  ARWILK  Begin  (Enable Multirow RMB actions)
		headtbl.enableRowSelect();
		// 040128  ARWILK  End  (Enable Multirow RMB actions)

		headbar = mgr.newASPCommandBar(headblk);
		headbar.setBorderLines(false,true);

		headbar.addCustomCommand("structure", mgr.translate("PCMWHISTORICALSEPARATERMBSTRUCT: Structure..."));
		headbar.addCustomCommandSeparator();
		headbar.addCustomCommand("copy",mgr.translate("PCMWHISTORICALSEPARATERMBCOPY: Copy Historical Work Order..."));
		headbar.addCustomCommandSeparator();
      //Bug 83532, Start
      headbar.addCustomCommandSeparator();
      headbar.addCustomCommand("workorderaddr", mgr.translate("PCMWHISTORICALSEPARATERMBWORKORDERADDR: Work Order Address..."));
      headbar.addCustomCommandSeparator();
      //Bug 83532, End
		headbar.addCustomCommand("reopen", mgr.translate("PCMWHISTORICALSEPARATERMBREOP: Reopen"));
		headbar.addCustomCommandSeparator();
		headbar.addCustomCommand("requisitions", mgr.translate("PCMWHISTORICALSEPARATERMBREQUI: Requisitions..."));
		headbar.addCustomCommand("budget",mgr.translate("PCMWHISTORICALSEPARATERMBBUDGET: Budget..."));
		headbar.addCustomCommand("freeNotes",mgr.translate("PCMWHISTORICALSEPARATERMBFREENOTES: Free Notes..."));
		headbar.addCustomCommand("coInfo",mgr.translate("PCMWHISTORICALSEPARATERMBCOINFO: CO Information..."));
		headbar.addCustomCommand("returns",mgr.translate("PCMWHISTORICALSEPARATERMBRETURNS: Returns..."));
		headbar.addCustomCommandSeparator();
		headbar.addCustomCommand("printHistWO",mgr.translate("PCMWHISTORICALSEPARATERMBPRINTHISTWO: Print..."));
		headbar.addCustomCommand("printAuthRelCerti",mgr.translate("PCMWHISTORICALSEPARATERMBPRINTREPRTAUTHRELCERTI: Print/Reprint Authorized Release Certificate..."));
		headbar.addCustomCommand("printReleaseCertif",mgr.translate("PCMWHISTORICALSEPARATERMBRELCERTIF: Release Certificate..."));

		headbar.defineCommand("reopen", "reopen" , "viewWorkOrder");

		// 031210  ARWILK  Begin  (Replace blocks with tabs)
		headbar.addCustomCommand("activateOperations", "");
		headbar.addCustomCommand("activateMaterials", "");
		headbar.addCustomCommand("activatePermits", "");
		headbar.addCustomCommand("activateTimeReport", "");
		headbar.addCustomCommand("activateToolsAndFacilities", "");
		headbar.addCustomCommand("activatePostings", "");
		headbar.addCustomCommand("activateJobs", "");
		headbar.addCustomCommand("activateMaintTask", "");
		// 031210  ARWILK  End  (Replace blocks with tabs)

		// 040128  ARWILK  Begin  (Enable Multirow RMB actions)
		headbar.addCommandValidConditions("reopen",   "CAN_REOPEN_WO",   "Enable" ,   "True");

		headbar.enableMultirowAction();
		headbar.removeFromMultirowAction("copy");
//		Bug 83532, Start
      headbar.removeFromMultirowAction("workorderaddr");
//		Bug 83532, Start
		headbar.removeFromMultirowAction("requisitions");
		headbar.removeFromMultirowAction("budget");
		headbar.removeFromMultirowAction("freeNotes");
		headbar.removeFromMultirowAction("coInfo");
		headbar.removeFromMultirowAction("returns");
		// 040128  ARWILK  End  (Enable Multirow RMB actions)

		headlay = headblk.getASPBlockLayout();   
		headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
		headlay.unsetAutoLayoutSelect();  
		headlay.setSimple("MCH_CODE_DESCRIPTION"); 
		headlay.setSimple("NAME");
		headlay.setSimple("ORGCODEDESCRIPTION");
		headlay.setSimple("WORKTYPEDESCRIPTION");
		headlay.setSimple("PRIORITYDESCRIPTION");
		headlay.setSimple("DISCOVERDESCRIPTION");
		headlay.setSimple("SYMPTOMDESCRIPTION");
		headlay.setSimple("ERRCLASSDESCR");
		headlay.setSimple("ERRTYPEDESC");
		headlay.setSimple("PERFORMEDACTIONDESC");
		headlay.setSimple("ERRCAUSEDESC");
		headlay.setSimple("OP_STATUS_DESC");
		headlay.setSimple("CALL_CODE_DESC");
		headlay.setSimple("VENDOR_NAME");

//		Bug 69168, Start
      //Bug 82715, Start
		headlay.defineGroup("","WO_NO,CONTRACT,ERR_DESCR,WOSTATUSID,CONNECTION_TYPE,MCH_CODE,MCH_CODE_DESCRIPTION,MCH_CODE_CONTRACT,ORG_CODE,ORGCODEDESCRIPTION,GROUPID,MCHCODEPOS",false,true);
		//Bug 82715, End
      headlay.defineGroup(mgr.translate("PCMWHISTORICALSEPARATERMBGRPLABEL1: Planning Information"),"WORK_TYPE_ID,WORKTYPEDESCRIPTION,PRIORITY_ID,PRIORITYDESCRIPTION,REG_DATE,REPORTED_BY,NAME",true,false);
		headlay.defineGroup(mgr.translate("PCMWHISTORICALSEPARATERMBGRPLABEL2: Schedule"),"PLAN_S_DATE,REQUIRED_START_DATE,PLAN_F_DATE,REQUIRED_END_DATE,REALSDATE,REALFDATE,PLAN_HRS,PLANNED_MAN_HRS",true,false); 
		headlay.defineGroup(mgr.translate("PCMWHISTORICALSEPARATERMBGRPLABEL3: Fault Report Information"),"ERR_DISCOVER_CODE,DISCOVERDESCRIPTION,ERR_SYMPTOM,SYMPTOMDESCRIPTION,ERR_DESCR_LO",true,false);
		headlay.defineGroup(mgr.translate("PCMWHISTORICALSEPARATERMBGRPLABEL4: Work Order Information"),"CBISCONNECTED,CBHASCONNECTIONS,CBWARRANTYONOBJECT,REPAIR_FLAG,FIXED_PRICE,DOCUMENT,CBPROJCONNECTED,RELEASE_CERTIFICATE",true,false);   
		headlay.defineGroup(mgr.translate("PCMWHISTORICALSEPARATERMBGRPLABEL5: Report In"),"ERR_CLASS,ERRCLASSDESCR,ERR_TYPE,ERRTYPEDESC,PERFORMED_ACTION_ID,PERFORMEDACTIONDESC,ERR_CAUSE,ERRCAUSEDESC,ERR_CAUSE_LO,WORK_DONE,TEST_POINT_ID,PERFORMED_ACTION_LO",true,false);  
		headlay.defineGroup(mgr.translate("PCMWHISTORICALSEPARATERMBGRPLABEL6: PM Information"),"PM_NO,PM_REVISION,NOTE,ACTION_CODE_ID,PM_DESCR",true,false);   
		headlay.defineGroup(mgr.translate("PCMWHISTORICALSEPARATERMBGRPLABEL7: Prepare"),"WORK_DESCR_LO,LATEST_TRANSACTION",true,false);   
		headlay.defineGroup(mgr.translate("PCMWHISTORICALSEPARATERMBGRPLABEL8: Prepare Codes"),"OP_STATUS_ID,OP_STATUS_DESC,CALL_CODE,CALL_CODE_DESC,VENDOR_NO,VENDOR_NAME",true,false);   
		headlay.defineGroup(mgr.translate("PCMWHISTORICALSEPARATERMBGRPLABEL9: Signatures"),"WORK_LEADER_SIGN,WORK_MASTER_SIGN,PREPARED_BY",true,false);   
		headlay.defineGroup(mgr.translate("PCMWHISTORICALSEPARATERMBGRPLABEL10: Work Center Load"),"WORKCENTER,RESOURCENO,LOADHOURS",true,false);   
		if (mgr.isModuleInstalled("PROJ"))
			headlay.defineGroup(mgr.translate("PCMWHISTORICALSEPARATERMBGRPLABEL11: Project Information"),"PROGRAM_ID,PROGRAMDESC,PROJECT_NO,PROJECTDESC,SUBPROJECT_ID,SUBPROJECTDESC,ACTIVITY_ID,ACTIVITYDESC,ACTIVITY_SEQ",true,false);
//		Bug 69168, End

		// 031210  ARWILK  Begin  (Replace blocks with tabs)
		tabs = mgr.newASPTabContainer();
		tabs.addTab(mgr.translate("PCMWHISTORICALSEPARATEOPERTAB: Operations"), "javascript:commandSet('HEAD.activateOperations','')");
		tabs.addTab(mgr.translate("PCMWHISTORICALSEPARATEMATERITAB: Materials"), "javascript:commandSet('HEAD.activateMaterials','')");
		tabs.addTab(mgr.translate("PCMWHISTORICALSEPARATEPERMITTAB: Permits"), "javascript:commandSet('HEAD.activatePermits','')");
		tabs.addTab(mgr.translate("PCMWHISTORICALSEPARATETIMEREPTAB: Time Report"), "javascript:commandSet('HEAD.activateTimeReport','')");
		tabs.addTab(mgr.translate("PCMWHISTORICALSEPARATETOOLSFACTAB: Tools and Facilities"), "javascript:commandSet('HEAD.activateToolsAndFacilities','')");
		tabs.addTab(mgr.translate("PCMWHISTORICALSEPARATEPOSTTAB: Postings"), "javascript:commandSet('HEAD.activatePostings','')");
		tabs.addTab(mgr.translate("PCMWHISTORICALSEPARATEJOBTAB: Jobs"), "javascript:commandSet('HEAD.activateJobs','')");

		if (mgr.isModuleInstalled("VIM"))
		{
			tabs.addTab(mgr.translate("PCMWHISTORICALSEPARATEMAINTTSKTAB: Maint Task"), "javascript:commandSet('HEAD.activateMaintTask','')");
		}
		// 031210  ARWILK  End  (Replace blocks with tabs)

		headbar.addCommandValidConditions("printAuthRelCerti",   "WOSTATUSID",  "Disable",  "OBSERVED;PREPARED;RELEASED;UNDERPREPARATION");
		headbar.appendCommandValidConditions("printAuthRelCerti", "WO_NO", "Disable", "null");

		headbar.addCommandValidConditions("printReleaseCertif",   "WOSTATUSID",  "Disable",  "OBSERVED;PREPARED;RELEASED;UNDERPREPARATION");
                headbar.appendCommandValidConditions("printReleaseCertif", "WO_NO", "Disable", "null");


		//-----------------------------------------------------------------------------------------------
		//----------------- block used for RMB status funtions  -----------------------------------------
		//-----------------------------------------------------------------------------------------------

		prntblk = mgr.newASPBlock("RMBBLK");
		prntblk.addField("ATTR0");
		prntblk.addField("ATTR1");
		prntblk.addField("ATTR2");
		prntblk.addField("ATTR3");
		prntblk.addField("ATTR4");
		prntblk.addField("RESULT_KEY");

		//------------------------------------------------------------------------------------------------
		//-------------------------------------------- Permits Tab  --------------------------------------
		//------------------------------------------------------------------------------------------------

		itemblk1 = mgr.newASPBlock("ITEM1");

		f = itemblk1.addField("ITEM1_OBJID");
		f.setHidden();
		f.setDbName("OBJID");

		f = itemblk1.addField("ITEM1_OBJVERSION");
		f.setHidden();
		f.setDbName("OBJVERSION");

		f = itemblk1.addField("ITEM1_WO_NO","Number","#");
		f.setSize(11);
		f.setHidden();
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM2WONO: WO Number");
		f.setDbName("WO_NO");

		f = itemblk1.addField("PERMIT_SEQ","Number");
		f.setSize(11);
		f.setDynamicLOV("PERMIT",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBPERMITSEQ: Permit");

		f = itemblk1.addField("PERMITTYPEID");
		f.setSize(12);
		f.setLabel("PCMWHISTORICALSEPARATERMBPERMITTYPEID: Type");
		f.setFunction("PERMIT_API.Get_Permit_Type_Id(:PERMIT_SEQ)");
		mgr.getASPField("PERMIT_SEQ").setValidation("PERMITTYPEID");
		f.setReadOnly();

		f = itemblk1.addField("PERMITDESCRIPTION");
		f.setSize(57);
		f.setLabel("PCMWHISTORICALSEPARATERMBPERMITDESCRIPTION: Description");
		f.setFunction("PERMIT_API.Get_Description(:PERMIT_SEQ)");
		mgr.getASPField("PERMIT_SEQ").setValidation("PERMITDESCRIPTION");
		f.setReadOnly();

		itemblk1.setView("WORK_ORDER_PERMIT");
		itemblk1.defineCommand("WORK_ORDER_PERMIT_API", "");

		itemset1 = itemblk1.getASPRowSet();

		itemtbl1 = mgr.newASPTable(itemblk1);
		itemtbl1.setWrap();
		// 040128  ARWILK  Begin  (Enable Multirow RMB actions)
		itemtbl1.enableRowSelect();
		// 040128  ARWILK  End  (Enable Multirow RMB actions)

		itembar1 = mgr.newASPCommandBar(itemblk1);
		itembar1.setBorderLines(true,true);

		itembar1.defineCommand(itembar1.OKFIND, "okFindITEM1");
		itembar1.defineCommand(itembar1.COUNTFIND, "countFindITEM1");

		itembar1.addCustomCommand("viewPermit", mgr.translate("PCMWHISTORICALSEPARATERMBVIPER: View Permit..."));

		// 040128  ARWILK  Begin  (Enable Multirow RMB actions)
		itembar1.enableMultirowAction();
		// 040128  ARWILK  End  (Enable Multirow RMB actions)

		itemblk1.setMasterBlock(headblk); 

		itemlay1 = itemblk1.getASPBlockLayout();    
		itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT); 
		itemlay1.unsetAutoLayoutSelect();              

		//------------------------------------------------------------------------------------------------
		//-------------------------------------------- Postings Tab  -------------------------------------
		//------------------------------------------------------------------------------------------------

		itemblk2 = mgr.newASPBlock("ITEM2");

		f = itemblk2.addField("ITEM2_OBJID");
		f.setHidden();
		f.setDbName("OBJID");

		f = itemblk2.addField("ITEM2_OBJVERSION");
		f.setHidden();
		f.setDbName("OBJVERSION");

		f = itemblk2.addField("ITEM2_COMPANY");
		f.setSize(11);
		f.setHidden();
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM3COMPANY: Company Id");
		f.setDbName("COMPANY");
		f.setUpperCase();

		f = itemblk2.addField("ITEM2_WORK_ORDER_COST_TYPE");
		f.setSize(15);
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM3WORKORDERCOSTTYPE: Cost Type");
		f.setDbName("WORK_ORDER_COST_TYPE");
		f.setReadOnly();

		f = itemblk2.addField("CATALOG_NO");
		f.setSize(16);
		f.setLabel("PCMWHISTORICALSEPARATERMBCATALOGNO: Sales Part Number");
		f.setUpperCase();


		f = itemblk2.addField("LINE_DESCRIPTION");
		f.setSize(18);
		f.setLabel("PCMWHISTORICALSEPARATERMBSALESPARTCATALOGDESC: Description");
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = itemblk2.addField("ORDER_NO");
		f.setSize(16);
		f.setLabel("PCMWHISTORICALSEPARATERMBORDERNUMBER: Customer Order No");
		f.setReadOnly();
		f.setUpperCase();

		f = itemblk2.addField("LIST_PRICE","Number");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBLISTPRICE: List Price");
		f.setReadOnly();
		f.setDefaultNotVisible();


		f = itemblk2.addField("ITEM2_DISCOUNT","Number");
		f.setDbName("DISCOUNT");
		f.setSize(17);
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM2DISCOUNT: Discount");
		f.setDefaultNotVisible();

		f = itemblk2.addField("ITEM2SALESPRICEAMOUNT","Money");
		f.setSize(17);
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM2SALESPRICEAMOUNT: Sales Price Amount");
		f.setFunction("(LIST_PRICE - (NVL(DISCOUNT, 0)/100 * LIST_PRICE)) * QTY_TO_INVOICE");
		f.setReadOnly();

		f = itemblk2.addField("ITEM2_WORK_ORDER_ACCOUNT_TYPE");
		f.setSize(12);
		f.setHidden();
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM3WORKORDERACCOUNTTYPE: Account Type");
		f.setDbName("WORK_ORDER_ACCOUNT_TYPE");
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = itemblk2.addField("ITEM2_QTY","Number");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM3QTY: Hours/Qty");
		f.setDbName("QTY");
		f.setReadOnly();

		f = itemblk2.addField("QTY_TO_INVOICE","Number");
		f.setSize(13);
		f.setLabel("PCMWHISTORICALSEPARATERMBQTYTOINV: Qty To Invoice");

		f = itemblk2.addField("ITEM2_AMOUNT","Number");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM3AMOUNT: Cost Amount");
		f.setDbName("AMOUNT");
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = itemblk2.addField("POST_PART_OWNERSHIP");
		f.setDbName("PART_OWNERSHIP");
		f.setSize(25);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM3PARTOWNERSHIP: Ownership");

		f = itemblk2.addField("POST_PART_OWNERSHIP_DB");
		f.setDbName("PART_OWNERSHIP_DB");
		f.setSize(20);
		f.setHidden();

		f = itemblk2.addField("POSTING_OWNER");
		f.setDbName("OWNER");
		f.setSize(20);
		f.setMaxLength(20);
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM3PARTOWNER: Owner");
		f.setUpperCase();
		f.setReadOnly();

		f = itemblk2.addField("POST_OWNER_NAME");
		f.setSize(20);
		f.setMaxLength(100);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM3PARTOWNERNAME: Owner Name");
		f.setFunction("CUSTOMER_INFO_API.Get_Name(:POSTING_OWNER)");

                f = itemblk2.addField("CSS_TYPE");
                f.setSize(14);
                f.setLabel("PCMWHISTORICALSEPARATERMBCSSTYPE: CSS Type");
                f.setReadOnly();

                f = itemblk2.addField("ITEM2_STATE");
                f.setDbName("STATE");
                f.setSize(14);
                f.setLabel("PCMWHISTORICALSEPARATERMBITEM2STATUS: Status");
                f.setReadOnly();

		f = itemblk2.addField("WORK_ORDER_BOOK_STATUS");
		f.setSize(13);
		f.setLabel("PCMWHISTORICALSEPARATERMBWORKORDERBOOKSTATUS: Booking Status");
		f.setReadOnly();

		f = itemblk2.addField("SIGNATURE");
		f.setSize(13);
		f.setLabel("PCMWHISTORICALSEPARATERMBSIGNATURE: Auth Signature");
		f.setUpperCase();
		f.setDefaultNotVisible();

		f = itemblk2.addField("SIGNATURE_ID");
		f.setSize(13);
		f.setHidden();
		f.setLabel("PCMWHISTORICALSEPARATERMBSIGNATUREID: Auth Signature");
		f.setUpperCase();
		f.setDefaultNotVisible();

		f = itemblk2.addField("ITEM2_CMNT");
		f.setSize(19);
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM3CMNT: Comment");
		f.setDbName("CMNT");
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = itemblk2.addField("ACCNT");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBACCNT: Account");
		f.setDefaultNotVisible();

		f = itemblk2.addField("ITEM2_COST_CENTER");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM3COSTCENTER: Cost Center");
		f.setDbName("COST_CENTER");
		f.setDefaultNotVisible();

		f = itemblk2.addField("ITEM2_PROJECT_NO");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM3PROJECTNO: Project No");
		f.setDbName("PROJECT_NO");
		f.setDefaultNotVisible();

		f = itemblk2.addField("ITEM2_OBJECT_NO");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM3OBJECTNO: Object No");
		f.setDbName("OBJECT_NO");
		f.setDefaultNotVisible();

		f = itemblk2.addField("CODE_C");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBCODEC: Code C");
		f.setDefaultNotVisible();

		f = itemblk2.addField("CODE_D");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBCODED: Code D");
		f.setDefaultNotVisible();

		f = itemblk2.addField("CODE_G");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBCODEG: Code G");
		f.setDefaultNotVisible();

		f = itemblk2.addField("CODE_H");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBCODEH: Code H");
		f.setDefaultNotVisible();

		f = itemblk2.addField("CODE_I");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBCODEI: Code I");
		f.setDefaultNotVisible();

		f = itemblk2.addField("CODE_J");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBCODEJ: Code J");
		f.setDefaultNotVisible();

		f = itemblk2.addField("CDOCUMENTTEXT");
		f.setSize(14);
		f.setLabel("PCMWHISTORICALSEPARATERMBCDOCUMENTTEXT: Document Text");
		f.setFunction("''");
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = itemblk2.addField("REQUISITION_NO");
		f.setSize(15);
		f.setLabel("PCMWHISTORICALSEPARATERMBREQUISITIONNO: Requisition No");
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = itemblk2.addField("REQUISITION_LINE_NO");
		f.setSize(20);
		f.setLabel("PCMWHISTORICALSEPARATERMBREQUISITIONLINENO: Requisition Line No");
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = itemblk2.addField("REQUISITION_RELEASE_NO");
		f.setSize(22);
		f.setLabel("PCMWHISTORICALSEPARATERMBREQUISITIONRELEASENO: Requisition Release No");
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = itemblk2.addField("REQUISITIONVENDORNO");
		f.setSize(23);
		f.setLabel("PCMWHISTORICALSEPARATERMBREQUISITIONVENDORNO: Requisition Supplier No");
		f.setFunction("Purchase_Req_Util_API.Get_Line_Vendor_No (:REQUISITION_NO,:REQUISITION_LINE_NO,:REQUISITION_RELEASE_NO)");
		mgr.getASPField("REQUISITION_NO").setValidation("REQUISITIONVENDORNO");
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = itemblk2.addField("ITEM2_WO_NO","Number","#");
		f.setSize(11);
		f.setHidden();
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM3WONO: WO No");
		f.setDbName("WO_NO");

		f = itemblk2.addField("CATALOGDESC");
		f.setSize(11);
		f.setHidden();
		f.setLabel("PCMWHISTORICALSEPARATERMBCATALOGDESC: Description");
		if (mgr.isModuleInstalled("ORDER"))
		{
			f.setFunction("SALES_PART_API.Get_Catalog_Desc(:CONTRACT,:CATALOG_NO)");
			mgr.getASPField("CATALOG_NO").setValidation("CATALOGDESC");
		}
		else
			f.setFunction("''");

		f = itemblk2.addField("ITEM2_ROW_NO","Number");   
		f.setSize(11);
		f.setHidden();
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM3ROWNO: Row No");
		f.setDbName("ROW_NO");

		f = itemblk2.addField("ITEM2_CONTRACT");
		f.setSize(11);
		f.setHidden();
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM3CONTRACT: Site");
		f.setDbName("CONTRACT");
		f.setUpperCase();

		itemblk2.setView("WORK_ORDER_CODING");
		itemblk2.defineCommand("WORK_ORDER_CODING_API", "");
		itemset2 = itemblk2.getASPRowSet();

		itemlay2 = itemblk2.getASPBlockLayout();    
		itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);
		itemlay2.unsetAutoLayoutSelect();   

		itembar2 = mgr.newASPCommandBar(itemblk2);
		itembar2.setBorderLines(true,true);

		itembar2.defineCommand(itembar2.OKFIND, "okFindITEM2");
		itembar2.defineCommand(itembar2.COUNTFIND, "countFindITEM2");
		itembar2.enableCommand(itembar2.FIND);

		itemtbl2 = mgr.newASPTable(itemblk2);
		itemtbl2.setWrap();

		itemblk2.setMasterBlock(headblk); 

		//------------------------------------------------------------------------------------------------
		//-------------------------------------------- Crafts Tab  ---------------------------------------
		//------------------------------------------------------------------------------------------------

		itemblk3 = mgr.newASPBlock("ITEM3");

		f = itemblk3.addField("ITEM3_OBJID");
		f.setHidden();
		f.setDbName("OBJID");

		f = itemblk3.addField("ITEM3_OBJVERSION");
		f.setHidden();
		f.setDbName("OBJVERSION");

		f = itemblk3.addField("ITEM3_WO_NO","Number","#");
		f.setSize(11);
		f.setHidden();
		f.setDbName("WO_NO");

		f = itemblk3.addField("ROW_NO","Number");
		f.setSize(8);
		f.setLabel("PCMWHISTORICALSEPARATERMBOPRNO: Operation No");

		f = itemblk3.addField("DESCRIPTION");
		f.setSize(25);
		f.setLabel("PCMWHISTORICALSEPARATERMBDESCRIPTION: Description");

		f = itemblk3.addField("ITEM3_CONTRACT");
		f.setSize(8);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM3CONT: Site");
		f.setDbName("CONTRACT");

		f = itemblk3.addField("ITEM3_ORG_CODE");
		f.setSize(8);
		f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ITEM3_CONTRACT CONTRACT",600,450);
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM3ORGCODE: Maintenance Organization");
		f.setDbName("ORG_CODE");

		     //Bug ID 59224,start
                     f = itemblk3.addField("ITEM3_ORG_CODE_DESC");
                     f.setLabel("PCMWHISTORICALSEPARATERMBITEM3ORGCODEDESC: Maint. Org. Description");
                     f.setFunction("Organization_Api.Get_Description(:ITEM3_CONTRACT,:ITEM3_ORG_CODE)");
                     f.setReadOnly();
                     //Bug ID 59224,end

		f = itemblk3.addField("ROLE_CODE");
		f.setSize(10);
		f.setDynamicLOV("ROLE_TO_SITE_LOV","ITEM3_CONTRACT CONTRACT",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBROLECODE: Craft ID");

		      //Bug ID 59224,start
                      f = itemblk3.addField("ROLE_CODE_DESC");
                      f.setLabel("PCMWHISTORICALSEPARATERMBITEM3ROLECODEDESC: Craft Description");
                      f.setFunction("ROLE_API.Get_Description(:ROLE_CODE)");
                      f.setReadOnly();
                     //Bug ID 59224,end

		f = itemblk3.addField("SIGN");
		f.setSize(20);
		f.setDynamicLOV("EMPLOYEE",600,450);
		f.setLOVProperty("TITLE","PCMWHISTORICALSEPARATERMBLOVTITLE1: List of Signature");
		f.setLabel("PCMWHISTORICALSEPARATERMBSIGN: Executed By");

                f = itemblk3.addField("ITEM3_SIGN_ID");
                f.setDbName("SIGN_ID");
                f.setLabel("PCMWHISTORICALSEPARATERMBITEM3SIGNID: Employee ID");
                f.setReadOnly();

		f = itemblk3.addField("ITEM3_PLAN_MEN","Number");
		f.setSize(14);
		f.setLabel("PCMWHISTORICALSEPARATERMBPLANMEN: Planned Men");
		f.setDbName("PLAN_MEN");

		f = itemblk3.addField("ITEM3_PLAN_HRS","Number");
		f.setSize(14);
		f.setLabel("PCMWHISTORICALSEPARATERMBPLANHRS: Planned Hours");
		f.setDbName("PLAN_HRS");

                itemblk3.addField("TOTAL_MAN_HOURS","Number").
                setLabel("PCMWHISTORICALSEPARATERMBTOTALMANHRS: Total Man Hours").
                setFunction("PLAN_MEN*PLAN_HRS").
                setReadOnly();

                itemblk3.addField("ALLOCATED_HOURS","Number").
                setLabel("PCMWHISTORICALSEPARATERMBALLOCATEDHRS: Allocated Hours").
                setReadOnly();

                itemblk3.addField("TOTAL_ALLOCATED_HOURS","Number").
                setLabel("PCMWHISTORICALSEPARATERMBTOTALLOCHRS: Total Allocated Hours").
                setFunction("Work_Order_Role_API.Get_Total_Alloc(:ITEM3_WO_NO,:ROW_NO)").
                setReadOnly();

                itemblk3.addField("TOTAL_REMAINING_HOURS","Number").
                setLabel("PCMWHISTORICALSEPARATERMBTOTALREMAININGHRS: Total Remaining Hours").
                setFunction("(PLAN_MEN*PLAN_HRS) - Work_Order_Role_API.Get_Total_Alloc(:ITEM3_WO_NO,:ROW_NO)").
                setReadOnly();

                f = itemblk3.addField("TEAM_CONTRACT");
                f.setSize(7);
                f.setLabel("PCMWHISTORICALSEPARATERMBCONT: Team Site");
                f.setMaxLength(5);
                f.setUpperCase();

                f= itemblk3.addField("TEAM_ID");
                f.setSize(13);
                f.setMaxLength(20);
                f.setLabel("PCMWHISTORICALSEPARATERMBTID: Team ID");
                f.setUpperCase();

                f= itemblk3.addField("TEAMDESC");
                f.setFunction("Maint_Team_API.Get_Description(:TEAM_ID,:TEAM_CONTRACT)");
                f.setSize(40);
                f.setMaxLength(200);
                f.setReadOnly();
                f.setLabel("PCMWHISTORICALSEPARATERMBDESC: Description");
                
		f = itemblk3.addField("ITEM3_JOB_ID", "Number");
		f.setDbName("JOB_ID");
		f.setSize(8);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setDynamicLOV("WORK_ORDER_JOB", "ITEM3_WO_NO WO_NO");
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM3JOBID: Job Id");

		f = itemblk3.addField("DATE_FROM","Datetime");
		f.setSize(20);
		f.setLabel("PCMWHISTORICALSEPARATERMBDATEFROM: Date From");

		f = itemblk3.addField("DATE_TO","Datetime");
		f.setSize(20);
		f.setLabel("PCMWHISTORICALSEPARATERMBDATETO: Date To");

		f = itemblk3.addField("TOOLS");
		f.setSize(20);
		f.setLabel("PCMWHISTORICALSEPARATERMBTOOLS: Tools");

		f = itemblk3.addField("REMARK");
		f.setSize(29);
		f.setLabel("PCMWHISTORICALSEPARATERMBREMARK: Remark");

                f = itemblk3.addField("CBADDQUALIFICATION");
                f.setLabel("PCMWHISTORICALSEPARATERMBCBADDQUAL: Additional Qualifications");
                f.setFunction("Work_Order_Role_API.Check_Qualifications_Exist(:ITEM3_WO_NO,:ROW_NO)");
                f.setCheckBox("0,1");
                f.setReadOnly();
                
		itemblk3.setView("WORK_ORDER_ROLE");
		itemblk3.defineCommand("WORK_ORDER_ROLE_API", "");
		itemset3 = itemblk3.getASPRowSet();

		itembar3 = mgr.newASPCommandBar(itemblk3);
		itembar3.setBorderLines(true,true);

		itembar3.defineCommand(itembar3.OKFIND, "okFindITEM3");
		itembar3.defineCommand(itembar3.COUNTFIND, "countFindITEM3");
                itembar3.addCustomCommand("additionalQualifications",mgr.translate("PCMWHISTORICALSEPARATERMBADDQUAL: Additional Qualifications..."));
                
		itemblk3.setMasterBlock(headblk); 

		itemtbl3 = mgr.newASPTable(itemblk3);
		itemtbl3.setWrap();

		itemlay3 = itemblk3.getASPBlockLayout();    
		itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT); 
		itemlay3.unsetAutoLayoutSelect();   


                //------------------Employee Allocations on Operations tab------------------

                 //------------Allocation on OLperations - child of operations-------------

                itemblk10 = mgr.newASPBlock("ITEM10");

                itemblk10.addField("ITEM10_OBJID").
                setHidden().
                setDbName("OBJID");

                itemblk10.addField("ITEM10_OBJVERSION").
                setHidden().
                setDbName("OBJVERSION");

                itemblk10.addField("PARENT_ROW_NO","Number").
                setLabel("PCMWHISTORICALSEPARATERMBPOPERATIONNO: Parent Operation No").
                setHidden();

                itemblk10.addField("ITEM10_ROW_NO","Number").
                setLabel("PCMWHISTORICALSEPARATERMBITEM10ROWNO: Operation No").
                setDbName("ROW_NO").
                setHidden().
                setInsertable();
        
                itemblk10.addField("ALLOCATION_NO","Number").
                setLabel("PCMWHISTORICALSEPARATERMBALLOCNO: Allocation No");
                
                itemblk10.addField("ITEM10_DESCRIPTION").
                setDbName("DESCRIPTION").
                setLabel("PCMWHISTORICALSEPARATERMBALLOCDESC: Description");

                itemblk10.addField("ITEM10_SIGN").
                setSize(20).
                setLOV("../mscomw/MaintEmployeeLov.page","ITEM10_COMPANY COMPANY",600,450).
                setLOVProperty("TITLE","PCMWHISTORICALSEPARATERMBLOVTITLE5: List of Employee").
                setLabel("PCMWHISTORICALSEPARATERMBITEM10SIGN: Executed By").
                setDbName("SIGN").
                setUpperCase();
                
                itemblk10.addField("ITEM10_SIGN_ID").
                setSize(18).
                setLabel("PCMWHISTORICALSEPARATERMBITEM10SIGNID: Employee ID").
                setDbName("SIGN_ID").
                setUpperCase().
                setReadOnly();

                itemblk10.addField("ITEM10_ORG_CODE").
                setSize(8).
                setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ITEM10_CONTRACT CONTRACT",600,450).
                setLabel("PCMWHISTORICALSEPARATERMBITEM8ORGCODE: Maint.Org.").
                setUpperCase().
                setDbName("ORG_CODE");
                                      
		//Bug ID 59224,start
                itemblk10.addField("ITEM10_ORG_CODE_DESC").
                setLabel("HISORICALORGCODEDESC: Maint. Org. Description").
                setFunction("Organization_Api.Get_Description(:ITEM10_CONTRACT,:ITEM10_ORG_CODE)").
                setReadOnly();
               //Bug ID 59224,end     

                itemblk10.addField("ITEM10_CONTRACT").
                setSize(8).
                setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
                setLabel("PCMWHISTORICALSEPARATERMBITEM10CONTRACT: Maint.Org. Site").
                setUpperCase().
                setDbName("CONTRACT");
                
                itemblk10.addField("ITEM10_ROLE_CODE").
                setSize(10).
                setDynamicLOV("ROLE_TO_SITE_LOV","ITEM10_CONTRACT CONTRACT",600,445).
                setLabel("PCMWHISTORICALSEPARATERMBITEM10ROLECODE: Craft ID").
                setDbName("ROLE_CODE").
                setUpperCase();
                
                //Bug ID 59224,start
                itemblk10.addField("ITEM10_ROLE_CODE_DESC").
                setLabel("HISTORICAT2LIGHTROLECODEDESC: Craft Description").
                setFunction("ROLE_API.Get_Description(:ITEM10_ROLE_CODE)").
                setReadOnly();
               //Bug ID 59224,end  

                itemblk10.addField("ITEM8_ALLOCATED_HOURS","Number").
                setDbName("ALLOCATED_HOURS").
                setLabel("PCMWHISTORICALSEPARATERMBALLOCHRS: Allocated Hours");
                
                itemblk10.addField("ITEM10_TEAM_CONTRACT").
                setSize(7).
                setDynamicLOV("USER_ALLOWED_SITE_LOV","ITEM10_COMPANY COMPANY",600,450).
                setLabel("PCMWHISTORICALSEPARATERMBITEM10TCONT: Team Site").
                setMaxLength(5).
                setDbName("TEAM_CONTRACT").
                setInsertable();
                
                itemblk10.addField("ITEM10_TEAM_ID").
                setSize(13).
                setDynamicLOV("MAINT_TEAM","TEAM_CONTRACT",600,450).
                setMaxLength(20).
                setDbName("TEAM_ID").
                setInsertable().
                setLabel("PCMWHISTORICALSEPARATERMBITEM10TEAMID: Team ID").
                setUpperCase();

                itemblk10.addField("ITEM10_TEAMDESC").
                setFunction("Maint_Team_API.Get_Description(:ITEM10_TEAM_ID,:ITEM10_TEAM_CONTRACT)").
                setSize(40).
                setMaxLength(200).
                setReadOnly().
                setLabel("PCMWHISTORICALSEPARATERMBITEM10DESC: Description");

                itemblk10.addField("ITEM10_DATE_FROM","Datetime").
                setSize(22).
                setDbName("DATE_FROM").
                setLabel("PPCMWHISTORICALSEPARATERMBITEM10DFROM: Date from").
                setDefaultNotVisible();
        
                itemblk10.addField("ITEM10_DATE_TO","Datetime").
                setSize(22).
                setDbName("DATE_TO").
                setLabel("PCMWHISTORICALSEPARATERMBITEM8DTO: Date to").
                setDefaultNotVisible();
        
                itemblk10.addField("ITEM10_WO_NO","Number","#").
                setMandatory().
                setHidden().
                setDbName("WO_NO");
        
                itemblk10.addField("ITEM10_COMPANY").
                setSize(18).
                setHidden().
                setLabel("PCMWHISTORICALSEPARATERMBITEM10COMPANY: Company").
                setUpperCase().
                setDbName("COMPANY");
                
                itemblk10.setView("WORK_ORDER_ROLE");
                itemset10 = itemblk10.getASPRowSet();

                itemblk10.setMasterBlock(itemblk3);

                itembar10 = mgr.newASPCommandBar(itemblk10);
                itembar10.enableCommand(itembar10.FIND);
                itembar10.defineCommand(itembar10.COUNTFIND,"countFindITEM10");
                itembar10.defineCommand(itembar10.OKFIND,"okFindITEM10");
                
                itemtbl10 = mgr.newASPTable(itemblk10);
                itemtbl10.setWrap();
                
                itemlay10 = itemblk10.getASPBlockLayout();
                itemlay10.setDefaultLayoutMode(itemlay10.MULTIROW_LAYOUT);
                itemlay10.setDialogColumns(3);  
                
		//------------------------------------------------------------------------------------------------
		//------------------------------------------ Time Report Tab  ------------------------------------
		//------------------------------------------------------------------------------------------------

		itemblk4 = mgr.newASPBlock("ITEM4");

		f = itemblk4.addField("ITEM4_OBJID");
		f.setHidden();
		f.setDbName("OBJID");

		f = itemblk4.addField("ITEM4_OBJVERSION");
		f.setHidden();
		f.setDbName("OBJVERSION");

		f = itemblk4.addField("CRE_DATE","Date");
		f.setSize(12);
		f.setLabel("PCMWHISTORICALSEPARATERMBCREDATE: Creation Date");
		f.setDefaultNotVisible();

		f = itemblk4.addField("ITEM4_COMPANY");
		f.setHidden();
		f.setDbName("COMPANY");

		f = itemblk4.addField("EMP_NO");
		f.setSize(10);
		f.setDynamicLOV("EMPLOYEE_NO","ITEM4_COMPANY COMPANY",600,445); 
		f.setLabel("PCMWHISTORICALSEPARATERMBEMPNO: Employee ID");

		f = itemblk4.addField("EMP_SIGNATURE");
		f.setSize(8);
		f.setDynamicLOV("EMPLOYEE_LOV","ITEM4_COMPANY COMPANY",600,445); 
		f.setLabel("PCMWHISTORICALSEPARATERMBEMPSIGNATURE: Signature");

		f = itemblk4.addField("ITEM4_NAME");
		f.setSize(16);
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM4NAME: Name");
		f.setFunction("Person_Info_API.Get_Name(:EMP_SIGNATURE)");
		//f.setDbName("NAME");   
		f.setDefaultNotVisible();

		f = itemblk4.addField("ITEM4_ORG_CODE");
		f.setSize(11);
		f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ITEM4_CONTRACT CONTRACT",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM4ORGCODE: Maintenance Organization");
		f.setDbName("ORG_CODE");
		f.setLOVProperty("TITLE",mgr.translate("PCMWHISTORICALSEPARATERMBORCO: List of Maintenance Organization"));

	      //Bug ID 59224,start
                     f = itemblk4.addField("ITEM0_ORG_CODE_DESC");
                     f.setLabel("PCMWHISTORICALSEPARATERMBITEM4ORGCODEDESC: Maint. Org. Description");
                     f.setFunction("Organization_Api.Get_Description(:ITEM4_CONTRACT,:ITEM4_ORG_CODE)");
                     f.setReadOnly();
                     //Bug ID 59224,end

                     f = itemblk4.addField("ITEM4_CONTRACT");
		f.setSize(11);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM4CONTRACT: Site");
		f.setDbName("CONTRACT");

		f = itemblk4.addField("ITEM4ROLE_CODE");
		f.setSize(10);
		f.setDynamicLOV("ROLE_TO_SITE_LOV","ITEM4_CONTRACT CONTRACT",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM4ROLECODE: Craft");
		f.setLOVProperty("TITLE",mgr.translate("PCMWHISTORICALSEPARATERMBROCO: List of Craft"));
		f.setDbName("ROLE_CODE");

		      //Bug ID 59224,start
                      f = itemblk4.addField("ITEM4ROLE_CODE_DESC");
		      f.setLabel("PCMWHISTORICALSEPARATERMBITEM4ROLECODEDESC: Craft Description");
		      f.setFunction("ROLE_API.Get_Description(:ITEM4ROLE_CODE)");
		      f.setReadOnly();
		      //Bug ID 59224,end

		f = itemblk4.addField("ITEM4_CATALOG_NO");
		f.setSize(17);
		f.setDynamicLOV("SALES_PART_SERVICE_LOV","ITEM4_CONTRACT CONTRACT",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBCATALOGNO: Sales Part Number");
		f.setLOVProperty("TITLE",mgr.translate("PCMWHISTORICALSEPARATERMBCANO: List of Sales Part Number"));
		f.setDbName("CATALOG_NO");

		f = itemblk4.addField("ITEM4SALESPARTCATALOGDESC");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBSALESPARTCATADESC4: Description");
		if (mgr.isModuleInstalled("ORDER"))
			f.setFunction("SALES_PART_API.Get_Catalog_Desc(:ITEM4_CONTRACT,:CATALOG_NO)");
		else
			f.setFunction("''");
		f.setDefaultNotVisible();

		f = itemblk4.addField("PRICE_LIST_NO");
		f.setSize(13);
		f.setDynamicLOV("SALES_PRICE_LIST","ITEM4_CONTRACT CONTRACT",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBPRICELISTNO: Price List No");
		f.setLOVProperty("TITLE",mgr.translate("PCMWHISTORICALSEPARATERMBPRLINO: List of Price List No"));
		f.setDefaultNotVisible();

		f = itemblk4.addField("QTY","Number");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMHOURS: Hours");

		f = itemblk4.addField("SALESPARTCOST","Number","#.##");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBSALESPARTCOST: Cost");
		if (mgr.isModuleInstalled("ORDER"))
			f.setFunction("SALES_PART_API.Get_Cost(:ITEM4_CONTRACT,:ITEM4_CATALOG_NO)");
		else
			f.setFunction("''");
		f.setDefaultNotVisible();

		f = itemblk4.addField("AMOUNT","Number","#.##");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBAMOUNT: Cost Amount");

		f = itemblk4.addField("ITEM4_LIST_PRICE","Number");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBLISTSALESPRICE: Sales Price");
		f.setDbName("LIST_PRICE");

		f = itemblk4.addField("DISCOUNT","Number");
		f.setFunction("''");
		f.setHidden();

		f = itemblk4.addField("AMOUNTSALES","Number","#.##");
		f.setSize(17);
		f.setLabel("PCMWHISTORICALSEPARATERMBAMOUNTSALES: Price Amount");
		f.setFunction("(LIST_PRICE*QTY)");

		f = itemblk4.addField("CMNT");
		f.setSize(25);
		f.setLabel("PCMWHISTORICALSEPARATERMBCMNT: Comment");
		f.setDefaultNotVisible();

		f = itemblk4.addField("ITEM4_WO_NO","Number","#");
		f.setHidden();
		f.setDbName("WO_NO");

		f = itemblk4.addField("ITEM4_ROW_NO","Number");
		f.setDbName("ROW_NO");
		f.setHidden();

		f = itemblk4.addField("ITEM4_WORK_ORDER_BOOK_STATUS");
		f.setDbName("WORK_ORDER_BOOK_STATUS");
		f.setHidden();

		f = itemblk4.addField("WORK_ORDER_COST_TYPE");
		f.setHidden();

		f = itemblk4.addField("WORK_ORDER_ACCOUNT_TYPE");
		f.setHidden();

		f = itemblk4.addField("AGREEMENT_PRICE_FLAG","Number");
		f.setHidden();

		itemblk4.setView("WORK_ORDER_CODING");
		itemblk4.defineCommand("WORK_ORDER_CODING_API", "");
		itemset4 = itemblk4.getASPRowSet();

		itembar4 = mgr.newASPCommandBar(itemblk4);
		itembar4.setBorderLines(true,true);

		itembar4.defineCommand(itembar4.OKFIND, "okFindITEM4");
		itembar4.defineCommand(itembar4.COUNTFIND, "countFindITEM4");

		itemblk4.setMasterBlock(headblk); 

		itemtbl4 = mgr.newASPTable(itemblk4);
		itemtbl4.setWrap();

		itemlay4 = itemblk4.getASPBlockLayout();    
		itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT); 
		itemlay4.unsetAutoLayoutSelect();   

		//------------------------------------------------------------------------------------------------
		//------------------------------------ Material Tab (Upper Block) --------------------------------
		//------------------------------------------------------------------------------------------------

		itemblk5 = mgr.newASPBlock("ITEM5");

		f = itemblk5.addField("ITEM5_OBJID");
		f.setDbName("OBJID");
		f.setHidden();

		f = itemblk5.addField("ITEM5_OBJVERSION");
		f.setHidden();
		f.setDbName("OBJVERSION");

		f = itemblk5.addField("OBJSTATE");
		f.setHidden();

		f = itemblk5.addField("OBJEVENTS");
		f.setHidden();

		f = itemblk5.addField("MAINT_MATERIAL_ORDER_NO");
		f.setSize(12);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBMAINTMATERIALORDERNO: Order No");

		f = itemblk5.addField("ITEM5_WO_NO");
		f.setSize(11);
		f.setMaxLength(8);
		f.setReadOnly();
		f.setDbName("WO_NO");
		f.setDynamicLOV("ACTIVE_WO_NOT_REPORTED",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBWO_NO: Workorder No");

		f = itemblk5.addField("MCHCODE");
		f.setSize(13);
		f.setReadOnly();
		f.setMaxLength(2000);
		f.setHidden();
		f.setLabel("PCMWHISTORICALSEPARATERMBMCHCODE: Object ID");
		f.setFunction("WORK_ORDER_API.Get_Mch_Code(:ITEM5_WO_NO)");
		f.setUpperCase();

		f = itemblk5.addField("ITEM5_SIGNATURE_ID");
		f.setHidden();
		f.setDbName("SIGNATURE_ID");

		f = itemblk5.addField("ITEM5_SIGNATURE");
		f.setSize(8);
		f.setDbName("SIGNATURE");
		f.setMaxLength(20);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM5SIGNATURE: Signature");
		f.setUpperCase();

		f = itemblk5.addField("SIGNATURENAME");
		f.setSize(15);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setMaxLength(2000);
		f.setFunction("EMPLOYEE_API.Get_Employee_Info(Site_API.Get_Company(:ITEM5_CONTRACT),:ITEM5_SIGNATURE_ID)");

		f = itemblk5.addField("ITEM5_CONTRACT");
		f.setSize(10);
		f.setReadOnly();
		f.setMaxLength(5);
		f.setDbName("CONTRACT");
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM2_CONTRACT: Site");
		f.setUpperCase();

		f = itemblk5.addField("ENTERED","Date");
		f.setSize(15);
		f.setLabel("PCMWHISTORICALSEPARATERMBENTERED: Entered");

		f = itemblk5.addField("INT_DESTINATION_ID");
		f.setSize(8);
		f.setMaxLength(30);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBINT_DESTINATION_ID: Int Destination");

		f = itemblk5.addField("INT_DESTINATION_DESC");
		f.setSize(15);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setMaxLength(2000);

		f = itemblk5.addField("DUE_DATE","Date");
		f.setSize(15);
		f.setLabel("PCMWHISTORICALSEPARATERMBDUE_DATE: Due Date");

		f = itemblk5.addField("STATE");
		f.setSize(10);
		f.setMaxLength(20);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBSTATE: Status");

		f = itemblk5.addField("SNOTETEXT");
		f.setSize(15);
		f.setMaxLength(2000);
		f.setHidden();
		f.setLabel("PCMWHISTORICALSEPARATERMBSNOTETEXT: Total Value");
		f.setFunction("''");

		itemblk5.setView("MAINT_MATERIAL_REQUISITION");
		itemblk5.defineCommand("MAINT_MATERIAL_REQUISITION_API", "");
		itemset5 = itemblk5.getASPRowSet();
		itemblk5.setMasterBlock(headblk);
		itemtbl5 = mgr.newASPTable(itemblk5);

		itembar5 = mgr.newASPCommandBar(itemblk5);
		itembar5.enableCommand(itembar5.FIND);
		itembar5.defineCommand(itembar5.COUNTFIND, "countFindITEM5");
		itembar5.defineCommand(itembar5.OKFIND, "okFindITEM5");

		itemtbl5.setWrap();

		itemlay5 = itemblk5.getASPBlockLayout();
		itemlay5.setDefaultLayoutMode(itemlay5.MULTIROW_LAYOUT);
		itemlay5.setSimple("INT_DESTINATION_DESC");
		itemlay5.setSimple("SIGNATURENAME");   

		//------------------------------------------------------------------------------------------------
		//----------------------------------- Material Tab (Lower Block) ---------------------------------
		//------------------------------------------------------------------------------------------------

		itemblk = mgr.newASPBlock("ITEM");

		f = itemblk.addField("ITEM_OBJID");
		f.setHidden(   );
		f.setDbName("OBJID");

		f = itemblk.addField("ITEM_OBJVERSION");
		f.setHidden();
		f.setDbName("OBJVERSION");

		f = itemblk.addField("LINE_ITEM_NO","Number");
		f.setSize(8);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBLINE_ITEM_NO: Line No");

		f = itemblk.addField("PART_NO");
		f.setSize(14);
		f.setReadOnly();
		f.setHyperlink("../invenw/InventoryPart.page","PART_NO","NEWWIN");
		f.setInsertable();
		f.setDynamicLOV("INVENTORY_PART_WO_LOV","SPARE_CONTRACT",600,445);
		f.setMandatory();
		f.setMaxLength(25);
		f.setLabel("PCMWHISTORICALSEPARATERMBPART_NO: Part No");
		f.setUpperCase();

		f = itemblk.addField("SPAREDESCRIPTION");
		f.setSize(20);
		f.setMaxLength(2000);
		f.setReadOnly();
		f.setInsertable();
		f.setLabel("PCMWHISTORICALSEPARATERMBSPAREDESCRIPTION: Part Description");
		f.setFunction("INVENTORY_PART_API.Get_Description(:SPARE_CONTRACT,:PART_NO)");

		f = itemblk.addField("SPARE_CONTRACT");
		f.setSize(11);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
		f.setReadOnly();
		f.setMaxLength(5);
		f.setInsertable();
		f.setDefaultNotVisible();
		f.setMandatory();
		f.setLabel("PCMWHISTORICALSEPARATERMBSPARE_CONTRACT: Site");
		f.setUpperCase();

		f = itemblk.addField("CONDITION_CODE");
		f.setSize(10);
		f.setReadOnly();
		f.setLabel("PCMWSEPARATESTANDARDJOB2CONDITIONCODE: Condition Code");

		f = itemblk.addField("CONDITION_CODE_DESC");
		f.setSize(20);
		f.setReadOnly();
		f.setLabel("PCMWSEPARATESTANDARDJOB2CONDITIONCODEDESC: Condition Code Description");
		f.setFunction("CONDITION_CODE_API.Get_Description(:CONDITION_CODE)");

		f = itemblk.addField("PART_OWNERSHIP");
		f.setSize(25);
		f.setReadOnly();
		f.setSelectBox();
		f.enumerateValues("PART_OWNERSHIP_API");
		f.setLabel("PCMWHISTORICALSEPARATERMBPARTOWNERSHIP: Ownership");
		f.setCustomValidation("PART_OWNERSHIP","PART_OWNERSHIP_DB");

		f = itemblk.addField("PART_OWNERSHIP_DB");
		f.setSize(20);
		f.setHidden();

		f = itemblk.addField("OWNER");
		f.setSize(15);
		f.setMaxLength(20);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBPARTOWNER: Owner");
		f.setCustomValidation("OWNER,PART_OWNERSHIP_DB","OWNER_NAME,WO_CUST");
		f.setDynamicLOV("CUSTOMER_INFO");
		f.setUpperCase();

		f = itemblk.addField("WO_CUST");
		f.setSize(20);
		f.setHidden();
		f.setFunction("ACTIVE_SEPARATE_API.Get_Customer_No(:ITEM5_WO_NO)");


		f = itemblk.addField("OWNER_NAME");
		f.setSize(20);
		f.setMaxLength(100);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBPARTOWNERNAME: Owner Name");
		f.setFunction("CUSTOMER_INFO_API.Get_Name(:OWNER)");

		f = itemblk.addField("HASSPARESTRUCTURE");
		f.setSize(8);
		f.setReadOnly();
		f.setMaxLength(2000);
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBHASSPARESTRUCTURE: Structure");
		f.setFunction("Equipment_Spare_Structure_API.Has_Spare_Structure_Boolean(:PART_NO,:SPARE_CONTRACT)");

		f = itemblk.addField("ITEM_JOB_ID", "Number");
		f.setDbName("JOB_ID");
		f.setSize(8);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setDynamicLOV("WORK_ORDER_JOB", "ITEM_WO_NO WO_NO");
		f.setLabel("PCMWHISTORICALSEPARATERMBITEMJOBID: Job Id");

		f = itemblk.addField("DIMQTY");
		f.setSize(11);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setMaxLength(2000);
		f.setLabel("PCMWHISTORICALSEPARATERMBDIMQTY: Dimension/Quality");
		f.setFunction("INVENTORY_PART_API.Get_Dim_Quality(:SPARE_CONTRACT,:PART_NO)");

		f = itemblk.addField("TYPEDESIGN");
		f.setSize(15);
		f.setReadOnly();
		f.setMaxLength(2000);
		f.setLabel("PCMWHISTORICALSEPARATERMBTYPEDESIGN: Type Designation");
		f.setFunction("INVENTORY_PART_API.Get_Type_Designation(:SPARE_CONTRACT,:PART_NO)");

		f = itemblk.addField("DATE_REQUIRED","Date");
		f.setSize(13);
		f.setLabel("PCMWHISTORICALSEPARATERMBDATE_REQUIRED: Date Required");

		f = itemblk.addField("PLAN_QTY","Number");
		f.setSize(14);
		f.setMandatory();
		f.setLabel("PCMWHISTORICALSEPARATERMBPLAN_QTY: Quantity Required");

		f = itemblk.addField("ITEM_QTY","Number");
		f.setSize(13);
		f.setDbName("QTY");
		f.setInsertable();
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBQTYISS: Quantity Issued");

		f = itemblk.addField("QTY_SHORT","Number");
		f.setSize(11);
		f.setMandatory();
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setHidden();
		f.setLabel("PCMWHISTORICALSEPARATERMBQTY_SHORT: Quantity Short");

		f = itemblk.addField("QTYONHAND","Number");
		f.setSize(17);
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBQTYONHAND: Quantity on Hand");
		//f.setFunction("Inventory_Part_In_Stock_API.Get_Inventory_Quantity(:SPARE_CONTRACT,:PART_NO,NULL,'ONHAND',NULL,NULL,:PART_OWNERSHIP_DB,NULL,NULL,NULL,:OWNER,NULL,'PICKING','F','PALLET','DEEP','BUFFER','DELIVERY','SHIPMENT','MANUFACTURING',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,:CONDITION_CODE)");
		if (mgr.isModuleInstalled("INVENT"))
		    f.setFunction("Maint_Material_Req_Line_Api.Get_Qty_On_Hand(:ITEM0_MAINT_MATERIAL_ORDER_NO,:LINE_ITEM_NO)");
		else
                    f.setFunction("''");


		f = itemblk.addField("QTY_ASSIGNED","Number");
		f.setSize(11);
		f.setMandatory();
		f.setLabel("PCMWHISTORICALSEPARATERMBQTY_ASSIGNED: Quantity Assigned");

		f = itemblk.addField("QTY_RETURNED","Number");
		f.setSize(11);
		f.setMandatory();
		f.setDefaultNotVisible();
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBQTY_RETURNED: Quantity Returned");

		f = itemblk.addField("UNITMEAS");
		f.setSize(11);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setMaxLength(2000);
		f.setLabel("PCMWHISTORICALSEPARATERMBUNITMEAS: Unit");
		f.setFunction("Purchase_Part_Supplier_API.Get_Unit_Meas(:SPARE_CONTRACT,:PART_NO)");

		f = itemblk.addField("CATALOG_CONTRACT");
		f.setSize(10);
		f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
		f.setMaxLength(5);
		f.setDefaultNotVisible();
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBCATALOG_CONTRACT: Sales Part Site");
		f.setUpperCase();

		f = itemblk.addField("ITEM_CATALOG_NO");
		f.setSize(9);
		f.setDbName("CATALOG_NO");
		f.setMaxLength(25);
		f.setDefaultNotVisible();
		f.setDynamicLOV("SALES_PART_ACTIVE_LOV","CATALOG_CONTRACT",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM_CATALOG_NO: Sales Part Number");
		f.setUpperCase();

		f = itemblk.addField("ITEM_CATALOGDESC");
		f.setSize(17);
		f.setMaxLength(2000);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBITEMCATALOGDESC: Sales Part Description");
		if (mgr.isModuleInstalled("ORDER"))
			f.setFunction("SALES_PART_API.Get_Catalog_Desc(:CATALOG_CONTRACT,:ITEM_CATALOG_NO)");
		else
			f.setFunction("''");

		f = itemblk.addField("ITEM_PRICE_LIST_NO");
		f.setSize(10);
		f.setMaxLength(10);
		f.setDefaultNotVisible();
		f.setDynamicLOV("SALES_PRICE_LIST",600,445);
		f.setLabel("PCMWHISTORICALSEPARATERMBPRICE_LIST_NO: Price List No");
		f.setUpperCase();
		f.setDbName("PRICE_LIST_NO");

		f = itemblk.addField("ITEM_LIST_PRICE","Number");
		f.setSize(9);
		f.setDefaultNotVisible();
		f.setDbName("LIST_PRICE");
		f.setLabel("PCMWHISTORICALSEPARATERMBLIST_PRICE: Sales Price");

		f = itemblk.addField("ITEM_DISCOUNT","Number");
		f.setSize(14);
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBDISCOUNT: Discount %");
		f.setDbName("DISCOUNT");

		f = itemblk.addField("SALESPRICEAMOUNT", "Number");
		f.setSize(14);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBSALESPRICEAMOUNT: Price Amount");
		f.setFunction("''");

		f = itemblk.addField("COST", "Number");
		f.setSize(11);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBCOST: Cost");
		f.setFunction("''");

		f = itemblk.addField("AMOUNTCOST", "Number");
		f.setSize(11);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setMaxLength(15);
		f.setLabel("PCMWHISTORICALSEPARATERMBAMOUNTCOST: Cost Amount");
		f.setFunction("''");

		f = itemblk.addField("SCODEA");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBSCODEA: Account");
		f.setFunction("''");
		f.setDefaultNotVisible();
		f.setReadOnly();
		f.setMaxLength(10);

		f = itemblk.addField("SCODEB");
		f.setSize(11);
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBSCODEB: Cost Center");
		f.setFunction("''");
		f.setReadOnly();
		f.setMaxLength(10);

		f = itemblk.addField("SCODEF");
		f.setSize(11);
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBSCODEF: Project No");
		f.setFunction("''");
		f.setReadOnly();
		f.setMaxLength(10);

		f = itemblk.addField("SCODEE");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBSCODEE: Object No");
		f.setFunction("''");
		f.setDefaultNotVisible();
		f.setReadOnly();
		f.setMaxLength(10);

		f = itemblk.addField("SCODEC");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBSCODEC: Code C");
		f.setFunction("''");
		f.setDefaultNotVisible();
		f.setReadOnly();
		f.setMaxLength(10);

		f = itemblk.addField("SCODED");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBSCODED: Code D");
		f.setFunction("''");
		f.setDefaultNotVisible();
		f.setReadOnly();
		f.setMaxLength(10);

		f = itemblk.addField("SCODEG");
		f.setSize(11);
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBSCODEG: Code G");
		f.setFunction("''");
		f.setReadOnly();
		f.setMaxLength(10);

		f = itemblk.addField("SCODEH");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBSCODEH: Code H");
		f.setFunction("''");
		f.setDefaultNotVisible();
		f.setReadOnly();
		f.setMaxLength(10);

		f = itemblk.addField("SCODEI");
		f.setSize(11);
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBSCODEI: Code I");
		f.setFunction("''");
		f.setReadOnly();
		f.setMaxLength(10);

		f = itemblk.addField("SCODEJ");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBSCODEJ: Code J");
		f.setFunction("''");
		f.setDefaultNotVisible();
		f.setReadOnly();
		f.setMaxLength(10);

		f = itemblk.addField("ITEM_WO_NO","Number","#");
		f.setSize(17);
		f.setMandatory();
		f.setMaxLength(8);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setHidden();
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM_WO_NO: Work Order No");
		f.setDbName("WO_NO");

		f = itemblk.addField("PLAN_LINE_NO","Number");
		f.setSize(17);
		f.setReadOnly();
		f.setHidden();
		f.setDefaultNotVisible();
		f.setLabel("PCMWHISTORICALSEPARATERMBPLAN_LINE_NO: Plan Line No");

		f = itemblk.addField("ITEM0_MAINT_MATERIAL_ORDER_NO","Number");
		f.setSize(17);
		f.setHidden();
		f.setDefaultNotVisible();
		f.setReadOnly();
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM0_MAINT_MATERIAL_ORDER_NO: Mat Req Order No");
		f.setDbName("MAINT_MATERIAL_ORDER_NO");

		itemblk.setView("MAINT_MATERIAL_REQ_LINE");
		itemblk.defineCommand("MAINT_MATERIAL_REQ_LINE_API", "");
		itemset = itemblk.getASPRowSet();

		itemblk.setMasterBlock(itemblk5);

		itembar = mgr.newASPCommandBar(itemblk);
		itembar.enableCommand(itembar.FIND);
		itembar.defineCommand(itembar.COUNTFIND, "countFindITEM");
		itembar.defineCommand(itembar.OKFIND, "okFindITEM");

		itemtbl = mgr.newASPTable(itemblk);
		itemtbl.setWrap();

		itemlay = itemblk.getASPBlockLayout();
		itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);

		if (mgr.isModuleInstalled("VIM"))
		{
			//------------------------------------------------------------------------------------------------
			//----------------------------------------- Maint Task Tab  --------------------------------------
			//------------------------------------------------------------------------------------------------

			itemblk6 = mgr.newASPBlock("ITEM6");

			f = itemblk6.addField("ITEM6_OBJID");
			f.setHidden();
			f.setDbName("OBJID");

			f = itemblk6.addField("ITEM6_OBJVERSION");
			f.setHidden();
			f.setDbName("OBJVERSION");

			f = itemblk6.addField("ITEM6_WO_NO","Number","#");
			f.setHidden();
			f.setDbName("WO_NO");

			f = itemblk6.addField("TASK_NO");
			f.setSize(25);
			f.setLabel("PCMWHISTORICALSEPARATERMBTASKNO: Task Number");

			f = itemblk6.addField("ITEM6_ORDER_NO");
			f.setSize(25);
			f.setDbName("ORDER_NO");
			f.setLabel("PCMWHISTORICALSEPARATERMB6ORDERNO: Order Number");

			f = itemblk6.addField("LOCATION_CODE");
			f.setSize(25);
			f.setLabel("PCMWHISTORICALSEPARATERMBLOCATIONCODE: Location Code");

			f = itemblk6.addField("POS");
			f.setSize(25);
			f.setLabel("PCMWHISTORICALSEPARATERMBPOS: Position");

			f = itemblk6.addField("VEHICLE_ID");
			f.setSize(30);
			f.setLabel("PCMWHISTORICALSEPARATERMBVEHIID: Vehicle ID");

			f = itemblk6.addField("PRODUCT_NO");
			f.setSize(20);
			f.setLabel("PCMWHISTORICALSEPARATERMBPRODUCTNO: Product Number");

			f = itemblk6.addField("PRODUCT_NO_DESC");
			f.setSize(35);
			f.setDefaultNotVisible();
			f.setLabel("PCMWHISTORICALSEPARATERMBPRODNODESC: Product Number Desc");

			f = itemblk6.addField("MODEL_NO");
			f.setSize(20);
			f.setLabel("PCMWHISTORICALSEPARATERMBMODELNO: Model Number");

			f = itemblk6.addField("MODEL_NO_DESC");
			f.setSize(35);
			f.setDefaultNotVisible();
			f.setLabel("PCMWHISTORICALSEPARATERMBMODNODESC: Model Number Desc");

			f = itemblk6.addField("SYMPTOM");
			f.setSize(20);
			f.setLabel("PCMWHISTORICALSEPARATERMBSYMPTOM: Symptom");

			f = itemblk6.addField("SYMPTOM_DESC");
			f.setDefaultNotVisible();
			f.setSize(35);
			f.setLabel("PCMWHISTORICALSEPARATERMBSYMPTOMDESC: Symptom Desc");

			f = itemblk6.addField("FAULT_MODE");
			f.setSize(20);
			f.setLabel("PCMWHISTORICALSEPARATERMBFAULTMODE: Fault Mode");

			f = itemblk6.addField("FAULT_MODE_DESC");
			f.setSize(35);      
			f.setDefaultNotVisible();
			f.setLabel("PCMWHISTORICALSEPARATERMBFAULTMODEDESC: Fault Mode Desc");

			f = itemblk6.addField("FAULT_CODE");
			f.setSize(20);
			f.setLabel("PCMWHISTORICALSEPARATERMBFAULTCODE: Fault Code");

			f = itemblk6.addField("FAULT_CODE_DESC");
			f.setSize(35);
			f.setDefaultNotVisible();
			f.setLabel("PCMWHISTORICALSEPARATERMBFAULTCODEDESC: Fault Code Desc");

			f = itemblk6.addField("FAULT_WARRANTY");
			f.setDefaultNotVisible();
			f.setLabel("PCMWHISTORICALSEPARATERMBFAULTWAR: Fault Warranty");
			f.setCheckBox("FALSE,TRUE");

			itemblk6.setView("WORK_ORDER_FROM_VIM_HIST");
			itemblk6.defineCommand("WORK_ORDER_FROM_VIM_HIST_API", "");
			itemset6 = itemblk6.getASPRowSet();

			itembar6 = mgr.newASPCommandBar(itemblk6);
			itembar6.setBorderLines(true,true);

			itembar6.defineCommand(itembar6.OKFIND, "okFindITEM6");
			itembar6.defineCommand(itembar6.COUNTFIND, "countFindITEM6");

			itemblk6.setMasterBlock(headblk); 

			itemtbl6 = mgr.newASPTable(itemblk6);
			itemtbl6.setWrap();

			itemlay6 = itemblk6.getASPBlockLayout();    
			itemlay6.setDefaultLayoutMode(itemlay6.MULTIROW_LAYOUT); 
			itemlay6.unsetAutoLayoutSelect();

			itemlay6.setSimple("PRODUCT_NO_DESC");
			itemlay6.setSimple("MODEL_NO_DESC");
			itemlay6.setSimple("SYMPTOM_DESC");   
			itemlay6.setSimple("FAULT_MODE_DESC");   
			itemlay6.setSimple("FAULT_CODE_DESC");

			itemlay6.defineGroup(mgr.translate("PCMWHISTORICALSEPARATERMB6GRPLABEL1: Task Information"),"TASK_NO,ITEM6_ORDER_NO,LOCATION_CODE,POS,VEHICAL_ID",true,true);
			itemlay6.defineGroup(mgr.translate("PCMWHISTORICALSEPARATERMB6GRPLABEL2: Fault Information"),"PRODUCT_NO,PRODUCT_NO_DESC,MODEL_NO,MODEL_NO_DESC,SYMPTOM,SYMPTOM_DESC,FAULT_MODE,FAULT_MODE_DESC,FAULT_CODE,FAULT_CODE_DESC,FAULT_WARRANTY",true,true); 

			//------------------------------------------------------------------------------------------------
			//---------------------------------- Maint Task Tab (Oper Param Block) ---------------------------
			//------------------------------------------------------------------------------------------------

			itemblk8 = mgr.newASPBlock("ITEM8");

			itemblk8.addField("ITEM8_OBJID").
			setHidden().
			setDbName("OBJID");

			itemblk8.addField("ITEM8_OBJVERSION").
			setHidden().
			setDbName("OBJVERSION");

			itemblk8.addField("ITEM8_WO_NO","Number").
			setDbName("WO_NO").
			setHidden();

			itemblk8.addField("ITEM8_TASK_NO").
			setDbName("TASK_NO").
			setHidden();

			itemblk8.addField("OPER_PARAM").
			setSize(25).
			setLabel("PCMWHISTORICALSEPARATERMB8OPERPARAM: Oper Param").
			setMandatory().
			setInsertable().
			setUpperCase().
			setMaxLength(30).
			setReadOnly();

			itemblk8.addField("OPER_PARAM_DESC").
			setSize(25).
			setLabel("PCMWHISTORICALSEPARATERMB8OPERPARAMDESC: Oper Param Description").
			setMaxLength(50).
			setReadOnly();

			itemblk8.addField("VALUE_OH","Number").
			setSize(25).
			setLabel("PCMWHISTORICALSEPARATERMB8VALUEOH: Value After Overhaul").
			setInsertable(); 

			itemblk8.addField("VALUE_TOTAL","Number").
			setSize(25).
			setLabel("PCMWHISTORICALSEPARATERMB8VALUETOT: Value Total").
			setInsertable();

			itemblk8.setView("WORK_ORDER_VIM_PARAM_HIST");
			itemblk8.defineCommand("WORK_ORDER_VIM_PARAM_HIST_API", "");
			itemblk8.setTitle(mgr.translate("PCMWHISTORICALSEPARATERMBOPERPARAMBLK: Oper Param"));

			itemset8 = itemblk8.getASPRowSet();

			itembar8 = mgr.newASPCommandBar(itemblk8);

			itembar8.defineCommand(itembar8.OKFIND, "okFindITEM8");
			itembar8.defineCommand(itembar8.COUNTFIND, "countFindITEM8");

			itemblk8.setMasterBlock(itemblk6); 
			itemtbl8 = mgr.newASPTable(itemblk8);
			itemtbl8.setWrap();

			itemlay8 = itemblk8.getASPBlockLayout();    
			itemlay8.setDefaultLayoutMode(itemlay8.MULTIROW_LAYOUT); 
		}

		//------------------------------------------------------------------------------------------------
		//----------------------------------- Tools and Facilities Tab  ----------------------------------
		//------------------------------------------------------------------------------------------------

		itemblk7 = mgr.newASPBlock("ITEM7");

		f = itemblk7.addField("ITEM7_OBJID");
		f.setHidden();
		f.setDbName("OBJID");

		f = itemblk7.addField("ITEM7_OBJVERSION");
		f.setHidden();
		f.setDbName("OBJVERSION");

		f = itemblk7.addField("ITEM7_WO_NO","Number","#");
		f.setSize(11);
		f.setHidden();
		f.setDbName("WO_NO");

		f = itemblk7.addField("ITEM_ROW_NO");
		f.setSize(10);
		f.setDbName("ROW_NO");
		f.setLabel("PCMWHISTORICALSEPARATERMBROWNO: Row No");

		f = itemblk7.addField("TOOL_FACILITY_ID");
		f.setSize(10);
		f.setLabel("PCMWHISTORICALSEPARATERMBTFID: Tool/Facility Id");
		f.setUpperCase();

		f = itemblk7.addField("TOOL_FACILITY_DESC");
		f.setSize(25);
		f.setLabel("PCMWHISTORICALSEPARATERMBTFDESC: Tool/Facility Description");
		f.setDefaultNotVisible();


		f = itemblk7.addField("TOOL_FACILITY_TYPE");
		f.setSize(10);
		f.setLabel("PCMWHISTORICALSEPARATERMBTFTYPE: Tool/Facility Type");
		f.setUpperCase();

		f = itemblk7.addField("TYPE_DESC");
		f.setSize(25);
		f.setLabel("PCMWHISTORICALSEPARATERMBTFTYPEDESC: Type Description");
		f.setDefaultNotVisible();

		f = itemblk7.addField("ITEM7_CONTRACT");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM7CONTRACT: Site");
		f.setDbName("CONTRACT");
		f.setUpperCase();

		f = itemblk7.addField("ITEM7_DEPARTMENT");
		f.setSize(11);
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM7DEPT: Maintenance Organization");
		f.setDbName("ORG_CODE");

		f = itemblk7.addField("ITEM7_QTY", "Number");
		f.setSize(10);
      //Bug ID 82934, Start
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM7QTY: Planned Quantity");
      //Bug ID 82934, End
		f.setDbName("QTY");
		f.setUpperCase();

		f = itemblk7.addField("ITEM7_PLANNED_HOUR", "Number");
		f.setDbName("PLANNED_HOUR");
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM7PLANNEDHOUR: Planned Hours");
		f.setSize(10);

		f = itemblk7.addField("ITEM7_JOB_ID", "Number");
		f.setDbName("JOB_ID");
		f.setSize(8);
		f.setReadOnly();
		f.setDefaultNotVisible();
		f.setDynamicLOV("WORK_ORDER_JOB", "ITEM7_WO_NO WO_NO");
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM7JOBID: Job Id");

		f = itemblk7.addField("ITEM7_CRAFT_LINE_NO", "Number");
		f.setDbName("CRAFT_LINE_NO");
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM7OPERNO: Operation No");
		f.setSize(8);

		f = itemblk7.addField("ITEM7_PLAN_LINE_NO", "Number");
		f.setDbName("PLAN_LINE_NO");
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM7PLANLINENO: Plan Line No");
		f.setSize(8);
		f.setReadOnly();

		f = itemblk7.addField("ITEM5_COST", "Money");
		f.setDbName("COST");
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM7COST: Cost");
		f.setSize(12);
		f.setDefaultNotVisible();

		f = itemblk7.addField("ITEM7_COST_CURRENCY");
		f.setDbName("COST_CURRENCY");
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM7COSTCURRENCY: Cost Currency");
		f.setSize(10);
		f.setReadOnly();

		f = itemblk7.addField("ITEM7_COST_AMOUNT", "Money");
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM7COSTAMOUNT: Cost Amount");
		f.setSize(12);
		f.setReadOnly();
		f.setDbName("COST_AMOUNT");
		f.setDefaultNotVisible();

		f = itemblk7.addField("ITEM7_CATALOG_NO_CONTRACT");
		f.setDbName("CATALOG_NO_CONTRACT");
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM7CATNOCONTRACT: Sales Part Site");
		f.setSize(8);

		f = itemblk7.addField("ITEM7_CATALOG_NO");
		f.setDbName("CATALOG_NO");
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM7CATALOGNO: Sales Part");
		f.setSize(20);

		f = itemblk7.addField("ITEM5_CATALOG_NO_DESC");
		f.setDbName("CATALOG_DESC");
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM7CATALOGDESC: Sales Part Description");
		f.setSize(30);
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = itemblk7.addField("ITEM7_SALES_PRICE", "Money");
		f.setDbName("SALES_PRICE");
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM7SALESPRICE: Sales Price");
		f.setSize(12);
		f.setDefaultNotVisible();

		f = itemblk7.addField("ITEM7_SALES_CURRENCY");
		f.setDbName("PRICE_CURRENCY");
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM7SALESCURR: Sales Currency");
		f.setSize(10);
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = itemblk7.addField("ITEM7_DISCOUNT", "Number");
		f.setDbName("DISCOUNT");
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM7DISCOUNT: Discount");
		f.setSize(8);
		f.setDefaultNotVisible();

		f = itemblk7.addField("ITEM7_DISCOUNTED_PRICE", "Money");
		f.setDbName("DISCOUNTED_PRICE");
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM7DISCOUNTEDPRICE: Discounted Price");
		f.setSize(12);
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = itemblk7.addField("ITEM7_PLANNED_PRICE", "Money");
		f.setDbName("PLANNED_PRICE_AMT");
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM7PLANNEDPRICE: Planned Price Amount");
		f.setSize(12);
		f.setReadOnly();
		f.setDefaultNotVisible();

		f = itemblk7.addField("FROM_DATE_TIME", "Datetime");
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM7FROMDATE: From Date/Time");
		f.setSize(20);
		f.setDefaultNotVisible();

		f = itemblk7.addField("TO_DATE_TIME", "Datetime");
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM7TODATE: To Date/Time");
		f.setSize(20);
		f.setDefaultNotVisible();

		f = itemblk7.addField("ITEM5_NOTE");
		f.setDbName("NOTE");
		f.setLabel("PCMWHISTORICALSEPARATERMBITEM7NOTE: Note");
		f.setSize(20);
		f.setDefaultNotVisible();

		itemblk7.setView("WORK_ORDER_TOOL_FACILITY");
		itemblk7.defineCommand("WORK_ORDER_TOOL_FACILITY_API", "");
		itemset7 = itemblk7.getASPRowSet();

		itembar7 = mgr.newASPCommandBar(itemblk7);
		itembar7.setBorderLines(true,true);

		itembar7.defineCommand(itembar7.OKFIND, "okFindITEM7");
		itembar7.defineCommand(itembar7.COUNTFIND, "countFindITEM7");

		itemblk7.setMasterBlock(headblk); 

		itemtbl7 = mgr.newASPTable(itemblk7);
		itemtbl7.setWrap();

		itemlay7 = itemblk7.getASPBlockLayout();    
		itemlay7.setDefaultLayoutMode(itemlay7.MULTIROW_LAYOUT); 
		itemlay7.unsetAutoLayoutSelect();   

		// ---------------------------------------------------------------------------------------
		// --------------------------------   ITEMBLK9  ------------------------------------------
		// ---------------------------------------------------------------------------------------

		itemblk9 = mgr.newASPBlock("ITEM9");

		itemblk9.addField("ITEM9_OBJID").
		setDbName("OBJID").
		setHidden();

		itemblk9.addField("ITEM9_OBJVERSION").
		setDbName("OBJVERSION").
		setHidden();

		itemblk9.addField("ITEM9_WO_NO", "Number", "#").
		setDbName("WO_NO").
		setHidden().
		setReadOnly().
		setInsertable().
		setMandatory();

		itemblk9.addField("JOB_ID", "Number").
		setLabel("HISTSEPARATEITEM9JOBID: Job ID").
		setReadOnly().
		setInsertable().
		setMandatory();

		itemblk9.addField("STD_JOB_ID").
		setSize(15).
		setLabel("HISTSEPARATEITEM9STDJOBID: Standard Job ID").
		setLOV("SeparateStandardJobLov.page", "STD_JOB_CONTRACT CONTRACT", 600, 445, true).
		setUpperCase().
		setInsertable().
		setQueryable().
		setMaxLength(12);

		itemblk9.addField("STD_JOB_CONTRACT").
		setSize(10).
		setLabel("HISTSEPARATEITEM9STDJOBCONTRACT: Site").
		setUpperCase().
		setReadOnly().
		setMaxLength(5);

		itemblk9.addField("STD_JOB_REVISION").
		setSize(10).
		setLabel("HISTSEPARATEITEM9STDJOBREVISION: Revision").
		setDynamicLOV("SEPARATE_STANDARD_JOB_LOV", "STD_JOB_CONTRACT CONTRACT,STD_JOB_ID", 600, 445, true).    
		setUpperCase().
		setInsertable().
		setQueryable().
		setMaxLength(6);

		itemblk9.addField("ITEM9_DESCRIPTION").
		setDbName("DESCRIPTION").
		setSize(35).
		setLabel("HISTSEPARATEITEM9DESCRIPTION: Description").
		setUpperCase().
		setMandatory().
		setInsertable().
		setMaxLength(4000);

		itemblk9.addField("ITEM9_QTY", "Number").
		setDbName("QTY").
		setLabel("HISTSEPARATEITEM9QTY: Quantity").
		setMandatory().
		setInsertable();

		itemblk9.addField("ITEM9_COMPANY").
		setDbName("COMPANY").
		setSize(20).
		setHidden().
		setUpperCase().
		setInsertable();

                itemblk9.addField("STD_JOB_STATUS").
                setLabel("HISTSEPARATESTDJOBSTATUS: Std Job Status").
                setFunction("Standard_Job_API.Get_Status(:STD_JOB_ID,:STD_JOB_CONTRACT,:STD_JOB_REVISION)");

		itemblk9.addField("SIGN_ID").
		setSize(35).
		setLabel("HISTSEPARATEITEM9SIGNID: Executed By").
		setQueryable().
                setDbName("SIGNATURE").
		//setFunction("Company_Emp_API.Get_Person_Id(COMPANY, EMPLOYEE_ID)").
		setReadOnly();

		itemblk9.addField("EMPLOYEE_ID").
		setSize(15).
		setLabel("HISTSEPARATEITEM9EMPLOYEEID: Employee ID").
		setDynamicLOV("EMPLOYEE_NO", "ITEM9_COMPANY COMPANY", 600, 445).    
		setUpperCase().
		setInsertable().
		setQueryable().
		setMaxLength(11);

		itemblk9.addField("ITEM9_DATE_FROM", "Datetime").
		setDbName("DATE_FROM").
		setSize(20).
		setLabel("HISTSEPARATEITEM9DATEFROM: Date From").
		setInsertable();

		itemblk9.addField("ITEM9_DATE_TO", "Datetime").
		setDbName("DATE_TO").
		setSize(20).
		setLabel("HISTSEPARATEITEM9DATETO: Date To").
		setInsertable();

		itemblk9.addField("STD_JOB_FLAG", "Number").
		setHidden().
		setInsertable();

		itemblk9.addField("KEEP_CONNECTIONS").
		setHidden().
		setSize(3).
		setInsertable();

		itemblk9.addField("RECONNECT").
		setHidden().
		setSize(3).
		setInsertable();

		// -----------------------------------------------------------------------
		// -----------------------  Hidden Fields --------------------------------
		// -----------------------------------------------------------------------

		itemblk9.addField("N_JOB_EXIST", "Number").
		setFunction("0").
		setHidden();

		itemblk9.addField("S_STD_JOB_EXIST").
		setFunction("''").
		setHidden();

		itemblk9.addField("N_ROLE_EXIST", "Number").
		setFunction("0").
		setHidden();

		itemblk9.addField("N_MAT_EXIST", "Number").
		setFunction("0").
		setHidden();

		itemblk9.addField("N_TOOL_EXIST", "Number").
		setFunction("0").
		setHidden();

		itemblk9.addField("N_PLANNING_EXIST", "Number").
		setFunction("0").
		setHidden();

		itemblk9.addField("N_DOC_EXIST", "Number").
		setFunction("0").
		setHidden();

		itemblk9.addField("S_STD_JOB_ID").
		setFunction("''").
		setHidden();

		itemblk9.addField("S_STD_JOB_CONTRACT").
		setFunction("''").
		setHidden();

		itemblk9.addField("S_STD_JOB_REVISION").
		setFunction("''").
		setHidden();

		itemblk9.addField("N_QTY", "Number").
		setFunction("0").
		setHidden();

		itemblk9.addField("S_IS_SEPARATE").
		setFunction("''").
		setHidden();

		itemblk9.addField("S_AGREEMENT_ID").
		setFunction("''").
		setHidden();

		itemblk9.setView("WORK_ORDER_JOB");
		itemblk9.defineCommand("WORK_ORDER_JOB_API","New__,Modify__,Remove__");
		itemblk9.setMasterBlock(headblk);

		itemset9 = itemblk9.getASPRowSet();

		itemtbl9 = mgr.newASPTable(itemblk9);
		itemtbl9.setTitle(mgr.translate("HISTSEPARATEITEM9WOJOBS: Jobs"));
		itemtbl9.setWrap();

		itembar9 = mgr.newASPCommandBar(itemblk9);
		itembar9.enableCommand(itembar9.FIND);

		itembar9.defineCommand(itembar9.OKFIND, "okFindITEM9");
		itembar9.defineCommand(itembar9.COUNTFIND, "countFindITEM9");

		itembar9.disableCommand(itembar9.NEWROW);
		itembar9.disableCommand(itembar9.EDITROW);
		itembar9.disableCommand(itembar9.DELETE);
		itembar9.disableCommand(itembar9.DUPLICATEROW);

		itemlay9 = itemblk9.getASPBlockLayout();
		itemlay9.setDefaultLayoutMode(itemlay9.MULTIROW_LAYOUT);
	}

	public void getViewNames()
	{
		ASPManager mgr = getASPManager();

		String viewName1;
		String titleName1;
		String viewName2;
		String titleName2;
		String viewName3;
		String titleName3;
		String titleName4;
		String titleName5;
		String titleName6;
		String titleName7;
		String titleName8;
		String titleName9;
		String titleName10;
		String titleName11;
		String titleName12;
		String titleName13;

		trans.clear();
		ASPCommand cmd = trans.addCustomFunction( "GETCON", "User_Default_API.Get_Contract", "CONTRACT1" );

		cmd = trans.addCustomFunction("GETCOM", "Site_API.Get_Company", "COMPANY1");
		cmd.addReference("CONTRACT1", "GETCON/DATA");

		cmd = trans.addCustomCommand("GETCODE1", "Maintenance_Accounting_API.Get_Log_Code_Part");
		cmd.addParameter("CODEPARTCOSTCENTER");
		cmd.addParameter("VIEWNAME");
		cmd.addParameter("PKGNAME");
		cmd.addParameter("INTERNAME");
		cmd.addReference("COMPANY1", "GETCOM/DATA");
		cmd.addParameter("LOGCODEPARTCOSTCENTER", "CostCenter");

		cmd = trans.addCustomFunction("GETNAME1", "Accounting_Code_Parts_API.Get_Name", "CODEPARTB");
		cmd.addReference("COMPANY1", "GETCOM/DATA");
		cmd.addReference("CODEPARTCOSTCENTER", "GETCODE1/DATA");


		cmd = trans.addCustomCommand("GETCODE2", "Maintenance_Accounting_API.Get_Log_Code_Part");
		cmd.addParameter("CODEPARTCOSTCENTER");
		cmd.addParameter("VIEWNAME");
		cmd.addParameter("PKGNAME");
		cmd.addParameter("INTERNAME");
		cmd.addReference("COMPANY1", "GETCOM/DATA");
		cmd.addParameter("LOGCODEPARTCOSTCENTER", "Project");

		cmd = trans.addCustomFunction( "GETNAME2", "Accounting_Code_Parts_API.Get_Name", "CODEPARTB");
		cmd.addReference("COMPANY1", "GETCOM/DATA");
		cmd.addReference("CODEPARTCOSTCENTER", "GETCODE2/DATA");

		cmd = trans.addCustomCommand("GETCODE3", "Maintenance_Accounting_API.Get_Log_Code_Part");
		cmd.addParameter("CODEPARTCOSTCENTER");
		cmd.addParameter("VIEWNAME");
		cmd.addParameter("PKGNAME");
		cmd.addParameter("INTERNAME");
		cmd.addReference("COMPANY1", "GETCOM/DATA");
		cmd.addParameter("LOGCODEPARTCOSTCENTER", "Object");

		cmd = trans.addCustomFunction("GETNAME3", "Accounting_Code_Parts_API.Get_Name", "CODEPARTB");
		cmd.addReference("COMPANY1", "GETCOM/DATA");
		cmd.addReference("CODEPARTCOSTCENTER", "GETCODE3/DATA");

		cmd = trans.addCustomFunction("GETNAME4", "Accounting_Code_Parts_API.Get_Name", "CODEPARTB");
		cmd.addReference("COMPANY1", "GETCOM/DATA");
		cmd.addParameter("CODEPARTCOSTCENTER", "A");

		cmd = trans.addCustomFunction("GETNAME5", "Accounting_Code_Parts_API.Get_Name", "CODEPARTB");
		cmd.addReference("COMPANY1", "GETCOM/DATA");
		cmd.addParameter("CODEPARTCOSTCENTER", "C");

		cmd = trans.addCustomFunction("GETNAME6", "Accounting_Code_Parts_API.Get_Name", "CODEPARTB");
		cmd.addReference("COMPANY1", "GETCOM/DATA");
		cmd.addParameter("CODEPARTCOSTCENTER", "D");

		cmd = trans.addCustomFunction("GETNAME7", "Accounting_Code_Parts_API.Get_Name", "CODEPARTB");
		cmd.addReference("COMPANY1", "GETCOM/DATA");
		cmd.addParameter("CODEPARTCOSTCENTER", "G");

		cmd = trans.addCustomFunction("GETNAME8", "Accounting_Code_Parts_API.Get_Name", "CODEPARTB");
		cmd.addReference("COMPANY1", "GETCOM/DATA");
		cmd.addParameter("CODEPARTCOSTCENTER", "H");

		cmd = trans.addCustomFunction("GETNAME9", "Accounting_Code_Parts_API.Get_Name", "CODEPARTB");
		cmd.addReference("COMPANY1", "GETCOM/DATA");
		cmd.addParameter("CODEPARTCOSTCENTER", "I");

		cmd = trans.addCustomFunction("GETNAME10", "Accounting_Code_Parts_API.Get_Name", "CODEPARTB");
		cmd.addReference("COMPANY1", "GETCOM/DATA");
		cmd.addParameter("CODEPARTCOSTCENTER", "J");

		cmd = trans.addCustomFunction("GETNAME11", "Accounting_Code_Parts_API.Get_Name", "CODEPARTB");
		cmd.addReference("COMPANY1", "GETCOM/DATA");
		cmd.addParameter("CODEPARTCOSTCENTER", "B");

		cmd = trans.addCustomFunction("GETNAME12", "Accounting_Code_Parts_API.Get_Name", "CODEPARTB");
		cmd.addReference("COMPANY1", "GETCOM/DATA");
		cmd.addParameter("CODEPARTCOSTCENTER", "E");

		cmd = trans.addCustomFunction("GETNAME13", "Accounting_Code_Parts_API.Get_Name", "CODEPARTB");
		cmd.addReference("COMPANY1", "GETCOM/DATA");
		cmd.addParameter("CODEPARTCOSTCENTER", "F");

		trans = mgr.perform(trans);

		viewName1 = trans.getValue("GETCODE1/DATA/VIEWNAME");
		titleName1 = trans.getValue("GETNAME1/DATA/CODEPARTB");

		viewName2 = trans.getValue("GETCODE2/DATA/VIEWNAME");
		titleName2 = trans.getValue("GETNAME2/DATA/CODEPARTB");

		viewName3 = trans.getValue("GETCODE3/DATA/VIEWNAME");
		titleName3 = trans.getValue("GETNAME3/DATA/CODEPARTB");

		titleName4 = trans.getValue("GETNAME4/DATA/CODEPARTB");
		titleName5 = trans.getValue("GETNAME5/DATA/CODEPARTB");
		titleName6 = trans.getValue("GETNAME6/DATA/CODEPARTB");
		titleName7 = trans.getValue("GETNAME7/DATA/CODEPARTB");
		titleName8 = trans.getValue("GETNAME8/DATA/CODEPARTB");
		titleName9 = trans.getValue("GETNAME9/DATA/CODEPARTB");
		titleName10 = trans.getValue("GETNAME10/DATA/CODEPARTB");
		titleName11 = trans.getValue("GETNAME11/DATA/CODEPARTB");
		titleName12 = trans.getValue("GETNAME12/DATA/CODEPARTB");
		titleName13 = trans.getValue("GETNAME13/DATA/CODEPARTB");

		mgr.getASPField("ACCNT").setLabel(titleName4);
		mgr.getASPField("CODE_C").setLabel(titleName5);
		mgr.getASPField("CODE_D").setLabel(titleName6);
		mgr.getASPField("CODE_G").setLabel(titleName7);
		mgr.getASPField("CODE_H").setLabel(titleName8);
		mgr.getASPField("CODE_I").setLabel(titleName9);
		mgr.getASPField("CODE_J").setLabel(titleName10);
		mgr.getASPField("COST_CENTER").setLabel(titleName11);
		mgr.getASPField("ITEM2_COST_CENTER").setLabel(titleName11);
		mgr.getASPField("OBJECT_NO").setLabel(titleName12);
		mgr.getASPField("ITEM2_OBJECT_NO").setLabel(titleName12);
		mgr.getASPField("PROJECT_NO").setLabel(titleName13);
		mgr.getASPField("ITEM2_PROJECT_NO").setLabel(titleName13);       

		mgr.getASPField("COST_CENTER").setDynamicLOV(viewName1,"COMPANY1 COMPANY",600,445);
		mgr.getASPField("PROJECT_NO").setDynamicLOV(viewName2,"COMPANY1 COMPANY",600,445);
		mgr.getASPField("OBJECT_NO").setDynamicLOV(viewName3,"COMPANY1 COMPANY",600,445);  
		mgr.getASPField("CODE_C").setDynamicLOV("CODE_C","COMPANY1 COMPANY",600,445); 
		mgr.getASPField("CODE_D").setDynamicLOV("CODE_D","COMPANY1 COMPANY",600,445);
		mgr.getASPField("CODE_G").setDynamicLOV("CODE_G","COMPANY1 COMPANY",600,445);
		mgr.getASPField("CODE_H").setDynamicLOV("CODE_H","COMPANY1 COMPANY",600,445);
		mgr.getASPField("CODE_I").setDynamicLOV("CODE_I","COMPANY1 COMPANY",600,445);
		mgr.getASPField("CODE_J").setDynamicLOV("CODE_J","COMPANY1 COMPANY",600,445);
		mgr.getASPField("REPORTED_BY").setDynamicLOV("EMPLOYEE_LOV","COMPANY1 COMPANY",600,445);
		mgr.getASPField("PREPARED_BY").setDynamicLOV("EMPLOYEE_LOV","COMPANY1 COMPANY",600,445);
		mgr.getASPField("WORK_MASTER_SIGN").setDynamicLOV("EMPLOYEE_LOV","COMPANY1 COMPANY",600,445);
		mgr.getASPField("WORK_LEADER_SIGN").setDynamicLOV("EMPLOYEE_LOV","COMPANY1 COMPANY",600,445);
		mgr.getASPField("SIGNATURE").setDynamicLOV("EMPLOYEE_LOV","COMPANY1 COMPANY",600,445);
		mgr.getASPField("ACCNT").setDynamicLOV("ACCOUNT","COMPANY1 COMPANY",600,445);
		mgr.getASPField("CATALOG_NO").setDynamicLOV("SALES_PART","CONTRACT1 CONTRACT",600,445);
	}

	public void checkObjAvailable()
	{
		ASPManager mgr = getASPManager();

		if (!isSecurityChecked)
		{
			trans.clear();

			trans.addSecurityQuery("SEPARATE_WORK_ORDER," +
								   "WORK_ORDER_REQUIS_HEADER," +
								   "PERMIT,"+"COMPETENCY_GROUP_LOV1");

			trans.addSecurityQuery("Historical_Work_Order_API","Check_Work_Order");

			trans.addPresentationObjectQuery("PCMW/SeparateWorkOrder.page," +
											 "PCMW/WorkOrderRequisHeaderRMBRO.page," +
											 "PCMW/HistWorkOrderBudget.page," +
											 "PCMW/HistWorkOrderReportPage.page," +
											 "PCMW/HistoricalSeparateCOInfo.page," +
											 "PCMW/Permit.page");

			trans = mgr.perform(trans);

			ASPBuffer secViewBuff = trans.getSecurityInfo();

			actionsBuffer = mgr.newASPBuffer();

			if (secViewBuff.namedItemExists("PCMW/SeparateWorkOrder.page") && secViewBuff.itemExists("SEPARATE_WORK_ORDER"))
				actionsBuffer.addItem("okHeadStructure","");

			if (secViewBuff.itemExists("Historical_Work_Order_API.Check_Work_Order"))
				actionsBuffer.addItem("okHeadReOpen","");

			if (secViewBuff.namedItemExists("PCMW/WorkOrderRequisHeaderRMBRO.page") && secViewBuff.itemExists("WORK_ORDER_REQUIS_HEADER"))
				actionsBuffer.addItem("okHeadRequisitions","");

			if (secViewBuff.namedItemExists("PCMW/HistWorkOrderBudget.page"))
				actionsBuffer.addItem("okHeadBudget","");

			if (secViewBuff.namedItemExists("PCMW/HistWorkOrderReportPage.page"))
				actionsBuffer.addItem("okHeadFreeNotes","");

			if (secViewBuff.namedItemExists("PCMW/HistoricalSeparateCOInfo.page"))
				actionsBuffer.addItem("okHeadCoInfo","");

			if (secViewBuff.namedItemExists("PCMW/Permit.page") && secViewBuff.itemExists("PERMIT"))
				actionsBuffer.addItem("okItem1ViewPermit","");

                        if (secViewBuff.itemExists("COMPETENCY_GROUP_LOV1"))
                                actionsBuffer.addItem("okItem1additionalQualifications","");

			isSecurityChecked = true;
		}
	}

	public void adjustActions()
	{
		ASPManager mgr = getASPManager();

		if (!actionsBuffer.itemExists("okHeadStructure"))
			headbar.removeCustomCommand("structure");

		if (!actionsBuffer.itemExists("okHeadReOpen"))
			headbar.removeCustomCommand("reopen");

		if (!actionsBuffer.itemExists("okHeadRequisitions"))
			headbar.removeCustomCommand("requisitions");

		if (!actionsBuffer.itemExists("okHeadBudget"))
			headbar.removeCustomCommand("budget");

		if (!actionsBuffer.itemExists("okHeadFreeNotes"))
			headbar.removeCustomCommand("freeNotes");

		if (!actionsBuffer.itemExists("okHeadCoInfo"))
			headbar.removeCustomCommand("coInfo");

		if (!actionsBuffer.itemExists("okItem1ViewPermit"))
			itembar1.removeCustomCommand("viewPermit");

                if (!actionsBuffer.itemExists("okItem1additionalQualifications"))
			 itembar3.removeCustomCommand("additionalQualifications");

               
	}

	// 031210  ARWILK  Begin  (Replace blocks with tabs)
	public void activateOperations()
	{
		tabs.setActiveTab(1);
	}

	public void activateMaterials()
	{
		tabs.setActiveTab(2);
	}

	public void activatePermits()
	{
		tabs.setActiveTab(3);
	}

	public void activateTimeReport()
	{
		tabs.setActiveTab(4);
	}

	public void activateToolsAndFacilities()
	{
		tabs.setActiveTab(5);
	}

	public void activatePostings()
	{
		tabs.setActiveTab(6);
	}

	public void activateJobs()
	{
		tabs.setActiveTab(7);
	}

	public void activateMaintTask()
	{
		tabs.setActiveTab(8);
	}
	// 031210  ARWILK  End  (Replace blocks with tabs)

	// 040128  ARWILK  Begin  (Enable Multirow RMB actions)
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
	// 040128  ARWILK  End  (Enable Multirow RMB actions)

	public void adjust()
	{
		ASPManager mgr = getASPManager();

		// 031210  ARWILK  Begin  (Replace blocks with tabs)
		headbar.removeCustomCommand("activateOperations");
		headbar.removeCustomCommand("activateMaterials");
		headbar.removeCustomCommand("activatePermits");
		headbar.removeCustomCommand("activateTimeReport");
		headbar.removeCustomCommand("activateToolsAndFacilities");
		headbar.removeCustomCommand("activatePostings");
		headbar.removeCustomCommand("activateJobs");
		headbar.removeCustomCommand("activateMaintTask");
		// 031210  ARWILK  End  (Replace blocks with tabs)

		if (mgr.isPresentationObjectInstalled("enterw/SupplierInfoGeneral.page"))
			mgr.getASPField("VENDOR_NO").setHyperlink("../enterw/SupplierInfoGeneral.page","VENDOR_NO","NEWWIN");

		if (mgr.isPresentationObjectInstalled("docmaw/DocReference.page"))
			mgr.getASPField("DOCUMENT").setHyperlink("../docmaw/DocReference.page","LU_NAME,KEY_REF","NEWWIN");
     
		getViewNames();

		if (headset.countRows()>0)
		{
                        setWorkCenterValues();
			if (headlay.isSingleLayout())
			{
				if ("VIM".equals(headset.getRow().getValue("CONNECTION_TYPE_DB")))
				{
					mgr.getASPField("MCH_CODE_CONTRACT").setHidden();
				}
				else
				{
					mgr.getASPField("MCH_CODE_CONTRACT").unsetHidden();

				}
			}
		    if (!mgr.isEmpty(mgr.getQueryStringValue("IS_POSTING_ACTIVATE"))) {
			this.activatePostings();
		    }
		}
      
	}


//===============================================================
//  HTML
//===============================================================
	protected String getDescription()
	{
		return "PCMWHISTORICALSEPARATERMBTITLE: Historical Work Order";
	}

	protected String getTitle()
	{
		return "PCMWHISTORICALSEPARATERMBTITLE: Historical Work Order";
	}

	protected void printContents() throws FndException
	{
		ASPManager mgr = getASPManager();

        if ((bFirstRequest)) // mgr.isExplorer())
        {
            appendToHTML("<html>\n");
            appendToHTML("<head></head>\n"); 
            appendToHTML("<body>"); 
            appendToHTML("<form name='form' method='POST' action='"+mgr.getURL()+"'>"); 
            appendToHTML("  <input type=\"hidden\" value=\"OK\" name=\"FIRST_REQUEST\" >"); 
            appendToHTML("  <input type=\"hidden\" value=\""+mgr.HTMLEncode(mgr.readValue("QUOTATION_ID"))+"\" name=\"QUOTATION_ID\" >"); 
            appendToHTML("  <input type=\"hidden\" value=\"FALSE\" name=\"VIEWWORKORDER\" >"); 
	    appendToHTML("</form></body></html>"); 
            appendDirtyJavaScript("document.form.submit();"); 
         }

       else
        {

	        printHiddenField("VIEWWORKORDER", "FALSE");
		appendToHTML(headlay.show());

		if (headlay.isSingleLayout() && (headset.countRows() > 0))
		{
			appendToHTML(tabs.showTabsInit());

			if (tabs.getActiveTab() == 1)
                        {
                           appendToHTML(itemlay3.show());
                           if (itemlay3.isSingleLayout() && (itemset3.countRows() > 0))
                               appendToHTML(itemlay10.show());
                        }
			else if (tabs.getActiveTab() == 2)
			{
				appendToHTML(itemlay5.show());
				if (itemlay5.isSingleLayout() && (itemset5.countRows() > 0))
					appendToHTML(itemlay.show());
			}
			else if (tabs.getActiveTab() == 3)
				appendToHTML(itemlay1.show());
			else if (tabs.getActiveTab() == 4)
				appendToHTML(itemlay4.show());
			else if (tabs.getActiveTab() == 5)
				appendToHTML(itemlay7.show());
			else if (tabs.getActiveTab() == 6)
				appendToHTML(itemlay2.show());
			else if (tabs.getActiveTab() == 7)
				appendToHTML(itemlay9.show());
			else if (tabs.getActiveTab() == 8)
			{
				appendToHTML(itemlay6.show());
				if (itemlay6.isSingleLayout() && (itemset6.countRows() > 0))
					appendToHTML(itemlay8.show());
			}
		}
		// 031210  ARWILK  End  (Replace blocks with tabs)

		appendDirtyJavaScript("//-------------------------------------------------------------------------------\n");
		appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
		appendDirtyJavaScript("//-------------------------------------------------------------------------------\n");
                appendDirtyJavaScript("	window.name = \"wndHistWO\";\n");

		// 040128  ARWILK  Begin  (Remove uneccessary global variables)
		if (bOpenNewWindow)
		{

			appendDirtyJavaScript("  window.open(\"");
			appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));    //XSS_Safe AMNILK 20070716
			appendDirtyJavaScript("\", \"");
			appendDirtyJavaScript(newWinHandle);
			appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
		}
                    
		// 040128  ARWILK  End  (Remove uneccessary global variables)

		appendDirtyJavaScript("if('");
		appendDirtyJavaScript(performRMB);
		appendDirtyJavaScript("' == \"TRUE\")\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("  url_to_go = '");
		appendDirtyJavaScript(URLString);
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("  window_name = '");
		appendDirtyJavaScript(WindowName);
		appendDirtyJavaScript("';\n");
		appendDirtyJavaScript("  window.open(url_to_go,window_name,\"resizable,status=yes,width=750,height=550\");\n");
		appendDirtyJavaScript("}\n");

		appendDirtyJavaScript("function viewWorkOrder()\n");
		appendDirtyJavaScript("{\n");
		appendDirtyJavaScript("if (confirm(\" ");
		appendDirtyJavaScript(mgr.translateJavaScript("PCMWHISTORICALSEPARATERMBVIEWWO: Work Order(s) opened successfully! Do you want to view the reopened work order(s)?"));
		appendDirtyJavaScript("\" )) \n");
		appendDirtyJavaScript("{ \n");
		appendDirtyJavaScript("document.form.VIEWWORKORDER.value = \"TRUE\"; \n");
		appendDirtyJavaScript("return true;\n");
	        appendDirtyJavaScript(" }\n");
		appendDirtyJavaScript("else \n");
		appendDirtyJavaScript("{ \n");
		appendDirtyJavaScript("document.form.VIEWWORKORDER.value = \"FALSE\"; \n");
		appendDirtyJavaScript("return true;\n");
		appendDirtyJavaScript("} \n");
		appendDirtyJavaScript("}\n");
	}
        }
}
