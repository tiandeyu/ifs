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
*  File        : MaterialRequisReservatDlg2.java 
*  ASP2JAVA Tool  2001-02-21  - Created Using the ASP file MaterialRequisReservatDlg2.asp
*  Modified    :  2001-02-22  BUNI Corrected some conversion errors.
*                 2001-04-06  BUNI Changed default layout mode of itemblk to multi record mode
*                                  and added a back button to refesh calling form after unreserv.   
*                 2001-07-30  BUNI Added error messege when negative quntity enter for "Unreserve Qty"  
*                                  field.
*              :  2001-10-16  BUNI Modified form so that to refresh calling form for each 
*                             unreserv of material.  
*              :  2002-12-04  GACO Set Max Length of SERIAL_NO to 50
*              :  2003-01-08  Nimhlk -  Added two fields Condition Code and Condition Code Description according to specification W110 - Condition Codes.
*                 031219      ARWILK    Edge Developments - (Removed clone and doReset Methods)
*                 2004-06-25  BUNILK    Modified submit() method. 
*                 040723      VAGULK    Added fields for Project group,SCME612 - Project Inventory.
*                 040803      VAGULK    Changed view from "INVENTORY_PART_LOCATION" to "WORK_ORDER_PART_ALLOC" as in Centura.
*                 041108      NAMELK    Non Standard and Duplicated Translation Tags Corrected. 
*                 050318      NIJALK    Call 122733: Modified submit().
*                 050728      NIJALK    Bug 126022, Modified submit(),preDefine(). 
*                 051205      SHAFLK    Bug Id 53956, Changed run(),submit(), okFind() and predefine().
*                 051222      NIJALK    Merged bug 53956. 
*                 051229      SHAFLK    Bug 54562, Modified method preDefine() and submit().
*                 060118      NIJALK    Merged bug 54562.
*                 060815      AMNILK    Bug 58216, Eliminated SQL Injection security vulnerability.
*                 060904      AMNILK    Merged Bug Id: 58216.
*                 070713      ASSALK    Webification - Modified printContents();
*  		  070727      AMNILK    Eliminated XSS Security Vulnerability.
*                 080202      NIJALK    Bug 66456, Modified preDefine().
*                 090403      SHAFLK    Bug 80347, Modified preDefine().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class MaterialRequisReservatDlg2 extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.MaterialRequisReservatDlg2");

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
    private String qty;
    private String mContract;
    private ASPTransactionBuffer trans;
    private String cancelWin;
    private String okClose;
    private String qrystr;
    private String frmname;
    private ASPBuffer buff;
    private ASPBuffer row;
    private String sPartNo;
    private String sContract;
    private String qtyLeft;
    private String partDesc;
    private double maintMatOrdNo;
    private ASPTransactionBuffer secBuff;
    private ASPCommand cmd;
    private ASPQuery q;
    private ASPBuffer temp;
    private int currrow;
    private String mWoNo;
    private String mLineItemNo;
    private String mPartNo;
    private boolean refreshMain;

    //===============================================================
    // Construction 
    //===============================================================
    public MaterialRequisReservatDlg2(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        mWoNo = "";
        mLineItemNo = "";
        mLineItemNo = "";
        qty = "";
        qty = "";
        mPartNo = "";
        mContract = "";
        mContract = "";
        ASPManager mgr = getASPManager();

        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();
        fmt = mgr.newASPHTMLFormatter();   

        mWoNo = ctx.readValue("MYWONO",mWoNo);
        mLineItemNo = ctx.readValue("MYLINEITNO",mLineItemNo);
        mPartNo = ctx.readValue("MYPARTNO",mPartNo);
        mContract = ctx.readValue("MYCONTRACT",mContract);
        cancelWin = ctx.readValue("CANCELWIN","");
        okClose = ctx.readValue("OKCLOSE","FALSE");
        qrystr = ctx.readValue("QRYSTR","");
        frmname = ctx.readValue("FRMNAME","");
        sPartNo = ctx.readValue("SPARTNO","");
        sContract = ctx.readValue("SCONTRACT","");
        refreshMain = ctx.readFlag("REFRESHMAIN",false);
        qtyLeft = ctx.readValue("QTYLEFT","");
        partDesc = ctx.readValue("PARTDESC",""); 
        maintMatOrdNo = ctx.readNumber("MAINTMATORDNO"); 

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (mgr.buttonPressed("SUBMIT"))
            submit();
        else if (mgr.buttonPressed("CANCEL"))
            cancel();
        else if (mgr.dataTransfered())
        {
            buff = mgr.getTransferedData();

            row = buff.getBufferAt(0);
            mWoNo = row.getValue("WO_NO");

            row = buff.getBufferAt(1);
            mLineItemNo = row.getValue("LINE_ITEM_NO");

            okFind();
            okFindITEM0();
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        }

        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
        {
            sPartNo = mgr.readValue("PART_NO");
            sContract = mgr.readValue("CONTRACT");
            mWoNo = mgr.readValue("WO_NO");
            mLineItemNo = mgr.readValue("LINE_ITEM_NO");
            qtyLeft = mgr.readValue("QTYLEFT");
            partDesc = mgr.readValue("DESCRIPTION");
            frmname = mgr.readValue("FRMNAME");
            qrystr = mgr.readValue("QRYSTR");  
            maintMatOrdNo = mgr.readNumberValue("MAINTMATORDNO");

            okFind();
        }
        else if (mgr.buttonPressed("BACK"))
            back();

        ctx.writeValue("MYWONO",mWoNo);
        ctx.writeValue("MYLINEITNO",mLineItemNo);
        ctx.writeValue("FRMNAME",frmname);
        ctx.writeValue("QRYSTR",qrystr);
        ctx.writeValue("SPARTNO",sPartNo);
        ctx.writeValue("SCONTRACT",sContract);
        ctx.writeValue("QTYLEFT",qtyLeft);
        ctx.writeValue("PARTDESC",partDesc); 
        ctx.writeNumber("MAINTMATORDNO",maintMatOrdNo); 
        ctx.writeFlag("REFRESHMAIN",refreshMain);

    }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
// This function is used to check security status for methods which
// are in modules Order,Purch and Invent .This function is called before
// the required method is performed.  -NUPE,21-07-2000


    public boolean  checksec( String method,int ref) 
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

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void  submit()
    {
        ASPManager mgr = getASPManager();

        String error = "FALSE";

        double nQuantity = mgr.readNumberValue("ITEM0_NQUANTITY");
        if (isNaN(nQuantity))
            nQuantity = 0;

        double nAvailable = 0;

        double nQtyLeft = headset.getRow().getNumberValue("QTY_ASSIGNED");
        if (isNaN(nQtyLeft))
            nQtyLeft = 0;

        if (nQuantity < 0)
        {
            mgr.showAlert(mgr.translate("PCMWMATERIALREQUISRESERVATDLG2TEXTNEGATIVE: Not allowed to use negative values."));
        }
        else
        {
            nAvailable = itemset0.getRow().getNumberValue("QTY_ASSIGNED");
            if (isNaN(nAvailable))
                nAvailable = 0;

            if (nQuantity != 0)
            {
                if (nAvailable < nQuantity)
                    error = "TRUE";
                else
                {
                    double nNegQty = -1 * nQuantity;
                    double nDummy = 0;
                    String nIssuedQty = "0";
                    String nNegQtyStr = mgr.getASPField("QUANTITY").formatNumber(nNegQty);

                    if (checksec("INVENTORY_PART_IN_STOCK_API.Reserve_Part",1))
                    {
                        cmd = trans.addCustomCommand("GEN1","INVENTORY_PART_IN_STOCK_API.Reserve_Part");
                        cmd.addParameter("CATCH_QTY",mgr.getASPField("CATCH_QTY").formatNumber(nDummy));
                        cmd.addParameter("SPARE_CONTRACT");
                        cmd.addParameter("PART_NO");
                        cmd.addParameter("CONFIGURATION_ID",itemset0.getRow().getValue("CONFIGURATION_ID"));      
                        cmd.addParameter("LOCATION_NO",itemset0.getRow().getValue("LOCATION_NO"));
                        cmd.addParameter("LOT_BATCH_NO",itemset0.getRow().getValue("LOT_BATCH_NO"));
                        cmd.addParameter("SERIAL_NO",itemset0.getRow().getValue("SERIAL_NO"));
                        cmd.addParameter("ENG_CHG_LEVEL",itemset0.getRow().getValue("ENG_CHG_LEVEL"));
                        cmd.addParameter("WAIV_DEV_REJ_NO",itemset0.getRow().getValue("WAIV_DEV_REJ_NO"));
                        cmd.addParameter("ACTIVITY_SEQ",mgr.getASPField("ACTIVITY_SEQ").formatNumber(nDummy));
                        cmd.addParameter("QUANTITY",nNegQtyStr);
                    }

                    cmd = trans.addCustomCommand("GEN2","Work_Order_Part_Alloc_API.Update_Assigned");
                    cmd.addParameter("WO_NO",mWoNo);
                    cmd.addParameter("LINE_ITEM_NO",mLineItemNo);
                    cmd.addParameter("LOCATION_NO",itemset0.getRow().getValue("LOCATION_NO"));
                    cmd.addParameter("LOT_BATCH_NO",itemset0.getRow().getValue("LOT_BATCH_NO"));
                    cmd.addParameter("SERIAL_NO",itemset0.getRow().getValue("SERIAL_NO"));
                    cmd.addParameter("ENG_CHG_LEVEL",itemset0.getRow().getValue("ENG_CHG_LEVEL"));
                    cmd.addParameter("WAIV_DEV_REJ_NO",itemset0.getRow().getValue("WAIV_DEV_REJ_NO"));
                    cmd.addParameter("SPARE_CONTRACT");
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("ACTIVITY_SEQ",mgr.getASPField("ACTIVITY_SEQ").formatNumber(nDummy)); 
                    cmd.addParameter("CONFIGURATION_ID",itemset0.getRow().getValue("CONFIGURATION_ID"));      
                    cmd.addParameter("QTY_DIFF",nNegQtyStr);

                    cmd = trans.addCustomCommand("GEN3","Maint_Material_Req_Line_API.Modify_Issued_Impl__"); 
                    cmd.addParameter("ORD_NO",headset.getRow().getFieldValue("MAINT_MAT_ORD_NO")); 
                    cmd.addParameter("LINE_ITEM_NO",mLineItemNo);
                    cmd.addParameter("QTY_ISSUED",nIssuedQty);
                    cmd.addParameter("ITEM0_NQUANTITY",mgr.getASPField("ITEM0_NQUANTITY").formatNumber(nQuantity));
                }

            }
            if ("TRUE".equals(error))
                mgr.showAlert(mgr.translate("PCMWMATERIALREQUISRESERVATDLG2QTYRESERVED: Maximum quantity is &1 ",String.valueOf(nAvailable)));
            else
            {
                trans=mgr.perform(trans);

                double availQty = nQtyLeft - nQuantity;

                trans.clear();

                q = trans.addEmptyQuery(headblk);
                q.includeMeta("ALL");
                mgr.submit(trans);

                temp = headset.getRow();

                temp.setValue("PART_NO",sPartNo);
                temp.setValue("SPARE_CONTRACT",sContract);
                temp.setNumberValue("QTY_ASSIGNED",availQty);
                temp.setValue("DESCRIPTION",partDesc);
                temp.setNumberValue("MAINT_MAT_ORD_NO",maintMatOrdNo);

                headset.setRow(temp);

                okFindITEM0();
                refreshMain = true;
            }
        }
    }


    public void  cancel()
    {
        cancelWin = "TRUE";
    }

    public void  back()
    {
        okClose = "TRUE";
    }


//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void  okFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addEmptyQuery(headblk);
        q.includeMeta("ALL");
        mgr.submit(trans);

        temp = headset.getRow();
        double qtyLeftNum = toDouble(qtyLeft);

        temp.setValue("PART_NO",sPartNo);
        temp.setValue("SPARE_CONTRACT",sContract);
        temp.setNumberValue("QTY_ASSIGNED",qtyLeftNum);
        temp.setValue("DESCRIPTION",partDesc);
        temp.setNumberValue("MAINT_MAT_ORD_NO",maintMatOrdNo);

        headset.setRow(temp);

        okFindITEM0();
    }


    public void  countFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();
    }

//-----------------------------------------------------------------------------
//-----------------------  CMDBAR SEARCH-IT FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

    public void  okFindITEM0()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        q = trans.addEmptyQuery(itemblk0);
	//Bug 58216 Start
	q.addWhereCondition("WO_NO= ? AND LINE_ITEM_NO= ?");
	q.addParameter("WO_NO",mWoNo);
	q.addParameter("LINE_ITEM_NO",mLineItemNo);
	//Bug 58216 End
        q.includeMeta("ALL");

        mgr.submit(trans);
    }


    public void  countFindITEM0()
    {
        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk0);
	//Bug 58216 Start
        q.addWhereCondition("WO_NO= ? AND LINE_ITEM_NO= ?");
	q.addParameter("WO_NO",mWoNo);
	q.addParameter("LINE_ITEM_NO",mLineItemNo);
	//Bug 58216 End
        q.includeMeta("ALL");
        q.setSelectList("to_char(count(*)) N");

        mgr.submit(trans);

        itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
        itemset0.clear();
        headset.goTo(currrow);
    }


    public void  preDefine()
    {
        ASPManager mgr = getASPManager();


        //-------------------------------HEAD BLOCK---------------------------------------------------------


        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("OBJVERSION");
        f.setHidden();
        f.setFunction("''");   

        f = headblk.addField("PART_NO");
        f.setSize(14);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2PANO: Part No");
        f.setReadOnly();
        f.setUpperCase();
        f.setMaxLength(25);
        f.setFunction("''");

        f = headblk.addField("DESCRIPTION");
        f.setSize(28);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2DESCR: Part Description");
        f.setReadOnly();
        f.setFunction("''");

        f = headblk.addField("QTY_ASSIGNED", "Number");
        f.setSize(11);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2QTYASS: Quantity Left");
        f.setReadOnly();
        f.setFunction("''");

        f = headblk.addField("PROJECT_ID");
        f.setSize(10);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2PROJECTNO: Project ID");
        //f.setFunction("Activity_API.Get_Project_Id(:ACTIVITY_SEQ)");
        f.setFunction("''");
        f.setMaxLength(20);
        f.setReadOnly();

        f = headblk.addField("PROJECT_DESC");
        f.setSize(10);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2PROJECTDESC: Project Description");
        f.setFunction("''");
        //f.setFunction("PROJECT_API.Get_Name(:PROJECT_ID)");
        f.setMaxLength(10);

        f = headblk.addField("ACTIVITY_SEQ","Number");
        f.setSize(10);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2ACTIVITYSEQ: Activity Sequence");
        f.setFunction("''");
        f.setMaxLength(100);
        f.setReadOnly();

        f = headblk.addField("SPARE_CONTRACT");
        f.setSize(6);
        f.setHidden();
        f.setMaxLength(5);
        f.setFunction("''");

        f = headblk.addField("WO_NO");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("LINE_ITEM_NO");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("MAINT_MAT_ORD_NO","Number","#");
        f.setSize(15);
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("CATCH_QTY","Number");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("ORD_NO","Number");
        f.setFunction("''");
        f.setHidden();

        headblk.setView("DUAL");
        headset = headblk.getASPRowSet();

        headtbl = mgr.newASPTable(headblk);

        headbar = mgr.newASPCommandBar(headblk);
        headbar.disableCommand(headbar.FIND);
        headbar.disableCommand(headbar.BACKWARD);
        headbar.disableCommand(headbar.FORWARD);

        headlay = headblk.getASPBlockLayout();                
        headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
        headlay.setDialogColumns(2);


        //--------------------------------ITEM BLOCK-------------------------------------------------------

        itemblk0 = mgr.newASPBlock("ITEM0");

        f = itemblk0.addField("ITEM0_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk0.addField("ITEM0_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk0.addField("ITEM0_WO_NO");
        f.setDbName("WO_NO");
        f.setHidden();

        f = itemblk0.addField("ITEM0_LINE_ITEM_NO");
        f.setDbName("LINE_ITEM_NO");
        f.setHidden();

        f = itemblk0.addField("ITEM0_NQUANTITY", "Number");
        f.setSize(15);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2UNQTY: Unreserve Qty");
        f.setFunction("''");

        f = itemblk0.addField("ITEM0_QTY_ASSIGNED","Number");
        f.setSize(14);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2QTYASSI: Qty Reserved");
        f.setReadOnly();
        f.setDbName("QTY_ASSIGNED");

        f = itemblk0.addField("LOCATION_NO");
        f.setSize(15);
        f.setReadOnly();
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2LOCNO: Location");
        f.setMaxLength(35);

        f = itemblk0.addField("ITEM0_SPARE_CONTRACT");
        f.setSize(11);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2ISPARECON: Site");
        f.setUpperCase();
        f.setReadOnly();
        f.setDbName("SPARE_CONTRACT");
        f.setMaxLength(5);

        f = itemblk0.addField("LOCWAREHOUSE");
        f.setSize(17);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2LOWAHO: Warehouse");
        f.setFunction("Inventory_Location_API.Get_Warehouse(:ITEM0_SPARE_CONTRACT,:LOCATION_NO)");
        f.setReadOnly();

        f = itemblk0.addField("LOCBAY");
        f.setSize(5);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2LBAY: Bay");
        f.setFunction("Inventory_Location_API.Get_Bay_No(:ITEM0_SPARE_CONTRACT,:LOCATION_NO)");
        f.setReadOnly();

        f = itemblk0.addField("LOCROW");
        f.setSize(5);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2LROW: Row");
        f.setFunction("Inventory_Location_API.Get_Row_No(:ITEM0_SPARE_CONTRACT,:LOCATION_NO)");
        f.setReadOnly();

        f = itemblk0.addField("LOCTIER");
        f.setSize(5);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2LCT: Tier");
        f.setFunction("Inventory_Location_API.Get_Tier_No(:ITEM0_SPARE_CONTRACT,:LOCATION_NO)");
        f.setReadOnly();

        f = itemblk0.addField("LOCBIN");
        f.setSize(5);
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2LCB: Bin");
        f.setFunction("Inventory_Location_API.Get_Bin_No(:ITEM0_SPARE_CONTRACT,:LOCATION_NO)");
        f.setReadOnly();

        f = itemblk0.addField("PART_NO1");
        f.setSize(11);
        f.setHidden();
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2PARNO: Part no");
        f.setUpperCase();
        f.setDbName("PART_NO");
        f.setMaxLength(25);

        f = itemblk0.addField("LOT_BATCH_NO");
        f.setSize(11);
        f.setReadOnly();
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2LOBANO: Lot/Batch No");
        f.setMaxLength(20);

        f = itemblk0.addField("WAIV_DEV_REJ_NO");
        f.setSize(11);
        f.setReadOnly();
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2WADENO: W/D/R No");
        f.setMaxLength(15);

        f = itemblk0.addField("CONDITIONCODE");
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2CONDITIONCODE: Condition Code");
        f.setFunction("Condition_Code_Manager_API.Get_Condition_Code(:PART_NO, :SERIAL_NO, :LOT_BATCH_NO)");
        f.setReadOnly();
        f.setSize(15);

        f = itemblk0.addField("CONDDESC");
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2CONDDESC: Condition Code Description");
        f.setFunction("CONDITION_CODE_API.GET_DESCRIPTION(Condition_Code_Manager_API.Get_Condition_Code(:PART_NO, :SERIAL_NO, :LOT_BATCH_NO))");
        f.setSize(31);
        f.setReadOnly();

        f = itemblk0.addField("SERIAL_NO");
        f.setSize(8);
        f.setReadOnly();
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2SENO: Serial No");
        f.setUpperCase();
        f.setMaxLength(50);

        f = itemblk0.addField("ENG_CHG_LEVEL");
        f.setSize(11);
        f.setReadOnly();
        f.setHidden();
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2ENCHLE: Eng Chg Level");
        f.setMaxLength(2);

        //Bug 66456, Start, Added check on PROJ
        if (mgr.isModuleInstalled("PROJ"))
        {
            f = itemblk0.addField("ITEM0_ACTIVITY_SEQ","Number");
            f.setSize(11);
            f.setMandatory();
            f.setDbName("ACTIVITY_SEQ");
            f.setReadOnly();
            f.setMaxLength(10);
            f.setLabel("PCMWMATERIALREQUISRESERVATDLG2ITMACTIVITYSEQ: Activity Seq");
        }
        //Bug 66456, End

        f= itemblk0.addField("CONFIGURATION_ID");
        f.setSize(11);
        f.setReadOnly();
        f.setLabel("PCMWMATERIALREQUISRESERVATDLG2CONFIGURATIONID: Configuration ID");

        //Bug 66456, Start, Added check on PROJ
        if (mgr.isModuleInstalled("PROJ"))
        {
            f = itemblk0.addField("PROGRAM_ID");
            f.setSize(10);
            f.setFunction("Activity_API.Get_Program_Id(:ACTIVITY_SEQ)");
            f.setLabel("PCMWMATERIALREQUISRESERVATDLG2PROJECTPRGID: Program ID");
            f.setReadOnly();
            f.setDefaultNotVisible();
    
            f = itemblk0.addField("PROGRAM_DESC");
            f.setSize(10);
            f.setFunction("Activity_API.Get_Program_Description(:ACTIVITY_SEQ)");
            f.setLabel("PCMWMATERIALREQUISRESERVATDLG2PROJDESC: Program Description");
            f.setDefaultNotVisible();
            f.setReadOnly();
    
            f = itemblk0.addField("ITEM0_PROJECT_ID");
            f.setSize(11);
            f.setFunction("Activity_API.Get_Project_Id(:ACTIVITY_SEQ)");
            f.setReadOnly();
            f.setLabel("PCMWMATERIALREQUISRESERVATDLG2ITEM0PROJECTID: Project ID");
    
            f = itemblk0.addField("ITEM0_PROJECT_DESC");
            f.setSize(10);
            f.setLabel("PCMWMATERIALREQUISRESERVATDLG2PROJECTDESC: Project Description");
            f.setFunction("Activity_API.Get_Project_Name(:ACTIVITY_SEQ)");
            f.setReadOnly();
    
            f = itemblk0.addField("SUB_PROJECT_ID");
            f.setSize(10);
            f.setLabel("PCMWMATERIALREQUISRESERVATDLG2SUBPROJECTID: Sub Project ID");
            f.setFunction("Activity_API.Get_Sub_Project_Id(:ACTIVITY_SEQ)");
            f.setReadOnly();
            f.setDefaultNotVisible();
    
            f = itemblk0.addField("SUB_PROJECT_DESC");
            f.setSize(10);
            f.setLabel("PCMWMATERIALREQUISRESERVATDLG2SUBPROJDESC: Sub Project Description");
            f.setFunction("Activity_API.Get_Sub_Project_Description(:ACTIVITY_SEQ)");
            f.setReadOnly();
            f.setDefaultNotVisible();
    
            f = itemblk0.addField("ACTIVITY_ID");
            f.setSize(10);
            f.setLabel("PCMWMATERIALREQUISRESERVATDLG2ACTIVITYID: Activity ID");
            f.setFunction("Activity_API.Get_Activity_No(:ACTIVITY_SEQ)");
            f.setReadOnly();
            f.setDefaultNotVisible();
    
            f = itemblk0.addField("ACTIVITY_DESC");
            f.setSize(10);
            f.setLabel("PCMWMATERIALREQUISRESERVATDLG2ACTDESC: Activity Description");
            f.setFunction("Activity_API.Get_Description(:ACTIVITY_SEQ)");
            f.setReadOnly();
            f.setDefaultNotVisible();
        }
        //Bug 66456, End

        f = itemblk0.addField("NNEGQTY","Number");
        f.setSize(15);
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("QUANTITY","Number");
        f.setSize(15);
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("QTY_DIFF","Number");
        f.setSize(15);
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("QTY_ISSUED","Number");
        f.setSize(15);
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("NISSUEDQTY","Number");
        f.setSize(15);
        f.setFunction("''");
        f.setHidden();

        itemblk0.setView("WORK_ORDER_PART_ALLOC");
        itemblk0.defineCommand("WORK_ORDER_PART_ALLOC_API","New__,Modify__,Remove__");
        itemset0 = itemblk0.getASPRowSet();
        itemblk0.setMasterBlock(headblk);
        itembar0 = mgr.newASPCommandBar(itemblk0);
        itembar0.defineCommand(itembar0.SAVERETURN,"submit");
        itembar0.defineCommand(itembar0.CANCELEDIT,"cancel");
        itemtbl0 = mgr.newASPTable(itemblk0);
        itembar0.disableCommand(itembar0.NEWROW);
        itembar0.disableCommand(itembar0.DUPLICATEROW);
        itembar0.disableCommand(itembar0.DELETE);

        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
        itemlay0.setDialogColumns(2);  
    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWMATERIALREQUISRESERVATDLG2TITLE: Unreserve Part";
    }

    protected String getTitle()
    {
        return "PCMWMATERIALREQUISRESERVATDLG2TITLE: Unreserve Part";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.show());

        if (itemlay0.isVisible())
        {
            appendToHTML(itemlay0.show());
        }

        appendToHTML("<br>\n");
        appendToHTML(fmt.drawSubmit("BACK",mgr.translate("PCMWMATERIALREQUISRESERVATDLG2BACK: Back"),""));

        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

        appendDirtyJavaScript("if('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(cancelWin));	//XSS_Safe AMNILK 20070727
        appendDirtyJavaScript("' == 'TRUE')\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("  self.close();\n");
        appendDirtyJavaScript("} \n");

        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(okClose));		//XSS_Safe AMNILK 20070727
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
              appendDirtyJavaScript("  window.open('");
              appendDirtyJavaScript(mgr.encodeStringForJavascript(qrystr));	//XSS_Safe AMNILK 20070727
              appendDirtyJavaScript("' + \"&OKMANUNRESERVE=1\",'");
              appendDirtyJavaScript(mgr.encodeStringForJavascript(frmname));	//XSS_Safe AMNILK 20070727
              appendDirtyJavaScript("',\"\");\n");
           }
        }
    }

}
