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
*  File        : PMRoundDefinition.java 
*  Created     : ASP2JAVA Tool  010219  Created Using the ASP file PMRoundDefinition.asp
*  Modified    :
*  JEWILK  010403  Changed file extensions from .asp to .page
*  CHCRLK  010528  Made ITEM0_DESCRIPTION a database field.
*  ARWILK  010904  Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction(). 
*                  (Caused when cancelNew is performed after saveNew)
*  SHCHLK  011016  Did the security check.  
*  SHAFLK  020502  Bug 29244,Changed size of Note field and made it to a text area.
*  YAWILK  021203  Added MCH_CODE_CONTRACT and Modified MCH_CODE max length to 100       
*  SHAFLK  030402  Added methods isModuleInst1 and isModuleInst and used them inside validate() and preDefine().
*  SAPRLK  031217  Web Alignment - removed methods clone() and doReset().
*  ARWILK  040226  Edge Developments - (Enable Multirow RMB actions, Remove uneccessary global variables, Enhance Performance, Clean Javascript Code)
*  THWILK  040603  Added PM_REVISION and removed STD_JOB_ID and changed all the relevant places which was affected due to the key changes under 
*                  IID AMEC109A, Multiple Standard Jobs on PMs.
*  ARWILK  040715  PM_REVISION was set as insertable.
*  ARWILK  040722  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  ARWILK  040903  Modified preDefine.(IID AMEC111A: Std Jobs as PM Templates)
*  NAMELK  041110  Duplicated Translation Tags Corrected.
*  ARWILK  041111  Replaced getContents with printContents.
*  SHAFLK  050428  Bug 50914, Modified preDefine() method.
*  NIJALK  050617  Merged bug 50914.
*  NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile).
*  ERALLK  051208  Bug 54385, Modified the length and the height of 'Alternate Designation' and 'Materials' Fields. 
*  NIJALK  051221  Merged bug 54385.
*  NIJALK  060111  Changed DATE format to compatible with data formats in PG19.
*  AMNILK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMNILK  060904  Merged Bug Id: 58216.
*  AMNILK  070725  Eliminated XSS Security Vulnerability.
*  AMDILK  070731  Removed the scroll buttons of the parent when the detail block is in new or edit modes
*  NIJALK  070730  Bug 66572, Modified preDefine(),activatePm() and printContents().
*  AMDILK  070816  Merged bug 66572
*  SHAFLK  071026  Bug 67948, Modified preDefine(),activatePm(),obsoletePm() and printContents().
*  CHANLK  071201  Bug 67819, Change okFind to enable next button.
*  NIJALK  071219  Bug 69819, Removed ASPFields AGREEMENT_ID, CUSTOMERAGREEMENTDESCRIPTION.
*  SHAFLK  080107  Bug 70375, Modified okFindITEM0() and okFindITEM01().
*  SHAFLK  090306  Bug 81166, Modified preDefine().
*  SHAFLK  100125  Bug 87307, Added New RMB "Preposting".
*  NIJALK  100601  Bug 89752, Modified activatePm(), preDefine().
*  SHAFLK  101005  Bug 93366, Modified preDefine().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class PMRoundDefinition extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.PMRoundDefinition");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPContext ctx;

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

    private ASPField f;
    private ASPBlock b;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private ASPTransactionBuffer trans1;
    private ASPTransactionBuffer secBuff;
    private ASPQuery qry;
    private int currrow;
    private String val;
    private ASPCommand cmd;
    private ASPBuffer buf;
    private ASPQuery q;
    private ASPBuffer data;
    private int currrowitem;
    private String pmStartUnit;
    private String StartValue;
    private String selMon;
    private String selWee;
    private String selDay;
    private String clientValMon;
    private String clientValwee;
    private String clientValDay;
    private String temp;
    private ASPBuffer temp1;
    private String nStartval;
    private String calStatDate;
    private String startDate;
    private String calling_url;
    private ASPBuffer keys;
    private String isSecure[] = new String[7];
    private String dateMask;
    private ASPBuffer SecBuff;
    private boolean varSec;
    private boolean secPrep;
    private boolean secReNum;
    private boolean secRep;   
    // 040226  ARWILK  Begin  (Remove uneccessary global variables)
    private boolean bOpenNewWindow;
    private String urlString;
    private String newWinHandle;
    // 040226  ARWILK  End  (Remove uneccessary global variables)

    private boolean bSetObsolete;
    private boolean bHasActiveRepl;
    private String sMsgTxt;
    private String sMsgTxt1;
    private boolean secPrePost;   
    private ASPBuffer ret_data_buffer;
    private String lout;
    //===============================================================
    // Construction 
    //===============================================================
    public PMRoundDefinition(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();

        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();
        trans1 = mgr.newASPTransactionBuffer();

        secPrep = ctx.readFlag("SECPREP", false);
        secReNum = ctx.readFlag("SECRENUM", false);
        secRep = ctx.readFlag("SECREP", false);
        varSec = ctx.readFlag("VARSEC", false);
        secPrePost = ctx.readFlag("SECPREPOST",false);

        if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();
        else if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if ((!mgr.isEmpty(mgr.getQueryStringValue("ROUNDDEF_ID"))))
        {   
            mgr.setPageExpiring();
            okFind();
            okFindITEM0();
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        }
        else if (mgr.dataTransfered()){
            okFind();
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();

        securityChk();
        adjust();

        ctx.writeFlag("SECPREP", secPrep);
        ctx.writeFlag("SECRENUM", secReNum);
        ctx.writeFlag("SECREP", secRep);
        ctx.writeFlag("VARSEC", varSec);
        ctx.writeFlag("SECPREPOST",secPrePost);
    }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------
// This function is used to check security status for methods which
// are in modules Order,Purch and Invent .This function is called before
// the required method is performed.  -NUPE,21-07-2000

    public boolean checksec( String method,int ref) 
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
//-----------------------------  PERFORM FUNCTION  ----------------------------
//-----------------------------------------------------------------------------

    public void perform( String command) 
    {
        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();
        mgr.submit(trans);

        if (itemset0.countSelectedRows()>0)
            itemset0.setFilterOn();

        headset.goTo(currrow);
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();

        String txt;
        String stdJobId="";
        String stdJobContract="";
        String stdJobRev="";

        val = mgr.readValue("VALIDATE");

        if ("TEST_POINT_ID".equals(val))
        {
            cmd = trans.addCustomFunction("TESTPOINTDESC", "Equipment_Object_Test_Pnt_API.Get_Description", "MSEQOBJECTTESTPOINTDESCRIPTIO" );
            cmd.addParameter("ITEM0_CONTRACT", mgr.readValue("ITEM0_CONTRACT"));
            cmd.addParameter("MCH_CODE", mgr.readValue("MCH_CODE"));
            cmd.addParameter("TEST_POINT_ID", mgr.readValue("TEST_POINT_ID"));

            cmd = trans.addCustomFunction("LOCATION", "Equipment_Object_Test_Pnt_API.Get_Location", "MSEQOBJECTTESTPOINTLOCATION" );
            cmd.addParameter("ITEM0_CONTRACT", mgr.readValue("ITEM0_CONTRACT"));
            cmd.addParameter("MCH_CODE", mgr.readValue("MCH_CODE"));
            cmd.addParameter("TEST_POINT_ID", mgr.readValue("TEST_POINT_ID"));

            trans = mgr.validate(trans);

            String strTestPointDesc = trans.getValue("TESTPOINTDESC/DATA/MSEQOBJECTTESTPOINTDESCRIPTIO");
            String strLocation = trans.getValue("LOCATION/DATA/MSEQOBJECTTESTPOINTLOCATION");

            txt = (mgr.isEmpty(strTestPointDesc) ? "" :strTestPointDesc)  + "^"  + (mgr.isEmpty(strLocation) ? "" :strLocation) + "^";
            mgr.responseWrite(txt);
            mgr.endResponse();
        }

        else if ("SIGNATURE".equals(val))
        {
            cmd = trans.addCustomFunction("SIGID", "Company_Emp_API.Get_Max_Employee_Id", "SIGNATURE_ID" );
            cmd.addParameter("COMPANY");
            cmd.addParameter("SIGNATURE");

            trans = mgr.validate(trans);

            String strSignatureId = trans.getValue("SIGID/DATA/SIGNATURE_ID");

            txt = (mgr.isEmpty(strSignatureId) ? "" :strSignatureId)  + "^" ;
            mgr.responseWrite(txt);
            mgr.endResponse();
        }
        //=================only to get date formater============================================
        else if ("PM_START_UNIT".equals(val) || "START_VALUE".equals(val))
        {
            if ("Day".equals(mgr.readValue("PM_START_UNIT")))
            {
                buf = mgr.newASPBuffer();
                buf.addFieldItem("START_DATE_TEMP",mgr.readValue("START_VALUE"));
                mgr.responseWrite(buf.getFieldValue("START_DATE_TEMP") +"^" );
            }

            mgr.endResponse();
        }
    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void okFind()
    {
        ASPManager mgr = getASPManager();
        String  curr_row_exists = "FALSE";

        trans.clear();

        q = trans.addQuery(headblk);
        q.setOrderByClause("ROUNDDEF_ID");
        q.includeMeta("ALL");


        if (mgr.dataTransfered())
        {
            ASPBuffer retBuffer = mgr.getTransferedData();
            if (retBuffer.itemExists("ROUNDDEF_ID"))
            {
                String ret_round_id = retBuffer.getValue("ROUNDDEF_ID");
                q.addWhereCondition("ROUNDDEF_ID = ?");
                q.addParameter("ROUNDDEF_ID",ret_round_id);

            }
            else if (retBuffer.itemExists("CURR_ROW"))
            {
                curr_row_exists = "TRUE";
                ret_data_buffer = retBuffer.getBuffer("ROWS");
                currrow = Integer.parseInt(retBuffer.getValue("CURR_ROW"));
                lout = retBuffer.getValue("LAYOUT");
                q.addOrCondition(ret_data_buffer);

            }
            else
                q.addOrCondition(mgr.getTransferedData());
        }
        //Bug 67819, Start
        mgr.querySubmit(trans,headblk);
        //Bug 67819, End
        eval(headset.syncItemSets());

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWPMROUNDDEFINITIONNODATA: No data found."));
            headset.clear();
        }
        if (headset.countRows() == 1)
        {
            okFindITEM0();
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
        }

        if ("TRUE".equals(curr_row_exists))
        {
            headset.goTo(currrow);
            if ("1".equals(lout))
                headlay.setLayoutMode(headlay.MULTIROW_LAYOUT);
            else{
                headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
                okFindITEM0();
            }
        }
    }

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

        cmd = trans.addEmptyCommand("HEAD","PM_ROUND_DEFINITION_API.New__",headblk);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);

        data = trans.getBuffer("HEAD/DATA");
        headset.addRow(data);
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
	// Bug 58216 Start
        q.addWhereCondition("ROUNDDEF_ID = ?");
	q.addParameter("ROUNDDEF_ID",headset.getRow().getValue("ROUNDDEF_ID"));
	//Bug 58216 End
        q.setOrderByClause("PM_ORDER_NO");
        q.includeMeta("ALL");
        //Bug 70375, Start
        mgr.querySubmit(trans,itemblk0);
        //Bug 70375, End

        if (mgr.commandBarActivated())
        {
            if (itemset0.countRows() == 0 && "ITEM0.OkFind".equals(mgr.readValue("__COMMAND")))
            {
                mgr.showAlert(mgr.translate("PCMWPMROUNDDEFINITIONNODATA: No data found."));
                itemset0.clear();
            }
        }
        itemtbl0.clearQueryRow();

        headset.goTo(headrowno);
    }

    public void okFindITEM01()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        int headrowno = headset.getCurrentRowNo();

        q = trans.addQuery(itemblk0);
	// Bug 58216 Start
        q.addWhereCondition("ROUNDDEF_ID = ?");
	q.addParameter("ROUNDDEF_ID",headset.getRow().getValue("ROUNDDEF_ID"));
	//Bug 58216 End
        q.setOrderByClause("PM_ORDER_NO");
        q.includeMeta("ALL");
        //Bug 70375, Start
        mgr.querySubmit(trans,itemblk0);
        //Bug 70375, End

        headset.goTo(headrowno);
    }

    public void countFindITEM0()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk0);
	// Bug 58216 Start
        q.addWhereCondition("ROUNDDEF_ID = ?");
	q.addParameter("ROUNDDEF_ID",headset.getRow().getValue("ROUNDDEF_ID"));
	//Bug 58216 End
        q.setOrderByClause("PM_NO,PM_REVISION");
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
        itemset0.clear();
    }

    public void newRowITEM0()
    {
        ASPManager mgr = getASPManager();


        trans.clear();

        cmd = trans.addEmptyCommand("ITEM0","PM_ACTION_API.New__",itemblk0);
        cmd.setOption("ACTION","PREPARE");

        cmd = trans.addCustomFunction("ITEM1PMTYPE","Pm_Type_API.Get_Client_Value(1)","ITEM1_PM_TYPE" );

        trans = mgr.perform(trans);

        data = trans.getBuffer("ITEM0/DATA");

        String pmtype_ = trans.getValue("ITEM1PMTYPE/DATA/ITEM1_PM_TYPE");

        data.setFieldItem("PM_TYPE", pmtype_);
        data.setFieldItem("ITEM0_ROUNDDEF_ID",headset.getRow().getValue("ROUNDDEF_ID"));

        itemset0.addRow(data);

    }

    public void saveReturnITEM0()
    {
        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();
        currrowitem = itemset0.getCurrentRowNo();
        itemset0.changeRow();

        pmStartUnit = mgr.readValue("PM_START_UNIT");
        StartValue = mgr.readValue("START_VALUE");

        ASPBuffer tempbuff = itemset0.getRow();
        tempbuff.setFieldItem("ITEM0_CONTRACT2",itemset0.getRow().getValue("MCH_CODE_CONTRACT"));     
        itemset0.setRow(tempbuff);

        if (!(mgr.isEmpty(pmStartUnit)))
        {
            selMon = "select (Pm_Start_Unit_API.Get_Client_Value(2)          ) CLIENT_START2 from DUAL";
            selWee = "select (Pm_Start_Unit_API.Get_Client_Value(1)          ) CLIENT_START1 from DUAL";
            selDay = "select (Pm_Start_Unit_API.Get_Client_Value(0)          ) CLIENT_START from DUAL";
            trans1.addQuery("YYM", selMon);
            trans1.addQuery("YYW", selWee);
            trans1.addQuery("YYD", selDay);
            trans1 = mgr.perform(trans1);

            clientValMon  = trans1.getValue("YYM/DATA/CLIENT_START2");
            clientValwee  = trans1.getValue("YYW/DATA/CLIENT_START1");
            clientValDay  = trans1.getValue("YYD/DATA/CLIENT_START");

            trans1.clear();


            if (clientValMon.equals(pmStartUnit))
            {
                temp = mgr.readValue("START_VALUE");
                int length = temp.length();
                String nTYvalue = temp.substring(0,2);
                String nTMvalue = temp.substring(2,4);

		//Bug 58216 Start
                String sel2 = "select to_number( ? ) TEMPSTA from DUAL";
                String sel3 = "select to_number( ? ) TEMPTY from DUAL";
                String sel4 = "select to_number( ? ) TEMPTM from DUAL";
                q = trans1.addQuery("XX2",sel2);
                q.addParameter("START_VALUE",StartValue);
                q = trans1.addQuery("XX3",sel3);
                q.addParameter("START_VALUE",nTYvalue);
                q = trans1.addQuery("XX4",sel4);
                q.addParameter("START_VALUE",nTMvalue);
		//Bug 58216 End
                trans1 = mgr.perform(trans1);
                nStartval  = trans1.getValue("XX2/DATA/TEMPSTA");
                double nTY = trans1.getNumberValue("XX3/DATA/TEMPTY");
                double nTM = trans1.getNumberValue("XX4/DATA/TEMPTM");

                if (( length != 4 ) ||  ( nTM <1 ) ||  ( nTM>12 ) ||  ( nTY<0 ))
                {
                    dateMask = mgr.getFormatMask("Date",true);
                    mgr.showAlert(mgr.translate("PCMWPMROUNDDEFINITIONINFO1: Invalid Start Value ")+temp+mgr.translate("PCMWPMROUNDDEFINITIONINFO2: . The Format should be ")+ dateMask+mgr.translate("PCMWPMROUNDDEFINITIONINFO3:   for Start Unit 'Day' and YYMM or YYWW for Start Unit 'Month' or 'Week'."));

                    if (mgr.isEmpty(mgr.readValue("PM_NO")) && mgr.isEmpty(mgr.readValue("PM_REVISION")))
                        itemlay0.setLayoutMode(itemlay0.NEW_LAYOUT);
                    else
                        itemlay0.setLayoutMode(itemlay0.EDIT_LAYOUT);
                }
                else
                {
                    trans1.clear();
		    
		    //Bug 58216 Start
                    String sel = "select to_date(?,'RRMM') START_DATE from DUAL";
                    q = trans1.addQuery("XX",sel);
		    q.addParameter("START_VALUE",StartValue);
		    //Bug 58216 End
                    trans1 = mgr.perform(trans1);
                    calStatDate  = trans1.getValue("XX/DATA/START_DATE");
                    temp1 = itemset0.getRow();
                    temp1.setValue("START_DATE",calStatDate);
                    temp1.setValue("START_VALUE",StartValue);
                    itemset0.setRow(temp1);
                    trans1.clear();
                    trans1 = mgr.submit(trans1);
                    headset.goTo(currrow);
                    itemset0.goTo(currrowitem);
                    itemlay0.setLayoutMode(itemlay0.MULTIROW_LAYOUT);             
                }
            }
            else if (clientValwee.equals(pmStartUnit))
            {
                temp = mgr.readValue("START_VALUE");

                cmd = trans1.addCustomFunction("STARTDA","Pm_Calendar_API.Get_Date","START_DATE");
                cmd.addParameter("START_VALUE", StartValue);
                cmd.addParameter("INDEX1", "1");
                trans1=mgr.perform(trans1);  
                startDate = trans1.getValue("STARTDA/DATA/START_DATE");


                if (( "".equals(startDate) ))
                {
                    dateMask = mgr.getFormatMask("Date",true);
                    mgr.showAlert(mgr.translate("PCMWPMROUNDDEFINITIONINFO1: Invalid Start Value ")+temp+mgr.translate("PCMWPMROUNDDEFINITIONINFO2: . The Format should be ")+ dateMask+mgr.translate("PCMWPMROUNDDEFINITIONINFO3:   for Start Unit 'Day' and YYMM or YYWW for Start Unit 'Month' or 'Week'."));
                    headset.goTo(currrow);
                    itemset0.goTo(currrowitem);

                    if (mgr.isEmpty(mgr.readValue("PM_NO")) && mgr.isEmpty(mgr.readValue("PM_REVISION")))
                        itemlay0.setLayoutMode(itemlay0.NEW_LAYOUT);
                    else
                        itemlay0.setLayoutMode(itemlay0.EDIT_LAYOUT);
                }
                else
                {
                    temp1 = itemset0.getRow();
                    temp1.setValue("START_DATE",trans1.getValue("STARTDA/DATA/START_DATE"));
                    temp1.setValue("START_VALUE",StartValue);
                    itemset0.setRow(temp1);
                    trans1.clear();
                    trans1 = mgr.submit(trans1);
                    headset.goTo(currrow);
                    itemset0.goTo(currrowitem);
                    itemlay0.setLayoutMode(itemlay0.MULTIROW_LAYOUT);             
                }
            }

            else if (clientValDay.equals(pmStartUnit))
            {
                dateMask = mgr.getFormatMask("Date",true);
                trans1.clear();
                ASPBuffer buf = mgr.newASPBuffer();
                buf.addFieldItem("START_VALUE_TEMP",StartValue);

                //Bug 58216 Start
		String sel = "select to_date( ? ,'YYYY-MM-DD-hh24.MI.ss') START_DATE from DUAL";
                q = trans1.addQuery("XX",sel);
		q.addParameter("START_VALUE",buf.getValue("START_VALUE_TEMP"));
		//Bug 58216 End
                trans1 = mgr.perform(trans1);
                calStatDate  = trans1.getValue("XX/DATA/START_DATE");

                temp1 = itemset0.getRow();

                temp1.setValue("START_DATE",calStatDate);
                temp1.setValue("START_VALUE",StartValue);
                itemset0.setRow(temp1);
                trans1.clear();
                trans1 = mgr.submit(trans1);
                headset.goTo(currrow);
                itemset0.goTo(currrowitem);
                itemlay0.setLayoutMode(itemlay0.MULTIROW_LAYOUT);
            }
            else
            {
                trans1.clear();
                trans1 = mgr.submit(trans1);
            }

        }
        else if (mgr.isEmpty(pmStartUnit))
        {
            trans1.clear();
            mgr.submit(trans1);
            headset.goTo(currrow);
        }

    }

    public void saveNewITEM0()
    {
        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();
        currrowitem = itemset0.getCurrentRowNo();
        itemset0.changeRow();

        pmStartUnit = mgr.readValue("PM_START_UNIT");

        StartValue = mgr.readValue("START_VALUE");

        ASPBuffer tempbuff = itemset0.getRow();
        tempbuff.setFieldItem("ITEM0_CONTRACT2",itemset0.getRow().getValue("MCH_CODE_CONTRACT"));     
        itemset0.setRow(tempbuff);

        if (!(mgr.isEmpty(pmStartUnit)))
        {
            selMon = "select (Pm_Start_Unit_API.Get_Client_Value(2)          ) CLIENT_START2 from DUAL";
            selWee = "select (Pm_Start_Unit_API.Get_Client_Value(1)          ) CLIENT_START1 from DUAL";
            selDay = "select (Pm_Start_Unit_API.Get_Client_Value(0)          ) CLIENT_START from DUAL";
            trans1.addQuery("YYM", selMon);
            trans1.addQuery("YYW", selWee);
            trans1.addQuery("YYD", selDay);
            trans1 = mgr.perform(trans1);

            clientValMon  = trans1.getValue("YYM/DATA/CLIENT_START2");
            clientValwee  = trans1.getValue("YYW/DATA/CLIENT_START1");
            clientValDay  = trans1.getValue("YYD/DATA/CLIENT_START");

            trans1.clear();


            if (clientValMon.equals(pmStartUnit))
            {
                temp = mgr.readValue("START_VALUE");
                int length = temp.length();
                String nTYvalue = temp.substring(0,2);
                String nTMvalue = temp.substring(2,4);
		//Bug 58216 Start
                String sel2 = "select to_number( ? ) TEMPSTA from DUAL";
                String sel3 = "select to_number( ? ) TEMPTY from DUAL";
                String sel4 = "select to_number( ? ) TEMPTM from DUAL";
                q = trans1.addQuery("XX2",sel2);
		q.addParameter("START_VALUE",StartValue);
                q = trans1.addQuery("XX3",sel3);
		q.addParameter("START_VALUE",nTYvalue);
                q = trans1.addQuery("XX4",sel4);
		q.addParameter("START_VALUE",nTMvalue);
		//Bug 58216 End
                trans1 = mgr.perform(trans1);
                nStartval  = trans1.getValue("XX2/DATA/TEMPSTA");
                double nTY = trans1.getNumberValue("XX3/DATA/TEMPTY");
                double nTM = trans1.getNumberValue("XX4/DATA/TEMPTM");

                if (( length != 4 ) ||  ( nTM <1 ) ||  ( nTM>12 ) ||  ( nTY<0 ))
                {
                    dateMask = mgr.getFormatMask("Date",true);
                    mgr.showAlert(mgr.translate("PCMWPMROUNDDEFINITIONINFO1: Invalid Start Value ")+temp+mgr.translate("PCMWPMROUNDDEFINITIONINFO2: . The Format should be ")+ dateMask+mgr.translate("PCMWPMROUNDDEFINITIONINFO3:   for Start Unit 'Day' and YYMM or YYWW for Start Unit 'Month' or 'Week'."));
                    itemlay0.setLayoutMode(itemlay0.NEW_LAYOUT);
                }
                else
                {
                    trans1.clear();
		    //Bug 58216 Start
                    String sel = "select to_date( ? ,'RRMM') START_DATE from DUAL";
                    q = trans1.addQuery("XX",sel);
		    q.addParameter("START_VALUE",StartValue);
		    //Bug 58216 End
                    trans1 = mgr.perform(trans1);
                    calStatDate  = trans1.getValue("XX/DATA/START_DATE");
                    temp1 = itemset0.getRow();
                    temp1.setValue("START_DATE",calStatDate);
                    temp1.setValue("START_VALUE",StartValue);
                    itemset0.setRow(temp1);
                    trans1.clear();
                    trans1 = mgr.submit(trans1);
                    headset.goTo(currrow);
                    trans.clear();
                    newRowITEM0();
                }
            }
            else if (clientValwee.equals(pmStartUnit))
            {
                temp = mgr.readValue("START_VALUE");

                cmd = trans1.addCustomFunction("STARTDA","Pm_Calendar_API.Get_Date","START_DATE");
                cmd.addParameter("START_VALUE", StartValue);
                cmd.addParameter("INDEX1", "1");
                trans1=mgr.perform(trans1);  
                startDate = trans1.getValue("STARTDA/DATA/START_DATE");


                if (( "".equals(startDate) ))
                {
                    dateMask = mgr.getFormatMask("Date",true);
                    mgr.showAlert(mgr.translate("PCMWPMROUNDDEFINITIONINFO1: Invalid Start Value ")+temp+mgr.translate("PCMWPMROUNDDEFINITIONINFO2: . The Format should be ")+ dateMask+mgr.translate("PCMWPMROUNDDEFINITIONINFO3:   for Start Unit 'Day' and YYMM or YYWW for Start Unit 'Month' or 'Week'."));
                    itemlay0.setLayoutMode(itemlay0.NEW_LAYOUT);

                }
                else
                {
                    temp1 = itemset0.getRow();
                    temp1.setValue("START_DATE",trans1.getValue("STARTDA/DATA/START_DATE"));
                    temp1.setValue("START_VALUE",StartValue);
                    itemset0.setRow(temp1);
                    trans1.clear();
                    trans1 = mgr.submit(trans1);
                    headset.goTo(currrow);
                    trans.clear();
                    newRowITEM0();
                }
            }

            else if (clientValDay.equals(pmStartUnit))
            {
                dateMask = mgr.getFormatMask("Date",true);
                trans1.clear();
                ASPBuffer buf = mgr.newASPBuffer();
                buf.addFieldItem("START_VALUE_TEMP",StartValue);
		
		//Bug 58216 Start
                String sel = "select to_date( ? ,'YYYY-MM-DD-hh24.MI.ss') START_DATE from DUAL";
		q = trans1.addQuery("XX",sel);
		q.addParameter("START_VALUE",buf.getValue("START_VALUE_TEMP"));
		//Bug 58216 End
                trans1 = mgr.perform(trans1);
                calStatDate  = trans1.getValue("XX/DATA/START_DATE");

                temp1 = itemset0.getRow();

                temp1.setValue("START_DATE",calStatDate);
                temp1.setValue("START_VALUE",StartValue);
                itemset0.setRow(temp1);
                trans1.clear();
                trans1 = mgr.submit(trans1);
                headset.goTo(currrow);
                trans.clear();
                newRowITEM0();

            }
            else
            {
                trans1.clear();
                trans1 = mgr.submit(trans1);
            }

        }
        else if (mgr.isEmpty(pmStartUnit))
        {
            trans1.clear();
            mgr.submit(trans1);
            headset.goTo(currrow);
            trans.clear();
            newRowITEM0();
        }

    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void preposting()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();        

        lout = (headlay.isMultirowLayout()?"1":"0");
        calling_url=mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url);

        cmd = trans1.addCustomFunction("GETPRE","Pm_Round_Definition_API.Get_Pre_Posting_Id","PRE_POSTING_ID");
        cmd.addParameter("ROUNDDEF_ID",headset.getRow().getValue("ROUNDDEF_ID"));

        trans1=mgr.perform(trans1);  
        String pre_posting_id = trans1.getValue("GETPRE/DATA/PRE_POSTING_ID");
        String contract = headset.getRow().getValue("CONTRACT");
        trans.clear();

        cmd=trans.addCustomFunction("COM","Site_API.Get_Company","COMPANY");
        cmd.addParameter("CONTRACT",headset.getValue("CONTRACT"));

        cmd = trans.addCustomCommand("ALLOCODEPART","Pre_Accounting_API.Get_Allowed_Codeparts");
        cmd.addParameter("CODE_A");
        cmd.addParameter("CODE_B");
        cmd.addParameter("CODE_C");
        cmd.addParameter("CODE_D");
        cmd.addParameter("CODE_E");
        cmd.addParameter("CODE_F");
        cmd.addParameter("CODE_G");
        cmd.addParameter("CODE_H");
        cmd.addParameter("CODE_I");
        cmd.addParameter("CODE_J");
        cmd.addParameter("STR_CODE","T51"); 
        cmd.addParameter("CONTROL_TYPE",""); 
        cmd.addReference("COMPANY","COM/DATA");

        trans = mgr.perform(trans);

        String code_a = trans.getValue("ALLOCODEPART/DATA/CODE_A");
        String code_b = trans.getValue("ALLOCODEPART/DATA/CODE_B");
        String code_c = trans.getValue("ALLOCODEPART/DATA/CODE_C");
        String code_d = trans.getValue("ALLOCODEPART/DATA/CODE_D");
        String code_e = trans.getValue("ALLOCODEPART/DATA/CODE_E");
        String code_f = trans.getValue("ALLOCODEPART/DATA/CODE_F");
        String code_g = trans.getValue("ALLOCODEPART/DATA/CODE_G");
        String code_h = trans.getValue("ALLOCODEPART/DATA/CODE_H");
        String code_i = trans.getValue("ALLOCODEPART/DATA/CODE_I");
        String code_j = trans.getValue("ALLOCODEPART/DATA/CODE_J");
        
        ASPBuffer prePostBuffer = mgr.newASPBuffer();
        ASPBuffer data = prePostBuffer.addBuffer("dataBuffer");
        data.addItem("CONTRACT",contract);
        data.addItem("PRE_ACCOUNTING_ID",pre_posting_id);
        data.addItem("ENABL0",code_a);
        data.addItem("ENABL1",code_b);
        data.addItem("ENABL2",code_c);
        data.addItem("ENABL3",code_d);
        data.addItem("ENABL4",code_e);
        data.addItem("ENABL5",code_f);
        data.addItem("ENABL6",code_g);
        data.addItem("ENABL7",code_h);
        data.addItem("ENABL8",code_i);
        data.addItem("ENABL9",code_j);
        data.addItem("ENABL10","0");

        ASPBuffer return_buffer = prePostBuffer.addBuffer("return_buffer");
        ASPBuffer ret = return_buffer.addBuffer("ROWS");
        ret.parse(headset.getRows("ROUNDDEF_ID").format());
        return_buffer.addItem("CURR_ROW",Integer.toString(headset.getCurrentRowNo()));
        return_buffer.addItem("LAYOUT",lout);
        mgr.transferDataTo("../mpccow/PreAccountingDlg.page",prePostBuffer);
    }

    public void prepareRound()
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
        urlString = createTransferUrl("PmActionRound.page", itemset0.getSelectedRows("PM_NO,PM_REVISION"));
        newWinHandle = "prepareRound"; 
        // 040226  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void reNoRoutePM()
    {
        // 040226  ARWILK  Begin  (Enable Multirow RMB actions)
        ASPManager mgr = getASPManager();

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());
        else
            itemset0.selectRow();

        bOpenNewWindow = true;
        urlString = "ChangePMActionDlg.page?ROUNDDEF_ID=" + mgr.URLEncode(headset.getRow().getValue("ROUNDDEF_ID"));
        newWinHandle = "reNoRoutePM"; 
        // 040226  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void replacements()
    {
        ASPManager mgr = getASPManager();

        calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL", calling_url);

        // 040226  ARWILK  Begin  (Enable Multirow RMB actions)
        if (itemlay0.isMultirowLayout())
            itemset0.store();
        else
        {
            itemset0.unselectRows();
            itemset0.selectRow();
        }

        bOpenNewWindow = true;
        urlString = createTransferUrl("Replacements.page", itemset0.getSelectedRows("PM_NO,PM_REVISION"));
        newWinHandle = "replacements"; 
        // 040226  ARWILK  End  (Enable Multirow RMB actions)
    }

    public void performITEM0(String command) 
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        int headcur = headset.getCurrentRowNo();

        if (itemlay0.isMultirowLayout())
        {
            itemset0.storeSelections();
            itemset0.markSelectedRows(command);
            mgr.submit(trans);

            itemset0.refreshAllRows();
        }
        else
        {
            itemset0.unselectRows();
            itemset0.markRow(command);
            int currrow = itemset0.getCurrentRowNo();
            mgr.submit(trans);

            itemset0.goTo(currrow);
            itemset0.refreshRow();
        }   

        headset.goTo(headcur);
    }

    public void activatePm()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        int count;
        int headcur = headset.getCurrentRowNo();

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());

        int currrow = itemset0.getCurrentRowNo();
        itemset0.markRow("ACTIVE__");

        String sPmNo = itemset0.getValue("PM_NO"); 
        String sPmRev = itemset0.getValue("PM_REVISION"); 

	//Bug 89752, Start
	cmd = trans.addCustomFunction("GETACTIVEREV","Pm_Action_API.Get_Active_Revision","ACTIVE_REV");
	cmd.addParameter("PM_NO", sPmNo);
	//Bug 89752, End

        cmd = trans.addCustomFunction("SETPREVACTOBSOLETE","Pm_Action_API.Set_Active_To_Obsolete","NUMDUMMY");
        cmd.addParameter("PM_NO", sPmNo);

	//Bug 89752, Start
	cmd = trans.addCustomCommand("COPYGENVALUES","Pm_Action_Criteria_API.Copy_Generation_Values");
	cmd.addParameter("PM_NO", sPmNo);
	cmd.addParameter("PM_REVISION", sPmRev);
	cmd.addReference("ACTIVE_REV", "GETACTIVEREV/DATA");
	//Bug 89752, End

        trans = mgr.perform(trans);

        double numRows = trans.getNumberValue("SETPREVACTOBSOLETE/DATA/NUMDUMMY");

        if (numRows > 0)
        {
            //Bug 67948, start
            trans.clear();
            cmd = trans.addCustomCommand("REMOBSREP","Pm_Action_Replaced_API.Remove_Replacements");
            cmd.addParameter("PM_NO", sPmNo);
            cmd.addParameter("PM_REVISION", sPmRev);            
            trans = mgr.perform(trans);
            //Bug 67948, end

            bSetObsolete = true;

            String txt1 = mgr.translate("PCMWPMRNDDEFPREVACTTOOBONE1: Previous");
            String txt2 = mgr.translate("PCMWPMRNDDEFPREVACTTOOBONE2: Active");
            String txt3 = mgr.translate("PCMWPMRNDDEFPREVACTTOOBONE3: Revision for PM No &1 was set to",sPmNo);
            String txt4 = mgr.translate("PCMWPMRNDDEFPREVACTTOOBONE4: Obsolete");

            sMsgTxt = txt1 + " \"" + txt2 + "\" " + txt3 + " \"" + txt4 + "\"."; 
        }

        trans.clear();
        mgr.submit(trans);

        itemset0.refreshAllRows();

        headset.goTo(headcur);
        itemset0.goTo(currrow);

    }

    public void obsoletePm()
    {
        ASPManager mgr = getASPManager();

        //Bug 67948, start
        String sPmNo = itemset0.getValue("PM_NO"); 
        String sPmRev = itemset0.getValue("PM_REVISION"); 
        //Bug 67948, end

        performITEM0("OBSOLETE__");

        //Bug 67948, start
        trans.clear();
        cmd = trans.addCustomCommand("REMCURROBSREP","Pm_Action_Replaced_API.Remove_Curr_Replacements");
        cmd.addParameter("PM_NO", sPmNo);
        cmd.addParameter("PM_REVISION", sPmRev);

        trans = mgr.perform(trans);
        //Bug 67948, end
    }

    public void preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("ROUNDDEF_ID");
        f.setSize(16);
        f.setMandatory();
        f.setUpperCase();
        f.setInsertable();
        f.setMaxLength(8);
        f.setHilite();
        f.setLabel("PCMWPMROUNDDEFINITIONRNDDEFID: Route ID");
        f.setReadOnly();

        f = headblk.addField("DESCRIPTION");
        f.setSize(40);
        f.setMandatory();
        f.setReadOnly();
        f.setHilite();
        f.setInsertable();
        f.setMaxLength(60);
        f.setLabel("PCMWPMROUNDDEFINITIONDESC: Description");

        f = headblk.addField("CONTRACT");
        f.setSize(8);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);     
        f.setMandatory();
        f.setMaxLength(5);
        f.setLabel("PCMWPMROUNDDEFINITIONCONT: Site");
        f.setUpperCase();

        f = headblk.addField("NOTE");
        f.setSize(66);
        f.setHeight(4);//Bug 29244
        f.setLabel("PCMWPMROUNDDEFINITIONNOTE: Note");
        f.setMaxLength(2000);//Bug 29244

        f = headblk.addField("PRE_POSTING_ID","Number");
        f.setHidden();

        f = headblk.addField("CODE_A");
        f.setHidden(); 
        f.setFunction("''");

        f = headblk.addField("CODE_B");
        f.setHidden(); 
        f.setFunction("''");

        f = headblk.addField("CODE_C");
        f.setHidden(); 
        f.setFunction("''");

        f = headblk.addField("CODE_D");
        f.setHidden(); 
        f.setFunction("''");

        f = headblk.addField("CODE_E");
        f.setHidden(); 
        f.setFunction("''");

        f = headblk.addField("CODE_F");
        f.setHidden(); 
        f.setFunction("''");

        f = headblk.addField("CODE_G");
        f.setHidden(); 
        f.setFunction("''");

        f = headblk.addField("CODE_H");
        f.setHidden(); 
        f.setFunction("''");

        f = headblk.addField("CODE_I");
        f.setHidden(); 
        f.setFunction("''");

        f = headblk.addField("CODE_J");
        f.setHidden(); 
        f.setFunction("''");

        f = headblk.addField("CONTROL_TYPE");
        f.setHidden(); 
        f.setFunction("''");

        f = headblk.addField("STR_CODE");
        f.setHidden(); 
        f.setFunction("''");

        headblk.setView("PM_ROUND_DEFINITION");
        headblk.defineCommand("PM_ROUND_DEFINITION_API","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWPMROUNDDEFINITIONHD: PM Actions for Route List"));
        headtbl.setWrap();

        headbar = mgr.newASPCommandBar(headblk);
        headbar.defineCommand(headbar.SAVERETURN,null,"checkHeadFields(-1)");
        headbar.defineCommand(headbar.SAVENEW,null,"checkHeadFields(-1)");

        headbar.addCustomCommand("preposting",mgr.translate("PCMWPMROUNDDEFINITIONPREPOST: Preposting..."));

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

        //------------------------------------------------------------------------------------------------------------
        //------------------------------------------------------------------------------------------------------------
        //------------------------------------------------------------------------------------------------------------

        itemblk0 = mgr.newASPBlock("ITEM0");

        f = itemblk0.addField("ITEM0_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk0.addField("ITEM0_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk0.addField("PM_ORDER_NO","Number");
        f.setSize(7);
        f.setLabel("PCMWPMROUNDDEFINITIONPMORNO: Order");

        f = itemblk0.addField("PM_NO","Number","#");
        f.setSize(8);
        f.setReadOnly();
        f.setLabel("PCMWPMROUNDDEFINITIONPMNO: PM No");

        f = itemblk0.addField("PM_REVISION");
        f.setSize(10);
        f.setReadOnly();
        f.setInsertable();
        f.setLabel("PCMWPMROUNDDEFINITIONPMREV: PM Revision");

        f = itemblk0.addField("ITEM0_CONTRACT");
        f.setSize(11);
        f.setDynamicLOV("SITE",600,445);
        f.setMaxLength(5);
        f.setMandatory();
        f.setLabel("PCMWPMROUNDDEFINITIONIT0CON: Site");
        f.setDbName("MCH_CODE_CONTRACT");
        f.setUpperCase();

        f = itemblk0.addField("COMPANY");
        f.setSize(11);
        f.setHidden();
        f.setUpperCase();

        f = itemblk0.addField("ITEM0_CONTRACT2");
        f.setHidden();
        f.setDbName("CONTRACT");

        f = itemblk0.addField("MCH_CODE");
        f.setSize(22);
        f.setMaxLength(100);
        f.setDynamicLOV("MAINTENANCE_OBJECT","ITEM0_CONTRACT CONTRACT",600,445);      
        f.setMandatory();
        f.setHyperlink("../equipw/EquipmentAllObjectLightRedirect.page","MCH_CODE,ITEM0_CONTRACT","NEWWIN");
        f.setLabel("PCMWPMROUNDDEFINITIONMCHCODE: Object ID");
        f.setUpperCase();

        f = itemblk0.addField("MCHNAME");
        f.setSize(25);
        f.setLabel("PCMWPMROUNDDEFINITIONMCHNAME: Object Description");
        f.setReadOnly();
        f.setMaxLength(45);
        f.setFunction("Maintenance_Object_API.Get_Mch_Name(:ITEM0_CONTRACT,:MCH_CODE)");
        mgr.getASPField("MCH_CODE").setValidation("MCHNAME");

        f = itemblk0.addField("TEST_POINT_ID");
        f.setSize(11);
        f.setMaxLength(6); 
        f.setDynamicLOV("EQUIPMENT_OBJECT_TEST_PNT","ITEM0_CONTRACT,MCH_CODE",600,445);     
        f.setLabel("PCMWPMROUNDDEFINITIONTPID: Testpoint");
        f.setCustomValidation("TEST_POINT_ID,MCH_CODE,ITEM0_CONTRACT", "MSEQOBJECTTESTPOINTDESCRIPTIO,MSEQOBJECTTESTPOINTLOCATION");
        f.setUpperCase();

        f = itemblk0.addField("MSEQOBJECTTESTPOINTDESCRIPTIO");
        f.setSize(20);
        f.setLabel("PCMWPMROUNDDEFINITIONMSEQOBJECTTESTPOINTDESCRIPTIO: Testpoint Description");
        f.setReadOnly();
        f.setFunction("Equipment_Object_Test_Pnt_API.Get_Description(:ITEM0_CONTRACT,:MCH_CODE,:TEST_POINT_ID)");
        f.setDefaultNotVisible();

        f = itemblk0.addField("MSEQOBJECTTESTPOINTLOCATION");
        f.setSize(11);
        f.setMaxLength(20);
        f.setLabel("PCMWPMROUNDDEFINITIONMSEQOBJTPLOC: Location");
        f.setReadOnly();
        f.setFunction("Equipment_Object_Test_Pnt_API.Get_Location(:ITEM0_CONTRACT,:MCH_CODE,:TEST_POINT_ID)");
        f.setDefaultNotVisible();

        f = itemblk0.addField("ACTION_CODE_ID");
        f.setSize(12);
        f.setDynamicLOV("MAINTENANCE_ACTION",600,445);
        f.setMandatory();
        f.setMaxLength(10);
        f.setLabel("PCMWPMROUNDDEFINITIONACID: Action");
        f.setUpperCase();

        f = itemblk0.addField("ACTIONDESCR");
        f.setSize(17);
        f.setReadOnly();
        f.setDefaultNotVisible();
        f.setFunction("Maintenance_Action_API.Get_Description(:ACTION_CODE_ID)");
        mgr.getASPField("ACTION_CODE_ID").setValidation("ACTIONDESCR");

        f = itemblk0.addField("STATE");
        f.setSize(20);
        f.setReadOnly();
        f.setLabel("PCMWPMROUNDDEFSTATE: State");

        f = itemblk0.addField("ITEM0_OBJSTATE");
        f.setDbName("OBJSTATE");
        f.setHidden();

        f = itemblk0.addField("OLD_REVISION");
        f.setFunction("Pm_Action_API.Get_Old_Revision(:PM_NO,:PM_REVISION)");
        f.setReadOnly();
        f.setLabel("PCMWPMROUNDDEFOLDREV: Old Revision");

        f = itemblk0.addField("PM_TYPE");
        f.setSize(13);
        f.setHidden();

        f = itemblk0.addField("SIGNATURE");
        f.setSize(11);
        f.setMaxLength(20);
        f.setDynamicLOV("EMPLOYEE_LOV",600,445);     
        f.setLabel("PCMWPMROUNDDEFINITIONSIGN: Signature");
        f.setCustomValidation("SIGNATURE,COMPANY", "SIGNATURE_ID");
        f.setUpperCase(); 
        f.setDefaultNotVisible();

        f = itemblk0.addField("SIGNATURE_ID");
        f.setSize(8);
        f.setHidden();
        f.setLabel("PCMWPMROUNDDEFINITIONSIGID: Signature Id");
        f.setUpperCase();

        f = itemblk0.addField("OP_STATUS_ID");
        f.setSize(18);
        f.setDynamicLOV("OPERATIONAL_STATUS",600,445); 
        f.setMaxLength(3);
        f.setLabel("PCMWPMROUNDDEFINITIONOPSID: Operational Status");
        f.setUpperCase();

        f = itemblk0.addField("ORG_CONTRACT");
        f.setSize(15);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445); 
        f.setMandatory();
        f.setMaxLength(5);
        f.setLabel("PCMWPMROUNDDEFINITIONWOCONTRACT: WO Site");
        f.setUpperCase();
        f.setInsertable();
        f.setReadOnly();

        f = itemblk0.addField("ORG_CODE");
        f.setSize(20);
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ORG_CONTRACT CONTRACT",600,445); 
        f.setMandatory();
        f.setMaxLength(8);
        f.setLabel("PCMWPMROUNDDEFINITIONORGCODE: Maintenance Organization");
        f.setUpperCase();

        f = itemblk0.addField("PRIORITY_ID");
        f.setSize(8);
        f.setMaxLength(1);
        f.setDynamicLOV("MAINTENANCE_PRIORITY",600,445); 
        f.setLabel("PCMWPMROUNDDEFINITIONPRIOID: Priority");
        f.setUpperCase();
        f.setDefaultNotVisible();

        f = itemblk0.addField("WORK_TYPE_ID");
        f.setSize(9);
        f.setDynamicLOV("WORK_TYPE",600,445); 
        f.setLabel("PCMWPMROUNDDEFINITIONWORK_TYPE_ID: Work Type");
        f.setUpperCase();
        f.setDefaultNotVisible();

        f = itemblk0.addField("PM_GENERATEABLE");
        f.setSize(13);
        f.setSelectBox();
        f.enumerateValues("PM_GENERATEABLE_API");
        f.setLabel("PCMWPMROUNDDEFINITIONPM_GENERATEABLE: Generateable");
        f.setDefaultNotVisible();

        f = itemblk0.addField("PM_PERFORMED_DATE_BASED");
        f.setSize(22);
        f.setSelectBox();
        f.enumerateValues("PM_PERFORMED_DATE_BASED_API");
        f.setLabel("PCMWPMROUNDDEFINITIONPM_PERFORMED_DATE_BASED: Performed Date Based");
        f.setDefaultNotVisible();

        f = itemblk0.addField("ROLE_CODE");
        f.setSize(8);
        f.setDynamicLOV("ROLE_TO_SITE_LOV","ITEM0_CONTRACT CONTRACT",600,445); 
        f.setLabel("PCMWPMROUNDDEFINITIONROLE_CODE: Craft");
        f.setUpperCase();
        f.setDefaultNotVisible();

        f = itemblk0.addField("PLAN_MEN","Number");
        f.setSize(14);
        f.setLabel("PCMWPMROUNDDEFINITIONPLAN_MEN: Planned Men");
        f.setDefaultNotVisible();

        f = itemblk0.addField("PLAN_HRS","Number");
        f.setSize(16);
        f.setLabel("PCMWPMROUNDDEFINITIONPLAN_HRS: Planned Hours");
        f.setDefaultNotVisible();

        f = itemblk0.addField("START_VALUE");
        f.setSize(10);
        f.setLabel("PCMWPMROUNDDEFINITIONSTART_VALUE: Start Value");
        f.setUpperCase();
        f.setCustomValidation("START_VALUE,START_DATE,PM_START_UNIT","START_VALUE_TEMP");
        f.setDefaultNotVisible();

        f = itemblk0.addField("START_DATE","Date");
        f.setHidden();

        f = itemblk0.addField("START_DATE_TEMP","Date");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("PM_START_UNIT");
        f.setSize(12);
        f.setSelectBox();
        f.enumerateValues("PM_START_UNIT_API");
        f.setCustomValidation("START_VALUE,START_DATE,PM_START_UNIT","START_VALUE_TEMP");
        f.setLabel("PCMWPMROUNDDEFINITIONPM_START_UNIT: Start Unit");
        f.setDefaultNotVisible();

        f = itemblk0.addField("INTERVAL");
        f.setSize(9);
        f.setLabel("PCMWPMROUNDDEFINITIONINTERVAL: Interval");
        f.setDefaultNotVisible();

        f = itemblk0.addField("PM_INTERVAL_UNIT");
        f.setSize(12);
        f.setSelectBox();
        f.enumerateValues("PM_INTERVAL_UNIT_API");
        f.setLabel("PCMWPMROUNDDEFINITIONPM_INTERVAL_UNIT: Interval Unit");
        f.setDefaultNotVisible();

        f = itemblk0.addField("CALL_CODE");
        f.setSize(9);
        f.setDynamicLOV("MAINTENANCE_EVENT",600,445); 
        f.setLabel("PCMWPMROUNDDEFINITIONCALL_CODE: Event");
        f.setUpperCase();
        f.setDefaultNotVisible();

        f = itemblk0.addField("START_CALL","Number");
        f.setSize(12);
        f.setLabel("PCMWPMROUNDDEFINITIONSTART_CALL: Start Event");
        f.setDefaultNotVisible();

        f = itemblk0.addField("CALL_INTERVAL","Number");
        f.setSize(15);
        f.setLabel("PCMWPMROUNDDEFINITIONCALL_INTERVAL: Event Interval");
        f.setDefaultNotVisible();

        //======================== Valid ====================

        f = itemblk0.addField("VALID_FROM","Date");
        f.setDefaultNotVisible();
        f.setLabel("PCMWPMROUNDDEFVALIDFROM: Valid From");

        f = itemblk0.addField("VALID_TO","Date");
        f.setDefaultNotVisible();
        f.setLabel("PCMWPMROUNDDEFVALIDTO: Valid To");

        f = itemblk0.addField("CALENDAR_ID");
        f.setUpperCase();
        f.setLabel("PCMWPMROUNDDEFCALENDAR: Calendar");
        f.setSize(15);
        f.setDefaultNotVisible();
        f.setMaxLength(10);
        f.setDynamicLOV("WORK_TIME_CALENDAR",600,445);
        f.setLOVProperty("WHERE","OBJSTATE = 'Generated'");

        f = itemblk0.addField("CALENDAR_DESC");
        f.setDefaultNotVisible();
        f.setFunction("Work_Time_Calendar_API.Get_Description(:CALENDAR_ID)");
        f.setSize(20);
        mgr.getASPField("CALENDAR_ID").setValidation("CALENDAR_DESC");

        f = itemblk0.addField("ITEM0_DESCRIPTION");
        f.setSize(22);
        f.setLabel("PCMWPMROUNDDEFINITIONITEM0_DESCRIPTION: Work Description");
        f.setDbName("DESCRIPTION");
        f.setDefaultNotVisible();

        f = itemblk0.addField("VENDOR_NO");
        f.setSize(11);
        f.setDynamicLOV("SUPPLIER_INFO",600,445); 
        f.setLabel("PCMWPMROUNDDEFINITIONVENDOR_NO: Contractor");
        f.setUpperCase();
        f.setDefaultNotVisible();

        f = itemblk0.addField("VENDORNAME");
        f.setSize(20);
        f.setLabel("PCMWPMROUNDDEFINITIONVENDORNAME: Contractor Name");
        f.setReadOnly();
        f.setFunction("Maintenance_Supplier_API.Get_Description(:VENDOR_NO)");
        f.setDefaultNotVisible();
        mgr.getASPField("VENDOR_NO").setValidation("VENDORNAME");

        f = itemblk0.addField("MATERIALS");
        f.setSize(22);
        f.setLabel("PCMWPMROUNDDEFINITIONMATERIALS: Material");
        f.setMaxLength(2000);
        f.setDefaultNotVisible();

        f = itemblk0.addField("TESTNUMBER");
        f.setSize(13);
        f.setLabel("PCMWPMROUNDDEFINITIONTESTNUMBER: Test Number");
        f.setDefaultNotVisible();

        f = itemblk0.addField("ALTERNATE_DESIGNATION");
        f.setSize(22);
        f.setLabel("PCMWPMROUNDDEFINITIONALTERNATE_DESIGNATION: Alternate Designation");
        f.setMaxLength(2000);
        f.setDefaultNotVisible();

        f = itemblk0.addField("LAST_CHANGED","Date");
        f.setSize(14);
        f.setReadOnly();
        f.setLabel("PCMWPMROUNDDEFINITIONLAST_CHANGED: Last Modified");
        f.setDefaultNotVisible();

        f = itemblk0.addField("LATEST_PM","Date");
        f.setSize(15);
        f.setReadOnly();
        f.setLabel("PCMWPMROUNDDEFINITIONLATEST_PM: Last Performed");
        f.setDefaultNotVisible();

        f = itemblk0.addField("ITEM0_ROUNDDEF_ID");
        f.setSize(9);
        f.setMandatory();
        f.setHidden();
        f.setLabel("PCMWPMROUNDDEFINITIONITEM0_ROUNDDEF_ID: Route ID");
        f.setDbName("ROUNDDEF_ID");
        f.setUpperCase();

        f = itemblk0.addField("CLIENT_START");
        f.setFunction("Pm_Start_Unit_API.Get_Client_Value(0)");
        f.setHidden();

        f = itemblk0.addField("CLIENT_START1");
        f.setFunction("Pm_Start_Unit_API.Get_Client_Value(1)");
        f.setHidden();

        f = itemblk0.addField("CLIENT_START2");
        f.setFunction("Pm_Start_Unit_API.Get_Client_Value(2)");
        f.setHidden();

        f = itemblk0.addField("ITEM3_TEMP");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("ITEM2_TEMP");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("START_VALUE_TEMP","Date");
        f.setHidden();
        f.setFunction("''");

        f = itemblk0.addField("INDEX1");
        f.setFunction("''");
        f.setHidden();

        f = itemblk0.addField("NUMDUMMY","Number");
        f.setFunction("0");
        f.setHidden();

        f = itemblk0.addField("CAN_REPLACE"); 
        f.setHidden();  
        f.setFunction("Pm_Action_API.Can_Add_Replacements(:PM_NO,:PM_REVISION)");

        f = itemblk0.addField("STRDUMMY");
        f.setFunction("''");  
        f.setHidden(); 

	//Bug 89752, Start
	f = itemblk0.addField("ACTIVE_REV"); 
	f.setHidden();  
	f.setFunction("''");
	//Bug 89752, End

        itemblk0.setView("PM_ACTION");
        itemblk0.defineCommand("PM_ACTION_API","New__,Modify__,Remove__,ACTIVE__,OBSOLETE__");
        itemblk0.setMasterBlock(headblk);

        itemset0 = itemblk0.getASPRowSet();

        itembar0 = mgr.newASPCommandBar(itemblk0);

        itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
        itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");
        itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
        itembar0.defineCommand(itembar0.SAVERETURN,"saveReturnITEM0","checkItem0Fields(-1)");
        itembar0.defineCommand(itembar0.SAVENEW,"saveNewITEM0","checkItem0Fields(-1)");

        itembar0.addCustomCommand("prepareRound", mgr.translate("PCMWPMROUNDDEFINITIONPRE: Prepare..."));
        itembar0.addCustomCommand("reNoRoutePM", mgr.translate("PCMWPMROUNDDEFINITIONRENO: Re-Number Route PM Action..."));
        itembar0.addCustomCommand("replacements", mgr.translate("PCMWPMROUNDDEFINITIONREP: Replacements..."));
        itembar0.addCustomCommandSeparator();
        itembar0.addCustomCommand("activatePm",mgr.translate("PCMWPMRNDDEFACTIVE: Active"));
        itembar0.addCustomCommand("obsoletePm",mgr.translate("PCMWPMRNDDEFOBSOLETE: Obsolete"));

        itembar0.addCommandValidConditions("activatePm", "OBJSTATE", "Enable", "Preliminary");
        itembar0.addCommandValidConditions("obsoletePm","OBJSTATE",  "Enable", "Active");
        //Bug 67948, Start
        itembar0.addCommandValidConditions("replacements", "OBJSTATE",    "Disable", "Obsolete");
        //Bug 67948, End

        // 040226  ARWILK  Begin  (Enable Multirow RMB actions)
        itembar0.enableMultirowAction();

        itembar0.removeFromMultirowAction("reNoRoutePM");
        itembar0.removeFromMultirowAction("activatePm");
        // 040226  ARWILK  End  (Enable Multirow RMB actions)

        itembar0.addCustomCommandGroup("PMSTATUS", mgr.translate("PCMWPMRNDDEFNPMSTATUS: PM Action Status"));
        itembar0.setCustomCommandGroup("activatePm", "PMSTATUS");
        itembar0.setCustomCommandGroup("obsoletePm", "PMSTATUS");

        itemtbl0 = mgr.newASPTable(itemblk0);
        itemtbl0.setTitle(mgr.translate("PCMWPMROUNDDEFINITIONITM0: Details"));
        itemtbl0.setWrap();
        // 040226  ARWILK  Begin  (Enable Multirow RMB actions)
        itemtbl0.enableRowSelect();
        // 040226  ARWILK  End  (Enable Multirow RMB actions)

        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
        itemlay0.setDialogColumns(2);

        itemlay0.setSimple("ACTIONDESCR");
        itemlay0.setSimple("CALENDAR_DESC");

        //------------------------------------------------------------------------------------------------------------
        //------------------------------------------------------------------------------------------------------------
        //------------------------------------------------------------------------------------------------------------

        itemblk1 = mgr.newASPBlock("ITEM1");

        f = itemblk1.addField("ITEM1_PM_TYPE");
        f.setHidden();
        f.setFunction("Pm_Type_API.Get_Client_Value(1)");

        itemblk1.setView("DUAL");
        itemset1 = itemblk1.getASPRowSet();

        //------------------------------------------------------------------------------------------------------------
        //------------------------------------------------------------------------------------------------------------
        //------------------------------------------------------------------------------------------------------------

        b = mgr.newASPBlock("PM_GENERATEABLE");

        b.addField("CLIENT_VALUES0");

        b = mgr.newASPBlock("PM_PERFORMED_DATE_BASED");

        b.addField("CLIENT_VALUES1");

        b = mgr.newASPBlock("PM_START_UNIT");

        b.addField("CLIENT_VALUES2");

        b = mgr.newASPBlock("PM_INTERVAL_UNIT");

        b.addField("CLIENT_VALUES3");
    }

    public void securityChk()
    {
        ASPManager mgr = getASPManager();

        if (!varSec)
        {
            trans.clear();

            trans.addSecurityQuery("PM_ACTION,PRE_ACCOUNTING");
            trans.addPresentationObjectQuery("PCMW/PmActionRound.page,PCMW/Replacements.page,PCMW/ChangePMActionDlg.page,MPCCOW/PreAccountingDlg.page");

            trans = mgr.perform(trans);

            SecBuff = trans.getSecurityInfo();

            if (SecBuff.itemExists("PM_ACTION") && SecBuff.namedItemExists("PCMW/PmActionRound.page"))
                secPrep = true;
            if (SecBuff.namedItemExists("PCMW/ChangePMActionDlg.page"))
                secReNum = true;
            if (SecBuff.itemExists("PM_ACTION") && SecBuff.namedItemExists("PCMW/Replacements.page"))
                secRep = true;
            if (SecBuff.itemExists("PRE_ACCOUNTING") && SecBuff.namedItemExists("MPCCOW/PreAccountingDlg.page"))
                secPrePost = true;
            varSec = true;
        }
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

        if (itemlay0.isNewLayout())
        {
            headbar.disableCommand(headbar.FIND);
            headbar.disableCommand(headbar.BACK);
        }

        itembar0.enableCommand(itembar0.FIND);

        if (!secPrep)
            itembar0.removeCustomCommand("prepareRound");
        if (!secReNum)
            itembar0.removeCustomCommand("reNoRoutePM");
        if (!secRep)
            itembar0.removeCustomCommand("replacements");
        if (!secPrePost)
            headbar.removeCustomCommand("preposting");
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
        return "PCMWPMROUNDDEFINITIONPAFRL: PM Actions for Route List";
    }

    protected String getTitle()
    {
        return "PCMWPMROUNDDEFINITIONPAFRL: PM Actions for Route List";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.show());

        if (itemlay0.isVisible())
            appendToHTML(itemlay0.show());

        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

        // 040226  ARWILK  Begin  (Remove uneccessary global variables)
        if (bOpenNewWindow)
        {
            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString)); 	//XSS_Safe AMNILK 20070725
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=900,height=600\"); \n");      
        }
        // 040226  ARWILK  End  (Remove uneccessary global variables)

        if (bSetObsolete)
        {
            appendDirtyJavaScript("alert('" + sMsgTxt + "'); \n");
            appendDirtyJavaScript(" commandSet('','');\n");                   
        }
    }
}                                                                               
