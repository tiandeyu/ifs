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
*  File        : WorkOrderRequisHeaderRMB.java 
*  Created     : ARWILK  010221
*  Modified    :
*  BUNILK  010328  Corrected some errors in all saveReturn(), newRow(), okFind(),and 
*                  some RMB functions and validate() function. Added to new functions (readNumberField()
*                  and readField() ).   
*  BUNILK  010331  Modified requisitionToOrder0() and requisitionToOrder1() methods.     
*  NUPELK  010424  Added missing RMB Text Processing.
*  INROLK  010504  Did necessary changes for Call ID 77556  
*  ARWILK  010515  Corrected the Validations.
*  CHCRLK  010612  Modified overwritten validates.  
*  BUNILK  010618  Set 'Requisitioner' field readonly.   
*  INROLK  010621  Changed functions requisitionToOrder0 and requisitionToOrder1
*  BUNILK  010815  Modified saveReturn1() and saveReturn0() methods.
*  BUNILK  010816  Modified validation of PART_NO field, setValues() and setValuesItem1() methods.
*                  and set defaultNotVisible property for some fields of "No Part Requsition Line" block.   
*  ARWILK  010904  Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction(). 
*                  (Caused when cancelNew is performed after saveNew)     
*  BUNILK  010907  Add setInsertable property to REQUISITIONERCODE field and removed getting default 
*                  value for it while putting new record.                    
*  BUNILK  010928  Added security check for Actions.
*  BUNILK  011008  Added LOV for Requsitioner field and removed edit button from headblk.
*  SHAFLK  010604  Bug id 30462, Changed newRow(),newRowITEM0()  and newRowITEM1(). 
*  SHAFLK  010919  Bug id 32905, Added some fields to itemblock1 & 2, Changed functions prePost0() & prePost1() and Changed HTML part.
*  BUNILK  021212  Merged with 2002-3 SP3
*  YAWILK  021226  Added new actions "Requisition for Request" and "Request" Modified newRowITEM0()
*  NIMHLK  030107  Added two fields Condition Code and Condition Code Description according to specification W110 - Condition Codes.
*  SHAFLK  030401  Changed newRowITEM0(), newRowITEM1(), saveReturn0() and saveReturn1(). 
*  JEWILK  030725  Scream Merge.
*  CHAMLK  030827  Added Ownership, Owner and Owner Name. 
*  JEWILK  030912  Modified createPurchaseOrder() to read DEFAUTH from mgr. Call 100915.
*  JEWILK  030926  Corrected overridden javascript function toggleStyleDisplay(). 
*  JEWILK  031012  Corrected Enable/Disable conditions for RMB actions requisitionToOrderMain0 and requisitionToOrderMain1.
*  JEWILK  031013  Corrected Validation of ORDER_CODE to allow values 1,5 and 6. Call 106251.
*  JEWILK  031013  Added fields SERIAL_NO,LOT_BATCH_NO,SERVICE_TYPE,SERVICE_TYPE_DESC. Call 106256.
*  JEWILK  031013  Modified SaveReturn0() to correctly save PART_OWNERSHIP and OWNER. Call 106271.
*  ARWILK  031107  (Bug#110151) Added new fields to itemblk1. (Check method comments)
*  SAPRLK  040105  Web Alignment, Converting blocks to tabs, removing 'clone' & 'doReset' methods, removing the title of the blocks.
*  VAGULK  040128  Web Alignment ,arranged the field order according to the Centura application.
*  ARWILK  040219  Edge Developments - (Enable Multirow RMB actions, Remove uneccessary global variables, Enhance Performance, Clean Javascript Code)
* --------------------------------- Edge - SP1 Merge -------------------------
*  BUNILK  040106  Bug 40689 Modified validation part of PART_NO field.
*  SHAFLK  040120  Bug 41815,Removed Java dependencies.
*  SHAFLK  040128  Bug 40045, Modified methods requisitionToOrderMain0 and requisitionToOrderMain1. 
*  SHAFLK  040302  Bug 43068, Modified methods validatePartNo,saveReturn0(), predefine() and validation of PART_NO and added validations for SERIAL_NO and CONDITIONCODE.
*  THWILK  040322  Merge with SP1.
*  THWILK  040330  Modified PURCHASE_ORDER2_API.Check_Internal_Destination__ call to PURCHASE_ORDER_API.Check_Internal_Destination__.
*  THWILK  040331  Modified methods getContents(),releaseRequisition(),releaseRequisitionline0(),releaseRequisitionline1(),planRequisition(),
*                  planRequisitionline0(),planRequisitionline1(),okFind11() and predefine().
*  SHAFLK  040406  Bug Id 40357, Modified method saveReturn1(), and preDefine().
*  THWILK  040816  Merged Bug Id 40357.
*  THWILK  040823  Call 117018, Modified predefine().
*  NIAJLK  040823  Call 117031, Modified adjust().
*  THWILK  040823  Call 117020, Modified run(),createPurchaseOrder(),requisitionToOrderMain0(),requisitionToOrderMain1().
*  ARWILK  040824  Call 116980, Modiofied method saveReturn0.
*  ThWilk  040825  Added missing RMB functionality- spare parts in object, object structure and spare parts in detached part list.
*                  Modified predefine() and okFind().Added methods sparePartObject0,objStructure0 and detchedPartList0. 
*  NIJALK  040906  Modified preDefine(), saveReturn0(). Added field JOB_ID to itemblk0.
*  NIJALK  040908  Call 117750: Modified preDefine().
*  ARWILK  041112  Replaced getContents with printContents.
*  SHAFLK  050201  Bug Id 48736, Modified method setValues().
*  NAMELK  050214  Merged Bug 48736.
*  NIJALK  050228  Modified run().
*  NIJALK  050404  Call 122930: Modified newrow().
*  SHAFLK  050301  Bug Id 48760, Modified Validations of VENDORNO and PART_NO of Item Block0.
*  DiAmlk  050511  Merged the corrections done to the LCS Bug ID:48760.
*  THWILK  050415  Bug Id 50501, Modified methods okFindITEM0 and okFindITEM1.
*  DiAmlk  050517  Merged the corrections done in LCS Bug ID:50501. 
*  SHAFLK  050622  Bug ID 51498, Create supplier for purchase part dialog if trying to create a line with purchase part number with no 
*                  existing "supplier for purchase part" object.
*  ThWilk  050711  Merged the corrections done in LCS Bug 51498.In addition tax functionality,price conversions and price U/M was implemented.  
*  AMNILK  051020  Call Id: 127932. Modified the FORMATDATE at newRow(),newRowItem0(),newRowItem1(),saveReturn0().
*  NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile).
*  NIJALK  051214  Call 129484: Modified newRow(),newRowITEM0(),newRowITEM1(),saveReturn1().
*  DiAmlk  060109  Bug ID:130787 - Modified the methods newRow(),newRowITEM0() and newRowITEM1().
*  SHAFLK  060105  Bug Id 55378, Changed releaseRequisitionLine0 and releaseRequisitionLine1.
*  SHAFLK  060117  Bug Id 55378, Changed planRequisitionLine0 and planRequisitionLine1.
*  NIJALK  060123  Merged bug 55378.
*  ASSALK  060223  Call 134524. Added confirmation, error msg and a information message when save external order.
*  NIJALK  060223  Call 135208: Modified urlString in prePost1().
*  NIJALK  060225  Call 135205: Set ITEM1_VENDORNO to Uppercase.
*  NIJALK  060227  Call 135204: Set ITEM1_LISTPRICE Editable. Set validation to ITEM1_LISTPRICE. Modified setValuesItem1().
*  SULILK  060301  Call 135213: Modified preDefine().
*  NIJALK  060301  Call 135774: Modified saveReturn0Confirmed().
*  NIJALK  060302  Call 135776: Modified validation to PART_NO,SERIAL_NO.
*  NIJALK  060307  Call 136539: Filtered LOV for SERIAL_NO by PART_OWNERSHIP.
*  NEKOLK  060308  Call 135828: Added refreshForm() and made changes in predefine().
*  NIJALK  060317  Call 137562: Removed validation to SERIAL_NO. Set CONDITION_CODE Uppercase.
*  NIJALK  060322  Call 137757: Added LOV property to field SERIAL_NO.
*  ----------------------------- 2.4.0 ---------------------------------------
*  RaKalk  060614  Changed the call to Get_Taxable of Purchase_Part_API to call Get_Taxable_Db
*  SHAFLK  060525  Bug 57598, Modified validation for PART_NO.
*  ChAmlk  060627  Merge with App7 SP1.
*  ILSOLK  060721  Merge with App7 SP1.
*  AMNILK  060731  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMDILK  060807  Merged with Bug Id 58214.
*  ILSOLK  060905  Merged Bug 59699.
*  AMNILK  060818  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMDILK  060901  Bug 58216, Eliminated SQL Injection security vulnerability.Modified methods validate(), populateOrderLines()
*  AMDILK  060906  Merged with the Bug Id 58216
*  AMNILK  060911  REQUEST ID 35876, INFORMATION IF SEVERAL PURCHASE REQUISITION EXISTS. Added new check box & modified the run(). 
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  AMDILK  070330  Merged bug id 64068
*  AMDILK  070515  Call Id 144679: Set a default value for the field "sales price"
*  AMDILK  070515  Call Id 144690: Modified saveReturn0Confirmed()
*  AMDILK  070518  Call Id 144691: Checked the wo status when enabling the RMB "Release"
*  ILSOLK  070524  Modified run().(Call Id 144665) 
*  AMDILK  070604  Call Id 144681: Inserted a new RMB "Notes" to the Requistion tab
*  ASSALK  070605  Call ID 144546: Changed order by clause of header block.
*  CHANLK  070605  Call 144675 change External services check box functionality.
*  AMDILK  070615  Call Id 144687: Modified releaseRequisition() refresh the details lines after state change
*  AMNILK  070716  Eliminated SQL Injections.
*  AMNILK  070727  Eliminated XXS Security Vulnerabilities.
*  ILSOLK  070731  Eliminated LocalizationErrors.
*  AMDILK  070801  Removed the scroll buttons of the parent when the child tabs are in new or edit modes
*  ILSOLK  070827  Modified adjust()(Call ID 147750)
*  ILSOLK  070904  Modified adjust() & releaseRequisition()(Call ID 148213)
*  SHAFLK  071030  Bug 67719, Added some columns to No part section.
*  SHAFLK  080404  Bug 72772, Modified preDefine().  
*  SHAFLK  080417  Bug 72924, Modified preDefine().
*  SHAFLK  080828  Bug 76455, Modified preDefine() and saveReturn0Confirmed().
*  SHAFLK  080901  Bug 76620, Modified preDefine().
*  SHAFLK  080909  Bug 76748, Modified preDefine(),saveReturn1(),newRowITEM1() and validation().
*  SHAFLK  081027  Bug 77458, Redisigned the page.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

import java.util.*;

public class WorkOrderRequisHeaderRMB extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderRequisHeaderRMB");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPHTMLFormatter fmt;
    private ASPContext ctx;

    private ASPBlock upperblk;
    private ASPRowSet upperset;
    private ASPTable uppertbl;
    private ASPBlockLayout upperlay;

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

    private ASPBlock requestblk;
    private ASPRowSet requestset;
    private ASPCommandBar requestbar;
    private ASPTable requesttbl;
    private ASPBlockLayout requestlay;

    private ASPBlock reqlineblk;
    private ASPRowSet reqlineset;
    private ASPCommandBar reqlinebar;
    private ASPTable reqlinetbl;
    private ASPBlockLayout reqlinelay;

    private ASPBlock orderblk;
    private ASPRowSet orderset;
    private ASPTable ordertbl;
    private ASPCommandBar orderbar;
    private ASPBlockLayout orderlay;

    private ASPBlock lineblk;
    private ASPRowSet lineset;
    private ASPCommandBar linebar;
    private ASPTable linetbl;
    private ASPBlockLayout linelay;

    private ASPBlock createSuppblk;
    private ASPRowSet createSuppset;
    private ASPCommandBar createSuppbar;
    private ASPTable createSupptbl;
    private ASPBlockLayout createSupplay;

    private ASPField f;
    private ASPCommandBar upperbar;
    private ASPBlock b;

    private boolean bOpenNewWindow;
    private String urlString;
    private String newWinHandle;
    private boolean createSupp;


    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private String calling_url;
    private boolean reportCust;
    private String up_mch_code;
    private String mch_name;
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
    private String strAppOwner;
    private int buffWoNo; 
    private int up_wo_no; 
    private String cus_no; 
    private String agree_id;
    private String dateformat;
    private String editFlag;   
    private boolean again;
    private boolean actEna0;
    private boolean actEna1;
    private boolean actEna2;
    private boolean actEna3;
    private boolean actEna4;
    private boolean actEna5;
    private boolean actEna6;
    private boolean actEna7;
    private boolean actEna8;
    private String dateMask;
    private boolean reqtoreq;
    private boolean reqtoord;
    private boolean fromRmb;
    private String sItemContract;
    private String sItemPartNo;
    private String sItemOriginalQty;
    private String sRequisitionNo;
    private String sLineNo;
    private String sReleaseNo;
    private String sItemWantedDeliveryDate;
    private String sItemProjectId;
    private int itemValue;
    private ASPBuffer defbuyDataBuffer;
    private ASPBuffer defauthDataBuffer;
    private ASPBuffer selectedRowsBuffer;
    private ASPBuffer objIdBuffer0;
    private ASPBuffer objIdBuffer1;
    private boolean isChecked;
    private int currHeadRow;
    private int currItem0Row;
    private int currItem1Row;
    private int selBlk;
    private boolean isSalesPartOk;
    private boolean isSecurityChecked;
    private String qrystr;
    private String withoutSn;
    private String withoutConditionCode;
    private String withoutSnInHand;
    private String sWoStatus;

    //Web Alignment - Replacing Blocks with Tabs
    private ASPTabContainer tabs;    
    //

    public WorkOrderRequisHeaderRMB(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();

        fmt = mgr.newASPHTMLFormatter();
        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();
        qrystr =  "";
        calling_url=ctx.findGlobal("CALLING_URL", "");
        strAppOwner = ctx.readValue("CTX7", strAppOwner);
        buffWoNo = ctx.readNumber("CTX8", buffWoNo);
        up_wo_no = ctx.readNumber("CTX9", up_wo_no);
        cus_no = ctx.readValue("CTX11", cus_no);
        agree_id = ctx.readValue("CTX12", agree_id);
        dateformat = ctx.readValue("CTX3", dateformat); 
        reportCust = ctx.readFlag("REPORTCUST", false);
        editFlag = ctx.readValue("EDITFLAG", "FALSE"); 
        qrystr = ctx.readValue("QRYSTR",qrystr);
        createSupp  = ctx.readFlag("CREATESUPP", false);

        actEna0 = ctx.readFlag("ACTENA0", false);
        actEna1 = ctx.readFlag("ACTENA1", false);
        actEna2 = ctx.readFlag("ACTENA2", false);
        actEna3 = ctx.readFlag("ACTENA3", false);
        actEna4 = ctx.readFlag("ACTENA4", false);
        actEna5 = ctx.readFlag("ACTENA5", false);
        actEna6 = ctx.readFlag("ACTENA6", false);
        actEna7 = ctx.readFlag("ACTENA7", false);
        actEna8 = ctx.readFlag("ACTENA8", false);

        again = ctx.readFlag("AGAIN", false);
        dateMask =  ctx.readValue("DATEMASK", ""); 

        reqtoreq = ctx.readFlag("REQTOREQ", false);
        reqtoord = ctx.readFlag("REQTOORD", false);
        fromRmb  = ctx.readFlag("FROMRMB", false);
        sItemContract = ctx.readValue("SITEMCONTRACT", "");
        sItemPartNo   = ctx.readValue("SITEMPARTNO", "");
        sItemOriginalQty = ctx.readValue("SITEMORIGINALQTY", "");
        sRequisitionNo = ctx.readValue("SREQUISITIONNO", "");
        sLineNo = ctx.readValue("SLINENO", "");
        sReleaseNo =  ctx.readValue("SRELEASENO", "");
        sItemWantedDeliveryDate = ctx.readValue("SITEMWANTEDDELIVERYDATE", ""); 
        sItemProjectId = ctx.readValue("SITEMPROJECTID", "");
        itemValue = ctx.readNumber("ITEMVALUE", 0);

        defbuyDataBuffer = ctx.readBuffer("BUYDATABUFFER");
        defauthDataBuffer = ctx.readBuffer("DEFAUTHDATBUF");
        selectedRowsBuffer = ctx.readBuffer("SELROWSBUF");
        objIdBuffer0 = ctx.readBuffer("OBJIDBUFFER0");
        objIdBuffer1 = ctx.readBuffer("OBJIDBUFFER1");

        currHeadRow = ctx.readNumber("CURRHEADROW", 0);
        currItem0Row = ctx.readNumber("CURRITEM0ROW", 0);
        currItem1Row = ctx.readNumber("CURRITEM1ROW", 0);
        selBlk = ctx.readNumber("SELBLK", 0);

        isSalesPartOk = ctx.readFlag("ISSALESPARTOK", false);
        isSecurityChecked = ctx.readFlag("ISSECURITYCHECKED", false);

	sWoStatus = ctx.readValue("WOSTATUS", "");

        dateMask = mgr.getFormatMask("Date",true); 
        dateformat = mgr.getFormatMask("Date",true);

	if (mgr.commandBarActivated()){
            eval(mgr.commandBarFunction());

	    int up_wo_no = toInt(upperset.getRow().getNumberValue("UP_WO_NO"));

	    if ("HEAD.Delete".equals(mgr.readValue("__COMMAND")))
	    {
	       if ((headset.countRows() > 0)) 
	       {
		   headset.refreshRow();
	       }
	       else
	       {
		   //mgr.redirectTo("WorkOrderRequisHeaderRMB.page?WO_NO="+upperset.getRow().getNumberValue("UP_WO_NO"));
		   mgr.redirectTo("WorkOrderRequisHeaderRMB.page?WO_NO="+ up_wo_no);
	       }
                
            }

	        
	}
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (mgr.buttonPressed("CREATENEWREQ"))
            createNewRequisition("1");
        else if (mgr.buttonPressed("ADDEXISTING"))
            addExisting();
        else if (mgr.buttonPressed("CANCELREQ"))
            cancel();
        else if (mgr.buttonPressed("AUTOSELECT"))
            autoSelect();
        else if (mgr.buttonPressed("ADDTOORDER"))
            addToOrder(false);
        else if (mgr.buttonPressed("CREATENEW"))
            createNewOrder();
        else if (mgr.buttonPressed("CANCEL"))
            cancel();
        else if ( mgr.buttonPressed("CRESUPPCANCEL") )
            cancelCreateSupplierForPart();
        else if ( mgr.buttonPressed("CRESUPPOK") )
            createSupplierForPart();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
        {
            up_wo_no    = toInt(mgr.readNumberValue("WO_NO"));
            up_mch_code = mgr.readValue("MCH_CODE", "");
            mch_name    = mgr.readValue("DESCRIPTION", "");
            cus_no      = mgr.readValue("CUSTOMER_NO", "");
            agree_id    = mgr.readValue("AGREEMENT_ID", "");
	    sWoStatus   = mgr.readValue("OBJSTATE");
	    
	    reportCust  = true;
            startup();
            searchWorkOrder();
        }
        else if (mgr.dataTransfered())
        {
            ASPBuffer buff= mgr.getTransferedData();
            eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME", upperset.getRow())); 

            ASPBuffer row = buff.getBufferAt(0);

            if (row.itemExists("WO_NO"))
                up_wo_no = toInt(row.getNumberValue("WO_NO"));
            if (row.itemExists("MCH_CODE"))
                up_mch_code = row.getValue("MCH_CODE");
            if (row.itemExists("MCH_NAME"))
                mch_name = row.getValue("MCH_NAME");
            if (row.itemExists("REQUISITION_NO"))
                sRequisitionNo = row.getValue("REQUISITION_NO");
            if (row.itemExists("CUSTOMER_NO"))
                cus_no = row.getValue("CUSTOMER_NO");
            if (row.itemExists("AGREEMENT_ID"))
                agree_id = row.getValue("AGREEMENT_ID");
	    if (row.itemExists("OBJSTATE"))
                sWoStatus = row.getValue("OBJSTATE");

	    startup();
            searchWorkOrder();   
        }
        else if ("WITHOUTSNOK".equals(mgr.readValue("BUTTONVAL")))
        {            
            saveReturn0Confirmed();
        }
        else if("WITHOUTCDOK".equals(mgr.readValue("BUTTONVALCD")))
        {
            saveReturn0Confirmed();
        }
        else if("WITHOUTSNINHANDOK".equals(mgr.readValue("BUTTONVALSNNOTINHAND")))
        {
            saveReturn0Confirmed();
        }

        checkObjAvaileble();
        adjust();

        tabs.saveActiveTab();

        ctx.writeValue("CTX3", dateformat);
        ctx.writeValue("CTX7", strAppOwner);
        ctx.writeNumber("CTX8", buffWoNo);
        ctx.writeNumber("CTX9", up_wo_no);
        ctx.writeValue("CTX11", cus_no);
        ctx.writeValue("CTX12", agree_id);
        ctx.writeFlag("REPORTCUST", reportCust);
        ctx.writeValue("EDITFLAG", editFlag);
        ctx.writeFlag("ACTENA0", actEna0);
        ctx.writeFlag("ACTENA1", actEna1);
        ctx.writeFlag("ACTENA2", actEna2);
        ctx.writeFlag("ACTENA3", actEna3);
        ctx.writeFlag("ACTENA4", actEna4);
        ctx.writeFlag("ACTENA5", actEna5);
        ctx.writeFlag("ACTENA6", actEna6);
        ctx.writeFlag("ACTENA7", actEna7);
        ctx.writeFlag("ACTENA8", actEna8);



        ctx.writeFlag("AGAIN", again);
        ctx.writeValue("DATEMASK", dateMask); 

        ctx.writeFlag("REQTOREQ", reqtoreq);
        ctx.writeFlag("REQTOORD", reqtoord);
        ctx.writeFlag("FROMRMB", fromRmb);
        ctx.writeValue("SITEMCONTRACT", sItemContract);
        ctx.writeValue("SITEMPARTNO", sItemPartNo);
        ctx.writeValue("SITEMORIGINALQTY", sItemOriginalQty);
        ctx.writeValue("SREQUISITIONNO", sRequisitionNo);
        ctx.writeValue("SLINENO", sLineNo);
        ctx.writeValue("SRELEASENO", sReleaseNo);
        ctx.writeValue("SITEMWANTEDDELIVERYDATE", sItemWantedDeliveryDate);
        ctx.writeValue("SITEMPROJECTID", sItemProjectId);
        ctx.writeNumber("ITEMVALUE", itemValue);

        ctx.writeNumber("CURRHEADROW", currHeadRow);
        ctx.writeNumber("CURRITEM0ROW", currItem0Row);
        ctx.writeNumber("CURRITEM1ROW", currItem1Row);
        ctx.writeNumber("SELBLK", selBlk);

        if (objIdBuffer0 != null)
            ctx.writeBuffer("OBJIDBUFFER0", objIdBuffer0);
        if (objIdBuffer1 != null)
            ctx.writeBuffer("OBJIDBUFFER1", objIdBuffer1);
        if (selectedRowsBuffer!=null)
            ctx.writeBuffer("SELROWSBUF",selectedRowsBuffer);

        ctx.writeFlag("ISSALESPARTOK", isSalesPartOk);
        ctx.writeFlag("ISSECURITYCHECKED", isSecurityChecked);
        ctx.writeValue("QRYSTR",qrystr);
        ctx.writeFlag("CREATESUPP", createSupp );
	
	ctx.writeValue("WOSTATUS", sWoStatus);
    }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
// This function is used to check security status for methods which
// are in modules Order,Purch and Invent .This function is called before
// the required method is performed. 

    public boolean checksec( String method,int ref) 
    {
        String[] splitted;
        ASPManager mgr = getASPManager();

        boolean isSecure[] = new boolean[6] ;

        isSecure[ref] = false ; 
        splitted = split(method,".");

        ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer ();
        secBuff.addSecurityQuery(splitted[0],splitted[1]);

        secBuff = mgr.perform(secBuff);

        if (secBuff.getSecurityInfo().itemExists(method))
        {
            isSecure[ref] = true;
            return true; 
        }
        else
            return false;
    }

    public boolean checksec1( String method,int ref,boolean[]isSecure) 
    {
        String[] splitted;
        ASPManager mgr = getASPManager();

        isSecure[ref] = false ; 
        splitted = split(method,".");

        ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer ();
        secBuff.addSecurityQuery(splitted[0],splitted[1]);

        secBuff = mgr.perform(secBuff);

        if (secBuff.getSecurityInfo().itemExists(method))
        {
            isSecure[ref] = true;
            return true; 
        }
        else
            return false;
    }

    public void cancel()
    {
        sLineNo = "";
        sReleaseNo = "";
        sItemContract = "";
        sItemPartNo = "";
        sItemOriginalQty = "";
        sItemWantedDeliveryDate = "";

        reqtoreq = false;
        reqtoord = false;
        isChecked = false;
        orderset.clear();
        lineset.clear();
        requestset.clear();
        reqlineset.clear();
        requestlay.setLayoutMode(ASPBlockLayout.NONE);

        goToRows();
    }

    /** Adding line to an existing requisition */
    public void addExisting()
    {
        ASPManager mgr = getASPManager();

        requestset.changeRow();
        if (!isValidRequisitionNo())
            mgr.showAlert(mgr.translate("PCMWWORKORDERREQUISHEADERRMBNOTRNO: Invalid Requisition Number."));
        else
            createNewRequisition("2");
    }

    /** Checkes the entered requisition no is valid or not */
    public boolean isValidRequisitionNo()
    {
        String newReqNo = requestset.getValue("ADDTO_REQUEST");
        int noRows = reqlineset.countRows();

        for (int i = 0 ; i < noRows ; i++)
        {
            if (reqlineset.getValue("LINE_INQUIRY_NO").equals(newReqNo))
                return true;
            else
                reqlineset.next();
        }
        return false;
    }

    public void  createNewSupplier() throws FndException
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
        ASPCommand cmd;
        createSupp = true;
        itemset0.changeRow();

        cmd = trans.addCustomFunction("SGC", "Site_API.Get_Company", "COMPANY");
        cmd.addParameter("TEMP_NULL",headset.getValue("CONTRACT"));
        trans = mgr.perform(trans);

        String suppCompany  = trans.getValue("SGC/DATA/COMPANY"); 
        String suppCurrCode;
        String suppTaxRegime;
        String suppTaxable;
        String suppVatCode;
        String suppVatCodeDesc;
        trans.clear();
        if (isPurchInst())
        {

            cmd = trans.addCustomFunction( "GETCURR", "Supplier_Api.Get_Currency_Code", "CRESUPP_CURR_CODE" );
            cmd.addParameter("TEMP_NULL",itemset0.getValue("VENDOR_NO"));


            cmd = trans.addCustomFunction("IIIGTR", "Purchase_Part_Supplier_Api.Get_Tax_Regime_Db", "CRESUPP_TAX_REGIME");
            cmd.addParameter("TEMP_NULL",headset.getValue("CONTRACT"));
            cmd.addParameter("TEMP_NULL",itemset0.getValue("VENDOR_NO"));

            cmd = trans.addCustomFunction("PPGT", "Purchase_Part_API.Get_Taxable_Db", "CRESUPP_TAXABLE");
            cmd.addParameter("TEMP_NULL",headset.getValue("CONTRACT"));
            cmd.addParameter("TEMP_NULL",itemset0.getValue("PART_NO"));

            cmd = trans.addCustomFunction("PPHFC", "Purchase_Part_API.Get_Fee_Code", "CRESUPP_VAT_CODE");
            cmd.addParameter("TEMP_NULL",headset.getValue("CONTRACT"));
            cmd.addParameter("TEMP_NULL",itemset0.getValue("PART_NO"));

            cmd = trans.addCustomFunction("SFGD", "Statutory_Fee_API.Get_Description", "CRESUPP_VAT_CODE_DESC");
            cmd.addParameter("COMPANY", suppCompany);
            cmd.addReference("CRESUPP_VAT_CODE","PPHFC/DATA");

            trans = mgr.perform(trans);
            suppCurrCode     = trans.getValue("GETCURR/DATA/CRESUPP_CURR_CODE");
            suppTaxRegime    = trans.getValue("IIIGTR/DATA/CRESUPP_TAX_REGIME");       
            suppTaxable      = trans.getValue("PPGT/DATA/CRESUPP_TAXABLE");       
            suppVatCode      = trans.getValue("PPHFC/DATA/CRESUPP_VAT_CODE");       
            suppVatCodeDesc  = trans.getValue("SFGD/DATA/CRESUPP_VAT_CODE_DESC");       
        }
        else
        {
            suppCurrCode="";      
            suppTaxRegime="";      
            suppTaxable="";      
            suppVatCode="";      
            suppVatCodeDesc="";      
        }

        ASPBuffer data;
        data = mgr.newASPBuffer();
        createSuppset.addRow(data);
        createSuppset.setValue("CRESUPP_COMPANY",suppCompany);
        createSuppset.setValue("CRESUPP_CONTRACT",headset.getValue("CONTRACT"));
        createSuppset.setValue("CRESUPP_TAX_REGIME",suppTaxRegime);
        createSuppset.setValue("CRESUPP_PART_NO",itemset0.getValue("PART_NO"));
        createSuppset.setValue("CRESUPP_DESCRIPTION",itemset0.getValue("NOTE_TEXT"));
        createSuppset.setValue("CRESUPP_SUPPLIER",itemset0.getValue("VENDOR_NO"));
        createSuppset.setValue("CRESUPP_UM","");
        createSuppset.setValue("CRESUPP_CURR_CODE",suppCurrCode);
        createSuppset.setNumberValue("CRESUPP_PRICE",mgr.getASPField("CRESUPP_PRICE").parseNumber("0"));
        createSuppset.setValue("CRESUPP_PRICE_UNIT_MEAS","");
        createSuppset.setNumberValue("CRESUPP_PRICE_CONV_FACTOR","");
        createSuppset.setValue("CRESUPP_TAXABLE",suppTaxable);
        createSuppset.setValue("CRESUPP_VAT_CODE",suppVatCode);
        createSuppset.setValue("CRESUPP_VAT_CODE_DESC",suppVatCodeDesc);
        createSuppset.setValue("CRESUPP_INCL_TAX_CURR_CODE",suppCurrCode);
        createSuppset.setValue("CRESUPP_TAX_PERCENTAGE","");

        if (("SALETX".equals(createSuppset.getValue("CRESUPP_TAX_REGIME")))||("NOTFOUND".equals(createSuppset.getValue("CRESUPP_TAX_REGIME"))))
        {
            mgr.getASPField("CRESUPP_VAT_CODE").setHidden();
            mgr.getASPField("CRESUPP_VAT_CODE_DESC").setHidden();
            mgr.getASPField("CRESUPP_PRICE_INCL_TAX").setHidden();
            mgr.getASPField("CRESUPP_INCL_TAX_CURR_CODE").setHidden();
            mgr.getASPField("CRESUPP_TAXABLE").setHidden();
        }
        if ("FALSE".equals(createSuppset.getValue("CRESUPP_TAXABLE")))
        {
            mgr.getASPField("CRESUPP_PRICE_INCL_TAX").setReadOnly();
        }
    }


    public void  createSupplierForPart()
    {
        ASPManager mgr = getASPManager();
        ASPCommand cmd;
        createSuppset.changeRow();

        if (mgr.isEmpty(createSuppset.getValue("CRESUPP_PRICE")))
            mgr.showAlert (mgr.translate("PCMWWORKORDRREQUISHEADERRMBNOPRICE: The field [Price] must have  a value"));
        else if (mgr.isEmpty(createSuppset.getValue("CRESUPP_UM")))
            mgr.showAlert(mgr.translate("PCMWWORKORDRREQUISHEADERRMBNOUM: The field [Unit Meas] must have  a value"));
        else
        {
            if (isPurchInst())
            {
                cmd = trans.addCustomFunction("FND_BOOLEAN_TRUE", "Fnd_Boolean_API.decode","DUMMY");
                cmd.addParameter("DUMMY","TRUE");
                cmd = trans.addCustomCommand("MAKESUP", "Purchase_Part_Supplier_API.New");
                cmd.addParameter("CONTRACT",headset.getValue("CONTRACT"));
                cmd.addParameter("CRESUPP_PART_NO",createSuppset.getValue("CRESUPP_PART_NO"));
                cmd.addParameter("CRESUPP_SUPPLIER",createSuppset.getValue("CRESUPP_SUPPLIER"));
                cmd.addParameter("CRESUPP_UM",createSuppset.getValue("CRESUPP_UM"));
                cmd.addParameter("CRESUPP_CURR_CODE",createSuppset.getValue("CRESUPP_CURR_CODE"));
                cmd.addParameter("CRESUPP_PRICE",mgr.formatNumber("CRESUPP_PRICE",createSuppset.getNumberValue("CRESUPP_PRICE")));
                cmd.addParameter("CRESUPP_PRICE_CONV_FACTOR",createSuppset.getValue("CRESUPP_PRICE_CONV_FACTOR"));
                cmd.addParameter("CRESUPP_PRICE_UNIT_MEAS",createSuppset.getValue("CRESUPP_PRICE_UNIT_MEAS"));
                cmd.addParameter("DUMMY","0");
                cmd.addParameter("DUMMY","0");
                cmd.addParameter("DUMMY","0");
                cmd.addParameter("DUMMY");
                cmd.addReference("DUMMY","FND_BOOLEAN_TRUE/DATA");

                trans = mgr.perform(trans);
            }
            createSupp = false;
            createSuppset.clear();
        }
    }


    public void  cancelCreateSupplierForPart()
    {
        createSuppset.clear();
        createSupp = false;
    }

    public boolean isPurchInst()
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();
        ASPCommand cmd = mgr.newASPCommand();
        String modVersion;

        cmd = trans1.addCustomFunction("MODVERS","module_api.get_version","MODULENAME");
        cmd.addParameter("MODULENAME","PURCH");

        trans1 = mgr.perform(trans1);
        modVersion = trans1.getValue("MODVERS/DATA/MODULENAME");

        if ( !mgr.isEmpty(modVersion) )
            return true;
        else
            return false;
    }

    /** This invokes when the user presses the "Create New" button in "Requisition to Requset" dialog */
    public void createNewRequisition( String sOption)
    {
        ASPManager mgr = getASPManager();
        ASPCommand cmd;
        String newInquiry = "";
        String newRequisitionNo;

        int j = 0;
        StringTokenizer stLineNo      = new StringTokenizer(sLineNo);
        StringTokenizer stReleaseNo   = new StringTokenizer(sReleaseNo);
        StringTokenizer stContract    = new StringTokenizer(sItemContract);
        StringTokenizer stPartNo      = new StringTokenizer(sItemPartNo);
        StringTokenizer stOriginalQty = new StringTokenizer(sItemOriginalQty);
        StringTokenizer stWanDelDate  = new StringTokenizer(sItemWantedDeliveryDate);

        requestset.changeRow();

        if ("1".equals(sOption) &&   mgr.isEmpty(requestset.getRow().getFieldValue("EXPIRY_DATE")))
        {
            mgr.showAlert(mgr.translate("PCMWWORKORDERREQUISHEADERRMBNULLDATE: The latest reply date must have a value"));
        }
        else
        {
            newRequisitionNo = ((!fromRmb) ? requestset.getValue("ADDTO_REQUEST") : reqlineset.getValue("LINE_INQUIRY_NO")) ;
            trans.clear();

            while (stLineNo.hasMoreTokens())
            {
                String temp_contract = stContract.nextToken();

                if ("1".equals(sOption)) //  Option for create new Requisition
                {
                    cmd = trans.addCustomCommand("CINQ" + j, "Inquiry_Order_API.Create_Inquiry");
                    cmd.addParameter("LINE_INQUIRY_NO");
                    cmd.addParameter("CONTRACT", temp_contract);
                    cmd.addParameter("REQ_BUYER_CODE", requestset.getValue("REQ_BUYER_CODE"));
                    cmd.addParameter("DATE_EXPIRES", requestset.getRow().getFieldValue("EXPIRY_DATE"));
                    cmd.addParameter("REQ_CURRENCY_CODE", requestset.getValue("REQ_CURRENCY_CODE"));
                    cmd.addParameter("ITEM0_PROJECT_ID", sItemProjectId);
                    cmd.addParameter("REQUISITION_NO", headset.getValue("REQUISITION_NO"));
                }

                cmd = trans.addCustomCommand("CREQINQ" + j, "Inquiry_Line_Part_Order_API.Create_Req_Inquiry");
                if ("1".equals(sOption))
                    cmd.addReference("LINE_INQUIRY_NO","CINQ" + j + "/DATA");
                else if ("2".equals(sOption))
                    cmd.addParameter("LINE_INQUIRY_NO", newRequisitionNo);

                cmd.addParameter("CONTRACT", temp_contract);
                cmd.addParameter("PART_NO", (itemValue == 0)?stPartNo.nextToken():sItemPartNo );
                cmd.addParameter("ORIGINAL_QTY", stOriginalQty.nextToken());
                cmd.addParameter("REQ_BUY_UNIT_MEAS", requestset.getValue("REQ_BUY_UNIT_MEAS"));
                cmd.addParameter("WANTEDDELIVERYDATE", stWanDelDate.nextToken());
                cmd.addParameter("REQUISITION_NO", sRequisitionNo);
                cmd.addParameter("LINE_NO", stLineNo.nextToken());
                cmd.addParameter("RELEASE_NO", stReleaseNo.nextToken());

                j++;
            }

            trans = mgr.perform(trans);

            for (int i = 0; i < j; i++)
            {
                newInquiry = newInquiry + trans.getValue("CINQ" + i + "/DATA/LINE_INQUIRY_NO") + ", ";
            }

            createNewRequest(itemValue); // Change the state of the line


            if ("1".equals(sOption))
                mgr.showAlert(mgr.translate("PCMWWORKORDERREQUISHEADERRMBNEWCRTEDREQNO: Created Request No &1.", newInquiry));
            else if ("2".equals(sOption))
                mgr.showAlert(mgr.translate("PCMWWORKORDERREQUISHEADERRMBADDEXTLN: Lines successfully added to the Request No &1", newRequisitionNo));

            reqtoreq = false;
            fromRmb  = false;

            // Ok, we dont need the requestlay any more.
            requestlay.setLayoutMode(ASPBlockLayout.NONE);
        }
    }

    /** This function markes the selected row with required method */
    public void createNewRequest(int item)
    {
        ASPManager mgr = getASPManager();
        ASPCommand cmd;

        int j = 0;
        requestset.clear();

        StringTokenizer stLineNo    = new StringTokenizer(sLineNo);
        StringTokenizer stReleaseNo = new StringTokenizer(sReleaseNo);

        trans.clear();

        if (item == 0)
        {
            while (stLineNo.hasMoreTokens())
            {
                cmd = trans.addCustomCommand("REQTOREQ0" + j, "WORK_ORDER_REQUIS_LINE_API.Requisition_To_Request");
                cmd.addParameter("REQUISITION_NO", headset.getValue("REQUISITION_NO"));
                cmd.addParameter("LINE_NO", stLineNo.nextToken());
                cmd.addParameter("RELEASE_NO", stReleaseNo.nextToken());

                j++;
            }
        }
        else if (item == 1)
        {
            while (stLineNo.hasMoreTokens())
            {
                cmd = trans.addCustomCommand("REQTOREQ1" + j, "WORK_ORDER_REQUIS_LINE_API.Requisition_To_Request");
                cmd.addParameter("REQUISITION_NO", headset.getValue("REQUISITION_NO"));
                cmd.addParameter("LINE_NO", stLineNo.nextToken());
                cmd.addParameter("RELEASE_NO", stReleaseNo.nextToken());

                j++;
            }
        }

        sLineNo = "";
        sReleaseNo = "";
        sItemContract = "";
        sItemPartNo = "";
        sItemOriginalQty = "";
        sItemWantedDeliveryDate = "";

        int currentRow = headset.getCurrentRowNo();
        mgr.submit(trans);
        headset.goTo(currentRow);

        if (item == 0)
            okFindITEM0();
        else if (item == 1)
            okFindITEM1();
    }

    /** Invokes this when press Add to Order RMB in "Requisition to Request" dialog detail part */
    public void addToOrderRequisition()
    {
        fromRmb = true;
        reqlineset.store();
        createNewRequisition("2");
    }

    public void startup()
    {
        ASPManager mgr = getASPManager();
        ASPCommand cmd;
        cmd = trans.addCustomFunction("APPOWN","Fnd_Session_API.Get_App_Owner","APPOWNER");
        trans = mgr.perform(trans);
        strAppOwner = trans.getValue("APPOWN/DATA/APPOWNER");
        trans.clear();  
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();

        boolean isSecure[] = new boolean[6] ;

        ASPCommand cmd;
        int ref;
        double qtyRequired;
        String priceListNo;
        String val;
        String strDesc;
        String strSup;
        String strUnim;
        String strCat;
        String strCatDesc;
        String txt;
        String sOrderType;
        double listPrice;
        double salePriceAmnt;
        double nDiscount; 
        double baseUnitPrice;
        double currencyRate;
        String activeInd = "";

        val = mgr.readValue("VALIDATE");

        if ("PART_NO".equals(val))
        {
            String sCondiCodeUsage ="";
            String sDefCondiCode ="";
            String sConditionCode = mgr.readValue("CONDITION_CODE");
            String buyer = mgr.readValue("BUYER_CODE");
            String sConditionCodeDesc = "";
            if (checksec1("Purchase_Part_API.Get_Description", 1, isSecure))
            {
                cmd = trans.addCustomFunction("DESCR", "Purchase_Part_API.Get_Description", "DESCRIPTION");
                cmd.addParameter("ITEM0_CONTRACT",mgr.readValue("ITEM0_CONTRACT", ""));
                cmd.addParameter("PART_NO");
            }

            if (checksec1("Purchase_Part_Supplier_API.Get_Primary_Supplier_No", 2, isSecure))
            {
                cmd = trans.addCustomFunction("SUPNO", "Purchase_Part_Supplier_API.Get_Primary_Supplier_No", "VENDOR_NO");
                cmd.addParameter("CONTRACT",mgr.readValue("ITEM0_CONTRACT", ""));
                cmd.addParameter("PART_NO");
            }

            if ( checksec1("Inventory_Part_API.Get_Unit_Meas",3,isSecure) )
            {
                cmd = trans.addCustomFunction("UNITM","Inventory_Part_API.Get_Unit_Meas","UNIT_MEAS");
                cmd.addParameter("ITEM0_CONTRACT",mgr.readValue("ITEM0_CONTRACT", ""));
                cmd.addParameter("PART_NO");                 
            }

            if (checksec1("Sales_Part_API.Get_Catalog_No_For_Part_No", 4, isSecure))
            {
                cmd = trans.addCustomFunction("CATNO", "Sales_Part_API.Get_Catalog_No_For_Part_No", "CATALOG_NO");
                cmd.addParameter("ITEM0_CONTRACT",mgr.readValue("ITEM0_CONTRACT", ""));
                cmd.addParameter("PART_NO");
                // Bug 57598, start
                cmd = trans.addCustomFunction("GETACT","Sales_Part_API.Get_Activeind","ACTIVEIND");
                cmd.addParameter("ITEM0_CONTRACT",mgr.readValue("ITEM0_CONTRACT", ""));
                cmd.addReference("CATALOG_NO","CATNO/DATA");
                cmd = trans.addCustomFunction("GETACTDB","Active_Sales_Part_API.Encode","ACTIVEIND_DB");
                cmd.addReference("ACTIVEIND","GETACT/DATA");
                // Bug 57598, end
            }
            
            cmd = trans.addCustomFunction("SUPCONTACT", "Purchase_Part_Supplier_API.Get_Contact", "CONTACT");
            cmd.addParameter("ITEM0_CONTRACT",mgr.readValue("ITEM0_CONTRACT", ""));
            cmd.addParameter("PART_NO");
            cmd.addReference("VENDOR_NO", "SUPNO/DATA");

            cmd = trans.addCustomFunction("CL1","Address_Type_Code_API.Get_Client_Value(1)","CLIENT_VAL");

            cmd = trans.addCustomFunction("SUPPADDNO","Supplier_Address_Api.Get_Address_No","ADD_NO");
            cmd.addReference("VENDOR_NO","SUPNO/DATA");
            cmd.addReference("CLIENT_VAL","CL1/DATA");

            cmd = trans.addCustomFunction("SUPPADDE","Supplier_Address_API.Get_Contact","CONTACT");
            cmd.addReference("VENDOR_NO","SUPNO/DATA");
            cmd.addReference("ADD_NO","SUPPADDNO/DATA");


            cmd = trans.addCustomFunction("GETPROCTYPE", "Purchase_Part_API.Get_Process_Type", "PROCESS_TYPE");
            cmd.addParameter("ITEM0_CONTRACT",mgr.readValue("ITEM0_CONTRACT", ""));
            cmd.addParameter("PART_NO");

            cmd = trans.addCustomFunction("GETPROCTYPEDESC", "ORDER_PROC_TYPE_API.Get_Description", "ORDERPROCDESC");
            cmd.addReference("PROCESS_TYPE", "GETPROCTYPE/DATA");

            cmd = trans.addCustomFunction("GETVENDNAME", "Supplier_API.Get_Vendor_Name", "VENDORNAME");
            cmd.addReference("VENDOR_NO", "SUPNO/DATA");

            cmd = trans.addCustomFunction("GETTECHCOORDID", "Purchase_Part_API.Get_Tech_Coordinator_Id", "TECHNICAL_COORDINATOR_ID");
            cmd.addParameter("ITEM0_CONTRACT",mgr.readValue("ITEM0_CONTRACT", ""));
            cmd.addParameter("PART_NO");

            cmd = trans.addCustomFunction("GETBUYERCODE1", "Purchase_Part_API.Get_Buyer_Code", "BUYER_CODE");
            cmd.addParameter("ITEM0_CONTRACT",mgr.readValue("ITEM0_CONTRACT", ""));
            cmd.addParameter("PART_NO");

            cmd = trans.addCustomFunction("GETBUYERCODE0","User_Default_API.Get_Buyer_Code","BUYER_CODE");

            cmd = trans.addCustomFunction("GETBUYERCODE2","Supplier_API.Get_Buyer_Code","BUYER_CODE");
            cmd.addReference("VENDOR_NO", "SUPNO/DATA");

            String sCompanyOwned = "COMPANY OWNED";
            cmd = trans.addCustomFunction("COMPOWNED", "Part_Ownership_API.Decode", "PART_OWNERSHIP");
            cmd.addParameter("PART_OWNERSHIP", sCompanyOwned);

            trans = mgr.validate(trans);

            ref = 0;


            if (isSecure[ref += 1])
            {
                strDesc = trans.getValue("DESCR/DATA/DESCRIPTION");
            }
            else
                strDesc = "";

            if (isSecure[ref += 1])
            {
                strSup = trans.getValue("SUPNO/DATA/VENDOR_NO");
            }
            else
                strSup = "";

            if (isSecure[ref += 1])
            {
                strUnim = trans.getValue("UNITM/DATA/UNIT_MEAS");
            }
            else
                strUnim = ""; 

            if (isSecure[ref += 1])
            {
                strCat = trans.getValue("CATNO/DATA/CATALOG_NO");
                activeInd = trans.getValue("GETACTDB/DATA/ACTIVEIND_DB");
            }
            else
                strCat = "";  

            String contact = trans.getValue("SUPCONTACT/DATA/CONTACT");

            if (mgr.isEmpty(contact))
                contact = trans.getValue("SUPPADDE/DATA/CONTACT");

            String procType = trans.getValue("GETPROCTYPE/DATA/PROCESS_TYPE");
            String procTypeDesc = trans.getValue("GETPROCTYPEDESC/DATA/ORDERPROCDESC");
            String vendorName = trans.getValue("GETVENDNAME/DATA/VENDORNAME");
            String techCoord = trans.getValue("GETTECHCOORDID/DATA/TECHNICAL_COORDINATOR_ID");

            if ( mgr.isEmpty(buyer) )
                buyer = trans.getValue("GETBUYERCODE0/DATA/BUYER_CODE");
            if ( mgr.isEmpty(buyer) )
                buyer = trans.getValue("GETBUYERCODE1/DATA/BUYER_CODE");
            if ( mgr.isEmpty(buyer) )
                buyer = trans.getValue("GETBUYERCODE2/DATA/BUYER_CODE");

            sOrderType = mgr.readValue("ITEM0_ORDER_CODE");
            String ownership_ = "";
            String sOwner = "";

            if (("6".equals(sOrderType)) && ("".equals(mgr.readValue("PART_OWNERSHIP"))))
            {
                ownership_ = trans.getValue("COMPOWNED/DATA/PART_OWNERSHIP");   
                sOwner = "";
            }
            
            if ("6".equals(sOrderType))
                strCat = "";

            trans.clear();
            cmd = trans.addCustomFunction("CONDCODEDB","PART_CATALOG_API.Get_Condition_Code_Usage_Db","CONDITION_CODE");
            cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));

            cmd = trans.addCustomFunction("DEFCONDCODE","CONDITION_CODE_API.Get_Default_Condition_Code","CONDITION_CODE");

            if (!mgr.isEmpty ("SERIAL_NO"))
            {

                String sInventSerTrack ="";
                String sAfterDelSerTrack = "";

                cmd = trans.addCustomFunction("INVTRACK","PART_CATALOG_API.Get_Serial_Tracking_Code_Db","INV_TRACK");
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));

                cmd = trans.addCustomFunction("AFTTRACK","PART_CATALOG_API.Get_Eng_Serial_Tracking_Db","AFT_TRACK");
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));

                cmd = trans.addCustomFunction("CHECKEXIST","PART_SERIAL_CATALOG_API.Check_Exist","CHECK_EXIST");
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
                cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));

                cmd = trans.addCustomFunction("CONDITIONCODE","PART_SERIAL_CATALOG_API.Get_Condition_Code","CONDITION_CODE");
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
                cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
            }
            trans = mgr.validate(trans);
            if ("ALLOW_COND_CODE".equals(trans.getValue("CONDCODEDB/DATA/CONDITION_CODE")))
            {
                sDefCondiCode = trans.getValue("DEFCONDCODE/DATA/CONDITION_CODE");

                if (!mgr.isEmpty ("SERIAL_NO"))
                {
                    String sInventSerTrack = trans.getValue("INVTRACK/DATA/INV_TRACK");
                    String sAfterDelSerTrack = trans.getValue("AFTTRACK/DATA/AFT_TRACK");
                    if ("SERIAL TRACKING".equals(sInventSerTrack) && "SERIAL TRACKING".equals(sAfterDelSerTrack))
                    {
                        String sSerialExist = trans.getValue("CHECKEXIST/DATA/CHECK_EXIST");

                        if ("TRUE".equals(sSerialExist))
                        {
                            sConditionCode = trans.getValue("CONDITIONCODE/DATA/CONDITION_CODE");
                            if (mgr.isEmpty (sConditionCode))
                                sConditionCode = sDefCondiCode;
                        }
                        else
                            sConditionCode = sDefCondiCode;
                    }
                    else
                        sConditionCode = sDefCondiCode;
                }
                else
                    sConditionCode = sDefCondiCode ;

                trans.clear();
                cmd = trans.addCustomFunction("CONDCODEDESC","CONDITION_CODE_API.Get_Description","CONDITIONCODEDESC");
                cmd.addParameter("CONDITION_CODE",sConditionCode);

                trans = mgr.validate(trans);
                sConditionCodeDesc =  trans.getValue("CONDCODEDESC/DATA/CONDITIONCODEDESC");

            }
            
            txt = (mgr.isEmpty(strDesc) ? "" : (strDesc)) + "^" + 
                  (mgr.isEmpty(strSup) ? "" : (strSup)) + "^" +
                  (mgr.isEmpty(strUnim) ? "" :strUnim)  + "^" +
                  (mgr.isEmpty(strCat) ? "" :strCat) + "^" + 
                  (mgr.isEmpty(contact) ? "" :contact) + "^" +
                  (mgr.isEmpty(procType) ? "" :procType) + "^" +
                  (mgr.isEmpty(procTypeDesc) ? "" :procTypeDesc) + "^" + 
                  (mgr.isEmpty(vendorName) ? "" :vendorName) + "^" +
                  (mgr.isEmpty(techCoord) ? "" :techCoord) + "^" +
                  (mgr.isEmpty(buyer) ? "" :buyer) + "^" +
                  (mgr.isEmpty(ownership_) ? "" :ownership_) + "^" +
                  (mgr.isEmpty(sOwner) ? "" :sOwner) + "^"+
                  (mgr.isEmpty(sConditionCode) ? "" : (sConditionCode))+ "^"+
                  (mgr.isEmpty(sConditionCodeDesc) ? "" : (sConditionCodeDesc))+ "^"+
                  (mgr.isEmpty(activeInd) ? "" : (activeInd))+ "^";
            mgr.responseWrite(txt);  
        }
        else if ("CATALOG_NO".equals(val))
        {
            listPrice = 0;
            salePriceAmnt = 0;
            priceListNo = "";
            nDiscount = 0;

            cmd = trans.addCustomFunction("GETPRICELISTFUNC", "Customer_Order_Pricing_Api.Get_Valid_Price_List", "PRICE_LIST_NO");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addParameter("CATALOG_NO");
            cmd.addParameter("CUSTOMER_NO");
            cmd.addParameter("HEAD_CURRENCEY_CODE");

            trans = mgr.validate(trans);

            priceListNo = trans.getValue("GETPRICELISTFUNC/DATA/PRICE_LIST_NO");

            trans.clear();

            if (checksec1("SALES_PART_API.Get_Catalog_Desc", 1, isSecure))
            {
                cmd = trans.addCustomFunction("CATDESC", "SALES_PART_API.Get_Catalog_Desc", "CATALOGDESC");
                cmd.addParameter("CATALOG_CONTRACT");
                cmd.addParameter("CATALOG_NO");
            }

            cmd = trans.addCustomCommand("PRICEINFO", "Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("BASE_UNIT_PRICE", "0");
            cmd.addParameter("SALE_UNIT_PRICE", "0");
            cmd.addParameter("DISCOUNT", "0");
            cmd.addParameter("CURRENCY_RATE", "0");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addParameter("CATALOG_NO");
            cmd.addParameter("CUSTOMER_NO");
            cmd.addParameter("AGREEMENT_ID");                          
            cmd.addParameter("PRICE_LIST_NO", priceListNo);
            cmd.addParameter("ORIGINAL_QTY");

            trans = mgr.validate(trans);

            priceListNo = trans.getValue("GETPRICELISTFUNC/DATA/PRICE_LIST_NO");

            if (mgr.isEmpty(priceListNo))
                priceListNo = trans.getValue("PRICEINFO/DATA/PRICE_LIST_NO");

            nDiscount = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");   

            if (isNaN(nDiscount))
                nDiscount = 0;

            listPrice = trans.getNumberValue("PRICEINFO/DATA/SALE_UNIT_PRICE");  

            if (isNaN(listPrice))
                listPrice = 0;

            qtyRequired = mgr.readNumberValue("ORIGINAL_QTY");

            if (isNaN(qtyRequired))
                qtyRequired = 0;

            salePriceAmnt = ( listPrice - (nDiscount / 100 * listPrice) ) * qtyRequired;

            ref = 0;     

            if (isSecure[ref += 1])
                strCatDesc = trans.getValue("CATDESC/DATA/CATALOGDESC");
            else
                strCatDesc = "";

            String s_listPrice = mgr.formatNumber("ITEM0_LISTPRICE", listPrice);
            String s_salePriceAmnt = mgr.formatNumber("ITEM0_SALESPRICEAMOUNT", salePriceAmnt);
            String s_nDiscount = mgr.formatNumber("ITEM0_DISCOUNT", nDiscount);

            txt = (mgr.isEmpty(strCatDesc) ? "" : (strCatDesc)) + "^" + s_listPrice + "^" + s_salePriceAmnt + "^" + (mgr.isEmpty(priceListNo)?"":priceListNo) + "^" + s_nDiscount + "^"; 
            mgr.responseWrite(txt);
        }
        else if ("VENDOR_NO".equals(val))
        {
            String sVenName;
            String buyer = mgr.readValue("BUYER_CODE");

            double nPartExist = 0;
            double nSupplierExist = 0;

            cmd = trans.addCustomFunction( "VENNO", "Supplier_API.Get_Vendor_Name", "VENDORNAME" );
            cmd.addParameter("VENDOR_NO");

            cmd = trans.addCustomFunction("UNITM", "Purchase_Part_Supplier_API.Get_Buy_Unit_Meas", "UNIT_MEAS");
            cmd.addParameter("ITEM0_CONTRACT");
            cmd.addParameter("PART_NO");
            cmd.addParameter("VENDOR_NO");

            cmd = trans.addCustomFunction("GETCONTACT", "Purchase_Part_Supplier_API.Get_Contact", "CONTACT");
            cmd.addParameter("ITEM0_CONTRACT");
            cmd.addParameter("PART_NO");
            cmd.addParameter("VENDOR_NO");

            cmd = trans.addCustomFunction("GETBUYERCODE0","User_Default_API.Get_Buyer_Code","BUYER_CODE");

            cmd = trans.addCustomFunction("GETBUYERCODE1","Purchase_Part_API.Get_Buyer_Code","BUYER_CODE");
            cmd.addParameter("ITEM0_CONTRACT");
            cmd.addParameter("PART_NO");

            cmd = trans.addCustomFunction("GETBUYERCODE2", "Supplier_API.Get_Buyer_Code", "BUYER_CODE");
            cmd.addParameter("VENDOR_NO");

            if (isPurchInst())
            {
                cmd = trans.addCustomFunction("PARTEXIST", "Purchase_Part_API.Check_Exist", "PART_EXIST");
                cmd.addParameter("ITEM0_CONTRACT,PART_NO");
                cmd = trans.addCustomFunction("SUPPEXIST", "Purchase_Part_Supplier_API.Exist_N", "SUPP_EXIST");
                cmd.addParameter("ITEM0_CONTRACT,PART_NO,VENDOR_NO");
            }

            trans = mgr.validate(trans);

            sVenName = trans.getValue("VENNO/DATA/VENDORNAME");
            String unitMeas = trans.getValue("UNITM/DATA/UNIT_MEAS");
            String contact = trans.getValue("GETCONTACT/DATA/CONTACT");
            if ( mgr.isEmpty(buyer) )
                buyer = trans.getValue("GETBUYERCODE0/DATA/BUYER_CODE");
            if ( mgr.isEmpty(buyer) )
                buyer = trans.getValue("GETBUYERCODE1/DATA/BUYER_CODE");
            if (mgr.isEmpty(buyer))
                buyer = trans.getValue("GETBUYERCODE2/DATA/BUYER_CODE");

            if (isPurchInst())
            {
                nPartExist = trans.getNumberValue("PARTEXIST/DATA/PART_EXIST");
                nSupplierExist = trans.getNumberValue("SUPPEXIST/DATA/SUPP_EXIST");
            }
            else
            {
                nPartExist = 0;
                nSupplierExist = 0;
            }
            if ( ( nPartExist == 1 ) && ( nSupplierExist == 0 ) &&  ! mgr.isEmpty(mgr.readValue("VENDOR_NO")) )
                // Seperate dialog should be popped up to assign a supplier for the part
                txt = "NO_SUPPLIER_EXIST" + "^";
            else
                txt = (mgr.isEmpty(sVenName) ? "" : sVenName) + "^" +
                      (mgr.isEmpty(unitMeas) ? "" : unitMeas) + "^" +
                      (mgr.isEmpty(contact) ? "" : contact) + "^" +
                      (mgr.isEmpty(buyer) ? "" : buyer) + "^";

            mgr.responseWrite(txt);
        }

        else if ("CONDITION_CODE".equals(val))
        {
            String sConditionDescription;

            trans.clear();

            cmd = trans.addCustomFunction("GETCONDDESC","CONDITION_CODE_API.Get_Description","CONDITIONCODEDESC");
            cmd.addParameter("CONDITION_CODE", mgr.readValue("CONDITION_CODE"));

            trans = mgr.validate(trans);

            sConditionDescription = trans.getValue("GETCONDDESC/DATA/CONDITIONCODEDESC");

            txt = (mgr.isEmpty(sConditionDescription) ? "" : (sConditionDescription)) + "^";

            mgr.responseWrite(txt);
        }

        else if ("ORDER_CODE".equals(val))
        {
            String sOrderCode = mgr.readValue("ORDER_CODE");
            String sOrderCodeDesc;
            String sExtSrv = "FALSE";

            if (( "1".equals(sOrderCode) ) ||  ( "5".equals(sOrderCode) ) ||  ( "6".equals(sOrderCode) ))
            {
                cmd = trans.addCustomFunction( "ORD", "Purchase_Order_Type_API.Get_Description", "ORDERCODEDESC" );
                cmd.addParameter("ORDER_CODE");

                trans = mgr.validate(trans);

                sOrderCodeDesc = trans.getValue("ORD/DATA/ORDERCODEDESC");
                if ("6".equals(sOrderCode))
                   sExtSrv = "TRUE";
                else
                   sExtSrv = "FALSE";
                
                txt = (mgr.isEmpty(sOrderCodeDesc) ? "" : sOrderCodeDesc) + "^" + (mgr.isEmpty(sExtSrv) ? "" : sExtSrv) + "^";
            }
            else
                txt = "No_Data_Found" + mgr.translate("PCMWWORKORDERREQUISHEADERRMBMSSGE: Valid order codes are 1,5 and 6");

            mgr.responseWrite(txt);
        }
        else if ("EXTERNAL_SERVICE".equals(val))
        {
           String sExtSrv = mgr.readValue("EXTERNAL_SERVICE");
           String sOrderCode;
           String sOrderCodeDesc;

           if (sExtSrv != null && (sExtSrv.equals("TRUE")))
              sOrderCode = "6";
           else
              sOrderCode = null;

           if (sOrderCode != null){
	           cmd = trans.addCustomFunction( "ORD", "Purchase_Order_Type_API.Get_Description", "ORDERCODEDESC" );
	           cmd.addParameter("ORDER_CODE", sOrderCode);
	
	           trans = mgr.validate(trans);
	
	           sOrderCodeDesc = trans.getValue("ORD/DATA/ORDERCODEDESC");
           }
           else
        	   sOrderCodeDesc = null;

           txt = (mgr.isEmpty(sOrderCode) ? "" : sOrderCode) + "^" + (mgr.isEmpty(sOrderCodeDesc) ? "" : sOrderCodeDesc) + "^";
           mgr.responseWrite(txt);
        }

        else if ("DESTINATION_ID".equals(val))
        {
            String sDestDesc;
            String sDestId = mgr.readValue("DESTINATION_ID");
            String sContract = mgr.readValue("CONTRACT");

            cmd = trans.addCustomFunction( "DESTID", "INTERNAL_DESTINATION_API.Get_Description", "INTERNAL_DESTINATION" );
            cmd.addParameter("CONTRACT", sContract);
            cmd.addParameter("DESTINATION_ID", sDestId);

            trans = mgr.validate(trans);               

            sDestDesc = trans.getValue("DESTID/DATA/INTERNAL_DESTINATION");
            txt = (mgr.isEmpty(sDestDesc) ? "" : sDestDesc) + "^";

            mgr.responseWrite(txt);
        }
        else if ("PROCESS_TYPE".equals(val))
        {
            cmd = trans.addCustomFunction("ORDPROTY", "ORDER_PROC_TYPE_API.Get_Description", "ORDERPROCDESC");
            cmd.addParameter("PROCESS_TYPE");

            trans = mgr.validate(trans);

            String sOrdPrDesc = trans.getValue("ORDPROTY/DATA/ORDERPROCDESC");
            txt = (mgr.isEmpty(sOrdPrDesc) ? "" : sOrdPrDesc) + "^";

            mgr.responseWrite(txt);
        }
        else if ("ITEM1_CATALOG_NO".equals(val))
        {
            listPrice = 0;
            salePriceAmnt = 0;
            priceListNo = "";
            nDiscount = 0;

            cmd = trans.addCustomFunction("GETPRICELISTFUNC", "Customer_Order_Pricing_Api.Get_Valid_Price_List", "ITEM1_PRICE_LIST_NO");
            cmd.addParameter("ITEM1_CATALOG_CONTRACT");
            cmd.addParameter("ITEM1_CATALOG_NO");
            cmd.addParameter("ITEM1_CUSTOMER_NO");
            cmd.addParameter("HEAD_CURRENCEY_CODE");

            trans = mgr.validate(trans);

            priceListNo = trans.getBuffer("GETPRICELISTFUNC/DATA").getFieldValue("ITEM1_PRICE_LIST_NO");

            trans.clear();

            if (checksec1("SALES_PART_API.Get_Catalog_Desc", 1, isSecure))
            {
                cmd = trans.addCustomFunction("CATDESC1", "SALES_PART_API.Get_Catalog_Desc", "ITEM1_CATALOGDESC");
                cmd.addParameter("ITEM1_CATALOG_CONTRACT");
                cmd.addParameter("ITEM1_CATALOG_NO");
            }

            cmd = trans.addCustomCommand("PRICEINFO", "Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("BASE_UNIT_PRICE");
            cmd.addParameter("SALE_UNIT_PRICE");
            cmd.addParameter("DISCOUNT");
            cmd.addParameter("CURRENCY_RATE");
            cmd.addParameter("ITEM1_CATALOG_CONTRACT");
            cmd.addParameter("ITEM1_CATALOG_NO");
            cmd.addParameter("ITEM1_CUSTOMER_NO");
            cmd.addParameter("ITEM1_AGREEMENT_ID");
            cmd.addParameter("ITEM1_PRICE_LIST_NO", priceListNo);
            cmd.addParameter("ITEM1_ORIGINALQTY");

            trans = mgr.validate(trans);

            if (mgr.isEmpty(priceListNo))
                priceListNo = trans.getBuffer("PRICEINFO/DATA").getFieldValue("ITEM1_PRICE_LIST_NO");

            nDiscount = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");   
            if (isNaN(nDiscount))
                nDiscount = 0;

            listPrice = trans.getNumberValue("PRICEINFO/DATA/SALE_UNIT_PRICE");  
            if (isNaN(listPrice))
                listPrice = 0;

            qtyRequired = mgr.readNumberValue("ITEM1_ORIGINALQTY");
            if (isNaN(qtyRequired))
                qtyRequired = 0;

            salePriceAmnt = ( listPrice - (nDiscount / 100 * listPrice) ) * qtyRequired;

            ref = 0;     

            if (isSecure[ref += 1])
                strCatDesc = trans.getValue("CATDESC1/DATA/ITEM1_CATALOGDESC");
            else
                strCatDesc = "";

            String s_listPrice = mgr.formatNumber("ITEM1_LISTPRICE", listPrice);
            String s_salePriceAmnt = mgr.formatNumber("ITEM1_SALESPRICEAMOUNT", salePriceAmnt);
            String s_nDiscount = mgr.formatNumber("DISCOUNT", nDiscount);

            txt = (mgr.isEmpty(strCatDesc) ? "" : (strCatDesc)) + "^" +(mgr.isEmpty(strCatDesc) ? "" : (strCatDesc)) + "^" + s_listPrice + "^" + s_salePriceAmnt + "^" + (mgr.isEmpty(priceListNo)?"":priceListNo) + "^" + s_nDiscount + "^"; 
	    mgr.responseWrite(txt);
        }
        else if ("ORIGINAL_QTY".equals(val))
        {
            listPrice = 0;
            salePriceAmnt = 0;
            priceListNo = "";
            nDiscount = 0;

            if ((!mgr.isEmpty(mgr.readValue("CATALOG_NO"))) && (!mgr.isEmpty(mgr.readValue("ORIGINAL_QTY"))))
            {
                cmd = trans.addCustomCommand("PRICEINFO","Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("BASE_UNIT_PRICE");
                cmd.addParameter("SALE_UNIT_PRICE");
                cmd.addParameter("DISCOUNT");
                cmd.addParameter("CURRENCY_RATE");
                cmd.addParameter("CATALOG_CONTRACT");
                cmd.addParameter("CATALOG_NO");
                cmd.addParameter("CUSTOMER_NO");
                cmd.addParameter("AGREEMENT_ID");
                cmd.addParameter("PRICE_LIST_NO");
                cmd.addParameter("ORIGINAL_QTY");

                trans = mgr.validate(trans);

                if (mgr.isEmpty(mgr.readValue("PRICE_LIST_NO")))
                    priceListNo = trans.getValue("PRICEINFO/DATA/PRICE_LIST_NO");
                else
                    priceListNo = mgr.readValue("PRICE_LIST_NO");

                nDiscount = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");   
                if (isNaN(nDiscount))
                    nDiscount = 0;

                listPrice = trans.getNumberValue("PRICEINFO/DATA/SALE_UNIT_PRICE");  
                if (isNaN(listPrice))
                    listPrice = 0;

                qtyRequired = mgr.readNumberValue("ORIGINAL_QTY");
                if (isNaN(qtyRequired))
                    qtyRequired = 0;

                salePriceAmnt = ( listPrice - (nDiscount / 100 * listPrice )) * qtyRequired;
            }

            String s_listPrice = mgr.formatNumber("ITEM0_LISTPRICE", listPrice);
            String s_salePriceAmnt = mgr.formatNumber("ITEM0_SALESPRICEAMOUNT", salePriceAmnt);
            String s_nDiscount = mgr.formatNumber("ITEM0_DISCOUNT", nDiscount);

            txt = (mgr.isEmpty(priceListNo)?"":priceListNo) + "^" + s_nDiscount + "^" + s_listPrice + "^" + s_salePriceAmnt + "^" ;
            mgr.responseWrite(txt);
        }
        else if ("PRICE_LIST_NO".equals(val))
        {
            listPrice = 0;
            salePriceAmnt = 0;
            priceListNo = "";
            nDiscount = 0;

            if ((!mgr.isEmpty(mgr.readValue("CATALOG_NO"))) && (!mgr.isEmpty(mgr.readValue("ORIGINAL_QTY"))))
            {
                cmd = trans.addCustomCommand("PRICEINFO", "Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("BASE_UNIT_PRICE");
                cmd.addParameter("SALE_UNIT_PRICE");
                cmd.addParameter("DISCOUNT");
                cmd.addParameter("CURRENCY_RATE");
                cmd.addParameter("CATALOG_CONTRACT");
                cmd.addParameter("CATALOG_NO");
                cmd.addParameter("CUSTOMER_NO");
                cmd.addParameter("AGREEMENT_ID");
                cmd.addParameter("PRICE_LIST_NO");
                cmd.addParameter("ORIGINAL_QTY");   

                trans = mgr.validate(trans);         

                priceListNo = trans.getValue("PRICEINFO/DATA/PRICE_LIST_NO");

                nDiscount = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");   
                if (isNaN(nDiscount))
                    nDiscount = 0;

                listPrice = trans.getNumberValue("PRICEINFO/DATA/SALE_UNIT_PRICE");  
                if (isNaN(listPrice))
                    listPrice = 0;

                qtyRequired = mgr.readNumberValue("ORIGINAL_QTY");
                if (isNaN(qtyRequired))
                    qtyRequired = 0;

                salePriceAmnt = ( listPrice - (nDiscount / 100 * listPrice )) * qtyRequired;
            }

            String s_listPrice = mgr.formatNumber("ITEM0_LISTPRICE", listPrice);
            String s_salePriceAmnt = mgr.formatNumber("ITEM0_SALESPRICEAMOUNT", salePriceAmnt);
            String s_nDiscount = mgr.formatNumber("ITEM0_DISCOUNT", nDiscount);

            txt = (mgr.isEmpty(priceListNo)?"":priceListNo) + "^" + s_nDiscount + "^" + s_listPrice + "^" + s_salePriceAmnt + "^" ;
            mgr.responseWrite(txt);
        }
        else if ("ITEM1_ORIGINALQTY".equals(val))
        {
            listPrice = 0;
            salePriceAmnt = 0;
            priceListNo = "";
            nDiscount = 0;

            if ((!mgr.isEmpty(mgr.readValue("ITEM1_CATALOG_NO"))) && (!mgr.isEmpty(mgr.readValue("ITEM1_ORIGINALQTY"))))
            {
                cmd = trans.addCustomCommand("PRICEINFO", "Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("BASE_UNIT_PRICE");
                cmd.addParameter("SALE_UNIT_PRICE");
                cmd.addParameter("DISCOUNT");
                cmd.addParameter("CURRENCY_RATE");
                cmd.addParameter("ITEM1_CATALOG_CONTRACT");
                cmd.addParameter("ITEM1_CATALOG_NO");
                cmd.addParameter("ITEM1_CUSTOMER_NO");
                cmd.addParameter("ITEM1_AGREEMENT_ID");
                cmd.addParameter("ITEM1_PRICE_LIST_NO");
                cmd.addParameter("ITEM1_ORIGINALQTY");

                trans = mgr.validate(trans);

                if (mgr.isEmpty(mgr.readValue("ITEM1_PRICE_LIST_NO")))
                    priceListNo = trans.getBuffer("PRICEINFO/DATA").getFieldValue("ITEM1_PRICE_LIST_NO");
                else
                    priceListNo = mgr.readValue("ITEM1_PRICE_LIST_NO");

                nDiscount = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");   
                if (isNaN(nDiscount))
                    nDiscount = 0;

                listPrice = trans.getNumberValue("PRICEINFO/DATA/SALE_UNIT_PRICE");  
                if (isNaN(listPrice))
                    listPrice = 0;

                qtyRequired = mgr.readNumberValue("ITEM1_ORIGINALQTY");
                if (isNaN(qtyRequired))
                    qtyRequired = 0;

                salePriceAmnt = ( listPrice - (nDiscount / 100 * listPrice )) * qtyRequired;
            }

            String s_listPrice = mgr.formatNumber("ITEM1_LISTPRICE", listPrice);
            String s_salePriceAmnt = mgr.formatNumber("ITEM1_SALESPRICEAMOUNT", salePriceAmnt);
            String s_nDiscount = mgr.formatNumber("DISCOUNT", nDiscount);

            txt = (mgr.isEmpty(priceListNo)?"":priceListNo) + "^" + s_nDiscount + "^" + s_listPrice + "^" + s_salePriceAmnt + "^" ;
            mgr.responseWrite(txt);
        }
        else if ("ITEM1_PRICE_LIST_NO".equals(val))
        {
            listPrice = 0;
            salePriceAmnt = 0;
            priceListNo = "";
            nDiscount = 0;

            if ((!mgr.isEmpty(mgr.readValue("ITEM1_CATALOG_NO"))) && (!mgr.isEmpty(mgr.readValue("ITEM1_ORIGINALQTY"))))
            {
                cmd = trans.addCustomCommand("PRICEINFO", "Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("BASE_UNIT_PRICE");
                cmd.addParameter("SALE_UNIT_PRICE");
                cmd.addParameter("DISCOUNT");
                cmd.addParameter("CURRENCY_RATE");
                cmd.addParameter("ITEM1_CATALOG_CONTRACT");
                cmd.addParameter("ITEM1_CATALOG_NO");
                cmd.addParameter("ITEM1_CUSTOMER_NO");
                cmd.addParameter("ITEM1_AGREEMENT_ID");
                cmd.addParameter("ITEM1_PRICE_LIST_NO");
                cmd.addParameter("ITEM1_ORIGINALQTY");   

                trans = mgr.validate(trans);         

                priceListNo = trans.getBuffer("PRICEINFO/DATA").getFieldValue("ITEM1_PRICE_LIST_NO");

                nDiscount = trans.getNumberValue("PRICEINFO/DATA/DISCOUNT");   
                if (isNaN(nDiscount))
                    nDiscount = 0;

                listPrice = trans.getNumberValue("PRICEINFO/DATA/SALE_UNIT_PRICE");  
                if (isNaN(listPrice))
                    listPrice = 0;

                qtyRequired = mgr.readNumberValue("ITEM1_ORIGINALQTY");
                if (isNaN(qtyRequired))
                    qtyRequired = 0;

                salePriceAmnt = ( listPrice - (nDiscount / 100 * listPrice )) * qtyRequired;
            }

            String s_listPrice = mgr.formatNumber("ITEM1_LISTPRICE", listPrice);
            String s_salePriceAmnt = mgr.formatNumber("ITEM1_SALESPRICEAMOUNT", salePriceAmnt);
            String s_nDiscount = mgr.formatNumber("DISCOUNT", nDiscount);

            txt = (mgr.isEmpty(priceListNo)?"":priceListNo) + "^" + s_nDiscount + "^" + s_listPrice + "^" + s_salePriceAmnt + "^" ;
            mgr.responseWrite(txt);
        }
        else if ("ITEM1_VENDORNO".equals(val))
        {
            cmd = trans.addCustomFunction( "VENNO", "Supplier_API.Get_Vendor_Name", "VENDORNAME" );
            cmd.addParameter("ITEM1_VENDORNO");

            String qGetContact = "SELECT Supplier_Address_API.Get_Contact(?, Supplier_Address_Api.Get_Address_No(?,Address_Type_Code_API.Get_Client_Value(1)))  VENDORCONTACT FROM DUAL ";
            ASPQuery q = trans.addQuery("GETCONTACT", qGetContact);
	    q.addParameter("CONTRACT", mgr.readValue("ITEM1_VENDORNO"));
	    q.addParameter("ADD_NO", mgr.readValue("ITEM1_VENDORNO"));

            trans = mgr.validate(trans);

            String sVenName = trans.getValue("VENNO/DATA/VENDORNAME");
            String contact = trans.getValue("GETCONTACT/DATA/VENDORCONTACT");

            //Bug 76748, start
            trans.clear();

            String taxableDb = mgr.readValue("TAXABLE_DB");
            String feeCode = null;
            String feeTypeDb = null;
            String feeCodeDesc = null;

            cmd = trans.addCustomFunction("PPSAGTRD", "Purchase_Part_Supplier_API.Get_Tax_Regime_Db", "ITEM1_TAX_REGIME");
            cmd.addParameter("ITEM1_CONTRACT");

            cmd = trans.addCustomFunction("SUPTAX", "Supplier_API.Supplier_Is_Taxable","TAX_EXEMPT");
            cmd.addParameter("ITEM1_COMPANY,ITEM1_VENDORNO");

            trans = mgr.validate(trans);

            String taxExempt = trans.getValue("SUPTAX/DATA/TAX_EXEMPT"); 
            String taxRegime = trans.getValue("PPSAGTRD/DATA/ITEM1_TAX_REGIME");

            if ("VAT".equals(taxRegime) || "MIX".equals(taxRegime) && "TRUE".equals(taxableDb))
            {
               if("TRUE".equals(taxExempt))
                  feeTypeDb = "VAT";
               else
                  feeTypeDb = "CALCVAT;NOTAX";
            }
            if ((mgr.isEmpty(mgr.readValue("FEE_CODE"))) && "TRUE".equals(taxableDb))
            {
                trans.clear();
                trans.addSecurityQuery("Supplier_API","Get_Default_Fee_Code");
    
                trans = mgr.validate(trans);
                ASPBuffer sec = trans.getSecurityInfo();
                trans.clear();
    
                if (sec.itemExists("Supplier_API.Get_Default_Fee_Code"))
                {                   
                   cmd = trans.addCustomFunction("IIIAGDVC", "Supplier_API.Get_Default_Fee_Code", "FEE_CODE");
                   cmd.addParameter("ITEM1_CONTRACT");
                   cmd.addParameter("ITEM1_VENDORNO");
                   
                   cmd = trans.addCustomFunction("SFGD", "Statutory_Fee_API.Get_Description", "FEEDESC");
                   cmd.addParameter("ITEM1_COMPANY");
                   cmd.addReference("FEE_CODE","IIIAGDVC/DATA");
                   
                   trans = mgr.validate(trans);
    
                   feeCode = trans.getValue("IIIAGDVC/DATA/FEE_CODE");   
                   feeCodeDesc = trans.getValue("SFGD/DATA/FEEDESC");
                   trans.clear();
                   
                   if (mgr.isEmpty(feeCode))
                   {
                      cmd = trans.addCustomFunction("CDIAGTC", "Company_Distribution_Info_API.Get_Tax_Code", "FEE_CODE");
                      cmd.addParameter("ITEM1_COMPANY");
    
                      cmd = trans.addCustomFunction("SFGD", "Statutory_Fee_API.Get_Description", "FEEDESC");
                      cmd.addParameter("ITEM1_COMPANY");
                      cmd.addReference("FEE_CODE","CDIAGTC/DATA");
    
                      trans = mgr.validate(trans);
    
                      feeCode = trans.getValue("CDIAGTC/DATA/FEE_CODE");   
                      feeCodeDesc = trans.getValue("SFGD/DATA/FEEDESC");   
                      trans.clear();
    
                   }
                }
            }
            txt = (mgr.isEmpty(sVenName) ? "" : sVenName) + "^" +
                  (mgr.isEmpty(contact) ? "" : contact) + "^" +
                  (mgr.isEmpty(feeCode) ? "" : feeCode) + "^" +
                  (mgr.isEmpty(feeCodeDesc) ? "" : feeCodeDesc) + "^" +
                  (mgr.isEmpty(feeTypeDb) ? "" : feeTypeDb) + "^" ;

            //Bug 76748, end

            mgr.responseWrite(txt);
        }
        //Bug 76748, start
        if ("FEE_CODE".equals(val))
        {

           cmd = trans.addCustomFunction("SFGD", "Statutory_Fee_API.Get_Description", "FEEDESC");
           cmd.addParameter("ITEM1_COMPANY,FEE_CODE");

           trans = mgr.validate(trans);

           String feeCodeDesc = trans.getValue("SFGD/DATA/FEEDESC");
           txt = (mgr.isEmpty(feeCodeDesc) ? "" : feeCodeDesc) + "^" ;
           mgr.responseWrite(txt);
        }
        //Bug 76748, end
        else if ("DEFAUTH".equals(val))
        {
            cmd = trans.addCustomFunction("GETAUTHNAME", "Order_Coordinator_API.Get_Name", "COOR_NAME");
            cmd.addParameter("DEFAUTH");

            trans = mgr.validate(trans);

            String sAuthName = trans.getValue("GETAUTHNAME/DATA/COOR_NAME");
            txt = (mgr.isEmpty(sAuthName) ? "" : sAuthName) + "^";

            mgr.responseWrite(txt);
        }
        else if ("DEFBUYER".equals(val))
        {
            cmd = trans.addCustomFunction("GETBUYERNAME", "Purchase_Buyer_API.Get_Name", "BUYER_NAME");
            cmd.addParameter("DEFBUYER");

            trans = mgr.validate(trans);

            String sBuyerName = trans.getValue("GETBUYERNAME/DATA/BUYER_NAME");
            txt = (mgr.isEmpty(sBuyerName) ? "" : sBuyerName) + "^";

            mgr.responseWrite(txt);
        }
        else if ("PART_OWNERSHIP".equals(val))
        {
            cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
            cmd.addParameter("PART_OWNERSHIP");
            trans = mgr.validate(trans);

            String sOwnershipDb = trans.getValue("VALIDOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");
            txt = (mgr.isEmpty(sOwnershipDb) ? "" : (sOwnershipDb)) + "^";  
            mgr.responseWrite(txt);
        }
        else if ("OWNING_CUSTOMER_NO".equals(val))
        {
            cmd = trans.addCustomFunction("GETOWNERNAME", "CUSTOMER_INFO_API.Get_Name", "OWNER_NAME");
            cmd.addParameter("OWNING_CUSTOMER_NO");

            trans = mgr.validate(trans);

            String sOwnerName = trans.getValue("GETOWNERNAME/DATA/OWNER_NAME");

            //txt = (mgr.isEmpty(sOwnerName) ? "" : (sOwnerName)) + "^" + (mgr.isEmpty(sWoCust) ? "": (sWoCust)) + "^";  
            txt = (mgr.isEmpty(sOwnerName) ? "" : (sOwnerName)) + "^";  
            mgr.responseWrite(txt);
        }
        else if ("SERVICE_TYPE".equals(val))
        {
            cmd = trans.addCustomFunction("GETSERVICEDESC", "External_Service_Type_API.Get_Description", "SERVICE_TYPE_DESC");
            cmd.addParameter("SERVICE_TYPE");

            trans = mgr.validate(trans);

            String sServiceDesc = trans.getValue("GETSERVICEDESC/DATA/SERVICE_TYPE_DESC");
            txt = (mgr.isEmpty(sServiceDesc) ? "" : (sServiceDesc)) + "^";  
            mgr.responseWrite(txt);
        }
        // 031107  ARWILK  Begin  (Bug#110151)
        else if ("ASSORTMENT".equals(val))
        {
            cmd = trans.addCustomFunction("GETASSORTDESC", "Supplier_Assortment_API.Get_Description", "ITEM1_SUPPLIER_ASSORTMENT_DESC");
            cmd.addParameter("ASSORTMENT");

            trans = mgr.validate(trans);

            String sAssortDescription = trans.getValue("GETASSORTDESC/DATA/ITEM1_SUPPLIER_ASSORTMENT_DESC");

            txt = (mgr.isEmpty(sAssortDescription) ? "" : (sAssortDescription)) + "^";  
            mgr.responseWrite(txt);
        }
        // 031107  ARWILK  End  (Bug#110151)
        else if ("ITEM1_LISTPRICE".equals(val))
        {
            listPrice = mgr.readNumberValue("ITEM1_LISTPRICE");
            nDiscount = mgr.readNumberValue("DISCOUNT");
            qtyRequired = mgr.readNumberValue("ITEM1_ORIGINALQTY");

            if (isNaN(listPrice))
                listPrice = 0;
            if (isNaN(nDiscount))
                nDiscount = 0;
            if (isNaN(qtyRequired))
                qtyRequired = 0;

            salePriceAmnt  = (listPrice - (nDiscount / 100 * listPrice)) * qtyRequired;

            if (isNaN(salePriceAmnt))
                salePriceAmnt = 0;

            String sSalesPriceAmt = mgr.getASPField("ITEM1_SALESPRICEAMOUNT").formatNumber(salePriceAmnt);
            String sListPrice = mgr.getASPField("ITEM1_LISTPRICE").formatNumber(listPrice);

            txt = (mgr.isEmpty(sSalesPriceAmt) ? "" : (sSalesPriceAmt)) + "^" +
                  (mgr.isEmpty(sListPrice) ? "" : (sListPrice)) + "^";  
            mgr.responseWrite(txt);
        }

        mgr.endResponse();
    }
//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void newRow()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd; 
        int beg_pos;
        int end_pos;   
        int lenofdate;
        String attribute;
        String lsAttr;
        String strContract = null;
        String reqDate;
        String strReqDate = null;
        String strRequisitioner = null;
        String strOrderCode = null;

        up_wo_no = toInt(upperset.getRow().getNumberValue("UP_WO_NO"));

        itemset0.clear();
        itemtbl0.clearQueryRow();
        itemset1.clear();
        itemtbl1.clearQueryRow();

        lsAttr = "REQUISITIONER_CODE" + (char)31  + (char)30 + "CONTRACT" + (char)31  + (char)30 + "REQUISITION_DATE" + (char)31 + (char)30;

        cmd = trans.addCustomCommand("PREPREQ", "WORK_ORDER_REQUIS_HEADER_API.New__"); 
        cmd.addParameter("INFO");    
        cmd.addParameter("OBJID");
        cmd.addParameter("OBJVERSION");
        cmd.addParameter("ATTR", lsAttr);
        cmd.addParameter("ACTION", "PREPARE");

        trans = mgr.perform(trans);

        ASPBuffer data = trans.getBuffer("PREPREQ/DATA");

        attribute = trans.getValue("PREPREQ/DATA/ATTR");

        StringTokenizer st1 = new StringTokenizer(attribute, ((char)30)+"");

        while (st1.hasMoreTokens())
        {
            String valuePair = st1.nextToken();

            StringTokenizer st2 = new StringTokenizer(valuePair, ((char)31)+"");

            if (st2.hasMoreTokens())
            {
                String fieldName = st2.nextToken();
                if (fieldName.indexOf("CONTRACT")>-1 && st2.hasMoreTokens())
                    strContract = st2.nextToken();

                if (fieldName.indexOf("REQUISITION_DATE")>-1 && st2.hasMoreTokens())
                    strReqDate = st2.nextToken();

                if (fieldName.indexOf("REQUISITIONER_CODE")>-1 && st2.hasMoreTokens())
                    strRequisitioner = st2.nextToken();

                if (fieldName.indexOf("ORDER_CODE")>-1 && st2.hasMoreTokens())
                    strOrderCode = st2.nextToken();
            }
        }                         

        trans.clear();
        cmd = trans.addQuery("FORMATDATE","SELECT to_date(?,'YYYY-MM-DD-hh24.MI.ss') REQUISITION_DATE FROM DUAL");
	cmd.addParameter("MCH_NAME",strReqDate);

        cmd = trans.addQuery("GETORDERCODEDESC","SELECT Purchase_Order_Type_API.Get_Description( ? ) ORDCODEDESC FROM DUAL");
	cmd.addParameter("ORDER_CODE",strOrderCode);

        if (mgr.isEmpty(strContract))
        {
            cmd = trans.addCustomFunction("GETSITE","Active_Separate_API.Get_Contract","UP_CONTRACT");
            cmd.addParameter("WO_NO",upperset.getRow().getValue("UP_WO_NO"));
        }

        trans = mgr.perform(trans);


        strReqDate = trans.getBuffer("FORMATDATE/DATA").getFieldValue("REQUISITION_DATE");
        String strOrderCodeDesc = trans.getValue("GETORDERCODEDESC/DATA/ORDCODEDESC");

        if (mgr.isEmpty(strContract))
            strContract = trans.getValue("GETSITE/DATA/UP_CONTRACT");

        data.setFieldItem("CONTRACT", strContract);
        data.setFieldItem("REQUISITION_DATE", strReqDate);
        data.setFieldItem("REQUISITIONER_CODE", strRequisitioner);
        data.setFieldItem("ORDER_CODE", strOrderCode);
        data.setFieldItem("ORDERCODEDESC", strOrderCodeDesc);
        data.setNumberValue("WO_NO", up_wo_no);  
        data.setFieldItem("MCH_CODE", upperset.getRow().getValue("UP_MCH_CODE"));  
        data.setFieldItem("HEAD_MCH_NAME", upperset.getRow().getValue("MCH_NAME"));
        headset.addRow(data); 

        eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME", upperset.getRow()));    
        eval(headblk.generateAssignments());

        up_wo_no = toInt(upperset.getRow().getNumberValue("UP_WO_NO"));
        up_mch_code = upperset.getRow().getValue("UP_MCH_CODE");
        mch_name = upperset.getRow().getValue("MCH_NAME");
    }

    public String checkNull(String val)
    {
        ASPManager mgr = getASPManager();
        return(mgr.isEmpty(val) ? "" : (val)); 
    }

    public void saveReturn()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd;
        int currrow;
        String reqno;
        String lsAttr;

        eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME", upperset.getRow()));  
        currrow = headset.getCurrentRowNo();
        trans.clear();
        buffWoNo =  toInt(upperset.getRow().getNumberValue("UP_WO_NO"));

        if ("New__".equals(headset.getRowStatus()))
        {
            headset.changeRow();
            lsAttr = "REQUISITIONER_CODE" + (char)31 + readField("REQUISITIONER_CODE") + (char)30;
            lsAttr = lsAttr + "CONTRACT" + (char)31 + checkNull(headset.getRow().getValue("CONTRACT")) + (char)30;
            lsAttr = lsAttr + "REQUISITION_DATE" + (char)31 + checkNull(headset.getRow().getValue("REQUISITION_DATE")) + (char)30;
            lsAttr = lsAttr + "ORDER_CODE" + (char)31 + checkNull(headset.getRow().getValue("ORDER_CODE")) + (char)30;
            lsAttr = lsAttr + "DESTINATION_ID" + (char)31 + checkNull(headset.getRow().getValue("DESTINATION_ID")) + (char)30;
            lsAttr = lsAttr + "INTERNAL_DESTINATION" + (char)31 + checkNull(headset.getRow().getValue("INTERNAL_DESTINATION")) + (char)30;
            lsAttr = lsAttr + "EXTERNAL_SERVICE" + (char)31 + headset.getRow().getValue("EXTERNAL_SERVICE") + (char)30;
            lsAttr = lsAttr + "WO_NO" + (char)31 + buffWoNo + (char)30; 

            cmd = trans.addCustomCommand("MODIFORM3", "WORK_ORDER_REQUIS_HEADER_API.New__"); 
            cmd.addParameter("INFO");    
            cmd.addParameter("OBJID", headset.getRow().getValue("OBJID"));
            cmd.addParameter("OBJVERSION", headset.getRow().getValue("OBJVERSION"));
            cmd.addParameter("ATTR",lsAttr);
            cmd.addParameter("ACTION","DO");

            trans = mgr.perform(trans);
            reqno = trans.getValue("MODIFORM3/DATA/ATTR");

            okFind11();

            for (int i = 0; i <= headset.countRows(); i++)
            {
                headset.goTo(i);
                eval(headblk.generateAssignments());
            }

            headset.goTo(currrow);
        }
        else
        {
            headset.changeRow();

            lsAttr = "REQUISITIONER_CODE" + (char)31 + checkNull(headset.getRow().getValue("REQUISITIONER_CODE")) + (char)30;
            lsAttr = lsAttr + "REQUISITION_DATE" + (char)31 + checkNull(headset.getRow().getValue("REQUISITION_DATE"))+ (char)30;
            lsAttr = lsAttr + "ORDER_CODE" + (char)31 + checkNull(headset.getRow().getValue("ORDER_CODE"))+ (char)30;
            lsAttr = lsAttr + "DESTINATION_ID" + (char)31 + checkNull(headset.getRow().getValue("DESTINATION_ID"))+ (char)30;
            lsAttr = lsAttr + "INTERNAL_DESTINATION" + (char)31 + checkNull(headset.getRow().getValue("INTERNAL_DESTINATION"))+ (char)30;

            cmd = trans.addCustomCommand("MODIFORM3", "WORK_ORDER_REQUIS_HEADER_API.Modify__"); 
            cmd.addParameter("INFO");    
            cmd.addParameter("OBJID", headset.getRow().getValue("OBJID"));
            cmd.addParameter("OBJVERSION", headset.getRow().getValue("OBJVERSION"));
            cmd.addParameter("ATTR", lsAttr);
            cmd.addParameter("ACTION", "DO");

            mgr.submit(trans);

            eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME", upperset.getRow()));  
            trans.clear();

            for (int i = 0; i <= headset.countRows(); i++)
            {
                headset.goTo(i);
                eval(headblk.generateAssignments());
            }

            headset.goTo(currrow);  
        }

        eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME", upperset.getRow()));  
        up_wo_no = toInt(upperset.getRow().getNumberValue("UP_WO_NO"));
        up_mch_code = upperset.getRow().getValue("UP_MCH_CODE");
        mch_name = upperset.getRow().getValue("MCH_NAME");
    }

    public void saveReturn0()
    {
        ASPManager mgr = getASPManager();
        ASPCommand cmd;
        
        boolean serialPart = false,conditionAllowed = false,exists = false;

        itemset0.changeRow();

        trans.clear();

        cmd = trans.addCustomFunction("CONDITIONCODEDB","PART_CATALOG_API.Get_Condition_Code_Usage_Db","CONDITION_CODE");
        cmd.addParameter("PART_NO",itemset0.getRow().getValue("PART_NO"));
                                                    
        if( "TRUE".equals(headset.getRow().getValue("EXTERNAL_SERVICE")) )
        {
            cmd = trans.addCustomFunction("INVTRACK","PART_CATALOG_API.Get_Serial_Tracking_Code_Db","INV_TRACK");
            cmd.addParameter("PART_NO",itemset0.getRow().getValue("PART_NO"));                
    
            cmd = trans.addCustomFunction("CHECKEXIST","PART_SERIAL_CATALOG_API.Check_Exist","CHECK_EXIST");
            cmd.addParameter("PART_NO",itemset0.getRow().getValue("PART_NO"));
            cmd.addParameter("SERIAL_NO",itemset0.getRow().getValue("SERIAL_NO"));
    
            trans = mgr.perform(trans);
                
            if ("SERIAL TRACKING".equals(trans.getValue("INVTRACK/DATA/INV_TRACK")))
                 serialPart = true;
            if ("ALLOW_COND_CODE".equals(trans.getValue("CONDITIONCODEDB/DATA/CONDITION_CODE"))) 
                 conditionAllowed = true;
            if( "TRUE".equals(trans.getValue("CHECKEXIST/DATA/CHECK_EXIST")))
                 exists = true;
    
            trans.clear();

            boolean saveReturnConfirmed = true;

            if( serialPart && (itemset0.getRow().getValue("SERIAL_NO") == null) )
            {
                withoutSn = "TRUE";
                ctx.setCookie( "Serial_Part_Sn", "TRUE" ); 
                itemlay0.setLayoutMode(itemlay0.NEW_LAYOUT);
                saveReturnConfirmed = false;
            }        
            else if( serialPart && !exists )
            {
                withoutSnInHand = "TRUE";
                ctx.setCookie( "SN_Not_In_Hand", "TRUE" );
                itemlay0.setLayoutMode(itemlay0.NEW_LAYOUT);
                saveReturnConfirmed = false;
            }          
            if( serialPart && conditionAllowed && (itemset0.getRow().getValue("CONDITION_CODE") == null))
            {
                withoutConditionCode = "TRUE";            
                ctx.setCookie( "Condition_Code", "TRUE" );
                itemlay0.setLayoutMode(itemlay0.NEW_LAYOUT);
                saveReturnConfirmed = false;
            }           
            if( saveReturnConfirmed )                            
                saveReturn0Confirmed();            
        }
        else
        {
            trans = mgr.perform(trans);

            if ("ALLOW_COND_CODE".equals(trans.getValue("CONDITIONCODEDB/DATA/CONDITION_CODE"))) 
                conditionAllowed = true;

            trans.clear();
            
            if( conditionAllowed && (itemset0.getRow().getValue("CONDITION_CODE") == null))
            {
                withoutConditionCode = "TRUE";            
                ctx.setCookie( "Condition_Code", "TRUE" );
                itemlay0.setLayoutMode(itemlay0.NEW_LAYOUT);
            }
            else
                saveReturn0Confirmed();
        }
            
    
    }

    public void saveReturn0Confirmed() 
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd;
        int currrow;
        int itemcurrrow;
        int itemcurrrow__=0;
        String information;    
        String lsAttr;
        int beg_pos;
        int end_pos;
        String info;
        String objid = null;

        eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME", upperset.getRow()));  
        currrow = headset.getCurrentRowNo();
        trans.clear();
        buffWoNo =  toInt(upperset.getRow().getNumberValue("UP_WO_NO"));

        itemcurrrow = itemset0.getCurrentRowNo();
        if (itemset1.countRows()>0)
            itemcurrrow__ = itemset1.getCurrentRowNo();

	if ("New__".equals(itemset0.getRowStatus()))
        {
            itemset0.changeRow();                   
            lsAttr = "WO_NO" + (char)31 + toInt(upperset.getRow().getNumberValue("UP_WO_NO")) + (char)30; 
            lsAttr = lsAttr + "REQUISITION_NO" + (char)31 + (mgr.isEmpty(headset.getRow().getValue("REQUISITION_NO")) ? "" :headset.getRow().getValue("REQUISITION_NO")) + (char)30;
            lsAttr = lsAttr + "CONTRACT" + (char)31 + readField("CONTRACT")+ (char)30;
            lsAttr = lsAttr + "PART_NO" + (char)31 + readField("PART_NO") + (char)30;
            lsAttr = lsAttr + "VENDOR_NO" + (char)31 + readField("VENDOR_NO") + (char)30;
            lsAttr = lsAttr + "CONTACT" + (char)31 + readField("CONTACT") + (char)30;
            lsAttr = lsAttr + "JOB_ID" + (char)31 + readField("JOB_ID") + (char)30;
            lsAttr = lsAttr + "UNIT_MEAS" + (char)31 + readField("UNIT_MEAS") + (char)30;
            lsAttr = lsAttr + "ORIGINAL_QTY" + (char)31 + readNumberField("ORIGINAL_QTY") + (char)30;
            lsAttr = lsAttr + "WANTED_RECEIPT_DATE" + (char)31 + itemset0.getValue("WANTED_RECEIPT_DATE").substring(0,10) + (char)30;
            //lsAttr = lsAttr + strAppOwner + ".Purchase_Req_Util_API.Get_Line_Latest_Order_Date(REQUISITION_NO, LINE_NO, RELEASE_NO)" + (char)31 + readField("LATESTORDERDATE") + (char)30; 
            lsAttr = lsAttr + "COST_RETURN" + (char)31 + readField("COST_RETURN") + (char)30;
            lsAttr = lsAttr + "BUYER_CODE" + (char)31 + readField("BUYER_CODE") + (char)30;
            lsAttr = lsAttr + "TECHNICAL_COORDINATOR_ID" + (char)31 + readField("TECHNIACATECHNICAL_COORDINATOR_IDL_COORDINATOR_ID") + (char)30;
            lsAttr = lsAttr + "PROCESS_TYPE" + (char)31 + readField("PROCESS_TYPE") + (char)30;
            lsAttr = lsAttr + "REQUEST_TYPE" + (char)31 + readField("REQUEST_TYPE") + (char)30;
            lsAttr = lsAttr + "BUY_UNIT_MEAS" + (char)31 + readField("BUY_UNIT_MEAS") + (char)30;
            lsAttr = lsAttr + "CATALOG_CONTRACT" + (char)31 + readField("CATALOG_CONTRACT") + (char)30;     
            lsAttr = lsAttr + "CATALOG_NO" + (char)31 + readField("CATALOG_NO") + (char)30;
            lsAttr = lsAttr + "PRICE_LIST_NO" + (char)31 + readField("PRICE_LIST_NO") + (char)30;
            lsAttr = lsAttr + "NOTE_TEXT" + (char)31 + readField("NOTE_TEXT") + (char)30;
            lsAttr = lsAttr + "DESCRIPTION" + (char)31 + readField("DESCRIPTION") + (char)30;
            //lsAttr = lsAttr + strAppOwner + ".PURCHASE_REQ_LINE_PART_API.Get_Part_Ownership(REQUISITION_NO, LINE_NO, RELEASE_NO)"+ (char)31 + readField("PART_OWNERSHIP") + (char)30;
            //lsAttr = lsAttr + "PART_OWNERSHIP_DB"+ (char)31 + readField("PART_OWNERSHIP_DB")+ (char)30;
            //lsAttr = lsAttr + strAppOwner + ".PURCHASE_REQ_LINE_PART_API.Get_Owning_Customer_No(REQUISITION_NO, LINE_NO, RELEASE_NO)"+ (char)31 + readField("OWNER") + (char)30;
            lsAttr = lsAttr + "SERIAL_NO"+ (char)31 + readField("SERIAL_NO") + (char)30;
            lsAttr = lsAttr + "LOT_BATCH_NO"+ (char)31 + readField("LOT_BATCH_NO") + (char)30;
            lsAttr = lsAttr + "SERVICE_TYPE"+ (char)31 + readField("SERVICE_TYPE") + (char)30;
            lsAttr = lsAttr + "CONDITION_CODE"+ (char)31 + readField("CONDITION_CODE")+ (char)30; //Bug 43608
            lsAttr = lsAttr + "IS_EXTERNAL_SERVICE"+ (char)31 + readField("EXTERNAL_SERVICE")+ (char)30;
            lsAttr = lsAttr + "CRAFT_LINE_NO"+ (char)31 + readField("CRAFT_LINE_NO")+ (char)30;

            itemset0.goTo(itemcurrrow); 

            cmd = trans.addCustomCommand("SUBMITFORM", "PART_WO_REQUIS_LINE_API.New__"); 
            cmd.addParameter("INFO");    
            cmd.addParameter("OBJID");
            cmd.addParameter("OBJVERSION");
            cmd.addParameter("ATTR", lsAttr);
            cmd.addParameter("ACTION", "DO");
            trans = mgr.perform(trans);            

            objid = trans.getValue("SUBMITFORM/DATA/OBJID");
            trans.clear(); 

            okFindITEM0(); 
            gotoNewRow(itemset0,objid);

            information = trans.getValue("SUBMITFORM/DATA/INFO");

            if (!mgr.isEmpty(information))
            {
                beg_pos = information.indexOf((char)31) + 1;
                end_pos = information.indexOf((char)30);
                info = information.substring(beg_pos, end_pos);
                mgr.showAlert(mgr.translate("PCMWWORKORDERREQUISHEADERRMBINFOM: &1", info));
            }
            itemlay0.setLayoutMode(itemlay0.SINGLE_LAYOUT);
        }
        else
        {
            //itemset0.changeRow();

            lsAttr = "CONTRACT" + (char)31 + readField("CONTRACT")+ (char)30;
            lsAttr = lsAttr + "PART_NO" + (char)31 + readField("PART_NO") + (char)30;
            lsAttr = lsAttr + "VENDOR_NO" + (char)31 + readField("VENDOR_NO") + (char)30;
            lsAttr = lsAttr + "CONTACT" + (char)31 + readField("CONTACT") + (char)30;
            lsAttr = lsAttr + "JOB_ID" + (char)31 + readField("JOB_ID") + (char)30;
            lsAttr = lsAttr + "UNIT_MEAS" + (char)31 + readField("UNIT_MEAS") + (char)30;
            lsAttr = lsAttr + "ORIGINAL_QTY" + (char)31 + readNumberField("ORIGINAL_QTY") + (char)30;
            lsAttr = lsAttr + "WANTED_RECEIPT_DATE" + (char)31 + itemset0.getValue("WANTED_RECEIPT_DATE").substring(0,10) + (char)30;
            //lsAttr = lsAttr + strAppOwner + ".Purchase_Req_Util_API.Get_Line_Latest_Order_Date(REQUISITION_NO, LINE_NO, RELEASE_NO)" + (char)31 + readField("LATESTORDERDATE") + (char)30;
            lsAttr = lsAttr + "COST_RETURN" + (char)31 + readField("COST_RETURN") + (char)30;
            lsAttr = lsAttr + "BUYER_CODE" + (char)31 + readField("BUYER_CODE") + (char)30;
            lsAttr = lsAttr + "TECHNICAL_COORDINATOR_ID" + (char)31 + readField("TECHNICAL_COORDINATOR_ID") + (char)30;
            lsAttr = lsAttr + "PROCESS_TYPE" + (char)31 + readField("PROCESS_TYPE") + (char)30;
            lsAttr = lsAttr + "REQUEST_TYPE" + (char)31 + readField("REQUEST_TYPE") + (char)30;
            lsAttr = lsAttr + "BUY_UNIT_MEAS" + (char)31 + readField("BUY_UNIT_MEAS") + (char)30;
            lsAttr = lsAttr + "CATALOG_CONTRACT" + (char)31 + itemset0.getRow().getValue("CATALOG_CONTRACT") + (char)30;     
            lsAttr = lsAttr + "CATALOG_NO" + (char)31 + readField("CATALOG_NO") + (char)30;
            lsAttr = lsAttr + "PRICE_LIST_NO" + (char)31 + readField("PRICE_LIST_NO") + (char)30;
            lsAttr = lsAttr + "NOTE_TEXT" + (char)31 + readField("NOTE_TEXT") + (char)30;
            lsAttr = lsAttr + "DESCRIPTION" + (char)31 + readField("DESCRIPTION") + (char)30;
            lsAttr = lsAttr + "PART_OWNERSHIP" + (char)31 + readField("PART_OWNERSHIP") + (char)30;
            lsAttr = lsAttr + "OWNING_CUSTOMER_NO" + (char)31 + readField("OWNING_CUSTOMER_NO") + (char)30;
            lsAttr = lsAttr + "SERIAL_NO" + (char)31 + readField("SERIAL_NO") + (char)30;
            lsAttr = lsAttr + "LOT_BATCH_NO" + (char)31 + readField("LOT_BATCH_NO") + (char)30;
            lsAttr = lsAttr + "SERVICE_TYPE" + (char)31 + readField("SERVICE_TYPE") + (char)30;
            lsAttr = lsAttr + "CONDITION_CODE"+ (char)31 + readField("CONDITION_CODE")+ (char)30;    //Bug 43608
            lsAttr = lsAttr + "IS_EXTERNAL_SERVICE"+ (char)31 + readField("EXTERNAL_SERVICE")+ (char)30;

            cmd = trans.addCustomCommand("MODIFORM3", "PART_WO_REQUIS_LINE_API.Modify__"); 
            cmd.addParameter("INFO");    
            cmd.addParameter("OBJID", itemset0.getRow().getValue("OBJID"));
            cmd.addParameter("OBJVERSION", itemset0.getRow().getValue("OBJVERSION"));
            cmd.addParameter("ATTR", lsAttr);
            cmd.addParameter("ACTION", "DO");

            trans = mgr.perform(trans);

            eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME", upperset.getRow()));  
            trans.clear();

            okFindITEM0();   
            itemset0.goTo(itemcurrrow);
            itemlay0.setLayoutMode(itemlay0.SINGLE_LAYOUT);
        }

        if (itemset1.countRows()>0)
            itemset1.goTo(itemcurrrow__);

        up_wo_no = toInt(upperset.getRow().getNumberValue("UP_WO_NO"));
        up_mch_code = upperset.getRow().getValue("UP_MCH_CODE");
        mch_name = upperset.getRow().getValue("MCH_NAME");
    }

    public String readField(String field_name)
    {
        ASPManager mgr = getASPManager();

        String field_val = mgr.readValue(field_name);
        if (mgr.isEmpty(field_val))
            field_val = "";

        return field_val;
    }

    public double readNumberField(String field_name)
    {
        ASPManager mgr = getASPManager();

        double field_val = mgr.readNumberValue(field_name);
        if (isNaN(field_val))
            field_val = 0;

        return field_val;
    }

    public void saveReturn1()
    {   
        ASPManager mgr = getASPManager();

        editFlag = "FALSE";

        ASPCommand cmd;
        int currrow;
        int itemcurrrow;
        int itemcurrrow__=0;
        String information = null;
        String lsAttr;
        int beg_pos;
        int end_pos;
        String info;
        String objid = null;

        eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME", upperset.getRow()));
        currrow = headset.getCurrentRowNo();
        trans.clear();
        buffWoNo =  toInt(upperset.getRow().getNumberValue("UP_WO_NO"));

        itemcurrrow = itemset1.getCurrentRowNo();
        if (itemset0.countRows()>0)
            itemcurrrow__ = itemset0.getCurrentRowNo();

        if ("New__".equals(itemset1.getRowStatus()))
        {   
            itemset1.changeRow();
            lsAttr = "WO_NO" + (char)31 + toInt(upperset.getRow().getNumberValue("UP_WO_NO")) + (char)30; 
            lsAttr = lsAttr + "REQUISITION_NO" + (char)31 + (mgr.isEmpty(headset.getRow().getValue("REQUISITION_NO")) ? "" : headset.getRow().getValue("REQUISITION_NO")) + (char)30;
            lsAttr = lsAttr + "CONTRACT" + (char)31 + readField("CONTRACT") + (char)30;
            lsAttr = lsAttr + "NOTE_TEXT" + (char)31 + readField("ITEM_NOTE_TEXT2") + (char)30;
            lsAttr = lsAttr + "STAT_GRP" + (char)31 + readField("STAT_GRP") + (char)30;
            lsAttr = lsAttr + "VENDOR_NO" + (char)31 + readField("ITEM1_VENDORNO") + (char)30;
            lsAttr = lsAttr + "CONTACT" + (char)31 + readField("ITEM1_VENDORCONTACT") + (char)30;
            lsAttr = lsAttr + "REQUEST_TYPE" + (char)31 + checkNull(itemset1.getRow().getValue("ITEM1_REQUEST_TYPE")) + (char)30;
            lsAttr = lsAttr + "WANTED_RECEIPT_DATE" + (char)31 +  itemset1.getValue("WANTED_RECEIPT_DATE").substring(0,10) + (char)30;        
            lsAttr = lsAttr + "ORIGINAL_QTY" + (char)31 + readNumberField("ITEM1_ORIGINALQTY") + (char)30;

            double unit_price = mgr.readNumberValue("FBUY_UNIT_PRICE");
            //Bug 40357,start
            if (!(mgr.isEmpty("ITEM1_VENDORNO")) && isNaN(unit_price))
            {
                unit_price = 0;
                mgr.showAlert(mgr.translate("PCMWWORKORDERREQUISHEADERRMBPRICECORRZERO: Price/Curr is set to zero."));
            }
            lsAttr = lsAttr + "FBUY_UNIT_PRICE" + (char)31 + unit_price + (char)30;

	    double sales_price = mgr.readNumberValue("ITEM1_LISTPRICE");

            if ( isNaN( sales_price ) ) {
               sales_price = 0;
	       itemset1.setNumberValue("SALES_PRICE",0.0);   
	    }
            
            double discount = mgr.readNumberValue("DISCOUNT");
            if (isNaN(discount))
                discount = 0;

            if (discount != 0)
                lsAttr = lsAttr + "LINE_DISCOUNT" + (char)31 + readNumberField("DISCOUNT") + (char)30;

            lsAttr = lsAttr + "LATEST_ORDER_DATE" + (char)31 +  itemset1.getValue("LATEST_ORDER_DATE").substring(0,10)+ (char)30;
            lsAttr = lsAttr + "COST_RETURN" + (char)31 + readField("ITEM1_COST_RETURN")+ (char)30;
            lsAttr = lsAttr + "BUY_UNIT_MEAS" + (char)31 + readField("ITEM1_BUYUNITMEAS")+ (char)30;
            lsAttr = lsAttr + "CATALOG_CONTRACT" + (char)31 + checkNull(itemset1.getRow().getValue("CATALOG_CONTRACT")) + (char)30;     
            lsAttr = lsAttr + "PRICE_TYPE_DB" + (char)31 + (mgr.isEmpty(readField("PRICE_TYPE_DB"))?"CORRECT":"ESTIMATED") + (char)30;
            lsAttr = lsAttr + "BUYER_CODE" + (char)31 +readField("ITEM1_BUYERCODE") + (char)30;
            lsAttr = lsAttr + "TECHNICAL_COORDINATOR_ID" + (char)31 + readField("ITEM1_TECHCOORDID") + (char)30;
            lsAttr = lsAttr + "CATALOG_NO" + (char)31 + readField("ITEM1_CATALOG_NO") + (char)30;
	    lsAttr = lsAttr + "PRICE_LIST_NO" + (char)31 + readField("ITEM1_PRICE_LIST_NO") + (char)30;
            lsAttr = lsAttr + "SALES_PRICE" + (char)31 + itemset1.getRow().getValue("SALES_PRICE") + (char)30;
            lsAttr = lsAttr + "DESCRIPTION" + (char)31 + readField("ITEM_NOTE_TEXT2") + (char)30;
            lsAttr = lsAttr + "NOTE_TEXT" + (char)31 + readField("ITEM1_NOTE_TEXT") + (char)30;
            // 031107  ARWILK  Begin  (Bug#110151)
            lsAttr = lsAttr + "ASSORTMENT" + (char)31 + readField("ASSORTMENT") + (char)30;
            // 031107  ARWILK  End  (Bug#110151)
            lsAttr = lsAttr + "LINE_DESCRIPTION" + (char)31 +readField("LINE_DESCRIPTION")+ (char)30;
            //Bug 76748, start
            lsAttr = lsAttr + "TAXABLE_DB" + (char)31 + (mgr.isEmpty(readField("TAXABLE_DB"))?"FALSE":"TRUE") + (char)30;
            lsAttr = lsAttr + "FEE_CODE" + (char)31 +readField("FEE_CODE") + (char)30;
            //Bug 76748, end

            itemset1.goTo(itemcurrrow); 

	    cmd = trans.addCustomCommand("SUBMITFORM", "NOPART_WO_REQUIS_LINE_API.New__"); 
            cmd.addParameter("INFO");    
            cmd.addParameter("OBJID");
            cmd.addParameter("OBJVERSION");
            cmd.addParameter("ATTR", lsAttr);
            cmd.addParameter("ACTION", "DO");
            trans = mgr.perform(trans);
            objid = trans.getValue("SUBMITFORM/DATA/OBJID");
            trans.clear();

            okFindITEM1();
            gotoNewRow(itemset1,objid);

            information = trans.getValue("SUBMITFORM/DATA/INFO");

            if (!mgr.isEmpty(information))
            {
                beg_pos = information.indexOf((char)31) + 1;
                end_pos = information.indexOf((char)30);
                info = information.substring(beg_pos, end_pos);
                mgr.showAlert(mgr.translate("PCMWWORKORDERREQUISHEADERRMBINFO: &1", info));
            }
        }
        else
        {
            itemset1.changeRow();  


            lsAttr = "ORDER_NO" + (char)31 + (mgr.isEmpty(itemset1.getValue("ORDER_NO")) ? "" : itemset1.getValue("ORDER_NO")) + (char)30;
            lsAttr = lsAttr + "ASSG_LINE_NO" + (char)31 + (mgr.isEmpty(itemset1.getRow().getValue("ITEM1_ASSGLINENO")) ? "" : itemset1.getRow().getValue("ITEM1_ASSGLINENO")) + (char)30;
            lsAttr = lsAttr + "ASSG_RELEASE_NO" + (char)31 + (mgr.isEmpty(itemset1.getRow().getFieldValue("ITEM1_ASSGRELEASENO")) ? "" : itemset1.getRow().getFieldValue("ITEM1_ASSGRELEASENO")) + (char)30;
            lsAttr = lsAttr + "VENDOR_NO" + (char)31 + readField("ITEM1_VENDORNO") + (char)30;
            lsAttr = lsAttr + "CONTACT" + (char)31 + readField("ITEM1_VENDORCONTACT") + (char)30;
            lsAttr = lsAttr + "DESCRIPTION" + (char)31 + readField("ITEM_NOTE_TEXT2") + (char)30;
            lsAttr = lsAttr + "WANTED_RECEIPT_DATE" + (char)31 + itemset1.getValue("WANTED_RECEIPT_DATE").substring(0,10) + (char)30;
            lsAttr = lsAttr + "LATEST_ORDER_DATE" + (char)31 + itemset1.getValue("LATEST_ORDER_DATE").substring(0,10) + (char)30;
            lsAttr = lsAttr + "COST_RETURN" + (char)31 + readField("ITEM1_COST_RETURN")+ (char)30;
            lsAttr = lsAttr + "NOTE_TEXT" + (char)31 + readField("ITEM1_NOTE_TEXT") + (char)30;
            lsAttr = lsAttr + "CONTRACT" + (char)31 + checkNull(itemset1.getValue("CONTRACT")) + (char)30;
            lsAttr = lsAttr + "REQUEST_TYPE" + (char)31 + checkNull(itemset1.getRow().getValue("ITEM1_REQUEST_TYPE")) + (char)30;
            lsAttr = lsAttr + "BUY_UNIT_MEAS" + (char)31 + readField("ITEM1_BUYUNITMEAS") + (char)30;
            lsAttr = lsAttr + "ORIGINAL_QTY" + (char)31 + readNumberField("ITEM1_ORIGINALQTY") + (char)30;

            double unit_price  = mgr.readNumberValue("FBUY_UNIT_PRICE");
	    double sales_price = mgr.readNumberValue("ITEM1_LISTPRICE");
	    
            if (!(mgr.isEmpty("ITEM1_VENDORNO")) && isNaN(unit_price))
            {
                unit_price = 0;
                itemset1.setNumberValue("FBUY_UNIT_PRICE",0.0);
                mgr.showAlert(mgr.translate("PCMWWORKORDERREQUISHEADERRMBPRICECORRZERO: Price/Curr is set to zero."));
            }

	    if ( isNaN( sales_price ) ) {
               sales_price = 0;
	       itemset1.setNumberValue("SALES_PRICE",0.0);   
	    }

	    lsAttr = lsAttr + "FBUY_UNIT_PRICE" + (char)31 + unit_price + (char)30;
            
            double discount = mgr.readNumberValue("DISCOUNT");
            if (isNaN(discount))
                discount = 0;

            if (discount != 0)
                lsAttr = lsAttr + "LINE_DISCOUNT" + (char)31 + readNumberField("DISCOUNT") + (char)30;

            lsAttr = lsAttr + "STAT_GRP" + (char)31 + readField("STAT_GRP") + (char)30;
            lsAttr = lsAttr + "CATALOG_CONTRACT" + (char)31 +itemset1.getRow().getValue("CATALOG_CONTRACT") + (char)30;     
            lsAttr = lsAttr + "PRICE_TYPE_DB" + (char)31 + (mgr.isEmpty(readField("PRICE_TYPE_DB"))?"CORRECT":"ESTIMATED") + (char)30;
            lsAttr = lsAttr + "BUYER_CODE" + (char)31 + readField("ITEM1_BUYERCODE") + (char)30;
            lsAttr = lsAttr + "TECHNICAL_COORDINATOR_ID" + (char)31 + readField("ITEM1_TECHCOORDID") + (char)30;
            lsAttr = lsAttr + "CATALOG_NO" + (char)31 + readField("ITEM1_CATALOG_NO") + (char)30;
	    lsAttr = lsAttr + "LINE_DESCRIPTION" + (char)31 +readField("LINE_DESCRIPTION")+ (char)30;
	    lsAttr = lsAttr + "PRICE_LIST_NO" + (char)31 + readField("ITEM1_PRICE_LIST_NO") + (char)30;
            lsAttr = lsAttr + "SALES_PRICE" + (char)31 + itemset1.getRow().getValue("SALES_PRICE") + (char)30;
            // 031107  ARWILK  Begin  (Bug#110151)
            lsAttr = lsAttr + "ASSORTMENT" + (char)31 + readField("ASSORTMENT") + (char)30;
            // 031107  ARWILK  End  (Bug#110151)
            //Bug 76748, start
            lsAttr = lsAttr + "TAXABLE_DB" + (char)31 + (mgr.isEmpty(readField("TAXABLE_DB"))?"FALSE":"TRUE") + (char)30;
            lsAttr = lsAttr + "FEE_CODE" + (char)31 +readField("FEE_CODE") + (char)30;
            //Bug 76748, end

            cmd = trans.addCustomCommand("SUBMITFORM", "NOPART_WO_REQUIS_LINE_API.Modify__"); 
            cmd.addParameter("INFO");    
            cmd.addParameter("OBJID", itemset1.getRow().getValue("OBJID"));
            cmd.addParameter("OBJVERSION", itemset1.getRow().getValue("OBJVERSION"));
            cmd.addParameter("ATTR", lsAttr);
            cmd.addParameter("ACTION", "DO");

            trans = mgr.perform(trans);
            trans.clear();

            okFindITEM1();
            itemset1.goTo(itemcurrrow);
            information = trans.getValue("SUBMITFORM/DATA/INFO");

            if (!mgr.isEmpty(information))
            {
                beg_pos = information.indexOf((char)31) + 1;
                end_pos = information.indexOf((char)30);
                info = information.substring(beg_pos,end_pos);
                mgr.showAlert(mgr.translate("PCMWWORKORDERREQUISHEADERRMBINFO: &1", info));
            }
        }

        if (itemset0.countRows()>0)
            itemset0.goTo(itemcurrrow__);

        up_wo_no = toInt(upperset.getRow().getNumberValue("UP_WO_NO"));
        up_mch_code = upperset.getRow().getValue("UP_MCH_CODE");
        mch_name = upperset.getRow().getValue("MCH_NAME");  
    }

    public void saveNew()
    {
        int currrow;

        eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME", upperset.getRow()));  
        currrow = headset.getCurrentRowNo();
        trans.clear();
        buffWoNo =  toInt(upperset.getRow().getNumberValue("UP_WO_NO"));

        saveReturn();
        newRow();
    }

    public void saveNew0()
    {
        int currrow;

        eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME", upperset.getRow()));  
        currrow = headset.getCurrentRowNo();
        trans.clear();
        buffWoNo =  toInt(upperset.getRow().getNumberValue("UP_WO_NO"));

        saveReturn0();
        newRowITEM0();
    }

    public void saveNew1()
    {
        editFlag = "FALSE";
        int currrow;

        eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME", upperset.getRow()));  
        currrow = headset.getCurrentRowNo();
        trans.clear();
        buffWoNo =  toInt(upperset.getRow().getNumberValue("UP_WO_NO"));

        saveReturn1();
        newRowITEM1();
    }

    public void gotoNewRow(ASPRowSet rowset, String objid)
    {
        ASPManager mgr = getASPManager();

        rowset.first();
        while (!objid.equals(rowset.getValue("OBJID")))
            rowset.next();
    }

    public void cancelEdit1()
    {
        ASPManager mgr = getASPManager();

        editFlag = "FALSE";

        itemlay1.setLayoutMode(itemlay1.getHistoryMode());
        itembar1.getASPRowSet().resetRow();
    }
//-----------------------------------------------------------------------------
//----------------------  CMDBAR EDIT-IT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void newRowITEM0()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd;
        String attribute;
        String wantdate;
        String Strwantdate;
        String lsAttr ="";
        int beg_pos;
        int end_pos;
        int lenofdate;
        String latestorderdate= "";
        String Strlatestorderdate;
        String costreturn= "";
        String catalogcontract= "";
        String reqtype= "";

        trans.clear();
        cmd = trans.addCustomFunction("WANTDATE", "Active_Separate_API.Get_Plan_S_Date", "WANTED_RECEIPT_DATE");
        cmd.addParameter("WO_NO", upperset.getRow().getValue("UP_WO_NO"));

        lsAttr = "WO_NO"+ (char)31 + upperset.getRow().getValue("UP_WO_NO") +(char)30;

        cmd = trans.addCustomCommand("PREPITEM", "PART_WO_REQUIS_LINE_API.New__"); 
        cmd.addParameter("INFO");    
        cmd.addParameter("OBJID");
        cmd.addParameter("OBJVERSION");
        cmd.addParameter("ATTR", lsAttr);
        cmd.addParameter("ACTION", "PREPARE");

        trans = mgr.perform(trans);
        wantdate = trans.getValue("WANTDATE/DATA/WANTED_RECEIPT_DATE");


        attribute = trans.getValue("PREPITEM/DATA/ATTR");

        StringTokenizer st1 = new StringTokenizer(attribute, ((char)30)+"");

        while (st1.hasMoreTokens())
        {
            String valuePair = st1.nextToken();

            StringTokenizer st2 = new StringTokenizer(valuePair, ((char)31)+"");

            if (st2.hasMoreTokens())
            {
                String fieldName = st2.nextToken();
                if (fieldName.indexOf("LATEST_ORDER_DATE")>-1 && st2.hasMoreTokens())
                    latestorderdate = st2.nextToken();

                if (fieldName.indexOf("COST_RETURN")>-1 && st2.hasMoreTokens())
                    costreturn = st2.nextToken();

                if (fieldName.indexOf("CATALOG_CONTRACT")>-1 && st2.hasMoreTokens())
                    catalogcontract = st2.nextToken();

                if (fieldName.indexOf("REQUEST_TYPE")>-1 && st2.hasMoreTokens())
                    reqtype = st2.nextToken();
            }
        }                         

        trans.clear();

        if (mgr.isEmpty(wantdate))
        {
            cmd = trans.addCustomFunction("WANTDATE1", "Maintenance_Site_Utility_API.Get_Site_Date", "SITE_DATE");
            cmd.addParameter("CONTRACT", headset.getRow().getValue("CONTRACT"));
            trans = mgr.perform(trans);
            wantdate = trans.getValue("WANTDATE1/DATA/SITE_DATE");
        }

        trans.clear();

        cmd = trans.addEmptyCommand("ITEM0", "PART_WO_REQUIS_LINE_API.New__", itemblk0);
        cmd.setOption("ACTION", "PREPARE");
        trans = mgr.perform(trans);

        ASPBuffer data = trans.getBuffer("ITEM0/DATA");
        trans.clear();

        cmd = trans.addQuery("FORMATDATE","SELECT to_date( ? ,'YYYY-MM-DD-hh24.MI.ss') LATEST_ORDER_DATE FROM DUAL");
	cmd.addParameter("MCH_NAME",latestorderdate);

        trans = mgr.perform(trans);
        Date dLastOrderDate = trans.getBuffer("FORMATDATE/DATA").getFieldDateValue("LATEST_ORDER_DATE");
        Strlatestorderdate = mgr.formatDate("LATEST_ORDER_DATE",dLastOrderDate);

        trans.clear(); 
        cmd = trans.addQuery("FORMATDATE","SELECT to_date( ? ,'YYYY-MM-DD-hh24.MI.ss') WANTED_RECEIPT_DATE FROM DUAL");
	cmd.addParameter("MCH_NAME",wantdate);

        trans = mgr.perform(trans);
        Date dWantDate = trans.getBuffer("FORMATDATE/DATA").getFieldDateValue("WANTED_RECEIPT_DATE");
        Strwantdate = mgr.formatDate("WANTED_RECEIPT_DATE",dWantDate);

        data.setFieldItem("WANTED_RECEIPT_DATE", Strwantdate);
        data.setFieldItem("ITEM0_CONTRACT", catalogcontract);
        data.setFieldItem("CATALOG_CONTRACT", catalogcontract);
        data.setFieldItem("COST_RETURN", costreturn);
        data.setFieldItem("LATEST_ORDER_DATE", Strlatestorderdate);
        data.setFieldItem("REQUEST_TYPE", reqtype);
        data.setFieldItem("ITEM0_ORDER_CODE", headset.getRow().getValue("ORDER_CODE"));

        itemset0.addRow(data); 

        eval(headblk.generateAssignments());
        eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME", upperset.getRow()));
        up_wo_no = toInt(upperset.getRow().getNumberValue("UP_WO_NO"));
        up_mch_code = upperset.getRow().getValue("UP_MCH_CODE");
        mch_name = upperset.getRow().getValue("MCH_NAME");
    }

    public void newRowITEM1()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd;
        String attribute;
        String wantdate;
        String Strwantdate; 
        String lsAttr;
        int beg_pos;
        int end_pos;
        int lenofdate;
        String latestorderdate = "";
        String Strlatestorderdate;  
        String costreturn= "";
        String catalogcontract= "";
        String orginalqty= "";
        String reqtype= "";
        String pricetype= "";
        String taxable = "FALSE";

        cmd = trans.addCustomFunction("WANTDATE", "Active_Separate_API.Get_Plan_S_Date", "ITEM1_WANTEDRECEIPTDATE");
        cmd.addParameter("WO_NO", upperset.getRow().getValue("UP_WO_NO"));

        lsAttr = "WO_NO"+ (char)31 + upperset.getRow().getValue("UP_WO_NO") +(char)30;

        cmd = trans.addCustomCommand("PREPITEM", "NOPART_WO_REQUIS_LINE_API.New__"); 
        cmd.addParameter("INFO");    
        cmd.addParameter("OBJID");
        cmd.addParameter("OBJVERSION");
        cmd.addParameter("ATTR", lsAttr);
        cmd.addParameter("ACTION", "PREPARE");

        trans = mgr.perform(trans);

        wantdate = trans.getValue("WANTDATE/DATA/ITEM1_WANTEDRECEIPTDATE");

        attribute = trans.getValue("PREPITEM/DATA/ATTR");

        StringTokenizer st1 = new StringTokenizer(attribute, ((char)30)+"");

        while (st1.hasMoreTokens())
        {
            String valuePair = st1.nextToken();

            StringTokenizer st2 = new StringTokenizer(valuePair, ((char)31)+"");

            if (st2.hasMoreTokens())
            {
                String fieldName = st2.nextToken();

                if (fieldName.indexOf("LATEST_ORDER_DATE")>-1 && st2.hasMoreTokens())
                    latestorderdate = st2.nextToken();

                if (fieldName.indexOf("ORIGINAL_QTY")>-1 && st2.hasMoreTokens())
                    orginalqty = st2.nextToken();

                if (fieldName.indexOf("COST_RETURN")>-1 && st2.hasMoreTokens())
                    costreturn = st2.nextToken();

                if (fieldName.indexOf("CATALOG_CONTRACT")>-1 && st2.hasMoreTokens())
                    catalogcontract = st2.nextToken();

                if (fieldName.indexOf("PRICE_TYPE_DB")>-1 && st2.hasMoreTokens())
                    pricetype = st2.nextToken();

                if (fieldName.indexOf("REQUEST_TYPE")>-1 && st2.hasMoreTokens())
                    reqtype = st2.nextToken();


            }
        }                         


        trans.clear();

        if (mgr.isEmpty(wantdate))
        {
            cmd = trans.addCustomFunction("WANTDATE1", "Maintenance_Site_Utility_API.Get_Site_Date", "SITE_DATE");
            cmd.addParameter("CONTRACT", headset.getRow().getValue("CONTRACT"));
            trans = mgr.perform(trans);
            wantdate = trans.getValue("WANTDATE1/DATA/SITE_DATE");
        }

        //Bug 76748, start
        trans.clear();

        cmd=trans.addCustomFunction("COM","Site_API.Get_Company","COMPANY");
        cmd.addParameter("CONTRACT",headset.getValue("CONTRACT"));

        cmd = trans.addCustomFunction("GETTAX", "Company_Distribution_Info_API.Get_Purch_Taxable_Db", "TAXABLE_DB");
        cmd.addReference("COMPANY","COM/DATA");

        trans = mgr.perform(trans);
        taxable = trans.getValue("GETTAX/DATA/TAXABLE_DB");
        String company = trans.getValue("COM/DATA/COMPANY");

        //Bug 76748, end
        trans.clear();

        cmd = trans.addEmptyCommand("ITEM1", "NOPART_WO_REQUIS_LINE_API.New__", itemblk1);
        cmd.setOption("ACTION", "PREPARE");
        trans = mgr.perform(trans);

        ASPBuffer data = trans.getBuffer("ITEM1/DATA");
        trans.clear();

        cmd = trans.addQuery("FORMATDATE","SELECT to_date( ? ,'YYYY-MM-DD-hh24.MI.ss') LATEST_ORDER_DATE FROM DUAL");
	cmd.addParameter("MCH_NAME",latestorderdate);

        trans = mgr.perform(trans);
        Date dLastOrderDate = trans.getBuffer("FORMATDATE/DATA").getFieldDateValue("LATEST_ORDER_DATE");
        Strlatestorderdate = mgr.formatDate("LATEST_ORDER_DATE",dLastOrderDate);

        trans.clear(); 
        cmd = trans.addQuery("FORMATDATE","SELECT to_date( ? ,'YYYY-MM-DD-hh24.MI.ss') WANTED_RECEIPT_DATE FROM DUAL");
	cmd.addParameter("MCH_NAME",wantdate);

        trans = mgr.perform(trans);
        Date dWantDate = trans.getBuffer("FORMATDATE/DATA").getFieldDateValue("WANTED_RECEIPT_DATE");
        Strwantdate = mgr.formatDate("WANTED_RECEIPT_DATE",dWantDate);

        data.setFieldItem("ITEM1_WANTEDRECEIPTDATE", Strwantdate);
        data.setFieldItem("ITEM1_CONTRACT", headset.getRow().getValue("CONTRACT"));
        data.setFieldItem("ITEM1_LATESTORDERDATE", Strlatestorderdate);
        data.setFieldItem("ITEM1_ORIGINALQTY", orginalqty);
        data.setFieldItem("ITEM1_COST_RETURN", costreturn); 
        data.setFieldItem("ITEM1_CATALOG_CONTRACT", headset.getRow().getValue("CONTRACT")); 
        data.setFieldItem("ITEM1_REQUEST_TYPE", reqtype);
        data.setFieldItem("PRICE_TYPE_DB", pricetype);
        //Bug 76748, start
        data.setFieldItem("TAXABLE_DB", taxable);
        data.setFieldItem("ITEM1_COMPANY", company);
        data.setFieldItem("FEE_TYPE_DB","CALCVAT;NOTAX");
        //Bug 76748, end
        itemset1.addRow(data); 

        eval(headblk.generateAssignments());
        eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME", upperset.getRow()));
        up_wo_no = toInt(upperset.getRow().getNumberValue("UP_WO_NO"));
        up_mch_code = upperset.getRow().getValue("UP_MCH_CODE");
        mch_name = upperset.getRow().getValue("MCH_NAME");
    }

    public void deleteRow()
    {
        ASPManager mgr = getASPManager();

        int rowNo;

        if (headlay.isSingleLayout())
        {
            rowNo = headset.getCurrentRowNo();
            headset.goTo(headset.getCurrentRowNo());     
        }
        else
        {
            rowNo = 0;
            headset.goTo(headset.getRowSelected());
        }

        headset.setRemoved();
        mgr.submit(trans);

        if (headlay.isSingleLayout())
        {
            headset.goTo(rowNo+1);
            okFindITEM0();
            okFindITEM1();
        }
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void countFind()
    {
        ASPManager mgr = getASPManager();

        int currrow;

        up_wo_no = toInt(upperset.getRow().getNumberValue("UP_WO_NO"));
        currrow = headset.getCurrentRowNo();
        ASPQuery q = trans.addQuery(headblk);
        q.addWhereCondition("WO_NO = ?");
	q.addParameter("UP_WO_NO",Integer.toString(up_wo_no));
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);

        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.goTo(currrow);      
    }

    public void countFindITEM0()
    {
        ASPManager mgr = getASPManager();

        int currow;

        currow = headset.getCurrentRowNo();
        ASPQuery q = trans.addQuery(itemblk0);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("REQUISITION_NO = ?");
	q.addParameter("REQUISITION_NO",headset.getRow().getValue("REQUISITION_NO"));
	mgr.submit(trans);

        itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
        headset.goTo(currow);

        eval(headblk.generateAssignments());
        eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME", upperset.getRow()));
        up_wo_no = toInt(upperset.getRow().getNumberValue("UP_WO_NO"));
        up_mch_code = upperset.getRow().getValue("UP_MCH_CODE");
        mch_name = upperset.getRow().getValue("MCH_NAME");
    }

    public void countFindITEM1()
    {
        ASPManager mgr = getASPManager();

        int currow;

        currow = headset.getCurrentRowNo();
        ASPQuery q = trans.addQuery(itemblk1);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("REQUISITION_NO = ?");
	q.addParameter("REQUISITION_NO",headset.getRow().getValue("REQUISITION_NO"));
	mgr.submit(trans);

        itemlay1.setCountValue(toInt(itemset1.getRow().getValue("N")));
        headset.goTo(currow);

        eval(headblk.generateAssignments());
        eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME", upperset.getRow()));
        up_wo_no = toInt(upperset.getRow().getNumberValue("UP_WO_NO"));
        up_mch_code = upperset.getRow().getValue("UP_MCH_CODE");
        mch_name = upperset.getRow().getValue("MCH_NAME");
    }

    public void searchWorkOrder()
    {
        ASPManager mgr = getASPManager();

        String buffMchCode;
        String buffMchName;
        int rowno_ =0;

        ASPQuery q = trans.addQuery(upperblk);
        q.includeMeta("ALL");

        buffWoNo = up_wo_no;
        buffMchCode = up_mch_code ;
        buffMchName = mch_name;

        q = trans.addEmptyQuery(headblk);

        if (!mgr.isEmpty(Integer.toString(up_wo_no))){
            q.addWhereCondition("WO_NO = ?");
	    q.addParameter("UP_WO_NO",Integer.toString(up_wo_no));
	}

        q.setOrderByClause("TO_NUMBER(REQUISITION_NO)");
        q.includeMeta("ALL");

        mgr.submit(trans);

        if (upperset.countRows() == 0)
            mgr.showAlert(mgr.translate("PCMWWORKORDERREQUISHEADERRMBNODATAFOUND: No data found."));

        if (headset.countRows() == 1)
        {
            okFindITEM0();
            okFindITEM1();
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        }
        else
        {
            if (!(mgr.isEmpty(sRequisitionNo)))
            {
                headset.first();
                while (!(sRequisitionNo.equals(headset.getRow().getValue("REQUISITION_NO"))) && headset.next())
                {

                }
                rowno_ =   headset.getCurrentRowNo();
                headlay.setLayoutMode(headlay.SINGLE_LAYOUT);

                okFindITEM0();
                okFindITEM1();
                headset.goTo(rowno_);
            }
        }

        if (itemset0.countRows() > 0)
            setValues();

        if (itemset1.countRows() > 0)
            setValuesItem1();

        eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME", upperset.getRow()));  
        eval(headblk.generateAssignments());

        ASPBuffer upper = upperset.getRow();
        upper.setValue("UP_WO_NO", Integer.toString(buffWoNo));
        upper.setValue("UP_MCH_CODE", buffMchCode);
        upper.setValue("MCH_NAME", buffMchName);
        upperset.setRow(upper);   
    }

    public void okFind()
    {
        ASPManager mgr = getASPManager();

        ASPQuery q = trans.addQuery(headblk);
        q.addWhereCondition("WO_NO = ?");
	q.addParameter("UP_WO_NO",Integer.toString(buffWoNo));
        q.setOrderByClause("TO_NUMBER(REQUISITION_NO)");
        q.includeMeta("ALL");

        if (mgr.dataTransfered())
            q.addOrCondition(mgr.getTransferedData());

        mgr.submit(trans);

        if (headset.countRows() == 1)
        {
            okFindITEM0();
            okFindITEM1();

            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        }

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWWORKORDERREQUISHEADERRMBNODATA: No data found."));
            headset.clear();
            headlay.setLayoutMode(headlay.MULTIROW_LAYOUT); 
        }

        eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME", upperset.getRow()));  
        eval(headblk.generateAssignments());
        up_wo_no = toInt(upperset.getRow().getNumberValue("UP_WO_NO"));
        up_mch_code = upperset.getRow().getValue("UP_MCH_CODE");
        mch_name = upperset.getRow().getValue("MCH_NAME");

        qrystr = mgr.createSearchURL(headblk);
    }

    public void okFind11()
    {
        ASPManager mgr = getASPManager();
        trans.clear();
        ASPQuery q = trans.addEmptyQuery(headblk);
        q.addWhereCondition("WO_NO = ?");
	q.addParameter("UP_WO_NO",Integer.toString(buffWoNo));
        q.setOrderByClause("TO_NUMBER(REQUISITION_NO)");
        q.includeMeta("ALL");

        if (mgr.dataTransfered())
            q.addOrCondition(mgr.getTransferedData());

        mgr.submit(trans);

        if (headset.countRows() == 1)
        {
            okFindITEM0();
            okFindITEM1();
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        }

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWWORKORDERREQUISHEADERRMBNODATA: No data found."));
            headset.clear();
            headlay.setLayoutMode(headlay.MULTIROW_LAYOUT); 
        }

        eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME", upperset.getRow()));  
        eval(headblk.generateAssignments());
        up_wo_no = toInt(upperset.getRow().getNumberValue("UP_WO_NO"));
        up_mch_code = upperset.getRow().getValue("UP_MCH_CODE");
        mch_name = upperset.getRow().getValue("MCH_NAME");
    }

//-----------------------------------------------------------------------------
//-----------------------  CMDBAR SEARCH-IT FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

    public void okFindITEM0()
    {
        ASPManager mgr = getASPManager();

        int headrowno;

        if (!isSecurityChecked)
            checkMethodsAvailable();

        if (headset.countRows() != 0)
        {
            trans.clear();

            headrowno = headset.getCurrentRowNo();

            itemset0.clear();
            ASPQuery q = trans.addEmptyQuery(itemblk0);
            q.addWhereCondition("WO_NO = ? and REQUISITION_NO = ?");
            q.addWhereCondition("REQUISITION_NO = ?");
	    q.addParameter("UP_WO_NO",Integer.toString(buffWoNo));
	    q.addParameter("REQUISITION_NO",headset.getRow().getValue("REQUISITION_NO"));
	    q.addParameter("REQUISITION_NO",headset.getRow().getValue("REQUISITION_NO"));
	    q.setOrderByClause("TO_NUMBER(LINE_NO),TO_NUMBER(RELEASE_NO)");
            q.includeMeta("ALL");

            String selectList = modifySelectList(0, itemblk0.getSelectList());
            q.setSelectList(selectList);

            mgr.submit(trans);

            if (mgr.commandBarActivated())
            {
                if (itemset0.countRows() == 0 && "ITEM0.OkFind".equals(mgr.readValue("__COMMAND")))
                {
                    mgr.showAlert(mgr.translate("PCMWWORKORDERREQUISHEADERRMBNODATA: No data found."));
                    itemset0.clear();
                }
            }

            itemset0 = setRMBField(0, itemset0, "REQ_TO_ORD_VLD"); // Check the validity of RMB's for the fetched data
            headset.goTo(headrowno);

            setValues();

        }

        // store objid list for refresh purposes.
        objIdBuffer0 = itemset0.getRows("OBJID");
        ctx.writeBuffer("OBJIDBUFFER0", objIdBuffer0);
    }

    public void okFindITEM1()
    {
        ASPManager mgr = getASPManager();

        int headrowno;

        if (!isSecurityChecked)
            checkMethodsAvailable();

        if (headset.countRows() != 0)
        {
            trans.clear();
            headrowno = headset.getCurrentRowNo();

            itemset1.clear();
            ASPQuery q = trans.addEmptyQuery(itemblk1);
            q.addWhereCondition("WO_NO = ? and REQUISITION_NO = ?");
            q.addWhereCondition("REQUISITION_NO = ?");
	    q.addParameter("UP_WO_NO",Integer.toString(buffWoNo));
	    q.addParameter("REQUISITION_NO",headset.getRow().getValue("REQUISITION_NO"));
	    q.addParameter("REQUISITION_NO",headset.getRow().getValue("REQUISITION_NO"));
	    q.setOrderByClause("TO_NUMBER(LINE_NO),TO_NUMBER(RELEASE_NO)");
            q.includeMeta("ALL");  

            String selectList = modifySelectList(1, itemblk1.getSelectList());
            q.setSelectList(selectList);

            mgr.submit(trans);

            if (mgr.commandBarActivated())
            {
                if (itemset1.countRows() == 0 && "ITEM1.OkFind".equals(mgr.readValue("__COMMAND")))
                {
                    mgr.showAlert(mgr.translate("PCMWWORKORDERREQUISHEADERRMBNODATA: No data found."));
                    itemset1.clear();
                }
            }

            itemset1 = setRMBField(1, itemset1, "REQ_TO_ORD_VLD2"); // Check the validity of RMB's for the fetched data
            setValuesItem1();
            headset.goTo(headrowno);
        }
        // store objid list for refresh purposes.
        objIdBuffer1 = itemset1.getRows("OBJID");
        ctx.writeBuffer("OBJIDBUFFER1", objIdBuffer1);
    }

    public void refreshITEM()
    {
        if (selBlk == 0)
            refreshITEM0();
        else if (selBlk == 1)
            refreshITEM1();

        goToRows();
    }

    public void refreshITEM0()
    {
        ASPManager mgr = getASPManager();

        int headrowno;

        trans.clear();
        headrowno = headset.getCurrentRowNo();

        itemset0.clear();
        ASPQuery q = trans.addEmptyQuery(itemblk0);
        q.addOrCondition(objIdBuffer0);
        q.setOrderByClause("LINE_NO,RELEASE_NO");
        q.includeMeta("ALL");  

        String selectList = modifySelectList(0, itemblk0.getSelectList());
        q.setSelectList(selectList);

        mgr.submit(trans);

        setValues();
        headset.goTo(headrowno);
    }

    public void refreshITEM1()
    {
        ASPManager mgr = getASPManager();

        int headrowno;

        trans.clear();
        headrowno = headset.getCurrentRowNo();

        itemset1.clear();
        ASPQuery q = trans.addEmptyQuery(itemblk1);
        q.addOrCondition(objIdBuffer1);
        q.setOrderByClause("LINE_NO,RELEASE_NO");
        q.includeMeta("ALL");  

        String selectList = modifySelectList(1, itemblk1.getSelectList());
        q.setSelectList(selectList);

        mgr.submit(trans);

        setValuesItem1();
        headset.goTo(headrowno);
    }

    private ASPRowSet setRMBField(int blkNo, ASPRowSet setTmp, String sFieldName)
    {
        ASPManager mgr = getASPManager();
        ASPBuffer bufTemp = null;
        int nCountRows = setTmp.countRows();

        for (int nCounter = 0; nCounter < nCountRows; nCounter++)
        {
            setTmp.goTo(nCounter);
            bufTemp = setTmp.getRow();

            if (blkNo == 0)
            {
                if (mgr.isEmpty(setTmp.getValue("ORDER_NO")) && (( ( "Released".equals(setTmp.getValue("LINE_STATE"))) ) || ("Authorized".equals(setTmp.getValue("LINE_STATE")) ) ))
                    bufTemp.setValue(sFieldName, "OK");
                else
                    bufTemp.setValue(sFieldName, "*");
            }
            else
            {
                if (mgr.isEmpty(setTmp.getValue("ORDER_NO")) && (( ( "Released".equals(setTmp.getValue("LINE_STATE"))) ) || ("Authorized".equals(setTmp.getValue("LINE_STATE")) ) ))
                    bufTemp.setValue(sFieldName, "OK");
                else
                    bufTemp.setValue(sFieldName, "*");
            }

            setTmp.setRow(bufTemp);
        }
        return setTmp;
    }

    public String modifySelectList(int blkNo, String selectList)
    {
        // Modifies the select list with get methods from Shop Order if the methods are available.
        ASPManager mgr = getASPManager();

        if (isSalesPartOk)
            if (blkNo == 0)
                selectList = mgr.replace(selectList, "'' CATALOGDESC", "SALES_PART_API.Get_Catalog_Desc(CATALOG_CONTRACT,CATALOG_NO) CATALOGDESC");
            else
                selectList = mgr.replace(selectList, "'' ITEM1_CATALOGDESC", "SALES_PART_API.Get_Catalog_Desc(CATALOG_CONTRACT,CATALOG_NO) ITEM1_CATALOGDESC");

        return selectList;
    }

    public void checkMethodsAvailable()
    {
        ASPManager mgr = getASPManager();
        ASPBuffer sec;

        trans.clear();
        trans.addSecurityQuery("SALES_PART_API", "Get_Catalog_Desc");

        trans = mgr.perform(trans);

        sec = trans.getSecurityInfo();
        trans.clear();
        isSalesPartOk = sec.itemExists("SALES_PART_API.Get_Catalog_Desc");
        isSecurityChecked = true;
    }


//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void planRequisition()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd;
        int currrow     = 0;
        int currItemRow = 0;
        int count       = 0;

        currrow = headset.getCurrentRowNo();

        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            headset.setFilterOn();
            count = headset.countSelectedRows();
        }
        else
        {
            currrow = headset.getCurrentRowNo();
            currItemRow = itemset0.getCurrentRowNo();
            headset.unselectRows();
            count = 1;
        }


        for (int i=0; i<count; i++)
        {

            cmd = trans.addCustomCommand("FREEZE"+i, "Work_Order_Requis_Header_API.Freeze__");
            cmd.addParameter("WO_NO",Integer.toString(buffWoNo));  
            cmd.addParameter("REQUISITION_NO",headset.getRow().getValue("REQUISITION_NO"));

            if (headlay.isMultirowLayout())
                headset.next();
        }

        trans = mgr.perform(trans);
        if (headlay.isMultirowLayout())
        {
            headset.setFilterOff();
            headset.unselectRows();
        }

        trans.clear();
        //okFind(); 

        headset.refreshAllRows();
	itemset0.refreshAllRows();

        headset.goTo(currrow);
        itemset0.goTo(currItemRow);

    }


    public void releaseRequisition()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd;
        int currrow     = 0;
        int currItemRow = 0;
        int count;
        trans.clear();

        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            headset.setFilterOn();
            count = headset.countSelectedRows();
        }
        else
        {
            currrow = headset.getCurrentRowNo();
            currItemRow = itemset0.getCurrentRowNo();
            headset.unselectRows();
            count = 1;
        }

        for (int i=0; i<count; i++)
        {
            cmd = trans.addCustomCommand("ACTIVATE"+i, "Work_Order_Requis_Header_API.Activate__");
            cmd.addParameter("WO_NO",Integer.toString(buffWoNo));
            cmd.addParameter("REQUISITION_NO",headset.getRow().getValue("REQUISITION_NO"));

            if (headlay.isMultirowLayout())
                headset.next();
        }

        trans = mgr.perform(trans);
        if (headlay.isMultirowLayout())
        {
            headset.setFilterOff();
            headset.unselectRows();
        }

        trans.clear();
        //okFind();   

        headset.refreshAllRows();
	if (itemset0.countRows() >0)
        {
	    itemset0.refreshAllRows();
	}
	if (itemset1.countRows() >0) 
	{
	    itemset1.refreshAllRows();
	}
	

        headset.goTo(currrow);
	if (itemset0.countRows() >0)
        {
	    itemset0.goTo(currItemRow);
	}
	/*if (itemset1.countRows() >0) 
	{
	    itemset1.goTo(currItemRow);
	}*/
        
	
    }

    // 040219  ARWILK  Begin  (Enable Multirow RMB actions)
    public void perform(String command) 
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            headset.markSelectedRows(command);
            mgr.submit(trans);
        }
        else
        {
            headset.unselectRows();
            headset.markRow(command);
            int currrow = headset.getCurrentRowNo();
            mgr.submit(trans);

            headset.goTo(currrow);
        }   
    }
    // 040219  ARWILK  End  (Enable Multirow RMB actions)

    public void evalUpper()
    {
        eval(headblk.generateAssignments());
        eval(upperblk.generateAssignments("UP_WO_NO,UP_MCH_CODE,MCH_NAME", upperset.getRow()));
        up_wo_no = toInt(upperset.getRow().getNumberValue("UP_WO_NO"));
        up_mch_code = upperset.getRow().getValue("UP_MCH_CODE");
        mch_name = upperset.getRow().getValue("MCH_NAME");
    }

    public void setGlobalvalues()
    {
        ASPManager mgr = getASPManager();

        ctx.setGlobal("GLOBAL_WO_NO", mgr.readValue("UP_WO_NO", ""));
        ctx.setGlobal("GLOBAL_MCH_CODE", mgr.readValue("UP_MCH_CODE", ""));
        ctx.setGlobal("GLOBAL_MCH_NAME", mgr.readValue("MCH_NAME", ""));
    }

    public void setValues()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd; 
        String priceListNo;  
        int n;    
        String catNo;
        String catCon;
        String priceLiNo;
        double nQtyReq;
        double listPrice;
        double salePriceAmnt;
        double nDiscount;
        double baseUnitPrice;  
        double currencyRate;

        listPrice = 0;
        salePriceAmnt = 0;
        priceListNo = "";
        nDiscount = 0;

        trans.clear();      

        n = itemset0.countRows();

        if (n > 0)
        {
            itemset0.first();

            for (int i = 0; i <= n; ++i)
            {
                catNo = itemset0.getRow().getFieldValue("CATALOG_NO");
                catCon = itemset0.getRow().getFieldValue("CATALOG_CONTRACT");
                priceLiNo = itemset0.getRow().getFieldValue("PRICE_LIST_NO");
                nQtyReq = itemset0.getRow().getNumberValue("ORIGINAL_QTY");
                if (isNaN(nQtyReq))
                    nQtyReq = 0;

                if ((!mgr.isEmpty(catNo)) && (nQtyReq != 0 ))
                {
                    String s_nQtyReq = mgr.formatNumber("ORIGINAL_QTY", nQtyReq);

                    cmd = trans.addCustomCommand("PRICEINFO" + i,"Work_Order_Coding_API.Get_Price_Info");
                    cmd.addParameter("BASE_UNIT_PRICE", "0");
                    cmd.addParameter("SALE_UNIT_PRICE", "0");
                    cmd.addParameter("DISCOUNT", "0");
                    cmd.addParameter("CURRENCY_RATE", "0");
                    cmd.addParameter("CATALOG_CONTRACT", catCon);
                    cmd.addParameter("CATALOG_NO", catNo);
                    cmd.addParameter("CUSTOMER_NO", cus_no);
                    cmd.addParameter("AGREEMENT_ID", agree_id);
                    cmd.addParameter("PRICE_LIST_NO", priceLiNo);
                    cmd.addParameter("ORIGINAL_QTY", s_nQtyReq);
                }

                itemset0.next();
            }
            trans = mgr.validate(trans);

            itemset0.first();

            for (int i = 0; i < n; ++i)
            {
                ASPBuffer row = itemset0.getRow();
                listPrice = 0;
                salePriceAmnt = 0;
                priceListNo = "";
                nQtyReq = itemset0.getRow().getNumberValue("ORIGINAL_QTY");

                if (isNaN(nQtyReq))
                    nQtyReq = 0;

                priceLiNo = itemset0.getRow().getFieldValue("PRICE_LIST_NO");
                catNo = itemset0.getRow().getFieldValue("CATALOG_NO");

                if ((!mgr.isEmpty(catNo)) && (nQtyReq != 0))
                {
                    if (mgr.isEmpty(priceListNo))
                        priceListNo = trans.getValue("PRICEINFO" + i + "/DATA/PRICE_LIST_NO");

                    nDiscount = trans.getNumberValue("PRICEINFO" + i + "/DATA/DISCOUNT");   
                    if (isNaN(nDiscount))
                        nDiscount = 0;

                    listPrice = trans.getNumberValue("PRICEINFO" + i + "/DATA/SALE_UNIT_PRICE");  
                    if (isNaN(listPrice))
                        listPrice = 0;

                    salePriceAmnt = (listPrice -(nDiscount / 100 * listPrice)) * nQtyReq;

                    row.setNumberValue("ITEM0_LISTPRICE", listPrice);
                    row.setNumberValue("ITEM0_DISCOUNT", nDiscount);
                    row.setNumberValue("ITEM0_SALESPRICEAMOUNT", salePriceAmnt);
                    itemset0.setRow(row);
                }

                itemset0.next();
            }
        }

        itemset0.first();

        trans.clear();
    }

    public void setValuesItem1()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd;
        String priceListNo;  
        int n;
        String catNo;
        String catCon;
        String priceLiNo;
        double nQtyReq;    
        double listPrice;
        double salePriceAmnt;
        double nDiscount;
        double baseUnitPrice;  
        double currencyRate;    

        listPrice = 0;
        salePriceAmnt = 0;
        priceListNo = "";
        nDiscount = 0;

        trans.clear();      
        n = itemset1.countRows();

        if (n > 0)
        {
            itemset1.first();

            for (int i = 0; i < n; ++i)
            {
                catNo = itemset1.getRow().getFieldValue("ITEM1_CATALOG_NO");
                catCon = itemset1.getRow().getFieldValue("ITEM1_CATALOG_CONTRACT");
                priceLiNo = itemset1.getRow().getFieldValue("ITEM1_PRICE_LIST_NO");
                nQtyReq = itemset1.getRow().getNumberValue("ITEM1_ORIGINALQTY");

                if (isNaN(nQtyReq))
                    nQtyReq = 0;

                if ((!mgr.isEmpty(catNo)) && (nQtyReq != 0))
                {
                    String s_nQtyReq = mgr.formatNumber("ITEM1_ORIGINALQTY", nQtyReq);

                    cmd = trans.addCustomCommand("PRICEINFO" + i, "Work_Order_Coding_API.Get_Price_Info");
                    cmd.addParameter("BASE_UNIT_PRICE", "0");
                    cmd.addParameter("SALE_UNIT_PRICE", "0");
                    cmd.addParameter("DISCOUNT", "0");
                    cmd.addParameter("CURRENCY_RATE", "0");
                    cmd.addParameter("ITEM1_CATALOG_CONTRACT", catCon);
                    cmd.addParameter("ITEM1_CATALOG_NO", catNo);
                    cmd.addParameter("ITEM1_CUSTOMER_NO", cus_no);
                    cmd.addParameter("ITEM1_AGREEMENT_ID", agree_id);
                    cmd.addParameter("ITEM1_PRICE_LIST_NO", priceLiNo);
                    cmd.addParameter("ITEM1_ORIGINALQTY", s_nQtyReq);
                }

                itemset1.next();
            }

            trans = mgr.validate(trans);

            itemset1.first();

            for (int i = 0; i < n; ++i)
            {
                ASPBuffer row = itemset1.getRow();

                listPrice = 0;
                salePriceAmnt = 0;
                priceListNo = "";

                nQtyReq = itemset1.getRow().getNumberValue("ITEM1_ORIGINALQTY");

                if (isNaN(nQtyReq))
                    nQtyReq = 0;

                priceLiNo = itemset1.getRow().getFieldValue("ITEM1_PRICE_LIST_NO");
                catNo = itemset1.getRow().getFieldValue("ITEM1_CATALOG_NO");
                listPrice = itemset1.getRow().getNumberValue("SALES_PRICE");
                if (isNaN(listPrice))
                    listPrice = 0;

                if ((!mgr.isEmpty(catNo)) && (nQtyReq != 0))
                {
                    if (mgr.isEmpty(priceListNo))
                        priceListNo = trans.getValue("PRICEINFO" + i + "/DATA/ITEM1_PRICE_LIST_NO");

                    nDiscount = trans.getNumberValue("PRICEINFO" + i + "/DATA/DISCOUNT");   
                    if (isNaN(nDiscount))
                        nDiscount = 0;

                    salePriceAmnt = (listPrice - (nDiscount / 100 * listPrice)) * nQtyReq;

                    row.setNumberValue("ITEM1_SALESPRICEAMOUNT", salePriceAmnt);
                    itemset1.setRow(row);
                }

                itemset1.next();
            }
        }

        itemset1.first();
        trans.clear();
    }

    public void sparePartObject0()
    {
        ASPManager mgr = getASPManager();

        bOpenNewWindow = true;

        urlString = "MaintenanceObject2.page?MCH_CODE=" + mgr.URLEncode(headset.getValue("MCH_CODE")) +
                    "&ORDER_NO=" + mgr.URLEncode(headset.getValue("REQUISITION_NO")) +
                    "&CONTRACT=" + mgr.URLEncode(headset.getValue("CONTRACT")) +
                    "&WO_NO=" + mgr.URLEncode(headset.getValue("WO_NO")) +
                    "&QRYSTR=" + mgr.URLEncode(qrystr) +
                    "&FRMNAME=" + mgr.URLEncode("maintMatReq");
        
        newWinHandle = "sparePartInObject"; 
    }

    public void objStructure0()
    {
        ASPManager mgr = getASPManager();

        bOpenNewWindow = true;

        urlString = "MaintenaceObject3.page?MCH_CODE=" + mgr.URLEncode(headset.getValue("MCH_CODE")) +
                    "&CONTRACT=" + mgr.URLEncode(headset.getValue("CONTRACT")) +
                    "&WO_NO=" + mgr.URLEncode(headset.getValue("WO_NO")) +
                    "&QRYSTR=" + mgr.URLEncode(qrystr) +
                    "&FRMNAME=" + mgr.URLEncode("maintMatReq");

        newWinHandle = "objStructure"; 
    }


    public void detchedPartList0()
    {
        ASPManager mgr = getASPManager();


        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());
        else
            itemset0.selectRow();

        bOpenNewWindow = true;

        urlString = "../equipw/EquipmentSpareStructure2.page?SPARE_ID=" + mgr.URLEncode(itemset0.getValue("PART_NO"));

        newWinHandle = "detchedPartList"; 

    }


    public void planRequisitionLine0()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd;
        int currrow;
        int count=0;

        currrow = itemset0.getCurrentRowNo();

        if (itemlay0.isMultirowLayout())
        {
            itemset0.storeSelections();
            itemset0.setFilterOn();
            count = itemset0.countSelectedRows();
        }
        else
        {
            currrow = itemset0.getCurrentRowNo();
            itemset0.unselectRows();
            count = 1;
        }


        for (int i=0; i<count; i++)
        {
            cmd = trans.addCustomCommand("ACTIVATE"+i, "Work_Order_Requis_Line_API.Freeze__");
            cmd.addParameter("REQUISITION_NO",headset.getValue("REQUISITION_NO"));
            cmd.addParameter("LINE_NO",itemset0.getValue("LINE_NO"));  
            cmd.addParameter("RELEASE_NO",itemset0.getValue("RELEASE_NO"));

            if (itemlay0.isMultirowLayout())
                itemset0.next();
        }

        trans = mgr.perform(trans);
        if (itemlay0.isMultirowLayout())
        {
            itemset0.setFilterOff();
            itemset0.unselectRows();
        }

        trans.clear();
        headset.refreshRow();
        okFindITEM0();
        evalUpper();
        itemset0.goTo(currrow);

    }

    public void releaseRequisitionLine0()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd;
        int currrow=0;
        int count;


        trans.clear();

        if (itemlay0.isMultirowLayout())
        {
            itemset0.storeSelections();
            itemset0.setFilterOn();
            count = itemset0.countSelectedRows();
        }
        else
        {
            currrow = itemset0.getCurrentRowNo();
            itemset0.unselectRows();
            count = 1;
        }

        for (int i=0; i<count; i++)
        {

            cmd = trans.addCustomCommand("ACTIVATE"+i, "Work_Order_Requis_Line_API.Activate__");
            cmd.addParameter("REQUISITION_NO",headset.getValue("REQUISITION_NO"));
            cmd.addParameter("LINE_NO",itemset0.getValue("LINE_NO"));
            cmd.addParameter("RELEASE_NO",itemset0.getValue("RELEASE_NO")); 

            if (itemlay0.isMultirowLayout())
                itemset0.next();
        }

        trans = mgr.perform(trans);
        if (itemlay0.isMultirowLayout())
        {
            itemset0.setFilterOff();
            itemset0.unselectRows();
        }


        trans.clear();
        headset.refreshRow();
        okFindITEM0();     
        evalUpper();
        itemset0.goTo(currrow);

    }

    // 040219  ARWILK  Begin  (Enable Multirow RMB actions)
    public void performITEM0(String command) 
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        if (itemlay0.isMultirowLayout())
        {
            itemset0.storeSelections();
            itemset0.markSelectedRows(command);
            mgr.submit(trans);
        }
        else
        {
            itemset0.unselectRows();
            itemset0.markRow(command);
            int currrow = itemset0.getCurrentRowNo();
            mgr.submit(trans);

            itemset0.goTo(currrow);
        }   
    }
    // 040219  ARWILK  End  (Enable Multirow RMB actions)

    public void planRequisitionLine1()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd;
        int currrow;
        int count=0;

        currrow = itemset1.getCurrentRowNo();

        if (itemlay1.isMultirowLayout())
        {
            itemset1.storeSelections();
            itemset1.setFilterOn();
            count = itemset1.countSelectedRows();
        }
        else
        {
            currrow = itemset1.getCurrentRowNo();
            itemset1.unselectRows();
            count = 1;
        }

        for (int i=0; i<count; i++)
        {
            cmd = trans.addCustomCommand("ACTIVATE"+i, "Work_Order_Requis_Line_API.Freeze__");
            cmd.addParameter("REQUISITION_NO",headset.getValue("REQUISITION_NO"));
            cmd.addParameter("LINE_NO",itemset1.getValue("LINE_NO"));  
            cmd.addParameter("RELEASE_NO",itemset1.getValue("RELEASE_NO"));

            if (itemlay1.isMultirowLayout())
                itemset1.next();
        }

        trans = mgr.perform(trans);
        if (itemlay1.isMultirowLayout())
        {
            itemset1.setFilterOff();
            itemset1.unselectRows();
        }

        trans.clear();
        headset.refreshRow();
        okFindITEM1();
        evalUpper();
        itemset1.goTo(currrow);

    }

    public void releaseRequisitionLine1()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd;
        int currrow=0;
        int count;
        trans.clear();

        if (itemlay1.isMultirowLayout())
        {
            itemset1.storeSelections();
            itemset1.setFilterOn();
            count = itemset1.countSelectedRows();
        }
        else
        {
            currrow = itemset1.getCurrentRowNo();
            itemset1.unselectRows();
            count = 1;
        }

        for (int i=0; i<count; i++)
        {
            cmd = trans.addCustomCommand("ACTIVATE"+i, "Work_Order_Requis_Line_API.Activate__");
            cmd.addParameter("REQUISITION_NO",headset.getValue("REQUISITION_NO"));
            cmd.addParameter("LINE_NO",itemset1.getValue("LINE_NO"));
            cmd.addParameter("RELEASE_NO",itemset1.getValue("RELEASE_NO")); 

            if (itemlay1.isMultirowLayout())
                itemset1.next();
        }

        trans = mgr.perform(trans);
        if (itemlay1.isMultirowLayout())
        {
            itemset1.setFilterOff();
            itemset1.unselectRows();
        }

        trans.clear();
        headset.refreshRow();
        okFindITEM1();     
        evalUpper();
        itemset1.goTo(currrow);
    }

    // 040219  ARWILK  Begin  (Enable Multirow RMB actions)
    public void performITEM1(String command) 
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        if (itemlay1.isMultirowLayout())
        {
            itemset1.storeSelections();
            itemset1.markSelectedRows(command);
            mgr.submit(trans);
        }
        else
        {
            itemset1.unselectRows();
            itemset1.markRow(command);
            int currrow = itemset1.getCurrentRowNo();
            mgr.submit(trans);

            itemset1.goTo(currrow);
        }   
    }
    // 040219  ARWILK  End  (Enable Multirow RMB actions)

    public void prePost0()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd;

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());
        else
            itemset0.selectRow();

        trans.clear();

        cmd = trans.addCustomFunction("PRE", "Purchase_Req_Util_API.Get_Line_Pre_Accounting_Id", "PRE_POSTING_ID");
        cmd.addParameter("REQUISITION_NO", headset.getValue("REQUISITION_NO"));
        cmd.addParameter("LINE_NO", itemset0.getValue("LINE_NO"));  
        cmd.addParameter("RELEASE_NO", itemset0.getValue("RELEASE_NO"));      

        trans = mgr.perform(trans);

        String pre_posting_id = trans.getValue("PRE/DATA/PRE_POSTING_ID");

        // 040219  ARWILK  Begin  (Remove uneccessary global variables)
        bOpenNewWindow = true;
        urlString = "../mpccow/PreAccountingDlg1.page?STRING_CODE=" + mgr.URLEncode(itemset0.getValue("STRING_CODE")) +
                    "&CONTRACT=" + mgr.URLEncode(itemset0.getRow().getFieldValue("ITEM0_CONTRACT")) +
                    "&PRE_ACCOUNTING_ID=" + mgr.URLEncode(pre_posting_id) +
                    "&ENABL10=" + mgr.URLEncode(itemset0.getValue("ENABL10"));

        newWinHandle = "prePost0"; 
        // 040219  ARWILK  End  (Remove uneccessary global variables)
    }

    public void prePost1()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd;

        if (itemlay1.isMultirowLayout())
            itemset1.goTo(itemset1.getRowSelected());
        else
            itemset1.selectRow();

        trans.clear();

        cmd = trans.addCustomFunction("PRE2", "Purchase_Req_Util_API.Get_Line_Pre_Accounting_Id", "PRE_POSTING_ID");
        cmd.addParameter("REQUISITION_NO", headset.getValue("REQUISITION_NO"));
        cmd.addParameter("LINE_NO", itemset1.getValue("LINE_NO"));  
        cmd.addParameter("RELEASE_NO", itemset1.getValue("RELEASE_NO"));       

        trans = mgr.perform(trans);

        String pre_posting_id = trans.getValue("PRE2/DATA/PRE_POSTING_ID");

        // 040219  ARWILK  Begin  (Remove uneccessary global variables)
        bOpenNewWindow = true;
        urlString = "../mpccow/PreAccountingDlg1.page?STRING_CODE=" + mgr.URLEncode(itemset1.getRow().getValue("STRING_CODE")) +
                    "&CONTRACT=" + mgr.URLEncode(itemset1.getValue("ITEM1_CONTRACT")) +
                    "&PRE_ACCOUNTING_ID=" + mgr.URLEncode(pre_posting_id) +
                    "&ENABL10=" + mgr.URLEncode(itemset1.getRow().getValue("ENABL10"));

        newWinHandle = "prePost1"; 
        // 040219  ARWILK  End  (Remove uneccessary global variables)
    }

    public void prePost()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd;

        String pre_posting_id;

        calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        cmd = trans.addCustomFunction("PRE", "Purchase_Requisition_API.Get_Pre_Accounting_Id", "PRE_POSTING_ID");
        cmd.addParameter("REQUISITION_NO", headset.getValue("REQUISITION_NO"));

        trans = mgr.perform(trans);

        pre_posting_id = trans.getValue("PRE/DATA/PRE_POSTING_ID");

        trans.clear();

        GetEnabledFields();

        ASPBuffer prePostBuffer = mgr.newASPBuffer();
        ASPBuffer data = prePostBuffer.addBuffer("dataBuffer");

        data.addItem("CONTRACT", headset.getValue("CONTRACT"));
        data.addItem("PRE_ACCOUNTING_ID", pre_posting_id);
        data.addItem("ENABL0", Integer.toString(enabl0));
        data.addItem("ENABL1", Integer.toString(enabl1));
        data.addItem("ENABL2", Integer.toString(enabl2));
        data.addItem("ENABL3", Integer.toString(enabl3));
        data.addItem("ENABL4", Integer.toString(enabl4));
        data.addItem("ENABL5", Integer.toString(enabl5));
        data.addItem("ENABL6", Integer.toString(enabl6));
        data.addItem("ENABL7", Integer.toString(enabl7));
        data.addItem("ENABL8", Integer.toString(enabl8));
        data.addItem("ENABL9", Integer.toString(enabl9));
        data.addItem("ENABL10", Integer.toString(0));

        ASPBuffer returnBuffer = prePostBuffer.addBuffer("return_buffer");

        ASPBuffer ret = returnBuffer.addBuffer("ROWS");
        ret.addItem("WO_NO", Double.toString(upperset.getRow().getNumberValue("UP_WO_NO")));
        ret.addItem("MCH_CODE", upperset.getRow().getValue("UP_MCH_CODE"));
        ret.addItem("MCH_NAME", upperset.getRow().getValue("MCH_NAME"));

        if (headlay.isSingleLayout() && headset.countRows() > 0)
        {
            ret.addItem("REQUISITION_NO", headset.getRow().getValue("REQUISITION_NO"));
        }

        mgr.transferDataTo("../mpccow/PreAccountingDlg.page", prePostBuffer);
    } 

    public void GetEnabledFields()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd;

        int code_a;
        int code_b;
        int code_c;
        int code_d;
        int code_e;
        int code_f;
        int code_g;
        int code_h;
        int code_i;
        int code_j;

        trans.clear();

        cmd=trans.addCustomFunction("COM","Site_API.Get_Company","COMPANY");
        cmd.addParameter("CONTRACT",headset.getValue("CONTRACT"));

        cmd = trans.addCustomCommand( "POSTI", "Pre_Accounting_API.Get_Allowed_Codeparts");  
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
        cmd.addParameter("STR_CODE","M114"); 
        cmd.addParameter("CONTROL_TYPE"); 
        cmd.addReference("COMPANY","COM/DATA");

        trans = mgr.perform(trans);

        code_a = toInt(trans.getNumberValue("POSTI/DATA/CODE_A"));
        code_b = toInt(trans.getNumberValue("POSTI/DATA/CODE_B"));
        code_c = toInt(trans.getNumberValue("POSTI/DATA/CODE_C"));
        code_d = toInt(trans.getNumberValue("POSTI/DATA/CODE_D"));
        code_e = toInt(trans.getNumberValue("POSTI/DATA/CODE_E"));
        code_f = toInt(trans.getNumberValue("POSTI/DATA/CODE_F"));
        code_g = toInt(trans.getNumberValue("POSTI/DATA/CODE_G"));
        code_h = toInt(trans.getNumberValue("POSTI/DATA/CODE_H"));
        code_i = toInt(trans.getNumberValue("POSTI/DATA/CODE_I"));
        code_j = toInt(trans.getNumberValue("POSTI/DATA/CODE_J"));


        enabl0 = ((code_a == 0) ? 0 : 1);
        enabl1 = ((code_b == 0) ? 0 : 1);
        enabl2 = ((code_c == 0) ? 0 : 1);
        enabl3 = ((code_d == 0) ? 0 : 1);
        enabl4 = ((code_e == 0) ? 0 : 1);
        enabl5 = ((code_f == 0) ? 0 : 1);
        enabl6 = ((code_g == 0) ? 0 : 1);
        enabl7 = ((code_h == 0) ? 0 : 1);
        enabl8 = ((code_i == 0) ? 0 : 1);
        enabl9 = ((code_j == 0) ? 0 : 1);

        if ("Closed".equals(headset.getValue("STATUSCODE")))
        {
            enabl0 = 0;
            enabl1 = 0;
            enabl2 = 0;
            enabl3 = 0;
            enabl4 = 0;
            enabl5 = 0;
            enabl6 = 0;
            enabl7 = 0;
            enabl8 = 0;
            enabl9 = 0;
        }
    }

    public void supplierPerPart()
    {
        ASPManager mgr = getASPManager();   

        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;

        transferBuffer = mgr.newASPBuffer();
        rowBuff = mgr.newASPBuffer();

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());
        else
            itemset0.selectRow();

        rowBuff.addItem("PART_NO", itemset0.getRow().getValue("PART_NO"));
        rowBuff.addItem("CONTRACT", itemset0.getRow().getFieldValue("ITEM0_CONTRACT"));

        transferBuffer.addBuffer("DATA", rowBuff);

        bOpenNewWindow = true;
        urlString = createTransferUrl("../purchw/PurchasePartSupplier.page", transferBuffer);
        newWinHandle = "PurchasePartSupplier"; 

        evalUpper();
    }

    /** "Requisition to Request" rmb invokes in Part Requisition Lines */
    public void requisitionToRequestMain0()
    {
        ASPManager mgr = getASPManager();

        itemValue = 0;

        if (itemlay0.isMultirowLayout())
        {
            itemset0.storeSelections();
            itemset0.setFilterOn();
        }
        else
        {
            itemset0.unselectRows();
            itemset0.selectRow();
        }

        reqtoreq = true;
        setRequisitionToRequest(0);
        requestlay.setLayoutMode(ASPBlockLayout.NEW_LAYOUT);
    }

    /** "Requisition to Request" rmb invokes in No Part Requisition Lines */
    public void requisitionToRequestMain1()
    {
        ASPManager mgr = getASPManager();

        itemValue = 1;

        if (itemlay1.isMultirowLayout())
        {
            itemset1.storeSelections();
            itemset1.setFilterOn();
        }
        else
        {
            itemset1.unselectRows();
            itemset1.selectRow();
        }

        reqtoreq = true;
        setRequisitionToRequest(1);
        requestlay.setLayoutMode(ASPBlockLayout.NEW_LAYOUT);
    }

    /** Set the "Requisition to Request" */
    public void setRequisitionToRequest(int item)
    {
        ASPManager mgr = getASPManager();

        String sUnitMeas = "";
        String sBuyerCode = "";
        String sCurrencyCode = "";
        String sTempCurrency = "";
        String sTempBuyer = "";
        String sTempUnitMeas = "";

        if (item == 0)  //  For part requisition line
        {
            itemset0.first();
            int nRows = itemset0.countSelectedRows();

            sTempCurrency = itemset0.getValue("ITEM0_CURRENCY_CODE");
            sTempBuyer = itemset0.getValue("BUYER_CODE");
            sTempUnitMeas = itemset0.getValue("BUY_UNIT_MEAS");

            for (int i = 0; i < nRows; i++)
            {
                sItemContract = sItemContract + itemset0.getRow().getFieldValue("ITEM0_CONTRACT") + " ";
                sLineNo = sLineNo + itemset0.getValue("LINE_NO") + " ";
                sReleaseNo = sReleaseNo + itemset0.getValue("RELEASE_NO") + " ";
                sItemPartNo = sItemPartNo + itemset0.getValue("PART_NO") + " ";
                sItemWantedDeliveryDate = sItemWantedDeliveryDate + itemset0.getRow().getFieldValue("WANTEDDELIVERYDATE") + " ";
                sItemOriginalQty = sItemOriginalQty + itemset0.getValue("ORIGINAL_QTY") + " ";

                if (!mgr.isEmpty(sTempCurrency) || !mgr.isEmpty(itemset0.getValue("ITEM0_CURRENCY_CODE")))
                {
                    if (!sTempCurrency.equals(itemset0.getValue("ITEM0_CURRENCY_CODE")))
                        sCurrencyCode = null;
                    else
                        sCurrencyCode = itemset0.getValue("ITEM0_CURRENCY_CODE");
                }

                if (!mgr.isEmpty(sTempBuyer) || !mgr.isEmpty(itemset0.getValue("BUYER_CODE")))
                {
                    if (!sTempBuyer.equals(itemset0.getValue("BUYER_CODE")))
                        sBuyerCode = null;
                    else
                        sBuyerCode = itemset0.getValue("BUYER_CODE");
                }

                if (!mgr.isEmpty(sTempUnitMeas) || !mgr.isEmpty(itemset0.getValue("BUY_UNIT_MEAS")))
                {
                    if (!sTempUnitMeas.equals(itemset0.getValue("BUY_UNIT_MEAS")))
                        sUnitMeas = null;
                    else
                        sUnitMeas = itemset0.getValue("BUY_UNIT_MEAS");
                }

                itemset0.next();
            }

            sItemProjectId = itemset0.getValue("ITEM0_PROJECT_ID");
            sRequisitionNo = headset.getValue("REQUISITION_NO");
        }
        else if (item == 1) //  For no part requisition line
        {
            itemset1.first();
            int nRows = itemset1.countSelectedRows();

            sTempCurrency = itemset1.getValue("ITEM1_CURRENCY_CODE");
            sTempBuyer = itemset1.getValue("BUYER_CODE");
            sTempUnitMeas = itemset1.getValue("BUY_UNIT_MEAS");

            for (int i = 0 ; i < nRows ; i++)
            {
                sItemContract = sItemContract + itemset1.getValue("CONTRACT") + " ";
                sLineNo = sLineNo + itemset1.getValue("LINE_NO") + " ";
                sReleaseNo = sReleaseNo + itemset1.getValue("RELEASE_NO") + " ";
                sItemWantedDeliveryDate = sItemWantedDeliveryDate + itemset1.getRow().getFieldValue("ITEM1_WANTEDDELIVERYDATE") + " ";
                sItemOriginalQty = sItemOriginalQty + itemset1.getValue("ORIGINAL_QTY") + " ";

                if (!mgr.isEmpty(sTempCurrency) || !mgr.isEmpty(itemset1.getValue("ITEM1_CURRENCY_CODE")))
                {
                    if (!sTempCurrency.equals(itemset1.getValue("ITEM1_CURRENCY_CODE")))
                        sCurrencyCode = null;
                    else
                        sCurrencyCode = itemset1.getValue("ITEM1_CURRENCY_CODE");
                }

                if (!mgr.isEmpty(sTempBuyer) || !mgr.isEmpty(itemset1.getValue("BUYER_CODE")))
                {
                    if (!sTempBuyer.equals(itemset1.getValue("BUYER_CODE")))
                        sBuyerCode = null;
                    else
                        sBuyerCode = itemset1.getValue("BUYER_CODE");
                }

                if (!mgr.isEmpty(sTempUnitMeas) || !mgr.isEmpty(itemset1.getValue("BUY_UNIT_MEAS")))
                {
                    if (!sTempUnitMeas.equals(itemset1.getValue("BUY_UNIT_MEAS")))
                        sUnitMeas = null;
                    else
                        sUnitMeas = itemset1.getValue("BUY_UNIT_MEAS");
                }

                itemset1.next();
            }

            sItemProjectId = itemset1.getValue("ITEM1_PROJECT_ID");
            sRequisitionNo = headset.getValue("REQUISITION_NO");
            sItemPartNo = "";
        }

        // Adding a header row to the Requisition to Request to dialog
        ASPBuffer data;
        data = mgr.newASPBuffer();
        data.addFieldItem("EXPIRY_DATE", "");
        data.addFieldItem("ADDTO_REQUEST", "");
        data.addFieldItem("REQ_CURRENCY_CODE", sCurrencyCode);
        data.addFieldItem("REQ_BUY_UNIT_MEAS", sUnitMeas);
        data.addFieldItem("REQ_BUYER_CODE", sBuyerCode);
        requestset.addRow(data);

        reqLinePopulateTable(sCurrencyCode, sBuyerCode, sItemContract, sItemProjectId, item);
    }

    /** Populate the child tabel in "Requisition to Request" dialog box */
    public void reqLinePopulateTable( String sCurrcode,String sBuyer,String sContract,String nProjectid, int item)
    {
        ASPManager mgr = getASPManager();
        ASPQuery q;
        String sInternalDestinationId;
        String sCurrency;
        ASPBuffer reqData;
        int currentRow;
        String tempContract = "";

        // Prepare SQL statement to populate the child table in the Requisition to Requset dialog
        sCurrency = (mgr.isEmpty(sCurrcode) ? "NULL" : sCurrcode);
        reqData = requestset.getRow();
        requestset.clear();
        sInternalDestinationId = (mgr.isEmpty(headset.getValue("DESTINATION_ID")) ? "" : headset.getValue("DESTINATION_ID"));
        sRequisitionNo = headset.getValue("REQUISITION_NO");

        currentRow = headset.getCurrentRowNo();

        if (item == 0)
            tempContract = (mgr.isEmpty(sContract)? "NULL" : itemset0.getRow().getFieldValue("ITEM0_CONTRACT"));
        else
            tempContract = (mgr.isEmpty(sContract)? "NULL" : itemset1.getRow().getFieldValue("ITEM1_CONTRACT"));

        trans.clear();
        q = trans.addEmptyQuery(reqlineblk);
        q.addWhereCondition("PRINTED_FLAG_DB = 'N'");
        q.addWhereCondition("INQUIRY_ORDER_API.Check_Internal_Destination__(inquiry_no, ? , ? )=1");
        q.addWhereCondition("nvl(CURRENCY_CODE,chr(2)) =nvl( ? ,chr(2))");
        q.addWhereCondition("BUYER_CODE = ?");
        q.addWhereCondition("CONTRACT = ?");
	q.addParameter("DESTINATION_ID",sInternalDestinationId);
	q.addParameter("REQUISITION_NO",sRequisitionNo);
	q.addParameter("ITEM1_CURRENCY_CODE",sCurrency);
	q.addParameter("BUYER_CODE",sBuyer);
	q.addParameter("UP_CONTRACT",tempContract);
	q.addWhereCondition("OBJSTATE IN ('Planned' , 'Revised', 'Released')");
	String sNvlCondtn="(" + (mgr.isEmpty(nProjectid) ? nProjectid : "'" + nProjectid + "'") + ",chr(2))";
	q.addWhereCondition("nvl(PROJECT_ID,chr(2)) = ?");
	q.addParameter("LINE_PROJECT_ID",sNvlCondtn);

	q.setOrderByClause("DATE_EXPIRES DESC");

        q.includeMeta("ALL");

        mgr.submit(trans);
        headset.goTo(currentRow);
        requestset.addRow(reqData);
    }

    /**    Invokes when user selects the action "Requisition to Order" from part lines*/
    public void requisitionToOrderMain0()
    {
        ASPManager mgr = getASPManager();
        ASPCommand cmd;
        selBlk = 0;

        int contRows = 0;
        String sBuyer;
        boolean bMultiBuyer = false;

        saveRowNos();

        if (itemlay0.isMultirowLayout())
        {
            itemset0.storeSelections();
            itemset0.setFilterOn();
            contRows = itemset0.countSelectedRows();
        }
        else
        {
            itemset0.unselectRows();
            itemset0.selectRow();
            contRows = 1;
        }

        itemset0.first();
        selectedRowsBuffer = itemset0.getSelectedRows();
        sBuyer = itemset0.getValue("BUYER_CODE");

        if (contRows > 1)
        {

            itemset0.next();

            for (int i = 1; i < contRows; i++)
            {
                String sLineBuyer = itemset0.getValue("BUYER_CODE");

                if (mgr.isEmpty(sBuyer) && !mgr.isEmpty(sLineBuyer))
                {
                    bMultiBuyer = true;
                    break;
                }
                else if (!sBuyer.equals(sLineBuyer))
                {
                    bMultiBuyer = true;
                    break;
                }

                if (itemlay0.isMultirowLayout())
                    itemset0.next();
            }

            if (bMultiBuyer)
                sBuyer = "";
        }

        populateOrderHeader(true, sBuyer, "", "");
        reqtoord = true;

        if (itemlay0.isMultirowLayout())
            itemset0.setFilterOff();
    }

    /**    Invokes when user selects the action "Requisition to Order" from no part lines*/
    public void requisitionToOrderMain1()
    {
        ASPManager mgr = getASPManager();
        ASPCommand cmd;
        selBlk = 1;

        int contRows = 0;
        String sBuyer;
        boolean bMultiBuyer = false;

        saveRowNos();

        if (itemlay1.isMultirowLayout())
        {
            itemset1.storeSelections();
            itemset1.setFilterOn();
            contRows = itemset1.countSelectedRows();
        }
        else
        {
            itemset1.unselectRows();
            itemset1.selectRow();
            contRows = 1;
        }

        itemset1.first();
        selectedRowsBuffer = itemset1.getSelectedRows();
        sBuyer = itemset1.getValue("BUYER_CODE");

        if (contRows > 1)
        {
            itemset1.next();

            for (int i = 1; i < contRows; i++)
            {
                String sLineBuyer = itemset1.getValue("BUYER_CODE");

                if (mgr.isEmpty(sBuyer) && !mgr.isEmpty(sLineBuyer))
                {
                    bMultiBuyer = true;
                    break;
                }
                else if (!sBuyer.equals(sLineBuyer))
                {
                    bMultiBuyer = true;
                    break;
                }

                if (itemlay1.isMultirowLayout())
                    itemset1.next();
            }

            if (bMultiBuyer)
                sBuyer = "";
        }

        populateOrderHeader(false, sBuyer, "", "");
        reqtoord = true;

        if (itemlay1.isMultirowLayout())
            itemset1.setFilterOff();
    }

    private void populateOrderHeader(boolean isPartLines, String sBuyer, String defAuthCode, String defAuthName)
    {
        ASPManager mgr = getASPManager();
        ASPQuery q;
        ASPCommand cmd;

        String n1;
        String n2;

        trans.clear(); 
        q = trans.addQuery("COODLISTCOUNT","ORDER_COORDINATOR","TO_CHAR(COUNT(*)) N","","");
        q = trans.addQuery("BUYERLISTCOUNT","PURCHASE_BUYER","TO_CHAR(COUNT(*)) N","","");

        trans = mgr.perform(trans);

        n1 =  trans.getValue("COODLISTCOUNT/DATA/N");
        n2 =  trans.getValue("BUYERLISTCOUNT/DATA/N");

        trans.clear();

        q = trans.addQuery("COODLIST","ORDER_COORDINATOR","AUTHORIZE_CODE,AUTHORIZE_CODE","","");
        q.setBufferSize(toInt(n1));
        q = trans.addQuery("BUYERLIST","PURCHASE_BUYER","BUYER_CODE,BUYER_CODE","","");
        q.setBufferSize(toInt(n2));

        if (mgr.isEmpty(defAuthCode))
        {
            cmd = trans.addCustomFunction( "DEFAULT_AUTHORIZER", "User_Default_Api.Get_Authorize_Code", "DEFAUTH" );

            cmd = trans.addCustomFunction( "DEFAULT_AUTHORIZER_NAME", "Order_Coordinator_API.Get_Name", "COOR_NAME" );
            cmd.addReference("DEFAUTH","DEFAULT_AUTHORIZER/DATA");
        }

        cmd = trans.addCustomFunction( "BUYERNAME", "Purchase_Buyer_API.Get_Name", "BUYER_NAME" );
        cmd.addParameter("BUYER_CODE",sBuyer);

        trans = mgr.perform(trans);

        defauthDataBuffer =  trans.getBuffer("COODLIST");
        defbuyDataBuffer =  trans.getBuffer("BUYERLIST");

        if (mgr.isEmpty(defAuthCode))
        {
            defAuthCode = trans.getValue("DEFAULT_AUTHORIZER/DATA/DEFAUTH");
            defAuthName = trans.getValue("DEFAULT_AUTHORIZER_NAME/DATA/COOR_NAME");
        }
        String sBuyerName = trans.getValue("BUYERNAME/DATA/BUYER_NAME");

        if (mgr.isEmpty(defAuthName))
            defAuthName = " ";

        ctx.writeBuffer("DEFAUTHDATBUF", defauthDataBuffer);
        ctx.writeBuffer("BUYDATABUFFER", defbuyDataBuffer);

        ASPBuffer orderHeader = mgr.newASPBuffer();
        orderHeader.setFieldItem("DEFAUTH", defAuthCode);
        orderHeader.setFieldItem("COOR_NAME", defAuthName);        
        orderHeader.setFieldItem("DEFBUYER", sBuyer);
        orderHeader.setFieldItem("BUYER_NAME", sBuyerName);

        trans.clear();

        String sOrderCode = headset.getValue("ORDER_CODE");

        String sItemSupplier = null;
        String sItemContract = null;
        String sItemDemandOrder = null;
        String sItemProcessType = null;

        if (isPartLines)
        {
            // For part requisition line
            sItemSupplier =itemset0.getValue("VENDOR_NO");
            sItemContract =itemset0.getRow().getFieldValue("ITEM0_CONTRACT");
            sItemProcessType = itemset0.getValue("PROCESS_TYPE");
        }
        else
        {
            // For no part requisition line
            sItemSupplier =itemset1.getValue("VENDOR_NO");
            sItemContract =itemset1.getValue("CONTRACT");
            sItemProcessType = "";
        }

        populateOrderLines(isPartLines,defAuthCode,sBuyer,sItemSupplier,sItemContract,sOrderCode,sItemDemandOrder,sItemProcessType);

        orderset.addRow(orderHeader);
    }

    private void populateOrderLines(boolean isPartLines,String sAuthorize,String sBuyer,String sSupplier,String sContract,String sOrderCode,String sdemandOrderNo,String sProcessType)
    {
        ASPManager mgr = getASPManager();
        ASPQuery q;
        AutoString subSql = new AutoString();
	
        String strDirecrDelAddrWhere = getDirectDeliveryAddress(isPartLines);

        trans.clear();
        lineset.clear();
        String sInternalDestinationId = headset.getValue("DESTINATION_ID");
        String sRequitionNo = headset.getValue("REQUISITION_NO");

        // Creating subsql query

	// SQLInjection_Safe AMNILK 20070716
        
	subSql.append("( AUTHORIZE_CODE = ?) AND ");
        subSql.append("( BUYER_CODE = ?) AND ");
        subSql.append("( VENDOR_NO = ?) AND ");
        subSql.append("( CONTRACT = ?) AND ");
        subSql.append("( po.objstate IN ('Planned', 'Released')) AND ");
        subSql.append("( po.order_code  = ?) AND ");
        subSql.append("( ? != '2' OR  (", strDirecrDelAddrWhere, ")) AND ");
        subSql.append("( PURCHASE_ORDER_API.Check_Internal_Destination__(po.order_no, ?, ?)=1) AND ");
        subSql.append("( nvl(", (mgr.isEmpty(sProcessType) ? "null" : "?" ), ",chr(2))  =  (SELECT nvl(pol.process_type,chr(2))  FROM purchase_order_line pol WHERE pol.order_no = po.order_no AND ROWNUM = 1  ) OR  NOT EXISTS ( SELECT 1 FROM purchase_order_line pol  WHERE pol.order_no = po.order_no ))");

        q = trans.addQuery(lineblk);
        q.addWhereCondition(subSql.toString());
        q.addParameter("AUTH_ID", sAuthorize);
	q.addParameter("DEF_BUYER_CODE", sBuyer);
	q.addParameter("VENDOR_NO", sSupplier);
	q.addParameter("CONTRACT", sContract);
	q.addParameter("ORDER_CODE", sOrderCode);
	q.addParameter("ORDER_CODE", sOrderCode);
	q.addParameter("INTERNAL_DESTINATION", sInternalDestinationId);
	q.addParameter("REQUISITION_NO", sRequitionNo);

	if ( !mgr.isEmpty(sProcessType) ) {
	    q.addParameter("PROCESS_TYPE",sProcessType);
	}

        q.setOrderByClause("ORDER_DATE DESC");
        q.includeMeta("ALL");

        mgr.submit(trans);
        trans.clear();
    }

    private void saveRowNos()
    {
        currHeadRow = headset.getCurrentRowNo();
        currItem0Row = itemset0.getCurrentRowNo();
        currItem1Row = itemset1.getCurrentRowNo();
    }

    private void goToRows()
    {
        headset.goTo(currHeadRow);
        if (itemset0.countRows()>0)
            itemset0.goTo(currItem0Row);
        if (itemset1.countRows()>0)
            itemset1.goTo(currItem1Row);

        itemset0.unselectRows();
        itemset1.unselectRows();
    }

    public String getDirectDeliveryAddress(boolean isPartLines)
    {
        ASPManager mgr = getASPManager();
        ASPCommand cmd;

        String strDirecrDelAddrWhere;
        String sAddr1 = null;
        String sAddr2 = null;
        String sAddr3 = null;
        String sAddr4 = null;
        String sAddr5 = null;
        String sAddr6 = null;

        if ("2".equals(headset.getValue("ORDER_CODE")))
        {
            trans.clear();

            cmd = trans.addCustomCommand( "DELADDR", "Purchase_Req_Util_API.Get_Direct_Deliv_Addr");
            cmd.addParameter("ADDR1", "");
            cmd.addParameter("ADDR2", "");
            cmd.addParameter("ADDR3", "");
            cmd.addParameter("ADDR4", "");
            cmd.addParameter("ADDR5", "");
            cmd.addParameter("ADDR6", "");
            cmd.addParameter("REQUISITION_NO", headset.getValue("REQUISITION_NO"));

            if (isPartLines)
            {
                cmd.addParameter("LINE_NO", itemset0.getValue("LINE_NO"));
                cmd.addParameter("RELEASE_NO", itemset0.getValue("RELEASE_NO"));
            }
            else
            {
                cmd.addParameter("LINE_NO", itemset1.getValue("LINE_NO"));
                cmd.addParameter("RELEASE_NO", itemset1.getValue("RELEASE_NO"));
            }

            trans = mgr.perform(trans);

            sAddr1 = trans.getValue("DELADDR/DATA/ADDR1");
            sAddr2 = trans.getValue("DELADDR/DATA/ADDR2");
            sAddr3 = trans.getValue("DELADDR/DATA/ADDR3");
            sAddr4 = trans.getValue("DELADDR/DATA/ADDR4");
            sAddr5 = trans.getValue("DELADDR/DATA/ADDR5");
            sAddr6 = trans.getValue("DELADDR/DATA/ADDR6");

            sAddr1 = (mgr.isEmpty(sAddr1) ? null : "'" + sAddr1 + "'");
            sAddr2 = (mgr.isEmpty(sAddr2) ? null : "'" + sAddr2 + "'");
            sAddr3 = (mgr.isEmpty(sAddr3) ? null : "'" + sAddr3 + "'");
            sAddr4 = (mgr.isEmpty(sAddr4) ? null : "'" + sAddr4 + "'");
            sAddr5 = (mgr.isEmpty(sAddr5) ? null : "'" + sAddr5 + "'");
            sAddr6 = (mgr.isEmpty(sAddr6) ? null : "'" + sAddr6 + "'");
        }

        strDirecrDelAddrWhere = "nvl(po.recipient_name,chr(2)) = nvl(" + sAddr1 + ",chr(2)) AND nvl(po.address1,chr(2)) =nvl(" + sAddr2 + ",chr(2)) AND nvl(po.address2,chr(2)) =nvl(" + sAddr3 + ",chr(2)) AND nvl(po.zip_code,chr(2)) = nvl(" + sAddr4 + ",chr(2)) AND nvl(po.city,chr(2)) = nvl(" + sAddr5 + ",chr(2)) AND nvl(po.addr_state,chr(2)) = nvl(" + sAddr6 + ",chr(2))";
        return strDirecrDelAddrWhere;
    }

    /** Invokes when the Create New button presses in "Requisition to Order" dialog */
    public void createNewOrder()
    {
        ASPManager mgr = getASPManager();
        String sOption;
        String sOrderNo;
        String receivedInfo;
        String orderNo;
        String lineNo;
        String releaseNo;
        String[] infoArray;

        sOrderNo = mgr.readValue("ADDTO_ORDER");
        sOption = "createnew";

        receivedInfo = createPurchaseOrder(sOrderNo,sOption);
        infoArray = split(receivedInfo,"^");

        orderNo = infoArray[0];
        lineNo = infoArray[1];
        releaseNo = infoArray[2];

        reqtoord = false;
        goToRows();
        refreshITEM();
    }

    public void autoSelect()
    {
        ASPManager mgr = getASPManager();
        String receivedInfo;
        String orderNo;
        String lineNo;
        String releaseNo;
        String[] infoArray;

        receivedInfo = createPurchaseOrder("","");
        infoArray = split(receivedInfo,"^");

        orderNo = infoArray[0];
        lineNo = infoArray[1];
        releaseNo = infoArray[2];

        reqtoord = false;
        goToRows();
        refreshITEM();
    }

    /** Invokes when the Add to Order button presses in "Requisition to Order" dialog */
    public void addToOrder(boolean fromRmb)
    {
        ASPManager mgr = getASPManager();
        String receivedInfo;
        String orderNo;
        String lineNo;
        String releaseNo;
        String sOrderNo;
        String[] infoArray;

        sOrderNo = ((!fromRmb) ? mgr.readValue("ADDTO_ORDER") : lineset.getValue("LINE_ORDER_NO"));
        if (mgr.isEmpty(sOrderNo) || !orderAvailable(sOrderNo))
            mgr.showAlert(mgr.translate("PCMWWORKORDERREQUISHEADERRMBNOTORD: &1 Invalid Order Number.", sOrderNo));
        else
        {
            receivedInfo = createPurchaseOrder(sOrderNo,"");
            infoArray = split(receivedInfo,"^");

            orderNo = infoArray[0];
            lineNo = infoArray[1];
            releaseNo = infoArray[2];

            fromRmb = false;
            reqtoord = false;
            goToRows();
            refreshITEM();
        }
    }

    public void addToOrderSelected()
    {
        lineset.store();
        addToOrder(true);
    }

    public boolean orderAvailable( String psOrderNo)
    {
        int linesetRowCount;
        String ordNo;
        linesetRowCount = lineset.countRows();
        lineset.first();
        for (int cnt = 0 ; cnt < linesetRowCount; cnt++)
        {
            ordNo = lineset.getValue("LINE_ORDER_NO");
            if (psOrderNo.equals(ordNo))
                return true;
            lineset.next();
        }
        lineset.first();
        return false;
    }

    /** Create Purchase Orders on different options in "Requisition to Order" dialog */
    public String createPurchaseOrder( String sOrderNo,String sOption)
    {
        ASPManager mgr = getASPManager();
        ASPCommand cmd;

        String outInfo;
        String orderNo;
        String lineNo;
        String releaseNo;
        String requisitionInfo;
        ASPBuffer currRow;

        int n = selectedRowsBuffer.countItems();
        currRow = selectedRowsBuffer.getBufferAt(0);

        trans.clear();

        String sFinalBuyer = mgr.readValue("DEFBUYER");
        String sBuyer = currRow.getValue("BUYER_CODE");
        String sVendor = currRow.getValue("VENDOR_NO"); 
        String sSuppContact = currRow.getValue("CONTACT"); 
        String sCurrCode = currRow.getValue("ITEM0_CURRENCY_CODE"); 


        for (int i=0; i < n; i++)
        {

            String sRequisitionNo = currRow.getValue("REQUISITION_NO"); 
            String sLineNo = currRow.getValue("LINE_NO"); 
            String sReleaseNo = currRow.getValue("RELEASE_NO"); 

            boolean newOrder = false;

            if (i > 0 && mgr.isEmpty(sOrderNo))
            {
                if (!mgr.isEmpty(sFinalBuyer))
                    newOrder = false;
                else if (!sBuyer.equals(selectedRowsBuffer.getBufferAt(i).getValueAt(0)))
                {
                    newOrder = true;
                    sFinalBuyer = null;
                }

                if (!sVendor.equals(selectedRowsBuffer.getBufferAt(i).getValueAt(1)))
                    newOrder = true;

                if (!sCurrCode.equals(selectedRowsBuffer.getBufferAt(i).getValueAt(3)))
                    newOrder = true;
            }
            else
            {
                newOrder = true;   
                if (isChecked)
                    sFinalBuyer = null;
            }

            cmd = trans.addCustomCommand( "NEWORDER" + i, "Purchase_Order_API.User_Requisition_Line_To_Order");

            if (!newOrder)
                cmd.addReference("REQ_ORDER_NO", "NEWORDER0/DATA");
            else
                cmd.addParameter("REQ_ORDER_NO", sOrderNo);

            cmd.addParameter("ASSG_LINE_NO,ASSG_RELEASE_NO");
            cmd.addParameter("REQ_INFO");
            cmd.addParameter("REQUISITION_NO", sRequisitionNo);
            cmd.addParameter("LINE_NO", sLineNo);
            cmd.addParameter("RELEASE_NO", sReleaseNo);
            cmd.addParameter("DEFAUTH", mgr.readValue("DEFAUTH"));
            cmd.addParameter("DEFBUYER", sFinalBuyer);

            if (newOrder)
                cmd.addParameter("NEW_ORDER", sOption);
            else
                cmd.addParameter("NEW_ORDER");

            cmd.addParameter("DEFAULT_BUYER", "" );
        }

        trans = mgr.perform(trans);

        outInfo = trans.getValue("MASTER/DATA/REQ_INFO");
        orderNo = trans.getValue("MASTER/DATA/REQ_ORDER_NO");
        lineNo = trans.getValue("MASTER/DATA/ASSG_LINE_NO");
        releaseNo = trans.getValue("MASTER/DATA/ASSG_RELEASE_NO");

        clearDlgs();

        return(orderNo + "^" + lineNo + "^" + releaseNo);
    }

    public void clearDlgs()
    {
        orderset.clear();
        lineset.clear();
        requestset.clear();
        reqlineset.clear();
    }

    public void purchaseOrder0()
    {
        ASPManager mgr = getASPManager();

        // 040219  ARWILK  Begin  (Enable Multirow RMB actions)
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
                rowBuff.addItem("ORDER_NO", itemset0.getValue("ORDER_NO"));
            else
                rowBuff.addItem(null, itemset0.getValue("ORDER_NO"));

            transferBuffer.addBuffer("DATA", rowBuff);

            if (itemlay0.isMultirowLayout())
                itemset0.next();
        }

        if (itemlay0.isMultirowLayout())
            itemset0.setFilterOff();

        urlString = createTransferUrl("../purchw/PurchaseOrder.page", transferBuffer);
        newWinHandle = "purchaseOrder0"; 
        // 040219  ARWILK  End  (Enable Multirow RMB actions)
    }

    /**    Redirects the user selected line to Inquiry page */
    public void request0()
    {
        ASPManager mgr = getASPManager();

        // 040219  ARWILK  Begin  (Enable Multirow RMB actions)
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
                rowBuff.addItem("INQUIRY_NO", itemset0.getValue("INQUIRY_NO"));
                rowBuff.addItem("CONTRACT_NO", itemset0.getRow().getFieldValue("ITEM0_CONTRACT"));
            }
            else
            {
                rowBuff.addItem(null, itemset0.getValue("INQUIRY_NO"));
                rowBuff.addItem(null, itemset0.getRow().getFieldValue("ITEM0_CONTRACT"));
            }

            transferBuffer.addBuffer("DATA", rowBuff);

            if (itemlay0.isMultirowLayout())
                itemset0.next();
        }

        if (itemlay0.isMultirowLayout())
            itemset0.setFilterOff();

        urlString = createTransferUrl("../purchw/Inquiry.page", transferBuffer);
        newWinHandle = "request0"; 
        // 040219  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void purchaseOrder1()
    {
        ASPManager mgr = getASPManager();

        // 040219  ARWILK  Begin  (Enable Multirow RMB actions)
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
                rowBuff.addItem("ORDER_NO", itemset1.getValue("ORDER_NO"));
            else
                rowBuff.addItem(null, itemset1.getValue("ORDER_NO"));

            transferBuffer.addBuffer("DATA", rowBuff);

            if (itemlay1.isMultirowLayout())
                itemset1.next();
        }

        if (itemlay1.isMultirowLayout())
            itemset1.setFilterOff();

        urlString = createTransferUrl("../purchw/PurchaseOrder.page", transferBuffer);
        newWinHandle = "purchaseOrder1"; 
        // 040219  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void refreshForm()
    {
        ASPManager mgr = getASPManager();
        okFindITEM0(); 
    }


    public void request1()
    {
        ASPManager mgr = getASPManager();

        // 040219  ARWILK  Begin  (Enable Multirow RMB actions)
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
                rowBuff.addItem("INQUIRY_NO", itemset1.getValue("ITEM1_INQUIRY_NO"));
                rowBuff.addItem("CONTRACT_NO", itemset1.getValue("CONTRACT"));
            }
            else
            {
                rowBuff.addItem(null, itemset1.getValue("ITEM1_INQUIRY_NO"));
                rowBuff.addItem(null, itemset1.getValue("CONTRACT"));
            }

            transferBuffer.addBuffer("DATA", rowBuff);

            if (itemlay1.isMultirowLayout())
                itemset1.next();
        }

        if (itemlay1.isMultirowLayout())
            itemset1.setFilterOff();

        urlString = createTransferUrl("../purchw/Inquiry.page", transferBuffer);
        newWinHandle = "request1"; 
        // 040219  ARWILK  End  (Enable Multirow RMB actions)
    }


    public void notes0()
    {
	ASPManager mgr = getASPManager();
	ASPCommand cmd;

	String sNotetxt    = "";
	String sObjVersion = "";
         
	trans.clear();

	if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());

	cmd = trans.addCustomCommand("GETOBJVERION","Part_Wo_Requis_Line_Api.Get_Id_Ver_By_Keys__");
        cmd.addParameter("OBJID");
        cmd.addParameter("OBJVERSION");
	cmd.addParameter("WO_NO", headset.getValue("WO_NO")); 
	cmd.addParameter("REQUISITION_NO", headset.getValue("REQUISITION_NO")); 
	cmd.addParameter("LINE_NO", itemset0.getValue("LINE_NO")); 
	cmd.addParameter("RELEASE_NO", itemset0.getValue("RELEASE_NO"));

	if ( checksec("Purchase_Req_Util_API.Get_Line_Note_Text",3) )
        {
           cmd = trans.addCustomFunction("GETLINENOTETXT","Purchase_Req_Util_API.Get_Line_Note_Text","NOTETXT");
           cmd.addParameter("REQUISITION_NO", headset.getValue("REQUISITION_NO"));
           cmd.addParameter("LINE_NO", itemset0.getValue("LINE_NO"));    
	   cmd.addParameter("RELEASE_NO", itemset0.getValue("RELEASE_NO")); 
        }

	trans = mgr.perform(trans);

	sNotetxt    = trans.getValue("GETLINENOTETXT/DATA/NOTETXT");
	sObjVersion = trans.getBuffer("GETOBJVERION/DATA").getValue("OBJVERSION");

	bOpenNewWindow = true;
        urlString = "NotesDlg.page?NOTETXT=" + sNotetxt + 
	            "&OBJID=" + mgr.URLEncode(itemset0.getValue("OBJID")) +
	            "&OBJVERSION=" + sObjVersion +
	            "&REQNO=" + mgr.URLEncode(itemset0.getValue("REQUISITION_NO")) +
	            "&WONO=" + mgr.URLEncode(itemset0.getValue("WO_NO")) +
	            "&LINENO=" + mgr.URLEncode(itemset0.getValue("LINE_NO")) +
	            "&RELEASENO=" + mgr.URLEncode(itemset0.getValue("RELEASE_NO")) +
	            "&IDENTIFICATION=" + mgr.URLEncode("PARTS") + 
                    "&FRM_NAME=" + mgr.URLEncode("NotesDlg");
        newWinHandle = "notes";
    }


    public void notes1()
    {
	ASPManager mgr = getASPManager();
	ASPCommand cmd;

	String sNotetxt    = "";
	String sObjVersion = "";
         
	trans.clear();

	if (itemlay1.isMultirowLayout())
           itemset1.goTo(itemset1.getRowSelected());

	   cmd = trans.addCustomCommand("GETOBJVERION","NoPart_Wo_Requis_Line_Api.Get_Id_Ver_By_Keys__");
           cmd.addParameter("OBJID");
           cmd.addParameter("OBJVERSION");
	   cmd.addParameter("WO_NO", headset.getValue("WO_NO")); 
	   cmd.addParameter("REQUISITION_NO", headset.getValue("REQUISITION_NO")); 
	   cmd.addParameter("LINE_NO", itemset1.getValue("LINE_NO")); 
	   cmd.addParameter("RELEASE_NO", itemset1.getValue("RELEASE_NO"));

	if ( checksec("Purchase_Req_Util_API.Get_Line_Note_Text",3) )
        {
           cmd = trans.addCustomFunction("GETLINENOTETXT","Purchase_Req_Util_API.Get_Line_Note_Text","NOTETXT");
           cmd.addParameter("REQUISITION_NO", headset.getValue("REQUISITION_NO"));
           cmd.addParameter("LINE_NO", itemset1.getValue("LINE_NO"));  
	   cmd.addParameter("RELEASE_NO", itemset1.getValue("RELEASE_NO")); 
        }

	trans = mgr.perform(trans);

	sNotetxt    = trans.getValue("GETLINENOTETXT/DATA/NOTETXT");
	sObjVersion = trans.getBuffer("GETOBJVERION/DATA").getValue("OBJVERSION");

	bOpenNewWindow = true;
        urlString = "NotesDlg.page?NOTETXT=" + sNotetxt +
	            "&OBJID=" + mgr.URLEncode(itemset1.getValue("OBJID")) +
	            "&OBJVERSION=" + sObjVersion +
	            "&REQNO=" + mgr.URLEncode(itemset1.getValue("REQUISITION_NO")) +
	            "&WONO=" + mgr.URLEncode(itemset1.getValue("WO_NO")) +
	            "&LINENO=" + mgr.URLEncode(itemset1.getValue("LINE_NO")) +
	            "&RELEASENO=" + mgr.URLEncode(itemset1.getValue("RELEASE_NO")) +
	            "&IDENTIFICATION=" + mgr.URLEncode("NOPARTS") + 
                    "&FRM_NAME=" + mgr.URLEncode("NotesDlg");
        newWinHandle = "notes";
    }


    /**    Document Text action itemset0 invokes this */ 
    public void documentText0()
    {
        ASPManager mgr = getASPManager();

        String callingUrl = mgr.getURL();
        ctx.setGlobal("CALLING_URL", callingUrl);

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());
        else
            itemset0.selectRow();

        // 040219  ARWILK  Begin  (Enable Multirow RMB actions)
        bOpenNewWindow = true;
        urlString = "../mpccow/DocumentTextDlg.page?NOTE_ID=" + mgr.URLEncode(itemset0.getValue("NOTEID")) + 
                    "&WO_NO=" + mgr.URLEncode(itemset0.getValue("WO_NO")) + 
                    "&FRM_NAME=" + mgr.URLEncode("WorkOrderRequisHeaderRMB");
        newWinHandle = "documentText0"; 
        // 040219  ARWILK  End  (Enable Multirow RMB actions)
    }

    /**    Document Text action itemset1 invokes this */ 
    public void documentText1()
    {
        ASPManager mgr = getASPManager();

        String callingUrl = mgr.getURL();
        ctx.setGlobal("CALLING_URL", callingUrl);

        if (itemlay1.isMultirowLayout())
            itemset1.goTo(itemset1.getRowSelected());
        else
            itemset1.selectRow();

        // 040219  ARWILK  Begin  (Enable Multirow RMB actions)
        bOpenNewWindow = true;
        urlString = "../mpccow/DocumentTextDlg.page?NOTE_ID=" + mgr.URLEncode(itemset1.getValue("ITEM1_NOTEID")) + 
                    "&WO_NO=" + mgr.URLEncode(itemset1.getValue("WO_NO")) + 
                    "&FRM_NAME=" + mgr.URLEncode("WorkOrderRequisHeaderRMB");
        newWinHandle = "documentText1"; 
        // 040219  ARWILK  End  (Enable Multirow RMB actions)
    }

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

//-----------------------------------------------------------------------------
//-------------------------  BACK FUNCTION  -----------------------------------
//-----------------------------------------------------------------------------


    public void preDefine()
    {
        ASPManager mgr = getASPManager();     

        //-------------------------------------------------------------------------------------------------------------
        //-----------------------------------------------  UPPER Block ------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------

        upperblk = mgr.newASPBlock("UPPER");

        f = upperblk.addField("UP_WO_NO");
        f.setReadOnly();
        f.setFunction("''");
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBUWN: WO No");

        f = upperblk.addField("UP_CONTRACT");
        f.setHidden();
        f.setFunction("''");

        f = upperblk.addField("UP_MCH_CODE");
        f.setFunction("''");
        f.setSize(29);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBUMCHCODE: Object ID");

        f = upperblk.addField("MCH_NAME");
        f.setSize(50);
        f.setFunction("''");
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBMNAME: Description");

        upperblk.setView("DUAL");
        upperset = upperblk.getASPRowSet();
        uppertbl = mgr.newASPTable(upperblk);
        upperset = upperblk.getASPRowSet();
        upperbar = mgr.newASPCommandBar(upperblk);

        upperbar.disableCommand(upperbar.FIND);
        upperbar.disableCommand(upperbar.NEWROW);
        upperbar.disableCommand(upperbar.BACK);
        upperbar.disableCommand(upperbar.FORWARD);      
        upperbar.disableCommand(upperbar.BACKWARD);   
        upperbar.disableCommand(upperbar.DUPLICATEROW);

        upperlay = upperblk.getASPBlockLayout();
        upperlay.setDefaultLayoutMode(upperlay.SINGLE_LAYOUT);


        //-------------------------------------------------------------------------------------------------------------
        //-----------------------------------------------  HEAD Block -------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------


        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("MODULENAME");
        f.setHidden();
        f.setFunction("''");

        boolean purchInst = false; 
        if (mgr.isModuleInstalled("PURCH"))
            purchInst = true;
        else
            purchInst = false;  

        f = headblk.addField("ATTR");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("INFO");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("ACTION");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("WO_NO","Number", "#");
        f.setHidden();

        f = headblk.addField("MCH_CODE");
        f.setHidden();
        f.setFunction("Active_Separate_API.Get_Mch_Code(:WO_NO)");

        f = headblk.addField("REQUISITION_NO");
        f.setSize(13);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBREQUISITION_NO: Requisition No");

        f = headblk.addField("REQUISITIONER_CODE");
        f.setSize(11);
        f.setMandatory();
        f.setReadOnly();
        f.setInsertable();
        f.setDynamicLOV("PURCHASE_REQUISITIONER_LOV",600,445);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBREQUISITIONERCODE: Requisitioner");
        f.setUpperCase();

        f = headblk.addField("CONTRACT");
        f.setSize(8);
        f.setReadOnly();
        f.setUpperCase();
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBCONTRACT: Site");

        f = headblk.addField("REQUISITION_DATE","Date");
        f.setSize(11);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBREQUISITIONDATE: Requisition Date");

        f = headblk.addField("ORDER_CODE");
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBORDERCODE: Order Code");
        f.setDynamicLOV("PURCHASE_ORDER_TYPE_LOV");
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERREQUISHEADERRMBORDCD: Order Codes"));
        f.setCustomValidation("ORDER_CODE","ORDERCODEDESC,EXTERNAL_SERVICE");
        f.setSize(12);
        f.setMaxLength(3);
        f.setMandatory();

        f = headblk.addField("ORDERCODEDESC");
        f.setReadOnly();
        f.setSize(30);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBORDERCODEDESC: Order Code Description");
        f.setFunction("Purchase_Order_Type_API.Get_Description(Purchase_Req_Util_API.Get_Order_Code(REQUISITION_NO))");
        f.setDefaultNotVisible();   

        f = headblk.addField("DESTINATION_ID");
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBINTDESTINATION: Int Destination");
        f.setDynamicLOV("INTERNAL_DESTINATION_LOV","CONTRACT");
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERREQUISHEADERRMBINTDESTINATIONS: Int Destinations"));
        f.setCustomValidation("DESTINATION_ID,CONTRACT","INTERNAL_DESTINATION");
        f.setSize(12);
        f.setMaxLength(30);
        f.setUpperCase();           

        f = headblk.addField("INTERNAL_DESTINATION");
        f.setSize(35);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBINTDEST: Int Destination Description");
        f.setDefaultNotVisible(); 

        f = headblk.addField("STATUSCODE");
        f.setSize(8);
        f.setReadOnly();
        f.setHidden();
        f.setFunction("Purchase_Req_Util_API.Get_Requisition_Status(:REQUISITION_NO)");

        f = headblk.addField("STATUSCODE1");
        f.setSize(8);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBSTATUSCODE: Status");
        f.setFunction("Purchase_Req_Util_API.Get_Requisition_State(:REQUISITION_NO)");

        f = headblk.addField("AUTHORIZATION_REQUIRED");
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBAUTHOREQUI: Authorization Required");
        f.setCheckBox("NOT_REQUIRED,REQUIRED");
        f.setFunction("Purchase_Requisition_Api.Authorization_Required(:REQUISITION_NO)");
        f.setReadOnly();

        f = headblk.addField("PRE_ACC_EXIST");
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBPREACCEXIST: Pre Accounting Exists");
        f.setCheckBox("0,1");
        f.setFunction("Pre_Accounting_Api.Pre_Accounting_Exist(PURCHASE_REQUISITION_API.Get_Pre_Accounting_Id(:REQUISITION_NO))");
        f.setReadOnly();

        f = headblk.addField("HEAD_MCH_NAME");
        f.setHidden();
        f.setFunction("Maintenance_Object_API.Get_Mch_Name(Maintenance_Object_API.Get_Sup_Contract(Purchase_Req_Util_API.Get_Contract(:REQUISITION_NO),Active_Separate_API.Get_Mch_Code(:WO_NO)),Active_Separate_API.Get_Mch_Code(:WO_NO))");

        f = headblk.addField("PLANN");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("RELEASE");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("POCREATE");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("PLAN_DUM");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("REL_DUM");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("CO_ORD");
        f.setHidden();
        f.setFunction("Active_Work_Order_API.Get_Order_Coordinator(:WO_NO)");

        f = headblk.addField("HEAD_CURRENCEY_CODE");
        f.setHidden();
        f.setFunction("ACTIVE_SEPARATE_API.Get_Currency_Code(:WO_NO)");

        f= headblk.addField ("EXTERNAL_SERVICE");
        f.setCheckBox("FALSE,TRUE");
        f.setCustomValidation("EXTERNAL_SERVICE","ORDER_CODE,ORDERCODEDESC");
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBRMEXTERNALSERVICE: External Service");

	f=headblk.addField("MULTI_PURCHASE_REQ");
	f.setReadOnly();
	f.setLabel("MULTIPURCHREQEXIST: Multiple Purchase Requisitions Exist");
	f.setFunction("Work_Order_Requis_Header_Api.Multiple_Purch_Req_Exist(:WO_NO)");
	f.setCheckBox("FALSE,TRUE");
	f.setDefaultNotVisible();

        headblk.setView("WORK_ORDER_REQUIS_PURCH");
        headblk.defineCommand("WORK_ORDER_REQUIS_HEADER_API","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();

        headtbl = mgr.newASPTable(headblk);
        headtbl.setWrap();
        headtbl.enableRowSelect();

        headbar = mgr.newASPCommandBar(headblk);

        headbar.defineCommand(headbar.SAVERETURN, "saveReturn", "checkOrderCodeValues");
        headbar.defineCommand(headbar.SAVENEW, "saveNew", "checkOrderCodeValues");
        headbar.defineCommand(headbar.DELETE, "deleteRow");

        headbar.defineCommand(headbar.DUPLICATE, "duplicate");
        headbar.defineCommand(headbar.FAVORITE, "favorite");
        headbar.enableCommand(headbar.SUBMIT);

        headbar.enableMultirowAction();

        headbar.addCustomCommand("planRequisition", mgr.translate("PCMWWORKORDERREQUISHEADERRMBCHGTOPLAN: Change to Planned"));
        headbar.addCustomCommand("releaseRequisition", mgr.translate("PCMWWORKORDERREQUISHEADERRMBRELEASE: Release"));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("prePost", mgr.translate("PCMWWORKORDERREQUISHEADERRMBPREPOSTH: Pre Posting..."));

        // 040219  ARWILK  Begin  (Enable Multirow RMB actions)
        headbar.addCommandValidConditions("planRequisition",    "STATUSCODE", "Enable", "Released");
        headbar.addCommandValidConditions("releaseRequisition", "STATUSCODE", "Enable", "Planned");

        headbar.removeFromMultirowAction("prePost");
        // 040219  ARWILK  End  (Enable Multirow RMB actions)

        //Web Alignment - Replacing Blocks with Tabs
        headbar.addCustomCommand("activatePartReqLinesTab","Part Requisition Lines");
        headbar.addCustomCommand("activateNoPartReqLinesTab","No Part Requisition Lines");
        headbar.addCustomCommand("createNewSupplier", "");
        headbar.addCustomCommand("refreshForm","");
        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);  
        headlay.setFieldOrder("REQUISITION_NO,CONTRACT,REQUISITION_DATE,STATUSCODE,REQUISITIONER_CODE,AUTHORIZATION_REQUIRED,ORDER_CODE,ORDERCODEDESC,PRE_ACC_EXIST,DESTINATION_ID,INTERNAL_DESTINATION,EXTERNAL_SERVICE");
        headlay.setSimple("ORDERCODEDESC");
        headlay.setSimple("INTERNAL_DESTINATION");

        tabs = mgr.newASPTabContainer();
        tabs.setTabSpace(5);  
        tabs.setTabWidth(100);

        //Bug 72772, start
        tabs.addTab(mgr.translate("PCMWWORKORDERREQUISHEADERRMBITM0: Part Requisition Lines"),"javascript:commandSet('HEAD.activatePartReqLinesTab','')");
        tabs.addTab(mgr.translate("PCMWWORKORDERREQUISHEADERRMBITM1: No Part Requisition Lines"),"javascript:commandSet('HEAD.activateNoPartReqLinesTab','')");
        //Bug 72772, end
        //

        //---------------------------------------------------------------------------------------------------------
        //----------------------------------         ITEM0 Block          -----------------------------------------
        //---------------------------------------------------------------------------------------------------------


        itemblk0 = mgr.newASPBlock("ITEM0");

        f = itemblk0.addField("ITEM0_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk0.addField("ITEM0_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk0.addField("ITEM0_WO_NO","Number");
        f.setSize(11);
        f.setMandatory();
        f.setHidden();
        f.setDbName("WO_NO");
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBWO_NO: WO no");

        f = itemblk0.addField("ITEM0_REQUISITION_NO");
        f.setSize(11);
        f.setHidden();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM0_REQUISITION_NO: Requisition no");
        f.setDbName("REQUISITION_NO");

        f = itemblk0.addField("LINE_NO");
        f.setSize(11);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBLINE_NO: Line No");

        f = itemblk0.addField("RELEASE_NO");
        f.setSize(11);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBRELEASE_NO: Release No");

        f = itemblk0.addField("ITEM0_CONTRACT");
        f.setSize(11);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setMandatory();
        f.setUpperCase();
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM0_CONTRACT: Site");
        f.setDbName("CONTRACT");
        f.setHidden();

        f = itemblk0.addField("PART_NO");
        f.setSize(13);
        f.setDynamicLOV("PURCHASE_PART_INVENT_ATTR","CONTRACT",600,445);
        f.setMandatory();
        f.setReadOnly();
        f.setInsertable(); 
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBPARTNO: Part No");
        f.setHyperlink("../purchw/PurchasePart.page","PART_NO","NEWWIN");
        f.setCustomValidation("ITEM0_CONTRACT,PART_NO,VENDOR_NO,SERIAL_NO,CONDITION_CODE,BUYER_CODE,ITEM0_ORDER_CODE","DESCRIPTION,VENDOR_NO,UNIT_MEAS,CATALOG_NO,CONTACT,PROCESS_TYPE,ORDERPROCDESC,VENDORNAME,TECHNICAL_COORDINATOR_ID,BUYER_CODE,PART_OWNERSHIP,OWNING_CUSTOMER_NO,CONDITION_CODE,CONDITIONCODEDESC,ACTIVEIND_DB");
        f.setUpperCase();

        f = itemblk0.addField("DESCRIPTION");
        f.setSize(21);
        f.setDefaultNotVisible();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM_NOTE_TEXT: Description");

        f = itemblk0.addField("VENDOR_NO");
        f.setUpperCase();
        f.setSize(11);
        f.setDynamicLOV("PURCHASE_PART_SUPPLIER_LOV","PART_NO, CONTRACT",600,445);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBVENDORNO: Supplier");
        f.setCustomValidation("VENDOR_NO,ITEM0_CONTRACT,PART_NO,BUYER_CODE","VENDORNAME,UNIT_MEAS,CONTACT,BUYER_CODE");

        f = itemblk0.addField("VENDORNAME");
        f.setSize(25);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBVNAME: Supplier Name");
        f.setDefaultNotVisible();
        f.setFunction("Purchase_Req_Util_API.Get_Line_Vendor_Name(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)"); 

        f = itemblk0.addField("CONTACT");
        f.setSize(10);
        f.setDynamicLOV("SUPP_DEF_CONTACT_LOV","VENDOR_NO SUPPLIER_ID");
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERREQUISHEADERRMBVENDORCONT: Contacts"));
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBVCONTRACT: Supplier Contact");
        f.setDefaultNotVisible();

        f = itemblk0.addField("CONDITION_CODE");
        f.setSize(20);
        f.setDynamicLOV("CONDITION_CODE",600,445);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBCONDITIONCODE: Condition Code");
        f.setCustomValidation("ITEM0_CONTRACT,PART_NO,SERIAL_NO,CONDITION_CODE","CONDITIONCODEDESC");
        f.setUpperCase();

        f = itemblk0.addField("CONDITIONCODEDESC");
        f.setSize(20);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBCONDITIONCODEDESC: Condition Code Description");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(50);
        f.setFunction("Condition_Code_API.Get_Description(Purchase_Req_Util_API.Get_Line_Condition_Code(:REQUISITION_NO, :LINE_NO, :RELEASE_NO))");

        f = itemblk0.addField("PART_OWNERSHIP");
        f.setSize(25);
        f.setInsertable();
        f.setSelectBox();
        f.enumerateValues("PART_OWNERSHIP_API");
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBPARTOWNERSHIP: Ownership");
        f.setCustomValidation("PART_OWNERSHIP","PART_OWNERSHIP_DB");

        f = itemblk0.addField("PART_OWNERSHIP_DB");
        f.setSize(20);
        f.setHidden();
        f.setFunction("Part_Ownership_API.Encode(PURCHASE_REQ_LINE_PART_API.Get_Part_Ownership(:REQUISITION_NO, :LINE_NO, :RELEASE_NO))");

        f = itemblk0.addField("OWNING_CUSTOMER_NO");
        f.setSize(15);
        f.setMaxLength(20);
        f.setInsertable();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBPARTOWNER: Owner");
        f.setCustomValidation("OWNING_CUSTOMER_NO,PART_OWNERSHIP_DB","OWNER_NAME");
        f.setDynamicLOV("CUSTOMER_INFO");
        f.setUpperCase();

        f = itemblk0.addField("OWNER_NAME");
        f.setSize(20);
        f.setMaxLength(100);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBPARTOWNERNAME: Owner Name");
        f.setFunction("CUSTOMER_INFO_API.Get_Name(PURCHASE_REQ_LINE_PART_API.Get_Owning_Customer_No(:REQUISITION_NO, :LINE_NO, :RELEASE_NO))");

        f = itemblk0.addField("UNIT_MEAS");
        f.setSize(21);
        f.setDynamicLOV("ISO_UNIT",600,445);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBUNITMEAS: Unit of Measurement");

        f = itemblk0.addField("ORIGINAL_QTY","Number");
        f.setSize(19);
        f.setMandatory();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBORIGINALQTY: Original Quantity");
        f.setCustomValidation("CATALOG_CONTRACT,CATALOG_NO,ORIGINAL_QTY,ITEM0_REQUISITION_NO,AGREEMENT_ID,CUSTOMER_NO,PRICE_LIST_NO","PRICE_LIST_NO,ITEM0_DISCOUNT,ITEM0_LISTPRICE,ITEM0_SALESPRICEAMOUNT");

        f = itemblk0.addField("CRAFT_LINE_NO","Number");
        f.setDynamicLOV("WORK_ORDER_ROLE","WO_NO",600,445);
        f.setInsertable();
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBCRFTLINNO: Operation No");

        //Bug ID 57760,start
        f = itemblk0.addField("ITEM0_FBUY_UNIT_PRICE","Money");
	f.setSize(19);
        f.setFunction("PURCHASE_REQ_UTIL_API.Get_Line_Fbuy_Unit_Price(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");
	f.setLabel("PCMWWORKORDERREQUISFBUYUNITPRICE: Price/Curr");
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = itemblk0.addField("FBUY_UNIT_PRICE_TAX","Money");
	f.setSize(19);
        f.setFunction("DECODE(Purchase_Part_Supplier_Api.Get_Tax_Regime_Db(Purchase_Req_Util_API.Get_Line_Contract(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)), 'VAT',DECODE(Purchase_Part_API.Get_Taxable(Purchase_Req_Util_API.Get_Line_Contract(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO),Purchase_Req_Util_API.Get_Line_Part_No(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)), 'TRUE',PURCHASE_REQ_UTIL_API.Get_Line_Fbuy_Unit_Price(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO) * ( 1 + Purchase_Part_Supplier_API.Get_Assumed_Vat_Percentage(Purchase_Req_Util_API.Get_Line_Contract(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO),Purchase_Req_Util_API.Get_Line_Part_No(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO),Purchase_Req_Util_API.Get_Line_Vendor_No(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO))/100), PURCHASE_REQ_UTIL_API.Get_Line_Fbuy_Unit_Price(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)), 'MIX',DECODE(Purchase_Part_API.Get_Taxable(Purchase_Req_Util_API.Get_Line_Contract(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO),Purchase_Req_Util_API.Get_Line_Part_No(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)), 'TRUE',PURCHASE_REQ_UTIL_API.Get_Line_Fbuy_Unit_Price(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO) * ( 1 + Purchase_Part_Supplier_API.Get_Assumed_Vat_Percentage(Purchase_Req_Util_API.Get_Line_Contract(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO),Purchase_Req_Util_API.Get_Line_Part_No(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO),Purchase_Req_Util_API.Get_Line_Vendor_No(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO))/100), PURCHASE_REQ_UTIL_API.Get_Line_Fbuy_Unit_Price(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)),NULL)");
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBPRICEBASCURINCTAX: Price/Curr incl. Tax");
	f.setDefaultNotVisible();
        f.setReadOnly(); 
         
      	f = itemblk0.addField("SUPP_CURRENCY_CODE");
      	f.setSize(19);
      	f.setLabel("PCMWWORKORDERREQUISSUPPCURRENCYCODE: Currency Code");
      	f.setFunction("Purchase_Req_Util_API.Get_Line_Currency_Code(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");
      	f.setDefaultNotVisible();
      	f.setReadOnly();

	f = itemblk0.addField("ITEM0_CRESUPP_PRICE_UNIT_MEAS");
      	f.setSize(19);
      	f.setLabel("PCMWWORKORDERREQUISPRICEUM: Price U/M");
      	f.setFunction("PURCHASE_REQ_LINE_PART_API.Get_Price_Unit_Meas(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");
      	f.setDefaultNotVisible();
      	f.setReadOnly();

      	f = itemblk0.addField("ITEM0_BUY_UNIT_PRICE","Money");
        f.setSize(19);
        f.setLabel("PCMWWORKORDERREQUISPRICEBASE: Price/Base");
        f.setFunction("PURCHASE_REQ_LINE_PART_API.Get_Buy_Unit_Price(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");
        f.setDefaultNotVisible();
        f.setReadOnly();  

        f = itemblk0.addField("ITEM0_BUY_UNIT_PRICE_INCL_TAX","Money");
	f.setSize(19);
	f.setLabel("PCMWITEMPRICEBASEUNITINCLTAX: Price/Base incl. Tax");
	f.setDefaultNotVisible();
        f.setReadOnly();
        f.setFunction("DECODE(Purchase_Part_Supplier_Api.Get_Tax_Regime_Db(Purchase_Req_Util_API.Get_Line_Contract(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)), 'VAT',DECODE(Purchase_Part_API.Get_Taxable(Purchase_Req_Util_API.Get_Line_Contract(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO),Purchase_Req_Util_API.Get_Line_Part_No(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)), 'TRUE', PURCHASE_REQ_LINE_PART_API.Get_Buy_Unit_Price(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO) * ( 1 + Purchase_Part_Supplier_API.Get_Assumed_Vat_Percentage(Purchase_Req_Util_API.Get_Line_Contract(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO),Purchase_Req_Util_API.Get_Line_Part_No(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO),Purchase_Req_Util_API.Get_Line_Vendor_No(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO))/100), PURCHASE_REQ_LINE_PART_API.Get_Buy_Unit_Price(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)), 'MIX',DECODE(Purchase_Part_API.Get_Taxable(Purchase_Req_Util_API.Get_Line_Contract(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO),Purchase_Req_Util_API.Get_Line_Part_No(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)), 'TRUE', PURCHASE_REQ_LINE_PART_API.Get_Buy_Unit_Price(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO) * ( 1 + Purchase_Part_Supplier_API.Get_Assumed_Vat_Percentage(Purchase_Req_Util_API.Get_Line_Contract(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO),Purchase_Req_Util_API.Get_Line_Part_No(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO),Purchase_Req_Util_API.Get_Line_Vendor_No(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO))/100), PURCHASE_REQ_LINE_PART_API.Get_Buy_Unit_Price(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)),NULL)");
        
        f = itemblk0.addField("ITEM0_PRICE_CONV_FACTOR","Number");
        f.setSize(19);
        f.setLabel("PCMWWORKORDERREQUISPRICECONVFACTOR: Price Conv Factor");
        f.setFunction("PURCHASE_REQ_LINE_PART_API.Get_Price_Conv_Factor(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");
        f.setDefaultNotVisible();
        f.setReadOnly(); 

	f = itemblk0.addField("ITEM0_DISCOUNT", "Number");
        f.setSize(19);
        f.setLabel("PCMWWORKORDERREQUISITEM0LINEDISCOUNT: Discount");
        f.setFunction("PURCHASE_REQ_UTIL_API.Get_Line_Discount(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");
        f.setDefaultNotVisible();
        f.setReadOnly();  
        
        f = itemblk0.addField("ITEM0_ADD_COST_AMOUNT","Money");
        f.setSize(19);
        f.setLabel("PCMWWORKORDERREQUISADDITIONALCOST: Additional Cost");
        f.setFunction("PURCHASE_REQ_UTIL_API.Get_Line_Additional_Cost(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");
        f.setDefaultNotVisible();
        f.setReadOnly(); 

        f = itemblk0.addField("ITEM0_ADD_COST_AMOUNT_INCL_TAX","Money");
        f.setSize(19);
        f.setLabel("PCMWWORKORDERREQUISADDCOSTINCLTAX: Additional Cost incl. Tax");
        f.setFunction("DECODE(Purchase_Part_Supplier_Api.Get_Tax_Regime_Db(Purchase_Req_Util_API.Get_Line_Contract(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)), 'VAT',DECODE(Purchase_Part_API.Get_Taxable(Purchase_Req_Util_API.Get_Line_Contract(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO),Purchase_Req_Util_API.Get_Line_Part_No(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)), 'TRUE', (PURCHASE_REQ_UTIL_API.Get_Line_Additional_Cost(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)) * ( 1 + Purchase_Part_Supplier_API.Get_Assumed_Vat_Percentage(Purchase_Req_Util_API.Get_Line_Contract(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO),Purchase_Req_Util_API.Get_Line_Part_No(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO),(Purchase_Req_Util_API.Get_Line_Vendor_No(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)))/100), PURCHASE_REQ_UTIL_API.Get_Line_Additional_Cost(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)), 'MIX',DECODE(Purchase_Part_API.Get_Taxable(Purchase_Req_Util_API.Get_Line_Contract(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO),Purchase_Req_Util_API.Get_Line_Part_No(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)), 'TRUE', (PURCHASE_REQ_UTIL_API.Get_Line_Additional_Cost(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)) * ( 1 + Purchase_Part_Supplier_API.Get_Assumed_Vat_Percentage(Purchase_Req_Util_API.Get_Line_Contract(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO),Purchase_Req_Util_API.Get_Line_Part_No(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO),(Purchase_Req_Util_API.Get_Line_Vendor_No(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)))/100), (PURCHASE_REQ_UTIL_API.Get_Line_Additional_Cost(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO))),NULL)");
        f.setDefaultNotVisible();
        f.setReadOnly(); 

        f = itemblk0.addField("ITEM0_TOTAL_BASE","Money");
        f.setSize(19);
        f.setLabel("PCMWWORKORDERREQUISTOTALBASE: Total/Base");
        f.setFunction("PURCHASE_REQ_LINE_PART_API.Get_Line_Total(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = itemblk0.addField("ITEM0_TOTAL_CURR","Money");
        f.setSize(19);
        f.setLabel("PCMWWORKORDERREQUISTOTALCURR: Total/Curr");
        f.setFunction("PURCHASE_REQ_LINE_PART_API.Get_Line_Tot_In_Price_Curr(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = itemblk0.addField("ITEM0_GROSS_TOTAL_BASE","Money");
        f.setSize(19);
        f.setLabel("PCMWWORKORDERREQUISGROSSTOTALBASE: Gross Total/Base");
        f.setFunction("PURCHASE_REQ_UTIL_API.Get_Inc_Tax_Total_Base(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = itemblk0.addField("ITEM0_GROSS_TOTAL_CURR","Money");
        f.setSize(19);
        f.setLabel("PCMWWORKORDERREQUISGROSSTOTALCURR: Gross Total/Curr");
        f.setFunction("PURCHASE_REQ_UTIL_API.Get_Inc_Tax_Total_Curr(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");
        f.setDefaultNotVisible();
        f.setReadOnly();
        //bug id 57760,end

        f = itemblk0.addField("SERIAL_NO");
        f.setSize(19);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBSERIALNO: Serial No");
        f.setDynamicLOV("PART_SERIAL_CATALOG","PART_NO,PART_OWNERSHIP");
        f.setLOVProperty("WHERE","Part_Serial_Catalog_API.Is_In_Inventory(PART_NO,SERIAL_NO) = 'TRUE'");
        f.setMaxLength(50);
        f.setDefaultNotVisible();
        f.setInsertable();
        f.setUpperCase();

        f = itemblk0.addField("LOT_BATCH_NO");
        f.setSize(19);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBLOTBATCHNO: Lot Batch No");
        f.setDynamicLOV("LOT_BATCH_MASTER_LOV","PART_NO");
        f.setMaxLength(20);
        f.setDefaultNotVisible();
        f.setInsertable();
        f.setUpperCase();

        f = itemblk0.addField("SERVICE_TYPE");
        f.setSize(19);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBSERVICETYPE: External Service Type");
        f.setDynamicLOV("EXTERNAL_SERVICE_TYPE");
        f.setMaxLength(20);
        f.setDefaultNotVisible();
        f.setInsertable();
        f.setCustomValidation("SERVICE_TYPE","SERVICE_TYPE_DESC");
        f.setUpperCase();

        f = itemblk0.addField("SERVICE_TYPE_DESC");
        f.setSize(30);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBSERVICETYPEDESC: Service Description");
        f.setFunction("External_Service_Type_API.Get_Description(Purchase_Req_Util_API.Get_Line_Service_Type(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO))");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk0.addField("JOB_ID", "Number");
        f.setDefaultNotVisible();
        f.setDynamicLOV("WORK_ORDER_JOB", "WO_NO");
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBJOBID: Job Id");
        f.setInsertable();
        f.setSize(20);

        f = itemblk0.addField("WANTED_RECEIPT_DATE","Date");
        f.setSize(21);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBWANTEDRECEIPTDATE: Wanted Receipt Date");

        f = itemblk0.addField("LATEST_ORDER_DATE","Date");
        f.setSize(17);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBLATESTORDERDATE: Last Order Date");
        f.setReadOnly();
        f.setInsertable();

        f = itemblk0.addField("LINE_STATUS");
        f.setSize(11);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM0_STATUSCODE: Status");

        f = itemblk0.addField("PLANNERBUYER");
        f.setReadOnly();
        f.setSize(11);
        f.setDynamicLOV("INVENTORY_PART_PLANNER",600,445);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBPLANNERBUYER: Planner");
        f.setFunction("Purchase_Req_Util_API.Get_Line_Planner_Buyer(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");
        f.setDefaultNotVisible();

        f = itemblk0.addField("COST_RETURN");
        f.setSize(17);
        f.setSelectBox();
        f.setMandatory();
        f.enumerateValues("COST_RETURN_API");
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBCOST_RETURN: Cost Return at");
        f.setDefaultNotVisible();

        f = itemblk0.addField("BUYER_CODE");
        f.setSize(11);
        f.setInsertable();
        f.setDynamicLOV("PURCHASE_BUYER_LOV",600,445);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBBUYERCODE: Buyer");
        f.setUpperCase();
        f.setDefaultNotVisible();

        f = itemblk0.addField("TECHNICAL_COORDINATOR_ID");
        f.setSize(11);
        f.setDynamicLOV("TECHNICAL_COORDINATOR_LOV");
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERREQUISHEADERRMBTECHCOOD: Tech Coordinators"));
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBTECHCOORDID: Tech Coordinator");
        f.setDefaultNotVisible();
        f.setUpperCase();

        f = itemblk0.addField("PROCESS_TYPE");
        f.setSize(20);
        f.setDynamicLOV("ORDER_PROC_TYPE");
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERREQUISHEADERRMBORDPROCT: Order Proc Types"));
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBORDPROCTYPE: Order Proc Type");
        f.setMaxLength(3);
        f.setCustomValidation("PROCESS_TYPE","ORDERPROCDESC");
        f.setDefaultNotVisible();
        f.setUpperCase();

        f = itemblk0.addField("ORDERPROCDESC");
        f.setSize(25);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBORDPROCDESC: Order Proc Desc");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setFunction("ORDER_PROC_TYPE_API.Get_Description(Purchase_Req_Util_API.Get_Line_Process_Type(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO))");

        f = itemblk0.addField("REQUEST_TYPE");
        f.setSize(25);
        f.setSelectBox();
        f.enumerateValues("REQUEST_TYPE_API");
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBREQTYPE: Request Type");
        f.setDefaultNotVisible();

        f = itemblk0.addField("ORDER_NO");
        f.setSize(11);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBORDERNO: Order No");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk0.addField("BUY_UNIT_MEAS");
        f.setSize(16);
        f.setDynamicLOV("ISO_UNIT",600,445);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBBUYUNITMEAS: Buy Unit");
        f.setDefaultNotVisible();

        f = itemblk0.addField("ASSG_LINE_NO");
        f.setSize(14);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBASSGLINENO: Order Line No");
        f.setDefaultNotVisible();

        f = itemblk0.addField("ASSG_RELEASE_NO");
        f.setSize(18);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBASSGRELEASENO: Order Release No");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk0.addField("LINEORDERSTATUS");
        f.setSize(20);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBLINEORDERSTATUS: Order Line Status");
        f.setReadOnly();
        f.setFunction("Purchase_Req_Util_API.Get_Line_Order_Status(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");
        f.setDefaultNotVisible();

        f = itemblk0.addField("CATALOG_CONTRACT");
        f.setSize(19);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBCATALOG_CONTRACT: Sales Part Site");
        f.setUpperCase();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk0.addField("CATALOG_NO");
        f.setSize(19);
        f.setDynamicLOV("SALES_PART_ACTIVE_LOV","CATALOG_CONTRACT CONTRACT",600,445);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBCATALOG_NO: Sales Part Number");
        f.setCustomValidation("CATALOG_CONTRACT,CATALOG_NO,ORIGINAL_QTY,ITEM0_REQUISITION_NO,LINE_NO,RELEASE_NO,AGREEMENT_ID,CUSTOMER_NO,PRICE_LIST_NO,HEAD_CURRENCEY_CODE","CATALOGDESC,ITEM0_LISTPRICE,ITEM0_SALESPRICEAMOUNT,PRICE_LIST_NO,ITEM0_DISCOUNT");
        f.setUpperCase();
        f.setDefaultNotVisible();

        f = itemblk0.addField("CATALOGDESC");
        f.setSize(25);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBCATALOGDESC: Sales Part Description");
        f.setFunction("''");
        f.setDefaultNotVisible();

        f = itemblk0.addField("PRICE_LIST_NO");
        f.setSize(15);
        f.setDynamicLOV("SALES_PRICE_LIST",600,445);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBPRICE_LIST_NO: Price List No");
        f.setUpperCase();
        f.setCustomValidation("PRICE_LIST_NO,CATALOG_CONTRACT,CATALOG_NO,ORIGINAL_QTY,ITEM0_REQUISITION_NO,AGREEMENT_ID,CUSTOMER_NO","PRICE_LIST_NO,ITEM0_DISCOUNT,ITEM0_LISTPRICE,ITEM0_SALESPRICEAMOUNT");
        f.setDefaultNotVisible();

        f = itemblk0.addField("ITEM0_LISTPRICE", "Money");
        f.setSize(12);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM0_LISTPRICE: Sales Price");
        f.setFunction("''");
        f.setDefaultNotVisible();

        f = itemblk0.addField("ITEM0_SALESPRICEAMOUNT", "Money");
        f.setSize(19);
        f.setReadOnly(); 
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM0_SALESPRICEAMOUNT: Sales Price Amount");
        f.setFunction("''");
        f.setDefaultNotVisible();

        f = itemblk0.addField("NOTE_TEXT");
        f.setSize(30);                          
        f.setHeight(3);                         
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM0_NOTE_TEXT: Note");
        f.setDefaultNotVisible();

        f = itemblk0.addField("LINE_STATE");
        f.setSize(11);
        f.setHidden();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBDBSTATUSCODE: Status");

        f = itemblk0.addField("WANTEDDELIVERYDATE","Date");
        f.setSize(8);
        f.setHidden();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBWANTEDDELIVERYDATE: wanted delivery");
        f.setFunction("Purchase_Req_Util_API.Get_Line_Wanted_Delivery_Date(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");

        f = itemblk0.addField("NOTETEXT");
        f.setSize(11);
        f.setHeight(3);
        f.setHidden();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBNOTETEXT: Note");
        f.setFunction("Purchase_Req_Util_API.Get_Line_Note_Text(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");

        f = itemblk0.addField("PARTNO");         
        f.setSize(11);
        f.setHidden();
        f.setFunction("''");
        f.setUpperCase();

        f = itemblk0.addField("NOTEID","Number");
        f.setSize(11);
        f.setHidden();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBNOTEID: Note Id");
        f.setFunction("Purchase_Req_Util_API.Get_Line_Note_Id(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");

        f = itemblk0.addField("DOCTEXTEXIST");
        f.setCheckBox("0,1"); 
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBDOCUMENTTEXT: Document Text");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setFunction("Decode(Document_Text_API.Note_Id_Exist(Purchase_Req_Util_API.Get_Line_Note_Id(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)),0,0,1)");
        
        f = itemblk0.addField("TEMPLATE_ID");
        f.setSize(21);
        f.setHidden();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBTEMPLID: Template ID");
        f.setFunction("Purchase_Req_Util_API.Get_Line_Template_Id(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");

        f = itemblk0.addField("REQ_ORDER_NO");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("REQ_INFO");
        f.setHidden(); 
        f.setFunction("''");

        f = itemblk0.addField("ITEM0_PREPOST_EXIST");
        f.setReadOnly(); 
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM0PREPOSTEXIST: Pre Accounting Exists");
        f.setFunction("Pre_Accounting_Api.Pre_Accounting_Exist(Purchase_Req_Util_API.Get_Line_Pre_Accounting_Id(REQUISITION_NO,LINE_NO,RELEASE_NO))");
        f.setCheckBox("0,1");

        f = itemblk0.addField("AGREEMENT_ID");
        f.setSize(50);
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("CUSTOMER_NO");
        f.setSize(50);
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("BASE_UNIT_PRICE");
        f.setHidden(); 
        f.setFunction("''");

        f = itemblk0.addField("SALE_UNIT_PRICE");
        f.setHidden(); 
        f.setFunction("''");

        f = itemblk0.addField("CURRENCY_RATE");
        f.setHidden(); 
        f.setFunction("''");

        f = itemblk0.addField("PRE_POSTING_ID");
        f.setHidden(); 
        f.setFunction("''");

        f = itemblk0.addField("CODE_A");
        f.setHidden(); 
        f.setFunction("''");

        f = itemblk0.addField("CODE_B");
        f.setHidden(); 
        f.setFunction("''");

        f = itemblk0.addField("CODE_C");
        f.setHidden(); 
        f.setFunction("''");

        f = itemblk0.addField("CODE_D");
        f.setHidden(); 
        f.setFunction("''");

        f = itemblk0.addField("CODE_E");
        f.setHidden(); 
        f.setFunction("''");

        f = itemblk0.addField("CODE_F");
        f.setHidden(); 
        f.setFunction("''");

        f = itemblk0.addField("CODE_G");
        f.setHidden(); 
        f.setFunction("''");

        f = itemblk0.addField("CODE_H");
        f.setHidden(); 
        f.setFunction("''");

        f = itemblk0.addField("CODE_I");
        f.setHidden(); 
        f.setFunction("''");

        f = itemblk0.addField("CODE_J");
        f.setHidden(); 
        f.setFunction("''");

        f = itemblk0.addField("CONTROL_TYPE");
        f.setHidden(); 
        f.setFunction("''");

        f = itemblk0.addField("STR_CODE");
        f.setHidden(); 
        f.setFunction("''");

        f = itemblk0.addField("COMPANY");
        f.setHidden(); 
        f.setFunction("''");

        f = itemblk0.addField("AUTH_ID");
        f.setHidden(); 
        f.setFunction("Purchase_Requisition_API.Get_Authorize_Id(REQUISITION_NO)");

        f = itemblk0.addField("REQ_STATE");
        f.setHidden(); 
        f.setFunction("Purchase_Req_Util_API.Get_Requisition_Status(REQUISITION_NO)");


        f = itemblk0.addField("ENABL10");
        f.setHidden();
        f.setFunction("'0'");

        f = itemblk0.addField( "STRING_CODE");
        f.setFunction("'M100'");
        f.setHidden();

        f = itemblk0.addField("INQUIRY_NO");
        f.setReadOnly();
        f.setHidden();
        f.setFunction("Inquiry_Line_Part_Order_API.Get_Inquiry_No_From_Req(:REQUISITION_NO,:LINE_NO,:RELEASE_NO)");

        f = itemblk0.addField("ITEM0_CURRENCY_CODE");
        f.setFunction("Purchase_Req_Util_API.Get_Line_Currency_Code(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");
        f.setReadOnly();
        f.setHidden();

        f = itemblk0.addField("ITEM0_PROJECT_ID");
        f.setFunction("Purchase_Req_Util_API.Get_Line_Project_Id(:ITEM0_REQUISITION_NO,:LINE_NO,:RELEASE_NO)");
        f.setReadOnly();
        f.setHidden();

        f = itemblk0.addField("ITEM0_ORDER_CODE");
        f.setFunction("Purchase_Req_Util_API.Get_Order_Code(:ITEM0_REQUISITION_NO)");
        f.setReadOnly();
        f.setHidden();

        f = itemblk0.addField("REQ_TO_ORD_VLD");
        f.setHidden();
        f.setReadOnly();
        f.setFunction("''");

        f = itemblk0.addField("NEW_ORDER");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("DEFAULT_BUYER");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("SITE_DATE","Datetime");
        f.setFunction("''");
        f.setHidden();      

        f = itemblk0.addField("EXCHANGE_ITEM");
        f.setHidden();

        f = itemblk0.addField("ADD_NO");
        f.setFunction("''");
        f.setHidden();      

        f = itemblk0.addField("CLIENT_VAL");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("AOUTH");
        f.setHidden(); 
        f.setFunction("Purchase_Requisition_API.Authorization_Required(REQUISITION_NO)");


        f = itemblk0.addField("INV_TRACK");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("AFT_TRACK");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("CHECK_EXIST");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("CHECK_MCH_CODE");
        f.setFunction("Active_Separate_API.Get_Mch_Code(:WO_NO)");
        f.setHidden();

        f = itemblk0.addField("PART_EXIST");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("SUPP_EXIST");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("ACTIVEIND");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("ACTIVEIND_DB");
        f.setHidden();
        f.setFunction("''"); 

	f = itemblk0.addField("NOTETXT");
        f.setHidden();
        f.setFunction("''"); 


        itemblk0.setView("PART_WO_REQUIS_PURCH_LINE");
        itemblk0.defineCommand("PART_WO_REQUIS_LINE_API","New__,Modify__,Remove__,FREEZE__");
        itemblk0.setMasterBlock(headblk);

        itemset0 = itemblk0.getASPRowSet();

        itemtbl0 = mgr.newASPTable(itemblk0);
        itemtbl0.setTitle(mgr.translate("PCMWWORKORDERREQUISHEADERRMBITM0: Part Requisition Lines"));
        itemtbl0.setWrap();
        itemtbl0.enableRowSelect();

        itembar0 = mgr.newASPCommandBar(itemblk0);

        itembar0.addCustomCommand("sparePartObject0", mgr.translate("PCMWWORKORDERREQUISHEADERRMBSPAREPARTOBJ: Spare Parts in Object..."));
        itembar0.addCustomCommand("objStructure0", mgr.translate("PCMWWORKORDERREQUISHEADERRMBOBJSTRUCT: Object Structure...")); 
        itembar0.addCustomCommand("detchedPartList0", mgr.translate("PCMWWORKORDERREQUISHEADERRMBSPAREINDETACH: Spare Parts in Detached Part List..."));

        itembar0.addCustomCommand("planRequisitionLine0", mgr.translate("PCMWWORKORDERREQUISHEADERRMBCHGTOPLAN: Change to Planned"));
        itembar0.addCustomCommand("releaseRequisitionLine0", mgr.translate("PCMWWORKORDERREQUISHEADERRMBRELEASE: Release"));
        itembar0.addCustomCommandSeparator();
        itembar0.addCustomCommand("requisitionToRequestMain0", mgr.translate("PCMWWORKORDERREQUISHEADERRMBREQTOREQ: Requisition to Request..."));
        itembar0.addCustomCommand("requisitionToOrderMain0", mgr.translate("PCMWWORKORDERREQUISHEADERRMBREQTOORD: Requisition to Order..."));
        itembar0.addCustomCommand("prePost0", mgr.translate("PCMWWORKORDERREQUISHEADERRMBPREPOST: Pre Posting..."));
        itembar0.addCustomCommand("supplierPerPart", mgr.translate("PCMWWORKORDERREQUISHEADERRMBSUPPERPRT: Supplier per Part..."));

        if (mgr.isPresentationObjectInstalled("purchw/PurchaseOrder.page"))
            itembar0.addCustomCommand("purchaseOrder0", mgr.translate("PCMWWORKORDERREQUISHEADERRMBPURCHORD: Purchase Order..."));
        if (mgr.isPresentationObjectInstalled("purchw/Inquiry.page"))
            itembar0.addCustomCommand("request0", mgr.translate("PCMWWORKORDERREQUISHEADERREQUESTRMB: Request..."));
	itembar0.addCustomCommand("notes0", mgr.translate("PCMWWORKORDERREQUISHEADERRMBNOTES0: Notes..."));
        if (mgr.isPresentationObjectInstalled("mpccow/DocumentTextDlg.page"))
            itembar0.addCustomCommand("documentText0", mgr.translate("PCMWWORKORDERREQUISHEADERRMBDOCTEXTP: Document Text..."));

        // 040219  ARWILK  Begin  (Enable Multirow RMB actions)
        itembar0.addCommandValidConditions("sparePartObject0", "CHECK_MCH_CODE","Disable","null");
        itembar0.addCommandValidConditions("objStructure0", "CHECK_MCH_CODE","Disable","null");
        itembar0.addCommandValidConditions("detchedPartList0", "CHECK_MCH_CODE","Disable","null");

        itembar0.addCommandValidConditions("planRequisitionLine0",          "LINE_STATUS",   "Enable",   "Released");
        itembar0.addCommandValidConditions("releaseRequisitionLine0",       "LINE_STATUS",   "Enable",   "Planned");

        itembar0.addCommandValidConditions("requisitionToRequestMain0",     "LINE_STATUS",   "Enable",    "Authorized;Partially Authorized;Released");
        itembar0.appendCommandValidConditions("requisitionToRequestMain0",  "INQUIRY_NO",         "null");
        itembar0.appendCommandValidConditions("requisitionToRequestMain0",  "EXCHANGE_ITEM",      "Item not exchanged");


        itembar0.addCommandValidConditions("requisitionToOrderMain0",       "ITEM0_ORDER_CODE",   "Enable",   "1;2;6");
        itembar0.appendCommandValidConditions("requisitionToOrderMain0", "AOUTH","NOT_REQUIRED");
        itembar0.appendCommandValidConditions("requisitionToOrderMain0", "REQ_STATE","Partially Authorized;Authorized;Released;");
        itembar0.appendCommandValidConditions("requisitionToOrderMain0", "ORDER_NO","null");
        itembar0.appendCommandValidConditions("requisitionToOrderMain0", "LINE_STATUS","Partially Authorized;Authorized;Released");

        if (mgr.isPresentationObjectInstalled("purchw/PurchaseOrder.page"))
            itembar0.addCommandValidConditions("purchaseOrder0",            "ORDER_NO",            "Disable",   "null");
        if (mgr.isPresentationObjectInstalled("purchw/Inquiry.page"))
            itembar0.addCommandValidConditions("request0",                  "INQUIRY_NO",         "Disable",   "null");
        // 040219  ARWILK  End  (Enable Multirow RMB actions)

        itembar0.defineCommand(itembar0.OKFIND, "okFindITEM0");   
        itembar0.defineCommand(itembar0.NEWROW, "newRowITEM0");
        itembar0.defineCommand(itembar0.COUNTFIND, "countFindITEM0");    

        itembar0.defineCommand(itembar0.SAVERETURN , "saveReturn0", "checkItem0Fields(-1)");
        itembar0.defineCommand(itembar0.SAVENEW , "saveNew0", "checkItem0Fields(-1)"); 

        // 040219  ARWILK  Begin  (Enable Multirow RMB actions)
        itembar0.enableMultirowAction();
        itembar0.forceEnableMultiActionCommand("sparePartObject0");
        itembar0.forceEnableMultiActionCommand("objStructure0");
        itembar0.forceEnableMultiActionCommand("detchedPartList0");

        itembar0.removeFromMultirowAction("prePost0");
        itembar0.removeFromMultirowAction("supplierPerPart");
        if (mgr.isPresentationObjectInstalled("mpccow/DocumentTextDlg.page"))
            itembar0.removeFromMultirowAction("documentText0");
        // 040219  ARWILK  End  (Enable Multirow RMB actions)

	itembar0.removeFromMultirowAction("notes0");

        itembar0.enableCommand(itembar0.FIND);
        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

        //--------------------------------------------------------------------------------------------
        //--------------------------------------------------------------------------------------------
        //--------------------------------------------------------------------------------------------

        itemblk1 = mgr.newASPBlock("ITEM1");

        f = itemblk1.addField("ITEM1_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk1.addField("ITEM1_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk1.addField("ITEM1_WO_NO","Number");
        f.setSize(11);
        f.setMandatory();
        f.setHidden();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_WO_NO: WO no");
        f.setDbName("WO_NO");

        f = itemblk1.addField("ITEM1_REQUISITION_NO");
        f.setSize(11);
        f.setDynamicLOV("WORK_ORDER_REQUIS_HEADER","WO_NO",600,445);
        f.setMandatory();
        f.setHidden();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1REQUISITIONNO: Requisition no");
        f.setDbName("REQUISITION_NO");

        f = itemblk1.addField("ITEM1_LINE_NO");
        f.setSize(11);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1LINENO: Line No");
        f.setDbName("LINE_NO");

        f = itemblk1.addField("ITEM1_RELEASE_NO");
        f.setSize(11);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_RELEASE_NO: Release No");
        f.setDbName("RELEASE_NO");

        f = itemblk1.addField("ITEM1_CONTRACT");
        f.setSize(11);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        // 031107  ARWILK  Begin  (Bug#110151)
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_CONTRACT: Site");
        f.setDbName("CONTRACT");
        f.setHidden();
        // 031107  ARWILK  End  (Bug#110151)

        f = itemblk1.addField("ITEM_NOTE_TEXT2");
        f.setSize(25);                     
        f.setHeight(3);                    
        f.setMandatory();
        f.setDefaultNotVisible();
        f.setDbName("DESCRIPTION");
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM_NOTE_TEXT2: Description");

        // 031107  ARWILK  Begin  (Bug#110151)
        f = itemblk1.addField("ASSORTMENT");
        f.setSize(20);                     
        f.setMaxLength(20);
        f.setUpperCase();
        f.setInsertable();
        f.setDynamicLOV("SUPPLIER_ASSORTMENT",600,445);
        f.setCustomValidation("ASSORTMENT", "ITEM1_SUPPLIER_ASSORTMENT_DESC");
        f.setLabel("PCMWWORKORDERSUPPASSORTMENT: Supplier Assortment");

        f = itemblk1.addField("ITEM1_SUPPLIER_ASSORTMENT_DESC");
        f.setSize(35);                     
        f.setMaxLength(2000);
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setFunction("Supplier_Assortment_API.Get_Description(Purchase_Req_Util_API.Get_Line_Assortment(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO))"); 
        f.setLabel("PCMWWORKORDERSUPPASSORTDESCRIPTION: Supplier Assortment Description");
        // 031107  ARWILK  End  (Bug#110151)

        f = itemblk1.addField("STAT_GRP");
        f.setSize(15);
        f.setDynamicLOV("PURCHASE_PART_GROUP",600,445);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBSTATGRP: Purchase Group");
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_VENDORNO");
        f.setSize(11);
        f.setUpperCase();
        f.setDynamicLOV("SUPPLIER_INFO",600,445);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_VENDORNO: Supplier");
        f.setDbName("VENDOR_NO");
        f.setCustomValidation("ITEM1_VENDORNO,ITEM1_COMPANY,ITEM1_CONTRACT,TAXABLE_DB,FEE_CODE","ITEM1_VENDORNAME,ITEM1_VENDORCONTACT,FEE_CODE,FEEDESC,FEE_TYPE_DB");                    
        
        f = itemblk1.addField("ITEM1_VENDORNAME");
        f.setSize(25);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBVNAME1: Supplier Name");
        f.setFunction("Purchase_Req_Util_API.Get_Line_Vendor_Name(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)"); 

        f = itemblk1.addField("ITEM1_VENDORCONTACT");
        f.setSize(10);
        f.setDynamicLOV("SUPP_DEF_CONTACT_LOV","ITEM1_VENDORNO SUPPLIER_ID");  
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERREQUISHEADERRMBVENDORCONT1: Contacts"));
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBVCONTRACT2: Supplier Contact");
        f.setDbName("CONTACT");

        f = itemblk1.addField("ITEM1_WANTEDRECEIPTDATE","Date");
        f.setSize(21);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_WANTEDRECEIPTDATE: Wanted Receipt Date");
        f.setDbName("WANTED_RECEIPT_DATE");

        f = itemblk1.addField("ITEM1_ORIGINALQTY","Number", "#"); 
        f.setSize(19);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_ORIGINALQTY: Original Quantity");
        f.setDbName("ORIGINAL_QTY");
        f.setCustomValidation("ITEM1_CATALOG_CONTRACT,ITEM1_CATALOG_NO,ITEM1_ORIGINALQTY,ITEM1_PRICE_LIST_NO,ITEM1_AGREEMENT_ID,ITEM1_CUSTOMER_NO","ITEM1_PRICE_LIST_NO,DISCOUNT,ITEM1_LISTPRICE,ITEM1_SALESPRICEAMOUNT");

        f = itemblk1.addField("PRICE_TYPE_DB");
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBESTPRICE: Estimated Price");
        f.setCheckBox("CORRECT,ESTIMATED");
        f.setDefaultNotVisible();

        f = itemblk1.addField("FBUY_UNIT_PRICE","Money","#.##");
        f.setSize(19);
        f.setDefaultNotVisible();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBPRICECURR: Price/Curr");

        //Bug 67719, start 
        f = itemblk1.addField("ITEM1_FBUY_UNIT_PRICE_TAX","Money");
        f.setSize(19);
        f.setFunction("DECODE(Purchase_Part_Supplier_Api.Get_Tax_Regime_Db(Purchase_Req_Util_API.Get_Line_Contract(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)), 'VAT', Purchase_Req_Util_API.Get_Line_Fbuy_Unit_Price(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)  * ( 1 + Statutory_Fee_API.Get_Fee_Rate (Site_API.Get_Company(Purchase_Req_Util_API.Get_Line_Contract(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)),PURCHASE_REQ_LINE_NOPART_API.Get_Fee_Code(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO))/100), 'MIX', Purchase_Req_Util_API.Get_Line_Fbuy_Unit_Price(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO) * ( 1 + Statutory_Fee_API.Get_Fee_Rate (Site_API.Get_Company(Purchase_Req_Util_API.Get_Line_Contract(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)),PURCHASE_REQ_LINE_NOPART_API.Get_Fee_Code(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO))/100), NULL)");        
        f.setLabel("PURCHWPURCHASEREQUISITIONPRICEBASCURINCTAX1: Price/Curr incl. Tax");
        f.setDefaultNotVisible();
        f.setReadOnly(); 
        
        f = itemblk1.addField("BUY_UNIT_PRICE","Money","#.##");
        f.setSize(19);
        f.setDefaultNotVisible();
        f.setLabel("PCMWWORKORDERREQUISPRICEBASE1: Price/Base");
                
        f = itemblk1.addField("ITEM1_BUY_UNIT_PRICE_INCL_TAX","Money");
        f.setSize(19);
        f.setLabel("PCMWITEMPRICEBASEUNITINCLTAX1: Price/Base incl. Tax");
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setFunction("DECODE(Purchase_Part_Supplier_Api.Get_Tax_Regime_Db(Purchase_Req_Util_API.Get_Line_Contract(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)), 'VAT', PURCHASE_REQ_LINE_NOPART_API.Get_Buy_Unit_Price(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO) * ( 1 + Statutory_Fee_API.Get_Fee_Rate (Site_API.Get_Company(Purchase_Req_Util_API.Get_Line_Contract(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)),PURCHASE_REQ_LINE_NOPART_API.Get_Fee_Code(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO))/100), 'MIX', PURCHASE_REQ_LINE_NOPART_API.Get_Buy_Unit_Price(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO) * ( 1 + Statutory_Fee_API.Get_Fee_Rate (Site_API.Get_Company(Purchase_Req_Util_API.Get_Line_Contract(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)),PURCHASE_REQ_LINE_NOPART_API.Get_Fee_Code(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO))/100), NULL)");
        
        f = itemblk1.addField("DISCOUNT","Money");
        f.setSize(19);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBDISCOUNT: Discount");
        f.setDbName("LINE_DISCOUNT");
        
        //Bug 76748, start
        f = itemblk1.addField("ITEM1_COMPANY");
        f.setHidden(); 
        f.setFunction("Site_API.Get_Company(:ITEM1_CATALOG_CONTRACT)");

        f = itemblk1.addField("TAXABLE_DB");
        f.setSize(5);
        f.setCheckBox("FALSE,TRUE");
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBTAXABLE: Taxable");

        f = itemblk1.addField("FEE_TYPE_DB");
        f.setFunction("DECODE(Purchase_Part_Supplier_API.Get_Tax_Regime_Db(:ITEM1_CATALOG_CONTRACT), 'VAT','VAT;CALCVAT;NOTAX', 'MIX','VAT;CALCVAT;NOTAX')");
        f.setHidden();  

        f = itemblk1.addField("FEE_CODE");
        f.setSize(19);
        f.setDynamicLOV("STATUTORY_FEE_DEDUCT_MULTIPLE","ITEM1_COMPANY COMPANY, FEE_TYPE_DB");
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBFEECODE: Tax Code");
        f.setCustomValidation("FEE_CODE,ITEM1_COMPANY","FEEDESC");

        f = itemblk1.addField("FEEDESC");
        f.setSize(25);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBFEEDESC: Tax Code Description");
        f.setFunction("STATUTORY_FEE_API.Get_Description(Site_API.Get_Company(Purchase_Req_Util_API.Get_Line_Contract(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)), PURCHASE_REQ_LINE_NOPART_API.Get_Fee_Code(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO))");

        f = itemblk1.addField("ITEM1_TAX_REGIME");
        f.setFunction("Purchase_Part_Supplier_Api.Get_Tax_Regime_Db(Purchase_Req_Util_API.Get_Line_Contract(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO))");
        f.setHidden();

        f = itemblk1.addField("TAX_EXEMPT");
        f.setFunction("Supplier_API.Supplier_Is_Taxable(Site_API.Get_Company(Purchase_Req_Util_API.Get_Line_Contract(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)),Purchase_Req_Util_API.Get_Line_Vendor_No(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO))");
        f.setHidden();
        //Bug 76748, end

        f = itemblk1.addField("ITEM1_ADD_COST_AMOUNT","Money");
        f.setSize(19);
        f.setLabel("PCMWWORKORDERREQUISADDITIONALCOST1: Additional Cost");
        f.setFunction("PURCHASE_REQ_UTIL_API.Get_Line_Additional_Cost(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)");
        f.setDefaultNotVisible();
        f.setReadOnly(); 

        f = itemblk1.addField("ITEM1_ADD_COST_AMOUNT_INCL_TAX","Money");
        f.setSize(19);
        f.setLabel("PCMWWORKORDERREQUISADDCOSTINCLTAX1: Additional Cost incl. Tax");
        f.setFunction("DECODE(Purchase_Part_Supplier_Api.Get_Tax_Regime_Db(Purchase_Req_Util_API.Get_Line_Contract(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)), 'VAT',DECODE(Purchase_Part_API.Get_Taxable(Purchase_Req_Util_API.Get_Line_Contract(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO),Purchase_Req_Util_API.Get_Line_Part_No(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)), 'TRUE', (PURCHASE_REQ_UTIL_API.Get_Line_Additional_Cost(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)) * ( 1 + Purchase_Part_Supplier_API.Get_Assumed_Vat_Percentage(Purchase_Req_Util_API.Get_Line_Contract(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO),Purchase_Req_Util_API.Get_Line_Part_No(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO),(Purchase_Req_Util_API.Get_Line_Vendor_No(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)))/100), PURCHASE_REQ_UTIL_API.Get_Line_Additional_Cost(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)), 'MIX',DECODE(Purchase_Part_API.Get_Taxable(Purchase_Req_Util_API.Get_Line_Contract(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO),Purchase_Req_Util_API.Get_Line_Part_No(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)), 'TRUE', (PURCHASE_REQ_UTIL_API.Get_Line_Additional_Cost(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)) * ( 1 + Purchase_Part_Supplier_API.Get_Assumed_Vat_Percentage(Purchase_Req_Util_API.Get_Line_Contract(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO),Purchase_Req_Util_API.Get_Line_Part_No(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO),(Purchase_Req_Util_API.Get_Line_Vendor_No(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)))/100), (PURCHASE_REQ_UTIL_API.Get_Line_Additional_Cost(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO))),NULL)");
        f.setDefaultNotVisible();
        f.setReadOnly();   

        f = itemblk1.addField("ITEM1_TOTAL_BASE","Money");
        f.setSize(19);
        f.setLabel("PCMWWORKORDERREQUISTOTALBASE1: Total/Base");
        f.setFunction("PURCHASE_REQ_LINE_NOPART_API.Get_Line_Total(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)");
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = itemblk1.addField("ITEM1_TOTAL_CURR","Money");
        f.setSize(19);
        f.setLabel("PCMWWORKORDERREQUISTOTALCURR1: Total/Curr");
        f.setFunction("PURCHASE_REQ_LINE_NOPART_API.Get_Line_Tot_In_Price_Curr(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)");
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = itemblk1.addField("ITEM1_GROSS_TOTAL_BASE","Money");
        f.setSize(19);
        f.setLabel("PCMWWORKORDERREQUISGROSSTOTALBASE1: Gross Total/Base");
        f.setFunction("PURCHASE_REQ_UTIL_API.Get_Inc_Tax_Total_Base(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)");
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = itemblk1.addField("ITEM1_GROSS_TOTAL_CURR","Money");
        f.setSize(19);
        f.setLabel("PCMWWORKORDERREQUISGROSSTOTALCURR1: Gross Total/Curr");
        f.setFunction("PURCHASE_REQ_UTIL_API.Get_Inc_Tax_Total_Curr(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)");
        f.setDefaultNotVisible();
        f.setReadOnly();
        //Bug 67719, end

        f = itemblk1.addField("ITEM1_LATESTORDERDATE","Date");
        f.setSize(15);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_LATESTORDERDATE: Last Order Date");
        f.setDbName("LATEST_ORDER_DATE");

        f = itemblk1.addField("ITEM1_STATUSCODE");
        f.setSize(11);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_STATUSCODE: Status");
        f.setDbName("LINE_STATUS");

        f = itemblk1.addField("ITEM1_PLANNERBUYER");
        f.setSize(11);
        f.setReadOnly();
        f.setDynamicLOV("INVENTORY_PART_PLANNER",600,445);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_PLANNERBUYER: Planner");
        f.setFunction("Purchase_Req_Util_API.Get_Line_Planner_Buyer(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)");
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_BUYERCODE");
        f.setSize(11);
        f.setInsertable();
        f.setDynamicLOV("PURCHASE_BUYER_LOV",600,445);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBBUYERCODE1: Buyer");
        f.setDbName("BUYER_CODE");
        f.setUpperCase();

        f = itemblk1.addField("ITEM1_TECHCOORDID");
        f.setSize(11);
        f.setDynamicLOV("TECHNICAL_COORDINATOR_LOV");
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERREQUISHEADERRMBTECHCOOD1: Tech Coordinators"));
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBTECHCOORDID2: Tech Coordinator");
        f.setDefaultNotVisible();
        f.setDbName("TECHNICAL_COORDINATOR_ID");
        f.setUpperCase();

        f = itemblk1.addField("ITEM1_COST_RETURN");
        f.setSize(17);
        f.setSelectBox();
        f.enumerateValues("COST_RETURN_API");
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_COST_RETURN: Cost Return at");
        f.setDbName("COST_RETURN");
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_ORDERNO");
        f.setSize(11);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_ORDERNO: Order No");
        f.setDbName("ORDER_NO");
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_BUYUNITMEAS");
        f.setSize(16);
        f.setDynamicLOV("ISO_UNIT",600,445);
        f.setMandatory();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_BUYUNITMEAS: Unit");
        f.setDbName("BUY_UNIT_MEAS");
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_ASSGLINENO");
        f.setSize(14);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_ASSGLINENO: Order Line No");
        f.setDbName("ASSG_LINE_NO");
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_ASSGRELEASENO");
        f.setSize(18);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_ASSGRELEASENO: Order Release No");
        f.setDbName("ASSG_RELEASE_NO");
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_LINEORDERSTATUS");
        f.setSize(18);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_LINEORDERSTATUS: Order Line Status");
        f.setFunction("Purchase_Req_Util_API.Get_Line_Order_Status(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)");
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_CATALOG_CONTRACT");
        f.setSize(17);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_CATALOG_CONTRACT: Sales Part Site");
        f.setDbName("CATALOG_CONTRACT");
        f.setUpperCase();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_CATALOG_NO");
        f.setSize(19);
        f.setDynamicLOV("SALES_PART_SERVICE_LOV","ITEM1_CATALOG_CONTRACT CONTRACT",600,445);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_CATALOG_NO: Sales Part Number");
        f.setDbName("CATALOG_NO");
        f.setCustomValidation("ITEM1_CATALOG_CONTRACT,ITEM1_CATALOG_NO,ITEM1_ORIGINALQTY,ITEM1_LINE_NO,ITEM1_RELEASE_NO,ITEM1_AGREEMENT_ID,ITEM1_CUSTOMER_NO,ITEM1_PRICE_LIST_NO","ITEM1_CATALOGDESC,LINE_DESCRIPTION,ITEM1_LISTPRICE,ITEM1_SALESPRICEAMOUNT,ITEM1_PRICE_LIST_NO,DISCOUNT");
        f.setUpperCase();
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_CATALOGDESC");
        f.setSize(23);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_CATALOGDESC: Catalog No Description");
        f.setFunction("''");
        f.setDefaultNotVisible();
        f.setHidden();

        f = itemblk1.addField("LINE_DESCRIPTION");
        f.setSize(23);
        f.setMaxLength(35);
        f.setDefaultNotVisible();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1LINEDESCRIPTION: Sales Part Description");
        

        f = itemblk1.addField("ITEM1_PRICE_LIST_NO");
        f.setSize(15);
        f.setDynamicLOV("SALES_PRICE_LIST",600,445);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_PRICE_LIST_NO: Price List No");
        f.setDbName("PRICE_LIST_NO");
        f.setUpperCase();
        f.setCustomValidation("ITEM1_PRICE_LIST_NO,ITEM1_CATALOG_CONTRACT,ITEM1_CATALOG_NO,ITEM1_ORIGINALQTY,ITEM1_AGREEMENT_ID,ITEM1_CUSTOMER_NO","ITEM1_PRICE_LIST_NO,DISCOUNT,ITEM1_LISTPRICE,ITEM1_SALESPRICEAMOUNT");
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_LISTPRICE", "Money","#.##");
        f.setSize(12);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_LISTPRICE: Sales Price");
        f.setDbName("SALES_PRICE");
        f.setCustomValidation("ITEM1_LISTPRICE,ITEM1_ORIGINALQTY,DISCOUNT","ITEM1_SALESPRICEAMOUNT,ITEM1_LISTPRICE");
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_SALESPRICEAMOUNT", "Money","#.##");
        f.setSize(20);
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_SALESPRICEAMOUNT: Sales Price Amount");
        f.setReadOnly();
        f.setFunction("''");
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_REQUEST_TYPE");
        f.setSize(25);
        f.setSelectBox();
        f.enumerateValues("REQUEST_TYPE_API");
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBREQTYPE1: Request Type");
        f.setDefaultNotVisible();
        f.setDbName("REQUEST_TYPE");

        f = itemblk1.addField("ITEM1_NOTE_TEXT");
        f.setSize(25);                     
        f.setHeight(3);                    
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_NOTE_TEXT: Note");
        f.setDbName("NOTE_TEXT");
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_DOCTEXTEXIST");
        f.setCheckBox("0,1"); 
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBDOCUMENTTEXT1: Document Text");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setFunction("Decode(Document_Text_API.Note_Id_Exist(Purchase_Req_Util_API.Get_Line_Note_Id(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)),0,0,1)");
        
        f = itemblk1.addField("ITEM1_DBSTATUSCODE");
        f.setSize(11);
        f.setHidden();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_DBSTATUSCODE: Status");
        f.setDbName("LINE_STATE");

        f = itemblk1.addField("ITEM1_WANTEDDELIVERYDATE","Date");
        f.setSize(8);
        f.setHidden();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_WANTEDDELIVERYDATE: wanted delivery");
        f.setFunction("Purchase_Req_Util_API.Get_Line_Wanted_Delivery_Date(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)");

        f = itemblk1.addField("ITEM1_NOTETEXT");
        f.setSize(11);
        f.setHidden();
        f.setHeight(3);
        f.setDefaultNotVisible();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_NOTETEXT: Note");
        f.setFunction("Purchase_Req_Util_API.Get_Line_Note_Text(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)");

        f = itemblk1.addField("ITEM1_CONTRACT_DUM");
        f.setSize(11);
        f.setHidden();
        f.setFunction("''");
        f.setUpperCase();

        f = itemblk1.addField("ITEM1_NOTEID","Number", "#");
        f.setSize(11);
        f.setHidden();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1_NOTEID: Note");
        f.setFunction("Purchase_Req_Util_API.Get_Line_Note_Id(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)");

        f = itemblk1.addField("ITEM1_PREPOST_EXIST");
        f.setReadOnly(); 
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBITEM1PREPOSTEXIST: Pre Accounting Exists");
        f.setFunction("Pre_Accounting_Api.Pre_Accounting_Exist(Purchase_Req_Util_API.Get_Line_Pre_Accounting_Id(REQUISITION_NO,LINE_NO,RELEASE_NO))");
        f.setCheckBox("0,1");

        f = itemblk1.addField("ITEM1_AGREEMENT_ID");
        f.setSize(50);
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("ITEM1_CUSTOMER_NO");
        f.setSize(50);
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("ITEM1_AUTH_ID");
        f.setHidden(); 
        f.setFunction("Purchase_Requisition_API.Get_Authorize_Id(REQUISITION_NO)");

        f = itemblk1.addField("ITEM1_REQ_STATE");
        f.setHidden(); 
        f.setFunction("Purchase_Req_Util_API.Get_Requisition_Status(REQUISITION_NO)");

        f = itemblk1.addField( "STRING_CODE1");
        f.setFunction("'M100'");
        f.setDbName("STRING_CODE");
        f.setHidden();

        f = itemblk1.addField("ITEM1_ENABL10");
        f.setFunction("'0'");
        f.setDbName("ENABL10");
        f.setHidden();

        f = itemblk1.addField("ITEM1_CURRENCY_CODE");
        f.setFunction("Purchase_Req_Util_API.Get_Line_Currency_Code(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)");
        f.setReadOnly();
        f.setHidden();

        f = itemblk1.addField("ITEM1_PROJECT_ID");
        f.setFunction("Purchase_Req_Util_API.Get_Line_Project_Id(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)");
        f.setReadOnly();
        f.setHidden();

        f = itemblk1.addField("ITEM1_ORDER_CODE");
        f.setFunction("Purchase_Req_Util_API.Get_Order_Code(:ITEM1_REQUISITION_NO)");
        f.setReadOnly();
        f.setHidden();

        f = itemblk1.addField("ITEM1_TEMPLATE_ID");
        f.setSize(21);
        f.setHidden();
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBTEMPLID: Template ID");
        f.setFunction("Purchase_Req_Util_API.Get_Line_Template_Id(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)");

        f = itemblk1.addField("REQ_TO_ORD_VLD2");
        f.setHidden();
        f.setReadOnly();
        f.setFunction("''");

        f = itemblk1.addField("ITEM1_INQUIRY_NO");
        f.setReadOnly();
        f.setHidden();
        f.setFunction("Inquiry_Line_Nopart_Order_API.Get_Inquiry_No_From_Req(:REQUISITION_NO,:LINE_NO,:RELEASE_NO)");

        f = itemblk1.addField("ITEM1_EXCHANGE_ITEM");
        f.setHidden();
        f.setFunction("PURCHASE_REQ_LINE_PART_API.Get_Exchange_Item(:ITEM1_REQUISITION_NO,:ITEM1_LINE_NO,:ITEM1_RELEASE_NO)");

        f = itemblk1.addField("ITEM1_AOUTH");
        f.setHidden(); 
        f.setFunction("Purchase_Requisition_API.Authorization_Required(REQUISITION_NO)");

        itemblk1.setView("NOPART_WO_REQUIS_PURCH_LINE");
        itemblk1.defineCommand("NOPART_WO_REQUIS_LINE_API","New__,Modify__,Remove__");

        itemset1 = itemblk1.getASPRowSet();

        itemtbl1 = mgr.newASPTable(itemblk1);   
        itemtbl1.setTitle(mgr.translate("PCMWWORKORDERREQUISHEADERRMBITM1: No Part Requisition Lines"));
        itemtbl1.setWrap();
        itemtbl1.enableRowSelect();

        itemblk1.setMasterBlock(headblk);

        itembar1 = mgr.newASPCommandBar(itemblk1);

        itembar1.addCustomCommand("planRequisitionLine1", mgr.translate("PCMWWORKORDERREQUISHEADERRMBCHGTOPLAN: Change to Planned"));
        itembar1.addCustomCommand("releaseRequisitionLine1", mgr.translate("PCMWWORKORDERREQUISHEADERRMBRELEASE: Release"));
        itembar1.addCustomCommandSeparator();
        itembar1.addCustomCommand("requisitionToRequestMain1", mgr.translate("PCMWWORKORDERREQUISHEADERRMBREQTOREQ1: Requisition to Request..."));
        itembar1.addCustomCommand("requisitionToOrderMain1", mgr.translate("PCMWWORKORDERREQUISHEADERRMBREQTOORD1: Requisition to Order..."));
        itembar1.addCustomCommand("prePost1", mgr.translate("PCMWWORKORDERREQUISHEADERRMBPREPOST: Pre Posting..."));

        if (mgr.isPresentationObjectInstalled("purchw/PurchaseOrder.page"))
            itembar1.addCustomCommand("purchaseOrder1", mgr.translate("PCMWWORKORDERREQUISHEADERRMBPURCHORD1: Purchase Order..."));
        if (mgr.isPresentationObjectInstalled("purchw/Inquiry.page"))
            itembar1.addCustomCommand("request1", mgr.translate("PCMWWORKORDERREQUISHEADERRMBREQUESTRMB: Request..."));
        itembar1.addCustomCommand("notes1", mgr.translate("PCMWWORKORDERREQUISHEADERRMBNOTES1: Notes..."));
	if (mgr.isPresentationObjectInstalled("mpccow/DocumentTextDlg.page"))
            itembar1.addCustomCommand("documentText1", mgr.translate("PCMWWORKORDERREQUISHEADERRMBDOCTEXTNP: Document Text..."));

        itembar1.addCustomCommand("processText", mgr.translate("PCMWWORKORDERREQUISHEADERRMBTXTPOCESS1: Text Processing..."));

        // 040219  ARWILK  Begin  (Enable Multirow RMB actions)
        itembar1.addCommandValidConditions("planRequisitionLine1",          "LINE_STATUS",      "Enable",   "Released");
        itembar1.addCommandValidConditions("releaseRequisitionLine1",       "LINE_STATUS",      "Enable",   "Planned");

        itembar1.addCommandValidConditions("requisitionToRequestMain1",     "LINE_STATUS",      "Enable",    "Authorized;Partially Authorized;Released");
        itembar1.appendCommandValidConditions("requisitionToRequestMain1",  "LINE_STATUS",      "null");
        itembar1.appendCommandValidConditions("requisitionToRequestMain1",  "LINE_STATUS",   "Item not exchanged");


        itembar1.addCommandValidConditions("requisitionToOrderMain1",       "ITEM1_ORDER_CODE",   "Enable",   "1;2;6");
        itembar1.appendCommandValidConditions("requisitionToOrderMain1", "ITEM1_AOUTH","NOT_REQUIRED");
        itembar1.appendCommandValidConditions("requisitionToOrderMain1", "ITEM1_REQ_STATE","Partially Authorized;Authorized;Released");
        itembar1.appendCommandValidConditions("requisitionToOrderMain1", "ORDER_NO","null");
        itembar1.appendCommandValidConditions("requisitionToOrderMain1", "LINE_STATUS","Partially Authorized;Authorized;Released");


        if (mgr.isPresentationObjectInstalled("purchw/PurchaseOrder.page"))
            itembar1.addCommandValidConditions("purchaseOrder1",            "ORDER_NO",         "Disable",   "null");
        if (mgr.isPresentationObjectInstalled("purchw/Inquiry.page"))
            itembar1.addCommandValidConditions("request1",                  "ITEM1_INQUIRY_NO",      "Disable",   "null");
        // 040219  ARWILK  End  (Enable Multirow RMB actions)

        itembar1.defineCommand(itembar1.OKFIND, "okFindITEM1");
        itembar1.defineCommand(itembar1.NEWROW, "newRowITEM1");
        itembar1.defineCommand(itembar1.COUNTFIND, "countFindITEM1");

        itembar1.defineCommand(itembar1.SAVERETURN, "saveReturn1", "checkItem1Fields(-1)");
        itembar1.defineCommand(itembar1.SAVENEW , "saveNew1", "checkItem1Fields(-1)");
        itembar1.defineCommand(itembar1.CANCELEDIT , "cancelEdit1");

        itembar1.enableCommand(itembar1.FIND);

        // 040219  ARWILK  Begin  (Enable Multirow RMB actions)
        itembar1.enableMultirowAction();

        itembar1.removeFromMultirowAction("prePost1");
        if (mgr.isPresentationObjectInstalled("mpccow/DocumentTextDlg.page"))
            itembar1.removeFromMultirowAction("documentText1");
        itembar1.removeFromMultirowAction("processText");
        // 040219  ARWILK  End  (Enable Multirow RMB actions)

	itembar1.removeFromMultirowAction("notes1");

        itemlay1 = itemblk1.getASPBlockLayout();
        itemlay1.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        // 031107  ARWILK  Begin  (Bug#110151)
        itemlay1.setSimple("ITEM1_SUPPLIER_ASSORTMENT_DESC");
        // 031107  ARWILK  End  (Bug#110151)

        //--------------------------------------------------------------------------------------------
        //--------------------------------------------------------------------------------------------
        //--------------------------------------------------------------------------------------------

        itemblk2 = mgr.newASPBlock("ITEM2");
        f = itemblk2.addField("APPOWNER");
        f.setHidden();
        f.setFunction("''");

        itemblk2.setView("DUAL");
        itemset2 = itemblk2.getASPRowSet();

        //--------------------------------------------------------------------------------------------
        //--------------------------------------------------------------------------------------------
        //--------------------------------------------------------------------------------------------

        b = mgr.newASPBlock("COST_RETURN");

        b.addField("CLIENT_VALUES0");

        b = mgr.newASPBlock("ITEM1_COST_RETURN");

        b.addField("CLIENT_VALUES1");

        //--------------------------------------------------------------------------------------------
        //--------------------------------------------------------------------------------------------
        //--------------------------------------------------------------------------------------------


        // For Header in requisition to Request dialog:
        requestblk = mgr.newASPBlock("REQUEST");

        requestblk.addField( "TEMP_REQ_ORDER_NO" ).
        setFunction("''").
        setHidden();

        requestblk.addField("REQ_BUYER_CODE").
        setFunction("''").
        setSize(12).
        setUpperCase().
        setDynamicLOV("PURCHASE_BUYER").
        setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERREQUISHEADERRMBBUYER: Buyer")).
        setLabel("PCMWWORKORDERREQUISHEADERRMBBUYERCODE: Buyer").
        setMandatory();

        requestblk.addField( "REQ_CURRENCY_CODE" ).
        setUpperCase().
        setDynamicLOV("ISO_CURRENCY").
        setSize(12).
        setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERREQUISHEADERRMBCURCODE: Currency")).
        setFunction("''").
        setMandatory().
        setLabel("PCMWWORKORDERREQUISCURCODE: Currency");

        requestblk.addField("EXPIRY_DATE","Date" ).
        setFunction("''").
        setLabel("PCMWWORKORDERREQUISHEADERRMBLATESTREPDATE: Latest Reply date").
        setMandatory().
        setSize(12);

        requestblk.addField("REQ_BUY_UNIT_MEAS").
        setFunction("''").
        setDynamicLOV("ISO_UNIT").
        setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERREQUISHEADERRMBPUM: Purch U/M")).
        setLabel("PCMWWORKORDERREQUISHEADERRMBBUYUNITMEAS0: Purch U/M").
        setMandatory().
        setSize(10);

        requestblk.addField( "ADDTO_REQUEST").
        setSize(12).
        setFunction("''").
        setLabel("PCMWWORKORDERREQUISHEADERRMBADDTORQST: Add to Request");

        requestblk.setView("DUAL");
        requestblk.defineCommand("","New__,Modify__");
        requestset = requestblk.getASPRowSet();
        requesttbl = mgr.newASPTable(requestblk);
        requestbar = mgr.newASPCommandBar(requestblk);
        requestlay = requestblk.getASPBlockLayout();

        requestbar.addCustomCommand("refreshReqToRequisitLines", "Populate table");
        requestbar.disableCommand(requestbar.CANCELEDIT);
        requestbar.disableCommand(requestbar.SAVERETURN);
        requestbar.disableCommand(requestbar.BACKWARD);
        requestbar.disableCommand(requestbar.SAVENEW);
        requestbar.disableCommand(requestbar.CANCELNEW);

        requestlay.setDefaultLayoutMode(requestlay.CUSTOM_LAYOUT);
        requestlay.setEditable();


        // For the table in Requisition to Request dlg

        reqlineblk = mgr.newASPBlock("REQUESTLINE");

        reqlineblk.addField( "LINE_INQUIRY_NO" ).
        setSize(12).
        setLabel("PCMWWORKORDERREQUISHEADERRMBINQNO: Inquiry No").
        setFunction("INQUIRY_NO");

        reqlineblk.addField( "LINE_REVISION_NO" ).
        setSize(12).
        setLabel("PCMWWORKORDERREQUISHEADERRMBREVNO: Revision No").
        setDbName("REVISION_NO");

        reqlineblk.addField( "REQ_LINE_STATE" ).
        setSize(9).
        setLabel("PCMWWORKORDERREQUISHEADERRMBLSTATE: State").
        setDbName("STATE");

        reqlineblk.addField( "DATE_EXPIRES","Date").
        setSize(12).
        setLabel("PCMWWORKORDERREQUISHEADERRMBTIMELIMIT: Latest Reply Date");

        reqlineblk.addField( "LAST_ACTIVITY_DATE","Date").
        setSize(18).
        setLabel("PCMWWORKORDERREQUISHEADERRMBLACTDATE: Last Activity Date").
        setMaxLength(12);

        reqlineblk.addField( "LINE_PROJECT_ID").
        setDbName("PROJECT_ID").
        setHidden();

        reqlineblk.setView("INQUIRY_ORDER");
        reqlineblk.setMasterBlock(requestblk);
        reqlineset = reqlineblk.getASPRowSet();

        reqlinebar = mgr.newASPCommandBar(reqlineblk);
        reqlinebar.disableCommand(reqlinebar.NEWROW);
        reqlinebar.disableCommand(reqlinebar.FIND);
        reqlinebar.disableCommand(reqlinebar.VIEWDETAILS);
        reqlinebar.disableRowStatus();

        reqlinelay = reqlineblk.getASPBlockLayout();
        reqlinelay.setDefaultLayoutMode(reqlinelay.MULTIROW_LAYOUT);
        reqlinelay.unsetAutoLayoutSelect();

        reqlinebar.disableCommand(reqlinebar.EDITROW);
        reqlinebar.disableCommand(reqlinebar.DELETE);
        reqlinebar.disableCommand(reqlinebar.DUPLICATEROW);
        reqlinebar.addCustomCommand("addToOrderRequisition",mgr.translate("PCMWWORKORDERREQUISHEADERRMBADDEXSTRMB: Add Existing"));

        reqlinetbl = mgr.newASPTable(reqlineblk);
        reqlinetbl.disableEditProperties();

        // For the table in Requisition to order dlg

        orderblk = mgr.newASPBlock("ORDER");

        orderblk.addField( "DEFSITE" ).
        setFunction("''");

        orderblk.addField( "DEF_BUYER_SUPP" ).
        setSize(20).
        setFunction("''").
        setHidden();

        orderblk.addField("DEFAUTH").
        setLabel("PCMWWORKORDERREQUISHEADERRMBDEFCOORD: Coordinator").
        setCustomValidation("DEFAUTH","COOR_NAME").
        setFunction("''");

        orderblk.addField( "COOR_NAME" ).
        setSize(20).
        setFunction("''").
        setReadOnly();

        orderblk.addField( "DEFBUYER").
        setCustomValidation("DEFBUYER","BUYER_NAME").
        setLabel("PCMWWORKORDERREQUISHEADERRMBDEFBUYER: Buyer Code").
        setFunction("''");

        orderblk.addField( "BUYER_NAME" ).
        setSize(20).
        setFunction("''").
        setReadOnly();

        orderblk.addField( "DEF_BUYER_CODE" ).
        setFunction("''").
        setLabel("PCMWWORKORDERREQUISHEADERRMBDEFBUYERCODE: Use Default Buyer Code from Supplier").
        setCheckBox();

        orderblk.addField("ORDER_EXISTS").
        setHidden().
        setFunction("''");

        orderblk.addField("ORDER_SET_ORDER_NO").
        setHidden().
        setFunction("''");

        orderblk.setView("DUAL");

        orderset = orderblk.getASPRowSet();
        ordertbl = mgr.newASPTable(orderblk);
        orderbar = mgr.newASPCommandBar(orderblk);
        orderbar.disableMinimize();
        orderlay = orderblk.getASPBlockLayout();

        orderlay.setDefaultLayoutMode(orderlay.CUSTOM_LAYOUT);
        orderlay.setEditable();
        orderlay.defineGroup(mgr.translate("PCMWWORKORDERREQUISHEADERRMBPARAMETER: Parameters"),"DEFAUTH,COOR_NAME,DEFBUYER,BUYER_NAME,DEF_BUYER_CODE",true,true,1);

        orderlay.setSimple("COOR_NAME");
        orderlay.setSimple("BUYER_NAME");

        // order lines.

        lineblk = mgr.newASPBlock("ORDERLINE");

        lineblk.addField("LINE_OBJID").
        setDbName("OBJID").
        setHidden();

        lineblk.addField("LINE_OBJVERSION").
        setDbName("OBJVERSION").
        setHidden();

        lineblk.addField( "LINE_ORDER_NO" ).
        setSize(10).
        setLabel("PCMWWORKORDERREQUISHEADERRMBLINEORDERNO: Order No").
        setReadOnly().
        setFunction("ORDER_NO").
        setMaxLength(12);


        lineblk.addField( "LINE_CURRENCY_CODE" ).
        setSize(10).
        setLabel("PCMWWORKORDERREQUISHEADERRMBLINECURR: Currency").
        setReadOnly().
        setFunction("CURRENCY_CODE").
        setMaxLength(3);

        lineblk.addField( "LINE_CONTACT" ).
        setSize(15).
        setLabel("PCMWWORKORDERREQUISHEADERRMBLINECONTACT: Supplier Contact").
        setFunction("CONTACT");


        lineblk.addField( "BLANKET_ORDER" ).
        setLabel("PCMWWORKORDERREQUISHEADERRMBBLANKETORDER: Blanket No").
        setHidden();

        lineblk.addField( "LINE_VENDOR_NO" ).
        setLabel("PCMWWORKORDERREQUISHEADERRMBLINEVENDOR: Supplier").
        setFunction("VENDOR_NO").
        setHidden();

        lineblk.addField("LINEBLK_WANTED_RECEIPT_DATE", "Date").
        setLabel("PCMWWORKORDERREQUISHEADERRMBRECEIPTDATE: Receipt Date").
        setSize(20).
        setDbName("WANTED_RECEIPT_DATE");

        lineblk.setView("PURCHASE_ORDER po");
        lineblk.setMasterBlock(orderblk);
        lineset = lineblk.getASPRowSet();
        linebar = mgr.newASPCommandBar(lineblk);

        linebar.disableCommand(linebar.DUPLICATEROW);
        linebar.disableCommand(linebar.VIEWDETAILS);
        linebar.disableCommand(linebar.FIND);
        linebar.addCustomCommand("addToOrderSelected",mgr.translate("PCMWWORKORDERREQUISHEADERRMBADDTOORDBUT: Add To Order"));

        linetbl = mgr.newASPTable(lineblk);
        linelay = lineblk.getASPBlockLayout();
        linelay.setDefaultLayoutMode(linelay.MULTIROW_LAYOUT);

        // For Create supplier for purchase part dialog

        createSuppblk = mgr.newASPBlock("CREATESUP");

        createSuppblk.addField( "CRESUPP_COMPANY" ).
        setFunction("''").
        setHidden();

        createSuppblk.addField( "CRESUPP_CONTRACT" ).
        setFunction("''").
        setHidden();

        f = createSuppblk.addField("CRESUPP_TAX_REGIME");
        if (purchInst)
            f.setFunction("Purchase_Part_Supplier_Api.Get_Tax_Regime_Db(:CRESUPP_CONTRACT, :CRESUPP_SUPPLIER)");
        else
            f.setFunction("''");
        f.setHidden();

        createSuppblk.addField( "CRESUPP_PART_NO" ).
        setFunction("''").
        setReadOnly().
        setSize(25).
        setLabel("PCMWWORKORDERREQUISHEADERRMBCRESUPPPARTNO: Part No");

        createSuppblk.addField( "CRESUPP_DESCRIPTION" ).
        setFunction("''").
        setSize(35).
        setReadOnly().
        setLabel("PCMWWORKORDERREQUISHEADERRMBCRESUPPARTDES: Part Description");

        createSuppblk.addField( "CRESUPP_SUPPLIER" ).
        setFunction("''").
        setReadOnly().
        setSize(10).
        setLabel("PCMWWORKORDERREQUISHEADERRMBCRESUPPVENDORNO: Supplier");

        createSuppblk.addField( "CRESUPP_UM" ).
        setFunction("''").
        setSize(10).
        setDynamicLOV("ISO_UNIT").
        setMandatory().
        setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERREQUISHEADERRMBCRESUPPUNITMEAS: U/M")).
        setLabel("PCMWWORKORDERREQUISHEADERRMBCRESUPPUNITMEAS: U/M");

        f = createSuppblk.addField( "CRESUPP_PRICE","Money" );
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBCRESUPPPRICE: Price");
        f.setSize(10);
        f.setMandatory();
        f.setFunction("0");  // See history for this
        if (purchInst)
            f.setCustomValidation("purchw/PurchaseRequisitionValidations.page","CRESUPP_PRICE,CRESUPP_TAX_REGIME,CRESUPP_TAXABLE,CRESUPP_CONTRACT,CRESUPP_PART_NO,CRESUPP_TAX_PERCENTAGE","CRESUPP_PRICE_INCL_TAX");

        createSuppblk.addField( "CRESUPP_CURR_CODE" ).
        setFunction("''").
        setReadOnly().
        setSize(5).
        setLabel("PCMWWORKORDERREQUISHEADERRMBCRESUPPCURRCODE: Currency Code ");

        createSuppblk.addField("CRESUPP_PRICE_UNIT_MEAS").
        setSize(10).
        setDynamicLOV("ISO_UNIT").
        setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERREQUISHEADERRMBCOMCRESUPPPUM: Price U/M")).
        setLabel("PCMWWORKORDERREQUISHEADERRMBCRESUPPPRICEUNITMEAS: Price U/M");

        createSuppblk.addField("CRESUPP_PRICE_CONV_FACTOR","Number").
        setSize(10).
        setLabel("PCMWWORKORDERREQUISHEADERRMBCRESUPPPRICECONVFACTOR: Price Conv Factor");

        createSuppblk.addField("CRESUPP_TAXABLE").
        setFunction("''").
        setCheckBox("FALSE,TRUE").
        setLabel("PCMWWORKORDERREQUISHEADERRMBCRESUPPTAXABLE: Taxable ").
        setSize(5).
        setReadOnly();

        createSuppblk.addField("CRESUPP_TAX_PERCENTAGE").
        setFunction("''").
        setHidden();

        createSuppblk.addField("CRESUPP_VAT_CODE").
        setUpperCase().
        setSize(10).
        setFunction("''").
        setLabel("PCMWWORKORDERREQUISHEADERRMBTAXCODE: Tax Code").
        setReadOnly();

        createSuppblk.addField("CRESUPP_VAT_CODE_DESC").
        setSize(25).
        setMaxLength(100).
        setLabel("PCMWWORKORDERREQUISHEADERRMBTAXCDDESC: Tax Code Description").
        setFunction("''").
        setReadOnly();

        f = createSuppblk.addField("CRESUPP_PRICE_INCL_TAX", "Money");
        f.setLabel("PCMWWORKORDERREQUISHEADERRMBPRICEINCLTAX: Price incl. Tax");
        f.setSize(10);
        f.setFunction("0");
        if (purchInst)
            f.setCustomValidation("../purchw/PurchaseRequisitionValidations.page","CRESUPP_PRICE_INCL_TAX,CRESUPP_TAX_REGIME,CRESUPP_TAXABLE,CRESUPP_CONTRACT,CRESUPP_PART_NO,CRESUPP_TAX_PERCENTAGE","CRESUPP_PRICE");

        createSuppblk.addField( "CRESUPP_INCL_TAX_CURR_CODE" ).
        setFunction("''").
        setReadOnly().
        setSize(5).
        setLabel("PCMWWORKORDERREQUISHEADERRMBCRESUPPCURRCODE: Currency Code ");

        createSuppblk.addField( "TEMP_NULL" ).
        setFunction("''").
        setHidden();

        createSuppblk.addField("DUMMY").
        setFunction("''").
        setHidden();

        createSupptbl = mgr.newASPTable(createSuppblk);
        createSuppset = createSuppblk.getASPRowSet();
        createSuppbar = mgr.newASPCommandBar(createSuppblk);
        createSupplay = createSuppblk.getASPBlockLayout();
        createSupplay.setDefaultLayoutMode(createSupplay.CUSTOM_LAYOUT);
        createSupplay.setEditable();
        createSupplay.setDialogColumns(1);

        createSupplay.setSimple("CRESUPP_CURR_CODE");
        createSupplay.setSimple("CRESUPP_VAT_CODE_DESC");
        createSupplay.setSimple("CRESUPP_INCL_TAX_CURR_CODE");


        headbar.removeCustomCommand("createNewSupplier");

        enableConvertGettoPost();
    }

    //Web Alignment - replacing blocks with tabs
    public void activatePartReqLinesTab()
    {
        tabs.setActiveTab(1);
    }
    public void activateNoPartReqLinesTab()
    {
        tabs.setActiveTab(2);
    }
    //

    public void processText()
    {
        ASPManager mgr = getASPManager();

        if (!"TRUE".equals(editFlag))
            itemlay1.setLayoutMode(itemlay1.EDIT_LAYOUT);

        editFlag = "TRUE";

        mgr.getASPField("ITEM1_NOTE_TEXT").setHidden();
        mgr.getASPField("STAT_GRP").setHidden();
        mgr.getASPField("ITEM1_VENDORNO").setHidden(); 
        mgr.getASPField("ITEM1_WANTEDRECEIPTDATE").setHidden();
        mgr.getASPField("ITEM1_ORIGINALQTY").setHidden();
        mgr.getASPField("FBUY_UNIT_PRICE").setHidden();
        mgr.getASPField("DISCOUNT").setHidden();
        mgr.getASPField("ITEM1_BUYUNITMEAS").setHidden();
        mgr.getASPField("ITEM1_CATALOG_CONTRACT").setHidden();
        mgr.getASPField("ITEM1_CATALOG_NO").setHidden();
        mgr.getASPField("ITEM1_PRICE_LIST_NO").setHidden();
        mgr.getASPField("ITEM1_LINE_NO").setHidden();
        mgr.getASPField("ITEM1_RELEASE_NO").setHidden();
        mgr.getASPField("ITEM1_LATESTORDERDATE").setHidden();
        mgr.getASPField("ITEM1_STATUSCODE").setHidden();
        mgr.getASPField("ITEM1_PLANNERBUYER").setHidden();
        mgr.getASPField("ITEM1_COST_RETURN").setHidden();
        mgr.getASPField("ITEM1_ORDERNO").setHidden();
        mgr.getASPField("ITEM1_ASSGLINENO").setHidden();
        mgr.getASPField("ITEM1_ASSGRELEASENO").setHidden();
        mgr.getASPField("ITEM1_LINEORDERSTATUS").setHidden();
        mgr.getASPField("ITEM1_LISTPRICE").setHidden();
        mgr.getASPField("ITEM1_SALESPRICEAMOUNT").setHidden();
        // 031107  ARWILK  Begin  (Bug#110151)
        mgr.getASPField("ASSORTMENT").setHidden();
        mgr.getASPField("ITEM1_VENDORNAME").setHidden();
        mgr.getASPField("ITEM1_VENDORCONTACT").setHidden();
        mgr.getASPField("PRICE_TYPE_DB").setHidden();
        mgr.getASPField("ITEM1_BUYERCODE").setHidden();
        mgr.getASPField("ITEM1_TECHCOORDID").setHidden();
        mgr.getASPField("ITEM1_REQUEST_TYPE").setHidden();
        mgr.getASPField("ITEM1_DOCTEXTEXIST").setHidden();
        mgr.getASPField("ITEM1_PREPOST_EXIST").setHidden();
        // 031107  ARWILK  End  (Bug#110151)
    }

    public void unset()
    {
        ASPManager mgr = getASPManager();

        //mgr.getASPField("ITEM1_CONTRACT").unsetHidden();
        mgr.getASPField("ITEM1_NOTE_TEXT").unsetHidden();
        mgr.getASPField("STAT_GRP").unsetHidden();
        mgr.getASPField("ITEM1_VENDORNO").unsetHidden(); 
        mgr.getASPField("ITEM1_WANTEDRECEIPTDATE").unsetHidden();
        mgr.getASPField("ITEM1_ORIGINALQTY").unsetHidden();
        mgr.getASPField("FBUY_UNIT_PRICE").unsetHidden();
        mgr.getASPField("DISCOUNT").unsetHidden();
        mgr.getASPField("ITEM1_BUYUNITMEAS").unsetHidden();
        mgr.getASPField("ITEM1_CATALOG_CONTRACT").unsetHidden();
        mgr.getASPField("ITEM1_CATALOG_NO").unsetHidden();
        mgr.getASPField("ITEM1_PRICE_LIST_NO").unsetHidden();
        mgr.getASPField("ITEM1_LINE_NO").unsetHidden();
        mgr.getASPField("ITEM1_RELEASE_NO").unsetHidden();
        mgr.getASPField("ITEM1_LATESTORDERDATE").unsetHidden();
        mgr.getASPField("ITEM1_STATUSCODE").unsetHidden();
        mgr.getASPField("ITEM1_PLANNERBUYER").unsetHidden();
        mgr.getASPField("ITEM1_COST_RETURN").unsetHidden();
        mgr.getASPField("ITEM1_ORDERNO").unsetHidden();
        mgr.getASPField("ITEM1_ASSGLINENO").unsetHidden();
        mgr.getASPField("ITEM1_ASSGRELEASENO").unsetHidden();
        mgr.getASPField("ITEM1_LINEORDERSTATUS").unsetHidden();
        mgr.getASPField("ITEM1_LISTPRICE").unsetHidden();
        mgr.getASPField("ITEM1_SALESPRICEAMOUNT").unsetHidden();
        // 031107  ARWILK  Begin  (Bug#110151)
        mgr.getASPField("ASSORTMENT").unsetHidden();
        mgr.getASPField("ITEM1_VENDORNAME").unsetHidden();
        mgr.getASPField("ITEM1_VENDORCONTACT").unsetHidden();
        mgr.getASPField("PRICE_TYPE_DB").unsetHidden();
        mgr.getASPField("ITEM1_BUYERCODE").unsetHidden();
        mgr.getASPField("ITEM1_TECHCOORDID").unsetHidden();
        mgr.getASPField("ITEM1_REQUEST_TYPE").unsetHidden();
        mgr.getASPField("ITEM1_DOCTEXTEXIST").unsetHidden();
        mgr.getASPField("ITEM1_PREPOST_EXIST").unsetHidden();
        // 031107  ARWILK  End  (Bug#110151)
    }

    public void checkObjAvaileble()
    {
        if (!again)
        {
            ASPManager mgr = getASPManager();

            ASPBuffer availObj;
            trans.clear();

            trans.addSecurityQuery("Work_Order_Requis_Header_API","Freeze__,Activate__");
            trans.addSecurityQuery("Work_Order_Requis_Line_API","Freeze__,Activate__");
            trans.addSecurityQuery("Pre_Accounting_API","Get_Allowed_Codeparts");
            trans.addSecurityQuery("Work_Order_Requis_Line_API","Requisition_To_Request");
            trans.addSecurityQuery("Purchase_Order_API","Requisition_Line_To_Order");
            trans.addSecurityQuery("PURCHASE_PART_SUPPLIER,PURCHASE_ORDER");
            trans.addPresentationObjectQuery("mpccow/PreAccountingDlg.page,purchw/PurchasePartSupplier.page,purchw/PurchaseOrder.page");

            trans = mgr.perform(trans);
            availObj = trans.getSecurityInfo();

            if (availObj.itemExists("Work_Order_Requis_Header_API.Freeze__"))
                actEna0 = true;

            if (availObj.itemExists("Work_Order_Requis_Header_API.Activate__"))
                actEna1 = true;

            if (availObj.itemExists("Work_Order_Requis_Line_API.Freeze__"))
                actEna2 = true;

            if (availObj.itemExists("Work_Order_Requis_Line_API.Activate__"))
                actEna3 = true;

            if (availObj.itemExists("Pre_Accounting_API.Get_Allowed_Codeparts") && availObj.namedItemExists("mpccow/PreAccountingDlg.page"))
                actEna4 = true;

            if (availObj.itemExists("PURCHASE_PART_SUPPLIER") &&  availObj.namedItemExists("purchw/PurchasePartSupplier.page"))
                actEna5 = true;

            if (availObj.itemExists("Purchase_Order_API.Requisition_Line_To_Order"))
                actEna6 = true;

            if (availObj.itemExists("PURCHASE_ORDER") && availObj.namedItemExists("purchw/PurchaseOrder.page"))
                actEna7 = true;

            if (availObj.itemExists("Work_Order_Requis_Line_API.Requisition_To_Request"))
                actEna8 = true;

            again = true;
        }
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        headbar.removeCustomCommand("activatePartReqLinesTab");
        headbar.removeCustomCommand("activateNoPartReqLinesTab");

        if (!actEna0)
            headbar.removeCustomCommand("planRequisition");

        if (!actEna1)
            headbar.removeCustomCommand("releaseRequisition");

        if (!actEna2)
        {
            itembar0.removeCustomCommand("planRequisitionLine0");
            itembar1.removeCustomCommand("planRequisitionLine1");
        }

        if (!actEna3)
        {
            itembar0.removeCustomCommand("releaseRequisitionLine0");
            itembar1.removeCustomCommand("releaseRequisitionLine1");
        }

        if (!actEna4)
        {
            headbar.removeCustomCommand("prePost");
            itembar0.removeCustomCommand("prePost0");
            itembar1.removeCustomCommand("prePost1");
        }

        if (!actEna5)
            itembar0.removeCustomCommand("supplierPerPart");

        if (!actEna6)
        {
            itembar0.removeCustomCommand("requisitionToOrderMain0");
            itembar1.removeCustomCommand("requisitionToOrderMain1");
        }

        if (!actEna8)
        {
            itembar0.removeCustomCommand("requisitionToRequestMain0");
            itembar1.removeCustomCommand("requisitionToRequestMain1");
        }

        if (itemset0.countRows()>0)
        {
            if (reportCust)
            {
                ASPBuffer item0 = itemset0.getRow();
                item0.setValue("CUSTOMER_NO",cus_no);
                item0.setValue("AGREEMENT_ID",agree_id);
                itemset0.setRow(item0);
            }
        }

        if (itemset1.countRows()>0)
        {
            if (reportCust)
            {
                ASPBuffer item1 = itemset1.getRow();
                item1.setValue("ITEM1_CUSTOMER_NO",cus_no);
                item1.setValue("ITEM1_AGREEMENT_ID",agree_id);
                itemset1.setRow(item1);  
            }
        }

        if (calling_url.indexOf("HistoricalRound.page") > -1)
        {
            headbar.disableCommand(headbar.EDITROW);
            headbar.disableCommand(headbar.DELETE);
            headbar.disableCommand(headbar.DUPLICATEROW);
            headbar.disableCommand(headbar.NEWROW);        
            itembar0.disableCommand(itembar0.EDITROW);
            itembar0.disableCommand(itembar0.DELETE);
            itembar0.disableCommand(itembar0.DUPLICATEROW);
            itembar0.disableCommand(itembar0.NEWROW); 
            itembar1.disableCommand(itembar1.EDITROW);
            itembar1.disableCommand(itembar1.DELETE);
            itembar1.disableCommand(itembar1.DUPLICATEROW);
            itembar1.disableCommand(itembar1.NEWROW);      
        }

        if (headset.countRows() == 0)
        {
            headbar.removeCustomCommand("planRequisition");
            headbar.removeCustomCommand("releaseRequisition");
        }

        if (itemset0.countRows() == 0)
        {
            itembar0.removeCustomCommand("planRequisitionLine0");
            itembar0.removeCustomCommand("releaseRequisitionLine0");
            itembar0.removeCustomCommand("requisitionToRequestMain0");
            itembar0.removeCustomCommand("requisitionToOrderMain0");

            if (mgr.isPresentationObjectInstalled("mpccow/DocumentTextDlg.page"))
            {
                itembar0.removeCustomCommand("documentText0");
            }
        }

        if (itemset1.countRows() == 0)
        {
            itembar1.removeCustomCommand("planRequisitionLine1");
            itembar1.removeCustomCommand("releaseRequisitionLine1");
            itembar1.removeCustomCommand("requisitionToRequestMain1");
            itembar1.removeCustomCommand("requisitionToOrderMain1");

            if (mgr.isPresentationObjectInstalled("mpccow/DocumentTextDlg.page"))
            {
                itembar1.removeCustomCommand("documentText1");
            }

            itembar1.removeCustomCommand("processText");
        }
	if ((itemset1.countRows() == 0) && (itemset0.countRows() == 0)) 
	{
	    headbar.removeCustomCommand("releaseRequisition");
	}

        if ("TRUE".equals(editFlag))
            processText();
        else
            unset();

        if (itemlay0.isVisible())
        {
            if (itemlay0.isNewLayout() || itemlay0.isEditLayout())
            {
                String sItemOrderCode = itemset0.getRow().getFieldValue("ITEM0_ORDER_CODE");

                if ("6".equals(sItemOrderCode))
                {
                    mgr.getASPField("PART_OWNERSHIP").unsetReadOnly();
                    mgr.getASPField("OWNING_CUSTOMER_NO").unsetReadOnly();
                    mgr.getASPField("SERIAL_NO").unsetReadOnly();
                    mgr.getASPField("LOT_BATCH_NO").unsetReadOnly();
                    mgr.getASPField("SERVICE_TYPE").unsetReadOnly();
                }
                else
                {
                    mgr.getASPField("PART_OWNERSHIP").unsetInsertable();
                    mgr.getASPField("PART_OWNERSHIP").setReadOnly();
                    mgr.getASPField("OWNING_CUSTOMER_NO").unsetInsertable();
                    mgr.getASPField("OWNING_CUSTOMER_NO").setReadOnly();
                    mgr.getASPField("SERIAL_NO").unsetInsertable();
                    mgr.getASPField("SERIAL_NO").setReadOnly();
                    mgr.getASPField("LOT_BATCH_NO").unsetInsertable();
                    mgr.getASPField("LOT_BATCH_NO").setReadOnly();
                    mgr.getASPField("SERVICE_TYPE").unsetInsertable();
                    mgr.getASPField("SERVICE_TYPE").setReadOnly();
                }
            }
        }

        if (headlay.isVisible() && headlay.isSingleLayout() && headset.countRows()>0)
        {
            String sOrderCode = headset.getValue("ORDER_CODE");
            if ("6".equals(sOrderCode))
            {
                tabs.setTabEnabled(2,false); 
                activatePartReqLinesTab();
            }
        }
         headbar.removeCustomCommand("refreshForm");

        if ( itemlay0.isNewLayout() || itemlay0.isEditLayout() || itemlay1.isNewLayout() || itemlay1.isEditLayout() )
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


//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWWORKORDERREQUISHEADERRMBTITLE: Purchase Requisitions";
    }

    protected String getTitle()
    {
        if (reqtoreq)
            return "PCMWWORKORDERREQUISHEADERRMBTITLEREQ: Requisition to Request parameters";
        else if (reqtoord)
            return "PCMWWORKORDERREQUISHEADERRMBTITLEORD: Requisition to Order parameters";
        else if (createSupp)
            return "PCMWWORKORDERREQUISHEADERRMBCRESUPFORPART: Create Supplier for Purchase Part ? ";
        else
            return "PCMWWORKORDERREQUISHEADERRMBTITLE: Purchase Requisitions";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        printHiddenField("BUTTONVAL","");
        printHiddenField("BUTTONVALCD","");
        printHiddenField("BUTTONVALSNNOTINHAND","");

        if ( createSupp)
        {
            appendToHTML(createSupplay.show());
            appendToHTML(fmt.drawSubmit("CRESUPPOK",mgr.translate("PCMWWORKORDERREQUISHEADERRMBOKBUTTON:    OK    "),""));
            appendToHTML("&nbsp;\n");
            appendToHTML(fmt.drawSubmit("CRESUPPCANCEL",mgr.translate("PCMWWORKORDERREQUISHEADERRMBCANCREQCER:    Cancel    "),""));
        }
        else if (reqtoreq)
        {
            appendToHTML(requestlay.show());
            appendToHTML(reqlinelay.show());

            appendToHTML(fmt.drawSubmit("CREATENEWREQ",mgr.translate("PCMWWORKORDERREQUISHEADERRMBCRTNEWRQ: Create New"),""));
            appendToHTML("&nbsp;\n");

            if (reqlineset.countRows() > 0)
                appendToHTML(fmt.drawSubmit("ADDEXISTING",mgr.translate("PCMWWORKORDERREQUISHEADERRMBADDEXST: Add Existing"),""));

            appendToHTML("&nbsp;");
            appendToHTML(fmt.drawSubmit("CANCELREQ",mgr.translate("PCMWWORKORDERREQUISHEADERRMBCANCREQ:    Cancel    "),""));
        }
        else if (reqtoord)
        {
            appendToHTML("  <table>\n");
            appendToHTML("   <tr><td>");
            appendToHTML(orderbar.showBar());
            appendToHTML("</td></tr>\n");
            appendToHTML("    <tr>\n");
            appendToHTML("      <td>");
            appendToHTML(fmt.drawWriteLabel("PCMWWORKORDERREQUISHEADERRMBPARAMETERS: Parameters"));
            appendToHTML("      <table class=\"BlockLayoutTable\">\n");
            appendToHTML("        <tr>\n");
            appendToHTML("          <td>\n");
            appendToHTML("          <table class=\"BlockLayoutTable\">\n");
            appendToHTML("            <tr>\n");
            appendToHTML("              <td>");
            appendToHTML(fmt.drawWriteLabel("PCMWWORKORDERREQUISHEADERRMBCOORDIN: Coordinator  "));
            appendToHTML("</td>\n");
            appendToHTML("              <td>");
            appendToHTML(fmt.drawSelect("DEFAUTH", defauthDataBuffer, orderset.getValue("DEFAUTH"), "onChange=validateDefauth(-1)", true));
            appendToHTML("</td>\n");
            appendToHTML("              <td>");
            appendToHTML(fmt.drawReadOnlyTextField("COOR_NAME", orderset.getValue("COOR_NAME"), ""));
            appendToHTML("</td>\n");
            appendToHTML("            </tr>\n");
            appendToHTML("            <tr>\n");
            appendToHTML("              <td>");
            appendToHTML(fmt.drawWriteLabel("PCMWWORKORDERREQUISHEADERRMBBUYCODE: Buyer Code "));
            appendToHTML("</td>\n");
            appendToHTML("              <td>");

            if (mgr.getASPField("DEFBUYER").isReadOnly())
                appendToHTML(fmt.drawReadOnlyTextField("DEFBUYER", (mgr.isEmpty(orderset.getValue("DEFBUYER"))?"":orderset.getValue("DEFBUYER")), ""));
            else
                appendToHTML(fmt.drawSelect("DEFBUYER", defbuyDataBuffer, orderset.getValue("DEFBUYER"), "onChange=validateDefbuyer(-1)", true));

            appendToHTML("              </td>\n");
            appendToHTML("              <td>");
            appendToHTML(fmt.drawReadOnlyTextField("BUYER_NAME", orderset.getValue("BUYER_NAME"), ""));
            appendToHTML("</td>\n");
            appendToHTML("            </tr>\n");
            appendToHTML("          </table>\n");
            appendToHTML("          <table class=\"BlockLayoutTable\">\n");
            appendToHTML("            <tr>\n");
            appendToHTML("              <td>");
            appendToHTML(fmt.drawCheckbox("CBDEFBUYER", "ON", isChecked, "onClick=setOtherFields()"));
            appendToHTML("</td>\n");
            appendToHTML("              <td>");
            appendToHTML(fmt.drawWriteLabel("PCMWWORKORDERREQUISHEADERRMBUSEDEFBUY:  Use Default Buyer Code From Supplier "));
            appendToHTML("</td>\n");
            appendToHTML("            </tr>\n");
            appendToHTML("          </table>\n");
            appendToHTML("          </td>\n");
            appendToHTML("          <td></td>\n");
            appendToHTML("        </tr>\n");
            appendToHTML("      </table>\n");
            appendToHTML("      <table>\n");
            appendToHTML("        <tr>\n");
            appendToHTML("          <td>\n");
            appendToHTML("         <table class=\"BlockLayoutTable\">\n");
            appendToHTML("            <tr>\n");
            appendToHTML("              <td>");
            appendToHTML(fmt.drawWriteLabel("PCMWWORKORDERREQUISHEADERRMBPURCHORDNO: Purchase Order No"));
            appendToHTML("</td>\n");
            appendToHTML("              <td>");
            appendToHTML(fmt.drawTextField("ADDTO_ORDER", "", "OnChange=toUpper_('ADDTO_ORDER',-1)", 5));
            appendToHTML("</td>\n");
            appendToHTML("            </tr>\n");
            appendToHTML("          </table>\n");
            appendToHTML("          <table>\n");
            appendToHTML("            <tr>\n");
            appendToHTML("              <td>");
            appendToHTML(linelay.show());
            appendToHTML("</td>\n");
            appendToHTML("            </tr>\n");
            appendToHTML("          </table>\n");
            appendToHTML("          <table>\n");
            appendToHTML("            <tr>\n");
            appendToHTML("              <td><span>");
            appendToHTML(fmt.drawSubmit("AUTOSELECT", mgr.translate("PCMWWORKORDERREQUISHEADERRMBAUTSELBUTTON: Auto Select"), ""));
            appendToHTML(" </span>");

            if (lineset.countRows()>0)
            {
                appendToHTML(" <span>");
                appendToHTML(fmt.drawSubmit("ADDTOORDER", mgr.translate("PCMWWORKORDERREQUISHEADERRMBADDTOORDBUT: Add To Order"), ""));
                appendToHTML("</span>");
            }

            appendToHTML(" <span>");
            appendToHTML(fmt.drawSubmit("CREATENEW", mgr.translate("PCMWWORKORDERREQUISHEADERRMBCRENEWBUTTON: Create New"), ""));
            appendToHTML("</span> <span>");
            appendToHTML(fmt.drawSubmit("CANCEL", mgr.translate("PCMWWORKORDERREQUISHEADERRMBCANCREQ:    Cancel    "), ""));
            appendToHTML("</span></td>\n");
            appendToHTML("            </tr>\n");
            appendToHTML("          </table>\n");
            appendToHTML("          </td>\n");
            appendToHTML("        </tr>\n");
            appendToHTML("      </table>\n");
            appendToHTML("      </td>\n");
            appendToHTML("    </tr>\n");
            appendToHTML("  </table>\n");
        }
        else
        {
            appendToHTML(upperlay.show());
            appendToHTML(headlay.show());

            if (headlay.isSingleLayout() && headset.countRows() > 0)
                appendToHTML(tabs.showTabsInit());

            if (itemlay0.isVisible() && tabs.getActiveTab() == 1)
                appendToHTML(itemlay0.show());

            if (itemlay1.isVisible() && tabs.getActiveTab() == 2)
                appendToHTML(itemlay1.show());
        }

        appendDirtyJavaScript("window.name = \"WorkOrderRequisHeaderRMB\";\n"); 

        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(withoutConditionCode);
        appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"Condition_Code\")==\"TRUE\")\n");
        appendDirtyJavaScript("{ \n");
        appendDirtyJavaScript("     alert(\"");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWWORKORDERREQUISHEDRMBSNCDR: Condition code functionality is enabled for this part. Condition code must be specified."));
        appendDirtyJavaScript("\")  \n");
        appendDirtyJavaScript("	    document.form.BUTTONVALCD.value = \"WITHOUTCDOK\";\n");
        appendDirtyJavaScript("	    writeCookie(\"Condition_Code\", \"FALSE\", '', COOKIE_PATH); \n");
        appendDirtyJavaScript("	    if( document.form.EXTERNAL_SERVICE.value != \"TRUE\" )\n");
        appendDirtyJavaScript("     { \n");
        appendDirtyJavaScript("	         f.submit();\n");
        appendDirtyJavaScript("     } \n");        
        appendDirtyJavaScript("     \n");
        appendDirtyJavaScript("} \n");   
        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(withoutSn);
        appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"Serial_Part_Sn\")==\"TRUE\")\n");
        appendDirtyJavaScript("{ \n");
        appendDirtyJavaScript("      if (confirm(\"");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWWORKORDERREQUISHEDRMBSPCD: Serial No has to be registered before reservation of customer order. Still want to save the record?"));
        appendDirtyJavaScript("\")) {\n");
        appendDirtyJavaScript("		document.form.BUTTONVAL.value = \"WITHOUTSNOK\";\n");
        appendDirtyJavaScript("		writeCookie(\"Serial_Part_Sn\", \"FALSE\", '', COOKIE_PATH); \n");
        appendDirtyJavaScript("		f.submit();\n");
        appendDirtyJavaScript("     } \n");
        appendDirtyJavaScript("} \n");
        appendDirtyJavaScript("else if ('");
        appendDirtyJavaScript(withoutSnInHand);
        appendDirtyJavaScript("' == \"TRUE\" && readCookie(\"SN_Not_In_Hand\")==\"TRUE\")\n");
        appendDirtyJavaScript("{ \n");
        appendDirtyJavaScript("      if (confirm(\"");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWWORKORDERREQUISHEDRMBSNNINH: Serial No entered does not exists in serial catalog. Still want to save the record?"));
        appendDirtyJavaScript("\")) {\n");
        appendDirtyJavaScript("		document.form.BUTTONVALSNNOTINHAND.value = \"WITHOUTSNINHANDOK\";\n");
        appendDirtyJavaScript("		writeCookie(\"Serial_Part_Sn\", \"FALSE\", '', COOKIE_PATH); \n");
        appendDirtyJavaScript("		f.submit();\n");
        appendDirtyJavaScript("     } \n");
        appendDirtyJavaScript("} \n");        

        appendDirtyJavaScript("function validatePartNo(i)\n"); 
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("   setDirty();\n");
        appendDirtyJavaScript("   if( !checkPartNo(i) ) return;\n");
        appendDirtyJavaScript("   window.status='Please wait for validation';\n");
        appendDirtyJavaScript("   r = __connect(\n");
        appendDirtyJavaScript("           '");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=PART_NO'\n");
        appendDirtyJavaScript("           + '&ITEM0_CONTRACT=' + URLClientEncode(getValue_('ITEM0_CONTRACT',i))\n");
        appendDirtyJavaScript("           + '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
        appendDirtyJavaScript("         + '&VENDOR_NO=' + URLClientEncode(getValue_('VENDOR_NO',i))\n");
        appendDirtyJavaScript("         + '&ORDER_CODE=' + URLClientEncode(getValue_('ORDER_CODE',i))\n");
        appendDirtyJavaScript("         + '&BUYER_CODE=' + URLClientEncode(getValue_('BUYER_CODE',i))\n");
        appendDirtyJavaScript("         + '&ITEM0_ORDER_CODE=' + URLClientEncode(getValue_('ITEM0_ORDER_CODE',i))\n");
        appendDirtyJavaScript("           );\n");
        appendDirtyJavaScript("   window.status='';\n");
        appendDirtyJavaScript("   if( checkStatus_(r,'PART_NO',i,'Part No') )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("           assignValue_('DESCRIPTION',i,0);\n");
        appendDirtyJavaScript("           assignValue_('VENDOR_NO',i,1);\n");
        appendDirtyJavaScript("           assignValue_('UNIT_MEAS',i,2);\n");
        appendDirtyJavaScript("           assignValue_('CATALOG_NO',i,3);\n");
        appendDirtyJavaScript("           assignValue_('CONTACT',i,4);\n");
        appendDirtyJavaScript("           assignValue_('PROCESS_TYPE',i,5);\n");
        appendDirtyJavaScript("           assignValue_('ORDERPROCDESC',i,6);\n");
        appendDirtyJavaScript("           assignValue_('VENDORNAME',i,7);\n");
        appendDirtyJavaScript("           assignValue_('TECHNICAL_COORDINATOR_ID',i,8);\n");
        appendDirtyJavaScript("           assignValue_('BUYER_CODE',i,9);\n");
        appendDirtyJavaScript("           assignValue_('PART_OWNERSHIP',i,10);\n");
        appendDirtyJavaScript("           assignValue_('OWNING_CUSTOMER_NO',i,11);\n");
        appendDirtyJavaScript("           assignValue_('CONDITION_CODE',i,12);\n");
        appendDirtyJavaScript("           assignValue_('CONDITIONCODEDESC',i,13);\n");
        appendDirtyJavaScript("           assignValue_('ACTIVEIND_DB',i,14);\n");
        appendDirtyJavaScript("   }\n");
        //Bug 57598, start
        appendDirtyJavaScript("   if (f.ACTIVEIND_DB.value == 'N') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWWORKORDERREQUISHEADERRMBINVSALESPART: All sale parts connected to the part are inactive."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.CATALOG_NO.value = ''; \n");
        appendDirtyJavaScript("   } \n");
        //Bug 57598, end
        appendDirtyJavaScript("         validateCatalogNo(i)\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function toggleStyleDisplay(el)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   el = document.getElementById(el);\n");
        appendDirtyJavaScript("   if(el.style.display!='none')\n");
        appendDirtyJavaScript("   {  \n");
        appendDirtyJavaScript("      el.style.display='none';\n");
        appendDirtyJavaScript("      tempTBL.style.display='none';\n");
        appendDirtyJavaScript("   }  \n");
        appendDirtyJavaScript("   else\n");
        appendDirtyJavaScript("   {  \n");
        appendDirtyJavaScript("      el.style.display='block';\n");
        appendDirtyJavaScript("      tempTBL.style.display='block';\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovPartNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("    if( getRowStatus_('ITEM0',i)=='QueryMode__')\n");
        appendDirtyJavaScript("      openLOVWindow('PART_NO',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PURCHASE_PART_INVENT_ATTR&__FIELD=");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWWORKORDERREQUISHEADERRMBPARTNOFLD: Part+No"));
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           ,600,445,'validatePartNo');\n");
        appendDirtyJavaScript("    else\n");
        appendDirtyJavaScript("      openLOVWindow('PART_NO',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PURCHASE_PART_INVENT_ATTR&__FIELD=");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWWORKORDERREQUISHEADERRMBPARTNOFLD: Part+No"));
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
        appendDirtyJavaScript("           ,600,445,'validatePartNo');\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovVendorno(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("    if( getRowStatus_('ITEM0',i)=='QueryMode__')\n");
        appendDirtyJavaScript("      openLOVWindow('VENDOR_NO',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PURCHASE_PART_SUPPLIER_LOV&__FIELD=");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWWORKORDERREQUISHEADERRMBSUPPLIERFLD: Supplier"));
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
        appendDirtyJavaScript("           ,600,445,'validateVendorno');\n");
        appendDirtyJavaScript("    else\n");
        appendDirtyJavaScript("      openLOVWindow('VENDOR_NO',i,\n");
        appendDirtyJavaScript("           '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PURCHASE_PART_SUPPLIER_LOV&__FIELD=");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWWORKORDERREQUISHEADERRMBSUPPLIERFLD: Supplier"));
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
        appendDirtyJavaScript("           + '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
        appendDirtyJavaScript("           ,600,445,'validateVendorno');\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateOriginalqty(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("   setDirty();\n");
        appendDirtyJavaScript("   if( !checkOriginalqty(i) ) return;\n");
        appendDirtyJavaScript("   window.status='Please wait for validation';\n");
        appendDirtyJavaScript("   r = __connect(\n");
        appendDirtyJavaScript("           '");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ORIGINAL_QTY'\n");
        appendDirtyJavaScript("           + '&CATALOG_CONTRACT=' + URLClientEncode(getValue_('CATALOG_CONTRACT',i))\n");
        appendDirtyJavaScript("           + '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
        appendDirtyJavaScript("           + '&ORIGINAL_QTY=' + URLClientEncode(getValue_('ORIGINAL_QTY',i))\n");
        appendDirtyJavaScript("           + '&ITEM0_REQUISITION_NO=' + URLClientEncode(getValue_('ITEM0_REQUISITION_NO',i))\n");
        appendDirtyJavaScript("           + '&AGREEMENT_ID=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(agree_id));		//XSS_Safe AMNILK 20070726
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&CUSTOMER_NO=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(cus_no));		//XSS_Safe AMNILK 20070726
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&PRICE_LIST_NO=' + URLClientEncode(getValue_('PRICE_LIST_NO',i))\n");
        appendDirtyJavaScript("           );\n");
        appendDirtyJavaScript("   window.status='';\n");
        appendDirtyJavaScript("   if( checkStatus_(r,'ORIGINAL_QTY',i,'Original Quantity') )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("           assignValue_('PRICE_LIST_NO',i,0);\n");
        appendDirtyJavaScript("           assignValue_('ITEM0_DISCOUNT',i,1);\n");
        appendDirtyJavaScript("           assignValue_('ITEM0_LISTPRICE',i,2);\n");
        appendDirtyJavaScript("           assignValue_('ITEM0_SALESPRICEAMOUNT',i,3);\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateCatalogNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("   setDirty();\n");
        appendDirtyJavaScript("   if( !checkCatalogNo(i) ) return;\n");
        appendDirtyJavaScript("   if( getValue_('CATALOG_NO',i)=='' )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("           getField_('CATALOGDESC',i).value = '';\n");
        appendDirtyJavaScript("           getField_('ITEM0_LISTPRICE',i).value = '';\n");
        appendDirtyJavaScript("           getField_('ITEM0_SALESPRICEAMOUNT',i).value = '';\n");
        appendDirtyJavaScript("           getField_('PRICE_LIST_NO',i).value = '';\n");
        appendDirtyJavaScript("           getField_('ITEM0_DISCOUNT',i).value = '';\n");
        appendDirtyJavaScript("           return;\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("   window.status='Please wait for validation';\n");
        appendDirtyJavaScript("   r = __connect(\n");
        appendDirtyJavaScript("           '");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=CATALOG_NO'\n");
        appendDirtyJavaScript("           + '&CATALOG_CONTRACT=' + URLClientEncode(getValue_('CATALOG_CONTRACT',i))\n");
        appendDirtyJavaScript("           + '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
        appendDirtyJavaScript("           + '&ORIGINAL_QTY=' + URLClientEncode(getValue_('ORIGINAL_QTY',i))\n");
        appendDirtyJavaScript("           + '&ITEM0_REQUISITION_NO=' + URLClientEncode(getValue_('ITEM0_REQUISITION_NO',i))\n");
        appendDirtyJavaScript("           + '&LINE_NO=' + URLClientEncode(getValue_('LINE_NO',i))\n");
        appendDirtyJavaScript("           + '&RELEASE_NO=' + URLClientEncode(getValue_('RELEASE_NO',i))\n");
        appendDirtyJavaScript("           + '&AGREEMENT_ID=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(agree_id));		//XSS_Safe AMNILK 20070726
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&CUSTOMER_NO=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(cus_no));		//XSS_Safe AMNILK 20070726
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&PRICE_LIST_NO=' + URLClientEncode(getValue_('PRICE_LIST_NO',i))\n");
        appendDirtyJavaScript("  + '&HEAD_CURRENCEY_CODE=' + URLClientEncode(getValue_('HEAD_CURRENCEY_CODE',i))\n");
        appendDirtyJavaScript("           );\n");
        appendDirtyJavaScript("   window.status='';\n");
        appendDirtyJavaScript("   if( checkStatus_(r,'CATALOG_NO',i,'Sales Part Number') )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("           assignValue_('CATALOGDESC',i,0);\n");
        appendDirtyJavaScript("           assignValue_('ITEM0_LISTPRICE',i,1);\n");
        appendDirtyJavaScript("           assignValue_('ITEM0_SALESPRICEAMOUNT',i,2);\n");
        appendDirtyJavaScript("           assignValue_('PRICE_LIST_NO',i,3);\n");
        appendDirtyJavaScript("           assignValue_('ITEM0_DISCOUNT',i,4);\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateVendorno(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkVendorno(i) ) return; \n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("   r = __connect(\n");
        appendDirtyJavaScript("           '");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=VENDOR_NO'\n");
        appendDirtyJavaScript("		+ '&VENDOR_NO=' + URLClientEncode(getValue_('VENDOR_NO',i))  \n");
        appendDirtyJavaScript("		+ '&ITEM0_CONTRACT=' + URLClientEncode(getValue_('ITEM0_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i)) \n");
        appendDirtyJavaScript("		+ '&BUYER_CODE=' + URLClientEncode(getValue_('BUYER_CODE',i))\n");
        appendDirtyJavaScript("		); \n");
        appendDirtyJavaScript(" 	window.status=''; \n");
        appendDirtyJavaScript("   if(r.indexOf(\"NO_SUPPLIER_EXIST\") == -1)  // Supplier exists\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("	    if( checkStatus_(r,'VENDOR_NO',i,'Supplier') )\n");
        appendDirtyJavaScript("	    { \n");
        appendDirtyJavaScript("		assignValue_('VENDORNAME',i,0);\n");
        appendDirtyJavaScript("		assignValue_('UNIT_MEAS',i,1); \n");
        appendDirtyJavaScript("		assignValue_('CONTACT',i,2);\n");
        appendDirtyJavaScript("		assignValue_('BUYER_CODE',i,3); \n");
        appendDirtyJavaScript("	    }\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("   else\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("      // Popup the window to create the supplier for the part.\n");
        appendDirtyJavaScript("      commandSet('HEAD.createNewSupplier','');   \n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validatePriceListNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("   setDirty();\n");
        appendDirtyJavaScript("   if( !checkPriceListNo(i) ) return;\n");
        appendDirtyJavaScript("   if( getValue_('PRICE_LIST_NO',i)=='' )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("           getField_('PRICE_LIST_NO',i).value = '';\n");
        appendDirtyJavaScript("           getField_('ITEM0_DISCOUNT',i).value = '';\n");
        appendDirtyJavaScript("           getField_('ITEM0_LISTPRICE',i).value = '';\n");
        appendDirtyJavaScript("           getField_('ITEM0_SALESPRICEAMOUNT',i).value = '';\n");
        appendDirtyJavaScript("           return;\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("   window.status='Please wait for validation';\n");
        appendDirtyJavaScript("   r = __connect(\n");
        appendDirtyJavaScript("           '");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=PRICE_LIST_NO'\n");
        appendDirtyJavaScript("           + '&PRICE_LIST_NO=' + URLClientEncode(getValue_('PRICE_LIST_NO',i))\n");
        appendDirtyJavaScript("           + '&CATALOG_CONTRACT=' + URLClientEncode(getValue_('CATALOG_CONTRACT',i))\n");
        appendDirtyJavaScript("           + '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
        appendDirtyJavaScript("           + '&ORIGINAL_QTY=' + URLClientEncode(getValue_('ORIGINAL_QTY',i))\n");
        appendDirtyJavaScript("           + '&ITEM0_REQUISITION_NO=' + URLClientEncode(getValue_('ITEM0_REQUISITION_NO',i))\n");
        appendDirtyJavaScript("           + '&AGREEMENT_ID=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(agree_id));		//XSS_Safe AMNILK 20070726
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&CUSTOMER_NO=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(cus_no));		//XSS_Safe AMNILK 20070726
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           );\n");
        appendDirtyJavaScript("   window.status='';\n");
        appendDirtyJavaScript("   if( checkStatus_(r,'PRICE_LIST_NO',i,'Price List No') )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("           assignValue_('PRICE_LIST_NO',i,0);\n");
        appendDirtyJavaScript("           assignValue_('ITEM0_DISCOUNT',i,1);\n");
        appendDirtyJavaScript("           assignValue_('ITEM0_LISTPRICE',i,2);\n");
        appendDirtyJavaScript("           assignValue_('ITEM0_SALESPRICEAMOUNT',i,3);\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateItem1Originalqty(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("   setDirty();\n");
        appendDirtyJavaScript("   if( !checkItem1Originalqty(i) ) return;\n");
        appendDirtyJavaScript("   if( getValue_('ITEM1_ORIGINALQTY',i)=='' )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("           getField_('ITEM1_PRICE_LIST_NO',i).value = '';\n");
        appendDirtyJavaScript("           getField_('DISCOUNT',i).value = '';\n");
        appendDirtyJavaScript("           getField_('ITEM1_LISTPRICE',i).value = '';\n");
        appendDirtyJavaScript("           getField_('ITEM1_SALESPRICEAMOUNT',i).value = '';\n");
        appendDirtyJavaScript("           return;\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("   window.status='Please wait for validation';\n");
        appendDirtyJavaScript("   r = __connect(\n");
        appendDirtyJavaScript("           '");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM1_ORIGINALQTY'\n");
        appendDirtyJavaScript("           + '&ITEM1_CATALOG_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CATALOG_CONTRACT',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_CATALOG_NO=' + URLClientEncode(getValue_('ITEM1_CATALOG_NO',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_ORIGINALQTY=' + URLClientEncode(getValue_('ITEM1_ORIGINALQTY',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_PRICE_LIST_NO=' + URLClientEncode(getValue_('ITEM1_PRICE_LIST_NO',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_AGREEMENT_ID=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(agree_id));		//XSS_Safe AMNILK 20070726
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&ITEM1_CUSTOMER_NO=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(cus_no));		//XSS_Safe AMNILK 20070726
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           );\n");
        appendDirtyJavaScript("   window.status='';\n");
        appendDirtyJavaScript("   if( checkStatus_(r,'ITEM1_ORIGINALQTY',i,'Original Quantity') )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("           assignValue_('ITEM1_PRICE_LIST_NO',i,0);\n");
        appendDirtyJavaScript("           assignValue_('DISCOUNT',i,1);\n");
        appendDirtyJavaScript("           assignValue_('ITEM1_LISTPRICE',i,2);\n");
        appendDirtyJavaScript("           assignValue_('ITEM1_SALESPRICEAMOUNT',i,3);\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateItem1CatalogNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("   setDirty();\n");
        appendDirtyJavaScript("   if( !checkItem1CatalogNo(i) ) return;\n");
        appendDirtyJavaScript("   if( getValue_('ITEM1_CATALOG_NO',i)=='' )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("           getField_('ITEM1_CATALOGDESC',i).value = '';\n");
        appendDirtyJavaScript("           getField_('ITEM1_LISTPRICE',i).value = '';\n");
        appendDirtyJavaScript("           getField_('ITEM1_SALESPRICEAMOUNT',i).value = '';\n");
        appendDirtyJavaScript("           getField_('ITEM1_PRICE_LIST_NO',i).value = '';\n");
        appendDirtyJavaScript("           getField_('DISCOUNT',i).value = '';\n");
        appendDirtyJavaScript("           return;\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("   window.status='Please wait for validation';\n");
        appendDirtyJavaScript("   r = __connect(\n");
        appendDirtyJavaScript("           '");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM1_CATALOG_NO'\n");
        appendDirtyJavaScript("           + '&ITEM1_CATALOG_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CATALOG_CONTRACT',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_CATALOG_NO=' + URLClientEncode(getValue_('ITEM1_CATALOG_NO',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_ORIGINALQTY=' + URLClientEncode(getValue_('ITEM1_ORIGINALQTY',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_LINE_NO=' + URLClientEncode(getValue_('ITEM1_LINE_NO',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_RELEASE_NO=' + URLClientEncode(getValue_('ITEM1_RELEASE_NO',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_AGREEMENT_ID=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(agree_id));		//XSS_Safe AMNILK 20070726
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&ITEM1_CUSTOMER_NO=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(cus_no));		//XSS_Safe AMNILK 20070726
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&ITEM1_PRICE_LIST_NO=' + URLClientEncode(getValue_('ITEM1_PRICE_LIST_NO',i))\n");
        appendDirtyJavaScript("  + '&HEAD_CURRENCEY_CODE=' + URLClientEncode(getValue_('HEAD_CURRENCEY_CODE',i))\n");
        appendDirtyJavaScript("           );\n");
        appendDirtyJavaScript("   window.status='';\n");
        appendDirtyJavaScript("   if( checkStatus_(r,'ITEM1_CATALOG_NO',i,'Sales Part Number') )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("           assignValue_('ITEM1_CATALOGDESC',i,0);\n");
        appendDirtyJavaScript("           assignValue_('LINE_DESCRIPTION',i,1);\n");
	appendDirtyJavaScript("           assignValue_('ITEM1_LISTPRICE',i,2);\n");
        appendDirtyJavaScript("           assignValue_('ITEM1_SALESPRICEAMOUNT',i,3);\n");
        appendDirtyJavaScript("           assignValue_('ITEM1_PRICE_LIST_NO',i,4);\n");
        appendDirtyJavaScript("           assignValue_('DISCOUNT',i,5);\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateItem1PriceListNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("   setDirty();\n");
        appendDirtyJavaScript("   if( !checkItem1PriceListNo(i) ) return;\n");
        appendDirtyJavaScript("   if( getValue_('ITEM1_PRICE_LIST_NO',i)=='' )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("           getField_('ITEM1_PRICE_LIST_NO',i).value = '';\n");
        appendDirtyJavaScript("           getField_('DISCOUNT',i).value = '';\n");
        appendDirtyJavaScript("           getField_('ITEM1_LISTPRICE',i).value = '';\n");
        appendDirtyJavaScript("           getField_('ITEM1_SALESPRICEAMOUNT',i).value = '';\n");
        appendDirtyJavaScript("           return;\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("   window.status='Please wait for validation';\n");
        appendDirtyJavaScript("   r = __connect(\n");
        appendDirtyJavaScript("           '");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM1_PRICE_LIST_NO'\n");
        appendDirtyJavaScript("           + '&ITEM1_PRICE_LIST_NO=' + URLClientEncode(getValue_('ITEM1_PRICE_LIST_NO',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_CATALOG_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CATALOG_CONTRACT',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_CATALOG_NO=' + URLClientEncode(getValue_('ITEM1_CATALOG_NO',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_ORIGINALQTY=' + URLClientEncode(getValue_('ITEM1_ORIGINALQTY',i))\n");
        appendDirtyJavaScript("           + '&ITEM1_AGREEMENT_ID=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(agree_id));		//XSS_Safe AMNILK 20070726
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           + '&ITEM1_CUSTOMER_NO=");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(cus_no));		//XSS_Safe AMNILK 20070726
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("           );\n");
        appendDirtyJavaScript("   window.status='';\n");
        appendDirtyJavaScript("   if( checkStatus_(r,'ITEM1_PRICE_LIST_NO',i,'Price List No') )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("           assignValue_('ITEM1_PRICE_LIST_NO',i,0);\n");
        appendDirtyJavaScript("           assignValue_('DISCOUNT',i,1);\n");
        appendDirtyJavaScript("           assignValue_('ITEM1_LISTPRICE',i,2);\n");
        appendDirtyJavaScript("           assignValue_('ITEM1_SALESPRICEAMOUNT',i,3);\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateDefauth(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   inValue = f.DEFAUTH.options[f.DEFAUTH.selectedIndex].value;\n");
        appendDirtyJavaScript("   r = __connect(\n");
        appendDirtyJavaScript("           ");
        appendDirtyJavaScript("APP_ROOT+");
        appendDirtyJavaScript("'pcmw/WorkOrderRequisHeaderRMB.page?VALIDATE=DEFAUTH'\n");
        appendDirtyJavaScript("           + '&DEFAUTH=' + inValue\n");
        appendDirtyJavaScript("           );\n");
        appendDirtyJavaScript("   if( checkStatus_(r,'DEFAUTH',i,'DEFAUTH') )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("           assignValue_('COOR_NAME',i,0);\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateDefbuyer(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   inValue = f.DEFBUYER.options[f.DEFBUYER.selectedIndex].value;\n");
        appendDirtyJavaScript("   r = __connect(\n");
        appendDirtyJavaScript("           ");
        appendDirtyJavaScript("APP_ROOT+");
        appendDirtyJavaScript("'pcmw/WorkOrderRequisHeaderRMB.page?VALIDATE=DEFBUYER'\n");
        appendDirtyJavaScript("           + '&DEFBUYER=' + inValue\n");
        appendDirtyJavaScript("           );\n");
        appendDirtyJavaScript("   if( checkStatus_(r,'DEFBUYER',i,'DEFBUYER') )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("           assignValue_('BUYER_NAME',i,0);\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validatePartOwnership(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkPartOwnership(i) ) return;\n");
        appendDirtyJavaScript(" if( getValue_('PART_OWNERSHIP',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=PART_OWNERSHIP'\n");
        appendDirtyJavaScript("		+ '&PART_OWNERSHIP=' + SelectURLClientEncode('PART_OWNERSHIP',i)		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'PART_OWNERSHIP',i,'Ownership') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('PART_OWNERSHIP_DB',i,0);\n");
        appendDirtyJavaScript("		if (f.PART_OWNERSHIP_DB.value == 'CONSIGNMENT'){\n");
        appendDirtyJavaScript("		alert('");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWWORKORDERREQUISHEADERRMBINVOWNER1: Ownership type Consignment not allowed for Work Order Requisitions."));
        appendDirtyJavaScript("'); \n");
        appendDirtyJavaScript("      f.PART_OWNERSHIP.value = ''; \n");
        appendDirtyJavaScript("      f.PART_OWNERSHIP_DB.value = ''; \n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("		if (f.PART_OWNERSHIP_DB.value == 'SUPPLIER LOANED'){\n");
        appendDirtyJavaScript("		alert('");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWWORKORDERREQUISHEADERRMBINVOWNER2: Ownership type Supplier Loaned not allowed for Work Order Requisitions."));
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
        appendDirtyJavaScript("	  var enable_multichoice =(true && ITEM0_IN_FIND_MODE);\n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'CUSTOMER OWNED') \n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("	  var key_value = (getValue_('OWNING_CUSTOMER_NO',i).indexOf('%') !=-1)? getValue_('OWNING_CUSTOMER_NO',i):'';\n");
        appendDirtyJavaScript("	  openLOVWindow('OWNING_CUSTOMER_NO',i,\n");
        appendDirtyJavaScript("'");
        appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CUSTOMER_INFO&__FIELD=Owner&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('OWNING_CUSTOMER_NO',i))\n");
        appendDirtyJavaScript("+ '&OWNING_CUSTOMER_NO=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript(",550,500,'validateOwner');\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateOwner(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("      setDirty();\n");
        appendDirtyJavaScript("   if( !checkOwner(i) ) return;\n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'CUSTOMER OWNED') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      if( getValue_('OWNING_CUSTOMER_NO',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("      if( getValue_('PART_OWNERSHIP_DB',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("      if( getValue_('OWNING_CUSTOMER_NO',i)=='' )\n");
        appendDirtyJavaScript("      {\n");
        appendDirtyJavaScript("         getField_('OWNER_NAME',i).value = '';\n");
        appendDirtyJavaScript("         return;\n");
        appendDirtyJavaScript("      }\n");
        appendDirtyJavaScript("      window.status='Please wait for validation';\n");
        appendDirtyJavaScript("      r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=OWNING_CUSTOMER_NO'\n");
        appendDirtyJavaScript("                    + '&OWNING_CUSTOMER_NO=' + URLClientEncode(getValue_('OWNING_CUSTOMER_NO',i))\n");
        appendDirtyJavaScript("                    + '&PART_OWNERSHIP_DB=' + URLClientEncode(getValue_('PART_OWNERSHIP_DB',i))\n");
        appendDirtyJavaScript("                   );\n");
        appendDirtyJavaScript("      window.status='';\n");
        appendDirtyJavaScript("      if( checkStatus_(r,'OWNING_CUSTOMER_NO',i,'Owner') )\n");
        appendDirtyJavaScript("      {\n");
        appendDirtyJavaScript("         assignValue_('OWNER_NAME',i,0);\n");
        appendDirtyJavaScript("      }\n");
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'COMPANY OWNED' && f.OWNING_CUSTOMER_NO.value != '') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWWORKORDERREQUISHEADERRMBINVOWNER11: Owner should not be specified for Company Owned Stock."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.OWNING_CUSTOMER_NO.value = ''; \n");
        appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'CONSIGNMENT' && f.OWNING_CUSTOMER_NO.value != '') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWWORKORDERREQUISHEADERRMBINVOWNER12: Owner should not be specified for Consignment Stock."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.OWNING_CUSTOMER_NO.value = ''; \n");
        appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'SUPPLIER LOANED' && f.OWNING_CUSTOMER_NO.value != '') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWWORKORDERREQUISHEADERRMBINVOWNER13: Owner should not be specified for Supplier Loaned Stock."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.OWNING_CUSTOMER_NO.value = ''; \n");
        appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == '' && f.OWNING_CUSTOMER_NO.value != '') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWWORKORDERREQUISHEADERRMBINVOWNER14: Owner should not be specified when there is no Ownership type."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.OWNING_CUSTOMER_NO.value = ''; \n");
        appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function checkOrderCodeValues(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if (checkOrderCode(i))\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("      orderCode = getValue_('ORDER_CODE',i);\n");
        appendDirtyJavaScript("      if (orderCode!=1 && orderCode!=5 && orderCode!=6)\n");
        appendDirtyJavaScript("      {\n");
        appendDirtyJavaScript("         alert('" + mgr.translateJavaScript("PCMWWORKORDERREQUISHEADERRMBMSSGE: Valid order codes are 1,5 and 6") + "');\n");
        appendDirtyJavaScript("         return false;\n");
        appendDirtyJavaScript("      }\n");
        appendDirtyJavaScript("      return true;\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("   return false;\n");
        appendDirtyJavaScript("}\n");

        if (bOpenNewWindow)
        {
            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));		//XSS_Safe AMNILK 20070726
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
        }
    }
}
