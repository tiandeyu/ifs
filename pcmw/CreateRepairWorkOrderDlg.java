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
*  File        : CreateRepairWorkOrderDlg.java 
*  Created     : ASP2JAVA Tool  010308
*  Modified    :     
*  CHCRLK  010613  Modified overwritten validations. 
*  CHCRLK  010814  Modified validation for REG_DATE.
*  JEWILK  010828  Modified to open in a new window when called from the navigator.
*  VAGULK  011017  Set the value for Reported by  be fetched from the logged on user(70641)
*  SHAFLK  020614  Bug Id 30785,Changed Lov for Object Id in General section.
*  JEJALK  021204  Takeoff Beta modification.
*  ARWILK  031218  Edge Developments - (Removed clone and doReset Methods)
* -------------------------------- Edge - SP1 Merge -------------------------
*  SHAFLK  040119  Bug Id 41815,Removed Java dependencies. 
*  THWILK  040325  Merge with SP1.
*  ARWILK  040720  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  NAMELK  041104  Non Standard Translation Tags Corrected.
*  NEKOLK  040405  Merged - Bug ID 49482, Modified saveReturn() .
*  SHAFLK  050425  Bug 50830, Used mgr.translateJavaScript() method to get the correctly translated strings in JavaScript functions.
*  NIJALK  050617  Merged bug 50830. 
*  NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile).
*  NIJALK  060303  Bug 136150: Modified printContents().
*  SULILK  060321  Call 135197: Modified intVali(),preDefine(),validate().
*  JEWILK  060803  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged Bug Id: 58214.
*  AMDILK  060918  Modified preDefine(), removed the comment line in the "REPORTED_BY"
*  ILSOLK  070425  Modified for Call ID 142870,142873.
*  ILSOLK  070426  Modified for Call ID 143038. 
*  AMDILK  070509  Call Id 143041: Mdified saveReturn()
*  ASSALK  070604  Call ID 145308. Modified validate(),preDefine(),printContents().
*  ILSOLK  070710  Eliminated XSS.
*  AMNILK  070713  Eliminated SQL Injection.
*  AMDILK  070830  Call id 147714: Included work order contract to the dialog
*  SHAFLK  071227  Bug 70214, Modified preDefine().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class CreateRepairWorkOrderDlg extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pladew.CreateRepairWorkOrderDlg");

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
    private boolean myFlag;
    private String flag2;
    private String mchc;
    private String cont;
    private String tr_Contract;
    private String tr_MchCode;
    private String tr_mchName;
    private String cbacc;
    private ASPTransactionBuffer trans;
    private String url;
    private String CancelFlag;
    private String FinishFlag;
    private String msg1;
    private String msg2;
    private ASPBuffer buff1;
    private ASPBuffer row1;
    private String contract;
    private ASPTransactionBuffer secBuff;
    private ASPCommand cmd;
    private String mchname;
    private String part_no;
    private String serial_no;
    private String regdate;
    private String company;
    private String isClient;
    private String isinsur;
    private String invVal;
    private boolean flag;
    private String cbaccounted;
    private ASPBuffer row;
    private String val;
    private String mch;
    private String part;
    private String serial;
    private String invenVal;
    private String orgdescr;
    private String rep_by_id;
    private String mch_code_contract;
    private String supmchname;
    private ASPBuffer buf;
    private String finish;
    private String regdate_;
    private String myValue;
    private String mch_code;
    private String dateValue;
    private String errdescr;
    private String org_code;
    private String reported_by;
    private String sup_mch_code;
    private String wono;
    private ASPQuery q;
    private ASPBuffer data;
    private ASPBuffer temp;
    private String title;
    private String date_mask;
    private boolean newWndFlag;

    //================================================================================
    // ASP2JAVA WARNING: Types of the following variables are not identified by
    // the ASP2JAVA converter. Define them and remove these comments.
    //================================================================================
    private String isSecure[];
    private String msk;
    private String txt;
    private String txt1;
    private double leadtime;
    private String dateMask;
    private String sel;

    //===============================================================
    // Construction 
    //===============================================================
    public CreateRepairWorkOrderDlg(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        myFlag = false;
        flag2 =  "";
        mchc =  "";
        cont =  "";
        tr_Contract =  "";
        tr_MchCode =  "";
        tr_mchName =  "";
        cbacc = "";

        ASPManager mgr = getASPManager();

        trans = mgr.newASPTransactionBuffer();
        ctx = mgr.getASPContext();
        url = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");

        myFlag = ctx.readFlag("MYFLAG",false);

        tr_MchCode = ctx.readValue("MCHCODE",tr_MchCode);
        tr_Contract = ctx.readValue("CON",tr_Contract);
        tr_mchName =  ctx.readValue("MCHNAME",tr_mchName);
        url = ctx.readValue("URL",url);

        msk = mgr.getFormatMask("Datetime",true);

        CancelFlag = ""; 
        FinishFlag = "";
        msg1 = "";
        msg2 = "";

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (mgr.dataTransfered())
        {
            myFlag = true;
            okFind();
            buff1 = mgr.getTransferedData();
            row1 = buff1.getBufferAt(0); 
            tr_MchCode = row1.getValue("MCH_CODE");
            row1 = buff1.getBufferAt(1); 
            tr_Contract = row1.getValue("MCH_CODE_CONTRACT");
            contract=tr_Contract;
            mchc = tr_MchCode;
            cont = tr_Contract;
            intVali(); 
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("NEWWNDFLAG")))
        {
            newWndFlag = true;
        }
        else
            newRow();

        adjust();

        ctx.writeValue("MCHCODE",tr_MchCode);
        ctx.writeValue("CON",tr_Contract);
        ctx.writeValue("MCHNAME",tr_mchName);
        ctx.writeFlag("MYFLAG",myFlag);
        ctx.writeValue("URL",url);   
    }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
// This function is used to check security status for methods which
// are in modules Order,Purch and Invent .This function is called before
// the required method is performed.  -NUPE,21-07-2000

    public boolean  checksec( String method,int ref,String[]isSecure1)
    {
        ASPManager mgr = getASPManager();

        isSecure1[ref] = "false" ;
        String splitted[] = split(method,".");

        String first = splitted[0].toString();
        String Second = splitted[1].toString();

        secBuff = mgr.newASPTransactionBuffer();
        secBuff.addSecurityQuery(first,Second);
        secBuff = mgr.perform(secBuff);
        if (secBuff.getSecurityInfo().itemExists(method))
        {
            isSecure1[ref] = "true";
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
        int ref;
        ASPManager mgr = getASPManager();
        isSecure = new String[7];

        trans.clear();

        cmd = trans.addCustomCommand("CON1","Maintenance_Spare_API.Get_Default_Contract");
        cmd.addParameter("CONTRACT");

	cmd = trans.addCustomCommand("CON3","Maintenance_Spare_API.Get_Default_Contract");
        cmd.addParameter("WO_CONTRACT");

        cmd = trans.addCustomFunction("DAT","Maintenance_Site_Utility_API.Get_Site_Date","REG_DATE");
        cmd.addReference("CONTRACT","CON1/DATA");

        cmd = trans.addCustomFunction("COM","Site_API.Get_Company","COMPANY");
        cmd.addReference("CONTRACT","CON1/DATA");

        cmd = trans.addCustomFunction( "OBJDES", "Equipment_Serial_API.Get_Mch_Name", "MCHNAME" );
        cmd.addParameter("CONTRACT",tr_Contract);
        cmd.addParameter("MCH_CODE",tr_MchCode);

        cmd = trans.addCustomFunction( "PARTNO", "Equipment_Serial_API.Get_Part_No", "PART_NO" );
        cmd.addParameter("CONTRACT",tr_Contract);
        cmd.addParameter("MCH_CODE",tr_MchCode);

        cmd = trans.addCustomFunction( "SERIALNO", "Equipment_Serial_API.Get_Serial_No", "SERIAL_NO" );
        cmd.addParameter("CONTRACT",tr_Contract);
        cmd.addParameter("MCH_CODE",tr_MchCode);

        if (checksec("Inventory_Part_API.Get_Zero_Cost_Flag",1,isSecure))
        {
            cmd = trans.addCustomFunction( "VAL2", "Inventory_Part_API.Get_Zero_Cost_Flag", "ISCLIENTVALUE" );
            cmd.addParameter("CONTRACT",tr_Contract); 
            cmd.addReference("PART_NO","PARTNO/DATA");
        }
        if (checksec("Inventory_Part_Zero_Cost_API.Get_client_value",2,isSecure))
        {
            cmd = trans.addCustomFunction( "VAL3", "Inventory_Part_Zero_Cost_API.Get_client_value", "ISINSURANCEPART" );
            cmd.addParameter("INDEX1");
        }
        /*if (checksec("Inventory_Part_API.Get_Inventory_Value_By_Method",3,isSecure))
        {
            cmd = trans.addCustomFunction( "INVVAL", "Inventory_Part_API.Get_Inventory_Value_By_Method", "INVENTORYVALUE" ); 
            cmd.addParameter("CONTRACT",tr_Contract);
            cmd.addReference("PART_NO","PARTNO/DATA");
        }*/

        if (checksec("Active_Separate_API.Get_Inventory_Value",3,isSecure))
        {
            cmd = trans.addCustomCommand("INVVAL","Active_Separate_API.Get_Inventory_Value");
            cmd.addParameter("INVENTORYVALUE");
            cmd.addParameter("CONTRACT",tr_Contract);
            cmd.addReference("PART_NO","PARTNO/DATA");
            cmd.addReference("SERIAL_NO","SERIALNO/DATA");
            //cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
            cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
            cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));
        }

        trans=mgr.perform(trans);

        mchname = trans.getValue("OBJDES/DATA/MCHNAME");
        part_no = trans.getValue("PARTNO/DATA/PART_NO");
        serial_no = trans.getValue("SERIALNO/DATA/SERIAL_NO");
        regdate = trans.getValue("DAT/DATA/REG_DATE");
        company = trans.getValue("COM/DATA/COMPANY");

        ref = 0;


        if (isSecure[ref += 1] =="true")
            isClient = trans.getValue("VAL2/DATA/ISCLIENTVALUE");
        else
            isClient = "";

        if (isSecure[ref += 1] =="true")
            isinsur = trans.getValue("VAL3/DATA/ISINSURANCEPART");
        else
            isinsur = "";

        if (isSecure[ref += 1] =="true")
            invVal = trans.getValue("INVVAL/DATA/INVENTORYVALUE");
        else
            invVal = "";

        flag = false;


        if (!mgr.isEmpty (isinsur) && !mgr.isEmpty (isClient))
        {
            if (isinsur.equals(isClient))
                flag = true;
        }

        if (flag)
            cbaccounted="checked";

        row = headset.getRow();
        row.setValue("MCH_CODE_CONTRACT",tr_Contract);
        row.setValue("CONTRACT",tr_Contract);
	row.setValue("WO_CONTRACT", tr_Contract);
        row.setValue("MCH_CODE",tr_MchCode);
        row.setValue("PART_NO",part_no);
        row.setValue("SERIAL_NO",serial_no);
        row.setValue("MCHNAME",mchname);
        row.setValue("COMPANY",company);
        row.setValue("INVENTORYVALUE",invVal);       
        headset.setRow(row);
    }

    public void validate()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        val = mgr.readValue("VALIDATE");

        if ("MCH_CODE".equals(val))
        {
            cmd = trans.addCustomFunction( "OBJDES", "Equipment_Serial_API.Get_Mch_Name", "MCHNAME" );
            cmd.addParameter("MCH_CODE_CONTRACT");
            cmd.addParameter("MCH_CODE");

            cmd = trans.addCustomFunction( "PARTNO", "Equipment_Serial_API.Get_Part_No", "PART_NO" );
            cmd.addParameter("MCH_CODE_CONTRACT");
            cmd.addParameter("MCH_CODE");

            cmd = trans.addCustomFunction( "SERIALNO", "Equipment_Serial_API.Get_Serial_No", "SERIAL_NO" );
            cmd.addParameter("MCH_CODE_CONTRACT");
            cmd.addParameter("MCH_CODE");

            cmd = trans.addCustomFunction( "VAL2", "Inventory_Part_API.Get_Zero_Cost_Flag", "ISCLIENTVALUE" );
            cmd.addParameter("MCH_CODE_CONTRACT"); 
            cmd.addReference("PART_NO","PARTNO/DATA"); 

            cmd = trans.addCustomFunction( "VAL3", "Inventory_Part_Zero_Cost_API.Get_client_value", "ISINSURANCEPART" );
            cmd.addParameter("INDEX1","2");

            /*cmd = trans.addCustomFunction( "INVVAL", "Inventory_Part_API.Get_Inventory_Value_By_Method", "INVENTORYVALUE" ); 
            cmd.addParameter("MCH_CODE_CONTRACT");
            cmd.addReference("PART_NO","PARTNO/DATA");  */

            cmd = trans.addCustomCommand("INVVAL","Active_Separate_API.Get_Inventory_Value");
            cmd.addParameter("INVENTORYVALUE");
            cmd.addParameter("MCH_CODE_CONTRACT");
            cmd.addReference("PART_NO","PARTNO/DATA");
            cmd.addReference("SERIAL_NO","SERIALNO/DATA");
            //cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
            cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
            cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));

            trans = mgr.validate(trans);

            mch = trans.getValue("OBJDES/DATA/MCHNAME");
            part = trans.getValue("PARTNO/DATA/PART_NO");
            serial = trans.getValue("SERIALNO/DATA/SERIAL_NO");


            isClient = trans.getValue("VAL2/DATA/ISCLIENTVALUE");
            isinsur = trans.getValue("VAL3/DATA/ISINSURANCEPART");
            invenVal = trans.getValue("INVVAL/DATA/INVENTORYVALUE");

            if (isinsur.equals(isClient))
                cbacc="TRUE";

            txt=(((mch==null)?"":mch)+"^"+((part==null)?"":part)+"^"+((serial==null)?"":serial)+"^"+((cbacc==null)?"":cbacc)+"^"+((invenVal==null)?"":invenVal))+"^";

            mgr.responseWrite(txt);
        }
        else if ("ORG_CODE".equals(val))
        {
            cmd = trans.addCustomFunction( "VAL4","Organization_API.Get_Description","ORGDESCR");
            cmd.addParameter("WO_CONTRACT");
            cmd.addParameter("ORG_CODE"); 
            trans = mgr.perform(trans);

            orgdescr = trans.getValue("VAL4/DATA/ORGDESCR");
            txt1=((orgdescr==null)?"":orgdescr);
            mgr.responseWrite(orgdescr);
        }
        else if ("REPORTED_BY".equals(val))
        {
            cmd = trans.addCustomFunction( "VAL5", "Company_Emp_API.Get_Max_Employee_Id","REPORTED_BY_ID");
            cmd.addParameter("COMPANY");  
            cmd.addParameter("REPORTED_BY"); 
            trans = mgr.perform(trans);

            rep_by_id = trans.getValue("VAL5/DATA/REPORTED_BY_ID");
            mgr.responseWrite(rep_by_id);
        }
        else if ("SUP_MCH_CODE".equals(val))
        {

            cmd = trans.addCustomFunction("CON","EQUIPMENT_OBJECT_API.Get_Contract","CONTRACT");
            cmd.addParameter("SUP_MCH_CODE",mgr.readValue("SUP_MCH_CODE"));

            cmd = trans.addCustomFunction( "VAL6", "Maintenance_Object_API.Get_Mch_Name","SUPMCHNAME");
            cmd.addParameter("CONTRACT");   
            cmd.addParameter("SUP_MCH_CODE");  

            trans = mgr.perform(trans);

            contract = trans.getValue("CON/DATA/CONTRACT");
            supmchname = trans.getValue("VAL6/DATA/SUPMCHNAME");
            mgr.responseWrite(supmchname);

            txt = (mgr.isEmpty(contract) ? "" : (contract))+ "^" + (mgr.isEmpty(supmchname) ? "" : (supmchname))+ "^";
            mgr.responseWrite(txt); 
        }
        else if ("CONTRACT".equals(val))
        {
            cmd = trans.addCustomFunction( "VAL7", "Maintenance_Object_API.Get_Mch_Name","SUPMCHNAME");
            cmd.addParameter("CONTRACT");   
            cmd.addParameter("SUP_MCH_CODE"); 

            cmd = trans.addCustomFunction("COM","Site_API.Get_Company","COMPANY");
            cmd.addParameter("CONTRACT");

            trans = mgr.perform(trans);

            supmchname = trans.getValue("VAL7/DATA/SUPMCHNAME");
            company = trans.getValue("COM/DATA/COMPANY");

            txt = (mgr.isEmpty(supmchname) ? "" : (supmchname))+ "^" + (mgr.isEmpty(company) ? "" : (company))+ "^";         
            mgr.responseWrite(txt);
        }
        else if ("MCH_CODE_CONTRACT".equals(val))
        {
            cmd = trans.addCustomFunction( "OBJDES", "Equipment_Serial_API.Get_Mch_Name", "MCHNAME" );
            cmd.addParameter("MCH_CODE_CONTRACT");
            cmd.addParameter("MCH_CODE");

            cmd = trans.addCustomFunction( "PARTNO", "Equipment_Serial_API.Get_Part_No", "PART_NO" );
            cmd.addParameter("MCH_CODE_CONTRACT");
            cmd.addParameter("MCH_CODE");

            cmd = trans.addCustomFunction( "SERIALNO", "Equipment_Serial_API.Get_Serial_No", "SERIAL_NO" );
            cmd.addParameter("MCH_CODE_CONTRACT");
            cmd.addParameter("MCH_CODE");

            /*cmd = trans.addCustomFunction( "INVVAL", "Inventory_Part_API.Get_Inventory_Value_By_Method","INVENTORYVALUE");
            cmd.addParameter("MCH_CODE_CONTRACT");   
            cmd.addReference("PART_NO","PARTNO/DATA"); */

            cmd = trans.addCustomCommand("INVVAL","Active_Separate_API.Get_Inventory_Value");
            cmd.addParameter("INVENTORYVALUE");
            cmd.addParameter("MCH_CODE_CONTRACT");
            cmd.addReference("PART_NO","PARTNO/DATA");
            cmd.addReference("SERIAL_NO","SERIALNO/DATA");
            //cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
            cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
            cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));

            trans = mgr.perform(trans);

            mch = trans.getValue("OBJDES/DATA/MCHNAME");
            part = trans.getValue("PARTNO/DATA/PART_NO");
            serial = trans.getValue("SERIALNO/DATA/SERIAL_NO");
            invenVal = trans.getValue("INVVAL/DATA/INVENTORYVALUE");

            contract = mgr.readValue("MCH_CODE_CONTRACT");

            txt = (mgr.isEmpty(contract) ? "" : (contract))+ "^" + (mgr.isEmpty(mch) ? "" : (mch))+ "^" + (mgr.isEmpty(part) ? "" : (part))+ "^" + (mgr.isEmpty(serial) ? "" : (serial))+ "^" + (mgr.isEmpty(invenVal) ? "" : (invenVal))+ "^"; 
            mgr.responseWrite(txt);
        }
        else if ("PLAN_F_DATE".equals(val))
        {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("PLAN_F_DATE",mgr.readValue("PLAN_F_DATE"));
            boolean isErr = false;
            if (!mgr.isEmpty(mgr.readValue("REG_DATE"))) {
                trans.clear();
                ASPQuery q = trans.addQuery("GETDATE","SELECT 'TRUE' DUMMY_RESULT FROM DUAL WHERE ? > ?");
                q.addParameter("PLAN_F_DATE",mgr.readValue("PLAN_F_DATE"));
                q.addParameter("REG_DATE",mgr.readValue("REG_DATE"));

                trans = mgr.perform(trans);
                String result = trans.getValue("GETDATE/DATA/DUMMY_RESULT");

                if (!"TRUE".equals(result)) {
                    txt = "No_Data_Found" + mgr.translate("PCMWPLANEDDATE: Planned Completion is earlier than Planned Start.");
                    isErr = true;
                }
            }

            if (!isErr )
                txt = mgr.readValue("PLAN_F_DATE") +"^";
            mgr.responseWrite(txt); 
        }
        else if ("REG_DATE".equals(val))
        {
            if (!mgr.isEmpty(mgr.readValue("PLAN_F_DATE")))
                finish = mgr.readValue("PLAN_F_DATE");
            else
                finish = "";   

            buf = mgr.newASPBuffer();
            buf.addFieldItem("REG_DATE",mgr.readValue("REG_DATE"));
            regdate_ = buf.getFieldValue("REG_DATE");
            boolean isErr = false;

            if (mgr.isEmpty(mgr.readValue("PLAN_F_DATE")))
            {

                cmd = trans.addCustomFunction("LEAD","Inventory_Part_API.Get_Purch_Leadtime","LEAD_TIME");
                cmd.addParameter("CONTRACT");
                cmd.addParameter("PART_NO");

                trans = mgr.perform(trans);

                leadtime = trans.getNumberValue("LEAD/DATA/LEAD_TIME");
                if (isNaN(leadtime))
                    leadtime = 0.0;

                trans.clear();

                String dateMask = mgr.readValue("DATE_MASK");

                if (!mgr.isEmpty(mgr.readValue("REG_DATE")))
                {
		    // SQLInjection_Safe AMNILK 20070713

		    sel = "select to_date( ? , ? ) + ? PLAN_F_DATE from DUAL";

		    q = trans.addQuery("XX",sel);   
		    q.addParameter("DUMMY_RESULT",mgr.readValue("REG_DATE"));
		    q.addParameter("DUMY",dateMask);
		    q.addParameter("LEAD_TIME",new Double(leadtime).toString());

    
		
                    trans = mgr.perform(trans);  
                    finish = trans.getBuffer("XX/DATA").getFieldValue("PLAN_F_DATE");                                                                                                          
                }
            }
            else
            {
                trans.clear();
                ASPQuery q = trans.addQuery("GETDATE","SELECT 'TRUE' DUMMY_RESULT FROM DUAL WHERE ? > ?");
                q.addParameter("PLAN_F_DATE",mgr.readValue("PLAN_F_DATE"));
                q.addParameter("REG_DATE",mgr.readValue("REG_DATE"));

                trans = mgr.perform(trans);
                String result = trans.getValue("GETDATE/DATA/DUMMY_RESULT");

                if (!"TRUE".equals(result)) {
                    txt = "No_Data_Found" + mgr.translate("PCMWPLANEDDATE: Planned Completion is earlier than Planned Start.");
                    isErr = true;
                }
            }

            if (!isErr )
                txt = regdate_ +"^";//+ finish +"^";                                                                                                                                          

            mgr.responseWrite(txt);
        }
	else if ("WO_CONTRACT".equals(val))
        {
            trans.clear();

	    cmd = trans.addCustomFunction("WOCOM","Site_API.Get_Company","COMPANY");
            cmd.addParameter("WO_CONTRACT");

            trans = mgr.perform(trans);

            company = trans.getValue("WOCOM/DATA/COMPANY");

            txt = (mgr.isEmpty(company) ? "" : (company))+ "^";         
            mgr.responseWrite(txt);
        }

        mgr.endResponse();      
    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void saveReturn()
    {
        ASPManager mgr = getASPManager();

        flag2 = "0";

        myValue = mgr.readValue("PLAN_F_DATE");
        mch_code = mgr.readValue("MCH_CODE");
        dateValue = mgr.readValue("REG_DATE");
        errdescr  = mgr.readValue("ERRDESCR");
        org_code= mgr.readValue("ORG_CODE");
        reported_by = mgr.readValue("REPORTED_BY");
        contract = mgr.readValue("CONTRACT");
        sup_mch_code  = mgr.readValue("SUP_MCH_CODE");

        cmd = trans.addCustomCommand("VALUE","Active_Separate_API.Create_Work_Order");
        cmd.addParameter("WO_NO");
        cmd.addParameter("WO_CONTRACT");
        cmd.addParameter("MCH_CODE");
        cmd.addParameter("ERRDESCR");
        cmd.addParameter("ORG_CODE");
        cmd.addParameter("REG_DATE");
        cmd.addParameter("PLAN_F_DATE",mgr.readValue("PLAN_F_DATE"));
        cmd.addParameter("REPORTED_BY_ID");
        cmd.addParameter("MCH_CODE_CONTRACT");


        cmd = trans.addCustomCommand("VALUE1","Equipment_Serial_API.Move_To_Repair");
        cmd.addParameter("MCH_CODE_CONTRACT");
        cmd.addParameter("MCH_CODE");
        cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
        cmd.addParameter("SUP_MCH_CODE",mgr.readValue("SUP_MCH_CODE"));
        cmd.addParameter("NOTE",mgr.readValue("NOTE"));

        trans=mgr.perform(trans);  
        wono = trans.getValue("VALUE/DATA/WO_NO");
        trans.clear();

        cmd = trans.addCustomCommand("VALUE2","Active_Separate_API.Change_To_Repair_Work_Order");
        cmd.addParameter("WO_NO",wono);
        trans=mgr.perform(trans);  

        trans.clear();

        flag2 = "1";


        if ("0".equals(flag2))
            msg1 = mgr.translate("PLADEWCREATEREPAIRWORKORDERDLGNOTCREATED: No Work Order has been created");
        else
            msg2 = mgr.translate("PLADEWCREATEREPAIRWORKORDERDLGCREATED: Work Order has been created (&1 - &2)",wono,mgr.readValue("ERRDESCR"));

        trans.clear();
        FinishFlag = "true";
    }


    public void cancel() 
    {
        ASPManager mgr = getASPManager();

        if (myFlag)
        {
            CancelFlag = "true";
        }
        else
        {
            mgr.redirectTo("../Default.page");
            headtbl.clearQueryRow();
        }
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void countFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();
    }

    public void newRow()
    {
        ASPManager mgr = getASPManager();


        cmd = trans.addCustomCommand("CON1","Maintenance_Spare_API.Get_Default_Contract");
        cmd.addParameter("MCH_CODE_CONTRACT");

        cmd = trans.addCustomCommand("CON2","Maintenance_Spare_API.Get_Default_Contract");
        cmd.addParameter("CONTRACT");

        cmd = trans.addCustomCommand("CON3","Maintenance_Spare_API.Get_Default_Contract");
        cmd.addParameter("WO_CONTRACT");
    
        cmd = trans.addCustomFunction("DAT","Maintenance_Site_Utility_API.Get_Site_Date","REG_DATE");
        cmd.addReference("MCH_CODE_CONTRACT","CON1/DATA");

        cmd = trans.addCustomFunction("COM","Site_API.Get_Company","COMPANY");
        cmd.addReference("MCH_CODE_CONTRACT","CON1/DATA");

        cmd = trans.addCustomFunction("GETUSER","Fnd_Session_API.Get_Fnd_User","FND_USER");

        cmd = trans.addCustomFunction("GETIDUSER","Person_Info_API.Get_Id_For_User","REPORTED_BY");
        cmd.addReference("FND_USER","GETUSER/DATA");

        cmd = trans.addCustomFunction("GETMAXIDUSER","Company_Emp_API.Get_Max_Employee_Id","REPORTED_BY_ID");
        cmd.addReference("COMPANY","COM/DATA");      
        cmd.addReference("REPORTED_BY","GETIDUSER/DATA");

        trans = mgr.perform(trans);

        data = trans.getBuffer("HEAD/DATA");
        headset.addRow(data);


        temp = headset.getRow();
        temp.setValue("MCH_CODE_CONTRACT",trans.getValue("CON1/DATA/MCH_CODE_CONTRACT"));
        temp.setValue("CONTRACT",trans.getValue("CON2/DATA/CONTRACT"));
        temp.setValue("WO_CONTRACT",trans.getValue("CON3/DATA/WO_CONTRACT"));
        temp.setValue("COMPANY",trans.getValue("COM/DATA/COMPANY"));
        temp.setValue("REPORTED_BY",trans.getValue("GETIDUSER/DATA/REPORTED_BY"));
        temp.setValue("REPORTED_BY_ID",trans.getValue("GETMAXIDUSER/DATA/REPORTED_BY_ID"));
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
            mgr.showAlert(mgr.translate("PLADEWCREATEREPAIRWORKORDERDLGNODATA: No data found."));
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
        f.setLOV("../equipw/EquipmentSerialLov.page","CONTRACT",600,450);   
        f.setLabel("PLADEWCREATEREPAIRWORKORDERDLGMCH: Object ID");
        f.setCustomValidation("MCH_CODE_CONTRACT,MCH_CODE,PART_NO", "MCHNAME,PART_NO,SERIAL_NO,CBACCOUNTED,INVENTORYVALUE");
        f.setMaxLength(100);
        f.setMandatory();
        f.setInsertable();

        f = headblk.addField("MCH_CODE_CONTRACT");
        f.setFunction("''");
        f.setSize(10);
        f.setUpperCase();
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PLADEWCREATEREPAIRWORKORDERDLGMCHCODECON: Site");
        f.setCustomValidation("MCH_CODE_CONTRACT,MCH_CODE,PART_NO", "CONTRACT,MCHNAME,PART_NO,SERIAL_NO,INVENTORYVALUE");
        f.setMaxLength(5);
        f.setMandatory();
        f.setInsertable();

        f = headblk.addField("MCHNAME");
        f.setFunction("''");
        f.setSize(26);
        f.setLabel("PLADEWCREATEREPAIRWORKORDERDLGMCHN: Object Description");
        f.setReadOnly();
        f.setMaxLength(2000);

        f = headblk.addField("COMPANY");
        f.setFunction("''");
        f.setHidden();


        f = headblk.addField("PART_NO");
        f.setFunction("''");
        f.setSize(10);
        f.setLabel("PLADEWCREATEREPAIRWORKORDERDLGPANO: Part No");
        f.setUpperCase();
        f.setReadOnly();
        f.setMaxLength(25);

        f = headblk.addField("INVENTORYVALUE");
        f.setFunction("''");
        f.setSize(12);
        f.setLabel("PLADEWCREATEREPAIRWORKORDERDLGINVENVALUE: Inventory Value");
        f.setReadOnly();

        f = headblk.addField("CBACCOUNTED");
        f.setLabel("PLADEWCREATEREPAIRWORKORDERDLGCBACCOUNTED: Accounted");
        f.setFunction("''");
        f.setCheckBox("FALSE,TRUE");
        f.setReadOnly();
        f.setMaxLength(5);

        f = headblk.addField("SERIAL_NO");
        f.setFunction("''");
        f.setSize(10);
        f.setLabel("PLADEWCREATEREPAIRWORKORDERDLGSENO: Serial No");
        f.setUpperCase(); 
        f.setReadOnly();
        f.setMaxLength(50);

        //Repar Workshop-------------

        f = headblk.addField("SUP_MCH_CODE");
        f.setFunction("''");
        f.setSize(26);
        f.setMandatory();
        f.setDynamicLOV("MAINTENANCE_OBJECT","CONTRACT",600,445);
        f.setLabel("PLADEWCREATEREPAIRWORKORDERDLGSUPMCH: Object ID");
        f.setCustomValidation("CONTRACT, SUP_MCH_CODE", "SUPMCHNAME");
        f.setUpperCase();
        f.setMaxLength(100);
        f.setInsertable();

        f = headblk.addField("CONTRACT");
        f.setFunction("''");
        f.setSize(10);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PLADEWCREATEREPAIRWORKORDERDLGCON: Site");
        f.setCustomValidation("CONTRACT,SUP_MCH_CODE", "SUPMCHNAME,COMPANY");
        f.setUpperCase();
        f.setMandatory();
        f.setInsertable();

        f = headblk.addField("SUPMCHNAME");
        f.setFunction("''");
        f.setSize(26);
        f.setLabel("PLADEWCREATEREPAIRWORKORDERDLGSUPMCHN: Object Description");
        f.setReadOnly();
        f.setMaxLength(2000);

        f = headblk.addField("NOTE");
        f.setFunction("''");
        f.setSize(45);
        f.setLabel("PLADEWCREATEREPAIRWORKORDERDLGNOTE: Note");
        f.setMaxLength(2000);

        //Work Order Information----------------

        f = headblk.addField("REG_DATE","Datetime");
        f.setFunction("''");
        f.setSize(22);
        f.setLabel("PLADEWCREATEREPAIRWORKORDERDLGRDATE: Planned Start");
        f.setMandatory();
        f.setCustomValidation("REG_DATE,CONTRACT,PART_NO,PLAN_F_DATE","REG_DATE");

        f = headblk.addField("PLAN_F_DATE","Datetime");
        f.setFunction("''");
        f.setSize(22);
        f.setMandatory();
        f.setLabel("PLADEWCREATEREPAIRWORKORDERDLGPLFDATE: Planned Completion");
        f.setCustomValidation("PLAN_F_DATE,REG_DATE","PLAN_F_DATE");  

        f = headblk.addField("ERRDESCR");
        f.setFunction("''");
        f.setSize(22);
        f.setMaxLength(60);
        f.setLabel("PLADEWCREATEREPAIRWORKORDERDLGERR: Directive");
        f.setMandatory();

        f = headblk.addField("WO_CONTRACT");
        f.setFunction("''");
        f.setSize(10);
        f.setLabel("PLADEWCREATEREPAIRWORKORDERDLGWOCONTRACT: Site");
	f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setCustomValidation("WO_CONTRACT", "COMPANY");
        f.setMandatory();
	f.setInsertable();

        f = headblk.addField("ORG_CODE");
        f.setFunction("''");
        f.setSize(13);
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","WO_CONTRACT CONTRACT",600,445);
        f.setLabel("PLADEWCREATEREPAIRWORKORDERDLGORGCODE: Maintenance Organization");
        f.setCustomValidation("WO_CONTRACT,ORG_CODE","ORGDESCR");
        f.setMandatory();
        f.setUpperCase();
        f.setInsertable();

        f = headblk.addField("ORGDESCR");
        f.setFunction("''");
        f.setSize(31);
        f.setReadOnly();

        f = headblk.addField("REPORTED_BY");
        f.setSize(22);
        f.setFunction("''");
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445);
        f.setLabel("PLADEWCREATEREPAIRWORKORDERDLGREPBY: Reported by");
        f.setCustomValidation("COMPANY,REPORTED_BY", "REPORTED_BY_ID");
        f.setMandatory();
        f.setUpperCase();
        f.setInsertable();

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

        f = headblk.addField("TO_CONTRACT");
        f.setFunction("''");
        f.setSize(45);
        f.setHidden();

        f = headblk.addField("TO_MCH_CODE");
        f.setFunction("''");
        f.setSize(45);
        f.setHidden();

        f = headblk.addField("CMNT");
        f.setFunction("''");
        f.setSize(45);
        f.setHidden();

        f = headblk.addField("WO_NO");
        f.setFunction("''");
        f.setSize(8);
        f.setHidden();

        f = headblk.addField("REPORTED_BY_ID");
        f.setFunction("''");
        f.setSize(13);
        f.setHidden();
        f.setUpperCase();
        f.setMaxLength(11);

        f = headblk.addField("PERSON_ID");
        f.setFunction("''");
        f.setSize(10);
        f.setHidden();

        f = headblk.addField("DUMY");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("FND_USER");
        f.setFunction("''");   
        f.setHidden();

        f = headblk.addField("HEAD_TEMP");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("LEAD_TIME");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("CONFIGURATION_ID");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("CONDITION_CODE");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("DUMMY_RESULT");
        f.setFunction("''");
        f.setHidden();

        headblk.setView("DUAL");
        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.setBorderLines(false,true);


        headtbl = mgr.newASPTable(headblk);
        headlay = headblk.getASPBlockLayout();

        headlay.setDefaultLayoutMode(headlay.EDIT_LAYOUT);
        headlay.setSimple("ORGDESCR");
        headbar.enableCommand(headbar.SAVERETURN);
        headbar.enableCommand(headbar.CANCELNEW); 

        headlay.setEditable();
        headlay.setColumnWidth(1,120,200);

        headlay.defineGroup(mgr.translate("PLADEWCREATEREPAIRWORKORDERDLGGRPLABEL1: General"),"MCH_CODE,MCH_CODE_CONTRACT,MCHNAME,PART_NO,INVENTORYVALUE,CBACCOUNTED,SERIAL_NO",true,true);
        headlay.defineGroup(mgr.translate("PLADEWCREATEREPAIRWORKORDERDLGGRPLABEL2: Repair Workshop"),"SUP_MCH_CODE,CONTRACT,SUPMCHNAME,NOTE",true,true);
        headlay.defineGroup(mgr.translate("PLADEWCREATEREPAIRWORKORDERDLGGRPLABEL3: Work Order Information"),"REG_DATE,PLAN_F_DATE,ERRDESCR,ORG_CODE,ORGDESCR,REPORTED_BY,WO_CONTRACT",true,true);
        headbar.defineCommand(headbar.SAVERETURN,"saveReturn","checkAllFields()");
        headbar.defineCommand(headbar.CANCELEDIT,"cancel","self.close()");

        //-----------------------------------------------------------------------
        //----------------- block used for setcheckbox() funtion ----------------
        //-----------------------------------------------------------------------

        tempblk = mgr.newASPBlock("TEMP");
        f = tempblk.addField("LUNAME");

	enableConvertGettoPost();
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        if (myFlag)

            title = mgr.translate("PCMWCREATEREPAIRWORKORDERDLGCRTRPWOSO: Create Repair Work Order for Serial Object")+" - " +tr_MchCode+" "+tr_mchName;

        else
            title = mgr.translate("PCMWCREATEREPAIRWORKORDERDLGCRTRPWOSO: Create Repair Work Order for Serial Object");

        if (!mgr.isEmpty(mgr.readValue("DATE_MASK")))
            date_mask = mgr.readValue("DATE_MASK");
        else
            date_mask = "";     
    }

//===============================================================
//  HTML
//===============================================================

    protected String getDescription()
    {
        return title;
    }

    protected String getTitle()
    {
        return "PCMWCREATEREPAIRWORKORDERDLGCRTRPWOSO: Create Repair Work Order for Serial Object";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        // XSS_Safe ILSOLK 20070710
        if (newWndFlag)
        {
            newWndFlag = false;

            String url = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");
            appendDirtyJavaScript("window.location ='"+mgr.encodeStringForJavascript(url)+"Navigator.page?MAINMENU=Y&NEW=Y';");
            if (mgr.isExplorer())
                appendDirtyJavaScript("   window.open('"+mgr.encodeStringForJavascript(url)+"Pcmw/CreateRepairWorkOrderDlg.page','CreateRepairWO','resizable,status,scrollbars=yes,width=835,height=610');\n");
            else
                appendDirtyJavaScript("   window.open('"+mgr.encodeStringForJavascript(url)+"Pcmw/CreateRepairWorkOrderDlg.page','CreateRepairWO','resizable,status,scrollbars=yes,width=835,height=610');\n");
        }
        else
        {
            printHiddenField("CONFIG_DATEMASK",msk);
            printHiddenField("DATE_MASK",date_mask);

            appendToHTML(headlay.show());

            appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
            appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
            appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

            appendDirtyJavaScript("function WriteSite(con)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("form.CONTRACT.value=con;\n");
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
            appendDirtyJavaScript("		+ '&MCH_CODE_CONTRACT=' + URLClientEncode(getValue_('MCH_CODE_CONTRACT',i))\n");
            appendDirtyJavaScript("		+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
            appendDirtyJavaScript("		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript(" window.status='';\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'MCH_CODE',i,'Object ID') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('MCHNAME',i,0);\n");
            appendDirtyJavaScript("		assignValue_('PART_NO',i,1);\n");
            appendDirtyJavaScript("		assignValue_('SERIAL_NO',i,2);\n");
            appendDirtyJavaScript("		assignValue_('CBACCOUNTED',i,3);\n");
            appendDirtyJavaScript("		assignValue_('INVENTORYVALUE',i,4);\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	if (getValue_('CBACCOUNTED',i) == \"TRUE\")\n");
            appendDirtyJavaScript("		f.CBACCOUNTED.click();	\n");
            appendDirtyJavaScript("}\n");

            /*appendDirtyJavaScript("function validatePlanFDate(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   regval = document.form.REG_DATE.value;\n");
            appendDirtyJavaScript("   document.form.REG_DATE.value= regval;\n");
            appendDirtyJavaScript("   planfdat = document.form.PLAN_F_DATE.value;\n");
            appendDirtyJavaScript("   document.form.PLAN_F_DATE.value= planfdat;\n");
            appendDirtyJavaScript("   	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkPlanFDate(i) ) return;\n");
            appendDirtyJavaScript(" window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	r = __connect(\n");
            appendDirtyJavaScript("		'");
            appendDirtyJavaScript(mgr.getURL());
            appendDirtyJavaScript("?VALIDATE=PLAN_F_DATE'\n");
            appendDirtyJavaScript("		+ '&REG_DATE=' +regval\n");
            appendDirtyJavaScript("		+ '&PLAN_F_DATE=' +planfdat\n");
            appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
            appendDirtyJavaScript("		+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript(" window.status='';\n");
            appendDirtyJavaScript("	if (regval != '')\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("            	if (planfdat < regval)\n");
            appendDirtyJavaScript("		{\n");
            appendDirtyJavaScript("			alert('");
            appendDirtyJavaScript(mgr.translateJavaScript("PLADEWCREATEREPAIRWORKORDERDLGCOMPDATETHAN: Completion Date is earlier than Start Date."));
            appendDirtyJavaScript("');\n");
            appendDirtyJavaScript("			getField_('PLAN_F_DATE',i).value = '';\n");
            appendDirtyJavaScript("		}\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");*/

            appendDirtyJavaScript("if ('");
            appendDirtyJavaScript(CancelFlag);
            appendDirtyJavaScript("'=='true')\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   self.close();\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("if ('");
            appendDirtyJavaScript(FinishFlag);
            appendDirtyJavaScript("'=='true')\n");
            appendDirtyJavaScript("{ \n");
            appendDirtyJavaScript("   if ('");
            appendDirtyJavaScript(flag2);
            appendDirtyJavaScript("'== 0)\n");
            appendDirtyJavaScript("       alert('");
            appendDirtyJavaScript(mgr.translateJavaScript(msg1));
            appendDirtyJavaScript("');\n");
            appendDirtyJavaScript("   else   \n");
            appendDirtyJavaScript("       alert('");
            appendDirtyJavaScript(mgr.translateJavaScript(msg2));
            appendDirtyJavaScript("');\n");
            appendDirtyJavaScript("   self.close();\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function lovMchCode(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	openLOVWindow('MCH_CODE',i,\n");
            appendDirtyJavaScript("      '../equipw/EquipmentSerialLov.page?' \n");
            appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
            appendDirtyJavaScript("		,600,445,'validateMchCode');\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript(" if (f.DATE_MASK.value == \"\")\n");
            appendDirtyJavaScript("    constructMask();\n");
            appendDirtyJavaScript("\n");

            appendDirtyJavaScript(" function constructMask()\n");
            appendDirtyJavaScript(" {\n");
            appendDirtyJavaScript("   dateMask = f.CONFIG_DATEMASK.value; \n");
            appendDirtyJavaScript("   re = /(\")/g; \n");                                          
            appendDirtyJavaScript("   dateMask1 = dateMask.replace(re,\"\\\"\"); \n");                  
            appendDirtyJavaScript("   re = /G+/g; \n");                                        
            appendDirtyJavaScript("   dateMask2 = dateMask1.replace(re,\"AD\"); \n");                
            appendDirtyJavaScript("   re = /(y+)/g; \n");                                      
            appendDirtyJavaScript("   dateMask3 = dateMask2.replace(re,\"YYYY\"); \n");              
            appendDirtyJavaScript("   re = /(M+)/g; \n");                                       
            appendDirtyJavaScript("   dateMask3.match(re); \n");                                  
            appendDirtyJavaScript("   if (RegExp.$1.length > 3) \n");                             
            appendDirtyJavaScript("      dateMask4 = dateMask3.replace(re,\"Mont\"); \n");            
            appendDirtyJavaScript("   else if  ( RegExp.$1.length == 3 ) \n");                      
            appendDirtyJavaScript("      dateMask4 = dateMask3.replace(re,\"MON\"); \n");            
            appendDirtyJavaScript("   else \n");                                               
            appendDirtyJavaScript("      dateMask4 = dateMask3.replace(re,\"MM\"); \n");              
            appendDirtyJavaScript("   re = /^D/g; \n");                                        
            appendDirtyJavaScript("   dateMask5 = dateMask4.replace(re,\"DDD\");\n");               
            appendDirtyJavaScript("   re = /\bD+/g; \n");                                      
            appendDirtyJavaScript("   dateMask6 = dateMask5.replace(re,\"DDD\"); \n");              
            appendDirtyJavaScript("   re = /d+/g; \n");                                        
            appendDirtyJavaScript("   dateMask7 = dateMask6.replace(re,\"DD\"); \n");               
            appendDirtyJavaScript("   re = /(E+)/g; \n");                                      
            appendDirtyJavaScript("   dateMask7.match(re); \n");                                   
            appendDirtyJavaScript("   if (RegExp.$1.length > 3) \n");                             
            appendDirtyJavaScript("      dateMask8 = dateMask7.replace(re,\"DAY\"); \n");             
            appendDirtyJavaScript("   else \n");                                               
            appendDirtyJavaScript("      dateMask8 = dateMask7.replace(re,\"DY\"); \n");              
            appendDirtyJavaScript("   re = /H+|k+/g; \n");                                     
            appendDirtyJavaScript("   dateMask9 = dateMask8.replace(re,\"HH24\"); \n");             
            appendDirtyJavaScript("   re = /h+|K+/g; \n");                                     
            appendDirtyJavaScript("   dateMask10 = dateMask9.replace(re,\"HH\"); \n");               
            appendDirtyJavaScript("   re = /m+/g; \n");                                         
            appendDirtyJavaScript("   dateMask11 = dateMask10.replace(re,\"MI\"); \n");              
            appendDirtyJavaScript("   re = /s+/g; \n");                                        
            appendDirtyJavaScript("   dateMask12 = dateMask11.replace(re,\"SS\"); \n");              
            appendDirtyJavaScript("   re = /w+/g; \n");                                        
            appendDirtyJavaScript("   dateMask13 = dateMask12.replace(re,\"IW\"); \n");              
            appendDirtyJavaScript("   re = /W+/g; \n");                                        
            appendDirtyJavaScript("   dateMask14 = dateMask13.replace(re,\"W\"); \n");               
            appendDirtyJavaScript("   re = /a+/g; \n");                                        
            appendDirtyJavaScript("   dateMask15 = dateMask14.replace(re,\"AM\"); \n");              
            appendDirtyJavaScript("   re = /Mont/g; \n");                                       
            appendDirtyJavaScript("   dateMask16 = dateMask15.replace(re,\"Month\"); \n");  
            appendDirtyJavaScript("   f.DATE_MASK.value = dateMask16; \n");
            appendDirtyJavaScript(" }\n");
            appendDirtyJavaScript("\n");    

            appendDirtyJavaScript(" function validateRegDate(i) \n");
            appendDirtyJavaScript("{ \n");
            appendDirtyJavaScript("  if( getRowStatus_('HEAD',i)=='QueryMode__' ) return; \n");
            appendDirtyJavaScript("     setDirty(); \n");
            appendDirtyJavaScript("  if( !checkRegDate(i) ) return; \n");     
            appendDirtyJavaScript("     window.status='Please wait for validation'; \n");
            appendDirtyJavaScript("  r = __connect( \n");
            appendDirtyJavaScript("		'");
            appendDirtyJavaScript(mgr.getURL());
            appendDirtyJavaScript("?VALIDATE=REG_DATE'\n");     
            appendDirtyJavaScript("  + '&REG_DATE=' + URLClientEncode(getValue_('REG_DATE',i)) \n");
            appendDirtyJavaScript("  + '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i)) \n");
            appendDirtyJavaScript("  + '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i)) \n");
            appendDirtyJavaScript("  + '&PLAN_F_DATE=' + URLClientEncode(getValue_('PLAN_F_DATE',i)) \n");
            appendDirtyJavaScript("  + '&DATE_MASK=' + URLClientEncode(getValue_('DATE_MASK',i)) \n");
            appendDirtyJavaScript("  ); \n");
            appendDirtyJavaScript("  window.status=''; \n");
            appendDirtyJavaScript("  if( checkStatus_(r,'REG_DATE',i,'Planned Start') ) \n");
            appendDirtyJavaScript("  { \n");
            appendDirtyJavaScript("    assignValue_('REG_DATE',i,0); \n");
            appendDirtyJavaScript("    assignValue_('PLAN_F_DATE',i,1); \n");
            appendDirtyJavaScript("  } \n");
            appendDirtyJavaScript("} \n");
        }
    }
}
