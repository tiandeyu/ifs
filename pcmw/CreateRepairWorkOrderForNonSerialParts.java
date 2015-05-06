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
*  File        : CreateRepairWorkOrderForNonSerialParts.java 
*  Created     : ASP2JAVA Tool  010312
*  Modified    :    
*  CHCRLK  010503  Wrote numCount to ctx in run() method.
*  CHCRLK  010613  Modified overwritten validations. 
*  VAGULK  010719  Changed the saveNew() to SaveReturn() Call Id 77822
*  CHCRLK  010824  Set to open in new window when called from the navigator.
*  VAGULK  011011  Changed the title given for the Auto Repair WO (Translation problem)-62864
*  VAGULK  011012  Changed the LOV for Part No to  MAINTENANCE_INV_PART_LOV (70639)
*  VAGULK  011018  Corrected the prob with Exec Dept LOV and description (70640)
*  JEJALK  021205  Takeoff Beta Modification.
*  SAPRLK  031222  Web Alignment - removed methods clone() and doReset().
*  SHAFLK  040119  Bug Id 41815,Removed Java dependencies. 
*  SHAFLK  040223  Bug Id 42844,Modified saveReturn() and preDefine() methods.
* -------------------------------- Edge - SP1 Merge -------------------------
*  THWILK  040325  Merge with SP1.
*  ARWILK  040720  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  THWILK  040818  Modified OkIssue().
*  SHAFLK  040812  Bug 45904, Modified method okFindIssue().
*  NIJALK  041008  Merged bug 45904.
*  SHAFLK  040525  Bug 50830, Used mgr.translateJavaScript() method to get the correctly translated strings in JavaScript functions.
*  NIJALK  050617  Merged bug 50830.
*  NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile).  
*  NIJALK  060303  Bug 136150: Modified printContents().
*  RANFLK  060306  Bug 136358 Modified predefine()
*  THWILK  060306  Bug 136341, Modified validate().
*  NIJALK  060306  Bug 136366, Modified validateRepairPartNo().
*  NIJALK  060306  Bug 136366, Modified saveReturn(). Used mgr.showError() function instead of mgr.showAlert().
*  AMNILK  060727  Bug 58214.  HTML, JavaScript & URL Encoding for the vulnerable fields.
*  AMNILK  060807  Merged Bug id: 58214.
*  ILSOLK  070218  Modifed for MTPR904 Hink tasks ReqId 45088.setLovPropery() for Object Id.
*  AMDILK  070503  Call Id 143404: Modified validate() 
*  ASSALK  070531  Call ID 145439: Modified printContents().
*  ASSALK  070604  Call 145439. Modified validate(),preDefine(),printContents().
*  ASSALK  070624  Call 145439. Mdofied printContents().
*  AMNILK  070713  Eliminated SQL Injection.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class CreateRepairWorkOrderForNonSerialParts extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.CreateRepairWorkOrderForNonSerialParts");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPForm frm;
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
    private String finishFlag;
    private String closeFlag;
    private String generatedFlag;
    private String wono;
    private ASPTransactionBuffer trans;
    private double numCount;
    private boolean fromMaintMat;
    private String nWoNo;
    private String sMchCode;
    private String sMchDesc;
    private String sMchCodeContract;
    private String sContract;
    private String sPartNo;
    private String sPartDesc;
    private String sSpareContract;
    private String sErrDescrLo;
    private ASPCommand cmd;
    private String contract;
    private String reg_date;
    private ASPBuffer row;
    private ASPTransactionBuffer secBuff;
    private String val;
    private String description;
    private String partNo;
    private String cont;
    private String repDesc;
    private String sOrgCode;
    private String sOrgDesc;
    private String sDirective;
    private String inventVal;
    private String zeroFlag;
    private String clVal;
    private String cbaccounted;
    private String finish;
    private ASPBuffer buf;
    private String regdate_;
    private String flg;
    private String rep_by_id;
    private String company;
    private String rep_contract;
    private ASPBuffer data;
    private String comp;
    private String sRepBy;
    private String sRepById;
    private String regDate;
    private double nInventoryVal;
    private String clientVal;
    private String regdat;
    private String err;
    private String org;
    private String repBy;
    private String repPartNo;
    private String repairable;
    private ASPQuery q;
    private String n;
    private String head_command;
    private String msk,date_mask;
    private String mch;
    private String part;

    //================================================================================
    // ASP2JAVA WARNING: Types of the following variables are not identified by
    // the ASP2JAVA converter. Define them and remove these comments.
    //================================================================================
    private String isSecure[] = new String[8];  
    private String title;  
    private String txt;  
    private String leadtime;  
    private String dateMask;  
    private String sel;  
    private boolean newWndFlag;

    //===============================================================
    // Construction 
    //===============================================================
    public CreateRepairWorkOrderForNonSerialParts(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        finishFlag =  closeFlag ;
        closeFlag =  generatedFlag ;
        generatedFlag =  "";
        title =  "";
        wono =  "";
        ASPManager mgr = getASPManager();

        frm = mgr.getASPForm();
        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();
        title = ctx.readValue("CTX1",title);
        wono = ctx.readValue("WONO",wono);

        fromMaintMat = ctx.readFlag("FROMMAINTMAT",false);

        nWoNo = ctx.readValue("WO_NO","");
        sMchCode = ctx.readValue("MCH_CODE","");
        sMchDesc = ctx.readValue("DESCRIPTION","");
        sMchCodeContract = ctx.readValue("MCH_CODE_CONTRACT","");
        sPartNo = ctx.readValue("PART_NO","");
        sPartDesc = ctx.readValue("SPAREDESCRIPTION","");
        sSpareContract = ctx.readValue("SPARE_CONTRACT","");
        sErrDescrLo = ctx.readValue("ERRDESCRLO","");
        numCount = ctx.readNumber("NUMCOUNT",0.0);
        sContract = ctx.readValue("CONTRACT","");

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (mgr.dataTransfered())
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();
        else if ((!mgr.isEmpty(mgr.getQueryStringValue("WO_NO"))) && (!mgr.isEmpty(mgr.getQueryStringValue("PART_NO"))))
        {
            fromMaintMat = true;

            nWoNo = mgr.readValue("WO_NO");
            sMchCode = mgr.readValue("MCH_CODE");
            sMchDesc = mgr.readValue("DESCRIPTION");
            sContract = mgr.readValue("CONTRACT");
            sPartNo = mgr.readValue("PART_NO");
            sPartDesc = mgr.readValue("SPAREDESCRIPTION");
            sSpareContract = mgr.readValue("SPARE_CONTRACT");
            String numCountStr = mgr.readValue("COUNT");
            numCount = toDouble(numCountStr);

            okFindIssue();
            sErrDescrLo = mgr.translate("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSAUTOMATICAUTO: Auto Repair WO for &1 ",sPartNo);
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("NEWWNDFLAG")))
        {
            newWndFlag = true;
        }
        else
        {
            newRow();
        }

        adjust();

        ctx.writeValue("CTX1",title);
        ctx.writeValue("WONO",wono); 
        ctx.writeNumber("NUMCOUNT",numCount); 
        ctx.writeFlag("FROMMAINTMAT",fromMaintMat);

        ctx.writeValue("WO_NO",nWoNo); 
        ctx.writeValue("MCH_CODE",sMchCode); 
        ctx.writeValue("DESCRIPTION",sMchDesc); 
        ctx.writeValue("MCH_CODE_CONTRACT",sMchCodeContract); 
        ctx.writeValue("PART_NO",sPartNo); 
        ctx.writeValue("SPAREDESCRIPTION",sPartDesc); 
        ctx.writeValue("SPARE_CONTRACT",sSpareContract); 
        ctx.writeValue("ERRDESCRLO",sErrDescrLo);
        ctx.writeValue("CONTRACT",sContract); 
    }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

    public void startup()
    {
        ASPManager mgr = getASPManager();

        cmd = trans.addCustomCommand("GEN1", "Maintenance_Spare_API.Get_Default_Contract");
        cmd.addParameter("CONTRACT");

        cmd = trans.addCustomFunction("GEN2", "Maintenance_Site_Utility_API.Get_Site_Date","REGDATE");
        cmd.addParameter("CONTRACT");

        trans = mgr.perform(trans);

        contract = trans.getValue("GEN1/DATA/CONTRACT");
        reg_date = trans.getValue("GEN2/DATA/REGDATE");

        row = headset.getRow();
        row.setValue("CONTRACT",contract);
        row.setValue("REGDATE",reg_date);
        headset.setRow(row);
    }


    public boolean  checksec( String method,int ref)
    {
        ASPManager mgr = getASPManager();

        isSecure[ref] = "false" ; 


        String splitted[] = split(method,"."); 

        secBuff = mgr.newASPTransactionBuffer();
        secBuff.addSecurityQuery(splitted[0],splitted[1]);

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

    public void validate()
    {
        int ref;
        ASPManager mgr = getASPManager();

        val = mgr.readValue("VALIDATE");

        if ("MCH_CODE".equals(val))
        {
            cmd = trans.addCustomFunction("MCNAME","Maintenance_Object_API.Get_Mch_Name","MCHNAME");
            cmd.addParameter("MCH_CODE_CONTRACT");
            cmd.addParameter("MCH_CODE");

            trans = mgr.validate(trans);

            description = trans.getValue("MCNAME/DATA/MCHNAME");

            txt = (mgr.isEmpty(description) ? "" : (description))+ "^" ;
            mgr.responseWrite(txt);
        }
        else if ("REPAIR_PART_NO".equals(val))
        {
            partNo = mgr.readValue("REPAIR_PART_NO");
            cont = mgr.readValue("REPAIR_PART_CONTRACT");

            if (!mgr.isEmpty(partNo) && !mgr.isEmpty(cont))
            {

                cmd = trans.addCustomFunction("REPADESC","Inventory_Part_API.Get_Description","REPAIRPARTDESCR");
                cmd.addParameter("REPAIR_PART_CONTRACT");
                cmd.addParameter("REPAIR_PART_NO");

                cmd = trans.addCustomFunction("GETORGCONT","Maintenance_Inv_Part_API.Get_Org_Contract","CONTRACT");
                cmd.addParameter("REPAIR_PART_CONTRACT");
                cmd.addParameter("REPAIR_PART_NO");

                cmd = trans.addCustomFunction("GETORGCODE","Maintenance_Inv_Part_API.Get_Org_Code","ORG_CODE");
                cmd.addParameter("REPAIR_PART_CONTRACT",cont);
                cmd.addParameter("REPAIR_PART_NO",partNo);

                cmd = trans.addCustomFunction("GETORGDESC","Organization_API.Get_Description","ORGDESCR");
                cmd.addReference("CONTRACT","GETORGCONT/DATA");
                cmd.addReference("ORG_CODE","GETORGCODE/DATA");

                cmd = trans.addCustomFunction("GETDIRECTIVE","Maintenance_inv_Part_API.Get_Directive","ERR_DESCR");
                cmd.addParameter("REPAIR_PART_CONTRACT");
                cmd.addParameter("REPAIR_PART_NO");

                cmd = trans.addCustomFunction("COM","Site_API.Get_Company","COMPANY");
                cmd.addReference("CONTRACT","GETORGCONT/DATA");
            }

            if (checksec("INVENTORY_PART_API.Get_Description",1))
            {
                cmd = trans.addCustomFunction("INVENTVAL","Inventory_Part_API.Get_Inventory_Value_By_Method","INVENTORYVALUE");
                cmd.addParameter("REPAIR_PART_CONTRACT");
                cmd.addParameter("REPAIR_PART_NO");
            }
            if (checksec("Inventory_Part_API.Get_Zero_Cost_Flag",2))
            {
                cmd = trans.addCustomFunction("INTZEROF","Inventory_Part_API.Get_Zero_Cost_Flag","INVENPAZEROFLAG");
                cmd.addParameter("REPAIR_PART_CONTRACT");
                cmd.addParameter("REPAIR_PART_NO");
            }
            if (checksec("Inventory_Part_Zero_Cost_API.Get_client_value",3))
            {
                cmd = trans.addCustomFunction("CLVAL","Inventory_Part_Zero_Cost_API.Get_client_value","CLIENTVAL");
                cmd.addParameter("NINDEX","2");
            }

            trans = mgr.validate(trans);

            repDesc = trans.getValue("REPADESC/DATA/REPAIRPARTDESCR");
            String sOrgContract = trans.getValue("GETORGCONT/DATA/CONTRACT");
            sOrgCode = trans.getValue("GETORGCODE/DATA/ORG_CODE");
            sOrgDesc = trans.getValue("GETORGDESC/DATA/ORGDESCR");
            sDirective = trans.getValue("GETDIRECTIVE/DATA/ERR_DESCR");
            String sCompany = trans.getValue("COM/DATA/COMPANY");

            ref = 0;

            if (isSecure[ref += 1] =="true")
            {
                inventVal = trans.getValue("INVENTVAL/DATA/INVENTORYVALUE");
            }
            if (( isSecure[ref += 1] =="true" ) &&  ( isSecure[ref += 1] =="true" ))
            {
                zeroFlag = trans.getValue("INTZEROF/DATA/INVENPAZEROFLAG");
                clVal = trans.getValue("CLVAL/DATA/CLIENTVAL");
            }


            if (clVal.equals(zeroFlag))
                cbaccounted = "TRUE";
            else
                cbaccounted = "FALSE";

            txt = (mgr.isEmpty(repDesc) ? "" : (repDesc))+ "^" + 
                  (mgr.isEmpty(inventVal) ? "" : (inventVal))+ "^" + 
                  (mgr.isEmpty(cbaccounted) ? "": (cbaccounted))+ "^" +  
                  (mgr.isEmpty(sOrgCode) ? "": (sOrgCode))+ "^"+ 
                  (mgr.isEmpty(sOrgDesc) ? "": (sOrgDesc))+ "^" + 
                  (mgr.isEmpty(sDirective) ? "": (sDirective))+ "^" + 
                  (mgr.isEmpty(sOrgContract) ? "": (sOrgContract))+ "^" + 
                  (mgr.isEmpty(sCompany) ? "": (sCompany))+ "^";

            mgr.responseWrite(txt);
        }
        else if ("REGDATE".equals(val))
        {
            if (!mgr.isEmpty(mgr.readValue("PLAN_F_DATE")))
                finish = mgr.readValue("PLAN_F_DATE");
            else
                finish = "";   

            buf = mgr.newASPBuffer();
            buf.addFieldItem("REGDATE",mgr.readValue("REGDATE"));
            regdate_ = buf.getFieldValue("REGDATE");
            boolean isErr = false;

            if (mgr.isEmpty(mgr.readValue("PLAN_F_DATE")))
            {
                cmd = trans.addCustomFunction("LEAD","Inventory_Part_API.Get_Purch_Leadtime","LEAD_TIME");
                cmd.addParameter("REPAIR_PART_CONTRACT");
                cmd.addParameter("REPAIR_PART_NO");

                trans = mgr.perform(trans);
                leadtime = trans.getValue("LEAD/DATA/LEAD_TIME");

                trans.clear();

                String dateMask = mgr.readValue("DATE_MASK");   

                if (!mgr.isEmpty(mgr.readValue("REGDATE")))
                {
                    if (!mgr.isEmpty(leadtime))
                    {
			// SQLInjection_Safe AMNILK 20070713
                        sel = "select to_date( ? , ? ) + ? PLAN_F_DATE from DUAL";
                        flg = "True";                                                                                                                
                    }
                    else
                        flg = "";   

                }

                if ( ("True".equals(flg)) && ( Integer.parseInt(leadtime) > 0 ) )
                {
		    // SQLInjection_Safe AMNILK 20070713
                    q = trans.addQuery("XX",sel);                                                                                                            
		    q.addParameter("DUMMY_RESULT",mgr.readValue("REGDATE"));
		    q.addParameter("DESCRIPTION",dateMask);
		    q.addParameter("LEAD_TIME",leadtime);

                    trans = mgr.validate(trans);  
		    finish = trans.getBuffer("XX/DATA").getFieldValue("PLAN_F_DATE");   

                }

            }
            else
            {
                trans.clear();
                ASPQuery q = trans.addQuery("GETDATE","SELECT 'TRUE' DUMMY_RESULT FROM DUAL WHERE ? > ?");
                q.addParameter("PLAN_F_DATE",mgr.readValue("PLAN_F_DATE"));
                q.addParameter("REGDATE",mgr.readValue("REGDATE"));

                trans = mgr.perform(trans);
                String result = trans.getValue("GETDATE/DATA/DUMMY_RESULT");

                if (!"TRUE".equals(result)) {
                    txt = "No_Data_Found" + mgr.translate("PCMWPLANEDDATE: Planned Completion is earlier than Planned Start.");
                    isErr = true;
                }
            }
	    
            if (!isErr )
                txt = regdate_ +"^"+ finish +"^";   

	    mgr.responseWrite(txt);                        
        }

        else if ("PLAN_F_DATE".equals(val))
        {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("PLAN_F_DATE",mgr.readValue("PLAN_F_DATE")); 
            boolean isErr = false;
            if (!mgr.isEmpty(mgr.readValue("REGDATE"))) {
                trans.clear();
                ASPQuery q = trans.addQuery("GETDATE","SELECT 'TRUE' DUMMY_RESULT FROM DUAL WHERE ? > ?");
                q.addParameter("PLAN_F_DATE",mgr.readValue("PLAN_F_DATE"));
                q.addParameter("REGDATE",mgr.readValue("REGDATE"));

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

        else if ("REPORTED_BY".equals(val))
        {
            cmd = trans.addCustomFunction( "REPBY", "Company_Emp_API.Get_Max_Employee_Id","REPORTED_BY_ID");
            cmd.addParameter("COMPANY");  
            cmd.addParameter("REPORTED_BY"); 

            trans = mgr.validate(trans);

            rep_by_id = trans.getValue("REPBY/DATA/REPORTED_BY_ID");

            mgr.responseWrite(rep_by_id);
        }
        else if ("MCH_CODE_CONTRACT".equals(val))
        {
            cmd = trans.addCustomFunction( "OBJDES", "Equipment_Serial_API.Get_Mch_Name", "MCHNAME" );
            cmd.addParameter("MCH_CODE_CONTRACT");
            cmd.addParameter("MCH_CODE");

            cmd = trans.addCustomFunction( "PARTNO", "Equipment_Serial_API.Get_Part_No", "REPAIR_PART_NO" );
            cmd.addParameter("MCH_CODE_CONTRACT");
            cmd.addParameter("MCH_CODE");

            trans = mgr.validate(trans);

            mch = trans.getValue("OBJDES/DATA/MCHNAME");
            part = trans.getValue("PARTNO/DATA/PART_NO");

            rep_contract = mgr.readValue("MCH_CODE_CONTRACT");
            contract = mgr.readValue("MCH_CODE_CONTRACT");

            txt = (mgr.isEmpty(rep_contract) ? "" : (rep_contract))+ "^" + (mgr.isEmpty(contract) ? "" : (contract))+ "^" + (mgr.isEmpty(mch) ? "" : (mch))+ "^" + (mgr.isEmpty(part) ? "" : (part))+ "^" ; 
            mgr.responseWrite(txt);
        }
        else if ("CONTRACT".equals(val))
        {
            cmd = trans.addCustomFunction("COM","Site_API.Get_Company","COMPANY");
            cmd.addParameter("CONTRACT");

            trans = mgr.validate(trans);

            company = trans.getValue("COM/DATA/COMPANY");
            mgr.responseWrite((mgr.isEmpty(company)?"":company)+"^");
        }
        else if ("ORG_CODE".equals(val))
        {
            cmd = trans.addCustomFunction("GETDESC","Organization_API.Get_Description","ORGDESCR");
            cmd.addParameter("CONTRACT");
            cmd.addParameter("ORG_CODE");

            trans = mgr.validate(trans);

            String sOrgDesc = trans.getValue("GETDESC/DATA/ORGDESCR");
            mgr.responseWrite((mgr.isEmpty(sOrgDesc)?"":sOrgDesc)+"^");
        }
        else if ("REPAIR_PART_CONTRACT".equals(val))
        {
            partNo = mgr.readValue("REPAIR_PART_NO");
            cont = mgr.readValue("REPAIR_PART_CONTRACT");

            if (!mgr.isEmpty(partNo) && !mgr.isEmpty(cont))
            {

                cmd = trans.addCustomFunction("REPADESC","Inventory_Part_API.Get_Description","REPAIRPARTDESCR");
                cmd.addParameter("REPAIR_PART_CONTRACT");
                cmd.addParameter("REPAIR_PART_NO");
            }
            cmd = trans.addCustomFunction("INVENTVAL","Inventory_Part_API.Get_Inventory_Value_By_Method","INVENTORYVALUE");
            cmd.addParameter("REPAIR_PART_CONTRACT");
            cmd.addParameter("REPAIR_PART_NO");

            repDesc = trans.getValue("REPADESC/DATA/REPAIRPARTDESCR");
            inventVal = trans.getValue("INVENTVAL/DATA/INVENTORYVALUE");


            txt = (mgr.isEmpty(repDesc) ? "" : (repDesc))+ "^" + (mgr.isEmpty(inventVal) ? "" : (inventVal))+ "^" ;
            mgr.responseWrite(txt);

        }
        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void newRow()
    {
        ASPManager mgr = getASPManager();


        cmd = trans.addCustomCommand("GEN1", "Maintenance_Spare_API.Get_Default_Contract");
        cmd.addParameter("CONTRACT");

        cmd = trans.addCustomFunction("GEN2", "Maintenance_Site_Utility_API.Get_Site_Date","REGDATE");
        cmd.addParameter("CONTRACT");

        cmd = trans.addCustomFunction("COM","Site_API.Get_Company","COMPANY");
        cmd.addReference("CONTRACT","GEN1/DATA");

        cmd = trans.addCustomFunction("GETUSER","Fnd_Session_API.Get_Fnd_User","FND_USER");

        cmd = trans.addCustomFunction("GETIDUSER","Person_Info_API.Get_Id_For_User","REPORTED_BY");
        cmd.addReference("FND_USER","GETUSER/DATA");

        cmd = trans.addCustomFunction("GETMAXIDUSER","Company_Emp_API.Get_Max_Employee_Id","REPORTED_BY_ID");
        cmd.addReference("COMPANY","COM/DATA");      
        cmd.addReference("REPORTED_BY","GETIDUSER/DATA");


        trans = mgr.perform(trans);

        contract = trans.getValue("GEN1/DATA/CONTRACT");
        data = trans.getBuffer("HEAD/DATA");
        reg_date = trans.getValue("GEN2/DATA/REGDATE");
        comp = trans.getValue("COM/DATA/COMPANY");
        sRepBy = trans.getValue("GETIDUSER/DATA/REPORTED_BY");
        sRepById = trans.getValue("GETMAXIDUSER/DATA/REPORTED_BY_ID");

        headset.addRow(data);


        row = headset.getRow();
        row.setValue("MCH_CODE_CONTRACT",contract);
        row.setValue("REPAIR_PART_CONTRACT",contract);
        row.setValue("CONTRACT",contract);
        row.setValue("COMPANY",comp);
        row.setValue("REPORTED_BY",sRepBy);
        row.setValue("REPORTED_BY_ID",sRepById);
        headset.setRow(row);
    }


    public void okFindIssue()
    {
        ASPManager mgr = getASPManager();

        headset.addRow(mgr.newASPBuffer());

        if (headset.countRows()>0)
        {
            trans.clear();

            //Bug 45904, start
            cmd = trans.addCustomFunction("MCHCONTR","EQUIPMENT_OBJECT_API.Get_Contract","MCH_CODE_CONTRACT");
            cmd.addParameter("MCH_CODE",sMchCode);
            //Bug 45904, end

            cmd = trans.addCustomFunction("GETCOMP","Site_API.Get_Company","COMPANY");
            cmd.addParameter("CONTRACT",sSpareContract);

            cmd = trans.addCustomFunction("GETREGDATE", "Maintenance_Site_Utility_API.Get_Site_Date","REGDATE");
            cmd.addParameter("CONTRACT");

            cmd = trans.addCustomFunction("GETINVVAL","Inventory_Part_API.Get_Inventory_Value_By_Method","INVENTORYVALUE");
            cmd.addParameter("CONTRACT",sSpareContract);
            cmd.addParameter("REPAIR_PART_NO",sPartNo);

            cmd = trans.addCustomFunction("GETZEROCOST","Inventory_Part_API.Get_Zero_Cost_Flag","INVENPAZEROFLAG");
            cmd.addParameter("CONTRACT",sSpareContract);
            cmd.addParameter("REPAIR_PART_NO",sPartNo);

            cmd = trans.addCustomFunction("GETCLIENTVAL","Inventory_Part_Zero_Cost_API.Get_client_value","CLIENTVAL");
            cmd.addParameter("NINDEX","2");

            cmd = trans.addCustomFunction("GETDIRECTIVE","Maintenance_inv_Part_API.Get_Directive","ERR_DESCR");
            cmd.addParameter("CONTRACT",sSpareContract);
            cmd.addParameter("REPAIR_PART_NO",sPartNo);

            cmd = trans.addCustomFunction("GETORGCODE","Maintenance_Inv_Part_API.Get_Org_Code","ORG_CODE");
            cmd.addParameter("CONTRACT",sSpareContract);
            cmd.addParameter("REPAIR_PART_NO",sPartNo);

            cmd = trans.addCustomFunction("GETORGDESC","Organization_API.Get_Description","ORGDESCR");
            cmd.addReference("CONTRACT",sContract);
            cmd.addReference("ORG_CODE","GETORGCODE/DATA");

            cmd = trans.addCustomFunction("GETUSER","Fnd_Session_API.Get_Fnd_User","FND_USER");

            cmd = trans.addCustomFunction("GETIDUSER","Person_Info_API.Get_Id_For_User","REPORTED_BY");
            cmd.addReference("FND_USER","GETUSER/DATA");

            cmd = trans.addCustomFunction("GETMAXIDUSER","Company_Emp_API.Get_Max_Employee_Id","REPORTED_BY_ID");
            cmd.addReference("COMPANY","GETCOMP/DATA");     
            cmd.addReference("REPORTED_BY","GETIDUSER/DATA");

            trans = mgr.perform(trans);

            regDate = trans.getValue("GETREGDATE/DATA/REGDATE");
            nInventoryVal = trans.getNumberValue("GETINVVAL/DATA/INVENTORYVALUE");
            zeroFlag = trans.getValue("GETZEROCOST/DATA/INVENPAZEROFLAG");
            clientVal = trans.getValue("GETCLIENTVAL/DATA/CLIENTVAL");
            sDirective = trans.getValue("GETDIRECTIVE/DATA/ERR_DESCR");
            sOrgCode = trans.getValue("GETORGCODE/DATA/ORG_CODE");
            sOrgDesc = trans.getValue("GETORGDESC/DATA/ORGDESCR");
            sRepBy = trans.getValue("GETIDUSER/DATA/REPORTED_BY");
            sRepById = trans.getValue("GETMAXIDUSER/DATA/REPORTED_BY_ID");
            String sMchContact = trans.getValue("MCHCONTR/DATA/MCH_CODE_CONTRACT");  //Bug 45904

            row = headset.getRow();

            row.setValue("MCH_CODE",sMchCode);
            row.setValue("MCHNAME",sMchDesc);
            row.setValue("CONTRACT",sContract);
            row.setValue("MCH_CODE_CONTRACT",sMchContact);  //Bug 45904
            row.setValue("REPAIR_PART_NO",sPartNo);
            row.setValue("REPAIRPARTDESCR",sPartDesc);
            row.setValue("REPAIR_PART_CONTRACT",sSpareContract);
            row.setValue("ERR_DESCR",sDirective);
            row.setValue("ORG_CODE",sOrgCode);
            row.setValue("ORGDESCR",sOrgDesc);       
            row.setValue("REPORTED_BY",sRepBy);
            row.setValue("REPORTED_BY_ID",sRepById);
            row.setNumberValue("INVENTORYVALUE",nInventoryVal);

            if (clientVal.equals(zeroFlag))
                row.setValue("CBACCOUNTED","TRUE");
            headset.setRow(row);
        }
    }

    public void saveReturn()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        regdat = mgr.readValue("REGDATE");
        err = mgr.readValue("ERR_DESCR");
        org = mgr.readValue("ORG_CODE");
        repBy = mgr.readValue("REPORTED_BY");

        if (mgr.isEmpty(sErrDescrLo))
            sErrDescrLo = mgr.translate("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSAUTOMATIC: Repair WO for &1 ",mgr.readValue("REPAIR_PART_NO"));

        if (!mgr.isEmpty(err) && !mgr.isEmpty(org) && !mgr.isEmpty(repBy))
        {
            cmd = trans.addCustomFunction("REPPART","PART_CATALOG_API.Get_Serial_Tracking_Code_Db","REPPARTNO");
            cmd.addParameter("REPAIR_PART_NO",mgr.readValue("REPAIR_PART_NO"));

            cmd = trans.addCustomFunction("REPAIR","MAINTENANCE_INV_PART_API.Get_Repairable","REPAIRABLE");
            cmd.addParameter("REPAIR_PART_CONTRACT",mgr.readValue("REPAIR_PART_CONTRACT"));
            cmd.addParameter("REPAIR_PART_NO",mgr.readValue("REPAIR_PART_NO"));

            trans=mgr.perform(trans);          

            repPartNo=trans.getValue("REPPART/DATA/REPPARTNO");
            repairable=trans.getValue("REPAIR/DATA/REPAIRABLE");

            trans.clear();


            if ("NOT SERIAL TRACKING".equals(repPartNo))
            {

                if ("TRUE".equals(repairable))
                {
                    trans.clear();
                    cmd = trans.addCustomCommand("VALUE","Active_Separate_API.Create_Work_Order_For_Non_Ser");
                    cmd.addParameter("WONO");
                    cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
                    cmd.addParameter("MCH_CODE",mgr.readValue("MCH_CODE"));
                    cmd.addParameter("REPAIR_PART_NO",mgr.readValue("REPAIR_PART_NO"));
                    cmd.addParameter("REPAIR_PART_CONTRACT",mgr.readValue("REPAIR_PART_CONTRACT"));
                    cmd.addParameter("ERR_DESCR",mgr.readValue("ERR_DESCR"));
                    cmd.addParameter("ORG_CODE",mgr.readValue("ORG_CODE"));
                    cmd.addParameter("REGDATE",mgr.readValue("REGDATE"));
                    cmd.addParameter("PLAN_F_DATE",mgr.readValue("PLAN_F_DATE"));
                    cmd.addParameter("REPORTED_BY_ID",mgr.readValue("REPORTED_BY_ID"));
                    cmd.addParameter("ERR_DESCR_LO",sErrDescrLo); 
                    cmd.addParameter("MCH_CODE_CONTRACT",mgr.readValue("MCH_CODE_CONTRACT"));
                    cmd.addParameter("LOT_BATCH_NO",mgr.readValue("LOT_BATCH_NO"));

                    trans=mgr.perform(trans);  
                    wono = trans.getBuffer("VALUE/DATA").getFieldValue("WONO");

                    if (fromMaintMat)
                    {
                        generatedFlag = "TRUE";

                        numCount--;

                        if (numCount < 1)
                            closeFlag = "TRUE";
                        else
                            okFindIssue();
                    }
                    else
                    {
                        finishFlag = "true";
                        closeFlag = "TRUE";
                    }
                }
                else
                    mgr.showError(mgr.translate("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSREPAI: This part is not repairable."));
            }
            else
                mgr.showError(mgr.translate("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSNOALLOW: Not allowed to create repair work order on non serial objects here"));   
        }
        else
            mgr.showError(mgr.translate("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSNOTCREATED: No Work Order has been created"));
    }

    public void cancel()
    {
        ASPManager mgr = getASPManager();

        if (fromMaintMat)
        {
            numCount = numCount - 1;            
            ctx.writeNumber("NUMCOUNT",numCount); 

            if (numCount < 1)
                closeFlag = "TRUE";
            else
                okFindIssue();
        }
        else
        {
            closeFlag = "TRUE";
        }
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void count()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        n = headset.getRow().getValue("N");
        mgr.setStatusLine("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSQRYCNT: Query will retrieve &1 rows",n);
    }


    public void okFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.includeMeta("ALL");
        mgr.submit(trans);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSNODATA: No data found."));
            headset.clear();
        }
    }


    public void preDefine()
    {
        ASPManager mgr = getASPManager();  

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("MCH_CODE");
        f.setSize(26);
        f.setDynamicLOV("MAINTENANCE_OBJECT","MCH_CODE_CONTRACT CONTRACT",600,445);
	f.setLOVProperty("WHERE","(OBJ_LEVEL IS NULL OR (OBJ_LEVEL IS NOT NULL AND Equipment_Object_Level_API.Get_Create_Wo(OBJ_LEVEL) = 'TRUE'))");
        f.setLabel("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSMCH_CODE: Object ID");
        f.setCustomValidation("MCH_CODE_CONTRACT,MCH_CODE","MCHNAME");
        f.setUpperCase();
        f.setMaxLength(100);
        f.setMandatory();

        f = headblk.addField("MCH_CODE_CONTRACT");
        f.setSize(10);
        f.setUpperCase();
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSMCHCODECON: Site");
        f.setCustomValidation("MCH_CODE_CONTRACT,MCH_CODE", "CONTRACT,REPAIR_PART_CONTRACT,MCHNAME,REPAIR_PART_NO");
        f.setMaxLength(5);
        f.setMandatory();
        f.setInsertable();

        f = headblk.addField("CONTRACT");
        f.setSize(10);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSCONTRACT: Site");
        f.setCustomValidation("CONTRACT", "COMPANY");
        f.setUpperCase();
        f.setMaxLength(5);
        f.setMandatory();

        f = headblk.addField("MCHNAME");
        f.setSize(36);
        f.setLabel("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSMCHNAME: Object Description");
        f.setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:MCH_CODE_CONTRACT,:MCH_CODE)");
        f.setMaxLength(2000);
        f.setReadOnly();

        f = headblk.addField("REPAIR_PART_NO");
        f.setSize(26);
        f.setDynamicLOV("MAINTENANCE_INV_PART_LOV","REPAIR_PART_CONTRACT CONTRACT",600,445);
        f.setLabel("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSREPAIR_PART_NO: Part No");
        f.setCustomValidation("REPAIR_PART_CONTRACT,REPAIR_PART_NO","REPAIRPARTDESCR,INVENTORYVALUE,CBACCOUNTED,ORG_CODE,ORGDESCR,ERR_DESCR,CONTRACT,COMPANY");
        f.setUpperCase();
        f.setMaxLength(25);
        f.setMandatory();

        f = headblk.addField("REPAIR_PART_CONTRACT");
        f.setSize(10);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSREPAIR_PART_CONTRACT: Site");
        f.setCustomValidation("REPAIR_PART_CONTRACT,REPAIR_PART_NO","REPAIRPARTDESCR,INVENTORYVALUE");
        f.setUpperCase();
        f.setMaxLength(5);
        f.setMandatory();

        f = headblk.addField("REPAIRPARTDESCR");
        f.setSize(36);
        f.setLabel("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSREPAIRPARTDESCR: Part Description");
        f.setFunction("Inventory_Part_API.Get_Description(:REPAIR_PART_CONTRACT,:REPAIR_PART_NO)");
        f.setReadOnly();
        f.setMaxLength(35);

        f = headblk.addField("LOT_BATCH_NO");
        f.setSize(20);
        f.setLabel("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSLOTBATCHNO: Lot Batch No");
        f.setMaxLength(20);

        f = headblk.addField("INVENTORYVALUE", "Number");
        f.setSize(12);
        f.setLabel("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSINVENTORYVALUE: Inventory Value");
        f.setFunction("''");
        f.setReadOnly();

        f = headblk.addField("CBACCOUNTED");
        f.setSize(13);
        f.setLabel("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSCBACCOUNTED: Accounted");
        f.setFunction("''");
        f.setCheckBox("FALSE,TRUE");
        f.setReadOnly();

        f = headblk.addField("REGDATE", "Datetime");
        f.setSize(25);
        f.setLabel("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSREGDATE: Planned Start");
        f.setFunction("''");
        f.setCustomValidation("REGDATE,REPAIR_PART_CONTRACT,REPAIR_PART_NO,PLAN_F_DATE","REGDATE,PLAN_F_DATE");

        f = headblk.addField("PLAN_F_DATE","Datetime");
        f.setSize(25);
        f.setLabel("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSPLAN_F_DATE: Planned Completion");
        f.setCustomValidation("PLAN_F_DATE,REGDATE","PLAN_F_DATE");
        f.setMandatory();

        f = headblk.addField("ERR_DESCR");
        f.setSize(45);
        f.setLabel("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSERR_DESCR: Directive");
        f.setMaxLength(60);
        f.setMandatory();

        f = headblk.addField("ORG_CODE");
        f.setSize(13);
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,445);
        f.setLabel("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSORG_CODE: Maintenance Organization");
        f.setCustomValidation("CONTRACT,ORG_CODE","ORGDESCR");
        f.setUpperCase();
        f.setMaxLength(8);
        f.setMandatory();

        f = headblk.addField("ORGDESCR");
        f.setSize(31);
        f.setFunction("Organization_API.Get_Description(:CONTRACT,:ORG_CODE)");
        f.setReadOnly();
        f.setMaxLength(2000);

        f = headblk.addField("REPORTED_BY");
        f.setSize(13);
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445);
        f.setLabel("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSREPORTED_BY: Reported by");
        f.setCustomValidation("COMPANY,REPORTED_BY", "REPORTED_BY_ID");
        f.setUpperCase();
        f.setMaxLength(20);
        f.setMandatory();

        f = headblk.addField("COMPANY");
        f.setHidden();
        f.setUpperCase();

        f = headblk.addField("WO_NO", "Number");
        f.setHidden();

        f = headblk.addField("WONO", "Number");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("DESCRIPTION");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("REPORTED_BY_ID");
        f.setHidden();
        f.setUpperCase();

        f = headblk.addField("FND_USER");
        f.setHidden();

        f = headblk.addField("INVENPAZEROFLAG");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("CLIENTVAL");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("NINDEX");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("REPPARTNO");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("REPAIRABLE");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("LEAD_TIME");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("ERR_DESCR_LO");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("DUMMY_RESULT");
        f.setFunction("''");
        f.setHidden();

        headblk.setView("ACTIVE_SEPARATE");
        headblk.defineCommand("ACTIVE_SEPARATE_API","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.enableCommand(headbar.SAVERETURN);
        headbar.enableCommand(headbar.CANCELNEW); 

        headbar.defineCommand(headbar.SAVERETURN,"saveReturn","checkHeadFields(-1)");
        headbar.defineCommand(headbar.CANCELNEW,"cancel");

        headtbl = mgr.newASPTable(headblk);

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
        headlay.setEditable();
        headlay.setSimple("ORGDESCR");

        headlay.defineGroup(mgr.translate("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSGRPLABEL1: Object for Repair Work Order"),"MCH_CODE,MCH_CODE_CONTRACT,MCHNAME",true,true);
        headlay.defineGroup(mgr.translate("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSGRPLABEL2: Part to Repair"),"REPAIR_PART_NO,REPAIR_PART_CONTRACT,REPAIRPARTDESCR,LOT_BATCH_NO,INVENTORYVALUE,CBACCOUNTED",true,true);
        headlay.defineGroup(mgr.translate("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSGRPLABEL3: Work Order Information"),"REGDATE,PLAN_F_DATE,ERR_DESCR,ORG_CODE,ORGDESCR,REPORTED_BY,CONTRACT",true,true);

        enableConvertGettoPost();

    }


    public void adjust()
    {
        ASPManager mgr = getASPManager();

        msk = mgr.getFormatMask("Datetime",true);

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
        return "PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSTITLE1: Create Repair Work Order for Non Serial Part";
    }

    protected String getTitle()
    {
        return "PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSTITLE: Create Repair Work Order for Non Serial Part";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        if (newWndFlag)
        {
            newWndFlag = false;

            String url = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");

            appendDirtyJavaScript("window.location ='" + url + "Navigator.page?MAINMENU=Y&NEW=Y';");

            if (mgr.isExplorer())
                appendDirtyJavaScript(" window.open('" + url + "Pcmw/CreateRepairWorkOrderForNonSerialParts.page','CreateRepairWONonSer','resizable,status,scrollbars=yes,width=796,height=625');\n");
            else
                appendDirtyJavaScript(" window.open('" + url + "Pcmw/CreateRepairWorkOrderForNonSerialParts.page','CreateRepairWONonSer','resizable,status,scrollbars=yes,width=796,height=625');\n");
        }
        else
        {
            printHiddenField("CONFIG_DATEMASK",msk);
            printHiddenField("DATE_MASK",date_mask);
            printHiddenField("__VALTEXT1","");
            printHiddenField("__PARTNO","");
            printHiddenField("__PARTDESC","");

            if (headlay.isVisible())
                appendToHTML(headlay.show());

            appendDirtyJavaScript("function validateRepairPartNo(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkRepairPartNo(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('REPAIR_PART_NO',i)=='' )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		getField_('REPAIRPARTDESCR',i).value = '';\n");
            appendDirtyJavaScript("		getField_('INVENTORYVALUE',i).value = '';\n");
            appendDirtyJavaScript("		getField_('CBACCOUNTED',i).value = '';\n");
            appendDirtyJavaScript("		return;\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript(" window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	r = __connect(\n");
            appendDirtyJavaScript("		'");
            appendDirtyJavaScript(mgr.getURL());
            appendDirtyJavaScript("?VALIDATE=REPAIR_PART_NO'\n");
            appendDirtyJavaScript("		+ '&REPAIR_PART_CONTRACT=' + URLClientEncode(getValue_('REPAIR_PART_CONTRACT',i))\n");
            appendDirtyJavaScript("		+ '&REPAIR_PART_NO=' + URLClientEncode(getValue_('REPAIR_PART_NO',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("   window.status='';\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'REPAIR_PART_NO',i,'Part No') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('REPAIRPARTDESCR',i,0);\n");
            appendDirtyJavaScript("		assignValue_('INVENTORYVALUE',i,1);\n");
            appendDirtyJavaScript("		assignValue_('CBACCOUNTED',i,2);\n");
            appendDirtyJavaScript("		assignValue_('ORG_CODE',i,3);\n");
            appendDirtyJavaScript("		assignValue_('ORGDESCR',i,4);\n");
            appendDirtyJavaScript("		assignValue_('ERR_DESCR',i,5);\n");
            appendDirtyJavaScript("		assignValue_('CONTRACT',i,6);\n");
            appendDirtyJavaScript("		assignValue_('COMPANY',i,7);\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	if (getValue_('CBACCOUNTED',i) == \"TRUE\")\n");
            appendDirtyJavaScript("		f.CBACCOUNTED.click();\n");
            appendDirtyJavaScript("}\n");

            /*appendDirtyJavaScript("function validatePlanFDate(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   regval = document.form.REGDATE.value;\n");
            appendDirtyJavaScript("   document.form.REGDATE.value= regval;\n");
            appendDirtyJavaScript("   planfdat = document.form.PLAN_F_DATE.value;\n");
            appendDirtyJavaScript("   document.form.PLAN_F_DATE.value= planfdat;\n");
            appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkPlanFDate(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('PLAN_F_DATE',i)=='' )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		getField_('PLAN_F_DATE',i).value = '';\n");
            appendDirtyJavaScript("		return;\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("   window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	r = __connect(\n");
            appendDirtyJavaScript("		'");
            appendDirtyJavaScript(mgr.getURL());
            appendDirtyJavaScript("?VALIDATE=PLAN_F_DATE'\n");
	    appendDirtyJavaScript("		+ '&PLAN_F_DATE=' + URLClientEncode(getValue_('PLAN_F_DATE',i))\n");
            appendDirtyJavaScript("		+ '&REGDATE=' + URLClientEncode(getValue_('REGDATE',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("   window.status='';\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'PLAN_F_DATE',i,'Planned Completion') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('PLAN_F_DATE',i,0);\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("	if (regval != '')\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("      if (planfdat < regval)\n");
            appendDirtyJavaScript("		{\n");
            appendDirtyJavaScript("			alert('");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSCOMPDATETHAN: Planned Completion is earlier than Planned Start."));
            appendDirtyJavaScript("');\n");
            appendDirtyJavaScript("			getField_('PLAN_F_DATE',i).value = '';\n");
            appendDirtyJavaScript("		}\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("}\n");*/

            appendDirtyJavaScript("if ('");
            appendDirtyJavaScript(finishFlag);
            appendDirtyJavaScript("'=='true')\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   alert('");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSCREATED: A repair work order has been created (&1 - &2)",wono,mgr.encodeStringForJavascript(mgr.readValue("ERR_DESCR"))));
            appendDirtyJavaScript("');\n");
            appendDirtyJavaScript("   window.location = \"../Default.page\";\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("if ('");
            appendDirtyJavaScript(generatedFlag);
            appendDirtyJavaScript("' == \"TRUE\")\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   alert('");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWCREATEREPAIRWORKORDERFORNONSERIALPARTSCREATED: A repair work order has been created (&1 - &2)",wono,mgr.encodeStringForJavascript(mgr.readValue("ERR_DESCR"))));
            appendDirtyJavaScript("');\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("if ('");
            appendDirtyJavaScript(closeFlag);
            appendDirtyJavaScript("' == \"TRUE\")\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   window.close();\n");
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

            appendDirtyJavaScript(" function validateRegdate(i) \n");
            appendDirtyJavaScript("{ \n");
            appendDirtyJavaScript("  if( getRowStatus_('HEAD',i)=='QueryMode__' ) return; \n");
            appendDirtyJavaScript("     setDirty(); \n");
            appendDirtyJavaScript("  if( !checkRegdate(i) ) return; \n");
            appendDirtyJavaScript("	 if( getValue_('REGDATE',i)=='' ) \n");
            appendDirtyJavaScript("  { \n");
            appendDirtyJavaScript("     getField_('REGDATE',i).value = ''; \n");
            appendDirtyJavaScript("     getField_('PLAN_F_DATE',i).value = ''; \n");
            appendDirtyJavaScript("     return; \n");
            appendDirtyJavaScript("  } \n");     
            appendDirtyJavaScript("     window.status='Please wait for validation'; \n");
            appendDirtyJavaScript("  r = __connect( \n");
            appendDirtyJavaScript("		'");
            appendDirtyJavaScript(mgr.getURL());
            appendDirtyJavaScript("?VALIDATE=REGDATE'\n");    
            appendDirtyJavaScript("  + '&REGDATE=' + URLClientEncode(getValue_('REGDATE',i)) \n");
            appendDirtyJavaScript("  + '&REPAIR_PART_CONTRACT=' + URLClientEncode(getValue_('REPAIR_PART_CONTRACT',i)) \n");
            appendDirtyJavaScript("  + '&REPAIR_PART_NO=' + URLClientEncode(getValue_('REPAIR_PART_NO',i)) \n");
            appendDirtyJavaScript("  + '&PLAN_F_DATE=' + URLClientEncode(getValue_('PLAN_F_DATE',i)) \n");
            appendDirtyJavaScript("  + '&DATE_MASK=' + URLClientEncode(getValue_('DATE_MASK',i)) \n");
	    appendDirtyJavaScript("  ); \n");
            appendDirtyJavaScript("  window.status=''; \n");
            appendDirtyJavaScript("  if( checkStatus_(r,'REGDATE',i,'Planned Start') ) \n");
            appendDirtyJavaScript("  { \n");
            appendDirtyJavaScript("    assignValue_('REGDATE',i,0); \n");
            appendDirtyJavaScript("    assignValue_('PLAN_F_DATE',i,1); \n");
            appendDirtyJavaScript("  } \n");
            appendDirtyJavaScript("  else\n");
            appendDirtyJavaScript("  {\n");
            appendDirtyJavaScript("    document.form.REGDATE.value = '';\n");
            appendDirtyJavaScript("  }\n");
            appendDirtyJavaScript("} \n");

            appendDirtyJavaScript("function validatePlanFDate(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("	if( !checkPlanFDate(i) ) return;\n");
            appendDirtyJavaScript("	if( getValue_('PLAN_F_DATE',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	if( getValue_('REGDATE',i).indexOf('%') != -1) return;\n");
            appendDirtyJavaScript("	window.status='Please wait for validation';\n");
            appendDirtyJavaScript("	 r = __connect(\n");
            appendDirtyJavaScript("		APP_ROOT+ 'pcmw/CreateRepairWorkOrderForNonSerialParts.page'+'?VALIDATE=PLAN_F_DATE'\n");
            appendDirtyJavaScript("		+ '&PLAN_F_DATE=' + URLClientEncode(getValue_('PLAN_F_DATE',i))\n");
            appendDirtyJavaScript("		+ '&REGDATE=' + URLClientEncode(getValue_('REGDATE',i))\n");
            appendDirtyJavaScript("		);\n");
            appendDirtyJavaScript("	window.status='';\n");
            appendDirtyJavaScript("\n");
            appendDirtyJavaScript("	if( checkStatus_(r,'PLAN_F_DATE',i,'Planned Completion') )\n");
            appendDirtyJavaScript("	{\n");
            appendDirtyJavaScript("		assignValue_('PLAN_F_DATE',i,0);\n");
            appendDirtyJavaScript("	}\n");
            appendDirtyJavaScript("  else\n");
            appendDirtyJavaScript("  {\n");
            appendDirtyJavaScript("    document.form.PLAN_F_DATE.value = '';\n");
            appendDirtyJavaScript("  }\n");
            appendDirtyJavaScript("}\n");
        }
    }
}
