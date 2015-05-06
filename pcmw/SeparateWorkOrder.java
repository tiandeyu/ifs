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
*  File        : SeparateWorkOrder.java 
*  Created     : ARWILK  010219  Created.
*  Modified    :
*  JEWILK  010403  Changed file extensions from .asp to .page
*  ARWILK  010502  Disabled some RMB's if the WO_STATUS is finished.
*  CHCRLK  010613  Modified overwritten validations.  
*  BUNILK  010928  Added security check for Actions. 
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  THWILK  031024  Call ID 104789 - Added an extra parameter(#)to the deffinition of the field WO_NO.
*  JEWILK  031110  Changed the display format mask of WO No. Call 110461.
*  ARWILK  031219  Edge Developments - (Removed clone and doReset Methods)
*  VAGULK  040119  Made the field order according to the order in Centura application(Web Alignment).
* -------------------------------- Edge - SP1 Merge -------------------------
*  SHAFLK  040120  Bug Id 41815,Removed Java dependencies.
*  THWILK  040324  Merge with SP1.
*  ARWILK  040723  Changed the label Department to Maintenance Organization. (IID AMEC500C: Resource Planning)
*  SHAFLK  050228  Bug Id 49810,added Object Id to Item block.
*  Chanlk  050314  Merged bug 49810.
*  SHAFLK  050224  Bug Id 49624,Modified method prepareRmb().
*  NIJALK  050624  Merged bug 49624.
*  SHAFLK  050919  Bug 52880, Modified preDefine().
*  NIJALK  051004  Merged bug 52880.
*  NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile).
* ----------------------------------------------------------------------------
*  AMNILK  060728  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMDILK  060807  Merged with Bug Id 58214
*  AMDILK  060816  Bug 58216, Eliminated SQL errors in web applications. Modified methods okFindITEM(), okFindITEMalert(), 
*                  countFindITEM(), nextLevel(), previousLevel()
*  AMDILK  060904  Merged with the Bug Id 58216
*  AMDILK  070801  Removed the scroll buttons of the parent when the detail block is in new or edit modes
*  ASSALK  080506  bUG 72214, Winglet merge.
*  SHAFLK  090907  Bug 85718, Modified okFindITEM() and okFindITEMalert().
*  100827  SHAFLK  Bug 92599, Modified RMB functions.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class SeparateWorkOrder extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.SeparateWorkOrder");

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
    private ASPField f;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private String strCurItem;
    private int i;
    private String strHeadList;
    private String strPrevWo;
    private ASPTransactionBuffer trans;
    private boolean backbutton;
    private boolean initialSearch;
    private boolean existInHead;
    private boolean parentexist;
    private String val;
    private ASPCommand cmd;
    private String tpDoExist;
    private String site;
    private String directive;
    private String startDate;
    private String endDate;
    private String orgCode;
    private String hasStructure;
    private String historical;
    private String state;
    private ASPQuery q;
    private ASPBuffer data;
    private int currrow;
    private String woNo;
    private ASPBuffer buffer;
    private String calling_url;
    private String errDescr;
    private String contract;
    private String connWoNo;
    private int totalHeadRows;
    private String strWhereCondition;
    private boolean exists;
    private int newTotalHeadRows;
    private String newWoNo;
    private String parentWoNo;
    private boolean definedflag;
    private String date_fmt;
    private int tpParent;
    private String txt;  
    private ASPBuffer row;
    private boolean actEna1;
    private boolean actEna2;
    private boolean again;
    private boolean bOpenNewWindow;
    private String urlString;
    private String newWinHandle;
    private int newWinHeight = 600;
    private int newWinWidth = 900;

    //===============================================================
    // Construction 
    //===============================================================
    public SeparateWorkOrder(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        strCurItem =  "";
        i =  0;
        strHeadList =  "";
        strPrevWo =  "";
        ASPManager mgr = getASPManager();   

        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();

        backbutton = ctx.readFlag("BACKBUT",false);
        initialSearch = ctx.readFlag("INITIAL",true);
        existInHead = ctx.readFlag("INHEAD",false);
        strHeadList = ctx.readValue("HEADLIST",strHeadList);
        strCurItem =ctx.readValue("CURITEM",strCurItem);
        strPrevWo =ctx.readValue("PREVWO",strPrevWo );
        parentexist = ctx.readFlag("PARENTEXIST",false); 
        date_fmt = mgr.getFormatMask("Date",true);   
        actEna1 = ctx.readFlag("ACTENA1",false); 
        actEna2 = ctx.readFlag("ACTENA2",false); 
        again = ctx.readFlag("AGAIN",false);

        if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();
        else if (mgr.commandBarActivated())
        {
            eval( mgr.commandBarFunction() );
        }
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (mgr.dataTransfered())
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WO_NO")))
        {
            okFind();
            backbutton = true;
        }

        checkObjAvaileble();
        adjust();

        ctx.writeFlag("BACKBUT",backbutton);
        ctx.writeFlag("INITIAL",initialSearch);
        ctx.writeValue("HEADLIST",strHeadList);
        ctx.writeValue("PREVWO",strPrevWo );
        ctx.writeFlag("INHEAD",existInHead);
        ctx.writeFlag("PARENTEXIST",parentexist);
        ctx.writeValue("CURITEM",strCurItem);
        ctx.writeFlag("ACTENA1",actEna1);
        ctx.writeFlag("ACTENA2",actEna2);
        ctx.writeFlag("AGAIN",again);

    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void  validate()
    {
        ASPManager mgr = getASPManager();

        val = mgr.readValue("VALIDATE");

        if ("WO_NO".equals(val))
        {
            cmd = trans.addCustomFunction("DOEXIST","Historical_Separate_API.Do_Exist","ISHISTORICAL");
            cmd.addParameter("WO_NO");

            cmd = trans.addCustomFunction("GETPARENT","WORK_ORDER_CONNECTION_API.GET_PARENT","PARENTWONO");
            cmd.addParameter("WO_NO");

            trans = mgr.validate(trans);

            tpDoExist = trans.getValue("DOEXIST/DATA/ISHISTORICAL");
            tpParent = toInt(trans.getValue("GETPARENT/DATA/PARENTWONO"));

            txt = (mgr.isEmpty(tpDoExist ) ? "" :tpDoExist ) + "^" + tpParent + "^";
            mgr.responseWrite(txt);
        }

        if ("CONNECTED_WO_NO".equals(val))
        {
            cmd = trans.addCustomFunction("SITE","ACTIVE_SEPARATE_API.Get_Contract","ITEM_CONTRACT");
            cmd.addParameter("CONNECTED_WO_NO");

            cmd = trans.addCustomFunction("MCH","ACTIVE_SEPARATE_API.Get_Mch_Code","ITEM_MCH_CODE");
            cmd.addParameter("CONNECTED_WO_NO");

            cmd = trans.addCustomFunction("ERRDESC","SEPARATE_WORK_ORDER_API.Get_Err_Descr","ERRDESCR");
            cmd.addParameter("CONNECTED_WO_NO");

            cmd = trans.addCustomFunction("STDATE","SEPARATE_WORK_ORDER_API.Get_Plan_S_Date","PLANSDATE");
            cmd.addParameter("CONNECTED_WO_NO");

            cmd = trans.addCustomFunction("FNDATE","SEPARATE_WORK_ORDER_API.Get_Plan_F_Date","PLANFDATE");
            cmd.addParameter("CONNECTED_WO_NO");

            cmd = trans.addCustomFunction("ORGCODE","SEPARATE_WORK_ORDER_API.Get_Org_Code","ORGCODE");
            cmd.addParameter("CONNECTED_WO_NO");

            cmd = trans.addCustomFunction("CONDOWN","Work_Order_Connection_API.Has_Connection_Down","HASSTRUCTURE");
            cmd.addParameter("CONNECTED_WO_NO");

            cmd = trans.addCustomFunction("DOEXIST","Historical_Separate_API.Do_Exist","ITEM_ISHISTORICAL");
            cmd.addParameter("CONNECTED_WO_NO");

            cmd = trans.addCustomFunction("STATE","SEPARATE_WORK_ORDER_API.Get_State","WOSTATUSCODE");
            cmd.addParameter("CONNECTED_WO_NO");     

            trans = mgr.validate(trans);

            site = trans.getValue("SITE/DATA/ITEM_CONTRACT");
            String mch = trans.getValue("MCH/DATA/ITEM_MCH_CODE");
            directive = trans.getValue("ERRDESC/DATA/ERRDESCR");
            startDate = trans.getBuffer("STDATE/DATA").getFieldValue("PLANSDATE");
            endDate = trans.getBuffer("FNDATE/DATA").getFieldValue("PLANFDATE");
            orgCode = trans.getValue("ORGCODE/DATA/ORGCODE");
            hasStructure = trans.getValue("CONDOWN/DATA/HASSTRUCTURE");
            historical = trans.getValue("DOEXIST/DATA/ITEM_ISHISTORICAL");
            state = trans.getValue("STATE/DATA/WOSTATUSCODE");

            txt = (mgr.isEmpty(site) ? "" : (site ))+ "^" + (mgr.isEmpty(mch) ? "" : (mch))+ "^" + (mgr.isEmpty(directive) ? "" : (directive))+ "^" + (mgr.isEmpty(startDate) ? "" : (startDate))+ "^"
                  + (mgr.isEmpty(endDate) ? "" : (endDate))+ "^" + (mgr.isEmpty(orgCode) ? "" : (orgCode))+ "^"
                  + (mgr.isEmpty(hasStructure) ? "" : (hasStructure))+ "^" + (mgr.isEmpty(historical) ? "" : (historical))+ "^"
                  + (mgr.isEmpty(state) ? "" : (state));
            mgr.responseWrite(txt);
        }

        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void  okFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.includeMeta("ALL");

        if (mgr.dataTransfered())
            q.addOrCondition(mgr.getTransferedData());

        mgr.submit(trans);

        eval(headset.syncItemSets());

        if (headset.countRows() == 1)
        {
            okFindITEM();   
        }

        headlay.setLayoutMode(headlay.SINGLE_LAYOUT);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWSEPARATEWORKORDERNODATA: No data found."));
            headset.clear();
            headlay.setLayoutMode(headlay.MULTIROW_LAYOUT);            
        }
    }


    public void  countFind()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(headblk);
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        headlay.setCountValue(toInt(headset.getRow().getValue("N")));
        headset.clear();
    }


    public void  newRow()
    {
        ASPManager mgr = getASPManager();

        cmd = trans.addEmptyCommand("HEAD","SEPARATE_WORK_ORDER_API.New__",headblk);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);
        data = trans.getBuffer("HEAD/DATA");
        headset.addRow(data);
    }

//-----------------------------------------------------------------------------
//----------------------  CMDBAR EDIT-IT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void  okFindITEM()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        currrow = headset.getCurrentRowNo();
        q = trans.addQuery(itemblk0);
        q.addWhereCondition("WO_NO = ?");
	q.addParameter("WO_NO", headset.getRow().getValue("WO_NO"));
        q.includeMeta("ALL");
        mgr.querySubmit(trans, itemblk0);
        headset.goTo(currrow);
    }


    public void  okFindITEMalert()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        currrow = headset.getCurrentRowNo();
        q = trans.addQuery(itemblk0);
        q.addWhereCondition("WO_NO = ?");
	q.addParameter("WO_NO", headset.getRow().getValue("WO_NO"));
        q.includeMeta("ALL");
        mgr.querySubmit(trans, itemblk0);
        if (itemset0.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWSEPARATEWORKORDERNODATAFOUND: No data found."));
            itemset0.clear();
        }
        headset.goTo(currrow);
    }

    public void  countFindITEM()
    {
        ASPManager mgr = getASPManager();

        q = trans.addQuery(itemblk0);
        q.setSelectList("to_char(count(*)) N");
        q.addWhereCondition("WO_NO = ?");
	q.addParameter("WO_NO", headset.getRow().getValue("WO_NO"));
        mgr.submit(trans);
        itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
        itemset0.clear();
    }

    public void  newRowITEM()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        cmd = trans.addEmptyCommand("ITEM","WORK_ORDER_CONNECTION_API.New__",itemblk0);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);
        data = trans.getBuffer("ITEM/DATA");
        data.setFieldItem("ITEM_WO_NO",headset.getValue("WO_NO"));
        itemset0.addRow(data);
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void  none()
    {

    }


    public void  prepareRmbMas()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        if ("TRUE".equals(headset.getRow().getValue("HIST_EXIST")))
            mgr.showAlert("CANSEPWOORD: Cannot perform on the selected record.");
        else{
            bOpenNewWindow = true;
            urlString = "ActiveSeparate2.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO"));
            newWinHandle = "PrepareWo"; 
        }
    }


    public void  reportInRmbMas()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        if ("TRUE".equals(headset.getRow().getValue("HIST_EXIST")))
            mgr.showAlert("CANSEPWOORD: Cannot perform on the selected record.");
        else{
            bOpenNewWindow = true;
            urlString = "ActiveSeperateReportInWorkOrder.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO"));
            newWinHandle = "ReportInWo"; 
        }

    }


    public void  createWO()
    {
        ASPManager mgr = getASPManager();  

        calling_url = mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url);

        if (headlay.isMultirowLayout())
            headset.goTo(headset.getRowSelected());
        else
            headset.selectRow();

        if ("TRUE".equals(headset.getRow().getValue("HIST_EXIST")))
            mgr.showAlert("CANSEPWOORD: Cannot perform on the selected record.");

        else{
            bOpenNewWindow = true;
            urlString = "CreateWoDlg.page?WO_NO=" + mgr.URLEncode(headset.getRow().getValue("WO_NO")) +
                        "&ERR_DESCR=" + mgr.URLEncode(headset.getRow().getValue("ERR_DESCR")) +
                        "&CONTRACT=" + mgr.URLEncode(headset.getRow().getValue("CONTRACT"))+ "&FROMFORM=NOTSTRUC";
            newWinHandle = "CreateWo"; 
        }

    }


    public void  prepareRmb()
    {
        ASPManager mgr = getASPManager();

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());
        else
            itemset0.selectRow();

        if ("TRUE".equals(itemset0.getRow().getValue("ITEM_ISHISTORICAL")))
            mgr.showAlert("CANSEPWOORD: Cannot perform on the selected record.");
        else{
            bOpenNewWindow = true;
            urlString = "ActiveSeparate2.page?WO_NO=" + mgr.URLEncode(itemset0.getRow().getValue("CONNECTED_WO_NO"));
            newWinHandle = "PrepareWo"; 
        }
    }


    public void  reportInRmb()
    {
        ASPManager mgr = getASPManager();

        if (itemlay0.isMultirowLayout())
            itemset0.goTo(itemset0.getRowSelected());
        else
            itemset0.selectRow();

        buffer = mgr.newASPBuffer();
        row = buffer.addBuffer("0");
        row.addItem("WO_NO",connWoNo);

        if ("TRUE".equals(itemset0.getRow().getValue("ITEM_ISHISTORICAL")))
            mgr.showAlert("CANSEPWOORD: Cannot perform on the selected record.");
        else{
            bOpenNewWindow = true;
            urlString = "ActiveSeperateReportInWorkOrder.page?WO_NO=" + mgr.URLEncode(itemset0.getRow().getValue("CONNECTED_WO_NO"));
            newWinHandle = "ReportInWo"; 
        }
    }


    public void  nextLevel()
    {
        ASPManager mgr = getASPManager();
        ASPBuffer tempBuff = mgr.newASPBuffer();
	String strConnWoNo = "";

        totalHeadRows = headset.countRows();

        if (itemlay0.isMultirowLayout())
        {
            itemset0.storeSelections();
            itemset0.setFilterOn();
            connWoNo = itemset0.getRow().getValue("CONNECTED_WO_NO");  
	    strConnWoNo = "?";
            tempBuff.addItem("CONNECTED_WO_NO",itemset0.getRow().getValue("CONNECTED_WO_NO"));
	    itemset0.setFilterOff();
        }
	else {
            connWoNo   = itemset0.getValue("CONNECTED_WO_NO"); 
            strConnWoNo = "?";
            tempBuff.addItem("CONNECTED_WO_NO",itemset0.getValue("CONNECTED_WO_NO"));
	}

        strWhereCondition ="WO_NO IN (";
        strHeadList = "";
        exists = false;

        headset.first();
        ASPBuffer buff = headset.getRows("WO_NO");
	for (i=1; i <= totalHeadRows-1 ; i++)
        {
	    woNo = headset.getRow().getValue("WO_NO");
	    strHeadList += "?,";
            
            if (connWoNo.equals(woNo))
                exists = true;

            headset.next();
        }

        if (!exists)
        {
	    
	    headset.clear();
            itemset0.clear();  
            trans.clear();

            if (totalHeadRows > 1) {
		strWhereCondition = strWhereCondition +  strHeadList + strConnWoNo + ")";
		q = trans.addEmptyQuery(headblk);  
                q.addWhereCondition(strWhereCondition);

		for (int i = 0; i < buff.countItems(); i++)
		{
		     ASPBuffer temp = buff.getBufferAt(i);
		     q.addParameter("WO_NO",temp.getValueAt(0));
		}
		for (int i = 0; i < tempBuff.countItems(); i++)
                 q.addParameter(tempBuff.getNameAt(i),tempBuff.getValueAt(i));

	    }
	    else {
		strWhereCondition = strWhereCondition + strConnWoNo + ")";
		q = trans.addEmptyQuery(headblk);  
                q.addWhereCondition(strWhereCondition);
		for (int i = 0; i < tempBuff.countItems(); i++)
                 q.addParameter(tempBuff.getNameAt(i),tempBuff.getValueAt(i));
	    }
   

            mgr.submit(trans);   
        }

        newTotalHeadRows = headset.countRows();
        headset.first();
        for (i=0; i<=newTotalHeadRows; i++)
        {
            newWoNo = headset.getRow().getValue("WO_NO");

            if (connWoNo.equals(newWoNo))
            {
                break;
            }
            headset.next();
        }

        headset.goTo(i);
        trans.clear();
        okFindITEM();
    }


    public void  previousLevel()
    {
        ASPManager mgr = getASPManager();
        ASPBuffer tempBuff = mgr.newASPBuffer();
	String strParentWoNo = "";

        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            headset.setFilterOn();
            parentWoNo = headset.getRow().getValue("PARENTWONO");  
            woNo = headset.getRow().getValue("WO_NO"); 
	    strParentWoNo = "?";
            tempBuff.addItem("PARENTWONO",headset.getRow().getValue("PARENTWONO"));
	    headset.setFilterOff();
        }
	else {
            parentWoNo = headset.getValue("PARENTWONO");  
            strParentWoNo = "?";
            tempBuff.addItem("PARENTWONO",headset.getValue("PARENTWONO"));
	}


        if (mgr.isEmpty(parentWoNo))
            mgr.showAlert(mgr.translate("PCMWSEPARATEWORKORDERPARENTREC: No parent record exists for current record."));
        else
        {
            strWhereCondition ="WO_NO IN (";
            strHeadList = "";
            exists = false; 
            totalHeadRows = headset.countRows();
	    ASPBuffer buff = headset.getRows("WO_NO");
            headset.first();
            for (i=1; i<=(totalHeadRows-1) ; i++)
            {
                woNo = headset.getRow().getValue("WO_NO");
		strHeadList += "?,";
                
                if (parentWoNo.equals(woNo))
                    exists = true;

                headset.next();
            }

            headset.clear();
            itemset0.clear();  
            trans.clear();

            if (!exists)
            {

		if (totalHeadRows > 1) {
		    strWhereCondition = strWhereCondition +  strHeadList + strParentWoNo + ")";
		    q = trans.addEmptyQuery(headblk);  
		    q.addWhereCondition(strWhereCondition);
    
		    for (int i = 0; i < buff.countItems(); i++)
		    {
			 ASPBuffer temp = buff.getBufferAt(i);
			 q.addParameter("WO_NO",temp.getValueAt(0));
		    }
		    for (int i = 0; i < tempBuff.countItems(); i++)
		     q.addParameter(tempBuff.getNameAt(i),tempBuff.getValueAt(i));
    
		}
		else {
		    strWhereCondition = strWhereCondition + strParentWoNo + ")";
		    q = trans.addEmptyQuery(headblk);  
		    q.addWhereCondition(strWhereCondition);
		    for (int i = 0; i < tempBuff.countItems(); i++)
		     q.addParameter(tempBuff.getNameAt(i),tempBuff.getValueAt(i));
		}

                mgr.submit(trans);   
            }

            newTotalHeadRows = headset.countRows();
            headset.first();
            for (i=0; i<=newTotalHeadRows; i++)
            {
                newWoNo = headset.getRow().getValue("WO_NO");

                if (parentWoNo.equals(newWoNo))
                {
                    break;
                }
                headset.next();
            }

            headset.goTo(i);
            trans.clear();
            okFindITEM();
        }
    }


    public void  preDefine()
    {
        ASPManager mgr = getASPManager();


        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("ISHISTORICAL");
        f.setSize(10);
        f.setHidden();
        f.setFunction("Historical_Separate_API.Do_Exist(:WO_NO)");
        f.setReadOnly();

        f = headblk.addField("CONTRACT");
        f.setSize(14);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setLabel("PCMWSEPARATEWORKORDERCONTRACT: Site");
        f.setUpperCase();
        f.setReadOnly();
        f.setMaxLength(5);
        f.setHilite();

        f = headblk.addField("STATE");
        f.setSize(14);
        f.setLabel("PCMWSEPARATEWORKORDERSTATE: Status");
        f.setReadOnly();
        f.setMaxLength(253);
        f.setHilite();

        f = headblk.addField("WO_NO","Number","#");
        f.setSize(14);
        f.setLOV("ActiveSeparateLov.page",600,445);
        f.setCustomValidation("WO_NO","ISHISTORICAL,PARENTWONO");
        f.setMandatory();
        f.setLabel("PCMWSEPARATEWORKORDERWONO: WO No");   
        f.setReadOnly();    
        f.setHyperlink("SeparateWorkOrderRedirect.page","WO_NO,ISHISTORICAL","NEWWIN");  
        f.setHilite();

        f = headblk.addField("MCH_CODE_CONTRACT");
        f.setHidden();

        f = headblk.addField("MCH_CODE");
        f.setSize(20);
        f.setDynamicLOV("MAINTENANCE_OBJECT","CONTRACT",600,450);
        f.setLabel("PCMWSEPARATEWORKORDERMCHCODE: Object ID");
        f.setHyperlink("../equipw/EquipmentAllObjectLightRedirect.page","MCH_CODE,MCH_CODE_CONTRACT,WO_NO","NEWWIN");
        f.setUpperCase();
        f.setHilite();
        f.setMaxLength(100); 
        f.setReadOnly();    

        f = headblk.addField("DESCRIPTION");
        f.setSize(30);
        f.setLabel("PCMWSEPARATEWORKORDEROBJDESC: Object Description");
        f.setDefaultNotVisible();
        f.setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:CONTRACT,:MCH_CODE)");
        f.setReadOnly();
        f.setHilite();
        f.setMaxLength(2000);

        f = headblk.addField("PLAN_S_DATE","Datetime");
        f.setSize(14);
        f.setLabel("PCMWSEPARATEWORKORDERPSDATE: Planned Start");
        f.setReadOnly();

        f = headblk.addField("PLAN_F_DATE","Datetime");
        f.setSize(20);
        f.setLabel("PCMWSEPARATEWORKORDERPFDATE: Planned Completion");
        f.setReadOnly();

        f = headblk.addField("ORG_CODE");
        f.setSize(14);
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,445);
        f.setLabel("PCMWSEPARATEWORKORDERORGCODE: Maintenance Organization");
        f.setUpperCase();
        f.setReadOnly();
        f.setMaxLength(8);
        f.setHilite();

        f = headblk.addField("ERR_DESCR");
        f.setLabel("PCMWSEPARATEWORKORDERERRDESCR: Directive");
        f.setSize(35);
        f.setReadOnly();
        f.setMaxLength(60);
        f.setHilite();

        f = headblk.addField("PARENTWONO","Number","#");
        f.setSize(14);
        f.setLabel("PCMWSEPARATEWORKORDERPARENTWONO: Belongs to");
        f.setFunction("WORK_ORDER_CONNECTION_API.GET_PARENT(:WO_NO)");
        f.setReadOnly();                

        f = headblk.addField("HIST_EXIST","Boolean");
        f.setSize(14);
        f.setHidden();
        f.setFunction("Historical_Separate_API.Do_Exist(:WO_NO)");

        // Bug 72214, start
        f = headblk.addField("SCHEDULING_CONSTRAINTS");
	f.setLabel("PCMWSEPARATEWORKORDERSCHEDCONST: Scheduling Constraints");
	f.setSize(35);
	f.setReadOnly();
	// Bug 72214, end

        headblk.setView("SEPARATE_WORK_ORDER");
        headblk.defineCommand("SEPARATE_WORK_ORDER_API","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWSEPARATEWORKORDERMASTER_TITLE: Structure for Work Order"));
        headtbl.setWrap();

        headbar = mgr.newASPCommandBar(headblk);

        headbar.disableCommand(headbar.NEWROW);
        headbar.disableCommand(headbar.DELETE);
        headbar.disableCommand(headbar.EDITROW);
        headbar.disableCommand(headbar.DUPLICATEROW);

        headbar.addCustomCommand("none",mgr.translate("PCMWSEPARATEWORKORDERNONE: "));
        headbar.addCustomCommand("previousLevel",mgr.translate("PCMWSEPARATEWORKORDERPLMAS: Previous Level"));
        headbar.addCustomCommand("prepareRmbMas",mgr.translate("PCMWSEPARATEWORKORDERPREPMAS: Prepare..."));
        headbar.addCustomCommand("reportInRmbMas",mgr.translate("PCMWSEPARATEWORKORDERREPINMAS: Report In..."));   
        headbar.addCustomCommand("createWO",mgr.translate("PCMWSEPARATEWORKORDERCREATEWO: Create WO..."));

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        headlay.setFieldOrder("WO_NO,PLAN_S_DATE,PLAN_F_DATE,CONTRACT,STATE,ORG_CODE,ERR_DESCR,PARENTWONO,MCH_CODE,DESCRIPTION");

        headlay.setSimple("DESCRIPTION");              


        itemblk0 = mgr.newASPBlock("ITEM");


        f = itemblk0.addField("ITEM_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk0.addField("ITEM_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk0.addField("CONNECTED_WO_NO","Number","#");
        f.setSize(13);
        f.setDynamicLOV("ACTIVE_SEPARATE2",600,445);
        f.setMandatory();
        f.setLabel("PCMWSEPARATEWORKORDERCONWONO: WO No");
        f.setCustomValidation("CONNECTED_WO_NO","ITEM_CONTRACT,ITEM_MCH_CODE,ERRDESCR,PLANSDATE,PLANFDATE,ORGCODE,HASSTRUCTURE,ITEM_ISHISTORICAL,WOSTATUSCODE");
        f.setInsertable();

        f = itemblk0.addField("ITEM_MCH_CODE");
        f.setSize(11);
        f.setLabel("PCMWSEPARATEWORKORDERITEMMCHCODE: Object Id");
        f.setFunction("ACTIVE_SEPARATE_API.Get_Mch_Code(CONNECTED_WO_NO)");
        f.setReadOnly();

        f = itemblk0.addField("ITEM_CONTRACT");
        f.setSize(11);
        f.setLabel("PCMWSEPARATEWORKORDERIT0CON: Site");
        f.setFunction("ACTIVE_SEPARATE_API.Get_Contract(CONNECTED_WO_NO)");
        f.setReadOnly();
        f.setMaxLength(5);


        f = itemblk0.addField("ERRDESCR");
        f.setSize(35);
        f.setLabel("PCMWSEPARATEWORKORDERERRDESCR: Directive");
        f.setFunction("SEPARATE_WORK_ORDER_API.Get_Err_Descr(CONNECTED_WO_NO)");
        f.setReadOnly();
        f.setMaxLength(60);

        f = itemblk0.addField("PLANSDATE","Datetime");
        f.setSize(22);
        f.setLabel("PCMWSEPARATEWORKORDERPLANSDATE: Planned Start");
        f.setFunction("SEPARATE_WORK_ORDER_API.Get_Plan_S_Date(CONNECTED_WO_NO)");
        f.setCustomValidation("PLANSDATE","PLANSDATE");
        f.setReadOnly();

        f = itemblk0.addField("PLANFDATE","Datetime");
        f.setSize(22);
        f.setLabel("PCMWSEPARATEWORKORDERPLANFDATE: Planned Completion");
        f.setFunction("SEPARATE_WORK_ORDER_API.Get_Plan_F_Date(CONNECTED_WO_NO)");
        f.setCustomValidation("PLANFDATE","PLANFDATE");
        f.setReadOnly();

        f = itemblk0.addField("ORGCODE");
        f.setSize(25);
        f.setLabel("PCMWSEPARATEWORKORDERORGNCODE: Maintenance Organization");
        f.setFunction("SEPARATE_WORK_ORDER_API.Get_Org_Code(CONNECTED_WO_NO)");
        f.setReadOnly();
        f.setMaxLength(8);

        f = itemblk0.addField("HASSTRUCTURE");
        f.setSize(12);
        f.setLabel("PCMWSEPARATEWORKORDERHASSTRUCTURE: Has Structure");
        f.setFunction("Work_Order_Connection_API.Has_Connection_Down(CONNECTED_WO_NO)");
        f.setCheckBox("FALSE,TRUE");
        f.setValidateFunction("disableChange");

        f = itemblk0.addField("ITEM_ISHISTORICAL");
        f.setSize(12);
        f.setHidden();
        f.setLabel("PCMWSEPARATEWORKORDERIT0ISHIST: Is Historical");
        f.setFunction("Historical_Separate_API.Do_Exist(CONNECTED_WO_NO)");
        f.setMaxLength(8);

        f = itemblk0.addField("WOSTATUSCODE");
        f.setSize(14);
        f.setLabel("PCMWSEPARATEWORKORDERWOSTATUSCODE: Status");
        f.setFunction("SEPARATE_WORK_ORDER_API.Get_State(CONNECTED_WO_NO)");
        f.setReadOnly();
        f.setMaxLength(253);

        f = itemblk0.addField("ITEM_WO_NO","Number","#");
        f.setSize(13);
        f.setDynamicLOV("ACTIVE_WORK_ORDER",600,445);
        f.setMandatory();
        f.setHidden();
        f.setLabel("PCMWSEPARATEWORKORDERIT0WONO: Parent WO No");
        f.setDbName("WO_NO");

        // Bug 72214, start
        f = itemblk0.addField("ITEM_SCHEDULING_CONSTRAINTS");
        f.setLabel("PCMWSEPARATEWORKORDERIT0SCHEDCONST: Scheduling Constraints");
        f.setFunction("Active_Separate_API.Get_Scheduling_Constraints(:CONNECTED_WO_NO)");
        f.setSize(35);
        f.setReadOnly();
	// Bug 72214, end

        itemblk0.setView("WORK_ORDER_CONNECTION");
        itemblk0.defineCommand("WORK_ORDER_CONNECTION_API","New__,Modify__,Remove__");
        itemblk0.setMasterBlock(headblk);
        itemset0 = itemblk0.getASPRowSet();     

        itembar0 = mgr.newASPCommandBar(itemblk0);

        itembar0.defineCommand(itembar0.OKFIND,"okFindITEMalert");
        itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM");
        itembar0.defineCommand(itembar0.NEWROW,"newRowITEM");
        itembar0.disableCommand(itembar0.DUPLICATEROW);
        itembar0.enableCommand(itembar0.FIND);

        itemtbl0 = mgr.newASPTable(itemblk0);
        itemtbl0.setTitle(mgr.translate("PCMWSEPARATEWORKORDERITM0: Work Order Connection"));
        itemtbl0.setWrap();

        itembar0.addCustomCommand("none",mgr.translate("PCMWSEPARATEWORKORDERNONE: "));
        itembar0.addCustomCommand("nextLevel",mgr.translate("PCMWSEPARATEWORKORDERNEXTLEVEL: Next Level"));
        itembar0.addCustomCommand("previousLevel",mgr.translate("PCMWSEPARATEWORKORDERPREVIOUSLEVEL: Previous Level"));
        itembar0.addCustomCommand("prepareRmb",mgr.translate("PCMWSEPARATEWORKORDERPREPARE: Prepare..."));
        itembar0.addCustomCommand("reportInRmb",mgr.translate("PCMWSEPARATEWORKORDERREPORIN: Report In..."));   
        itembar0.addCustomCommand("createWO",mgr.translate("PCMWSEPARATEWORKORDERCREWO: Create WO..."));

        itembar0.defineCommand(itembar0.SAVERETURN,null,"checkItemFields(-1)");
        itembar0.defineCommand(itembar0.SAVENEW,null,"checkItemFields(-1)");

        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
        itemlay0.setDialogColumns(2);

    }

    public void checkObjAvaileble()
    {
        if (!again)
        {
            ASPManager mgr = getASPManager();

            ASPBuffer availObj;
            trans.clear();
            trans.addSecurityQuery("ACTIVE_SEPARATE");
            trans.addPresentationObjectQuery("PCMW/ActiveSeparate2.page,PCMW/ActiveSeperateReportInWorkOrder.page");

            trans = mgr.perform(trans);

            availObj = trans.getSecurityInfo();

            if (availObj.itemExists("ACTIVE_SEPARATE") && availObj.namedItemExists("PCMW/ActiveSeparate2.page"))
                actEna1 = true;

            if (availObj.itemExists("ACTIVE_SEPARATE") && availObj.namedItemExists("PCMW/ActiveSeperateReportInWorkOrder.page"))
                actEna2 = true;

            again = true;
        }
    }


    public void  adjust()
    {
        ASPManager mgr = getASPManager();

        if (!actEna1)
        {
            headbar.removeCustomCommand("prepareRmbMas");
            itembar0.removeCustomCommand("prepareRmb");
        }
        if (!actEna2)
        {
            headbar.removeCustomCommand("reportInRmbMas");
            itembar0.removeCustomCommand("reportInRmb");
        }

        if (headset.countRows()>0)
            mgr.createSearchURL(headblk);

        if (( headlay.getLayoutMode() == 0 ) ||   definedflag)
        {

            headbar.disableCommand(headbar.NEWROW);
            headbar.disableCommand(headbar.DELETE);
            headbar.disableCommand(headbar.BACK);
            headbar.disableCommand(headbar.BACKWARD);
            headbar.disableCommand(headbar.FORWARD);

            headbar.removeCustomCommand("none");
            headbar.removeCustomCommand("previousLevel"); 
            headbar.removeCustomCommand("prepareRmbMas");
            headbar.removeCustomCommand("reportInRmbMas");
            headbar.removeCustomCommand("createWO");

        }

        if (headlay.isMultirowLayout())
        {
            headbar.removeCustomCommand("previousLevel");
            mgr.getASPField("PARENTWONO").setHyperlink("SeparateWorkOrderRedirect.page","PARENTWONO,","NEWWIN");  
        }
        else
        {
            if (headset.countRows()>0)
            {
                if (!mgr.isEmpty(headset.getValue("PARENTWONO")))
                    mgr.getASPField("PARENTWONO").setHyperlink("SeparateWorkOrderRedirect.page","PARENTWONO,","NEWWIN");

                if ("TRUE".equals(headset.getRow().getValue("HIST_EXIST")))
                {
                    headbar.removeCustomCommand("prepareRmbMas");
                    headbar.removeCustomCommand("reportInRmbMas");
                    headbar.removeCustomCommand("createWO");
                }

            }
        }                        


        if ((!itemlay0.isMultirowLayout())&&(itemset0.countRows()>0))
        {
            if ("TRUE".equals(itemset0.getRow().getValue("ITEM_ISHISTORICAL")))
            {
                itembar0.removeCustomCommand("prepareRmb");
                itembar0.removeCustomCommand("reportInRmb");
                itembar0.removeCustomCommand("createWO");
            }
        }

        headbar.enableCommand(headbar.FAVORITE);
        mgr.createSearchURL(headblk);

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
        return "PCMWSEPARATEWORKORDERMASTERTITLE: Structure for Work Order";
    }

    protected String getTitle()
    {
        return "PCMWSEPARATEWORKORDERMASTERTITLE: Structure for Work Order";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.show());

        if ((headlay.isSingleLayout()) && (headset.countRows() > 0))
            appendToHTML(itemlay0.show());

        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");

        appendDirtyJavaScript("function validateConnectedWoNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if( getRowStatus_('ITEM',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("	setDirty();\n");
        appendDirtyJavaScript("	if( !checkConnectedWoNo(i) ) return;\n");
        appendDirtyJavaScript(" window.status='Please wait for validation';\n");
        appendDirtyJavaScript("	r = __connect(\n");
        appendDirtyJavaScript("		'");
        appendDirtyJavaScript(mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=CONNECTED_WO_NO'\n");
        appendDirtyJavaScript("		+ '&CONNECTED_WO_NO=' + URLClientEncode(getValue_('CONNECTED_WO_NO',i))\n");
        appendDirtyJavaScript("		);\n");
        appendDirtyJavaScript(" window.status='';\n");
        appendDirtyJavaScript("	if( checkStatus_(r,'CONNECTED_WO_NO',i,'WO No') )\n");
        appendDirtyJavaScript("	{\n");
        appendDirtyJavaScript("		assignValue_('ITEM_CONTRACT',i,0);\n");
        appendDirtyJavaScript("		assignValue_('ITEM_MCH_CODE',i,1);\n");
        appendDirtyJavaScript("		assignValue_('ERRDESCR',i,2);\n");
        appendDirtyJavaScript("		assignValue_('PLANSDATE',i,3);\n");
        appendDirtyJavaScript("		assignValue_('PLANFDATE',i,4);\n");
        appendDirtyJavaScript("		assignValue_('ORGCODE',i,5);\n");
        appendDirtyJavaScript("		f.HASSTRUCTURE.value = f.HASSTRUCTURE.defaultValue = __getValidateValue(6);\n");
        appendDirtyJavaScript("       if (__getValidateValue(6)=='TRUE')\n");
        appendDirtyJavaScript("       {\n");
        appendDirtyJavaScript("        	f.HASSTRUCTURE.checked = f.HASSTRUCTURE.defaultChecked = true;\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript("       else\n");
        appendDirtyJavaScript("       {\n");
        appendDirtyJavaScript("        	f.HASSTRUCTURE.checked = f.HASSTRUCTURE.defaultChecked = false;\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript("		assignValue_('ITEM_ISHISTORICAL',i,7);\n");
        appendDirtyJavaScript("		assignValue_('WOSTATUSCODE',i,8);\n");
        appendDirtyJavaScript("	}\n");
        appendDirtyJavaScript("}\n");
        if (bOpenNewWindow)
        {
            appendDirtyJavaScript("  window.open(\"");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(urlString));
            appendDirtyJavaScript("\", \"");
            appendDirtyJavaScript(newWinHandle);
            appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=");      
            appendDirtyJavaScript(newWinWidth);
            appendDirtyJavaScript(",height=");
            appendDirtyJavaScript(newWinHeight);
            appendDirtyJavaScript("\"); \n");  

        }

        appendDirtyJavaScript("function disableChange()\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if (f.HASSTRUCTURE.checked == false)\n");
        appendDirtyJavaScript("		f.HASSTRUCTURE.checked = f.HASSTRUCTURE.defaultChecked = true;\n");
        appendDirtyJavaScript("	else\n");
        appendDirtyJavaScript("		f.HASSTRUCTURE.checked = f.HASSTRUCTURE.defaultChecked = false;\n");
        appendDirtyJavaScript("}\n");
    }

}
