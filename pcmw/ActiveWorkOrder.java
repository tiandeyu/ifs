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
*  File        : ActiveWorkOrder.java 
*  Created     : CHDELK  010220
*  Modified    : 
*  BUNILK  010331  Made some corrections in ok() function. 
*  CHDELK  010424  Made the back button invisible when opend from the tree
*  CHDELK  010425  Made smone changes in the ok() function
*  BUNILK  010718  Changed header text.
*  BUNILK  010724  Modified okFindITEM0(),okFindITEM1() and countFindITEM0() methods. 
*  VAGULK  011011  Changed the fiels from Workorder  No to WO No (70534)
*  VAGULK  020829  Added function adjust() so that an empty row could not be edited(32378)
*  GACOLK  021204  Set Max Length of SERIAL_NO to 50
*  NIMHLK  030209  Added two fields Condition Code and Condition Code Description according to specification W110 - Condition Codes.
*  SAPRLK  031222  Web Alignment - removed methods clone() and doReset().
*  VAGULK  030112  Made field order according to the order in the Centura application(Web Alignment).
*  SHAFLK  040906  Bug 46542, Modified method okFindITEM1().
*  NIJALK  041007  Merged 46542.
*  NAMELK  041104  Non Standard Translation Tag Constants Corrected.
*  ARWILK  041115  Replaced getContents with printContents.
*  NIJALK  050728  Bug 126023, Modified run(),okFind2(),okFindTransfer().
*  SHAFLK  051230  Bug 54562, Added Configuration ID and done some changes in run() and okFind2(), okFindTransfer().
*  NIJALK  060118  Merged bug 54562.
*  JaJalk  060303  Made the respective changes as the LCS 54432 was only treated to centura client.
* ----------------------------------------------------------------------------
*  NIJALK  060509  Bug 57099, Modified okFind2(), run(), ok().
*  AMNILK  060629  Merged with SP1 APP7.
*  SHAFLK  061129  Bug 61446, Modified ok() and preDefine().
*  ILSOLK  070302  Merged Bug ID 61446.
*  ILSOLK  070709  Eliminated XSS.
*  AMNILK  070712  Eliminated SQL Injections.
*  ASSALK  070713  Webification - Modified printContents().
*  AMNILK  070719  Modified Double value conversions at okFind2(),okFindTransfer().
*  AMDILK  070731  Removed the scroll buttons of the parent when the detail block is in find or edit modes
*  SHAFLK  071101  Bug 68811 Modified preDefine()
* ----------------------------------------------------------------------------
* -----------------------APP7.5 SP1-------------------------------------
*  071214  ILSOLK  Bug Id 68773, Eliminated XSS.
*  080202  NIJALK  Bug 66456, Modified preDefine().
*  SHAFLK  090403  Bug 80347, Modified preDefine().
*  SHAFLK  090710  Bug 84612, Modified okFind().
*  SHAFLK  091207  Bug 87501, Modified okFindITEM0() and okFindITEM1().
* -----------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class ActiveWorkOrder extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveWorkOrder");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
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

    private ASPField f; 
    private ASPHTMLFormatter fmt;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private ASPBuffer buff;
    private ASPBuffer row;
    private String woNo;
    private double ordNo;
    private ASPTransactionBuffer secBuff;
    private String val;
    private ASPQuery q;
    private int currrow;
    private ASPCommand cmd;
    private int curentrow;
    private String sPartNo;
    private String sContract ;
    private String sWoNo;
    private String sLineItemNo;
    private String qtyLeft;
    private String partDesc;
    private String qrystr;  
    private String frmname;
    private String maintMatOrdNo;
    private String okClose;
    private String backButton;
    private String[] isSecure =  new String[7]; 
    private int ref;
    private int item0row;
    private double nOrd;
    

    //===============================================================
    // Construction 
    //===============================================================
    public ActiveWorkOrder(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        isSecure =  new String[2] ;
        ASPManager mgr = getASPManager();

        trans = mgr.newASPTransactionBuffer();
        fmt = mgr.newASPHTMLFormatter();   
        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();

        sPartNo=ctx.readValue("SPARTNO","");
        sContract=ctx.readValue("SCONTRACT","");
        sWoNo=ctx.readValue("SWONO","");
        sLineItemNo=ctx.readValue("SLINEITEMNO","");
        qtyLeft=ctx.readValue("QTYLEFT","");
        partDesc=ctx.readValue("PARTDESC",""); 
        qrystr=ctx.readValue("QRYSTR","");
        frmname=ctx.readValue("FRMNAME","");
        qrystr = ctx.readValue("QRYSTR","");
        maintMatOrdNo=ctx.readValue("MAINTMATORDNO","");
        okClose=ctx.readValue("OKCLOSE","");
        backButton=ctx.readValue("BACKBUTTON","");
        item0row=ctx.readNumber("ITEM0ROW",0);
        nOrd = ctx.readNumber("NORD",0);

        if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();
        else if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("MAINTMATORDNO")))
        {

            ordNo = mgr.readNumberValue("MAINTMATORDNO");
            nOrd =  mgr.readNumberValue("MAINTMATORDNO");
            sWoNo = mgr.readValue("WO_NO","");
            qrystr = mgr.readValue("QRYSTR","");
            frmname = mgr.readValue("FRMNAME","");
            sPartNo= mgr.readValue("PART_NO","");
            sContract= mgr.readValue("CONTRACT","");
            sLineItemNo = mgr.readValue("LINE_ITEM_NO","");
            okFind2();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
        {
            maintMatOrdNo = mgr.readValue("MAINTMATORDNO","");
            qrystr = mgr.readValue("QRYSTR","");
            frmname = mgr.readValue("FRMNAME","");
            okFind();
        }
        else if (mgr.dataTransfered())
        {
            buff = mgr.getTransferedData();
            row = buff.getBufferAt(0);
            woNo = row.getValue("WO_NO");
            okFind(); 
        }
        else if (mgr.buttonPressed("BACK"))
            back();
        else
            backButton="FALSE";

        adjust();

        ctx.writeValue("FRMNAME",frmname);
        ctx.writeValue("QRYSTR",qrystr);
        ctx.writeValue("BACKBUTTON",backButton);
        ctx.writeNumber("ITEM0ROW",item0row) ;
        ctx.writeValue("SLINEITEMNO",sLineItemNo);
    }


//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

    public boolean  checksec( String method,int ref) 
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
        ASPManager mgr = getASPManager();

        val = mgr.readValue("VALIDATE");
        mgr.showError("VALIDATE not implemented");
        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void countFind()
    {
        ASPManager mgr = getASPManager();

        headbar.disableCommand(headbar.REMOVE);
        q = trans.addQuery(headblk);
        q.setSelectList("to_char(count(*)) N");
        q.includeMeta("ALL");

        mgr.submit(trans);

        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();
    }


    public void okFind()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        q = trans.addQuery(headblk);
        q.includeMeta("ALL");


        if (mgr.dataTransfered())
            q.addOrCondition( mgr.getTransferedData() );
        mgr.submit(trans);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWACTIVEWORKORDERNODAT: No data found."));
            headset.clear();
        }
        if (headset.countRows() == 1)
        {
            okFindITEM1();
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        }
        //Bug 84612, start
        qrystr = mgr.createSearchURL(headblk);
        //Bug 84612, end
    }

    public void okFind2()
    {
        ASPManager mgr = getASPManager();
        trans.clear();

	int nOrdintVal;
	nOrdintVal = new Double(nOrd).intValue();

        ASPQuery q = trans.addEmptyQuery(headblk);
	
	// SQLInjection_Safe AMNILK 20070712

        q.addWhereCondition("MAINT_MATERIAL_ORDER_NO = ?");
	q.addParameter("MAINT_MATERIAL_ORDER_NO",new Integer(nOrdintVal).toString());

        q.includeMeta("ALL");
        mgr.submit(trans);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWACTIVEWORKORDERNODAT: No data found."));
            headset.clear();
        }
        if (headset.countRows() == 1)
        {
            okFindITEM1();
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        }


    }

    public void okFindTransfer()
    {
        ASPManager mgr = getASPManager();

	int nOrdintVal;
	nOrdintVal = new Double(nOrd).intValue();

        trans.clear();
        q = trans.addQuery(headblk);

	// SQLInjection_Safe AMNILK 20070712
        q.addWhereCondition("MAINT_MATERIAL_ORDER_NO = ? ");
	q.addParameter("MAINT_MATERIAL_ORDER_NO",new Integer(nOrdintVal).toString());

        q.includeMeta("ALL");

        mgr.submit(trans);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWACTIVEWORKORDERNODAT: No data found."));
            headset.clear();
        }

        if (headset.countRows() > 0)
            okFindITEM1();
    }

//-----------------------------------------------------------------------------
//-----------------------  CMDBAR SEARCH-IT FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

    public void countFindITEM0()
    {
        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();

        trans.clear();

        q = trans.addQuery(itemblk0);   
        q.setSelectList("to_char(count(*)) N");

	// SQLInjection_Safe AMNILK 20070712
	q.addWhereCondition("ORDER_NO = ?");
	q.addParameter("ORDER_NO",headset.getRow().getValue("WO_NO"));

        q.addWhereCondition("CONTRACT = USER_ALLOWED_SITE_API.AUTHORIZED(CONTRACT)");
	q.addWhereCondition("NVL(DIRECTION,'-')  = '-'");
        q.addWhereCondition("NVL(QTY_REVERSED,0)<QUANTITY ");
        q.addWhereCondition("ORDER_TYPE = Order_Type_API.Get_Client_Value(3)");

	// SQLInjection_Safe AMNILK 20070712

	q.addWhereCondition("MAINT_MATERIAL_REQ_LINE_API.Get_Order_No_For_Wo_Line( ? ,LINE_ITEM_NO) = ? ");
	q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
	q.addParameter("MAINT_MATERIAL_ORDER_NO",headset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));
        q.includeMeta("ALL");

        mgr.submit(trans);

        itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
        itemset0.clear();
        headset.goTo(currrow); 
    }

    public void okFindITEM0()
    {
        ASPManager mgr = getASPManager();

        int nOrdintVal = new Double(headset.getRow().getValue("WO_NO")).intValue();
        trans.clear();
        currrow = headset.getCurrentRowNo();

        q = trans.addEmptyQuery(itemblk0);
        q.addWhereCondition("CONTRACT = USER_ALLOWED_SITE_API.AUTHORIZED(CONTRACT)");

	// SQLInjection_Safe AMNILK 20070712
	q.addWhereCondition("ORDER_NO = ? "); 
	q.addParameter("ORDER_NO",new Integer(nOrdintVal).toString());
	
        q.addWhereCondition("NVL(DIRECTION,'-')  = '-'");
        q.addWhereCondition("NVL(QTY_REVERSED,0)<QUANTITY ");
        q.addWhereCondition("ORDER_TYPE = Order_Type_API.Get_Client_Value(3)");

	// SQLInjection_Safe AMNILK 20070712
	q.addWhereCondition("MAINT_MATERIAL_REQ_LINE_API.Get_Order_No_For_Wo_Line( ? ,LINE_ITEM_NO) = ? ");
	q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
	q.addParameter("MAINT_MATERIAL_ORDER_NO",headset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));
	
        q.includeMeta("ALL");

        mgr.submit(trans);
        headset.goTo(currrow);
    }


    public void okFindITEM1()
    {
        ASPManager mgr = getASPManager();
        int nOrdintVal = new Double(headset.getRow().getValue("WO_NO")).intValue();
        trans.clear();
        currrow = headset.getCurrentRowNo();
        q = trans.addEmptyQuery(itemblk0);
        q.addWhereCondition("CONTRACT = USER_ALLOWED_SITE_API.AUTHORIZED(CONTRACT)");

	// SQLInjection_Safe AMNILK 20070712
	q.addWhereCondition("ORDER_NO = ? "); 
	q.addParameter("ORDER_NO",new Integer(nOrdintVal).toString());

        q.addWhereCondition("NVL(DIRECTION,'-')  = '-'");
        q.addWhereCondition("NVL(QTY_REVERSED,0)<QUANTITY ");
        q.addWhereCondition("ORDER_TYPE = Order_Type_API.Get_Client_Value(3)");

	// SQLInjection_Safe AMNILK 20070712

	q.addWhereCondition("MAINT_MATERIAL_REQ_LINE_API.Get_Order_No_For_Wo_Line( ? ,LINE_ITEM_NO) = ? ");
	q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
	q.addParameter("MAINT_MATERIAL_ORDER_NO",headset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));

	q.addWhereCondition("Active_Work_Order_API.Get_Obj_State( ? ) != 'FINISHED'");
	q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

        if (!mgr.isEmpty(sLineItemNo)){
	    q.addWhereCondition("LINE_ITEM_NO = ? "); 
    	    q.addParameter("LINE_ITEM_NO",sLineItemNo);
	}
        q.includeMeta("ALL");

        mgr.submit(trans);
        headset.goTo(currrow);
    }

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

    public void ok()
    {
        ASPManager mgr = getASPManager();
        String owner = "";

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());

        currrow = headset.getCurrentRowNo();
        item0row = itemset0.getCurrentRowNo();

        double qtyReserved=itemset0.getRow().getNumberValue("QTY_REVERSED");
        if (isNaN(qtyReserved))
            qtyReserved=0;
        double qty=itemset0.getRow().getNumberValue("QUANTITY");
        if (isNaN(qtyReserved))
            qty=0;

        double qtyUnIssue = mgr.readNumberValue("QTYUNISSUE");
        if (isNaN(qtyUnIssue))
            qtyUnIssue = 0;

        trans.clear();
        cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
        cmd.addParameter("PART_OWNERSHIP",itemset0.getRow().getValue("PART_OWNERSHIP"));

        trans = mgr.perform(trans);
        String sOwnershipDb = trans.getValue("VALIDOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");
        trans.clear();
        if ("CUSTOMER OWNED".equals(sOwnershipDb)) 
	{
            owner = itemset0.getRow().getValue("OWNING_CUSTOMER_NO");
        }
        if ("SUPPLIER LOANED".equals(sOwnershipDb)) 
	{
            owner =  itemset0.getRow().getValue("OWNING_VENDOR_NO");
        }

        cmd = trans.addCustomCommand("FUN","MAINT_MATERIAL_REQ_LINE_API.Unissue");
        cmd.addParameter("QTY_REVERSED",mgr.getASPField("QTY_REVERSED").formatNumber(qtyReserved));
        cmd.addParameter("ITEM0_CONTRACT",mgr.readValue("ITEM0_CONTRACT"));
        cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
        cmd.addParameter("QTYUNISSUE",mgr.getASPField("QTYUNISSUE").formatNumber(qtyUnIssue));
        cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
        cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        cmd.addParameter("SEQUENCE_NO",mgr.readValue("SEQUENCE_NO"));
        cmd.addParameter("LINE_ITEM_NO",mgr.readValue("LINE_ITEM_NO"));
        cmd.addParameter("QUANTITY",mgr.getASPField("QUANTITY").formatNumber(qty));
        cmd.addParameter("TRANSACTION_ID",mgr.readValue("TRANSACTION_ID"));
        cmd.addParameter("SOURCE",mgr.readValue("SOURCE")); 
        cmd.addParameter("PART_OWNERSHIP_DB",sOwnershipDb);  
        cmd.addParameter("OWNER",owner);

        trans = mgr.submit(trans);

        headset.goTo(currrow);  

        trans.clear();
        currrow = headset.getCurrentRowNo();
        q = trans.addEmptyQuery(itemblk0);
        q.addWhereCondition("CONTRACT = USER_ALLOWED_SITE_API.AUTHORIZED(CONTRACT)");

	// SQLInjection_Safe AMNILK 20070712

	q.addWhereCondition("ORDER_NO = ? "); 
	q.addParameter("ORDER_NO",headset.getRow().getValue("WO_NO"));

        q.addWhereCondition("NVL(DIRECTION,'-')  = '-'");
        q.addWhereCondition("NVL(QTY_REVERSED,0)<QUANTITY ");
        q.addWhereCondition("ORDER_TYPE = Order_Type_API.Get_Client_Value(3)");

	// SQLInjection_Safe AMNILK 20070712
	q.addWhereCondition("MAINT_MATERIAL_REQ_LINE_API.Get_Order_No_For_Wo_Line( ? ,LINE_ITEM_NO) = ? ");
	q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
	q.addParameter("MAINT_MATERIAL_ORDER_NO",headset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));

        if (!mgr.isEmpty(sLineItemNo)){
	    q.addWhereCondition("LINE_ITEM_NO = ? "); 
    	    q.addParameter("LINE_ITEM_NO",sLineItemNo);

	}
        
	q.includeMeta("ALL");

        mgr.submit(trans);
        headset.goTo(currrow);
        itemset0.goTo(item0row);
    }


    public void previous()
    {

        headset.previous();
        okFindITEM1();

    }


    public void next()
    {

        headset.next();
        okFindITEM1();

    }


    public void viewDetailsHead()
    {


        curentrow = headset.getRowSelected();
        headset.storeSelections();
        headset.goTo(curentrow);
        okFindITEM0();
        headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
    }

    public void back()
    {
        okClose = "TRUE";
    }

    public void adjust()
    {
        if (itemset0.countRows() == 0)
        {
            itemlay0.setLayoutMode(itemlay0.MULTIROW_LAYOUT);
        }

        if (itemlay0.isFindLayout() || itemlay0.isEditLayout() )
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

    public void preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("MAINT_MATERIAL_ORDER_NO","Number","#");
        f.setSize(12);
        f.setLabel("PCMWACTIVEWORKORDERMAINTMATERIALORDERNO: Order No").
        setHilite();

        f = headblk.addField("WO_NO","Number","#");
        f.setSize(10);
        f.setDynamicLOV("ACTIVE_WO_NOT_REPORTED",600,445);
        f.setLabel("PCMWACTIVEWORKORDERWONO: WO No").
        setHilite();

        f = headblk.addField("MCHCODE");
        f.setSize(12);
        f.setLabel("PCMWACTIVEWORKORDERMCHCODE: Object ID");
        f.setFunction("WORK_ORDER_API.Get_Mch_Code(:WO_NO)");
        mgr.getASPField("WO_NO").setValidation("MCHCODE");
        f.setUpperCase().
        setHilite();

        f = headblk.addField("DESCRIPTION");
        f.setSize(35);
        f.setFunction("Maintenance_Object_API.Get_Mch_Name(:CONTRACT,WORK_ORDER_API.Get_Mch_Code(:WO_NO))");
        mgr.getASPField("MCHCODE").setValidation("DESCRIPTION");
        f.setDefaultNotVisible().
        setHilite();

        f = headblk.addField("SIGNATURE_ID");
        f.setSize(12);
        f.setHidden();

        f = headblk.addField("SIGNATURE");
        f.setSize(12);
        f.setDynamicLOV("EMPLOYEE_LOV",600,445);
        f.setLabel("PCMWACTIVEWORKORDERSIGNATURE: Signature");
        f.setUpperCase();

        f = headblk.addField("SIGNATURENAME");
        f.setSize(30);
        f.setFunction("EMPLOYEE_API.Get_Employee_Info(SITE_API.Get_Company(:CONTRACT),:SIGNATURE_ID)");
        f.setDefaultNotVisible();

        f = headblk.addField("CONTRACT");
        f.setSize(12);
        f.setLabel("PCMWACTIVEWORKORDERCONTRACT: Site");
        f.setUpperCase();
        f.setDefaultNotVisible();
        f.setHilite();

        f = headblk.addField("ENTERED","Date");
        f.setSize(10);
        f.setLabel("PCMWACTIVEWORKORDERENTERED: Entered");
        f.setDefaultNotVisible();

        f = headblk.addField("INT_DESTINATION_ID");
        f.setSize(12);
        f.setDynamicLOV("INTERNAL_DESTINATION_LOV",600,445);
        f.setLabel("PCMWACTIVEWORKORDERINT_DESTINATION_ID: Int Destination");
        f.setDefaultNotVisible();

        f = headblk.addField("INT_DESTINATION_DESC");
        f.setSize(30);
        f.setDefaultNotVisible();

        f = headblk.addField("DUE_DATE","Date");
        f.setSize(12);
        f.setLabel("PCMWACTIVEWORKORDERDUE_DATE: Due Date");

        f = headblk.addField("STATE");
        f.setSize(10);
        f.setLabel("PCMWACTIVEWORKORDERSTATE: Status").
        setHilite();

        f = headblk.addField("PROJECT_ID");
        f.setSize(10);
        f.setLabel("PCMWACTIVEWORKORDERPROJECTID: Project ID");
        //f.setFunction("Activity_API.Get_Project_Id(:ACTIVITY_SEQ)");
        f.setFunction("''");
        f.setMaxLength(20);
        f.setReadOnly();

        f = headblk.addField("PROJECT_DESC");
        f.setSize(10);
        f.setLabel("PCMWACTIVEWORKORDERPROJECTDESC: Project Description");
        f.setFunction("''");
        //f.setFunction("PROJECT_API.Get_Name(:PROJECT_ID)");
        f.setMaxLength(10);

        f = headblk.addField("ACTIVITY_SEQ","Number");
        f.setSize(10);
        f.setLabel("PCMWACTIVEWORKORDERACTIVITYSEQE: Activity Sequence");
        f.setMaxLength(100);
        f.setReadOnly();
        f.setInsertable();

        headblk.setView("MAINT_MATERIAL_REQUISITION");
        headblk.defineCommand("MAINT_MATERIAL_REQUISITION_API","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWACTIVEWORKORDERHD: Unissue of Inventory Part for Maint - Material Requisition"));
        headtbl.setWrap();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.setBorderLines(false,true);
        headbar.disableCommand(headbar.NEWROW);
        headbar.disableCommand(headbar.DELETE);
        headbar.disableCommand(headbar.EDITROW);
        headbar.disableCommand(headbar.DUPLICATEROW);
        headbar.defineCommand(headbar.COUNTFIND,"countFind");
        headbar.defineCommand(headbar.BACKWARD,"previous");
        headbar.defineCommand(headbar.FORWARD,"next");
        headbar.defineCommand(headbar.VIEWDETAILS,"viewDetailsHead");
        headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields(-1)");
        headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");

        headset = headblk.getASPRowSet();
        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        headlay.setDialogColumns(3);
        headlay.setFieldOrder("MAINT_MATERIAL_ORDER_NO,WO_NO,MCHCODE,DESCRIPTION,SIGNATURE,SIGNATURENAME,CONTRACT,ENTERED,INT_DESTINATION_ID,INT_DESTINATION_DESC,DUE_DATE,STATE"); 
        headlay.setSimple("DESCRIPTION");
        headlay.setSimple("SIGNATURENAME");
        headlay.setSimple("INT_DESTINATION_DESC");

        // ITEM BLOCK***************************************         

        itemblk0 = mgr.newASPBlock("ITEM0");

        itemblk0.addField("ITEM0_OBJID").
        setHidden().
        setDbName("OBJID");

        itemblk0.addField("ITEM0_OBJVERSION").
        setHidden().
        setDbName("OBJVERSION");

        itemblk0.addField("QTYUNISSUE","Number").
        setSize(12).
        setLabel("PCMWACTIVEWORKORDERQTYUNISSUE: Quantity Unissued").
        setFunction("''");

        itemblk0.addField("QTYISSUED","Number").
        setSize(12).
        setLabel("PCMWACTIVEWORKORDERQTYISSUED: Quantity Issued").
        setFunction("NVL(quantity,0) - NVL(qty_reversed,0)").
        setReadOnly();  

        itemblk0.addField("PART_NO").
        setSize(25).
        setMaxLength(25).
        setLOV("../invenw/InventoryPartWoLovLov.page","CONTRACT",600,445).
        setLabel("PCMWACTIVEWORKORDERPART_NO: Part No").
        setUpperCase().
        setReadOnly();

        itemblk0.addField("PARTDESC").
        setSize(25).
        setLabel("PCMWACTIVEWORKORDERPARTDESC: Part Description").
        setFunction("Inventory_Part_API.Get_Description(:ITEM0_CONTRACT,:PART_NO)").
        setReadOnly();     

        itemblk0.addField("ITEM0_CONTRACT").
        setSize(5).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445).
        setLabel("PCMWACTIVEWORKORDERITEM0_CONTRACT: Site").
        setDbName("CONTRACT").
        setUpperCase().
        setReadOnly();

        itemblk0.addField("LOCATION_NO").
        setSize(25).
        setMaxLength(35).
        setLabel("PCMWACTIVEWORKORDERLOCATION_NO: Location No").
        setReadOnly();

        itemblk0.addField("WAREHOUSE").
        setSize(25).
        setMaxLength(25).
        setLabel("PCMWACTIVEWORKORDERWAREHOUSE: Warehouse").
        setFunction("Inventory_Location_API.Get_Warehouse(:ITEM0_CONTRACT,:LOCATION_NO)").
        setReadOnly();

        itemblk0.addField("BAY").
        setSize(25).
        setMaxLength(25).
        setLabel("PCMWACTIVEWORKORDERBAY: Bay").
        setFunction("Inventory_Location_API.Get_Bay_No(:ITEM0_CONTRACT,:LOCATION_NO)").
        setReadOnly();

        itemblk0.addField("ROW_NO").
        setSize(25).
        setMaxLength(25).
        setLabel("PCMWACTIVEWORKORDERROW_NO: Row").
        setFunction("Inventory_Location_API.Get_Row_No(:ITEM0_CONTRACT,:LOCATION_NO)").
        setReadOnly();          

        itemblk0.addField("TIER").
        setSize(25).
        setMaxLength(25).
        setLabel("PCMWACTIVEWORKORDERTIER: Tier").
        setFunction("Inventory_Location_API.Get_Tier_No(:ITEM0_CONTRACT,:LOCATION_NO)").
        setReadOnly();

        itemblk0.addField("BIN").
        setSize(25).
        setMaxLength(25).
        setLabel("PCMWACTIVEWORKORDERBIN: Bin").
        setFunction("Inventory_Location_API.Get_Bin_No(:ITEM0_CONTRACT,:LOCATION_NO)").
        setReadOnly();

        itemblk0.addField("LOT_BATCH_NO").
        setSize(25).
        setMaxLength(20).
        setLabel("PCMWACTIVEWORKORDERLOT_BATCH_NO: Lot/Batch No").
        setReadOnly();

        itemblk0.addField("SERIAL_NO").
        setSize(20).
        setMaxLength(50).
        setLabel("PCMWACTIVEWORKORDERSERIAL_NO: Serial No").
        setUpperCase().
        setReadOnly();

        itemblk0.addField("CONDITION_CODE").
        setLabel("PCMWACTIVEWORKORDERCONDITIONCODE: Condition Code").
        setSize(15).
        setReadOnly();

        itemblk0.addField("CONDDESC").
        setLabel("PCMWACTIVEWORKORDERCONDDESC: Condition Code Description").
        setFunction("CONDITION_CODE_API.Get_Description(:CONDITION_CODE)").
        setSize(20).
        setMaxLength(50).
        setReadOnly();

        itemblk0.addField("WAIV_DEV_REJ_NO").
        setSize(12).
        setLabel("PCMWACTIVEWORKORDERWAIV_DEV_REJ_NO: W/D/R No").
        setDefaultNotVisible().
        setReadOnly();

        itemblk0.addField("ENG_CHG_LEVEL").
        setSize(3).
        setMaxLength(2).
        setLabel("PCMWACTIVEWORKORDERENG_CHG_LEVEL: EC").
        setReadOnly();

        itemblk0.addField("QUANTITY","Number").
        setSize(12).
        setHidden().
        setLabel("PCMWACTIVEWORKORDERQUANTITY: Quantity").
        setReadOnly();

        itemblk0.addField("COST","Number").
        setSize(12).
        setHidden().
        setLabel("PCMWACTIVEWORKORDERCOST: Cost").
        setReadOnly();

        itemblk0.addField("LINE_ITEM_NO","Number").
        setSize(12).
        setHidden().
        setLabel("PCMWACTIVEWORKORDERLINE_ITEM_NO: Line Item No").
        setReadOnly();

        itemblk0.addField("QTY_REVERSED","Number").
        setSize(12).
        setHidden().
        setLabel("PCMWACTIVEWORKORDERQTY_REVERSED: Qty Reversed").
        setReadOnly();

        itemblk0.addField("CONFIGURATION_ID").
        setSize(20).
        setLabel("PCMWACTIVEWORKORDERCONFIGURATION_ID: Configuration ID").
        setReadOnly();

        itemblk0.addField("DATE_CREATED","Date").
        setSize(12).
        setLabel("PCMWACTIVEWORKORDERDATECREATED: Created").
        setReadOnly().
        setHidden();

        itemblk0.addField("ACCOUNTING_ID","Number").
        setSize(12).
        setHidden().
        setLabel("PCMWACTIVEWORKORDERACCOUNTING_ID: Accounting Id").
        setReadOnly();

        itemblk0.addField("TRANSACTION_ID","Number").
        setSize(12).
        setHidden().
        setLabel("PCMWACTIVEWORKORDERTRANSACTION_ID: TransactionId").
        setReadOnly();

        itemblk0.addField("PARTSHORTAGESEXIST","Number").
        setSize(12).
        setHidden().
        setLabel("PCMWACTIVEWORKORDERPARTSHORTAGESEXIST: Shortages Exist").
        setFunction("Shortage_Demand_API.Shortage_Exists(:ITEM0_CONTRACT,:PART_NO)").
        setReadOnly();

        itemblk0.addField("PARTSHORTAGEFLAG").
        setSize(12).
        setHidden().
        setLabel("PCMWACTIVEWORKORDERPARTSHORTAGEFLAG: PartShortageFlag").
        setFunction("Inventory_Part_API.Get_Shortage_Flag(:ITEM0_CONTRACT,:PART_NO)").
        setReadOnly();

        itemblk0.addField("SEQUENCE_NO").
        setSize(12).
        setHidden().
        setLabel("PCMWACTIVEWORKORDERSEQUENCE_NO: PartShortageFlag").
        setReadOnly();

        itemblk0.addField("SOURCE").
        setSize(12).
        setMaxLength(2000).
        setHidden().
        setLabel("PCMWACTIVEWORKORDERSOURCE: Source").
        setReadOnly();

        itemblk0.addField("ORDER_NO").
        setSize(12).
        setHidden().
        setReadOnly();

        itemblk0.addField("DIRECTION").
        setSize(12).
        setHidden().
        setReadOnly();

        //Bug 66456, Start, Added check on PROJ
        if (mgr.isModuleInstalled("PROJ"))
        {
            itemblk0.addField("ITEM0_ACTIVITY_SEQ","Number").
            setSize(11).
            setDbName("ACTIVITY_SEQ").
            setReadOnly().
            setLabel("PCMWACTIVEWORKORDERACTIVITYSEQ: Activity Seq");
    
            itemblk0.addField("PROGRAM_ID").
            setSize(10).
            setFunction("Activity_API.Get_Program_Id(:ACTIVITY_SEQ)").
            setLabel("PCMWACTIVEWORKORDERPROGID: Program ID").
            setReadOnly().
            setDefaultNotVisible();
    
            itemblk0.addField("PROGRAM_DESC").
            setSize(10).
            setFunction("Activity_API.Get_Program_Description(:ACTIVITY_SEQ)").
            setLabel("PCMWACTIVEWORKORDERPROGDESC: Program Description").
            setReadOnly().
            setDefaultNotVisible();
    
            itemblk0.addField("ITEM0_PROJECT_ID").
            setSize(11).
            setFunction("Activity_API.Get_Project_Id(:ACTIVITY_SEQ)").
            setReadOnly().
            setLabel("PCMWACTIVEWORKORDERPROJECTID: Project ID").
            setDefaultNotVisible();
    
            itemblk0.addField("ITEM0_PROJECT_DESC").
            setSize(10).
            setLabel("PCMWACTIVEWORKORDERPROJECTDESC: Project Description").
            setFunction("Activity_API.Get_Project_Name(:ACTIVITY_SEQ)").
            setReadOnly().
            setDefaultNotVisible();
    
            itemblk0.addField("SUB_PROJECT_ID").
            setSize(10).
            setLabel("PCMWACTIVEWORKORDERSUBPROJID: Sub Project ID").
            setFunction("Activity_API.Get_Sub_Project_Id(:ACTIVITY_SEQ)").
            setReadOnly().
            setDefaultNotVisible();
    
            itemblk0.addField("SUB_PROJECT_DESC").
            setSize(10).
            setLabel("PCMWACTIVEWORKORDERSUBPRDESC: Sub Project Description").
            setFunction("Activity_API.Get_Sub_Project_Description(:ACTIVITY_SEQ)").
            setReadOnly().
            setDefaultNotVisible();
    
            itemblk0.addField("ACTIVITY_ID").
            setSize(10).
            setLabel("PCMWACTIVEWORKORDERACTID: Activity ID").
            setFunction("Activity_API.Get_Activity_No(:ACTIVITY_SEQ)").
            setReadOnly().
            setDefaultNotVisible();
    
            itemblk0.addField("ACTIVITY_DESC").
            setSize(10).
            setLabel("PCMWACTIVEWORKORDERACTIDESC: Activity Description").
            setFunction("Activity_API.Get_Description(:ACTIVITY_SEQ)").
            setReadOnly().
            setDefaultNotVisible();
        }
        //Bug 66456, End

        itemblk0.addField("PART_OWNERSHIP").
        setSize(12).
        setHidden().
        setReadOnly();

        itemblk0.addField("PART_OWNERSHIP_DB").
        setSize(12).
        setHidden().
        setReadOnly();

        itemblk0.addField("OWNING_CUSTOMER_NO").
        setSize(12).
        setHidden().
        setReadOnly();

        itemblk0.addField("OWNING_VENDOR_NO").
        setSize(12).
        setHidden().
        setReadOnly();

        itemblk0.addField("OWNER").
        setFunction("''").
        setSize(12).
        setHidden().
        setReadOnly();

        itemblk0.setView("INVENTORY_TRANSACTION_HIST");
        itemblk0.defineCommand("INVENTORY_TRANSACTION_HIST_API","New__,Modify__,Remove__");
        itemset0 = itemblk0.getASPRowSet();
        itemblk0.setMasterBlock(headblk);

        itemset0 = itemblk0.getASPRowSet();
        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDialogColumns(3);
        itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);

        itembar0 = mgr.newASPCommandBar(itemblk0);
        itembar0.setBorderLines(true,true);

        itembar0.defineCommand(itembar0.SAVERETURN,"ok","checkItem0Fields(-1)");
        itembar0.disableCommand(itembar0.NEWROW);
        itembar0.enableCommand(itembar0.CANCELEDIT);
        itembar0.disableCommand(itembar0.DELETE);
        itembar0.disableCommand(itembar0.DUPLICATEROW);
        itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
        itembar0.enableCommand(itembar0.FIND);
        itembar0.defineCommand(itembar0.OKFIND,"okFindITEM1");

        itemtbl0 = mgr.newASPTable(itemblk0);
        itemtbl0.setTitle(mgr.translate("PCMWACTIVEWORKORDERITM0: Inventory Transaction Hist"));
        itemtbl0.setWrap();

    }  

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWACTIVEWORKORDERTITLE: Unissue of Inventory Part for Maint - Material Requisition";
    }

    protected String getTitle()
    {
        return "PCMWACTIVEWORKORDERTITLE: Unissue of Inventory Part for Maint - Material Requisition";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.show());

        if (itemlay0.isVisible())
            appendToHTML(itemlay0.show());

        appendToHTML("<br>\n");

        if (!backButton.equals("FALSE"))
            appendToHTML(fmt.drawSubmit("BACK",mgr.translate("PCMWACTIVEWORKORDERBACK: <Back"),""));

        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(okClose)); // Bug Id 68773
        appendDirtyJavaScript("' == \"TRUE\")\n");
        appendDirtyJavaScript("{\n");
        if (qrystr.equals("MaintMaterialReqLineOvw.page")) {
           appendDirtyJavaScript("window.opener.refresh();\n");
        }
        else {
           appendDirtyJavaScript("  window.open('");
           appendDirtyJavaScript(mgr.encodeStringForJavascript(qrystr)); // XSS_Safe ILSOLK 20070709
           appendDirtyJavaScript("' + \"&OKUNISSUE=1\",'");
           appendDirtyJavaScript(mgr.encodeStringForJavascript(frmname)); // XSS_Safe ILSOLK 20070709
           appendDirtyJavaScript("',\"\");\n");
        }
        appendDirtyJavaScript("  window.close();\n");
        appendDirtyJavaScript("}\n");
    }
}

