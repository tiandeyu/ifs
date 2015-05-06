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
*  File        : WorkOrderPermit.java 
*  ASP2JAVA Tool  2001-03-21  - Created Using the ASP file WorkOrderPermit.page
*  Modified    :  2001-03-21  BUNI Corrected some conversion errors.
*              :  2001-03-23  BUNI Modified Create Permit... and Replace Permit...
*                                  Action functions so as to open those forms in a new window. 
*                 2001-09-04  ARWI Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction(). 
*				   (Caused when cancelNew is performed after saveNew)  
*              :  2001-10-01  BUNI Added security check for Actions.
*              :  2002-12-04  GACO Set Max Length of MCH_CODE to 100
*              :  2003-11-07  JEWILK Modified display format of WO_NO. Call 110352.    
*              :  2004-01-05  SAPRLK Web Alignment - Removed methods clone() and doReset(), simplify code for RMBs, Links to multirow RMBs.
*              :  2004-01-28  VAGULK Web Alignment - Arranged field order as in Centura application
*              :  2004-02-10  ARWILK Edge Developments - (Enable Multirow RMB actions, Remove uneccessary global variables, Enhance Performance, Clean Javascript Code)
* --------------------------------- Edge - SP1 Merge -------------------------
*              :  2004-01-16  SHAFLK Bug Id 41263,Changed Lov for Permit.
*              :  2004-03-24  ThWilk Merge with SP1.
*              :  2004-04-21  ARWILK (Call ID#112761) Added valid conditions for RMB's.
*              :  2005-08-05  NEKOLK AMUT 115: Added extra flds to itemblk.
*              :  2005-08-24  NEKOLK Amut 115: Removed New__ from itebblk.
*              :  2005-09-07  NIJALK Bug 126388: Modified printContents(),run() & okFind().
*              :  2005-09-20  NEKOLK Bug 127052: added permit status to ITEM Block.
*              :  2006-01-26  THWILK Corrected localization errors.
*              :  2006-08-14  AMDILK Bug 58216, Eliminated SQL errors in web applications Modified methods
*                                    okFind(), okFindITEM(), countFindITEM()
*              :  2006-09-04  AMDILK Merged with the Bug Id 58216
*              :  2006-09-20  DiAmlk Bug ID:139589, Modified the method run.
*              ;  2007-05-18  AMDILK Call Id 144561: Inserted a new column "Site"
*              :  2007-06-05  CHANLK Call 144725 Remove prepare from multirow action.
*  	       :  2007-07-27  AMNILK Eliminated XXS Security Vulnerabilities.
*              :  2007-08-03  AMDILK Refreshed the window after replacing a permit
*              :  2007-07-18  NIJALK Bug 65987, Modified function call in PERMITSTATUS, DELIMITATION_ORDER_STATUS.
*              :  2007-08-08  AMDILK Merged bug 65987
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WorkOrderPermit extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderPermit");

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

    private ASPBlock itemblk;
    private ASPRowSet itemset;
    private ASPCommandBar itembar;
    private ASPTable itemtbl;
    private ASPBlockLayout itemlay;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private ASPCommand cmd;
    private ASPQuery q;
    private boolean actEna1;
    private boolean actEna2;
    private boolean actEna3;
    private boolean actEna4;
    private boolean again;
    private boolean bOpenNewWindow;
    private boolean comBarAct;
    private String sWoNo;
    private String urlString;
    private String newWinHandle; 
    private String qrystr;

    //===============================================================
    // Construction 
    //===============================================================
    public WorkOrderPermit(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();

        trans = mgr.newASPTransactionBuffer();
        ctx = mgr.getASPContext();
        fmt = mgr.newASPHTMLFormatter();

        sWoNo = ctx.readValue("CTXWONO", "");
        comBarAct = ctx.readFlag("COMBARACT", false);
        actEna1 = ctx.readFlag("ACTENA1", false);
        actEna2 = ctx.readFlag("ACTENA2", false);
        actEna3 = ctx.readFlag("ACTENA3", false);
        actEna4 = ctx.readFlag("ACTENA4", false);
        again = ctx.readFlag("AGAIN", false);
        qrystr = ctx.readValue("QRYSTR","");

        if (mgr.commandBarActivated())
        {
            eval(mgr.commandBarFunction());
            comBarAct = true; 
        } else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (mgr.dataTransfered())
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();   
            okFindITEM();
        } else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            sWoNo = mgr.readValue("WO_NO");
            okFind();
            okFindITEM();
        } else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();
        else if (mgr.isEmpty(mgr.getQueryStringValue("REFRESHFLAG"))) 
	   refreshPage();

        checkObjAvaileble();

        adjust();

        ctx.writeValue("CTXWONO", sWoNo);
        ctx.writeFlag("ACTENA1", actEna1);
        ctx.writeFlag("ACTENA2", actEna2);
        ctx.writeFlag("ACTENA3", actEna3);
        ctx.writeFlag("ACTENA4", actEna4);
        ctx.writeFlag("AGAIN", again);
        ctx.writeValue("QRYSTR",qrystr);
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();

        String val = mgr.readValue("VALIDATE");
        String txt = "";

        if ("PERMIT_SEQ".equals(val))
        {
            cmd = trans.addCustomFunction("PERMITID", "PERMIT_API.Get_Permit_Type_Id", "PERMITTYPEID");
            cmd.addParameter("PERMIT_SEQ");

            cmd = trans.addCustomFunction("PERMITDESC", "PERMIT_API.Get_Description", "PERMITDESCRIPTION");
            cmd.addParameter("PERMIT_SEQ");

            trans = mgr.validate(trans);

            String permitType = trans.getValue("PERMITID/DATA/PERMITTYPEID");
            String permitDesc = trans.getValue("PERMITDESC/DATA/PERMITDESCRIPTION");

            txt = (mgr.isEmpty(permitType) ? "": (permitType))+ "^" + (mgr.isEmpty(permitDesc) ? "": (permitDesc))+ "^"; 
            mgr.responseWrite(txt);
        }

        mgr.endResponse();
    }

    //-----------------------------------------------------------------------------
    //-------------------------  CMDBAR FUNCTIONS  --------------------------------
    //-----------------------------------------------------------------------------

    public void okFind()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        q = trans.addQuery(headblk);

        if (!mgr.isEmpty(sWoNo))
        {
            q.addWhereCondition("WO_NO = ?");
            q.addParameter("WO_NO", sWoNo);
        }

        if (mgr.dataTransfered())
            q.addOrCondition(mgr.getTransferedData());

        q.includeMeta("ALL");
        mgr.submit(trans);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWWORKORDERPERMITNODATA: No data found."));
            headset.clear();
        }
        qrystr = mgr.createSearchURL(headblk);
    }

    public void okFindITEM()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        int headrowno = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk);
        q.addWhereCondition("WO_NO = ?");
        q.addParameter("WO_NO", headset.getRow().getValue("WO_NO"));
        q.includeMeta("ALL");
        mgr.submit(trans);

        headset.goTo(headrowno);

        if (comBarAct)
        {
            if (itemset.countRows() == 0 && "ITEM.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWWORKORDERPERMITNODATA: No data found."));
                itemset.clear();
            }
        }
    }

    public void countFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);

        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();
    }

    public void countFindITEM()
    {
        ASPManager mgr = getASPManager();

        int headrowno = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("ITEM_WO_NO = ?");
        q.addParameter("ITEM_WO_NO", headset.getRow().getValue("WO_NO"));
        mgr.submit(trans);

        itemlay.setCountValue(toInt(itemset.getRow().getValue("N")));
        itemset.clear();
        itemset.goTo(headrowno);
    }

    public void newRowITEM()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addEmptyCommand("ITEM", "WORK_ORDER_PERMIT_API.New__", itemblk);
        cmd.setParameter("ITEM_WO_NO", headset.getRow().getValue("WO_NO"));
        cmd.setOption("ACTION", "PREPARE");
        trans = mgr.perform(trans);

        ASPBuffer data = trans.getBuffer("ITEM/DATA");
        itemset.addRow(data);
    }

//-----------------------------------------------------------------------------
//------------------------  ITEMBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void none()
    {
        ASPManager mgr = getASPManager();

        mgr.showAlert(mgr.translate("PCMWWORKORDERPERMITNONE: No RMB method has been selected."));
    }

    public void attributes()
    {
        ASPManager mgr = getASPManager();

        String calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);

        String primary_key = headset.getRow().getValue("WO_NO");
        ctx.setGlobal("FORM_NAME", "WorkOrderPermit.page");
        ctx.setGlobal("PRIMARY_KEY", primary_key); 

        // 040210  ARWILK  Begin  (Enable Multirow RMB actions)
        int count = 0;
        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;

        bOpenNewWindow = true;

        if (itemlay.isMultirowLayout())
        {
            itemset.storeSelections();
            itemset.setFilterOn();
            count = itemset.countSelectedRows();
        } else
        {
            itemset.unselectRows();
            count = 1;
        }

        transferBuffer = mgr.newASPBuffer();

        for (int i = 0; i < count; i++)
        {
            rowBuff = mgr.newASPBuffer();

            if (i == 0)
                rowBuff.addItem("PERMIT_TYPE_ID", itemset.getValue("PERMITTYPEID"));
            else
                rowBuff.addItem(null, itemset.getValue("PERMITTYPEID"));

            transferBuffer.addBuffer("DATA", rowBuff);

            if (itemlay.isMultirowLayout())
                itemset.next();
        }

        if (itemlay.isMultirowLayout())
            itemset.setFilterOff();

        urlString = createTransferUrl("PermitTypeRMB.page", transferBuffer);

        newWinHandle = "attributes"; 
        // 040210  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void preparePermit()
    {
        ASPManager mgr = getASPManager();

        String calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);

        // 040210  ARWILK  Begin  (Enable Multirow RMB actions)
        if (itemlay.isMultirowLayout())
            itemset.store();
        else
        {
            itemset.unselectRows();
            itemset.selectRow();
        }

        bOpenNewWindow = true;
        urlString = createTransferUrl("Permit.page", itemset.getSelectedRows("PERMIT_SEQ"));
        newWinHandle = "preparePermit"; 
        // 040210  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void createPermit()
    {
        ASPManager mgr = getASPManager();

        String work_ord_no = headset.getRow().getValue("WO_NO");

        //Web Alignment - simplify code for RMBs
        bOpenNewWindow = true;
        urlString = "CreatePermitDlg.page?WO_NO=" + mgr.URLEncode(work_ord_no);
        newWinHandle = "replace_permit";
        //
    }


    public void connectPermit()
    {
        ASPManager mgr = getASPManager();

        bOpenNewWindow = true;

        urlString = "ConnPermitDlg.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO"))+
                    "&QRYSTR="+mgr.URLEncode(qrystr);
        newWinHandle = "createPermitTemp"; 
    }


    public void replacePermit()
    {
        ASPManager mgr = getASPManager();

        String work_ord_no = headset.getRow().getValue("WO_NO");
        ctx.setGlobal("PRIMARY_KEY", work_ord_no); 

        if (itemlay.isMultirowLayout())
            itemset.goTo(itemset.getRowSelected());
        else
            itemset.selectRow();

        //Web Alignment - simplify code for RMBs
        bOpenNewWindow = true;
        urlString = "ReplacePermitDlg.page?PERMIT_SEQ=" + mgr.URLEncode(itemset.getRow().getValue("PERMIT_SEQ")) + "&WO_NO=" + mgr.URLEncode(work_ord_no);
        newWinHandle = "replace_permit";
        //
    }

    private void refreshPage()
     {
       ASPManager mgr = this.getASPManager();
       
       if ("TRUE".equals(mgr.readValue("REFRESHFLAG")))
       {
          if ("HEAD".equals(mgr.readValue("REFRESHBLOCK")))
             okFindITEM();
     
       }
    }   

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

    public void preDefine()
    {
        ASPManager mgr = getASPManager();


        headblk = mgr.newASPBlock("HEAD");

        headblk.addField("OBJID").
        setHidden();

        headblk.addField("OBJVERSION").
        setHidden();

        headblk.addField("WO_NO", "Number", "#").
        setSize(13).
        setLabel("PCMWWORKORDERPERMITHEADWONO: WO No").
        setReadOnly().
        setMaxLength(8);

        headblk.addField("CONTRACT").
        setSize(5).
        setLabel("PCMWWORKORDERPERMITCONTRACT: Site").
        setUpperCase().
        setReadOnly();

        headblk.addField("STATE").
        setSize(11).
        setLabel("PCMWWORKORDERPERMITSTATE: Status").
        setReadOnly().
        setMaxLength(30);

        headblk.addField("MCH_CODE").
        setSize(13).
        setMaxLength(100).
        setLabel("PCMWWORKORDERPERMITMCH_CODE: Object ID").
        setUpperCase().
        setReadOnly();

        headblk.addField("DESCRIPTION").
        setSize(28).
        setLabel("PCMWWORKORDERPERMITDESCRIPTION: Description").
        setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:CONTRACT,:MCH_CODE)").
        setReadOnly().
        setMaxLength(2000);

        headblk.setView("ACTIVE_SEPARATE");
        headblk.defineCommand("ACTIVE_SEPARATE_API", "");

        headset = headblk.getASPRowSet();

        headtbl = mgr.newASPTable(headblk);
        headtbl.setWrap();

        headbar = mgr.newASPCommandBar(headblk);

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        headlay.setDialogColumns(2);

        //------------------------------------------------------------------------------------------------------------
        //--------------------------------------     ITEM Block     --------------------------------------------------
        //------------------------------------------------------------------------------------------------------------

        itemblk = mgr.newASPBlock("ITEM");

        itemblk.addField("ITEM_OBJID").
        setDbName("OBJID").
        setHidden();

        itemblk.addField("ITEM_OBJVERSION").
        setDbName("OBJVERSION").
        setHidden();

        itemblk.addField("ITEM_WO_NO", "Number", "#").
        setSize(8).
        setDbName("WO_NO").
        setMandatory().
        setLabel("PCMWWORKORDERPERMITWONO: WO Number").
        setHidden().
        setReadOnly();

        itemblk.addField("PERMIT_SEQ", "Number", "#").
        setSize(20).
        setDynamicLOV("PERMIT_LOV",600,450).
        setCustomValidation("PERMIT_SEQ","PERMITTYPEID,PERMITDESCRIPTION").
        setLabel("PCMWWORKORDERPERMITPERMITSEQ: Permit").
        setReadOnly().
        setInsertable().
        setMaxLength(6);

        itemblk.addField("PERMITTYPEID").
        setSize(20).
        setLabel("PCMWWORKORDERPERMITPERMITTYPEID: Type").
        setFunction("PERMIT_API.Get_Permit_Type_Id(:PERMIT_SEQ)").
        setReadOnly().
        setMaxLength(2000);

        itemblk.addField("PERMITDESCRIPTION").
        setSize(48).
        setLabel("PCMWWORKORDERPERMITPERMITDESCRIPTION: Description").
        setFunction("PERMIT_API.Get_Description(:PERMIT_SEQ)").
        setReadOnly().
        setMaxLength(2000);


        itemblk.addField("PERMITSTATUS").
        setSize(20).
        setLabel("PCMWWORKORDERPERMITPERMITSTATUS: Permit Status").
        setFunction("PERMIT_API.Get_State(:PERMIT_SEQ)").
        setReadOnly().
        setMaxLength(2000);

        itemblk.addField("ITEM_CONTRACT").
        setSize(10).
        setLabel("PCMWPERMITCONTRACT: Site").
        setFunction("PERMIT_API.Get_Contract(:PERMIT_SEQ)").
	setReadOnly();

        itemblk.addField("VALID_FROM_DATE","Datetime").
        setSize(25).
        setFunction("PERMIT_API.Get_Valid_From_Date(:PERMIT_SEQ)").
        setReadOnly().
        setLabel("PCMWPERMITFROMDATE: Valid From Date");

        itemblk.addField("VALID_TO_DATE","Datetime").
        setSize(25).
        setReadOnly().
        setFunction("PERMIT_API.Get_Valid_To_Date(:PERMIT_SEQ)").
        setLabel("PCMWPERMITTODATE: Valid To Date");

        itemblk.addField("DELIMITATION_ORDER_NO", "Number", "#").
        setSize(20).
        setLabel("DEMITATATIONORDERNO: Isolation Order NO").
        setReadOnly();

        itemblk.addField("ISOLATION_TYPE").
        setSize(35).
        setFunction("ISOLATION_ORDER_PERMIT_API.Get_Isolation_Type(:PERMIT_SEQ,DELIMITATION_ORDER_NO)").
        setLabel("WORKORDERPERMITISOLATIONTYPE: Isolation Type").
        setReadOnly();

        itemblk.addField("DELIMITATION_ORDER_DES").
        setSize(35).
        setFunction("DELIMITATION_ORDER_API.Get_Description(:DELIMITATION_ORDER_NO)").
        setLabel("WORKORDERPERMITISOLATIONORDERDESC: Isolation Order Description").
        setReadOnly();


        itemblk.addField("DELIMITATION_ORDER_STATUS").
        setSize(35).
        setFunction("DELIMITATION_ORDER_API.Get_Decoded_State(:DELIMITATION_ORDER_NO)").
        setLabel("WORKORDERPERMITISOLATIONORDERSTATUS: Isolation Order Status").
        setReadOnly();


        itemblk.addField("PLAN_START_ESTABLISHMENT","Datetime").
        setSize(28).
        setFunction("DELIMITATION_ORDER_API.Get_Plan_Start_Establishment(:DELIMITATION_ORDER_NO)").
        setLabel("PCMWDELIMITATIONORDERTABPLANSTRTEST: Planned Start Establishment");

        itemblk.addField("PLAN_FINISH_ESTABLISHMENT","Datetime").
        setSize(28).
        setFunction("DELIMITATION_ORDER_API.Get_Plan_Finish_Establishment(:DELIMITATION_ORDER_NO)").
        setLabel("PCMWDELIMITATIONORDERTABPLANFINEST: Planned Completion Establishment");

        itemblk.addField("PLAN_START_REESTABLISHMENT","Datetime").
        setSize(28).
        setFunction("DELIMITATION_ORDER_API.Get_Plan_Start_Reestablishment(:DELIMITATION_ORDER_NO)").
        setLabel("PCMWDELIMITATIONORDERTABPLANSTARTREEST: Planned Start Reestablishment");

        itemblk.addField("PLAN_FINISH_REESTABLISHMENT","Datetime").
        setSize(28).
        setFunction("DELIMITATION_ORDER_API.Get_Plan_Finish_Reest(:DELIMITATION_ORDER_NO)").
        setLabel("PCMWDELIMITATIONORDERTABPLANFINREEST: Planned Completion Reestablishment");


        itemblk. setView("WORK_ORDER_PERMIT");
        itemblk.defineCommand("WORK_ORDER_PERMIT_API", "Remove__");
        itemblk.setMasterBlock(headblk);

        itemset = itemblk.getASPRowSet();

        itembar = mgr.newASPCommandBar(itemblk);
        itembar.defineCommand(itembar.NEWROW, "newRowITEM");

        itembar.defineCommand(itembar.COUNTFIND, "countFindITEM");
        itembar.defineCommand(itembar.OKFIND, "okFindITEM");

        itembar.addCustomCommand("createPermit", mgr.translate("PCMWWORKORDERPERMITCREPERM: Create Permit..."));
        itembar.addCustomCommand("connectPermit", mgr.translate("PCMWWORKORDERPERMITCONN: Connect Permit..."));

        itembar.addCustomCommand("attributes", mgr.translate("PCMWWORKORDERPERMITATTRI: Attributes..."));
        itembar.addCustomCommand("preparePermit", mgr.translate("PCMWWORKORDERPERMITPREPERM: Prepare Permit..."));
        itembar.addCustomCommand("replacePermit", mgr.translate("PCMWWORKORDERPERMITREPPERM: Replace Permit..."));

        //Links with multirow RMBs
        itembar.enableMultirowAction();
        itembar.forceEnableMultiActionCommand("createPermit");
        itembar.forceEnableMultiActionCommand("connectPermit");

        itembar.addCommandValidConditions("attributes","PERMIT_SEQ","Disable","null");
        itembar.addCommandValidConditions("preparePermit","PERMIT_SEQ","Disable","null");
        itembar.removeFromMultirowAction("replacePermit");
        itembar.removeFromMultirowAction("preparePermit");
        //

        itemtbl = mgr.newASPTable(itemblk);
        itemtbl.setWrap();
        // 040210  ARWILK  Begin  (Enable Multirow RMB actions)
        //itemtbl.enableRowSelect();
        // 040210  ARWILK  End  (Enable Multirow RMB actions)

        itemlay = itemblk.getASPBlockLayout();
        itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);
        itemlay.setDialogColumns(2);
    }

    // 040210  ARWILK  Begin  (Enable Multirow RMB actions)
    private String createTransferUrl(String url, ASPBuffer object)
    {
        ASPManager mgr = getASPManager();

        try
        {
            String pkg = mgr.pack(object,1900 - url.length());
            char sep = url.indexOf('?')>0 ? '&' : '?';
            urlString = url + sep + "__TRANSFER=" + pkg ;
            return urlString;
        } catch (Throwable any)
        {
            return null;
        }
    }
    // 040210  ARWILK  End  (Enable Multirow RMB actions)

    public void checkObjAvaileble()
    {
        if (!again)
        {
            ASPManager mgr = getASPManager();

            ASPBuffer availObj;
            trans.clear();
            trans.addSecurityQuery("PERMIT_TYPE,PERMIT_TYPE_ATTRIBUTE,PERMIT,MODULE");
            trans.addPresentationObjectQuery("PCMW/CreatePermitDlg.page,PCMW/PermitTypeRMB.page,PCMW/Permit.page,PCMW/ReplacePermitDlg.page");

            trans = mgr.perform(trans);

            availObj = trans.getSecurityInfo();

            if (availObj.itemExists("PERMIT_TYPE") && availObj.namedItemExists("PCMW/CreatePermitDlg.page"))
                actEna1 = true;
            if (availObj.itemExists("PERMIT_TYPE_ATTRIBUTE") && availObj.namedItemExists("PCMW/PermitTypeRMB.page"))
                actEna2 = true;
            if (availObj.itemExists("PERMIT") && availObj.namedItemExists("PCMW/Permit.page"))
                actEna3 = true;
            if (availObj.itemExists("MODULE") && availObj.namedItemExists("PCMW/ReplacePermitDlg.page"))
                actEna4 = true;

            again = true;
        }

        if (!actEna1)
            itembar.removeCustomCommand("createPermit");
        if (!actEna2)
            itembar.removeCustomCommand("attributes");
        if (!actEna3)
            itembar.removeCustomCommand("preparePermit");
        if (!actEna4)
            itembar.removeCustomCommand("replacePermit");
    }

    public void adjust()
    {
        if (itemset.countRows()==0)
        {
            itemtbl.disableRowSelect();
            itembar.removeCustomCommand("attributes");
        } else
        {
            itemtbl.enableRowSelect();
        }
    }
//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWWORKORDERPERMITTITLE: Permit";
    }

    protected String getTitle()
    {
        return "PCMWWORKORDERPERMITTITLE: Permit";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        printHiddenField("REFRESHFLAG", "");
        printHiddenField("REFRESHBLOCK", "");

        appendToHTML(headlay.show());

        if (itemlay.isVisible())
            appendToHTML(itemlay.show());

        appendDirtyJavaScript("window.name =\"WorkOrderPermit\";");

        if (bOpenNewWindow)
        {
            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));	//XSS_Safe AMNILK 20070726
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
        }

        appendDirtyJavaScript("function refreshPage()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   f.REFRESHFLAG.value = 'TRUE';\n");
        appendDirtyJavaScript("   f.REFRESHBLOCK.value = 'HEAD';\n");
        appendDirtyJavaScript("   f.submit();\n");
        appendDirtyJavaScript("}\n"); 
    }
}
