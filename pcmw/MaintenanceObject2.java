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
*  File        : MaintenanceObject2.java 
*  Created     : 010319  INROLK
*  Modified    : 
*  JEWILK  010403  Changed file extensions from .asp to .page
*  CHDELK  010406  Added a dataTransferred block to run()
*                  Updated the saveReturnITEM0() 's Insert_Part_No section
*                  Added necessary code to JavaScript
*  BUNILK  010531  Removed Overview and Find buttons from header part. 
*  SHAFLK  020502  Bug 29246,Added custom command "Detached Spare Part List...", functions checkSecurity() and adjust()
*          020502  And changed sparePartList().
*  SHAFLK  020508  Bug 29946,Changed HTML part to return to correct page after Save&Return.
*  SHAFLK  020509  Bug 29943,Changed RMB "Detached Spare Part List..."
*  SHAFLK  020520  Bug 30188,Made changes to return to correct page after Save&Return.
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  JEWILK  030925  Corrected overridden javascript function toggleStyleDisplay(). 
*  ARWILK  031218  Edge Developments - (Removed clone and doReset Methods)
* ------------------------------ EDGE - SP1 Merge ---------------------------
*  SHAFLK  040213  Bug 40256,Modified methods run(), validate(), okFind() and preDefine().   
*  VAGULK  040324  Merged with SP1.
*  THWILK  040414  Call 114025, Modified sparePartList() to include and pass order number.
*  NEKOLK  041004  Bug 47227,Modified saveReturnITEM0() and preDefine().
*  NIJALK  041028  Merged 47227.
*  ARWILK  041111  Replaced getContents with printContents.
*  NIJALK  041116  Replaced of Inventory_Part_Location_API methods from Inventory_Part_In_Stock_API methods
*  SHAFLK  040910  Bug 46860,Modified method saveReturnITEM0().
*  NIJALK  050224  Merged bug 46860.
*  SHAFLK  051213  Bug 55111,Modified method saveReturnITEM0().
*  NIJALK  060118  Merged bug 55111.
*  NEKOLK  060308  Call 135828:
*  SHAFLK  060525  Bug 57598,Modified saveReturnITEM0.
*  AMNILK  060629  Merged with SP1 APP7.
*  AMDILK  060731  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding
*  AMNILK  060807  Merged Bug ID: 58214.
*  DIAMLK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMNILK  060906  Merged Bug Id: 58216.
*  AMNILK  070718  Eliminated XSS Security Vulnerability.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class MaintenanceObject2 extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.MaintenanceObject2");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPHTMLFormatter fmt;
    private ASPContext ctx;

    private ASPBlock defvalblk;
    private ASPRowSet defset;

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
    private String form_name;
    private String woNo;
    private String cont;
    private String mchCode;
    private String orderNo;
    private ASPTransactionBuffer trans;
    private String callingUrl;
    private String calling_url;
    private boolean newWin;
    private int beg_pos;
    private int end_pos;
    private String oldurl;
    private ASPBuffer buff;
    private ASPBuffer row;
    private ASPCommand cmd;
    private ASPQuery q;
    private String n;
    private String strWoNo;
    private String strMchCode;
    private String strMchName;
    private String fname;
    private String frameName;
    private String okSpareParts;
    private String sRequisitionNo;
    private String qrystr;
    private boolean fromObjStruct;
    private ASPBuffer secBuff;
    private boolean varSec;
    private boolean secDetSp;
    //===============================================================
    // Construction 
    //===============================================================
    public MaintenanceObject2(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        form_name = "";
        woNo =  "";
        cont =  "";
        mchCode =  "";
        ASPManager mgr = getASPManager();

        fmt = mgr.newASPHTMLFormatter();
        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();

        frameName = ctx.readValue("FRAMENAME","");
        okSpareParts = ctx.readValue("OKSPAREPARTS","");
        qrystr = ctx.readValue("QRYSTR","");
        fromObjStruct = ctx.readFlag("FROMOBJSTRU",false);
        secDetSp = ctx.readFlag("SECDETSP",false);
        varSec = ctx.readFlag("VARSEC",false);

        if (mgr.dataTransfered())
        {
            callingUrl = ctx.getGlobal("CALLING_URL"); 
            calling_url = callingUrl;
        }
        woNo = ctx.readValue("WONO","");
        cont = ctx.readValue("CONT",cont);
        mchCode = ctx.readValue("MCHCODE",mchCode);
        orderNo = ctx.readValue("ORDER",orderNo);
        newWin = ctx.readFlag("NEWWIN",false);


        if (mgr.dataTransfered())
        {
            form_name = ctx.readValue("FRMNAME",form_name);
            beg_pos = callingUrl.lastIndexOf("/")+1;
            end_pos = callingUrl.lastIndexOf(".");
            form_name = callingUrl.substring(beg_pos,end_pos);

            if ("WorkOrderRequisHeaderRMB".equals(form_name))
                oldurl = ctx.getGlobal("OLD_URL") ;
        }

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (mgr.buttonPressed("BACK"))
            back();
        else if (mgr.dataTransfered())
        {
            buff = mgr.getTransferedData();
            row = buff.getBufferAt(0); 
            mchCode = row.getValue("MCH_CODE");
            cont = row.getValue("CONTRACT");
            woNo = row.getValue("WO_NO");
            orderNo = row.getValue("ORDER_NO");
            frameName =  row.getValue("FRMNAME");
            qrystr =  row.getValue("QRYSTR");
            okFind();
            if (!mgr.isEmpty(frameName))
                newWin = true;

        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE")))
        {
            mchCode = mgr.readValue("MCH_CODE","");
            orderNo = mgr.readValue("ORDER_NO","");
            cont = mgr.readValue("CONTRACT","");
            woNo = mgr.readValue("WO_NO","");
            frameName = mgr.readValue("FRMNAME","");
            qrystr = mgr.readValue("QRYSTR","");
            
            if (!mgr.isEmpty(mgr.getQueryStringValue("FROMOBJSTRUCT")))
                fromObjStruct = true;

            okFind();
            newWin = true;
        }
        checkSecurity();
        adjust();
        
        ctx.writeValue("WONO",woNo);
        ctx.writeValue("FRAMENAME",frameName);
        ctx.writeValue("CONT",cont);
        ctx.writeValue("MCHCODE",mchCode);
        ctx.writeValue("ORDER",orderNo);
        if (mgr.dataTransfered())
            ctx.writeValue("FRMNAME",form_name);
        ctx.writeFlag("NEWWIN",newWin);
        ctx.writeValue("OKSPAREPARTS",okSpareParts);
        ctx.writeValue("QRYSTR",qrystr);
        ctx.writeFlag("FROMOBJSTRU",fromObjStruct);
        ctx.writeFlag("SECDETSP",secDetSp);
        ctx.writeFlag("VARSEC",varSec);
    }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
// This function is used to check security status for methods which
// are in modules Order,Purch and Invent .This function is called before
// the required method is performed.  -NUPE,21-07-2000

    public boolean  checksec( String method,int ref) //TODO: Parameter types are not recognized and set them to String. Declare type[s] for (method,ref)
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer secBuff; 

        String isSecure[]= new String[7];
        isSecure[ref] = "false" ; 
        String splitted[] = split(method,".");

        String first = splitted[0].toString();
        String Second = splitted[1].toString();

        secBuff = mgr.newASPTransactionBuffer();
        secBuff.addSecurityQuery(first,Second);

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

        String val = mgr.readValue("VALIDATE");
        String txt="";
        trans.clear();

        if ("PLANED_QTY".equals(val))
        {
            /*cmd = trans.addCustomFunction("DTE","Active_Work_Order_API.Get_Plan_S_Date","DATE_REQ");
            cmd.addParameter("WONO",mgr.readValue("WONO",""));*/

            cmd = trans.addCustomFunction("DTE","Maint_Material_Requisition_API.Get_Due_Date","DATE_REQ");


            cmd.addParameter("MAINT_MATERIAL_ORDER_NO",mgr.readValue("MAINT_MATERIAL_ORDER_NO"));

            trans = mgr.validate(trans);


            String dateReq = trans.getBuffer("DTE/DATA").getFieldValue("DATE_REQ");
            trans.clear();

            cmd = trans.addQuery("SUYSTDATE","SELECT SYSDATE SYS_DATE FROM DUAL");
            trans = mgr.perform(trans);

            String sysDate = trans.getBuffer("SUYSTDATE/DATA").getFieldValue("SYS_DATE"); 

            if (mgr.isEmpty(dateReq))
            {
                dateReq =  sysDate;
            }

            txt = (mgr.isEmpty(dateReq) ? "": (dateReq))+ "^";
            mgr.responseWrite(txt);
        }

        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void countFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        //Bug 58216 start
        q.addWhereCondition("MCH_CODE = ?");
        q.addParameter("MCH_CODE",mchCode);
        //Bug 58216 end
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        n = headset.getRow().getValue("N");
        headlay.setCountValue(toInt(n));
        headset.clear();
    }

    public void okFind()
    {
        ASPManager mgr = getASPManager();
        
        q = trans.addEmptyQuery(headblk);
        //Bug 58216 start
        q.addWhereCondition("MCH_CODE = ? AND CONTRACT = ?");//Bug 29943
        q.addParameter("MCH_CODE",mchCode);
        q.addParameter("CONTRACT",cont);
        //Bug 58216 end
        q.includeMeta("ALL"); 
        

        q = trans.addEmptyQuery(itemblk0);
        q.addMasterConnection("HEAD","MCH_CODE","ITEM0_MCH_CODE");
        q.includeMeta("ALL");

        q = trans.addEmptyQuery(defvalblk);
        q.includeMeta("ALL");

        mgr.submit(trans);

        eval(headset.syncItemSets());

        if (headset.countRows() == 0)
            mgr.showAlert(mgr.translate("PCMWMAINTENANCEOBJECT2NODATAFOUND: No data found."));
        else
        {
            ASPBuffer r = headset.getRow();
            r.setValue("WONO",woNo);
            r.setValue("MAINT_MATERIAL_ORDER_NO",orderNo);
            headset.setRow(r);
        }

    }

    //-----------------------------------------------------------------------------
//-----------------------  CMDBAR SEARCH-IT FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

    public void OkFindITEM0()
    {
        ASPManager mgr = getASPManager();

        q = trans.addEmptyQuery(itemblk0);
        //Bug 58216 start
        q.addWhereCondition("MCH_CODE = ?");
        q.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));
        //Bug 58216 end
        q.includeMeta("ALL");

        int currrow = headset.getCurrentRowNo();
        mgr.submit(trans);
        headset.goTo(currrow);
    }

    public void countFindITEM0()
    {
        ASPManager mgr = getASPManager();

        int headrowno = headset.getCurrentRowNo();
        q = trans.addQuery(itemblk0);
        //Bug 58216 start
        q.addWhereCondition("MCH_CODE = ?");
        q.addParameter("MCH_CODE",headset.getRow().getValue("MCH_CODE"));
        //Bug 58216 end
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        n = itemset0.getRow().getValue("N");
        itemlay0.setCountValue(toInt(n));
        itemset0.clear();
        headset.goTo(headrowno);
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void sparePartList()
    {
        ASPManager mgr = getASPManager();

        ASPBuffer buffer =null;
        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());

        if (itemset0.getValue("HAS_SPARE_STRUCTURE").equals(defset.getValue("YES")))
        {
            buffer = mgr.newASPBuffer();
            row = buffer.addBuffer("0");      
            row.addItem("SPARE_ID",headset.getRow().getValue("MCH_CODE"));
            row.addItem("WO_NO",woNo);    
            row.addItem("FRAME",frameName);
            row.addItem("QRYST",qrystr);
            row.addItem("ORDER_NO",orderNo);
            mgr.transferDataTo("../equipw/EquipmentSpareStructure3.page",buffer);
        }
        else
            mgr.showAlert(mgr.translate("PCMWMAINTENANCEOBJECT2MORESELDATA: Can not list Detached Spare Part List for selected line."));      
    }

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

    public void back()
    {
        ASPManager mgr = getASPManager();

        fname="MaintenanceObject2.page";
        mgr.redirectTo(calling_url+"?MCH_CODE="+mgr.URLEncode(mchCode)+"&FORM_NAME="+mgr.URLEncode(fname)+"&CONTRACT="+mgr.URLEncode(cont));
    }

    public void saveReturnITEM0()
    {
        ASPManager mgr = getASPManager();
        int i=0;
        itemset0.changeRow();
        String catno;
        String suplier;
        String description;
         
       // itemset0.first();
        
       // for (i=1; itemset0.countRows() >= i; i++)
       // {
            String pqty = itemset0.getRow().getValue("PLANED_QTY");
            if (!mgr.isEmpty(itemset0.getRow().getValue("PLANED_QTY")))
            {
                double plan_qty = itemset0.getRow().getNumberValue("PLANED_QTY");
                if (isNaN(plan_qty))
                    plan_qty = 0;

                String s_plan_qty = mgr.formatNumber("PLANED_QTY",plan_qty);
                trans.clear();


                if ("maintMatReq".equals(frameName))

                {
                    if ( checksec("Purchase_Part_Supplier_API.Get_Primary_Supplier_No",2 ))
                    {
                        cmd = trans.addCustomFunction("PRIMARYSUP","Purchase_Part_Supplier_API.Get_Primary_Supplier_No","PRISUPLIER");
                        cmd.addParameter("SPARE_CONTRACT");
                        cmd.addParameter("SPARE_ID");

                        cmd = trans.addCustomFunction("ISDSCRIPTION","Purchase_Part_API.Get_Description","PARTDESCRIPTION");
                        cmd.addParameter("SPARE_CONTRACT");
                        cmd.addParameter("SPARE_ID");


                        trans = mgr.perform(trans);

                        suplier = trans.getValue("PRIMARYSUP/DATA/PRISUPLIER");
                        description = trans.getValue("ISDSCRIPTION/DATA/PARTDESCRIPTION");
                    }
                    else
                    {
                        suplier ="";
                        description =""; 
                    }
                    
                    trans.clear();
                    cmd = trans.addCustomCommand("INSPRT"+i,"Work_Order_Requis_Line_API.Insert_Requisition_Line");
                    cmd.addParameter("LINE_NO");
                    cmd.addParameter("RELEASE_NO");
                    cmd.addParameter("WONO",woNo);
                    cmd.addParameter("MAINT_MATERIAL_ORDER_NO",headset.getRow().getValue("MAINT_MATERIAL_ORDER_NO")); 
                    cmd.addParameter("SPARE_CONTRACT",itemset0.getRow().getValue("SPARE_CONTRACT"));
                    cmd.addParameter("SPARE_ID",itemset0.getRow().getValue("SPARE_ID"));
                    cmd.addParameter("UNITMEAS",itemset0.getRow().getFieldValue("UNITMEAS"));
                    cmd.addParameter("PLANED_QTY",s_plan_qty);
                    cmd.addParameter("DATE_REQ",itemset0.getRow().getFieldValue("DATE_REQ"));
                    cmd.addParameter("DEMAND_CODE",itemset0.getRow().getFieldValue("DEMAND_CODE"));
                    cmd.addParameter("VENDORNO",suplier);
                    cmd.addParameter("PARTDESCRIPTION",description);
                    cmd.addParameter("NOTE",itemset0.getRow().getFieldValue("NOTE"));
                    cmd.addParameter("CONDITION_CODE");
                    cmd.addParameter("SERVICETYP"); 
                    cmd.addParameter("SERIALNO");
                    cmd.addParameter("LOTBACHNO");   
                }
                else 
                {  
                    
                    trans.clear();

                    cmd = trans.addCustomFunction("CHDEMAND","MAINT_MATERIAL_REQ_LINE_API.Check_Part_Status_Demand_Flag","DEMAND_CODE");
                    cmd.addParameter("SPARE_ID",itemset0.getRow().getValue("SPARE_ID"));
                    cmd.addParameter("SPARE_CONTRACT",itemset0.getRow().getValue("SPARE_CONTRACT"));

                    mgr.perform(trans);

                    trans.clear();

                    
                   if ( checksec("Sales_Part_API.Get_Catalog_No_For_Part_No",1 ))
                    {
                        cmd = trans.addCustomFunction("CATNOBUFFER","Sales_Part_API.Get_Catalog_No_For_Part_No","CATNO");
                        cmd.addParameter("SPARE_CONTRACT");
                        cmd.addParameter("SPARE_ID");

                        cmd = trans.addCustomFunction("GETACT","Sales_Part_API.Get_Activeind","ACTIVEIND");
                        cmd.addParameter("SPARE_CONTRACT");
                        cmd.addReference("CATNO","CATNOBUFFER/DATA");

                        cmd = trans.addCustomFunction("GETACTDB","Active_Sales_Part_API.Encode","ACTIVEIND_DB");
                        cmd.addReference("ACTIVEIND","GETACT/DATA");

                        trans = mgr.perform(trans);

                        catno = trans.getValue("CATNOBUFFER/DATA/CATNO");

                        String activeInd = trans.getValue("GETACTDB/DATA/ACTIVEIND_DB");

                        if ("N".equals(activeInd)) {
                            catno= "";
                        }
                        else
                            catno = catno;

                        trans.clear();
                    }
                    else
                        catno =""; 
                    trans.clear();

                    cmd = trans.addCustomCommand("INSPRT"+i,"MAINT_MATERIAL_REQ_LINE_API.Insert_Part_No");
                    cmd.addParameter("WONO",woNo);
                    cmd.addParameter("SPARE_CONTRACT",itemset0.getRow().getValue("SPARE_CONTRACT"));
                    cmd.addParameter("SPARE_ID",itemset0.getRow().getValue("SPARE_ID"));
                    cmd.addParameter("DATE_REQ",itemset0.getRow().getFieldValue("DATE_REQ"));
                    cmd.addParameter("PLANED_QTY",s_plan_qty);
                    cmd.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT"));
                    cmd.addParameter("CATNO",catno); 
                    cmd.addParameter("CONDITION_CODE");
                    cmd.addParameter("PART_OWNERSHIP_DB");
                    cmd.addParameter("OWNER"); 
                    cmd.addParameter("JOBS",""); 
                    cmd.addParameter("JOB_ID","");
                    cmd.addParameter("MAINT_MATERIAL_ORDER_NO",headset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));
                }
            //itemset0.next(); 
       // }
        mgr.perform(trans);

        okSpareParts="TRUE";
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

        f = headblk.addField("MCH_CODE");
        f.setSize(30);
        f.setMaxLength(100);
        f.setLabel("PCMWMAINTENANCEOBJECT2MCHCODE: Object ID");
        f.setUpperCase();
        f.setReadOnly();

        f = headblk.addField("DESCRIPTION");
        f.setSize(29);
        f.setLabel("PCMWMAINTENANCEOBJECT2DESC: Description");
        f.setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:CONTRACT,:MCH_CODE)");
        mgr.getASPField("MCH_CODE").setValidation("DESCRIPTION");
        f.setReadOnly();

        f = headblk.addField("CONTRACT");
        f.setSize(7);
        f.setLOV("../Mpccow/UserAllowedSiteLovLov.page",600,445);
        f.setLabel("PCMWMAINTENANCEOBJECT2CON: Site");
        f.setUpperCase();
        f.setReadOnly();

        f = headblk.addField("WONO", "Number");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("MAINT_MATERIAL_ORDER_NO", "Number");
        f.setHidden();
        f.setFunction("''");

        headblk.setView("MAINTENANCE_OBJECT");
        headblk.defineCommand("MAINTENANCE_OBJECT_API","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWMAINTENANCEOBJECT2HD: Spare Parts in Object"));
        headtbl.setWrap();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.disableCommand(headbar.DELETE);
        headbar.disableCommand(headbar.EDITROW);
        headbar.disableCommand(headbar.NEWROW);
        headbar.disableCommand(headbar.DUPLICATEROW);
        headbar.defineCommand(headbar.COUNTFIND,"countFind");   
        headbar.defineCommand(headbar.OKFIND,"okFind");
        headbar.disableCommand(headbar.BACK);
        headbar.disableCommand(headbar.FIND);

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);


        defvalblk = mgr.newASPBlock("DEFVAL");

        f = defvalblk.addField("YES");
        f.setFunction("Translate_Boolean_API.Get_Client_Value(1)");

        defvalblk.setView("DUAL");
        defset = defvalblk.getASPRowSet();

        itemblk0 = mgr.newASPBlock("ITEM0");

        f = itemblk0.addField("ITEM0_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk0.addField("ITEM0_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk0.addField("PLANED_QTY","Number");
        f.setSize(17);
        f.setCustomValidation("WONO,MAINT_MATERIAL_ORDER_NO","DATE_REQ");
        f.setLabel("PCMWMAINTENANCEOBJECT2PLANEDQTY: Planned Quantity");
        f.setFunction("''");

        f = itemblk0.addField("DATE_REQ","Date");
        f.setLabel("PCMWMAINTENANCEOBJECT2DREQ: Date Required");
        f.setFunction("''");

        f = itemblk0.addField("SPARE_ID");
        f.setSize(15);
        f.setLOV("../invenw/InventoryPartWoLovLov.page",600,445);
        f.setLabel("PCMWMAINTENANCEOBJECT2SPID: Part No");
        f.setUpperCase();
        f.setReadOnly();

        f = itemblk0.addField("PART_NO");
        f.setHidden();
        f.setFunction("SPARE_ID");

        f = itemblk0.addField("ITEM0_DESCRIPTION");
        f.setSize(22);
        f.setLabel("PCMWMAINTENANCEOBJECT2IT0DESC: Part Description");
        f.setFunction("Purchase_Part_API.Get_Description(:SPARE_CONTRACT,:SPARE_ID)");
        f.setReadOnly();

        f = itemblk0.addField("SPARE_CONTRACT");
        f.setSize(12);
        f.setLabel("PCMWMAINTENANCEOBJECT2SPCON: Site");
        f.setUpperCase();
        f.setReadOnly();

        f = itemblk0.addField("INVENTORYFLAG");
        f.setSize(14);
        f.setLabel("PCMWMAINTENANCEOBJECT2INVLAG: Inventory Part");
        f.setFunction("Purchase_Part_API.Get_Inventory_Flag(:SPARE_CONTRACT,:SPARE_ID)");
        f.setReadOnly();

        f = itemblk0.addField("QTYONHAND","Number");
        f.setSize(15);
        f.setLabel("PCMWMAINTENANCEOBJECT2QTYONHAND: Quantity on Hand");
        f.setFunction("Inventory_Part_In_Stock_API.Get_Inventory_Qty_Onhand(:SPARE_CONTRACT,:SPARE_ID,NULL)");
        f.setReadOnly();

        f = itemblk0.addField("UNITMEAS");
        f.setSize(11);
        f.setLabel("PCMWMAINTENANCEOBJECT2UNITMEAS: Unit");
        f.setFunction("Purchase_Part_Supplier_API.Get_Unit_Meas(:SPARE_CONTRACT,:SPARE_ID)");
        f.setReadOnly();

        f = itemblk0.addField("HAS_SPARE_STRUCTURE");
        f.setSize(9);
        f.setLabel("PCMWMAINTENANCEOBJECT2STRUC: Structure");
        f.setFunction("Translate_Boolean_API.Decode(Equipment_Spare_Structure_API.Has_Spare_Structure(:SPARE_ID,:SPARE_CONTRACT))");
        f.setReadOnly();

      f = itemblk0.addField("CONDITION_CODE");
      f.setLabel("PCMWMAINTENANCEOBJECT2CONDITIONCODE: Condition Code");
      f.setSize(15);
      f.setReadOnly();
      f.setDynamicLOV("CONDITION_CODE",600,445);
      f.setUpperCase();

      f = itemblk0.addField("CONDDESC");
      f.setLabel("PCMWMAINTENANCEOBJECT2CONDDESC: Condition Code Description");
      f.setSize(20);
      f.setMaxLength(50);
      f.setDefaultNotVisible();
      f.setReadOnly();
      f.setFunction("CONDITION_CODE_API.Get_Description(:CONDITION_CODE)");

      f = itemblk0.addField("PART_OWNERSHIP");
      f.setSize(25);
      f.setReadOnly();
      f.setSelectBox();
      f.enumerateValues("PART_OWNERSHIP_API");
      f.setLabel("PCMWMAINTENANCEOBJECT2PARTOWNERSHIP: Ownership");

      f = itemblk0.addField("PART_OWNERSHIP_DB");
      f.setSize(20);
      f.setHidden();

      f = itemblk0.addField("OWNER");
      f.setSize(15);
      f.setMaxLength(20);
      f.setReadOnly();
      f.setLabel("PCMWMAINTENANCEOBJECT2PARTOWNER: Owner");
      f.setDynamicLOV("CUSTOMER_INFO");
      f.setUpperCase();

      f = itemblk0.addField("OWNER_NAME");
      f.setSize(20);
      f.setMaxLength(100);
      f.setReadOnly();
      f.setLabel("PCMWMAINTENANCEOBJECT2PARTOWNERNAME: Owner Name");
      f.setFunction("CUSTOMER_INFO_API.Get_Name(:OWNER)");

        f = itemblk0.addField("DIMQUALITY");
        f.setSize(16);
        f.setLabel("PCMWMAINTENANCEOBJECT2DIMQLTY: Dimension/Quality");
        f.setFunction("INVENTORY_PART_API.Get_Dim_Quality(:SPARE_CONTRACT,:SPARE_ID)");
        f.setReadOnly();

        f = itemblk0.addField("TYPEDESIGNATION");
        f.setSize(15);
        f.setLabel("PCMWMAINTENANCEOBJECT2TYPEDESIG: Type Designation");
        f.setFunction("INVENTORY_PART_API.Get_Type_Designation(:SPARE_CONTRACT,:SPARE_ID)");
        f.setReadOnly();

        f = itemblk0.addField("QTY");
        f.setSize(10);
        f.setLabel("PCMWMAINTENANCEOBJECT2QUANTY: Quantity");
        f.setReadOnly();

        f = itemblk0.addField("MCH_PART");
        f.setSize(12);
        f.setLabel("PCMWMAINTENANCEOBJECT2MCHPART: Object Part");
        f.setReadOnly();

        f = itemblk0.addField("DRAWING_NO");
        f.setSize(18);
        f.setLabel("PCMWMAINTENANCEOBJECT2DRAWNO: Drawing No");
        f.setUpperCase();
        f.setReadOnly();

        f = itemblk0.addField("DRAWING_POS");
        f.setSize(16);
        f.setLabel("PCMWMAINTENANCEOBJECT2DRAWPOS: Drawing Position");
        f.setUpperCase();
        f.setReadOnly();

        f = itemblk0.addField("NOTE");
        f.setSize(20);
        f.setLabel("PCMWMAINTENANCEOBJECT2NOTE: Note");
        f.setReadOnly();

        f = itemblk0.addField("ITEM0_MCH_CODE");
        f.setHidden();
        f.setDbName("MCH_CODE");

        f = itemblk0.addField("LINE_NO");
        f.setHidden();
        f.setFunction("' '");

        f = itemblk0.addField("RELEASE_NO");
        f.setHidden();
        f.setFunction("' '");

        f = itemblk0.addField("REQUISITION_NO");
        f.setHidden();
        f.setFunction("' '");

        f = itemblk0.addField("VENDORNO");
        f.setHidden();
        f.setFunction("' '");

        f = itemblk0.addField("DEMAND_CODE");
        f.setHidden();
        f.setFunction("' '");   

        f = itemblk0.addField("SYS_DATE","Date");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("CATNO");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("PRISUPLIER");
       	f.setFunction("''");
       	f.setHidden();

       	f = itemblk0.addField("PARTDESCRIPTION");
       	f.setFunction("''");
       	f.setHidden();

        f = itemblk0.addField("JOBS");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("JOB_ID");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("SERIALNO");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("LOTBACHNO");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("SERVICETYP");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("ACTIVEIND");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("ACTIVEIND_DB");
        f.setHidden();
        f.setFunction("''");
        

        itemblk0.setView("EQUIPMENT_OBJECT_SPARE");
        itemblk0.defineCommand("EQUIPMENT_OBJECT_SPARE_API","New__,Modify__,Remove__");
        itemblk0.setMasterBlock(headblk);
        itemset0 = itemblk0.getASPRowSet();

        itembar0 = mgr.newASPCommandBar(itemblk0);
        itembar0.enableCommand(itembar0.FIND);
        itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
        itembar0.defineCommand(itembar0.OKFIND,"OkFindITEM0");
        itembar0.disableCommand(itembar0.NEWROW);
        itembar0.disableCommand(itembar0.SAVENEW);
        itembar0.defineCommand(itembar0.SAVERETURN,"saveReturnITEM0");
        itembar0.disableCommand(itembar0.DUPLICATEROW);
        itembar0.disableCommand(itembar0.DELETE);

        if (mgr.isPresentationObjectInstalled("equipw/EquipmentSpareStructure3.page")) 
            itembar0.addCustomCommand("sparePartList",mgr.translate("PCMWMAINTENANCEOBJECT2CDN: Detached Spare Part List..."));  

        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
        itemtbl0 = mgr.newASPTable(itemblk0);
        itemtbl0.setTitle(mgr.translate("PCMWMAINTENANCEOBJECT2ITM0: Details"));
        itemtbl0.setWrap();
    }

    
    public void checkSecurity()
    {
        ASPManager mgr = getASPManager();

        if (!varSec)
        {
            trans.clear();

            trans.addSecurityQuery("PURCHASE_PART");
            trans.addPresentationObjectQuery("EQUIPW/EquipmentSpareStructure3.page");     

            trans = mgr.perform(trans);

            secBuff = trans.getSecurityInfo();

            if (secBuff.itemExists("PURCHASE_PART") && secBuff.namedItemExists("EQUIPW/EquipmentSpareStructure3.page")) 
                secDetSp = true;

            varSec = true;
        }
    }     

    public void adjust()
    {
        ASPManager mgr = getASPManager();


        if (mgr.isPresentationObjectInstalled("equipw/EquipmentSpareStructure3.page")) 
        {
            if (!secDetSp)
                itembar0.removeCustomCommand("sparePartList");
        }
    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWMAINTENANCEOBJECT2SPO: Spare Parts in Object";
    }

    protected String getTitle()
    {
        return "PCMWMAINTENANCEOBJECT2SPO: Spare Parts in Object";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.show());

        if (headlay.isSingleLayout() && headset.countRows() > 0)
        {
            appendToHTML(itemlay0.show());
        }

        if (!newWin)
        {
            appendToHTML(" <table id=\"buttonTBL\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n");
            appendToHTML(fmt.drawSubmit("BACK",mgr.translate("PCMWMAINTENANCEOBJECT2BC: Back "),"BACK"));
            appendToHTML(" </table>\n");
        }

        appendDirtyJavaScript("window.name = \"frmSpareParts\";\n"); 
        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(okSpareParts));	//XSS_Safe AMNILK 20070717
        appendDirtyJavaScript("' == \"TRUE\")\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("window.opener.commandSet('HEAD.refreshForm','');\n");
        //appendDirtyJavaScript("',\"alwaysRaised=yes,resizable,status=yes,scrollbars,width=770,height=460\");\n");
        appendDirtyJavaScript("	self.close();\n"); 
        appendDirtyJavaScript(" window.close();\n");
	    appendDirtyJavaScript("}\n");

        if (fromObjStruct)
        {
            appendDirtyJavaScript("if ('");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(okSpareParts));  //XSS_Safe AMNILK 20070717
            appendDirtyJavaScript("' == \"TRUE\")\n");
            appendDirtyJavaScript("{\n");          
            appendDirtyJavaScript("  window.open(\"MaintenaceObject3.page?FROMOBJPART=TRUE\",\"objStruct\",\"scrollbars,resizable,status=yes,width=750,height=500\"); \n");
            appendDirtyJavaScript("}\n");
        }

        appendDirtyJavaScript("function toggleStyleDisplay(el)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   el = document.getElementById(el);\n");
        appendDirtyJavaScript("   if(el.style.display!='none')\n");
        appendDirtyJavaScript("   {  \n");
        appendDirtyJavaScript("      el.style.display='none';\n");
        appendDirtyJavaScript("      buttonTBL.style.display='none';\n");
        appendDirtyJavaScript("   }  \n");
        appendDirtyJavaScript("   else\n");
        appendDirtyJavaScript("   {  \n");
        appendDirtyJavaScript("      el.style.display='block';\n");
        appendDirtyJavaScript("      buttonTBL.style.display='block';\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}\n");
    }
}
