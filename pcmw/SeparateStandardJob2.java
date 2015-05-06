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
*  File        : SeparateStandardJob2.java 
*  Created     : SHFELK  010226  Created.
*  Modified    :    
*  SHFELK  010226  Changed Validations for Department.    
*  JEWILK  010403  Changed file extensions from .asp to .page
*  INROLK  010502  Changed department and Craft ID validation.
*  CHCRLK  010613  Modified overwritten validations. 
*  CHCRLK  010620  Made changes to Planning section
*  BUNILK  010620  Modified validations of material block, checsec() and GetpriceAmount()
*                  methods.    
*  BUNILK  010717  Modified PART_NO field validation. 
*  BUNILK  010806  Made some changes in return AutoString of getContents() function so that to call       
*                  checkPartVal() javascript function from onBlur event of PART_NO field. Done some changes in 
*                  validatePartNo() method also.
*  INROLK  010807  Changed the function of Cost to WORK_ORDER_PLANNING_UTIL_API.Get_Cost. call id 77857.
*  SHCHLK  010809  Change the validations of Crafts,Materials & Planning tabs.(Called Id - 77872)
*  ARWILK  010904  Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction(). 
*                  (Caused when cancelNew is performed after saveNew)
*  INROLK  010910  Changed Adjust to update check boxes Crafts and Meritrials when Crafts and Meterials are added. call id 78144.
*  INROLK  011015  Added Security Check to RMBs. 
*  SHAFLK  020531  Bug Id 30464, changed newRow().
*  BUNILK  020909  Bug ID 32182 Added a new method isModuleInst() to check availability of 
*                  ORDER module inside preDefine method and checked availability for DOCMAN methods also.    
*  BUNILK  021212  Merged with 2002-3 SP3 
*  JEJALK  021217  Synchronizing with vimw - Added "Craft Line No"  to the material tab.
*  JEJALK  030331  Renamed Crafts tab to Operations and Row No to Operation No, and Craft Line No to Operation No.
*  CHCRLK  030403  Added new tab Tools and Facilities.
*  CHCRLK  030430  Added CONDITION_CODE & CONDITION_CODE_DESC to Materials tab.
*  CHAMLK  030722  Added OWNERSHIP and OWNER to Materials Tab.
*  ARWILK  031003  (Bug#105018) Corrected some translation problems.
*  JEWILK  031014  Modified javascript method validateWorkOrderCostType() to restrict cost type Tools/Facilities. Call 107127.
*  PAPELK  031015  Call Id 107309. Changed trnaslation constants for PriceAmount and ListPrice in Crafts tab.
*  THWILK  031015  Call Id 107128. Added WORK_ORDER_INVOICE_TYPE filed for Tools& facilities tab and set the default value as "As Reported".
*  CHAMLK  031018  Passed Company and Created by when calling CopyStandardJobDlg.page
*  CHAMLK  031024  Modified function validate() to call method Purchase_Order_Line_Util_API.Get_Base_Price_Supp_Part only when a supplier exists for the Purchase Part.
*  SAPRLK  031217  Web Alignment, Converting blocks to tabs, removing 'clone' & 'doReset' methods, the link 'LOV from purchase part' in Material tab still exists.
*  VAGULK  030211  Web Alignment , Changed the field order as given in Centura.
*  SAPRLK  040225  Web Alignment - added multirow option to the Material Tab, simplify code for RMBs,remove unnecessary method calls for hidden fields,
*                  removed unnecessary global variables.  
* -------------------------------- Edge - SP1 Merge -------------------------
*  SHAFLK  040120  Bug Id 41815,Removed Java dependencies.
*  THWILK  040324  Merge with SP1.
*  ARWILK  040709  Added ORG_CONTRACT to the ITEMBLK0 AND ITEMBLK3. (IID AMEC500C: Resource Planning)  
*  ARWILK  040723  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  NIJALK  040727  Added new parameter CONTRACT to method calls to Role_Sales_Part_API.Get_Def_Catalog_No.  
*  INROLK  040701  Bug Id 43583 Added Sales part description field and added to validation of sales part.
*  SHAFLK  040716  Bug Id 43583, Removed setSimple of added Sales part description. 
*  ThWilk  040806  Merged Bug Id 43583.
*  ARWILK  040806  Added Custom validations to field CONTRACT.
*  SHAFLK  040713  Bug Id 44699,Added method GetSalesPriceUnit() and modified validations of ITEM1_CATALOG_NO and CONDITION_CODE and validatePartNo(). 
*  SHAFLK  040713  Bug Id 44699,Removed method GetSalesPriceUnit() and modified validations of ITEM1_CATALOG_NO and CONDITION_CODE to handle Price Amount. 
*  ThWilk  040810  Merged Bug 44699.
*  ARWILK  040819  Modified method updatePMact.
*  NIJALK  040830  Call 117469, Modified  setCheckBoxValues().
*  NIJALK  041007  Removed calls to method Role_Sales_Part_API.Get_Def_Contract().
*  NIJALK  041018  Added Custom function createNewRevision(). 
*  NIJALK  041021  Modified Adjust(), PreDefine(). 
*  ARWILK  041022  Did some major changes to the GUI and functionality. (Spec AMEC111 - Standard Jobs as PM Templates)
*  NIJALK  041103  Modified predefine(),validate().Added new methods showObsoletePM(), updatePM(), generatePMRevision().
*  NIJALK  041108  Added field STANDARD_JOB_TYPE_DB to headblk. modified generatePmRevision().
*  NIJALK  041110  Added new field HAS_OBS_WO to headblk & added new method showObsoleteWORev().
*  NAMELK  041110  Duplicated Translation Tags Corrected.
*  ARWILK  041112  Replaced getContents with printContents.
*  NIJALK  041124  Modified preDefine(), printContents(), getBudgetValues().
*  NIJALK  041222  Modified predefine(),getBudgetValues().
*  NEKOLK  041210  Bug Id 48478, Modified preDefine() to modify MaxLength of REMARK fld in Craft block.
*  HIWELK  041229  Bug 48478, Changed 'Remark' field to a multiline field.
*  Chanlk  050103  Merged Bug 48478.
*  NIJALK  050112  Modified preDefine(), printContents(),validate().
*  NIJALK  050113  Field "DISCOUNT" made hidden in all tabs. Renamed field label "Sales Price" to "List price".
*                  Added field "Sales Price/Unit" and set its priority over the priority of "List price".
*  NIJALK  050117  Modified Site fields on Material Requisions Tab.
*  NIJALK  050120  Modified validations to ROLE_CODE and ORG_CODE.
*  NIJALK  050126  Modified validation to PART_NO.
*  NIJALK  050203  Modified validate(), preDefine().
*  NIJALK  050208  Modified run(),createNewRevision(),okFind().
*  NIJALK  050210  Removed function updatePMact().
*  DIAMLK  050215  Modified the methods validate() and preDefine().
*  DIAMLK  050303  Bug ID:122381 - Modified the method preDefine() in order to make Sales Part Site 
*                  read only in Planning Tab.
*  NIJALK  050401  Bug 122927: modified adjust().
*  NIJALK  050419  Bug 122551: Modified printContents(), validate(), predefine().
*  NIJALK  050420  Bug 123549: Modified newRowITEM2().
*  NIJALK  050520  Modified availibilityDet(),preDefine().
*  THWILK  050526  Added the functionality required under Work Order Team & Qualifications(AMEC114).
*  THWILK  050608  Accomadated further modifications relating to Work Order Team & Qualifications(AMEC114).
*  THWILK  050610  Modified javascript methods.
*  THWILK  050610  Modified checkObjAvaileble(),run()& javascript methods lovTeamId and lovOrgContract.
*  THWILK  050615  Modified lovItem0OrgCode & lovTeamId javascript methods.
*  SHAFLK  050425  Bug 50830, Used translateJavaScript() method to get the correctly translated strings in JavaScript functions. 
*  NIJALK  050617  Merged bug 50830. 
*  THWILK  050622  Modified run(),additionalQualifications(). 
*  THWILK  050630  Added the RMB "Predecessors" under Work Order Dependencies(AMEC114). 
*  THWILK  050707  Modified methods run(),additionalQualifications() and predecessors().
*  DiAmlk  050711  Bug ID:125442 - Modified the method validate.
*  NIJALK  050729  Bug 126009, field "WORK_DESCRIPTION" unset mandatory.
*  NIJALK  050801  Bug 126024, Modified activatePm(); Changed Info Msg when activating std job. 
*  NEKOLK  050804  AMUT 115: added GENERATE fld to itemblk5 and modified lov for delimitation_id.
*  NEKOLK  050810  Made changes in newrowitem5()
*  NIJALK  050826  Bug 126558: Modified preDefine(), adjust() to Set Automatically generated external planning lines read only.
*  NEKOLK  050829  B 126657 : Modified item5 define().
*  AMNILK  051006  Call Id:127600. Modified method preDefine and validate.Add custom validations to ORG_CODE and ORG_CONTRACT.
*  THWILK  051103  Call Id:128445, Modified propereties of Site in Predefine().
*  NIJALK  051111  Bug 128833: Modified preDefine().
*  NIJALK  051114  BUg 128810: Added Expenses & Fixed Price fields to Budget Tab.
*  NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile).
*  NIJALK  050631  Bug 132231: Added methods saveReturn(),saveNew() with validations to "Start Value" & "Start Unit".
*                  Shifted the code for refreshing the headset to saveReturn() from run().
*  NIJALK  060306  Call 136390: Disable RMB 'Document' from budget tab and enable it on headblk.
*  SULILK  060310  Call 136679: Modified preDefine(),saveReturnITEM2() and added removeITEM0(),removeITEM1(),removeITEM2(),removeITEM3(),
*                               okFindALLITEM0(),okFindALLITEM1(),saveReturnITEM0(),saveReturnITEM1(),saveReturnITEM3().
*  SULILK  030321  Call 135197: Modified preDefine(),validate() and added a java script for validation of condition code
*  NIJALK  060323  Modified createNewRevision().
*  SULILK  060324  Modified validate().
*  SULILK  060324  Modified removeITEM0(),removeITEM1(),removeITEM2(),removeITEM3().
* ----------------------------------------------------------------------------
*  CHODLK  060515  Bug 56718, Modified method preDefine(), added duplicateITEM0.
*  SHAFLK  060525  Bug 57598, Modified validation for PART_NO.
*  AMNILK  060629  Merged with SP1 APP7.
*  AMNILK  060731  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMDILK  060807  Merged with Bug Id 58214
*  AMDILK  060811  Bug 58216, Eliminated SQL errors in web applications. Modified methods okFindITEM0(), okFindALLITEM0(), 
*                             okFindITEM1(), okFindALLITEM1(), okFindITEM2(), okFindALLITEM2(),okFindITEM3(), okFindITEM4(),
*                             okFindITEM5(), okFindITEM6(), okFindITEM7(), countFindITEM0(), countFindITEM1(), countFindITEM2(),
*                             countFindITEM3(), countFindITEM4(), countFindITEM5(),countFindITEM6(), activatePm()
*  AMDILK  060904  Merged with the Bug Id 58216
*  DiAmlk  060919  Modified the methods adjust and activatePm.
*  SHAFLK  061222  Bug 61959  Modified preDefine(),printContents() and added saveReturnITEM6(). 
*  AMNILK  070202  Merged LCS Bug Id: 61959.     
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  AMDILK  070330  Merged bug id 64068
*  AMNILK  070725  Eliminated XSS Security Vulnerability.
*  AMDILK  070801  Removed the scroll buttons of the parent when the child tabs are in new or edit modes
*  SHAFLK  081105  Bug 77824, Modified preDefine().
*  CHANLK  080324  Bug 78529, Modified adjust().
*  CHANLK  090330  Bug 78556, Modified saveReturnITEM6().
*  SHAFLK  080505  Bug 82435, Modified preDefine().
*  SHAFLK  080513  Bug 78586, Modified preDefine().
*  SHAFLK  090728  Bug 84886, Modified printContents().
*  SHAFLK  100503  Bug 89834, Modified preDefine().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class SeparateStandardJob2 extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.SeparateStandardJob2");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPContext ctx;
    private ASPHTMLFormatter fmt;
    private ASPBlock tempblk;

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

    private ASPBlock itemblk5;
    private ASPRowSet itemset5;
    private ASPCommandBar itembar5;
    private ASPTable itemtbl5;
    private ASPBlockLayout itemlay5;

    private ASPBlock itemblk6;
    private ASPRowSet itemset6;
    private ASPCommandBar itembar6;
    private ASPTable itemtbl6;
    private ASPBlockLayout itemlay6;

    private ASPBlock itemblk7;
    private ASPRowSet itemset7;
    private ASPCommandBar itembar7;
    private ASPTable itemtbl7;
    private ASPBlockLayout itemlay7;

    private ASPBlock eventblk;
    private ASPRowSet eventset;
    private ASPBlockLayout eventlay;

    private ASPField f;
    private ASPBlock tempblk1;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private double plQuant;
    private ASPTransactionBuffer trans;
    private ASPTransactionBuffer trans1;
    private String item1RowStatus;
    private String flag;
    private ASPTransactionBuffer secBuff;
    private String val;
    private ASPCommand cmd;
    private double numCost;
    private double nCost;
    private ASPQuery q;
    private ASPBuffer data;
    private int headrowno;
    private int currrow;
    private String calling_url;
    private ASPBuffer buffer;
    private String current_url;
    private ASPBuffer buff;
    private ASPBuffer r;
    private String fldTitleCreatedBy;
    private String fldTitleChangedBy;
    private String lovTitleCreatedBy;
    private String lovTitleChangedBy;
    private String isSecure[];  
    private String dateformat; 
    private String txt;
    private ASPBuffer row; 
    private String fldTitlePartNoPurch;
    private String lovTitlePartNoPurch;
    //Variables for Security check for RMBs.
    private boolean again;
    private boolean qtyOnHand_;
    private boolean availibilityDet_;
    private boolean inventoryPart_;
    private boolean suppPerPart_;
    private boolean secCompetence;
    //Web Alignment - Adding tabs
    private ASPTabContainer tabs;   
    //Web Alignment - simplify code for RMBs
    private boolean replaced = false;
    private boolean bOpenNewWindow;
    private String urlString;
    private String newWinHandle;
    private int i;
    private int newWinHeight = 600;
    private int newWinWidth = 900;
    private double salesPrc;
    private double AmountCost;
    private double qtyInv;
    private double lstprc;
    private double disc;
    private double mylstprc;
    private String planMatRev;
    private String planExtRev;
    private String perBudg;
    private String matBudg;
    private String toolBudg;
    private String extBudg;
    private String expBudg;
    private String fixBudg;
    private String totBudg;
    private String planPer;
    private String planMat;
    private String planTool;
    private String planExt;
    private String planExp;
    private String planFix;
    private String planTot;
    private String planPerRev;
    private String planToolRev;
    private String planExpRev;
    private String planFixRev;
    private String planTotRev;
    private String qrystr;
    private String new_std_job_id;
    private String new_std_job_revision;
    private String new_contract;
    private String headNo;
    private String dateMask;
    private String laymode;

    //===============================================================
    // Construction 
    //===============================================================
    public SeparateStandardJob2(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        isSecure =  new String[2] ;
        plQuant =  0;
        i=0;
        qrystr =  "";
        new_std_job_id = "";
        new_std_job_revision = "";
        new_contract = "";
        ASPManager mgr = getASPManager();

        ctx = mgr.getASPContext();
        fmt = mgr.newASPHTMLFormatter();
        trans = mgr.newASPTransactionBuffer();
        trans1 = mgr.newASPTransactionBuffer();
        flag = ctx.readValue("FLAG","FALSE");
        dateformat = ctx.readValue("CTX1",dateformat);
        dateformat = mgr.getFormatMask("Date",true);

        again = ctx.readFlag("AGAIN",again);
        qtyOnHand_ = ctx.readFlag("QTYONHAND",false);
        availibilityDet_ = ctx.readFlag("AVAILIBILITYDET",false);
        inventoryPart_ = ctx.readFlag("INVENTORYPART",false);
        suppPerPart_ = ctx.readFlag("SUPPPERPART",false);
        secCompetence = ctx.readFlag("SECCOMP",false);
        replaced = ctx.readFlag("REPLACED",false);
        qrystr = ctx.readValue("QRYSTR",qrystr);
        laymode = ctx.readValue("LAYMODE","");

        if (mgr.commandBarActivated())
        {
            eval(mgr.commandBarFunction());
            /* Did this because the CHANGED_BY field is not updated properly after savereturn) - ARWILK*/
            //if ("ITEM0.SaveReturn".equals(mgr.readValue("__COMMAND")))
            if ("saveReturnITEM0".equals(mgr.readValue("__COMMAND")))
                itemset0.refreshRow();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if ("TRUE".equals(mgr.readValue("REFRESHPARENT")))
            predecessors(); 
        else if (!mgr.isEmpty(mgr.getQueryStringValue("PREDECESSORS")))
        {
           headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
           okFind();   

           if (headset.countRows() != 1)
           {
               headNo = ctx.getGlobal("HEADGLOBAL");
               int headSetNo = Integer.parseInt(headNo);
               headset.goTo(headSetNo);   
               okFindITEM0();
               okFindITEM1();
               okFindITEM2();
               okFindITEM3();
               okFindITEM4();
               okFindITEM5();
               okFindITEM6();
               okFindITEM7();

           }
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("STDQUALIFICATION")))
        {
           headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
           okFind();   

           if (headset.countRows() != 1)
           {
               headNo = ctx.getGlobal("HEADGLOBAL");
               int headSetNo = Integer.parseInt(headNo);
               headset.goTo(headSetNo);   
               okFindITEM0();
               okFindITEM1();
               okFindITEM2();
               okFindITEM3();
               okFindITEM4();
               okFindITEM5();
               okFindITEM6();
               okFindITEM7();
           }
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("STD_JOB_ID")))
            okFind();
        else if (mgr.dataTransfered())
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();
        }

        if (!again)
        {
            checkObjAvaileble();
            again = true;
        }

        if (!mgr.isEmpty(mgr.getQueryStringValue("REVCRT")))
        {
            int i = 0;
            new_std_job_id = mgr.readValue("NEW_STD_JOB_ID");
            new_std_job_revision = mgr.readValue("NEW_REV");
            new_contract = mgr.readValue("CONTRACT");

            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();

            if(headset.countRows()>0)
            {
                headset.first();
                while (i == 0)
                {
                    if (headset.getRow().getValue("STD_JOB_ID").equals(new_std_job_id) && headset.getRow().getValue("STD_JOB_REVISION").equals(new_std_job_revision) && headset.getRow().getValue("CONTRACT").equals(new_contract))
                        i = 1;
                    else
                        headset.next();
                }
            }
            
        }

        adjust();

        tabs.saveActiveTab();

        ctx.writeValue("CTX1",dateformat);

        ctx.writeFlag("AGAIN",again);
        ctx.writeFlag("QTYONHAND",qtyOnHand_);
        ctx.writeFlag("AVAILIBILITYDET",availibilityDet_); 
        ctx.writeFlag("INVENTORYPART",inventoryPart_);
        ctx.writeFlag("SUPPPERPART",suppPerPart_);
        ctx.writeFlag("SECCOMP",secCompetence);
        ctx.writeFlag("REPLACED",replaced);
        ctx.writeValue("QRYSTR",qrystr);
        ctx.writeValue("LAYMODE",laymode);
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

        isSecure[ref] = "false" ;
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

    public void validate()
    {
        int ref;
        isSecure = new String[8];                
        String tftypedesc;
        double tfcost;
        String tfcostcurr;
        String tfnote;
        double qty;
        double tfpamoun = 0;
        String tfsalesp = null;
        String tfspsite = null;
        String tfspdesc = null;
        double tfsprice = 0;
        String tfspcurr = null;
        String strqty;
        String stfsprice;
        String stfpamoun;
        String strtfcost;
        String ccDesc;
        String teamDesc;

        ASPManager mgr = getASPManager();

        val = mgr.readValue("VALIDATE");

        if ("CONTRACT".equals(val)) {

            String sCalendar;  
            String sCalDesc;
            String sComp;
        
            cmd = trans.addCustomFunction("CALENDER", "Organization_API.Get_Calendar_Id", "CALENDAR_ID" );
            cmd.addParameter("CONTRACT");
            cmd.addParameter("HEAD_ORG_CODE");

            cmd = trans.addCustomFunction("CALDESC", "Work_Time_Calendar_API.Get_Description", "CALENDAR_DESCRIPTION" );
            cmd.addReference("CALENDAR_ID","CALENDER/DATA");

            cmd = trans.addCustomFunction("GETCOMP", "Site_API.Get_Company", "COMPANY" );
            cmd.addParameter("CONTRACT");

            trans=mgr.validate(trans);
            sComp = trans.getValue("GETCOMP/DATA/COMPANY");
            sCalendar =trans.getValue("CALENDER/DATA/CALENDAR_ID");
            sCalDesc = trans.getValue("CALDESC/DATA/CALENDAR_DESCRIPTION");
                        
            txt = (mgr.isEmpty (sComp) ? "" : sComp) +"^" + 
                  (mgr.isEmpty (sCalendar) ? "" : sCalendar) + "^" +
                  (mgr.isEmpty (sCalDesc) ? "" : sCalDesc) +"^" ;  
            mgr.responseWrite(txt);
        }   

        else if ("CREATED_BY".equals(val))
        {
            cmd = trans.addCustomFunction("GTMXEMPID", "Company_Emp_API.Get_Max_Employee_Id", "CREATED_BY_ID");
            cmd.addParameter("COMPANY"); 
            cmd.addParameter("CREATED_BY"); 

            trans = mgr.validate(trans);

            String crtdid = trans.getValue("GTMXEMPID/DATA/CREATED_BY_ID");

            txt = (mgr.isEmpty(crtdid) ? "" : (crtdid)) + "^" ;

            mgr.responseWrite(txt);
        }
        else if ("CHANGED_BY".equals(val))
        {
            cmd = trans.addCustomFunction("GTMXEMPIDCH", "Company_Emp_API.Get_Max_Employee_Id", "CHANGED_BY_ID");
            cmd.addParameter("COMPANY"); 
            cmd.addParameter("CHANGED_BY"); 

            trans = mgr.validate(trans);

            String chngid = trans.getValue("GTMXEMPIDCH/DATA/CHANGED_BY_ID");

            txt = (mgr.isEmpty(chngid) ? "" : (chngid)) + "^";  

            mgr.responseWrite(txt);
        }
        else if ("CATALOG_NO".equals(val))
        {
            String ctlgdesc0 = "";
            double lstprc0 = 0;

            if (checksec("SALES_PART_API.Get_Catalog_Desc",1,isSecure))
            {
                cmd = trans.addCustomFunction("GTOCTLGDESC", "SALES_PART_API.Get_Catalog_Desc", "SLSPRTCTLGDESC");
                cmd.addParameter("ITEM0_CONTRACT"); 
                cmd.addParameter("CATALOG_NO");
            }
            if (checksec("SALES_PART_API.Get_List_Price",2,isSecure))
            {
                cmd = trans.addCustomFunction("GTOPRTPRC", "SALES_PART_API.Get_List_Price", "SLSPRTLSTPRICE");
                cmd.addParameter("ITEM0_CONTRACT"); 
                cmd.addParameter("CATALOG_NO");  
            }

            trans = mgr.validate(trans);

            ref = 0;


            if (isSecure[ref += 1] =="true")
                ctlgdesc0 = trans.getValue("GTOCTLGDESC/DATA/SLSPRTCTLGDESC");
            else
                ctlgdesc0 = "";

            if (isSecure[ref += 1] =="true")
                lstprc0 = trans.getNumberValue("GTOPRTPRC/DATA/SLSPRTLSTPRICE");
            else
                lstprc0 = 0;
            String lstprc0str = mgr.getASPField("SLSPRTLSTPRICE").formatNumber(lstprc0);

            trans.clear();
            numCost = NOT_A_NUMBER;

            if (!mgr.isEmpty(mgr.readValue("CATALOG_NO","")))
            {
                if (checksec("WORK_ORDER_PLANNING_UTIL_API.Get_Cost",1,isSecure))
                {
                    cmd = trans.addCustomFunction("GTOCSTA","WORK_ORDER_PLANNING_UTIL_API.Get_Cost","SLSPRTCOST");
                    cmd.addParameter("ORG_CODE",mgr.readValue("ORG_CODE",""));
                    cmd.addParameter("ROLE_CODE",mgr.readValue("ROLE_CODE",""));
                    cmd.addParameter("CATALOG_CONTRACT",mgr.readValue("ITEM0_CONTRACT","")); 
                    cmd.addParameter("CATALOG_NO",mgr.readValue("CATALOG_NO","")); 
                    cmd.addParameter("ORG_CONTRACT",mgr.readValue("ORG_CONTRACT",""));

                    trans = mgr.validate(trans);

                    numCost = trans.getNumberValue("GTOCSTA/DATA/SLSPRTCOST");
                }
                else
                    numCost=  NOT_A_NUMBER;
            }

            trans.clear();

            cmd = trans.addCustomFunction("COSTB","ROLE_API.Get_Role_Cost","COST1");
            cmd.addParameter("ROLE_CODE");

            cmd = trans.addCustomFunction("COSTC","ORGANIZATION_API.Get_Org_Cost","COST2");
            cmd.addParameter("ORG_CONTRACT");
            cmd.addParameter("ORG_CODE");

            trans = mgr.validate(trans);

            if (isNaN(numCost))
            {
                numCost = trans.getNumberValue("COSTB/DATA/COST1");
                if (isNaN(numCost))
                    numCost = trans.getNumberValue("COSTC/DATA/COST2");
            }
            String numCoststr = mgr.getASPField("SLSPRTCOST").formatNumber(numCost);
            trans.clear();

            GetPriceAmount1();

            String AmountCost1 = txt;

            txt = (mgr.isEmpty(ctlgdesc0) ? "" : (ctlgdesc0)) + "^" + (mgr.isEmpty(numCoststr) ? "" : numCoststr)+ "^" + (mgr.isEmpty(lstprc0str) ? "" : lstprc0str)+ "^" + (mgr.isEmpty(AmountCost1) ? "" : AmountCost1)+ "^";

            mgr.responseWrite(txt);
        }
        else if ("PART_NO".equals(val))
        {
            System.out.println("Within the validation   **************");

            if (checksec("Purchase_Part_API.Get_Description",1,isSecure))
            {
                cmd = trans.addCustomFunction("GTDESC", "Purchase_Part_API.Get_Description", "PARTDESCRIPTION");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT")); 
                cmd.addParameter("PART_NO"); 
            }
            if (checksec("INVENTORY_PART_API.Get_Dim_Quality",2,isSecure))
            {
                cmd = trans.addCustomFunction("GTDMQTY", "INVENTORY_PART_API.Get_Dim_Quality", "DIMQUALITY");
                cmd.addParameter("SPARE_CONTRACT"); 
                cmd.addParameter("PART_NO"); 
            }
            if (checksec("INVENTORY_PART_API.Get_Type_Designation",3,isSecure))
            {
                cmd = trans.addCustomFunction("GTTYPDESC", "INVENTORY_PART_API.Get_Type_Designation", "TYPEDESIGNATION");
                cmd.addParameter("SPARE_CONTRACT"); 
                cmd.addParameter("PART_NO"); 
            }
            if (checksec("INVENTORY_PART_API.Get_Unit_Meas",4,isSecure))
            {
                cmd = trans.addCustomFunction("GTUNITMEAS", "INVENTORY_PART_API.Get_Unit_Meas", "PARTUNITCODE");
                cmd.addParameter("SPARE_CONTRACT"); 
                cmd.addParameter("PART_NO"); 
            }
            if (checksec("Inventory_Part_API.Get_Description",5,isSecure))
            {
                cmd = trans.addCustomFunction("INVEDESC","Inventory_Part_API.Get_Description","PARTDESCRIPTION");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT")); 
                cmd.addParameter("PART_NO"); 
            }
            if (checksec("Purchase_Part_API.Get_Default_Buy_Unit_Meas",6,isSecure))
            {
                cmd = trans.addCustomFunction("DEFUNITMEAS","Purchase_Part_API.Get_Default_Buy_Unit_Meas","PARTUNITCODE");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT")); 
                cmd.addParameter("PART_NO"); 
            }
            if (checksec("PART_CATALOG_API.Get_Condition_Code_Usage_Db",7,isSecure))
            {
                cmd = trans.addCustomFunction("GETCONDUSAGE","PART_CATALOG_API.Get_Condition_Code_Usage_Db","COND_CODE_USAGE");
                cmd.addParameter("PART_NO"); 
            }


            trans = mgr.validate(trans);

            ref = 0;
            String prtdesc = "";
            String prtdesc1 = "";
            String dimqly = "";
            String typdesig = "";
            String prtunitcd = "";
            String prtunitcd1 = "";
            String sCatalogNo = "";
            String sCondCodeUage = "";
            String sCondCode = "";
            String sCondDesc = "";
            String activeInd = "";

            if (isSecure[ref += 1] =="true")
                prtdesc = trans.getBuffer("GTDESC/DATA").getFieldValue("PARTDESCRIPTION");
            else
                prtdesc = "";

            if (isSecure[ref += 1] =="true")
                dimqly = trans.getValue("GTDMQTY/DATA/DIMQUALITY");
            else
                dimqly = "";

            if (isSecure[ref += 1] =="true")
                typdesig = trans.getValue("GTTYPDESC/DATA/TYPEDESIGNATION");
            else
                typdesig    = "";

            if (isSecure[ref += 1] =="true")
                prtunitcd = trans.getBuffer("GTUNITMEAS/DATA").getValue("PARTUNITCODE");
            else
                prtunitcd = "";

            if (isSecure[ref += 1] =="true")
                prtdesc1 = trans.getValue("INVEDESC/DATA/PARTDESCRIPTION");
            else
                prtdesc1 = "";

            if (isSecure[ref += 1] =="true")
                prtunitcd1 = trans.getValue("DEFUNITMEAS/DATA/PARTUNITCODE");
            else
                prtunitcd1 = "";

            if (isSecure[ref += 1] =="true")
                sCondCodeUage = trans.getValue("GETCONDUSAGE/DATA/COND_CODE_USAGE");
            else
                sCondCodeUage = "";

            if (mgr.isEmpty(prtdesc))
                prtdesc = prtdesc1;

            if (mgr.isEmpty(prtunitcd))
                prtunitcd = prtunitcd1;

            trans.clear();
            nCost = NOT_A_NUMBER;


            double isInventory = 0;
            if (checksec("INVENTORY_PART_API.Part_Exist",1,isSecure))
            {
                cmd = trans.addCustomFunction("ITEM1ISINVEPART","INVENTORY_PART_API.Part_Exist","INVENTORYPART");
                cmd.addParameter("CONTRACT",mgr.readValue("ITEM1_CONTRACT",""));
                cmd.addParameter("PART_NO");

                trans = mgr.validate(trans);
                isInventory = trans.getNumberValue("ITEM1ISINVEPART/DATA/INVENTORYPART");
            }

            //if (isInventory == 1 && checksec("Inventory_Part_API.Get_Inventory_Value_By_Method",1,isSecure))
            if (isInventory == 1 && checksec("Active_Separate_API.Get_Inventory_Value",1,isSecure))
            {
                trans.clear();

                /*cmd = trans.addCustomFunction("ITEM1COST","Inventory_Part_API.Get_Inventory_Value_By_Method","ITEM1PRTCOST"); 
                cmd.addParameter("CONTRACT",mgr.readValue("ITEM1_CONTRACT",""));
                cmd.addParameter("PART_NO"); */

                cmd = trans.addCustomCommand("ITEM1COST","Active_Separate_API.Get_Inventory_Value");
                cmd.addParameter("ITEM1PRTCOST");
                cmd.addParameter("CONTRACT",mgr.readValue("ITEM1_CONTRACT"));
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
                cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
                cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
                cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));

                trans = mgr.validate(trans);
                nCost = trans.getNumberValue("ITEM1COST/DATA/ITEM1PRTCOST");
            }

            if (isInventory == 0 && checksec("Purchase_Order_Line_Util_API.Get_Base_Price_Supp_Part",1,isSecure))
            {
                trans.clear();

                q = trans.addQuery("SYSD","SELECT SYSDATE FROM DUAL");
                q.includeMeta("ALL");

                cmd = trans.addCustomFunction("ITEM1SUPP","Purchase_Order_Line_Util_API.Get_Primary_Supplier","SUPPLIER");
                cmd.addParameter("CONTRACT",mgr.readValue("ITEM1_CONTRACT",""));
                cmd.addParameter("PART_NO");

                trans = mgr.validate(trans);

                String nVendor = trans.getValue("ITEM1SUPP/DATA/SUPPLIER");
                String sysdate = trans.getValue("SYSD/DATA/SYSDATE");
                trans.clear();

                if (!mgr.isEmpty(nVendor))
                {
                    cmd = trans.addCustomCommand("ITEM1COST","Purchase_Order_Line_Util_API.Get_Base_Price_Supp_Part"); 
                    cmd.addParameter("ITEM1PRTCOST");
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("CONTRACT",mgr.readValue("ITEM1_CONTRACT",""));
                    cmd.addParameter("VENDOR",nVendor);
                    cmd.addParameter("PLANNED_QTY");
                    cmd.addParameter("PRICE_DATE",sysdate);


                    trans = mgr.validate(trans);
                    nCost = trans.getNumberValue("ITEM1COST/DATA/ITEM1PRTCOST");
                }

            }

            String nCostStr = mgr.getASPField("ITEM1PRTCOST").formatNumber(nCost);

            trans.clear();

            if (checksec("Sales_Part_API.Get_Catalog_No_For_Part_No",2,isSecure))
            {
                cmd = trans.addCustomFunction("CATFORPART","Sales_Part_API.Get_Catalog_No_For_Part_No","CATALOG_NO");
                cmd.addParameter("CONTRACT",mgr.readValue("ITEM1_CONTRACT",""));
                cmd.addParameter("PART_NO");

                cmd = trans.addCustomFunction("GETACT","Sales_Part_API.Get_Activeind","ACTIVEIND");
                cmd.addParameter("CONTRACT",mgr.readValue("ITEM1_CONTRACT",""));
                cmd.addReference("CATALOG_NO","CATFORPART/DATA");
                cmd = trans.addCustomFunction("GETACTDB","Active_Sales_Part_API.Encode","ACTIVEIND_DB");
                cmd.addReference("ACTIVEIND","GETACT/DATA");
                trans = mgr.validate(trans);
                sCatalogNo = trans.getValue("CATFORPART/DATA/CATALOG_NO");
                activeInd = trans.getValue("GETACTDB/DATA/ACTIVEIND_DB");
            }
            else
                sCatalogNo = "";
            trans.clear();

            if (mgr.isEmpty(sCatalogNo))
                sCatalogNo = mgr.readValue("ITEM1_CATALOG_NO","");

            cmd = trans.addCustomFunction("CATEXT","Sales_Part_API.Check_Exist","CAT_EXIST");
            cmd.addParameter("ITEM1_STD_JOB_CONTRACT",mgr.readValue("ITEM1_STD_JOB_CONTRACT"));
            cmd.addParameter("CATALOG_NO",sCatalogNo);

            trans = mgr.validate(trans);

            double nExist = trans.getNumberValue("CATEXT/DATA/CAT_EXIST");

            if (nExist != 1)
                sCatalogNo = "";

            trans.clear();
            if (!mgr.isEmpty(sCatalogNo))
            {
                cmd = trans.addCustomFunction("DEFCATDESC","SALES_PART_API.Get_Catalog_Desc","ITEM1CTLGDESC");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT")); 
                cmd.addParameter("CATALOG_NO",sCatalogNo); 

                cmd = trans.addCustomFunction("LPRICE","SALES_PART_API.Get_List_Price","ITEM1PRTLSTPRC");
                cmd.addParameter("CONTRACT",mgr.readValue("ITEM1_CONTRACT")); 
                cmd.addParameter("CATALOG_NO",sCatalogNo);                                                                                                                                                                

            }

            trans = mgr.validate(trans);
            String sCatDesc = trans.getValue("DEFCATDESC/DATA/ITEM1CTLGDESC");
            double numLPrice = trans.getNumberValue("LPRICE/DATA/ITEM1PRTLSTPRC");

            String numLPriceStr = mgr.getASPField("ITEM1PRTLSTPRC").formatNumber(numLPrice);

            trans.clear();

            cmd = trans.addCustomFunction("SUUPCODE","INVENTORY_PART_API.Get_Supply_Code ","SUPPLY_CODE");
            cmd.addParameter("ITEM1_CONTRACT");
            cmd.addParameter("ITEM1_CATALOG_NO",sCatalogNo);

            cmd = trans.addCustomCommand("MATREQLINE","Material_Requis_Line_API.CHECK_PART_NO__");
            cmd.addParameter("PARTDESCRIPTION",prtdesc);
            cmd.addReference("SUPPLY_CODE","SUUPCODE/DATA");
            cmd.addParameter("PARTUNITCODE",prtunitcd);
            cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
            cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));

            if ("ALLOW_COND_CODE".equals(sCondCodeUage))
            {
                cmd = trans.addCustomFunction("GETDEFCOND","Condition_Code_API.Get_Default_Condition_Code","CONDITION_CODE");

                cmd = trans.addCustomFunction("GETCONDDESC","Condition_Code_API.Get_Description","CONDITION_CODE_DESC");
                cmd.addReference("CONDITION_CODE","GETDEFCOND/DATA");
            }

            trans = mgr.validate(trans);

            if ("ALLOW_COND_CODE".equals(sCondCodeUage))
            {
                sCondCode = trans.getValue("GETDEFCOND/DATA/CONDITION_CODE");
                sCondDesc = trans.getValue("GETCONDDESC/DATA/CONDITION_CODE_DESC");
            }

            double nPlannedHrs = 0;
            double valFlag = 0;
            double nPlannedMen = 0;

            String salesPartNoVal = sCatalogNo;    

            double nSalesPriceVal = mgr.readNumberValue("ITEM1_SALES_PRICE");
            if (isNaN(nSalesPriceVal))
                nSalesPriceVal = 0;

            double nSlsPrtListPriceVal = numLPrice;

            String invTypeVal = mgr.readValue("ITEM1_WORK_ORDER_INVOICE_TYPE");

            double nDiscountVal = mgr.readNumberValue("ITEM1_DISCOUNT");
            if (isNaN(nDiscountVal))
                nDiscountVal = 0;

            double nQtyToInvVal = mgr.readNumberValue("ITEM1_QTY_TO_INVOICE");
            if (isNaN(nQtyToInvVal))
                nQtyToInvVal = 0;

            double nPlanQtyVal = mgr.readNumberValue("PLANNED_QTY");
            if (isNaN(nPlanQtyVal))
                nPlanQtyVal = 0;

            double nValPriceAmt = validatePriceAmtCrafts(valFlag,salesPartNoVal,nSalesPriceVal,nSlsPrtListPriceVal,invTypeVal,nDiscountVal,nQtyToInvVal,nPlanQtyVal,nPlannedHrs,nPlannedMen);

            String strPriceAmt = mgr.getASPField("ITEM1_PRICE_AMOUNT").formatNumber(nValPriceAmt);

            txt = (mgr.isEmpty(prtdesc) ? "" : (prtdesc)) + "^" + 
                    (mgr.isEmpty(dimqly) ? "" : dimqly)+ "^" + 
                    (mgr.isEmpty(typdesig) ? "" : typdesig)+ "^" + 
                    (mgr.isEmpty(prtunitcd) ? "" : prtunitcd)+ "^" + 
                    (mgr.isEmpty(nCostStr) ? "" : nCostStr)+ "^" + 
                    (mgr.isEmpty(sCatalogNo) ? "" : sCatalogNo) + "^" + 
                    (mgr.isEmpty(sCatDesc) ? "" : sCatDesc) + "^" + 
                    (mgr.isEmpty(numLPriceStr) ? "" : numLPriceStr) + "^" + 
                    (mgr.isEmpty(strPriceAmt) ? "" : strPriceAmt) + "^"+
                    (mgr.isEmpty(sCondCode) ? "" : sCondCode) + "^"+ 
                    (mgr.isEmpty(sCondDesc) ? "" : sCondDesc) + "^"+
                    (mgr.isEmpty(activeInd) ? "" : activeInd) + "^";

            mgr.responseWrite(txt);
        }
        else if ("CONDITION_CODE".equals(val))
        {
            double salesPaCost;

            cmd = trans.addCustomFunction("CCDESC", "Condition_Code_API.Get_Description", "CONDITION_CODE_DESC");
            cmd.addParameter("CONDITION_CODE"); 

            trans = mgr.validate(trans);

            ccDesc = trans.getValue("CCDESC/DATA/CONDITION_CODE_DESC");
            trans.clear();
            if (checksec("STANDARD_JOB_SPARE_UTILITY_API.Get_Sales_Price",1,isSecure))
            {
                cmd = trans.addCustomFunction("GETSP", "STANDARD_JOB_SPARE_UTILITY_API.Get_Sales_Price", "SALES_PRICE");
                cmd.addParameter("ITEM1_CONTRACT"); 
                cmd.addParameter("ITEM1_CATALOG_NO"); 
                cmd.addParameter("CONDITION_CODE"); 
            }

            if (checksec("SALES_PART_API.Get_List_Price",2,isSecure))
            {
                cmd = trans.addCustomFunction("GTPRTPRC", "SALES_PART_API.Get_List_Price", "ITEM1PRTLSTPRC");
                cmd.addParameter("ITEM1_CONTRACT"); 
                cmd.addParameter("ITEM1_CATALOG_NO"); 
            }

            trans = mgr.validate(trans);

            ref = 0;

            if (isSecure[ref += 1] =="true")

                salesPrc = trans.getNumberValue("GETSP/DATA/SALES_PRICE");
            else
                salesPrc    = 0;


            AmountCost = 0;

            qtyInv = mgr.readNumberValue("ITEM1_QTY_TO_INVOICE");

            if (!isNaN(qtyInv))
            {
                if (!isNaN(salesPrc))
                {
                    lstprc = trans.getNumberValue("GTPRTPRC/DATA/ITEM1PRTLSTPRC");
                    if (isNaN(lstprc))
                        lstprc = 0;
                    AmountCost = qtyInv * salesPrc;

                }
                else
                {
                    if (isSecure[ref += 1] =="true")
                    {
                        lstprc = trans.getNumberValue("GTPRTPRC/DATA/ITEM1PRTLSTPRC");
                        if (isNaN(lstprc))
                            lstprc = 0;

                        mylstprc = lstprc;

                        AmountCost = qtyInv * lstprc;
                    }
                }   
            }
            else
            {
                if (!isNaN(salesPrc))
                {
                    lstprc = trans.getNumberValue("GTPRTPRC/DATA/ITEM1PRTLSTPRC");
                    if (isNaN(lstprc))
                        lstprc = 0;
                    plQuant = mgr.readNumberValue("PLANNED_QTY");
                    if (isNaN(plQuant))
                        plQuant = 0;

                    AmountCost = plQuant * salesPrc;
                }
                else
                {
                    if (isSecure[ref += 1] =="true")
                    {
                        lstprc = trans.getNumberValue("GTPRTPRC/DATA/ITEM1PRTLSTPRC");
                        if (isNaN(lstprc))
                            lstprc = 0;

                        mylstprc = lstprc;

                        plQuant = 0;
                        plQuant = mgr.readNumberValue("PLANNED_QTY");
                        if (isNaN(plQuant))
                            plQuant = 0;

                        AmountCost = plQuant * lstprc;
                    }
                }
            }  

            disc = 0;
            disc = mgr.readNumberValue("ITEM1_DISCOUNT");

            String salesPrcstr = mgr.getASPField("ITEM1_SALES_PRICE").formatNumber(salesPrc);
            if (!isNaN(disc))
            {
                AmountCost = AmountCost - (disc/100 * AmountCost);
            }

            String salesPaCostS = mgr.readValue("ITEM1PRTCOST","");
            trans.clear();

            cmd = trans.addCustomCommand("SALESPARTCOST","Active_Separate_API.Get_Inventory_Value");
            cmd.addParameter("ITEM1PRTCOST");
            cmd.addParameter("CONTRACT",mgr.readValue("ITEM1_CONTRACT"));
            cmd.addParameter("PART_NO");
            cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
            cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
            cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));
            
            trans = mgr.validate(trans);

            salesPaCost =  trans.getNumberValue("SALESPARTCOST/DATA/ITEM1PRTCOST");
            salesPaCostS = mgr.getASPField("ITEM1PRTCOST").formatNumber(salesPaCost);

            String AmountCostStr = mgr.getASPField("ITEM1_PRICE_AMOUNT").formatNumber(AmountCost);
            
            txt = (mgr.isEmpty(salesPrcstr) ? "" : (salesPrcstr)) + "^" + 
                  (mgr.isEmpty(ccDesc) ? "" : (ccDesc)) + "^" +
                  (mgr.isEmpty(salesPaCostS) ? "" : salesPaCostS)+ "^" +
                  (mgr.isEmpty(AmountCostStr) ? "" : AmountCostStr)+ "^";

            mgr.responseWrite(txt);
        }
        else if ("PART_OWNERSHIP".equals(val))
        {
            cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
            cmd.addParameter("PART_OWNERSHIP");
            trans = mgr.validate(trans);

            String sOwnershipDb = trans.getValue("VALIDOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");

            txt = (mgr.isEmpty(sOwnershipDb) ? "" : (sOwnershipDb)) + "^";  
            mgr.responseWrite(txt);
        }
        else if ("OWNER".equals(val))
        {
            cmd = trans.addCustomFunction("GETOWNERNAME", "CUSTOMER_INFO_API.Get_Name", "OWNER_NAME");
            cmd.addParameter("OWNER");
            trans = mgr.validate(trans);

            String sOwnerName = trans.getValue("GETOWNERNAME/DATA/OWNER_NAME");

            txt = (mgr.isEmpty(sOwnerName) ? "" : (sOwnerName)) + "^";  
            mgr.responseWrite(txt);
        }
        else if ("ITEM1_CATALOG_NO".equals(val))
        {
            if (checksec("SALES_PART_API.Get_Catalog_Desc",1,isSecure))
            {
                cmd = trans.addCustomFunction("GTCTLGDESC", "SALES_PART_API.Get_Catalog_Desc", "ITEM1CTLGDESC");
                cmd.addParameter("ITEM1_CONTRACT"); 
                cmd.addParameter("ITEM1_CATALOG_NO"); 
            }

            //if (checksec("Inventory_Part_API.Get_Inventory_Value_By_Method",2,isSecure))
            if (checksec("Active_Separate_API.Get_Inventory_Value",2,isSecure))
            {
                /*cmd = trans.addCustomFunction("GTCST", "Inventory_Part_API.Get_Inventory_Value_By_Method", "ITEM1PRTCOST");
                cmd.addParameter("CONTRACT",mgr.readValue("ITEM1_CONTRACT",""));
                cmd.addParameter("PART_NO"); */

                cmd = trans.addCustomCommand("GTCST","Active_Separate_API.Get_Inventory_Value");
                cmd.addParameter("ITEM1PRTCOST");
                cmd.addParameter("CONTRACT",mgr.readValue("ITEM1_CONTRACT"));
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
                cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
                cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
                cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));
            }

            if (checksec("STANDARD_JOB_SPARE_UTILITY_API.Get_Sales_Price",3,isSecure))
            {
                cmd = trans.addCustomFunction("GETSP", "STANDARD_JOB_SPARE_UTILITY_API.Get_Sales_Price", "SALES_PRICE");
                cmd.addParameter("ITEM1_CONTRACT"); 
                cmd.addParameter("ITEM1_CATALOG_NO"); 
                cmd.addParameter("CONDITION_CODE"); 
            }

            if (checksec("SALES_PART_API.Get_List_Price",4,isSecure))
            {
                cmd = trans.addCustomFunction("GTPRTPRC", "SALES_PART_API.Get_List_Price", "ITEM1PRTLSTPRC");
                cmd.addParameter("ITEM1_CONTRACT"); 
                cmd.addParameter("ITEM1_CATALOG_NO"); 
            }

            trans = mgr.validate(trans);

            ref = 0;
            String ctlgdesc = "";
            double prtcst = 0;


            if (isSecure[ref += 1] =="true")
                ctlgdesc = trans.getValue("GTCTLGDESC/DATA/ITEM1CTLGDESC");
            else
                ctlgdesc    = "";

            if (isSecure[ref += 1] =="true")
            {
                prtcst = trans.getNumberValue("GTCST/DATA/ITEM1PRTCOST");
                if (isNaN(prtcst))
                    prtcst = 0;
            }
            else
                prtcst = 0;

            
            String prtcstStr = mgr.getASPField("ITEM1PRTCOST").formatNumber(prtcst);

            if (isSecure[ref += 1] =="true")

                salesPrc = trans.getNumberValue("GETSP/DATA/SALES_PRICE");
            else
                salesPrc = 0;

            double AmountCost = 0;
            //double                                                                                                                lstprc                                                                                                                =                                                                                                                0;

            double qtyInv = mgr.readNumberValue("ITEM1_QTY_TO_INVOICE");

            if (!isNaN(qtyInv))
            {
                if (!isNaN(salesPrc))
                {
                    lstprc = trans.getNumberValue("GTPRTPRC/DATA/ITEM1PRTLSTPRC");
                    if (isNaN(lstprc))
                        lstprc = 0;
                    AmountCost = qtyInv * salesPrc;

                }
                else
                {
                    if (isSecure[ref += 1] =="true")
                    {
                        lstprc = trans.getNumberValue("GTPRTPRC/DATA/ITEM1PRTLSTPRC");
                        if (isNaN(lstprc))
                            lstprc = 0;
                        
                        AmountCost = qtyInv * lstprc;
                    }
                }   
            }
            else
            {
                if (!isNaN(salesPrc))
                {
                    lstprc = trans.getNumberValue("GTPRTPRC/DATA/ITEM1PRTLSTPRC");
                    if (isNaN(lstprc))
                        lstprc = 0;
                    plQuant = mgr.readNumberValue("PLANNED_QTY");
                    if (isNaN(plQuant))
                        plQuant = 0;

                    AmountCost = plQuant * salesPrc;
                }
                else
                {
                    if (isSecure[ref += 1] =="true")
                    {
                        lstprc = trans.getNumberValue("GTPRTPRC/DATA/ITEM1PRTLSTPRC");
                        if (isNaN(lstprc))
                            lstprc = 0;

                        plQuant = 0;
                        plQuant = mgr.readNumberValue("PLANNED_QTY");
                        if (isNaN(plQuant))
                            plQuant = 0;

                        AmountCost = plQuant * lstprc;
                    }
                }
            }  

            double disc = 0;
            disc = mgr.readNumberValue("ITEM1_DISCOUNT");

            String salesPrcstr = mgr.getASPField("ITEM1_SALES_PRICE").formatNumber(salesPrc);

            if (!isNaN(disc))
            {
                AmountCost = AmountCost - (disc/100 * AmountCost);
            }

            String lstprcStr = mgr.getASPField("ITEM1PRTLSTPRC").formatNumber(lstprc);
            String AmountCostStr = mgr.getASPField("ITEM1_PRICE_AMOUNT").formatNumber(AmountCost);
            txt = (mgr.isEmpty(salesPrcstr) ? "" : (salesPrcstr)) + "^" + 
                    (mgr.isEmpty(ctlgdesc) ? "" : (ctlgdesc)) + "^" + 
                    (mgr.isEmpty(prtcstStr) ? "" : prtcstStr)+ "^" + 
                    (mgr.isEmpty(lstprcStr) ? "" : lstprcStr)+ "^" + 
                    (mgr.isEmpty(AmountCostStr) ? "" : AmountCostStr)+ "^";

            mgr.responseWrite(txt);
        }
        else if ("ORG_CODE".equals(val))
        {
            String defCat="";
            String defCatDesc = "";
            String listPrice = "";
            String colRoleCode = mgr.readValue("ROLE_CODE","");
            String colCatalogNo  = mgr.readValue("CATALOG_NO","");

            secBuff = mgr.newASPTransactionBuffer();
            secBuff.addSecurityQuery("SALES_PART");
            secBuff.addSecurityQuery("ORGANIZATION_SALES_PART_API","Get_Def_Catalog_No");

            secBuff = mgr.perform(secBuff);

            if (secBuff.getSecurityInfo().itemExists("ORGANIZATION_SALES_PART_API.Get_Def_Catalog_No"))
            {
                cmd = trans.addCustomFunction("CATNO", "ORGANIZATION_SALES_PART_API.Get_Def_Catalog_No", "DEF_CATALOG_NO");
                cmd.addParameter("ITEM0_CONTRACT"); 
                cmd.addParameter("ORG_CODE"); 

                cmd = trans.addCustomFunction("GTOCTLGDESC1", "SALES_PART_API.Get_Catalog_Desc", "SLSPRTCTLGDESC");
                cmd.addParameter("ITEM0_CONTRACT"); 
                cmd.addReference("DEF_CATALOG_NO","CATNO/DATA");                                                                                                                                                                

                cmd = trans.addCustomFunction("LPRICE","SALES_PART_API.Get_List_Price","SLSPRTLSTPRICE");
                cmd.addParameter("ITEM0_CONTRACT");  
                cmd.addReference("DEF_CATALOG_NO","CATNO/DATA");                                                                                                                                                                

                trans = mgr.validate(trans);

                defCat = trans.getValue("CATNO/DATA/DEF_CATALOG_NO");
                defCatDesc = trans.getValue("GTOCTLGDESC1/DATA/SLSPRTCTLGDESC");
                listPrice = trans.getValue("LPRICE/DATA/SLSPRTLSTPRICE");
            }
            else
            {
                defCat = "";
                defCatDesc = "";
                listPrice = "";
            }

            numCost = NOT_A_NUMBER;
            String defNumCost = "";

            if (mgr.isEmpty(colRoleCode) || (!mgr.isEmpty(colRoleCode) && mgr.isEmpty(colCatalogNo)))
            {
                if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
                {
                    trans.clear();

                    if (checksec("WORK_ORDER_PLANNING_UTIL_API.Get_Cost",1,isSecure))
                    {
                        cmd = trans.addCustomFunction("GETCOST","WORK_ORDER_PLANNING_UTIL_API.Get_Cost","SLSPRTCOST");
                        cmd.addParameter("ORG_CODE",mgr.readValue("ORG_CODE",""));
                        cmd.addParameter("ROLE_CODE",mgr.readValue("ROLE_CODE",""));
                        cmd.addParameter("CATALOG_CONTRACT",mgr.readValue("ITEM0_CONTRACT","")); 
                        cmd.addParameter("CATALOG_NO",mgr.readValue("CATALOG_NO",""));
                        cmd.addParameter("ORG_CONTRACT",mgr.readValue("ORG_CONTRACT",""));
                    }
                }

                if (!mgr.isEmpty(defCat))
                {
                    if (checksec("WORK_ORDER_PLANNING_UTIL_API.Get_Cost",2,isSecure))
                    {
                        cmd = trans.addCustomFunction("GETDEFCOST","WORK_ORDER_PLANNING_UTIL_API.Get_Cost","DEFSLSPRTCOST");
                        cmd.addParameter("ORG_CODE",mgr.readValue("ORG_CODE",""));
                        cmd.addParameter("ROLE_CODE",mgr.readValue("ROLE_CODE",""));
                        cmd.addParameter("CATALOG_CONTRACT",mgr.readValue("ITEM0_CONTRACT","")); 
                        cmd.addParameter("CATALOG_NO",defCat); 
                        cmd.addParameter("ORG_CONTRACT",mgr.readValue("ORG_CONTRACT",""));
                    }
                }

                if (mgr.isEmpty(mgr.readValue("CATALOG_NO","")) || !mgr.isEmpty(defCat))
                    trans = mgr.perform(trans);

                if (isSecure[1] =="true")
                {
                    if (mgr.isEmpty(mgr.readValue("CATALOG_NO","")))
                        numCost = trans.getNumberValue("GETCOST/DATA/SLSPRTCOST");

                    if (!mgr.isEmpty(defCat))
                        defNumCost = trans.getBuffer("GETDEFCOST/DATA").getFieldValue("DEFSLSPRTCOST");
                }
                else
                {
                    numCost = NOT_A_NUMBER;
                    defNumCost = "";
                }

                trans.clear();

                cmd = trans.addCustomFunction("COSTB","ROLE_API.Get_Role_Cost","COST1");
                cmd.addParameter("ROLE_CODE");

                cmd = trans.addCustomFunction("COSTC","ORGANIZATION_API.Get_Org_Cost","COST2");
                cmd.addParameter("ORG_CONTRACT");
                cmd.addParameter("ORG_CODE");

                trans = mgr.perform(trans);

                numCost = trans.getNumberValue("COSTB/DATA/COST1");

                if (isNaN(numCost))
                    numCost = trans.getNumberValue("COSTC/DATA/COST2");

                String numCostStr = mgr.getASPField("SLSPRTCOST").formatNumber(numCost);
                if (mgr.isEmpty(defNumCost))
                {
                    defNumCost = trans.getBuffer("COSTB/DATA").getFieldValue("COST1");

                    if (mgr.isEmpty(defNumCost))
                        defNumCost = trans.getBuffer("COSTC/DATA").getFieldValue("COST2");
                }

                txt = (mgr.isEmpty(defCat) ? "" :defCat ) + "^"+(mgr.isEmpty(defCat) ? "" :defCat ) + "^" + (mgr.isEmpty(numCostStr) ? "" :numCostStr ) + "^" + (mgr.isEmpty(defNumCost) ? "" :defNumCost ) + "^"+
                      (mgr.isEmpty(defCatDesc) ? "" :defCatDesc ) + "^"+(mgr.isEmpty(listPrice) ? "" :listPrice ) + "^";
            }
            else
                txt = (mgr.isEmpty(defCat) ? "" :defCat ) + "^"+(mgr.isEmpty(mgr.readValue("CATALOG_NO","")) ? "" :mgr.readValue("CATALOG_NO","") ) + "^" + (mgr.isEmpty(mgr.readValue("SLSPRTCOST","")) ? "" :mgr.readValue("SLSPRTCOST","") ) + "^" + (mgr.isEmpty(mgr.readValue("DEFSLSPRTCOST","")) ? "" :mgr.readValue("DEFSLSPRTCOST","") ) + "^"+
                      (mgr.isEmpty(defCatDesc) ? "" :defCatDesc ) + "^"+(mgr.isEmpty(listPrice) ? "" :listPrice ) + "^";

            mgr.responseWrite(txt);
        }
        else if ("ROLE_CODE".equals(val))
        {
            String sContract = "";
            cmd = trans.addCustomFunction("ROLEDESC","ROLE_API.Get_Description","ROLEDESCRIPTION");
            cmd.addParameter("ROLE_CODE");
            trans = mgr.validate(trans);

            String roledesc = trans.getValue("ROLEDESC/DATA/ROLEDESCRIPTION");
            trans.clear();
            numCost = NOT_A_NUMBER;


            if (!mgr.isEmpty(mgr.readValue("CATALOG_NO","")))
            {
                if (checksec("WORK_ORDER_PLANNING_UTIL_API.Get_Cost",1,isSecure))
                {
                    cmd = trans.addCustomFunction("GTOCSTA","WORK_ORDER_PLANNING_UTIL_API.Get_Cost","SLSPRTCOST");
                    cmd.addParameter("ORG_CODE",mgr.readValue("ORG_CODE",""));
                    cmd.addParameter("ROLE_CODE",mgr.readValue("ROLE_CODE",""));
                    cmd.addParameter("CATALOG_CONTRACT",mgr.readValue("ITEM0_CONTRACT","")); 
                    cmd.addParameter("CATALOG_NO"); 
                    cmd.addParameter("ORG_CONTRACT",mgr.readValue("ORG_CONTRACT",""));

                    trans = mgr.validate(trans);

                    numCost = trans.getNumberValue("GTOCSTA/DATA/SLSPRTCOST");
                }
                else
                    numCost = NOT_A_NUMBER;
            }

            trans.clear();

            cmd = trans.addCustomFunction("COSTB","ROLE_API.Get_Role_Cost","COST1");
            cmd.addParameter("ROLE_CODE");

            cmd = trans.addCustomFunction("COSTC","ORGANIZATION_API.Get_Org_Cost","COST2");
            cmd.addParameter("ORG_CONTRACT");
            cmd.addParameter("ORG_CODE");

            trans = mgr.validate(trans);

            numCost = trans.getNumberValue("COSTB/DATA/COST1");

            if (isNaN(numCost))
                numCost = trans.getNumberValue("COSTC/DATA/COST2");

            String numCostStr = mgr.getASPField("SLSPRTCOST").formatNumber(numCost);


            trans.clear();

            cmd = trans.addCustomFunction("DEFCATNO","ROLE_SALES_PART_API.Get_Def_Catalog_No","CATALOG_NO");
            cmd.addParameter("ROLE_CODE");
            cmd.addParameter("ITEM0_CONTRACT"); 

            cmd = trans.addCustomFunction("GTOCTLGDESC1", "SALES_PART_API.Get_Catalog_Desc", "SLSPRTCTLGDESC");
            cmd.addParameter("ITEM0_CONTRACT"); 
            cmd.addReference("CATALOG_NO","DEFCATNO/DATA");                                                                                                                                                                

            cmd = trans.addCustomFunction("LPRICE","SALES_PART_API.Get_List_Price","SLSPRTLSTPRICE");
            cmd.addParameter("ITEM0_CONTRACT");  
            cmd.addReference("CATALOG_NO","DEFCATNO/DATA");                                                                                                                                                                

            trans = mgr.validate(trans);
            String catnum = trans.getValue("DEFCATNO/DATA/CATALOG_NO");
            String catDesc = trans.getValue("GTOCTLGDESC1/DATA/SLSPRTCTLGDESC");
            double numLPrice = trans.getNumberValue("LPRICE/DATA/SLSPRTLSTPRICE");

            if (mgr.isEmpty(catnum))
            {
                trans.clear();
                cmd = trans.addCustomFunction("CATNO", "ORGANIZATION_SALES_PART_API.Get_Def_Catalog_No", "DEF_CATALOG_NO");
                cmd.addParameter("ITEM0_CONTRACT"); 
                cmd.addParameter("ORG_CODE"); 

                cmd = trans.addCustomFunction("GTOCTLGDESC1", "SALES_PART_API.Get_Catalog_Desc", "SLSPRTCTLGDESC");
                cmd.addParameter("ITEM0_CONTRACT"); 
                cmd.addReference("DEF_CATALOG_NO","CATNO/DATA");                                                                                                                                                                

                cmd = trans.addCustomFunction("LPRICE","SALES_PART_API.Get_List_Price","SLSPRTLSTPRICE");
                cmd.addParameter("ITEM0_CONTRACT");  
                cmd.addReference("DEF_CATALOG_NO","CATNO/DATA"); 

                trans = mgr.validate(trans);
                catnum = trans.getValue("CATNO/DATA/DEF_CATALOG_NO");
                catDesc = trans.getValue("GTOCTLGDESC1/DATA/SLSPRTCTLGDESC");
                numLPrice = trans.getNumberValue("LPRICE/DATA/SLSPRTLSTPRICE");
            }

            String numLPriceStr = mgr.getASPField("SLSPRTLSTPRICE").formatNumber(numLPrice);

            if (mgr.isEmpty(catnum))
                catnum = mgr.readValue("CATALOG_NO","");
            sContract = mgr.readValue("ITEM0_CONTRACT","");

            txt = (mgr.isEmpty(catnum) ? "" :catnum ) + "^" + (mgr.isEmpty(roledesc) ? "" :roledesc ) + "^" + (mgr.isEmpty(numCostStr) ? "" :numCostStr ) + "^" + (mgr.isEmpty(catDesc) ? "" :catDesc ) + "^" + (mgr.isEmpty(numLPriceStr) ? "" :numLPriceStr ) + "^";

            mgr.responseWrite(txt);  
        }
        else if (("PLANNED_MEN".equals(val)) || ("PLANNED_HOURS".equals(val)) || ("QTY_TO_INVOICE".equals(val)) || ("SALES_PRICE".equals(val)) || ("DISCOUNT".equals(val)) || ("WORK_ORDER_INVOICE_TYPE".equals(val)))
        {
            double nPlanQtyVal = 0;
            double valFlag = 1;

            String salesPartNoVal = mgr.readValue("CATALOG_NO");    

            double nSalesPriceVal = mgr.readNumberValue("SALES_PRICE");
            if (isNaN(nSalesPriceVal))
                nSalesPriceVal = 0;

            double nSlsPrtListPriceVal = mgr.readNumberValue("SLSPRTLSTPRICE");
            if (isNaN(nSlsPrtListPriceVal))
                nSlsPrtListPriceVal = 0;

            String invTypeVal = mgr.readValue("WORK_ORDER_INVOICE_TYPE");

            double nDiscountVal = mgr.readNumberValue("DISCOUNT");
            if (isNaN(nDiscountVal))
                nDiscountVal = 0;

            double nQtyToInvVal = mgr.readNumberValue("QTY_TO_INVOICE");
            if (isNaN(nQtyToInvVal))
                nQtyToInvVal = 0;

            double nPlannedHrs = mgr.readNumberValue("PLANNED_HOURS");
            if (isNaN(nPlannedHrs))
                nPlannedHrs = 0;

            double nPlannedMen = mgr.readNumberValue("PLANNED_MEN");
            if (isNaN(nPlannedMen))
                nPlannedMen = 0;

            double nValPriceAmt = validatePriceAmtCrafts(valFlag,salesPartNoVal,nSalesPriceVal,nSlsPrtListPriceVal,invTypeVal,nDiscountVal,nQtyToInvVal,nPlanQtyVal,nPlannedHrs,nPlannedMen);

            String strPriceAmt = mgr.getASPField("ITEM0_PRICE_AMOUNT").formatNumber(nValPriceAmt);
            txt = (mgr.isEmpty(strPriceAmt) ? "" : (strPriceAmt))+ "^" ;
            mgr.responseWrite(txt);
        }
        else if ("TEAM_ID".equals(val))
        {
            cmd = trans.addCustomFunction("TDESC", "Maint_Team_API.Get_Description", "ITEM0_TEAMDESC" );    
            cmd.addParameter("TEAM_ID");
            cmd.addParameter("TEAM_CONTRACT");
            trans = mgr.validate(trans);   
            teamDesc  = trans.getValue("TDESC/DATA/ITEM0_TEAMDESC");

            txt =  (mgr.isEmpty(teamDesc) ? "" : (teamDesc)) + "^";
            mgr.responseWrite(txt);
        }

        else if ("WORK_ORDER_COST_TYPE".equals(val))
        {
            cmd = trans.addCustomFunction("WOCO1", "Work_Order_Cost_Type_Api.Get_Client_Value(0)", "PERS");

            cmd = trans.addCustomFunction("WOCO2", "Work_Order_Cost_Type_Api.Get_Client_Value(1)", "METR");

            trans = mgr.validate(trans);

            String perso = trans.getValue("WOCO1/DATA/PERS");
            String meter = trans.getValue("WOCO2/DATA/METR");

            txt = (mgr.isEmpty(perso) ? "" : perso) + "^" + (mgr.isEmpty(meter) ? "" : meter)+ "^" ;

            mgr.responseWrite(txt);

        }
        else if (("ITEM1_SALES_PRICE".equals(val)) || ("ITEM1_DISCOUNT".equals(val)) || ("ITEM1_QTY_TO_INVOICE".equals(val)) || ("ITEM1_WORK_ORDER_INVOICE_TYPE".equals(val)) || ("PLANNED_QTY".equals(val)))
        {
            double valFlag = 0;
            double nPlannedMen = 0;
            double nPlannedHrs = 0;
            String salesPartNoVal = mgr.readValue("ITEM1_CATALOG_NO");
            double nCost = 0;
            String strCost = mgr.readValue("ITEM1PRTCOST","");

            double nSalesPriceVal = mgr.readNumberValue("ITEM1_SALES_PRICE");
            if (isNaN(nSalesPriceVal))
                nSalesPriceVal = 0;

            double nSlsPrtListPriceVal = mgr.readNumberValue("ITEM1PRTLSTPRC");
            if (isNaN(nSlsPrtListPriceVal))
                nSlsPrtListPriceVal = 0;

            String invTypeVal = mgr.readValue("ITEM1_WORK_ORDER_INVOICE_TYPE");

            double nDiscountVal = mgr.readNumberValue("ITEM1_DISCOUNT");
            if (isNaN(nDiscountVal))
                nDiscountVal = 0;

            double nQtyToInvVal = mgr.readNumberValue("ITEM1_QTY_TO_INVOICE");
            if (isNaN(nQtyToInvVal))
                nQtyToInvVal = 0;

            double nPlanQtyVal = mgr.readNumberValue("PLANNED_QTY");
            if (isNaN(nPlanQtyVal))
                nPlanQtyVal = 0;

            double nValPriceAmt = validatePriceAmtCrafts(valFlag,salesPartNoVal,nSalesPriceVal,nSlsPrtListPriceVal,invTypeVal,nDiscountVal,nQtyToInvVal,nPlanQtyVal,nPlannedHrs,nPlannedMen);

            String strPriceAmt = mgr.getASPField("ITEM1_PRICE_AMOUNT").formatNumber(nValPriceAmt);

            trans.clear();

            cmd = trans.addCustomCommand("GETCOST","Active_Separate_API.Get_Inventory_Value");
            cmd.addParameter("ITEM1PRTCOST");
            cmd.addParameter("CONTRACT",mgr.readValue("ITEM1_CONTRACT"));
            cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
            cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
            cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
            cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));
            
            trans = mgr.validate(trans);

            nCost =  trans.getNumberValue("GETCOST/DATA/ITEM1PRTCOST");
            strCost = mgr.getASPField("ITEM1PRTCOST").formatNumber(nCost);

            txt = (mgr.isEmpty(strPriceAmt) ? "" : (strPriceAmt))+ "^" +
                  (mgr.isEmpty(strCost) ? "" : (strCost))+ "^" ;

            mgr.responseWrite(txt);
        }
        else if ("ITEM2_CATALOG_NO".equals(val))
        {
            ASPTransactionBuffer testbuff = mgr.newASPTransactionBuffer();
            testbuff.addSecurityQuery("SALES_PART");
            mgr.perform(testbuff);
            ASPBuffer sec = testbuff.getSecurityInfo();
            String sPlCost = "";

            if (sec.itemExists("SALES_PART"))
            {
                trans.clear();

                if (!mgr.isEmpty(mgr.readValue("ITEM2_CATALOG_NO")))
                {
                    cmd = trans.addCustomFunction("GETITEM2LP", "Sales_Part_API.Get_List_Price", "ITEM2_SALES_PRICE");
                    cmd.addParameter("CATALOG_CONTRACT"); 
                    cmd.addParameter("CATALOG_NO",mgr.readValue("ITEM2_CATALOG_NO"));                                                                                                                                                                

                    cmd = trans.addCustomFunction("GETDESC", "SALES_PART_API.Get_Catalog_Desc", "ITEM2_SALES_PART_DESC");
                    cmd.addParameter("CATALOG_CONTRACT",mgr.readValue("CATALOG_CONTRACT")); 
                    cmd.addParameter("CATALOG_NO",mgr.readValue("ITEM2_CATALOG_NO"));

                    cmd = trans.addCustomFunction("GETLISTPRICE","SALES_PART_API.Get_List_Price","LIST_PRICE");
                    cmd.addParameter("CATALOG_CONTRACT");
                    cmd.addParameter("CATALOG_NO",mgr.readValue("ITEM2_CATALOG_NO"));                                                                                                                                                                

                    cmd = trans.addCustomFunction("GETCOSTTYPE2","Work_Order_Cost_Type_Api.Get_Client_Value(2)","WORK_ORDER_COST_TYPE");
                    cmd = trans.addCustomFunction("GETCOSTTYPE3","Work_Order_Cost_Type_Api.Get_Client_Value(3)","WORK_ORDER_COST_TYPE");
                    cmd = trans.addCustomFunction("GETCOSTTYPE0","Work_Order_Cost_Type_Api.Get_Client_Value(0)","WORK_ORDER_COST_TYPE");

                    trans = mgr.validate(trans);

                    double item2LP = trans.getNumberValue("GETITEM2LP/DATA/SALES_PRICE");
                    String costTypePersonal = trans.getValue("GETCOSTTYPE0/DATA/WORK_ORDER_COST_TYPE"); 
                    String costTypeExternal = trans.getValue("GETCOSTTYPE2/DATA/WORK_ORDER_COST_TYPE"); 
                    String costTypeExpense = trans.getValue("GETCOSTTYPE3/DATA/WORK_ORDER_COST_TYPE"); 
                    String salesPartDesc = trans.getValue("GETDESC/DATA/ITEM2_SALES_PART_DESC");
                    String listPrice = trans.getValue("GETLISTPRICE/DATA/LIST_PRICE");
                    trans.clear();

                    if ((mgr.readValue("WORK_ORDER_COST_TYPE").equals(costTypeExternal)) || (mgr.readValue("WORK_ORDER_COST_TYPE").equals(costTypeExpense)))
                    {
                        cmd = trans.addCustomFunction("GETPLCOST", "Sales_Part_API.Get_Cost", "COST");
                        cmd.addParameter("CATALOG_CONTRACT"); 
                        cmd.addParameter("CATALOG_NO",mgr.readValue("ITEM2_CATALOG_NO"));  

                        trans = mgr.validate(trans);
                        double nCost = trans.getNumberValue("GETPLCOST/DATA/COST");
                    }
                    if (mgr.readValue("WORK_ORDER_COST_TYPE").equals(costTypePersonal))
                    {
                        trans.clear();
                        cmd = trans.addCustomFunction("GETPERCOST","Standard_Job_Planning_API.Get_Cost_By_Planning","COST");
                        cmd.addParameter("STD_JOB_ID",mgr.readValue("ITEM2_STD_JOB_ID"));
                        cmd.addParameter("CONTRACT",mgr.readValue("ITEM2_STD_JOB_CONTRACT"));
                        cmd.addParameter("STD_JOB_REVISION",mgr.readValue("ITEM2_STD_JOB_REVISION"));
                        cmd.addParameter("ROW_NO",mgr.readValue("ITEM2_ROW_NO"));
                        cmd.addParameter("CATALOG_CONTRACT");
                        cmd.addParameter("CATALOG_NO",mgr.readValue("ITEM2_CATALOG_NO"));
                        trans = mgr.validate(trans);
                        sPlCost = trans.getValue("GETPERCOST/DATA/COST");
                    }
                    else
                        sPlCost = mgr.getASPField("COST").formatNumber(nCost);

                    double valFlag = 0;
                    double nPlannedMen = 0;
                    double nPlannedHrs = 0;
                    String salesPartNoVal = mgr.readValue("ITEM2_CATALOG_NO");

                    double nSlsPrtListPriceVal = mgr.readNumberValue("ITEM2_SALES_PRICE");
                    if (isNaN(nSlsPrtListPriceVal))
                        nSlsPrtListPriceVal = 0;

                    double nSalesPriceVal = item2LP;
                    if (isNaN(nSalesPriceVal))
                        nSalesPriceVal = 0;

                    String invTypeVal = mgr.readValue("ITEM2_WORK_ORDER_INVOICE_TYPE");

                    double nDiscountVal = mgr.readNumberValue("ITEM2_DISCOUNT");
                    if (isNaN(nDiscountVal))
                        nDiscountVal = 0;

                    double nQtyToInvVal = mgr.readNumberValue("ITEM2_QTY_TO_INVOICE");
                    if (isNaN(nQtyToInvVal))
                        nQtyToInvVal = 0;

                    double nPlanQtyVal = mgr.readNumberValue("QUANTITY");
                    if (isNaN(nPlanQtyVal))
                        nPlanQtyVal = 0;

                    if (!mgr.isEmpty(mgr.readValue("ITEM2_CATALOG_NO"))&&(mgr.readValue("WORK_ORDER_COST_TYPE").equals(costTypeExternal)) || (mgr.readValue("WORK_ORDER_COST_TYPE").equals(costTypeExpense)))
                    {
                        if (nPlanQtyVal==0)
                        {
                            nPlanQtyVal = 1;
                        }
                    }
                    double nValPriceAmt = validatePriceAmtCrafts(valFlag,salesPartNoVal,item2LP,nSlsPrtListPriceVal,invTypeVal,nDiscountVal,nQtyToInvVal,nPlanQtyVal,nPlannedHrs,nPlannedMen);

                    String strPriceAmt = mgr.getASPField("ITEM1_PRICE_AMOUNT").formatNumber(nValPriceAmt);
                    String strListPrice = mgr.getASPField("ITEM2_SALES_PRICE").formatNumber(item2LP);
                    String strQtyPlan = mgr.getASPField("QUANTITY").formatNumber(nPlanQtyVal);
                    txt = (mgr.isEmpty(strListPrice) ? "" : (strListPrice))+ "^" + (mgr.isEmpty(sPlCost) ? "" : (sPlCost))+ "^" + (mgr.isEmpty(strPriceAmt) ? "" : (strPriceAmt))+ "^"+(mgr.isEmpty(salesPartDesc) ? "" : (salesPartDesc))+"^"+(mgr.isEmpty(listPrice) ? "" : (listPrice))+"^"+(mgr.isEmpty(strQtyPlan) ? "" : (strQtyPlan))+"^";

                    mgr.responseWrite(txt);
                }
            }
        }
        else if (("QUANTITY".equals(val)) || ("ITEM2_QTY_TO_INVOICE".equals(val)) || ("ITEM2_SALES_PRICE".equals(val)) || ("ITEM2_DISCOUNT".equals(val)) || ("ITEM2_WORK_ORDER_INVOICE_TYPE".equals(val)))
        {
            double nPlannedHrs = 0;
            double nPlannedMen = 0;
            double valFlag = 0;

            String salesPartNoVal = mgr.readValue("ITEM2_CATALOG_NO");    

            double nSalesPriceVal = mgr.readNumberValue("ITEM2_SALES_PRICE");
            if (isNaN(nSalesPriceVal))
                nSalesPriceVal = 0;

            double nSlsPrtListPriceVal = mgr.readNumberValue("ITEM2_SALES_PRICE");
            if (isNaN(nSlsPrtListPriceVal))
                nSlsPrtListPriceVal = 0;

            String invTypeVal = mgr.readValue("ITEM2_WORK_ORDER_INVOICE_TYPE");

            double nDiscountVal = mgr.readNumberValue("ITEM2_DISCOUNT");
            if (isNaN(nDiscountVal))
                nDiscountVal = 0;

            double nQtyToInvVal = mgr.readNumberValue("ITEM2_QTY_TO_INVOICE");
            if (isNaN(nQtyToInvVal))
                nQtyToInvVal = 0;

            double nPlanQtyVal = mgr.readNumberValue("QUANTITY");
            if (isNaN(nPlanQtyVal))
                nPlanQtyVal = 0;

            double nValPriceAmt = validatePriceAmtCrafts(valFlag,salesPartNoVal,nSalesPriceVal,nSlsPrtListPriceVal,invTypeVal,nDiscountVal,nQtyToInvVal,nPlanQtyVal,nPlannedHrs,nPlannedMen);

            String strPriceAmt = mgr.getASPField("ITEM2_PRICE_AMOUNT").formatNumber(nValPriceAmt);
            txt = (mgr.isEmpty(strPriceAmt) ? "" : (strPriceAmt))+ "^" ;
            mgr.responseWrite(txt);
        }
        else if ("COST".equals(val))
        {
            String costTypeVal = mgr.readValue("WORK_ORDER_COST_TYPE");
            String sCatalogNo = mgr.readValue("ITEM2_CATALOG_NO");

            double costAmtVal = mgr.readNumberValue("COST");
            if (isNaN(costAmtVal))
                costAmtVal = 0;

            double nQtyVal = mgr.readNumberValue("QUANTITY");
            if (isNaN(nQtyVal))
                nQtyVal = 0;

            trans.clear();
            cmd = trans.addCustomFunction("GETWOCOSTTYPE2","Work_Order_Cost_Type_Api.Get_Client_Value(2)","WORK_ORDER_COST_TYPE");
            cmd = trans.addCustomFunction("GETWOCOSTTYPE3","Work_Order_Cost_Type_Api.Get_Client_Value(3)","WORK_ORDER_COST_TYPE");
            trans = mgr.validate(trans);

            String woCostTypeExternal = trans.getValue("GETWOCOSTTYPE2/DATA/WORK_ORDER_COST_TYPE"); 
            String woCostTypeExpense = trans.getValue("GETWOCOSTTYPE3/DATA/WORK_ORDER_COST_TYPE");

            if (costTypeVal.equals(woCostTypeExternal)||costTypeVal.equals(woCostTypeExpense))
            {
                if ((costAmtVal>=0||sCatalogNo != null)&&(nQtyVal==0))
                {
                    nQtyVal =1;
                    String strQty = mgr.getASPField("QUANTITY").formatNumber(nQtyVal);
                    txt = (mgr.isEmpty(strQty) ? "" : (strQty))+ "^" ;
                    mgr.responseWrite(txt);
                }
            }
        }

        else if ("TOOL_FACILITY_ID".equals(val))
        {
            String tftype;
            tfspsite = mgr.readValue("SALES_PART_SITE");

            cmd = trans.addCustomFunction("TFDESC","Tool_Facility_API.Get_Tool_Facility_Description","TOOL_FACILITY_DESC");
            cmd.addParameter("TOOL_FACILITY_ID");

            if (mgr.isEmpty(mgr.readValue("TOOL_FACILITY_TYPE")))
            {
                cmd = trans.addCustomFunction("TFTYPE","Tool_Facility_API.Get_Tool_Facility_Type","TOOL_FACILITY_TYPE");
                cmd.addParameter("TOOL_FACILITY_ID");

                cmd = trans.addCustomFunction("TFTYPEDESC","Tool_Facility_Type_API.Get_Type_Description","TOOL_FACILITY_TYPE_DESC");
                cmd.addReference("TOOL_FACILITY_TYPE","TFTYPE/DATA");
            }

            if (isNaN(mgr.readNumberValue("QTY")))
            {
                cmd = trans.addCustomFunction("TFQTY","Connect_Tools_Facilities_API.Get_Qty","QTY_DUMMY");
                cmd.addParameter("TOOL_FACILITY_ID");
                cmd.addParameter("CONTRACT",mgr.readValue("ITEM3_CONTRACT"));           
                cmd.addParameter("ORG_CODE",mgr.readValue("ITEM3_ORG_CODE"));
            }

            cmd = trans.addCustomFunction("TFCOST","Standard_Job_Tool_Facility_API.Get_Cost","ITEM3_COST");
            cmd.addParameter("TOOL_FACILITY_ID");
            if (mgr.isEmpty(mgr.readValue("TOOL_FACILITY_TYPE")))
                cmd.addReference("TOOL_FACILITY_TYPE","TFTYPE/DATA");
            else
                cmd.addParameter("TOOL_FACILITY_TYPE",mgr.readValue("TOOL_FACILITY_TYPE"));

            cmd = trans.addCustomFunction("TFCOSTCURR","Standard_Job_Tool_Facility_API.Get_Cost_Currency","COST_CURRENCY");
            cmd.addParameter("STD_JOB_ID",mgr.readValue("ITEM3_STD_JOB_ID"));
            cmd.addParameter("CONTRACT",mgr.readValue("ITEM3_STD_JOB_CONTRACT"));
            cmd.addParameter("STD_JOB_REVISION",mgr.readValue("ITEM3_STD_JOB_REVISION"));

            if (mgr.isEmpty(mgr.readValue("NOTE")))
            {
                cmd = trans.addCustomFunction("TFNOTE","Connect_Tools_Facilities_API.Get_Note","NOTE");
                cmd.addParameter("TOOL_FACILITY_ID");
                cmd.addParameter("CONTRACT",mgr.readValue("ITEM3_CONTRACT"));           
                cmd.addParameter("ORG_CODE",mgr.readValue("ITEM3_ORG_CODE"));
            }

            if (mgr.isEmpty(mgr.readValue("ITEM3_SALES_PART")) && !(mgr.isEmpty(mgr.readValue("TOOL_FACILITY_ID"))))
            {
                cmd = trans.addCustomFunction("TFSP","Connect_Tools_Facilities_API.Get_Catalog_No","ITEM3_SALES_PART");
                cmd.addParameter("TOOL_FACILITY_ID");
                cmd.addParameter("CONTRACT",mgr.readValue("ITEM3_CONTRACT"));           
                cmd.addParameter("ORG_CODE",mgr.readValue("ITEM3_ORG_CODE"));

                cmd = trans.addCustomFunction("TFSPS","Connect_Tools_Facilities_API.Get_Catalog_No_Contract","SALES_PART_SITE");
                cmd.addParameter("TOOL_FACILITY_ID");
                cmd.addParameter("CONTRACT",mgr.readValue("ITEM3_CONTRACT"));           
                cmd.addParameter("ORG_CODE",mgr.readValue("ITEM3_ORG_CODE"));

                cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","SALES_PART_DESC");
                cmd.addParameter("SALES_PART_SITE",tfspsite);
                cmd.addReference("ITEM3_SALES_PART","TFSP/DATA");           

                cmd = trans.addCustomFunction("TFSPRICE","Standard_Job_Tool_Facility_API.Calculate_Sales_Price","ITEM3_LIST_PRICE");
                cmd.addReference("ITEM3_SALES_PART","TFSP/DATA");
                cmd.addParameter("SALES_PART_SITE",tfspsite);

                cmd = trans.addCustomFunction("TFSPCURR","Standard_Job_Tool_Facility_API.Get_Sales_Currency","PRICE_CURRENCY");
                cmd.addParameter("SALES_PART_SITE",tfspsite);

                if (!(mgr.isEmpty(mgr.readValue("QTY"))) && !(mgr.isEmpty(mgr.readValue("PLANNED_HOUR"))))
                {
                    cmd = trans.addCustomFunction("TFPRICEAM","Standard_Job_Tool_Facility_API.Calculated_Price_Amount","PRICE_AMOUNT");
                    cmd.addReference("ITEM3_SALES_PART","TFSP/DATA");
                    cmd.addParameter("SALES_PART_SITE",tfspsite);
                    cmd.addParameter("QTY",mgr.readValue("QTY"));           
                    cmd.addParameter("PLANNED_HOUR",mgr.readValue("PLANNED_HOUR"));
                    if (mgr.isEmpty(mgr.readValue("ITEM3_SALES_PRICE")))
                        cmd.addReference("ITEM3_LIST_PRICE","TFSPRICE/DATA");
                    else
                        cmd.addParameter("ITEM3_SALES_PRICE",mgr.readValue("ITEM3_SALES_PRICE"));           
                    cmd.addParameter("ITEM3_DISCOUNT",mgr.readValue("ITEM3_DISCOUNT"));
                }
            }

            trans = mgr.validate(trans);

            String tfdesc = trans.getValue("TFDESC/DATA/TOOL_FACILITY_DESC");

            if (mgr.isEmpty(mgr.readValue("TOOL_FACILITY_TYPE")))
            {
                tftype = trans.getValue("TFTYPE/DATA/TOOL_FACILITY_TYPE");
                tftypedesc = trans.getValue("TFTYPEDESC/DATA/TOOL_FACILITY_TYPE_DESC");
            }
            else
            {
                tftype = mgr.readValue("TOOL_FACILITY_TYPE");
                tftypedesc = mgr.readValue("TOOL_FACILITY_TYPE_DESC");
            }        

            if (isNaN(mgr.readNumberValue("QTY")))
            {
                qty = trans.getNumberValue("TFQTY/DATA/QTY_DUMMY");
                if (qty != 1)
                    qty = mgr.readNumberValue("QTY");
            }
            else
                qty = mgr.readNumberValue("QTY");

            tfcost = trans.getNumberValue("TFCOST/DATA/COST");
            tfcostcurr = trans.getValue("TFCOSTCURR/DATA/COST_CURRENCY");

            if (mgr.isEmpty(mgr.readValue("NOTE")))
                tfnote = trans.getValue("TFNOTE/DATA/NOTE");
            else
                tfnote = mgr.readValue("NOTE");

            if (mgr.isEmpty(mgr.readValue("ITEM3_SALES_PART")) && !(mgr.isEmpty(mgr.readValue("TOOL_FACILITY_ID"))))
            {
                tfsalesp = trans.getValue("TFSP/DATA/SALES_PART");
                tfspsite = trans.getValue("TFSPS/DATA/SALES_PART_SITE");
                tfspdesc = trans.getValue("TFSPDESC/DATA/SALES_PART_DESC");
                tfsprice = trans.getNumberValue("TFSPRICE/DATA/ITEM3_LIST_PRICE");
                tfspcurr = trans.getValue("TFSPCURR/DATA/PRICE_CURRENCY");
                if (!(mgr.isEmpty(mgr.readValue("QTY"))) && !(mgr.isEmpty(mgr.readValue("PLANNED_HOUR"))))
                    tfpamoun = trans.getNumberValue("TFPRICEAM/DATA/PRICE_AMOUNT");
                else
                    tfpamoun = mgr.readNumberValue("PRICE_AMOUNT");
            }
            else
            {
                tfsalesp = mgr.readValue("SALES_PART");
                tfspdesc = mgr.readValue("SALES_PART_DESC");
                tfsprice = mgr.readNumberValue("SALES_PRICE");
                tfspcurr = mgr.readValue("PRICE_CURRENCY");
                tfpamoun = mgr.readNumberValue("PRICE_AMOUNT");
            }        

            trans.clear();
            strqty = mgr.getASPField("QTY").formatNumber(qty);
            strtfcost = mgr.getASPField("ITEM3_COST").formatNumber(tfcost);
            stfsprice = mgr.getASPField("ITEM3_SALES_PRICE").formatNumber(tfsprice);
            stfpamoun = mgr.getASPField("PRICE_AMOUNT").formatNumber(tfpamoun);
            txt = (mgr.isEmpty(tfdesc) ? "" : (tfdesc))+ "^" + (mgr.isEmpty(tftype) ? "" : (tftype))+ "^" + (mgr.isEmpty(tftypedesc) ? "" : (tftypedesc))+ "^" + (mgr.isEmpty(strqty) ? "" : (strqty))+ "^" ; 
            txt = txt + (mgr.isEmpty(strtfcost) ? "":(strtfcost))+ "^" +(mgr.isEmpty(tfcostcurr) ? "" : (tfcostcurr))+ "^" +(mgr.isEmpty(tfnote) ? "" : (tfnote))+ "^"+(mgr.isEmpty(tfsalesp) ? "" : (tfsalesp))+ "^"+(mgr.isEmpty(tfspdesc) ? "" : (tfspdesc))+ "^"+(mgr.isEmpty(stfsprice) ? "" : (stfsprice))+ "^"+(mgr.isEmpty(tfspcurr) ? "" : (tfspcurr))+ "^"+(mgr.isEmpty(stfpamoun) ? "" : (stfpamoun))+ "^"; 
            mgr.responseWrite(txt);
        }
        else if ("TOOL_FACILITY_TYPE".equals(val))
        {
            if (!(mgr.isEmpty(mgr.readValue("TOOL_FACILITY_TYPE"))))
            {
                cmd = trans.addCustomFunction("TFTYPEDESC","Tool_Facility_Type_API.Get_Type_Description","TOOL_FACILITY_TYPE_DESC");
                cmd.addParameter("TOOL_FACILITY_TYPE");

                cmd = trans.addCustomFunction("TFCOST","Standard_Job_Tool_Facility_API.Get_Cost","ITEM3_COST");
                cmd.addParameter("TOOL_FACILITY_ID",mgr.readValue("TOOL_FACILITY_ID"));
                cmd.addParameter("TOOL_FACILITY_TYPE");

                cmd = trans.addCustomFunction("TFCOSTCURR","Standard_Job_Tool_Facility_API.Get_Cost_Currency","COST_CURRENCY");
                cmd.addParameter("STD_JOB_ID",mgr.readValue("ITEM3_STD_JOB_ID"));
                cmd.addParameter("CONTRACT",mgr.readValue("ITEM3_STD_JOB_CONTRACT"));
                cmd.addParameter("STD_JOB_REVISION",mgr.readValue("ITEM3_STD_JOB_REVISION"));

                trans = mgr.validate(trans);

                tftypedesc = trans.getValue("TFTYPEDESC/DATA/TOOL_FACILITY_TYPE_DESC");
                tfcost = trans.getNumberValue("TFCOST/DATA/COST");
                tfcostcurr = trans.getValue("TFCOSTCURR/DATA/COST_CURRENCY");
            }
            else
            {
                tftypedesc = null;
                if (mgr.isEmpty(mgr.readValue("TOOL_FACILITY_ID")))
                {
                    tfcost = 0;
                    tfcostcurr = null;
                }
                else
                {
                    tfcost = mgr.readNumberValue("ITEM3_COST");
                    tfcostcurr = mgr.readValue("COST_CURRENCY");
                }
            }

            trans.clear();
            strtfcost = mgr.getASPField("ITEM3_COST").formatNumber(tfcost);
            txt = (mgr.isEmpty(tftypedesc) ? "" : (tftypedesc))+ "^" + (mgr.isEmpty(strtfcost) ? "":(strtfcost))+ "^" +(mgr.isEmpty(tfcostcurr) ? "" : (tfcostcurr))+ "^" ; 
            mgr.responseWrite(txt);
        }
        else if (("ITEM3_CONTRACT".equals(val)) || ("ITEM3_ORG_CODE".equals(val)))
        {
            tfspsite = mgr.readValue("SALES_PART_SITE");

            if (!(mgr.isEmpty(mgr.readValue("TOOL_FACILITY_ID"))) && !(mgr.isEmpty(mgr.readValue("ITEM3_CONTRACT"))) && !(mgr.isEmpty(mgr.readValue("ITEM3_ORG_CODE"))))
            {
                if (mgr.isEmpty(mgr.readValue("NOTE")))
                {
                    cmd = trans.addCustomFunction("TFNOTE","Connect_Tools_Facilities_API.Get_Note","NOTE");
                    cmd.addParameter("TOOL_FACILITY_ID");
                    cmd.addParameter("CONTRACT",mgr.readValue("ITEM3_CONTRACT"));           
                    cmd.addParameter("ORG_CODE",mgr.readValue("ITEM3_ORG_CODE"));
                }

                cmd = trans.addCustomFunction("TFQTY","Connect_Tools_Facilities_API.Get_Qty","QTY_DUMMY");
                cmd.addParameter("TOOL_FACILITY_ID");
                cmd.addParameter("CONTRACT",mgr.readValue("ITEM3_CONTRACT"));           
                cmd.addParameter("ORG_CODE",mgr.readValue("ITEM3_ORG_CODE"));

                if (mgr.isEmpty(mgr.readValue("ITEM3_SALES_PART")))
                {
                    cmd = trans.addCustomFunction("TFSP","Connect_Tools_Facilities_API.Get_Catalog_No","ITEM3_SALES_PART");
                    cmd.addParameter("TOOL_FACILITY_ID");
                    cmd.addParameter("CONTRACT",mgr.readValue("ITEM3_CONTRACT"));           
                    cmd.addParameter("ORG_CODE",mgr.readValue("ITEM3_ORG_CODE"));

                    cmd = trans.addCustomFunction("TFSPS","Connect_Tools_Facilities_API.Get_Catalog_No_Contract","SALES_PART_SITE");
                    cmd.addParameter("TOOL_FACILITY_ID");
                    cmd.addParameter("CONTRACT",mgr.readValue("ITEM3_CONTRACT"));           
                    cmd.addParameter("ORG_CODE",mgr.readValue("ITEM3_ORG_CODE"));

                    cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","SALES_PART_DESC");
                    cmd.addParameter("SALES_PART_SITE",tfspsite);
                    cmd.addReference("ITEM3_SALES_PART","TFSP/DATA");           

                    cmd = trans.addCustomFunction("TFSPRICE","Standard_Job_Tool_Facility_API.Calculate_Sales_Price","ITEM3_LIST_PRICE");
                    cmd.addReference("ITEM3_SALES_PART","TFSP/DATA");
                    cmd.addParameter("SALES_PART_SITE",tfspsite);

                    cmd = trans.addCustomFunction("TFSPCURR","Standard_Job_Tool_Facility_API.Get_Sales_Currency","PRICE_CURRENCY");
                    cmd.addParameter("SALES_PART_SITE",tfspsite);

                    if (!(mgr.isEmpty(mgr.readValue("QTY"))) && !(mgr.isEmpty(mgr.readValue("PLANNED_HOUR"))))
                    {
                        cmd = trans.addCustomFunction("TFPRICEAM","Standard_Job_Tool_Facility_API.Calculated_Price_Amount","PRICE_AMOUNT");
                        cmd.addReference("ITEM3_SALES_PART","TFSP/DATA");
                        cmd.addParameter("SALES_PART_SITE",tfspsite);
                        cmd.addParameter("QTY",mgr.readValue("QTY"));           
                        cmd.addParameter("PLANNED_HOUR",mgr.readValue("PLANNED_HOUR"));
                        if (mgr.isEmpty(mgr.readValue("ITEM3_SALES_PRICE")))
                            cmd.addReference("ITEM3_LIST_PRICE","TFSPRICE/DATA");
                        else
                            cmd.addParameter("ITEM3_SALES_PRICE",mgr.readValue("ITEM3_SALES_PRICE"));
                        cmd.addParameter("ITEM3_DISCOUNT",mgr.readValue("ITEM3_DISCOUNT"));
                    }
                }

                trans = mgr.validate(trans);

                if (mgr.isEmpty(mgr.readValue("NOTE")))
                    tfnote = trans.getValue("TFNOTE/DATA/NOTE");
                else
                    tfnote = mgr.readValue("NOTE");

                qty = trans.getNumberValue("TFQTY/DATA/QTY_DUMMY");
                if (qty != 1)
                    qty = mgr.readNumberValue("QTY");

                if (mgr.isEmpty(mgr.readValue("ITEM3_SALES_PART")))
                {
                    tfsalesp = trans.getValue("TFSP/DATA/SALES_PART");
                    tfspsite = trans.getValue("TFSPS/DATA/SALES_PART_SITE");
                    tfspdesc = trans.getValue("TFSPDESC/DATA/SALES_PART_DESC");
                    tfsprice = trans.getNumberValue("TFSPRICE/DATA/ITEM3_LIST_PRICE");
                    tfspcurr = trans.getValue("TFSPCURR/DATA/PRICE_CURRENCY");
                    if (!(mgr.isEmpty(mgr.readValue("QTY"))) && !(mgr.isEmpty(mgr.readValue("PLANNED_HOUR"))))
                        tfpamoun = trans.getNumberValue("TFPRICEAM/DATA/PRICE_AMOUNT");
                    else
                        tfpamoun = mgr.readNumberValue("PRICE_AMOUNT");
                }
                else
                {
                    tfsalesp = mgr.readValue("SALES_PART");
                    tfspdesc = mgr.readValue("SALES_PART_DESC");
                    tfsprice = mgr.readNumberValue("SALES_PRICE");
                    tfspcurr = mgr.readValue("PRICE_CURRENCY");
                    tfpamoun = mgr.readNumberValue("PRICE_AMOUNT");
                }        
            }
            else
            {
                tfnote = mgr.readValue("NOTE");
                qty = mgr.readNumberValue("QTY");
            }

            trans.clear();
            strqty = mgr.getASPField("QTY").formatNumber(qty);
            stfsprice = mgr.getASPField("ITEM3_SALES_PRICE").formatNumber(tfsprice);
            stfpamoun = mgr.getASPField("PRICE_AMOUNT").formatNumber(tfpamoun);
            txt = (mgr.isEmpty(tfnote) ? "" : (tfnote))+ "^"+(mgr.isEmpty(strqty) ? "" : (strqty))+ "^" + (mgr.isEmpty(tfsalesp) ? "" : (tfsalesp))+"^"+(mgr.isEmpty(tfspdesc) ? "" : (tfspdesc))+ "^"+(mgr.isEmpty(stfsprice) ? "" : (stfsprice))+"^"+(mgr.isEmpty(tfspcurr) ? "" : (tfspcurr))+ "^"+(mgr.isEmpty(stfpamoun) ? "" : (stfpamoun))+ "^"; 
            mgr.responseWrite(txt);
        }
        else if ("ITEM3_SALES_PART".equals(val))
        {
            cmd = trans.addCustomFunction("TFSPDESC","Connect_Tools_Facilities_API.Get_Sales_Part_Desc","SALES_PART_DESC");
            cmd.addParameter("SALES_PART_SITE",mgr.readValue("SALES_PART_SITE"));
            cmd.addParameter("ITEM3_SALES_PART");           

            cmd = trans.addCustomFunction("TFSPRICE","Standard_Job_Tool_Facility_API.Calculate_Sales_Price","ITEM3_LIST_PRICE");
            cmd.addParameter("ITEM3_SALES_PART");
            cmd.addParameter("SALES_PART_SITE",mgr.readValue("SALES_PART_SITE"));

            cmd = trans.addCustomFunction("TFSPCURR","Standard_Job_Tool_Facility_API.Get_Sales_Currency","PRICE_CURRENCY");
            cmd.addReference("SALES_PART_SITE",mgr.readValue("SALES_PART_SITE"));

            if (!(mgr.isEmpty(mgr.readValue("QTY"))) && !(mgr.isEmpty(mgr.readValue("PLANNED_HOUR"))))
            {
                cmd = trans.addCustomFunction("TFPRICEAM","Standard_Job_Tool_Facility_API.Calculated_Price_Amount","PRICE_AMOUNT");
                cmd.addParameter("ITEM3_SALES_PART");
                cmd.addParameter("SALES_PART_SITE",mgr.readValue("SALES_PART_SITE"));
                cmd.addParameter("QTY",mgr.readValue("QTY"));           
                cmd.addParameter("PLANNED_HOUR",mgr.readValue("PLANNED_HOUR"));
                if (mgr.isEmpty(mgr.readValue("ITEM3_SALES_PRICE")))
                    cmd.addReference("ITEM3_LIST_PRICE","TFSPRICE/DATA");
                else
                    cmd.addParameter("ITEM3_SALES_PRICE",mgr.readValue("ITEM3_SALES_PRICE"));           
                cmd.addParameter("ITEM3_DISCOUNT",mgr.readValue("ITEM3_DISCOUNT"));
            }

            trans = mgr.validate(trans);

            tfspdesc = trans.getValue("TFSPDESC/DATA/SALES_PART_DESC");
            tfsprice = trans.getNumberValue("TFSPRICE/DATA/ITEM3_LIST_PRICE");
            tfspcurr = trans.getValue("TFSPCURR/DATA/PRICE_CURRENCY");

            if (!(mgr.isEmpty(mgr.readValue("QTY"))) && !(mgr.isEmpty(mgr.readValue("PLANNED_HOUR"))))
                tfpamoun = trans.getNumberValue("TFPRICEAM/DATA/PRICE_AMOUNT");
            else
                tfpamoun = mgr.readNumberValue("PRICE_AMOUNT");

            trans.clear();
            stfsprice = mgr.getASPField("SALES_PRICE").formatNumber(tfsprice);
            stfpamoun = mgr.getASPField("PRICE_AMOUNT").formatNumber(tfpamoun);

            txt = (mgr.isEmpty(tfspdesc) ? "" : (tfspdesc))+ "^"+(mgr.isEmpty(stfsprice) ? "" : (stfsprice))+"^"+(mgr.isEmpty(tfspcurr) ? "" : (tfspcurr))+ "^"+(mgr.isEmpty(stfpamoun) ? "" : (stfpamoun))+ "^"; 
            mgr.responseWrite(txt);
        }
        else if ("JOB_CATEGORY".equals(val))
        {
            trans.clear();

            cmd = trans.addCustomFunction("GETJCATDBVAL","JOB_CATEGORY_API.Encode","JOB_CATEGORY_DB");
            cmd.addParameter("JOB_CATEGORY");

            cmd = trans.addCustomFunction("GETCATDESC","STD_JOB_CATEGORY_API.Get_Description","ITEM4_DESCRIPTION");
            cmd.addReference("JOB_CATEGORY_DB","GETJCATDBVAL/DATA");
            cmd.addParameter("IDENTITY");

            trans = mgr.validate(trans);

            String sJobCatDb = trans.getValue("GETJCATDBVAL/DATA/JOB_CATEGORY_DB");
            String sJobCatDesc = trans.getValue("GETCATDESC/DATA/ITEM4_DESCRIPTION");

            txt = (mgr.isEmpty(sJobCatDb)?"":sJobCatDb) + "^" +
                  (mgr.isEmpty(sJobCatDesc)?"":sJobCatDesc) + "^";
            mgr.responseWrite(txt);
        }
        else if (("PERS_BUDGET_COST".equals(val)) ||  ("MAT_BUDGET_COST".equals(val)) || ("EXT_BUDGET_COST".equals(val)) ||  ("TF_BUDGET_COST".equals(val)) ||  ("EXP_BUDGET_COST".equals(val)) ||  ("FIX_BUDGET_COST".equals(val)))
        {
            double nPersCost = mgr.isEmpty(mgr.readValue("PERS_BUDGET_COST")) ? 0 : mgr.readNumberValue("PERS_BUDGET_COST");
            double nMatCost = mgr.isEmpty(mgr.readValue("MAT_BUDGET_COST")) ? 0 : mgr.readNumberValue("MAT_BUDGET_COST");
            double nToolCost = mgr.isEmpty(mgr.readValue("TF_BUDGET_COST")) ? 0 : mgr.readNumberValue("TF_BUDGET_COST");
            double nExtCost = mgr.isEmpty(mgr.readValue("EXT_BUDGET_COST")) ? 0 : mgr.readNumberValue("EXT_BUDGET_COST");
            double nExpCost = mgr.isEmpty(mgr.readValue("EXP_BUDGET_COST")) ? 0 : mgr.readNumberValue("EXP_BUDGET_COST");
            double nFixCost = mgr.isEmpty(mgr.readValue("FIX_BUDGET_COST")) ? 0 : mgr.readNumberValue("FIX_BUDGET_COST");

            double nTotCost =toDouble(nPersCost) + toDouble(nMatCost)+toDouble(nExtCost)+ toDouble(nToolCost)+ toDouble(nExpCost)+ toDouble(nFixCost);

            txt=(isNaN(nTotCost)? "" : mgr.formatNumber("TOT_BUDGET_COST",nTotCost)  )+ "^";

            mgr.responseWrite(txt);
        }
        else if (("QTY".equals(val)) || ("PLANNED_HOUR".equals(val)) || ("ITEM3_DISCOUNT".equals(val)) || ("ITEM3_SALES_PRICE".equals(val)))
        {
            String nCost = "";
            String nSalesPrice = mgr.readValue("ITEM3_SALES_PRICE");
            String nListPrice = mgr.readValue("ITEM3_LIST_PRICE");
            String nQty = mgr.readValue("QTY");
            String nPlanHrs = mgr.readValue("PLANNED_HOUR");
            String nDiscount = mgr.readValue("ITEM3_DISCOUNT");

            if (!mgr.isEmpty(mgr.readValue("ITEM3_SALES_PRICE")))
                nCost = nSalesPrice;
            else
                nCost = nListPrice;

            cmd = trans.addCustomFunction("GETAMOUNT","Standard_Job_Tool_Facility_API.Calculated_Price_Amount","PRICE_AMOUNT");
            cmd.addParameter("ITEM3_SALES_PART",mgr.readValue("ITEM3_SALES_PART"));
            cmd.addParameter("SALES_PART_SITE",mgr.readValue("SALES_PART_SITE"));
            cmd.addParameter("QTY",nQty);
            cmd.addParameter("PLANNED_HOUR",nPlanHrs);
            cmd.addParameter("QTY_DUMMY",nCost);
            cmd.addParameter("ITEM3_DISCOUNT",nDiscount);  

            trans = mgr.validate(trans);
            txt = trans.getValue("GETAMOUNT/DATA/PRICE_AMOUNT");
            mgr.responseWrite(txt);
        }
        else if ("HEAD_ORG_CODE".equals(val)) {

            String sCalendar;  
            String sCalDesc;
            
            cmd = trans.addCustomFunction("CALENDER", "Organization_API.Get_Calendar_Id", "CALENDAR_ID" );
            cmd.addParameter("CONTRACT");
            cmd.addParameter("HEAD_ORG_CODE");

            cmd = trans.addCustomFunction("CALDESC", "Work_Time_Calendar_API.Get_Description", "CALENDAR_DESCRIPTION" );
            cmd.addReference("CALENDAR_ID","CALENDER/DATA");

            trans=mgr.validate(trans);
            sCalendar =trans.getValue("CALENDER/DATA/CALENDAR_ID");
            sCalDesc = trans.getValue("CALDESC/DATA/CALENDAR_DESCRIPTION");
                        
            txt = (mgr.isEmpty (sCalendar) ? "" : (sCalendar)) + "^" + 
                  (mgr.isEmpty (sCalDesc) ? "" : sCalDesc) +"^";
            mgr.responseWrite(txt);
        }



        mgr.endResponse();
    }

    public double validatePriceAmtCrafts(double valFlag,String salesPartNo,double nSalesPrice,double nSalesPartListPrice,String invoiceType,double nDiscount,double nQtyToInvoice,double nPlannedQty,double nPlannedHrs,double nPlannedMen)
    {
        ASPManager mgr = getASPManager();
        trans.clear();

        cmd = trans.addCustomFunction("GETINVOICETYPE0","Work_Order_Invoice_Type_Api.Get_Client_Value(0)","INVOICETYPE");
        cmd = trans.addCustomFunction("GETINVOICETYPE1","Work_Order_Invoice_Type_Api.Get_Client_Value(1)","INVOICETYPE");
        cmd = trans.addCustomFunction("GETINVOICETYPE2","Work_Order_Invoice_Type_Api.Get_Client_Value(2)","INVOICETYPE");
        cmd = trans.addCustomFunction("GETINVOICETYPE3","Work_Order_Invoice_Type_Api.Get_Client_Value(3)","INVOICETYPE");

        trans = mgr.perform(trans);

        String sInvoiceTypeFixedLine  = trans.getValue("GETINVOICETYPE0/DATA/INVOICETYPE");
        String sInvoiceTypeAsReported  = trans.getValue("GETINVOICETYPE1/DATA/INVOICETYPE");
        String sInvoiceTypeMinQty  = trans.getValue("GETINVOICETYPE2/DATA/INVOICETYPE");
        String sInvoiceTypeMaxQty  = trans.getValue("GETINVOICETYPE3/DATA/INVOICETYPE");

        trans.clear();

        ASPTransactionBuffer testbuff = mgr.newASPTransactionBuffer();
        testbuff.addSecurityQuery("SALES_PART");
        mgr.perform(testbuff);
        ASPBuffer sec = testbuff.getSecurityInfo();
        double nPriceAmt = 0;
        double nTotalQty = 0;

        if (valFlag == 1)
        {
            if (nPlannedMen == 0)
            {
                nTotalQty = nPlannedHrs;
            }
            else
            {
                nTotalQty = nPlannedHrs * nPlannedMen;
            }
        }

        if (sec.itemExists("SALES_PART"))
        {
            trans.clear();

            if (!mgr.isEmpty(salesPartNo))
            {
                //Get the right price
                if (nSalesPrice == 0)
                    nSalesPrice = nSalesPartListPrice;

                //Calculate on right price

                if (invoiceType.equals(sInvoiceTypeFixedLine))
                    nPriceAmt = nSalesPrice * nQtyToInvoice;
                else if (invoiceType.equals(sInvoiceTypeAsReported))
                {
                    if (valFlag == 1)
                        nPriceAmt = nSalesPrice * nTotalQty;
                    else
                        nPriceAmt = nSalesPrice * nPlannedQty;
                }
                else if (invoiceType.equals(sInvoiceTypeMinQty))
                {
                    if (valFlag == 1)
                    {
                        if (nTotalQty > nQtyToInvoice)
                            nPriceAmt = nSalesPrice * nTotalQty;
                        else
                            nPriceAmt = nSalesPrice * nQtyToInvoice;
                    }
                    else
                    {
                        if (nPlannedQty > nQtyToInvoice)
                            nPriceAmt = nSalesPrice * nPlannedQty;
                        else
                            nPriceAmt = nSalesPrice * nQtyToInvoice;
                    }

                }
                else if (invoiceType.equals(sInvoiceTypeMaxQty))
                {
                    if (valFlag == 1)
                    {
                        if (nTotalQty < nQtyToInvoice)
                            nPriceAmt = nSalesPrice * nTotalQty;
                        else
                            nPriceAmt = nSalesPrice * nQtyToInvoice;
                    }
                    else
                    {
                        if (nPlannedQty < nQtyToInvoice)
                            nPriceAmt = nSalesPrice * nPlannedQty;
                        else
                            nPriceAmt = nSalesPrice * nQtyToInvoice;
                    }
                }

                if (nDiscount != 0)
                    nPriceAmt = nPriceAmt - ( nDiscount / 100 * nPriceAmt );
            }
        }
        return nPriceAmt;
    }


//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void newRow()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addEmptyCommand("HEAD","SEPARATE_STANDARD_JOB_API.New__",headblk);
        cmd.setOption("ACTION","PREPARE");

        cmd = trans.addCustomFunction("USER", "Fnd_Session_API.Get_Fnd_User", "USER_ID");
        cmd = trans.addCustomFunction( "GETCON","User_Default_API.Get_Contract","CONTRACT1" );

        cmd = trans.addCustomFunction( "GETCOM","Site_API.Get_Company","COMPANY1" );
        cmd.addReference("CONTRACT1","GETCON/DATA");

        cmd = trans.addCustomFunction("DEFUSER", "Person_Info_API.Get_Id_For_User", "CREATED_BY");
        cmd.addReference("USER_ID","USER/DATA"); 

        cmd = trans.addCustomFunction("CREBID","Company_Emp_API.Get_Max_Employee_Id","CREATED_BY_ID");
        cmd.addReference("COMPANY1","GETCOM/DATA");
        cmd.addReference("CREATED_BY","DEFUSER/DATA");

        cmd = trans.addCustomFunction( "CREDATE", "Maintenance_Site_Utility_API.Get_Site_Date(NULL)", "CREATION_DATE" );

        trans = mgr.perform(trans);

        String user = trans.getValue("USER/DATA/USER_ID");
        String creby = trans.getValue("DEFUSER/DATA/CREATED_BY");
        String creId = trans.getValue("CREBID/DATA/CREATED_BY_ID");
        String strTodate = trans.getValue("CREDATE/DATA/CREATION_DATE");

        strTodate = mgr.formatDate("CREATION_DATE",mgr.parseDate("CREATION_DATE",strTodate));


        data = trans.getBuffer("HEAD/DATA");

        data.setFieldItem("CREATED_BY",creby); 
        data.setFieldItem("CREATED_BY_ID",creId); 
        
        headset.addRow(data);
    }

//-----------------------------------------------------------------------------
//----------------------  CMDBAR EDIT-IT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void newRowITEM0()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        cmd = trans.addEmptyCommand("ITEM0","STANDARD_JOB_ROLE_API.New__",itemblk0);
        cmd.setParameter("ITEM0_STD_JOB_ID",mgr.readValue("STD_JOB_ID",""));
        cmd.setParameter("ITEM0_STD_JOB_REVISION",mgr.readValue("STD_JOB_REVISION",""));
        cmd.setParameter("ITEM0_STD_JOB_CONTRACT",mgr.readValue("CONTRACT",""));

        cmd.setOption("ACTION","PREPARE");

        trans = mgr.perform(trans);

        data = trans.getBuffer("ITEM0/DATA");

        data.setFieldItem("ITEM0_STD_JOB_ID",headset.getValue("STD_JOB_ID"));
        data.setFieldItem("ITEM0_STD_JOB_REVISION",headset.getValue("STD_JOB_REVISION"));
        data.setFieldItem("ITEM0_STD_JOB_CONTRACT",headset.getValue("CONTRACT"));
        data.setFieldItem("ORG_CONTRACT",headset.getValue("CONTRACT"));
        data.setFieldItem("ITEM0_CONTRACT",headset.getValue("CONTRACT"));
        data.setFieldItem("TEAM_CONTRACT",headset.getValue("CONTRACT"));

        itemset0.addRow(data);
    }


    public void newRowITEM1()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        cmd = trans.addEmptyCommand("ITEM1","STANDARD_JOB_SPARE_API.New__",itemblk1);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);
        data = trans.getBuffer("ITEM1/DATA");
        data.setFieldItem("ITEM1_STD_JOB_ID",headset.getValue("STD_JOB_ID"));
        data.setFieldItem("ITEM1_STD_JOB_REVISION",headset.getValue("STD_JOB_REVISION"));
        data.setFieldItem("ITEM1_STD_JOB_CONTRACT",headset.getValue("CONTRACT"));
        data.setFieldItem("ITEM1_CONTRACT",headset.getValue("CONTRACT"));
        itemset1.addRow(data);
    }


    public void newRowITEM2()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        cmd = trans.addEmptyCommand("ITEM2","STANDARD_JOB_PLANNING_API.New__",itemblk2);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);
        data = trans.getBuffer("ITEM2/DATA");
        data.setFieldItem("ITEM2_STD_JOB_ID",headset.getValue("STD_JOB_ID"));
        data.setFieldItem("ITEM2_STD_JOB_REVISION",headset.getValue("STD_JOB_REVISION"));
        data.setFieldItem("ITEM2_STD_JOB_CONTRACT",headset.getValue("CONTRACT"));
        data.setFieldItem("CATALOG_CONTRACT",headset.getValue("CONTRACT"));
        itemset2.addRow(data);
    }


    public void newRowITEM3()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        cmd = trans.addCustomFunction("ITEM3WO","Work_Order_Invoice_Type_API.Decode('AR')","WORK_ORDER_INVOICE_TYPE");
        cmd = trans.addEmptyCommand("ITEM3","STANDARD_JOB_TOOL_FACILITY_API.NEW__",itemblk3);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);
        data = trans.getBuffer("ITEM3/DATA");
        String work_order_inv_type=trans.getValue("ITEM3WO/DATA/WORK_ORDER_INVOICE_TYPE");
        data.setFieldItem("ITEM3_WORK_ORDER_INVOICE_TYPE",work_order_inv_type);
        data.setFieldItem("ITEM3_STD_JOB_ID",headset.getValue("STD_JOB_ID"));
        data.setFieldItem("ITEM3_STD_JOB_REVISION",headset.getValue("STD_JOB_REVISION"));
        data.setFieldItem("ITEM3_STD_JOB_CONTRACT",headset.getValue("CONTRACT"));
        itemset3.addRow(data);
    }

    public void newRowITEM4()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addEmptyCommand("ITEM4","STD_JOB_CATEGORY_API.NEW__",itemblk4);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);

        data = trans.getBuffer("ITEM4/DATA");
        data.setFieldItem("ITEM4_STD_JOB_ID", headset.getValue("STD_JOB_ID"));
        data.setFieldItem("ITEM4_STD_JOB_REVISION", headset.getValue("STD_JOB_REVISION"));
        data.setFieldItem("ITEM4_STD_JOB_CONTRACT", headset.getValue("CONTRACT"));

        itemset4.addRow(data);
    }

    public void newRowITEM5()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addEmptyCommand("ITEM5","STANDARD_JOB_PERMIT_API.NEW__",itemblk5);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);

        data = trans.getBuffer("ITEM5/DATA");
        data.setFieldItem("ITEM5_STD_JOB_ID", headset.getValue("STD_JOB_ID"));
        data.setFieldItem("ITEM5_STD_JOB_REVISION", headset.getValue("STD_JOB_REVISION"));
        data.setFieldItem("ITEM5_STD_JOB_CONTRACT", headset.getValue("CONTRACT"));
        data.setFieldItem("GENERATE", "TRUE");
        
        itemset5.addRow(data);
    }

    public void saveReturnITEM0()
    {
        ASPManager mgr = getASPManager();

        int currHead = headset.getCurrentRowNo();
        itemset0.changeRow();
        mgr.submit(trans);
        //itemlay0.setLayoutMode(itemlay0.MULTIROW_LAYOUT);
        headset.goTo(currHead);
        headset.refreshRow();
        okFindALLITEM0();
    }

    public void saveReturnITEM1()
    {
        ASPManager mgr = getASPManager();

        int currHead = headset.getCurrentRowNo();
        itemset1.changeRow();
        mgr.submit(trans);
        //itemlay1.setLayoutMode(itemlay1.MULTIROW_LAYOUT);
        headset.goTo(currHead);
        headset.refreshRow();
        okFindALLITEM1();
    }

    public void saveReturnITEM2()
    {
        ASPManager mgr = getASPManager();

        int currHead = headset.getCurrentRowNo();
        itemset2.changeRow();
        mgr.submit(trans);
        //itemlay2.setLayoutMode(itemlay2.MULTIROW_LAYOUT);
        headset.goTo(currHead);
        headset.refreshRow();
        okFindALLITEM2();
    }

    public void saveReturnITEM3()
    {
        ASPManager mgr = getASPManager();

        int currHead = headset.getCurrentRowNo();
        itemset3.changeRow();
        mgr.submit(trans);
        //itemlay3.setLayoutMode(itemlay3.MULTIROW_LAYOUT);
        headset.goTo(currHead);
        headset.refreshRow();
        okFindITEM3();
    }

    public boolean saveReturn()
    {
        ASPManager mgr = getASPManager();
        String temp = "";
        int length = 0;
        String pmStartUnit = "";
        String StartValue = "";
        int hislay = headlay.getHistoryMode();
        trans1.clear();
        boolean err = false;

        currrow = headset.getCurrentRowNo();
        headset.changeRow();

        if (headset.countRows()>0)
        {
            pmStartUnit = headset.getRow().getValue("PM_START_UNIT");
            StartValue = headset.getRow().getValue("START_VALUE");
        }

        if (!(mgr.isEmpty(pmStartUnit)))
        {
            String selMon = "select (Pm_Start_Unit_API.Get_Client_Value(2)) CLIENT_START2 from DUAL";
            String selWee = "select (Pm_Start_Unit_API.Get_Client_Value(1)) CLIENT_START1 from DUAL";
            String selDay = "select (Pm_Start_Unit_API.Get_Client_Value(0)) CLIENT_START from DUAL";

            trans1.addQuery("YYM",selMon);
            trans1.addQuery("YYW",selWee);
            trans1.addQuery("YYD",selDay);

            trans1 = mgr.perform(trans1);

            String clientValMon  = trans1.getValue("YYM/DATA/CLIENT_START2");
            String clientValwee  = trans1.getValue("YYW/DATA/CLIENT_START1");
            String clientValDay  = trans1.getValue("YYD/DATA/CLIENT_START");

            trans1.clear();

            if (clientValMon.equals(pmStartUnit))
            {
                temp = StartValue;
                length = temp.length();

                if (length != 4)
                {
                    err = true;
                }
                else
                {
                    String nTYvalue = temp.substring(0,2);
                    String nTMvalue = temp.substring(2,4);

                    String sel2 = "select to_number('"+StartValue+"     ') TEMPSTA from DUAL";
                    String sel3 = "select to_number('"+nTYvalue+"') TEMPTY from DUAL";
                    String sel4 = "select to_number('"+nTMvalue+"') TEMPTM from DUAL";

                    trans1.addQuery("XX2",sel2);
                    trans1.addQuery("XX3",sel3);
                    trans1.addQuery("XX4",sel4);
                    trans1 = mgr.perform(trans1);

                    double nTY = trans1.getNumberValue("XX3/DATA/TEMPTY"); 
                    double nTM = trans1.getNumberValue("XX4/DATA/TEMPTM");

                    if (( length != 4 ) ||  ( nTM < 1 ) ||  ( nTM > 12 ) ||  ( nTY < 0 ))
                    {
                        err = true;
                    }
                }   
            }
            else if (   clientValwee.equals(   pmStartUnit))
            {
                temp = mgr.readValue("START_VALUE");
                length = temp.length();

                cmd = trans1.addCustomFunction("STARTDA","Pm_Calendar_API.Get_Date","START_DATE");
                cmd.addParameter("START_VALUE",StartValue);
                cmd.addParameter("INDEX1","1");

                trans1=mgr.perform(trans1);  
                String startDate = trans1.getValue("STARTDA/DATA/START_DATE");

                if ((mgr.isEmpty(startDate)))
                {
                    err = true;
                }
            }
            else if (clientValDay.equals(pmStartUnit))
            {
                ASPBuffer buf = mgr.newASPBuffer();
                temp = StartValue;

                try
                {
                    buf.addFieldItem("START_DATE",StartValue);
                }
                catch (Throwable any)
                {
                    err = true;
                }
            }
        }

        if (err)
        {
            dateMask = mgr.getFormatMask("Date",true);
            mgr.showAlert(mgr.translate("PCMWPMACTIONINFO1: Invalid Start Value ")+ temp+mgr.translate("PCMWPMACTIONINFO2: . The Format should be  ")+ dateMask+mgr.translate("PCMWPMACTIONINFO3:   for Start Unit 'Day' and YYMM or YYWW for Start Unit 'Month' or 'Week'."));           
            headlay.setLayoutMode( Integer.valueOf(laymode).intValue() );
            return false;
        }
        else
        {
            trans.clear();
            mgr.submit(trans);

            headset.goTo(currrow); 
            headset.refreshRow();
            return true;
        } 

    }

    public void saveNew()
    {
        currrow = headset.getCurrentRowNo();
        trans.clear();

        if (saveReturn())
            newRow();
    }

    public void duplicateITEM0()
	 {
       ASPManager mgr = getASPManager();
       ASPBuffer  newRowBuff = null;
		 if (itemlay0.isMultirowLayout())
		    itemset0.goTo(itemset0.getRowSelected());
		 
       newRowBuff = itemset0.getRow();
       // sets the RO_NO to null as it'll be generated by the server. 
       newRowBuff.setValue("ROW_NO",null);
      
       itemset0.addRow(newRowBuff);
       itemlay0.setLayoutMode(itemlay0.NEW_LAYOUT);      
	 }
//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------
    
    public void remove()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());

        headset.setRemoved();
        mgr.submit(trans);
    }

    public void removeITEM0()
    {
        ASPManager mgr = getASPManager();

        if (itemlay0.isMultirowLayout())
        {
            itemset0.goTo(itemset0.getRowSelected());
        }
        else
        {
            itemset0.selectRow();
        }

        int currHead = headset.getCurrentRowNo();

        if(itemlay0.isMultirowLayout())
        {
            itemset0.setSelectedRowsRemoved();
        }
        else
        {
            itemset0.setRemoved();
        }
        mgr.submit(trans);
        headset.goTo(currHead);
        headset.refreshRow();
        okFindALLITEM0();  
    }

    public void removeITEM1()
    {
        ASPManager mgr = getASPManager();

        if (itemlay1.isMultirowLayout())
        {
            itemset1.goTo(itemset1.getRowSelected());
        }
        else
        {
            itemset1.selectRow();
        }

        int currHead = headset.getCurrentRowNo();

        if(itemlay1.isMultirowLayout())
        {
            itemset1.setSelectedRowsRemoved();
        }
        else
        {
            itemset1.setRemoved();
        }
        mgr.submit(trans);
        headset.goTo(currHead);
        headset.refreshRow();
        okFindALLITEM1();  
    }

    public void removeITEM2()
    {
        ASPManager mgr = getASPManager();

        if (itemlay2.isMultirowLayout())
        {
            itemset2.goTo(itemset2.getRowSelected());
        }
        else
        {
            itemset2.selectRow();
        }

        int currHead = headset.getCurrentRowNo();
        if(itemlay2.isMultirowLayout())
        {
            itemset2.setSelectedRowsRemoved();
        }
        else
        {
            itemset2.setRemoved();
        }
        mgr.submit(trans);
        headset.goTo(currHead);
        headset.refreshRow();
        okFindALLITEM2();  
    }

    public void removeITEM3()
    {
        ASPManager mgr = getASPManager();

        if (itemlay3.isMultirowLayout())
        {
            itemset3.goTo(itemset3.getRowSelected());
        }
        else
        {
            itemset3.selectRow();
        }

        int currHead = headset.getCurrentRowNo();
        if(itemlay3.isMultirowLayout())
        {
            itemset3.setSelectedRowsRemoved();
        }
        else
        {
            itemset3.setRemoved();
        }
        mgr.submit(trans);
        headset.goTo(currHead);
        headset.refreshRow();
        okFindITEM3();  
    }

    public void countFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CONTRACT) FROM dual)");
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);

        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();
    }


    public void okFind()
    {
        ASPManager mgr = getASPManager();
        trans.clear();

        if (itemset0.countRows() > 0)
            itemset0.clear();

        if (itemset1.countRows() > 0)
            itemset1.clear();

        q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CONTRACT) FROM dual)");
        q.includeMeta("ALL");

        if (mgr.dataTransfered())
            q.addOrCondition(mgr.getTransferedData());

        mgr.querySubmit(trans, headblk);

        //eval(headset.syncItemSets());

        if (headset.countRows() == 1)
        {
            okFindITEM0();
            okFindITEM1();
            okFindITEM2();
            okFindITEM3();
            okFindITEM4();
            okFindITEM5();
            okFindITEM6();

            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        }
        else if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWSEPARATESTANDARDJOB2NODATA: No data found."));
            headset.clear();
        }

        qrystr = mgr.createSearchURL(headblk);
    }

//-----------------------------------------------------------------------------
//-----------------------  CMDBAR SEARCH-IT FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

    public void okFindITEM0()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();

        itemset0.clear();
        q = trans.addQuery(itemblk0);
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM0_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));
        q.setOrderByClause("ROW_NO");
        q.includeMeta("ALL");

        mgr.submit(trans);

        if (mgr.commandBarActivated())
        {
            if (itemset0.countRows() == 0 && "ITEM0.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWSEPARATESTANDARDJOB2NODATA: No data found."));
                itemset0.clear();
            }
        }
        setPriceAmount0();
        headset.goTo(headrowno);
    }

    public void okFindALLITEM0()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();

        itemset0.clear();
        q = trans.addEmptyQuery(itemblk0);
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM0_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));
        q.setOrderByClause("ROW_NO");
        q.includeMeta("ALL");

        mgr.submit(trans);

        if (mgr.commandBarActivated())
        {
            if (itemset0.countRows() == 0 && "ITEM0.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWSEPARATESTANDARDJOB2NODATA: No data found."));
                itemset0.clear();
            }
        }
        setPriceAmount0();
        headset.goTo(headrowno);
    } 

    public void okFindITEM1()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();

        itemset1.clear();
        q = trans.addQuery(itemblk1);
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM1_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));

        q.includeMeta("ALL");

        mgr.submit(trans);

        if (mgr.commandBarActivated())
        {
            if (itemset1.countRows() == 0 && "ITEM1.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWSEPARATESTANDARDJOB2NODATA: No data found."));
                itemset1.clear();
            }
        }
        setPriceAmount1();
        headset.goTo(headrowno);
    }

    public void okFindALLITEM1()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();

        itemset1.clear();
        q = trans.addEmptyQuery(itemblk1);
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM1_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));

        q.includeMeta("ALL");

        mgr.submit(trans);

        if (mgr.commandBarActivated())
        {
            if (itemset1.countRows() == 0 && "ITEM1.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWSEPARATESTANDARDJOB2NODATA: No data found."));
                itemset1.clear();
            }
        }
        setPriceAmount1();
        headset.goTo(headrowno);
    } 

    public void okFindITEM2()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();
        itemset2.clear(); 
        q = trans.addQuery(itemblk2);
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM2_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));
        q.includeMeta("ALL");

        mgr.submit(trans);

        if (mgr.commandBarActivated())
        {
            if (itemset2.countRows() == 0 && "ITEM2.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWSEPARATESTANDARDJOB2NODATA: No data found."));
                itemset2.clear();
            }
        }

        setPriceAmount2();
        headset.goTo(headrowno);
    }

    public void okFindALLITEM2()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();
        itemset2.clear(); 
        q = trans.addEmptyQuery(itemblk2);
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM2_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));
        q.includeMeta("ALL");

        mgr.submit(trans);

        if (mgr.commandBarActivated())
        {
            if (itemset2.countRows() == 0 && "ITEM2.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWSEPARATESTANDARDJOB2NODATA: No data found."));
                itemset2.clear();
            }
        }

        setPriceAmount2();
        headset.goTo(headrowno);
    }


    public void okFindITEM3()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();
        itemset3.clear(); 
        q = trans.addEmptyQuery(itemblk3);
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM3_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));
        q.includeMeta("ALL");

        mgr.submit(trans);

        if (mgr.commandBarActivated())
        {
            if (itemset3.countRows() == 0 && "ITEM3.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWSEPARATESTANDARDJOB2NODATA: No data found."));
                itemset3.clear();
            }
        }

        headset.goTo(headrowno);
    }

    public void okFindITEM4()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();
        itemset4.clear(); 

        q = trans.addEmptyQuery(itemblk4);
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM4_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));
        q.includeMeta("ALL");

        mgr.submit(trans);

        headset.goTo(headrowno);
    }

    public void okFindITEM5()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();
        itemset5.clear(); 

        q = trans.addEmptyQuery(itemblk5);
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM5_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));
        q.includeMeta("ALL");

        mgr.submit(trans);

        headset.goTo(headrowno);
    }

    public void okFindITEM6()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        currrow = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk6);
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM6_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));
        q.includeMeta("ALL");

        mgr.submit(trans);

        headset.goTo(currrow);
    }

    public void okFindITEM7()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();
        itemset7.clear(); 

        q = trans.addEmptyQuery(itemblk7);
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM7_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));
        q.includeMeta("ALL");

        mgr.submit(trans);

        headset.goTo(headrowno);
    }


    public void countFindITEM0()
    {
        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();
        q = trans.addQuery(itemblk0);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM0_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));
        mgr.submit(trans);

        itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
        itemset0.clear();
        headset.goTo(currrow);   
    }


    public void countFindITEM1()
    {
        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();
        q = trans.addQuery(itemblk1);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM1_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));
        mgr.submit(trans);

        itemlay1.setCountValue(toInt(itemset1.getRow().getValue("N")));
        itemset1.clear();
        headset.goTo(currrow);   
    }


    public void countFindITEM2()
    {
        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();
        q = trans.addQuery(itemblk2);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM2_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));
        mgr.submit(trans);

        itemlay2.setCountValue(toInt(itemset2.getRow().getValue("N")));
        itemset2.clear();
        headset.goTo(currrow);   
    }


    public void countFindITEM3()
    {
        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();
        q = trans.addQuery(itemblk3);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM3_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));
        mgr.submit(trans);

        itemlay3.setCountValue(toInt(itemset3.getRow().getValue("N")));
        itemset3.clear();
        headset.goTo(currrow);   
    }

    public void countFindITEM4()
    {
        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk4);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM4_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));
        mgr.submit(trans);

        itemlay4.setCountValue(toInt(itemset4.getRow().getValue("N")));
        itemset4.clear();
        headset.goTo(currrow);   
    }

    public void countFindITEM5()
    {
        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk5);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM5_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));
        mgr.submit(trans);

        itemlay5.setCountValue(toInt(itemset5.getRow().getValue("N")));
        itemset5.clear();
        headset.goTo(currrow);   
    }

    public void countFindITEM6()
    {
        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk6);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM6_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));
        mgr.submit(trans);

        itemlay6.setCountValue(toInt(itemset6.getRow().getValue("N")));
        itemset6.clear();
        headset.goTo(currrow);   
    }

    public void countFindITEM7()
    {
        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk7);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM7_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));
        mgr.submit(trans);

        itemlay7.setCountValue(toInt(itemset7.getRow().getValue("N")));
        itemset7.clear();
        headset.goTo(currrow);   
    }

    public void setPriceAmount0()
    {
        ASPManager mgr = getASPManager();
        String qtyInvStr = null;
        String salesPriceStr = null;
        String salesLPriceStr = null;
        String planMenStr = null;
        String planHoStr = null;
        double qtyInv = 0;
        double salesPrice = 0;
        double salesLPrice = 0;
        double planMen = 0;
        double planHo = 0;
        double planQty = 0;
        double amtCost = 0;

        if (itemset0.countRows() > 0)
        {
            amtCost = 0;
            itemset0.first();
            for (int i=1;i<=itemset0.countRows();i++)
            {
                amtCost = 0;
                row = itemset0.getRow();
                qtyInvStr = itemset0.getRow().getValue("QTY_TO_INVOICE");
                if (!mgr.isEmpty(qtyInvStr))
                    qtyInv = toDouble(itemset0.getRow().getValue("QTY_TO_INVOICE"));
                else
                    qtyInv = 0;

                salesPriceStr = itemset0.getRow().getValue("SALES_PRICE");
                if (!mgr.isEmpty(salesPriceStr))
                    salesPrice = toDouble(itemset0.getRow().getValue("SALES_PRICE"));
                else
                    salesPrice = 0;

                salesLPriceStr = itemset0.getRow().getValue("SLSPRTLSTPRICE");
                if (!mgr.isEmpty(salesLPriceStr))
                    salesLPrice = toDouble(itemset0.getRow().getValue("SLSPRTLSTPRICE"));
                else
                    salesLPrice = 0;

                planHoStr = itemset0.getRow().getValue("PLANNED_HOURS");
                if (!mgr.isEmpty(planHoStr))
                    planHo = toDouble(itemset0.getRow().getValue("PLANNED_HOURS"));
                else
                    planHo = 0;

                planMenStr = itemset0.getRow().getValue("PLANNED_MEN");
                if (!mgr.isEmpty(planMenStr))
                    planMen = toDouble(itemset0.getRow().getValue("PLANNED_MEN"));
                else
                    planMen = 0;

                if (!mgr.isEmpty(itemset0.getRow().getValue("QTY_TO_INVOICE")))
                {
                    if (!mgr.isEmpty(itemset0.getRow().getValue("SALES_PRICE")))

                        amtCost = qtyInv *salesPrice;
                    else
                        amtCost = qtyInv * salesLPrice;   
                }
                else
                {
                    if (!mgr.isEmpty(itemset0.getRow().getValue("SALES_PRICE")))
                    {
                        if (mgr.isEmpty(itemset0.getRow().getValue("PLANNED_MEN")))
                            amtCost = planHo * salesPrice;
                        else
                            amtCost = planHo * salesPrice * planMen;
                    }

                    else
                    {
                        if (mgr.isEmpty(itemset0.getRow().getValue("PLANNED_MEN")))
                            amtCost = planHo * salesLPrice;
                        else
                            amtCost = planHo * salesLPrice * planMen;
                    }              
                }    
                double disco = 0;
                String discoStr = (itemset0.getRow().getValue("DISCOUNT"));
                if (mgr.isEmpty(discoStr))
                    disco=0;
                if (!mgr.isEmpty(itemset0.getRow().getValue("DISCOUNT")))
                {
                    amtCost = amtCost - (disco/100 * amtCost);
                }
                if (isNaN(amtCost))
                    amtCost=0;

                row.setNumberValue("ITEM0_PRICE_AMOUNT",amtCost);
                itemset0.setRow(row);
                itemset0.next();
            }
            itemset0.first();
        }

    }

    public void setPriceAmount1()
    {
        ASPManager mgr = getASPManager();
        double qtyInv = 0;
        double salesPrice = 0;
        double salesLPrice = 0;
        double planQty = 0;
        double amtCost1 = 0;
        double disco1=0;

        if (itemset1.countRows() > 0)
        {
            amtCost1 = 0;
            itemset1.first();
            for (int i=1;i<=itemset1.countRows();i++)
            {
                amtCost1 = 0;
                row = itemset1.getRow(); 
                qtyInv = itemset1.getNumberValue("QTY_TO_INVOICE");
                if (isNaN(qtyInv))
                    qtyInv = 0;

                salesPrice = itemset1.getNumberValue("SALES_PRICE");
                if (isNaN(salesPrice))
                    salesPrice = 0;

                salesLPrice = itemset1.getNumberValue("ITEM1PRTLSTPRC");
                if (isNaN(salesLPrice))
                    salesLPrice = 0;

                planQty = itemset1.getNumberValue("PLANNED_QTY");
                if (isNaN(planQty))
                    planQty = 0;

                if (!mgr.isEmpty(itemset1.getRow().getFieldValue("ITEM1_QTY_TO_INVOICE")))
                {
                    if (!mgr.isEmpty(itemset1.getRow().getFieldValue("ITEM1_SALES_PRICE")))
                        amtCost1 = qtyInv * salesPrice;
                    else
                        amtCost1    = qtyInv * salesLPrice;   
                }
                else
                {
                    if (!mgr.isEmpty(itemset1.getRow().getFieldValue("ITEM1_SALES_PRICE")))
                        amtCost1 = planQty * salesPrice;
                    else
                        amtCost1    = planQty * salesLPrice;
                }    
                disco1 = (itemset1.getNumberValue("DISCOUNT"));
                if (isNaN(disco1))
                    disco1=0;

                if (!mgr.isEmpty(itemset1.getRow().getFieldValue("ITEM1_DISCOUNT")))
                    amtCost1 = amtCost1 - (disco1/100 * amtCost1);

                if (isNaN(amtCost1))
                    amtCost1=0;

                if (mgr.isEmpty(itemset1.getRow().getValue("PARTDESCRIPTION")))
                {
                    trans.clear();

                    cmd = trans.addCustomCommand("DESCFORPART"+i,"PM_ACTION_SPARE_PART_API.Description_For_Part");
                    cmd.addParameter("PART_NO",itemset1.getRow().getValue("PART_NO"));
                    cmd.addParameter("CONTRACT",itemset1.getRow().getValue("CONTRACT"));
                    cmd.addParameter("PARTDESCRIPTION",itemset1.getRow().getValue("PARTDESCRIPTION"));

                    trans = mgr.perform(trans);
                    row.setValue("PARTDESCRIPTION",trans.getValue("DESCFORPART"+i+"/DATA/PARTDESCRIPTION"));
                }

                row.setNumberValue("ITEM1_PRICE_AMOUNT",amtCost1);
                itemset1.setRow(row);
                itemset1.next();
            }
            itemset1.first();
        }
    }

    public void setPriceAmount2()
    {
        ASPManager mgr = getASPManager();

        cmd = trans.addCustomFunction("GETINVOICETYPE0","Work_Order_Invoice_Type_Api.Get_Client_Value(0)","INVOICETYPE");
        cmd = trans.addCustomFunction("GETINVOICETYPE1","Work_Order_Invoice_Type_Api.Get_Client_Value(1)","INVOICETYPE");
        cmd = trans.addCustomFunction("GETINVOICETYPE2","Work_Order_Invoice_Type_Api.Get_Client_Value(2)","INVOICETYPE");
        cmd = trans.addCustomFunction("GETINVOICETYPE3","Work_Order_Invoice_Type_Api.Get_Client_Value(3)","INVOICETYPE");

        trans = mgr.perform(trans);

        String sInvoiceTypeFixedLine  = trans.getValue("GETINVOICETYPE0/DATA/INVOICETYPE");
        String sInvoiceTypeAsReported  = trans.getValue("GETINVOICETYPE1/DATA/INVOICETYPE");
        String sInvoiceTypeMinQty  = trans.getValue("GETINVOICETYPE2/DATA/INVOICETYPE");
        String sInvoiceTypeMaxQty  = trans.getValue("GETINVOICETYPE3/DATA/INVOICETYPE");

        trans.clear();

        ASPTransactionBuffer testbuff = mgr.newASPTransactionBuffer();
        testbuff.addSecurityQuery("SALES_PART");
        mgr.perform(testbuff);
        ASPBuffer sec = testbuff.getSecurityInfo();

        if (sec.itemExists("SALES_PART"))
        {
            trans.clear();
            int n = itemset2.countRows();

            itemset2.first();
            for (int i=1;i<=n;i++)
            {
                double nPriceAmt = 0;
                String salesPartNo = itemset2.getRow().getValue("CATALOG_NO");

                if (!mgr.isEmpty(salesPartNo))
                {
                    //Get the right price
                    double nSalesPrice = itemset2.getRow().getNumberValue("SALES_PRICE");
                    if (isNaN(nSalesPrice))
                        nSalesPrice = 0;

                    if (nSalesPrice == 0)
                    {
                        double nSalesPartListPrice = itemset2.getRow().getNumberValue("SALES_PRICE");
                        if (isNaN(nSalesPartListPrice))
                            nSalesPartListPrice = 0;

                        nSalesPrice = nSalesPartListPrice;
                    }

                    //Calculate on right price
                    String invoiceType = itemset2.getRow().getValue("WORK_ORDER_INVOICE_TYPE");

                    double nDiscount = itemset2.getRow().getNumberValue("DISCOUNT");
                    if (isNaN(nDiscount))
                        nDiscount = 0;

                    double nQtyToInvoice = itemset2.getRow().getNumberValue("QTY_TO_INVOICE");
                    if (isNaN(nQtyToInvoice))
                        nQtyToInvoice = 0;

                    double nPlannedQty = itemset2.getRow().getNumberValue("QUANTITY");
                    if (isNaN(nPlannedQty))
                        nPlannedQty = 0;

                    if (invoiceType.equals(sInvoiceTypeFixedLine))
                        nPriceAmt = nSalesPrice * nQtyToInvoice;
                    else if (invoiceType.equals(sInvoiceTypeAsReported))
                        nPriceAmt = nSalesPrice * nPlannedQty;
                    else if (invoiceType.equals(sInvoiceTypeMinQty))
                    {
                        if (nPlannedQty > nQtyToInvoice)
                            nPriceAmt = nSalesPrice * nPlannedQty;
                        else
                            nPriceAmt = nSalesPrice * nQtyToInvoice;
                    }
                    else if (invoiceType.equals(sInvoiceTypeMaxQty))
                    {
                        if (nPlannedQty < nQtyToInvoice)
                            nPriceAmt = nSalesPrice * nPlannedQty;
                        else
                            nPriceAmt = nSalesPrice * nQtyToInvoice;
                    }

                    if (nDiscount != 0)
                        nPriceAmt = nPriceAmt - ( nDiscount / 100 * nPriceAmt );


                    if (isNaN(nPriceAmt))
                        nPriceAmt = 0;

                    r = itemset2.getRow();
                    r.setNumberValue("ITEM2_PRICE_AMOUNT",nPriceAmt);
                    itemset2.setRow(r);
                }
                itemset2.next();
            }
        }
    }
//-----------------------------------------------------------------------------
//------------------------  HEADBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    //Web Alignment - Multirow Action
    private String createTransferUrl(String url, ASPBuffer object)
    {
        ASPManager mgr = getASPManager();

        try
        {
            String pkg = mgr.pack(object,1900 - url.length());
            char sep = url.indexOf('?')>0 ? '&' : '?';
            urlString = url + sep + "__TRANSFER=" + pkg ;
            return urlString;
        }
        catch (Throwable any)
        {
            return null;
        }
    }
    
    public void none()
    {
        ASPManager mgr = getASPManager();

        mgr.showAlert(mgr.translate("PCMWSEPARATESTANDARDJOB2NONE: No RMB method has been selected."));
    }

//-----------------------------------------------------------------------------
//------------------------  ITEMBAR1 CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void qtyOnHand()
    {
        ASPManager mgr = getASPManager();
        //Web Alignment - Enable Multirow Action
        int count = 0;
        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;

        bOpenNewWindow = true;

        current_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",current_url);  

        if (itemlay1.isMultirowLayout())
        {
            itemset1.storeSelections();
            itemset1.setFilterOn();
            count = itemset1.countSelectedRows();
        }
        else
        {
            itemset1.unselectRows();
            count = 1;
        }

        transferBuffer = mgr.newASPBuffer();

        for (int i = 0; i < count; i++)
        {
            rowBuff = mgr.newASPBuffer();

            if (i == 0)
            {
                rowBuff.addItem("PART_NO", itemset1.getRow().getValue("PART_NO"));
                rowBuff.addItem("CONTRACT", itemset1.getRow().getValue("SPARE_CONTRACT"));
            }
            else
            {
                rowBuff.addItem(null, itemset1.getRow().getValue("PART_NO"));
                rowBuff.addItem(null, itemset1.getRow().getValue("CONTRACT"));
            }

            transferBuffer.addBuffer("DATA", rowBuff);

            if (itemlay1.isMultirowLayout())
                itemset1.next();
        }

        if (itemlay1.isMultirowLayout())
            itemset1.setFilterOff();

        //Web Alignment - simplify code for RMBs
        urlString = createTransferUrl("../invenw/InventoryPartInventoryPartCurrentOnHand.page", transferBuffer);

        newWinHandle = "SPartNo"; 
        //
    }

    public void availibilityDet()
    {
        ASPManager mgr = getASPManager();

        //Web Alignment - Enable Multirow Action
        int count = 0;
        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;

        bOpenNewWindow = true;

        current_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",current_url);  

        if (itemlay1.isMultirowLayout())
        {
            itemset1.storeSelections();
            itemset1.setFilterOn();
            count = itemset1.countSelectedRows();
        }
        else
        {
            itemset1.unselectRows();
            count = 1;
        }

        transferBuffer = mgr.newASPBuffer();

        for (int i = 0; i < count; i++)
        {
            rowBuff = mgr.newASPBuffer();

            if (i == 0)
            {
                rowBuff.addItem("PART_NO", itemset1.getValue("PART_NO"));
                rowBuff.addItem("CONTRACT", itemset1.getValue("SPARE_CONTRACT"));
                rowBuff.addItem("PROJECT_ID", "*");
                rowBuff.addItem("CONFIGURATION_ID", "*");
            }
            else
            {
                rowBuff.addItem(null, itemset1.getValue("PART_NO"));
                rowBuff.addItem(null, itemset1.getValue("SPARE_CONTRACT"));
                rowBuff.addItem(null, "*");
                rowBuff.addItem(null, "*");
            }

            transferBuffer.addBuffer("DATA", rowBuff);

            if (itemlay1.isMultirowLayout())
                itemset1.next();
        }

        if (itemlay1.isMultirowLayout())
            itemset1.setFilterOff();

        //Web Alignment - simplify code for RMBs
        urlString = createTransferUrl("../invenw/InventoryPartAvailabilityPlanningQry.page", transferBuffer);

        newWinHandle = "AvailabilityDet"; 
        
    }

    public void inventoryPart()
    {
        ASPManager mgr = getASPManager();

        //Web Alignment - Enable Multirow Action
        int count = 0;
        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;

        bOpenNewWindow = true;

        current_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",current_url);  

        if (itemlay1.isMultirowLayout())
        {
            itemset1.storeSelections();
            itemset1.setFilterOn();
            count = itemset1.countSelectedRows();
        }
        else
        {
            itemset1.unselectRows();
            count = 1;
        }

        transferBuffer = mgr.newASPBuffer();

        for (int i = 0; i < count; i++)
        {
            rowBuff = mgr.newASPBuffer();

            if (i == 0)
            {
                rowBuff.addItem("PART_NO", itemset1.getValue("PART_NO"));
                rowBuff.addItem("CONTRACT", itemset1.getValue("SPARE_CONTRACT"));
            }
            else
            {
                rowBuff.addItem(null, itemset1.getValue("PART_NO"));
                rowBuff.addItem(null, itemset1.getValue("SPARE_CONTRACT"));
            }

            transferBuffer.addBuffer("DATA", rowBuff);

            if (itemlay1.isMultirowLayout())
                itemset1.next();
        }

        if (itemlay1.isMultirowLayout())
            itemset1.setFilterOff();

        //Web Alignment - simplify code for RMBs
        urlString = createTransferUrl("../invenw/InventoryPart.page", transferBuffer);

        newWinHandle = "InventPart"; 
        
    }

    public void suppPerPart()
    {
        ASPManager mgr = getASPManager();

        //Web Alignment - Enable Multirow Action
        int count = 0;
        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;

        bOpenNewWindow = true;

        current_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",current_url);  

        if (itemlay1.isMultirowLayout())
        {
            itemset1.storeSelections();
            itemset1.setFilterOn();
            count = itemset1.countSelectedRows();
        }
        else
        {
            itemset1.unselectRows();
            count = 1;
        }

        transferBuffer = mgr.newASPBuffer();

        for (int i = 0; i < count; i++)
        {
            rowBuff = mgr.newASPBuffer();

            if (i == 0)
            {
                rowBuff.addItem("PART_NO", itemset1.getValue("PART_NO"));
                rowBuff.addItem("CONTRACT", itemset1.getValue("SPARE_CONTRACT"));
            }
            else
            {
                rowBuff.addItem(null, itemset1.getValue("PART_NO"));
                rowBuff.addItem(null, itemset1.getValue("SPARE_CONTRACT"));
            }

            transferBuffer.addBuffer("DATA", rowBuff);

            if (itemlay1.isMultirowLayout())
                itemset1.next();
        }

        if (itemlay1.isMultirowLayout())
            itemset1.setFilterOff();

        //Web Alignment - simplify code for RMBs
        urlString = createTransferUrl("../purchw/PurchasePartSupplier.page", transferBuffer);

        newWinHandle = "SupPerPart"; 
        
    }

//-----------------------------------------------------------------------------
//----------------------------  CUSTOM FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

    public void setStdTextOnlyVal()
    {
        ASPManager mgr = getASPManager();

        if ((headset.getRow().getValue("CBROLES")) == "TRUE" || (headset.getRow().getValue("CBMATERIALS")) == "TRUE")
            mgr.getASPField("STD_TEXT_ONLY").setReadOnly();
    }

    public void GetPriceAmount1()
    {
        double plHours1,plmen1,lstprc1,salesPrc1,qtyInv1 = 0;

        int ref;
        txt=null;
        isSecure = new String[2];
        ASPManager mgr = getASPManager();

        if (checksec("SALES_PART_API.Get_List_Price",1,isSecure))
        {
            cmd = trans.addCustomFunction("GEN2", "SALES_PART_API.Get_List_Price", "SLSPRTLSTPRICE");
            cmd.addParameter("ITEM0_CONTRACT"); 
            cmd.addParameter("CATALOG_NO"); 
        }

        trans = mgr.perform(trans);

        ref = 0;
        double AmountCost1 = 0;

        qtyInv1 = mgr.readNumberValue("QTY_TO_INVOICE");
        salesPrc1 = mgr.readNumberValue("SALES_PRICE");

        if (!isNaN(qtyInv1))
        {
            if (!isNaN(salesPrc1))
                AmountCost1 = qtyInv1 * salesPrc1;
            else
            {
                if (isSecure[ref += 1] =="true")
                {
                    lstprc1 = trans.getNumberValue("GEN2/DATA/SLSPRTLSTPRICE");

                    AmountCost1 = qtyInv1 * lstprc1;
                }
                else
                    lstprc1 = NOT_A_NUMBER;
            }   
        }
        else
        {
            if (!isNaN(salesPrc1))
            {
                plmen1 = mgr.readNumberValue("PLANNED_MEN");
                plHours1 = mgr.readNumberValue("PLANNED_HOURS");

                if (isNaN(plmen1))
                    AmountCost1 = plHours1 * salesPrc1;
                else
                    AmountCost1 = plmen1 * plHours1 * salesPrc1;
            }
            else
            {
                if (isSecure[ref += 1] =="true")
                {
                    lstprc1 = trans.getNumberValue("GEN2/DATA/SLSPRTLSTPRICE");

                    plmen1 = mgr.readNumberValue("PLANNED_MEN");
                    plHours1 = mgr.readNumberValue("PLANNED_HOURS");

                    if (isNaN(plmen1))
                        AmountCost1 = plHours1 * lstprc1;
                    else
                        AmountCost1 = plmen1 * plHours1 * lstprc1;
                }
                else
                    lstprc1 = NOT_A_NUMBER;
            }
        }  

        double disc1 = 0;
        disc1 = mgr.readNumberValue("DISCOUNT");

        if (!isNaN(disc1))
            AmountCost1 = AmountCost1 - (disc1/100 * AmountCost1);
        String AmountCost1Str = mgr.getASPField("ITEM0_PRICE_AMOUNT").formatNumber(AmountCost1);
        txt = (mgr.isEmpty(AmountCost1Str) ? "" : AmountCost1Str)+ "^";
    }

    public void copyStdJobId()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();
        int currrow = headset.getCurrentRowNo();
        ctx.setGlobal("CURRROWGLOBAL",String.valueOf(currrow));

        //Web Alignment - simplify code for RMBs
        bOpenNewWindow = true;
        urlString = "CopyStandardJobDlg.page?STD_JOB_ID="+mgr.URLEncode(headset.getRow().getValue("STD_JOB_ID"))+
                    "&STD_JOB_REVISION="+mgr.URLEncode(headset.getRow().getValue("STD_JOB_REVISION"))+
                    "&CONTRACT="+mgr.URLEncode(headset.getRow().getValue("CONTRACT"))+
                    "&COMPANY="+mgr.URLEncode(headset.getRow().getValue("COMPANY"))+
                    "&CREATED_BY="+headset.getRow().getValue("CREATED_BY");
        newWinHandle = "CopyStdJobId";
        newWinHeight = 460;
        newWinWidth = 400;
        
    }

    public void createNewRevision()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        if (mgr.isEmpty(qrystr))
            qrystr = mgr.getURL()+"?SEARCH=Y&STD_JOB_ID="+mgr.URLEncode(headset.getValue("STD_JOB_ID"));      

        bOpenNewWindow = true;
        urlString = "CreateStdJobRevisionDlg.page?STD_JOB_ID=" + mgr.URLEncode(headset.getValue("STD_JOB_ID")) +
                    "&CONTRACT=" + mgr.URLEncode(headset.getValue("CONTRACT"))+
                    "&STD_JOB_REVISION=" + mgr.URLEncode(headset.getValue("STD_JOB_REVISION"))+
                    "&QRYSTR=" + mgr.URLEncode(qrystr);

        newWinHandle = "CreateStdJobRevisionDlg"; 
    }

    public void showObsoletePM()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        bOpenNewWindow = true;
        urlString = "PmWithObsoleteStdJobs.page?STD_JOB_ID=" + mgr.URLEncode(headset.getValue("STD_JOB_ID")) +
                    "&CONTRACT=" + mgr.URLEncode(headset.getValue("CONTRACT"));

        newWinHandle = "PmWithObsoleteStdJobs"; 
    }

    public void updatePM()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        try
        {
            if (!replaced)
            {
                replaced = true;
                cmd = trans.addCustomCommand("UPDPM","Standard_Job_API.Replace_Revsions_");
                cmd.addParameter("STD_JOB_ID",headset.getRow().getValue("STD_JOB_ID"));
                cmd.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT"));
                cmd.addParameter("STD_JOB_REVISION","");
                trans = mgr.perform(trans);

                mgr.showAlert(mgr.translate("PCMWSTDJOBREPLACEMENT: The replacement of the standard job revisions will be performed on the same revision of the PM Actions."));
            }
            else
                mgr.showAlert(mgr.translate("PCMWSTDJOBREPLACEMENTERROR2: The replacement of the standard job revisions is already executing as a background job."));                           
        }
        catch (Throwable any)
        {
            replaced = false;
            mgr.showError(mgr.translate("PCMWSTDJOBREPLACEMENTERROR: The update of PM actions was not performed."));
        }

        headset.refreshRow();

    }

    public void generatePMRevision()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        bOpenNewWindow = true;

        urlString = "GeneratePmRevDlg.page?ACTION_CODE_ID=" + mgr.URLEncode(headset.getValue("ACTION_CODE_ID")) +
                    "&STANDARD_JOB_TYPE_DB=" + mgr.URLEncode(headset.getValue("STANDARD_JOB_TYPE_DB"))+
                    "&STD_JOB_ID=" + mgr.URLEncode(headset.getValue("STD_JOB_ID"))+
                    "&CONTRACT=" + mgr.URLEncode(headset.getValue("CONTRACT"))+
                    "&STD_JOB_REVISION=" + mgr.URLEncode(headset.getValue("STD_JOB_REVISION"))+
                    "&ORG_CODE=" + mgr.URLEncode(headset.getValue("ORG_CODE"));

        newWinHandle = "GeneratePmRevDlg"; 
    } 

    public void showObsoleteWORev()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        bOpenNewWindow = true;
        urlString = "WOWithObsoleteStdJobs.page?STD_JOB_ID=" + mgr.URLEncode(headset.getValue("STD_JOB_ID")) +
                    "&CONTRACT=" + mgr.URLEncode(headset.getValue("CONTRACT"));

        newWinHandle = "WOWithObsoleteStdJobs"; 
    }

    public void attributes()
    {
        ASPManager mgr = getASPManager();

        String calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);

        if (itemlay5.isMultirowLayout())
            itemset5.store();
        else
        {
            itemset5.unselectRows();
            itemset5.selectRow();
        }

        bOpenNewWindow = true;
        urlString = createTransferUrl("PermitTypeRMB.page", itemset5.getSelectedRows("PERMIT_TYPE_ID"));
        newWinHandle = "attributes"; 
    }

    public void perform(String command) 
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            headset.markSelectedRows(command);
            mgr.submit(trans);

            headset.refreshAllRows();
        }
        else
        {
            headset.unselectRows();
            headset.markRow(command);
            int currrow = headset.getCurrentRowNo();
            mgr.submit(trans);

            headset.goTo(currrow);
            headset.refreshRow();
        }   
    }

    public void activatePm()
    {
        ASPManager mgr = getASPManager();
        String sState = "Active";

        //Info Msg is populated only if the std job already has active revision.
        trans.clear();

        q = trans.addQuery("HASMOREACTIVEREVS","SELECT DISTINCT(1) NCOUNT FROM SEPARATE_STANDARD_JOB WHERE STD_JOB_ID = ? AND CONTRACT =  ? AND OBJSTATE = '" + sState + "' AND STD_JOB_REVISION != ? ");
        q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addParameter("CONTRACT", headset.getRow().getValue("CONTRACT"));
        q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        trans = mgr.perform(trans);
        if (trans.getNumberValue("HASMOREACTIVEREVS/DATA/NCOUNT")>0)
            mgr.showAlert(mgr.translate("PCMWSEPARATESTANDARDJOB2ACTSTDJOB: Previous \"Active\" Revision for Standard Job "+headset.getRow().getValue("STD_JOB_ID")+" will be set to \"Obsolete\"."));

        perform("ACTIVE__");
    }

    public void obsoletePm()
    {
        ASPManager mgr = getASPManager();

        perform("OBSOLETE__");
    }

    public void getBudgetValues()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addCustomCommand("GETPLAN1", "Standard_Job_Spare_Utility_API.Get_Plan_Inv_Part_Cost");
        cmd.addParameter("PLAN_MAT_COST","");
        cmd.addParameter("ITEM6_STD_JOB_ID", itemset6.getValue("STD_JOB_ID"));
        cmd.addParameter("ITEM6_STD_JOB_CONTRACT", itemset6.getValue("STD_JOB_CONTRACT"));
        cmd.addParameter("ITEM6_STD_JOB_REVISION", itemset6.getValue("STD_JOB_REVISION"));

        cmd = trans.addCustomCommand("GETPLAN2", "Standard_Job_Spare_Utility_API.Get_Plan_Ext_Cost");
        cmd.addParameter("PLAN_EXT_COST","");
        cmd.addParameter("ITEM6_STD_JOB_ID", itemset6.getValue("STD_JOB_ID"));
        cmd.addParameter("ITEM6_STD_JOB_CONTRACT", itemset6.getValue("STD_JOB_CONTRACT"));
        cmd.addParameter("ITEM6_STD_JOB_REVISION", itemset6.getValue("STD_JOB_REVISION"));

        cmd = trans.addCustomCommand("GETPLAN3", "Standard_Job_Role_Utility_API.Get_Plan_Pers_Revenue");
        cmd.addParameter("PLAN_PERS_REV","");
        cmd.addParameter("ITEM6_STD_JOB_ID", itemset6.getValue("STD_JOB_ID"));
        cmd.addParameter("ITEM6_STD_JOB_CONTRACT", itemset6.getValue("STD_JOB_CONTRACT"));
        cmd.addParameter("ITEM6_STD_JOB_REVISION", itemset6.getValue("STD_JOB_REVISION"));

        cmd = trans.addCustomCommand("GETPLAN4", "Standard_Job_Spare_Utility_API.Get_Plan_Mat_Revenue");
        cmd.addParameter("PLAN_MAT_REV","");
        cmd.addParameter("ITEM6_STD_JOB_ID", itemset6.getValue("STD_JOB_ID"));
        cmd.addParameter("ITEM6_STD_JOB_CONTRACT", itemset6.getValue("STD_JOB_CONTRACT"));
        cmd.addParameter("ITEM6_STD_JOB_REVISION", itemset6.getValue("STD_JOB_REVISION"));

        cmd = trans.addCustomCommand("GETPLAN5", "Standard_Job_Spare_Utility_API.Get_Plan_Ext_Revenue");
        cmd.addParameter("PLAN_EXT_REV","");
        cmd.addParameter("ITEM6_STD_JOB_ID", itemset6.getValue("STD_JOB_ID"));
        cmd.addParameter("ITEM6_STD_JOB_CONTRACT", itemset6.getValue("STD_JOB_CONTRACT"));
        cmd.addParameter("ITEM6_STD_JOB_REVISION", itemset6.getValue("STD_JOB_REVISION"));

        cmd = trans.addCustomCommand("GETPLAN6", "Standard_Job_Utility_API.Get_Tool_Facility_Revenue");
        cmd.addParameter("PLAN_TOOL_REV","");
        cmd.addParameter("ITEM6_STD_JOB_ID", itemset6.getValue("STD_JOB_ID"));
        cmd.addParameter("ITEM6_STD_JOB_CONTRACT", itemset6.getValue("STD_JOB_CONTRACT"));
        cmd.addParameter("ITEM6_STD_JOB_REVISION", itemset6.getValue("STD_JOB_REVISION"));

        cmd = trans.addCustomCommand("GETPLAN7", "Standard_Job_Utility_API.Get_Tool_Facility_Cost");
        cmd.addParameter("PLAN_TOOL_COST","");
        cmd.addParameter("ITEM6_STD_JOB_ID", itemset6.getValue("STD_JOB_ID"));
        cmd.addParameter("ITEM6_STD_JOB_CONTRACT", itemset6.getValue("STD_JOB_CONTRACT"));
        cmd.addParameter("ITEM6_STD_JOB_REVISION", itemset6.getValue("STD_JOB_REVISION"));

        cmd = trans.addCustomCommand("GETPLAN8", "Standard_Job_Role_Utility_API.Get_Plan_Pers_Cost");
        cmd.addParameter("PLAN_PERS_COST","");
        cmd.addParameter("ITEM6_STD_JOB_ID", itemset6.getValue("STD_JOB_ID"));
        cmd.addParameter("ITEM6_STD_JOB_CONTRACT", itemset6.getValue("STD_JOB_CONTRACT"));
        cmd.addParameter("ITEM6_STD_JOB_REVISION", itemset6.getValue("STD_JOB_REVISION"));

        cmd = trans.addCustomFunction("GETPLAN9", "Standard_Job_Planning_API.Get_Plan_Cost","PLAN_EXP_COST");
        cmd.addParameter("ITEM6_STD_JOB_ID", itemset6.getValue("STD_JOB_ID"));
        cmd.addParameter("ITEM6_STD_JOB_CONTRACT", itemset6.getValue("STD_JOB_CONTRACT"));
        cmd.addParameter("ITEM6_STD_JOB_REVISION", itemset6.getValue("STD_JOB_REVISION"));
        cmd.addParameter("WORK_ORDER_COST_TYPE", "X");

        cmd = trans.addCustomFunction("GETPLAN10", "Standard_Job_Planning_API.Get_Plan_Cost","PLAN_FIX_COST");
        cmd.addParameter("ITEM6_STD_JOB_ID", itemset6.getValue("STD_JOB_ID"));
        cmd.addParameter("ITEM6_STD_JOB_CONTRACT", itemset6.getValue("STD_JOB_CONTRACT"));
        cmd.addParameter("ITEM6_STD_JOB_REVISION", itemset6.getValue("STD_JOB_REVISION"));
        cmd.addParameter("WORK_ORDER_COST_TYPE", "F");

        cmd = trans.addCustomFunction("GETPLAN11", "Standard_Job_Planning_API.Get_Plan_Revenue","PLAN_EXP_REV");
        cmd.addParameter("ITEM6_STD_JOB_ID", itemset6.getValue("STD_JOB_ID"));
        cmd.addParameter("ITEM6_STD_JOB_CONTRACT", itemset6.getValue("STD_JOB_CONTRACT"));
        cmd.addParameter("ITEM6_STD_JOB_REVISION", itemset6.getValue("STD_JOB_REVISION"));
        cmd.addParameter("WORK_ORDER_COST_TYPE", "X");

        cmd = trans.addCustomFunction("GETPLAN12", "Standard_Job_Planning_API.Get_Plan_Revenue","PLAN_FIX_REV");
        cmd.addParameter("ITEM6_STD_JOB_ID", itemset6.getValue("STD_JOB_ID"));
        cmd.addParameter("ITEM6_STD_JOB_CONTRACT", itemset6.getValue("STD_JOB_CONTRACT"));
        cmd.addParameter("ITEM6_STD_JOB_REVISION", itemset6.getValue("STD_JOB_REVISION"));
        cmd.addParameter("WORK_ORDER_COST_TYPE", "F");

        trans=mgr.perform(trans);

        String planMatCost    = trans.getValue("GETPLAN1/DATA/PLAN_MAT_COST");
        String planExtCost    = trans.getValue("GETPLAN2/DATA/PLAN_EXT_COST");
        String planPersRev    = trans.getValue("GETPLAN3/DATA/PLAN_PERS_REV");
        String planToolsRev   = trans.getValue("GETPLAN6/DATA/PLAN_TOOL_REV");
        String planToolsCost  = trans.getValue("GETPLAN7/DATA/PLAN_TOOL_COST");
        String planPersCost   = trans.getValue("GETPLAN8/DATA/PLAN_PERS_COST");
        planMatRev            = trans.getValue("GETPLAN4/DATA/PLAN_MAT_REV");
        planExtRev            = trans.getValue("GETPLAN5/DATA/PLAN_EXT_REV");
        String planExpCost    = trans.getValue("GETPLAN9/DATA/PLAN_EXP_COST");
        String planFixCost    = trans.getValue("GETPLAN10/DATA/PLAN_FIX_COST"); 
        String planExpRev     = trans.getValue("GETPLAN11/DATA/PLAN_EXP_REV");
        String planFixRev     = trans.getValue("GETPLAN12/DATA/PLAN_FIX_REV"); 

        row = itemset6.getRow();

        double totPersCost = toDouble(planPersCost) + toDouble(planMatCost) + toDouble(planExtCost) + toDouble(planToolsCost)+ toDouble(planExpCost)+ toDouble(planFixCost);
        double totPersRev  = toDouble(planPersRev) + toDouble(planMatRev) + toDouble(planExtRev) + toDouble(planToolsRev)+ toDouble(planExpRev)+ toDouble(planFixRev);

        row.setValue("PLAN_PERS_COST", planPersCost);
        row.setValue("PLAN_MAT_COST", planMatCost);
        row.setValue("PLAN_EXT_COST", planExtCost);
        row.setValue("PLAN_TOOL_COST", planToolsCost);
        row.setValue("PLAN_EXP_COST", planExpCost);
        row.setValue("PLAN_FIX_COST", planFixCost);

        row.setValue("PLAN_PERS_REV", planPersRev);
        row.setValue("PLAN_MAT_REV", planMatRev);
        row.setValue("PLAN_EXT_REV", planExtRev);
        row.setValue("PLAN_TOOL_REV", planToolsRev);
        row.setValue("PLAN_EXP_REV", planExpRev);
        row.setValue("PLAN_FIX_REV", planFixRev);

        row.setNumberValue("TOT_PLAN_COST", totPersCost);
        row.setNumberValue("TOT_PLAN_REV", totPersRev);
        itemset6.setRow(row);
    }

    public void additionalQualifications()
    {
        ASPManager mgr = getASPManager();
        
        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());
        //this is needed when a new STD JOB is created.(createSearchURL is undefined at this instance)
        calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);

        int head_current = headset.getCurrentRowNo();   
        String s_head_curr = String.valueOf(head_current);
        ctx.setGlobal("HEADGLOBAL", s_head_curr);

        bOpenNewWindow = true;
        urlString = "StdJobQualificationsDlg.page?RES_TYPE=QUALIFICATIONS"+
                    "&STD_JOB_ID="+ (mgr.isEmpty(itemset0.getValue("STD_JOB_ID"))?"":mgr.URLEncode(itemset0.getValue("STD_JOB_ID"))) +
                    "&STD_JOB_REVISION="+ (mgr.isEmpty(itemset0.getValue("STD_JOB_REVISION"))?"":mgr.URLEncode(itemset0.getValue("STD_JOB_REVISION"))) +
                    "&STD_JOB_CONTRACT="+ (mgr.isEmpty(itemset0.getValue("STD_JOB_CONTRACT"))?"":mgr.URLEncode(itemset0.getValue("STD_JOB_CONTRACT"))) +
                    "&ROW_NO="+ (mgr.isEmpty(itemset0.getValue("ROW_NO"))?"":mgr.URLEncode(itemset0.getValue("ROW_NO"))) +
                    "&QRYSTR=" + mgr.URLEncode(qrystr) +
                    "&FRMNAME=CopyWoOr";
        newWinHandle =  "additionalQualifications"; 
    }

    public void predecessors()
    {
       ASPManager mgr = getASPManager();
       int count;
       ASPBuffer data_buff;
       ASPBuffer rowBuff;
       //this is needed when a new STD JOB is created.(createSearchURL is undefined at this instance)
        calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);

       ctx.setGlobal("KEY_NO1", itemset0.getValue("STD_JOB_ID"));
       ctx.setGlobal("KEY_NO2", itemset0.getValue("STD_JOB_CONTRACT"));
       ctx.setGlobal("KEY_NO3", itemset0.getValue("STD_JOB_REVISION"));
       ctx.setGlobal("STR_LU", "StandardJobRole");
       
       int head_current = headset.getCurrentRowNo();   
       String s_head_curr = String.valueOf(head_current);
       ctx.setGlobal("HEADGLOBAL", s_head_curr);

       if (itemlay0.isMultirowLayout())
       {
           itemset0.storeSelections();
           itemset0.setFilterOn();
           count = itemset0.countSelectedRows();
       }
       else
       {
           itemset0.unselectRows();
           count = 1;
       }

       data_buff = mgr.newASPBuffer();

       for (int i = 0; i < count; i++)
       {
          rowBuff = mgr.newASPBuffer();

          if (i == 0)
          {   
              rowBuff.addItem("ROW_NO", itemset0.getValue("ROW_NO"));
              rowBuff.addItem("ROW_NO", itemset0.getValue("ROW_NO"));
          }
          else
          {
              rowBuff.addItem(null, itemset0.getValue("ROW_NO"));
              rowBuff.addItem(null, itemset0.getValue("ROW_NO"));
          }

          data_buff.addBuffer("DATA", rowBuff);

          if (itemlay0.isMultirowLayout())
              itemset0.next();
      }

       if (itemlay0.isMultirowLayout())
          itemset0.setFilterOff();

       bOpenNewWindow = true;
       if ("TRUE".equals(mgr.readValue("REFRESHPARENT")))
          urlString= createTransferUrl("ConnectPredecessorsDlg.page?&BLOCK=ITEM0", data_buff);
       else
           urlString= createTransferUrl("ConnectPredecessorsDlg.page?&QRYSTR=" + mgr.URLEncode(qrystr) +
                                        "&FRMNAME=CopyWoOr", data_buff);

       newWinHandle = "predecessors"; 
       newWinHeight = 550;
       newWinWidth = 650;

   }

    public void saveReturnITEM6()
    {
            ASPManager mgr = getASPManager();

//          Bug 78556, Start
            int currHead = headset.getCurrentRowNo();
//          Bug 78556, End
           trans.clear();

            double persCost = mgr.readNumberValue("PERS_BUDGET_COST");
            if (isNaN(persCost))
                    persCost = 0;

            double matCost = mgr.readNumberValue("MAT_BUDGET_COST");
            if (isNaN(matCost))
                    matCost = 0;

            double tfCost = mgr.readNumberValue("TF_BUDGET_COST");
            if (isNaN(tfCost))
                    tfCost = 0;

            double extCost = mgr.readNumberValue("EXT_BUDGET_COST");
            if (isNaN(extCost))
                    extCost = 0;

            double expCost = mgr.readNumberValue("EXP_BUDGET_COST");
            if (isNaN(expCost))
                    expCost = 0;

            double fixCost = mgr.readNumberValue("FIX_BUDGET_COST");
            if (isNaN(fixCost))
                    fixCost = 0;

            ASPBuffer buff = itemset6.getRow();

            buff.setNumberValue("PERS_BUDGET_COST", persCost);
            buff.setNumberValue("MAT_BUDGET_COST", matCost);
            buff.setNumberValue("TF_BUDGET_COST", tfCost);
            buff.setNumberValue("EXT_BUDGET_COST", extCost);
            buff.setNumberValue("EXP_BUDGET_COST", expCost);
            buff.setNumberValue("FIX_BUDGET_COST", fixCost);
            buff.setNumberValue("TOT_BUDGET_COST", persCost+matCost+tfCost+extCost+expCost+fixCost);

            itemset6.setRow(buff);
            mgr.submit(trans);
//          Bug 78556, Start
            headset.goTo(currHead);
            headset.refreshRow();
//          Bug 78556, End

            trans.clear();
            getBudgetValues();
    }


    public void preDefine()
    {
        ASPManager mgr = getASPManager();

        tempblk = mgr.newASPBlock("TEMP");
        f = tempblk.addField("LUNAME");

        //------------------------------------------------------------

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("OBJSTATE");
        f.setHidden();

        f = headblk.addField("OBJEVENTS");
        f.setHidden();

        f = headblk.addField("MODULENAME");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("COMPANY");
        f.setSize(0);
        f.setMandatory();
        f.setHidden();
        f.setUpperCase();

        f = headblk.addField("STD_JOB_ID");
        f.setSize(15);
        f.setMandatory();
        f.setReadOnly();
        f.setInsertable();
        f.setUpperCase();
        f.setMaxLength(12); 
        f.setLabel("PCMWSEPARATESTANDARDJOB2STDJOBID: Standard Job ID");
        f.setHilite();

        f = headblk.addField("DEFINITION");
        f.setSize(50);
        f.setMandatory();
        f.setLabel("PCMWSEPARATESTANDARDJOB2DEFDESC: Definition");
        f.setMaxLength(40);
        f.setHilite();

        f = headblk.addField("STD_JOB_REVISION");
        f.setSize(15);
        f.setMandatory();
        f.setReadOnly();
        f.setInsertable();
        f.setUpperCase();
        f.setMaxLength(6); 
        f.setLabel("PCMWSEPARATESTANDARDJOB2STDJOBREV: Standard Job Revision");
        f.setHilite();

        f = headblk.addField("CONTRACT");
        f.setSize(7);
        f.setInsertable();
        f.setReadOnly();
        f.setMaxLength(5); 
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450); 
        f.setLabel("PCMWSEPARATESTANDARDJOB2CONTRACT: Site");
        f.setCustomValidation("CONTRACT,HEAD_ORG_CODE","COMPANY,CALENDAR_ID,CALENDAR_DESCRIPTION");
        f.setUpperCase();
        f.setHilite();

        f = headblk.addField("STATE");
        f.setSize(15);
        f.setLabel("PCMWSEPARATESTANDARDJOB2STATE: Status");
        f.setLOV("StdJobStatus.page");
        f.setReadOnly();
        f.setHilite();

        f = headblk.addField("TYPE_ID");
        f.setSize(20);
        f.setMaxLength(20);
        f.setDynamicLOV("STD_JOB_TYPE",600,450);
        f.setLabel("PCMWSEPSTDJOB2STDJOBTYPE: Std Job Type");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("TYPE_DESCRIPTION");
        f.setSize(20);
        f.setMaxLength(200);
        f.setFunction("STD_JOB_TYPE_API.Get_Description(:TYPE_ID)");
        f.setReadOnly();
        mgr.getASPField("TYPE_ID").setValidation("TYPE_DESCRIPTION");
        f.setDefaultNotVisible();

        f = headblk.addField("ACTION_CODE_ID");
        f.setSize(10);
        f.setMaxLength(10);
        f.setUpperCase();
        f.setDynamicLOV("MAINTENANCE_ACTION",600,450);
        f.setLabel("PCMWSEPSTDJOB2ACTCODID: Action");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("ACTION_CODE_DESCRIPTION");
        f.setSize(20);
        f.setMaxLength(200);
        f.setFunction("MAINTENANCE_ACTION_API.Get_Description(:ACTION_CODE_ID)");
        f.setReadOnly();
        mgr.getASPField("ACTION_CODE_ID").setValidation("ACTION_CODE_DESCRIPTION");
        f.setDefaultNotVisible();

        f = headblk.addField("STD_TEXT_ONLY");
        f.setSize(18);
        f.setLabel("PCMWSEPARATESTANDARDJOB2STDTEXTONLY: Standard Text");
        f.setCheckBox("FALSE,TRUE");

        f = headblk.addField("WORK_DESCRIPTION");
        f.setSize(50);
        f.setHeight(5);
        f.setLabel("PCMWSEPARATESTANDARDJOB2WORKDESCRIPTION: Work Description (max 2000 char)");
        f.setMaxLength(2000);
        f.setDefaultNotVisible();

        // Group Planning Info

        f = headblk.addField("HEAD_ORG_CODE");
        f.setDbName("ORG_CODE");
        f.setSize(8);
        f.setMaxLength(8);
        f.setUpperCase();
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,450);
        f.setLabel("PCMWSEPSTDJOB2ORGCODE: Maintenance Organization");
        f.setCustomValidation("CONTRACT,HEAD_ORG_CODE","CALENDAR_ID,CALENDAR_DESCRIPTION");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("SIGNATURE");
        f.setSize(11);
        f.setMaxLength(11);
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,450);
        f.setLabel("PCMWSEPSTDJOB2SIGNATURE: Planned By");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("WORK_TYPE_ID");
        f.setSize(20);
        f.setMaxLength(20);
        f.setUpperCase();
        f.setDynamicLOV("WORK_TYPE",600,450);
        f.setLabel("PCMWSEPSTDJOB2WORKTYPEID: Work Type");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("CREATED_BY");
        f.setSize(20);
        f.setMaxLength(20);
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445); 
        f.setMandatory();
        f.setLOVProperty("TITLE",mgr.translate("PCMWSEPRSTDJOBLOVCREBY: List of Created by"));
        f.setLabel("PCMWSEPARATESTANDARDJOB2CREATEDBY: Created by");
        f.setUpperCase();
        f.setReadOnly();
        f.setInsertable();
        f.setCustomValidation("COMPANY,CREATED_BY","CREATED_BY_ID");

        f = headblk.addField("CREATED_BY_ID"); 
        f.setHidden(); 

        f = headblk.addField("OP_STATUS_ID");
        f.setSize(3);
        f.setMaxLength(3);
        f.setUpperCase();
        f.setDynamicLOV("OPERATIONAL_STATUS",600,450);
        f.setLabel("PCMWSEPSTDJOB2OPSTATUSID: Operational Status");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("CREATION_DATE","Date");
        f.setSize(18);
        f.setMandatory();
        f.setLabel("PCMWSEPARATESTANDARDJOB2CREATIONDATE: Creation Date");
        f.setReadOnly();

        f = headblk.addField("PRIORITY_ID");
        f.setSize(1);
        f.setMaxLength(1);
        f.setUpperCase();
        f.setDynamicLOV("MAINTENANCE_PRIORITY",600,450);
        f.setLabel("PCMWSEPSTDJOBPRIORITY: Priority");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("CHANGED_BY");
        f.setSize(20);
        f.setMaxLength(20);
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445); 
        f.setLabel("PCMWSEPARATESTANDARDJOB2CHANGEDBY: Changed by");
        f.setLOVProperty("TITLE",mgr.translate("PCMWSEPRSTDJOBLOVCHGBY: List of Changed by"));
        f.setUpperCase();
        f.setCustomValidation("COMPANY,CHANGED_BY","CHANGED_BY_ID");

        f = headblk.addField("CHANGED_BY_ID");
        f.setHidden(); 

        f = headblk.addField("EXECUTION_TIME","Number");
        f.setLabel("PCMWSEPSTDJOBEXECUTIONTIME: Execution Time");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("LAST_CHANGED","Date");
        f.setSize(18);
        f.setLabel("PCMWSEPSTDJOB2LASTCHANGED: Last Changed");
        f.setReadOnly();

        // Group Copied From

        f = headblk.addField("COPIED_SITE");
        f.setSize(5);
        f.setMaxLength(5);
        f.setLabel("PCMWSEPSTDJOB2COPIEDSITE: Site");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("COPIED_STD_JOB_ID");
        f.setSize(12);
        f.setMaxLength(12);
        f.setLabel("PCMWSEPSTDJOB2COPIEDSTDJOBID: Std Job ID");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("COPIED_REVISION");
        f.setSize(6);
        f.setMaxLength(6);
        f.setLabel("PCMWSEPSTDJOB2COPIEDREVISION: Revision");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("REPLACES_REVISION");
        f.setSize(6);
        f.setMaxLength(6);
        f.setLabel("PCMWSEPSTDJOB2REPLACESREVISION: Replaces Revision");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("REPLACED_REVISION");
        f.setSize(6);
        f.setMaxLength(6);
        f.setLabel("PCMWSEPSTDJOB2REPLACEDREVISION: Replaced By Revision");
        f.setReadOnly();
        f.setDefaultNotVisible();

        // Group Maintenance Plan

        f = headblk.addField("START_VALUE");
        f.setSize(11);
        f.setMaxLength(11);
        f.setLabel("PCMWSEPSTDJOB2STARTVALUE: Start Value");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("PM_START_UNIT");
        f.setMaxLength(200);
        f.setSelectBox();
        f.enumerateValues("PM_START_UNIT_API");
        f.setLabel("PCMWSEPSTDJOB2PMSTARTUNIT: Start Unit");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("INTERVAL","Number");
        f.setSize(3);
        f.setMaxLength(3);
        f.setLabel("PCMWSEPSTDJOB2INTERVAL: Interval");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("PM_INTERVAL_UNIT");
        f.setMaxLength(200);
        f.setSelectBox();
        f.enumerateValues("PM_INTERVAL_UNIT_API");
        f.setLabel("PCMWSEPSTDJOB2PMINTERVALUNIT: Interval Unit");
        f.setInsertable();
        f.setDefaultNotVisible();

        // Group Event

        f = headblk.addField("CALL_CODE");
        f.setSize(10);
        f.setMaxLength(10);
        f.setUpperCase();
        f.setDynamicLOV("MAINTENANCE_EVENT",600,450);
        f.setLabel("PCMWSEPSTDJOB2CALLCODE: Event");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("START_CALL","Number");
        f.setLabel("PCMWSEPSTDJOB2STARTCALL: Start");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("CALL_INTERVAL","Number");
        f.setLabel("PCMWSEPSTDJOB2CALLINTERVAL: Interval");
        f.setInsertable();
        f.setDefaultNotVisible();

        // Group Valid

        f = headblk.addField("VALID_FROM","Date");
        f.setSize(20);
        f.setLabel("PCMWSEPSTDJOB2VALIDFROM: Valid From");
        f.setDefaultNotVisible();

        f = headblk.addField("VALID_TO","Date");
        f.setSize(20);
        f.setLabel("PCMWSEPSTDJOB2VALIDTO: Valid To");
        f.setDefaultNotVisible();

        f = headblk.addField("CALENDAR_ID");
        f.setSize(10);
        f.setMaxLength(10);
        f.setUpperCase();
        f.setDynamicLOV("WORK_TIME_CALENDAR",600,450);
        f.setLabel("PCMWSEPSTDJOB2WORKTIMECALENDAR: Calendar");
        f.setInsertable();
        f.setLOVProperty("WHERE","OBJSTATE='Generated'");
        f.setDefaultNotVisible();

        f = headblk.addField("CALENDAR_DESCRIPTION");
        f.setFunction("Work_Time_Calendar_API.Get_Description(:CALENDAR_ID)");
        f.setSize(30);
        f.setReadOnly();
        f.setDefaultNotVisible();
        mgr.getASPField("CALENDAR_ID").setValidation("CALENDAR_DESCRIPTION");

        f = headblk.addField("STANDARD_JOB_TYPE");
        f.setHidden();

        f = headblk.addField("STANDARD_JOB_TYPE_DB");
        f.setHidden();

        // Group Std Job Has

        f = headblk.addField("CBROLES");
        f.setLabel("PCMWSEPARATESTANDARDJOB2CBROLES: Crafts");
        f.setFunction("SEPARATE_STANDARD_JOB_API.HAS_ROLE(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");
        f.setCheckBox("FALSE,TRUE");
        f.setReadOnly();
        f.setQueryable();

        f = headblk.addField("CBMATERIALS");
        f.setLabel("PCMWSEPARATESTANDARDJOB2CBMATERIALS: Materials");
        f.setFunction("SEPARATE_STANDARD_JOB_API.HAS_MATERIAL(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");
        f.setCheckBox("FALSE,TRUE");
        f.setReadOnly();

        f = headblk.addField("CBTOOLFACILITIES");
        f.setLabel("PCMWSEPARATESTANDARDJOB2CBTOOLFACILITIES: Tools and Facilities");
        f.setFunction("SEPARATE_STANDARD_JOB_API.HAS_FACILITY(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");
        f.setCheckBox("FALSE,TRUE");
        f.setReadOnly();

        f = headblk.addField("CBDOCUMENTS");
        f.setLabel("PCMWSEPARATESTANDARDJOB2CBDOCUMENTS: Documents");
        f.setFunction("DOC_REFERENCE_OBJECT_API.EXIST_OBJ_REFERENCE('StandardJob','CONTRACT='||CONTRACT||'^STD_JOB_ID='||STD_JOB_ID||'^STD_JOB_REVISION='||STD_JOB_REVISION||'^')");
        f.setCheckBox("FALSE,TRUE");
        f.setReadOnly();

        //Bug 82435, start
        f = headblk.addField("REASON");                     
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2REASON: Created Reason");
        f.setSize(15);

        f = headblk.addField("DONE_BY");                     
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2DONEBY: Created By");
        f.setSize(15);

        f = headblk.addField("REV_CRE_DATE", "Datetime");                     
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2REVCREDATE: Created Date");
        f.setSize(15);
        //Bug 82435, end

        // Hidden Dummy Fields

        f = headblk.addField("CBSTDTXT");
        f.setHidden();
        f.setFunction("''");
        f.setReadOnly();

        f = headblk.addField("KEY_REF");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("USER_ID");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("CONTRACT1");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("COMPANY1");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("IS_CONNECTED");
        f.setHidden();  
        f.setFunction("Standard_Job_API.Is_Connected(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");

        f = headblk.addField("IS_REPLACEABLE");
        f.setHidden();
        f.setFunction("Standard_Job_API.Is_Replaceable(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");

        f = headblk.addField("VALID_STD_JOB");
        f.setHidden();
        f.setFunction("STANDARD_JOB_API.Is_Active_Std_Job(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");

        f = headblk.addField("HAS_OBS_WO");
        f.setHidden();
        f.setFunction("Standard_Job_API.Obs_Std_Has_Wo(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");

        f = headblk.addField("START_DATE","Date");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("INDEX1");
        f.setHidden();
        f.setFunction("''");

        headblk.setView("SEPARATE_STANDARD_JOB");
        headblk.defineCommand("SEPARATE_STANDARD_JOB_API","New__,Modify__,Remove__,ACTIVE__,OBSOLETE__");

        headset = headblk.getASPRowSet();

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWSEPARATESTANDARDJOB2HD: Separate Standard Job"));
        headtbl.setWrap();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.defineCommand(headbar.DELETE,"remove");

        headbar.defineCommand(headbar.SAVERETURN,"saveReturn","checkHeadFields(-1)");
        headbar.defineCommand(headbar.SAVENEW,"saveNew","checkHeadFields(-1)");

        headbar.addCustomCommand("createNewRevision",mgr.translate("PCMWSEPARATESTDJOB2NEWREV: Create New Revision..."));
        headbar.addCustomCommand("copyStdJobId",mgr.translate("PCMWSEPARATESTANDARDJOB2COPYSTDJOB: Copy Standard Job..."));
        headbar.addCustomCommand("generatePMRevision",mgr.translate("PCMWSEPARATESTANDARDJOB2GENERATEPMREV: Generate PM Action..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("showObsoleteWORev",mgr.translate("PCMWSEPARATESTANDARDJOB2OBSOLETEWOREV: Obsolete Revisions on Active Work Orders..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("showObsoletePM",mgr.translate("PCMWSEPARATESTANDARDJOB2OBSPMREV: Obsolete Revisions on PM Actions..."));
        headbar.addCustomCommand("updatePM",mgr.translate("PCMWSEPARATESTANDARDJOB2UPDATEPMREV: Replace Obsolete Revisions on PM Actions"));
        headbar.addCustomCommand("none","");
        headbar.addCustomCommandSeparator();
        // When the Multirow Actions are enabled the Active RMB should be disabled.
        headbar.addCustomCommand("activatePm",mgr.translate("PCMWSEPSTDJOBACTIVE: Active"));
        headbar.addCustomCommand("obsoletePm",mgr.translate("PCMWSEPSTDJOBOBSOLETE: Obsolete"));

        headbar.addCommandValidConditions("activatePm","OBJSTATE", "Enable", "Preliminary");
        headbar.addCommandValidConditions("obsoletePm","OBJSTATE", "Enable", "Preliminary;Active");
        headbar.addCommandValidConditions("showObsoletePM","IS_REPLACEABLE","Enable","TRUE");
        headbar.addCommandValidConditions("updatePM","IS_REPLACEABLE","Enable","TRUE");
        headbar.addCommandValidConditions("generatePMRevision","VALID_STD_JOB","Enable","TRUE");
        headbar.addCommandValidConditions("showObsoleteWORev","HAS_OBS_WO","Enable","TRUE");

        tabs = mgr.newASPTabContainer();
        tabs.setTabSpace(5);  
        tabs.setTabWidth(100);

        //Bug 89834, start
        tabs.addTab("PCMWSEPARATESTANDARDJOB2PREPTAB: Prepare","javascript:commandSet('HEAD.activatePrepareTab','')");
        tabs.addTab("PCMWSEPARATESTANDARDJOB2PROGTAB: Program","javascript:commandSet('HEAD.activateProgramTab','')");
        tabs.addTab("PCMWSEPARATESTANDARDJOB2BUDGTAB: Budget","javascript:commandSet('HEAD.activateBudgetTab','')");
        tabs.addTab("PCMWSEPARATESTANDARDJOB2OPERTAB: Operations","javascript:commandSet('HEAD.activateOperationsTab','')");
        tabs.addTab("PCMWSEPARATESTANDARDJOB2MATTAB: Materials","javascript:commandSet('HEAD.activateMaterialTab','')");
        tabs.addTab("PCMWSEPARATESTANDARDJOB2TOOLTAB: Tools and Facilities","javascript:commandSet('HEAD.activateToolsnFacilitiesTab','')");
        tabs.addTab("PCMWSEPARATESTANDARDJOB2PLANTAB: Planning","javascript:commandSet('HEAD.activatePlanningTab','')");
        tabs.addTab("PCMWSEPARATESTANDARDJOB2PERMTAB: Permits","javascript:commandSet('HEAD.activatePermitTab','')");
        //Bug 89834, end

        headbar.addCustomCommand("activatePrepareTab","Prepare");
        headbar.addCustomCommand("activateProgramTab","Program");
        headbar.addCustomCommand("activateBudgetTab","Budget");
        headbar.addCustomCommand("activateOperationsTab","Operations");
        headbar.addCustomCommand("activateMaterialTab","Materials");
        headbar.addCustomCommand("activateToolsnFacilitiesTab","Tools and Facilities");
        headbar.addCustomCommand("activatePlanningTab","Planning");
        headbar.addCustomCommand("activatePermitTab","Permits");

        headbar.addCustomCommandGroup("PMSTATUS", mgr.translate("PCMWSEPSTDJOBGRPSTATUS: Standard Job Status"));
        headbar.setCustomCommandGroup("activatePm", "PMSTATUS");
        headbar.setCustomCommandGroup("obsoletePm", "PMSTATUS");

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

        headlay.defineGroup("","STD_JOB_ID,DEFINITION,STD_JOB_REVISION,CONTRACT,STATE,TYPE_ID,TYPE_DESCRIPTION,ACTION_CODE_ID,ACTION_CODE_DESCRIPTION,STD_TEXT_ONLY,WORK_DESCRIPTION",false,true);   
        headlay.defineGroup(mgr.translate("PCMWSEPSTDJOB2GRPLABELPLANINFO: Planning Info"),"HEAD_ORG_CODE,SIGNATURE,WORK_TYPE_ID,CREATED_BY,OP_STATUS_ID,CREATION_DATE,PRIORITY_ID,CHANGED_BY,EXECUTION_TIME,LAST_CHANGED",true,true);
        headlay.defineGroup(mgr.translate("PCMWSEPSTDJOB2GRPLABELCOPDFROM: Copied From"),"COPIED_SITE,COPIED_STD_JOB_ID,COPIED_REVISION,REPLACES_REVISION,REPLACED_REVISION",true,true);
        headlay.defineGroup(mgr.translate("PCMWSEPSTDJOB2GRPLABELMAINTPLAN: Maintenance Plan"),"START_VALUE,PM_START_UNIT,INTERVAL,PM_INTERVAL_UNIT",true,true);
        headlay.defineGroup(mgr.translate("PCMWSEPSTDJOB2GRPLABELEVENT: Event"),"CALL_CODE,START_CALL,CALL_INTERVAL",true,true);
        headlay.defineGroup(mgr.translate("PCMWSEPSTDJOB2GRPLABELVALID: Valid"),"VALID_FROM,VALID_TO,CALENDAR_ID,CALENDAR_DESCRIPTION",true,true);
        headlay.defineGroup(mgr.translate("PCMWSEPSTDJOB2GRPLABELSTJOBHAS: Standard Job Has"),"CBROLES,CBMATERIALS,CBTOOLFACILITIES,CBDOCUMENTS",true,true);
        headlay.defineGroup(mgr.translate("PCMWSEPSTDJOB2GRPLABELREVINFO: Revision Info"),"REASON,DONE_BY,REV_CRE_DATE",true,true);   

        headlay.setSimple("TYPE_DESCRIPTION");
        headlay.setSimple("ACTION_CODE_DESCRIPTION");
        headlay.setSimple("CALENDAR_DESCRIPTION");

        //-------------------------------------------------------
        //   Block refers to CRAFTS tab
        //-------------------------------------------------------

        itemblk0 = mgr.newASPBlock("ITEM0");

        f = itemblk0.addField("ITEM0_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk0.addField("ITEM0_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk0.addField("ROW_NO","Number");
        f.setSize(10);
        f.setLabel("PCMWSEPARATESTANDARDJOB2ROWNO: Operation No");
        f.setReadOnly();
        f.setInsertable();

        f = itemblk0.addField("DESCRIPTION");
        f.setSize(50);
        f.setMandatory();
        f.setLabel("PCMWSEPARATESTANDARDJOB2DESCRIPTION: Description");
        f.setMaxLength(60);

        f = itemblk0.addField("ORG_CODE");
        f.setSize(20);
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ORG_CONTRACT CONTRACT",600,450);
        f.setLabel("PCMWSEPARATESTANDARDJOB2ORGCODE: Maintenance Organization");
        f.setUpperCase();
        f.setCustomValidation("CATALOG_NO,ROLE_CODE,ORG_CONTRACT,ORG_CODE,ITEM0_CONTRACT,SLSPRTCOST","DEF_CATALOG_NO,CATALOG_NO,SLSPRTCOST,DEFSLSPRTCOST,SLSPRTCTLGDESC,SLSPRTLSTPRICE");
        f.setMaxLength(8);

        f = itemblk0.addField("ORG_CONTRACT");
        f.setSize(10);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
        f.setLabel("PCMWSEPARATESTANDARDJOB2ORGCONTR: Organization Site");
        f.setUpperCase();
        f.setMaxLength(5);

        f = itemblk0.addField("ROLE_CODE");
        f.setSize(12);
        f.setDynamicLOV("ROLE_TO_SITE_LOV","ORG_CONTRACT CONTRACT",600,450);
        f.setLabel("PCMWSEPARATESTANDARDJOB2ROLECODE: Craft ID");
        f.setUpperCase();
        f.setCustomValidation("CATALOG_NO,ROLE_CODE,ORG_CONTRACT,ORG_CODE,ITEM0_CONTRACT","CATALOG_NO,ROLEDESCRIPTION,SLSPRTCOST,SLSPRTCTLGDESC,SLSPRTLSTPRICE");
        f.setMaxLength(10);

        f = itemblk0.addField("ROLEDESCRIPTION");
        f.setSize(24);
        f.setLabel("PCMWSEPARATESTANDARDJOB2ROLEDESCRIPTION: Craft Description");
        f.setFunction("ROLE_API.Get_Description(:ROLE_CODE)");
        f.setReadOnly();
        f.setMaxLength(200);
        f.setDefaultNotVisible();

        f = itemblk0.addField("PLANNED_MEN","Number");
        f.setSize(22);
        f.setLabel("PCMWSEPARATESTANDARDJOB2PLANNEDMEN: Planned Men");
        f.setCustomValidation("PLANNED_MEN,PLANNED_HOURS,SLSPRTLSTPRICE,CATALOG_NO,ITEM0_CONTRACT,QTY_TO_INVOICE,SALES_PRICE,DISCOUNT,ITEM0_PRICE_AMOUNT,WORK_ORDER_INVOICE_TYPE","ITEM0_PRICE_AMOUNT");

        f = itemblk0.addField("PLANNED_HOURS","Number");
        f.setSize(22);
        f.setLabel("PCMWSEPARATESTANDARDJOB2PLANNEDHOURS: Planned Hours");
        f.setCustomValidation("PLANNED_HOURS,PLANNED_MEN,CATALOG_NO,ITEM0_CONTRACT,QTY_TO_INVOICE,SALES_PRICE,DISCOUNT,ITEM0_PRICE_AMOUNT,WORK_ORDER_INVOICE_TYPE,SLSPRTLSTPRICE","ITEM0_PRICE_AMOUNT");

        f = itemblk0.addField("TEAM_CONTRACT");
        f.setSize(7);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
        f.setLabel("PCMWSEPARATESTANDARDJOB2CONT: Team Site");
        f.setMaxLength(5);
        f.setInsertable();
        f.setUpperCase();

        f = itemblk0.addField("TEAM_ID");
        f.setCustomValidation("TEAM_ID,TEAM_CONTRACT","ITEM0_TEAMDESC");
        f.setSize(13);
        f.setDynamicLOV("MAINT_TEAM","TEAM_CONTRACT",600,450);
        f.setMaxLength(20);
        f.setInsertable();
        f.setLabel("PCMWSEPARATESTANDARDJOB2TID: Team ID");
        f.setUpperCase();

        f = itemblk0.addField("ITEM0_TEAMDESC");
        f.setFunction("Maint_Team_API.Get_Description(:TEAM_ID,:TEAM_CONTRACT)");
        f.setSize(40);
        f.setMaxLength(200);
        f.setLabel("PCMWSEPARATESTANDARDJOB2ITEM0DESC: Description");
        f.setReadOnly();

        f = itemblk0.addField("PREDECESSOR");
        f.setSize(22);
        f.setLabel("PCMWSEPERATESTANDARDJOB2PRED: Predecessors");
        f.setLOV("ConnectPredecessorsDlg.page",600,450);
        f.setFunction("Std_Job_Role_Dependencies_API.Get_Predecessors(:ITEM0_STD_JOB_ID,:ITEM0_STD_JOB_CONTRACT,:ITEM0_STD_JOB_REVISION,:ROW_NO)");

        f = itemblk0.addField("TOOLS");
        f.setSize(32);
        f.setLabel("PCMWSEPARATESTANDARDJOB2TOOLS: Tools");
        f.setMaxLength(25);
        f.setDefaultNotVisible();

        f = itemblk0.addField("REMARK");
        f.setSize(24);
        f.setLabel("PCMWSEPARATESTANDARDJOB2REMARK: Remark");
        f.setMaxLength(80);
        f.setMaxLength(2000);
        f.setDefaultNotVisible();
        f.setHeight(4);

        f = itemblk0.addField("ITEM0_CONTRACT");
        f.setSize(20);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
        f.setLabel("PCMWSEPARATESTANDARDJOB2ITEM0CONTRACT: Sales Part Site");   
        f.setDbName("CONTRACT");
        f.setUpperCase();
        f.setMaxLength(5);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk0.addField("CATALOG_NO");
        f.setSize(18);
        f.setDynamicLOV("SALES_PART_SERVICE_LOV","ITEM0_CONTRACT CONTRACT",600,450);
        f.setLabel("PCMWSEPARATESTANDARDJOB2CATALOGNO: Sales Part No");
        f.setUpperCase();
        f.setCustomValidation("ITEM0_CONTRACT,CATALOG_NO,ORG_CONTRACT,ORG_CODE,ROLE_CODE,PLANNED_HOURS,PLANNED_MEN,QTY_TO_INVOICE,SALES_PRICE,DISCOUNT,ITEM0_PRICE_AMOUNT","SLSPRTCTLGDESC,SLSPRTCOST,SLSPRTLSTPRICE,ITEM0_PRICE_AMOUNT");
        f.setMaxLength(25);
        f.setDefaultNotVisible();

        f = itemblk0.addField("SLSPRTCTLGDESC");
        f.setSize(24);
        f.setLabel("PCMWSEPARATESTANDARDJOB2SLSPRTCTLGDESC: Description");
        if (mgr.isModuleInstalled("ORDER"))
            f.setFunction("SALES_PART_API.Get_Catalog_Desc(:ITEM0_CONTRACT,:CATALOG_NO)");
        else
            f.setFunction("''");
        f.setReadOnly();
        f.setMaxLength(35);
        f.setDefaultNotVisible();

        f = itemblk0.addField("SLSPRTCOST","Money");
        f.setSize(12);
        f.setLabel("PCMWSEPARATESTANDARDJOB2SLSPRTCOST: Cost");
        f.setFunction("WORK_ORDER_PLANNING_UTIL_API.Get_Cost(:ORG_CODE,:ROLE_CODE,:ITEM0_CONTRACT,:CATALOG_NO,:ORG_CONTRACT)");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk0.addField("DEFSLSPRTCOST");
        f.setHidden();  
        f.setFunction("''");

        f = itemblk0.addField("SLSPRTLSTPRICE","Money");
        f.setSize(18);
        f.setLabel("PCMWSEPARATESTANDARDJOB2SLSPRTLSTPRC: List Price");
        if (mgr.isModuleInstalled("ORDER"))
            f.setFunction("SALES_PART_API.Get_List_Price(:ITEM0_CONTRACT,:CATALOG_NO)");
        else
            f.setFunction("''");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk0.addField("WORK_ORDER_INVOICE_TYPE");
        f.setSize(25);
        f.setLabel("PCMWSEPARATESTANDARDJOB2WRKOIT: Work Order Invoice Type");
        f.setSelectBox();
        f.enumerateValues("WORK_ORDER_INVOICE_TYPE_API");
        f.setInsertable();
        f.setCustomValidation("PLANNED_HOURS,PLANNED_MEN,CATALOG_NO,ITEM0_CONTRACT,QTY_TO_INVOICE,SALES_PRICE,DISCOUNT,ITEM0_PRICE_AMOUNT,WORK_ORDER_INVOICE_TYPE,SLSPRTLSTPRICE","ITEM0_PRICE_AMOUNT");
        f.setDefaultNotVisible();

        f = itemblk0.addField("QTY_TO_INVOICE","Number");
        f.setSize(18);
        f.setLabel("PCMWSEPARATESTANDARDJOB2QTYINV: Qty To Invoice");
        f.setCustomValidation("PLANNED_HOURS,PLANNED_MEN,CATALOG_NO,ITEM0_CONTRACT,QTY_TO_INVOICE,SALES_PRICE,DISCOUNT,ITEM0_PRICE_AMOUNT,WORK_ORDER_INVOICE_TYPE,SLSPRTLSTPRICE","ITEM0_PRICE_AMOUNT");
        f.setDefaultNotVisible();

        f = itemblk0.addField("SALES_PRICE","Number");
        f.setSize(18);
        f.setLabel("PCMWSEPARATESTANDARDJOB2SLSPRICE: Sales Price/Unit");
        f.setCustomValidation("PLANNED_HOURS,PLANNED_MEN,CATALOG_NO,ITEM0_CONTRACT,QTY_TO_INVOICE,SALES_PRICE,DISCOUNT,ITEM0_PRICE_AMOUNT,WORK_ORDER_INVOICE_TYPE,SLSPRTLSTPRICE","ITEM0_PRICE_AMOUNT");
        f.setDefaultNotVisible();

        f = itemblk0.addField("DISCOUNT","Number");
        f.setSize(18);
        f.setLabel("PCMWSEPARATESTANDARDJOB2DSCNT: Discount");
        f.setCustomValidation("PLANNED_HOURS,PLANNED_MEN,CATALOG_NO,ITEM0_CONTRACT,QTY_TO_INVOICE,SALES_PRICE,DISCOUNT,ITEM0_PRICE_AMOUNT,WORK_ORDER_INVOICE_TYPE,SLSPRTLSTPRICE","ITEM0_PRICE_AMOUNT");
        f.setDefaultNotVisible();
        f.setHidden();

        f = itemblk0.addField("ITEM0_PRICE_AMOUNT","Money");
        f.setSize(18);
        f.setLabel("PCMWSEPARATESTANDARDJOB2PRICEAMOUNT: Price Amount");
        f.setFunction("''");
        f.setReadOnly();

        f = itemblk0.addField("ITEM0_STD_JOB_ID");
        f.setHidden();
        f.setDbName("STD_JOB_ID");
        f.setUpperCase();

        f = itemblk0.addField("ITEM0_STD_JOB_REVISION");
        f.setHidden();
        f.setDbName("STD_JOB_REVISION");
        f.setMaxLength(6);
        f.setUpperCase();   

        f = itemblk0.addField("ITEM0_STD_JOB_CONTRACT");
        f.setHidden();
        f.setDbName("STD_JOB_CONTRACT");
        f.setUpperCase();

        f = itemblk0.addField("COST1","Money");
        f.setHidden();  
        f.setFunction("''");

        f = itemblk0.addField("COST2","Money");
        f.setHidden();  
        f.setFunction("''");

        f = itemblk0.addField("DEF_CATALOG_NO");
        f.setHidden();  
        f.setFunction("''");

        itemblk0.addField("CBADDQUALIFICATION").
        setLabel("PCMWSEPARATESTANDARDJOB2CBADDQUAL: Additional Qualifications").
        setFunction("Standard_Job_Role_API.Check_Qualifications_Exist(:ITEM0_STD_JOB_ID,:ITEM0_STD_JOB_CONTRACT,:ITEM0_STD_JOB_REVISION,:ROW_NO)").
        setCheckBox("0,1").
        setReadOnly().
        setQueryable();

        itemblk0.setView("STANDARD_JOB_ROLE");
        itemblk0.defineCommand("STANDARD_JOB_ROLE_API","New__,Modify__,Remove__");
        itemblk0.setMasterBlock(headblk);

        itemset0 = itemblk0.getASPRowSet();

        itembar0 = mgr.newASPCommandBar(itemblk0);

        itembar0.enableCommand(itembar0.FIND);
        itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");
        itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
        itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
        //itembar0.defineCommand(itembar0.SAVERETURN,null,"checkItem0Fields");
        itembar0.defineCommand(itembar0.DELETE,"removeITEM0");
        
        itembar0.defineCommand(itembar0.DUPLICATEROW,"duplicateITEM0");
        itembar0.defineCommand(itembar0.SAVERETURN,"saveReturnITEM0","checkItem0Fields(-1)");
        itembar0.defineCommand(itembar0.SAVENEW,null,"checkItem0Fields(-1)");

        itembar0.addCustomCommand("additionalQualifications",mgr.translate("PCMWSEPARATESTANDARDJOB2ADDQUALIFICATIONS: Additional Qualifications..."));
        itembar0.addCustomCommand("predecessors",mgr.translate("PCMWSEPARATESTANDARDJOB2PREDECESSORS: Predecessors..."));
        itembar0.removeFromMultirowAction("additionalQualifications");

        itemtbl0 = mgr.newASPTable(itemblk0);
        itemtbl0.enableRowSelect();
        itembar0.enableMultirowAction();

        itemtbl0.setWrap();

        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

        //-------------------------------------------------------
        //   Block refers to MATERIALS tab
        //-------------------------------------------------------

        itemblk1 = mgr.newASPBlock("ITEM1");

        f = itemblk1.addField("ITEM1_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk1.addField("ITEM1_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk1.addField("ITEM1_STD_JOB_ID");
        f.setHidden();
        f.setDbName("STD_JOB_ID");
        f.setUpperCase();
        f.setMaxLength(12);

        f = itemblk1.addField("ITEM1_STD_JOB_REVISION");
        f.setHidden();
        f.setDbName("STD_JOB_REVISION");
        f.setUpperCase();
        f.setMaxLength(6);

        f = itemblk1.addField("ITEM1_STD_JOB_CONTRACT");
        f.setHidden();
        f.setDbName("STD_JOB_CONTRACT");
        f.setUpperCase();
        f.setMaxLength(12);

        f = itemblk1.addField("STD_JOB_SPARE_SEQ","Number");
        f.setHidden();   

        f = itemblk1.addField("PART_NO");
        f.setSize(15);
        f.setDynamicLOV("INVENTORY_PART_WO_LOV","SPARE_CONTRACT CONTRACT",600,450);
        f.setLOVProperty("TITLE",mgr.translate("PCMWSEPARATESTANDARDJOB2LOVTITLE0: List of Part No"));
        f.setCustomValidation("SPARE_CONTRACT,PLANNED_QTY,PART_NO,ITEM1PRTLSTPRC,ITEM1_DISCOUNT,ITEM1_SALES_PRICE,ITEM1_QTY_TO_INVOICE,ITEM1_WORK_ORDER_INVOICE_TYPE,PLANNED_QTY,ITEM1_CONTRACT,ITEM1_STD_JOB_CONTRACT","PARTDESCRIPTION,DIMQUALITY,TYPEDESIGNATION,PARTUNITCODE,ITEM1PRTCOST,ITEM1_CATALOG_NO,ITEM1CTLGDESC,ITEM1PRTLSTPRC,ITEM1_PRICE_AMOUNT,CONDITION_CODE,CONDITION_CODE_DESC,ACTIVEIND_DB");
        f.setMandatory();
        f.setLabel("PCMWSEPARATESTANDARDJOB2PARTNO: Part No");
        f.setUpperCase();
        f.setMaxLength(25);

        f = itemblk1.addField("PARTDESCRIPTION");
        f.setSize(50);
        f.setLabel("PCMWSEPARATESTANDARDJOB2PARTDESCRIPTION: Part Description");
        f.setFunction("Purchase_Part_API.Get_Description(SPARE_CONTRACT,PART_NO)");
        f.setReadOnly();

        f = itemblk1.addField("SPARE_CONTRACT");
        f.setSize(15);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
        f.setLOVProperty("TITLE",mgr.translate("PCMWSEPARATESTANDARDJOB2LOVTITLE2: List of Site"));
        f.setLabel("PCMWSEPARATESTANDARDJOB2SPARECONTRACT: Site");
        f.setUpperCase();
        f.setMaxLength(5);

        f = itemblk1.addField("SITEDESCRIPTION");
        f.setHidden();
        f.setReadOnly();
        f.setInsertable();
        f.setFunction("SITE_API.Get_Description(:SPARE_CONTRACT)");
        mgr.getASPField("SPARE_CONTRACT").setValidation("SITEDESCRIPTION");   

        f = itemblk1.addField("CONDITION_CODE");
        f.setSize(10);
        f.setUpperCase();
        f.setMaxLength(10);
        f.setDynamicLOV("CONDITION_CODE");
        f.setLabel("PCMWSEPARATESTANDARDJOB2CONDITIONCODE: Condition Code");
        f.setCustomValidation("ITEM1_CONTRACT,PART_NO,ITEM1_CATALOG_NO,PLANNED_QTY,ITEM1_DISCOUNT,ITEM1_QTY_TO_INVOICE,CONDITION_CODE","ITEM1_SALES_PRICE,CONDITION_CODE_DESC,ITEM1_PRICE_AMOUNT,ITEM1PRTCOST");

        f = itemblk1.addField("CONDITION_CODE_DESC");
        f.setSize(20);
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2CONDITIONCODEDESC: Condition Code Description");
        f.setFunction("CONDITION_CODE_API.Get_Description(:CONDITION_CODE)");

        f = itemblk1.addField("PART_OWNERSHIP");
        f.setSize(25);
        f.setInsertable();
        f.setSelectBox();
        f.enumerateValues("PART_OWNERSHIP_API");
        f.setLabel("PCMWSEPARATESTANDARDJOB2PARTOWNERSHIP: Ownership");
        f.setCustomValidation("PART_OWNERSHIP","PART_OWNERSHIP_DB");

        f = itemblk1.addField("PART_OWNERSHIP_DB");
        f.setSize(20);
        f.setHidden();

        f = itemblk1.addField("OWNER");
        f.setSize(15);
        f.setMaxLength(20);
        f.setInsertable();
        f.setLabel("PCMWSEPARATESTANDARDJOB2PARTOWNER: Owner");
        f.setCustomValidation("OWNER,PART_OWNERSHIP_DB","OWNER_NAME");
        f.setDynamicLOV("CUSTOMER_INFO");

        f = itemblk1.addField("OWNER_NAME");
        f.setSize(20);
        f.setMaxLength(100);
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2PARTOWNERNAME: Owner Name");
        f.setFunction("CUSTOMER_INFO_API.Get_Name(:OWNER)");

        f = itemblk1.addField("CRAFT_LINE_NO","Number");
        f.setSize(15);
        f.setDynamicLOV("STANDARD_JOB_ROLE","ITEM1_STD_JOB_ID STD_JOB_ID,ITEM1_STD_JOB_CONTRACT STD_JOB_CONTRACT,ITEM1_STD_JOB_REVISION STD_JOB_REVISION",600,450);
        f.setLabel("PCMWSEPARATESTANDARDJOB2CRAFTLINENO: Operation No");
        f.setInsertable();
        f.setMaxLength(15); 

        f = itemblk1.addField("DIMQUALITY");
        f.setSize(18);
        f.setLabel("PCMWSEPARATESTANDARDJOB2DIMQUALITY: Dimension/Quality");
        f.setFunction("INVENTORY_PART_API.Get_Dim_Quality(:SPARE_CONTRACT,:PART_NO)");
        f.setReadOnly();

        f = itemblk1.addField("TYPEDESIGNATION");
        f.setSize(15);
        f.setLabel("PCMWSEPARATESTANDARDJOB2TYPEDESIGNATION: Type Designation");
        f.setFunction("INVENTORY_PART_API.Get_Type_Designation(:SPARE_CONTRACT,:PART_NO)");
        f.setReadOnly();

        f = itemblk1.addField("PARTUNITCODE");
        f.setSize(18); 
        f.setLabel("PCMWSEPARATESTANDARDJOB2PARTUNITCODE: Unit");
        f.setFunction("INVENTORY_PART_API.Get_Unit_Meas(:SPARE_CONTRACT,:PART_NO)");
        f.setReadOnly();

        f = itemblk1.addField("PLANNED_QTY","Number");
        f.setSize(15);
        f.setMandatory();
        f.setLabel("PCMWSEPARATESTANDARDJOB2PLANNEDQTY: Planned Quantity");
        f.setCustomValidation("PLANNED_QTY,ITEM1_CONTRACT,ITEM1_CATALOG_NO,ITEM1PRTLSTPRC,PART_NO,ITEM1_DISCOUNT,ITEM1_SALES_PRICE,ITEM1_QTY_TO_INVOICE,ITEM1_WORK_ORDER_INVOICE_TYPE,SERIAL_NO,CONFIGURATION_ID,CONDITION_CODE","ITEM1_PRICE_AMOUNT,ITEM1PRTCOST");

        f = itemblk1.addField("ITEM1_CONTRACT");
        f.setSize(18);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
        f.setLOVProperty("TITLE",mgr.translate("PCMWSEPARATESTANDARDJOB2LOVTITLE1: List of Sales Part Site"));
        f.setLabel("PCMWSEPARATESTANDARDJOB2ITEM1CONTRACT: Sales Part Site");
        f.setDbName("CONTRACT");
        f.setUpperCase();
        f.setMaxLength(5);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_CATALOG_NO");
        f.setSize(15);
        f.setDynamicLOV("SALES_PART_ACTIVE_LOV","ITEM1_CONTRACT CONTRACT",600,450);
        f.setLabel("PCMWSEPARATESTANDARDJOB2ITEM1CATALOGNO: Sales Part No");
        f.setDbName("CATALOG_NO");
        f.setUpperCase();
        f.setCustomValidation("ITEM1_CONTRACT,ITEM1_CATALOG_NO,PART_NO,PLANNED_QTY,ITEM1_DISCOUNT,ITEM1_SALES_PRICE,ITEM1_QTY_TO_INVOICE,CONDITION_CODE","ITEM1_SALES_PRICE,ITEM1CTLGDESC,ITEM1PRTCOST,ITEM1PRTLSTPRC,ITEM1_PRICE_AMOUNT");
        f.setMaxLength(25);
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1CTLGDESC");
        f.setSize(18);
        f.setLabel("PCMWSEPARATESTANDARDJOB2ITEM1CTLGDESC: Description");
        if (mgr.isModuleInstalled("ORDER"))
            f.setFunction("SALES_PART_API.Get_Catalog_Desc(:ITEM1_CONTRACT,:ITEM1_CATALOG_NO)");
        else
            f.setFunction("''");
        f.setReadOnly();
        f.setMaxLength(35);
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1PRTCOST","Number");
        f.setSize(15);
        f.setLabel("PCMWSEPARATESTANDARDJOB2ITEM1PRTCOST: Cost");
        //f.setFunction("Inventory_Part_API.Get_Inventory_Value_By_Method(:ITEM1_CONTRACT,:PART_NO)");
        f.setFunction("Active_Separate_API.Get_Invent_Value(:ITEM1_CONTRACT,:PART_NO,NULL,'*',:CONDITION_CODE)");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1PRTLSTPRC","Number");
        f.setSize(18);
        f.setLabel("PCMWSEPARATESTANDARDJOB2ITEM1PRTLSTPRC: List Price");
        if (mgr.isModuleInstalled("ORDER"))
            f.setFunction("SALES_PART_API.Get_List_Price(:ITEM1_CONTRACT,:ITEM1_CATALOG_NO)");
        else
            f.setFunction("''");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_WORK_ORDER_INVOICE_TYPE");
        f.setSize(25);
        f.setDbName("WORK_ORDER_INVOICE_TYPE");
        f.setLabel("PCMWSEPARATESTANDARDJOB2ITEM1WRKOIT: Work Order Invoice Type");
        f.setSelectBox();
        f.enumerateValues("WORK_ORDER_INVOICE_TYPE_API");
        f.setInsertable();
        f.setCustomValidation("ITEM1_QTY_TO_INVOICE,ITEM1_CONTRACT,ITEM1_CATALOG_NO,PART_NO,PLANNED_QTY,ITEM1_DISCOUNT,ITEM1_SALES_PRICE,ITEM1_PRICE_AMOUNT,ITEM1_WORK_ORDER_INVOICE_TYPE,ITEM1PRTLSTPRC","ITEM1_PRICE_AMOUNT");
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_QTY_TO_INVOICE","Number");
        f.setSize(18);
        f.setDbName("QTY_TO_INVOICE");
        f.setLabel("PCMWSEPARATESTANDARDJOB2ITEM1QTYINV: Qty To Invoice");
        f.setCustomValidation("ITEM1_QTY_TO_INVOICE,ITEM1_CONTRACT,ITEM1_CATALOG_NO,PART_NO,PLANNED_QTY,ITEM1_DISCOUNT,ITEM1_SALES_PRICE,ITEM1_PRICE_AMOUNT,ITEM1_WORK_ORDER_INVOICE_TYPE,ITEM1PRTLSTPRC","ITEM1_PRICE_AMOUNT");
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_SALES_PRICE","Number");
        f.setSize(15);
        f.setDbName("SALES_PRICE");
        f.setLabel("PCMWSEPARATESTANDARDJOB2ITEM1SLSPRICE: Sales Price/Unit");
        f.setCustomValidation("ITEM1_SALES_PRICE,ITEM1_QTY_TO_INVOICE,ITEM1_CONTRACT,ITEM1_CATALOG_NO,PART_NO,PLANNED_QTY,ITEM1_DISCOUNT,ITEM1_PRICE_AMOUNT,ITEM1_WORK_ORDER_INVOICE_TYPE,ITEM1PRTLSTPRC","ITEM1_PRICE_AMOUNT");
        f.setDefaultNotVisible();

        f = itemblk1.addField("ITEM1_DISCOUNT","Number");
        f.setSize(18);
        f.setDbName("DISCOUNT");
        f.setLabel("PCMWSEPARATESTANDARDJOB2ITEM1DSCNT: Discount");
        f.setCustomValidation("ITEM1_DISCOUNT,ITEM1_CATALOG_NO,ITEM1_PRICE_AMOUNT,ITEM1_QTY_TO_INVOICE,ITEM1_CONTRACT,ITEM1_SALES_PRICE,PART_NO,PLANNED_QTY,,ITEM1_WORK_ORDER_INVOICE_TYPE,ITEM1PRTLSTPRC","ITEM1_PRICE_AMOUNT");
        f.setDefaultNotVisible();
        f.setHidden();

        f = itemblk1.addField("ITEM1_PRICE_AMOUNT","Money");
        f.setLabel("PCMWSEPARATESTANDARDJOB2PRICEAMOUNT: Price Amount");
        f.setReadOnly(); 
        f.setFunction("''");

        f = itemblk1.addField("SUPPLY_CODE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("VENDOR");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("PRICE_DATE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("SUPPLIER");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("INVENTORYPART");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("COND_CODE_USAGE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("CAT_EXIST","Number");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("PROJECT_ID");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("SERIAL_NO");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("CONFIGURATION_ID");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("ACTIVEIND");
        f.setHidden();
        f.setFunction("''");

        f = itemblk1.addField("ACTIVEIND_DB");
        f.setHidden();
        f.setFunction("''");

        itemblk1.setView("STANDARD_JOB_SPARE");
        itemblk1.defineCommand("STANDARD_JOB_SPARE_API","New__,Modify__,Remove__");
        itemblk1.setMasterBlock(headblk);

        itemset1 = itemblk1.getASPRowSet();

        itembar1 = mgr.newASPCommandBar(itemblk1);

        itemtbl1 = mgr.newASPTable(itemblk1);
        itemtbl1.setWrap();

        //Web Alignment - Multirow Action
        itemtbl1.enableRowSelect();
        
        itembar1.enableCommand(itembar1.FIND);
        itembar1.enableCommand(itembar1.NEWROW);
        itembar1.enableCommand(itembar1.SAVERETURN);

        itembar1.addCustomCommand("none",""); 
        if (mgr.isPresentationObjectInstalled("invenw/InventoryPartInventoryPartCurrentOnHand.page"))
            itembar1.addCustomCommand("qtyOnHand",mgr.translate("PCMWSEPARATESTANDARDJOB2CURRQTY: Current Quantity on Hand..."));
        if (mgr.isPresentationObjectInstalled("invenw/InventoryPartAvailabilityPlanningQry.page"))
            itembar1.addCustomCommand("availibilityDet",mgr.translate("PCMWSEPARATESTANDARDJOB2AVAILDET: Query - Inventory Part Availability Planning..."));
        if (mgr.isPresentationObjectInstalled("invenw/InventoryPart.page"))
            itembar1.addCustomCommand("inventoryPart",mgr.translate("PCMWSEPARATESTANDARDJOB2INVENPART: Inventory Part..."));
        if (mgr.isPresentationObjectInstalled("purchw/PurchasePartSupplier.page"))
            itembar1.addCustomCommand("suppPerPart",mgr.translate("PCMWSEPARATESTANDARDJOB2SUPPPART: Supplier per Part..."));

        //Web Alignment - Enable Multirow Action
        itembar1.enableMultirowAction();
        itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1");
        itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
        itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
        //itembar1.defineCommand(itembar1.SAVERETURN,null,"checkItem1Fields");
        itembar1.defineCommand(itembar1.DELETE,"removeITEM1"); 
        
        itembar1.defineCommand(itembar1.SAVERETURN,"saveReturnITEM1","checkItem1Fields(-1)");
        itembar1.defineCommand(itembar1.SAVENEW,null,"checkItem1Fields(-1)");

        itemlay1 = itemblk1.getASPBlockLayout();
        itemlay1.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

        //-------------------------------------------------------
        //   Block refers to PLANNING tab
        //-------------------------------------------------------

        itemblk2 = mgr.newASPBlock("ITEM2");

        f = itemblk2.addField("ITEM2_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk2.addField("ITEM2_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk2.addField("ITEM2_STD_JOB_ID");
        f.setHidden();
        f.setDbName("STD_JOB_ID");   

        f = itemblk2.addField("ITEM2_STD_JOB_REVISION");
        f.setHidden();
        f.setMaxLength(6);
        f.setDbName("STD_JOB_REVISION");

        f = itemblk2.addField("ITEM2_STD_JOB_CONTRACT");
        f.setHidden();
        f.setDbName("STD_JOB_CONTRACT");

        f = itemblk2.addField("ITEM2_ROW_NO","Number");
        f.setSize(10);
        f.setLabel("PCMWSEPARATESTANDARDJOB2IT2RNO: Row No");
        f.setDbName("ROW_NO");
        f.setReadOnly();
        f.setInsertable();

        f = itemblk2.addField("WORK_ORDER_COST_TYPE");
        f.setSize(22);
        f.setCustomValidation("WORK_ORDER_COST_TYPE","PERS,METR");
        f.setMandatory();
        f.enumerateValues("WORK_ORDER_COST_TYPE_API");
        f.setLabel("PCMWSEPARATESTANDARDJOB2WOORCOTY: Work Order Cost Type");
        f.setSelectBox();
        f.unsetSearchOnDbColumn();

        f = itemblk2.addField("ITEM2_DUMMY");
        f.setHidden();
        f.setFunction("''");

        f = itemblk2.addField("PERS");
        f.setHidden();
        f.setFunction("''");

        f = itemblk2.addField("METR");
        f.setHidden();
        f.setFunction("''");

        f = itemblk2.addField("ASREP");
        f.setHidden();
        f.setFunction("''");

        f = itemblk2.addField("ITEM2_WORK_ORDER_INVOICE_TYPE");
        f.setDbName("WORK_ORDER_INVOICE_TYPE");
        f.setLabel("PCMWSEPARATESTANDARDJOB2WOORINTY: Work Order Invoice Type");
        f.setSize(25);
        f.setSelectBox();
        f.enumerateValues("WORK_ORDER_INVOICE_TYPE_API");
        f.setMandatory();
        f.unsetSearchOnDbColumn();
        f.setCustomValidation("ITEM2_SALES_PRICE,ITEM2_QTY_TO_INVOICE,CATALOG_CONTRACT,ITEM2_CATALOG_NO,QUANTITY,ITEM2_DISCOUNT,ITEM2_PRICE_AMOUNT,ITEM2_WORK_ORDER_INVOICE_TYPE","ITEM2_PRICE_AMOUNT");

        f = itemblk2.addField("QUANTITY","Number");
        f.setSize(18);
        f.setLabel("PCMWSEPARATESTANDARDJOB2PLAQTY: Planned Quantity");
        f.setCustomValidation("ITEM2_SALES_PRICE,ITEM2_QTY_TO_INVOICE,CATALOG_CONTRACT,ITEM2_CATALOG_NO,QUANTITY,ITEM2_DISCOUNT,ITEM2_PRICE_AMOUNT,ITEM2_WORK_ORDER_INVOICE_TYPE","ITEM2_PRICE_AMOUNT");

        f = itemblk2.addField("COST","Number");
        f.setSize(15);
        f.setLabel("PCMWSEPARATESTANDARDJOB2PLACO: Planned Cost");
        f.setCustomValidation("COST,WORK_ORDER_COST_TYPE,QUANTITY,ITEM2_CATALOG_NO","QUANTITY"); 

        f = itemblk2.addField("ITEM2_QTY_TO_INVOICE","Number");
        f.setDbName("QTY_TO_INVOICE");
        f.setCustomValidation("ITEM2_SALES_PRICE,ITEM2_QTY_TO_INVOICE,CATALOG_CONTRACT,ITEM2_CATALOG_NO,QUANTITY,ITEM2_DISCOUNT,ITEM2_PRICE_AMOUNT,ITEM2_WORK_ORDER_INVOICE_TYPE","ITEM2_PRICE_AMOUNT");
        f.setSize(18);
        f.setLabel("PCMWSEPARATESTANDARDJOB2QTYTINV: Qty To Invoice");

        f = itemblk2.addField("CATALOG_CONTRACT");
        f.setSize(20);
        f.setLabel("PCMWSEPARATESTANDARDJOB2CATCON: Sales Part Site");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk2.addField("ITEM2_CATALOG_NO");
        f.setSize(15);
        f.setLabel("PCMWSEPARATESTANDARDJOB2CATNO: Sales Part");
        f.setDbName("CATALOG_NO");
        f.setDynamicLOV("SALES_PART_ACTIVE_LOV","CATALOG_CONTRACT CONTRACT",600,450); 
        f.setUpperCase();
        f.setCustomValidation("ITEM2_STD_JOB_ID,ITEM2_STD_JOB_REVISION,ITEM2_STD_JOB_CONTRACT,ITEM2_ROW_NO,ITEM2_SALES_PRICE,ITEM2_QTY_TO_INVOICE,CATALOG_CONTRACT,ITEM2_CATALOG_NO,QUANTITY,ITEM2_DISCOUNT,ITEM2_PRICE_AMOUNT,ITEM2_WORK_ORDER_INVOICE_TYPE,WORK_ORDER_COST_TYPE","ITEM2_SALES_PRICE,COST,ITEM2_PRICE_AMOUNT,ITEM2_SALES_PART_DESC,LIST_PRICE,QUANTITY");
        f.setDefaultNotVisible();

        f = itemblk2.addField("ITEM2_SALES_PART_DESC");
        f.setFunction("SALES_PART_API.Get_Catalog_Desc(:CATALOG_CONTRACT,:ITEM2_CATALOG_NO)");
        f.setSize(25);
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2SALESPARTDESC: Sales Part Description");
        f.setDefaultNotVisible();

        f = itemblk2.addField("LIST_PRICE","Number");
        f.setFunction("SALES_PART_API.Get_List_Price(:CATALOG_CONTRACT,:ITEM2_CATALOG_NO)");
        f.setReadOnly();
        f.setQueryable();
        f.setLabel("PCMWSEPARATESTANDARDJOB2LISTPRICE: List Price");

        f = itemblk2.addField("ITEM2_SALES_PRICE","Number");
        f.setDbName("SALES_PRICE");
        f.setSize(18);
        f.setLabel("PCMWSEPARATESTANDARDJOB2SAPR: Sales Price/Unit");
        f.setCustomValidation("ITEM2_SALES_PRICE,ITEM2_QTY_TO_INVOICE,CATALOG_CONTRACT,ITEM2_CATALOG_NO,QUANTITY,ITEM2_DISCOUNT,ITEM2_PRICE_AMOUNT,ITEM2_WORK_ORDER_INVOICE_TYPE","ITEM2_PRICE_AMOUNT");
        f.setDefaultNotVisible();

        f = itemblk2.addField("ITEM2_PRICE_AMOUNT","Money");
        f.setLabel("PCMWSEPARATESTANDARDJOB2ITEM1SALESPRAMU: Sales Price/Amount");
        f.setReadOnly(); 
        f.setFunction("''");

        f = itemblk2.addField("ITEM2_DISCOUNT");
        f.setDbName("DISCOUNT");
        f.setSize(10);
        f.setLabel("PCMWSEPARATESTANDARDJOB2DISC: Discount");
        f.setCustomValidation("ITEM2_SALES_PRICE,ITEM2_QTY_TO_INVOICE,CATALOG_CONTRACT,ITEM2_CATALOG_NO,QUANTITY,ITEM2_DISCOUNT,ITEM2_PRICE_AMOUNT,ITEM2_WORK_ORDER_INVOICE_TYPE","ITEM2_PRICE_AMOUNT");
        f.setDefaultNotVisible();
        f.setHidden();

        f = itemblk2.addField("IS_AUTO_LINE","Number");
        f.setFunction("Standard_Job_Planning_API.Is_Auto_External_Line(:ITEM2_STD_JOB_ID,:ITEM2_STD_JOB_CONTRACT,:ITEM2_STD_JOB_REVISION,:ITEM2_ROW_NO)");
        f.setHidden();

        itemblk2.setView("STANDARD_JOB_PLANNING");
        itemblk2.defineCommand("STANDARD_JOB_PLANNING_API","New__,Modify__,Remove__");
        itemblk2.setMasterBlock(headblk);

        itemset2 = itemblk2.getASPRowSet();

        itembar2 = mgr.newASPCommandBar(itemblk2);

        itembar2.enableCommand(itembar2.FIND);
        itembar2.defineCommand(itembar2.NEWROW,"newRowITEM2");
        itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
        itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");
        itembar2.defineCommand(itembar2.SAVERETURN,null,"checkItem2Fields");
        itembar2.defineCommand(itembar2.DELETE,"removeITEM2");

        itembar2.defineCommand(itembar2.SAVERETURN,"saveReturnITEM2","checkItem2Fields(-1)");
        itembar2.defineCommand(itembar2.SAVENEW,null,"checkItem2Fields(-1)");

        itemtbl2 = mgr.newASPTable(itemblk2);
        itemtbl2.setWrap();
        itemlay2 = itemblk2.getASPBlockLayout();
        itemlay2.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

        //-------------------------------------------------------
        //   Block refers to TOOLS & FACILITIES tab
        //-------------------------------------------------------

        itemblk3 = mgr.newASPBlock("ITEM3");

        f = itemblk3.addField("ITEM3_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk3.addField("ITEM3_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk3.addField("ITEM3_STD_JOB_ID");
        f.setHidden();
        f.setDbName("STD_JOB_ID");

        f = itemblk3.addField("ITEM3_STD_JOB_REVISION");
        f.setHidden();
        f.setDbName("STD_JOB_REVISION");
        f.setMaxLength(6);

        f = itemblk3.addField("ITEM3_STD_JOB_CONTRACT");
        f.setHidden();
        f.setDbName("STD_JOB_CONTRACT");

        f = itemblk3.addField("TOOL_FACILITY_ROW_NO","Number");
        f.setSize(10);
        f.setLabel("PCMWSEPARATESTANDARDJOB2IT3RNO: Row No");
        f.setReadOnly();

        f = itemblk3.addField("TOOL_FACILITY_ID");
        f.setSize(20);
        f.setMaxLength(40);
        f.setUpperCase();
        f.setDynamicLOV("CONNECT_TOOLS_FACILITIES_LOV3");
        f.setCustomValidation("TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,TOOL_FACILITY_TYPE_DESC,QTY,ITEM3_CONTRACT,ITEM3_ORG_CODE,ITEM3_STD_JOB_ID,ITEM3_STD_JOB_REVISION,ITEM3_STD_JOB_CONTRACT,PLANNED_HOUR,ITEM3_DISCOUNT,ITEM3_SALES_PRICE","TOOL_FACILITY_DESC,TOOL_FACILITY_TYPE,TOOL_FACILITY_TYPE_DESC,QTY,ITEM3_COST,COST_CURRENCY,NOTE,ITEM3_SALES_PART,SALES_PART_DESC,ITEM3_LIST_PRICE,PRICE_CURRENCY,PRICE_AMOUNT");
        f.setLabel("PCMWSEPARATESTANDARDJOB2TOOLFACILITYID: Tool/Facility Id");

        f = itemblk3.addField("TOOL_FACILITY_DESC");
        f.setFunction("Tool_Facility_API.Get_Tool_Facility_Description(:TOOL_FACILITY_ID)");
        f.setLabel("PCMWSEPARATESTANDARDJOB2TOOLFACILITYDESC: Tool/Facility Description");
        f.setReadOnly();
        f.setSize(25);

        f = itemblk3.addField("TOOL_FACILITY_TYPE");
        f.setSize(20);
        f.setMaxLength(40);
        f.setMandatory();
        f.setUpperCase();
        f.setDynamicLOV("TOOL_FACILITY_TYPE");
        f.setCustomValidation("TOOL_FACILITY_ID,TOOL_FACILITY_TYPE,ITEM3_STD_JOB_ID,ITEM3_STD_JOB_REVISION,ITEM3_STD_JOB_CONTRACT,ITEM3_COST,COST_CURRENCY","TOOL_FACILITY_TYPE_DESC,ITEM3_COST,COST_CURRENCY");
        f.setLabel("PCMWSEPARATESTANDARDJOB2TOOLFACILITYTYPE: Tool/Facility Type");

        f = itemblk3.addField("TOOL_FACILITY_TYPE_DESC");
        f.setFunction("Tool_Facility_Type_API.Get_Type_Description(:TOOL_FACILITY_TYPE)");
        f.setSize(25);
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2TOOLFACILITYTYPEDESC: Type Description");

        f = itemblk3.addField("ITEM3_CONTRACT");
        f.setDbName("CONTRACT");
        f.setSize(10);
        f.setMaxLength(5);
        f.setDynamicLOV("CONNECT_TOOLS_FACILITIES_SITE","TOOL_FACILITY_ID ITEM3_ORG_CODE");
        f.setCustomValidation("ITEM3_CONTRACT,TOOL_FACILITY_ID,ITEM3_ORG_CODE,NOTE,QTY,PLANNED_HOUR,ITEM3_DISCOUNT,ITEM3_SALES_PART,SALES_PART_SITE,SALES_PART_DESC,ITEM3_SALES_PRICE,PRICE_CURRENCY,PRICE_AMOUNT","NOTE,QTY,ITEM3_SALES_PART,SALES_PART_DESC,ITEM3_LIST_PRICE,PRICE_CURRENCY,PRICE_AMOUNT");
        f.setLabel("PCMWSEPARATESTANDARDJOB2ITEM3CONTRACT: Site");

        f = itemblk3.addField("ITEM3_ORG_CODE");
        f.setDbName("ORG_CODE");
        f.setSize(15);
        f.setMaxLength(8);
        f.setUpperCase();
        f.setDynamicLOV("CONNECT_TOOLS_FACILITIES_ORG","TOOL_FACILITY_ID ITEM3_CONTRACT");
        f.setCustomValidation("ITEM3_ORG_CODE,ITEM3_CONTRACT,TOOL_FACILITY_ID,NOTE,QTY,PLANNED_HOUR,ITEM3_DISCOUNT,ITEM3_SALES_PART,SALES_PART_SITE,SALES_PART_DESC,ITEM3_SALES_PRICE,PRICE_CURRENCY,PRICE_AMOUNT","NOTE,QTY,ITEM3_SALES_PART,SALES_PART_DESC,ITEM3_LIST_PRICE,PRICE_CURRENCY,PRICE_AMOUNT");
        f.setLabel("PCMWSEPARATESTANDARDJOB2ITEM3ORGCODE: Maintenance Organization");

        f = itemblk3.addField("QTY","Number");
        f.setSize(12);
        f.setCustomValidation("QTY,PLANNED_HOUR,ITEM3_DISCOUNT,ITEM3_SALES_PART,SALES_PART_SITE,ITEM3_SALES_PRICE,PRICE_AMOUNT,ITEM3_LIST_PRICE","PRICE_AMOUNT");
        f.setLabel("PCMWSEPARATESTANDARDJOB2QTY: Quantity");   

        f = itemblk3.addField("QTY_DUMMY","Number");
        f.setFunction("''");
        f.setHidden();

        f = itemblk3.addField("PLANNED_HOUR","Number");
        f.setSize(15);
        f.setCustomValidation("QTY,PLANNED_HOUR,ITEM3_DISCOUNT,ITEM3_SALES_PART,SALES_PART_SITE,ITEM3_SALES_PRICE,PRICE_AMOUNT,ITEM3_LIST_PRICE","PRICE_AMOUNT");
        f.setLabel("PCMWSEPARATESTANDARDJOB2PLANNEDHOUR: Planned Hours");

        f = itemblk3.addField("OPERATION_NO","Number");
        f.setLabel("PCMWSEPARATESTANDARDJOB2OPERATIONNO: Operation No");
        f.setDynamicLOV("STANDARD_JOB_ROLE","ITEM3_STD_JOB_ID STD_JOB_ID,ITEM3_STD_JOB_CONTRACT STD_JOB_CONTRACT,ITEM3_STD_JOB_REVISION STD_JOB_REVISION");
        f.setSize(15);
        f.setUpperCase();
        f.setDefaultNotVisible();

        f = itemblk3.addField("ITEM3_COST","Number");
        f.setDbName("COST");
        f.setReadOnly();
        f.setSize(10);
        f.setLabel("PCMWSEPARATESTANDARDJOB2ITEM3COST: Cost");

        f = itemblk3.addField("COST_CURRENCY");
        f.setSize(10);
        f.setReadOnly();
        f.setHidden();
        f.setDynamicLOV("ISO_CURRENCY");
        f.setLabel("PCMWSEPARATESTANDARDJOB2COSTCURRENCY: Cost Currency");

        f = itemblk3.addField("SALES_PART_SITE");     
        f.setSize(10);
        f.setMaxLength(5);
        f.setUpperCase();
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV");
        f.setLabel("PCMWSEPARATESTANDARDJOB2SALESPARTSITE: Sales Part Site");
        f.setDefaultNotVisible();
        f.setReadOnly();

        f = itemblk3.addField("ITEM3_SALES_PART");
        f.setDbName("SALES_PART");
        f.setSize(10);
        f.setMaxLength(25);
        f.setUpperCase();
        f.setCustomValidation("ITEM3_SALES_PART,SALES_PART_SITE,QTY,PLANNED_HOUR,ITEM3_DISCOUNT,ITEM3_SALES_PRICE","SALES_PART_DESC,ITEM3_LIST_PRICE,PRICE_CURRENCY,PRICE_AMOUNT");
        f.setDynamicLOV("SALES_PART_SERVICE_LOV","SALES_PART_SITE");
        f.setLabel("PCMWSEPARATESTANDARDJOB2ITEM3SALESPART: Sales Part");
        f.setDefaultNotVisible();

        f = itemblk3.addField("SALES_PART_DESC");
        f.setFunction("Connect_Tools_Facilities_API.Get_Sales_Part_Desc(:SALES_PART_SITE,:ITEM3_SALES_PART)");
        f.setSize(25);
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2SALESPARTDESC: Sales Part Description");
        f.setDefaultNotVisible();

        f = itemblk3.addField("ITEM3_LIST_PRICE","Number");
        f.setSize(10);
        f.setLabel("PCMWSEPARATESTANDARDJOB2ITEM3LISTPRICE: List Price");
        if (mgr.isModuleInstalled("ORDER"))
            f.setFunction("SALES_PART_API.Get_List_Price(:SALES_PART_SITE,:ITEM3_SALES_PART)");
        else
            f.setFunction("''");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk3.addField("ITEM3_SALES_PRICE","Number");
        f.setDbName("SALES_PRICE");
        f.setSize(10);
        f.setLabel("PCMWSEPARATESTANDARDJOB2ITEM3SALESPRICE: Sales Price/Unit");
        f.setCustomValidation("ITEM3_SALES_PRICE,ITEM3_LIST_PRICE,PLANNED_HOUR,QTY,SALES_PART_SITE,ITEM3_SALES_PART,ITEM3_DISCOUNT","PRICE_AMOUNT");
        f.setDefaultNotVisible();

        f = itemblk3.addField("PRICE_CURRENCY");
        f.setReadOnly();
        f.setHidden();
        f.setDynamicLOV("ISO_CURRENCY");
        f.setLabel("PCMWSEPARATESTANDARDJOB2PRICECURRENCY: Price Currency");

        f = itemblk3.addField("ITEM3_DISCOUNT","Number");
        f.setDbName("DISCOUNT");
        f.setSize(10);               
        f.setCustomValidation("QTY,PLANNED_HOUR,ITEM3_DISCOUNT,ITEM3_SALES_PART,SALES_PART_SITE,ITEM3_SALES_PRICE,PRICE_AMOUNT,ITEM3_LIST_PRICE","PRICE_AMOUNT");
        f.setLabel("PCMWSEPARATESTANDARDJOB2ITEM3DISCOUNT: Discount");
        f.setDefaultNotVisible();
        f.setHidden();

        f = itemblk3.addField("ITEM3_WORK_ORDER_INVOICE_TYPE");
        f.setDbName("WORK_ORDER_INVOICE_TYPE");
        f.setLabel("PCMWSEPARATESTANDARDJOB2WOORINTY: Work Order Invoice Type");
        f.setSize(25);
        f.setSelectBox();
        f.enumerateValues("WORK_ORDER_INVOICE_TYPE_API");
        f.setMandatory();
        f.unsetSearchOnDbColumn();

        f = itemblk3.addField("PRICE_AMOUNT","Number");
        f.setFunction("Standard_Job_Tool_Facility_API.Calculated_Price_Amount(:ITEM3_SALES_PART,:SALES_PART_SITE,:QTY,:PLANNED_HOUR)");
        f.setSize(10);
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2PRICEAMOUNT: Price Amount");

        f = itemblk3.addField("NOTE");
        f.setSize(10);
        f.setMaxLength(2000);
        f.setLabel("PCMWSEPARATESTANDARDJOB2NOTE: Note");
        f.setDefaultNotVisible();

        itemblk3.setView("STANDARD_JOB_TOOL_FACILITY");
        itemblk3.defineCommand("STANDARD_JOB_TOOL_FACILITY_API","New__,Modify__,Remove__");
        itemblk3.setMasterBlock(headblk);

        itemset3 = itemblk3.getASPRowSet();

        itembar3 = mgr.newASPCommandBar(itemblk3);

        itembar3.enableCommand(itembar3.FIND);
        itembar3.defineCommand(itembar3.NEWROW,"newRowITEM3");
        itembar3.defineCommand(itembar3.COUNTFIND,"countFindITEM3");
        itembar3.defineCommand(itembar3.OKFIND,"okFindITEM3");
        itembar3.defineCommand(itembar3.SAVERETURN,null,"checkItem3Fields");
        itembar3.defineCommand(itembar3.DELETE,"removeITEM3");

        itembar3.defineCommand(itembar3.SAVERETURN,"saveReturnITEM3","checkItem3Fields(-1)");
        //itembar3.defineCommand(itembar3.SAVENEW,null,"checkItem3Fields(-1)");

        itemtbl3 = mgr.newASPTable(itemblk3);
        itemtbl3.setWrap();
        itemlay3 = itemblk3.getASPBlockLayout();
        itemlay3.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        itemlay3.setFieldOrder("TOOL_FACILITY_ROW_NO,ITEM3_CONTRACT");

        //-----------------------------------------------------------------------
        //---------------------------  Prepare Tab  -----------------------------
        //-----------------------------------------------------------------------

        itemblk4 = mgr.newASPBlock("ITEM4");

        f = itemblk4.addField("ITEM4_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk4.addField("ITEM4_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk4.addField("JOB_CATEGORY");
        f.setSize(50);
        f.setMandatory();
        f.setInsertable();
        f.setSelectBox();
        f.enumerateValues("JOB_CATEGORY_API");
        f.setLabel("PCMWSEPARATESTANDARDJOB2ITEM4CATCL: Category");
        f.setMaxLength(200);
        f.setCustomValidation("JOB_CATEGORY,IDENTITY","JOB_CATEGORY_DB,ITEM4_DESCRIPTION");

        f = itemblk4.addField("JOB_CATEGORY_DB");
        f.setMaxLength(20);     
        f.setHidden();

        f = itemblk4.addField("IDENTITY");
        f.setSize(20); 
        f.setMaxLength(50);
        f.setDynamicLOV("TYPE_DESIGNATION",600,450);
        f.setLabel("PCMWSEPARATESTANDARDJOB2ITEM4IDENT: Identity");
        f.setReadOnly();
        f.setMandatory();
        f.setInsertable();

        f = itemblk4.addField("ITEM4_DESCRIPTION");
        f.setFunction("STD_JOB_CATEGORY_API.Get_Description(:JOB_CATEGORY_DB,:IDENTITY)");
        f.setMaxLength(200);  
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2ITEM4DECR: Description");
        mgr.getASPField("IDENTITY").setValidation("STD_JOB_CATEGORY_API.Get_Description","JOB_CATEGORY_DB,IDENTITY","ITEM4_DESCRIPTION");

        f = itemblk4.addField("ITEM4_STD_JOB_ID");
        f.setHidden();
        f.setDbName("STD_JOB_ID");
        f.setUpperCase();

        f = itemblk4.addField("ITEM4_STD_JOB_REVISION");
        f.setHidden();
        f.setDbName("STD_JOB_REVISION");
        f.setMaxLength(6);
        f.setUpperCase();   

        f = itemblk4.addField("ITEM4_STD_JOB_CONTRACT");
        f.setHidden();
        f.setDbName("STD_JOB_CONTRACT");
        f.setUpperCase();

        itemblk4.setView("STD_JOB_CATEGORY");
        itemblk4.defineCommand("STD_JOB_CATEGORY_API","New__,Modify__,Remove__");
        itemblk4.setMasterBlock(headblk);

        itemset4 = itemblk4.getASPRowSet();

        itembar4 = mgr.newASPCommandBar(itemblk4);

        itembar4.enableCommand(itembar4.FIND);
        itembar4.defineCommand(itembar4.NEWROW,"newRowITEM4");
        itembar4.defineCommand(itembar4.COUNTFIND,"countFindITEM4");
        itembar4.defineCommand(itembar4.OKFIND,"okFindITEM4");

        itemtbl4 = mgr.newASPTable(itemblk4);
        itemtbl4.setWrap();

        itemlay4 = itemblk4.getASPBlockLayout();
        itemlay4.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

        //================================================================
        // Permit
        //================================================================ 

        itemblk5 = mgr.newASPBlock("ITEM5");

        f = itemblk5.addField("ITEM5_OBJID");
        f.setHidden();
        f.setDbName("OBJID");       

        f = itemblk5.addField("ITEM5_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk5.addField("ITEM5_STD_JOB_ID");
        f.setHidden();
        f.setDbName("STD_JOB_ID");
        f.setUpperCase();

        f = itemblk5.addField("ITEM5_STD_JOB_REVISION");
        f.setHidden();
        f.setDbName("STD_JOB_REVISION");
        f.setMaxLength(6);
        f.setUpperCase();   

        f = itemblk5.addField("ITEM5_STD_JOB_CONTRACT");
        f.setHidden();
        f.setDbName("STD_JOB_CONTRACT");
        f.setUpperCase();

        f = itemblk5.addField("PERMIT_TYPE_ID");
        f.setSize(10);
        f.setDynamicLOV("PERMIT_TYPE",600,445);
        f.setMandatory();
        f.setLabel("PCMWSEPSTDJOBPERTYPE: Permit Type");
        f.setUpperCase();
        f.setMaxLength(4);
        f.setReadOnly();
        f.setInsertable();

        f = itemblk5.addField("PERMITTYPEDESCRIPTION");
        f.setSize(51);
        f.setLabel("PCMWSEPSTDJOBPERTYPEDESC: Description");
        f.setFunction("PERMIT_TYPE_API.Get_Description(:PERMIT_TYPE_ID)");
        mgr.getASPField("PERMIT_TYPE_ID").setValidation("PERMITTYPEDESCRIPTION");
        f.setReadOnly();

        f = itemblk5.addField("DELIMITATION_ID","Number");
        f.setSize(16);
        f.setDynamicLOV("CONN_DELIMITATION",600,445);
        f.setLabel("PCMWSEPSTDJOBISOLID: Isolation ID");


        f = itemblk5.addField("GENERATE");
        f.setSize(10);
        f.setLabel("PCMWSTDGENERATE: Generate");
        f.setCheckBox("FALSE,TRUE");

        itemblk5.setView("STANDARD_JOB_PERMIT");
        itemblk5.defineCommand("STANDARD_JOB_PERMIT_API","New__,Modify__,Remove__");
        itemblk5.setMasterBlock(headblk);

        itemset5 = itemblk5.getASPRowSet();

        itembar5 = mgr.newASPCommandBar(itemblk5);

        itemtbl5 = mgr.newASPTable(itemblk5);
        itemtbl5.setWrap();
        itemtbl5.enableRowSelect();

        itembar5.defineCommand(itembar5.OKFIND,"okFindITEM5");
        itembar5.defineCommand(itembar5.COUNTFIND,"countFindITEM5");
        itembar5.defineCommand(itembar5.NEWROW,"newRowITEM5");
        itembar5.enableCommand(itembar5.FIND);

        itembar5.addCustomCommand("attributes",mgr.translate("PCMWSEPSTDJOBATTRRMB: Attributes..."));

        itembar5.enableMultirowAction();

        itemlay5 = itemblk5.getASPBlockLayout();
        itemlay5.setDialogColumns(2);
        itemlay5.setDefaultLayoutMode(itemlay5.MULTIROW_LAYOUT);

        //================================================================
        // Budget
        //================================================================= 

        itemblk6 = mgr.newASPBlock("ITEM6");

        f = itemblk6.addField("ITEM6_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk6.addField("ITEM6_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk6.addField("EDIT");
        f.setReadOnly();
        f.setFunction("''");
        f.setUpperCase();

        f = itemblk6.addField("ITEM6_STD_JOB_ID");
        f.setHidden();
        f.setDbName("STD_JOB_ID");
        f.setUpperCase();

        f = itemblk6.addField("ITEM6_STD_JOB_REVISION");
        f.setHidden();
        f.setDbName("STD_JOB_REVISION");
        f.setMaxLength(6);
        f.setUpperCase();   

        f = itemblk6.addField("ITEM6_STD_JOB_CONTRACT");
        f.setHidden();
        f.setDbName("STD_JOB_CONTRACT");
        f.setUpperCase();

        f = itemblk6.addField("PERS_BUDGET_COST","Money");   
        f.setSize(14);
        f.setCustomValidation("PERS_BUDGET_COST,MAT_BUDGET_COST,EXT_BUDGET_COST,TF_BUDGET_COST,EXP_BUDGET_COST,FIX_BUDGET_COST", "TOT_BUDGET_COST");
        f.setHidden();

        f = itemblk6.addField("MAT_BUDGET_COST","Money");   
        f.setSize(14);
        f.setCustomValidation("PERS_BUDGET_COST,MAT_BUDGET_COST,EXT_BUDGET_COST,TF_BUDGET_COST,EXP_BUDGET_COST,FIX_BUDGET_COST", "TOT_BUDGET_COST");
        f.setHidden();

        f = itemblk6.addField("TF_BUDGET_COST","Money");   
        f.setSize(14);
        f.setCustomValidation("PERS_BUDGET_COST,MAT_BUDGET_COST,EXT_BUDGET_COST,TF_BUDGET_COST,EXP_BUDGET_COST,FIX_BUDGET_COST", "TOT_BUDGET_COST");
        f.setHidden();

        f = itemblk6.addField("EXT_BUDGET_COST","Money");   
        f.setSize(14);
        f.setCustomValidation("PERS_BUDGET_COST,MAT_BUDGET_COST,EXT_BUDGET_COST,TF_BUDGET_COST,EXP_BUDGET_COST,FIX_BUDGET_COST", "TOT_BUDGET_COST");
        f.setHidden();

        f = itemblk6.addField("EXP_BUDGET_COST","Money");   
        f.setSize(14);
        f.setCustomValidation("PERS_BUDGET_COST,MAT_BUDGET_COST,EXT_BUDGET_COST,TF_BUDGET_COST,EXP_BUDGET_COST,FIX_BUDGET_COST", "TOT_BUDGET_COST");
        f.setHidden();

        f = itemblk6.addField("FIX_BUDGET_COST","Money");   
        f.setSize(14);
        f.setCustomValidation("PERS_BUDGET_COST,MAT_BUDGET_COST,EXT_BUDGET_COST,TF_BUDGET_COST,EXP_BUDGET_COST,FIX_BUDGET_COST", "TOT_BUDGET_COST");
        f.setHidden();

        f = itemblk6.addField("TOT_BUDGET_COST","Money");   
        f.setFunction("nvl(PERS_BUDGET_COST,0)+nvl(MAT_BUDGET_COST,0)+nvl(EXT_BUDGET_COST,0)+nvl(TF_BUDGET_COST,0)+nvl(EXP_BUDGET_COST,0)+nvl(FIX_BUDGET_COST,0)");
        f.setSize(14);
        f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("PLAN_PERS_COST","Money"); 
        f.setFunction("''");  
        f.setSize(14);
        f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("PLAN_MAT_COST","Money");   
        f.setFunction("''");
        f.setSize(14);
        f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("PLAN_TOOL_COST","Money");   
        f.setFunction("''");
        f.setSize(14);
        f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("PLAN_EXT_COST","Money");   
        f.setFunction("''");
        f.setSize(14);
        f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("PLAN_EXP_COST","Money");   
        f.setFunction("''");
        f.setSize(14);
        f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("PLAN_FIX_COST","Money");   
        f.setFunction("''");
        f.setSize(14);
        f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("TOT_PLAN_COST","Money");   
        f.setFunction("''");
        f.setSize(14);
        f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("PLAN_PERS_REV","Money"); 
        f.setFunction("''");
        f.setSize(14);
        f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("PLAN_MAT_REV","Money");   
        f.setFunction("''");
        f.setSize(14);
        f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("PLAN_TOOL_REV","Money");   
        f.setFunction("''");
        f.setSize(14);
        f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("PLAN_EXT_REV","Money");   
        f.setFunction("''");
        f.setSize(14);
        f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("PLAN_EXP_REV","Money");   
        f.setFunction("''");
        f.setSize(14);
        f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("PLAN_FIX_REV","Money");   
        f.setFunction("''");
        f.setSize(14);
        f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("TOT_PLAN_REV","Money"); 
        f.setFunction("''");
        f.setSize(14);
        f.setReadOnly();
        f.setHidden();

        itemblk6.setView("STANDARD_JOB_BUDGET");
        itemblk6.defineCommand("STANDARD_JOB_BUDGET_API","Modify__");
        itemblk6.setMasterBlock(headblk);
        itemblk6.disableDocMan();

        itemset6 = itemblk6.getASPRowSet();
        itembar6 = mgr.newASPCommandBar(itemblk6);
        itemtbl6 = mgr.newASPTable(itemblk6);
        itemtbl6.setWrap();

        itembar6.defineCommand(itembar6.OKFIND,"okFindITEM6");
        itembar6.defineCommand(itembar6.COUNTFIND,"countFindITEM6");
        itembar6.enableCommand(itembar6.EDIT);
        itembar6.defineCommand(itembar6.SAVERETURN,"saveReturnITEM6","checkItem6Fields(-1)");
        itembar6.disableCommand(itembar6.BACK);

        itembar6.disableMultirowAction();

        itemlay6 = itemblk6.getASPBlockLayout();
        itemlay6.setDialogColumns(1);
        itemlay6.setDefaultLayoutMode(itemlay6.SINGLE_LAYOUT);

        //================================================================
        // Programs tab
        //================================================================= 

        itemblk7 = mgr.newASPBlock("ITEM7");

        f = itemblk7.addField("ITEM7_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk7.addField("ITEM7_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk7.addField("ITEM7_STD_JOB_ID");
        f.setHidden();
        f.setDbName("STD_JOB_ID");
        f.setUpperCase();

        f = itemblk7.addField("ITEM7_STD_JOB_REVISION");
        f.setHidden();
        f.setDbName("STD_JOB_REVISION");
        f.setMaxLength(6);
        f.setUpperCase();   

        f = itemblk7.addField("ITEM7_STD_JOB_CONTRACT");
        f.setHidden();
        f.setDbName("STD_JOB_CONTRACT");
        f.setUpperCase();

        f = itemblk7.addField("JOB_PROGRAM_ID");   
        f.setSize(14);
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2JOBPROGID: Job Program Id");

        f = itemblk7.addField("JOB_PROGRAM_DESC");
        f.setFunction("Job_Program_API.Get_Description(:JOB_PROGRAM_ID, :JOB_PROGRAM_REVISION, :JOB_PROGRAM_CONTRACT)");
        f.setSize(14);
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2JOBPROGDESC: Description");


        f = itemblk7.addField("JOB_PROGRAM_REVISION");   
        f.setSize(14);
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2JOBPROGREV: Revision");

        f = itemblk7.addField("JOB_PROGRAM_CONTRACT");   
        f.setSize(14);
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2JOBPROGCONT: Site");

        f = itemblk7.addField("JOB_PROGRAM_STATE");   
        f.setFunction("Job_Program_API.Get_Status(:JOB_PROGRAM_ID,:JOB_PROGRAM_REVISION,:JOB_PROGRAM_CONTRACT)");
        f.setSize(14);
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2JOBPROGSTATE: Status");


        f = itemblk7.addField("JOB_PROGRAM_TYPE_ID");
        f.setFunction("Job_Program_API.Get_Program_Type_Id(:JOB_PROGRAM_ID,:JOB_PROGRAM_REVISION,:JOB_PROGRAM_CONTRACT)");
        f.setSize(14);
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2JOBPROGTYPEID: Type Id");

        f = itemblk7.addField("JOB_PROGRAM_TYPE_DESC");   
        f.setFunction("Program_Type_API.Get_Description(Job_Program_API.Get_Program_Type_Id(:JOB_PROGRAM_ID,:JOB_PROGRAM_REVISION,:JOB_PROGRAM_CONTRACT))");
        f.setSize(14);
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2JOBPROGTYPEDESC: Description");

        itemblk7.setView("STANDARD_JOB_PROGRAM");
        itemblk7.defineCommand("STANDARD_JOB_PROGRAM_API","");
        itemblk7.setMasterBlock(headblk);

        itemset7 = itemblk7.getASPRowSet();
        itembar7 = mgr.newASPCommandBar(itemblk7);
        itemtbl7 = mgr.newASPTable(itemblk7);
        itemtbl7.setWrap();

        itembar7.defineCommand(itembar7.OKFIND,"okFindITEM7");
        itembar7.defineCommand(itembar7.COUNTFIND,"countFindITEM7");

        itemlay7 = itemblk7.getASPBlockLayout();
        itemlay7.setDialogColumns(1);
        itemlay7.setDefaultLayoutMode(itemlay7.MULTIROW_LAYOUT);



        //==============================================================

        eventblk = mgr.newASPBlock("EVNTBLK");

        f = eventblk.addField("DEF_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = eventblk.addField("DEF_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = eventblk.addField("DEF_OBJSTATE");
        f.setHidden();
        f.setDbName("OBJSTATE");

        f = eventblk.addField("DEF_STD_JOB_ID");
        f.setHidden();
        f.setDbName("STD_JOB_ID");   

        f = eventblk.addField("DEF_STD_JOB_REVISION");
        f.setHidden();
        f.setDbName("STD_JOB_REVISION");
        f.setMaxLength(6);

        f = eventblk.addField("DEF_CONTRACT");
        f.setHidden();
        f.setDbName("CONTRACT");


        f = eventblk.addField("INVOICETYPE");
        f.setHidden();
        f.setFunction("''");

        eventblk.setView("SEPARATE_STANDARD_JOB");
        eventset = eventblk.getASPRowSet();

        tempblk1 = mgr.newASPBlock("TEMP1");

        tempblk1.addField("INFO");
        tempblk1.addField("ATTR");
        tempblk1.addField("ACTION");
    }

    //Web Alignment - Adding Tabs
    public void activatePrepareTab()
    {
        tabs.setActiveTab(1);
    }

    public void activateProgramTab()
    {
        tabs.setActiveTab(2);
    }

    public void activateBudgetTab()
    {
        tabs.setActiveTab(3);
    }

    public void activateOperationsTab()
    {
        tabs.setActiveTab(4);
        okFindITEM0();
    }

    public void activateMaterialTab()
    {
        tabs.setActiveTab(5);
        okFindITEM1();
    }

    public void activateToolsnFacilitiesTab()
    {
        tabs.setActiveTab(6);
        okFindITEM3();
    }

    public void activatePlanningTab()
    {
        tabs.setActiveTab(7);
        okFindITEM2();
    }

    public void activatePermitTab()
    {
        tabs.setActiveTab(8);
    }

    public void checkObjAvaileble()
    {
        ASPManager mgr = getASPManager();

        ASPBuffer availObj;
        trans.clear();
        trans.addSecurityQuery("INVENTORY_PART,INVENTORY_PART_CONFIG,PURCHASE_PART_SUPPLIER,COMPETENCY_GROUP_LOV1");
        trans.addPresentationObjectQuery("invenw/InventoryPartInventoryPartCurrentOnHand.page,invenw/InventoryPartAvailabilityPlanningQry.page,invenw/InventoryPart.page,purchw/PurchasePartSupplier.page");

        trans = mgr.perform(trans);

        availObj = trans.getSecurityInfo();

        if (availObj.itemExists("INVENTORY_PART") && availObj.namedItemExists("invenw/InventoryPartInventoryPartCurrentOnHand.page"))
            qtyOnHand_ = true;

        if (availObj.itemExists("INVENTORY_PART_CONFIG") && availObj.namedItemExists("invenw/InventoryPartAvailabilityPlanningQry.page"))
            availibilityDet_= true;

        if (availObj.itemExists("INVENTORY_PART") && availObj.namedItemExists("invenw/InventoryPart.page"))
            inventoryPart_= true;
        if (availObj.itemExists("PURCHASE_PART_SUPPLIER") && availObj.namedItemExists("purchw/PurchasePartSupplier.page"))
            suppPerPart_= true;
        if (availObj.itemExists("COMPETENCY_GROUP_LOV1"))
            secCompetence = true;


    }
    public void DissableRmbs()
    {
        ASPManager mgr = getASPManager();

        if (mgr.isPresentationObjectInstalled("invenw/InventoryPartInventoryPartCurrentOnHand.page")  && !qtyOnHand_)
            itembar1.removeCustomCommand("qtyOnHand");
        if (mgr.isPresentationObjectInstalled("invenw/InventoryPartAvailabilityPlanningQry.page")  && !availibilityDet_)
            itembar1.removeCustomCommand("availibilityDet");
        if (mgr.isPresentationObjectInstalled("invenw/InventoryPart.page") && !inventoryPart_)
            itembar1.removeCustomCommand("inventoryPart");
        if (mgr.isPresentationObjectInstalled("purchw/PurchasePartSupplier.page") && !suppPerPart_)
            itembar1.removeCustomCommand("suppPerPart");
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();
        mgr.setPageExpiring(); //Enabling the Page Expiring...

        item1RowStatus = itemset1.getRowStatus();

        laymode = String.valueOf( headlay.getLayoutMode() );

        headbar.removeCustomCommand("activatePrepareTab");
        headbar.removeCustomCommand("activateBudgetTab");
        headbar.removeCustomCommand("activateOperationsTab");
        headbar.removeCustomCommand("activateMaterialTab");
        headbar.removeCustomCommand("activateToolsnFacilitiesTab");
        headbar.removeCustomCommand("activatePlanningTab");
        headbar.removeCustomCommand("activatePermitTab");
        headbar.removeCustomCommand("activateProgramTab");

        if (headset.countRows()>0)
        {
            if (headlay.isMultirowLayout())
                headset.goTo(headset.getRowSelected());

            String state = headset.getRow().getValue("OBJSTATE");
            String con = headset.getRow().getValue("IS_CONNECTED");

            if (("Obsolete".equals(state)) || ("Active".equals(state) && "TRUE".equals(con)))
            {
                itembar0.disableCommand(itembar0.NEWROW);
                itembar1.disableCommand(itembar1.NEWROW);
                itembar2.disableCommand(itembar2.NEWROW);
                itembar3.disableCommand(itembar3.NEWROW);
                itembar4.disableCommand(itembar4.NEWROW);
                itembar5.disableCommand(itembar5.NEWROW);
            }
            if (itemlay0.isNewLayout())
               mgr.getASPField("PREDECESSOR").setReadOnly();
        }

        if (headset.countRows()>0)
        {
            if (itemset1.countRows()> 0)
                DissableRmbs();
        }

        if (headset.countRows() == 1)
            headbar.disableCommand(headbar.BACK);


        if (headlay.isEditLayout())
        {
            if ((headset.getRow().getValue("CBROLES")) == "TRUE" || (headset.getRow().getValue("CBMATERIALS")) == "TRUE")
                mgr.getASPField("STD_TEXT_ONLY").setReadOnly();
        }

        if (headlay.isNewLayout())
        {
            mgr.getASPField("CBROLES").setReadOnly();
            mgr.getASPField("CBMATERIALS").setReadOnly();
//          Bug 78529, Start
            mgr.getASPField("CHANGED_BY").setReadOnly();
//          Bug 78529, End
        }

        fldTitleCreatedBy = mgr.URLEncode(mgr.translate("PCMWSEPARATESTANDARDJOB2CREATEDBYFLD: Created+by"));
        fldTitleChangedBy = mgr.URLEncode(mgr.translate("PCMWSEPARATESTANDARDJOB2CHANGEDBYFLD: Changed+by"));
        fldTitlePartNoPurch = mgr.translate("PCMWPMACTIONPARTNOFLD: Part+No");

        lovTitleCreatedBy = mgr.URLEncode(mgr.translate("PCMWSEPARATESTANDARDJOB2CREATEDBYLOV: List of Created by"));
        lovTitleChangedBy = mgr.URLEncode(mgr.translate("PCMWSEPARATESTANDARDJOB2CHANGEDBYLOV: List of Changed by"));     
        lovTitlePartNoPurch = mgr.translate("PCMWPMACTIONPARTNOLOV: List+of+Part+No");

        if (headset.countRows() > 0 && headlay.isSingleLayout())
        {
            if (!itemlay0.isNewLayout())
            {
                if (itemset0.countRows() > 0)
                {
                    row = headset.getRow();
                    row.setValue("CBROLES","TRUE");
                    headset.setRow(row);
                }
                else if (itemset0.countRows() == 0)
                {
                    row = headset.getRow();
                    row.setValue("CBROLES","FALSE");
                    headset.setRow(row);
                }
            }
            if (!itemlay1.isNewLayout())
            {
                if (itemset1.countRows() > 0)
                {
                    row = headset.getRow();
                    row.setValue("CBMATERIALS","TRUE");
                    headset.setRow(row);
                }
                else if (itemset1.countRows() == 0)
                {
                    row = headset.getRow();
                    row.setValue("CBMATERIALS","FALSE");
                    headset.setRow(row);
                }
            }

            if (itemset3.countRows() > 0)
            {
                secBuff = mgr.newASPTransactionBuffer();
                secBuff.addSecurityQuery("SALES_PART");
                secBuff = mgr.perform(secBuff);

                if (!(secBuff.getSecurityInfo().itemExists("SALES_PART")))
                {
                    mgr.getASPField("SALES_PART_SITE").setHidden();
                    mgr.getASPField("ITEM3_SALES_PART").setHidden();
                    mgr.getASPField("SALES_PART_DESC").setHidden();
                    mgr.getASPField("ITEM3_SALES_PRICE").setHidden();
                    mgr.getASPField("ITEM3_DISCOUNT").setHidden();
                    mgr.getASPField("PRICE_AMOUNT").setHidden();     
                }

                secBuff.clear();
            }
        }

        if (tabs.getActiveTab() == 3)
        {
            if (itemset6.countRows() == 0)
            {
                perBudg   = "";
                matBudg   = "";
                toolBudg  = "";
                extBudg   = "";
                expBudg   = "";
                fixBudg   = "";
                totBudg   = "";
                
                planPer   = "";
                planMat   = "";
                planTool  = "";
                planExt   = "";
                planExp   = "";
                planFix   = "";
                planTot   = "";

                planPerRev   = "";
                planMatRev   = "";
                planToolRev  = "";
                planExtRev   = "";
                planExpRev   = "";
                planFixRev   = "";
                planTotRev   = "";

            }
            else
            {
                getBudgetValues();

                double perBudgNF   = itemset6.getNumberValue("PERS_BUDGET_COST");
                double matBudgNF   = itemset6.getNumberValue("MAT_BUDGET_COST");
                double toolBudgNF  = itemset6.getNumberValue("TF_BUDGET_COST");
                double extBudgNF   = itemset6.getNumberValue("EXT_BUDGET_COST");
                double expBudgNF   = itemset6.getNumberValue("EXP_BUDGET_COST");
                double fixBudgNF   = itemset6.getNumberValue("FIX_BUDGET_COST");
                double totBudgNF   = itemset6.getNumberValue("TOT_BUDGET_COST");

                perBudg    = mgr.formatNumber("PERS_BUDGET_COST", perBudgNF);
                matBudg    = mgr.formatNumber("MAT_BUDGET_COST", matBudgNF);
                toolBudg   = mgr.formatNumber("TF_BUDGET_COST", toolBudgNF);
                extBudg    = mgr.formatNumber("EXT_BUDGET_COST", extBudgNF);
                expBudg    = mgr.formatNumber("EXP_BUDGET_COST", expBudgNF);
                fixBudg    = mgr.formatNumber("FIX_BUDGET_COST", fixBudgNF);
                totBudg    = mgr.formatNumber("TOT_BUDGET_COST", totBudgNF);

                double planPerNF   = itemset6.getNumberValue("PLAN_PERS_COST");
                double planMatNF   = itemset6.getNumberValue("PLAN_MAT_COST");
                double planToolNF  = itemset6.getNumberValue("PLAN_TOOL_COST");
                double planExtNF   = itemset6.getNumberValue("PLAN_EXT_COST");
                double planExpNF   = itemset6.getNumberValue("PLAN_EXP_COST");
                double planFixNF   = itemset6.getNumberValue("PLAN_FIX_COST");
                double planTotNF   = itemset6.getNumberValue("TOT_PLAN_COST");

                planPer   = mgr.formatNumber("PLAN_PERS_COST", planPerNF);
                planMat   = mgr.formatNumber("PLAN_MAT_COST", planMatNF);
                planTool  = mgr.formatNumber("PLAN_TOOL_COST", planToolNF);
                planExt   = mgr.formatNumber("PLAN_EXT_COST", planExtNF);
                planExp   = mgr.formatNumber("PLAN_EXP_COST", planExpNF);
                planFix   = mgr.formatNumber("PLAN_FIX_COST", planFixNF);
                planTot   = mgr.formatNumber("TOT_PLAN_COST", planTotNF);

                double planPerRevNF   = itemset6.getNumberValue("PLAN_PERS_REV");
                double planMatRevNF   = itemset6.getNumberValue("PLAN_MAT_REV");
                double planToolRevNF  = itemset6.getNumberValue("PLAN_TOOL_REV");
                double planExtRevNF   = itemset6.getNumberValue("PLAN_EXT_REV");
                double planExpRevNF   = itemset6.getNumberValue("PLAN_EXP_REV");
                double planFixRevNF   = itemset6.getNumberValue("PLAN_FIX_REV");
                double planTotRevNF   = itemset6.getNumberValue("TOT_PLAN_REV");

                planPerRev   = mgr.formatNumber("PLAN_PERS_REV", planPerRevNF);
                planMatRev   = mgr.formatNumber("PLAN_MAT_REV", planMatRevNF);
                planToolRev  = mgr.formatNumber("PLAN_TOOL_REV", planToolRevNF);
                planExtRev   = mgr.formatNumber("PLAN_EXT_REV", planExtRevNF);
                planExpRev   = mgr.formatNumber("PLAN_EXP_REV", planExpRevNF);
                planFixRev   = mgr.formatNumber("PLAN_FIX_REV", planFixRevNF);
                planTotRev   = mgr.formatNumber("TOT_PLAN_REV", planTotRevNF);
            }
        }

        if (itemlay1.isVisible())
        {
            mgr.getASPField("SPARE_CONTRACT").setLOVProperty("WHERE","Site_API.Get_Company(CONTRACT) = '"+headset.getRow().getValue("COMPANY")+"'");
        }

        if (itemlay2.isEditLayout())
        {
            if ("1".equals(itemset2.getRow().getFieldValue("IS_AUTO_LINE")))
                mgr.getASPField("COST").setReadOnly();
        }

        if ( itemlay0.isNewLayout() || itemlay0.isEditLayout() || itemlay1.isNewLayout() || itemlay1.isEditLayout() ||
	     itemlay2.isNewLayout() || itemlay2.isEditLayout() || itemlay3.isNewLayout() || itemlay3.isEditLayout() ||
	     itemlay4.isNewLayout() || itemlay4.isEditLayout() || itemlay5.isNewLayout() || itemlay5.isEditLayout() ||
	     itemlay6.isEditLayout() )
        {
           headbar.disableCommand(headbar.DELETE);
           headbar.disableCommand(headbar.NEWROW);
           headbar.disableCommand(headbar.EDITROW);
           headbar.disableCommand(headbar.DELETE);
           headbar.disableCommand(headbar.DUPLICATEROW);
           headbar.disableCommand(headbar.FIND);
           headbar.disableCommand(headbar.BACKWARD);
           headbar.disableCommand(headbar.FORWARD);
        }
     }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWSEPARATESTANDARDJOB2TITLE: Separate Standard Job";
    }

    protected String getTitle()
    {
        return "PCMWSEPARATESTANDARDJOB2TITLE: Separate Standard Job";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        printHiddenField("HIDDENPARTNO", "");
        printHiddenField("ONCEGIVENERROR", "FALSE");
        printHiddenField("REFRESHPARENT", "FALSE");

        if (headlay.isSingleLayout() && headset.countRows() > 0)
       {

         if (itemlay0.isEditLayout() && (itemset0.countRows() > 0)) 
            appendDirtyJavaScript("   f.PREDECESSOR.disabled = true;\n");
        }
        
        appendToHTML(headlay.show());

        if (headset.countRows() > 0 && headlay.isSingleLayout())
        {
            if ("FALSE".equals(headset.getRow().getValue("STD_TEXT_ONLY")))
            {
                appendToHTML(tabs.showTabsInit());

                if (tabs.getActiveTab() == 1)
                {
                    appendToHTML(itemlay4.show());

                    appendDirtyJavaScript("function lovIdentity(i,params)\n");
                    appendDirtyJavaScript("{\n");
                    appendDirtyJavaScript("   var viewName = '';");
                    appendDirtyJavaScript("   switch(getValue_('JOB_CATEGORY_DB',i))\n");
                    appendDirtyJavaScript("   {\n");
                    appendDirtyJavaScript("      case 'ObjectCategory':\n");
                    appendDirtyJavaScript("           viewName = 'EQUIPMENT_OBJ_CATEGORY';\n");
                    appendDirtyJavaScript("           break;\n");
                    appendDirtyJavaScript("      case 'ObjectType':\n");
                    appendDirtyJavaScript("           viewName = 'EQUIPMENT_OBJ_TYPE';\n");
                    appendDirtyJavaScript("           break;\n");
                    appendDirtyJavaScript("      case 'TypeDesignation':\n");
                    appendDirtyJavaScript("           viewName = 'TYPE_DESIGNATION';\n");
                    appendDirtyJavaScript("           break;\n");
                    appendDirtyJavaScript("      case 'PartNo':\n");
                    appendDirtyJavaScript("           viewName = 'PART_CATALOG';\n");
                    appendDirtyJavaScript("           break;\n");
                    appendDirtyJavaScript("   }\n");
                    appendDirtyJavaScript("	  if(params) param = params;\n");
                    appendDirtyJavaScript("	  else param = '';\n");
                    appendDirtyJavaScript("	  var enable_multichoice =(true && ITEM4_IN_FIND_MODE);\n");
                    appendDirtyJavaScript("	  var key_value = (getValue_('IDENTITY',i).indexOf('%') !=-1)? getValue_('IDENTITY',i):'';\n");
                    appendDirtyJavaScript("	  openLOVWindow('IDENTITY',i,\n");
                    appendDirtyJavaScript("                 '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=' + viewName + '&__FIELD=Identity&__INIT=1&MULTICHOICE='+enable_multichoice+''\n");
                    appendDirtyJavaScript("                 + '&__KEY_VALUE=' + URLClientEncode(getValue_('IDENTITY',i))\n");
                    appendDirtyJavaScript("                 + '&IDENTITY=' + URLClientEncode(key_value)\n");
                    appendDirtyJavaScript("                 ,600,450,'validateIdentity');\n");
                    appendDirtyJavaScript("}\n");
                }

                else if (tabs.getActiveTab() == 2)
                    appendToHTML(itemlay7.show());

                else if (tabs.getActiveTab() == 3)
                {

                    if (!itemlay6.isEditLayout())
                    {
                        appendToHTML(itembar6.showBar());
                        appendToHTML("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">");
                        appendToHTML("<tr>");
                        appendToHTML("<td>");
                        appendToHTML("<table cellspacing=0 cellpadding=0 border=0 width=100%><tr><td>&nbsp;&nbsp;</td><td width=100%>");
                        appendToHTML("<table id=\"cntITEM6\" cellpadding=0 width=0 border=\"0\" class=\"BlockLayoutTable\">\n");
                        appendToHTML("	<tr>\n");
                        appendToHTML("		<td width=80>\n");
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=80 align=\"middle\">");
                        appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONBUDGCOST: Budget Cost"));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=80 align=\"middle\">");
                        appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONPLANCOST: Planned Cost"));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=100 align=\"middle\">");
                        appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONPLANREV: Planned Revenue"));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=80>\n");
                        appendToHTML("		</td>\n");
                        appendToHTML("	</tr>	\n");
                        appendToHTML("	<tr>\n");
                        appendToHTML("		<td width=0>");
                        appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONPERSONAL: Personal"));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(perBudg));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planPer));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planPerRev));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=80>\n");
                        appendToHTML("		</td>\n");
                        appendToHTML("	</tr>				\n");
                        appendToHTML("	<tr>\n");
                        appendToHTML("		<td width=0>");
                        appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONMATERIAL: Material"));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(matBudg));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planMat));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planMatRev));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=80>\n");
                        appendToHTML("		</td>\n");
                        appendToHTML("	</tr>				\n");
                        appendToHTML("	<tr>\n");
                        appendToHTML("		<td width=0>");
                        appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONTOOL: Tools/Facilities"));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(toolBudg));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planTool));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planToolRev));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=80>\n");
                        appendToHTML("		</td>\n");
                        appendToHTML("	</tr>				\n");
                        appendToHTML("	<tr>\n");
                        appendToHTML("		<td width=0>");
                        appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONEXTERNAL: External"));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(extBudg));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planExt));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planExtRev));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=80>\n");
                        appendToHTML("		</td>\n");
                        appendToHTML("	</tr>				\n");
                        appendToHTML("	<tr>\n");
                        appendToHTML("		<td width=0>");
                        appendToHTML(fmt.drawWriteLabel("PCMWSTDJOBEXPENSES: Expenses"));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(expBudg));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planExp));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planExpRev));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=80>\n");
                        appendToHTML("		</td>\n");
                        appendToHTML("	</tr>				\n");
                        appendToHTML("	<tr>\n");
                        appendToHTML("		<td width=0>");
                        appendToHTML(fmt.drawWriteLabel("PCMWSTDJOBFIXEDPRICE: Fixed Price"));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(fixBudg));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planFix));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planFixRev));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=80>\n");
                        appendToHTML("		</td>\n");
                        appendToHTML("	</tr>				\n");
                        appendToHTML("	<tr>\n");
                        appendToHTML("		<td width=0>");
                        appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONTOTAL: Total"));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(totBudg));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planTot));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planTotRev));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=80>\n");
                        appendToHTML("		</td>\n");
                        appendToHTML("	</tr>				\n");
                        appendToHTML("	<tr>\n");
                        appendToHTML("	</tr>	\n");
                        appendToHTML("</table>\n");
                        appendToHTML("</td>	\n");
                        appendToHTML("</tr>	\n");
                        appendToHTML("</table>\n");
                        appendToHTML("</td>	\n");
                        appendToHTML("</tr>	\n");
                        appendToHTML("</table>\n");
                    }
                    else
                    {

                        appendToHTML(itembar6.showBar());
                        appendToHTML("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">");
                        appendToHTML("<tr>");
                        appendToHTML("<td>");
                        appendToHTML("<table cellspacing=0 cellpadding=0 border=0 width=100%><tr><td>&nbsp;&nbsp;</td><td width=100%>");
                        appendToHTML("&nbsp;<table id=\"cntITEM6\" cellpadding=2 width=0 border=\"0\" class=\"BlockLayoutTable\">\n");
                        appendToHTML("	<tr>\n");
                        appendToHTML("		<td width=80>\n");
                        appendToHTML(itemlay6.generateDataPresentation());
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=80 align=\"middle\">");
                        appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONBUDGCOST: Budget Cost"));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=80 align=\"middle\">");
                        appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONPLANCOST: Planned Cost"));
                        appendToHTML(                                       "		</td>\n");
                        appendToHTML("		<td width=100 align=\"middle\">");
                        appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONPLANREV: Planned Revenue"));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=80>\n");
                        appendToHTML("		</td>\n");
                        appendToHTML("	</tr>	\n");
                        appendToHTML("	<tr>\n");
                        appendToHTML("		<td width=0>");
                        appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONPERSONAL: Personal"));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawTextField("PERS_BUDGET_COST1",perBudg,"OnChange=assignPersBudgetCost(0)",18));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planPer));
                        appendToHTML(               "		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planPerRev));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=80>\n");
                        appendToHTML("		</td>\n");
                        appendToHTML("	</tr>	\n");
                        appendToHTML("	<tr>\n");
                        appendToHTML("		<td width=0>");
                        appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONMATERIAL: Material"));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawTextField("MAT_BUDGET_COST1",matBudg,"OnChange=assignMatBudgetCost()",18));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planMat));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planMatRev));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=80>\n");
                        appendToHTML("		</td>\n");
                        appendToHTML("	</tr>				\n");
                        appendToHTML("	<tr>\n");
                        appendToHTML("		<td width=0>");
                        appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONTOOL: Tools/Facilities"));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawTextField("TF_BUDGET_COST1",toolBudg,"OnChange=assignTfBudgetCost()",18));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planTool));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planToolRev));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=80>\n");
                        appendToHTML("		</td>\n");
                        appendToHTML("	</tr>				\n");
                        appendToHTML("	<tr>\n");
                        appendToHTML("		<td width=0>");
                        appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONEXTERNAL: External"));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawTextField("EXT_BUDGET_COST1",extBudg,"OnChange=assignExtBudgetCost()",18));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planExt));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planExtRev));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=80>\n");
                        appendToHTML("		</td>\n");
                        appendToHTML("	</tr>				\n");
                        appendToHTML("	<tr>\n");
                        appendToHTML("		<td width=0>");
                        appendToHTML(fmt.drawWriteLabel("PCMWSTDJOBEXPENSES: Expenses"));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawTextField("EXP_BUDGET_COST1",expBudg,"OnChange=assignExpBudgetCost()",18));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planExp));
                        appendToHTML(               "		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planExpRev));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=80>\n");
                        appendToHTML("		</td>\n");
                        appendToHTML("	</tr>	\n");
                        appendToHTML("	<tr>\n");
                        appendToHTML("		<td width=0>");
                        appendToHTML(fmt.drawWriteLabel("PCMWSTDJOBFIXEDPRICE: Fixed Price"));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawTextField("FIX_BUDGET_COST1",fixBudg,"OnChange=assignFixBudgetCost()",18));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planFix));
                        appendToHTML(               "		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planFixRev));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=80>\n");
                        appendToHTML("		</td>\n");
                        appendToHTML("	</tr>	\n");
                        appendToHTML("	<tr>\n");
                        appendToHTML("		<td width=0>");
                        appendToHTML(fmt.drawWriteLabel("PCMWPMACTIONTOTAL: Total"));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadOnlyTextField("TOT_BUDGET_COST",totBudg,"",18));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planTot));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=0 align=\"right\">");
                        appendToHTML(fmt.drawReadValue(planTotRev));
                        appendToHTML("		</td>\n");
                        appendToHTML("		<td width=80>\n");
                        appendToHTML("		</td>\n");
                        appendToHTML("	</tr>				\n");
                        appendToHTML("	<tr>\n");
                        appendToHTML("	</tr>	\n");
                        appendToHTML("</table>	\n");
                        appendToHTML("</td>	\n");
                        appendToHTML("</tr>	\n");
                        appendToHTML("</table>\n");
                        appendToHTML("</td>	\n");
                        appendToHTML("</tr>	\n");
                        appendToHTML("</table>\n");
                    }
                }
                else if (tabs.getActiveTab() == 4)
                    appendToHTML(itemlay0.show());
                else if (tabs.getActiveTab() == 5)
                {
                    appendToHTML(itembar1.showBar());   

                    if ("New__".equals(item1RowStatus))
                    {
                        appendToHTML("	<font class=\"TextLabel\" face=\"Arial\"><a href=\"javascript:LovPartNoPurch(-1)\">");
                        appendToHTML(mgr.translate("PCMWPMACTIONLOVPURCH: LOV from Purchase Part..."));
                        appendToHTML("</a></font>\n");
                    }

                    appendToHTML(itemlay1.generateDataPresentation());
                }
                else if (tabs.getActiveTab() == 6)
                    appendToHTML(itemlay3.show());
                else if (tabs.getActiveTab() == 7)
                    appendToHTML(itemlay2.show());
                else if (tabs.getActiveTab() == 8)
                    appendToHTML(itemlay5.show());
            }
        }
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("function assignPersBudgetCost()\n");
        appendDirtyJavaScript("{\n");
	appendDirtyJavaScript("   document.form.PERS_BUDGET_COST.value = document.form.PERS_BUDGET_COST1.value;\n");
	appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("function assignMatBudgetCost()\n");
        appendDirtyJavaScript("{\n");
	appendDirtyJavaScript("   document.form.MAT_BUDGET_COST.value = document.form.MAT_BUDGET_COST1.value;\n");
	appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("function assignTfBudgetCost()\n");
        appendDirtyJavaScript("{\n");
	appendDirtyJavaScript("   document.form.TF_BUDGET_COST.value = document.form.TF_BUDGET_COST1.value;\n");
	appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("function assignExtBudgetCost()\n");
        appendDirtyJavaScript("{\n");
	appendDirtyJavaScript("   document.form.EXT_BUDGET_COST.value = document.form.EXT_BUDGET_COST1.value;\n");
	appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("function assignExpBudgetCost()\n");
        appendDirtyJavaScript("{\n");
	appendDirtyJavaScript("   document.form.EXP_BUDGET_COST.value = document.form.EXP_BUDGET_COST1.value;\n");
	appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("function assignFixBudgetCost()\n");
        appendDirtyJavaScript("{\n");
	appendDirtyJavaScript("   document.form.FIX_BUDGET_COST.value = document.form.FIX_BUDGET_COST1.value;\n");
	appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("window.name = \"CopyWoOr\";\n");

        appendDirtyJavaScript("function tabDisable()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("  if (f.STD_TEXT_ONLY.checked == true)\n");
        appendDirtyJavaScript("    document.form.__TABDISBLED.value = '1';\n");
        appendDirtyJavaScript("  else\n");
        appendDirtyJavaScript("    document.form.__TABDISBLED.value = '0';\n");
        appendDirtyJavaScript("  submit();\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function HypDoc()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   sStdJobIDVar = document.form.STD_JOB_ID.value;\n");
        appendDirtyJavaScript("   sStdJobCONTVar = document.form.CONTRACT.value;\n");
        appendDirtyJavaScript("   sStdJobREVVar = document.form.STD_JOB_REVISION.value;\n");
        appendDirtyJavaScript("   if (sStdJobIDVar =='' || sStdJobCONTVar=='' || sStdJobREVVar=='')\n");
        appendDirtyJavaScript("      window.open(\"../docmaw/DocReference.page\",\"hypeWindow\",\"scrollbars,resizable,status=yes,width=600,height=445\");\n");
        appendDirtyJavaScript("   else\n");
        appendDirtyJavaScript("      window.open(\"../docmaw/DocReference.page?LU_NAME=StandardJob&KEY_REF=STD_JOB_ID=\"+URLClientEncode(sStdJobIDVar)+\"&CONTRACT=\"+URLClientEncode(sStdJobCONTVar)+\"&STD_JOB_REV=\"+URLClientEncode(sStdJobREVVar)+\"^\",\"hypeWindow\",\"scrollbars,resizable,status=yes,width=600,height=445\");  \n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovCreatedBy(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("    if( getRowStatus_('HEAD',i)=='QueryMode__')\n");
        appendDirtyJavaScript("      openLOVWindow('CREATED_BY',i,\n");
        appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitleCreatedBy);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleCreatedBy);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		,600,445,'validateCreatedBy');\n");
        appendDirtyJavaScript("	 else\n");
        appendDirtyJavaScript("      openLOVWindow('CREATED_BY',i,\n");
        appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitleCreatedBy);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleCreatedBy);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
        appendDirtyJavaScript("		,600,445,'validateCreatedBy');\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovChangedBy(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("    if( getRowStatus_('HEAD',i)=='QueryMode__')\n");
        appendDirtyJavaScript("	   openLOVWindow('CHANGED_BY',i,\n");
        appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitleChangedBy);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleChangedBy);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		,600,445,'validateChangedBy');\n");
        appendDirtyJavaScript("	 else\n");
        appendDirtyJavaScript("	   openLOVWindow('CHANGED_BY',i,\n");
        appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=EMPLOYEE_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitleChangedBy);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitleChangedBy);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		+ '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
        appendDirtyJavaScript("		,600,445,'validateChangedBy');	 	\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function LovPartNoPurch(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	lovUrl = '");
        appendDirtyJavaScript(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT"));
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("	openLOVWindow('PART_NO',i,\n");
        appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PURCHASE_PART_LOV&__FIELD=");
        appendDirtyJavaScript(fldTitlePartNoPurch);
        appendDirtyJavaScript("&__TITLE=");
        appendDirtyJavaScript(lovTitlePartNoPurch);
        appendDirtyJavaScript("'\n");
        appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))\n");
        appendDirtyJavaScript("		,600,445,'validatePartNo');\n");
        appendDirtyJavaScript("}\n");   

        appendDirtyJavaScript("function validateWorkOrderCostType(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM2',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkWorkOrderCostType(i) ) return;\n");
        appendDirtyJavaScript(" window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=WORK_ORDER_COST_TYPE'\n");
        appendDirtyJavaScript("		+ '&WORK_ORDER_COST_TYPE=' + getField_('WORK_ORDER_COST_TYPE',i).options[getField_('WORK_ORDER_COST_TYPE',i).selectedIndex].value\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript(" window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'WORK_ORDER_COST_TYPE',i,'Work Order Cost Type') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('PERS',i,0);\n");
        appendDirtyJavaScript("		assignValue_('METR',i,1);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript(" if( getField_('WORK_ORDER_COST_TYPE',i).selectedIndex == 1 )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("         window.alert('");
        appendDirtyJavaScript(          mgr.translateJavaScript("PCMWSEPSTDJOBREGOFPER: Registration of Personnel costs is not allowed here. Use Crafts section."));
        appendDirtyJavaScript("         ');\n");
        appendDirtyJavaScript("		getField_('WORK_ORDER_COST_TYPE',i).value = '';\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript(" if( getField_('WORK_ORDER_COST_TYPE',i).selectedIndex == 2 )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("         window.alert('");
        appendDirtyJavaScript(          mgr.translateJavaScript("PCMWSEPSTDJOBREGOFMAT: Registration of Material costs is not allowed here. Use Materials section."));
        appendDirtyJavaScript("         ');\n");
        appendDirtyJavaScript("		getField_('WORK_ORDER_COST_TYPE',i).value = '';\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript(" if( getField_('WORK_ORDER_COST_TYPE',i).selectedIndex == 6 )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("         window.alert('");
        appendDirtyJavaScript(          mgr.translateJavaScript("PCMWSEPSTDJOBREGOFTOOLFAC: Registration of Tools/Facilities costs is not allowed here. Use Tools and Facilities section."));
        appendDirtyJavaScript("         ');\n");
        appendDirtyJavaScript("		getField_('WORK_ORDER_COST_TYPE',i).value = '';\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function checkHeadFields(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	return checkStdJobId(i) &&\n");
        appendDirtyJavaScript("	checkDefinition(i) &&\n");
        appendDirtyJavaScript("	checkState(i) &&\n");
        appendDirtyJavaScript("	checkContract(i) &&\n");
        appendDirtyJavaScript("	checkWorkDescription(i) &&\n");
        appendDirtyJavaScript("	checkCreatedBy(i) &&\n");
        appendDirtyJavaScript("	checkCreationDate(i) &&\n");
        appendDirtyJavaScript("	checkChangedBy(i) &&\n");
        appendDirtyJavaScript("	checkLastChanged(i);\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovOrgCode(i,params)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if(params) param = params;\n");
        appendDirtyJavaScript("	else param = '';\n");
        appendDirtyJavaScript("	whereCond1 = '';\n"); 
        appendDirtyJavaScript("	whereCond2 = '';\n");
        appendDirtyJavaScript("    if (document.form.ORG_CONTRACT.value != '') \n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("	      whereCond1 = \" CONTRACT = '\" +URLClientEncode(document.form.ORG_CONTRACT.value)+\"' \";\n");  
        appendDirtyJavaScript("	      whereCond2 = \" MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ORG_CONTRACT.value)+\"' \";\n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("    else\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("	      whereCond1 = '';\n"); 
        appendDirtyJavaScript("	      whereCond2 = '';\n");
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript(" if (document.form.TEAM_ID.value != '')\n");
        appendDirtyJavaScript(" {\n");
        appendDirtyJavaScript(" if(whereCond1 != '' )\n");
        appendDirtyJavaScript("	           whereCond1 += \" AND \";\n");
        appendDirtyJavaScript(" if (document.form.ROLE_CODE.value == '')\n");
        appendDirtyJavaScript("	   whereCond1 += \" ORG_CODE IN (SELECT MAINT_ORG FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.TEAM_ID.value+\"' AND CONTRACT = '\"+ document.form.TEAM_CONTRACT.value+\"' \";\n");
        appendDirtyJavaScript("    else\n");
        appendDirtyJavaScript("	   whereCond1 += \" (ORG_CODE,1) IN (SELECT MAINT_ORG,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\"+ document.form.ROLE_CODE.value+\"',MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.TEAM_ID.value+\"' AND CONTRACT='\" + URLClientEncode(document.form.TEAM_CONTRACT.value) +\"' \";\n"); 
        appendDirtyJavaScript(" if(whereCond2 != '' )\n");
        appendDirtyJavaScript("	           whereCond1 += \" AND \" +whereCond2 +\" \";\n"); 
        appendDirtyJavaScript("	whereCond1 += \")\";\n"); 
        appendDirtyJavaScript(" }\n");
        appendDirtyJavaScript("	var enable_multichoice =(true && ITEM0_IN_FIND_MODE);\n");
        appendDirtyJavaScript("	var key_value = (getValue_('ORG_CODE',i).indexOf('%') !=-1)? getValue_('ORG_CODE',i):'';\n");
        appendDirtyJavaScript("	  openLOVWindow('ORG_CODE',i,\n");
        appendDirtyJavaScript("'");
        appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=ORG_CODE_ALLOWED_SITE_LOV&__FIELD=Maintenance+Organization&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
        appendDirtyJavaScript("+ '&ORG_CODE=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript("+ '&CONTRACT=' + URLClientEncode(getValue_('ORG_CONTRACT',i))\n");
        appendDirtyJavaScript(",550,500,'validateOrgCode');\n");
        appendDirtyJavaScript("}\n");


        appendDirtyJavaScript("function validateOrgCode(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkOrgCode(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('ORG_CODE',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('DEF_CATALOG_NO',i).value = '';\n");
        appendDirtyJavaScript("		getField_('CATALOG_NO',i).value = '';\n");
        appendDirtyJavaScript("		getField_('DEFSLSPRTCOST',i).value = '';\n");
        appendDirtyJavaScript("		if( getValue_('ROLE_CODE',i)=='' )\n");
        appendDirtyJavaScript("		getField_('SLSPRTCOST',i).value = '';\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ORG_CODE'\n");
        appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
        appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ORG_CONTRACT=' + URLClientEncode(getValue_('ORG_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM0_CONTRACT=' + URLClientEncode(getValue_('ITEM0_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&SLSPRTCOST=' + URLClientEncode(getValue_('SLSPRTCOST',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ORG_CODE',i,'Maintenance Organization') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('DEF_CATALOG_NO',i,0);\n");
        appendDirtyJavaScript("		assignValue_('CATALOG_NO',i,1);\n");
        appendDirtyJavaScript("		assignValue_('SLSPRTCOST',i,2);\n");
        appendDirtyJavaScript("		assignValue_('DEFSLSPRTCOST',i,3);\n");
        appendDirtyJavaScript("		assignValue_('SLSPRTCTLGDESC',i,4);\n");
        appendDirtyJavaScript("		assignValue_('SLSPRTLSTPRICE',i,5);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovOrgContract(i,params)"); 
        appendDirtyJavaScript("{"); 
        appendDirtyJavaScript("	if(params) param = params;\n"); 
        appendDirtyJavaScript("	else param = '';\n"); 
        appendDirtyJavaScript("	   whereCond1 = \" Site_API.Get_Company(CONTRACT) = Site_API.Get_Company('\" + URLClientEncode(document.form.ITEM0_STD_JOB_CONTRACT.value) + \"') \";\n"); 
        appendDirtyJavaScript("	var enable_multichoice =(true && ITEM0_IN_FIND_MODE);\n"); 
        appendDirtyJavaScript("	var key_value = (getValue_('ORG_CONTRACT',i).indexOf('%') !=-1)? getValue_('ORG_CONTRACT',i):'';\n"); 
        appendDirtyJavaScript("	openLOVWindow('ORG_CONTRACT',i,\n"); 
        appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=USER_ALLOWED_SITE_LOV&__FIELD=Organization+Site&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''"); 
        appendDirtyJavaScript("		+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ORG_CONTRACT',i))"); 
        appendDirtyJavaScript("		+ '&ORG_CONTRACT=' + URLClientEncode(key_value)");
        appendDirtyJavaScript("         + '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
        appendDirtyJavaScript("		,550,500,'validateTeamContract');\n"); 
        appendDirtyJavaScript("}\n");


        appendDirtyJavaScript("function lovRoleCode(i,params)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if(params) param = params;\n");
        appendDirtyJavaScript("	else param = '';\n");
        appendDirtyJavaScript("	whereCond1 = '';\n"); 
        appendDirtyJavaScript("	whereCond2 = '';\n");
        appendDirtyJavaScript("	   whereCond1 = \" Site_API.Get_Company(CONTRACT) = Site_API.Get_Company('\" + URLClientEncode(document.form.ITEM0_STD_JOB_CONTRACT.value) + \"') \";\n"); 
        appendDirtyJavaScript("	   whereCond2 = \" Site_API.Get_Company(MAINT_ORG_CONTRACT) = Site_API.Get_Company('\" + URLClientEncode(document.form.ITEM0_STD_JOB_CONTRACT.value) + \"') \";\n"); 
        appendDirtyJavaScript(" if (document.form.ORG_CONTRACT.value != '')\n");
        appendDirtyJavaScript("		whereCond1 +=\" AND CONTRACT = '\" +URLClientEncode(document.form.ORG_CONTRACT.value)+\"' \";\n"); 
        appendDirtyJavaScript(" else\n");
        appendDirtyJavaScript("		whereCond1 +=\"AND CONTRACT IS NOT NULL \";\n"); 
        appendDirtyJavaScript(" if (document.form.TEAM_ID.value != '')\n");
        appendDirtyJavaScript("	   whereCond1 += \" AND 1 IN (SELECT Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,ROLE_CODE,MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE TEAM_ID = '\"+ document.form.TEAM_ID.value+\"' AND CONTRACT = '\"+ document.form.TEAM_CONTRACT.value+\"' AND \" +whereCond2 +\")\";\n"); 
        appendDirtyJavaScript("	var enable_multichoice =(true && ITEM0_IN_FIND_MODE);\n");
        appendDirtyJavaScript("	var key_value = (getValue_('ROLE_CODE',i).indexOf('%') !=-1)? getValue_('ROLE_CODE',i):'';\n");
        appendDirtyJavaScript("	  openLOVWindow('ROLE_CODE',i,\n");
        appendDirtyJavaScript("'");
        appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=ROLE_TO_SITE_LOV&__FIELD=Craft+ID&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
        appendDirtyJavaScript("+ '&ROLE_CODE=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript("+ '&CONTRACT=' + URLClientEncode(getValue_('ORG_CONTRACT',i))\n");
        appendDirtyJavaScript(",550,500,'validateRoleCode');\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateRoleCode(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkRoleCode(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('ROLE_CODE',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('CATALOG_NO',i).value = '';\n");
        appendDirtyJavaScript("		getField_('ROLEDESCRIPTION',i).value = '';\n");
        appendDirtyJavaScript("		if( getValue_('ORG_CODE',i)=='' )\n");
        appendDirtyJavaScript("		    getField_('SLSPRTCOST',i).value = '';\n");
        appendDirtyJavaScript("		getField_('SLSPRTCTLGDESC',i).value = '';\n");
        appendDirtyJavaScript("		getField_('SLSPRTLSTPRICE',i).value = '';\n");
        appendDirtyJavaScript("		   validateOrgCode(i);\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ROLE_CODE'\n");
        appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
        appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ORG_CONTRACT=' + URLClientEncode(getValue_('ORG_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ORG_CODE=' + URLClientEncode(getValue_('ORG_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM0_CONTRACT=' + URLClientEncode(getValue_('ITEM0_CONTRACT',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ROLE_CODE',i,'Craft ID') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('CATALOG_NO',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ROLEDESCRIPTION',i,1);\n");
        appendDirtyJavaScript("		assignValue_('SLSPRTCOST',i,2);\n");
        appendDirtyJavaScript("		assignValue_('SLSPRTCTLGDESC',i,3);\n");
        appendDirtyJavaScript("		assignValue_('SLSPRTLSTPRICE',i,4);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validatePlannedMen(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkPlannedMen(i) ) return;\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=PLANNED_MEN'\n");
        appendDirtyJavaScript("		+ '&PLANNED_MEN=' + URLClientEncode(getValue_('PLANNED_MEN',i))\n");
        appendDirtyJavaScript("		+ '&PLANNED_HOURS=' + URLClientEncode(getValue_('PLANNED_HOURS',i))\n");
        appendDirtyJavaScript("		+ '&SLSPRTLSTPRICE=' + URLClientEncode(getValue_('SLSPRTLSTPRICE',i))\n");
        appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM0_CONTRACT=' + URLClientEncode(getValue_('ITEM0_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
        appendDirtyJavaScript("		+ '&SALES_PRICE=' + URLClientEncode(getValue_('SALES_PRICE',i))\n");
        appendDirtyJavaScript("		+ '&DISCOUNT=' + URLClientEncode(getValue_('DISCOUNT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM0_PRICE_AMOUNT=' + URLClientEncode(getValue_('ITEM0_PRICE_AMOUNT',i))\n");
        appendDirtyJavaScript("		+ '&WORK_ORDER_INVOICE_TYPE=' + URLClientEncode(getValue_('WORK_ORDER_INVOICE_TYPE',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'PLANNED_MEN',i,'Planned Men') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM0_PRICE_AMOUNT',i,0);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validatePartNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("f.HIDDENPARTNO.value = \"\";\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkPartNo(i) ) return;\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=PART_NO'\n");
        appendDirtyJavaScript("		+ '&SPARE_CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&PLANNED_QTY=' + URLClientEncode(getValue_('PLANNED_QTY',i))\n");
        appendDirtyJavaScript("		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM1PRTLSTPRC=' + URLClientEncode(getValue_('ITEM1PRTLSTPRC',i))\n");
        appendDirtyJavaScript("		+ '&ITEM1_DISCOUNT=' + URLClientEncode(getValue_('ITEM1_DISCOUNT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM1_SALES_PRICE=' + URLClientEncode(getValue_('ITEM1_SALES_PRICE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM1_QTY_TO_INVOICE=' + URLClientEncode(getValue_('ITEM1_QTY_TO_INVOICE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM1_WORK_ORDER_INVOICE_TYPE=' + URLClientEncode(getValue_('ITEM1_WORK_ORDER_INVOICE_TYPE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM1_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM1_STD_JOB_CONTRACT=' + URLClientEncode(getValue_('ITEM1_STD_JOB_CONTRACT',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'PART_NO',i,'Part No') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('PARTDESCRIPTION',i,0);\n");
        appendDirtyJavaScript("		assignValue_('DIMQUALITY',i,1);\n");
        appendDirtyJavaScript("		assignValue_('TYPEDESIGNATION',i,2);\n");
        appendDirtyJavaScript("		assignValue_('PARTUNITCODE',i,3);\n");
        appendDirtyJavaScript("		assignValue_('ITEM1PRTCOST',i,4);\n");
        appendDirtyJavaScript("		assignValue_('ITEM1_CATALOG_NO',i,5);\n");
        appendDirtyJavaScript("		assignValue_('ITEM1CTLGDESC',i,6);\n"); 
        appendDirtyJavaScript("		assignValue_('ITEM1PRTLSTPRC',i,7);\n");
        appendDirtyJavaScript("		assignValue_('ITEM1_PRICE_AMOUNT',i,8);\n");
        appendDirtyJavaScript("		assignValue_('CONDITION_CODE',i,9);\n");
        appendDirtyJavaScript("		assignValue_('CONDITION_CODE_DESC',i,10);\n");
        appendDirtyJavaScript("		assignValue_('ACTIVEIND_DB',i,11);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("else{\n");
        appendDirtyJavaScript("f.HIDDENPARTNO.value = f.PART_NO.value;\n");
        appendDirtyJavaScript("f.ONCEGIVENERROR.value = \"TRUE\";\n");
        appendDirtyJavaScript("getField_('PART_NO',i).value = '';\n");
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("   if (f.ACTIVEIND_DB.value == 'N') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWSEPARATESTANDARDJOB2INVSALESPART: All sale parts connected to the part are inactive."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.ITEM1_CATALOG_NO.value = ''; \n");
        appendDirtyJavaScript("      f.ITEM1CTLGDESC.value = ''; \n");   
        appendDirtyJavaScript("      f.ITEM1PRTLSTPRC.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("         validateItem1CatalogNo(-1);\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateToolFacilityId(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM3',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkToolFacilityId(i) ) return;\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=TOOL_FACILITY_ID'\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_ID=' + URLClientEncode(getValue_('TOOL_FACILITY_ID',i))\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_TYPE=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE',i))\n");
        appendDirtyJavaScript("		+ '&TOOL_FACILITY_TYPE_DESC=' + URLClientEncode(getValue_('TOOL_FACILITY_TYPE_DESC',i))\n");
        appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
        appendDirtyJavaScript("		+ '&ITEM3_CONTRACT=' + URLClientEncode(getValue_('ITEM3_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM3_ORG_CODE=' + URLClientEncode(getValue_('ITEM3_ORG_CODE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM3_STD_JOB_ID=' + URLClientEncode(getValue_('ITEM3_STD_JOB_ID',i))\n");
        appendDirtyJavaScript("		+ '&ITEM3_STD_JOB_REVISION=' + URLClientEncode(getValue_('ITEM3_STD_JOB_REVISION',i))\n");
        appendDirtyJavaScript("		+ '&PLANNED_HOUR=' + URLClientEncode(getValue_('PLANNED_HOUR',i))\n");
        appendDirtyJavaScript("		+ '&ITEM3_DISCOUNT=' + URLClientEncode(getValue_('ITEM3_DISCOUNT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM3_SALES_PRICE=' + URLClientEncode(getValue_('ITEM3_SALES_PRICE',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'TOOL_FACILITY_ID',i,'Tool/Facility Id') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_DESC',i,0);\n");
        appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_TYPE',i,1);\n");
        appendDirtyJavaScript("		assignValue_('TOOL_FACILITY_TYPE_DESC',i,2);\n");
        appendDirtyJavaScript("		assignValue_('QTY',i,3);\n");
        appendDirtyJavaScript("		assignValue_('ITEM3_COST',i,4);\n");
        appendDirtyJavaScript("		assignValue_('COST_CURRENCY',i,5);\n");
        appendDirtyJavaScript("		assignValue_('NOTE',i,6);\n");
        appendDirtyJavaScript("		assignValue_('ITEM3_SALES_PART',i,7);\n");
        appendDirtyJavaScript("		assignValue_('SALES_PART_DESC',i,8);\n");
        appendDirtyJavaScript("		assignValue_('ITEM3_LIST_PRICE',i,9);\n");
        appendDirtyJavaScript("		assignValue_('PRICE_CURRENCY',i,10);\n");
        appendDirtyJavaScript("		assignValue_('PRICE_AMOUNT',i,11);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateItem3SalesPart(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM3',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem3SalesPart(i) ) return;\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM3_SALES_PART'\n");
        appendDirtyJavaScript("		+ '&ITEM3_SALES_PART=' + URLClientEncode(getValue_('ITEM3_SALES_PART',i))\n");
        appendDirtyJavaScript("		+ '&SALES_PART_SITE=' + URLClientEncode(getValue_('SALES_PART_SITE',i))\n");
        appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
        appendDirtyJavaScript("		+ '&PLANNED_HOUR=' + URLClientEncode(getValue_('PLANNED_HOUR',i))\n");
        appendDirtyJavaScript("		+ '&ITEM3_DISCOUNT=' + URLClientEncode(getValue_('ITEM3_DISCOUNT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM3_SALES_PRICE=' + URLClientEncode(getValue_('ITEM3_SALES_PRICE',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM3_SALES_PART',i,'Sales Part') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('SALES_PART_DESC',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM3_LIST_PRICE',i,1);\n");
        appendDirtyJavaScript("		assignValue_('PRICE_CURRENCY',i,2);\n");
        appendDirtyJavaScript("		assignValue_('PRICE_AMOUNT',i,3);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateItem3Discount(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM3',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem3Discount(i) ) return;\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM3_DISCOUNT'\n");
        appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
        appendDirtyJavaScript("		+ '&PLANNED_HOUR=' + URLClientEncode(getValue_('PLANNED_HOUR',i))\n");
        appendDirtyJavaScript("		+ '&ITEM3_DISCOUNT=' + URLClientEncode(getValue_('ITEM3_DISCOUNT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM3_SALES_PART=' + URLClientEncode(getValue_('ITEM3_SALES_PART',i))\n");
        appendDirtyJavaScript("		+ '&SALES_PART_SITE=' + URLClientEncode(getValue_('SALES_PART_SITE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM3_SALES_PRICE=' + URLClientEncode(getValue_('ITEM3_SALES_PRICE',i))\n");
        appendDirtyJavaScript("		+ '&PRICE_AMOUNT=' + URLClientEncode(getValue_('PRICE_AMOUNT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM3_LIST_PRICE=' + URLClientEncode(getValue_('ITEM3_LIST_PRICE',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM3_DISCOUNT',i,'Discount') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('PRICE_AMOUNT',i,0);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validatePartOwnership(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkPartOwnership(i) ) return;\n");
        appendDirtyJavaScript(" if( getValue_('PART_OWNERSHIP',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=PART_OWNERSHIP'\n");
        appendDirtyJavaScript("		+ '&PART_OWNERSHIP=' + SelectURLClientEncode('PART_OWNERSHIP',i)		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'PART_OWNERSHIP',i,'Ownership') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('PART_OWNERSHIP_DB',i,0);\n");
        appendDirtyJavaScript("		if (f.PART_OWNERSHIP_DB.value == 'CONSIGNMENT'){\n");
        appendDirtyJavaScript("		alert('");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWSEPARATESTANDARDJOB2INVOWNER1: Ownership type Consignment is not allowed in Materials for Standard Jobs."));
        appendDirtyJavaScript("'); \n");
        appendDirtyJavaScript("      f.PART_OWNERSHIP.value = ''; \n");
        appendDirtyJavaScript("      f.PART_OWNERSHIP_DB.value = ''; \n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("		if (f.PART_OWNERSHIP_DB.value == 'SUPPLIER LOANED'){\n");
        appendDirtyJavaScript("		alert('");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWSEPARATESTANDARDJOB2INVOWNER2: Ownership type Supplier Loaned is not allowed in Materials for Standard Jobs."));
        appendDirtyJavaScript("'); \n");
        appendDirtyJavaScript("      f.PART_OWNERSHIP.value = ''; \n");
        appendDirtyJavaScript("      f.PART_OWNERSHIP_DB.value = ''; \n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript(" }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovOwner(i,params)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	  if(params) param = params;\n");
        appendDirtyJavaScript("	  else param = '';\n");
        appendDirtyJavaScript("	  var enable_multichoice =(true && ITEM1_IN_FIND_MODE);\n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'CUSTOMER OWNED') \n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("	  var key_value = (getValue_('OWNER',i).indexOf('%') !=-1)? getValue_('OWNER',i):'';\n");
        appendDirtyJavaScript("	  openLOVWindow('OWNER',i,\n");
        appendDirtyJavaScript("'");
        appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CUSTOMER_INFO&__FIELD=Owner&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("+ '&__KEY_VALUE=' + URLClientEncode(getValue_('OWNER',i))\n");
        appendDirtyJavaScript("+ '&OWNER=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript(",550,500,'validateOwner');\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateOwner(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("      setDirty();\n");
        appendDirtyJavaScript("   if( !checkOwner(i) ) return;\n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'CUSTOMER OWNED') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      if( getValue_('OWNER',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("      if( getValue_('PART_OWNERSHIP_DB',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("      if( getValue_('OWNER',i)=='' )\n");
        appendDirtyJavaScript("      {\n");
        appendDirtyJavaScript("         getField_('OWNER_NAME',i).value = '';\n");
        appendDirtyJavaScript("         return;\n");
        appendDirtyJavaScript("      }\n");
        appendDirtyJavaScript("      window.status='Please wait for validation';\n");
        appendDirtyJavaScript("      r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=OWNER'\n");
        appendDirtyJavaScript("                    + '&OWNER=' + URLClientEncode(getValue_('OWNER',i))\n");
        appendDirtyJavaScript("                    + '&PART_OWNERSHIP_DB=' + URLClientEncode(getValue_('PART_OWNERSHIP_DB',i))\n");
        appendDirtyJavaScript("                   );\n");
        appendDirtyJavaScript("      window.status='';\n");
        appendDirtyJavaScript("      if( checkStatus_(r,'OWNER',i,'Owner') )\n");
        appendDirtyJavaScript("      {\n");
        appendDirtyJavaScript("         assignValue_('OWNER_NAME',i,0);\n");
        appendDirtyJavaScript("      }\n");
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'COMPANY OWNED' && f.OWNER.value != '') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWSEPARATESTANDARDJOB2INVOWNER11: Owner should not be specified for Company Owned Stock."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.OWNER.value = ''; \n");
        appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'CONSIGNMENT' && f.OWNER.value != '') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWSEPARATESTANDARDJOB2INVOWNER12: Owner should not be specified for Consignment Stock."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.OWNER.value = ''; \n");
        appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'SUPPLIER LOANED' && f.OWNER.value != '') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWSEPARATESTANDARDJOB2INVOWNER13: Owner should not be specified for Supplier Loaned Stock."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.OWNER.value = ''; \n");
        appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == '' && f.OWNER.value != '') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWSEPARATESTANDARDJOB2INVOWNER14: Owner should not be specified when there is no Ownership type."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.OWNER.value = ''; \n");
        appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("}\n");
        
        appendDirtyJavaScript("function lovTeamId(i,params)"); 
        appendDirtyJavaScript("{"); 
        appendDirtyJavaScript("	if(params) param = params;\n"); 
        appendDirtyJavaScript("	else param = '';\n"); 
        appendDirtyJavaScript("	whereCond1 = '';\n"); 
        appendDirtyJavaScript("	whereCond2 = '';\n");
        appendDirtyJavaScript("	whereCond1 = \"COMPANY = Site_API.Get_Company('\" +URLClientEncode(document.form.ITEM0_STD_JOB_CONTRACT.value)+\"')\";\n"); 
        appendDirtyJavaScript(" if(document.form.TEAM_CONTRACT.value != '')\n");
        appendDirtyJavaScript("		whereCond1 += \" AND CONTRACT = '\" +URLClientEncode(document.form.TEAM_CONTRACT.value)+\"' \";\n"); 
        appendDirtyJavaScript(" else\n");
        appendDirtyJavaScript("		whereCond1 += \" AND CONTRACT IS NOT NULL \";\n"); 
        appendDirtyJavaScript("	whereCond1 += \" AND to_date(to_char(sysdate,\'YYYYMMDD\'),\'YYYYMMDD\') BETWEEN nvl(VALID_FROM,to_date(\'00010101\',\'YYYYMMDD\')) AND nvl(VALID_TO,to_date(\'99991231\',\'YYYYMMDD\')) \";\n"); 
        appendDirtyJavaScript(" if(document.form.ORG_CODE.value != '')\n");
        appendDirtyJavaScript(" {\n");
        appendDirtyJavaScript(" if( whereCond2=='')\n");
        appendDirtyJavaScript("		whereCond2 = \"MAINT_ORG = '\" +URLClientEncode(document.form.ORG_CODE.value)+\"' \";\n"); 
        appendDirtyJavaScript(" else \n");
        appendDirtyJavaScript("		whereCond2+= \" AND MAINT_ORG = '\" +URLClientEncode(document.form.ORG_CODE.value)+\"' \";\n"); 
        appendDirtyJavaScript(" }\n");
        appendDirtyJavaScript(" if(document.form.ORG_CONTRACT.value != '')\n");
        appendDirtyJavaScript(" {\n");
        appendDirtyJavaScript(" if(whereCond2=='' )\n");
        appendDirtyJavaScript("		whereCond2 = \"MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ORG_CONTRACT.value)+\"' \";\n"); 
        appendDirtyJavaScript(" else \n");
        appendDirtyJavaScript("		whereCond2 += \" AND MAINT_ORG_CONTRACT = '\" +URLClientEncode(document.form.ORG_CONTRACT.value)+\"' \";\n"); 
        appendDirtyJavaScript(" }\n");
        appendDirtyJavaScript(" if(whereCond2 !='' )\n");
        appendDirtyJavaScript("     {\n");
        appendDirtyJavaScript("        if(whereCond1 !='' )\n");
        appendDirtyJavaScript("	           whereCond1 += \" AND \";\n"); 
        appendDirtyJavaScript("        if(document.form.ROLE_CODE.value == '' )\n");
        appendDirtyJavaScript("	           whereCond1 += \" TEAM_ID IN (SELECT TEAM_ID FROM MAINT_TEAM_MEMBER_SERV WHERE \" +whereCond2 +\")\";\n"); 
        appendDirtyJavaScript("        else \n");
        appendDirtyJavaScript("	           whereCond1 += \" (TEAM_ID,1) IN (SELECT TEAM_ID,Employee_Role_API.Check_Exist(COMPANY,MEMBER_EMP_NO,'\"+ document.form.ROLE_CODE.value+\"',MAINT_ORG,MAINT_ORG_CONTRACT) FROM MAINT_TEAM_MEMBER_SERV WHERE \" +whereCond2 +\")\"		;\n"); 
        appendDirtyJavaScript("     }\n");
        appendDirtyJavaScript("	var enable_multichoice =(true && ITEM0_IN_FIND_MODE);\n"); 
        appendDirtyJavaScript("	var key_value = (getValue_('TEAM_ID',i).indexOf('%') !=-1)? getValue_('TEAM_ID',i):'';\n"); 
        appendDirtyJavaScript("	openLOVWindow('TEAM_ID',i,\n"); 
        appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=MAINT_TEAM&__FIELD=Team+Id&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''"); 
        appendDirtyJavaScript("		+ '&__KEY_VALUE=' + URLClientEncode(getValue_('TEAM_ID',i))"); 
        appendDirtyJavaScript("		+ '&TEAM_ID=' + URLClientEncode(key_value)"); 
        appendDirtyJavaScript("		,550,500,'validateTeamId');\n"); 
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovTeamContract(i,params)"); 
        appendDirtyJavaScript("{"); 
        appendDirtyJavaScript("	if(params) param = params;\n"); 
        appendDirtyJavaScript("	else param = '';\n"); 
        appendDirtyJavaScript("	   whereCond1 = \" Site_API.Get_Company(CONTRACT) = Site_API.Get_Company('\" + URLClientEncode(document.form.ITEM0_STD_JOB_CONTRACT.value) + \"') \";\n"); 
        appendDirtyJavaScript("	var enable_multichoice =(true && ITEM0_IN_FIND_MODE);\n"); 
        appendDirtyJavaScript("	var key_value = (getValue_('TEAM_CONTRACT',i).indexOf('%') !=-1)? getValue_('TEAM_CONTRACT',i):'';\n"); 
        appendDirtyJavaScript("	openLOVWindow('TEAM_CONTRACT',i,\n"); 
        appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=USER_ALLOWED_SITE_LOV&__FIELD=Team+Site&__INIT=1'+param+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+''"); 
        appendDirtyJavaScript("		+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ORG_CONTRACT',i))"); 
        appendDirtyJavaScript("		+ '&ORG_CONTRACT=' + URLClientEncode(key_value)");
        appendDirtyJavaScript("         + '&COMPANY=' + URLClientEncode(getValue_('COMPANY',i))\n");
        appendDirtyJavaScript("		,550,500,'validateTeamContract');\n"); 
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovPredecessor(i,params)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   document.form.REFRESHPARENT.value = \"TRUE\";\n");
        appendDirtyJavaScript("   submit();\n");  
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function setPredValues(sPredList,sblock)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("     if (sblock =='ITEM0') \n");
        appendDirtyJavaScript("  {\n");
        appendDirtyJavaScript("if (sPredList!='-1') \n");
        appendDirtyJavaScript("        getField_('PREDECESSOR', -1).value = sPredList;\n");
        appendDirtyJavaScript("else\n");
        appendDirtyJavaScript("       getField_('PREDECESSOR', -1).value = '';\n");
        appendDirtyJavaScript("  }\n"); 
        appendDirtyJavaScript("}\n");

        
        appendDirtyJavaScript("function lovDelimitationId(i,params)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	whereCond1 = '';\n"); 
        appendDirtyJavaScript(" if (document.form.PERMIT_TYPE_ID.value != '') \n");
        appendDirtyJavaScript(" {\n");
        appendDirtyJavaScript("	      whereCond1 = \" ISOLATION_TYPE IN (SELECT isolation_type FROM conn_permit_isolation u WHERE u.permit_type_id = '\" + URLClientEncode(document.form.PERMIT_TYPE_ID.value) + \"')\";\n"); 
        appendDirtyJavaScript("    }\n"); 

        appendDirtyJavaScript("if(params) param = params;\n");
	appendDirtyJavaScript("else param = '';\n");
	appendDirtyJavaScript("var enable_multichoice =(true && ITEM5_IN_FIND_MODE);\n");
	appendDirtyJavaScript("var key_value = (getValue_('DELIMITATION_ID',i).indexOf('%') !=-1)? getValue_('DELIMITATION_ID',i):'';\n");
	appendDirtyJavaScript("openLOVWindow('DELIMITATION_ID',i,\n");
	appendDirtyJavaScript("	APP_ROOT + 'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=CONN_DELIMITATION&__FIELD=Isolation+ID&__INIT=1'+'&__WHERE='+whereCond1+'&MULTICHOICE='+enable_multichoice+'&__MASK=%23%23%23%2C%23%23%23%2C%23%23%23%2C%23%230.%23%23'\n");
	appendDirtyJavaScript("	+ '&__KEY_VALUE=' + URLClientEncode(getValue_('DELIMITATION_ID',i))\n");
	appendDirtyJavaScript("	+ '&DELIMITATION_ID=' + URLClientEncode(key_value)\n");
	appendDirtyJavaScript("	,600,445,'validateDelimitationId');\n");
        appendDirtyJavaScript("}\n");  

        appendDirtyJavaScript("function validateConditionCode(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM1',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkConditionCode(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('ITEM1_CONTRACT',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("	if( getValue_('PART_NO',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("	if( getValue_('ITEM1_CATALOG_NO',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("	if( getValue_('PLANNED_QTY',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("	if( getValue_('ITEM1_DISCOUNT',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("	if( getValue_('ITEM1_QTY_TO_INVOICE',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("	if( getValue_('CONDITION_CODE',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("	if( getValue_('PLANNED_QTY',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("	if( getValue_('CONDITION_CODE',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('ITEM1_SALES_PRICE',i).value = '';\n");
        appendDirtyJavaScript("		getField_('CONDITION_CODE_DESC',i).value = '';\n");
        appendDirtyJavaScript("		getField_('ITEM1_PRICE_AMOUNT',i).value = '';\n");
        appendDirtyJavaScript("		getField_('ITEM1PRTCOST',i).value = '';\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	 r = __connect(\n");
        appendDirtyJavaScript("		APP_ROOT+ 'pcmw/SeparateStandardJob2.page'+'?VALIDATE=CONDITION_CODE'\n");
        appendDirtyJavaScript("		+ '&ITEM1_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM1_CATALOG_NO=' + URLClientEncode(getValue_('ITEM1_CATALOG_NO',i))\n");
        appendDirtyJavaScript("		+ '&PLANNED_QTY=' + URLClientEncode(getValue_('PLANNED_QTY',i))\n");
        appendDirtyJavaScript("		+ '&ITEM1_DISCOUNT=' + URLClientEncode(getValue_('ITEM1_DISCOUNT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM1_QTY_TO_INVOICE=' + URLClientEncode(getValue_('ITEM1_QTY_TO_INVOICE',i))\n");
        appendDirtyJavaScript("		+ '&CONDITION_CODE=' + URLClientEncode(getValue_('CONDITION_CODE',i))\n");
        appendDirtyJavaScript("		+ '&PLANNED_QTY=' + URLClientEncode(getValue_('PLANNED_QTY',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'CONDITION_CODE',i,'Condition Code') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM1_SALES_PRICE',i,0);\n");
        appendDirtyJavaScript("		assignValue_('CONDITION_CODE_DESC',i,1);\n");
        appendDirtyJavaScript("		assignValue_('ITEM1PRTCOST',i,2);\n");
        appendDirtyJavaScript("		assignValue_('ITEM1_PRICE_AMOUNT',i,3);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function checkItem0Fields()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("      if (f.ORG_CODE.value != '' && f.ORG_CONTRACT.value == '')  \n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWSEPARATESTANDARDJOB2ORGMASITEMAN: The field [Maint. Org. Site] must have a value"));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("   return false;\n"); 
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("  return checkDescription(0);\n");
        appendDirtyJavaScript("}\n"); 

        if (bOpenNewWindow)
        {
            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));	//XSS_Safe AMNILK 20070726
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=");
            appendDirtyJavaScript(newWinWidth);
            appendDirtyJavaScript(",height=");
            appendDirtyJavaScript(newWinHeight);
            appendDirtyJavaScript("\"); \n");       
        }
    }
}
