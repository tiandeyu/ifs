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
*  File        :  MoveToRepairWorkshopDlg.java 
*  Created     :  ASP2JAVA Tool  010307
*  Modified    :  
*  BUNILK  010308  Corrected some conversion errors.
*  JEWILK  010403  Changed file extensions from .asp to .page
*  CHDELK  010410  Change the title from "Move To Reapir Workshop" to "Turn Into Repair Order"
*  CHCRLK  010613  Modified overwritten validations.
*  GACOLK  021204  Set Max Length of MCH_CODE, SUP_MCH_CODE to 100
*  GACOLK  021204  Set Max Length of SERIAL_NO to 50
*  ARWILK  031219  Edge Developments - (Removed clone and doReset Methods)
* ------------------------------  EDGE - SP1 Merge ---------------------------
*  SHAFLK  040121  Bug Id 41815,Removed Java dependencies.   
*  VAGULK  040324  Merged with SP1.
*  ARWILK  041111  Replaced getContents with printContents.    
*  SHAFLK  050425  Bug 50830, Used mgr.translateJavaScript() method to get the correctly translated strings in JavaScript functions.
*  NIJALK  050617  Merged bug 50830.
*  AMDILK  060728  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged Bug Id: 58214. 
*  AMDILK  070621  Call ID 144270: Modified preDefinde()
*  AMNILK  070719  Eliminated XSS Security Vulnerability.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class MoveToRepairWorkshopDlg extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.MoveToRepairWorkshopDlg");

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
    private ASPBlock tempblk;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private String mchc;
    private String cont;
    private String tr_Contract;
    private String tr_MchCode;
    private ASPTransactionBuffer trans;
    private String frmname;
    private String actSep2;
    private String actSep2SM;
    private String wo_No;
    private String qrystr;
    private String CancelFlag;
    private String FinishFlag;
    private String WoTurnYes;
    private String sup_contract;
    private ASPTransactionBuffer secBuff;
    private ASPCommand cmd;
    private String mchname;
    private String part_no;
    private String serial_no;
    private String isClient;
    private boolean flag;
    private String cbaccounted;
    private ASPBuffer myrow;
    private String company;
    private String mch_code;
    private String sup_mch_code;
    private ASPQuery q;
    private ASPBuffer data;
    private ASPBuffer temp;

    //===============================================================
    // Construction 
    //===============================================================
    public MoveToRepairWorkshopDlg(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        mchc =  "";
        cont =  "";
        tr_Contract =  "";
        tr_MchCode =  "";
        ASPManager mgr = getASPManager();

        trans = mgr.newASPTransactionBuffer();
        ctx = mgr.getASPContext();

        frmname = ctx.readValue("FRMNAME", "");
        actSep2 = ctx.readValue("ACTSEP2", "FALSE");
        actSep2SM = ctx.readValue("ACTSEP2SM", "FALSE");
        wo_No = ctx.readValue("WONO", "");
        tr_MchCode = ctx.readValue("MCHCODE", tr_MchCode);
        tr_Contract = ctx.readValue("CON", tr_Contract);
        qrystr = ctx.readValue("QRYSTR", "");

        CancelFlag = ""; 
        FinishFlag = ""; 
        WoTurnYes  = "";

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE")))
        {
            okFind();
            frmname = mgr.readValue("FRMNAME");

            if ("prepareWorkOrd".equals(frmname))
            {
                actSep2="TRUE";
                actSep2SM="FALSE";
            }

            else if ("prepareWorkOrdSM".equals(frmname))
            {
                actSep2="FALSE";
                actSep2SM="TRUE";
            }

            qrystr = mgr.readValue("QRYSTR");      
            tr_MchCode = mgr.readValue("MCH_CODE");
            tr_Contract = mgr.readValue("CONTRACT");
            wo_No = mgr.readValue("WO_NO");
            sup_contract=tr_Contract;
            mchc = tr_MchCode;
            cont = tr_Contract;
            intVali(); 
        }
        else
            newRow();

        ctx.writeValue("ACTSEP2", actSep2);
        ctx.writeValue("ACTSEP2SM", actSep2SM);
        ctx.writeValue("MCHCODE",tr_MchCode);
        ctx.writeValue("CON", tr_Contract);
        ctx.writeValue("QRYSTR", qrystr);   
        ctx.writeValue("WONO", wo_No);   
        ctx.writeValue("FRMNAME", frmname);   
    }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
// This function is used to check security status for methods which
// are in modules Order,Purch and Invent .This function is called before
// the required method is performed.  -NUPE,21-07-2000

    public boolean  checksec( String method, int ref)
    {
        ASPManager mgr = getASPManager();
        String isSecure[] = new String[6] ;

        isSecure[ref] = "false" ; 
        String splitted[] = split(method, "."); 

        secBuff = mgr.newASPTransactionBuffer();
        secBuff.addSecurityQuery(splitted[0], splitted[1]);

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

    public void intVali()
    {
        String isSecure[] = new String[6] ; 

        int ref;
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addCustomCommand("CON1","Maintenance_Spare_API.Get_Default_Contract");
        cmd.addParameter("CONTRACT");

        cmd = trans.addCustomFunction( "OBJDES", "Equipment_Serial_API.Get_Mch_Name", "MCHNAME" );
        cmd.addParameter("CONTRACT", tr_Contract);
        cmd.addParameter("MCH_CODE", tr_MchCode);

        cmd = trans.addCustomFunction( "PARTNO", "Equipment_Serial_API.Get_Part_No", "PART_NO" );
        cmd.addParameter("CONTRACT", tr_Contract);
        cmd.addParameter("MCH_CODE", tr_MchCode);

        cmd = trans.addCustomFunction( "SERIALNO", "Equipment_Serial_API.Get_Serial_No", "SERIAL_NO" );
        cmd.addParameter("CONTRACT", tr_Contract);
        cmd.addParameter("MCH_CODE", tr_MchCode);

        trans=mgr.perform(trans);  

        mchname = trans.getValue("OBJDES/DATA/MCHNAME");
        part_no = trans.getValue("PARTNO/DATA/PART_NO");
        serial_no = trans.getValue("SERIALNO/DATA/SERIAL_NO");

        if (checksec("Inventory_Part_API.Get_Zero_Cost_Flag",1))
        {
            cmd = trans.addCustomFunction( "VAL2", "Inventory_Part_API.Get_Zero_Cost_Flag", "ISCLIENTVALUE" );
            cmd.addParameter("CONTRACT", tr_Contract);  
            cmd.addReference("PART_NO", "PARTNO/DATA");
        }
        if (checksec("Inventory_Part_Zero_Cost_API.Get_client_value",2))
        {
            cmd = trans.addCustomFunction( "VAL3", "Inventory_Part_Zero_Cost_API.Get_client_value", "ISINSURANCEPART" );
            cmd.addParameter("INDEX1");
        }
        ref = 0;

        String isclient = ""; 
        String isinsur = "";

        if (isSecure[ref += 1] =="true")
        {
            isclient = trans.getValue("VAL2/DATA/ISCLIENTVALUE");
        }
        else
            isClient   = "";

        if (isSecure[ref += 1] =="true")
        {
            isinsur = trans.getValue("VAL3/DATA/ISINSURANCEPART");
        }
        else
            isinsur = "";

        flag = false;

        if (isinsur.equals(isclient))
            flag = true;

        if (flag)
            cbaccounted="checked";

        myrow = headset.getRow();
        myrow.setValue("CONTRACT",tr_Contract);
        myrow.setValue("SUP_CONTRACT",tr_Contract);
        myrow.setValue("MCH_CODE",tr_MchCode);
        myrow.setValue("PART_NO",part_no);
        myrow.setValue("SERIAL_NO",serial_no);
        myrow.setValue("MCHNAME",mchname);

        headset.setRow(myrow);
    }


    public void validate()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        String val = mgr.readValue("VALIDATE");
        String txt = "";

        if ("MCH_CODE".equals(val))
        {
            cmd = trans.addCustomFunction( "OBJDES", "Equipment_Serial_API.Get_Mch_Name", "MCHNAME" );
            cmd.addParameter("CONTRACT");
            cmd.addParameter("MCH_CODE");

            cmd = trans.addCustomFunction( "PARTNO", "Equipment_Serial_API.Get_Part_No", "PART_NO" );
            cmd.addParameter("CONTRACT");
            cmd.addParameter("MCH_CODE");

            cmd = trans.addCustomFunction( "SERIALNO", "Equipment_Serial_API.Get_Serial_No", "SERIAL_NO" );
            cmd.addParameter("CONTRACT");
            cmd.addParameter("MCH_CODE");

            cmd = trans.addCustomFunction( "VAL2", "Inventory_Part_API.Get_Zero_Cost_Flag", "ISCLIENTVALUE" );
            cmd.addParameter("CONTRACT"); 
            cmd.addReference("PART_NO","PARTNO/DATA"); 

            cmd = trans.addCustomFunction( "VAL3", "Inventory_Part_Zero_Cost_API.Get_client_value", "ISINSURANCEPART" );
            cmd.addParameter("INDEX1","2");

            trans = mgr.validate(trans);

            String mch = trans.getValue("OBJDES/DATA/MCHNAME");
            String part = trans.getValue("PARTNO/DATA/PART_NO");
            String serial = trans.getValue("SERIALNO/DATA/SERIAL_NO");
            String isclient = trans.getValue("VAL2/DATA/ISCLIENTVALUE");
            String isinsur = trans.getValue("VAL3/DATA/ISINSURANCEPART");

            String cbacc = "";

            if (isinsur.equals(isclient))
                cbacc = "TRUE";

            txt=(((mch==null)?"":mch)+"^"+((part==null)?"":part)+"^"+((serial==null)?"":serial)+"^"+((cbacc==null)?"":cbacc))+"^";

            mgr.responseWrite(txt);
            mgr.endResponse();
        }

        if ("SUP_MCH_CODE".equals(val))
        {
            cmd = trans.addCustomFunction( "VAL6", "Maintenance_Object_API.Get_Mch_Name","SUPMCHNAME");
            cmd.addParameter("SUP_CONTRACT");   
            cmd.addParameter("SUP_MCH_CODE");   
            trans = mgr.perform(trans);

            String supmchname = trans.getValue("VAL6/DATA/SUPMCHNAME");
            mgr.responseWrite(supmchname);
            mgr.endResponse();
        }

        if ("CONTRACT".equals(val))
        {
            cmd = trans.addCustomFunction("COM","Site_API.Get_Company","COMPANY");
            cmd.addParameter("CONTRACT");

            trans = mgr.perform(trans);

            company = trans.getValue("COM/DATA/COMPANY");

            sup_contract = mgr.readValue("CONTRACT");
            mgr.responseWrite(sup_contract);
            mgr.endResponse();
        }
    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void saveReturn()
    {
        ASPManager mgr = getASPManager();

        mch_code = mgr.readValue("MCH_CODE");
        sup_contract = mgr.readValue("SUP_CONTRACT");
        sup_mch_code  = mgr.readValue("SUP_MCH_CODE");

        if (!mgr.isEmpty(sup_contract))
        {
            cmd = trans.addCustomCommand("VALUE1","Equipment_Serial_API.Move_To_Repair");
            cmd.addParameter("CONTRACT");
            cmd.addParameter("MCH_CODE");
            cmd.addParameter("SUP_CONTRACT",mgr.readValue("SUP_CONTRACT"));
            cmd.addParameter("SUP_MCH_CODE",mgr.readValue("SUP_MCH_CODE"));
            cmd.addParameter("NOTE",mgr.readValue("NOTE"));

            cmd = trans.addCustomCommand("VALUE2","Active_Separate_API.Change_To_Repair_Work_Order");
            cmd.addParameter("WO_NO",wo_No);
            trans=mgr.perform(trans);  

            WoTurnYes = "true";

        }

        FinishFlag = "true";
    }


    public void cancel()
    {
        CancelFlag = "true";
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------


    public void newRow()
    {
        ASPManager mgr = getASPManager();

        cmd = trans.addCustomCommand("CON1","Maintenance_Spare_API.Get_Default_Contract");
        cmd.addParameter("CONTRACT");

        cmd = trans.addCustomCommand("CON2","Maintenance_Spare_API.Get_Default_Contract");
        cmd.addParameter("SUP_CONTRACT");

        cmd = trans.addCustomFunction("COM","Site_API.Get_Company","COMPANY");
        cmd.addReference("CONTRACT","CON1/DATA");

        trans = mgr.perform(trans);

        data = trans.getBuffer("HEAD/DATA");
        headset.addRow(data);

        temp = headset.getRow();
        temp.setValue("CONTRACT",trans.getValue("CON1/DATA/CONTRACT"));
        temp.setValue("SUP_CONTRACT",trans.getValue("CON2/DATA/SUP_CONTRACT"));
        temp.setValue("COMPANY",trans.getValue("COM/DATA/COMPANY"));
        headset.setRow(temp);
    }

    public void okFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addEmptyQuery(headblk);
        q.includeMeta("ALL");
        mgr.submit(trans);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWMOVETOREPAIRWORKSHOPDLGNODATA: No data found."));
            headset.clear();
        }
    }

    public void preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setFunction("''");
        f.setHidden();

        //General----------

        f = headblk.addField("MCH_CODE");
        f.setFunction("''");
        f.setSize(26);
        f.setUpperCase();
        f.setDynamicLOV("EQUIPMENT_SERIAL","CONTRACT",600,445);
        f.setLabel("PCMWMOVETOREPAIRWORKSHOPDLGMCH: Object ID");
        f.setCustomValidation("CONTRACT,MCH_CODE,PART_NO", "MCHNAME,PART_NO,SERIAL_NO,CBACCOUNTED");
        f.setMaxLength(100);
        f.setMandatory();
        f.setInsertable();

        f = headblk.addField("CONTRACT");
        f.setFunction("''");
        f.setSize(10);
        f.setUpperCase();
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PCMWMOVETOREPAIRWORKSHOPDLGCON: Site");
        f.setCustomValidation("CONTRACT", "SUP_CONTRACT");
        f.setMaxLength(5);
        f.setMandatory();
        f.setInsertable();

        f = headblk.addField("MCHNAME");
        f.setFunction("''");
        f.setSize(26);
        f.setLabel("PCMWMOVETOREPAIRWORKSHOPDLGMCHN: Object Description");
        f.setReadOnly();
        f.setMaxLength(2000);

        f = headblk.addField("COMPANY");
        f.setFunction("''");
        f.setHidden();  

        f = headblk.addField("PART_NO");
        f.setFunction("''");
        f.setSize(10);
        f.setLabel("PCMWMOVETOREPAIRWORKSHOPDLGPANO: Part No");
        f.setUpperCase();
        f.setReadOnly();
        f.setMaxLength(25);

        f = headblk.addField("INVENTORYVALUE");
        f.setFunction("''");
        f.setSize(12);
        f.setLabel("PCMWMOVETOREPAIRWORKSHOPDLGINVENVALUE: Inventory Value");
        f.setReadOnly();

        f = headblk.addField("CBACCOUNTED");
        f.setLabel("PCMWMOVETOREPAIRWORKSHOPDLGCBACCOUNTED: Accounted");
        f.setFunction("''");
        f.setCheckBox("FALSE,TRUE");
        f.setReadOnly();
        f.setMaxLength(5);

        f = headblk.addField("SERIAL_NO");
        f.setFunction("''");
        f.setSize(10);
        f.setLabel("PCMWMOVETOREPAIRWORKSHOPDLGSENO: Serial No");
        f.setUpperCase(); 
        f.setReadOnly();
        f.setMaxLength(50);

        //Repar Workshop-------------

        f = headblk.addField("SUP_MCH_CODE");
        f.setFunction("''");
        f.setSize(26);
        f.setMandatory();
        f.setDynamicLOV("MAINTENANCE_OBJECT","SUP_CONTRACT CONTRACT",600,445);
        f.setLabel("PCMWMOVETOREPAIRWORKSHOPDLGSUPMCH: Object ID");
        f.setCustomValidation("SUP_CONTRACT, SUP_MCH_CODE", "SUPMCHNAME");
        f.setUpperCase();
        f.setMaxLength(100);
        f.setInsertable();

        f = headblk.addField("SUP_CONTRACT");
        f.setFunction("''");
        f.setSize(10);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PCMWMOVETOREPAIRWORKSHOPDLGSUPCON: Site");
        f.setUpperCase();
        f.setMandatory();
        f.setInsertable();

        f = headblk.addField("SUPMCHNAME");
        f.setFunction("''");
        f.setSize(26);
        f.setLabel("PCMWMOVETOREPAIRWORKSHOPDLGSUPMCHN: Object Description");
        f.setReadOnly();
        f.setMaxLength(2000);

        f = headblk.addField("NOTE");
        f.setFunction("''");
        f.setSize(45);
        f.setLabel("PCMWMOVETOREPAIRWORKSHOPDLGNOTE: Note");
        f.setMaxLength(2000);

        f = headblk.addField("INDEX1");
        f.setFunction("''");
        f.setSize(13);
        f.setHidden();

        f = headblk.addField("ISCLIENTVALUE");
        f.setFunction("''");
        f.setSize(13);
        f.setHidden();

        f = headblk.addField("ISINSURANCEPART");
        f.setFunction("''");
        f.setSize(13);
        f.setHidden();

        f = headblk.addField("WO_NO");
        f.setFunction("''");
        f.setHidden();

        headblk.setView("DUAL");
        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.setBorderLines(false,true);

        headtbl = mgr.newASPTable(headblk);
        headlay = headblk.getASPBlockLayout();

        headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
        headbar.enableCommand(headbar.SAVERETURN);
        headbar.enableCommand(headbar.CANCELNEW); 

        headlay.setEditable();

        headlay.defineGroup(mgr.translate("PCMWMOVETOREPAIRWORKSHOPDLGGRPLABEL1: General"),"MCH_CODE,CONTRACT,MCHNAME,PART_NO,INVENTORYVALUE,CBACCOUNTED,SERIAL_NO",true,true);
        headlay.defineGroup(mgr.translate("PCMWMOVETOREPAIRWORKSHOPDLGGRPLABEL2: Repair Workshop"),"SUP_MCH_CODE,SUP_CONTRACT,SUPMCHNAME,NOTE",true,true);
        headbar.defineCommand(headbar.SAVERETURN,"saveReturn","checkAllFields()");
        headbar.defineCommand(headbar.CANCELNEW,"cancel");

        //-----------------------------------------------------------------------
        //----------------- block used for setcheckbox() funtion ----------------
        //-----------------------------------------------------------------------

        tempblk = mgr.newASPBlock("TEMP");
        f = tempblk.addField("LUNAME");

        enableConvertGettoPost();
    }


//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWMOVETOREPAIRWORKSHOPDLGTITLE: Turn Into Repair Order";
    }

    protected String getTitle()
    {
        return "PCMWMOVETOREPAIRWORKSHOPDLGTITLE: Turn Into Repair Order";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.show());

        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

        appendDirtyJavaScript("function WriteSite(con)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("  form.SUP_CONTRACT.value=con;\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function checkAllFields(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("  return checkHeadFields(i);\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateMchCode(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkMchCode(i) ) return;\n");
        appendDirtyJavaScript(" window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=MCH_CODE'\n");
        appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
        appendDirtyJavaScript("		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("   window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'MCH_CODE',i,'Object ID') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('MCHNAME',i,0);\n");
        appendDirtyJavaScript("		assignValue_('PART_NO',i,1);\n");
        appendDirtyJavaScript("		assignValue_('SERIAL_NO',i,2);\n");
        appendDirtyJavaScript("		assignValue_('CBACCOUNTED',i,3);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	if (getValue_('CBACCOUNTED',i) == \"TRUE\")\n");
        appendDirtyJavaScript("		f.CBACCOUNTED.click();\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(CancelFlag);
        appendDirtyJavaScript("'=='true')\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("    self.close();\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(FinishFlag);
        appendDirtyJavaScript("'=='true')\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("    if ('");
        appendDirtyJavaScript(WoTurnYes);
        appendDirtyJavaScript("'=='true')\n");
        appendDirtyJavaScript("    {	\n");
        appendDirtyJavaScript("       alert('");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWMOVETOREPAIRWORKSHOPDLGWOTURNYES: Work order turn to repair work order"));
        appendDirtyJavaScript("');\n");
        appendDirtyJavaScript("       if ('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(actSep2));		//XSS_Safe AMNILK 20070718
        appendDirtyJavaScript("' == \"TRUE\" )\n");
        appendDirtyJavaScript("       {\n");
        appendDirtyJavaScript("       	  jwono = '");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(wo_No));		//XSS_Safe AMNILK 20070718	
        appendDirtyJavaScript("'; 	\n");
        appendDirtyJavaScript("       	  self.close(); \n");
        appendDirtyJavaScript("          window.open(\"ActiveSeparate2.page?WO_NO=\"+URLClientEncode(jwono),'");

        appendDirtyJavaScript(mgr.encodeStringForJavascript(frmname)); 		//XSS_Safe AMNILK 20070718
        appendDirtyJavaScript("',\"\"); \n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript("       if ('");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(actSep2SM));        //XSS_Safe AMNILK 20070718    	
        appendDirtyJavaScript("' == \"TRUE\" )\n");
        appendDirtyJavaScript("       {\n");
        appendDirtyJavaScript("          jwono = '");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(wo_No));		//XSS_Safe AMNILK 20070718
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("          self.close();  \n");
        appendDirtyJavaScript("          window.open(\"ActiveSeparate2ServiceManagement.page?WO_NO=\"+URLClientEncode(jwono),'");

        appendDirtyJavaScript(mgr.encodeStringForJavascript(frmname));	  	//XSS_Safe AMNILK 20070718
        appendDirtyJavaScript("',\"\");\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("    else\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("       alert('");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWMOVETOREPAIRWORKSHOPDLGWOTURNNO: No repair work order was created"));
        appendDirtyJavaScript("');\n");
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("}\n");
    }
}
