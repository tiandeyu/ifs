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
*  File        : FrameSetWorkOrderCost.java 
*  Created     : ARWILK  010307
*  Modified    :
*  INROLK  021202  Increased the height of Header Part.  
*  ARWILK  031223  Edge Developments - (Removed clone and doReset Methods)
*  NAMELK  041105  Non Standard Translation Tags Corrected.
*  SHAFLK  060109  Bug 55490, Modified whole page.
*  SULILK  060207  Merged bug 55490.
*  NIJALK  060302  Call 131739: Modified Userwhere for okFind(),countFind().
* ----------------------------------------------------------------------------
*  ChAmlk  061016  Bug 61103, Modified search1() to remove web security issues.
*  ILSOLK  070227  Merged Bug ID 61103.
*  ASSALK  070606  Call ID: 145461. Modified search1(), okFind(). Changed default buffer size.
*  ILSOLK  070730  Eliminated LocalizationErrors.
*  NIJALK  100218  Bug 87766, Modified the label of field CBHASCONNECTIONS.
* -----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class FrameSetWorkOrderCost extends ASPPageProvider {
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.FrameSetWorkOrderCost");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPForm frm;

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPContext ctx;
    private ASPHTMLFormatter fmt;
    private ASPLog log;
    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPCommandBar headbar;
    private ASPTable headtbl;
    private ASPBlockLayout headlay;
    private ASPField f;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private String whereStr;
    private String searchBaseName;
    private ASPTransactionBuffer trans;
    private int sWONoPass;
    private String urlLink1;
    private String urlLink2;
    private String searchUrl;
    private String current_url; 
    private String sWoNo;

    //===============================================================
    // Construction 
    //===============================================================

    public FrameSetWorkOrderCost(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        whereStr =  searchBaseName ;
        searchBaseName =  "";
        ASPManager mgr = getASPManager();

        trans = mgr.newASPTransactionBuffer();
        frm = mgr.getASPForm();

        ctx = mgr.getASPContext();
        fmt = getASPHTMLFormatter();
        current_url = mgr.getURL();

        ctx.setGlobal("CALLING_FORM",current_url);

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (mgr.dataTransfered())
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("CONNECTED_WO_NO"))) {
            sWoNo = mgr.readValue("WO_NO","");
            search1();          
        } else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();

        adjust(); 

    }
//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void  validate()
    {
        ASPManager mgr = getASPManager();

        String val;

        val = mgr.readValue("VALIDATE");
        mgr.showError("VALIDATE not implemented");
        mgr.endResponse();
    }

//=============================================================================
//  Command Bar Edit Group functions
//=============================================================================

    public void  okFind()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        ASPQuery q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CONTRACT) FROM DUAL) AND (MCH_CODE_CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(MCH_CODE_CONTRACT) FROM DUAL) OR MCH_CODE_CONTRACT IS NULL)");
        q.setOrderByClause("WO_NO");

        if (mgr.dataTransfered())
            q.addOrCondition(mgr.getTransferedData());
        //q.setBufferSize(10);
        q.includeMeta("ALL");

        mgr.querySubmit(trans,headblk);

        if (headset.countRows() == 0) {
            mgr.showAlert(mgr.translate("PCMWWORKORDERNODATAFOUND: No data found."));
            headset.clear();
        }

        if (headset.countRows() == 1)
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);

        mgr.createSearchURL(headblk);
    }


    public void  search1()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        ASPQuery q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CONTRACT) FROM DUAL)");
        q.addWhereCondition("WO_NO= ?");
        q.addParameter("WO_NO",sWoNo);
        q.setOrderByClause("WO_NO");
        //q.setBufferSize(10);
        q.includeMeta("ALL");

        mgr.querySubmit(trans,headblk);

        if (headset.countRows() == 0) {
            mgr.showAlert(mgr.translate("PCMWWORKORDERNODATAFOUND: No data found."));
            headset.clear();
        }
        mgr.createSearchURL(headblk);

    }

    public void  countFind()
    {
        ASPManager mgr = getASPManager();

        ASPQuery q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CONTRACT) FROM DUAL) AND (MCH_CODE_CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(MCH_CODE_CONTRACT) FROM DUAL) OR MCH_CODE_CONTRACT IS NULL)");
        q.setSelectList("to_char(count(*)) N");

        mgr.submit(trans);   

        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();   
    }


    public void  newRow()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd = trans.addEmptyCommand("HEAD","WORK_ORDER_API.New__",headblk);
        cmd.setOption("ACTION","PREPARE");

        trans = mgr.perform(trans);

        ASPBuffer data = trans.getBuffer("HEAD/DATA");
        headset.addRow(data);
    }


    public void  preDefine()
    {
        ASPManager mgr = getASPManager();


        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////  HEADBLK  ///////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("WO_NO","Number","#");
        f.setSize(13);
        f.setMaxLength(96);
        f.setLabel("PCMWWORKORDERWONO: WO No");
        f.setHyperlink("../pcmw/SeparateWorkOrder.page","WO_NO","NEWWIN");
        f.setReadOnly();
        f.setHilite();

        f = headblk.addField("CONTRACT");
        f.setSize(6);
        f.setMaxLength(5);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERSITTT: List Of Site"));
        f.setLabel("PCMWWORKORDERCONTRACT: WO Site");
        f.setUpperCase();
        f.setReadOnly();
        f.setHilite();

        f = headblk.addField("CONNECTION_TYPE");
        f.setSize(15);
        f.setLabel("PCMWWORKORDERCONNTYPE: ConnectionType");   
        f.setUpperCase();
        f.setMaxLength(15);
        f.setReadOnly();
        f.setHilite();

        f = headblk.addField("MCH_CODE");
        f.setSize(15);
        f.setMaxLength(100);
        f.setDynamicLOV("MAINTENANCE_OBJECT","MCH_CODE_CONTRACT CONTRACT",600,445);
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERLISTOBJ1: List Of Object ID"));
        f.setLabel("PCMWWORKORDERMCHCODE: Object ID");
        f.setUpperCase();
        f.setReadOnly();
        f.setHilite();

        f = headblk.addField("MCH_CODE_CONTRACT");
        f.setSize(15);
        f.setMaxLength(40);
        f.setLabel("PCMWWORKORDERMCHCODECONT: Site");
        f.setUpperCase();
        f.setReadOnly();
        f.setHilite();
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERSITTT: List Of Site"));
        mgr.getASPField("MCH_CODE").setHyperlink("../equipw/EquipmentAllObjectLightRedirect.page","MCH_CODE,MCH_CODE_CONTRACT,WO_NO","NEWWIN");

        f = headblk.addField("MCH_CODE_DESCRIPTION");
        f.setSize(22);
        f.setMaxLength(5);
        f.setLabel("PCMWWORKORDERDESCRIPTION: Description");
        f.setReadOnly();
        f.setHilite();
        f.setDefaultNotVisible();
        f = headblk.addField("WO_STATUS_ID");
        f.setSize(13);
        f.setMaxLength(96);
        f.setReadOnly(); 
        f.setLabel("PCMWWORKORDERWOSTATUSID: Status");
        f.setFunction("WORK_ORDER_API.GET_WO_STATUS_ID_CL(:WO_NO)");
        f.setHilite();

        f = headblk.addField("ERR_DESCR");
        f.setSize(55);
        f.setLabel("PCMWWORKORDERDIRECT: Directive");
        f.setHilite();

        f = headblk.addField("ORG_CODE");
        f.setSize(13);
        f.setMaxLength(2000);
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV",600,445);
        f.setLOVProperty("TITLE",mgr.translate("PCMWFRAMESETWORKORDERCOSTEXEDE: List Of Executing Dept"));
        f.setMandatory();
        f.setLabel("PCMWFRAMESETWORKORDERORGCODE: Executing Dept");
        f.setUpperCase();
        f.setReadOnly();
        f.setHilite();
        f.setDefaultNotVisible();

        f = headblk.addField("WORK_TYPE_ID");
        f.setSize(8);
        f.setMaxLength(20);
        f.setDynamicLOV("WORK_TYPE",600,445);
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERWORKOR: List Of Work Type"));
        f.setLabel("PCMWWORKORDERWORKTYPEID: Work Type");
        f.setUpperCase();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("CUSTOMER_ID");
        f.setHidden();
        f.setFunction("CUSTOMER_NO");

        f = headblk.addField("CUSTOMER_NO");
        f.setSize(8);
        f.setMaxLength(20);
        f.setDynamicLOV("CUSTOMER_INFO",600,445);
        f.setLabel("PCMWWORKORDERCUSTOMER_NO: Customer");
        f.setUpperCase();
        f.setReadOnly();

        f = headblk.addField("CUSTOMERNAME");
        f.setSize(20);
        f.setLabel("PCMWWORKORDERCUSTOMERNAME: Name");
        f.setFunction("CUSTOMER_INFO_API.Get_Name(:CUSTOMER_NO)");
        f.setReadOnly();

        f = headblk.addField("AGREEMENT_ID");
        f.setSize(10);
        f.setMaxLength(10);
        f.setDynamicLOV("CUSTOMER_AGREEMENT",600,445);
        f.setLabel("PCMWWORKORDERAGREEMENTID: Agreement ID");
        f.setReadOnly();

        f = headblk.addField("AUTHORIZE_CODE");
        f.setSize(10);
        f.setMaxLength(20);
        f.setLabel("PCMWWORKORDERAUTHORIZECODE: Authorize Code");
        f.setReadOnly();

	//Bug 87766, Start, Modified the label
        f = headblk.addField("CBHASCONNECTIONS");
        f.setSize(13);
        f.setLabel("PCMWWORKORDERCBHASCONNECTIONS: Has Structure");
        f.setFunction("substr(WORK_ORDER_CONNECTION_API.HAS_CONNECTION_DOWN(WO_NO),1,5)");
        f.setCheckBox("FALSE,TRUE");
        f.setDefaultNotVisible();
	//Bug 87766, End

        headblk.setView("WORK_ORDER");
        headblk.defineCommand("WORK_ORDER_API","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWWORKORDERHD: Work Order Cost Analysis Service Management"));
        headtbl.setWrap();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.disableCommand(headbar.NEWROW);
        headbar.disableCommand(headbar.EDITROW);
        headbar.disableCommand(headbar.DELETE);
        headbar.disableCommand(headbar.DUPLICATEROW);

        headbar.setBorderLines(false,true);

        headlay = headblk.getASPBlockLayout();   
        headlay.defineGroup("","WO_NO,CONTRACT,WO_STATUS_ID,ERR_DESCR,ORG_CODE,WORK_TYPE_ID,CUSTOMER_NO,CUSTOMERNAME,AGREEMENT_ID,AUTHORIZE_CODE,CBHASCONNECTIONS",false,true);
        headlay.defineGroup(mgr.translate("PCMWWORKORDERGRPLABELOBJ: Object"),"CONNECTION_TYPE,MCH_CODE,MCH_CODE_DESCRIPTION,MCH_CODE_CONTRACT",true,true);
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        headlay.setSimple("CUSTOMERNAME");  
        headlay.setSimple("MCH_CODE_DESCRIPTION");  
        headlay.setDialogColumns(3);
    }


    public void  adjust()
    {
        ASPManager mgr = getASPManager();

        mgr.getASPField("CUSTOMER_NO").setHidden();
        mgr.getASPField("CUSTOMERNAME").setHidden();
        mgr.getASPField("AGREEMENT_ID").setHidden();
        mgr.getASPField("AUTHORIZE_CODE").setHidden();

        sWONoPass = 0;

        if ((( headset.countRows()>0 )&& ( headlay.isSingleLayout() ))|| (( headset.countRows() == 1 ) &&  !( headlay.isFindLayout() ))) {
            sWONoPass = toInt(headset.getRow().getNumberValue("WO_NO"));

            urlLink1 = "WorkOrderCostNavigator.page?CONNECTED_WO_NO="+mgr.URLEncode(Integer.toString(sWONoPass))+"&FORM=2";      
            urlLink2 = "WorkOrderCostTable.page?CONNECTED_WO_NO="+mgr.URLEncode(Integer.toString(sWONoPass));        

        } else if (( headlay.isFindLayout() )|| (( headlay.isMultirowLayout() )&& ( headset.countRows() != 1 ))|| ( headset.countRows() == 0 )) {
            urlLink1 = "WorkOrderCostNavigator.page";
            urlLink2 = "WorkOrderCostTable.page";
        }

        ctx.setGlobal("URLLINK1",urlLink1);
        ctx.setGlobal("URLLINK2",urlLink2);
    }

    protected String getDescription()
    {
        return "Work Order Cost Analysis";
    }

    protected String getTitle()
    {
        return "PCMWFRAMEWORKORDERCOSTWOCOSTANYL: Work Order Cost Analysis";
    }

    protected AutoString getContents() throws FndException
    { 
        AutoString out = getOutputStream();
        out.clear();
        ASPManager mgr = getASPManager();

        out.append("<html>\n");
        out.append("<head>");
        out.append(mgr.generateHeadTag("WOCOSANAL: Work Order Cost Analysis"));
        out.append("<title></title>\n");
        out.append("</head>\n");
        out.append("<body ");
        out.append(mgr.generateBodyTag());
        out.append(">\n");
        out.append("<form ");
        out.append(mgr.generateFormTag());
        out.append(">\n");
        out.append(mgr.startPresentation("WOCOSANAL: Work Order Cost Analysis"));
        if (headlay.isVisible()) {
            out.append(headlay.show());
        }
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

        if (headlay.isSingleLayout() && (headset.countRows()>0) )
        {
            out.append("<iframe name=\"__navigatorPart\" src=\"" + urlLink1 + "\" width = \"25%\" height = \"45%\" frameborder=\"no\">");
            out.append("</iframe>");
            out.append("<iframe name=\"__costPart\" src=\"" + urlLink2 + "\" width = \"75%\" height = \"45%\" frameborder=\"no\">");
            out.append("</iframe>");
        }

        out.append(mgr.endPresentation());
        out.append("</form>\n");
        out.append("</body>\n");
        out.append("</html>");

        return out;    
    } 

}