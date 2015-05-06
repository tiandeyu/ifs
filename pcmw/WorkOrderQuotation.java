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
*  File        : WorkOrderQuotation.java 
*  Created     : ASP2JAVA Tool  010312 Created Using the ASP file WorkOrderQuotation.page
*  Modified    :
*  JEWILK  010322  Corrected conversion errors and done necessary adjustments.
*  JEWILK  010523  Changed the setDynamicLOV() method of the field 'WO_NO'to setLOV(). 
*                  Changed the order of the fields 'CONTRACT' and 'MCH_CODE' to fix the saving problem in the ITEM1 block.
*  VAGULK  010726  77848 - Filtered the Standard Job Id by Site
*  CHCRLK  010807  Modified print method. 
*  JEWILK  010807  Set field 'ITEM0_MCH_CODE' editable. (call # 77859)
*  ARWILK  010904  Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction(). 
*                  (Caused when cancelNew is performed after saveNew)
*  JEWILK  011008  Checked security permissions for RMB Actions.
*  SHAFLK  021108  Bug Id 34064,Changed method print. 
*  BUNILK  021205  Added new field Object Site and replaced work order site by Object site whenever it refer to Object.    
*  GACOLK  021205  Set max length of NEWOBJ_SERIAL_NO to 50
*                  Set max length of HEAD_MCH_CODE, ITEM0_MCH_CODE, MCH_CODE, NEWOBJ_MCH_CODE to 100
*  INROLK  030627  IIDADAM319N Work Order Quotation for VIM Serial. Added Connection_type and removed group Object
*  CHAMLK  030901  Moved New Equipment Object to the Prepare tab. Passed Ownership and Owner to dialog New Equipment Object.
*  JEWILK  030924  Set fields in Object group hidden in preDefine(), to avoid the javascript error caused by checkHeadFields(). Call 101351.
*  SHTOLK  031012  Call Id 106312, Modified validate() for "ITEM0_MCH_CODE". Replace "MCH_CODE" with "ITEM0_MCH_CODE"
*  THWILK  031014  Call ID 106436 Added an extra parameter(#)to the deffinition of the field QUOTATION_ID.
*  JEWILK  031016  Corrected few javascript errorrs which occured when returning from RMBs.
*  JEWILK  031016  Removed setUpperCase() method from CONNECTION_TYPE. Call 106548.
*  JEWILK  031023  Removed setMandatory() method from CONNECTION_TYPE. Corrected validation of ITEM0_MCH_CODE. Call 106548
*  CHAMLK  031029  Modified function run() to avoid call to okFindEditCust since now the customer on the quptation is updated in NewCustomerDlg itself.
*  ARWILK  031211  Edge Developments - (Removed clone and doReset Methods)
*  VAGULK  040106  Made the field order according to the order in the Centura application(Web Alignment).
*  BUNILK  040202  Edge Developments - (Enable Multirow RMB actions, Remove uneccessary global variables, Enhance Performance, Clean Javascript Code)
*
* -------------------------------- Edge - SP1 Merge -------------------------
*  SHAFLK  040120  Bug Id 41815,Removed Java dependencies.
*  SHAFLK  040219  Bug Id 42419,Modified validation of field MCH_CODE,
*  SHAFLK  040301  Bug Id 42419,Modified validation of field MCH_CODE,
*  THWILK  040322  Merge with SP1.
*  ARWILK  040616  Removed STD_JOB_ID from itemblk0. Did not add jobs tab to itemblk0 because the user can 
*                  use the WO hyperlink. (Spec - AMEC109A)  
*  ARWILK  040723  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  Chanlk  050215  Remove the fields MCH _CODE and related fields and operations.
*  SHAFLK  050425  Bug ID 50830,Used mgr.translateJavaScript() method to get the correctly translated strings in JavaScript functions.
*  NIJALK  050617  Merged Bug 50830.
*  SHAFLK  050919  Bug ID 52880,Modifiedvalidate().
*  NIJALK  051004  Merged bug 52880.
*  JEWILK  050111  Corrected date format problems. 
*  ASSALK  060227  Call: 135695. Changed the LOV page of the Agreement_id.
*  NEKOLK  060228  Call 135724. Modified newObject().
*  SULILK  060301  Call 135698: Modified adjust()
*  NIJALK  060301  Call 135697: Added RMBs connectExtWorkOrder,removeWorkOrder and their functionality. 
*  NEKOLK  060301  Call 135708: Modified   newCustomer() and run().
*  JAPALK  060308  Call ID 135707. Modified Validate() mathod.
*  ASSALK  060308  Call ID 135739. Added new RMB 'Agreements for VIM Serial Object'.
*  THWILK  060308  Call 136757, Modified run().
*  NIJALK  060321  Call 137727: Modified preDefine().
*  NEKOLK  060510  Bug ID 57371,Modified predefine().
* ----------------------------------------------------------------------------
*  AMNILK  060629  Merged with SP1 APP7.
*  AMNILK  060731  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMDILK  060807  Merged with Bug Id 58214
*  ILSOLK  060817  Set the MaxLength of Address1 as 100 .
*  AMDILK  060829  Bug 58216, Eliminated SQL errors in web applications. Modified the methods validate(), okFindForEdit(), 
*                             okFindITEM0(), countFindITEM0(), connectExtWorkOrder(), searchEvent(), okFindPTIIA(), okFindAfterUpd(), setBudgetTotals()
*  AMDILK  060906  Merged with the Bug Id 58216.
*  SHAFLK  060817  Bug 59691, Modified the customer address functionality in this page.
*  ILSOLK  060914  Merged Bug 59691. 
*  ILSOLK  070215  Modifed for MTPR904 Hink tasks ReqId 45088.setLovPropery() for Object Id. 
*  BUNILK  070504  Implemented "MTIS907 New Service Contract - Services" changes.
*  AMDILK  070404  Eliminate script errors
*  ARWILK  070410  MTIS907: Replaced calls to SERIAL_PART_AGR_DETAILS_API with Psc_Contr_Product_API. Also added hidden field LINE_NO to header.
*  ARWILK  070410  MTIS907: Removed references to VIMSerialPartAgreementsDlg.
*  ILSOLK  070427  Modified for Call Id 142908.( added 'Currency Code' field into General tab.)
*  AMDILK  070429  Call Id 143012: Modified adjust() to disable the field "Site" when the connection type is "VIM"
*  CHANLK  070518  Call 144303 Replace agreement connections with SRV CON
*  NIJALK  070525  Bug 64744, Modified run(), saveReturnITEM0(), preDefine() and printContents(). Added updatePostings().
*  AMDILK  070716  Merged bug 64744
*  AMNILK  070727  Eliminated XXS Security Vulnerabilities.
*  AMDILK  070801  Removed the scroll buttons of the parent when the detail block is in new or edit modes
*  HARPLK  090709  Bug 84436, Modified preDefine().
*  NIJALK  100218  Bug 87766, Modified preDefine().
*  ILSOLK  100916  Bug 93061, Modified saveReturnITEM0().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class WorkOrderQuotation extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.WorkOrderQuotation");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================

    private ASPContext ctx;
    private ASPBlock ciblk;
    private ASPRowSet ciset;
    private ASPCommandBar cibar;
    private ASPBlock ptiiablk;
    private ASPRowSet ptiiaset;
    private ASPBlock actcostblk;
    private ASPRowSet actcostset;
    private ASPBlock revblk;
    private ASPRowSet revset;
    private ASPBlock newobjblk;
    private ASPRowSet newobjset;
    private ASPBlock eventblk;
    private ASPRowSet eventset;
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
    private ASPField f;
    private ASPBlockLayout ptiialay;
    private ASPBlock b;

    private ASPField fDELIVERYADDRESS1;
    private ASPField fDELIVERYADDRESS2;
    private ASPField fDELIVERYADDRESS3;
    private ASPField fDELIVERYADDRESS4;
    private ASPField fDELIVERYADDRESS5;
    private ASPField fDELIVERYADDRESS6;
    private ASPField fDELIVERYADDRESS7;
    private ASPField fDELIVERYADDRESS8;

    private ASPField fCOUNTRY;
    private ASPField fADDRESS1;
    private ASPField fADDRESS2;
    private ASPField fZIP_CODE;
    private ASPField fCITY;
    private ASPField fCOUNTRY_CODE;
    private ASPField fSTATE;   
    private ASPField fCOUNTY;

    private String newWinHandle;
    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private boolean headSearch;
    private boolean qryStrSearch;
    private boolean isSecurityChecked;
    private boolean bOpenNewWindow;

    private ASPTransactionBuffer trans;
    private ASPTransactionBuffer trans1;
    private ASPBuffer actionsBuffer;

    private ASPCommand cmd;
    private int i;
    private int n;

    private String triger;
    private String triger1;
    private String triger2;
    private String triger3;
    private String sAgreementId;
    private String sCustomerId;
    private String sObjectId;
    private String calling_url;
    private String sQuotationId;
    private String head_command;
    private String sOwnership;
    private String sOwner;
    private String urlString;
    private String sCustId;
    private String sContract;
    private int headCurrrow;
    private String current_url;
    private String qrystr;
    private String sLastWoNo;
    private boolean bPpChanged;

    //===============================================================
    // Construction 
    //===============================================================
    public WorkOrderQuotation(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {

        triger = triger1;
        triger1 = triger2;
        triger2 = triger3;
        triger3 = "false";
        headSearch = false;
        qryStrSearch = false;
        sOwnership = "";
        sOwner = "";
        String mWoNo;
        sCustId = "";
        sContract = "";

        ASPManager mgr = getASPManager();

        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();
        trans1 = mgr.newASPTransactionBuffer();

        isSecurityChecked = ctx.readFlag("SECURITYCHECKED",false);
        actionsBuffer = ctx.readBuffer("ACTIONSBUFFER");
        sOwnership = ctx.readValue("OWNERSHIP",sOwnership);
        sOwner = ctx.readValue("OWNER",sOwner);
        sCustId = ctx.readValue("CUSTID","");
        sContract = ctx.readValue("SCONT","");
        qrystr = ctx.readValue("QRYSTR", "");
        sLastWoNo = ctx.readValue("LASTWONO", "");

        if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();

        else if (mgr.commandBarActivated())
        {
            eval(mgr.commandBarFunction());
        }
        else if (mgr.dataTransfered())
        {
            qryStrSearch=true; 
            okFind();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
        {
            validate();
        }

        else if ((!mgr.isEmpty(mgr.getQueryStringValue("QUOTATION_ID"))) && (!mgr.isEmpty(mgr.getQueryStringValue("MCH_CODE"))))
            okFindEditParentObj();

        else if ((!mgr.isEmpty(mgr.getQueryStringValue("QUOTATION_ID"))) && (!mgr.isEmpty(mgr.getQueryStringValue("AGREEMENT_ID"))))
            okFindEditAgr();

        else if (!mgr.isEmpty(mgr.getQueryStringValue("QUOTATION_ID")))
        {
                      
            if (headset.countRows()>0)
            {
                headset.refreshRow();
            }
            else
            {
                qryStrSearch=true;   
                okFind();
            }
           
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
        {
            mWoNo = mgr.readValue("WO_NO");
            okFind();   
        }
        else if ("TRUE".equals(mgr.readValue("ISHIDDENWO")))
            connectExtWorkOrder();
        else if ("TRUE".equals(mgr.readValue("PRPOSTCHANGED")))
            updatePostings();

        checkObjAvailable();
        adjust();
        adjustActions();

        ctx.writeFlag("SECURITYCHECKED",isSecurityChecked);
        ctx.writeBuffer("ACTIONSBUFFER",actionsBuffer);
        ctx.writeValue("OWNERSHIP",sOwnership);
        ctx.writeValue("OWNER",sOwner);
        ctx.writeValue("CUSTID",sCustId);
        ctx.writeValue("SCONT",sContract);
	ctx.writeValue("QRYSTR", qrystr);
        ctx.writeValue("LASTWONO", sLastWoNo);
    }

//-----------------------------------------------------------------------------
//-----------------------------  PERFORM FUNCTION  ----------------------------
//-----------------------------------------------------------------------------

    public void perform( String command) 
    {
        int currrow = 0;

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
            currrow = headset.getCurrentRowNo();
            mgr.submit(trans);

            headset.goTo(currrow);

        }
        setNewObjEvent(currrow);  
    }


//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();

        String val = mgr.readValue("VALIDATE");


        if ("HEAD_CONTRACT".equals(val))
        {
            cmd = trans.addCustomFunction( "COMP", "SITE_API.Get_Company", "SITECOMPANY" );
            cmd.addParameter("HEAD_CONTRACT", mgr.readValue("HEAD_CONTRACT"));

            trans = mgr.validate(trans);

            String siteCompany = trans.getValue("COMP/DATA/SITECOMPANY");
            mgr.responseWrite(siteCompany);
        }
        else if ("SALESMAN_CODE".equals(val))
        {
            cmd = trans.addCustomFunction( "SALESMAN", "SALES_PART_SALESMAN_API.Get_Name", "SALESPARTSALESMANNAME" );
            cmd.addParameter("CUSTOMER_ID", mgr.readValue("SALESMAN_CODE"));

            trans = mgr.validate(trans);

            String salesmanName = trans.getValue("SALESMAN/DATA/SALESPARTSALESMANNAME");
            mgr.responseWrite(salesmanName);
        }
        else if ("CUSTOMER_ID".equals(val))
        {
            ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer();
            secBuff.addSecurityQuery("Cust_Ord_Customer");

            secBuff = mgr.perform(secBuff);

            if (secBuff.getSecurityInfo().itemExists("Cust_Ord_Customer"))
            {
                cmd = trans.addCustomFunction("ISCRESTOP","Cust_Ord_Customer_API.Customer_Is_Credit_Stopped","CREDITSTOP");
                cmd.addParameter("CUSTOMER_ID");
                cmd.addParameter("SITECOMPANY");
            }

            cmd = trans.addCustomFunction( "CUSTGROUP", "CUST_ORD_CUSTOMER_API.Get_Cust_Grp", "CUSTOMERGROUP" );
            cmd.addParameter("CUSTOMER_ID", mgr.readValue("CUSTOMER_ID"));

            cmd = trans.addCustomFunction( "CUSTNAME", "CUSTOMER_INFO_API.Get_Name", "CUSTOMERINFONAME" );
            cmd.addParameter("CUSTOMER_ID", mgr.readValue("CUSTOMER_ID"));

            cmd = trans.addCustomFunction( "REF", "CUST_ORD_CUSTOMER_API.Get_Cust_Ref", "REFERENCE" );
            cmd.addParameter("CUSTOMER_ID", mgr.readValue("CUSTOMER_ID"));

            cmd = trans.addCustomFunction( "VADDR1", "WORK_ORDER_QUOTATION_API.Get_Visit_Address_Line1", "VISITADDRESS1" );
            cmd.addParameter("CUSTOMER_ID", mgr.readValue("CUSTOMER_ID"));
            cmd.addParameter("VIS_ADD_ID", mgr.readValue("VIS_ADD_ID"));

            cmd = trans.addCustomFunction( "VADDR2", "WORK_ORDER_QUOTATION_API.Get_Visit_Address_Line2", "VISITADDRESS2" );
            cmd.addParameter("CUSTOMER_ID", mgr.readValue("CUSTOMER_ID"));
            cmd.addParameter("VIS_ADD_ID", mgr.readValue("VIS_ADD_ID"));

            cmd = trans.addCustomFunction( "VADDR3", "WORK_ORDER_QUOTATION_API.Get_Visit_Address_Line3", "VISITADDRESS3" );
            cmd.addParameter("CUSTOMER_ID", mgr.readValue("CUSTOMER_ID"));
            cmd.addParameter("VIS_ADD_ID", mgr.readValue("VIS_ADD_ID"));

            cmd = trans.addCustomFunction( "VADDR4", "WORK_ORDER_QUOTATION_API.Get_Visit_Address_Line4", "VISITADDRESS4" );
            cmd.addParameter("CUSTOMER_ID", mgr.readValue("CUSTOMER_ID"));
            cmd.addParameter("VIS_ADD_ID", mgr.readValue("VIS_ADD_ID"));

            cmd = trans.addCustomFunction( "VADDR5", "WORK_ORDER_QUOTATION_API.Get_Visit_Address_Line5", "VISITADDRESS5" );
            cmd.addParameter("CUSTOMER_ID", mgr.readValue("CUSTOMER_ID"));
            cmd.addParameter("VIS_ADD_ID", mgr.readValue("VIS_ADD_ID"));

            cmd = trans.addCustomFunction( "VADDR6", "WORK_ORDER_QUOTATION_API.Get_Visit_Address_Line6", "VISITADDRESS6" );
            cmd.addParameter("CUSTOMER_ID", mgr.readValue("CUSTOMER_ID"));
            cmd.addParameter("VIS_ADD_ID", mgr.readValue("VIS_ADD_ID"));

            cmd = trans.addCustomFunction( "DADDR1", "WORK_ORDER_QUOTATION_API.Get_Delivery_Address_Line1", "DELIVERYADDRESS1" );
            cmd.addParameter("CUSTOMER_ID", mgr.readValue("CUSTOMER_ID"));
            cmd.addParameter("DEL_ADD_ID", mgr.readValue("DEL_ADD_ID"));

            cmd = trans.addCustomFunction( "DADDR2", "WORK_ORDER_QUOTATION_API.Get_Delivery_Address_Line2", "DELIVERYADDRESS2" );
            cmd.addParameter("CUSTOMER_ID", mgr.readValue("CUSTOMER_ID"));
            cmd.addParameter("DEL_ADD_ID", mgr.readValue("DEL_ADD_ID"));

            cmd = trans.addCustomFunction( "DADDR3", "WORK_ORDER_QUOTATION_API.Get_Delivery_Address_Line3", "DELIVERYADDRESS3" );
            cmd.addParameter("CUSTOMER_ID", mgr.readValue("CUSTOMER_ID"));
            cmd.addParameter("DEL_ADD_ID", mgr.readValue("DEL_ADD_ID"));

            cmd = trans.addCustomFunction( "DADDR4", "WORK_ORDER_QUOTATION_API.Get_Delivery_Address_Line4", "DELIVERYADDRESS4" );
            cmd.addParameter("CUSTOMER_ID", mgr.readValue("CUSTOMER_ID"));
            cmd.addParameter("DEL_ADD_ID", mgr.readValue("DEL_ADD_ID"));

            cmd = trans.addCustomFunction( "DADDR5", "WORK_ORDER_QUOTATION_API.Get_Delivery_Address_Line5", "DELIVERYADDRESS5" );
            cmd.addParameter("CUSTOMER_ID", mgr.readValue("CUSTOMER_ID"));
            cmd.addParameter("DEL_ADD_ID", mgr.readValue("DEL_ADD_ID"));

            cmd = trans.addCustomFunction( "DADDR6", "WORK_ORDER_QUOTATION_API.Get_Delivery_Address_Line6", "DELIVERYADDRESS6" );
            cmd.addParameter("CUSTOMER_ID", mgr.readValue("CUSTOMER_ID"));
            cmd.addParameter("DEL_ADD_ID", mgr.readValue("DEL_ADD_ID"));

            cmd = trans.addCustomFunction( "DISCCLASS", "CUST_ORD_CUSTOMER_API.Get_Discount_Class", "DISCOUNTCLASS" );
            cmd.addParameter("CUSTOMER_ID", mgr.readValue("CUSTOMER_ID"));

            cmd = trans.addCustomFunction( "CURR", "CUST_ORD_CUSTOMER_API.Get_Currency_Code", "CURRENCY" );
            cmd.addParameter("CUSTOMER_ID", mgr.readValue("CUSTOMER_ID"));

            trans = mgr.validate(trans);

            String crStop = "2";

            if (secBuff.getSecurityInfo().itemExists("Cust_Ord_Customer"))
                crStop = trans.getValue("ISCRESTOP/DATA/CREDITSTOP");

            String cusgroup = trans.getValue("CUSTGROUP/DATA/CUSTOMERGROUP");
            String cusname = trans.getValue("CUSTNAME/DATA/CUSTOMERINFONAME");
            String refer = trans.getValue("REF/DATA/REFERENCE");
            String vadd1 = trans.getValue("VADDR1/DATA/VISITADDRESS1");
            String vadd2 = trans.getValue("VADDR2/DATA/VISITADDRESS2");
            String vadd3 = trans.getValue("VADDR3/DATA/VISITADDRESS3");
            String vadd4 = trans.getValue("VADDR4/DATA/VISITADDRESS4");
            String vadd5 = trans.getValue("VADDR5/DATA/VISITADDRESS5");
            String vadd6 = trans.getValue("VADDR6/DATA/VISITADDRESS6");
            String dadd1 = trans.getValue("DADDR1/DATA/DELIVERYADDRESS1");
            String dadd2 = trans.getValue("DADDR2/DATA/DELIVERYADDRESS2");
            String dadd3 = trans.getValue("DADDR3/DATA/DELIVERYADDRESS3");
            String dadd4 = trans.getValue("DADDR4/DATA/DELIVERYADDRESS4");
            String dadd5 = trans.getValue("DADDR5/DATA/DELIVERYADDRESS5");
            String dadd6 = trans.getValue("DADDR6/DATA/DELIVERYADDRESS6");
            String discont = trans.getValue("DISCCLASS/DATA/DISCOUNTCLASS");
            String curr = trans.getValue("CURR/DATA/CURRENCY");

            trans.clear();

            ASPQuery q = trans.addQuery(ciblk);
            q.addWhereCondition("CUSTOMER_ID = ?");
	    q.addParameter("CUSTOMER_ID", mgr.readValue("CUSTOMER_ID"));
            q.includeMeta("ALL");

            q = trans.addQuery(ptiiablk);
            q.addMasterConnection("CI","CI_PARTY_TYPE_DB","PTIIA_PARTY_TYPE_DB");
            q.addMasterConnection("CI","CI_CUSTOMER_ID","PTIIA_IDENTITY");
            q.includeMeta("ALL");

            mgr.submit(trans);

            String vat = null;
            String payterm = null;
            if (ptiiaset.countRows() != 0)
            {
                vat = ptiiaset.getRow().getFieldValue("PTIIA_DEF_VAT_CODE");
                payterm = ptiiaset.getRow().getFieldValue("PTIIA_PAY_TERM_ID");
            }

            String delId="";
            String visId="";
            String txtCustId = (mgr.isEmpty(cusgroup) ? " " : (cusgroup)) + "^" + 
                               crStop + "^" +
                               (mgr.isEmpty(cusname) ? "" : (cusname)) + "^" +
                               (mgr.isEmpty(refer) ? "" : refer)+ "^"+
                               (mgr.isEmpty(vadd1) ? "" : vadd1)+ "^"+
                               (mgr.isEmpty(vadd2) ? "" : vadd2)+ "^"+
                               (mgr.isEmpty(vadd3) ? "" : vadd3)+ "^"+
                               (mgr.isEmpty(vadd4) ? "" : vadd4)+ "^"+
                               (mgr.isEmpty(vadd5) ? "" : vadd5)+ "^"+
                               (mgr.isEmpty(vadd6) ? "" : vadd6)+ "^"+
                               (mgr.isEmpty(dadd1) ? "" : dadd1)+ "^"+
                               (mgr.isEmpty(dadd2) ? "" : dadd2)+ "^"+
                               (mgr.isEmpty(dadd3) ? "" : dadd3)+ "^"+
                               (mgr.isEmpty(dadd4) ? "" : dadd4)+ "^"+
                               (mgr.isEmpty(dadd5) ? "" : dadd5)+ "^"+
                               (mgr.isEmpty(dadd6) ? "" : dadd6)+ "^"+
                               (mgr.isEmpty(discont) ? "" : discont)+ "^"+
                               (mgr.isEmpty(curr) ? "" : curr)+ "^"+
                               (mgr.isEmpty(vat) ? "" : vat)+ "^"+
                               (mgr.isEmpty(payterm) ? "" : payterm)+ "^" +
                               delId + "^" +
                               visId + "^" ;

            mgr.responseWrite(txtCustId);
        }
        else if ("ITEM0_MCH_CODE".equals(val))
        {
            ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer();
            secBuff.addSecurityQuery("Vehicle_Serial_Part_API");
            secBuff.addSecurityQuery("Vehicle_Serial_API");
            secBuff = mgr.perform(secBuff);

            String connTypeDB = (mgr.readValue("CONNECTION_TYPE_DB",""));
            String validationAttrAtr = "";
            String validationAttrAtr1 = "";
            String mchCode = "";
            String mchContract = "";
            String partno = "";
            String serialno = "";
            String machname ="";
            trans.clear();

            if ("EQUIPMENT".equals(connTypeDB))
            {
                String mchStr = mgr.readValue("ITEM0_MCH_CODE");
                if (mchStr.indexOf("~")>-1)
                {
                    String[] mchList = new String[3];

                    mchList = split(mchStr,"~");
                    mchCode = mchList[0];
                    mchContract = mchList[2];
                }
                else
                {
                    mchCode = mgr.readValue("ITEM0_MCH_CODE");
                    mchContract = mgr.readValue("ITEM0_MCHCONTRACT");
                }

                cmd = trans.addCustomFunction("MACHNAME","MAINTENANCE_OBJECT_API.Get_Mch_Name","DESCRIPTION");
                cmd.addParameter("ITEM0_MCHCONTRACT",mchContract);
                cmd.addParameter("ITEM0_MCH_CODE",mchCode);

                trans = mgr.validate(trans);

                machname = trans.getValue("MACHNAME/DATA/DESCRIPTION");

            }
            else
            {
                String vimStr = mgr.readValue("ITEM0_MCH_CODE");
                if (vimStr.indexOf("~")>-1)
                {
                    String[] vimList = new String[3];
                    vimList = split(vimStr,"~");
                    mchCode = vimList[0];
                    partno = vimList[1];
                    serialno = vimList[2];
                }
                else
                {

                    trans.clear();
                    mchCode = mgr.readValue("WO_NO");
                    cmd = trans.addCustomCommand("VIMOBJDETS","Active_Separate_API.Get_Vim_Part_Serial");                                
                    cmd.addParameter("PART_NO");
                    cmd.addParameter("SERIAL_NO");                                
                    cmd.addParameter("WO_NO",mchCode);

                    trans = mgr.validate(trans);

                    partno = trans.getValue("VIMOBJDETS/DATA/PART_NO");
                    serialno = trans.getValue("VIMOBJDETS/DATA/SERIAL_NO");
                    trans.clear();

                    if (mgr.isEmpty(partno))
                    {
                        cmd = trans.addCustomCommand("GETPARTSERIAL1","VEHICLE_SERIAL_API.Get_Vehicle_By_Id");
                        cmd.addParameter("PART_NO");
                        cmd.addParameter("SERIAL_NO");
                        cmd.addParameter("ITEM0_MCH_CODE");

                        trans = mgr.validate(trans);

                        partno = trans.getValue("GETPARTSERIAL1/DATA/PART_NO");
                        serialno = trans.getValue("GETPARTSERIAL1/DATA/SERIAL_NO");
                        trans.clear();
                    }
                }


                cmd = trans.addCustomFunction("MACHNAME","Part_Catalog_API.Get_Description","DESCRIPTION");
                cmd.addParameter("PART_NO",partno);

                trans = mgr.validate(trans);

                machname = trans.getValue("MACHNAME/DATA/DESCRIPTION");
                mchContract = mgr.readValue("ITEM0_MCHCONTRACT");
                trans.clear();
                if (secBuff.getSecurityInfo().itemExists("Vehicle_Serial_Part_API"))
                {
                    cmd = trans.addCustomFunction("VEHICLEIDAS", "Vehicle_Serial_Part_API.Get_Vehicle_Id_As_Obj_Id","ITEM0_MCH_CODE");
                    cmd.addParameter("PART_NO", partno);
                    trans = mgr.validate(trans);

                    String vehAsMchCode = trans.getValue("VEHICLEIDAS/DATA/MCH_CODE");
                    trans.clear();

                    if ("TRUE".equals(vehAsMchCode) && secBuff.getSecurityInfo().itemExists("Vehicle_Serial_API"))
                    {
                        cmd = trans.addCustomFunction("VEHICLEID", "Vehicle_Serial_API.Get_Vehicle_Id", "ITEM0_MCH_CODE");
                        cmd.addParameter("PART_NO", partno);
                        cmd.addParameter("SERIAL_NO", serialno);

                        trans = mgr.validate(trans);
                        mchCode = trans.getValue("VEHICLEID/DATA/MCH_CODE");
                        trans.clear();
                    }
                }
            }

            String txtObj = (mgr.isEmpty(machname) ? "" : (machname))+"^"+
                            (mgr.isEmpty(mchCode) ? "": (mchCode))+"^"+
                            (mgr.isEmpty(mchContract) ? "": (mchContract))+"^"+
                            (mgr.isEmpty(partno) ? "": (partno))+"^"+
                            (mgr.isEmpty(serialno) ? "": (serialno))+"^";

            mgr.responseWrite(txtObj);
        }
        else if ("REPORTED_BY".equals(val))
        {
            cmd = trans.addCustomFunction("GETEMPNAME", "PERSON_INFO_API.Get_Name", "NAME" );
            cmd.addParameter("REPORTED_BY",mgr.readValue("REPORTED_BY"));

            cmd = trans.addCustomFunction("GETMAXEMPID", "Company_Emp_API.Get_Max_Employee_Id", "REPORTED_BY_ID" );
            cmd.addParameter("COMPANY",mgr.readValue("COMPANY"));
            cmd.addParameter("REPORTED_BY",mgr.readValue("REPORTED_BY"));

            trans = mgr.perform(trans);

            String sEmpName = trans.getValue("GETEMPNAME/DATA/NAME");
            String sMaxEmpId = trans.getValue("GETMAXEMPID/DATA/REPORTED_BY_ID");

            String txtRepBy = (mgr.isEmpty(sEmpName)?"":(sEmpName)) + "^" + 
                              (mgr.isEmpty(sMaxEmpId)?"":(sMaxEmpId)) + "^";

            mgr.responseWrite(txtRepBy);
        }
        else if ("PLAN_HRS".equals(val))
        {
            String startDate = mgr.readValue("PLAN_S_DATE");
            String completionDate = null;
            String planHrs = mgr.readValue("PLAN_HRS");
            String finishDate=mgr.readValue("PLAN_F_DATE");

            ASPBuffer dateFormatBuff = mgr.newASPBuffer();
            dateFormatBuff.addFieldItem("PLAN_S_DATE",startDate);
              
            if ((!mgr.isEmpty(planHrs)) && (mgr.isEmpty(finishDate)))
            {
                String fmtdStartDate = dateFormatBuff.getValue("PLAN_S_DATE");

                ASPQuery q = trans.addQuery("CALCCOMPLETETIME","select to_date( ? ,'yyyy-mm-dd-hh24.MI.ss') + ?/24 OUTPUT from DUAL");
		q.addParameter("DUMMY", fmtdStartDate);
		q.addParameter("PLAN_HRS", planHrs);
		q.includeMeta("ALL");

                trans = mgr.validate(trans);

                completionDate = trans.getBuffer("CALCCOMPLETETIME/DATA").getFieldValue("OUTPUT");
            }else
                completionDate=finishDate;

            mgr.responseWrite((mgr.isEmpty(completionDate)?"":completionDate) + "^" );
        }
        else if ("PLAN_S_DATE".equals(val))
        {
            String startDate = mgr.readValue("PLAN_S_DATE");
            //String completionDate = null;
            //String planHrs = mgr.readValue("PLAN_HRS");
            //String finishDate=mgr.readValue("PLAN_F_DATE");

            ASPBuffer dateFormatBuff = mgr.newASPBuffer();
            dateFormatBuff.addFieldItem("PLAN_S_DATE",startDate);

            /*if ((!mgr.isEmpty(startDate)) && (mgr.isEmpty(finishDate)))
            {
                String fmtdStartDate = dateFormatBuff.getValue("PLAN_S_DATE");

                ASPQuery q = trans.addQuery("CALCCOMPLETETIME","select to_date('"+fmtdStartDate+"','yyyy-mm-dd-hh24.MI.ss') + "+planHrs+"/24 OUTPUT from DUAL");
                q.includeMeta("ALL");

                trans = mgr.validate(trans);

                completionDate = trans.getBuffer("CALCCOMPLETETIME/DATA").getFieldValue("OUTPUT");
            }else 
                  completionDate = finishDate;

            //if (mgr.isEmpty(completionDate))
              //  completionDate = startDate; */

            String txt = (mgr.isEmpty(startDate)?"":startDate)+"^" ;
                         //(mgr.isEmpty(completionDate)?"":completionDate)+"^";

            mgr.responseWrite(txt);
        }
        else if ("PLAN_F_DATE".equals(val))
        {
            ASPBuffer buf = mgr.newASPBuffer();
            buf.addFieldItem("PLAN_F_DATE",mgr.readValue("PLAN_F_DATE"));

            mgr.responseWrite(buf.getFieldValue("PLAN_F_DATE") +"^" );
        }
        else if ("VALID_FROM".equals(val))
        {
            ASPBuffer buf = mgr.newASPBuffer();
            buf.addFieldItem("VALID_FROM",mgr.readValue("VALID_FROM"));

            mgr.responseWrite(buf.getFieldValue("VALID_FROM") +"^" );
        }
        else if ("VALID_TO".equals(val))
        {
            ASPBuffer buf = mgr.newASPBuffer();
            buf.addFieldItem("VALID_TO",mgr.readValue("VALID_TO"));

            mgr.responseWrite(buf.getFieldValue("VALID_TO") +"^" );
        }
        else if ("QUOTATION_DATE".equals(val))
        {
            ASPBuffer buf = mgr.newASPBuffer();
            buf.addFieldItem("QUOTATION_DATE",mgr.readValue("QUOTATION_DATE"));

            mgr.responseWrite(buf.getFieldValue("QUOTATION_DATE") +"^" );
        }
        else if ("CONNECTION_TYPE".equals(val))
        {
            cmd = trans.addCustomFunction("CONNTYPEDB","MAINT_CONNECTION_TYPE_API.Encode","CONNECTION_TYPE_DB");
            cmd.addParameter("CONNECTION_TYPE");

            trans = mgr.validate(trans);

            String conn_db = trans.getValue("CONNTYPEDB/DATA/CONNECTION_TYPE_DB");

            String txt = (mgr.isEmpty(conn_db) ? "" : (conn_db))+ "^";

            mgr.responseWrite(txt);
        }
        else if ("ORG_CODE".equals(val))
        {
            cmd = trans.addCustomFunction("GETORGDESC","Organization_API.Get_Description","ORGCODEDESCRIPTION");
            cmd.addParameter("ITEM0_CONTRACT");
            cmd.addParameter("ORG_CODE");

            trans = mgr.validate(trans);

            String sOrgDesc = trans.getValue("GETORGDESC/DATA/ORGCODEDESCRIPTION");

            String txt = (mgr.isEmpty(sOrgDesc) ? "" : (sOrgDesc))+ "^";

            mgr.responseWrite(txt);
        }

        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void okFind()
    {
        ASPManager mgr = getASPManager();

        ASPQuery q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CONTRACT) FROM dual) AND OBJSTATE <> 'Rejected'");
        q.setOrderByClause("QUOTATION_ID");
        q.includeMeta("ALL");

        if (mgr.dataTransfered())
            q.addOrCondition( mgr.getTransferedData());

        mgr.submit(trans);

        eval(headset.syncItemSets());

        if (headset.countRows() == 1)
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            headSearch =true;
            okFindITEM0();
        }

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWWORKORDERQUOTATIONNODATAFOUND: No data found."));
            headset.clear();
            headlay.setLayoutMode(headlay.UNDEFINED);
        }
         qrystr = mgr.createSearchURL(headblk);
    }


    public void okFindAfterUpd() 
    {
        ASPManager mgr = getASPManager();

        ASPQuery q = trans.addEmptyQuery(headblk);
        String quotationId = ctx.readValue("VIM_SERIAL_PART_AGREEMENT_SET"); 
        ctx.writeValue("VIM_SERIAL_PART_AGREEMENT_SET","Done");
        q.addWhereCondition("CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CONTRACT) FROM dual) AND OBJSTATE <> 'Rejected' AND Quotation_id = ?");
	q.addParameter("QUOTATION_ID", quotationId);
	q.setOrderByClause("QUOTATION_ID");
        q.includeMeta("ALL");

        if (mgr.dataTransfered())
            q.addOrCondition( mgr.getTransferedData());

        mgr.submit(trans);

        eval(headset.syncItemSets());

        if (headset.countRows() == 1)
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            headSearch =true;
            okFindITEM0();
        }

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWWORKORDERQUOTATIONNODATAFOUND: No data found."));
            headset.clear();
            headlay.setLayoutMode(headlay.UNDEFINED);
        }
         qrystr = mgr.createSearchURL(headblk);
    }


    public void okFindForEdit( String quotationId) 
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        ASPQuery q = trans.addEmptyQuery(headblk);
        q.addWhereCondition("QUOTATION_ID = ?");
	q.addParameter("QUOTATION_ID", quotationId);
	q.setOrderByClause("QUOTATION_ID");
        q.includeMeta("ALL");

        q = trans.addEmptyQuery(itemblk0);
        q.addWhereCondition("QUOTATION_ID = ?");
	q.addParameter("QUOTATION_ID", quotationId);
	q.includeMeta("ALL");

        mgr.submit(trans);
        trans.clear();
    }


    public void okFindEditCust()
    {
        ASPManager mgr = getASPManager();

        String sQuotationId = mgr.readValue("QUOTATION_ID");
        sCustomerId = mgr.readValue("CUSTOMER_ID");

        okFindForEdit(sQuotationId);

        if (headset.countRows() == 1)
        {
            ASPBuffer row = headset.getRow();

            row.setValue("CUSTOMER_ID",sCustomerId);

            String sAttr = null;

            for (int i=0;i<row.countItems();i++)
            {
                if ((mgr.getASPField(row.getNameAt(i)).isReadOnly())
                    ||(mgr.getASPField(row.getNameAt(i)).isComputable())
                    ||(mgr.getASPField(row.getNameAt(i)).isHidden()))
                    continue;

                if (mgr.isEmpty(sAttr))
                {
                    sAttr = row.getNameAt(i)+(char)31+(mgr.isEmpty(row.getValue(row.getNameAt(i)))?"":row.getValue(row.getNameAt(i)))+(char)30;
                }
                else
                    sAttr += row.getNameAt(i)+(char)31+(mgr.isEmpty(row.getValue(row.getNameAt(i)))?"":row.getValue(row.getNameAt(i)))+(char)30;                     
            }

            trans.clear();   

            cmd = trans.addCustomCommand("ACTSAVERET","WORK_ORDER_QUOTATION_API.Modify__");
            cmd.addParameter("INFO","");
            cmd.addParameter("OBJID",headset.getRow().getValue("OBJID"));
            cmd.addParameter("OBJVERSION",headset.getRow().getValue("OBJVERSION"));
            cmd.addParameter("ATTR",sAttr);
            cmd.addParameter("ACTION","DO");

            trans = mgr.perform(trans); 
        }

        okFindForEdit(sQuotationId);

        if (headset.countRows() == 1)
        {
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
            headSearch =true;
            okFindITEM0();
        }
    }


    public void okFindEditParentObj()
    {
        ASPManager mgr = getASPManager();

        String sQuotationId = mgr.readValue("QUOTATION_ID");
        sObjectId = mgr.readValue("MCH_CODE");

        okFindForEdit(sQuotationId);

        if (headset.countRows() == 1)
        {
            headlay.setLayoutMode(headlay.EDIT_LAYOUT);
            triger1 = "true";
        }
    }


    public void okFindEditAgr()
    {
        ASPManager mgr = getASPManager();

        String sQuotationId = mgr.readValue("QUOTATION_ID");
        sAgreementId = mgr.readValue("AGREEMENT_ID");
        trans.clear();

        okFindForEdit(sQuotationId);

        if (headset.countRows() == 1)
        {
            headlay.setLayoutMode(headlay.EDIT_LAYOUT);
            triger2 = "true";
        }

    }


    public void countFind()
    {
        ASPManager mgr = getASPManager();

        ASPQuery q = trans.addQuery(headblk);
        q.addWhereCondition("CONTRACT IN (SELECT User_Allowed_Site_API.Authorized(CONTRACT) FROM dual) AND OBJSTATE <> 'Rejected'");
        q.setSelectList("to_char(count(*)) N");

        mgr.submit(trans);
        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();  
    }


    public void newRow()
    {
        ASPManager mgr = getASPManager();

        cmd = trans.addEmptyCommand("HEAD","WORK_ORDER_QUOTATION_API.New__",headblk);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);
        ASPBuffer data = trans.getBuffer("HEAD/DATA");

        trans.clear();

	cmd=trans.addCustomFunction("FNDUSER_CON","User_default_API.Get_Contract","HEAD_CONTRACT");
	cmd=trans.addCustomFunction("SITE_COMPANY","Site_Api.Get_Company","SITECOMPANY");
	cmd.addReference("HEAD_CONTRACT","FNDUSER_CON/DATA");

	trans = mgr.perform(trans);

        String sSiteCompany = trans.getValue("SITE_COMPANY/DATA/SITECOMPANY");

	data.setFieldItem("SITECOMPANY",sSiteCompany);

        headset.addRow(data);
    }


    public void saveReturn()
    {
        ASPManager mgr = getASPManager();
                
        headset.changeRow();
        int currrow = headset.getCurrentRowNo();

        mgr.submit(trans);

        headset.goTo(currrow);
        headset.refreshRow();
        setNewObjEvent(currrow);

        if (headset.countRows() == 1)
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);

        trans.clear();

        String str = "";
        str = headset.getRow().getValue("QUOTATION_ID");
        String str1 = "";
        str1 = ctx.readValue("VIM_SERIAL_PART_AGREEMENT_SET");

        if ( "Performed".equals(str1))
        {
            ctx.writeValue("VIM_SERIAL_PART_AGREEMENT_SET",str);
            okFindAfterUpd();
        }
    }

//-----------------------------------------------------------------------------
//----------------------  CMDBAR EDIT-IT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void okFindITEM0()
    {
        ASPManager mgr = getASPManager();

        int headrowno = headset.getCurrentRowNo();
        trans.clear();
        ASPQuery q = trans.addQuery(itemblk0);
        q.addWhereCondition("QUOTATION_ID = ?");
	q.addParameter("QUOTATION_ID", headset.getValue("QUOTATION_ID"));
	q.includeMeta("ALL");

        mgr.submit(trans);

        if (( itemset0.countRows() == 0 ) &&  ! qryStrSearch)
        {
            if (("ITEM0.OkFind".equals(mgr.readValue("__COMMAND"))) && !headSearch)
            {
                mgr.showAlert(mgr.translate("PCMWWORKORDERQUOTATIONNODATAFOUND: No data found."));
                itemset0.clear();
            }
        }
        headset.goTo(headrowno);
    }


    public void countFindITEM0()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        ASPQuery q = trans.addQuery(itemblk0);
        q.addWhereCondition("QUOTATION_ID = ?");
	q.addParameter("QUOTATION_ID", headset.getValue("QUOTATION_ID"));
	q.setSelectList("to_char(count(*)) N");

        mgr.submit(trans);

        itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
        itemset0.clear();
    }


    public void newRowITEM0()
    {
        ASPManager mgr = getASPManager();
        boolean setMchCode = false;
        int item0Rows;


        trans.clear();
        cmd = trans.addEmptyCommand("ITEM0","ACTIVE_SEPARATE_API.New__",itemblk0);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);

        ASPBuffer data = trans.getBuffer("ITEM0/DATA");
        data.setFieldItem("QUOTATION_ID",headset.getValue("QUOTATION_ID"));
        data.setFieldItem("ITEM0_CONTRACT",headset.getValue("CONTRACT"));
        data.setFieldItem("ERR_DESCR",headset.getValue("QUOTATION_DESCRIPTION"));

        trans.clear();

        cmd = trans.addCustomFunction("FNDSESSION", "Fnd_Session_API.Get_Fnd_User", "FNDUSER" );

        cmd = trans.addCustomFunction("PERSONINFO", "Person_Info_API.Get_Id_For_User", "USERID" );
        cmd.addReference("FNDUSER","FNDSESSION/DATA");

        cmd = trans.addCustomFunction("MAXEMPINFO", "Company_Emp_API.Get_Max_Employee_Id", "MAXEMPLOYEEID" );
        cmd.addParameter("COMPANY",data.getValue("COMPANY"));
        cmd.addReference("USERID","PERSONINFO/DATA");

        cmd = trans.addCustomFunction("MAXEMPINFONAME", "PERSON_INFO_API.Get_Name", "MAXEMPLOYEENAME" );
        cmd.addReference("USERID","PERSONINFO/DATA");

        trans = mgr.perform(trans);

        String sFndUser = trans.getValue("FNDSESSION/DATA/FNDUSER");
        String sUserId = trans.getValue("PERSONINFO/DATA/USERID");
        String sEmployeeId = trans.getValue("MAXEMPINFO/DATA/MAXEMPLOYEEID");
        String sEmployeeName = trans.getValue("MAXEMPINFONAME/DATA/MAXEMPLOYEENAME");


        data.setFieldItem("REPORTED_BY",sUserId);
        data.setFieldItem("REPORTED_BY_ID",sEmployeeId);
        data.setFieldItem("NAME",sEmployeeName);
        data.setFieldItem("CONNECTION_TYPE_DB","EQUIPMENT");

        itemset0.addRow(data);
    }

    public void saveReturnITEM0()
    {
        ASPManager mgr = getASPManager();
        ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();
        String sPpExist = "";

        itemset0.changeRow();
        int currrowHead = headset.getCurrentRowNo();
        int currrowItem0 = itemset0.getCurrentRowNo();

        sLastWoNo = itemset0.getValue("WO_NO");

        //Bug 93061,Start added mch_code_contract as a param
        trans1.clear();
        cmd = trans1.addCustomFunction("PPEXISTS","ACTIVE_SEPARATE_API.Exist_Pre_Postings","PP_EXISTS");
        cmd.addParameter("WO_NO", sLastWoNo);
        cmd.addParameter("ITEM0_MCHCONTRACT", itemset0.getValue("MCH_CODE_CONTRACT"));
        cmd.addParameter("ITEM0_MCH_CODE", itemset0.getValue("MCH_CODE"));
        trans1 = mgr.perform(trans1);
        sPpExist = trans1.getValue("PPEXISTS/DATA/PP_EXISTS");
        //Bug 93061,End

        mgr.submit(trans);

        headset.goTo(currrowHead);
        itemset0.goTo(currrowItem0);
        itemset0.refreshRow();

        if ("REMOVED".equals(sPpExist))
            mgr.showAlert("PCMWWOQUOTITEM0PPREMOVED: Existing pre-posting values are not updated");
        else if ("CHANGED".equals(sPpExist))
            bPpChanged = true;
    }

    public void saveNewITEM0()
    {
        saveReturnITEM0();
        newRowITEM0();
    }

    
    public void updatePostings()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        cmd = trans.addCustomCommand("UPDATEPP","ACTIVE_SEPARATE_API.Update_Mch_Pre_Postings");
        cmd.addParameter("WO_NO", sLastWoNo);
        trans = mgr.perform(trans);
    }


    public void okFindPTIIA()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        int currrow = headset.getCurrentRowNo();

        ASPQuery q = trans.addQuery(ciblk);
        q.addWhereCondition("CUSTOMER_ID = ?");
	q.addParameter("CUSTOMER_ID", headset.getValue("CUSTOMER_ID"));
	q.includeMeta("ALL");

        q = trans.addQuery(ptiiablk);
        q.addMasterConnection("CI","CI_PARTY_TYPE_DB","PTIIA_PARTY_TYPE_DB");
        q.addMasterConnection("CI","CI_CUSTOMER_ID","PTIIA_IDENTITY");
        q.includeMeta("ALL");

        mgr.submit(trans);
        headset.goTo(currrow);

        if (ptiiaset.countRows()>0)
        {
            String vt = ptiiaset.getRow().getFieldValue("PTIIA_DEF_VAT_CODE");
            String pt = ptiiaset.getRow().getFieldValue("PTIIA_PAY_TERM_ID");

            ASPBuffer r = headset.getRow();
            r.setValue("VATCODE", vt);
            r.setValue("PAYMENTTERM", pt);
            headset.setRow(r);

            ciset.clear();
            ptiiaset.clear();
        }
    }


    public void searchEvent()
    {
        ASPManager mgr = getASPManager();

        trans1.clear();
        ASPQuery q = trans1.addQuery(eventblk);
        q.addWhereCondition("OBJID = ?");
	q.addParameter("OBJID", headset.getValue("OBJID"));
	q.includeMeta("ALL");

        int currrow = headset.getCurrentRowNo();
        mgr.submit(trans1);
        headset.goTo(currrow);
        trans1.clear();   
    }


    public void setNewObjEvent( int currrow)
    {

        headset.goTo(currrow);
        searchEvent();
        String newObjEvents = eventset.getValue("OBJEVENTS");

        ASPBuffer r = headset.getRow();
        r.setValue("DUP_OBJEVENTS",newObjEvents);
        headset.setRow(r);
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void print()
    {
        ASPManager mgr = getASPManager();

        int count = 0;

        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            headset.setFilterOn();
            count = headset.countSelectedRows(  );
        }
        else
        {
            headset.unselectRows();
            count = 1;
        }


        for (int i = 0;i < count;i++)
        {
            String enabled_events = "^"+headset.getValue("DUP_OBJEVENTS");

            if (enabled_events.indexOf("^Print^",0)<0)
            {
                mgr.showAlert(mgr.translate("PCMWWORKORDERQUOTATIONCANNOTPRINT: Can not perform Print on selected line."));
                if (headlay.isMultirowLayout())
                    headset.setFilterOff();
                return;
            }
            else
            {
                // Function call for Report Print

                String attr1 =  "REPORT_ID" + (char)31 + "WORK_ORDER_QUOTATION_REP" + (char)30;
                String attr2 =  "QUOTATION_ID" + (char)31 + headset.getValue("QUOTATION_ID") + (char)30; 
                String attr3 =  "";
                String attr4 =  "";

                cmd = trans.addCustomCommand( "PRINTQUOT" + i, "Archive_API.New_Client_Report");
                cmd.addParameter("ATTR0");  
                cmd.addParameter("ATTR1",attr1);  
                cmd.addParameter("ATTR2",attr2);  
                cmd.addParameter("ATTR3",attr3);  
                cmd.addParameter("ATTR4",attr4);  

                if (headlay.isMultirowLayout())
                    headset.next();
            }
        }

        trans = mgr.perform(trans);

        if (headlay.isMultirowLayout())
            headset.setFilterOff();

        String result_key;
        ASPBuffer print = mgr.newASPBuffer();;
        ASPBuffer printBuff;

        for (int i = 0;i < count;i++)
        {
            result_key =  trans.getValue("PRINTQUOT" + i + "/DATA/ATTR0");

            trans.clear();
            perform("PRINT__");

            printBuff=print.addBuffer("DATA");
            //printBuff=print.addBuffer("DATA" + i);
            printBuff.addItem("RESULT_KEY",result_key);
        }
        callPrintDlg(print,true);

    }


    public void change()
    {
        ASPManager mgr = getASPManager();

        int count = 0;  
        String enabled_events;

        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            headset.setFilterOn();
            count = headset.countSelectedRows();
        }
        else
        {
            headset.unselectRows();
            headset.selectRow();
            count = 1;
        }

        for (int i = 0;i < count;i++)
        {
            enabled_events = "^"+headset.getValue("DUP_OBJEVENTS");

            if (enabled_events.indexOf("^Change^",0)<0)
            {
                mgr.showAlert(mgr.translate("PCMWWORKORDERQUOTATIONCANNOTCHANGE: Can not perform Revise on selected lines."));
                if (headlay.isMultirowLayout())
                    headset.setFilterOff();
                return;
            }
            else
            {
                if (headlay.isMultirowLayout())
                    headset.next();
            }

        }

        trans.clear();
        perform("CHANGE__");

        if (headlay.isMultirowLayout())
            headset.setFilterOff();
    }


    public void control()
    {
        ASPManager mgr = getASPManager();

        int count = 0;  
        String enabled_events;

        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            headset.setFilterOn();
            count = headset.countSelectedRows();
        }
        else
        {
            headset.unselectRows();
            headset.selectRow();
            count = 1;
        }

        for (int i = 0;i < count;i++)
        {

            enabled_events = "^"+headset.getValue("DUP_OBJEVENTS");

            if (enabled_events.indexOf("^Control^",0)<0)
            {
                mgr.showAlert(mgr.translate("PCMWWORKORDERQUOTATIONCANNOTCONTROL: Can not perform Follow Up on selected lines."));
                if (headlay.isMultirowLayout())
                    headset.setFilterOff();
                return;
            }
            else
            {
                if (headlay.isMultirowLayout())
                    headset.next();
            }
        }

        trans.clear();
        perform("CONTROL__");

        if (headlay.isMultirowLayout())
            headset.setFilterOff();
    }


    public void activate()
    {
        ASPManager mgr = getASPManager();

        int count = 0;  
        String enabled_events;

        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            headset.setFilterOn();
            count = headset.countSelectedRows();
        }
        else
        {
            headset.unselectRows();
            headset.selectRow();
            count = 1;
        }

        for (int i = 0;i < count;i++)
        {
            enabled_events = "^"+headset.getValue("DUP_OBJEVENTS");

            if (enabled_events.indexOf("^Activate^",0)<0)
            {
                mgr.showAlert(mgr.translate("PCMWWORKORDERQUOTATIONCANNOTACTIVATE: Can not perform Activate on selected line."));
                if (headlay.isMultirowLayout())
                    headset.setFilterOff();
                return;
            }
            else
            {
                if (headlay.isMultirowLayout())
                    headset.next();
            }
        }

        trans.clear();
        perform("ACTIVATE__");

        if (headlay.isMultirowLayout())
            headset.setFilterOff();
    }


    public void freeze()
    {
        ASPManager mgr = getASPManager();

        int count = 0;  
        String enabled_events;

        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            headset.setFilterOn();
            count = headset.countSelectedRows();
        }
        else
        {
            headset.unselectRows();
            headset.selectRow();
            count = 1;
        }

        for (int i = 0;i < count;i++)
        {
            enabled_events = "^"+headset.getValue("DUP_OBJEVENTS");

            if (enabled_events.indexOf("^Freeze^",0)<0)
            {
                mgr.showAlert(mgr.translate("PCMWWORKORDERQUOTATIONCANNOTFREEZE: Can not perform Freeze on selected line."));
                if (headlay.isMultirowLayout())
                    headset.setFilterOff();
                return;
            }
            else
            {
                if (headlay.isMultirowLayout())
                    headset.next();
            }
        }

        trans.clear();
        perform("FREEZE__");

        if (headlay.isMultirowLayout())
            headset.setFilterOff();
    }


 public void newCustomer()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();      

        if (headset.countRows()>0)
        {
            //Web Alignment - simplify code for RMBs
            bOpenNewWindow = true;
            urlString = "NewCustomerDlg.page?SITECOMPANY="+mgr.URLEncode(headset.getRow().getFieldValue("SITECOMPANY"))+"&QUOTATION_ID="+mgr.URLEncode(headset.getRow().getValue("QUOTATION_ID"))+"&FRM_NAME=ServiceRequest";
            newWinHandle = "newCustomer";
            ctx.setCookie("NewWinOpen","true");
            //
        }

        
        current_url = qrystr;
        ctx.setGlobal("CALLING_URL",current_url); 
        ctx.setGlobal("HEADCURRROW",String.valueOf(headset.getCurrentRowNo()));
    }

   
   public void  newObject()
   {
      ASPManager mgr = getASPManager();
      trans.clear();

      if ( headlay.isMultirowLayout() )
         headset.goTo(headset.getRowSelected());

      //if ( !(mgr.isEmpty(headset.getValue("MCH_CODE"))) )
      String sDbConnectionType = itemset0.getValue("CONNECTION_TYPE_DB");
      String sCustomerId = headset.getValue("CUSTOMER_ID");

      if ( "EQUIPMENT".equals(sDbConnectionType) && !mgr.isEmpty(sCustomerId) )
      {
         calling_url = mgr.getURL();
         ctx.setGlobal("CALLING_URL",calling_url);

         ASPBuffer buffer = mgr.newASPBuffer();

         ASPBuffer row = buffer.addBuffer("0");
         row.addItem("CONTRACT",headset.getValue("CONTRACT"));

         row = buffer.addBuffer("1");
         row.addItem("QUOTATION_ID",headset.getValue("QUOTATION_ID"));

         row = buffer.addBuffer("2");
         row.addItem("TITLE_FLAG","2");

         // Adding Ownercodes
         trans.clear();

         String sIndex = "2";
         cmd = trans.addCustomFunction("GETCUSTOWNED","PART_OWNERSHIP_API.Get_Client_Value","OWNERSHIP");
         cmd.addParameter("CLIENT_VAL",sIndex);

         trans = mgr.perform(trans); 

         sOwnership = trans.getValue("GETCUSTOWNED/DATA/OWNERSHIP");
         sOwner = headset.getValue("CUSTOMER_ID");

         row = buffer.addBuffer("3");
         row.addItem("OWNERSHIP",sOwnership);

         row = buffer.addBuffer("4");
         row.addItem("OWNER",sOwner);

        // mgr.transferDataTo("EquipmentAllObjectDlg.page",buffer);
         mgr.transferDataTo("../equipw/EquipmentAllObjectDlg.page",buffer);


      }
      else
      {
         mgr.showAlert(mgr.translate("PCMWWORKORDERQUOTATIONCANNOTCREOBJ: Can not perform New Equipment Object on selected line."));
      }
   }

   public void agrForObject()
    {
        ASPManager mgr = getASPManager();

        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;
        transferBuffer = mgr.newASPBuffer();
        rowBuff = mgr.newASPBuffer();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());

        rowBuff.addItem("MCH_CODE", itemset0.getValue("MCH_CODE"));
        rowBuff.addItem("CONTRACT", itemset0.getValue("MCH_CODE_CONTRACT"));

        transferBuffer.addBuffer("DATA", rowBuff);

        bOpenNewWindow = true;
        urlString = createTransferUrl("../equipw/EquipmentAllObjectAgreements.page", transferBuffer);
        newWinHandle = "EquipmentAllObjectAgreements"; 

        ctx.setGlobal("CALLING_URL",mgr.getURL());
        ctx.setCookie("NewWinOpen","true");
    }

    public void agrForCustomer()
    {
        ASPManager mgr = getASPManager();

        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;
        transferBuffer = mgr.newASPBuffer();
        rowBuff = mgr.newASPBuffer();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());

        rowBuff.addItem("CUSTOMER_NO", headset.getValue("CUSTOMER_ID"));
        transferBuffer.addBuffer("DATA", rowBuff);

        bOpenNewWindow = true;
        urlString = createTransferUrl("../orderw/CustomerAgreement.page", transferBuffer);
        newWinHandle = "CustomerAgreement"; 

        ctx.setGlobal("CALLING_URL",mgr.getURL());
        ctx.setCookie("NewWinOpen","true");
    }

    public void planning()
    {
        ASPManager mgr = getASPManager();

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());

        calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url);

        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;
        transferBuffer = mgr.newASPBuffer();
        rowBuff = mgr.newASPBuffer();

        rowBuff.addItem("WO_NO", itemset0.getValue("WO_NO"));
        rowBuff.addItem("QUOTATION_ID", headset.getValue("QUOTATION_ID"));
        rowBuff.addItem("FORM_NAME", "2");
        transferBuffer.addBuffer("DATA", rowBuff);

        bOpenNewWindow = true;
        urlString = createTransferUrl("WorkOrderPlanning.page", transferBuffer);
        newWinHandle = "WorkPlanning"; 
        ctx.setCookie("NewWinOpen","true");
    }

    public void connectExtWorkOrder()
    {
        ASPManager mgr = getASPManager();

        double nWoNo  = mgr.readNumberValue("HIDDENWO");
        String sWoNo = ""+nWoNo;

        trans.clear();
        cmd = trans.addCustomCommand("CONNETTWO","Active_Separate_API.Set_Quotation_Id__");
        cmd.addParameter("WO_NO",sWoNo);
        cmd.addParameter("QUOTATION_ID", headset.getValue("QUOTATION_ID"));
        trans = mgr.perform(trans);

        int headrowno = headset.getCurrentRowNo();
        trans.clear();
        ASPQuery q = trans.addEmptyQuery(itemblk0);
        q.addWhereCondition("QUOTATION_ID = ?");
	q.addParameter("QUOTATION_ID", headset.getValue("QUOTATION_ID"));
	q.includeMeta("ALL");
        mgr.submit(trans);

        headset.goTo(headrowno);
    }

    public void removeWorkOrder()
    {
        ASPManager mgr = getASPManager();

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());

        int cur_row = itemset0.getCurrentRowNo();

        trans.clear();
        cmd = trans.addCustomFunction("GETOBJSTATE","Active_Separate_API.Get_Obj_State","INFO");
        cmd.addParameter("WO_NO", itemset0.getValue("WO_NO"));
        
        cmd = trans.addCustomCommand("RESETWO","Active_Separate_API.Re_Set_Quotation_Id__");
        cmd.addParameter("INFO");
        cmd.addParameter("WO_NO", itemset0.getValue("WO_NO"));
        cmd.addParameter("QUOTATION_ID", headset.getValue("QUOTATION_ID"));
        cmd.addParameter("ACTION","DO");

        trans = mgr.perform(trans);

        String sState = trans.getValue("GETOBJSTATE/DATA/INFO");
        if (!("WORKREQUEST".equals(sState) || "FAULTREPORT".equals(sState)))
            mgr.showAlert("CANNOTREMOVE: Work Order state is higher than Fault Report/Service Request.");

        okFindITEM0();

        if (itemlay0.isSingleLayout() && itemset0.countRows()>0)
        {
            if ((cur_row-1)>0)
                itemset0.goTo(cur_row-1);
            else
                itemset0.first();
        }
    }

    public void historicalWork()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());

        calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url);

        ASPBuffer transferBuffer;
        ASPBuffer rowBuff;
        transferBuffer = mgr.newASPBuffer();
        rowBuff = mgr.newASPBuffer();

        rowBuff.addItem("CONTRACT", headset.getValue("CONTRACT"));
        rowBuff.addItem("QUOTATION_ID", headset.getValue("QUOTATION_ID"));
        transferBuffer.addBuffer("DATA", rowBuff);

        mgr.transferDataTo("HistoricalSeparateRMB.page",transferBuffer);
    }

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


//-----------------------------------------------------------------------------
//-------------------------  CUSTOM FUNCTIONS  --------------------------------
//-----------------------------------------------------------------------------

    public void setAgreementDates()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        cmd = trans.addCustomFunction( "CUSTAGGRVALIDFR", "CUSTOMER_AGREEMENT_API.Get_Valid_From", "VALID_FROM_TEMP" );
        cmd.addParameter("AGREEMENT_ID", headset.getRow().getFieldValue("AGREEMENT_ID"));

        cmd = trans.addCustomFunction( "CUSTAGGRVALIDUN", "CUSTOMER_AGREEMENT_API.Get_Valid_Until", "VALID_UNTIL_TEMP" );
        cmd.addParameter("AGREEMENT_ID", headset.getRow().getFieldValue("AGREEMENT_ID"));

        trans = mgr.validate(trans);

        String validFrom = trans.getValue("CUSTAGGRVALIDFR/DATA/VALID_FROM_TEMP");
        String validUntil = trans.getValue("CUSTAGGRVALIDUN/DATA/VALID_UNTIL_TEMP");

        ASPBuffer r = headset.getRow();
        r.setValue("CUSTOMERAGREEMENTVALID_FROM", validFrom);
        r.setValue("CUSTOMERAGREEMENTVALID_UNTIL", validUntil);
        headset.setRow(r);

        trans.clear();
    }


    public void setBudgetTotals()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        //Get Actual Costs

        cmd = trans.addCustomCommand( "ACTUALCOST", "Work_Order_Coding_API.Get_All_Outcome");
        cmd.addParameter("ACT_PERS_COST");  
        cmd.addParameter("ACT_MAT_COST");
        cmd.addParameter("ACT_EXT_COST");  
        cmd.addParameter("ACT_EXP_COST");  
        cmd.addParameter("ACT_FIX_COST");  
        cmd.addParameter("WO_NO",itemset0.getRow().getFieldValue("WO_NO"));  

        //Get Planned Revenues

        cmd = trans.addCustomCommand( "PLANNEDREV", "Work_Order_Coding_API.Get_Planned_Revenue");
        cmd.addParameter("WO_NO",itemset0.getRow().getFieldValue("WO_NO"));  
        cmd.addParameter("PLAN_PERS_REV");  
        cmd.addParameter("PLAN_MAT_REV");  
        cmd.addParameter("PLAN_EXT_REV");  
        cmd.addParameter("FIXED_PRICE");     

        //Get Plan External Cost

        cmd = trans.addCustomCommand( "PLANEXTCOST", "Work_Order_Requis_Header_API.Get_External_Cost");
        cmd.addParameter("PLAN_EXT_COST");  
        cmd.addParameter("WO_NO",itemset0.getRow().getFieldValue("WO_NO"));  

        //Get Plan Material Cost
        String woNo = itemset0.getRow().getFieldValue("WO_NO");
        String sqlStmt = "SELECT nvl(sum( nvl(qty_required,0) *  nvl(Inventory_Part_Cost_API.Get_Total_Standard(spare_contract, part_no, 1) , 0)),0) FROM WORK_ORDER_PART WHERE  wo_no = ?";

        ASPQuery q = trans.addQuery("PLANMATCOST",sqlStmt);
	q.addParameter("WO_NO", woNo);

        trans = mgr.perform(trans);

        double nActPersCost = trans.getNumberValue("ACTUALCOST/DATA/ACT_PERS_COST");
        if (isNaN(nActPersCost))
            nActPersCost = 0;

        double nActMatCost = trans.getNumberValue("ACTUALCOST/DATA/ACT_MAT_COST");
        if (isNaN(nActMatCost))
            nActMatCost = 0;

        double nActExtCost = trans.getNumberValue("ACTUALCOST/DATA/ACT_EXT_COST");
        if (isNaN(nActExtCost))
            nActExtCost = 0;



        double nPlanPersRev = trans.getNumberValue("PLANNEDREV/DATA/PLAN_PERS_REV");
        if (isNaN(nPlanPersRev))
            nPlanPersRev = 0;

        double nPlanMatRev = trans.getNumberValue("PLANNEDREV/DATA/PLAN_MAT_REV");
        if (isNaN(nPlanMatRev))
            nPlanMatRev = 0;

        double nPlanExtRev = trans.getNumberValue("PLANNEDREV/DATA/PLAN_EXT_REV");
        if (isNaN(nPlanExtRev))
            nPlanExtRev = 0;



        double nPlanPersCost = itemset0.getRow().getNumberValue("PLAN_PERS_COST");
        if (isNaN(nPlanPersCost))
            nPlanPersCost = 0;

        double nPlanMatCost = 0.0;

        double nPlanExtCost = trans.getNumberValue("PLANEXTCOST/DATA/PLAN_EXT_COST");
        if (isNaN(nPlanExtCost))
            nPlanExtCost = 0;


        double nTotPlanCost = nPlanPersCost + nPlanMatCost + nPlanExtCost;
        double nTotActCost = nActPersCost + nActMatCost + nActExtCost;
        double nPlanTotRev = nPlanPersRev + nPlanMatRev + nPlanExtRev;

        ASPBuffer r = itemset0.getRow();
        r.setNumberValue("TOT_ACT_COST", nTotActCost);
        r.setNumberValue("TOT_PLAN_COST", nTotPlanCost);
        r.setNumberValue("TOT_PLANNED_REV", nPlanTotRev);
        itemset0.setRow(r);

    }


    public void preDefine()
    {
        ASPManager mgr = getASPManager();



        // ==== HEAD BLOCK =============================================================================

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("OBJSTATE");
        f.setHidden();

        f = headblk.addField("OBJEVENTS");
        f.setHidden();

        f = headblk.addField("DUP_OBJEVENTS");
        f.setHidden();
        f.setFunction("OBJEVENTS");
        f.setReadOnly();

        f = headblk.addField("QUOTATION_ID","Number","#");
        f.setSize(11);
        f.setDynamicLOV("WORK_ORDER_QUOTATION_LOV",600,445);
        f.setLabel("PCMWWORKORDERQUOTATIONQUOTATIONID: Quotation ID");
        f.setReadOnly();
        f.setHilite();

        f = headblk.addField("QUOTATION_REV");
        f.setSize(8);
        f.setMaxLength(6);
        f.setMandatory();
        f.setLabel("PCMWWORKORDERQUOTATIONQUOTATIONREV: Revision");
        f.setUpperCase();
        f.setDefaultNotVisible();
        f.setHilite();

        f = headblk.addField("QUOTATION_DESCRIPTION");
        f.setSize(31);
        f.setMaxLength(50);
        f.setMandatory();
        f.setLabel("PCMWWORKORDERQUOTATIONQUOTATIONDESCRIPTION: Description");
        f.setHilite();

        f = headblk.addField("HEAD_CONTRACT");
        f.setSize(5);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PCMWWORKORDERQUOTATIONHEADCONTRACT: Site");
        f.setDbName("CONTRACT");
        f.setUpperCase();
        f.setCustomValidation("HEAD_CONTRACT","SITECOMPANY");
        f.setHilite();

        f = headblk.addField("STATE");
        f.setSize(10);
        f.setLabel("PCMWWORKORDERQUOTATIONSTATE: State");
        f.setReadOnly();
        f.setHilite();

        f = headblk.addField("SITECOMPANY");
        f.setSize(11);
        f.setHidden();
        f.setFunction("SITE_API.Get_Company(:HEAD_CONTRACT)");


        // ==== GENERAL TAB =============================================================================

        f = headblk.addField("VALID_FROM","Date");
        f.setSize(14);
        f.setMandatory();
        f.setLabel("PCMWWORKORDERQUOTATIONVALIDFROM: Valid From");
        f.setDefaultNotVisible();
        f.setCustomValidation("VALID_FROM","VALID_FROM");

        f = headblk.addField("VALID_TO","Date");
        f.setSize(14);
        f.setMandatory();
        f.setLabel("PCMWWORKORDERQUOTATIONVALIDTO: Valid To");
        f.setCustomValidation("VALID_TO","VALID_TO");

        f = headblk.addField("QUOTATION_DATE","Date");
        f.setSize(14);
        f.setMandatory();
        f.setLabel("PCMWWORKORDERQUOTATIONQUOTATIONDATE: Quotation Date");
        f.setDefaultNotVisible();
        f.setCustomValidation("QUOTATION_DATE","QUOTATION_DATE");

        f = headblk.addField("SALESMAN_CODE");
        f.setSize(9);
        f.setDynamicLOV("SALES_PART_SALESMAN_LOV",600,445);
        f.setLabel("PCMWWORKORDERQUOTATIONSALESMANCODE: Salesman");
        f.setUpperCase();
        f.setCustomValidation("SALESMAN_CODE","SALESPARTSALESMANNAME");

        f = headblk.addField("SALESPARTSALESMANNAME");
        f.setSize(22);
        f.setLabel("PCMWWORKORDERQUOTATIONSALESPARTSALESMANNAME: Salesman Name");
        f.setFunction("SALES_PART_SALESMAN_API.Get_Name(:SALESMAN_CODE)");
        f.setReadOnly();
        f.setDefaultNotVisible();

	f = headblk.addField("CURRENCY_CODE");
        f.setSize(6);
        f.setLabel("PCMWWORKORDERQUOTATIONCURRENCYCODE: Currency Code");
        f.setDynamicLOV("CURRENCY_CODE","SITECOMPANY COMPANY",600,445);
        f.setDefaultNotVisible();

        f = headblk.addField("QUOTATION_TEXT");
        f.setSize(54);
        f.setLabel("PCMWWORKORDERQUOTATIONQUOTATIONTEXT: Quotation Text");
        f.setDefaultNotVisible();
        f.setHeight(5);

        f = headblk.addField("QUOTATION_NOTE");
        f.setSize(54);
        f.setLabel("PCMWWORKORDERQUOTATIONQUOTATIONNOTE: Internal Note");
        f.setDefaultNotVisible(            );
        f.setHeight(5);

        // ====== CUSTOMER TAB =============================================================================

        f = headblk.addField("CUSTOMER_ID");
        f.setSize(20);
        f.setMaxLength(20);
        f.setDynamicLOV("CUSTOMER_INFO",600,445);
        f.setLabel("PCMWWORKORDERQUOTATIONCUSTOMERID: Customer No");
        f.setUpperCase();
        f.setCustomValidation("CUSTOMER_ID,SITECOMPANY,DEL_ADD_ID,VIS_ADD_ID","CUSTOMERGROUP,CREDITSTOP,CUSTOMERINFONAME,REFERENCE,VISITADDRESS1,VISITADDRESS2,VISITADDRESS3,VISITADDRESS4,VISITADDRESS5,VISITADDRESS6,DELIVERYADDRESS1,DELIVERYADDRESS2,DELIVERYADDRESS3,DELIVERYADDRESS4,DELIVERYADDRESS5,DELIVERYADDRESS6,DISCOUNTCLASS,CURRENCY,VATCODE,PAYMENTTERM,DEL_ADD_ID,VIS_ADD_ID");

        f= headblk.addField("CREDITSTOP");
        f.setFunction("''");
        f.setHidden();  

        f = headblk.addField("CUSTOMER_ID_DLG");
        f.setSize(20);
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("CUSTOMERGROUP");
        f.setSize(11);
        f.setLabel("PCMWWORKORDERQUOTATIONCUSTOMERGROUP: Customer Group");
        f.setFunction("CUST_ORD_CUSTOMER_API.Get_Cust_Grp(:CUSTOMER_ID)");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("CUSTOMERINFONAME");
        f.setSize(47);
        f.setLabel("PCMWWORKORDERQUOTATIONCUSTOMERINFONAME: Name");
        f.setFunction("CUSTOMER_INFO_API.Get_Name(:CUSTOMER_ID)");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("REFERENCE");
        f.setSize(16);
        f.setLabel("PCMWWORKORDERQUOTATIONREFERENCE: Reference");
        f.setFunction("CUST_ORD_CUSTOMER_API.Get_Cust_Ref(:CUSTOMER_ID)");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("COUNTRYCODE");
        f.setFunction("''");
        f.setReadOnly();
        f.setHidden();

        f = headblk.addField("DEL_ADD_ID");
        f.setSize(20);
        f.setMaxLength(50);
        f.setDynamicLOV("CUSTOMER_INFO_ADDRESS","CUSTOMER_ID",600,445);
        f.setLabel("PCMWWORKORDERQUATATIONDELVERY: Delivary Address");
        f.setUpperCase();
        
        f = headblk.addField("VIS_ADD_ID");
        f.setSize(20);
        f.setMaxLength(50);
        f.setDynamicLOV("CUSTOMER_INFO_ADDRESS","CUSTOMER_ID",600,445);
        f.setLabel("PCMWWORKORDERQUATATIONVISIT: Visit Address");
        f.setUpperCase();

        fADDRESS1 = headblk.addField("VISITADDRESS1").
                    setSize(22).
                    setMaxLength(100).
                    setLabel("PCMWWORKORDERQUOTATIONVISITADDRESS1: Address 1").
                    setFunction("WORK_ORDER_QUOTATION_API.Get_Visit_Address_Line1(:CUSTOMER_ID,:VIS_ADD_ID)").
                    setReadOnly().
                    setDefaultNotVisible();

        fADDRESS2 = headblk.addField("VISITADDRESS2").
                    setSize(22).
                    setLabel("PCMWWORKORDERQUOTATIONVISITADDRESS2: Address 2"). 
                    setFunction("WORK_ORDER_QUOTATION_API.Get_Visit_Address_Line2(:CUSTOMER_ID,:VIS_ADD_ID)").
                    setReadOnly().
                    setDefaultNotVisible();

        fZIP_CODE = headblk.addField("VISITADDRESS3").
                    setSize(22).
                    setLabel("PCMWWORKORDERQUOTATIONVISITADDRESS3: Address 3"). 
                    setFunction("WORK_ORDER_QUOTATION_API.Get_Visit_Address_Line3(:CUSTOMER_ID,:VIS_ADD_ID)").
                    setReadOnly().
                    setDefaultNotVisible();

        fCITY = headblk.addField("VISITADDRESS4").
                setSize(22).
                setLabel("PCMWWORKORDERQUOTATIONVISITADDRESS4: Address 4"). 
                setFunction("WORK_ORDER_QUOTATION_API.Get_Visit_Address_Line4(:CUSTOMER_ID,:VIS_ADD_ID)").
                setReadOnly().
                setDefaultNotVisible();

        fSTATE = headblk.addField("VISITADDRESS5").
                 setSize(22).
                 setLabel("PCMWWORKORDERQUOTATIONVISITADDRESS5: Address 5").
                 setFunction("WORK_ORDER_QUOTATION_API.Get_Visit_Address_Line5(:CUSTOMER_ID,:VIS_ADD_ID)"). 
                 setReadOnly().
                 setDefaultNotVisible();

        fCOUNTY = headblk.addField("VISITADDRESS6").
                  setSize(22).
                  setLabel("PCMWWORKORDERQUOTATIONVISITADDRESS6: Address 6"). 
                  setFunction("WORK_ORDER_QUOTATION_API.Get_Visit_Address_Line6(:CUSTOMER_ID,:VIS_ADD_ID)"). 
                  setReadOnly().
                  setDefaultNotVisible();

        fCOUNTRY_CODE = headblk.addField("TEMP_ADDRESS6").
                        setSize(22).
                        setLabel("PCMWWORKORDERQUOTATIONTEMPADDRESS6: Address 7"). 
                        setFunction("WORK_ORDER_QUOTATION_API.Get_Visit_Address_Line7(:CUSTOMER_ID,:VIS_ADD_ID)"). 
                        setReadOnly().
                        setDefaultNotVisible();

        fCOUNTRY = headblk.addField("TEMP1_ADDRESS6").
                   setSize(22).
                   setLabel("PCMWWORKORDERQUOTATIONTEMP1ADDRESS6: Address 8"). 
                   setFunction("WORK_ORDER_QUOTATION_API.Get_Visit_Address_Line8(:CUSTOMER_ID,:VIS_ADD_ID)"). 
                   setReadOnly().
                   setDefaultNotVisible();


        fDELIVERYADDRESS1 = headblk.addField("DELIVERYADDRESS1").
                            setSize(22).
                            setLabel("PCMWWORKORDERQUOTATIONDELIVERYADDRESS1: Address 1").
                            setFunction("WORK_ORDER_QUOTATION_API.Get_Delivery_Address_Line1(:CUSTOMER_ID,:DEL_ADD_ID)").
                            setReadOnly().
                            setDefaultNotVisible();

        fDELIVERYADDRESS2 = headblk.addField("DELIVERYADDRESS2").
                            setSize(22).
                            setLabel("PCMWWORKORDERQUOTATIONDELIVERYADDRESS2: Address 2"). 
                            setFunction("WORK_ORDER_QUOTATION_API.Get_Delivery_Address_Line2(:CUSTOMER_ID,:DEL_ADD_ID)").
                            setReadOnly().
                            setDefaultNotVisible();

        fDELIVERYADDRESS3 = headblk.addField("DELIVERYADDRESS3").
                            setSize(22).
                            setLabel("PCMWWORKORDERQUOTATIONDELIVERYADDRESS3: Address 3").
                            setFunction("WORK_ORDER_QUOTATION_API.Get_Delivery_Address_Line3(:CUSTOMER_ID,:DEL_ADD_ID)").
                            setReadOnly().
                            setDefaultNotVisible();

        fDELIVERYADDRESS4 = headblk.addField("DELIVERYADDRESS4").
                            setSize(22).
                            setLabel("PCMWWORKORDERQUOTATIONDELIVERYADDRESS4: Address 4"). 
                            setFunction("WORK_ORDER_QUOTATION_API.Get_Delivery_Address_Line4(:CUSTOMER_ID,:DEL_ADD_ID)").
                            setReadOnly().
                            setDefaultNotVisible();

        fDELIVERYADDRESS5 = headblk.addField("DELIVERYADDRESS5").
                            setSize(22).
                            setLabel("PCMWWORKORDERQUOTATIONDELIVERYADDRESS5: Address 5").
                            setFunction("WORK_ORDER_QUOTATION_API.Get_Delivery_Address_Line5(:CUSTOMER_ID,:DEL_ADD_ID)").
                            setReadOnly().
                            setDefaultNotVisible();

        fDELIVERYADDRESS6 = headblk.addField("DELIVERYADDRESS6").
                            setSize(22).
                            setLabel("PCMWWORKORDERQUOTATIONDELIVERYADDRESS6: Address 6"). 
                            setFunction("WORK_ORDER_QUOTATION_API.Get_Delivery_Address_Line6(:CUSTOMER_ID,:DEL_ADD_ID)").
                            setReadOnly().
                            setDefaultNotVisible();
        fDELIVERYADDRESS7 = headblk.addField("DELIVERYADDRESS7").
                            setSize(22).
                            setLabel("PCMWWORKORDERQUOTATIONDELIVERYADDRESS7: Address 7").
                            setFunction("WORK_ORDER_QUOTATION_API.Get_Delivery_Address_Line7(:CUSTOMER_ID,:DEL_ADD_ID)").
                            setReadOnly().
                            setDefaultNotVisible();

        fDELIVERYADDRESS8 = headblk.addField("DELIVERYADDRESS8").
                            setSize(22).
                            setLabel("PCMWWORKORDERQUOTATIONDELIVERYADDRESS8: Address 8"). 
                            setFunction("WORK_ORDER_QUOTATION_API.Get_Delivery_Address_Line8(:CUSTOMER_ID,:DEL_ADD_ID)").
                            setReadOnly().
                            setDefaultNotVisible();


        f = headblk.addField("PAYMENTTERM");
        f.setSize(6);
        f.setLabel(" ");
        f.setLabel("PCMWWORKORDERQUOTATIONPAYMENTTERM: Payment Term");
        f.setFunction("''");
        f.setUpperCase();
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("VATCODE");
        f.setSize(6);
        f.setLabel("PCMWWORKORDERQUOTATIONVATCODE: Vat Code");
        f.setFunction("''");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("DISCOUNTCLASS","Number");
        f.setSize(6);
        f.setLabel("PCMWWORKORDERQUOTATIONDISCOUNTCLASS: Discount Type");
        f.setFunction("CUST_ORD_CUSTOMER_API.Get_Discount_Class(:CUSTOMER_ID)");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = headblk.addField("CURRENCY");
        f.setSize(6);
        f.setLabel("PCMWWORKORDERQUOTATIONCURRENCY: Currency");
        f.setFunction("CUST_ORD_CUSTOMER_API.Get_Currency_Code(:CUSTOMER_ID)");
	f.setReadOnly();
        f.setDefaultNotVisible();


        // ==== other fields ==============================================

        f = headblk.addField("IS_DLG");
        f.setFunction("'false'");
        f.setHidden();

        f = headblk.addField("ATTR0");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("ATTR1");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("ATTR2");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("ATTR3");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("ATTR4");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("INFO");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("ATTR");
        f.setFunction("''");
        f.setHidden();

        f = headblk.addField("ACTION");
        f.setFunction("''");
        f.setHidden();
       
        f = headblk.addField("CHECK_HIST_WO_EXIST");
        f.setFunction("HISTORICAL_SEPARATE_API.Has_His_Wos(:QUOTATION_ID)");
        f.setHidden();

        headblk.setView("WORK_ORDER_QUOTATION");
        headblk.defineCommand("WORK_ORDER_QUOTATION_API","New__,Modify__,Remove__,CHANGE__,CONTROL__,ACTIVATE__,FREEZE__,PRINT__");
        headset = headblk.getASPRowSet();

        headbar = mgr.newASPCommandBar(headblk);

        headbar.disableCommand(headbar.DUPLICATE);

        headbar.addCustomCommand("print",mgr.translate("PCMWWORKORDERQUOTATIONPRI: Print Quotation and change status..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("historicalWork",mgr.translate("PCMWWORKORDERQUOTATIONHISTWORK: Historical Work Order..."));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("change",mgr.translate("PCMWWORKORDERQUOTATIONREVISE: Revise"));
        headbar.addCustomCommand("control",mgr.translate("PCMWWORKORDERQUOTATIONFOLLOWUP: Follow Up"));
        headbar.addCustomCommand("activate",mgr.translate("PCMWWORKORDERQUOTATIONACTI: Activate"));
        headbar.addCustomCommand("freeze",mgr.translate("PCMWWORKORDERQUOTATIONFREEZ: Freeze"));
        headbar.addCustomCommandSeparator();
        headbar.addCustomCommand("newCustomer",mgr.translate("PCMWWORKORDERQUOTATIONNEWCUSTOM: New Customer..."));
        headbar.addCustomCommandSeparator();
        headbar.addCommandValidConditions("historicalWork", "CHECK_HIST_WO_EXIST","Disable","FALSE");
        if (mgr.isPresentationObjectInstalled("orderw/CustomerAgreement.page"))
            headbar.addCustomCommand("agrForCustomer",mgr.translate("PCMWWORKORDERQUOTATIONAGRFORCUST: Agreements for Customer..."));
        
        headbar.defineCommand(headbar.SAVERETURN,"saveReturn","checkHeadFields(-1)");   
        headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");   
        headbar.enableMultirowAction();
        headbar.removeFromMultirowAction("newCustomer");
        if (mgr.isPresentationObjectInstalled("orderw/CustomerAgreement.page"))
            headbar.removeFromMultirowAction("agrForCustomer");
        headbar.removeFromMultirowAction("historicalWork");

        if (mgr.isPresentationObjectInstalled("orderw/CustomerAgreement.page"))
            headbar.addCommandValidConditions("agrForCustomer","CUSTOMER_ID","Disable","null");

        headtbl = mgr.newASPTable(headblk);
        headtbl.    enableRowSelect();
        headtbl.setTitle(mgr.translate("PCMWWORKORDERQUOTATIONPREPWOQUOT: Prepare Work Order Quotation"));
        headtbl.setWrap();
        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        headlay.setAutoLayoutSelect();

        headlay.defineGroup("","QUOTATION_ID,QUOTATION_REV,QUOTATION_DESCRIPTION,HEAD_CONTRACT,STATE",false,true);
        headlay.defineGroup(mgr.translate("PCMWWORKORDERQUOTATIONGRPLABEL1: General"),"VALID_FROM,VALID_TO,QUOTATION_DATE,SALESMAN_CODE,SALESPARTSALESMANNAME,QUOTATION_TEXT,QUOTATION_NOTE,CURRENCY_CODE",true,true);
        headlay.defineGroup(mgr.translate("PCMWWORKORDERQUOTATIONGRPLABEL2: Customer"),"CUSTOMER_ID,CUSTOMERGROUP,CUSTOMERINFONAME,REFERENCE,DEL_ADD_ID,VIS_ADD_ID,DELIVERYADDRESS1,DELIVERYADDRESS2,DELIVERYADDRESS3,DELIVERYADDRESS4,DELIVERYADDRESS5,DELIVERYADDRESS6,DELIVERYADDRESS7,DELIVERYADDRESS8,VISITADDRESS1,VISITADDRESS2,VISITADDRESS3,VISITADDRESS4,VISITADDRESS5,VISITADDRESS6,TEMP_ADDRESS6,TEMP1_ADDRESS6,PAYMENTTERM,VATCODE,DISCOUNTCLASS,CURRENCY",true,false);
        headlay.setAddressFieldList(fADDRESS1,fADDRESS2,fZIP_CODE,fCITY,fSTATE,fCOUNTY,fCOUNTRY_CODE,fCOUNTRY,"","ifs.enterw.LocalizedEnterwAddress");  
        headlay.setAddressFieldList(fDELIVERYADDRESS1,fDELIVERYADDRESS2,fDELIVERYADDRESS3,fDELIVERYADDRESS4,fDELIVERYADDRESS5,fDELIVERYADDRESS6,fDELIVERYADDRESS7,fDELIVERYADDRESS8,"","ifs.pcmw.LocalizedPcmwAddress"); 
        headlay.setSimple("SALESPARTSALESMANNAME");

        headlay.setDataSpan("QUOTATION_TEXT",3);     
        headlay.setDataSpan("QUOTATION_NOTE",3);     

        // ==== PREPARE TAB =============================================================================

        itemblk0 = mgr.newASPBlock("ITEM0");

        f = itemblk0.addField("ITEM0_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk0.addField("ITEM0_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk0.addField("ITEM0_OBJSTATE");
        f.setHidden();
        f.setDbName("OBJSTATE");

        f = itemblk0.addField("ITEM0_OBJEVENTS");
        f.setHidden();
        f.setDbName("OBJEVENTS");

        f = itemblk0.addField("ITEM0_QUOTATION_ID","Number","#");
        f.setSize(14);
        f.setHidden();
        f.setDbName("QUOTATION_ID");

        f = itemblk0.addField("WO_NO");
        f.setSize(17);
        f.setLOV("ActiveSeparateLov.page",600,445);
        f.setLabel("PCMWWORKORDERQUOTATIONWONO: WO No");
        f.setReadOnly();
        f.setHyperlink("ActiveSeparate2ServiceManagement.page","WO_NO","NEWWIN");

        f = itemblk0.addField("ITEM0_CONTRACT");
        f.setSize(6);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PCMWWORKORDERQUOTATIONITEM0CONTRACT: WO Site");
        f.setUpperCase();
        f.setInsertable();
        f.setReadOnly();
        f.setDbName("CONTRACT");

        f = itemblk0.addField("CONNECTION_TYPE");
        f.setSize(20);
        f.setSelectBox();
        f.setReadOnly();
        f.setInsertable();
        f.setCustomValidation("CONNECTION_TYPE","CONNECTION_TYPE_DB");
        f.enumerateValues("MAINT_CONNECTION_TYPE_API");
        f.setLabel("PCMWWORKORDERQUOTATIONCONNECTIONTYPE: Connection Type");

        f = itemblk0.addField("CONNECTION_TYPE_DB");
        f.setHidden();

        f = itemblk0.addField("ITEM0_MCHCONTRACT");
        f.setSize(6);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PCMWWORKORDERQUOTATIONITEM0MCHCONTRACT: Site");
        f.setUpperCase();
        f.setDbName("MCH_CODE_CONTRACT");


        f = itemblk0.addField("ITEM0_MCH_CODE");                        
        f.setSize(19);
        f.setMaxLength(100);
        f.setDynamicLOV("MAINTENANCE_OBJECT_ADDR_LOV1","ITEM0_MCHCONTRACT",600,445);
        f.setMandatory();
        f.setLabel("PCMWWORKORDERQUOTATIONITEM0MCHCODE: Object ID");
        f.setUpperCase();
        f.setDbName("MCH_CODE");
        f.setInsertable();
        f.setCustomValidation("ITEM0_MCH_CODE,ITEM0_MCHCONTRACT,CONNECTION_TYPE_DB","DESCRIPTION,ITEM0_MCH_CODE,ITEM0_MCHCONTRACT,PART_NO,SERIAL_NO");
        //f.setHyperlink("WorkOrderQuotationRedirect.page","ITEM0_MCH_CODE,ITEM0_MCHCONTRACT","NEWWIN");

        f = itemblk0.addField("DESCRIPTION");
        f.setSize(28);
        f.setDefaultNotVisible();
        f.setLabel("PCMWWORKORDERQUOTATIONDESCRIPTION: Description");
        f.setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:ITEM0_MCHCONTRACT,:ITEM0_MCH_CODE)");
        f.setReadOnly();

        f = itemblk0.addField("ITEM0_STATE");
        f.setSize(12);
        f.setLabel("PCMWWORKORDERQUOTATIONITEM0STATE: Status");
        f.setDbName("STATE");
        f.setReadOnly();

        f = itemblk0.addField("REG_DATE","Datetime");
        f.setSize(17);
        f.setMandatory();
        f.setLabel("PCMWWORKORDERQUOTATIONREGDATE: Date");
        f.setReadOnly();
        f.setInsertable();
        f.setDefaultNotVisible();

        f = itemblk0.addField("REPORTED_BY");
        f.setSize(12);
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445);
        f.setMandatory();
        f.setLabel("PCMWWORKORDERQUOTATIONREPORTEDBY: Reported by");
        f.setUpperCase();
        f.setReadOnly();
        f.setInsertable();
        f.setCustomValidation("REPORTED_BY,COMPANY","NAME,REPORTED_BY_ID");
        f.setDefaultNotVisible();

        f = itemblk0.addField("NAME");
        f.setSize(29);
        f.setFunction("PERSON_INFO_API.Get_Name(:REPORTED_BY)");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk0.addField("TEST_POINT_ID");
        f.setSize(17);
        f.setDynamicLOV("EQUIPMENT_OBJECT_TEST_PNT","ITEM0_MCHCONTRACT CONTRACT, ITEM0_MCH_CODE MCH_CODE",600,445);
        f.setLabel("PCMWWORKORDERQUOTATIONTESTPOINTID: Testpoint");
        f.setUpperCase();
        f.setDefaultNotVisible();

        f = itemblk0.addField("ERR_DESCR");
        f.setSize(41);
        f.setMandatory();
        f.setLabel("PCMWWORKORDERQUOTATIONERRDESCR: Directive");

        f = itemblk0.addField("OUTPUT","Datetime");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("WORK_DESCR_LO");
        f.setLabel("PCMWWORKORDERQUOTATIONWORKDESCRLO: Work Descr");
        f.setSize(12);
        f.setDefaultNotVisible();
        f.setMaxLength(2000);

        f = itemblk0.addField("CONTRACT_ID");
        f.setHidden();

        f = itemblk0.addField("AUTHORIZE_CODE");
        f.setSize(11);
        f.setDynamicLOV("ORDER_COORDINATOR_LOV",600,445);
        //Bug 84436, Start
        if (mgr.isModuleInstalled("SRVCON"))
            f.setFunction("Sc_Service_Contract_API.Get_Authorize_Code(:CONTRACT_ID)");
         else
            f.setFunction("''");
        //Bug 84436, End 
        f.setLabel("PCMWWORKORDERQUOTATIONAUTHORIZECODE: Coordinator");
        f.setUpperCase();
        f.setDefaultNotVisible();

        f = itemblk0.addField("REFERENCE_NO");
        f.setSize(12);
        f.setLabel("PCMWWORKORDERQUOTATIONREFERENCENO: Reference No");
        f.setDefaultNotVisible();

        f = itemblk0.addField("ORG_CODE");
        f.setSize(11);
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ITEM0_CONTRACT CONTRACT",600,445);
        f.setMandatory();
        f.setLabel("PCMWWORKORDERQUOTATIONORGCODE: Maintenance Organization");
        f.setCustomValidation("ITEM0_CONTRACT,ORG_CODE","ORGCODEDESCRIPTION");
        f.setUpperCase();

        f = itemblk0.addField("ORGCODEDESCRIPTION");
        f.setSize(27);
        f.setLabel(" ");
        f.setFunction("Organization_Api.Get_Description(:ITEM0_CONTRACT,:ORG_CODE)");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk0.addField("WORK_TYPE_ID");
        f.setSize(11);
        f.setDefaultNotVisible();
        f.setDynamicLOV("WORK_TYPE",600,445);
        f.setLabel("PCMWWORKORDERQUOTATIONWORKTYPEID: Work Type");
        f.setUpperCase();
        f.setMandatory();

        f = itemblk0.addField("WORKTYPEDESCRIPTION");
        f.setSize(27);
        f.setLabel(" ");
        f.setFunction("WORK_TYPE_API.Get_Description(:WORK_TYPE_ID)");
        mgr.getASPField("WORK_TYPE_ID").setValidation("WORKTYPEDESCRIPTION");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk0.addField("PRIORITY_ID");
        f.setSize(11);
        f.setDynamicLOV("MAINTENANCE_PRIORITY",600,445);
        f.setLabel("PCMWWORKORDERQUOTATIONPRIORITYID: Priority");
        f.setUpperCase();
        f.setDefaultNotVisible();

        f = itemblk0.addField("PRIORITYDESCRIPTION");
        f.setSize(27);
        f.setLabel(" ");
        f.setFunction("Maintenance_Priority_API.Get_Description(:PRIORITY_ID)");
        mgr.getASPField("PRIORITY_ID").setValidation("PRIORITYDESCRIPTION");
        f.setReadOnly();
        f.setDefaultNotVisible();

        f = itemblk0.addField("PLAN_S_DATE","Datetime");
        f.setSize(22);
        f.setLabel("PCMWWORKORDERQUOTATIONPLANSDATE: Start");
        f.setCustomValidation("PLAN_S_DATE,PLAN_HRS,PLAN_F_DATE","PLAN_S_DATE");

        f = itemblk0.addField("PLAN_F_DATE","Datetime");
        f.setSize(22);
        f.setLabel("PCMWWORKORDERQUOTATIONPLANFDATE: Completion");
        f.setCustomValidation("PLAN_F_DATE","PLAN_F_DATE");

        f = itemblk0.addField("PLAN_HRS","Number");
        f.setSize(22);
        f.setDefaultNotVisible();
        f.setLabel("PCMWWORKORDERQUOTATIONPLANHRS: Execution Time");
        f.setCustomValidation("PLAN_HRS,PLAN_S_DATE,PLAN_F_DATE","PLAN_F_DATE");

        f = itemblk0.addField("REPORTED_BY_ID");
        f.setSize(7);
        f.setHidden();
        f.setUpperCase();

        f = itemblk0.addField("COMPANY");
        f.setSize(7);
        f.setHidden();
        f.setUpperCase();

        f = itemblk0.addField("CUSTOMER_NO");
        f.setSize(14);
        f.setDynamicLOV("CUSTOMER_INFO",600,445);
        f.setHidden();
        f.setUpperCase();

        f = itemblk0.addField("ITEM0_AGREEMENT_ID");
        f.setDbName("AGREEMENT_ID");
        f.setSize(16);
        f.setHidden();
        f.setUpperCase();

        f = itemblk0.addField("PLAN_PERS_COST");
        f.setFunction("WORK_ORDER_ROLE_API.Calculate_Personal_Budget(:WO_NO)");
        f.setHidden();

        f = itemblk0.addField("PLAN_MAT_COST");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("PLAN_EXT_COST");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("TOT_BUDG_COST","Money"); 
        f.setSize(16);
        f.setLabel("PCMWWORKORDERQUOTATIONTOTBUDGCOST: Total Budget Cost");
        f.setFunction("nvl(BUD_PERS_COST,0)+nvl(BUD_MAT_COST,0)+nvl(BUD_EXT_COST,0)");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setHidden();

        f = itemblk0.addField("TOT_PLAN_COST","Money"); 
        f.setSize(16);
        f.setLabel("PCMWWORKORDERQUOTATIONTOTPLANCOST: Total Planned Cost");
        f.setFunction("''");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setHidden();

        f = itemblk0.addField("TOT_ACT_COST","Money"); 
        f.setSize(16);
        f.setLabel("PCMWWORKORDERQUOTATIONTOTACTCOST: Total Actual Cost");
        f.setFunction("''");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setHidden();

        f = itemblk0.addField("TOT_PLANNED_REV","Money"); 
        f.setSize(16);
        f.setLabel("PCMWWORKORDERQUOTATIONTOTPLANNEDREV: Total Planned Revenue");
        f.setFunction("''");
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setHidden();

        f = itemblk0.addField("SETTINGTIME","Number");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("FNDUSER");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("USERID");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("MAXEMPLOYEEID");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("MAXEMPLOYEENAME");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("FORM_NAME");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("PART_NO");
        f.setHidden();

        f = itemblk0.addField("SERIAL_NO");
        f.setHidden();

	//Bug 87766, Start, Modified the field labels
        f = itemblk0.addField("ISCONN");
        f.setLabel("PCMWWORKORDERQUOTATIONISCONN: In a Structure");
        f.setFunction("substr(WORK_ORDER_CONNECTION_API.HAS_CONNECTION_UP(WO_NO),1,5)");
        f.setReadOnly();
        f.setCheckBox("FALSE,TRUE");
        f.setDefaultNotVisible();

        f = itemblk0.addField("HASCONN");
        f.setLabel("PCMWWORKORDERQUOTATIONHASCONN: Has Structure");
        f.setFunction("substr(WORK_ORDER_CONNECTION_API.HAS_CONNECTION_DOWN(WO_NO),1,5)");
        f.setReadOnly();
        f.setCheckBox("FALSE,TRUE");
        f.setSize(13);
        f.setDefaultNotVisible();
	//Bug 87766, End

        f = itemblk0.addField("OWNERSHIP");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("OWNER");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("CLIENT_VAL");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("DUMMY", "String");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("PP_EXISTS"); 
        f.setHidden();  
        f.setFunction("''");

        itemblk0.setView("ACTIVE_SEPARATE");
        itemblk0.defineCommand("ACTIVE_SEPARATE_API","New__,Modify__,Remove__,CONTROL__,ACTIVATE__,FREEZE__");
        itemblk0.setMasterBlock(headblk);
        itemblk0.setTitle(mgr.translate("PCMWWORKORDERQUOTATIONPREPARELABLE: Prepare"));

        itemset0 = itemblk0.getASPRowSet();

        itembar0 = mgr.newASPCommandBar(itemblk0);
        itembar0.defineCommand(itembar0.OKFIND,   "okFindITEM0");
        itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
        itembar0.defineCommand(itembar0.NEWROW,    "newRowITEM0");
        itembar0.enableCommand(itembar0.FIND);
        itembar0.defineCommand(itembar0.SAVERETURN,"saveReturnITEM0","checkItem0Mandatory(-1)");   
        itembar0.defineCommand(itembar0.SAVENEW,null,"checkItem0Mandatory(-1)"); 
        itembar0.enableMultirowAction();  

        itembar0.addCustomCommand("connectExtWorkOrder",mgr.translate("PCMWWORKORDERQUOTATIONCOWO: Connect Existing Work Order..."));
        itembar0.addCustomCommand("removeWorkOrder",mgr.translate("PCMWWORKORDERQUOTATIONREMWO: Remove WO from Quotation"));
        itembar0.addCustomCommandSeparator();
        itembar0.addCustomCommand("planning",mgr.translate("PCMWWORKORDERQUOTATIONPLANNING: Planning..."));
        itembar0.addCustomCommand("newObject",mgr.translate("PCMWWORKORDERQUOTATIONNEWCHILDOBJ: New Equipment Object..."));
        
        if (mgr.isPresentationObjectInstalled("equipw/EquipmentAllObjectAgreements.page"))
            itembar0.addCustomCommand("agrForObject",mgr.translate("PCMWWORKORDERQUOTATIONCONTFOROBJ: Service Contracts for Equipment Object..."));


        if (mgr.isPresentationObjectInstalled("equipw/EquipmentAllObjectAgreements.page"))
            itembar0.addCommandValidConditions("agrForObject","MCH_CODE","Disable","null");
        
        itembar0.removeFromMultirowAction("newObject");
        itembar0.removeFromMultirowAction("planning");
        
        if (mgr.isPresentationObjectInstalled("equipw/EquipmentAllObjectAgreements.page"))
            itembar0.removeFromMultirowAction("agrForObject");

        itembar0.removeFromMultirowAction("removeWorkOrder");  

        itembar0.defineCommand("connectExtWorkOrder","connectExtWorkOrder","showLovWo(-1)");   
        itembar0.forceEnableMultiActionCommand("connectExtWorkOrder");     

        itemtbl0 = mgr.newASPTable(itemblk0);
        itemtbl0.setTitle(mgr.translate("PCMWWORKORDERQUOTATIONITM0: Prepare"));
        itemtbl0.setWrap();
        itemtbl0.enableRowSelect();

        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDialogColumns(2);
        itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);

        itemlay0.defineGroup(mgr.translate("PCMWWORKORDERQUOTATIONGRPLABEL5: General"),"WO_NO,ITEM0_CONTRACT,ERR_DESCR,ITEM0_STATE,REG_DATE,REPORTED_BY,NAME,CONNECTION_TYPE,ITEM0_MCH_CODE,ITEM0_MCHCONTRACT,DESCRIPTION,WORK_DESCR_LO,AUTHORIZE_CODE,REFERENCE_NO,TEST_POINT_ID",false,true);
        itemlay0.defineGroup(mgr.translate("PCMWWORKORDERQUOTATIONGRPLABEL6: Planning Information"),"ORG_CODE,ORGCODEDESCRIPTION,WORK_TYPE_ID,WORKTYPEDESCRIPTION,PRIORITY_ID,PRIORITYDESCRIPTION",true,true);
        itemlay0.defineGroup(mgr.translate("PCMWWORKORDERQUOTATIONGRPLABEL7: Planning Schedule"),"PLAN_S_DATE,PLAN_F_DATE,PLAN_HRS",true,true);
        itemlay0.defineGroup(mgr.translate("PCMWWORKORDERQUOTATIONGRPLABEL8: Work Order"),"ISCONN,HASCONN",true,true);

        itemlay0.setSimple("NAME");
        itemlay0.setSimple("ORGCODEDESCRIPTION");
        itemlay0.setSimple("WORKTYPEDESCRIPTION");
        itemlay0.setSimple("PRIORITYDESCRIPTION");


        // ---- SUB BLOCK - Actual Cost[PREPERE TAB] ---------

        actcostblk = mgr.newASPBlock("ACTCOST");

        f = actcostblk.addField("ACT_PERS_COST","Money");   
        f.setFunction("''");

        f = actcostblk.addField("ACT_MAT_COST","Money");   
        f.setFunction("''");

        f = actcostblk.addField("ACT_EXT_COST","Money");   
        f.setFunction("''");

        f = actcostblk.addField("ACT_EXP_COST","Money");   
        f.setFunction("''");

        f = actcostblk.addField("ACT_FIX_COST","Money");   
        f.setFunction("''");

        // ---- SUB BLOCK - Planned Revenue[PREPERE TAB] ---------

        revblk = mgr.newASPBlock("REV");

        f = actcostblk.addField("PLAN_PERS_REV","Money");   
        f.setFunction("''");

        f = actcostblk.addField("PLAN_MAT_REV","Money");   
        f.setFunction("''");

        f = actcostblk.addField("PLAN_EXT_REV","Money");   
        f.setFunction("''");

        f = actcostblk.addField("FIXED_PRICE","Money");   
        f.setFunction("''");


        // ==== CI BLOCK =============================================================================

        ciblk = mgr.newASPBlock("CI");

        f = ciblk.addField("CI_CUSTOMER_ID");
        f.setDbName("CUSTOMER_ID");
        f.setHidden();

        f = ciblk.addField("CI_PARTY_TYPE_DB");
        f.setDbName("PARTY_TYPE_DB");
        f.setHidden();

        f = ciblk.addField("CI_PARTY");
        f.setDbName("PARTY");
        f.setHidden();

        ciblk.setView("CUSTOMER_INFO");

        ciset = ciblk.getASPRowSet();

        cibar = mgr.newASPCommandBar(ciblk);
        cibar.defineCommand(cibar.OKFIND,"okFindCI");


        // ==== PTIIA BLOCK =============================================================================

        ptiiablk = mgr.newASPBlock("PTIIA");

        f = ptiiablk.addField("PTIIA_PARTY_TYPE_DB");
        f.setDbName("PARTY_TYPE_DB");
        f.setHidden();

        f = ptiiablk.addField("PTIIA_IDENTITY");
        f.setDbName("IDENTITY");
        f.setHidden();

        f = ptiiablk.addField("PTIIA_DEF_VAT_CODE");
        f.setDbName("DEF_VAT_CODE");
        f.setHidden();

        f = ptiiablk.addField("PTIIA_PAY_TERM_ID");
        f.setDbName("PAY_TERM_ID");
        f.setHidden();

        ptiiablk.setView("IDENTITY_INVOICE_INFO");

        ptiiaset = ptiiablk.getASPRowSet();

        ptiialay = ptiiablk.getASPBlockLayout();
        ptiialay.setDialogColumns(3);
        ptiialay.setDefaultLayoutMode(ptiialay.MULTIROW_LAYOUT);

        // ==== NEW OBJECT BLOCK =====================================================================

        newobjblk = mgr.newASPBlock("NEWOBJ");

        f = newobjblk.addField("NEWOBJ_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = newobjblk.addField("NEWOBJ_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = newobjblk.addField("NEWOBJ_OBJSTATE");
        f.setHidden();
        f.setDbName("OBJSTATE");

        f = newobjblk.addField("NEWOBJ_OBJEVENTS");
        f.setHidden();
        f.setDbName("OBJEVENTS");

        f = newobjblk.addField("NEWOBJ_MCH_CODE");
        f.setSize(14);
        f.setMaxLength(100);
        f.setLabel("PCMWWORKORDERQUOTATIONMCH_CODE: Object ID");
        f.setUpperCase();
        f.setDbName("MCH_CODE");
        f.setInsertable();

        f = newobjblk.addField("NEWOBJ_CONTRACT");
        f.setSize(7);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setMandatory();
        f.setLabel("PCMWWORKORDERQUOTATIONCONTRACT: Site");
        f.setUpperCase();
        f.setDbName("CONTRACT");
        f.setInsertable();

        f = newobjblk.addField("NEWOBJ_MCH_NAME");
        f.setSize(39);
        f.setLabel("PCMWWORKORDERQUOTATIONMCH_NAME: Description");
        f.setDbName("MCH_NAME");
        f.setInsertable();

        f = newobjblk.addField("NEWOBJ_OBJ_LEVEL");
        f.setSize(14);
        f.setDynamicLOV("EQUIPMENT_OBJECT_LEVEL",600,445);
        f.setLabel("PCMWWORKORDERQUOTATIONOBJ_LEVEL: Object Level");
        f.setDbName("OBJ_LEVEL");
        f.setInsertable();

        f = newobjblk.addField("NEWOBJ_PART_NO");
        f.setSize(11);
        f.setDynamicLOV("PART_CATALOG",600,445);
        f.setLabel("PCMWWORKORDERQUOTATIONPART_NO: Part No");
        f.setUpperCase();
        f.setDbName("PART_NO");
        f.setInsertable();

        f = newobjblk.addField("NEWOBJ_SERIAL_NO");
        f.setSize(11);
        f.setMaxLength(50);
        f.setLabel("PCMWWORKORDERQUOTATIONSERIAL_NO: Serial No");
        f.setUpperCase();
        f.setDbName("SERIAL_NO");
        f.setInsertable();

        newobjblk.setView("EQUIPMENT_ALL_OBJECT");
        newobjblk.defineCommand("EQUIPMENT_ALL_OBJECT_API","New__,Modify__,Remove__");
        newobjset = newobjblk.getASPRowSet();

        head_command = headbar.getSelectedCustomCommand();

        // ==== STATE BLOCK ================================================================================   

        eventblk = mgr.newASPBlock("EVENTBLOCK");

        f = eventblk.addField("DUMMY_OBJID");
        f.setDbName("OBJID");
        f.setHidden();

        f = eventblk.addField("DUMMY_QUOTATION_ID");
        f.setDbName("QUOTATION_ID");
        f.setHidden();

        f = eventblk.addField("DUMMY_OBJEVENTS");
        f.setDbName("OBJEVENTS");
        f.setHidden();

        eventblk.setView("WORK_ORDER_QUOTATION");
        eventblk.setMasterBlock(headblk);
        eventset = eventblk.getASPRowSet();


        // ==== CHECK BOX FIELDS BLOCK =====================================================================   

        b = mgr.newASPBlock("CUSTOMERAGREEMENTAGR_TYPE");

        b.addField("CLIENT_VALUES0");

        b = mgr.newASPBlock("HEAD_STATE");

        b.addField("CLIENT_VALUES2");

        enableConvertGettoPost();

    }


    public void checkObjAvailable()
    {
        ASPManager mgr = getASPManager();

        if (!isSecurityChecked)
        {
            trans.clear();

            trans.addSecurityQuery("WORK_ORDER_QUOTATION_REP,"+
                                   "HISTORICAL_SEPARATE,"+
                                   "COMPANY,"+
                                   "EQUIPMENT_ALL_OBJECT,"+
                                   "CUSTOMER_AGREEMENT,"+
                                   "ACTIVE_SEPARATE");

            trans.addSecurityQuery("Work_Order_Quotation_API","Create_Customer");

            trans.addPresentationObjectQuery("PCMW/HistoricalSeparateRMB.page,"+
                                             "PCMW/NewCustomerDlg.page,"+
                                             "EQUIPW/EquipmentAllObjectDlg.page,"+
                                             "EQUIPW/NavigatorFrameSet.page,"+
                                             "PCMW/CustomerAgreementDlg.page,"+
                                             "ORDERW/CustomerAgreement.page,"+
                                             "EQUIPW/EquipmentAllObjectAgreements.page,"+
                                             "PCMW/WorkOrderPlanning.page");

            trans = mgr.perform(trans);

            ASPBuffer secViewBuff = trans.getSecurityInfo();

            actionsBuffer = mgr.newASPBuffer();

            if (secViewBuff.itemExists("WORK_ORDER_QUOTATION_REP"))
                actionsBuffer.addItem("okHeadPrint","");

            if (secViewBuff.itemExists("HISTORICAL_SEPARATE") && secViewBuff.namedItemExists("PCMW/HistoricalSeparateRMB.page"))
                actionsBuffer.addItem("okHeadHistoricalWork","");

            if (secViewBuff.itemExists("COMPANY") && secViewBuff.itemExists("Work_Order_Quotation_API.Create_Customer") && secViewBuff.namedItemExists("PCMW/NewCustomerDlg.page"))
                actionsBuffer.addItem("okHeadNewCustomer","");

            if (secViewBuff.itemExists("EQUIPMENT_ALL_OBJECT"))
            {
                if (secViewBuff.namedItemExists("EQUIPW/EquipmentAllObjectDlg.page"))
                {
                    actionsBuffer.addItem("okHeadNewObject","");
                }
                if (secViewBuff.namedItemExists("EQUIPW/EquipmentAllObjectAgreements.page"))
                {
                    actionsBuffer.addItem("okItemAgrForObject","");
                }
            }

            if (secViewBuff.itemExists("CUSTOMER_AGREEMENT"))
            {
                if (secViewBuff.namedItemExists("PCMW/CustomerAgreementDlg.page"))
                    actionsBuffer.addItem("okHeadNewAgreement","");
                if (secViewBuff.namedItemExists("ORDERW/CustomerAgreement.page"))
                    actionsBuffer.addItem("okHeadAgrForCustomer","");
            }

            if (secViewBuff.itemExists("ACTIVE_SEPARATE") && secViewBuff.namedItemExists("PCMW/WorkOrderPlanning.page"))
                actionsBuffer.addItem("okItem0Planning","");


            isSecurityChecked = true;
        }
    }


    public void adjustActions()
    {
        ASPManager mgr = getASPManager();

        // Removing actions which are not allowed to the user.
        // headbar
        if (!actionsBuffer.itemExists("okHeadPrint"))
            headbar.removeCustomCommand("print");

        if (!actionsBuffer.itemExists("okHeadHistoricalWork"))
            headbar.removeCustomCommand("historicalWork");

        if (!actionsBuffer.itemExists("okHeadNewCustomer"))
            headbar.removeCustomCommand("newCustomer");
        
        if (!actionsBuffer.itemExists("okHeadAgrForCustomer") && mgr.isPresentationObjectInstalled("orderw/CustomerAgreement.page"))
            headbar.removeCustomCommand("agrForCustomer");

        if (!actionsBuffer.itemExists("okItemAgrForObject") && mgr.isPresentationObjectInstalled("equipw/EquipmentAllObjectAgreements.page"))
            itembar0.removeCustomCommand("agrForObject");
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        if (mgr.isPresentationObjectInstalled("equipw/EquipmentAllObjectLightRedirect.page"))
            mgr.getASPField("ITEM0_MCH_CODE").setHyperlink("../equipw/EquipmentAllObjectLightRedirect.page","ITEM0_MCH_CODE,ITEM0_CONTRACT,WO_NO","NEWWIN");

        if (mgr.isPresentationObjectInstalled("enterw/CustomerInfo.page"))
            mgr.getASPField("CUSTOMER_ID").setHyperlink("../enterw/CustomerInfo.page","CUSTOMER_ID","NEWWIN");
        mgr.createSearchURL(headblk);

        if (headset.countRows() > 0)
        {
            if (headlay.isSingleLayout() || headset.countRows() == 1)
            {
                String enabled_events = "^"+headset.getValue("DUP_OBJEVENTS");

                n = 0;

                if (enabled_events.indexOf("^Print^",0)<0)
                {
                    headbar.removeCustomCommand("print");
                }

                if (enabled_events.indexOf("^Change^",0)<0)
                {
                    headbar.removeCustomCommand("change");
                    n +=1;
                }

                if (enabled_events.indexOf("^Control^",0)<0)
                {
                    headbar.removeCustomCommand("control");
                    n +=1;
                }

                if (enabled_events.indexOf("^Activate^",0)<0)
                {
                    headbar.removeCustomCommand("activate");
                    n +=1;
                }

                if (enabled_events.indexOf("^Freeze^",0)<0)
                {
                    headbar.removeCustomCommand("freeze");
                    n +=1;
                }

                if (mgr.isPresentationObjectInstalled("orderw/CustomerAgreement.page")  &&   "".equals(headset.getValue("CUSTOMER_ID")))
                    headbar.removeCustomCommand("agrForCustomer");
            }

            if (headlay.isSingleLayout() && ("Confirmed".equals(headset.getValue("OBJSTATE"))))
            {
                itembar0.disableCommand(itembar0.NEWROW);
                itembar0.removeCustomCommand("connectExtWorkOrder");
            }
            if (headlay.isSingleLayout())
            {
                sCustId = headset.getValue("CUSTOMER_ID");
                sContract = headset.getValue("CONTRACT");
            }
        }

        if (itemset0.countRows()>0)
        {
            if (itemlay0.isSingleLayout() || itemlay0.isMultirowLayout() || itemset0.countRows() == 1)
            {
                if ("".equals(headset.getValue("CUSTOMER_ID")) || !("EQUIPMENT".equals(itemset0.getValue("CONNECTION_TYPE_DB"))))
                    itembar0.removeCustomCommand("newObject");
            }
        }

	//Disable the field "Site" when the connection type is VIM
	if ( itemset0.countRows()>0 ) {
	   if ( ( itemlay0.isEditLayout() ) && ( "VIM".equals(itemset0.getValue("CONNECTION_TYPE_DB")) ) ) {
              mgr.getASPField("ITEM0_MCHCONTRACT").setReadOnly();  
	   }
	}

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
        return "PCMWWORKORDERQUOTATIONPREPWOQUOT: Prepare Work Order Quotation";
    }

    protected String getTitle()
    {
        return "PCMWWORKORDERQUOTATIONPREPWOQUOT: Prepare Work Order Quotation";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        printHiddenField("ISHIDDENWO","");
        printHiddenField("HIDDENWO","");
        printHiddenField("NEW_AGREEMENT_NO","");
        printHiddenField("ORG_CODE_R","");
        printHiddenField("WORK_TYPE_ID_R","");
        printHiddenField("PRPOSTCHANGED", "");
        
        appendToHTML(headlay.show());
        if (headset.countRows()>0)
        {
            if (itemlay0.isVisible())
            {
                appendToHTML(itemlay0.show());
            }
        }
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript(   "//==========================================================\n");
        appendDirtyJavaScript("//==========================================================\n");
        appendDirtyJavaScript("// this portion is used when returned from RMB New Customer\n");
        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(triger);
        appendDirtyJavaScript("' == 'true')\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if (IS_EXPLORER)\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		cntHEAD2.style.display='block';\n");
        //appendDirtyJavaScript("		cntHEAD2End.style.display='block';\n");
        appendDirtyJavaScript("		setMinimizeImage(document['groupHEAD2'],'cntHEAD2',false);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	getField_('CUSTOMER_ID',-1).value = getField_('CUSTOMER_ID',-1).defaultValue = '");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(sCustomerId));	//XSS_Safe AMNILK 20070526
        appendDirtyJavaScript("';	\n");
        appendDirtyJavaScript("	validateCustomerId(-1);	\n");
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(triger1);
        appendDirtyJavaScript("' == 'true')\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if (IS_EXPLORER)\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		cntHEAD4.style.display='block';\n");
        //appendDirtyJavaScript("		cntHEAD4End.style.display='block';\n");
        appendDirtyJavaScript("		setMinimizeImage(document['groupHEAD4'],'cntHEAD4',false);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	getField_('HEAD_MCH_CODE',-1).value = getField_('HEAD_MCH_CODE',-1).defaultValue = '");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(sObjectId));	//XSS_Safe AMNILK 20070526
        appendDirtyJavaScript("';	\n");
        appendDirtyJavaScript("	validateHeadMchCode(-1);\n");
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(triger2);
        appendDirtyJavaScript("' == 'true')\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if (IS_EXPLORER)\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		cntHEAD3.style.display='block';\n");
        //appendDirtyJavaScript("		cntHEAD3End.style.display='block';\n");
        appendDirtyJavaScript("		setMinimizeImage(document['groupHEAD3'],'cntHEAD3',false);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	getField_('AGREEMENT_ID',-1).value = getField_('AGREEMENT_ID',-1).defaultValue = '");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(sAgreementId));	//XSS_Safe AMNILK 20070526
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("	validateAgreementId(-1);\n");
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("if ('");
        appendDirtyJavaScript(triger3);
        appendDirtyJavaScript("' == 'true')\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	validateMchCode(-1);\n");
        appendDirtyJavaScript("}\n");   
        appendDirtyJavaScript("//==========================================================\n");
        appendDirtyJavaScript("//==========================================================\n");
        appendDirtyJavaScript("function validateCustomerId(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('HEAD',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkCustomerId(i) ) return;\n");
        appendDirtyJavaScript("	if( getValue_('CUSTOMER_ID',i)=='' )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		getField_('CUSTOMERGROUP',i).value = '';\n");
        appendDirtyJavaScript("		getField_('CREDITSTOP',i).value = '';\n");
        appendDirtyJavaScript("		getField_('CUSTOMERINFONAME',i).value = '';\n");
        appendDirtyJavaScript("		getField_('REFERENCE',i).value = '';\n");
        appendDirtyJavaScript("		getField_('VISITADDRESS1',i).value = '';\n");
        appendDirtyJavaScript("		getField_('VISITADDRESS2',i).value = '';\n");
        appendDirtyJavaScript("		getField_('VISITADDRESS3',i).value = '';\n");
        appendDirtyJavaScript("		getField_('VISITADDRESS4',i).value = '';\n");
        appendDirtyJavaScript("		getField_('VISITADDRESS5',i).value = '';\n");
        appendDirtyJavaScript("		getField_('VISITADDRESS6',i).value = '';\n");
        appendDirtyJavaScript("		getField_('DELIVERYADDRESS1',i).value = '';\n");
        appendDirtyJavaScript("		getField_('DELIVERYADDRESS2',i).value = '';\n");
        appendDirtyJavaScript("		getField_('DELIVERYADDRESS3',i).value = '';\n");
        appendDirtyJavaScript("		getField_('DELIVERYADDRESS4',i).value = '';\n");
        appendDirtyJavaScript("		getField_('DELIVERYADDRESS5',i).value = '';\n");
        appendDirtyJavaScript("		getField_('DELIVERYADDRESS6',i).value = '';\n");
        appendDirtyJavaScript("		getField_('DISCOUNTCLASS',i).value = '';\n");
        appendDirtyJavaScript("		getField_('CURRENCY',i).value = '';\n");
        appendDirtyJavaScript("		getField_('VATCODE',i).value = '';\n");
        appendDirtyJavaScript("		getField_('PAYMENTTERM',i).value = '';\n");
        appendDirtyJavaScript("		getField_('DEL_ADD_ID',i).value = '';\n");
        appendDirtyJavaScript("		getField_('VIS_ADD_ID',i).value = '';\n");
        appendDirtyJavaScript("		return;\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=CUSTOMER_ID'\n");
        appendDirtyJavaScript("		+ '&CUSTOMER_ID=' + URLClientEncode(getValue_('CUSTOMER_ID',i))\n");
        appendDirtyJavaScript("		+ '&SITECOMPANY=' + URLClientEncode(getValue_('SITECOMPANY',i))\n");
	appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript("    retStr = r.split(\"^\");\n");
        appendDirtyJavaScript("    if(retStr[1] == '1')\n");
        appendDirtyJavaScript("    {   \n");
        appendDirtyJavaScript("        message = '");
        appendDirtyJavaScript(mgr.translateJavaScript("PCMWWORKORDERQUOTATIONMSSG: Customer is Credit blocked"));
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("        window.alert(message);\n");
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("    if( checkStatus_(r,'CUSTOMER_ID',i,'Customer No') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('CUSTOMERGROUP',i,0);\n");
        appendDirtyJavaScript("		assignValue_('CREDITSTOP',i,1);\n");
        appendDirtyJavaScript("		assignValue_('CUSTOMERINFONAME',i,2);\n");
        appendDirtyJavaScript("		assignValue_('REFERENCE',i,3);\n");
        appendDirtyJavaScript("		assignValue_('VISITADDRESS1',i,4);\n");
        appendDirtyJavaScript("		assignValue_('VISITADDRESS2',i,5);\n");
        appendDirtyJavaScript("		assignValue_('VISITADDRESS3',i,6);\n");
        appendDirtyJavaScript("		assignValue_('VISITADDRESS4',i,7);\n");
        appendDirtyJavaScript("		assignValue_('VISITADDRESS5',i,8);\n");
        appendDirtyJavaScript("		assignValue_('VISITADDRESS6',i,9);\n");
        appendDirtyJavaScript("		assignValue_('DELIVERYADDRESS1',i,10);\n");
        appendDirtyJavaScript("		assignValue_('DELIVERYADDRESS2',i,11);\n");
        appendDirtyJavaScript("		assignValue_('DELIVERYADDRESS3',i,12);\n");
        appendDirtyJavaScript("		assignValue_('DELIVERYADDRESS4',i,13);\n");
        appendDirtyJavaScript("		assignValue_('DELIVERYADDRESS5',i,14);\n");
        appendDirtyJavaScript("		assignValue_('DELIVERYADDRESS6',i,15);\n");
        appendDirtyJavaScript("		assignValue_('DISCOUNTCLASS',i,16);\n");
        appendDirtyJavaScript("		assignValue_('CURRENCY',i,17);\n");
        appendDirtyJavaScript("		assignValue_('VATCODE',i,18);\n");
        appendDirtyJavaScript("		assignValue_('PAYMENTTERM',i,19);\n");
        appendDirtyJavaScript("		assignValue_('DEL_ADD_ID',i,20);\n");
        appendDirtyJavaScript("		assignValue_('VIS_ADD_ID',i,21);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("function disableChangeStructure()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if (f.CBSTRUCTURE.checked == false)\n");
        appendDirtyJavaScript("		f.CBSTRUCTURE.checked = f.CBSTRUCTURE.defaultChecked = true;\n");
        appendDirtyJavaScript("	else\n");
        appendDirtyJavaScript("		f.CBSTRUCTURE.checked = f.CBSTRUCTURE.defaultChecked = false;\n");
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("function disableChangeWarranty()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if (f.CBWARRANTY.checked == false)\n");
        appendDirtyJavaScript("		f.CBWARRANTY.checked = f.CBWARRANTY.defaultChecked = true;\n");
        appendDirtyJavaScript("	else\n");
        appendDirtyJavaScript("		f.CBWARRANTY.checked = f.CBWARRANTY.defaultChecked = false;\n");
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("function disableChangeHasStructure()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if (f.HASSTRUCTURE.checked == false)\n");
        appendDirtyJavaScript("		f.HASSTRUCTURE.checked = f.HASSTRUCTURE.defaultChecked = true;\n");
        appendDirtyJavaScript("	else\n");
        appendDirtyJavaScript("		f.HASSTRUCTURE.checked = f.HASSTRUCTURE.defaultChecked = false;\n");
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("function disableChangeHasWarranty()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if (f.HASWARRANTY.checked == false)\n");
        appendDirtyJavaScript("		f.HASWARRANTY.checked = f.HASWARRANTY.defaultChecked = true;\n");
        appendDirtyJavaScript("	else\n");
        appendDirtyJavaScript("		f.HASWARRANTY.checked = f.HASWARRANTY.defaultChecked = false;\n");
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("function checkItem0Mandatory(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	return checkWoNo(i) &&\n");
        appendDirtyJavaScript("	checkItem0Contract(i) &&\n");
        appendDirtyJavaScript("	checkItem0MchCode(i) &&\n");
        appendDirtyJavaScript("	checkDescription(i) &&\n");
        appendDirtyJavaScript("	checkItem0State(i) &&\n");
        appendDirtyJavaScript("	checkRegDate(i) &&\n");
        appendDirtyJavaScript("	checkReportedBy(i) &&\n");
        appendDirtyJavaScript("	checkName(i) &&\n");
        appendDirtyJavaScript("	checkTestPointId(i) &&\n");
        appendDirtyJavaScript("	checkErrDescr(i) &&\n");
        appendDirtyJavaScript("	checkWorkDescrLo(i) &&\n");
        appendDirtyJavaScript("	checkAuthorizeCode(i) &&\n");
        appendDirtyJavaScript("	checkReferenceNo(i) &&\n");
        appendDirtyJavaScript("	checkOrgCode(i) &&\n");
        appendDirtyJavaScript("	checkOrgcodedescription(i) &&\n");
        appendDirtyJavaScript("	checkWorkTypeId(i) &&\n");
        appendDirtyJavaScript("	checkWorktypedescription(i) &&\n");
        appendDirtyJavaScript("	checkPriorityId(i) &&\n");
        appendDirtyJavaScript("	checkPrioritydescription(i) &&\n");
        appendDirtyJavaScript("	checkPlanSDate(i) &&\n");
        appendDirtyJavaScript("	checkPlanFDate(i) &&\n");
        appendDirtyJavaScript("	checkPlanHrs(i);\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovItem0MchCode(i)\n");
        appendDirtyJavaScript("{\n"); 
        appendDirtyJavaScript("  var key_value = (getValue_('ITEM0_MCH_CODE',i).indexOf('%') !=-1)? getValue_('ITEM0_MCH_CODE',i):'';\n");
        appendDirtyJavaScript("  if (getValue_('CONNECTION_TYPE_DB',i) == 'VIM')\n");
        appendDirtyJavaScript("  {\n");
        appendDirtyJavaScript("          jLov = '../vimw/VimSerialStrWoLov.page';\n");
        appendDirtyJavaScript("  }\n");
        appendDirtyJavaScript("  else\n");
        appendDirtyJavaScript("  {\n");
	appendDirtyJavaScript("  objwherecond = \" OBJ_LEVEL IS NULL OR (OBJ_LEVEL IS NOT NULL AND Equipment_Object_Level_API.Get_Create_Wo(OBJ_LEVEL) = 'TRUE')\";\n");
        appendDirtyJavaScript("  	jLov = 'MaintenanceObjectAddrLov1.page?MCH_CODE_CONTRACT=' +  URLClientEncode(getValue_('ITEM0_MCHCONTRACT',i)) +'&__WHERE='+ objwherecond ;\n");
        //appendDirtyJavaScript("                 +'&(OBJ_LEVEL IS NULL OR (OBJ_LEVEL IS NOT NULL AND Equipment_Object_Level_API.Get_Create_Wo(OBJ_LEVEL) = 'TRUE'))';\n");
        appendDirtyJavaScript("  }\n");
        appendDirtyJavaScript("  openLOVWindow('ITEM0_MCH_CODE',i,jLov");
        appendDirtyJavaScript(",600,450,'validateItem0MchCode');\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function showLovWo(i,params)\n");
        appendDirtyJavaScript("{\n"); 
        appendDirtyJavaScript("	 if(params) param = params;\n");
        appendDirtyJavaScript("	 else param = '';\n");
        appendDirtyJavaScript("  sCust='");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(sCustId));		//XSS_Safe AMNILK 20070526
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("  sHeadSite='");
        appendDirtyJavaScript(mgr.encodeStringForJavascript(sContract));	//XSS_Safe AMNILK 20070526
        appendDirtyJavaScript("';\n");
        appendDirtyJavaScript("  wherecond = \" QUOTATION_ID IS NULL AND WORK_TYPE_ID IS NOT NULL AND STATE IN (Active_Work_Order_API.Finite_State_Decode__('FAULTREPORT'),Active_Work_Order_API.Finite_State_Decode__('WORKREQUEST'))\";\n");
        appendDirtyJavaScript("  wherecond = wherecond + \" AND CUSTOMER_NO='\"+ sCust+\"'\";\n"); 
        appendDirtyJavaScript("  wherecond = wherecond + \" AND Active_Work_Order_API.Get_Contract(WO_NO) = '\"+sHeadSite+\"'\";\n"); 
        appendDirtyJavaScript("  var enable_multichoice =(true && ITEM0_IN_FIND_MODE);\n");
        appendDirtyJavaScript("  jLov = '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=ACTIVE_WORK_ORDER_VIM_LOV&__FIELD=WO+No&__INIT=1'+param+'&__WHERE='+wherecond+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('HIDDENWO',i));\n"); 
        appendDirtyJavaScript("  openLOVWindow('HIDDENWO',i,jLov,600,450,'setHiddenWo');\n");
        appendDirtyJavaScript("}\n"); 

        appendDirtyJavaScript("function setHiddenWo(i)\n");
        appendDirtyJavaScript("{\n"); 
        appendDirtyJavaScript("  f.ISHIDDENWO.value = \"TRUE\";\n");
        appendDirtyJavaScript("	 f.submit();\n");
        appendDirtyJavaScript("}\n");  

        appendDirtyJavaScript(" function validatePlanHrs(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("setDirty();\n");
        appendDirtyJavaScript("if( !checkPlanHrs(i) ) return;\n");
        appendDirtyJavaScript("if( getValue_('PLAN_HRS',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("if( getValue_('PLAN_S_DATE',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("if( getValue_('PLAN_F_DATE',i).indexOf('%') != -1) return;\n");
	appendDirtyJavaScript("if( getValue_('PLAN_HRS',i)=='' )\n");
	appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("return;\n");
	appendDirtyJavaScript("}\n");
	appendDirtyJavaScript("window.status='Please wait for validation';\n");

	appendDirtyJavaScript(" r = __connect(\n");
	appendDirtyJavaScript("APP_ROOT+ 'pcmw/WorkOrderQuotation.page'+'?VALIDATE=PLAN_HRS'\n");
	appendDirtyJavaScript("+ '&PLAN_HRS=' + URLClientEncode(getValue_('PLAN_HRS',i))\n");
	appendDirtyJavaScript("+ '&PLAN_S_DATE=' + URLClientEncode(getValue_('PLAN_S_DATE',i))\n");
	appendDirtyJavaScript("+ '&PLAN_F_DATE=' + URLClientEncode(getValue_('PLAN_F_DATE',i))\n");
	appendDirtyJavaScript("	);\n");
	appendDirtyJavaScript("window.status='';\n");

	appendDirtyJavaScript("if( checkStatus_(r,'PLAN_HRS',i,'Execution Time') )\n");
	appendDirtyJavaScript("{\n");
	appendDirtyJavaScript("	assignValue_('PLAN_F_DATE',i,0);\n");
        appendDirtyJavaScript("}\n");
        appendDirtyJavaScript("}\n");
       
        if (bOpenNewWindow)
        {
            appendDirtyJavaScript("if (readCookie('NewWinOpen')=='true')\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   removeCookie('NewWinOpen',COOKIE_PATH);\n");
            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));		//XSS_Safe AMNILK 20070526
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
            appendDirtyJavaScript("}\n");
        }

        if (bPpChanged)
        {
            bPpChanged = false;
            appendDirtyJavaScript("if (confirm(\"");
            appendDirtyJavaScript(mgr.translateJavaScript("PCMWWOQUOTITEM0REPEXTPP: Do you want to replace existing pre-postings?"));
            appendDirtyJavaScript("\")) {\n");
            appendDirtyJavaScript("	  document.form.PRPOSTCHANGED.value = \"TRUE\";\n");
            appendDirtyJavaScript("       f.submit();\n");
            appendDirtyJavaScript("     } \n");
        }
        
    }

}
