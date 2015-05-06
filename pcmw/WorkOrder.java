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
*  File        : WorkOrder.java 
*  Created     : ARWILK  010307  Created.
*  Modified    :
*  INROLK  021202  Added MCH_CODE_CONTRACT and CONNECTION_TYPE.  
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  THWILK  031017  Call ID 108246 - Added an extra parameter(#)to the deffinition of the field WO_NO.
*  CHCRLK  031027  Added translation constant for the Object group box. [Call ID 109409]
*  CHCRLK  031028  Modified JavaScript segments to comply with Web Client 3.6.0.
*  ARWILK  031223  Edge Developments - (Removed clone and doReset Methods)
*  ARWILK  040723  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning)
*  SHAFLK  050919  Bug 52880, Modified preDefine().
*  NIJALk  051004  Merged bug 52880.
*  NEKOLK  050528  Bug 54198, Modified userwhere.
*  THWILK  051103  Merged Bug 54198.
*  NIJALK  060125  Call 131739: Modified userwhere.
* ----------------------------------------------------------------------------
*  ChAmlk  061016  Bug 61103, Modified search1() to remove web security issues.
*  ILSOLK  070227  Merged Bug ID 61103.
*  BUNILK  070504  Implemented "MTIS907 New Service Contract - Services" changes.
*  AMNILK  070727  Eliminated SQLInjections Security Vulnerability.
*  ILSOLK  070731  Eliminated LocalizationErrors.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WorkOrder extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrder");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPForm frm;
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
    private String activetab;
    private String callingUrl;
    private String sTitleToShow;
    private String strShow;
    private int sWONoPass;
    private String sWoNo;
    private String sPassWoToTree; 
    private String urlLink1;
    private String urlLink2;

    //===============================================================
    // Construction 
    //===============================================================

    public WorkOrder(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        sPassWoToTree =  "";
        whereStr =  searchBaseName ;
        searchBaseName =  "";

        ASPManager mgr = getASPManager();

        trans = mgr.newASPTransactionBuffer();
        frm = mgr.getASPForm();
        ctx = mgr.getASPContext();
        fmt = getASPHTMLFormatter();

        activetab = ctx.readValue("ACTIVETAB","0");
        sPassWoToTree = ctx.readValue("SPAAWOTREE","false");
        callingUrl = ctx.getGlobal("CALLING_FORM");

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (mgr.dataTransfered())
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("CONNECTED_WO_NO")))
        {
            sWoNo = mgr.readValue("WO_NO","");
            search1();
        }

        adjust();

        ctx.writeValue("ACTIVETAB",activetab);
        ctx.writeValue("SPAAWOTREE",sPassWoToTree);

    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
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

    public void okFind()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        ASPQuery q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CONTRACT) FROM DUAL) AND (MCH_CODE_CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(MCH_CODE_CONTRACT) FROM DUAL) OR MCH_CODE_CONTRACT IS NULL)");
        q.setOrderByClause("WO_NO");

        if (mgr.dataTransfered())
            q.addOrCondition(mgr.getTransferedData());

        q.includeMeta("ALL");

        mgr.submit(trans);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWWORKORDERNODATAFOUND: No data found."));
            headset.clear();
        }

        if (headset.countRows() == 1)
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
    }


    public void search1()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        ASPQuery q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CONTRACT) FROM DUAL)");
        q.addWhereCondition("WO_NO= ?");
        q.addParameter("WO_NO",sWoNo);
        q.setOrderByClause("WO_NO");
        q.includeMeta("ALL");

        mgr.submit(trans);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWWORKORDERNODATAFOUND: No data found."));
            headset.clear();
        }
    }

    public void countFind()
    {
        ASPManager mgr = getASPManager();

        ASPQuery q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CONTRACT) FROM DUAL) AND MCH_CODE_CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(MCH_CODE_CONTRACT) FROM DUAL)");
        q.setSelectList("to_char(count(*)) N");

        mgr.submit(trans);   

        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();   
    }


    public void newRow()
    {
        ASPManager mgr = getASPManager();

        ASPCommand cmd = trans.addEmptyCommand("HEAD","WORK_ORDER_API.New__",headblk);
        cmd.setOption("ACTION","PREPARE");

        trans = mgr.perform(trans);

        ASPBuffer data = trans.getBuffer("HEAD/DATA");
        headset.addRow(data);
    }


    public void preDefine()
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
        //f.setHyperlink("../equipw/EquipmentAllObjectLightRedirect.page","MCH_CODE,MCH_CODE_CONTRACT","NEWWIN");
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
        //f.setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:CONTRACT,:MCH_CODE)");
        //mgr.getASPField("MCH_CODE").setValidation("DESCRIPTION");
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
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,445);
        f.setLOVProperty("TITLE",mgr.translate("PCMWWORKORDERMAINTORGLOVTITLE: List Of Maintenance Organization"));
        f.setMandatory();
        f.setLabel("PCMWWORKORDERORGCODE: Maitenance Organization");
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
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = headblk.addField("CONTRACT_ID");
        f.setDynamicLOV("PSC_CONTR_PRODUCT_LOV");
        f.setUpperCase();
        f.setMaxLength(15);
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERCONTRACTID: Contract ID");
        f.setSize(15);
        
        f = headblk.addField("LINE_NO","Number");
        f.setDynamicLOV("PSC_CONTR_PRODUCT_LOV");
        f.setReadOnly();
        f.setLabel("PCMWWORKORDERLINENO: Line No");
        f.setSize(10);

        f = headblk.addField("AGREEMENT_ID");
        f.setSize(10);
        f.setMaxLength(10);
        f.setLabel("PCMWWORKORDERAGREEMENTID: Agreement ID");
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = headblk.addField("AUTHORIZE_CODE");
        f.setSize(10);
        f.setMaxLength(20);
        f.setLabel("PCMWWORKORDERAUTHORIZECODE: Authorize Code");
        f.setReadOnly();

        f = headblk.addField("CBHASCONNECTIONS");
        f.setSize(13);
        f.setLabel("PCMWWORKORDERCBHASCONNECTIONS: Has Connections");
        f.setFunction("substr(WORK_ORDER_CONNECTION_API.HAS_CONNECTION_DOWN(WO_NO),1,5)");
        f.setCheckBox("FALSE,TRUE");
        f.setDefaultNotVisible();

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
        this.disableFooter();

        headlay = headblk.getASPBlockLayout();   
        headlay.defineGroup("","WO_NO,CONTRACT,WO_STATUS_ID,ERR_DESCR,ORG_CODE,WORK_TYPE_ID,CUSTOMER_NO,CUSTOMERNAME,AGREEMENT_ID,AUTHORIZE_CODE,CBHASCONNECTIONS",false,true);
        headlay.defineGroup(mgr.translate("PCMWWORKORDERGRPLABELOBJ: Object"),"CONNECTION_TYPE,MCH_CODE,MCH_CODE_DESCRIPTION,MCH_CODE_CONTRACT",true,true);
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        headlay.setSimple("CUSTOMERNAME");  
        headlay.setSimple("MCH_CODE_DESCRIPTION");  
        headlay.setDialogColumns(3);
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        ctx.setGlobal("CALLING_FORM",callingUrl);
        callingUrl = callingUrl.toUpperCase();                     

        if (callingUrl.indexOf("WORKORDERCOSTANALYSISSM") > 0)
        {
            sTitleToShow = mgr.translate("PCMWWORKORDERTITLE1: Work Order Cost/Revenue Analysis SM");   

            if (!headlay.isFindLayout())
                mgr.getASPField("AUTHORIZE_CODE").setHidden();

            searchBaseName = "WorkOrderCostAnalysisSM.page";

            if (mgr.isPresentationObjectInstalled("enterw/CustomerInfo.page"))
                mgr.getASPField("CUSTOMER_NO").setHyperlink("../enterw/CustomerInfo.page","CUSTOMER_ID","NEWWIN");
        }
        else if (callingUrl.indexOf("FRAMESETWORKORDERCOST") > 0)
        {
            sTitleToShow = mgr.translate("PCMWWORKORDERTITLE2: Work Order Cost Analysis");
            mgr.getASPField("CUSTOMER_NO").setHidden();
            mgr.getASPField("CUSTOMERNAME").setHidden();
            mgr.getASPField("AGREEMENT_ID").setHidden();
            mgr.getASPField("AUTHORIZE_CODE").setHidden();
            searchBaseName = "FrameSetWorkOrderCost.page";
        }

        /*try
        {
           strShow = headlay.show();
        }
        catch (Exception e)
        {
        }
  
        if (strShow.indexOf("help.page") > 0)
        {
           while (strShow.indexOf("WorkOrder.page") != -1)
              strShow =  mgr.replace(strShow,"WorkOrder.page\"",searchBaseName+"\" target=\"_parent\"");
        }*/

        sWONoPass = 0;

        if ((( headset.countRows()>0 )&& ( headlay.isSingleLayout() ))|| (( headset.countRows() == 1 ) &&  !( headlay.isFindLayout() )))
        {
            sWONoPass = toInt(headset.getRow().getNumberValue("WO_NO"));

            sPassWoToTree = "true";
            urlLink1 = "WorkOrderCostNavigator.page?CONNECTED_WO_NO="+mgr.URLEncode(Integer.toString(sWONoPass));

            if (callingUrl.indexOf("WORKORDERCOSTANALYSISSM") > 0)
            {
                urlLink1 = "WorkOrderCostNavigator.page?CONNECTED_WO_NO="+mgr.URLEncode(Integer.toString(sWONoPass))+"&FORM=1";         
                urlLink2 = "WorkOrderCostTableSM.page?CONNECTED_WO_NO="+mgr.URLEncode(Integer.toString(sWONoPass));
            }
            else if (callingUrl.indexOf("FRAMESETWORKORDERCOST") > 0)
            {
                urlLink1 = "WorkOrderCostNavigator.page?CONNECTED_WO_NO="+mgr.URLEncode(Integer.toString(sWONoPass))+"&FORM=2";      
                urlLink2 = "WorkOrderCostTable.page?CONNECTED_WO_NO="+mgr.URLEncode(Integer.toString(sWONoPass));        
            }

        }
        else if (( headlay.isFindLayout() )|| (( headlay.isMultirowLayout() )&& ( headset.countRows() != 1 ))|| ( headset.countRows() == 0 ))
        {
            sPassWoToTree = "true";
            urlLink1 = "WorkOrderCostNavigator.page";

            if (callingUrl.indexOf("WORKORDERCOSTANALYSISSM") > 0)
                urlLink2 = "WorkOrderCostTableSM.page";
            else if (callingUrl.indexOf("FRAMESETWORKORDERCOST") > 0)
                urlLink2 = "WorkOrderCostTable.page";
        }

    }

    private String modifiedStartPres(ASPManager mgr,String myStartPresentation)
    {
        int defInd = myStartPresentation.indexOf("Default.");
        if (defInd != -1)
        {
            int endInd = myStartPresentation.indexOf(">",defInd);
            String ext = myStartPresentation.substring(defInd+7,endInd);
            String oldStr = "Default"+ext;
            String newStr = "Default"+ext+" target=\"_parent\""; 
            myStartPresentation = mgr.replace(myStartPresentation,oldStr,newStr);
        }

        int navInd = myStartPresentation.indexOf("Navigator.");
        if (navInd != -1)
        {
            int endInd = myStartPresentation.indexOf(">",navInd);
            String ext = myStartPresentation.substring(navInd+9,endInd);
            String oldStr = "Navigator"+ext;
            String newStr = "Navigator"+ext+" target=\"_parent\""; 
            myStartPresentation = mgr.replace(myStartPresentation,oldStr,newStr);
        }

        return myStartPresentation;
    }

    private String modifiedEndPres(ASPManager mgr,String myEndPresentation)
    {
        myEndPresentation = mgr.replace(myEndPresentation,"document.location","parent.location");
        return myEndPresentation;
    }   

    /*public String  modifiedClientScript(ASPManager mgr,String sGenerateClientScript)
    {
       sGenerateClientScript = mgr.replace(sGenerateClientScript,"document.location=","window.parent.location=");
 
       return sGenerateClientScript;
    }*/


//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return sTitleToShow;
    }

    protected String getTitle()
    {
        return sTitleToShow;
    }

    protected AutoString getContents() throws FndException
    { 
        AutoString out = getOutputStream();
        out.clear();
        ASPManager mgr = getASPManager();

        out.append("<html>\n");
        out.append("<head>\n");
        out.append(mgr.generateHeadTag(sTitleToShow));
        out.append("<title></title>\n");
        out.append("</head>\n");
        out.append("<body ");
        out.append(mgr.generateBodyTag());
        out.append(">\n");
        out.append("<form ");
        out.append(mgr.generateFormTag());
        out.append(">\n");
        out.append(modifiedStartPres(mgr,mgr.startPresentation(sTitleToShow)));

        out.append(headlay.show());

        //out.append(modifiedClientScript(mgr,mgr.generateClientScript()));

        out.append(modifiedEndPres(mgr,mgr.endPresentation()));      

        out.append("</form>\n");
        out.append("</body>\n");
        out.append("</html>\n");

        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

        appendDirtyJavaScript("searchBaseName = '");
        appendDirtyJavaScript(searchBaseName);
        appendDirtyJavaScript("';\n");

        appendDirtyJavaScript("SEARCHBASE = SEARCHBASE.replace(\"WorkOrder.page\",searchBaseName);\n");

        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(sPassWoToTree));	//XSS_Safe AMNILK 20070726
        appendDirtyJavaScript("' == 'true')\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   parent.__treePart.location = \"");
        appendDirtyJavaScript(urlLink1);
        appendDirtyJavaScript("\";\n");
        appendDirtyJavaScript("   parent.__costPart.location = \"");
        appendDirtyJavaScript(urlLink2);
        appendDirtyJavaScript("\";\n");
        appendDirtyJavaScript("}\n");

        return out;
    }

}
