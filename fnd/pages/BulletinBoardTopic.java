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
*  File        : BulletinBoardTopic.java
*  Modified    :
*    ASP2JAVA Tool  2001-03-02  - Created Using the ASP file BulletinBoardTopic.asp
*    Mangala        2001-03-02  - Convert to WebKit 3.5
*    Jacek P        2001-Mar-29 - Changed .asp to .page
*    Suneth M       2001-Sep-20 - Defined a server function for OKFIND in preDefine()
*                                 to populate the deatil part.
*    Ramila         2002-03-28  - changed submit to querysubmit.
*    Rifki R        2002-Apr-08 - Fixed translation problems caused by trailing spaces.
*    Chandana D     2003-01-22  - Log id 942. Modified okFind() method.
*    Sampath        2003-08-03  - Insert a hidden field to avoid submitting twice   
*    Chandana D     2004-05-19  - Changed mgr.isNetscape6() to mgr.isMozilla().
*    Suneth M       2006-Sep-05 - Bug 60237, Added where conditions to countFind() & countFindITEM().
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class BulletinBoardTopic extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.BulletinBoardTopic");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPContext ctx;
   private ASPBlock blk;
   private ASPRowSet rowset;
   private ASPCommandBar cmdbar;
   private ASPTable tbl;
   private ASPBlockLayout lay;
   private ASPBlock itemblk;
   private ASPRowSet itemset;
   private ASPCommandBar itembar;
   private ASPTable itemtbl;
   private ASPBlockLayout itemlay;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private String fnduser;
   private ASPQuery query;
   private ASPCommand cmd;
   private ASPBuffer data;

   //===============================================================
   // Construction
   //===============================================================
   public BulletinBoardTopic(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      fnduser   = null;
      query   = null;
      cmd   = null;
      data   = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      BulletinBoardTopic page = (BulletinBoardTopic)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.fnduser   = null;
      page.query   = null;
      page.cmd   = null;
      page.data   = null;

      // Cloning immutable attributes
      page.ctx = page.getASPContext();
      page.blk = page.getASPBlock(blk.getName());
      page.rowset = page.blk.getASPRowSet();
      page.cmdbar = page.blk.getASPCommandBar();
      page.tbl = page.getASPTable(tbl.getName());
      page.lay = page.blk.getASPBlockLayout();
      page.itemblk = page.getASPBlock(itemblk.getName());
      page.itemset = page.itemblk.getASPRowSet();
      page.itembar = page.itemblk.getASPCommandBar();
      page.itemtbl = page.getASPTable(itemtbl.getName());
      page.itemlay = page.itemblk.getASPBlockLayout();

      return page;
   }

   public void run()
   {
      ASPManager mgr = getASPManager();


      trans = mgr.newASPTransactionBuffer();
      ctx = mgr.getASPContext();
      fnduser = ctx.readValue("FNDUSER");


      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if( !mgr.isEmpty(mgr.getQueryStringValue("LOAD")) )  //if( Request.QueryString("OWNER").Count > 0
      {
         startup();
         okFind();
      }
      else
        startup();

      adjust();
      ctx.writeValue("FNDUSER",fnduser);
   }


   public void  startup()
   {
      ASPManager mgr = getASPManager();


      trans.addCustomFunction("USERDET", "FND_SESSION_API.Get_Fnd_User","FNDUSER");
      trans = mgr.perform(trans);
      fnduser = trans.getValue("USERDET/DATA/FNDUSER");

   }

//=============================================================================
//  Command Bar Edit Group functions
//=============================================================================

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      query = trans.addQuery(blk);
      //query.addWhereCondition("Owner ='"+fnduser+"'");
      query.addWhereCondition("Owner = ?");
      query.addParameter("Owner", fnduser);
      
      query.setOrderByClause("TOPIC_ID");
      query.includeMeta("ALL");

      mgr.querySubmit(trans,blk);

      if (  rowset.countRows() == 0 )
      {
         mgr.showAlert("FNDPAGESBULLETINBOARDTOPICSNDF: No data found.");
         rowset.clear();
      }
      eval(rowset.syncItemSets());
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      query = trans.addQuery(blk);
      query.addWhereCondition("Owner = ?");
      query.addParameter("Owner", fnduser);
      query.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      lay.setCountValue(toInt(rowset.getValue("N")));
      rowset.clear();
   }


   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("MAIN","Bulletin_Board_Topics_API.New__",blk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("MAIN/DATA");
      rowset.addRow(data);
   }

//=============
// ITEMS
//=============

   public void  okFindITEM()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      query = trans.addQuery(itemblk);
      query.addWhereCondition("TOPIC_ID = ? ");
      query.addParameter("TOPIC_ID", rowset.getValue("TOPIC_ID"));
      query.includeMeta("ALL");
      int headrowno = rowset.getCurrentRowNo();
      mgr.querySubmit(trans,itemblk);
      rowset.goTo(headrowno);
   }


   public void  countFindITEM()
   {
      ASPManager mgr = getASPManager();

      query = trans.addQuery(itemblk);
      query.addWhereCondition("TOPIC_ID = ? ");
      query.addParameter("TOPIC_ID", rowset.getValue("TOPIC_ID"));
      query.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay.setCountValue(toInt(rowset.getValue("N")));
      itemset.clear();
   }


   public void  newRowITEM()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM","Bulletin_Board_Topic_Users_API.New__",itemblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM_TOPIC_ID", rowset.getValue("TOPIC_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM/DATA");
      itemset.addRow(data);
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      blk = mgr.newASPBlock("MAIN");

      blk.addField("OBJID").
          setHidden();

      blk.addField("OBJVERSION").
          setHidden();

      blk.addField("TOPIC_ID").
          setHidden();

      blk.addField("DESCRIPTION").
          setLabel("FNDPAGESBULLETINBOARDTOPICDESC: Description").
          setSize(25);

      blk.addField("OWNER").
          setHidden();

      blk.addField("CREATED_AT").
          setHidden();

      blk.addField("FNDUSER").
          setFunction("''").
          setHidden();


      blk.setView("BULLETIN_BOARD_TOPICS");
      blk.defineCommand("Bulletin_Board_Topics_API","New__,Modify__,Remove__");

      rowset = blk.getASPRowSet();

      cmdbar = mgr.newASPCommandBar(blk);
      cmdbar.disableCommand(cmdbar.DUPLICATEROW);
      cmdbar.addCustomCommand("addMessage","Add Messages");
      cmdbar.addCustomCommand("adminMessages","Administrate Messages");

      cmdbar.disableMinimize();

      blk.setTitle(mgr.translate("FNDPAGESBULLETINBOARDTOPICTOPICS: Topics"));

      tbl = mgr.newASPTable(blk);

      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.MULTIROW_LAYOUT);


   //----ITEM

      itemblk = mgr.newASPBlock("ITEM");

      itemblk.addField("ITEM_OBJID" ).
              setDbName("OBJID").
              setHidden();

      itemblk.addField("ITEM_OBJVERSION" ).
              setDbName("OBJVERSION").
              setHidden();

      itemblk.addField("ITEM_TOPIC_ID","Integer").
              setDbName("TOPIC_ID").
              setHidden();

      itemblk.addField("ITEM_IDENTITY").
              setDbName("IDENTITY").
              setLabel("FNDPAGESBULLETINBOARDTOPICIDENTITY: User").
              setDynamicLOV("FND_USER",450,450).
              setSize(12);


      itemblk.addField("BULLETIN_BOARD_USER_LEVEL").
              setLabel("FNDPAGESBULLETINBOARDTOPICUSERLEVEL: User Level").
              enumerateValues("Bulletin_Board_User_Level_API").
              setSelectBox().
              setSize(12);


      itemblk.setView("BULLETIN_BOARD_TOPIC_USERS");
      itemblk.defineCommand("Bulletin_Board_Topic_Users_API","New__,Modify__,Remove__");
      itemblk.setMasterBlock(blk);

      itemblk.setTitle(mgr.translate("FNDPAGESBULLETINBOARDTOPICTOPICUSER: Topic User Level"));
      itemset = itemblk.getASPRowSet();

      itembar = mgr.newASPCommandBar(itemblk);
      itembar.defineCommand(itembar.OKFIND,"okFindITEM");
      itembar.defineCommand(itembar.NEWROW,"newRowITEM");
      itembar.disableCommand(itembar.DUPLICATEROW);

      itembar.enableCommand(itembar.FIND);


      itemtbl = mgr.newASPTable(itemblk);

      itemlay = itemblk.getASPBlockLayout();
      itemlay.setDialogColumns(3);
      itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);

      ASPBlock dummy = mgr.newASPBlock("DUMMY");
      dummy.addField("Owner");
   }


   public void  addMessage()
   {
      ASPManager mgr = getASPManager();

      if (lay.isMultirowLayout())
         rowset.goTo(rowset.getRowSelected());

      mgr.redirectTo("BulletinBoardAddMessages.page?TOPIC_ID=" + rowset.getValue("TOPIC_ID"));


   }


   public void  adminMessages()
   {
      ASPManager mgr = getASPManager();

      if (lay.isMultirowLayout())
         rowset.goTo(rowset.getRowSelected());

      mgr.redirectTo("BulletinBoardMessages.page?TOPIC_ID=" + rowset.getValue("TOPIC_ID"));

     //mgr.transferDataTo("BulletinBoardMessages.page",rowset.getSelectedRows("TOPIC_ID"));

   }


   public void  adjust()
   {
      if (rowset.countRows() == 0)
      {
         itembar.disableCommand(itembar.NEWROW);
      }
   }

//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "FNDPAGESBULLETINBOARDTOPICTITLE: Bulletin Board Topics";
   }

   protected String getTitle()
   {
      return "FNDPAGESBULLETINBOARDTOPICTITLE: Bulletin Board Topics";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      if(lay.isVisible())
      {
      appendToHTML(lay.show());
      }
      if(itemlay.isVisible())
      {
      appendToHTML(itemlay.show());
      }
      if(mgr.isExplorer() || mgr.isMozilla())
      {
         appendToHTML("<div style=\"position:relative; visibility:hidden\" >\n");
         appendToHTML("<input type=\"text\" size=\"10\">\n");
         appendToHTML("</div>\n");
      }
      appendDirtyJavaScript("function commandSet(cmd,func)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("//alert(cmd);\n");
      appendDirtyJavaScript("//alert(tblPop1.items[tblPop1.items.length-1]+tblPop1.items.length);\n");
      appendDirtyJavaScript("  if( func=='' )\n");
      appendDirtyJavaScript("    ok = true;\n");
      appendDirtyJavaScript("  else\n");
      appendDirtyJavaScript("    if(func.indexOf('(')>-1)\n");
      appendDirtyJavaScript("       eval('ok = '+func+';');\n");
      appendDirtyJavaScript("    else\n");
      appendDirtyJavaScript("       eval('ok = '+func+'();');\n");
      appendDirtyJavaScript("  if( !ok ) return;\n");
      appendDirtyJavaScript("  f.__COMMAND.value = cmd;\n");
      appendDirtyJavaScript("  submit();\n");
      appendDirtyJavaScript("}\n");
   }

}
