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
*  File        : RoundStandardJob.java 
*  Created     : SHFELK  010220
*  Modified    :
*  JEWILK  010403  Changed file extensions from .asp to .page
*  BUNILK  010718  Modified ITEM0_PART_NO field validation
*  CHCRLK  010801  Added field Price Amount and added validations.
*  CHCRLK  010806  Modified validation for field ITEM0_PART_NO. 
*  BUNILK  010806  Made some changes in return AutoString of getContents() function so that to call       
*                  checkPartVal() javascript function from onBlur event of PART_NO field. Done some changes in 
*                  validateItem0PartNo() method also.
*  CHCRLK  010809  Modified validations for Price Amount. 
*  ARWILK  010818  Added LOV for Purchase Part and Modified validation for ITEM0_PART_NO.
*  CHCRLK  010822  Modified validations to correct format number error.
*  INROLK  011015  Added Security Check to RMBs. 
*  VAGULK  020316  Added action "Copy Standard Job" (78774)
*  VAGULK  020531  Bud Id 30468 
*  SHAFLK  020603  Bug Id 30463, changed newRow().
*  BUNILK  020906  Bug ID 32182 Added a new method isModuleInst() to check availability of 
*                  ORDER module inside preDefine method and checked availability for DOCMAN methods also.    
*  BUNILK  021212  Merged with 2002-3 SP3
*  CHCRLK  030502  Added CONDITION_CODE & CONDITION_CODE_DESC to Materials tab.
*  CHAMLK  030728  Added Part Ownership, Owner and Owner Name to Materials tab.
*  SHTOLK  031013  Call Id 107335, Modified okFindITEM1().   
*  THWILK  031017  Call Id 107371, Modified updatePMActions() method thereby checked for state instead of objstate.   
*  CHAMLK  031018  Passed Company and Created by when calling CopyStandardJobDlg.page
*  THWILK  031028  Call Id 107371, Modified updatePMActions() method and run method.
*  SAPRLK  031217  Web Alignment - removed methods clone() and doReset(). 
*  SHAFLK  040120  Bug Id 41815,Removed Java dependencies.
*  SAPRLK  040325  Merged with SP1.
*  SAPRLK  040415  Web Alignment, enabled multirow action, simplify code for RMBs, change conditional code in validate method,
*                  remove unnecessary global variables.
*  ARWILK  040806  Added Custom validations to field CONTRACT.
*  ARWILK  040819  Modified method updatePMActions.
*  NIJALK  040830  Call 117469, Modified setCheckBoxValues().
*  NIJALK  041018  Added Custom function createNewRevision(). 
*  NIJALK  041021  Modified Adjust(), PreDefine(). 
*  ARWILK  041022  Did some major changes to the GUI and functionality. (Spec AMEC111 - Standard Jobs as PM Templates)
*  NIJALK  041103  Modified predefine(), Added methods showObsoletePM(), updatePM(),generatePMRevision().
*  NIJALK  041108  Added field STANDARD_JOB_TYPE_DB to headblk. Modified generatePMRevision().
*  NIJALK  041109  Duplicated Translation Tags Corrected.
*  NIJALK  041110  Added new field HAS_OBS_WO to headblk & added new method showObsoleteWORev().
*  NAMELK  041110  Duplicated Translation Tags Corrected.
*  ARWILK  041112  Replced getContents with printContents.
*  NIJALK  041124  Modified preDefine().
*  NIJALK  050113  Field "DISCOUNT" made hidden.
*  NIJALK  050117  Modified Site fields on Material Requisions Tab.
*  NIJALK  050126  Modified validation to ITEM0_PART_NO.
*  NIJALK  050203  Modified validate(),predefine().
*  NIJALK  050208  Modified run(),createNewRevision(),okFind().
*  NIJALK  050210  Removed function updatePMActions().
*  NIJALK  050401  Call 122927: Modified adjust().
*  NIJALK  050520  Modified avaiDetails(), preDefine().
*  SHAFLK  050425  Bug 50830, Used translateJavaScript() method to get the correctly translated strings in JavaScript functions.
*  NIJALK  050617  Merged bug 50830. 
*  AMNILK  051006  Call Id:127600. Modified method preDefine and validate.Add custom validations to ORG_CODE and ORG_CONTRACT.
*  THWILK  051103  Call Id:128445, Modified propereties of Site in Predefine().
*  NIJALK  051111  Bug 128833: Modified preDefine().
*  NIJALK  051212  Bug 129893: Field WORK_DESCRIPTION/Remove Mandatory.
*  NIJALK  050631  Bug 132231: Added methods saveReturn(),saveNew() with validations to "Start Value" & "Start Unit".
*                  Shifted the code for refreshing the headset to saveReturn() from run().
*  NIJALK  060306  Call 136376: Modified preDefine().
*  NIJALK  060307  Call 136390: Enabled DocMan for headblk.
*  SULILK  060311  Call 136679: Modified activatePm(),run().okFind(),okFindITEM0(),newRowITEM0(),getPriceAmount(),preDefine() and
                                added removeITEM0(),saveReturnITEM0(),okFindALLITEM0().
*  SULILK  060321  Call 135197: Modified preDefine(),validate().
*  NIJALK  060323  Modified createNewRevision().
*  SULILK  060324  Modified removeITEM0().
*  SHAFLK  060525  Bug 57598, Modified validation for PART_NO.
*  AMNILK  060629  Merged with SP1 APP7.
*  AMNILK  060727  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMDILK  060807  Merged with Bug Id 58214
*  AMDILK  060811  Bug 58216, Eliminated SQL errors in web applications. Modified the methods okFindITEM0(),
*                             okFindALLITEM0(),okFindITEM1(), okFindITEM2(), okFindITEM3(), countFindITEM0(),
*                             countFindITEM1(), countFindITEM2(), countFindITEM3(), activatePm()
*  AMDILK  060904  Merged with the Bug Id 58216
*  SHAFLK  061222  Bug 61959  Added Budget tab.
*  AMNILK  070202  Merged LCS Bug Id: 61959.     
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  AMDILK  070330  Merged bug id 64068
*  AMNILK  070725  Eliminated XSS Security Vulnerability.
*  AMDILK  070801  Removed the scroll buttons of the parent when the child tabs are in new or edit modes
*  AMDILK  070803  Removed the scroll buttons of the parent when the child tabs are in new or edit modes 02
*  SHAFLK  081105  Bug 77824, Modified preDefine().
*  CHANLK  090330  Bug 78556, Modified saveReturnITEM6().
*  SHAFLK  080505  Bug 82435, Modified preDefine().
*  SHAFLK  080513  Bug 78586, Modified preDefine().
*  SHAFLK  100503  Bug 89834, Modified preDefine().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class RoundStandardJob extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.RoundStandardJob");

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

    private ASPBlock itemblk6;
    private ASPRowSet itemset6;
    private ASPCommandBar itembar6;
    private ASPTable itemtbl6;
    private ASPBlockLayout itemlay6;

    private ASPBlock eventblk;
    private ASPRowSet eventset;
    private ASPField f;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private ASPTransactionBuffer trans1;
    private ASPTransactionBuffer secBuff;
    private ASPCommand cmd;
    private ASPBuffer data;
    private ASPQuery q;
    private ASPBuffer buffer;
    private ASPBuffer r;
    private ASPBuffer row;

    private int currrow;
    private int headrowno;
    private int n;
    private int i;
    private int newWinHeight = 600;
    private int newWinWidth = 900;

    private String sta;
    private String current_url;
    private String created_by;
    private String created_by_id;
    private String created_date;
    private String title_;
    private String xx;
    private String yy;
    private String fldTitleCreatedBy;
    private String fldTitleChangedBy;
    private String lovTitleCreatedBy;
    private String lovTitleChangedBy;
    private String isSecure[];
    private String item0RowStatus;
    private String urlString;
    private String newWinHandle;
    private String dateMask;
    private String laymode;
    private String qrystr;
    private String new_std_job_id;
    private String new_std_job_revision;
    private String new_contract;

    private boolean again;
    private boolean cuQuInHand_;
    private boolean avaiDetails_;
    private boolean invenPartLimited_;
    private boolean suppPerPart_;
    private boolean bOpenNewWindow;

    private ASPTabContainer tabs;

    private String planMatRev;
    private String planExtRev;
    private String matBudg;
    private String extBudg;
    private String totBudg;
    private String planMat;
    private String planExt;
    private String planTot;
    private String planTotRev;

    //===============================================================
    // Construction 
    //===============================================================
    public RoundStandardJob(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        isSecure =  new String[11]; 
        qrystr =  "";
        new_std_job_id = "";
        new_std_job_revision = "";
        new_contract = "";
        ASPManager mgr = getASPManager();

        ctx = mgr.getASPContext();
        fmt = mgr.newASPHTMLFormatter();
        trans = mgr.newASPTransactionBuffer();
        trans1 = mgr.newASPTransactionBuffer();

        again = ctx.readFlag("AGAIN",again);
        cuQuInHand_ = ctx.readFlag("QTYONHAND",false);
        avaiDetails_ = ctx.readFlag("AVAILIBILITYDET",false);
        invenPartLimited_ = ctx.readFlag("INVENTORYPART",false);
        suppPerPart_ = ctx.readFlag("SUPPPERPART",false);
        qrystr = ctx.readValue("QRYSTR",qrystr);
        laymode = ctx.readValue("LAYMODE","");

        if (mgr.commandBarActivated())
        {
            eval(mgr.commandBarFunction());
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
        {
            okFind();
        }
            
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
            int i=0;
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

        ctx.writeFlag("AGAIN",again);
        ctx.writeFlag("QTYONHAND",cuQuInHand_);
        ctx.writeFlag("AVAILIBILITYDET",avaiDetails_); 
        ctx.writeFlag("INVENTORYPART",invenPartLimited_);
        ctx.writeFlag("SUPPPERPART",suppPerPart_);
        ctx.writeValue("QRYSTR",qrystr);
        ctx.writeValue("LAYMODE",laymode);
    }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
// This function is used to check security status for methods which
// are in modules Order,Purch and Invent .This function is called before
// the required method is performed.  -NUPE,21-07-2000

    public boolean  checksec( String method,int ref)
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

        String val = null;
        String txt = null; 
        String txt1 = null;
        String cr = null;
        String ch = null;
        String padescr = null;
        String dqua = null;
        String typedes = null;
        String pauncode = null;
        String catDesc;
        double nCost = 0;
        String sCatalogNo;
        double listPrice = 0;
        isSecure = new String[11];

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
                        
            txt = (mgr.isEmpty (sComp) ? "" : (sComp)) + "^" + 
                  (mgr.isEmpty (sCalendar) ? "" : sCalendar) +"^"+
                  (mgr.isEmpty (sCalDesc) ? "" : sCalDesc) +"^";  
            mgr.responseWrite(txt);
        }

        else if ("CREATED_BY".equals(val))
        {
            cmd=trans.addCustomFunction("GEN1","Company_Emp_API.Get_Max_Employee_Id","CREATED_BY_ID");
            cmd.addParameter("COMPANY");
            cmd.addParameter("CREATED_BY");

            trans=mgr.validate(trans);
            cr = trans.getValue("GEN1/DATA/CREATED_BY_ID");
            txt = (mgr.isEmpty(cr) ? "" : (cr))+ "^" ;
            mgr.responseWrite(txt);
        }
        else if ("CHANGED_BY".equals(val))
        {
            cmd=trans.addCustomFunction("GEN2","Company_Emp_API.Get_Max_Employee_Id","CHANGED_BY_ID");
            cmd.addParameter("COMPANY");
            cmd.addParameter("CHANGED_BY");

            trans=mgr.validate(trans);
            ch = trans.getValue("GEN2/DATA/CHANGED_BY_ID");
            txt1 = (mgr.isEmpty(ch) ? "" : (ch))+ "^" ; 
            mgr.responseWrite(txt1);
        }
        else if ("ITEM0_PART_NO".equals(val))
        {
            String sCondCodeUage = "";
            String sCondCode = "";
            String sCondDesc = "";
            String activeInd = "";

            if (checksec("Purchase_Part_API.Get_Description",1))
            {
                cmd=trans.addCustomFunction("GEN3","Purchase_Part_API.Get_Description","ITEM0_PARTDESCRIPTION");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("ITEM0_PART_NO");
            }

            if (checksec("INVENTORY_PART_API.Get_Dim_Quality",2))
            {
                cmd=trans.addCustomFunction("GEN4","INVENTORY_PART_API.Get_Dim_Quality","ITEM0_DIMQUALITY");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("ITEM0_PART_NO");
            }

            if (checksec("INVENTORY_PART_API.Get_Type_Designation",3))
            {
                cmd=trans.addCustomFunction("GEN5","INVENTORY_PART_API.Get_Type_Designation","ITEM0_TYPEDESIGNATION");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("ITEM0_PART_NO");
            }

            if (checksec("INVENTORY_PART_API.Get_Unit_Meas",4))
            {
                cmd=trans.addCustomFunction("GEN6","INVENTORY_PART_API.Get_Unit_Meas","ITEM0_PARTUNITCODE");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("ITEM0_PART_NO");
            }

            if (checksec("Sales_Part_API.Get_Catalog_No_For_Part_No",5))
            {
                cmd = trans.addCustomFunction("CATFORPART","Sales_Part_API.Get_Catalog_No_For_Part_No","ITEM0_CATALOG_NO");
                cmd.addParameter("CONTRACT",mgr.readValue("ITEM0_CONTRACT",""));
                cmd.addParameter("ITEM0_PART_NO");
            }

            if (checksec("Inventory_Part_API.Get_Description",6))
            {
                cmd = trans.addCustomFunction("PPGDI", "Inventory_Part_API.Get_Description", "ITEM0_PARTDESCRIPTION" );
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("ITEM0_PART_NO");
            }

            if (checksec("INVENTORY_PART_API.Part_Exist",7))
            {
                cmd = trans.addCustomFunction("CHKEX", "INVENTORY_PART_API.Part_Exist", "CHCKEXIST" );
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("ITEM0_PART_NO");
            }

            if (checksec("PART_CATALOG_API.Get_Condition_Code_Usage_Db",8))
            {
                cmd = trans.addCustomFunction("GETCONDUSAGE","PART_CATALOG_API.Get_Condition_Code_Usage_Db","COND_CODE_USAGE");
                cmd.addParameter("ITEM0_PART_NO"); 
            }

            if ( checksec("Sales_Part_API.Get_Activeind",8) )
            {
               cmd = trans.addCustomFunction("GETACT","Sales_Part_API.Get_Activeind","ACTIVEIND");
               cmd.addParameter("CONTRACT",mgr.readValue("ITEM0_CONTRACT",""));
               cmd.addReference("ITEM0_CATALOG_NO","CATFORPART/DATA");
            }

            if ( checksec("Active_Sales_Part_API.Encode",9) )
            {
               cmd = trans.addCustomFunction("GETACTDB","Active_Sales_Part_API.Encode","ACTIVEIND_DB");
               cmd.addReference("ACTIVEIND","GETACT/DATA");
            }
            trans = mgr.validate(trans);

            ref = 0;

            if (isSecure[ref += 1] =="true")
                padescr = trans.getBuffer("GEN3/DATA").getFieldValue("ITEM0_PARTDESCRIPTION");
            else
                padescr = "";

            if (isSecure[ref += 1] =="true")
                dqua = trans.getValue("GEN4/DATA/ITEM0_DIMQUALITY");
            else
                dqua = "";

            if (isSecure[ref += 1] =="true")
                typedes = trans.getBuffer("GEN5/DATA").getFieldValue("ITEM0_TYPEDESIGNATION");
            else
                typedes = "";

            if (isSecure[ref += 1] =="true")
                pauncode = trans.getBuffer("GEN6/DATA").getFieldValue("ITEM0_PARTUNITCODE");
            else
                pauncode    = "";

            if (isSecure[ref += 1] =="true")
                sCatalogNo = trans.getBuffer("CATFORPART/DATA").getFieldValue("ITEM0_CATALOG_NO");
            else
                sCatalogNo = "";

            if ((isSecure[ref += 1] =="true")&&(mgr.isEmpty(padescr)))
                padescr = trans.getValue("PPGDI/DATA/ITEM0_PARTDESCRIPTION");
            else if (mgr.isEmpty(padescr))
                padescr = "";

            int nInventoryFlag = 0;

            if (isSecure[ref += 1] =="true")
                nInventoryFlag = toInt(trans.getNumberValue("CHKEX/DATA/CHCKEXIST"));
            else
                nInventoryFlag  = 0;

            if (isSecure[ref += 1] =="true")
                sCondCodeUage = trans.getValue("GETCONDUSAGE/DATA/COND_CODE_USAGE");
            else
                sCondCodeUage = "";
            if ( "true".equals(isSecure[9]) )
               activeInd = trans.getValue("GETACTDB/DATA/ACTIVEIND_DB");
            else
               activeInd = "";


            String strCatDesc = "";
            String listPriceStr = "";
            String strPriceAmt = "";
            String test = "";

            trans.clear();

            cmd = trans.addCustomFunction("CATEXT","Sales_Part_API.Check_Exist","CAT_EXIST");
            cmd.addParameter("ITEM0_STD_JOB_CONTRACT",mgr.readValue("ITEM0_STD_JOB_CONTRACT"));
            cmd.addParameter("ITEM0_CATALOG_NO",sCatalogNo);

            trans = mgr.validate(trans);

            double nExist = trans.getNumberValue("CATEXT/DATA/CAT_EXIST");

            if (nExist != 1)
                sCatalogNo = "";

            trans.clear();

            if (!test.equals(sCatalogNo))
            {
                trans.clear();
                if (checksec("SALES_PART_API.Get_Catalog_Desc",1))
                {
                    cmd = trans.addCustomFunction("CATDESC","SALES_PART_API.Get_Catalog_Desc","ITEM0_SALESPARTCATALOG_DESC");
                    cmd.addParameter("ITEM0_CONTRACT");
                    cmd.addParameter("ITEM0_CATALOG_NO",sCatalogNo);
                }

                if (checksec("SALES_PART_API.Get_List_Price",2))
                {
                    cmd = trans.addCustomFunction("LISTPRICE","SALES_PART_API.Get_List_Price","NSALESPARTLIST_PRICE");
                    cmd.addParameter("ITEM0_CONTRACT");
                    cmd.addParameter("ITEM0_CATALOG_NO",sCatalogNo);
                }

                trans = mgr.validate(trans);

                ref = 0;

                if (isSecure[ref += 1] =="true")
                    strCatDesc = trans.getValue("CATDESC/DATA/SALESPARTCATALOG_DESC");
                else
                    strCatDesc = "";    

                if (isSecure[ref += 1] =="true")
                    listPrice = trans.getNumberValue("LISTPRICE/DATA/NSALESPARTLIST_PRICE");
                else
                    listPrice =NOT_A_NUMBER;

                listPriceStr = mgr.getASPField("NSALESPARTLIST_PRICE").formatNumber(listPrice);

                //Validation for Price Amount

                double nSalesPriceVal = mgr.readNumberValue("SALES_PRICE");
                //Checking if nSalesPriceVal is NaN is done in method validatePriceAmt()

                //double nSlsPrtListPriceVal = mgr.readNumberValue("NSALESPARTLIST_PRICE");
                if (isNaN(listPrice))
                    listPrice = 0;

                String invTypeVal = mgr.readValue("WORK_ORDER_INVOICE_TYPE");

                double nDiscountVal = mgr.readNumberValue("DISCOUNT");
                if (isNaN(nDiscountVal))
                    nDiscountVal = 0;

                double nQtyToInvVal = mgr.readNumberValue("QTY_TO_INVOICE");
                if (isNaN(nQtyToInvVal))
                    nQtyToInvVal = 0;

                double nPlanQtyVal = mgr.readNumberValue("ITEM0_PLANNED_QTY");
                if (isNaN(nPlanQtyVal))
                    nPlanQtyVal = 0;

                double nValPriceAmt = validatePriceAmt(sCatalogNo,nSalesPriceVal,listPrice,invTypeVal,nDiscountVal,nQtyToInvVal,nPlanQtyVal);

                strPriceAmt = mgr.getASPField("ITEM0_PRICE_AMOUNT").formatNumber(nValPriceAmt);
            }

            trans.clear();  

            cmd = trans.addCustomFunction("SUUPCODE","INVENTORY_PART_API.Get_Supply_Code","SUPPLY_CODE");
            cmd.addParameter("ITEM0_CONTRACT");
            cmd.addParameter("ITEM0_CATALOG_NO",sCatalogNo);

            cmd = trans.addCustomCommand("MATREQLINE","Material_Requis_Line_API.CHECK_PART_NO__");
            cmd.addParameter("ITEM0_PARTDESCRIPTION",padescr);
            cmd.addReference("SUPPLY_CODE","SUUPCODE/DATA");
            cmd.addParameter("ITEM0_PARTUNITCODE",pauncode);
            cmd.addParameter("ITEM0_PART_NO",mgr.readValue("ITEM0_PART_NO"));
            cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));

            if ("ALLOW_COND_CODE".equals(sCondCodeUage))
            {
                cmd = trans.addCustomFunction("GETDEFCOND","Condition_Code_API.Get_Default_Condition_Code","CONDITION_CODE");

                cmd = trans.addCustomFunction("GETCONDDESC","Condition_Code_API.Get_Description","CONDITION_CODE_DESC");
                cmd.addReference("CONDITION_CODE","GETDEFCOND/DATA");
            }

            trans = mgr.validate(trans);  //Changed Perform(trans) to validate(trans)

            if ("ALLOW_COND_CODE".equals(sCondCodeUage))
            {
                sCondCode = trans.getValue("GETDEFCOND/DATA/CONDITION_CODE");
                sCondDesc = trans.getValue("GETCONDDESC/DATA/CONDITION_CODE_DESC");
            }

            trans.clear(); 

            String sSuppID = null;

            if (nInventoryFlag == 1)
            {
                //if (checksec("Inventory_Part_API.Get_Inventory_Value_By_Method",1))
                if (checksec("Active_Separate_API.Get_Inventory_Value",1))
                {
                    /*cmd = trans.addCustomFunction("COSTBUFFONE","Inventory_Part_API.Get_Inventory_Value_By_Method","NSALESPARTCOST");
                    cmd.addParameter("SPARE_CONTRACT");
                    cmd.addParameter("ITEM0_PART_NO"); */

                    cmd = trans.addCustomCommand("COSTBUFFONE","Active_Separate_API.Get_Inventory_Value");
                    cmd.addParameter("NSALESPARTCOST");
                    cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));
                    cmd.addParameter("ITEM0_PART_NO",mgr.readValue("ITEM0_PART_NO"));
                    cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
                    cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
                    cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));
                }

                trans = mgr.validate(trans);
                ref = 0;

                if (isSecure[ref += 1] =="true")
                    nCost = trans.getNumberValue("COSTBUFFONE/DATA/NSALESPARTCOST");
                else
                    nCost   = 0;             
            }
            else
            {
                if (checksec("Purchase_Order_Line_Util_API.Get_Primary_Supplier",1))
                {
                    cmd = trans.addCustomFunction("SUPPCODD_ID","Purchase_Order_Line_Util_API.Get_Primary_Supplier","SUPPLIER_ID");
                    cmd.addParameter("SPARE_CONTRACT");
                    cmd.addParameter("ITEM0_PART_NO");
                }

                trans = mgr.validate(trans);
                ref = 0;

                if (isSecure[ref += 1] =="true")
                    sSuppID = trans.getValue("SUPPCODD_ID/DATA/SUPPLIER_ID");
                else
                    sSuppID = "";
            }

            trans.clear();

            if (!mgr.isEmpty(sSuppID))
            {
                if (checksec("Purchase_Order_Line_Util_API.Get_Base_Price_Supp_Part",1))
                {
                    cmd = trans.addQuery("SYSDATEBUFF","select SYSDATE F_SYSDATE from dual"); 

                    cmd = trans.addCustomCommand("SCOSTVAL","Purchase_Order_Line_Util_API.Get_Base_Price_Supp_Part");
                    cmd.addParameter("NSALESPARTCOST");
                    cmd.addParameter("ITEM0_PART_NO");
                    cmd.addParameter("SPARE_CONTRACT");
                    cmd.addParameter("SUPPLIER_ID",sSuppID);
                    cmd.addParameter("ITEM0_PLANNED_QTY");
                    cmd.addReference("F_SYSDATE","SYSDATEBUFF/DATA");          
                }

                trans = mgr.validate(trans);
                ref = 0;

                if (isSecure[ref += 1] =="true")
                    nCost = trans.getNumberValue("SCOSTVAL/DATA/NSALESPARTCOST");
            }

            String nCostStr = mgr.getASPField("NSALESPARTCOST").formatNumber(nCost);

            trans.clear();

            txt1 = (mgr.isEmpty(padescr) ? "" : (padescr))+ "^" + 
                    (mgr.isEmpty(dqua) ? "" : (dqua))+ "^" + 
                    (mgr.isEmpty(typedes) ? "" : (typedes))+ "^" + 
                    (mgr.isEmpty(pauncode) ? "" : (pauncode))+ "^" + 
                    (mgr.isEmpty(nCostStr) ? "" : (nCostStr)) + "^" + 
                    (mgr.isEmpty(sCatalogNo) ? "" : (sCatalogNo)) + "^" + 
                    (mgr.isEmpty(strCatDesc) ? "" : (strCatDesc))+ "^" + 
                    (mgr.isEmpty(listPriceStr) ? "" : (listPriceStr))+ "^" + 
                    (mgr.isEmpty(strPriceAmt) ? "" : (strPriceAmt))+ "^" +
                    (mgr.isEmpty(sCondCode) ? "" : sCondCode) + "^"+ 
                    (mgr.isEmpty(sCondDesc) ? "" : sCondDesc) + "^"+ 
                    (mgr.isEmpty(activeInd) ? "" : (activeInd))+ "^";

            mgr.responseWrite(txt1);
        }
        else if ("CONDITION_CODE".equals(val))
        {
            double salesPaCost;
            cmd = trans.addCustomFunction("CCDESC", "Condition_Code_API.Get_Description", "CONDITION_CODE_DESC");
            cmd.addParameter("CONDITION_CODE"); 

            trans = mgr.validate(trans);

            String ccDesc = trans.getValue("CCDESC/DATA/CONDITION_CODE_DESC");
            String salesPaCostS = mgr.readValue("NSALESPARTCOST","");

            trans.clear();

            cmd = trans.addCustomCommand("SALESPARTCOST","Active_Separate_API.Get_Inventory_Value");
            cmd.addParameter("NSALESPARTCOST");
            cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));
            cmd.addParameter("ITEM0_PART_NO",mgr.readValue("ITEM0_PART_NO"));
            cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
            cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
            cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));
            
            trans = mgr.validate(trans);

            salesPaCost =  trans.getNumberValue("SALESPARTCOST/DATA/NSALESPARTCOST");
            salesPaCostS = mgr.getASPField("NSALESPARTCOST").formatNumber(salesPaCost);

            txt = (mgr.isEmpty(ccDesc) ? "" : (ccDesc)) + "^" +
                  (mgr.isEmpty(salesPaCostS) ? "" : salesPaCostS)+ "^" ;  

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
        else if ("ITEM0_CATALOG_NO".equals(val))
        {
            if (checksec("SALES_PART_API.Get_Catalog_Desc",1))
            {
                cmd = trans.addCustomFunction("CATDESC","SALES_PART_API.Get_Catalog_Desc","ITEM0_SALESPARTCATALOG_DESC");
                cmd.addParameter("ITEM0_CONTRACT");
                cmd.addParameter("ITEM0_CATALOG_NO");
            }

            if (checksec("SALES_PART_API.Get_List_Price",2))
            {
                cmd = trans.addCustomFunction("LISTPRICE","SALES_PART_API.Get_List_Price","NSALESPARTLIST_PRICE");
                cmd.addParameter("ITEM0_CONTRACT");
                cmd.addParameter("ITEM0_CATALOG_NO");
            }

            if (checksec("INVENTORY_PART_API.Part_Exist",3))
            {
                cmd = trans.addCustomFunction("CHKEX", "INVENTORY_PART_API.Part_Exist", "CHCKEXIST" );
                cmd.addParameter("ITEM0_CONTRACT");
                cmd.addParameter("ITEM0_PART_NO");
            }

            trans = mgr.validate(trans);

            ref = 0;        

            if (isSecure[ref += 1] =="true")
                catDesc = trans.getValue("CATDESC/DATA/SALESPARTCATALOG_DESC");
            else
                catDesc = "";    

            listPrice = mgr.readNumberValue("NSALESPARTLIST_PRICE");

            if (isSecure[ref += 1] =="true")
                listPrice = trans.getNumberValue("LISTPRICE/DATA/NSALESPARTLIST_PRICE");
            else
                listPrice =NOT_A_NUMBER;         

            int nInventoryFlag = 0;

            if (isSecure[ref += 1] =="true")
                nInventoryFlag = toInt(trans.getNumberValue("CHKEX/DATA/CHCKEXIST"));
            else
                nInventoryFlag  = 0;        

            trans.clear();

            String sSuppID = null;

            if (nInventoryFlag == 1)
            {
                //if (checksec("Inventory_Part_API.Get_Inventory_Value_By_Method",1))
                if (checksec("Active_Separate_API.Get_Inventory_Value",1))
                {
                    /*cmd = trans.addCustomFunction("COSTBUFFONE","Inventory_Part_API.Get_Inventory_Value_By_Method","NSALESPARTCOST");
                    cmd.addParameter("ITEM0_CONTRACT");
                    cmd.addParameter("ITEM0_PART_NO"); */

                    cmd = trans.addCustomCommand("COSTBUFFONE","Active_Separate_API.Get_Inventory_Value");
                    cmd.addParameter("NSALESPARTCOST");
                    cmd.addParameter("ITEM0_CONTRACT",mgr.readValue("ITEM0_CONTRACT"));
                    cmd.addParameter("ITEM0_PART_NO",mgr.readValue("ITEM0_PART_NO"));
                    cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
                    cmd.addParameter("CONFIGURATION_ID",mgr.isEmpty(mgr.readValue("CONFIGURATION_ID"))?"*":mgr.readValue("CONFIGURATION_ID"));
                    cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));

                }

                trans = mgr.validate(trans);
                ref = 0;

                if (isSecure[ref += 1] =="true")
                    nCost = trans.getNumberValue("COSTBUFFONE/DATA/NSALESPARTCOST");
                else
                    nCost   = 0;             
            }
            else
            {
                if (checksec("Purchase_Order_Line_Util_API.Get_Primary_Supplier",1))
                {
                    cmd = trans.addCustomFunction("SUPPCODD_ID","Purchase_Order_Line_Util_API.Get_Primary_Supplier","SUPPLIER_ID");
                    cmd.addParameter("ITEM0_CONTRACT");
                    cmd.addParameter("ITEM0_PART_NO");
                }

                trans = mgr.validate(trans);
                ref = 0;

                if (isSecure[ref += 1] =="true")
                    sSuppID = trans.getValue("SUPPCODD_ID/DATA/SUPPLIER_ID");
                else
                    sSuppID = "";
            }

            trans.clear();

            if (!mgr.isEmpty(sSuppID))
            {
                if (checksec("Purchase_Order_Line_Util_API.Get_Base_Price_Supp_Part",1))
                {
                    cmd = trans.addQuery("SYSDATEBUFF","select SYSDATE F_SYSDATE from dual"); 

                    cmd = trans.addCustomCommand("SCOSTVAL","Purchase_Order_Line_Util_API.Get_Base_Price_Supp_Part");
                    cmd.addParameter("NSALESPARTCOST");
                    cmd.addParameter("ITEM0_PART_NO");
                    cmd.addParameter("ITEM0_CONTRACT");
                    cmd.addParameter("SUPPLIER_ID",sSuppID);
                    cmd.addParameter("ITEM0_PLANNED_QTY");
                    cmd.addReference("F_SYSDATE","SYSDATEBUFF/DATA");          
                }

                trans = mgr.validate(trans);
                ref = 0;

                if (isSecure[ref += 1] =="true")
                    nCost = trans.getNumberValue("SCOSTVAL/DATA/NSALESPARTCOST");
            }

            String nCostStr = mgr.getASPField("NSALESPARTCOST").formatNumber(nCost);

            //Validation for Price Amount
            String salesPartNoVal = mgr.readValue("ITEM0_CATALOG_NO");

            double nSalesPriceVal = mgr.readNumberValue("SALES_PRICE");
            //Checking if nSalesPriceVal is NaN is done in method validatePriceAmt()

            if (isNaN(listPrice))
                listPrice = 0;

            String invTypeVal = mgr.readValue("WORK_ORDER_INVOICE_TYPE");

            double nDiscountVal = mgr.readNumberValue("DISCOUNT");

            if (isNaN(nDiscountVal))
                nDiscountVal = 0;

            double nQtyToInvVal = mgr.readNumberValue("QTY_TO_INVOICE");
            if (isNaN(nQtyToInvVal))
                nQtyToInvVal = 0;

            double nPlanQtyVal = mgr.readNumberValue("ITEM0_PLANNED_QTY");

            if (isNaN(nPlanQtyVal))
                nPlanQtyVal = 0;

            double nValPriceAmt = validatePriceAmt(salesPartNoVal,nSalesPriceVal,listPrice,invTypeVal,nDiscountVal,nQtyToInvVal,nPlanQtyVal);

            String strPriceAmt = mgr.getASPField("ITEM0_PRICE_AMOUNT").formatNumber(nValPriceAmt);
            String strListPrice = mgr.getASPField("NSALESPARTLIST_PRICE").formatNumber(listPrice);

            txt1 = (mgr.isEmpty(catDesc) ? "" : (catDesc))+ "^" + (mgr.isEmpty(strListPrice) ? "" : (strListPrice)) + "^" + (mgr.isEmpty(nCostStr) ? "" : (nCostStr))+ "^" + (mgr.isEmpty(strPriceAmt) ? "" : (strPriceAmt))+ "^" ;

            mgr.responseWrite(txt1);
        }
        else if (("SALES_PRICE".equals(val)) || ("DISCOUNT".equals(val)) || ("QTY_TO_INVOICE".equals(val)) || ("WORK_ORDER_INVOICE_TYPE".equals(val)) || ("ITEM0_PLANNED_QTY".equals(val)))
        {
            String salesPartNoVal = mgr.readValue("ITEM0_CATALOG_NO");

            double nSalesPriceVal = mgr.readNumberValue("SALES_PRICE");
            //Checking if nSalesPriceVal is NaN is done in method validatePriceAmt()

            double nSlsPrtListPriceVal = mgr.readNumberValue("NSALESPARTLIST_PRICE");
            if (isNaN(nSlsPrtListPriceVal))
                nSlsPrtListPriceVal = 0;

            String invTypeVal = mgr.readValue("WORK_ORDER_INVOICE_TYPE");

            double nDiscountVal = mgr.readNumberValue("DISCOUNT");
            if (isNaN(nDiscountVal))
                nDiscountVal = 0;

            double nQtyToInvVal = mgr.readNumberValue("QTY_TO_INVOICE");
            if (isNaN(nQtyToInvVal))
                nQtyToInvVal = 0;

            double nPlanQtyVal = mgr.readNumberValue("ITEM0_PLANNED_QTY");
            if (isNaN(nPlanQtyVal))
                nPlanQtyVal = 0;

            double nValPriceAmt = validatePriceAmt(salesPartNoVal,nSalesPriceVal,nSlsPrtListPriceVal,invTypeVal,nDiscountVal,nQtyToInvVal,nPlanQtyVal);

            String strPriceAmt = mgr.getASPField("ITEM0_PRICE_AMOUNT").formatNumber(nValPriceAmt);
            txt = (mgr.isEmpty(strPriceAmt) ? "" : (strPriceAmt))+ "^" ;

            mgr.responseWrite(txt);
        }
        else if ("JOB_CATEGORY".equals(val))
        {
            trans.clear();

            cmd = trans.addCustomFunction("GETJCATDBVAL","JOB_CATEGORY_API.Encode","JOB_CATEGORY_DB");
            cmd.addParameter("JOB_CATEGORY");

            cmd = trans.addCustomFunction("GETCATDESC","STD_JOB_CATEGORY_API.Get_Description","ITEM1_DESCRIPTION");
            cmd.addReference("JOB_CATEGORY_DB","GETJCATDBVAL/DATA");
            cmd.addParameter("IDENTITY");

            trans = mgr.validate(trans);

            String sJobCatDb = trans.getValue("GETJCATDBVAL/DATA/JOB_CATEGORY_DB");
            String sJobCatDesc = trans.getValue("GETCATDESC/DATA/ITEM1_DESCRIPTION");

            txt = (mgr.isEmpty(sJobCatDb)?"":sJobCatDb) + "^" +
                  (mgr.isEmpty(sJobCatDesc)?"":sJobCatDesc) + "^";
            mgr.responseWrite(txt);
        }
        if ("HEAD_ORG_CODE".equals(val)) {

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

        else if (("MAT_BUDGET_COST".equals(val)) || ("EXT_BUDGET_COST".equals(val)))
        {
            double nMatCost = mgr.isEmpty(mgr.readValue("MAT_BUDGET_COST")) ? 0 : mgr.readNumberValue("MAT_BUDGET_COST");
            double nExtCost = mgr.isEmpty(mgr.readValue("EXT_BUDGET_COST")) ? 0 : mgr.readNumberValue("EXT_BUDGET_COST");

            double nTotCost =toDouble(nMatCost)+toDouble(nExtCost);

            txt=(isNaN(nTotCost)? "" : mgr.formatNumber("TOT_BUDGET_COST",nTotCost)  )+ "^";

            mgr.responseWrite(txt);
        }


        mgr.endResponse();
    }

    public double validatePriceAmt(String salesPartNo,double nSalesPrice,double nSalesPartListPrice,String invoiceType,double nDiscount,double nQtyToInvoice,double nPlannedQty)
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
        testbuff = mgr.perform(testbuff);
        ASPBuffer sec = testbuff.getSecurityInfo();
        double nPriceAmt = 0;

        if (sec.itemExists("SALES_PART"))
        {
            trans.clear();

            if (!mgr.isEmpty(salesPartNo))
            {
                //Get the right price 
                //We take the List Price only if the Sales Price is null.
                //If Sales Price is zero we take zero as price. 

                if (isNaN(nSalesPrice))
                    nSalesPrice = nSalesPartListPrice;

                //Calculate on right price

                if (sInvoiceTypeFixedLine.equals(invoiceType))
                    nPriceAmt = nSalesPrice * nQtyToInvoice;
                else if (sInvoiceTypeAsReported.equals(invoiceType))
                    nPriceAmt = nSalesPrice * nPlannedQty;
                else if (sInvoiceTypeMinQty.equals(invoiceType))
                {
                    if (nPlannedQty > nQtyToInvoice)
                        nPriceAmt = nSalesPrice * nPlannedQty;
                    else
                        nPriceAmt = nSalesPrice * nQtyToInvoice;
                }
                else if (sInvoiceTypeMaxQty.equals(invoiceType))
                {
                    if (nPlannedQty < nQtyToInvoice)
                        nPriceAmt = nSalesPrice * nPlannedQty;
                    else
                        nPriceAmt = nSalesPrice * nQtyToInvoice;
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

        cmd = trans.addEmptyCommand("HEAD","ROUND_STANDARD_JOB_API.New__",headblk);
        cmd.setOption("ACTION","PREPARE");

        cmd = trans.addCustomFunction( "FNDUSER", "Fnd_Session_API.Get_Fnd_User", "USER_ID" );

        cmd = trans.addCustomFunction( "USER", "Person_Info_API.Get_Id_For_User", "CREATED_BY" );
        cmd.addReference("USER_ID","FNDUSER/DATA");

        cmd=trans.addCustomFunction("USERID","Company_Emp_API.Get_Max_Employee_Id","CREATED_BY_ID");
        cmd.addReference("COMPANY","HEAD/DATA");
        cmd.addReference("CREATED_BY","USER/DATA");

        cmd = trans.addCustomFunction( "CREDATE", "Maintenance_Site_Utility_API.Get_Site_Date(NULL)", "CREATION_DATE" );

        trans = mgr.perform(trans);

        created_by = trans.getValue("USER/DATA/CREATED_BY"); 
        created_by_id = trans.getValue("USERID/DATA/CREATED_BY_ID"); 
        created_date = trans.getValue("CREDATE/DATA/CREATION_DATE"); 

        created_date = mgr.formatDate("CREATION_DATE",mgr.parseDate("CREATION_DATE",created_date));

        data = trans.getBuffer("HEAD/DATA");
        data.setFieldItem("CREATED_BY",created_by);
        data.setFieldItem("CREATED_BY_ID",created_by_id);
        headset.addRow(data);
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
   
    public void saveReturnITEM0()
    {
        ASPManager mgr = getASPManager();
        trans.clear();

        int currHead = headset.getCurrentRowNo();
        itemset0.changeRow();
        mgr.submit(trans);
        itemlay0.setLayoutMode(itemlay0.MULTIROW_LAYOUT);
        headset.goTo(currHead);
        headset.refreshRow();
        okFindALLITEM0();
    } 

    public void saveNew()
    {
        currrow = headset.getCurrentRowNo();
        trans.clear();

        if (saveReturn())
            newRow();
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

//-----------------------------------------------------------------------------
//----------------------  CMDBAR EDIT-IT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void newRowITEM0()
    {
        ASPManager mgr = getASPManager();
        trans.clear();

        cmd = trans.addEmptyCommand("ITEM0","STANDARD_JOB_SPARE_API.New__",itemblk0);
        cmd.setParameter("ITEM0_STD_JOB_ID",mgr.readValue("STD_JOB_ID",""));
        cmd.setParameter("ITEM0_STD_JOB_REVISION",mgr.readValue("STD_JOB_REVISION",""));
        cmd.setParameter("ITEM0_STD_JOB_CONTRACT",mgr.readValue("CONTRACT",""));
        cmd.setOption("ACTION","PREPARE");

        trans = mgr.perform(trans);

        data = trans.getBuffer("ITEM0/DATA");
        data.setFieldItem("ITEM0_STD_JOB_ID",headset.getRow().getValue("STD_JOB_ID"));
        data.setFieldItem("ITEM0_STD_JOB_REVISION",headset.getRow().getValue("STD_JOB_REVISION"));
        data.setFieldItem("ITEM0_STD_JOB_CONTRACT",headset.getRow().getValue("CONTRACT"));
        data.setFieldItem("ITEM0_CONTRACT",headset.getRow().getValue("CONTRACT"));
        itemset0.addRow(data);
    }

    public void newRowITEM1()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addEmptyCommand("ITEM1","STD_JOB_CATEGORY_API.NEW__",itemblk1);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);

        data = trans.getBuffer("ITEM1/DATA");
        data.setFieldItem("ITEM1_STD_JOB_ID", headset.getValue("STD_JOB_ID"));
        data.setFieldItem("ITEM1_STD_JOB_REVISION", headset.getValue("STD_JOB_REVISION"));
        data.setFieldItem("ITEM1_STD_JOB_CONTRACT", headset.getValue("CONTRACT"));

        itemset1.addRow(data);
    }

    public void newRowITEM2()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addEmptyCommand("ITEM2","STANDARD_JOB_PERMIT_API.NEW__",itemblk2);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);

        data = trans.getBuffer("ITEM2/DATA");
        data.setFieldItem("ITEM2_STD_JOB_ID", headset.getValue("STD_JOB_ID"));
        data.setFieldItem("ITEM2_STD_JOB_REVISION", headset.getValue("STD_JOB_REVISION"));
        data.setFieldItem("ITEM2_STD_JOB_CONTRACT", headset.getValue("CONTRACT"));

        itemset2.addRow(data);
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void okFind()
    {
        ASPManager mgr = getASPManager();
        trans.clear();

        if (itemset0.countRows() > 0)
            itemset0.clear();

        q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CONTRACT) FROM dual)");
        q.includeMeta("ALL");

        if (mgr.dataTransfered())
            q.addOrCondition(mgr.getTransferedData());
        
        mgr.querySubmit(trans, headblk);

        if (headset.countRows() == 1)
        {
            okFindITEM0();
            okFindITEM1();
            okFindITEM6();
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        }
        else if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWROUNDSTANDARDJOBNODATA: No data found."));
            headset.clear();
        }

        qrystr = mgr.createSearchURL(headblk);
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
	q.addParameter("ITEM0_STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("ITEM0_STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM0_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));

        q.includeMeta("ALL");

        mgr.submit(trans);

        if (mgr.commandBarActivated())
        {
            if (itemset0.countRows() == 0 && "ITEM0.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWROUNDSTANDARDJOBNODATA: No data found."));
                itemset0.clear();
            }
        }

        if (itemset0.countRows() == 0)
            itemset0.clear();
        else
            getPriceAmount();

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
	q.addParameter("ITEM0_STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("ITEM0_STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM0_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));

        q.includeMeta("ALL");

        mgr.submit(trans);

        if (mgr.commandBarActivated())
        {
            if (itemset0.countRows() == 0 && "ITEM0.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWROUNDSTANDARDJOBNODATA: No data found."));
                itemset0.clear();
            }
        }

        if (itemset0.countRows() == 0)
            itemset0.clear();
        else
            getPriceAmount();

        headset.goTo(headrowno);
    }

    public void okFindITEM1()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk1);
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("ITEM1_STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("ITEM1_STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM1_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));

        q.includeMeta("ALL");

        mgr.submit(trans);

        if (itemset1.countRows() == 0)
            itemset1.clear();

        headset.goTo(headrowno);
    }

    public void okFindITEM2()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk2);
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("ITEM2_STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("ITEM2_STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM2_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));

        q.includeMeta("ALL");

        mgr.submit(trans);

        if (itemset2.countRows() == 0)
            itemset2.clear();

        headset.goTo(headrowno);
    }

    public void okFindITEM3()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk3);
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("ITEM3_STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("ITEM3_STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM3_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));

        q.includeMeta("ALL");

        mgr.submit(trans);

        if (itemset3.countRows() == 0)
            itemset3.clear();

        headset.goTo(headrowno);
    }

    public void countFindITEM0()
    {
        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();
        q = trans.addQuery(itemblk0);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("STD_JOB_ID = ?");
	q.addParameter("ITEM0_STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("ITEM0_STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
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
	q.addParameter("ITEM1_STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("ITEM1_STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
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
	q.addParameter("ITEM2_STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("ITEM2_STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
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
	q.addParameter("ITEM3_STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
        q.addWhereCondition("STD_JOB_REVISION = ?");
	q.addParameter("ITEM3_STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
        q.addWhereCondition("STD_JOB_CONTRACT = ?");
	q.addParameter("ITEM3_STD_JOB_CONTRACT", headset.getRow().getValue("CONTRACT"));        
	mgr.submit(trans);

        itemlay3.setCountValue(toInt(itemset3.getRow().getValue("N")));
        itemset3.clear();

        headset.goTo(currrow);   
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

    public void saveReturnITEM6()
    {
            ASPManager mgr = getASPManager();

            trans.clear();
//          Bug 78556, Start
            int currHead = headset.getCurrentRowNo();
//          Bug 78556, End
            double matCost = mgr.readNumberValue("MAT_BUDGET_COST");
            if (isNaN(matCost))
                    matCost = 0;

            double extCost = mgr.readNumberValue("EXT_BUDGET_COST");
            if (isNaN(extCost))
                    extCost = 0;

            ASPBuffer buff = itemset6.getRow();

            buff.setNumberValue("MAT_BUDGET_COST", matCost);
            buff.setNumberValue("EXT_BUDGET_COST", extCost);
            buff.setNumberValue("TOT_BUDGET_COST", matCost+extCost);

            itemset6.setRow(buff);
            mgr.submit(trans);
//          Bug 78556, Start
            headset.goTo(currHead);
            headset.refreshRow();
//          Bug 78556, End
            

            trans.clear();
            getBudgetValues();
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


        trans=mgr.perform(trans);

        String planMatCost    = trans.getValue("GETPLAN1/DATA/PLAN_MAT_COST");
        String planExtCost    = trans.getValue("GETPLAN2/DATA/PLAN_EXT_COST");
        planMatRev            = trans.getValue("GETPLAN4/DATA/PLAN_MAT_REV");
        planExtRev            = trans.getValue("GETPLAN5/DATA/PLAN_EXT_REV");
        row = itemset6.getRow();

        double totPersCost = toDouble(planMatCost) + toDouble(planExtCost) ;
        double totPersRev  = toDouble(planMatRev) + toDouble(planExtRev) ;

        row.setValue("PLAN_MAT_COST", planMatCost);
        row.setValue("PLAN_EXT_COST", planExtCost);

        row.setValue("PLAN_MAT_REV", planMatRev);
        row.setValue("PLAN_EXT_REV", planExtRev);

        row.setNumberValue("TOT_PLAN_COST", totPersCost);
        row.setNumberValue("TOT_PLAN_REV", totPersRev);
        itemset6.setRow(row);
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
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
    //

    public void none()
    {
        ASPManager mgr = getASPManager();

        mgr.showAlert(mgr.translate("PCMWROUNDSTANDARDJOBNONE: No RMB method has been selected."));
    }

//---------------------------------RMB's IN MATERIALS TAB-------------------------------------------------------------------------

    public void cuQuInHand()
    {
        ASPManager mgr = getASPManager();
        //Web Alignment - Multirow Action
        int count = 0;
        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;

        bOpenNewWindow = true;

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

        transferBuffer = mgr.newASPBuffer();

        for (int i = 0; i < count; i++)
        {
            rowBuff = mgr.newASPBuffer();

            if (i == 0)
            {
                rowBuff.addItem("PART_NO", itemset0.getValue("PART_NO"));
                rowBuff.addItem("CONTRACT", itemset0.getValue("SPARE_CONTRACT"));
            }
            else
            {
                rowBuff.addItem(null, itemset0.getValue("PART_NO"));
                rowBuff.addItem(null, itemset0.getValue("SPARE_CONTRACT"));
            }

            transferBuffer.addBuffer("DATA", rowBuff);

            if (itemlay0.isMultirowLayout())
                itemset0.next();
        }

        if (itemlay0.isMultirowLayout())
            itemset0.setFilterOff();

        urlString = createTransferUrl("../invenw/InventoryPartInventoryPartCurrentOnHand.page", transferBuffer);
        newWinHandle = "CurrQtyInHand"; 
        //
    }

    public void avaiDetails()
    {
        ASPManager mgr = getASPManager();

        //Web Alignment - Multirow Action
        int count = 0;
        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;

        bOpenNewWindow = true;

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

        transferBuffer = mgr.newASPBuffer();

        for (int i = 0; i < count; i++)
        {
            rowBuff = mgr.newASPBuffer();

            if (i == 0)
            {
                rowBuff.addItem("PART_NO", itemset0.getValue("PART_NO"));
                rowBuff.addItem("CONTRACT", itemset0.getValue("SPARE_CONTRACT"));
                rowBuff.addItem("PROJECT_ID", "*");
                rowBuff.addItem("CONFIGURATION_ID", "*");
            }
            else
            {
                rowBuff.addItem(null, itemset0.getValue("PART_NO"));
                rowBuff.addItem(null, itemset0.getValue("SPARE_CONTRACT"));
                rowBuff.addItem(null, "*");
                rowBuff.addItem(null, "*");
            }

            transferBuffer.addBuffer("DATA", rowBuff);

            if (itemlay0.isMultirowLayout())
                itemset0.next();
        }

        if (itemlay0.isMultirowLayout())
            itemset0.setFilterOff();

        urlString = createTransferUrl("../invenw/InventoryPartAvailabilityPlanningQry.page", transferBuffer);
        newWinHandle = "availDetail"; 
        //
    }

    public void invenPartLimited()
    {
        ASPManager mgr = getASPManager();
        //Web Alignment - Multirow Action
        int count = 0;
        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;

        bOpenNewWindow = true;

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

        transferBuffer = mgr.newASPBuffer();

        for (int i = 0; i < count; i++)
        {
            rowBuff = mgr.newASPBuffer();

            if (i == 0)
            {
                rowBuff.addItem("PART_NO", itemset0.getValue("PART_NO"));
                rowBuff.addItem("CONTRACT", itemset0.getValue("SPARE_CONTRACT"));
            }
            else
            {
                rowBuff.addItem(null, itemset0.getValue("PART_NO"));
                rowBuff.addItem(null, itemset0.getValue("SPARE_CONTRACT"));
            }

            transferBuffer.addBuffer("DATA", rowBuff);

            if (itemlay0.isMultirowLayout())
                itemset0.next();
        }

        if (itemlay0.isMultirowLayout())
            itemset0.setFilterOff();

        urlString = createTransferUrl("../invenw/InventoryPart.page", transferBuffer);
        newWinHandle = "InvPartLtd"; 
    }

    public void suppPerPart()
    {
        ASPManager mgr = getASPManager();
        //Web Alignment - Multirow Action
        int count = 0;
        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;

        bOpenNewWindow = true;

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

        transferBuffer = mgr.newASPBuffer();

        for (int i = 0; i < count; i++)
        {
            rowBuff = mgr.newASPBuffer();

            if (i == 0)
            {
                rowBuff.addItem("PART_NO", itemset0.getValue("PART_NO"));
                rowBuff.addItem("CONTRACT", itemset0.getValue("SPARE_CONTRACT"));
            }
            else
            {
                rowBuff.addItem(null, itemset0.getValue("PART_NO"));
                rowBuff.addItem(null, itemset0.getValue("SPARE_CONTRACT"));
            }

            transferBuffer.addBuffer("DATA", rowBuff);

            if (itemlay0.isMultirowLayout())
                itemset0.next();
        }

        if (itemlay0.isMultirowLayout())
            itemset0.setFilterOff();

        urlString = createTransferUrl("../purchw/PurchasePartSupplier.page", transferBuffer);
        newWinHandle = "supPerPart"; 
    }

//-----------------------------------------------------------------------------
//-------------------------  CUSTOM FUNCTION  ---------------------------------
//-----------------------------------------------------------------------------

    /*public void setCheckBoxValues()
    {
        ASPManager mgr = getASPManager();

        String myVal = null;
        String myFinalVal = null;
        String value = null;
        String std_job_id = null;
        String cbmaterials = null; 
        String cbdocuments = null; 
        boolean docInstal = false;

        ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer();
        secBuff.addSecurityQuery("DOC_REFERENCE_OBJECT_API","EXIST_OBJ_REFERENCE");

        secBuff = mgr.perform(secBuff);

        if (secBuff.getSecurityInfo().itemExists("DOC_REFERENCE_OBJECT_API.EXIST_OBJ_REFERENCE"))
            docInstal = true;

        eval(headblk.generateAssignments("CONTRACT, STD_JOB_ID, STD_JOB_REVISION, STD_TEXT_ONLY",headset.getRow()));

        tempblk = getASPBlock("TEMP");

        n = headset.countRows();
        headset.first();

        for (i=1; i<=n; i++)
        {
            trans.clear();

            cmd = trans.addCustomFunction("CBMTRLS"+i,"ROUND_STANDARD_JOB_API.HAS_MATERIAL","CBMATERIALS");
            cmd.addParameter("STD_JOB_ID",headset.getRow().getValue("STD_JOB_ID"));
            cmd.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT"));
            cmd.addParameter("STD_JOB_REVISION",headset.getRow().getValue("STD_JOB_REVISION"));

            myVal = headset.getRow().getValue("STD_JOB_ID");
            myFinalVal = myVal.concat("^");
            value = "STD_JOB_ID=";
            std_job_id = value.concat(myFinalVal);

            String myValContract = headset.getRow().getValue("CONTRACT");
            String myFinalValContract = myValContract.concat("^");
            String valueContract = "CONTRACT=";
            String sContract = valueContract.concat(myFinalValContract);

            String conStd = sContract.concat(std_job_id);

            String myValRev = headset.getRow().getValue("STD_JOB_REVISION");
            String myFinalValRev = myValRev.concat("^");
            String valueRev = "STD_JOB_REVISION=";
            String sRev = valueRev.concat(myFinalValRev);

            String finalKeyRef = conStd.concat(sRev);

            if (docInstal)
            {
                cmd = trans.addCustomFunction("CBDOCU"+i,"DOC_REFERENCE_OBJECT_API.EXIST_OBJ_REFERENCE","CBDOCUMENTS");
                cmd.addParameter("LU_NAME","StandardJob");
                cmd.addParameter("KEY_REF",finalKeyRef);
            }

            trans = mgr.perform(trans);

            cbmaterials = trans.getValue("CBMTRLS"+i+"/DATA/CBMATERIALS");
            if (docInstal)
                cbdocuments = trans.getValue("CBDOCU"+i+"/DATA/CBDOCUMENTS");

            if ("TRUE".equals(cbmaterials))
            {
                r = headset.getRow();
                r.setValue("CBMATERIALS","TRUE" );
                headset.setRow(r);
            }

            if ("TRUE".equals(cbdocuments))
            {
                r = headset.getRow();
                r.setValue("CBDOCUMENTS","TRUE" );
                headset.setRow(r);
            }
            headset.next();
        }
    }*/


    public void getPriceAmount()
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
        testbuff = mgr.perform(testbuff);
        ASPBuffer sec = testbuff.getSecurityInfo();

        if (sec.itemExists("SALES_PART"))
        {
            trans.clear();
            int n = itemset0.countRows();

            itemset0.first();
            for (int i=1;i<=n;i++)
            {
                double nPriceAmt = 0;
                String salesPartNo = itemset0.getRow().getValue("CATALOG_NO");
                if (!mgr.isEmpty(salesPartNo))
                {
                    //Get the right price
                    double nSalesPrice = itemset0.getRow().getNumberValue("SALES_PRICE");
                    if (isNaN(nSalesPrice))
                        nSalesPrice = 0;

                    if (nSalesPrice == 0)
                    {
                        double nSalesPartListPrice = itemset0.getRow().getNumberValue("NSALESPARTLIST_PRICE");
                        if (isNaN(nSalesPartListPrice))
                            nSalesPartListPrice = 0;

                        nSalesPrice = nSalesPartListPrice;
                    }

                    //Calculate on right price
                    String invoiceType = itemset0.getRow().getValue("WORK_ORDER_INVOICE_TYPE");

                    double nDiscount = itemset0.getRow().getNumberValue("DISCOUNT");
                    if (isNaN(nDiscount))
                        nDiscount = 0;

                    double nQtyToInvoice = itemset0.getRow().getNumberValue("QTY_TO_INVOICE");
                    if (isNaN(nQtyToInvoice))
                        nQtyToInvoice = 0;

                    double nPlannedQty = itemset0.getRow().getNumberValue("PLANNED_QTY");
                    if (isNaN(nPlannedQty))
                        nPlannedQty = 0;
                    
                    if(!mgr.isEmpty(invoiceType))
                    {
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
                    }

                    if (nDiscount != 0)
                        nPriceAmt = nPriceAmt - ( nDiscount / 100 * nPriceAmt );

                    r = itemset0.getRow();
                    r.setNumberValue("ITEM0_PRICE_AMOUNT",nPriceAmt);
                    itemset0.setRow(r);
                }
                itemset0.next();
            }
        }
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
                    "&STD_JOB_REVISION="+ mgr.URLEncode(headset.getRow().getValue("STD_JOB_REVISION")) +
                    "&CONTRACT="+ mgr.URLEncode(headset.getRow().getValue("CONTRACT")) +
                    "&COMPANY="+mgr.URLEncode(headset.getRow().getValue("COMPANY"))+
                    "&CREATED_BY="+mgr.URLEncode(headset.getRow().getValue("CREATED_BY"));
        newWinHandle = "CopyStdJobId";
        newWinHeight = 460;
        newWinWidth = 400;
        //

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
        try
        {
            mgr.showAlert(mgr.translate("PCMWSTDJOBREPLACEMENT: The replacement of the standard job revisions will be performed on the same revision of the PM Actions."));

            cmd = trans.addCustomCommand("UPDPM","Standard_Job_API.Replace_Revsions_");
            cmd.addParameter("STD_JOB_ID",headset.getRow().getValue("STD_JOB_ID"));
            cmd.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT"));
            cmd.addParameter("STD_JOB_REVISION","");
            trans = mgr.validate(trans);

            headbar.disableCustomCommand("showObsoletePM");
            headbar.disableCustomCommand("updatePM");
        }
        catch (Throwable any)
        {
            mgr.showError(mgr.translate("PCMWSTDJOBREPLACEMENTERROR: The update of PM actions was not performed."));
        }

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

        if (itemlay2.isMultirowLayout())
            itemset2.store();
        else
        {
            itemset2.unselectRows();
            itemset2.selectRow();
        }

        bOpenNewWindow = true;
        urlString = createTransferUrl("PermitTypeRMB.page", itemset2.getSelectedRows("PERMIT_TYPE_ID"));
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
        trans.clear();

        q = trans.addQuery("HASMOREACTIVEREVS","SELECT DISTINCT(1) NCOUNT FROM ROUND_STANDARD_JOB WHERE STD_JOB_ID = ? AND CONTRACT = ? AND OBJSTATE = '"+sState+"' AND STD_JOB_REVISION != ?");
        q.addParameter("STD_JOB_ID", headset.getRow().getValue("STD_JOB_ID"));
	q.addParameter("CONTRACT", headset.getRow().getValue("CONTRACT"));
	q.addParameter("STD_JOB_REVISION", headset.getRow().getValue("STD_JOB_REVISION"));
	trans = mgr.perform(trans);
        if (trans.getNumberValue("HASMOREACTIVEREVS/DATA/NCOUNT")>0)
            mgr.showAlert(mgr.translate("PCMWRNDSTDJOBACTTOOBONE: Previous 'Active' Revision for Standard Job(s) was / were set to 'Obsolete'"));

        perform("ACTIVE__");

    }

    public void obsoletePm()
    {
        ASPManager mgr = getASPManager();

        perform("OBSOLETE__");
    }

    public void preDefine()
    {
        ASPManager mgr = getASPManager();

        tempblk = mgr.newASPBlock("TEMP");
        f = tempblk.addField("LU_NAME");
        f = tempblk.addField("KEY_REF");

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
        f.setSize(14);
        f.setMaxLength(12);
        f.setMandatory();
        f.setReadOnly();
        f.setLabel("PCMWROUNDSTANDARDJOBSJID: Standard Job ID");
        f.setHilite();
        f.setInsertable();
        f.setUpperCase(); 

        f = headblk.addField("DEFINITION");
        f.setSize(50);
        f.setMaxLength(40);
        f.setMandatory();
        f.setLabel("PCMWROUNDSTANDARDJOBDEFDESC: Definition");
        f.setHilite();

        f = headblk.addField("STD_JOB_REVISION");
        f.setSize(15);
        f.setMaxLength(6);
        f.setMandatory();
        f.setReadOnly();
        f.setLabel("PCMWROUNDSTANDARDJOBSJREV: Standard Job Revision");
        f.setHilite();
        f.setInsertable();
        f.setUpperCase(); 

        f = headblk.addField("CONTRACT");
        f.setSize(7);
        f.setInsertable();
        f.setReadOnly();
        f.setMaxLength(5);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450); 
        f.setLabel("PCMWROUNDSTANDARDJOBCON: Site");
        f.setCustomValidation("CONTRACT,HEAD_ORG_CODE","COMPANY,CALENDAR_ID,CALENDAR_DESCRIPTION");
        f.setHilite();
        f.setUpperCase();

        f = headblk.addField("STATE");
        f.setSize(12);
        f.setLabel("PCMWROUNDSTANDARDJOBSTATE: Status");
        f.setLOV("StdJobStatus.page");
        f.setHilite();
        f.setReadOnly(); 

        f = headblk.addField("TYPE_ID");
        f.setSize(20);
        f.setMaxLength(20);
        f.setDynamicLOV("STD_JOB_TYPE",600,450);
        f.setLabel("PCMWROUNDSTDJOB2STDJOBTYPE: Std Job Type");
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
        f.setLabel("PCMWROUNDSTDJOB2ACTCODID: Action");
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
        f.setSize(23);
        f.setLabel("PCMWROUNDSTANDARDJOBSTEXTONLY: Standard Text");
        f.setCheckBox("FALSE,TRUE");

        f = headblk.addField("WORK_DESCRIPTION");
        f.setSize(50);
        f.setHeight(5);
        f.setLabel("PCMWROUNDSTANDARDJOBWODESCR: Work Description");
        f.setMaxLength(2000);       

        // Group Planning Info

        f = headblk.addField("HEAD_ORG_CODE");
        f.setDbName("ORG_CODE");
        f.setSize(8);
        f.setMaxLength(8);
        f.setUpperCase();
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,450);
        f.setLabel("PCMWROUNDSTDJOB2ORGCODE: Maintenance Organization");
        f.setCustomValidation("CONTRACT,HEAD_ORG_CODE","CALENDAR_ID,CALENDAR_DESCRIPTION");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("SIGNATURE");
        f.setSize(11);
        f.setMaxLength(11);
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,450);
        f.setLabel("PCMWROUNDSTDJOB2SIGNATURE: Planned By");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("WORK_TYPE_ID");
        f.setSize(20);
        f.setMaxLength(20);
        f.setUpperCase();
        f.setDynamicLOV("WORK_TYPE",600,450);
        f.setLabel("PCMWROUNDSTDJOB2WORKTYPEID: Work Type");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("CREATED_BY");
        f.setSize(20);
        f.setMaxLength(20);
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445); 
        f.setLOVProperty("TITLE",mgr.translate("PCMWROUNDSTDJOBLOVCREBY: List of Created by"));
        f.setMandatory();
        f.setCustomValidation("COMPANY,CREATED_BY","CREATED_BY_ID");
        f.setLabel("PCMWROUNDSTANDARDJOBCRBY: Created by");
        f.setUpperCase();
        f.setInsertable();
        f.setReadOnly();

        f = headblk.addField("CREATED_BY_ID");
        f.setHidden();

        f = headblk.addField("OP_STATUS_ID");
        f.setSize(3);
        f.setMaxLength(3);
        f.setUpperCase();
        f.setDynamicLOV("OPERATIONAL_STATUS",600,450);
        f.setLabel("PCMWROUNDSTDJOB2OPSTATUSID: Operational Status");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("CREATION_DATE","Date");
        f.setSize(20);
        f.setMandatory();
        f.setLabel("PCMWROUNDSTANDARDJOBCRDATE: Creation Date");
        f.setReadOnly();

        f = headblk.addField("PRIORITY_ID");
        f.setSize(1);
        f.setMaxLength(1);
        f.setUpperCase();
        f.setDynamicLOV("MAINTENANCE_PRIORITY",600,450);
        f.setLabel("PCMWROUNDSTDJOBPRIORITY: Priority");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("CHANGED_BY");
        f.setSize(20);
        f.setMaxLength(20);
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445); 
        f.setLOVProperty("TITLE",mgr.translate("PCMWROUNDSTDJOBLOVCHGBY: List of Changed by"));
        f.setCustomValidation("COMPANY,CHANGED_BY","CHANGED_BY_ID");
        f.setLabel("PCMWROUNDSTANDARDJOBCHBY: Changed by");
        f.setUpperCase();

        f = headblk.addField("CHANGED_BY_ID");
        f.setHidden(); 

        f = headblk.addField("EXECUTION_TIME","Number");
        f.setLabel("PCMWROUNDSTDJOBEXECUTIONTIME: Execution Time");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("LAST_CHANGED","Date");
        f.setSize(20);
        f.setLabel("PCMWROUNDSTANDARDJOBLACHANGED: Last Changed");
        f.setReadOnly();

        // Group Copied From

        f = headblk.addField("COPIED_SITE");
        f.setSize(5);
        f.setMaxLength(5);
        f.setLabel("PCMWROUNDSTDJOB2COPIEDSITE: Site");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("COPIED_STD_JOB_ID");
        f.setSize(12);
        f.setMaxLength(12);
        f.setLabel("PCMWROUNDSTDJOB2COPIEDSTDJOBID: Std Job ID");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("COPIED_REVISION");
        f.setSize(6);
        f.setMaxLength(6);
        f.setLabel("PCMWROUNDSTDJOB2COPIEDREVISION: Revision");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("REPLACES_REVISION");
        f.setSize(6);
        f.setMaxLength(6);
        f.setLabel("PCMWROUNDSTDJOB2REPLACESREVISION: Replaces Revision");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("REPLACED_REVISION");
        f.setSize(6);
        f.setMaxLength(6);
        f.setLabel("PCMWROUNDSTDJOB2REPLACEDREVISION: Replaced By Revision");
        f.setReadOnly();
        f.setDefaultNotVisible();

        // Group Maintenance Plan

        f = headblk.addField("START_VALUE");
        f.setSize(11);
        f.setMaxLength(11);
        f.setLabel("PCMWROUNDSTDJOB2STARTVALUE: Start Value");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("PM_START_UNIT");
        f.setMaxLength(200);
        f.setSelectBox();
        f.enumerateValues("PM_START_UNIT_API");
        f.setLabel("PCMWROUNDSTDJOB2PMSTARTUNIT: Start Unit");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("INTERVAL","Number");
        f.setSize(3);
        f.setMaxLength(3);
        f.setLabel("PCMWROUNDSTDJOB2INTERVAL: Interval");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("PM_INTERVAL_UNIT");
        f.setMaxLength(200);
        f.setSelectBox();
        f.enumerateValues("PM_INTERVAL_UNIT_API");
        f.setLabel("PCMWROUNDSTDJOB2PMINTERVALUNIT: Interval Unit");
        f.setInsertable();
        f.setDefaultNotVisible();

        // Group Event

        f = headblk.addField("CALL_CODE");
        f.setSize(10);
        f.setMaxLength(10);
        f.setUpperCase();
        f.setDynamicLOV("MAINTENANCE_EVENT",600,450);
        f.setLabel("PCMWROUNDSTDJOB2CALLCODE: Event");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("START_CALL","Number");
        f.setLabel("PCMWROUNDSTDJOB2STARTCALL: Start");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("CALL_INTERVAL","Number");
        f.setLabel("PCMWROUNDSTDJOB2CALLINTERVAL: Interval");
        f.setInsertable();
        f.setDefaultNotVisible();

        // Group Valid

        f = headblk.addField("VALID_FROM","Date");
        f.setSize(20);
        f.setLabel("PCMWROUNDSTDJOB2VALIDFROM: Valid From");
        f.setDefaultNotVisible();

        f = headblk.addField("VALID_TO","Date");
        f.setSize(20);
        f.setLabel("PCMWROUNDSTDJOB2VALIDTO: Valid To");
        f.setDefaultNotVisible();

        f = headblk.addField("CALENDAR_ID");
        f.setSize(10);
        f.setMaxLength(10);
        f.setUpperCase();
        f.setDynamicLOV("WORK_TIME_CALENDAR",600,450);
        f.setLabel("PCMWROUNDSTDJOB2WORKTIMECALENDAR: Calendar");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = headblk.addField("CALENDAR_DESCRIPTION");
        f.setFunction("Work_Time_Calendar_API.Get_Description(:CALENDAR_ID)");
        f.setSize(30);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLOVProperty("WHERE","OBJSTATE='Generated'");
        mgr.getASPField("CALENDAR_ID").setValidation("CALENDAR_DESCRIPTION");

        f = headblk.addField("STANDARD_JOB_TYPE");
        f.setHidden();

        f = headblk.addField("STANDARD_JOB_TYPE_DB");
        f.setHidden();

        // Group Std Job Has

        f = headblk.addField("CBMATERIALS");
        f.setSize(10);
        f.setLabel("PCMWROUNDSTDJOBCBMATERIALS: Materials");
        f.setCheckBox("FALSE,TRUE");
        f.setFunction("ROUND_STANDARD_JOB_API.HAS_MATERIAL(:STD_JOB_ID,:CONTRACT,:STD_JOB_REVISION)");
        f.setReadOnly();

        f = headblk.addField("CBDOCUMENTS");
        f.setSize(10);
        f.setLabel("PCMWROUNDSTDJOBCBDOCMTS: Documents");
        f.setCheckBox("FALSE,TRUE");
        f.setFunction("DOC_REFERENCE_OBJECT_API.EXIST_OBJ_REFERENCE('StandardJob','CONTRACT='||CONTRACT||'^STD_JOB_ID='||STD_JOB_ID||'^STD_JOB_REVISION='||STD_JOB_REVISION||'^')");
        f.setReadOnly();

        //Bug 82435, start
        f = headblk.addField("REASON");                     
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setLabel("PCMWROUNDSTDJOBREASON: Created Reason");
        f.setSize(15);

        f = headblk.addField("DONE_BY");                     
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setLabel("PCMWROUNDSTDJOBDONEBY: Created By");
        f.setSize(15);

        f = headblk.addField("REV_CRE_DATE", "Datetime");                     
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setLabel("PCMWROUNDSTDJOBREVCREDATE: Created Date");
        f.setSize(15);
        //Bug 82435, end

        f = headblk.addField("USER_ID");
        f.setSize(11);
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

        headblk.setView("ROUND_STANDARD_JOB");
        headblk.defineCommand("ROUND_STANDARD_JOB_API","New__,Modify__,Remove__,ACTIVE__,OBSOLETE__");
        headset = headblk.getASPRowSet();

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWROUNDSTANDARDJOBHD: Route Standard Job"));
        headtbl.setWrap();

        headbar = mgr.newASPCommandBar(headblk);

        headbar.addCustomCommand("createNewRevision",mgr.translate("PCMWROUNDSTANDARDJOBNEWREV: Create New Revision..."));
        headbar.addCustomCommand("copyStdJobId",mgr.translate("PCMWROUNDSTANDARDJOBUPPMACTSTDJOBID: Copy Standard Job..."));
        headbar.addCustomCommand("generatePMRevision",mgr.translate("PCMWROUNDSTANDARDJOBGENERATEPMREV: Generate PM Action..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("showObsoleteWORev",mgr.translate("PCMWROUNDSTANDARDJOBOBSOLETEWOREV: Obsolete Revisions on Active Work Orders..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("showObsoletePM",mgr.translate("PCMWROUNDSTANDARDJOBOBSPMREV: Obsolete Revisions on PM Actions..."));
        headbar.addCustomCommand("updatePM",mgr.translate("PCMWROUNDSTANDARDJOBUPDATEPMREV: Replace Obsolete Revisions on PM Actions"));
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

        headbar.defineCommand(headbar.SAVERETURN,"saveReturn","checkHeadFields(-1)");
        headbar.defineCommand(headbar.SAVENEW,"saveNew","checkHeadFields(-1)");

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        headlay.defineGroup("","STD_JOB_ID,DEFINITION,STD_JOB_REVISION,CONTRACT,STATE,TYPE_ID,TYPE_DESCRIPTION,ACTION_CODE_ID,ACTION_CODE_DESCRIPTION,STD_TEXT_ONLY,WORK_DESCRIPTION",false,true);   
        headlay.defineGroup(mgr.translate("PCMWRNDSTDJOB2GRPLABELPLANINFO: Planning Info"),"HEAD_ORG_CODE,SIGNATURE,WORK_TYPE_ID,CREATED_BY,OP_STATUS_ID,CREATION_DATE,PRIORITY_ID,CHANGED_BY,EXECUTION_TIME,LAST_CHANGED",true,true);
        headlay.defineGroup(mgr.translate("PCMWRNDSTDJOB2GRPLABELCOPDFROM: Copied From"),"COPIED_SITE,COPIED_STD_JOB_ID,COPIED_REVISION,REPLACES_REVISION,REPLACED_REVISION",true,true);
        headlay.defineGroup(mgr.translate("PCMWRNDSTDJOB2GRPLABELMAINTPLAN: Maintenance Plan"),"START_VALUE,PM_START_UNIT,INTERVAL,PM_INTERVAL_UNIT",true,true);
        headlay.defineGroup(mgr.translate("PCMWRNDSTDJOB2GRPLABELEVENT: Event"),"CALL_CODE,START_CALL,CALL_INTERVAL",true,true);
        headlay.defineGroup(mgr.translate("PCMWRNDSTDJOB2GRPLABELVALID: Valid"),"VALID_FROM,VALID_TO,CALENDAR_ID,CALENDAR_DESCRIPTION",true,true);
        headlay.defineGroup(mgr.translate("PCMWRNDSTDJOB2GRPLABELSTJOBHAS: Standard Job Has"),"CBMATERIALS,CBDOCUMENTS",true,true);
        headlay.defineGroup(mgr.translate("PCMWRNDSTDJOB2GRPLABELREVINFO: Revision Info"),"REASON,DONE_BY,REV_CRE_DATE",true,true);   

        headbar.addCustomCommandGroup("PMSTATUS", mgr.translate("PCMWSEPSTDJOBGRPSTATUS: Standard Job Status"));
        headbar.setCustomCommandGroup("activatePm", "PMSTATUS");
        headbar.setCustomCommandGroup("obsoletePm", "PMSTATUS");

        headlay.setSimple("TYPE_DESCRIPTION");
        headlay.setSimple("ACTION_CODE_DESCRIPTION");
        headlay.setSimple("CALENDAR_DESCRIPTION");

        //Web Alignment - Adding Tabs
        tabs = mgr.newASPTabContainer();
        tabs.setTabSpace(5);  
        tabs.setTabWidth(100);

        //Bug 89834, start
        tabs.addTab("PCMWROUNDSTANDARDJOBPREPTAB: Prepare","javascript:commandSet('HEAD.activatePrepareTab','')");
        tabs.addTab("PCMWROUNDSTANDARDJOBPROGTAB: Program","javascript:commandSet('HEAD.activateProgramTab','')");
        tabs.addTab("PCMWROUNDSTANDARDJOBBUDGTAB: Budget","javascript:commandSet('HEAD.activateBudgetTab','')");
        tabs.addTab("PCMWROUNDSTANDARDJOBMATTAB: Materials","javascript:commandSet('HEAD.activateMaterialTab','')");
        tabs.addTab("PCMWROUNDSTANDARDJOBPERMTAB: Permits","javascript:commandSet('HEAD.activatePermitTab','')");
        //Bug 89834, end

        headbar.addCustomCommand("activatePrepareTab","Prepare");
        headbar.addCustomCommand("activateProgramTab","Program");
        headbar.addCustomCommand("activateBudgetTab","Budget");
        headbar.addCustomCommand("activateMaterialTab","Materials");
        headbar.addCustomCommand("activatePermitTab","Permits");

        //------------------------------------------------------------------------------------------------------------------------------------------
        //---------------------------------------Block referse to the Materials tab-----------------------------------------------------------------
        //------------------------------------------------------------------------------------------------------------------------------------------

        itemblk0 = mgr.newASPBlock("ITEM0");

        f = itemblk0.addField("ITEM0_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk0.addField("ITEM0_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk0.addField("ITEM0_STD_JOB_SPARE_SEQ","Number");
        f.setSize(11);
        f.setHidden();
        f.setLabel("PCMWROUNDSTANDARDJOBSTDJSS: Standard Job Spare Sequence");
        f.setDbName("STD_JOB_SPARE_SEQ");

        f = itemblk0.addField("ITEM0_PART_NO");
        f.setSize(11);
        f.setDynamicLOV("INVENTORY_PART_WO_LOV","SPARE_CONTRACT CONTRACT",600,450); 
        f.setCustomValidation("SPARE_CONTRACT,ITEM0_PART_NO,ITEM0_CONTRACT,ITEM0_CATALOG_NO,SALES_PRICE,WORK_ORDER_INVOICE_TYPE,DISCOUNT,QTY_TO_INVOICE,ITEM0_PLANNED_QTY,ITEM0_STD_JOB_CONTRACT","ITEM0_PARTDESCRIPTION,ITEM0_DIMQUALITY,ITEM0_TYPEDESIGNATION,ITEM0_PARTUNITCODE,NSALESPARTCOST,ITEM0_CATALOG_NO,ITEM0_SALESPARTCATALOG_DESC,NSALESPARTLIST_PRICE,ITEM0_PRICE_AMOUNT,CONDITION_CODE,CONDITION_CODE_DESC,ACTIVEIND_DB");
        f.setMandatory();     
        f.setLabel("PCMWROUNDSTANDARDJOBPNO: Part No");
        f.setUpperCase();
        f.setDbName("PART_NO");
        f.setMaxLength(25);

        f = itemblk0.addField("ITEM0_PARTDESCRIPTION");
        f.setSize(50);
        f.setLabel("PCMWROUNDSTANDARDJOBPDESCR: Part Description");
        f.setFunction("Purchase_Part_API.Get_Description(:SPARE_CONTRACT,:ITEM0_PART_NO)");
        f.setReadOnly();

        f = itemblk0.addField("SPARE_CONTRACT");
        f.setSize(11);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
        f.setLabel("PCMWROUNDSTANDARDJOBSPCON: Site");
        f.setUpperCase();
        f.setMaxLength(5);

        f = itemblk0.addField("CONDITION_CODE");
        f.setSize(10);
        f.setUpperCase();
        f.setMaxLength(10);
        f.setDynamicLOV("CONDITION_CODE");
        f.setLabel("PCMWROUNDSTANDARDJOBCONDITIONCODE: Condition Code");
        f.setCustomValidation("CONDITION_CODE,SPARE_CONTRACT,ITEM0_PLANNED_QTY,ITEM0_PART_NO","CONDITION_CODE_DESC,NSALESPARTCOST");

        f = itemblk0.addField("CONDITION_CODE_DESC");
        f.setSize(20);
        f.setReadOnly();
        f.setLabel("PCMWROUNDSTANDARDJOBCONDITIONCODEDESC: Condition Code Description");
        f.setFunction("CONDITION_CODE_API.Get_Description(:CONDITION_CODE)");

        f = itemblk0.addField("PART_OWNERSHIP");
        f.setSize(25);
        f.setInsertable();
        f.setSelectBox();
        f.enumerateValues("PART_OWNERSHIP_API");
        f.setLabel("PCMWROUNDSTANDARDJOBPARTOWNERSHIP: Ownership");
        f.setCustomValidation("PART_OWNERSHIP","PART_OWNERSHIP_DB");

        f = itemblk0.addField("PART_OWNERSHIP_DB");
        f.setSize(20);
        f.setHidden();

        f = itemblk0.addField("OWNER");
        f.setSize(15);
        f.setMaxLength(20);
        f.setInsertable();
        f.setLabel("PCMWROUNDSTANDARDJOBPARTOWNER: Owner");
        f.setCustomValidation("OWNER,PART_OWNERSHIP_DB","OWNER_NAME");
        f.setDynamicLOV("CUSTOMER_INFO");

        f = itemblk0.addField("OWNER_NAME");
        f.setSize(20);
        f.setMaxLength(100);
        f.setReadOnly();
        f.setLabel("PCMWROUNDSTANDARDJOBPARTOWNERNAME: Owner Name");
        f.setFunction("CUSTOMER_INFO_API.Get_Name(:OWNER)");

        f = itemblk0.addField("ITEM0_DIMQUALITY");
        f.setSize(18);
        f.setLabel("PCMWROUNDSTANDARDJOBDIMQUA: Dimension/Quality");
        f.setFunction("INVENTORY_PART_API.Get_Dim_Quality(:SPARE_CONTRACT,:ITEM0_PART_NO)");
        f.setReadOnly();

        f = itemblk0.addField("ITEM0_TYPEDESIGNATION");
        f.setSize(18);
        f.setLabel("PCMWROUNDSTANDARDJOBTYPEDES: Type Designation");
        f.setFunction("INVENTORY_PART_API.Get_Type_Designation(:SPARE_CONTRACT,:ITEM0_PART_NO)");
        f.setReadOnly();

        f = itemblk0.addField("ITEM0_PARTUNITCODE");
        f.setSize(11);
        f.setDynamicLOV("ISO_UNIT",600,450); 
        f.setLabel("PCMWROUNDSTANDARDJOBPUNITCO: Unit");
        f.setFunction("INVENTORY_PART_API.Get_Unit_Meas(:SPARE_CONTRACT,:ITEM0_PART_NO)");
        f.setReadOnly();

        f = itemblk0.addField("ITEM0_PLANNED_QTY","Number");
        f.setSize(18);
        f.setMandatory();
        f.setLabel("PCMWROUNDSTANDARDJOBPLQTY: Planned Quantity");
        f.setCustomValidation("ITEM0_CATALOG_NO,NSALESPARTLIST_PRICE,WORK_ORDER_INVOICE_TYPE,QTY_TO_INVOICE,SALES_PRICE,DISCOUNT,ITEM0_PLANNED_QTY","ITEM0_PRICE_AMOUNT");
        f.setDbName("PLANNED_QTY");

        f = itemblk0.addField("ITEM0_CONTRACT");
        f.setSize(18);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
        f.setLabel("PCMWROUNDSTDSALCON: Sales Part Site");
        f.setUpperCase();
        f.setDbName("CONTRACT");
        f.setMaxLength(5);
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk0.addField("ITEM0_CATALOG_NO");
        f.setSize(18);
        f.setDynamicLOV("SALES_PART_ACTIVE_LOV","ITEM0_CONTRACT CONTRACT",600,450);  
        f.setLabel("PCMWROUNDSTANDARDJOBCANO: Sales Part Number");
        f.setUpperCase();
        f.setDbName("CATALOG_NO");
        f.setCustomValidation("ITEM0_CONTRACT,ITEM0_PART_NO,ITEM0_CATALOG_NO,NSALESPARTLIST_PRICE,WORK_ORDER_INVOICE_TYPE,QTY_TO_INVOICE,SALES_PRICE,DISCOUNT,ITEM0_PLANNED_QTY","ITEM0_SALESPARTCATALOG_DESC,NSALESPARTLIST_PRICE,NSALESPARTCOST,ITEM0_PRICE_AMOUNT");
        f.setMaxLength(25);
        f.setDefaultNotVisible();

        f = itemblk0.addField("ITEM0_SALESPARTCATALOG_DESC");
        f.setSize(50);
        f.setLabel("PCMWROUNDSTANDARDJOBSAPADESC: Description");
        if (mgr.isModuleInstalled("ORDER"))
            f.setFunction("SALES_PART_API.Get_Catalog_Desc(:ITEM0_CONTRACT,:ITEM0_CATALOG_NO)");
        else
            f.setFunction("''");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk0.addField("NSALESPARTCOST","Number");
        f.setSize(11);
        f.setLabel("PCMWROUNDSTANDARDJOBNSAPACO: Cost");
        //f.setFunction("Inventory_Part_API.Get_Inventory_Value_By_Method(:ITEM0_CONTRACT,:ITEM0_PART_NO)");
        f.setFunction("Active_Separate_API.Get_Invent_Value(:ITEM0_CONTRACT,:ITEM0_PART_NO,NULL,'*',:CONDITION_CODE)");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk0.addField("NSALESPARTLIST_PRICE","Number");
        f.setSize(18);
        f.setLabel("PCMWROUNDSTANDARDJOBNSAPALIPRI: List Price");
        if (mgr.isModuleInstalled("ORDER"))
            f.setFunction("SALES_PART_API.Get_List_Price(:ITEM0_CONTRACT,:ITEM0_CATALOG_NO)");
        else
            f.setFunction("''");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk0.addField("WORK_ORDER_INVOICE_TYPE");
        f.setSize(25);
        f.setLabel("PCMWROUNDSTANDARDJOBWRKOIT: Work Order Invoice Type");
        f.setSelectBox();
        f.enumerateValues("WORK_ORDER_INVOICE_TYPE_API");
        f.setCustomValidation("ITEM0_CATALOG_NO,NSALESPARTLIST_PRICE,WORK_ORDER_INVOICE_TYPE,QTY_TO_INVOICE,SALES_PRICE,DISCOUNT,ITEM0_PLANNED_QTY","ITEM0_PRICE_AMOUNT");
        f.setInsertable();
        f.setDefaultNotVisible();

        f = itemblk0.addField("QTY_TO_INVOICE","Number");
        f.setSize(18);
        f.setLabel("PCMWROUNDSTANDARDJOBQTYINV: Qty To Invoice");
        f.setCustomValidation("ITEM0_CATALOG_NO,NSALESPARTLIST_PRICE,WORK_ORDER_INVOICE_TYPE,QTY_TO_INVOICE,SALES_PRICE,DISCOUNT,ITEM0_PLANNED_QTY","ITEM0_PRICE_AMOUNT");
        f.setDefaultNotVisible();

        f = itemblk0.addField("SALES_PRICE","Number");
        f.setSize(18);
        f.setCustomValidation("ITEM0_CATALOG_NO,NSALESPARTLIST_PRICE,WORK_ORDER_INVOICE_TYPE,QTY_TO_INVOICE,SALES_PRICE,DISCOUNT,ITEM0_PLANNED_QTY","ITEM0_PRICE_AMOUNT");
        f.setLabel("PCMWROUNDSTANDARDJOBSLSPRICE: Sales Price/Unit");
        f.setDefaultNotVisible();

        f = itemblk0.addField("DISCOUNT","Number");
        f.setSize(18);
        f.setLabel("PCMWROUNDSTANDARDJOBDSCNT: Discount");
        f.setCustomValidation("ITEM0_CATALOG_NO,NSALESPARTLIST_PRICE,WORK_ORDER_INVOICE_TYPE,QTY_TO_INVOICE,SALES_PRICE,DISCOUNT,ITEM0_PLANNED_QTY","ITEM0_PRICE_AMOUNT");
        f.setDefaultNotVisible();
        f.setHidden();

        f = itemblk0.addField("ITEM0_PRICE_AMOUNT","Number");
        f.setSize(11);
        f.setLabel("PCMWRNDSTDJOBITM0PRCAMNT: Price Amount");
        f.setFunction("''");
        f.setReadOnly();

        f = itemblk0.addField("ITEM0_STD_JOB_ID");
        f.setHidden();
        f.setUpperCase();
        f.setDbName("STD_JOB_ID");

        f = itemblk0.addField("ITEM0_STD_JOB_REVISION");
        f.setHidden();
        f.setUpperCase();
        f.setDbName("STD_JOB_REVISION");
        f.setMaxLength(6);

        f = itemblk0.addField("ITEM0_STD_JOB_CONTRACT");
        f.setHidden();
        f.setUpperCase();
        f.setDbName("STD_JOB_CONTRACT");

        f = itemblk0.addField("STD_JOB_SPARE_SEQ");
        f.setSize(11);
        f.setHidden();

        f = itemblk0.addField("SUPPLY_CODE");
        f.setFunction("''");
        f.setHidden();       

        f = itemblk0.addField("INVOICETYPE");
        f.setFunction("''");
        f.setHidden();  

        f = itemblk0.addField("SUPPLIER_ID");
        f.setFunction("''");
        f.setHidden(); 

        f = itemblk0.addField("F_SYSDATE");
        f.setFunction("''");
        f.setHidden(); 

        f = itemblk0.addField("CHCKEXIST");
        f.setFunction("''");
        f.setHidden();               

        f = itemblk0.addField("COND_CODE_USAGE");
        f.setFunction("''");
        f.setHidden();   

        f = itemblk0.addField("CAT_EXIST","Number");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("PROJECT_ID");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("SERIAL_NO");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("CONFIGURATION_ID");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("ACTIVEIND");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("ACTIVEIND_DB");
        f.setHidden();
        f.setFunction("''");

        itemblk0.setView("STANDARD_JOB_SPARE");
        itemblk0.defineCommand("STANDARD_JOB_SPARE_API","New__,Modify__,Remove__");
        itemblk0.setMasterBlock(headblk);
        itemset0= itemblk0.getASPRowSet();

        itembar0 = mgr.newASPCommandBar(itemblk0);

        itembar0.enableCommand(itembar0.FIND);
        itembar0.enableCommand(itembar0.NEWROW);
        itembar0.enableCommand(itembar0.SAVERETURN);

        itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");
        itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
        itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
        itembar0.defineCommand(itembar0.DELETE,"removeITEM0");

        //itembar0.defineCommand(itembar0.SAVERETURN,null,"checkItem0Fields(-1)");
        itembar0.defineCommand(itembar0.SAVERETURN,"saveReturnITEM0","checkItem0Fields(-1)");
        itembar0.defineCommand(itembar0.SAVENEW,null,"checkItem0Fields(-1)");

        itemtbl0 = mgr.newASPTable(itemblk0);
        itemtbl0.setWrap();

        //Web Alignment - Multirow Action
        itemtbl0.enableRowSelect();
        itembar0.enableMultirowAction();
        //

        itembar0.addCustomCommand("none","");
        if (mgr.isPresentationObjectInstalled("invenw/InventoryPartInventoryPartCurrentOnHand.page"))
            itembar0.addCustomCommand("cuQuInHand",mgr.translate("PCMWROUNDSTANDARDJOBCQIHAND: Current Quantity on Hand..."));
        if (mgr.isPresentationObjectInstalled("invenw/InventoryPartAvailabilityPlanningQry.page"))
            itembar0.addCustomCommand("avaiDetails",mgr.translate("PCMWROUNDSTANDARDJOBAVADETAILS: Query - Inventory Part Availability Planning..."));
        if (mgr.isPresentationObjectInstalled("invenw/InventoryPart.page"))
            itembar0.addCustomCommand("invenPartLimited",mgr.translate("PCMWROUNDSTANDARDJOBINPALIMITED: Inventory Part..."));
        if (mgr.isPresentationObjectInstalled("purchw/PurchasePartSupplier.page"))
            itembar0.addCustomCommand("suppPerPart",mgr.translate("PCMWROUNDSTANDARDJOBSUPERPART: Supplier Per Part..."));

        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

        //-----------------------------------------------------------------------
        //---------------------------  Prepare Tab  -----------------------------
        //-----------------------------------------------------------------------

        itemblk1 = mgr.newASPBlock("ITEM1");

        f = itemblk1.addField("ITEM1_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk1.addField("ITEM1_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk1.addField("JOB_CATEGORY");
        f.setSize(50);
        f.setMandatory();
        f.setInsertable();
        f.setSelectBox();
        f.enumerateValues("JOB_CATEGORY_API");
        f.setLabel("PCMWROUTESTANDARDJOBITEM1CATCL: Category");
        f.setMaxLength(200);
        f.setCustomValidation("JOB_CATEGORY,IDENTITY","JOB_CATEGORY_DB,ITEM1_DESCRIPTION");

        f = itemblk1.addField("JOB_CATEGORY_DB");
        f.setMaxLength(20);     
        f.setHidden();

        f = itemblk1.addField("IDENTITY");
        f.setSize(20); 
        f.setMaxLength(50);
        f.setDynamicLOV("TYPE_DESIGNATION",600,450);
        f.setLabel("PCMWROUTESTANDARDJOBITEM1IDENT: Identity");
        f.setReadOnly();
        f.setMandatory();
        f.setInsertable();

        f = itemblk1.addField("ITEM1_DESCRIPTION");
        f.setFunction("STD_JOB_CATEGORY_API.Get_Description(:JOB_CATEGORY_DB,:IDENTITY)");
        f.setMaxLength(200);  
        f.setReadOnly();
        f.setLabel("PCMWROUTESTANDARDJOBITEM1DECR: Description");
        mgr.getASPField("IDENTITY").setValidation("STD_JOB_CATEGORY_API.Get_Description","JOB_CATEGORY_DB,IDENTITY","ITEM1_DESCRIPTION");

        f = itemblk1.addField("ITEM1_STD_JOB_ID");
        f.setHidden();
        f.setDbName("STD_JOB_ID");
        f.setUpperCase();

        f = itemblk1.addField("ITEM1_STD_JOB_REVISION");
        f.setHidden();
        f.setDbName("STD_JOB_REVISION");
        f.setMaxLength(6);
        f.setUpperCase();   

        f = itemblk1.addField("ITEM1_STD_JOB_CONTRACT");
        f.setHidden();
        f.setDbName("STD_JOB_CONTRACT");
        f.setUpperCase();

        itemblk1.setView("STD_JOB_CATEGORY");
        itemblk1.defineCommand("STD_JOB_CATEGORY_API","New__,Modify__,Remove__");
        itemblk1.setMasterBlock(headblk);

        itemset1 = itemblk1.getASPRowSet();

        itembar1 = mgr.newASPCommandBar(itemblk1);

        itembar1.enableCommand(itembar1.FIND);
        itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1");
        itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
        itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");

        itemtbl1 = mgr.newASPTable(itemblk1);
        itemtbl1.setWrap();

        itemlay1 = itemblk1.getASPBlockLayout();
        itemlay1.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

        //================================================================
        // Permit
        //================================================================ 

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
        f.setUpperCase();

        f = itemblk2.addField("ITEM2_STD_JOB_REVISION");
        f.setHidden();
        f.setDbName("STD_JOB_REVISION");
        f.setMaxLength(6);
        f.setUpperCase();   

        f = itemblk2.addField("ITEM2_STD_JOB_CONTRACT");
        f.setHidden();
        f.setDbName("STD_JOB_CONTRACT");
        f.setUpperCase();

        f = itemblk2.addField("PERMIT_TYPE_ID");
        f.setSize(13);
        f.setDynamicLOV("PERMIT_TYPE_NONE",600,450);
        f.setMandatory();
        f.setLabel("PCMWRNDSTDJOBPERTYPE: Permit Type");
        f.setUpperCase();
        f.setReadOnly();
        f.setInsertable();
        f.setMaxLength(4);

        f = itemblk2.addField("PERMITTYPEDESCRIPTION");
        f.setSize(65);
        f.setLabel("PCMWRNDSTDJOBPERTYPEDESC: Description");
        f.setFunction("PERMIT_TYPE_API.Get_Description(:PERMIT_TYPE_ID)");
        mgr.getASPField("PERMIT_TYPE_ID").setValidation("PERMITTYPEDESCRIPTION");
        f.setReadOnly();

        itemblk2.setView("STANDARD_JOB_PERMIT");
        itemblk2.defineCommand("STANDARD_JOB_PERMIT_API","New__,Remove__");
        itemset2 = itemblk2.getASPRowSet();
        itemblk2.setMasterBlock(headblk);

        itembar2 = mgr.newASPCommandBar(itemblk2);

        itembar2.enableCommand(itembar2.FIND);

        //itembar2.defineCommand(itembar2.SAVERETURN,null,"checkItem2Fields(-1)"); 
        //itembar2.defineCommand(itembar2.SAVENEW,null,"checkItem2Fields(-1)");   
        itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");          
        itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");   
        itembar2.defineCommand(itembar2.NEWROW,"newRowITEM2");   

        itembar2.addCustomCommand("attributes",mgr.translate("PCMWRNDSTDJOBCDN111: Attributes..."));

        itembar2.enableMultirowAction();

        itemtbl2 = mgr.newASPTable(itemblk2);
        itemtbl2.setWrap();
        itemtbl2.enableRowSelect();

        itemlay2 = itemblk2.getASPBlockLayout();
        itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);  

        //================================================================
        // Programs tab
        //================================================================= 

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
        f.setUpperCase();

        f = itemblk3.addField("ITEM3_STD_JOB_REVISION");
        f.setHidden();
        f.setDbName("STD_JOB_REVISION");
        f.setMaxLength(6);
        f.setUpperCase();   

        f = itemblk3.addField("ITEM3_STD_JOB_CONTRACT");
        f.setHidden();
        f.setDbName("STD_JOB_CONTRACT");
        f.setUpperCase();

        f = itemblk3.addField("JOB_PROGRAM_ID");   
        f.setSize(14);
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2JOBPROGID: Job Program Id");

        f = itemblk3.addField("JOB_PROGRAM_DESC");
        f.setFunction("Job_Program_API.Get_Description(:JOB_PROGRAM_ID, :JOB_PROGRAM_REVISION, :JOB_PROGRAM_CONTRACT)");
        f.setSize(14);
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2JOBPROGDESC: Description");


        f = itemblk3.addField("JOB_PROGRAM_REVISION");   
        f.setSize(14);
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2JOBPROGREV: Revision");

        f = itemblk3.addField("JOB_PROGRAM_CONTRACT");   
        f.setSize(14);
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2JOBPROGCONT: Site");

        f = itemblk3.addField("JOB_PROGRAM_STATE");   
        f.setFunction("Job_Program_API.Get_Status(:JOB_PROGRAM_ID,:JOB_PROGRAM_REVISION,:JOB_PROGRAM_CONTRACT)");
        f.setSize(14);
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2JOBPROGSTATE: Status");


        f = itemblk3.addField("JOB_PROGRAM_TYPE_ID");
        f.setFunction("Job_Program_API.Get_Program_Type_Id(:JOB_PROGRAM_ID,:JOB_PROGRAM_REVISION,:JOB_PROGRAM_CONTRACT)");
        f.setSize(14);
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2JOBPROGTYPEID: Type Id");

        f = itemblk3.addField("JOB_PROGRAM_TYPE_DESC");   
        f.setFunction("Program_Type_API.Get_Description(Job_Program_API.Get_Program_Type_Id(:JOB_PROGRAM_ID,:JOB_PROGRAM_REVISION,:JOB_PROGRAM_CONTRACT))");
        f.setSize(14);
        f.setReadOnly();
        f.setLabel("PCMWSEPARATESTANDARDJOB2JOBPROGTYPEDESC: Description");

        itemblk3.setView("STANDARD_JOB_PROGRAM");
        itemblk3.defineCommand("STANDARD_JOB_PROGRAM_API","");
        itemblk3.setMasterBlock(headblk);

        itemset3 = itemblk3.getASPRowSet();
        itembar3 = mgr.newASPCommandBar(itemblk3);
        itemtbl3 = mgr.newASPTable(itemblk3);
        itemtbl3.setWrap();

        itembar3.defineCommand(itembar3.OKFIND,"okFindITEM3");
        itembar3.defineCommand(itembar3.COUNTFIND,"countFindITEM3");

        itemlay3 = itemblk3.getASPBlockLayout();
        itemlay3.setDialogColumns(1);
        itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);


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

        f = itemblk6.addField("MAT_BUDGET_COST","Money");   
        f.setCustomValidation("MAT_BUDGET_COST,EXT_BUDGET_COST", "TOT_BUDGET_COST");
        f.setSize(14);
        f.setHidden();

        f = itemblk6.addField("EXT_BUDGET_COST","Money");   
        f.setCustomValidation("MAT_BUDGET_COST,EXT_BUDGET_COST", "TOT_BUDGET_COST");
        f.setSize(14);
        f.setHidden();

        f = itemblk6.addField("TOT_BUDGET_COST","Money");   
        f.setFunction("nvl(MAT_BUDGET_COST,0)+nvl(EXT_BUDGET_COST,0)");
        f.setSize(14);
        f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("PLAN_MAT_COST","Money");   
        f.setFunction("''");
        f.setSize(14);
        f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("PLAN_EXT_COST","Money");   
        f.setFunction("''");
        f.setSize(14);
        f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("TOT_PLAN_COST","Money");   
        f.setFunction("''");
        f.setSize(14);
        f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("PLAN_MAT_REV","Money");   
        f.setFunction("''");
        f.setSize(14);
        f.setReadOnly();
        f.setHidden();

        f = itemblk6.addField("PLAN_EXT_REV","Money");   
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

        itembar6.enableCommand(itembar6.EDIT);
        itembar6.enableCommand(itembar6.SAVERETURN);
        itembar6.defineCommand(itembar6.OKFIND,"okFindITEM6");
        itembar6.defineCommand(itembar6.COUNTFIND,"countFindITEM6");
        itembar6.defineCommand(itembar6.SAVERETURN,"saveReturnITEM6","checkItem6Fields(-1)");
        itembar6.disableCommand(itembar6.BACK);

        itembar6.disableMultirowAction();

        itemlay6 = itemblk6.getASPBlockLayout();
        itemlay6.setDialogColumns(1);
        itemlay6.setDefaultLayoutMode(itemlay6.SINGLE_LAYOUT);



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

        eventblk.setView("ROUND_STANDARD_JOB");
        eventset = eventblk.getASPRowSet();
    }

    public void checkObjAvaileble()
    {
        ASPManager mgr = getASPManager();

        ASPBuffer availObj;
        trans.clear();
        trans.addSecurityQuery("INVENTORY_PART,INVENTORY_PART_CONFIG,PURCHASE_PART_SUPPLIER");
        trans.addPresentationObjectQuery("invenw/InventoryPartInventoryPartCurrentOnHand.page,invenw/InventoryPartAvailabilityPlanningQry.page,invenw/InventoryPart.page,purchw/PurchasePartSupplier.page");

        trans = mgr.perform(trans);

        availObj = trans.getSecurityInfo();

        if (availObj.itemExists("INVENTORY_PART") && availObj.namedItemExists("invenw/InventoryPartInventoryPartCurrentOnHand.page"))

            cuQuInHand_ = true;

        if (availObj.itemExists("INVENTORY_PART_CONFIG") && availObj.namedItemExists("invenw/InventoryPartAvailabilityPlanningQry.page"))
            avaiDetails_= true;

        if (availObj.itemExists("INVENTORY_PART") && availObj.namedItemExists("invenw/InventoryPart.page"))
            invenPartLimited_= true;

        if (availObj.itemExists("PURCHASE_PART_SUPPLIER") && availObj.namedItemExists("purchw/PurchasePartSupplier.page"))
            suppPerPart_= true;
    }

    public void DissableRmbs()
    {
        ASPManager mgr = getASPManager();

        if (mgr.isPresentationObjectInstalled("invenw/InventoryPartInventoryPartCurrentOnHand.page")  && !cuQuInHand_)
            itembar0.removeCustomCommand("cuQuInHand");

        if (mgr.isPresentationObjectInstalled("invenw/InventoryPartAvailabilityPlanningQry.page")  && !avaiDetails_)
            itembar0.removeCustomCommand("avaiDetails");
        if (mgr.isPresentationObjectInstalled("invenw/InventoryPart.page") && !invenPartLimited_)
            itembar0.removeCustomCommand("invenPartLimited");
        if (mgr.isPresentationObjectInstalled("purchw/PurchasePartSupplier.page") && !suppPerPart_)
            itembar0.removeCustomCommand("suppPerPart");
    }

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


    public void activateMaterialTab()
    {
        tabs.setActiveTab(4);
    } 

    public void activatePermitTab()
    {
        tabs.setActiveTab(5);
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        laymode = String.valueOf( headlay.getLayoutMode() );

        headbar.removeCustomCommand("activatePrepareTab");
        headbar.removeCustomCommand("activateMaterialTab");
        headbar.removeCustomCommand("activatePermitTab");
        headbar.removeCustomCommand("activateProgramTab");
        headbar.removeCustomCommand("activateBudgetTab");

        if (headset.countRows()>0)
        {
            if (headlay.isMultirowLayout())
            {
                headset.goTo(headset.getRowSelected());
            }

            String state = headset.getRow().getValue("OBJSTATE");
            String con = headset.getRow().getValue("IS_CONNECTED");

            if (("Obsolete".equals(state)) || ("Active".equals(state) && "TRUE".equals(con)))
            {
                itembar0.disableCommand(itembar0.NEWROW);
                itembar1.disableCommand(itembar1.NEWROW);
                itembar2.disableCommand(itembar2.NEWROW);
            }
        }

        if (headset.countRows() == 1)
            headbar.disableCommand(headbar.BACK);
        if (headset.countRows()>0)
        {
            if (itemset0.countRows()> 0)
                DissableRmbs();
        }
        if (headlay.isEditLayout())
        {
            if ((headset.getRow().getValue("CBMATERIALS")) == "TRUE")
                mgr.getASPField("STD_TEXT_ONLY").setReadOnly();
        }

        if (headlay.isNewLayout())
        {
            mgr.getASPField("CBMATERIALS").setReadOnly();
            mgr.getASPField("CREATION_DATE").setReadOnly();
        }

        if (headlay.isSingleLayout() && headset.countRows()>0)
        {
            title_ = mgr.translate("PCMWROUNDSTANDARDJOBDD: Route Standard Job - ");

            xx = headset.getRow().getValue("STD_JOB_ID");
            yy = headset.getRow().getValue("DEFINITION");
        }
        else
        {
            title_ = mgr.translate("PCMWROUNDSTANDARDJOBDDMULTI: Route Standard Job");
            xx = "";
            yy = "";
        }     

        fldTitleCreatedBy = mgr.translate("PCMWROUNDSTANDARDJOBCREATEDBYFLD: Created+by");
        fldTitleChangedBy = mgr.translate("PCMWROUNDSTANDARDJOBCHANGEDBYFLD: Changed+by");

        lovTitleCreatedBy = mgr.translate("PCMWROUNDSTANDARDJOBCREATEDBYLOV: List of Created by");
        lovTitleChangedBy = mgr.translate("PCMWROUNDSTANDARDJOBCHANGEDBYLOV: List of Changed by");

        item0RowStatus = itemset0.getRowStatus();

        if (itemlay0.isVisible())
        {
            mgr.getASPField("SPARE_CONTRACT").setLOVProperty("WHERE","Site_API.Get_Company(CONTRACT) = '"+headset.getRow().getValue("COMPANY")+"'");
        }

        if (tabs.getActiveTab() == 3)
        {
            if (itemset6.countRows() == 0)
            {
                matBudg   = "";
                extBudg   = "";
                totBudg   = "";
                
                planMat   = "";
                planExt   = "";
                planTot   = "";

                planMatRev   = "";
                planExtRev   = "";
                planTotRev   = "";

            }
            else
            {
                getBudgetValues();

                double matBudgNF   = itemset6.getNumberValue("MAT_BUDGET_COST");
                double extBudgNF   = itemset6.getNumberValue("EXT_BUDGET_COST");
                double totBudgNF   = itemset6.getNumberValue("TOT_BUDGET_COST");

                matBudg    = mgr.formatNumber("MAT_BUDGET_COST", matBudgNF);
                extBudg    = mgr.formatNumber("EXT_BUDGET_COST", extBudgNF);
                totBudg    = mgr.formatNumber("TOT_BUDGET_COST", totBudgNF);

                double planMatNF   = itemset6.getNumberValue("PLAN_MAT_COST");
                double planExtNF   = itemset6.getNumberValue("PLAN_EXT_COST");
                double planTotNF   = itemset6.getNumberValue("TOT_PLAN_COST");

                planMat   = mgr.formatNumber("PLAN_MAT_COST", planMatNF);
                planExt   = mgr.formatNumber("PLAN_EXT_COST", planExtNF);
                planTot   = mgr.formatNumber("TOT_PLAN_COST", planTotNF);

                double planMatRevNF   = itemset6.getNumberValue("PLAN_MAT_REV");
                double planExtRevNF   = itemset6.getNumberValue("PLAN_EXT_REV");
                double planTotRevNF   = itemset6.getNumberValue("TOT_PLAN_REV");

                planMatRev   = mgr.formatNumber("PLAN_MAT_REV", planMatRevNF);
                planExtRev   = mgr.formatNumber("PLAN_EXT_REV", planExtRevNF);
                planTotRev   = mgr.formatNumber("TOT_PLAN_REV", planTotRevNF);
            }
        }

        if ( itemlay0.isNewLayout() || itemlay0.isEditLayout() || itemlay1.isNewLayout() || itemlay1.isEditLayout() ||
	     itemlay2.isNewLayout() || itemlay2.isEditLayout() || itemlay6.isEditLayout() )
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
        return "PCMWROUNDSTANDARDJOBTITLE: Route Standard Job";
    }

    protected String getTitle()
    {
        return "PCMWROUNDSTANDARDJOBTITLE: Route Standard Job";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        printHiddenField("HIDDENPARTNO","");
        printHiddenField("ONCEGIVENERROR","FALSE");

        appendToHTML(headlay.show());

        if (headset.countRows() > 0 && headlay.isSingleLayout())
        {
            appendToHTML(tabs.showTabsInit());

            if (tabs.getActiveTab() == 1)
            {
                appendToHTML(itemlay1.show());

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
                appendDirtyJavaScript("	  var enable_multichoice =(true && ITEM1_IN_FIND_MODE);\n");
                appendDirtyJavaScript("	  var key_value = (getValue_('IDENTITY',i).indexOf('%') !=-1)? getValue_('IDENTITY',i):'';\n");
                appendDirtyJavaScript("	  openLOVWindow('IDENTITY',i,\n");
                appendDirtyJavaScript("                 '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=' + viewName + '&__FIELD=Identity&__INIT=1&MULTICHOICE='+enable_multichoice+''\n");
                appendDirtyJavaScript("                 + '&__KEY_VALUE=' + URLClientEncode(getValue_('IDENTITY',i))\n");
                appendDirtyJavaScript("                 + '&IDENTITY=' + URLClientEncode(key_value)\n");
                appendDirtyJavaScript("                 ,600,450,'validateIdentity');\n");
                appendDirtyJavaScript("}\n");
            }
            else if (tabs.getActiveTab() == 2)
            {
                appendToHTML(itemlay3.show());
            }

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
                    appendToHTML(fmt.drawWriteLabel("PCMWROUNDSTDJOBBUDGCOST: Budget Cost"));
                    appendToHTML("		</td>\n");
                    appendToHTML("		<td width=80 align=\"middle\">");
                    appendToHTML(fmt.drawWriteLabel("PCMWROUNDSTDJOBPLANCOST: Planned Cost"));
                    appendToHTML("		</td>\n");
                    appendToHTML("		<td width=100 align=\"middle\">");
                    appendToHTML(fmt.drawWriteLabel("PCMWROUNDSTDJOBPLANREV: Planned Revenue"));
                    appendToHTML("		</td>\n");
                    appendToHTML("		<td width=80>\n");
                    appendToHTML("		</td>\n");
                    appendToHTML("	</tr>	\n");
                    appendToHTML("	<tr>\n");
                    appendToHTML("		<td width=0>");
                    appendToHTML(fmt.drawWriteLabel("PCMWROUNDSTDJOBMATERIAL: Material"));
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
                    appendToHTML(fmt.drawWriteLabel("PCMWROUNDSTDJOBEXTERNAL: External"));
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
                    appendToHTML(fmt.drawWriteLabel("PCMWROUNDSTDJOBTOTAL: Total"));
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
                    appendToHTML(fmt.drawWriteLabel("PCMWROUNDSTDJOBBUDGCOST: Budget Cost"));
                    appendToHTML("		</td>\n");
                    appendToHTML("		<td width=80 align=\"middle\">");
                    appendToHTML(fmt.drawWriteLabel("PCMWROUNDSTDJOBPLANCOST: Planned Cost"));
                    appendToHTML(                                       "		</td>\n");
                    appendToHTML("		<td width=100 align=\"middle\">");
                    appendToHTML(fmt.drawWriteLabel("PCMWROUNDSTDJOBPLANREV: Planned Revenue"));
                    appendToHTML("		</td>\n");
                    appendToHTML("		<td width=80>\n");
                    appendToHTML("		</td>\n");
                    appendToHTML("	</tr>	\n");
                    appendToHTML("	<tr>\n");
                    appendToHTML("		<td width=0>");
                    appendToHTML(fmt.drawWriteLabel("PCMWROUNDSTDJOBMATERIAL: Material"));
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
                    appendToHTML(fmt.drawWriteLabel("PCMWROUNDSTDJOBEXTERNAL: External"));
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
                    appendToHTML(fmt.drawWriteLabel("PCMWROUNDSTDJOBTOTAL: Total"));
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
            {
                if (itemlay0.isVisible() && ("FALSE".equals(headset.getRow().getValue("STD_TEXT_ONLY"))))
                {
                    appendToHTML(itembar0.showBar());

                    if ("New__".equals(item0RowStatus))
                    {
                        appendToHTML("<font class=\"TextLabel\" face=\"Arial\"><a href=\"javascript:LovPartNoPurch(-1)\">");
                        appendToHTML(mgr.translate("PCMWPMACTIONROUNDLOVPURCH: LOV from Purchase Part..."));
                        appendToHTML("</a></font>\n");
                    }

                    appendToHTML(itemlay0.generateDataPresentation());
                }
            }
            else if (tabs.getActiveTab() == 5)
            {
                appendToHTML(itemlay2.show());
            }
        }

        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

        appendDirtyJavaScript("function assignMatBudgetCost()\n");
        appendDirtyJavaScript("{\n");
	appendDirtyJavaScript("   document.form.MAT_BUDGET_COST.value = document.form.MAT_BUDGET_COST1.value;\n");
	appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("function assignExtBudgetCost()\n");
        appendDirtyJavaScript("{\n");
	appendDirtyJavaScript("   document.form.EXT_BUDGET_COST.value = document.form.EXT_BUDGET_COST1.value;\n");
	appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function HypDoc()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   sStdJobIDVar = document.form.STD_JOB_ID.value;\n");
        appendDirtyJavaScript("   sStdJobREVVar = document.form.STD_JOB_REVISION.value;\n");
        appendDirtyJavaScript("   sStdJobCONTVar = document.form.CONTRACT.value;\n");
        appendDirtyJavaScript("   if (sStdJobIDVar =='' || sStdJobREVVar=='' || sStdJobCONTVar=='')\n");
        appendDirtyJavaScript("      window.open(\"../docmaw/DocReference.page\",\"hypeWindow\",\"scrollbars,resizable,status=yes,width=600,height=445\");\n");
        appendDirtyJavaScript("   else\n");
        appendDirtyJavaScript("      window.open(\"../docmaw/DocReference.page?LU_NAME=StandardJob&KEY_REF=STD_JOB_ID=\"+URLClientEncode(sStdJobIDVar)+\"&CONTRACT=\"+URLClientEncode(sStdJobCONTVar)+\"&STD_JOB_REVISION=\"+URLClientEncode(sStdJobREVVar)+\"^\",\"hypeWindow\",\"scrollbars,resizable,status=yes,width=600,height=445\");  \n");
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

        appendDirtyJavaScript("window.name = \"CopyWoOr\";\n");

        appendDirtyJavaScript("function LovPartNoPurch(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("        lovUrl = '");
        appendDirtyJavaScript(mgr.getConfigParameter("APPLICATION/LOCATION/ROOT"));
        appendDirtyJavaScript("'; \n");
        appendDirtyJavaScript("	openLOVWindow('ITEM0_PART_NO',i,\n");
        appendDirtyJavaScript("		'../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PURCHASE_PART_LOV&__FIELD='+URLClientEncode(\"");
        appendDirtyJavaScript(mgr.translateJavaScript("PARTBNO: Part No"));
        appendDirtyJavaScript("\")+'&__TITLE='+URLClientEncode(\"");
        appendDirtyJavaScript(mgr.translateJavaScript("LOISTPRICE: List of Part No"));
        appendDirtyJavaScript("\")\n");
        appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))\n");
        appendDirtyJavaScript("		,600,450,'validateItem0PartNo');\n");
        appendDirtyJavaScript("}\n");        

        appendDirtyJavaScript("function validateItem0PartNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("f.HIDDENPARTNO.value = \"\";\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkItem0PartNo(i) ) return;\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=ITEM0_PART_NO'\n");
        appendDirtyJavaScript("		+ '&SPARE_CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM0_PART_NO=' + URLClientEncode(getValue_('ITEM0_PART_NO',i))\n");
        appendDirtyJavaScript("		+ '&ITEM0_CONTRACT=' + URLClientEncode(getValue_('ITEM0_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM0_CATALOG_NO=' + URLClientEncode(getValue_('ITEM0_CATALOG_NO',i))\n");
        appendDirtyJavaScript("		+ '&SALES_PRICE=' + URLClientEncode(getValue_('SALES_PRICE',i))\n");
        appendDirtyJavaScript("		+ '&WORK_ORDER_INVOICE_TYPE=' + SelectURLClientEncode('WORK_ORDER_INVOICE_TYPE',i)		+ '&DISCOUNT=' + URLClientEncode(getValue_('DISCOUNT',i))\n");
        appendDirtyJavaScript("		+ '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
        appendDirtyJavaScript("		+ '&ITEM0_PLANNED_QTY=' + URLClientEncode(getValue_('ITEM0_PLANNED_QTY',i))\n");
        appendDirtyJavaScript("		+ '&ITEM0_STD_JOB_CONTRACT=' + URLClientEncode(getValue_('ITEM0_STD_JOB_CONTRACT',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'ITEM0_PART_NO',i,'Part No') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM0_PARTDESCRIPTION',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM0_DIMQUALITY',i,1);\n");
        appendDirtyJavaScript("		assignValue_('ITEM0_TYPEDESIGNATION',i,2);\n");
        appendDirtyJavaScript("		assignValue_('ITEM0_PARTUNITCODE',i,3);\n");
        appendDirtyJavaScript("		assignValue_('NSALESPARTCOST',i,4);\n");
        appendDirtyJavaScript("		assignValue_('ITEM0_CATALOG_NO',i,5);\n");
        appendDirtyJavaScript("		assignValue_('ITEM0_SALESPARTCATALOG_DESC',i,6);\n");
        appendDirtyJavaScript("		assignValue_('NSALESPARTLIST_PRICE',i,7);\n");
        appendDirtyJavaScript("		assignValue_('ITEM0_PRICE_AMOUNT',i,8);\n");
        appendDirtyJavaScript("		assignValue_('CONDITION_CODE',i,9);\n");
        appendDirtyJavaScript("		assignValue_('CONDITION_CODE_DESC',i,10);\n");
        appendDirtyJavaScript("		assignValue_('ACTIVEIND_DB',i,11);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("else{\n");
        appendDirtyJavaScript("f.HIDDENPARTNO.value = f.ITEM0_PART_NO.value;\n");
        appendDirtyJavaScript("f.ONCEGIVENERROR.value = \"TRUE\";\n");
        appendDirtyJavaScript("getField_('ITEM0_PART_NO',i).value = '';\n");
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("   if (f.ACTIVEIND_DB.value == 'N') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWROUNDSTANDARDJOBINVSALESPART: All sale parts connected to the part are inactive."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.ITEM0_CATALOG_NO.value = ''; \n");
        appendDirtyJavaScript("      f.ITEM0_SALESPARTCATALOG_DESC.value = ''; \n");   
        appendDirtyJavaScript("      f.NSALESPARTLIST_PRICE.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("}\n");


        //Validation for Sales Price/Unit
        appendDirtyJavaScript("function validateSalesPrice(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkSalesPrice(i) ) return;\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=SALES_PRICE'\n");
        appendDirtyJavaScript("		+ '&ITEM0_CATALOG_NO=' + URLClientEncode(getValue_('ITEM0_CATALOG_NO',i))\n");
        appendDirtyJavaScript("		+ '&NSALESPARTLIST_PRICE=' + URLClientEncode(getValue_('NSALESPARTLIST_PRICE',i))\n");
        appendDirtyJavaScript("		+ '&WORK_ORDER_INVOICE_TYPE=' + SelectURLClientEncode('WORK_ORDER_INVOICE_TYPE',i)\n");
        appendDirtyJavaScript("		+ '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
        appendDirtyJavaScript("		+ '&SALES_PRICE=' + URLClientEncode(getValue_('SALES_PRICE',i))\n");
        appendDirtyJavaScript("		+ '&DISCOUNT=' + URLClientEncode(getValue_('DISCOUNT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM0_PLANNED_QTY=' + URLClientEncode(getValue_('ITEM0_PLANNED_QTY',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'SALES_PRICE',i,'Sales Price/Unit') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM0_PRICE_AMOUNT',i,0);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");
        //Validation for Discount

        appendDirtyJavaScript("function validateDiscount(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkDiscount(i) ) return;\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=DISCOUNT'\n");
        appendDirtyJavaScript("		+ '&ITEM0_CATALOG_NO=' + URLClientEncode(getValue_('ITEM0_CATALOG_NO',i))\n");
        appendDirtyJavaScript("		+ '&NSALESPARTLIST_PRICE=' + URLClientEncode(getValue_('NSALESPARTLIST_PRICE',i))\n");
        appendDirtyJavaScript("		+ '&WORK_ORDER_INVOICE_TYPE=' + SelectURLClientEncode('WORK_ORDER_INVOICE_TYPE',i)\n");
        appendDirtyJavaScript("		+ '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
        appendDirtyJavaScript("		+ '&SALES_PRICE=' + URLClientEncode(getValue_('SALES_PRICE',i))\n");
        appendDirtyJavaScript("		+ '&DISCOUNT=' + URLClientEncode(getValue_('DISCOUNT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM0_PLANNED_QTY=' + URLClientEncode(getValue_('ITEM0_PLANNED_QTY',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'DISCOUNT',i,'Discount') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM0_PRICE_AMOUNT',i,0);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");
        //Validation for Qty To Invoice

        appendDirtyJavaScript("function validateQtyToInvoice(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkQtyToInvoice(i) ) return;\n");
        appendDirtyJavaScript("	window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=QTY_TO_INVOICE'\n");
        appendDirtyJavaScript("		+ '&ITEM0_CATALOG_NO=' + URLClientEncode(getValue_('ITEM0_CATALOG_NO',i))\n");
        appendDirtyJavaScript("		+ '&NSALESPARTLIST_PRICE=' + URLClientEncode(getValue_('NSALESPARTLIST_PRICE',i))\n");
        appendDirtyJavaScript("		+ '&WORK_ORDER_INVOICE_TYPE=' + SelectURLClientEncode('WORK_ORDER_INVOICE_TYPE',i)\n");
        appendDirtyJavaScript("		+ '&QTY_TO_INVOICE=' + URLClientEncode(getValue_('QTY_TO_INVOICE',i))\n");
        appendDirtyJavaScript("		+ '&SALES_PRICE=' + URLClientEncode(getValue_('SALES_PRICE',i))\n");
        appendDirtyJavaScript("		+ '&DISCOUNT=' + URLClientEncode(getValue_('DISCOUNT',i))\n");
        appendDirtyJavaScript("		+ '&ITEM0_PLANNED_QTY=' + URLClientEncode(getValue_('ITEM0_PLANNED_QTY',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'DISCOUNT',i,'Discount') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM0_PRICE_AMOUNT',i,0);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");

        /*appendDirtyJavaScript("function checkPartVal()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript(" if(f.ONCEGIVENERROR.value == \"TRUE\")");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("if(f.HIDDENPARTNO.value == f.ITEM0_PART_NO.value.toUpperCase())");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("        validateItem0PartNo(-1);\n");  
        appendDirtyJavaScript("}\n"); 
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("}\n");*/

        appendDirtyJavaScript("function validatePartOwnership(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
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
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWROUNDSTANDARDJOBINVOWNER1: Ownership type Consignment is not allowed in Materials for Standard Jobs."));
        appendDirtyJavaScript("'); \n");
        appendDirtyJavaScript("      f.PART_OWNERSHIP.value = ''; \n");
        appendDirtyJavaScript("      f.PART_OWNERSHIP_DB.value = ''; \n"); 
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("		if (f.PART_OWNERSHIP_DB.value == 'SUPPLIER LOANED'){\n");
        appendDirtyJavaScript("		alert('");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWROUNDSTANDARDJOBINVOWNER2: Ownership type Supplier Loaned is not allowed in Materials for Standard Jobs."));
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
        appendDirtyJavaScript("	  var enable_multichoice =(true && ITEM0_IN_FIND_MODE);\n");
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
        appendDirtyJavaScript("   if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
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
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWROUNDSTANDARDJOBINVOWNER11: Owner should not be specified for Company Owned Stock."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.OWNER.value = ''; \n");
        appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'CONSIGNMENT' && f.OWNER.value != '') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWROUNDSTANDARDJOBINVOWNER12: Owner should not be specified for Consignment Stock."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.OWNER.value = ''; \n");
        appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'SUPPLIER LOANED' && f.OWNER.value != '') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWROUNDSTANDARDJOBINVOWNER13: Owner should not be specified for Supplier Loaned Stock."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.OWNER.value = ''; \n");
        appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == '' && f.OWNER.value != '') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWROUNDSTANDARDJOBINVOWNER14: Owner should not be specified when there is no Ownership type."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.OWNER.value = ''; \n");
        appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("}\n");

        /*String outStr = out.toString();
        int beg_pos = outStr.indexOf("validateItem0PartNo(-1)");
        if (beg_pos > 0)
        {
            String begin_part = outStr.substring(0,beg_pos);
            String first_part = outStr.substring(beg_pos,beg_pos+24);
            String middle_part = "OnBlur=checkPartVal()";
            String last_part = outStr.substring(beg_pos+24);
            String outStrNew = begin_part + first_part + middle_part + last_part;
            out.clear();
            out.append(outStrNew);
        }*/

        //Web Alignment - simplify code for RMBs
        if (bOpenNewWindow)
        {
            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));  	//XSS_Safe AMNILK 20070725  
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=");
            appendDirtyJavaScript(newWinWidth);
            appendDirtyJavaScript(",height=");
            appendDirtyJavaScript(newWinHeight);
            appendDirtyJavaScript("\"); \n");      
        }
        //
    }
}
