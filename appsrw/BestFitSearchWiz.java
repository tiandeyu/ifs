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
*  File        : BestFitSearchWiz.java 
*  ASP2JAVA Tool  2001-03-16  - Created Using the ASP file BestFitSearchWiz.asp
*  Modified    :  2001-03-20  BUNI Corrected some conversion errors.
*                 NUPELK  14-05-2001 Made adjustments because of Security Scanning warnings
*              :  VAGULK  22-05-2001 Code Review  
*              :  SHCHLK  22-05-2001 Commented the buffer size in okFind() and okFind1() functions.
*              :  BUNI    25-06-2001 Modified saveReturn(), okFindITEM3(), previousForm()  and getContents()
*                                    functions.
*              :  BUNI    27-06-2001 Added another two fields just opposite to 'Value Text' and 'Where' select boxes in edit
*                                    layout mode to get functionality to, add numeric and alphanumeric values for seach criteria.
*                                    Adding more than one conditions separated by semicolon is also possible as in centura client. 
*              :  ARWI    2001-09-04 Used mgr.readValue("__COMMAND") to stop the system error that occurs when using mgr.commandBarFunction(). 
*                 (Caused when cancelNew is performed after saveNew)
* 030120      ThAblk    Bug Id 27913 Lots of changes. Mainly made frames 2,3 editable in multirow mode.
* 030120                Enabled Count Find functionality for all frames.
* 030120                Enabled Row Select in Frame 4.
* 030120                Removed Validate function.
* 030916      Raselk    Call 103276 - Core Bug ID 27913 is not merged to takeoff.
*                       & replaced getBuffer("GEN4/DATA").getFieldValue() used in nextForm() & getRow().getFieldValue
*                       in okFindITEM0 & CountFindITEM0 to getValue() 
*                       so as that it wont take the client value of the field without converting.
* ----------------------------------------------------------------------------
* New Comments:
* 2006/07/04 buhilk Bug 58216, Fixed SQL Injection threats
* 2006/09/13 sumelk Bug 59368, Corrected erroneous translation constants.
* 2007/04/16 sumelk Merged corrections for Bug 64093, Changed preDefine() and getContents(). 
*/


package ifs.appsrw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class BestFitSearchWiz extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.appsrw.BestFitSearchWiz");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPForm frm;
   private ASPHTMLFormatter fmt;
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
   private ASPCommandBar itembar1;
   private ASPTable itemtbl1;
   private ASPBlockLayout itemlay1;
   private ASPBlock itemblk2;
   private ASPRowSet itemset2;
   private ASPCommandBar itembar2;
   private ASPTable itemtbl2;
   private ASPBlockLayout itemlay2;
   private ASPField f;
   private ASPBlock tempblk1;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPLog log; // Bug 27913
   private int frame;
   private String techSeaNo;
   private boolean errFlag;
   private boolean valFlag;
   private ASPCommand cmd;
   private ASPBuffer buff;
   private ASPQuery q;
   private ASPBuffer bur;
   private String teClass; 
   private String techClass;
   private String selCou;
   private String selCou1;
   private String descLong;
   private String desc;
   private int teSpecNo;
   private String att;
   private String attdesc;
   private String valte;
   private String whte; 
   private String valno;
   private String unit; 
   private String closeFlag;
   private String sValidValues, not_equal_to, between, less_than, less_or_equal, equal_to, greater_than, greater_or_equal; // Bug 27913
   private int currRow;
   private boolean showHtmlPart;
   private String url;

   //===============================================================
   // Construction 
   //===============================================================
   public BestFitSearchWiz(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      log = null; // Bug 27913
      frame   = 0;
      techSeaNo   = null;
      errFlag   = false;
      valFlag   = false;
      cmd   = null;
      buff   = null;
      q   = null;
      bur   = null;
      teClass   = null;
      techClass   = null;
      selCou   = null;
      selCou1   = null;
      descLong   = null;
      desc   = null;
      teSpecNo   = 0;
      currRow   = 0;
      att   = null;
      attdesc   = null;
      valte   = null;
      whte   = null;
      valno   = null;
      unit   = null;
      closeFlag   = null;
// start, Bug Id 27913
      sValidValues = null;
      not_equal_to = null;
      between = null;
      less_than = null;
      less_or_equal = null;
      equal_to = null;
      greater_than = null;
      greater_or_equal = null;
// end, Bug Id 27913

      showHtmlPart = true;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      BestFitSearchWiz page = (BestFitSearchWiz)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.log   = null; // Bug 27913
      page.frame   = 0;
      page.techSeaNo   = null;
      page.errFlag   = false;
      page.valFlag   = false;
      page.cmd   = null;
      page.buff   = null;
      page.q   = null;
      page.bur   = null;
      page.showHtmlPart = true;

      // Cloning immutable attributes
      page.frm = page.getASPForm();
      page.fmt = page.getASPHTMLFormatter();
      page.ctx = page.getASPContext();
      page.headblk = page.getASPBlock(headblk.getName());
      page.headset = page.headblk.getASPRowSet();
      page.headbar = page.headblk.getASPCommandBar();
      page.headtbl = page.getASPTable(headtbl.getName());
      page.headlay = page.headblk.getASPBlockLayout();
      page.itemblk0 = page.getASPBlock(itemblk0.getName());
      page.itemset0 = page.itemblk0.getASPRowSet();
      page.itembar0 = page.itemblk0.getASPCommandBar();
      page.itemtbl0 = page.getASPTable(itemtbl0.getName());
      page.itemlay0 = page.itemblk0.getASPBlockLayout();
      page.itemblk1 = page.getASPBlock(itemblk1.getName());
      page.itemset1 = page.itemblk1.getASPRowSet();
      page.itembar1 = page.itemblk1.getASPCommandBar();
      page.itemtbl1 = page.getASPTable(itemtbl1.getName());
      page.itemlay1 = page.itemblk1.getASPBlockLayout();
      page.itemblk2 = page.getASPBlock(itemblk2.getName());
      page.itemset2 = page.itemblk2.getASPRowSet();
      page.itembar2 = page.itemblk2.getASPCommandBar();
      page.itemtbl2 = page.getASPTable(itemtbl2.getName());
      page.itemlay2 = page.itemblk2.getASPBlockLayout();
      page.f = page.getASPField(f.getName());
      page.tempblk1 = page.getASPBlock(tempblk1.getName());
      page.teClass   = null;
      page.techClass   = null;
      page.selCou   = null;
      page.selCou1   = null;
      page.descLong   = null;
      page.desc   = null;
      page.teSpecNo   = 0;
      page.currRow = 0;
      page.att   = null;
      page.attdesc   = null;
      page.valte   = null;
      page.whte   = null;
      page.valno   = null;
      page.unit   = null;
      page.closeFlag   = null;
// start, Bug Id 27913
      page.sValidValues = null;
      page.not_equal_to = null;
      page.between = null;
      page.less_than = null;
      page.less_or_equal = null;
      page.equal_to = null;
      page.greater_than = null;
      page.greater_or_equal = null;
// end, Bug Id 27913

      return page;
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      frm = mgr.getASPForm();
      fmt = mgr.newASPHTMLFormatter();
      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      log = mgr.getASPLog(); // Bug 27913

      mgr.getASPPage().disableHomeIcon();
      mgr.getASPPage().disableNavigate();
      mgr.getASPPage().disableOptions();
      
      frame = ctx.readNumber("FRAME",1);
      techSeaNo = ctx.readValue("TECHSEANO","");
      teClass = ctx.readValue("TECLASS","");
      techClass = ctx.readValue("TECHCLASS","");
      selCou = ctx.readValue("SELCOU","");
      selCou1 = ctx.readValue("SELCOU1","");
      descLong = ctx.readValue("DESCLONG","");
      desc = ctx.readValue("DESC","");
      teSpecNo = ctx.readNumber("TESPECNO",0);
      currRow = ctx.readNumber("CURRROW",0);
      att = ctx.readValue("ATT","");
      attdesc = ctx.readValue("ATTDESC","");
      valte = ctx.readValue("VALTE","");
      whte = ctx.readValue("WHTE","");
      valno = ctx.readValue("VALNO","");
      unit = ctx.readValue("UNIT","");
      errFlag = ctx.readFlag("ERRFLAG",false);
      valFlag = ctx.readFlag("VALFLAG",false);
      closeFlag = ctx.readValue("CLOSEFLAG","FALSE");
      showHtmlPart = ctx.readFlag("CTSSHHTML",true);


      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("WNDFLAG")))
      {
         if ("OpenInNewWin".equals(mgr.readValue("WNDFLAG","")))
         {
            showHtmlPart = false;
         }
      }
      else if (mgr.dataTransfered())
         okFind();
      else if (mgr.buttonPressed("PREVIOUS"))
         previousForm();
      else if (mgr.buttonPressed("NEXT"))
         nextForm();
      else if (mgr.buttonPressed("FINISH"))
         finishForm();
      else if (mgr.buttonPressed("CANCEL"))
         finishForm();
      else if ("1".equals(mgr.readValue("__SEARCHFLAG")))
      {
         teClass = mgr.readValue("__TECLASS","");
         techSeaNo = "";
         okFind1();   
      }
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         okFind();
      else
      {
         trans.clear();
         okFind();
         startup();
         okFindITEM0();
      }
      mgr.setPageExpiring();

      adjust();
      ctx.writeNumber("FRAME",frame);
      ctx.writeValue("TECHSEANO",techSeaNo);
      ctx.writeValue("TECLASS",teClass);
      ctx.writeValue("TECHCLASS",techClass);
      ctx.writeValue("DESC",desc);
      ctx.writeNumber("TESPECNO",teSpecNo);
      ctx.writeNumber("CURRROW",currRow);
      ctx.writeValue("DESCLONG",descLong);
      ctx.writeValue("SELCOU",selCou);
      ctx.writeValue("SELCOU1",selCou1);
      ctx.writeValue("ATT",att);
      ctx.writeValue("ATTDESC",attdesc);
      ctx.writeValue("VALTE",valte);
      ctx.writeValue("WHTE",whte);
      ctx.writeValue("VALNO",valno);
      ctx.writeValue("UNIT",unit);
      ctx.writeFlag("VALFLAG",valFlag);
      ctx.writeValue("CLOSEFLAG",closeFlag);
      ctx.writeFlag("CTSSHHTML",showHtmlPart);
   }

//-----------------------------------------------------------------------------
//-------------------------   UTILITY FUNCTIONS  ------------------------------
//-----------------------------------------------------------------------------


   public void  startup()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomCommand("GEN1","Technical_Search_Result_API.Delete_Result__");
      cmd.addParameter("TECHNICAL_SEARCH_NO","0");

      cmd = trans.addCustomCommand("GEN2","Technical_Search_Criteria_API.Delete_Criteria__");
      cmd.addParameter("TECHNICAL_SEARCH_NO","0");

      trans = mgr.perform(trans);
   }

//-----------------------------------------------------------------------------
//------------------------  CMDBAR SEARCH FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addEmptyQuery(headblk);
      q.includeMeta("ALL");
      mgr.submit(trans);

      techClass = headset.getValue("TECHNICAL_CLASS");
      if (mgr.isEmpty(headset.getValue("DESCRIPTION_LONG")))
         descLong = "";
      else
         descLong = headset.getValue("DESCRIPTION_LONG");
      if (mgr.isEmpty(headset.getValue("DESCRIPTION")))
         desc = "";
      else
         desc = headset.getValue("DESCRIPTION");
      if (mgr.isEmpty(headset.getValue("NSELECTEDCOUNTCLASS")))
         selCou = "";
      else
         selCou = headset.getValue("NSELECTEDCOUNTCLASS");

   }


   public void  okFind1()
   {
      ASPManager mgr = getASPManager();

      q = trans.addEmptyQuery(headblk);
      q.addWhereCondition("TECHNICAL_CLASS = ?"); // Bug 27913
      q.addParameter("TECHNICAL_CLASS", teClass);
      q.includeMeta("ALL");
      mgr.submit(trans);
// Bug 27913, start
      if( headset.countRows()==0 ) {
         mgr.showAlert(mgr.translate("NODATA: No data found."));
      }
      else {
      
// Bug 27913          while (!(teClass.equalsIgnoreCase(headset.getValue("TECHNICAL_CLASS"))))
// Bug 27913      {
// Bug 27913         headset.next();
// Bug 27913      }

          techClass = headset.getValue("TECHNICAL_CLASS");
   
          if (mgr.isEmpty(headset.getValue("DESCRIPTION_LONG")))
             descLong = "";
          else
             descLong   = headset.getValue("DESCRIPTION_LONG");
   
          if (mgr.isEmpty(headset.getValue("DESCRIPTION")))
             desc = "";
          else
             desc = headset.getValue("DESCRIPTION");
   
          if (mgr.isEmpty(headset.getValue("NSELECTEDCOUNTCLASS")))
             selCou = "";
          else
             selCou = headset.getValue("NSELECTEDCOUNTCLASS");
   
          trans.clear();
          startup();
      }
// Bug 27913, end
      trans.clear();
   }


   public void  okFindITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

// Bug Id 27913      String s_tec_no = String.valueOf(teSpecNo);
// start, Bug Id 27913
      String teSpecNoTemp="";
      if(frame == 5){
         itemset2.goTo( itemset2.getRowSelected());
         teSpecNoTemp = itemset2.getValue("TECHNICAL_SPEC_NO"); // Bug Id 27913
      }
// end, Bug Id 27913
      
      q = trans.addQuery(itemblk0);
      q.addWhereCondition("TECHNICAL_SPEC_NO = ?"); // Bug Id 27913
      q.addParameter("TECHNICAL_SPEC_NO", teSpecNoTemp);
      q.setOrderByClause("ROWTYPE,ATTRIB_NUMBER");
      q.includeMeta("ALL");

      mgr.submit(trans);

      if (mgr.commandBarActivated())
      {
         if (itemset0.countRows() == 0 && "ITEM0.OkFind".equals(mgr.readValue("__COMMAND")))
         {
            mgr.showAlert(mgr.translate("APPSRWBESTFITSEARCHWIZNODATA: No data found."));
            itemset0.clear();
         }
      }

   }
// Bug 27913, start
   public void CountFindITEM0()
   {
      ASPManager mgr = getASPManager();
      itemset2.storeSelections(); 
      itemset2.goTo( itemset2.getRowSelected());
      String teSpecNoTemp = itemset2.getValue("TECHNICAL_SPEC_NO");
      q = trans.addQuery(itemblk0);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("TECHNICAL_SPEC_NO = ?");
      q.addParameter("TECHNICAL_SPEC_NO", teSpecNoTemp);
      q.setOrderByClause("ROWTYPE,ATTRIB_NUMBER");
      mgr.submit(trans);
      itemlay0.setCountValue(toInt(itemset0.getValue("N")));
      itemset0.clear();
   }
// Bug 27913, end

   public void  okFindITEM1()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      q = trans.addQuery(itemblk1);
      q.addWhereCondition("TECHNICAL_SEARCH_NO = ?");
      q.addParameter("TECHNICAL_SEARCH_NO", techSeaNo);
      q.addWhereCondition("SEARCH_TYPE = '1'");
      q.includeMeta("ALL");

      mgr.submit(trans);

      if (mgr.commandBarActivated())
      {
         if (itemset1.countRows() == 0 && "ITEM1.OkFind".equals(mgr.readValue("__COMMAND")))
         {
            mgr.showAlert(mgr.translate("APPSRWBESTFITSEARCHWIZNODATA: No data found."));
            itemset1.clear();
         }
      }

   }


   public void  okFindITEM2()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      q = trans.addQuery(itemblk1);
      q.addWhereCondition("TECHNICAL_SEARCH_NO = ?");
      q.addParameter("TECHNICAL_SEARCH_NO", techSeaNo);
      if (frame == 2)
         q.addWhereCondition("SEARCH_TYPE = '1'");
      else
         q.addWhereCondition("SEARCH_TYPE = '2'");

      q.includeMeta("ALL");

      mgr.submit(trans);

      if (mgr.commandBarActivated())
      {
         if (itemset1.countRows() == 0 && "ITEM2.OkFind".equals(mgr.readValue("__COMMAND")))
         {
            mgr.showAlert(mgr.translate("APPSRWBESTFITSEARCHWIZNODATA: No data found."));
            itemset1.clear();
         }
      }

   }
// Bug 27913, start
        public void countFindITEM2()
        {
            ASPManager mgr = getASPManager();
            q = trans.addQuery(itemblk1);
            q.setSelectList("to_char(count(*)) N");
            q.addWhereCondition("TECHNICAL_SEARCH_NO = ?");
            q.addParameter("TECHNICAL_SEARCH_NO", techSeaNo);
            if(frame == 2)
              q.addWhereCondition("SEARCH_TYPE = '1'");
            else
              q.addWhereCondition("SEARCH_TYPE = '2'");
            mgr.submit(trans);
            itemlay1.setCountValue(toInt(itemset1.getValue("N")));
            itemset1.clear();
        }
// Bug 27913, end

   public void  okFindITEM3()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      q = trans.addQuery(itemblk2);
      q.addWhereCondition("TECHNICAL_SEARCH_NO = ?");
      q.addParameter("TECHNICAL_SEARCH_NO", techSeaNo);
      q.addWhereCondition("SILENT = 0");
      q.setOrderByClause("HIT_RATIO DESC");
      q.includeMeta("ALL");

      mgr.submit(trans);

      if (mgr.commandBarActivated())
      {
         if (itemset2.countRows() == 0 && "ITEM3.OkFind".equals(mgr.readValue("__COMMAND")))
         {
            mgr.showAlert(mgr.translate("APPSRWBESTFITSEARCHWIZNODATA: No data found."));
            itemset2.clear();
         }
      }

      int n = itemset2.countRows();
      itemset2.first();

      for (int i=1;i<=n;i++)
      {
         double n_hit_val = itemset2.getRow().getNumberValue("HIT_RATIO");
         mgr.getASPLog().trace("HIT_RATIO========="+itemset2.getRow().getNumberValue("HIT_RATIO"));
         mgr.getASPLog().trace("HIT_RATIO==2======="+itemset2.getValue("HIT_RATIO"));
         n_hit_val = n_hit_val * 100;

         String s_hit_val = mgr.formatNumber("HIT_RATIO",n_hit_val);

         String s_hit_val_per = s_hit_val + "%";


         ASPBuffer r = itemset2.getRow();
         r.setValue("HIT_RATIO_DUMMY",s_hit_val_per);

         itemset2.setRow(r);
         itemset2.next();
      }

      itemset2.first();

   }
   
// Bug 27913, start
   public void countFindITEM3()
   {
      ASPManager mgr = getASPManager();
      q = trans.addQuery(itemblk2);
      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("TECHNICAL_SEARCH_NO = ?");
      q.addParameter("TECHNICAL_SEARCH_NO", techSeaNo);
      q.addWhereCondition("SILENT = 0");
      q.setOrderByClause("HIT_RATIO DESC");
      mgr.submit(trans);
      itemlay2.setCountValue(toInt(itemset2.getValue("N")));
      itemset2.clear();
   }
// Bug 27913, end

//-----------------------------------------------------------------------------
//------------------------  CMDBAR BUTTON FUNCTIONS  --------------------------
//-----------------------------------------------------------------------------

   public void  nextForm()
   {
      ASPManager mgr = getASPManager();

      trans.clear();

      if (frame != 5)
         frame = frame + 1;

      if (frame == 2)
      {
// Bug 27913         String tclass = headset.getValue("TECHNICAL_CLASS");
         String techClass = mgr.readValue("TECHNICAL_CLASS");
         currRow = headset.getCurrentRowNo();

         cmd = trans.addCustomCommand("GEN3","Technical_Search_Criteria_API.Delete_Criteria__");
         cmd.addParameter("ITEM1_TECHNICAL_SEARCH_NO","0");

         if (mgr.isEmpty(techSeaNo))
         {
            cmd = trans.addCustomCommand("GEN4","Technical_Attrib_API.Copy_Attrib_To_Criteria_");
// Bug 27913            cmd.addParameter("ITEM1_TECHNICAL_CLASS",tclass);
            cmd.addParameter("ITEM1_TECHNICAL_CLASS",techClass); // Bug 27913
            cmd.addParameter("ITEM1_TECHNICAL_SEARCH_NO","0");
            trans = mgr.perform(trans);
            
            techSeaNo =trans.getValue("GEN4/DATA/TECHNICAL_SEARCH_NO");
            trans.clear();
         }
         trans = mgr.perform(trans);

         trans.clear();

         okFindITEM1();

         if (itemset1.countRows()>0)
         {
            att = itemset1.getValue("ATTRIBUTE");
            attdesc = itemset1.getValue("ITEM1_ATTR_DESC");
         }
      }
      if (frame == 3)
      {
// start, Bug Id 27913
         itemset1.changeRows();
         itemset1.storeSelections();
         mgr.submit(trans);  
         trans.clear();
// end, Bug Id 27913
         cmd = trans.addCustomCommand("GEN5","Technical_Search_Result_API.Delete_Result__");
         cmd.addParameter("ITEM1_TECHNICAL_SEARCH_NO",techSeaNo);

         cmd = trans.addCustomCommand("GEN6","Technical_Search_Criteria_API.Create_Query_");
         cmd.addParameter("ITEM1_TECHNICAL_SEARCH_NO",techSeaNo);
// Bug 27913         cmd.addParameter("ITEM1_TECHNICAL_CLASS",teClass);
         cmd.addParameter("ITEM1_TECHNICAL_CLASS",techClass); // Bug 27913

         trans = mgr.perform(trans);
         trans.clear();

         okFindITEM2();
         trans.clear();

         cmd = trans.addCustomFunction("GEN7","Technical_Search_Result_API.Selection_Count","SEL_COUN_NO");
         cmd.addParameter("ITEM1_TECHNICAL_SEARCH_NO",techSeaNo);
         trans = mgr.perform(trans);

         selCou1 = trans.getValue("GEN7/DATA/SEL_COUN_NO");

         if (itemset1.countRows()>0)
         {
            att = itemset1.getValue("ATTRIBUTE");
            attdesc = itemset1.getValue("ITEM1_ATTR_DESC");
            valno = itemset1.getValue("VALUE_NO");
            if (mgr.isEmpty(valno))
               valno = "";
            unit = itemset1.getValue("ITEM1_TECH_UNIT");
         }
      }

      if (frame == 4)
      {
// start, Bug Id 27913
         itemset1.changeRows();
         itemset1.storeSelections();
         mgr.submit(trans);  
         trans.clear();
// end, Bug Id 27913
         
         cmd = trans.addCustomCommand("GEN8","Technical_Search_Result_API.Calculate_Hit_Ratio__");
         cmd.addParameter("TECHNICAL_SEARCH_NO",techSeaNo);

         trans = mgr.perform(trans);
         trans.clear();

         okFindITEM3();
         trans.clear();

         cmd = trans.addCustomFunction("GEN9","Technical_Search_Result_API.Selection_Count","ITEM2_SEL_COUN_NO");
         cmd.addParameter("TECHNICAL_SEARCH_NO",techSeaNo);
         trans = mgr.perform(trans);

         selCou1 = trans.getValue("GEN9/DATA/ITEM2_SEL_COUN_NO");

         if (itemset2.countRows() == 0)
            errFlag = true;

         if (itemset2.countRows() != 0)
            teSpecNo = toInt(itemset2.getRow().getNumberValue("ITEM2_TECHNICAL_SPEC_NO"));
      }

      if (frame == 5)
      {
         itemset2.storeSelections(); 
         if ( itemset2.countSelectedRows()>1 )
         {
             mgr.showAlert(mgr.translate("MORETHANONESEL: Can not perform this operation for more than one record."));
             frame -= 1;
         }
         else
         {
            okFindITEM0();
         }      
      }
   }


   public void  previousForm()
   {
      if (frame != 1)
         frame = frame - 1;

      if (frame == 2)
         okFindITEM1();

      if (frame == 3)
         okFindITEM2();

      if (frame == 1)
         headset.goTo(currRow);
   }


   public void  finish()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addCustomCommand("GEN10","Technical_Search_Result_API.Delete_Result__");
      cmd.addParameter("ITEM1_TECHNICAL_SEARCH_NO",techSeaNo);

      cmd = trans.addCustomCommand("GEN11","Technical_Search_Criteria_API.Delete_Criteria__");
      cmd.addParameter("ITEM1_TECHNICAL_SEARCH_NO",techSeaNo);

      cmd = trans.addCustomCommand("GEN12","Technical_Search_Criteria_API.Delete_Criteria__");
      cmd.addParameter("ITEM1_TECHNICAL_SEARCH_NO",techSeaNo);

      cmd = trans.addCustomCommand("GEN13","Technical_Search_Result_API.Delete_Result__");
      cmd.addParameter("ITEM1_TECHNICAL_SEARCH_NO",techSeaNo);

      trans = mgr.perform(trans);
      trans.clear();
   }


   public void  finishForm()
   {

      finish();
      closeFlag = "TRUE";
   }


   public void  saveReturn()
   {
      ASPManager mgr = getASPManager();

      if (frame == 2)
      {
         String text = mgr.readValue("__VALTEXT1","");

         valte = "";

         if ("TRUE".equals(text))
            valte = "";
         else
         {
            String valte1 = mgr.readValue("__VALTEXT","");

            if (!(mgr.isEmpty(valte1)))
               valte = valte1;
         }

         String obj = itemset1.getRow().getFieldValue("ITEM1_OBJID");
         String objver = itemset1.getRow().getFieldValue("ITEM1_OBJVERSION");

         String attr = "VALUE_TEXT" + (char)31 + valte + (char)30;

         cmd = trans.addCustomCommand("MODI","TECHNICAL_SEARCH_CRITERIA_API.MODIFY__");
         cmd.addParameter("INFO");
         cmd.addParameter("OBJID",obj);
         cmd.addParameter("OBJVERSION",objver);
         cmd.addParameter("ATTR",attr);
         cmd.addParameter("ACTION","DO");

         trans = mgr.perform(trans);

         trans.clear();

         okFindITEM1();
      }

      if (frame == 3)
      {

         String text1 = mgr.readValue("__WHETEXT1","");

         whte = "";


         if ("TRUE".equals(text1))
            whte = "";
         else
         {
            String whte1 = mgr.readValue("__WHETEXT","");

            if (!(mgr.isEmpty(whte1)))
               whte = whte1;
         }   

         valno = mgr.readValue("VALUE_NO","");

         String obj = itemset1.getRow().getFieldValue("ITEM1_OBJID");
         String objver = itemset1.getRow().getFieldValue("ITEM1_OBJVERSION");

         String attr = "VALUE_NO" + (char)31 + valno + (char)30 + "WHERE_TEXT" + (char)31 + whte + (char)30;

         cmd = trans.addCustomCommand("MODIF","TECHNICAL_SEARCH_CRITERIA_API.MODIFY__");
         cmd.addParameter("INFO");
         cmd.addParameter("OBJID",obj);
         cmd.addParameter("OBJVERSION",objver);
         cmd.addParameter("ATTR",attr);
         cmd.addParameter("ACTION","DO");

         trans = mgr.perform(trans);

         trans.clear();

         okFindITEM2();
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

      f = headblk.addField("TECHNICAL_CLASS");
      f.setSize(14);
      f.setLabel("APPSRWBESTFITSEARCHWIZTECHNICALCLASS: Selected Technical Class:");
// Bug 27913      f.setCustomValidation("TECHNICAL_CLASS","TECHNICAL_CLASS");
      f.setUpperCase();
      f.setDynamicLOV("TECHNICAL_CLASS",600,450);

      f = headblk.addField("DESCRIPTION");
      f.setSize(25);
      f.setLabel("APPSRWBESTFITSEARCHWIZDESCRIPTION: Description");
      f.setUpperCase();
      f.setReadOnly();

      f = headblk.addField("DESCRIPTION_LONG");  
      f.setSize(50);
      f.setLabel("APPSRWBESTFITSEARCHWIZDESCRIPTION_LONG: Long Description1");
      f.setHeight(10);
      f.setReadOnly();

      f = headblk.addField("NSELECTEDCOUNTCLASS","Number");
      f.setSize(8);
      f.setLabel("APPSRWBESTFITSEARCHWIZNSELECTEDCOUNTCLASS: Selection Count");
      f.setFunction("Technical_Object_Reference_API.Get_Object_Count(:TECHNICAL_CLASS)");

      f = headblk.addField("TECHNICAL_SEARCH_NO","Number");
      f.setSize(8);
      f.setHidden();
      f.setFunction("0");

      headblk.setView("TECHNICAL_CLASS");
      headblk.defineCommand("TECHNICAL_CLASS_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.removeCommandGroup(headbar.CMD_GROUP_CUSTOM);
      headbar.disableMinimize();
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("APPSRWBESTFITSEARCHWIZHD: Best Fit Search"));  
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
      headlay.setEditable();

      //=========== Frame 5 ==========================================================================

      itemblk0 = mgr.newASPBlock("ITEM0");

      f = itemblk0 .addField("ITEM0_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk0 .addField("ITEM0_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk0 .addField("ITEM0_ATTRIB_NUMBER","Number");
      f.setSize(8);
      f.setDbName("ATTRIB_NUMBER");
      f.setLabel("APPSRWBESTFITSEARCHWIZATTBNUM: Order");

      f = itemblk0 .addField("ITEM0_TECHNICAL_CLASS");
      f.setSize(8);
      f.setDbName("TECHNICAL_CLASS");
      f.setHidden();

      f = itemblk0 .addField("ATTRIBUTE");
      f.setSize(7);
      f.setLabel("APPSRWBESTFITSEARCHWIZATTB: Attribute");
      f.setCustomValidation("ATTRIBUTE,ITEM0_TECHNICAL_CLASS","ITEM0_ATT_DESC,ITEM0_TECH_UNIT");
      f.setUpperCase();

      f = itemblk0 .addField("ITEM0_ATT_DESC");
      f.setSize(25);
      f.setLabel("APPSRWBESTFITSEARCHWIZITEM0ATTDESC: Attribute Description");
      f.setFunction("TECHNICAL_ATTRIB_STD_API.Get_Attrib_Desc(:ATTRIBUTE)");

      f = itemblk0 .addField("ITEM0_VALUE_TEXT");
      f.setSize(12);
      f.setLabel("APPSRWBESTFITSEARCHWIZVALTEXT: Value Text");
      f.setDbName("VALUE_TEXT");

      f = itemblk0 .addField("ITEM0_TECH_UNIT");
      f.setSize(8);
      f.setLabel("APPSRWBESTFITSEARCHWIZTECHUNIT0: Technical Unit");
      f.setFunction("TECHNICAL_ATTRIB_NUMERIC_API.Get_Technical_Unit_(:ITEM0_TECHNICAL_CLASS,:ATTRIBUTE)");
      f.setUpperCase();

      f = itemblk0 .addField("VALUE_NO","Number");
      f.setSize(8);
      f.setLabel("APPSRWBESTFITSEARCHWIZVALNO: Value No");

      f = itemblk0 .addField("LOWER_LIMIT","Number");
      f.setSize(8);
      f.setLabel("APPSRWBESTFITSEARCHWIZLOLIMIT: Lower Limit");
      f.unsetQueryable();

      f = itemblk0 .addField("UPPER_LIMIT","Number");
      f.setSize(8);
      f.setLabel("APPSRWBESTFITSEARCHWIZUPLIMIT: Upper Limit");
      f.unsetQueryable();

      f = itemblk0 .addField("ITEM0_INFO");
      f.setSize(8);
      f.setLabel("APPSRWBESTFITSEARCHWIZINFO: Info");
      f.setDbName("INFO");
      f.unsetQueryable();

      f = itemblk0 .addField("TECHNICAL_SPEC_NO","Number");
      f.setSize(8);
      f.setHidden();

      f = itemblk0 .addField("ROWTYPE");
      f.setSize(8);
      f.setHidden();

      f = itemblk0.addField("ITEM0_TECHNICAL_SEARCH_NO","Number");
      f.setSize(8);
      f.setHidden();
      f.setFunction("''");

      itemblk0 .setView("TECHNICAL_SPECIFICATION_BOTH");

      itemset0 = itemblk0.getASPRowSet();

      itembar0= mgr.newASPCommandBar(itemblk0 );
      itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
      itembar0.defineCommand(itembar0.COUNTFIND,"CountFindITEM0"); // Bug 27913
      itembar0.disableCommand(itembar0.EDITROW);
      itembar0.disableMinimize();

      itemtbl0 = mgr.newASPTable(itemblk0 );
      itemtbl0.setTitle(mgr.translate("APPSRWBESTFITSEARCHWIZITM0: Technical Specification"));  
      itemtbl0.disableQuickEdit();

      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);


      //========== Frame 2 & 3 =======================================================================   

      itemblk1= mgr.newASPBlock("ITEM1");

      f = itemblk1.addField("ITEM1_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk1.addField("ITEM1_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk1.addField("ITEM1_ATTRIBUTE");
      f.setSize(14);
      f.setCustomValidation("ATTRIBUTE,TECHNICAL_CLASS","ITEM1_ATTR_DESC,ITEM1_TECH_UNIT");
      f.setLabel("APPSRWBESTFITSEARCHWIZATTRB: Attribute");
      f.setDbName("ATTRIBUTE");
      f.setReadOnly();
      f.setUpperCase();

      f = itemblk1.addField("ITEM1_ATTR_DESC");
      f.setSize(25);
      f.setReadOnly();
      f.setLabel("APPSRWBESTFITSEARCHWIZATTRDESC: Attribute Description");
      f.setFunction("Technical_Attrib_Std_API.Get_Attrib_Desc(:ITEM1_ATTRIBUTE)");

      f = itemblk1.addField("ITEM1_VALUE_NO","Number");
      f.setSize(15);
      f.setLabel("APPSRWBESTFITSEARCHWIZVALNO1: Value No");
      f.setDbName("VALUE_NO");

      f = itemblk1.addField("ITEM1_TECH_UNIT");
      f.setSize(12);
      f.setReadOnly();
      f.setLabel("APPSRWBESTFITSEARCHWIZGENWEEK: Unit");
      f.setFunction("Technical_Attrib_Numeric_API.Get_Technical_Unit_(:ITEM1_TECHNICAL_CLASS,:ITEM1_ATTRIBUTE)");

      f = itemblk1.addField("ITEM1_VALUE_TEXT");
      f.setSize(15); // Bug Id 27913, Increased size.
      f.setLabel("APPSRWBESTFITSEARCHWIZVALTEXT1: Value Text");   
      f.setDbName("VALUE_TEXT");
      
// start, Bug Id 27913
      f = itemblk1.addField("WHERE_TEXT");
      f.setSize(17);
      f.setLabel("APPSRWBESTFITSEARCHWIZWHETEXT: Where");
      f.unsetQueryable();

      f = itemblk1.addField("ITEM1_VALUE_TEXT_OP");
      f.setSize(7);
      f.setLabel("OPERATOR: Operator");
      f.setFunction("''");
      f.setValidateFunction("setOperator");
      f.setSelectBox();

      f = itemblk1.addField("ITEM1_VALID_VALUES");
      f.setSize(10);
      f.setLabel("VALIDVALUES: Valid Values");
      f.setFunction("Technical_Attrib_Text_API.Get_Lov2(:ITEM1_TECHNICAL_CLASS,:ITEM1_ATTRIBUTE)");
      f.setHidden();
// end, Bug Id 27913

      f = itemblk1.addField("ITEM1_TECHNICAL_SEARCH_NO","Number");
      f.setDbName("TECHNICAL_SEARCH_NO");
      f.setHidden();

      f = itemblk1.addField("ITEM1_SEARCH_TYPE");
      f.setDbName("SEARCH_TYPE");
      f.setHidden();

      f = itemblk1.addField("ITEM1_TECHNICAL_CLASS");
      f.setDbName("TECHNICAL_CLASS");
      f.setHidden();

      f = itemblk1.addField("SEL_COUN_NO");
      f.setFunction("''");
      f.setHidden();

      f = itemblk1.addField("RESULT");
      f.setFunction("''");
      f.setHidden();

      itemblk1.setView("TECHNICAL_SEARCH_CRITERIA");
      itemblk1.defineCommand("TECHNICAL_SEARCH_CRITERIA_API","Modify__");

      itemset1 = itemblk1.getASPRowSet();

      itembar1= mgr.newASPCommandBar(itemblk1);

    //  itembar1.defineCommand(itembar1.SAVERETURN,"saveReturn");
   //   itembar1.defineCommand(itembar1.OKFIND,"okFindITEM2");
   //   itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM2"); // Bug 27913
      itembar1.disableCommand(itembar1.FIND);
      itembar1.disableCommand(itembar1.EDITROW);
      itembar1.disableMinimize();

      itemtbl1= mgr.newASPTable(itemblk1);
      itemtbl1.setTitle(mgr.translate("APPSRWBESTFITSEARCHWIZITM1: Technical Search Criteria"));  
      itemtbl1.setWrap();
      itemtbl1.setEditable();  //Bug Id 27913
      itemtbl1.disableQuickEdit();

      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);

      //========== frame 4 ============================================================================

      itemblk2 = mgr.newASPBlock("ITEM2");

      f = itemblk2.addField("ITEM2_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk2.addField("ITEM2_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk2.addField("OBJECT_ID");
      f.setSize(8);
      f.setLabel("APPSRWBESTFITSEARCHWIZOBJID: Object ID");
      f.setFunction("Technical_Object_Reference_API.Get_Lu_Name(:ITEM2_TECHNICAL_SPEC_NO)");

      f = itemblk2.addField("OBJECT_DESC");
      f.setSize(35);
      f.setLabel("APPSRWBESTFITSEARCHWIZOBJDESC: Object Description");
      f.setFunction("Technical_Object_Reference_API.Get_Instance_Description(:ITEM2_TECHNICAL_SPEC_NO)");

      f = itemblk2.addField("HIT_RATIO","Money");
      f.setSize(15);
      f.setHidden();
      f.setLabel("APPSRWBESTFITSEARCHWIZHITRAT: Hit Ratio");

      f = itemblk2.addField("HIT_RATIO_DUMMY");
      f.setSize(15);
      f.setLabel("APPSRWBESTFITSEARCHWIZHITRAT: Hit Ratio");
      f.setFunction("''");

      f = itemblk2.addField("ITEM2_TECHNICAL_SEARCH_NO","Number");
      f.setSize(15);
      f.setHidden();
      f.setDbName("TECHNICAL_SEARCH_NO");

      f = itemblk2.addField("ITEM2_TECHNICAL_SPEC_NO","Number");
      f.setSize(17);
      f.setHidden();
      f.setDbName("TECHNICAL_SPEC_NO");

      f = itemblk2.addField("KEY_REF");
      f.setSize(15);
      f.setHidden();
      f.setFunction("Technical_Object_Reference_API.Get_Key_Ref(:ITEM2_TECHNICAL_SPEC_NO)");

      f = itemblk2.addField("LU_NAME");
      f.setSize(15);
      f.setHidden();
      f.setFunction("Technical_Object_Reference_API.Get_Lu_Name(:ITEM2_TECHNICAL_SPEC_NO)");

      f = itemblk2.addField("SILENT");
      f.setSize(15);
      f.setHidden();

      f = itemblk2.addField("ITEM2_SEL_COUN_NO");
      f.setFunction("''");
      f.setHidden();

      itemblk2.setView("TECHNICAL_SEARCH_RESULT");
      itemblk2.defineCommand("TECHNICAL_SEARCH_RESULT_API"," ");

      itemset2 = itemblk2.getASPRowSet();
      itembar2= mgr.newASPCommandBar(itemblk2);
      itembar2.defineCommand(itembar2.OKFIND,"okFindITEM3");
      itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM3"); // Bug 27913
      itembar2.disableMinimize();

      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.setTitle(mgr.translate("APPSRWBESTFITSEARCHWIZITM2: Technical Search Result"));  
      itemtbl2.setWrap();
      itemtbl2.disableQuickEdit();
      
      itemtbl2.enableRowSelect(); // Bug Id 27913

      itemlay2 = itemblk2.getASPBlockLayout();
      itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);


      //========================TEMP BLK=====================================================================

      tempblk1 = mgr.newASPBlock("TEMP1");

      tempblk1.addField("INFO");
      tempblk1.addField("ATTR");
      tempblk1.addField("ACTION");
   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();

// start, Bug Id 27913
      // Translate operators for frames 2 and 3.
      if((frame == 2) || (frame == 3) ) {
         not_equal_to = mgr.translate("NOTEQUALTO: != (not equal to)");
         between = mgr.translate("BETWEEN: .. (between)");
         less_than = mgr.translate("LESSTHAN: < (less than)");
         less_or_equal = mgr.translate("LESSOREQUAL: <= (less or equal)");
         equal_to = mgr.translate("EQUALTO: = (equal to)");
         greater_than = mgr.translate("GREATERTHAN: > (greater than)");
         greater_or_equal = mgr.translate("GREATEROREQUAL: >= (greater or equal)");
      }
// end, Bug Id 27913

      if ((frame == 2) && itemlay1.isEditLayout())
      {
         String attrb = itemset1.getRow().getFieldValue("ITEM1_ATTRIBUTE");
         att = itemset1.getRow().getFieldValue("ATTRIBUTE");
         attdesc = itemset1.getRow().getFieldValue("ITEM1_ATTR_DESC");

         cmd = trans.addCustomCommand("LIST1","Technical_Attrib_Text_API.Get_Lov");
         cmd.addParameter("ITEM1_TECHNICAL_CLASS",teClass);
         cmd.addParameter("ITEM1_ATTRIBUTE",attrb);
         cmd.addParameter("RESULT");

         trans = mgr.perform(trans);

         String result = trans.getValue("LIST1/DATA/RESULT");

         bur = mgr.newASPBuffer();

         ASPBuffer row = bur.addBuffer("0");
         row.addItem("VALUE_TEXT_TXT","!=");
         row.addItem("VALUE_TEXT_TXT","!=");
         row = bur.addBuffer("1");
         row.addItem("VALUE_TEXT_TXT","=");
         row.addItem("VALUE_TEXT_TXT","=");

         int index = 2;
         int beg_pos = 0;

         while (!mgr.isEmpty(result))
         {
            int end_pos = result.indexOf((char)94);
            String res = result.substring(beg_pos,end_pos);

            String s_index = String.valueOf(index);

            row = bur.addBuffer(s_index);
            row.addItem("VALUE_TEXT_TXT",res);
            row.addItem("VALUE_TEXT_TXT",res);

            result = result.substring(end_pos+1);

            index = index + 1;
         }

         valte = itemset1.getValue("VALUE_TEXT");
      }

      if ((frame == 3) && itemlay1.isEditLayout())
      {
         att = itemset1.getRow().getFieldValue("ATTRIBUTE");
         attdesc = itemset1.getRow().getFieldValue("ITEM1_ATTR_DESC");

         String symbol1 = "!=";
         String symbol2 = "..";
         String symbol3 = "<";
         String symbol4 = "<=";
         String symbol5 = "=";
         String symbol6 = ">";
         String symbol7 = ">=";

         buff = mgr.newASPBuffer();

         ASPBuffer row = buff.addBuffer("0");
         row.addItem("WHERE_TEXT",symbol1);
         row.addItem("WHERE_TEXT",symbol1);
         row = buff.addBuffer("1");
         row.addItem("WHERE_TEXT",symbol2);
         row.addItem("WHERE_TEXT",symbol2);
         row = buff.addBuffer("2");
         row.addItem("WHERE_TEXT",symbol3);
         row.addItem("WHERE_TEXT",symbol3);
         row = buff.addBuffer("3");
         row.addItem("WHERE_TEXT",symbol4);
         row.addItem("WHERE_TEXT",symbol4);
         row = buff.addBuffer("4");
         row.addItem("WHERE_TEXT",symbol5);
         row.addItem("WHERE_TEXT",symbol5);
         row = buff.addBuffer("5");
         row.addItem("WHERE_TEXT",symbol6);
         row.addItem("WHERE_TEXT",symbol6);
         row = buff.addBuffer("6");
         row.addItem("WHERE_TEXT",symbol7);
         row.addItem("WHERE_TEXT",symbol7);

         whte = itemset1.getValue("WHERE_TEXT");
      }

      if (frame == 1)
      {
         headblk.setTitle(mgr.translate("APPSRWBESTFITSEARCHWIZHEADTITLE: Best Fit Search - "+desc));    

         //mgr.getASPField("DESCRIPTION").setHidden();
         //mgr.getASPField("DESCRIPTION_LONG").setHidden();
         mgr.getASPField("NSELECTEDCOUNTCLASS").setHidden();     

         mgr.getASPField("ATTRIBUTE").setHidden();
         mgr.getASPField("ITEM0_ATT_DESC").setHidden();
         mgr.getASPField("ITEM0_VALUE_TEXT").setHidden();
         mgr.getASPField("ITEM0_TECH_UNIT").setHidden();    
         mgr.getASPField("VALUE_NO").setHidden();
         mgr.getASPField("LOWER_LIMIT").setHidden();                 
         mgr.getASPField("UPPER_LIMIT").setHidden();
         mgr.getASPField("INFO").setHidden();
         mgr.getASPField("TECHNICAL_SPEC_NO").setHidden();

         mgr.getASPField("ITEM1_ATTRIBUTE").setHidden();
         mgr.getASPField("ITEM1_ATTR_DESC").setHidden();
         mgr.getASPField("ITEM1_TECH_UNIT").setHidden();    
         mgr.getASPField("ITEM1_VALUE_TEXT").setHidden();
         mgr.getASPField("WHERE_TEXT").setHidden();                
         mgr.getASPField("ITEM1_VALUE_NO").setHidden();

         mgr.getASPField("OBJECT_ID").setHidden();
         mgr.getASPField("OBJECT_DESC").setHidden();     
         mgr.getASPField("HIT_RATIO_DUMMY").setHidden();
      }
      if (frame == 2)
      {
// start, Bug Id 27913
         int nCurRowNo = itemset1.getCurrentRowNo();
         int nNoOfRows = itemset1.countRows();
         itemset1.first();
         sValidValues = "";
         for (int k=0; k<nNoOfRows; k++) {
            itemset1.goTo(k);
            if (k != 0 ) {
               sValidValues += "^#^";
            }
            sValidValues += itemset1.getValue("ITEM1_VALID_VALUES");
            itemset1.next();
         }
         itemset1.goTo(nCurRowNo);
//end, Bug Id 27913
         itemblk1.setTitle(mgr.translate("APPSRWBESTFITSEARCHWIZITEMTITLE0: Best Fit Search - "+desc));    

         mgr.getASPField("TECHNICAL_CLASS").setHidden();
         mgr.getASPField("DESCRIPTION").setHidden();     
         mgr.getASPField("DESCRIPTION_LONG").setHidden();
         mgr.getASPField("NSELECTEDCOUNTCLASS").setHidden();   

         mgr.getASPField("ATTRIBUTE").setHidden();
         mgr.getASPField("ITEM0_ATT_DESC").setHidden();
         mgr.getASPField("ITEM0_VALUE_TEXT").setHidden();
         mgr.getASPField("ITEM0_TECH_UNIT").setHidden();    
         mgr.getASPField("VALUE_NO").setHidden();
         mgr.getASPField("LOWER_LIMIT").setHidden();                 
         mgr.getASPField("UPPER_LIMIT").setHidden();
         mgr.getASPField("INFO").setHidden();
         mgr.getASPField("TECHNICAL_SPEC_NO").setHidden();

         mgr.getASPField("ITEM1_TECH_UNIT").setHidden();    
         mgr.getASPField("WHERE_TEXT").setHidden();                  
         mgr.getASPField("ITEM1_VALUE_NO").setHidden();

         mgr.getASPField("OBJECT_ID").setHidden();
         mgr.getASPField("OBJECT_DESC").setHidden();     
         mgr.getASPField("HIT_RATIO_DUMMY").setHidden();
      }
      if (frame == 3)
      {
         itemblk1.setTitle(mgr.translate("APPSRWBESTFITSEARCHWIZITEMTITLE1: Best Fit Search - "+desc));    

         mgr.getASPField("TECHNICAL_CLASS").setHidden();
         mgr.getASPField("DESCRIPTION").setHidden();     
         mgr.getASPField("DESCRIPTION_LONG").setHidden();
         mgr.getASPField("NSELECTEDCOUNTCLASS").setHidden();   

         mgr.getASPField("ATTRIBUTE").setHidden();
         mgr.getASPField("ITEM0_ATT_DESC").setHidden();
         mgr.getASPField("ITEM0_VALUE_TEXT").setHidden();
         mgr.getASPField("ITEM0_TECH_UNIT").setHidden();    
         mgr.getASPField("VALUE_NO").setHidden();
         mgr.getASPField("LOWER_LIMIT").setHidden();                 
         mgr.getASPField("UPPER_LIMIT").setHidden();
         mgr.getASPField("INFO").setHidden();
         mgr.getASPField("TECHNICAL_SPEC_NO").setHidden();

         mgr.getASPField("ITEM1_VALUE_TEXT").setHidden();

         mgr.getASPField("OBJECT_ID").setHidden();
         mgr.getASPField("OBJECT_DESC").setHidden();     
         mgr.getASPField("HIT_RATIO_DUMMY").setHidden();
      }
      if (frame == 4)
      {
         itemblk2.setTitle(mgr.translate("APPSRWBESTFITSEARCHWIZITEMTITLE2: Best Fit Search - "+desc));    

         mgr.getASPField("TECHNICAL_CLASS").setHidden();
         mgr.getASPField("DESCRIPTION").setHidden();     
         mgr.getASPField("DESCRIPTION_LONG").setHidden();
         mgr.getASPField("NSELECTEDCOUNTCLASS").setHidden();   

         mgr.getASPField("ATTRIBUTE").setHidden();
         mgr.getASPField("ITEM0_ATT_DESC").setHidden();
         mgr.getASPField("ITEM0_VALUE_TEXT").setHidden();
         mgr.getASPField("ITEM0_TECH_UNIT").setHidden();    
         mgr.getASPField("VALUE_NO").setHidden();
         mgr.getASPField("LOWER_LIMIT").setHidden();                 
         mgr.getASPField("UPPER_LIMIT").setHidden();
         mgr.getASPField("INFO").setHidden();
         mgr.getASPField("TECHNICAL_SPEC_NO").setHidden();

         mgr.getASPField("ITEM1_ATTRIBUTE").setHidden();
         mgr.getASPField("ITEM1_ATTR_DESC").setHidden();
         mgr.getASPField("ITEM1_TECH_UNIT").setHidden();    
         mgr.getASPField("ITEM1_VALUE_TEXT").setHidden();
         mgr.getASPField("WHERE_TEXT").setHidden();                  
         mgr.getASPField("ITEM1_VALUE_NO").setHidden();
      }
      if (frame == 5)
      {
         itemblk0.setTitle(mgr.translate("APPSRWBESTFITSEARCHWIZHEADTITLE: Best Fit Search - "+desc));    

         mgr.getASPField("TECHNICAL_CLASS").setHidden();
         mgr.getASPField("DESCRIPTION").setHidden();     
         mgr.getASPField("DESCRIPTION_LONG").setHidden();
         mgr.getASPField("NSELECTEDCOUNTCLASS").setHidden();   

         mgr.getASPField("ITEM1_ATTRIBUTE").setHidden();
         mgr.getASPField("ITEM1_ATTR_DESC").setHidden();
         mgr.getASPField("ITEM1_TECH_UNIT").setHidden();    
         mgr.getASPField("ITEM1_VALUE_TEXT").setHidden();
         mgr.getASPField("WHERE_TEXT").setHidden();                  
         mgr.getASPField("ITEM1_VALUE_NO").setHidden();

         mgr.getASPField("OBJECT_ID").setHidden();
         mgr.getASPField("OBJECT_DESC").setHidden();     
         mgr.getASPField("HIT_RATIO_DUMMY").setHidden();
      }

      url = mgr.getConfigParameter("APPLICATION/LOCATION/ROOT");
   }


//===============================================================
//  HTML
//===============================================================

   protected AutoString getContents() throws FndException
   { 
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>");
      out.append(mgr.generateHeadTag("APPSRWBESTFITSEARCHWIZTITLE: Best Fit Search"));
      out.append("<title></title>\n");
      out.append("</head>\n");
      if (showHtmlPart)
      {
         out.append("<body ");
         out.append(mgr.generateBodyTag());
         out.append(">\n");
         out.append("<form ");
         out.append(mgr.generateFormTag());
         out.append(">\n");
         out.append("<input type=\"hidden\" name=\"__SEARCHFLAG\" value>\n");
         out.append("<input type=\"hidden\" name=\"__TECLASS\" value>\n");
         out.append("<input type=\"hidden\" name=\"__VALTEXT\" value>\n");
         out.append("<input type=\"hidden\" name=\"__WHETEXT\" value>\n");
         out.append("<input type=\"hidden\" name=\"__WHETEXT1\" value>\n");
         out.append("<input type=\"hidden\" name=\"__VALTEXT1\" value>\n");
         out.append("<input type=\"hidden\" name=\"__VALNO\" value>\n");
         out.append(mgr.startPresentation("APPSRWBESTFITSEARCHWIZTITLE: Best Fit Search"));

         if ((frame != 2) && (frame != 3) && (frame != 4) && (frame != 5))
         {
            out.append(headbar.showBar());
         }
         if (headlay.isVisible())
         {

            if (frame == 1)
            {

               out.append(headlay.generateDataPresentation());
               /*out.append("  <table id=mainTBL class=\"BlockLayoutTable\" border=\"0\" width=\"750\">\n");
               out.append("    <tr>\n");
               out.append("     <td width=\"450\">\n");
               out.append("       <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"450\">\n");
               out.append("        <tr>\n");
               out.append("         <td colspan=\"3\">\n");
               out.append("        <table>\n");
               out.append("          <tr>\n");
               out.append("            <td nowrap width=\"110\" align=\"left\">");
               out.append(headlay.generateDataPresentation());
               out.append("            </td>\n");    
               out.append("            <td nowrap width=\"110\" valign=\"top\">");
               out.append(fmt.drawWriteLabel(mgr.translate("APPSRWBESTFITSEARCHWIZDESC: Description")));
               out.append("            <img src=\"../common/images/table_empty_image.gif\" width=\"1\" height=\"4\"></td>\n");
               out.append("            <td width=\"0\" valign=\"top\">");
               out.append(fmt.drawReadValue(desc));
               out.append("            </td></tr>\n");     
               out.append("         </table>\n");
               out.append("                      </td>\n");
               out.append("         </tr>\n");
               out.append("         <tr>        \n");    
               out.append("            <td nowrap width=\"0*\" align=\"left\" valign=\"top\">&nbsp;");
               out.append(fmt.drawWriteLabel(mgr.translate("APPSRWBESTFITSEARCHWIZDESCLONG: Long Description")));
               out.append("<img src=\"../common/images/table_empty_image.gif\" width=\"1\" height=\"4\"></td><td width=\"100\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>\n");
               out.append("            <td width=\"0*\" valign=\"top\" align=\"left\">");
               out.append(fmt.drawTextArea("DESCRIPTION_LONG",descLong,"readOnly",10,88));
               out.append("            </td>\n");
               out.append("            <td nowrap halign=\"right\"><img src=\"../common/images/table_empty_image.gif\" width=\"1\" height=\"4\"></td>");
               out.append("         </tr>\n");
               out.append("        </table>\n");
               out.append("      </td>\n");
               out.append("    </tr>\n");
               out.append("  </table>\n");*/
            }

         }if (frame == 2)
         {

            out.append(itembar1.showBar());
           // if (!itemlay1.isEditLayout())
           // {
               out.append(itemlay1.generateDataPresentation());
           /* }
            else
            {
               out.append("   <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=525>\n");
               out.append("      <tr>\n");
               out.append("         <td nowrap width=110 align=\"left\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.append(fmt.drawWriteLabel(mgr.translate("APPSRWBESTFITSEARCHWIZATTRIB: Attribute")));
               out.append("         </td>\n");
               out.append("         <td width=0 align=\"left\">");
               out.append(fmt.drawReadValue(att));
               out.append("         </td>\n");
               out.append("         <td nowrap width=150 align=\"left\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.append(fmt.drawWriteLabel(mgr.translate("APPSRWBESTFITSEARCHWIZATTRDESC: Attribute Description")));
               out.append("         </td>\n");
               out.append("         <td nowrap width=0 align=\"left\">");
               out.append(fmt.drawReadValue(attdesc));
               out.append("         </td>\n");
               out.append("      </tr>\n");
               out.append("      <tr>\n");
               out.append("         <td nowrap width=110 align=\"left\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.append(fmt.drawWriteLabel(mgr.translate("APPSRWBESTFITSEARCHWIZVALTEXT: Value Text")));
               out.append("         </td>\n");
               out.append("         <td width=0 align=\"left\">");
               out.append(fmt.drawSelect("VALUE_TEXT_TXT",bur,"","onChange=Sub()"));
               out.append(fmt.drawTextField("SERACH_TEXT","","onBlur=Sub1()"));
               out.append("         </td>\n");
               out.append("      </tr>\n");
               out.append("   </table>\n");
            }*/

         }if (frame == 3)
         {
            out.append(itembar1.showBar());
            //if (!itemlay1.isEditLayout())
            //{
               out.append(itemlay1.generateDataPresentation());
            /*}
            else
            {
               out.append("   <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=525>\n");
               out.append("      <tr>\n");
               out.append("         <td nowrap width=110 align=\"left\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.append(fmt.drawWriteLabel(mgr.translate("APPSRWBESTFITSEARCHWIZATTRIB: Attribute")));
               out.append("         </td>\n");
               out.append("         <td width=0 align=\"left\">");
               out.append(fmt.drawReadValue(att));
               out.append("         </td>\n");
               out.append("         <td nowrap width=150 align=\"left\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.append(fmt.drawWriteLabel(mgr.translate("APPSRWBESTFITSEARCHWIZATTRDESC: Attribute Description")));
               out.append("         </td>\n");
               out.append("         <td nowrap width=0 align=\"left\">");
               out.append(fmt.drawReadValue(attdesc));
               out.append("         </td>\n");
               out.append("      </tr>\n");
               out.append("      <tr>\n");
               out.append("         <td nowrap width=110 align=\"left\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.append(fmt.drawWriteLabel(mgr.translate("APPSRWBESTFITSEARCHWIZVALNO: Value No")));
               out.append("         </td>\n");
               out.append("         <td width=0 align=\"left\">");
               out.append(fmt.drawTextField("VALUE_NO",valno,"",18,35));
               out.append("         </td>\n");
               out.append("         <td nowrap width=150 align=\"left\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.append(fmt.drawWriteLabel(mgr.translate("APPSRWBESTFITSEARCHWIZUNIT1: Technical Unit")));
               out.append("         </td>\n");
               out.append("         <td nowrap width=0 align=\"left\">");
               out.append(fmt.drawReadValue(unit));
               out.append("         </td>\n");
               out.append("      </tr>\n");
               out.append("      <tr>\n");
               out.append("         <td nowrap width=110 align=\"left\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.append(fmt.drawWriteLabel(mgr.translate("APPSRWBESTFITSEARCHWIZWHERETEXT: Where Text")));
               out.append("         </td>\n");
               out.append("         <td width=0 align=\"left\">");
               out.append(fmt.drawSelect("WHERE_TEXT",buff,"","onChange=Sub3()"));
               out.append(fmt.drawTextField("SERACH_TEXT1","","onBlur=Sub2()"));
               out.append("         </td>\n");
               out.append("      </tr>\n");
               out.append("   </table>\n");
            }*/

         }if (frame == 4)
         {
            out.append(itembar2.showBar());
            out.append(itemlay2.generateDataPresentation());

         }if (frame == 5)
         {
            out.append(itembar0.showBar());
            out.append(itemlay0.generateDataPresentation());
         }
         if (!((headlay.isEditLayout() || itemlay0.isEditLayout() || itemlay1.isEditLayout() || itemlay2.isEditLayout()) || (headlay.isFindLayout() || itemlay0.isFindLayout() || itemlay1.isFindLayout() || itemlay2.isFindLayout())))
         {
            out.append("<table id=\"buttonTBL\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"*0\">\n");
            out.append("   <tr>\n");

            if (frame != 5)
            {
               out.append("         <td  align=\"left\">&nbsp;");
               out.append(fmt.drawWriteLabel(mgr.translate("APPSRWBESTFITSEARCHWIZNSELECTEDCOUNTCLASS: Selection Count")));
               out.append("         &nbsp;");
               //out.append("         &nbsp;</td>\n");
               //out.append("         <td width=\"9\" align=\"right\">");

               if ((frame == 1) || (frame == 2))
               {
                  out.append(fmt.drawReadValue(selCou));
               }
               else
               {
                  out.append(fmt.drawReadValue(selCou1));
               }
               out.append("         </td>\n");                                         
            }
            
            out.append("         </tr>\n");
            out.append("         <tr>\n");
            if (frame == 1)
            {
               out.append("<td align=\"right\" width=\"665\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>\n");
               out.append("    <td align=\"right\">\n");                     
            }
            else if (frame == 5)
            {
               out.append("<td align=\"right\" width=\"570\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>\n");
               out.append("    <td align=\"right\">\n"); 
            }
            else if ((frame == 4)&&(errFlag))
            {
               out.append("<td align=\"right\" width=\"570\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>\n");
               out.append("    <td align=\"right\">\n"); 
            }
            else
            {
               out.append("<td align=\"right\" width=\"525\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>\n");
               out.append("    <td align=\"right\">\n");   
            }

            if (frame != 1)
            {
               out.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.append(fmt.drawSubmit("PREVIOUS",mgr.translate("APPSRWBESTFITSEARCHWIZPREVIOUSBUT: <Previous"),"PREVIOUS"));
               out.append("&nbsp;");
            }

            if (( frame != 5 ) && !(errFlag))
            {
               out.append(fmt.drawSubmit("NEXT",mgr.translate("APPSRWBESTFITSEARCHWIZNEXBUT: Next> "),""));
               out.append("&nbsp;");
            }
            if (frame > 4)
            {
               out.append(fmt.drawSubmit("FINISH",mgr.translate("APPSRWBESTFITSEARCHWIZFINBUT: Finish"),""));
               out.append("&nbsp;");
            }
            out.append(fmt.drawSubmit("CANCEL",mgr.translate("APPSRWBESTFITSEARCHWIZCANBUT: Cancel"),"Cancel"));
            out.append("    </td>\n");
            out.append("  </tr>\n");
            out.append("</table>\n");
         }

         appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
         appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
         appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
// start, Bug Id 27913
         if (frame==2) {
            appendDirtyJavaScript("if (f.ITEM1_VALUE_TEXT_OP!=null) {\n");
            appendDirtyJavaScript("    for (var i = 1; i<f.ITEM1_VALUE_TEXT_OP.length; i++) {\n");
            appendDirtyJavaScript("       f.ITEM1_VALUE_TEXT_OP[i].options[0] = new Option(\"\", \"\");\n");
            appendDirtyJavaScript("       f.ITEM1_VALUE_TEXT_OP[i].options[1] = new Option(\"" + not_equal_to + "\", \"!=\");\n");
            appendDirtyJavaScript("       f.ITEM1_VALUE_TEXT_OP[i].options[2] = new Option(\"" + equal_to + "\", \"=\");\n");
            appendDirtyJavaScript("    }\n");
            appendDirtyJavaScript("   var sValidValues = \"" + sValidValues + "\";\n");
            appendDirtyJavaScript("   var arrayValidValues = sValidValues.split(\"^#^\");\n");
            appendDirtyJavaScript("   var nMaxLength = 3;\n");
            appendDirtyJavaScript("   var sFirstOption = \"__\";\n");
            appendDirtyJavaScript("   if (f.ITEM1_VALUE_TEXT_OP[1].options[1].value.length > f.ITEM1_VALUE_TEXT_OP[1].options[2].value.length)\n");
            appendDirtyJavaScript("       nMaxLength = f.ITEM1_VALUE_TEXT_OP[1].options[1].value.length;\n");
            appendDirtyJavaScript("   else\n");
            appendDirtyJavaScript("       nMaxLength = f.ITEM1_VALUE_TEXT_OP[1].options[2].value.length;\n");
            appendDirtyJavaScript("   for (var i = 1; i<f.ITEM1_VALUE_TEXT_OP.length; i++) {\n");
            appendDirtyJavaScript("      options = arrayValidValues[i-1];\n");
            appendDirtyJavaScript("        if (options.charAt(options.length-1) == \"^\" ) {\n");
            appendDirtyJavaScript("           options = options.substring(0,options.length-1);\n");
            appendDirtyJavaScript("        }\n");
            appendDirtyJavaScript("        arrayOfOptions = options.split(\"^\");\n");
            appendDirtyJavaScript("        for (var j = 0; j < arrayOfOptions.length; j++) {\n");
            appendDirtyJavaScript("           f.ITEM1_VALUE_TEXT_OP[i].options[f.ITEM1_VALUE_TEXT_OP[i].options.length] = new Option(arrayOfOptions[j], arrayOfOptions[j]);\n");
            appendDirtyJavaScript("           if (nMaxLength < arrayOfOptions[j].length) {\n");
            appendDirtyJavaScript("              nMaxLength = arrayOfOptions[j].length;\n");
            appendDirtyJavaScript("           }\n");
            appendDirtyJavaScript("        }\n");
            appendDirtyJavaScript("   }\n");
            appendDirtyJavaScript("   for (var i=0; i<nMaxLength; i++) {\n");
            appendDirtyJavaScript("     sFirstOption = sFirstOption + \"_\";\n");
            appendDirtyJavaScript("   }\n");
            appendDirtyJavaScript("   for (var i = 1; i<f.ITEM1_VALUE_TEXT_OP.length; i++) {\n");
            appendDirtyJavaScript("     f.ITEM1_VALUE_TEXT_OP[i].options[0] = new Option(sFirstOption, \"\");\n");
            appendDirtyJavaScript("     f.ITEM1_VALUE_TEXT_OP[i].selectedIndex = 0;\n");
            appendDirtyJavaScript("   }\n");
            appendDirtyJavaScript("}\n");
         }
         else if (frame==3) {
            appendDirtyJavaScript("if (f.ITEM1_VALUE_TEXT_OP!=null) {\n");
            appendDirtyJavaScript(" for (var i = 1; i<f.ITEM1_VALUE_TEXT_OP.length; i++) {\n");
            appendDirtyJavaScript("   f.ITEM1_VALUE_TEXT_OP[i].options[0] = new Option(\"\",", "\"\");\n");
            appendDirtyJavaScript("   f.ITEM1_VALUE_TEXT_OP[i].options[1] = new Option(\"" + not_equal_to + "\", \"!=\");\n");
            appendDirtyJavaScript("   f.ITEM1_VALUE_TEXT_OP[i].options[2] = new Option(\"" + between + "\", \"..\");\n");
            appendDirtyJavaScript("   f.ITEM1_VALUE_TEXT_OP[i].options[3] = new Option(\"" + less_than + "\", \"<\");\n");
            appendDirtyJavaScript("   f.ITEM1_VALUE_TEXT_OP[i].options[4] = new Option(\"" + less_or_equal + "\", \"<=\");\n");
            appendDirtyJavaScript("   f.ITEM1_VALUE_TEXT_OP[i].options[5] = new Option(\"" + equal_to + "\", \"=\");\n");
            appendDirtyJavaScript("   f.ITEM1_VALUE_TEXT_OP[i].options[6] = new Option(\"" + greater_than + "\", \">\");\n");
            appendDirtyJavaScript("   f.ITEM1_VALUE_TEXT_OP[i].options[7] = new Option(\"" + greater_or_equal + "\", \">=\");\n");
            appendDirtyJavaScript(" }\n");
            appendDirtyJavaScript("}\n");
         }

         appendDirtyJavaScript("function setOperator(i)\n");
         appendDirtyJavaScript("{\n");
         if (frame==2) {
            appendDirtyJavaScript("   var sSelectedOp = f.ITEM1_VALUE_TEXT_OP[i].options[f.ITEM1_VALUE_TEXT_OP[i].selectedIndex].value;");
            appendDirtyJavaScript("   if (sSelectedOp==\"!=\" || sSelectedOp==\"=\") {");
            appendDirtyJavaScript("       if (f.ITEM1_VALUE_TEXT[i].value == \"\") // This is the first selection\n");
            appendDirtyJavaScript("          f.ITEM1_VALUE_TEXT[i].value = sSelectedOp;\n");
            appendDirtyJavaScript("       else\n");
            appendDirtyJavaScript("          f.ITEM1_VALUE_TEXT[i].value += \";\" + sSelectedOp;\n");
            appendDirtyJavaScript("   } else {");
            appendDirtyJavaScript("       // Get the last char of ITEM1_VALUE_TEXT[i].value\n");
            appendDirtyJavaScript("       var lastChar = \"\";\n");
            appendDirtyJavaScript("       var valueText = f.ITEM1_VALUE_TEXT[i].value;\n");
            appendDirtyJavaScript("       var length = valueText.length;\n");
            appendDirtyJavaScript("       if (length > 0)\n");
            appendDirtyJavaScript("          lastChar = valueText.substr(length-1, 1);\n");
            appendDirtyJavaScript("       if (f.ITEM1_VALUE_TEXT[i].value == \"\") // This is the first selection\n");
            appendDirtyJavaScript("          f.ITEM1_VALUE_TEXT[i].value = sSelectedOp;\n");
            appendDirtyJavaScript("       else if (lastChar == \"=\")\n");
            appendDirtyJavaScript("          f.ITEM1_VALUE_TEXT[i].value += sSelectedOp;\n");
            appendDirtyJavaScript("       else\n");
            appendDirtyJavaScript("          f.ITEM1_VALUE_TEXT[i].value += \";\" + sSelectedOp;\n");
            appendDirtyJavaScript("   }");
            appendDirtyJavaScript("   f.ITEM1_VALUE_TEXT_OP[i].selectedIndex = 0;\n");
         } else if (frame==3) {
            appendDirtyJavaScript("   if (f.WHERE_TEXT[i].value == \"\") // This is the first selection\n");
            appendDirtyJavaScript("      f.WHERE_TEXT[i].value = f.ITEM1_VALUE_TEXT_OP[i].options[f.ITEM1_VALUE_TEXT_OP[i].selectedIndex].value;\n");
            appendDirtyJavaScript("   else\n");
            appendDirtyJavaScript("      f.WHERE_TEXT[i].value += \";\" + f.ITEM1_VALUE_TEXT_OP[i].options[f.ITEM1_VALUE_TEXT_OP[i].selectedIndex].value;\n");
            appendDirtyJavaScript("   f.ITEM1_VALUE_TEXT_OP[i].selectedIndex = 0;\n");
         }
         appendDirtyJavaScript("}\n");
         
// end, Bug Id 27913
         
         appendDirtyJavaScript("function validateTechnicalClass(i)\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   document.form.TECHNICAL_CLASS.value = f.TECHNICAL_CLASS.value.toUpperCase();\n"); // Bug Id 27913
         appendDirtyJavaScript("   document.form.__SEARCHFLAG.value = '1';\n");
         appendDirtyJavaScript("   document.form.__TECLASS.value = document.form.TECHNICAL_CLASS.value;\n");
         appendDirtyJavaScript("   submit();\n");
         appendDirtyJavaScript("}\n");
         appendDirtyJavaScript("function Sub(i)\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("document.form.SERACH_TEXT.focus();"); 
         appendDirtyJavaScript("}\n");
         appendDirtyJavaScript("function Sub3(i)\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("document.form.SERACH_TEXT1.focus();"); 
         appendDirtyJavaScript("}\n");
         appendDirtyJavaScript("function Sub1(i)\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   if(document.form.VALUE_TEXT_TXT.value != \"\" || document.form.SERACH_TEXT.value != \"\")\n");
         appendDirtyJavaScript("   {\n");
         appendDirtyJavaScript("         document.form.__VALTEXT.value = '");
         appendDirtyJavaScript(valte);
         appendDirtyJavaScript("';\n");
         appendDirtyJavaScript("      if(document.form.__VALTEXT.value != \"\")\n");
         appendDirtyJavaScript("      {\n");
         appendDirtyJavaScript("         document.form.__VALTEXT.value = '");
         appendDirtyJavaScript(valte);
         appendDirtyJavaScript("'+';'+document.form.VALUE_TEXT_TXT.value+document.form.SERACH_TEXT.value;\n");
         appendDirtyJavaScript("         document.form.__VALTEXT1.value = \"FALSE\";\n");
         appendDirtyJavaScript("      }   \n");
         appendDirtyJavaScript("      else\n");
         appendDirtyJavaScript("      {\n");
         appendDirtyJavaScript("         document.form.__VALTEXT.value = document.form.VALUE_TEXT_TXT.value + document.form.SERACH_TEXT.value;\n");
         appendDirtyJavaScript("         document.form.__VALTEXT1.value = \"FALSE\";\n");
         appendDirtyJavaScript("      }   \n");
         appendDirtyJavaScript("   }   \n");
         appendDirtyJavaScript("   else\n");
         appendDirtyJavaScript("      document.form.__VALTEXT1.value = \"TRUE\";   \n");
         appendDirtyJavaScript("}\n");
         appendDirtyJavaScript("function Sub2(i)\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   if(document.form.WHERE_TEXT.value != \"\" || document.form.SERACH_TEXT1.value != \"\")\n");
         appendDirtyJavaScript("   {\n");
         appendDirtyJavaScript("         document.form.__WHETEXT.value = '");
         appendDirtyJavaScript(whte);
         appendDirtyJavaScript("';\n");
         appendDirtyJavaScript("      if(document.form.__WHETEXT.value != \"\")\n");
         appendDirtyJavaScript("      {\n");
         appendDirtyJavaScript("         document.form.__WHETEXT.value = '");
         appendDirtyJavaScript(whte);
         appendDirtyJavaScript("'+';'+document.form.WHERE_TEXT.value+document.form.SERACH_TEXT1.value;\n");
         appendDirtyJavaScript("         document.form.__WHETEXT1.value = \"FALSE\";\n");
         appendDirtyJavaScript("      }   \n");
         appendDirtyJavaScript("      else\n");
         appendDirtyJavaScript("      {\n");
         appendDirtyJavaScript("         document.form.__WHETEXT.value = document.form.WHERE_TEXT.value+document.form.SERACH_TEXT1.value;\n");
         appendDirtyJavaScript("         document.form.__WHETEXT1.value = \"FALSE\";\n");
         appendDirtyJavaScript("      }   \n");
         appendDirtyJavaScript("   }   \n");
         appendDirtyJavaScript("   else\n");
         appendDirtyJavaScript("      document.form.__WHETEXT1.value = \"TRUE\";   \n");
         appendDirtyJavaScript("}\n");
         appendDirtyJavaScript("if ('");
         appendDirtyJavaScript(closeFlag);
         appendDirtyJavaScript("' == \"TRUE\")\n");
         appendDirtyJavaScript("{\n");
         appendDirtyJavaScript("   window.close();\n");
         appendDirtyJavaScript("}\n");
         out.append(mgr.endPresentation());
         out.append("</form>\n");
         out.append("</body>\n");
      }
      else
      {
         appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
         appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
         appendDirtyJavaScript("window.location = '");
         appendDirtyJavaScript(url);
         appendDirtyJavaScript("'+\"Navigator.page?MAINMENU=Y&NEW=Y\";\n"); 
         appendDirtyJavaScript("window.open('");
         appendDirtyJavaScript(url);
         appendDirtyJavaScript("'+\"appsrw/BestFitSearchWiz.page\",\"frmTechnicalSearchWiz\",\"resizable=yes,scrollbars=yes,status=yes,menubar=no,height=517,width=820\");\n"); 
      }
      out.append("</html>");
      return out;
   }

}
