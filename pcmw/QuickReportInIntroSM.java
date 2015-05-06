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
*  File        : QuickReportInIntroSM.java 
*  Created     : ASP2JAVA Tool  010222  Created Using the ASP file QuickReportInIntroSM.asp
*  Modified    : 
*  CHDELK  010223  Converted to Java
*  CHDELK  010417  "null" value corrections were done to attribute strings
*                  Actual Start/ Actual Completion field length was set to 22
*  CHDELK  010423  Made corrections to Employee & Department validations
*  INROLK  010608  Changed Validation of Customer No and Work Type ID for Call Id 65159.
*  CHCRLK  010613  Modified overwritten validations.
*  INROLK  011018  Changed PrepareForm and finishForm . call id 70752,70812 ,
*  VAGULK  010402  Modified to change the query parameters by entering a random value.
*                  This is done to avoid the browser fetch the page from cache. 
* ----------------------------------------------------------------------------
*  SHAFLK  020708  Bug id 31919, Changed finishForm and add some fields to headblk and itemblk,
*  BUNILK  021129  Added new fields (Object Site, and Object description) and replaced 
*                  work order site by Object site whenever it refer to Object.    
*  INROLK  030120  Code Review Done.
*  BUNILK  030123  Modified finishForm() method.  
*  CHAMLK  030131  Call Id - 92967 - Modified finishForm() method. 
*  JEWILK  030925  Corrected overridden javascript function toggleStyleDisplay(). 
*  CHAMLK  031022  Modified functions prepareITEM() and prepare() in order to check the value of the ITEM_CONTRACT field.
*  CHAMLK  031028  Modified function finishForm() to fetch the reported by id from the headset.
*  CHCRLK  031111  Modified function finishForm() to fix error when time is also reported. [Call ID 110638]
*  ARWILK  031219  Edge Developments - (Removed clone and doReset Methods)
*  VAGULK  040109  Made field order according to the order in the Centura application(Web Alignment).
* --------------------------------- Edge - SP1 Merge -------------------------
*  SHAFLK  040120  Bug Id 41815,Removed Java dependencies.
*  THWILK  040324  Merge with SP1.
*  ARWILK  040722  Changed method calls to support the org_code key change. (IID AMEC500C: Resource Planning) 
*  NIJALK  040819  Added parameter CONTRACT to method calls to Role_Sales_Part_API.Get_Def_Catalog_No.
*  ARWILK  041111  Replaced getContents with printContents.
*  NIJALK  050120  Modified LOVs and validations to ITEM1_ORG_CODE,ROLE_CODE and EMP_NO.
*  SHAFLK  050304  Bug 48641, Modified methods prepareForm(), finishForm() and validation of REPORTED_BY.
*  NIJALK  050408  Merged bug 48641.
*  SHAFLK  050425  Bug 50830, Used getJSLabel() method to get the correctly translated strings in JavaScript functions.
*  NIJALK  050617  Merged bug 50830. 
*  NIJALK  051025  Bug 128030: Modified finishForm(),prepareForm().
*  NIJALK  051124  Bug 129261, Replaced method mgr.getConfigParameter(String name) with mgr.getFormatMask(String datatype, boolean read_profile).
*  SHAFLK  060524  Bug 58197, Modified finishForm().
* ----------------------------------------------------------------------------
*  AMNILK  060629  Merged with SP1 APP7.
*  AMDILK  060728  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encodings
*  AMDILK  060807  Merged with Bug ID 58214
*  SHAFLK  070312  Bug 64068, Removed extra mgr.translate().   
*  AMDILK  070330  Merged bug id 64068
*  AMDILK  070608  Remove the agreement details and inserted serv.contract information 
*  AMNILK  070725  Eliminated XSS Security Vulnerability.
*  CHANLK  071201  Bug 69168, remove lable of PERFORMED_ACTION_LO, change GUI
*  AMNILK  080225  Bug Id 70012, Modified printContents(), adjust().
*  SHAFLK  080311  Bug 72265, Modified prepareForm().
*  ARWILK  080502  Bug 70920, Overode lovLineNo and lovContractId.
*  SHAFLK  080612  Bug 74329, Restructured the page to support adding Pre Posting.
*  NIJALK  080917  Bug 75335, Modified methods finishForm(), finishForm1().
*  NIJALK  081029  Rollbacked the changes done in Bug 75335.
*  HARPLK  090709  Bug 84436, Modified preDefine().
*  CHARLK  090826  Bug 79242, Added a button instead of check box 'Add Work Order Prepostings'.
*  SHAFLK  100210  Bug 88904, Modified printContents().
* ----------------------------------------------------------------------------
*/

package ifs.pcmw; 

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;

public class QuickReportInIntroSM extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.QuickReportInIntroSM");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPHTMLFormatter fmt;
   private ASPForm frm;
   private ASPContext ctx;

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   private ASPBlock itemblk;
   private ASPRowSet itemset;
   private ASPCommandBar itembar;
   private ASPTable itemtbl;
   private ASPBlockLayout itemlay;

   private ASPField f;    
   private ASPBlock blkPost;
   private ASPBlock b;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private String empno;
   private String objid;
   private String objversion;
   private String closeWin;
   private ASPTransactionBuffer secBuff;
   private String val;
   private ASPCommand cmd;
   private String agridval;
   private String tpcompany;
   private String objdesc;
   private ASPBuffer buf;
   private String tpname;
   private String tpempno;
   private String dRegDate;
   private String securityOk;
   private String tpsign;
   private String tprolecode;
   private String tporgcode;
   private String tpamount;
   private String tpcatalogno;
   private String tpitemcompany;
   private String tpcatalogNo;
   private boolean errFlag;
   private String itemrolecode;
   private String itemorgcode;
   private ASPBuffer data;
   private String employeeNo;
   private String horgcode;
   private ASPBuffer row;
   private String sAttr;
   private String sMchCode;
   private String sWoNo;
   private String sdate;
   private String WoNo;
   private int num;
   private String sAccountType;
   private String sCostType;
   private String sSignature;
   private String sBookStatus;
   private int n;
   private String sComment;  
   private String[] isSecure =  new String[7];
   private double introFlag;  
   private double win2Flag;
   private double countOfNext1;
   private double countOfNext;
   private String date_fmt;  
   private String txt; 
   private boolean showHtmlPart;
   private boolean toDefaultPage;
   private String url;
   private String calling_url;

//  		Bug 69168, Start
   private String workDone;
   private String workDoneLo;
//  		Bug 69168, Start
   //Bug Id 70012,Start
   private boolean bPcmsciExist;
   // Bug Id 70012,End
   private String sAlertMessage;

   private boolean preEna;

   //Bug 79242, Start
   private boolean bOpenNewWindow;
   private String sTempPrePostId;
   private String urlString;
   private String newWinHandle;
   //Bug 79242, End

   //===============================================================
   // Construction 
   //===============================================================
   public QuickReportInIntroSM(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() 
   {
      isSecure =  new String[6] ;
      ASPManager mgr = getASPManager();


      trans = mgr.newASPTransactionBuffer();
      fmt = mgr.newASPHTMLFormatter();
      frm = mgr.getASPForm();
      ctx = mgr.getASPContext();

      introFlag = toDouble(ctx.readValue("INTROFLAG","1"));
      win2Flag = toDouble(ctx.readValue("WIN2FLAG","0"));
      countOfNext1 = toDouble(ctx.readValue("COUNTOFNEXTONE","0"));
      countOfNext = toDouble(ctx.readValue("COUNTOFNEXT","0"));
      empno = ctx.readValue("EMP","");
      closeWin = ctx.readValue("CLOSEWIN","");
      //Bug Id 70012, Start
      bPcmsciExist = ctx.readFlag("PCMSCIEXIST",false);
      //Bug Id 70012, End

      date_fmt = mgr.getFormatMask("Date",true);
      showHtmlPart = ctx.readFlag("CTSSHHTML",true);
      toDefaultPage = ctx.readFlag("CTXTODEF",false);

      preEna = ctx.readFlag("PREENAA",false);
      sWoNo =  ctx.readValue("SWONO","");
      //Bug 79242, Start
      sTempPrePostId = ctx.readValue("TEMPPREPOSTID","");
      workDone = ctx.readValue("WORKDONEDISP","");
      workDoneLo = ctx.readValue("PERFORMEDACTIONLODISP");
      //Bug 79242, End

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("WNDFLAG")))
      {
         ctx.setGlobal("WO_NO","0");


         if ("QuickReportInIntroSM".equals(mgr.readValue("WNDFLAG","")))
         {
            showHtmlPart = false;
         }
         else if ("QuickReportInIntroSMFromPortal".equals(mgr.readValue("WNDFLAG","")))
         {
            showHtmlPart = false;
            toDefaultPage = true;

            calling_url = ctx.getGlobal("CALLING_URL");
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
         prepareITEM();
      else if (mgr.buttonPressed("DELETECURRREP"))
         deleteITEM();
      else if (mgr.buttonPressed("PREVTIMEREP"))
         backwardITEM();
      else if (mgr.buttonPressed("NEXTTIMEREP"))
         forwardITEM();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      //Bug 79242, Start
      else if (mgr.buttonPressed("ADDPREPOST")) 
	 addPreposting();
     //Bug 79242, End
              
     else
         prepareForm();

      adjust();
      ctx.writeValue("INTROFLAG",String.valueOf(introFlag));
      ctx.writeValue("WIN2FLAG",String.valueOf(win2Flag));
      ctx.writeValue("COUNTOFNEXTONE",String.valueOf(countOfNext1));
      ctx.writeValue("COUNTOFNEXT",String.valueOf(countOfNext));
      ctx.writeValue("EMP",empno);
      ctx.writeFlag("CTSSHHTML",showHtmlPart);
      ctx.writeFlag("CTXTODEF",toDefaultPage);
      //Bug Id 70012, Start
      ctx.writeFlag("PCMSCIEXIST", bPcmsciExist);
      //Bug Id 70012, End

      ctx.writeFlag("PREENAA",preEna);
      ctx.writeValue("SWONO",sWoNo);
      //Bug 79242, Start
      ctx.writeValue("TEMPPREPOSTID",sTempPrePostId);
      ctx.writeValue("WORKDONEDISP",workDone);
      ctx.writeValue("PERFORMEDACTIONLODISP",workDoneLo);
      //Bug 79242, End

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

   public void store()
   {

      if (headset.countRows()>0)
         headset.changeRow();
      if (itemset.countRows()>0)
         itemset.changeRow();
   }


   public void assign()
   {
      eval(headblk.generateAssignments());
      eval(itemblk.generateAssignments());
   }


   public void reassign()
   {

      headset.clear();
      eval(headblk.regenerateAssignments());

      itemset.clear();
      eval(itemblk.regenerateAssignments());

   }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void validate()
   {
      ASPManager mgr = getASPManager();

      val = mgr.readValue("VALIDATE");

      // Commented by amdilk
      /*if ("CUSTOMER_NO".equals(val))
      {
          cmd = trans.addCustomFunction("AGRID","CUST_AGREEMENT_OBJECT_API.Get_Valid_Agreement","AGREEMENT_ID");
          cmd.addParameter("CUSTOMER_NO");
          cmd.addParameter("MCH_CODE_CONTRACT");
          cmd.addParameter("MCH_CODE");
          cmd.addParameter("WORK_TYPE_ID");
          cmd.addParameter("REG_DATE");

          trans = mgr.validate(trans);
          agridval = trans.getValue("AGRID/DATA/AGREEMENT_ID");

          if (mgr.isExplorer())
              txt = (mgr.isEmpty(agridval) ? "" : (agridval));
          else
              txt = (mgr.isEmpty(agridval) ? " " : (agridval));
          mgr.responseWrite(txt);
      }*/
      if ("CONTRACT".equals(val))
      {
         cmd = trans.addCustomFunction("GETCOMP","Site_API.Get_Company","COMPANY");
         cmd.addParameter("MCH_CODE_CONTRACT");

         trans = mgr.validate(trans);

         tpcompany = trans.getValue("GETCOMP/DATA/COMPANY");

         txt = (mgr.isEmpty(tpcompany) ? "" :tpcompany) + "^";
         mgr.responseWrite(txt);
      }
      else if ("MCH_CODE".equals(val))
      {

         String mchCodeContract = mgr.readValue("MCH_CODE_CONTRACT");
         String mchCode = mgr.readValue("MCH_CODE");

         if (mgr.readValue("MCH_CODE").indexOf("~") > -1)
         {
            String strAttr = mgr.readValue("MCH_CODE");

            mchCode = strAttr.substring(0,strAttr.indexOf("~"));       
            String validationAttrAtr = strAttr.substring(strAttr.indexOf("~")+1,strAttr.length());
            mchCodeContract =  validationAttrAtr.substring(validationAttrAtr.indexOf("~")+1,validationAttrAtr.length());                

            cmd = trans.addCustomFunction("OBJDES","Maintenance_Object_API.Get_Mch_Name","MCH_CODE_DESCRIPTION");
            cmd.addParameter("MCH_CODE_CONTRACT",mchCodeContract);
            cmd.addParameter("MCH_CODE",mchCode);
         }
         else
         {
            if (mgr.isEmpty(mchCodeContract))
            {
               cmd = trans.addCustomFunction("MCHCONTR","EQUIPMENT_OBJECT_API.Get_Contract","MCH_CODE_CONTRACT");
               cmd.addParameter("MCH_CODE");
            }

            cmd = trans.addCustomFunction("OBJDES","MAINTENANCE_OBJECT_API.Get_Mch_Name","MCH_CODE_DESCRIPTION");
            if (mgr.isEmpty(mgr.readValue("MCH_CODE_CONTRACT")))
               cmd.addReference("MCH_CODE_CONTRACT","MCHCONTR/DATA");
            else
               cmd.addParameter("MCH_CODE_CONTRACT");
            cmd.addParameter("MCH_CODE");
         }

         trans = mgr.validate(trans);

         if (mgr.readValue("MCH_CODE").indexOf("~") == -1 && mgr.isEmpty(mchCodeContract))
            mchCodeContract = trans.getValue("MCHCONTR/DATA/MCH_CODE_CONTRACT");

         objdesc = trans.getValue("OBJDES/DATA/MCH_CODE_DESCRIPTION");

         txt = (mgr.isEmpty(objdesc) ? "" :objdesc)+ "^" + (mgr.isEmpty(mchCodeContract) ? "" :mchCodeContract)+ "^" + (mgr.isEmpty(mchCode) ? "" :mchCode)+ "^";

         mgr.responseWrite(txt);   
      }
      //commented by amdilk
      /*else if ("WORK_TYPE_ID".equals(val))
      {
          cmd = trans.addCustomFunction("AGRID","CUST_AGREEMENT_OBJECT_API.Get_Valid_Agreement","AGREEMENT_ID");
          cmd.addParameter("CUSTOMER_NO");
          cmd.addParameter("MCH_CODE_CONTRACT");
          cmd.addParameter("MCH_CODE");
          cmd.addParameter("WORK_TYPE_ID");
          cmd.addParameter("REG_DATE");

          trans = mgr.validate(trans);
          agridval = trans.getValue("AGRID/DATA/AGREEMENT_ID");

          if (mgr.isExplorer())
              txt = (mgr.isEmpty(agridval) ? "" : (agridval));
          else
              txt = (mgr.isEmpty(agridval) ? " " : (agridval));
          mgr.responseWrite(txt);
      }*/
      else if ("REAL_S_DATE".equals(val))
      {
         buf = mgr.newASPBuffer();
         buf.addFieldItem("REAL_S_DATE",mgr.readValue("REAL_S_DATE"));
         mgr.responseWrite(buf.getFieldValue("REAL_S_DATE") +"^" );  
      }
      else if ("CRE_DATE".equals(val))
      {
         buf = mgr.newASPBuffer();
         buf.addFieldItem("CRE_DATE",mgr.readValue("CRE_DATE"));
         mgr.responseWrite(buf.getFieldValue("CRE_DATE") +"^" );     
      }
      else if ("REAL_F_DATE".equals(val))
      {
         buf = mgr.newASPBuffer();
         buf.addFieldItem("REAL_F_DATE",mgr.readValue("REAL_F_DATE"));
         mgr.responseWrite(buf.getFieldValue("REAL_F_DATE") +"^" );  
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

         trans = mgr.validate(trans);

         tpname = trans.getValue("GETNAME/DATA/NAME");
         tpempno = trans.getValue("EMPID/DATA/EMPNO");
         empno = tpempno;
         if (mgr.isEmpty(dept))
            tporgcode = trans.getValue("ORG/DATA/ORG_CODE");
         else
            tporgcode= dept;
         dRegDate = mgr.readValue("REG_DATE");

         txt = (mgr.isEmpty(tpname ) ? "" :tpname ) + "^" + (mgr.isEmpty(tpempno) ? "" :tpempno) + "^" + (mgr.isEmpty(dRegDate ) ? "" :dRegDate ) + "^" + (mgr.isEmpty(tporgcode ) ? "" :tporgcode ) + "^";

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

         String sOrgCode = mgr.readValue("ITEM_ORG_CODE");
         String sRoleCode = mgr.readValue("ROLE_CODE");
         String sOrgContract = mgr.readValue("ITEM_CONTRACT");

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

         securityOk = "";
         secBuff = (ASPTransactionBuffer)mgr.newASPBuffer();
         secBuff.addSecurityQuery("SALES_PART");

         secBuff = mgr.perform(secBuff);

         if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
            securityOk = "TRUE";

         cmd = trans.addCustomFunction("GETPERID","Company_Emp_API.Get_Person_Id","SIGN");
         cmd.addParameter("COMPANY");
         cmd.addParameter("EMP_NO",emp_id);

         cmd = trans.addCustomFunction("GETNAME","PERSON_INFO_API.Get_Name","ITEM_NAME");
         cmd.addReference("SIGN", "GETPERID/DATA");

         if (mgr.isEmpty(sRoleCode))
         {
            cmd = trans.addCustomFunction("GETROLE","Employee_Role_API.Get_Default_Role","ROLE_CODE");
            cmd.addParameter("COMPANY");
            cmd.addParameter("EMP_NO",emp_id);
         }

         if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
         {
            cmd = trans.addCustomFunction("EMPORGCONT","Employee_API.Get_Org_Contract","ITEM_CONTRACT");
            cmd.addParameter("COMPANY");
            cmd.addParameter("EMP_NO",emp_id);
         }

         if (mgr.isEmpty(sOrgCode))
         {
            cmd = trans.addCustomFunction("GETORG","Employee_API.Get_Organization","ITEM_ORG_CODE");
            cmd.addParameter("COMPANY");
            cmd.addParameter("EMP_NO",emp_id);
         }

         cmd = trans.addCustomCommand("CALCAMOUNT","Work_Order_Coding_API.Calc_Amount");
         cmd.addParameter("AMOUNT",(mgr.isEmpty(mgr.readValue("AMOUNT")) ? "0" :mgr.readValue("AMOUNT")));
         cmd.addParameter("QTY",(mgr.isEmpty(mgr.readValue("QTY")) ? "0" :mgr.readValue("QTY")));
         if (mgr.isEmpty(sOrgCode))
            cmd.addReference("ITEM_ORG_CODE", "GETORG/DATA");
         else
            cmd.addParameter("ITEM_ORG_CODE", sOrgCode);
         if (mgr.isEmpty(sRoleCode))
            cmd.addReference("ROLE_CODE", "GETROLE/DATA");
         else
            cmd.addParameter("ROLE_CODE", sRoleCode);
         if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
            cmd.addReference("ITEM_CONTRACT", "EMPORGCONT/DATA");
         else
            cmd.addParameter("ITEM_CONTRACT",sOrgContract);    

         if ("TRUE".equals(securityOk))
         {
            cmd = trans.addCustomFunction("CATALO","Role_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
            cmd.addReference("ROLE_CODE", "GETROLE/DATA");
            if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
               cmd.addReference("ITEM_CONTRACT", "EMPORGCONT/DATA");
            else
               cmd.addParameter("ITEM_CONTRACT",sOrgContract);    
         }

         trans = mgr.validate(trans);

         tpsign = trans.getValue("GETPERID/DATA/SIGN");
         tpname = trans.getValue("GETNAME/DATA/ITEM_NAME");
         if (mgr.isEmpty(sRoleCode))
            tprolecode = trans.getValue("GETROLE/DATA/ROLE_CODE");
         else
            tprolecode = sRoleCode;
         if (mgr.isEmpty(sOrgCode))
            tporgcode = trans.getValue("GETORG/DATA/ORG_CODE");
         else
            tporgcode = sOrgCode;

         tpamount = trans.getBuffer("CALCAMOUNT/DATA").getFieldValue("AMOUNT");

         if (mgr.isEmpty(sOrgCode) || mgr.isEmpty(sOrgContract))
            sOrgContract = trans.getValue("EMPORGCONT/DATA/CONTRACT");

         if ("TRUE".equals(securityOk))
            tpcatalogno = trans.getValue("CATALO/DATA/CATALOG_NO");
         else
            tpcatalogno = mgr.readValue("CATALOG_NO");

         txt = (mgr.isEmpty(tpsign ) ? "" :tpsign ) + "^" + (mgr.isEmpty(tpname ) ? "" :tpname )+ "^" + (mgr.isEmpty(tprolecode ) ? "" :tprolecode )+ "^"+ (mgr.isEmpty(tporgcode ) ? "" :tporgcode )+ "^" + (mgr.isEmpty(tpamount) ? "0.00":tpamount) +"^"+ (mgr.isEmpty(tpcatalogno) ? "":tpcatalogno) +"^"+
               (mgr.isEmpty(emp_id) ? "":emp_id) +"^" +(mgr.isEmpty(sOrgContract) ? "":sOrgContract) +"^";

         mgr.responseWrite(txt);
      }
      else if ("ITEM_CONTRACT".equals(val))
      {
         cmd = trans.addCustomFunction("GETCOMP","Site_API.Get_Company","COMPANY");
         cmd.addParameter("ITEM_CONTRACT", mgr.readValue("ITEM_CONTRACT"));

         trans = mgr.validate(trans);

         tpitemcompany = trans.getValue("GETCOMP/DATA/COMPANY");

         txt = (mgr.isEmpty(tpitemcompany) ? "" :tpitemcompany);

         mgr.responseWrite(txt);
      }
      else if ("ITEM_ORG_CODE".equals(val))
      {
         securityOk = "";
         String reqstr = null;
         int startpos = 0;
         int endpos = 0;
         int i = 0;
         String ar[] = new String[6];
         String colOrgContract = "";
         String colOrgCode = "";

         String new_org_code = mgr.readValue("ITEM_ORG_CODE","");

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
            colOrgCode = mgr.readValue("ITEM_ORG_CODE");
            colOrgContract = mgr.readValue("ITEM_CONTRACT"); 
         }

         if (checksec("Organization_Sales_Part_API.Get_Def_Catalog_No",2) && mgr.isEmpty(mgr.readValue("ROLE_CODE")))
         {
            securityOk = "TRUE";
            cmd = trans.addCustomFunction("ORGCATALO","Organization_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
            cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));     
            cmd.addParameter("ITEM_ORG_CODE",colOrgCode);     
         }

         cmd = trans.addCustomCommand("CALCAMOUNT","Work_Order_Coding_API.Calc_Amount");
         cmd.addParameter("AMOUNT");
         cmd.addParameter("QTY",(mgr.isEmpty(mgr.readValue("QTY")) ? "0" :mgr.readValue("QTY")));
         cmd.addParameter("ITEM_ORG_CODE",colOrgCode);
         cmd.addParameter("ROLE_CODE");
         cmd.addParameter("ITEM_CONTRACT",colOrgContract);    

         trans = mgr.validate(trans);

         tpamount = trans.getBuffer("CALCAMOUNT/DATA").getFieldValue("AMOUNT");

         if ("TRUE".equals(securityOk))
            tpcatalogNo = trans.getValue("ORGCATALO/DATA/CATALOG_NO");
         else
            tpcatalogNo = mgr.readValue("CATALOG_NO");

         txt = (mgr.isEmpty(tpamount) ? "0.00":tpamount) + "^" + (mgr.isEmpty(tpcatalogNo) ? "":tpcatalogNo) + "^"+
               (mgr.isEmpty(colOrgCode) ? "":colOrgCode) + "^" +(mgr.isEmpty(colOrgContract) ? "":colOrgContract) + "^" ;

         mgr.responseWrite(txt);
      }
      else if ("ROLE_CODE".equals(val))
      {
         securityOk = "";

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
               if (!mgr.isEmpty(mgr.readValue("ITEM_ORG_CODE")))
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
            colOrgContract = mgr.readValue("ITEM_CONTRACT"); 
         }

         secBuff = (ASPTransactionBuffer)mgr.newASPBuffer();
         secBuff.addSecurityQuery("SALES_PART");

         secBuff = mgr.perform(secBuff);

         if (secBuff.getSecurityInfo().itemExists("SALES_PART") && (!mgr.isEmpty(mgr.readValue("ROLE_CODE"))))
            securityOk = "TRUE";


         if ("TRUE".equals(securityOk))
         {
            cmd = trans.addCustomFunction("CATALO","Role_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
            cmd.addParameter("ROLE_CODE",colRoleCode);
            cmd.addParameter("ITEM_CONTRACT",colOrgContract); 
         }

         cmd = trans.addCustomCommand("CALCAMOUNT","Work_Order_Coding_API.Calc_Amount");
         cmd.addParameter("AMOUNT",(mgr.isEmpty(mgr.readValue("AMOUNT")) ? "0" :mgr.readValue("AMOUNT")));
         cmd.addParameter("QTY",(mgr.isEmpty(mgr.readValue("QTY")) ? "0" :mgr.readValue("QTY")));
         cmd.addParameter("ITEM_ORG_CODE");
         cmd.addParameter("ROLE_CODE",colRoleCode);
         cmd.addParameter("ITEM_CONTRACT",colOrgContract);    

         trans = mgr.validate(trans);

         tpamount = trans.getBuffer("CALCAMOUNT/DATA").getFieldValue("AMOUNT");

         if ("TRUE".equals(securityOk))
            tpcatalogno = trans.getValue("CATALO/DATA/CATALOG_NO");
         else
            tpcatalogno = mgr.readValue("CATALOG_NO");

         if (mgr.isEmpty(tpcatalogno))
         {
            trans.clear();
            cmd = trans.addCustomFunction("ORGCATALO","Organization_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
            cmd.addParameter("CONTRACT",mgr.readValue("CONTRACT"));     
            cmd.addParameter("ITEM_ORG_CODE",mgr.readValue("ITEM_ORG_CODE"));

            trans = mgr.validate(trans);

            tpcatalogno = trans.getValue("ORGCATALO/DATA/CATALOG_NO");
         }

         txt = (mgr.isEmpty(tpamount) ? "0.00":tpamount) + "^" + (mgr.isEmpty(tpcatalogno) ? "":tpcatalogno) + "^"+
               (mgr.isEmpty(colRoleCode) ? "":colRoleCode) + "^" +(mgr.isEmpty(colOrgContract) ? "":colOrgContract) + "^" ;

         mgr.responseWrite(txt);
      }
      else if ("QTY".equals(val))
      {
         cmd = trans.addCustomCommand("CALCAMOUNT","Work_Order_Coding_API.Calc_Amount");
         cmd.addParameter("AMOUNT",(mgr.isEmpty(mgr.readValue("AMOUNT")) ? "0" :mgr.readValue("AMOUNT")));
         cmd.addParameter("QTY",(mgr.isEmpty(mgr.readValue("QTY")) ? "0" :mgr.readValue("QTY")));
         cmd.addParameter("ITEM_ORG_CODE");
         cmd.addParameter("ROLE_CODE");
         cmd.addParameter("ITEM_CONTRACT");    

         trans = mgr.validate(trans);

         double tpamount = trans.getNumberValue("CALCAMOUNT/DATA/AMOUNT");
         if (isNaN(tpamount))
            tpamount=0;
         txt = mgr.getASPField("AMOUNT").formatNumber(tpamount);

         mgr.responseWrite(txt);
      }
      else if ("CONTRACT_ID".equals(val))
      {
         String sContractId   = mgr.readValue("CONTRACT_ID");
         String sLineNo       = mgr.readValue("LINE_NO");
         String sContractName = "";
         String sLineDesc     = "";

         if (sContractId.indexOf("^") > -1)
         {
            String strAttr = sContractId;
            sContractId = strAttr.substring(0, strAttr.indexOf("^"));       
            sLineNo = strAttr.substring(strAttr.indexOf("^") + 1, strAttr.length());
         }

         if (checksec("SC_SERVICE_CONTRACT_API.Get_Contract_Name",1))
         {
            cmd = trans.addCustomFunction("GETCONTRACTNAME", "SC_SERVICE_CONTRACT_API.Get_Contract_Name", "CONTRACT_NAME");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }

         if (checksec("PSC_CONTR_PRODUCT_API.Get_Description",1))
         {
            cmd = trans.addCustomFunction("GETLINEDESC", "PSC_CONTR_PRODUCT_API.Get_Description", "LINE_DESC");
            cmd.addParameter("CONTRACT_ID", sContractId);
            cmd.addParameter("LINE_NO", sLineNo);
         }

         trans = mgr.validate(trans);

         sContractName  = trans.getValue("GETCONTRACTNAME/DATA/CONTRACT_NAME");
         sLineDesc      = trans.getValue("GETLINEDESC/DATA/LINE_DESC");

         txt =  (mgr.isEmpty(sContractId) ? "" : (sContractId)) + "^" +(mgr.isEmpty(sContractName)?"":sContractName) + "^" +
                (mgr.isEmpty(sLineNo) ? "" : (sLineNo)) + "^" +(mgr.isEmpty(sLineDesc)?"":sLineDesc) + "^" ;

         mgr.responseWrite(txt);

      }
      else if ("LINE_NO".equals(val))
      {
         String sContractId   = mgr.readValue("CONTRACT_ID");
         String sLineNo       = mgr.readValue("LINE_NO");
         String sContractName = "";
         String sLineDesc     = "";

         if (sLineNo.indexOf("^") > -1)
         {
            String strAttr = sLineNo;
            sContractId = strAttr.substring(0, strAttr.indexOf("^"));       
            sLineNo =  strAttr.substring(strAttr.indexOf("^") + 1, strAttr.length());                
         }

         if (checksec("SC_SERVICE_CONTRACT_API.Get_Contract_Name",1))
         {
            cmd = trans.addCustomFunction("GETCONTRACTNAME", "SC_SERVICE_CONTRACT_API.Get_Contract_Name", "CONTRACT_NAME");
            cmd.addParameter("CONTRACT_ID", sContractId);
         }

         if (checksec("PSC_CONTR_PRODUCT_API.Get_Description",1))
         {
            cmd = trans.addCustomFunction("GETLINEDESC", "PSC_CONTR_PRODUCT_API.Get_Description", "LINE_DESC");
            cmd.addParameter("CONTRACT_ID", sContractId);
            cmd.addParameter("LINE_NO", sLineNo);
         }

         trans = mgr.validate(trans);

         sContractName  = trans.getValue("GETCONTRACTNAME/DATA/CONTRACT_NAME");
         sLineDesc      = trans.getValue("GETLINEDESC/DATA/LINE_DESC");

         txt =  (mgr.isEmpty(sContractId) ? "" : (sContractId)) + "^" +(mgr.isEmpty(sContractName)?"":sContractName) + "^" +
                (mgr.isEmpty(sLineNo) ? "" : (sLineNo)) + "^" +(mgr.isEmpty(sLineDesc)?"":sLineDesc) + "^" ;

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
      mgr.showAlert(String.valueOf(introFlag));

      itemset.last();
      errFlag = true;

      if (introFlag == 4)
      {
         if (mgr.isEmpty(itemset.getRow().getValue("CATALOG_NO")))
         {
            mgr.showAlert(mgr.translate("PCMWQUICKREPORTININTROSMCATNULL: The field [Sales Part] must have a value."));
            errFlag = false;
            itemset.first();
         }
         else if (mgr.isEmpty(itemset.getRow().getValue("QTY")))
         {
            mgr.showAlert(mgr.translate("PCMWQUICKREPORTININTROSMQTYNULL: The field [Hours] must have a value."));
            errFlag = false;
            itemset.first();
         }
         else if (mgr.isEmpty(itemset.getRow().getValue("ORG_CODE")))
         {
            mgr.showAlert(mgr.translate("PCMWQUICKREPORTININTROSMORGNULL: The field [Maintenance Organization] must have a value."));
            errFlag = false;
            itemset.first();
         }
         else if (mgr.isEmpty(itemset.getRow().getValue("CONTRACT")))
         {
            mgr.showAlert(mgr.translate("PCMWQUICKREPORTININTROSMSITENULL: The field [Site] must have a value."));
            errFlag = false;
            itemset.first();
         }
         else if (mgr.isEmpty(itemset.getRow().getValue("CRE_DATE")))
         {
            mgr.showAlert(mgr.translate("PCMWQUICKREPORTININTROSMCRENULL: The field [Performed Date] must have a value."));
            errFlag = false;
            itemset.first();
         }
      }
      if (errFlag)
      {
         buf = itemset.getRow();

         cmd = trans.addCustomFunction("GETITNAME","Person_Info_API.Get_Name","ITEM_NAME");
         cmd.addParameter("SIGN",itemset.getRow().getValue("SIGN"));

         cmd = trans.addCustomFunction("GETSIG","Employee_API.Get_Signature","SIGN");
         cmd.addParameter("COMPANY",itemset.getRow().getValue("COMPANY"));
         cmd.addParameter("EMP_NO",itemset.getRow().getValue("EMP_NO"));

         trans = mgr.perform(trans);

         tpname = trans.getValue("GETITNAME/DATA/ITEM_NAME");
         tpsign = trans.getValue("GETSIG/DATA/SIGN");

         buf.setValue("SIGN",tpsign);
         buf.setValue("ITEM_NAME",tpname);
         buf.setValue("QTY","");
         buf.setValue("AMOUNT","");

         itemset.addRow(buf);
      }
      assign();
   }


   public void prepareITEM()
   {
      ASPManager mgr = getASPManager();

      itemlay.setLayoutMode(itemlay.CUSTOM_LAYOUT);
      itemset.changeRow();
      itemrolecode = itemset.getRow().getValue("ROLE_CODE");
      itemorgcode = itemset.getRow().getValue("ORG_CODE");       
      itemset.last();
      errFlag = true;

      if (introFlag == 4)
      {
         if (mgr.isEmpty(itemset.getRow().getValue("CATALOG_NO")))
         {
            mgr.showAlert(mgr.translate("PCMWQUICKREPORTININTROSMCATNULL: The field [Sales Part] must have a value."));
            errFlag = false;
            itemset.first();
         }
         else if (mgr.isEmpty(itemset.getRow().getValue("QTY")))
         {
            mgr.showAlert(mgr.translate("PCMWQUICKREPORTININTROSMQTYNULL: The field [Hours] must have a value."));
            errFlag = false;
            itemset.first();
         }
         else if (mgr.isEmpty(itemset.getRow().getValue("ORG_CODE")))
         {
            mgr.showAlert(mgr.translate("PCMWQUICKREPORTININTROSMORGNULL: The field [Maintenance Organization] must have a value."));
            errFlag = false;
            itemset.first();
         }
         else if (mgr.isEmpty(itemset.getRow().getValue("CONTRACT")))
         {
            mgr.showAlert(mgr.translate("PCMWQUICKREPORTININTROSMSITENULL: The field [Site] must have a value."));
            errFlag = false;
            itemset.first();
         }
         else if (mgr.isEmpty(itemset.getRow().getValue("CRE_DATE")))
         {
            mgr.showAlert(mgr.translate("PCMWQUICKREPORTININTROSMCRENULL: The field [Performed Date] must have a value."));
            errFlag = false;
            itemset.first();
         }
      }
      if (errFlag)
      {
         cmd = trans.addEmptyCommand("ITEM","WORK_ORDER_CODING_API.New__",itemblk);
         cmd.setOption("ACTION","PREPARE");

         trans = mgr.perform(trans);

         data = trans.getBuffer("ITEM/DATA");

         data.setValue("EMP_NO",headset.getRow().getValue("EMPNO"));
         data.setValue("SIGN",headset.getRow().getValue("REPORTED_BY"));
         data.setValue("ITEM_NAME",headset.getRow().getValue("NAME"));
         data.setValue("ITEM_COMPANY",headset.getRow().getValue("COMPANY"));
         data.setValue("CRE_DATE",headset.getRow().getValue("REAL_F_DATE"));
         data.setValue("ORG_CODE",headset.getRow().getValue("TMP_ORG_CODE"));   
         data.setValue("ROLE_CODE",itemrolecode);
         data.setValue("CONTRACT",itemset.getRow().getValue("CONTRACT"));
         data.setValue("CATALOG_NO",itemset.getRow().getValue("CATALOG_NO"));
         itemset.addRow(data);
      }
      itemblk.generateAssignments();      
   }


   public void deleteITEM()
   {

      itemset.changeRow();

      if (( introFlag == 4 ) &&  ( itemset.countRows() > 1 ))
      {
         itemset.setRemoved();
         itemset.previous();
      }
      else
      {
         itemset.clear();
         introFlag = introFlag - 1;
      }
      itemblk.generateAssignments();
   }


   public void prepareForm()
   {
      ASPManager mgr = getASPManager();

      store();

      if (( introFlag == 1 ))
      {
         cmd = trans.addEmptyCommand("HEAD","HISTORICAL_SEPARATE_API.New__",headblk);
         cmd.setOption("ACTION","PREPARE");

         cmd = trans.addCustomFunction("GETSITE","User_Default_API.Get_Contract","MCH_CODE_CONTRACT");

         cmd = trans.addCustomFunction("GETCOMP2","Site_API.Get_Company","COMPANY");
         cmd.addReference("MCH_CODE_CONTRACT","GETSITE/DATA");

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
         }
         else
         {
            employeeNo = mgr.readValue("EMPNO");
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
            cmd = trans.addEmptyCommand("ITEM","WORK_ORDER_CODING_API.New__",itemblk);
            cmd.setOption("ACTION","PREPARE");

            cmd = trans.addCustomFunction("GETROLE","Employee_Role_API.Get_Default_Role","ROLE_CODE");
            cmd.addParameter("COMPANY");
            cmd.addParameter("EMP_NO",empno);

            cmd = trans.addCustomFunction("GETORG","Employee_API.Get_Organization","ITEM_ORG_CODE");
            cmd.addParameter("COMPANY");
            cmd.addParameter("EMP_NO",empno);

            if (checksec("Sales_Part_API.Get_List_Price",1))
            {
               cmd = trans.addCustomFunction("GETLP","Sales_Part_API.Get_List_Price","LIST_PRICE");
               cmd.addParameter("ITEM_CONTRACT",mgr.readValue("ITEM_CONTRACT"));
               cmd.addParameter("CATALOG_NO");
            }

            securityOk = "";
            secBuff = (ASPTransactionBuffer)mgr.newASPBuffer();
            secBuff.addSecurityQuery("SALES_PART");

            secBuff = mgr.perform(secBuff);

            if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
               securityOk = "TRUE";

            if ("TRUE".equals(securityOk))
            {
               cmd = trans.addCustomFunction("CATALO","Role_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
               cmd.addReference("ROLE_CODE", "GETROLE/DATA");
               cmd.addParameter("ITEM_CONTRACT",mgr.readValue("ITEM_CONTRACT")); 
            }
         }
         else
         {
            if (itemset.countRows()==0)
            {
               cmd = trans.addEmptyCommand("ITEM","WORK_ORDER_CODING_API.New__",itemblk);
               cmd.setOption("ACTION","PREPARE");

               cmd = trans.addCustomFunction("GETROLE","Employee_Role_API.Get_Default_Role","ROLE_CODE");
               cmd.addParameter("COMPANY");
               cmd.addParameter("EMP_NO",empno);

               cmd = trans.addCustomFunction("GETORG","Employee_API.Get_Organization","ITEM_ORG_CODE");
               cmd.addParameter("COMPANY");
               cmd.addParameter("EMP_NO",empno);

               if (checksec("Sales_Part_API.Get_List_Price",1))
               {
                  cmd = trans.addCustomFunction("GETLP","Sales_Part_API.Get_List_Price","LIST_PRICE");
                  cmd.addParameter("ITEM_CONTRACT",mgr.readValue("ITEM_CONTRACT"));
                  cmd.addParameter("CATALOG_NO");
               }

               securityOk = "";
               secBuff = (ASPTransactionBuffer)mgr.newASPBuffer();
               secBuff.addSecurityQuery("SALES_PART");

               secBuff = mgr.perform(secBuff);

               if (secBuff.getSecurityInfo().itemExists("SALES_PART"))
                  securityOk = "TRUE";

               if ("TRUE".equals(securityOk))
               {
                  cmd = trans.addCustomFunction("CATALO","Role_Sales_Part_API.Get_Def_Catalog_No","CATALOG_NO");
                  cmd.addReference("ROLE_CODE", "GETROLE/DATA");
                  cmd.addParameter("ITEM_CONTRACT",mgr.readValue("ITEM_CONTRACT"));

               }
               countOfNext =0;

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
            String hempName = trans.getValue("GETNAME/DATA/NAME");
            String hempId = trans.getValue("EMPID/DATA/EMPNO");

            data.setFieldItem("REPORTED_BY",repBy);
            data.setFieldItem("TMP_ORG_CODE",horgCode);
            data.setFieldItem("EMPNO",hempId);
            data.setFieldItem("NAME",hempName);
            countOfNext1 = 1;
         }

         headset.addRow(data);
      }
      if (introFlag == 2)
      {
         horgcode = trans.getValue("HGETORG/DATA/ORG_CODE");


      }
      if (introFlag == 4)
      {
         if (countOfNext == 0)
         {
            data = trans.getBuffer("ITEM/DATA");
            //Bug 72265, start
            if (mgr.isEmpty(empno))
            {
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
            data.setValue("ITEM_NAME",headset.getRow().getValue("NAME"));
            data.setValue("ITEM_COMPANY",headset.getRow().getValue("COMPANY"));
            data.setValue("CRE_DATE",headset.getRow().getValue("REAL_F_DATE"));
            data.setValue("ROLE_CODE",tprolecode);
            data.setValue("ORG_CODE",headset.getRow().getValue("TMP_ORG_CODE"));


            if ("TRUE".equals(securityOk))
               data.setValue("CATALOG_NO",trans.getValue("CATALO/DATA/CATALOG_NO"));

            itemset.addRow(data);
            countOfNext =1;
         }
         else

         {
            if (itemset.countRows()>0)
               itemset.last();
         }

      }
      assign();
   }


   public void previousForm()
   {
      ASPManager mgr = getASPManager();

      store();

      if (introFlag == 2)
      {

         buf = mgr.newASPBuffer();
         buf.addFieldItem("REAL_S_DATE",mgr.readValue("REAL_S_DATE"));
         buf.addFieldItem("REAL_F_DATE",mgr.readValue("REAL_F_DATE"));
         String realSdate = buf.getFieldValue("REAL_S_DATE") ;
         String realFdate = buf.getFieldValue("REAL_F_DATE") ;

         row = headset.getRow();
         row.setValue ("TMP_ORG_CODE",mgr.readValue("ORG_CODE",""));
         row.setValue ("ORG_CODE",mgr.readValue("ORG_CODE",""));
         row.setValue ("ERR_DESCR",mgr.readValue("ERR_DESCR",""));
         row.setValue ("CONTRACT",mgr.readValue("CONTRACT",""));
         row.setFieldItem("REAL_S_DATE",realSdate);
         row.setFieldItem("REAL_F_DATE",realFdate);


         headset.addRow(row);
      }

      if (introFlag == 4)
         itemset.changeRow();

      if (introFlag == 2)
         win2Flag = 1;

      if (introFlag != 1)
         introFlag = introFlag - 1;
      else
         mgr.showAlert(mgr.translate("PCMWQUICKREPORTININTROSMFIRSTPG: This is the first "));

      if (introFlag == 1)
         win2Flag = 1;
//  		Bug 69168, Start
      if (introFlag == 3)
      {
         workDone = mgr.readValue("WORK_DONE","");
         workDoneLo = mgr.readValue("PERFORMED_ACTION_LO", "");
      }
//  		Bug 69168, End
      assign();
   }


   public void nextForm()
   {
      ASPManager mgr = getASPManager();

      store();
      errFlag = true;

      if (introFlag == 4)
         mgr.showAlert(mgr.translate("PCMWQUICKREPORTININTROSMFINISHBUT: This is the last "));

      if (( errFlag ) &&  ( introFlag != 4 ))
      {
         introFlag = introFlag + 1;

         if (( introFlag < 4 ) ||  (( introFlag == 4 )))
            prepareForm();

         if (introFlag == 2)
         {
            row = headset.getRow();
            /*if (win2Flag == 0)
            {
                row.setValue("ERR_DESCR","Quick Reported");
            }*/
            row.setValue("ORG_CODE",headset.getRow().getValue("TMP_ORG_CODE"));
            headset.setRow(row);                      
         }

      }
//  		Bug 69168, Start
      if (introFlag == 3)
      {
         workDone = mgr.readValue("WORK_DONE","");
         workDoneLo = mgr.readValue("PERFORMED_ACTION_LO", "");
      }
//  		Bug 69168, End
      assign();
   }

    //Bug 79242, Start
    public void addPreposting()
    {
        ASPManager mgr = getASPManager();
	int enabl10 = 0;
	
	if (headset.countRows()>0)
	    headset.changeRow();

	if (itemset.countRows()>0)
	    itemset.changeRow();
                
	if (introFlag == 3)
        {
            workDone = mgr.readValue("WORK_DONE_DISP","");
            workDoneLo = mgr.readValue("PERFORMED_ACTION_LO_DISP");
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
      String error_codes = "";

      if (introFlag == 1)
      {
         mgr.showAlert(mgr.translate("PCMWQUICKREPORTININTROSMGONEXT: Go to next page to finish."));    
         errFlag = false;
      }

      if (mgr.isEmpty(mgr.readValue("REAL_S_DATE")))
         sdate = "";
      else
         sdate   = headset.getRow().getValue("REAL_S_DATE");

      if (introFlag == 3 &&  ( mgr.isEmpty(mgr.readValue("WORK_DONE")) ))
      {
         errFlag = false;
      }

      if ((introFlag >= 2) && (errFlag))
      {

         String work_type_id=headset.getRow().getValue("WORK_TYPE_ID");
         if (mgr.isEmpty(work_type_id))
            work_type_id="";
         String agreement_id=headset.getRow().getValue("AGREEMENT_ID");
         if (mgr.isEmpty(agreement_id))
            agreement_id="";
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
         String contract_id = headset.getRow().getValue("CONTRACT_ID");
         if (mgr.isEmpty(contract_id))
            contract_id="";
         String line_no=headset.getRow().getValue("LINE_NO");
         if (mgr.isEmpty(line_no))
            line_no="";
         //String reported_by_id=itemset.getRow().getValue("EMP_NO");
         String reported_by_id=headset.getRow().getValue("EMPNO");

         if (mgr.isEmpty(reported_by_id))
            reported_by_id="";

         sAttr = sAttr + "PM_TYPE" + String.valueOf((char)31) +headset.getRow().getValue("PM_TYPE")+ String.valueOf((char)30)
                 + "REG_DATE" + String.valueOf((char)31) +headset.getRow().getValue("REG_DATE")+ String.valueOf((char)30)
                 + "REPORTED_BY_ID" + String.valueOf((char)31) +reported_by_id+ String.valueOf((char)30)
                 + "MCH_CODE_CONTRACT" + String.valueOf((char)31) +headset.getRow().getValue("MCH_CODE_CONTRACT")+ String.valueOf((char)30)
                 + "CONTRACT" + String.valueOf((char)31) +headset.getRow().getValue("CONTRACT")+ String.valueOf((char)30)
                 + "COMPANY" + String.valueOf((char)31) +headset.getRow().getValue("COMPANY")+ String.valueOf((char)30)
                 + "MCH_CODE" + String.valueOf((char)31) +headset.getRow().getValue("MCH_CODE")+ String.valueOf((char)30)
                 + "ORG_CODE" + String.valueOf((char)31) +headset.getRow().getValue("ORG_CODE")+ String.valueOf((char)30)
                 + "WORK_TYPE_ID" + String.valueOf((char)31) +work_type_id+ String.valueOf((char)30)
                 + "CUSTOMER_NO" + String.valueOf((char)31) +headset.getRow().getValue("CUSTOMER_NO")+ String.valueOf((char)30)
                 + "AUTHORIZE_CODE" + String.valueOf((char)31) +headset.getRow().getValue("AUTHORIZE_CODE")+ String.valueOf((char)30)
                 + "ERR_DESCR" + String.valueOf((char)31) +headset.getRow().getValue("ERR_DESCR")+ String.valueOf((char)30)
                 + "AGREEMENT_ID" + String.valueOf((char)31) +agreement_id+ String.valueOf((char)30)
                 + "CONTRACT_ID" + String.valueOf((char)31) +contract_id+ String.valueOf((char)30)
                 + "LINE_NO" + String.valueOf((char)31) +line_no+ String.valueOf((char)30)
                 + "REAL_S_DATE" + String.valueOf((char)31) +sdate+ String.valueOf((char)30)
                 + "REAL_F_DATE" + String.valueOf((char)31) +headset.getRow().getValue("REAL_F_DATE")+ String.valueOf((char)30);

         sMchCode =  headset.getRow().getValue("MCH_CODE");
         if (introFlag >= 3)
         {
            sAttr = sAttr + "WORK_DONE" + String.valueOf((char)31) +headset.getRow().getValue("WORK_DONE")+ String.valueOf((char)30)
                    + "PERFORMED_ACTION_LO" + String.valueOf((char)31) +performed_action_lo+ String.valueOf((char)30)
                    + "ERR_SYMPTOM" + String.valueOf((char)31) +err_symptom+ String.valueOf((char)30)
                    + "ERR_CAUSE" + String.valueOf((char)31) +err_cause+ String.valueOf((char)30)
                    + "ERR_CLASS" + String.valueOf((char)31) +err_class+ String.valueOf((char)30)
                    + "ERR_TYPE" + String.valueOf((char)31) +err_type+ String.valueOf((char)30)
                    + "ERR_DISCOVER_CODE" + String.valueOf((char)31) +err_discover_code+ String.valueOf((char)30)
                    + "PERFORMED_ACTION_ID" + String.valueOf((char)31) +performed_action_id+ String.valueOf((char)30);

         }

         sWoNo = ctx.getGlobal("WO_NO");
         if (("0".equals(sWoNo)))
         {  
            int beg_pos;
            int end_pos;
            trans.clear();
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

            trans.clear();
           
            ctx.setGlobal("OBJ_ID", objid);
            ctx.setGlobal("OBJ_VER", objversion);
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
               itemset.changeRow();

               if (mgr.isEmpty(itemset.getRow().getValue("ORG_CODE")))
               {
                  errFlag = false;
                  itemset.first();
               }

               if (mgr.isEmpty(itemset.getRow().getValue("QTY")))
               {
                  errFlag = false;
                  itemset.first();
               }

               if (mgr.isEmpty(itemset.getRow().getFieldValue("ITEM_CONTRACT")))
               {
                  errFlag = false;
                  itemset.first();
               }

               if (mgr.isEmpty(itemset.getRow().getValue("CRE_DATE")))
               {
                  errFlag = false;
                  itemset.first();
               }
            }

                if (( introFlag >= 3 ) &&  !( mgr.isEmpty(sWoNo) ) && errFlag  && (countOfNext>0))
                {
                    if (itemset.countRows()>0)
                    {
		                  itemset.changeRow();
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

                  int n = itemset.countRows();
                  itemset.first();

                  for (count=1; count<=n; ++count)
                  {
                     errFlag2 = true;
                     String emp_no=itemset.getRow().getValue("EMP_NO");
                     if (mgr.isEmpty(emp_no))
                     {
                        trans.clear();
                        cmd = trans.addCustomFunction("EMPID","COMPANY_EMP_API.Get_Max_Employee_Id","EMPNO");
                        cmd.addParameter("COMPANY",itemset.getRow().getValue("COMPANY"));
                        cmd.addParameter("REPORTED_BY",headset.getRow().getValue("REPORTED_BY"));
                        trans = mgr.perform(trans);
                        emp_no = trans.getValue("EMPID/DATA/EMPNO");

                        if (mgr.isEmpty(emp_no))
                           emp_no="";
                     }
                     String list_price=itemset.getRow().getValue("LIST_PRICE");
                     if (mgr.isEmpty(list_price))
                        list_price="";
                     String role_code=itemset.getRow().getValue("ROLE_CODE");
                     if (mgr.isEmpty(role_code))
                        role_code="";
                     if (mgr.isEmpty(itemset.getRow().getValue("ORG_CODE")))
                        errFlag2 = false;


                     if (mgr.isEmpty(itemset.getRow().getValue("QTY")))
                        errFlag2 = false;

                     if (mgr.isEmpty(itemset.getRow().getValue("AMOUNT")))
                        errFlag2 = false;

                     if (mgr.isEmpty(itemset.getRow().getFieldValue("ITEM_CONTRACT")))
                        errFlag2 = false;


                     if (mgr.isEmpty(itemset.getRow().getValue("CRE_DATE")))
                        errFlag2 = false;

                     if (mgr.isEmpty(itemset.getRow().getValue("CATALOG_NO")))
                        errFlag2 = false;
                     if (errFlag2)
                     {

                        sComment = itemset.getRow().getValue("ITEM_NAME") + ", " + itemset.getRow().getValue("ORG_CODE") + " (" + itemset.getRow().getValue("ROLE_CODE") + ")" + itemset.getRow().getValue("CATALOG_NO") + ".";
                        sAttr = "";
                        sAttr = sAttr + "CMNT" + String.valueOf((char)31) +sComment+ String.valueOf((char)30)
                                + "EMP_NO" + String.valueOf((char)31) +emp_no+ String.valueOf((char)30)
                                + "WORK_ORDER_COST_TYPE" + String.valueOf((char)31) +sCostType+ String.valueOf((char)30)
                                + "WORK_ORDER_ACCOUNT_TYPE" + String.valueOf((char)31) +sAccountType+ String.valueOf((char)30)
                                + "SIGNATURE" + String.valueOf((char)31) +sSignature+ String.valueOf((char)30)
                                + "SIGNATURE_ID" + String.valueOf((char)31) +sSignature+ String.valueOf((char)30)
                                + "WORK_ORDER_BOOK_STATUS" + String.valueOf((char)31) +sBookStatus+ String.valueOf((char)30)
                                + "CONTRACT" + String.valueOf((char)31) +itemset.getRow().getValue("CONTRACT")+ String.valueOf((char)30)
                                + "COMPANY" + String.valueOf((char)31) +itemset.getRow().getValue("COMPANY")+ String.valueOf((char)30)
                                + "ORG_CODE" + String.valueOf((char)31) +itemset.getRow().getValue("ORG_CODE")+ String.valueOf((char)30)
                                + "CRE_DATE" + String.valueOf((char)31) +itemset.getRow().getValue("CRE_DATE")+ String.valueOf((char)30)
                                + "CATALOG_NO" + String.valueOf((char)31) +itemset.getRow().getValue("CATALOG_NO")+ String.valueOf((char)30)
                                + "LIST_PRICE" + String.valueOf((char)31) +list_price+ String.valueOf((char)30)
                                + "MCH_CODE" + String.valueOf((char)31) +headset.getRow().getValue("MCH_CODE")+ String.valueOf((char)30)
                                + "ROLE_CODE" + String.valueOf((char)31) +role_code+ String.valueOf((char)30)
                                + "QTY" + String.valueOf((char)31) +itemset.getRow().getValue("QTY")+ String.valueOf((char)30)
                                + "WO_NO" + String.valueOf((char)31) +sWoNo+ String.valueOf((char)30)
                                + "MCH_CODE_CONTRACT" + String.valueOf((char)31) +headset.getRow().getValue("MCH_CODE_CONTRACT")+ String.valueOf((char)30)
                                + "AMOUNT" + String.valueOf((char)31) +itemset.getRow().getValue("AMOUNT")+ String.valueOf((char)30);


                        
                        if (itemset.getRow().getNumberValue("QTY") > 0 )
                        {

			   //Bug 79242, Start
		           cmd = trans.addCustomCommand("HISTSEPNEW"+count,"Work_Order_Coding_API.New__");

                           cmd.addParameter("INFO","");
                           cmd.addParameter("OBJID");
                           cmd.addParameter("OBJVERSION");
                           cmd.addParameter("ATTR",sAttr);
                           cmd.addParameter("ACTION","DO");
			   //Bug 79242, End

                        }
                     }
                     itemset.next();
                }
		    //Bug 79242, Start
		    trans=mgr.perform(trans);
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
            cmd = trans.addCustomCommand("FINI", "ACTIVE_SEPARATE_API.REPORT__"); 
            cmd.addParameter("INFO","");     
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
	//Bug 79242, Start
        ASPManager mgr = getASPManager();

        trans.clear();
        
	cmd = trans.addCustomCommand("DELPREPOST","Active_Separate_API.Delete_Temp_Pre_Posting");
	cmd.addParameter("PRE_ACCOUNTING_ID", sTempPrePostId);
        trans = mgr.perform(trans);
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

      itemset.clear();
      itemtbl.clearQueryRow();

      assign();
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR BROWSE FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void backwardITEM()
   {
      ASPManager mgr = getASPManager();

      itemset.changeRow();
      if (!itemset.previous())
         mgr.showAlert(mgr.translate("PCMWQUICKREPORTININTROSMFIRSTROW: This is the first line."));
      assign();
   }


   public void forwardITEM()
   {
      ASPManager mgr = getASPManager();

      itemset.changeRow();
      if (!itemset.next())
         mgr.showAlert(mgr.translate("PCMWQUICKREPORTININTROSMLASTROW: This is the last line."));
      assign();
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

      f = headblk.addField("REG_DATE","Datetime");
      f.setSize(10);
      f.setHidden();

      f = headblk.addField("CUSTOMER_NO");
      f.setSize(11);
      f.setDynamicLOV("CUSTOMER_INFO",600,450);
      f.setMandatory();
      f.setLabel("PCMWQUICKREPORTININTROSMCUSTNO: Customer No:");
      f.setUpperCase();
      f.setMandatory();
      f.setMaxLength(20);
      //f.setCustomValidation("CUSTOMER_NO,MCH_CODE_CONTRACT, MCH_CODE, WORK_TYPE_ID, REG_DATE","AGREEMENT_ID");

      f = headblk.addField("MCH_CODE_CONTRACT");
      f.setSize(8);
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
      f.setLOVProperty("TITLE",mgr.translate("PCMWQUICKREPORTININTROSMCONLOV: List of Site"));
      f.setMandatory();
      f.setLabel("PCMWQUICKREPORTININTROSMCON: Site:");
      f.setUpperCase();
      f.setMaxLength(5);

      f = headblk.addField("MCH_CODE");
      f.setSize(27);
      f.setLOV("MaintenanceObjectLov1.page","MCH_CODE_CONTRACT CONTRACT",600,450);
      f.setMandatory();
      f.setLabel("PCMWQUICKREPORTININTROSMMCHCODE: Object ID:");
      f.setUpperCase();
      f.setMandatory();
      f.setMaxLength(100);
      f.setCustomValidation("CUSTOMER_NO,MCH_CODE_CONTRACT,MCH_CODE,WORK_TYPE_ID,REG_DATE","MCH_CODE_DESCRIPTION,MCH_CODE_CONTRACT,MCH_CODE");

      f = headblk.addField("MCH_CODE_DESCRIPTION");
      f.setSize(30);
      f.setReadOnly();
      f.setMaxLength(45);
      f.setLabel("PCMWQUICKREPORTININTROSMMCHODEDESC: Object Description:");

      f = headblk.addField("WORK_TYPE_ID");
      f.setSize(11);
      f.setDynamicLOV("WORK_TYPE",600,450);
      f.setLabel("PCMWQUICKREPORTININTROSMWTID: Work Type:");
      f.setUpperCase();
      f.setMaxLength(20);
      //f.setCustomValidation("CUSTOMER_NO,MCH_CODE_CONTRACT, MCH_CODE, WORK_TYPE_ID, REG_DATE","AGREEMENT_ID");

      f = headblk.addField("AUTHORIZE_CODE");
      f.setDynamicLOV("ORDER_COORDINATOR_LOV",600,450);
      f.setMandatory();
      f.setUpperCase();
      f.setSize(20);
      f.setLabel("PCMWQUICKREPORTININTROSMAUTHORIZE_CODE: Coordinator"); 

      f = headblk.addField("CONTRACT_ID");                     
      f.setLOV("PscContrProductLov.page","CONTRACT,CUSTOMER_NO,MCH_CODE,WORK_TYPE_ID");
      f.setCustomValidation("CONTRACT_ID,LINE_NO","CONTRACT_ID,CONTRACT_NAME,LINE_NO,LINE_DESC");
      f.setUpperCase();
      f.setMaxLength(15);
      f.setDefaultNotVisible();
      f.setLabel("PCMWQUICKREPORTININTROSMCONTRACTID: Contract ID:");
      f.setSize(15);

      f = headblk.addField("CONTRACT_NAME");                     
      f.setDefaultNotVisible();
      //Bug 84436, Start
      if (mgr.isModuleInstalled("SRVCON"))
         f.setFunction("SC_SERVICE_CONTRACT_API.Get_Contract_Name(:CONTRACT_ID)");
      else
            f.setFunction("''");
      //Bug 84436, End 
      f.setReadOnly();
      f.setLabel("PCMWQUICKREPORTININTROSMCONTRACNAME: Contract Name:");
      f.setSize(15);      

      f = headblk.addField("LINE_NO");
      f.setLOV("PscContrProductLov.page","CONTRACT,CUSTOMER_NO,MCH_CODE,WORK_TYPE_ID,CONTRACT_ID");
      f.setCustomValidation("CONTRACT_ID,LINE_NO","CONTRACT_ID,CONTRACT_NAME,LINE_NO,LINE_DESC");
      f.setDefaultNotVisible();
      f.setLabel("PCMWQUICKREPORTININTROSMLINENO: Line No:");
      f.setSize(10);             

      f = headblk.addField("LINE_DESC");                     
      f.setDefaultNotVisible();
      //Bug 84436, Start
      if (mgr.isModuleInstalled("PCMSCI"))
            f.setFunction("PSC_CONTR_PRODUCT_API.Get_Description(:CONTRACT_ID,:LINE_NO)");
      else
            f.setFunction("''");
      //Bug 84436, End 
      f.setReadOnly();
      f.setLabel("PCMWQUICKREPORTININTROSMLINEDESC: Description:");
      f.setSize(15);

      f = headblk.addField("REPORTED_BY");
      f.setSize(8);
      f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,450);
      f.setMandatory();
      f.setLabel("PCMWQUICKREPORTININTROSMREPBY: Signature:");
      f.setUpperCase();
      f.setMandatory();
      f.setMaxLength(20);
      f.setCustomValidation("REPORTED_BY,COMPANY,REG_DATE,ORG_CODE","NAME,EMPNO,SYSDATE,TMP_ORG_CODE");

      f = headblk.addField("EMPNO");
      f.setSize(6);
      f.setHidden();
      f.setFunction("COMPANY_EMP_API.Get_Max_Employee_Id(:COMPANY,:REPORTED_BY)");

      f = headblk.addField("NAME");
      f.setSize(20);
      f.setHidden();
      f.setFunction("PERSON_INFO_API.Get_Name(:REPORTED_BY)");

      f = headblk.addField("TMP_ORG_CODE");
      f.setHidden();
      f.setUpperCase();

      f = headblk.addField("AGREEMENT_ID");
      f.setSize(11);
      f.setDynamicLOV("CUST_AGR_OBJ_LOV","MCH_CODE_CONTRACT,MCH_CODE,WORK_TYPE_ID,CUSTOMER_NO",600,450);
      f.setLabel("PCMWQUICKREPORTININTROSMAGRID: Agreement ID:");
      f.setHidden();
      f.setMaxLength(10);

      f = headblk.addField("SYSDATE","Datetime");
      f.setSize(0);
      f.setHidden();

      f = headblk.addField("PM_TYPE");
      f.setSize(0);
      f.setHidden();

      f = headblk.addField("COMPANY");
      f.setSize(6);
      f.setHidden();
      f.setUpperCase();

      f = headblk.addField("USERID");
      f.setHidden();
      f.setFunction("''");

      f = headblk.addField("GET_FND_USER");
      f.setHidden();
      f.setFunction("''");



      /////////////////// Directive /////////////////////

      f = headblk.addField("ERR_DESCR");
      f.setSize(30);
      f.setMandatory();
      f.setLabel("PCMWQUICKREPORTININTROSMERRDES: Directive:");
      f.setMaxLength(60);

      f = headblk.addField("CONTRACT");
      f.setSize(8);
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
      f.setLOVProperty("TITLE",mgr.translate("PCMWQUICKREPORTININTROSMCONLOV2: Site"));
      f.setMandatory();
      f.setLabel("PCMWQUICKREPORTININTROSMCON2: Site:");
      f.setUpperCase();
      f.setMaxLength(5);
      f.setCustomValidation("CUSTOMER_NO,CONTRACT,MCH_CODE,WORK_TYPE_ID,REG_DATE","COMPANY");

      f = headblk.addField("ORG_CODE");
      f.setSize(8);
      f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","CONTRACT",600,450);
      f.setLOVProperty("TITLE",mgr.translate("PCMWQUICKREPORTININTROSMORCOLOV: List of Maintenance Organization"));
      f.setMandatory();
      f.setLabel("PCMWQUICKREPORTININTROSMORGCODE: Maintenance Organization:");
      f.setUpperCase();
      f.setMaxLength(8);

      f = headblk.addField("REAL_S_DATE","Datetime");
      f.setSize(22);
      f.setLabel("PCMWQUICKREPORTININTROSMRSDATE: Actual Start:");
      f.setCustomValidation("REAL_S_DATE","REAL_S_DATE");

      f = headblk.addField("REAL_F_DATE","Datetime");
      f.setSize(22);
      f.setMandatory();
      f.setLabel("PCMWQUICKREPORTININTROSMRFDATE: Actual Completion:");
      f.setCustomValidation("REAL_F_DATE","REAL_F_DATE");



      /////////////////// Classification /////////////////////

//  		Bug 69168, Start
      f = headblk.addField("WORK_DONE");
      f.setSize(20);
      f.setLabel("PCMWQUICKREPORTININTROSMWRKDONE: Work Done:");
      f.setMandatory();
      f.setMaxLength(60);
      f.setMandatory();
      f.setHidden();

      f = headblk.addField("PERFORMED_ACTION_LO");
      f.setSize(25);
      f.setLabel(" ");  
      f.setHeight(2);  
      f.setMaxLength(2000);
      f.setHidden();
//  		Bug 69168, End

      f = headblk.addField("ERR_SYMPTOM");
      f.setSize(6);
      f.setDynamicLOV("WORK_ORDER_SYMPT_CODE",600,450);
      f.setLabel("PCMWQUICKREPORTININTROSMERSYMP: Symptom:");
      f.setUpperCase();
      f.setMaxLength(3);

      f = headblk.addField("ERR_CLASS");
      f.setSize(6);
      f.setDynamicLOV("WORK_ORDER_CLASS_CODE",600,450);
      f.setLabel("PCMWQUICKREPORTININTROSMERCLS: Class:");
      f.setUpperCase();
      f.setMaxLength(3);

      f = headblk.addField("ERR_DISCOVER_CODE");
      f.setSize(6);
      f.setDynamicLOV("WORK_ORDER_DISC_CODE",600,450);
      f.setLabel("PCMWQUICKREPORTININTROSMERDISCODE: Discovery:");
      f.setUpperCase();
      f.setMaxLength(3);

      f = headblk.addField("ERR_CAUSE");
      f.setSize(6);
      f.setDynamicLOV("MAINTENANCE_CAUSE_CODE",600,450);
      f.setLabel("PCMWQUICKREPORTININTROSMERCAUSE: Cause:");
      f.setUpperCase();
      f.setMaxLength(3);

      f = headblk.addField("ERR_TYPE");
      f.setSize(6);
      f.setDynamicLOV("WORK_ORDER_TYPE_CODE",600,450);
      f.setLabel("PCMWQUICKREPORTININTROSMERTYPE: Type:");
      f.setUpperCase();
      f.setMaxLength(3);

      f = headblk.addField("PERFORMED_ACTION_ID");
      f.setSize(6);
      f.setDynamicLOV("MAINTENANCE_PERF_ACTION",600,450);
      f.setLabel("PCMWQUICKREPORTININTROSMPAID: Performed Action:");
      f.setUpperCase();
      f.setMaxLength(3);

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
      headblk.setTitle(mgr.translate("PCMWQUICKREPORTININTROSMBLKTITLE: Quick Report in Work Wizard for SM"));
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.removeCommandGroup(headbar.CMD_GROUP_CUSTOM);
      headtbl = mgr.newASPTable(headblk);
      headtbl.setWrap();
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.NEW_LAYOUT);
      headlay.setEditable();

      headlay.setSimple("CONTRACT_NAME");
      headlay.setSimple("LINE_DESC");

      headbar.disableMinimize();
      headlay.showBottomLine(false);
      
      b = mgr.newASPBlock("PM_TYPE");

      b.addField("CLIENT_VALUES0");

      blkPost = mgr.newASPBlock("POSTI");
      f = blkPost.addField("CODE_A");
      f = blkPost.addField("CODE_B");
      f = blkPost.addField("CONTROL_TYPE");
      f = blkPost.addField("STR_CODE");

      /////////////////// Time Report /////////////////////

      itemblk = mgr.newASPBlock("ITEM");

      f = itemblk.addField("ITEM_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk.addField("ITEM_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk.addField("EMP_NO");
      f.setSize(20);
      f.setDynamicLOV("EMPLOYEE_NO","COMPANY",600,450);
      f.setLabel("PCMWQUICKREPORTININTROSMEMPLNO: Employee:");
      f.setUpperCase();
      f.setMaxLength(11);
      f.setCustomValidation("COMPANY,EMP_NO,QTY,ITEM_CONTRACT,AMOUNT,CATALOG_NO,ROLE_CODE,ITEM_ORG_CODE","SIGN,ITEM_NAME,ROLE_CODE,ITEM_ORG_CODE,AMOUNT,CATALOG_NO,EMP_NO,ITEM_CONTRACT");

      f = itemblk.addField("SIGN");
      f.setSize(20);
      f.setLabel("PCMWQUICKREPORTININTROSMSIGN: Signature:");
      f.setUpperCase();
      f.setReadOnly();
      f.setFunction("''");
      f.setMaxLength(20);

      f = itemblk.addField("ITEM_CONTRACT");
      f.setSize(20);
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);
      f.setLOVProperty("TITLE",mgr.translate("PCMWQUICKREPORTININTROSMCONT1LOV: List of Site"));
      f.setMandatory();
      f.setLabel("PCMWQUICKREPORTININTROSMITEMCONT: Site:");
      f.setUpperCase();
      f.setDbName("CONTRACT");
      f.setMaxLength(5);
      f.setCustomValidation("ITEM_CONTRACT", "ITEM_COMPANY");

      f = itemblk.addField("ITEM_ORG_CODE");
      f.setSize(20);
      f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ITEM_CONTRACT CONTRACT",600,450);
      f.setLOVProperty("TITLE",mgr.translate("PCMWQUICKREPORTININTROSMORCO1LOV: List of Maintenance Organization"));
      f.setMandatory();
      f.setLabel("PCMWQUICKREPORTININTROSMITORGCODE: Maintenance Organization:"); 
      f.setUpperCase();
      f.setDbName("ORG_CODE");
      f.setMaxLength(8);
      f.setCustomValidation("ROLE_CODE,QTY,AMOUNT,ITEM_ORG_CODE,CATALOG_NO,ITEM_CONTRACT,EMP_NO,CONTRACT", "AMOUNT,CATALOG_NO,ITEM_ORG_CODE,ITEM_CONTRACT");    

      f = itemblk.addField("ROLE_CODE");
      f.setSize(20);
      f.setDynamicLOV("ROLE_TO_SITE_LOV","ITEM_CONTRACT CONTRACT",600,450);
      f.setLabel("PCMWQUICKREPORTININTROSMROLECODE: Craft:");
      f.setUpperCase();
      f.setMaxLength(10);
      f.setCustomValidation("ROLE_CODE,QTY,AMOUNT,ITEM_ORG_CODE,CATALOG_NO,ITEM_CONTRACT,EMP_NO,CONTRACT", "AMOUNT,CATALOG_NO,ROLE_CODE,ITEM_CONTRACT");

      f = itemblk.addField("CATALOG_NO");
      f.setSize(20);
      f.setDynamicLOV("SALES_PART_SERVICE_LOV", "ITEM_CONTRACT CONTRACT",600,450);
      f.setLabel("PCMWQUICKREPORTININTROSMCATALOGNO: Sales Part:");
      f.setMaxLength(25);
      f.setUpperCase();
      f.setMandatory();

      f = itemblk.addField("CRE_DATE","Datetime");
      f.setSize(24);
      f.setMandatory();
      f.setLabel("PCMWQUICKREPORTININTROSMCREDATE: Performed Date:");
      f.setCustomValidation("CRE_DATE","CRE_DATE");

      f = itemblk.addField("QTY","Number");
      f.setSize(20);
      f.setMandatory();
      f.setLabel("PCMWQUICKREPORTININTROSMQTY: Hours:");
      f.setCustomValidation("ROLE_CODE,QTY,AMOUNT,ITEM_ORG_CODE,ITEM_CONTRACT", "AMOUNT");

      f = itemblk.addField("AMOUNT","Money");
      f.setSize(20);
      f.setLabel("PCMWQUICKREPORTININTROSMAMT: Cost Amount:");

      f = itemblk.addField("ITEM_COMPANY");
      f.setSize(0);
      f.setMandatory();
      f.setHidden();
      f.setUpperCase();
      f.setDbName("COMPANY");

      f = itemblk.addField("ITEM_NAME");
      f.setSize(20);
      f.setHidden();
      f.setFunction("''");

      f = itemblk.addField("COMMENT");
      f.setHidden();

      f = itemblk.addField("ACCOUNT_TYPE");
      f.setHidden();

      f = itemblk.addField("COST_TYPE");
      f.setHidden();

      f = itemblk.addField("SIGNATURE");
      f.setHidden();

      f = itemblk.addField("BOOK_STATUS");
      f.setHidden();

      f = itemblk.addField("LIST_PRICE");
      f.setHidden();

      f = itemblk.addField("COST");
      f.setHidden();
      f.setFunction("''");

      f = itemblk.addField("NUM_ZERO","Number");
      f.setHidden();

      f = itemblk.addField("UNTRANSFERED_ROWS");
      f.setHidden();
      f.setFunction("''");

      f = itemblk.addField("TRANSFERED_LINES");
      f.setHidden();
      f.setFunction("''");

      f = itemblk.addField("ITEM_WO_NO","Number");
      f.setSize(11);
      f.setHidden();
      f.setDbName("WO_NO");

      itemblk.setView("WORK_ORDER_CODING");
      itemblk.defineCommand("WORK_ORDER_CODING_API","New__,Modify__,Remove__");
      itemblk.setTitle(mgr.translate("PCMWQUICKREPORTININTROSMBLKTITLE: Quick Report in Work Wizard for SM"));
      itemset = itemblk.getASPRowSet();

      itembar = mgr.newASPCommandBar(itemblk);
      itembar.removeCommandGroup(itembar.CMD_GROUP_CUSTOM);

      itemtbl = mgr.newASPTable(itemblk);
      itemtbl.disableRowStatus();
      itemtbl.setWrap();
      itemlay = itemblk.getASPBlockLayout();
      itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);

      itembar.enableCommand(itembar.FORWARD);
      itembar.enableCommand(itembar.BACKWARD);
      itemlay.setEditable();
      itemlay.setDialogColumns(1);

      itembar.disableMinimize();

      itemlay.showBottomLine(false);

      disableOptions();
      disableHomeIcon();
      disableNavigate();
   }


   public void adjust()
   {
      ASPManager mgr = getASPManager();

      frm.setFormWidth(708);
      headbar.setWidth(frm.getFormWidth());   
      headbar.disableCommand(ASPCommandBar.SAVERETURN);
      headbar.disableCommand(ASPCommandBar.SAVENEW);
      headbar.disableCommand(ASPCommandBar.CANCELNEW);
      
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
         mgr.getASPField("CUSTOMER_NO").setHidden();
         mgr.getASPField("AUTHORIZE_CODE").setHidden();
         mgr.getASPField("MCH_CODE_CONTRACT").setHidden();
         mgr.getASPField("MCH_CODE").setHidden();
         mgr.getASPField("MCH_CODE_DESCRIPTION").setHidden();      
         mgr.getASPField("WORK_TYPE_ID").setHidden();
         mgr.getASPField("REPORTED_BY").setHidden();
         mgr.getASPField("AGREEMENT_ID").setHidden();
         mgr.getASPField("CONTRACT_ID").setHidden();
         mgr.getASPField("LINE_NO").setHidden();
         mgr.getASPField("CONTRACT_NAME").setHidden();
         mgr.getASPField("LINE_DESC").setHidden();
      }

      if (introFlag != 2)
      {
         mgr.getASPField("ERR_DESCR").setHidden();
         mgr.getASPField("ORG_CODE").setHidden();
         mgr.getASPField("REAL_S_DATE").setHidden();
         mgr.getASPField("REAL_F_DATE").setHidden();
         mgr.getASPField("CONTRACT").setHidden();
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
         mgr.getASPField("SIGN").setHidden();
         mgr.getASPField("ITEM_CONTRACT").setHidden();
         mgr.getASPField("ITEM_ORG_CODE").setHidden();
         mgr.getASPField("ROLE_CODE").setHidden();
         mgr.getASPField("CATALOG_NO").setHidden();
         mgr.getASPField("CRE_DATE").setHidden();
         mgr.getASPField("QTY").setHidden();
         mgr.getASPField("AMOUNT").setHidden();
      }

      itembar.disableCommandGroup(itembar.CMD_GROUP_SEARCH);
      itembar.disableCommand(itembar.DUPLICATE);

      url = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");

      // Bug Id 70012, Start
      if (mgr.isModuleInstalled("PCMSCI"))
      {
         bPcmsciExist = true;
      }
      // Bug Id 70012, End

   }

   protected String getDescription()
   {
      return "PCMWQUICKREPORTININTROSMQCKREPSM: Quick Report in Work Wizard for SM";
   }

   protected String getTitle()
   {
      return "PCMWQUICKREPORTININTROSMQCKREPSM: Quick Report in Work Wizard for SM";
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
               appendToHTML("  <table border=\"0\" class=\"BlockLayoutTable\" cellpadding=\"0\" cellspacing=\"0\" width=708>\n");
               appendToHTML("    <tr>\n");
               appendToHTML("      <td width=\"150\"><table id=\"picTBL\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"150\">\n");
               appendToHTML("        <tr>\n");
               appendToHTML("          <td width=\"100%\"><img src=\"images/PcmWizardWorkSM.gif\" WIDTH=\"130\" HEIGHT=\"260\"> </td>\n");
               appendToHTML("        </tr>\n");
               appendToHTML("      </table>\n");
               appendToHTML("      </td>\n");
               appendToHTML("      <td width=\"500\"><table id=\"superTBL\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"500\">\n");
               appendToHTML("        <tr>\n");
               appendToHTML("          <td><table id=\"noteTBL\" border=\"0\">\n");
               appendToHTML("            <tr>\n");
               appendToHTML("              <td>");

               if (introFlag == 1)
               {
                  appendToHTML(fmt.drawWriteLabel("PCMWQUICKREPORTININTROSMHEAD1: Quick Report In of Work"));
                  appendToHTML("          <br>\n");
                  appendToHTML("<p>");
                  appendToHTML(fmt.drawReadValue(mgr.translate("PCMWQUICKREPORTININTROSMHEAD1ROW1: In this Wizard you make a Quick Report directly into the History. ")));
                  appendToHTML(" <br>\n");
                  appendToHTML(fmt.drawReadValue(mgr.translate("PCMWQUICKREPORTININTROSMHEAD1ROW2: Beneath you report your Signature and what Object that the work has been done on. ")));
                  appendToHTML("</p>\n");
                  appendToHTML("<p>");
                  appendToHTML(fmt.drawReadValue(mgr.translate("PCMWQUICKREPORTININTROSMHEAD1ROW3: In the following windows you report the data needed for your report. ")));
                  appendToHTML(" <br>\n");
                  appendToHTML(fmt.drawReadValue(mgr.translate("PCMWQUICKREPORTININTROSMHEAD1ROW4: You can choose to finish after the next window or continue to add more data or report time. ")));
                  appendToHTML("</p>              <br>\n");
               }

               if (introFlag == 2)
               {
                  appendToHTML(fmt.drawWriteLabel("PCMWQUICKREPORTININTROSMHEAD2: Required Information"));
                  appendToHTML("              <br>\n");
                  appendToHTML("<p>");                                                                                                         
                  appendToHTML(fmt.drawReadValue(mgr.translate("PCMWQUICKREPORTININTROSMHEAD2ROW1: Register the information for the work done. ")));
                  appendToHTML(fmt.drawReadValue(mgr.translate("PCMWQUICKREPORTININTROSMHEAD2ROW2: After this you can choose to continue with additional information and time report, or to finish your registration. ")));
                  appendToHTML("    <br>          \n");
                  appendToHTML("</p>      <br>\n");
               }

               if (introFlag == 3)
               {
                  appendToHTML(fmt.drawWriteLabel("PCMWQUICKREPORTININTROSMHEAD3: Performed Action and Classification"));
                  appendToHTML("              <br>\n");
                  appendToHTML("<p>");
                  appendToHTML(fmt.drawReadValue(mgr.translate("PCMWQUICKREPORTININTROSMHEAD3ROW1: Register the additional information for the work done. ")));
                  appendToHTML(fmt.drawReadValue(mgr.translate("PCMWQUICKREPORTININTROSMHEAD3ROW2: The classification information could be necessary for follow-up. ")));
                  appendToHTML("              <br>\n");
                  appendToHTML(fmt.drawReadValue(mgr.translate("PCMWQUICKREPORTININTROSMHEAD3ROW3: Choose 'Next' to report time or 'Finish' to end. ")));
                  appendToHTML(" </p>             <br>\n");
               }

               if (introFlag == 4)
               {
                  appendToHTML(fmt.drawWriteLabel("PCMWQUICKREPORTININTROSMHEAD4: Time Report"));
                  appendToHTML("              <br>\n");
                  appendToHTML("<p>");
                  appendToHTML(fmt.drawReadValue(mgr.translate("PCMWQUICKREPORTININTROSMHEAD4ROW1: Here you can report your time. When ready press Finish. ")));
                  appendToHTML("   </p>           <br>\n");
               }

               appendToHTML("              </td>\n");
               appendToHTML("            </tr>\n");
               appendToHTML("          </table>\n");
//  		Bug 69168, Start
               if (introFlag == 3)
               {
                  appendToHTML("<table width=\"100%\" >\n");
                  appendToHTML("       <tr>\n");
                  appendToHTML("           <td nowrap height=\"26\" align=\"left\" >&nbsp;");
                  appendToHTML(fmt.drawWriteLabel("PCMWQUICKREPORTININTROSMWRKDONE: Work Done:"));
                  appendToHTML("</td>\n");
                  appendToHTML("           <td nowrap height=\"26\" align=\"left\" width = '78%'>");
                  appendToHTML(fmt.drawTextField("WORK_DONE_DISP", workDone, "OnChange=setWorkDone()", 51, 60, true));
                  appendToHTML("</td>\n");
                  appendToHTML("       </tr>\n");
                  appendToHTML("       <tr>\n");
                  appendToHTML("           <td nowrap height=\"26\" align=\"left\" >");
                  appendToHTML("</td>\n");
                  appendToHTML("           <td nowrap height=\"26\" align=\"left\" width = '78%'>");
                  appendToHTML(fmt.drawTextArea("PERFORMED_ACTION_LO_DISP", workDoneLo, "OnChange=setWorkDoneLo()", 2, 50));
                  appendToHTML("</td>\n");
                  appendToHTML("       </tr>\n");
                  appendToHTML("</table>\n");
               }
//  		Bug 69168, End
               appendToHTML(headlay.generateDialog());    //XSS_Safe AMNILK 20070725

            }

            if (introFlag != 0)
            {
               itemlay.setLayoutMode(itemlay.EDIT_LAYOUT);
               appendToHTML(itemlay.generateDialog());    //XSS_Safe AMNILK 20070725
            }
            appendToHTML("          </td>\n");
            appendToHTML("        </tr>\n");
            appendToHTML("      </table>\n");
            appendToHTML("      </td>\n");
            appendToHTML("    </tr>\n");
            appendToHTML("  </table>\n");
            appendToHTML("    <table id=\"lineTBL\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"650\">\n");
            appendToHTML("  <tr>\n");

            appendToHTML(fmt.drawReadValue("..........................................................."));
            appendToHTML(fmt.drawReadValue("..........................................................."));
            appendToHTML(fmt.drawReadValue(".........................................................."));

            appendToHTML("  </tr>\n");
            appendToHTML("  </table> \n");
            appendToHTML("  <table id=\"buttonTBL\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=708>\n");

            if (introFlag == 4)
            {
               appendToHTML("            <tr width= '100%'>\n");
               appendToHTML("              <td align=\"right\">");
               appendToHTML(fmt.drawSubmit("ADDTREP",mgr.translate("PCMWQUICKREPORTININTROSMADDTRBUT: Add Another Time Report"),"ADDTREP"));
               appendToHTML("                  &nbsp;");
               appendToHTML(fmt.drawSubmit("DELETECURRREP",mgr.translate("PCMWQUICKREPORTININTROSMDELTRBUT: Delete Current Time Report"),"DELETECURRREP"));
               appendToHTML("              </td>\n");
               appendToHTML("            </tr>\n");
               appendToHTML("            <tr></tr>\n");
               appendToHTML("            <tr width= '100%'>\n");
               appendToHTML("              <td align=\"right\">");
               appendToHTML(fmt.drawSubmit("PREVTIMEREP",mgr.translate("PCMWQUICKREPORTININTROSMPRETRBUT: <Previous Time Report"),"PREVTIMEREP"));
               appendToHTML("                  &nbsp;");
               appendToHTML(fmt.drawSubmit("NEXTTIMEREP",mgr.translate("PCMWQUICKREPORTININTROSMNEXTTRBUT: Next Time Report>"),"NEXTTIMEREP"));
               appendToHTML("\n");
               appendToHTML("              </td>\n");
               appendToHTML("            </tr>\n");
               appendToHTML("            <tr></tr>\n");
            }

            appendToHTML("            <tr width= '100%'>\n");
            appendToHTML("              <td align=\"right\">\n");
	    //Bug 79242, Start
            if (introFlag == 3)
	    {
               appendToHTML(fmt.drawSubmit("ADDPREPOST",mgr.translate("PCMWQUICKREPORTININTROSMPREPOST: Add Work Order Prepostings"),"ADDPREPOST"));
	    }
	    //Bug 79242, End

            if (introFlag != 1)
            {
               appendToHTML(fmt.drawSubmit("PREVIOUS",mgr.translate("PCMWQUICKREPORTININTROSMPREVBUT: <Previous"),"PREVIOUS"));
               appendToHTML("&nbsp;");
            }

            if (introFlag == 1)
            {
               appendToHTML(fmt.drawSubmit("NEXT",mgr.translate("PCMWQUICKREPORTININTROSMNEXBUT:  Next> "),"onFocus=checkMandato(1)"));
               appendToHTML("&nbsp;");
            }

            if (introFlag == 2)
            {
               appendToHTML(fmt.drawSubmit("NEXT",mgr.translate("PCMWQUICKREPORTININTROSMNEXBUT:  Next> "),"onFocus=checkMandato(2)"));
               appendToHTML("&nbsp;");
            }

            if (introFlag == 3)
            {
               appendToHTML(fmt.drawSubmit("NEXT",mgr.translate("PCMWQUICKREPORTININTROSMNEXBUT:  Next> "),"onFocus=checkMandato(3)"));
               appendToHTML("&nbsp;");
            }

            appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("PCMWQUICKREPORTININTROSMCANBUT: Cancel"),"Cancel"));
            appendToHTML("&nbsp;\n");

            if (introFlag == 3)
            {
               appendToHTML(fmt.drawSubmit("FINISH",mgr.translate("PCMWQUICKREPORTININTROSMFINBUT: Finish"),"onFocus=checkMandato(3)"));
            }

            if (introFlag == 4)
            {
               appendToHTML(fmt.drawSubmit("FINISH",mgr.translate("PCMWQUICKREPORTININTROSMFINBUT: Finish"),"onFocus=checkMandato(4)"));
            }
         }

         appendToHTML("              </td>\n");
         appendToHTML("            </tr>\n");
         appendToHTML("   </table>\n");
         appendToHTML("   <p>\n");
	 //Bug 79242, Start
         if (introFlag == 3 && !(preEna))
	    appendDirtyJavaScript("document.form.ADDPREPOST.disabled = true;\n");
	 //Bug 79242, End
         printHiddenField("BUTTONVALPRE","");




//     		Bug 69168, Start
         if (introFlag == 3)
         {
            appendDirtyJavaScript("document.form.__FOCUS.value = 'WORK_DONE_DISP';\n") ;
         }
//     		Bug 69168, End
         appendDirtyJavaScript("\n\n");
         if (!mgr.isEmpty(sAlertMessage))
         {
                appendDirtyJavaScript("		alert(\"");
                appendDirtyJavaScript(sAlertMessage);
                appendDirtyJavaScript("\");\n\n");
         }

         appendDirtyJavaScript("if('");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(closeWin));   //XSS_Safe AMNILK 20070725
         appendDirtyJavaScript("' == \"TRUE\")\n");
         appendDirtyJavaScript(" window.close();\n");
//  		Bug 69168, Start
         appendDirtyJavaScript("function setWorkDone()\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("  document.form.WORK_DONE.value = document.form.WORK_DONE_DISP.value;\n");
         appendDirtyJavaScript("}\n");

         appendDirtyJavaScript("function setWorkDoneLo()\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("  document.form.PERFORMED_ACTION_LO.value = document.form.PERFORMED_ACTION_LO_DISP.value;\n");
         appendDirtyJavaScript("}\n");
//  		Bug 69168, End
         appendDirtyJavaScript("function toggleStyleDisplay(el)\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   el = document.getElementById(el);\n");
         appendDirtyJavaScript("   if(el.style.display!='none')\n");
         appendDirtyJavaScript("   {  \n");
         appendDirtyJavaScript("      el.style.display='none';\n");
         appendDirtyJavaScript("      picTBL.style.display='none';\n");
         appendDirtyJavaScript("      buttonTBL.style.display='none';\n");
         appendDirtyJavaScript("      noteTBL.style.display='none';\n");
         appendDirtyJavaScript("      lineTBL.style.display='none';\n");
         appendDirtyJavaScript("      superTBL.style.display='none';\n");
         appendDirtyJavaScript("   }  \n");
         appendDirtyJavaScript("   else\n");
         appendDirtyJavaScript("   {  \n");
         appendDirtyJavaScript("      el.style.display='block';\n");
         appendDirtyJavaScript("      picTBL.style.display='block';\n");
         appendDirtyJavaScript("      buttonTBL.style.display='block';\n");
         appendDirtyJavaScript("      noteTBL.style.display='block';      \n");
         appendDirtyJavaScript("      lineTBL.style.display='block';\n");
         appendDirtyJavaScript("      superTBL.style.display='block';\n");
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("}\n");

         appendDirtyJavaScript("function checkMandato(i)\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("  if (i == 1)	\n");
         appendDirtyJavaScript("  {\n");
         appendDirtyJavaScript("       xP = eval('f.CUSTOMER_NO');\n");
         appendDirtyJavaScript("       xQ = eval('f.MCH_CODE');   \n");
         appendDirtyJavaScript("       xR = eval('f.REPORTED_BY');   \n");
         appendDirtyJavaScript("       xS = eval('f.AUTHORIZE_CODE');   \n");
         appendDirtyJavaScript("       labelP = '");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(mgr.getASPField("CUSTOMER_NO").getJSLabel())); //XSS_Safe AMNILK 20070725
         appendDirtyJavaScript("';\n");
         appendDirtyJavaScript("       labelQ = '");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(mgr.getASPField("MCH_CODE").getJSLabel())); //XSS_Safe AMNILK 20070725
         appendDirtyJavaScript("';\n");
         appendDirtyJavaScript("       labelR = '");  
         appendDirtyJavaScript(mgr.encodeStringForJavascript(mgr.getASPField("REPORTED_BY").getJSLabel())); //XSS_Safe AMNILK 20070725
         appendDirtyJavaScript("';\n");
         appendDirtyJavaScript("       labelS = '");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(mgr.getASPField("AUTHORIZE_CODE").getJSLabel())); //XSS_Safe AMNILK 20070725
         appendDirtyJavaScript("';\n");
         appendDirtyJavaScript("       if( f.CUSTOMER_NO.value=='' ) \n");
         appendDirtyJavaScript("         return missingValueError_(xP,labelP,'');  \n");
         appendDirtyJavaScript("       if( f.MCH_CODE.value=='' ) \n");
         appendDirtyJavaScript("         return missingValueError_(xQ,labelQ,'');  \n");
         appendDirtyJavaScript("       if( f.REPORTED_BY.value=='' ) \n");
         appendDirtyJavaScript("         return missingValueError_(xR,labelR,'');  \n");
         appendDirtyJavaScript("       if( f.AUTHORIZE_CODE.value=='' ) \n");
         appendDirtyJavaScript("         return missingValueError_(xS,labelS,'');  \n");
         appendDirtyJavaScript("  }\n");
         appendDirtyJavaScript("  if (i == 2)	\n");
         appendDirtyJavaScript("  {\n");
         appendDirtyJavaScript("       xO = eval('f.ERR_DESCR');\n");
         appendDirtyJavaScript("       xP = eval('f.ORG_CODE');\n");
         appendDirtyJavaScript("       xQ = eval('f.REAL_F_DATE');   \n");
         appendDirtyJavaScript("       labelO = '");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(mgr.getASPField("ERR_DESCR").getJSLabel()));   //XSS_Safe AMNILK 20070725
         appendDirtyJavaScript("';\n");
         appendDirtyJavaScript("       labelP = '");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(mgr.getASPField("ORG_CODE").getJSLabel())); //XSS_Safe AMNILK 20070725
         appendDirtyJavaScript("';\n");
         appendDirtyJavaScript("       labelQ = '");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(mgr.getASPField("REAL_F_DATE").getJSLabel())); //XSS_Safe AMNILK 20070725
         appendDirtyJavaScript("';\n");
         appendDirtyJavaScript("       if( f.ERR_DESCR.value=='' ) \n");
         appendDirtyJavaScript("         return missingValueError_(xO,labelO,'');  \n");
         appendDirtyJavaScript("       if( f.ORG_CODE.value=='' ) \n");
         appendDirtyJavaScript("         return missingValueError_(xP,labelP,'');  \n");
         appendDirtyJavaScript("       if( f.REAL_F_DATE.value=='' ) \n");
         appendDirtyJavaScript("         return missingValueError_(xQ,labelQ,'');  \n");
         appendDirtyJavaScript("  }\n");
         appendDirtyJavaScript("  if (i == 3)	\n");
         appendDirtyJavaScript("  {\n");
         appendDirtyJavaScript("       xP = eval('f.WORK_DONE');\n");
         appendDirtyJavaScript("       labelP = '");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(mgr.getASPField("WORK_DONE").getJSLabel()));   //XSS_Safe AMNILK 20070725
         appendDirtyJavaScript("';\n");
         appendDirtyJavaScript("       if( f.WORK_DONE.value=='' ) \n");
         appendDirtyJavaScript("         return missingValueError_(xP,labelP,'');  \n");
         appendDirtyJavaScript("  }\n");
         appendDirtyJavaScript("  if (i == 4)	\n");
         appendDirtyJavaScript("  {\n");
         appendDirtyJavaScript("       xP = eval('f.ITEM_CONTRACT');\n");
         appendDirtyJavaScript("       xQ = eval('f.ITEM_ORG_CODE');   \n");
         appendDirtyJavaScript("       xR = eval('f.CATALOG_NO');   \n");
         appendDirtyJavaScript("       xS = eval('f.CRE_DATE');   \n");
         appendDirtyJavaScript("       xT = eval('f.QTY');   \n");
         appendDirtyJavaScript("       labelP = '");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(mgr.getASPField("ITEM_CONTRACT").getJSLabel()));  //XSS_Safe AMNILK 20070725
         appendDirtyJavaScript("';\n");
         appendDirtyJavaScript("       labelQ = '");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(mgr.getASPField("ITEM_ORG_CODE").getJSLabel()));  //XSS_Safe AMNILK 20070725
         appendDirtyJavaScript("';\n");
         appendDirtyJavaScript("       labelR = '");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(mgr.getASPField("CATALOG_NO").getJSLabel()));     //XSS_Safe AMNILK 20070725
         appendDirtyJavaScript("';\n");
         appendDirtyJavaScript("       labelS = '");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(mgr.getASPField("CRE_DATE").getJSLabel()));    //XSS_Safe AMNILK 20070725
         appendDirtyJavaScript("';\n");
         appendDirtyJavaScript("       labelT = '");
         appendDirtyJavaScript(mgr.encodeStringForJavascript(mgr.getASPField("QTY").getJSLabel()));         //XSS_Safe AMNILK 20070725
         appendDirtyJavaScript("';\n");
         appendDirtyJavaScript("       if( f.ITEM_CONTRACT.value=='' ) \n");
         appendDirtyJavaScript("         return missingValueError_(xP,labelP,'');  \n");
         appendDirtyJavaScript("       if( f.ITEM_ORG_CODE.value=='' ) \n");
         appendDirtyJavaScript("         return missingValueError_(xQ,labelQ,'');  \n");
         appendDirtyJavaScript("       if( f.CATALOG_NO.value=='' ) \n");
         appendDirtyJavaScript("         return missingValueError_(xR,labelR,'');  \n");
         appendDirtyJavaScript("       if( f.CRE_DATE.value=='' ) \n");
         appendDirtyJavaScript("         return missingValueError_(xS,labelS,'');  \n");
         appendDirtyJavaScript("       if( f.QTY.value=='' ) \n");
         appendDirtyJavaScript("         return missingValueError_(xT,labelT,'');  \n");
         appendDirtyJavaScript("  }\n");
         appendDirtyJavaScript("}\n");

         appendDirtyJavaScript("function validateRoleCode(i)\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("	if( getRowStatus_('ITEM',i)=='QueryMode__' ) return;\n");
         appendDirtyJavaScript("	setDirty();\n");
         appendDirtyJavaScript("	if( !checkRoleCode(i) ) return;\n");
         appendDirtyJavaScript("   window.status='Please wait for validation';\n");
         appendDirtyJavaScript("    r = __connect(\n");
         appendDirtyJavaScript("		'");
         appendDirtyJavaScript(mgr.getURL());
         appendDirtyJavaScript("?VALIDATE=ROLE_CODE'\n");
         appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
         appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
         appendDirtyJavaScript("		+ '&AMOUNT=' + URLClientEncode(getValue_('AMOUNT',i))\n");
         appendDirtyJavaScript("		+ '&ITEM_ORG_CODE=' + URLClientEncode(getValue_('ITEM_ORG_CODE',i))\n");
         appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
         appendDirtyJavaScript("		);\n");
         appendDirtyJavaScript("   window.status='';\n");
         appendDirtyJavaScript("	if( checkStatus_(r,'ROLE_CODE',i,'Craft') )\n");
         appendDirtyJavaScript("	{\n");
         appendDirtyJavaScript("		assignValue_('AMOUNT',i,0);\n");
         appendDirtyJavaScript("		assignValue_('CATALOG_NO',i,1);\n");
         appendDirtyJavaScript("	}\n");
         appendDirtyJavaScript("}\n");
      }
      else
      {
         appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
         appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
         appendDirtyJavaScript("window.location = '");

         if (toDefaultPage)
         {
            appendDirtyJavaScript(mgr.encodeStringForJavascript(calling_url));     //XSS_Safe AMNILK 20070725
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
        appendDirtyJavaScript(mgr.encodeStringForJavascript(closeWin));   
        appendDirtyJavaScript("' == \"TRUE\")\n");
        appendDirtyJavaScript(" window.close();\n");

         appendDirtyJavaScript("    if (");
         appendDirtyJavaScript(    mgr.isExplorer());
         appendDirtyJavaScript("    )\n");
         appendDirtyJavaScript("    { \n");
         appendDirtyJavaScript("      window.open('");
         appendDirtyJavaScript(url);
         appendDirtyJavaScript("'+\"pcmw/QuickReportInIntroSM.page?RANDOM_VAL=\"+Math.random(),\"frmQuickReportInIntroSM\",\"resizable=yes, status=yes,menubar=no,height=500,width=950\");      \n");
         appendDirtyJavaScript("    } \n");
         appendDirtyJavaScript("    else \n");
         appendDirtyJavaScript("    { \n");
         appendDirtyJavaScript("      window.open('");
         appendDirtyJavaScript(url);
         appendDirtyJavaScript("'+\"pcmw/QuickReportInIntroSM.page?RANDOM_VAL=\"+Math.random(),\"frmQuickReportInIntroSM\",\"resizable=yes, status=yes,menubar=no,height=535 ,width=950\");      \n");
         appendDirtyJavaScript("    } \n");
      }

      appendDirtyJavaScript("function lovEmpNo(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('EMP_NO',i).indexOf('%') !=-1)? getValue_('EMP_NO',i):'';\n");
      appendDirtyJavaScript("	if( getValue_('ROLE_CODE',i)=='' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("              openLOVWindow('EMP_NO',i,\n");
      appendDirtyJavaScript("                  '../mscomw/MaintEmployeeLov.page?__FIELD=Employee&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('EMP_NO',i))\n"); 
      appendDirtyJavaScript("                  + '&PERSON_ID=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM_ORG_CODE',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateEmpNo');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("       {\n");        
      appendDirtyJavaScript("              openLOVWindow('EMP_NO',i,\n");
      appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Employee&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('EMP_NO',i))\n"); 
      appendDirtyJavaScript("                  + '&SIGNATURE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM_ORG_CODE',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");       
      appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateEmpNo');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovItemOrgCode(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ITEM_ORG_CODE',i).indexOf('%') !=-1)? getValue_('ITEM_ORG_CODE',i):'';\n");
      appendDirtyJavaScript("	if( getValue_('ROLE_CODE',i)=='' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("	            if( getValue_('EMP_NO',i)=='' )\n");
      appendDirtyJavaScript("             {\n");
      appendDirtyJavaScript("                  openLOVWindow('ITEM_ORG_CODE',i,\n");
      appendDirtyJavaScript("                     '../mscomw/OrgCodeAllowedSiteLov.page?__FIELD=Maintenance+Organization&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM_ORG_CODE',i))\n"); 
      appendDirtyJavaScript("                      + '&ORG_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM_COMPANY',i))\n");
      appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	             ,550,500,'validateItemOrgCode');\n");
      appendDirtyJavaScript("             }\n");
      appendDirtyJavaScript("	            else\n");
      appendDirtyJavaScript("             {\n");
      appendDirtyJavaScript("                 openLOVWindow('ITEM_ORG_CODE',i,\n");
      appendDirtyJavaScript("                     '../mscomw/MaintEmployeeLov.page?&__FIELD=Maintenance+Organization&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                     + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM_ORG_CODE',i))\n"); 
      appendDirtyJavaScript("                     + '&ORG_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                     + '&EMPLOYEE_ID=' + URLClientEncode(getValue_('EMP_NO',i))\n");
      appendDirtyJavaScript("                     + '&COMPANY=' + URLClientEncode(getValue_('ITEM_COMPANY',i))\n");
      appendDirtyJavaScript("                     + '&CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	            ,550,500,'validateItemOrgCode');\n");
      appendDirtyJavaScript("             }\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("       {\n");        
      appendDirtyJavaScript("              openLOVWindow('ITEM_ORG_CODE',i,\n");
      appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Maintenance+Organization&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM_ORG_CODE',i))\n"); 
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");       
      appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateItemOrgCode');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovRoleCode(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && ITEM_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('ROLE_CODE',i).indexOf('%') !=-1)? getValue_('ROLE_CODE',i):'';\n");
      appendDirtyJavaScript("	if( getValue_('EMP_NO',i)=='' )\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("	            if( getValue_('ITEM_ORG_CODE',i)=='' )\n");
      appendDirtyJavaScript("             {\n");
      appendDirtyJavaScript("                  openLOVWindow('ROLE_CODE',i,\n");
      appendDirtyJavaScript("                     '../mscomw/RoleToSiteLov.page?__FIELD=Craft&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                      + '&__KEY_VALUE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n"); 
      appendDirtyJavaScript("                      + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                      + '&COMPANY=' + URLClientEncode(getValue_('ITEM_COMPANY',i))\n");
      appendDirtyJavaScript("                      + '&CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	             ,550,500,'validateRoleCode');\n");
      appendDirtyJavaScript("             }\n");
      appendDirtyJavaScript("	            else\n");
      appendDirtyJavaScript("             {\n");
      appendDirtyJavaScript("                 openLOVWindow('ROLE_CODE',i,\n");
      appendDirtyJavaScript("                     '../mscomw/EmployeeRoleLov.page?&__FIELD=Craft&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                     + '&__KEY_VALUE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n"); 
      appendDirtyJavaScript("                      + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                     + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM_ORG_CODE',i))\n");
      appendDirtyJavaScript("                     + '&COMPANY=' + URLClientEncode(getValue_('ITEM_COMPANY',i))\n");
      appendDirtyJavaScript("                     + '&CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	            ,550,500,'validateRoleCode');\n");
      appendDirtyJavaScript("             }\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript(" else\n");
      appendDirtyJavaScript("       {\n");        
      appendDirtyJavaScript("              openLOVWindow('ROLE_CODE',i,\n");
      appendDirtyJavaScript("                  '../mscomw/EmployeeRoleLov.page?__FIELD=Craft&__INIT=1'+param+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("                  + '&__KEY_VALUE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n"); 
      appendDirtyJavaScript("                  + '&ROLE_CODE=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("                  + '&ORG_CODE=' + URLClientEncode(getValue_('ITEM_ORG_CODE',i))\n");
      appendDirtyJavaScript("                  + '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
      appendDirtyJavaScript("                  + '&COMPANY=' + URLClientEncode(getValue_('ITEM_COMPANY',i))\n");
      appendDirtyJavaScript("                  + '&ORG_CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");       
      appendDirtyJavaScript("       	   ,550,500,'validateRoleCode');\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateRoleCode(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("	if( getRowStatus_('ITEM',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("	setDirty();\n");
      appendDirtyJavaScript("	if( !checkRoleCode(i) ) return;\n");
      appendDirtyJavaScript("	if( getValue_('ROLE_CODE',i)=='' )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		getField_('AMOUNT',i).value = '';\n");
      appendDirtyJavaScript("		getField_('CATALOG_NO',i).value = '';\n");
      appendDirtyJavaScript("		getField_('ROLE_CODE',i).value = '';\n");
      appendDirtyJavaScript("		validateItemOrgCode(i);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("	window.status='Please wait for validation';\n");
      appendDirtyJavaScript("	r = __connect(\n");
      appendDirtyJavaScript("		'");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ROLE_CODE'\n");
      appendDirtyJavaScript("		+ '&ROLE_CODE=' + URLClientEncode(getValue_('ROLE_CODE',i))\n");
      appendDirtyJavaScript("		+ '&QTY=' + URLClientEncode(getValue_('QTY',i))\n");
      appendDirtyJavaScript("		+ '&AMOUNT=' + URLClientEncode(getValue_('AMOUNT',i))\n");
      appendDirtyJavaScript("		+ '&ITEM_ORG_CODE=' + URLClientEncode(getValue_('ITEM_ORG_CODE',i))\n");
      appendDirtyJavaScript("		+ '&CATALOG_NO=' + URLClientEncode(getValue_('CATALOG_NO',i))\n");
      appendDirtyJavaScript("		+ '&ITEM_CONTRACT=' + URLClientEncode(getValue_('ITEM_CONTRACT',i))\n");
      appendDirtyJavaScript("		+ '&EMP_NO=' + URLClientEncode(getValue_('EMP_NO',i))\n");
      appendDirtyJavaScript("		+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n");
      appendDirtyJavaScript("		);\n");
      appendDirtyJavaScript("	window.status='';\n");
      appendDirtyJavaScript("	if( checkStatus_(r,'ROLE_CODE',i,'Craft') )\n");
      appendDirtyJavaScript("	{\n");
      appendDirtyJavaScript("		assignValue_('AMOUNT',i,0);\n");
      appendDirtyJavaScript("		assignValue_('CATALOG_NO',i,1);\n");
      appendDirtyJavaScript("		assignValue_('ROLE_CODE',i,2);\n");
      appendDirtyJavaScript("		assignValue_('ITEM_CONTRACT',i,3);\n");
      appendDirtyJavaScript("	}\n");
      appendDirtyJavaScript("}\n");


      //Bug Id 70012, Start
      appendDirtyJavaScript("function lovLineNo(i,params)\n{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('CONTRACT_ID',i).indexOf('%') !=-1)? getValue_('CONTRACT_ID',i):'';\n");
      appendDirtyJavaScript(" 	if(getValue_('CONNECTION_TYPE_DB',i) != 'VIM')\n");
      appendDirtyJavaScript(" 	{\n");
      appendDirtyJavaScript("		whereCond1 = '';\n"); 
      appendDirtyJavaScript("	      	whereCond1 = \" OBJSTATE = 'Active' \";\n"); 
      appendDirtyJavaScript(" 	if (document.form.CONTRACT.value != '') \n");
      appendDirtyJavaScript("	      		whereCond1 += \" AND CONTRACT = '\" + URLClientEncode(document.form.CONTRACT.value) + \"' \";\n"); 
      appendDirtyJavaScript(" 	if (document.form.CUSTOMER_NO.value != '') \n");
      //(-/+) Bug 70920, Modified where condition to consider category objects (Start).
      appendDirtyJavaScript("	      		whereCond1 += \" AND (CUSTOMER_NO = '\" + URLClientEncode(document.form.CUSTOMER_NO.value)+\"' OR Sc_Contract_Customer_API.Check_Exist(CONTRACT_ID, ( '\" + URLClientEncode(document.form.CUSTOMER_NO.value)+ \"')) = 'TRUE' ) \" ;\n"); 
      //(-/+) Bug 70920, Modified where condition to consider category objects (Finish).
      appendDirtyJavaScript(" 	if (document.form.WORK_TYPE_ID.value != '') \n");
      appendDirtyJavaScript("	      		whereCond1 += \" AND WORK_TYPE_ID = '\" + URLClientEncode(document.form.WORK_TYPE_ID.value) + \"' \";\n"); 
      appendDirtyJavaScript(" 	if (document.form.CONTRACT_ID.value != '') \n");
      appendDirtyJavaScript("	      		whereCond1 += \" AND CONTRACT_ID = '\" + URLClientEncode(document.form.CONTRACT_ID.value) + \"' \";\n"); 
      //(-/+) Bug 70920, Modified where condition to consider category objects (Start).
      appendDirtyJavaScript(" 	if (document.form.MCH_CODE.value != '' && document.form.CONTRACT.value != '') \n");
      appendDirtyJavaScript("	      		whereCond1 += \" AND (((CONTRACT_ID,LINE_NO) IN (SELECT contract_id, line_no FROM PSC_SRV_LINE_OBJECTS WHERE mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' AND mch_contract = '\" + URLClientEncode(document.form.CONTRACT.value) + \"')) OR (mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' AND mch_contract = '\" + URLClientEncode(document.form.CONTRACT.value) + \"') OR CONNECTION_TYPE_DB = 'CATEGORY')\";\n");
      //(-/+) Bug 70920, Modified where condition to consider category objects (Finish).
      if (bPcmsciExist)
      {  // Filtering by From Date
         appendDirtyJavaScript("	      		whereCond1 += \" AND Psc_Contr_Product_Api.Get_Date_From(CONTRACT_ID,LINE_NO) <= sysdate \";\n"); 
      }
      appendDirtyJavaScript("		openLOVWindow('CONTRACT_ID',i,\n");
      appendDirtyJavaScript("		'PscContrProductLov.page' + '?__VIEW=DUMMY&__INIT=1&__LOV=Y&__WHERE=' +whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("							,550,500,'validateContractId');\n");
      appendDirtyJavaScript(" 	}\n");
      appendDirtyJavaScript("	else\n");
      appendDirtyJavaScript("  window.open('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript("ServiceContractLineSearchDlg.page?"));
      appendDirtyJavaScript("' + '&CUSTOMER_NO='+ URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("+ '&WO_NO=' + URLClientEncode(getValue_('WO_NO',i))\n");
      appendDirtyJavaScript("+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n"); 
      appendDirtyJavaScript("+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
      appendDirtyJavaScript("+ '&WORK_TYPE_ID=' + URLClientEncode(getValue_('WORK_TYPE_ID',i))\n");
      appendDirtyJavaScript(",'serviceContract','scrollbars,resizable,status=yes,width=770,height=500');\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function lovContractId(i,params)\n{\n");
      appendDirtyJavaScript("	if(params) param = params;\n");
      appendDirtyJavaScript("	else param = '';\n");
      appendDirtyJavaScript("	var enable_multichoice =(true && HEAD_IN_FIND_MODE);\n");
      appendDirtyJavaScript("	var key_value = (getValue_('CONTRACT_ID',i).indexOf('%') !=-1)? getValue_('CONTRACT_ID',i):'';\n");
      appendDirtyJavaScript(" 	if(getValue_('CONNECTION_TYPE_DB',i) != 'VIM')\n");
      appendDirtyJavaScript(" 	{\n");
      appendDirtyJavaScript("		whereCond1 = '';\n"); 
      appendDirtyJavaScript("	      	whereCond1 = \" OBJSTATE = 'Active' \";\n"); 
      appendDirtyJavaScript(" 	if (document.form.CONTRACT.value != '') \n");
      appendDirtyJavaScript("	      		whereCond1 += \" AND CONTRACT = '\" + URLClientEncode(document.form.CONTRACT.value) + \"' \";\n"); 
      appendDirtyJavaScript(" 	if (document.form.CUSTOMER_NO.value != '') \n");
      //(-/+) Bug 70920, Modified where condition to consider category objects (Start).
      appendDirtyJavaScript("	      		whereCond1 += \" AND (CUSTOMER_NO = '\" + URLClientEncode(document.form.CUSTOMER_NO.value)+\"' OR Sc_Contract_Customer_API.Check_Exist(CONTRACT_ID, ( '\" + URLClientEncode(document.form.CUSTOMER_NO.value)+ \"')) = 'TRUE' ) \" ;\n"); 
      //(-/+) Bug 70920, Modified where condition to consider category objects (Finish).
      appendDirtyJavaScript(" 	if (document.form.WORK_TYPE_ID.value != '') \n");
      appendDirtyJavaScript("	      		whereCond1 += \" AND WORK_TYPE_ID = '\" + URLClientEncode(document.form.WORK_TYPE_ID.value) + \"' \";\n"); 
      //(-/+) Bug 70920, Modified where condition to consider category objects (Start).
      appendDirtyJavaScript(" 	if (document.form.MCH_CODE.value != '' && document.form.CONTRACT.value != '') \n");
      appendDirtyJavaScript("	      		whereCond1 += \" AND (((CONTRACT_ID,LINE_NO) IN (SELECT contract_id, line_no FROM PSC_SRV_LINE_OBJECTS WHERE mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' AND mch_contract = '\" + URLClientEncode(document.form.CONTRACT.value) + \"')) OR (mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' AND mch_contract = '\" + URLClientEncode(document.form.CONTRACT.value) + \"') OR CONNECTION_TYPE_DB = 'CATEGORY')\";\n");
      //(-/+) Bug 70920, Modified where condition to consider category objects (Finish).
      if (bPcmsciExist)
      {  // Filtering by From Date
         appendDirtyJavaScript("	      		whereCond1 += \" AND Psc_Contr_Product_Api.Get_Date_From(CONTRACT_ID,LINE_NO) <= sysdate \";\n"); 
      }
      appendDirtyJavaScript("		openLOVWindow('CONTRACT_ID',i,\n");
      appendDirtyJavaScript("		'PscContrProductLov.page' + '?__VIEW=DUMMY&__INIT=1&__LOV=Y&__WHERE=' +whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("							,550,500,'validateContractId');\n");
      appendDirtyJavaScript(" 	}\n");
      appendDirtyJavaScript("	else\n");
      appendDirtyJavaScript("  window.open('");
      appendDirtyJavaScript(mgr.encodeStringForJavascript("ServiceContractLineSearchDlg.page?"));
      appendDirtyJavaScript("' + '&CUSTOMER_NO='+ URLClientEncode(getValue_('CUSTOMER_NO',i))\n");
      appendDirtyJavaScript("+ '&WO_NO=' + URLClientEncode(getValue_('WO_NO',i))\n");
      appendDirtyJavaScript("+ '&CONTRACT=' + URLClientEncode(getValue_('CONTRACT',i))\n"); 
      appendDirtyJavaScript("+ '&MCH_CODE=' + URLClientEncode(getValue_('MCH_CODE',i))\n");
      appendDirtyJavaScript("+ '&WORK_TYPE_ID=' + URLClientEncode(getValue_('WORK_TYPE_ID',i))\n");
      appendDirtyJavaScript(",'serviceContract','scrollbars,resizable,status=yes,width=770,height=500');\n");
      appendDirtyJavaScript("}\n");
      //Bug Id 70012, End
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
