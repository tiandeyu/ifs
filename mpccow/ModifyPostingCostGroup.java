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
 *  File        : ModifyPostingCostGroup.java 
 *  Description :
 *  Notes       : 
 * ----------------------------------------------------------------------------
 *  Modified    :
 *
 *    RaKalk    2007-Jul-31  - Modified modifyPostingGroup to use encodeStringForJS to encode java script alert messages
 *    Cpeilk    2007-Jun-25  - Created.
 * ----------------------------------------------------------------------------
 */


package ifs.mpccow;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class ModifyPostingCostGroup extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.mpccow.ModifyPostingCostGroup");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPContext      ctx;

   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPBlockLayout headlay;
   private ASPCommandBar headbar;
   private ASPTable headtbl;

   private ASPBlock itemblk0;
   private ASPRowSet itemset0;
   private ASPBlockLayout itemlay0;
   private ASPCommandBar itembar0;
   private ASPTable itemtbl0;

   private String javascript_message;
   private String costBucketSeq ="";
   
   //===============================================================
   // Construction 
   //===============================================================
   public ModifyPostingCostGroup(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if ( !mgr.isEmpty(mgr.getQueryStringValue("SEARCH")) )
         okFind();
      else if (mgr.buttonPressed("SUBMIT"))
         submit();
      else if (mgr.buttonPressed("CANCEL"))
         cancel();
      else
         okFindAll();

   }

   public void  countFind()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q = trans.addQuery(headblk);
      String n;

      q.setSelectList("to_char(count(*)) N");
      q.addWhereCondition("EXISTS (SELECT 1 FROM user_allowed_site_pub WHERE contract = site)");
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

      trans.clear();
      q = trans.addQuery(headblk);
      q.addWhereCondition("EXISTS (SELECT 1 FROM user_allowed_site_pub WHERE contract = site)");
      q.includeMeta("ALL");

      mgr.querySubmit(trans,headblk);

      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("MPCCOWMODIFYPOSTINGCOSTGROUPNODATA: No data found."));
         headset.clear();
      }
      else
         mgr.createSearchURL(headblk);

      eval(headset.syncItemSets());
   }

   public void  okFindITEM0()
   {
      ASPManager           mgr      = getASPManager();
      ASPTransactionBuffer trans    = mgr.newASPTransactionBuffer();
      int                  nCurrRow =  headset.getCurrentRowNo();
      ASPQuery             q;

      itemset0.clear();
      q = trans.addEmptyQuery(itemblk0);
      q.addWhereCondition("CONTRACT = ?");
      q.addWhereCondition("COST_BUCKET_TYPE_DB NOT IN ('BUCKETS')");
      q.addParameter("CONTRACT", headset.getValue("CONTRACT"));
      q.includeMeta("ALL");
      
      mgr.querySubmit(trans, itemblk0);
      headset.goTo(nCurrRow);
   }

   public void  okFindAll()
   {
      okFind();
      if (headset.countRows() > 0 )
      {
         okFindITEM0();
      }
   }

//-----------------------------------------------------------------------------
//----------------------------  BUTTON FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

   public void  submit()
   {
      ASPManager mgr = getASPManager();
      ctx   = mgr.getASPContext();

      int n = itemset0.countRows();
      itemset0.first();
      for (int i=1; i <= n; i++)
      {
         ctx.writeValue("NEWPOSTINGGROUP"+i, mgr.readValueAt("POSTING_GROUP_ID",i));
         itemset0.next();
      }

      String error_msg = mgr.translate("MPCCOWMODIFYPOSTINGCOSTGROUPREVALUE: Changing the Posting Cost Groups for a Cost Bucket will initiate revaluation transactions for all parts in stock or transit with the Cost Bucket ID in its detailed structure.");
      //set confirm message when the OK button is pressed
      javascript_message = "\nif (confirm('"+ error_msg+ "\\n'))\n   commandSet('ITEM0.modifyPostingGroup','');\n\n";
   }


   public void  cancel()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans    = mgr.newASPTransactionBuffer();
      ASPCommand cmd;

      if (!mgr.isEmpty(costBucketSeq))
      {
         cmd = trans.addCustomCommand( "REMCOSTB", "Temporary_Cost_Bucket_API.Remove_Buckets");
         cmd.addParameter("NCOSTBUCKETSEQ",costBucketSeq);

         trans = mgr.submit(trans);
      }

      mgr.redirectTo("../Navigator.page?MAINMENU=Y&NEW=Y");
   }

   public void  modifyPostingGroup() 
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans    = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      String attr;
      String newCostId = "";
      String oldCostId = "";
      boolean modified = false;
      String msg ="";
      int startmsg = 0;
      int endmsg = 0;
      int index = 1;

      cmd = trans.addCustomCommand( "MODPOSGRP", "Cost_Bucket_API.Modify_Posting_Group");
      cmd.addParameter("NCOSTBUCKETSEQ" );
      cmd.addParameter("CONTRACT", headset.getValue("CONTRACT"));
      cmd.addParameter("ACTION", "CHECK");

      cmd = trans.addCustomFunction( "NEXTCOSTBSEQ", "Temporary_Cost_Bucket_API.Get_Next_Cost_Bucket_Seq","COSTBUCKETSEQ");

      trans = mgr.perform(trans);

      costBucketSeq = trans.getValue("NEXTCOSTBSEQ/DATA/COSTBUCKETSEQ");
      trans.clear();

      
      int n = itemset0.countRows();
      int[] selected = new int[n];

      itemset0.changeRows();
      itemset0.first();
      for (int i=1; i <= n; i++)
      {
         newCostId = ctx.readValue("NEWPOSTINGGROUP"+i, "");
         oldCostId = itemset0.getDbValue("POSTING_GROUP_ID");

         if (!(mgr.isEmpty(oldCostId) && mgr.isEmpty(newCostId)) )
         {
            if (mgr.isEmpty(oldCostId) && !mgr.isEmpty(newCostId))
            {
               modified = true;
            }
            else if (!mgr.isEmpty(oldCostId) && mgr.isEmpty(newCostId))
            {
               modified = true;
            }
            else if (! oldCostId.equals(newCostId))
            {
               modified = true;
            }

            if (modified)
            {
               selected[index++] = i;
               attr = createCostBucketAttr(costBucketSeq, oldCostId, newCostId);

               cmd = trans.addCustomCommand( "MODPOSGRP"+i, "Temporary_Cost_Bucket_API.New");
               cmd.addParameter("INFO" );
               cmd.addParameter("ATTR", attr);
            }
         }
         itemset0.next();
      }

      cmd = trans.addCustomCommand( "MODPOSGRP", "Cost_Bucket_API.Modify_Posting_Group");
      cmd.addParameter("NCOSTBUCKETSEQ",costBucketSeq );
      cmd.addParameter("CONTRACT", headset.getValue("CONTRACT"));
      cmd.addParameter("ACTION", "DO");

      trans = mgr.submit(trans);

      // info handling only for the modified items.
      n = index - 1;
      for (int i=1; i <= n; i++)
      {
         msg ="";
         startmsg=0;
         endmsg=0;
         msg = trans.getValue("MODPOSGRP"+selected[i]+"/DATA/INFO");

         if (!mgr.isEmpty(msg))
         {
            while (msg.length()>1)
            {
               startmsg = msg.indexOf(String.valueOf((char)31),0);
               endmsg = msg.indexOf(String.valueOf((char)30),startmsg);
               
               try
               {
                  appendDirtyJavaScript("   alert('",mgr.encodeStringForJavascript(msg.substring(startmsg + 1,endmsg)),"');\n");
               }
               catch (FndException e)
               {
               }
               msg = msg.substring(endmsg+1);
            }
         }
      }
      
      try
      {
         appendDirtyJavaScript("window.location =APP_ROOT+'Navigator.page?MAINMENU=Y&NEW=Y';\n");
      }
      catch (FndException e)
      {
      }
   }

   public String createCostBucketAttr(String costBucketSeq, String oldCostId, String newCostId)
   {
      ASPManager mgr = getASPManager();
      AutoString subAttr;
      String attr;

      subAttr = new AutoString();
      subAttr.append("COST_BUCKET_SEQ",String.valueOf((char)31),costBucketSeq,String.valueOf((char)30));
      subAttr.append("CONTRACT",String.valueOf((char)31),headset.getValue("CONTRACT"),String.valueOf((char)30));
      subAttr.append("COST_BUCKET_ID",String.valueOf((char)31),itemset0.getValue("COST_BUCKET_ID"),String.valueOf((char)30));
      subAttr.append("OLD_POSTING_GROUP_ID",String.valueOf((char)31),oldCostId,String.valueOf((char)30));
      subAttr.append("NEW_POSTING_GROUP_ID",String.valueOf((char)31),newCostId,String.valueOf((char)30));

      attr = subAttr.toString();
      return attr; 
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID")
         .setHidden();

      headblk.addField("OBJVERSION")
         .setHidden();

      headblk.addField("CONTRACT")
         .setSize(8)
         .setUpperCase()
         .setLabel("MPCCOWMODIFYPOSTINGCOSTGROUPCONTRACT: Site")
         .setReadOnly();

      headblk.addField("DESCRIPTION")
         .setSize(30)
         .setFunction("Site_API.Get_Description(CONTRACT)")
         .setLabel("MPCCOWMODIFYPOSTINGCOSTGROUPDESCRIPTION: Description")
         .setReadOnly();

      headblk.setView("SITE");
      
      headset = headblk.getASPRowSet();

      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(ASPBlockLayout.SINGLE_LAYOUT);
      headlay.unsetAutoLayoutSelect();
      headbar = mgr.newASPCommandBar(headblk);

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle("MPCCOWMODIFYPOSTINGCOSTGROUPTITLESITE: Site");
      
      
      itemblk0 = mgr.newASPBlock("ITEM0");

      itemblk0.addField("ITEM0_OBJID").
         setHidden().
         setDbName("OBJID");

      itemblk0.addField("ITEM0_OBJVERSION").
         setHidden().
         setDbName("OBJVERSION");

      itemblk0.addField("COST_BUCKET_ID")
         .setSize(8)
         .setReadOnly()
         .setUpperCase()
         .setLabel("MPCCOWMODIFYPOSTINGCOSTGROUPCOSTBUCKETID: Cost Bucket ID");

      itemblk0.addField("COST_BUCKET_DESC")
         .setSize(35)
         .setDbName("DESCRIPTION")
         .setReadOnly()
         .setLabel("MPCCOWMODIFYPOSTINGCOSTGROUPCOSTBUCKETDESC: Cost Bucket Description");

      itemblk0.addField("OLD_POSTING_GROUP")
         .setDbName("POSTING_GROUP_ID")
         .setSize(20)
         .setReadOnly()
         .setUpperCase()
         .setLabel("MPCCOWMODIFYPOSTINGCOSTGROUPOLDPOSTINGGROUP: Old Posting Cost Group");

      itemblk0.addField("POSTING_GROUP_ID")
         .setSize(20)
         .setDynamicLOV("COST_BUCKET_POSTING_GROUP","COMPANY")
         .setLOVProperty("TITLE",mgr.translate("MPCCOWMODIFYPOSTINGCOSTGROUPNEWPOSTCGP: New Posting Control Groups"))
         .setLabel("MPCCOWMODIFYPOSTINGCOSTGROUPPOSTINGGROUPID: New Posting Control Group");

      itemblk0.addField("COMPANY")
         .setFunction("Site_API.Get_Company(CONTRACT)")
         .setHidden();

      itemblk0.addField("COSTBUCKETSEQ")
         .setFunction("''")
         .setHidden();

      itemblk0.addField("NCOSTBUCKETSEQ","Number")
         .setFunction("0")
         .setHidden();

      itemblk0.addField("ACTION")
         .setFunction("''")
         .setHidden();

      itemblk0.addField("INFO")
         .setFunction("''")
         .setHidden();

      itemblk0.addField("ATTR")
         .setFunction("''")
         .setHidden();
     
      itemblk0.setView("COST_BUCKET");
      itemblk0.setMasterBlock(headblk);

      itemset0 = itemblk0.getASPRowSet();

      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDefaultLayoutMode(ASPBlockLayout.MULTIROW_LAYOUT);
      
      itembar0 = mgr.newASPCommandBar(itemblk0);
      itembar0.disableCommand(itembar0.VIEWDETAILS);
      itembar0.addCustomCommand("modifyPostingGroup", "");

      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle("MPCCOWMODIFYPOSTINGCOSTGROUPTITLEMPCBG: Modify Posting Cost Group per Cost Bucket");
      itemtbl0.setEditable();

   }


   protected String getDescription()
   {
      return "MPCCOWMODIFYPOSTINGCOSTGROUPMPCBG: Modify Posting Cost Group per Cost Bucket";
   }

   protected String getTitle()
   {
      return "MPCCOWMODIFYPOSTINGCOSTGROUPMPCBG: Modify Posting Cost Group per Cost Bucket";
   }

   public void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      if (headlay.isVisible())
      {
         appendToHTML(headlay.show());
      }

      if (itemlay0.isVisible() && headset.countRows() > 0 )
      {
         appendToHTML(itemlay0.show());

         printNewLine();
         beginDataPresentation();
         printSubmitButton("SUBMIT", "MPCCOWMODIFYPOSTINGCOSTGROUPOKBUTTON:   OK    ", "");
         printSpaces(1);
         printSubmitButton("CANCEL", "MPCCOWMODIFYPOSTINGCOSTGROUPCANCELBUTTON:  Cancel ", "");
         endDataPresentation(false);
      }

      // javascript_message, when not empty, contains javascript for confirm message and call for commandSet.
      if (!mgr.isEmpty(javascript_message))
      {
         appendDirtyJavaScript(javascript_message);
      }
         
      
   }

}
