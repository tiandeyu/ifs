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
*  File        : DocumentGroup.java 
*  Modified    :
*    ASP2JAVA Tool  2001-03-15  - Created Using the ASP file DocumentGroup.asp
*              : Bug 31903 Increased the field length in person_id
*    BAKALK   2002-12-30 merged SP3. Replace base file in Salsa with the file in SP3. 
*    BAKALK   2004-02-17 Removed applet dependency for validation.
*    BAKALK   2004-02-17 Changed the dialog layout. Made it in accordance with IFS GUI standard.
*    BAKALK   2004-03-08 Added onchange tag to "ITEM1_GROUP_ID".
*    BAKALK   2006-07-25 Bug ID 58216, Fixed Sql Injection.
*    VIRALK   2008-03-03 Bug Id 69651 Replaced view PERSON_INFO with PERSON_INFO_LOV.
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DocumentGroup extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocumentGroup");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
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

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private boolean showDialog;
   private String val;
   private ASPCommand cmd;
   private String personName;
   private String userID;
   private String grpDes;
   private ASPQuery q;
   private ASPBuffer data;
   private int headrowno;
   private String grp_id;
   private String item1_group_id;
   private String item1_group_description;

   //================================================================================
   // ASP2JAVA WARNING: Types of the following variables are not identified by
   // the ASP2JAVA converter. Define them and remove these comments.
   //================================================================================
   private String txt;  //ASP2JAVA WARNING: The type of this variable could not be found and was set to 'String'.
   //Original assignment: txt =  (mgr.isEmpty(personName) ? "" : personName) + "^" + (mgr.isEmpty(userID) ? "" : userID) + "^" 


   //===============================================================
   // Construction 
   //===============================================================
   public DocumentGroup(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      showDialog   = false;
      val   = null;
      cmd   = null;
      personName   = null;
      userID   = null;
      grpDes   = null;
      q   = null;
      data   = null;
      headrowno   = 0;
      grp_id   = null;
      item1_group_id   = null;
      item1_group_description   = null;


      // ASP2JAVA WARNING: 
      //Could not identify the following variables.
      //If any of these variables are mutable, reset them here.
      txt   = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      DocumentGroup page = (DocumentGroup)(super.clone(obj));

      //Initializing mutable attributes
      page.trans   = null;
      page.showDialog   = false;
      page.val   = null;
      page.cmd   = null;
      page.personName   = null;
      page.userID   = null;
      page.grpDes   = null;
      page.q   = null;
      page.data   = null;
      page.headrowno   = 0;
      page.grp_id   = null;
      page.item1_group_id   = null;
      page.item1_group_description   = null;
      
      //Cloning immutable attributes
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

      //ASP2JAVA WARNING: 

      //Could not identify the following variables.
      //Do the necessary cloning of immutable variables and
      //resetting of mutable variables here.
      page.txt   = null;

      return page;
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

      fmt   = mgr.newASPHTMLFormatter();
      trans = mgr.newASPTransactionBuffer();
      ctx   = mgr.getASPContext();
      showDialog = ctx.readFlag ("SHOWDLG",false);
      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if(!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if((!mgr.isEmpty(mgr.getQueryStringValue("GROUP_ID"))))
         transfer();
      else if (mgr.buttonPressed("OK"))
         copyGroupMembers();
      else if (mgr.buttonPressed("CANCEL"))
         showDialog = false;
      ctx.writeFlag ("SHOWDLG",showDialog);
   }

//=============================================================================
//   VALIDATE FUNCTION
//=============================================================================

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      val = mgr.readValue("VALIDATE");

      if  ( "PERSON_ID".equals(val) ) 
      {
         cmd = trans.addCustomFunction("PERNAME","PERSON_INFO_API.Get_Name","NAME");
         cmd.addParameter("PERSON_ID");
         trans = mgr.validate(trans);
         personName = trans.getValue("PERNAME/DATA/NAME");
         trans.clear();
         cmd = trans.addCustomFunction("UID","PERSON_INFO_API.Get_User_Id","USER_ID");
         cmd.addParameter("PERSON_ID");
         trans = mgr.validate(trans);
         userID = trans.getValue("UID/DATA/USER_ID");
         trans.clear();
         txt = (mgr.isEmpty(personName) ? "" : personName) + "^" + (mgr.isEmpty(userID) ? "" : userID) + "^" ;
         mgr.responseWrite(txt);
      }

      else if  ( "ITEM1_GROUP_ID".equals(val) ) 
      {
         cmd = trans.addCustomFunction("GRPDES","DOCUMENT_GROUP_API.Get_Group_Description","GROUP_DESCRIPTION");
         cmd.addParameter("ITEM1_GROUP_ID");
         trans = mgr.validate(trans);
         grpDes = trans.getValue("GRPDES/DATA/GROUP_DESCRIPTION");
         txt = (mgr.isEmpty(grpDes) ? "" : grpDes+"^");
         mgr.responseWrite(txt);
      }
      mgr.endResponse();
   }

//=============================================================================
//   CMDBAR FIND FUNCTIONS
//=============================================================================

   public void  transfer()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addEmptyQuery(headblk);
      //bug 58216 starts
      q.addWhereCondition("GROUP_ID = ?");
      q.addParameter("GROUP_ID",mgr.readValue("GROUP_ID"));
      //bug 58216 end
      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);
      if ( headset.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTGROUPNODATA: No data found."));
         headset.clear();
      }
      eval(headset.syncItemSets());
      
      if (headset.countRows() > 0)
         okFindITEM0();
   }

//----------------------------------------- HEAD -----------------------------------------

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(headblk);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);
      if ( headset.countRows() == 0 ) 
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTGROUPNODATA: No data found."));
         headset.clear();
      }
      eval(headset.syncItemSets());
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
      cmd = trans.addEmptyCommand("HEAD","DOCUMENT_GROUP_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }

//----------------------------------------- ITEM0 ----------------------------------------

   public void  okFindITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk0);
      //bug 58216 starts
      q.addWhereCondition("GROUP_ID = ?");
      q.addParameter("GROUP_ID",headset.getValue("GROUP_ID"));
      //bug 58216 end
      q.includeMeta("ALL");
      headrowno = headset.getCurrentRowNo();
      mgr.submit(trans);
      headset.goTo(headrowno);
   }


   public void  countFindITEM0()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk0);
      q.setSelectList("to_char(count(*)) N");
      //bug 58216 starts
      q.addWhereCondition("GROUP_ID = ?");
      q.addParameter("GROUP_ID",headset.getValue("GROUP_ID"));
      //bug 58216 end
      mgr.submit(trans);
      itemlay0.setCountValue(toInt(itemset0.getRow().getValue("N")));
      itemset0.clear();
   }


   public void  newRowITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM0","DOCUMENT_GROUP_MEMBERS_API.New__",itemblk0);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM0/DATA");
      grp_id = headset.getRow().getValue("GROUP_ID");
      data.setFieldItem("ITEM0_GROUP_ID",grp_id);
      itemset0.addRow(data);
   }

//=============================================================================
//   CUSTOM FUNCTIONS
//=============================================================================

   public void  getCopyDialog()
   {
      ASPManager mgr = getASPManager();
      //itemset1.addRow
      eval(itemblk1.generateAssignments());
      
      showDialog = true;
      if (mgr.isEmpty(item1_group_id))
         item1_group_id = "";
      if (mgr.isEmpty(item1_group_description))
         item1_group_description = "";
   }


   public void  copyGroupMembers()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addCustomCommand("COPYGRP","DOCUMENT_GROUP_MEMBERS_API.Copy_Members_Of_Group");
      cmd.addParameter("ITEM1_GROUP_ID", mgr.readValue("ITEM1_GROUP_ID"));
      cmd.addParameter("GROUP_ID", headset.getRow().getValue("GROUP_ID"));
      trans = mgr.perform(trans);
      trans.clear();
      okFindITEM0();
      showDialog = false;
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

   //----------------------------------- Master Block -----------------------------------
      headblk = mgr.newASPBlock("HEAD");
      headblk.addField("OBJID").
              setHidden();
      headblk.addField("OBJVERSION").
              setMandatory().
              setHidden();
      headblk.addField("GROUP_ID").
              setSize(20).
              setMaxLength(20).
              setReadOnly().
              setMandatory().
              setLabel("DOCMAWDOCUMENTGROUPGROUPID: Group ID:");
      headblk.addField("GROUP_DESCRIPTION").
              setSize(30).
              setMaxLength(2000).
              setMandatory().
              setLabel("DOCMAWDOCUMENTGROUPGRPDES: Group Description:");
      headblk.setView("DOCUMENT_GROUP");
      headblk.defineCommand("DOCUMENT_GROUP_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(headbar.DELETE);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.BACK);
      headbar.disableCommand(headbar.DUPLICATEROW);
      headbar.addCustomCommand("getCopyDialog",mgr.translate("DOCMAWDOCUMENTGROUPCGM: Copy Group Members..."));
      headtbl = mgr.newASPTable(headblk);
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
   //----------------------------------- Detail Block -----------------------------------
      itemblk0 = mgr.newASPBlock("ITEM0");
      itemblk0.addField("ITEM0_OBJID").
               setDbName("OBJID").
               setHidden();
      itemblk0.addField("ITEM0_OBJVERSION").
               setDbName("OBJVERSION").
               setMandatory().
               setHidden();
      itemblk0.addField("ITEM0_GROUP_ID").
               setDbName("GROUP_ID").
               setMandatory().
               setHidden();
      itemblk0.addField("PERSON_ID").
               setSize(20).
               setMaxLength(30).
               setMandatory().
               setReadOnly().
               setInsertable().
               setUpperCase().
               setDynamicLOV("PERSON_INFO_LOV"). //,"PERSON_ID, NAME, USER_ID").
               setCustomValidation("PERSON_ID", "NAME, USER_ID").
               setLabel("DOCMAWDOCUMENTGROUPPERSID: Person ID");
      itemblk0.addField("NAME").
               setSize(30).
               setReadOnly().
               setFunction("PERSON_INFO_API.Get_Name(:PERSON_ID)").
               setLabel("DOCMAWDOCUMENTGROUPPERNAME: Name");
      itemblk0.addField("USER_ID").
               setSize(20).
               setReadOnly().
               setUpperCase().
               setFunction("PERSON_INFO_API.Get_User_Id(:PERSON_ID)").
               setLabel("DOCMAWDOCUMENTGROUPUSERID: User ID");
      itemblk0.addField("NOTE").
               setSize(30).
               setMaxLength(2000).
               setLabel("DOCMAWDOCUMENTGROUPNOTE: Note");
      itemblk0.setView("DOCUMENT_GROUP_MEMBERS");
      itemblk0.defineCommand("DOCUMENT_GROUP_MEMBERS_API","New__,Modify__,Remove__");
      itemblk0.setMasterBlock(headblk);
      itemset0 = itemblk0.getASPRowSet();
      itembar0 = mgr.newASPCommandBar(itemblk0);
      itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");
      itemtbl0 = mgr.newASPTable(itemblk0);
      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);
   //----------------------------------- Copy Dialog -----------------------------------
      itemblk1 = mgr.newASPBlock("ITEM1");

      itemblk1.addField("ITEM1_GROUP_ID").
               setSize(20).
               setMaxLength(20).
               setDbName("GROUP_ID").
               setDynamicLOV("DOCUMENT_GROUP").
               setCustomValidation("ITEM1_GROUP_ID", "ITEM1_GROUP_DESCRIPTION");

      itemblk1.addField("ITEM1_GROUP_DESCRIPTION").
               setSize(30).
               setMaxLength(2000).
               setReadOnly().
               setDbName("GROUP_DESCRIPTION");

      itemblk1.setView("DOCUMENT_GROUP");
      itemblk1.setTitle (mgr.translate("Select Group to Copy From"));
      itemset1 = itemblk1.getASPRowSet();
      itembar1 = mgr.newASPCommandBar(itemblk1);
      itembar1.disableMinimize();
      itemtbl1 = mgr.newASPTable(itemblk1);
      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDefaultLayoutMode(itemlay1.CUSTOM_LAYOUT);
      itemlay1.setEditable();
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "DOCMAWDOCUMENTGROUPTITLE: Document Group";
   }

   protected String getTitle()
   {
      return "DOCMAWDOCUMENTGROUPTITLE: Document Group";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      String commandbar_space ="<td widh=10>&nbsp;</td>";
      
       if (showDialog) { 
          String our_buttons = fmt.drawSubmit("CANCEL",mgr.translate("DOCMAWDOCUMENTGROUPCANCEL:   Cancel  "),"");
          our_buttons += fmt.drawSubmit("OK",mgr.translate("DOCMAWDOCUMENTGROUPOK:     OK     "),"");

      appendToHTML("      <table id=\"topTable\" width=\"350\" border=0 cellpadding=0 cellspacing=0>\n");
      appendToHTML("<tr ><td colspan=\"3\"><!-- bar start -->"+itembar1.showBar()+"<!-- bar end --></td></tr>");//first row has only one column which contains command bar
      appendToHTML("<tr >");// second row starts: it has 3 columns >> two sapaces and our lay out.
      appendToHTML(commandbar_space);//space 1(row :2 col:1)
      appendToHTML("<td >");//our lay out is in this column (row :2 col:2 starts)
      appendToHTML("   <!-- ********* our layout starts-->      <table id=\"cntITEM1\" border=0 cellspacing=0 cellpadding=2 class=\"BlockLayoutTable\" width=\"100%\"");
      appendToHTML("            <tr>\n");//row: 1
      appendToHTML("               <td width=10>&nbsp;</td>\n");
      appendToHTML("               <td width=210>");
      appendToHTML(fmt.drawReadLabel(mgr.translate("DOCMAWDOCUMENTGROUPGRPID: Group ID:")));
      appendToHTML("</td>\n");
      appendToHTML("            </tr>\n");

      appendToHTML("            <tr>\n");//row:2
      appendToHTML("               <td>&nbsp;</td>\n");
      appendToHTML("               <td>");
      
      itemset1.addRow(mgr.newASPBuffer());//just to take the tag
      String html_tag = mgr.getASPField("ITEM1_GROUP_ID").getTag();
      appendToHTML(fmt.drawTextField("ITEM1_GROUP_ID",item1_group_id, "onchange=\"validateItem1GroupId(-1)\""+html_tag));
      itemset1.clear();
      appendToHTML("</td>\n");
      appendToHTML("            </tr>\n");

      appendToHTML("            <tr>\n");//row:3
      appendToHTML("               <td>&nbsp;</td>\n");
      appendToHTML("               <td>");
      appendToHTML(fmt.drawReadLabel(mgr.translate("DOCMAWDOCUMENTGROUPGRPDES: Group Description:")));
      appendToHTML("</td>\n");
      appendToHTML("            </tr>\n");

      appendToHTML("            <tr>\n");//row:4
      appendToHTML("               <td>&nbsp;</td>\n");
      appendToHTML("               <td >");
      String item1_group_description_tag="";
      appendToHTML(fmt.drawReadOnlyTextField("ITEM1_GROUP_DESCRIPTION",item1_group_description,item1_group_description_tag));
      appendToHTML("</td>\n");
      appendToHTML("            </tr>\n");

      appendToHTML("            <tr>\n");//row:5
      appendToHTML("               <td colspan=\"2\">&nbsp;</td>\n");
      appendToHTML("            </tr>\n");
      appendToHTML("         </table><!-- *********our layout ends-->\n");
      appendToHTML("</td>");//row :2 col:2 ends)
      appendToHTML(commandbar_space);//space 2 (row :2 col:3) end of second row
      appendToHTML("<tr >");// third row starts: it has 2 columns >> our buttons in one, and a spce in the other.
      appendToHTML("<td colspan=\"2\" align=\"right\">"+our_buttons+"</td>");//(row:3 col:1)
      appendToHTML(commandbar_space);//space in third col (row :3 col:2) end of third row
      appendToHTML("</table>");// end of main table

      /*
      appendToHTML("      <td>\n");
      appendToHTML(itembar1.showBar());*/


      /*appendToHTML("      </td>\n");
      appendToHTML("      </table>\n");*/
       } else { 
      appendToHTML(headlay.show());
       if (headset.countRows()>0 && headlay.isSingleLayout()) 
      appendToHTML(itemlay0.show());
       } 
      appendDirtyJavaScript("//=============================================================================\n");
      appendDirtyJavaScript("//   CLIENT FUNCTIONS\n");
      appendDirtyJavaScript("//=============================================================================\n");
      appendDirtyJavaScript("function validateItem1GroupId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    setDirty();\n");
      appendDirtyJavaScript("    if( !checkItem1GroupId(i) ) return;\n");
      appendDirtyJavaScript("    if( getValue_('ITEM1_GROUP_ID',i)=='' )\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("        getField_('ITEM1_GROUP_DESCRIPTION',i).value = '';\n");
      appendDirtyJavaScript("        return;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    r = __connect(\n");
      appendDirtyJavaScript("        '");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM1_GROUP_ID'\n");
      appendDirtyJavaScript("        + '&ITEM1_GROUP_ID=' + getValue_('ITEM1_GROUP_ID',i)\n");
      appendDirtyJavaScript("        );\n");
      appendDirtyJavaScript("    if( checkStatus_(r,'ITEM1_GROUP_ID',i,'ITEM1_GROUP_ID') )\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("        assignValue_('ITEM1_GROUP_DESCRIPTION',i,0);\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("}\n");
   }

}
