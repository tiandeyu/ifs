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
*  File        : WorkOrderReturns.java 
*  Created     : INROLK 2003-06-13 Specification 'ADAM305 Work Order Returns'
*                CHCRLK 2003-06-26 Added action Move Repair/Exchange Part to Inventory. 
*                CHCRLK 2003-07-25 Added action Print Tag Report. 
*                Chamlk 2003-08-26 Added Lov to Owner and set it to Uppercase. Set Part No 
*                                  to uppercase. Added new validations to validate Ownership and Owner.
*                ARWILK 2003-10-08 (Bug#106213) Changed functions preDefine and validate.(Check method comments)
*                SAPRLK 2004-01-05  Web Alignment - removed methods clone() and doReset().
*                SAPRLK 2004-02-12  Web Alignment - change of conditional code in validate method, removal of the back button,
*                                   remove unnecessary method calls for hidden fields
*                ARWILK 040305      (Bug#112762) - Added new upperblk.
*                VAGULK 040316      Bug 112751 - Removed error message "No Data Found" when the data window is displayed.
*                VAGULK 040317      Replaced mgr.submit() with mgr.querysubmit() in okFind() and okFindHEAD().
* -----------------------------Edge - SP1 Merge ------------------------------
*                SHAFLK 040129      Bug Id 42356,Removed Java dependencies.
*                ChAmlk 040128      Bug Id 42684, Modified function invPartStock in order to allow the return of 'Contained' serials 
*                                   to inventory. Set the serial no to uppercase. 
*                ChAmlk 040323      Merge with SP1.
*		 Chanlk	050316	    change parameters on function Get_Inventory_Value()
*                SHAFLK 050321      Bug 49819, Modified method preDefine().
*                NIJALK 050411      Merged bug 49819.
*                NIJALK 060321      Bug 135197: Replaced method calls to Inventory_Part_Cost_API.Get_Cost with Active_Separate_API.Get_Inventory_Value.
* ---------------------------- Sparks Project --------------------------------
*                AMNILK 060731      Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*                AMDILK 060807      Merged with Bug Id 58214
*  		 AMNILK 060818      Bug 58216, Eliminated SQL Injection security vulnerability.
*                AMDILK 060906      Merged with the Bug Id 58216
*  		 SHAFLK  070116     Bug 62854, Modified validation for Part No.
*  		 AMNILK  070208     Merged LCS Bug 62854. 
*                SHAFLK 061120      Bug 61446, Added Supplier Loaned stock.
*                ILSOLK 070302      Merged Bug ID 61446.
*                ASSALK 070704      Webification - Site field changed to visible of return lines. 
*                SHAFLK 090907      Bug 85740, Modified adjust(), newRowHEAD() and preDefine().    
*                SHAFLK 090918      Bug 85740, Modified okFindHEAD().    
* ----------------------------------------------------------------------------  
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class WorkOrderReturns extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderReturns");   

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPContext ctx;
    private ASPHTMLFormatter fmt;

    private ASPBlock upperblk;
    private ASPRowSet upperset;
    private ASPCommandBar upperbar;
    private ASPTable uppertbl;
    private ASPBlockLayout upperlay;  

    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPCommandBar headbar;
    private ASPTable headtbl;
    private ASPBlockLayout headlay;  

    private ASPBlock prntblk;
    private ASPRowSet printset;  

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private String qrystr;
    private String rRowN;
    private String rRawId;
    private String callingUrl;
    private ASPBuffer temp;
    private ASPTransactionBuffer secBuff;
    private ASPCommand cmd;
    private ASPBuffer data;
    private int headrowno;
    private ASPBuffer row;
    //private String sWoNo;
    private String sContract;  

    private ASPQuery q;
    private boolean actEna1;
    private boolean again;

    private String performRMB;
    private String URLString;
    private String WindowName;


    //===============================================================
    // Construction 
    //===============================================================
    public WorkOrderReturns(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {

        ASPManager mgr = getASPManager();

        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();
        fmt = mgr.newASPHTMLFormatter();

        sContract = ctx.readValue("SCONNNT",sContract);
        //sWoNo = ctx.readValue("SWONO",sWoNo);
        qrystr = ctx.readValue("QRYSTR","");
        again = ctx.readFlag("AGAIN",false);
        actEna1 = ctx.readFlag("ACTENA1",false);

        callingUrl = ctx.getGlobal("CALLING_URL");  

        if (mgr.commandBarActivated())
	{
            eval(mgr.commandBarFunction());
	    if ("HEAD.SaveReturn".equals(mgr.readValue("__COMMAND")))
                        headset.refreshRow();
	}
        else if (mgr.dataTransfered())
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
            okFind();
        //          checkObjAvaileble();
        adjust();                                 


        ctx.writeValue("SCONNNT",sContract);

        //ctx.writeValue("SWONO",sWoNo);
        ctx.writeValue("QRYSTR",qrystr);
        ctx.writeFlag("AGAIN",again);
        ctx.writeFlag("ACTENA1",actEna1);
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();

        String val = mgr.readValue("VALIDATE");
        String txt;

        // 031008  ARWILK  Begin (Bug#106213)
        if ("CONTRACT".equals(val))
        {
            double inventValue;
            double totalValue;
            String sConfigurationId;

            sConfigurationId = ((mgr.isEmpty(mgr.readValue("CONFIGURATION_ID")))?"*":mgr.readValue("CONFIGURATION_ID"));

            trans.clear();

            cmd = trans.addCustomFunction("GETOWNVAL","PART_OWNERSHIP_API.Encode","PART_OWNERSHIP");
            cmd.addParameter("PART_OWNERSHIP", mgr.readValue("PART_OWNERSHIP"));

            trans = mgr.validate(trans);

            if ("CUSTOMER OWNED".equals(trans.getValue("GETOWNVAL/DATA/PART_OWNERSHIP")))
            {
                inventValue = 0.00;
                totalValue = 0.00;
            }
            else
            {
                trans.clear();

                cmd = trans.addCustomCommand("GETINVVAL","Active_Separate_API.Get_Inventory_Value");
                cmd.addParameter("INVENTORY_VALUE");
                cmd.addParameter("CONTRACT", mgr.readValue("CONTRACT"));
                cmd.addParameter("PART_NO", mgr.readValue("PART_NO"));
                cmd.addParameter("SERIAL_NO", mgr.readValue("SERIAL_NO"));
                cmd.addParameter("CONFIGURATION_ID", sConfigurationId);
                cmd.addParameter("CONDITION_CODE", mgr.readValue("CONDITION_CODE"));

                trans = mgr.validate(trans);

                inventValue = trans.getNumberValue("GETINVVAL/DATA/INVENTORY_VALUE");
                totalValue = mgr.readNumberValue("QTY_TO_RETURN") * inventValue;
            }

            String sInventValue = mgr.getASPField("INVENTORY_VALUE").formatNumber(inventValue);
            String sTotalValue = mgr.getASPField("TOTAL_VALUE").formatNumber(totalValue);
            if (mgr.isEmpty(sTotalValue))
                sTotalValue = "0.00";

            txt = (mgr.isEmpty(sInventValue) ? "" : (sInventValue)) + "^" +
                  (mgr.isEmpty(sTotalValue) ? "" : (sTotalValue)) + "^";

            mgr.responseWrite(txt);
        }
        // 031008  ARWILK  End (Bug#106213)
        else if ("PART_NO".equals(val))
        {
            String sCompanyOwned = "COMPANY OWNED";
            String sCondiCodeUsage ="";
            String sDefCondiCode ="";
            // 031008  ARWILK  Begin (Bug#106213)
            String sConditionCode = mgr.readValue("CONDITION_CODE");
            String sConditionCodeDesc = "";
            String sDimQty ="";
            String sTypeDesig="";
            double inventValue;
            double totalValue;
            String sConfigurationId;
            String vendorNo = "";
            String custOwner = "";
            String partOwnership = "";
            String sOwner = mgr.readValue("OWNER");
            String ownership = mgr.readValue("PART_OWNERSHIP");

                cmd = trans.addCustomFunction("VENDNO","PURCHASE_PART_SUPPLIER_API.Get_Primary_Supplier_No","VENDOR_NO");
                cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));

                trans = mgr.validate(trans);

                vendorNo = trans.getValue("VENDNO/DATA/VENDOR_NO");
            
                trans.clear();
                cmd = trans.addCustomFunction("GETPARTOWNSHIP","Purchase_Part_Supplier_API.Get_Part_Ownership","PART_OWNERSHIP");
                cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
                cmd.addParameter("VENDOR_NO",vendorNo);

                cmd = trans.addCustomFunction("CUSTNO","Supplier_API.Get_Customer_No","CUSTOMER_NO");
                cmd.addParameter("VENDOR_NO",vendorNo);

                cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
                cmd.addReference("PART_OWNERSHIP","GETPARTOWNSHIP/DATA");
                trans = mgr.validate(trans);

                ownership = trans.getValue("GETPARTOWNSHIP/DATA/PART_OWNERSHIP");
                partOwnership = trans.getValue("VALIDOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");
                custOwner   = trans.getValue("CUSTNO/DATA/CUSTOMER_NO");
                if ((!mgr.isEmpty(vendorNo)) && "CUSTOMER OWNED".equals(partOwnership)) {
                       sCompanyOwned = partOwnership;
                       sOwner = custOwner;
                }
            trans.clear();

            sConfigurationId = ((mgr.isEmpty(mgr.readValue("CONFIGURATION_ID")))?"*":mgr.readValue("CONFIGURATION_ID"));

            // 031008  ARWILK  End (Bug#106213)
            cmd = trans.addCustomFunction("COMPOWNED","Part_Ownership_API.Decode","PART_OWNERSHIP");
            cmd.addParameter("PART_OWNERSHIP",sCompanyOwned);

            cmd = trans.addCustomFunction("INVVALUE","Inventory_Part_Config_API.Get_Inventory_Value_By_Method","INVENTORY_VALUE");
            cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
            cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
            // 031008  ARWILK  Begin (Bug#106213)
            cmd.addParameter("CONFIGURATION_ID",sConfigurationId);
            // 031008  ARWILK  End (Bug#106213)

            cmd = trans.addCustomFunction("PARTDESC","PART_CATALOG_API.Get_Description","PARTDESCRIPTION");
            cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));

            cmd = trans.addCustomFunction("CONDCODEDB","PART_CATALOG_API.Get_Condition_Code_Usage_Db","CONDITION_CODE");
            cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));


            cmd = trans.addCustomFunction("DEFCONDCODE","CONDITION_CODE_API.Get_Default_Condition_Code","CONDITION_CODE");

            if (!mgr.isEmpty ("SERIAL_NO"))
            {

                String sInventSerTrack ="";
                String sAfterDelSerTrack = "";

                cmd = trans.addCustomFunction("INVTRACK","PART_CATALOG_API.Get_Serial_Tracking_Code_Db","INV_TRACK");
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));

                cmd = trans.addCustomFunction("AFTTRACK","PART_CATALOG_API.Get_Eng_Serial_Tracking_Db","AFT_TRACK");
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));

                cmd = trans.addCustomFunction("CHECKEXIST","PART_SERIAL_CATALOG_API.Check_Exist","CHECK_EXIST");
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
                cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));

                cmd = trans.addCustomFunction("CONDITIONCODE","PART_SERIAL_CATALOG_API.Get_Condition_Code","CONDITION_CODE");
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
                cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));

                cmd = trans.addCustomFunction("PARTOWNERSHIP","PART_SERIAL_CATALOG_API.Get_Part_Ownership","PART_OWNERSHIP");
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
                cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));

                cmd = trans.addCustomFunction("PARTOWNERSHIPDB","Part_Ownership_API.Encode","PART_OWNERSHIP_DB");
                cmd.addReference("PART_OWNERSHIP","PARTOWNERSHIP/DATA");

                cmd = trans.addCustomFunction("OWNERCUST","PART_SERIAL_CATALOG_API.Get_Owning_Customer_No","OWNER");
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
                cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));

                cmd = trans.addCustomFunction("OWNERVENDOR","PART_SERIAL_CATALOG_API.Get_Owning_Vendor_No","OWNER");
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
                cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
            }
            if (!mgr.isEmpty ("CONTRACT"))
            {
                cmd = trans.addCustomFunction("DIMQTY","INVENTORY_PART_API.Get_Dim_Quality","DIMENTION_QUALITY");
                cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));

                cmd = trans.addCustomFunction("TYPEDESIGNATION","INVENTORY_PART_API.Get_Type_Designation","TYPE_DESIGNATION");
                cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));

            }
            trans = mgr.validate(trans);

            ownership = trans.getValue("COMPOWNED/DATA/PART_OWNERSHIP");
            double invvalue_ = trans.getNumberValue("INVVALUE/DATA/INVENTORY_VALUE");
            String partDesc_ = trans.getValue("PARTDESC/DATA/PARTDESCRIPTION");

            int totValue_ =  (int)(invvalue_ * mgr.readNumberValue("QTY_TO_RETURN"));
            String stotValue_ = mgr.getASPField("TOTAL_VALUE").formatNumber(totValue_);

            if ("ALLOW_COND_CODE".equals(trans.getValue("CONDCODEDB/DATA/CONDITION_CODE")))
            {
                sDefCondiCode = trans.getValue("DEFCONDCODE/DATA/CONDITION_CODE");

                if (!mgr.isEmpty ("SERIAL_NO"))
                {
                    String sInventSerTrack = trans.getValue("INVTRACK/DATA/INV_TRACK");
                    String sAfterDelSerTrack = trans.getValue("AFTTRACK/DATA/AFT_TRACK");
                    if ("SERIAL TRACKING".equals(sInventSerTrack) && "SERIAL TRACKING".equals(sAfterDelSerTrack))
                    {
                        String sSerialExist = trans.getValue("CHECKEXIST/DATA/CHECK_EXIST");

                        if ("TRUE".equals(sSerialExist))
                        {
                            sConditionCode = trans.getValue("CONDITIONCODE/DATA/CONDITION_CODE");
                            if (mgr.isEmpty (sConditionCode))
                                sConditionCode = sDefCondiCode;
                        }
                        else
                            sConditionCode = sDefCondiCode;
                    }
                    else
                        sConditionCode = sDefCondiCode;
                }
                else
                    sConditionCode = sDefCondiCode ;

            }

            if (!mgr.isEmpty ("CONTRACT"))
            {
                sDimQty =  trans.getValue("DIMQTY/DATA/DIMENTION_QUALITY");
                sTypeDesig =  trans.getValue("TYPEDESIGNATION/DATA/TYPE_DESIGNATION");
            }

            if (!mgr.isEmpty ("SERIAL_NO"))
            {
                String sInventSerTrack = trans.getValue("INVTRACK/DATA/INV_TRACK");
                String sAfterDelSerTrack = trans.getValue("AFTTRACK/DATA/AFT_TRACK");
                if ("SERIAL TRACKING".equals(sInventSerTrack) && "SERIAL TRACKING".equals(sAfterDelSerTrack))
                {
                    String sSerialExist = trans.getValue("CHECKEXIST/DATA/CHECK_EXIST");
                    ownership = trans.getValue("PARTOWNERSHIP/DATA/PART_OWNERSHIP");

                    if ("TRUE".equals(sSerialExist))
                    {
                        String sDbValOwnership = trans.getValue("PARTOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");
                        if ("CUSTOMER OWNED".equals(sDbValOwnership))
                        {
                            sOwner =  trans.getValue("OWNERCUST/DATA/OWNER");
                        }
                        if ("SUPPLIER LOANED".equals(sDbValOwnership))
                        {
                            sOwner =  trans.getValue("OWNERVENDOR/DATA/OWNER");
                        }

                    }
                }
            }

            // 031008  ARWILK  Begin (Bug#106213)

            trans.clear();

            cmd = trans.addCustomFunction("GETOWNVAL","PART_OWNERSHIP_API.Encode","PART_OWNERSHIP");
            cmd.addParameter("PART_OWNERSHIP", ownership);

            trans = mgr.validate(trans);
            String ownershipdb =trans.getValue("GETOWNVAL/DATA/PART_OWNERSHIP");

            if ("CUSTOMER OWNED".equals(trans.getValue("GETOWNVAL/DATA/PART_OWNERSHIP")))
            {
                inventValue = 0.00;
                totalValue = 0.00;
            }
            else
            {
                trans.clear();

                cmd = trans.addCustomCommand("GETINVVAL","Active_Separate_API.Get_Inventory_Value");
                cmd.addParameter("INVENTORY_VALUE");
                cmd.addParameter("CONTRACT", mgr.readValue("CONTRACT"));
                cmd.addParameter("PART_NO", mgr.readValue("PART_NO"));
                cmd.addParameter("SERIAL_NO", mgr.readValue("SERIAL_NO"));
                cmd.addParameter("CONFIGURATION_ID", sConfigurationId);
                cmd.addParameter("CONDITION_CODE", sConditionCode);

                trans = mgr.validate(trans);

                inventValue = trans.getNumberValue("GETINVVAL/DATA/INVENTORY_VALUE");
                totalValue = mgr.readNumberValue("QTY_TO_RETURN") * inventValue;
            }

            String sInventValue = mgr.getASPField("INVENTORY_VALUE").formatNumber(inventValue);
            String sTotalValue = mgr.getASPField("TOTAL_VALUE").formatNumber(totalValue);
            if (mgr.isEmpty(sTotalValue))
                sTotalValue = "0.00";

            txt = (mgr.isEmpty(ownership) ? "" : (ownership)) + "^" +
                  (mgr.isEmpty(ownershipdb) ? "" : (ownershipdb)) + "^" +
                  (mgr.isEmpty(sInventValue) ? "" : (sInventValue)) + "^" +
                  (mgr.isEmpty(sTotalValue) ? "" : (sTotalValue)) + "^" +
                  (mgr.isEmpty(partDesc_) ? "" : (partDesc_)) + "^" +
                  (mgr.isEmpty(sConditionCode) ? "" : (sConditionCode)) + "^" +
                  (mgr.isEmpty(sConditionCodeDesc) ? "" : (sConditionCodeDesc)) + "^" +
                  (mgr.isEmpty(sDimQty) ? "" : (sDimQty)) + "^" +
                  (mgr.isEmpty(sTypeDesig) ? "" : (sTypeDesig)) + "^" +
                  (mgr.isEmpty(sOwner) ? "" : (sOwner)) + "^";

            // 031008  ARWILK  End (Bug#106213)


            mgr.responseWrite(txt);
        }
        else if ("SERIAL_NO".equals(val))
        {
            String sCompanyOwned = "COMPANY OWNED";
            String sCondiCodeUsage ="";
            String sDefCondiCode ="";
            // 031008  ARWILK  Begin (Bug#106213)
            String sConditionCode = mgr.readValue("CONDITION_CODE");
            String sConditionCodeDesc = "";
            String sOwner = "";
            String ownership_ = mgr.readValue("PART_OWNERSHIP");
            double inventValue;
            double totalValue;
            String sConfigurationId;


            sConfigurationId = ((mgr.isEmpty(mgr.readValue("CONFIGURATION_ID")))?"*":mgr.readValue("CONFIGURATION_ID"));

            // 031008  ARWILK  End (Bug#106213)

            cmd = trans.addCustomFunction("CONDCODEDB","PART_CATALOG_API.Get_Condition_Code_Usage_Db","CONDITION_CODE");
            cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));

            cmd = trans.addCustomFunction("DEFCONDCODE","CONDITION_CODE_API.Get_Default_Condition_Code","CONDITION_CODE");
            if (!mgr.isEmpty ("SERIAL_NO"))
            {

                String sInventSerTrack ="";
                String sAfterDelSerTrack = "";

                cmd = trans.addCustomFunction("INVTRACK","PART_CATALOG_API.Get_Serial_Tracking_Code_Db","INV_TRACK");
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));

                cmd = trans.addCustomFunction("AFTTRACK","PART_CATALOG_API.Get_Eng_Serial_Tracking_Db","AFT_TRACK");
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));

                cmd = trans.addCustomFunction("CHECKEXIST","PART_SERIAL_CATALOG_API.Check_Exist","CHECK_EXIST");
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
                cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));

                cmd = trans.addCustomFunction("CONDITIONCODE","PART_SERIAL_CATALOG_API.Get_Condition_Code","CONDITION_CODE");
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
                cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));

                cmd = trans.addCustomFunction("PARTOWNERSHIP","PART_SERIAL_CATALOG_API.Get_Part_Ownership","PART_OWNERSHIP");
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
                cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));

                cmd = trans.addCustomFunction("PARTOWNERSHIPDB","Part_Ownership_API.Encode","PART_OWNERSHIP_DB");
                cmd.addReference("PART_OWNERSHIP","PARTOWNERSHIP/DATA");

                cmd = trans.addCustomFunction("OWNERCUST","PART_SERIAL_CATALOG_API.Get_Owning_Customer_No","OWNER");
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
                cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));

                cmd = trans.addCustomFunction("OWNERVENDOR","PART_SERIAL_CATALOG_API.Get_Owning_Vendor_No","OWNER");
                cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));
                cmd.addParameter("SERIAL_NO",mgr.readValue("SERIAL_NO"));
            }

            trans = mgr.validate(trans);

            ownership_ = trans.getValue("COMPOWNED/DATA/PART_OWNERSHIP");

            if ("ALLOW_COND_CODE".equals(trans.getValue("CONDCODEDB/DATA/CONDITION_CODE")))
            {
                sDefCondiCode = trans.getValue("DEFCONDCODE/DATA/CONDITION_CODE");

                if (!mgr.isEmpty ("SERIAL_NO"))
                {
                    String sInventSerTrack = trans.getValue("INVTRACK/DATA/INV_TRACK");
                    String sAfterDelSerTrack = trans.getValue("AFTTRACK/DATA/AFT_TRACK");
                    if ("SERIAL TRACKING".equals(sInventSerTrack) && "SERIAL TRACKING".equals(sAfterDelSerTrack))
                    {
                        String sSerialExist = trans.getValue("CHECKEXIST/DATA/CHECK_EXIST");

                        if ("TRUE".equals(sSerialExist))
                        {
                            sConditionCode = trans.getValue("CONDITIONCODE/DATA/CONDITION_CODE");
                            if (mgr.isEmpty (sConditionCode))
                                sConditionCode = sDefCondiCode;
                        }
                        else
                            sConditionCode = sDefCondiCode;
                    }
                    else
                        sConditionCode = sDefCondiCode;
                }
                else
                    sConditionCode = sDefCondiCode ;

                trans.clear();
                cmd = trans.addCustomFunction("CONDCODEDESC","CONDITION_CODE_API.Get_Description","CONDITIONCODEDESC");
                cmd.addParameter("CONDITION_CODE",mgr.readValue("CONDITION_CODE"));

                trans = mgr.validate(trans);
                sConditionCodeDesc =  trans.getValue("CONDCODEDESC/DATA/CONDITIONCODEDESC");

            }
            if (!mgr.isEmpty ("SERIAL_NO"))
            {
                String sInventSerTrack = trans.getValue("INVTRACK/DATA/INV_TRACK");
                String sAfterDelSerTrack = trans.getValue("AFTTRACK/DATA/AFT_TRACK");
                if ("SERIAL TRACKING".equals(sInventSerTrack) && "SERIAL TRACKING".equals(sAfterDelSerTrack))
                {
                    String sSerialExist = trans.getValue("CHECKEXIST/DATA/CHECK_EXIST");
                    ownership_ = trans.getValue("PARTOWNERSHIP/DATA/PART_OWNERSHIP");

                    if ("TRUE".equals(sSerialExist))
                    {
                        String sDbValOwnership = trans.getValue("PARTOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");
                        if ("CUSTOMER OWNED".equals(sDbValOwnership))
                        {
                            sOwner =  trans.getValue("OWNERCUST/DATA/OWNER");
                        }
                        if ("SUPPLIER LOANED".equals(sDbValOwnership))
                        {
                            sOwner =  trans.getValue("OWNERVENDOR/DATA/OWNER");
                        }

                    }
                }
            }

            // 031008  ARWILK  Begin (Bug#106213)

            trans.clear();

            cmd = trans.addCustomFunction("GETOWNVAL","PART_OWNERSHIP_API.Encode","PART_OWNERSHIP");
            cmd.addParameter("PART_OWNERSHIP", ownership_);

            trans = mgr.validate(trans);

            if ("CUSTOMER OWNED".equals(trans.getValue("GETOWNVAL/DATA/PART_OWNERSHIP")))
            {
                inventValue = 0.00;
                totalValue = 0.00;
            }
            else
            {
                trans.clear();

                cmd = trans.addCustomCommand("GETINVVAL","Active_Separate_API.Get_Inventory_Value");
                cmd.addParameter("INVENTORY_VALUE");
                cmd.addParameter("CONTRACT", mgr.readValue("CONTRACT"));
                cmd.addParameter("PART_NO", mgr.readValue("PART_NO"));
                cmd.addParameter("SERIAL_NO", mgr.readValue("SERIAL_NO"));
                cmd.addParameter("CONFIGURATION_ID", sConfigurationId);
                cmd.addParameter("CONDITION_CODE", sConditionCode);

                trans = mgr.validate(trans);

                inventValue = trans.getNumberValue("GETINVVAL/DATA/INVENTORY_VALUE");
                totalValue = mgr.readNumberValue("QTY_TO_RETURN") * inventValue;
            }

            String sInventValue = mgr.getASPField("INVENTORY_VALUE").formatNumber(inventValue);
            String sTotalValue = mgr.getASPField("TOTAL_VALUE").formatNumber(totalValue);
            if (mgr.isEmpty(sTotalValue))
                sTotalValue = "0.00";

            txt = (mgr.isEmpty(ownership_) ? "" : (ownership_))+ "^"+
                  (mgr.isEmpty(sConditionCode) ? "" : (sConditionCode))+ "^"+
                  (mgr.isEmpty(sConditionCodeDesc) ? "" : (sConditionCodeDesc))+ "^"+
                  (mgr.isEmpty(sInventValue) ? "" : (sInventValue))+ "^"+
                  (mgr.isEmpty(sTotalValue) ? "" : (sTotalValue))+ "^"+
                  (mgr.isEmpty(sOwner) ? "" : (sOwner))+ "^";

            // 031008  ARWILK  End (Bug#106213)

            mgr.responseWrite(txt);
        }
        else if ("CONFIGURATION_ID".equals(val))
        {
            // 031008  ARWILK  Begin (Bug#106213)
            double inventValue;
            double totalValue;
            String sConfigurationId;

            sConfigurationId = ((mgr.isEmpty(mgr.readValue("CONFIGURATION_ID")))?"*":mgr.readValue("CONFIGURATION_ID"));

            trans.clear();

            cmd = trans.addCustomFunction("GETOWNVAL","PART_OWNERSHIP_API.Encode","PART_OWNERSHIP");
            cmd.addParameter("PART_OWNERSHIP", mgr.readValue("PART_OWNERSHIP"));

            trans = mgr.validate(trans);

            if ("CUSTOMER OWNED".equals(trans.getValue("GETOWNVAL/DATA/PART_OWNERSHIP")))
            {
                inventValue = 0.00;
                totalValue = 0.00;
            }
            else
            {
                trans.clear();

                cmd = trans.addCustomCommand("GETINVVAL","Active_Separate_API.Get_Inventory_Value");
                cmd.addParameter("INVENTORY_VALUE");
                cmd.addParameter("CONTRACT", mgr.readValue("CONTRACT"));
                cmd.addParameter("PART_NO", mgr.readValue("PART_NO"));
                cmd.addParameter("SERIAL_NO", mgr.readValue("SERIAL_NO"));
                cmd.addParameter("CONFIGURATION_ID", sConfigurationId);
                cmd.addParameter("CONDITION_CODE", mgr.readValue("CONDITION_CODE"));

                trans = mgr.validate(trans);

                inventValue = trans.getNumberValue("GETINVVAL/DATA/INVENTORY_VALUE");
                totalValue = mgr.readNumberValue("QTY_TO_RETURN") * inventValue;
            }
            String sInventValue = mgr.getASPField("INVENTORY_VALUE").formatNumber(inventValue);
            String sTotalValue = mgr.getASPField("TOTAL_VALUE").formatNumber(totalValue);
            if (mgr.isEmpty(sTotalValue))
                sTotalValue = "0.00";

            txt = (mgr.isEmpty(sInventValue) ? "" : (sInventValue))+ "^"+
                  (mgr.isEmpty(sTotalValue) ? "" : (sTotalValue))+ "^" ;

            mgr.responseWrite(txt);
        }
        // 031008  ARWILK  Begin (Bug#106213)
        else if ("CONDITION_CODE".equals(val))
        {
            double inventValue;
            double totalValue;
            String sConfigurationId;
            String sConditionDescription;

            sConfigurationId = ((mgr.isEmpty(mgr.readValue("CONFIGURATION_ID")))?"*":mgr.readValue("CONFIGURATION_ID"));

            trans.clear();

            cmd = trans.addCustomFunction("GETOWNVAL","PART_OWNERSHIP_API.Encode","PART_OWNERSHIP");
            cmd.addParameter("PART_OWNERSHIP", mgr.readValue("PART_OWNERSHIP"));

            cmd = trans.addCustomFunction("GETCONDDESC","CONDITION_CODE_API.Get_Description","CONDITIONCODEDESC");
            cmd.addParameter("CONDITION_CODE", mgr.readValue("CONDITION_CODE"));

            trans = mgr.validate(trans);

            sConditionDescription = trans.getValue("GETCONDDESC/DATA/CONDITIONCODEDESC");

            if ("CUSTOMER OWNED".equals(trans.getValue("GETOWNVAL/DATA/PART_OWNERSHIP")))
            {
                inventValue = 0.00;
                totalValue = 0.00;
            }
            else
            {
                trans.clear();

                cmd = trans.addCustomCommand("GETINVVAL","Active_Separate_API.Get_Inventory_Value");
                cmd.addParameter("INVENTORY_VALUE");
                cmd.addParameter("CONTRACT", mgr.readValue("CONTRACT"));
                cmd.addParameter("PART_NO", mgr.readValue("PART_NO"));
                cmd.addParameter("SERIAL_NO", mgr.readValue("SERIAL_NO"));
                cmd.addParameter("CONFIGURATION_ID", sConfigurationId);
                cmd.addParameter("CONDITION_CODE", mgr.readValue("CONDITION_CODE"));

                trans = mgr.validate(trans);

                inventValue = trans.getNumberValue("GETINVVAL/DATA/INVENTORY_VALUE");
                totalValue = mgr.readNumberValue("QTY_TO_RETURN") * inventValue;
            }
            String sInventValue = mgr.getASPField("INVENTORY_VALUE").formatNumber(inventValue);
            String sTotalValue = mgr.getASPField("TOTAL_VALUE").formatNumber(totalValue);
            if (mgr.isEmpty(sTotalValue))
                sTotalValue = "0.00";

            txt = (mgr.isEmpty(sInventValue) ? "" : (sInventValue)) + "^" +
                  (mgr.isEmpty(sTotalValue) ? "" : (sTotalValue)) + "^" +
                  (mgr.isEmpty(sConditionDescription) ? "" : (sConditionDescription)) + "^";

            mgr.responseWrite(txt);
        }
        else if ("PART_OWNERSHIP".equals(val))
        {
            double inventValue;
            double totalValue;
            String sConfigurationId;
            String sOwnershipDb;

            sConfigurationId = ((mgr.isEmpty(mgr.readValue("CONFIGURATION_ID")))?"*":mgr.readValue("CONFIGURATION_ID"));

            trans.clear();

            cmd = trans.addCustomFunction("VALIDOWNERSHIPDB", "PART_OWNERSHIP_API.Encode", "PART_OWNERSHIP_DB");
            cmd.addParameter("PART_OWNERSHIP", mgr.readValue("PART_OWNERSHIP"));

            cmd = trans.addCustomFunction("GETOWNVAL","PART_OWNERSHIP_API.Encode","PART_OWNERSHIP");
            cmd.addParameter("PART_OWNERSHIP", mgr.readValue("PART_OWNERSHIP"));

            trans = mgr.validate(trans);

            sOwnershipDb = trans.getValue("VALIDOWNERSHIPDB/DATA/PART_OWNERSHIP_DB");

            if ("CUSTOMER OWNED".equals(trans.getValue("GETOWNVAL/DATA/PART_OWNERSHIP")))
            {
                inventValue = 0.00;
                totalValue = 0.00;
            }
            else
            {
                trans.clear();

                cmd = trans.addCustomCommand("GETINVVAL","Active_Separate_API.Get_Inventory_Value");
                cmd.addParameter("INVENTORY_VALUE");
                cmd.addParameter("CONTRACT", mgr.readValue("CONTRACT"));
                cmd.addParameter("PART_NO", mgr.readValue("PART_NO"));
                cmd.addParameter("SERIAL_NO", mgr.readValue("SERIAL_NO"));
                cmd.addParameter("CONFIGURATION_ID", sConfigurationId);
                cmd.addParameter("CONDITION_CODE", mgr.readValue("CONDITION_CODE"));

                trans = mgr.validate(trans);

                inventValue = trans.getNumberValue("GETINVVAL/DATA/INVENTORY_VALUE");
                totalValue = mgr.readNumberValue("QTY_TO_RETURN") * inventValue;
            }
            String sInventValue = mgr.getASPField("INVENTORY_VALUE").formatNumber(inventValue);
            String sTotalValue = mgr.getASPField("TOTAL_VALUE").formatNumber(totalValue);
            if (mgr.isEmpty(sTotalValue))
                sTotalValue = "0.00";

            txt = (mgr.isEmpty(sInventValue) ? "" : (sInventValue)) + "^" +
                  (mgr.isEmpty(sTotalValue) ? "" : (sTotalValue)) + "^" +
                  (mgr.isEmpty(sOwnershipDb) ? "" : (sOwnershipDb)) + "^";  

            mgr.responseWrite(txt);
        }
        // 031008  ARWILK  End (Bug#106213)
        else if ("OWNER".equals(val))
        {
            String sOwnerName="";
            String sOwnershipDb = mgr.readValue("PART_OWNERSHIP_DB"); 
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
            cmd.addParameter("WO_NO");
            //cmd.addParameter("WO_NO",mgr.readValue("ITEM3_WO_NO",""));

            trans = mgr.validate(trans);

            if ("CUSTOMER OWNED".equals(sOwnershipDb))
                  sOwnerName = trans.getValue("GETOWNERNAME/DATA/OWNER_NAME");
            if ("SUPPLIER LOANED".equals(sOwnershipDb))
                  sOwnerName = trans.getValue("GETOWNERNAME1/DATA/OWNER_NAME");
            String sWoCust    = trans.getValue("GETOWCUST/DATA/WO_CUST");

            txt = (mgr.isEmpty(sOwnerName) ? "" : (sOwnerName)) + "^" + (mgr.isEmpty(sWoCust) ? "": (sWoCust)) + "^";  
            mgr.responseWrite(txt);
        }
        // 031008  ARWILK  Begin (Bug#106213)
        else if ("QTY_TO_RETURN".equals(val))
        {
            double totalValue;

            trans.clear();

            cmd = trans.addCustomFunction("GETOWNVAL","PART_OWNERSHIP_API.Encode","PART_OWNERSHIP");
            cmd.addParameter("PART_OWNERSHIP", mgr.readValue("PART_OWNERSHIP"));

            trans = mgr.validate(trans);

            if ("CUSTOMER OWNED".equals(trans.getValue("GETOWNVAL/DATA/PART_OWNERSHIP")))
                totalValue = 0.00;
            else
                totalValue = mgr.readNumberValue("QTY_TO_RETURN") * mgr.readNumberValue("INVENTORY_VALUE");
            String sTotalValue = mgr.getASPField("TOTAL_VALUE").formatNumber(totalValue);
            if (mgr.isEmpty(sTotalValue))
                sTotalValue = "0.00";

            //txt = totalValue + "^";
            txt = (mgr.isEmpty(sTotalValue) ? "" : (sTotalValue)) + "^" ;

            mgr.responseWrite(txt);
        }
        // 031008  ARWILK  End (Bug#106213)

        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------


//-----------------------------------------------------------------------------
//-------------------------  CUSTOM FUNCTIONS  --------------------------------
//-----------------------------------------------------------------------------

    public void invPartStock()
    {
        ASPManager mgr = getASPManager();
        boolean activated = false;

        if (headlay.isMultirowLayout())
        {
            headset.goTo(headset.getRowSelected());
            if (activateInvPartStock())
                activated = true;
            else
                activated = false;   
        }
        else
            activated = true;

        if (activated)
        {
            trans.clear();
            cmd = trans.addCustomFunction("GETOBJS","PART_SERIAL_CATALOG_API.Get_Objstate","OBJSTATE");
            cmd.addParameter("PART_NO",headset.getRow().getValue("PART_NO"));
            cmd.addParameter("SERIAL_NO",headset.getRow().getValue("SERIAL_NO"));

            cmd = trans.addCustomFunction("GETTID","Work_Order_Returns_API.Get_Latest_Transaction_Id","TRANSID");
            cmd.addParameter("PART_NO",headset.getRow().getValue("PART_NO"));
            cmd.addParameter("CONTRACT",headset.getRow().getValue("CONTRACT"));
            cmd.addParameter("SERIAL_NO",headset.getRow().getValue("SERIAL_NO"));

            trans = mgr.perform(trans);         

            String sObjState = trans.getValue("GETOBJS/DATA/OBJSTATE");
            int nTransID = (int)(trans.getNumberValue("GETTID/DATA/TRANSID"));
            int nQtyLeft = (int)(headset.getRow().getNumberValue("QTY_TO_RETURN") - headset.getRow().getNumberValue("QTY_RETURNED"));
            qrystr = "";     
            trans.clear();

            String sWindowName = "WOReturns"; 
            String sQryStr = mgr.getURL() + "?WO_NO="+mgr.URLEncode(headset.getValue("WO_NO"));  

            if ("InFacility".equals(sObjState) || "Contained".equals(sObjState) || mgr.isEmpty(sObjState))
            {
                performRMB = "TRUE";
                URLString = "InventoryPartInStockDlg.page?WO_NO="+mgr.URLEncode(headset.getValue("WO_NO"))+
                            "&LINE_NO="+mgr.URLEncode(headset.getRow().getValue("LINE_NO"))+
                            "&CONTRACT="+mgr.URLEncode(headset.getRow().getValue("CONTRACT"))+
                            "&PART_NO="+mgr.URLEncode(headset.getRow().getValue("PART_NO"))+
                            "&PARTDESCRIPTION="+mgr.URLEncode(headset.getRow().getValue("PARTDESCRIPTION"))+
                            "&SERIAL_NO="+mgr.URLEncode(headset.getRow().getValue("SERIAL_NO"))+
                            "&LOT_BATCH_NO="+mgr.URLEncode(headset.getRow().getValue("LOT_BATCH_NO"))+
                            "&CONFIGURATION_ID="+mgr.URLEncode(headset.getRow().getValue("CONFIGURATION_ID"))+
                            "&PART_OWNERSHIP="+mgr.URLEncode(headset.getRow().getValue("PART_OWNERSHIP"))+
                            "&OWNER="+mgr.URLEncode(headset.getRow().getValue("OWNER"))+
                            "&OWNER_NAME="+mgr.URLEncode(headset.getRow().getValue("OWNER_NAME"))+
                            "&WO_CUST="+mgr.URLEncode(headset.getRow().getValue("WO_CUST"))+
                            "&CONDITION_CODE="+mgr.URLEncode(headset.getRow().getValue("CONDITION_CODE"))+
                            "&CONDITIONCODEDESC="+mgr.URLEncode(headset.getRow().getValue("CONDITIONCODEDESC"))+
                            "&INVENTORY_VALUE="+mgr.URLEncode(headset.getRow().getValue("INVENTORY_VALUE"))+
                            "&QTY_LEFT="+nQtyLeft+
                            "&QTY_RETURNED="+mgr.URLEncode(headset.getRow().getValue("QTY_RETURNED"))+
                            "&FRMNAME="+sWindowName+
                            "&QRYSTR="+sQryStr;                                                    
            }
            else if ("Issued".equals(sObjState))
            {
                performRMB   = "TRUE";
                URLString = "InventoryTransactionHistDlg.page?WO_NO="+mgr.URLEncode(headset.getValue("WO_NO"))+
                            "&LINE_NO="+mgr.URLEncode(headset.getRow().getValue("LINE_NO"))+
                            "&CONTRACT="+mgr.URLEncode(headset.getRow().getValue("CONTRACT"))+
                            "&PART_NO="+mgr.URLEncode(headset.getRow().getValue("PART_NO"))+
                            "&PARTDESCRIPTION="+mgr.URLEncode(headset.getRow().getValue("PARTDESCRIPTION"))+
                            "&SERIAL_NO="+mgr.URLEncode(headset.getRow().getValue("SERIAL_NO"))+
                            "&LOT_BATCH_NO="+mgr.URLEncode(headset.getRow().getValue("LOT_BATCH_NO"))+
                            "&CONFIGURATION_ID="+mgr.URLEncode(headset.getRow().getValue("CONFIGURATION_ID"))+
                            "&PART_OWNERSHIP="+mgr.URLEncode(headset.getRow().getValue("PART_OWNERSHIP"))+
                            "&OWNER="+mgr.URLEncode(headset.getRow().getValue("OWNER"))+
                            "&OWNER_NAME="+mgr.URLEncode(headset.getRow().getValue("OWNER_NAME"))+
                            "&WO_CUST="+mgr.URLEncode(headset.getRow().getValue("WO_CUST"))+
                            "&CONDITION_CODE="+mgr.URLEncode(headset.getRow().getValue("CONDITION_CODE"))+
                            "&CONDITIONCODEDESC="+mgr.URLEncode(headset.getRow().getValue("CONDITIONCODEDESC"))+
                            "&INVENTORY_VALUE="+mgr.URLEncode(headset.getRow().getValue("INVENTORY_VALUE"))+
                            "&QTY_LEFT="+nQtyLeft+
                            "&TRANS_ID="+nTransID+
                            "&QTY_RETURNED="+mgr.URLEncode(headset.getRow().getValue("QTY_RETURNED"))+
                            "&FRMNAME="+sWindowName+
                            "&QRYSTR="+sQryStr;                    
            }
            else
                mgr.showAlert(mgr.translate("PCMWWORKORDERRETURNSCANNOTPERMOVE: Cannot perform Move Repair/Exchange Part to Inventory on this record."));        
        }
        else
            mgr.showAlert(mgr.translate("PCMWWORKORDERRETURNSCANNOTPERMOVE: Cannot perform Move Repair/Exchange Part to Inventory on this record."));        
    }

    public boolean activateInvPartStock()
    {
        ASPManager mgr = getASPManager();    
        double nQtyToRet = headset.getRow().getNumberValue("QTY_TO_RETURN");
        double nQtyRet = headset.getRow().getNumberValue("QTY_RETURNED");

        int nLeftToRet = (int) (nQtyToRet - nQtyRet);

        if (nLeftToRet>0)
        {
            //Status of the work order should be between Released and Work Done.
            trans.clear(); 
            cmd = trans.addCustomFunction("ISVALID","WORK_ORDER_RETURNS_API.Is_Valid_Status_For_Return","VALFORRET");
            cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

            trans = mgr.perform(trans);

            String sValidForRet = trans.getValue("ISVALID/DATA/VALFORRET");

            if ("TRUE".equals(sValidForRet))
                return true;
            else
                return false;  
        }
        else
            return false; 
    } 

    public void matTagReport()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());

        prntblk = getASPBlock("RMBBLK");
        printset = prntblk.getASPRowSet();

        String attr1 = "REPORT_ID" + (char)31 + "MATERIAL_TAG_REP" + (char)30;
        String attr2 = "WO_NO_LIST" + (char)31 + headset.getRow().getFieldValue("WO_NO") + (char)30 + "LINE_NO_LIST" + (char)31 + headset.getRow().getFieldValue("LINE_NO") + (char)30;
        String attr3 = "";
        String attr4 = "";

        cmd = trans.addCustomCommand( "PRNT","Archive_API.New_Client_Report");
        cmd.addParameter("ATTR0");                       
        cmd.addParameter("ATTR1",attr1);       
        cmd.addParameter("ATTR2",attr2);              
        cmd.addParameter("ATTR3",attr3);      
        cmd.addParameter("ATTR4",attr4);       

        trans = mgr.perform(trans);

        String attr0 = trans.getValue("PRNT/DATA/ATTR0");

        ASPBuffer print = mgr.newASPBuffer();
        ASPBuffer printBuff=print.addBuffer("DATA");
        printBuff.addItem("RESULT_KEY",attr0);

        callPrintDlg(print,true);
    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void newRowHEAD()
    {
        ASPManager mgr = getASPManager();

        cmd = trans.addEmptyCommand("HEAD","WORK_ORDER_RETURNS_API.New__",headblk); 
        cmd.setParameter("WO_NO", upperset.getValue("WO_NO"));

        cmd.setOption("ACTION","PREPARE");

        trans = mgr.perform(trans);

        data = trans.getBuffer("HEAD/DATA");
        data.setFieldItem("WO_NO", upperset.getValue("WO_NO"));
        headset.addRow(data);
    }

    public void saveReturnHEAD()
    {
        ASPManager mgr = getASPManager();

        headset.changeRow();


        int currrow = headset.getCurrentRowNo();
        mgr.submit(trans);
        headset.goTo(currrow);
        headlay.setLayoutMode(headlay.getHistoryMode());    
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void okFind()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        q = trans.addQuery(upperblk);

        if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO"))){
	    q.addWhereCondition("WO_NO = ?");
	    q.addParameter("WO_NO",mgr.readValue("WO_NO"));
	}

        if (mgr.dataTransfered())
            q.addOrCondition(mgr.getTransferedData());

        q.includeMeta("ALL");
        mgr.querySubmit(trans, upperblk);

        if (upperset.countRows() > 0)
            okFindHEAD();
    }

    public void okFindHEAD()
    {
        ASPManager mgr = getASPManager();

        ASPBuffer buff = mgr.newASPBuffer();

        trans.clear();

        q = trans.addQuery(headblk);
        q.addWhereCondition("WO_NO = ?");
	q.addParameter("WO_NO",upperset.getValue("WO_NO"));

        q.includeMeta("ALL");
        mgr.querySubmit(trans, headblk);
        if (headset.countRows() > 0)
                    setValuesOwner();

    }

    public void  setValuesOwner()
    {
           ASPManager mgr = getASPManager();

           trans.clear();      

           int n = headset.countRows();

           if ( n > 0 )
           {
              headset.first();

              for ( int i=0; i<=n; ++i )
              {                    
                 String owner = headset.getRow().getFieldValue("OWNER");
                 String sOwnershipDb = headset.getRow().getFieldValue("PART_OWNERSHIP_DB");

                  if ("CUSTOMER OWNED".equals(sOwnershipDb)){
                      cmd = trans.addCustomFunction("GETOWNERNAME"+i, "CUSTOMER_INFO_API.Get_Name", "OWNER_NAME");
                      cmd.addParameter("OWNER",owner);
                  }
                  if ("SUPPLIER LOANED".equals(sOwnershipDb)){
                      cmd = trans.addCustomFunction("GETOWNERNAME1"+i, "Supplier_API.Get_Vendor_Name", "OWNER_NAME");
                      cmd.addParameter("OWNER",owner);
                  }
                 headset.next();
              }           

              trans = mgr.validate(trans);

              headset.first();

              for ( int i=0; i<n; ++i )
              {
                 String  ownerName = "";
                 row = headset.getRow();


                 if ("CUSTOMER OWNED".equals(headset.getRow().getFieldValue("PART_OWNERSHIP_DB"))){
                     ownerName= trans.getValue("GETOWNERNAME"+i+"/DATA/OWNER_NAME");
                 }
                 if ("SUPPLIER LOANED".equals(headset.getRow().getFieldValue("PART_OWNERSHIP_DB"))){
                     ownerName= trans.getValue("GETOWNERNAME1"+i+"/DATA/OWNER_NAME");
                 }
                 row.setValue("OWNER_NAME",ownerName);

                 headset.setRow(row);

                 headset.next();
              }
           }
           headset.first();
    }

    public void countFindHEAD()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        q = trans.addQuery(headblk);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ? AND CONTRACT = ?");
	q.addParameter("WO_NO",upperset.getValue("WO_NO"));
	q.addParameter("CONTRACT",upperset.getValue("CONTRACT"));
	mgr.submit(trans);
        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();
    }

// -----------------------------------------------------------------------------
// ------------------------        PREDEFINE     -------------------------------
// -----------------------------------------------------------------------------

    public void preDefine()
    {
        ASPManager mgr = getASPManager();

        upperblk = mgr.newASPBlock("UPPER");

        upperblk.addField("UPPER_OBJID").
        setDbName("OBJID").
        setHidden();

        upperblk.addField("UPPER_OBJVERSION").
        setDbName("OBJVERSION").
        setHidden();

        upperblk.addField("UPPER_OBJSTATE").
        setDbName("OBJSTATE").
        setHidden();

        upperblk.addField("COMPANY").
        setHidden();

        upperblk.addField("UPPER_WO_NO","Number","#").
        setDbName("WO_NO").
        setSize(13).
        setLabel("PCMWWORKORDERRETUPPWONO: WO No").
        setReadOnly().
        setMaxLength(8);

        upperblk.addField("UPPER_CONTRACT").
        setDbName("CONTRACT").
        setSize(5).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450).
        setLabel("PCMWWORKORDERRETUPPCONTRACT: Site").
        setUpperCase().
        setReadOnly().
        setMaxLength(5);

        upperblk.addField("UPPER_MCH_CODE").
        setDbName("MCH_CODE").
        setSize(13).
        setDynamicLOV("MAINTENANCE_OBJECT","UPPER_CONTRACT",600,450).
        setCustomValidation("UPPER_CONTRACT,UPPER_MCH_CODE","UPPER_DESCRIPTION").
        setLabel("PCMWWORKORDERRETUPPMCHCODE: Object ID").
        setUpperCase().
        setReadOnly().
        setMaxLength(100);

        upperblk.addField("UPPER_DESCRIPTION").
        setSize(28).
        setLabel("PCMWWORKORDERRETUPPOBJDESC: Object Description").
        setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:UPPER_CONTRACT,:UPPER_MCH_CODE)").
        setReadOnly().
        setMaxLength(2000);

        upperblk.addField("UPPER_STATE").
        setDbName("STATE").
        setSize(11).
        setLabel("PCMWWORKORDERRETUPPSTATE: Status").
        setReadOnly().
        setMaxLength(30); 

        upperblk.setView("ACTIVE_SEPARATE");

        upperset = upperblk.getASPRowSet();

        uppertbl = mgr.newASPTable(upperblk);
        uppertbl.setWrap();

        upperbar = mgr.newASPCommandBar(upperblk);

        upperbar.disableCommand(upperbar.FIND);
        upperbar.disableCommand(upperbar.COUNTFIND);
        upperbar.disableCommand(upperbar.BACK);

        upperlay = upperblk.getASPBlockLayout();
        upperlay.setDefaultLayoutMode(upperlay.SINGLE_LAYOUT);


        //----------------------------------------------------------------------------------------------------------------
        //----------------------------------------------------------------------------------------------------------------
        //----------------------------------------------------------------------------------------------------------------


        headblk = mgr.newASPBlock("HEAD");

        headblk.addField("OBJID").
        setHidden();

        headblk.addField("OBJVERSION").
        setHidden();

        headblk.addField("WO_NO","Number","#").
        setLabel("WORKORDERRETURNSHEADWONO: WO No").
        setHidden().
        setReadOnly();

        headblk.addField("LINE_NO","Number").
        setSize(13).
        setLabel("WORKORDERRETURNSLINENO: Line No").
        setReadOnly();

        headblk.addField("CONTRACT").
        setSize(5).
        setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445).
        setLabel("WORKORDERRETURNSCONTRACT: Site").
        setCustomValidation("CONTRACT,PART_NO,CONFIGURATION_ID,CONDITION_CODE,PART_OWNERSHIP,QTY_TO_RETURN,SERIAL_NO","INVENTORY_VALUE,TOTAL_VALUE").
        setUpperCase();

        headblk.addField("PART_NO").
        setSize(25).
        setLabel("WORKORDERRETURNSPARTNO: Part No:").
        setDynamicLOV("INVENTORY_PART_WO_LOV","CONTRACT",600,445).
        setMandatory().
        setUpperCase().
        setCustomValidation("CONTRACT,PART_NO,CONFIGURATION_ID,SERIAL_NO,CONDITION_CODE,PART_OWNERSHIP,QTY_TO_RETURN,OWNER","PART_OWNERSHIP,PART_OWNERSHIP_DB,INVENTORY_VALUE,TOTAL_VALUE,PARTDESCRIPTION,CONDITION_CODE,CONDITIONCODEDESC,DIMENTION_QUALITY,TYPE_DESIGNATION,OWNER").
        setMaxLength(25);

        headblk.addField("PARTDESCRIPTION").
        setSize(20).
        setReadOnly().
        setDefaultNotVisible().
        setFunction("PART_CATALOG_API.Get_Description(:PART_NO)");

        headblk.addField("SERIAL_NO").
        setSize(11).
        setLabel("WORKORDERRETURNSSERIALNO: Serial No").
        setDynamicLOV("PART_SERIAL_CATALOG","PART_NO",600,445).
        setUpperCase().
        setCustomValidation("CONTRACT,PART_NO,CONFIGURATION_ID,SERIAL_NO,CONDITION_CODE,PART_OWNERSHIP,QTY_TO_RETURN","PART_OWNERSHIP,CONDITION_CODE,CONDITIONCODEDESC,INVENTORY_VALUE,TOTAL_VALUE,OWNER").
        setMaxLength(50);

        headblk.addField("LOT_BATCH_NO").
        setSize(11).
        setDynamicLOV("LOT_BATCH_MASTER","PART_NO",600,445).
        setLabel("WORKORDERRETURNSLOTBATCHNO: Lot Batch No").
        setUpperCase();

        headblk.addField("CONFIGURATION_ID").
        setSize(20).
        setDynamicLOV("INVENTORY_PART_CONFIG","CONTRACT,PART_NO",600,445).
        setCustomValidation("CONTRACT,PART_NO,CONFIGURATION_ID,CONDITION_CODE,PART_OWNERSHIP,QTY_TO_RETURN,SERIAL_NO","INVENTORY_VALUE,TOTAL_VALUE").
        setLabel("WORKORDERRETURNSCONFIGID: Configuration ID");  

        headblk.addField("CONDITION_CODE").
        setDynamicLOV("CONDITION_CODE",600,445).
        setCustomValidation("CONTRACT,PART_NO,CONFIGURATION_ID,CONDITION_CODE,PART_OWNERSHIP,QTY_TO_RETURN,SERIAL_NO","INVENTORY_VALUE,TOTAL_VALUE,CONDITIONCODEDESC").
        setLabel("WORKORDERRETURNSCONDITIONCODE: Condition Code").
        setUpperCase();  

        headblk.addField("CONDITIONCODEDESC").
        setSize(20).
        setReadOnly().
        setDefaultNotVisible().
        setFunction("CONDITION_CODE_API.Get_Description(:CONDITION_CODE)");

        headblk.addField("PART_OWNERSHIP").
        setSize(25).
        setInsertable().
        setSelectBox().
        enumerateValues("PART_OWNERSHIP_API").
        setLabel("WORKORDERRETURNSPARTOWNERSHIP: Ownership").
        setCustomValidation("CONTRACT,PART_NO,CONFIGURATION_ID,CONDITION_CODE,PART_OWNERSHIP,QTY_TO_RETURN,SERIAL_NO","INVENTORY_VALUE,TOTAL_VALUE,PART_OWNERSHIP_DB");

        headblk.addField("PART_OWNERSHIP_DB").
        setHidden();

        headblk.addField("OWNER").
        setSize(15).
        setMaxLength(20).
        setInsertable().
        setLabel("WORKORDERRETURNSPARTOWNER: Owner").
        setCustomValidation("OWNER,PART_OWNERSHIP_DB","OWNER_NAME,WO_CUST").
        setDynamicLOV("CUSTOMER_INFO").
        setUpperCase();

        headblk.addField("WO_CUST").
        setSize(20).
        setHidden().
        setFunction("ACTIVE_SEPARATE_API.Get_Customer_No(:WO_NO)");

        headblk.addField("OWNER_NAME").
        setSize(20).
        setMaxLength(100).
        setReadOnly().
        setDefaultNotVisible().
        setLabel("WORKORDERRETURNSPARTOWNERNAME: Owner Name").
        setFunction("CUSTOMER_INFO_API.Get_Name(:OWNER)");

        headblk.addField("DIMENTION_QUALITY").
        setSize(11).
        setFunction("INVENTORY_PART_API.Get_Dim_Quality(:CONTRACT,:PART_NO)").
        setReadOnly().
        setLabel("WORKORDERRETURNSDIMQTY: Dimension/Quality");

        headblk.addField("TYPE_DESIGNATION").
        setSize(20).
        setReadOnly().
        setLabel("WORKORDERRETURNSTYPEDES: Type Designation").
        setFunction("INVENTORY_PART_API.Get_Type_Designation(:CONTRACT,:PART_NO)");

        headblk.addField("QTY_TO_RETURN","Number").
        setLabel("WORKORDERRETURNSQTYTORETURN: Quantity to Return").
        setMandatory().
        setCustomValidation("PART_OWNERSHIP,QTY_TO_RETURN,INVENTORY_VALUE","TOTAL_VALUE").
        setSize(20);

        headblk.addField("QTY_RETURNED","Number").
        setSize(20).
        setReadOnly().
        setLabel("WORKORDERRETURNSQTYRETURN: Quantity Returned");

        headblk.addField("MTRL_ORDER_NO","Number").
        setSize(20).
        setDynamicLOV("MAINT_MATERIAL_REQUISITION",600,445).
        setLabel("WORKORDERRETURNSMTRLORDERNO: Mtrl Order No");

        headblk.addField("MTRL_ORDER_LINE_NO","Number").
        setSize(20).
        setDynamicLOV("MAINT_MATERIAL_REQ_LINE",600,445).
        setLabel("WORKORDERRETURNSMTRLORDERLINE: Mtrl Order Line No");

        headblk.addField("INVENTORY_VALUE","Number").
        setSize(20).
        setReadOnly().
        setLabel("WORKORDERRETURNSINVVALUE: Inventory Value").
        setFunction("Work_Order_Returns_API.Get_Inventory_Value(WO_NO, LINE_NO, CONTRACT, PART_NO, CONFIGURATION_ID,CONDITION_CODE,PART_OWNERSHIP)");  

        headblk.addField("TOTAL_VALUE","Number").
        setSize(20).
        setReadOnly().
        setLabel("WORKORDERRETURNSTOTALVALUE: Total Value").
        setFunction("Work_Order_Returns_API.Get_Inventory_Value(WO_NO, LINE_NO, CONTRACT, PART_NO, CONFIGURATION_ID,CONDITION_CODE,PART_OWNERSHIP)* QTY_TO_RETURN");

        headblk.addField("ACQUISITION_VALUE","Number").
        setSize(20).
        setLabel("WORKORDERRETURNSACQUISVALUE: Acquisition Value");

        headblk.addField("INV_TRACK").
        setFunction("''").
        setHidden();

        headblk.addField("AFT_TRACK").
        setFunction("''").
        setHidden();

        headblk.addField("CHECK_EXIST").
        setFunction("''").
        setHidden();

        headblk.addField("PART_EXIST").
        setFunction("''").
        setHidden();

        headblk.addField("OBJSTATE").
        setFunction("''").
        setHidden();

        headblk.addField("VALFORRET").
        setFunction("''").
        setHidden();     

        headblk.addField("TRANSID").
        setFunction("''").
        setHidden();

        headblk.addField("VENDOR_NO").
        setFunction("''").
        setHidden(); 

        headblk.addField("CUSTOMER_NO").
        setFunction("''").
        setHidden();     

        headblk.setView("WORK_ORDER_RETURNS");
        headblk.defineCommand("WORK_ORDER_RETURNS_API","New__,Modify__,Remove__");
        headblk.setMasterBlock(upperblk);

        headset = headblk.getASPRowSet();

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("WORKORDERRETURNSRETURNS: Returns"));
        headtbl.setWrap();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.enableCommand(headbar.FIND);

        headbar.defineCommand(headbar.OKFIND, "okFindHEAD");
        headbar.defineCommand(headbar.COUNTFIND, "countFindHEAD");
        headbar.defineCommand(headbar.NEWROW, "newRowHEAD");
        headbar.defineCommand(headbar.SAVERETURN, "saveReturnHEAD", "checkHeadOwner()");
        headbar.defineCommand(headbar.SAVENEW, null, "checkHeadOwner()");

        headbar.addCustomCommand("invPartStock",mgr.translate("WORKORDERRETURNSINVPARTSTOCK: Move Repair/Exchange Part to Inventory..."));
        headbar.addCustomCommand("matTagReport",mgr.translate("WORKORDERRETURNSMATTAGREP: Print Tag Report..."));

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        headlay.setSimple("PARTDESCRIPTION");
        headlay.setSimple("CONDITIONCODEDESC");

        //headlay.setDataSpan("CURRENCY_CODE",2);

        //-----------------------------------------------------------------------
        //------------------  block used for RMB print funtion  -----------------
        //-----------------------------------------------------------------------

        prntblk = mgr.newASPBlock("RMBBLK");
        prntblk.addField("ATTR0");
        prntblk.addField("ATTR1");
        prntblk.addField("ATTR2");
        prntblk.addField("ATTR3");
        prntblk.addField("ATTR4");
        prntblk.addField("RESULT_KEY");
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        String lovwhere = "inventory_part_api.check_exist_anywhere(part_no) = 'TRUE'";

        cmd = trans.addCustomFunction("PARTEXIST","Part_Catalog_API.Check_Part_Exists2","PART_EXIST");
        cmd.addParameter("PART_NO",mgr.readValue("PART_NO"));

        mgr.perform(trans);

        String partExist_ = trans.getValue("PARTEXIST/DATA/PART_EXIST");

        if ("1".equals(partExist_))
        {
            lovwhere = "PART_NO='"+mgr.readValue("PART_NO")+"' AND OBJSTATE IN ('Issued', 'InFacility','Contained')";
            mgr.getASPField("SERIAL_NO").setLOVProperty("WHERE","PART_NO='"+mgr.readValue("PART_NO")+"'");
        }
        else
	{
	    lovwhere = lovwhere + " AND OBJSTATE IN ('Issued', 'InFacility','Contained')";
            mgr.getASPField("SERIAL_NO").setLOVProperty("WHERE",lovwhere);
	}

        if (!mgr.isEmpty(callingUrl))
        {
            if (callingUrl.indexOf("HistoricalRound") > 0 || callingUrl.indexOf("HistoricalSeparateRMB") > 0)
            {
                headbar.disableCommand(headbar.DUPLICATEROW);
                headbar.disableCommand(headbar.NEWROW);
                headbar.disableCommand(headbar.DELETE);
            }
        }

        if (headlay.isSingleLayout())
        {
            if (!activateInvPartStock())
                headbar.removeCustomCommand("invPartStock");
        }
        
        if (headlay.isVisible())
        {
            mgr.getASPField("CONTRACT").setLOVProperty("WHERE","Site_API.Get_Company(CONTRACT) = '"+upperset.getRow().getValue("COMPANY")+"'");
        }

    }

//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return "PCMWORKORDERRETURNS: Returns";
    }

    protected String getTitle()
    {
        return "PCMWORKORDERRETURNS: Returns";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(upperlay.show());

        if (upperset.countRows() > 0)
            appendToHTML(headlay.show());

        appendToHTML("<br>\n");

        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

        appendDirtyJavaScript("window.name = \"WOReturns\";\n");

        appendDirtyJavaScript("if('");
        appendDirtyJavaScript(performRMB);
        appendDirtyJavaScript("' == \"TRUE\")\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("  url_to_go = '");
        appendDirtyJavaScript(URLString);
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("  window_name = '");
        appendDirtyJavaScript(WindowName);
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("  window.open(url_to_go,window_name,\"resizable,status=yes,width=750,height=550\");\n");
        appendDirtyJavaScript("}\n\n");    

        appendDirtyJavaScript("function validatePartOwnership(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("		if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("			setDirty();\n");
        appendDirtyJavaScript("		if( !checkPartOwnership(i) ) return;\n");
        appendDirtyJavaScript("		if( getValue_('CONTRACT',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("		if( getValue_('PART_NO',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("		if( getValue_('CONFIGURATION_ID',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("		if( getValue_('CONDITION_CODE',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("		if( getValue_('PART_OWNERSHIP',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("		if( getValue_('QTY_TO_RETURN',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("		if( getValue_('PART_OWNERSHIP',i)=='' )\n");
        appendDirtyJavaScript("		{\n");
        appendDirtyJavaScript("			getField_('INVENTORY_VALUE',i).value = '';\n");
        appendDirtyJavaScript("			getField_('TOTAL_VALUE',i).value = '';\n");
        appendDirtyJavaScript("			getField_('PART_OWNERSHIP_DB',i).value = '';\n");
        appendDirtyJavaScript("			return;\n");
        appendDirtyJavaScript("		}\n");
        appendDirtyJavaScript("		window.status='Please wait for validation';\n");
        appendDirtyJavaScript("		r = __connect(\n");
        appendDirtyJavaScript("			'" + mgr.getURL() + "?VALIDATE=PART_OWNERSHIP'\n");
        appendDirtyJavaScript("			+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
        appendDirtyJavaScript("			+ '&PART_NO=' + URLClientEncode(getValue_('PART_NO',i))\n");
        appendDirtyJavaScript("			+ '&CONFIGURATION_ID=' + URLClientEncode(getValue_('CONFIGURATION_ID',i))\n");
        appendDirtyJavaScript("			+ '&CONDITION_CODE=' + URLClientEncode(getValue_('CONDITION_CODE',i))\n");
        appendDirtyJavaScript("			+ '&PART_OWNERSHIP=' + SelectURLClientEncode('PART_OWNERSHIP',i)\n");
        appendDirtyJavaScript("			+ '&QTY_TO_RETURN=' + URLClientEncode(getValue_('QTY_TO_RETURN',i))\n");
        appendDirtyJavaScript("			+ '&SERIAL_NO=' + URLClientEncode(getValue_('SERIAL_NO',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("		window.status='';\n\n");
        appendDirtyJavaScript("		if( checkStatus_(r,'PART_OWNERSHIP',i,'Ownership') )\n");
        appendDirtyJavaScript("		{\n");
        appendDirtyJavaScript("			assignValue_('INVENTORY_VALUE',i,0);\n");
        appendDirtyJavaScript("			assignValue_('TOTAL_VALUE',i,1);\n");
        appendDirtyJavaScript("			assignValue_('PART_OWNERSHIP_DB',i,2);\n\n");
        appendDirtyJavaScript("			if (getValue_('PART_OWNERSHIP_DB',i) == 'CONSIGNMENT')\n");
        appendDirtyJavaScript("			{\n");

        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWWORKORDERRETURNSINVOWNER1: Ownership type Consignment is not allowed in Work Order Returns to Inventory."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("				f.PART_OWNERSHIP.value = '';\n");
        appendDirtyJavaScript("				f.INVENTORY_VALUE.value = 0.0;\n");
        appendDirtyJavaScript("				f.TOTAL_VALUE.value = 0.0;\n");
        appendDirtyJavaScript("				f.PART_OWNERSHIP_DB.value = '';\n");
        appendDirtyJavaScript("			}\n\n");
        appendDirtyJavaScript("		}\n");
        appendDirtyJavaScript("}\n\n");

        appendDirtyJavaScript("function lovOwner(i,params)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	  if(params) param = params;\n");
        appendDirtyJavaScript("	  else param = '';\n");
        appendDirtyJavaScript("	  var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
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

        appendDirtyJavaScript("function checkHeadOwner()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if (checkHeadFields())\n");
        appendDirtyJavaScript("   {\n");
        //appendDirtyJavaScript("      alert(f.WO_CUST.value);\n");
        //appendDirtyJavaScript("      alert(f.OWNER.value);\n");
        appendDirtyJavaScript("      if (f.WO_CUST.value != '' && f.OWNER.value != '' && f.WO_CUST.value != f.OWNER.value && f.PART_OWNERSHIP_DB.value == 'CUSTOMER OWNED')  \n");
        appendDirtyJavaScript("         return confirm('");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWWORKORDERRETURNSDIFFCUST: Owning Customer No is not the same as the Work Order Customer No. Still want to save this record?"));
        appendDirtyJavaScript("');\n");
        appendDirtyJavaScript("      else if (f.WO_CUST.value == '' && f.OWNER.value != '' && f.PART_OWNERSHIP_DB.value == 'CUSTOMER OWNED')  \n");
        appendDirtyJavaScript("         return confirm('");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWWORKORDERRETURNSNOCUST: There is no Customer specified for the Work Order although you have entered an Owning Customer No. Still want to save this record?"));
        appendDirtyJavaScript("');\n");
        appendDirtyJavaScript("      else \n");
        appendDirtyJavaScript("         return true;\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function validateOwner(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("   if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
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
        appendDirtyJavaScript("                    + '&PART_OWNERSHIP_DB=' + URLClientEncode(getValue_('PART_OWNERSHIP_DB',i))\n");
        appendDirtyJavaScript("                    + '&WO_NO=' + URLClientEncode(getValue_('WO_NO',i))\n");
        appendDirtyJavaScript("                   );\n");
        appendDirtyJavaScript("      window.status='';\n");
        appendDirtyJavaScript("      if( checkStatus_(r,'OWNER',i,'Owner') )\n");
        appendDirtyJavaScript("      {\n");
        appendDirtyJavaScript("         assignValue_('OWNER_NAME',i,0);\n");
        appendDirtyJavaScript("         assignValue_('WO_CUST',i,1);\n");
        appendDirtyJavaScript("      }\n");
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'COMPANY OWNED' && f.OWNER.value != '') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWWORKORDERRETURNSINVOWNER11: Owner should not be specified for Company Owned Stock."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.OWNER.value = ''; \n");
        appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == 'CONSIGNMENT' && f.OWNER.value != '') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWWORKORDERRETURNSINVOWNER12: Owner should not be specified for Consignment Stock."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.OWNER.value = ''; \n");
        appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("   if (f.PART_OWNERSHIP_DB.value == '' && f.OWNER.value != '') \n");
        appendDirtyJavaScript("   { \n");
        appendDirtyJavaScript("      alert('");
        appendDirtyJavaScript(       mgr.translateJavaScript("PCMWWORKORDERRETURNSINVOWNER14: Owner should not be specified when there is no Ownership type."));
        appendDirtyJavaScript("          ');\n");
        appendDirtyJavaScript("      f.OWNER.value = ''; \n");
        appendDirtyJavaScript("      f.OWNER_NAME.value = ''; \n");   
        appendDirtyJavaScript("   } \n");
        appendDirtyJavaScript("}\n");
    }
}
