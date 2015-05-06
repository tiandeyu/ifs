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
*  File        : ChangePMActionDlg.java 
*  Created     : SHFELK  010516  Created .
*  Modified    :
*  CHCRLK  010613  Modified overwritten validations.
*  ARWILK  031222  Edge Developments - (Removed clone and doReset Methods)
* --------------------------------- Edge - SP1 Merge -------------------------
*  SHAFLK  040119  Bug Id 41815,Removed Java dependencies. 
*  THWILK  040325  Merge with SP1.
*  SHAFLK  050425  Bug 50830, Used mgr.translateJavaScript() method to get the correctly translated strings in JavaScript functions.
*  NIJALK  050617  Merged bug 50830. 
*  JEWILK  060803  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged Bug Id: 58214.
*  ILSOLK  070709  Eliminated XSS.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class ChangePMActionDlg extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.ChangePMActionDlg");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPHTMLFormatter fmt;
    private ASPContext ctx;
    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPCommandBar headbar;
    private ASPTable headtbl;
    private ASPBlockLayout headlay;
    private ASPField f;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private String roundDefID;
    private String cancelFlag;
    private String saveReturnFlag;
    private ASPBuffer temp;
    private ASPCommand cmd;

    //===============================================================
    // Construction 
    //===============================================================
    public ChangePMActionDlg(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();

        trans = mgr.newASPTransactionBuffer();
        fmt = mgr.newASPHTMLFormatter();
        ctx = mgr.getASPContext();

        roundDefID = ctx.readValue("ROUNDDEFID","");
        cancelFlag = "";
        saveReturnFlag = "";

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if ((!mgr.isEmpty(mgr.getQueryStringValue("ROUNDDEF_ID"))))
        {
            roundDefID = mgr.readValue("ROUNDDEF_ID");
            mgr.setPageExpiring();
        }
        startup();
        ctx.writeValue("ROUNDDEF_ID",roundDefID); 
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void  validate()
    {
        String val = null;
        ASPBuffer buf = null;
        String txt = null;
        ASPManager mgr = getASPManager();

        val = mgr.readValue("VALIDATE");

        if ("START".equals(val))
        {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("START",mgr.readValue("START"));

            txt = mgr.readValue("START");
            mgr.responseWrite(txt);    
        }

        if ("INTERVAL".equals(val))
        {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("INTERVAL",mgr.readValue("INTERVAL"));

            txt = mgr.readValue("INTERVAL");
            mgr.responseWrite(txt);    
        }
        mgr.endResponse();
    }

    public void  startup()
    {

        headset.addRow(trans.getBuffer("HEAD/DATA"));
        temp = headset.getRow();
        temp.setValue("ROUTEID",roundDefID);
        headset.setRow(temp);
    }

    public void  saveReturn()
    {
        String flg = null;
        ASPManager mgr = getASPManager();

        if (!((mgr.isEmpty(mgr.readValue("ROUTEID"))) || (mgr.isEmpty(mgr.readValue("START"))) || (mgr.isEmpty(mgr.readValue("INTERVAL"))) ))
        {
            cmd = trans.addCustomFunction("ISPMROUNDDEF", "PM_ROUND_DEFINITION_API.Is_Pm_Round_Defintion_Id","ISPMROUND");
            cmd.addParameter("ROUTEID",mgr.readValue("ROUTEID"));

            trans = mgr.perform(trans);
            flg =trans.getValue("ISPMROUNDDEF/DATA/ISPMROUND");
            trans.clear();

            if ("TRUE".equals(flg))
            {
                cmd = trans.addCustomCommand("UPDATEPMORD", "PM_ACTION_API.Update_Pm_Order_Id");
                cmd.addParameter("ROUTEID",mgr.readValue("ROUTEID"));
                cmd.addParameter("START",mgr.readValue("START"));
                cmd.addParameter("INTERVAL",mgr.readValue("INTERVAL"));

                trans = mgr.perform(trans);

                roundDefID = mgr.readValue("ROUTEID");
                saveReturnFlag = "TRUE";   
            }
            else
            {
                roundDefID = mgr.readValue("ROUTEID");
                mgr.showAlert("PCMWCHANGEPMACTIONDLGERROR1: Selected Route ID does not exist.");
                headset.addRow(trans.getBuffer("HEAD/DATA"));
                temp = headset.getRow();
                temp.setValue("ROUTEID","");
                temp.setValue("START","");
                temp.setValue("INTERVAL","");           
                headset.setRow(temp);
            }   
        }
    }

    public void  cancelEdit()
    {

        cancelFlag = "TRUE";
    }

    public void  preDefine()
    {
        ASPManager mgr = getASPManager();

        mgr.beginASPEvent();

        headblk = mgr.newASPBlock("HEAD");

        headblk.addField("OBJID").
        setHidden().
        setFunction("''");

        headblk.addField("OBJVERSION").
        setHidden().
        setFunction("''");

        f = headblk.addField("ROUTEID");
        f.setSize(8);
        f.setDynamicLOV("PM_ROUND_DEFINITION",600,445);
        f.setLabel("PCMWCHANGEPMACTIONDLGROUTEID: Route ID ");
        f.setFunction("''");
        f.setMandatory();
        f.setUpperCase(); 

        f = headblk.addField("START", "Number");
        f.setSize(8);
        f.setLabel("PCMWCHANGEPMACTIONDLGSTART: Start from Order Number "); 
        f.setMandatory();
        f.setCustomValidation("START","START");
        f.setFunction("''"); 

        f = headblk.addField("INTERVAL", "Number");
        f.setSize(8);
        f.setLabel("PCMWCHANGEPMACTIONDLGINTERVAL: New Interval ");
        f.setMandatory();
        f.setCustomValidation("INTERVAL","INTERVAL");   
        f.setFunction("''");

        headblk.addField("ISPMROUND").
        setHidden().
        setFunction("''");

        headblk.setView("");
        headblk.defineCommand("","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.defineCommand(headbar.SAVERETURN,"saveReturn","checkHeadFields()");
        headbar.defineCommand(headbar.CANCELEDIT,"cancelEdit");
        headbar.removeCommandGroup(headbar.CMD_GROUP_CUSTOM);
        headbar.enableCommand(headbar.SAVERETURN);
        headbar.enableCommand(headbar.CANCELEDIT);

        headtbl = mgr.newASPTable(headblk);

        headlay = headblk.getASPBlockLayout();

        headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
        headlay.setEditable();
    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWCHANGEPMACTIONDLGTITLE: Change Route PM Action";
    }

    protected String getTitle()
    {
        return "PCMWCHANGEPMACTIONDLGCHANGEPM: Change Route PM Action";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        if (headlay.isVisible())
        {
            appendToHTML(headbar.showBar());
            appendToHTML("<p>");
            appendToHTML(fmt.drawReadValue(mgr.translate("PCMWCHANGEPMACTIONDLGHEAD1ROW1: To be able to change the order of Route PM Actions please enter the Route ID and select a start order number together with an interval.")));
            appendToHTML("</p>\n");
            appendToHTML(         headlay.generateDialog(         )); // XSS_Safe ILSOLK 20070713
        }

        appendDirtyJavaScript("function checkHeadFields(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   return checkRouteid(i) &&\n");
        appendDirtyJavaScript("   checkStart(i) &&\n");
        appendDirtyJavaScript("   checkInterval(i);\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(cancelFlag);
        appendDirtyJavaScript("'=='TRUE')\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   self.close();\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(saveReturnFlag);
        appendDirtyJavaScript("'=='TRUE')\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   roundID = '");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(roundDefID)); // XSS_Safe ILSOLK 20070709
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("   qryStr = \"PMRoundDefinition.page?ROUNDDEF_ID=\"+URLClientEncode(roundID);\n");
        appendDirtyJavaScript("   window.open(qryStr,\"PMRoundDef\",\"\");\n");
        appendDirtyJavaScript("   self.close();\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateStart(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("   setDirty();\n");
        appendDirtyJavaScript("   if( !checkStart(i) ) return;\n");
        appendDirtyJavaScript("   if( getValue_('START',i)=='' )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("      getField_('START',i).value = '';\n");
        appendDirtyJavaScript("      return;\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("   window.status='Please wait for validation';\n");
        appendDirtyJavaScript("   r = __connect(\n");
        appendDirtyJavaScript("       '");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=START'\n");
        appendDirtyJavaScript("       + '&START=' + URLClientEncode(getValue_('START',i))\n");
        appendDirtyJavaScript("       );\n");
        appendDirtyJavaScript("   window.status='';\n");
        appendDirtyJavaScript("   if( checkStatus_(r,'START',i,'Start from Order Number ') )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("      assignValue_('START',i,0);\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("   if (getValue_(\"START\") != '')\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("      if (getValue_(\"START\") < 0)\n");
        appendDirtyJavaScript("      {\n");
        appendDirtyJavaScript("         alert('");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWCHANGEPMACTIONDLGPOSINT: Value must be a positive integer."));
        appendDirtyJavaScript("');\n");
        appendDirtyJavaScript("	 getField_('START',i).value = '';\n");
        appendDirtyJavaScript("      }\n");
        appendDirtyJavaScript("   }			\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateInterval(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("   setDirty();\n");
        appendDirtyJavaScript("   if( !checkInterval(i) ) return;\n");
        appendDirtyJavaScript("   if( getValue_('INTERVAL',i)=='' )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("      getField_('INTERVAL',i).value = '';\n");
        appendDirtyJavaScript("      return;\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("   window.status='Please wait for validation';\n");
        appendDirtyJavaScript("   r = __connect(\n");
        appendDirtyJavaScript("       '");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=INTERVAL'\n");
        appendDirtyJavaScript("       + '&INTERVAL=' + URLClientEncode(getValue_('INTERVAL',i))\n");
        appendDirtyJavaScript("       );\n");
        appendDirtyJavaScript("   window.status='';\n");
        appendDirtyJavaScript("   if( checkStatus_(r,'INTERVAL',i,'New Interval ') )\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("      assignValue_('INTERVAL',i,0);\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("   if (getValue_(\"INTERVAL\") != '')\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("      if (getValue_(\"INTERVAL\") < 0)\n");
        appendDirtyJavaScript("      {\n");
        appendDirtyJavaScript("         alert('");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWCHANGEPMACTIONDLGPOSINT: Value must be a positive integer."));
        appendDirtyJavaScript("');\n");
        appendDirtyJavaScript("         getField_('INTERVAL',i).value = '';\n");
        appendDirtyJavaScript("      }\n");
        appendDirtyJavaScript("   }		\n");
        appendDirtyJavaScript("}\n");

    }
}
