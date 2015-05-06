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
*  File        : ActiveSeparateOvw.java 
*  Created     : INROLK  010305 Java conversion.
*  Modified    :    
*  CHCRLK  010808  Modified method printWO and printServO();
*  INROLK  011004  Added Security Check to RMBs.
*  INROLK  011010  added mgr.createSearchURL(headblk) to add to add link. call id 70400.
*  SHAFLK  021108  Bug Id 34064,Changed methods printServO & printWO. 
*  YAWILK  021129  Added MCH_CODE_CONTRACT; Modified MCH_CODE max length to 100
*  BUNILK  021210  Merged with 2002-3 SP3
*  CHCRLK  030529  Added column Connection Type.
*  ARWILK  031002  (Bug#105066) Changed methods okFind and getViewNames (Check method comments)
*  JEWILK  031006  Made field CONNECTION_TYPE Insertable. Call 105177.
*  THWILK  031024  Call ID 104789 - Added an extra parameter(#)to the deffinition of the field WO_NO.
*  JEWILK  031027  Removed setUpperCase() from field CONNECTION_TYPE. added methods saveReturn() and saveNew(). Call 109396.
*  SAPRLK  031218  Web Alignment - removed methods clone(), doReset() and group the state change RMBs together inside RMB 'Work Order Status'.
*  VAGULK  040108  Made the field order according to the order in the Centura application(Web Alignment).
*  SAPRLK  040401  Made Changes for RMBs using data transfer.
*  SAPRLK  040422  Web Alignment - Added multirow action, removed unnecessary global variables, simplified code for RMBs.   
*  THWILK  040604  Added PM_REVISION and performed necesary changes relating to the key change under IID AMEC109A, Multiple Standard Jobs on PMs.
*  ARWILK  040716  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning)
*  THWILK  040818  Call 116824, Changed the Tag of the page title.
*  NAMELK  041104  Duplicated Translation Tags Corrected.
*  ARWILK  041115  Replaced getContents with printContents.
*  SHAFLK  050919  Bug 52880, Modified adjust().
*  NIJALK  051004  Merged bug 52880.
*  NEKOLK  051125  Bug 54415, Changed server method Get_Contract to Get_Def_Contract .
*  NIJALK  051220  Merged bug 54415.
*  NEKOLK  060223  Call 135238: Modified confirm() and cancel().
*  NIJALK  060223  Call 135216: Added fields PLANNED_MAN_HRS,REQUIRED_S_DATE,REQUIRED_E_DATE to headblk.
*  ASSALK  060224  Call 135211: Modified newRow(). added functionality to fetch reported by auto.
*  ASSALK  060310  Call ID: 136742. Added 'Work Description' and 'man hours' removed.
*  NIJALK  060322  Renamed 'Work Master' to 'Executed By'.
*  ILSOLK  060817  Set the MaxLength of Address1 as 100.
*  AMDILK  060912  MTPR904: Request id 21686. Modifed Copy(), in order to pass the parameters correctly to CopyWorkOrderDlg.page
*  AMDILK  070404  Eliminated script errors
*  ILSOLK  070706  Eliminated XSS.
*  ILSOLK  070905  Modifed okFind().(Call ID 148318)
* ----------------------------------------------------------------------------
* -----------------------APP7.5 SP1-------------------------------------
*  ILSOLK  071214  Bug Id 68773, Eliminated XSS.
*  CHANLK  080205  Bug 66842,Remove maxLength of PM_NO
* -----------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class ActiveSeparateOvw extends ASPPageProvider
{

    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveSeparateOvw");


    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPContext ctx;

    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPCommandBar headbar;
    private ASPTable headtbl;
    private ASPBlockLayout headlay;

    private ASPBlock printblk;
    private ASPRowSet printset;
    private ASPField f;
    private ASPBlock b;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private String newreq;
    private int currrow;
    private ASPCommand cmd;
    private ASPQuery q;
    private ASPBuffer data;
    private String current_url;  
    private String attr3;
    private String attr4;
    private String attr;
    private ASPBuffer r;
    private ASPBuffer key;
    private String head_command;
    private String sCompany; 
    private String attr1;  
    private String attr2; 
    //============ Variables for Security check for RMBs ===============
    private boolean again;
    private boolean printServO_;
    private boolean printWO_;
    private boolean copy_;
    private boolean prepareWo_;

    //Web Alignment - replace Links with RMBs
    private boolean bOpenNewWindow;
    private String urlString;
    private String newWinHandle;
    //

    //===============================================================
    // Construction 
    //===============================================================
    public ActiveSeparateOvw(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager(); 

        trans = mgr.newASPTransactionBuffer();
        ctx = mgr.getASPContext();
        newreq = ctx.readValue("NEWREQ",""); 
        //=============== Variables for Security check for RMBs =================
        again = ctx.readFlag("AGAIN",again);
        printServO_= ctx.readFlag("PRINTSERVO",false);
        printWO_= ctx.readFlag("PRINTWO",false);
        copy_= ctx.readFlag("COPY",false);
        prepareWo_ = ctx.readFlag("PREPAREWO",false);

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (mgr.dataTransfered())
            findTransfered();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("FAULT_REP_FLAG")))
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
            okFind();

        if (!again)
        {
            checkObjAvaileble();
            again = true;
        }

        adjust();
        ctx.writeFlag("AGAIN",again);
        ctx.writeFlag("PRINTSERVO",printServO_);
        ctx.writeFlag("PRINTWO",printWO_);
        ctx.writeFlag("COPY",copy_);
        ctx.writeFlag("PREPAREWO",prepareWo_);
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------


//: Parameter types are not recognized and set them to String. Declare type[s] for (command)
    public void perform( String command) 
    {
        ASPManager mgr = getASPManager();

        //Web Alignment - Multirow Action
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


    public void validate()
    {
        ASPManager mgr = getASPManager();

        String val = mgr.readValue("VALIDATE");
        String txt="";  

        if ("MCH_CODE".equals(val))
        {
            String mch = "";
            String cur = "";
            String grp = "";
            String crt = "";
            String mchcon = "";

            cmd = trans.addCustomFunction("OBJMCHCON","EQUIPMENT_OBJECT_API.Get_Def_Contract","MCH_CODE_CONTRACT");
            cmd.addParameter("MCH_CODE");

            cmd = trans.addCustomFunction( "OBJDESC","MAINTENANCE_OBJECT_API.Get_Mch_Name","MCH_CODE_DESCRIPTION" );
            cmd.addReference("MCH_CODE_CONTRACT","OBJMCHCON/DATA");
            cmd.addParameter("MCH_CODE");

            cmd = trans.addCustomFunction( "CURRPOS","PART_SERIAL_CATALOG_API.Get_Alt_Latest_Transaction","LATEST_TRANSACTION" );
            cmd.addReference("MCH_CODE_CONTRACT","OBJMCHCON/DATA");
            cmd.addParameter("MCH_CODE");

            cmd = trans.addCustomFunction( "GRPID","EQUIPMENT_OBJECT_API.Get_Group_Id","GROUPID" );
            cmd.addReference("MCH_CODE_CONTRACT","OBJMCHCON/DATA");
            cmd.addParameter("MCH_CODE");

            cmd = trans.addCustomFunction( "CRTLY","EQUIPMENT_FUNCTIONAL_API.Get_Criticality","CRITICALITY" );
            cmd.addReference("MCH_CODE_CONTRACT","OBJMCHCON/DATA");
            cmd.addParameter("MCH_CODE");

            trans = mgr.validate(trans);

            mchcon = trans.getValue("OBJMCHCON/DATA/MCH_CODE_CONTRACT");
            mch = trans.getValue("OBJDESC/DATA/MCH_CODE_DESCRIPTION");
            cur = trans.getValue("CURRPOS/DATA/LATEST_TRANSACTION");
            grp = trans.getValue("GRPID/DATA/GROUPID");
            crt = trans.getValue("CRTLY/DATA/CRITICALITY");
            txt = (mgr.isEmpty(mch) ? "" :mch) + "^" +
                  (mgr.isEmpty(cur) ? "" :cur)+ "^" +
                  (mgr.isEmpty(grp) ? "" :grp)+ "^" +
                  (mgr.isEmpty(crt) ? "" :crt)+ "^" +
                  (mgr.isEmpty(mchcon) ? "" :mchcon)+ "^";
            mgr.responseWrite(txt);
        }
        else if ("REPORTED_BY".equals(val))
        {
            String repid =""; 
            cmd = trans.addCustomFunction( "REPOID","Company_Emp_API.Get_Max_Employee_Id","REPORTED_BY_ID" );
            cmd.addParameter("COMPANY");
            cmd.addParameter("REPORTED_BY");

            trans = mgr.validate(trans);
            repid = trans.getValue("REPOID/DATA/REPORTED_BY_ID");
            mgr.responseWrite(repid);
        }
        else if ("PREPARED_BY".equals(val))
        {
            String preid = "";
            cmd = trans.addCustomFunction( "PREPID","Company_Emp_API.Get_Max_Employee_Id","PREPARED_BY_ID" );
            cmd.addParameter("COMPANY");
            cmd.addParameter("PREPARED_BY");

            trans = mgr.validate(trans);

            preid = trans.getValue("PREPID/DATA/PREPARED_BY_ID");
            mgr.responseWrite(preid);
        }
        else if ("WORK_MASTER_SIGN".equals(val))
        {
            String wmsid = "";
            cmd = trans.addCustomFunction( "WOMSID","Company_Emp_API.Get_Max_Employee_Id","WORK_MASTER_SIGN_ID" );
            cmd.addParameter("COMPANY");
            cmd.addParameter("WORK_MASTER_SIGN");

            trans = mgr.validate(trans);
            wmsid = trans.getValue("WOMSID/DATA/WORK_MASTER_SIGN_ID");
            mgr.responseWrite(wmsid);
        }
        else if ("WORK_LEADER_SIGN".equals(val))
        {

            String wlsid = ""; 
            cmd = trans.addCustomFunction( "WOLSID","Company_Emp_API.Get_Max_Employee_Id","WORK_LEADER_SIGN_ID" );
            cmd.addParameter("COMPANY");
            cmd.addParameter("WORK_LEADER_SIGN");

            trans = mgr.validate(trans);
            wlsid = trans.getValue("WOLSID/DATA/WORK_LEADER_SIGN_ID");
            mgr.responseWrite(wlsid);
        }
        else if ("ORG_CODE".equals(val))
        {
            cmd = trans.addCustomFunction( "GETORGDESC","Organization_API.Get_Description","ORGCODEDESCR" );
            cmd.addParameter("CONTRACT");
            cmd.addParameter("ORG_CODE");

            trans = mgr.validate(trans);
            String sOrgDesc = trans.getValue("GETORGDESC/DATA/ORGCODEDESCR");
            mgr.responseWrite(sOrgDesc);
        }

        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR FUNCTIONS  ---------------------------------
//-----------------------------------------------------------------------------

    public void findTransfered()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.addWhereCondition("OBJSTATE IN ('FAULTREPORT', 'WORKREQUEST') AND CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CONTRACT)from DUAL) ");
        if (mgr.dataTransfered())
            q.addOrCondition(mgr.getTransferedData());
        q.includeMeta("ALL");
        mgr.submit(trans);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEOVWNODATA: No data found."));
            headset.clear();
        }
        else
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT ); 
    }


    public void okFind()
    {
        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();
        q = trans.addQuery(headblk);
        q.addWhereCondition("OBJSTATE IN ('FAULTREPORT', 'WORKREQUEST') AND CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CONTRACT)from DUAL) ");
        if (mgr.dataTransfered())
            q.addOrCondition(mgr.getTransferedData());
        q.includeMeta("ALL");
        mgr.querySubmit(trans,headblk);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEOVWNODATA: No data found."));
            headset.clear();
        }
        // 031002  ARWILK  Begin (Bug#105066)
        else if (headset.countRows() == 1)
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        }
        // 031002  ARWILK  End (Bug#105066)

        headset.goTo(currrow);
        mgr.createSearchURL(headblk);

    }


    public void countFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("OBJSTATE IN ('FAULTREPORT', 'WORKREQUEST') AND  CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CONTRACT)from DUAL) ");
        mgr.submit(trans);
        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();
    }


    public void newRow()
    {
        ASPManager mgr = getASPManager();
        String repby=null,reportById=null;
        trans.clear();

        cmd = trans.addEmptyCommand("MAIN","ACTIVE_SEPARATE_API.New__",headblk);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);
        data = trans.getBuffer("MAIN/DATA");
        //data.setValue("CONNECTION_TYPE_DB","EQUIPMENT");

        trans.clear();
        cmd = trans.addCustomFunction("GETUSER","Fnd_Session_API.Get_Fnd_User","ISUSER");

        cmd = trans.addCustomFunction("GETREPBY","Person_Info_API.Get_Id_For_User","REPORTED_BY");
        cmd.addReference("ISUSER","GETUSER/DATA");

        cmd = trans.addCustomFunction("REPBYID","Company_Emp_API.Get_Max_Employee_Id","REPORTED_BY_ID");
        cmd.addParameter("COMPANY",data.getFieldValue("COMPANY"));
        cmd.addReference("REPORTED_BY","GETREPBY/DATA");

        trans = mgr.perform(trans);
        repby = trans.getValue("GETREPBY/DATA/REPORTED_BY");
        reportById = trans.getValue("REPBYID/DATA/REPORTED_BY_ID");

        data.setValue("REPORTED_BY",repby);
        data.setValue("REPORTED_BY_ID",reportById);
        data.setValue("CONNECTION_TYPE_DB","EQUIPMENT");

        headset.addRow(data);

        newreq = "TRUE";
    }

    public void saveReturn()
    {
        ASPManager mgr = getASPManager();

        headset.changeRow();
        int currrowHead = headset.getCurrentRowNo();

        mgr.submit(trans);

        headset.goTo(currrowHead);
        headset.refreshRow();
    }

    public void saveNew()
    {
        saveReturn();
        newRow();
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
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

    public void none()
    {
        ASPManager mgr = getASPManager();

        mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEOVWNOSELECT: No RMB methods has been selected."));
    }


    public void prepareWo()
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

        urlString = createTransferUrl("ActiveSeparate2.page", transferBuffer);

        newWinHandle = "PrepareWO"; 
        //
    }


    public void copy()
    {
        ASPManager mgr = getASPManager();
        ASPBuffer row=null;
        ASPBuffer buff;

        /*if (headlay.isMultirowLayout())
        {
            headset.goTo(headset.getRowSelected());
        } */

        if (headlay.isMultirowLayout())
            headset.store();
        else
        {
            headset.unselectRows();
            headset.selectRow();
        }

        current_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",current_url);
        ctx.setGlobal("FORM_NAME","CopyWorkOrderDlg.page");

	urlString = "CopyWorkOrderDlg.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) +
                    "&FAULT_REP_FLAG=" + mgr.URLEncode(headset.getRow().getValue("FAULT_REP_FLAG")) +
                    "&COMPANY=" + mgr.URLEncode(headset.getRow().getValue("COMPANY"));

	//Web Alignment - open tabs implemented as RMBs in a new window
        bOpenNewWindow = true;
        newWinHandle = "copy";
    }


    public void printWO()
    {
        ASPManager mgr = getASPManager();

        //Web Alignment - Enable Multirow Action
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
            attr1 = "REPORT_ID" + (char)31 + "ACTIVE_SEP_WO_PRINT_REP" + (char)30;
            attr2 = "WO_NO_LIST" + (char)31 + headset.getValue("WO_NO") + (char)30;
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


    public void printServO()
    {
        ASPManager mgr = getASPManager();

        //Web Alignment - Enable Multirow Action
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
            attr1 = "REPORT_ID" + (char)31 + "PRINT_WORK_ORDER_SE_MA_REP" + (char)30;
            attr2 = "WO_NO_LIST" + (char)31 + headset.getValue("WO_NO") + (char)30;
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


    public void confirm()
    {

        if (headlay.isMultirowLayout())
            headset.store();
        else
        {
            headset.unselectRows();
            headset.selectRow();
        }
        int rowno = headset.getCurrentRowNo();
            trans.clear();
            perform("CONFIRM__");

            headset.goTo(rowno);
            headset.clearRow();
  
    }


    public void cancel()
    {

        if (headlay.isMultirowLayout())
            headset.store();
        else
        {
            headset.unselectRows();
            headset.selectRow();
        }
        int rowno = headset.getCurrentRowNo();
            trans.clear();
            perform("CANCEL__");

            headset.goTo(rowno);
            headset.clearRow();

    }


    public void preDefine()
    {
        ASPManager mgr = getASPManager();


        headblk = mgr.newASPBlock("MAIN");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("OBJSTATE");
        f.setHidden();

        f = headblk.addField("OBJEVENTS");
        f.setHidden();

        f = headblk.addField("WO_NO","Number","#");
        f.setSize(12);
        f.setLabel("PCMWACTIVESEPARATEOVWWONO: WO No");
        f.setReadOnly();
        f.setMaxLength(8);
        f.setHilite();

        f = headblk.addField("CONTRACT");
        f.setSize(12);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",650,445);
        f.setLabel("PCMWACTIVESEPARATEOVWCONTRACT: WO Site");
        f.setUpperCase();
        f.setMaxLength(5);

        f = headblk.addField("CONNECTION_TYPE");
        f.setSize(15);
        f.setSelectBox();                                  
        f.setLabel("PCMWACTIVESEPARATEOVWCONNECTIONTYPE: Connection Type");
        //f.setCustomValidation("CONNECTION_TYPE","CONNECTION_TYPE_DB");
        f.enumerateValues("MAINT_CONNECTION_TYPE_API");
        f.setReadOnly();
        f.setInsertable();

        f = headblk.addField("MCH_CODE");
        f.setSize(12);
        f.setDynamicLOV("MAINTENANCE_OBJECT","MCH_CODE_CONTRACT CONTRACT",600,445);
        f.setCustomValidation("MCH_CODE","MCH_CODE_DESCRIPTION,LATEST_TRANSACTION,GROUPID,CRITICALITY,MCH_CODE_CONTRACT");
        f.setLabel("PCMWACTIVESEPARATEOVWMCHCODE: Object ID");
        f.setUpperCase();
        f.setMaxLength(100);

        f = headblk.addField("MCH_CODE_DESCRIPTION");
        f.setSize(25);
        f.setReadOnly();
        f.setLabel("PCMWACTIVESEPARATEOVWMCHNAME: Object Description");
        f.setMaxLength(45);
        f.setDefaultNotVisible();

        f = headblk.addField("MCH_CODE_CONTRACT");
        f.setSize(12);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",650,445);
        f.setLabel("PCMWACTIVESEPARATEOVWMCHCODECONTRACT: Site");
        f.setUpperCase();
        f.setMaxLength(5);

        f = headblk.addField("LATEST_TRANSACTION");
        f.setSize(25);
        f.setLabel("PCMWACTIVESEPARATEOVWLATESTTRANSACTION: Latest Transaction");
        f.setFunction("PART_SERIAL_CATALOG_API.Get_Alt_Latest_Transaction(:MCH_CODE_CONTRACT,:MCH_CODE)");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("GROUPID");
        f.setSize(12);
        f.setDynamicLOV("EQUIPMENT_OBJ_GROUP",600,445);
        f.setLabel("PCMWACTIVESEPARATEOVWGROUPID: Group ID");
        f.setFunction("EQUIPMENT_OBJECT_API.Get_Group_Id(:MCH_CODE_CONTRACT,:MCH_CODE)");
        f.setReadOnly();
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = headblk.addField("CRITICALITY");
        f.setSize(25);
        f.setDynamicLOV("EQUIPMENT_CRITICALITY",600,445);
        f.setLabel("PCMWACTIVESEPARATEOVWCRITICALITY: Criticality");
        f.setFunction("EQUIPMENT_FUNCTIONAL_API.Get_Criticality(:MCH_CODE_CONTRACT,:MCH_CODE)");
        f.setReadOnly();
        f.setMaxLength(10);
        f.setDefaultNotVisible();


        f = headblk.addField("REPORTED_BY");
        f.setSize(14);
        f.setDynamicLOV("EMPLOYEE_LOV",600,445);
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPARATEOVWREPORTEDBY1: Reported By");
        f.setCustomValidation("COMPANY,REPORTED_BY","REPORTED_BY_ID");
        f.setUpperCase();
        f.setMaxLength(20);
        f.setDefaultNotVisible();
        f.setHilite();

        f = headblk.addField("ORG_CODE");
        f.setSize(10);
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,445);
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPARATEOVWMAINTORGCODE: Maintenance Organization");
        f.setUpperCase();
        f.setMaxLength(8);
        f.setCustomValidation("CONTRACT,ORG_CODE","ORGCODEDESCR");

        f = headblk.addField("ERR_DESCR");
        f.setSize(50);
        f.setMandatory();
        f.setLabel("PCMWACTIVESEPARATEOVWERRDESCR: Directive");
        f.setMaxLength(60);
        f.setHilite();

        f = headblk.addField("ORGCODEDESCR");
        f.setSize(25);
        f.setFunction("Organization_Api.Get_Description(:CONTRACT,:ORG_CODE)");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);

        f = headblk.addField("REG_DATE","Datetime");
        f.setSize(12);
        f.setMaxLength(20);
        f.setLabel("PCMWACTIVESEPARATEOVWREGDATE: Registration Date");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("CLIENT_VALUES");
        f.setSize(14);
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("STATE");
        f.setSize(12);
        f.setLabel("PCMWACTIVESEPARATEOVWSTATE: Status"); 
        f.setReadOnly();
        f.setMaxLength(253);
        f.setHilite();

        f = headblk.addField("PLAN_S_DATE","Datetime");
        f.setSize(18);
        f.setLabel("PCMWACTIVESEPARATEOVWPLANSDATE: Planned Start");

        f = headblk.addField("PLAN_F_DATE","Datetime");
        f.setSize(18);
        f.setLabel("PCMWACTIVESEPARATEOVWPLANFDATE: Planned Completion");
        f.setDefaultNotVisible();

        f = headblk.addField("PLAN_HRS","Number");
        f.setSize(18);
        f.setLabel("PCMWACTIVESEPARATEOVWPLANHOURS: Execution Time");
        f.setDefaultNotVisible();

        f = headblk.addField("PLANNED_MAN_HRS","Number");
        f.setSize(18);
        f.setLabel("PCMWACTIVESEPARATEOVWPLANMANHOURS: Man Hours");
        f.setDefaultNotVisible();

        f = headblk.addField("REQUIRED_S_DATE","Datetime");
        f.setSize(18);
        f.setDbName("REQUIRED_START_DATE");
        f.setLabel("PCMWACTIVESEPARATEOVWREQSDATE: Required Start");
        f.setDefaultNotVisible();

        f = headblk.addField("REQUIRED_E_DATE","Datetime");
        f.setSize(18);
        f.setDbName("REQUIRED_END_DATE");
        f.setLabel("PCMWACTIVESEPARATEOVWREENDDATE: Latest Completion");
        f.setDefaultNotVisible();

        f = headblk.addField("CALL_CODE");
        f.setSize(9);
        f.setDynamicLOV("MAINTENANCE_EVENT",600,445);
        f.setLabel("PCMWACTIVESEPARATEOVWCALLCODE: Event");
        f.setUpperCase();
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = headblk.addField("OP_STATUS_ID");
        f.setSize(20);
        f.setDynamicLOV("OPERATIONAL_STATUS",600,445);
        f.setLabel("PCMWACTIVESEPARATEOVWOPSTATUSID: Operational Status");
        f.setUpperCase();
        f.setMaxLength(3);
        f.setDefaultNotVisible();

        f = headblk.addField("PRIORITY_ID");
        f.setSize(5);
        f.setDynamicLOV("MAINTENANCE_PRIORITY",600,445);
        f.setLabel("PCMWACTIVESEPARATEOVWPRIORITYID: Priority");
        f.setHyperlink("../pcmw/MaintenancePriorityOvw.page","PRIORITY_ID","NEWWIN");
        f.setUpperCase();
        f.setMaxLength(1);

        f = headblk.addField("PRIORITYDESC");
        f.setSize(25);
        f.setFunction("Maintenance_Priority_API.Get_Description(:PRIORITY_ID)");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        mgr.getASPField("PRIORITY_ID").setValidation("PRIORITYDESC");

        f = headblk.addField("WORK_TYPE_ID");
        f.setSize(12);
        f.setDynamicLOV("WORK_TYPE",600,445);
        f.setLabel("PCMWACTIVESEPARATEOVWWORKTYPEID: Work Type");
        f.setUpperCase();
        f.setHyperlink("../pcmw/WorkType.page","WORK_TYPE_ID","NEWWIN");
        f.setMaxLength(20);
        f.setDefaultNotVisible();

        f = headblk.addField("WORKTYPEDESC");
        f.setSize(25);
        f.setFunction("WORK_TYPE_API.Get_Description(:WORK_TYPE_ID)");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        mgr.getASPField("WORK_TYPE_ID").setValidation("WORKTYPEDESC");

        f = headblk.addField("VENDOR_NO");
        f.setSize(12);
        f.setDynamicLOV("SUPPLIER_INFO",600,445);
        f.setLabel("PCMWACTIVESEPARATEOVWVENDORNO: Contractor");
        f.setUpperCase();
        f.setMaxLength(20);
        f.setDefaultNotVisible();

        f = headblk.addField("VENDORNAME");
        f.setSize(18);
        f.setLabel("PCMWACTIVESEPARATEOVWVENDORNAME: Contractor Name");
        f.setFunction("Supplier_Info_API.Get_Name(:VENDOR_NO)");
        mgr.getASPField("VENDOR_NO").setValidation("VENDORNAME");
        f.setReadOnly();
        f.setMaxLength(100);
        f.setDefaultNotVisible();

        f = headblk.addField("PM_TYPE");
        f.setSize(12);
        f.setHidden();
        f.setLabel("PCMWACTIVESEPARATEOVWPMTYPE: PM Type");
        f.setMaxLength(20);
        f.setDefaultNotVisible();

        f = headblk.addField("REPORTED_BY_ID");
        f.setSize(14);
        f.setHidden();
        f.setLabel("PCMWACTIVESEPARATEOVWREPORTEDBY2: Reported By ID");
        f.setMaxLength(11);
        f.setDefaultNotVisible();

        f = headblk.addField("REPAIR_FLAG");
        f.setSize(22);
        f.setLabel("PCMWACTIVESEPARATEOVWREPAIRFLAG: Repair Work Order");
        f.setCheckBox("FALSE,TRUE");
        f.setReadOnly();
        f.setMaxLength(5);
        f.setDefaultNotVisible();

        f = headblk.addField("PREPARED_BY");
        f.setSize(14);
        f.setDynamicLOV("EMPLOYEE_LOV",600,445);
        f.setLabel("PCMWACTIVESEPARATEOVWPREPAREDBY: Prepared By");
        f.setCustomValidation("COMPANY,PREPARED_BY","PREPARED_BY_ID");
        f.setUpperCase();
        f.setMaxLength(20);
        f.setDefaultNotVisible();

        f = headblk.addField("PREPARED_BY_ID");
        f.setSize(14);
        f.setHidden();
        f.setLabel("PCMWACTIVESEPARATEOVWREPORTEDBY3: Prepared By ID");
        f.setMaxLength(11);
        f.setDefaultNotVisible();

        f = headblk.addField("WORK_MASTER_SIGN");
        f.setSize(14);
        f.setDynamicLOV("EMPLOYEE_LOV",600,445);
        f.setLOVProperty("TITLE","PCMWACTIVESEPARATEOVWLOVEXECBY: List of Executed By");
        f.setLabel("PCMWACTIVESEPARATEOVWWORKMASTERSIG: Executed By");
        f.setCustomValidation("COMPANY,WORK_MASTER_SIGN","WORK_MASTER_SIGN_ID");
        f.setUpperCase();
        f.setMaxLength(20);
        f.setDefaultNotVisible();

        f = headblk.addField("WORK_MASTER_SIGN_ID");
        f.setSize(14);
        f.setHidden();
        f.setLabel("PCMWACTIVESEPARATEOVWREPORTEDBY4: Executed By ID");
        f.setMaxLength(11);

        f = headblk.addField("WORK_LEADER_SIGN");
        f.setSize(15);
        f.setDynamicLOV("EMPLOYEE_LOV",600,445);
        f.setLOVProperty("TITLE","PCMWACTIVESEPARATEOVWLOVTITLE2: List of Work Leader");
        f.setLabel("PCMWACTIVESEPARATEOVWWORKLEADERSIGN: Work Leader");
        f.setCustomValidation("COMPANY,WORK_LEADER_SIGN","WORK_LEADER_SIGN_ID");
        f.setUpperCase();
        f.setMaxLength(20);
        f.setDefaultNotVisible();

        f = headblk.addField("WORK_LEADER_SIGN_ID");
        f.setSize(14);
        f.setHidden();
        f.setLabel("PCMWACTIVESEPARATEOVWREPORTEDBY5: Reported By");
        f.setMaxLength(11);

        f = headblk.addField("AUTHORIZE_CODE");
        f.setSize(14);
        f.setDynamicLOV("ORDER_COORDINATOR_LOV",600,445);
        f.setLabel("PCMWACTIVESEPARATEOVWAUTHORIZECODE: Coordinator");
        f.setUpperCase();
        f.setReadOnly();
        f.setMaxLength(20);
        f.setDefaultNotVisible();

        f = headblk.addField("COMPANY");
        f.setSize(10);
        f.setHidden();
        f.setLabel("PCMWACTIVESEPARATEOVWCOMPANY: Company");
        f.setUpperCase();

        f = headblk.addField("COST_CENTER");
        f.setSize(14);
        f.setLabel("PCMWACTIVESEPARATEOVWCOSTCENTER: Cost Center");
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = headblk.addField("PROJECT_NO");
        f.setSize(10);
        f.setLabel("PCMWACTIVESEPARATEOVWPROJECTNO: Project No.");
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = headblk.addField("OBJECT_NO");
        f.setSize(10);
        f.setLabel("PCMWACTIVESEPARATEOVWOBJECTNO: Object no.");
        f.setMaxLength(10);
        f.setDefaultNotVisible();

        f = headblk.addField("LAST_ACTIVITY_DATE","Datetime");
        f.setSize(14);
        f.setLabel("PCMWACTIVESEPARATEOVWLASTACTIVITYDATE: Last Updated");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("TEST_POINT_ID");
        f.setSize(12);
        f.setDynamicLOV("EQUIPMENT_OBJECT_TEST_PNT","MCH_CODE,MCH_CODE_CONTRACT CONTRACT",600,445);
        f.setLabel("PCMWACTIVESEPARATEOVWTESTPOINTID: Testpoint");
        f.setUpperCase();
        f.setMaxLength(6);
        f.setDefaultNotVisible();

        f = headblk.addField("PM_NO","Number");
        f.setSize(10);
        f.setLabel("PCMWACTIVESEPARATEOVWPMNO: PM No.");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("PM_REVISION");
        f.setSize(10);
        f.setLabel("PCMWACTIVESEPARATEOVWPMREV: PM Revision.");
        f.setReadOnly();
        f.setMaxLength(6);
        f.setDefaultNotVisible();

        f = headblk.addField("QUOTATION_ID","Number");
        f.setSize(16);
        f.setDynamicLOV("WORK_ORDER_QUOTATION_LOV",600,445);
        f.setLabel("PCMWACTIVESEPARATEOVWQUOTATIONID: Quotation ID");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("SALESMANCODE");
        f.setSize(16);
        f.setDynamicLOV("PERSON_INFO",600,445);
        f.setLabel("PCMWACTIVESEPARATEOVWSALESMANCODE: Salesman Code");
        f.setFunction("WORK_ORDER_QUOTATION_API.Get_Salesman_Code(:QUOTATION_ID)");
        f.setReadOnly();
        f.setMaxLength(5);
        f.setDefaultNotVisible();

        f = headblk.addField("CUSTOMER_NO");
        f.setSize(14);
        f.setDynamicLOV("CUSTOMER_INFO",600,445);
        f.setLabel("PCMWACTIVESEPARATEOVWCUSTOMERNO: Customer No");
        f.setUpperCase();
        f.setReadOnly();
        f.setMaxLength(20);

        f = headblk.addField("CUSTOMERNAME");
        f.setSize(40);
        f.setLabel("PCMWACTIVESEPARATEOVWCUSTOMERNAME: Customer Name");
        f.setFunction("CUSTOMER_INFO_API.Get_Name(:CUSTOMER_NO)");
        f.setReadOnly();
        f.setMaxLength(100);
        f.setDefaultNotVisible();

        f = headblk.addField("CONTACT");
        f.setSize(12);
        f.setLabel("PCMWACTIVESEPARATEOVWCONTACT: Contact");
        f.setReadOnly();
        f.setMaxLength(30);
        f.setDefaultNotVisible();

        f = headblk.addField("REFERENCE_NO");
        f.setSize(12);
        f.setLabel("PCMWACTIVESEPARATEOVWREFERENCENO: Reference No.");
        f.setReadOnly();
        f.setMaxLength(25);
        f.setDefaultNotVisible();

        f = headblk.addField("PHONE_NO");
        f.setSize(12);
        f.setLabel("PCMWACTIVESEPARATEOVWPHONENO: Phone No.");
        //f.setReadOnly();
        f.setMaxLength(20);
        f.setDefaultNotVisible();

        f = headblk.addField("FIXED_PRICE");
        f.setSize(13);
        f.setLabel("PCMWACTIVESEPARATEOVWFIXEDPRICE: Fixed Price");
        f.setSelectBox();
        f.enumerateValues("FIXED_PRICE_API");
        f.setReadOnly();
        f.setMaxLength(20);
        f.setDefaultNotVisible();

        f = headblk.addField("ADDRESS1");
        f.setSize(35);
        f.setLabel("PCMWACTIVESEPARATEOVWADDRESS1: Address1/2");
        f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address1(:WO_NO)");
        f.setReadOnly();
        f.setMaxLength(100);
        f.setDefaultNotVisible();

        f = headblk.addField("ADDRESS2");
        f.setSize(35);
        f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address2(:WO_NO)");
        f.setReadOnly();
        f.setMaxLength(35);
        f.setDefaultNotVisible();

        f = headblk.addField("ADDRESS3");
        f.setSize(35);
        f.setLabel("PCMWACTIVESEPARATEOVWADDRESS3: Address3/4");
        f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address3(:WO_NO)");
        f.setReadOnly();
        f.setMaxLength(35);
        f.setDefaultNotVisible();

        f = headblk.addField("ADDRESS4");
        f.setSize(35);
        f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address4(:WO_NO)");
        f.setReadOnly();
        f.setMaxLength(35);
        f.setDefaultNotVisible();

        f = headblk.addField("ADDRESS5");
        f.setSize(35);
        f.setLabel("PCMWACTIVESEPARATEOVWADDRESS5: Address5/6");
        f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address5(:WO_NO)");
        f.setReadOnly();
        f.setMaxLength(35);
        f.setDefaultNotVisible();

        f = headblk.addField("ADDRESS6");
        f.setSize(35);
        f.setFunction("WORK_ORDER_ADDRESS_API.Get_Address6(:WO_NO)");
        f.setReadOnly();
        f.setMaxLength(35);
        f.setDefaultNotVisible();

        f = headblk.addField("ADDRESS_ID");
        f.setSize(8);
        f.setHidden();
        f.setLabel("PCMWACTIVESEPARATEOVWADDRESSID: Address Id");
        f.setUpperCase();
        f.setMaxLength(50);
        f.setDefaultNotVisible();

        f = headblk.addField("FAULT_REP_FLAG","Number");
        f.setSize(8);
        f.setHidden();
        f.setLabel("PCMWACTIVESEPARATEOVWFAULTREPFLAG: Fault Report Flag");

        f = headblk.addField("WORK_DESCR_LO");
        f.setSize(30);
        f.setLabel("PCMWACTIVESEPARATEOVWWORKDESLO: Work Description");

        //-------- Added fields for hyperlink(Documents) ------

        f = headblk.addField("LU_NAME");
        f.setHidden();
        f.setFunction("'ActiveSeparate'");

        f = headblk.addField("KEY_REF");
        f.setHidden();
        f.setFunction("CONCAT('WO_NO=',CONCAT(WO_NO,'^'))");

        f = headblk.addField("DOCUMENT");
        f.setFunction("substr(Doc_Reference_Object_API.Exist_Obj_Reference('ActiveSeparate',CONCAT('WO_NO=',CONCAT(WO_NO,'^'))),1, 5)");
        f.setUpperCase();
        f.setLabel("PCMWACTIVESEPARATEOVWDOCUMENT: Has Documents");
        f.setReadOnly();
        f.setCheckBox("FALSE,TRUE");
        f.setSize(18);
        f.setDefaultNotVisible();

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

        f = headblk.addField("CONNECTION_TYPE_DB");
        f.setHidden();

        f = headblk.addField("ISUSER");
        f.setHidden();
        f.setFunction("''");

        headblk.setView("ACTIVE_SEPARATE");
        headblk.defineCommand("ACTIVE_SEPARATE_API","New__,Modify__,Remove__,CONFIRM__,CANCEL__");
        headblk.disableDocMan();
        headset = headblk.getASPRowSet();


        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWACTIVESEPARATEOVWHD1: Active Work Requests"));
        headtbl.setWrap();

        //Web Alignment - Multirow Action
        headtbl.enableRowSelect();
        //

        headbar = mgr.newASPCommandBar(headblk);
        headbar.setBorderLines(false,true);
        headbar.addCustomCommand("prepareWo",mgr.translate("PCMWACTIVESEPARATEOVWPRE: Prepare..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("copy",mgr.translate("PCMWACTIVESEPARATEOVWCOP: Copy..."));
        headbar.addCustomCommand("printWO",mgr.translate("PCMWACTIVESEPARATEOVWPRNTWO: Print..."));
        headbar.addCustomCommand("printServO",mgr.translate("PCMWACTIVESEPARATEOVWPRNTSERO: Print Service Order..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("confirm",mgr.translate("PCMWACTIVESEPARATEOVWOBS: Observed"));
        headbar.addCustomCommand("cancel",mgr.translate("PCMWACTIVESEPARATEOVWCAN: Cancelled"));
        headbar.defineCommand(headbar.SAVERETURN,"saveReturn","checkMandoFields(-1)");
        headbar.defineCommand(headbar.SAVENEW,"saveNew","checkMandoFields(-1)");

        //Web Alignment - Grouping RMBs together
        headbar.addCustomCommandGroup("WOSTATUS",mgr.translate("PCMWACTIVESEPARATEOVWWOSTATUS: Work Order Status"));
        headbar.setCustomCommandGroup("confirm","WOSTATUS"); 
        headbar.setCustomCommandGroup("cancel","WOSTATUS"); 
        //

        //Web Alignment - Multirow Action
        headbar.enableMultirowAction();
        headbar.removeFromMultirowAction("copy");

        //--------------------------------

        //This block is for print purposes 

        printblk = mgr.newASPBlock("PRINT");

        f = printblk .addField("RESULT_KEY");
        f.setFunction("''");
        f.setHidden();

        printset = printblk.getASPRowSet();

        //-------------------------------

        b = mgr.newASPBlock("STATES");

        b.addField("CLIENT_VALUES0");

        b = mgr.newASPBlock("PMTYPE");

        b.addField("CLIENT_VALUES1");

        b = mgr.newASPBlock("FIXEDPRICE");

        b.addField("CLIENT_VALUES2");
        head_command = headbar.getSelectedCustomCommand();

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        headlay.setDialogColumns(2);

        headlay.defineGroup("","WO_NO,ERR_DESCR,CONTRACT,REPORTED_BY,REG_DATE,STATE",false,true);   
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATEOVWCONTINFO: Contact Information"),"CONTACT,PHONE_NO,REFERENCE_NO",true,false);
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATEOVWCUSTOMER: Customer"),"CUSTOMER_NO,CUSTOMERNAME,AUTHORIZE_CODE",true,false);
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATEOVWOBJECT: Object"),"CONNECTION_TYPE,MCH_CODE,MCH_CODE_DESCRIPTION,MCH_CODE_CONTRACT,LATEST_TRANSACTION,GROUPID,CRITICALITY,ADDRESS1,ADDRESS2,ADDRESS3,ADDRESS4,ADDRESS5,ADDRESS6",true,false);
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATEOVWPLANINFO: Planning Information"),"ORG_CODE,ORGCODEDESCR,PRIORITY_ID,PRIORITYDESC,WORK_TYPE_ID,WORKTYPEDESC,WORK_DESCR_LO",true,false);
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATEOVWPLANSCHEDULE: Planning Schedule"),"PLAN_S_DATE,REQUIRED_S_DATE,PLAN_F_DATE,REQUIRED_E_DATE,PLAN_HRS",true,false);
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATEOVWSIGNATURES: Signatures"),"WORK_MASTER_SIGN,WORK_LEADER_SIGN",true,false);
        headlay.defineGroup(mgr.translate("PCMWACTIVESEPARATEOVWPMINFOMA: PM Information"),"PM_NO,PM_REVISION",true,false);

        headlay.setSimple("CUSTOMERNAME");
        headlay.setSimple("ADDRESS2");
        headlay.setSimple("ADDRESS4");
        headlay.setSimple("ADDRESS6");
        headlay.setSimple("ORGCODEDESCR");
        headlay.setSimple("PRIORITYDESC");
        headlay.setSimple("WORKTYPEDESC");

        headlay.setFieldSpan("CUSTOMER_NO",1,3);
        headlay.setFieldSpan("ADDRESS1",1,3);
        headlay.setFieldSpan("ADDRESS3",1,3);
        headlay.setFieldSpan("ADDRESS5",1,3);
    }


    public void getViewNames()
    {
        ASPManager mgr = getASPManager();
        String viewName1=null;
        String viewName=null;
        String titleName1=null;
        String viewName2=null;
        String titleName2=null;
        String viewName3=null;
        String titleName3=null;

        trans.clear();

        cmd = trans.addCustomFunction( "GETCON","User_Default_API.Get_Contract","CONTRACT1" );

        cmd = trans.addCustomFunction( "GETCOM","Site_API.Get_Company","COMPANY1" );
        cmd.addReference("CONTRACT1","GETCON/DATA");

        cmd = trans.addCustomCommand( "GETCODE1","Maintenance_Accounting_API.Get_Log_Code_Part");
        cmd.addParameter("CODEPARTCOSTCENTER");
        cmd.addParameter("VIEWNAME");
        cmd.addParameter("PKGNAME");
        cmd.addParameter("INTERNAME");
        cmd.addReference("COMPANY1","GETCOM/DATA");
        cmd.addParameter("LOGCODEPARTCOSTCENTER","CostCenter");

        cmd = trans.addCustomFunction( "GETNAME1","Accounting_Code_Parts_API.Get_Name","COST_CENTER");
        cmd.addReference("COMPANY1","GETCOM/DATA");
        cmd.addReference("CODEPARTCOSTCENTER","GETCODE1/DATA");

        cmd = trans.addCustomCommand( "GETCODE2","Maintenance_Accounting_API.Get_Log_Code_Part");
        cmd.addParameter("CODEPARTCOSTCENTER");
        cmd.addParameter("VIEWNAME");
        cmd.addParameter("PKGNAME");
        cmd.addParameter("INTERNAME");
        cmd.addReference("COMPANY1","GETCOM/DATA");
        cmd.addParameter("LOGCODEPARTCOSTCENTER","Project");

        cmd = trans.addCustomFunction( "GETNAME2","Accounting_Code_Parts_API.Get_Name","PROJECT_NO");
        cmd.addReference("COMPANY1","GETCOM/DATA");
        cmd.addReference("CODEPARTCOSTCENTER","GETCODE2/DATA");

        cmd = trans.addCustomCommand( "GETCODE3","Maintenance_Accounting_API.Get_Log_Code_Part");
        cmd.addParameter("CODEPARTCOSTCENTER");
        cmd.addParameter("VIEWNAME");
        cmd.addParameter("PKGNAME");
        cmd.addParameter("INTERNAME");
        cmd.addReference("COMPANY1","GETCOM/DATA");
        cmd.addParameter("LOGCODEPARTCOSTCENTER","Object");

        cmd = trans.addCustomFunction( "GETNAME3","Accounting_Code_Parts_API.Get_Name","OBJECT_NO");
        cmd.addReference("COMPANY1","GETCOM/DATA");
        cmd.addReference("CODEPARTCOSTCENTER","GETCODE3/DATA");

        trans = mgr.perform(trans);

        sCompany = trans.getValue("GETCOM/DATA/COMPANY1");

        viewName1 = trans.getValue("GETCODE1/DATA/VIEWNAME");
        titleName1 = trans.getValue("GETNAME1/DATA/COST_CENTER");

        viewName2 = trans.getValue("GETCODE2/DATA/VIEWNAME");
        titleName2 = trans.getValue("GETNAME2/DATA/PROJECT_NO");

        viewName3 = trans.getValue("GETCODE3/DATA/VIEWNAME");
        titleName3 = trans.getValue("GETNAME3/DATA/OBJECT_NO");

        // 031002  ARWILK  Begin (Bug#105066)

        //mgr.getASPField("COST_CENTER").setDynamicLOV(viewName1,"GETCOM/DATA/COMPANY1 COMPANY",600,445);
        mgr.getASPField("COST_CENTER").setDynamicLOV(viewName1, 600, 445);
        mgr.getASPField("COST_CENTER").setLOVProperty("WHERE","COMPANY='"+sCompany+"'");
        mgr.getASPField("COST_CENTER").setLabel(titleName1);

        //mgr.getASPField("PROJECT_NO").setDynamicLOV(viewName2,"GETCOM/DATA/COMPANY1 COMPANY",600,445);
        mgr.getASPField("PROJECT_NO").setDynamicLOV(viewName2, 600, 445);
        mgr.getASPField("PROJECT_NO").setLOVProperty("WHERE","COMPANY='"+sCompany+"'");
        mgr.getASPField("PROJECT_NO").setLabel(titleName2);

        //mgr.getASPField("OBJECT_NO").setDynamicLOV(viewName3,"GETCOM/DATA/COMPANY1 COMPANY",600,445);   
        mgr.getASPField("OBJECT_NO").setDynamicLOV(viewName3, 600, 445);   
        mgr.getASPField("OBJECT_NO").setLOVProperty("WHERE","COMPANY='"+sCompany+"'");
        mgr.getASPField("OBJECT_NO").setLabel(titleName3);

        // 031002  ARWILK  End (Bug#105066)

        mgr.getASPField("PREPARED_BY").setLOVProperty("WHERE","COMPANY='"+sCompany+"'");
        mgr.getASPField("WORK_MASTER_SIGN").setLOVProperty("WHERE","COMPANY='"+sCompany+"'");
        mgr.getASPField("WORK_LEADER_SIGN").setLOVProperty("WHERE","COMPANY='"+sCompany+"'");
        mgr.getASPField("REPORTED_BY").setLOVProperty("WHERE","COMPANY='"+sCompany+"'");

    }

    public void checkObjAvaileble()
    {
        ASPManager mgr = getASPManager();

        ASPBuffer availObj;
        trans.clear();
        trans.addSecurityQuery("CUSTOMER_AGREEMENT,ACTIVE_SEP_WO_PRINT_REP");
        trans.addSecurityQuery("Active_Work_Order_API","Copy__");
        trans.addPresentationObjectQuery("PCMW/CopyWorkOrderDlg.page,PCMW/ActiveSeparate2.page");

        trans = mgr.perform(trans);

        availObj = trans.getSecurityInfo();

        if (availObj.itemExists("CUSTOMER_AGREEMENT"))
            printServO_ = true;

        if (availObj.itemExists("ACTIVE_SEP_WO_PRINT_REP"))

            printWO_ = true;

        if (availObj.itemExists("Active_Work_Order_API.Copy__") && availObj.namedItemExists("PCMW/CopyWorkOrderDlg.page"))
            copy_= true;

        if (availObj.namedItemExists("PCMW/ActiveSeparate2.page"))
            prepareWo_= true;
    }

    public void DissableRmbs()
    {
        ASPManager mgr = getASPManager();

        if (!printServO_)
            headbar.removeCustomCommand("printServO");
        if (!printWO_)
            headbar.removeCustomCommand("printWO");

        if (!copy_)
            headbar.removeCustomCommand("copy");
        if (!prepareWo_)
            headbar.removeCustomCommand("prepareWo");

    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        DissableRmbs();

        if (mgr.isPresentationObjectInstalled("equipw/EquipmentAllObjectLightRedirect.page"))
            mgr.getASPField("MCH_CODE").setHyperlink("../equipw/EquipmentAllObjectLightRedirect.page","MCH_CODE,MCH_CODE_CONTRACT,WO_NO","NEWWIN");
        if (mgr.isPresentationObjectInstalled("equipw/EquipmentGeneralDataGroups.page"))
            mgr.getASPField("GROUPID").setHyperlink("../equipw/EquipmentGeneralDataGroups.page","GROUPID","NEWWIN");
        if (mgr.isPresentationObjectInstalled("enterw/SupplierInfoGeneral.page"))
            mgr.getASPField("VENDOR_NO").setHyperlink("../enterw/SupplierInfoGeneral.page","VENDOR_NO","NEWWIN");
        if (mgr.isPresentationObjectInstalled("enterw/CustomerInfo.page"))
            mgr.getASPField("CUSTOMER_NO").setHyperlink("../enterw/CustomerInfo.page","CUSTOMER_NO","NEWWIN");
        if (mgr.isPresentationObjectInstalled("docmaw/DocReference.page"))
            mgr.getASPField("DOCUMENT").setHyperlink("../docmaw/DocReference.page","LU_NAME,KEY_REF","NEWWIN");
        getViewNames();

        if (!(headlay.isMultirowLayout()))
        {
            mgr.getASPField("LAST_ACTIVITY_DATE").setHidden();
            //mgr.getASPField("PM_NO").setHidden();
            //mgr.getASPField("PM_REVISION").setHidden();
            mgr.getASPField("FIXED_PRICE").setHidden();
            mgr.getASPField("ADDRESS_ID").setHidden();

            if (headlay.isEditLayout())
            {
                String cType = headset.getRow().getValue("CONNECTION_TYPE_DB");
                if ("VIM".equals(cType))
                {
                    mgr.getASPField("MCH_CODE").setReadOnly();
                    mgr.getASPField("MCH_CODE_CONTRACT").setReadOnly();
                }
            }
        }

    }


//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWACTIVESEPARATEOVWTITLE1: Overview - Active Work Requests";
    }

    protected String getTitle()
    {
        return "PCMWACTIVESEPARATEOVWTITLE1: Overview - Active Work Requests";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        if (headlay.isVisible())
            appendToHTML(headlay.show());

        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(newreq)); // Bug Id 68773
        appendDirtyJavaScript("' == \"TRUE\")\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("	    cntMAIN4.style.display='block';\n");
        //appendDirtyJavaScript("	    cntMAIN4End.style.display='block';\n");
        appendDirtyJavaScript("	    setMinimizeImage(document['groupMAIN4'],'cntMAIN4',false)\n");
        appendDirtyJavaScript("	}\n");

        appendDirtyJavaScript("function checkMandoFields(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	return checkReportedBy(i) &&\n");
        appendDirtyJavaScript("	checkErrDescr(i) &&\n");
        appendDirtyJavaScript("	checkConnectionType(i) &&\n");
        appendDirtyJavaScript("	checkOrgCode(i);\n");
        appendDirtyJavaScript("}\n");
	
        // XSS_Safe ILSOLK 20070706
        if (bOpenNewWindow)
        {
            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
        }
    }
}
