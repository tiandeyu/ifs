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
*  File        : CreateWoDlg.java 
*  Modified    :
*  ARWILK  010219  Created.
*  INROLK  011002  WorkOrderStructure is called after creating a WO. call id 69794.
*  CHCRLK  011016  Added field ORGDESC. 70749  
*  SAPRLK  031222  Web Alignment - removed methods clone() and doReset().
*  ARWILK  040720  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning)
*  DiAmlk  050323  Bug ID:122655 - Modified the methods okFind(),preDefine() and ok().Added field ORGCONTRACT. 
*  SHAFLK  050224  Bug 49624, Modified methods ok() and okFind(). 
*  NIJALK  050624  Merged bug 49624.
*  NIJALK  060210  Call 133579: Changed *.asp url pattern to *.page pattern.
*  JEWILK  060803  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged Bug Id: 58214.
*  ILSOLK  070713  Eliminated XSS.
* ----------------------------------------------------------------------------
* -----------------------APP7.5 SP1-------------------------------------
*  071215  ILSOLK  Bug Id 68773, Eliminated XSS.
*  100827  SHAFLK  Bug Id 92599, Modified run(), ok(), cancel() and printContents().
* -----------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class CreateWoDlg extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.CreateWoDlg");


    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPContext ctx;
    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPCommandBar headbar;
    private ASPTable headtbl;
    private ASPBlockLayout headlay;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private boolean fromGraphiStruct;
    private String okClose;
    private String saveClose;
    private String sWoNo;
    private String sErrDescr;
    private String sContract;
    private ASPBuffer buff;
    private ASPBuffer row;
    private String val;
    private ASPCommand cmd;
    private String cusdesc;
    private ASPBuffer buf;
    private ASPQuery q;
    private String wono;
    private String errdescparent;
    private String callingUrl;
    private String txt; 
    private String newWoNo;
    private String sFromForm;
    private String okSub;
    //===============================================================
    // Construction 
    //===============================================================
    public CreateWoDlg(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();


        trans = mgr.newASPTransactionBuffer();
        ctx = mgr.getASPContext();

        fromGraphiStruct = ctx.readFlag("FROMGRAPHISTRUCT",false);
        okClose = ctx.readValue("OKCLOSE","FALSE");
        saveClose = ctx.readValue("SAVECLOSE","FALSE");
        okSub =   ctx.readValue("OKSUB","FALSE");

        sWoNo = ctx.readValue("SWONO","");
        sErrDescr = ctx.readValue("SERRDESCR","");
        sContract = ctx.readValue("SCONTR","");
        newWoNo = ctx.readValue("NEWWONO","");
        sFromForm = ctx.readValue("SFROMFORM","");
        
        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (mgr.dataTransfered())
        {
            buff = mgr.getTransferedData();
            row = buff.getBufferAt(0);
            sWoNo = row.getValue("WO_NO");
            row = buff.getBuffer("1"); 
            sErrDescr = row.getValue("ERR_DESCR");
            row = buff.getBuffer("2"); 
            sContract = row.getValue("CONTRACT");
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
        {
            sWoNo = mgr.readValue("WO_NO");
            sErrDescr = mgr.readValue("ERR_DESCR");
            sContract = mgr.readValue("CONTRACT");
            sFromForm = mgr.readValue("FROMFORM");
            if (mgr.isEmpty(sFromForm)) {
                fromGraphiStruct = true;
            }
        }

        okFind();

        ctx.writeValue("SWONO",sWoNo);
        ctx.writeValue("SERRDESCR",sErrDescr);
        ctx.writeValue("SCONTR",sContract);
        ctx.writeFlag("FROMGRAPHISTRUCT",fromGraphiStruct);
        ctx.writeValue("SFROMFORM",sFromForm);

    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void  validate()
    {
        ASPManager mgr = getASPManager();

        val = mgr.readValue("VALIDATE");

        if ("ORGCODE".equals(val))
        {
            cmd = trans.addCustomFunction("ORGDESC","Organization_Api.Get_Description","ORGDESC");
            cmd.addParameter("CONTRACT");
            cmd.addParameter("ORGCODE");

            trans = mgr.validate(trans);

            String orgdesc = trans.getValue("ORGDESC/DATA/ORGDESC");

            txt = (mgr.isEmpty(orgdesc) ? "" : (orgdesc)); 
            mgr.responseWrite(txt);
        }
        else if ("CUSTOMER_NO".equals(val))
        {
            cmd = trans.addCustomFunction("CUSDES","CUSTOMER_INFO_API.Get_Name","CUSTOMERDESCRIPTION");
            cmd.addParameter("CUSTOMER_NO");

            trans = mgr.validate(trans);

            cusdesc = trans.getValue("CUSDES/DATA/CUSTOMERDESCRIPTION");

            txt = (mgr.isEmpty(cusdesc) ? "" : (cusdesc)); 
            mgr.responseWrite(txt);
        }
        else if ("PLANSDATE".equals(val))
        {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("PLANSDATE",mgr.readValue("PLANSDATE"));
            mgr.responseWrite(buf.getFieldValue("PLANSDATE") +"^" ); 
        }
        else if ("PLANFDATE".equals(val))
        {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("PLANFDATE",mgr.readValue("PLANFDATE"));
            mgr.responseWrite(buf.getFieldValue("PLANFDATE") +"^" ); 
        }

        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void  okFind()
    {
        ASPManager mgr = getASPManager();

        if (!("TRUE".equals(okClose)))
        {
            q = trans.addEmptyQuery(headblk);
            q.includeMeta("ALL");
            mgr.submit(trans);

            if (headset.countRows() == 0)
            {
                mgr.showAlert(mgr.translate("PCMWCREATEWODLGNODATA: No data found."));
            }

            row = headset.getRow();

            trans.clear();

            cmd = trans.addCustomFunction("CUST","ACTIVE_SEPARATE_API.Get_Customer_No","CUSTOMER_NO");
            cmd.addParameter("WONO",sWoNo);

            cmd = trans.addCustomFunction("CUSDES","CUSTOMER_INFO_API.Get_Name","CUSTOMERDESCRIPTION");
            cmd.addReference("CUSTOMER_NO","CUST/DATA");

            trans = mgr.perform(trans);

            String cust = trans.getValue("CUST/DATA/CUSTOMER_NO");
            String cusdesc = trans.getValue("CUSDES/DATA/CUSTOMERDESCRIPTION");                       

            row.setValue("WONO",sWoNo);                    
            row.setValue("ERRDESCPARENT",sErrDescr);
            row.setValue("CONTRACT",sContract);
            row.setValue("ORGCONTRACT",sContract); 

            row.setValue("CUSTOMER_NO",cust);
            row.setValue("CUSTOMERDESCRIPTION",cusdesc);


            headset.setRow(row);                          
        }
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void  ok()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addCustomCommand("CREWO","Active_Separate_API.Insert_Inherit_Wo");
        cmd.addParameter("WONO",headset.getRow().getValue("WONO"));
        cmd.addParameter("WONOOLD",headset.getRow().getValue("WONO"));
        cmd.addParameter("ORGCODE",mgr.readValue("ORGCODE"));
        cmd.addParameter("ORGCONTRACT",mgr.readValue("ORGCONTRACT"));

        cmd.addParameter("ERRDESC",mgr.readValue("ERRDESC"));
        cmd.addParameter("PLANSDATE",mgr.readValue("PLANSDATE"));
        cmd.addParameter("PLANFDATE",mgr.readValue("PLANFDATE"));
        cmd.addParameter("CUSTOMER_NO",mgr.readValue("CUSTOMER_NO"));

        trans = mgr.perform(trans);

        newWoNo = trans.getValue("CREWO/DATA/WONO");

        if (fromGraphiStruct)
        {
            saveClose = "TRUE";
            okClose = "TRUE";

        }
        else
        {
            okSub = "TRUE";
            okClose = "TRUE";

        }
    }


    public void  cancel()
    {
        ASPManager mgr = getASPManager();

        okClose = "TRUE";
    }


    public void  preDefine()
    {
        ASPManager mgr = getASPManager();


        headblk = mgr.newASPBlock("HEAD");

        headblk.addField("OBJID").
        setHidden().
        setFunction("''");

        headblk.addField("OBJVERSION").                 
        setHidden().                                  
        setFunction("''");                            

        headblk.addField("WONO", "Number").            
        setSize(12).                                  
        setLabel("PCMWCREATEWODLGWONO: WO No").                      
        setFunction("''").                            
        setReadOnly();   

        headblk.addField("WONOOLD", "Number").            
        setFunction("''").  
        setHidden();

        headblk.addField("CONTRACT").                  
        setHidden().                                  
        setFunction("''");                            

        headblk.addField("ERRDESCPARENT").             
        setSize(39).                                  
        setLabel("PCMWCREATEWODLGERRDESPAR: Parent Directive").      
        setFunction("''").                            
        setReadOnly();                                

        headblk.addField("ERRDESC").
        setSize(39).
        setMandatory().
        setLabel("PCMWCREATEWODLGERRDESC: New WO Directive").
        setFunction("''");

        headblk.addField("PLANSDATE", "Datetime").
        setSize(12).
        setLabel("PCMWCREATEWODLGPSDATE: Planned Start").
        setFunction("''").
        setCustomValidation("PLANSDATE","PLANSDATE");

        headblk.addField("PLANFDATE", "Datetime").
        setSize(12).
        setLabel("PCMWCREATEWODLGPFDATE: Planned Completion").
        setFunction("''").
        setCustomValidation("PLANFDATE","PLANFDATE");

        headblk.addField("ORGCODE").
        setSize(12).
        setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ORGCONTRACT CONTRACT",600,445).
        setLOVProperty("TITLE",mgr.translate("PCMWCREATEWODLGORGCODELOV: List of Maintenance Organization")).
        setCustomValidation("ORGCODE","ORGDESC").
        setMandatory().
        setLabel("PCMWCREATEWODLGORGCODE: Maintenance Organization").
        setFunction("''").
        setUpperCase();

        headblk.addField("ORGCONTRACT").
        setSize(8).
        setDynamicLOV("USER_ALLOWED_SITE_LOV").
        setLabel("PCMWCREATEWODLGORGCONTRACT: Maint.Org. Site").
        setMandatory().
        setUpperCase().
        setFunction("''").
        setMaxLength(5);

        headblk.addField("ORGDESC").
        setSize(30).
        setLabel("PCMWCREATEWODLGORGCODEDESC: Organization Description").
        setFunction("''").
        setReadOnly();

        headblk.addField("CUSTOMER_NO").
        setSize(12).
        setDynamicLOV("CUSTOMER_INFO",600,445).
        setLOVProperty("TITLE",mgr.translate("PCMWCREATEWODLGCUSNOLOV: List of Customer No")).
        setLabel("PCMWCREATEWODLGCUSNO: Customer No").
        setCustomValidation("CUSTOMER_NO","CUSTOMERDESCRIPTION").
        setUpperCase().
        setFunction("''");

        headblk.addField("CUSTOMERDESCRIPTION").
        setSize(27).
        setFunction("''"). 
        setLabel("PCMWCREATEWODLGCUSTDESC: Description").
        setReadOnly();

        headblk.setView("DUAL");
        headset = headblk.getASPRowSet();
        headbar = mgr.newASPCommandBar(headblk);

        headbar.enableCommand(headbar.SAVERETURN);
        headbar.enableCommand(headbar.CANCELEDIT);
        headbar.defineCommand(headbar.SAVERETURN,"ok","checkHeadFields(-1)");
        headbar.defineCommand(headbar.CANCELEDIT,"cancel");

        headtbl = mgr.newASPTable(headblk);
        headlay = headblk.getASPBlockLayout();
        headlay.setDialogColumns(1); 
        headlay.setDefaultLayoutMode(headlay.EDIT_LAYOUT);
        headlay.setEditable();
        headlay.setFieldOrder("WONO");
        headlay.defineGroup(mgr.translate("PCMWCREATEWODLGGRPLABEL1: Parent Work Order"),"WONO,ERRDESCPARENT",true,true);
        headlay.defineGroup(mgr.translate("PCMWCREATEWODLGGRPLABEL2: New Work Order"),"ERRDESC,PLANSDATE,PLANFDATE,ORGCODE,ORGDESC,ORGCONTRACT,CUSTOMER_NO,CUSTOMERDESCRIPTION,CONTRACT",true,true);
        headlay.setSimple("ORGDESC");
        headlay.setSimple("CUSTOMERDESCRIPTION");
    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWCREATEWODLGTITLE: Create Work Order under Parent";
    }

    protected String getTitle()
    {
        return "PCMWCREATEWODLGTITLE: Create Work Order under Parent";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();
        appendToHTML(headlay.show());
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(saveClose)); // Bug Id 68773
        appendDirtyJavaScript("' == \"TRUE\")\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("      window.open(\"ActiveSeparate2WOStructure.page?WO_NO=\"+'");
        appendDirtyJavaScript(mgr.URLEncode(newWoNo));
        appendDirtyJavaScript("'+\"\",\"main\",\"toolbar=yes,menubar,location,directories=yes,status=yes,scrollbars\");\n");
        appendDirtyJavaScript("      window.open(\"WorkOrderNavigator.page?WO_NO=\"+'");
        appendDirtyJavaScript("'+\"\",\"navigator\",\"toolbar=yes,menubar,location,directories=yes,status=yes,scrollbars\");\n");
        appendDirtyJavaScript("  window.close();\n");
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(okClose)); // Bug Id 68773
        appendDirtyJavaScript("' == \"TRUE\")\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("  window.close();\n");
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(okSub));
        appendDirtyJavaScript("' == \"TRUE\")\n");
	appendDirtyJavaScript("{\n");
	appendDirtyJavaScript("  window.opener.location = \"SeparateWorkOrder.page?WO_NO=");
	appendDirtyJavaScript(mgr.encodeStringForJavascript(sWoNo)); // XSS_Safe SHAFLK 20100827
	appendDirtyJavaScript(" \";\n");
	appendDirtyJavaScript("  window.close();\n");
	appendDirtyJavaScript("}\n"); 

    }

}
