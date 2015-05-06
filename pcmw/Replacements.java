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
*  File        : Replacements.java 
*  Created     : SHFELK  010220  Created.
*  Modified    :
*  JEWILK  010403  Changed file extensions from .asp to .page
*  SHCHLK  011016  Did the security check.
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  PAPELK  031018  Call Id 106758.Modified adjust() and preDefine() to set
*                  the view of Replaces PM No LOV dynamically.
*  CHAMLK  031018  Modified preDefine() to allow the user to delete Replacements.
*  THWILK  031027  Call Id 106758 - Modified adjust() to refer the DB value for PM_TYPE.
*  ARWILK  031219  Edge Developments - (Removed clone and doReset Methods)
*  VAGULK  031219  Made the field order according to the order in the Centura application(Web Alignment).
*  THWILK  040601  Added PM_REVISION and changed the method calls relating to the key change under IID AMEC109A, Multiple Standard Jobs on PMs.
*  THWILK  040607  Added lov's for REPLACED_PM_REVISION and some javascript methods lovReplacedPmRevision,lovReplacedPmNo, 
*                  validateReplacedPmNo under IID AMEC109A, Multiple Standard Jobs on PMs.
*  ARWILK  041111  Replaced getContents with printContents.
*  NIJALK  041115  Modified LOV for REPLACED_PM_NO. Added field PM_STATE. Modified adjust(). 
*  NIJALK  050815  Bug 126347, Modified java script function lovReplacedPmNo().
*  NIJALK  051019  Call 128002: Modified functions lovReplacedPmRevision(), validate().
*  AMDILK  060728  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMDILK  060807  Merged with Bug Id 58214
*  AMNILK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMDILK  060831  Bug 58216, Eliminated SQL Injection security vulnerability.Modifeid method okFindITEM0()
*  AMNILK  060905  Merged Bug 58216.
*  AMDILK  070731  Removed the scroll buttons of the parent when the detail block is in new or edit modes
*  chanLK  080206  Bug 66842, Change field format PM_NO 
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class Replacements extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.Replacements");

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
    private ASPTransactionBuffer trans;
    private ASPBuffer buff;
    private ASPBuffer row;
    private ASPBuffer temp;
    //private ASPCommand pmtype1;
    private String val;
    private ASPCommand cmd;
    private String mcode;
    private String mname;
    private String cont;
    private String actcode;
    private String actdescr;
    private ASPBuffer data;
    private ASPQuery q;
    private int currrow;
    private int headrowno;
    private int totalHeadRows;
    private String pnum;
    private String prev;
    private String strWhereCondition;
    private String strHeadList;
    private boolean exists;
    private int i;
    private String pmnumber;
    private String pmrevision;
    private int newTotalHeadRows;
    private String newWoNo;
    private String newRev;
    private String parentPmNo;
    private String parentPmRev;
    private String pmNo;
    private String pmRev;
    private String newPmNo;
    private String newPmRev;
    private String calling_url;
    private ASPBuffer buffer;
    private String pm_no;
    private String pm_rev;
    private String title_;
    private String xx;
    private String yy;
    private String zz;
    private String pmno;  
    private String pmrev;  
    //private String pmtype;  
    //private String pm_type1; 
    private String txt;  
    private ASPBuffer secBuff;
    private boolean varSec;
    private boolean secNextPre; 
    private boolean secPrep;
    private String callingUrl;
    private boolean check_callingform;

    //===============================================================
    // Construction 
    //===============================================================
    public Replacements(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();

        ctx = mgr.getASPContext();
        trans = mgr.newASPTransactionBuffer();

        //pmno = ctx.readNumber("PMN", pmno);
        //pmtype = ctx.readValue("PMT", pmtype);
        //pm_type1 = ctx.readValue("PMTO", pm_type1);
        secPrep = ctx.readFlag("SECPREP", false);
        varSec = ctx.readFlag("VARSEC", false);
        secNextPre = ctx.readFlag("SECNEXTPRE", false);
        callingUrl = ctx.getGlobal("CALLING_URL");

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
            okFind();
        else if (!mgr.isEmpty(mgr.getQueryStringValue("PM_NO")) && !mgr.isEmpty(mgr.getQueryStringValue("PM_REVISION")))
        {
            pmno = mgr.readValue("PM_NO");
            pmrev = mgr.readValue("PM_REVISION");
            okFind();
            okFindITEM0();
        }
        else if (mgr.dataTransfered())
        {
            //buff= mgr.getTransferedData();
            //row = buff.getBufferAt(0);
            //pmno = row.getNumberValue("PM_NO");
            //row = buff.getBufferAt(1);
            //pmtype = row.getValue("PM_TYPE");
            //startup();
            okFind();
            okFindITEM0();   
        }

        checkSecurity();
        if (callingUrl.indexOf("PmAction.page") != -1)
        {
            check_callingform = true;
        }
        if (callingUrl.indexOf("PmActionRound.page") != -1)
        {
            check_callingform = false;
        }
        adjust();
        //startup();
        //ctx.writeNumber("PMN",pmno);
        //ctx.writeValue("PMT",pmtype); 
        //ctx.writeValue("PMTO",pm_type1);
        ctx.writeFlag("SECPREP",secPrep);
        ctx.writeFlag("VARSEC",varSec);
        ctx.writeFlag("SECNEXTPRE",secNextPre);
    }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

    /*public void startup()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        if ("RA".equals(pmtype))
            pmtype1 = trans.addCustomFunction("HED","Pm_Type_API.Get_Db_Value(1)","PM_TYPE");

        else if ("SA".equals(pmtype))
            pmtype1 = trans.addCustomFunction("HED","Pm_Type_API.Get_Db_Value(0)","PM_TYPE");

        trans = mgr.perform(trans);   
        pm_type1 = trans.getValue("HED/DATA/PM_TYPE");  
    }*/

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();

        val = mgr.readValue("VALIDATE");

        if ("REPLACED_PM_NO".equals(val))
        {
            String validationAttrAtr1 = "";
            String reppmno = "";
            String reppmrev = "";
            trans.clear();
            validationAttrAtr1 = mgr.readValue("REPLACED_PM_NO");
            if (mgr.readValue("REPLACED_PM_NO").indexOf("~") > -1)
            {

                String [] attrarr =  validationAttrAtr1.split("~");

                reppmno  = attrarr[0];
                reppmrev = attrarr[1]; 
            }
            else
            {
                reppmno = mgr.readValue("REPLACED_PM_NO");
                reppmrev = mgr.readValue("REPLACED_PM_REVISION");
            }


            cmd = trans.addCustomFunction("TEST1", "Pm_Action_API.Get_Mch_Code", "MCHCODE" );
            cmd.addParameter("REPLACED_PM_NO",reppmno);
            cmd.addParameter("REPLACED_PM_REVISION",reppmrev);

            cmd = trans.addCustomFunction("TEST2", "Pm_Action_API.Get_Mch_Name", "MCHNAME" );
            cmd.addParameter("REPLACED_PM_NO",reppmno);
            cmd.addParameter("REPLACED_PM_REVISION",reppmrev);

            cmd = trans.addCustomFunction("TEST3", "PM_ACTION_API.Get_Contract", "ITEM0_CONTRACT" );
            cmd.addParameter("REPLACED_PM_NO",reppmno);
            cmd.addParameter("REPLACED_PM_REVISION",reppmrev);


            cmd = trans.addCustomFunction("TEST4", "Pm_Action_API.Get_Action_Code_Id", "ACTIONCODE" );
            cmd.addParameter("REPLACED_PM_NO",reppmno);
            cmd.addParameter("REPLACED_PM_REVISION",reppmrev);


            cmd = trans.addCustomFunction("TEST5", "Pm_Action_API.Get_Action_Descr", "ACTIONDESCRIPTION" );
            cmd.addParameter("REPLACED_PM_NO",reppmno);
            cmd.addParameter("REPLACED_PM_REVISION",reppmrev);


            trans = mgr.validate(trans);

            mcode = trans.getValue("TEST1/DATA/MCHCODE");
            mname = trans.getValue("TEST2/DATA/MCHNAME");
            cont  = trans.getValue("TEST3/DATA/ITEM0_CONTRACT");
            actcode  = trans.getValue("TEST4/DATA/ACTIONCODE");
            actdescr = trans.getValue("TEST5/DATA/ACTIONDESCRIPTION");


            txt=(mgr.isEmpty(reppmno)? "":reppmno)+ "^" + (mgr.isEmpty(reppmrev)? "":reppmrev)+ "^" + (mgr.isEmpty(mcode)? "":mcode)+ "^" + (mgr.isEmpty(mname)? "":mname)+ "^" + (mgr.isEmpty(cont)? "":cont)+ "^" + (mgr.isEmpty(actcode)? "":actcode)+ "^" + (mgr.isEmpty(actdescr)? "":actdescr)+ "^";
            mgr.responseWrite( txt );

            mgr.endResponse();

        }
        if ("REPLACED_PM_REVISION".equals(val))
        {
            String validationAttrAtr1 = mgr.readValue("REPLACED_PM_REVISION");
            String reppmno = "";
            String reppmrev = "";

            if (mgr.readValue("REPLACED_PM_REVISION").indexOf("~") > -1)
            {
                String [] attrarr =  validationAttrAtr1.split("~");

                reppmno  = attrarr[0];
                reppmrev = attrarr[1]; 
            }
            else
            {
                reppmno = mgr.readValue("REPLACED_PM_NO");
                reppmrev = mgr.readValue("REPLACED_PM_REVISION");
            }

            trans.clear();
            cmd = trans.addCustomFunction("TEST1", "Pm_Action_API.Get_Mch_Code", "MCHCODE" );
            cmd.addParameter("REPLACED_PM_NO",reppmno);
            cmd.addParameter("REPLACED_PM_REVISION",reppmrev);

            cmd = trans.addCustomFunction("TEST2", "Pm_Action_API.Get_Mch_Name", "MCHNAME" );
            cmd.addParameter("REPLACED_PM_NO",reppmno);
            cmd.addParameter("REPLACED_PM_REVISION",reppmrev);

            cmd = trans.addCustomFunction("TEST3", "PM_ACTION_API.Get_Contract", "ITEM0_CONTRACT" );
            cmd.addParameter("REPLACED_PM_NO",reppmno);
            cmd.addParameter("REPLACED_PM_REVISION",reppmrev);


            cmd = trans.addCustomFunction("TEST4", "Pm_Action_API.Get_Action_Code_Id", "ACTIONCODE" );
            cmd.addParameter("REPLACED_PM_NO",reppmno);
            cmd.addParameter("REPLACED_PM_REVISION",reppmrev);


            cmd = trans.addCustomFunction("TEST5", "Pm_Action_API.Get_Action_Descr", "ACTIONDESCRIPTION" );
            cmd.addParameter("REPLACED_PM_NO",reppmno);
            cmd.addParameter("REPLACED_PM_REVISION",reppmrev);


            trans = mgr.validate(trans);

            mcode = trans.getValue("TEST1/DATA/MCHCODE");
            mname = trans.getValue("TEST2/DATA/MCHNAME");
            cont  = trans.getValue("TEST3/DATA/ITEM0_CONTRACT");
            actcode  = trans.getValue("TEST4/DATA/ACTIONCODE");
            actdescr = trans.getValue("TEST5/DATA/ACTIONDESCRIPTION");


            txt=(mgr.isEmpty(reppmno)? "":reppmno)+ "^" + (mgr.isEmpty(reppmrev)? "":reppmrev)+ "^" + (mgr.isEmpty(mcode)? "":mcode)+ "^" + (mgr.isEmpty(mname)? "":mname)+ "^" + (mgr.isEmpty(cont)? "":cont)+ "^" + (mgr.isEmpty(actcode)? "":actcode)+ "^" + (mgr.isEmpty(actdescr)? "":actdescr)+ "^";
            mgr.responseWrite( txt );

            mgr.endResponse();

        }


    }

//-----------------------------------------------------------------------------
//----------------------  CMDBAR EDIT-IT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void newRowITEM0()
    {
        ASPManager mgr = getASPManager();

        cmd = trans.addEmptyCommand("ITEM0","PM_ACTION_REPLACED_API.New__",itemblk0);
        cmd.setOption("ACTION","PREPARE");
        trans = mgr.perform(trans);
        data = trans.getBuffer("ITEM0/DATA");
        data.setFieldItem("ITEM0_PM_NO",headset.getRow().getValue("PM_NO"));
        data.setFieldItem("ITEM0_PM_REVISION",headset.getRow().getValue("PM_REVISION"));
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

        trans.clear();
        headrowno = headset.getCurrentRowNo();
        q = trans.addEmptyQuery(headblk);
	//Bug 58216 Start
        if (!mgr.isEmpty(pmno)){
            q.addWhereCondition("PM_NO = ?");
    	    q.addParameter("PM_NO",pmno);
	}
        if (!mgr.isEmpty(pmrev)){
            q.addWhereCondition("PM_REVISION = ?");
	    q.addParameter("PM_REVISION",pmrev);
	}
	//Bug 58216 End

        //q.addWhereCondition("PM_TYPE_DB = '"+pm_type1+"'");

        if (mgr.dataTransfered())
            q.addOrCondition(mgr.getTransferedData());

        q.includeMeta("ALL");

        mgr.submit(trans);
        trans.clear();
        int noOfRows = headset.countRows();
        headset.first();
        String pmNo="";
        String pmRev="";
        for (int k=0;k<noOfRows;k++)
        {
            //temp = headset.getRow();
            pmNo= headset.getRow().getValue("PM_NO");
            pmRev=headset.getRow().getFieldValue("PM_REVISION");
            cmd = trans.addCustomCommand("TEST" + k, "Pm_Action_Replaced_API.Get_Replaced_By");
            cmd.addParameter("PM_NO",pmNo);
            cmd.addParameter("PM_REVISION",pmRev);
            cmd.addParameter("REPLACEDBYPMNO");
            cmd.addParameter("REPLACEDBYPMREVISION");
            headset.next();
        }

        trans = mgr.perform(trans);
        headset.first();
        for (int k=0;k<noOfRows;k++)
        {
            temp = headset.getRow();
            pmNo=trans.getValue("TEST" + k + "/DATA/REPLACEDBYPMNO");
            pmRev=trans.getValue("TEST" + k + "/DATA/REPLACEDBYPMREVISION");
            temp.setValue("REPLACEDBYPMNO",pmNo);
            temp.setValue("REPLACEDBYPMREVISION",pmRev);
            headset.setRow(temp);
            headset.next();

        }


        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWREPLACEMENTSNODATA: No data found."));
            headset.clear();
        }
        else if (headset.countRows() == 1)
            headlay.setLayoutMode(headlay.SINGLE_LAYOUT);

        trans.clear();
        headset.goTo(headrowno);
        mgr.createSearchURL(headblk);    
    }




    /*public void okFind1()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        q = trans.addEmptyQuery(headblk);
        //q.addWhereCondition("PM_NO = '"+pmno+"'");
        //q.addWhereCondition("PM_TYPE_DB = '"+pm_type1+"'");

        q.includeMeta("ALL");

        mgr.submit(trans);

        if (headset.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWREPLACEMENTSNODATA: No data found."));
            headset.clear();
        }

        headlay.setLayoutMode(headlay.SINGLE_LAYOUT);      
        trans.clear();
        mgr.createSearchURL(headblk);    
    }*/

//-----------------------------------------------------------------------------
//-----------------------  CMDBAR SEARCH-IT FUNCTIONS  ------------------------
//-----------------------------------------------------------------------------

    public void countFindITEM0()
    {
        ASPManager mgr = getASPManager();

        currrow = headset.getCurrentRowNo();
        q = trans.addQuery(itemblk0);
	//Bug 58216 Start
        q.addWhereCondition("PM_NO = ?" );
        q.addWhereCondition("PM_REVISION = ?" );
	q.addParameter("PM_NO",headset.getRow().getValue("PM_NO"));
	q.addParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));
	//bug 58216 End
        q.setSelectList("to_char(count(*)) N");
        mgr.submit(trans);
        itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
        itemset0.clear();
        headset.goTo(currrow);   
    }


    public void okFindITEM0()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();

        q = trans.addEmptyQuery(itemblk0);
	//Bug 58216 Start
        q.addWhereCondition("PM_NO = ?" );
        q.addWhereCondition("PM_REVISION = ?" );
	q.addParameter("PM_NO",headset.getRow().getValue("PM_NO"));
	q.addParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));
	//bug 58216 End 
        q.setOrderByClause("REPLACED_PM_NO,REPLACED_PM_REVISION");
        q.includeMeta("ALL");

        mgr.submit(trans);


        headset.goTo(headrowno);
    }


    public void okFindITEM1()
    {
        ASPManager mgr = getASPManager();

        trans.clear();
        headrowno = headset.getCurrentRowNo();

        q = trans.addEmptyQuery(itemblk0);
	//Bug 58216 Start
        q.addWhereCondition("PM_NO = ?" );
        q.addWhereCondition("PM_REVISION = ?" );
	q.addParameter("PM_NO",headset.getRow().getValue("PM_NO"));
	q.addParameter("PM_REVISION",headset.getRow().getValue("PM_REVISION"));
	//bug 58216 End
        q.setOrderByClause("REPLACED_PM_NO,REPLACED_PM_REVISION");
        q.includeMeta("ALL");

        mgr.submit(trans);

        if (itemset0.countRows() == 0)
        {
            mgr.showAlert(mgr.translate("PCMWREPLACEMENTSNODATA: No data found."));
            itemset0.clear();
        }

        headset.goTo(headrowno);
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void nextLevel()
    {
        ASPManager mgr = getASPManager();

        totalHeadRows = headset.countRows();

        if (itemset0.countRows() == 0)
            mgr.showAlert(mgr.translate("PCMWREPLACEMENTSPER: There are no more levels to go."));
        else
        {
            if (itemlay0.isMultirowLayout())
            {
                itemset0.storeSelections();
                itemset0.setFilterOn();
                pnum = itemset0.getRow().getValue("REPLACED_PM_NO");  
                prev = itemset0.getRow().getValue("REPLACED_PM_REVISION");  
                itemset0.setFilterOff();
            }
            else
            {
                pnum = itemset0.getValue("REPLACED_PM_NO");
                prev = itemset0.getValue("REPLACED_PM_REVISION");  

            }
            strWhereCondition ="(PM_NO,PM_REVISION) IN (";
            strHeadList = "";
            exists = false;


            headset.first();
	    //Bug 58216 start
	    ASPBuffer paramBuff = mgr.newASPBuffer();
	    paramBuff = headset.getRows("PM_NO, PM_REVISION");
            for (i=1; i<=totalHeadRows ; i++)
            {
                strHeadList += "(";
                pmnumber = headset.getRow().getValue("PM_NO");
                pmrevision = headset.getRow().getValue("PM_REVISION");
                strHeadList += "?,?),";


                if (pnum.equals(pmnumber) && prev.equals(pmrevision))
                    exists = true;

                headset.next();
            }

            if (!exists)
            {
                strWhereCondition = strWhereCondition + strHeadList +  "(?, ?))";

                headset.clear();
                itemset0.clear();
                trans.clear();

                q = trans.addEmptyQuery(headblk);  
                q.addWhereCondition(strWhereCondition);
		for (int i = 0; i < paramBuff.countItems(); i++)
                {
                    ASPBuffer temp = paramBuff.getBufferAt(i);
                    q.addParameter("PM_NO",temp.getValueAt(0));
                    q.addParameter("PM_REVISION",temp.getValueAt(1));
                }
		q.addParameter("PM_NO", pnum);
		q.addParameter("PM_REVISION", prev);
		//Bug 58216 end
                q.includeMeta("ALL");

                mgr.submit(trans);   
            }

            newTotalHeadRows = headset.countRows();
            headset.first();
            for (i=0; i<=newTotalHeadRows; i++)
            {
                newWoNo = headset.getRow().getValue("PM_NO");
                newRev = headset.getRow().getValue("PM_REVISION");


                if (pnum.equals(newWoNo) &&  prev.equals(newRev))
                {
                    break;
                }
                headset.next();
            }

            headset.goTo(i);
            trans.clear();
            okFindITEM0();
        }  
    }


    public void preLevel()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isMultirowLayout())
        {
            headset.storeSelections();
            headset.setFilterOn();
            parentPmNo = headset.getRow().getValue("REPLACEDBYPMNO");   
            pmNo = headset.getRow().getValue("PM_NO");   
            pmRev = headset.getRow().getValue("PM_REVISION");   
            headset.setFilterOff();
        }

        else
            parentPmNo = headset.getValue("REPLACEDBYPMNO");   
        parentPmRev = headset.getValue("REPLACEDBYPMREVISION");   


        if (mgr.isEmpty(parentPmNo) && mgr.isEmpty(parentPmRev))
            mgr.showAlert(mgr.translate("PCMWREPLACEMENTSPARENTREC: No parent record exists for current record."));

        else
        {
            strWhereCondition ="(PM_NO,PM_REVISION) IN (";
            strHeadList = "";
            exists = false;   
            totalHeadRows = headset.countRows();
            headset.first();

	    //Bug 58216 start
	    ASPBuffer paramBuff = mgr.newASPBuffer();
	    paramBuff = headset.getRows("PM_NO, PM_REVISION");
            for (i=1; i<=totalHeadRows ; i++)
            {
		strHeadList += "(";
                pmNo = headset.getRow().getValue("PM_NO");
                pmRev = headset.getRow().getValue("PM_REVISION");
                strHeadList += "?,?),";

                if (parentPmNo.equals(pmNo) && parentPmRev.equals(pmRev))
                    exists = true;

                headset.next();
            }

            if (!exists)
            {
		strWhereCondition = strWhereCondition + strHeadList +  "(?, ?))";
		
                headset.clear();
                itemset0.clear();  
                trans.clear();

                q = trans.addEmptyQuery(headblk);  
                q.addWhereCondition(strWhereCondition);
		for (int i = 0; i < paramBuff.countItems(); i++)
                {
                    ASPBuffer temp = paramBuff.getBufferAt(i);
                    q.addParameter("PM_NO",temp.getValueAt(0));
                    q.addParameter("PM_REVISION",temp.getValueAt(1));
                }
		q.addParameter("PM_NO", parentPmNo);
		q.addParameter("PM_REVISION", parentPmRev);
		//Bug 58216 end
                q.includeMeta("ALL");

                mgr.submit(trans);   
            }

            newTotalHeadRows = headset.countRows();
            headset.first();
            for (i=0; i<=newTotalHeadRows; i++)
            {
                newPmNo = headset.getRow().getValue("PM_NO");
                newPmRev = headset.getRow().getValue("PM_REVISION");

                if (parentPmNo.equals(newPmNo) && parentPmRev.equals(newPmRev))
                {
                    break;
                }
                headset.next();
            }

            headset.goTo(i);
            trans.clear();
            okFindITEM0();
        }
    }


    public void prepare()
    {
        ASPManager mgr = getASPManager();

        calling_url=mgr.getURL();
        ctx.setGlobal("CALLING_URL",calling_url);

        if (itemlay0.isMultirowLayout())
        {
            itemset0.storeSelections();
            itemset0.setFilterOn();
        }

        buffer=mgr.newASPBuffer();
        row=buffer.addBuffer("0");
        row.addItem("PM_NO",itemset0.getRow().getValue("REPLACED_PM_NO"));
        row.addItem("PM_REVISION",itemset0.getRow().getValue("REPLACED_PM_REV"));

        pm_no = itemset0.getRow().getValue("REPLACED_PM_NO");
        pm_rev = itemset0.getRow().getValue("REPLACED_PM_REV");

        if ("ActiveRound".equals(headset.getValue("PM_TYPE_DB")))
            mgr.redirectTo("PmActionRound.page?PM_NO="+mgr.URLEncode(pm_no)+"&PM_REVISION="+mgr.URLEncode(pm_rev)+"");

        else if ("ActiveSeparate".equals(headset.getValue("PM_TYPE_DB")))
            mgr.redirectTo("PmAction.page?PM_NO="+mgr.URLEncode(pm_no)+"&PM_REVISION="+mgr.URLEncode(pm_rev)+"");
    }


    public void preDefine()
    {
        ASPManager mgr = getASPManager();


        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("PM_NO","Number", "#");
        f.setSize(8);
        f.setMandatory();
        f.setLabel("PCMWREPLACEMENTSPM_NO: PM No");

        f = headblk.addField("PM_REVISION");
        f.setSize(8);
        f.setMandatory();
        f.setLabel("PCMWREPLACEMENTSPM_REV: PM Revision");

        f = headblk.addField("MCH_CODE");
        f.setSize(15);
        f.setMaxLength(100);
        f.setDynamicLOV("MAINTENANCE_OBJECT","CONTRACT",600,450);
        f.setLabel("PCMWREPLACEMENTSMCH_CODE: Object ID");
        f.setUpperCase();

        f = headblk.addField("MCH_NAME");
        f.setSize(23);
        f.setLabel("PCMWREPLACEMENTSMCH_NAME: Object Description");

        f = headblk.addField("CONTRACT");
        f.setSize(8);
        f.setDynamicLOV("SITE",600,450);
        f.setLabel("PCMWREPLACEMENTSCONTRACT: Site");
        f.setUpperCase();

        f = headblk.addField("ACTION_CODE_ID");
        f.setSize(15);
        f.setDynamicLOV("MAINTENANCE_ACTION",600,450);
        f.setLabel("PCMWREPLACEMENTSACTION_CODE_ID: Action");
        f.setUpperCase();

        f = headblk.addField("ACTION_DESCR");
        f.setSize(20);
        f.setLabel("PCMWREPLACEMENTSACTION_DESCR: Action Description");

        f = headblk.addField("REPLACEDBYPMNO","Number");
        f.setSize(15);
        f.setFunction("''");
        //f.setFunction("Pm_Action_Replaced_API.Get_Replaced_By(:PM_NO,:PM_REVISION)");
        //mgr.getASPField("PM_NO").setValidation("REPLACEDBYPMNO");
        //f.setCustomValidation("PM_NO,PM_REVISION","REPLACEDBYPMNO,REPLACEDBYPMREVISION");
        f.setLabel("PCMWREPLACEMENTSREPLACEDBYPMNO: Replaced by Pm No");

        f = headblk.addField("REPLACEDBYPMREVISION");
        f.setSize(15);
        f.setFunction("''");
        //f.setFunction("Pm_Action_Replaced_API.Get_Replaced_By(:PM_NO,PM_REVISION)");
        //mgr.getASPField("PM_NO").setValidation("REPLACEDBYPMNO");
        //f.setCustomValidation("PM_NO,PM_REVISION","REPLACEDBYPMNO,REPLACEDBYPMREVISION");
        f.setLabel("PCMWREPLACEMENTSREPLACEDBYPMREVISION: Replaced by PM Revision");

        f = headblk.addField("PM_TYPE");
        f.setSize(11);
        f.setMandatory();
        f.setHidden();

        f = headblk.addField("PM_TYPE_DB");
        f.setSize(11);
        f.setMandatory();
        f.setHidden();

        f = headblk.addField("PM_STATE");
        f.setSize(20);
        f.setHidden();
        f.setFunction("Pm_Action_API.Get_Pm_State(:PM_NO,:PM_REVISION)");

        headblk.setView("PM_ACTION");
        headblk.defineCommand("PM_ACTION_API","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();

        headtbl = mgr.newASPTable(headblk);
        headtbl.setTitle(mgr.translate("PCMWREPLACEMENTSHD: Replacements by PM Action"));
        headtbl.setWrap();

        headbar = mgr.newASPCommandBar(headblk);

        headbar.addCustomCommand("none   ", "");
        headbar.addCustomCommand("nextLevel",mgr.translate("PCMWREPLACEMENTSNEXTL1: Next Level"));
        headbar.addCustomCommand("preLevel",mgr.translate("PCMWREPLACEMENTSPREL1: Previous Level"));

        headbar.disableCommand(headbar.BACK);
        headbar.disableCommand(headbar.EDITROW);
        headbar.disableCommand(headbar.NEWROW);
        headbar.disableCommand(headbar.DUPLICATEROW);
        headbar.disableCommand(headbar.DELETE);         

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);
        headlay.setSimple("MCH_NAME");

        //=================================================================================================================   

        itemblk0 = mgr.newASPBlock("ITEM0");

        f = itemblk0.addField("ITEM0_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk0.addField("ITEM0_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk0.addField("REPLACED_PM_NO","Number", "#");
        f.setSize(20);
        f.setMandatory();
        f.setLabel("PCMWREPLACEMENTSREPLACED_PM_NO: Replaces PM No");
        f.setCustomValidation("REPLACED_PM_NO","REPLACED_PM_NO,REPLACED_PM_REVISION,MCHCODE,MCHNAME,ITEM0_CONTRACT,ACTIONCODE,ACTIONDESCRIPTION");

        f = itemblk0.addField("REPLACED_PM_REVISION");
        f.setSize(20);
        f.setMandatory();
        f.setLabel("PCMWREPLACEMENTSREPLACED_PM_REVISION: Replaces PM Revision");
        f.setCustomValidation("REPLACED_PM_NO,REPLACED_PM_REVISION","REPLACED_PM_NO,REPLACED_PM_REVISION,MCHCODE,MCHNAME,ITEM0_CONTRACT,ACTIONCODE,ACTIONDESCRIPTION");


        f = itemblk0.addField("MCHCODE");
        f.setSize(20);
        f.setLabel("PCMWREPLACEMENTSMCHCODE: Object ID");
        f.setFunction("Pm_Action_API.Get_Mch_Code(:REPLACED_PM_NO,:REPLACED_PM_REVISION)");

        f = itemblk0.addField("MCHNAME");
        f.setSize(20);
        f.setLabel("PCMWREPLACEMENTSMCHNAME: Object Description");
        f.setFunction("Pm_Action_API.Get_Mch_Name(:REPLACED_PM_NO,:REPLACED_PM_REVISION)");

        f = itemblk0.addField("ITEM0_CONTRACT");
        f.setSize(15);
        f.setLabel("PCMWREPLACEMENTSITEM0_CONTRACT: Site");
        f.setFunction("PM_ACTION_API.Get_Contract(:REPLACED_PM_NO,:REPLACED_PM_REVISION)");

        f = itemblk0.addField("ACTIONCODE");
        f.setSize(20);
        f.setLabel("PCMWREPLACEMENTSACTIONCODE: Action");
        f.setFunction("Pm_Action_API.Get_Action_Code_Id(:REPLACED_PM_NO,:REPLACED_PM_REVISION)");

        f = itemblk0.addField("ACTIONDESCRIPTION");
        f.setSize(20);
        f.setLabel("PCMWREPLACEMENTSACTIONDESCRIPTION: Action Description");
        f.setFunction("Pm_Action_API.Get_Action_Descr(:REPLACED_PM_NO,:REPLACED_PM_REVISION)");

        f = itemblk0.addField("ITEM0_PM_NO","Number", "#");
        f.setSize(11);
        f.setLOV("PmActionLov.page",600,445);
        f.setHidden();
        f.setDbName("PM_NO");

        f = itemblk0.addField("ITEM0_PM_REVISION");
        f.setSize(11);
        f.setLOV("PmActionLov.page","ITEM0_PM_NO",600,445);
        f.setHidden();
        f.setDbName("PM_REVISION");

        itemblk0.setView("PM_ACTION_REPLACED");
        itemblk0.defineCommand("PM_ACTION_REPLACED_API","New__,Modify__,Remove__");
        itemset0 = itemblk0.getASPRowSet();

        itemblk0.setMasterBlock(headblk);

        itembar0 = mgr.newASPCommandBar(itemblk0);
        itembar0.enableCommand(itembar0.FIND);
        itembar0.disableCommand(itembar0.EDITROW);
        //itembar0.disableCommand(itembar0.DELETE);
        itembar0.enableCommand(itembar0.DELETE);
        itembar0.disableCommand(itembar0.DUPLICATEROW);

        itembar0.defineCommand(itembar0.OKFIND,"okFindITEM1");
        itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
        itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");

        itembar0.defineCommand(itembar0.SAVERETURN,null,"checkItem0Fields(-1)");
        itembar0.defineCommand(itembar0.SAVENEW,null,"checkItem0Fields(-1)");

        itembar0.addCustomCommand("none   ", "");
        itembar0.addCustomCommand("nextLevel",mgr.translate("PCMWREPLACEMENTSNEXTL: Next Level"));
        itembar0.addCustomCommand("preLevel",mgr.translate("PCMWREPLACEMENTSPREL: Previous Level"));
        itembar0.addCustomCommand("prepare",mgr.translate("PCMWREPLACEMENTSPREP: Prepare..."));

        itemtbl0 = mgr.newASPTable(itemblk0);
        itemtbl0.setTitle(mgr.translate("PCMWREPLACEMENTSITM0: PM Action Replaced"));
        itemtbl0.setWrap();

        itemlay0 = itemblk0.getASPBlockLayout();
        itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);  
    }

    public void checkSecurity()             
    {
        ASPManager mgr = getASPManager();

        if (!varSec)
        {
            trans.clear();

            trans.addSecurityQuery("PM_ACTION");
            trans.addPresentationObjectQuery("PCMW/PmAction.page,PCMW/PmActionRound.page");

            trans = mgr.perform(trans);

            secBuff = trans.getSecurityInfo();

            if (secBuff.itemExists("PM_ACTION") && secBuff.namedItemExists("PCMW/PmAction.page") && secBuff.namedItemExists("PCMW/PmActionRound.page"))
                secPrep = true;
            if (secBuff.itemExists("PM_ACTION"))
                secNextPre = true;

            varSec = true;
        }
    }                         


    public void adjust()
    {
        ASPManager mgr = getASPManager();

        if (headlay.isSingleLayout() && headset.countRows()>0)
        {
            title_ = mgr.translate("PCMWREPLACEMENTSDD: Replacements by PM Action - ");

            xx = headset.getRow().getValue("PM_NO");
            yy = headset.getRow().getValue("MCH_CODE");
            zz = headset.getRow().getValue("ACTION_CODE_ID");
        }
        else
        {
            title_ = mgr.translate("PCMWREPLACEMENTSDDMULTI: Replacements by PM Action");
            xx = "";
            yy = "";
            zz = "";
        }

        if (!secPrep)
            itembar0.removeCustomCommand("prepare");
        if (!secNextPre)
        {
            headbar.removeCustomCommand("nextLevel"); 
            headbar.removeCustomCommand("preLevel");
            itembar0.removeCustomCommand("preLevel");
            itembar0.removeCustomCommand("nextLevel");
        }

        if ("ActiveSeparate".equals(headset.getRow().getValue("PM_TYPE_DB")))
        {
            if (itemlay0.isFindLayout())
                mgr.getASPField("REPLACED_PM_NO").setLOV("PmActionLov1.page",600,450);
            else if (itemlay0.isNewLayout())
                mgr.getASPField("REPLACED_PM_NO").setLOV("PmActionLov.page",600,450);

            mgr.getASPField("REPLACED_PM_REVISION").setDynamicLOV("PM_ACTION_LOV","REPLACED_PM_NO PM_NO",600,450);
        }
        else if ("ActiveRound".equals(headset.getRow().getValue("PM_TYPE_DB")))
        {
            if (itemlay0.isFindLayout())
                mgr.getASPField("REPLACED_PM_NO").setLOV("PmActionLov1.page",600,450);
            else if (itemlay0.isNewLayout())
                mgr.getASPField("REPLACED_PM_NO").setLOV("PmActionLov.page",600,450);

            mgr.getASPField("REPLACED_PM_REVISION").setDynamicLOV("PM_ACTION_ROUND_LOV","REPLACED_PM_NO PM_NO",600,450);     
        }

        if (headset.countRows()>0 && headlay.isSingleLayout() && "Obsolete".equals(headset.getRow().getValue("PM_STATE")))
        {
            itembar0.disableCommand(itembar0.NEWROW);
            itembar0.disableCommand(itembar0.DELETE);
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

//-----------------------------------------------------------------------------
//------------------------  SHOW ITEM BAR FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void showItembar()
    {
        ASPManager mgr = getASPManager();

        mgr.responseWrite(itembar0.showBar());
    }


//===============================================================
//  HTML
//===============================================================
    protected String getDescription()
    {
        return title_ + xx + " " + yy + " " + zz;
    }

    protected String getTitle()
    {
        return "PCMWREPLACEMENTSTITLE: Replacements by PM Action";
    }

    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        appendToHTML(headlay.show());

        if (headlay.isSingleLayout() && (headset.countRows() > 0))
            appendToHTML(itemlay0.show());

        appendDirtyJavaScript("function lovReplacedPmNo(i,params)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	  if(params) param = params;\n");
        appendDirtyJavaScript("	  else param = '';\n");
        appendDirtyJavaScript("	  var enable_multichoice =(true && ITEM0_IN_FIND_MODE);\n");
        appendDirtyJavaScript("	  var key_value = (getValue_('REPLACED_PM_NO',i).indexOf('%') !=-1)? getValue_('REPLACED_PM_NO',i):''; \n");

        if (check_callingform)
        {
            if (itemlay0.isFindLayout())
            {
                appendDirtyJavaScript("   openLOVWindow('REPLACED_PM_NO',i,\n");
                appendDirtyJavaScript("   'PmActionLov1.page?__VIEW=DUMMY&__INIT=1'\n");
                appendDirtyJavaScript("   + '&__WHERE=' + \"PM_TYPE_DB LIKE  'ActiveSeparate' AND CONTRACT = (User_Allowed_Site_API.Authorized(CONTRACT)) AND PM_NO IN (SELECT PM_NO FROM PM_ACTION WHERE OBJSTATE = 'Active'AND TRUNC(SYSDATE) BETWEEN TRUNC(nvl(VALID_FROM,SYSDATE)) AND TRUNC(nvl(VALID_TO,SYSDATE)) AND Pm_Action_Calendar_Plan_API.Generated_Wo_Exist( PM_NO, PM_REVISION) =  'FALSE')\"\n");
                appendDirtyJavaScript("   + '&MULTICHOICE='+enable_multichoice+''+ '&__KEY_VALUE=' + URLClientEncode(getValue_('REPLACED_PM_NO',i)),600,450,'validateReplacedPmNo');\n");
            }
            else if (itemlay0.isNewLayout())
            {
                appendDirtyJavaScript("   openLOVWindow('REPLACED_PM_NO',i,\n");
                appendDirtyJavaScript("   'PmActionLov.page?__VIEW=DUMMY&__INIT=1'\n");
                appendDirtyJavaScript("   + '&__WHERE=' + \"PM_TYPE_DB LIKE  'ActiveSeparate' AND CONTRACT = (User_Allowed_Site_API.Authorized(CONTRACT)) AND PM_NO IN (SELECT PM_NO FROM PM_ACTION WHERE OBJSTATE = 'Active'AND TRUNC(SYSDATE) BETWEEN TRUNC(nvl(VALID_FROM,SYSDATE)) AND TRUNC(nvl(VALID_TO,SYSDATE)) AND Pm_Action_Calendar_Plan_API.Generated_Wo_Exist( PM_NO, PM_REVISION) =  'FALSE')\"\n");
                appendDirtyJavaScript("   + '&MULTICHOICE='+enable_multichoice+''+ '&__KEY_VALUE=' + URLClientEncode(getValue_('REPLACED_PM_NO',i)),600,450,'validateReplacedPmNo');\n");
            }
        }
        else
        {
            if (itemlay0.isFindLayout())
            {
                appendDirtyJavaScript("   openLOVWindow('REPLACED_PM_NO',i,\n");
                appendDirtyJavaScript("   'PmActionLov1.page?__VIEW=DUMMY&__INIT=1'\n");
                appendDirtyJavaScript("   + '&__WHERE=' + \"PM_TYPE_DB LIKE  'ActiveRound' AND CONTRACT = (User_Allowed_Site_API.Authorized(CONTRACT)) AND PM_NO IN (SELECT PM_NO FROM PM_ACTION WHERE OBJSTATE = 'Active'AND TRUNC(SYSDATE) BETWEEN TRUNC(nvl(VALID_FROM,SYSDATE)) AND TRUNC(nvl(VALID_TO,SYSDATE)) AND Pm_Action_Calendar_Plan_API.Generated_Wo_Exist( PM_NO, PM_REVISION) =  'FALSE')\"\n");
                appendDirtyJavaScript("   + '&MULTICHOICE='+enable_multichoice+''+ '&__KEY_VALUE=' + URLClientEncode(getValue_('REPLACED_PM_NO',i)),600,450,'validateReplacedPmNo');\n");
            }
            else if (itemlay0.isNewLayout())
            {
                appendDirtyJavaScript("   openLOVWindow('REPLACED_PM_NO',i,\n");
                appendDirtyJavaScript("   'PmActionLov.page?__VIEW=DUMMY&__INIT=1'\n");
                appendDirtyJavaScript("   + '&__WHERE=' + \"PM_TYPE_DB LIKE  'ActiveRound' AND CONTRACT = (User_Allowed_Site_API.Authorized(CONTRACT)) AND PM_NO IN (SELECT PM_NO FROM PM_ACTION WHERE OBJSTATE = 'Active'AND TRUNC(SYSDATE) BETWEEN TRUNC(nvl(VALID_FROM,SYSDATE)) AND TRUNC(nvl(VALID_TO,SYSDATE)) AND Pm_Action_Calendar_Plan_API.Generated_Wo_Exist( PM_NO, PM_REVISION) =  'FALSE')\"\n");
                appendDirtyJavaScript("   + '&MULTICHOICE='+enable_multichoice+''+ '&__KEY_VALUE=' + URLClientEncode(getValue_('REPLACED_PM_NO',i)),600,450,'validateReplacedPmNo');\n");
            }
        }
        appendDirtyJavaScript("}\n"); 

        appendDirtyJavaScript("function validateReplacedPmNo(i)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("if( getRowStatus_('ITEM0',i)=='QueryMode__' ) return;\n");
        appendDirtyJavaScript("   setDirty();\n");
        appendDirtyJavaScript("   if( getValue_('REPLACED_PM_NO',i).indexOf('~') == -1)\n");
        appendDirtyJavaScript("   {\n");
        appendDirtyJavaScript("      if( !checkReplacedPmNo(i) ) return;\n");
        appendDirtyJavaScript("   }\n");
        appendDirtyJavaScript("      if( getValue_('REPLACED_PM_NO',i).indexOf('%') != -1) return;\n");
        appendDirtyJavaScript("         if( getValue_('REPLACED_PM_NO',i)=='' )\n");
        appendDirtyJavaScript("         {\n");
        appendDirtyJavaScript("            getField_('REPLACED_PM_NO',i).value = '';\n");
        appendDirtyJavaScript("            getField_('REPLACED_PM_REVISION',i).value = '';\n");
        appendDirtyJavaScript("		   getField_('MCHCODE',i).value = '';\n");
        appendDirtyJavaScript("            getField_('MCHNAME',i).value = '';\n");
        appendDirtyJavaScript("            getField_('ITEM0_CONTRACT',i).value = '';\n");
        appendDirtyJavaScript("            getField_('ACTIONCODE',i).value = '';\n");
        appendDirtyJavaScript("            getField_('ACTIONDESCRIPTION',i).value = '';\n");
        appendDirtyJavaScript("            return;\n");
        appendDirtyJavaScript("         }\n");
        appendDirtyJavaScript(" window.status='Please wait for validation';\n");
        appendDirtyJavaScript(" r = __connect(\n");
        appendDirtyJavaScript(" '");
        appendDirtyJavaScript( mgr.getURL());
        appendDirtyJavaScript("?VALIDATE=REPLACED_PM_NO'+ '&REPLACED_PM_NO=' + URLClientEncode(getValue_('REPLACED_PM_NO',i)));\n");
        appendDirtyJavaScript(" window.status='';\n");
        appendDirtyJavaScript(" if( checkStatus_(r,'REPLACED_PM_NO',i,'Replaces PM No') )\n");
        appendDirtyJavaScript("    {\n");
        appendDirtyJavaScript("       assignValue_('REPLACED_PM_NO',i,0);\n");
        appendDirtyJavaScript("       assignValue_('REPLACED_PM_REVISION',i,1);\n");
        appendDirtyJavaScript("       assignValue_('MCHCODE',i,2);\n");
        appendDirtyJavaScript("       assignValue_('MCHNAME',i,3);\n");
        appendDirtyJavaScript("       assignValue_('ITEM0_CONTRACT',i,4);\n");
        appendDirtyJavaScript("       assignValue_('ACTIONCODE',i,5);\n");
        appendDirtyJavaScript("       assignValue_('ACTIONDESCRIPTION',i,6);\n");
        appendDirtyJavaScript("    }\n");
        appendDirtyJavaScript("}\n");

        if (itemlay0.isFindLayout())
            appendDirtyJavaScript("var lovPage = 'PmActionLov1';");
        else if (itemlay0.isNewLayout())
            appendDirtyJavaScript("var lovPage = 'PmActionLov';");

        appendDirtyJavaScript("function lovReplacedPmRevision(i,params)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	  if(params) param = params;\n");
        appendDirtyJavaScript("	  else param = '';\n");
        appendDirtyJavaScript("	  var enable_multichoice =(true && ITEM0_IN_FIND_MODE);\n");
        appendDirtyJavaScript("	  var key_value = (getValue_('REPLACED_PM_REVISION',i).indexOf('%') !=-1)? getValue_('REPLACED_PM_REVISION',i):''; \n");
        appendDirtyJavaScript("   var rPmNo = document.form.REPLACED_PM_NO.value;\n");

        if (check_callingform)
        {
            appendDirtyJavaScript("if (rPmNo != '') {");
            appendDirtyJavaScript("   openLOVWindow('REPLACED_PM_REVISION',i,\n");
            appendDirtyJavaScript("   '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PM_ACTION_LOV&__FIELD=Replaced+Pm+Revision&__INIT=1' \n");
            appendDirtyJavaScript("   + '&__WHERE=' + \"PM_NO LIKE '\" +rPmNo+\"'\"  \n");
            appendDirtyJavaScript("   + '&MULTICHOICE='+enable_multichoice+''+ '&__KEY_VALUE=' + URLClientEncode(getValue_('REPLACED_PM_REVISION',i)),600,450,'validateReplacedPmRevision');\n");
            appendDirtyJavaScript("} else {\n");
            appendDirtyJavaScript("   openLOVWindow('REPLACED_PM_REVISION',i,\n");
            appendDirtyJavaScript("   lovPage+'.page?__VIEW=DUMMY&__INIT=1'\n");
            appendDirtyJavaScript("   + '&__WHERE=' + \"PM_TYPE_DB LIKE  'ActiveSeparate' AND CONTRACT = (User_Allowed_Site_API.Authorized(CONTRACT)) AND PM_NO IN (SELECT PM_NO FROM PM_ACTION WHERE OBJSTATE = 'Active'AND TRUNC(SYSDATE) BETWEEN TRUNC(nvl(VALID_FROM,SYSDATE)) AND TRUNC(nvl(VALID_TO,SYSDATE)) AND Pm_Action_Calendar_Plan_API.Generated_Wo_Exist( PM_NO, PM_REVISION) =  'FALSE')\"\n");
            appendDirtyJavaScript("   + '&MULTICHOICE='+enable_multichoice+''+ '&__KEY_VALUE=' + URLClientEncode(getValue_('REPLACED_PM_NO',i)),600,450,'validateReplacedPmRevision');\n");
            appendDirtyJavaScript("}\n");
        }
        else
        {
            appendDirtyJavaScript("if (rPmNo != '') {");
            appendDirtyJavaScript("   openLOVWindow('REPLACED_PM_REVISION',i,\n");
            appendDirtyJavaScript("   '../common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PM_ACTION_ROUND_LOV&__FIELD=Replaced+Pm+Revision&__INIT=1' \n");
            appendDirtyJavaScript("   + '&__WHERE=' + \"PM_NO LIKE '\" +rPmNo+\"'\"  \n");
            appendDirtyJavaScript("   + '&MULTICHOICE='+enable_multichoice+''+ '&__KEY_VALUE=' + URLClientEncode(getValue_('REPLACED_PM_REVISION',i)),600,450,'validateReplacedPmRevision');\n");
            appendDirtyJavaScript("} else {\n");
            appendDirtyJavaScript("   openLOVWindow('REPLACED_PM_REVISION',i,\n");
            appendDirtyJavaScript("   lovPage+'.page?__VIEW=DUMMY&__INIT=1'\n");
            appendDirtyJavaScript("   + '&__WHERE=' + \"PM_TYPE_DB LIKE  'ActiveRound' AND CONTRACT = (User_Allowed_Site_API.Authorized(CONTRACT)) AND PM_NO IN (SELECT PM_NO FROM PM_ACTION WHERE OBJSTATE = 'Active'AND TRUNC(SYSDATE) BETWEEN TRUNC(nvl(VALID_FROM,SYSDATE)) AND TRUNC(nvl(VALID_TO,SYSDATE)) AND Pm_Action_Calendar_Plan_API.Generated_Wo_Exist( PM_NO, PM_REVISION) =  'FALSE')\"\n");
            appendDirtyJavaScript("   + '&MULTICHOICE='+enable_multichoice+''+ '&__KEY_VALUE=' + URLClientEncode(getValue_('REPLACED_PM_NO',i)),600,450,'validateReplacedPmRevision');\n");
            appendDirtyJavaScript("}\n");
        }
        appendDirtyJavaScript("}\n"); 

        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
        appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
        appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
    }
}
