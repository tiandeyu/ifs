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
*  File        : MaintTask.java 
*  Created     : CHAMLK  021231 
*  Modified    : 
*  ARWILK  030909  Added the RMB "Perform Structure Change" and the method getDefaultValues().
*                  (IID GEAM223N Install Part WEB)
*  VAGULK  030911  Added Oper Param Tab.(Asynchronous Reporting)
*  SAPRLK  031230  Web Alignment - removed methods clone() and doReset().
*  SAPRLK  040211  Web Alignment - remove unnecessary method calls for hidden fields, 
*                  removed unnecessary global variables.  
*  SAPRLK  040212  Web Alignment - changing the code for tabs & removal of unnecessary methods.
*  ARWILK  041001  LCS Merge:46394
* ----------------------------------------------------------------------------
*  VAGULK  050722  AMAD 119-ECSS.
*  HiWelk  050225  Bug 48930  Displayed info messages raised in the server.
*  DiAmlk  050729  Merged the corrections done in LCS Bug ID:48930.
*  BUNILK  050926  Altered performStuctChange() method. 
*  BUNILK  051010  Added new Action 'Report Fault'.
*  SHAFLK  060223  GUI change(Call 135158)
*  SHAFLK  060227  Modified cancel(), run(),printContents() and added okFindMaint(). (Call 135574)
*  SHAFLK  060313  Modified performStuctChange().Call 136936
*  AMDILK  060728  Bug 58214, Eliminated XSS security vulnerability and added html & javascript encoding.
*  AMNILK  060807  Merged Bug ID: 58214.
*  AMNILK  060815  Bug 58216, Eliminated SQL Injection security vulnerability.
*  AMNILK  060904  Merged Bug Id: 58216.
*  ASSALK  070130  Wings merge. (ASSALK  070118  B129409: Modified preDefine() and validate(), okFindITEM5())
*  ASSALK  070212  Call 140802, Modified vilidate(), adjust(), preDefine().
*  ARWILK  070227  Call 140827, Added new fileds NEW_TEMPL_STRESS_PART_NO, NEW_TEMPL_STRESS_PART_REV, NEW_STRESS_RATING_ID, STRESS_RATING_DESC.
*  ASSALK  070314  Call 140802, Modified okFindITEM1, okFindITEM2, okFindITEM3, okFindITEM4 and okFindITEM5.
*  ASSALK  070418  Call 142375, Modified okFind(), okFindITEM5(), preDefine() and okFindMaint().
*  ARWILK  070514  Call 143876, Added new RMB "Suggest Operational Values".
*  NAMELK  070604  Call 145240, Modified adjust() and preDefine().
*  ASSALK  070613  Call 145344, Modified adjust().
*  ASSALK  070620  Call 146180, Modified getContents().
*  ILSOLK  070730  Eliminated LocalizationErrors.
*  ASSALK  080514  bug 72214, winglet merge.
*  BUNILK  080623  Bug 66317, Added "Terminate Inspections' functionality.
*  BUNILK  091030  Bug 84513, Altered LOV related codes of Manufacturer and Manufacture's Part No fields.
* ----------------------------------------------------------------------------
*/

package ifs.pcmw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;

public class MaintTask extends ASPPageProvider
{
   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.pcmw.MaintTask");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPContext ctx;
   private ASPHTMLFormatter fmt;

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;

   private ASPField f;

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

   private ASPBlock itemblk4;
   private ASPRowSet itemset4;
   private ASPCommandBar itembar4;
   private ASPTable itemtbl4;
   private ASPBlockLayout itemlay4;

   // (+) WINGS (Start)
   private ASPBlock itemblk5;
   private ASPRowSet itemset5;
   private ASPCommandBar itembar5;
   private ASPTable itemtbl5;
   private ASPBlockLayout itemlay5;
   // (+) WINGS (Finish)

   private ASPBlock blkOne;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================

   private ASPTransactionBuffer trans;
   private String callingUrl;
   private ASPBuffer temp;
   //private ASPTransactionBuffer secBuff;
   private ASPCommand cmd;
   private ASPBuffer data;
   private int headrowno;
   private ASPBuffer row;
   private ASPBuffer retBuffer;
   private String ret_wo_no;
   private String sWoNo;
   private String sItem2WoNo;
   private String sItem3WoNo;
   private String sObjectID;
   private ASPQuery q;
   private String sFaultMode;
   private String sFunctionNo;
   private String sSubFunctionNo;
   private String sBottomFunctionNo;
   private String sFaultCode;
   private String sTaskType;
   //vagu
   private int activetab;
   private int itemno;
   private ASPTabContainer tabs;
   private String actualCompletion;
   //vagu
   // 030909  ARWILK  Begin (IID GEAM223N Install Part WEB)
   private String sIsLocal;
   private boolean bStrWizPageExists;
   private boolean bOpenStrWizPage;
   private boolean bReportFaultPage;
   private String sWindowPath;
   // 030909  ARWILK  End (IID GEAM223N Install Part WEB)
   private boolean bCancelPage;
   private int headSetNo;
   private String sIsConnected;
   private String sWinHandleName;
   private boolean bOpenNewWindow;

   //===============================================================
   // Construction 
   //===============================================================
   public MaintTask(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   public void run() 
   {
      sWoNo =  "";
      sItem2WoNo = "";
      sItem3WoNo = "";

      ASPManager mgr = getASPManager();

      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      fmt = mgr.newASPHTMLFormatter();

      sWoNo = ctx.readValue("SWONO","");
      sItem2WoNo = ctx.readValue("SWONO","");
      sItem3WoNo = ctx.readValue("SWONO","");
      sFaultMode = ctx.readValue("ITEM2_FAULT_MODE","");
      sFunctionNo = ctx.readValue("ITEM2_FUNCTION_NO","");
      sSubFunctionNo = ctx.readValue("ITEM2_SUB_FUNCTION_NO","");
      sBottomFunctionNo = ctx.readValue("ITEM2_BOTTOM_FUNCTION_NO","");
      sFaultCode = ctx.readValue("ITEM2_FAULT_CODE","");
      activetab = Integer.parseInt(ctx.readValue("ACTIVETAB","0"));
      actualCompletion = ctx.readValue("ACTUALCOMPLETION",actualCompletion);

      // 030909  ARWILK  Begin (IID GEAM223N Install Part WEB)
      sIsLocal = ctx.readValue("S_IS_LOCAL","");
      bStrWizPageExists = ctx.readFlag("S_STR_WIZ_PAGE_EXISTS",false);
      sWoNo = ctx.readValue("WO_NO");
      // 030909  ARWILK  End (IID GEAM223N Install Part WEB)

      callingUrl = ctx.getGlobal("CALLING_URL_WO","../pcmw/ActiveSeparate2.page");

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (mgr.dataTransfered())
      {
         sWoNo = mgr.readValue("WO_NO");
         activetab = Integer.parseInt(ctx.findGlobal("CURRTAB","0"));
         okFind();
         getDefaultValues();
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
         okFind();
         // 030909  ARWILK  Begin (IID GEAM223N Install Part WEB)
         getDefaultValues();
         // 030909  ARWILK  End (IID GEAM223N Install Part WEB)
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("TASK_NO")))
      {
         okFind();
      }
      else if ( "TRUE".equals(mgr.readValue("REFRESH_FLAG")) )
      {
         String headNo = ctx.getGlobal("HEADGLOBAL");
         headSetNo = Integer.parseInt(headNo);
         okFindMaint();     
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("DOCANCEL")))
      {
         okFind();  
      }
      else if (!mgr.isEmpty(mgr.readValue("DEFAULTDATE")))
      {
         setDefaultOperParams();
      }


      tabs.saveActiveTab();


      adjust();

      ctx.writeValue("ACTIVETAB",String.valueOf(activetab));
      ctx.writeValue("ITEM2_FAULT_MODE",sFaultMode);
      ctx.writeValue("ITEM2_FUNCTION_NO",sFunctionNo);
      ctx.writeValue("ITEM2_SUB_FUNCTION_NO",sSubFunctionNo);
      ctx.writeValue("ITEM2_BOTTOM_FUNCTION_NO",sBottomFunctionNo);
      ctx.writeValue("ITEM2_FAULT_CODE",sFaultCode);
      ctx.writeValue("ACTUALCOMPLETION",actualCompletion);
      // 030909  ARWILK  Begin (IID GEAM223N Install Part WEB)
      ctx.writeValue("S_IS_LOCAL",sIsLocal);
      ctx.writeFlag("S_STR_WIZ_PAGE_EXISTS",bStrWizPageExists);
      ctx.writeValue("WO_NO",sWoNo);
      // 030909  ARWILK  End (IID GEAM223N Install Part WEB)
   }

   public void saveReturn()
   {
      ASPManager mgr = getASPManager();

      itemset2.changeRow();

      temp = itemset2.getRow();

      temp.setValue("ITEM2_FAULT_MODE",sFaultMode);
      temp.setValue("ITEM2_FUNCTION_NO",sFunctionNo);
      temp.setValue("ITEM2_SUB_FUNCTION_NO",sSubFunctionNo);
      temp.setValue("ITEM2_BOTTOM_FUNCTION_NO",sBottomFunctionNo);
      temp.setValue("ITEM2_FAULT_CODE",sFaultCode);
      itemset2.setRow(temp);

      int currrow = itemset2.getCurrentRowNo();
      mgr.submit(trans);
      itemset2.goTo(currrow);
      itemlay2.setLayoutMode(itemlay2.getHistoryMode());    
   }

   public void saveReturnITEM5()
   {
      ASPManager mgr = getASPManager();
      itemset5.changeRow();
      int currrow = itemset5.getCurrentRowNo();
      displayWarning(mgr.submit(trans),"ITEM5", "INFO__");
      itemset5.goTo(currrow);
      itemset5.refreshRow();
   }

   public void saveITEM4()
   {
      ASPManager mgr = getASPManager();
      String attr = "";
      int rows = itemset4.countRows();

      itemset4.first();
      itemset4.changeRows();

      for (int i = 0; i < rows; i++)
      {
         if ("Modify__".equals(itemset4.getRowStatus()))
         {
            attr =  "VALUE_OH" + (char)31 + itemset4.getValue("VALUE_OH") + (char)30;
            attr +=  "VALUE_TOTAL" + (char)31 + itemset4.getValue("VALUE_TOTAL") + (char)30;

            trans.clear();
            cmd = trans.addCustomCommand("ITEM4","WORK_ORDER_FROM_VIM_PARAM_API.Modify__");
            cmd.addParameter("INFO");
            cmd.addParameter("OBJID", itemset4.getRow().getValue("OBJID"));
            cmd.addParameter("OBJVERSION", itemset4.getRow().getValue("OBJVERSION"));
            cmd.setParameter("ATTR", attr);
            cmd.setParameter("ACTION", "DO");

            trans = mgr.perform(trans);

            displayWarning(trans,"ITEM4", "INFO");
            attr = "";
         }
         itemset4.next();
      }

      itemset4.first();
      okFindITEM4();
   }

   private void displayWarning(ASPBuffer buf, String bufName, String fieldName)
   {
      ASPManager mgr = getASPManager();

      String info__ = null;
      int bufCount = buf.getBuffer(bufName).countItems();

      for (int i=0; i<bufCount; i++)
      {
         String tempInfo__ = buf.getBuffer(bufName).getBufferAt(i).getValue(fieldName);
         info__ = (tempInfo__ != null) ? info__ + tempInfo__ : info__;
      }

      if (info__ != null)
      {
         String warnings[] = info__.split(""+(char)30);

         String warning = new String();

         for (int i=0;i<warnings.length;i++)
         {
            if (warnings[i].indexOf("WARNING") != -1)
               warning = warning + warnings[i].substring(warnings[i].indexOf((char)31)) + "\n";
         }

         if (warning.trim().length() > 0 )
            mgr.showAlert(warning);
      }
   }

   public void cancelEdit()
   {
      ASPManager mgr = getASPManager();

      itemlay2.setLayoutMode(itemlay2.getHistoryMode());
      itemset2.resetRow();

      ASPBuffer temp = itemset2.getRow();
      temp.setValue("ITEM2_FAULT_MODE","");
      temp.setValue("ITEM2_FUNCTION_NO","");
      temp.setValue("ITEM2_SUB_FUNCTION_NO","");
      temp.setValue("ITEM2_BOTTOM_FUNCTION_NO","");
      temp.setValue("ITEM2_FAULT_CODE","");
      itemset2.setRow(temp);
   }

   public void cancel()
   {
      ASPManager mgr = getASPManager();
      int currrow = headset.getCurrentRowNo();
      headset.goTo(currrow);
      //Bug 46394
      if (mgr.isPresentationObjectInstalled("VIMW/CancelCause.page"))
      {
         trans.clear();
         cmd = trans.addCustomFunction("WOVIMCANCEL","Work_Order_From_Vim_API.Is_Vim_Work_Order","ISVIMCAN"); 
         cmd.addParameter("WO_NO",headset.getValue("WO_NO"));

         trans = mgr.perform(trans);

         String wovimcan = trans.getValue("WOVIMCANCEL/DATA/ISVIMCAN");

         ctx.setGlobal("HEADGLOBAL", headset.getRow().getValue("WO_NO"));
         if ("TRUE".equals(wovimcan))
         {

            bCancelPage = true;

            sWindowPath = "EnterCancelCauseDlg.page?WO_NO="+mgr.URLEncode(headset.getRow().getValue("WO_NO")); 
         }
      }
   }


   public void reportFault()
   {
      ASPManager mgr = getASPManager();

      bReportFaultPage = true;

      sWindowPath = "../vimw/SerialFaultQuickReport.page?PART_NO=" + mgr.URLEncode(mgr.isEmpty(itemset1.getRow().getValue("PART_NO"))?"":itemset1.getRow().getValue("PART_NO")) + 
                    "&SERIAL_NO=" + mgr.URLEncode(mgr.isEmpty(itemset1.getRow().getValue("SERIAL_NO"))?"":itemset1.getRow().getValue("SERIAL_NO")); 
   }

   public void resendWorkOrderNo()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      //Bug 46394, start
      ASPTransactionBuffer secBuff;
      boolean securityOk = false;
      secBuff = mgr.newASPTransactionBuffer();
      secBuff.addSecurityQuery("Work_Order_From_Vim_API");
      secBuff = mgr.perform(secBuff);

      if ( secBuff.getSecurityInfo().itemExists("Work_Order_From_Vim_API") )
         securityOk = true;
      if (securityOk)
      {
         cmd = trans.addCustomCommand("WOVIMRESENDWO","Work_Order_From_Vim_API.Resend_Wo_No"); 
         cmd.addParameter("WO_NO",headset.getValue("WO_NO"));
         trans = mgr.perform(trans);
      }
   }

   // 030909  ARWILK  Begin (IID GEAM223N Install Part WEB)


   public void performStuctChange()
   {
      ASPManager mgr = getASPManager();

      bOpenStrWizPage = true;
      ctx.setGlobal("CALLING_URL", mgr.getURL());

      sWindowPath = "../vimw/VimSerialStrChangeWiz.page?INI_DISPLAY_FIRST_PAGE=TRUE" +
                    "&INI_STR_CHANGE_TYPE=" +
                    "&INI_SEQ_NO=" +
                    "&INI_PARENT_SEQ_NO=" +
                    "&INI_PART_NO_TOP_OUT=" + mgr.URLEncode(mgr.isEmpty(itemset1.getRow().getValue("PART_NO"))?"":itemset1.getRow().getValue("PART_NO")) + 
                    "&INI_SERIAL_NO_TOP_OUT=" + mgr.URLEncode(mgr.isEmpty(itemset1.getRow().getValue("SERIAL_NO"))?"":itemset1.getRow().getValue("SERIAL_NO")) +
                    "&INI_TOP_PART_NO_OUT=" + 
                    "&INI_TOP_PART_REV_OUT=" + 
                    "&INI_STRUCTURE_ADDRESS_OUT=" + 
                    "&INI_PART_NO_OUT=" + 
                    "&INI_PART_REV_OUT=" + 
                    "&INI_SERIAL_NO_OUT=" + 
                    "&INI_PART_NO_TOP_IN=" + mgr.URLEncode(mgr.isEmpty(itemset1.getRow().getValue("PART_NO"))?"":itemset1.getRow().getValue("PART_NO")) +
                    "&INI_SERIAL_NO_TOP_IN=" + mgr.URLEncode(mgr.isEmpty(itemset1.getRow().getValue("SERIAL_NO"))?"":itemset1.getRow().getValue("SERIAL_NO")) +
                    "&INI_TOP_PART_NO_IN=" + 
                    "&INI_TOP_PART_REV_IN=" + 
                    "&INI_STRUCTURE_ADDRESS_IN=" + 
                    "&INI_PART_NO_IN=" + 
                    "&INI_PART_REV_IN=" + 
                    "&INI_SERIAL_NO_IN=" + 
                    "&INI_OPERATIONAL_CONDITION=" + 
                    "&INI_LOCATION_CODE=" + 
                    "&INI_INSTALLATION_DATE=" + 
                    "&INI_WORKSHOP_CODE=" + 
                    "&INI_REPLACE_DATE=" + 
                    "&INI_TASK_NO=" + mgr.URLEncode(mgr.isEmpty(itemset1.getRow().getValue("TASK_NO"))?"":headset.getRow().getValue("TASK_NO")) + 
                    "&INI_UNSCHEDULED_REMOVAL=" + 
                    "&INI_REMARK=" + 
                    "&INI_CONDITION_CODE=" + 
                    "&INI_CONDITION_REMARK=" + 
                    "&INI_ALLOW_MISMATCH=TRUE";
   }

   public void getDefaultValues()
   {
      ASPManager mgr = getASPManager();

      //secBuff.clear();

      ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer();

      secBuff.addPresentationObjectQuery("VIMW/VimSerialStrChangeWiz.page");
      secBuff.addSecurityQuery("Vim_Configuration_API","Get_Parameter_Value");
      secBuff.addSecurityQuery("Transfer_Work_Order_Util_API","Is_Local_Site");
      secBuff = mgr.perform(secBuff); 

      bStrWizPageExists = secBuff.getSecurityInfo().namedItemExists("VIMW/VimSerialStrChangeWiz.page");

      //bStrWizPageExists = mgr.isPresentationObjectInstalled("vimw/VimSerialStrChangeWiz.page");

      if (secBuff.getSecurityInfo().itemExists("Vim_Configuration_API.Get_Parameter_Value")
          && secBuff.getSecurityInfo().itemExists("Transfer_Work_Order_Util_API.Is_Local_Site"))
      {
         trans.clear();

         cmd = trans.addCustomCommand("WOVIMGETDEFVAL","Vim_Configuration_API.Get_Parameter_Value");
         cmd.addParameter("SCONTRACTVAL","");
         cmd.addParameter("OBJECT_LU","TransferWorkOrderUtil");
         cmd.addParameter("OBJECT_KEY","CONTRACT");
         cmd.addParameter("PROPERTY_NAME","VIM_DB_CONTRACT");

         trans = mgr.perform(trans);

         String sContVal = trans.getValue("WOVIMGETDEFVAL/DATA/SCONTRACTVAL");

         trans.clear();

         cmd = trans.addCustomFunction("WOVIMGETDEFVAL","Transfer_Work_Order_Util_API.Is_Local_Site","SISLOCAL");
         cmd.addParameter("SCONTRACTVAL",mgr.isEmpty(sContVal)?"":sContVal);

         trans = mgr.perform(trans);

         // Check the type of database whether distributed or local.

         sIsLocal = trans.getValue("WOVIMGETDEFVAL/DATA/SISLOCAL");
      }
   }

   // 030909  ARWILK  End (IID GEAM223N Install Part WEB)

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void validate()
   {
      ASPManager mgr = getASPManager();
      //Bug 46394, start
      String sFaultDesc ="";
      String sFuncNo = "";
      String sSubFuncNo = "";
      String sBotFuncNo = "";
      String sFaultCode = "";
      String strAttr;
      String templStressPartNo;
      String templStressPartRev;
      String stressRatingId;
      String stressRatingDesc;

      ASPTransactionBuffer secBuff;
      boolean security1Ok = false;
      boolean security2Ok = false;
      boolean security3Ok = false;
      secBuff = mgr.newASPTransactionBuffer();
      secBuff.addSecurityQuery("Fault_Mode_API");
      secBuff.addSecurityQuery("PRODUCT_FUNCTION_API");
      secBuff.addSecurityQuery("Fault_Code_API");
      secBuff = mgr.perform(secBuff);

      if ( secBuff.getSecurityInfo().itemExists("Fault_Mode_API") )
         security1Ok = true;

      if ( secBuff.getSecurityInfo().itemExists("PRODUCT_FUNCTION_API") )
         security2Ok = true;

      if ( secBuff.getSecurityInfo().itemExists("Fault_Code_API") )
         security3Ok = true;

      String val = mgr.readValue("VALIDATE");
      String txt;

      if ("ITEM2_FAULT_MODE".equals(val))
      {
         if ( security1Ok )
         {
            cmd = trans.addCustomFunction("ITEM2FAULTMOD", "Fault_Mode_API.Get_Description", "ITEM2_FAULT_MODE_DESC" );
            cmd.addParameter("ITEM2_PRODUCT_NO");
            cmd.addParameter("ITEM2_MODEL_NO");
            cmd.addParameter("ITEM2_FAULT_MODE");

            trans = mgr.validate(trans);

            sFaultDesc   = trans.getValue("ITEM2FAULTMOD/DATA/ITEM2_FAULT_MODE_DESC");
         }

         txt = (mgr.isEmpty(sFaultDesc)? "" : sFaultDesc) +"^";

         mgr.responseWrite(txt);
      }
      else if ("ITEM2_FUNCTION_NO".equals(val))
      {
         if ( security2Ok )
         {
            cmd = trans.addCustomFunction("ITEM2FUNCNO", "PRODUCT_FUNCTION_API.Get_Description", "ITEM2_FUNCTION_DESC" );
            cmd.addParameter("ITEM2_PRODUCT_NO");
            cmd.addParameter("ITEM2_MODEL_NO");
            cmd.addParameter("ITEM2_FUNCTION_NO");

            trans = mgr.validate(trans);

            sFuncNo   = trans.getValue("ITEM2FUNCNO/DATA/ITEM2_FUNCTION_DESC");
         }

         txt = (mgr.isEmpty(sFuncNo)? "" : sFuncNo) +"^";

         mgr.responseWrite(txt);
      }
      else if ("ITEM2_SUB_FUNCTION_NO".equals(val))
      {
         if ( security2Ok )
         {
            cmd = trans.addCustomFunction("ITEM2SUBFUNCNO", "PRODUCT_FUNCTION_API.Get_Description", "ITEM2_SUB_FUNCTION_DESC" );
            cmd.addParameter("ITEM2_PRODUCT_NO");
            cmd.addParameter("ITEM2_MODEL_NO");
            cmd.addParameter("ITEM2_SUB_FUNCTION_NO");

            trans = mgr.validate(trans);

            sSubFuncNo   = trans.getValue("ITEM2SUBFUNCNO/DATA/ITEM2_SUB_FUNCTION_DESC");
         }

         txt = (mgr.isEmpty(sSubFuncNo)? "" : sSubFuncNo) +"^";

         mgr.responseWrite(txt);
      }
      else if ("ITEM2_BOTTOM_FUNCTION_NO".equals(val))
      {
         if ( security2Ok )
         {
            cmd = trans.addCustomFunction("ITEM2BOTFUNCNO", "PRODUCT_FUNCTION_API.Get_Description", "ITEM2_BOTTOM_FUNCTION_DESC" );
            cmd.addParameter("ITEM2_PRODUCT_NO");
            cmd.addParameter("ITEM2_MODEL_NO");
            cmd.addParameter("ITEM2_BOTTOM_FUNCTION_NO");

            trans = mgr.validate(trans);

            sBotFuncNo   = trans.getValue("ITEM2BOTFUNCNO/DATA/ITEM2_BOTTOM_FUNCTION_DESC");
         }

         txt = (mgr.isEmpty(sBotFuncNo)? "" : sBotFuncNo) +"^";

         mgr.responseWrite(txt);
      }
      else if ("ITEM2_FAULT_CODE".equals(val))
      {
         if ( security3Ok )
         {
            cmd = trans.addCustomFunction("ITEM2FAULTCODE", "Fault_Code_API.Get_Fault_Code_Desc", "ITEM2_FAULT_CODE_DESC" );
            cmd.addParameter("ITEM2_FAULT_CODE");

            trans = mgr.validate(trans);

            sFaultCode   = trans.getValue("ITEM2FAULTCODE/DATA/ITEM2_FAULT_CODE_DESC");

         }

         txt = (mgr.isEmpty(sFaultCode)? "" : sFaultCode) +"^";

         mgr.responseWrite(txt);
      }
      else if ("ITEM5_NEW_MAINT_PROG_ID".equals(val))
      {
         cmd = trans.addCustomFunction("ITEM5ISCONNECTEDTOMAINTPRO1", "Maint_Program_Part_Rev_API.Part_Rev_Exist_On_Maint_Prog", "DUMMY" );
         cmd.addParameter("ITEM5_NEW_PART_NO",mgr.readValue("ITEM5_NEW_PART_NO"));
         cmd.addParameter("ITEM5_NEW_PART_REV",mgr.readValue("ITEM5_NEW_PART_REV"));

         trans = mgr.validate(trans);

         String sIsConnected1 = trans.getValue("ITEM5ISCONNECTEDTOMAINTPRO1/DATA/DUMMY");

         if ("TRUE".equalsIgnoreCase(sIsConnected1))
         {
            if (!mgr.isEmpty(mgr.readValue("ITEM5_NEW_MAINT_PROG_ID")) && "TRUE".equals(mgr.readValue("ITEM5_DISCONN_MAINT_PROG")))
            {
               txt = "FALSE^";                        
            }
            else
               txt = (mgr.isEmpty(mgr.readValue("ITEM5_DISCONN_MAINT_PROG"))? "FALSE" : mgr.readValue("ITEM5_DISCONN_MAINT_PROG")) +"^";
         }
         else
            txt = "TRUE^";  

         String maintProgId="",validationAttrAtr="",maintProgRev="",maintProgDesc="";
         if (mgr.readValue("ITEM5_NEW_MAINT_PROG_ID").indexOf("^") > -1)
         {
            strAttr = mgr.readValue("ITEM5_NEW_MAINT_PROG_ID");

            maintProgId = strAttr.substring(0,strAttr.indexOf("^"));       
            validationAttrAtr = strAttr.substring(strAttr.indexOf("^")+1,strAttr.length());
            maintProgRev = validationAttrAtr.substring(0,validationAttrAtr.indexOf("^"));       
            validationAttrAtr = validationAttrAtr.substring(validationAttrAtr.indexOf("^")+1,validationAttrAtr.length());
            maintProgDesc = validationAttrAtr.substring(0,validationAttrAtr.indexOf("^"));       
         }
         else
         {
            maintProgId = mgr.readValue("ITEM5_NEW_MAINT_PROG_ID");
            maintProgRev = mgr.readValue("ITEM5_NEW_MAINT_PROG_REV");
         }

         txt += (mgr.isEmpty(maintProgId) ? "" : (maintProgId)) + "^" + (mgr.isEmpty(maintProgRev) ? "" : (maintProgRev)) + "^" + (mgr.isEmpty(maintProgDesc) ? "" : (maintProgDesc)) + "^";

         mgr.responseWrite(txt);
      }
      else if ("ITEM5_NEW_MAINT_PROG_REV".equals(val))
      {
         cmd = trans.addCustomFunction("ITEM5ISCONNECTEDTOMAINTPRO2", "Maint_Program_Part_Rev_API.Part_Rev_Exist_On_Maint_Prog", "DUMMY" );
         cmd.addParameter("ITEM5_NEW_PART_NO");
         cmd.addParameter("ITEM5_NEW_PART_REV");

         trans = mgr.validate(trans);

         String sIsConnected2 = trans.getValue("ITEM5ISCONNECTEDTOMAINTPRO2/DATA/DUMMY");

         if ("TRUE".equalsIgnoreCase(sIsConnected2))
         {
            if (!mgr.isEmpty(mgr.readValue("ITEM5_NEW_MAINT_PROG_REV")) && "TRUE".equals(mgr.readValue("ITEM5_DISCONN_MAINT_PROG")))
            {
               txt = "FALSE^";                        
            }
            else
               txt = (mgr.isEmpty(mgr.readValue("ITEM5_DISCONN_MAINT_PROG"))? "FALSE" : mgr.readValue("ITEM5_DISCONN_MAINT_PROG")) +"^";
         }
         else
            txt = "TRUE^";

         String maintProgId="",validationAttrAtr="",maintProgRev="",maintProgDesc="";
         if (mgr.readValue("ITEM5_NEW_MAINT_PROG_REV").indexOf("^") > -1)
         {
            strAttr = mgr.readValue("ITEM5_NEW_MAINT_PROG_REV");

            maintProgId = strAttr.substring(0,strAttr.indexOf("^"));       
            validationAttrAtr = strAttr.substring(strAttr.indexOf("^")+1,strAttr.length());
            maintProgRev = validationAttrAtr.substring(0,validationAttrAtr.indexOf("^"));       
            validationAttrAtr = validationAttrAtr.substring(validationAttrAtr.indexOf("^")+1,validationAttrAtr.length());
            maintProgDesc = validationAttrAtr.substring(0,validationAttrAtr.indexOf("^"));       
         }
         else
         {
            maintProgId = mgr.readValue("ITEM5_NEW_MAINT_PROG_ID");
            maintProgRev = mgr.readValue("ITEM5_NEW_MAINT_PROG_REV");
         }

         txt += (mgr.isEmpty(maintProgId) ? "" : (maintProgId)) + "^" + (mgr.isEmpty(maintProgRev) ? "" : (maintProgRev)) + "^" + (mgr.isEmpty(maintProgDesc) ? "" : (maintProgDesc)) + "^";

         mgr.responseWrite(txt);
      }
      else if ("ITEM5_NEW_MAINT_GROUP".equals(val))
      {
         cmd = trans.addCustomFunction("ITEM5MAINTGROUP", "Maint_Group_API.Get_Maint_Group_Desc", "ITEM5_MAINT_GRP_DESC" );
         cmd.addParameter("ITEM5_NEW_PART_NO");
         cmd.addParameter("ITEM5_NEW_MAINT_GROUP");

         trans = mgr.validate(trans);

         String sMaintGroupDesc   = trans.getValue("ITEM5MAINTGROUP/DATA/ITEM5_MAINT_GRP_DESC");

         txt = (mgr.isEmpty(sMaintGroupDesc)? "" : sMaintGroupDesc) +"^";

         mgr.responseWrite(txt);
      }
      else if ("ITEM5_NEW_MANUFACTURER_NO".equals(val))
      {
         cmd = trans.addCustomFunction("ITEM5MANUFACNAME", "Manufacturer_Info_API.Get_Name", "ITEM5_MANUFACTURER_NAME" );
         cmd.addParameter("ITEM5_NEW_MANUFACTURER_NO");

         trans = mgr.validate(trans);

         String sManuName   = trans.getValue("ITEM5MANUFACNAME/DATA/ITEM5_MANUFACTURER_NAME");

         txt = (mgr.isEmpty(sManuName)? "" : sManuName) +"^";

         mgr.responseWrite(txt);
      }
      // (+) WINGS (Finish)
      else if ("NEW_TEMPL_STRESS_PART_NO".equals(val))
      {
         strAttr = mgr.readValue("NEW_TEMPL_STRESS_PART_NO");

         templStressPartNo = strAttr.substring(0,strAttr.indexOf("^"));
         strAttr = strAttr.substring(strAttr.indexOf("^")+1,strAttr.length());

         templStressPartRev = strAttr.substring(0,strAttr.indexOf("^"));
         strAttr = strAttr.substring(strAttr.indexOf("^")+1,strAttr.length());

         stressRatingId = strAttr.substring(0,strAttr.indexOf("^"));
         strAttr = strAttr.substring(strAttr.indexOf("^")+1,strAttr.length());

         //Getting description from the converted LOV string is wrong and will return the description in uppercase.
         //stressRatingDesc = strAttr.substring(0,strAttr.length());
         trans.clear();
         cmd = trans.addCustomFunction("GETITEM5STRESSDESC", "Stress_Rating_Per_Part_API.Get_Stress_Rating_Description", "STRESS_RATING_DESC" );
         cmd.addParameter("NEW_TEMPL_STRESS_PART_NO", templStressPartNo);
         cmd.addParameter("NEW_TEMPL_STRESS_PART_REV",templStressPartRev);
         cmd.addParameter("NEW_STRESS_RATING_ID", stressRatingId);
         trans = mgr.validate(trans);
         stressRatingDesc = trans.getValue("GETITEM5STRESSDESC/DATA/STRESS_RATING_DESC");

         txt = (mgr.isEmpty(templStressPartNo) ? "" : (templStressPartNo)) + "^" + 
               (mgr.isEmpty(templStressPartRev) ? "" : (templStressPartRev)) + "^" + 
               (mgr.isEmpty(stressRatingId) ? "" : (stressRatingId)) + "^" + 
               (mgr.isEmpty(stressRatingDesc) ? "" : (stressRatingDesc)) + "^";

         mgr.responseWrite(txt);
      }
      else if ("NEW_TEMPL_STRESS_PART_REV".equals(val))
      {
         strAttr = mgr.readValue("NEW_TEMPL_STRESS_PART_REV");

         templStressPartNo = strAttr.substring(0,strAttr.indexOf("^"));
         strAttr = strAttr.substring(strAttr.indexOf("^")+1,strAttr.length());

         templStressPartRev = strAttr.substring(0,strAttr.indexOf("^"));
         strAttr = strAttr.substring(strAttr.indexOf("^")+1,strAttr.length());

         stressRatingId = strAttr.substring(0,strAttr.indexOf("^"));
         strAttr = strAttr.substring(strAttr.indexOf("^")+1,strAttr.length());

         //Getting description from the converted LOV string is wrong and will return the description in uppercase.
         //stressRatingDesc = strAttr.substring(0,strAttr.length());
         trans.clear();
         cmd = trans.addCustomFunction("GETITEM5STRESSDESC", "Stress_Rating_Per_Part_API.Get_Stress_Rating_Description", "STRESS_RATING_DESC" );
         cmd.addParameter("NEW_TEMPL_STRESS_PART_NO", templStressPartNo);
         cmd.addParameter("NEW_TEMPL_STRESS_PART_REV",templStressPartRev);
         cmd.addParameter("NEW_STRESS_RATING_ID", stressRatingId);
         trans = mgr.validate(trans);
         stressRatingDesc = trans.getValue("GETITEM5STRESSDESC/DATA/STRESS_RATING_DESC");

         txt = (mgr.isEmpty(templStressPartNo) ? "" : (templStressPartNo)) + "^" + 
               (mgr.isEmpty(templStressPartRev) ? "" : (templStressPartRev)) + "^" + 
               (mgr.isEmpty(stressRatingId) ? "" : (stressRatingId)) + "^" + 
               (mgr.isEmpty(stressRatingDesc) ? "" : (stressRatingDesc)) + "^";

         mgr.responseWrite(txt);
      }
      else if ("NEW_STRESS_RATING_ID".equals(val))
      {
         strAttr = mgr.readValue("NEW_STRESS_RATING_ID");

         templStressPartNo = strAttr.substring(0,strAttr.indexOf("^"));
         strAttr = strAttr.substring(strAttr.indexOf("^")+1,strAttr.length());

         templStressPartRev = strAttr.substring(0,strAttr.indexOf("^"));
         strAttr = strAttr.substring(strAttr.indexOf("^")+1,strAttr.length());

         stressRatingId = strAttr.substring(0,strAttr.indexOf("^"));
         strAttr = strAttr.substring(strAttr.indexOf("^")+1,strAttr.length());

         //Getting description from the converted LOV string is wrong and will return the description in uppercase.
         //stressRatingDesc = strAttr.substring(0,strAttr.length());
         trans.clear();
         cmd = trans.addCustomFunction("GETITEM5STRESSDESC", "Stress_Rating_Per_Part_API.Get_Stress_Rating_Description", "STRESS_RATING_DESC" );
         cmd.addParameter("NEW_TEMPL_STRESS_PART_NO", templStressPartNo);
         cmd.addParameter("NEW_TEMPL_STRESS_PART_REV",templStressPartRev);
         cmd.addParameter("NEW_STRESS_RATING_ID", stressRatingId);
         trans = mgr.validate(trans);
         stressRatingDesc = trans.getValue("GETITEM5STRESSDESC/DATA/STRESS_RATING_DESC");

         txt = (mgr.isEmpty(templStressPartNo) ? "" : (templStressPartNo)) + "^" + 
               (mgr.isEmpty(templStressPartRev) ? "" : (templStressPartRev)) + "^" + 
               (mgr.isEmpty(stressRatingId) ? "" : (stressRatingId)) + "^" + 
               (mgr.isEmpty(stressRatingDesc) ? "" : (stressRatingDesc)) + "^";

         mgr.responseWrite(txt);
      }
      //Bug 84513 Start
      else if ("ITEM5_PART_REV".equals(val))
      {
         trans.clear();
         
         cmd = trans.addCustomFunction("PARTREVEXIST","part_manu_part_rev_api.check_part_rev_exist","PART_REV_EXIST");
         cmd.addParameter("ITEM5_PART_NO");
         cmd.addParameter("ITEM5_PART_REV");

         trans = mgr.validate(trans);
         
         String isPartRevExist = trans.getValue("PARTREVEXIST/DATA/PART_REV_EXIST");

         txt =  (mgr.isEmpty(isPartRevExist) ? "" : (isPartRevExist))+"^";

         mgr.responseWrite(txt);
      }
      //Bug 84513 End

      mgr.endResponse();
   }

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void okFindMaint()
   {
      trans.clear();
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      //Bug 58216 Start
      q.addWhereCondition("WO_NO = ?");
      q.addParameter("WO_NO",Integer.toString(headSetNo));
      //Bug 58216 End
      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);

      if (headset.countRows() == 0)
      {
         headset.clear();
      }

      if (headset.countRows() == 1)
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
      }

      if (headset.countRows() > 0 )
      {
         sTaskType = headset.getRow().getValue("MAINT_TASK_TYPE_DB");
         okFindITEM1();
         if ("3".equals(sTaskType)) // for Serial Repair
         {
            okFindITEM2();
         }
         // (+) WINGS (Start)
         if ("4".equals(sTaskType)) // for Modification
         {
            okFindITEM3();
            okFindITEM5();
         }
         // (+) WINGS (Final)
         okFindITEM4();

      }
   }

   public void okFind()
   {

      ASPManager mgr = getASPManager();
      //(+) Bug 66317 Start
      trans.clear();
      //(+) Bug 66317 End

      q = trans.addQuery(headblk);
      if (mgr.dataTransfered())
         q.addOrCondition(mgr.getTransferedData());

      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);
      eval(headset.syncItemSets());

      if (headset.countRows() == 1)
      {
         headlay.setLayoutMode(headlay.SINGLE_LAYOUT);
      }

      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("PCMWMAINTTASKNODATA: No data found."));
         headset.clear();
      }

      //(-/+) Bug 66317 Start
      if (headset.countRows() > 0 )
      {
         sTaskType = headset.getRow().getValue("MAINT_TASK_TYPE_DB");
         okFindITEM1();
         if ("3".equals(sTaskType)) // for Serial Repair
         {
            okFindITEM2();
         }
         // (+) WINGS (Start)
         else if ("4".equals(sTaskType)) // for Modification
         {
            okFindITEM3();
            okFindITEM5();
         }
         // (+) WINGS (Finish)
         okFindITEM4();
      }
       //(-/+) Bug 66317 End  
   }

   public void okFindITEM1()
   {
      trans.clear();
      ASPManager mgr = getASPManager();
      //(+) Bug 66317 Start
      trans.clear();
      //(+) Bug 66317 End
      int currrow = headset.getCurrentRowNo();

      q = trans.addQuery(itemblk1);
      q.addWhereCondition("WO_NO = ?  AND TASK_NO = ?");
      q.addParameter("WO_NO", headset.getValue("WO_NO"));
      q.addParameter("TASK_NO",headset.getValue("TASK_NO"));
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk1);


      if (itemset1.countRows() == 0)
      {
         itemset1.clear();
      }
      headset.goTo(currrow);
   }



   public void okFindITEM2()
   {
      trans.clear();
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk2);
      q.addWhereCondition("WO_NO = ?  AND TASK_NO = ?");
      q.addParameter("WO_NO", headset.getValue("WO_NO"));
      q.addParameter("TASK_NO",headset.getValue("TASK_NO"));
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk2);


      if (itemset2.countRows() == 0)
      {
         itemset2.clear();
      }
   }

   public void okFindITEM3()
   {
      ASPManager mgr = getASPManager();
      trans.clear();

      q = trans.addQuery(itemblk3);
      q.addWhereCondition("WO_NO = ?  AND TASK_NO = ?");
      q.addParameter("WO_NO", headset.getValue("WO_NO"));
      q.addParameter("TASK_NO",headset.getValue("TASK_NO"));
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk3);

      if (itemset3.countRows() == 0)
      {
         itemset3.clear();
      }
   }

   public void okFindITEM4()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      int currrow = headset.getCurrentRowNo();

      q = trans.addQuery(itemblk4);

      q.addWhereCondition("WO_NO = ?  AND TASK_NO = ?");
      q.addParameter("WO_NO", headset.getValue("WO_NO"));
      q.addParameter("TASK_NO",headset.getValue("TASK_NO"));

      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk4);

      if (itemset4.countRows() == 0)
         itemset4.clear();

      headset.goTo(currrow);
   }

   // (+) WINGS (Start)
   public void okFindITEM5()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();
      ASPCommand cmd = mgr.newASPCommand();

      trans.clear();
      int currrow = headset.getCurrentRowNo();

      q = trans.addQuery(itemblk5);

      q.addWhereCondition("WO_NO = ?  AND TASK_NO = ?");
      q.addParameter("WO_NO", headset.getValue("WO_NO"));
      q.addParameter("TASK_NO",headset.getValue("TASK_NO"));     q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk5);

      if (itemset5.countRows() == 0)
      {
         itemset5.clear();
      }
      else
      {
         int numRows = itemset5.countRows();
         ASPBuffer temp;

         for (int i=0;i<numRows;i++)
         {
            itemset5.goTo(i);
            cmd = trans1.addCustomCommand("TEMPINFO"+i,"Serial_Structure_Template_API.Get_Template_Info");
            cmd.addParameter("ITEM5_TEMPL_PART_NO");
            cmd.addParameter("ITEM5_TEMPL_PART_REV");
            cmd.addParameter("ITEM5_STRCT_ADDRESS");
            cmd.addParameter("ITEM5_NEW_PART_NO",itemset5.getValue("NEW_PART_NO"));
            cmd.addParameter("ITEM5_NEW_PART_REV",itemset5.getValue("NEW_PART_REV"));
         }
         trans1 = mgr.perform(trans1);
         for (int i=0;i<numRows;i++)
         {
            itemset5.goTo(i);
            temp = itemset5.getRow();                        
            temp.setValue("ITEM5_TEMPL_PART_NO",trans1.getValue("TEMPINFO"+i+"/DATA/ITEM5_TEMPL_PART_NO"));
            temp.setValue("ITEM5_TEMPL_PART_REV",trans1.getValue("TEMPINFO"+i+"/DATA/ITEM5_TEMPL_PART_REV"));
            temp.setValue("ITEM5_STRCT_ADDRESS",trans1.getValue("TEMPINFO"+i+"/DATA/ITEM5_STRCT_ADDRESS"));
            itemset5.setRow(temp);
         }

      }
      headset.goTo(currrow);
   }
   // (+) WINGS (Finish)

   public void countFind()
   {
      ASPManager mgr = getASPManager();
      //(+) Bug 66317 Start
      trans.clear();
      //(+) Bug 66317 End
      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.querySubmit(trans,headblk);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }

   //Bug 46394, start
   public boolean isModuleInst(String module_)
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans1 = mgr.newASPTransactionBuffer();
      ASPCommand cmd = mgr.newASPCommand();
      String modVersion;

      cmd = trans1.addCustomFunction("MODVERS", "Module_API.Get_Version", "MODULENAME");
      cmd.addParameter("MODULENAME",module_);

      trans1 = mgr.performConfig(trans1);
      modVersion = trans1.getValue("MODVERS/DATA/MODULENAME");

      if (!mgr.isEmpty(modVersion))
         return true;
      else
         return false;
   }

   public void getDefaultOperParams()
   {
      ASPManager mgr = getASPManager();

      actualCompletion = headset.getRow().getFieldValue("ACTUALCOMPLETION");

      if (mgr.isEmpty(actualCompletion))
      {
         trans.clear();   
         trans.addQuery("SYSDT","DUAL","SYSDATE DUMMY_SYSDATE","","");
         trans = mgr.perform(trans);

         actualCompletion = trans.getBuffer("SYSDT/DATA").getFieldValue("DUMMY_SYSDATE");
      }

      sWindowPath = "../vimw/SerialMaintDateDlg.page?";
      sWindowPath += "REQUEST_FINISH_DATE=" + mgr.URLEncode(actualCompletion);

      bOpenNewWindow = true; 
      sWinHandleName = "MaintTaskToSerialMaintDateDlg";
   }

   public void setDefaultOperParams()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer data = null;
      String sDefaultDate = mgr.readValue("DEFAULTDATE");


      int rows = itemset4.countRows();
      itemset4.store();
      itemset4.first();

      trans.clear();

      for (int i = 0; i < rows; i++)
      {
         ASPCommand cmd = trans.addCustomCommand("MTASKDFLT" + i, "WORK_ORDER_FROM_VIM_PARAM_API.Get_Defaults__");
         cmd.addParameter("VALUE_OH");
         cmd.addParameter("VALUE_TOTAL");
         cmd.addParameter("ITEM4_WO_NO", itemset4.getRow().getFieldValue("ITEM4_WO_NO"));
         cmd.addParameter("ITEM4_TASK_NO", itemset4.getRow().getFieldValue("ITEM4_TASK_NO"));
         cmd.addParameter("OPER_PARAM", itemset4.getRow().getFieldValue("OPER_PARAM"));
         cmd.addParameter("ITEM4_ACTUAL_COMP_DT", sDefaultDate);

         itemset4.next();
      }

      trans = mgr.perform(trans);

      itemset4.first();

      for (int i = 0; i < rows; i++)
      {
         ASPBuffer itemInfo = trans.getBuffer("MTASKDFLT" + i + "/DATA");

         String valueOh =  itemInfo.getValueAt(0);
         String valueTotal =  itemInfo.getValueAt(1);

         data = itemset4.getRow();
         data.setFieldItem("VALUE_OH", valueOh);
         data.setFieldItem("VALUE_TOTAL", valueTotal);

         itemset4.setRow(data);
         itemset4.next();
      }

      itemset4.first();
   }

   public void preDefine()
   {
      ASPManager mgr = getASPManager();
      blkOne = mgr.newASPBlock("BLKONE");

      //Bug 46394, start
      boolean vimInstalled = false;

      blkOne.addField("MODULENAME")
      .setHidden()
      .setFunction("''");

      if ( isModuleInst("VIM") )
         vimInstalled = true;
      else
         vimInstalled = false;
      //Bug 46394, end

      if (vimInstalled)
      {
         headblk = mgr.newASPBlock("HEAD");

         headblk.addField("OBJID").
         setHidden();

         headblk.addField("OBJVERSION").
         setHidden();

         f = headblk.addField("WO_NO","Number","#");
         f.setLabel("PCMWMAINTTASKWONO: Work Order Number");
         f.setSize(20);
         f.setReadOnly();

         f = headblk.addField("TASK_NO");
         f.setSize(20);
         f.setMaxLength(20);
         f.setLabel("PCMWMAINTTASKTASKNO: Task No");
         f.setUpperCase();
         f.setReadOnly();

         f = headblk.addField("ACTUALCOMPLETION","Datetime");
         f.setFunction("ACTIVE_SEPARATE_API.Get_Real_F_Date(WO_NO)");
         f.setSize(20);
         f.setLabel("PCMWMAINTTASKACTUALCOMPLETION: Actual Completion");
         f.setMaxLength(20);
         f.setReadOnly();

         f = headblk.addField("TASK_CODE");
         f.setLabel("PCMWMAINTTASKTASKCODE: Task Code");
         f.setInsertable();
         f.setSize(25);  
         f.setMaxLength(40);
         f.setUpperCase();
         f.setReadOnly();

         f = headblk.addField("TASK_DESC");
         f.setLabel("PCMWMAINTTASKTASKDESC: Task Desc");
         f.setInsertable();
         f.setSize(25);  
         f.setMaxLength(40);
         f.setUpperCase();
         f.setReadOnly();

         // (+) WINGS (Start)
         f = headblk.addField("MOD_EXECUTION_TYPE_DB");
         f.setHidden();
         // (+) WINGS (Finish)

         //(+) Bug 66317 Start
         // Hidden. Used to check whether to display fault/modification blocks
         f = headblk.addField("HEAD_TASK_TYPE");
         f.setFunction("MAINT_TASK_API.Get_Maint_Task_Type_Db(:TASK_NO)");
         f.setHidden();
         //(+) Bug 66317 End

         f = headblk.addField("MAINT_TASK_TYPE_DB");
         f.setHidden();

         headblk.setView("WORK_ORDER_FROM_VIM");
         headset = headblk.getASPRowSet();
         headtbl = mgr.newASPTable(headblk);
         headtbl.setTitle(mgr.translate("PCMWMAINTTASKHD: Maint Task"));
         headtbl.setWrap();

         headbar = mgr.newASPCommandBar(headblk);
         headbar.disableCommand(headbar.DUPLICATEROW);
         headbar.disableCommand(headbar.NEWROW);
         headbar.disableCommand(headbar.DELETE);
         headbar.disableCommand(headbar.BACK);

         // Putting up Cancel and Resend work order no
         headbar.addCustomCommand("reportFault", mgr.translate("PCMWMAINTTASKREPORTFAULT: Report Fault..."));
         headbar.addCustomCommandSeparator();

         headbar.addCustomCommand("cancel", mgr.translate("PCMWMAINTTASKCANCELNEW: Cancel..."));
         headbar.addCustomCommand("resendWorkOrderNo", mgr.translate("PCMWMAINTTASKRESENDWONO: Resend Work Order Number"));
         headbar.addCustomCommand("performStuctChange", mgr.translate("PCMWMAINTTASKPERFSTRCHG: Perform Structure Change..."));

         headlay = headblk.getASPBlockLayout();

         headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
         headlay.setDialogColumns(2);


         //Web Alignment - Adding Tabs
         tabs = mgr.newASPTabContainer();
         tabs.setTabSpace(5);  
         tabs.setTabWidth(100);

         tabs.addTab("General","javascript:commandSet('HEAD.general','')");
         tabs.addTab("Fault","javascript:commandSet('HEAD.fault','')");
         tabs.addTab("Modification","javascript:commandSet('HEAD.modification','')");
         tabs.addTab("Oper Param","javascript:commandSet('HEAD.operparam','')");


         headbar.addCustomCommand("general","General");
         headbar.addCustomCommand("fault","Fault");
         headbar.addCustomCommand("modification","Modification");
         headbar.addCustomCommand("operparam","Oper Param");
         //

         headlay.defineGroup(mgr.translate("PCMWMODIFICATIONINFO: Info"), "WO_NO,TASK_NO,ACTUALCOMPLETION,TASK_CODE,TASK_DESC",false,true,2);



         //--------------------Block1 - General    --------------------------------------------

         itemblk1 = mgr.newASPBlock("ITEM1");

         itemblk1.addField("ITEM1_OBJID").
         setHidden().
         setDbName("OBJID");

         itemblk1.addField("ITEM1_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");

         itemblk1.addField("ITEM1_WO_NO").
         setDbName("WO_NO").
         setHidden();

         itemblk1.addField("ITEM1_TASK_NO").
         setDbName("TASK_NO").
         setSize(20).
         setLabel("PCMWMAINTTASKITEM1TASKNO: Task No").
         setUpperCase().
         setReadOnly();

         f = itemblk1.addField("MAINT_TASK_TYPE");
         f.setSize(35);
         f.setMaxLength(200);
         f.enumerateValues("MAINT_TASK_TYPE_API");
         f.setInsertable();
         f.setLabel("PCMWMAINTTASKMAINTTASKTYPE: Task Type");
         f.setReadOnly();
         // 030909  ARWILK  End (IID GEAM223N Install Part WEB)


         itemblk1.addField("POS").
         setSize(10).
         setLabel("PCMWMAINTTASKDESCRIPTION: Position").
         setReadOnly().
         setMaxLength(10);

         itemblk1.addField("ORDER_NO").
         setSize(20).
         setLabel("PCMWMAINTTASKORDERNO: Order No").
         setUpperCase().
         setReadOnly();

         itemblk1.addField("LOCATION_CODE").
         setSize(10).
         setLabel("PCMWMAINTTASKLOCATIONCODE: Location Code").
         setReadOnly().
         setMaxLength(10);

         itemblk1.addField("VEHICLE_ID").
         setSize(25).
         setLabel("PCMWMAINTTASKVEHICLEID: Vehicle ID").
         setReadOnly().
         setMaxLength(25);

         itemblk1.addField("PART_NO").
         setSize(25).
         setLabel("PCMWMAINTTASKPARTNO: Part No").
         setReadOnly().
         setUpperCase().
         setMaxLength(25);

         itemblk1.addField("SERIAL_NO").
         setSize(25).
         setLabel("PCMWMAINTTASKSERIALNO: Serial No").
         setReadOnly().
         setUpperCase().
         setMaxLength(50);

         // 030909  ARWILK  Begin (IID GEAM223N Install Part WEB)

         f = itemblk1.addField("SUB_PART_NO");
         f.setSize(25);
         f.setUpperCase();
         f.setLabel("PCMWMAINTTASKSUBPARTNO: Part Number");
         f.setInsertable();
         f.setReadOnly();
         f.setMaxLength(25);

         f = itemblk1.addField("SUB_SERIAL_NO");
         f.setSize(50);
         f.setUpperCase();
         f.setLabel("PCMWMAINTTASKSUBSERIALNO: Serial Number");
         f.setInsertable();
         f.setReadOnly();
         f.setMaxLength(50);

         // 030909  ARWILK  End (IID GEAM223N Install Part WEB)


         itemblk1.addField("USER_CREATED").
         setSize(30).
         setLabel("PCMWMAINTTASKCREATEDBY: Created By").
         setReadOnly().
         setMaxLength(30);

         itemblk1.addField("DT_CRE", "Datetime").
         setSize(30).
         setReadOnly().
         setMaxLength(30);

         itemblk1.addField("USER_SIGN").
         setSize(30).
         setLabel("PCMWMAINTTASKCHANGEDBY: Changed By").
         setReadOnly().
         setMaxLength(30);

         f = itemblk1.addField("DT_CHG", "Datetime");
         f.setSize(30);
         f.setReadOnly();
         f.setMaxLength(30);

         // -------------------------------------------------------------------------------------------------
         // ------------  Hidden Fields ---------------------------------------------------------------------
         // -------------------------------------------------------------------------------------------------


         f = itemblk1.addField("REAL_F_DATE", "Datetime");
         f.setFunction("''");
         f.setHidden();

         itemblk1.addField("ISVIMCAN").
         setHidden().
         setUpperCase().
         setFunction("''");

         // 030909  ARWILK  Begin (IID GEAM223N Install Part WEB)
         itemblk1.addField("SCONTRACTVAL").
         setHidden().
         setFunction(         "''");

         itemblk1.addField("SISLOCAL").
         setHidden().
         setFunction("''");

         itemblk1.addField("OBJECT_LU").
         setHidden().
         setFunction("''");

         itemblk1.addField("OBJECT_KEY").
         setHidden().
         setFunction("''");

         itemblk1.addField("PROPERTY_NAME").
         setHidden().
         setFunction("''");
         // 030909  ARWILK  End (IID GEAM223N Install Part WEB)  


         itemblk1.setView("WORK_ORDER_FROM_VIM");
         itemblk1.defineCommand("WORK_ORDER_FROM_VIM_API","Modify__");
         itemset1 = itemblk1.getASPRowSet();

         itemblk1.setMasterBlock(headblk);

         itembar1 = mgr.newASPCommandBar(itemblk1);

         itemtbl1 = mgr.newASPTable(itemblk1);
         itemtbl1.setTitle(mgr.translate("PCMWMAINTTASKITM1: General"));
         itemtbl1.setWrap();

         itembar1.disableCommand(itembar1.DUPLICATEROW);
         itembar1.disableCommand(itembar1.NEWROW);
         itembar1.disableCommand(itembar1.DELETE);
         itembar1.disableCommand(itembar1.BACK); 
         itembar1.disableCommand(itembar1.EDITROW); 

         // Putting up Cancel and Resend work order no

         itembar1.addCustomCommand("cancel", mgr.translate("PCMWMAINTTASKCANCELNEW: Cancel..."));
         itembar1.addCustomCommand("resendWorkOrderNo", mgr.translate("PCMWMAINTTASKRESENDWONO: Resend Work Order Number"));
         // 030909  ARWILK  Begin (IID GEAM223N Install Part WEB)
         itembar1.addCustomCommand("performStuctChange", mgr.translate("PCMWMAINTTASKPERFSTRCHG: Perform Structure Change..."));
         // 030909  ARWILK  End (IID GEAM223N Install Part WEB)

         itemlay1 = itemblk1.getASPBlockLayout();
         itemlay1.setSimple("DT_CRE");
         itemlay1.setSimple("DT_CHG");

         // 030909  ARWILK  Begin (IID GEAM223N Install Part WEB)
         itemlay1.defineGroup(mgr.translate("PCMWMAINTTASKINFORMATION: Task Information"),"MAINT_TASK_TYPE,ORDER_NO,POS,LOCATION_CODE,VEHICLE_ID,PART_NO,SERIAL_NO",true,true,3);
         itemlay1.defineGroup(mgr.translate("PCMWMAINTTASKREPLACESERIAL: Replace Serial"),"SUB_PART_NO,SUB_SERIAL_NO",true,true);
         itemlay1.defineGroup(mgr.translate("PCMWMAINTTASKCREATION: Creation"),"USER_CREATED,DT_CRE,USER_SIGN,DT_CHG",true,true);

         itemlay1.setFieldSpan("VEHICLE_ID",1,3);
         // 030909  ARWILK  End (IID GEAM223N Install Part WEB)



         itemlay1.setDefaultLayoutMode(itemlay1.SINGLE_LAYOUT);
         itemlay1.setDialogColumns(2);

         //---------------- Block 2 - Fault ------------------------------------------------

         itemblk2 = mgr.newASPBlock("ITEM2");

         itemblk2.addField("ITEM2_OBJID").
         setHidden().
         setDbName("OBJID");

         itemblk2.addField("ITEM2_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");

         itemblk2.addField("ITEM2_WO_NO").
         setDbName("WO_NO").
         setHidden();

         itemblk2.addField("ITEM2_TASK_NO").
         setDbName("TASK_NO").
         setSize(20).
         setLabel("PCMWMAINTTASKITEM2TASKNO: Task No").
         setUpperCase().
         setReadOnly();

         itemblk2.addField("ITEM2_PRODUCT_NO").
         setDbName("PRODUCT_NO").
         setSize(25).
         setLabel("PCMWMAINTTASKITEM2PRODUCTNO: Product No").
         setUpperCase().
         setReadOnly();

         itemblk2.addField("ITEM2_PRODUCT_DESC").
         setFunction("PRODUCT_CATALOG_API.Get_Description(:ITEM2_PRODUCT_NO)").
         setSize(30).
         setLabel("PCMWMAINTTASKPRODDESC: Product Description").
         setDefaultNotVisible().
         setReadOnly();

         itemblk2.addField("ITEM2_MODEL_NO").
         setDbName("MODEL_NO").
         setSize(25).
         setLabel("PCMWMAINTTASKITEM2MODELNO: Model No").
         setUpperCase().
         setReadOnly();

         itemblk2.addField("ITEM2_MODEL_DESC").
         setFunction("PRODUCT_MODEL_API.Get_Description(:ITEM2_PRODUCT_NO,:ITEM2_MODEL_NO)").
         setSize(30).
         setLabel("PCMWMAINTTASKMODELDESC: Model Description").
         setDefaultNotVisible().
         setReadOnly();

         itemblk2.addField("ITEM2_SYMPTOM").
         setDbName("SYMPTOM").
         setSize(30).
         setLabel("PCMWMAINTTASKITEM2SYMPTOM: Symptom").
         setUpperCase().
         setReadOnly();

         itemblk2.addField("ITEM2_SYMPTOM_DESC").
         setFunction("Fault_Mode_API.Get_Description(:ITEM2_PRODUCT_NO,:ITEM2_MODEL_NO,:ITEM2_SYMPTOM)").
         setSize(30).
         setDefaultNotVisible().
         setLabel("PCMWMAINTTASKSYMPTOMDESC: Symptom Description").
         setReadOnly();

         itemblk2.addField("ITEM2_FAULT_MODE").
         setDbName("FAULT_MODE").
         setDynamicLOV("FAULT_MODE3",600,450).
         setSize(25).
         setLabel("PCMWMAINTTASKITEM2FAULTMODE: Fault Mode").
         setCustomValidation("ITEM2_PRODUCT_NO,ITEM2_MODEL_NO,ITEM2_FAULT_MODE","ITEM2_FAULT_MODE_DESC").
         setUpperCase();

         itemblk2.addField("ITEM2_FAULT_MODE_DESC").
         setFunction("Fault_Mode_API.Get_Description(:ITEM2_PRODUCT_NO,:ITEM2_MODEL_NO,:ITEM2_FAULT_MODE)").
         setSize(25).
         setDefaultNotVisible().
         setLabel("PCMWMAINTTASKMODEESC: Mode Description").
         setReadOnly();

         itemblk2.addField("ITEM2_FUNCTION_NO").
         setDbName("FUNCTION_NO").
         setDynamicLOV("FAULT_FUNCTION_STRUCTURE",600,450).
         setSize(25).
         setLabel("PCMWMAINTTASKITEM2FUNCTIONNO: Function No").
         setCustomValidation("ITEM2_PRODUCT_NO,ITEM2_MODEL_NO,ITEM2_FUNCTION_NO","ITEM2_FUNCTION_DESC").
         setUpperCase();

         itemblk2.addField("ITEM2_FUNCTION_DESC").
         setFunction("PRODUCT_FUNCTION_API.Get_Description(:ITEM2_PRODUCT_NO,:ITEM2_MODEL_NO,:ITEM2_FUNCTION_NO)").
         setSize(30).
         setDefaultNotVisible().
         setLabel("PCMWMAINTTASKFUNCTIONDESC: Function Description").
         setReadOnly();

         itemblk2.addField("ITEM2_SUB_FUNCTION_NO").
         setDbName("SUB_FUNCTION_NO").
         setDynamicLOV("FAULT_FUNCTION_STRUCTURE2",600,450).
         setSize(25).
         setLabel("PCMWMAINTTASKITEM2SUBFUNCTIONNO: Sub Function No").
         setCustomValidation("ITEM2_PRODUCT_NO,ITEM2_MODEL_NO,ITEM2_SUB_FUNCTION_NO","ITEM2_SUB_FUNCTION_DESC").
         setUpperCase();

         itemblk2.addField("ITEM2_SUB_FUNCTION_DESC").
         setFunction("PRODUCT_FUNCTION_API.Get_Description(:ITEM2_PRODUCT_NO,:ITEM2_MODEL_NO,:ITEM2_SUB_FUNCTION_NO)").
         setSize(25).
         setDefaultNotVisible().
         setLabel("PCMWMAINTTASKSUBFUNCDESC: Sub Function Description").
         setReadOnly();

         itemblk2.addField("ITEM2_BOTTOM_FUNCTION_NO").
         setDbName("BOTTOM_FUNCTION_NO").
         setDynamicLOV("FAULT_FUNCTION_STRUCTURE2",600,450).
         setSize(25).
         setLabel("PCMWMAINTTASKITEM2BOTTOMFUNCTIONNO: Bottom Function No").
         setCustomValidation("ITEM2_PRODUCT_NO,ITEM2_MODEL_NO,ITEM2_BOTTOM_FUNCTION_NO","ITEM2_BOTTOM_FUNCTION_DESC").
         setUpperCase();

         itemblk2.addField("ITEM2_BOTTOM_FUNCTION_DESC").
         setFunction("PRODUCT_FUNCTION_API.Get_Description(:ITEM2_PRODUCT_NO,:ITEM2_MODEL_NO,:ITEM2_BOTTOM_FUNCTION_NO)").
         setSize(30).
         setDefaultNotVisible().
         setLabel("PCMWMAINTTASKBOTFUNCDESC: Bottom Function Description").
         setReadOnly();

         itemblk2.addField("ITEM2_FAULT_CODE").
         setDbName("FAULT_CODE").
         setDynamicLOV("FAULT_FUNCTION2",600,450).
         setSize(10).
         setLabel("PCMWMAINTTASKITEM2FAULTCODE: Fault Code").
         setCustomValidation("ITEM2_FAULT_CODE","ITEM2_FAULT_CODE_DESC").
         setUpperCase();

         itemblk2.addField("ITEM2_FAULT_CODE_DESC").
         setFunction("Fault_Code_API.Get_Fault_Code_Desc(:ITEM2_FAULT_CODE)").
         setSize(25).
         setDefaultNotVisible().
         setLabel("PCMWMAINTTASKFAULTCODEDESC: Code Description").
         setReadOnly();

         itemblk2.addField("ITEM2_DEFERRAL_DESC").
         setDbName("DEFERRAL_DESC").
         setSize(50).
         setLabel("PCMWMAINTTASKITEM2DEFERRALDESC: Deferral Description").
         setUpperCase().
         setReadOnly();

         itemblk2.addField("ITEM2_DEFERRAL_ACTIVE").
         setDbName("DEFERRAL_ACTIVE").
         setLabel("PCMWMAINTTASKITEM2DEFERRALACTIVE: Deferral Fault").
         setUpperCase().
         setReadOnly().
         setSize(5).
         setCheckBox("FALSE,TRUE");

         itemblk2.addField("ITEM2_FAULT_WARRANTY").
         setDbName("FAULT_WARRANTY").
         setLabel("PCMWMAINTTASKITEM2FAULTWARRANTY: Fault Warranty").
         setUpperCase().
         setReadOnly().
         setSize(5).
         setCheckBox("FALSE,TRUE");

         itemblk2.setView("WORK_ORDER_FROM_VIM");
         itemblk2.defineCommand("WORK_ORDER_FROM_VIM_API","Modify__");
         itemset2 = itemblk2.getASPRowSet();

         itemblk2.setMasterBlock(headblk);

         itembar2 = mgr.newASPCommandBar(itemblk2);

         itembar2.enableCommand(itembar2.EDITROW);
         itembar2.defineCommand(itembar2.SAVERETURN,"saveReturn");
         itembar2.defineCommand(itembar2.CANCELEDIT,"cancelEdit");

         itembar2.addCustomCommand("cancel", mgr.translate("PCMWMAINTTASKCANCELNEW: Cancel..."));
         itembar2.addCustomCommand("resendWorkOrderNo", mgr.translate("PCMWMAINTTASKRESENDWONO: Resend Work Order Number"));

         itemtbl2 = mgr.newASPTable(itemblk2);
         itemtbl2.setTitle(mgr.translate("PCMWMAINTTASKITM2: Fault"));
         itemtbl2.setWrap();

         itemlay2 = itemblk2.getASPBlockLayout();
         itemlay2.setDefaultLayoutMode(itemlay2.SINGLE_LAYOUT);
         itemlay2.setDialogColumns(2);

         itemlay2.setSimple("ITEM2_PRODUCT_DESC");
         itemlay2.setSimple("ITEM2_MODEL_DESC");
         itemlay2.setSimple("ITEM2_SYMPTOM_DESC");
         itemlay2.setSimple("ITEM2_FAULT_MODE_DESC");
         itemlay2.setSimple("ITEM2_FUNCTION_DESC");
         itemlay2.setSimple("ITEM2_SUB_FUNCTION_DESC");
         itemlay2.setSimple("ITEM2_BOTTOM_FUNCTION_DESC");
         itemlay2.setSimple("ITEM2_FAULT_CODE_DESC");

         // ------------------ Block 3 - Modification -------------------------------------

         itemblk3 = mgr.newASPBlock("ITEM3");

         itemblk3.addField("ITEM3_OBJID").
         setHidden().
         setDbName("OBJID");

         itemblk3.addField("ITEM3_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");

         itemblk3.addField("ITEM3_WO_NO").
         setDbName("WO_NO").
         setHidden();

         itemblk3.addField("ITEM3_TASK_NO").
         setDbName("TASK_NO").
         setLabel("PCMWMAINTTASKITEM3TASKNO: Task No").
         setUpperCase().
         setReadOnly();

         itemblk3.addField("MOD_EXECUTION_TYPE").
         setSelectBox().
         enumerateValues("MOD_EXECUTION_TYPE_API").
         setLabel("PCMWMAINTTASKITEM3NEXTEXECSTEP: Next Execution Step");
         
         //(+) Bug 66317 Start
         itemblk3.addField("ITEM3_MOD_EXECUTION_TYPE_DB").
         setDbName("MOD_EXECUTION_TYPE_DB").                 
         setHidden();
         
         itemblk3.addField("OLD_MOD_EXECUTION_TYPE").
         setHidden();

         itemblk3.addField("TERMINATE_INSPECTIONS").
         setCheckBox("FALSE,TRUE").
         setLabel("PCMWMAINTTASKITEM3TERMINSPCB: Terminate Inspections");
         //(+) Bug 66317 End

         itemblk3.addField("ITEM3_EXECUTION_REMARK").
         setDbName("MOD_EXECUTION_REMARK").
         setSize(100).
         setLabel("PCMWMAINTTASKITEM3EXECREM: Execution Remark").
         //(-/+) Bug 66317 Start
         setHeight(4); 
         //(-/+) Bug 66317 End   

         itemblk3.setView("WORK_ORDER_FROM_VIM");
         itemblk3.defineCommand("WORK_ORDER_FROM_VIM_API","Modify__");
         itemset3 = itemblk3.getASPRowSet();

         itemblk3.setMasterBlock(headblk);
         itemtbl3 = mgr.newASPTable(itemblk3);
         itemtbl3.setTitle(mgr.translate("PCMWMAINTTASKITEM3ITM3: Modification"));       
         itemtbl3.setWrap();

         itembar3 = mgr.newASPCommandBar(itemblk3);
         //(+) Bug 66317 Start
         itembar3.defineCommand(itembar3.SAVERETURN, null, "checkItem3MandatoryFields");
         //(+) Bug 66317 End

         itembar3.addCustomCommand("cancel", mgr.translate("PCMWMAINTTASKCANCELNEW: Cancel..."));
         itembar3.addCustomCommand("resendWorkOrderNo", mgr.translate("PCMWMAINTTASKRESENDWONO: Resend Work Order Number"));

         itemlay3 = itemblk3.getASPBlockLayout();
         itemlay3.setDefaultLayoutMode(itemlay3.SINGLE_LAYOUT);        
         
         // (+) WINGS (Start)
         // ------------------ Block 5 - Rename Serials -------------------------------------

         itemblk5 = mgr.newASPBlock("ITEM5");

         itemblk5.addField("ITEM5_OBJID").
         setHidden().
         setDbName("OBJID");

         itemblk5.addField("ITEM5_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");

         itemblk5.addField("ITEM5_PART_NO").
         setDbName("PART_NO").
         setLabel("PCMWMAINTKITEM5PARTNO: Part Number").
         setUpperCase().
         setMaxLength(25).
         setReadOnly().
         setSize(25);

         itemblk5.addField("ITEM5_PART_DESCRIPTION").
         setLabel("PCMWMAINTKITEM5PARTDESC: Part Description").
         setSize(50).
         unsetQueryable().
         setReadOnly().
         setFunction("Eng_Part_Master_API.Get_Part_Description(:ITEM5_PART_NO)");

         itemblk5.addField("ITEM5_PART_REV").
         setLabel("PCMWMAINTTASKITEM5PARTREV: Part Revision").
         setFunction("VIM_SERIAL_STRUCTURE_API.Get_Part_Rev(VIM_SERIAL_STRUCTURE_API.Get_Seq_No(PART_NO,SERIAL_NO))").
         //Bug 84513 Start
         setCustomValidation("ITEM5_PART_NO,ITEM5_PART_REV","PART_REV_EXIST").
         //Bug 84513 End
         setSize(6).
         setReadOnly();

         itemblk5.addField("ITEM5_SERIAL_NO").
         setDbName("SERIAL_NO").
         setLabel("PCMWMAINTKITEM5SN: Serial Number").
         setUpperCase().
         setMaxLength(50).
         setReadOnly().
         setSize(30);

         itemblk5.addField("ITEM5_NEW_PART_NO").
         setDbName("NEW_PART_NO").
         setLabel("PCMWMAINTKITEM5NEWPARTNO: New Part Number").
         setUpperCase().
         setMaxLength(25).
         setReadOnly().
         setSize(25);

         itemblk5.addField("ITEM5_NEW_PART_DESCRIPTION").
         setLabel("PCMWMAINTKITEM5NEWPARTDESC: New Part Description").
         setSize(50).
         unsetQueryable().
         setReadOnly().
         setFunction("Eng_Part_Master_API.Get_Part_Description(:ITEM5_NEW_PART_NO)");

         itemblk5.addField("ITEM5_NEW_PART_REV").
         setLabel("PCMWMAINTKITEM5NEWPARTREV: New Part Revision").
         setDbName("NEW_PART_REV").
         setUpperCase().
         setReadOnly().
         setMaxLength(6).
         setSize(6);

         itemblk5.addField("ITEM5_NEW_MAINT_PROG_ID").
         setLabel("PCMWMAINTKITEM5MTPROGID: Maint Program ID").
         setDbName("NEW_MAINT_PROG_ID").
         setLOV("../vimw/MaintProgramLov.page","ITEM5_NEW_PART_NO PART_NO, ITEM5_NEW_PART_REV PART_REV",600,450).
         //setDynamicLOV("MAINT_PROGRAM_PART_REV_LOV","ITEM5_NEW_PART_NO PART_NO,ITEM5_NEW_PART_REV PART_REV").
         setUpperCase().
         setMaxLength(40).
         setCustomValidation("ITEM5_NEW_MAINT_PROG_ID,ITEM5_NEW_PART_NO,ITEM5_NEW_PART_REV","ITEM5_DISCONN_MAINT_PROG,ITEM5_NEW_MAINT_PROG_ID,ITEM5_NEW_MAINT_PROG_REV,ITEM5_MAINT_PROG_DESC").
         setSize(40);

         itemblk5.addField("ITEM5_NEW_MAINT_PROG_REV").
         setDbName("NEW_MAINT_PROG_REV").
         setLabel("PCMWMAINTKITEM5MAINTPROGREV: Maint Program Revision").
         setUpperCase().
         setMaxLength(6).
         setCustomValidation("ITEM5_NEW_MAINT_PROG_ID,ITEM5_NEW_MAINT_PROG_REV,ITEM5_NEW_PART_NO,ITEM5_NEW_PART_REV","ITEM5_DISCONN_MAINT_PROG,ITEM5_NEW_MAINT_PROG_ID,ITEM5_NEW_MAINT_PROG_REV,ITEM5_MAINT_PROG_DESC").
         setSize(6).
         //setDynamicLOV("MAINT_PROGRAM_PART_REV_LOV","ITEM5_NEW_PART_NO PART_NO,ITEM5_NEW_PART_REV PART_REV,ITEM5_NEW_MAINT_PROG_ID MAINT_PROG_ID");
         setLOV("../vimw/MaintProgramLov.page","ITEM5_NEW_PART_NO PART_NO, ITEM5_NEW_PART_REV PART_REV, ITEM5_NEW_MAINT_PROG_ID MAINT_PROG_ID");

         itemblk5.addField("ITEM5_MAINT_PROG_DESC").
         setLabel("PCMWMAINTKITEM5MAINTPRGDESC: Maint Program Description").
         setFunction("Maint_Program_API.Get_Maint_Prog_Desc(:ITEM5_NEW_MAINT_PROG_ID,:ITEM5_NEW_MAINT_PROG_REV)").
         setSize(50).
         setReadOnly().
         unsetQueryable();

         itemblk5.addField("ITEM5_NEW_MAINT_GROUP").
         setLabel("PCMWMAINTKITME5MAINTGROUP: Maint Group").
         setCustomValidation("ITEM5_NEW_MAINT_GROUP,ITEM5_NEW_PART_NO","ITEM5_MAINT_GRP_DESC").
         setDbName("NEW_MAINT_GROUP").
         setUpperCase().
         setMaxLength(25).
         setSize(25).
         //setDynamicLOV("MAINT_GROUP","ITEM5_NEW_PART_NO PART_NO");
         setDynamicLOV("MAINT_PROGRAM_MAINT_GROUP2","ITEM5_NEW_PART_NO PART_NO,ITEM5_NEW_PART_REV PART_REV,ITEM5_NEW_MAINT_PROG_ID MAINT_PROG_ID,ITEM5_NEW_MAINT_PROG_ID MAINT_PROG_REV");

         itemblk5.addField("ITEM5_MAINT_GRP_DESC").
         setLabel("PCMWMAINTKITEM5MAINTGDESC: Maint Group Description").
         setSize(50).
         setFunction("Maint_Group_API.Get_Maint_Group_Desc(:ITEM5_NEW_PART_NO,:ITEM5_NEW_MAINT_GROUP)").
         setReadOnly().
         unsetQueryable();

         itemblk5.addField("ITEM5_DISCONN_MAINT_PROG").
         setDbName("DISCONN_MAINT_PROG").
         setUpperCase().
         setLabel("PCMWMAINTKITEM5DISCONMAINTPRG: Disconnect Serial From Maint Program").
         setCheckBox("FALSE,TRUE");

         itemblk5.addField("ITEM5_UPDATE_OPER_LOG_HIST").
         setDbName("UPDATE_OPER_LOG_HIST").
         setLabel("PCMWMAINTKITEM5OPRLOGHIST: Update Operational Log History").
         setCheckBox("FALSE,TRUE");

         itemblk5.addField("ITEM5_UPDATE_REPLACE_HIST").
         setDbName("UPDATE_REPLACE_HIST").
         setCheckBox("FALSE,TRUE").
         setLabel("PCMWMAINTKITEM5UPDREPHIST: Update Replacement History");

         itemblk5.addField("ITEM5_UPDATE_OTHER_HIST").
         setDbName("UPDATE_OTHER_HIST").
         setCheckBox("FALSE,TRUE").
         setLabel("PCMWMAINTKITEM5UPDOTHSHIST: Update Other History");

         itemblk5.addField("ITEM5_TEMPL_PART_NO").
         setReadOnly().
         setFunction("''").
         setLabel("PCMWMAINTTASKITEM5TEMPPARTNO: Template Part Number").
         setSize(40);

         itemblk5.addField("ITEM5_TEMPL_PART_REV").
         setReadOnly().
         setFunction("''").
         setLabel("PCMWMAINTTASKITEM5TEMPPARTREV: Template Part Revision").
         setSize(40);

         itemblk5.addField("ITEM5_STRCT_ADDRESS").
         setReadOnly().
         setFunction("''").
         setLabel("PCMWMAINTTASKITEM5STRUADD: Structure Position").
         setSize(40);

         itemblk5.addField("ITEM5_OPP_STATUS").
         setReadOnly().
         setFunction("Part_Serial_Catalog_API.Get_Operational_Status(:ITEM5_PART_NO, :ITEM5_SERIAL_NO)").
         setLabel("PCMWMAINTTASKITEM5OPPSTUS: Operational Status").
         setSize(40);

         itemblk5.addField("ITEM5_QUARANTINED").
         setCheckBox("FALSE,TRUE").
         setLabel("PCMWMAINTTASKITEM5QUARAN: Quarantined").
         setReadOnly().
         setFunction("VIM_SERIAL_API.Get_Quarantined(:ITEM5_PART_NO,:ITEM5_SERIAL_NO)");

         itemblk5.addField("ITEM5_NEW_MANUFACTURER_NO").
         setDbName("NEW_MANUFACTURER_NO").
         setMaxLength(20).
         setSize(20).
         setUpperCase().
         setCustomValidation("ITEM5_NEW_MANUFACTURER_NO","ITEM5_MANUFACTURER_NAME").
         //Bug 84513 Start
         setDynamicLOV("PART_REV_MANUFACTURER_LOV","ITEM5_NEW_PART_NO NEW_PART_NO, ITEM5_NEW_PART_REV NEW_PART_REV").
         //Bug 84513 End
         setLabel("PCMWMAINTKITEM5MANUNO: Manufacturer Number");

         itemblk5.addField("ITEM5_MANUFACTURER_NAME").
         setReadOnly().
         setFunction("Manufacturer_Info_API.Get_Name(:ITEM5_NEW_MANUFACTURER_NO)").
         unsetQueryable().
         setSize(50).
         setLabel("PCMWMAINTKITEM5MANUNAME: Manufacturer Name");

         itemblk5.addField("ITEM5_NEW_MANU_PART_NO").
         setDbName("NEW_MANU_PART_NO").
         setUpperCase().
         setSize(40).
         setMaxLength(80).
         setLabel("PCMWMAINTKITEM5MANUPARTNO: Manufacturer Part Number").
         //Bug 84513 Start
         setDynamicLOV("PART_REV_MANUF_PART_LOV","ITEM5_NEW_PART_NO NEW_PART_NO, ITEM5_NEW_PART_REV NEW_PART_REV, ITEM5_NEW_MANUFACTURER_NO NEW_MANUFACTURER_NO");
         //Bug 84513 End
         
         itemblk5.addField("NEW_TEMPL_STRESS_PART_NO").
         setLabel("PCMWMAINTTASKITEM5STRPNO: Stress Rating Template Part No").
         setLOV("../vimw/LifeLimit3Lov.page","ITEM5_NEW_PART_NO PART_NO,ITEM5_NEW_PART_REV PART_REV").
         setCustomValidation("NEW_TEMPL_STRESS_PART_NO","NEW_TEMPL_STRESS_PART_NO,NEW_TEMPL_STRESS_PART_REV,NEW_STRESS_RATING_ID,STRESS_RATING_DESC").
         setSize(35).
         setUpperCase();

         itemblk5.addField("NEW_TEMPL_STRESS_PART_REV").
         setLabel("PCMWMAINTTASKITEM5STRPR: Stress Rating Template Part Rev").
         setLOV("../vimw/LifeLimit3Lov.page","ITEM5_NEW_PART_NO PART_NO,ITEM5_NEW_PART_REV PART_REV").
         setCustomValidation("NEW_TEMPL_STRESS_PART_REV","NEW_TEMPL_STRESS_PART_NO,NEW_TEMPL_STRESS_PART_REV,NEW_STRESS_RATING_ID,STRESS_RATING_DESC").
         setSize(5).
         setUpperCase();

         itemblk5.addField("NEW_STRESS_RATING_ID").
         setLabel("PCMWMAINTTASKITEM5SD: Stress Rating ID").
         setLOV("../vimw/LifeLimit3Lov.page","ITEM5_NEW_PART_NO PART_NO,ITEM5_NEW_PART_REV PART_REV").
         setCustomValidation("NEW_STRESS_RATING_ID","NEW_TEMPL_STRESS_PART_NO,NEW_TEMPL_STRESS_PART_REV,NEW_STRESS_RATING_ID,STRESS_RATING_DESC").
         setSize(20).
         setUpperCase();

         itemblk5.addField("STRESS_RATING_DESC").
         setLabel("PCMWMAINTTASKITEM5SRD: Stress Rating Description").
         setSize(20).
         setReadOnly().
         setFunction("Stress_Rating_Per_Part_API.Get_Stress_Rating_Description(NEW_TEMPL_STRESS_PART_NO,NEW_TEMPL_STRESS_PART_REV,NEW_STRESS_RATING_ID)");

         itemblk5.addField("ITEM5_USER_CREATED").
         setDbName("USER_CREATED").
         setSize(30).
         setLabel("PCMWMAINTTASKITEM5CREATEDBY: Created By").
         setReadOnly().
         setMaxLength(30);

         itemblk5.addField("ITEM5_DT_CRE", "Datetime").
         setLabel("PCMWMAINTTASKITEM5DATECRE: Date Created").
         setDbName("DT_CRE").
         setSize(30).
         setReadOnly().
         setMaxLength(30);

         itemblk5.addField("ITEM5_USER_SIGN").
         setDbName("USER_SIGN").
         setSize(30).
         setLabel("PCMWMAINTTASKCHANGEDBYITEM5: Changed By").
         setReadOnly().
         setMaxLength(30);

         itemblk5.addField("DUMMY").setHidden().setFunction("''");

         f = itemblk5.addField("ITEM5_DT_CHG", "Datetime");
         f.setLabel("PCMWMAINTTASKITEM5DATECHG: Date Changed");
         f.setDbName("DT_CHG");
         f.setSize(30);
         f.setReadOnly();
         f.setMaxLength(30);

         itemblk5.addField("ITEM5_SEQ_NO").setDbName("SEQ_NO").setHidden();

         //Bug 84513 Start
         itemblk5.addField("PART_REV_EXIST").
         setHidden().
         setFunction("part_manu_part_rev_api.check_part_rev_exist(:ITEM5_NEW_PART_NO,:ITEM5_NEW_PART_REV)");   
         //Bug 84513 End

         itemblk5.setView("WO_FROM_VIM_RENAME_PART");
         itemblk5.defineCommand("WO_FROM_VIM_RENAME_PART_API","Modify__");
         itemset5 = itemblk5.getASPRowSet();

         itemblk5.setMasterBlock(headblk);
         itemtbl5 = mgr.newASPTable(itemblk5);
         itemtbl5.setTitle(mgr.translate("PCMWMAINTTASKITEM3ITM5: Rename Serials"));
         itemtbl5.setWrap();

         itembar5 = mgr.newASPCommandBar(itemblk5);
         itemlay5 = itemblk5.getASPBlockLayout();
         itemlay5.setDefaultLayoutMode(itemlay5.MULTIROW_LAYOUT);

         itembar5.defineCommand(itembar5.SAVERETURN,"saveReturnITEM5");
         // (+) WINGS (Finish)

         // ------------------ Block 4 - Oper Param -------------------------------------

         itemblk4 = mgr.newASPBlock("ITEM4");

         itemblk4.addField("ITEM4_OBJID").
         setHidden().
         setDbName("OBJID");

         itemblk4.addField("ITEM4_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");

         itemblk4.addField("ITEM4_WO_NO").
         setDbName("WO_NO").
         setHidden();

         itemblk4.addField("ITEM4_TASK_NO").
         setDbName("TASK_NO").
         setHidden();

         itemblk4.addField("OPER_PARAM").
         setSize(25).
         setLabel("PCMWMAINTTASKOPERPARAM: Oper Param").
         setMandatory().
         setMaxLength(30).
         setReadOnly();

         itemblk4.addField("OPER_PARAM_DESC").
         setSize(25).
         setLabel("PCMWMAINTTASKOPERPARAMDESC: Oper Param Description").
         setMaxLength(50).
         setReadOnly();

         itemblk4.addField("VALUE_OH","Number").
         setSize(25).
         setLabel("PCMWMAINTTASKVALUEOH: Value After Overhaul");

         itemblk4.addField("VALUE_TOTAL","Number").
         setSize(25).
         setLabel("PCMWMAINTTASKVALUETOT: Value Total");

         itemblk4.addField("ITEM4_ACTUAL_COMP_DT", "Datetime").
         setFunction("''").
         setHidden();

         itemblk4.addField("DUMMY_SYSDATE", "Datetime").
         setFunction("SYSDATE").
         setHidden();

         itemblk4.addField("INFO").
         setFunction("''").
         setHidden();

         itemblk4.addField("ATTR").
         setFunction("''").
         setHidden();

         itemblk4.addField("ACTION").
         setFunction("''").
         setHidden();

         itemblk4.setView("WORK_ORDER_FROM_VIM_PARAM");
         itemblk4.defineCommand("WORK_ORDER_FROM_VIM_PARAM_API","Modify__");
         itemset4 = itemblk4.getASPRowSet();

         itemblk4.setMasterBlock(headblk);

         itembar4 = mgr.newASPCommandBar(itemblk4);

         itembar4.defineCommand(itembar5.SAVE,"saveITEM4");
         itembar4.enableCommand(itembar4.FIND);
         itembar4.enableCommand(itembar4.SAVE);
         itembar4.enableMultirowAction();
         itembar4.disableCommand(itembar4.VIEWDETAILS);
         itembar4.disableCommand(itembar4.EDITROW);
         itembar4.addSecureCustomCommand("getDefaultOperParams", "VIMWMTASKOPPARASUGGVAL: Suggest Operational Values...","Work_Order_From_Vim_Param_API.Get_Defaults__");

         itemtbl4 = mgr.newASPTable(itemblk4);
         itemtbl4.setTitle(mgr.translate("PCMWMAINTTASKITM4TITLE: Oper Param"));
         itemtbl4.setWrap();
         itemtbl4.setEditable();

         itemlay4 = itemblk4.getASPBlockLayout();
         itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT);
      }
   }

   public void adjust()
   {
      ASPManager mgr = getASPManager();

      headbar.removeCustomCommand("general");
      headbar.removeCustomCommand("fault");
      headbar.removeCustomCommand("modification");
      headbar.removeCustomCommand("operparam");

      trans.clear();

      ASPTransactionBuffer secBuff = mgr.newASPTransactionBuffer();
      secBuff.addSecurityQuery("Work_Order_From_Vim_API","Resend_Wo_No");
      secBuff.addSecurityQuery("Work_Order_From_Vim_API","Cancel__");

      secBuff = mgr.perform(secBuff);

      if (!secBuff.getSecurityInfo().itemExists("Work_Order_From_Vim_API.Resend_Wo_No") || "TRUE".equals(sIsLocal))
      {
          headbar.removeCustomCommand("resendWorkOrderNo");
          itembar1.removeCustomCommand("resendWorkOrderNo");
          itembar2.removeCustomCommand("resendWorkOrderNo");
          itembar3.removeCustomCommand("resendWorkOrderNo");
      }

      if (!secBuff.getSecurityInfo().itemExists("Work_Order_From_Vim_API.Cancel__"))
         headbar.removeCustomCommand("cancel");

      // 030909  ARWILK  Begin (IID GEAM223N Install Part WEB)
      if ("TRUE".equals(sIsLocal) && bStrWizPageExists)
          itembar1.enableCommand("performStuctChange");
      else
          itembar1.disableCommand("performStuctChange");
      // 030909  ARWILK  End (IID GEAM223N Install Part WEB)

      // (+) WINGS (Start)
      if ("4".equals(mgr.readValue("MOD_EXECUTION_TYPE_DB")))
      {
         mgr.getASPField("ITEM5_UPDATE_OPER_LOG_HIST").setReadOnly();
         mgr.getASPField("ITEM5_UPDATE_REPLACE_HIST").setReadOnly();
         mgr.getASPField("ITEM5_UPDATE_OTHER_HIST").setReadOnly();
      }

      // (+) WINGS (Finish)
      if (itemlay5.getLayoutMode() == itemlay5.EDIT_LAYOUT)
      {
         trans.clear();
         cmd = trans.addCustomFunction("ITEM5ISCONNECTEDTOMAINTPROAD", "Maint_Program_Part_Rev_API.Part_Rev_Exist_On_Maint_Prog", "DUMMY" );
         cmd.addParameter("ITEM5_NEW_PART_NO",itemset5.getValue("NEW_PART_NO"));
         cmd.addParameter("ITEM5_NEW_PART_REV",itemset5.getValue("NEW_PART_REV"));

         trans = mgr.perform(trans);
         sIsConnected = "";
         sIsConnected = trans.getValue("ITEM5ISCONNECTEDTOMAINTPROAD/DATA/DUMMY");

         if (this.itemlay5.getLayoutMode() == this.itemlay5.EDIT_LAYOUT)
         {
            if (!mgr.isEmpty(itemset5.getValue("DISCONN_MAINT_PROG")))
            {
               if (("FALSE".equalsIgnoreCase(sIsConnected) && "TRUE".equalsIgnoreCase(itemset5.getValue("DISCONN_MAINT_PROG"))) || ("TRUE".equalsIgnoreCase(sIsConnected) && "FALSE".equalsIgnoreCase(itemset5.getValue("DISCONN_MAINT_PROG"))))
               {
                  mgr.getASPField("ITEM5_DISCONN_MAINT_PROG").setReadOnly();
               }
            }
         }

         if ("4".equals(headset.getValue("MOD_EXECUTION_TYPE_DB")))
         {
            mgr.getASPField("ITEM5_UPDATE_OPER_LOG_HIST").setReadOnly();
            mgr.getASPField("ITEM5_UPDATE_REPLACE_HIST").setReadOnly();
            mgr.getASPField("ITEM5_UPDATE_OTHER_HIST").setReadOnly();
         }
      }

   }

   //(+) Bug 66317 Start
   private void toggleModificationTab()
   {
       ASPManager mgr = getASPManager();
       String oldExecutionType;

       tabs.setTabEnabled(3, false);

       if (headset.countRows() > 0)
       {
          //Bug 84513 Start
          if ("4".equals(headset.getRow().getValue("MAINT_TASK_TYPE_DB")))
          {
             if (itemset3.countRows() > 0)
             {
                oldExecutionType = itemset3.getRow().getValue("OLD_MOD_EXECUTION_TYPE");
                tabs.setTabEnabled(3, true);
                if ("3".equals(oldExecutionType) || "4".equals(oldExecutionType))
                {
                    itembar3.disableCommand(itembar3.EDITROW);
                }
             }
          }
          //Bug 84513 End
       }
   }
   //(+) Bug 66317 End

   public void tabsInit()
   {
      ASPManager mgr = getASPManager();    
      
      //(+) Bug 66317 Start
      toggleModificationTab();
      //(+) Bug 66317 End

      switch (activetab)
      {
      case 0:
         {
            tabs.setActiveTab(1);
            break;
         }
      case 1:
         {
            tabs.setActiveTab(2);
            break;
         }
      case 2:
         {
            tabs.setActiveTab(3);
            break;
         }
      case 3:
         {
            tabs.setActiveTab(4);
            break;
         }
      } 

      mgr.responseWrite(tabs.showTabsInit());
   }

   public void tabsFinish()
   {
      ASPManager mgr = getASPManager();
      mgr.responseWrite(tabs.showTabsFinish());
   }

   public void general()                     
   {
      activetab = 0;
   }

   public void fault()                     
   {
      activetab = 1;
   }

   public void modification()                     
   {
      activetab = 2;
   }

   public void operparam()                     
   {
      activetab = 3;
   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "PCMWMAINTTASKTITLE: Maint Task";
   }

   protected String getTitle()
   {
      return "PCMWMAINTTASKTITLE: Maint Task";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      appendToHTML(headlay.show());

      printHiddenField("REM", "");
      printHiddenField("TABNO", "");
      printHiddenField("DEFAULTDATE", "");

      appendDirtyJavaScript("window.name = \"mainttask\";\n\n");

      if (headlay.isSingleLayout() && headset.countRows() > 0)
      {
         tabsInit();

         if (activetab == 0)
            appendToHTML(itemlay1.show());
         else if (activetab == 1)
            appendToHTML(itemlay2.show());
         // (+) WINGS (Start)
         else if (activetab == 2)
         {
            appendToHTML(itemlay3.show());
            //(+) Bug 66317 Start
            // Call every time modification tab is selected.
            if (itemlay3.isEditLayout())
              appendDirtyJavaScript("toggleExecutionTypeSelection();\n");
            //(+) Bug 66317 End 
            appendToHTML(itemlay5.show());
         }
         // (+) WINGS (Finish)
         else if ( activetab == 3)
            appendToHTML(itemlay4.show());

         tabsFinish();
      }


      if (bCancelPage)
      {
         appendDirtyJavaScript("\n\nwindow.open(\"");
         appendDirtyJavaScript(sWindowPath);
         appendDirtyJavaScript("\",\"cancelCause\",\"status=yes,resizable=1,scrollbars=yes,width=750,height=600\");\n\n");

      }
      if (bOpenStrWizPage)
      {
         appendDirtyJavaScript("\n\nwindow.open(\"");
         appendDirtyJavaScript(sWindowPath);
         appendDirtyJavaScript("\",\"vimSerStrChgWiz\",\"status=yes,resizable=1,scrollbars=yes,width=750,height=600\");\n\n");
      }

      if (bReportFaultPage)
      {
         appendDirtyJavaScript("\n\nwindow.open(\"");
         appendDirtyJavaScript(sWindowPath);
         appendDirtyJavaScript("\",\"reportFault\",\"status=yes,resizable=1,scrollbars=yes,width=800,height=600\");\n\n");
      }

      if (bOpenNewWindow)
      {
         bOpenNewWindow = false;

         appendDirtyJavaScript("\n\nwindow.open(\"");
         appendDirtyJavaScript(sWindowPath);
         appendDirtyJavaScript("\", \"" + sWinHandleName + "\", \"status=yes,resizable=1,scrollbars=yes,width=800,height=600\");\n\n");
      }

      appendToHTML(fmt.drawHidden("REFRESH_FLAG",""));

      appendDirtyJavaScript("function refreshMaint()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   f.REFRESH_FLAG.value = 'TRUE';\n");
      appendDirtyJavaScript("   submit();\n");
      appendDirtyJavaScript("}\n\n");

      appendDirtyJavaScript("function setDefaultDateValues(val)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("      document.form.DEFAULTDATE.value = val;\n");
      appendDirtyJavaScript("      f.submit();\n");
      appendDirtyJavaScript("}\n\n");

      appendDirtyJavaScript("function lovItem5NewMaintGroup(i,params)\n");
      appendDirtyJavaScript("{ \n");
      appendDirtyJavaScript("    if(params) param = params;\n");
      appendDirtyJavaScript("    else param = '';\n");
      appendDirtyJavaScript("    var enable_multichoice =(true && ITEM5_IN_FIND_MODE);\n");
      appendDirtyJavaScript("    var key_value = (getValue_('ITEM5_NEW_MAINT_GROUP',i).indexOf('%') !=-1)? getValue_('ITEM5_NEW_MAINT_GROUP',i):'';\n");
      appendDirtyJavaScript("    if(getValue_('ITEM5_NEW_MAINT_PROG_ID',i) == '')\n");
      appendDirtyJavaScript("       openLOVWindow('ITEM5_NEW_MAINT_GROUP',i,\n");
      appendDirtyJavaScript("           APP_ROOT + 'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=MAINT_GROUP&__FIELD=Maint+Group&__INIT=1' + '&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("           + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM5_NEW_MAINT_GROUP',i))\n");
      appendDirtyJavaScript("           + '&ITEM5_NEW_MAINT_GROUP=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("           + '&PART_NO=' + URLClientEncode(getValue_('ITEM5_NEW_PART_NO',i))\n");
      appendDirtyJavaScript("           ,550,500,'validateItem5NewMaintGroup');\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("       openLOVWindow('ITEM5_NEW_MAINT_GROUP',i,\n");
      appendDirtyJavaScript("         	APP_ROOT + 'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=MAINT_PROGRAM_MAINT_GROUP2&__FIELD=Maint+Group&__INIT=1' + '&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("         	+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM5_NEW_MAINT_GROUP',i))\n");
      appendDirtyJavaScript("         	+ '&ITEM5_NEW_MAINT_GROUP=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("         	+ '&PART_NO=' + URLClientEncode(getValue_('ITEM5_NEW_PART_NO',i))\n");
      appendDirtyJavaScript("         	+ '&PART_REV=' + URLClientEncode(getValue_('ITEM5_NEW_PART_REV',i))\n");
      appendDirtyJavaScript("         	+ '&MAINT_PROG_ID=' + URLClientEncode(getValue_('ITEM5_NEW_MAINT_PROG_ID',i))\n");
      appendDirtyJavaScript("         	+ '&MAINT_PROG_REV=' + URLClientEncode(getValue_('ITEM5_NEW_MAINT_PROG_REV',i))\n");
      appendDirtyJavaScript("         	,550,500,'validateItem5NewMaintGroup');\n");
      appendDirtyJavaScript("}\n");
      
      //(+) Bug 66317 Start
      appendDirtyJavaScript("function validateModExecutionType(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if( getRowStatus_('ITEM3',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("   setDirty();\n");
      appendDirtyJavaScript("   if( !checkModExecutionType(i) ) return;\n");
      appendDirtyJavaScript("   toggleExecutionTypeSelection();\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateTerminateInspections(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if( getRowStatus_('ITEM3',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript("   setDirty();\n");
      appendDirtyJavaScript("   if( !checkTerminateInspections(i) ) return;\n");
      appendDirtyJavaScript("}\n"); 

      appendDirtyJavaScript("function checkItem3MandatoryFields()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (f.TERMINATE_INSPECTIONS.checked && f.ITEM3_EXECUTION_REMARK.value == '')\n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(mgr.translate("VIMWMAINTTASKMODEXFORTERMINSP: When terminate inspections is selected mod execution remark must be given."));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("   else if (f.MOD_EXECUTION_TYPE.selectedIndex > 0 && (f.ITEM3_MOD_EXECUTION_TYPE_DB.value != f.MOD_EXECUTION_TYPE.selectedIndex) && f.ITEM3_EXECUTION_REMARK.value == '')\n");
      appendDirtyJavaScript("      alert('");
      appendDirtyJavaScript(mgr.translate("VIMWMAINTTASKMODEXFORTYPECHANGE: When the execution type is updated the mod execution remark must be given."));
      appendDirtyJavaScript("');\n");      
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("      return true\n;");
      appendDirtyJavaScript("}\n"); 

      appendDirtyJavaScript("function toggleExecutionTypeSelection(i)\n");
      appendDirtyJavaScript("{\n");
      // If Nothing or Blank Selection
      appendDirtyJavaScript("   if (f.MOD_EXECUTION_TYPE.selectedIndex < 1)\n");
      appendDirtyJavaScript("      f.TERMINATE_INSPECTIONS.disabled = false;\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      f.TERMINATE_INSPECTIONS.checked = false;\n");
      appendDirtyJavaScript("      f.TERMINATE_INSPECTIONS.disabled = true;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");
      //(+) Bug 66317 End  

      //Bug 84513 Start
      appendDirtyJavaScript("function lovItem5NewManufacturerNo(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  if(params) param = params;\n");
      appendDirtyJavaScript("  else param = '';\n");
      appendDirtyJavaScript("  var enable_multichoice =(true && ITEM5_IN_FIND_MODE);\n");
      appendDirtyJavaScript("  var key_value = (getValue_('ITEM5_NEW_MANUFACTURER_NO',i).indexOf('%') !=-1)? getValue_('ITEM5_NEW_MANUFACTURER_NO',i):'';\n");
      appendDirtyJavaScript("	if(getField_('PART_REV_EXIST',i).value == 'TRUE') \n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("openLOVWindow('ITEM5_NEW_MANUFACTURER_NO',i,\n");
      appendDirtyJavaScript("  	APP_ROOT + 'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PART_REV_MANUFACTURER_LOV&__FIELD=Manufacturer+Number&__INIT=1&__LOV=Y' + '&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("  	+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM5_NEW_MANUFACTURER_NO',i))\n");
      appendDirtyJavaScript("  	+ '&ITEM5_NEW_MANUFACTURER_NO=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("  	+ '&PART_NO=' + URLClientEncode(getValue_('ITEM5_NEW_PART_NO',i))\n");
      appendDirtyJavaScript("  	+ '&PART_REV=' + URLClientEncode(getValue_('ITEM5_NEW_PART_REV',i))\n");
      appendDirtyJavaScript("  	,550,500,'validateItem5NewManufacturerNo');\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("else\n");
      appendDirtyJavaScript("{\n"); 
      appendDirtyJavaScript("openLOVWindow('ITEM5_NEW_MANUFACTURER_NO',i,\n");
      appendDirtyJavaScript("  	APP_ROOT + 'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=VIM_SERIAL_MANUFACTURER2&__FIELD=Manufacturer+Number&__INIT=1&__LOV=Y' + '&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("  	+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM5_NEW_MANUFACTURER_NO',i))\n");
      appendDirtyJavaScript("  	+ '&ITEM5_NEW_MANUFACTURER_NO=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("  	+ '&PART_NO=' + URLClientEncode(getValue_('ITEM5_NEW_PART_NO',i))\n");
      appendDirtyJavaScript("  	,550,500,'validateItem5NewManufacturerNo');\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function lovItem5NewManuPartNo(i,params)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  if(params) param = params;\n");
      appendDirtyJavaScript("  else param = '';\n");
      appendDirtyJavaScript("  var enable_multichoice =(true && ITEM5_IN_FIND_MODE);\n");
      appendDirtyJavaScript("  var key_value = (getValue_('ITEM5_NEW_MANU_PART_NO',i).indexOf('%') !=-1)? getValue_('ITEM5_NEW_MANU_PART_NO',i):'';\n");
      appendDirtyJavaScript("	if(getField_('PART_REV_EXIST',i).value == 'TRUE') \n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("openLOVWindow('ITEM5_NEW_MANU_PART_NO',i,\n");
      appendDirtyJavaScript("        APP_ROOT + 'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=PART_REV_MANUF_PART_LOV&__FIELD=%1DManufacturer%1D0027s+Part+No&__INIT=1&__LOV=Y' + '&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("        + '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM5_NEW_MANU_PART_NO',i))\n");
      appendDirtyJavaScript("        + '&ITEM5_NEW_MANU_PART_NO=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("        + '&PART_NO=' + URLClientEncode(getValue_('ITEM5_NEW_PART_NO',i))\n");
      appendDirtyJavaScript("        + '&PART_REV=' + URLClientEncode(getValue_('ITEM5_NEW_PART_REV',i))\n");
      appendDirtyJavaScript("        + '&MANUFACTURE_NO=' + URLClientEncode(getValue_('ITEM5_NEW_MANUFACTURER_NO',i))\n");
      appendDirtyJavaScript("        ,550,500,'validateItem5NewManuPartNo');\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("else\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("openLOVWindow('ITEM5_NEW_MANU_PART_NO',i,\n");
      appendDirtyJavaScript("		APP_ROOT + 'common/scripts/DynamicLov.page?__DYNAMIC_LOV_VIEW=VIM_SERIAL_MANUFACTURER&__FIELD=%1DManufacturer%1D0027s+Part+No&__INIT=1&__LOV=Y' + '&MULTICHOICE='+enable_multichoice+''\n");
      appendDirtyJavaScript("  	+ '&__KEY_VALUE=' + URLClientEncode(getValue_('ITEM5_NEW_MANU_PART_NO',i))\n");
      appendDirtyJavaScript("  	+ '&ITEM5_NEW_MANU_PART_NO=' + URLClientEncode(key_value)\n");
      appendDirtyJavaScript("  	+ '&PART_NO=' + URLClientEncode(getValue_('ITEM5_NEW_PART_NO',i))\n");
      appendDirtyJavaScript("  	+ '&MANUFACTURE_NO=' + URLClientEncode(getValue_('ITEM5_NEW_MANUFACTURER_NO',i))\n");
      appendDirtyJavaScript("  	,550,500,'validateItem5NewManuPartNo');\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("}\n");   
      //Bug 84513 End
   }   
}  