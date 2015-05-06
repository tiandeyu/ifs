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
*  File        : DocClassProcessAction.java
*  Prsalk     2003-01-23  Created
*  Nisilk     2003-04-01  Added methods doReset and clone
*  Bakalk     2003-07-29  Many changes done due to call id: 99996
*  Shtolk     2003-07-31  Fixed Call ID 99602, Changed the title of itemblk0 to be 'Document Types'. And changed
*             2003-07-31  the translate constant to 'DOCMAWDOCCLASSPRCACTDOCTYPES'.
*  InoSlk     2003-08-01  Call ID 95439: A few GUI changes in Copy Configuration dialog.
*  InoSlk     2003-08-27  Call ID 101731: Modified doReset() and clone().
*  BAKALK     2006-07-18  Bug ID 58216, Fixed Sql Injection.
*  ILSOLK     20070807    Eliminated SQLInjection.
*  SHTHLK     2008-04-16   Bug Id 70286, Replaced addCustomCommand() with addSecureCustomCommand()
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;  
import java.util.*;


public class DocClassProcessAction extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocClassProcessAction");


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
   private ASPBlock itemblk0;
   private ASPRowSet itemset0;
   private ASPCommandBar itembar0;
   private ASPTable itemtbl0;
   private ASPBlockLayout itemlay0;
   private ASPField f;
   private ASPBlock dummyblk;
   private ASPRowSet dummyset;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPCommand cmd;
   private ASPBuffer data;
   private ASPBuffer rowsetBuffer;
   private ASPQuery q;

   private boolean multirow;
   private boolean overview;
   private boolean revtype;
   private boolean showdialog;
   private boolean item_duplicated;
   private boolean head_duplicated;
   private boolean showConfirmConnectFiles;
   private boolean emptyFileType;

   private String db_val;
   private String row_no;
   private String from_class;
   private String from_process;
   private String to_class;
   private String to_process;
   private String list_value;
   private String message;
   private String searchURL;

   private int curRowNo;
   private int itemRowNo;

   //===============================================================
   // Construction
   //===============================================================
   public DocClassProcessAction(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }
   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans                    = null;
      cmd                      = null;
      data                     = null;
      rowsetBuffer             = null;
      q                        = null;

      multirow                 = false;
      overview                 = false;
      revtype                  = false;
      showdialog               = false;
      item_duplicated          = false;
      head_duplicated          = false;
      showConfirmConnectFiles  = false;
      emptyFileType            = false;

      db_val                   = null;
      row_no                   = null;
      from_class               = null;
      from_process             = null;
      to_class                 = null;
      to_process               = null;
      message                  = null;
      searchURL                = null;
      list_value               = null;

      curRowNo                 = 0;
      itemRowNo                = 0;
      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      DocClassProcessAction page = (DocClassProcessAction)(super.clone(obj));
      //Initialising mutable attributes
      page.trans                    = null;
      page.cmd                      = null;
      page.data                     = null;
      page.rowsetBuffer             = null;
      page.q                        = null;

      page.multirow                 = false;
      page.overview                 = false;
      page.revtype                  = false;
      page.showdialog               = false;
      page.item_duplicated          = false;
      page.head_duplicated          = false;
      page.showConfirmConnectFiles  = false;
      page.emptyFileType            = false;

      page.db_val                   = null;
      page.row_no                   = null;
      page.from_class               = null;
      page.from_process             = null;
      page.to_class                 = null;
      page.to_process               = null;
      page.message                  = null;
      page.searchURL                = null;
      page.list_value          = null;

      page.curRowNo                 = 0;
      page.itemRowNo                = 0;

      //Cloning immutable attributes
      page.ctx = page.getASPContext();
      page.fmt = page.getASPHTMLFormatter();
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
      page.dummyblk = page.getASPBlock(dummyblk.getName());
      page.dummyset = page.dummyblk.getASPRowSet();
      page.f = page.getASPField(f.getName());
      return page;
   }



   public void run()
   {
      ASPManager mgr = getASPManager();


      trans = mgr.newASPTransactionBuffer();
      ctx = mgr.getASPContext();
      fmt = mgr.newASPHTMLFormatter();

      multirow = ctx.readFlag("MULTIROW",true);
      overview = ctx.readFlag("OVERVIEW",false);
      revtype  = ctx.readFlag("REVTYPE",false);
      showdialog = ctx.readFlag("SHOWDIALOG",false);
      db_val = ctx.readValue("DB_VAL","");
      row_no = ctx.readValue("ROW_NO","");
      from_class = mgr.readValue("FROM_CLASS");
      from_process = mgr.readValue("FROM_CLASS");

      to_class = mgr.readValue("TO_CLASS");
      to_process= mgr.readValue("TO_PROCESS");

      curRowNo = (int)ctx.readNumber("CURROWNO",0);

      itemRowNo = (int)ctx.readNumber("ITEMROWNO",0);

      rowsetBuffer = ctx.readBuffer("CUR_ROW_SET");//rowsetBuffer
      item_duplicated = false;
      head_duplicated = false;
      message = "";
      showConfirmConnectFiles = false;
      emptyFileType = false;

      if (mgr.commandBarActivated())
      {
         String commnd = mgr.readValue("__COMMAND");

         if ("HEAD.DuplicateRow".equals(commnd))
            head_duplicated = true;

         if ("ITEM0.DuplicateRow".equals(commnd))
            item_duplicated = true;
         eval(mgr.commandBarFunction());
      }
      else if (mgr.commandLinkActivated())
         eval (mgr.commandLinkFunction()); // SQLInjection_Safe ILSOLK 20070807
      else if (mgr.dataTransfered())
         search();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("SEARCH")))
         search();
      else if (!mgr.isEmpty(mgr.getQueryStringValue("PACKAGE_NO")))
         okFind();
      else if (mgr.buttonPressed("OKBUT"))
         doCopyConfiguration();
      else if (mgr.buttonPressed("CANBUT"))
         backToForm();
      else if ("TRUE".equals(mgr.readValue("REFRESH_ROW")))
         refreshCurrentRow();
      else
         okFind();  

      adjust();

      ctx.writeFlag("MULTIROW",multirow);
      ctx.writeFlag("OVERVIEW",overview);
      ctx.writeFlag("REVTYPE",revtype);
      ctx.writeValue("DB_VAL",db_val);
      ctx.writeValue("ROW_NO",row_no);
      ctx.writeFlag("SHOWDIALOG",showdialog);
      ctx.writeNumber("CURROWNO",curRowNo);

      ctx.writeNumber("ITEMROWNO",itemRowNo);
      if (rowsetBuffer != null)
      {
         ctx.writeBuffer("CUR_ROW_SET",rowsetBuffer);
      }

   }

   //-----------------------------------------------------------------------------
   //-------------------------   UTILITY FUNCTIONS  ------------------------------
   //-----------------------------------------------------------------------------

   //-----------------------------------------------------------------------------
   //-----------------------------  VALIDATE FUNCTION  ---------------------------
   //-----------------------------------------------------------------------------


   //=============================================================================
   //  Command Bar functions for Head Part
   //=============================================================================

   public void  search()
   {
      okFind();
      if (headset.countRows()>0)
      {
         okFindITEM0();
      }
   }

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      searchURL = mgr.createSearchURL(headblk);

      trans.clear();
      q = trans.addQuery(headblk);
      if (mgr.dataTransfered())
      {
         q.addOrCondition(mgr.getTransferedData());
      }
      q.setOrderByClause("DOC_CLASS");
      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);

      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCCLASSPRCACTNODATA: No data found."));
         headset.clear();
      }
      eval(headset.syncItemSets());
   }


   public void  populateAfterCopyingConfig()
   {
      ASPManager mgr = getASPManager();

      searchURL = mgr.createSearchURL(headblk);

      trans.clear();
      q = trans.addQuery(headblk);
      q.addOrCondition(reverseBuffer(rowsetBuffer));
      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);

      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCCLASSPRCACTNODATA: No data found."));
         headset.clear();
      }
      headset.last();
      eval(headset.syncItemSets());
   }

   //made this private since this method is specific here. :baka
   private ASPBuffer reverseBuffer(ASPBuffer buff)
   {
      ASPBuffer forBuff;//buffer in the for loop
      ASPBuffer tempBuff = buff.copy();
      String temp_doc_class;
      String temp_process;
      for (int k=0;k<buff.countItems();k++)
      {
         forBuff = buff.getBufferAt(k);
         temp_doc_class = forBuff.getValueAt(0);
         temp_process   = forBuff.getValueAt(1);

         forBuff = tempBuff.getBufferAt(tempBuff.countItems()-k-1);
         forBuff.setValueAt(0,temp_doc_class);
         forBuff.setValueAt(1,temp_process);

      }
      return tempBuff;
   }


   public void  countFind()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(headblk);
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      headlay.setCountValue(toInt(headset.getRow().getValue("N")));
      headset.clear();
   }


   public void  newRow()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM1","DOC_CLASS_PROC_ACTION_HEAD_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }

   //=============================================================================
   //  Command Bar functions for Detail Part
   //=============================================================================

   public void  okFindITEM0()
   {
      ASPManager mgr = getASPManager();

      if (headset.countRows()>0)
      {
         trans.clear();
         q = trans.addQuery(itemblk0);
         //bug 58216 starts
         q.addWhereCondition("DOC_CLASS = ? AND EDM_MACRO_PROCESS = ?");
         q.addParameter("DOC_CLASS",headset.getValue("DOC_CLASS"));
         q.addParameter("EDM_MACRO_PROCESS",headset.getValue("EDM_MACRO_PROCESS"));
         //bug 58216 end
         q.setOrderByClause("LINE_NO");
         q.includeMeta("ALL");
         int headrowno = headset.getCurrentRowNo();
         mgr.submit(trans);
         headset.goTo(headrowno);
      }
      else
         itemset0.clear();
   }


   public void  countFindITEM0()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk0);
      //bug 58216 starts
      q.addWhereCondition("PACKAGE_NO = ?");
      q.addParameter("PACKAGE_NO",headset.getRow().getValue("PACKAGE_NO") );
      //bug 58216 end
      q.setSelectList("to_char(count(*)) N");
      mgr.submit(trans);
      itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
      itemset0.clear();
   }


   public void  newRowITEM0()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addEmptyCommand("ITEM0","DOC_CLASS_PROC_ACTION_LINE_API.New__",itemblk0);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM0/DATA");
      data.setFieldItem("ITEM0_DOC_CLASS",headset.getRow().getValue("DOC_CLASS"));
      data.setFieldItem("ITEM0_PROCESS",headset.getRow().getValue("EDM_MACRO_PROCESS"));
      itemset0.addRow(data);

   }


   public void  refreshCurrentRow()
   {
      if (headlay.isMultirowLayout())
      {
         headset.goTo(curRowNo);
      }
      else
         headset.selectRow();

      headset.refreshRow();

      if (itemlay0.isMultirowLayout())
      {
         itemset0.goTo(itemRowNo);
      }
      else
         itemset0.selectRow();

      itemset0.refreshRow();
   }


   //-----------------------------------------------------------------------------
   //------------------------  CMDBAR CUSTOM FUNCTIONS  --------------------------
   //-----------------------------------------------------------------------------


   public void  copyConfiguration()
   {
      if (headlay.isMultirowLayout())
      {
         headset.storeSelections();
         headset.setFilterOn();
      }
      else
         headset.selectRow();

      from_class = headset.getRow().getValue("DOC_CLASS");
      from_process = headset.getRow().getValue("EDM_MACRO_PROCESS");

      curRowNo = headset.getCurrentRowNo();

      showdialog=true;
      headset.setFilterOff();
      rowsetBuffer = headset.getRows("DOC_CLASS,EDM_MACRO_PROCESS");
   }





   //-----------------------------------------------------------------------------
   //------------------------  BUTTON FUNCTIONS  ---------------------------------
   //-----------------------------------------------------------------------------


   public void  doCopyConfiguration()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomCommand("COPYCONFIG","DOC_CLASS_PROC_ACTION_HEAD_API.Copy_Configuration");
      cmd.addParameter("DOC_CLASS",mgr.readValue("FROM_CLASS"));
      cmd.addParameter("EDM_MACRO_PROCESS",mgr.readValue("FROM_PROCESS"));
      cmd.addParameter("TO_CLASS",mgr.readValue("TO_CLASS"));
      cmd.addParameter("TO_PROCESS",mgr.readValue("TO_PROCESS_SELECT"));

      trans = mgr.perform(trans);
      trans.clear();
      showdialog =false;

      //add this new row to the buffer in ctx
      int k = rowsetBuffer.countItems();
      ASPBuffer tempBuff = rowsetBuffer.addBufferAt("NEW_BUFF"+k,k);
      tempBuff.addItemAt("DOC_CLASS",mgr.readValue("TO_CLASS"),0);
      tempBuff.addItemAt("EDM_MACRO_PROCESS",mgr.readValue("TO_PROCESS_SELECT"),1);

      populateAfterCopyingConfig();


   }

   public void  backToForm()
   {
      //headset.setFilterOn();
      headset.goTo(curRowNo);
      showdialog =false;
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      disableConfiguration();

      headblk = mgr.newASPBlock("HEAD");

      headblk.disableDocMan();

      headblk.addField("OBJID").
      setHidden();

      headblk.addField("OBJVERSION").
      setHidden();

      headblk.addField("DOC_CLASS").
      setSize(20).
      setMaxLength(12).
      setMandatory().
      setReadOnly().
      setInsertable().
      setUpperCase().
      setDynamicLOV("DOC_CLASS").
      setLabel("DOCMAWDOCCLASSPRCACTDOCCLASS: Doc Class");


      headblk.addField("EDM_MACRO_PROCESS").
      setSize(30).
      setMaxLength(30).
      setReadOnly().
      setInsertable().
      setMandatory().
      setSelectBox().
      enumerateValues("EDM_MACRO_PROCESS_API").
      //setDynamicLOV("EDM_MACRO_PROCESS").
      setLabel("DOCMAWDOCCLASSPRCACTPROCESS: Process");

      headblk.addField("EDM_MACRO_PROCESS_DB").
      setHidden().
      setFunction("EDM_MACRO_PROCESS_API.Encode(:EDM_MACRO_PROCESS)");

      headblk.addField("MACRO_OPTION").
      setSize(20).
      setMaxLength(20).
      setMandatory().
      setSelectBox().
      enumerateValues("MACRO_OPTION_API").
      setLabel("DOCMAWDOCCLASSPRCACTMO: Macro option");

      headblk.addField("NAME_FILES_USING").
      setSize(20).
      setMaxLength(20).
      setMandatory().
      setSelectBox().
      enumerateValues("NAME_FILES_USING_API").
      setLabel("DOCMAWDOCCLASSPROCACTIONNAMEFILESUSING: Name Files Using");

      headblk.setView("DOC_CLASS_PROC_ACTION_HEAD");
      headblk.defineCommand("DOC_CLASS_PROC_ACTION_HEAD_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.addSecureCustomCommand("copyConfiguration",mgr.translate("DOCMAWDOCCLASSPROCACTIONCOPYDOCCONFIG: Copy Configuration..."),"DOC_CLASS_PROC_ACTION_HEAD_API.Copy_Configuration"); //Bug Id 70286

      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("DOCMAWDOCCLASSPRCACTDOCCLSPRCACT: Document Class Process Action"));

      headlay = headblk.getASPBlockLayout();
      headlay.setFieldOrder("DOC_CLASS");
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
      headlay.setDialogColumns(2);

      //
      // Lines
      //

      itemblk0 = mgr.newASPBlock("ITEM0");

      itemblk0.addField("ITEM0_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk0.addField("ITEM0_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      itemblk0.addField("ITEM0_LINE_NO").
      setDbName("LINE_NO").
      setHidden();

      itemblk0.addField("ITEM0_DOC_CLASS").
      setDbName("DOC_CLASS").
      setHidden();

      itemblk0.addField("ITEM0_PROCESS").
      setDbName("EDM_MACRO_PROCESS").
      setHidden();


      headblk.addField("ITEM0_EDM_MACRO_PROCESS_DB").
      setHidden().
      setFunction("EDM_MACRO_PROCESS_API.Encode(:ITEM0_PROCESS)");


      itemblk0.addField("ITEM0_DOC_TYPE").
      setDbName("DOCUMENT_TYPE").
      setSize(20).
      setMaxLength(12).
      setDynamicLOV("DOCUMENT_TYPE").
      setReadOnly().
      setInsertable().
      setLabel("DOCMAWDOCCLASSPRCACTDOCTYPE: Doc Type");

      //bug 58216 starts
      itemblk0.addField("PACKAGE_NO").setFunction("''").setHidden();
      //bug 58216 end


      itemblk0.setView("DOC_CLASS_PROC_ACTION_LINE");
      itemblk0.defineCommand("DOC_CLASS_PROC_ACTION_LINE_API","New__,Modify__,Remove__");
      itemblk0.setMasterBlock(headblk);

      itemset0 = itemblk0.getASPRowSet();

      itembar0 = mgr.newASPCommandBar(itemblk0);
      itembar0.enableCommand(itembar0.FIND);

      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle(mgr.translate("DOCMAWDOCCLASSPRCACTDOCTYPES: Document Types"));

      itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");

      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDialogColumns(2);
      itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
      itemblk0.setTitle(mgr.translate("DOCMAWDOCCLASSPRCACTDOCTYPES: Document Types"));

      dummyblk = mgr.newASPBlock("DUMMY");

      dummyblk.disableDocMan();

      dummyblk.addField("TO_CLASS").
      setSize(20).
      setMaxLength(12).
      setMandatory().
      setInsertable().
      //setHidden().
      setUpperCase().
      setDynamicLOV("DOC_CLASS").
      setFunction("''");

      dummyblk.addField("TO_PROCESS").
      setHidden().
      setFunction("''");
      dummyblk.setView("DOC_CLASS_PROC_ACTION_HEAD");
      dummyblk.defineCommand("DOC_CLASS_PROC_ACTION_HEAD_API","New__");
      dummyset = dummyblk.getASPRowSet();
   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();

      if (headset.countRows() == 0)
      {
         headbar.disableCommand(headbar.EDITROW);
         headbar.disableCommand(headbar.DUPLICATEROW);
         headbar.disableCommand(headbar.BACK);
         headbar.disableCommand(headbar.DELETE);
         headbar.disableCommand(headbar.FORWARD);
         headbar.removeCustomCommand("copyConfiguration");
      }
      if (showdialog)
      {
         //mgr.getASPField("TO_CLASS").unsetHidden();
         trans.clear();

         cmd = trans.addEmptyCommand("DUMMY","DOC_CLASS_PROC_ACTION_HEAD_API.New__",dummyblk);
         cmd.setOption("ACTION","PREPARE");

         cmd = trans.addCustomCommand("ENUMERATE","EDM_MACRO_PROCESS_API.ENUMERATE");
         cmd.addParameter("TO_PROCESS");

         trans = mgr.perform(trans);

         ASPBuffer data = trans.getBuffer("DUMMY/DATA");
         list_value     = trans.getValue("ENUMERATE/DATA/TO_PROCESS");
         debug("%%%%%%%%%%%%% list_value="+list_value);
         debug("from class ******************" + from_class);
         dummyset.addRow(data);
         eval(dummyblk.generateAssignments());
      }
   }

   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      return "DOCMAWDOCCLASSPRCACTTITLE: Document Class Process Action";
   }

   protected String getTitle()
   {
      return "DOCMAWDOCCLASSPRCACTTITLE: Document Class Process Action";
   }

   protected AutoString getContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      AutoString out = getOutputStream();
      out.clear();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("DOCMAWDOCCLASSPRCACTTITLE: Document Class Process Action"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");

      out.append("  <input type =\"hidden\" name=\"CONFIRMCONNECTION\" value=\"\">\n");
      out.append("  <input type=\"hidden\" name=\"REFRESH_ROW\" value=\"FALSE\">\n");

      out.append(mgr.startPresentation("DOCMAWDOCCLASSPRCACTTITLE: Document Class Process Action"));

      if (showdialog)
      {

         out.append(dummyblk.generateHiddenFields());
         out.append("<table border=\"0\" width=\"600\" height=\"1\">\n");
         out.append("<tr>\n");
         out.append("<td width=\"500\" height=\"35\"><font face=\"Verdana\" size=\"2\">");
         out.append(fmt.drawReadLabel(mgr.translate("DOCMAWDOCCLASSPRCACTTABLETITLE: Copy Configuration")));
         out.append("</font></td>\n");
         out.append("</tr>\n");
         out.append("<tr>\n");
         out.append("<td width=\"237\" height=\"10\"></td>\n");
         out.append("</tr>\n");
         out.append("<tr>\n");
         out.append("<td nowrap width=\"230\">");
         out.append(fmt.drawReadLabel(mgr.translate("DOCMAWDOCCLASSPRCACTFROMCLASS: From Class:")));
         out.append("</td>\n");
         out.append("<td nowrap width=\"200\">");
         out.append(fmt.drawReadLabel(mgr.translate("DOCMAWDOCCLASSPRCACTFROMPROC: From Process:")));
         out.append("</td>\n");
         out.append("</tr>\n");
         out.append("<tr>\n");
         out.append("<td nowrap width=\"200\">");
         //out.append(fmt.drawTextField("FROM_CLASS",from_class,"readOnly"));
         out.append(fmt.drawReadOnlyTextField("FROM_CLASS",from_class,""));
         out.append("</td>\n");
         out.append("<td nowrap width=\"200\">");
         out.append(fmt.drawReadOnlyTextField("FROM_PROCESS",from_process,""));
         out.append("</td>\n");
         out.append("<td nowrap>");
         out.append(fmt.drawSubmit("OKBUT",mgr.translate("DOCMAWDOCCLASSPRCACTAPPOK:  Ok    "),""));
         out.append("</td>\n");
         out.append("</tr>\n");
         out.append("<tr>\n");
         out.append("<td nowrap width=\"200\">");
         out.append(fmt.drawReadLabel(mgr.translate("DOCMAWDOCCLASSPRCACTTOCLASS: To Class:")));
         out.append("</td>\n");
         out.append("<td nowrap width=\"200\">");
         out.append(fmt.drawReadLabel(mgr.translate("DOCMAWDOCCLASSPRCACTTOPROC: To Process:")));
         out.append("</td>\n");
         out.append("</tr>\n");
         out.append("<tr>\n");
         out.append("<td nowrap width=\"200\">");

         //initialise variables
         if (mgr.isEmpty(to_class))
            to_class=from_class;
         if (mgr.isEmpty(to_process))
            to_process="";

         out.append(fmt.drawTextField("TO_CLASS",to_class,mgr.getASPField("TO_CLASS").getTag()));
         out.append("</td>\n");
         out.append("<td nowrap width=\"200\">");
         out.append(fmt.drawSelectStart("TO_PROCESS_SELECT",""));
         StringTokenizer st = new StringTokenizer(this.list_value, "" + DocmawUtil.FIELD_SEPARATOR);

         String currentToken;
         while (st.hasMoreTokens())
         {
            currentToken=st.nextToken();
            out.append(fmt.drawSelectOption(currentToken,currentToken,false));
         }

         out.append(fmt.drawSelectEnd());
         out.append("</td>\n");
         out.append("<td nowrap width=\"200\">");
         out.append(fmt.drawSubmit("CANBUT",mgr.translate("DOCMAWDOCCLASSPRCACTAPPCANCEL: Cancel"),""));
         out.append("</td>\n");
         out.append("</tr>\n");
         out.append("</table>\n");

         dummyset.clearRow();//remove unneccery row
      }
      else
      {

         if (headlay.isVisible())
         {
            out.append(headlay.show());
         }
         if (itemlay0.isVisible() && headset.countRows()>0)
         {
            out.append(itemlay0.show());
         }
      }
      //-----------------------------------------------------------------------------
      //----------------------------  CLIENT FUNCTIONS  -----------------------------
      //-----------------------------------------------------------------------------

      if ((showConfirmConnectFiles) && (!emptyFileType))
      {
         appendDirtyJavaScript(" if (confirm('");
         appendDirtyJavaScript(message);
         appendDirtyJavaScript("')) {\n");
         appendDirtyJavaScript("    f.CONFIRMCONNECTION.value='OK';\n");
         appendDirtyJavaScript(" }\n");
         appendDirtyJavaScript(" else {\n");
         appendDirtyJavaScript("    f.CONFIRMCONNECTION.value='CANCEL';\n");
         appendDirtyJavaScript(" }\n");
         appendDirtyJavaScript(" submit();\n");
      }
      else if ((showConfirmConnectFiles) && (emptyFileType))
      {
         appendDirtyJavaScript(" f.CONFIRMCONNECTION.value='CANCEL';\n");
         appendDirtyJavaScript(" submit();\n");
      }
      appendDirtyJavaScript("function refreshParent()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" document.form.REFRESH_ROW.value=\"TRUE\"\n");
      appendDirtyJavaScript(" submit() \n");
      appendDirtyJavaScript("}\n");

      out.append(mgr.endPresentation());
      out.append("</form>\n");

      out.append("</body>\n");
      out.append("</html>");
      return out;
   }
}
