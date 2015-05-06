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
*  File        : CalendarGenerationDlg.java 
*  Modified    :
*  SHFELK  010215  Created.
*  BUNILK  010720  Made some changes in redirect way of submit() and cancel() methods, to avoid 
*                  problems in Netscape browser   
*  INROLK  010802  Removed method Week_Org_API.Check_Ordered_week in function submit()..call id 77846.
*  CHCRLK  011011  Changed LOV's of CONTRACT and ORG_CODE.
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  YAWILK  021210  Added MCH_CODE_CONTRACT and SEPERATE_PM Check box
*  THWILK  031020  Call Id 106480,Changed MCH_CODE_CONTRACT into CONTRACT in Check_Generatable_Pm__ method call.
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
*  VAGULK  031229  Made the field order according to the order in the Centura application(Web Alignment).
*  ARWILK  040720  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  NAMELK  041104  Non Standard Translation Constants Changed.
*  AMNILK  051011  Modified submit method.Change the redirecting url to Navigator.page from Default.page.
*  NEKOLK  060109  Bug 55200..Modified LOv for MCH_CODE and ORG_CODE
*  NIJALK  060123  Merged bug 55200.(Lov for MCH_CODE Only.)
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  AMDILK  070330  Merged bug id 64068
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class CalendarGenerationDlg extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.CalendarGenerationDlg");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPHTMLFormatter fmt;
    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPCommandBar headbar;
    private ASPTable headtbl;
    private ASPBlockLayout headlay;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private ASPCommand cmd;
    private ASPQuery q;

    //===============================================================
    // Construction 
    //===============================================================
    public CalendarGenerationDlg(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();

        trans = mgr.newASPTransactionBuffer();
        fmt = mgr.newASPHTMLFormatter();  

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else
        {
            okFind();
            getDefaults();
        }  
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------
    public void  validate()
    {
        ASPManager mgr = getASPManager();
        String val = null;
        String week = null;
        String dateFrom = null;
        String dataTo = null;
        int len = 0;  
        String ye = null; 
        String we = null;  
        int nye = 0;  
        int nwe = 0;  
        String txt = null;

        val = mgr.readValue("VALIDATE");

        if ("WEEK".equals(val))
        {
            week = mgr.readValue("WEEK","");
            len = week.length();        
            ye = week.substring(0,2);
            we = week.substring(2,4);
            nye =toInt(ye)*1;  
            nwe = toInt(we)*1;

            if (( len != 4 ) ||  ( nwe<1 ) ||  ( nwe>53 ) ||  ( nye<0 ) ||  (( nye == 0 ) &&  ( !("00".equals(ye)) )) ||  ( nye>100 ))
            {
                mgr.responseWrite("No_Data_Found" + "Week "+week+" is not a valid week. The format should be YYWW.");
            }
            else
            {
                trans.clear();

                cmd = trans.addCustomFunction("DDATEFROM","Pm_Calendar_API.Get_Date","DATEFROM");
                cmd.addParameter("WEEK");
                cmd.addParameter("DAY_NUMBER","1");

                cmd = trans.addCustomFunction("DDATETO","Pm_Calendar_API.Get_Date","DATETO");
                cmd.addParameter("WEEK");
                cmd.addParameter("DAY_NUMBER","7");

                trans = mgr.perform(trans);

                dateFrom = trans.getBuffer("DDATEFROM/DATA").getFieldValue("DATEFROM");
                dataTo = trans.getBuffer("DDATETO/DATA").getFieldValue("DATETO");

                txt = (mgr.isEmpty(dateFrom) ? "" : (dateFrom))+ "^" + (mgr.isEmpty(dataTo) ? "" : (dataTo))+ "^";

                mgr.responseWrite(txt);
            }
        }
        mgr.endResponse();
    }
//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void  countFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
    }


    public void  getDefaults()
    {
        ASPManager mgr = getASPManager();
        ASPBuffer row = null;
        String contract = null;

        trans.clear();
        cmd = trans.addCustomCommand("CON","Maintenance_Spare_API.Get_Default_Contract");
        cmd.addParameter("CONTRACT");
        trans = mgr.perform(trans);
        contract=trans.getValue("CON/DATA/CONTRACT");

        row = headset.getRow();
        row.setValue("CONTRACT",contract);
        headset.setRow(row);
    }


    public void  okFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.includeMeta("ALL");
        mgr.submit(trans);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWCALENDARGENERATIONDLGNODATA: No data found."));
            headset.clear();
        }
    }

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

    public void  submit()
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer testbuff = null;
        ASPBuffer sec = null;  
        String rcode = null;

        trans.clear();
        testbuff = mgr.newASPTransactionBuffer();
        testbuff.addSecurityQuery("Pm_Calendar_API","Generate__");
        mgr.perform(testbuff);
        sec = testbuff.getSecurityInfo();

        if (!(sec.itemExists("Pm_Calendar_API.Generate__")))
            mgr.showAlert(mgr.translate("PCMWCALENDARGENERATIONDLGALERTMSG1: Week should have a value"));
        else
        {
            rcode="TRUE";

            if ("FALSE".equals(rcode))
                mgr.showAlert(mgr.translate("PCMWCALENDARGENERATIONDLGERROR1: This week is not in the calendar"));
            else
            {
                trans.clear();
                cmd = trans.addCustomCommand("GEN3","Pm_Calendar_Plan_API.Check_Generatable_Pm__");
                cmd.addParameter("RCODE");
                cmd.addParameter("CONTRACT");
                cmd.addParameter("WEEK");
                cmd.addParameter("DATEFROM");
                cmd.addParameter("DATETO");                  
                cmd.addParameter("ORG_CODE");
                cmd.addParameter("ROUNDDEF_ID");
                cmd.addParameter("PRIORITY_ID");
                cmd.addParameter("MCH_CODE");
                cmd.addParameter("GROUP_ID");
                cmd.addParameter("ACTION_CODE_ID");

                trans=mgr.perform(trans);
                rcode=trans.getValue("GEN3/DATA/RCODE");


                if (!("FALSE".equals(rcode)))
                {
                    trans.clear();
                    cmd = trans.addCustomCommand("GEN5","Pm_Calendar_Plan_API.Generate__");
                    cmd.addParameter("CONTRACT");
                    cmd.addParameter("WEEK");
                    cmd.addParameter("DATEFROM");
                    cmd.addParameter("DATETO");                              
                    cmd.addParameter("ORG_CODE");
                    cmd.addParameter("ROUNDDEF_ID");
                    cmd.addParameter("PRIORITY_ID");
                    cmd.addParameter("MCH_CODE");
                    cmd.addParameter("GROUP_ID");
                    cmd.addParameter("ACTION_CODE_ID");
                    cmd.addParameter("SEPERATE_PM");
                    cmd.addParameter("MCH_CODE_CONTRACT");
                    cmd.addParameter("CONTRACT"); // This is the maintenance organization site.
                    trans=mgr.perform(trans);
                    trans.clear();
                    String curr_url = mgr.getURL();
                    int end_pnt = curr_url.indexOf("/CalendarGenerationDlg");
                    String fst_part = curr_url.substring(0,end_pnt); 
                    int end_pnt1 = fst_part.lastIndexOf("/");
                    String snd_part = fst_part.substring(0,end_pnt1+1);
                    String new_url = snd_part + "Navigator.page"+"&MAINMENU=Y&NEW=Y"; 
                    mgr.redirectTo(new_url);
                }

            }          
        }
    }


    public void  cancel()
    {
        ASPManager mgr = getASPManager();

        String curr_url = mgr.getURL();
        int end_pnt = curr_url.indexOf("/CalendarGenerationDlg");
        String fst_part = curr_url.substring(0,end_pnt); 
        int end_pnt1 = fst_part.lastIndexOf("/");
        String snd_part = fst_part.substring(0,end_pnt1+1);
        String new_url = snd_part + "Default.page";
        mgr.redirectTo(new_url);
        headtbl.clearQueryRow();
        headset.setFilterOff();
    }

//-----------------------------------------------------------------------------
//------------------------ PREDEFINE FUNCTION ---------------------------------
//-----------------------------------------------------------------------------

    public void  preDefine()
    {
        ASPManager mgr = getASPManager();


        headblk = mgr.newASPBlock("HEAD");

        headblk.addField("OBJID").
        setFunction("''").
        setHidden();

        headblk.addField("OBJVERSION").
        setFunction("''").
        setHidden();

        headblk.addField("RCODE").
        setSize(11).
        setHidden().
        setFunction("''");

        headblk.addField("CONTRACT").
        setSize(8).
        setFunction("''").
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setLOVProperty("TITLE",mgr.translate("PCMWCALENDARGENERATIONDLGCONT: List of Site")).
        setLabel("PCMWCALENDARGENERATIONDLGCON: WO Site").
        setUpperCase().
        setMaxLength(5);

        headblk.addField("WEEK").
        setSize(12).
        setFunction("''").
        setLabel("PCMWCALENDARGENERATIONDLGWEEK: Week").
        setMaxLength(4).
        setCustomValidation("WEEK","DATEFROM,DATETO");

        headblk.addField("DAY_NUMBER").
        setHidden().
        setFunction("''");

        headblk.addField("DATEFROM", "Date").
        setSize(18).
        setMandatory().
        setLabel("PCMWCALENDARGENERATIONDLGDATEFROM: Date from").
        setFunction("''");

        headblk.addField("DATETO", "Date").
        setSize(18).
        setMandatory().
        setLabel("PCMWCALENDARGENERATIONDLGDATETO: Date to").
        setFunction("''");

        headblk.addField("ORG_CODE").
        setSize(10).
        setFunction("''"). 
        setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,450).
        setLOVProperty("TITLE",mgr.translate("PCMWCALENDARGENERATIONDLGORCO: List of Maintenance Organization")).
        setLabel("PCMWCALENDARGENERATIONDLGORGCODE: Maintenance Organization").
        setUpperCase().
        setMaxLength(8);

        headblk.addField("ROUNDDEF_ID").
        setSize(10).
        setFunction("''").
        setDynamicLOV("PM_ROUND_DEFINITION",600,445).
        setLabel("PCMWCALENDARGENERATIONDLGROUNDDEF_ID: Route ID").
        setUpperCase();

        headblk.addField("PRIORITY_ID").
        setSize(6).
        setFunction("''").
        setDynamicLOV("MAINTENANCE_PRIORITY",600,445).
        setLabel("PCMWCALENDARGENERATIONDLGPRIORITY_ID: Priority").
        setUpperCase();

        headblk.addField("MCH_CODE").
        setSize(15).
        setMaxLength(100).
        setFunction("''").
        setDynamicLOV("MAINTENANCE_OBJECT_LOV","MCH_CODE_CONTRACT CONTRACT",600,445).
        setLabel("PCMWCALENDARGENERATIONDLGMCH_CODE: Object ID").
        setUpperCase();

        headblk.addField("MCH_CODE_CONTRACT").
        setSize(8).
        setFunction("''").
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setLOVProperty("TITLE",mgr.translate("PCMWCALENDARGENERATIONDLGLOVMCHCONTRACT: List of Site")).
        setLabel("PCMWCALENDARGENERATIONDLGMCHCONTRACT: Site").
        setUpperCase().
        setMaxLength(5);

        headblk.addField("GROUP_ID").
        setSize(10).
        setFunction("''").
        setDynamicLOV("EQUIPMENT_OBJ_GROUP",600,445).
        setLabel("PCMWCALENDARGENERATIONDLGGROUP_ID: Group ID").
        setUpperCase();

        headblk.addField("ACTION_CODE_ID").
        setSize(10).
        setFunction("''").
        setDynamicLOV("MAINTENANCE_ACTION",600,445).
        setLabel("PCMWCALENDARGENERATIONDLGACTION_CODE_ID: Action").
        setUpperCase();

        headblk.addField("SEPERATE_PM").
        setSize(10).
        setFunction("''").
        setCheckBox("0,1").
        setLabel("PCMWCALENDARGENERATIONDLGSEPERATE_PM: Seperate PM");

        headblk.setView("DUAL");
        headblk.defineCommand("","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();
        headbar = mgr.newASPCommandBar(headblk);

        headbar.enableCommand(headbar.SAVERETURN);
        headbar.enableCommand(headbar.CANCELEDIT);
        headbar.defineCommand(headbar.SAVERETURN,"submit","checkHeadFields(-1)"); 
        headbar.defineCommand(headbar.CANCELEDIT,"cancel");

        headtbl = mgr.newASPTable(headblk);
        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
        headlay.setEditable();
        headlay.setDialogColumns(3);
        headlay.defineGroup(mgr.translate("PCMWCALENDARGENERATIONDLGGRPLABEL1: Parameters"),"CONTRACT,WEEK,DATEFROM,DATETO",true,true);
        headlay.defineGroup(mgr.translate("PCMWCALENDARGENERATIONDLGGRPLABEL2: Selection Parameters"),"ORG_CODE,ROUNDDEF_ID,MCH_CODE_CONTRACT,MCH_CODE,GROUP_ID,SEPERATE_PM,PRIORITY_ID,ACTION_CODE_ID",true,true);
    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWCALENDARGENERATIONDLGTITLE: Calender Generation";
    }

    protected String getTitle()
    {
        return "PCMWCALENDARGENERATIONDLGTITLE: Calender Generation";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(" <table id=\"cntITEM0\" border=\"0\" width= '75%'>\n");
        appendToHTML(" <tr>\n");
        appendToHTML("  <td>\n");
        appendToHTML(headbar.showBar());
        appendToHTML("</td></tr></table>\n");
        appendToHTML("<table id=\"cntITEM0\" border=\"0\" width= '75%'>\n");
        appendToHTML(" <tr>\n");
        appendToHTML("  <td>\n");
        appendToHTML(fmt.drawReadLabel("PCMWCALENDARGENERATIONDLGBACKGRNDJOBINFOTEXT: This job will be started as a background job. You will find the status of the job in the Background Jobs window, in the general folder in the Navigator."));
        appendToHTML("</td></tr><tr><td>\n");
        appendToHTML(fmt.drawReadLabel("PCMWCALENDARGENERATIONDLGPARAMINFOTEXT: The Selection Parameters are optional. If a parameter is left blank then selection will be done without regard to the value of this parameter."));
        appendToHTML("</td></tr>\n");
        appendToHTML(" </table>\n");
        appendToHTML(" <table id=\"cntITEM0\" border=\"0\" width= '75%'>\n");
        appendToHTML(" <tr>\n");
        appendToHTML("  <td>\n");
        appendToHTML(headlay.generateDataPresentation());
        appendToHTML("</td></tr></table>\n");
    }

}
