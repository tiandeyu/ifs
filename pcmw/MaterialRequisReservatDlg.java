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
*  File        : MaterialRequisReservatDlg.java 
*  Created     : ASP2JAVA Tool  010221
*  Modified    :  
*  BUNILK  010222  Corrected some conversion errors.
*  BUNILK  010406  Changed default layout mode of itemblk to multi record mode
*                  and added a back button to refesh calling form after reserv.   
*  BUNILK  011016  Modified form so that to refresh calling form for each 
*                  reservation of material.  
*  GACOLK  021204  Set Max Length of SERIAL_NO to 50
*  NIMHLK  030110  Added two fields Condition Code and Condition Code Description according to specification W110 - Condition Codes.
*  CHCRLK  030505  Corrected errors and added Condition Code and Condition Code Description to the header.
*  ARWILK  031219  Edge Developments - (Removed clone and doReset Methods)
*  BUNILK  040625  Modified predefine() and ok() methods
*  VAGULK  040723  Added fields for Project group,SCME612 - Project Inventory.
*  BUNILK  040827  Modified run(), okFind() and okFindITEM0() methods so that to filter by supply_code. 
*  NAMELK  041108  Non Standard and Duplicated Translation Tags Corrected. 
*  ARWILK  041115  Replaced getContents with printContents.
*  NIJALK  050405  Call 123081: Modified run(), okFind(), okFindITEM0().
*  SHAFLK  050420  Bug 50710, Modified method preDefine().
*  DiAmlk  050518  Merged the corrections done in LCS Bug ID:50710.
*  NIJALK  050808  Bug 126169: Modified okFindITEM0().
*  SHAFLK  051229  Bug 54562, Modified method preDefine() and ok().
*  NIJALK  060118  Merged bug 54562.
*  NIJALK  060210  Call 133546: Renamed "STANDARD_INVENTORY" with "INVENT_ORDER".
*  SULILK  060222  Call 134906: Modified run(), okFind().
*  NIJALK  060223  Call 135198: Modified run(). 
*  NIJALK  060311  Call 136946: Modified run().
*  AMNILK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMDILK  060831  Bug 58216, Eliminated SQL Injection security vulnerability.Modifeid method okFindITEM0()
*  AMNILK  060905  Merged Bug Id: 58216.
*  ASSALK  070713  Webification - Modified printContents();
*  AMNILK  070718  Eliminated XSS Security Vulnerability.
*  NIJALK  080202  Bug 66456, Modified run(), ok(), preDefine().
*  SHAFLK  090403  Bug 80347, Modified preDefine().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class MaterialRequisReservatDlg extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.MaterialRequisReservatDlg");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPContext ctx;
    private ASPHTMLFormatter fmt;

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

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private String sContract;
    private ASPTransactionBuffer trans;
    private String okClose;
    private boolean refreshMain;
    private String maintMatOrdNo;
    private String frmname;
    private String qrystr;
    private String cancelWin;
    private ASPBuffer buff;
    private ASPBuffer row;
    private String qtyLeft;
    private String partDesc;
    private String condCode;
    private String condCodeDesc;
    private ASPQuery q;
    private ASPBuffer temp;
    private ASPCommand cmd;
    private String fname;
    private String sWoNo;
    private String sLineItemNo; 
    private String sPartNo; 
    private String sSupplyCode;
    private String sProjectInventory;
    private String sStandardInventory;
    private String sProjectId;
    private String sProjectDesc;
    private String sActivitySeq;
    private String sOwnership;
    private String sOwner;
    private String sOwnerName;

    //===============================================================
    // Construction 
    //===============================================================
    public MaterialRequisReservatDlg(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        sWoNo = "";
        sLineItemNo = "";
        sLineItemNo = "";
        sPartNo = "";
        sContract = "";
        sContract = "";
        partDesc = "";      
        condCode = "";
        condCodeDesc = "";
        sOwnership = "";
        sOwner = "";
        sOwnerName = "";
        qtyLeft = "";

        ASPManager mgr = getASPManager();

        mgr.setPageExpiring();
        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();
        fmt = mgr.newASPHTMLFormatter();   

        okClose = ctx.readValue("OKCLOSE","FALSE");
        refreshMain = ctx.readFlag("REFRESHMAIN",false);
        sWoNo = ctx.readValue("CTXWONO",sWoNo);
        sLineItemNo = ctx.readValue("CTXLINEITEM",sLineItemNo);
        sPartNo = ctx.readValue("CTXPART",sPartNo);
        partDesc = ctx.readValue("PARTDESC",partDesc);
        condCode = ctx.readValue("CONDCODE",condCode);
        condCodeDesc = ctx.readValue("CONDCODEDESC",condCodeDesc);
        maintMatOrdNo = ctx.readValue("MAINTMATORDNO","");
        sContract = ctx.readValue("CTXCON",sContract);
        frmname= ctx.readValue("FRMNAME","");
        qrystr = ctx.readValue("QRYSTR","");
        cancelWin = ctx.readValue("CANCELWIN","");
        sOwnership = ctx.readValue("CTXOWNERSHIP",sOwnership);
        sOwner = ctx.readValue("CTXOWNER",sOwner);
        sOwnerName = ctx.readValue("CTXOWNERNAME",sOwnerName);

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (mgr.dataTransfered())
        {
            buff = mgr.getTransferedData();

            row = buff.getBufferAt(0); 
            sWoNo = row.getValue("WO_NO");

            row = buff.getBufferAt(1); 
            sLineItemNo = row.getValue("LINE_ITEM_NO");

            okFind();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
        {
            sPartNo = mgr.getQueryStringValue("PART_NO");
            sContract = mgr.getQueryStringValue("CONTRACT");
            sWoNo = mgr.getQueryStringValue("WO_NO");
            sLineItemNo = mgr.getQueryStringValue("LINE_ITEM_NO");
            qtyLeft = mgr.getQueryStringValue("QTYLEFT");
            partDesc = mgr.getQueryStringValue("DESCRIPTION");
            condCode = mgr.getQueryStringValue("CONDITION_CODE");
            condCodeDesc =  mgr.getQueryStringValue("CONDITION_CODE_DESC");
            qrystr = mgr.getQueryStringValue("QRYSTR");  
            frmname = mgr.getQueryStringValue("FRMNAME");
            maintMatOrdNo = mgr.getQueryStringValue("MAINTMATORDNO");
            sOwnership = mgr.getQueryStringValue("OWNERSHIP");
            sOwner = mgr.getQueryStringValue("OWNER");
            sOwnerName = mgr.getQueryStringValue("OWNERNAME");

            if ("NULL".equals(condCode) || "null".equals(condCode))
                condCode = "";
            if ("NULL".equals(condCodeDesc) || "null".equals(condCodeDesc))
                condCodeDesc = "";

            if (!mgr.isEmpty(maintMatOrdNo))
            {
		//Bug 58216 Start
                q = trans.addQuery("ACTSEQ","SELECT ACTIVITY_SEQ FROM MAINT_MATERIAL_REQUISITION WHERE MAINT_MATERIAL_ORDER_NO = ?");
		q.addParameter("ACTIVITY_SEQ",maintMatOrdNo);
                //Bug 58216 End

                trans = mgr.perform(trans);
                ASPBuffer tempBuff = trans.getBuffer("ACTSEQ/DATA");
                sActivitySeq = tempBuff.getValueAt(0);

                trans.clear();

                cmd  = trans.addCustomFunction("SUPPCODE","MAINT_MATERIAL_REQ_LINE_API.Get_Supply_Code","SUPPLY_CODE");
                cmd.addParameter("WO_NO",maintMatOrdNo);
                cmd.addParameter("LINE_ITEM_NO",sLineItemNo);

                cmd = trans.addCustomFunction("SUPPCODE1","MAINT_MAT_REQ_SUP_API.DECODE('PROJECT_INVENTORY')","DB_VAL1");
                cmd = trans.addCustomFunction("SUPPCODE2","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","DB_VAL2");

                //Bug 66456, Start, Added check on PROJ
                if (!mgr.isEmpty(sActivitySeq) && mgr.isModuleInstalled("PROJ"))
                {
                    cmd  = trans.addCustomFunction("PROJID","Activity_API.Get_Project_Id","PROJECT_ID");
                    cmd.addParameter("ACTIVITY_SEQ",sActivitySeq);

                    cmd  = trans.addCustomFunction("PROJDESC","Project_API.Get_Name","PROJECT_DESC");
                    cmd.addReference("PROJECT_ID","PROJID/DATA");
                }
                //Bug 66456, End
                trans = mgr.perform(trans);

                sSupplyCode = trans.getValue("SUPPCODE/DATA/SUPPLY_CODE");
                sProjectInventory = trans.getValue("SUPPCODE1/DATA/DB_VAL1");
                sStandardInventory = trans.getValue("SUPPCODE2/DATA/DB_VAL2");

                //Bug 66456, Start, Added check on PROJ
                if (!mgr.isEmpty(sActivitySeq) && mgr.isModuleInstalled("PROJ"))
                {
                    sProjectId = trans.getValue("PROJID/DATA/PROJECT_ID");
                    sProjectDesc = trans.getValue("PROJDESC/DATA/PROJECT_DESC");
                }
                else
                {
                    sProjectId = "";
                    sProjectDesc = "";
                }
                //Bug 66456, End
                trans.clear();
            }
            if ((!mgr.isEmpty(condCode)) && mgr.isEmpty(condCodeDesc))
            {
                trans.clear();
                cmd = trans.addCustomFunction("CONCODEDESC","CONDITION_CODE_API.Get_Description","CONDITION_CODE_DESC");
                cmd.addParameter("CONDITION_CODE",condCode);
                trans = mgr.perform(trans);
                condCodeDesc = trans.getValue("CONCODEDESC/DATA/CONDITION_CODE_DESC");
            }
            okFind();
        }
        else if (mgr.buttonPressed("BACK"))
            back();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();

        ctx.writeValue("CTXWONO",sWoNo);
        ctx.writeValue("CTXLINEITEM",sLineItemNo);
        ctx.writeValue("CTXPART",sPartNo);
        ctx.writeValue("CTXCON",sContract);
        ctx.writeValue("FRMNAME",frmname);
        ctx.writeValue("QRYSTR",qrystr);
        ctx.writeValue("MAINTMATORDNO",maintMatOrdNo);
        ctx.writeFlag("REFRESHMAIN",refreshMain);
        ctx.writeValue("PARTDESC",partDesc);
        ctx.writeValue("CONDCODE",condCode);
        ctx.writeValue("CONDCODEDESC",condCodeDesc);
        ctx.writeValue("CTXOWNERSHIP",sOwnership);
        ctx.writeValue("CTXOWNER",sOwner);
        ctx.writeValue("CTXOWNERNAME",sOwnerName);
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void countFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        String n = headset.getRow().getValue("N");
        headlay.setCountValue(toInt(n));
    }


    public void okFind()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        q = trans.addEmptyQuery(headblk);
        q.includeMeta("ALL");
        mgr.submit(trans);

        temp = headset.getRow();

        double qtyLeftNum = toDouble(qtyLeft);

        temp.setValue("SPARTNO", sPartNo);
        temp.setValue("SCONTRACT", sContract);
        temp.setNumberValue("NQUANTITY", qtyLeftNum);
        temp.setValue("SDESCRIPTION", partDesc);
        temp.setValue("CONDITION_CODE", condCode);
        temp.setValue("CONDITION_CODE_DESC",condCodeDesc);
        temp.setValue("PROJECT_ID", sProjectId);     
        temp.setValue("PROJECT_DESC", sProjectDesc);
        temp.setValue("ACTIVITY_SEQ", sActivitySeq);
        temp.setValue("OWNERSHIP", sOwnership);
        temp.setValue("OWNER", sOwner);
        temp.setValue("OWNERNAME", sOwnerName);

        headset.setRow(temp);

        okFindITEM0();
    }

//-----------------------------------------------------------------------------
//-----------------------  CMDBAR SEARCH-IT FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

    public void okFindITEM0()
    {
        ASPManager mgr = getASPManager();
        String sWhereStmt = "";
        String sOwingCustomerNo = "";
        String sConsignmentStock = "";

	//Bug 58216 start
	ASPBuffer paramBuff = mgr.newASPBuffer();
        if (mgr.isEmpty(condCode)) {
            sWhereStmt += "PART_NO= ? AND CONTRACT= ? AND (QTY_ONHAND - QTY_RESERVED) > 0";
            paramBuff.addItem("PART_NO",sPartNo);
	    paramBuff.addItem("CONTRACT",sContract);
	}
	else {
            sWhereStmt += "PART_NO= ? AND CONTRACT= ? AND (QTY_ONHAND - QTY_RESERVED) > 0 AND Condition_Code_Manager_API.Get_Condition_Code( PART_NO, SERIAL_NO, LOT_BATCH_NO) = ? ";
	    paramBuff.addItem("PART_NO", sPartNo);
	    paramBuff.addItem("CONTRACT", sContract);
	    paramBuff.addItem("CONDITION_CODE", condCode);
	}
        if (!mgr.isEmpty(sProjectId) && sSupplyCode.equals(sProjectInventory))	{
            sWhereStmt += " AND PROJECT_ID = ?";
            paramBuff.addItem("PROJECT_ID", sProjectId);
	}
	else 
	    sWhereStmt += " AND PROJECT_ID IS NULL";
        trans.clear();
        cmd = trans.addCustomFunction("OWNDB","PART_OWNERSHIP_API.Encode","OWNERSHIP_DB");
        cmd.addParameter("OWNERSHIP",sOwnership);
        trans = mgr.perform(trans);
        String ownership_db = trans.getValue("OWNDB/DATA/OWNERSHIP_DB");

        if ("COMPANY OWNED".equals(ownership_db))
        {
            sConsignmentStock = "CONSIGNMENT";
            sWhereStmt += " AND (PART_OWNERSHIP_DB = ? OR PART_OWNERSHIP_DB = ?)";
	    paramBuff.addItem("OWNERSHIP_DB", ownership_db);
	    paramBuff.addItem("OWNERSHIP_DB", sConsignmentStock);
        }
        else if ("CUSTOMER OWNED".equals(ownership_db))
        {
            sOwingCustomerNo = sOwner;
            sWhereStmt += " AND PART_OWNERSHIP_DB = ?";
	    paramBuff.addItem("OWNERSHIP_DB", ownership_db);        }

        if (!mgr.isEmpty(sOwingCustomerNo)) {
            sWhereStmt += " AND OWNING_CUSTOMER_NO = ?";
	    paramBuff.addItem("OWNER", sOwingCustomerNo);
	}

        if (!mgr.isEmpty(condCode)) {
            sWhereStmt += " AND CONDITION_CODE = ?";
	    paramBuff.addItem("CONDITION_CODE", condCode);
	}
       
        trans.clear();
        q = trans.addEmptyQuery(itemblk0);
	q.addWhereCondition(sWhereStmt);
	for (int i = 0; i < paramBuff.countItems(); i++)
                    q.addParameter(paramBuff.getNameAt(i),paramBuff.getValueAt(i));

	//Bug 58216 end

        q.includeMeta("ALL");
        mgr.querySubmit(trans,itemblk0);

    }

    public void countFindITEM0()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk0);    
        if (mgr.isEmpty(condCode)){
            //Bug 58216 Start
            q.addWhereCondition("PART_NO= ? AND CONTRACT= ? AND (QTY_ONHAND - QTY_RESERVED) > 0 ");
	    q.addParameter("PART_NO",sPartNo);
	    q.addParameter("CONTRACT",sContract);
	}
        else{
            q.addWhereCondition("PART_NO= ? AND CONTRACT= ? AND (QTY_ONHAND - QTY_RESERVED) > 0 AND Condition_Code_Manager_API.Get_Condition_Code( PART_NO, SERIAL_NO, LOT_BATCH_NO) = ?");
	    q.addParameter("PART_NO",sPartNo);
	    q.addParameter("CONTRACT",sContract);
	    q.addParameter("CONDITION_CODE",condCode);
	    //Bug 58216 End
	}
        q.includeMeta("ALL");

        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        String n = itemset0.getRow().getValue("N");
        itemset0.clear();
        itemlay0.setCountValue(toInt(n));
    }

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

    public void ok()
    {
        ASPManager mgr = getASPManager();


        double nQuantity = mgr.readNumberValue("ITEM0_NQUANTITY");       
        if (isNaN(nQuantity))
            nQuantity = 0;


        double nAvailable = itemset0.getRow().getNumberValue("QTY_AVAILABLE");
        if (isNaN(nAvailable))
            nAvailable = 0;

        double nQtyLeft = headset.getRow().getNumberValue("NQUANTITY");
        if (isNaN(nQtyLeft))
            nQtyLeft = 0;

        if (nQuantity != 0)
        {
            if ((nQuantity > nAvailable) || (nQuantity > nQtyLeft))
            {
                double nQtyMax = 0;

                if (nAvailable < nQtyLeft)
                    nQtyMax = nAvailable;
                else
                    nQtyMax = nQtyLeft;      

                String nQtyMaxStr = String.valueOf(nQtyMax);

                mgr.showAlert(mgr.translate("PCMWMATERIALREQUISRESERVATDLGQTYOVERFLOW: Maximum quantity is &1 ",nQtyMaxStr));
                okClose = "FALSE" ;
            }
            else
            {
                String nQuantityStr = mgr.getASPField("ITEM0_NQUANTITY").formatNumber(nQuantity);         

                cmd = trans.addCustomCommand("RES","MAINT_MATERIAL_REQ_LINE_API.Make_Reservation_Detail");
                cmd.addParameter("WO_NO",maintMatOrdNo);
                cmd.addParameter("LINE_ITEM_NO",sLineItemNo);
                cmd.addParameter("LOCATION_NO", itemset0.getRow().getValue("LOCATION_NO"));
                cmd.addParameter("LOT_BATCH_NO", itemset0.getRow().getValue("LOT_BATCH_NO"));
                cmd.addParameter("SERIAL_NO", itemset0.getRow().getValue("SERIAL_NO"));
                cmd.addParameter("ENG_CHG_LEVEL", itemset0.getRow().getValue("ENG_CHG_LEVEL"));
                cmd.addParameter("WAIV_DEV_REJ_NO", itemset0.getRow().getValue("WAIV_DEV_REJ_NO"));
                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))
                {
                    cmd.addParameter("ACTIVITY_SEQ", itemset0.getRow().getValue("ACTIVITY_SEQ"));
                    cmd.addParameter("PROJECT_ID", itemset0.getRow().getValue("PROJECT_ID"));
                }
                else
                {
                    cmd.addParameter("ACTIVITY_SEQ", "");
                    cmd.addParameter("PROJECT_ID", "");
                }
                //Bug 66456, End
                cmd.addParameter("ITEM0_NQUANTITY", nQuantityStr);
                cmd.addParameter("CONFIGURATION_ID", itemset0.getRow().getValue("CONFIGURATION_ID"));

                trans = mgr.perform(trans);

                double availQty = nQtyLeft - nQuantity;

                trans.clear();

                q = trans.addEmptyQuery(headblk);
                q.includeMeta("ALL");
                mgr.submit(trans);

                temp = headset.getRow();

                temp.setValue("SPARTNO",sPartNo);
                temp.setValue("SCONTRACT",sContract);
                temp.setNumberValue("NQUANTITY",availQty);
                temp.setValue("SDESCRIPTION",partDesc);

                headset.setRow(temp);

                okFindITEM0();
                refreshMain = true;
            }
        }
    }

    public void cancel()
    {
        fname = "true";
        cancelWin = "TRUE";
    }

    public void back()
    {
        okClose = "TRUE";
    }

    public void preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("OBJVERSION");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("SPARTNO");
        f.setSize(14);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGSPARTNO: Part No");
        f.setFunction("''");
        f.setUpperCase();
        f.setReadOnly();

        f = headblk.addField("SDESCRIPTION");
        f.setSize(28);
        f.setReadOnly();
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGSDESCRIPTION: Part Description");
        f.setFunction("''");

        f = headblk.addField("NQUANTITY", "Number");
        f.setSize(11);
        f.setReadOnly();
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGNQUANTITY: Quantity Left");
        f.setFunction("''");

        f = headblk.addField("SCONTRACT");
        f.setSize(6);
        f.setHidden();
        f.setReadOnly();
        f.setFunction("''");
        f.setUpperCase();

        f = headblk.addField("OWNERSHIP");
        f.setSize(20);
        f.setReadOnly();
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGOWNERSHIP: Ownership");
        f.setFunction("''");

        f = headblk.addField("OWNERSHIP_DB");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("OWNER");
        f.setSize(20);
        f.setReadOnly();
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGOWNER: Owner");
        f.setFunction("''");

        f = headblk.addField("OWNERNAME");
        f.setSize(20);
        f.setReadOnly();
        f.setMaxLength(100);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGOWNERNAME: Owner Name");
        f.setFunction("''");

        f = headblk.addField("CONDITION_CODE");
        f.setSize(10);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGCONDITIONCODE: Condition Code");
        f.setFunction("''");
        f.setUpperCase();
        f.setReadOnly();

        f = headblk.addField("CONDITION_CODE_DESC");
        f.setSize(28);
        f.setReadOnly();
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGCONDCODEDESC: Condition Code Description");
        f.setFunction("''");

        f = headblk.addField("PROJECT_ID");
        f.setSize(10);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGPROJECTNO: Project ID");
        //f.setFunction("Activity_API.Get_Project_Id(:ACTIVITY_SEQ)");
        f.setFunction("''");
        f.setMaxLength(20);
        f.setReadOnly();

        f = headblk.addField("PROJECT_DESC");        
        f.setSize(10);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGPROJECTDESC: Project Description");
        f.setFunction("''");
        //f.setFunction("PROJECT_API.Get_Name(:PROJECT_ID)");
        f.setMaxLength(10);

        f = headblk.addField("ACTIVITY_SEQ","Number");
        f.setSize(10);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGACTIVITYSEQ: Activity Sequence");
        f.setFunction("''");
        f.setMaxLength(100);
        f.setReadOnly();

        f = headblk.addField("SUPPLY_CODE");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("DB_VAL1");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("DB_VAL2");
        f.setHidden();
        f.setFunction("''");

        headblk.setView("DUAL");
        headset = headblk.getASPRowSet();

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle("PCMWMATERIALREQUISRESERVATDLGMAST: Manual Reservation");

        headbar = mgr.newASPCommandBar(headblk);
        headbar.defineCommand(headbar.BACK,"cancel");
        headbar.disableCommand(headbar.DUPLICATEROW);
        headbar.disableCommand(headbar.EDITROW);
        headbar.disableCommand(headbar.NEWROW); 
        headbar.disableCommand(headbar.DELETE); 
        headbar.disableCommand(headbar.FIND); 
        headbar.disableCommand(headbar.BACK);

        itemblk0 = mgr.newASPBlock("ITEM0");

        f = itemblk0.addField("ITEM0_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk0.addField("ITEM0_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk0.addField("ITEM0_NQUANTITY", "Number");
        f.setSize(18);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGIT0NQTY: Reserve Quantity");
        f.setFunction("''");

        f = itemblk0.addField("PART_NO");
        f.setHidden();
        f.setReadOnly();

        f = itemblk0.addField("CONTRACT");
        f.setSize(11);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGCONT: Site");
        f.setUpperCase();
        f.setReadOnly();

        f = itemblk0.addField("LOCATION_NO");
        f.setSize(11);
        f.setMandatory();
        f.setReadOnly();
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGLOCNO: Location");

        f = itemblk0.addField("LOCWAREHOUSE");
        f.setSize(13);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGLOCWH: Warehouse");
        f.setReadOnly();
        f.setFunction("Inventory_Location_API.Get_Warehouse(:CONTRACT,:LOCATION_NO)");

        f = itemblk0.addField("LOCBAY");
        f.setSize(6);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGLOCBAY: Bay");
        f.setReadOnly();
        f.setFunction("Inventory_Location_API.Get_Bay_No(:CONTRACT,:LOCATION_NO)");

        f = itemblk0.addField("LOCROW");
        f.setSize(6);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGLOCROW: Row");
        f.setReadOnly();
        f.setFunction("Inventory_Location_API.Get_Row_No(:CONTRACT,:LOCATION_NO)");

        f = itemblk0.addField("LOCTIER");
        f.setSize(6);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGLOCTIER: Tier");
        f.setReadOnly();
        f.setFunction("Inventory_Location_API.Get_Tier_No(:CONTRACT,:LOCATION_NO)");

        f = itemblk0.addField("LOCBIN");
        f.setSize(6);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGLOCBIN: Bin");
        f.setReadOnly();
        f.setFunction("Inventory_Location_API.Get_Bin_No(:CONTRACT,:LOCATION_NO)");

        f = itemblk0.addField("LOT_BATCH_NO");
        f.setSize(14);
        f.setMandatory();
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGLOTBATNO: Lot/Batch No");
        f.setReadOnly();

        f = itemblk0.addField("SERIAL_NO");
        f.setSize(10);
        f.setMaxLength(50);
        f.setMandatory();
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGSERNO: Serial No");
        f.setReadOnly();
        f.setUpperCase();

        f = itemblk0.addField("CONDITIONCODE");
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGCONDITIONCODE: Condition Code");
        f.setFunction("''");
        f.setFunction("Condition_Code_Manager_API.Get_Condition_Code(:PART_NO, :SERIAL_NO, :LOT_BATCH_NO)");
        f.setReadOnly();
        f.setSize(15);

        f = itemblk0.addField("CONDDESC");
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGCONDDESC: Condition Code Description");
        f.setFunction("CONDITION_CODE_API.GET_DESCRIPTION(Condition_Code_Manager_API.Get_Condition_Code(:PART_NO, :SERIAL_NO, :LOT_BATCH_NO))");
        f.setSize(31);
        f.setReadOnly();

        f = itemblk0.addField("WAIV_DEV_REJ_NO");
        f.setSize(11);
        f.setReadOnly();
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGWDREJNO: W/D/R No");

        f = itemblk0.addField("AVAILABILITY_CONTROL_ID");
        f.setSize(11);
        f.setReadOnly();
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGAVAILABILITYCONTROLID: Availability Control Id");

        f = itemblk0.addField("AVAILABILITY_CONTROL_ID_DESC");
        f.setSize(35);
        f.setReadOnly();
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGAVAILABILITYCONTROLIDDESC: Availability Control Description");
        f.setFunction("PART_AVAILABILITY_CONTROL_API.Get_Description(:AVAILABILITY_CONTROL_ID)");

        f = itemblk0.addField("CONFIGURATION_ID");
        f.setSize(28);
        f.setReadOnly();
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGCONFIGURATIONID: Configuration Id");

        f = itemblk0.addField("QTY_AVAILABLE","Number");
        f.setSize(20);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGQTYAVAIL: Quantity Available");
        f.setReadOnly();
        f.setFunction("QTY_ONHAND - QTY_RESERVED");

        f = itemblk0.addField("ENG_CHG_LEVEL");
        f.setSize(11);
        f.setMandatory();
        f.setHidden();
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGENGCHGLVL: Eng Chg Level");

        //Bug 66456, Start, Added check on PROJ
        if (mgr.isModuleInstalled("PROJ"))
        {
            f = itemblk0.addField("ITEM0_ACTIVITY_SEQ","Number");
            f.setSize(11);
            f.setDbName("ACTIVITY_SEQ");
            f.setReadOnly();
            f.setLabel("PCMWMATERIALREQUISRESERVATDLGITMACTIVITYSEQ: Activity Seq");
    
            f = itemblk0.addField("PROGRAM_ID");
            f.setSize(10);
            f.setFunction("Activity_API.Get_Program_Id(:ACTIVITY_SEQ)");
            f.setLabel("PCMWMATERIALREQUISRESERVATDLGPROGID: Program ID");
            f.setReadOnly();
            f.setDefaultNotVisible();
    
            f = itemblk0.addField("PROGRAM_DESC");
            f.setSize(10);
            f.setFunction("Activity_API.Get_Program_Description(:ACTIVITY_SEQ)");
            f.setLabel("PCMWMATERIALREQUISRESERVATDLGPROGDESC: Program Description");
            f.setReadOnly();
            f.setDefaultNotVisible();
    
            f = itemblk0.addField("ITEM0_PROJECT_ID");
            f.setSize(11);
            f.setDbName("PROJECT_ID");
            f.setReadOnly();
            f.setLabel("PCMWMATERIALREQUISRESERVATDLGITEM0PROJECTID: Project ID");
    
            f = itemblk0.addField("ITEM0_PROJECT_DESC");
            f.setSize(10);
            f.setLabel("PCMWMATERIALREQUISRESERVATDLGPROJECTDESC: Project Description");
            f.setFunction("Activity_API.Get_Project_Name(:ACTIVITY_SEQ)");
            f.setReadOnly();
    
            f = itemblk0.addField("SUB_PROJECT_ID");
            f.setSize(10);
            f.setLabel("PCMWMATERIALREQUISRESERVATDLGSUBPROJID: Sub Project ID");
            f.setFunction("Activity_API.Get_Sub_Project_Id(:ACTIVITY_SEQ)");
            f.setReadOnly();
            f.setDefaultNotVisible();
    
            f = itemblk0.addField("SUB_PROJECT_DESC");
            f.setSize(10);
            f.setLabel("PCMWMATERIALREQUISRESERVATDLGSUBPRDESC: Sub Project Description");
            f.setFunction("Activity_API.Get_Sub_Project_Description(:ACTIVITY_SEQ)");
            f.setReadOnly();
            f.setDefaultNotVisible();
    
            f = itemblk0.addField("ACTIVITY_ID");
            f.setSize(10);
            f.setLabel("PCMWMATERIALREQUISRESERVATDLGACTID: Activity ID");
            f.setFunction("Activity_API.Get_Activity_No(:ACTIVITY_SEQ)");
            f.setReadOnly();
            f.setDefaultNotVisible();
    
            f = itemblk0.addField("ACTIVITY_DESC");
            f.setSize(10);
            f.setLabel("PCMWMATERIALREQUISRESERVATDLGACTIDESC: Activity Description");
            f.setFunction("Activity_API.Get_Description(:ACTIVITY_SEQ)");
            f.setReadOnly();
            f.setDefaultNotVisible();
        }
        //Bug 66456, End

        f = itemblk0.addField("WO_NO");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("LINE_ITEM_NO");
        f.setHidden();
        f.setFunction("''");

        itemblk0.setView("INVENTORY_PART_IN_STOCK_NOPAL");
        itemblk0.defineCommand("INVENTORY_PART_IN_STOCK_API","New__,Modify__,Remove__");
        itemset0 = itemblk0.getASPRowSet();

        itembar0 = mgr.newASPCommandBar(itemblk0);
        itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
        itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
        itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");
        itembar0.defineCommand(itembar0.SAVERETURN,"ok"); 
        itembar0.disableCommand(itembar0.NEWROW);
        itembar0.disableCommand(itembar0.DUPLICATEROW);
        itembar0.disableCommand(itembar0.DELETE); 
        itembar0.enableCommand(itembar0.FIND);
        itembar0.enableRowStatus();

        itemtbl0 = mgr.newASPTable(itemblk0);
        itemtbl0.setTitle("PCMWMATERIALREQUISRESERVATDLGITM: Manual Reservation Items");

        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
        itemlay0.setDialogColumns(2);
    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWMATERIALREQUISRESERVATDLGTITLE: Manual Reservation";
    }

    protected String getTitle()
    {
        return "PCMWMATERIALREQUISRESERVATDLGTITLE: Manual Reservation";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.show());

        if (itemlay0.isVisible())
            appendToHTML(itemlay0.show());

        appendToHTML("<br>\n");
        appendToHTML(fmt.drawSubmit("BACK",mgr.translate("PCMWMATERIALREQUISRESERVATDLGBACK: Back"),""));
        
        if (refreshMain)
        {
           ctx.writeFlag("REFRESHMAIN",false);
           if ("MaintMaterialReqLineOvw.page".equals(qrystr)) {
              appendDirtyJavaScript("window.opener.refresh();\n");
           }
           else {
            appendDirtyJavaScript("  window.open('");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(qrystr));	//XSS_Safe AMNILK 20070717
            appendDirtyJavaScript("' + \"&OKMANRESERVE=1\",'");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(frmname));	//XSS_Safe AMNILK 20070717
            appendDirtyJavaScript("',\"\");\n");
           }
        }

        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(okClose));		//XSS_Safe AMNILK 20070717
        appendDirtyJavaScript("' == \"TRUE\")\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("  window.close();\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("if('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(cancelWin));	//XSS_Safe AMNILK 20070717
        appendDirtyJavaScript("' == 'TRUE')\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("  self.close();\n");
        appendDirtyJavaScript("} \n");
    }
}
