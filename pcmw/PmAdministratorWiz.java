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
*  File        : PmAdministratorWiz.java 
*
*  Created    : 2008-09-24  ASSALK  for bug 77272, under DEFCON.
*  Modified   :
*  081028    CHCRLK    Corrected some minor issues.   
*  081104    CHCRLK    Corrected more issues.
*  090512    SHAFLK    Bug 82577, Removed Sales Part No, Sales Part Site or Sales Price.   
*  090512    SHAFLK    Bug 82650, Modified finishForm(). 
*  091103    SHAFLK    Bug 78801, Modified finishForm(). 
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;


public class PmAdministratorWiz extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.PmAdministratorWiz");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPHTMLFormatter fmt;
   private ASPContext ctx;
   private ASPForm frm;
   private ASPField f;

   private ASPBlock nsblk;
   private ASPRowSet nsset;
   private ASPCommandBar nsbar;
   private ASPTable nstbl;
   private ASPBlockLayout nslay;

   private ASPBlock csblk;
   private ASPRowSet csset;
   private ASPCommandBar csbar;
   private ASPTable cstbl;
   private ASPBlockLayout cslay;

   private ASPBlock pmblk;
   private ASPRowSet pmset;
   private ASPCommandBar pmbar;
   private ASPTable pmtbl;
   private ASPBlockLayout pmlay;

   private ASPTransactionBuffer trans;
   private ASPTransactionBuffer trans1;
   private ASPBuffer data;
   private ASPCommand cmd;
   private ASPQuery q;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================

   private String val;
   private int introFlag;
   private String mchCode1;
   private String contract1;
   private String maintEmpSign1;
   private String signature1;
   private String opStatusId1;
   private String priorityId1;
   private String orgCode1;
   private String orgContract1;
   private String rounddefId1;
   private String workTypeId1;
   private String generatable1;
   private String actionCodeId1;
   private String catalogNo1;
   private String catalogContract1;
   private String salesPrice1;
   private String customerNo1;
   private String contractId1;
   private String lineNo1;
   private String currencyCode1;
   private String pmState1;
   private String separateAction1;
   private String objStructure1;
   private String startValue1;
   private String pmStartUnit1;
   private String pmStartUnitDb1;
   private String interval1;
   private String pmIntervalUnit1;
   private String callCode1;
   private String startCall1;
   private String callInterval1;
   private String maintEmployee1;
   private String company1;
   private String startDate1;

   private String orgContract2;
   private String orgCode2;
   private String rounddefId2;
   private String maintEmpSign2;
   private String signature2;
   private String opStatusId2;
   private String workTypeId2;
   private String priorityId2;
   private String salesPrice2;
   private String actionCodeId2;
   private String catalogContract2;
   private String catalogNo2;
   private String customerNo2;
   private String contractId2;
   private String lineNo2;
   private String generatable2;
   private String validFrom2;
   private String validTo2;
   private String updateRev2;
   private String revInterval2;
   private String startValue2;
   private String pmStartUnit2;
   private String pmStartUnitDb2;
   private String interval2;
   private String pmIntervalUnit2;
   private String callCode2;
   private String startCall2;
   private String callInterval2;
   private String sVal1 = "";
   private String sVal2 = "";
   private String sVal3 = "";
   private String sVal4 = "";
   private String dateMask = "";
   private ASPBuffer temp1;
   private String temp = "";
   private int length;
   private boolean bFirstRequest;

   //===============================================================
   // Construction 
   //===============================================================
   public PmAdministratorWiz(ASPManager mgr, String page_path)
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
      trans1 = mgr.newASPTransactionBuffer();

      introFlag = toInt(ctx.readValue("INTROFLAG","1"));
      readFromContext();
            
      if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (mgr.buttonPressed("PREVIOUS"))
         previousForm();
      else if ("NEXTFLAG".equals(mgr.readValue("NEXTFLAG")))
         nextForm();
      else if (mgr.buttonPressed("NEXT"))
         nextForm();      
      else if (mgr.buttonPressed("CANCEL"))
         cancelForm();
      else if (mgr.buttonPressed("FINISH"))
         finishForm();
      else if ("OK".equals(mgr.readValue("FIRST_REQUEST")) ||!mgr.isExplorer())
         startUp();
      else
         bFirstRequest = true;         
      
      adjust();

      ctx.writeValue("INTROFLAG",String.valueOf(introFlag));
      writeToContext();
   }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------

   public String getNextToken(int cookie)
   { 
      String sVal = "";
      if (cookie==1) sVal = sVal1;
      if (cookie==2) sVal = sVal2;
      if (cookie==3) sVal = sVal3;
      if (cookie==4) sVal = sVal4;

      ASPManager mgr = getASPManager();
      if (mgr.isEmpty(sVal))
         return null;

      int i=0;
      i = sVal.indexOf("^");
      String outString = sVal.substring(0,i);
      sVal = sVal.substring(i+1);

      if (cookie==1) sVal1 = sVal;
      if (cookie==2) sVal2 = sVal;
      if (cookie==3) sVal3 = sVal;
      if (cookie==4) sVal4 = sVal;

      return outString;
   }

   public void readFromContext()
   {
      ASPManager mgr = getASPManager();

      sVal1 = "";
      sVal2 = "";
      sVal3 = "";
      sVal4 = "";

      if (!mgr.isEmpty(ctx.readValue("S_VALUE_1"))) sVal1 = ctx.readValue("S_VALUE_1");
      if (!mgr.isEmpty(ctx.readValue("S_VALUE_2"))) sVal2 = ctx.readValue("S_VALUE_2");
      if (!mgr.isEmpty(ctx.readValue("S_VALUE_3"))) sVal3 = ctx.readValue("S_VALUE_3");
      if (!mgr.isEmpty(ctx.readValue("S_VALUE_4"))) sVal4 = ctx.readValue("S_VALUE_4");

      startDate1 = getNextToken(1);
      maintEmployee1 = getNextToken(1);
      company1 = getNextToken(1);
      mchCode1 = getNextToken(1);
      contract1 = getNextToken(1);
      maintEmpSign1 = getNextToken(1);
      signature1 = getNextToken(1);
      opStatusId1 = getNextToken(1);
      priorityId1 = getNextToken(1);
      orgCode1 = getNextToken(1);
      orgContract1 = getNextToken(1);
      rounddefId1 = getNextToken(1);
      workTypeId1 = getNextToken(1);
      generatable1 = getNextToken(1);
      actionCodeId1 = getNextToken(1);
      customerNo1 = getNextToken(2);
      contractId1 = getNextToken(2);
      lineNo1 = getNextToken(2);
      currencyCode1 = getNextToken(2);
      pmState1 = getNextToken(2);
      separateAction1 = getNextToken(2);
      objStructure1 = getNextToken(2);
      startValue1 = getNextToken(2);
      pmStartUnit1 = getNextToken(2);
      pmStartUnitDb1 = getNextToken(2);
      interval1 = getNextToken(2);
      pmIntervalUnit1 = getNextToken(2);
      callCode1 = getNextToken(2);
      startCall1 = getNextToken(2);
      callInterval1 = getNextToken(2);
      orgContract2 = getNextToken(3);
      orgCode2 = getNextToken(3);
      rounddefId2 = getNextToken(3);
      maintEmpSign2 = getNextToken(3);
      signature2 = getNextToken(3);
      opStatusId2 = getNextToken(3);
      workTypeId2 = getNextToken(3);
      priorityId2 = getNextToken(3);
      actionCodeId2 = getNextToken(3);
      customerNo2 = getNextToken(3);
      contractId2 = getNextToken(3);
      lineNo2 = getNextToken(3);
      generatable2 = getNextToken(3);
      validFrom2 = getNextToken(3);
      validTo2 = getNextToken(4);
      updateRev2 = getNextToken(4);
      revInterval2 = getNextToken(4);
      startValue2 = getNextToken(4);
      pmStartUnit2 = getNextToken(4);
      pmStartUnitDb2 = getNextToken(4);
      interval2 = getNextToken(4);
      pmIntervalUnit2 = getNextToken(4);
      callCode2 = getNextToken(4);
      startCall2 = getNextToken(4);
      callInterval2 = getNextToken(4);      
   }

   public String setSval(String sVal,String s)
   {
      ASPManager mgr = getASPManager();

      if (mgr.isEmpty(s))
         return sVal+"^";
      else
         return sVal+s+"^";
   }

   public void writeToContext()
   {
      ASPManager mgr = getASPManager();

      sVal1 = "";
      sVal2 = "";
      sVal3 = "";
      sVal4 = "";

      sVal1 = setSval(sVal1,startDate1);
      sVal1 = setSval(sVal1,maintEmployee1);
      sVal1 = setSval(sVal1,company1);
      sVal1 = setSval(sVal1,mchCode1);
      sVal1 = setSval(sVal1,contract1);
      sVal1 = setSval(sVal1,maintEmpSign1);
      sVal1 = setSval(sVal1,signature1);
      sVal1 = setSval(sVal1,opStatusId1);
      sVal1 = setSval(sVal1,priorityId1);
      sVal1 = setSval(sVal1,orgCode1);
      sVal1 = setSval(sVal1,orgContract1);
      sVal1 = setSval(sVal1,rounddefId1);
      sVal1 = setSval(sVal1,workTypeId1);
      sVal1 = setSval(sVal1,generatable1);
      sVal1 = setSval(sVal1,actionCodeId1);
      sVal1 = setSval(sVal1,catalogNo1);
      sVal1 = setSval(sVal1,catalogContract1);
      ctx.writeValue("S_VALUE_1",sVal1);

      sVal2 = setSval(sVal2,customerNo1);
      sVal2 = setSval(sVal2,contractId1);
      sVal2 = setSval(sVal2,lineNo1);
      sVal2 = setSval(sVal2,currencyCode1);
      sVal2 = setSval(sVal2,pmState1);
      sVal2 = setSval(sVal2,separateAction1);
      sVal2 = setSval(sVal2,objStructure1);
      sVal2 = setSval(sVal2,startValue1);
      sVal2 = setSval(sVal2,pmStartUnit1);
      sVal2 = setSval(sVal2,pmStartUnitDb1);
      sVal2 = setSval(sVal2,interval1);
      sVal2 = setSval(sVal2,pmIntervalUnit1);
      sVal2 = setSval(sVal2,callCode1);
      sVal2 = setSval(sVal2,startCall1);
      sVal2 = setSval(sVal2,callInterval1);
      ctx.writeValue("S_VALUE_2",sVal2);

      sVal3 = setSval(sVal3,orgContract2);
      sVal3 = setSval(sVal3,orgCode2);
      sVal3 = setSval(sVal3,rounddefId2);
      sVal3 = setSval(sVal3,maintEmpSign2);
      sVal3 = setSval(sVal3,signature2);
      sVal3 = setSval(sVal3,opStatusId2);
      sVal3 = setSval(sVal3,workTypeId2);
      sVal3 = setSval(sVal3,priorityId2);
      sVal3 = setSval(sVal3,actionCodeId2);
      sVal3 = setSval(sVal3,catalogContract2);
      sVal3 = setSval(sVal3,catalogNo2);
      sVal3 = setSval(sVal3,customerNo2);
      sVal3 = setSval(sVal3,contractId2);
      sVal3 = setSval(sVal3,lineNo2);
      sVal3 = setSval(sVal3,generatable2);
      sVal3 = setSval(sVal3,validFrom2);
      ctx.writeValue("S_VALUE_3",sVal3);

      sVal4 = setSval(sVal4,validTo2);
      sVal4 = setSval(sVal4,updateRev2);
      sVal4 = setSval(sVal4,revInterval2);
      sVal4 = setSval(sVal4,startValue2);
      sVal4 = setSval(sVal4,pmStartUnit2);
      sVal4 = setSval(sVal4,pmStartUnitDb2);
      sVal4 = setSval(sVal4,interval2);
      sVal4 = setSval(sVal4,pmIntervalUnit2);
      sVal4 = setSval(sVal4,callCode2);
      sVal4 = setSval(sVal4,startCall2);
      sVal4 = setSval(sVal4,callInterval2);
      ctx.writeValue("S_VALUE_4",sVal4);      
   }

   public void startUp()
   {
      ASPManager mgr = getASPManager();
      trans.clear();

      csset.clear();
      data = mgr.newASPBuffer();

      cmd = trans.addCustomFunction("FNDUSER","Fnd_Session_API.Get_Fnd_User","FND_USER");
      cmd = trans.addCustomFunction("GETPERS", "Person_Info_API.Get_Id_For_User", "SIGNATURE");
      cmd.addReference("FND_USER","FNDUSER/DATA");

      cmd = trans.addCustomFunction("GETLCHG", "Maintenance_Site_Utility_API.Get_Site_Date","VALID_FROM");
      cmd.addParameter("DUMMY_VAL","");
      cmd = trans.addCustomFunction("GETVAL","User_Default_API.Get_Contract","CONTRACT");
      cmd = trans.addCustomFunction( "GETCOM", "Site_API.Get_Company", "COMPANY");
      cmd.addReference("CONTRACT","GETVAL/DATA");

      cmd = trans.addCustomFunction("GETCLIVAL","Pm_Generateable_API.Get_Client_Value","PM_GENERATEABLE");
      cmd.addParameter("PM_GENERATEABLE","1");

      trans = mgr.perform(trans);
      data.addItem("ORG_CONTRACT",trans.getBuffer("GETVAL/DATA").getFieldValue("CONTRACT"));
      data.addItem("CONTRACT",trans.getBuffer("GETVAL/DATA").getFieldValue("CONTRACT"));
      data.addItem("COMPANY",trans.getBuffer("GETCOM/DATA").getFieldValue("COMPANY"));
      data.addItem("GENERATABLE",trans.getBuffer("GETCLIVAL/DATA").getFieldValue("PM_GENERATEABLE"));
      data.addItem("SIGNATURE",trans.getBuffer("GETPERS/DATA").getFieldValue("SIGNATURE"));

      company1 = trans.getBuffer("GETCOM/DATA").getFieldValue("COMPANY");
      orgContract1 = trans.getBuffer("GETVAL/DATA").getFieldValue("CONTRACT");
      contract1 = trans.getBuffer("GETVAL/DATA").getFieldValue("CONTRACT");
      generatable1 = trans.getBuffer("GETCLIVAL/DATA").getFieldValue("PM_GENERATEABLE");
      signature1 = trans.getBuffer("GETPERS/DATA").getFieldValue("SIGNATURE");
      validFrom2 = trans.getBuffer("GETLCHG/DATA").getFieldValue("VALID_FROM");
      signature2 = signature1;
      orgContract2 = orgContract1;
      
      csset.addRow(data);
   }

   public void  prepareForm()
   {
      ASPManager mgr = getASPManager();
      trans.clear();

      if (introFlag == 3)
      {
         csset.clear();
         nsset.clear();
         pmset.clear();
         String sGen="";
         String sGen1="";

         cmd = trans.addCustomFunction("GETDU","PM_TYPE_API.decode","PM_DUMMY");
         cmd.addParameter("PM_DUMMY","ActiveSeparate");

         if (!mgr.isEmpty(generatable1) && !generatable1.equals(mgr.translate("PCMWPMADMININSTRATORWIZNONE: None")))
         {
            if (generatable1.equals(mgr.translate("PCMWPMADMININSTRATORWIZYES: Yes")))
            {
               cmd = trans.addCustomFunction("GENA","PM_GENERATEABLE_API.decode","PM_GENERATEABLE");
               cmd.addParameter("PM_GENERATEABLE ","Y");
            }
            else if (generatable1.equals(mgr.translate("PCMWPMADMININSTRATORWIZNO: No")))
            {
               cmd = trans.addCustomFunction("GENA","PM_GENERATEABLE_API.decode","PM_GENERATEABLE");
               cmd.addParameter("PM_GENERATEABLE","N");
            }
         }
         trans = mgr.perform(trans);
         if (!mgr.isEmpty(generatable1) && !generatable1.equals(mgr.translate("PCMWPMADMININSTRATORWIZNONE: None")) &&
             (generatable1.equals(mgr.translate("PCMWPMADMININSTRATORWIZYES: Yes")) || generatable1.equals(mgr.translate("PCMWPMADMININSTRATORWIZNO: No"))))
            sGen = trans.getBuffer("GENA/DATA").getFieldValue("PM_GENERATEABLE");
         sGen1 = trans.getBuffer("GETDU/DATA").getFieldValue("PM_DUMMY");
         trans.clear();

         q = trans.addEmptyQuery(pmblk);  
         if (!mgr.isEmpty(mchCode1))
         {
            q.addWhereCondition("MCH_CODE = ?");         q.addParameter("MCH_CODE",mchCode1);
         }
         //if (!mgr.isEmpty(contract1)) { 	q.addWhereCondition("CONTRACT = ?");			q.addParameter("CONTRACT",contract1); }
         if (!mgr.isEmpty(maintEmpSign1))
         {
            q.addWhereCondition("MAINT_EMP_SIGN = ?");      q.addParameter("MAINT_EMP_SIGN",maintEmpSign1);
         }
         if (!mgr.isEmpty(signature1))
         {
            q.addWhereCondition("SIGNATURE = ?");        q.addParameter("SIGNATURE",signature1);
         }
         if (!mgr.isEmpty(opStatusId1))
         {
            q.addWhereCondition("OP_STATUS_ID = ?");     q.addParameter("OP_STATUS_ID",opStatusId1);
         }
         if (!mgr.isEmpty(priorityId1))
         {
            q.addWhereCondition("PRIORITY_ID = ?");         q.addParameter("PRIORITY_ID",priorityId1);
         }
         if (!mgr.isEmpty(orgCode1))
         {
            q.addWhereCondition("ORG_CODE = ?");         q.addParameter("ORG_CODE",orgCode1);
         }
         if (!mgr.isEmpty(orgContract1))
         {
            q.addWhereCondition("ORG_CONTRACT = ?");     q.addParameter("ORG_CONTRACT",orgContract1);
         }
         if (!mgr.isEmpty(rounddefId1))
         {
            q.addWhereCondition("ROUNDDEF_ID = ?");         q.addParameter("ROUNDDEF_ID",rounddefId1);
         }
         if (!mgr.isEmpty(workTypeId1))
         {
            q.addWhereCondition("WORK_TYPE_ID = ?");     q.addParameter("WORK_TYPE_ID",workTypeId1);
         }
         if (!mgr.isEmpty(sGen))
         {
            q.addWhereCondition("PM_GENERATEABLE = ?");     q.addParameter("GENERATABLE",sGen);
         }
         if (!mgr.isEmpty(actionCodeId1))
         {
            q.addWhereCondition("ACTION_CODE_ID = ?");      q.addParameter("ACTION_CODE_ID",actionCodeId1);
         }
         if (!mgr.isEmpty(customerNo1))
         {
            q.addWhereCondition("CUSTOMER_NO = ?");         q.addParameter("CUSTOMER_NO",customerNo1);
         }
         if (!mgr.isEmpty(contractId1))
         {
            q.addWhereCondition("CONTRACT_ID = ?");         q.addParameter("CONTRACT_ID",contractId1);
         }
         if (!mgr.isEmpty(lineNo1))
         {
            q.addWhereCondition("LINE_NO = ?");       q.addParameter("LINE_NO",lineNo1);
         }
         if (!mgr.isEmpty(currencyCode1))
         {
            q.addWhereCondition("CURRENCY_CODE = ?");    q.addParameter("CURRENCY_CODE",currencyCode1);
         }
         if (!"BLANK".equals(pmState1))
         {
            if (!"PRELIMINARY".equals(pmState1))
               if (!mgr.isEmpty(pmState1))
               {
                  q.addWhereCondition("STATE = ?");         q.addParameter("PM_STATE","Preliminary");
               }
            if (!"ACTIVE".equals(pmState1))
               if (!mgr.isEmpty(pmState1))
               {
                  q.addWhereCondition("STATE = ?");         q.addParameter("PM_STATE","Active");
               }
         }
         if ("TRUE".equals(separateAction1))
         {
            q.addWhereCondition("PM_TYPE = ?");                     q.addParameter("PM_TYPE",sGen1);
         }
         /*if (!mgr.isEmpty(objStructure1))
         /{
            q.addWhereCondition("OBJ_STRUCTURE = ?");    q.addParameter("OBJ_STRUCTURE",objStructure1);
         }*/
         if (!mgr.isEmpty(startValue1))
         {
            q.addWhereCondition("START_VALUE = ?");         q.addParameter("START_VALUE",startValue1);
         }
         if (!mgr.isEmpty(pmStartUnit1))
         {
            q.addWhereCondition("PM_START_UNIT = ?");    q.addParameter("PM_START_UNIT",pmStartUnit1);
         }
         if (!mgr.isEmpty(pmStartUnitDb1))
         {
            q.addWhereCondition("PM_START_UNIT_DB = ?");    q.addParameter("PM_START_UNIT_DB",pmStartUnitDb1);
         }
         if (!mgr.isEmpty(interval1))
         {
            q.addWhereCondition("INTERVAL = ?");         q.addParameter("INTERVAL",interval1);
         }
         if (!mgr.isEmpty(pmIntervalUnit1))
         {
            q.addWhereCondition("PM_INTERVAL_UNIT = ?");    q.addParameter("PM_INTERVAL_UNIT",pmIntervalUnit1);
         }
         if (!mgr.isEmpty(callCode1))
         {
            q.addWhereCondition("CALL_CODE = ?");        q.addParameter("CALL_CODE",callCode1);
         }
         if (!mgr.isEmpty(startCall1))
         {
            q.addWhereCondition("START_CALL = ?");       q.addParameter("START_CALL",startCall1);
         }
         if (!mgr.isEmpty(callInterval1))
         {
            q.addWhereCondition("CALL_INTERVAL = ?");    q.addParameter("CALL_INTERVAL",callInterval1);
         }
         if (!mgr.isEmpty(startDate1))
         {
            q.addWhereCondition("START_DATE = ?");       q.addParameter("START_DATE",startDate1);
         }
         if (!mgr.isEmpty(maintEmployee1))
         {
            q.addWhereCondition("MAINT_EMPLOYEE = ?");      q.addParameter("MAINT_EMPLOYEE",maintEmployee1);
         }
         //q.addWhereCondition("ORG_CONTRACT IN (SELECT &AO.User_Allowed_Site_API.Authorized(ORG_CONTRACT) FROM dual)");

         q.includeMeta("ALL");
         mgr.submit(trans);

         int i=0;
         for (i=0;i<pmset.countRows();i++)
         {
            pmset.goTo(i);
            String newRevision = "";
            if ("TRUE".equals(updateRev2))
            {
               int nRevInterval = 0;
               if (!mgr.isEmpty(revInterval2))
                  nRevInterval = Integer.parseInt(revInterval2);
               newRevision = pmset.getValue("PM_REVISION");
               int newRev = Integer.parseInt(newRevision);
               if (newRev>0 && newRev<=100000)
               {
                  newRev = newRev + nRevInterval;
                  pmset.setValue("NEW_REVISION",String.valueOf(newRev));
               }
            }
            if (!mgr.isEmpty(pmIntervalUnit2)) pmset.setValue("PM_INTERVAL_UNIT",pmIntervalUnit2);
            if (!mgr.isEmpty(pmStartUnit2)) pmset.setValue("PM_START_UNIT",pmStartUnit2);
            if (!mgr.isEmpty(generatable2)) pmset.setValue("PM_GENERATEABLE",generatable2);
            if (!mgr.isEmpty(rounddefId2))  pmset.setValue("ROUNDDEF_ID",rounddefId2);
            if (!mgr.isEmpty(interval2)) pmset.setValue("INTERVAL",interval2);
            if (!mgr.isEmpty(startValue2))  pmset.setValue("START_VALUE",startValue2);
            if (!mgr.isEmpty(actionCodeId2))   pmset.setValue("ACTION_CODE_ID",actionCodeId2);
            if (!mgr.isEmpty(opStatusId2))  pmset.setValue("OP_STATUS_ID",opStatusId2);
            if (!mgr.isEmpty(orgCode2))     pmset.setValue("ORG_CODE",orgCode2);
            if (!mgr.isEmpty(orgContract2))    pmset.setValue("ORG_CONTRACT",orgContract2);
            if (!mgr.isEmpty(priorityId2))  pmset.setValue("PRIORITY_ID",priorityId2);
            if (!mgr.isEmpty(workTypeId2))  pmset.setValue("WORK_TYPE_ID",workTypeId2);
            if (!mgr.isEmpty(callCode2)) pmset.setValue("CALL_CODE",callCode2);
            if (!mgr.isEmpty(startCall2))   pmset.setValue("START_CALL",startCall2);
            if (!mgr.isEmpty(callInterval2))   pmset.setValue("CALL_INTERVAL",callInterval2);
            if (!mgr.isEmpty(contractId2))  pmset.setValue("CONTRACT_ID",contractId2);
            if (!mgr.isEmpty(lineNo2))      pmset.setValue("LINE_NO",lineNo2);
            if (!mgr.isEmpty(customerNo2))  pmset.setValue("CUSTOMER_NO",customerNo2);
            if (!mgr.isEmpty(signature2))   pmset.setValue("SIGNATURE",signature2);
            if (!mgr.isEmpty(maintEmpSign2))   pmset.setValue("MAINT_EMP_SIGN",maintEmpSign2);
            if (!mgr.isEmpty(validFrom2))   pmset.setDateValue("VALID_FROM",validFrom2);
            if (!mgr.isEmpty(validTo2))     pmset.setDateValue("VALID_TO",validTo2);
            if (!mgr.isEmpty(company1))        pmset.setValue("COMPANY",company1);
         }
      }
   }

   public void  nextForm()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      if (introFlag==1) csset.changeRows();
      if (introFlag==2) nsset.changeRows();
      setValues(introFlag,true);
      introFlag++;
      prepareForm();
   }

   public void  previousForm()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      if (introFlag==1) csset.changeRows();
      if (introFlag==2) nsset.changeRows();
      setValues(introFlag,false);
      introFlag--;
      prepareForm();
   }

   public void setValues(int i,boolean next)
   {
      ASPManager mgr = getASPManager();

      if (i==1)
      {
         startDate1 = (!mgr.isEmpty(mgr.readValue("START_DATE"))? mgr.readValue("START_DATE"):"");
         maintEmployee1 = (!mgr.isEmpty(mgr.readValue("MAINT_EMPLOYEE"))? mgr.readValue("MAINT_EMPLOYEE"):"");
         company1 = (!mgr.isEmpty(mgr.readValue("COMPANY"))? mgr.readValue("COMPANY"):"");
         mchCode1 = (!mgr.isEmpty(mgr.readValue("MCH_CODE"))? mgr.readValue("MCH_CODE"):"");
         contract1 = (!mgr.isEmpty(mgr.readValue("CONTRACT"))? mgr.readValue("CONTRACT"):""); 
         maintEmpSign1 = (!mgr.isEmpty(mgr.readValue("MAINT_EMP_SIGN"))? mgr.readValue("MAINT_EMP_SIGN"):"");
         signature1 = (!mgr.isEmpty(mgr.readValue("SIGNATURE"))? mgr.readValue("SIGNATURE"):"");
         opStatusId1 = (!mgr.isEmpty(mgr.readValue("OP_STATUS_ID"))? mgr.readValue("OP_STATUS_ID"):"");
         priorityId1 = (!mgr.isEmpty(mgr.readValue("PRIORITY_ID"))? mgr.readValue("PRIORITY_ID"):"");
         orgCode1 = (!mgr.isEmpty(mgr.readValue("ORG_CODE"))? mgr.readValue("ORG_CODE"):"");
         orgContract1 = (!mgr.isEmpty(mgr.readValue("ORG_CONTRACT"))? mgr.readValue("ORG_CONTRACT"):"");
         rounddefId1 = (!mgr.isEmpty(mgr.readValue("ROUNDDEF_ID"))? mgr.readValue("ROUNDDEF_ID"):"");
         workTypeId1 = (!mgr.isEmpty(mgr.readValue("WORK_TYPE_ID"))? mgr.readValue("WORK_TYPE_ID"):"");
         generatable1 = (!mgr.isEmpty(mgr.readValue("GENERATABLE"))?  mgr.readValue("GENERATABLE"):"");
         actionCodeId1 = (!mgr.isEmpty(mgr.readValue("ACTION_CODE_ID"))? mgr.readValue("ACTION_CODE_ID"):"");
         customerNo1 = (!mgr.isEmpty(mgr.readValue("CUSTOMER_NO"))? mgr.readValue("CUSTOMER_NO"):"");
         contractId1 = (!mgr.isEmpty(mgr.readValue("CONTRACT_ID"))? mgr.readValue("CONTRACT_ID"):"");
         lineNo1 = (!mgr.isEmpty(mgr.readValue("LINE_NO"))?  mgr.readValue("LINE_NO"):"");
         currencyCode1 = (!mgr.isEmpty(mgr.readValue("CURRENCY_CODE"))? mgr.readValue("CURRENCY_CODE"):"");
         pmState1 = (!mgr.isEmpty(mgr.readValue("PM_STATE"))? mgr.readValue("PM_STATE"):"");
         separateAction1 = (!mgr.isEmpty(mgr.readValue("SEPARATE_ACTION"))? mgr.readValue("SEPARATE_ACTION"):"");
         objStructure1 = (!mgr.isEmpty(mgr.readValue("OBJ_STRUCTURE"))? mgr.readValue("OBJ_STRUCTURE"):"");
         startValue1 = (!mgr.isEmpty(mgr.readValue("START_VALUE"))? mgr.readValue("START_VALUE"):"");
         pmStartUnit1 = (!mgr.isEmpty(mgr.readValue("PM_START_UNIT"))? mgr.readValue("PM_START_UNIT"):"");
         pmStartUnitDb1 = (!mgr.isEmpty(mgr.readValue("PM_START_UNIT_DB"))? mgr.readValue("PM_START_UNIT_DB"):"");
         interval1 = (!mgr.isEmpty(mgr.readValue("INTERVAL"))? mgr.readValue("INTERVAL"):"");
         pmIntervalUnit1 = (!mgr.isEmpty(mgr.readValue("PM_INTERVAL_UNIT"))? mgr.readValue("PM_INTERVAL_UNIT"):"");
         callCode1 = (!mgr.isEmpty(mgr.readValue("CALL_CODE"))? mgr.readValue("CALL_CODE"):"");
         startCall1 = (!mgr.isEmpty(mgr.readValue("START_CALL"))? mgr.readValue("START_CALL"):"");
         callInterval1 = (!mgr.isEmpty(mgr.readValue("CALL_INTERVAL"))? mgr.readValue("CALL_INTERVAL"):"");
      }
      if (i==2)
      {
         orgContract2 = (!mgr.isEmpty(mgr.readValue("NS_ORG_CONTRACT"))? mgr.readValue("NS_ORG_CONTRACT"):"");
         orgCode2 = (!mgr.isEmpty(mgr.readValue("NS_ORG_CODE"))? mgr.readValue("NS_ORG_CODE"):"");
         rounddefId2 = (!mgr.isEmpty(mgr.readValue("NS_ROUNDDEF_ID"))? mgr.readValue("NS_ROUNDDEF_ID"):"");
         maintEmpSign2 = (!mgr.isEmpty(mgr.readValue("NS_MAINT_EMP_SIGN"))? mgr.readValue("NS_MAINT_EMP_SIGN"):"");
         signature2 = (!mgr.isEmpty(mgr.readValue("NS_SIGNATURE"))? mgr.readValue("NS_SIGNATURE"):"");
         opStatusId2 = (!mgr.isEmpty(mgr.readValue("NS_OP_STATUS_ID"))? mgr.readValue("NS_OP_STATUS_ID"):"");
         workTypeId2 = (!mgr.isEmpty(mgr.readValue("NS_WORK_TYPE_ID"))? mgr.readValue("NS_WORK_TYPE_ID"):"");
         priorityId2 = (!mgr.isEmpty(mgr.readValue("NS_PRIORITY_ID"))? mgr.readValue("NS_PRIORITY_ID"):"");
         actionCodeId2 = (!mgr.isEmpty(mgr.readValue("NS_ACTION_CODE_ID"))? mgr.readValue("NS_ACTION_CODE_ID"):"");
         customerNo2 = (!mgr.isEmpty(mgr.readValue("NS_CUSTOMER_NO"))? mgr.readValue("NS_CUSTOMER_NO"):"");
         contractId2 = (!mgr.isEmpty(mgr.readValue("NS_CONTRACT_ID"))? mgr.readValue("NS_CONTRACT_ID"):"");
         lineNo2 = (!mgr.isEmpty(mgr.readValue("NS_LINE_NO"))? mgr.readValue("NS_LINE_NO"):"");
         generatable2 = (!mgr.isEmpty(mgr.readValue("NS_GENERATABLE"))? mgr.readValue("NS_GENERATABLE"):"");
         validFrom2 = (!mgr.isEmpty(mgr.readValue("NS_VALID_FROM"))? mgr.readValue("NS_VALID_FROM"):"");
         validTo2 = (!mgr.isEmpty(mgr.readValue("NS_VALID_TO"))? mgr.readValue("NS_VALID_TO"):"");
         updateRev2 = (!mgr.isEmpty(mgr.readValue("NS_UPDATE_REV"))? mgr.readValue("NS_UPDATE_REV"):"");
         revInterval2 = (!mgr.isEmpty(mgr.readValue("NS_REV_INTERVAL"))? mgr.readValue("NS_REV_INTERVAL"):"");
         startValue2 = (!mgr.isEmpty(mgr.readValue("NS_START_VALUE"))? mgr.readValue("NS_START_VALUE"):"");
         pmStartUnit2 = (!mgr.isEmpty(mgr.readValue("NS_PM_START_UNIT"))? mgr.readValue("NS_PM_START_UNIT"):"");
         pmStartUnitDb2 = (!mgr.isEmpty(mgr.readValue("NS_PM_START_UNIT_DB"))? mgr.readValue("NS_PM_START_UNIT_DB"):"");
         interval2 = (!mgr.isEmpty(mgr.readValue("NS_INTERVAL"))? mgr.readValue("NS_INTERVAL"):"");
         pmIntervalUnit2 = (!mgr.isEmpty(mgr.readValue("NS_PM_INTERVAL_UNIT"))? mgr.readValue("NS_PM_INTERVAL_UNIT"):"");
         callCode2 = (!mgr.isEmpty(mgr.readValue("NS_CALL_CODE"))? mgr.readValue("NS_CALL_CODE"):"");
         startCall2 = (!mgr.isEmpty(mgr.readValue("NS_START_CALL"))? mgr.readValue("NS_START_CALL"):"");
         callInterval2 = (!mgr.isEmpty(mgr.readValue("NS_CALL_INTERVAL"))? mgr.readValue("NS_CALL_INTERVAL"):"");

         if (!next)
         {
            data = mgr.newASPBuffer();
            if (!mgr.isEmpty(mchCode1))  data.addItem("MCH_CODE",mchCode1);
            if (!mgr.isEmpty(contract1))    data.addItem("CONTRACT",contract1);
            if (!mgr.isEmpty(maintEmpSign1))   data.addItem("MAINT_EMP_SIGN",maintEmpSign1);
            if (!mgr.isEmpty(signature1))   data.addItem("SIGNATURE",signature1);
            if (!mgr.isEmpty(opStatusId1))  data.addItem("OP_STATUS_ID",opStatusId1);
            if (!mgr.isEmpty(priorityId1))  data.addItem("PRIORITY_ID",priorityId1);
            if (!mgr.isEmpty(orgCode1))  data.addItem("ORG_CODE",orgCode1);
            if (!mgr.isEmpty(orgContract1))    data.addItem("ORG_CONTRACT",orgContract1);
            if (!mgr.isEmpty(rounddefId1))  data.addItem("ROUNDDEF_ID",rounddefId1);
            if (!mgr.isEmpty(workTypeId1))  data.addItem("WORK_TYPE_ID",workTypeId1);
            if (!mgr.isEmpty(generatable1))    data.addItem("GENERATABLE",generatable1);
            if (!mgr.isEmpty(actionCodeId1))   data.addItem("ACTION_CODE_ID",actionCodeId1);
            if (!mgr.isEmpty(customerNo1))  data.addItem("CUSTOMER_NO",customerNo1);
            if (!mgr.isEmpty(contractId1))  data.addItem("CONTRACT_ID",contractId1);
            if (!mgr.isEmpty(lineNo1))      data.addItem("LINE_NO",lineNo1);
            if (!mgr.isEmpty(currencyCode1))   data.addItem("CURRENCY_CODE",currencyCode1);
            if (!mgr.isEmpty(pmState1))  data.addItem("PM_STATE",pmState1);
            if (!mgr.isEmpty(separateAction1))    data.addItem("SEPARATE_ACTION",separateAction1);
            if (!mgr.isEmpty(objStructure1))   data.addItem("OBJ_STRUCTURE",objStructure1);
            if (!mgr.isEmpty(startValue1))  data.addItem("START_VALUE",startValue1);
            if (!mgr.isEmpty(pmStartUnit1))    data.addItem("PM_START_UNIT",pmStartUnit1);
            if (!mgr.isEmpty(pmStartUnitDb1))  data.addItem("PM_START_UNIT_DB",pmStartUnitDb1);
            if (!mgr.isEmpty(interval1))    data.addItem("INTERVAL",interval1);
            if (!mgr.isEmpty(pmIntervalUnit1))    data.addItem("PM_INTERVAL_UNIT",pmIntervalUnit1);
            if (!mgr.isEmpty(callCode1))    data.addItem("CALL_CODE",callCode1);
            if (!mgr.isEmpty(startCall1))   data.addItem("START_CALL",startCall1);
            if (!mgr.isEmpty(callInterval1))   data.addItem("CALL_INTERVAL",callInterval1);
            if (!mgr.isEmpty(startDate1))   data.addItem("START_DATE",startDate1);
            if (!mgr.isEmpty(maintEmployee1))  data.addItem("MAINT_EMPLOYEE",maintEmployee1);
            if (!mgr.isEmpty(company1))  data.addItem("COMPANY",company1);

            csset.addRow(data);
         }
      }
      if (i==3 || (i==1 && next))
      {
         data = mgr.newASPBuffer();
         nsset.clear();
         if (!mgr.isEmpty(orgContract2))    data.addItem("ORG_CONTRACT",orgContract2);
         if (!mgr.isEmpty(orgCode2))        data.addItem("ORG_CODE",orgCode2);
         if (!mgr.isEmpty(rounddefId2))     data.addItem("ROUNDDEF_ID",rounddefId2);
         if (!mgr.isEmpty(maintEmpSign2))   data.addItem("MAINT_EMP_SIGN",maintEmpSign2);
         if (!mgr.isEmpty(signature2))      data.addItem("SIGNATURE",signature2);
         if (!mgr.isEmpty(opStatusId2))      data.addItem("OP_STATUS_ID",opStatusId2);
         if (!mgr.isEmpty(workTypeId2))     data.addItem("WORK_TYPE_ID",workTypeId2);
         if (!mgr.isEmpty(priorityId2))     data.addItem("PRIORITY_ID",priorityId2);
         if (!mgr.isEmpty(actionCodeId2))   data.addItem("ACTION_CODE_ID",actionCodeId2);
         if (!mgr.isEmpty(customerNo2))     data.addItem("CUSTOMER_NO",customerNo2);
         if (!mgr.isEmpty(contractId2))     data.addItem("CONTRACT_ID",contractId2);
         if (!mgr.isEmpty(lineNo2))      data.addItem("LINE_NO",lineNo2);
         if (!mgr.isEmpty(generatable2)) data.addItem("NS_GENERATABLE",generatable2);
         /*if (!mgr.isEmpty(validFrom2))      data.addItem("NS_VALID_FROM",validFrom2);
         if (!mgr.isEmpty(validTo2))     data.addItem("NS_VALID_TO",validTo2);*/
         if (!mgr.isEmpty(updateRev2))      data.addItem("NS_UPDATE_REV",updateRev2);
         if (!mgr.isEmpty(revInterval2)) data.addItem("NS_REV_INTERVAL",revInterval2);
         if (!mgr.isEmpty(startValue2))     data.addItem("START_VALUE",startValue2);
         if (!mgr.isEmpty(pmStartUnit2)) data.addItem("PM_START_UNIT",pmStartUnit2);
         if (!mgr.isEmpty(pmStartUnitDb2))  data.addItem("PM_START_UNIT_DB",pmStartUnitDb2);
         if (!mgr.isEmpty(interval2))    data.addItem("INTERVAL",interval2);
         if (!mgr.isEmpty(pmIntervalUnit2)) data.addItem("PM_INTERVAL_UNIT",pmIntervalUnit2);
         if (!mgr.isEmpty(callCode2))    data.addItem("CALL_CODE",callCode2);
         if (!mgr.isEmpty(startCall2))      data.addItem("START_CALL",startCall2);
         if (!mgr.isEmpty(callInterval2))   data.addItem("CALL_INTERVAL",callInterval2);
         if (!mgr.isEmpty(company1))        data.addItem("COMPANY",company1);


         nsset.addRow(data);
	 nsset.setDateValue("NS_VALID_FROM",validFrom2);
	 nsset.setDateValue("NS_VALID_TO",validTo2);
      }
   }

   public void  cancelForm()
   {
      ASPManager mgr = getASPManager();
      mgr.redirectTo("../Navigator.page?MAINMENU=Y&NEW=Y");
   }

   public void  finishForm()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      trans1.clear();
      pmset.changeRows();

      int i=0;
      String pmStartUnit = "";
      String StartValue = "";
      String calStatDate = "";

      for (i=0;i<pmset.countRows();i++)
      {
         pmset.goTo(i);

         pmStartUnit = pmset.getValue("PM_START_UNIT");
         StartValue = pmset.getValue("START_VALUE");
         calStatDate = "";

         if (!(mgr.isEmpty(pmStartUnit)))
         {
            String selMon = "select (Pm_Start_Unit_API.Get_Client_Value(2)) CLIENT_START2 from DUAL";
            String selWee = "select (Pm_Start_Unit_API.Get_Client_Value(1)) CLIENT_START1 from DUAL";
            String selDay = "select (Pm_Start_Unit_API.Get_Client_Value(0)) CLIENT_START from DUAL";

            trans.clear();
            trans.addQuery("YYM",selMon);
            trans.addQuery("YYW",selWee);
            trans.addQuery("YYD",selDay);

            trans = mgr.perform(trans);

            String clientValMon  = trans.getValue("YYM/DATA/CLIENT_START2");
            String clientValwee  = trans.getValue("YYW/DATA/CLIENT_START1");
            String clientValDay  = trans.getValue("YYD/DATA/CLIENT_START");

            trans.clear();

            if (clientValMon.equals(pmStartUnit))
            {
               temp = pmset.getValue("START_VALUE");
               length = temp.length();

               if (length != 4)
               {
                  dateMask = mgr.getFormatMask("Date",true);
                  mgr.showAlert(mgr.translate("PCMWPMADMINISTRATORWIZINFO1: Invalid Start Value &1. The Format should be &2 for Start Unit 'Day' and YYMM or YYWW for Start Unit 'Month' or 'Week'.",temp, dateMask));
                  return;
               }
               else
               {
                  String nTYvalue = temp.substring(0,2);
                  String nTMvalue = temp.substring(2,4);

                  String sel2 = "select to_number('"+StartValue+"') TEMPSTA from DUAL";
                  String sel3 = "select to_number('"+nTYvalue+"') TEMPTY from DUAL";
                  String sel4 = "select to_number('"+nTMvalue+"') TEMPTM from DUAL";

                  trans.addQuery("XX2",sel2);
                  trans.addQuery("XX3",sel3);
                  trans.addQuery("XX4",sel4);
                  trans = mgr.perform(trans);

                  double nTY = trans.getNumberValue("XX3/DATA/TEMPTY"); 
                  double nTM = trans.getNumberValue("XX4/DATA/TEMPTM");

                  if (( length != 4 ) ||  ( nTM < 1 ) ||  ( nTM > 12 ) ||  ( nTY < 0 ))
                  {
                     dateMask = mgr.getFormatMask("Date",true);
                     mgr.showAlert(mgr.translate("PCMWPMADMINISTRATORWIZINFO2: Invalid Start Value &1. The Format should be &2 for Start Unit 'Day' and YYMM or YYWW for Start Unit 'Month' or 'Week'.", temp, dateMask));           
                     return;
                  }
                  else
                  {
                     trans.clear();
                     String sel = "select to_date(?,'RRMM') START_DATE from DUAL";
                     q = trans.addQuery("XX",sel);
                     q.addParameter("START_VALUE",StartValue);
                     trans = mgr.perform(trans);

                     calStatDate = trans.getBuffer("XX/DATA").getFieldValue("START_DATE");
                     trans.clear();
                  }
               }   
            }
            else if (clientValwee.equals(pmStartUnit))
            {
               temp = pmset.getValue("START_VALUE");
               length = temp.length();

               cmd = trans.addCustomFunction("STARTDA","Pm_Calendar_API.Get_Date","START_DATE");
               cmd.addParameter("PM_DUMMY",StartValue);
               cmd.addParameter("INDEX1","1");

               trans = mgr.perform(trans);  

               calStatDate = trans.getBuffer("STARTDA/DATA").getFieldValue("START_DATE");

               if ((mgr.isEmpty(calStatDate) ))
               {
                  dateMask = mgr.getFormatMask("Date",true);
                  mgr.showAlert(mgr.translate("PCMWPMADMINISTRATORWIZINFO3: Invalid Start Value &1. The Format should be &2 for Start Unit 'Day' and YYMM or YYWW for Start Unit 'Month' or 'Week'.", temp, dateMask)); 
                  return;
               }
            }
            else if (clientValDay.equals(pmStartUnit))
            {
                       
               //Bug 78801, start
               calStatDate = mgr.formatDate("START_DATE",mgr.parseDate("START_DATE",StartValue));

               if ((mgr.isEmpty(calStatDate) ))
               {
                  dateMask = mgr.getFormatMask("Date",true);
                  mgr.showAlert(mgr.translate("PCMWPMADMINISTRATORWIZINFO4: Invalid Start Value &1. The Format should be a system date format &2 for Start Unit 'Day' and YYMM or YYWW for Start Unit 'Month' or 'Week'.", temp, dateMask)); 
                  return;

               }
               //Bug 78801, end
            }
         }

         trans.clear();
         cmd = trans.addCustomFunction("MAXEMP","Company_Emp_API.Get_Max_Employee_Id","SIGNATURE_ID");
         cmd.addParameter("NS_COMPANY",company1);
         cmd.addParameter("PM_SIGNATURE",pmset.getValue("SIGNATURE"));

         cmd = trans.addCustomFunction("MAXMAINTEMP","Employee_API.Get_Max_Maint_Emp","PM_MAINT_EMPLOYEE");
         cmd.addParameter("NS_COMPANY",company1);
         cmd.addParameter("PM_MAINT_EMP_SIGN",pmset.getValue("MAINT_EMP_SIGN"));

         trans = mgr.perform(trans);  
         pmset.setValue("SIGNATURE_ID", trans.getValue("MAXEMP/DATA/SIGNATURE_ID"));
         pmset.setValue("MAINT_EMPLOYEE", trans.getValue("MAXMAINTEMP/DATA/MAINT_EMPLOYEE"));

         if ("TRUE".equals(pmset.getValue("UPDATE_ROW")))
         {
            cmd = trans1.addCustomCommand("UPDATEPM"+i,"PM_ACTION_API.Bulk_Modifications__");
            cmd.addParameter("PM_NO",pmset.getValue("PM_NO"));
            cmd.addParameter("PM_REVISION",pmset.getValue("PM_REVISION"));
            cmd.addParameter("PM_ORG_CODE",pmset.getValue("ORG_CODE"));
            cmd.addParameter("PM_WORK_TYPE_ID",pmset.getValue("WORK_TYPE_ID"));
            cmd.addParameter("PM_OP_STATUS_ID",pmset.getValue("OP_STATUS_ID"));
            cmd.addParameter("PM_PRIORITY_ID",pmset.getValue("PRIORITY_ID"));
            cmd.addParameter("PM_GENERATEABLE",pmset.getValue("PM_GENERATEABLE"));
            cmd.addParameter("PM_CALL_CODE",pmset.getValue("CALL_CODE"));
            cmd.addParameter("PM_ACTION_CODE_ID",pmset.getValue("ACTION_CODE_ID"));
            cmd.addParameter("PM_START_VALUE",pmset.getValue("START_VALUE"));
            cmd.addParameter("PM_PM_START_UNIT",pmset.getValue("PM_START_UNIT"));
            cmd.addParameter("PM_INTERVAL",pmset.getValue("INTERVAL"));
            cmd.addParameter("PM_PM_INTERVAL_UNIT",pmset.getValue("PM_INTERVAL_UNIT"));
            cmd.addParameter("PM_ROUNDDEF_ID",pmset.getValue("ROUNDDEF_ID"));
            cmd.addParameter("PM_START_CALL",pmset.getValue("START_CALL"));
            cmd.addParameter("PM_CALL_INTERVAL",pmset.getValue("CALL_INTERVAL"));
            cmd.addParameter("START_VALUE_TEMP",calStatDate);
            cmd.addParameter("PM_CUSTOMER_NO",pmset.getValue("CUSTOMER_NO"));
            cmd.addParameter("PM_DUMMY","");
            cmd.addParameter("PM_DUMMY","");
            cmd.addParameter("PM_CURRENCY_CODE",pmset.getValue("CURRENCY_CODE"));
            cmd.addParameter("PM_DUMMY","");
            cmd.addParameter("PM_SIGNATURE",pmset.getValue("SIGNATURE"));
            cmd.addParameter("SIGNATURE_ID",pmset.getValue("SIGNATURE_ID"));
            cmd.addParameter("PM_ORG_CONTRACT",pmset.getValue("ORG_CONTRACT"));
            cmd.addParameter("PM_MAINT_EMP_SIGN",pmset.getValue("MAINT_EMP_SIGN"));
            cmd.addParameter("PM_MAINT_EMPLOYEE",pmset.getValue("MAINT_EMPLOYEE"));
            cmd.addParameter("VALID_FROM",pmset.getClientValue("VALID_FROM"));
            cmd.addParameter("VALID_TO",pmset.getClientValue("VALID_TO"));
            cmd.addParameter("NEW_REVISION",pmset.getValue("NEW_REVISION"));
            cmd.addParameter("PM_CONTRACT",pmset.getValue("CONTRACT_ID"));
            cmd.addParameter("PM_LINE_NO",pmset.getValue("LINE_NO"));
            cmd.addParameter("SET_ACTIVE",pmset.getValue("SET_ACTIVE"));

         }
      }

      trans1 = mgr.perform(trans1);
      cancelForm();        
   }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      val = mgr.readValue("VALIDATE");
      trans.clear();

      if ("MCH_CODE".equals(val))
      {  
         cmd = trans.addCustomFunction("HASSTRUC","Equipment_Object_API.Has_Structure__","OBJ_STRUCTURE");
         cmd.addParameter("CONTRACT");
         cmd.addParameter("MCH_CODE");

         trans = mgr.validate(trans);

         String hasStructure = trans.getValue("HASSTRUC/DATA/OBJ_STRUCTURE");

         String txt = (mgr.isEmpty(hasStructure) ? "" :hasStructure) + "^"; 

         mgr.responseWrite(txt); 
      }
      mgr.endResponse();
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      // ***************************************************
      // ***** Current Settings ****************************
      // ***************************************************     

      csblk = mgr.newASPBlock("CURSET");

      f = csblk.addField("OBJID");
      f.setHidden();

      f = csblk.addField("OBJVERSION");
      f.setHidden();

      f = csblk.addField("MCH_CODE");
      f.setLabel("PCMWPAADMINISTRATORWIZMCHCODE: Object ID");
      f.setSize(30);
      f.setMaxLength(100);
      f.setUpperCase();
      f.setDynamicLOV("MAINTENANCE_OBJECT","CONTRACT",600,450);
      f.setCustomValidation("CONTRACT,MCH_CODE","OBJ_STRUCTURE");

      f = csblk.addField("CONTRACT");
      f.setLabel("PCMWPAADMINISTRATORWIZSITE: Site");
      f.setSize(5);
      f.setUpperCase();
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);

      f = csblk.addField("MAINT_EMP_SIGN");
      f.setSize(20);
      f.setLabel("PCMWPAADMINISTRATORWIZEXEBY: Executed By");
      f.setUpperCase();
      f.setDynamicLOV("MAINT_EMPLOYEE","COMPANY",600,450);

      f  = csblk.addField("SIGNATURE");
      f.setSize(20);
      f.setUpperCase();
      f.setLabel("PCMWPAADMINISTRATORWIZSIG: Planned By");
      f.setDynamicLOV("EMPLOYEE_LOV","COMPANY",600,450);

      f  = csblk.addField("OP_STATUS_ID");
      f.setSize(3);
      f.setUpperCase();
      f.setLabel("PCMWPAADMINISTRATORWIZOPRSTATE: Operational Status");
      f.setDynamicLOV("OPERATIONAL_STATUS",600,450);

      f  = csblk.addField("PRIORITY_ID");
      f.setSize(1);
      f.setUpperCase();
      f.setLabel("PCMWPAADMINISTRATORWIZPRIORITY: Priority");
      f.setDynamicLOV("MAINTENANCE_PRIORITY",600,450);

      f  = csblk.addField("ORG_CODE");
      f.setSize(8);
      f.setUpperCase();
      f.setLabel("PCMWPAADMINISTRATORWIZORGCODE: Maint. Org");
      f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","ORG_CONTRACT CONTRACT");
      //f.enumerateValues("ORGANIZATION_API.");

      f = csblk.addField("ORG_CONTRACT");
      f.setSize(5);
      f.setUpperCase();
      f.setLabel("PCMWPAADMINISTRATORWIZORGCONTRACT: WO Site");
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV");

      f  = csblk.addField("ROUNDDEF_ID");
      f.setSize(8);
      f.setUpperCase();
      f.setLabel("PCMWPAADMINISTRATORWIZROUNDDEFID: Route ID");
      f.setDynamicLOV("PM_ROUND_DEFINITION2","CONTRACT");

      f  = csblk.addField("WORK_TYPE_ID");
      f.setSize(20);
      f.setUpperCase();
      f.setLabel("PCMWPAADMINISTRATORWIZWORKTYPID: Work Type");
      f.setDynamicLOV("WORK_TYPE");

      f  = csblk.addField("GENERATABLE");
      f.setFunction("''");
      f.setSelectBox();
      f.setSize(20);
      f.setLabel("PCMWPAADMINISTRATORWIZCSGENERATABLE: Generatable");

      f  = csblk.addField("ACTION_CODE_ID");
      f.setUpperCase();
      f.setSize(10);
      f.setLabel("PCMWPAADMINISTRATORWIZACTIONCID: Action");
      f.setDynamicLOV("MAINTENANCE_ACTION");

      f  = csblk.addField("CUSTOMER_NO");
      f.setSize(20);
      f.setUpperCase();
      f.setLabel("PCMWPAADMINISTRATORWIZCUSTOMERID: Customer No");
      f.setDynamicLOV("CUSTOMER_INFO");

      f  = csblk.addField("CONTRACT_ID");
      f.setSize(15);
      f.setLabel("PCMWPAADMINISTRATORWIZCONTRACTID: Contract ID");
      f.setUpperCase();
      f.setLOV("PscContrProductLov.page","CONTRACT,CUSTOMER_NO,MCH_CODE");

      f  = csblk.addField("LINE_NO","Number","##");
      f.setSize(25);
      f.setLabel("PCMWPAADMINISTRATORWIZLINENO: Line No");
      f.setLOV("PscContrProductLov.page","CONTRACT,CUSTOMER_NO,MCH_CODE");

      f  = csblk.addField("CURRENCY_CODE");
      f.setSize(20);
      f.setLabel("PCMWPAADMINISTRATORWIZCSCURRENCYCODE: Currency");
      f.setUpperCase();
      f.setDynamicLOV("ISO_CURRENCY");

      f  = csblk.addField("PM_STATE");      
      f.setFunction("''");      
      f.setSelectBox();
      f.setSize(20);
      f.setLabel("PCMWPAADMINISTRATORWIZPMSTATE: Status");

      f  = csblk.addField("SEPARATE_ACTION");
      f.setCheckBox("FALSE,TRUE");
      f.setSize(11);
      f.setFunction("''");
      f.setLabel("PCMWPAADMINISTRATORWIZSEPARAAC: Separate Action");

      f  = csblk.addField("OBJ_STRUCTURE");
      f.setCheckBox("FALSE,TRUE");
      f.setSize(11);
      f.setFunction("''");
      f.setReadOnly();
      f.setLabel("PCMWPAADMINISTRATORWIZOBJSTRUCT: Object In Structure");

      f = csblk.addField("START_VALUE");
      f.setLabel("PCMWPAADMINISTRATORWIZSTVALUE: Start Value");
      f.setMaxLength(11);
      f.setSize(13);
      f.setUpperCase();

      f = csblk.addField("PM_START_UNIT");
      f.setSize(12);
      f.setLabel("PCMWPAADMINISTRATORWIZPMSUNIT: Start Unit");
      f.setSelectBox();
      f.enumerateValues("PM_START_UNIT_API");

      f  = csblk.addField("PM_START_UNIT_DB");
      f.setHidden();
      f.setFunction("PM_START_UNIT_API.Encode( :PM_START_UNIT )");

      f = csblk.addField("INTERVAL");
      f.setSize(8);
      f.setLabel("PCMWPAADMINISTRATORWIZINTERVAL: Interval");

      f = csblk.addField("PM_INTERVAL_UNIT");
      f.setSize(15);
      f.setLabel("PCMWPAADMINISTRATORWIZPMINTUNIT: Interval Unit");
      f.setSelectBox();
      f.enumerateValues("PM_INTERVAL_UNIT_API");

      f  = csblk.addField("CALL_CODE");
      f.setSize(12);
      f.setLabel("PCMWPAADMINISTRATORWIZCSEVENT: Event");
      f.setUpperCase();
      f.setDynamicLOV("MAINTENANCE_EVENT",600,450);

      f  = csblk.addField("START_CALL","Number","#");
      f.setSize(10);
      f.setLabel("PCMWPAADMINISTRATORWIZSTART: Start");

      f  = csblk.addField("CALL_INTERVAL","Number", "#");
      f.setSize(8);
      f.setLabel("PCMWPAADMINISTRATORWIZCALLINTERVAL: Interval");

      f  = csblk.addField("MAINT_EMPLOYEE");
      f.setHidden();

      f  = csblk.addField("COMPANY");
      f.setHidden();

      f  = csblk.addField("START_DATE","Date");
      f.setHidden();

      f  = csblk.addField("FND_USER");
      f.setFunction("''");
      f.setHidden();

      csblk.setView("PM_ACTION");
      csset = csblk.getASPRowSet();

      csbar = mgr.newASPCommandBar(csblk);
      csbar.disableCommand(csbar.SAVERETURN);
      csbar.disableCommand(csbar.CANCELEDIT);
      csbar.disableCommand(csbar.FORWARD);
      csbar.disableModeLabel();

      cstbl = mgr.newASPTable(csblk);
      cstbl.setWrap();

      cslay = csblk.getASPBlockLayout();
      cslay.setEditable();
      cslay.setDialogColumns(3);
      cslay.defineGroup("","MCH_CODE,CONTRACT,MAINT_EMP_SIGN,SIGNATURE,OP_STATUS_ID,PRIORITY_ID,ORG_CODE,ORG_CONTRACT,ROUNDDEF_ID,WORK_TYPE_ID,GENERATABLE,ACTION_CODE_ID,CUSTOMER_NO,CONTRACT_ID,LINE_NO,CURRENCY_CODE,PM_STATE,SEPARATE_ACTION,OBJ_STRUCTURE",true,true);
      cslay.defineGroup(mgr.translate("PCMWPAADMINISTRATORWIZCALENDAR: Calendar"),"START_VALUE,PM_START_UNIT,INTERVAL,PM_INTERVAL_UNIT",true,true);
      cslay.defineGroup(mgr.translate("PCMWPAADMINISTRATORWIZEVENT: Event"),"CALL_CODE,START_CALL,CALL_INTERVAL",true,true);

      // ***************************************************
      // ********* new Settings ****************************
      // ***************************************************

      nsblk = mgr.newASPBlock("NEWSET");

      f  = nsblk.addField("NS_COMPANY");
      f.setDbName("COMPANY");
      f.setHidden();

      f = nsblk.addField("NS_ORG_CONTRACT");
      f.setDbName("ORG_CONTRACT");
      f.setLabel("PCMWPAADMINISTRATORWIZNSWOSITE: WO Site");
      f.setSize(5); 
      f.setUpperCase();
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);

      f = nsblk.addField("NS_ORG_CODE");
      f.setDbName("ORG_CODE");
      f.setLabel("PCMWPAADMINISTRATORWIZNSMAINTORG: Maint. Org");
      f.setSize(8); 
      f.setUpperCase();
      f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","NS_ORG_CONTRACT CONTRACT",600,450);

      f  = nsblk.addField("NS_ROUNDDEF_ID");
      f.setDbName("ROUNDDEF_ID");
      f.setSize(8);
      f.setUpperCase();
      f.setLabel("PCMWPAADMINISTRATORWIZNSROUNDDEFID: Route ID");
      f.setDynamicLOV("PM_ROUND_DEFINITION2","NS_ORG_CONTRACT CONTRACT");

      f = nsblk.addField("NS_MAINT_EMP_SIGN");
      f.setDbName("MAINT_EMP_SIGN");
      f.setSize(20);
      f.setLabel("PCMWPAADMINISTRATORWIZNSEXEBY: Executed By");
      f.setUpperCase();
      f.setDynamicLOV("MAINT_EMPLOYEE","NS_COMPANY COMPANY",600,450);

      f  = nsblk.addField("NS_SIGNATURE");
      f.setDbName("SIGNATURE");
      f.setSize(20);
      f.setUpperCase();
      f.setLabel("PCMWPAADMINISTRATORWIZNSSIG: Planned By");
      f.setDynamicLOV("EMPLOYEE_LOV","NS_COMPANY COMPANY",600,450);

      f  = nsblk.addField("NS_OP_STATUS_ID");
      f.setDbName("OP_STATUS_ID");
      f.setSize(3);
      f.setUpperCase();
      f.setLabel("PCMWPAADMINISTRATORWIZNSOPRSTATE: Operational Status");
      f.setDynamicLOV("OPERATIONAL_STATUS",600,450);

      f  = nsblk.addField("NS_WORK_TYPE_ID");
      f.setDbName("WORK_TYPE_ID");
      f.setSize(20);
      f.setUpperCase();
      f.setLabel("PCMWPAADMINISTRATORWIZNSWORKTYPID: Work Type");
      f.setDynamicLOV("WORK_TYPE");

      f  = nsblk.addField("NS_PRIORITY_ID");
      f.setDbName("PRIORITY_ID");
      f.setSize(1);
      f.setUpperCase();
      f.setLabel("PCMWPAADMINISTRATORWIZNSPRIORITY: Priority");
      f.setDynamicLOV("MAINTENANCE_PRIORITY",600,450);

      f  = nsblk.addField("NS_ACTION_CODE_ID");
      f.setDbName("ACTION_CODE_ID");
      f.setUpperCase();
      f.setSize(10);
      f.setLabel("PCMWPAADMINISTRATORWIZNSACTIONCID: Action");
      f.setDynamicLOV("MAINTENANCE_ACTION");

      f  = nsblk.addField("NS_CUSTOMER_NO");
      f.setDbName("CUSTOMER_NO");
      f.setSize(20);
      f.setUpperCase();
      f.setLabel("PCMWPAADMINISTRATORWIZNSCUSTOMERID: Customer No");
      f.setDynamicLOV("CUSTOMER_INFO");

      f  = nsblk.addField("NS_CONTRACT_ID");
      f.setDbName("CONTRACT_ID");
      f.setSize(15);
      f.setLabel("PCMWPAADMINISTRATORWIZNSCONTRACTID: Contract ID");
      f.setUpperCase();
      f.setLOV("PscContrProductLov.page","NS_CUSTOMER_NO CUSTOMER_NO");

      f  = nsblk.addField("NS_LINE_NO","Number","##");
      f.setDbName("LINE_NO");
      f.setSize(25);
      f.setLabel("PCMWPAADMINISTRATORWIZNSLINENO: Line No");
      f.setLOV("PscContrProductLov.page","NS_CUSTOMER_NO CUSTOMER_NO");

      f  = nsblk.addField("NS_GENERATABLE");
      f.setFunction("''");
      f.setSelectBox();
      f.setSize(20);
      f.setLabel("PCMWPAADMINISTRATORWIZGENERATABLE: Generatable");
      f.enumerateValues("PM_GENERATEABLE_API");

      f  = nsblk.addField("NS_VALID_FROM","Date");
      f.setSize(20);
      f.setFunction("''");
      f.setLabel("PCMWPAADMINISTRATORWIZNSVALIDF: Valid From");

      f  = nsblk.addField("NS_VALID_TO","Date");
      f.setSize(20);
      f.setFunction("''");
      f.setLabel("PCMWPAADMINISTRATORWIZNSVALIDT: Valid To");

      f  = nsblk.addField("NS_UPDATE_REV");
      f.setCheckBox("FALSE,TRUE");
      f.setValidateFunction("validateUpdRev");
      f.setSize(20);
      f.setFunction("''");
      f.setLabel("PCMWPAADMINISTRATORWIZNSUPDRENO: Update Revision Number");

      f  = nsblk.addField("NS_REV_INTERVAL","Number","##");
      f.setSize(6);
      f.setFunction("''");
      f.setLabel("PCMWPAADMINISTRATORWIZNSRINTERVAL: Revision Interval");

      f = nsblk.addField("NS_START_VALUE");
      f.setDbName("START_VALUE");
      f.setLabel("PCMWPAADMINISTRATORWIZNSSTVALUE: Start Value");
      f.setSize(11);
      f.setUpperCase();

      f = nsblk.addField("NS_PM_START_UNIT");
      f.setDbName("PM_START_UNIT");
      f.setSize(12);
      f.setLabel("PCMWPAADMINISTRATORWIZNSPMSUNIT: Start Unit");
      f.setSelectBox();
      f.enumerateValues("PM_START_UNIT_API");

      f  = nsblk.addField("NS_PM_START_UNIT_DB");
      f.setDbName("PM_START_UNIT_DB");
      f.setHidden();
      f.setFunction("PM_START_UNIT_API.Encode( :PM_START_UNIT )");

      f = nsblk.addField("NS_INTERVAL");
      f.setDbName("INTERVAL");
      f.setSize(12);
      f.setLabel("PCMWPAADMINISTRATORWIZNSINTERVAL: Interval");
      f.setMaxLength(3);

      f = nsblk.addField("NS_PM_INTERVAL_UNIT");
      f.setDbName("PM_INTERVAL_UNIT");
      f.setSize(20);
      f.setLabel("PCMWPAADMINISTRATORWIZNSPMINTUNIT: Interval Unit");
      f.setSelectBox();
      f.enumerateValues("PM_INTERVAL_UNIT_API");

      f  = nsblk.addField("NS_CALL_CODE");
      f.setDbName("CALL_CODE");
      f.setSize(10);
      f.setLabel("PCMWPAADMINISTRATORWIZNSEEVENT: Event");
      f.setUpperCase();
      f.setDynamicLOV("MAINTENANCE_EVENT",600,450);

      f  = nsblk.addField("NS_START_CALL","Number","#");
      f.setDbName("START_CALL");
      f.setSize(10);
      f.setLabel("PCMWPAADMINISTRATORWIZNSSTART: Start");

      f  = nsblk.addField("NS_CALL_INTERVAL","Number", "#");
      f.setDbName("CALL_INTERVAL");
      f.setSize(7);
      f.setLabel("PCMWPAADMINISTRATORWIZNSCALLINTERVAL: Interval");

      nsblk.setView("PM_ACTION");
      nsset = nsblk.getASPRowSet();

      nsbar = mgr.newASPCommandBar(nsblk);
      nsbar.disableCommand(nsbar.SAVERETURN);
      nsbar.disableCommand(nsbar.CANCELEDIT);
      nsbar.disableCommand(nsbar.FORWARD);
      nsbar.disableModeLabel();

      nstbl = mgr.newASPTable(nsblk);
      nstbl.setWrap();

      nslay = nsblk.getASPBlockLayout();
      nslay.setEditable();
      nslay.setDialogColumns(3);
      nslay.defineGroup("","NS_ORG_CONTRACT,NS_ORG_CODE,NS_ROUNDDEF_ID,NS_MAINT_EMP_SIGN,NS_SIGNATURE,NS_OP_STATUS_ID,NS_WORK_TYPE_ID,NS_PRIORITY_ID,NS_ACTION_CODE_ID,NS_CUSTOMER_NO,NS_CONTRACT_ID,NS_LINE_NO,NS_GENERATABLE,NS_VALID_FROM,NS_VALID_TO,NS_UPDATE_REV,NS_REV_INTERVAL",true,true);
      nslay.defineGroup(mgr.translate("PCMWPAADMINISTRATORWIZNSCALENDAR: Calendar"),"NS_START_VALUE,NS_PM_START_UNIT,NS_INTERVAL,NS_PM_INTERVAL_UNIT",true,true);
      nslay.defineGroup(mgr.translate("PCMWPAADMINISTRATORWIZNSEVENT: Event"),"NS_CALL_CODE,NS_START_CALL,NS_CALL_INTERVAL",true,true);


      // ***************************************************
      // ********* table of amendments *********************
      // ***************************************************

      pmblk = mgr.newASPBlock("PMSET");

      f = pmblk.addField("PM_OBJID");
      f.setDbName("OBJID");
      f.setHidden();

      f = pmblk.addField("PM_OBJVERSION");
      f.setDbName("OBJVERSION");
      f.setHidden();

      f  = pmblk.addField("PM_COMPANY");
      f.setDbName("COMPANY");
      f.setHidden();

      f = pmblk.addField("UPDATE_ROW");
      f.setCheckBox("FALSE,TRUE");
      f.setFunction("'TRUE'");
      f.setLabel("PCMWPAADMINISTRATORWIZPMUPDROW: Update Row");
      f.setSize(10);

      f = pmblk.addField("SET_ACTIVE");
      f.setCheckBox("FALSE,TRUE");
      f.setFunction("'TRUE'");
      f.setSize(5);
      f.setLabel("PCMWPAADMINISTRATORWIZPMSETAC: Set Active");

      f = pmblk.addField("PM_NO","Number","#");
      f.setLabel("PCMWPAADMINISTRATORWIZPMNOE: PM No");
      f.setSize(7); 
      f.setUpperCase();
      f.setReadOnly();

      f = pmblk.addField("PM_REVISION");
      f.setLabel("PCMWPAADMINISTRATORWIZPMREV: Revision");
      f.setSize(6); 
      f.setUpperCase();
      f.setReadOnly();

      f = pmblk.addField("NEW_REVISION");
      f.setLabel("PCMWPAADMINISTRATORWIZPMNREV: New Revision");
      f.setSize(6); 
      f.setUpperCase();
      f.setFunction("''");     

      f = pmblk.addField("PM_CONTRACT");
      f.setDbName("CONTRACT");
      f.setLabel("PCMWPAADMINISTRATORWIZPMSITE: Site");
      f.setSize(5);
      f.setUpperCase();
      f.setDynamicLOV("SITE",600,450);
      f.setMandatory();
      f.setReadOnly();

      f = pmblk.addField("PM_MCH_CODE");
      f.setDbName("MCH_CODE");
      f.setLabel("PCMWPAADMINISTRATORWIZPMMCHCODE: Object ID");
      f.setSize(15);
      f.setMaxLength(100);
      f.setUpperCase();
      f.setReadOnly();
      f.setDynamicLOV("MAINTENANCE_OBJECT","PM_CONTRACT CONTRACT",600,450);

      f  = pmblk.addField("MCH_NAME");
      f.setLabel("PCMWPAADMINISTRATORWIZPMMCHCODEDEX: Object Description");
      f.setSize(25);
      f.setFunction("MAINTENANCE_OBJECT_API.Get_Mch_Name(:PM_CONTRACT,:PM_MCH_CODE)");
      f.setReadOnly();

      f  = pmblk.addField("PM_ACTION_CODE_ID");
      f.setDbName("ACTION_CODE_ID");
      f.setUpperCase();
      f.setSize(7);
      f.setLabel("PCMWPAADMINISTRATORWIZPMACTIONCID: Action");
      f.setDynamicLOV("MAINTENANCE_ACTION");

      f  = pmblk.addField("ACTION_DESC");
      f.setFunction("MAINTENANCE_ACTION_API.Get_Description(:PM_ACTION_CODE_ID)");
      f.setSize(25);
      f.setLabel("PCMWPAADMINISTRATORWIZPMACTIONDEX: Description");
      f.setReadOnly();

      f  = pmblk.addField("STATE");
      f.setSize(12);
      f.setReadOnly();
      f.setLabel("PCMWPAADMINISTRATORWIZPMBSTATE: Status");

      f = pmblk.addField("PM_MAINT_EMP_SIGN");
      f.setDbName("MAINT_EMP_SIGN");
      f.setSize(8);
      f.setLabel("PCMWPAADMINISTRATORWIZPMEXEBY: Executed By");
      f.setUpperCase();
      f.setDynamicLOV("MAINT_EMPLOYEE","PM_COMPANY COMPANY",600,450);

      f  = pmblk.addField("PM_SIGNATURE");
      f.setDbName("SIGNATURE");
      f.setSize(8);
      f.setUpperCase();      
      f.setLabel("PCMWPAADMINISTRATORWIZPMSIG: Planned By");
      f.setDynamicLOV("EMPLOYEE_LOV","PM_COMPANY COMPANY",600,450);

      f  = pmblk.addField("PM_TYPE");
      f.setSize(10);
      f.setLabel("PCMWPAADMINISTRATORWIZPMTYPE: PM Type");
      f.setMandatory();
      f.setReadOnly();

      f  = pmblk.addField("PM_OP_STATUS_ID");
      f.setDbName("OP_STATUS_ID");
      f.setSize(3);
      f.setUpperCase();
      f.setLabel("PCMWPAADMINISTRATORWIZPMOPRSTATE: Operational Status");
      f.setDynamicLOV("OPERATIONAL_STATUS",600,450);

      f = pmblk.addField("PM_ORG_CODE");
      f.setDbName("ORG_CODE");
      f.setLabel("PCMWPAADMINISTRATORWIZPMMAINTORG: Maintenance Organization");
      f.setSize(5); 
      f.setUpperCase();
      f.setDynamicLOV("ORG_CODE_ALLOWED_SITE_LOV","PM_CONTRACT CONTRACT",600,450);

      f = pmblk.addField("PM_ORG_CONTRACT");
      f.setDbName("ORG_CONTRACT");
      f.setLabel("PCMWPAADMINISTRATORWIZPPMWOSITE: Maint. Org. Site");
      f.setSize(5); 
      f.setUpperCase();
      f.setMandatory();
      f.setDynamicLOV("USER_ALLOWED_SITE_LOV",600,450);

      f  = pmblk.addField("PM_PRIORITY_ID");
      f.setDbName("PRIORITY_ID");
      f.setSize(1);
      f.setUpperCase();
      f.setLabel("PCMWPAADMINISTRATORWIZPMPRIORITY: Priority");
      f.setDynamicLOV("MAINTENANCE_PRIORITY",600,450);

      f  = pmblk.addField("PM_WORK_TYPE_ID");
      f.setDbName("WORK_TYPE_ID");
      f.setSize(10);
      f.setUpperCase();
      f.setLabel("PCMWPAADMINISTRATORWIZPMWORKTYPID: Work Type");
      f.setDynamicLOV("WORK_TYPE");

      f  = pmblk.addField("PM_GENERATEABLE");
      f.setSelectBox();
      f.setSize(20);
      f.setLabel("PCMWPAADMINISTRATORWIZPMGENERATABLE: Generatable");
      f.enumerateValues("PM_GENERATEABLE_API");

      f  = pmblk.addField("VALID_FROM","Date");
      f.setSize(20);
      f.setLabel("PCMWPAADMINISTRATORWIZPMVALIDF: Valid From");

      f  = pmblk.addField("VALID_TO","Date");
      f.setSize(20);
      f.setLabel("PCMWPAADMINISTRATORWIZPMVALIDT: Valid To");

      f = pmblk.addField("PM_START_VALUE");
      f.setDbName("START_VALUE");
      f.setLabel("PCMWPAADMINISTRATORWIZPMSTVALUE: Start Value");
      f.setSize(11);
      f.setUpperCase();

      f  = pmblk.addField("PM_START_DATE","Date");
      f.setDbName("START_DATE");
      f.setHidden();

      f = pmblk.addField("PM_PM_START_UNIT");
      f.setDbName("PM_START_UNIT");
      f.setSize(12);
      f.setLabel("PCMWPAADMINISTRATORWIZPMPMSUNIT: Start Unit");
      f.setSelectBox();
      f.enumerateValues("PM_START_UNIT_API");

      f  = pmblk.addField("PM_PM_START_UNIT_DB");
      f.setDbName("PM_START_UNIT_DB");
      f.setHidden();
      f.setFunction("PM_START_UNIT_API.Encode( :PM_PM_START_UNIT )");

      f = pmblk.addField("PM_INTERVAL");
      f.setDbName("INTERVAL");
      f.setSize(3);
      f.setLabel("PCMWPAADMINISTRATORWIZPMINTERVAL: Interval");

      f = pmblk.addField("PM_PM_INTERVAL_UNIT");
      f.setDbName("PM_INTERVAL_UNIT");
      f.setSize(20);
      f.setLabel("PCMWPAADMINISTRATORWIZPMPMINTUNIT: Interval Unit");
      f.setSelectBox();
      f.enumerateValues("PM_INTERVAL_UNIT_API");

      f  = pmblk.addField("PM_CALL_CODE");
      f.setDbName("CALL_CODE");
      f.setSize(7);
      f.setLabel("PCMWPAADMINISTRATORWIZPMEVENT: Event");
      f.setUpperCase();
      f.setDynamicLOV("MAINTENANCE_EVENT",600,450);

      f  = pmblk.addField("PM_START_CALL","Number","#");
      f.setDbName("START_CALL");
      f.setSize(2);
      f.setLabel("PCMWPAADMINISTRATORWIZPMSTART: Event Start");

      f  = pmblk.addField("PM_CALL_INTERVAL","Number", "#");
      f.setDbName("CALL_INTERVAL");
      f.setSize(2);
      f.setLabel("PCMWPAADMINISTRATORWIZPEVENTMINTERVAL: Event Interval");

      f  = pmblk.addField("PM_ROUNDDEF_ID");
      f.setDbName("ROUNDDEF_ID");
      f.setSize(8);
      f.setUpperCase();
      f.setLabel("PCMWPAADMINISTRATORWIZPMROUNDDEFID: Route ID");
      f.setDynamicLOV("PM_ROUND_DEFINITION2","PM_ORG_CONTRACT CONTRACT");

      f  = pmblk.addField("PM_CUSTOMER_NO");
      f.setDbName("CUSTOMER_NO");				 
      f.setSize(10);
      f.setUpperCase();
      f.setLabel("PCMWPAADMINISTRATORWIZPMCUSTOMERID: Customer No");
      f.setDynamicLOV("CUSTOMER_INFO");

      f  = pmblk.addField("PM_CONTRACT_ID");
      f.setDbName("CONTRACT_ID");
      f.setSize(10);
      f.setLabel("PCMWPAADMINISTRATORWIZPMCONTRACTID: Contract ID");
      f.setUpperCase();
      f.setDynamicLOV("PSC_CONTR_PRODUCT_LOV3");

      f  = pmblk.addField("PM_LINE_NO","Number","##");
      f.setDbName("LINE_NO");
      f.setSize(6);
      f.setLabel("PCMWPAADMINISTRATORWIZPMLINENO: Line No");
      f.setDynamicLOV("PSC_CONTR_PRODUCT_LOV3","PM_CONTRACT_ID CONTRACT_ID");

      f  = pmblk.addField("PM_CURRENCY_CODE");
      f.setDbName("CURRENCY_CODE");
      f.setReadOnly();
      f.setSize(6);
      f.setLabel("PCMWPAADMINISTRATORWIZCURRENCYCODE: Currency");
      f.setUpperCase();
      f.setDynamicLOV("ISO_CURRENCY");

      f  = pmblk.addField("SIGNATURE_ID");
      f.setHidden();

      f  = pmblk.addField("PM_MAINT_EMPLOYEE");
      f.setDbName("MAINT_EMPLOYEE");
      f.setHidden();

      f  = pmblk.addField("VALID_PM");
      f.setFunction("Pm_Action_Api.Check_New_Row(:PM_NO,:PM_REVISION)");
      f.setHidden();

      f  = pmblk.addField("OBJSTATE");
      f.setHidden();

      f  = pmblk.addField("PM_DUMMY");
      f.setHidden();
      f.setFunction("''");

      f  = pmblk.addField("INDEX1");
      f.setHidden();
      f.setFunction("''");

      f  = pmblk.addField("CLIENT_START");
      f.setHidden();
      f.setFunction("''");

      f  = pmblk.addField("CLIENT_START1");
      f.setHidden();
      f.setFunction("''");

      f  = pmblk.addField("CLIENT_START2");
      f.setHidden();
      f.setFunction("''");

      f  = pmblk.addField("START_VALUE_TEMP","Date");
      f.setHidden();
      f.setFunction("''");

      f  = pmblk.addField("DUMMY_VAL");
      f.setHidden();
      f.setFunction("''");

      f  = pmblk.addField("WO_EXIST");
      f.setFunction("Pm_Action_Calendar_Plan_API.Generated_Wo_Exist(:PM_NO,:PM_REVISION)");

      pmblk.setView("PM_ACTION");
      pmblk.defineCommand("PM_ACTION_API","Modify__");
      pmset = pmblk.getASPRowSet();

      pmbar = mgr.newASPCommandBar(pmblk);

      pmtbl = mgr.newASPTable(pmblk);
      pmtbl.setEditable();

      pmlay = pmblk.getASPBlockLayout();
      pmlay.setDefaultLayoutMode(pmlay.MULTIROW_LAYOUT);
      pmlay.unsetAutoLayoutSelect();      

      enableConvertGettoPost();
   }  

   public void adjust()
   {
      ASPManager mgr = getASPManager();

      mgr.getASPField("VALID_PM").setHidden();
      mgr.getASPField("WO_EXIST").setHidden();

      if (introFlag == 2 && mgr.isEmpty(mgr.readValue("ROUNDDEF_ID"))) 
      {
         mgr.getASPField("NS_ROUNDDEF_ID").setReadOnly();
      }
   }

//===============================================================
//  HTML
//===============================================================
   protected AutoString getContents() throws FndException
   { 
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();

      if (bFirstRequest && mgr.isExplorer())
      {
         out.append("<body onload=\"javascript:onLoad();\">\n");  
         out.append("<head></head>\n"); 
         out.append("<body>"); 
         out.append("<form name='form' method='POST'action='"+mgr.getURL()+"?"+mgr.getQueryString()+"'>"); 
         out.append(fmt.drawHidden("FIRST_REQUEST", "OK"));            
         out.append("</form></body></html>"); 

         appendDirtyJavaScript("document.form.submit();"); 
      }
      else
      {
         out.append("<body onload=\"javascript:onLoad();\">\n"); 
         out.append("<head>");
         out.append(mgr.generateHeadTag("PCMWPMADMININSTRATORWIX: PM Administrator"));
         out.append("<title></title>\n");      
         out.append("</head>\n");
         out.append("<body>\n");
         out.append("<form ");
         out.append(mgr.generateFormTag());
         out.append(">\n");
         out.append("<BR>\n");
         out.append(mgr.startPresentation("PCMWPMADMININSTRATORWIX: PM Administrator"));
         out.append("&nbsp;");
         if (introFlag == 1)
            out.append(fmt.drawReadValue(mgr.translate("PCMWPMADMININSTRATORWIZST1: Current Settings")));
         else if (introFlag == 2)
            out.append(fmt.drawReadValue(mgr.translate("PCMWPMADMININSTRATORWIZST2: New Settings")));
         else if (introFlag == 3)
            out.append(fmt.drawReadValue(mgr.translate("PCMWPMADMININSTRATORWIZST3: Table of Amendments")));
         out.append("&nbsp;");
         out.append("  <table id=mainTBL class=\"BlockLayoutTable\" border=\"0\" width=708>\n");
         out.append("    <tr>\n");
         out.append("      <td width=\"708\">\n");
         if (introFlag == 1)
         {
            cslay.setLayoutMode(cslay.EDIT_LAYOUT);
            out.append(cslay.generateDialog());
         }
         if (introFlag == 2)
         {
            nslay.setLayoutMode(nslay.EDIT_LAYOUT);
            out.append(nslay.generateDialog());
         }
         if (introFlag == 3)
         {
            pmlay.setLayoutMode(pmlay.MULTIROW_LAYOUT);
            out.append(pmlay.generateDataPresentation());
         }
         out.append("      </td>\n");
         out.append("    </tr>\n");
         out.append("    <tr>\n");
         out.append("      <td width=\"708\">\n");
         out.append("	 <p align=\"right\">\n");
         out.append("&nbsp;");
         out.append(fmt.drawSubmit("CANCEL",mgr.translate("PCMWPMADMININSTRATORWIZCANCEL: Cancel"),"CANCEL"));
         out.append("&nbsp;");
         if (introFlag != 1)
         {
            out.append(fmt.drawSubmit("PREVIOUS",mgr.translate("PCMWPMADMININSTRATORWIZPRE: <Previous"),"PREVIOUS"));               
            out.append("&nbsp;");
         }
         if (introFlag != 3)
         {
            out.append(fmt.drawButton("NEXT",mgr.translate("PCMWPMADMININSTRATORWIZNEXT:  Next> "),"OnClick=\"allFieldsValid("+String.valueOf(introFlag)+")\""));
            out.append("&nbsp;");
         }
         if (introFlag == 3)
            out.append(fmt.drawSubmit("FINISH",mgr.translate("PCMWPMADMININSTRATORWIZFINISH: Finish"),"FINISH"));
         out.append("   </td>\n");
         out.append("  </tr>\n");
         out.append("</table>\n");
         out.append("   <p>\n");
         out.append(mgr.endPresentation());
         out.append("<input type=\"hidden\" name=\"NEXTFLAG\" value=''>\n");
         out.append("</form>\n");
         out.append("</body>\n");

         appendDirtyJavaScript("function allFieldsValid(i)\n");
         appendDirtyJavaScript("{\n");      
         appendDirtyJavaScript("   document.form.NEXTFLAG.value = \"NEXTFLAG\";\n");
         appendDirtyJavaScript("   if(i == 1)\n");
         appendDirtyJavaScript("      if(checkCursetFields(0)) f.submit();\n");
         appendDirtyJavaScript("   if(i == 2)\n");
         appendDirtyJavaScript("      if(checkNewsetFields(0)) f.submit();\n"); 
         appendDirtyJavaScript("}\n");

         if (introFlag == 1)
         {
            appendDirtyJavaScript("{\n");      
            appendDirtyJavaScript("   document.form.GENERATABLE[document.form.GENERATABLE.length] = new Option(\"\",\"BLANK\");\n");
            appendDirtyJavaScript("   document.form.GENERATABLE[document.form.GENERATABLE.length] = new Option(\"" + mgr.translate("PCMWPMADMININSTRATORWIZYES: Yes") +"\",\"YES\");\n");
            appendDirtyJavaScript("   document.form.GENERATABLE[document.form.GENERATABLE.length] = new Option(\"" + mgr.translate("PCMWPMADMININSTRATORWIZNO: No") +"\",\"NO\");\n");
            appendDirtyJavaScript("   document.form.GENERATABLE[document.form.GENERATABLE.length] = new Option(\"" + mgr.translate("PCMWPMADMININSTRATORWIZNONE: None") +"\",\"NONE\");\n");
            appendDirtyJavaScript("   document.form.PM_STATE[document.form.PM_STATE.length] = new Option(\"\",\"BLANK\");\n");
            appendDirtyJavaScript("   document.form.PM_STATE[document.form.PM_STATE.length] = new Option(\"" + mgr.translate("PCMWPMADMININSTRATORWIZPRELIMINARY: Preliminary") +"\",\"PRELIMINARY\");\n");
            appendDirtyJavaScript("   document.form.PM_STATE[document.form.PM_STATE.length] = new Option(\"" + mgr.translate("PCMWPMADMININSTRATORWIZACTIVE: Active") +"\",\"ACTIVE\");\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function lovContractId(i,params)\n{\n");
            appendDirtyJavaScript("	if(params) param = params;\n");
            appendDirtyJavaScript("	else param = '';\n");
            appendDirtyJavaScript("	var enable_multichoice =(true && CURSET_IN_FIND_MODE);\n");
            appendDirtyJavaScript("	var key_value = (getValue_('CONTRACT_ID',i).indexOf('%') !=-1)? getValue_('CONTRACT_ID',i):'';\n");
            appendDirtyJavaScript("	whereCond1 = '';\n"); 
            appendDirtyJavaScript("	whereCond1 = \" OBJSTATE = 'Active' AND CONTRACT IN (SELECT CONTRACT FROM SITE_PUBLIC WHERE EXISTS (SELECT 1 FROM USER_ALLOWED_SITE_PUB WHERE CONTRACT = SITE)) \";\n");
            appendDirtyJavaScript("	whereCond1 += \" AND (CUSTOMER_NO = NVL('\" + URLClientEncode(document.form.CUSTOMER_NO.value) + \"',CUSTOMER_NO)) \" ;\n"); 
            if (mgr.isModuleInstalled("PCMSCI"))
               appendDirtyJavaScript("	  whereCond1 += \" AND (((CONTRACT_ID,LINE_NO) IN (SELECT contract_id, line_no FROM PSC_SRV_LINE_OBJECTS WHERE mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' AND mch_contract = '\" + URLClientEncode(document.form.CONTRACT.value) + \"')) OR (mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' AND mch_contract = '\" + URLClientEncode(document.form.CONTRACT.value) + \"') OR CONNECTION_TYPE_DB = 'CATEGORY')\";\n");         
            appendDirtyJavaScript("		openLOVWindow('CONTRACT_ID',i,\n");
            appendDirtyJavaScript("		'PscContrProductLov.page' + '?__VIEW=DUMMY&__INIT=1&__LOV=Y&__WHERE=' +whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
            appendDirtyJavaScript("							,550,500,'validateContractId');\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function lovLineNo(i,params)\n{\n");
            appendDirtyJavaScript("	if(params) param = params;\n");
            appendDirtyJavaScript("	else param = '';\n");
            appendDirtyJavaScript("	var enable_multichoice =(true && CURSET_IN_FIND_MODE);\n");
            appendDirtyJavaScript("	var key_value = (getValue_('LINE_NO',i).indexOf('%') !=-1)? getValue_('LINE_NO',i):'';\n");
            appendDirtyJavaScript("	whereCond1 = '';\n"); 
            appendDirtyJavaScript("	whereCond1 = \" OBJSTATE = 'Active' AND CONTRACT IN (SELECT CONTRACT FROM SITE_PUBLIC WHERE EXISTS (SELECT 1 FROM USER_ALLOWED_SITE_PUB WHERE CONTRACT = SITE)) \";\n");
            appendDirtyJavaScript("	whereCond1 += \" AND (CUSTOMER_NO = NVL('\" + URLClientEncode(document.form.CUSTOMER_NO.value) + \"',CUSTOMER_NO)) \" ;\n"); 
            if (mgr.isModuleInstalled("PCMSCI"))
               appendDirtyJavaScript("	  whereCond1 += \" AND (((CONTRACT_ID,LINE_NO) IN (SELECT contract_id, line_no FROM PSC_SRV_LINE_OBJECTS WHERE mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' AND mch_contract = '\" + URLClientEncode(document.form.CONTRACT.value) + \"')) OR (mch_code = '\" + URLClientEncode(document.form.MCH_CODE.value) + \"' AND mch_contract = '\" + URLClientEncode(document.form.CONTRACT.value) + \"') OR CONNECTION_TYPE_DB = 'CATEGORY')\";\n");         
            appendDirtyJavaScript("		openLOVWindow('LINE_NO',i,\n");
            appendDirtyJavaScript("		'PscContrProductLov.page' + '?__VIEW=DUMMY&__INIT=1&__LOV=Y&__WHERE=' +whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
            appendDirtyJavaScript("							,550,500,'validateLineNo');\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function validateContractId(i)\n");
            appendDirtyJavaScript("{\n");
	         appendDirtyJavaScript("if( getRowStatus_('CURSET',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("if (getValue_('CONTRACT_ID',i).indexOf('^') !=-1)\n"); 
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   var strArr = getValue_('CONTRACT_ID',i).split('^');\n");
            appendDirtyJavaScript("   document.form.CONTRACT_ID.value = strArr[0];\n");
            appendDirtyJavaScript("   document.form.LINE_NO.value = strArr[1];\n");
            appendDirtyJavaScript(" }\n");
            appendDirtyJavaScript("else\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   document.form.CONTRACT_ID.value = getValue_('CONTRACT_ID',i);\n");
            appendDirtyJavaScript("   document.form.LINE_NO.value = getValue_('LINE_NO',i);\n");
            appendDirtyJavaScript("}\n");
            appendDirtyJavaScript("	if( !checkContractId(i) ) return;\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function validateLineNo(i)\n");
            appendDirtyJavaScript("{\n");
	         appendDirtyJavaScript("if( getRowStatus_('CURSET',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");            
            appendDirtyJavaScript("if (getValue_('LINE_NO',i).indexOf('^') !=-1)\n"); 
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   var strArr = getValue_('LINE_NO',i).split('^');\n");
            appendDirtyJavaScript("   document.form.CONTRACT_ID.value = strArr[0];\n");
            appendDirtyJavaScript("   document.form.LINE_NO.value = strArr[1];\n");
            appendDirtyJavaScript(" }\n");
            appendDirtyJavaScript("else\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   document.form.CONTRACT_ID.value = getValue_('CONTRACT_ID',i);\n");
            appendDirtyJavaScript("   document.form.LINE_NO.value = getValue_('LINE_NO',i);\n");
            appendDirtyJavaScript("}\n");
            appendDirtyJavaScript("	if( !checkLineNo(i) ) return;\n");
            appendDirtyJavaScript("}\n");

         }

         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   if (\"YES\" == \""+mgr.encodeStringForJavascript(generatable1)+"\")\n");
         appendDirtyJavaScript("      document.form.GENERATABLE.selectedIndex = 1;");
         appendDirtyJavaScript("   if (\"NO\" == \""+mgr.encodeStringForJavascript(generatable1)+"\")\n");
         appendDirtyJavaScript("      document.form.GENERATABLE.selectedIndex = 2;");
         appendDirtyJavaScript("   if (\"NONE\" == \""+mgr.encodeStringForJavascript(generatable1)+"\")\n");
         appendDirtyJavaScript("      document.form.GENERATABLE.selectedIndex = 3;");
         appendDirtyJavaScript("}\n");

	      if (introFlag==2) 
         {
            appendDirtyJavaScript("validateUpdRev();\n\n");

            appendDirtyJavaScript("function lovNsContractId(i,params)\n{\n");
            appendDirtyJavaScript("	if(params) param = params;\n");
            appendDirtyJavaScript("	else param = '';\n");
            appendDirtyJavaScript("	var enable_multichoice =(true && NEWSET_IN_FIND_MODE);\n");
            appendDirtyJavaScript("	var key_value = (getValue_('NS_CONTRACT_ID',i).indexOf('%') !=-1)? getValue_('NS_CONTRACT_ID',i):'';\n");
            appendDirtyJavaScript("	whereCond1 = '';\n"); 
            appendDirtyJavaScript("	whereCond1 = \" OBJSTATE = 'Active' AND CONTRACT IN (SELECT CONTRACT FROM SITE_PUBLIC WHERE EXISTS (SELECT 1 FROM USER_ALLOWED_SITE_PUB WHERE CONTRACT = SITE)) \";\n");
            appendDirtyJavaScript("	whereCond1 += \" AND (CUSTOMER_NO = NVL('\" + URLClientEncode(document.form.NS_CUSTOMER_NO.value) + \"',CUSTOMER_NO)) \" ;\n"); 
            if (mgr.isModuleInstalled("PCMSCI"))
               appendDirtyJavaScript("	  whereCond1 += \" AND (((CONTRACT_ID,LINE_NO) IN (SELECT contract_id, line_no FROM PSC_SRV_LINE_OBJECTS WHERE mch_code = '" +mchCode1+ "' AND mch_contract = '" +contract1+ "')) OR (mch_code = '" +mchCode1+ "' AND mch_contract = '" +contract1+ "') OR CONNECTION_TYPE_DB = 'CATEGORY')\";\n");         
            appendDirtyJavaScript("		openLOVWindow('NS_CONTRACT_ID',i,\n");
            appendDirtyJavaScript("		'PscContrProductLov.page' + '?__VIEW=DUMMY&__INIT=1&__LOV=Y&__WHERE=' +whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
            appendDirtyJavaScript("							,550,500,'validateNsContractId');\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function lovNsLineNo(i,params)\n{\n");
            appendDirtyJavaScript("	if(params) param = params;\n");
            appendDirtyJavaScript("	else param = '';\n");
            appendDirtyJavaScript("	var enable_multichoice =(true && NEWSET_IN_FIND_MODE);\n");
            appendDirtyJavaScript("	var key_value = (getValue_('NS_LINE_NO',i).indexOf('%') !=-1)? getValue_('NS_LINE_NO',i):'';\n");
            appendDirtyJavaScript("	whereCond1 = '';\n"); 
            appendDirtyJavaScript("	whereCond1 = \" OBJSTATE = 'Active' AND CONTRACT IN (SELECT CONTRACT FROM SITE_PUBLIC WHERE EXISTS (SELECT 1 FROM USER_ALLOWED_SITE_PUB WHERE CONTRACT = SITE)) \";\n");
            appendDirtyJavaScript("	whereCond1 += \" AND (CUSTOMER_NO = NVL('\" + URLClientEncode(document.form.NS_CUSTOMER_NO.value) + \"',CUSTOMER_NO)) \" ;\n"); 
            if (mgr.isModuleInstalled("PCMSCI"))
               appendDirtyJavaScript("	  whereCond1 += \" AND (((CONTRACT_ID,LINE_NO) IN (SELECT contract_id, line_no FROM PSC_SRV_LINE_OBJECTS WHERE mch_code = '" +mchCode1+ "' AND mch_contract = '" +contract1+ "')) OR (mch_code = '" +mchCode1+ "' AND mch_contract = '" +contract1+ "') OR CONNECTION_TYPE_DB = 'CATEGORY')\";\n");         
            appendDirtyJavaScript("		openLOVWindow('NS_LINE_NO',i,\n");
            appendDirtyJavaScript("		'PscContrProductLov.page' + '?__VIEW=DUMMY&__INIT=1&__LOV=Y&__WHERE=' +whereCond1+'&MULTICHOICE='+enable_multichoice+''\n");
            appendDirtyJavaScript("							,550,500,'validateNsLineNo');\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function validateNsContractId(i)\n");
            appendDirtyJavaScript("{\n");
	         appendDirtyJavaScript("if( getRowStatus_('NEWSET',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");
            appendDirtyJavaScript("if (getValue_('NS_CONTRACT_ID',i).indexOf('^') !=-1)\n"); 
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   var strArr = getValue_('NS_CONTRACT_ID',i).split('^');\n");
            appendDirtyJavaScript("   document.form.NS_CONTRACT_ID.value = strArr[0];\n");
            appendDirtyJavaScript("   document.form.NS_LINE_NO.value = strArr[1];\n");
            appendDirtyJavaScript(" }\n");
            appendDirtyJavaScript("else\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   document.form.NS_CONTRACT_ID.value = getValue_('NS_CONTRACT_ID',i);\n");
            appendDirtyJavaScript("   document.form.NS_LINE_NO.value = getValue_('NS_LINE_NO',i);\n");
            appendDirtyJavaScript("}\n");
            appendDirtyJavaScript("	if( !checkNsContractId(i) ) return;\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function validateNsLineNo(i)\n");
            appendDirtyJavaScript("{\n");
	         appendDirtyJavaScript("if( getRowStatus_('NEWSET',i)=='QueryMode__' ) return;\n");
            appendDirtyJavaScript("	setDirty();\n");            
            appendDirtyJavaScript("if (getValue_('NS_LINE_NO',i).indexOf('^') !=-1)\n"); 
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   var strArr = getValue_('NS_LINE_NO',i).split('^');\n");
            appendDirtyJavaScript("   document.form.NS_CONTRACT_ID.value = strArr[0];\n");
            appendDirtyJavaScript("   document.form.NS_LINE_NO.value = strArr[1];\n");
            appendDirtyJavaScript(" }\n");
            appendDirtyJavaScript("else\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("   document.form.NS_CONTRACT_ID.value = getValue_('NS_CONTRACT_ID',i);\n");
            appendDirtyJavaScript("   document.form.NS_LINE_NO.value = getValue_('NS_LINE_NO',i);\n");
            appendDirtyJavaScript("}\n");
            appendDirtyJavaScript("	if( !checkNsLineNo(i) ) return;\n");
            appendDirtyJavaScript("}\n");

            appendDirtyJavaScript("function checkNsRevInterval(i)\n");
            appendDirtyJavaScript("{\n");
            appendDirtyJavaScript("	fld = getField_('NS_REV_INTERVAL',i);\n");
            appendDirtyJavaScript(" if(f.NS_UPDATE_REV.checked == true) \n");
            appendDirtyJavaScript("	  return checkNumberValue_(fld,'Revision Interval','') && checkMandatory_(fld,'Revision Interval','');\n");
            appendDirtyJavaScript(" else\n");
            appendDirtyJavaScript("	  return checkNumberValue_(fld,'Revision Interval','');\n");
            appendDirtyJavaScript("}\n");

         }

         appendDirtyJavaScript("function validateUpdRev()\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   if(f.NS_UPDATE_REV.checked == true)\n");
         appendDirtyJavaScript("      f.NS_REV_INTERVAL.disabled = false;\n");
         appendDirtyJavaScript("   else\n");
         appendDirtyJavaScript("   {\n");
         appendDirtyJavaScript("      f.NS_REV_INTERVAL.disabled = true;\n");
         appendDirtyJavaScript("      f.NS_REV_INTERVAL.value = \"\";\n");
         appendDirtyJavaScript("   }\n");
         appendDirtyJavaScript("}\n");
      }

      return out;
   }
}
