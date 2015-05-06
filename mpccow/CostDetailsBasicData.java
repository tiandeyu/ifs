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
 *  File         : CostDetailsBasicData.java 
 *  Description  : Basic Data for Cost Details.
 *  Notes        : 
 * ----------------------------------------------------------------------------
 *  Modified     :
 *     SuThlk  2009-Jun-24 - Bug 84193, Corrected the values passed for setActiveTab in printContents().
 *     MaJalk  2007-Sep-04 - Set maximum lengths to fields COST_SOURCE_ID, DESCRIPTION, POSTING_GROUP_ID, ITEM4_DESCRIPTION.
 *     Cpeilk  2007-Jun-27 - Created
 * ----------------------------------------------------------------------------
 */


package ifs.mpccow;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class CostDetailsBasicData extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.CostDetailsBasicData");


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

   private ASPBlock ovheadblk;
   private ASPRowSet ovheadset;
   private ASPCommandBar ovheadbar;
   private ASPTable ovheadtbl;
   private ASPBlockLayout ovheadlay;

   private ASPBlock itemblk1;
   private ASPRowSet itemset1;
   private ASPCommandBar itembar1;
   private ASPTable itemtbl1;
   private ASPBlockLayout itemlay1;

   private ASPBlock itemblk2;
   private ASPCommandBar itembar2;
   private ASPBlockLayout itemlay2;

   private ASPBlock itemblk3;
   private ASPCommandBar itembar3;
   private ASPBlockLayout itemlay3;

   private ASPBlock siteblk;
   private ASPRowSet siteset;
   private ASPCommandBar sitebar;
   private ASPTable sitetbl;
   private ASPBlockLayout sitelay;

   private ASPBlock purchaserblk;
   private ASPRowSet purchaserset;
   private ASPCommandBar purchaserbar;
   private ASPTable purchasertbl;
   private ASPBlockLayout purchaserlay;

   private ASPBlock requisitionerblk;
   private ASPRowSet requisitionerset;
   private ASPCommandBar requisitionerbar;
   private ASPTable requisitionertbl;
   private ASPBlockLayout requisitionerlay;

   private ASPBlock purgroupblk;
   private ASPRowSet purgroupset;
   private ASPCommandBar purgroupbar;
   private ASPTable purgrouptbl;
   private ASPBlockLayout purgrouplay;

   private ASPBlock assetclassblk;
   private ASPRowSet assetclassset;
   private ASPCommandBar assetclassbar;
   private ASPTable assetclasstbl;
   private ASPBlockLayout assetclasslay;

   private ASPBlock comgroupblk;
   private ASPRowSet comgroupset;
   private ASPCommandBar comgroupbar;
   private ASPTable comgrouptbl;
   private ASPBlockLayout comgrouplay;

   private ASPBlock invparttypeblk;
   private ASPRowSet invparttypeset;
   private ASPCommandBar invparttypebar;
   private ASPTable invparttypetbl;
   private ASPBlockLayout invparttypelay;

   private ASPBlock productcodeblk;
   private ASPRowSet productcodeset;
   private ASPCommandBar productcodebar;
   private ASPTable productcodetbl;
   private ASPBlockLayout productcodelay;

   private ASPBlock abcclassblk;
   private ASPRowSet abcclassset;
   private ASPCommandBar abcclassbar;
   private ASPTable abcclasstbl;
   private ASPBlockLayout abcclasslay;

   private ASPBlock prodfamilyblk;
   private ASPRowSet prodfamilyset;
   private ASPCommandBar prodfamilybar;
   private ASPTable prodfamilytbl;
   private ASPBlockLayout prodfamilylay;

   private ASPBlock plannerblk;
   private ASPRowSet plannerset;
   private ASPCommandBar plannerbar;
   private ASPTable plannertbl;
   private ASPBlockLayout plannerlay;

   private ASPBlock accgroupblk;
   private ASPRowSet accgroupset;
   private ASPCommandBar accgroupbar;
   private ASPTable accgrouptbl;
   private ASPBlockLayout accgrouplay;

   private ASPBlock laborclassblk;
   private ASPRowSet laborclassset;
   private ASPCommandBar laborclassbar;
   private ASPTable laborclasstbl;
   private ASPBlockLayout laborclasslay;

   private ASPBlock workcenblk;
   private ASPRowSet workcenset;
   private ASPCommandBar workcenbar;
   private ASPTable workcentbl;
   private ASPBlockLayout workcenlay;

   private ASPBlock itemblk4;
   private ASPRowSet itemset4;
   private ASPCommandBar itembar4;
   private ASPTable itemtbl4;
   private ASPBlockLayout itemlay4;

   private ASPTabContainer tabs;
   private ASPTabContainer tabsIndicators;
   private ASPTabContainer tabsIndicators1;
   private int indicatorsActiveTab;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private int currrow;
   private static boolean mfgstd_installed;
   private static boolean cost_installed;


   //===============================================================
   // Construction 
   //===============================================================
   public CostDetailsBasicData(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPContext ctx = mgr.getASPContext();

      indicatorsActiveTab = ctx.readNumber("INDICATORSACTIVETAB",1);
      
      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if(mgr.dataTransfered())
         okFind();
      else if(!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
      {
         okFind();
      }
      
      adjust();

      ctx.writeNumber("INDICATORSACTIVETAB",indicatorsActiveTab);

      tabs.saveActiveTab();
   }

   
   public void  newRowITEM0()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      cmd = trans.addEmptyCommand("ITEM0","COST_SOURCE_API.New__",itemblk0);
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("ITEM0/DATA");
      data.setFieldItem("COST_SOURCE_ID","");
      data.setFieldItem("DESCRIPTION","");
      data.setFieldItem("COMPANY",headset.getValue("COMPANY"));
      itemset0.addRow(data);
   }

   public void  newRowOVHEAD()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      cmd = trans.addEmptyCommand("OVHEAD","COST_SOURCE_OVERHEAD_RATE_API.New__",ovheadblk);
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("OVHEAD/DATA");
      data.setFieldItem("OVERHEAD_RATE","");
      data.setFieldItem("VALID_FROM_DATE","");
      data.setFieldItem("COMPANY",headset.getValue("COMPANY"));
      data.setFieldItem("COST_SOURCE_ID",itemset0.getValue("COST_SOURCE_ID"));
      ovheadset.addRow(data);
   }

   public void  newRowSITE()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      cmd = trans.addEmptyCommand("SITECOST","SITE_COST_SOURCE_API.New__",siteblk);
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("SITECOST/DATA");
      data.setFieldItem("COMPANY",headset.getValue("COMPANY"));
      siteset.addRow(data);
   }

   public void  newRowPURCHASER()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      cmd = trans.addEmptyCommand("PURCHASER","PURCHASE_BUYER_COST_SOURCE_API.New__",purchaserblk);
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("PURCHASER/DATA");
      data.setFieldItem("COMPANY",headset.getValue("COMPANY"));
      purchaserset.addRow(data);
   }

   public void  newRowREQUISITIONER()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      cmd = trans.addEmptyCommand("REQUISITIONER","PUR_REQUISER_COST_SOURCE_API.New__",requisitionerblk);
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("REQUISITIONER/DATA");
      data.setFieldItem("COMPANY",headset.getValue("COMPANY"));
      requisitionerset.addRow(data);
   }

   public void  newRowPURGROUP()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      cmd = trans.addEmptyCommand("PURGROUP","PUR_PART_GROUP_COST_SOURCE_API.New__",purgroupblk);
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("PURGROUP/DATA");
      data.setFieldItem("COMPANY",headset.getValue("COMPANY"));
      purgroupset.addRow(data);
   }

   public void  newRowASSETCLASS()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;
      
      cmd = trans.addEmptyCommand("ASSETCLASS","ASSET_CLASS_COST_SOURCE_API.New__",assetclassblk);
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("ASSETCLASS/DATA");
      data.setFieldItem("COMPANY",headset.getValue("COMPANY"));
      assetclassset.addRow(data);
   }

   public void  newRowCOMGROUP()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;
      
      cmd = trans.addEmptyCommand("COMGROUP","COMM_GROUP_COST_SOURCE_API.New__",comgroupblk);
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("COMGROUP/DATA");
      data.setFieldItem("COMPANY",headset.getValue("COMPANY"));
      comgroupset.addRow(data);
   }

   public void  newRowINVPARTTYPE()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;
      
      cmd = trans.addEmptyCommand("INVPARTTYPE","INV_PART_TYPE_COST_SOURCE_API.New__",invparttypeblk);
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("INVPARTTYPE/DATA");
      data.setFieldItem("COMPANY",headset.getValue("COMPANY"));
      invparttypeset.addRow(data);
   }

   public void  newRowPRODUCTCODE()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;
      
      cmd = trans.addEmptyCommand("PRODUCTCODE","INV_PROD_CODE_COST_SOURCE_API.New__",productcodeblk);
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("PRODUCTCODE/DATA");
      data.setFieldItem("COMPANY",headset.getValue("COMPANY"));
      productcodeset.addRow(data);
   }

   public void  newRowABCCLASS()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;
      
      cmd = trans.addEmptyCommand("ABCCLASS","ABC_CLASS_COST_SOURCE_API.New__",abcclassblk);
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("ABCCLASS/DATA");
      data.setFieldItem("COMPANY",headset.getValue("COMPANY"));
      abcclassset.addRow(data);
   }

   public void  newRowPRODFAMILY()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;
      
      cmd = trans.addEmptyCommand("PRODFAMILY","INV_PROD_FAM_COST_SOURCE_API.New__",prodfamilyblk);
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("PRODFAMILY/DATA");
      data.setFieldItem("COMPANY",headset.getValue("COMPANY"));
      prodfamilyset.addRow(data);
   }

   public void  newRowPLANNER()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;
      
      cmd = trans.addEmptyCommand("PLANNER","INV_PART_PLAN_COST_SOURCE_API.New__",plannerblk);
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("PLANNER/DATA");
      data.setFieldItem("COMPANY",headset.getValue("COMPANY"));
      plannerset.addRow(data);
   }

   public void  newRowACCGROUP()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;
      
      cmd = trans.addEmptyCommand("ACCGROUP","ACC_GROUP_COST_SOURCE_API.New__",accgroupblk);
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("ACCGROUP/DATA");
      data.setFieldItem("COMPANY",headset.getValue("COMPANY"));
      accgroupset.addRow(data);
   }

   public void  newRowLABORCLASS()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;
      
      cmd = trans.addEmptyCommand("LABORCLASS","LABOR_CLASS_COST_SOURCE_API.New__",laborclassblk);
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("LABORCLASS/DATA");
      data.setFieldItem("COMPANY",headset.getValue("COMPANY"));
      laborclassset.addRow(data);
   }

   public void  newRowWORKCEN()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;
      
      cmd = trans.addEmptyCommand("WORKCEN","WORK_CENTER_COST_SOURCE_API.New__",workcenblk);
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("WORKCEN/DATA");
      data.setFieldItem("COMPANY",headset.getValue("COMPANY"));
      workcenset.addRow(data);
   }

   public void  newRowITEM4()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      cmd = trans.addEmptyCommand("ITEM4","COST_BUCKET_POSTING_GROUP_API.New__",itemblk4);
      cmd.setOption("ACTION","PREPARE");

      trans = mgr.perform(trans);

      data = trans.getBuffer("ITEM4/DATA");
      data.setFieldItem("COMPANY",headset.getValue("COMPANY"));
      itemset4.addRow(data);
   }

   public void  countFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q = trans.addQuery(headblk);
      String n;

      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("COMPANY IN (SELECT company FROM company_finance_auth1)");
      q.includeMeta("ALL");

      trans = mgr.perform(trans);

      n = trans.getValue("HEAD/DATA/N");
      headlay.setCountValue(toInt(n));
   }

   public void  okFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q = trans.addQuery(headblk);

      q.addWhereCondition("COMPANY IN (SELECT company FROM company_finance_auth1)");
      q.includeMeta("ALL");

      mgr.querySubmit(trans,headblk);

      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("MPCCOWCOSTDETAILBASICDATANODATA: No data found."));
         headset.clear();
      }
      else
         mgr.createSearchURL(headblk);
      eval(headset.syncItemSets());
   }

   public void  okFindITEM0()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      
      q = trans.addEmptyQuery(itemblk0);
      q.addWhereCondition("COMPANY = ? ");
      q.addParameter("COMPANY", headset.getValue("COMPANY"));
      q.includeMeta("ALL");        
      
      currrow=headset.getCurrentRowNo();
      mgr.submit(trans);   
      headset.goTo(currrow);
   }

   public void  okFindOVHEAD()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int head_row_no;
      int item_row_no;
      
      if (itemset0.countRows() > 0 )
      {
         q = trans.addEmptyQuery(ovheadblk);
         q.addWhereCondition("COMPANY = ? AND COST_SOURCE_ID = ?");
         q.addParameter("COMPANY",headset.getValue("COMPANY"));
         q.addParameter("COST_SOURCE_ID",itemset0.getValue("COST_SOURCE_ID"));
         q.includeMeta("ALL");
   
         head_row_no = headset.getCurrentRowNo();
         item_row_no = itemset0.getCurrentRowNo();
   
         mgr.querySubmit(trans,ovheadblk);
         headset.goTo(head_row_no);
         itemset0.goTo(item_row_no);
      }
      else
         ovheadset.clear();
      
   }

   public void  okFindITEM1()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      
      q = trans.addEmptyQuery(itemblk1);
      q.addWhereCondition("COMPANY = ? ");
      q.addParameter("COMPANY", headset.getValue("COMPANY"));
      q.includeMeta("ALL");        
      
      currrow=headset.getCurrentRowNo();
      mgr.submit(trans);   
      headset.goTo(currrow);
   }

   public void  okFindSITE()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      
      q = trans.addEmptyQuery(siteblk);
      q.addWhereCondition("COMPANY = ?");
      q.addWhereCondition("CONTRACT IN (SELECT CONTRACT FROM site_public WHERE CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT))");
      q.addParameter("COMPANY",headset.getValue("COMPANY"));
      q.setOrderByClause("CONTRACT,VALID_FROM_DATE");
      q.includeMeta("ALL");        
      
      currrow=headset.getCurrentRowNo();
      mgr.submit(trans);   
      headset.goTo(currrow);
   }

   public void  okFindPURCHASER()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      
      q = trans.addEmptyQuery(purchaserblk);
      q.addWhereCondition("COMPANY = ?");
      q.addParameter("COMPANY",headset.getValue("COMPANY"));
      q.setOrderByClause("BUYER_CODE, VALID_FROM_DATE");
      q.includeMeta("ALL");        
      
      currrow=headset.getCurrentRowNo();
      mgr.submit(trans);   
      headset.goTo(currrow);
   }

   public void  okFindREQUISITIONER()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      
      q = trans.addEmptyQuery(requisitionerblk);
      q.addWhereCondition("COMPANY = ?");
      q.addParameter("COMPANY",headset.getValue("COMPANY"));
      q.includeMeta("ALL");        
      
      currrow=headset.getCurrentRowNo();
      mgr.submit(trans);   
      headset.goTo(currrow);
   }

   public void  okFindPURGROUP()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      
      q = trans.addEmptyQuery(purgroupblk);
      q.addWhereCondition("COMPANY = ?");
      q.addParameter("COMPANY",headset.getValue("COMPANY"));
      q.includeMeta("ALL");        
      
      currrow=headset.getCurrentRowNo();
      mgr.submit(trans);   
      headset.goTo(currrow);
   }

   public void  okFindASSETCLASS()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      
      q = trans.addEmptyQuery(assetclassblk);
      q.addWhereCondition("COMPANY = ?");
      q.addParameter("COMPANY",headset.getValue("COMPANY"));
      q.includeMeta("ALL");        
      
      currrow=headset.getCurrentRowNo();
      mgr.submit(trans);   
      headset.goTo(currrow);
   }

   public void  okFindCOMGROUP()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      
      q = trans.addEmptyQuery(comgroupblk);
      q.addWhereCondition("COMPANY = ?");
      q.addParameter("COMPANY",headset.getValue("COMPANY"));
      q.includeMeta("ALL");        
      
      currrow=headset.getCurrentRowNo();
      mgr.submit(trans);   
      headset.goTo(currrow);
   }

   public void  okFindINVPARTTYPE()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      
      q = trans.addEmptyQuery(invparttypeblk);
      q.addWhereCondition("COMPANY = ?");
      q.addParameter("COMPANY",headset.getValue("COMPANY"));
      q.includeMeta("ALL");        
      
      currrow=headset.getCurrentRowNo();
      mgr.submit(trans);   
      headset.goTo(currrow);
   }

   public void  okFindPRODUCTCODE()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      
      q = trans.addEmptyQuery(productcodeblk);
      q.addWhereCondition("COMPANY = ?");
      q.addParameter("COMPANY",headset.getValue("COMPANY"));
      q.includeMeta("ALL");        
      
      currrow=headset.getCurrentRowNo();
      mgr.submit(trans);   
      headset.goTo(currrow);
   }

   public void  okFindABCCLASS()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      
      q = trans.addEmptyQuery(abcclassblk);
      q.addWhereCondition("COMPANY = ?");
      q.addParameter("COMPANY",headset.getValue("COMPANY"));
      q.setOrderByClause("ABC_CLASS, VALID_FROM_DATE");
      q.includeMeta("ALL");        
      
      currrow=headset.getCurrentRowNo();
      mgr.submit(trans);   
      headset.goTo(currrow);
   }

   public void  okFindPRODFAMILY()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      
      q = trans.addEmptyQuery(prodfamilyblk);
      q.addWhereCondition("COMPANY = ?");
      q.addParameter("COMPANY",headset.getValue("COMPANY"));
      q.setOrderByClause("PART_PRODUCT_FAMILY, VALID_FROM_DATE");
      q.includeMeta("ALL");        
      
      currrow=headset.getCurrentRowNo();
      mgr.submit(trans);   
      headset.goTo(currrow);
   }

   public void  okFindPLANNER()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      
      q = trans.addEmptyQuery(plannerblk);
      q.addWhereCondition("COMPANY = ?");
      q.addParameter("COMPANY",headset.getValue("COMPANY"));
      q.setOrderByClause("BUYER_CODE, VALID_FROM_DATE");
      q.includeMeta("ALL");        
      
      currrow=headset.getCurrentRowNo();
      mgr.submit(trans);   
      headset.goTo(currrow);
   }

   public void  okFindACCGROUP()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      
      q = trans.addEmptyQuery(accgroupblk);
      q.addWhereCondition("COMPANY = ?");
      q.addParameter("COMPANY",headset.getValue("COMPANY"));
      q.setOrderByClause("ACCOUNTING_GROUP, VALID_FROM_DATE");
      q.includeMeta("ALL");        
      
      currrow=headset.getCurrentRowNo();
      mgr.submit(trans);   
      headset.goTo(currrow);
   }

   public void  okFindLABORCLASS()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      
      q = trans.addEmptyQuery(laborclassblk);
      q.addWhereCondition("COMPANY = ?");
      q.addWhereCondition("EXISTS (SELECT 1 FROM user_allowed_site_pub uasp WHERE uasp.site = CONTRACT)");
      q.addParameter("COMPANY",headset.getValue("COMPANY"));
      q.setOrderByClause("CONTRACT, LABOR_CLASS_NO, VALID_FROM_DATE");
      q.includeMeta("ALL");        
      
      currrow=headset.getCurrentRowNo();
      mgr.submit(trans);   
      headset.goTo(currrow);
   }

   public void  okFindWORKCEN()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      
      q = trans.addEmptyQuery(workcenblk);
      q.addWhereCondition("COMPANY = ?");
      q.addWhereCondition("EXISTS (SELECT 1 FROM user_allowed_site_pub uasp WHERE uasp.site = CONTRACT)");
      q.addParameter("COMPANY",headset.getValue("COMPANY"));
      q.setOrderByClause("CONTRACT, WORK_CENTER_NO, VALID_FROM_DATE");
      q.includeMeta("ALL");        
      
      currrow=headset.getCurrentRowNo();
      mgr.submit(trans);   
      headset.goTo(currrow);
   }

   public void  okFindITEM4()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      
      q = trans.addEmptyQuery(itemblk4);
      q.addWhereCondition("COMPANY = ? ");
      q.addParameter("COMPANY", headset.getValue("COMPANY"));
      q.includeMeta("ALL");        
      
      currrow=headset.getCurrentRowNo();
      mgr.submit(trans);   
      headset.goTo(currrow);
   }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();
      ASPField f;

      mfgstd_installed    = mgr.isModuleInstalled("MFGSTD");
      cost_installed    = mgr.isModuleInstalled("COST");

      headblk = mgr.newASPBlock("HEAD");
      
      f = headblk.addField("OBJID");
      f.setHidden();
      
      f = headblk.addField("OBJVERSION");
      f.setHidden();
      
      f = headblk.addField("COMPANY");
      f.setSize(22);
      f.setMandatory();
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOMPANY: Company");
      f.setReadOnly();
      f.setUpperCase();

      f = headblk.addField("MANDATORY_POSTING_GROUP_DB");
      f.setLabel("MPCCOWCOSTDETAILBASICDATAMANDATORYPOSTINGGROUPDB: Mandatory to Use Posting Cost Groups");
      f.setCheckBox("FALSE,TRUE");
      f.setMandatory();

      f = headblk.addField("MANDATORY_COST_SOURCE_DB");
      f.setLabel("MPCCOWCOSTDETAILBASICDATAMANDATORYCOSTSOURCEDB: Mandatory to Use Cost Source");
      f.setCheckBox("FALSE,TRUE");
      f.setMandatory();

      f = headblk.addField("USE_ACCOUNTING_YEAR_DB");
      f.setLabel("MPCCOWCOSTDETAILBASICDATAUSEACCOUNTINGYEARDB: Use OH Accounting Year");
      f.setCheckBox("FALSE,TRUE");
      f.setMandatory();
      
      headblk.setView("COMPANY_DISTRIBUTION_INFO");   
      headblk.defineCommand("COMPANY_DISTRIBUTION_INFO_API","Modify__");
      headset = headblk.getASPRowSet();
      
      headtbl = mgr.newASPTable(headblk);
      
      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.DUPLICATEROW);
      headbar.disableCommand(headbar.BACK);
      
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);      
      
      headbar.addCustomCommand("activateCostSource","Cost Source");
      headbar.addCustomCommand("activateCostSourceIndicatorDefinition","Cost Source Indicator Definition");
      headbar.addCustomCommand("activateCostSourceIndicators","Cost Source Indicators");
      if (cost_installed)
      {
         headbar.addCustomCommand("activatePostingCostGroup","Posting Cost Group");
      }
      
      headtbl.setTitle("MPCCOWCOSTDETAILBASICDATACOSTDET: Basic Data for Cost Details");
      
      
      //Cost Source tab
      
      itemblk0 = mgr.newASPBlock("ITEM0");
      
      f = itemblk0.addField("ITEM0_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
      
      f = itemblk0.addField("ITEM0_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
      
      f = itemblk0.addField("COST_SOURCE_ID");
      f.setSize(22);
      f.setMaxLength(20);
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID");
      f.setUpperCase();
      
      f = itemblk0.addField("DESCRIPTION");
      f.setSize(30);
      f.setMaxLength(100);
      f.setInsertable();
      f.setMandatory();
      f.setLabel("MPCCOWCOSTDETAILBASICDATADESCRIPTION: Description");

      f = itemblk0.addField("ITEM0_COMPANY");
      f.setHidden();
      f.setDbName("COMPANY");
      
      itemblk0.setView("COST_SOURCE");
      itemblk0.defineCommand("COST_SOURCE_API","New__,Modify__,Remove__");
      itemblk0.setMasterBlock(headblk);
      
      itemset0 = itemblk0.getASPRowSet();
      
      itembar0 = mgr.newASPCommandBar(itemblk0);
      
      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDefaultLayoutMode(itemlay0.SINGLE_LAYOUT);
      
      itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");
      itembar0.disableCommand(itembar0.BACK);
      
      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle("MPCCOWCOSTDETAILBASICDATACOSTSOURCE: Cost Source");

      //Cost Source Overhead Rate
      
      ovheadblk = mgr.newASPBlock("OVHEAD");
      
      f = ovheadblk.addField("OVHEAD_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
      
      f = ovheadblk.addField("OVHEAD_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
      
      f = ovheadblk.addField("OVERHEAD_RATE","Number");
      f.setSize(22);
      f.setMandatory();
      f.setInsertable();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAOVERHEADRATED: Overhead Rate");
      
      f = ovheadblk.addField("VALID_FROM_DATE","Date");
      f.setSize(22);
      f.setInsertable();
      f.setMandatory();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAVALIDFROMDATE: Valid From");

      f = ovheadblk.addField("OVHEAD_COMPANY");
      f.setHidden();
      f.setDbName("COMPANY");

      f = ovheadblk.addField("OVHEAD_COST_SOURCE_ID");
      f.setHidden();
      f.setDbName("COST_SOURCE_ID");
      
      ovheadblk.setView("COST_SOURCE_OVERHEAD_RATE");
      ovheadblk.defineCommand("COST_SOURCE_OVERHEAD_RATE_API","New__,Modify__,Remove__");
      ovheadblk.setMasterBlock(itemblk0);
      
      ovheadset = ovheadblk.getASPRowSet();
      
      ovheadbar = mgr.newASPCommandBar(ovheadblk);
      
      ovheadlay = ovheadblk.getASPBlockLayout();
      ovheadlay.setDefaultLayoutMode(ovheadlay.MULTIROW_LAYOUT);
      
      ovheadbar.defineCommand(ovheadbar.NEWROW,"newRowOVHEAD");
      
      ovheadtbl = mgr.newASPTable(ovheadblk);
      ovheadtbl.setTitle("MPCCOWCOSTDETAILBASICDATAOVHEAD: Cost Source Overhead Rate");
      
      
      //Cost Source Indicator Definition tab
      
      itemblk1 = mgr.newASPBlock("ITEM1");
      
      f = itemblk1.addField("ITEM1_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
      
      f = itemblk1.addField("ITEM1_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
      
      f = itemblk1.addField("TRANSACTION_COST_TYPE");
      f.setSize(25);
      f.enumerateValues("TRANSACTION_COST_TYPE_API");
      f.setMandatory();
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATATRANSACTIONCOSTTYPE: Cost Type");
      f.setInsertable();

      f = itemblk1.addField("COST_SOURCE_INDICATOR");
      f.setSize(25);
      f.setSelectBox();
      f.enumerateValues("COST_SOURCE_INDICATOR_API");
      f.setMandatory();
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEINDICATOR: Cost Source Indicator");
      f.setInsertable();

      f = itemblk1.addField("FIXED_VALUE");
      f.setSize(22);
      f.setDynamicLOV("COST_SOURCE","COMPANY");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATAFIXEDVALUE: Fixed Cost Source"));
      f.setInsertable();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAFIXEDVALUE: Fixed Cost Source");
      f.setValidation("Cost_Source_API.Get_Description","COMPANY, FIXED_VALUE","FIXED_VALUE_DESC");
      
      f = itemblk1.addField("FIXED_VALUE_DESC");
      f.setSize(30);
      f.setReadOnly();
      f.setFunction("Cost_Source_API.Get_Description(COMPANY, FIXED_VALUE)");
      f.setLabel("MPCCOWCOSTDETAILBASICDATAFIXEDVALUEDESC: Description");
      
      
      itemblk1.setView("COST_TYPE_SOURCE_INDICATOR");   
      itemblk1.defineCommand("COST_TYPE_SOURCE_INDICATOR_API","Modify__");
      itemblk1.setMasterBlock(headblk);
      itemset1 = itemblk1.getASPRowSet();
      
      itembar1 = mgr.newASPCommandBar(itemblk1);
      
      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT); 
      
      itembar1.disableCommand(itembar1.DUPLICATEROW);
      itembar1.disableCommand(itembar1.NEWROW);
      
      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setTitle("MPCCOWCOSTDETAILBASICDATACOSTSINDEF: Cost Source Indicator Definition");


      //Cost Source Indicators tab
      
      itemblk2 = mgr.newASPBlock("ITEM2");
      
      itemblk2.setMasterBlock(headblk);
      itembar2 = mgr.newASPCommandBar(itemblk2);

      itemlay2 = itemblk2.getASPBlockLayout();

      itembar2.addCustomCommand("activateSite","Site");
      itembar2.addCustomCommand("activatePurchaser","Purchaser");
      itembar2.addCustomCommand("activateRequisitioner","Requisitioner");
      itembar2.addCustomCommand("activatePurGroup","Purchase Group");
      itembar2.addCustomCommand("activateAssetClass","Asset Class");
      itembar2.addCustomCommand("activateComGroup","Commodity Group");
      itembar2.addCustomCommand("activateInvPartType","Inventory Part Type");
      itembar2.addCustomCommand("activateProductCode","Product Code");
      

      itemblk3 = mgr.newASPBlock("ITEM3");
      
      itemblk3.setMasterBlock(headblk);
      itembar3 = mgr.newASPCommandBar(itemblk3);

      itemlay3 = itemblk3.getASPBlockLayout();

      itembar3.addCustomCommand("activateAbcClass","ABC Class");
      itembar3.addCustomCommand("activateProdFamily","Product Family");
      itembar3.addCustomCommand("activatePlanner","Planner");
      itembar3.addCustomCommand("activateAccGroup","Accounting Group");
      if (mfgstd_installed)
      {
         itembar3.addCustomCommand("activateLaborClass","Labor Class");
         itembar3.addCustomCommand("activateWorkCenter","Work Center");
      }
      

      //Site tab
      
      siteblk = mgr.newASPBlock("SITE");
      
      f = siteblk.addField("SITE_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
      
      f = siteblk.addField("SITE_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
      
      f = siteblk.addField("CONTRACT");
      f.setSize(8);
      f.setDynamicLOV("SITE");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATASITE: Site"));
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setLabel("MPCCOWCOSTDETAILBASICDATASITE: Site");
      f.setUpperCase();
      f.setValidation("Site_API.Get_Description","CONTRACT","SITE_DESCRIPTION");
      
      f = siteblk.addField("SITE_DESCRIPTION");
      f.setSize(30);
      f.setFunction("Site_API.Get_Description(:CONTRACT)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATASITEDESC: Site Description");

      f = siteblk.addField("SITE_COST_SOURCE_ID");
      f.setSize(22);
      f.setDynamicLOV("COST_SOURCE","COMPANY");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID"));
      f.setMandatory();
      f.setDbName("COST_SOURCE_ID");
      f.setInsertable();
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID");
      f.setUpperCase();
      f.setValidation("Cost_Source_API.Get_Description","SITE_COMPANY, SITE_COST_SOURCE_ID","SITE_COST_SOURCE_DESC");

      f = siteblk.addField("SITE_COST_SOURCE_DESC");
      f.setSize(30);
      f.setFunction("Cost_Source_API.Get_Description(:SITE_COMPANY, :SITE_COST_SOURCE_ID)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEDESC: Cost Source Description");

      f = siteblk.addField("SITE_VALID_FROM_DATE","Date");
      f.setSize(22);
      f.setDbName("VALID_FROM_DATE");
      f.setInsertable();
      f.setMandatory();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAVALIDFROMDATE: Valid From");

      f = siteblk.addField("SITE_COMPANY");
      f.setHidden();
      f.setDbName("COMPANY");
      
      siteblk.setView("SITE_COST_SOURCE");
      siteblk.defineCommand("SITE_COST_SOURCE_API","New__,Modify__,Remove__");
      siteblk.setMasterBlock(headblk);
      
      siteset = siteblk.getASPRowSet();
      
      sitebar = mgr.newASPCommandBar(siteblk);
      
      sitelay = siteblk.getASPBlockLayout();
      sitelay.setDefaultLayoutMode(sitelay.MULTIROW_LAYOUT);
      
      sitebar.defineCommand(sitebar.NEWROW,"newRowSITE");
      
      sitetbl = mgr.newASPTable(siteblk);
      sitetbl.setTitle("MPCCOWCOSTDETAILBASICDATASITETAB: Site");


      //Purchaser tab
      
      purchaserblk = mgr.newASPBlock("PURCHASER");
      
      f = purchaserblk.addField("PURCHASER_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
      
      f = purchaserblk.addField("PURCHASER_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
      
      f = purchaserblk.addField("BUYER_CODE");
      f.setSize(22);
      f.setDynamicLOV("PURCHASE_BUYER_LOV2");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATABUYERCODE: Buyer Code"));
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setLabel("MPCCOWCOSTDETAILBASICDATABUYERCODE: Buyer Code");
      f.setUpperCase();
      f.setValidation("Purchase_Buyer_API.Get_Name","BUYER_CODE","NAME");
      
      f = purchaserblk.addField("NAME");
      f.setSize(30);
      f.setFunction("Purchase_Buyer_API.Get_Name(BUYER_CODE)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATANAME: Name");

      f = purchaserblk.addField("PURCHASER_COST_SOURCE_ID");
      f.setSize(22);
      f.setDynamicLOV("COST_SOURCE","COMPANY");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID"));
      f.setMandatory();
      f.setInsertable();
      f.setDbName("COST_SOURCE_ID");
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID");
      f.setUpperCase();
      f.setValidation("Cost_Source_API.Get_Description","PURCHASER_COMPANY, PURCHASER_COST_SOURCE_ID","PURCHASER_COST_SOURCE_DESC");

      f = purchaserblk.addField("PURCHASER_COST_SOURCE_DESC");
      f.setSize(30);
      f.setFunction("Cost_Source_API.Get_Description(:PURCHASER_COMPANY, :PURCHASER_COST_SOURCE_ID)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEDESC: Cost Source Description");

      f = purchaserblk.addField("PURCHASER_VALID_FROM_DATE","Date");
      f.setSize(22);
      f.setDbName("VALID_FROM_DATE");
      f.setInsertable();
      f.setMandatory();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAVALIDFROMDATE: Valid From");

      f = purchaserblk.addField("PURCHASER_COMPANY");
      f.setHidden();
      f.setDbName("COMPANY");
      
      purchaserblk.setView("PURCHASE_BUYER_COST_SOURCE");
      purchaserblk.defineCommand("PURCHASE_BUYER_COST_SOURCE_API","New__,Modify__,Remove__");
      purchaserblk.setMasterBlock(headblk);
      
      purchaserset = purchaserblk.getASPRowSet();
      
      purchaserbar = mgr.newASPCommandBar(purchaserblk);
      
      purchaserlay = purchaserblk.getASPBlockLayout();
      purchaserlay.setDefaultLayoutMode(purchaserlay.MULTIROW_LAYOUT);
      
      purchaserbar.defineCommand(purchaserbar.NEWROW,"newRowPURCHASER");
      
      purchasertbl = mgr.newASPTable(purchaserblk);
      purchasertbl.setTitle("MPCCOWCOSTDETAILBASICDATAPURCHASERTAB: Purchaser");


      //Requisitioner tab
      
      requisitionerblk = mgr.newASPBlock("REQUISITIONER");
      
      f = requisitionerblk.addField("REQUISITIONER_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
      
      f = requisitionerblk.addField("REQUISITIONER_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
      
      f = requisitionerblk.addField("REQUISITIONER_CODE");
      f.setSize(22);
      f.setDynamicLOV("PURCHASE_REQUISITIONER_LOV");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATAREQCODE: Requisitioner ID"));
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAREQCODE: Requisitioner ID");
      f.setUpperCase();
      f.setValidation("Purchase_Requisitioner_API.Get_Requisitioner","REQUISITIONER_CODE","REQUISITIONER_NAME");
      
      f = requisitionerblk.addField("REQUISITIONER_NAME");
      f.setSize(30);
      f.setFunction("Purchase_Requisitioner_API.Get_Requisitioner(REQUISITIONER_CODE)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAREQNAME: Requisitioner Name");

      f = requisitionerblk.addField("REQUISITIONER_COST_SOURCE_ID");
      f.setSize(22);
      f.setDynamicLOV("COST_SOURCE","COMPANY");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID"));
      f.setMandatory();
      f.setInsertable();
      f.setDbName("COST_SOURCE_ID");
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID");
      f.setUpperCase();
      f.setValidation("Cost_Source_API.Get_Description","REQUISITIONER_COMPANY, REQUISITIONER_COST_SOURCE_ID","REQUISITIONER_COST_SOURCE_DESC");

      f = requisitionerblk.addField("REQUISITIONER_COST_SOURCE_DESC");
      f.setSize(30);
      f.setFunction("Cost_Source_API.Get_Description(:REQUISITIONER_COMPANY, :REQUISITIONER_COST_SOURCE_ID)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEDESC: Cost Source Description");

      f = requisitionerblk.addField("REQUISITIONER_VALID_FROM_DATE","Date");
      f.setSize(22);
      f.setDbName("VALID_FROM_DATE");
      f.setInsertable();
      f.setMandatory();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAVALIDFROMDATE: Valid From");

      f = requisitionerblk.addField("REQUISITIONER_COMPANY");
      f.setHidden();
      f.setDbName("COMPANY");
      
      requisitionerblk.setView("PUR_REQUISER_COST_SOURCE");
      requisitionerblk.defineCommand("PUR_REQUISER_COST_SOURCE_API","New__,Modify__,Remove__");
      requisitionerblk.setMasterBlock(headblk);
      
      requisitionerset = requisitionerblk.getASPRowSet();
      
      requisitionerbar = mgr.newASPCommandBar(requisitionerblk);
      
      requisitionerlay = requisitionerblk.getASPBlockLayout();
      requisitionerlay.setDefaultLayoutMode(requisitionerlay.MULTIROW_LAYOUT);
      
      requisitionerbar.defineCommand(requisitionerbar.NEWROW,"newRowREQUISITIONER");
      
      requisitionertbl = mgr.newASPTable(requisitionerblk);
      requisitionertbl.setTitle("MPCCOWCOSTDETAILBASICDATAREQUISITIONERTAB: Requisitioner");


      //Purchase Group tab
      
      purgroupblk = mgr.newASPBlock("PURGROUP");
      
      f = purgroupblk.addField("PURGROUP_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
      
      f = purgroupblk.addField("PURGROUP_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
      
      f = purgroupblk.addField("STAT_GRP");
      f.setSize(8);
      f.setDynamicLOV("PURCHASE_PART_GROUP");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATASTATGRP: Purchase Group"));
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setLabel("MPCCOWCOSTDETAILBASICDATASTATGRP: Purchase Group");
      f.setUpperCase();
      f.setValidation("Purchase_Part_Group_API.Get_Description","STAT_GRP","STAT_GRP_DESC");
      
      f = purgroupblk.addField("STAT_GRP_DESC");
      f.setSize(30);
      f.setFunction("Purchase_Part_Group_API.Get_Description(STAT_GRP)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATASTATGRPDESC: Purchase Group Description");

      f = purgroupblk.addField("PURGROUP_COST_SOURCE_ID");
      f.setSize(22);
      f.setDynamicLOV("COST_SOURCE","COMPANY");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID"));
      f.setMandatory();
      f.setInsertable();
      f.setDbName("COST_SOURCE_ID");
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID");
      f.setUpperCase();
      f.setValidation("Cost_Source_API.Get_Description","PURGROUP_COMPANY, PURGROUP_COST_SOURCE_ID","PURGROUP_COST_SOURCE_DESC");

      f = purgroupblk.addField("PURGROUP_COST_SOURCE_DESC");
      f.setSize(30);
      f.setFunction("Cost_Source_API.Get_Description(:PURGROUP_COMPANY, :PURGROUP_COST_SOURCE_ID)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEDESC: Cost Source Description");

      f = purgroupblk.addField("PURGROUP_VALID_FROM_DATE","Date");
      f.setSize(22);
      f.setDbName("VALID_FROM_DATE");
      f.setInsertable();
      f.setMandatory();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAVALIDFROMDATE: Valid From");

      f = purgroupblk.addField("PURGROUP_COMPANY");
      f.setHidden();
      f.setDbName("COMPANY");
      
      purgroupblk.setView("PUR_PART_GROUP_COST_SOURCE");
      purgroupblk.defineCommand("PUR_PART_GROUP_COST_SOURCE_API","New__,Modify__,Remove__");
      purgroupblk.setMasterBlock(headblk);
      
      purgroupset = purgroupblk.getASPRowSet();
      
      purgroupbar = mgr.newASPCommandBar(purgroupblk);
      
      purgrouplay = purgroupblk.getASPBlockLayout();
      purgrouplay.setDefaultLayoutMode(purgrouplay.MULTIROW_LAYOUT);
      
      purgroupbar.defineCommand(purgroupbar.NEWROW,"newRowPURGROUP");
      
      purgrouptbl = mgr.newASPTable(purgroupblk);
      purgrouptbl.setTitle("MPCCOWCOSTDETAILBASICDATAPURGROUPTAB: Purchase Group");


      //Asset Class tab
      
      assetclassblk = mgr.newASPBlock("ASSETCLASS");
      
      f = assetclassblk.addField("ASSETCLASS_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
      
      f = assetclassblk.addField("ASSETCLASS_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
      
      f = assetclassblk.addField("ASSET_CLASS");
      f.setSize(5);
      f.setDynamicLOV("ASSET_CLASS");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATAASSETCLASS: Asset Class"));
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAASSETCLASS: Asset Class");
      f.setUpperCase();
      f.setValidation("Asset_Class_API.Get_Description","ASSET_CLASS","ASSET_CLASS_DESC");
      
      f = assetclassblk.addField("ASSET_CLASS_DESC");
      f.setSize(30);
      f.setFunction("Asset_Class_API.Get_Description(ASSET_CLASS)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAASSETCLASSDESC: Asset Class Description");

      f = assetclassblk.addField("ASSETCLASS_COST_SOURCE_ID");
      f.setSize(22);
      f.setDynamicLOV("COST_SOURCE","COMPANY");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID"));
      f.setMandatory();
      f.setInsertable();
      f.setDbName("COST_SOURCE_ID");
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID");
      f.setUpperCase();
      f.setValidation("Cost_Source_API.Get_Description","ASSETCLASS_COMPANY, ASSETCLASS_COST_SOURCE_ID","ASSETCLASS_COST_SOURCE_DESC");

      f = assetclassblk.addField("ASSETCLASS_COST_SOURCE_DESC");
      f.setSize(30);
      f.setFunction("Cost_Source_API.Get_Description(:ASSETCLASS_COMPANY, :ASSETCLASS_COST_SOURCE_ID)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEDESC: Cost Source Description");

      f = assetclassblk.addField("ASSETCLASS_VALID_FROM_DATE","Date");
      f.setSize(22);
      f.setDbName("VALID_FROM_DATE");
      f.setInsertable();
      f.setMandatory();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAVALIDFROMDATE: Valid From");

      f = assetclassblk.addField("ASSETCLASS_COMPANY");
      f.setHidden();
      f.setDbName("COMPANY");
      
      assetclassblk.setView("ASSET_CLASS_COST_SOURCE");
      assetclassblk.defineCommand("ASSET_CLASS_COST_SOURCE_API","New__,Modify__,Remove__");
      assetclassblk.setMasterBlock(headblk);
      
      assetclassset = assetclassblk.getASPRowSet();
      
      assetclassbar = mgr.newASPCommandBar(assetclassblk);
      
      assetclasslay = assetclassblk.getASPBlockLayout();
      assetclasslay.setDefaultLayoutMode(assetclasslay.MULTIROW_LAYOUT);
      
      assetclassbar.defineCommand(assetclassbar.NEWROW,"newRowASSETCLASS");
      
      assetclasstbl = mgr.newASPTable(assetclassblk);
      assetclasstbl.setTitle("MPCCOWCOSTDETAILBASICDATAASSETCLASSTAB: Asset Class");

      //Commodity Group tab
      
      comgroupblk = mgr.newASPBlock("COMGROUP");
      
      f = comgroupblk.addField("COMGROUP_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
      
      f = comgroupblk.addField("COMGROUP_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
      
      f = comgroupblk.addField("COMMODITY_CODE");
      f.setSize(8);
      f.setDynamicLOV("LOV_COMMODITY_GROUP_2");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATACOMMODITYCODE: Commodity Code"));
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOMMODITYCODE: Commodity Code");
      f.setUpperCase();
      f.setValidation("Commodity_Group_API.Get_Description","COMMODITY_CODE","COMMODITY_CODE_DESC");
      
      f = comgroupblk.addField("COMMODITY_CODE_DESC");
      f.setSize(30);
      f.setFunction("Commodity_Group_API.Get_Description(COMMODITY_CODE)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOMMODITYCODEDESC: Commodity Code Description");

      f = comgroupblk.addField("COMGROUP_COST_SOURCE_ID");
      f.setSize(22);
      f.setDynamicLOV("COST_SOURCE","COMPANY");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID"));
      f.setMandatory();
      f.setInsertable();
      f.setDbName("COST_SOURCE_ID");
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID");
      f.setUpperCase();
      f.setValidation("Cost_Source_API.Get_Description","COMGROUP_COMPANY, COMGROUP_COST_SOURCE_ID","COMGROUP_COST_SOURCE_DESC");

      f = comgroupblk.addField("COMGROUP_COST_SOURCE_DESC");
      f.setSize(30);
      f.setFunction("Cost_Source_API.Get_Description(:COMGROUP_COMPANY, :COMGROUP_COST_SOURCE_ID)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEDESC: Cost Source Description");

      f = comgroupblk.addField("COMGROUP_VALID_FROM_DATE","Date");
      f.setSize(22);
      f.setDbName("VALID_FROM_DATE");
      f.setInsertable();
      f.setMandatory();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAVALIDFROMDATE: Valid From");

      f = comgroupblk.addField("COMGROUP_COMPANY");
      f.setHidden();
      f.setDbName("COMPANY");
      
      comgroupblk.setView("COMM_GROUP_COST_SOURCE");
      comgroupblk.defineCommand("COMM_GROUP_COST_SOURCE_API","New__,Modify__,Remove__");
      comgroupblk.setMasterBlock(headblk);
      
      comgroupset = comgroupblk.getASPRowSet();
      
      comgroupbar = mgr.newASPCommandBar(comgroupblk);
      
      comgrouplay = comgroupblk.getASPBlockLayout();
      comgrouplay.setDefaultLayoutMode(comgrouplay.MULTIROW_LAYOUT);
      
      comgroupbar.defineCommand(comgroupbar.NEWROW,"newRowCOMGROUP");
      
      comgrouptbl = mgr.newASPTable(comgroupblk);
      comgrouptbl.setTitle("MPCCOWCOSTDETAILBASICDATACOMGROUPTAB: Commodity Group");


      //Inventory Part Type tab
      
      invparttypeblk = mgr.newASPBlock("INVPARTTYPE");
      
      f = invparttypeblk.addField("INVPARTTYPE_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
      
      f = invparttypeblk.addField("INVPARTTYPE_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
      
      f = invparttypeblk.addField("TYPE_CODE");
      f.setSize(30);
      f.setSelectBox();
      f.enumerateValues("Inventory_Part_Type_API");
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setLabel("MPCCOWCOSTDETAILBASICDATATYPECODE: Part Type");
      
      f = invparttypeblk.addField("INVPARTTYPE_COST_SOURCE_ID");
      f.setSize(22);
      f.setDynamicLOV("COST_SOURCE","COMPANY");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID"));
      f.setMandatory();
      f.setInsertable();
      f.setDbName("COST_SOURCE_ID");
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID");
      f.setUpperCase();
      f.setValidation("Cost_Source_API.Get_Description","INVPARTTYPE_COMPANY, INVPARTTYPE_COST_SOURCE_ID","INVPARTTYPE_COST_SOURCE_DESC");

      f = invparttypeblk.addField("INVPARTTYPE_COST_SOURCE_DESC");
      f.setSize(30);
      f.setFunction("Cost_Source_API.Get_Description(:INVPARTTYPE_COMPANY, :INVPARTTYPE_COST_SOURCE_ID)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEDESC: Cost Source Description");

      f = invparttypeblk.addField("INVPARTTYPE_VALID_FROM_DATE","Date");
      f.setSize(22);
      f.setDbName("VALID_FROM_DATE");
      f.setInsertable();
      f.setMandatory();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAVALIDFROMDATE: Valid From");

      f = invparttypeblk.addField("INVPARTTYPE_COMPANY");
      f.setHidden();
      f.setDbName("COMPANY");
      
      invparttypeblk.setView("INV_PART_TYPE_COST_SOURCE");
      invparttypeblk.defineCommand("INV_PART_TYPE_COST_SOURCE_API","New__,Modify__,Remove__");
      invparttypeblk.setMasterBlock(headblk);
      
      invparttypeset = invparttypeblk.getASPRowSet();
      
      invparttypebar = mgr.newASPCommandBar(invparttypeblk);
      
      invparttypelay = invparttypeblk.getASPBlockLayout();
      invparttypelay.setDefaultLayoutMode(invparttypelay.MULTIROW_LAYOUT);
      
      invparttypebar.defineCommand(invparttypebar.NEWROW,"newRowINVPARTTYPE");
      
      invparttypetbl = mgr.newASPTable(invparttypeblk);
      invparttypetbl.setTitle("MPCCOWCOSTDETAILBASICDATAINVPARTTYPETAB: Inventory Part Type");

      //Product Code tab
      
      productcodeblk = mgr.newASPBlock("PRODUCTCODE");
      
      f = productcodeblk.addField("PRODUCTCODE_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
      
      f = productcodeblk.addField("PRODUCTCODE_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
      
      f = productcodeblk.addField("PART_PRODUCT_CODE");
      f.setSize(8);
      f.setDynamicLOV("INVENTORY_PRODUCT_CODE");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATAPRODUCTCODE: Product Code"));
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAPRODUCTCODE: Product Code");
      f.setUpperCase();
      f.setValidation("Inventory_Product_Code_API.Get_Description","PART_PRODUCT_CODE","PART_PRODUCT_CODE_DESC");
      
      f = productcodeblk.addField("PART_PRODUCT_CODE_DESC");
      f.setSize(30);
      f.setFunction("Inventory_Product_Code_API.Get_Description(PART_PRODUCT_CODE)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAPRODUCTCODEDESC: Product Code Description");

      f = productcodeblk.addField("PRODUCTCODE_COST_SOURCE_ID");
      f.setSize(22);
      f.setDynamicLOV("COST_SOURCE","COMPANY");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID"));
      f.setMandatory();
      f.setInsertable();
      f.setDbName("COST_SOURCE_ID");
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID");
      f.setUpperCase();
      f.setValidation("Cost_Source_API.Get_Description","PRODUCTCODE_COMPANY, PRODUCTCODE_COST_SOURCE_ID","PRODUCTCODE_COST_SOURCE_DESC");

      f = productcodeblk.addField("PRODUCTCODE_COST_SOURCE_DESC");
      f.setSize(30);
      f.setFunction("Cost_Source_API.Get_Description(:PRODUCTCODE_COMPANY, :PRODUCTCODE_COST_SOURCE_ID)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEDESC: Cost Source Description");

      f = productcodeblk.addField("PRODUCTCODE_VALID_FROM_DATE","Date");
      f.setSize(22);
      f.setDbName("VALID_FROM_DATE");
      f.setInsertable();
      f.setMandatory();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAVALIDFROMDATE: Valid From");

      f = productcodeblk.addField("PRODUCTCODE_COMPANY");
      f.setHidden();
      f.setDbName("COMPANY");
      
      productcodeblk.setView("INV_PROD_CODE_COST_SOURCE");
      productcodeblk.defineCommand("INV_PROD_CODE_COST_SOURCE_API","New__,Modify__,Remove__");
      productcodeblk.setMasterBlock(headblk);
      
      productcodeset = productcodeblk.getASPRowSet();
      
      productcodebar = mgr.newASPCommandBar(productcodeblk);
      
      productcodelay = productcodeblk.getASPBlockLayout();
      productcodelay.setDefaultLayoutMode(productcodelay.MULTIROW_LAYOUT);
      
      productcodebar.defineCommand(productcodebar.NEWROW,"newRowPRODUCTCODE");
      
      productcodetbl = mgr.newASPTable(productcodeblk);
      productcodetbl.setTitle("MPCCOWCOSTDETAILBASICDATAPRODUCTCODETAB: Product Code");
      

      //ABC Class tab
      
      abcclassblk = mgr.newASPBlock("ABCCLASS");
      
      f = abcclassblk.addField("ABCCLASS_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
      
      f = abcclassblk.addField("ABCCLASS_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
      
      f = abcclassblk.addField("ABC_CLASS");
      f.setSize(3);
      f.setDynamicLOV("ABC_CLASS");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATAABCCLASS: ABC Class"));
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAABCCLASS: ABC Class");
      f.setUpperCase();
      f.setValidation("ABC_CLASS_API.Get_Abc_Percent","ABC_CLASS","ABC_PER");
      
      f = abcclassblk.addField("ABC_PER");
      f.setSize(30);
      f.setFunction("ABC_CLASS_API.Get_Abc_Percent(ABC_CLASS)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAABCPER: ABC %");

      f = abcclassblk.addField("ABCCLASS_COST_SOURCE_ID");
      f.setSize(22);
      f.setDynamicLOV("COST_SOURCE","COMPANY");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID"));
      f.setMandatory();
      f.setInsertable();
      f.setDbName("COST_SOURCE_ID");
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID");
      f.setUpperCase();
      f.setValidation("Cost_Source_API.Get_Description","ABCCLASS_COMPANY, ABCCLASS_COST_SOURCE_ID","ABCCLASS_COST_SOURCE_DESC");

      f = abcclassblk.addField("ABCCLASS_COST_SOURCE_DESC");
      f.setSize(30);
      f.setFunction("Cost_Source_API.Get_Description(:ABCCLASS_COMPANY, :ABCCLASS_COST_SOURCE_ID)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEDESC: Cost Source Description");

      f = abcclassblk.addField("ABCCLASS_VALID_FROM_DATE","Date");
      f.setSize(22);
      f.setDbName("VALID_FROM_DATE");
      f.setInsertable();
      f.setMandatory();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAVALIDFROMDATE: Valid From");

      f = abcclassblk.addField("ABCCLASS_COMPANY");
      f.setHidden();
      f.setDbName("COMPANY");
      
      abcclassblk.setView("ABC_CLASS_COST_SOURCE");
      abcclassblk.defineCommand("ABC_CLASS_COST_SOURCE_API","New__,Modify__,Remove__");
      abcclassblk.setMasterBlock(headblk);
      
      abcclassset = abcclassblk.getASPRowSet();
      
      abcclassbar = mgr.newASPCommandBar(abcclassblk);
      
      abcclasslay = abcclassblk.getASPBlockLayout();
      abcclasslay.setDefaultLayoutMode(abcclasslay.MULTIROW_LAYOUT);
      
      abcclassbar.defineCommand(abcclassbar.NEWROW,"newRowABCCLASS");
      
      abcclasstbl = mgr.newASPTable(abcclassblk);
      abcclasstbl.setTitle("MPCCOWCOSTDETAILBASICDATAABCCLASSTAB: ABC Class");


      //Product Family tab
      
      prodfamilyblk = mgr.newASPBlock("PRODFAMILY");
      
      f = prodfamilyblk.addField("PRODFAMILY_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
      
      f = prodfamilyblk.addField("PRODFAMILY_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
      
      f = prodfamilyblk.addField("PART_PRODUCT_FAMILY");
      f.setSize(8);
      f.setDynamicLOV("INVENTORY_PRODUCT_FAMILY");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATAPRODFAMILY: Product Family"));
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAPRODFAMILY: Product Family");
      f.setUpperCase();
      f.setValidation("INVENTORY_PRODUCT_FAMILY_API.Get_Description","PART_PRODUCT_FAMILY","PART_PRODUCT_FAMILY_DESC");
      
      f = prodfamilyblk.addField("PART_PRODUCT_FAMILY_DESC");
      f.setSize(30);
      f.setFunction("INVENTORY_PRODUCT_FAMILY_API.Get_Description(PART_PRODUCT_FAMILY)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAPRODFAMILYDESC: Product Family Description");

      f = prodfamilyblk.addField("PRODFAMILY_COST_SOURCE_ID");
      f.setSize(22);
      f.setDynamicLOV("COST_SOURCE","COMPANY");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID"));
      f.setMandatory();
      f.setInsertable();
      f.setDbName("COST_SOURCE_ID");
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID");
      f.setUpperCase();
      f.setValidation("Cost_Source_API.Get_Description","PRODFAMILY_COMPANY, PRODFAMILY_COST_SOURCE_ID","PRODFAMILY_COST_SOURCE_DESC");

      f = prodfamilyblk.addField("PRODFAMILY_COST_SOURCE_DESC");
      f.setSize(30);
      f.setFunction("Cost_Source_API.Get_Description(:PRODFAMILY_COMPANY, :PRODFAMILY_COST_SOURCE_ID)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEDESC: Cost Source Description");

      f = prodfamilyblk.addField("PRODFAMILY_VALID_FROM_DATE","Date");
      f.setSize(22);
      f.setDbName("VALID_FROM_DATE");
      f.setInsertable();
      f.setMandatory();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAVALIDFROMDATE: Valid From");

      f = prodfamilyblk.addField("PRODFAMILY_COMPANY");
      f.setHidden();
      f.setDbName("COMPANY");
      
      prodfamilyblk.setView("INV_PROD_FAM_COST_SOURCE");
      prodfamilyblk.defineCommand("INV_PROD_FAM_COST_SOURCE_API","New__,Modify__,Remove__");
      prodfamilyblk.setMasterBlock(headblk);
      
      prodfamilyset = prodfamilyblk.getASPRowSet();
      
      prodfamilybar = mgr.newASPCommandBar(prodfamilyblk);
      
      prodfamilylay = prodfamilyblk.getASPBlockLayout();
      prodfamilylay.setDefaultLayoutMode(prodfamilylay.MULTIROW_LAYOUT);
      
      prodfamilybar.defineCommand(prodfamilybar.NEWROW,"newRowPRODFAMILY");
      
      prodfamilytbl = mgr.newASPTable(prodfamilyblk);
      prodfamilytbl.setTitle("MPCCOWCOSTDETAILBASICDATAPRODFAMILYTAB: Product Family");


      //Planner tab
      
      plannerblk = mgr.newASPBlock("PLANNER");
      
      f = plannerblk.addField("PLANNER_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
      
      f = plannerblk.addField("PLANNER_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
      
      f = plannerblk.addField("PLANNER_BUYER_CODE");
      f.setSize(22);
      f.setDynamicLOV("INVENTORY_PART_PLANNER");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATAPLANNER: Planner ID"));
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setDbName("BUYER_CODE");
      f.setLabel("MPCCOWCOSTDETAILBASICDATAPLANNER: Planner ID");
      f.setUpperCase();
      f.setValidation("INVENTORY_PART_PLANNER_API.Get_Buyer_Name","PLANNER_BUYER_CODE","BUYER_CODE_NAME");
      
      f = plannerblk.addField("BUYER_CODE_NAME");
      f.setSize(30);
      f.setFunction("INVENTORY_PART_PLANNER_API.Get_Buyer_Name(:PLANNER_BUYER_CODE)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAPLANNERDESC: Name");

      f = plannerblk.addField("PLANNER_COST_SOURCE_ID");
      f.setSize(22);
      f.setDynamicLOV("COST_SOURCE","COMPANY");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID"));
      f.setMandatory();
      f.setInsertable();
      f.setDbName("COST_SOURCE_ID");
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID");
      f.setUpperCase();
      f.setValidation("Cost_Source_API.Get_Description","PLANNER_COMPANY, PLANNER_COST_SOURCE_ID","PLANNER_COST_SOURCE_DESC");

      f = plannerblk.addField("PLANNER_COST_SOURCE_DESC");
      f.setSize(30);
      f.setFunction("Cost_Source_API.Get_Description(:PLANNER_COMPANY, :PLANNER_COST_SOURCE_ID)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEDESC: Cost Source Description");

      f = plannerblk.addField("PLANNER_VALID_FROM_DATE","Date");
      f.setSize(22);
      f.setDbName("VALID_FROM_DATE");
      f.setInsertable();
      f.setMandatory();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAVALIDFROMDATE: Valid From");

      f = plannerblk.addField("PLANNER_COMPANY");
      f.setHidden();
      f.setDbName("COMPANY");
      
      plannerblk.setView("INV_PART_PLAN_COST_SOURCE");
      plannerblk.defineCommand("INV_PART_PLAN_COST_SOURCE_API","New__,Modify__,Remove__");
      plannerblk.setMasterBlock(headblk);
      
      plannerset = plannerblk.getASPRowSet();
      
      plannerbar = mgr.newASPCommandBar(plannerblk);
      
      plannerlay = plannerblk.getASPBlockLayout();
      plannerlay.setDefaultLayoutMode(plannerlay.MULTIROW_LAYOUT);
      
      plannerbar.defineCommand(plannerbar.NEWROW,"newRowPLANNER");
      
      plannertbl = mgr.newASPTable(plannerblk);
      plannertbl.setTitle("MPCCOWCOSTDETAILBASICDATAPLANNERTAB: Planner");


      //Accounting Group tab
      
      accgroupblk = mgr.newASPBlock("ACCGROUP");
      
      f = accgroupblk.addField("ACCGROUP_OBJID");
      f.setHidden();
      f.setDbName("OBJID");
      
      f = accgroupblk.addField("ACCGROUP_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");
      
      f = accgroupblk.addField("ACCOUNTING_GROUP");
      f.setSize(8);
      f.setDynamicLOV("ACCOUNTING_GROUP");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATAACCGROUP: Accounting Group"));
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAACCGROUP: Accounting Group");
      f.setUpperCase();
      f.setValidation("ACCOUNTING_GROUP_API.Get_Description","ACCOUNTING_GROUP","ACCOUNTING_GROUP_DESC");
      
      f = accgroupblk.addField("ACCOUNTING_GROUP_DESC");
      f.setSize(30);
      f.setFunction("ACCOUNTING_GROUP_API.Get_Description(ACCOUNTING_GROUP)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAACCGROUPDESC: Accounting Group Description");

      f = accgroupblk.addField("ACCGROUP_COST_SOURCE_ID");
      f.setSize(22);
      f.setDynamicLOV("COST_SOURCE","COMPANY");
      f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID"));
      f.setMandatory();
      f.setInsertable();
      f.setDbName("COST_SOURCE_ID");
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID");
      f.setUpperCase();
      f.setValidation("Cost_Source_API.Get_Description","ACCGROUP_COMPANY, ACCGROUP_COST_SOURCE_ID","ACCGROUP_COST_SOURCE_DESC");

      f = accgroupblk.addField("ACCGROUP_COST_SOURCE_DESC");
      f.setSize(30);
      f.setFunction("Cost_Source_API.Get_Description(:ACCGROUP_COMPANY, :ACCGROUP_COST_SOURCE_ID)");
      f.setReadOnly();
      f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEDESC: Cost Source Description");

      f = accgroupblk.addField("ACCGROUP_VALID_FROM_DATE","Date");
      f.setSize(22);
      f.setDbName("VALID_FROM_DATE");
      f.setInsertable();
      f.setMandatory();
      f.setLabel("MPCCOWCOSTDETAILBASICDATAVALIDFROMDATE: Valid From");

      f = accgroupblk.addField("ACCGROUP_COMPANY");
      f.setHidden();
      f.setDbName("COMPANY");
      
      accgroupblk.setView("ACC_GROUP_COST_SOURCE");
      accgroupblk.defineCommand("ACC_GROUP_COST_SOURCE_API","New__,Modify__,Remove__");
      accgroupblk.setMasterBlock(headblk);
      
      accgroupset = accgroupblk.getASPRowSet();
      
      accgroupbar = mgr.newASPCommandBar(accgroupblk);
      
      accgrouplay = accgroupblk.getASPBlockLayout();
      accgrouplay.setDefaultLayoutMode(accgrouplay.MULTIROW_LAYOUT);
      
      accgroupbar.defineCommand(accgroupbar.NEWROW,"newRowACCGROUP");
      
      accgrouptbl = mgr.newASPTable(accgroupblk);
      accgrouptbl.setTitle("MPCCOWCOSTDETAILBASICDATAACCGROUPTAB: Accounting Group");


      if (mfgstd_installed)
      {
         //Labor Class tab
      
         laborclassblk = mgr.newASPBlock("LABORCLASS");
         
         f = laborclassblk.addField("LABORCLASS_OBJID");
         f.setHidden();
         f.setDbName("OBJID");
         
         f = laborclassblk.addField("LABORCLASS_OBJVERSION");
         f.setHidden();
         f.setDbName("OBJVERSION");
   
         f = laborclassblk.addField("LABORCLASS_CONTRACT");
         f.setSize(8);
         f.setDynamicLOV("SITE");
         f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATALABORCLASSSITE: Site"));
         f.setMandatory();
         f.setReadOnly();
         f.setInsertable();
         f.setLabel("MPCCOWCOSTDETAILBASICDATALABORCLASSSITE: Site");
         f.setUpperCase();
         f.setDbName("CONTRACT");
         
         f = laborclassblk.addField("LABOR_CLASS_NO");
         f.setSize(12);
         f.setDynamicLOV("LABOR_CLASS","LABORCLASS_CONTRACT CONTRACT");
         f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATALABORCLASS: Labor Class"));
         f.setMandatory();
         f.setReadOnly();
         f.setInsertable();
         f.setLabel("MPCCOWCOSTDETAILBASICDATALABORCLASS: Labor Class");
         f.setUpperCase();
         f.setValidation("Labor_Class_API.Get_Labor_Class_Description","LABORCLASS_CONTRACT, LABOR_CLASS_NO","LABOR_CLASS_NO_DESC");
         
         f = laborclassblk.addField("LABOR_CLASS_NO_DESC");
         f.setSize(30);
         f.setFunction("Labor_Class_API.Get_Labor_Class_Description(:LABORCLASS_CONTRACT, :LABOR_CLASS_NO)");
         f.setReadOnly();
         f.setLabel("MPCCOWCOSTDETAILBASICDATALABORCLASSDESC: Labor Class Description");
   
         f = laborclassblk.addField("LABORCLASS_COST_SOURCE_ID");
         f.setSize(22);
         f.setDynamicLOV("COST_SOURCE","COMPANY");
         f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID"));
         f.setMandatory();
         f.setInsertable();
         f.setDbName("COST_SOURCE_ID");
         f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID");
         f.setUpperCase();
         f.setValidation("Cost_Source_API.Get_Description","LABORCLASS_COMPANY, LABORCLASS_COST_SOURCE_ID","LABORCLASS_COST_SOURCE_DESC");
   
         f = laborclassblk.addField("LABORCLASS_COST_SOURCE_DESC");
         f.setSize(30);
         f.setFunction("Cost_Source_API.Get_Description(:LABORCLASS_COMPANY, :LABORCLASS_COST_SOURCE_ID)");
         f.setReadOnly();
         f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEDESC: Cost Source Description");
   
         f = laborclassblk.addField("LABORCLASS_VALID_FROM_DATE","Date");
         f.setSize(22);
         f.setDbName("VALID_FROM_DATE");
         f.setInsertable();
         f.setMandatory();
         f.setLabel("MPCCOWCOSTDETAILBASICDATAVALIDFROMDATE: Valid From");
   
         f = laborclassblk.addField("LABORCLASS_COMPANY");
         f.setHidden();
         f.setDbName("COMPANY");
         
         laborclassblk.setView("LABOR_CLASS_COST_SOURCE");
         laborclassblk.defineCommand("LABOR_CLASS_COST_SOURCE_API","New__,Modify__,Remove__");
         laborclassblk.setMasterBlock(headblk);
         
         laborclassset = laborclassblk.getASPRowSet();
         
         laborclassbar = mgr.newASPCommandBar(laborclassblk);
         
         laborclasslay = laborclassblk.getASPBlockLayout();
         laborclasslay.setDefaultLayoutMode(laborclasslay.MULTIROW_LAYOUT);
         
         laborclassbar.defineCommand(laborclassbar.NEWROW,"newRowLABORCLASS");
         
         laborclasstbl = mgr.newASPTable(laborclassblk);
         laborclasstbl.setTitle("MPCCOWCOSTDETAILBASICDATALABORCLASSTAB: Labor Class");


         //Work Center tab
      
         workcenblk = mgr.newASPBlock("WORKCEN");
         
         f = workcenblk.addField("WORKCEN_OBJID");
         f.setHidden();
         f.setDbName("OBJID");
         
         f = workcenblk.addField("WORKCEN_OBJVERSION");
         f.setHidden();
         f.setDbName("OBJVERSION");
   
         f = workcenblk.addField("WORKCEN_CONTRACT");
         f.setSize(8);
         f.setDynamicLOV("SITE");
         f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATAWORKCENSITE: Site"));
         f.setMandatory();
         f.setReadOnly();
         f.setInsertable();
         f.setLabel("MPCCOWCOSTDETAILBASICDATAWORKCENSITE: Site");
         f.setUpperCase();
         f.setDbName("CONTRACT");
         
         f = workcenblk.addField("WORK_CENTER_NO");
         f.setSize(12);
         f.setDynamicLOV("WORK_CENTER","WORKCEN_CONTRACT CONTRACT");
         f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATAWORKCEN: Work Center"));
         f.setMandatory();
         f.setReadOnly();
         f.setInsertable();
         f.setLabel("MPCCOWCOSTDETAILBASICDATAWORKCEN: Work Center");
         f.setUpperCase();
         f.setValidation("Work_Center_API.Get_Description","WORKCEN_CONTRACT, WORK_CENTER_NO","WORK_CENTER_NO_DESC");
         
         f = workcenblk.addField("WORK_CENTER_NO_DESC");
         f.setSize(30);
         f.setFunction("Work_Center_API.Get_Description(:WORKCEN_CONTRACT, :WORK_CENTER_NO)");
         f.setReadOnly();
         f.setLabel("MPCCOWCOSTDETAILBASICDATAWORKCENDESC: Work Center Description");
   
         f = workcenblk.addField("WORKCEN_COST_SOURCE_ID");
         f.setSize(22);
         f.setDynamicLOV("COST_SOURCE","COMPANY");
         f.setLOVProperty("TITLE",mgr.translate("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID"));
         f.setMandatory();
         f.setInsertable();
         f.setDbName("COST_SOURCE_ID");
         f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEID: Cost Source ID");
         f.setUpperCase();
         f.setValidation("Cost_Source_API.Get_Description","WORKCEN_COMPANY, WORKCEN_COST_SOURCE_ID","WORKCEN_COST_SOURCE_DESC");
   
         f = workcenblk.addField("WORKCEN_COST_SOURCE_DESC");
         f.setSize(30);
         f.setFunction("Cost_Source_API.Get_Description(:WORKCEN_COMPANY, :WORKCEN_COST_SOURCE_ID)");
         f.setReadOnly();
         f.setLabel("MPCCOWCOSTDETAILBASICDATACOSTSOURCEDESC: Cost Source Description");
   
         f = workcenblk.addField("WORKCEN_VALID_FROM_DATE","Date");
         f.setSize(22);
         f.setDbName("VALID_FROM_DATE");
         f.setInsertable();
         f.setMandatory();
         f.setLabel("MPCCOWCOSTDETAILBASICDATAVALIDFROMDATE: Valid From");
   
         f = workcenblk.addField("WORKCEN_COMPANY");
         f.setHidden();
         f.setDbName("COMPANY");
         
         workcenblk.setView("WORK_CENTER_COST_SOURCE");
         workcenblk.defineCommand("WORK_CENTER_COST_SOURCE_API","New__,Modify__,Remove__");
         workcenblk.setMasterBlock(headblk);
         
         workcenset = workcenblk.getASPRowSet();
         
         workcenbar = mgr.newASPCommandBar(workcenblk);
         
         workcenlay = workcenblk.getASPBlockLayout();
         workcenlay.setDefaultLayoutMode(workcenlay.MULTIROW_LAYOUT);
         
         workcenbar.defineCommand(workcenbar.NEWROW,"newRowWORKCEN");
         
         workcentbl = mgr.newASPTable(workcenblk);
         workcentbl.setTitle("MPCCOWCOSTDETAILBASICDATAWORKCENTAB: Work Center");
      }


      if (cost_installed)
      {
         //Posting Cost Group tab
      
         itemblk4 = mgr.newASPBlock("ITEM4");
         
         f = itemblk4.addField("ITEM4_OBJID");
         f.setHidden();
         f.setDbName("OBJID");
         
         f = itemblk4.addField("ITEM4_OBJVERSION");
         f.setHidden();
         f.setDbName("OBJVERSION");
         
         f = itemblk4.addField("POSTING_GROUP_ID");
         f.setSize(22);
         f.setMaxLength(20);
         f.setMandatory();
         f.setReadOnly();
         f.setInsertable();
         f.setLabel("MPCCOWCOSTDETAILBASICDATAPOSTINGGROUPID: Posting Cost Group");
         f.setUpperCase();
         
         f = itemblk4.addField("ITEM4_DESCRIPTION");
         f.setSize(40);
         f.setMaxLength(100);
         f.setDbName("DESCRIPTION");
         f.setLabel("MPCCOWCOSTDETAILBASICDATAPOSTINGGROUPDESC: Posting Cost Group Description");
   
         f = itemblk4.addField("ITEM4_COMPANY");
         f.setHidden();
         f.setDbName("COMPANY");
         
         itemblk4.setView("COST_BUCKET_POSTING_GROUP");
         itemblk4.defineCommand("COST_BUCKET_POSTING_GROUP_API","New__,Modify__,Remove__");
         itemblk4.setMasterBlock(headblk);
         
         itemset4 = itemblk4.getASPRowSet();
         
         itembar4 = mgr.newASPCommandBar(itemblk4);
         
         itemlay4 = itemblk4.getASPBlockLayout();
         itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT);
         
         itembar4.defineCommand(itembar4.NEWROW,"newRowITEM4");
         
         itemtbl4 = mgr.newASPTable(itemblk4);
         itemtbl4.setTitle("MPCCOWCOSTDETAILBASICDATAPOSTINGCOSTGROUP: Posting Cost Group");
      }
      
      tabs = mgr.newASPTabContainer();
      tabs.setDirtyFlagEnabled(false);
      tabs.addTab(mgr.translate("MPCCOWCOSTDETAILBASICDATACOSTSRC: Cost Source"),"javascript:commandSet('HEAD.activateCostSource','')");
      tabs.addTab(mgr.translate("MPCCOWCOSTDETAILBASICDATACOSTSRCINDEF: Cost Source Indicator Definition"),"javascript:commandSet('HEAD.activateCostSourceIndicatorDefinition','')");
      tabs.addTab(mgr.translate("MPCCOWCOSTDETAILBASICDATACOSTSRCINS: Cost Source Indicators"),"javascript:commandSet('HEAD.activateCostSourceIndicators','')");
      if (cost_installed)
      {
         tabs.addTab(mgr.translate("MPCCOWCOSTDETAILBASICDATAPOSTCOSTGP: Posting Cost Group"),"javascript:commandSet('HEAD.activatePostingCostGroup','')");
      }
      
      tabsIndicators = newASPTabContainer("COSTINDICATORS");
      tabsIndicators.addTab("MPCCOWCOSTDETAILBASICDATACOSTSSITE: Site", "javascript:commandSet('ITEM2.activateSite','')");
      tabsIndicators.addTab("MPCCOWCOSTDETAILBASICDATACOSTSPURCHASER: Purchaser", "javascript:commandSet('ITEM2.activatePurchaser','')");
      tabsIndicators.addTab("MPCCOWCOSTDETAILBASICDATACOSTSREQUISITIONER: Requisitioner", "javascript:commandSet('ITEM2.activateRequisitioner','')");
      tabsIndicators.addTab("MPCCOWCOSTDETAILBASICDATACOSTSPURGROUP: Purchase Group", "javascript:commandSet('ITEM2.activatePurGroup','')");
      tabsIndicators.addTab("MPCCOWCOSTDETAILBASICDATACOSTSASSETCLASS: Asset Class", "javascript:commandSet('ITEM2.activateAssetClass','')");
      tabsIndicators.addTab("MPCCOWCOSTDETAILBASICDATACOSTSCOMGROUP: Commodity Group", "javascript:commandSet('ITEM2.activateComGroup','')");
      tabsIndicators.addTab("MPCCOWCOSTDETAILBASICDATACOSTSINVPARTTYPE: Inventory Part Type", "javascript:commandSet('ITEM2.activateInvPartType','')");
      tabsIndicators.addTab("MPCCOWCOSTDETAILBASICDATACOSTSPRODUCTCODE: Product Code", "javascript:commandSet('ITEM2.activateProductCode','')");
      

      tabsIndicators1 = newASPTabContainer("COSTINDICATORS1");
      tabsIndicators1.addTab("MPCCOWCOSTDETAILBASICDATACOSTSABCCLASS: ABC Class", "javascript:commandSet('ITEM3.activateAbcClass','')");
      tabsIndicators1.addTab("MPCCOWCOSTDETAILBASICDATACOSTSPRODFAMILY: Product Family", "javascript:commandSet('ITEM3.activateProdFamily','')");
      tabsIndicators1.addTab("MPCCOWCOSTDETAILBASICDATACOSTSPLANNER: Planner", "javascript:commandSet('ITEM3.activatePlanner','')");
      tabsIndicators1.addTab("MPCCOWCOSTDETAILBASICDATACOSTSACCGROUP: Accounting Group", "javascript:commandSet('ITEM3.activateAccGroup','')");
      if (mfgstd_installed)
      {
         tabsIndicators1.addTab("MPCCOWCOSTDETAILBASICDATACOSTSLABORCLASS: Labor Class", "javascript:commandSet('ITEM3.activateLaborClass','')");
         tabsIndicators1.addTab("MPCCOWCOSTDETAILBASICDATACOSTSWORKCEN: Work Center", "javascript:commandSet('ITEM3.activateWorkCenter','')");
      }
      

      tabs.setLeftTabSpace(3);
      tabs.setContainerSpace(6);
      tabs.setTabWidth(100);
   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();

      if ( headset.countRows() == 0 ) 
      {
         headbar.disableCommand(headbar.EDITROW);
         headbar.disableCommand(headbar.BACK);
         headbar.disableCommand(headbar.FORWARD);
         headbar.disableCommand(headbar.BACKWARD);
         itembar0.disableCommand(itembar0.NEWROW);
      }
      
      if ( itemset0.countRows() == 0 ) 
      {
         itembar0.disableCommand(itembar0.DUPLICATEROW);
         itembar0.disableCommand(itembar0.DELETE);
         itembar0.disableCommand(itembar0.EDITROW);
         ovheadbar.disableCommand(ovheadbar.NEWROW);
      }

      if (siteset.countRows() > 0)
      {
         mgr.getASPField("CONTRACT").setLOVProperty("WHERE","COMPANY = '"+headset.getValue("COMPANY")+"' AND CONTRACT = User_Allowed_Site_API.Authorized(CONTRACT)");
      }
      if (mfgstd_installed)
      {
         if (laborclassset.countRows() > 0)
         {
            mgr.getASPField("LABORCLASS_CONTRACT").setLOVProperty("WHERE","COMPANY = '"+headset.getValue("COMPANY")+"' AND EXISTS (SELECT 1 FROM user_allowed_site_pub uasp WHERE uasp.site = CONTRACT)");
         }
         if (workcenset.countRows() > 0)
         {
            mgr.getASPField("WORKCEN_CONTRACT").setLOVProperty("WHERE","COMPANY = '"+headset.getValue("COMPANY")+"' AND EXISTS (SELECT 1 FROM user_allowed_site_pub uasp WHERE uasp.site = CONTRACT)");
         }
      }
      
      
      headbar.removeCustomCommand("activateCostSource");
      headbar.removeCustomCommand("activateCostSourceIndicatorDefinition");
      headbar.removeCustomCommand("activateCostSourceIndicators");
      if (cost_installed)
      {
         headbar.removeCustomCommand("activatePostingCostGroup");
      }
   }


   public String  tabsInit()
   {
      return tabs.showTabsInit();
   }

   public String  tabsFinish()
   {
      return tabs.showTabsFinish();
   }

   public String  tabsIndicatorsInit()
   {
      return tabsIndicators.showTabsInit();
   }

   public String  tabsIndicatorsFinish()
   {
      return tabsIndicators.showTabsFinish();
   }

   public String  tabsIndicators1Init()
   {
      return tabsIndicators1.showTabsInit();
   }

   public String  tabsIndicators1Finish()
   {
      return tabsIndicators1.showTabsFinish();
   }

   public void  activateCostSource()
   {
      tabs.setActiveTab(1);
   }

   public void  activateCostSourceIndicatorDefinition()
   {
      tabs.setActiveTab(2);
   }

   public void  activateCostSourceIndicators()
   {
      tabs.setActiveTab(3);
   }

   public void  activatePostingCostGroup()
   {
      tabs.setActiveTab(4);
   }



   public void activateSite()
   {
      indicatorsActiveTab = 1;
      tabsIndicators.setActiveTab(1);
   }

   public void activatePurchaser()
   {
      indicatorsActiveTab = 2;
      tabsIndicators.setActiveTab(2);
   }

   public void activateRequisitioner()
   {
      indicatorsActiveTab = 3;
      tabsIndicators.setActiveTab(3);
   }

   public void activatePurGroup()
   {
      indicatorsActiveTab = 4;
      tabsIndicators.setActiveTab(4);
   }

   public void activateAssetClass()
   {
      indicatorsActiveTab = 5;
      tabsIndicators.setActiveTab(5);
   }

   public void activateComGroup()
   {
      indicatorsActiveTab = 6;
      tabsIndicators.setActiveTab(6);
   }

   public void activateInvPartType()
   {
      indicatorsActiveTab = 7;
      tabsIndicators.setActiveTab(7);
   }

   public void activateProductCode()
   {
      indicatorsActiveTab = 8;
      tabsIndicators.setActiveTab(8);
   }
   public void activateAbcClass()
   {
      indicatorsActiveTab = 9;
      tabsIndicators1.setActiveTab(9);
   }

   public void activateProdFamily()
   {
      indicatorsActiveTab = 10;
      tabsIndicators1.setActiveTab(10);
   }

   public void activatePlanner()
   {
      indicatorsActiveTab = 11;
      tabsIndicators1.setActiveTab(11);
   }

   public void activateAccGroup()
   {
      indicatorsActiveTab = 12;
      tabsIndicators1.setActiveTab(12);
   }

   public void activateLaborClass()
   {
      indicatorsActiveTab = 13;
      tabsIndicators1.setActiveTab(13);
   }
   public void activateWorkCenter()
   {
      indicatorsActiveTab = 14;
      tabsIndicators1.setActiveTab(14);
   }


   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      return "MPCCOWCOSTDETAILBASICDATATITLE: Basic Data for Cost Details";
   }

   protected String getTitle()
   {
      return "MPCCOWCOSTDETAILBASICDATATITLE: Basic Data for Cost Details";
   }

   protected void printContents() throws FndException
   { 
      ASPManager mgr = getASPManager();
      
      if (headlay.isVisible()) 
      {
         appendToHTML(headlay.show());
      }
      if (!headlay.isMultirowLayout()) 
      {
         if(headlay.isVisible() && headset.countRows()!=0) 
         {
            if(itemlay0.isVisible() || itemlay1.isVisible() || itemlay2.isVisible() || itemlay3.isVisible())
            {
               if (cost_installed)
               {
                  if (itemlay4.isVisible())
                  {
                     appendToHTML(tabs.showTabsInit());
                  }
               }
               else
               {
                  appendToHTML(tabs.showTabsInit());
               }
            }
         }
      }
      if  ( tabs.getActiveTab() == 1 )  
      {
         if (itemlay0.isVisible() && headset.countRows()!=0) 
         {
            appendToHTML(itemlay0.show());
         }
         if (ovheadlay.isVisible() && headset.countRows()!=0) 
         {
            appendToHTML(ovheadlay.show());
         }
      }
      if  ( tabs.getActiveTab() == 2 )  
      {
         if (itemlay1.isVisible() && headset.countRows()!=0) 
         {
            appendToHTML(itemlay1.show());
         }
      }
      if ( tabs.getActiveTab() == 3 )
      {
         tabsIndicators.setActiveTab(indicatorsActiveTab);
         if (indicatorsActiveTab == 9 || indicatorsActiveTab == 10 || indicatorsActiveTab == 11 || indicatorsActiveTab == 12 || indicatorsActiveTab == 13 || indicatorsActiveTab == 14)
         {
            tabsIndicators1.setActiveTab(indicatorsActiveTab - 8);
            // Bug 84193, setActiveTab accepts values > 1.
            tabsIndicators.setActiveTab(1);
         }
         else
         {
            tabsIndicators.setActiveTab(indicatorsActiveTab);
            // Bug 84193, setActiveTab accepts values > 1.
            tabsIndicators1.setActiveTab(1);
         }

         if (sitelay.isVisible() && purchaserlay.isVisible() && requisitionerlay.isVisible() && purgrouplay.isVisible()
             && assetclasslay.isVisible() && comgrouplay.isVisible() && invparttypelay.isVisible() && productcodelay.isVisible() 
             && abcclasslay.isVisible() && prodfamilylay.isVisible() && plannerlay.isVisible() && accgrouplay.isVisible())
         {
            if (mfgstd_installed)
            {
               if (laborclasslay.isVisible() && workcenlay.isVisible())
               {
                  appendToHTML(tabsIndicatorsInit());
                  appendToHTML(tabsIndicators1Init());
               }
            }
            else
            {
               appendToHTML(tabsIndicatorsInit());
               appendToHTML(tabsIndicators1Init());
            }
         }

         if ( indicatorsActiveTab == 1 )
         {
            if (sitelay.isVisible()) appendToHTML(sitelay.show());
         }
         if ( indicatorsActiveTab == 2 )
         {
            if (purchaserlay.isVisible()) appendToHTML(purchaserlay.show());
         }
         if ( indicatorsActiveTab == 3 )
         {
            if (requisitionerlay.isVisible()) appendToHTML(requisitionerlay.show());
         }
         if ( indicatorsActiveTab == 4 )
         {
            if (purgrouplay.isVisible()) appendToHTML(purgrouplay.show());
         }
         if ( indicatorsActiveTab == 5 )
         {
            if (assetclasslay.isVisible()) appendToHTML(assetclasslay.show());
         }
         if ( indicatorsActiveTab == 6 )
         {
            if (comgrouplay.isVisible()) appendToHTML(comgrouplay.show());
         }
         if ( indicatorsActiveTab == 7 )
         {
            if (invparttypelay.isVisible()) appendToHTML(invparttypelay.show());
         }
         if ( indicatorsActiveTab == 8 )
         {
            if (productcodelay.isVisible()) appendToHTML(productcodelay.show());
         }
         if ( indicatorsActiveTab == 9 )
         {
            if (abcclasslay.isVisible()) appendToHTML(abcclasslay.show());
         }
         if ( indicatorsActiveTab == 10 )
         {
            if (prodfamilylay.isVisible()) appendToHTML(prodfamilylay.show());
         }
         if ( indicatorsActiveTab == 11 )
         {
            if (plannerlay.isVisible()) appendToHTML(plannerlay.show());
         }
         if ( indicatorsActiveTab == 12 )
         {
            if (accgrouplay.isVisible()) appendToHTML(accgrouplay.show());
         }

         if (mfgstd_installed)
         {
            if ( indicatorsActiveTab == 13 )
            {
               if (laborclasslay.isVisible()) appendToHTML(laborclasslay.show());
            }
            if ( indicatorsActiveTab == 14 )
            {
               if (workcenlay.isVisible()) appendToHTML(workcenlay.show());
            }
         }

         
         if (sitelay.isVisible() && purchaserlay.isVisible() && requisitionerlay.isVisible() && purgrouplay.isVisible()
             && assetclasslay.isVisible() && comgrouplay.isVisible() && invparttypelay.isVisible() && productcodelay.isVisible() 
             && abcclasslay.isVisible() && prodfamilylay.isVisible() && plannerlay.isVisible() && accgrouplay.isVisible())
         {
            if (mfgstd_installed)
            {
               if (laborclasslay.isVisible() && workcenlay.isVisible())
               {
                  appendToHTML(tabsIndicatorsFinish());
                  appendToHTML(tabsIndicators1Finish());
               }
            }
            else
            {
               appendToHTML(tabsIndicatorsFinish());
               appendToHTML(tabsIndicators1Finish());
            }
         }
            
      }

      if (cost_installed)
      {
         if  ( tabs.getActiveTab() == 4 )  
         {
            if (itemlay4.isVisible() && headset.countRows()!=0) 
            {
               appendToHTML(itemlay4.show());
            }
         }
      }
      

      if( headlay.isVisible() && (itemlay0.isVisible() || itemlay1.isVisible() || itemlay2.isVisible() || itemlay3.isVisible() )) 
      {
         if (cost_installed)
         {
            if (itemlay4.isVisible())
            {
               appendToHTML(tabs.showTabsFinish());
            }
         }
         else
         {
            appendToHTML(tabs.showTabsFinish());
         }
      }
      
   }
}