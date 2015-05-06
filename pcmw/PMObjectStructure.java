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
*  File        : PMObjectStructure.java 
*  Created     : 041213  NIJALK (AMEC112 - Job Program)
*  Modified    :
*  050117   NIJALK   Removed unused variables.
*  051110   NIJALK   Replaced 'frames' with 'iframes' for child windows.
*  060126   THWILK   Corrected localization errors.
*  080206   CHANLK   Bug 71027,Correct field properties.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class PMObjectStructure extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.PMObjectStructure");

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
    
    private ASPTabContainer tabs;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private ASPQuery q;
    private ASPField f;

    private String sMchCodePass;
    private String sMchNamePass;
    private String sContractPass;
    private String urlLink1;
    private String urlLink2;
    private String callingUrl;

    //===============================================================
    // Construction 
    //===============================================================
    public PMObjectStructure(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();

        trans = mgr.newASPTransactionBuffer();
        fmt = mgr.newASPHTMLFormatter();
        ctx = mgr.getASPContext();

        callingUrl = ctx.findGlobal("CALLING_FORM","");

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE")) && !mgr.isEmpty(mgr.getQueryStringValue("CONTRACT")))
        {
            okFind();
        }   
        else if (mgr.dataTransfered())
            okFind();

        adjust();

        tabs.saveActiveTab();
    }


//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------
    
//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void okFind()
    {
            ASPManager mgr = getASPManager();

            q = trans.addQuery(headblk);
            q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");

            if (mgr.dataTransfered())
                    q.addOrCondition(mgr.getTransferedData());

            q.includeMeta("ALL");

            mgr.querySubmit(trans, headblk);

            eval(headset.syncItemSets());
                
            if (headset.countRows() == 1)
                    headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            else if (headset.countRows() == 0)
            {
                    mgr.showAlert(mgr.translate("PMOBJECTSTRUCTURENODATA: No data found."));
                    headset.clear();
            }
    }

    public void countFind()
    {
            ASPManager mgr = getASPManager();

            q = trans.addQuery(headblk);
            q.setSelectList("to_char(count(*)) N");
            q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
            mgr.submit(trans);
            headlay.setCountValue(toInt(headset.getRow().getValue("N")));
            headset.clear();
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
//----------------------------  SELECT FUNCTION  ------------------------------
//-----------------------------------------------------------------------------


    public void preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("COMPANY");
        f.setLabel("PCMWPMOBJECTSTRUCTURECOMPANY: Company");
        f.setHidden();

        f = headblk.addField("MCH_CODE");
        f.setLabel("PCMWPMOBJECTSTRUCTUREMCHCODE: Object ID");
        f.setHilite();
        f.setDynamicLOV("MAINTENANCE_OBJECT",600,450);
        f.setLOVProperty("WHERE","CONTRACT = CONTRACT");
        f.setReadOnly();
        f.setUpperCase();

        f = headblk.addField("MCH_NAME");
        f.setLabel("PCMWPMOBJECTSTRUCTUREMCHNAME: Description");
        f.setReadOnly();
        f.setHilite();
        f.setMaxLength(45);

        f = headblk.addField("CONTRACT");
        f.setLabel("PCMWPMOBJECTSTRUCTURECONTRACT: Site");
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
        f.setReadOnly();
        f.setUpperCase();

        f = headblk.addField("OBJ_LEVEL");
        f.setLabel("PCMWPMOBJECTSTRUCTUREOBJLEVEL: Object Level");
        f.setReadOnly();
        f.setDynamicLOV("EQUIPMENT_OBJECT_LEVEL",600,450);
        f.setMaxLength(45);

        f = headblk.addField("OPERATIONAL_STATUS");
        f.setLabel("PCMWPMOBJECTSTRUCTUREOPERATIONALSTATUS: Operational Status");
        f.setReadOnly();

        f = headblk.addField("CATEGORY_ID");
        f.setLabel("PCMWPMOBJECTSTRUCTURECATEGORYID: Object Category");
        f.setDynamicLOV("EQUIPMENT_OBJ_CATEGORY",600,450);
        f.setReadOnly();
        f.setUpperCase();

        f = headblk.addField("MCH_TYPE");
        f.setLabel("PCMWPMOBJECTSTRUCTUREMCHTYPE: Object Type");
        f.setReadOnly();
        f.setDynamicLOV("EQUIPMENT_OBJ_TYPE",600,450);
        f.setMaxLength(20);
        f.setUpperCase();
        
        f = headblk.addField("TYPE");
        f.setLabel("PCMWPMOBJECTSTRUCTURETYPE: Type Designation");
        f.setReadOnly();
        f.setUpperCase();
        
        f = headblk.addField("PART_NO");
        f.setLabel("PCMWPMOBJECTSTRUCTUREPARTNO: Part No");
        f.setDynamicLOV("PART_CATALOG",600,450);
        f.setReadOnly();
        f.setUpperCase();

        f = headblk.addField("SERIAL_NO");
        f.setLabel("PCMWPMOBJECTSTRUCTURESERIALNO: Serial No");
        f.setReadOnly();
        f.setUpperCase();

        headblk.setView("MAINTENANCE_OBJECT");

        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.disableModeLabel();
        headbar.enableCommand(headbar.FIND);

        headbar.addCustomCommand("activatePMAction", "");
        headbar.addCustomCommand("activateMaintenancePlan", "");
       
        headtbl = mgr.newASPTable(headblk);
        headtbl.setWrap();

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        headlay.setDialogColumns(2); 

        tabs = mgr.newASPTabContainer();
        tabs.addTab(mgr.translate("PCMWPMOBJECTSTRUCTUREPMACTION: PM Action"), "javascript:commandSet('HEAD.activatePMAction','')");
        tabs.addTab(mgr.translate("PCMWPMOBJECTSTRUCTUREMAINTPLAN: Maintenance Plan"), "javascript:commandSet('HEAD.activateMaintenancePlan','')");

        this.disableFooter();

        //-------------itemblk----------------------------------------------------------------

        itemblk = mgr.newASPBlock("ITEM");

        itembar = mgr.newASPCommandBar(itemblk);
        itembar.disableModeLabel();
        itembar.disableCommand(itembar.FIND);
        itembar.disableMinimize();

        itemlay = itemblk.getASPBlockLayout();
        itemlay.setDefaultLayoutMode(itemlay.CUSTOM_LAYOUT);
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        ctx.setGlobal("CALLING_FORM",callingUrl);
        callingUrl = callingUrl.toUpperCase();                     

        sMchCodePass = "";
        sMchNamePass = "";
        sContractPass = "";

        headbar.removeCustomCommand("activatePMAction");
        headbar.removeCustomCommand("activateMaintenancePlan");

        if ((( headset.countRows()>0 )&& ( headlay.isSingleLayout() ))|| (( headset.countRows() == 1 ) &&  !( headlay.isFindLayout() )))
        {
                sMchCodePass = headset.getRow().getValue("MCH_CODE");
                sMchNamePass = headset.getRow().getValue("MCH_NAME");
                sContractPass = headset.getRow().getValue("CONTRACT");

                urlLink1 = "PmObjStructNavigator.page?MCH_CODE="+mgr.URLEncode(sMchCodePass)+
                                                      "&MCH_NAME="+mgr.URLEncode(sMchNamePass)+
                                                      "&CONTRACT="+mgr.URLEncode(sContractPass);

                if (tabs.getActiveTab() == 1)
                {
                    urlLink1 = "PmObjStructNavigator.page?MCH_CODE="+mgr.URLEncode(sMchCodePass)+
                        "&MCH_NAME="+mgr.URLEncode(sMchNamePass)+
                        "&CONTRACT="+mgr.URLEncode(sContractPass)+"&ACTTAB=1";

                    urlLink2 = "PmObjStructPmAction.page?MCH_CODE="+mgr.URLEncode(sMchCodePass)+
                        "&CONTRACT="+mgr.URLEncode(sContractPass);
                }
                else if (tabs.getActiveTab() == 2)
                {
                    urlLink1 = "PmObjStructNavigator.page?MCH_CODE="+mgr.URLEncode(sMchCodePass)+
                        "&MCH_NAME="+mgr.URLEncode(sMchNamePass)+
                        "&CONTRACT="+mgr.URLEncode(sContractPass)+"&ACTTAB=2";
                    
                    urlLink2 = "PmObjStructMaintPlan.page?MCH_CODE="+mgr.URLEncode(sMchCodePass)+
                        "&CONTRACT="+mgr.URLEncode(sContractPass);
                }
        }
        else if (( headlay.isFindLayout() )|| (( headlay.isMultirowLayout() )&& ( headset.countRows() != 1 ))|| ( headset.countRows() == 0 ))
        {
                urlLink1 = "PmObjStructNavigator.page";

                if (tabs.getActiveTab() == 1)
                    urlLink2 = "PmObjStructPmAction.page";
                else if (tabs.getActiveTab() == 2)
                    urlLink2 = "PmObjStructMaintPlan.page";
        }
    }

    public void activatePMAction()
    {
        tabs.setActiveTab(1);
    }

    public void activateMaintenancePlan()
    {
        tabs.setActiveTab(2);
    }

    private String getNewTitle()
    {
        if (headlay.isSingleLayout() && headset.countRows()>0)
            return "PCMWPMOBJECTSTRUCTURETITLE: PM for Object Structure: "+headset.getRow().getValue("MCH_CODE")+" - "+headset.getRow().getValue("MCH_NAME")+"";
        else
            return "PCMWPMOBJECTSTRUCTURETITLEMULTI: PM for Object Structure"; 

    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return getNewTitle(); 
    }

    protected String getTitle()
    {
        return getNewTitle(); 
    }

    protected AutoString getContents() throws FndException
    { 
        AutoString out = getOutputStream();
        out.clear();
        ASPManager mgr = getASPManager();

        out.append("<html>\n");
        out.append("<head>\n");
        out.append(mgr.generateHeadTag(getNewTitle()));
        out.append("<title></title>\n");
        out.append("</head>\n");
        out.append("<body ");
        out.append(mgr.generateBodyTag());
        out.append(">\n");
        out.append("<form ");
        out.append(mgr.generateFormTag());
        out.append(">\n");
        out.append(mgr.startPresentation(getNewTitle()));

        out.append(headlay.show());
        if (headlay.isSingleLayout() && headset.countRows()>0)
        {
            out.append(tabs.showTabsInit());
            out.append(itembar.showBar());
        }

        if (headlay.isSingleLayout() && (headset.countRows()>0) )
        {
           out.append("<iframe name=\"__treePart\" src=\"" + urlLink1 + "\" width = \"25%\" height = \"100%\" >");
           out.append("</iframe>");
           out.append("<iframe name=\"__pmPart\" src=\"" + urlLink2 + "\" width = \"74%\" height = \"100%\" >");
           out.append("</iframe>");
        }

        out.append(mgr.endPresentation());      
        
        out.append("</form>\n");
        out.append("</body>\n");
        out.append("</html>\n");

        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

        return out;
    }
}
