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
*  File        : QuickReportToHistory.java 
*  Created     : ASP2JAVA Tool  010223  Created Using the ASP file QuickReportToHistory.asp
*  Modified    : 
*  CHDELK  010225  Converted to Java
*  CHDELK  010417  Actual Completion field size set to 22 
*                  "null" erreors in attribute strings were corrected
*                  Actual Start/ Actual Completion field length was set to 22
*  CHDELK  010423  Made corrections to Employee & Department validations
*  INROLK  010523  Changed the position of Description of Exec.Department. Call ID 65380 and 65384.
*  JEWILK  010710  Modified Netscape incompatible code segments in javascript functions. (call# 66177) 
*  INROLK  011018  Changed PrepareForm and finishForm . call id 70752,70812 ,
*  SHAFLK  020807  Bug id 31919, Changed finishForm,
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  BUNILK  021206  Added new field Object Site and replaced work order site by Object site whenever it refer to Object.       
*  INROLK  030120  Code Review Done.
*  JEWILK  030925  Corrected overridden javascript function toggleStyleDisplay(). 
*  PAPELK  031014  Call Id 103279.Corrected prepareForm() and validate() to takeup 
*                  default site and validate site according to object entered.
*  ARWILK  031110  (Bug#108229) An alert message was setup for the WO Creation Process.(Check method comments)
*  ARWILK  031215  Edge Developments - (Removed clone and doReset Methods)
*  VAGULK  040109  Made field order according to the order in the Centura application(Web Alignment).
*  SAPRLK  040224  Web Alignmnt - remove unnecessary method calls for hidden fields, removed unnecessary global variables,
*                  change of conditional code in validate method.
*  ARWILK  040722  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  ARWILK  041111  Replaced getContents with printContents.
*  SHAFLK  041209  Bug Id 48393, Modified methods nextForm() and finishForm().
*  Chanlk  041220  Merged Bug Id 48393.
*  NIJALK  050120  Modified LOVs and validations to ITEM1_ORG_CODE,ROLE_CODE and EMP_NO.
*  SHAFLK  050304  Bug 48641, Modified methods prepareForm(), finishForm() and validation of REPORTED_BY.
*  NIJALK  050408  Merged bug 48641.
*  NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile).
*  SHAFLK  060524  Bug 58197, Modified finishForm().
*  AMNILK  060629  Merged with SP1 APP7.
*  AMNILK  060911  Request Id: 35867. MTPR904 Hink tasks.Added field Work_Type_Id.
*  ILSOLK  070227  Modified finishForm().(Call ID 141319)
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  AMDILK  070330  Merged bug id 64068
*  AMDILK  070608  Called method " showBottomLine() " from preDefine()
*  AMNILK  070725  Eliminated XSS Security Vulnerability.
*  CHANLK  071201  Bug 69168, remove lable of PERFORMED_ACTION_LO, change window height
*  CHANLK  071202  Bug 69163, Resolve the translation problem.

* ----------------------------------------------------------------------------
* -----------------------APP7.5 SP1-------------------------------------
*  071215  ILSOLK  Bug Id 68773, Eliminated XSS.
*  SHAFLK  080311  Bug 72265, Modified prepareForm().
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
*  File        : QuickReportToHistory.java 
*  Created     : ASP2JAVA Tool  010223  Created Using the ASP file QuickReportToHistory.asp
*  Modified    : 
*  CHDELK  010225  Converted to Java
*  CHDELK  010417  Actual Completion field size set to 22 
*                  "null" erreors in attribute strings were corrected
*                  Actual Start/ Actual Completion field length was set to 22
*  CHDELK  010423  Made corrections to Employee & Department validations
*  INROLK  010523  Changed the position of Description of Exec.Department. Call ID 65380 and 65384.
*  JEWILK  010710  Modified Netscape incompatible code segments in javascript functions. (call# 66177) 
*  INROLK  011018  Changed PrepareForm and finishForm . call id 70752,70812 ,
*  SHAFLK  020807  Bug id 31919, Changed finishForm,
*  GACOLK  021204  Set Max Length of MCH_CODE to 100
*  BUNILK  021206  Added new field Object Site and replaced work order site by Object site whenever it refer to Object.       
*  INROLK  030120  Code Review Done.
*  JEWILK  030925  Corrected overridden javascript function toggleStyleDisplay(). 
*  PAPELK  031014  Call Id 103279.Corrected prepareForm() and validate() to takeup 
*                  default site and validate site according to object entered.
*  ARWILK  031110  (Bug#108229) An alert message was setup for the WO Creation Process.(Check method comments)
*  ARWILK  031215  Edge Developments - (Removed clone and doReset Methods)
*  VAGULK  040109  Made field order according to the order in the Centura application(Web Alignment).
*  SAPRLK  040224  Web Alignmnt - remove unnecessary method calls for hidden fields, removed unnecessary global variables,
*                  change of conditional code in validate method.
*  ARWILK  040722  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  ARWILK  041111  Replaced getContents with printContents.
*  SHAFLK  041209  Bug Id 48393, Modified methods nextForm() and finishForm().
*  Chanlk  041220  Merged Bug Id 48393.
*  NIJALK  050120  Modified LOVs and validations to ITEM1_ORG_CODE,ROLE_CODE and EMP_NO.
*  SHAFLK  050304  Bug 48641, Modified methods prepareForm(), finishForm() and validation of REPORTED_BY.
*  NIJALK  050408  Merged bug 48641.
*  NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile).
*  SHAFLK  060524  Bug 58197, Modified finishForm().
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  CHANLK  071201  Bug 69168, remove lable of PERFORMED_ACTION_LO, change window height
*  CHANLK  071202  Bug 69163, Resolve the translation problem.
*  SHAFLK  080307  Bug 72265, Modified prepareForm().
*  SHAFLK  080612  Bug 74329, Restructured the page to support adding Pre Posting.
*  NIJALK  080917  Bug 75335, Modified methods finishForm(), finishForm1().
*  NIJALK  081029  Rollbacked the changes done in Bug 75335.
*  CHARLK  090826  Bug 79242, Added a button instead of check box 'Add Work Order Prepostings'.
* -----------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class QuickReportToHistory extends ASPPageProvider
{
    //===============================================================
    // Static constants 
    //===============================================================
    public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.QuickReportToHistory");

    //===============================================================
    // Instances created on page creation (immutable attributes) 
    //===============================================================
    private ASPHTMLFormatter fmt;
    private ASPContext ctx;
    private ASPForm frm;

    private ASPBlock headblk;
    private ASPRowSet headset;
    private ASPCommandBar headbar;
    private ASPTable headtbl;
    private ASPBlockLayout headlay;

    private ASPBlock itemblk1;
    private ASPRowSet itemset1;
    private ASPCommandBar itembar1;
    private ASPTable itemtbl1;
    private ASPBlockLayout itemlay1;

    private ASPBlock BlkSympt;
    private ASPRowSet RowsetSympt;
    private ASPBlock BlkCause;
    private ASPRowSet RowsetCause;
    private ASPBlock BlkClass;
    private ASPRowSet RowsetClass;
    private ASPBlock BlkType;
    private ASPRowSet RowsetType;
    private ASPBlock BlkDisc;
    private ASPRowSet RowsetDisc;
    private ASPBlock BlkPerAct;
    private ASPRowSet RowsetPerAct;
    private ASPField f;
    private ASPBlock b;
    private ASPBlock blkPost;

    //===============================================================
    // Transient temporary variables (never cloned) 
    //===============================================================
    private ASPTransactionBuffer trans;
    private String empno;
    private String closeWin;
    private String sympt;
    private String caus;
    private String clas;
    private String typ;
    private String disc;
    private String peract;
    private ASPBuffer symptBuff;
    private ASPBuffer errorBuff;
    private ASPBuffer errclassBuff;
    private ASPBuffer errtypeBuff;
    private ASPBuffer errdiscBuff;
    private ASPBuffer peractBuff;
    private String val;
    private ASPCommand cmd;
    private ASPBuffer buf;
    private String tpname;
    private String tpempno;
    private String tprolecode;
    private String tporgcode;
    private double tpamount;
    private boolean errFlag;
    private String emp_no;
    private String itemrolecode;
    private String itemorgcode;
    private String itemContract;
    private double countOfNext;
    private double countOfNext2;
    private double countOfNext1;
    private String errDesc;
    private String orgCode;
    private String realSdate;
    private String realFdate;
    private ASPBuffer data;
    private double introFlag; 
    private String date_fmt; 
    private String txt;
    private boolean showHtmlPart;
    private String url;
    private boolean toDefaultPage;
    private String calling_url;

    //031110  ARWILK  Begin  (Bug#108229)
    private String sAlertMessage;
    //031110  ARWILK  End  (Bug#108229)
    private String sWoNo;
    private boolean preEna;
    private String prePostMessage;
    //Bug 79242, Start
    private boolean bOpenNewWindow;
    private String sTempPrePostId;
    private String urlString;
    private String newWinHandle;
    //Bug 79242, End

    //===============================================================
    // Construction 
    //===============================================================
    public QuickReportToHistory(ASPManager mgr, String page_path)
    {
        super(mgr,page_path);
    }

    public void run() 
    {
        ASPManager mgr = getASPManager();


        fmt = mgr.newASPHTMLFormatter();
        ctx = mgr.getASPContext();
        frm = mgr.getASPForm();

        trans = mgr.newASPTransactionBuffer();

        introFlag = toDouble(ctx.readValue("INTROFLAG","1"));
        empno = ctx.readValue("EMP","");
        emp_no = ctx.readValue("EMPNO","");
        itemContract = ctx.readValue("ITEMCONTRACT","");
        itemorgcode  = ctx.readValue("ITEMORGCODE","");
        itemrolecode = ctx.readValue("ITEMROLECODE","");
        errDesc = ctx.readValue("ERRDESC","");
        orgCode = ctx.readValue("ORGCODE","");
        realSdate  = ctx.readValue("REALSDATE","");
        realFdate = ctx.readValue("REALFDATE","");
        tporgcode= ctx.readValue("ORG","");
        closeWin = ctx.readValue("CLOSEWIN","");
        sympt = ctx.readValue("SYMPT","");
        caus = ctx.readValue("CAUS","");
        clas = ctx.readValue("CLAS","");
        typ = ctx.readValue("TYP","");
        disc = ctx.readValue("DISC_DISPLAY","");
        peract = ctx.readValue("PERACT",""); 
        symptBuff = ctx.readBuffer("SYMPTBUFF");
        errorBuff = ctx.readBuffer("ERRORBUFF");
        errclassBuff = ctx.readBuffer("ERRCLASSBUFF");
        errtypeBuff = ctx.readBuffer("ERRTYPEBUFF");
        errdiscBuff = ctx.readBuffer("ERRDISCBUFF");
        peractBuff = ctx.readBuffer("PERACTBUFF");
        showHtmlPart = ctx.readFlag("CTSSHHTML",true);
        toDefaultPage = ctx.readFlag("CTXTODEF",false);
        countOfNext = toDouble(ctx.readValue("COUNTOFNEXT","0"));
        countOfNext2 = toDouble(ctx.readValue("COUNTOFNEXTTWO","0"));
        countOfNext1 = toDouble(ctx.readValue("COUNTOFNEXTONE","0"));
        date_fmt = mgr.getFormatMask("Date",true);
        
        preEna = ctx.readFlag("PREENAA",false);
        sWoNo =  ctx.readValue("SWONO","");
	//Bug 79242, Start
        sTempPrePostId =  ctx.readValue("TEMPPREPOSTID","");
	//Bug 79242, End

        if (mgr.commandBarActivated())
            eval(mgr.commandBarFunction());
        else if (!mgr.isEmpty(mgr.getQueryStringValue("WNDFLAG")))
        {   
            ctx.setGlobal("WO_NO","0");


            if ("QuickReportToHistory".equals(mgr.readValue("WNDFLAG","")))
            {
                showHtmlPart = false;
            }
            else if ("QuickReportToHistoryFromPortal".equals(mgr.readValue("WNDFLAG","")))
            {
                showHtmlPart = false;
                toDefaultPage = true;

                calling_url = ctx.findGlobal("CALLING_URL");
                ctx.setGlobal("CALLING_URL",calling_url);                     
            }
        }
        else if (mgr.buttonPressed("PREVIOUS"))
            previousForm();
        else if (mgr.buttonPressed("NEXT"))
            nextForm();
        else if (mgr.buttonPressed("FINISH"))
            finishForm();
        else if (mgr.buttonPressed("CANCEL"))
            cancelForm();
        else if (mgr.buttonPressed("ADDTREP"))
            prepareITEM1();
        else if (mgr.buttonPressed("DELETECURRREP"))
            deleteITEM1();
        else if (mgr.buttonPressed("PREVTIMEREP"))
            backwardITEM();
        else if (mgr.buttonPressed("NEXTTIMEREP"))
            forwardITEM();
	//Bug 79242, Start
	else if (mgr.buttonPressed("ADDPREPOST")) 
	    addPreposting();
	//Bug 79242, End
        else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
            validate();
        else
        {
            prepareForm();
            populateListBoxes();
        }
        adjust();

        ctx.writeValue("INTROFLAG",String.valueOf(introFlag));
        ctx.writeValue("COUNTOFNEXT",String.valueOf(countOfNext));
        ctx.writeValue("COUNTOFNEXTTWO",String.valueOf(countOfNext2));
        ctx.writeValue("COUNTOFNEXTONE",String.valueOf(countOfNext1));
        ctx.writeValue("EMPNO",emp_no);
        ctx.writeValue("ITEMCONTRACT",itemContract);
        ctx.writeValue("ITEMORGCODE",itemorgcode);
        ctx.writeValue("ITEMROLECODE",itemrolecode);
        ctx.writeValue("EMP",empno);
        ctx.writeValue("ORG",tporgcode);
        ctx.writeValue("SYMPT",sympt);
        ctx.writeValue("CAUS",caus);
        ctx.writeValue("CLAS",clas);
        ctx.writeValue("TYP",typ);                
        ctx.writeValue("DISC_DISPLAY",disc);
        ctx.writeValue("PERACT",peract);
        ctx.writeFlag("CTSSHHTML",showHtmlPart);
        ctx.writeFlag("CTXTODEF",toDefaultPage);
        ctx.writeValue("ERRDESC",errDesc);
        ctx.writeValue("ORGCODE",orgCode);
        ctx.writeValue("REALSDATE",realSdate);
        ctx.writeValue("REALFDATE",realFdate);        
        
        ctx.writeFlag("PREENAA",preEna);
        ctx.writeValue("SWONO",sWoNo);
	//Bug 79242, Start
        ctx.writeValue("TEMPPREPOSTID",sTempPrePostId);
	//Bug 79242, End


        if (showHtmlPart)
        {
            ctx.writeBuffer("SYMPTBUFF",symptBuff);
            ctx.writeBuffer("ERRORBUFF",errorBuff);
            ctx.writeBuffer("ERRCLASSBUFF",errclassBuff);
            ctx.writeBuffer("ERRTYPEBUFF",errtypeBuff);
            ctx.writeBuffer("ERRDISCBUFF",errdiscBuff);
            ctx.writeBuffer("PERACTBUFF",peractBuff);
        }
    }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

    public void store()
    {

        if (headset.countRows()>0)
            headset.changeRow();
        if (itemset1.countRows()>0)
            itemset1.changeRows();
    }


    public void assign()
    {

        eval(headblk.generateAssignments());
        eval(itemblk1.generateAssignments());
    }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

    public void validate()
    {
        ASPManager mgr = getASPManager();

        val = mgr.readValue("VALIDATE");

        if ("CONTRACT".equals(val))
        {
            cmd = trans.addCustomFunction("GETCOMP","Site_API.Get_Company","COMPANY");
            cmd.addParameter("CONTRACT");

            trans = mgr.validate(trans);

            String tpcompany = trans.getValue("GETCOMP/DATA/COMPANY");

            txt = (mgr.isEmpty(tpcompany) ? "" :tpcompany);

            mgr.responseWrite(txt);
        }
        else if ("MCH_CODE".equals(val))
        {
            String objdesc = "";
            String mchCodeContract = mgr.readValue("MCH_CODE_CONTRACT");
            String mchCode = mgr.readValue("MCH_CODE");

            if (mgr.readValue("MCH_CODE").indexOf("~") > -1)
            {
                String strAttr = mgr.readValue("MCH_CODE");

                mchCode = strAttr.substring(0,strAttr.indexOf("~"));       
                String validationAttrAtr = strAttr.substring(strAttr.indexOf("~")+1,strAttr.length());
                mchCodeContract =  validationAttrAtr.substring(validationAttrAtr.indexOf("~")+1,validationAttrAtr.length());                

                cmd = trans.addCustomFunction("OBJDES","Maintenance_Object_API.Get_Mch_Name","MCHNAME");
                cmd.addParameter("MCH_CODE_CONTRACT",mchCodeContract);
                cmd.addParameter("MCH_CODE",mchCode);
            }
            else
            {

                cmd = trans.addCustomFunction("MCHCONTR","EQUIPMENT_OBJECT_API.Get_Contract","MCH_CODE_CONTRACT");
                cmd.addParameter("MCH_CODE");

                mchCodeContract = mgr.readValue("MCHCONTR/DATA/MCH_CODE_CONTRACT");

                cmd = trans.addCustomFunction("OBJDES","Maintenance_Object_API.Get_Mch_Name","MCHNAME");
                cmd.addReference("MCH_CODE_CONTRACT","MCHCONTR/DATA");
                cmd.addParameter("MCH_CODE");
            }

            trans = mgr.validate(trans);

            if (mgr.readValue("MCH_CODE").indexOf("~") == -1 && mgr.isEmpty(mchCodeContract))
                mchCodeContract = trans.getValue("MCHCONTR/DATA/MCH_CODE_CONTRACT");

            objdesc = trans.getValue("OBJDES/DATA/MCHNAME");

            txt = (mgr.isEmpty(objdesc) ? "" :objdesc) + "^" + (mgr.isEmpty(mchCodeContract) ? "" :mchCodeContract) + "^" + (mgr.isEmpty(mchCode) ? "" :mchCode) + "^";
            mgr.responseWrite(txt);
        }
        else if ("REAL_S_DATE".equals(val))
        {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("REAL_S_DATE",mgr.readValue("REAL_S_DATE"));
            mgr.responseWrite(buf.getFieldValue("REAL_S_DATE") +"^" );  
            mgr.endResponse();
        }
        else if ("REAL_F_DATE".equals(val))
        {
            buf = mgr.newASPBuffer();
            buf.addFieldItem("REAL_F_DATE",mgr.readValue("REAL_F_DATE"));
            mgr.responseWrite(buf.getFieldValue("REAL_F_DATE") +"^" );  
            mgr.endResponse();
        }
        else if ("REPORTED_BY".equals(val))
        {
            String dept = mgr.readValue("ORG_CODE");
            cmd = trans.addCustomFunction("GETNAME","PERSON_INFO_API.Get_Name","NAME");
            cmd.addParameter("REPORTED_BY");

            cmd = trans.addCustomFunction("EMPID","COMPANY_EMP_API.Get_Max_Employee_Id","EMPNO");
            cmd.addParameter("COMPANY");
            cmd.addParameter("REPORTED_BY");

            cmd = trans.addCustomFunction("ORG","Employee_API.Get_Organization","ORG_CODE");
            cmd.addParameter("COMPANY");
            cmd.addReference("EMPNO","EMPID/DATA");

            cmd = trans.addCustomFunction("ORGDESC","Organization_API.Get_Description","ORGCODEDESCRIPTION");
            cmd.addParameter("CONTRACT");
            cmd.addReference("ORG_CODE","ORG/DATA");

            trans = mgr.validate(trans);

            tpname = trans.getValue("GETNAME/DATA/NAME");
            tpempno = trans.getValue("EMPID/DATA/EMPNO");
            String orgdesc = trans.getValue("ORGDESC/DATA/ORGCODEDESCRIPTION");
            empno = tpempno;
            if (mgr.isEmpty(dept))
                tporgcode= trans.getValue("ORG/DATA/ORG_CODE");
            else
                tporgcode= dept;
            String dRegDate = mgr.readValue("REG_DATE"); 


            txt = (mgr.isEmpty(tpname ) ? "" :tpname ) + "^" + (mgr.isEmpty(tpempno) ? "" :tpempno)+ "^" + (mgr.isEmpty(dRegDate ) ? "" :dRegDate )+ "^" + (mgr.isEmpty(tporgcode ) ? "" :tporgcode ) + "^"+ (mgr.isEmpty(orgdesc) ? "" :orgdesc) + "^"; 

            mgr.responseWrite(txt);
        }
        else if ("EMP_NO".equals(val))
        {
            String reqstr = null;
            int startpos = 0;
            int endpos = 0;
            int i = 0;
            String ar[] = new String[2];
            String emp_id = "";

            String sOrgCode = mgr.readValue("ITEM1_ORG_CODE");
            String sRoleCode = mgr.readValue("ROLE_CODE");
            String sOrgContract = mgr.readValue("ITEM1_CONTRACT");

            String new_sign_id = mgr.readValue("EMP_NO","");

            if (new_sign_id.indexOf("^",0)>0)
            {
                for (i=0 ; i<2; i++)
                {
                    endpos = new_sign_id.indexOf("^",startpos);
                    reqstr = new_sign_id.substring(startpos,endpos);
                    ar[i] = reqstr;
                    startpos= endpos+1;
                }
                emp_id = ar[1];
            }
            else
                emp_id = new_sign_id;

            cmd = trans.addCustomFunction("GETPERID","Company_Emp_API.Get_Person_Id","SIGN");
            cmd.addParameter("COMPANY");
            cmd.addParameter("EMP_NO",emp_id);

            cmd = trans.addCustomFunction("GETNAME","PERSON_INFO_API.Get_Name","ITEM1_NAME");
            cmd.addReference("SIGN", "GETPERID/DATA");

            if (mgr.isEmpty(sRoleCode))
            {
                cmd = trans.addCustomFunction("GETROLE","Employee_Role_API.Get_Default_Role","ROLE_CODE");
                cmd.addParameter("COMPANY");
                cmd.addParameter("EMP_NO",emp_id);
            }

            if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
            {
                cmd = trans.addCustomFunction("EMPORGCONT","Employee_API.Get_Org_Contract","ITEM1_CONTRACT");
                cmd.addParameter("ITEM1_COMPANY");
                cmd.addParameter("EMP_NO",emp_id);
            }

            if (mgr.isEmpty(sOrgCode))
            {
                cmd = trans.addCustomFunction("GETORG","Employee_API.Get_Organization","ACTION");
                cmd.addParameter("COMPANY");
                cmd.addParameter("EMP_NO",emp_id);
            }

            double amount_ = mgr.readNumberValue("AMOUNT"); 
            if (isNaN(amount_))
                amount_ = 0;
            String amount1_ = mgr.getASPField("AMOUNT").formatNumber(amount_);

            double qty_ = mgr.readNumberValue("QTY"); 
            if (isNaN(qty_))
                qty_ = 0;
            String qty1_ = mgr.getASPField("QTY").formatNumber(qty_);

            cmd = trans.addCustomCommand("CALCAMOUNT","Work_Order_Coding_API.Calc_Amount");
            cmd.addParameter("AMOUNT",amount1_);
            cmd.addParameter("QTY",qty1_);
            if (mgr.isEmpty(sOrgCode))
                cmd.addReference("ACTION", "GETORG/DATA");
            else
                cmd.addParameter("ACTION", sOrgCode);
            if (mgr.isEmpty(sRoleCode))
                cmd.addReference("ROLE_CODE", "GETROLE/DATA");
            else
                cmd.addParameter("ROLE_CODE", sRoleCode);
            if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
                cmd.addReference("ITEM1_CONTRACT","EMPORGCONT/DATA");
            else
                cmd.addParameter("ITEM1_CONTRACT",sOrgContract);

            trans = mgr.validate(trans);

            String tpsign = trans.getValue("GETPERID/DATA/SIGN");
            tpname = trans.getValue("GETNAME/DATA/ITEM1_NAME");

            if (mgr.isEmpty(sRoleCode))
                tprolecode = trans.getValue("GETROLE/DATA/ROLE_CODE");
            else
                tprolecode = sRoleCode;

            if (mgr.isEmpty(sOrgCode))
                tporgcode = trans.getValue("GETORG/DATA/ACTION");
            else
                tporgcode = sOrgCode;

            if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
                sOrgContract = trans.getValue("EMPORGCONT/DATA/CONTRACT");

            tpamount = trans.getNumberValue("CALCAMOUNT/DATA/AMOUNT");
            if (isNaN(tpamount))
                tpamount=0;

            txt = (mgr.isEmpty(tpsign ) ? "" :tpsign ) + "^" + (mgr.isEmpty(tpname)? "" :tpname )+ "^" + (mgr.isEmpty(tprolecode ) ? "" :tprolecode )+ "^"+ (mgr.isEmpty(tporgcode ) ? "" :tporgcode )+ "^"+ (tpamount==0 ? mgr.getASPField("AMOUNT").formatNumber(0.0) :mgr.getASPField("AMOUNT").formatNumber(mgr.readNumberValue("AMOUNT")))+ "^"+
                  (mgr.isEmpty(sOrgContract ) ? "" :sOrgContract ) + "^" + (mgr.isEmpty(emp_id ) ? "" :emp_id ) + "^" ;

            mgr.responseWrite(txt);
        }
        else if ("ITEM1_CONTRACT".equals(val))
        {
            cmd = trans.addCustomFunction("GETCOMP","Site_API.Get_Company","ACTION");
            cmd.addParameter("ITEM1_CONTRACT");

            trans = mgr.validate(trans);

            String tpitemcompany = trans.getValue("GETCOMP/DATA/ACTION");

            txt = (mgr.isEmpty(tpitemcompany) ? "" :tpitemcompany);

            mgr.responseWrite(txt);
        }
        else if ("ITEM1_ORG_CODE".equals(val))
        {
            String reqstr = null;
            int startpos = 0;
            int endpos = 0;
            int i = 0;
            String ar[] = new String[6];
            String colOrgContract = "";
            String colOrgCode = "";

            String new_org_code = mgr.readValue("ITEM1_ORG_CODE","");

            if (new_org_code.indexOf("^",0)>0)
            {
                if (!mgr.isEmpty(mgr.readValue("ROLE_CODE")))
                {
                    for (i=0 ; i<5; i++)
                    {
                        endpos = new_org_code.indexOf("^",startpos);
                        reqstr = new_org_code.substring(startpos,endpos);
                        ar[i] = reqstr;
                        startpos= endpos+1;
                    }
                    colOrgCode = ar[3];
                    colOrgContract = ar[4];
                }
                else
                {
                    if (!mgr.isEmpty(mgr.readValue("EMP_NO")))
                    {
                        for (i=0 ; i<4; i++)
                        {
                            endpos = new_org_code.indexOf("^",startpos);
                            reqstr = new_org_code.substring(startpos,endpos);
                            ar[i] = reqstr;
                            startpos= endpos+1;
                        }
                        colOrgCode = ar[2];
                        colOrgContract = ar[3];
                    }
                    else
                    {
                        for (i=0 ; i<2; i++)
                        {
                            endpos = new_org_code.indexOf("^",startpos);
                            reqstr = new_org_code.substring(startpos,endpos);
                            ar[i] = reqstr;
                            startpos= endpos+1;
                        }
                        colOrgCode = ar[0];
                        colOrgContract = ar[1];
                    }
                }
            }
            else
            {
                colOrgCode = mgr.readValue("ITEM1_ORG_CODE");
                colOrgContract = mgr.readValue("ITEM1_CONTRACT"); 
            }


            cmd = trans.addCustomCommand("CALCAMOUNT","Work_Order_Coding_API.Calc_Amount");
            cmd.addParameter("AMOUNT",(mgr.isEmpty(mgr.readValue("AMOUNT")) ? "0" :mgr.readValue("AMOUNT")));
            cmd.addParameter("QTY",(mgr.isEmpty(mgr.readValue("QTY")) ? "0" :mgr.readValue("QTY")));
            cmd.addParameter("ITEM1_ORG_CODE",colOrgCode);
            cmd.addParameter("ROLE_CODE");
            cmd.addParameter("ITEM1_CONTRACT",colOrgContract);

            trans = mgr.validate(trans);

            tpamount = trans.getNumberValue("CALCAMOUNT/DATA/AMOUNT");
            if (isNaN(tpamount))
                tpamount=0;
            txt =  mgr.getASPField("AMOUNT").formatNumber(tpamount)+ "^" + (mgr.isEmpty(colOrgCode)? "" :colOrgCode) + "^"+ (mgr.isEmpty(colOrgContract)? "" :colOrgContract) + "^";

            mgr.responseWrite(txt);
        }
        else if ("ROLE_CODE".equals(val))
        {
            String reqstr = null;
            int startpos = 0;
            int endpos = 0;
            int i = 0;
            String ar[] = new String[6];

            String colRoleCode = "";
            String colOrgContract = "";
            String new_role_code = mgr.readValue("ROLE_CODE","");

            if (new_role_code.indexOf("^",0)>0)
            {
                if (!mgr.isEmpty(mgr.readValue("EMP_NO")))
                {
                    for (i=0 ; i<5; i++)
                    {
                        endpos = new_role_code.indexOf("^",startpos);
                        reqstr = new_role_code.substring(startpos,endpos);
                        ar[i] = reqstr;
                        startpos= endpos+1;
                    }
                    colRoleCode = ar[2];
                    colOrgContract = ar[4];
                }
                else
                {
                    if (!mgr.isEmpty(mgr.readValue("ITEM1_ORG_CODE")))
                    {
                        for (i=0 ; i<5; i++)
                        {
                            endpos = new_role_code.indexOf("^",startpos);
                            reqstr = new_role_code.substring(startpos,endpos);
                            ar[i] = reqstr;
                            startpos= endpos+1;
                        }
                        colRoleCode = ar[2];
                        colOrgContract = ar[4];
                    }
                    else
                    {
                        for (i=0 ; i<2; i++)
                        {
                            endpos = new_role_code.indexOf("^",startpos);
                            reqstr = new_role_code.substring(startpos,endpos);
                            ar[i] = reqstr;
                            startpos= endpos+1;
                        }
                        colRoleCode = ar[0];
                        colOrgContract = ar[1];
                    }
                }
            }
            else
            {
                colRoleCode = mgr.readValue("ROLE_CODE");
                colOrgContract = mgr.readValue("ITEM1_CONTRACT"); 
            }

            cmd = trans.addCustomCommand("CALCAMOUNT","Work_Order_Coding_API.Calc_Amount");
            cmd.addParameter("AMOUNT",(mgr.isEmpty(mgr.readValue("AMOUNT")) ? "0" :mgr.readValue("AMOUNT")));
            cmd.addParameter("QTY",(mgr.isEmpty(mgr.readValue("QTY")) ? "0" :mgr.readValue("QTY")));
            cmd.addParameter("ITEM1_ORG_CODE");
            cmd.addParameter("ROLE_CODE",colRoleCode);
            cmd.addParameter("ITEM1_CONTRACT",colOrgContract);

            trans = mgr.validate(trans);

            tpamount = trans.getNumberValue("CALCAMOUNT/DATA/AMOUNT");
            if (isNaN(tpamount))
                tpamount=0;
            txt =  mgr.getASPField("AMOUNT").formatNumber(tpamount)+ "^"+
                   (mgr.isEmpty(colOrgContract)? "" : colOrgContract) + "^" +
                   (mgr.isEmpty(colRoleCode)? "" : colRoleCode) + "^" ;

            mgr.responseWrite(txt);   
        }
        else if ("QTY".equals(val))
        {
            cmd = trans.addCustomCommand("CALCAMOUNT","Work_Order_Coding_API.Calc_Amount");
            cmd.addParameter("AMOUNT",(mgr.isEmpty(mgr.readValue("AMOUNT")) ? "0" :mgr.readValue("AMOUNT")));
            cmd.addParameter("QTY",(mgr.isEmpty(mgr.readValue("QTY")) ? "0" :mgr.readValue("QTY")));
            cmd.addParameter("ITEM1_ORG_CODE",mgr.readValue("ITEM1_ORG_CODE"));
            cmd.addParameter("ROLE_CODE",mgr.readValue("ROLE_CODE"));
            cmd.addParameter("ITEM1_CONTRACT");

            trans = mgr.validate(trans);

            tpamount = trans.getNumberValue("CALCAMOUNT/DATA/AMOUNT");
            if (isNaN(tpamount))
                tpamount=0;
            txt = mgr.getASPField("AMOUNT").formatNumber(tpamount);   
            mgr.responseWrite(txt);
        }
        else if ("ORG_CODE".equals(val))
        {
            cmd = trans.addCustomFunction("GETORGDESC","Organization_API.Get_Description","ORGCODEDESCRIPTION");
            cmd.addParameter("CONTRACT");
            cmd.addParameter("ORG_CODE");

            trans = mgr.validate(trans);

            String sOrdDesc = trans.getValue("GETORGDESC/DATA/ORGCODEDESCRIPTION");

            txt = (mgr.isEmpty(sOrdDesc) ? "" :sOrdDesc);

            mgr.responseWrite(txt);
        }

        mgr.endResponse();
    }

//-----------------------------------------------------------------------------
//-------------------------  CMDBAR EDIT FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------

    public void prepare()
    {
        ASPManager mgr = getASPManager();

        store();
        itemset1.last();
        errFlag = true;

        if (introFlag == 4 &&  ( mgr.isEmpty(itemset1.getRow().getValue("QTY")) ))
        {
            errFlag = false;
            itemset1.first();
        }

        if (introFlag == 4 &&  ( mgr.isEmpty(itemset1.getRow().getFieldValue("ITEM1_ORG_CODE")) ))
        {
            errFlag = false;
            itemset1.first();
        }

        if (introFlag == 4 &&  ( mgr.isEmpty(itemset1.getRow().getFieldValue("ITEM1_CONTRACT")) ))
        {
            errFlag = false;
            itemset1.first();
        }

        if (introFlag == 4 &&  ( mgr.isEmpty(itemset1.getRow().getValue("CRE_DATE")) ))
        {
            errFlag = false;
            itemset1.first();
        }

        if (errFlag)
        {
            trans.clear();

            buf = itemset1.getRow();

            cmd = trans.addCustomFunction("GETITNAME","Person_Info_API.Get_Name","ITEM1_NAME");
            cmd.addParameter("SIGN",itemset1.getRow().getValue("SIGN"));

            cmd = trans.addCustomFunction("GETSIG","Employee_API.Get_Signature","SIGN");
            cmd.addParameter("COMPANY",itemset1.getRow().getFieldValue("ITEM1_COMPANY"));
            cmd.addParameter("EMP_NO",itemset1.getRow().getValue("EMP_NO"));

            trans = mgr.perform(trans);
            tpname = trans.getValue("GETITNAME/DATA/ITEM1_NAME");
            String tpsign = trans.getValue("GETSIG/DATA/SIGN");

            buf.setValue("SIGN",tpsign);
            buf.setValue("ITEM1_NAME",tpname);
            buf.setValue("QTY","");
            buf.setValue("AMOUNT","");

            itemset1.addRow(buf);
        }

        assign();
        itemtbl1.fitToPage();
    }


    public void prepareITEM1()
    {
        ASPManager mgr = getASPManager();

        itemlay1.setLayoutMode(itemlay1.CUSTOM_LAYOUT);
        itemset1.changeRow();
        itemrolecode = itemset1.getRow().getValue("ROLE_CODE");
        itemorgcode = itemset1.getRow().getFieldValue("ITEM1_ORG_CODE");        
        itemset1.last();
        errFlag = true;

        if (introFlag == 4)
        {
            if (mgr.isEmpty(itemset1.getRow().getValue("QTY")))
            {
                errFlag = false;
                itemset1.first();
            }

            else if (mgr.isEmpty(itemset1.getRow().getFieldValue("ITEM1_ORG_CODE")))
            {
                errFlag = false;
                itemset1.first();
            }

            else if (mgr.isEmpty(itemset1.getRow().getFieldValue("ITEM1_CONTRACT")))
            {
                errFlag = false;
                itemset1.first();
            }

            else if (mgr.isEmpty(itemset1.getRow().getValue("CRE_DATE")))
            {
                errFlag = false;
                itemset1.first();
            }
        }

        if (errFlag)
        {
            cmd = trans.addEmptyCommand("ITEM1","WORK_ORDER_CODING_API.New__",itemblk1);
            cmd.setOption("ACTION","PREPARE");

            trans = mgr.perform(trans);

            data = trans.getBuffer("ITEM1/DATA");

            data.setValue("EMP_NO",headset.getRow().getValue("EMPNO"));
            data.setValue("SIGN",itemset1.getRow().getValue("SIGN"));
            data.setValue("ITEM_NAME",headset.getRow().getValue("NAME"));
            data.setValue("ITEM_COMPANY",headset.getRow().getValue("COMPANY"));
            data.setValue("CRE_DATE",headset.getRow().getValue("REAL_F_DATE"));
            data.setValue("ORG_CODE",itemorgcode);   
            data.setValue("ROLE_CODE",itemrolecode);
            itemset1.addRow(data);
        }
        itemblk1.generateAssignments();     
    }

    public void deleteITEM1()
    {

        itemset1.changeRow();

        if (( introFlag == 4 ) &&  ( itemset1.countRows() > 1 ))
        {
            itemset1.setRemoved();
            itemset1.previous();
        }

        else
        {
            itemset1.clear();
            introFlag = introFlag - 1;
        }
        itemblk1.generateAssignments();
    }


    public void previousForm()
    {
        ASPManager mgr = getASPManager();

        store();
        headset.changeRow();

        if (introFlag == 2)
        {
            errDesc = mgr.readValue("ERR_DESCR","");
            orgCode = mgr.readValue("ORG_CODE","");

            buf = mgr.newASPBuffer();
            buf.addFieldItem("REAL_S_DATE",mgr.readValue("REAL_S_DATE"));
            buf.addFieldItem("REAL_F_DATE",mgr.readValue("REAL_F_DATE"));
            realSdate = buf.getFieldValue("REAL_S_DATE") ;
            realFdate = buf.getFieldValue("REAL_F_DATE") ;

            buf = headset.getRow();
            buf.setValue ("TMP_ORG_CODE",orgCode);
            buf.setValue ("ORGCODEDESCRIPTION",mgr.readValue("ORGCODEDESCRIPTION",""));
            headset.addRow(buf);
        }

        if (introFlag == 3)
        {
            sympt = mgr.readValue("ERRSYMPT","");
            caus = mgr.readValue("ERRCAUSE");
            clas = mgr.readValue("ERRCLAS");
            typ = mgr.readValue("TYPE");
            disc = mgr.readValue("DISCOVER");
            peract = mgr.readValue("PERACT");
        }

        if (introFlag == 4)
        {
            itemset1.changeRow();

            emp_no = mgr.readValue("EMP_NO","");
            itemContract = mgr.readValue("ITEM1_CONTRACT","");
            itemorgcode =  mgr.readValue("ITEM1_ORG_CODE","");
            itemrolecode = mgr.readValue("ROLE_CODE",""); 
        }

        if (introFlag != 1)
            introFlag = introFlag - 1;

        assign();
    }


    public void nextForm()
    {
        ASPManager mgr = getASPManager();

        store();
        headset.changeRow ();                
        errFlag = true;

        if (introFlag == 1 &&  ( mgr.isEmpty(mgr.readValue("REPORTED_BY")) ))
        {
            errFlag = false;
        }

        if (introFlag == 1 &&  ( mgr.isEmpty(mgr.readValue("MCH_CODE")) ))
        {
            errFlag = false;
        }

        if (introFlag == 1 &&  ( mgr.isEmpty(mgr.readValue("MCH_CODE_CONTRACT")) ))
        {
            errFlag = false;
        }

        if (introFlag == 2 &&  ( mgr.isEmpty(mgr.readValue("ERR_DESCR")) ))
        {
            errFlag = false;
        }

        if (introFlag == 2 &&  ( mgr.isEmpty(mgr.readValue("ORG_CODE")) ))
        {
            errFlag = false;
        }

        if (introFlag == 2 &&  ( mgr.isEmpty(mgr.readValue("REAL_F_DATE")) ))
        {
            errFlag = false;
        }

        if (introFlag == 2 &&  ( mgr.isEmpty(mgr.readValue("CONTRACT")) ))
        {
            errFlag = false;
        }



        if (introFlag == 3)
        {
            if (mgr.isEmpty(mgr.readValue("WORK_DONE")))
            {
                errFlag = false;
            }
            sympt = mgr.readValue("ERRSYMPT","");
            caus = mgr.readValue("ERRCAUSE");
            clas = mgr.readValue("ERRCLAS");
            typ = mgr.readValue("TYPE");
            disc = mgr.readValue("DISCOVER");
            peract = mgr.readValue("PERACT");

        }

        if (introFlag == 4 &&  ( mgr.isEmpty(mgr.readValue("REAL_F_DATE")) ))
        {
            errFlag = false;
        }

        if (introFlag == 4)
        {
            mgr.showAlert(mgr.translate("PCMWQUICKREPORTTOHISTORYFINISHBUT: This is the last page"));
        }

        if (( errFlag ) &&  ( introFlag != 4 ))
        {
            introFlag = toDouble(introFlag) + 1;

            if (( introFlag < 4 ) ||  (( introFlag == 4 ) ))
            {
                prepareForm();
            }

            if (introFlag == 2 && countOfNext2== 0)
            {
                buf = headset.getRow();
                //buf.setValue("ERR_DESCR","Quick Reported");
                buf.setValue("ORG_CODE",headset.getRow().getValue("TMP_ORG_CODE"));
                headset.setRow(buf);                      
            }
            if (introFlag == 2)
                countOfNext2 = 1;

        }
        assign();

    }
    //Bug 79242, Start
    public void addPreposting()
    {
        ASPManager mgr = getASPManager();
	int enabl10 = 0;
	
	if (headset.countRows()>0)
	    headset.changeRow();

	if (itemset1.countRows()>0)
	    itemset1.changeRow();

        if (introFlag == 3)
        {
            sympt = mgr.readValue("ERRSYMPT","");
            caus = mgr.readValue("ERRCAUSE");
            clas = mgr.readValue("ERRCLAS");
            typ = mgr.readValue("TYPE");
            disc = mgr.readValue("DISCOVER");
            peract = mgr.readValue("PERACT");
        }

	if (mgr.isEmpty(sTempPrePostId))
	{
	    trans.clear();

	    cmd = trans.addCustomCommand("ADDTEMPPREPOST","Active_Separate_API.Add_Temp_Pre_Posting");
	    cmd.addParameter("PRE_ACCOUNTING_ID","");
	    cmd.addParameter("CONTRACT",headset.getValue("CONTRACT"));
	    cmd.addParameter("MCH_CODE_CONTRACT",headset.getValue("MCH_CODE_CONTRACT"));
	    cmd.addParameter("MCH_CODE",headset.getValue("MCH_CODE"));

	    trans = mgr.perform(trans);

	    sTempPrePostId = trans.getValue("ADDTEMPPREPOST/DATA/PRE_ACCOUNTING_ID");
	}

	urlString = "../mpccow/PreAccountingDlg1.page?PRE_ACCOUNTING_ID=" + sTempPrePostId +
		    "&STRING_CODE=T50"+
		    "&CONTRACT=" + headset.getValue("CONTRACT")+
		    "&ENABL10=" + Integer.toString(enabl10);

	bOpenNewWindow = true;
	newWinHandle = "";
    }
    //Bug 79242, End


    public void finishForm()
    {   
        int count;
        ASPManager mgr = getASPManager();
        store();

        errFlag = true;
        boolean errFlag2 = true;
        String sAttr = "";
        String sMchCode = "";
        String sdate = "";
        String objid="";
        String objversion = "";
        String objid1="";
        String objversion1="";
        String sComment = "";
        String sWoType="";
        String error_codes = "";

        if (introFlag == 1)
        {
            mgr.showAlert(mgr.translate("PCMWQUICKREPORTTOHISTORYDIRNULL: Go to next page to finish"));    
            errFlag = false;
        }

        if (introFlag == 2 &&  ( mgr.isEmpty(mgr.readValue("ERR_DESCR")) ))
        {
            errFlag = false;
        }

        if (introFlag == 2 &&  ( mgr.isEmpty(mgr.readValue("ORG_CODE")) ))
        {
            errFlag = false;
        }

        if (introFlag == 2 &&  ( mgr.isEmpty(mgr.readValue("REAL_F_DATE")) ))
        {
            errFlag = false;
        }

        if (introFlag == 3 &&  ( mgr.isEmpty(mgr.readValue("WORK_DONE")) ))
        {
            errFlag = false;
        }

        if (mgr.isEmpty(mgr.readValue("REAL_S_DATE")))
            sdate = "";
        else
            sdate  = headset.getRow().getValue("REAL_S_DATE");
        if(mgr.isEmpty(mgr.readValue("WORK_TYPE_ID")))
	    sWoType="";
	else
	    sWoType=headset.getRow().getValue("WORK_TYPE_ID");


        if ((introFlag >= 2) && (errFlag))
        {

            empno=headset.getRow().getValue("EMPNO");
	    sAttr = sAttr + "PM_TYPE" + (char)31 +headset.getRow().getValue("PM_TYPE")+ (char)30
                    + "REG_DATE" + (char)31 +headset.getRow().getValue("REG_DATE")+ (char)30
                    + "REPORTED_BY_ID" + (char)31 +empno+ (char)30
                    + "CONTRACT" + (char)31 +headset.getRow().getValue("CONTRACT")+ (char)30
                    + "MCH_CODE_CONTRACT" + (char)31 +headset.getRow().getValue("MCH_CODE_CONTRACT")+ (char)30
                    + "COMPANY" + (char)31 +headset.getRow().getValue("COMPANY")+ (char)30
                    + "MCH_CODE" + (char)31 +headset.getRow().getValue("MCH_CODE")+(char)30
                    + "ORG_CODE" + (char)31 +headset.getRow().getValue("ORG_CODE")+ (char)30
                    + "ERR_DESCR" + (char)31 +headset.getRow().getValue("ERR_DESCR")+ (char)30
		    + "WORK_TYPE_ID" + (char)31 +sWoType+ (char)30
                    + "REAL_S_DATE" + (char)31 +sdate+ (char)30
                    + "REAL_F_DATE" + (char)31 +headset.getRow().getValue("REAL_F_DATE")+ (char)30;

            sMchCode =  headset.getRow().getValue("MCH_CODE");
            if (introFlag >= 3)
            {
                if (introFlag == 3)
                {
                    sympt = mgr.readValue("ERR_SYMPTOM","");
                    caus = mgr.readValue("ERR_CAUSE");
                    clas = mgr.readValue("ERR_CLASS");
                    typ = mgr.readValue("ERR_TYPE");
                    disc = mgr.readValue("ERR_DISCOVER_CODE","");
                    peract = mgr.readValue("PERFORMED_ACTION_ID","");

                }

                String performed_action_lo=headset.getRow().getValue("PERFORMED_ACTION_LO");
                if (mgr.isEmpty(performed_action_lo))
                    performed_action_lo="";
                String err_symptom=headset.getRow().getValue("ERR_SYMPTOM");

                if (mgr.isEmpty(err_symptom))
                    err_symptom="";
                String err_cause=headset.getRow().getValue("ERR_CAUSE");
                if (mgr.isEmpty(err_cause))
                    err_cause="";
                String err_class=headset.getRow().getValue("ERR_CLASS");
                if (mgr.isEmpty(err_class))
                    err_class="";
                String err_type=headset.getRow().getValue("ERR_TYPE");
                if (mgr.isEmpty(err_type))
                    err_type="";
                String err_discover_code=headset.getRow().getValue("ERR_DISCOVER_CODE");
                if (mgr.isEmpty(err_discover_code))
                    err_discover_code="";
                String performed_action_id=headset.getRow().getValue("PERFORMED_ACTION_ID");
                if (mgr.isEmpty(performed_action_id))
                    performed_action_id="";

                sAttr = sAttr + "WORK_DONE" +(char)31 +headset.getRow().getValue("WORK_DONE")+ (char)30
                        + "PERFORMED_ACTION_LO" + (char)31 +performed_action_lo+ (char)30
                        + "ERR_SYMPTOM" + (char)31 +err_symptom+ (char)30
                        + "ERR_CAUSE" + (char)31 +err_cause+ (char)30
                        + "ERR_CLASS" + (char)31 +err_class+ (char)30
                        + "ERR_TYPE" + (char)31 +err_type+ (char)30
                        + "ERR_DISCOVER_CODE" + (char)31 +err_discover_code+ (char)30
                        + "PERFORMED_ACTION_ID" + (char)31 +performed_action_id+ (char)30;

            }


            sWoNo = ctx.getGlobal("WO_NO");
            if (("0".equals(sWoNo)))
            {   
                    int beg_pos;
                    int end_pos;
                    cmd = trans.addCustomCommand("MODIFRM", "ACTIVE_SEPARATE_API.New__"); 
                    cmd.addParameter("INFO");    
                    cmd.addParameter("OBJID");
                    cmd.addParameter("OBJVERSION");
                    cmd.addParameter("ATTR",sAttr);
                    cmd.addParameter("ACTION","DO");
                    trans = mgr.perform(trans);
    
                    sWoNo = trans.getValue("MODIFRM/DATA/ATTR");
                    objid = trans.getValue("MODIFRM/DATA/OBJID");
                    objversion = trans.getValue("MODIFRM/DATA/OBJVERSION");

                    ctx.setGlobal("OBJ_ID", objid);
                    ctx.setGlobal("OBJ_VER", objversion);
                    /*cmd = trans.addCustomCommand("WOK", "ACTIVE_SEPARATE_API.WORK__"); 
                    cmd.addParameter("INFO");    
                    cmd.addParameter("OBJID",objid);
                    cmd.addParameter("OBJVERSION",objversion);
                    cmd.addParameter("ATTR");
                    cmd.addParameter("ACTION","DO");
                    trans = mgr.perform(trans);
                    objversion = trans.getValue("WOK/DATA/OBJVERSION");*/
    
                    beg_pos = sWoNo.indexOf("W"); 
                    end_pos = sWoNo.indexOf("F");
                    sWoNo = sWoNo.substring(beg_pos,end_pos);
                    sWoNo = sWoNo.substring((sWoNo.indexOf((char)31) + 1), sWoNo.indexOf((char)30));
                    ctx.setGlobal("WO_NO", sWoNo);

                    }
                    //Bug 79242, Start
                    if (!(mgr.isEmpty(sWoNo) || mgr.isEmpty(sTempPrePostId)))
	            {
                      trans.clear();
    
                      cmd = trans.addCustomCommand("COPYPREPOST","Active_Separate_API.Copy_Temp_Pre_Posting");
		      cmd.addParameter("WO_NO", sWoNo);
		      cmd.addParameter("PRE_ACCOUNTING_ID", sTempPrePostId);
    
                    trans = mgr.perform(trans);
    
                    }
                   //Bug 79242, End
                if (introFlag == 4)
                {
                    itemset1.changeRow();

                    if (mgr.isEmpty(itemset1.getRow().getValue("ORG_CODE")))
                    {
                        errFlag = false;
                        itemset1.first();
                    }

                    if (mgr.isEmpty(itemset1.getRow().getValue("QTY")))
                    {
                        errFlag = false;
                        itemset1.first();
                    }

                    if (mgr.isEmpty(itemset1.getRow().getFieldValue("ITEM1_CONTRACT")))
                    {
                        errFlag = false;
                        itemset1.first();
                    }

                    if (mgr.isEmpty(itemset1.getRow().getValue("CRE_DATE")))
                    {
                        errFlag = false;
                        itemset1.first();
                    }
                }                
                  if (( introFlag >= 3 ) &&  !( mgr.isEmpty(sWoNo) ) && errFlag  && (countOfNext>0))
                   {   
                       if (itemset1.countRows()>0)
                       {   
                            itemset1.changeRow();
                            trans.clear();
    
                            cmd = trans.addCustomFunction("ACCTYPE","Work_Order_Account_Type_API.Get_Client_Value","ACCOUNT_TYPE");
                            cmd.addParameter("NUM_ZERO","0");
    
                            cmd = trans.addCustomFunction("COSTTYPE","Work_Order_Cost_Type_API.Get_Client_Value","COST_TYPE");
                            cmd.addParameter("NUM_ZERO","0");
    
                            cmd = trans.addCustomFunction("AUTHSIG","Maintenance_Configuration_API.Get_Auth_Def_Sign","SIGNATURE");
    
                            cmd = trans.addCustomFunction("BOOKSTATUS","Work_Order_Book_Status_API.Get_Client_Value","BOOK_STATUS");
                            cmd.addParameter("NUM_ZERO","0");
    
                            trans = mgr.perform(trans);
    
                            String sAccountType = trans.getValue("ACCTYPE/DATA/ACCOUNT_TYPE");
                            String sCostType = trans.getValue("COSTTYPE/DATA/COST_TYPE");
                            String sSignature = trans.getValue("AUTHSIG/DATA/SIGNATURE");
                            String sBookStatus = trans.getValue("BOOKSTATUS/DATA/BOOK_STATUS");
    
                            assign();
    		            //Bug 79242, Start
                            trans.clear();
		            //Bug 79242, End
    
                            
    
                            int n = itemset1.countRows();
                            itemset1.first();
    
                            for (count=1; count<=n; ++count)
                            {
                                errFlag2 = true;
    
                                if (mgr.isEmpty(itemset1.getRow().getValue("ORG_CODE")))
                                    errFlag2 = false;
    
    
                                if (mgr.isEmpty(itemset1.getRow().getValue("QTY")))
                                    errFlag2 = false;
    
                                if (mgr.isEmpty(itemset1.getRow().getFieldValue("ITEM1_CONTRACT")))
                                    errFlag2 = false;
    
    
                                if (mgr.isEmpty(itemset1.getRow().getValue("CRE_DATE")))
                                    errFlag2 = false;
    
                                if (errFlag2)
                                {
                                    String emp_no=itemset1.getRow().getValue("EMP_NO");
                                    if (mgr.isEmpty(emp_no))
                                    {
                                        trans.clear();
                                        cmd = trans.addCustomFunction("EMPID","COMPANY_EMP_API.Get_Max_Employee_Id","EMPNO");
                                        cmd.addParameter("COMPANY",itemset1.getRow().getFieldValue("ITEM1_COMPANY"));
                                        cmd.addParameter("REPORTED_BY",headset.getRow().getValue("REPORTED_BY"));
                                        trans = mgr.perform(trans);
                                        emp_no = trans.getValue("EMPID/DATA/EMPNO");
    
                                        if (mgr.isEmpty(emp_no))
                                            emp_no="";
                                    }
                                    String role_code=itemset1.getRow().getValue("ROLE_CODE");
                                    if (mgr.isEmpty(role_code))
                                        role_code="";
                                    String itemName_ = itemset1.getRow().getValue("ITEM1_NAME");
                                    String roleCode_ = itemset1.getRow().getValue("ROLE_CODE");
                                    if (mgr.isEmpty (roleCode_))
                                        roleCode_ = "";
                                    if (mgr.isEmpty(itemName_))
                                        sComment = itemset1.getRow().getValue("ORG_CODE") + " (" + roleCode_ + ")" + ".";
                                    else
                                        sComment = itemName_ + ", " + itemset1.getRow().getValue("ORG_CODE") + " (" + roleCode_ + ")" + ".";
                                    sAttr = "";
                                    sAttr = sAttr + "CMNT" + (char)31 +sComment+(char)30
                                            + "EMP_NO" + (char)31 +emp_no+ (char)30
                                            + "WORK_ORDER_COST_TYPE" + (char)31 +sCostType+ (char)30
                                            + "WORK_ORDER_ACCOUNT_TYPE" + (char)31 +sAccountType+ (char)30
                                            + "SIGNATURE" + (char)31 +sSignature+ (char)30
                                            + "SIGNATURE_ID" + (char)31 +sSignature+ (char)30
                                            + "WORK_ORDER_BOOK_STATUS" + (char)31 +sBookStatus+ (char)30
                                            + "CONTRACT" + (char)31 +itemset1.getRow().getFieldValue("ITEM1_CONTRACT")+ (char)30
                                            + "COMPANY" + (char)31 +itemset1.getRow().getFieldValue("ITEM1_COMPANY")+ (char)30
                                            + "ORG_CODE" + (char)31 +itemset1.getRow().getFieldValue("ITEM1_ORG_CODE")+ (char)30
                                            + "CRE_DATE" + (char)31 +itemset1.getRow().getValue("CRE_DATE")+ (char)30
                                            + "MCH_CODE" + (char)31 +headset.getRow().getValue("MCH_CODE")+ (char)30
                                            + "MCH_CODE_CONTRACT" + (char)31 +headset.getRow().getValue("MCH_CODE_CONTRACT")+ (char)30
                                            + "ROLE_CODE" + (char)31 +role_code+ (char)30
                                            + "QTY" + (char)31 +itemset1.getRow().getValue("QTY")+ (char)30
                                            + "WO_NO" + (char)31 +sWoNo+ (char)30 
                                            + "AMOUNT" + (char)31 +itemset1.getRow().getValue("AMOUNT")+ (char)30;
                                    
                                    //Bug 58197, start
                                    if (itemset1.getRow().getNumberValue("QTY") > 0) {
				    //Bug 79242, Start
				        cmd = trans.addCustomCommand("HISTSEPNEW"+count,"Work_Order_Coding_API.New__");
                                        cmd.addParameter("INFO","");
                                        cmd.addParameter("OBJID",objid1);
                                        cmd.addParameter("OBJVERSION",objversion1);
                                        cmd.addParameter("ATTR",sAttr);
                                        cmd.addParameter("ACTION","DO");
    
                                       	//Bug 79242, End
                                    }
                                    //Bug 58197, end
                                }
                                itemset1.next();
                            }
                              //Bug 79242, Start
		               trans = mgr.perform(trans);
		             //Bug 79242, End
                        }

                    }


                if (( introFlag >= 3 ) &&  !( mgr.isEmpty(sWoNo) ) && errFlag)
                {
                    sAlertMessage = mgr.translate("PCMWQUICKREPORTTOHISTORYWOCREATED: Work Order &1 has been created.", sWoNo);
                    ctx.setGlobal("SALERT",sAlertMessage);
    
                    objid =  ctx.getGlobal("OBJ_ID");
                    objversion  =  ctx.getGlobal("OBJ_VER");
                    trans.clear();
                    cmd = trans.addCustomCommand("FINI", "ACTIVE_SEPARATE_API.FINISH__"); 
                    cmd.addParameter("INFO");    
                    cmd.addParameter("OBJID",objid);
                    cmd.addParameter("OBJVERSION",objversion);
                    cmd.addParameter("ATTR");
                    cmd.addParameter("ACTION","DO"); 
    
                    trans=mgr.perform(trans);
                    cancelForm();
                }
            }
        
        assign();
    }

    public void cancelForm()
    {

       ASPManager mgr = getASPManager();

       trans.clear();
       	cmd = trans.addCustomCommand("DELPREPOST","Active_Separate_API.Delete_Temp_Pre_Posting");
	cmd.addParameter("PRE_ACCOUNTING_ID", sTempPrePostId);

    
        trans=mgr.perform(trans);
        //Bug 79242, End
        closeWin = "TRUE";
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void clear()
    {

        headset.clear();
        headtbl.clearQueryRow();
        itemset1.clear();
        itemtbl1.clearQueryRow();
        assign();
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR BROWSE FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void backwardITEM()
    {
        ASPManager mgr = getASPManager();

        itemset1.changeRow();
        if (!itemset1.previous())
            mgr.showAlert(mgr.translate("PCMWQUICKREPORTTOHISTORYFIRSTROW: This is the first line."));
        assign();
    }


    public void forwardITEM()
    {
        ASPManager mgr = getASPManager();

        itemset1.changeRow();
        if (!itemset1.next())
            mgr.showAlert(mgr.translate("PCMWQUICKREPORTTOHISTORYLASTROW: This is the last line."));
        assign();
    }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

    public void prepareForm()
    {
        ASPManager mgr = getASPManager();

        store();

        if (( introFlag == 1 ))
        {
            cmd = trans.addEmptyCommand("HEAD","HISTORICAL_SEPARATE_API.New__",headblk);
            cmd.setOption("ACTION","PREPARE");

            cmd = trans.addCustomFunction("GETCOMP2","Site_API.Get_Company","COMPANY");
            cmd.addReference("CONTRACT","HEAD/DATA");

            cmd = trans.addQuery("REPBY","SELECT Fnd_Session_API.Get_Fnd_User FROM DUAL");

            cmd = trans.addCustomFunction("PERSONINFO", "Person_Info_API.Get_Id_For_User", "USERID" );
            cmd.addReference("GET_FND_USER","REPBY/DATA");

            if (countOfNext1 == 0)
            {
                cmd = trans.addCustomFunction("GETNAME","PERSON_INFO_API.Get_Name","NAME");
                cmd.addReference("USERID","PERSONINFO/DATA");                                      

                cmd = trans.addCustomFunction("EMPID","COMPANY_EMP_API.Get_Max_Employee_Id","EMPNO");
                cmd.addReference("COMPANY","GETCOMP2/DATA");
                cmd.addReference("USERID","PERSONINFO/DATA");

                cmd = trans.addCustomFunction("HGETORG","Employee_API.Get_Organization","ORG_CODE");
                cmd.addReference("COMPANY","GETCOMP2/DATA");
                cmd.addReference("EMPNO","EMPID/DATA");      

                cmd = trans.addCustomFunction("ORGDESC","Organization_Api.Get_Description","ORGCODEDESCRIPTION");
                cmd.addReference("CONTRACT","HEAD/DATA");
                cmd.addReference("ORG_CODE","HGETORG/DATA");
            }
            else
            {
                String employeeNo = mgr.readValue("EMPNO");
                cmd = trans.addCustomFunction("HGETORG","Employee_API.Get_Organization","ORG_CODE");
                cmd.addReference("COMPANY","GETCOMP2/DATA");
                cmd.addParameter("EMP_NO",employeeNo);
            }

        }

        if (introFlag == 4)
        {
            ASPBuffer row = null;
            int count;
            if (countOfNext == 0)
            {
                cmd = trans.addEmptyCommand("ITEM1","WORK_ORDER_CODING_API.New__",itemblk1);
                cmd.setOption("ACTION","PREPARE");

                getDefault(true);


            }
            else
            {
                if (itemset1.countRows()==0)
                {
                    cmd = trans.addEmptyCommand("ITEM1","WORK_ORDER_CODING_API.New__",itemblk1);
                    cmd.setOption("ACTION","PREPARE");

                    getDefault(true); countOfNext =0;
                }
            }
        }

        trans = mgr.perform(trans);

        if (introFlag == 1)
        {
            String repBy = trans.getValue("PERSONINFO/DATA/USERID");

            data = trans.getBuffer("HEAD/DATA");
            if (countOfNext1 == 0)
            {
                String horgCode = trans.getValue("HGETORG/DATA/ORG_CODE");
                String orgdesc =  trans.getValue("ORGDESC/DATA/ORGCODEDESCRIPTION");
                String hempName = trans.getValue("GETNAME/DATA/NAME");
                String hempId = trans.getValue("EMPID/DATA/EMPNO");
                String mchCodeContract = trans.getValue("HEAD/DATA/CONTRACT");

                data.setFieldItem("REPORTED_BY",repBy);
                data.setFieldItem("ORGCODEDESCRIPTION",orgdesc);
                data.setFieldItem("TMP_ORG_CODE",horgCode);
                data.setFieldItem("EMPNO",hempId);
                data.setFieldItem("NAME",hempName);
                data.setFieldItem("MCH_CODE_CONTRACT",mchCodeContract);
                countOfNext1 = 1;
            }

            headset.addRow(data);
        }

        if (introFlag == 2)
        {
            ASPBuffer row = null;

            String horgcode = trans.getValue("HGETORG/DATA/ORG_CODE");
            if (countOfNext2 >0)
            {
                row = headset.getRow();
                row.setValue("ERR_DESCR",errDesc);
                row.setValue("ORG_CODE",mgr.readValue("TMP_ORG_CODE"));
                row.setFieldItem("REAL_S_DATE",realSdate);
                row.setFieldItem("REAL_F_DATE",realFdate);
                headset.setRow(row);
            }
        }

        if (introFlag == 4)
        {
            if (   countOfNext    ==    0)
            {

                data = trans.getBuffer("ITEM1/DATA");
                //Bug 72265, start
                if (mgr.isEmpty(empno)) {
                    empno = headset.getRow().getValue("EMPNO");
                }
                trans.clear();
                cmd = trans.addCustomFunction("GETROLE","Employee_Role_API.Get_Default_Role","ROLE_CODE");
                cmd.addParameter("COMPANY");
                cmd.addParameter("EMP_NO",empno);
                trans = mgr.perform(trans);
                tprolecode = trans.getValue("GETROLE/DATA/ROLE_CODE");
                //Bug 72265, end
                data.setValue("EMP_NO",headset.getRow().getValue("EMPNO"));
                data.setValue("SIGN",headset.getRow().getValue("REPORTED_BY"));
                data.setValue("ITEM1_NAME",headset.getRow().getValue("NAME"));
                data.setValue("COMPANY",headset.getRow().getValue("COMPANY"));
                data.setValue("CRE_DATE",headset.getRow().getValue("REAL_F_DATE"));
                data.setValue("ROLE_CODE",tprolecode);
                data.setValue("ORG_CODE",headset.getRow().getValue("TMP_ORG_CODE"));

                itemset1.addRow(data);
                countOfNext =1;

            }
            else

            {
                if (itemset1.countRows()>0)
                    itemset1.last();
            }
        }
        assign();
    }


    public void getDefault(boolean typeFlag) 
    {

        if (typeFlag)
        {
            cmd = trans.addCustomFunction("GETROLE","Employee_Role_API.Get_Default_Role","ROLE_CODE");
            cmd.addParameter("COMPANY");
            cmd.addParameter("EMP_NO",empno);

            cmd = trans.addCustomFunction("GETORG","Employee_API.Get_Organization","ACTION");
            cmd.addParameter("COMPANY");
            cmd.addParameter("EMP_NO",empno);
        }
    }

//-----------------------------------------------------------------------------
//----------------------------  SELECT FUNCTION  ------------------------------
//-----------------------------------------------------------------------------

    public void select()
    {
        store();
        itemset1.switchSelections();
        assign();
    }

//-------------------------------------------------------
//  Populate List Boxes
//-------------------------------------------------------

    public void populateListBoxes()
    {
        ASPManager mgr = getASPManager();

        trans.clear();

        trans.addQuery("SYMT","select A.ERR_SYMPTOM,A.ERR_SYMPTOM,' - ',A.DESCRIPTION FROM WORK_ORDER_SYMPT_CODE A");
        trans.addQuery("ERROR","select A.ERR_CAUSE,A.ERR_CAUSE,' - ',A.DESCRIPTION FROM MAINTENANCE_CAUSE_CODE A");
        trans.addQuery("ERRORCLS","select A.ERR_CLASS,A.ERR_CLASS,' - ',A.DESCRIPTION FROM WORK_ORDER_CLASS_CODE A");
        trans.addQuery("ERRORTYPE","select A.ERR_TYPE,A.ERR_TYPE,' - ',A.DESCRIPTION FROM WORK_ORDER_TYPE_CODE A");
        trans.addQuery("ERRORDISC","select A.ERR_DISCOVER_CODE,A.ERR_DISCOVER_CODE,' - ',A.DESCRIPTION FROM WORK_ORDER_DISC_CODE A");
        trans.addQuery("PERACT","select A.PERFORMED_ACTION_ID,A.PERFORMED_ACTION_ID,' - ',A.DESCRIPTION FROM MAINTENANCE_PERF_ACTION A");

        trans = mgr.perform(trans);

        symptBuff = trans.getBuffer("SYMT");
        errorBuff = trans.getBuffer("ERROR");
        errclassBuff = trans.getBuffer("ERRORCLS");
        errtypeBuff = trans.getBuffer("ERRORTYPE");
        errdiscBuff = trans.getBuffer("ERRORDISC");
        peractBuff = trans.getBuffer("PERACT");

        trans.clear();

    }

    public void preDefine()
    {
        ASPManager mgr = getASPManager();

        headblk = mgr.newASPBlock("HEAD");

        f = headblk.addField("OBJID");
        f.setHidden();

        f = headblk.addField("OBJVERSION");
        f.setHidden();

        f = headblk.addField("INFO");
        f.setHidden();

        f = headblk.addField("ATTR");
        f.setHidden();

        f = headblk.addField("ACTION");
        f.setHidden();

        f = headblk.addField("REPORTED_BY");
        f.setSize(14);
        f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,445);
        f.setCustomValidation("REPORTED_BY,COMPANY,REG_DATE,ORG_CODE","NAME,EMPNO,SYSDATE,TMP_ORG_CODE,ORGCODEDESCRIPTION");
        f.setMandatory();
        f.setLabel("PCMWQUICKREPORTTOHISTORYREPBY: Signature:");
        f.setUpperCase();

        f = headblk.addField("USERID");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("GET_FND_USER");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("NAME");
        f.setHidden();
        f.setFunction("PERSON_INFO_API.Get_Name(:REPORTED_BY)");

        f = headblk.addField("MCH_CODE");
        f.setSize(14);
        f.setMaxLength(100);
        f.setLOV("MaintenanceObjectLov1.page","MCH_CODE_CONTRACT CONTRACT",600,450);
        f.setMandatory();
        f.setLabel("PCMWQUICKREPORTTOHISTORYMCHCODE: Object ID:");
        f.setUpperCase();
        f.setCustomValidation("MCH_CODE_CONTRACT, MCH_CODE","MCHNAME,MCH_CODE_CONTRACT,MCH_CODE");

        f = headblk.addField("MCHNAME");
        f.setSize(30);   
        f.setLabel("PCMWQUICKREPORTTOHISTORYMCHNAME: Object Description:");
        f.setReadOnly();
        f.setMaxLength(45);
        f.setFunction("''");

        f = headblk.addField("MCH_CODE_CONTRACT");
        f.setSize(14);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setMandatory();
        f.setLabel("PCMWQUICKREPORTTOHISTORYCONT: Site:");
        f.setUpperCase();

        f = headblk.addField("EMPNO");
        f.setSize(6);
        f.setHidden();
        f.setLabel("PCMWQUICKREPORTTOHISTORYEMPNO: Quick Report In of Work:");
        f.setFunction("COMPANY_EMP_API.Get_Max_Employee_Id(:COMPANY,:REPORTED_BY)");

        f = headblk.addField("PM_TYPE");
        f.setHidden();

        f = headblk.addField("SYSDATE","Datetime");
        f.setHidden();
        f.setFunction("''");

        f = headblk.addField("COMPANY");
        f.setHidden();
        f.setUpperCase();

        f = headblk.addField("REG_DATE","Datetime");
        f.setHidden();


        //====================================  Directive  =====================================================

        f = headblk.addField("ERR_DESCR");
        f.setSize(40);
        f.setMaxLength(40);
        f.setMandatory();
        f.setLabel("PCMWQUICKREPORTTOHISTORYERRDESCR: Directive:");

        f = headblk.addField("CONTRACT");
        f.setSize(14);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setCustomValidation("CONTRACT","COMPANY");
        f.setMandatory();
        f.setLabel("PCMWQUICKREPORTTOHISTORYCONT2: Site:");
        f.setUpperCase();

        f = headblk.addField("ORG_CODE");
        f.setSize(25);
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,445);
        f.setMandatory();
        f.setLabel("PCMWQUICKREPORTTOHISTORYORGCODE: Maintenance Organization:");
        f.setUpperCase();
        f.setCustomValidation("CONTRACT,ORG_CODE","ORGCODEDESCRIPTION");

        f = headblk.addField("ORGCODEDESCRIPTION");
        f.setSize(20);
        f.setFunction("Organization_API.Get_Description(:CONTRACT,:ORG_CODE)");
        f.setReadOnly();

        f = headblk.addField("WORK_TYPE_ID");
	f.setSize(11);
	f.setDynamicLOV("WORK_TYPE",600,445);
        f.setLabel("QUICKWORKTYPEID: Work Type:");
	f.setUpperCase();
	f.setMaxLength(20);

        f = headblk.addField("TMP_ORG_CODE");
        f.setHidden();
        f.setUpperCase();

        f = headblk.addField("REAL_S_DATE","Datetime");
        f.setSize(22);
        f.setLabel("PCMWQUICKREPORTTOHISTORYRSDATE: Actual Start:");
        f.setCustomValidation("REAL_S_DATE","REAL_S_DATE");

        f = headblk.addField("REAL_F_DATE","Datetime");
        f.setSize(22);
        f.setMandatory();
        f.setLabel("PCMWQUICKREPORTTOHISTORYRFDATE: Actual Completion:");
        f.setCustomValidation("REAL_F_DATE","REAL_F_DATE");


        //============================  Classification  =====================================================


        f = headblk.addField("WORK_DONE");
        f.setSize(41);
        f.setMaxLength(60);
        f.setMandatory();
        f.setLabel("PCMWQUICKREPORTTOHISTORYWORKDONE: Work Done:");

        f = headblk.addField("PERFORMED_ACTION_LO");
        f.setSize(40);
        f.setMaxLength(2000);
//  		Bug 69168, Start
        f.setLabel(" ");
        f.setHeight(2);
//  		Bug 69168, End
        
        f = headblk.addField("ERR_SYMPTOM");
        f.setSize(6);
        f.setLabel("PCMWQUICKREPORTTOHISTORYERRSYMPTOM: Symptom:");
        f.setUpperCase();
        f.setHidden();

        f = headblk.addField("ERR_CLASS");
        f.setSize(6);
        f.setLabel("PCMWQUICKREPORTTOHISTORYERRCLASS: Class:");
        f.setUpperCase();
        f.setHidden();

        f = headblk.addField("ERR_DISCOVER_CODE");
        f.setSize(6);
        f.setLabel("PCMWQUICKREPORTTOHISTORYERRDISCODE: Discovery:");
        f.setUpperCase();
        f.setHidden();

        f = headblk.addField("ERR_CAUSE");
        f.setSize(6);
        f.setLabel("PCMWQUICKREPORTTOHISTORYERRCAUSE: Cause:");
        f.setUpperCase();
        f.setHidden();

        f = headblk.addField("ERR_TYPE");
        f.setSize(6);
        f.setLabel("PCMWQUICKREPORTTOHISTORYERRTYPE: Type:");
        f.setUpperCase();
        f.setHidden();

        f = headblk.addField("PERFORMED_ACTION_ID");
        f.setSize(6);
        f.setLabel("PCMWQUICKREPORTTOHISTORYPACTID: Performed Action:");
        f.setUpperCase();
        f.setHidden();

        f = headblk.addField("PRE_ACCOUNTING_ID", "Number");
        f.setHidden();

        headblk.addField("CODE_C").
        setUpperCase().
        setHidden();

        headblk.addField("CODE_D").
        setUpperCase().
        setHidden();

        headblk.addField("CODE_E").
        setFunction("''").    
        setHidden();

        headblk.addField("CODE_F").
        setFunction("''").    
        setHidden();

        headblk.addField("CODE_G").
        setUpperCase().
        setHidden();

        headblk.addField("CODE_H").
        setUpperCase().
        setHidden();

        headblk.addField("CODE_I").
        setUpperCase().
        setHidden();

        headblk.addField("CODE_J").
        setUpperCase().
        setHidden(); 
        
        headblk.addField("WO_NO","Number","#").
        setHidden(); 

        headblk.setView("ACTIVE_SEPARATE");
        headblk.defineCommand("ACTIVE_SEPARATE_API","New__,Modify__,Remove__");
        headset = headblk.getASPRowSet();
        headblk.setTitle(mgr.translate("PCMWQUICKREPORTTOHISTORYMASTERTITLE: Quick Report in Work Wizard"));

        headbar = mgr.newASPCommandBar(headblk);
        headbar.disableCommand(headbar.SAVERETURN);
        headbar.disableCommand(headbar.CANCELEDIT);
        headbar.disableCommand(headbar.FORWARD);
        headbar.disableModeLabel();

        headtbl = mgr.newASPTable(headblk);
        headtbl.setWrap();

        headlay = headblk.getASPBlockLayout();
        headlay.setDefaultLayoutMode(headlay.EDIT_LAYOUT);
        headlay.setDialogColumns(1);

        headlay.setSimple("ORGCODEDESCRIPTION");

	headlay.showBottomLine(false);

        headbar.disableMinimize();



        b = mgr.newASPBlock("PM_TYPE");

        b.addField("CLIENT_VALUES0");

        blkPost = mgr.newASPBlock("POSTI");
        f = blkPost.addField("CODE_A");
        f = blkPost.addField("CODE_B");
        f = blkPost.addField("CONTROL_TYPE");
        f = blkPost.addField("STR_CODE");

        //======================================  Time Report in new block  ====================================


        itemblk1 = mgr.newASPBlock("ITEM1");

        f = itemblk1.addField("ITEM1_OBJID");
        f.setHidden();
        f.setDbName("OBJID");

        f = itemblk1.addField("ITEM1_OBJVERSION");
        f.setHidden();
        f.setDbName("OBJVERSION");

        f = itemblk1.addField("EMP_NO");
        f.setSize(14);
        f.setDynamicLOV("EMPLOYEE_NO","COMPANY",600,445);
        f.setCustomValidation("COMPANY,EMP_NO,AMOUNT,QTY,ITEM1_CONTRACT,ITEM1_ORG_CODE,ROLE_CODE,ITEM1_COMPANY", "SIGN,ITEM1_NAME,ROLE_CODE,ITEM1_ORG_CODE,AMOUNT,ITEM1_CONTRACT,EMP_NO");
        f.setLabel("PCMWQUICKREPORTTOHISTORYEMPLNO: Employee:");
        f.setUpperCase();

        f = itemblk1.addField("SIGN");
        f.setSize(15);
        f.setLabel("PCMWQUICKREPORTTOHISTORYSIGN: Signature:");
        f.setFunction("''");
        f.setHidden();
        f.setUpperCase();

        f = itemblk1.addField("ITEM1_CONTRACT");
        f.setSize(14);
        f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,445);
        f.setCustomValidation("ITEM1_CONTRACT", "ITEM1_COMPANY");
        f.setMandatory();
        f.setLabel("PCMWQUICKREPORTTOHISTORYIT1CONT: Site:");
        f.setUpperCase();
        f.setDbName("CONTRACT");

        f = itemblk1.addField("ITEM1_ORG_CODE");
        f.setSize(14);
        f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ITEM1_CONTRACT CONTRACT",600,445);
        f.setCustomValidation("AMOUNT,QTY,ROLE_CODE,ITEM1_ORG_CODE,ITEM1_CONTRACT,EMP_NO", "AMOUNT,ITEM1_ORG_CODE,ITEM1_CONTRACT");
        f.setMandatory();
        f.setLabel("PCMWQUICKREPORTTOHISTORYIT1ORGCODE: Maintenance Organization:");
        f.setUpperCase();
        f.setDbName("ORG_CODE");

        f = itemblk1.addField("ROLE_CODE");
        f.setSize(14);
        f.setDynamicLOV("ROLE_TO_SITE_LOV","ITEM1_CONTRACT CONTRACT",600,445);
        f.setCustomValidation("AMOUNT,QTY,ROLE_CODE,ITEM1_ORG_CODE,ITEM1_CONTRACT,EMP_NO", "AMOUNT,ITEM1_CONTRACT,ROLE_CODE");
        f.setLabel("PCMWQUICKREPORTTOHISTORYROLECODE: Craft:");
        f.setUpperCase();

        f = itemblk1.addField("CRE_DATE","Datetime");
        f.setSize(22);
        f.setMandatory();
        f.setLabel("PCMWQUICKREPORTTOHISTORYCREDATE: Performed Date:");

        f = itemblk1.addField("QTY","Number");
        f.setCustomValidation("AMOUNT,QTY,ROLE_CODE,ITEM1_ORG_CODE,ITEM1_CONTRACT", "AMOUNT");
        f.setSize(14);
        f.setMandatory();
        f.setLabel("PCMWQUICKREPORTTOHISTORYQTY: Hours:");

        f = itemblk1.addField("AMOUNT","Money");
        f.setSize(14);
        f.setLabel("PCMWQUICKREPORTTOHISTORYAMOUNT: Cost Amount:");

        f = itemblk1.addField("CMBTIMEREPORT");
        f.setSize(13);
        f.setLabel("PCMWQUICKREPORTTOHISTORYCMBTIMEREP: Recall Time Report:");
        f.setFunction("''");
        f.setHidden();

        f = itemblk1.addField("ITEM1_COMPANY");
        f.setSize(0);
        f.setMandatory();
        f.setHidden();
        f.setLabel("PCMWQUICKREPORTTOHISTORYIT1COMP: Time Report");
        f.setUpperCase();
        f.setDbName("COMPANY");

        f = itemblk1.addField("ITEM1_NAME");
        f.setHidden();
        f.setFunction("''");

        f = itemblk1.addField("COMMENT");
        f.setHidden();

        f = itemblk1.addField("ACCOUNT_TYPE");
        f.setHidden();

        f = itemblk1.addField("COST_TYPE");
        f.setHidden();

        f = itemblk1.addField("SIGNATURE");
        f.setHidden();

        f = itemblk1.addField("BOOK_STATUS");
        f.setHidden();

        f = itemblk1.addField("NUM_ZERO","Number");
        f.setHidden();

        itemblk1.setView("WORK_ORDER_CODING");
        itemblk1.defineCommand("WORK_ORDER_CODING_API","New__,Modify__,Remove__");
        itemset1 = itemblk1.getASPRowSet();

        itembar1 = mgr.newASPCommandBar(itemblk1);
        itembar1.removeCommandGroup(headbar.CMD_GROUP_CUSTOM);

        itemtbl1 = mgr.newASPTable(itemblk1);
        itemtbl1.disableRowStatus();
        itemtbl1.setTitle(mgr.translate("PCMWQUICKREPORTTOHISTORYITM1: Work Order Coding"));
        itemtbl1.setWrap();

        itemlay1 = itemblk1.getASPBlockLayout();
        itemlay1.setDefaultLayoutMode(itemlay1.CUSTOM_LAYOUT);
        itembar1.enableCommand(itembar1.FORWARD);
        itembar1.enableCommand(itembar1.BACKWARD);
        itemlay1.setEditable();
        itemlay1.setDialogColumns(1); 
	itemlay1.showBottomLine(false);

        itembar1.disableMinimize();

        // ----------------------------------------------------
        // Define block "BlkSympt" for Symptom Combo box
        // ----------------------------------------------------

        BlkSympt = mgr.newASPBlock("SYMPT");

        f = BlkSympt.addField("S3_ERR_SYMPTOM");
        f.setSize(3);
        f.setDbName("ERR_SYMPTOM");
        f.setLabel("PCMWQUICKREPORTTOHISTORYERRSYMP: Error Symptom:");

        f = BlkSympt.addField("SYMPT_DISPLAY");
        f.setFunction("ERR_SYMPTOM" + "||" + "' - '" + "||" + "DESCRIPTION");

        BlkSympt.setView("WORK_ORDER_SYMPT_CODE");   
        RowsetSympt = BlkSympt.getASPRowSet();

        // ----------------------------------------------------
        // Define block "BlkCause" for Cause Combo box
        // ----------------------------------------------------

        BlkCause = mgr.newASPBlock("CAUS");

        f = BlkCause.addField("CMB_ERR_CAUSE");
        f.setDbName("ERR_CAUSE");

        f = BlkCause.addField("CAUSE_DISPLAY");
        f.setFunction("ERR_CAUSE" + "||" + "' - '" + "||" + "DESCRIPTION");

        BlkCause.setView("MAINTENANCE_CAUSE_CODE");  
        RowsetCause = BlkCause.getASPRowSet();     

        // ----------------------------------------------------
        // Define block "BlkClass" for Class Combo box
        // ----------------------------------------------------

        BlkClass = mgr.newASPBlock("CLAS");

        f = BlkClass.addField("CMB_ERR_CLASS");
        f.setDbName("ERR_CLASS");

        f = BlkClass.addField("CLASS_DISPLAY");
        f.setFunction("ERR_CLASS" + "||" + "' - '" + "||" + "DESCRIPTION");

        BlkClass.setView("WORK_ORDER_CLASS_CODE");  
        RowsetClass = BlkClass.getASPRowSet();     

        // ----------------------------------------------------
        // Define block "BlkType" for Type Combo box
        // ----------------------------------------------------

        BlkType = mgr.newASPBlock("TYP");

        f = BlkType.addField("CMB_ERR_TYPE");
        f.setDbName("ERR_TYPE");

        f = BlkType.addField("TYPE_DISPLAY");
        f.setFunction("ERR_TYPE" + "||" + "' - '" + "||" + "DESCRIPTION");

        BlkType.setView("WORK_ORDER_TYPE_CODE");  
        RowsetType = BlkType.getASPRowSet();      

        // ----------------------------------------------------
        // Define block "BlkDisc" for Discovery Combo box
        // ----------------------------------------------------

        BlkDisc = mgr.newASPBlock("DISC");

        f = BlkDisc.addField("S3_ERR_DISCOVER_CODE");
        f.setDbName("ERR_DISCOVER_CODE");

        f = BlkDisc.addField("DISC_DISPLAY");
        f.setFunction("ERR_DISCOVER_CODE" + "||" + "' - '" + "||" + "DESCRIPTION");

        BlkDisc.setView("WORK_ORDER_DISC_CODE");  
        RowsetDisc = BlkDisc.getASPRowSet();  

        // ----------------------------------------------------
        // Define block "BlkPerAct" for Performed Action Combo box
        // ----------------------------------------------------

        BlkPerAct = mgr.newASPBlock("PERACT");

        f = BlkPerAct.addField("S3_PERFORMED_ACTION_ID");
        f.setDbName("PERFORMED_ACTION_ID");

        f = BlkPerAct.addField("PERACT_DISPLAY");
        f.setFunction("PERFORMED_ACTION_ID" + "||" + "' - '" + "||" + "DESCRIPTION");

        BlkPerAct.setView("MAINTENANCE_PERF_ACTION");  
        RowsetPerAct = BlkPerAct.getASPRowSet();  


        disableOptions();
        disableHomeIcon();
        disableNavigate();
    }

    public void adjust()
    {
        ASPManager mgr = getASPManager();

        frm.setFormWidth(708);
        headbar.setWidth(frm.getFormWidth());

        ASPBuffer availObj;
        trans.clear();
        trans.addSecurityQuery("PRE_ACCOUNTING");
        //Bug 79242, Start, used PreAccountingDlg1
        trans.addPresentationObjectQuery("MPCCOW/PreAccountingDlg1.page");
	//Bug 79242, End

        trans = mgr.perform(trans);

        availObj = trans.getSecurityInfo();

         //Bug 79242, Start, used PreAccountingDlg1
        if (availObj.itemExists("PRE_ACCOUNTING") && availObj.namedItemExists("MPCCOW/PreAccountingDlg1.page"))
           preEna = true;
	//Bug 79242, End
        if (introFlag != 1)
        {
            mgr.getASPField("REPORTED_BY").setHidden();
            mgr.getASPField("MCH_CODE").setHidden();
            mgr.getASPField("MCHNAME").setHidden();      
            mgr.getASPField("MCH_CODE_CONTRACT").setHidden();
        }

        if (introFlag != 2)
        {
            mgr.getASPField("ERR_DESCR").setHidden();
            mgr.getASPField("ORG_CODE").setHidden();
            mgr.getASPField("REAL_S_DATE").setHidden();
            mgr.getASPField("REAL_F_DATE").setHidden();
            mgr.getASPField("CONTRACT").setHidden();
            mgr.getASPField("ORGCODEDESCRIPTION").setHidden();
	    //Request 35867 Start
	    mgr.getASPField("WORK_TYPE_ID").setHidden();
	    // End

        }

        if (introFlag != 3)
        {
            mgr.getASPField("WORK_DONE").setHidden();
            mgr.getASPField("PERFORMED_ACTION_LO").setHidden();
            mgr.getASPField("ERR_SYMPTOM").setHidden();
            mgr.getASPField("ERR_CLASS").setHidden();
            mgr.getASPField("ERR_DISCOVER_CODE").setHidden();
            mgr.getASPField("ERR_CAUSE").setHidden();
            mgr.getASPField("ERR_TYPE").setHidden();
            mgr.getASPField("PERFORMED_ACTION_ID").setHidden();
        }

        if (introFlag != 4)
        {
            mgr.getASPField("EMP_NO").setHidden();
            mgr.getASPField("ITEM1_CONTRACT").setHidden();
            mgr.getASPField("ITEM1_ORG_CODE").setHidden();
            mgr.getASPField("ROLE_CODE").setHidden();
            mgr.getASPField("CRE_DATE").setHidden();
            mgr.getASPField("QTY").setHidden();
            mgr.getASPField("AMOUNT").setHidden();
        }

        itembar1.disableCommandGroup(itembar1.CMD_GROUP_SEARCH);
        itembar1.disableCommand(itembar1.DUPLICATE);

        url = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");
    }

    protected String getDescription()
    {
        return "PCMWQUICKREPORTTOHISTORYTITLE: Quick Report to History Wizard";
    }

    protected String getTitle()
    {
        return "PCMWQUICKREPORTTOHISTORYTITLE: Quick Report to History Wizard";
    }

//===============================================================
//  HTML
//===============================================================
    protected void printContents() throws FndException
    {
        ASPManager mgr = getASPManager();

        if (showHtmlPart)
        {
            if (!("TRUE".equals(closeWin)))
            {
                appendToHTML(headbar.showBar());

                if (headlay.isVisible())
                {
                    appendToHTML("  <table id=mainTBL class=\"BlockLayoutTable\" border=\"0\" width=708>\n");
                    appendToHTML("    <tr>\n");
                    appendToHTML("      <td width=\"142\">\n");
                    appendToHTML("       <table id=\"picTBL\" border=\"0\"  cellspacing=\"1\" width=\"57%\" height=\"208\">\n");
                    appendToHTML("        <tr>\n");
                    appendToHTML("         <td width=\"100%\" height=\"202\"><img src=\"images/PcmWizardWorkSM.gif\" alt=\"PcmWizardWorkSM.gif(7590 bytes)\" WIDTH=\"130\" HEIGHT=\"260\"></td>\n");
                    appendToHTML("        </tr>\n");
                    appendToHTML("       </table>\n");
                    appendToHTML("      </td>\n");
                    appendToHTML("      <td width=\"767\">\n");
                    appendToHTML("       <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=525>\n");
                    appendToHTML("        <tr>\n");
                    appendToHTML("         <td>\n");
                    appendToHTML("          <table id=\"noteTBL\" border=\"0\">\n");
                    appendToHTML("           <tr>\n");
                    appendToHTML("            <td>");

                    if (introFlag == 1)
                    {
                        appendToHTML(fmt.drawReadLabel("PCMWQUICKREPORTTOHISTORYHEAD1: Quick Report In Of Work"));
                        appendToHTML("               <p><br>\n");
                        appendToHTML(fmt.drawReadValue(mgr.translate("PCMWQUICKREPORTTOHISTORYHEAD1ROW11: In this Wizard you make a Quick Report directly into the History. ")));
                        appendToHTML(fmt.drawReadValue(mgr.translate("PCMWQUICKREPORTTOHISTORYHEAD1ROW12: Beneath you report your Signature and what Object that the work has been done on. ")));
                        appendToHTML("                <br>\n");
                        appendToHTML(fmt.drawReadValue(mgr.translate("PCMWQUICKREPORTTOHISTORYHEAD1ROW14: In the following windows you report the data needed for your report. ")));
                        appendToHTML(fmt.drawReadValue(mgr.translate("PCMWQUICKREPORTTOHISTORYHEAD1ROW15: You can choose to finish after the next window or continue to add more data or report time .")));
                    }
                    else if (introFlag == 2)
                    {
                        appendToHTML(fmt.drawReadLabel("PCMWQUICKREPORTTOHISTORYHEAD2: Required Information"));
                        appendToHTML("         <p>     <br>\n");
                        appendToHTML(fmt.drawReadValue(mgr.translate("PCMWQUICKREPORTTOHISTORYHEAD2ROW11: Register the information for the work done. ")));
                        appendToHTML(fmt.drawReadValue(mgr.translate("PCMWQUICKREPORTTOHISTORYHEAD2ROW12: After this you can choose to continue with additional information and time report, or to finish your registration. ")));
                        appendToHTML("              <br>\n");
                    }
                    else if (introFlag == 3)
                    {
                        appendToHTML(fmt.drawReadLabel("PCMWQUICKREPORTTOHISTORYHEAD3: Performed Action and Classification"));
                        appendToHTML("          <p>    <br>\n");
                        appendToHTML(fmt.drawReadValue(mgr.translate("PCMWQUICKREPORTTOHISTORYHEAD3ROW11: Register the additional information for the work done. ")));
                        appendToHTML(fmt.drawReadValue(mgr.translate("PCMWQUICKREPORTTOHISTORYHEAD3ROW12: The classification information could be necessary for follow-up. ")));
                        appendToHTML(fmt.drawReadValue(mgr.translate("PCMWQUICKREPORTTOHISTORYHEAD3ROW13: Choose 'Next' to report time or 'Finish' to end.")));
                        appendToHTML("              <br>\n");
                    }
                    else if (introFlag == 4)
                    {
                        appendToHTML(fmt.drawReadLabel("PCMWQUICKREPORTTOHISTORYHEAD4: Time Report"));
                        appendToHTML("            <p>  <br>\n");
                        appendToHTML(fmt.drawReadValue(mgr.translate("PCMWQUICKREPORTTOHISTORYHEAD4ROW11: Here you can report your time. When ready press Finish. ")));
                        appendToHTML("              <br>\n");
                    }

                    appendToHTML("            </td>\n");
                    appendToHTML("           </tr>\n");
                    appendToHTML("          </table>\n");
                    appendToHTML(headlay.generateDialog());		//XSS_Safe AMNILK 20070725
                    appendToHTML("    <table id=\"TST\" border=0 bgcolor=green class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= '100%'>\n");

                    if (introFlag == 3)
                    {
                        appendToHTML("       <tr>\n");
                        appendToHTML("           <td nowrap height=\"26\" align=\"left\" >");
                        appendToHTML(fmt.drawWriteLabel("PCMWQUICKREPORTTOHISTORYSY: Symptom:"));
                        appendToHTML("</td>\n");
                        appendToHTML("           <td nowrap height=\"26\" align=\"left\" width = '59%'>");
                        appendToHTML(fmt.drawSelect("ERRSYMPT",symptBuff,sympt,"onChange=setSymtValue()"));
                        appendToHTML("</td>\n");
                        appendToHTML("       </tr>\n");

                        appendToHTML("       <tr>\n");
                        appendToHTML("           <td nowrap height=\"26\" align=\"left\" >");
                        appendToHTML(fmt.drawWriteLabel("PCMWQUICKREPORTTOHISTORYCL: Class:"));
                        appendToHTML("</td>\n");
                        appendToHTML("           <td nowrap height=\"26\" align=\"left\" width = '59%'>");
                        appendToHTML(fmt.drawSelect("ERRCLAS",errclassBuff,clas,"onChange=setErrorClsValue()"));
                        appendToHTML("</td>\n");
                        appendToHTML("       </tr>\n");

                        appendToHTML("       <tr>\n");
                        appendToHTML("	   <td nowrap height=\"26\" align=\"left\" >");
                        appendToHTML(fmt.drawWriteLabel("PCMWQUICKREPORTTOHISTORYDI: Discovery:"));
                        appendToHTML("</td>\n");
                        appendToHTML("           <td nowrap height=\"26\" align=\"left\" width = '59%'>");
                        appendToHTML(fmt.drawSelect("DISCOVER",errdiscBuff,disc,"onChange=setDiscoveryValue()"));
                        appendToHTML("</td>\n");
                        appendToHTML("       </tr>\n");

                        appendToHTML("       <tr>\n");
                        appendToHTML("           <td nowrap height=\"26\" align=\"left\" >");
                        appendToHTML(fmt.drawWriteLabel("PCMWQUICKREPORTTOHISTORYCA: Cause:"));
                        appendToHTML("</td>\n");
                        appendToHTML("           <td nowrap height=\"26\" align=\"left\" width = '59%'>");
                        appendToHTML(fmt.drawSelect("ERRCAUSE",errorBuff,caus,"onChange=setErrorValue()"));
                        appendToHTML("</td>\n");
                        appendToHTML("       </tr>\n");

                        appendToHTML("       <tr>\n");
                        appendToHTML("           <td nowrap height=\"26\" align=\"left\" >");
                        appendToHTML(fmt.drawWriteLabel("PCMWQUICKREPORTTOHISTORYTY: Type:"));
                        appendToHTML("</td>\n");
                        appendToHTML("           <td nowrap height=\"26\" align=\"left\" width = '59%'>");
                        appendToHTML(fmt.drawSelect("TYPE",errtypeBuff,typ,"onChange=setTypeValue()"));
                        appendToHTML("</td>\n");
                        appendToHTML("       </tr>\n");

                        appendToHTML("       <tr>\n");
                        appendToHTML("	   <td nowrap height=\"26\" align=\"left\" >");
                        appendToHTML(fmt.drawWriteLabel("PCMWQUICKREPORTTOHISTORYPER: Performed Action:"));
                        appendToHTML("</td>\n");
                        appendToHTML("           <td nowrap height=\"26\" align=\"left\" width = '59%'>");
                        appendToHTML(fmt.drawSelect("PERACT",peractBuff,peract,"onChange=setPerformedValue()"));
                        appendToHTML("</td>\n");
                        appendToHTML("       </tr>\n");
                        
                        

                    }

                    appendToHTML("    </table>\n");
                }

                if (introFlag != 0)
                {
                    itemlay1.setLayoutMode(itemlay1.EDIT_LAYOUT);
                    appendToHTML(itemlay1.generateDialog());		//XSS_Safe AMNILK 20070725
                }

                appendToHTML("         </td>\n");
                appendToHTML("        </tr>\n");
                appendToHTML("       </table>\n");
                appendToHTML("      </td>\n");
                appendToHTML("    </tr>\n");
                appendToHTML("  </table>\n");
                appendToHTML("<table id=\"lineTBL\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=708>\n");
                appendToHTML(" <tr>\n");
                appendToHTML(fmt.drawReadValue("............................................................"));
                appendToHTML(fmt.drawReadValue("..........................................................."));
                appendToHTML(fmt.drawReadValue(".........................................................."));
                appendToHTML(" </tr>\n");
                appendToHTML("</table> \n");
                appendToHTML("<table id=\"buttonTBL\" border=\"0\" width=710>\n");

                if (introFlag == 4)
                {
                    appendToHTML("  <tr width= '100%'>\n");
                    appendToHTML("   <td align=\"right\">");
                    appendToHTML(fmt.drawSubmit("ADDTREP",mgr.translate("PCMWQUICKREPORTTOHISTORYADDTRBUT: Add Another Time Report"),"OnClick='checkItem1Fields(0)'"));
                    appendToHTML("       &nbsp;");
                    appendToHTML(fmt.drawSubmit("DELETECURRREP",mgr.translate("PCMWQUICKREPORTTOHISTORYDELTRBUT: Delete Current Time Report"),"DELETECURRREP"));
                    appendToHTML("   </td>\n");
                    appendToHTML("  </tr>\n");
                    appendToHTML("  <tr width= '100%'>\n");
                    appendToHTML("   <td align=\"right\">");
                    appendToHTML(fmt.drawSubmit("PREVTIMEREP",mgr.translate("PCMWQUICKREPORTTOHISTORYPRETRBUT: <Previous Time Report"),"PREVTIMEREP"));
                    appendToHTML("       &nbsp;");
                    appendToHTML(fmt.drawSubmit("NEXTTIMEREP",mgr.translate("PCMWQUICKREPORTTOHISTORYNEXTTRBUT: Next Time Report>"),"NEXTTIMEREP"));
                    appendToHTML("   </td>\n");
                    appendToHTML("  </tr>\n");
                }

                appendToHTML("  <tr width= '100%'>\n");
                appendToHTML("   <td align=\"right\">\n");
		//Bug 79242, Start
                if (introFlag == 3)
		{
                    appendToHTML(fmt.drawSubmit("ADDPREPOST",mgr.translate("PCMWQUICKREPORTTOHISTORYPREPOST: Add Work Order Prepostings"),"ADDPREPOST"));
		}
		//Bug 79242, End


                if (introFlag != 1)
                {
                    appendToHTML(fmt.drawSubmit("PREVIOUS",mgr.translate("PCMWQUICKREPORTTOHISTORYPREVBUTT: <Previous"),"PREVIOUS"));
                    appendToHTML("&nbsp;");
                }

                if (introFlag != 4)
                {
                    appendToHTML(fmt.drawSubmit("NEXT",mgr.translate("PCMWQUICKREPORTTOHISTORYNEXBUT:  Next> "),"OnClick='checkHeadFields(0)'"));
                    appendToHTML("&nbsp;");
                }

                appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWQUICKREPORTTOHISTORYCANBUT: Cancel"),"Cancel"));
                appendToHTML("&nbsp;");

                if (introFlag > 2)
                    appendToHTML(fmt.drawSubmit("FINISH",mgr.translate("PCMWQUICKREPORTTOHISTORYFINISHBUTTON: Finish"),"OnClick='checkHeadFields(0);checkItem1Fields(0)'"));

                appendToHTML("   </td>\n");
                appendToHTML("  </tr>\n");
                appendToHTML("</table>\n");
                appendToHTML("   <p>\n");
            }
	    //Bug 79242, Start
            if (introFlag == 3 && !(preEna))
		appendDirtyJavaScript("document.form.ADDPREPOST.disabled = true;\n");
	    //Bug 79242, End
            printHiddenField("BUTTONVALPRE","");

 

            appendDirtyJavaScript("function setSymtValue()\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("  sympt = document.form.ERRSYMPT.options[document.form.ERRSYMPT.selectedIndex].value;\n");
            appendDirtyJavaScript("  document.form.ERR_SYMPTOM.value = sympt;\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function setErrorValue()\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("  caus = document.form.ERRCAUSE.options[document.form.ERRCAUSE.selectedIndex].value;\n");
            appendDirtyJavaScript("  document.form.ERR_CAUSE.value = caus;\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function setErrorClsValue()\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("  clas = document.form.ERRCLAS.options[document.form.ERRCLAS.selectedIndex].value;\n");
            appendDirtyJavaScript("  document.form.ERR_CLASS.value = clas;\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function setTypeValue()\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("  typ = document.form.TYPE.options[document.form.TYPE.selectedIndex].value;\n");
            appendDirtyJavaScript("  document.form.ERR_TYPE.value = typ;\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function setDiscoveryValue()\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("  disc = document.form.DISCOVER.options[document.form.DISCOVER.selectedIndex].value;\n");
            appendDirtyJavaScript("  document.form.ERR_DISCOVER_CODE.value = disc;\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function setPerformedValue()\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("  peract = document.form.PERACT.options[document.form.PERACT.selectedIndex].value;\n");
            appendDirtyJavaScript("  document.form.PERFORMED_ACTION_ID.value = peract;\n");
            appendDirtyJavaScript("}\n");

            //031110  ARWILK  Begin  (Bug#108229)
            appendDirtyJavaScript("\n\n");
            if (!mgr.isEmpty(sAlertMessage))
            {
                appendDirtyJavaScript("		alert(\"");
                appendDirtyJavaScript(sAlertMessage);
                appendDirtyJavaScript("\");\n\n");
            }
            appendDirtyJavaScript("if('");
            appendDirtyJavaScript(mgr.encodeStringForJavascript(closeWin));	// Bug Id 68773
            appendDirtyJavaScript("' == \"TRUE\")\n");
            appendDirtyJavaScript(" 	self.close();\n\n");
            //031110  ARWILK  End  (Bug#108229)

            appendDirtyJavaScript("function toggleStyleDisplay(el)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   el = document.getElementById(el);\n");
            appendDirtyJavaScript("   if(el.style.display!='none')\n");
            appendDirtyJavaScript("   {  \n");
            appendDirtyJavaScript("      el.style.display='none';\n");
            appendDirtyJavaScript("      buttonTBL.style.display='none';\n");
            appendDirtyJavaScript("      lineTBL.style.display='none';\n");
            appendDirtyJavaScript("      noteTBL.style.display='none';\n");
            appendDirtyJavaScript("      mainTBL.style.display='none';\n");
            appendDirtyJavaScript("   }  \n");
            appendDirtyJavaScript("   else\n");
            appendDirtyJavaScript("   {  \n");
            appendDirtyJavaScript("      el.style.display='block';\n");
            appendDirtyJavaScript("      buttonTBL.style.display='block';\n");
            appendDirtyJavaScript("      lineTBL.style.display='block';\n");
            appendDirtyJavaScript("      noteTBL.style.display='block'; \n");
            appendDirtyJavaScript("      mainTBL.style.display='block';        \n");
            appendDirtyJavaScript("   }\n");
            appendDirtyJavaScript("}\n");
        }
        else
        {
            appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
            appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
            appendDirtyJavaScript("window.location = '");

            if (toDefaultPage)
            {
                appendDirtyJavaScript(mgr.encodeStringForJavascript(calling_url));	//XSS_Safe AMNILK 20070725
                appendDirtyJavaScript("';\n");
            }
            else
            {
                appendDirtyJavaScript(url);
                appendDirtyJavaScript("'+\"Navigator.page?MAINMENU=Y&NEW=Y\";\n"); 
            }                     
            printHiddenField("BUTTONVALPRE","");

              
            if (!mgr.isEmpty(sAlertMessage))
            {
                appendDirtyJavaScript("		alert(\"");
                appendDirtyJavaScript(sAlertMessage);
                appendDirtyJavaScript("\");\n\n");
            } 
            appendDirtyJavaScript("if('");
            appendDirtyJavaScript(closeWin);
            appendDirtyJavaScript("' == \"TRUE\")\n");
            appendDirtyJavaScript(" 	self.close();\n\n");

            appendDirtyJavaScript("    if (");
            appendDirtyJavaScript(    mgr.isExplorer());
            appendDirtyJavaScript("    )\n");
            appendDirtyJavaScript("    { \n");
            appendDirtyJavaScript("      window.open('");
            appendDirtyJavaScript(url);
//  		Bug 69168, Start
            appendDirtyJavaScript("'+\"pcmw/QuickReportToHistory.page?RANDOM_VAL=\"+Math.random(),\"frmQuickReportToHistory\",\"resizable=yes, status=yes,menubar=no,height=520,width=742\");      \n");
//  		Bug 69168, End
            appendDirtyJavaScript("    } \n");
            appendDirtyJavaScript("    else \n");
            appendDirtyJavaScript("    { \n");
            appendDirtyJavaScript("      window.open('");
            appendDirtyJavaScript(url);
            appendDirtyJavaScript("'+\"pcmw/QuickReportToHistory.page?RANDOM_VAL=\"+Math.random(),\"frmQuickReportToHistory\",\"resizable=yes, status=yes,menubar=no,height=525,width=742\");      \n");
            appendDirtyJavaScript("    } \n");
        }

        appendDirtyJavaScript("function lovEmpNo(i,params)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if(params) param = params;\n");
        appendDirtyJavaScript("	else param = '';\n");
        appendDirtyJavaScript("	var enable_multichoice =(true && ITEM1_IN_FIND_MODE);\n");
        appendDirtyJavaScript("	var key_value = (getValue_('EMP_NO',i).indexOf('%') !=-1)? getValue_('EMP_NO',i):'';\n");
        appendDirtyJavaScript("	if( getValue_('ROLE_CODE',i)=='' )\n");
        appendDirtyJavaScript("       {\n");
        appendDirtyJavaScript("              openLOVWindow('EMP_NO',i,\n");
        appendDirtyJavaScript("                  '../mscomw/MaintEmployeeLov.page?__FIELD=Employee&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('EMP_NO',i))\n"); 
        appendDirtyJavaScript("                  + '&PERSON_ID=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
        appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
        appendDirtyJavaScript("                  + '&CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");       
        appendDirtyJavaScript("       	   ,550,500,'validateEmpNo');\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript(" else\n");
        appendDirtyJavaScript("       {\n");        
        appendDirtyJavaScript("              openLOVWindow('EMP_NO',i,\n");
        appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Employee&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('EMP_NO',i))\n"); 
        appendDirtyJavaScript("                  + '&SIGNATURE=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
        appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
        appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");       
        appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");       
        appendDirtyJavaScript("       	   ,550,500,'validateEmpNo');\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovItem1OrgCode(i,params)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if(params) param = params;\n");
        appendDirtyJavaScript("	else param = '';\n");
        appendDirtyJavaScript("	var enable_multichoice =(true && ITEM1_IN_FIND_MODE);\n");
        appendDirtyJavaScript("	var key_value = (getValue_('ITEM1_ORG_CODE',i).indexOf('%') !=-1)? getValue_('ITEM1_ORG_CODE',i):'';\n");
        appendDirtyJavaScript("	if( getValue_('ROLE_CODE',i)=='' )\n");
        appendDirtyJavaScript("       {\n");
        appendDirtyJavaScript("	            if( getValue_('EMP_NO',i)=='' )\n");
        appendDirtyJavaScript("             {\n");
        appendDirtyJavaScript("                  openLOVWindow('ITEM1_ORG_CODE',i,\n");
        appendDirtyJavaScript("                     '../mscomw/OrgCodeAllowedSiteLov.page?__FIELD=Maintenance+Organization&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n"); 
        appendDirtyJavaScript("                      + '&ORG_CODE=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
        appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");       
        appendDirtyJavaScript("       	             ,550,500,'validateItem1OrgCode');\n");
        appendDirtyJavaScript("             }\n");
        appendDirtyJavaScript("	            else\n");
        appendDirtyJavaScript("             {\n");
        appendDirtyJavaScript("                 openLOVWindow('ITEM1_ORG_CODE',i,\n");
        appendDirtyJavaScript("                     '../mscomw/MaintEmployeeLov.page?&__FIELD=Maintenance+Organization&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("                     + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n"); 
        appendDirtyJavaScript("                     + '&ORG_CODE=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript("                     + '&EMPLOYEE_ID=' + URLClientEncode(getValue_('EMP_NO',i))\n");
        appendDirtyJavaScript("                     + '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
        appendDirtyJavaScript("                     + '&CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");       
        appendDirtyJavaScript("       	            ,550,500,'validateItem1OrgCode');\n");
        appendDirtyJavaScript("             }\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript(" else\n");
        appendDirtyJavaScript("       {\n");        
        appendDirtyJavaScript("              openLOVWindow('ITEM1_ORG_CODE',i,\n");
        appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Maintenance+Organization&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n"); 
        appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
        appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
        appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");       
        appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");       
        appendDirtyJavaScript("       	   ,550,500,'validateItem1OrgCode');\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript("}\n");

        appendDirtyJavaScript("function lovRoleCode(i,params)\n");
        appendDirtyJavaScript("{\n");
        appendDirtyJavaScript("	if(params) param = params;\n");
        appendDirtyJavaScript("	else param = '';\n");
        appendDirtyJavaScript("	var enable_multichoice =(true && ITEM1_IN_FIND_MODE);\n");
        appendDirtyJavaScript("	var key_value = (getValue_('ROLE_CODE',i).indexOf('%') !=-1)? getValue_('ROLE_CODE',i):'';\n");
        appendDirtyJavaScript("	if( getValue_('EMP_NO',i)=='' )\n");
        appendDirtyJavaScript("       {\n");
        appendDirtyJavaScript("	            if( getValue_('ITEM1_ORG_CODE',i)=='' )\n");
        appendDirtyJavaScript("             {\n");
        appendDirtyJavaScript("                  openLOVWindow('ROLE_CODE',i,\n");
        appendDirtyJavaScript("                     '../mscomw/RoleToSiteLov.page?__FIELD=Craft&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n"); 
        appendDirtyJavaScript("                      + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
        appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");       
        appendDirtyJavaScript("       	             ,550,500,'validateRoleCode');\n");
        appendDirtyJavaScript("             }\n");
        appendDirtyJavaScript("	            else\n");
        appendDirtyJavaScript("             {\n");
        appendDirtyJavaScript("                 openLOVWindow('ROLE_CODE',i,\n");
        appendDirtyJavaScript("                     '../mscomw/EmployeeRoleLov.page?&__FIELD=Craft&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("                     + '&__KEY_VALUE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n"); 
        appendDirtyJavaScript("                      + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript("                     + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
        appendDirtyJavaScript("                     + '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
        appendDirtyJavaScript("                     + '&CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");       
        appendDirtyJavaScript("       	            ,550,500,'validateRoleCode');\n");
        appendDirtyJavaScript("             }\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript(" else\n");
        appendDirtyJavaScript("       {\n");        
        appendDirtyJavaScript("              openLOVWindow('ROLE_CODE',i,\n");
        appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Craft&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
        appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n"); 
        appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
        appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM1_ORG_CODE',i))\n");
        appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
        appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM1_COMPANY',i))\n");
        appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM1_CONTRACT',i))\n");       
        appendDirtyJavaScript("       	   ,550,500,'validateRoleCode');\n");
        appendDirtyJavaScript("       }\n");
        appendDirtyJavaScript("}\n");

	/*appendDirtyJavaScript("function checkHeadFields(i)\n");
	appendDirtyJavaScript("{\n");
	appendDirtyJavaScript("}\n");*/
	//Bug 79242, Start
	if (bOpenNewWindow)
	{
	   appendDirtyJavaScript("  winref = window.open(\"");
	   appendDirtyJavaScript(urlString);
	   appendDirtyJavaScript("\", \"");
	   appendDirtyJavaScript(newWinHandle);
	   appendDirtyJavaScript("\",\"alwaysRaised,resizable,status,scrollbars=yes,width=600,height=300,top=30,left=30\"); \n");  
	   //appendDirtyJavaScript("  setTimeout(\"winref.focus()\",100);");    
	   appendDirtyJavaScript("  f.submit();");    
	}
	//Bug 79242, End


    }
}
