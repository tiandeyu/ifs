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
*  File        : DelimitationOrderTab.java 
*  Created     : ASP2JAVA Tool  010219
*  Modified    :
*  CHCRLK  010808  Modified methods print() and printSigns().
*  ARWILK  010904  Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction(). 
*                  (Caused when cancelNew is performed after saveNew)
*  CHCRLK  010910  Changed the deleteRow() method.
*  CHCRLK  011005  Modified all the state changing methods.
*  INROLK  011010  Status not applicable are removed in the single row mode. and added mgr.createSearchURL(headblk) to add to links. call ids 70151 and 70396  
*  VAGULK  011018  Added Security Handling for Actions
*  SHAFLK  021108  Bug Id 34064,Changed methods print & printSign.    
*  GACOLK  021204  Set Max Length of MCH_CODE, ITEM1_MCH_CODE to 100
*  BUNILK  021210  Merged with 2002-3 SP3
*  Chamlk  031023  Modified function validate() and preDefine() to fetch correct values for fields ELECTRICAL_SECURITY_LEADER_ID,
*                  APPROVED_BY_ID and COMPANY.
*  THWILK  031024  Call ID 109046, Overide the javascript function lovItem1PositionId for the LOV of ITEM1_POSITION_ID to get the values filetered properely.
*  ChAmlk  031106  Modified function createReEst() to allow the creation of reestablishment.
*  ARWILK  031216  Edge Developments - (Replaced blocks with tabs, Replaced links with RMB's, Removed clone and doReset Methods)
*  ARWILK  031229  Edge Developments - (Replaced links with multirow RMB's)
*  SAPRLK  040224  Web Alignmnt - Added multirow action to the head table, remove unnecessary method calls for hidden fields, 
*                  change of conditional code in validate method.
*  ARWILK  041001  Call ID 118336, createFromIsolationTemplate was set to popup even when there are no records.
*  NEKOLK  050505  AMUT115:Isolation and Permits.
*  NEKOLK  050607  AMUT115:Isolation and Permits added isolation_id,description col.
*  NEKOLK  050803  Changed ITEM2 view.
*  NEKOLK  050811  B 126182.Rename the RMB cancel to Cancelled
*  NEKOLK  050812  B 126319. made changes in preDefine().
*  NEKOLK  050812  B 126331.  made changes in preDefine().
*  NEKOLK  050812  B 125511. set Prepared by as mandatory fld.
*  NIJALK  050905  B126317: Modified function perform().
*  NIJALK  050905  B126330: Added command valid conditions to "Create Reestablishment".
*  NIJALK  050913  B126330: Modified activateEstablishmentInst(),activateReestablishmentInst().
*  NIJALK  051003  Modified duplicated tags in preDefine().
*  NIJALK  051007  B127673: Modified okFind().
*  NIJALK  051007  B127706: Added function saveReturn().
*  AMNILK  051101  Call Id: Modified preDefine().Made Isolation Type Insertable but not Editable.
*  RANFLK  060313  Call Id; 137138 Remove 'setHilite()' from fields in preDefine method 
*  THWILK  060313  Call 137155, Modified okFind().
*  THWILK  060313  Call 137136, Modified predefine().
*  THWILK  060315  Call 137292, Modified predefine().
*  THWILK  060315  Call 137289, Modified predefine().
*  NEKOLK  060315  Call 137228, Added lovuserwhere to DELIMITATION_ID.
*  THWILK  060315  Call 137288, Modified predefine() and run().
*  THWILK  060316  Call 137291, Added verifyOperabilityConfirm(),javascript code and modified 
*                  verifyOperability(),predefine() and run().
*  RANFLK  060316  Call 137141 Modified adjust()
*  SULILK  060317  Call 137287: Modified startPreparation(),preparation(),revise(),approvePreparation(),startEstablishment(),
                                completeEstablishment(),reopen(),startReestablishment(),completeReestablishment(),
                                verifyOperabilityConfirm(),historical().
*  JEWILK  060803  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged Bug Id: 58214.
*  DIAMLK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMDILK  060905  Merged with the Bug Id 58216
* ----------------- APP7.5 SP2 -----------------------------------------------------------------------
*  NIJALK  080314  Bug 72024, Modified validate(), preDefine() and printContents().
* ----------------------------------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class DelimitationOrderTab extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.DelimitationOrderTab");

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
    private ASPBlock itemblk0;
    private ASPRowSet itemset0;
    private ASPCommandBar itembar0;
    private ASPTable itemtbl0;
    private ASPBlockLayout itemlay0;
    private ASPBlock itemblk1;
    private ASPRowSet itemset1;
    private ASPCommandBar itembar1;
    private ASPTable itemtbl1;
    private ASPBlockLayout itemlay1;
    private ASPBlock itemblk2;
    private ASPRowSet itemset2;
    private ASPCommandBar itembar2;
    private ASPTable itemtbl2;
    private ASPBlockLayout itemlay2;
    private ASPBlock itemblk3;
    private ASPRowSet itemset3;
    private ASPCommandBar itembar3;
    private ASPTable itemtbl3;
    private ASPBlockLayout itemlay3;
    private ASPBlock itemblk4;
    private ASPRowSet itemset4;
    private ASPCommandBar itembar4;
    private ASPTable itemtbl4;
    private ASPBlockLayout itemlay4;
    private ASPBlock b;
    private ASPPage page;
    private ASPBlock eventblk;
    private ASPRowSet eventset;
    // 031216  ARWILK  Begin  (Replace blocks with tabs)
    private ASPTabContainer tabs;
    // 031216  ARWILK  End  (Replace blocks with tabs)

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private ASPTransactionBuffer trans1;
    private boolean satis;
    private boolean isFind;
    private boolean comBarAct;
    private int currrow;
    private ASPCommand cmd;
    private ASPBuffer psdateBuff;
    private ASPBuffer pfdateBuff;
    private ASPBuffer data;
    private ASPQuery q;
    private int headrowno;
    private String attr3;
    private String attr4;
    private String result_key;
    private String existReest;
    private ASPBuffer keys;
    private String fldTitleElectricalSecurityLeader;
    private String fldTitleApprovedby;
    private String fldTitlePreparedby0;
    private String fldTitlePreparedby1;
    private String fldTitlePreparedby2;
    private String lovTitleElectricalSecurityLeader;
    private String lovTitleApprovedby;
    private String lovTitlePreparedby0;
    private String lovTitlePreparedby1;
    private String lovTitlePreparedby2;
    private String root_path; 
    //================================================================================
    // ASP2JAVA WARNING: Types of the following variables are not identified by
    // the ASP2JAVA converter. Define them and remove these comments.
    //================================================================================
    private String txt; 
    private double newDelNo;  
    private double DelOrderNo;
    private String attr1;  
    private String attr2; 
    private boolean chksec;
    private boolean enable1;
    private boolean enable2;
    private boolean enable3;
    // 031216  ARWILK  Begin  (Replace links with RMB's)
    private boolean bOpenNewWindow;
    private String urlString;
    private String newWinHandle;
    private String hasWo;

    // 031216  ARWILK  End  (Replace links with RMB's)

    //===============================================================
    // Construction 
    //===============================================================
    public DelimitationOrderTab(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();

        trans = mgr.newASPTransactionBuffer();
        trans1 = mgr.newASPTransactionBuffer();
        page = mgr.getASPPage ();
        ctx = mgr.getASPContext();
        fmt = mgr.newASPHTMLFormatter();

        satis = ctx.readFlag("SATIS",true);
        isFind = ctx.readFlag("ISFIND",false);
        comBarAct = ctx.readFlag("COMBARACT",false);
        enable1 = ctx.readFlag("ENABLE1",false);
        enable2 = ctx.readFlag("ENABLE2",false);
        enable3 = ctx.readFlag("ENABLE3",false);
        root_path = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");
        if (mgr.commandBarActivated())
        {
            comBarAct = true;
            eval(mgr.commandBarFunction());
            if ("ITEM0.SaveReturn".equals(mgr.readValue("__COMMAND")))
                headset.refreshRow();

        } else if (mgr.dataTransfered())
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();
        } else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if ("TRUE".equals(mgr.readValue("REFRESHPARENT1")))
            verifyOperabilityConfirm();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("j")))
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("DELIMITATION_ORDER_NO")))
            okFind();

        chkAvailability();
        adjust();

        ctx.writeFlag("SATIS",satis);
        ctx.writeFlag("ENABLE1",enable1);
        ctx.writeFlag("ENABLE2",enable2);
        ctx.writeFlag("ENABLE3",enable3);

        // 031216  ARWILK  Begin  (Replace blocks with tabs)
        tabs.saveActiveTab();
        // 031216  ARWILK  End  (Replace blocks with tabs)
    }

//-----------------------------------------------------------------------------
//-----------------------------  PERFORM FUNCTION  ----------------------------
//-----------------------------------------------------------------------------

    public void  perform( String command) 
    {
        ASPManager mgr = getASPManager();
        int count;

        trans.clear();            
        //Web Alignment - enable multirow action
        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            count = headset.countSelectedRows();
            headset.markSelectedRows(command);
            mgr.submit(trans);

            headset.first();

            for (int i = 0;i < headset.countRows();i++)
            {
                headset.refreshRow();
                if (headlay.isMultirowLayout())
                    headset.next();
            }    
        } else
        {
            headset.unselectRows();
            count = 0;
            headset.markRow(command);
            int currrow = headset.getCurrentRowNo();
            mgr.submit(trans);

            headset.goTo(currrow);
            headset.refreshRow();
        }   
        setNewObjEvent(currrow);
    }

    public void  setNewObjEvent( int currrow) 
    {
        ASPBuffer r;

        headset.goTo(currrow);

        searchEvent();
        String newObjEvents = eventset.getValue("OBJEVENTS");

        r = headset.getRow();
        //r.setValue("HEAD_OBJEVENTS",newObjEvents);
        r.setValue("HEAD_OBJEVENTS",newObjEvents);
        headset.setRow(r);
    }
    public void  searchEvent()
    {
        ASPManager mgr = getASPManager();
        trans1.clear();
        currrow = headset.getCurrentRowNo();
        q = trans1.addQuery(eventblk);
        q.addWhereCondition("OBJID = ?");
        q.addParameter("OBJID",headset.getValue("OBJID"));
        q.includeMeta("ALL");

        mgr.submit(trans1);
        headset.goTo(currrow);
        trans1.clear();
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void  validate()
    {
        ASPManager mgr = getASPManager();
        String val= null;
        String secid = null;
        String appid = null;
        String verid = null;
        String preid = null;
        String hpreid = null;

        String mcname = null;
        String mcloc = null;
        String mcpos = null;
        String emctype = null;
        String mctype = null;
        String mccode = null;
        String woed = null;
        String displaySDate = null;
        String displayFDate = null;
        String compa = null;
        String item0Comp = null;
        String item1Comp = null;


        val = mgr.readValue("VALIDATE");

        if ("ELECTRICAL_SECURITY_LEADER".equals(val))
        {
            cmd = trans.addCustomFunction("SECID", "Company_Emp_API.Get_Max_Employee_Id", "ELECTRICAL_SECURITY_LEADER_ID");
            cmd.addParameter("COMPANY");
            cmd.addParameter("ELECTRICAL_SECURITY_LEADER");

            trans = mgr.validate(trans);
            secid = trans.getValue("SECID/DATA/ELECTRICAL_SECURITY_LEADER_ID");
            txt = (mgr.isEmpty(secid) ? "" :secid)+ "^";
            mgr.responseWrite(txt);
        }


        else if ("APPROVED_BY".equals(val))
        {
            cmd = trans.addCustomFunction("APPID", "Company_Emp_API.Get_Max_Employee_Id", "APPROVED_BY_ID");
            cmd.addParameter("COMPANY");
            cmd.addParameter("APPROVED_BY");

            trans = mgr.validate(trans);
            appid = trans.getValue("APPID/DATA/APPROVED_BY_ID");
            txt = (mgr.isEmpty(appid) ? "" :appid)+ "^";
            mgr.responseWrite(txt);
        }

        if ("HEAD_PREPARED_BY".equals(val))
        {
            cmd = trans.addCustomFunction("HPREID", "Company_Emp_API.Get_Max_Employee_Id", "HEAD_PREPARED_BY_ID");
            cmd.addParameter("COMPANY");
            cmd.addParameter("PREPARED_BY",mgr.readValue("HEAD_PREPARED_BY"));

            trans = mgr.validate(trans);
            hpreid = trans.getBuffer("HPREID/DATA").getValue("PREPARED_BY_ID");
            txt = (mgr.isEmpty(hpreid) ? "" :hpreid)+ "^";
            mgr.responseWrite(txt);
        }



        else if ("VERIFIED_BY".equals(val))
        {
            cmd = trans.addCustomFunction("VERID", "Company_Emp_API.Get_Max_Employee_Id", "VERIFIED_BY_ID");
            cmd.addParameter("COMPANY");
            cmd.addParameter("VERIFIED_BY");

            trans = mgr.validate(trans);
            verid = trans.getValue("VERID/DATA/VERIFIED_BY_ID");
            txt = (mgr.isEmpty(verid) ? "" :verid)+ "^";
            mgr.responseWrite(txt);
        }

        else if ("PREPARED_BY".equals(val))
        {
            cmd = trans.addCustomFunction("PREID", "Company_Emp_API.Get_Max_Employee_Id", "PREPARED_BY_ID");
            cmd.addParameter("COMPANY");
            cmd.addParameter("PREPARED_BY");

            trans = mgr.validate(trans);
            preid = trans.getValue("PREID/DATA/PREPARED_BY_ID");
            txt = (mgr.isEmpty(preid) ? "" :preid)+ "^";
            mgr.responseWrite(txt);
        }


        else if ("MCH_CODE".equals(val))
        {
            cmd = trans.addCustomFunction("MCNA", "EQUIPMENT_ALL_OBJECT_API.Get_Mch_Name", "MCH_NAME");
            cmd.addParameter("ITEM0_CONTRACT");
            cmd.addParameter("MCH_CODE");

            cmd = trans.addCustomFunction("MCLO", "EQUIPMENT_FUNCTIONAL_API.Get_Mch_Loc", "MCH_LOC");
            cmd.addParameter("ITEM0_CONTRACT");
            cmd.addParameter("MCH_CODE");

            cmd = trans.addCustomFunction("MCPO", "EQUIPMENT_FUNCTIONAL_API.Get_Mch_Pos", "MCH_POS");
            cmd.addParameter("ITEM0_CONTRACT");
            cmd.addParameter("MCH_CODE");

            cmd = trans.addCustomFunction("EQMCTY", "EQUIPMENT_FUNCTIONAL_API.Get_Mch_Type", "EQUIP_MCH_TYPE");
            cmd.addParameter("ITEM0_CONTRACT");
            cmd.addParameter("MCH_CODE");

            trans = mgr.validate(trans);

            mcname = trans.getValue("MCNA/DATA/MCH_NAME");
            mcloc  = trans.getValue("MCLO/DATA/MCH_LOC");
            mcpos  = trans.getValue("MCPO/DATA/MCH_POS");
            emctype= trans.getValue("EQMCTY/DATA/EQUIP_MCH_TYPE");
            mctype = emctype;

            txt = (mgr.isEmpty(mcname) ? "":mcname) + "^" + (mgr.isEmpty(mcloc) ? "" :mcloc) + "^" + (mgr.isEmpty(mcpos) ? "" :mcpos) + "^" + (mgr.isEmpty(emctype) ? "" :emctype) + "^"+ (mgr.isEmpty(mctype) ? "" :mctype) + "^";
            mgr.responseWrite(txt);
        }


        else if ("ITEM1_MCH_CODE".equals(val))
        {
            cmd = trans.addCustomFunction("MCNA", "EQUIPMENT_ALL_OBJECT_API.Get_Mch_Name", "ITEM1_MCH_NAME");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("ITEM1_MCH_CODE");

            cmd = trans.addCustomFunction("MCLO", "EQUIPMENT_FUNCTIONAL_API.Get_Mch_Loc", "ITEM1_MCH_LOC");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("ITEM1_MCH_CODE");

            cmd = trans.addCustomFunction("MCPO", "EQUIPMENT_FUNCTIONAL_API.Get_Mch_Pos", "ITEM1_MCH_POS");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("ITEM1_MCH_CODE");

            cmd = trans.addCustomFunction("EQMCTY", "EQUIPMENT_FUNCTIONAL_API.Get_Mch_Type", "ITEM1_EQUIP_MCH_TYPE");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("ITEM1_MCH_CODE");

            trans = mgr.validate(trans);

            mcname = trans.getValue("MCNA/DATA/ITEM1_MCH_NAME");
            mcloc  = trans.getValue("MCLO/DATA/ITEM1_MCH_LOC");
            mcpos  = trans.getValue("MCPO/DATA/ITEM1_MCH_POS");
            emctype= trans.getValue("EQMCTY/DATA/ITEM1_EQUIP_MCH_TYPE");
            String machinetype = emctype;       

            txt = (mgr.isEmpty(mcname) ? "":mcname) + "^" + (mgr.isEmpty(mcloc) ? "" :mcloc) + "^" + (mgr.isEmpty(mcpos) ? "" :mcpos) + "^" + (mgr.isEmpty(emctype) ? "" :emctype) + "^" + (mgr.isEmpty(machinetype) ? "" :machinetype) + "^";
            mgr.responseWrite(txt);
        }


        else if ("WO_NO".equals(val))
        {
            cmd = trans.addCustomFunction("MCCO", "WORK_ORDER_API.Get_Mch_Code", "WORKORDERMCHCODE");
            cmd.addParameter("WO_NO");

            cmd = trans.addCustomFunction("WOED", "WORK_ORDER_API.Get_Err_Descr", "WORKORDERERRDESCR");
            cmd.addParameter("WO_NO");

            cmd = trans.addCustomFunction("PSDATE", "WORK_ORDER_API.Get_Plan_S_Date", "WORKORDERPLANSDATE");
            cmd.addParameter("WO_NO");

            cmd = trans.addCustomFunction("PFDATE", "WORK_ORDER_API.Get_Plan_F_Date", "WORKORDERPLANFDATE");
            cmd.addParameter("WO_NO");

            trans = mgr.validate(trans);

            mccode  = trans.getValue("MCCO/DATA/WORKORDERMCHCODE");
            woed    = trans.getValue("WOED/DATA/WORKORDERERRDESCR");

            psdateBuff = trans.getBuffer("PSDATE/DATA");
            displaySDate = psdateBuff.getFieldValue("WORKORDERPLANSDATE");

            pfdateBuff = trans.getBuffer("PFDATE/DATA");
            displayFDate = pfdateBuff.getFieldValue("WORKORDERPLANFDATE");

            txt = (mgr.isEmpty(mccode) ? "":mccode) + "^" + (mgr.isEmpty(woed) ? "" :woed) + "^" + (mgr.isEmpty(displaySDate) ? "" :displaySDate) + "^" + (mgr.isEmpty(displayFDate) ? "" :displayFDate) + "^";
            mgr.responseWrite(txt);
        }


        else if ("CONTRACT".equals(val))
        {
            cmd = trans.addCustomFunction("COMP", "SITE_API.Get_Company", "COMPANY");
            cmd.addParameter("CONTRACT");

            trans = mgr.validate(trans);

            compa = trans.getValue("COMP/DATA/COMPANY");


            txt = (mgr.isEmpty(compa) ? "":compa)+ "^";
            mgr.responseWrite(txt);
        }


        else if ("ITEM0_CONTRACT".equals(val))
        {
            cmd = trans.addCustomFunction("ITEM0_COMP", "SITE_API.Get_Company", "ITEM0_TEMP");
            cmd.addParameter("ITEM0_CONTRACT");

            trans = mgr.validate(trans);

            item0Comp = trans.getValue("ITEM0_COMP/DATA/ITEM0_TEMP");

            txt = (mgr.isEmpty(item0Comp) ? "":item0Comp)+ "^";
            mgr.responseWrite(txt);
        }


        else if ("ITEM1_CONTRACT".equals(val))
        {
            cmd = trans.addCustomFunction("ITEM1_COMP", "SITE_API.Get_Company", "ITEM1_TEMP");
            cmd.addParameter("ITEM1_CONTRACT");

            trans = mgr.validate(trans);

            item1Comp = trans.getValue("ITEM1_COMP/DATA/ITEM1_TEMP");

            txt = (mgr.isEmpty(item1Comp) ? "":item1Comp)+ "^";
            mgr.responseWrite(txt);
        }


        //Bug 72024, Start
        else if ("PLAN_START_ESTABLISHMENT".equals(val))
        {
            String sDelOrdNo       = mgr.readValue("DELIMITATION_ORDER_NO","");
            String sPlanStartEst   = mgr.readValue("PLAN_START_ESTABLISHMENT","");
            String sPlanFinishEst  = mgr.readValue("PLAN_FINISH_ESTABLISHMENT","");
            String sPlanStartRest  = mgr.readValue("PLAN_START_REESTABLISHMENT","");
            String sPlanFinishRest = mgr.readValue("PLAN_FINISH_REESTABLISHMENT","");
            String sOldSDate = mgr.readValue("OLD_S_DATE","");

            trans.clear();

            if (!mgr.isEmpty(sPlanFinishEst) && !mgr.isEmpty(sOldSDate))
            {
                cmd = trans.addQuery("CALCFEST","SELECT ? + (? - ?) PLAN_FINISH_ESTABLISHMENT FROM DUAL");
                cmd.addParameter("PLAN_START_ESTABLISHMENT",sPlanStartEst);
                cmd.addParameter("PLAN_FINISH_ESTABLISHMENT",sPlanFinishEst);
                cmd.addParameter("OLD_S_DATE",sOldSDate);
            }

            if (!mgr.isEmpty(sPlanStartRest) && !mgr.isEmpty(sOldSDate))
            {
                cmd = trans.addQuery("CALCSREST","SELECT ? + (? - ?) PLAN_START_REESTABLISHMENT FROM DUAL");
                cmd.addParameter("PLAN_START_ESTABLISHMENT",sPlanStartEst);
                cmd.addParameter("PLAN_START_REESTABLISHMENT",sPlanStartRest);
                cmd.addParameter("OLD_S_DATE",sOldSDate);
            }

            if (!mgr.isEmpty(sPlanFinishRest) && !mgr.isEmpty(sOldSDate))
            {
                cmd = trans.addQuery("CALCFREST","SELECT ? + (? - ?) PLAN_FINISH_REESTABLISHMENT FROM DUAL");
                cmd.addParameter("PLAN_START_ESTABLISHMENT",sPlanStartEst);
                cmd.addParameter("PLAN_FINISH_REESTABLISHMENT",sPlanFinishRest);
                cmd.addParameter("OLD_S_DATE",sOldSDate); 
            }

            trans = mgr.validate(trans);

            if (!mgr.isEmpty(sPlanFinishEst) && !mgr.isEmpty(sOldSDate))
                sPlanFinishEst = trans.getBuffer("CALCFEST/DATA").getFieldValue("PLAN_FINISH_ESTABLISHMENT");

            if (!mgr.isEmpty(sPlanStartRest) && !mgr.isEmpty(sOldSDate))
                sPlanStartRest = trans.getBuffer("CALCSREST/DATA").getFieldValue("PLAN_START_REESTABLISHMENT");

            if (!mgr.isEmpty(sPlanFinishRest) && !mgr.isEmpty(sOldSDate))
                sPlanFinishRest = trans.getBuffer("CALCFREST/DATA").getFieldValue("PLAN_FINISH_REESTABLISHMENT"); 

            sOldSDate = sPlanStartEst;

            txt = (mgr.isEmpty(sPlanFinishEst) ? "":sPlanFinishEst)+ "^"
                  + (mgr.isEmpty(sPlanStartRest) ? "":sPlanStartRest)+ "^"
                  + (mgr.isEmpty(sPlanFinishRest) ? "":sPlanFinishRest)+ "^"
                  + (mgr.isEmpty(sOldSDate) ? "":sOldSDate)+ "^";
            mgr.responseWrite(txt);
        }
        //Bug 72024, End

        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void  newRow()
    {
        ASPManager mgr = getASPManager();

        cmd = trans.addEmptyCommand("HEAD","DELIMITATION_ORDER_API.New__",headblk);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);
        data = trans.getBuffer("HEAD/DATA");
        headset.addRow(data);

    }

//-----------------------------------------------------------------------------
//----------------------  CMDBAR EDIT-IT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void  newRowITEM0()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        cmd = trans.addEmptyCommand("ITEM0","EST_DEL_ORDER_LINE_API.New__",itemblk0);
        cmd.setOption("ACTION","PREPARE");
        cmd.setParameter("ITEM0_DELIMITATION_ORDER_NO", headset.getValue("DELIMITATION_ORDER_NO"));
        trans = mgr.perform(trans);
        data = trans.getBuffer("ITEM0/DATA");
        data.setFieldItem("ITEM0_DELIMITATION_ORDER_NO",headset.getValue("DELIMITATION_ORDER_NO"));
        itemset0.addRow(data);
    }


    public void  newRowITEM1()
    {
        ASPManager mgr = getASPManager();
        trans.clear();
        cmd = trans.addEmptyCommand("ITEM1","REEST_DEL_ORDER_LINE_API.New__",itemblk1);
        cmd.setOption("ACTION","PREPARE");
        cmd.setParameter("ITEM1_DELIMITATION_ORDER_NO", headset.getValue("DELIMITATION_ORDER_NO"));
        trans = mgr.perform(trans);
        data = trans.getBuffer("ITEM1/DATA");
        data.setFieldItem("ITEM1_DELIMITATION_ORDER_NO",headset.getValue("DELIMITATION_ORDER_NO"));
        itemset1.addRow(data);
    }


    public void  newRowITEM2()
    {
        ASPManager mgr = getASPManager();

        cmd = trans.addEmptyCommand("ITEM2","PERMIT_API.New__",itemblk2);
        cmd.setOption("ACTION","PREPARE");
        cmd.setParameter("ITEM2_DELIMITATION_ORDER_NO", headset.getValue("DELIMITATION_ORDER_NO"));
        trans = mgr.perform(trans);
        data = trans.getBuffer("ITEM2/DATA");
        data.setFieldItem("ITEM2_DELIMITATION_ORDER_NO",headset.getValue("DELIMITATION_ORDER_NO"));
        itemset2.addRow(data);
    }


    public void  newRowITEM3()
    {
        ASPManager mgr = getASPManager();

        cmd = trans.addEmptyCommand("ITEM3","DELIMITATION_WORK_ORDER_API.New__",itemblk3);
        cmd.setOption("ACTION","PREPARE");
        cmd.setParameter("ITEM3_DELIMITATION_ORDER_NO", headset.getValue("DELIMITATION_ORDER_NO"));
        trans = mgr.perform(trans);
        data = trans.getBuffer("ITEM3/DATA");
        data.setFieldItem("ITEM3_DELIMITATION_ORDER_NO",headset.getValue("DELIMITATION_ORDER_NO"));
        data.setFieldItem("WO_NO",mgr.readValue("WO_NO"));
        itemset3.addRow(data);
    }


    public void  newRowITEM4()
    {
        ASPManager mgr = getASPManager();

        cmd = trans.addEmptyCommand("ITEM4","DELIMITATION_ORDER_HISTORY_API.New__",itemblk4);
        cmd.setOption("ACTION","PREPARE");
        cmd.setParameter("ITEM4_DELIMITATION_ORDER_NO", headset.getValue("DELIMITATION_ORDER_NO"));
        trans = mgr.perform(trans);
        data = trans.getBuffer("ITEM4/DATA");
        data.setFieldItem("ITEM4_DELIMITATION_ORDER_NO",headset.getValue("DELIMITATION_ORDER_NO"));
        itemset4.addRow(data);
    }

    public void  deleteRow()
    {
        ASPManager mgr = getASPManager();
        headset.refreshRow();

        headset.store();
        if (headlay.isMultirowLayout())
        {
            headset.setSelectedRowsRemoved();
            headset.unselectRows();
        } else
            headset.setRemoved();

        mgr.submit(trans);
        headbar.browseUpdate(1);
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void  countFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CONTRACT) FROM dual)");

        mgr.submit(trans);
        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();
    }


    public void  okFind()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CONTRACT) FROM dual)");
        q.setOrderByClause("DELIMITATION_ORDER_NO");
        q.includeMeta("ALL");

        if (mgr.dataTransfered())
            q.addOrCondition(mgr.getTransferedData());

        mgr.querySubmit(trans,headblk);

        eval(headset.syncItemSets());

        if (headset.countRows() == 1)
        {
            okFindITEM0();
            okFindITEM1();
            okFindITEM2();
            okFindITEM3();
            okFindITEM4();
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        }

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWDELIMITATIONORDERTABNODATAFOUND: No data found."));
            headset.clear();
        }
        mgr.createSearchURL(headblk);

    }

//-----------------------------------------------------------------------------
//-----------------------  CMDBAR SEARCH-IT FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

    public void  okFindITEM0()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();
        q = trans.addQuery(itemblk0);
        q.addWhereCondition("DELIMITATION_ORDER_NO = ?");
        q.addParameter("DELIMITATION_ORDER_NO", headset.getValue("DELIMITATION_ORDER_NO"));
        q.includeMeta("ALL");
        mgr.submit(trans);

        if (comBarAct)
        {
            if (itemset0.countRows() == 0 && "ITEM0.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWDELIMITATIONORDERTABNODATA: No data found."));
                itemset0.clear();
            }
        }
        headset.goTo(headrowno);
    }


    public void  okFindITEM1()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();
        q = trans.addQuery(itemblk1);
        q.addWhereCondition("DELIMITATION_ORDER_NO = ?");
        q.addParameter("DELIMITATION_ORDER_NO", headset.getValue("DELIMITATION_ORDER_NO"));
        q.includeMeta("ALL");
        mgr.submit(trans);

        if (comBarAct)
        {
            if (itemset1.countRows() == 0 && "ITEM1.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWDELIMITATIONORDERTABNODATA: No data found."));
                itemset1.clear();
            }
        }
        headset.goTo(headrowno);
    }

    public void  okFindITEM2()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();
        q = trans.addQuery(itemblk2);
        q.addWhereCondition("DELIMITATION_ORDER_NO = ?");
        q.addParameter("DELIMITATION_ORDER_NO", headset.getValue("DELIMITATION_ORDER_NO"));
        q.includeMeta("ALL");
        mgr.submit(trans);

        if (comBarAct)
        {
            if (itemset2.countRows() == 0 && "ITEM2.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWDELIMITATIONORDERTABNODATA: No data found."));
                itemset2.clear();
            }
        }
        headset.goTo(headrowno);
    }

    public void  okFindITEM3()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();
        q = trans.addQuery(itemblk3);
        q.addWhereCondition("DELIMITATION_ORDER_NO = ?");
        q.addParameter("DELIMITATION_ORDER_NO", headset.getValue("DELIMITATION_ORDER_NO"));
        q.includeMeta("ALL");
        mgr.submit(trans);

        if (comBarAct)
        {
            if (itemset3.countRows() == 0 && "ITEM3.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWDELIMITATIONORDERTABNODATA: No data found."));
                itemset3.clear();
            }
        }
        headset.goTo(headrowno);
    }

    public void  okFindITEM4()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();
        q = trans.addQuery(itemblk4);
        q.addWhereCondition("DELIMITATION_ORDER_NO = ?");
        q.addParameter("DELIMITATION_ORDER_NO", headset.getValue("DELIMITATION_ORDER_NO"));
        q.includeMeta("ALL");
        mgr.submit(trans);

        if (comBarAct)
        {
            if (itemset4.countRows() == 0 && "ITEM4.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWDELIMITATIONORDERTABNODATA: No data found."));
                itemset4.clear();
            }
        }
        headset.goTo(headrowno);
    }

    public void  countFindITEM0()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk0);
        q.addWhereCondition("DELIMITATION_ORDER_NO = ?");
        q.addParameter("DELIMITATION_ORDER_NO", headset.getValue("DELIMITATION_ORDER_NO"));
        q.setSelectList("to_char(count(*)) N");
        headrowno = headset.getCurrentRowNo();
        mgr.submit(trans);
        itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
        itemset0.clear();
        headset.goTo(headrowno);
    }

    public void  countFindITEM1()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk1);
        q.addWhereCondition("DELIMITATION_ORDER_NO = ?");
        q.addParameter("DELIMITATION_ORDER_NO", headset.getValue("DELIMITATION_ORDER_NO"));
        q.setSelectList("to_char(count(*)) N");
        headrowno = headset.getCurrentRowNo();
        mgr.submit(trans);
        itemlay1.setCountValue(toInt(itemset1.getRow().getValue("N")));
        itemset1.clear();
        headset.goTo(headrowno);
    }

    public void  countFindITEM2()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk2);
        q.addWhereCondition("DELIMITATION_ORDER_NO = ?");
        q.addParameter("DELIMITATION_ORDER_NO", headset.getValue("DELIMITATION_ORDER_NO"));
        q.setSelectList("to_char(count(*)) N");
        headrowno = headset.getCurrentRowNo();
        mgr.submit(trans);
        itemlay2.setCountValue(toInt(itemset2.getRow().getValue("N")));
        itemset2.clear();
        headset.goTo(headrowno);
    }

    public void  countFindITEM3()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk3);
        q.addWhereCondition("DELIMITATION_ORDER_NO = ?");
        q.addParameter("DELIMITATION_ORDER_NO", headset.getValue("DELIMITATION_ORDER_NO"));
        q.setSelectList("to_char(count(*)) N");
        headrowno = headset.getCurrentRowNo();
        mgr.submit(trans);
        itemlay3.setCountValue(toInt(itemset3.getRow().getValue("N")));
        itemset3.clear();
        headset.goTo(headrowno);
    }

    public void  countFindITEM4()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk4);
        q.addWhereCondition("DELIMITATION_ORDER_NO = ?");
        q.addParameter("DELIMITATION_ORDER_NO", headset.getValue("DELIMITATION_ORDER_NO"));
        q.setSelectList("to_char(count(*)) N");
        headrowno = headset.getCurrentRowNo();
        mgr.submit(trans);
        itemlay4.setCountValue(toInt(itemset4.getRow().getValue("N")));
        itemset4.clear();
        headset.goTo(headrowno);
    }

    public void saveReturn()
    {
        ASPManager mgr = getASPManager();

        headset.changeRow();
        int currrow = headset.getCurrentRowNo();
        mgr.submit(trans);
        headset.refreshAllRows();
        headset.goTo(currrow);
        headlay.setLayoutMode(headlay.getHistoryMode());    
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void  cpyDelOrd()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            headset.setFilterOn();
        }
        cmd = trans.addCustomCommand("COPY","Delimitation_Order_API.Copy__");
        cmd.addParameter("NEW_DELIMITATION_ORDER_NO");
        cmd.addParameter("DELIMITATION_ORDER_NO",headset.getRow().getValue("DELIMITATION_ORDER_NO"));

        trans=mgr.perform(trans);
        newDelNo = toDouble(trans.getValue("COPY/DATA/NEW_DELIMITATION_ORDER_NO")); 
        String sNewDelNo = trans.getValue("COPY/DATA/NEW_DELIMITATION_ORDER_NO");
        if (!mgr.isEmpty(sNewDelNo))
            mgr.showAlert(mgr.translate("POPYDELOMITATIONORDER: New Isolation Order No: ")+sNewDelNo);
    }

    public void  print()
    {
        ASPManager mgr = getASPManager();
        // Function call for Report Print

        //Web Alignment - Enable multirow action
        int count = 0;
        ASPBuffer print;
        ASPBuffer printBuff;
        String attr1;
        String attr2;
        String attr3;
        String attr4;

        trans.clear();

        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            headset.setFilterOn();
            count = headset.countSelectedRows();
        } else
        {
            headset.unselectRows();
            count = 1;
        }

        for (int i = 0;i < count;i++)
        {

            DelOrderNo = toDouble(headset.getValue("DELIMITATION_ORDER_NO"));

            attr1 =  "REPORT_ID" + (char)31 + "DELIMITATION_ORDER_REP" + (char)30;
            attr2 =  "DELIMITATION_ORDER_NO_LIST" + (char)31 + DelOrderNo + (char)30; 
            attr3 =  "";
            attr4 =  "";       

            cmd = trans.addCustomCommand( "PRNT" + i, "Archive_API.New_Client_Report");
            cmd.addParameter("ATTR0");  
            cmd.addParameter("ATTR1",attr1);  
            cmd.addParameter("ATTR2",attr2);  
            cmd.addParameter("ATTR3",attr3);  
            cmd.addParameter("ATTR4",attr4);  

            if (headlay.isMultirowLayout())
                headset.next();
        }

        trans = mgr.perform(trans);

        if (headlay.isMultirowLayout())
            headset.setFilterOff();

        print = mgr.newASPBuffer();

        for (int i = 0;i < count;i++)
        {
            printBuff = print.addBuffer("DATA");
            //printBuff = print.addBuffer("DATA" + i);
            printBuff.addItem("RESULT_KEY", trans.getValue("PRNT" + i + "/DATA/ATTR0"));
        }

        callPrintDlg(print,true);

        //
    }

    public void  printSigns()
    {
        ASPManager mgr = getASPManager();


        //Web Alignment - Enable multirow action
        int count = 0;
        ASPBuffer print;
        ASPBuffer printBuff;
        String attr1;
        String attr2;
        String attr3;
        String attr4;

        trans.clear();

        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            headset.setFilterOn();
            count = headset.countSelectedRows();
        } else
        {
            headset.unselectRows();
            count = 1;
        }

        for (int i = 0;i < count;i++)
        {

            DelOrderNo = toDouble(headset.getValue("DELIMITATION_ORDER_NO"));

            attr1 =  "REPORT_ID" + (char)31 + "EST_DEL_ORD_SIGNS_REP" + (char)30;
            attr2 =  "DELIMITATION_ORDER_NO" + (char)31 + DelOrderNo + (char)30; 
            attr3 =  "";
            attr4 =  "";

            cmd = trans.addCustomCommand( "PRNT" + i, "Archive_API.New_Client_Report");
            cmd.addParameter("ATTR0");  
            cmd.addParameter("ATTR1",attr1);  
            cmd.addParameter("ATTR2",attr2);  
            cmd.addParameter("ATTR3",attr3);  
            cmd.addParameter("ATTR4",attr4);  

            if (headlay.isMultirowLayout())
                headset.next();
        }

        trans = mgr.perform(trans);

        if (headlay.isMultirowLayout())
            headset.setFilterOff();

        print = mgr.newASPBuffer();

        for (int i = 0;i < count;i++)
        {
            printBuff = print.addBuffer("DATA");
            //printBuff = print.addBuffer("DATA" + i);
            printBuff.addItem("RESULT_KEY", trans.getValue("PRNT" + i + "/DATA/ATTR0"));
        }

        callPrintDlg(print,true);

        //

    }

    public void createFromIsolationTemplate()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());

        bOpenNewWindow = true;

        urlString = "ModuleDlg.page";

        newWinHandle = "createDeliTemp"; 
    }

    public void  createReEst()
    {
        ASPManager mgr = getASPManager();

        if (itemlay0.isMultirowLayout())
        {
            itemset0.storeSelections();
            itemset0.setFilterOn();
        }

        cmd = trans.addCustomCommand("EXREST1","Delimitation_Order_Utility_API.Copy_And_Reverse_Est");
        cmd.addParameter("ITEM0_DELIMITATION_ORDER_NO",itemset0.getRow().getFieldValue("ITEM0_DELIMITATION_ORDER_NO"));
        trans=mgr.perform(trans);
        mgr.showAlert(mgr.translate( "PCMWDELIMITATIONORDERTABREESTABLISHED: Establishment instructions reversed."));
        okFindITEM0();
        okFindITEM1();
    }

    public void  prePermit()
    {
        ASPManager mgr = getASPManager();

        if (itemlay2.isMultirowLayout())
        {
            itemset2.storeSelections();
            itemset2.setFilterOn();
        }
        keys = itemset2.getSelectedRows("PERMIT_SEQ");
        mgr.transferDataTo("Permit.page",keys);
    }

    public void  startPreparation()
    {
        int currentrow = headset.getCurrentRowNo();
        trans.clear();      
        perform("START_PREPARATION__");
        headset.goTo(currentrow);
    }

    public void  preparation()
    {
        int currentrow = headset.getCurrentRowNo();
        trans.clear();
        perform("PREPARE__");
        headset.goTo(currentrow);
    }

    public void  revise()
    {
        int currentrow = headset.getCurrentRowNo();
        trans.clear();
        perform("REVISE__");
        headset.goTo(currentrow);
    }

    public void  approvePreparation()
    {
        int currentrow = headset.getCurrentRowNo();
        trans.clear();
        perform("APPROVE_PREPARATION__");
        headset.goTo(currentrow);
    }

    public void  startEstablishment()
    {
        int currentrow = headset.getCurrentRowNo();
        trans.clear(); 
        perform("START_ESTABLISHMENT__");
        headset.goTo(currentrow);
    }

    public void  completeEstablishment()
    {
        int currentrow = headset.getCurrentRowNo();
        trans.clear();
        perform("COMPLETE_ESTABLISHMENT__");
        headset.goTo(currentrow);
    }

    public void  reopen()
    {
        int currentrow = headset.getCurrentRowNo();
        trans.clear(); 
        perform("REOPEN__"); 
        headset.goTo(currentrow);
    }

    public void  startReestablishment()
    {
        int currentrow = headset.getCurrentRowNo();
        trans.clear();
        perform("START_REESTABLISHMENT__");
        headset.goTo(currentrow);
    }

    public void  completeReestablishment()
    {
        int currentrow = headset.getCurrentRowNo();
        trans.clear();
        perform("COMPLETE_REESTABLISHMENT__");
        headset.goTo(currentrow);
    }

    public void  verifyOperability()
    {
        ASPManager mgr = getASPManager();    
        cmd  = trans.addCustomFunction("CMDHASACTIVEWO","DELIMITATION_WORK_ORDER_API.Has_Active_Wos","HASACTIVEWO");
        cmd.addParameter("DELIMITATION_ORDER_NO",headset.getRow().getValue("DELIMITATION_ORDER_NO"));
        trans = mgr.perform(trans);
        hasWo = trans.getValue("CMDHASACTIVEWO/DATA/HASACTIVEWO");
        if ("FALSE".equals(hasWo))
            verifyOperabilityConfirm();

    }

    public void  verifyOperabilityConfirm()
    {
        int currentrow = headset.getCurrentRowNo();
        trans.clear();
        perform("VERIFY_OPERABILITY__");
        headset.goTo(currentrow);
    }

    public void  cancel()
    {
        int currentrow = headset.getCurrentRowNo();
        trans.clear();
        perform("CANCELED__");
        headset.goTo(currentrow);
    }
    public void  historical()
    {
        int currentrow = headset.getCurrentRowNo();
        trans.clear();
        perform("COMPLETE__");
        headset.goTo(currentrow);
    }



//-----------------------------------------------------------
//--------------------- Added RMB for documentation ---------
//-----------------------------------------------------------

    public void  documentation()
    {
        ASPManager mgr = getASPManager();

        mgr.redirectTo("../docmaw/DocReference.page?LU_NAME=DelimitationOrder&KEY_REF=CONTRACT="+mgr.URLEncode(headset.getRow().getValue("CONTRACT"))+"^DELIMITATION_ORDER_NO="+mgr.URLEncode(headset.getRow().getValue("DELIMITATION_ORDER_NO")+"^"));
    }

    public void  preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        headblk.addField("OBJID").
        setHidden();

        headblk.addField("OBJVERSION").
        setHidden();

        headblk.addField("OBJSTATE").
        setHidden();

        headblk.addField("HEAD_OBJEVENTS").
        setFunction("OBJEVENTS").
        setHidden();

        headblk.addField("DELIMITATION_ORDER_NO","Number","#").
        setSize(20).
        setLabel("PCMWDELIMITATIONORDERTABDELORDNO: Isolation Order No").
        setReadOnly();

        headblk.addField("ISOLATION_TYPE").
        setSize(20).
        setLabel("PCMWISOLATIONTYPE: Isolation Type").
        setDynamicLOV("ISOLATION_TYPE",600,445).
        setMaxLength(100).
        setMandatory().
        setReadOnly().
        setInsertable().
        setUpperCase();

        headblk.addField("DESCRIPTION").
        setSize(20).
        setMaxLength(60).
        setMandatory().
        setLabel("PCMWDELIMITATIONORDERTABDESC: Description");

        headblk.addField("CONTRACT").
        setSize(8).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445).
        setCustomValidation("CONTRACT","COMPANY").
        setLabel("PCMWDELIMITATIONORDERTABCONTRACT: Site").
        setUpperCase();

        headblk.addField("STATE").
        setSize(20).
        setReadOnly().
        setMaxLength(253).
        setLabel("PCMWDELIMITATIONORDERTABSTATE: Status");

        //Bug 72024, Start, Added custom validation
        headblk.addField("PLAN_START_ESTABLISHMENT","Datetime").
        setSize(28).
        setCustomValidation("DELIMITATION_ORDER_NO,PLAN_START_ESTABLISHMENT,PLAN_FINISH_ESTABLISHMENT,PLAN_START_REESTABLISHMENT,PLAN_FINISH_REESTABLISHMENT,OLD_S_DATE","PLAN_FINISH_ESTABLISHMENT,PLAN_START_REESTABLISHMENT,PLAN_FINISH_REESTABLISHMENT,OLD_S_DATE").
        setLabel("PCMWDELIMITATIONORDERTABPLANSTRTEST: Planned Start Establishment");
        //Bug 72024, End

        headblk.addField("PLAN_FINISH_ESTABLISHMENT","Datetime").
        setSize(28).
        setLabel("PCMWDELIMITATIONORDERTABPLANFINEST: Planned Completion Establishment");

        headblk.addField("PLAN_START_REESTABLISHMENT","Datetime").
        setSize(28).
        setLabel("PCMWDELIMITATIONORDERTABPLANSTARTREEST: Planned Start Reestablishment");

        headblk.addField("PLAN_FINISH_REESTABLISHMENT","Datetime").
        setSize(28).
        setLabel("PCMWDELIMITATIONORDERTABPLANFINREEST: Planned Completion Reestablishment");

        headblk.addField("RANGE").
        setSize(28).
        setDefaultNotVisible().
        setLabel("PCMWDELIMITATIONORDERTABRANGE: Range");

        headblk.addField("CONDITIONS").
        setSize(28).
        setDefaultNotVisible().
        setLabel("PCMWDELIMITATIONORDERTABCOND: Conditions");

        headblk.addField("EQUIPMENT_INVOLVED").
        setSize(28).
        setDefaultNotVisible().
        setLabel("PCMWDELIMITATIONORDERTABEQUIPINVOL: Equipment Involved");

        headblk.addField("DISTRIBUTION").
        setSize(28).
        setDefaultNotVisible().
        setLabel("PCMWDELIMITATIONORDERTABDISTR: Distribution");

        headblk.addField("NOTE").
        setSize(28).
        setDefaultNotVisible().
        setLabel("PCMWDELIMITATIONORDERTABNOTE: Note");

        headblk.addField("ELECTRICAL_SECURITY_LEADER_ID").
        setSize(6).
        setHidden().
        setUpperCase();

        headblk.addField("ELECTRICAL_SECURITY_LEADER").
        setSize(15).
        setDefaultNotVisible().
        setMaxLength(20).
        setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445).
        setCustomValidation("COMPANY, ELECTRICAL_SECURITY_LEADER","ELECTRICAL_SECURITY_LEADER_ID").
        setLabel("PCMWDELIMITATIONORDERTABELECSECLEAD: Electrical Security Leader").
        setUpperCase();

        headblk.addField("COMPANY").
        setSize(20).
        setMaxLength(20).
        //setFunction("Site_API.Get_Company(:CONTRACT)").
        setHidden().
        setUpperCase();

        headblk.addField("LOCKCOUNT").
        setSize(12).
        setLabel("PCMWDELIMITATIONORDERTABLOCKCOUNT: Lock Count").
        setReadOnly().
        setDefaultNotVisible().
        setFunction("Delimitation_Order_Utility_API.Get_Est_Count_Locks(:DELIMITATION_ORDER_NO)");

        headblk.addField("SIGNCOUNT").
        setSize(12).
        setLabel("PCMWDELIMITATIONORDERTABSIGNCOUNT: Sign Count").
        setReadOnly().
        setDefaultNotVisible().
        setFunction("Delimitation_Order_Utility_API.Get_Est_Count_Signs(:DELIMITATION_ORDER_NO)");

        headblk.addField("CBOBJECTTYPECHANGED").
        setSize(20).
        setLabel("PCMWDELIMITATIONORDERTABCBOBJECTTYPECHANGED: Equipment Type Changed").
        setReadOnly().
        setDefaultNotVisible().
        setFunction("Delimitation_Order_Utility_API.Check_Del_Line_Object_Type(:DELIMITATION_ORDER_NO)").
        setCheckBox("FALSE,TRUE");

        headblk.addField("HEAD_PREPARED_BY").
        setDbName("PREPARED_BY").
        setSize(15).
        setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445).
        setLabel("PCMWDELIMORDPREPAREDBY: Prepared By").
        setCustomValidation("COMPANY, HEAD_PREPARED_BY","HEAD_PREPARED_BY_ID").
        setMaxLength(20).
        setUpperCase();

        headblk.addField("APPROVED_BY").
        setSize(15).
        setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445).
        setLabel("PCMWDELIMITATIONORDERTABAPPBY: Approved By").
        setDefaultNotVisible().
        setCustomValidation("COMPANY, APPROVED_BY","APPROVED_BY_ID").
        setMaxLength(20).
        setUpperCase();

        headblk.addField("APPROVED_BY_ID").
        setSize(5).
        setHidden().
        setUpperCase();

        headblk.addField("VERIFIED_BY_ID").
        setSize(5).
        setHidden().
        setUpperCase();

        headblk.addField("HEAD_PREPARED_BY_ID").
        setDbName("PREPARED_BY_ID").
        setSize(5).
        setHidden().
        setUpperCase();

        headblk.addField("VERIFIED_BY").
        setSize(15).
        setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445).
        setLabel("PCMWDELIMORDCERTIFIEDBY: Certified By").
        setCustomValidation("COMPANY, VERIFIED_BY","VERIFIED_BY_ID").
        setMaxLength(20).
        setUpperCase();


        //--------- Added for the Hypelink (Documents)

        headblk.addField("LU_NAME").
        setHidden().
        setFunction("'DelimitationOrder'");

        headblk.addField("KEY_REF").
        setHidden().
        setFunction("CONCAT(CONCAT('CONTRACT=',CONCAT(CONTRACT,'^')),CONCAT('DELIMITATION_ORDER_NO=',CONCAT(DELIMITATION_ORDER_NO,'^')))");

        headblk.addField("DOCUMENT").
        setFunction("substr(Doc_Reference_Object_API.Exist_Obj_Reference('DelimitationOrder',CONCAT(CONCAT('CONTRACT=',CONCAT(CONTRACT,'^')),CONCAT('DELIMITATION_ORDER_NO=',CONCAT(DELIMITATION_ORDER_NO,'^')))),1,5)").
        setUpperCase().
        setDefaultNotVisible().
        setLabel("PCMWDELIMITATIONORDERTABDOCUMENT: Has Documents").
        setReadOnly().
        setCheckBox("FALSE,TRUE").
        setSize(18);

        headblk.addField("HASACTIVEWO").
        setFunction("''").
        setUpperCase().
        setCheckBox("FALSE,TRUE").
        setHidden();

        //Bug 72024, Start
        headblk.addField("OLD_S_DATE","Datetime").
        setFunction("''").
        setHidden();
        //Bug 72024, End

        headblk.setView("DELIMITATION_ORDER");
        headblk.defineCommand("DELIMITATION_ORDER_API","New__,Modify__,Remove__,START_PREPARATION__,PREPARE__,REVISE__,APPROVE_PREPARATION__,START_ESTABLISHMENT__,COMPLETE_ESTABLISHMENT__,REOPEN__,START_REESTABLISHMENT__,COMPLETE_REESTABLISHMENT__,VERIFY_OPERABILITY__,CANCELED__,COMPLETE__");
        headblk.disableDocMan();
        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWDELIMITATIONORDERTABHEADTITLE: Isolation Order"));    
        headtbl.setWrap();

        //Web Alignment - Multirow Action
        headtbl.enableRowSelect();
        //

        if (mgr.isPresentationObjectInstalled("docmaw/DocReference.page"))
            headbar.addCustomCommand("documentation",mgr.translate("PCMWDELIMITATIONORDERTABDOCU: Document..."));
        headbar.addCustomCommand("cpyDelOrd",mgr.translate("PCMWDELIMITATIONORDERTABCOPY: Copy"));
        headbar.addCustomCommand("print",mgr.translate("PCMWDELIMITATIONORDERTABPRINT: Print..."));
        headbar.addCustomCommand("printSigns",mgr.translate("PCMWDELIMITATIONORDERTABPRINTSIGN: Print Signs..."));
        // 031216  ARWILK  Begin  (Links with RMB's)
        headbar.addCustomCommand("createFromIsolationTemplate",mgr.translate("PCMWDELIMITATIONORDERTABCREDELTEM: Create From Isolation Template..."));
        // 031216  ARWILK  End  (Links with RMB's)

        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("StartPreparation",mgr.translate("PCMWDELIMITATIONORDERTABSTPREP: Start Preparation"));
        headbar.addCustomCommand("Preparation",mgr.translate("PCMWDELIMITATIONORDERTABPREPARE: Prepare"));
        headbar.addCustomCommand("Revise",mgr.translate("PCMWDELIMITATIONORDERTABREVISE: Revise"));
        headbar.addCustomCommand("ApprovePreparation",mgr.translate("PCMWDELIMITATIONORDERTABAPPPRE: Approve Preparation"));
        headbar.addCustomCommand("StartEstablishment",mgr.translate("PCMWDELIMITATIONORDERTABSTEST: Start Establishment"));
        headbar.addCustomCommand("CompleteEstablishment",mgr.translate("PCMWDELIMITATIONORDERTABCOMEST: Complete Establishment"));
        headbar.addCustomCommand("Reopen",mgr.translate("PCMWDELIMITATIONORDERTABREOPINS: Reopen For Instructions"));
        headbar.addCustomCommand("StartReestablishment",mgr.translate("PCMWDELIMITATIONORDERTABSTREEST: Start Reestablishment"));
        headbar.addCustomCommand("CompleteReestablishment",mgr.translate("PCMWDELIMITATIONORDERTABCOMREEST: Complete Reestablishment"));
        headbar.addCustomCommand("VerifyOperability",mgr.translate("PCMWDELIMITATIONORDERTABVEROP: Verify Operability"));
        headbar.addCustomCommand("Cancel",mgr.translate("PCMWDELIMITATIONORDERTABCANCEL: Cancelled"));
        headbar.addCustomCommand("Historical",mgr.translate("PCMWDELIMITATIONORDERTABHIST: Historical"));

        // 031216  ARWILK  Begin  (Replace blocks with tabs)
        headbar.addCustomCommand("activateEstablishmentInst", "");
        headbar.addCustomCommand("activateReestablishmentInst", "");    
        headbar.addCustomCommand("activateWorkPermits", "");    
        headbar.addCustomCommand("activateWorkOrders", ""); 
        headbar.addCustomCommand("activateHistory", "");
        // 031216  ARWILK  End  (Replace blocks with tabs)

        // 031229  ARWILK  Begin  (Links with multirow RMB's)
        headbar.enableMultirowAction();
        if (mgr.isPresentationObjectInstalled("docmaw/DocReference.page"))
            headbar.removeFromMultirowAction("documentation");
        headbar.removeFromMultirowAction("cpyDelOrd");
        // 031229  ARWILK  End  (Links with multirow RMB's)    

        headbar.forceEnableMultiActionCommand("createFromIsolationTemplate");

        //Web Alignment - Add Valid Conditions for the States

        headbar.addCustomCommandGroup("ISSTATUS", mgr.translate("PCMWISOLATIONORDERSTUS: Isolation Order Status"));
        headbar.setCustomCommandGroup("StartPreparation", "ISSTATUS"); 
        headbar.setCustomCommandGroup("Preparation", "ISSTATUS");
        headbar.setCustomCommandGroup("Revise", "ISSTATUS");
        headbar.setCustomCommandGroup("ApprovePreparation", "ISSTATUS");
        headbar.setCustomCommandGroup("StartEstablishment", "ISSTATUS");
        headbar.setCustomCommandGroup("CompleteEstablishment", "ISSTATUS");
        headbar.setCustomCommandGroup("Reopen", "ISSTATUS");
        headbar.setCustomCommandGroup("StartReestablishment", "ISSTATUS");
        headbar.setCustomCommandGroup("CompleteReestablishment", "ISSTATUS");
        headbar.setCustomCommandGroup("VerifyOperability", "ISSTATUS");
        headbar.setCustomCommandGroup("Cancel", "ISSTATUS");
        headbar.setCustomCommandGroup("Historical", "ISSTATUS"); 

        headbar.addCommandValidConditions("StartPreparation","OBJSTATE","Enable","New");
        headbar.addCommandValidConditions("Preparation","OBJSTATE","Enable","New;UnderPreparation");
        headbar.addCommandValidConditions("Revise","OBJSTATE","Enable","PreparationApproved;Prepared");
        headbar.addCommandValidConditions("ApprovePreparation","OBJSTATE","Enable","Prepared");
        headbar.addCommandValidConditions("StartEstablishment","OBJSTATE","Enable","PreparationApproved");
        headbar.addCommandValidConditions("CompleteEstablishment","OBJSTATE","Enable","EstablishmentStarted;ReopenedForInstructions");
        headbar.addCommandValidConditions("Reopen","OBJSTATE","Enable","EstablishmentCompleted");
        headbar.addCommandValidConditions("StartReestablishment","OBJSTATE","Enable","EstablishmentCompleted;ReopenedForInstructions");
        headbar.addCommandValidConditions("CompleteReestablishment","OBJSTATE","Enable","ReestablishmentStarted");
        headbar.addCommandValidConditions("VerifyOperability","OBJSTATE","Enable","ReestablishmentCompleted");
        headbar.addCommandValidConditions("Cancel","OBJSTATE","Enable","New;UnderPreparation;PreparationApproved;Prepared");
        headbar.addCommandValidConditions("Historical","OBJSTATE","Enable","OperabilityVerified");
        //

        headbar.defineCommand(headbar.SAVERETURN,"saveReturn","checkMando()");
        headbar.defineCommand(headbar.SAVENEW,null,"checkMando()");
        headbar.defineCommand(headbar.DELETE ,"deleteRow");

        headlay = headblk.getASPBlockLayout();
        headlay.setFieldOrder("DELIMITATION_ORDER_NO");
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);  
        headlay.setFieldOrder("DELIMITATION_ORDER_NO,DESCRIPTION,ISOLATION_TYPE,CONTRACT,STATE,PLAN_START_ESTABLISHMENT,PLAN_FINISH_ESTABLISHMENT,PLAN_START_REESTABLISHMENT,PLAN_FINISH_REESTABLISHMENT,RANGE,CONDITIONS,EQUIPMENT_INVOLVED,DISTRIBUTION,NOTE,APPROVED_BY,ELECTRICAL_SECURITY_LEADER,LOCKCOUNT,SIGNCOUNT,DOCUMENT");
        headlay.defineGroup("","DELIMITATION_ORDER_NO,DESCRIPTION,ISOLATION_TYPE,STATE,CONTRACT,COMPANY",false,true);
        headlay.defineGroup(mgr.translate("PCMWDELIMITATIONORDERTABGRPLABEL1: Prepare"),"PLAN_START_ESTABLISHMENT,PLAN_FINISH_ESTABLISHMENT,PLAN_START_REESTABLISHMENT,PLAN_FINISH_REESTABLISHMENT,RANGE,CONDITIONS,EQUIPMENT_INVOLVED,NOTE,DISTRIBUTION,CBOBJECTTYPECHANGED,ELECTRICAL_SECURITY_LEADER,LOCKCOUNT,SIGNCOUNT,HEAD_PREPARED_BY,APPROVED_BY,VERIFIED_BY",true,true);  

        // 031216  ARWILK  Begin  (Replace blocks with tabs)
        tabs = mgr.newASPTabContainer();
        tabs.addTab(mgr.translate("PCMWDELIMITATIONORDERESTINSTRTAB: Establishment Instructions"), "javascript:commandSet('HEAD.activateEstablishmentInst', '')");
        tabs.addTab(mgr.translate("PCMWDELIMITATIONORDERREESINSTRTAB: Reestablishment Instructions"), "javascript:commandSet('HEAD.activateReestablishmentInst', '')");
        tabs.addTab(mgr.translate("PCMWDELIMITATIONORDERWORKPERMTAB: Work Permits"), "javascript:commandSet('HEAD.activateWorkPermits', '')");
        tabs.addTab(mgr.translate("PCMWDELIMITATIONORDERWORKORDTAB: Work Orders"), "javascript:commandSet('HEAD.activateWorkOrders', '')");
        tabs.addTab(mgr.translate("PCMWDELIMITATIONORDERHISTORYTAB: History"), "javascript:commandSet('HEAD.activateHistory', '')");
        // 031216  ARWILK  End  (Replace blocks with tabs)

        //-----------------------------------------------------------------------
        //---------- This block used to update RMB list box in Header------------
        //-----------------------------------------------------------------------

        eventblk = mgr.newASPBlock("EVNTBLK");

        eventblk.addField("DEF_OBJID").
        setHidden().
        setDbName("OBJID");

        eventblk.addField("DEF_OBJVERSION").
        setHidden().
        setDbName("OBJVERSION");

        eventblk.addField("EVNTBLK_OBJEVENTS"). 
        setDbName("OBJEVENTS").   
        setHidden();


        eventblk.setView("DELIMITATION_ORDER");
        eventset = eventblk.getASPRowSet();


        itemblk0 = mgr.newASPBlock("ITEM0");

        itemblk0.addField("ITEM0_OBJID").
        setHidden().
        setDbName("OBJID");

        itemblk0.addField("ITEM0_OBJVERSION").
        setHidden().
        setDbName("OBJVERSION");

        itemblk0.addField("ITEM0_DELIMITATION_ORDER_NO","Number").
        setSize(11).
        setDynamicLOV("DELIMITATION_ORDER",600,445).
        setMandatory().
        setHidden().
        setLabel("PCMWDELIMITATIONORDERTABDELORDNO: Isolation Order No").
        setDbName("DELIMITATION_ORDER_NO");

        itemblk0.addField("SEQUENCE_NO","Number").
        setSize(11).
        setMandatory().
        setReadOnly().
        setInsertable().
        setLabel("PCMWDELIMITATIONORDERTABROWNO: Row No");

        itemblk0.addField("ITEM0_CONTRACT").
        setSize(11).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445).
        setCustomValidation("ITEM0_CONTRACT","ITEM0_COMPANY").
        setLabel("PCMWDELIMITATIONORDERTABSITE: Site").
        setDbName("CONTRACT").
        setUpperCase();

        itemblk0.addField("ITEM0_TEMP").
        setHidden().
        setFunction("''");

        itemblk0.addField("MCH_CODE").
        setSize(11).
        setMaxLength(100).
        setDynamicLOV("EQUIPMENT_ALL_OBJECT","ITEM0_CONTRACT CONTRACT",600,445).
        setLabel("PCMWDELIMITATIONORDERTABOBJEID: Object ID").
        setCustomValidation("ITEM0_CONTRACT,MCH_CODE","MCH_NAME,MCH_LOC,MCH_POS,EQUIP_MCH_TYPE,MCH_TYPE").
        setUpperCase();

        itemblk0.addField("MCH_NAME").
        setSize(40).
        setLabel("PCMWDELIMITATIONORDERTABOBJDESC: Object Description").
        setReadOnly().
        setFunction("EQUIPMENT_ALL_OBJECT_API.GET_MCH_NAME(:ITEM0_CONTRACT,:MCH_CODE)");

        itemblk0.addField("MCH_LOC").
        setSize(11).
        setLabel("PCMWDELIMITATIONORDERTABROOM: Room").
        setReadOnly().
        setFunction("EQUIPMENT_FUNCTIONAL_API.Get_Mch_Loc(:ITEM0_CONTRACT,:MCH_CODE)");

        itemblk0.addField("MCH_POS").
        setSize(11).
        setReadOnly().
        setLabel("PCMWDELIMITATIONORDERTABPOS: Position").
        setFunction("EQUIPMENT_FUNCTIONAL_API.Get_Mch_Pos(:ITEM0_CONTRACT,:MCH_CODE)");

        itemblk0.addField("MCH_TYPE").
        setSize(11).
        setDynamicLOV("EQUIPMENT_OBJ_TYPE",600,445).
        setLabel("PCMWDELIMITATIONORDERTABOBJTYPE: Object Type").
        setUpperCase();

        itemblk0.addField("POSITION_ID").
        setSize(11).
        setDynamicLOV("EQUIPMENT_OBJECT_POSITION","MCH_TYPE",600,445).
        setLabel("PCMWDELIMITATIONORDERTABPOSID: Position Id").
        setUpperCase();

        itemblk0.addField("LOCK_NEEDS").
        setSize(11).
        setMandatory().
        setLabel("PCMWDELIMITATIONORDERTABLONEEDS: Lock Needs").
        enumerateValues("LOCK_NEEDS_API").
        setSelectBox();

        itemblk0.addField("SIGN_NEEDS").
        setSize(11).
        setMandatory().
        setLabel("PCMWDELIMITATIONORDERTABSIGNEEDS: Sign Needs").
        setSelectBox().
        enumerateValues("SIGN_NEEDS_API");

        itemblk0.addField("PART_ELECTRICAL_DISCONN").
        setSize(25).
        setMandatory().
        setLabel("PCMWDELIMITATIONORDERTABPARTELECDISC: Part Elec. Disconnection").
        setSelectBox().
        enumerateValues("PART_ELECTRICAL_DISCONN_API");

        itemblk0.addField("DATE_PREPARED","Datetime").
        setSize(20).
        setLabel("PCMWDELIMITATIONORDERTABDATPRE: Date Prepared");

        itemblk0.addField("PREPARED_BY").
        setSize(11).
        setDynamicLOV("EMPLOYEE_LOV","ITEM0_COMPANY COMPANY",600,445).
        setLabel("PCMWDELIMITATIONORDERTABPREPAREDBY: Prepared by").
        setUpperCase().
        setMandatory().                
        setCustomValidation("COMPANY,PREPARED_BY","PREPARED_BY_ID");

        itemblk0.addField("PREPARED_BY_ID").
        setSize(8).
        setHidden().
        setMandatory().
        setLabel("PCMWDELIMITATIONORDERTABPREPARED_BY_ID: Prepared by Id").
        setUpperCase();

        itemblk0.addField("ITEM0_COMPANY").
        setSize(6).
        setMandatory().
        setHidden().
        setLabel("PCMWDELIMITATIONORDERTABITEM0_COMPANY: Site").
        setDbName("COMPANY").
        setUpperCase();

        itemblk0.addField("ITEM0_NOTE").
        setSize(40).
        setDefaultNotVisible().
        setLabel("PCMWDELIMITATIONORDERTABNOTE: Note").
        setMaxLength(2000).
        setDbName("NOTE");

        itemblk0.addField("EQUIP_MCH_TYPE").
        setSize(25).
        setDefaultNotVisible().
        setLabel("PCMWDELIMITATIONORDERTABEQOBJTYPE: Equipment Object Type").
        setReadOnly().
        setFunction("EQUIPMENT_FUNCTIONAL_API.Get_Mch_Type(:ITEM0_CONTRACT,:MCH_CODE)");

        itemblk0.addField("DELIMITATION_ID","Number","#").
        setSize(8).
        setDynamicLOV("DELIMITATION",600,445).setLOVProperty("WHERE","Delimitation_Api.Get_Approved_By(delimitation_id) is not null").
        setLabel("DELIMITATIONID: Isolation Id");

        itemblk0.addField("DEL_DESCRIPTION").
        setSize(25).
        setLabel("DELDESCRIPTION: Description").
        setReadOnly().
        setFunction("DELIMITATION_API.Get_Description(:DELIMITATION_ID)");

        itemblk0.addField("EXIST_REEST").
        setFunction("DELIMITATION_ORDER_UTILITY_API.Exist_Reest_Instructions(:ITEM0_DELIMITATION_ORDER_NO)").
        setHidden();

        itemblk0.setView("EST_DEL_ORDER_LINE");
        itemblk0.defineCommand("EST_DEL_ORDER_LINE_API","New__,Modify__,Remove__");
        itemblk0.setMasterBlock(headblk);

        itemset0 = itemblk0.getASPRowSet();

        itembar0 = mgr.newASPCommandBar(itemblk0);
        itembar0.enableCommand(itembar0.FIND);
        itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
        itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
        itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");

        itembar0.defineCommand(itembar0.SAVERETURN,null,"checkItem0Fields(-1)");
        itembar0.defineCommand(itembar0.SAVENEW,null,"checkItem0Fields(-1)");

        itembar0.addCustomCommand("createReEst",mgr.translate("PCMWDELIMITATIONORDERTABCREREEST: Create Reestablishment"));
        itembar0.addCommandValidConditions("createReEst","EXIST_REEST","Enable","FALSE");

        itemtbl0 = mgr.newASPTable(itemblk0);
        itemtbl0.setWrap();

        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setFieldOrder("ITEM0_DELIMITATION_ORDER_NO");
        itemlay0.setDialogColumns(2);
        itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);

        itemblk1 = mgr.newASPBlock("ITEM1");

        itemblk1.addField("ITEM1_OBJID").
        setHidden().
        setDbName("OBJID");

        itemblk1.addField("ITEM1_OBJVERSION").
        setHidden().
        setDbName("OBJVERSION");

        itemblk1.addField("ITEM1_DELIMITATION_ORDER_NO","Number").
        setSize(11).
        setDynamicLOV("DELIMITATION_ORDER",600,445).
        setMandatory().
        setHidden().
        setLabel("PCMWDELIMITATIONORDERTABITEM1_DELIMITATION_ORDER_NO: Isolation Order No").
        setDbName("DELIMITATION_ORDER_NO");

        itemblk1.addField("ITEM1_SEQUENCE_NO","Number").
        setSize(8).
        setMandatory().
        setReadOnly().
        setInsertable().
        setLabel("PCMWDELIMITATIONORDERTABROWNO: Row No").
        setDbName("SEQUENCE_NO");

        itemblk1.addField("ITEM1_CONTRACT").
        setSize(11).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445).
        setLabel("PCMWDELIMITATIONORDERTABSITE: Site").
        setCustomValidation("ITEM1_CONTRACT","ITEM1_COMPANY").
        setDbName("CONTRACT").
        setUpperCase();

        itemblk1.addField("ITEM1_MCH_CODE").
        setSize(16).
        setMaxLength(100).
        setDynamicLOV("EQUIPMENT_ALL_OBJECT","ITEM1_CONTRACT CONTRACT",600,445).
        setLabel("PCMWDELIMITATIONORDERTABITEM1_MCH_CODE: Object ID").
        setDbName("MCH_CODE").
        setCustomValidation("ITEM1_CONTRACT,ITEM1_MCH_CODE","ITEM1_MCH_NAME,ITEM1_MCH_LOC,ITEM1_MCH_POS,ITEM1_EQUIP_MCH_TYPE,ITEM1_MCH_TYPE").
        setUpperCase();

        itemblk1.addField("ITEM1_MCH_NAME").
        setSize(30).
        setReadOnly().
        setLabel("PCMWDELIMITATIONORDERTABOBJDESC: Object Description").
        setFunction("EQUIPMENT_ALL_OBJECT_API.GET_MCH_NAME(:ITEM1_CONTRACT,:ITEM1_MCH_CODE)");

        itemblk1.addField("ITEM1_MCH_LOC").
        setSize(11).
        setReadOnly().
        setLabel("PCMWDELIMITATIONORDERTABROOM: Room").
        setFunction("EQUIPMENT_FUNCTIONAL_API.Get_Mch_Loc(:ITEM1_CONTRACT,:ITEM1_MCH_CODE)");

        itemblk1.addField("ITEM1_MCH_POS").
        setSize(11).
        setReadOnly().
        setLabel("PCMWDELIMITATIONORDERTABPOS: Position").
        setFunction("EQUIPMENT_FUNCTIONAL_API.Get_Mch_Pos(:ITEM1_CONTRACT,:ITEM1_MCH_CODE)");

        itemblk1.addField("ITEM1_MCH_TYPE").
        setSize(11).
        setDynamicLOV("EQUIPMENT_OBJ_TYPE",600,445).
        setLabel("PCMWDELIMITATIONORDERTABOBJTYPE: Object Type").
        setDbName("MCH_TYPE").
        setUpperCase();

        itemblk1.addField("ITEM1_POSITION_ID").
        setSize(11).
        setDynamicLOV("EQUIPMENT_OBJECT_POSITION","ITEM1_MCH_TYPE",600,445).
        setLabel("PCMWDELIMITATIONORDERTABITEM1_POSITION_ID: Position Id").
        setDbName("POSITION_ID").                             
        setUpperCase();


        itemblk1.addField("ITEM1_PART_ELECTRICAL_DISCONN").
        setSize(25).
        setMandatory().
        setLabel("PCMWDELIMITATIONORDERTABPARTELECDISCONN: Part Elec. Disconnection").
        setDbName("PART_ELECTRICAL_DISCONN").
        setSelectBox().
        enumerateValues("PART_ELECTRICAL_DISCONN_API");

        itemblk1.addField("ITEM1_DATE_PREPARED","Datetime").
        setSize(13).
        setLabel("PCMWDELIMITATIONORDERTABDATEPRE: Date Prepared").
        setDbName("DATE_PREPARED");

        itemblk1.addField("ITEM1_PREPARED_BY").
        setSize(11).
        setDynamicLOV("EMPLOYEE_LOV","COMPANY ITEM1_COMPANY",600,445).
        setLabel("PCMWDELIMITATIONORDERTABPREBY: Prepared by").
        setDbName("PREPARED_BY").
        setMandatory().                        
        setUpperCase();

        itemblk1.addField("ITEM1_PREPARED_BY_ID").
        setSize(8).
        setHidden().
        setLabel("PCMWDELIMITATIONORDERTABPREBYID: Prepared by Id").
        setDbName("PREPARED_BY_ID").
        setUpperCase();

        itemblk1.addField("ITEM1_COMPANY").
        setSize(6).
        setMandatory().
        setHidden().
        setLabel("PCMWDELIMITATIONORDERTABCOMPANY: Company").
        setDbName("COMPANY").
        setUpperCase();

        itemblk1.addField("ITEM1_TEMP").
        setHidden().
        setFunction("''");

        itemblk1.addField("ITEM1_NOTE").
        setSize(20).
        setDefaultNotVisible().
        setLabel("PCMWDELIMITATIONORDERTABNOTE: Note").
        setDbName("NOTE");

        itemblk1.addField("ITEM1_EQUIP_MCH_TYPE").
        setSize(25).
        setReadOnly().
        setDefaultNotVisible().
        setLabel("PCMWDELIMITATIONORDERTABEQUIPOBJTYPE: Equipment Object Type").
        setFunction("EQUIPMENT_FUNCTIONAL_API.Get_Mch_Type(:ITEM1_CONTRACT,:ITEM1_MCH_CODE)");

        itemblk1.addField("ITEM1_DELIMITATION_ID","Number","#").
        setDbName("DELIMITATION_ID").
        setDynamicLOV("DELIMITATION",600,445).setLOVProperty("WHERE","Delimitation_Api.Get_Approved_By(delimitation_id) is not null").
        setSize(8).
        setLabel("ITEM1DELIMITATIONID: Isolation Id");

        itemblk1.addField("ITEM1_DEL_DESCRIPTION").
        setSize(25).
        setLabel("ITEM1DELDESCRIPTION: Description").
        setReadOnly().
        setFunction("DELIMITATION_API.Get_Description(:ITEM1_DELIMITATION_ID)");

        itemblk1.setView("REEST_DEL_ORDER_LINE");
        itemblk1.defineCommand("REEST_DEL_ORDER_LINE_API","New__,Modify__,Remove__,START_PREPARATION__,PREPARE__,REVISE__,APPROVE_PREPARATION__,START_ESTABLISHMENT__,COMPLETE_ESTABLISHMENT__,REOPEN__,START_REESTABLISHMENT__,COMPLETE_REESTABLISHMENT__,VERIFY_OPERABILITY__");
        itemblk1.setMasterBlock(headblk);

        itemset1 = itemblk1.getASPRowSet();

        itembar1 = mgr.newASPCommandBar(itemblk1);
        itembar1.enableCommand(itembar1.FIND);
        itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
        itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
        itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1");

        itemtbl1 = mgr.newASPTable(itemblk1);
        itembar1.defineCommand(itembar1.SAVERETURN,null,"checkItem1Fields()");
        itembar1.defineCommand(itembar1.SAVENEW,null,"checkItem1Fields()"); 
        itemtbl1.setWrap();

        itemlay1 = itemblk1.getASPBlockLayout();
        itemlay1.setFieldOrder("ITEM1_DELIMITATION_ORDER_NO");
        itemlay1.setDialogColumns(2);
        itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);



        itemblk2 = mgr.newASPBlock("ITEM2");

        itemblk2.addField("ITEM2_OBJID").
        setHidden().
        setDbName("OBJID");

        itemblk2.addField("ITEM2_OBJVERSION").
        setHidden().
        setDbName("OBJVERSION");

        itemblk2.addField("ITEM2_DELIMITATION_ORDER_NO","Number").
        setSize(11).
        setDynamicLOV("DELIMITATION_ORDER",600,445).
        setHidden().
        setLabel("PCMWDELIMITATIONORDERTABDELORDNO: Isolation Order No").
        setDbName("DELIMITATION_ORDER_NO");

        itemblk2.addField("ITEM2_CONTRACT").
        setSize(11).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445).
        setLabel("PCMWDELIMITATIONORDERTABITEM2_CONTRACT: Site").
        setFunction("PERMIT_API.Get_Contract(:PERMIT_SEQ)").
        setReadOnly().
        setUpperCase();  

        itemblk2.addField("PERMIT_SEQ","Number").
        setSize(15).
        setLabel("PCMWDELIMITATIONORDERTABPERMITSEQ: Permit Seq.").
        setReadOnly();

        itemblk2.addField("VALID_FROM_DATE","Datetime").
        setSize(15).
        setLabel("PCMWDELIMITATIONORDERTABVALIDDATE: Valid From Date").
        setFunction("PERMIT_API.Get_Valid_From_Date(:PERMIT_SEQ)").
        setReadOnly();

        itemblk2.addField("VALID_TO_DATE","Datetime").
        setSize(14).
        setLabel("PCMWDELIMITATIONORDERTABVALID_TO_DATE: Valid To Date").
        setFunction("PERMIT_API.Get_Valid_To_Date(:PERMIT_SEQ)").
        setReadOnly();

        itemblk2.addField("ITEM2_APPROVED_BY").
        setSize(11).
        setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445).
        setLabel("PCMWDELIMITATIONORDERTABITEM2APPROVEDBY: Prepared By").
        setFunction("PERMIT_API.Get_Prepared_By(:PERMIT_SEQ)").
        setReadOnly().
        setMandatory().               
        setUpperCase();

        itemblk2.addField("ITEM2_STATE").
        setSize(11).
        setLabel("PCMWDELIMITATIONORDERTABITEM2_STATE: Status").
        setFunction("PERMIT_API.Get_Rowstate(:PERMIT_SEQ)").
        setReadOnly();  

        itemblk2.setView("ISOLATION_ORDER_PERMIT");
        itemblk2.defineCommand("ISOLATION_ORDER_PERMIT_API","New__,Modify__,Remove__");
        itemblk2.setMasterBlock(headblk);
        itemset2 = itemblk2.getASPRowSet();

        itembar2 = mgr.newASPCommandBar(itemblk2);
        itembar2.enableCommand(itembar2.FIND);
        itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");
        itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
        itembar2.disableCommand(itembar2.NEWROW);
        itembar2.disableCommand(itembar2.EDITROW);

        itembar2.addCustomCommand("prePermit",mgr.translate("PCMWDELIMITATIONORDERTABPREPER: Prepare Permit..."));

        itemtbl2 = mgr.newASPTable(itemblk2);
        itemtbl2.setWrap();

        itemlay2 = itemblk2.getASPBlockLayout();
        itemlay2.setFieldOrder("ITEM2_DELIMITATION_ORDER_NO");
        itemlay2.setDialogColumns(2);
        itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);


        //////////Definition for Itemblock 3///////


        itemblk3 = mgr.newASPBlock("ITEM3");

        itemblk3.addField("ITEM3_OBJID").
        setHidden().
        setDbName("OBJID");

        itemblk3.addField("ITEM3_OBJVERSION").
        setHidden().
        setDbName("OBJVERSION");

        itemblk3.addField("ITEM3_DELIMITATION_ORDER_NO","Number").
        setSize(11).
        setDynamicLOV("DELIMITATION_ORDER",600,445).
        setMandatory().
        setHidden().
        setReadOnly().
        setLabel("PCMWDELIMITATIONORDERTABDELORDNO: Isolation Order No").
        setDbName("DELIMITATION_ORDER_NO");

        itemblk3.addField("WO_NO","Number","#").
        setSize(11).
        setDynamicLOV("ACTIVE_WORK_ORDER",600,445).
        setLOVProperty("WHERE","CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)").
        setMandatory().
        setReadOnly().
        setInsertable().
        setCustomValidation("WO_NO","WORKORDERMCHCODE, WORKORDERERRDESCR, WORKORDERPLANSDATE, WORKORDERPLANFDATE").
        setLabel("PCMWDELIMITATIONORDERTABWONO: WO No").
        setHyperlink("ActiveSeparate2.page","WO_NO","NEWWIN");

        itemblk3.addField("WORKORDERMCHCODE").
        setSize(19).
        setLabel("PCMWDELIMITATIONORDERTABWORKORDERMCHCODE: Object ID").
        setFunction("WORK_ORDER_API.Get_Mch_Code(:WO_NO)").
        setReadOnly().
        setUpperCase();

        itemblk3.addField("WORKORDERERRDESCR").
        setSize(24).
        setLabel("PCMWDELIMITATIONORDERTABDIR: Directive").
        setFunction("WORK_ORDER_API.Get_Err_Descr(:WO_NO)").
        setReadOnly();

        itemblk3.addField("WORKORDERPLANSDATE","Datetime","yyyy-MM-dd HH:mm:ss").
        setSize(25).
        setLabel("PCMWDELIMITATIONORDERTABWORKORDERPLANSDATE: Planned Start").
        setReadOnly().
        setFunction("WORK_ORDER_API.Get_Plan_S_Date(:WO_NO)");

        itemblk3.addField("WORKORDERPLANFDATE","Datetime").
        setSize(25).
        setLabel("PCMWDELIMITATIONORDERTABWORKORDERPLANFDATE: Planned Completion").
        setFunction("WORK_ORDER_API.Get_Plan_F_Date(:WO_NO)").
        setReadOnly();

        itemblk3.setView("DELIMITATION_WORK_ORDER");
        itemblk3.defineCommand("DELIMITATION_WORK_ORDER_API","New__,Modify__,Remove__");
        itemblk3.setMasterBlock(headblk);

        itemset3 = itemblk3.getASPRowSet();

        itembar3 = mgr.newASPCommandBar(itemblk3);
        itembar3.enableCommand(itembar3.FIND);
        itembar3.defineCommand(itembar3.OKFIND,"okFindITEM3");
        itembar3.defineCommand(itembar3.COUNTFIND,"countFindITEM3");
        itembar3.defineCommand(itembar3.NEWROW,"newRowITEM3");
        itembar3.disableCommand(itembar3.EDITROW);

        itembar3.defineCommand(itembar3.SAVERETURN,null,"checkMandoItem3()");
        itembar3.defineCommand(itembar3.SAVENEW,null,"checkMandoItem3()");   

        itemtbl3 = mgr.newASPTable(itemblk3);
        itemtbl3.setWrap();

        itemlay3 = itemblk3.getASPBlockLayout();
        itemlay3.setFieldOrder("ITEM3_DELIMITATION_ORDER_NO");
        itemlay3.setDialogColumns(2);
        itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);


        /////////////Definition for itemblock 4

        itemblk4 = mgr.newASPBlock("ITEM4");

        itemblk4.addField("ITEM4_OBJID").
        setHidden().
        setDbName("OBJID");

        itemblk4.addField("ITEM4_OBJVERSION").
        setHidden().
        setDbName("OBJVERSION");

        itemblk4.addField("ITEM4_DELIMITATION_ORDER_NO","Number").
        setSize(11).
        setMandatory().
        setHidden().
        setLabel("PCMWDELIMITATIONORDERTABDELORDNO: Isolation Order No").
        setDbName("DELIMITATION_ORDER_NO");

        itemblk4.addField("CREATE_DATE","Datetime").
        setSize(17).
        setReadOnly().
        setLabel("PCMWDELIMITATIONORDERTABCREATE_DATE: Creation Date");

        itemblk4.addField("USER_NAME").
        setSize(12).
        setReadOnly().
        setLabel("PCMWDELIMITATIONORDERTABUSER_NAME: User Name").
        setUpperCase();

        itemblk4.addField("DELIMITATION_ORDER_STATUS").
        setSize(20).
        setReadOnly().
        setLabel("PCMWDELIMITATIONORDERTABDELORDSTAT: Isolation Order Status");

        itemblk4.addField("ITEM4_PLAN_START_ESTABLISHMENT","Datetime").
        setSize(20).
        setReadOnly().
        setLabel("PCMWDELIMITATIONORDERTABITEM4_PLAN_START_ESTABLISHMENT: Planned Start Est.").
        setDbName("PLAN_START_ESTABLISHMENT");

        itemblk4.addField("ITEM4_PLAN_FINISH_ESTABLISHMENT","Datetime").
        setSize(23).
        setReadOnly().
        setLabel("PCMWDELIMITATIONORDERTABITEM4_PLAN_FINISH_ESTABLISHMENT: Planned Finish Est.").
        setDbName("PLAN_FINISH_ESTABLISHMENT");

        itemblk4.addField("ITEM4_PLAN_START_REESTABLISHMENT","Datetime").
        setSize(22).
        setReadOnly().
        setLabel("PCMWDELIMITATIONORDERTABPLANSTREEST: Planned Start Reest.").
        setDbName("PLAN_START_REESTABLISHMENT");

        itemblk4.addField("ITEM4_PLAN_FINISH_REESTABLISHMENT","Datetime").
        setSize(20).
        setReadOnly().
        setLabel("PCMWDELIMITATIONORDERTABITEM4_PLAN_FINISH_REESTABLISHMENT: Planned Finish Reest.").
        setDbName("PLAN_FINISH_REESTABLISHMENT");

        itemblk4.setView("DELIMITATION_ORDER_HISTORY");
        itemblk4.defineCommand("DELIMITATION_ORDER_HISTORY_API","New__,Modify__,Remove__,START_PREPARATION__,PREPARE__,REVISE__,APPROVE_PREPARATION__,START_ESTABLISHMENT__,COMPLETE_ESTABLISHMENT__,REOPEN__,START_REESTABLISHMENT__,COMPLETE_REESTABLISHMENT__,VERIFY_OPERABILITY__");
        itemblk4.setMasterBlock(headblk);

        itemset4 = itemblk4.getASPRowSet();

        itembar4 = mgr.newASPCommandBar(itemblk4);
        itembar4.enableCommand(itembar4.FIND);
        itembar4.defineCommand(itembar4.OKFIND,"okFindITEM4");
        itembar4.defineCommand(itembar4.COUNTFIND,"countFindITEM4");
        itembar4.disableCommand(itembar4.NEWROW);
        itembar4.disableCommand(itembar4.EDITROW);
        itembar4.disableCommand(itembar4.DELETE);
        itembar4.disableCommand(itembar4.DUPLICATEROW);

        itemtbl4 = mgr.newASPTable(itemblk4);
        itemtbl4.setWrap();

        itemlay4 = itemblk4.getASPBlockLayout();
        itemlay4.setFieldOrder("ITEM4_DELIMITATION_ORDER_NO");
        itemlay4.setDialogColumns(2);
        itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT);

        b = mgr.newASPBlock("LOCK_NEEDS");

        b.addField("CLIENT_VALUES0");

        b = mgr.newASPBlock("SIGN_NEEDS");

        b.addField("CLIENT_VALUES1");

        b = mgr.newASPBlock("PART_ELECTRICAL_DISCONN");

        b.addField("CLIENT_VALUES2");

        b = mgr.newASPBlock("ITEM1_PART_ELECTRICAL_DISCONN");

        b.addField("CLIENT_VALUES3");

        b = mgr.newASPBlock("NEW_DEL_ORD_NO");

        b.addField("NEW_DELIMITATION_ORDER_NO","Number","0.");
        b.addField("ATTR0");
        b.addField("ATTR1");
        b.addField("ATTR2");
        b.addField("ATTR3");
        b.addField("ATTR4");

    }

    public void chkAvailability()
    {
        if (!chksec)
        {

            ASPManager mgr = getASPManager();

            ASPBuffer availObj;
            trans.clear();

            trans.addSecurityQuery("DELIMITATION_ORDER");
            trans.addSecurityQuery("Delimitation_API","Copy");

            trans = mgr.perform(trans);

            availObj = trans.getSecurityInfo();

            if ( availObj.itemExists("Delimitation_API.Copy") )
                enable1 = true;

            if ( availObj.itemExists("DELIMITATION_ORDER") )
                enable2 = true;

            chksec = true;
        }

        if (!enable1)
            headbar.removeCustomCommand("cpyDelOrd");

        if (!enable2)
        {
            headbar.removeCustomCommand("print");
            headbar.removeCustomCommand("printSigns");  
        }
    }

    // 031216  ARWILK  Begin  (Replace blocks with tabs)
    public void activateEstablishmentInst()
    {
        tabs.setActiveTab(1);
        this.okFindITEM0();
    }

    public void activateReestablishmentInst()
    {
        tabs.setActiveTab(2);
        this.okFindITEM0();
    }

    public void activateWorkPermits()
    {
        tabs.setActiveTab(3);
    }

    public void activateWorkOrders()
    {
        tabs.setActiveTab(4);
    }

    public void activateHistory()
    {
        tabs.setActiveTab(5);
    }
    // 031216  ARWILK  End  (Replace blocks with tabs)

    public void  adjust()
    {
        ASPManager mgr = getASPManager();

        // 031216  ARWILK  Begin  (Replace blocks with tabs)
        headbar.removeCustomCommand("activateEstablishmentInst");
        headbar.removeCustomCommand("activateReestablishmentInst"); 
        headbar.removeCustomCommand("activateWorkPermits"); 
        headbar.removeCustomCommand("activateWorkOrders");  
        headbar.removeCustomCommand("activateHistory");
        // 031216  ARWILK  End  (Replace blocks with tabs)

        if (mgr.isPresentationObjectInstalled("docmaw/DocReference.page"))
            mgr.getASPField("DOCUMENT").setHyperlink("../docmaw/DocReference.page","LU_NAME,KEY_REF","NEWWIN");

        if (headset.countRows() == 1)
            headbar.disableCommand(headbar.BACK);

        if (headlay.isFindLayout() || itemlay0.isFindLayout() || itemlay1.isFindLayout() || itemlay2.isFindLayout())
            isFind = true;

        if (itemlay0.isNewLayout() || itemlay0.isEditLayout() || itemlay0.isFindLayout() || itemlay1.isNewLayout() || itemlay1.isEditLayout() || itemlay1.isFindLayout() || itemlay2.isNewLayout() || itemlay2.isEditLayout() || itemlay3.isNewLayout() || itemlay3.isEditLayout() || itemlay4.isEditLayout() || itemlay2.isFindLayout() || itemlay3.isFindLayout())
        {
            headbar.disableCommand(headbar.DELETE);
            headbar.disableCommand(headbar.NEWROW);
            headbar.disableCommand(headbar.EDITROW);
            headbar.disableCommand(headbar.BACK);
            headbar.disableCommand(headbar.DELETE);
            headbar.disableCommand(headbar.DUPLICATEROW);
            headbar.disableCommand(headbar.FIND);
            headbar.disableCommand(headbar.BACKWARD);
            headbar.disableCommand(headbar.FORWARD);

            if (mgr.isPresentationObjectInstalled("docmaw/DocReference.page"))
                headbar.removeCustomCommand("documentation");

            headbar.removeCustomCommand("cpyDelOrd");
            headbar.removeCustomCommand("print");
            headbar.removeCustomCommand("printSigns");
            headbar.removeCustomCommand("StartPreparation");
            headbar.removeCustomCommand("Preparation");
            headbar.removeCustomCommand("Revise");
            headbar.removeCustomCommand("ApprovePreparation");
            headbar.removeCustomCommand("StartEstablishment");
            headbar.removeCustomCommand("CompleteEstablishment");
            headbar.removeCustomCommand("Reopen");
            headbar.removeCustomCommand("StartReestablishment");
            headbar.removeCustomCommand("CompleteReestablishment");
            headbar.removeCustomCommand("VerifyOperability");
            headbar.removeCustomCommand("Cancel");
            headbar.removeCustomCommand("Historical");
        }

        if (headset.countRows()>0)
        {
            /* if (headlay.isSingleLayout()  ||   headset.countRows() == 1)
         {
             String enabled_events = "^"+headset.getRow().getFieldValue("HEAD_OBJEVENTS");

             if (enabled_events.indexOf("^StartPreparation^",0)<0)
                 headbar.removeCustomCommand("StartPreparation");

             if (enabled_events.indexOf("^Prepare^",0)<0)
                 headbar.removeCustomCommand("Preparation");

             if (enabled_events.indexOf("^Revise^",0)<0)
                 headbar.removeCustomCommand("Revise");

             if (enabled_events.indexOf("^ApprovePreparation^",0)<0)
                 headbar.removeCustomCommand("ApprovePreparation");

             if ((enabled_events.indexOf("^StartEstablishment^",0)<0) && (enabled_events.indexOf("^Restart^",0)<0))
                 headbar.removeCustomCommand("StartEstablishment");

             if (enabled_events.indexOf("^CompleteEstablishment^",0)<0)
                 headbar.removeCustomCommand("CompleteEstablishment");

             if (enabled_events.indexOf("^Reopen^",0)<0)
                 headbar.removeCustomCommand("Reopen");

             if (enabled_events.indexOf("^StartReestablishment^",0)<0)
                 headbar.removeCustomCommand("StartReestablishment");

             if (enabled_events.indexOf("^CompleteReestablishment^",0)<0)
                 headbar.removeCustomCommand("CompleteReestablishment");
             if (enabled_events.indexOf("^VerifyOperability^",0)<0)
                 headbar.removeCustomCommand("VerifyOperability");
                             if (enabled_events.indexOf("^Cancel^",0)<0)
                 headbar.removeCustomCommand("Cancel");
                             if (enabled_events.indexOf("^Historical^",0)<0)
                 headbar.removeCustomCommand("Historical");

         }  */
        }

        if ( headset.countRows() == 0 )
        {
            mgr.getASPField("HEAD_PREPARED_BY").setDynamicLOV("EMPLOYEE_LOV",600,445);
            mgr.getASPField("HEAD_PREPARED_BY").setLOVProperty("WHERE","COMPANY IS NOT NULL");


            mgr.getASPField("APPROVED_BY").setDynamicLOV("EMPLOYEE_LOV",600,445);
            mgr.getASPField("APPROVED_BY").setLOVProperty("WHERE","COMPANY IS NOT NULL");

            mgr.getASPField("VERIFIED_BY").setDynamicLOV("EMPLOYEE_LOV",600,445);
            mgr.getASPField("VERIFIED_BY").setLOVProperty("WHERE","COMPANY IS NOT NULL");

            mgr.getASPField("ELECTRICAL_SECURITY_LEADER").setDynamicLOV("EMPLOYEE_LOV",600,445);
            mgr.getASPField("ELECTRICAL_SECURITY_LEADER").setLOVProperty("WHERE","COMPANY IS NOT NULL");




        }


        fldTitleElectricalSecurityLeader = mgr.translate("PCMWDELIMITATIONORDERTABELECTSECLEADFLD: Electrical+Security+Leader");
        fldTitleApprovedby = mgr.translate("PCMWDELIMITATIONORDERTABAPPROVEDBYFLD: Approved+by");
        fldTitlePreparedby0 = mgr.translate("PCMWDELIMITATIONORDERTABPREPAREDBYFLD0: Prepared+by");
        fldTitlePreparedby1 = mgr.translate("PCMWDELIMITATIONORDERTABPREPAREDBYFLD1: Prepared+by");
        fldTitlePreparedby2 = mgr.translate("PCMWDELIMITATIONORDERTABPREPAREDBYFLD2: Prepared+by");

        lovTitleElectricalSecurityLeader = mgr.translate("PCMWDELIMITATIONORDERTABELECTSECLEADLOV: List+of+Electrical+Security+Leader");
        lovTitleApprovedby = mgr.translate("PCMWDELIMITATIONORDERTABAPPROVEDBYLOV: List+of+Approved+by");
        lovTitlePreparedby0 = mgr.translate("PCMWDELIMITATIONORDERTABPREPAREDBYLOV0: List+of+Prepared+by");
        lovTitlePreparedby1 = mgr.translate("PCMWDELIMITATIONORDERTABPREPAREDBYLOV1: List+of+Prepared+by");
        lovTitlePreparedby2 = mgr.translate("PCMWDELIMITATIONORDERTABPREPAREDBYLOV2: List+of+Prepared+by");

    }


//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWDELIMITATIONORDERTABTITLE: Isolation Order";
    }

    protected String getTitle()
    {
        return "PCMWDELIMITATIONORDERTABTITLE: Isolation Order";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.show());

        // 031216  ARWILK  Begin  (Replace blocks with tabs)
        if (headlay.isSingleLayout() && (headset.countRows() > 0))
        {
            appendToHTML(tabs.showTabsInit());

            if (tabs.getActiveTab() == 1)
                appendToHTML(itemlay0.show());
            else if (tabs.getActiveTab() == 2)
                appendToHTML(itemlay1.show());
            else if (tabs.getActiveTab() == 3)
                appendToHTML(itemlay2.show());
            else if (tabs.getActiveTab() == 4)
                appendToHTML(itemlay3.show());
            else if (tabs.getActiveTab() == 5)
                appendToHTML(itemlay4.show());
        }
        // 031216  ARWILK  End  (Replace blocks with tabs)

        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");


        printHiddenField("REFRESHPARENT1", "FALSE");
        appendDirtyJavaScript("window.name='delimitation';\n");

        //Bug 72024, Start
        if (headlay.isEditLayout())
            appendDirtyJavaScript("document.form.OLD_S_DATE.value = document.form.PLAN_START_ESTABLISHMENT.value;\n");
        //Bug 72024, End

        appendDirtyJavaScript("function checkMando()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("  return checkDescription(0);\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function checkMandoItem0()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("  return checkSequenceNo(0);\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function checkMandoItem3()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript(" return checkWoNo(0);\n");
        appendDirtyJavaScript("} \n");

        /*appendDirtyJavaScript("function lovElectricalSecurityLeader(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("  if('");
        appendDirtyJavaScript(isFind);
        appendDirtyJavaScript("' == 'True')\n");
        appendDirtyJavaScript("  { \n");
        appendDirtyJavaScript("     openLOVWindow('ELECTRICAL_SECURITY_LEADER',i,\n");
        appendDirtyJavaScript("	'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitleElectricalSecurityLeader);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleElectricalSecurityLeader);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("	+ '&COMPANY=null'\n");
        appendDirtyJavaScript("	 ,600,445,'validateElectricalSecurityLeader');\n");
        appendDirtyJavaScript("  }  \n");
        appendDirtyJavaScript("  else\n");
        appendDirtyJavaScript("  {\n");
        appendDirtyJavaScript("    openLOVWindow('ELECTRICAL_SECURITY_LEADER',i,\n");
        appendDirtyJavaScript("        '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitleElectricalSecurityLeader);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleElectricalSecurityLeader);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("	+ '&COMPANY=' + getValue_('COMPANY',i)\n");
        appendDirtyJavaScript("	,600,445,'validateElectricalSecurityLeader');\n");
        appendDirtyJavaScript("  }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovApprovedBy(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("  if('");
        appendDirtyJavaScript(isFind);
        appendDirtyJavaScript("' == 'True')\n");
        appendDirtyJavaScript("  { \n");
        appendDirtyJavaScript("    openLOVWindow('APPROVED_BY',i,\n");
        appendDirtyJavaScript("     '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitleApprovedby);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleApprovedby);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("    + '&COMPANY= null'\n");
        appendDirtyJavaScript("    ,600,445,'validateApprovedBy');\n");
        appendDirtyJavaScript("  }\n");
        appendDirtyJavaScript("  else\n");
        appendDirtyJavaScript("  {\n");
        appendDirtyJavaScript("     openLOVWindow('APPROVED_BY',i,\n");
        appendDirtyJavaScript("	'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitleApprovedby);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleApprovedby);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("	+ '&COMPANY=' + getValue_('COMPANY',i)\n");
        appendDirtyJavaScript("	,600,445,'validateApprovedBy');\n");
        appendDirtyJavaScript("  }	\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovPreparedBy(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("  if('");
        appendDirtyJavaScript(isFind);
        appendDirtyJavaScript("' == 'True')\n");
        appendDirtyJavaScript("  { \n");
        appendDirtyJavaScript("	openLOVWindow('PREPARED_BY',i,\n");
        appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitlePreparedby0);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitlePreparedby0);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		+ '&COMPANY= null'\n");
        appendDirtyJavaScript("		,600,445,'validatePreparedBy');\n");
        appendDirtyJavaScript("  }\n");
        appendDirtyJavaScript("  else\n");
        appendDirtyJavaScript("  {\n");
        appendDirtyJavaScript("    	openLOVWindow('PREPARED_BY',i,\n");
        appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitlePreparedby0);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitlePreparedby0);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		+ '&COMPANY=' + getValue_('ITEM0_COMPANY',i)\n");
        appendDirtyJavaScript("		,600,445,'validatePreparedBy');\n");
        appendDirtyJavaScript("  }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovItem1PreparedBy(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("  if('");
        appendDirtyJavaScript(isFind);
        appendDirtyJavaScript("' == 'True')\n");
        appendDirtyJavaScript("  { \n");
        appendDirtyJavaScript("	openLOVWindow('ITEM1_PREPARED_BY',i,\n");
        appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitlePreparedby1);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitlePreparedby1);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		+ '&COMPANY= null'\n");
        appendDirtyJavaScript("		,600,445,'validateItem1PreparedBy');\n");
        appendDirtyJavaScript("  }\n");
        appendDirtyJavaScript("  else\n");
        appendDirtyJavaScript("  {\n");
        appendDirtyJavaScript("       openLOVWindow('ITEM1_PREPARED_BY',i,\n");
        appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitlePreparedby1);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitlePreparedby1);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		+ '&COMPANY=' + getValue_('ITEM1_COMPANY',i)\n");
        appendDirtyJavaScript("		,600,445,'validateItem1PreparedBy');\n");
        appendDirtyJavaScript("  }		\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovItem2ApprovedBy(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("  if('");
        appendDirtyJavaScript(isFind);
        appendDirtyJavaScript("' == 'True')\n");
        appendDirtyJavaScript("  { \n");
        appendDirtyJavaScript("       openLOVWindow('ITEM2_APPROVED_BY',i,\n");
        appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitlePreparedby2);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitlePreparedby2);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		+ '&COMPANY= null'\n");
        appendDirtyJavaScript("		,600,445,'validateItem2ApprovedBy');\n");
        appendDirtyJavaScript("  }\n");
        appendDirtyJavaScript("  else\n");
        appendDirtyJavaScript("  {\n");
        appendDirtyJavaScript("        openLOVWindow('ITEM2_APPROVED_BY',i,\n");
        appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitlePreparedby2);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitlePreparedby2);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		+ '&COMPANY=' + getValue_('COMPANY',i)\n");
        appendDirtyJavaScript("		,600,445,'validateItem2ApprovedBy');\n");
        appendDirtyJavaScript("  }		\n");
        appendDirtyJavaScript("}\n");  */

        appendDirtyJavaScript("function lovItem1PositionId(i,params)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("if(params) param = params;\n");
        appendDirtyJavaScript("else param = '';\n");
        appendDirtyJavaScript("var enable_multichoice =(true && ITEM1_IN_FIND_MODE);\n");
        appendDirtyJavaScript("var key_value = (getValue_('ITEM1_POSITION_ID',i).indexOf('%') !=-1)? getValue_('ITEM1_POSITION_ID',i):'';\n");
        appendDirtyJavaScript("openLOVWindow('ITEM1_POSITION_ID',i,\n");
        appendDirtyJavaScript("     '" + root_path +"common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EQUIPMENT_OBJECT_POSITION&__FIELD=Position+Id&__INIT=1&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM1_POSITION_ID',i))\n");
        appendDirtyJavaScript("+ '&ITEM1_POSITION_ID=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript("+ '&MCH_TYPE=' + URLClientEncode(getValue_('ITEM1_MCH_TYPE',i))\n");
        appendDirtyJavaScript(",600,445,'validateItem1PositionId');\n");
        appendDirtyJavaScript("}\n");


        if ("TRUE".equals(hasWo))
        {
            appendDirtyJavaScript("      if (confirm(\"");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWDELIMITATIONORDERTABWARNING: There are connected active work orders. Do you want to continue?"));
            appendDirtyJavaScript("\")){\n");
            appendDirtyJavaScript("   document.form.REFRESHPARENT1.value = \"TRUE\";\n");
            appendDirtyJavaScript("   submit();\n");  
            appendDirtyJavaScript("     } \n");
        }


        // 031216  ARWILK  Begin  (Links with RMB's)
        if (bOpenNewWindow)
        {
            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(urlString);
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
        }
        // 031216  ARWILK  End  (Links with RMB's)
    }
}
