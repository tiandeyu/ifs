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
*  File        : BulletinBoardMessages.java
*  Modified    :
*    ASP2JAVA Tool  2001-03-02  - Created Using the ASP file BulletinBoardMessages.asp
*    Mangala        2001-03-02  - Convert to WebKit 3.5
*    Ramila         2002-03-28  - changed submit to querysubmit.
*    Rifki R        2002-04-08  - Fixed translation problems caused by trailing spaces.
*    Chandana D     2003-01-22  - Log id 942. Changed okFindItem() method.
* ----------------------------------------------------------------------------
*/


package ifs.fnd.pages;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class BulletinBoardMessages extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.fnd.pages.BulletinBoardMessages");


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
   private int headrowno;

   //===============================================================
   // Construction
   //===============================================================
   public BulletinBoardMessages(ASPManager mgr, String page_path)
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
      headrowno   = 0;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      BulletinBoardMessages page = (BulletinBoardMessages)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.fnduser   = null;
      page.query   = null;
      page.cmd   = null;
      page.data   = null;
      page.headrowno   = 0;

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
      else if(mgr.dataTransfered()||!mgr.isEmpty(mgr.getQueryStringValue("TOPIC_ID")) )
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
      query.setOrderByClause("TOPIC_ID");

      if( mgr.dataTransfered() )
            query.addOrCondition( mgr.getTransferedData() );

      query.includeMeta("ALL");

      mgr.querySubmit(trans,blk);

      if (  rowset.countRows() == 0 )
      {
         mgr.showAlert("FNDPAGESBULLETINBOARDMESSAGESNODATA: No data found.");
         rowset.clear();
      }
      else
         okFindITEM();
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      query = trans.addQuery(blk);
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
      rowset.addRow(trans.getBuffer("MAIN/DATA"));
   }

//=============
// ITEMS
//=============

   public void  okFindITEM()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      query = trans.addQuery(itemblk);
      //query.addWhereCondition("TOPIC_ID ='"+rowset.getValue("TOPIC_ID")+"' AND WRITER='"+fnduser+"'");
      query.addWhereCondition("TOPIC_ID = ? AND WRITER = ?");
      query.addParameter("TOPIC_ID",rowset.getValue("TOPIC_ID")); 
      query.addParameter("WRITER",fnduser);
      
      query.includeMeta("ALL");
      headrowno = rowset.getCurrentRowNo();
      mgr.querySubmit(trans,itemblk);
      rowset.goTo(headrowno);
   }


   public void  countFindITEM()
   {
      ASPManager mgr = getASPManager();

      query = trans.addQuery(itemblk);
      query.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay.setCountValue(toInt(rowset.getValue("N")));
      itemset.clear();
   }


   public void  newRowITEM()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM","Bulletin_Board_Messages_API.New__",itemblk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("ITEM_TOPIC_ID", rowset.getValue("TOPIC_ID"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM/DATA");
      data.setValue("ITEM_TOPIC_ID",rowset.getValue("TOPIC_ID"));
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
          setLabel("FNDPAGESBULLETINBOARDMESSAGESDESC: Description").
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
      cmdbar.disableCommand(cmdbar.DELETE);
      cmdbar.disableCommand(cmdbar.EDITROW);
      cmdbar.disableCommand(cmdbar.NEWROW);
      cmdbar.disableCommand(cmdbar.FIND);
      cmdbar.disableCommand(cmdbar.BACK);
      cmdbar.disableCommand(cmdbar.BACKWARD);

      cmdbar.disableMinimize();


      //blk.unsetHideable();
      blk.setTitle(mgr.translate("FNDPAGESBULLETINBOARDMESSAGESTOPICS: Topics"));

      tbl = mgr.newASPTable(blk);

      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.SINGLE_LAYOUT);

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

      itemblk.addField("ITEM_MESSAGE_ID").
              setDbName("MESSAGE_ID").
              setHidden().
              setLabel("FNDPAGESBULLETINBOARDMESSAGESMSGID: Message Id").
              setSize(12);


      itemblk.addField("ITEM_CREATED_AT","Datetime").
              setDbName("CREATED_AT").
              setLabel("FNDPAGESBULLETINBOARDMESSAGESCREATEDAT: Date Created").
              setReadOnly().
              setSize(30);

      itemblk.addField("ITEM_MESSAGE").
              setDbName("MESSAGE").
              setLabel("FNDPAGESBULLETINBOARDMESSAGESMSG: Message").
              setHeight(5).
              setSize(100);


      itemblk.setView("BULLETIN_BOARD_MESSAGES");
      itemblk.defineCommand("Bulletin_Board_Messages_API","New__,Modify__,Remove__");
      itemblk.setMasterBlock(blk);

      //itemblk.unsetHideable();
      itemblk.setTitle(mgr.translate("FNDPAGESBULLETINBOARDMESSAGESTOPICMSG: Topic Messages"));
      itemset = itemblk.getASPRowSet();

      itembar = mgr.newASPCommandBar(itemblk);
      itembar.defineCommand(itembar.NEWROW,    "newRowITEM");
      itembar.disableCommand(itembar.DUPLICATEROW);

      itembar.defineCommand(itembar.OKFIND,"okFindITEM");
      itembar.enableCommand(itembar.FIND);


      itemtbl = mgr.newASPTable(itemblk);

      itemlay = itemblk.getASPBlockLayout();
      itemlay.setDialogColumns(1);
      itemlay.setDefaultLayoutMode(itemlay.MULTIROW_LAYOUT);

      ASPBlock dummy = mgr.newASPBlock("DUMMY");
      dummy.addField("WRITER");
   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();

      if (itemlay.isNewLayout())
      {
         mgr.getASPField("ITEM_MESSAGE_ID").setHidden();
         mgr.getASPField("ITEM_CREATED_AT").setHidden();
      }
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "FNDPAGESBULLETINBOARDMESSAGESTITLE: Bulletin Board Messages";
   }

   protected String getTitle()
   {
      return "FNDPAGESBULLETINBOARDMESSAGESTITLE: Bulletin Board Messages";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      appendToHTML(lay.show());
       if(itemlay.isVisible())
      {
      appendToHTML(itemlay.show());
      }
   }

}
