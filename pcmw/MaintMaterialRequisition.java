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
* -----------	-----------------------------------------------------------------
*  File        :  MaintMaterialRequisition.java 
*  Created     :  ASP2JAVA Tool  010222
*  Modified    :  
*  BUNILK  010224  Corrected some conversion errors.
*  BUNILK  010307  Corrected some errors in state changing functions.
*  BUNILK  010426  Disabled edit,duplicate,new and delete buttons of material line
*                  block when the material state is "Closed";
*  CHDELK  010426  ade some modifications in calling the Unissue form (javascript)
*  INROLK  010430  Changed Sales Price to ReadOnly.
*  CHCRLK  010613  Modified overwritten validation.
*  BUNILK  010704  Modified all validations of itemblk0 according to new functionality.      
*  BUNILK  010716  Modified PART_NO field validation. 
*  BUNILK  010806  Made some changes in return AutoString of getContents() function so that to call       
*                  checkPartVal() javascript function from onBlur event of PART_NO field. Done some changes in 
*                  validatePartNo() method also. Modified sparePartObject() method and associated javascripts. 
*  CHCRLK  010809  Modified print method.
*  ARWILK  010904  Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction(). 
*                  (Caused when cancelNew is performed after saveNew)
*  BUNILK  011004  Added security check for Actions.
*  VAGULK  011011  Changed the fiels Workorder No to WO No(70532)
*  JEWILK  011017  Modified validations of fields 'PLAN_QTY','CATALOG_NO','PRICE_LIST_NO' to 
*                  correctly display 'Sales Price' and 'Discounted Price'.
*  SHAFLK  020508  Bug 29946,Added hyperlinks for the operations "Spare Parts in Object" and "Object Structure" under the header level of Material section. 
*  SHAFLK  020509  Bug 29943,Made changes to refresh in same page after RMB retrn. 
*  SHAFLK  020520  Bug 30188,Made changes in run() to refresh in same page after RMB retrn. 
*  SHAFLK  021108  Bug Id 34064,Changed method printPicList. 
*  SHAFLK  021204  Set Max Length of MCH_CODE to 100
*  BUNILK  021213  Merged with 2002-3 SP3
*  JEJALK  030430  Added Condition Code and ConditionCode Description.
*  JEJALK  030502  Added New action  - Issued Serials .
*  JEJALK  030503  Added new  action - Available to Reserve.
*  CHCRLK  030506  Modified actions Reserve Manually and Issue Manually.
*  JEJALK  030506  Modify the action Issued Serials.
*  ARWILK  031217  Edge Developments - (Replaced links with RMB's, Removed clone and doReset Methods)
*  ARWILK  031229  Edge Developments - (Replaced links with multirow RMB's)
*  ARWILK  040226  Edge Developments - (Enable Multirow RMB actions, Remove uneccessary global variables, Enhance Performance, Clean Javascript Code)
* ------------------------------ EDGE - SP1 Merge ---------------------------
*  SHAFLK  040120  Bug Id 41815,Removed Java dependencies. 
*  ARWILK  040324  Merged with SP1.
*  SHAFLK  040421  Bug Id 42866, Modified method issue().
*  ThWilk  040818  Merged Bug Id 42866.
*  NIJALK  040824  Call 116946, Modified predefine(),validate(),getContents() to get default condition code for condition allowed part.
*  ThWilk  040824  Call 117106, Modified availtoreserve().
*  NIJALK  040826  Call 117104, Modified manIssue().
*  SHAFLK  040722  Bug 43249, Modified validations of PART_NO, CONDITION_CODE and added fields and validations for PART_OWNERSHIP and OWNER and added some more fields to predefine() and modified HTML part. 
*  NIJALK  040902  Merged bug 43249.
*  ARWILK  040910  Modified availtoreserve.
*  NIJALK  040916  Added parameters project_id, activity_seq to method call to MAINT_MATERIAL_REQ_LINE_API.MakeIssueDetail(). Modified preDefine().
*  NIJALK  040929  Added parameter project_id to method calls to Inventory_Part_In_Stock_API.Get_Inventory_Quantity.
*  ARWILK  041005  LCS Merge: 46434.
*  BUNILK  041006  Field supply code added added to material line block and Changed server function of QTYONHAND field and modified validations of Part No, Condition Code, Ownership and owner fields of Material tab.. 
*  SHAFLK  040906  Bug 46542, Modified method matReqUnissue().
*  NIJALK  041007  Merged 46542. 
*  SHAFLK  040812  Bug 45904, Modified method issue().
*  NIJALK  041007  Merged 45904.
*  NIJALK  041025  Modified Issue().
*  NIJALK  041025  Added Job Id to Material Requisition Line,                  
*  NIJALK  041025  Modified Predefine(), Validate().
*  BUNILK  041026  Modified validations of material tab, added new validation to supply code.
*  NIJALK  041028  Modified preDefine(), sparePartObject().
*  SHAFLK  040916  Bug 46621, Modified validations of PART_NO, CONDITION_CODE, PART_OWNERSHIP and OWNER. 
*  Chanlk  041104  Merged Bug 46621.
*  NAMELK  041108  Duplicated Translation Tags Corrected.
*  ARWILK  041111  Replaced getContents with printContents.
*  NIJALK  041202  Added new parameters to method calls to Inventory_Part_In_Stock_API.Get_Inventory_Quantity.
*  NAMELK  050117  MCH_CONTRACT, WO_CONTRACT fields added.
*  NIJALK  050203  Modified sparePartObject(),objStructure(), detchedPartList(), run(),validate(),
*                  newRowITEM0() and preDefine().
*  NAMELK  050224  Merged Bug 48035 manually.
*  NIJALK  050302  Modified Lov for Requisition Site.
*  NIJALK  050405  Call 123081: Modified manReserve().
*  NEKOLK  050407  Merged - Bug 48852, Modified issue() and reserve().
*  NIJALK  050407  Call 123086: Modified manReserve(), reserve().
*  NIJALK  050520  Modified availDetail(), preDefine().
*  SHAFLK  050330  Bug 50258, Modified issue() and manIssue().
*  NIJALK  050527  Merged bug 50258.
*  DiAmlk  050613  Bug ID:124832 - Renamed the RMB Available to Reserve to Inventory Part in Stock... and
*                  modified the method availtoreserve.
*  SHAFLK  050425  Bug 50830, Used mgr.translateJavaScript() method to get the correctly translated strings in JavaScript functions.
*  NIJALK  050617  Merged bug 50830. 
*  NIJALK  050805  Bug 126176, Modified issue().
*  NIJALK  050808  Bug 126179: Modified matReqUnissue().
*  NIJALK  050811  Bug 126137, Modified availtoreserve().
*  NIJALK  050824  Bug 126179, Added method saveReturn().
*  ERALLK  051214  Bug 54280. Added column 'Quantity Available'.Modified the validate() function.
*  NIJALK  051227  Merged bug 54280.
*  THWILK  060126  Corrected localization errors.
*  NIJALK  060210  Call 133546: Renamed "STANDARD_INVENTORY" with "INVENT_ORDER".
*  SULILK  060304  Call 134906: Modified manReserve(), GetInventoryQuantity(), manIssue().
*  NEKOLK  060308  Call 135828: Added refreshForm() and made changes in predefine().
*  ASSALK  060316  Material Issue & Reserve modification. Issue and reserve made available after
*                    unissue all materials.
*  SULILK  060322  Modified preDefine(),validate(),run(),setValuesInMaterials() nad added saveReturnITEM0().
* ----------------------------------------------------------------------------
*  NIJALK  060509  Bug 57099, Modified matReqUnissue().
*  NIJALK  060510  Bug 57256, Modified manIssue(), GetInventoryQuantity().
*  NIJALK  060515  Bug 56688, Modified run(),issue(),adjust().
*  SHAFLK  060516  Bug 57826, Modified okFindITEM0()
*  SHAFLK  060525  Bug 57598, Modified validation for PART_NO and setValuesInMaterials().
*  AMNILK  060629  Merged with SP1 APP7.
*  AMDILK  060728  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding
*  AMNILK  060807  Merged Bug ID: 58214.
*  DIAMLK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMDILK  060905  Merged with the Bug Id 58216
*  SHAFLK  070116  Bug 62854, Modified validation for Part No.
*  AMNILK  070208  Merged LCS Bug 62854.   
*  SHAFLK  061120  Bug 61466, Added 'Supplier Loaned' stock to Materials.
*  ILSOLK  070302  Merged Bug Id 61466. 
*  SHAFLK  070228  Bug 63812, Modified printPicList.
*  ILSOLK  070410  Merged Bug Id 63812.
*  ASSALK  070716  Webification changes. Added WO Status, Operation No fields.
*  AMNILK  070727  Eliminated XSS Security Vulnerability.
*  ILSOLK  070730  Eliminated LocalizationErrors.
*  AMDILK  070731  Removed the scroll buttons of the parent when the detail block is in new or edit modes
*  ASSALK  070910  CALL 148510, Modified preDefine(), issue().
*  NIJALK  080202  Bug 66456, Modified GetInventoryQuantity(), validate(), preDefine() and printContents().
*  SHAFLK  080131  Bug 70815, Modified run(), issue(), reserve(), preDefine(). 
*  SHAFLK  081121  Bug 78187, Modified issue().
*  CHANLK  090225  Bug 76767, Modified preDefine(), validate(), issue(), reserve().
*  SHAFLK  090630  Bug 82543  Modified detchedPartList(). 
*  NIJALK  090704  Bug 82543, Modified run() and printContents(). Added refresh().
*  SHAFLK  090921  Bug 85901, Modified preDefine().
*  SHAFLK  100920  Bug 93011, Modified perform().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class MaintMaterialRequisition extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.MaintMaterialRequisition");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPHTMLFormatter fmt;
    private ASPContext ctx;

    private ASPBlock prntblk;
    private ASPRowSet printset;

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

    private ASPBlock eventblk;
    private ASPRowSet eventset;

    private ASPField f;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private String openCreRepNonSer;
    private String creRepNonSerPath;
    private ASPTransactionBuffer trans;
    private boolean satis;
    private String qrystr;
    private boolean canPerform;
    private String nQtyLeft;
    private ASPBuffer buff;
    private ASPCommand cmd;
    private ASPBuffer data;
    private ASPQuery q;
    private ASPBuffer retBuffer;
    private String ret_wo_no;
    private ASPBuffer row;
    private ASPBuffer r;
    private ASPBuffer key;
    private ASPBuffer buffer;
    private String txt;
    private boolean actEna2;
    private boolean actEna3;
    private boolean actEna4;
    private boolean actEna5;
    private boolean actEna6;
    private boolean actEna7;
    private boolean actEna8;
    private boolean actEna9;
    private boolean actEna10;
    private boolean actEna11;
    private boolean actEna12;
    private boolean actEna13;
    private boolean actEna14;
    private boolean actEna15;
    private boolean again;
    // 031217  ARWILK  Begin  (Links with RMB's)
    private boolean bOpenNewWindow;
    private String urlString;
    private String newWinHandle;
    // 031217  ARWILK  End  (Links with RMB's)
    private String isSecure[];
    private ASPTransactionBuffer secBuff;

    //===============================================================
    // Construction 
    //===============================================================
    public MaintMaterialRequisition(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        openCreRepNonSer = creRepNonSerPath;
        creRepNonSerPath = "";
        ASPManager mgr = getASPManager();

        fmt = mgr.newASPHTMLFormatter();
        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();

        satis = ctx.readFlag("SATIS", true);
        qrystr = ctx.readValue("QRYSTR", "");
        canPerform = ctx.readFlag("CANPRF", false);
        nQtyLeft = ctx.readValue("NQTYLEFT", "");
        actEna2 = ctx.readFlag("ACTENA2", false);
        actEna3 = ctx.readFlag("ACTENA3", false);
        actEna4 = ctx.readFlag("ACTENA4", false);
        actEna5 = ctx.readFlag("ACTENA5", false);
        actEna6 = ctx.readFlag("ACTENA6", false);
        actEna7 = ctx.readFlag("ACTENA7", false);
        actEna8 = ctx.readFlag("ACTENA8", false);
        actEna9 = ctx.readFlag("ACTENA9", false);
        actEna10 = ctx.readFlag("ACTENA10", false); 
        actEna11 = ctx.readFlag("ACTENA11", false);
        actEna12 = ctx.readFlag("ACTENA12", false);
        actEna13 = ctx.readFlag("ACTENA13", false);
        actEna14 = ctx.readFlag("ACTENA14", false);
        actEna15 = ctx.readFlag("ACTENA15", false);
        again = ctx.readFlag("AGAIN", false);

        if (mgr.commandBarActivated())
        {
            eval(mgr.commandBarFunction());
            if ("ITEM0.SaveReturn".equals(mgr.readValue("__COMMAND")))
                    {
                       headset.refreshRow();
                       setValuesInMaterials();
                    }
        }
        else if (mgr.dataTransfered())
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("NOTETEXTENT")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();   

            if (headset.countRows() > 0)
            {
                buff = headset.getRow();
                buff.setValue("SNOTETEXT", mgr.readValue("NOTETEXTENT",""));
                headset.setRow(buff);
            }
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("OKSPAREPART")) || !mgr.isEmpty(mgr.getQueryStringValue("SHOWMAT")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();   

            if (headset.countRows() != 1)
            {
                String s_sel_wo = ctx.findGlobal("CURRROWGLOBAL");  
                int sel_wo = Integer.parseInt(s_sel_wo);
                headset.goTo(sel_wo);      

                okFindITEM0();
            }
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("OKMANRESERVE")) && !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();

            String curRow = ctx.getGlobal("CURRROW");
            int curRowInt = Integer.parseInt(curRow);

            headset.goTo(curRowInt);

            if (headset.countRows() != 1)
                okFindITEM0();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("OKMANUNRESERVE")) && !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();   

            String curRow = ctx.getGlobal("CURRROW");
            int curRowInt = Integer.parseInt(curRow);

            headset.goTo(curRowInt);

            if (headset.countRows() != 1)
                okFindITEM0();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("OKUNISSUE")) && !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();   

            if (headset.countRows() != 1)
            {
                int curr_head = Integer.parseInt(ctx.getGlobal("HEADCURR"));
                headset.goTo(curr_head); 

                okFindITEM0();
            }
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("OKMANISSUE")) && !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();   

            String curRow = ctx.getGlobal("CURRROW");
            int curRowInt = Integer.parseInt(curRow);

            headset.goTo(curRowInt);

            if (headset.countRows() != 1)
                okFindITEM0();

            if ("TRUE".equals(mgr.readValue("CREREPWO")))
                openCreWOforNonSerial();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            okFind();   
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();
	//Bug 82543, Start
	else if ( !mgr.isEmpty(mgr.readValue("REFRESH_FLAG")))
	{
	    refresh();
	    refreshForm();
	}
	//Bug 82543, End

        adjust();

        ctx.writeFlag("SATIS", satis);
        ctx.writeValue("QRYSTR", qrystr);
        ctx.writeFlag("ACTENA2", actEna2);
        ctx.writeFlag("ACTENA3", actEna3);
        ctx.writeFlag("ACTENA4", actEna4);
        ctx.writeFlag("ACTENA5", actEna5);
        ctx.writeFlag("ACTENA6", actEna6);
        ctx.writeFlag("ACTENA7", actEna7);
        ctx.writeFlag("ACTENA8", actEna8);
        ctx.writeFlag("ACTENA9", actEna9);
        ctx.writeFlag("ACTENA10", actEna10);
        ctx.writeFlag("ACTENA11", actEna11);
        ctx.writeFlag("ACTENA12", actEna12);
        ctx.writeFlag("ACTENA13", actEna13);
        ctx.writeFlag("ACTENA14", actEna14);
        ctx.writeFlag("ACTENA15", actEna15);
        ctx.writeFlag("AGAIN", again);
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();

        String val = mgr.readValue("VALIDATE");

        String sOnhand = "ONHAND";
        String sPicking = "PICKING";
        String sF = "F" ;
        String sPallet = "PALLET"  ;
        String sDeep = "DEEP" ;
        String sBuffer = "BUFFER" ;
        String sDelivery = "DELIVERY"  ;
        String sShipment = "SHIPMENT"  ;
        String sManufacturing = "MANUFACTURING" ;
        String sTrue = "TRUE";
        String sFalse = "FALSE";
        //Bug 76767, Start
        String sAvailable = "AVAILABLE";
        String sNettable = "NETTABLE";
        //Bug 76767, End

        cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
        cmd.addParameter("PART_OWNERSHIP",mgr.readValue("PART_OWNERSHIP"));
        trans = mgr.validate(trans);

        String sClientOwnershipDefault = trans.getValue("VALIDOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");
        trans.clear();
        String sClientOwnershipConsignment = "CONSIGNMENT"  ;


        if ("WO_NO".equals(val))
        {
            cmd = trans.addCustomFunction("PLANSDATE","Active_Work_Order_API.Get_Plan_S_Date","DUE_DATE");
            cmd.addParameter("WO_NO");

            cmd = trans.addCustomFunction("PREACCID","Active_Work_Order_API.Get_Pre_Accounting_Id","NPREACCOUNTINGID");
            cmd.addParameter("WO_NO");

            cmd = trans.addCustomFunction("CONTRA","WORK_ORDER_API.Get_Contract","CONTRACT");
            cmd.addParameter("WO_NO");

            cmd = trans.addCustomFunction("COMP","Site_API.Get_Company","COMPANY");
            cmd.addReference("CONTRACT","CONTRA/DATA");

            cmd = trans.addCustomFunction("OBJID","WORK_ORDER_API.Get_Mch_Code","MCH_CODE");
            cmd.addParameter("WO_NO");

            cmd = trans.addCustomFunction("GETMCHCONT","WORK_ORDER_API.Get_Mch_Code_Contract","MCH_CONTRACT");
            cmd.addParameter("WO_NO");

            cmd = trans.addCustomFunction("MCHDESC","Maintenance_Object_API.Get_Mch_Name","DESCRIPTION");
            cmd.addReference("CONTRACT","CONTRA/DATA");
            cmd.addReference("MCH_CODE","OBJID/DATA");

            cmd = trans.addQuery("SUYSTDATE","SELECT SYSDATE SYS_DATE FROM DUAL");

            trans = mgr.validate(trans);

            String dueDate = trans.getBuffer("PLANSDATE/DATA").getFieldValue("DUE_DATE");
            String nPreAccountingId = trans.getBuffer("PREACCID/DATA").getFieldValue("NPREACCOUNTINGID");
            String contract = trans.getValue("CONTRA/DATA/CONTRACT");
            String company = trans.getValue("COMP/DATA/COMPANY");
            String mchCode = trans.getValue("OBJID/DATA/MCH_CODE");
            String description = trans.getValue("MCHDESC/DATA/DESCRIPTION");
            String sysDate = trans.getBuffer("SUYSTDATE/DATA").getFieldValue("SYS_DATE");
            String mchContract = trans.getValue("GETMCHCONT/DATA/MCH_CONTRACT");

            txt = (mgr.isEmpty(dueDate) ? (sysDate) : (dueDate))+ "^" + (mgr.isEmpty(nPreAccountingId) ? "" : (nPreAccountingId))+ "^" + (mgr.isEmpty(contract) ? "" : (contract))+ "^" + (mgr.isEmpty(company) ? "" : (company))+ "^" + (mgr.isEmpty(mchCode) ? "" : (mchCode))+ "^" + (mgr.isEmpty(description) ? "" : (description))+ "^" +
                  (mgr.isEmpty(contract) ? "" : (contract))+ "^" +(mgr.isEmpty(mchContract) ? "" : (mchContract))+ "^";

            mgr.responseWrite(txt);
        }
        else if ("MCH_CODE".equals(val))
        {
            cmd = trans.addCustomFunction("MCHNAM","Maintenance_Object_API.Get_Mch_Name","DESCRIPTION");
            cmd.addParameter("CONTRACT");
            cmd.addParameter("MCH_CODE");

            trans = mgr.validate(trans);

            String descri = trans.getValue("MCHNAM/DATA/DESCRIPTION");

            txt = (mgr.isEmpty(descri) ? "" : (descri))+ "^";

            mgr.responseWrite(txt);
        }
        else if ("SIGNATURE".equals(val))
        {
            cmd = trans.addCustomFunction("SIGNID","Company_Emp_API.Get_Max_Employee_Id","SIGNATURE_ID");
            cmd.addParameter("COMPANY");
            cmd.addParameter("SIGNATURE");

            cmd = trans.addCustomFunction("SIGNNAME","EMPLOYEE_API.Get_Employee_Info","SIGNATURENAME");
            cmd.addParameter("COMPANY");
            cmd.addReference("SIGNATURE_ID","SIGNID/DATA");

            trans = mgr.validate(trans);

            String signId = trans.getValue("SIGNID/DATA/SIGNATURE_ID");
            String signName = trans.getValue("SIGNNAME/DATA/SIGNATURENAME");

            txt = (mgr.isEmpty(signId) ? "" : (signId))+ "^" + (mgr.isEmpty(signName) ? "" : (signName))+ "^";

            mgr.responseWrite(txt);
        }
        else if ("INT_DESTINATION_ID".equals(val))
        {
            cmd = trans.addCustomFunction("INTDESTIDDESC","Internal_Destination_API.Get_Description","INT_DESTINATION_DESC");
            cmd.addParameter("CONTRACT");
            cmd.addParameter("INT_DESTINATION_ID");

            trans = mgr.validate(trans);

            String intDestDesc = trans.getValue("INTDESTIDDESC/DATA/INT_DESTINATION_DESC");

            txt = (mgr.isEmpty(intDestDesc) ? "" : (intDestDesc))+ "^";

            mgr.responseWrite(txt);
        }
        else if ("PART_NO".equals(val))
        {
            String defCond = new String();
            String condesc = new String();
            String sDefCondiCode= "";
            String activeInd = "";
            String vendorNo = "";
            String custOwner = "";
            String partOwnership = "";
            String sOwner = mgr.readValue("OWNER");
            String ownership = "";

            cmd = trans.addCustomFunction("GETCONDCODEUSAGEDB","PART_CATALOG_API.Get_Condition_Code_Usage_Db","COND_CODE_USAGE");
            cmd.addParameter("PART_NO");

            cmd = trans.addCustomFunction("DEFCONDCODE","CONDITION_CODE_API.Get_Default_Condition_Code","CONDITION_CODE");
            cmd = trans.addCustomFunction("MAINTREQSUP","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","SUPPLY_CODE");

            trans = mgr.validate(trans);
            if ("ALLOW_COND_CODE".equals(trans.getValue("GETCONDCODEUSAGEDB/DATA/COND_CODE_USAGE")))
            {
                sDefCondiCode = trans.getValue("DEFCONDCODE/DATA/CONDITION_CODE");

            }
            String sStandardInv = trans.getValue("MAINTREQSUP/DATA/SUPPLY_CODE");
            trans.clear();

            cmd = trans.addCustomFunction("VENDNO","PURCHASE_PART_SUPPLIER_API.Get_Primary_Supplier_No","VENDOR_NO");
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");

            trans = mgr.validate(trans);

            vendorNo = trans.getValue("VENDNO/DATA/VENDOR_NO");
        
            trans.clear();
            cmd = trans.addCustomFunction("PARTOWNSHIP","Purchase_Part_Supplier_API.Get_Part_Ownership","PART_OWNERSHIP");
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");
            cmd.addParameter("VENDOR_NO",vendorNo);

            cmd = trans.addCustomFunction("CUSTNO","Supplier_API.Get_Customer_No","CUSTOMER_NO");
            cmd.addParameter("VENDOR_NO",vendorNo);

            cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
            cmd.addReference("PART_OWNERSHIP","PARTOWNSHIP/DATA");

            trans = mgr.validate(trans);

            ownership = trans.getValue("PARTOWNSHIP/DATA/PART_OWNERSHIP");
            partOwnership = trans.getValue("VALIDOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");
            custOwner   = trans.getValue("CUSTNO/DATA/CUSTOMER_NO");
            if ((!mgr.isEmpty(vendorNo)) && "CUSTOMER OWNED".equals(partOwnership)) {
                   sClientOwnershipDefault = partOwnership;
                   sOwner = custOwner;
            }
            trans.clear();

            cmd = trans.addCustomFunction("CATALOGNO","Sales_Part_API.Get_Catalog_No_For_Part_No","CATALOG_NO");
            cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
            cmd.addParameter("PART_NO");

            cmd = trans.addCustomFunction("CATADESC","Sales_Part_API.Get_Catalog_Desc","CATALOGDESC");
            cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
            cmd.addReference("CATALOG_NO","CATALOGNO/DATA");

            cmd = trans.addCustomFunction("GETACT","Sales_Part_API.Get_Activeind","ACTIVEIND");
            cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
            cmd.addReference("CATALOG_NO","CATALOGNO/DATA");

            cmd = trans.addCustomFunction("GETACTDB","Active_Sales_Part_API.Encode","ACTIVEIND_DB");
            cmd.addReference("ACTIVEIND","GETACT/DATA");

            cmd = trans.addCustomFunction("PARTDESC","INVENTORY_PART_API.Get_Description","SPAREDESCRIPTION");
            cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
            cmd.addParameter("PART_NO");

            cmd = trans.addCustomFunction("SPARESTRUCT","Equipment_Spare_Structure_API.Has_Spare_Structure_Boolean","HASSPARESTRUCTURE");
            cmd.addParameter("PART_NO");
            cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));

            cmd = trans.addCustomFunction("DIMQUAL","INVENTORY_PART_API.Get_Dim_Quality","DIMQTY");
            cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
            cmd.addParameter("PART_NO");

            cmd = trans.addCustomFunction("TYPEDESI","INVENTORY_PART_API.Get_Type_Designation","TYPEDESIGN");
            cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
            cmd.addParameter("PART_NO");

            if ("COMPANY OWNED".equals(sClientOwnershipDefault))
                sClientOwnershipConsignment="CONSIGNMENT";
            else
                sClientOwnershipConsignment=null;

            if (sStandardInv.equals(mgr.readValue("SUPPLY_CODE")))
            {
                //Qty OnHand
                cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sOnhand);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL");
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
                cmd.addParameter("OWNERSHIP_TYPE3");
                cmd.addParameter("OWNERSHIP_TYPE4");
                cmd.addParameter("OWNER",sOwner);
                cmd.addParameter("OWNER_VENDOR");
                cmd.addParameter("LOCATION_TYPE1",sPicking);
                cmd.addParameter("LOCATION_TYPE2",sF);
                cmd.addParameter("LOCATION_TYPE3",sPallet);
                cmd.addParameter("LOCATION_TYPE4",sDeep);
                cmd.addParameter("LOCATION_TYPE5",sBuffer);
                cmd.addParameter("LOCATION_TYPE6",sDelivery);
                cmd.addParameter("LOCATION_TYPE7",sShipment);
                cmd.addParameter("LOCATION_TYPE8",sManufacturing);
                cmd.addParameter("LOT_BATCH_NO");
                cmd.addParameter("SERIAL_NO");
                cmd.addParameter("ENG_CHG_LEVEL");
                cmd.addParameter("WAIV_DEV_REJ_NO");
                cmd.addParameter("INCLUDE_STANDARD",sTrue);
                cmd.addParameter("INCLUDE_PROJECT",sFalse);
                cmd.addParameter("ACTIVITY_SEQ","0");
                cmd.addParameter("PROJECT_ID","");
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                cmd.addParameter("CONDITION_CODE",sDefCondiCode);

                //Qty Available
                cmd = trans.addCustomFunction("INVAVL","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sAvailable);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL", sNettable);
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment); 
                cmd.addParameter("OWNERSHIP_TYPE3");
                cmd.addParameter("OWNERSHIP_TYPE4");
                cmd.addParameter("OWNER",sOwner);
                cmd.addParameter("OWNER_VENDOR");
                cmd.addParameter("LOCATION_TYPE1",sPicking);
                cmd.addParameter("LOCATION_TYPE2",sF);
                cmd.addParameter("LOCATION_TYPE3",sPallet);
                cmd.addParameter("LOCATION_TYPE4",sDeep);
                cmd.addParameter("LOCATION_TYPE5",sBuffer);
                cmd.addParameter("LOCATION_TYPE6",sDelivery);
                cmd.addParameter("LOCATION_TYPE7",sShipment);
                cmd.addParameter("LOCATION_TYPE8",sManufacturing);
                cmd.addParameter("LOT_BATCH_NO");
                cmd.addParameter("SERIAL_NO");
                cmd.addParameter("ENG_CHG_LEVEL");
                cmd.addParameter("WAIV_DEV_REJ_NO");
                cmd.addParameter("INCLUDE_STANDARD",sTrue);
                cmd.addParameter("INCLUDE_PROJECT",sFalse);
                cmd.addParameter("ACTIVITY_SEQ","0");
                cmd.addParameter("PROJECT_ID","");
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                cmd.addParameter("CONDITION_CODE",sDefCondiCode);
            }
            else
            {
                //Bug 66456, Start
                if (mgr.isModuleInstalled("PROJ"))
                {
                    cmd = trans.addCustomFunction("PROJID","Activity_API.Get_Project_Id","PROJECT_ID");
                    cmd.addParameter("ACTIVITY_SEQ",mgr.readValue("ACTIVITY_SEQ"));
                }
                //Bug 66456, End

                //Qty OnHand
                cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sOnhand);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL");
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
                cmd.addParameter("OWNERSHIP_TYPE3");
                cmd.addParameter("OWNERSHIP_TYPE4");
                cmd.addParameter("OWNER",sOwner);
                cmd.addParameter("OWNER_VENDOR");
                cmd.addParameter("LOCATION_TYPE1",sPicking);
                cmd.addParameter("LOCATION_TYPE2",sF);
                cmd.addParameter("LOCATION_TYPE3",sPallet);
                cmd.addParameter("LOCATION_TYPE4",sDeep);
                cmd.addParameter("LOCATION_TYPE5",sBuffer);
                cmd.addParameter("LOCATION_TYPE6",sDelivery);
                cmd.addParameter("LOCATION_TYPE7",sShipment);
                cmd.addParameter("LOCATION_TYPE8",sManufacturing);
                cmd.addParameter("LOT_BATCH_NO");
                cmd.addParameter("SERIAL_NO");
                cmd.addParameter("ENG_CHG_LEVEL");
                cmd.addParameter("WAIV_DEV_REJ_NO");
                cmd.addParameter("INCLUDE_STANDARD",sFalse);
                cmd.addParameter("INCLUDE_PROJECT",sTrue);
                cmd.addParameter("ACTIVITY_SEQ","0");
                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))
                    cmd.addReference("PROJECT_ID", "PROJID/DATA");
                else
                    cmd.addParameter("PROJECT_ID", "");
                //Bug 66456, End
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                cmd.addParameter("CONDITION_CODE",sDefCondiCode);

                //Qty Available
                cmd = trans.addCustomFunction("INVAVL","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sAvailable);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL", sNettable);
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment); 
                cmd.addParameter("OWNERSHIP_TYPE3");
                cmd.addParameter("OWNERSHIP_TYPE4");
                cmd.addParameter("OWNER",sOwner);
                cmd.addParameter("OWNER_VENDOR");
                cmd.addParameter("LOCATION_TYPE1",sPicking);
                cmd.addParameter("LOCATION_TYPE2",sF);
                cmd.addParameter("LOCATION_TYPE3",sPallet);
                cmd.addParameter("LOCATION_TYPE4",sDeep);
                cmd.addParameter("LOCATION_TYPE5",sBuffer);
                cmd.addParameter("LOCATION_TYPE6",sDelivery);
                cmd.addParameter("LOCATION_TYPE7",sShipment);
                cmd.addParameter("LOCATION_TYPE8",sManufacturing);
                cmd.addParameter("LOT_BATCH_NO");
                cmd.addParameter("SERIAL_NO");
                cmd.addParameter("ENG_CHG_LEVEL");
                cmd.addParameter("WAIV_DEV_REJ_NO");
                cmd.addParameter("INCLUDE_STANDARD",sFalse);
                cmd.addParameter("INCLUDE_PROJECT",sTrue);
                cmd.addParameter("ACTIVITY_SEQ","0");
                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))
                    cmd.addReference("PROJECT_ID", "PROJID/DATA");
                else
                    cmd.addParameter("PROJECT_ID", "");
                //Bug 66456, End
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                cmd.addParameter("CONDITION_CODE",sDefCondiCode);
            }

            cmd = trans.addCustomFunction("UNITMES","Purchase_Part_Supplier_API.Get_Unit_Meas","UNITMEAS");
            cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
            cmd.addParameter("PART_NO");

            cmd = trans.addCustomFunction("GETSALESPRICEGRPID","SALES_PART_API.GET_SALES_PRICE_GROUP_ID","SALES_PRICE_GROUP_ID");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addReference("CATALOG_NO","CATANO/DATA");

            cmd = trans.addCustomFunction("SUUPCODE","INVENTORY_PART_API.Get_Supply_Code ","SUPPLY_CODE");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addReference("CATALOG_NO","CATANO/DATA");

            cmd = trans.addCustomFunction("CONDALLOW","PART_CATALOG_API.Get_Condition_Code_Usage_Db","COND_CODE_USAGE");
            cmd.addParameter("PART_NO");

            trans = mgr.validate(trans);

            String cataNo = trans.getValue("CATALOGNO/DATA/CATALOG_NO");
            String cataDesc = trans.getValue("CATADESC/DATA/CATALOGDESC");
            activeInd = trans.getValue("GETACTDB/DATA/ACTIVEIND_DB");
            String hasStruct = trans.getValue("SPARESTRUCT/DATA/HASSPARESTRUCTURE");
            String dimQty = trans.getValue("DIMQUAL/DATA/DIMQTY");
            String typeDesi = trans.getValue("TYPEDESI/DATA/TYPEDESIGN");
            double qtyOnHand = trans.getNumberValue("INVONHAND/DATA/QTYONHAND");
            String qtyOnHand1 = mgr.getASPField("QTYONHAND").formatNumber(qtyOnHand);
            String unitMeas = trans.getValue("UNITMES/DATA/UNITMEAS");
            String partDesc = trans.getValue("PARTDESC/DATA/SPAREDESCRIPTION");
            String salesPriceGroupId = trans.getValue("GETSALESPRICEGRPID/DATA/SALES_PRICE_GROUP_ID");
            String suppCode = trans.getValue("SUUPCODE/DATA/SUPPLY_CODE");
            String condco = trans.getValue("CONDALLOW/DATA/COND_CODE_USAGE");
            double qtyAvail = trans.getNumberValue("INVAVL/DATA/QTY_AVAILABLE");
            String qtyAvail1 = mgr.getASPField("QTY_AVAILABLE").formatNumber(qtyAvail);

            trans.clear();

            cmd = trans.addCustomFunction("WOSITE","Work_Order_API.Get_Contract","WO_SITE");
            cmd.addParameter("ITEM0_WO_NO",mgr.readValue("ITEM0_WO_NO"));

            cmd = trans.addCustomFunction("CATEXT","Sales_Part_API.Check_Exist","CAT_EXIST");
            cmd.addReference("WO_SITE","WOSITE/DATA");
            cmd.addParameter("CATALOG_NO",cataNo);

            trans = mgr.validate(trans);

            double nExist = trans.getNumberValue("CATEXT/DATA/CAT_EXIST");

            if (nExist != 1)
            {
                cataNo = "";
                cataDesc = "";
                salesPriceGroupId = "";
                suppCode = "";
            }

            trans.clear();

            if ("ALLOW_COND_CODE".equals(condco))
            {
                cmd = trans.addCustomFunction("CONDCO","CONDITION_CODE_API.Get_Default_Condition_Code","DEF_COND_CODE");
                cmd = trans.addCustomFunction("CONCODE","CONDITION_CODE_API.Get_Description","CONDDESC");
                cmd.addReference("DEF_COND_CODE","CONDCO/DATA");
                trans = mgr.validate(trans);
                defCond = trans.getValue("CONDCO/DATA/DEF_COND_CODE");
                condesc = trans.getValue("CONCODE/DATA/CONDDESC");
                trans.clear();
            }

            cmd = trans.addCustomCommand("MATREQLINE","Material_Requis_Line_API.CHECK_PART_NO__");
            cmd.addParameter("SPAREDESCRIPTION",partDesc);
            cmd.addParameter("SUPPLY_CODE",suppCode);
            cmd.addParameter("UNITMEAS",unitMeas);
            cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
            cmd.addParameter("SPARE_CONTRACT",mgr.readValue("SPARE_CONTRACT"));

            mgr.perform(trans); 

            txt = (mgr.isEmpty(cataNo) ? "" : (cataNo))+ "^" +
                  (mgr.isEmpty(cataDesc) ? "" : (cataDesc))+ "^" +
                  (mgr.isEmpty(hasStruct) ? "": hasStruct) + "^" + 
                  (mgr.isEmpty(dimQty) ? "": dimQty) + "^" +
                  (mgr.isEmpty(typeDesi) ? "": typeDesi) + "^" + 
                  (mgr.isEmpty(qtyOnHand1) ? "": qtyOnHand1) + "^" + 
                  (mgr.isEmpty(unitMeas) ? "": unitMeas) + "^" + 
                  (mgr.isEmpty(partDesc) ? "": partDesc) + "^" + 
                  (mgr.isEmpty(salesPriceGroupId) ? "" : salesPriceGroupId) + "^"+
                  (mgr.isEmpty(defCond) ? "" : defCond) +"^"+
                  (mgr.isEmpty(condesc) ? "" : condesc) +"^"+ 
                  (mgr.isEmpty(qtyAvail1) ? "": qtyAvail1) + "^" +
                  (mgr.isEmpty(activeInd) ? "": activeInd) + "^" +
                  (mgr.isEmpty(ownership) ? "": ownership) + "^" +
                  (mgr.isEmpty(sClientOwnershipDefault) ? "": sClientOwnershipDefault) + "^" +
                  (mgr.isEmpty(sOwner) ? "": sOwner) + "^" ;
            mgr.responseWrite(txt); 
        }

        else if ("SUPPLY_CODE".equals(val))
        {
            String qtyOnHand = "";
            sClientOwnershipDefault = mgr.readValue("PART_OWNERSHIP_DB");
            if (mgr.isEmpty(sClientOwnershipDefault))
                sClientOwnershipDefault = "COMPANY OWNED";

            cmd = trans.addCustomFunction("MAINTREQSUP","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","SUPPLY_CODE");
            trans = mgr.validate(trans);
            String sStandardInv = trans.getValue("MAINTREQSUP/DATA/SUPPLY_CODE");
            trans.clear();

            if ("COMPANY OWNED".equals(sClientOwnershipDefault))
                sClientOwnershipConsignment="CONSIGNMENT";
            else
                sClientOwnershipConsignment=null;

            if (sStandardInv.equals(mgr.readValue("SUPPLY_CODE")))
            {
                //Qty OnHand
                cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sOnhand);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL");
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
                cmd.addParameter("OWNERSHIP_TYPE3");
                cmd.addParameter("OWNERSHIP_TYPE4");
                cmd.addParameter("OWNER");
                cmd.addParameter("OWNER_VENDOR");
                cmd.addParameter("LOCATION_TYPE1",sPicking);
                cmd.addParameter("LOCATION_TYPE2",sF);
                cmd.addParameter("LOCATION_TYPE3",sPallet);
                cmd.addParameter("LOCATION_TYPE4",sDeep);
                cmd.addParameter("LOCATION_TYPE5",sBuffer);
                cmd.addParameter("LOCATION_TYPE6",sDelivery);
                cmd.addParameter("LOCATION_TYPE7",sShipment);
                cmd.addParameter("LOCATION_TYPE8",sManufacturing);
                cmd.addParameter("LOT_BATCH_NO");
                cmd.addParameter("SERIAL_NO");
                cmd.addParameter("ENG_CHG_LEVEL");
                cmd.addParameter("WAIV_DEV_REJ_NO");
                cmd.addParameter("INCLUDE_STANDARD",sTrue);
                cmd.addParameter("INCLUDE_PROJECT",sFalse);
                cmd.addParameter("ACTIVITY_SEQ","0");
                cmd.addParameter("PROJECT_ID","");
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                cmd.addParameter("CONDITION_CODE");

                //Qty Available
                cmd = trans.addCustomFunction("INVAVL","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sAvailable);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL", sNettable);
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
                cmd.addParameter("OWNERSHIP_TYPE3");
                cmd.addParameter("OWNERSHIP_TYPE4");
                cmd.addParameter("OWNER");
                cmd.addParameter("OWNER_VENDOR");
                cmd.addParameter("LOCATION_TYPE1",sPicking);
                cmd.addParameter("LOCATION_TYPE2",sF);
                cmd.addParameter("LOCATION_TYPE3",sPallet);
                cmd.addParameter("LOCATION_TYPE4",sDeep);
                cmd.addParameter("LOCATION_TYPE5",sBuffer);
                cmd.addParameter("LOCATION_TYPE6",sDelivery);
                cmd.addParameter("LOCATION_TYPE7",sShipment);
                cmd.addParameter("LOCATION_TYPE8",sManufacturing);
                cmd.addParameter("LOT_BATCH_NO");
                cmd.addParameter("SERIAL_NO");
                cmd.addParameter("ENG_CHG_LEVEL");
                cmd.addParameter("WAIV_DEV_REJ_NO");
                cmd.addParameter("INCLUDE_STANDARD",sTrue);
                cmd.addParameter("INCLUDE_PROJECT",sFalse);
                cmd.addParameter("ACTIVITY_SEQ","0");
                cmd.addParameter("PROJECT_ID","");
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                cmd.addParameter("CONDITION_CODE");
            }
            else
            {
                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))
                {
                    cmd = trans.addCustomFunction("PROJID","Activity_API.Get_Project_Id","PROJECT_ID");
                    cmd.addParameter("ACTIVITY_SEQ",mgr.readValue("ACTIVITY_SEQ"));
                }
                //Bug 66456, End

                //Qty OnHand
                cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sOnhand);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL");
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
                cmd.addParameter("OWNERSHIP_TYPE3");
                cmd.addParameter("OWNERSHIP_TYPE4");
                cmd.addParameter("OWNER");
                cmd.addParameter("OWNER_VENDOR");
                cmd.addParameter("LOCATION_TYPE1",sPicking);
                cmd.addParameter("LOCATION_TYPE2",sF);
                cmd.addParameter("LOCATION_TYPE3",sPallet);
                cmd.addParameter("LOCATION_TYPE4",sDeep);
                cmd.addParameter("LOCATION_TYPE5",sBuffer);
                cmd.addParameter("LOCATION_TYPE6",sDelivery);
                cmd.addParameter("LOCATION_TYPE7",sShipment);
                cmd.addParameter("LOCATION_TYPE8",sManufacturing);
                cmd.addParameter("LOT_BATCH_NO");
                cmd.addParameter("SERIAL_NO");
                cmd.addParameter("ENG_CHG_LEVEL");
                cmd.addParameter("WAIV_DEV_REJ_NO");
                cmd.addParameter("INCLUDE_STANDARD",sFalse);
                cmd.addParameter("INCLUDE_PROJECT",sTrue);
                cmd.addParameter("ACTIVITY_SEQ","0");
                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))
                    cmd.addReference("PROJECT_ID", "PROJID/DATA");
                else
                    cmd.addParameter("PROJECT_ID", "");
                //Bug 66456, End
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                cmd.addParameter("CONDITION_CODE");

                //Qty Available
                cmd = trans.addCustomFunction("INVAVL","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sAvailable);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL", sNettable);
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
                cmd.addParameter("OWNERSHIP_TYPE3");
                cmd.addParameter("OWNERSHIP_TYPE4");
                cmd.addParameter("OWNER");
                cmd.addParameter("OWNER_VENDOR");
                cmd.addParameter("LOCATION_TYPE1",sPicking);
                cmd.addParameter("LOCATION_TYPE2",sF);
                cmd.addParameter("LOCATION_TYPE3",sPallet);
                cmd.addParameter("LOCATION_TYPE4",sDeep);
                cmd.addParameter("LOCATION_TYPE5",sBuffer);
                cmd.addParameter("LOCATION_TYPE6",sDelivery);
                cmd.addParameter("LOCATION_TYPE7",sShipment);
                cmd.addParameter("LOCATION_TYPE8",sManufacturing);
                cmd.addParameter("LOT_BATCH_NO");
                cmd.addParameter("SERIAL_NO");
                cmd.addParameter("ENG_CHG_LEVEL");
                cmd.addParameter("WAIV_DEV_REJ_NO");
                cmd.addParameter("INCLUDE_STANDARD",sFalse);
                cmd.addParameter("INCLUDE_PROJECT",sTrue);
                cmd.addParameter("ACTIVITY_SEQ","0");
                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))
                    cmd.addReference("PROJECT_ID", "PROJID/DATA");
                else
                    cmd.addParameter("PROJECT_ID", "");
                //Bug 66456, End
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                cmd.addParameter("CONDITION_CODE");
            }    
            trans = mgr.validate(trans);
            qtyOnHand = trans.getBuffer("INVONHAND/DATA").getFieldValue("QTYONHAND");
            double qtyAvail = trans.getNumberValue("INVAVL/DATA/QTY_AVAILABLE"); 
            String qtyAvail1 = mgr.getASPField("QTY_AVAILABLE").formatNumber(qtyAvail);

            txt = (mgr.isEmpty(qtyOnHand) ? "": qtyOnHand) + "^" +
                  (mgr.isEmpty(qtyAvail1) ? "": qtyAvail1) + "^" ;

            mgr.responseWrite(txt);
        }
        else if ("PLAN_QTY".equals(val))
        {
            String spareId = mgr.readValue("PART_NO","");

            double nCost = 0;
            double nAmountCost = 0;

            double nDiscountedPrice = mgr.readNumberValue("ITEMDISCOUNTEDPRICE");
            if (isNaN(nDiscountedPrice))
                nDiscountedPrice = 0;

            double nSalesPriceAmount = 0;

            String nPriceListNo = mgr.readValue("PRICE_LIST_NO","");

            double nDiscount = mgr.readNumberValue("DISCOUNT");
            if (isNaN(nDiscount))
                nDiscount = 0;

            double nListPrice = mgr.readNumberValue("LIST_PRICE");
            if (isNaN(nListPrice))
                nListPrice = 0;
            trans.clear();
            cmd = trans.addCustomFunction("GETOBJSUPL","MAINT_MATERIAL_REQ_LINE_API.Is_Obj_Supp_Loaned","OBJ_LOAN");
            cmd.addParameter("ITEM0_WO_NO");
    
            trans = mgr.validate(trans);
    
            String sObjSup = trans.getValue("GETOBJSUPL/DATA/OBJ_LOAN");
    
            trans.clear();

            if (!mgr.isEmpty(spareId))
            {
                /*cmd = trans.addCustomFunction("GETINVVAL","Inventory_Part_API.Get_Inventory_Value_By_Method","COST");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("PART_NO");*/

                cmd = trans.addCustomCommand("GETINVVAL","Active_Separate_API.Get_Inventory_Value");
                cmd.addParameter("COST");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("PART_NO");
                cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
                cmd.addParameter("CONFIGURATION_IDS",mgr.isEmpty(mgr.readValue("CONFIGURATION_IDS"))?"*":mgr.readValue("CONFIGURATION_IDS"));
                cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));

                trans = mgr.validate(trans);

                nCost = trans.getNumberValue("GETINVVAL/DATA/COST");
                if (isNaN(nCost))
                    nCost = 0;
                if ("TRUE".equals(sObjSup) || "SUPPLIER LOANED".equals(mgr.readValue("PART_OWNERSHIP_DB"))) 
                    nCost = 0;
            }

            double planQty = mgr.readNumberValue("PLAN_QTY");
            if (isNaN(planQty))
                planQty = 0;

            if (planQty == 0)
                nAmountCost = 0;
            else
                nAmountCost = nCost * planQty;

            String cataNo = mgr.readValue("CATALOG_NO","");  

            if (!mgr.isEmpty(cataNo) && planQty != 0)
            {
                trans.clear();

                cmd = trans.addCustomFunction("AGREID","Active_Work_Order_API.Get_Agreement_Id","AGREEMENT_ID");
                cmd.addParameter("ITEM0_WO_NO");

                cmd = trans.addCustomFunction("CUSTONO","Active_Work_Order_API.Get_Customer_No","CUSTOMER_NO");
                cmd.addParameter("ITEM0_WO_NO");

                cmd = trans.addCustomCommand("PRICEINF","Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("BASE_PRICE","0");
                cmd.addParameter("SALE_PRICE","0");
                cmd.addParameter("DISCOUNT","0");
                cmd.addParameter("CURRENCY_RATE","0");
                cmd.addParameter("CATALOG_CONTRACT");
                cmd.addParameter("CATALOG_NO");
                cmd.addReference("CUSTOMER_NO","CUSTONO/DATA");
                cmd.addReference("AGREEMENT_ID","AGREID/DATA");
                cmd.addParameter("PRICE_LIST_NO");
                cmd.addParameter("PLAN_QTY");

                trans = mgr.validate(trans);

                if (mgr.isEmpty(nPriceListNo))
                    nPriceListNo = trans.getBuffer("PRICEINF/DATA").getFieldValue("PRICE_LIST_NO");

                nDiscount = trans.getNumberValue("PRICEINF/DATA/DISCOUNT");
                if (isNaN(nDiscount))
                    nDiscount = 0;

                nListPrice = trans.getNumberValue("PRICEINF/DATA/SALE_PRICE");
                if (isNaN(nListPrice))
                    nListPrice = 0;

                if (nListPrice == 0)
                {
                    nDiscountedPrice = nListPrice - (nDiscount / 100 * nListPrice);
                    nSalesPriceAmount = (nListPrice - (nDiscount / 100 * nListPrice)) * planQty;
                }
                else
                {
                    if (nDiscount == 0)
                    {
                        nSalesPriceAmount = nListPrice * planQty;
                        nDiscountedPrice = nListPrice;
                    }
                    else
                    {
                        nSalesPriceAmount = nListPrice * planQty; 
                        nSalesPriceAmount = nSalesPriceAmount - (nDiscount / 100 * nSalesPriceAmount);
                        nDiscountedPrice =  nListPrice - (nDiscount / 100 * nListPrice);

                    }
                }
            }

            String nCostStr = mgr.formatNumber("COST",nCost);
            String nAmountCostStr = mgr.formatNumber("AMOUNTCOST",nAmountCost);
            String nDiscountStr = mgr.formatNumber("DISCOUNT",nDiscount);
            String nListPriceStr = mgr.formatNumber("LIST_PRICE",nListPrice);
            String nSalesPriceAmountStr = mgr.formatNumber("SALESPRICEAMOUNT",nSalesPriceAmount);
            String nDiscountedPriceStr = mgr.formatNumber("ITEMDISCOUNTEDPRICE",nDiscountedPrice);

            txt = (mgr.isEmpty(nCostStr) ? "": nCostStr) + "^" + (mgr.isEmpty(nAmountCostStr) ? "": nAmountCostStr) + "^" + (mgr.isEmpty(nPriceListNo) ? "": nPriceListNo) + "^" + (mgr.isEmpty(nDiscountStr) ? "": nDiscountStr) + "^" + (mgr.isEmpty(nListPriceStr) ? "": nListPriceStr) + "^" + (mgr.isEmpty(nSalesPriceAmountStr) ? "": nSalesPriceAmountStr) + "^"+ (mgr.isEmpty(nDiscountedPriceStr) ? "": nDiscountedPriceStr) + "^";

            mgr.responseWrite(txt); 
        }
        else if ("CATALOG_NO".equals(val))
        {
            trans.clear();
            cmd = trans.addCustomFunction("GETOBJSUPL","MAINT_MATERIAL_REQ_LINE_API.Is_Obj_Supp_Loaned","OBJ_LOAN");
            cmd.addParameter("ITEM0_WO_NO");

            trans = mgr.validate(trans);

            String sObjSup = trans.getValue("GETOBJSUPL/DATA/OBJ_LOAN");

            trans.clear();

            cmd = trans.addCustomFunction("CURRCO","ACTIVE_SEPARATE_API.Get_Currency_Code","CURRENCEY_CODE");
            cmd.addParameter("ITEM0_WO_NO");

            cmd = trans.addCustomFunction("AGREID","Active_Work_Order_API.Get_Agreement_Id","AGREEMENT_ID");
            cmd.addParameter("ITEM0_WO_NO");

            cmd = trans.addCustomFunction("CUSTONO","Active_Work_Order_API.Get_Customer_No","CUSTOMER_NO");
            cmd.addParameter("ITEM0_WO_NO");

            cmd = trans.addCustomFunction("PRILST","Customer_Order_Pricing_Api.Get_Valid_Price_List","PRICE_LIST_NO");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addParameter("CATALOG_NO");
            cmd.addReference("CUSTOMER_NO","CUSTONO/DATA");
            cmd.addReference("CURRENCEY_CODE","CURRCO/DATA");

            cmd = trans.addCustomCommand("PRICEINF","Work_Order_Coding_API.Get_Price_Info");
            cmd.addParameter("BASE_PRICE","0"); 
            cmd.addParameter("SALE_PRICE","0");
            cmd.addParameter("DISCOUNT","0");
            cmd.addParameter("CURRENCY_RATE","0");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addParameter("CATALOG_NO");
            cmd.addReference("CUSTOMER_NO","CUSTONO/DATA");
            cmd.addReference("AGREEMENT_ID","AGREID/DATA");
            cmd.addReference("PRICE_LIST_NO","PRILST/DATA");
            cmd.addParameter("PLAN_QTY");

            cmd = trans.addCustomFunction("CATADESC","Sales_Part_API.Get_Catalog_Desc","CATALOGDESC");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addParameter("CATALOG_NO");

            cmd = trans.addCustomFunction("GETSALESPRICEGRPID","SALES_PART_API.GET_SALES_PRICE_GROUP_ID","SALES_PRICE_GROUP_ID");
            cmd.addParameter("CATALOG_CONTRACT");
            cmd.addParameter("CATALOG_NO");

            String spareId = mgr.readValue("PART_NO","");

            double nCost = mgr.readNumberValue("COST");
            if (isNaN(nCost))
                nCost = 0;

            if (!mgr.isEmpty(spareId))
            {
                /*cmd = trans.addCustomFunction("GETINVVAL"," Inventory_Part_API.Get_Inventory_Value_By_Method","COST");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("PART_NO"); */

                cmd = trans.addCustomCommand("GETINVVAL","Active_Separate_API.Get_Inventory_Value");
                cmd.addParameter("COST");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("PART_NO");
                cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
                cmd.addParameter("CONFIGURATION_IDS",mgr.isEmpty(mgr.readValue("CONFIGURATION_IDS"))?"*":mgr.readValue("CONFIGURATION_IDS"));
                cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));
            }

            trans = mgr.validate(trans);

            String nPriceListNo = trans.getValue("PRILST/DATA/PRICE_LIST_NO");

            if (mgr.isEmpty(nPriceListNo))
                nPriceListNo = trans.getValue("PRICEINF/DATA/PRICE_LIST_NO");

            double nDiscount = trans.getNumberValue("PRICEINF/DATA/DISCOUNT");
            if (isNaN(nDiscount))
                nDiscount = 0;

            String cataDesc = trans.getValue("CATADESC/DATA/CATALOGDESC");
            String salesPriceGroupId = trans.getValue("GETSALESPRICEGRPID/DATA/SALES_PRICE_GROUP_ID");

            double planQty = mgr.readNumberValue("PLAN_QTY");
            if (isNaN(planQty))
                planQty = 0;

            double nListPrice = 0;
            double nSalesPriceAmount = 0;
            double nDiscountedPriceAmt = 0;

            nListPrice = trans.getNumberValue("PRICEINF/DATA/SALE_PRICE");
            if (isNaN(nListPrice))
                nListPrice =0;

            if (nListPrice == 0)
            {
                nSalesPriceAmount  = (nListPrice - (nDiscount / 100 * nListPrice)) * planQty;
                nDiscountedPriceAmt = (nListPrice - (nDiscount / 100 * nListPrice));
            }
            else
            {
                if (nDiscount == 0)
                {
                    nSalesPriceAmount = nListPrice * planQty;
                    nDiscountedPriceAmt = nListPrice;
                }
                else
                {
                    nSalesPriceAmount = nListPrice * planQty;
                    nSalesPriceAmount = nSalesPriceAmount - (nDiscount / 100 * nSalesPriceAmount);
                    nDiscountedPriceAmt = (nListPrice - (nDiscount / 100 * nListPrice));
                }
            }

            if (!mgr.isEmpty(spareId))
            {
                nCost = trans.getNumberValue("GETINVVAL/DATA/COST");
                if (isNaN(nCost))
                    nCost = 0;
                if ("TRUE".equals(sObjSup) || "SUPPLIER LOANED".equals(mgr.readValue("PART_OWNERSHIP_DB"))) 
                    nCost = 0;
            }

            double nCostAmt = 0;

            if (planQty == 0)
                nCostAmt = 0;
            else
                nCostAmt    =  nCost *  planQty;

            String nListPriceStr = mgr.formatNumber("LIST_PRICE",nListPrice);
            String nCostStr = mgr.formatNumber("COST",nCost);
            String nCostAmtStr = mgr.formatNumber("AMOUNTCOST",nCostAmt);
            String nDiscountStr = mgr.formatNumber("DISCOUNT",nDiscount);
            String nSalesPriceAmountStr = mgr.formatNumber("SALESPRICEAMOUNT",nSalesPriceAmount);
            String nDiscountedPriceStr = mgr.formatNumber("ITEMDISCOUNTEDPRICE",nDiscountedPriceAmt);

            txt = (mgr.isEmpty(nListPriceStr) ? "": nListPriceStr) + "^" + (mgr.isEmpty(nCostStr) ? "": nCostStr) + "^" + (mgr.isEmpty(nCostAmtStr) ? "": nCostAmtStr) + "^" + (mgr.isEmpty(cataDesc) ? "": cataDesc) + "^" + (mgr.isEmpty(nDiscountStr) ? "": nDiscountStr) + "^"+ (mgr.isEmpty(nSalesPriceAmountStr) ? "": nSalesPriceAmountStr) + "^" + (mgr.isEmpty(nDiscountedPriceStr) ? "": nDiscountedPriceStr) + "^"+ (mgr.isEmpty(salesPriceGroupId)?"":salesPriceGroupId) + "^" + (mgr.isEmpty(nPriceListNo)?"":nPriceListNo) + "^";

            mgr.responseWrite(txt);
        }
        else if ("PRICE_LIST_NO".equals(val))
        {
            String partNo = mgr.readValue("PART_NO","");
            double nCostAmt = 0;
            double nCost = 0;
            double nListPrice = 0;
            double nSalesPriceAmount = 0;
            double nDiscountedPriceAmt = 0;
            double nDiscount = 0;
            String nPriceListNo = mgr.readValue("PRICE_LIST_NO","");

            if (!mgr.isEmpty(partNo))
            {
                /*cmd = trans.addCustomFunction("GETINVAL","Inventory_Part_API.Get_Inventory_Value_By_Method","COST");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("PART_NO");*/

                cmd = trans.addCustomCommand("GETINVAL","Active_Separate_API.Get_Inventory_Value");
                cmd.addParameter("COST");
                cmd.addParameter("SPARE_CONTRACT");
                cmd.addParameter("PART_NO");
                cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
                cmd.addParameter("CONFIGURATION_IDS",mgr.isEmpty(mgr.readValue("CONFIGURATION_IDS"))?"*":mgr.readValue("CONFIGURATION_IDS"));
                cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));

                trans = mgr.validate(trans);

                nCost = trans.getNumberValue("GETINVAL/DATA/COST");
                if (isNaN(nCost))
                    nCost = 0;
            }

            double planQty = mgr.readNumberValue("PLAN_QTY");
            if (isNaN(planQty))
                planQty = 0;

            if (planQty == 0)
                nCostAmt = 0;
            else
                nCostAmt    = nCost * planQty;

            String cataNo = mgr.readValue("CATALOG_NO",""); 

            if (!mgr.isEmpty(cataNo) && planQty != 0)
            {
                trans.clear();

                cmd = trans.addCustomFunction("AGREID","Active_Work_Order_API.Get_Agreement_Id","AGREEMENT_ID");
                cmd.addParameter("ITEM0_WO_NO");

                cmd = trans.addCustomFunction("CUSTONO","Active_Work_Order_API.Get_Customer_No","CUSTOMER_NO");
                cmd.addParameter("ITEM0_WO_NO");

                cmd = trans.addCustomCommand("PRICEINF","Work_Order_Coding_API.Get_Price_Info");
                cmd.addParameter("BASE_PRICE","0"); 
                cmd.addParameter("SALE_PRICE","0");
                cmd.addParameter("DISCOUNT","0");
                cmd.addParameter("CURRENCY_RATE","0");
                cmd.addParameter("CATALOG_CONTRACT");
                cmd.addParameter("CATALOG_NO");
                cmd.addReference("CUSTOMER_NO","CUSTONO/DATA");
                cmd.addReference("AGREEMENT_ID","AGREID/DATA");
                cmd.addParameter("PRICE_LIST_NO");
                cmd.addParameter("PLAN_QTY");

                trans = mgr.validate(trans);        

                if (mgr.isEmpty(nPriceListNo))
                    nPriceListNo = trans.getBuffer("PRICEINF/DATA").getFieldValue("PRICE_LIST_NO");

                nDiscount = trans.getNumberValue("PRICEINF/DATA/DISCOUNT");
                if (isNaN(nDiscount))
                    nDiscount = 0;

                nListPrice = trans.getNumberValue("PRICEINF/DATA/SALE_PRICE");
                if (isNaN(nListPrice))
                    nListPrice =0;

                if (nListPrice == 0)
                {
                    nSalesPriceAmount  = (nListPrice - (nDiscount / 100 * nListPrice)) * planQty;
                    nDiscountedPriceAmt = (nListPrice - (nDiscount / 100 * nListPrice));
                }
                else
                {
                    if (nDiscount == 0)
                    {
                        nSalesPriceAmount = nListPrice * planQty;
                        nDiscountedPriceAmt = nListPrice;
                    }
                    else
                    {
                        nSalesPriceAmount = nListPrice * planQty;
                        nSalesPriceAmount = nSalesPriceAmount - (nDiscount / 100 * nSalesPriceAmount);
                        nDiscountedPriceAmt = (nListPrice - (nDiscount / 100 * nListPrice));
                    }
                }
            }

            String nCostStr = mgr.formatNumber("COST",nCost);
            String nCostAmtStr = mgr.formatNumber("AMOUNTCOST",nCostAmt);
            String nListPriceStr = mgr.formatNumber("LIST_PRICE",nListPrice);
            String nDiscountStr = mgr.formatNumber("DISCOUNT",nDiscount);
            String nSalesPriceAmountStr = mgr.formatNumber("SALESPRICEAMOUNT",nSalesPriceAmount);
            String nDiscountedPriceAmtStr = mgr.formatNumber("ITEMDISCOUNTEDPRICE",nDiscountedPriceAmt);

            txt =  (mgr.isEmpty(nCostStr) ? "": nCostStr) + "^" + (mgr.isEmpty(nCostAmtStr) ? "": nCostAmtStr) + "^" + (mgr.isEmpty(nListPriceStr) ? "": nListPriceStr) + "^" + (mgr.isEmpty(nDiscountStr) ? "": nDiscountStr) + "^"+ (mgr.isEmpty(nSalesPriceAmountStr) ? "": nSalesPriceAmountStr) + "^"+ (mgr.isEmpty(nDiscountedPriceAmtStr) ? "": nDiscountedPriceAmtStr) + "^";

            mgr.responseWrite(txt);
        }
        else if ("DISCOUNT".equals(val))
        {
            double nListPrice = mgr.readNumberValue("LIST_PRICE");
            if (isNaN(nListPrice))
                nListPrice = 0;

            double planQty = mgr.readNumberValue("PLAN_QTY");
            if (isNaN(planQty))
                planQty = 0;

            double salePriceAmt = 0;

            double nDiscount = mgr.readNumberValue("DISCOUNT");
            if (isNaN(nDiscount))
                nDiscount = 0;

            double discountedPrice = 0;

            salePriceAmt =  nListPrice * planQty;
            salePriceAmt =  salePriceAmt - (nDiscount / 100 * salePriceAmt);
            discountedPrice = nListPrice - (nDiscount / 100 * nListPrice);       

            String salePriceAmtStr = mgr.getASPField("SALESPRICEAMOUNT").formatNumber(salePriceAmt);
            String discountedPriceStr = mgr.formatNumber("ITEMDISCOUNTEDPRICE",discountedPrice);

            txt = (mgr.isEmpty(salePriceAmtStr) ? "": salePriceAmtStr) + "^" + (mgr.isEmpty(discountedPriceStr) ? "": discountedPriceStr) + "^";

            mgr.responseWrite(txt);
        }
        else if ("CONDITION_CODE".equals(val))
        {
            String qtyOnHand1 = "";
            String qtyAvail1 = "";
            double qtyOnHand = 0;
            double qtyAvail = 0;

            double nCostAmt = 0;
            double nCost = 0;
            double planQty = 0;

            cmd = trans.addCustomFunction("MAINTREQSUP","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","SUPPLY_CODE");
            trans = mgr.validate(trans);
            String sStandardInv = trans.getValue("MAINTREQSUP/DATA/SUPPLY_CODE");
            trans.clear();

            if ("COMPANY OWNED".equals(sClientOwnershipDefault))
                sClientOwnershipConsignment="CONSIGNMENT";
            else
                sClientOwnershipConsignment=null;

            if (sStandardInv.equals(mgr.readValue("SUPPLY_CODE")))
            {
                //Qty OnHand
                cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sOnhand);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL");
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
                cmd.addParameter("OWNERSHIP_TYPE3");
                cmd.addParameter("OWNERSHIP_TYPE4");
                cmd.addParameter("OWNER");
                cmd.addParameter("OWNER_VENDOR");
                cmd.addParameter("LOCATION_TYPE1",sPicking);
                cmd.addParameter("LOCATION_TYPE2",sF);
                cmd.addParameter("LOCATION_TYPE3",sPallet);
                cmd.addParameter("LOCATION_TYPE4",sDeep);
                cmd.addParameter("LOCATION_TYPE5",sBuffer);
                cmd.addParameter("LOCATION_TYPE6",sDelivery);
                cmd.addParameter("LOCATION_TYPE7",sShipment);
                cmd.addParameter("LOCATION_TYPE8",sManufacturing);
                cmd.addParameter("LOT_BATCH_NO");
                cmd.addParameter("SERIAL_NO");
                cmd.addParameter("ENG_CHG_LEVEL");
                cmd.addParameter("WAIV_DEV_REJ_NO");
                cmd.addParameter("INCLUDE_STANDARD",sTrue);
                cmd.addParameter("INCLUDE_PROJECT",sFalse);
                cmd.addParameter("ACTIVITY_SEQ","0");
                cmd.addParameter("PROJECT_ID","");
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                cmd.addParameter("CONDITION_CODE");

                //Qty Available
                cmd = trans.addCustomFunction("INVAVL","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sAvailable);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL", sNettable);
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
                cmd.addParameter("OWNERSHIP_TYPE3");
                cmd.addParameter("OWNERSHIP_TYPE4");
                cmd.addParameter("OWNER");
                cmd.addParameter("OWNER_VENDOR");
                cmd.addParameter("LOCATION_TYPE1",sPicking);
                cmd.addParameter("LOCATION_TYPE2",sF);
                cmd.addParameter("LOCATION_TYPE3",sPallet);
                cmd.addParameter("LOCATION_TYPE4",sDeep);
                cmd.addParameter("LOCATION_TYPE5",sBuffer);
                cmd.addParameter("LOCATION_TYPE6",sDelivery);
                cmd.addParameter("LOCATION_TYPE7",sShipment);
                cmd.addParameter("LOCATION_TYPE8",sManufacturing);
                cmd.addParameter("LOT_BATCH_NO");
                cmd.addParameter("SERIAL_NO");
                cmd.addParameter("ENG_CHG_LEVEL");
                cmd.addParameter("WAIV_DEV_REJ_NO");
                cmd.addParameter("INCLUDE_STANDARD",sTrue);
                cmd.addParameter("INCLUDE_PROJECT",sFalse);
                cmd.addParameter("ACTIVITY_SEQ","0");
                cmd.addParameter("PROJECT_ID","");
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                cmd.addParameter("CONDITION_CODE");
            }
            else
            {
                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))
                {
                    cmd = trans.addCustomFunction("PROJID","Activity_API.Get_Project_Id","PROJECT_ID");
                    cmd.addParameter("ACTIVITY_SEQ",mgr.readValue("ACTIVITY_SEQ"));
                }
                //Bug 66456, End

                //Qty OnHand
                cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sOnhand);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL");
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
                cmd.addParameter("OWNERSHIP_TYPE3");
                cmd.addParameter("OWNERSHIP_TYPE4");
                cmd.addParameter("OWNER");
                cmd.addParameter("OWNER_VENDOR");
                cmd.addParameter("LOCATION_TYPE1",sPicking);
                cmd.addParameter("LOCATION_TYPE2",sF);
                cmd.addParameter("LOCATION_TYPE3",sPallet);
                cmd.addParameter("LOCATION_TYPE4",sDeep);
                cmd.addParameter("LOCATION_TYPE5",sBuffer);
                cmd.addParameter("LOCATION_TYPE6",sDelivery);
                cmd.addParameter("LOCATION_TYPE7",sShipment);
                cmd.addParameter("LOCATION_TYPE8",sManufacturing);
                cmd.addParameter("LOT_BATCH_NO");
                cmd.addParameter("SERIAL_NO");
                cmd.addParameter("ENG_CHG_LEVEL");
                cmd.addParameter("WAIV_DEV_REJ_NO");
                cmd.addParameter("INCLUDE_STANDARD",sFalse);
                cmd.addParameter("INCLUDE_PROJECT",sTrue);
                cmd.addParameter("ACTIVITY_SEQ","0");
                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))
                    cmd.addReference("PROJECT_ID", "PROJID/DATA");
                else
                    cmd.addParameter("PROJECT_ID", "");
                //Bug 66456, End
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                cmd.addParameter("CONDITION_CODE");

                //Qty Available
                cmd = trans.addCustomFunction("INVAVL","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sAvailable);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL", sNettable);
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
                cmd.addParameter("OWNERSHIP_TYPE3");
                cmd.addParameter("OWNERSHIP_TYPE4");
                cmd.addParameter("OWNER");
                cmd.addParameter("OWNER_VENDOR");
                cmd.addParameter("LOCATION_TYPE1",sPicking);
                cmd.addParameter("LOCATION_TYPE2",sF);
                cmd.addParameter("LOCATION_TYPE3",sPallet);
                cmd.addParameter("LOCATION_TYPE4",sDeep);
                cmd.addParameter("LOCATION_TYPE5",sBuffer);
                cmd.addParameter("LOCATION_TYPE6",sDelivery);
                cmd.addParameter("LOCATION_TYPE7",sShipment);
                cmd.addParameter("LOCATION_TYPE8",sManufacturing);
                cmd.addParameter("LOT_BATCH_NO");
                cmd.addParameter("SERIAL_NO");
                cmd.addParameter("ENG_CHG_LEVEL");
                cmd.addParameter("WAIV_DEV_REJ_NO");
                cmd.addParameter("INCLUDE_STANDARD",sFalse);
                cmd.addParameter("INCLUDE_PROJECT",sTrue);
                cmd.addParameter("ACTIVITY_SEQ","0");
                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))
                    cmd.addReference("PROJECT_ID", "PROJID/DATA");
                else
                    cmd.addParameter("PROJECT_ID", "");
                //Bug 66456, End
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                cmd.addParameter("CONDITION_CODE");
            }    

            cmd = trans.addCustomFunction("CONCODE","CONDITION_CODE_API.Get_Description","CONDDESC");
            cmd.addParameter("CONDITION_CODE");

            trans = mgr.validate(trans);

            String descri = trans.getValue("CONCODE/DATA/CONDDESC");
            qtyOnHand = trans.getNumberValue("INVONHAND/DATA/QTYONHAND"); 
            qtyOnHand1 = mgr.getASPField("QTYONHAND").formatNumber(qtyOnHand);
            qtyAvail = trans.getNumberValue("INVAVL/DATA/QTY_AVAILABLE");
            qtyAvail1 = mgr.getASPField("QTY_AVAILABLE").formatNumber(qtyAvail);

            trans.clear();
            
            cmd = trans.addCustomCommand("GETINVVAL","Active_Separate_API.Get_Inventory_Value");
            cmd.addParameter("COST");
            cmd.addParameter("SPARE_CONTRACT");
            cmd.addParameter("PART_NO");
            cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
            cmd.addParameter("CONFIGURATION_IDS",mgr.isEmpty(mgr.readValue("CONFIGURATION_IDS"))?"*":mgr.readValue("CONFIGURATION_IDS"));
            cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));
    
            trans = mgr.validate(trans);
    
            nCost = trans.getNumberValue("GETINVVAL/DATA/COST");
    
            if (isNaN(nCost))
                nCost=0;

            planQty = mgr.readNumberValue("PLAN_QTY");
            if (isNaN(planQty))
                planQty=0;
           
            if (planQty == 0)
                nCostAmt = 0;
            else
                nCostAmt    = nCost * planQty;
           
            String strCost=mgr.getASPField("COST").formatNumber(nCost);
            String strCostAmt=mgr.getASPField("AMOUNTCOST").formatNumber(nCostAmt);
            
            txt = (mgr.isEmpty(descri) ? "" : (descri))+ "^" +
                  (mgr.isEmpty(qtyOnHand1) ? "": qtyOnHand1) + "^" +
                  (mgr.isEmpty(qtyAvail1) ? "": qtyAvail1) + "^"+
                  (mgr.isEmpty(strCost) ? "": strCost) + "^" +
                  (mgr.isEmpty(strCostAmt) ? "": strCostAmt) + "^"; 

            /*txt = (mgr.isEmpty(descri) ? "" : (descri))+ "^" +
                  (mgr.isEmpty(qtyOnHand1) ? "": qtyOnHand1) + "^" +
                  (mgr.isEmpty(qtyAvail1) ? "": qtyAvail1) + "^" ; */

            mgr.responseWrite(txt);
        }
        if ("PART_OWNERSHIP".equals(val))
        {
            String qtyOnHand1 = "";
            double qtyOnHand = 0;
            double qtyAvail = 0;
            String qtyAvail1 = "";
            cmd = trans.addCustomFunction("MAINTREQSUP","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","SUPPLY_CODE");
            trans = mgr.validate(trans);
            String sStandardInv = trans.getValue("MAINTREQSUP/DATA/SUPPLY_CODE");
            trans.clear();

            if ("COMPANY OWNED".equals(sClientOwnershipDefault))
                sClientOwnershipConsignment="CONSIGNMENT";
            else
                sClientOwnershipConsignment=null;

            if (sStandardInv.equals(mgr.readValue("SUPPLY_CODE")))
            {
                //Qty OnHand
                cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sOnhand);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL");
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
                cmd.addParameter("OWNERSHIP_TYPE3");
                cmd.addParameter("OWNERSHIP_TYPE4");
                cmd.addParameter("OWNER");
                cmd.addParameter("OWNER_VENDOR");
                cmd.addParameter("LOCATION_TYPE1",sPicking);
                cmd.addParameter("LOCATION_TYPE2",sF);
                cmd.addParameter("LOCATION_TYPE3",sPallet);
                cmd.addParameter("LOCATION_TYPE4",sDeep);
                cmd.addParameter("LOCATION_TYPE5",sBuffer);
                cmd.addParameter("LOCATION_TYPE6",sDelivery);
                cmd.addParameter("LOCATION_TYPE7",sShipment);
                cmd.addParameter("LOCATION_TYPE8",sManufacturing);
                cmd.addParameter("LOT_BATCH_NO");
                cmd.addParameter("SERIAL_NO");
                cmd.addParameter("ENG_CHG_LEVEL");
                cmd.addParameter("WAIV_DEV_REJ_NO");
                cmd.addParameter("INCLUDE_STANDARD",sTrue);
                cmd.addParameter("INCLUDE_PROJECT",sFalse);
                cmd.addParameter("ACTIVITY_SEQ","0");
                cmd.addParameter("PROJECT_ID","");
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                cmd.addParameter("CONDITION_CODE");

                //Qty Available
                cmd = trans.addCustomFunction("INVAVL","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sAvailable);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL", sNettable);
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
                cmd.addParameter("OWNERSHIP_TYPE3");
                cmd.addParameter("OWNERSHIP_TYPE4");
                cmd.addParameter("OWNER");
                cmd.addParameter("OWNER_VENDOR");
                cmd.addParameter("LOCATION_TYPE1",sPicking);
                cmd.addParameter("LOCATION_TYPE2",sF);
                cmd.addParameter("LOCATION_TYPE3",sPallet);
                cmd.addParameter("LOCATION_TYPE4",sDeep);
                cmd.addParameter("LOCATION_TYPE5",sBuffer);
                cmd.addParameter("LOCATION_TYPE6",sDelivery);
                cmd.addParameter("LOCATION_TYPE7",sShipment);
                cmd.addParameter("LOCATION_TYPE8",sManufacturing);
                cmd.addParameter("LOT_BATCH_NO");
                cmd.addParameter("SERIAL_NO");
                cmd.addParameter("ENG_CHG_LEVEL");
                cmd.addParameter("WAIV_DEV_REJ_NO");
                cmd.addParameter("INCLUDE_STANDARD",sTrue);
                cmd.addParameter("INCLUDE_PROJECT",sFalse);
                cmd.addParameter("ACTIVITY_SEQ","0");
                cmd.addParameter("PROJECT_ID","");
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                cmd.addParameter("CONDITION_CODE");
            }
            else
            {
                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))
                {
                    cmd = trans.addCustomFunction("PROJID","Activity_API.Get_Project_Id","PROJECT_ID");
                    cmd.addParameter("ACTIVITY_SEQ",mgr.readValue("ACTIVITY_SEQ"));
                }
                //Bug 66456, End

                //Qty OnHand
                cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sOnhand);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL");
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
                cmd.addParameter("OWNERSHIP_TYPE3");
                cmd.addParameter("OWNERSHIP_TYPE4");
                cmd.addParameter("OWNER");
                cmd.addParameter("OWNER_VENDOR");
                cmd.addParameter("LOCATION_TYPE1",sPicking);
                cmd.addParameter("LOCATION_TYPE2",sF);
                cmd.addParameter("LOCATION_TYPE3",sPallet);
                cmd.addParameter("LOCATION_TYPE4",sDeep);
                cmd.addParameter("LOCATION_TYPE5",sBuffer);
                cmd.addParameter("LOCATION_TYPE6",sDelivery);
                cmd.addParameter("LOCATION_TYPE7",sShipment);
                cmd.addParameter("LOCATION_TYPE8",sManufacturing);
                cmd.addParameter("LOT_BATCH_NO");
                cmd.addParameter("SERIAL_NO");
                cmd.addParameter("ENG_CHG_LEVEL");
                cmd.addParameter("WAIV_DEV_REJ_NO");
                cmd.addParameter("INCLUDE_STANDARD",sFalse);
                cmd.addParameter("INCLUDE_PROJECT",sTrue);
                cmd.addParameter("ACTIVITY_SEQ","0");
                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))
                    cmd.addReference("PROJECT_ID", "PROJID/DATA");
                else
                    cmd.addParameter("PROJECT_ID", "");
                //Bug 66456, End
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                cmd.addParameter("CONDITION_CODE");

                //Qty Available
                cmd = trans.addCustomFunction("INVAAVL","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sAvailable);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL", sNettable);
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
                cmd.addParameter("OWNERSHIP_TYPE3");
                cmd.addParameter("OWNERSHIP_TYPE4");
                cmd.addParameter("OWNER");
                cmd.addParameter("OWNER_VENDOR");
                cmd.addParameter("LOCATION_TYPE1",sPicking);
                cmd.addParameter("LOCATION_TYPE2",sF);
                cmd.addParameter("LOCATION_TYPE3",sPallet);
                cmd.addParameter("LOCATION_TYPE4",sDeep);
                cmd.addParameter("LOCATION_TYPE5",sBuffer);
                cmd.addParameter("LOCATION_TYPE6",sDelivery);
                cmd.addParameter("LOCATION_TYPE7",sShipment);
                cmd.addParameter("LOCATION_TYPE8",sManufacturing);
                cmd.addParameter("LOT_BATCH_NO");
                cmd.addParameter("SERIAL_NO");
                cmd.addParameter("ENG_CHG_LEVEL");
                cmd.addParameter("WAIV_DEV_REJ_NO");
                cmd.addParameter("INCLUDE_STANDARD",sFalse);
                cmd.addParameter("INCLUDE_PROJECT",sTrue);
                cmd.addParameter("ACTIVITY_SEQ","0");
                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))
                    cmd.addReference("PROJECT_ID", "PROJID/DATA");
                else
                    cmd.addParameter("PROJECT_ID", "");
                //Bug 66456, End
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                cmd.addParameter("CONDITION_CODE");
            }

            trans = mgr.validate(trans); 
            qtyOnHand = trans.getNumberValue("INVONHAND/DATA/QTYONHAND"); 
            qtyOnHand1 = mgr.getASPField("QTYONHAND").formatNumber(qtyOnHand);
            qtyAvail = trans.getNumberValue("INVAVL/DATA/QTY_AVAILABLE");
            qtyAvail1 = mgr.getASPField("QTY_AVAILABLE").formatNumber(qtyAvail);

            txt = (mgr.isEmpty(sClientOwnershipDefault) ? "" : (sClientOwnershipDefault)) + "^"+
                  (mgr.isEmpty(qtyOnHand1) ? "": qtyOnHand1) + "^" +
                  (mgr.isEmpty(qtyAvail1) ? "": qtyAvail1) + "^" ;    

            mgr.responseWrite(txt);
        }

        if ("OWNER".equals(val))
        {   
            String sOwnershipDb = mgr.readValue("PART_OWNERSHIP_DB"); 
            String qtyOnHand1 = ""; 
            String qtyAvail1 = "";
            double qtyOnHand = 0;
            double qtyAvail = 0;
            String sOwnerName="";

            cmd = trans.addCustomFunction("MAINTREQSUP","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","SUPPLY_CODE");
            trans = mgr.validate(trans);
            String sStandardInv = trans.getValue("MAINTREQSUP/DATA/SUPPLY_CODE");
            trans.clear();

            if ("COMPANY OWNED".equals(sClientOwnershipDefault))
                sClientOwnershipConsignment="CONSIGNMENT";
            else
                sClientOwnershipConsignment=null;

            if (sStandardInv.equals(mgr.readValue("SUPPLY_CODE")))
            {
                //Qty OnHand
                cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sOnhand);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL");
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
                cmd.addParameter("OWNERSHIP_TYPE3");
                cmd.addParameter("OWNERSHIP_TYPE4");
                cmd.addParameter("OWNER");
                cmd.addParameter("OWNER_VENDOR");
                cmd.addParameter("LOCATION_TYPE1",sPicking);
                cmd.addParameter("LOCATION_TYPE2",sF);
                cmd.addParameter("LOCATION_TYPE3",sPallet);
                cmd.addParameter("LOCATION_TYPE4",sDeep);
                cmd.addParameter("LOCATION_TYPE5",sBuffer);
                cmd.addParameter("LOCATION_TYPE6",sDelivery);
                cmd.addParameter("LOCATION_TYPE7",sShipment);
                cmd.addParameter("LOCATION_TYPE8",sManufacturing);
                cmd.addParameter("LOT_BATCH_NO");
                cmd.addParameter("SERIAL_NO");
                cmd.addParameter("ENG_CHG_LEVEL");
                cmd.addParameter("WAIV_DEV_REJ_NO");
                cmd.addParameter("INCLUDE_STANDARD",sTrue);
                cmd.addParameter("INCLUDE_PROJECT",sFalse);
                cmd.addParameter("ACTIVITY_SEQ","0");
                cmd.addParameter("PROJECT_ID","");
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                cmd.addParameter("CONDITION_CODE");

                //Qty Available
                cmd = trans.addCustomFunction("INVAVL","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sAvailable);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL",sNettable);
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
                cmd.addParameter("OWNERSHIP_TYPE3");
                cmd.addParameter("OWNERSHIP_TYPE4");
                cmd.addParameter("OWNER");
                cmd.addParameter("OWNER_VENDOR");
                cmd.addParameter("LOCATION_TYPE1",sPicking);
                cmd.addParameter("LOCATION_TYPE2",sF);
                cmd.addParameter("LOCATION_TYPE3",sPallet);
                cmd.addParameter("LOCATION_TYPE4",sDeep);
                cmd.addParameter("LOCATION_TYPE5",sBuffer);
                cmd.addParameter("LOCATION_TYPE6",sDelivery);
                cmd.addParameter("LOCATION_TYPE7",sShipment);
                cmd.addParameter("LOCATION_TYPE8",sManufacturing);
                cmd.addParameter("LOT_BATCH_NO");
                cmd.addParameter("SERIAL_NO");
                cmd.addParameter("ENG_CHG_LEVEL");
                cmd.addParameter("WAIV_DEV_REJ_NO");
                cmd.addParameter("INCLUDE_STANDARD",sTrue);
                cmd.addParameter("INCLUDE_PROJECT",sFalse);
                cmd.addParameter("ACTIVITY_SEQ","0");
                cmd.addParameter("PROJECT_ID","");
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                cmd.addParameter("CONDITION_CODE");
            }
            else
            {
                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))
                {
                    cmd = trans.addCustomFunction("PROJID","Activity_API.Get_Project_Id","PROJECT_ID");
                    cmd.addParameter("ACTIVITY_SEQ",mgr.readValue("ACTIVITY_SEQ"));
                }
                //Bug 66456, End

                //Qty OnHand
                cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sOnhand);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL");
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
                cmd.addParameter("OWNERSHIP_TYPE3");
                cmd.addParameter("OWNERSHIP_TYPE4");
                cmd.addParameter("OWNER");
                cmd.addParameter("OWNER_VENDOR");
                cmd.addParameter("LOCATION_TYPE1",sPicking);
                cmd.addParameter("LOCATION_TYPE2",sF);
                cmd.addParameter("LOCATION_TYPE3",sPallet);
                cmd.addParameter("LOCATION_TYPE4",sDeep);
                cmd.addParameter("LOCATION_TYPE5",sBuffer);
                cmd.addParameter("LOCATION_TYPE6",sDelivery);
                cmd.addParameter("LOCATION_TYPE7",sShipment);
                cmd.addParameter("LOCATION_TYPE8",sManufacturing);
                cmd.addParameter("LOT_BATCH_NO");
                cmd.addParameter("SERIAL_NO");
                cmd.addParameter("ENG_CHG_LEVEL");
                cmd.addParameter("WAIV_DEV_REJ_NO");
                cmd.addParameter("INCLUDE_STANDARD",sFalse);
                cmd.addParameter("INCLUDE_PROJECT",sTrue);
                cmd.addParameter("ACTIVITY_SEQ","0");
                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))
                    cmd.addReference("PROJECT_ID", "PROJID/DATA");
                else
                    cmd.addParameter("PROJECT_ID", "");
                //Bug 66456, End
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                cmd.addParameter("CONDITION_CODE");

                //Qty Available
                cmd = trans.addCustomFunction("INVAVL","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTY_AVAILABLE");
                cmd.addParameter("CONTRACT",mgr.readValue("SPARE_CONTRACT",""));
                cmd.addParameter("PART_NO");
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sAvailable);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL", sNettable);
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
                cmd.addParameter("OWNERSHIP_TYPE3");
                cmd.addParameter("OWNERSHIP_TYPE4");
                cmd.addParameter("OWNER");
                cmd.addParameter("OWNER_VENDOR");
                cmd.addParameter("LOCATION_TYPE1",sPicking);
                cmd.addParameter("LOCATION_TYPE2",sF);
                cmd.addParameter("LOCATION_TYPE3",sPallet);
                cmd.addParameter("LOCATION_TYPE4",sDeep);
                cmd.addParameter("LOCATION_TYPE5",sBuffer);
                cmd.addParameter("LOCATION_TYPE6",sDelivery);
                cmd.addParameter("LOCATION_TYPE7",sShipment);
                cmd.addParameter("LOCATION_TYPE8",sManufacturing);
                cmd.addParameter("LOT_BATCH_NO");
                cmd.addParameter("SERIAL_NO");
                cmd.addParameter("ENG_CHG_LEVEL");
                cmd.addParameter("WAIV_DEV_REJ_NO");
                cmd.addParameter("INCLUDE_STANDARD",sFalse);
                cmd.addParameter("INCLUDE_PROJECT",sTrue);
                cmd.addParameter("ACTIVITY_SEQ","0");
                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))
                    cmd.addReference("PROJECT_ID", "PROJID/DATA");
                else
                    cmd.addParameter("PROJECT_ID", "");
                //Bug 66456, End
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                cmd.addParameter("CONDITION_CODE");
            }

            if ("CUSTOMER OWNED".equals(sOwnershipDb))
	    {
		cmd = trans.addCustomFunction("GETOWNERNAME", "CUSTOMER_INFO_API.Get_Name", "OWNER_NAME");
		cmd.addParameter("OWNER");
            }
            if ("SUPPLIER LOANED".equals(sOwnershipDb))
	    {
		cmd = trans.addCustomFunction("GETOWNERNAME1", "Supplier_API.Get_Vendor_Name", "OWNER_NAME");
		cmd.addParameter("OWNER");
            }

            cmd = trans.addCustomFunction("GETOWCUST","ACTIVE_SEPARATE_API.Get_Customer_No", "WO_CUST");
            cmd.addParameter("WO_NO",mgr.readValue("ITEM0_WO_NO",""));

            trans = mgr.validate(trans);

            qtyOnHand = trans.getNumberValue("INVONHAND/DATA/QTYONHAND");
            qtyOnHand1 = mgr.getASPField("QTYONHAND").formatNumber(qtyOnHand);
            qtyAvail = trans.getNumberValue("INVAVL/DATA/QTY_AVAILABLE");
            qtyAvail1 = mgr.getASPField("QTY_AVAILABLE").formatNumber(qtyAvail);
            if ("CUSTOMER OWNED".equals(sOwnershipDb))
		sOwnerName = trans.getValue("GETOWNERNAME/DATA/OWNER_NAME");
            if ("SUPPLIER LOANED".equals(sOwnershipDb))
		sOwnerName = trans.getValue("GETOWNERNAME1/DATA/OWNER_NAME");
            String sWoCust    = trans.getValue("GETOWCUST/DATA/WO_CUST");

            txt = (mgr.isEmpty(sOwnerName) ? "" : (sOwnerName)) + "^" + (mgr.isEmpty(sWoCust) ? "": (sWoCust)) + "^" + (mgr.isEmpty(qtyOnHand1) ? "": qtyOnHand1) + "^" + (mgr.isEmpty(qtyAvail1) ? "": qtyAvail1) + "^" ;   
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

        cmd = trans.addEmptyCommand("HEAD","MAINT_MATERIAL_REQUISITION_API.New__",headblk);
        cmd.setOption("ACTION","PREPARE");

        trans = mgr.perform(trans);
        headset.addRow(data);
    }

//-----------------------------------------------------------------------------
//----------------------  CMDBAR EDIT-IT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void newRowITEM0()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        cmd = trans.addEmptyCommand("ITEM0","MAINT_MATERIAL_REQ_LINE_API.New__",itemblk0);
        cmd.setOption("ACTION","PREPARE");

        cmd = trans.addCustomFunction("GETCONT","WORK_ORDER_API.Get_Contract","WO_CONTRACT");
        cmd.addParameter("ITEM0_WO_NO",headset.getRow().getValue("WO_NO"));

        trans = mgr.perform(trans);
        data = trans.getBuffer("ITEM0/DATA");
        data.setFieldItem("ITEM0_WO_NO",headset.getRow().getValue("WO_NO"));
        data.setFieldItem("ITEM0_MAINT_MATERIAL_ORDER_NO",headset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));
        data.setFieldItem("DATE_REQUIRED",headset.getRow().getFieldValue("DUE_DATE"));
        data.setFieldItem("SPARE_CONTRACT",headset.getRow().getFieldValue("CONTRACT"));
        data.setFieldItem("CATALOG_CONTRACT",trans.getValue("GETCONT/DATA/WO_CONTRACT"));
        itemset0.addRow(data);
    }

    public void duplicateITEM0()
    {
        ASPManager mgr = getASPManager();

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());

        itemlay0.setLayoutMode(itemlay0.NEW_LAYOUT);
        cmd = trans.addEmptyCommand("ITEM0","MAINT_MATERIAL_REQ_LINE_API.New__",itemblk0);
        cmd.setOption("ACTION","PREPARE");

        trans = mgr.perform(trans);

        data = trans.getBuffer("ITEM0/DATA");

        data.setFieldItem("PART_NO",itemset0.getRow().getValue("PART_NO"));
        data.setFieldItem("SPAREDESCRIPTION",itemset0.getRow().getValue("SPAREDESCRIPTION"));
        data.setFieldItem("CONDITION_CODE",itemset0.getRow().getValue("CONDITION_CODE"));
        data.setFieldItem("CONDDESC",itemset0.getRow().getValue("CONDDESC"));
        data.setFieldItem("SPARE_CONTRACT",itemset0.getRow().getValue("SPARE_CONTRACT"));
        data.setFieldItem("HASSPARESTRUCTURE",itemset0.getRow().getValue("HASSPARESTRUCTURE"));
        data.setFieldItem("DIMQTY",itemset0.getRow().getValue("DIMQTY"));
        data.setFieldItem("TYPEDESIGN",itemset0.getRow().getValue("TYPEDESIGN"));
        data.setFieldItem("DATE_REQUIRED",itemset0.getRow().getFieldValue("DATE_REQUIRED"));
        data.setFieldItem("PLAN_QTY",itemset0.getRow().getValue("PLAN_QTY"));
        data.setFieldItem("QTY_SHORT",itemset0.getRow().getValue("QTY_SHORT"));
        data.setFieldItem("QTYONHAND",itemset0.getRow().getValue("QTYONHAND"));
        data.setFieldItem("CATALOG_NO",itemset0.getRow().getValue("CATALOG_NO"));
        data.setFieldItem("CATALOGDESC",itemset0.getRow().getValue("CATALOGDESC"));
        data.setFieldItem("PRICE_LIST_NO",itemset0.getRow().getValue("PRICE_LIST_NO"));
        data.setFieldItem("LIST_PRICE",itemset0.getRow().getFieldValue("LIST_PRICE"));
        data.setFieldItem("UNITMEAS",itemset0.getRow().getValue("UNITMEAS"));
        data.setFieldItem("CATALOG_CONTRACT",itemset0.getRow().getValue("CATALOG_CONTRACT"));
        data.setFieldItem("PLAN_LINE_NO",itemset0.getRow().getValue("PLAN_LINE_NO"));
        data.setFieldItem("DISCOUNT",itemset0.getRow().getFieldValue("DISCOUNT"));
        data.setFieldItem("SALESPRICEAMOUNT",itemset0.getRow().getFieldValue("SALESPRICEAMOUNT"));
        data.setFieldItem("SCODEA",itemset0.getRow().getValue("SCODEA"));
        data.setFieldItem("SCODEB",itemset0.getRow().getValue("SCODEB"));
        data.setFieldItem("SCODEC",itemset0.getRow().getValue("SCODEC"));
        data.setFieldItem("SCODED",itemset0.getRow().getValue("SCODED"));
        data.setFieldItem("SCODEE",itemset0.getRow().getValue("SCODEE"));
        data.setFieldItem("SCODEF",itemset0.getRow().getValue("SCODEF"));
        data.setFieldItem("SCODEG",itemset0.getRow().getValue("SCODEG"));
        data.setFieldItem("SCODEH",itemset0.getRow().getValue("SCODEH"));
        data.setFieldItem("SCODEI",itemset0.getRow().getValue("SCODEI"));
        data.setFieldItem("SCODEJ",itemset0.getRow().getValue("SCODEJ"));
        data.setFieldItem("ITEM0_WO_NO",headset.getRow().getValue("WO_NO"));
        data.setFieldItem("ITEM0_MAINT_MATERIAL_ORDER_NO",headset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));

        itemset0.addRow(data);                        
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

    public void okFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (Select User_Allowed_Site_API.Authorized(CONTRACT) from DUAL)");
        if (mgr.dataTransfered())
        {
            retBuffer =  mgr.getTransferedData();
            if (retBuffer.itemExists("WO_NO"))
            {
                ret_wo_no = retBuffer.getValue("WO_NO");
                q.addWhereCondition("WO_NO = ?");
                q.addParameter("WO_NO",ret_wo_no);

            }
            else
                q.addOrCondition(mgr.getTransferedData());
        }
        q.includeMeta("ALL");

        mgr.querySubmit(trans,headblk);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONNODATA: No data found."));
            headset.clear();
        }

        if (headset.countRows() == 1)
            okFindITEM0();

        qrystr = mgr.createSearchURL(headblk);
    }

//-----------------------------------------------------------------------------
//-----------------------  CMDBAR SEARCH-IT FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

    public void okFindITEM0()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        int headrowno = headset.getCurrentRowNo();
        q = trans.addQuery(itemblk0);
        q.addWhereCondition("WO_NO = ?");
        q.addWhereCondition("MAINT_MATERIAL_ORDER_NO = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        q.addParameter("MAINT_MATERIAL_ORDER_NO",headset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));
	q.includeMeta("ALL");
        mgr.querySubmit(trans,itemblk0);

        if (itemset0.countRows() == 0 && "ITEM0.OkFind".equals(mgr.readValue("__COMMAND")))
        {
            mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONNODATA: No data found."));
            itemset0.clear();
        }

        headset.goTo(headrowno);

        if (itemset0.countRows() > 0)
            setValuesInMaterials();
    }

    public void countFindITEM0()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk0);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ?");
        q.addWhereCondition("MAINT_MATERIAL_ORDER_NO = ?");
        q.addParameter("WO_NO",headset.getRow().getValue("WO_NO"));
        q.addParameter("MAINT_MATERIAL_ORDER_NO",headset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));
        int headrowno = headset.getCurrentRowNo();
        mgr.submit(trans);
        itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
        itemset0.clear();
        headset.goTo(headrowno);
    }

    public void saveReturn()
    {
        ASPManager mgr = getASPManager();
        int currrow = headset.getCurrentRowNo();

        headset.changeRow();
        mgr.submit(trans);
        headset.goTo(currrow);
        headlay.setLayoutMode(headlay.getHistoryMode());

        // Prepare Query String
        if (mgr.isEmpty(qrystr))
            qrystr = mgr.getURL()+"?SEARCH=Y&MAINT_MATERIAL_ORDER_NO="+headset.getRow().getValue("MAINT_MATERIAL_ORDER_NO");
        else
        {
            int start = qrystr.indexOf("MAINT_MATERIAL_ORDER_NO");
            if (start > 0)
            {
                String subStr1 = qrystr.substring(start);
                int end = subStr1.indexOf("&");
                String subStr2;
                if (end > 0)
                    subStr2 = subStr1.substring(0,end);
                else
                    subStr2 = subStr1;
                String subStr3 = subStr2+";"+headset.getRow().getValue("MAINT_MATERIAL_ORDER_NO");
                qrystr = qrystr.replaceAll(subStr2,subStr3);
            }
            else
                qrystr = qrystr+";MAINT_MATERIAL_ORDER_NO="+headset.getRow().getValue("MAINT_MATERIAL_ORDER_NO");
        }
    }


    public void setValuesInMaterials()
    {
        ASPManager mgr = getASPManager();

        trans.clear(); 
        cmd = trans.addCustomFunction("GETOBJSUPL","MAINT_MATERIAL_REQ_LINE_API.Is_Obj_Supp_Loaned","OBJ_LOAN");
        cmd.addParameter("ITEM0_WO_NO");

        trans = mgr.validate(trans);

        String sObjSup = trans.getValue("GETOBJSUPL/DATA/OBJ_LOAN");

        trans.clear();

        int n = itemset0.countRows();

        if (n > 0)
        {
            itemset0.first();

            for (int i=0; i<=n; ++i)
            {
                String spareCont = itemset0.getRow().getValue("SPARE_CONTRACT");
                String partNo =itemset0.getRow().getFieldValue("PART_NO");
                String cataNo = itemset0.getRow().getValue("CATALOG_NO");
                String nPlanQty = itemset0.getRow().getFieldValue("PLAN_QTY");
                String cataCont = itemset0.getRow().getFieldValue("CATALOG_CONTRACT");
                String priceListNo = itemset0.getRow().getFieldValue("PRICE_LIST_NO");
                String planLineNo = itemset0.getRow().getFieldValue("PLAN_LINE_NO");

                String serialNo = itemset0.getRow().getFieldValue("SERIAL_NO");
                String conditionCode = itemset0.getRow().getFieldValue("CONDITION_CODE");
                String configId = itemset0.getRow().getFieldValue("CONFIGURATION_IDS");
                String owner = itemset0.getRow().getFieldValue("OWNER");
                String sOwnershipDb = itemset0.getRow().getFieldValue("PART_OWNERSHIP_DB");
                if ("CUSTOMER OWNED".equals(sOwnershipDb))
		{
		    cmd = trans.addCustomFunction("GETOWNERNAME"+i, "CUSTOMER_INFO_API.Get_Name", "OWNER_NAME");
		    cmd.addParameter("OWNER",owner);
                }
                if ("SUPPLIER LOANED".equals(sOwnershipDb))
		{
		    cmd = trans.addCustomFunction("GETOWNERNAME1"+i, "Supplier_API.Get_Vendor_Name", "OWNER_NAME");
		    cmd.addParameter("OWNER",owner);
                }

                /*cmd = trans.addCustomFunction("GETCOST"+i,"Inventory_Part_API.Get_Inventory_Value_By_Method","COST");
                cmd.addParameter("SPARE_CONTRACT",spareCont);
                cmd.addParameter("PART_NO",partNo); */

                cmd = trans.addCustomCommand("GETCOST"+i,"Active_Separate_API.Get_Inventory_Value");
                cmd.addParameter("COST");
                cmd.addParameter("SPARE_CONTRACT",spareCont);
                cmd.addParameter("PART_NO",partNo);
                cmd.addParameter("SERIAL_NO",serialNo);
                cmd.addParameter("CONFIGURATION_IDS",mgr.isEmpty(mgr.readValue("CONFIGURATION_IDS"))?"*":mgr.readValue("CONFIGURATION_IDS"));
                cmd.addParameter("CONDITION_CODE",conditionCode);

                if ((!mgr.isEmpty(cataNo)) && (!mgr.isEmpty(nPlanQty)))
                {
                    cmd = trans.addCustomFunction("AGREID"+i,"Active_Work_Order_API.Get_Agreement_Id","AGREEMENT_ID");
                    cmd.addParameter("ITEM0_WO_NO",itemset0.getRow().getFieldValue("ITEM0_WO_NO"));

                    cmd = trans.addCustomFunction("CUSTONO"+i,"Active_Work_Order_API.Get_Customer_No","CUSTOMER_NO");
                    cmd.addParameter("ITEM0_WO_NO",itemset0.getRow().getFieldValue("ITEM0_WO_NO"));

                    cmd = trans.addCustomCommand("PRICEINFO"+i,"Work_Order_Coding_API.Get_Price_Info");
                    cmd.addParameter("BASE_PRICE","0");
                    cmd.addParameter("SALE_PRICE","0");
                    cmd.addParameter("DISCOUNT","0");
                    cmd.addParameter("CURRENCY_RATE","0");
                    cmd.addParameter("CATALOG_CONTRACT",cataCont);
                    cmd.addParameter("CATALOG_NO",cataNo);
                    cmd.addReference("CUSTOMER_NO","CUSTONO"+i+"/DATA");
                    cmd.addReference("AGREEMENT_ID","AGREID"+i+"/DATA");
                    cmd.addParameter("PRICE_LIST_NO",priceListNo);
                    cmd.addParameter("PLAN_QTY",nPlanQty);

                    cmd = trans.addCustomFunction("LISTPRICE"+i,"WORK_ORDER_PLANNING_API.Get_Sales_Price","LIST_PRICE");
                    cmd.addParameter("ITEM0_WO_NO",itemset0.getRow().getFieldValue("ITEM0_WO_NO"));
                    cmd.addParameter("PLAN_LINE_NO",planLineNo);
                }

                itemset0.next();
            }           

            trans = mgr.validate(trans);

            itemset0.first();

            for (int i=0; i<n; ++i)
            {
                double nCost = 0;
                String  ownerName = "";

                row = itemset0.getRow();
                if ("CUSTOMER OWNED".equals(itemset0.getRow().getFieldValue("PART_OWNERSHIP_DB")))
		{
                    ownerName= trans.getValue("GETOWNERNAME"+i+"/DATA/OWNER_NAME");
                }
                if ("SUPPLIER LOANED".equals(itemset0.getRow().getFieldValue("PART_OWNERSHIP_DB")))
		{
                    ownerName= trans.getValue("GETOWNERNAME1"+i+"/DATA/OWNER_NAME");
                }
                if (!mgr.isEmpty(itemset0.getRow().getFieldValue("PART_NO")))
                {
                    nCost= trans.getNumberValue("GETCOST"+i+"/DATA/COST");
                    if (isNaN(nCost))
                        nCost =0;
                    if ("TRUE".equals(sObjSup) || "SUPPLIER LOANED".equals(mgr.readValue("PART_OWNERSHIP_DB"))) 
                        nCost = 0;
                }
                double nPlanQty = itemset0.getRow().getNumberValue("PLAN_QTY");
                if (isNaN(nPlanQty))
                    nPlanQty = 0;

                double nCostAmount;

                if (nPlanQty != 0)
                {
                    double nCostTemp = trans.getNumberValue("GETCOST"+i+"/DATA/COST");
                    if (isNaN(nCostTemp))
                        nCostTemp =0;
                    if ("TRUE".equals(sObjSup) || "SUPPLIER LOANED".equals(mgr.readValue("PART_OWNERSHIP_DB"))) 
                        nCostTemp = 0;

                    nCostAmount = nCostTemp * nPlanQty;
                }
                else
                    nCostAmount = 0;

                String priceListNo = itemset0.getRow().getFieldValue("PRICE_LIST_NO");
                String cataNo = itemset0.getRow().getFieldValue("CATALOG_NO");
                double nDiscount = itemset0.getRow().getNumberValue("DISCOUNT");
                if (isNaN(nDiscount))
                    nDiscount = 0;

                if ((!mgr.isEmpty(cataNo)) && (nPlanQty != 0))
                {
                    double listPrice = trans.getNumberValue("LISTPRICE"+i+"/DATA/LIST_PRICE");
                    if (isNaN(listPrice))
                        listPrice = 0;

                    if (mgr.isEmpty(priceListNo))
                        priceListNo = trans.getBuffer("PRICEINFO"+i+"/DATA").getFieldValue("PRICE_LIST_NO");

                    if (nDiscount == 0)
                    {
                        nDiscount = trans.getNumberValue("PRICEINFO"+i+"/DATA/DISCOUNT");   
                        if (isNaN(nDiscount))
                            nDiscount = 0;
                    }

                    double nSalesPriceAmount;

                    if (listPrice == 0)
                    {
                        listPrice = trans.getNumberValue("PRICEINFO"+i+"/DATA/BASE_PRICE");
                        if (isNaN(listPrice))
                            listPrice = 0;

                        double planQty = trans.getNumberValue("PRICEINFO"+i+"/DATA/PLAN_QTY");  
                        if (isNaN(planQty))
                            planQty = 0;

                        double nSaleUnitPrice = trans.getNumberValue("PRICEINFO"+i+"/DATA/SALE_PRICE");
                        if (isNaN(nSaleUnitPrice))
                            nSaleUnitPrice = 0;

                        nSalesPriceAmount  = nSaleUnitPrice * planQty;
                    }
                    else
                    {
                        double nListPriceTemp = trans.getNumberValue("LISTPRICE"+i+"/DATA/LIST_PRICE");
                        if (isNaN(nListPriceTemp))
                            nListPriceTemp = 0;

                        double planQty = trans.getNumberValue("PRICEINFO"+i+"/DATA/PLAN_QTY");  
                        if (isNaN(planQty))
                            planQty = 0;

                        double nDiscountTemp = itemset0.getRow().getNumberValue("DISCOUNT");
                        if (isNaN(nDiscountTemp))
                            nDiscountTemp = 0;

                        if (nDiscountTemp == 0)
                        {
                            nDiscountTemp = trans.getNumberValue("PRICEINFO"+i+"/DATA/DISCOUNT");
                            if (isNaN(nDiscountTemp))
                                nDiscountTemp = 0;
                        }
                        nSalesPriceAmount = nListPriceTemp * planQty;
                        nSalesPriceAmount = nSalesPriceAmount - (nDiscountTemp / 100 * nSalesPriceAmount);
                    }

                    //row.setValue("PRICE_LIST_NO",priceListNo);
                    //row.setNumberValue("LIST_PRICE",listPrice);
                    //row.setNumberValue("DISCOUNT",nDiscount);
                    row.setNumberValue("SALESPRICEAMOUNT",nSalesPriceAmount);
                }

                row.setNumberValue("COST",nCost);
                row.setNumberValue("AMOUNTCOST",nCostAmount);
                row.setValue("OWNER_NAME",ownerName);

                itemset0.setRow(row);

                itemset0.next();
            }
        }

        itemset0.first();
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void perform( String command) 
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            headset.markSelectedRows(command);
            mgr.submit(trans);
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

    public boolean noReserv()
    {
        ASPManager mgr = getASPManager();

        int count = itemset0.countRows();

        if (count > 0)
        {
            itemset0.unselectRows();
            itemset0.first();

            for (int i = 0; i < count; i++)
            {
                double nQtyAssign = itemset0.getNumberValue("QTY_ASSIGNED");

                if (isNaN(nQtyAssign))
                    nQtyAssign = 0;

                if (nQtyAssign > 0)
                    return false;

                itemset0.next();
            }

            return true;
        }
        else
            return true;
    }

    public boolean noIssue()
    {
        ASPManager mgr = getASPManager();

        int count = itemset0.countRows();

        if (count > 0)
        {
            itemset0.unselectRows();
            itemset0.first();

            for (int i = 0; i < count; i++)
            {
                double nIssue = itemset0.getNumberValue("QTY");

                if (isNaN(nIssue))
                    nIssue = 0;

                if (nIssue > 0)
                    return false;

                itemset0.next();
            }

            return true;
        }
        else
            return true;
    }

    public void plan()
    {
        ASPManager mgr = getASPManager();

        if (noReserv() && noIssue())
            perform("PLAN__");
        else
            mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONCANNOTPLAN: Can not perform on selected line"));    
    }

    public void release()
    {
        ASPManager mgr = getASPManager();

        perform("RELEASE__");
    }

    public void close()
    {
        ASPManager mgr = getASPManager();

        if (noReserv())
            perform("CLOSE__");
        else
            mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONCANNOTCLOSE: Can not perform on selected line"));    
    }

    public void printPicList()
    {
        ASPManager mgr = getASPManager();

        ASPBuffer print;
	ASPBuffer printBuff;
	String attr1;
	String attr2;
	String attr3;
	String attr4;

	if ( headlay.isMultirowLayout() )
	    headset.goTo(headset.getRowSelected());
    
	String orderNo = headset.getRow().getValue("MAINT_MATERIAL_ORDER_NO");
	String orderList =  orderNo +";";

	trans.clear();

	cmd = trans.addCustomFunction("RESEXIST","MAINT_MATERIAL_REQUISITION_API.New_Assign_Exist","EXIST"); 
	cmd.addParameter("ORDER_LIST",orderList); 

	trans = mgr.perform(trans);

	String res_exist = trans.getValue("RESEXIST/DATA/EXIST");
	trans.clear();
	if ( !mgr.isEmpty(orderNo) )
	{
	    if ("1".equals(res_exist)) 
	    {

		attr1 = "REPORT_ID" + (char)31 + "MAINT_MATERIAL_REQUISITION_REP" + (char)30;
		attr2 = "MAINT_MTRL_ORDER_NO" + (char)31 + headset.getValue("MAINT_MATERIAL_ORDER_NO") + (char)30;
		attr3 =  "";
		attr4 =  "";

		cmd = trans.addCustomCommand("PRINTPICKLIST","Archive_API.New_Client_Report");
		cmd.addParameter("ATTR0");                       
		cmd.addParameter("ATTR1",attr1);       
		cmd.addParameter("ATTR2",attr2);              
		cmd.addParameter("ATTR3",attr3);      
		cmd.addParameter("ATTR4",attr4);  

		trans = mgr.perform(trans);

		String attr0 = trans.getValue("PRINTPICKLIST/DATA/ATTR0");

		print = mgr.newASPBuffer();

		printBuff = print.addBuffer("DATA");
		printBuff.addItem("RESULT_KEY", attr0);

		callPrintDlg(print,true);
	    }
	    else

		mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONNONEW: No new Assigned stock for this Material Order."));
	}
	else
	    mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONCANNOT: Can not perform on selected line"));

    }

    // 031217  ARWILK  Begin  (Links with RMB's)
    public void sparePartObject()
    {
        ASPManager mgr = getASPManager();

        if (headset.countRows()>0)
        {
            int curr = headset.getCurrentRowNo();
            ctx.setGlobal("CURRROWGLOBAL",Integer.toString(curr));
        }

        bOpenNewWindow = true;

        urlString = "MaintenanceObject2.page?MCH_CODE=" + mgr.URLEncode(headset.getValue("MCH_CODE")) +
                    "&CONTRACT=" + mgr.URLEncode(headset.getValue("WO_CONTRACT")) +
                    "&WO_NO=" + mgr.URLEncode(headset.getValue("WO_NO")) +
                    "&ORDER_NO=" + mgr.URLEncode(headset.getValue("MAINT_MATERIAL_ORDER_NO")) +
                    "&QRYSTR=" + mgr.URLEncode(qrystr) +
                    "&FRMNAME=" + mgr.URLEncode("maintMatReqNavi");

        newWinHandle = "sparePartInObject"; 
    }

    //Bug 82543, Start
    public void refresh()
    {
        if (headlay.isSingleLayout())
            headset.refreshRow();
        else
            headset.refreshAllRows();
    }
    //Bug 82543, End

    public void refreshForm()
    {
       ASPManager mgr = getASPManager();
       
       okFindITEM0();
    }

    public void objStructure()
    {
        ASPManager mgr = getASPManager();

        if (headset.countRows()>0)
        {
            int curr = headset.getCurrentRowNo();
            ctx.setGlobal("CURRROWGLOBAL",Integer.toString(curr));
        }

        bOpenNewWindow = true;

        urlString = "MaintenaceObject3.page?MCH_CODE=" + mgr.URLEncode(headset.getValue("MCH_CODE")) +
                    "&CONTRACT=" + mgr.URLEncode(headset.getValue("WO_CONTRACT")) +
                    "&WO_NO=" + mgr.URLEncode(headset.getValue("WO_NO")) +
                    "&ORDER_NO=" + mgr.URLEncode(headset.getValue("MAINT_MATERIAL_ORDER_NO")) +
                    "&QRYSTR=" + mgr.URLEncode(qrystr) +
                    "&FRMNAME=" + mgr.URLEncode("maintMatReq");

        newWinHandle = "objStructure"; 
    }
    // 031217  ARWILK  End  (Links with RMB's)

    public void detchedPartList()
    {
        ASPManager mgr = getASPManager();

        String sPartNo = "";
        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());
        else
            itemset0.selectRow();

        if (headset.countRows()>0)
        {
            int curr = headset.getCurrentRowNo();
            ctx.setGlobal("CURRROWGLOBAL",Integer.toString(curr));
        }

        ASPBuffer buffer = mgr.newASPBuffer();
        ASPBuffer row = buffer.addBuffer("0");
        //Bug 82543, start
//        if (itemset0.countRows() > 0) {
        if (itemset0.getRowSelected() != -1) {
            itemset0.goTo(itemset0.getRowSelected());
            sPartNo  = itemset0.getRow().getValue("PART_NO");
        }
        if (!(mgr.isEmpty(sPartNo))) {
            trans.clear();
            cmd = trans.addCustomFunction("SPARESTRUCT","Equipment_Spare_Structure_API.Has_Spare_Structure","HASSPARESTRUCTURE");
            cmd.addParameter("PART_NO",itemset0.getRow().getValue("PART_NO"));
            cmd.addParameter("CONTRACT",itemset0.getRow().getValue("SPARE_CONTRACT"));
            trans = mgr.perform(trans);
            String hasSt = trans.getValue("SPARESTRUCT/DATA/HASSPARESTRUCTURE");
            if ("FALSE".equals(hasSt)) {
                mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONCANNOT: Cannot perform on selected line"));
            }
            else{

                bOpenNewWindow = true;
                row.addItem("PART_NO",sPartNo);
                row.addItem("WO_NO",headset.getRow().getValue("WO_NO"));    
                row.addItem("FRAME","maintMatReq");
                row.addItem("QRYST",qrystr);
                row.addItem("ORDER_NO",headset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));

                urlString = createTransferUrl("../equipw/EquipmentSpareStructure3.page", buffer);

                newWinHandle = "detachedPart"; 
            }
        }
        else {

            bOpenNewWindow = true;
            row.addItem("PART_NO",sPartNo);
            row.addItem("WO_NO",headset.getRow().getValue("WO_NO"));    
            row.addItem("FRAME","maintMatReq");
            row.addItem("QRYST",qrystr);
            row.addItem("ORDER_NO",headset.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));

            urlString = createTransferUrl("../equipw/EquipmentSpareStructure3.page", buffer);

            newWinHandle = "detachedPart"; 
        }
        //Bug 82543, end
    }
    
    public void reserve()
    {
       ASPManager mgr = getASPManager();
       ASPTransactionBuffer transForReserve;

       int currrow = headset.getCurrentRowNo();
       int count = 0;
       int successCount = 0;

       String sStatusCodeReleased;
       String dfStatus;

       double nQtyShort;

       trans.clear();

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

       cmd = trans.addCustomFunction("FINSTATEDEC", "MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__", "DB_STATE"); 
       cmd.addParameter("DB_STATE", "Released"); 

       cmd = trans.addCustomFunction("RESALLOW", "MAINT_MATERIAL_REQ_LINE_API.Is_Reservation_Allowed", "RES_ALLO");
       cmd.addParameter("WO_NO",itemset0.getValue("WO_NO"));

       trans = mgr.perform(trans);

       sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DB_STATE");
       dfStatus = headset.getValue("STATE");

       if (!(sStatusCodeReleased.equals(dfStatus)))
       {
          mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONCANNOTSTAT2: Maint Material Requisition status not valid for material reserve."));
          return ;
       }

       if (!("TRUE".equals(trans.getValue("RESALLOW/DATA/RES_ALLO"))))
       {
          mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONCANNOTSTATWO2: Work order status not valid for material reserve."));
          return;
       }

       transForReserve = mgr.newASPTransactionBuffer();

       for (int i = 0; i < count; i++)
       {

          if (itemset0.getNumberValue("PLAN_QTY") <= (itemset0.getNumberValue("QTY") + itemset0.getNumberValue("QTY_ASSIGNED"))) // + itemset.getNumberValue("QTY_RETURNED"))) * ASSALK  Material Issue & Reserve modification.
          {
             mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONCANNOTMAT3: No material requirements for selected item."));
             return;
          }

          double planQty = itemset0.getRow().getNumberValue("PLAN_QTY");
          if ( isNaN( planQty) )
             planQty = 0;

          double qtyOnHand = GetInventoryQuantity("ONHAND");
          double nRes = GetInventoryQuantity("RESERVED");
          double nAvalToRes =  qtyOnHand - nRes;
          String sAvalToRes = mgr.getASPField("QTY_ASSIGNED").formatNumber(nAvalToRes);

          cmd = transForReserve.addCustomCommand("RESSHORT_" + successCount,"MAINT_MATERIAL_REQ_LINE_API.Make_Reservation_Short");
          cmd.addParameter("QTY_LEFT");
          cmd.addParameter("MAINT_MATERIAL_ORDER_NO", itemset0.getValue("MAINT_MATERIAL_ORDER_NO"));
          cmd.addParameter("LINE_ITEM_NO", itemset0.getValue("LINE_ITEM_NO"));
          cmd.addParameter("LOCATION_NO", "");    
          cmd.addParameter("LOT_BATCH_NO", "");
          cmd.addParameter("SERIAL_NO", "");
          cmd.addParameter("ENG_CHG_LEVEL", "");
          cmd.addParameter("WAIV_DEV_REJ_NO", "");
          cmd.addParameter("ACTIVITY_SEQ", "");
          cmd.addParameter("PROJECT_ID", "");
          cmd.addParameter("QTY_TO_ASSIGN", "");

          successCount++;

          if (itemlay0.isMultirowLayout())
             itemset0.next();
          if (planQty > nAvalToRes)
          {
              mgr.showAlert(mgr.translate("PCMWAMAINTMATERIALREQAVAIL: Available quantity for part ") +itemset0.getRow().getValue("PART_NO") + mgr.translate("PCMWACTIVESEPARATE2AVAIL1:  was ")  +sAvalToRes+ mgr.translate("PCMWACTIVESEPARATE2AVAI2: ."));
          }
          trans.clear();
       }

       trans = mgr.perform(transForReserve);

       itemset0.first();

       for (int i = 0; i <= successCount; i++)
       {
          nQtyShort = trans.getNumberValue("RESSHORT_" + i + "/DATA/QTY_LEFT");

    	  //Bug 76767, Start, Modified the error message  
    	  if (nQtyShort > 0)
    	      mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONCOULDNOTALL: All material could not be allocated for part &1. Remaining quantity to be reserved: &2", itemset0.getValue("PART_NO"), new Double(nQtyShort).toString()));
    	  //Bug 76767, End  

          if (itemlay0.isMultirowLayout())
             itemset0.next();
       }

       if (itemlay0.isMultirowLayout())
          itemset0.setFilterOff();

       headset.goTo(currrow);
       okFindITEM0();
    }

    public void manReserve()
    {
        ASPManager mgr = getASPManager();

        int currrow = headset.getCurrentRowNo();

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());
        else
            itemset0.selectRow();

        trans.clear();

        cmd = trans.addCustomFunction("FINSTATEDEC", "MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__", "DB_STATE"); 
        cmd.addParameter("DB_STATE", "Released"); 

        cmd = trans.addCustomFunction("RESALLOW", "MAINT_MATERIAL_REQ_LINE_API.Is_Reservation_Allowed", "RES_ALLO");
        cmd.addParameter("WO_NO", itemset0.getRow().getFieldValue("ITEM0_WO_NO"));

        trans = mgr.perform(trans);

        String sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DB_STATE");
        String bResAllowed = trans.getValue("RESALLOW/DATA/RES_ALLO");
        String dfStatus = headset.getRow().getValue("STATE");

        if (!("TRUE".equals(bResAllowed)))
        {
            mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONCANNOTSTATWO5: Work order status not valid for material reserve."));
            return ;
        }

        if (!(sStatusCodeReleased.equals(dfStatus)))
        {
            mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONCANNOTSTAT5MAT: Maint Material Requisition not valid for material reserve."));
            return ;
        }

        double plan_qty = itemset0.getRow().getNumberValue("PLAN_QTY");
        if (isNaN(plan_qty))
            plan_qty = 0;

        double qty = itemset0.getRow().getNumberValue("QTY");
        if (isNaN(qty))
            qty = 0;

        double qty_assign  = itemset0.getRow().getNumberValue("QTY_ASSIGNED");
        if (isNaN(qty_assign))
            qty_assign = 0;

        double qty_return = itemset0.getRow().getNumberValue("QTY_RETURNED");
        if (isNaN(qty_return))
            qty_return = 0;

        if (plan_qty > ( qty + qty_assign )) // + qty_return)) * ASSALK  Material Issue & Reserve modification.
        {
            double nQtyLeftNum = plan_qty - qty - qty_assign;
            double nQuantityLeft = nQtyLeftNum;
            // 040226  ARWILK  Begin  (Remove uneccessary global variables)
            bOpenNewWindow = true;

            urlString = "MaterialRequisReservatDlg.page?WO_NO=" + mgr.URLEncode(headset.getValue("WO_NO")) + 
                        "&LINE_ITEM_NO=" + mgr.URLEncode(itemset0.getRow().getValue("LINE_ITEM_NO")) + 
                        "&FRMNAME=" + mgr.URLEncode("maintMatReq") +
                        "&QRYSTR=" + mgr.URLEncode(qrystr) +
                        "&PART_NO=" + mgr.URLEncode(itemset0.getValue("PART_NO")) +
                        "&CONTRACT=" + mgr.URLEncode(itemset0.getValue("SPARE_CONTRACT")) +
                        "&DESCRIPTION=" + mgr.URLEncode(itemset0.getValue("SPAREDESCRIPTION")) +
                        "&CONDITION_CODE=" + mgr.URLEncode(itemset0.getRow().getValue("CONDITION_CODE")) +
                        "&CONDITION_CODE_DESC=" + mgr.URLEncode(itemset0.getRow().getValue("CONDDESC")) +
                        "&QTYLEFT=" + mgr.URLEncode(new Double(nQuantityLeft).toString()) +
                        "&MAINTMATORDNO=" + mgr.URLEncode(itemset0.getValue("MAINT_MATERIAL_ORDER_NO")) +
                        "&CONDITION_CODE=" + mgr.URLEncode(mgr.isEmpty(itemset0.getValue("CONDITION_CODE"))? "" :itemset0.getValue("CONDITION_CODE") ) +
                        "&CONDDESC=" + mgr.URLEncode(itemset0.getRow().getValue("CONDDESC"))+
                        "&OWNERSHIP=" + mgr.URLEncode(itemset0.getRow().getValue("PART_OWNERSHIP")) +
                        "&OWNER=" + mgr.URLEncode(itemset0.getRow().getValue("OWNER")) +
                        "&OWNERNAME=" + mgr.URLEncode(itemset0.getRow().getValue("OWNER_NAME")) ;

            newWinHandle = "manReserve"; 
            // 040226  ARWILK  End  (Remove uneccessary global variables)

            ctx.setGlobal("CURRROW",String.valueOf(currrow));
        }
        else
            mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONCANNOTMAT: No material requirements for selected item."));

        headset.goTo(currrow); 
    }

    public void unreserve()
    {
        ASPManager mgr = getASPManager();

        int currrow = headset.getCurrentRowNo();

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());
        else
            itemset0.selectRow();

        trans.clear();

        cmd = trans.addCustomFunction("FINSTATEDEC", "MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__", "DB_STATE"); 
        cmd.addParameter("DB_STATE", "Released"); 

        trans = mgr.perform(trans);

        String sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DB_STATE");
        String dfStatus = headset.getRow().getValue("STATE");

        if (!(sStatusCodeReleased.equals(dfStatus)))
        {
            mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONCANNOTSTAT6MAT: Maint Material Requisition status not valid for material unreserve."));
            return ;
        }

        double qty_assign = itemset0.getRow().getNumberValue("QTY_ASSIGNED");
        if (isNaN(qty_assign))
            qty_assign = 0;

        if (qty_assign > 0)
        {
            // 040226  ARWILK  Begin  (Remove uneccessary global variables)
            bOpenNewWindow = true;

            urlString = "MaterialRequisReservatDlg2.page?WO_NO=" + mgr.URLEncode(headset.getValue("WO_NO")) + 
                        "&LINE_ITEM_NO=" + mgr.URLEncode(itemset0.getValue("LINE_ITEM_NO")) + 
                        "&FRMNAME=" + mgr.URLEncode("maintMatReq") +
                        "&QRYSTR=" + mgr.URLEncode(qrystr) +
                        "&PART_NO=" + mgr.URLEncode(itemset0.getValue("PART_NO")) +
                        "&CONTRACT=" + mgr.URLEncode(itemset0.getValue("SPARE_CONTRACT")) +
                        "&DESCRIPTION=" + mgr.URLEncode(itemset0.getValue("SPAREDESCRIPTION")) +
                        "&QTYLEFT=" + mgr.URLEncode(String.valueOf(qty_assign)) +
                        "&MAINTMATORDNO=" + mgr.URLEncode(itemset0.getRow().getValue("MAINT_MATERIAL_ORDER_NO"));

            newWinHandle = "unreserve"; 
            // 040226  ARWILK  End  (Remove uneccessary global variables)

            ctx.setGlobal("CURRROW", String.valueOf(currrow));
        }
        else
            mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONCANNOTMAT: No material requirements for selected item."));
    }

    public void availtoreserve()
    {
        ASPManager mgr = getASPManager();

        // 040108  ARWILK  Begin  (Enable Multirow RMB actions)
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

        String sProjectId =  headset.getValue("PROJECT_ID");

        for (int i = 0; i < count; i++)
        {
            rowBuff = mgr.newASPBuffer();

            if (i == 0)
            {
                rowBuff.addItem("CONTRACT", itemset0.getValue("SPARE_CONTRACT"));
                rowBuff.addItem("PART_NO", itemset0.getValue("PART_NO"));
            }
            else
            {
                rowBuff.addItem(null, itemset0.getValue("SPARE_CONTRACT"));
                rowBuff.addItem(null, itemset0.getValue("PART_NO"));
            }

            transferBuffer.addBuffer("DATA", rowBuff);

            if (itemlay0.isMultirowLayout())
                itemset0.next();
        }

        if (itemlay0.isMultirowLayout())
            itemset0.setFilterOff();

        ctx.setGlobal("TRNURL", mgr.getURL());        

        urlString = createTransferUrl("../invenw/InventoryPartInStockOvw.page", transferBuffer);
        newWinHandle = "availToReserve"; 
    }

    public void matReqIssued()
    {
        ASPManager mgr = getASPManager();
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
        String order_no_ = headset.getRow().getValue("WO_NO");

        for (int i = 0; i < count; i++)
        {
            rowBuff = mgr.newASPBuffer();

            if (i == 0)
            {
                rowBuff.addItem("ORDER_NO", order_no_);
                rowBuff.addItem("LINE_ITEM_NO", itemset0.getValue("LINE_ITEM_NO"));
                rowBuff.addItem("TRANSACTION_CODE", "WOISS");
            }
            else
            {
                rowBuff.addItem(null, order_no_);
                rowBuff.addItem(null, itemset0.getValue("LINE_ITEM_NO"));
                rowBuff.addItem(null, "WOISS");
            }

            transferBuffer.addBuffer("DATA", rowBuff);

            if (itemlay0.isMultirowLayout())
                itemset0.next();
        }

        if (itemlay0.isMultirowLayout())
            itemset0.setFilterOff();

        urlString = createTransferUrl("ActiveWorkOrderIssue.page", transferBuffer);
        newWinHandle = "matReqIssueRMB";
    }

    public void issue()
    {
       ASPManager mgr = getASPManager();
       double qtyOnHand = 0.0;
       double nQtyAvblToIssue = 0.0;
       double nTotQtyRes = 0.0;
       double nTotQtyPlanable = 0.0;
       double nCount = 0.0;
       int count = 0;
       int successCount = 0;
       ASPTransactionBuffer transForIssue;
       ASPTransactionBuffer secBuff;
       boolean securityOk = false; 
       boolean canPerform = true;
       secBuff = mgr.newASPTransactionBuffer();
       secBuff.addSecurityQuery("Inventory_Part_In_Stock_API");
       secBuff = mgr.perform(secBuff);

       if ( secBuff.getSecurityInfo().itemExists("Inventory_Part_In_Stock_API") )
          securityOk = true;

       int currrow = headset.getCurrentRowNo();

       trans.clear();

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

          trans.clear();
          cmd = trans.addCustomFunction("FINSTATEDEC","MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__","DB_STATE"); 
          cmd.addParameter("DB_STATE","Released"); 

          cmd = trans.addCustomFunction("ISSALLOW","MAINT_MATERIAL_REQ_LINE_API.Is_Issued_Allowed","ISS_ALLO");
          cmd.addParameter("WO_NO",itemset0.getValue("WO_NO"));

          trans = mgr.perform(trans);
          String sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DB_STATE");
          String bIssAllowed = trans.getValue("ISSALLOW/DATA/ISS_ALLO");
          String dfStatus = headset.getValue("STATE");
          if (!("TRUE".equals(bIssAllowed)))
          {
             mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONCANNOTSTATWO5WO: Work order status not valid for material issue."));
             return ;
          }

          if (!(sStatusCodeReleased.equals(dfStatus)))
          {
             mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONCANNOTSTAT1: Maint Material Requisition status not valid for material issue."));
             return ;
          }

          itemset0.first();
          transForIssue = mgr.newASPTransactionBuffer();

          for (int i = 0; i < count; i++)
          {
                 qtyOnHand = GetInventoryQuantity("ONHAND");;
                 double nRes = GetInventoryQuantity("RESERVED");
                 double qty_assign = itemset0.getRow().getNumberValue("QTY_ASSIGNED");
                 if ( isNaN(qty_assign) )
                        qty_assign = 0;
    
                 double nAvailToIss = qtyOnHand - nRes + qty_assign;
                 String sAvailToIss = mgr.getASPField("QTY_ASSIGNED").formatNumber(nAvailToIss); 

                 double plan_qty1 = itemset0.getNumberValue("PLAN_QTY");
                 if (isNaN(plan_qty1))
                    plan_qty1 = 0;

                 double qty1 = itemset0.getNumberValue("QTY");
                 if (isNaN(qty1))
                    qty1 = 0;

                 double qty_outstanding = plan_qty1 - qty1; //(qty + qty_return); * ASSALK  Material Issue & Reserve modification.

                 if ( qty_outstanding > 0 )
                    canPerform = true;

                 if (!canPerform){
                     mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONCANNOTMAT1: No material requirements for selected item."));
                     return;
                 }
                 else
                 {
                    trans.clear();

                    if (plan_qty1 > nAvailToIss)
                        mgr.showAlert(mgr.translate("PCMWAMAINTMATERIALREQAVAIL: Available quantity for part ") +itemset0.getRow().getValue("PART_NO") + mgr.translate("PCMWACTIVESEPARATE2AVAIL1:  was ")  +sAvailToIss+ mgr.translate("PCMWACTIVESEPARATE2AVAI2: .")); 

                    if ( securityOk )
                    {
                       cmd = transForIssue.addCustomFunction("INVONHANDRES"+successCount,"Inventory_Part_In_Stock_API.Get_Sales_Plannable_Qty_Res","QTYRES");
                       cmd.addParameter("CONTRACT",itemset0.getValue("SPARE_CONTRACT"));
                       cmd.addParameter("PART_NO",itemset0.getValue("PART_NO"));
                       cmd.addParameter("CONFIGURATION","");
                    }
                    if ( securityOk )
                    {
                       cmd = transForIssue.addCustomFunction("INVONHANDPLAN"+successCount,"Inventory_Part_In_Stock_API.Get_Sales_Plannable_Qty_Onhand","QTYPLANNABLE");
                       cmd.addParameter("CONTRACT",itemset0.getValue("SPARE_CONTRACT"));
                       cmd.addParameter("PART_NO",itemset0.getValue("PART_NO"));
                       cmd.addParameter("CONFIGURATION","");
                    }
                    cmd = transForIssue.addCustomFunction("AUTOREP"+ successCount,"MAINTENANCE_INV_PART_API.Get_Auto_Repairable","AUTO_REPAIRABLE");
                    cmd.addParameter("CONTRACT",headset.getValue("CONTRACT"));
                    cmd.addParameter("PART_NO",itemset0.getValue("PART_NO"));

                    cmd = transForIssue.addCustomFunction("REPAIR"+ successCount,"MAINTENANCE_INV_PART_API.Get_Repairable","REPAIRABLE");
                    cmd.addParameter("SPARE_CONTRACT",itemset0.getValue("SPARE_CONTRACT"));
                    cmd.addParameter("PART_NO",itemset0.getValue("PART_NO"));

                    //Bug 56688, Replaced Make_Issue_Detail with Make_Auto_Issue_Detail.
                    cmd = transForIssue.addCustomCommand("MAKEISSUDETA" + successCount,"MAINT_MATERIAL_REQ_LINE_API.Make_Auto_Issue_Detail");
                    cmd.addParameter("DUMMY_ACT_QTY_ISSUED");
                    cmd.addParameter("ITEM0_MAINT_MATERIAL_ORDER_NO",itemset0.getRow().getFieldValue("ITEM0_MAINT_MATERIAL_ORDER_NO"));
                    cmd.addParameter("LINE_ITEM_NO",itemset0.getRow().getValue("LINE_ITEM_NO"));
                    cmd.addParameter("LOCATION_NO","");    
                    cmd.addParameter("LOT_BATCH_NO","");
                    cmd.addParameter("SERIAL_NO","");
                    cmd.addParameter("ENG_CHG_LEVEL","");
                    cmd.addParameter("WAIV_DEV_REJ_NO","");
                    cmd.addParameter("PROJECT_ID","");
                    cmd.addParameter("ACTIVITY_SEQ","");
                    cmd.addParameter("QTY_TO_ISSUE","");
                    //Bug 56688, End

                    successCount++;

                 }

                 if (itemlay0.isMultirowLayout())
                    itemset0.next();
                 trans.clear();
             }


             trans = mgr.perform(transForIssue);

             itemset0.first();

             for (int i = 0; i < successCount; i++)
             {
        		 //Bug 76767, Start  
        		 double nQtyIssued = trans.getNumberValue("MAKEISSUDETA" + i + "/DATA/DUMMY_ACT_QTY_ISSUED");
        		 double nQtyShort = itemset0.getNumberValue("PLAN_QTY") - nQtyIssued;

        		 if (nQtyShort > 0)
        		    mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONCANTISSUE: All material could not be issued for part &1. Remaining quantity to be issued: &2", itemset0.getValue("PART_NO"), new Double(nQtyShort).toString()));
        		 //Bug 76767, End

                 String isAutoRepairable = trans.getValue("AUTOREP"+i+"/DATA/AUTO_REPAIRABLE");
                 String isRepairable = trans.getValue("REPAIR"+i+"/DATA/REPAIRABLE");
                 if ( securityOk)
                    nTotQtyRes = trans.getNumberValue("INVONHANDRES"+i+"/DATA/QTYRES");
                 else
                    nTotQtyRes = 0;

                 if ( isNaN(nTotQtyRes) )
                    nTotQtyRes = 0;
                 if ( securityOk)
                    nTotQtyPlanable = trans.getNumberValue("INVONHANDPLAN"+i+"/DATA/QTYPLANNABLE");
                 else
                    nTotQtyPlanable = 0;

                 if ( isNaN(nTotQtyPlanable) )
                    nTotQtyPlanable = 0;

                 if (( "TRUE".equals(isAutoRepairable) ) &&  ( "TRUE".equals(isRepairable) ) )
                 {
                     double plan_qty = itemset0.getNumberValue("PLAN_QTY");
                     if (isNaN(plan_qty))
                        plan_qty = 0;

                     double qty = itemset0.getNumberValue("QTY");
                     if (isNaN(qty))
                        qty = 0;

                     double qty_assign = itemset0.getNumberValue("QTY_ASSIGNED");
                     if ( isNaN(qty_assign) )
                        qty_assign = 0;

                    double nQtyPlanToIssue = (plan_qty - qty);
                    double nAvablQty =  (nTotQtyPlanable - nTotQtyRes);
                    openCreRepNonSer = "FALSE";
                    if (qty_assign==0)
                    {
                       if (nQtyPlanToIssue <= nAvablQty)
                       {
                          openCreRepNonSer = "TRUE";
                          nCount = nQtyPlanToIssue;
                       }
                       else
                       {
                          openCreRepNonSer = "TRUE";
                          nCount = nAvablQty;
                       }

                    }
                    else if (qty_assign > 0 )
                    {

                       if (qty_assign == plan_qty)
                       {
                          openCreRepNonSer = "TRUE";
                          nCount = qty_assign;
                       }
                       else if ((qty_assign < plan_qty))
                       {
                          if ((nAvablQty == 0))
                          {
                             openCreRepNonSer = "TRUE";
                             nCount = qty_assign;
                          }
                          else if ((nAvablQty >= (plan_qty - qty_assign)))
                          {
                             openCreRepNonSer = "TRUE";
                             nCount = plan_qty;
                          }
                          else if ((nAvablQty < (plan_qty - qty_assign)))
                          {
                             openCreRepNonSer = "TRUE";
                             nCount = (qty_assign + nAvablQty);
                          }
                       }
                    }
                    if (nCount == 0)
                    {
                       openCreRepNonSer = "FALSE";
                    }
                    creRepNonSerPath = "CreateRepairWorkOrderForNonSerialParts.page?WO_NO="+mgr.URLEncode(headset.getValue("WO_NO"))+      
                                       "&CONTRACT="+mgr.URLEncode(headset.getValue("CONTRACT"))+
                                       "&MCH_CODE="+mgr.URLEncode(headset.getValue("MCH_CODE"))+
                                       "&DESCRIPTION="+mgr.URLEncode(headset.getValue("MCH_CODE_DESCRIPTION"))+
                                       "&PART_NO="+mgr.URLEncode(itemset0.getValue("PART_NO"))+
                                       "&SPAREDESCRIPTION="+mgr.URLEncode(itemset0.getValue("SPAREDESCRIPTION"))+
                                       "&SPARE_CONTRACT="+mgr.URLEncode(itemset0.getValue("SPARE_CONTRACT"))+
                                       "&COUNT="+nCount;
                 }
                 if (itemlay0.isMultirowLayout())
                       itemset0.next();
             }


         if (itemlay0.isMultirowLayout())
            itemset0.setFilterOff();

         headset.goTo(currrow);
         okFindITEM0();

    }

    public void manIssue()
    {
        ASPManager mgr = getASPManager();

        int currrow = headset.getCurrentRowNo();

        String creRepWO = "FALSE";

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());
        else
            itemset0.selectRow();

        trans.clear();

        cmd = trans.addCustomFunction("FINSTATEDEC","MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__","DB_STATE"); 
        cmd.addParameter("DB_STATE","Released"); 

        cmd = trans.addCustomFunction("SERIALTRA","PART_CATALOG_API.Get_Serial_Tracking_Code_Db","SERIAL_TRACK");
        cmd.addParameter("PART_NO",itemset0.getRow().getValue("PART_NO"));

        cmd = trans.addCustomFunction("AUTOREP","MAINTENANCE_INV_PART_API.Get_Auto_Repairable","AUTO_REPAIRABLE");
        cmd.addParameter("SPARE_CONTRACT",itemset0.getRow().getValue("SPARE_CONTRACT"));
        cmd.addParameter("PART_NO",itemset0.getRow().getValue("PART_NO"));

        cmd = trans.addCustomFunction("REPAIR","MAINTENANCE_INV_PART_API.Get_Repairable","REPAIRABLE");
        cmd.addParameter("SPARE_CONTRACT",itemset0.getRow().getValue("SPARE_CONTRACT"));
        cmd.addParameter("PART_NO",itemset0.getRow().getValue("PART_NO"));

        cmd = trans.addCustomFunction("ISSALLOW","MAINT_MATERIAL_REQ_LINE_API.Is_Issued_Allowed","ISS_ALLO");
        cmd.addParameter("WO_NO",itemset0.getRow().getFieldValue("ITEM0_WO_NO"));

        trans = mgr.perform(trans);

        String sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DB_STATE");
        String hasSerialNum = trans.getValue("SERIALTRA/DATA/SERIAL_TRACK");
        String isAutoRepairable = trans.getValue("AUTOREP/DATA/AUTO_REPAIRABLE");
        String isRepairable = trans.getValue("REPAIR/DATA/REPAIRABLE");
        String bIssAllowed = trans.getValue("ISSALLOW/DATA/ISS_ALLO");
        String dfStatus = headset.getRow().getValue("STATE");

        if (!("TRUE".equals(bIssAllowed)))
        {
            mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONCANNOTSTATWO5WO: Work order status not valid for material issue."));
            return ;
        }

        if (!(sStatusCodeReleased.equals(dfStatus)))
        {
            mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONMATISSUE: Maint Material Requisition status not valid for material issue."));
            return ;
        }

        double plan_qty = itemset0.getRow().getNumberValue("PLAN_QTY");
        if (isNaN(plan_qty))
            plan_qty = 0;

        double qty = itemset0.getRow().getNumberValue("QTY");
        if (isNaN(qty))
            qty = 0;

        double qty_return = itemset0.getRow().getNumberValue("QTY_RETURNED");
        if ( isNaN(qty_return) )
            qty_return = 0;

        double qty_outstanding = plan_qty - qty; //(qty + qty_return); * ASSALK  Material Issue & Reserve modification.

        if ( qty_outstanding > 0 )
        {
            double nQtyLeftNum = plan_qty - qty;

            if (nQtyLeftNum < 0)
                nQtyLeft = "0";
            else
                nQtyLeft    = String.valueOf(nQtyLeftNum);   

            if (("TRUE".equals(isAutoRepairable)) && ("TRUE".equals(isRepairable)) && ("NOT SERIAL TRACKING".equals(hasSerialNum)))
                creRepWO = "TRUE";

            // 040226  ARWILK  Begin  (Remove uneccessary global variables)
            bOpenNewWindow = true;

            urlString = "InventoryPartLocationDlg.page?PART_NO=" + mgr.URLEncode(itemset0.getValue("PART_NO")) + 
                        "&CONTRACT=" + mgr.URLEncode(itemset0.getValue("SPARE_CONTRACT")) + 
                        "&FRMNAME=" + mgr.URLEncode("maintMatReq") +
                        "&QRYSTR=" + mgr.URLEncode(qrystr) +
                        "&WO_NO=" + mgr.URLEncode(itemset0.getValue("WO_NO")) +
                        "&LINE_ITEM_NO=" + mgr.URLEncode(itemset0.getValue("LINE_ITEM_NO")) +
                        "&DESCRIPTION=" + mgr.URLEncode(itemset0.getValue("SPAREDESCRIPTION")) +
                        "&HEAD_CONDITION_CODE=" + mgr.URLEncode(itemset0.getRow().getValue("CONDITION_CODE")) +
                        "&HEAD_CONDITION_CODE_DESC=" + mgr.URLEncode(itemset0.getRow().getValue("CONDDESC")) +
                        "&QTYLEFT=" + mgr.URLEncode(nQtyLeft) +
                        "&MAINTMATORDNO=" + mgr.URLEncode(itemset0.getValue("MAINT_MATERIAL_ORDER_NO"))+
                        "&OWNERSHIP=" + mgr.URLEncode(itemset0.getRow().getValue("PART_OWNERSHIP")) +
                        "&OWNER=" + mgr.URLEncode(itemset0.getRow().getValue("OWNER")) +
                        "&OWNERNAME=" + mgr.URLEncode(itemset0.getRow().getValue("OWNER_NAME"));

            newWinHandle = "manIssue"; 
            // 040226  ARWILK  End  (Remove uneccessary global variables)

            ctx.setGlobal("CURRROW", String.valueOf(currrow));
        }
        else
            mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONCANNOTMAT: No material requirements for selected item."));

        headset.goTo(currrow);
    }

    public void matReqUnissue()
    {
        ASPManager mgr = getASPManager();

        int currrow = headset.getCurrentRowNo();

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());
        else
            itemset0.selectRow();

        trans.clear();

        cmd = trans.addCustomFunction("FINSTATEDEC", "MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__", "DB_STATE"); 
        cmd.addParameter("DB_STATE", "Released"); 

        cmd = trans.addCustomFunction("OSTATE","Active_Separate_API.Get_Obj_State","STATE");  
        cmd.addParameter("WO_NO",headset.getRow().getValue("WO_NO")); 

        trans = mgr.perform(trans);

        String objState  =trans.getBuffer("OSTATE/DATA").getFieldValue("STATE");

        String sStatusCodeReleased = trans.getValue("FINSTATEDEC/DATA/DB_STATE");
        String dfStatus = headset.getRow().getValue("STATE");

        double qty = itemset0.getRow().getNumberValue("QTY");

        if (isNaN(qty))
            qty = 0;

        if (qty > 0)
        {
            if (!(( "WORKDONE".equals(objState) )|| ( "REPORTED".equals(objState) )|| ( "STARTED".equals(objState) )|| ( "RELEASED".equals(objState) )))
            {
                mgr.showAlert(mgr.translate("PCMWMAINTMATERIALREQUISITIONMATUNISSUE: Maint Material Requisition status not valid for material Unissue."));
                return ;
            }
            else
            {
                // 040226  ARWILK  Begin  (Remove uneccessary global variables)
                bOpenNewWindow = true;

                urlString = "ActiveWorkOrder.page?WO_NO=" + mgr.URLEncode(itemset0.getRow().getFieldValue("ITEM0_WO_NO")) + 
                            "&MAINTMATORDNO=" + mgr.URLEncode(itemset0.getRow().getFieldValue("ITEM0_MAINT_MATERIAL_ORDER_NO")) + 
                            "&FRMNAME=" + mgr.URLEncode("maintMatReq") +
                            "&QRYSTR=" + mgr.URLEncode(qrystr) +
                            "&PART_NO=" + mgr.URLEncode(itemset0.getRow().getValue("PART_NO")) +
                            "&CONTRACT=" + mgr.URLEncode(itemset0.getRow().getValue("SPARE_CONTRACT"))+
                            "&LINE_ITEM_NO="+ mgr.URLEncode(itemset0.getRow().getValue("LINE_ITEM_NO"));

                newWinHandle = "matReqUnissue"; 
                // 040226  ARWILK  End  (Remove uneccessary global variables)

                ctx.setGlobal("CURRROWGLOBAL",String.valueOf(currrow));
                ctx.setGlobal("HEADCURR", String.valueOf(headset.getCurrentRowNo()));
                ctx.setGlobal("ITEMCURR", String.valueOf(itemset0.getCurrentRowNo()));
            }
        }
        else
        {
            mgr.showAlert(mgr.translate("PCMWACTIVESEPARATEREPORTINWORKORDERSMNOISSUEMATERIAL: Cannot perform Material Requisition Unissue on this record."));
        }

        headset.goTo(currrow); 
    }
    public double GetInventoryQuantity(String sQtyType)
    {
        ASPManager mgr = getASPManager();

        //isSecure = new String[15];
        String sOnhand = "ONHAND";
        String sPicking = "PICKING";
        String sF = "F" ;
        String sPallet = "PALLET"  ;
        String sDeep = "DEEP" ;
        String sBuffer = "BUFFER" ;
        String sDelivery = "DELIVERY"  ;
        String sShipment = "SHIPMENT"  ;
        String sManufacturing = "MANUFACTURING" ;
        String sOwnershipDb = "COMPANY OWNED"  ;
        String sClientOwnershipConsignment = "CONSIGNMENT"  ;
        double nQty = 0.0;
        String sTrue = "TRUE";
        String sFalse = "FALSE";

        ASPTransactionBuffer secBuff;
        boolean securityOk = false;
        secBuff = mgr.newASPTransactionBuffer();
        secBuff.addSecurityQuery("Inventory_Part_In_Stock_API");
        secBuff = mgr.perform(secBuff);

        if ( secBuff.getSecurityInfo().itemExists("Inventory_Part_In_Stock_API") )
            securityOk = true;

        trans.clear();

        cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
        cmd.addParameter("PART_OWNERSHIP",itemset0.getRow().getValue("PART_OWNERSHIP"));
        trans = mgr.validate(trans);
        String sClientOwnershipDefault = trans.getValue("VALIDOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");
        trans.clear();

        if ( securityOk )
        {
            trans.clear();
            cmd = trans.addCustomFunction("MAINTREQSUP","MAINT_MAT_REQ_SUP_API.DECODE('INVENT_ORDER')","SUPPLY_CODE");
            trans = mgr.validate(trans);
            String sStandardInv = trans.getValue("MAINTREQSUP/DATA/SUPPLY_CODE");
            trans.clear();

            if ("COMPANY OWNED".equals(sClientOwnershipDefault))
                sClientOwnershipConsignment="CONSIGNMENT";
            else
                sClientOwnershipConsignment=null;

            if (sStandardInv.equals(itemset0.getRow().getValue("SUPPLY_CODE")))
            {
                trans.clear();
                cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                cmd.addParameter("CONTRACT",itemset0.getRow().getFieldValue("SPARE_CONTRACT"));
                cmd.addParameter("PART_NO",itemset0.getRow().getValue("PART_NO"));
                cmd.addParameter("CONFIGURATION",itemset0.getRow().getValue("CONFIGURATION"));
                cmd.addParameter("QTY_TYPE",sQtyType);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL");
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
                cmd.addParameter("OWNERSHIP_TYPE3");
                cmd.addParameter("OWNERSHIP_TYPE4");
                cmd.addParameter("OWNER");
                cmd.addParameter("OWNER_VENDOR");
                cmd.addParameter("LOCATION_TYPE1",sPicking);
                cmd.addParameter("LOCATION_TYPE2",sF);
                cmd.addParameter("LOCATION_TYPE3",sPallet);
                cmd.addParameter("LOCATION_TYPE4",sDeep);
                cmd.addParameter("LOCATION_TYPE5",sBuffer);
                cmd.addParameter("LOCATION_TYPE6",sDelivery);
                cmd.addParameter("LOCATION_TYPE7",sShipment);
                cmd.addParameter("LOCATION_TYPE8",sManufacturing);
                cmd.addParameter("LOT_BATCH_NO");
                cmd.addParameter("SERIAL_NO");
                cmd.addParameter("ENG_CHG_LEVEL");
                cmd.addParameter("WAIV_DEV_REJ_NO");
                cmd.addParameter("INCLUDE_STANDARD",sTrue);
                cmd.addParameter("INCLUDE_PROJECT",sFalse);
                cmd.addParameter("ITEM_ACTIVITY_SEQ","0");
                cmd.addParameter("PROJECT_ID","");
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                //cmd.addParameter("CONDITION_CODE");
                cmd.addParameter("CONDITION_CODE",itemset0.getRow().getFieldValue("CONDITION_CODE"));

            }
            else
            {
                trans.clear();
                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))
                {
                    cmd = trans.addCustomFunction("PROJID","Activity_API.Get_Project_Id","PROJECT_ID");
                    cmd.addParameter("ITEM_ACTIVITY_SEQ",mgr.readValue("ITEM_ACTIVITY_SEQ"));
                }
                //Bug 66456, End

                cmd = trans.addCustomFunction("INVONHAND","Inventory_Part_In_Stock_API.Get_Inventory_Quantity","QTYONHAND");
                cmd.addParameter("CONTRACT",itemset0.getRow().getFieldValue("SPARE_CONTRACT"));
                cmd.addParameter("PART_NO",itemset0.getRow().getValue("PART_NO"));
                cmd.addParameter("CONFIGURATION");
                cmd.addParameter("QTY_TYPE",sQtyType);
                cmd.addParameter("EXPIRATION");
                cmd.addParameter("SUPPLY_CONTROL");
                cmd.addParameter("PART_OWNERSHIP_DB",sClientOwnershipDefault);
                cmd.addParameter("OWNERSHIP_TYPE2",sClientOwnershipConsignment);
                cmd.addParameter("OWNERSHIP_TYPE3");
                cmd.addParameter("OWNERSHIP_TYPE4");
                cmd.addParameter("OWNER");
                cmd.addParameter("OWNER_VENDOR");
                cmd.addParameter("LOCATION_TYPE1",sPicking);
                cmd.addParameter("LOCATION_TYPE2",sF);
                cmd.addParameter("LOCATION_TYPE3",sPallet);
                cmd.addParameter("LOCATION_TYPE4",sDeep);
                cmd.addParameter("LOCATION_TYPE5",sBuffer);
                cmd.addParameter("LOCATION_TYPE6",sDelivery);
                cmd.addParameter("LOCATION_TYPE7",sShipment);
                cmd.addParameter("LOCATION_TYPE8",sManufacturing);
                cmd.addParameter("LOT_BATCH_NO");
                cmd.addParameter("SERIAL_NO");
                cmd.addParameter("ENG_CHG_LEVEL");
                cmd.addParameter("WAIV_DEV_REJ_NO");
                cmd.addParameter("INCLUDE_STANDARD",sFalse);
                cmd.addParameter("INCLUDE_PROJECT",sTrue);
                cmd.addParameter("ITEM_ACTIVITY_SEQ","0");
                //Bug 66456, Start, Added check on PROJ
                if (mgr.isModuleInstalled("PROJ"))
                    cmd.addReference("PROJECT_ID", "PROJID/DATA");
                else
                    cmd.addParameter("PROJECT_ID", "");
                //Bug 66456, End
                cmd.addParameter("LOCATION_NO");
                cmd.addParameter("ORDER_ISSUE");
                cmd.addParameter("AUTOMAT_RESERV");
                cmd.addParameter("MANUAL_RESERV");
                //cmd.addParameter("CONDITION_CODE");
                cmd.addParameter("CONDITION_CODE",itemset0.getRow().getFieldValue("CONDITION_CODE"));
            }    
        }
        trans = mgr.perform(trans);


        if ( securityOk )
        {
            nQty = trans.getNumberValue("INVONHAND/DATA/QTYONHAND");
            if ( isNaN(nQty) )
                nQty = 0;
        }
        else
        {
            nQty = 0.0;
        }

        return nQty;
    }

    public void availDetail()
    {
        ASPManager mgr = getASPManager();
        String sProjectId;

        // 040226  ARWILK  Begin  (Enable Multirow RMB actions)
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

        cmd = trans.addCustomFunction("GETPROJINV","MAINT_MAT_REQ_SUP_API.DECODE('PROJECT_INVENTORY')","SUPPLY_CODE");

        trans = mgr.perform(trans);

        if ((trans.getValue("GETPROJINV/DATA/SUPPLY_CODE")).equals(itemset0.getValue("SUPPLY_CODE")))
            sProjectId = itemset0.getValue("PROJECT_ID");
        else
            sProjectId = "*";

        transferBuffer = mgr.newASPBuffer();

        for (int i = 0; i < count; i++)
        {
            rowBuff = mgr.newASPBuffer();

            if (i == 0)
            {
                rowBuff.addItem("PART_NO", itemset0.getValue("PART_NO"));
                rowBuff.addItem("CONTRACT", itemset0.getValue("SPARE_CONTRACT"));
                rowBuff.addItem("PROJECT_ID", sProjectId);
                rowBuff.addItem("CONFIGURATION_ID", "*");
            }
            else
            {
                rowBuff.addItem(null, itemset0.getValue("PART_NO"));
                rowBuff.addItem(null, itemset0.getValue("SPARE_CONTRACT"));
                rowBuff.addItem(null, sProjectId);
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
        // 040226  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void supPerPart()
    {
        ASPManager mgr = getASPManager();

        // 040226  ARWILK  Begin  (Enable Multirow RMB actions)
        if (itemlay0.isMultirowLayout())
            itemset0.store();
        else
        {
            itemset0.unselectRows();
            itemset0.selectRow();
        }

        bOpenNewWindow = true;
        urlString = createTransferUrl("../purchw/PurchasePartSupplier.page", itemset0.getSelectedRows("PART_NO"));
        newWinHandle = "supPerPart"; 
        // 040226  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void openCreWOforNonSerial()
    {
        ASPManager mgr = getASPManager();

        openCreRepNonSer = "TRUE";
        creRepNonSerPath = "CreateRepairWorkOrderForNonSerialParts.page?WO_NO="+mgr.URLEncode(mgr.readValue("WONO",""))+
                           "&MCH_CODE="+mgr.URLEncode(headset.getValue("MCH_CODE"))+
                           "&MCH_DESCRIPTION="+mgr.URLEncode(headset.getValue("DESCRIPTION"))+
                           "&CONTRACT="+mgr.URLEncode(headset.getValue("CONTRACT"))+
                           "&PART_NO="+mgr.URLEncode(mgr.readValue("PARTNO",""))+
                           "&SPAREDESCRIPTION="+mgr.URLEncode(mgr.readValue("PARTDESC",""))+
                           "&SPARE_CONTRACT="+mgr.URLEncode(mgr.readValue("SPARECONTRACT",""))+
                           "&COUNT="+mgr.URLEncode(mgr.readValue("COUNT",""));                                                                                                                                                                                                                                                                                    
    }

    public void preDefine()
    {
        ASPManager mgr = getASPManager();


        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("OBJSTATE");
        f.setHidden();

        f = headblk.addField("OBJEVENTS");
        f.setHidden();

        f = headblk.addField("MAINT_MATERIAL_ORDER_NO","Number","#");
        f.setSize(12);
        f.setReadOnly();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONMAINT_MATERIAL_ORDER_NO: Order No");

        f = headblk.addField("CONTRACT");
        f.setSize(10);
        f.setMaxLength(5);
        f.setReadOnly();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONREQCONTRACT: Requisition Site");
        f.setLOV("../mscomw/UserAllowedSitesLov.page","COMPANY");
        f.setUpperCase();
        f.setInsertable();
        f.setMandatory();

        f = headblk.addField("WO_NO","Number","#");
        f.setSize(11);
        f.setReadOnly();
        f.setMaxLength(8);
        f.setHyperlink("ActiveSeparate2.page","WO_NO","NEWWIN");
        f.setInsertable();
        f.setCustomValidation("WO_NO","DUE_DATE,NPREACCOUNTINGID,WO_CONTRACT,COMPANY,MCH_CODE,DESCRIPTION,CONTRACT,MCH_CONTRACT");
        f.setDynamicLOV("ACTIVE_WO_NOT_REPORTED",600,445);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONWO_NO: WO No");
        
        f = headblk.addField("WO_CONTRACT");
        f.setSize(10);
        f.setReadOnly();
        f.setMaxLength(20);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONWOSITE: WO Site");
        f.setFunction("WORK_ORDER_API.Get_Contract(:WO_NO)");

        f = headblk.addField("MCH_CODE");
        f.setSize(13);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONMCHCODE: Object ID");
        f.setReadOnly();
        f.setMaxLength(100);
        f.setHyperlink("../equipw/EquipmentAllObjectLight.page","MCH_CODE,CONTRACT","NEWWIN");
        f.setFunction("WORK_ORDER_API.Get_Mch_Code(:WO_NO)");
        f.setCustomValidation("MCH_CODE,CONTRACT","DESCRIPTION");
        f.setUpperCase();

        f = headblk.addField("DESCRIPTION");
        f.setSize(30);
        f.setFunction("Maintenance_Object_API.Get_Mch_Name(:CONTRACT,WORK_ORDER_API.Get_Mch_Code(:WO_NO))");
        f.setLabel("PCMWMAINTMATERIALREQUISITIONDESCRIPTION: Description");
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setReadOnly();

        f = headblk.addField("MCH_CONTRACT");
        f.setSize(10);
        f.setReadOnly();
        f.setMaxLength(20);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONOBJSITE: Object Site");
        f.setFunction("WORK_ORDER_API.Get_Mch_Code_Contract(:WO_NO)");

        f = headblk.addField("SIGNATURE");
        f.setSize(8);
        f.setMaxLength(20);
        f.setCustomValidation("SIGNATURE,COMPANY","SIGNATURE_ID,SIGNATURENAME");
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONSIGNATURE: Signature");
        f.setUpperCase();

        f = headblk.addField("SIGNATURENAME");
        f.setSize(15);
        f.setMaxLength(2000);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setFunction("EMPLOYEE_API.Get_Employee_Info(Site_API.Get_Company(:CONTRACT),:SIGNATURE_ID)");

        f = headblk.addField("ENTERED","Date");
        f.setSize(15);
        f.setReadOnly(      );
        f.setLabel("PCMWMAINTMATERIALREQUISITIONENTERED: Entered");

        f = headblk.addField("INT_DESTINATION_ID");
        f.setSize(8);
        f.setMaxLength(30);
        f.setUpperCase();
        f.setCustomValidation("INT_DESTINATION_ID,CONTRACT","INT_DESTINATION_DESC");
        f.setDynamicLOV("INTERNAL_DESTINATION_LOV","CONTRACT",600,445);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONINT_DESTINATION_ID: Int Destination");

        f = headblk.addField("INT_DESTINATION_DESC");
        f.setSize(20);
        f.setDefaultNotVisible();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONINT_DESTINATION_DESC: Description");
        f.setMaxLength(2000);

        f = headblk.addField("DUE_DATE","Date");
        f.setSize(15);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONDUE_DATE: Due Date");

        f = headblk.addField("STATE");
        f.setSize(10);
        f.setReadOnly();
        f.setMaxLength(20);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONSTATE: Status");

        f = headblk.addField("NREQUISITIONVALUE", "Number");
        f.setReadOnly(); 
        f.setSize(8);
        f.setHidden();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONNREQUISITIONVALUE: Total Value");
        f.setFunction("''");

        f = headblk.addField("SNOTETEXT");
        f.setSize(15);
        f.setReadOnly();
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("SIGNATURE_ID");
        f.setSize(6);
        f.setReadOnly();
        f.setMaxLength(11);
        f.setHidden();
        f.setUpperCase();

        f = headblk.addField("NNOTEID", "Number");
        f.setSize(6);
        f.setHidden();
        f.setReadOnly();
        f.setMaxLength(10);
        f.setFunction("''");

        f = headblk.addField("ORDERCLASS");
        f.setSize(3);
        f.setHidden();
        f.setReadOnly();
        f.setMaxLength(3);
        f.setFunction("''");

        f = headblk.addField("SNOTEIDEXIST");
        f.setSize(4);
        f.setHidden();
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setFunction("''");

        f = headblk.addField("COMPANY");
        f.setSize(6);
        f.setHidden();
        f.setMaxLength(20);
        f.setFunction("Site_API.Get_Company(WORK_ORDER_API.Get_Contract(:WO_NO))");
        f.setUpperCase();

        f = headblk.addField("ACTIVITY_SEQ");
        f.setHidden();

        f = headblk.addField("PROJECT_ID");
        //Bug 66456, Start, Added check on PROJ
        if (mgr.isModuleInstalled("PROJ"))
            f.setFunction("Activity_API.Get_Project_Id(:ACTIVITY_SEQ)");
        else
            f.setFunction("''");
        //Bug 66456, End
        f.setHidden();

        f = headblk.addField("CODE_A");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("CODE_B");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("CODE_C");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("CODE_D");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("CODE_E");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("CODE_F");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("CODE_G");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("CODE_H");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("CODE_I");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("CODE_J");
        f.setFunction("''");
        f.setHidden();


        f = headblk.addField("STR_CODE");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("CONTROL_TYPE");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("SYS_DATE","Date");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("NPREACCOUNTINGID", "Number");
        f.setSize(11);
        f.setHidden();
        f.setReadOnly();
        f.setMaxLength(10);
        f.setFunction("Active_Work_Order_API.Get_Pre_Accounting_Id(:WO_NO)");

        f = headblk.addField("INCLUDE_STANDARD");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("INCLUDE_PROJECT");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("CONFIGURATION_ID");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("EXIST");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("ORDER_LIST");
        f.setFunction("''");
        f.setHidden();

        headblk.setView("MAINT_MATERIAL_REQUISITION");
        headblk.defineCommand("MAINT_MATERIAL_REQUISITION_API", "New__,Modify__,Remove__,PLAN__,RELEASE__,CLOSE__");
        headset = headblk.getASPRowSet();

        headtbl = mgr.newASPTable(headblk);
        // 040226  ARWILK  Begin  (Enable Multirow RMB actions)
        headtbl.enableRowSelect();
        // 040226  ARWILK  End  (Enable Multirow RMB actions)

        headbar = mgr.newASPCommandBar(headblk);
        headbar.addCustomCommand("plan", mgr.translate("PCMWMAINTMATERIALREQUISITIONPLANCONS: Plan"));
        headbar.addCustomCommand("release", mgr.translate("PCMWMAINTMATERIALREQUISITIONRELEA: Release"));
        headbar.addCustomCommand("close", mgr.translate("PCMWMAINTMATERIALREQUISITIONCLOS: Close"));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("printPicList", mgr.translate("PCMWMAINTMATERIALREQUISITIONPICLSTMAT: Pick List For Material Requistion - Printout..."));

        headbar.defineCommand(headbar.SAVERETURN, "saveReturn", "checkHeadFields()");
        headbar.defineCommand(headbar.SAVENEW, null, "checkHeadFields()");

        // 040226  ARWILK  Begin  (Enable Multirow RMB actions)
        headbar.enableMultirowAction();

        headbar.addCommandValidConditions("plan", "OBJSTATE", "Enable", "Released");
        headbar.addCommandValidConditions("release", "OBJSTATE", "Enable", "Planned");
        headbar.addCommandValidConditions("close", "OBJSTATE", "Enable", "Planned;Released");
        // 040226  ARWILK  End  (Enable Multirow RMB actions)
        headbar.addCustomCommand("refreshForm","");
        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        headlay.setSimple("DESCRIPTION");
        headlay.setSimple("SIGNATURENAME");
        headlay.setSimple("INT_DESTINATION_DESC");
        headlay.setFieldOrder("MAINT_MATERIAL_ORDER_NO,CONTRACT,WO_NO,WO_CONTRACT,MCH_CODE,DESCRIPTION,MCH_CONTRACT,SIGNATURE,SIGNATURENAME,INT_DESTINATION_ID,INT_DESTINATION_DESC,ENTERED,DUE_DATE,STATE");

        //--------------------------------------------------------------------------------------------------
        //------------------------------------       ITEM0 Block      --------------------------------------
        //--------------------------------------------------------------------------------------------------

        itemblk0 = mgr.newASPBlock("ITEM0");

        f = itemblk0.addField("ITEM0_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk0.addField("ITEM0_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk0.addField("LINE_ITEM_NO","Number");
        f.setSize(8);
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONLINE_ITEM_NO: Line No");

        f = itemblk0.addField("PART_NO");
        f.setSize(14);
        f.setReadOnly();
        f.setInsertable();
        f.setMaxLength(25);
        f.setHyperlink("../invenw/InventoryPart.page","PART_NO","NEWWIN"); 
        f.setCustomValidation("PART_NO,SPARE_CONTRACT,CATALOG_CONTRACT,OWNER,CONDITION_CODE,SUPPLY_CODE,ITEM0_MAINT_MATERIAL_ORDER_NO,PART_OWNERSHIP,ITEM0_WO_NO","CATALOG_NO,CATALOGDESC,SCODEA,SCODEB,SCODEC,SCODED,SCODEE,SCODEF,SCODEG,SCODEH,SCODEI,SCODEJ,HASSPARESTRUCTURE,DIMQTY,TYPEDESIGN,QTYONHAND,UNITMEAS,CONDITION_CODE,CONDDESC,QTY_AVAILABLE,ACTIVEIND_DB,PART_OWNERSHIP,PART_OWNERSHIP_DB,OWNER");  
        f.setDynamicLOV("INVENTORY_PART_WO_LOV","SPARE_CONTRACT CONTRACT",600,445);
        f.setMandatory();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONPART_NO: Part No");
        f.setUpperCase();

        f = itemblk0.addField("SPAREDESCRIPTION");
        f.setSize(20);
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMaxLength(2000);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONSPAREDESCRIPTION: Part Description");
        f.setFunction("INVENTORY_PART_API.Get_Description(:SPARE_CONTRACT,:PART_NO)");

        f = itemblk0.addField("CONDITION_CODE");
        f.setLabel("PCMWMAINTMATERIALREQUISITIONCONDITIONCODE: Condition Code");
        f.setSize(15);
        f.setDynamicLOV("CONDITION_CODE",600,445);
        f.setUpperCase(); 
        f.setCustomValidation("CONDITION_CODE,PART_NO,SPARE_CONTRACT,OWNER,PART_OWNERSHIP,SUPPLY_CODE,ITEM0_MAINT_MATERIAL_ORDER_NO,ACTIVITY_SEQ,PLAN_QTY","CONDDESC,QTYONHAND,QTY_AVAILABLE,COST,AMOUNTCOST"); 

        f = itemblk0.addField("CONDDESC");
        f.setLabel("PCMWMAINTMATERIALREQUISITIONCONDDESC: Condition Code Description");
        f.setSize(20);
        f.setMaxLength(50);
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setFunction("CONDITION_CODE_API.Get_Description(:CONDITION_CODE)");

        f = itemblk0.addField("PART_OWNERSHIP");
        f.setSize(25);
        f.setInsertable();
        f.setSelectBox();
        f.enumerateValues("PART_OWNERSHIP_API");
        f.setLabel("PCMWMAINTMATERIALREQUISPARTOWNERSHIP: Ownership"); 
        f.setCustomValidation("PART_OWNERSHIP_DB,PART_NO,SPARE_CONTRACT,OWNER,CONDITION_CODE,SUPPLY_CODE,ITEM0_MAINT_MATERIAL_ORDER_NO,PART_OWNERSHIP","PART_OWNERSHIP_DB,QTYONHAND,QTY_AVAILABLE"); 

        f = itemblk0.addField("PART_OWNERSHIP_DB");
        f.setSize(20);
        f.setHidden();

        f = itemblk0.addField("OWNER");
        f.setSize(15);
        f.setMaxLength(20);
        f.setInsertable();
        f.setLabel("PCMWMAINTMATERIALREQUISPARTOWNER: Owner"); 
        f.setCustomValidation("OWNER,PART_OWNERSHIP,CONDITION_CODE,PART_NO,SPARE_CONTRACT,SUPPLY_CODE,ITEM0_MAINT_MATERIAL_ORDER_NO","OWNER_NAME,WO_CUST,QTYONHAND,QTY_AVAILABLE");       
        f.setDynamicLOV("CUSTOMER_INFO");
        f.setUpperCase();

        f = itemblk0.addField("WO_CUST");
        f.setSize(20);
        f.setHidden();
        f.setFunction("ACTIVE_SEPARATE_API.Get_Customer_No(:ITEM0_WO_NO)");

        f = itemblk0.addField("OWNER_NAME");
        f.setSize(20);
        f.setMaxLength(100);
        f.setReadOnly();
        f.setLabel("PCMWMAINTMATERIALREQUISPARTOWNERNAME: Owner Name");
        f.setFunction("CUSTOMER_INFO_API.Get_Name(:OWNER)");

        f = itemblk0.addField("SPARE_CONTRACT");
        f.setSize(11);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setMandatory();
        f.setDefaultNotVisible(); 
        f.setReadOnly(); 
        f.setMaxLength(5);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONSPARE_CONTRACT: Site");
        f.setUpperCase();

        f = itemblk0.addField("HASSPARESTRUCTURE");
        f.setSize(8);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONHASSPARESTRUCTURE: Structure");
        f.setFunction("Equipment_Spare_Structure_API.Has_Spare_Structure_Boolean(:PART_NO,:SPARE_CONTRACT)");

        f = itemblk0.addField("ITEM0_WO_NO","Number","#");
        f.setSize(17);
        f.setMandatory();
        f.setHidden();
        f.setReadOnly();
        f.setMaxLength(8);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONITEM0_WO_NO: Work Order No");
        f.setDbName("WO_NO");

        f = itemblk0.addField("JOB_ID","Number");
        f.setDynamicLOV("WORK_ORDER_JOB","ITEM0_WO_NO WO_NO",600,445);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONJOBID: Job Id");
        f.setMaxLength(20);

        f = itemblk0.addField("CRAFT_LINE_NO","Number");
        f.setInsertable();
        f.setDynamicLOV("WORK_ORDER_ROLE","WO_NO",600,445);
        f.setLOVProperty("WHERE","PARENT_ROW_NO IS NULL");
        f.setLabel("PCMWMAINTMATERIALREQUISITIONOPRNO: Operation No");
        f.setSize(15);
                
        f = itemblk0.addField("DIMQTY");
        f.setSize(11);
        f.setMaxLength(2000);
        f.setReadOnly();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONDIMQTY: Dimension/Quality");
        f.setFunction("INVENTORY_PART_API.Get_Dim_Quality(:SPARE_CONTRACT,:PART_NO)");

        f = itemblk0.addField("TYPEDESIGN");
        f.setSize(15);
        f.setMaxLength(2000);
        f.setReadOnly();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONTYPEDESIGN: Type Designation");
        f.setFunction("INVENTORY_PART_API.Get_Type_Designation(:SPARE_CONTRACT,:PART_NO)");

        f = itemblk0.addField("DATE_REQUIRED","Date");
        f.setSize(15);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONDATE_REQUIRED: Date Required");

        f = itemblk0.addField("SUPPLY_CODE");
        f.setSize(25);
        f.setMandatory();
        f.setInsertable();
        f.setSelectBox();
        f.setCustomValidation("PART_NO,SPARE_CONTRACT,CATALOG_CONTRACT,OWNER,CONDITION_CODE,SUPPLY_CODE,ITEM0_MAINT_MATERIAL_ORDER_NO,ACTIVITY_SEQ","QTYONHAND,QTY_AVAILABLE"); 
        f.enumerateValues("MAINT_MAT_REQ_SUP_API");
        f.setLabel("PCMWMAINTMATERIALREQUISITIONSUPPLYCODE: Supply Code");
        f.setMaxLength(200);

        f = itemblk0.addField("PLAN_QTY","Number");
        f.setSize(14);
        f.setCustomValidation("PLAN_QTY,PART_NO,SPARE_CONTRACT,CATALOG_NO,CATALOG_CONTRACT,PRICE_LIST_NO,PLAN_LINE_NO,DISCOUNT,ITEM0_WO_NO,COST,LIST_PRICE,ITEMDISCOUNTEDPRICE,SERIAL_NO,CONFIGURATION_IDS,CONDITION_CODE,PART_OWNERSHIP_DB","COST,AMOUNTCOST,PRICE_LIST_NO,DISCOUNT,LIST_PRICE,SALESPRICEAMOUNT,ITEMDISCOUNTEDPRICE");
	f.setMandatory();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONPLAN_QTY: Quantity Required");

        f = itemblk0.addField("QTY","Number");
        f.setSize(13);
        f.setReadOnly();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONQTY: Quantity Issued");

        f = itemblk0.addField("QTY_SHORT","Number");
        f.setSize(11);
        f.setMandatory();
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setHidden();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONQTY_SHORT: Quantity Short");

        f = itemblk0.addField("QTYONHAND","Number");
        f.setSize(17);
        f.setReadOnly();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONQTYONHAND: Quantity on Hand");
        if (mgr.isModuleInstalled("INVENT"))
            f.setFunction("Maint_Material_Req_Line_Api.Get_Qty_On_Hand(:ITEM0_MAINT_MATERIAL_ORDER_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''"); 

        //Bug 76767, Start, Modified the function call
        f = itemblk0.addField("QTY_AVAILABLE","Number");
        f.setSize(17);
        f.setReadOnly();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONQTY_AVAILABLE: Quantity Available");
        if (mgr.isModuleInstalled("INVENT"))
            f.setFunction("MAINT_MATERIAL_REQ_LINE_API.Get_Qty_Available(:ITEM0_MAINT_MATERIAL_ORDER_NO,:LINE_ITEM_NO)");
        else
            f.setFunction("''");
        //Bug 76767, End

        f = itemblk0.addField("QTY_ASSIGNED","Number");
        f.setSize(11);
        f.setReadOnly();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONQTY_ASSIGNED: Quantity Assigned");

        f = itemblk0.addField("QTY_RETURNED","Number");
        f.setSize(11);
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setMandatory();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONQTY_RETURNED: Quantity Returned");

        f = itemblk0.addField("UNITMEAS");
        f.setSize(11);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONUNITMEAS: Unit");
        f.setFunction("Purchase_Part_Supplier_API.Get_Unit_Meas(:SPARE_CONTRACT,:PART_NO)");

        f = itemblk0.addField("CATALOG_CONTRACT");
        f.setSize(10);
        f.setMaxLength(5);
        f.setDefaultNotVisible();
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONCATALOG_CONTRACT: Sales Part Site");
        f.setUpperCase();
        f.setReadOnly();

        f = itemblk0.addField("CATALOG_NO");
        f.setSize(9);
        f.setMaxLength(25);
        f.setDefaultNotVisible();
        f.setCustomValidation("CATALOG_NO,ITEM0_WO_NO,CATALOG_CONTRACT,PRICE_LIST_NO,PLAN_QTY,PLAN_LINE_NO,PART_NO,SPARE_CONTRACT,COST,PLAN_QTY,DISCOUNT,PART_OWNERSHIP_DB","LIST_PRICE,COST,AMOUNTCOST,CATALOGDESC,DISCOUNT,SALESPRICEAMOUNT,ITEMDISCOUNTEDPRICE,SALES_PRICE_GROUP_ID,PRICE_LIST_NO");
        f.setDynamicLOV("SALES_PART_ACTIVE_LOV","CATALOG_CONTRACT CONTRACT",600,445);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONCATALOG_NO: Sales Part Number");
        f.setUpperCase();

        f = itemblk0.addField("CATALOGDESC");
        f.setSize(17);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setMaxLength(2000);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONCATALOGDESC: Sales Part Description");
        f.setFunction("SALES_PART_API.Get_Catalog_Desc(:CATALOG_CONTRACT,:CATALOG_NO)");

        f = itemblk0.addField("PRICE_LIST_NO");
        f.setSize(10);
        f.setMaxLength(10);
        f.setDefaultNotVisible();
        f.setDynamicLOV("SALES_PRICE_LIST","SALES_PRICE_GROUP_ID,CURRENCEY_CODE",600,445);
        f.setCustomValidation("PRICE_LIST_NO,SPARE_CONTRACT,PART_NO,COST,PLAN_QTY,CATALOG_NO,PLAN_QTY,ITEM0_WO_NO,PLAN_LINE_NO,DISCOUNT,CATALOG_CONTRACT","COST,AMOUNTCOST,LIST_PRICE,DISCOUNT,SALESPRICEAMOUNT,ITEMDISCOUNTEDPRICE");   
        f.setLabel("PCMWMAINTMATERIALREQUISITIONPRICE_LIST_NO: Price List No");
        f.setUpperCase();

        f = itemblk0.addField("LIST_PRICE","Money");
        f.setSize(9);                          
        f.setDefaultNotVisible();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONLIST_PRICE: Sales Price");
        f.setReadOnly();

        f = itemblk0.addField("DISCOUNT","Number");
        f.setSize(14);
        f.setCustomValidation("DISCOUNT,LIST_PRICE,PLAN_QTY","SALESPRICEAMOUNT,ITEMDISCOUNTEDPRICE");
        f.setDefaultNotVisible();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONDISCOUNT: Discount %");

        f = itemblk0.addField("ITEMDISCOUNTEDPRICE", "Money");
        f.setSize(14);
        f.setReadOnly();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONITEMDISCOUNTEDPRICE: Discounted Price");
        f.setFunction("LIST_PRICE - (NVL(DISCOUNT, 0)/100*LIST_PRICE)");

        f = itemblk0.addField("SALESPRICEAMOUNT","Money");
        f.setSize(14);
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONSALESPRICEAMOUNT: Price Amount");
        f.setFunction("''");

        f = itemblk0.addField("COST", "Money");
        f.setSize(11);
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONCOST: Cost");
        f.setFunction("''");

        f = itemblk0.addField("AMOUNTCOST", "Money");    
        f.setSize(11);
        f.setMaxLength(15);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONAMOUNTCOST: Cost Amount");
        f.setFunction("''");

        f = itemblk0.addField("SCODEA");
        f.setSize(11);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONSCODEA: Account");
        f.setMaxLength(10);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setFunction("''");

        f = itemblk0.addField("SCODEB");
        f.setSize(11);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONSCODEB: Cost Center");
        f.setMaxLength(10);
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setFunction("''");

        f = itemblk0.addField("SCODEF");
        f.setSize(11);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONSCODEF: Project No");
        f.setMaxLength(10);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setFunction("''");

        f = itemblk0.addField("SCODEE");
        f.setSize(11);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONSCODEE: Object No");
        f.setMaxLength(10);
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setFunction("''");

        f = itemblk0.addField("SCODEC");
        f.setSize(11);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONSCODEC: Code C");
        f.setMaxLength(10);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setFunction("''");

        f = itemblk0.addField("SCODED");
        f.setSize(11);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONSCODED: Code D");
        f.setMaxLength(10);
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setFunction("''");

        f = itemblk0.addField("SCODEG");
        f.setSize(11);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONSCODEG: Code G");
        f.setMaxLength(10);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setFunction("''");

        f = itemblk0.addField("SCODEH");
        f.setSize(11);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONSCODEH: Code H");
        f.setMaxLength(10);
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setFunction("''");

        f = itemblk0.addField("SCODEI");
        f.setSize(11);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONSCODEI: Code I");
        f.setMaxLength(10);
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setFunction("''");

        f = itemblk0.addField("SCODEJ");
        f.setSize(11);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONSCODEJ: Code J");
        f.setMaxLength(10);
        f.setDefaultNotVisible();
        f.setReadOnly();
        f.setFunction("''");

        f = itemblk0.addField("SALES_PRICE_GROUP_ID");
        f.setHidden();
        f.setFunction("SALES_PART_API.GET_SALES_PRICE_GROUP_ID(:CATALOG_CONTRACT,:CATALOG_NO)");

        f = itemblk0.addField("PLAN_LINE_NO","Number");
        f.setSize(17);
        f.setReadOnly();
        f.setHidden();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONPLAN_LINE_NO: Plan Line No");

        f = itemblk0.addField("ITEM0_MAINT_MATERIAL_ORDER_NO","Number","#");
        f.setSize(17);
        f.setHidden();
        f.setReadOnly();
        f.setLabel("PCMWMAINTMATERIALREQUISITIONITEM0_MAINT_MATERIAL_ORDER_NO: Mat Req Order No");
        f.setDbName("MAINT_MATERIAL_ORDER_NO");

        f = itemblk0.addField("WO_STATUS");
        f.setSize(30);
        f.setLabel("PCMWMAINTMATERIALREQUISITIONITEM0WOSTATUS: WO Status");
        f.setFunction("Pm_Type_API.Encode(Active_Work_Order_API.Get_Pm_Type(:ITEM0_WO_NO))");
        f.setReadOnly();

        f = itemblk0.addField("AGREEMENT_ID");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("CUSTOMER_NO");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("CURRENCEY_CODE");
        f.setHidden();
        f.setFunction("ACTIVE_SEPARATE_API.Get_Currency_Code(:ITEM0_WO_NO)");

        f = itemblk0.addField("BASE_PRICE","Money");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("SALE_PRICE","Money");
        f.setHidden();
        f.setFunction("''"); 

        f = itemblk0.addField("CURRENCY_RATE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("DB_STATE");
        f.setHidden();
        f.setFunction("''"); 

        f = itemblk0.addField("SERIAL_TRACK");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("AUTO_REPAIRABLE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("REPAIRABLE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("QTY_LEFT");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("LOCATION_NO");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("LOT_BATCH_NO");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("SERIAL_NO");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("ENG_CHG_LEVEL");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("WAIV_DEV_REJ_NO");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("QTY_TO_ASSIGN");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("QTY_TO_ISSUE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("RES_ALLO");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("ISS_ALLO");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("COND_CODE_USAGE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("DEF_COND_CODE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("CONFIGURATION");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("QTY_TYPE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("EXPIRATION");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("SUPPLY_CONTROL");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("OWNERSHIP_TYPE1");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("OWNERSHIP_TYPE2");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("OWNERSHIP_TYPE3");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("OWNERSHIP_TYPE4");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("OWNER_VENDOR");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("LOCATION_TYPE1");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("LOCATION_TYPE2");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("LOCATION_TYPE3");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("LOCATION_TYPE4");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("LOCATION_TYPE5");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("LOCATION_TYPE6");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("LOCATION_TYPE7");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("LOCATION_TYPE8");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("ORDER_ISSUE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("AUTOMAT_RESERV");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("MANUAL_RESERV");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("ITEM_ACTIVITY_SEQ");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("SUPPLY_CODE_DB");
        f.setHidden();

        f = itemblk0.addField("QTYRES");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("QTYPLANNABLE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("CAT_EXIST","Number");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("WO_SITE");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("CONFIGURATION_IDS");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("ACTIVEIND");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("ACTIVEIND_DB");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("OBJ_LOAN");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("VENDOR_NO");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("DUMMY_ACT_QTY_ISSUED","Number");
        f.setFunction("''");
        f.setHidden();

        itemblk0.setView("MAINT_MATERIAL_REQ_LINE");
        itemblk0.defineCommand("MAINT_MATERIAL_REQ_LINE_API","New__,Modify__,Remove__");
        itemblk0.setMasterBlock(headblk);

        itemset0 = itemblk0.getASPRowSet();

        itembar0 = mgr.newASPCommandBar(itemblk0);
        itembar0.enableCommand(itembar0.FIND);
        itembar0.defineCommand(itembar0.COUNTFIND, "countFindITEM0");
        itembar0.defineCommand(itembar0.OKFIND, "okFindITEM0");
        itembar0.defineCommand(itembar0.NEWROW, "newRowITEM0");
        //itembar0.defineCommand(itembar0.SAVERETURN, null, "checkItem0Fields()");
        itembar0.defineCommand(itembar0.DUPLICATEROW, "duplicateITEM0");

        // 031217  ARWILK  Begin  (Links with RMB's)
        itembar0.addCustomCommand("sparePartObject", mgr.translate("PCMWMAINTMATERIALREQUISITIONSPAREPARTOBJ: Spare Parts in Object..."));
        itembar0.addCustomCommand("objStructure", mgr.translate("PCMWMAINTMATERIALREQUISITIONOBJSTRUCT: Object Structure...")); 
        // 031217  ARWILK  End  (Links with RMB's)
        itembar0.addCustomCommand("detchedPartList", mgr.translate("PCMWMAINTMATERIALREQUISITIONSPAREINDETACH: Spare Parts in Detached Part List..."));
        itembar0.forceEnableMultiActionCommand("sparePartObject");
        itembar0.forceEnableMultiActionCommand("objStructure");
        itembar0.forceEnableMultiActionCommand("detchedPartList");
        itembar0.addCustomCommandSeparator();
        itembar0.addCustomCommand("reserve", mgr.translate("PCMWMAINTMATERIALREQUISITIONRESERV: Reserve"));
        itembar0.addCustomCommand("manReserve", mgr.translate("PCMWMAINTMATERIALREQUISITIONRESERVMAN: Reserve manually..."));
        itembar0.addCustomCommand("availtoreserve", mgr.translate("PCMWMAINTMATERIALREQUISITIONAVAILBLETORESERV: Inventory Part in Stock..."));
        itembar0.addCustomCommand("unreserve", mgr.translate("PCMWMAINTMATERIALREQUISITIONUNRESERV: Unreserve..."));
        itembar0.addCustomCommand("issue", mgr.translate("PCMWMAINTMATERIALREQUISITIONISSUE: Issue"));
        itembar0.addCustomCommand("manIssue", mgr.translate("PCMWMAINTMATERIALREQUISITIONISSUEMAN: Issue manually..."));
        itembar0.addCustomCommandSeparator();
        itembar0.addCustomCommand("availDetail", mgr.translate("PCMWMAINTMATERIALREQUISITIONINVAVAILPLAN: Query - Inventory Part Availability Planning..."));
        itembar0.addCustomCommand("supPerPart", mgr.translate("PCMWMAINTMATERIALREQUISITIONSUPFORPART: Supplier per Part..."));
        itembar0.addCustomCommand("matReqUnissue", mgr.translate("PCMWMAINTMATERIALREQUISITIONMATREQUNISSU: Material Requisition Unissue..."));
        itembar0.addCustomCommand("matReqIssued", mgr.translate("PCMWMAINTMATERIALREQUISITIONISSUEDDETAILS: Issued Part Details..."));

        // 031229  ARWILK  Begin  (Links with multirow RMB's)
        itembar0.enableMultirowAction();
        itembar0.removeFromMultirowAction("manReserve");
        itembar0.removeFromMultirowAction("unreserve");
        itembar0.removeFromMultirowAction("manIssue");
        itembar0.removeFromMultirowAction("matReqUnissue");
        // 031229  ARWILK  End  (Links with multirow RMB's)

        itemtbl0 = mgr.newASPTable(itemblk0);
        itemtbl0.enableRowSelect();

        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);

        //--------------------------------------------------------------------------------------------------
        //------------------------------------       EVENT Block      --------------------------------------
        //--------------------------------------------------------------------------------------------------

        eventblk = mgr.newASPBlock("EVNTBLK");

        f = eventblk.addField("DEF_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = eventblk.addField("DEF_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = eventblk.addField("EVENT_OBJEVENTS");
        f.setDbName("OBJEVENTS");
        f.setHidden();

        f = eventblk.addField("EVNTBLK_WO_NO");
        f.setDbName("WO_NO");
        f.setHidden();

        f = eventblk.addField("EVENT_MAINT_MATERIAL_ORDER_NO");
        f.setHidden();
        f.setDbName("MAINT_MATERIAL_ORDER_NO");

        eventblk.setView("MAINT_MATERIAL_REQUISITION");
        eventset = eventblk.getASPRowSet();


        //--------------------------------------------------------------------------------------------------
        //------------------------------------       RMB Block      ----------------------------------------
        //--------------------------------------------------------------------------------------------------

        prntblk = mgr.newASPBlock("RMBBLK");
        prntblk.addField("ATTR0");
        prntblk.addField("ATTR1");
        prntblk.addField("ATTR2");
        prntblk.addField("ATTR3");
        prntblk.addField("ATTR4");
        prntblk.addField("RESULT_KEY");
    }

    // 040226  ARWILK  Begin  (Enable Multirow RMB actions)
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
    // 040226  ARWILK  End  (Enable Multirow RMB actions)

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        if (!again)
        {
            ASPBuffer availObj;

            trans.clear();
            trans.addSecurityQuery("EQUIPMENT_OBJECT_SPARE,PM_ACTION,EQUIPMENT_SPARE_STRUCTURE,INVENTORY_PART_IN_STOCK_NOPAL,WORK_ORDER_PART_ALLOC,INVENTORY_PART_CONFIG,PURCHASE_PART_SUPPLIER,INVENTORY_TRANSACTION_HIST,INVENTORY_PART_IN_STOCK");
            trans.addPresentationObjectQuery("PCMW/MaintenanceObject2.page,PCMW/MaintenaceObject3.page,EQUIPW/EquipmentSpareStructure3.page,PCMW/MaterialRequisReservatDlg.page,PCMW/MaterialRequisReservatDlg2.page,INVENW/InventoryPartAvailabilityPlanningQry.page,PURCHW/PurchasePartSupplier.page,PCMW/ActiveWorkOrder.page,PCMW/ActiveWorkOrderIssue.page,invenw/InventoryPartInStockOvw.page,PCMW/InventoryPartLocationDlg.page");
            trans.addSecurityQuery("MAINT_MATERIAL_REQUISITION_RPI","Report_Printout");
            trans.addSecurityQuery("MAINT_MATERIAL_REQ_LINE_API","Make_Reservation_Short,Make_Auto_Issue_Detail,Make_Manual_Issue_Detail");
            trans = mgr.perform(trans);

            availObj = trans.getSecurityInfo();

            if (availObj.itemExists("MAINT_MATERIAL_REQUISITION_RPI.Report_Printout"))
                actEna2 = true;

            if (availObj.itemExists("EQUIPMENT_OBJECT_SPARE") && availObj.namedItemExists("PCMW/MaintenanceObject2.page"))
                actEna3 = true;

            if (availObj.itemExists("PM_ACTION") && availObj.namedItemExists("PCMW/MaintenaceObject3.page"))
                actEna4 = true;

            if (availObj.itemExists("EQUIPMENT_SPARE_STRUCTURE") && availObj.namedItemExists("EQUIPW/EquipmentSpareStructure3.page"))
                actEna5 = true;

            if (availObj.itemExists("MAINT_MATERIAL_REQ_LINE_API.Make_Reservation_Short"))
                actEna6 = true;

            if (availObj.itemExists("INVENTORY_PART_IN_STOCK_NOPAL") && availObj.namedItemExists("PCMW/MaterialRequisReservatDlg.page"))
                actEna7 = true;

            if (availObj.itemExists("WORK_ORDER_PART_ALLOC") && availObj.namedItemExists("PCMW/MaterialRequisReservatDlg2.page"))
                actEna8 = true;

            if (availObj.itemExists("MAINT_MATERIAL_REQ_LINE_API.Make_Auto_Issue_Detail"))
                actEna9 = true;

            if (availObj.itemExists("INVENTORY_PART_CONFIG") && availObj.namedItemExists("INVENW/InventoryPartAvailabilityPlanningQry.page"))
                actEna10 = true;

            if (availObj.itemExists("PURCHASE_PART_SUPPLIER") && availObj.namedItemExists("PURCHW/PurchasePartSupplier.page"))
                actEna11 = true;

            if (availObj.itemExists("INVENTORY_TRANSACTION_HIST") && availObj.namedItemExists("PCMW/ActiveWorkOrder.page"))
                actEna12 = true;

            if (availObj.itemExists("INVENTORY_PART_IN_STOCK"))
                actEna13 = true;

            if (availObj.itemExists("INVENTORY_TRANSACTION_HIST") && availObj.namedItemExists("PCMW/ActiveWorkOrderIssue.page"))
                actEna14 = true;
            
            if ( availObj.itemExists("INVENTORY_PART_IN_STOCK_NOPAL") && availObj.namedItemExists("PCMW/InventoryPartLocationDlg.page") && availObj.itemExists("MAINT_MATERIAL_REQ_LINE_API.Make_Manual_Issue_Detail")  )
                actEna15 = true;

            again = true;
        }

        if (!actEna2)
            headbar.removeCustomCommand("printPicList");

        // 031217  ARWILK  Begin  (Links with RMB's)
        if (!actEna3)
            itembar0.removeCustomCommand("sparePartObject");

        if (!actEna4)
            itembar0.removeCustomCommand("objStructure");
        // 031217  ARWILK  End  (Links with RMB's)

        if (!actEna5)
            itembar0.removeCustomCommand("detchedPartList");

        if (!actEna6)
            itembar0.removeCustomCommand("reserve");

        if (!actEna7)
            itembar0.removeCustomCommand("manReserve");

        if (!actEna8)
            itembar0.removeCustomCommand("unreserve");

        if (!actEna9)
            itembar0.removeCustomCommand("issue");

        if (mgr.isPresentationObjectInstalled("invenw/InventoryPartAvailabilityPlanningQry.page"))
        {
            if (!actEna10)
                itembar0.removeCustomCommand("availDetail");
        }

        if (mgr.isPresentationObjectInstalled("purchw/PurchasePartSupplier.page"))
        {
            if (!actEna11)
                itembar0.removeCustomCommand("supPerPart");
        }

        if (!actEna12)
            itembar0.removeCustomCommand("matReqUnissue");

        if (mgr.isPresentationObjectInstalled("invenw/InventoryPartInStockOvw.page"))
        {

            if (!actEna13)
                itembar0.removeCustomCommand("availtoreserve");
        }

        if (!actEna14)
            itembar0.removeCustomCommand("matReqIssued");
        
        if ( !actEna15 )
          itembar0.removeCustomCommand("manIssue");

        if (itemlay0.isVisible())
        {
            String mat_state = headset.getRow().getFieldValue("STATE");

            trans.clear();

            cmd = trans.addCustomFunction("TRANSLATEDCLOSE","MAINT_MATERIAL_REQUISITION_API.Finite_State_Decode__","DB_STATE"); 
            cmd.addParameter("DB_STATE","Closed"); 

            trans = mgr.perform(trans);

            String db_mat_state = trans.getValue("TRANSLATEDCLOSE/DATA/DB_STATE");

            if (db_mat_state.equals(mat_state))
            {
                itembar0.disableCommand(itembar0.NEWROW);
                itembar0.disableCommand(itembar0.DELETE);
                itembar0.disableCommand(itembar0.DUPLICATEROW);
                itembar0.disableCommand(itembar0.EDITROW);

            }
        } 

        headbar.removeCustomCommand("refreshForm");

        if ( itemlay0.isNewLayout() || itemlay0.isEditLayout() )
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
        return "PCMWMAINTMATERIALREQUISITIONTITLE: Maint Material Requisition";
    }

    protected String getTitle()
    {
        return "PCMWMAINTMATERIALREQUISITIONTITLE: Maint Material Requisition";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        printHiddenField("HIDDENPARTNO","");
        printHiddenField("ONCEGIVENERROR","");
        printHiddenField("CREREPNONSER",openCreRepNonSer);
	//Bug 82543, Start
	printHiddenField("REFRESH_FLAG","");
	//Bug 82543, End

        appendToHTML(headlay.show());

        if (headlay.isSingleLayout() && (headset.countRows() > 0))
            appendToHTML(itemlay0.show());

        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

        appendDirtyJavaScript("window.name = \"maintMatReq\"\n");

        appendDirtyJavaScript("function validatePartNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("f.HIDDENPARTNO.value = \"\";\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkPartNo(i) ) return;\n");
        appendDirtyJavaScript(" window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=PART_NO'\n");
        appendDirtyJavaScript("		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
        appendDirtyJavaScript("		+ '&SPARE_CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&CATALOG_CONTRACT=' + URLClientEncode(getValue_('CATALOG_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&WO_NO=' + document.form.WO_NO.value\n");
	appendDirtyJavaScript("		+ '&OWNER=' + URLClientEncode(getValue_('OWNER',i))\n");
        appendDirtyJavaScript("		+ '&ACTIVITY_SEQ=' + URLClientEncode(getValue_('ACTIVITY_SEQ',i))\n");
        appendDirtyJavaScript("         + '&SUPPLY_CODE=' + SelectURLClientEncode('SUPPLY_CODE',i)		+ '&ITEM0_MAINT_MATERIAL_ORDER_NO=' + URLClientEncode(getValue_('ITEM0_MAINT_MATERIAL_ORDER_NO',i))");
	appendDirtyJavaScript("		+ '&CONDITION_CODE=' + URLClientEncode(getValue_('CONDITION_CODE',i))\n");
        appendDirtyJavaScript("		+ '&PART_OWNERSHIP=' + URLClientEncode(getValue_('PART_OWNERSHIP',i))\n");
        appendDirtyJavaScript("		+ '&ITEM0_WO_NO=' + URLClientEncode(getValue_('ITEM0_WO_NO',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("   window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'PART_NO',i,'Part No') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('CATALOG_NO',i,0);\n");
        appendDirtyJavaScript("		assignValue_('CATALOGDESC',i,1);\n");
        appendDirtyJavaScript("		assignValue_('HASSPARESTRUCTURE',i,2);\n");
        appendDirtyJavaScript("		assignValue_('DIMQTY',i,3);\n");
        appendDirtyJavaScript("		assignValue_('TYPEDESIGN',i,4);\n");
        appendDirtyJavaScript("		assignValue_('QTYONHAND',i,5);\n");
        appendDirtyJavaScript("		assignValue_('UNITMEAS',i,6);\n");
        appendDirtyJavaScript("	 	assignValue_('SPAREDESCRIPTION',i,7);\n");
        appendDirtyJavaScript("		assignValue_('SALES_PRICE_GROUP_ID',i,8);\n");
        appendDirtyJavaScript("		assignValue_('CONDITION_CODE',i,9);\n");
        appendDirtyJavaScript("		assignValue_('CONDDESC',i,10);\n"); 
        appendDirtyJavaScript("        assignValue_('QTY_AVAILABLE',i,11);\n");
        appendDirtyJavaScript("        assignValue_('ACTIVEIND_DB',i,12);\n");
        appendDirtyJavaScript("        assignValue_('PART_OWNERSHIP',i,13);\n");
        appendDirtyJavaScript("        assignValue_('PART_OWNERSHIP_DB',i,14);\n");
        appendDirtyJavaScript("        assignValue_('OWNER',i,15);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("else{\n");
        appendDirtyJavaScript("f.HIDDENPARTNO.value = f.PART_NO.value;\n");
        appendDirtyJavaScript("f.ONCEGIVENERROR.value = \"TRUE\";\n");
        appendDirtyJavaScript("getField_('PART_NO',i).value = '';\n");
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("   if (f.ACTIVEIND_DB.value == 'N') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWMAINTMATERIALREQUISITIONINVSALESPART: All sale parts connected to the part are inactive."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.CATALOG_NO.value = ''; \n");
        appendDirtyJavaScript("      f.CATALOGDESC.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("        validateCatalogNo(i);\n");
        appendDirtyJavaScript("}\n");

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
        appendDirtyJavaScript("		+ '&PART_OWNERSHIP=' + SelectURLClientEncode('PART_OWNERSHIP',i)		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
        appendDirtyJavaScript("		+ '&SPARE_CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))\n");
        appendDirtyJavaScript("		+ '&OWNER=' + URLClientEncode(getValue_('OWNER',i))\n");
	appendDirtyJavaScript("		+ '&ACTIVITY_SEQ=' + URLClientEncode(getValue_('ACTIVITY_SEQ',i))\n");
        appendDirtyJavaScript("         + '&SUPPLY_CODE=' + SelectURLClientEncode('SUPPLY_CODE',i)		+ '&ITEM0_MAINT_MATERIAL_ORDER_NO=' + URLClientEncode(getValue_('ITEM0_MAINT_MATERIAL_ORDER_NO',i))");
        appendDirtyJavaScript("		+ '&CONDITION_CODE=' + URLClientEncode(getValue_('CONDITION_CODE',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("	window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'PART_OWNERSHIP',i,'Ownership') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('PART_OWNERSHIP_DB',i,0);\n");
        appendDirtyJavaScript("		assignValue_('QTYONHAND',i,1);\n");
        appendDirtyJavaScript("		assignValue_('QTY_AVAILABLE',i,2);\n");
        appendDirtyJavaScript("		if (f.PART_OWNERSHIP_DB.value == 'CONSIGNMENT'){\n");
        appendDirtyJavaScript("		alert('");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWMAINTMATERIALREQUISITIONINVOWNER1: Ownership type Consignment is not allowed in Materials for Work Orders."));
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
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'SUPPLIER LOANED') \n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("	  var key_value = (getValue_('OWNER',i).indexOf('%') !=-1)? getValue_('OWNER',i):'';\n");
        appendDirtyJavaScript("	  openLOVWindow('OWNER',i,\n");
        appendDirtyJavaScript("'");
        appendDirtyJavaScript("../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PURCHASE_PART_SUPPLIER_LOV&__FIELD=Owner&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('OWNER',i))\n"); 
        appendDirtyJavaScript("                  + '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
        appendDirtyJavaScript("                  + '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");       
        appendDirtyJavaScript(",550,500,'validateOwner');\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}\n");


        appendDirtyJavaScript("function checkItem0Owner()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if (checkItem0Fields())\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("      if (f.WO_CUST.value != '' && f.OWNER.value != '' && f.WO_CUST.value != f.OWNER.value && f.PART_OWNERSHIP_DB.value == 'CUSTOMER OWNED')  \n");
        appendDirtyJavaScript("         return confirm('");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWMAINTMATERIALREQUISITIONDIFFCUST: Owning Customer No is not the same as the Work Order Customer No. Still want to save this record?"));
        appendDirtyJavaScript("');\n");
        appendDirtyJavaScript("      else if (f.WO_CUST.value == '' && f.OWNER.value != '' && f.PART_OWNERSHIP_DB.value == 'CUSTOMER OWNED') \n");
        appendDirtyJavaScript("         return confirm('");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWMAINTMATERIALREQUISITIONNOCUST: There is no Customer specified for the Work Order although you have entered an Owning Customer No. Still want to save this record?"));
        appendDirtyJavaScript("');\n");
        appendDirtyJavaScript("      else \n");
        appendDirtyJavaScript("         return true;\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateOwner(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("      setDirty();\n");
        appendDirtyJavaScript("   if( !checkOwner(i) ) return;\n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'CUSTOMER OWNED' || f.PART_OWNERSHIP_DB.value == 'SUPPLIER LOANED') \n");
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
	appendDirtyJavaScript("		+ '&PART_OWNERSHIP=' + URLClientEncode(getValue_('PART_OWNERSHIP',i))\n");
	appendDirtyJavaScript("		+ '&PART_OWNERSHIP_DB=' + URLClientEncode(getValue_('PART_OWNERSHIP_DB',i))\n");
        appendDirtyJavaScript("                    + '&ITEM0_WO_NO=' + URLClientEncode(getValue_('ITEM0_WO_NO',i))\n");
	appendDirtyJavaScript("		+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
        appendDirtyJavaScript("		+ '&ACTIVITY_SEQ=' + URLClientEncode(getValue_('ACTIVITY_SEQ',i))\n");
        appendDirtyJavaScript("		+ '&SPARE_CONTRACT=' + URLClientEncode(getValue_('SPARE_CONTRACT',i))\n");
        appendDirtyJavaScript("         + '&SUPPLY_CODE=' + SelectURLClientEncode('SUPPLY_CODE',i)		+ '&ITEM0_MAINT_MATERIAL_ORDER_NO=' + URLClientEncode(getValue_('ITEM0_MAINT_MATERIAL_ORDER_NO',i))");
        appendDirtyJavaScript("		+ '&CONDITION_CODE=' + URLClientEncode(getValue_('CONDITION_CODE',i))\n");
        appendDirtyJavaScript("                   );\n");
        appendDirtyJavaScript("      window.status='';\n");
        appendDirtyJavaScript("      if( checkStatus_(r,'OWNER',i,'Owner') )\n");
        appendDirtyJavaScript("      {\n");
        appendDirtyJavaScript("         assignValue_('OWNER_NAME',i,0);\n");
        appendDirtyJavaScript("         assignValue_('WO_CUST',i,1);\n");
        appendDirtyJavaScript("		assignValue_('QTYONHAND',i,2);\n");
        appendDirtyJavaScript("		assignValue_('QTY_AVAILABLE',i,3);\n");
        appendDirtyJavaScript("      }\n");
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP.value == 'Company Owned' && f.OWNER.value != '') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWMAINTMATERIALREQUISITIONINVOWNER11: Owner should not be specified for Company Owned Stock."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.OWNER.value = ''; \n");
        appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP.value == 'Consignment' && f.OWNER.value != '') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWMAINTMATERIALREQUISITIONINVOWNER12: Owner should not be specified for Consignment Stock."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.OWNER.value = ''; \n");
        appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP.value == '' && f.OWNER.value != '') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWMAINTMATERIALREQUISITIONINVOWNER14: Owner should not be specified when there is no Ownership type."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.OWNER.value = ''; \n");
        appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("}\n");

        if (itemlay0.isNewLayout())
        {
            appendDirtyJavaScript("   { \n");
            appendDirtyJavaScript("      f.PART_OWNERSHIP.selectedIndex = 1;\n");
            appendDirtyJavaScript("   } \n");
        }

        if (itemlay0.isNewLayout() && !mgr.isEmpty(headset.getValue("ACTIVITY_SEQ")))
        {
            appendDirtyJavaScript("   { \n");
            //Bug 66456, Start, Added check whether PROJ installed
            if (mgr.isModuleInstalled("PROJ"))
                appendDirtyJavaScript("      f.SUPPLY_CODE.selectedIndex = 1;\n");
            else
                appendDirtyJavaScript("      f.SUPPLY_CODE.selectedIndex = 2;\n");
            //Bug 66456, End
            appendDirtyJavaScript("   } \n");
        }
        else if (itemlay0.isNewLayout() && mgr.isEmpty(headset.getValue("ACTIVITY_SEQ")))
        {
            appendDirtyJavaScript("   { \n");
            appendDirtyJavaScript("      f.SUPPLY_CODE.selectedIndex = 2;\n");
            appendDirtyJavaScript("   } \n");
        }

        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(openCreRepNonSer);
        appendDirtyJavaScript("' == \"TRUE\")\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if (document.form.CREREPNONSER.value == \"TRUE\")\n");
        appendDirtyJavaScript("      window.open('");
        appendDirtyJavaScript(creRepNonSerPath);
        appendDirtyJavaScript("',\"createRepWONonSer\",\"alwaysRaised=yes,resizable,status=yes,scrollbars=yes,width=790,height=575\");\n");
        appendDirtyJavaScript("   document.form.CREREPNONSER.value = \"FALSE\";\n");
        appendDirtyJavaScript("}\n");

        /*appendDirtyJavaScript("function checkPartVal()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript(" if(f.ONCEGIVENERROR.value == \"TRUE\")");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("if(f.HIDDENPARTNO.value == f.PART_NO.value.toUpperCase())");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("        validatePartNo(-1);\n");  
        appendDirtyJavaScript("}\n"); 
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("}\n");*/

        /*String outStr = out.toString();
        int beg_pos = outStr.indexOf("validatePartNo(-1)");

        if (beg_pos > 0)
        {
            String begin_part = outStr.substring(0,beg_pos);
            String first_part = outStr.substring(beg_pos,beg_pos+19);
            String middle_part = "OnBlur=checkPartVal()";
            String last_part = outStr.substring(beg_pos+19);
            String outStrNew = begin_part + first_part + middle_part + last_part;
            out.clear();
            out.append(outStrNew);
        }*/

        // 031217  ARWILK  Begin  (Links with RMB's)
        if (bOpenNewWindow)
        {
            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));	//XSS_Safe AMNILK 20070727
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
        }
        // 031217  ARWILK  End  (Links with RMB's)     

	//Bug 82543, Start
	appendDirtyJavaScript("function refreshPage()\n");
	appendDirtyJavaScript("{\n");
	appendDirtyJavaScript("   f.REFRESH_FLAG.value = 'TRUE';\n");
	appendDirtyJavaScript("   f.submit();\n");
	appendDirtyJavaScript("}\n");
	//Bug 82543, End
    }
}
