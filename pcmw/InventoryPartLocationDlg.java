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
*  File        : InventoryPartLocationDlg.java 
*  Created     : ASP2JAVA Tool  010220
*  Modified    :  
*  BUNILK  010222  Corrected some conversion errors.
*  BUNILK  011016  Modified form so that to refresh calling form for each 
*                  issue of material.  
*  GACOLK  021204  Set Max Length of SERIAL_NO to 50
*  RASELK  030113  Added two fields 'Condition Code' & 'Condition Code Description' 
*  CHCRLK  030506  Added Condition Code and Condition Code Description to the header.
*  ARWILK  031218  Edge Developments - (Removed clone and doReset Methods)
*  BUNILK  040625  Modified ok() method.  
*  VAGULK  040723  Added fields for Project group,Changed the view to INVENTORY_PART_IN_STOCK_NOPAL as in Centura,SCME612 - Project Inventory.
*  BUNILK  040827  Modified run(), okFind() and okFindITEM0() methods so that to filter by supply_code.
*  SHAFLK  040812  Bug 45904, Modified method ok().
*  NIJALK  041008  Merged 45904.      
*  NAMELK  041105  Duplicated and Non Standard Translation Tags Corrected. 
*  ARWILK  041115  Replaced getContents with printContents.
*  NIJALK  041116  Replaced of Inventory_Part_Location_API methods from Inventory_Part_In_Stock_API methods
*  SHAFLK  050420  Bug 50710, Modified method preDefine().
*  DiAmlk  050518  Merged the corrections done in LCS Bug ID:50710.
*  NIJALK  050808  Bug 126169: Modified okFindITEM0().
*  SHAFLK  051205  Bug 53956, Modified method okFindITEM0().
*  NIJALK  051222  Merged bug 53956.
*  SHAFLK  051229  Bug 54562, Modified method preDefine() and ok().
*  NIJALK  060118  Merged bug 54562.
*  NIJALK  060210  Call 133546: Renamed "STANDARD_INVENTORY" with "INVENT_ORDER".
*  SULILK  060222  Call 134906: Modified preDefine(), run().
*  NIJALK  060311  Call 136946: Modified run().
* ----------------------------------------------------------------------------
*  NIJALK  060510  Bug 57256, Modified okFindITEM0(), preDefine(), run().
*  NIJALK  060515  Bug 56688, Modified ok().
*  AMNILK  060629  Merged with SP1 APP7.
*  DIAMLK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMNILK  060906  Merged Bug Id: 58216.
*  ASSALK  070713  Webification - Modified printContents();
*  AMNILK  070716  Eliminated SQL Injections.
*  AMNILK  070717  Eliminated XSS Security Vulnerability.
*  ASSALK  070917  Call 148886. Modified preDefine() and ok().
*  CHANLK  071202  Bug 68364, Change window.open to replace.
*  NIJALK  080202  Bug 66456, Modified run(), ok() and preDefine().
*  SHAFLK  090403  Bug 80347, Modified preDefine().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class InventoryPartLocationDlg extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.InventoryPartLocationDlg");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPHTMLFormatter fmt;
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

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private String creRepNonSerPath;
    private String openCreRepNonSer;
    private ASPTransactionBuffer trans;
    private String okClose;
    private String partDesc;
    private String frmname;
    private String qrystr;
    private String cancelWin;
    private String maintMatOrdNo;
    private String creRepWO;
    private String sMchCode;
    private String sMchDesc;
    private ASPBuffer buff;
    private ASPBuffer row;
    private String qtyLeft;
    private ASPQuery q;
    private ASPBuffer temp;
    private ASPCommand cmd;
    private String sCencelIssue;
    private String sWoNo;  
    private String sLineItemNo; 
    private String sPartNo;  
    private String sContract;
    private String sOkissue; 
    private boolean refreshMain;
    private String condCode;
    private String condCodeDesc;
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
    public InventoryPartLocationDlg(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        sWoNo = "";
        sLineItemNo="";
        sLineItemNo = "";
        sPartNo = "";
        sPartNo = "";
        sContract = "";
        sContract = "";
        creRepNonSerPath= "";
        creRepNonSerPath = openCreRepNonSer;
        openCreRepNonSer = "";
        condCode = "";
        condCodeDesc = "";
        sOwnership = "";
        sOwner = "";
        sOwnerName = "";
        ASPManager mgr = getASPManager();

        mgr.setPageExpiring();
        fmt = mgr.newASPHTMLFormatter();   
        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();

        okClose = ctx.readValue("OKCLOSE","FALSE");
        sWoNo = ctx.readValue("CTXWONO",sWoNo);
        sLineItemNo = ctx.readValue("CTXLINEITEM",sLineItemNo);
        sPartNo = ctx.readValue("CTXPART",sPartNo);
        partDesc = ctx.readValue("CTXPARTDESC","");
        sContract = ctx.readValue("CTXCON",sContract);
        frmname= ctx.readValue("FRMNAME","");
        qrystr = ctx.readValue("QRYSTR","");
        cancelWin = ctx.readValue("CANCELWIN","");
        maintMatOrdNo = ctx.readValue("MAINTMATORDNO","");
        creRepWO = ctx.readValue("CTXAUTOREP","");
        sMchCode = ctx.readValue("CTXMCHCODE","");
        sMchDesc = ctx.readValue("CTXMCHDESC","");
        refreshMain = ctx.readFlag("REFRESHMAIN",false);
        condCode = ctx.readValue("CONDCODE",condCode);
        condCodeDesc = ctx.readValue("CONDCODEDESC",condCodeDesc);
        sOwnership = ctx.readValue("CTXOWNERSHIP",sOwnership);
        sOwner = ctx.readValue("CTXOWNER",sOwner);
        sOwnerName = ctx.readValue("CTXOWNERNAME",sOwnerName);

        if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();
        else if (mgr.commandBarActivated())
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
            sWoNo = mgr.readValue("WO_NO");
            sLineItemNo = mgr.readValue("LINE_ITEM_NO");
            sPartNo = mgr.readValue("PART_NO");
            sContract = mgr.readValue("CONTRACT");
            qrystr = mgr.readValue("QRYSTR");     
            frmname = mgr.readValue("FRMNAME");
            qtyLeft = mgr.readValue("QTYLEFT");
            partDesc = mgr.readValue("DESCRIPTION");
            maintMatOrdNo = mgr.readValue("MAINTMATORDNO");
            creRepWO = mgr.readValue("CREREPWO");
            sMchCode = mgr.readValue("MCH_CODE");
            sMchDesc = mgr.readValue("MCH_DESCRIPTION");
            sOwnership = mgr.getQueryStringValue("OWNERSHIP");
            sOwner = mgr.getQueryStringValue("OWNER");
            sOwnerName = mgr.getQueryStringValue("OWNERNAME");

            if (!mgr.isEmpty(mgr.getQueryStringValue("HEAD_CONDITION_CODE")))
                condCode = mgr.readValue("HEAD_CONDITION_CODE");
            else if (!mgr.isEmpty(mgr.getQueryStringValue("CONDITION_CODE")))
                condCode = mgr.readValue("CONDITION_CODE");
            else
                condCode = "";

            if (!mgr.isEmpty(mgr.getQueryStringValue("HEAD_CONDITION_CODE_DESC")))
                condCodeDesc =  mgr.readValue("HEAD_CONDITION_CODE_DESC");
            else if (!mgr.isEmpty(mgr.getQueryStringValue("CONDDESC")))
                condCodeDesc = mgr.readValue("CONDDESC");
            else
                condCodeDesc = "";

            if ("NULL".equals(condCode) || "null".equals(condCode))
                condCode = "";
            if ("NULL".equals(condCodeDesc) || "null".equals(condCodeDesc))
                condCodeDesc = "";

            if (!mgr.isEmpty(maintMatOrdNo))
            {
		// SQLInjection_Safe AMNILK 20070716
                trans.addQuery("ACTSEQ","SELECT ACTIVITY_SEQ FROM MAINT_MATERIAL_REQUISITION WHERE MAINT_MATERIAL_ORDER_NO = ?").addParameter("DUMMY",maintMatOrdNo);

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


            okFind();
        }
        else if (mgr.buttonPressed("BACK"))
            back();

        adjust();

        ctx.writeValue("CTXWONO",sWoNo);
        ctx.writeValue("CTXLINEITEM",sLineItemNo);
        ctx.writeValue("CTXPART",sPartNo);
        ctx.writeValue("CTXPARTDESC",partDesc);
        ctx.writeValue("CTXCON",sContract);
        ctx.writeValue("FRMNAME",frmname);
        ctx.writeValue("QRYSTR",qrystr);
        ctx.writeValue("MAINTMATORDNO",maintMatOrdNo);
        ctx.writeValue("CTXAUTOREP",creRepWO);
        ctx.writeValue("CTXMCHCODE",sMchCode);
        ctx.writeValue("CTXMCHDESC",sMchDesc);
        ctx.writeFlag("REFRESHMAIN",refreshMain);
        ctx.writeValue("CONDCODE",condCode);
        ctx.writeValue("CONDCODEDESC",condCodeDesc);
        ctx.writeValue("CTXOWNERSHIP",sOwnership);
        ctx.writeValue("CTXOWNER",sOwner);
        ctx.writeValue("CTXOWNERNAME",sOwnerName);
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void  okFind()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        q = trans.addEmptyQuery(headblk);
        q.includeMeta("ALL");
        mgr.submit(trans);

        temp = headset.getRow();

        temp.setValue("WO_NO",sWoNo);
        temp.setValue("LINE_ITEM_NO",sLineItemNo);
        temp.setValue("PART_NO",sPartNo);
        temp.setValue("SPARE_CONTRACT",sContract);
        temp.setValue("QTY_LEFT",qtyLeft);
        temp.setValue("DESCRIPTION",partDesc);
        temp.setValue("HEAD_CONDITION_CODE",condCode);
        temp.setValue("HEAD_CONDITION_CODE_DESC",condCodeDesc);
        temp.setValue("PROJECT_ID",sProjectId);     
        temp.setValue("PROJECT_DESC",sProjectDesc);
        temp.setValue("ACTIVITY_SEQ",sActivitySeq);

        headset.setRow(temp);

        okFindITEM0();
    }

//-----------------------------------------------------------------------------
//-----------------------  CMDBAR SEARCH-IT FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

    public void  okFindITEM0()
    {
        ASPManager mgr = getASPManager();

        String sWhereStmt = "";
        String sOwingCustomerNo = "";
        String sConsignmentStock = "";

        //Bug 58216 start

        if (mgr.isEmpty(condCode))                 
            sWhereStmt = "PART_NO= ? AND CONTRACT= ? AND QTY_ONHAND > 0";
        else
            sWhereStmt = "PART_NO= ? AND CONTRACT= ? AND QTY_ONHAND > 0 AND Condition_Code_Manager_API.Get_Condition_Code( PART_NO, SERIAL_NO, LOT_BATCH_NO) = ?";

        if (!mgr.isEmpty(sProjectId) && sSupplyCode.equals(sProjectInventory))
            sWhereStmt = sWhereStmt +  " AND PROJECT_ID = ?";
        else
            sWhereStmt = sWhereStmt +  " AND PROJECT_ID IS NULL";

        trans.clear();
        cmd = trans.addCustomFunction("OWNDB","PART_OWNERSHIP_API.Encode","OWNERSHIP_DB");
        cmd.addParameter("OWNERSHIP",sOwnership);
        trans = mgr.perform(trans);
        String ownership_db = trans.getValue("OWNDB/DATA/OWNERSHIP_DB");

        if ("COMPANY OWNED".equals(ownership_db))
        {
            sConsignmentStock = "CONSIGNMENT";
            sWhereStmt = sWhereStmt +  " AND (PART_OWNERSHIP_DB = ? OR PART_OWNERSHIP_DB = ?)";
        }
        else if ("CUSTOMER OWNED".equals(ownership_db))
        {
            sOwingCustomerNo = sOwner;
            sWhereStmt = sWhereStmt +  " AND PART_OWNERSHIP_DB = ?";
        }

        if (!mgr.isEmpty(sOwingCustomerNo))
            sWhereStmt = sWhereStmt +  " AND OWNING_CUSTOMER_NO = ?";
        //Bug 58216 end

        trans.clear();
        q = trans.addEmptyQuery(itemblk0);

        q.addWhereCondition(sWhereStmt);

        //Bug 58216 Start
        q.addParameter("PART_NO",sPartNo);
        q.addParameter("CONTRACT",sContract);

        if (!mgr.isEmpty(condCode))
            q.addParameter("CONDITION_CODE",condCode);

        if (!mgr.isEmpty(sProjectId) && sSupplyCode.equals(sProjectInventory))
            q.addParameter("PROJECT_ID",sProjectId);

        if ("COMPANY OWNED".equals(ownership_db))
        {
            q.addParameter("PART_OWNERSHIP_DB",ownership_db);
            q.addParameter("PART_OWNERSHIP_DB",sConsignmentStock);
        }
        else if ("CUSTOMER OWNED".equals(ownership_db))
        {
            q.addParameter("PART_OWNERSHIP_DB",ownership_db);
        }

        if (!mgr.isEmpty(sOwingCustomerNo))
            q.addParameter("OWNING_CUSTOMER_NO",sOwingCustomerNo);
        //Bug 58216 end

        q.includeMeta("ALL");
        q.setOrderByClause("SERIAL_NO");
        mgr.querySubmit(trans,itemblk0);

        trans.clear();

        int numOfRowItem = itemset0.countRows();
        int currrow = itemset0.getCurrentRowNo();

        for (int i=0;i<numOfRowItem;i++)
        {
            cmd = trans.addCustomFunction("QTYASSIGNED","Work_Order_Part_Alloc_API.Get_Assigned_In_Loc","QUANTITY_ASSIGNED");         
            cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));                                                           
            cmd.addParameter("LINE_ITEM_NO",headset.getRow().getValue("LINE_ITEM_NO")); 
            cmd.addParameter("LOCATION_NO",itemset0.getRow().getValue("LOCATION_NO"));
            cmd.addParameter("SERIAL_NO",itemset0.getRow().getValue("SERIAL_NO")); 
            cmd.addParameter("LOT_BATCH_NO",itemset0.getRow().getValue("LOT_BATCH_NO"));                                            
            cmd.addParameter("WAIV_DEV_REJ_NO",itemset0.getRow().getValue("WAIV_DEV_REJ_NO"));                                            

            trans = mgr.perform(trans);

            double sQtyAssigned = trans.getNumberValue("QTYASSIGNED/DATA/QUANTITY_ASSIGNED"); 
            if (isNaN(sQtyAssigned))
                sQtyAssigned = 0;

            trans.clear(); 

            row = itemset0.getRow();
            row.setNumberValue("QUANTITY_ASSIGNED",sQtyAssigned);
            itemset0.setRow(row);

            itemset0.next();      
        }

        itemset0.goTo(currrow);
    }

    public void  countFindITEM0()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk0);
        if (mgr.isEmpty(condCode))
        {
            //Bug 58216 start
            q.addWhereCondition("PART_NO= ? AND CONTRACT= ? AND QTY_ONHAND > 0");
            q.addParameter("PART_NO",sPartNo);
            q.addParameter("CONTRACT",sContract);
            //Bug 58216 end
        }
        else
        {
            //Bug 58216 start
            q.addWhereCondition("PART_NO= ? AND CONTRACT= ? AND QTY_ONHAND > 0 AND Condition_Code_Manager_API.Get_Condition_Code( PART_NO, SERIAL_NO, LOT_BATCH_NO) = ?");
            q.addParameter("PART_NO",sPartNo);
            q.addParameter("CONTRACT",sContract);
            q.addParameter("CONDITION_CODE",condCode);
            //bug 58216 end
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

    public void  ok()
    {
        ASPManager mgr = getASPManager();

        double nQuantity = mgr.readNumberValue("ITEM0_NQUANTITY");
        if (isNaN(nQuantity))
            nQuantity = 0;

        double nAvailable = mgr.readNumberValue("QTY_AVAILABLE");
        if (isNaN(nAvailable))
            nAvailable = 0;

        double nQtyLeft = mgr.readNumberValue("QTY_LEFT");
        if (isNaN(nQtyLeft))
            nQtyLeft = 0;

        double nQtyAsssign = mgr.readNumberValue("QUANTITY_ASSIGNED");
        if (isNaN(nQtyAsssign))
            nQtyAsssign = 0;

        if (nQuantity != 0)
        {
            if ((nQuantity > (nAvailable + nQtyAsssign))||(nQuantity > nQtyLeft))
            {
                double nQtyMax = 0;   
                if (nAvailable < nQtyLeft)
                    nQtyMax = nAvailable + nQtyAsssign;
                else
                    nQtyMax = nQtyLeft;    

                String nQtyMaxStr = String.valueOf(nQtyMax);

                mgr.showAlert(mgr.translate("PCMWINVENTORYPARTLOCATIONDLGQTYOVERFLOW: Maximum quantity is &1 ",nQtyMaxStr));

            }
            else
            {
                String nQuantityStr = mgr.readValue("ITEM0_NQUANTITY");
                
                cmd = trans.addCustomCommand("RES","MAINT_MATERIAL_REQ_LINE_API.Make_Manual_Issue_Detail");
                cmd.addParameter("QTY_ISSUED");
                cmd.addParameter("MAINT_MATERIAL_ORDER_NO",maintMatOrdNo);
                cmd.addParameter("LINE_ITEM_NO",sLineItemNo);
                cmd.addParameter("LOCATION_NO", itemset0.getRow().getValue("LOCATION_NO"));
                cmd.addParameter("LOT_BATCH_NO", itemset0.getRow().getValue("LOT_BATCH_NO"));
                cmd.addParameter("SERIAL_NO", itemset0.getRow().getValue("SERIAL_NO"));
                cmd.addParameter("ENG_CHG_LEVEL", itemset0.getRow().getValue("ENG_CHG_LEVEL"));
                cmd.addParameter("WAIV_DEV_REJ_NO", itemset0.getRow().getValue("WAIV_DEV_REJ_NO"));
                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))
                {
                    cmd.addParameter("PROJECT_ID", itemset0.getRow().getValue("PROJECT_ID"));
                    cmd.addParameter("ACTIVITY_SEQ", itemset0.getRow().getValue("ACTIVITY_SEQ"));
                }
                else
                {
                    cmd.addParameter("PROJECT_ID", "");
                    cmd.addParameter("ACTIVITY_SEQ", "");
                }
                //Bug 66456, End
                cmd.addParameter("ITEM0_NQUANTITY",nQuantityStr);
                cmd.addParameter("PLAN_LINE_NO",itemset0.getRow().getValue("PLAN_LINE_NO"));
                cmd.addParameter("QUANTITY_ASSIGNED",itemset0.getRow().getFieldValue("QUANTITY_ASSIGNED"));
                cmd.addParameter("CONFIGURATION_ID", itemset0.getRow().getValue("CONFIGURATION_ID"));

                trans = mgr.perform(trans);

                trans.clear();

                q = trans.addEmptyQuery(headblk);
                q.includeMeta("ALL");
                mgr.submit(trans);

                temp = headset.getRow();

                double nQtyLeftNew = nQtyLeft - nQuantity;

                temp.setValue("WO_NO",sWoNo);
                temp.setValue("LINE_ITEM_NO",sLineItemNo);
                temp.setValue("PART_NO",sPartNo);
                temp.setValue("SPARE_CONTRACT",sContract);
                temp.setNumberValue("QTY_LEFT",nQtyLeftNew);
                temp.setValue("DESCRIPTION",partDesc);

                headset.setRow(temp);

                okFindITEM0();

                refreshMain = true;

                if ("TRUE".equals(creRepWO))
                {
                    openCreRepNonSer = "TRUE";
                    creRepNonSerPath = "CreateRepairWorkOrderForNonSerialParts.page?WO_NO="+sWoNo+
                                       "&MCH_CODE="+mgr.URLEncode(sMchCode)+
                                       "&DESCRIPTION="+mgr.URLEncode(sMchDesc)+
                                       "&CONTRACT="+mgr.URLEncode(sContract)+
                                       "&PART_NO="+mgr.URLEncode(sPartNo)+
                                       "&SPAREDESCRIPTION="+mgr.URLEncode(partDesc)+
                                       "&SPARE_CONTRACT="+mgr.URLEncode(sContract)+
                                       "&CREREPWO="+creRepWO+
                                       "&COUNT="+nQuantity;
                }
            }
        }

    }

    public void  back()
    {
        okClose = "TRUE";
    }


    public void  preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("OBJVERSION");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("PART_NO");
        f.setSize(14);
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGPARTNO: Part No");
        f.setUpperCase();
        f.setReadOnly();
        f.setFunction("''");

        f = headblk.addField("DESCRIPTION");
        f.setSize(28);
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGDESCRIPTION: Part Description");
        f.setReadOnly();
        f.setFunction("''");

        f = headblk.addField("QTY_LEFT", "Number");
        f.setSize(11);
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGQTYLEFT: Quantity Left");
        f.setReadOnly();
        f.setFunction("''");

        f = headblk.addField("HEAD_CONDITION_CODE");
        f.setSize(10);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGCONDITIONCODE: Condition Code");
        f.setFunction("''");
        f.setUpperCase();
        f.setReadOnly();

        f = headblk.addField("HEAD_CONDITION_CODE_DESC");
        f.setSize(28);
        f.setReadOnly();
        f.setLabel("PCMWMATERIALREQUISRESERVATDLGCONDCODEDESC: Condition Code Description");
        f.setFunction("''");

        f = headblk.addField("PROJECT_ID");
        f.setSize(10);
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGPROJECTNO: Project ID");
        f.setFunction("''");
        f.setMaxLength(20);
        f.setReadOnly();

        f = headblk.addField("PROJECT_DESC");
        f.setSize(10);
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGPROJECTDESC: Project Description");
        f.setFunction("''");
        f.setMaxLength(10);

        f = headblk.addField("ACTIVITY_SEQ","Number");
        f.setSize(10);
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGACTIVITYSEQ: Activity Sequence");
        f.setFunction("''");
        f.setMaxLength(100);
        f.setReadOnly();

        f = headblk.addField("SPARE_CONTRACT");
        f.setSize(6);
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("WO_NO");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("LINE_ITEM_NO");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("MAINT_MATERIAL_ORDER_NO");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("SUPPLY_CODE");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("DB_VAL1");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("DB_VAL2");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("PLAN_LINE_NO","Number","#");
        f.setHidden();
        f.setFunction("''");

        //Bug 58216 start
        f = headblk.addField("DUMMY", "String");
        f.setHidden();
        f.setFunction("''");
        //Bug 58216 end

        f = headblk.addField("QTY_ISSUED","Number");
        f.setHidden();
        f.setFunction("''");

        headblk.setView("DUAL");
        headset = headblk.getASPRowSet();

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle("PCMWINVENTORYPARTLOCATIONDLGMAST: Manual Issue");

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
        f.setSize(15);
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGITEM0NQUANTITY: Issue Qty");
        f.setFunction("''");
        //f.setDefaultNotVisible();   

        f = itemblk0.addField("LOCATION_NO");
        f.setSize(11);
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGLOCATIONNO: Location");
        f.setReadOnly();

        f = itemblk0.addField("CONTRACT");
        f.setSize(11);
        f.setMandatory();
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGCONTRACT: Site");
        f.setUpperCase();
        f.setReadOnly();

        f = itemblk0.addField("QUANTITY_ASSIGNED","Number");
        f.setSize(14);
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGQUANTITYASSIGNED: Qty Assigned");
        f.setFunction("''");
        f.setReadOnly();

        f = itemblk0.addField("LOCWAREHOUSE");
        f.setSize(13);
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGLOCWAREHOUSE: Warehouse");
        f.setFunction("Inventory_Location_API.Get_Warehouse(:CONTRACT,:LOCATION_NO)");
        f.setReadOnly();

        f = itemblk0.addField("LOCBAY");
        f.setSize(6);
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGLOCBAY: Bay");
        f.setFunction("Inventory_Location_API.Get_Bay_No(:CONTRACT,:LOCATION_NO)");
        f.setReadOnly();
        //f.setDefaultNotVisible();

        f = itemblk0.addField("LOCROW");
        f.setSize(6);
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGLOCROW: Row");
        f.setFunction("Inventory_Location_API.Get_Row_No(:CONTRACT,:LOCATION_NO)");
        f.setReadOnly();
        //f.setDefaultNotVisible();   

        f = itemblk0.addField("LOCTIER");
        f.setSize(6);
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGLOCTIER: Tier");
        f.setFunction("Inventory_Location_API.Get_Tier_No(:CONTRACT,:LOCATION_NO)");
        f.setReadOnly();

        f = itemblk0.addField("LOCBIN");
        f.setSize(6);
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGLOCBIN: Bin");
        f.setFunction("Inventory_Location_API.Get_Bin_No(:CONTRACT,:LOCATION_NO)");
        f.setReadOnly();
        //f.setDefaultNotVisible();   

        f = itemblk0.addField("LOT_BATCH_NO");
        f.setSize(14);
        f.setMandatory();
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGLOTBATCHNO: Lot/Batch No");
        f.setReadOnly();
        //f.setDefaultNotVisible();

        f = itemblk0.addField("CONDITION_CODE");
        f.setSize(10);
        f.setFunction("Condition_Code_Manager_API.Get_Condition_Code(:PART_NO,:SERIAL_NO,:LOT_BATCH_NO)");
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGCONDITIONCODE: Condition Code");
        f.setReadOnly();
        //f.setDefaultNotVisible();

        f = itemblk0.addField("CODE_DESC");
        f.setSize(35);
        f.setFunction("CONDITION_CODE_API.GET_DESCRIPTION(Condition_Code_Manager_API.Get_Condition_Code(:PART_NO,:SERIAL_NO,:LOT_BATCH_NO))");
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGCODEDESC: Condition Code Description");
        f.setReadOnly();
        //f.setDefaultNotVisible(); 

        f = itemblk0.addField("CONFIGURATION_ID");
        f.setSize(11);
        f.setReadOnly();
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGCONFIGURATION_ID: Configuration ID");

        f = itemblk0.addField("SERIAL_NO");
        f.setSize(10);
        f.setMaxLength(50);
        f.setMandatory();
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGSERIALNO: Serial No");
        f.setUpperCase();
        f.setReadOnly();

        f = itemblk0.addField("WAIV_DEV_REJ_NO");
        f.setSize(11);
        f.setReadOnly();
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGWAIVDEVREJNO: W/D/R No");

        f = itemblk0.addField("AVAILABILITY_CONTROL_ID");
        f.setSize(11); 
        f.setReadOnly();
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGAVAILABILITYCONTROLID: Availability Control Id");

        f = itemblk0.addField("AVAILABILITY_CONTROL_ID_DESC");
        f.setSize(35); 
        f.setReadOnly();
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGAVAILABILITYCONTROLIDDESC: Availability Control Description");
        f.setFunction("PART_AVAILABILITY_CONTROL_API.Get_Description(:AVAILABILITY_CONTROL_ID)");

        f = itemblk0.addField("QTY_AVAILABLE","Number");
        f.setSize(20);
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGQTYAVAILABLE: Quantity Available");
        f.setReadOnly();
        f.setFunction("QTY_ONHAND - QTY_RESERVED");
        f.setReadOnly();

        f = itemblk0.addField("ENG_CHG_LEVEL");
        f.setSize(11);
        f.setMandatory();
        f.setHidden();
        f.setDefaultNotVisible();
        f.setLabel("PCMWINVENTORYPARTLOCATIONDLGENGCHGLEVEL: Eng Chg Level");

        //Bug 66456, Start, Added check on PROJ
        if (mgr.isModuleInstalled("PROJ"))
        {
            f = itemblk0.addField("ITEM0_ACTIVITY_SEQ","Number");
            f.setSize(11);
            f.setDbName("ACTIVITY_SEQ");
            f.setReadOnly();
            f.setLabel("PCMWINVENTORYPARTLOCATIONDLGACTSEQ: Activity Seq");
    
            f = itemblk0.addField("PROGRAM_ID");
            f.setSize(10);
            f.setFunction("Activity_API.Get_Program_Id(:ACTIVITY_SEQ)");
            f.setLabel("PCMWINVENTORYPARTLOCATIONDLGPROGID: Program ID");
            f.setReadOnly();
            f.setDefaultNotVisible();
    
            f = itemblk0.addField("PROGRAM_DESC");
            f.setSize(10);
            f.setFunction("Activity_API.Get_Program_Description(:ACTIVITY_SEQ)");
            f.setLabel("PCMWINVENTORYPARTLOCATIONDLGPROGDESC: Program Description");
            f.setReadOnly();
            f.setDefaultNotVisible();
    
            f = itemblk0.addField("ITEM0_PROJECT_ID");
            f.setSize(11);
            f.setDbName("PROJECT_ID");
            f.setReadOnly();
            f.setLabel("PCMWINVENTORYPARTLOCATIONDLGITEM0PROJECTID: Project ID");
    
            f = itemblk0.addField("ITEM0_PROJECT_DESC");
            f.setSize(10);
            f.setLabel("PCMWINVENTORYPARTLOCATIONDLGPROJECTDESCRI: Project Description");
            f.setFunction("Activity_API.Get_Project_Name(:ACTIVITY_SEQ)");
            f.setReadOnly();
    
            f = itemblk0.addField("SUB_PROJECT_ID");
            f.setSize(10);
            f.setLabel("PCMWINVENTORYPARTLOCATIONDLGSUBPROJID: Sub Project ID");
            f.setFunction("Activity_API.Get_Sub_Project_Id(:ACTIVITY_SEQ)");
            f.setReadOnly();
            f.setDefaultNotVisible();
    
            f = itemblk0.addField("SUB_PROJECT_DESC");
            f.setSize(10);
            f.setLabel("PCMWINVENTORYPARTLOCATIONDLGSUBPRDESC: Sub Project Description");
            f.setFunction("Activity_API.Get_Sub_Project_Description(:ACTIVITY_SEQ)");
            f.setReadOnly();
            f.setDefaultNotVisible();
    
            f = itemblk0.addField("ACTIVITY_ID");
            f.setSize(10);
            f.setLabel("PCMWINVENTORYPARTLOCATIONDLGACTID: Activity ID");
            f.setFunction("Activity_API.Get_Activity_No(:ACTIVITY_SEQ)");
            f.setReadOnly();
            f.setDefaultNotVisible();
    
            f = itemblk0.addField("ACTIVITY_DESC");
            f.setSize(10);
            f.setLabel("PCMWINVENTORYPARTLOCATIONDLGACTIDESC: Activity Description");
            f.setFunction("Activity_API.Get_Description(:ACTIVITY_SEQ)");
            f.setReadOnly();
            f.setDefaultNotVisible(); 
        }
        //Bug 66456, End 

        f = headblk.addField("OWNERSHIP_DB");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("OWNERSHIP");
        f.setHidden();
        f.setFunction("''");

        //Bug 58216 start
        f = itemblk0.addField("OWNING_CUSTOMER_NO");
        f.setHidden();

        f = itemblk0.addField("PART_OWNERSHIP_DB");
        f.setHidden();
        //Bug 58216 end

        itemblk0.setView("INVENTORY_PART_IN_STOCK_NOPAL"); 
        itemblk0.defineCommand("INVENTORY_PART_IN_STOCK_API","New__,Modify__,Remove__");
        itemblk0.setMasterBlock(headblk);
        itemset0 = itemblk0.getASPRowSet();

        itembar0 = mgr.newASPCommandBar(itemblk0);
        itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
        itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
        itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");
        itembar0.defineCommand(itembar0.SAVERETURN,"ok","checkItem0Fields()");
        itembar0.disableCommand(itembar0.NEWROW);
        itembar0.disableCommand(itembar0.DUPLICATEROW);
        itembar0.disableCommand(itembar0.DELETE); 
        itembar0.enableCommand(itembar0.FIND);

        itembar0.enableRowStatus();

        itemtbl0 = mgr.newASPTable(itemblk0);
        itemtbl0.setTitle("PCMWINVENTORYPARTLOCATIONDLGITM: Manual Issue Items");

        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
        itemlay0.setDialogColumns(2);
    }

    public void  adjust()
    {
        ASPManager mgr = getASPManager();

        sOkissue = "  "+mgr.translate("PCMWINVENTORYPARTLOCATIONDLGOKISSUE: Ok")+"  ";
        sCencelIssue = mgr.translate("PCMWINVENTORYPARTLOCATIONDLGCANCELISSUE: Cancel");
        
    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWINVENTORYPARTLOCATIONDLGTITLE: Manual Issue";
    }

    protected String getTitle()
    {
        return "PCMWINVENTORYPARTLOCATIONDLGTITLE: Manual Issue";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.show());

        if (headlay.isSingleLayout() && (headset.countRows() > 0))
            appendToHTML(itemlay0.show());

        appendToHTML("<br>\n");
        appendToHTML(fmt.drawSubmit("BACK",mgr.translate("PCMWINVENTORYPARTLOCATIONDLGBACK: <Back"),""));

        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(openCreRepNonSer);
        appendDirtyJavaScript("' == \"TRUE\")\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   window.open('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(creRepNonSerPath));   //XSS_Safe AMNILK 20070716
        appendDirtyJavaScript("',\"createRepWONonSer\",\"alwaysRaised=yes,resizable,status=yes,scrollbars=yes,width=790,height=575\");\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(okClose));  	//XSS_Safe AMNILK 20070716
        appendDirtyJavaScript("' == \"TRUE\")\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("  window.close();\n");
        appendDirtyJavaScript("}\n");

        if (refreshMain)
        {
           ctx.writeFlag("REFRESHMAIN",false);
           if ("MaintMaterialReqLineOvw.page".equals(qrystr)) {
              appendDirtyJavaScript("window.opener.refresh();\n");
           }
           else {
//         	  Bug 68364, Start              
         	  appendDirtyJavaScript("  window.opener.location.replace('");
//         	  Bug 68364, Start              
              appendDirtyJavaScript(mgr.encodeStringForJavascript(qrystr));	//XSS_Safe AMNILK 20070716
              appendDirtyJavaScript("' + \"&OKMANISSUE=1\",'");
              appendDirtyJavaScript(mgr.encodeStringForJavascript(frmname));	//XSS_Safe AMNILK 20070716
              appendDirtyJavaScript("',\"\");\n");
           }
        }
    }
}
