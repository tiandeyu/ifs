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
*  File        : StdJobsByCategoryDlg.java 
*  Created     : 041129  NIJALK (AMEC112 - Job Program)
*  Modified    :
*  NIJALK  041220 Modified predefine().
*  NIJALK  041231 Call 120861: Modified predefine(),printContents(),query(). 
*                 Added methods okFindITEM(),cancelFindITEM().
*  NIJALK  050328 Modified run(),ret().
*  THWILK  060126 Corrected localization errors.
*  AMNILK  070713 Eliminated SQL Injections.
*  AMNILK  070727 Eliminated SQLInjections Security Vulnerability.
*  CHANLK  080229 Bug 71736, Corrected.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class StdJobsByCategoryDlg extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.StdJobsByCategoryDlg");

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
    private ASPField f;

    private String val = "";
    private String txt = "";
    private String job_prog_id;
    private String job_prog_revision;
    private String job_prog_contract ;
    private String std_job_simulation_id;
    private String frmname;
    private String qrystr;
    private boolean win_cls;
    private boolean show_item = false;
    private boolean refreshMain;
    private int totRows;

    //===============================================================
    // Construction 
    //===============================================================
    public StdJobsByCategoryDlg(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();

        job_prog_id = "";
        job_prog_revision = "";
        job_prog_contract = "";
        std_job_simulation_id = "";

        trans = mgr.newASPTransactionBuffer();
        fmt = mgr.newASPHTMLFormatter();
        ctx = mgr.getASPContext();

        job_prog_id = ctx.readValue("JOB_PROGRAM_ID","");
        job_prog_revision = ctx.readValue("JOB_PROGRAM_REVISION",""); 
        job_prog_contract = ctx.readValue("JOB_PROGRAM_CONTRACT","");
        std_job_simulation_id = ctx.readValue("STDJOBSIMULATIONID","");
        win_cls = ctx.readFlag("WINCLS",win_cls);
        show_item = ctx.readFlag("SHOWITEM",show_item);
        refreshMain = ctx.readFlag("REFRESHMAIN",false);
        frmname= ctx.readValue("FRMNAME","");
        qrystr = ctx.readValue("QRYSTR","");

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("JOB_PROGRAM_ID")) && !mgr.isEmpty(mgr.getQueryStringValue("JOB_PROGRAM_REVISION")) && !mgr.isEmpty(mgr.getQueryStringValue("JOB_PROGRAM_CONTRACT")))
        {
            job_prog_id = mgr.readValue("JOB_PROGRAM_ID");
            job_prog_revision = mgr.readValue("JOB_PROGRAM_REVISION");
            job_prog_contract = mgr.readValue("JOB_PROGRAM_CONTRACT");
            qrystr = mgr.readValue("QRYSTR");     
            frmname = mgr.readValue("FRMNAME");
            std_job_simulation_id = mgr.readValue("STDJOBSIMULATIONID");
        }
        else if (mgr.buttonPressed("QUERY"))
            query();
        else if (mgr.buttonPressed("RET"))
            ret();
        else if (mgr.buttonPressed("CANCEL"))
            cancel();

        adjust();

        ctx.writeValue("JOB_PROGRAM_ID",job_prog_id);
        ctx.writeValue("JOB_PROGRAM_REVISION",job_prog_revision);
        ctx.writeValue("JOB_PROGRAM_CONTRACT",job_prog_contract);
        ctx.writeFlag("WINCLS",win_cls);
        ctx.writeFlag("SHOWITEM",show_item);
        ctx.writeFlag("REFRESHMAIN",refreshMain);
        ctx.writeValue("FRMNAME",frmname);
        ctx.writeValue("QRYSTR",qrystr);
        ctx.writeValue("STDJOBSIMULATIONID",std_job_simulation_id);
    }


//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();
    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------


//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------
    public void okFindITEM()
    {
        ASPManager mgr = getASPManager();

        headset.clear();

        q = trans.addQuery(itemblk);
        q.addWhereCondition("Standard_Job_API.Get_Std_Job_State(STD_JOB_ID,CONTRACT,STD_JOB_REVISION) = 'Active'");
        q.addWhereCondition("Standard_Job_API.Is_Valid_Std_Job(STD_JOB_ID,CONTRACT,STD_JOB_REVISION) = 'TRUE'");
        q.setOrderByClause("STD_JOB_ID,STD_JOB_REVISION,CONTRACT");
        q.includeMeta("ALL");

        mgr.submit(trans);

        totRows = itemset.countRows();
        show_item = true;

        if (itemset.countRows()==0)
        {
            mgr.showAlert(mgr.translate("PCMWSTDJOBSBYCATEGORYDLGNODATA: No Data Found"));
            itemset.clear(); 
            show_item = false;
        }
    }

    public void cancelFindITEM()
    {
        itemlay.setDefaultLayoutMode(itemlay.getHistoryMode());

        totRows = itemset.countRows();
        show_item = true;

        if (itemset.countRows()== 0)
        {
            itemset.clear();
            show_item = false;
        }   
    }

    public void query()
    {
        ASPManager mgr = getASPManager();

        String sObjectCategory = "";
        String sObjectType = "";
        String sTypeDesignation = "";
        String sPartNo = "";
        String sWhere = "";
        String obj_cat = mgr.readValue("OBJ_CAT");
        String obj_type = mgr.readValue("OBJ_TYPE");
        String type_desg = mgr.readValue("TYPE_DESG");
        String part_no = mgr.readValue("PART_NO");
        trans.clear();

        if (!mgr.isEmpty(obj_cat) || !mgr.isEmpty(obj_type) || !mgr.isEmpty(type_desg) || !mgr.isEmpty(part_no))
        {
            if (!mgr.isEmpty(obj_cat))
            {
                sObjectCategory = "ObjectCategory";
            }
            if (!mgr.isEmpty(obj_type))
            {
                sObjectType = "ObjectType";
            }
            if (!mgr.isEmpty(type_desg))
            {
                sTypeDesignation = "TypeDesignation";
            }
            if (!mgr.isEmpty(part_no))
            {
                sPartNo = "PartNo";
            }

            cmd = trans.addCustomCommand("GETWHERE","Std_Job_Category_API.Std_Jobs_By_Category");
            cmd.addParameter("S_WHERE","");
            cmd.addParameter("S_OBJ_CAT",sObjectCategory);
            cmd.addParameter("OBJ_CAT",mgr.readValue("OBJ_CAT"));
            cmd.addParameter("S_OBJ_TYPE",sObjectType);
            cmd.addParameter("OBJ_TYPE",mgr.readValue("OBJ_TYPE"));
            cmd.addParameter("S_TYPE_DESG",sTypeDesignation);
            cmd.addParameter("TYPE_DESG",mgr.readValue("TYPE_DESG"));
            cmd.addParameter("S_PART_NO",sPartNo);
            cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
            cmd.addParameter("JOB_PROGRAM_CONTRACT",job_prog_contract);

            trans = mgr.perform(trans);
            sWhere = trans.getValue("GETWHERE/DATA/S_WHERE");
            trans.clear();
            headset.clear();
            itemset.clear(); 
            
//          Bug 71736, start
            if (sWhere != null){

               q = trans.addEmptyQuery(itemblk);  
   
   	    //SQLInjection_Safe AMNILK  20070713
               q.addWhereCondition(sWhere);
               q.setOrderByClause("STD_JOB_ID,STD_JOB_REVISION,CONTRACT");
               q.includeMeta("ALL");
   
               mgr.submit(trans);
   
               totRows = itemset.countRows();
   
               ASPBuffer temp = mgr.newASPBuffer();
               temp.setFieldItem("OBJ_CAT",obj_cat);
               temp.setFieldItem("OBJ_TYPE",obj_type);
               temp.setFieldItem("TYPE_DESG",type_desg);
               temp.setFieldItem("PART_NO",part_no);
               headset.addRow(temp);
            }
//          Bug 71736, start

        }
        else
        {
            itemlay.setDefaultLayoutMode(itemlay.FIND_LAYOUT);
            headset.clear();
        }  

        show_item = true;
    }

    public void cancel()
    {
        win_cls = true;
    }

    public void ret()
    {
        ASPManager mgr = getASPManager();
        int count =0;

        if (itemlay.isMultirowLayout())
        {
            itemset.storeSelections();
            itemset.setFilterOn();
            count = itemset.countSelectedRows();
        }
        else
        {
            itemset.unselectRows();
            count = 1;
        }

        if ((itemlay.isSingleLayout()) || (itemlay.isMultirowLayout() && count>0))
        {
            for (int i = 0;i < count;i++)
            {
                trans.clear();

                if ("jobProgram".equals(frmname))
                {
                    cmd = trans.addCustomCommand("INSERTJOB","Standard_Job_Program_API.Insert_Std_Job"); 
                    cmd.addParameter("EXISTS","");
                    cmd.addParameter("JOB_PROGRAM_ID",job_prog_id);
                    cmd.addParameter("JOB_PROGRAM_REVISION",job_prog_revision);
                    cmd.addParameter("JOB_PROGRAM_CONTRACT",job_prog_contract);
                    cmd.addParameter("STD_JOB_ID",itemset.getValue("STD_JOB_ID"));
                    cmd.addParameter("STD_JOB_REVISION",itemset.getValue("STD_JOB_REVISION"));
                    cmd.addParameter("CONTRACT",itemset.getValue("CONTRACT"));
                }
                else if ("jobProgToObjectItems".equals(frmname))
                {
                    cmd = trans.addCustomCommand("INSERTJOB","Simulated_Std_Job_API.Insert_Std_Jobs"); 
                    cmd.addParameter("EXISTS","");
                    cmd.addParameter("JOB_PROGRAM_ID",job_prog_id);
                    cmd.addParameter("JOB_PROGRAM_REVISION",job_prog_revision);
                    cmd.addParameter("JOB_PROGRAM_CONTRACT",job_prog_contract);
                    cmd.addParameter("STD_JOB_ID",itemset.getValue("STD_JOB_ID"));
                    cmd.addParameter("STD_JOB_REVISION",itemset.getValue("STD_JOB_REVISION"));
                    cmd.addParameter("CONTRACT",itemset.getValue("CONTRACT"));
                    cmd.addParameter("STD_JOB_SIM_ID",std_job_simulation_id);
                }

                trans = mgr.perform(trans);

                if ("TRUE".equals(trans.getValue("INSERTJOB/DATA/EXISTS")))
                {
                    mgr.showAlert(mgr.translate("PCMWSTDJOBBYCATEGORYADDED: Revision '"+job_prog_revision+"' of Standard Job '"+job_prog_id+"' in Site '"+job_prog_contract+"' is already connected to this Job Program, and can not be added again!"));
                }

                if (itemlay.isMultirowLayout())
                    itemset.next();
            }

            if (itemlay.isMultirowLayout())
                itemset.setFilterOff();

            win_cls = true;
            refreshMain = true;
        }
        else
            mgr.showAlert(mgr.translate("PCMWSTDJOBSBYCATEGORYNOSELROWS: One or more Standard Job(s) should be selected to perform this action."));
    }

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

        f = headblk.addField("JOB_PROGRAM_ID");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("JOB_PROGRAM_REVISION");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("JOB_PROGRAM_CONTRACT");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("OBJ_CAT");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGOBJCAT: Object Category");
        f.setDynamicLOV("EQUIPMENT_OBJ_CATEGORY");
        f.setValidateFunction("validate");
        f.setFunction("''");

        f = headblk.addField("OBJ_TYPE");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGOBJTYPE: Object Type");
        f.setDynamicLOV("EQUIPMENT_OBJ_TYPE");
        f.setValidateFunction("validate");
        f.setFunction("''");

        f = headblk.addField("TYPE_DESG");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGTYPEDESG: Type Designation");
        f.setDynamicLOV("TYPE_DESIGNATION");
        f.setValidateFunction("validate");
        f.setFunction("''");

        f = headblk.addField("PART_NO");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGPARTNO: Part No.");
        f.setDynamicLOV("PART_CATALOG");
        f.setValidateFunction("validate");
        f.setFunction("''");

        f = headblk.addField("S_OBJ_CAT");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("S_OBJ_TYPE");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("S_TYPE_DESG");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("S_PART_NO");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("S_WHERE");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("EXISTS");
        f.setFunction("''");
        f.setHidden();   

        f = headblk.addField("STD_JOB_SIM_ID");
        f.setFunction("''");
        f.setHidden();   

        headblk.setView("DUAL");

        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.setBorderLines(false,true);
        headbar.disableModeLabel();

        headbar.disableCommand(headbar.NEWROW);

        headtbl = mgr.newASPTable(headblk);
        headtbl.setWrap();

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
        headlay.setDialogColumns(2); 
        headlay.setEditable();

        //-------------itemblk----------------------------------------------------------------

        itemblk = mgr.newASPBlock("ITEM");

        f = itemblk.addField("OBJID");
        f.setHidden();

        f = itemblk.addField("OBJVERSION");
        f.setHidden();

        f = itemblk.addField("COMPANY");
        f.setHidden();

        f = itemblk.addField("CONTRACT");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBCONTRACT: Site");
        f.setReadOnly();
        f.setUpperCase();
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV");
        f.setSize(10);

        f = itemblk.addField("STD_JOB_ID");    
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBID: Standard Job ID");
        f.setReadOnly();
        f.setLOV("StandardJobLov1.page","CONTRACT",600,450);
        f.setUpperCase();
        f.setSize(15);

        f = itemblk.addField("STD_JOB_REVISION");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBREVISION: Revision");
        f.setReadOnly();
        f.setUpperCase();
        f.setLOV("StandardJobLov1.page","STD_JOB_ID,CONTRACT",600,450);
        f.setSize(10);

        f = itemblk.addField("STDJOBWORK_DESC");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBWORKDESC: Work Description");
        f.setFunction("STANDARD_JOB_API.Get_Work_Description(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");
        f.setReadOnly();
        f.setSize(20);

        f = itemblk.addField("STD_JOB_DEFINITION");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBDEFINITION: Description");
        f.setFunction("STANDARD_JOB_API.Get_Definition(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");
        f.setReadOnly();
        f.setSize(20);

        f = itemblk.addField("STD_JOB_STATUS");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBSTATUS: Status");
        f.setFunction("STANDARD_JOB_API.Get_Status(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");
        f.setReadOnly();
        f.setSelectBox();
        f.enumerateValues("STANDARD_JOB_API.Enumerate_States__","");
        f.setSize(10);

        f = itemblk.addField("STD_JOB_TYPE");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBTYPE: Type");
        f.setSelectBox();
        f.enumerateValues("STANDARD_JOB_TYPE_API");
        f.setFunction("STANDARD_JOB_API.Get_Standard_Job_Type(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");
        f.setReadOnly();
        f.setSize(15);

        f = itemblk.addField("STD_JOB_TYPE_ID");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBTYPEID: Std Job Type");
        f.setFunction("STANDARD_JOB_API.Get_Type_Id(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");
        f.setReadOnly();
        f.setDynamicLOV("STD_JOB_TYPE",600,450);
        f.setSize(15);

        f = itemblk.addField("STD_JOB_TYPE_DESC");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBTYPEDESC: Description");
        f.setFunction("Std_Job_Type_API.Get_Description(STANDARD_JOB_API.Get_Type_Id(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION))");
        f.setReadOnly();
        f.setSize(20);

        f = itemblk.addField("STD_JOB_ACTION_CODE");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBACTIONCODE: Action");
        f.setFunction("STANDARD_JOB_API.Get_Action_Code_Id(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");
        f.setReadOnly();
        f.setDynamicLOV("MAINTENANCE_ACTION",600,450);
        f.setSize(10);

        f = itemblk.addField("STD_JOB_ACT_DESC");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBACTDESC: Action Description");
        f.setFunction("MAINTENANCE_ACTION_API.Get_Description(STANDARD_JOB_API.Get_Action_Code_Id(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION))");
        f.setReadOnly();
        f.setSize(15);

        f = itemblk.addField("STD_JOB_ORG_CODE");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGMAINTORG: Maint. Org.");
        f.setFunction("STANDARD_JOB_API.Get_Org_Code(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");
        f.setReadOnly();
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,450);
        f.setSize(10);

        f = itemblk.addField("STD_JOB_WORK_TYPE");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBWORKTYPE: Work Type");
        f.setFunction("STANDARD_JOB_API.Get_Work_Type_Id(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");
        f.setReadOnly();
        f.setDynamicLOV("WORK_TYPE",600,450);
        f.setSize(10);

        f = itemblk.addField("STD_JOB_OP_STATUS");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBOPSTATUS: Operational Status");
        f.setFunction("STANDARD_JOB_API.Get_Op_Status_Id(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");
        f.setReadOnly();
        f.setSize(10);
        f.setDynamicLOV("OPERATIONAL_STATUS",600,450);
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_PRIORITY");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBPRIORITY: Priority");
        f.setFunction("STANDARD_JOB_API.Get_Op_Status_Id(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");
        f.setReadOnly();
        f.setSize(10);
        f.setDynamicLOV("MAINTENANCE_PRIORITY",600,450);
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_EXEC_TIME");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBEXECTIME: Execution Time");
        f.setFunction("STANDARD_JOB_API.Get_Execution_Time(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");
        f.setReadOnly();
        f.setSize(10);
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_SIGN");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBSIGN: Planned By");
        f.setFunction("STANDARD_JOB_API.Get_Signature(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION) ");
        f.setReadOnly();
        f.setSize(15);
        f.setLOV("../mscomw/MaintEmployeeLov.page","COMPANY",600,450);
        f.setLOVProperty("TITLE","PCMWSTDJOBSBYCATEGORYDLGLOVTITLE: List of Employee");
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_CREATED_BY");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBCREATEDBY: Created By");
        f.setFunction("STANDARD_JOB_API.Get_Created_By(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION) ");
        f.setReadOnly();
        f.setSize(15);
        f.setLOV("../mscomw/MaintEmployeeLov.page","COMPANY",600,450);
        f.setLOVProperty("TITLE","PCMWSTDJOBSBYCATEGORYDLGLOVTITLE: List of Employee");
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_CREATION_DATE","Datetime");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGCREATIONDATE: Creation Date");
        f.setFunction("STANDARD_JOB_API.Get_Creation_Date(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION) ");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_CHANGED_BY");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBCHANGEDBY: Changed By");
        f.setFunction("STANDARD_JOB_API.Get_Changed_By(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION) ");
        f.setReadOnly();
        f.setSize(15);
        f.setLOV("../mscomw/MaintEmployeeLov.page","COMPANY",600,450);
        f.setLOVProperty("TITLE","PCMWSTDJOBSBYCATEGORYDLGLOVTITLE: List of Employee");
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_LAST_CHANGED");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBLASTCHANGED: Last Changed");
        f.setFunction("STANDARD_JOB_API.Get_Last_Changed(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION) ");
        f.setReadOnly();
        f.setSize(15);
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_START_VALUE");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBSTARTVALUE: Start Value");
        f.setFunction("STANDARD_JOB_API.Get_Start_Value(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION) ");
        f.setReadOnly();
        f.setSize(15);
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_INTERVAL");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBINTERVAL: Interval");
        f.setFunction("STANDARD_JOB_API.Get_Interval(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION) ");
        f.setReadOnly();
        f.setSize(15);
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_INTERVAL_UNIT");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBINTERVALUNIT: Interval Unit");
        f.setFunction("Pm_Interval_Unit_API.Decode(STANDARD_JOB_API.Get_Pm_Interval_Unit(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION))");
        f.setReadOnly();
        f.setSize(15);
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_CALL_CODE");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBCALLCODE: Event");
        f.setFunction("STANDARD_JOB_API.Get_Call_Code(:STD_JOB_ID,:STD_JOB_REVISION,:CONTRACT)");
        f.setReadOnly();
        f.setSize(15);
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_START_CALL");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBSTARTCALL: Start");
        f.setFunction("STANDARD_JOB_API.Get_Start_Call(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION) ");
        f.setReadOnly();
        f.setSize(15);
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_CALL_INTERVAL");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBCALLINTERVAL: Interval");
        f.setFunction("STANDARD_JOB_API.Get_Call_Interval(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION) ");
        f.setReadOnly();
        f.setSize(15);
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_VALID_TO","Datetime");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGVALIDTO: Valid To");
        f.setFunction("STANDARD_JOB_API.Get_Valid_To(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_VALID_FROM","Datetime");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGVALIDFROM: Valid From");
        f.setFunction("STANDARD_JOB_API.Get_Valid_From(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_CAL_ID");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBCALID: Calendar ID");
        f.setFunction("STANDARD_JOB_API.Get_Calendar_Id(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");
        f.setReadOnly();
        f.setSize(10);
        f.setDynamicLOV("WORK_TIME_CALENDAR",600,450);
        f.setDefaultNotVisible();

        f = itemblk.addField("STD_JOB_CAL_DESC");
        f.setLabel("PCMWSTDJOBSBYCATEGORYDLGSTDJOBCALDESC: Description");
        f.setFunction("Work_Time_Calendar_API.Get_Description(STANDARD_JOB_API.Get_Calendar_Id(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION))");
        f.setReadOnly();
        f.setSize(20);
        f.setDefaultNotVisible();
        itemblk.setView("STANDARD_JOB");

        itemset = itemblk.getASPRowSet();

        itembar = mgr.newASPCommandBar(itemblk);
        itembar.disableModeLabel();

        itembar.disableCommand(itembar.FIND);
        itembar.disableCommand(itembar.NEWROW);
        itembar.disableCommand(itembar.COUNTFIND);
        itembar.disableMultirowAction();

        itembar.defineCommand(itembar.OKFIND,"okFindITEM");
        itembar.defineCommand(itembar.CANCELFIND,"cancelFindITEM");

        itemtbl = mgr.newASPTable(itemblk);
        itemtbl.enableRowSelect();
        itemtbl.setWrap();

        itemlay = itemblk.getASPBlockLayout();
        itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);
        itemlay.setDialogColumns(2); 
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();
    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWSTDJOBSBYCATEGORYDLGTITLE: Add Standard Jobs by Category";
    }

    protected String getTitle()
    {
        return "PCMWSTDJOBSBYCATEGORYDLGTITLE: Add Standard Jobs by Category";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.show());
        appendToHTML("<table border=0 cellspacing=0 cellpadding=1 width= '100%'>\n");
        appendToHTML("<tr align=\"right\">\n");
        appendToHTML("<td width=\"100%\" align=\"right\">\n");
        appendToHTML(fmt.drawSubmit("QUERY",mgr.translate("PCMWSTDJOBSBYCATEGORYDLGQUERY:  Query "),"QUERY"));
        appendToHTML("&nbsp;&nbsp;"); 
        appendToHTML(fmt.drawSubmit("RET",mgr.translate("PCMWSTDJOBSBYCATEGORYDLGRETURN:  Return "),"RET"));
        appendToHTML("&nbsp;&nbsp;");
        appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWSTDJOBSBYCATEGORYDLGCANCEL:  Cancel "),"CANCEL"));
        appendToHTML("</td>\n</tr>\n</table>\n");
        if (show_item)
        {
            appendToHTML(itemlay.show());
        }

        appendDirtyJavaScript("if (");
        appendDirtyJavaScript(win_cls);		//XSS_Safe AMNILK 20070726
        appendDirtyJavaScript(")\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("  window.close();\n");
        appendDirtyJavaScript("}\n");

        if (refreshMain)
        {
            ctx.writeFlag("REFRESHMAIN",false);
            appendDirtyJavaScript("  window.open('");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(qrystr));	//XSS_Safe AMNILK 20070726
            appendDirtyJavaScript("' + \"&ADDSTDJOB=1\",'");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(frmname));	//XSS_Safe AMNILK 20070726
            appendDirtyJavaScript("',\"\");\n");
        }

        // "Query" button is disabled when itemlay is in find layout.
        appendDirtyJavaScript("if (ITEM_IN_FIND_MODE == true)\n");
        appendDirtyJavaScript("   f.QUERY.disabled = true;\n");

        // "Return" button is disabled when loading the page
        appendDirtyJavaScript("f.RET.disabled = true;\n");

        // this java script fuction is replaced to enable/disable "Return" button when selecting 
        // rows from itemset.
        appendDirtyJavaScript("function CCA(CB,row_no)\n");
        appendDirtyJavaScript("{ \n");
        appendDirtyJavaScript("     cca = true;\n");
        appendDirtyJavaScript("     if (CB.checked)\n");
        appendDirtyJavaScript("         highLight(CB,row_no);\n");
        appendDirtyJavaScript("     else\n");
        appendDirtyJavaScript("         downLight(CB,row_no);\n");
        appendDirtyJavaScript("     var isSelected = '';\n");
        appendDirtyJavaScript("     if (" + totRows + " > 1)\n");
        appendDirtyJavaScript("     {\n");
        appendDirtyJavaScript("         for (var i = 0; i <" + totRows + "; i++)\n");
        appendDirtyJavaScript("             { \n");
        appendDirtyJavaScript("                 if (f.__SELECTED2[i].checked)\n");
        appendDirtyJavaScript("                     isSelected = 'TRUE';\n");
        appendDirtyJavaScript("             } \n");
        appendDirtyJavaScript("     }\n");
        appendDirtyJavaScript("     else if (" + totRows + " == 1)\n");
        appendDirtyJavaScript("     {\n");
        appendDirtyJavaScript("         if (CB.checked)\n");
        appendDirtyJavaScript("              isSelected = 'TRUE';\n");
        appendDirtyJavaScript("     }\n");
        appendDirtyJavaScript("     if (isSelected == 'TRUE')\n");
        appendDirtyJavaScript("         f.RET.disabled = false;\n");
        appendDirtyJavaScript("     else\n");
        appendDirtyJavaScript("         f.RET.disabled = true;\n");
        appendDirtyJavaScript("} \n");
    }
}

