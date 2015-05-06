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
*  File        : ActiveWorkOrder3.java 
*  Modified    :
*  SHFELK  010216  Created.
*  JEWILK  011008  Checked security permissions for RMB Actions.
*  YAWILK  021202  Added MCH_CODE_CONTRACT and Modified MCH_CODE max length to 100
*  ARWILK  031218  Edge Developments - (Removed clone and doReset Methods)
*  VAGULK  040109  Made field order according to the order in the Centura application(Web Alignment).
*  ARWILK  040720  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning) 
*  ARWILK  041111  Replaced getContents with printContents.
*  SHAFLK  050106  Bug 48741, Modified methods transcustorder(), okFind() and preDefine().
*  NIJALK  050117  Merged bug 48741. 
*  NIJALK  051024  Bug 129030: Added hyperlink to field WO_NO & RMB "Transfer to Customer Order...".            
*  BUNILK  070504  Implemented "MTIS907 New Service Contract - Services" changes.
*  AMDILK  070607  Call Id 145865: Inserted new information for the customer info tab; Contrat name, contract type, 
*                  line description and invoice type
*  ILSOLK  070709  Eliminated XSS.
*  HARPLK  090708  Bug 84436, Modified preDefine().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ActiveWorkOrder3 extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ActiveWorkOrder3");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPContext ctx;

    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPCommandBar headbar;
    private ASPTable headtbl;
    private ASPBlockLayout headlay;

    private ASPField f;
    private ASPBlock b;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private String wo_no;
    private String mch_code;
    private String work_type_id;
    private String agreement_id;
    private String customerno;
    private String contract;
    private ASPTransactionBuffer trans;
    private String qrystr;
    private ASPCommand cmd;
    private ASPQuery q;
    private int currrow;
    private ASPBuffer temp;
    private String current_url;
    private ASPBuffer row;
    private ASPBuffer buffer;
    private boolean isSecurityChecked;
    private ASPBuffer actionsBuffer;
    private boolean bOpenNewWindow;
    private String urlString;
    private String newWinHandle;

    //===============================================================
    // Construction 
    //===============================================================
    public ActiveWorkOrder3(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        wo_no =  "";
        mch_code =  "";
        work_type_id =  "";
        agreement_id =  "";
        customerno = "";
        contract = "";
        ASPManager mgr = getASPManager(); 

        trans = mgr.newASPTransactionBuffer();
        ctx = mgr.getASPContext();

        qrystr = ctx.readValue("QRYSTR","");

        isSecurityChecked = ctx.readFlag("SECURITYCHECKED",false);
        actionsBuffer = ctx.readBuffer("ACTIONSBUFFER");

        if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();
        else if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (mgr.dataTransfered())
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE")))
            okFind1();

        checkObjAvailable();
        adjustActions();

        ctx.writeValue("QRYSTR",qrystr); 
        ctx.writeFlag("SECURITYCHECKED",isSecurityChecked);
        ctx.writeBuffer("ACTIONSBUFFER",actionsBuffer);
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();
        String val = null;
        String oldcusnam = null;
        String name = null;
        String mchCodeCon = null;
        String worktypedes = null;
        String orgedes = null;
        String maintgrpid = null;
        String hasconn = null;
        String txt = null;
        val = mgr.readValue("VALIDATE");

        if ("CUSTOMER_NO".equals(val))
        {
            cmd = trans.addCustomFunction("OLDCUNAME","Equipment_All_Object_API.Get_Mch_Name","CUSTNAME");
            cmd.addParameter("CUSTOMER_NO");

            trans = mgr.validate(trans);

            oldcusnam = trans.getValue("OLDCUNAME/DATA/OLDCUSTNAME");

            txt = (mgr.isEmpty(oldcusnam)? "" : oldcusnam);
            mgr.responseWrite(txt);
        }
        else if ("MCH_CODE".equals(val))
        {
            cmd = trans.addCustomFunction("NAME","MAINTENANCE_OBJECT_API.Get_Mch_Name","MCH_CODE_DESCRIPTION");
            cmd.addParameter("MCH_CODE_CONTRACT");
            cmd.addParameter("MCH_CODE");

            trans = mgr.validate(trans);

            name = trans.getValue("NAME/DATA/MCH_CODE_DESCRIPTION");

            txt = (mgr.isEmpty(name)? "" : name)+ "^";

            mgr.responseWrite(txt);
        }
        else if ("WORK_TYPE_ID".equals(val))
        {
            cmd = trans.addCustomFunction("WORKTYPEDES","WORK_TYPE_API.Get_Description","WORKTYPEDESCRIPTION");
            cmd.addParameter("WORK_TYPE_ID");

            trans = mgr.validate(trans);

            worktypedes = trans.getValue("WORKTYPEDES/DATA/WORKTYPEDESCRIPTION");

            txt = (mgr.isEmpty(worktypedes)? "" : worktypedes);
            mgr.responseWrite(txt);
        }
        else if ("ORG_CODE".equals(val))
        {
            cmd = trans.addCustomFunction("ORGDESCRIP","ORGANIZATION_API.Get_Description","ORGANIZATIONDESCRIPTION");
            cmd.addParameter("CONTRACT");
            cmd.addParameter("ORG_CODE");

            trans = mgr.validate(trans);

            orgedes = trans.getValue("ORGDESCRIP/DATA/ORGANIZATIONDESCRIPTION");

            txt = (mgr.isEmpty(orgedes)? "" : orgedes);
            mgr.responseWrite(txt);
        }
        else if ("MCH_CODE".equals(val))
        {
            cmd = trans.addCustomFunction("MAINTGRPID","MAINTENANCE_OBJECT_API.Get_Group_Id","MAINTENANCEOBJECTGROUP_ID");
            cmd.addParameter("MCH_CODE_CONTRACT");
            cmd.addParameter("MCH_CODE");

            trans = mgr.validate(trans);

            maintgrpid = trans.getValue("MAINTGRPID/DATA/ORGANIZATIONDESCRIPTION");

            txt = (mgr.isEmpty(maintgrpid)? "" : maintgrpid);
            mgr.responseWrite(txt);
        }
        else if ("WO_NO".equals(val))
        {
            cmd = trans.addCustomFunction("HASCONN","Work_Order_Connection_API.Has_Connection_Down","CBHASSTRUCTURE");
            cmd.addParameter("WO_NO");

            trans = mgr.validate(trans);

            hasconn = trans.getValue("HASCONN/DATA/CBHASSTRUCTURE");

            txt = (mgr.isEmpty(hasconn)? "" : hasconn);
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
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
        q.addWhereCondition("Work_Order_Coding_Utility_API.Is_Wo_Transferable(WO_NO)  =  'TRUE' "); 
        q.includeMeta("ALL");

        mgr.submit(trans);

        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();
    }

    public void okFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
        q.addWhereCondition("Work_Order_Coding_Utility_API.Is_Wo_Transferable(WO_NO) = 'TRUE'"); 
        q.setOrderByClause("WO_NO");

        if (mgr.dataTransfered())
            q.addOrCondition( mgr.getTransferedData() );

        q.includeMeta("ALL");


        if (mgr.dataTransfered())
            q.addOrCondition( mgr.getTransferedData() );
        //Bug 48741, start
        mgr.querySubmit(trans,headblk);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWACTIVEWORKORDER3NODATA: No data found."));
            headset.clear();
        }
    }

    public void okFind1()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
        q.addWhereCondition("Work_Order_Coding_Utility_API.Is_Wo_Transferable(WO_NO)  =  'TRUE' "); 
        q.setOrderByClause("WO_NO");

        if (mgr.dataTransfered())
            q.addOrCondition(mgr.getTransferedData());

        q.includeMeta("ALL");

        if (mgr.dataTransfered())
            q.addOrCondition(mgr.getTransferedData());

        mgr.submit(trans);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWACTIVEWORKORDER3NODATA: No data found."));
        }
        else if (headset.countRows() < headset.countDbRows())
        {
            int cntRow = headset.countRows();
            String cntRowStr = String.valueOf(cntRow);
            int cntRowDb = headset.countDbRows();
            String cntRowDbStr = String.valueOf(cntRowDb);
            mgr.showAlert(mgr.translate( "PCMWACTIVEWORKORDER3MOREDATA: You have fetched the first &1 lines of &2 lines.", cntRowStr,cntRowDbStr));
        }

        if (headset.countRows() != 0)
        {
            headbar.enableCommand(headbar.FAVORITE);
            mgr.createSearchURL(headblk);
        }

        headtbl.clearQueryRow();
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void none()
    {
        ASPManager mgr = getASPManager();

        mgr.showAlert(mgr.translate("PCMWACTIVEWORKORDER3NORMB: No RMB Methods has been selected"));
    }

    public void transcustorder()
    {

        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());


        cmd = trans.addCustomCommand("FUNC","Work_Order_Coding_Utility_API.Create_Customer_Order");
        cmd.addParameter("TRANS_COUNT","0");
        cmd.addParameter("UNTRANSFERED_ROWS","TRUE");
        cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));

        trans = mgr.perform(trans);

        trans.clear();
        okFind();
    }

    public void gototranscustorder()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.storeSelections();
        else
        {
            headset.unselectRows();
            headset.selectRow();
        }

        bOpenNewWindow = true;
        urlString = createTransferUrl("ActiveWorkOrder4.page", headset.getSelectedRows("WO_NO"));
        newWinHandle = "AciveWorkOrder4"; 
    }

    public void addCoToWOr()
    {
        ASPManager mgr = getASPManager();
        String nWoNo = null;
        String strAuthoCode = null;

        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            headset.setFilterOn();
        }
        current_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",current_url);

        nWoNo=headset.getRow().getValue("WO_NO");
        strAuthoCode=headset.getRow().getValue("AUTHORIZE_CODE");

        buffer = mgr.newASPBuffer();
        row = buffer.addBuffer("0");
        row.addItem("WO_NO",nWoNo);
        row = buffer.addBuffer("1");
        row.addItem("AUTHORIZE_CODE",strAuthoCode);

        mgr.transferDataTo("OrderCoordinatorDlg.page",buffer);
    }

    public void changeCustomer()
    {
        ASPManager mgr = getASPManager();
        String strmchcode = null;
        String worktyid = null;
        double n = 0;

        headset.storeSelections();
        current_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",current_url);

	if (headlay.isMultirowLayout())
            headset.store();
        else
        {
            headset.unselectRows();
            headset.selectRow();
        }

        headset.setFilterOn();

        strmchcode=headset.getRow().getValue("MCH_CODE");
        worktyid=headset.getRow().getValue("WORK_TYPE_ID");
        n = toDouble(headset.countSelectedRows());

        if ((mgr.isEmpty(strmchcode))|| (mgr.isEmpty(worktyid)))
        {
            mgr.showAlert(mgr.translate("PCMWACTIVEWORKORDER3NOCHAN: Can not perform Change Customer for selected line(s)"));
        }
        else
        {
            buffer = mgr.newASPBuffer();
            row = buffer.addBuffer("0");
            row.addItem("WO_NO",headset.getRow().getValue("WO_NO"));
            row = buffer.addBuffer("1");
            row.addItem("MCH_CODE",headset.getRow().getValue("MCH_CODE"));
            row = buffer.addBuffer("2");
            row.addItem("WORK_TYPE_ID",headset.getRow().getValue("WORK_TYPE_ID"));
            row = buffer.addBuffer("3");
            row.addItem("AGREEMENT_ID",headset.getRow().getValue("AGREEMENT_ID"));
            row = buffer.addBuffer("4");
            row.addItem("CUSTOMER_NO",headset.getRow().getValue("CUSTOMER_NO"));
            row = buffer.addBuffer("5");
            row.addItem("CONTRACT",headset.getRow().getValue("CONTRACT"));
	    row = buffer.addBuffer("6");
            row.addItem("MCH_CODE_CONTRACT",headset.getRow().getValue("MCH_CODE_CONTRACT"));

            mgr.transferDataTo("ActiveWorkOrderDlg.page",buffer);
        }
    }

    public void custOrder()
    {
        ASPManager mgr = getASPManager();


        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());

        currrow = headset.getCurrentRowNo();

        mgr.redirectTo("../orderw/CustomerOrder.page");
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
//----------------------------  SELECT FUNCTION  ------------------------------
//-----------------------------------------------------------------------------

    public void select()
    {
        headset.switchSelections();
    }

    public void preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        headblk.addField("OBJID").
        setHidden();

        headblk.addField("OBJVERSION").
        setHidden();

        headblk.addField("WO_NO","Number").
        setSize(10).
        setMaxLength(8).
        setLabel("PCMWACTIVEWORKORDER3WO_NO: WO No").
        setHyperlink("ActiveSeparate2.page","WO_NO","NEWWIN").
        setReadOnly();

        headblk.addField("CONTRACT").
        setSize(10).
        setMaxLength(5).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setMandatory().
        setLabel("PCMWACTIVEWORKORDER3CONTRACT: WO Site").
        setUpperCase().
        setReadOnly();

        headblk.addField("CUSTOMER_NO").
        setSize(25).
        setMaxLength(20).
        setDynamicLOV("CUSTOMER_INFO",600,450).
        setLabel("PCMWACTIVEWORKORDER3CUSTOMER_NO: Customer No").
        //setFunction("Sc_Service_Contract_API.Get_Customer_Id(:CONTRACT_ID)").
        setUpperCase().
        setReadOnly();

        headblk.addField("PARTYTYPECUSTOMERNAME").
        setSize(25).
        setLabel("PCMWACTIVEWORKORDER3PARTYTYPECUSTOMERNAME: Customer Name").
        setFunction("CUSTOMER_INFO_API.Get_Name(:CUSTOMER_NO)").
        setReadOnly();

        headblk.addField("REFERENCE_NO").
        setSize(25).
        setMaxLength(25).
        setLabel("PCMWACTIVEWORKORDER3REFERENCE_NO: Reference No").
        setReadOnly();

        headblk.addField("MCH_CODE").
        setSize(25).
        setMaxLength(100).
        setDynamicLOV("MAINTENANCE_OBJECT",600,450).
        setLabel("PCMWACTIVEWORKORDER3MCH_CODE: Object ID").
        setUpperCase().
        setReadOnly();

        headblk.addField("MCH_CODE_CONTRACT").
        setSize(10).
        setMaxLength(5).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setMandatory().
        setLabel("PCMWACTIVEWORKORDER3MCHCODECONTRACT: Site").
        setUpperCase().
        setReadOnly();

        headblk.addField("MCH_CODE_DESCRIPTION").
        setSize(25).
        setLabel("PCMWACTIVEWORKORDER3MCH_CODE_DESCRIPTION: Object Description").
        setReadOnly().
        setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:MCH_CODE_CONTRACT,:MCH_CODE)");
        mgr.getASPField("MCH_CODE").setValidation("MCH_CODE_DESCRIPTION");

        headblk.addField("WORK_TYPE_ID").
        setSize(10).
        setMaxLength(20).
        setDynamicLOV("WORK_TYPE",600,450).
        setLabel("PCMWACTIVEWORKORDER3WORK_TYPE_ID: Work Type").
        setUpperCase().
        setReadOnly();

        headblk.addField("WORKTYPEDESCRIPTION").
        setSize(25).
        setLabel("PCMWACTIVEWORKORDER3WORKTYPEDESCRIPTION: Work Type Description").
        setFunction("WORK_TYPE_API.Get_Description(:WORK_TYPE_ID)").
        setReadOnly();

        headblk.addField("CONTRACT_ID").
        setUpperCase().
        setMaxLength(15).
        setReadOnly().
        setLabel("PCMWACTIVEWORKORDER3CONTRACTID: Contract ID").
        setSize(15);

         //Bug 84436, Start
        f = headblk.addField("CONTRACT_NAME");                   
        f.setDefaultNotVisible();       
        if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("SC_SERVICE_CONTRACT_API.Get_Contract_Name(:CONTRACT_ID)");
        else
            f.setFunction("''");          
        f.setReadOnly();
        f.setLabel("PCMWACTIVEWORKORDER3CONTRACTNAME: Contract Name");
        f.setSize(15);
         //Bug 84436, End

        headblk.addField("LINE_NO","Number").
        setReadOnly().
        setLabel("PCMWACTIVEWORKORDER3LINENO: Line No").
        setSize(10);

        //Bug 84436, Start
        f = headblk.addField("LINE_DESC");   
        if (mgr.isModuleInstalled("PCMSCI"))
            f.setFunction("PSC_CONTR_PRODUCT_API.Get_Description(:CONTRACT_ID,:LINE_NO)");
        else
            f.setFunction("''");
        f.setReadOnly();
        f.setLabel("PCMWACTIVEWORKORDER3LINEDESC: Description");
        f.setSize(15);
        
        f = headblk.addField("CONTRACT_TYPE");          
         if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("SC_SERVICE_CONTRACT_API.Get_Contract_Type(:CONTRACT_ID)");
         else
            f.setFunction("''");         
        f.setReadOnly();
        f.setLabel("PCMWACTIVEWORKORDER3CONTRACTTYPE: Contract Type");
        f.setSize(15);
        

        f = headblk.addField("INVOICE_TYPE");  
        if (mgr.isModuleInstalled("PCMSCI"))
            f.setFunction("PSC_CONTR_PRODUCT_API.Get_Invoice_Type(:CONTRACT_ID,:LINE_NO)");
        else
            f.setFunction("''");
        f.setReadOnly();
        f.setLabel("PCMWACTIVEWORKORDER3INVOICETYPE: Invoice Type");
        f.setSize(15);
        //Bug 84436, End
   
        headblk.addField("AGREEMENT_ID").
        setSize(10).
        setMaxLength(10).
        setDynamicLOV("CUSTOMER_AGREEMENT",600,450).
        setLabel("PCMWACTIVEWORKORDER3AGREEMENT_ID: Agreement ID").
        setUpperCase().
	setHidden().
        setReadOnly();

        //Bug 84436, Start
        f = headblk.addField("AUTHORIZE_CODE");
        f.setSize(25);
        f.setMaxLength(20);
        f.setDynamicLOV("ORDER_COORDINATOR",600,450);        
        if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("Sc_Service_Contract_API.Get_Authorize_Code(:CONTRACT_ID)");
        else
            f.setFunction("''");        
        f.setLabel("PCMWACTIVEWORKORDER3AUTHORIZE_CODE: Coordinator");
        f.setUpperCase();
        f.setReadOnly();
        //Bug 84436, End 

        headblk.addField("ORG_CODE").
        setSize(10).
        setMaxLength(8).
        setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,450).
        setMandatory().
        setLabel("PCMWACTIVEWORKORDER3ORG_CODE: Maintenance Organization").
        setUpperCase().
        setReadOnly();

        headblk.addField("ORGANIZATIONDESCRIPTION").
        setSize(25).
        setLabel("PCMWACTIVEWORKORDER3ORGANIZATIONDESCRIPTION: Organization Description").
        setReadOnly().
        setFunction("ORGANIZATION_API.Get_Description(:CONTRACT,:ORG_CODE)");

        headblk.addField("MAINTENANCEOBJECTGROUP_ID").
        setSize(25).
        setDynamicLOV("EQUIPMENT_OBJ_GROUP",600,450).
        setLabel("PCMWACTIVEWORKORDER3MAINTENANCEOBJECTGROUP_ID: Group ID").
        setFunction("MAINTENANCE_OBJECT_API.Get_Group_Id(:MCH_CODE_CONTRACT,:MCH_CODE)").
        setReadOnly();

        headblk.addField("REAL_F_DATE","Datetime").
        setSize(10).
        setLabel("PCMWACTIVEWORKORDER3REAL_F_DATE: Actual Completion").
        setReadOnly();

        headblk.addField("CBHASSTRUCTURE").
        setSize(12).
        setLabel("PCMWACTIVEWORKORDER3CBHASSTRUCTURE: Has Structure").
        setFunction("Work_Order_Connection_API.Has_Connection_Down(:WO_NO)").
        setReadOnly().
        setCheckBox("FALSE,TRUE");

        f = headblk.addField("TRANS_COUNT", "Number").
            setHidden().
            setFunction("''");

        f = headblk.addField("UNTRANSFERED_ROWS").
            setHidden().
            setFunction("''");


        headblk.addField("PARENT","Number").
        setSize(10).
        setLabel("PCMWACTIVEWORKORDER3PARENT: Parent WO No").
        setFunction("WORK_ORDER_CONNECTION_API.GET_PARENT(:WO_NO)").
        setReadOnly();

        headblk.setView("WORK_ORDER");
        headblk.defineCommand("ACTIVE_WORK_ORDER_API","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.setBorderLines(false,true);

        headbar.disableCommand(headbar.DELETE);
        headbar.disableCommand(headbar.EDITROW);
        headbar.disableCommand(headbar.NEWROW);
        headbar.disableCommand(headbar.DUPLICATEROW);      
        headbar.addCustomCommand("none","");
        headbar.addCustomCommand("transcustorder",mgr.translate("PCMWACTIVEWORKORDER3CREACUSTORDELINE: Create Customer Order"));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("gototranscustorder",mgr.translate("PCMWACTIVEWORKORDER3TRANSCUSTORDELINE: Transfer to Customer Order..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("addCoToWOr",mgr.translate("PCMWACTIVEWORKORDER3COORDWO: Add Coordinator to Work Order..."));
        headbar.addCustomCommand("changeCustomer",mgr.translate("PCMWACTIVEWORKORDER3CHGCUS: Change Customer..."));

        headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields(-1)");
        headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWACTIVEWORKORDER3HD: Transfer to Customer Order"));
        headtbl.setWrap();

        b = mgr.newASPBlock("ORG_CODE");

        b.addField("CLIENT_VALUES0");
    }

    public void checkObjAvailable()
    {
        ASPManager mgr = getASPManager();

        if (!isSecurityChecked)
        {
            trans.clear();

            trans.addSecurityQuery("ACTIVE_WORK_ORDER,CUSTOMER_ORDER");

            trans.addSecurityQuery("Work_Order_Coding_Utility_API","Create_Customer_Order");
            trans.addSecurityQuery("Active_Work_Order_API","Modify_Coordinator");

            trans.addPresentationObjectQuery("ORDERW/CustomerOrder.page,"+
                                             "PCMW/OrderCoordinatorDlg.page,"+
                                             "PCMW/ActiveWorkOrderDlg.page");

            trans = mgr.perform(trans);

            ASPBuffer secViewBuff = trans.getSecurityInfo();

            actionsBuffer = mgr.newASPBuffer();

            if (secViewBuff.itemExists("Work_Order_Coding_Utility_API.Create_Customer_Order"))
                actionsBuffer.addItem("okHeadTranscustorder","");

            if (secViewBuff.itemExists("CUSTOMER_ORDER") && secViewBuff.namedItemExists("ORDERW/CustomerOrder.page"))
                actionsBuffer.addItem("okHeadCustOrder","");

            if (secViewBuff.itemExists("Active_Work_Order_API.Modify_Coordinator") && secViewBuff.namedItemExists("PCMW/OrderCoordinatorDlg.page"))
                actionsBuffer.addItem("okHeadAddCoToWOr","");

            if (secViewBuff.itemExists("ACTIVE_WORK_ORDER") && secViewBuff.namedItemExists("PCMW/ActiveWorkOrderDlg.page"))
                actionsBuffer.addItem("okHeadChangeCustomer","");

            isSecurityChecked = true;
        }
    }

    public void adjustActions()
    {
        ASPManager mgr = getASPManager();

        // Removing actions which are not allowed to the user.
        // headbar
        if (!actionsBuffer.itemExists("okHeadTranscustorder"))
            headbar.removeCustomCommand("transcustorder");

        if (mgr.isPresentationObjectInstalled("orderw/CustomerOrder.page"))
        {
            if (!actionsBuffer.itemExists("okHeadCustOrder"))
                headbar.removeCustomCommand("custOrder");
        }

        if (!actionsBuffer.itemExists("okHeadAddCoToWOr"))
            headbar.removeCustomCommand("addCoToWOr");

        if (!actionsBuffer.itemExists("okHeadChangeCustomer"))
            headbar.removeCustomCommand("changeCustomer");
    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWACTIVEWORKORDER3TITLE: Transfer to Customer Order";
    }

    protected String getTitle()
    {
        return "PCMWACTIVEWORKORDER3TITLE: Transfer to Customer Order";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.show());

        if (bOpenNewWindow)
        {
            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString)); // XSS_Safe ILSOLK 20070709
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=600,height=400\");");      
        }
    }
}
